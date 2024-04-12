/**
 * README
 * This extension is being used to list customer order lines
 *
 * Name: EXT104MI.LstHeadCharges
 * Description: Transaction used to get head charges for ecom orders
 * Date	      Changed By                      Description
 *20231019  SuriyaN@fortude.co     Get head charges for ecom orders */

public class LstHeadCharges extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private int iCONO, maxPageSize = 10000
  private String iORNO
  private Map < String, HashMap > orderList = new TreeMap < String, HashMap > ()
  private boolean validInput = true

  public LstHeadCharges(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
  }

  /**
   * Main method to list head charges for e-commerce orders.
   */

  public void main() {
    iCONO = program.LDAZD.CONO as Integer
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO").trim()
    if (!validateInput()) {
      if (!validateBasedOnOREF()) {
        mi.error("Invalid Order Number " + iORNO)
      }
    }

    for (Map.Entry < String, HashMap > entry: orderList.entrySet()) {
      HashMap < String, String > order = (HashMap) entry.getValue()
      String orno = order.get("ORNO")
      String oref = order.get("OREF")
      listHeadCharges(orno, oref)
    }
  }

  /**
   * Validates the input parameters.
   *
   * @return True if the input is valid, false otherwise.
   */

  public boolean validateInput() {
    if (!iORNO.trim().isEmpty()) {
      //Validate Order Number
      Map < String, String > params = ["ORNO": iORNO.toString().trim()]
      Closure < ? > callback = {
        Map < String,
        String > response ->
        if (response.ORNO == null) {
          validInput = false
          return false
        } else {
          HashMap < String, String > order = new HashMap < String, String > ()
          order.put("ORNO", iORNO)
          order.put("ORTP", response.ORTP)
          order.put("CUCD", response.CUCD)
          order.put("OREF", response.OREF)
          orderList.put(iORNO, order)
        }
      }
      miCaller.call("OIS100MI", "GetOrderHead", params, callback)
    } else {
      mi.error("Order number must be entered.")
      validInput = false
    }
    return validInput
  }

  /**
   * Validates the input based on order reference.
   *
   * @return True if the input is valid, false otherwise.
   */

  public boolean validateBasedOnOREF() {
    ExpressionFactory expression = database.getExpressionFactory("OOHEAD")
    // set Ecom order number filter
    String oref = "EC" + iORNO
    expression = expression.eq("OAOREF", oref)
    DBAction query = database.table("OOHEAD").index("00").matching(expression).selection("OAORNO", "OAORTP", "OACUCD", "OAOREF").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    query.readAll(container, 1, maxPageSize, rsReadOrders)
    return validInput
  }

  Closure < ? > rsReadOrders = {
    DBContainer container ->
    String orno = container.get("OAORNO").toString()
    if (orno != null) {
      validInput = true
      HashMap < String, String > order = new HashMap < String, String > ()
      order.put("ORNO", orno)
      order.put("ORTP", container.get("OAORTP").toString())
      order.put("CUCD", container.get("OACUCD").toString())
      order.put("OREF", container.get("OAOREF").toString())
      orderList.put(orno, order)
    }
  }

  /**
   * Lists head charges for the specified order.
   *
   * @param orno The order number.
   * @param oref The order reference.
   */

  public void listHeadCharges(String orno, String oref) {
    Map < String, String > params = ["CONO": iCONO.toString().trim(), "ORNO": iORNO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.ORNO != null) {
        long dlix = response.DLIX == null ? 0L : response.DLIX as Long
        double cram = response.CRAM == null ? 0D : response.CRAM as Double
        int crty = response.CRTY == null ? 0 : response.CRTY as Integer
        if (dlix == 0 && crty == 0 && cram != 0) {
          if (oref.startsWith("EC")) {
            mi.outData.put("OREF", oref)
          }
          mi.outData.put("ORNO", response.ORNO)
          mi.outData.put("CRID", response.CRID)
          mi.outData.put("CRD0", response.CRD0)
          mi.outData.put("CRAM", response.CRAM)
          mi.write()
        }
      }
    }
    miCaller.call("OIS100MI", "LstConnCOCharge", params, callback)
  }
}