/**
 * README
 * This extension is being used to Delete records from EXTNCT table. 
 *
 * Name: EXT856MI.DelASNHead
 * Description: Deleting records from EXTNCT table
 * Date	      Changed By                      Description
 *20230829  SuriyaN@fortude.co    Deleting records from EXTNCT table
 *
 */

public class DelASNHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO
  private String iDIVI, iWHLO, iPYCU
  private boolean validInput = true

  public DelASNHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iPYCU = (mi.inData.get("PYCU") == null || mi.inData.get("PYCU").trim().isEmpty()) ? "" : mi.inData.get("PYCU")

    validateInput()
    if (validInput) {
      deleteRecord()
    }

  }

  /**
   *Validate inputs
   * @params int CONO ,String ITNO,String WHLO,String iWHSL
   * @return void
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
        return
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Customer Number
    params = ["CUNO": iPYCU.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.CUNO == null) {
        mi.error("Invalid Customer Numebr Number " + iPYCU)
        validInput = false
        return
      }
    }
    miCaller.call("CRS610MI", "GetBasicData", params, callback)

    //Validate Warehouse Number
    params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.WHLO == null) {
        mi.error("Invalid Warehouse Number " + iWHLO)
        validInput = false
        return
      }
    }
    miCaller.call("MMS005MI", "GetWarehouse", params, callback)
  }

  /**
   *Delete records from EXTNCT table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTNCT").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXPYCU", iPYCU)
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