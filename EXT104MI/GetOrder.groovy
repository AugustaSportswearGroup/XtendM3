/**
 * README
 * This extension is being used to get order details for ecommerce application 
 *
 * Name: EXT104MI.GetOrder
 * Description: Transaction used to Get order details
 * Date	      Changed By                      Description
 *20231019  SuriyaN@fortude.co     Get order details for ecommerce application  */

public class GetOrder extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private int iCONO, maxPageSize = 10000, translationId
  private String iORNO, mbmd, cucd
  private boolean validInput = true, isPHWO = false
  private Map < String, String > order
  private double chrg, frt, taxCharge

  public GetOrder(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
  }

  /**
   * Main method to execute the transaction
   */

  public void main() {
    iCONO = program.LDAZD.CONO as Integer
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO").trim()
    validateInput()
    if (validInput) {
      outputData(order)
    }

  }

  /**
   * Method to validate input order number
   */

  public void validateInput() {
    //Validate Order Number
    Map < String, String > params = ["ORNO": iORNO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.ORNO == null) {
        mi.error("Invalid Order Number " + iORNO)
        validInput = false
        return
      } else {
        order = response
      }
    }
    miCaller.call("OIS100MI", "GetOrderHead", params, callback)
  }

  /**
   * Method to output order data
   * @param order Map containing order details
   */

  public void outputData(Map < String, String > order) {
    mi.outData.put("DIVI", order.DIVI)
    mi.outData.put("ORNO", order.ORNO)
    mi.outData.put("OREF", order.OREF)
    mi.outData.put("ORDT", order.ORDT)
    mi.outData.put("CUOR", order.CUOR)
    mi.outData.put("CUCD", order.CUCD)
    mi.outData.put("ORSL", order.ORSL)
    mi.outData.put("ORSH", order.ORST)
    mi.outData.put("TEPA", order.TEPA)
    String tepy = order.TEPY.toString().trim()
    tepy = getTranslation("ECZPT" + tepy)
    if (tepy.isEmpty()) {
      tepy = getTranslation("ECZPTDEFAU")
    }
    mi.outData.put("TEPY", tepy)
    mi.outData.put("OTIP", getTipNumber(order.ORNO))
    String zStatus = getOrderStatus(order.ORSL as Integer, order.ORST as Integer)
    mi.outData.put("ORST", zStatus)
    HashMap < String, String > delivery = readDeliveries(order.CUNO, order.ORNO, order.TEPY, zStatus)
    mi.outData.put("DSDT", delivery.get("DSDT"))
    mi.outData.put("CARR", delivery.get("CARR"))
    mi.outData.put("MODL", delivery.get("MODL"))
    mi.outData.put("ETRN", delivery.get("ETRN"))
    double vtam = delivery.get("VTAM") as Double
    mi.outData.put("IVNO", delivery.get("IVNO"))
    HashMap < String, String > shipToAddress = getShippingAddress(order.ORNO)
    mi.outData.put("SCUN", shipToAddress.SCUN)
    mi.outData.put("SAD1", shipToAddress.SAD1)
    mi.outData.put("SAD2", shipToAddress.SAD2)
    mi.outData.put("STOW", shipToAddress.STOW)
    mi.outData.put("SECA", shipToAddress.SECA)
    mi.outData.put("SPON", shipToAddress.SPON)
    mi.outData.put("SCSC", shipToAddress.SCSC)
    HashMap < String, String > billToAddress = getBillingAddress(order.ORNO)
    mi.outData.put("BCUN", billToAddress.BCUN)
    mi.outData.put("BAD1", billToAddress.BAD1)
    mi.outData.put("BAD2", billToAddress.BAD2)
    mi.outData.put("BTOW", billToAddress.BTOW)
    mi.outData.put("BECA", billToAddress.BECA)
    mi.outData.put("BPON", billToAddress.BPON)
    mi.outData.put("BCSC", billToAddress.BCSC)
    HashMap < String, String > orderFin = getOrderFinancial(order.ORNO)
    mi.outData.put("PTOT", orderFin.NTLA)
    rtvShippingCharges(order.ORNO)
    mi.outData.put("STOT", frt as String)
    mi.outData.put("CRAM", chrg as String)
    double discount = rtvDiscount(order.ORNO)
    mi.outData.put("DTOT", discount as String)
    double xavtam = rtvVatAmount(order.ORNO)
    mi.outData.put("VTAM", xavtam as String)
    double ntla = orderFin.NTLA as Double
    ntla = ntla + frt + chrg + discount + taxCharge + xavtam
    mi.outData.put("NTLA", ntla as String)
    mi.write()
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
   * Method to get carrier based on model
   * @param modl Model number
   * @return Carrier name
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
   * Method to get tip number for an order
   * @param orderNumber Order number
   * @return Tip number
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
   * Method to get order status based on ORSL and ORST
   * @param orsl Order SL status
   * @param orst Order ST status
   * @return Order status
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
    HashMap < String, String > invoices = new HashMap < String, String > ()
    int counter = 0
    String etrn = ""
    String shipDate = "0"
    double vtam = 0D
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
          HashMap < String, String > dv = getDeliveryDateAndVAT(orno, container.get("OQWHLO").toString(), container.get("OQDLIX").toString(), tepy, invoices)
          int dsdt = dv.get("DLDT") as Integer
          int shdt = shipDate as Integer
          if (dsdt < shdt && dsdt != 0) {
            shipDate = dsdt as String
          }
          double vtam_temp = dv.get("VTAM") as Double
          vtam = vtam + vtam_temp
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

    int invoiceCounter = 0
    String ivnos = ""
    for (Map.Entry < String, String > entry: invoices.entrySet()) {
      String invoiceValue = entry.getValue()
      if (invoiceCounter == 0) {
        ivnos = invoiceValue
      } else {
        ivnos = ivnos + "," + invoiceValue
      }
      invoiceCounter = invoiceCounter + 1
    }

    carr = getCarrier(carr)
    delivery.put("DSDT", shipDate as String)
    delivery.put("CARR", carr as String)
    delivery.put("MODL", modl as String)
    delivery.put("ETRN", etrn as String)
    delivery.put("VTAM", vtam as String)
    delivery.put("IVNO", ivnos as String)
    return delivery
  }

  /**
   * Method to get delivery date and VAT amount
   * @param orno Order number
   * @param whlo Warehouse location
   * @param dlix Delivery number
   * @param tepy Transaction type
   * @param invoices Map containing invoice information
   * @return Map containing delivery date and VAT amount
   */

  public HashMap < String, String > getDeliveryDateAndVAT(String orno, String whlo, String dlix, String tepy, HashMap < String, String > invoices) {
    HashMap < String, String > delInfo = new HashMap < String, String > ()
    String shipDate = "0"
    String vtam = "0"
    int counter = 0
    DBAction query = database.table("ODHEAD").index("00").selection("UADLDT", "UADIVI", "UAPYNO", "UAIVNO", "UAYEA4", "UACUNO").build()
    DBContainer container = query.getContainer()
    container.set("UACONO", iCONO)
    container.set("UAORNO", orno)
    container.set("UAWHLO", whlo)
    container.set("UADLIX", dlix as long)
    container.set("UATEPY", tepy)
    if (query.read(container)) {
      shipDate = container.get("UADLDT").toString()
      int ivno = container.get("UAIVNO").toString() as Integer
      if (ivno != 0) {
        Closure < ? > rsReadVTAM = {
          DBContainer container2 ->
          String cino = container2.get("ESCINO").toString()
          if (cino != null && counter == 0) {
            counter = counter + 1
            vtam = container2.get("ESVTAM").toString()
            if (vtam == null) {
              vtam = "0"
            }
            int reco = (container2.get("ESRECO").toString()) as Integer
            String invoStat = "O"
            if (reco == 9) {
              invoStat = "C"
            }
            String duedate = container2.get("ESDUDT").toString()
            String closedate = container2.get("ESREDE").toString()
            invoices.put(ivno as String, ivno + "|" + invoStat + "|" + duedate + "|" + closedate)
          }
        }
        DBAction query1 = database.table("FSLEDG").index("10").selection("ESCINO", "ESVTAM", "ESDUDT", "ESREDE", "ESRECO").build()
        DBContainer container1 = query1.getContainer()
        container1.set("ESCONO", iCONO)
        container1.set("ESDIVI", container.get("UADIVI"))
        container1.set("ESPYNO", container.get("UAPYNO"))
        String cino = "00000000" + ivno
        cino = cino.substring(cino.length() - 9)
        container1.set("ESCINO", cino)
        container1.set("ESINYR", container.get("UAYEA4"))
        container1.set("ESCUNO", container.get("UACUNO"))
        container1.set("ESTRCD", 10)
        query1.readAll(container1, 7, maxPageSize, rsReadVTAM)
      }
    }
    delInfo.put("DLDT", shipDate)
    delInfo.put("VTAM", vtam)
    return delInfo
  }

  /**
   * Method to get shipping address for an order
   * @param orno Order number
   * @return Map containing shipping address
   */

  public HashMap getShippingAddress(String orno) {
    HashMap < String, String > shipAddress = new HashMap < String, String > ()
    Map < String, String > params = ["CONO": iCONO.toString().trim(), "ORNO": orno.trim(), "ADRT": "1"]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.error == null) {
        shipAddress.put("SCUN", response.CUNM)
        shipAddress.put("SAD1", response.CUA1)
        shipAddress.put("SAD2", response.CUA2)
        shipAddress.put("STOW", response.TOWN)
        shipAddress.put("SECA", response.ECAR)
        shipAddress.put("SPON", response.PONO)
        shipAddress.put("SCSC", response.CSCD)
      } else {
        shipAddress.put("SCUN", "")
        shipAddress.put("SAD1", "")
        shipAddress.put("SAD2", "")
        shipAddress.put("STOW", "")
        shipAddress.put("SECA", "")
        shipAddress.put("SPON", "")
        shipAddress.put("SCSC", "")
      }
    }
    miCaller.call("OIS100MI", "GetAddress", params, callback)
    return shipAddress
  }

  /**
   * Method to get billing address for an order
   * @param orno Order number
   * @return Map containing billing address
   */

  public HashMap getBillingAddress(String orno) {
    HashMap < String, String > billAddress = new HashMap < String, String > ()
    Map < String, String > params = ["CONO": iCONO.toString().trim(), "ORNO": orno.trim(), "ADRT": "3"]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.error == null) {
        billAddress.put("BCUN", response.CUNM)
        billAddress.put("BAD1", response.CUA1)
        billAddress.put("BAD2", response.CUA2)
        billAddress.put("BTOW", response.TOWN)
        billAddress.put("BECA", response.ECAR)
        billAddress.put("BPON", response.PONO)
        billAddress.put("BCSC", response.CSCD)
      } else {
        billAddress.put("BCUN", "")
        billAddress.put("BAD1", "")
        billAddress.put("BAD2", "")
        billAddress.put("BTOW", "")
        billAddress.put("BECA", "")
        billAddress.put("BPON", "")
        billAddress.put("BCSC", "")
      }
    }
    miCaller.call("OIS100MI", "GetAddress", params, callback)
    return billAddress
  }

  /**
   * Method to get order financial details
   * @param orno Order number
   * @return Map containing order financial details
   */

  public HashMap getOrderFinancial(String orno) {
    HashMap < String, String > orderFin = new HashMap < String, String > ()
    DBAction query = database.table("OOHEAD").index("00").selection("OANTLA", "OATXAP", "OATAXC").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    container.set("OAORNO", orno)
    if (query.read(container)) {
      orderFin.put("NTLA", container.get("OANTLA") as String)
    }
    return orderFin
  }

  /**
   * Method to retrieve shipping charges for an order
   * @param orno Order number
   */

  public void rtvShippingCharges(String orno) {
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
   * Method to retrieve charges for an order
   * @param orno Order number
   * @param dlix Delivery number
   * @param whlo Warehouse location
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
   * Method to retrieve discount for an order
   * @param orno Order number
   * @return Discount amount
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
   * Method to retrieve VAT amount for an order
   * @param orno Order number
   * @return VAT amount
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