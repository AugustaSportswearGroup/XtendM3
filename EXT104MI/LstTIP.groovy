/**
 * README
 * This extension is being used to list TIP
 *
 * Name: EXT104MI.LstTIP
 * Description: Transaction used to list TIP for ecom orders
 * Date	      Changed By                      Description
 *20231019  SuriyaN@fortude.co     List TIP for ecom orders */

public class LstTIP extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private int iCONO, translationId, maxPageSize = 10000
  private String iPYNO
  private Map < String, HashMap > orderList = new TreeMap < String, HashMap > ()
  private boolean validInput = true

  public LstTIP(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
  }

  /**
   * Executes the main logic of the transaction.
   */

  public void main() {
    iCONO = program.LDAZD.CONO as Integer
    iPYNO = (mi.inData.get("PYNO") == null || mi.inData.get("PYNO").trim().isEmpty()) ? "" : mi.inData.get("PYNO") as String
    if (validateInput()) {
      String prefix = getTranslation("TIPPREFIX")
      String[] prefixes = prefix.split(",")
      for (String prep: prefixes) {
        lstTipCustomers(prep)
      }
    }
  }

  /**
   * Validates the input data.
   * @return boolean indicating whether the input is valid or not.
   */

  public boolean validateInput() {
    //Validate Payer is entered
    if (iPYNO.isEmpty()) {
      mi.error("Payer number must be entered.")
      validInput = false
    }
    return validInput
  }

  /**
   * Lists tip customers based on the given prefix.
   * @param prefix The prefix to filter customers.
   */

  public void lstTipCustomers(String prefix) {
    ExpressionFactory expression = database.getExpressionFactory("OCUSMA")
    // set prefix filter
    expression = expression.like("OKCUNO", prefix.trim() + "%")
    DBAction query = database.table("OCUSMA").index("87").matching(expression).selection("OKCUNO", "OKCUNM", "OKSTAT", "OKCUTP").build()
    DBContainer container = query.getContainer()
    container.set("OKCONO", iCONO)
    container.set("OKPYNO", iPYNO)
    query.readAll(container, 2, maxPageSize, rsReadCustomers)
  }

  Closure < ? > rsReadCustomers = {
    DBContainer container ->
    String cuno = container.get("OKCUNO").toString()
    if (cuno != null) {
      int stat = container.get("OKSTAT").toString() as Integer
      int cutp = container.get("OKCUTP").toString() as Integer
      if (stat == 20 && (cutp == 1 || cutp == 0)) {
        mi.outData.put("TPNO", container.get("OKCUNO").toString())
        mi.outData.put("CUNM", container.get("OKCUNM").toString())
        mi.write()
      }
    }
  }

  /**
   * getTranslation - Get the CRS881 data translation using a String keyword
   * @params String keyword
   * @return String
   */
  private String getTranslation(String keyword) {
    if (translationId == 0) {
      Map < String, String > params = ["CONO": iCONO.toString().trim(), "DIVI": "", "TRQF": "0", "MSTD": "ECOM", "MVRS": "1", "BMSG": "ECZ001MI", "IBOB": "O", "ELMP": "API", "ELMD": "Properties"]
      Closure < ? > callback = {
        Map < String,
        String > response ->
        if (response.IDTR != null) {
          translationId = response.IDTR as Integer
        }
      }
      miCaller.call("CRS881MI", "GetTranslation", params, callback)
    }

    String mbmd = ""
    if (translationId != 0) {
      Closure < ? > getTranslatedData = {
        DBContainer container ->
        mbmd = container.get("TDMBMD")
        if (mbmd == null) {
          mbmd = ""
        }
      }
      DBAction query = database.table("MBMTRD").index("30").selection("TDMBMD").build()
      DBContainer container = query.getContainer()
      container.set("TDCONO", iCONO)
      container.set("TDDIVI", "")
      container.set("TDIDTR", translationId as Integer)
      container.set("TDMVXD", keyword)
      query.readAll(container, 4, maxPageSize, getTranslatedData)
    }
    return mbmd.trim()
  }
}