/**
 * README
 * This extension is being used to Get records from EXTSTL table. 
 *
 * Name: EXT001MI.GetPlayerLine
 * Description: Get records from EXTSTL table
 * Date	      Changed By                      Description
 *20230404  SuriyaN@fortude.co     Get records from EXTSTL table
 *
 */

public class GetPlayerLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iPLID, iORNR, iITNO
  private int iCONO, iPONR
  private boolean validInput = true

  public GetPlayerLine(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iPLID = (mi.inData.get("PLID") == null || mi.inData.get("PLID").trim().isEmpty()) ? "" : mi.inData.get("PLID")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer

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
  public void validateInput() {

    //Validate Company Number
    Map<String, String> params = ["CONO": iCONO.toString().trim()]
    Closure<?> callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return 
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

    //Validate Player Number
    params = ["CONO": iCONO.toString().trim(), "DIVI": iDIVI.toString().trim(), "PLID": iPLID.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.PLID == null) {
        mi.error("Invalid PlayerID Number " + iPLID + ". Header Record not Found.")
        validInput = false
        return 
      }
    }
    miCaller.call("EXT001MI", "GetPlayerHead", params, callback)

    //Validate Item Number
    params = ["CONO": iCONO.toString().trim(), "ITNO": iITNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.ITNO == null) {
        mi.error("Invalid Item Number " + iITNO)
        validInput = false
        return 
      }
    }
    miCaller.call("MMS200MI", "Get", params, callback)

    //Validate Temporary Order Number
    DBAction query = database.table("OXHEAD").index("00").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    container.set("OAORNO", iORNR)
    if (!query.read(container)) {
      mi.error("Temporary Order Number not found " + iORNR)
      validInput = false
      return 
    }

  }
  /**
   *Get records from EXTSTL table
   * @params 
   * @return 
   */
  public void getRecord() {
    DBAction query = database.table("EXTSTL").selection("EXPLID", "EXKLOC", "EXORNO", "EXPONR", "EXPOSX", "EXITNO", "EXORQT", "EXSNUM", "EXWHLO", "EXDLIX", "EXRORN", "EXRORL", "EXRORX", "EXUDF1", "EXUDF2", "EXUDF3", "EXUDF4", "EXUDF5", "EXUDF6").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXPLID", iPLID)
    container.set("EXORNR", iORNR)
    container.set("EXITNO", iITNO)
    container.set("EXPONR", iPONR as Integer)
    if (query.read(container)) {
      mi.outData.put("PLID", container.get("EXPLID").toString())
      mi.outData.put("KLOC", container.get("EXKLOC").toString())
      mi.outData.put("ORNR", container.get("EXORNR").toString())
      mi.outData.put("ORNO", container.get("EXORNO").toString())
      mi.outData.put("PONR", container.get("EXPONR").toString())
      mi.outData.put("POSX", container.get("EXPOSX").toString())
      mi.outData.put("ITNO", container.get("EXITNO").toString())
      mi.outData.put("ORQT", container.get("EXORQT").toString())
      mi.outData.put("SNUM", container.get("EXSNUM").toString())
      mi.outData.put("WHLO", container.get("EXWHLO").toString())
      mi.outData.put("DLIX", container.get("EXDLIX").toString())
      mi.outData.put("RORN", container.get("EXRORN").toString())
      mi.outData.put("RORL", container.get("EXRORL").toString())
      mi.outData.put("RORX", container.get("EXRORX").toString())
      mi.outData.put("UDF1", container.get("EXUDF1").toString())
      mi.outData.put("UDF2", container.get("EXUDF2").toString())
      mi.outData.put("UDF3", container.get("EXUDF3").toString())
      mi.outData.put("UDF4", container.get("EXUDF4").toString())
      mi.outData.put("UDF5", container.get("EXUDF5").toString())
      mi.outData.put("UDF6", container.get("EXUDF6").toString())

      mi.write()

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}