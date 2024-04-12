/**
 * README
 * This extension is being used to list customer orders for ecommerce B2B site
 *
 * Name: EXT104MI.LstOrders
 * Description: Transaction used to list customer orders for ecommerce B2B site
 * Date	      Changed By                      Description
 *20231023  SuriyaN@fortude.co     List customer orders for ecommerce B2B site*/
 


public class LstOrders extends ExtendM3Transaction {
  private final MIAPI mi
  private final LoggerAPI logger
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private int iCONO, rowCount, maxPageSize = 10000
  private int pageSize, pageRecordCount = 0, translationId = 0, pageRecordMax = 0
  private String iDIVI, iCUNO, iORNO, iPONO, iITNO, iFRDT, iTODT, iPNUM, iPSZE, iLORN, iDRTN, iLODT, iOREF, iORTP, iIVNO, iSTAT
  private boolean validInput = true, hasOrderFilter = false
  private String ordersFilter, zStatus
  private Map < String, HashMap > pageOrders = new TreeMap < String, HashMap > (Collections.reverseOrder())
  private long timestamp = 0L
  private double frt, chrg, taxCharge

  public LstOrders(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller, LoggerAPI logger) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
    this.logger = logger
  }

  /**
   * Main method to process orders.
   */

  public void main() {
    iCONO = program.LDAZD.CONO as Integer
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI as String : mi.inData.get("DIVI") as String
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO").trim()
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO").trim()
    iPONO = (mi.inData.get("PONO") == null || mi.inData.get("PONO").trim().isEmpty()) ? "" : mi.inData.get("PONO").trim()
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO").trim()
    iFRDT = (mi.inData.get("FRDT") == null || mi.inData.get("FRDT").trim().isEmpty()) ? "" : mi.inData.get("FRDT").trim()
    iTODT = (mi.inData.get("TODT") == null || mi.inData.get("TODT").trim().isEmpty()) ? "" : mi.inData.get("TODT").trim()
    iPNUM = (mi.inData.get("PNUM") == null || mi.inData.get("PNUM").trim().isEmpty()) ? "1" : mi.inData.get("PNUM").trim()
    iPSZE = (mi.inData.get("PSZE") == null || mi.inData.get("PSZE").trim().isEmpty()) ? "25" : mi.inData.get("PSZE").trim()
    iLORN = (mi.inData.get("LORN") == null || mi.inData.get("LORN").trim().isEmpty()) ? "" : mi.inData.get("LORN").trim()
    iDRTN = (mi.inData.get("DRTN") == null || mi.inData.get("DRTN").trim().isEmpty()) ? "" : mi.inData.get("DRTN").trim()
    iLODT = (mi.inData.get("LODT") == null || mi.inData.get("LODT").trim().isEmpty()) ? "" : mi.inData.get("LODT").trim().trim()
    iOREF = (mi.inData.get("OREF") == null || mi.inData.get("OREF").trim().isEmpty()) ? "" : mi.inData.get("OREF").trim()
    iORTP = (mi.inData.get("ORTP") == null || mi.inData.get("ORTP").trim().isEmpty()) ? "" : mi.inData.get("ORTP").trim()
    iIVNO = (mi.inData.get("IVNO") == null || mi.inData.get("IVNO").trim().isEmpty()) ? "" : mi.inData.get("IVNO").trim()
    iSTAT = (mi.inData.get("STAT") == null || mi.inData.get("STAT").trim().isEmpty()) ? "" : mi.inData.get("STAT").trim()

    pageSize = iPSZE as Integer
    readOrders()
    int pageNum = iPNUM as Integer
    int recordCount = 0
    int pageTo = pageNum * pageSize
    int pageFrom = (pageTo - pageSize) + 1
    for (Map.Entry < String, HashMap > entry: pageOrders.entrySet()) {
      recordCount = recordCount + 1
      if (recordCount >= pageFrom && recordCount <= pageTo) {
        HashMap < String, String > order = (HashMap) entry.getValue()
        mi.outData.put("DIVI", order.get("DIVI").toString())
        String orno = order.get("ORNO").toString()
        mi.outData.put("ORNO", orno)
        mi.outData.put("ORDT", order.get("RGDT").toString())
        String ortp = getTranslation("ECZOT" + order.get("ORTP").toString())
        if (ortp.isEmpty()) {
          ortp = getTranslation("ECZOTDEFAU")
        }
        mi.outData.put("ORTP", ortp as String)
        String subl = getTranslation("SUBL" + order.get("ORTP").toString())
        if (subl.equals("Y")) {
          mi.outData.put("SUBL", "1")
        } else {
          mi.outData.put("SUBL", "0")
        }
        mi.outData.put("CUCD", order.get("CUCD").toString())
        mi.outData.put("OREF", order.get("OREF").toString())
        mi.outData.put("CUOR", order.get("CUOR").toString())
        mi.outData.put("ORSL", order.get("ORSL").toString())
        mi.outData.put("ORSH", order.get("ORST").toString())
        zStatus = getOrderStatus(order.get("ORSL") as Integer, order.get("ORST") as Integer)
        mi.outData.put("ORST", zStatus)
        int oblc = order.get("OBLC") as Integer
        if (oblc > 0 && oblc < 9) {
          if (oblc == 7) {
            mi.outData.put("OBLC", order.get("OBLC").toString() + "." + order.get("DTID").toString())
          } else {
            mi.outData.put("OBLC", order.get("OBLC").toString())
          }
        }
        mi.outData.put("OTIP", getTipNumber(orno))
        String cuno = order.get("CUNO").toString()
        HashMap < String, String > delivery = readDeliveries(cuno, orno, order.get("TEPY").toString(), zStatus)
        mi.outData.put("CARR", delivery.get("CARR").toString())
        mi.outData.put("MODL", delivery.get("MODL").toString())
        mi.outData.put("ETRN", delivery.get("ETRN").toString())
        mi.outData.put("DSDT", delivery.get("DSDT").toString())
        mi.outData.put("NTLA", order.get("NTLA").toString())
        double ntla = order.get("NTLA") as Double
        rtvShippingCharges(orno)
        mi.outData.put("STOT", frt as String)
        double discount = rtvDiscount(order.ORNO)
        mi.outData.put("DTOT", discount as String)
        double xavtam = rtvVatAmount(order.ORNO)
        ntla = ntla + frt + chrg + discount + taxCharge + xavtam
        mi.outData.put("NTLA", ntla as String)
        mi.write()
      }
      if (recordCount >= pageTo) {
        break
      }
    }
  }

  /**
   * Validates input data.
   */

  public validateInput() {
    if (!iCUNO.trim().isEmpty()) {
      //Validate Customer Number
      Map < String, String > params = ["CONO": iCONO.toString().trim(), "CUNO": iCUNO.toString().trim()]
      Closure < ? > callback = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + iCUNO)
          validInput = false
          return false
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }
  }

  /**
   * Reads orders with filter based on customer numbers.
   * @param customers An array of customer numbers
   * @return True if there is a filter on orders, otherwise false
   */

  public boolean readOrdersWithFilter(String[] customers) {
    hasOrderFilter = false
    ordersFilter = ""
    if (!iORNO.isEmpty() || !iPONO.isEmpty() || !iOREF.isEmpty() || !iIVNO.isEmpty()) {
      hasOrderFilter = true
      if (!iORNO.isEmpty() && iORNO.length() == 10) {
        ordersFilter = rtvOrdersByORNO(customers, iORNO)
      }
      if (!iPONO.isEmpty() && ordersFilter.isEmpty()) {
        rtvOrdersByPONO(customers)
      }
      if (!iOREF.isEmpty() && ordersFilter.isEmpty() && iOREF.length() == 14) {
        rtvOrdersByOREF(customers)
      }
      if (!iIVNO.isEmpty() && ordersFilter.isEmpty() && iIVNO.length() <= 9) {
        rtvOrdersByIVNO(customers)
      }
    }

    return hasOrderFilter
  }

  /**
   * Retrieves orders by order number and customer numbers.
   * @param customers An array of customer numbers
   * @param orderNumber The order number
   * @return The orders found
   */
  public String rtvOrdersByORNO(String[] customers, String orderNumber) {
    String orders = ""
    ExpressionFactory expression = getGenericFilter(customers)

    DBAction query = database.table("OOHEAD").index("00").matching(expression).selection("OACONO", "OADIVI", "OACUNO", "OAORNO", "OAORDT", "OAORTP", "OAOREF", "OARGDT", "OARGTM", "OALMTS", "OACUOR", "OAORSL", "OAORST", "OAOBLC", "OADTID", "OACUCD", "OATEPY", "OANTLA").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    container.set("OAORNO", orderNumber)
    if (query.read(container)) {
      String orno = container.get("OAORNO").toString()
      if (orders.isEmpty()) {
        orders = orno
      }
      pageRecordCount = pageRecordCount + 1
      String hour = container.get("OARGTM")
      String padded = "000000" + hour
      hour = padded.substring(padded.length() - 6)
      String rgdt = container.get("OARGDT")
      String key = rgdt + hour + orno
      HashMap < String, String > order = new HashMap < String, String > ()
      order.put("ORNO", orno)
      order.put("RGDT", rgdt)
      order.put("RGTM", container.get("OARGTM") as String)
      order.put("ORTP", container.get("OAORTP") as String)
      order.put("OREF", container.get("OAOREF") as String)
      order.put("DIVI", container.get("OADIVI") as String)
      order.put("LMTS", container.get("OALMTS") as String)
      order.put("CUOR", container.get("OACUOR") as String)
      order.put("ORST", container.get("OAORST") as String)
      order.put("ORSL", container.get("OAORSL") as String)
      order.put("OBLC", container.get("OAOBLC") as String)
      order.put("DTID", container.get("OADTID") as String)
      order.put("CUCD", container.get("OACUCD") as String)
      order.put("CUNO", container.get("OACUNO") as String)
      order.put("TEPY", container.get("OATEPY") as String)
      order.put("NTLA", container.get("OANTLA") as String)
      pageOrders.put(key, order)
    }
    return orders
  }

  /**
   * Retrieves orders by purchase order number and customer numbers.
   * @param customers An array of customer numbers
   */

  public void rtvOrdersByPONO(String[] customers) {
    String orders = ""
    ExpressionFactory expression = getGenericFilter(customers)
    // set Customer's PO number filter
    int length = iPONO.length() > 18 ? 18 : iPONO.length()
    String pono = "%" + iPONO.toUpperCase().substring(0, length).trim() + "%"
    expression = expression.and(expression.like("OACUOR", pono))

    DBAction query = database.table("OOHEAD").index("20").reverse().matching(expression).selection("OACONO", "OADIVI", "OACUNO", "OAORNO", "OAORDT", "OAORTP", "OAOREF", "OARGDT", "OARGTM", "OALMTS", "OACUOR", "OAORSL", "OAORST", "OAOBLC", "OADTID", "OACUCD", "OATEPY", "OANTLA").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    query.readAll(container, 1, maxPageSize, rsReadOrders)
  }

  /**
   * Retrieves orders by reference number and customer numbers.
   * @param customers An array of customer numbers
   */
  public void rtvOrdersByOREF(String[] customers) {
    ExpressionFactory expression = getGenericFilter(customers)
    // set Ecom order number filter
    String oref = "EC" + iOREF
    expression = expression.and(expression.eq("OAOREF", oref))

    DBAction query = database.table("OOHEAD").index("20").matching(expression).selection("OACONO", "OADIVI", "OACUNO", "OAORNO", "OAORDT", "OAORTP", "OAOREF", "OARGDT", "OARGTM", "OALMTS", "OACUOR", "OAORSL", "OAORST", "OAOBLC", "OADTID", "OACUCD", "OATEPY", "OANTLA").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)

    query.readAll(container, 1, maxPageSize, rsReadOrders)
  }

  /**
   * Retrieves orders by invoice number and customer numbers.
   * @param customers An array of customer numbers
   */

  public void rtvOrdersByIVNO(String[] customers) {
    Closure < ? > rsReadDelOrders = {
      DBContainer container ->
      String orno = container.get("UAORNO").toString()
      if (orno != null) {
        rtvOrdersByORNO(customers, orno)
      }
    }

    ExpressionFactory expression = database.getExpressionFactory("ODHEAD")
    // set Invoice number filter
    expression = expression.in("UACUNO", customers).and(expression.eq("UAIVNO", iIVNO))

    DBAction query = database.table("ODHEAD").index("00").matching(expression).selection("UAORNO").build()
    DBContainer container = query.getContainer()
    container.set("UACONO", iCONO)
    query.readAll(container, 1, maxPageSize, rsReadDelOrders)
  }

  /**
   * Retrieves the tip number for a given order number.
   * @param orderNumber The order number
   * @return The tip number associated with the order
   */

  public String getTipNumber(String orderNumber) {
    String tipNumber = ""
    DBAction query = database.table("OOHEAC").index("00").selection("BEORNO", "BEAGN5").build()
    DBContainer container = query.getContainer()
    container.set("BECONO", iCONO)
    container.set("BEORNO", orderNumber)
    if (query.read(container)) {
      tipNumber = container.get("BEAGN5").toString()

    }
    return tipNumber
  }

  /**
   * Retrieves linked customers for the current customer.
   * @return A comma-separated string of linked customer numbers
   */

  public String getLinkedCustomers() {
    String customers = iCUNO
    Closure < ? > rsReadCustomers = {
      DBContainer container ->
      String cuno = container.get("OKCUNO").toString().trim()
      if (cuno != null) {
        if (!cuno.equals(iCUNO)) {
          customers = customers + "," + cuno
        }
      }
    }
    ExpressionFactory expression = database.getExpressionFactory("OCUSMA")
    expression = expression.eq("OKCUST", iCUNO)
    DBAction query = database.table("OCUSMA").index("00").matching(expression).selection("OKCUNO", "OKCUST").build()
    DBContainer container = query.getContainer()
    container.set("OKCONO", iCONO)
    container.set("OKCUST", iCUNO)

    query.readAll(container, 1, maxPageSize, rsReadCustomers)
    return customers
  }

  /**
   *Get records from OOHEAD table
   * @params 
   * @return 
   */
  public void readOrders() {
    boolean drtn = false
    //Limit returned records based on page number
    int pageNum = iPNUM as Integer
    int pageRecordMax = (pageNum + 1) * pageSize

    // Retrieve linked customers based on OKCUST
    String[] customers = getLinkedCustomers().split(",")

    //If duration is set then clear other filters and should use timestamp condition only
    if (!iDRTN.isEmpty()) {
      int duration = iDRTN as Integer
      iFRDT = ""
      iTODT = ""
      iPONO = ""
      iORNO = ""
      iLORN = ""
      iOREF = ""
      iIVNO = ""
      iITNO = ""
      drtn = true
      Calendar cal = Calendar.getInstance()
      cal.add(Calendar.MINUTE, -duration)
      timestamp = cal.getTimeInMillis()
    }

    if (readOrdersWithFilter(customers)) {
      return
    }

    ExpressionFactory expression = getGenericFilter(customers)
    // set Timestamp filter
    if (!iDRTN.isEmpty()) {
      expression = expression.and(expression.ge("OALMTS", timestamp as String))
    }

    DBAction query = database.table("OOHEAD").index("20").reverse().matching(expression).selection("OACONO", "OADIVI", "OACUNO", "OAORNO", "OAORDT", "OAORTP", "OAOREF", "OARGDT", "OARGTM", "OALMTS", "OACUOR", "OAORSL", "OAORST", "OAOBLC", "OADTID", "OACUCD", "OATEPY", "OANTLA").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    //container.set("OACUNO", iCUNO)

    query.readAll(container, 1, pageRecordMax, rsReadOrders)
  }

  Closure < ? > rsReadOrders = {
    DBContainer container ->
    String orno = container.get("OAORNO").toString()
    if (orno != null) {
      pageRecordCount = pageRecordCount + 1
      String hour = container.get("OARGTM")
      String padded = "000000" + hour
      hour = padded.substring(padded.length() - 6)
      String rgdt = container.get("OARGDT")
      String key = rgdt + hour + orno
      HashMap < String, String > order = new HashMap < String, String > ()
      order.put("ORNO", orno)
      order.put("RGDT", rgdt)
      order.put("RGTM", container.get("OARGTM") as String)
      order.put("ORTP", container.get("OAORTP") as String)
      order.put("OREF", container.get("OAOREF") as String)
      order.put("DIVI", container.get("OADIVI") as String)
      order.put("LMTS", container.get("OALMTS") as String)
      order.put("CUOR", container.get("OACUOR") as String)
      order.put("ORST", container.get("OAORST") as String)
      order.put("ORSL", container.get("OAORSL") as String)
      order.put("OBLC", container.get("OAOBLC") as String)
      order.put("DTID", container.get("OADTID") as String)
      order.put("CUCD", container.get("OACUCD") as String)
      order.put("CUNO", container.get("OACUNO") as String)
      order.put("TEPY", container.get("OATEPY") as String)
      order.put("NTLA", container.get("OANTLA") as String)
      pageOrders.put(key, order)
      if (hasOrderFilter) {
        ordersFilter = orno
      } else {
        ordersFilter = ordersFilter + "," + orno
      }
    }
  }

  /**
   * Retrieves a generic filter expression for querying orders based on customer numbers and other criteria.
   * @param customers An array of customer numbers
   * @return The ExpressionFactory representing the filter expression
   */

  private ExpressionFactory getGenericFilter(String[] customers) {
    ExpressionFactory expression = database.getExpressionFactory("OOHEAD")
    expression = expression.in("OACUNO", customers).and(expression.eq("OADIVI", iDIVI))
    //Don't include return orders
    String retOrtps = getTranslation("ECZEXCORTP")
    if (!retOrtps.isEmpty()) {
      String[] retOrtpList = retOrtps.split(",")
      for (String retOrtp: retOrtpList) {
        expression = expression.and(expression.ne("OAORTP", retOrtp))
      }
    }
    //Don't include cancelled orders
    expression = expression.and(expression.ne("OAORST", "90")).and(expression.ne("OAORSL", "90"))
    //Include ortp IN statement 
    if (!iORTP.isEmpty()) {
      String inOrtps = getTranslation("ECZ" + iORTP.toUpperCase())
      if (!inOrtps.isEmpty()) {
        String[] inOrtpList = inOrtps.split(",")
        expression = expression.in("OAORTP", inOrtpList)
      }
    }

    //set From Date filter
    if (!iFRDT.isEmpty()) {
      expression = expression.and(expression.ge("OAORDT", iFRDT))
    }
    // set To Date filter
    if (!iTODT.isEmpty()) {
      expression = expression.and(expression.le("OAORDT", iTODT))
    }
    // set Status filter
    if (!iSTAT.isEmpty()) {
      if (iSTAT.equals("In Progress")) {
        expression = expression.and(expression.lt("OAORST", "66"))
      } else if (iSTAT.equals("PartiallyShipped")) {
        expression = expression.and(expression.ge("OAORST", "66")).and(expression.lt("OAORSL", "66"))
      } else if (iSTAT.equals("Shipped")) {
        expression = expression.and(expression.ge("OAORST", "66")).and(expression.ge("OAORSL", "66"))
      }
    }
    return expression
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

  /**
   * Determines the status of an order based on its shipping and processing statuses.
   * @param orsl The shipping status of the order
   * @param orst The processing status of the order
   * @return A string representing the order status
   */
  public String getOrderStatus(int orsl, int orst) {
    String status = ""
    if ((orsl == 77 && orst == 77) || (orsl >= 66 && orsl < 90 && orst >= 66 && orst < 90)) {
      status = "Shipped"
    } else if (orsl < 66 && orst >= 66 && orst < 90) {
      status = "Partially shipped"
    } else if (orsl < 66 && orst < 66) {
      status = "Submitted"
    } else if (orsl == 90 && orst == 90) {
      status = "Cancelled"
    } else {
      status = orsl + "/" + orst
    }
    return status
  }

  /**
   *Get data from MHDISH table
   * @params 
   * @return 
   */
  public HashMap readDeliveries(String cuno, String orno, String tepy, String zStatus) {
    HashMap < String, String > delivery = new HashMap < String, String > ()
    int counter = 0
    String etrn = ""
    String shipDate = "0"
    String carr = ""
    String modl = ""
    Closure < ? > rsReadDeliveries = {
      DBContainer container ->
      String dlix = container.get("OQDLIX").toString()
      if (dlix != null) {
        if (counter == 0) {
          shipDate = container.get("OQDSDT")
          etrn = container.get("OQETRN").toString().trim()
          String modf = container.get("OQMODF").toString().trim()
          if (!modf.isEmpty()) {
            carr = modf
            modl = modf
          } else {
            carr = container.get("OQMODL").toString().trim()
            modl = container.get("OQMODL").toString().trim()
          }
        } else {
          String trckNum = container.get("OQETRN").toString().trim()
          if (!etrn.isEmpty() && !trckNum.isEmpty()) {
            etrn = etrn + "," + trckNum
          }
        }
        counter = counter + 1
        int pgrs = (container.get("OQPGRS").toString()) as Integer

        if (zStatus.equals("Submitted")) {
          int dsdt = container.get("OQDSDT") as Integer
          int shdt = shipDate as Integer
          if (dsdt < shdt || shdt == 0) {
            shipDate = dsdt as String
          }
        } else if ((zStatus.equals("Partially shipped") || zStatus.equals("Shipped")) && pgrs > 50) {
          // Read ODHEAD and FSLEDG
          HashMap < String, String > dv = getDeliveryDate(orno, container.get("OQWHLO").toString(), container.get("OQDLIX").toString(), tepy)
          int dsdt = dv.get("DLDT") as Integer
          int shdt = shipDate as Integer
          if (dsdt < shdt && dsdt != 0) {
            shipDate = dsdt as String
          }
        }
      }
    }

    ExpressionFactory expression = database.getExpressionFactory("MHDISH")
    expression = expression.eq("OQRIDN", orno).and(expression.eq("OQRORC", "3"))

    DBAction query = database.table("MHDISH").index("60").matching(expression).selection("OQDSDT", "OQMODF", "OQMODL", "OQETRN", "OQPGRS", "OQWHLO", "OQDLIX").build()
    DBContainer container = query.getContainer()
    container.set("OQCONO", iCONO)
    container.set("OQINOU", 1)
    container.set("OQCONA", cuno)
    query.readAll(container, 3, maxPageSize, rsReadDeliveries)

    carr = getCarrier(carr)
    delivery.put("DSDT", shipDate as String)
    delivery.put("CARR", carr as String)
    delivery.put("MODL", modl as String)
    delivery.put("ETRN", etrn as String)
    return delivery
  }

  /**
   * Retrieves the carrier name based on the provided model.
   * @param modl The model for which to retrieve the carrier name
   * @return The carrier name
   */

  public String getCarrier(String modl) {
    String carrier = ""
    DBAction query = database.table("CSYTAB").index("00").selection("CTTX15").build()
    DBContainer container = query.getContainer()
    container.set("CTCONO", iCONO)
    container.set("CTDIVI", "")
    container.set("CTLNCD", "GB")
    container.set("CTSTCO", "MODL")
    container.set("CTSTKY", modl)
    if (query.read(container)) {
      carrier = container.get("CTTX15").toString()
    }
    return carrier
  }

  /**
   * Retrieves delivery date information based on the order number, warehouse, delivery index, and order type.
   * @param orno The order number
   * @param whlo The warehouse location
   * @param dlix The delivery index
   * @param tepy The order type
   * @return A HashMap containing delivery date information
   */

  public HashMap < String, String > getDeliveryDate(String orno, String whlo, String dlix, String tepy) {
    HashMap < String, String > delInfo = new HashMap < String, String > ()
    String shipDate = "0"

    DBAction query = database.table("ODHEAD").index("00").selection("UADLDT", "UADIVI", "UAPYNO", "UAIVNO", "UAYEA4", "UACUNO").build()
    DBContainer container = query.getContainer()
    container.set("UACONO", iCONO)
    container.set("UAORNO", orno)
    container.set("UAWHLO", whlo)
    container.set("UADLIX", dlix as long)
    container.set("UATEPY", tepy)
    if (query.read(container)) {
      shipDate = container.get("UADLDT").toString()
    }
    delInfo.put("DLDT", shipDate)

    return delInfo
  }

  /**
   * Retrieves shipping charges for a given order number.
   * @param orno The order number for which to retrieve shipping charges
   */
  public void rtvShippingCharges(String orno) {
    //double[] charges = new double[3]
    chrg = 0.0
    frt = 0.0
    taxCharge = 0.0
    Closure < ? > rsReadMHDISH = {
      DBContainer container ->
      String dlix = container.get("OQDLIX") as String
      String whlo = container.get("OQWHLO")
      if (dlix != null) {
        rtvCharges(orno, dlix, whlo)
      }
    }
    ExpressionFactory expression = database.getExpressionFactory("MHDISH")
    // set order number filter
    expression = expression.eq("OQRIDN", orno).and(expression.eq("OQTTYP", "31"))
    DBAction query = database.table("MHDISH").index("00").matching(expression).selection("OQWHLO", "OQDLIX").build()
    DBContainer container = query.getContainer()
    container.set("OQCONO", iCONO)
    container.set("OQINOU", 1)
    query.readAll(container, 2, maxPageSize, rsReadMHDISH)

    rtvCharges(orno, "0", "")
  }

  /**
   * Retrieves charges for a given order number, delivery index, and warehouse location.
   * @param orno The order number
   * @param dlix The delivery index
   * @param whlo The warehouse location
   */

  public void rtvCharges(String orno, String dlix, String whlo) {
    Closure < ? > rsReadOOCHRG = {
      DBContainer container ->
      String crid = container.get("OECRID").toString().trim()
      if (crid != null) {
        double cram = container.get("OECRAM") as Double
        if (!dlix.equals("0")) {
          if (crid.startsWith("FRT")) {
            frt = frt + cram
          } else {
            if (crid.equals("SL TAX")) {
              taxCharge = taxCharge + cram
            } else {
              chrg = chrg + cram
            }
          }
        } else {
          String chst = container.get("OECHST").toString().trim()
          if (crid.startsWith("FRT") && !chst.equals("90")) {
            frt = frt + cram
          }
          if (!crid.startsWith("FRT") && !chst.equals("90")) {
            if (crid.equals("SL TAX")) {
              taxCharge = taxCharge + cram
            } else {
              chrg = chrg + cram
            }
          }
        }
      }
    }
    DBAction query = database.table("OOCHRG").index("10").selection("OECRID", "OECRAM", "OECHST").build()
    DBContainer container = query.getContainer()
    container.set("OECONO", iCONO)
    container.set("OEORNO", orno)
    container.set("OEDLIX", dlix as Long)
    container.set("OEWHLO", whlo)
    query.readAll(container, 4, maxPageSize, rsReadOOCHRG)
  }

  /**
   * Retrieves the discount amount for a given order number.
   * @param orno The order number
   * @return The discount amount
   */
  public double rtvDiscount(String orno) {
    double discount = 0.0

    Closure < ? > rsReadOOLICH = {
      DBContainer container ->
      String crid = container.get("O7CRID").toString().trim()
      if (crid != null) {
        double cram = container.get("O7CRAM") as Double
        discount = discount + cram
      }
    }
    DBAction query = database.table("OOLICH").index("00").selection("O7CRID", "O7CRAM").build()
    DBContainer container = query.getContainer()
    container.set("O7CONO", iCONO)
    container.set("O7ORNO", orno)
    query.readAll(container, 2, maxPageSize, rsReadOOLICH)

    return discount
  }

  /**
   * Retrieves the VAT amount for a given order number.
   * @param orno The order number
   * @return The VAT amount
   */
  public double rtvVatAmount(String orno) {
    double vatAmt = 0
    Map < String, String > params = ["CONO": iCONO.toString().trim(), "ORNO": orno]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.VTAM != null && response.IVTA != null) {
        //Get VAT amounts
        double respVTAM = 0
        String strVTAM = response.VTAM as String
        if (!strVTAM.trim().isEmpty()) {
          respVTAM = strVTAM as Double
        }
        double respIVTA = 0
        String strIVTA = response.IVTA as String
        if (!strIVTA.trim().isEmpty()) {
          respIVTA = response.IVTA as Double
        }
        vatAmt = respVTAM + respIVTA
      }
    }
    miCaller.call("OIS100MI", "GetOrderValue", params, callback)
    return vatAmt
  }

}