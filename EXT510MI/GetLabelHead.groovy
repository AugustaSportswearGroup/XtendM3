/**
 * README
 * This extension is being used to Get records from EXTSMF table. 
 *
 * Name: EXT510MI.GetLabelHead
 * Description: Get records from EXTSMF table
 * Date	      Changed By                      Description
 *20230817  SuriyaN@fortude.co     Get records from EXTSMF table
 *
 */

public class GetLabelHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iRIDN
  private int iCONO
  private long iDLIX
  boolean validInput = true

  public GetLabelHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iRIDN = (mi.inData.get("RIDN") == null || mi.inData.get("RIDN").trim().isEmpty()) ? "" : mi.inData.get("RIDN")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long

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
    def params = ["CONO": iCONO.toString().trim()]
    def callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    if (!iRIDN.trim().isEmpty()) {
      //Validate Order Number
      params = ["CONO": iCONO.toString().trim(), "ORNO": iRIDN.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ORNO == null) {
          validInput = false
        }
      }
      miCaller.call("OIS100MI", "GetHead", params, callback)

      //If not CO, check if valid DO
      if (!validInput) {
        validInput = true
        params = ["CONO": iCONO.toString().trim(), "TRNR": iRIDN.toString().trim()]
        callback = {
          Map < String,
          String > response ->
          if (response.TRNR == null) {
            mi.error("Invalid Order Number " + iRIDN)
            validInput = false
            return false
          }
        }
        miCaller.call("MMS100MI", "GetHead", params, callback)
      }
    }

      //Validate Delivery Number
      params = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.DLIX == null) {
          mi.error("Invalid Delivery Number " + iDLIX)
          validInput = false
        }
      }
      miCaller.call("MWS410MI", "GetHead", params, callback)
    

    if (!iWHLO.trim().isEmpty()) {
      //Validate Warehouse Number
      params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.WHLO == null) {
          mi.error("Invalid Warehouse Number " + iWHLO)
          validInput = false
          return false
        }
      }
      miCaller.call("MMS005MI", "GetWarehouse", params, callback)
    }

  }

  /**
   *Get records from EXTSMF table
   * @params 
   * @return 
   */
  public getRecord() {
    DBAction query = database.table("EXTSMF").selection("EXDIVI", "EXRIDN", "EXFACI", "EXWHLO", "EXPYCU", "EXSPOC", "EXIVNO", "EXITNO", "EXSPID", "EXCSID", "EXGTIN", "EXBXNO", "EXQUAN", "EXBANO", "EXUDF1", "EXUDF2", "EXUDF3", "EXUDF4", "EXCONO", "EXDLIX").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDLIX", iDLIX as Long)
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXRIDN", iRIDN)
    if (query.read(container)) {
      mi.outData.put("DIVI", container.get("EXDIVI").toString())
      mi.outData.put("RIDN", container.get("EXRIDN").toString())
      mi.outData.put("RIDL", container.get("EXRIDL").toString())
      mi.outData.put("FACI", container.get("EXFACI").toString())
      mi.outData.put("WHLO", container.get("EXWHLO").toString())
      mi.outData.put("PYCU", container.get("EXPYCU").toString())
      mi.outData.put("SPOC", container.get("EXSPOC").toString())
      mi.outData.put("IVNO", container.get("EXIVNO").toString())
      mi.outData.put("ITNO", container.get("EXITNO").toString())
      mi.outData.put("SPID", container.get("EXSPID").toString())
      mi.outData.put("CSID", container.get("EXCSID").toString())
      mi.outData.put("GTIN", container.get("EXGTIN").toString())
      mi.outData.put("BXNO", container.get("EXBXNO").toString())
      mi.outData.put("QUAN", container.get("EXQUAN").toString())
      mi.outData.put("BANO", container.get("EXBANO").toString())
      mi.outData.put("UDF1", container.get("EXUDF1").toString())
      mi.outData.put("UDF2", container.get("EXUDF2").toString())
      mi.outData.put("UDF3", container.get("EXUDF3").toString())
      mi.outData.put("UDF4", container.get("EXUDF4").toString())
      mi.outData.put("CONO", container.get("EXCONO").toString())
      mi.outData.put("DLIX", container.get("EXDLIX").toString())

      mi.write()

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}