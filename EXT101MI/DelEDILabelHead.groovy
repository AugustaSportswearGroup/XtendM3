/**
 * README
 * This extension is being used to Delete records from EXTGLB table. 
 *
 * Name: EXT101MI.DelEDILabelHead
 * Description: Deleting records from EXTGLB table
 * Date	      Changed By                      Description
 *20230825  SuriyaN@fortude.co    Deleting records from EXTGLB table
 *
 */

public class DelEDILabelHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO
  private String iDIVI, iORNR, iORNO
  private boolean validInput = true

  public DelEDILabelHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
    this.mi = mi
    this.database = database
    this.program = program
    this.miCaller = miCaller
  }
  /**
   ** Main function
   * @param
   * @return
   */
  public void main() {
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")

    validateInput()
    if (validInput) {
      deleteRecord()
    }

  }

  /**
   *Validate Records
   * @params
   * @return 
   */
  public validateInput() {
    //Validate Company Number
    Map < String, String > params = ["CONO": iCONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Division
    params = ["CONO": iCONO.toString().trim(), "DIVI": iDIVI.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.DIVI == null) {
        mi.error("Invalid Division " + iDIVI)
        validInput = false
        return
      }
    }

    miCaller.call("MNS100MI", "GetBasicData", params, callback)

    //Validate Temporary Order Number
    DBAction query = database.table("OXHEAD").index("00").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    container.set("OAORNO", iORNR)

    if (!query.read(container)) {
      mi.error("Temporary Order Number not found " + iORNR)
      validInput = false
      return false
    }

    //Validate Order Number
    params = ["ORNO": iORNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.ORNO == null) {
        mi.error("Invalid Order Number " + iORNO)
        validInput = false
        return false
      }
    }
    miCaller.call("OIS100MI", "GetOrderHead", params, callback)

  }

  /**
   *Delete records from EXTGLB table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTGLB").index("00").build()
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXORNR", iORNR)
    container.set("EXORNO", iORNO)
    container.set("EXCONO", iCONO)

    if (!query.readLock(container, deleteCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > deleteCallBack = {
    LockedResult lockedResult ->
    lockedResult.delete()
  }
}