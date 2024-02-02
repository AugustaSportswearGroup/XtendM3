/**
 * README
 * Get a record from the table EXTOOH
 *
 * Name: EXT012MI.GetHeaderInfo
 * Description: Get a record from the EXTOOH table
 * Date	      Changed By            Description
 * 20230815	  NATARAJKB        Get a record from the EXTOOH table
 *
 */

public class LstHeaderInfo extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final LoggerAPI logger
  private final ProgramAPI program
  private final UtilityAPI utility
  private final MICallerAPI miCaller

  //Input fields
  private String iORNO
  private int iCONO
  private boolean validInput = true

  public LstHeaderInfo(MIAPI mi, DatabaseAPI database, LoggerAPI logger, ProgramAPI program, UtilityAPI utility, MICallerAPI miCaller) {
    this.mi = mi
    this.database = database
    this.logger = logger
    this.program = program
    this.utility = utility
    this.miCaller = miCaller
  }

  /**
   * Main method
   * @param
   * @return
   */
  public void main() {

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
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

    //Validate Order Number
    if (!iORNO.toString().trim().isEmpty()) {
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
  }

  /**
   * Gets a record from the EXTOOH table
   *
   */
  private void getRecord() {
    ExpressionFactory expression = database.getExpressionFactory("EXTOOH")
    expression = expression.eq("EXCONO", iCONO.toString())

    if (iORNO.trim() != "") {
      expression = expression.and(expression.eq("EXORNO", iORNO.toString()))
    }

    DBAction action = database.table("EXTOOH").index("00").matching(expression).selectAllFields().build()
    DBContainer container = action.getContainer()
    container.set("EXCONO", iCONO as Integer)

    // Fetch the information form the table and output it
    Closure < ? > readAllRecords = {
      DBContainer resultContainer ->
      mi.outData.put("CONO", resultContainer.get("EXCONO").toString())
      mi.outData.put("ORNO", resultContainer.get("EXORNO").toString())
      mi.outData.put("CEDI", resultContainer.get("EXCEDI").toString())
      mi.outData.put("TPID", resultContainer.get("EXTPID").toString())
      mi.outData.put("PYNO", resultContainer.get("EXPYNO").toString())
      mi.outData.put("DEPT", resultContainer.get("EXDEPT").toString())
      mi.outData.put("VEND", resultContainer.get("EXVEND").toString())
      mi.outData.put("FOBC", resultContainer.get("EXFOBC").toString())
      mi.outData.put("ADST", resultContainer.get("EXADST").toString())
      mi.outData.put("ADBT", resultContainer.get("EXADBT").toString())
      mi.outData.put("ADDS", resultContainer.get("EXADDS").toString())
      mi.outData.put("ADBY", resultContainer.get("EXADBY").toString())
      mi.outData.put("UNID", resultContainer.get("EXUNID").toString())
      mi.outData.put("CANR", resultContainer.get("EXCANR").toString())
      mi.outData.put("SDIN", resultContainer.get("EXSDIN").toString())
      mi.outData.put("CDRP", resultContainer.get("EXCDRP").toString())
      mi.outData.put("REZA", resultContainer.get("EXREZA").toString())
      mi.outData.put("RELO", resultContainer.get("EXRELO").toString())
      mi.outData.put("CUOR", resultContainer.get("EXCUOR").toString())
      mi.outData.put("SDNR", resultContainer.get("EXSDNR").toString())
      mi.outData.put("ROUT", resultContainer.get("EXROUT").toString())
      mi.outData.put("RLSN", resultContainer.get("EXRLSN").toString())
      mi.outData.put("CUNF", resultContainer.get("EXCUNF").toString())
      mi.outData.put("STOT", resultContainer.get("EXSTOT").toString())
      mi.outData.put("STLN", resultContainer.get("EXSTLN").toString())
      mi.outData.put("STQT", resultContainer.get("EXSTQT").toString())
      mi.outData.put("STWT", resultContainer.get("EXSTWT").toString())
      mi.outData.put("STUM", resultContainer.get("EXSTUM").toString())
      mi.outData.put("STVL", resultContainer.get("EXSTVL").toString())
      mi.outData.put("STVU", resultContainer.get("EXSTVU").toString())

      mi.outData.put("RGDT", resultContainer.getInt("EXRGDT").toString())
      mi.outData.put("RGTM", resultContainer.getInt("EXRGTM").toString())
      mi.outData.put("LMDT", resultContainer.getInt("EXLMDT").toString())
      mi.outData.put("CHNO", resultContainer.getInt("EXCHNO").toString())
      mi.outData.put("CHID", resultContainer.get("EXCHID").toString())
      mi.outData.put("LMTS", resultContainer.get("EXLMTS").toString())
      mi.write()
    }
    if (!action.readAll(container, 1, 10000, readAllRecords)) {
      mi.error("Record does not exist")
      return
    }

  }
}