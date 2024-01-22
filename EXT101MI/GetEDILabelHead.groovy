/**
 * README
 * This extension is being used to Get records from EXTGLB table. 
 *
 * Name: EXT101MI.GetEDILabelHead
 * Description: Get records from EXTGLB table
 * Date	      Changed By                      Description
 *20230825  SuriyaN@fortude.co     Get records from EXTGLB table
 *
 */

public class GetEDILabelHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iORNR, iORNO
  private int iCONO
  private boolean validInput = true

  public GetEDILabelHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    validateInput()
    if (validInput) {
      getRecord()
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
   *Get records from EXTGLB table
   * @params 
   * @return 
   */
  public getRecord() {
    DBAction query = database.table("EXTGLB").selection("EXDIVI", "EXORNR", "EXORNO", "EXCUOR", "EXCUNO", "EXDEPT", "EXDEPN", "EXSHPT", "EXUD01", "EXUD02", "EXUD03", "EXUD04", "EXUD05", "EXUD06", "EXUD07", "EXUD08", "EXUD09", "EXUD10", "EXCONO").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXORNR", iORNR)
    container.set("EXORNO", iORNO)
    if (query.read(container)) {
      mi.outData.put("DIVI", container.get("EXDIVI").toString())
      mi.outData.put("ORNR", container.get("EXORNR").toString())
      mi.outData.put("ORNO", container.get("EXORNO").toString())
      mi.outData.put("CUOR", container.get("EXCUOR").toString())
      mi.outData.put("CUNO", container.get("EXCUNO").toString())
      mi.outData.put("DEPT", container.get("EXDEPT").toString())
      mi.outData.put("DEPN", container.get("EXDEPN").toString())
      mi.outData.put("SHPT", container.get("EXSHPT").toString())
      mi.outData.put("UD01", container.get("EXUD01").toString())
      mi.outData.put("UD02", container.get("EXUD02").toString())
      mi.outData.put("UD03", container.get("EXUD03").toString())
      mi.outData.put("UD04", container.get("EXUD04").toString())
      mi.outData.put("UD05", container.get("EXUD05").toString())
      mi.outData.put("UD06", container.get("EXUD06").toString())
      mi.outData.put("UD07", container.get("EXUD07").toString())
      mi.outData.put("UD08", container.get("EXUD08").toString())
      mi.outData.put("UD09", container.get("EXUD09").toString())
      mi.outData.put("UD10", container.get("EXUD10").toString())
      mi.outData.put("CONO", container.get("EXCONO").toString())

      mi.write()

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}