/**
 * README
 * This extension is being used to list customer order lines
 *
 * Name: EXT104MI.LstOrderLines
 * Description: Transaction used to Add order charges to customer order based on data from globalship
 * Date	      Changed By                      Description
 *20231019  SuriyaN@fortude.co     Add order charges to customer order based on data from globalship */

import java.text.DecimalFormat

public class LstOrderLines extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private int iCONO, maxPageSize = 10000, translationId
  private String iORNO, mbmd, cucd
  private Map < String, HashMap > orderList = new TreeMap < String, HashMap > ()
  private Map < String, HashMap > addressList = new HashMap < String, HashMap > ()
  private Map < String, HashMap > deliveryList = new HashMap < String, HashMap > ()
  private boolean validInput = true, isPHWO = false

  public LstOrderLines(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
  }

  /**
   * Main method
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
      cucd = order.get("CUCD")
      String phwoOrtp = getTranslation("PHWOORTP")
      String ortp = "-" + order.get("ORTP").trim() + "-"
      isPHWO = phwoOrtp.contains(ortp)
      readOrderLines(order.get("ORNO"))
    }
  }

  /**
   * Validates the input order number.
   * @return boolean - Returns true if input is valid, false otherwise.
   */

  public boolean validateInput() {
    if (!iORNO.trim().isEmpty()) {
      //Validate Customer Number
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
   * Validates the input order number based on OREF.
   * @return boolean - Returns true if input is valid, false otherwise.
   */
  public boolean validateBasedOnOREF() {
    ExpressionFactory expression = database.getExpressionFactory("OOHEAD")
    // set Ecom order number filter
    String oref = "EC" + iORNO
    expression = expression.eq("OAOREF", oref)
    DBAction query = database.table("OOHEAD").index("00").matching(expression).selection("OAORNO", "OAORTP", "OACUCD").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    query.readAll(container, 1, maxPageSize, rsReadOrders)
    return validInput
  }

  /**
   * Callback function to read orders.
   */

  Closure < ? > rsReadOrders = {
    DBContainer container ->
    String orno = container.get("OAORNO").toString()
    if (orno != null) {
      validInput = true
      HashMap < String, String > order = new HashMap < String, String > ()
      order.put("ORNO", orno)
      order.put("ORTP", container.get("OAORTP").toString())
      order.put("CUCD", container.get("OACUCD").toString())
      orderList.put(orno, order)
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

  /**
   * Validates and retrieves line charges for the given order number.
   * @param orno The order number
   */

  public void readOrderLines(String orno) {
    DBAction query = database.table("OOLINE").index("00").selection("OBORNO", "OBPONR", "OBPOSX", "OBHDPR", "OBITDS", "OBORQT", "OBSAPR", "OBNEPR", "OBITNO", "OBMODL", "OBDSDT", "OBORST", "OBRORC", "OBRORN", "OBCFIN", "OBELNO", "OBMOYE", "OBCUSX", "OBCUOR", "OBADID", "OBTEPY", "OBDSTX").build()
    DBContainer container = query.getContainer()
    container.set("OBCONO", iCONO)
    container.set("OBORNO", orno)

    query.readAll(container, 2, maxPageSize, rsReadOrderLines)
  }

  Closure < ? > rsReadOrderLines = {
    DBContainer container ->
    String ponr = container.get("OBPONR").toString()
    if (ponr != null) {
      mi.outData.put("ITNO", container.get("OBHDPR").toString() + " " + container.get("OBITDS").toString())
      int orqt = container.get("OBORQT") as Integer
      mi.outData.put("ORQT", orqt as String)
      mi.outData.put("SHDT", container.get("OBDSDT").toString())
      String status = "Submitted"
      int orst = container.get("OBORST").toString() as Integer
      int rorc = container.get("OBRORC").toString() as Integer
      if (orst == 77 || orst == 66) {
        status = "Shipped"
      } else {
        String rorn = container.get("OBRORN").toString()
        if (orst < 66 && rorc == 1 && !rorn.isEmpty() && !rorn.equals("￮￮￮￮￮￮￮￮￮￮")) {
          status = "MO Released"
        }
      }
      mi.outData.put("ORST", status)
      mi.outData.put("STAT", container.get("OBORST").toString())
      mi.outData.put("RNUM", container.get("OBDSTX").toString())
      mi.outData.put("CUCD", cucd)
      mi.outData.put("SNUM", container.get("OBELNO").toString())
      int cusx = container.get("OBCUSX").toString().trim() as Integer
      mi.outData.put("CUSX", cusx as String)
      String wnum = container.get("OBMOYE").toString().trim()
      String orno = container.get("OBORNO").toString()
      mi.outData.put("WNUM", wnum)
      if (isPHWO && wnum.isEmpty()) {
        mi.outData.put("WNUM", orno + "_" + cusx)
      }
      mi.outData.put("CUOR", container.get("OBCUOR").toString())
      mi.outData.put("PONR", container.get("OBPONR").toString())

      String posx = container.get("OBPOSX").toString()
      String adid = container.get("OBADID").toString().trim()
      HashMap address = addressList.get(adid)
      if (address == null) {
        getLineAddress(adid, orno, ponr, posx)
        address = addressList.get(adid)
      }
      if (address != null) {
        mi.outData.put("SAD1", address.get("CUA1").toString())
        mi.outData.put("SAD2", address.get("CUA2").toString())
        mi.outData.put("SPON", address.get("PONO").toString())
        mi.outData.put("STOW", address.get("TOWN").toString())
        mi.outData.put("SECA", address.get("ECAR").toString())
        mi.outData.put("SCSC", address.get("CSCD").toString())
      }
      double sapr = container.get("OBSAPR").toString() as double
      DecimalFormat df = new DecimalFormat("#0.00")
      String formattedSapr = df.format(sapr)
      mi.outData.put("PRWD", formattedSapr)
      double lineCharge = getLineCharge(orno, ponr, posx)
      double oNetPrice = container.get("OBNEPR").toString() as double
      double netPrice = oNetPrice
      if (orqt != 0 && lineCharge != 0) {
        double charge = lineCharge / orqt
        charge = Math.round(charge * 100) / 100
        netPrice = netPrice - charge
      }
      mi.outData.put("SAPR", df.format(netPrice))
      double lineAmount = (orqt * oNetPrice) + lineCharge
      lineAmount = Math.round(lineAmount * 100) / 100
      mi.outData.put("LNAM", df.format(lineAmount))
      String itno = container.get("OBITNO").toString().trim()
      mi.outData.put("ISKU", itno)
      String cfin = container.get("OBCFIN").toString()
      setItemAttributes(itno, cfin as Integer)
      String defModl = container.get("OBMODL").toString().trim()
      String tepy = container.get("OBTEPY").toString().trim()
      getDeliveryInfo(orno, defModl, tepy, ponr as Integer, posx as Integer, status)
      mi.write()

    }
  }

  /**
   * Retrieves line address using address identifier, order number, purchase order number, and position.
   * @param adid The address identifier
   * @param orno The order number
   * @param ponr The purchase order number
   * @param posx The position
   */

  public void getLineAddress(String adid, String orno, String ponr, String posx) {
    Map < String, String > params = ["CONO": iCONO.toString().trim(), "ORNO": orno, "PONR": ponr, "POSX": posx]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CUA1 != null) {
        HashMap address = new HashMap < String, String > ()
        address.put("CUNM", response.CUNM)
        address.put("CUA1", response.CUA1)
        address.put("CUA2", response.CUA2)
        address.put("PONO", response.PONO)
        address.put("ECAR", response.ECAR)
        address.put("TOWN", response.TOWN)
        address.put("CSCD", response.CSCD)
        addressList.put(adid, address)
      } else {
        HashMap address = new HashMap < String, String > ()
        address.put("CUNM", "")
        address.put("CUA1", "")
        address.put("CUA2", "")
        address.put("PONO", "")
        address.put("ECAR", "")
        address.put("TOWN", "")
        address.put("CSCD", "")
        addressList.put(adid, address)
      }
    }
    miCaller.call("OIS100MI", "GetLineAddress", params, callback)
  }

  /**
   * getLineCharge - Get the total line charge data
   * @params String orno, int ponr, int pos
   * @return double
   */
  public double getLineCharge(String orno, String ponr, String posx) {
    double lineCharge = 0.0
    //Validate Delivery Number
    Map < String, String > paramsLstLineCharge = ["CONO": iCONO.toString().trim(), "ORNO": orno, "PONR": ponr, "POSX": posx]
    Closure < ? > callbackLstLineCharge = {
      Map < String,
      String > response ->
      if (response.CRID != null) {
        double pbam = response.PBAM as double
        lineCharge = lineCharge + pbam
      }
    }
    miCaller.call("OIS100MI", "LstLineCharge", paramsLstLineCharge, callbackLstLineCharge)

    return lineCharge
  }

  /**
   * Sets item attributes based on the item number and configuration flag.
   * @param itno The item number
   * @param cfin The configuration flag
   */
  public void setItemAttributes(String itno, int cfin) {
    DBAction query = database.table("MITMAH").index("00").selection("HMSTYN", "HMTX15", "HMOPTY").build()
    DBContainer container = query.getContainer()
    container.set("HMCONO", iCONO)
    container.set("HMITNO", itno)
    if (query.read(container)) {
      mi.outData.put("SIZE", container.get("HMTX15").toString())
      DBAction query1 = database.table("MPDOPT").index("00").selection("PFTX30").build()
      DBContainer container1 = query1.getContainer()
      container1.set("PFCONO", iCONO)
      container1.set("PFOPTN", container.get("HMOPTY").toString())
      if (query1.read(container1)) {
        mi.outData.put("COLR", container1.get("PFTX30").toString())
      }
    } else {
      DBAction query1 = database.table("MITMAS").index("00").selection("MMITCL", "MMITTY").build()
      DBContainer container1 = query1.getContainer()
      container1.set("MMCONO", iCONO)
      container1.set("MMITNO", itno)
      if (query1.read(container1)) {
        String itcl = container1.get("MMITCL").toString().trim()
        String itty = container1.get("MMITTY").toString().trim()
        String colorPrefix = getTranslation("ECZC" + itcl).trim()
        if (colorPrefix.isEmpty()) {
          String woolJacket = getTranslation("ECZC" + itty).trim()
          if (cfin != 0 && !woolJacket.isEmpty()) {
            mi.outData.put("ISKU", itno)
            String[] jdta = getWoolJacketData(cfin)
            mi.outData.put("SIZE", jdta[0])
            mi.outData.put("COLR", jdta[1])
          }
        }
        if (cfin != 0 && !colorPrefix.isEmpty()) {
          getPHData(itno, itcl, colorPrefix, cfin)
        }
      }
    }

    if (itno.startsWith("0P")) {
      boolean modify = false
      DBAction query1 = database.table("MITMAS").index("00").selection("MMITCL", "MMITDS").build()
      DBContainer container1 = query1.getContainer()
      container1.set("MMCONO", iCONO)
      container1.set("MMITNO", itno)
      if (query1.read(container1)) {
        String itcl = container1.get("MMITCL").toString().trim()
        String itds = container1.get("MMITDS").toString().trim()
        if (itcl.equals("PHCN")) {
          modify = true
        }
        if (!modify && itds.toUpperCase().contains("CROWN")) {
          modify = true
        }
        if (modify) {
          mi.outData.put("ISKU", itno.substring(2))
        }
      }
    }
  }

  /**
   * Retrieves wool jacket data based on the configuration flag.
   * @param cfin The configuration flag
   * @return An array containing size and color data for the wool jacket
   */

  public String[] getWoolJacketData(int cfin) {
    String[] wjdata = new String[2]
    String sizeWJ = ""
    String colorWJ = ""

    Closure < ? > rsMPDCDFData = {
      DBContainer container ->
      String ftid = container.get("QJFTID").toString()
      if (ftid != null) {
        if (ftid.trim().equals("SIZE") && sizeWJ.isEmpty()) {
          sizeWJ = container.get("QJOPTN").toString()
        }
        if (ftid.trim().equals("COLOR") && colorWJ.isEmpty()) {
          String optn = container.get("QJOPTN").toString()
          DBAction query1 = database.table("MPDOPT").index("00").selection("PFTX30").build()
          DBContainer container1 = query1.getContainer()
          container1.set("PFCONO", iCONO)
          container1.set("PFOPTN", optn)
          if (query1.read(container1)) {
            colorWJ = container1.get("PFTX30").toString()
          }
        }
      }
    }

    DBAction query = database.table("MPDCDF").index("00").selection("QJOPTN", "QJFTID").build()
    DBContainer container = query.getContainer()
    container.set("QJCONO", iCONO)
    container.set("QJCFIN", cfin)
    query.readAll(container, 2, maxPageSize, rsMPDCDFData)
    wjdata[0] = sizeWJ
    wjdata[1] = colorWJ
    return wjdata
  }

  /**
   * Retrieves PH data based on item number, item class, color prefix, and configuration flag.
   * @param itno The item number
   * @param itcl The item class
   * @param colorPrefix The color prefix
   * @param cfin The configuration flag
   */

  public void getPHData(String itno, String itcl, String colorPrefix, int cfin) {
    String size = ""
    String color = ""
    String item = itno
    String keyword = getTranslation("ECZS" + itcl).trim()

    Closure < ? > rsMPDCDFData = {
      DBContainer container ->
      String ftid = container.get("QJFTID").toString()
      if (ftid != null) {
        if (keyword.contains(ftid.trim())) {
          size = container.get("QJOPTN").toString()
          String optn = container.get("QJOPTN").toString()
          DBAction query1 = database.table("MPDOPT").index("00").selection("PFTX30", "PFTX15").build()
          DBContainer container1 = query1.getContainer()
          container1.set("PFCONO", iCONO)
          container1.set("PFOPTN", optn)
          if (query1.read(container1)) {
            mi.outData.put("SIZE", container1.get("PFTX15").toString())
          }
        } else if (ftid.startsWith(colorPrefix)) {
          color = container.get("QJOPTN").toString()
          String optn = container.get("QJOPTN").toString()
          DBAction query1 = database.table("MPDOPT").index("00").selection("PFTX30", "PFTX15").build()
          DBContainer container1 = query1.getContainer()
          container1.set("PFCONO", iCONO)
          container1.set("PFOPTN", optn)
          if (query1.read(container1)) {
            mi.outData.put("COLR", container1.get("PFTX30").toString())
          }
        }
      }
    }

    DBAction query = database.table("MPDCDF").index("00").selection("QJOPTN", "QJFTID").build()
    DBContainer container = query.getContainer()
    container.set("QJCONO", iCONO)
    container.set("QJCFIN", cfin)
    query.readAll(container, 2, maxPageSize, rsMPDCDFData)
    if (!size.isEmpty() && !color.isEmpty()) {
      String mValue = getMatrixValue(itno, size, color, "C")
      if (mValue.isEmpty()) {
        mValue = getMatrixValue(itno, size, color, "FC")
      }
      mi.outData.put("ISKU", mValue)
    }
  }

  /**
   * Retrieves matrix value based on item number, size, color, and suffix.
   * @param itno The item number
   * @param size The size
   * @param color The color
   * @param suffix The suffix
   * @return The matrix value
   */

  public String getMatrixValue(String itno, String size, String color, String suffix) {
    String mValue = ""
    int count = 0
    Closure < ? > rsMPMXVAData = {
      DBContainer container ->
      String mrc1 = container.get("QBMRC1").toString()
      count = count + 1
      if (mrc1 != null && count == 1) {
        if (mrc1.startsWith("0P")) {
          mValue = mrc1.substring(2)
        }
      }
    }

    DBAction query = database.table("MPMXVA").index("00").selection("QBMRC1", "QBMXID").build()
    DBContainer container = query.getContainer()
    container.set("QBCONO", iCONO)
    container.set("QBMXID", itno + suffix)
    container.set("QBMVC1", size)
    container.set("QBMVC2", color)
    container.set("QBMVN1", 0)
    container.set("QBMVN2", 0)
    query.readAll(container, 6, maxPageSize, rsMPMXVAData)

    return mValue
  }

  /**
   * Retrieves delivery information based on order number, default model, transport type, purchase order number, position, and status.
   * @param orno The order number
   * @param defModl The default model
   * @param tepy The transport type
   * @param ponr The purchase order number
   * @param posx The position
   * @param status The status
   */

  public void getDeliveryInfo(String orno, String defModl, String tepy, int ponr, int posx, String status) {
    String carr = ""
    String modl = ""
    String etrn = ""
    String shipDate = ""
    String ivno = ""
    String dlix = ""
    int counter = 0

    Closure < ? > rsMHDISHData = {
      DBContainer container ->
      String dlixTemp = container.get("URDLIX").toString()
      if (dlixTemp != null && counter == 0) {
        counter = counter + 1
        HashMap delivery = deliveryList.get(dlixTemp)
        if (delivery != null) {
          carr = delivery.get("CARR")
          modl = delivery.get("MODL")
          etrn = delivery.get("ETRN")
          shipDate = delivery.get("SHDT")
          ivno = delivery.get("IVNO")
          carr = delivery.get("CARR")
          dlix = delivery.get("DLIX")
        } else {
          DBAction query1 = database.table("MHDISH").index("00").selection("OQDLIX", "OQMODF", "OQMODL", "OQDSDT", "OQETRN", "OQPGRS", "OQWHLO").build()
          DBContainer container1 = query1.getContainer()
          container1.set("OQCONO", iCONO)
          container1.set("OQINOU", 1)
          dlix = container.get("URDLIX").toString().trim()
          container1.set("OQDLIX", dlix as Long)
          if (query1.read(container1)) {
            etrn = container1.get("OQETRN")
            String modf = container1.get("OQMODF").toString().trim()
            if (!modf.isEmpty()) {
              modl = modf
            } else {
              modl = container1.get("OQMODL").toString().trim()
            }
          } else {
            modl = defModl
          }
          carr = getCarrier(modl)
          int pgrs = (container1.get("OQPGRS").toString().trim()) as Integer
          if (status.equals("Submitted")) {
            shipDate = container1.get("OQDSDT").toString()
          } else if (status.equals("Shipped") && pgrs > 50) {
            DBAction query2 = database.table("ODHEAD").index("00").selection("UADLDT", "UAIVNO").build()
            DBContainer container2 = query2.getContainer()
            container2.set("UACONO", iCONO)
            container2.set("UAORNO", orno)
            container2.set("UAWHLO", container1.get("OQWHLO"))
            container2.set("UADLIX", dlix as Long)
            container2.set("UATEPY", tepy)
            if (query2.read(container2)) {
              shipDate = container2.get("UADLDT").toString().trim()
              ivno = container2.get("UAIVNO").toString().trim()
              if (ivno.equals("0")) {
                ivno = ""
              }
            }
          }
          delivery = new HashMap < String, String > ()
          delivery.put("CARR", carr)
          delivery.put("MODL", modl)
          delivery.put("ETRN", etrn)
          delivery.put("SHDT", shipDate)
          delivery.put("IVNO", ivno)
          delivery.put("DLIX", dlixTemp)
          deliveryList.put(dlixTemp, delivery)
        }
      }
    }

    DBAction query = database.table("MHDISL").index("10").selection("URDLIX").build()
    DBContainer container = query.getContainer()
    container.set("URCONO", iCONO)
    container.set("URRORC", 3)
    container.set("URRIDN", orno)
    container.set("URRIDL", ponr)
    container.set("URRIDX", posx)
    query.readAll(container, 5, maxPageSize, rsMHDISHData)

    mi.outData.put("CARR", carr)
    mi.outData.put("MODL", modl)
    mi.outData.put("ETRN", etrn)
    mi.outData.put("SHDT", shipDate)
    mi.outData.put("IVNO", ivno)
    mi.outData.put("DLIX", dlix)
  }

  /**
   * Retrieves carrier based on model.
   * @param modl The model
   * @return The carrier
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
}