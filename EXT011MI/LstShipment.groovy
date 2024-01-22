/**
 * README
 * This extension is being used to list data for Logicor's Globalship. Globalship is a shipping solution used for
 * processing shipments of orders for delivery to both domestic and international destinations.
 *
 * Name: EXT011MI.LstShipment
 * Description: List details of items to be shipped.
 * Date       Changed By                       Description
 * 20230508   SuriyaN@fortude.co     Initial development from MAK cPrepareShipment
 *
 */
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class LstShipment extends ExtendM3Transaction {
  //Global Variables
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private long iDLIX
  private int iCONO, pickingLineSuffix, translationId, orderLineNumber, indexBoxBySku, orderCategory, plFromDate, currentDate, maxPageSize = 10000
  private boolean boxByKit, boxByLeague, boxByTeam, boxByStg2, boxBySKU, canWHLORunCase, isWHLOTypeCase
  private boolean isWUPPriceOverride, isDDPPriceOverride, isMCHEAD
  private String  iBCNT, orderNumber, warehouse, tempOrderNumber, groupIndex, stage, deliveryMethod, costingType, zzFacility, zzPriceList, overrideCurrency
  private boolean validInput = true
  private HashMap <String, String> orderHead
  private HashMap <String, String> addressData
  private HashMap <String, HashMap<String, String>> allocationLineBLT

  public LstShipment(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
  }

  /**
   * Main function
   * @param
   * @return
   */
  public void main() {
    iCONO =  program.LDAZD.CONO as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0L : mi.inData.get("DLIX") as Long
    iBCNT = (mi.inData.get("BCNT") == null || mi.inData.get("BCNT").trim().isEmpty()) ? "N" : mi.inData.get("BCNT") as String
    translationId = 0
    plFromDate = 0
    boxByKit = false
    boxByLeague = false
    boxByTeam = false
    boxByStg2 = false
    boxBySKU = false
    isWUPPriceOverride = false
    isDDPPriceOverride = false
    isMCHEAD = false
    validateInput(iCONO, iDLIX) //Check if inputs are valid.
    if (validInput) {
      currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger() // Get current date
      orderHead = getOrderHeadData(orderNumber) // Read order header
      addressData = getDeliveryAddress(iDLIX as String) // Read delivery address
      canWHLORunCase = !(getTranslation("FWE" + warehouse).equalsIgnoreCase("y"))
      isWHLOTypeCase = !(getTranslation("FWTE" + warehouse).equalsIgnoreCase("y"))
      costingType = ""
      zzFacility = ""
      zzPriceList = ""
      overrideCurrency = ""
      //Prepare data for line price override of international and IPD shipments
      if (iBCNT.equalsIgnoreCase("x") || iBCNT.equalsIgnoreCase("y") || iBCNT.equalsIgnoreCase("z")) {
        String frtPoOrtp = getTranslation("FRTPOORTP")
        String frtPoCscd = getTranslation("FRTPOCSCD")
        String frtPoCarr = getTranslation("FRTPOCARR")
        String ortp = orderHead.get("OAORTP")
        String cscd = addressData.get("CSCD")
        String tedl = orderHead.get("OATEDL")
        String prrf = ""
        costingType = getTranslation(warehouse.trim() + deliveryMethod.trim() + cscd.trim())
        zzFacility = getFacility(warehouse)
        if (frtPoOrtp.contains(ortp.trim()) &&  frtPoCscd.contains(cscd.trim()) && frtPoCarr.contains(deliveryMethod.substring(0,1).toString())) {
          String cucd = orderHead.get("OACUCD")
          overrideCurrency = cucd.trim()
          isWUPPriceOverride = true
          prrf = getTranslation("FRTPOPRRF")
          plFromDate = getPriceListDate(prrf, cucd)
          if (plFromDate == 0) {
            isWUPPriceOverride = false
          }
        }
        String frtDDPPO = getTranslation("FRT" + tedl.trim() + cscd.trim())
        if (!frtDDPPO.isEmpty()) {
          if (cscd.trim().equals("US")) {
            isMCHEAD = true
          }
          String[] tokens = frtDDPPO.split("-")
          isDDPPriceOverride = true
          prrf = tokens[0].trim()
          overrideCurrency = tokens[1].trim()
          int frmDate = getPriceListDate(prrf, overrideCurrency)
          if (frmDate == 0) {
            isDDPPriceOverride = false
          } else {
            plFromDate = frmDate
          }
        }
        zzPriceList = prrf
      }
      //Check for special boxing logic
      if (orderCategory == 3) {
        specialBoxingCheck(orderHead, addressData)
      }

      //handle box by league and box by team packaging
      if (boxByLeague || boxByTeam) {
        allocationLineBLT = new HashMap <String, HashMap<String, String>>()
        orderLineNumber = 0
        tempOrderNumber = getTemporaryOrderNumber(orderNumber)
        ExpressionFactory expression = database.getExpressionFactory("MITALO")
        expression = expression.eq("MQRIDI", iDLIX as String)
        DBAction query = database.table("MITALO").index("10").matching(expression).selection("MQITNO", "MQRIDL", "MQRIDX", "MQRFTX", "MQCAMU", "MQALQT", "MQBANO", "MQWHSL", "MQREPN").build() //Get delivery related information
        DBContainer container = query.getContainer()
        container.set("MQCONO", iCONO as Integer)
        if (orderCategory == 3) {
          container.set("MQTTYP", 31)
        } else if (orderCategory == 5) {
          container.set("MQTTYP", 51)
        } else if (orderCategory == 9) {
          container.set("MQTTYP", 92)
        }
        container.set("MQRIDN", orderNumber)
        container.set("MQRIDO", 0)
        query.readAll(container, 4,maxPageSize, getAllocationLinesBLT)
        for (String key: allocationLineBLT.keySet()) {
          HashMap <String, String> recordBLT = allocationLineBLT.get(key)
          //output data
          buildOutput(recordBLT)
        }
      }

      //handle box by kit packaging
      if (boxByKit) {
        tempOrderNumber = getTemporaryOrderNumber(orderNumber)
        DBAction query = database.table("MITALO").index("20").selection("MQITNO", "MQRIDL", "MQRIDX", "MQRFTX", "MQCAMU", "MQALQT", "MQBANO", "MQWHSL", "MQREPN").build() //Get delivery related information
        DBContainer container = query.getContainer()
        container.set("MQCONO", iCONO as Integer)
        if (orderCategory == 3) {
          container.set("MQTTYP", 31)
        } else if (orderCategory == 5) {
          container.set("MQTTYP", 51)
        } else if (orderCategory == 9) {
          container.set("MQTTYP", 92)
        }
        container.set("MQRIDN", orderNumber)
        container.set("MQRIDO", 0)
        container.set("MQRIDI", iDLIX as Long)
        container.set("MQWHLO", warehouse)
        container.set("MQPLSX", pickingLineSuffix as Integer)
        //read and output data
        query.readAll(container, 7,maxPageSize, getAllocationLinesBK)
      }

      //handle regular packaging
      if (!boxByLeague && !boxByTeam && !boxByKit) {
        DBAction query = database.table("MITALO").index("20").selection("MQITNO", "MQRIDL", "MQRIDX", "MQRFTX", "MQCAMU", "MQALQT", "MQBANO", "MQWHSL", "MQREPN").build() //Get delivery related information
        DBContainer container = query.getContainer()
        container.set("MQCONO", iCONO as Integer)
        if (orderCategory == 3) {
          container.set("MQTTYP", 31)
        } else if (orderCategory == 5) {
          container.set("MQTTYP", 51)
        } else if (orderCategory == 9) {
          container.set("MQTTYP", 92)
        }
        container.set("MQRIDN", orderNumber)
        container.set("MQRIDO", 0)
        container.set("MQRIDI", iDLIX as Long)
        container.set("MQWHLO", warehouse)
        container.set("MQPLSX", pickingLineSuffix as Integer)
        //read and output data
        query.readAll(container, 7,maxPageSize, getAllocationLines)
      }
    } else {
      return
    }
  }

  /**
   *Validate inputs
   * @params int iCONO, int DLIX
   * @return
   */
  private validateInput(int iCONO, long iDLIX) {
    //Validate Delivery Number
    Map<String, String> paramsDLIX = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
    Closure<?> callbackDLIX = {
      Map < String,
        String > response ->
        if (response.DLIX == null) {
          mi.error("Invalid delivery Number " + iDLIX)
          validInput = false
          return
        } else {
          orderNumber = response.RIDN
          warehouse = response.WHLO
          pickingLineSuffix = response.PLSX as Integer
          orderCategory = response.RORC as Integer
          deliveryMethod = response.MODF
          validInput = true
        }
    }
    miCaller.call("MWS410MI", "GetHead", paramsDLIX, callbackDLIX)
  }

  // Read MITALO records for box by league and team logic
  Closure<?> getAllocationLinesBLT = {
    DBContainer container ->
      String itno = container.get("MQITNO")
      int ridl = container.get("MQRIDL") as Integer
      String ridx = container.get("MQRIDX")
      String rftx = container.get("MQRFTX")
      String camu = container.get("MQCAMU")
      String alqt = container.get("MQALQT")
      String bano = container.get("MQBANO")
      String whsl = container.get("MQWHSL")
      String repn = container.get("MQREPN")
      String ridn = container.get("MQRIDN")

      if(itno!=null&&!itno.trim().isEmpty()) {
        if (orderLineNumber != ridl) {
          orderLineNumber = ridl
          HashMap <String, String> allocationLine = new HashMap <String, String> ()
          allocationLine.put("ITNO", itno)
          allocationLine.put("RIDN", ridn)
          allocationLine.put("RIDL", ridl as String)
          allocationLine.put("RIDX", ridx)
          allocationLine.put("RFTX", rftx)
          allocationLine.put("CAMU", camu)
          allocationLine.put("ALQT", alqt)
          allocationLine.put("BANO", bano)
          allocationLine.put("WHSL", whsl)
          allocationLine.put("REPN", repn)
          listRosterLinesBLT(allocationLine)
        }
      }
  }

  // Read MITALO records for box by kit logic
  Closure<?> getAllocationLinesBK = {
    DBContainer container ->
      String itno = container.get("MQITNO")
      int ridl = container.get("MQRIDL") as Integer
      String ridx = container.get("MQRIDX")
      String rftx = container.get("MQRFTX")
      String camu = container.get("MQCAMU")
      String alqt = container.get("MQALQT")
      String bano = container.get("MQBANO")
      String whsl = container.get("MQWHSL")
      String repn = container.get("MQREPN")
      String ridn = container.get("MQRIDN")
      if(itno!=null&&!itno.trim().isEmpty()) {
        HashMap <String, String> allocationLine = new HashMap <String, String> ()
        allocationLine.put("ITNO", itno)
        allocationLine.put("RIDN", ridn)
        allocationLine.put("RIDL", ridl as String)
        allocationLine.put("RIDX", ridx)
        allocationLine.put("RFTX", rftx)
        allocationLine.put("CAMU", camu)
        allocationLine.put("ALQT", alqt)
        allocationLine.put("BANO", bano)
        allocationLine.put("WHSL", whsl)
        allocationLine.put("REPN", repn)
        load_POnumber_groupindex(allocationLine)
      }
  }

  // Read MITALO records for regular packaging logic
  Closure<?> getAllocationLines = {
    DBContainer container ->
      String itno = container.get("MQITNO")
      String ridl = container.get("MQRIDL")
      String ridx = container.get("MQRIDX")
      String rftx = container.get("MQRFTX")
      String camu = container.get("MQCAMU")
      String alqt = container.get("MQALQT")
      String bano = container.get("MQBANO")
      String whsl = container.get("MQWHSL")
      String repn = container.get("MQREPN")
      String ridn = container.get("MQRIDN")
      if(itno!=null&&!itno.trim().isEmpty()) {
        HashMap <String, String> allocationLine = new HashMap <String, String> ()
        allocationLine.put("ITNO", itno)
        allocationLine.put("RIDN", ridn)
        allocationLine.put("RIDL", ridl)
        allocationLine.put("RIDX", ridx)
        allocationLine.put("RFTX", "")
        allocationLine.put("CAMU", camu)
        allocationLine.put("ALQT", alqt)
        allocationLine.put("BANO", bano)
        allocationLine.put("WHSL", whsl)
        allocationLine.put("REPN", repn)
        buildOutput(allocationLine)
      }
  }

  /**
   *Read through roster line records (EXTSTL) and update rftx and alqt valus of allocation line data
   * @params HashMap <String, String> allocationLine
   * @return
   */
  private void listRosterLinesBLT(HashMap <String, String> allocationLine) {
    Closure<?> getRosterLine = {
      DBContainer container ->
        String plid = container.get("EXPLID") as String
        if(plid!=null && !plid.trim().isEmpty()) {
          String rftx = getRFTXData(plid) // read rftx from EXTSTH
          String key = allocationLine.get("RIDN") + allocationLine.get("ITNO") + rftx
          HashMap <String, String> recordBLT = allocationLineBLT.get(key)
          if (recordBLT == null) {
            allocationLine.put("RFTX", rftx)
            allocationLineBLT.put(key, allocationLine)
          } else {
            double xxalqt = recordBLT.get("ALQT") as Double
            double zzalqt = allocationLine.get("ALQT") as Double
            zzalqt = zzalqt + xxalqt
            recordBLT.put("ALQT", zzalqt as String)
            allocationLineBLT.put(key, recordBLT)
          }
        }
    }
    DBAction query = database.table("EXTSTL").index("30").selection("EXPLID").build() //Get delivery related information
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO as Integer)
    container.set("EXDIVI", orderHead.get("OADIVI").trim())
    container.set("EXORNR", tempOrderNumber)
    container.set("EXPONR", allocationLine.get("RIDL") as Integer)
    query.readAll(container, 4, maxPageSize, getRosterLine)

  }

  /**
   *Read through roster line records (EXTSTL) and update rftx, alqt, grin, stge valus of allocation line data
   * @params HashMap <String, String> allocationLine
   * @return
   */
  private void load_POnumber_groupindex(HashMap <String, String> allocationLine) {
    boolean in99 = false
    String ponr = allocationLine.get("RIDL")
    String itno = allocationLine.get("ITNO")

    Closure<?> getRosterLine = {
      DBContainer container ->
        String plid = container.get("EXPLID") as String
        if(plid!=null && !plid.trim().isEmpty()) {
          allocationLine.put("RFTX", plid)
          allocationLine.put("ALQT", container.get("EXORQT") as String)
          groupIndex = ""
          stage = ""
          getRFTXData(plid)
          allocationLine.put("GRIN", groupIndex as String)
          allocationLine.put("STGE", stage as String)
          buildOutput(allocationLine)
        }
    }
    DBAction query = database.table("EXTSTL").index("30").selection("EXPLID", "EXORQT").build() //Get delivery related information
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO as Integer)
    container.set("EXDIVI", orderHead.get("OADIVI").trim())
    container.set("EXORNR", tempOrderNumber)
    container.set("EXPONR", allocationLine.get("RIDL") as Integer)
    container.set("EXPOSX", allocationLine.get("RIDX") as Integer)
    container.set("EXITNO", allocationLine.get("ITNO") as String)
    query.readAll(container, 6, maxPageSize, getRosterLine)
  }

  /**
   *Read EXTSTH using player id as key and return the RFTX value. Also, set group index and stage data.
   * @params String plid
   * @return String rftx
   */
  private String getRFTXData(String plid) {
    String rftx = ""
    DBAction query = database.table("EXTSTH").index("00").selection("EXLEAG", "EXTEAM", "EXUDF2", "EXPLNM").build() //Get delivery related information
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO as Integer)
    container.set("EXDIVI", orderHead.get("OADIVI").trim())
    container.set("EXPLID", plid)
    if (query.read(container)) {
      String plnm = container.get("EXPLNM") as String
      String leag = container.get("EXLEAG") as String
      String team = container.get("EXTEAM") as String
      if (boxByLeague) {
        rftx = leag
      }
      if (boxByTeam) {
        rftx = team
      }
      groupIndex = container.get("EXUDF2") as String
      stage = "1"
      if (boxByStg2) {
        plnm = plnm.toUpperCase().trim()
        team = team.toUpperCase().trim()
        leag = leag.toUpperCase().trim()
        if (plnm.equals("OTHER") && team.equals("OTHER") && leag.equals("OTHER") || plnm.equals("OTHER") && leag.equals("OTHER")) {
          stage = "2"
        }
      }
    }
    return rftx.trim()
  }

  /**
   *Build and output the API data with the allocation line input.
   * @params HashMap <String, String> allocationLine
   * @return
   */
  private void buildOutput(HashMap <String, String> allocationLine) {
    String itno = allocationLine.get("ITNO")
    String ridl = allocationLine.get("RIDL")
    String ridx = allocationLine.get("RIDX")
    String rftx = allocationLine.get("RFTX")
    String camu = allocationLine.get("CAMU")
    String alqt = allocationLine.get("ALQT")
    String bano = allocationLine.get("BANO")
    String whsl = allocationLine.get("WHSL")
    String repn = allocationLine.get("REPN")
    String ridn = allocationLine.get("RIDN")
    String stge = allocationLine.get("STGE")
    String grin = allocationLine.get("GRIN")

    if (stge != null) {
      mi.outData.put("STGE", stge as String)
    }
    if (grin != null) {
      mi.outData.put("GRIN", grin as String)
      mi.outData.put("PON2", grin as String)
    }
    //Start collecting data
    HashMap <String, String> itemData = getItemMasterData(itno)
    HashMap <String, String> orderLine = getOrderLineData(ridn, ridl as Integer, ridx as Integer)

    double lineOrqt = orderLine.get("OBORQT") as double
    double lineCharge = 0.0
    if (orderCategory == 3) {
      lineCharge = getLineCharge(ridn, ridl as Integer, ridx as Integer)
      lineCharge = lineCharge / lineOrqt
    }
    double netPrice = orderLine.get("OBNEPR") as double
    double linePrice = netPrice
    netPrice = (netPrice + lineCharge).round(4)

    if (isWUPPriceOverride || isDDPPriceOverride) {
      if (isDDPPriceOverride && isMCHEAD) {
        if (!costingType.isEmpty()) {
          double price = getCostPrice(zzFacility, itno, costingType)
          if (price == 0.0) {
            price = getCostPrice(zzFacility, itno, "1")
          }
          linePrice = price
        } else {
          linePrice = getCostPrice(zzFacility, itno, "1")
        }
      } else {
        linePrice = getBasePrice(zzPriceList, overrideCurrency, plFromDate, itno)
      }
    }

    camu = camu.trim()
    if (!camu.isEmpty()) {  //With LPN
      //productid
      mi.outData.put("PRID", itno as String)
      //puserstring1
      String ust1 = camu
      //if (Character.isDigit(camu.charAt(0))) {
      //  ust1 = camu.substring(6)
      //}
      mi.outData.put("UST1", ust1 as String)
      //quantity
      mi.outData.put("QTY1", alqt as String)
      //puserstring2
      mi.outData.put("UST2", netPrice as String)
      if (boxBySKU && (!boxByLeague && !boxByTeam && !boxByKit)) {
        indexBoxBySku = indexBoxBySku + 1
        mi.outData.put("RFTX", indexBoxBySku as String)
      } else {
        //reference text
        mi.outData.put("RFTX", rftx as String)
      }

      //build contents
      if (iBCNT.equalsIgnoreCase("y") || iBCNT.equalsIgnoreCase("z")) {
        buildContents(itemData, alqt as String, linePrice)
      }
      mi.write()
    } else { //No LPN
      String locationType = getLocationType(warehouse, itno, whsl, bano, camu, repn)
      //Special case logic
      double allocOrqt = alqt as Double
      double cfi2 = itemData.get("MMCFI2") as Double
      int chcd = itemData.get("MMCHCD") as Integer
      String tepy = orderHead.get("OATEPY") as String
      if ((lineOrqt == allocOrqt) && (allocOrqt >= cfi2) && (cfi2 > 0) && (chcd == 3) && (!tepy.equals("002") && !tepy.equals("003")) &&
        canWHLORunCase && (isWHLOTypeCase || (!isWHLOTypeCase && locationType.equals("20")))) {
        int full = (int) (allocOrqt / cfi2) //Get number of full case
        int part = (int) (allocOrqt % cfi2) //Get remaining quantity which is not full case

        for (int i = 0; i < full; i++) {   //Case quantity
          //productid
          mi.outData.put("PRID", itno as String)
          //puserstring1
          mi.outData.put("UST1", "CASE")
          //quantity
          mi.outData.put("QTY1", Integer.toString((int)cfi2))
          //puserstring2
          mi.outData.put("UST2", netPrice as String)

          if (boxBySKU && (!boxByLeague && !boxByTeam && !boxByKit)) {
            indexBoxBySku = indexBoxBySku + 1
            mi.outData.put("RFTX", indexBoxBySku as String)
          } else {
            //reference text
            mi.outData.put("RFTX", rftx as String)
          }

          //build contents
          if (iBCNT.equalsIgnoreCase("y") || iBCNT.equalsIgnoreCase("z")) {
            buildContents(itemData, cfi2 as String, linePrice)
          }
          mi.write()
        }

        if (part > 0) {   //Remaining quantity which is not a case quantity
          //productid
          mi.outData.put("PRID", itno as String)
          //puserstring1
          mi.outData.put("UST1", "")
          //quantity
          mi.outData.put("QTY1", Integer.toString(part))
          //puserstring2
          mi.outData.put("UST2", netPrice as String)

          if (boxBySKU && (!boxByLeague && !boxByTeam && !boxByKit)) {
            indexBoxBySku = indexBoxBySku + 1
            mi.outData.put("RFTX", indexBoxBySku as String)
          } else {
            //reference text
            mi.outData.put("RFTX", rftx as String)
          }

          //build contents
          if (iBCNT.equalsIgnoreCase("y") || iBCNT.equalsIgnoreCase("z")) {
            buildContents(itemData, part as String, linePrice)
          }
          mi.write()
        }
      } else {
        //productid
        mi.outData.put("PRID", itno as String)
        //puserstring1
        mi.outData.put("UST1", "")
        //quantity
        mi.outData.put("QTY1", alqt as String)
        //puserstring2
        mi.outData.put("UST2", netPrice as String)

        if (boxBySKU && (!boxByLeague && !boxByTeam && !boxByKit)) {
          indexBoxBySku = indexBoxBySku + 1
          mi.outData.put("RFTX", indexBoxBySku as String)
        } else {
          //reference text
          mi.outData.put("RFTX", rftx as String)
        }

        //build contents
        if (iBCNT.equalsIgnoreCase("y") || iBCNT.equalsIgnoreCase("z")) {
          buildContents(itemData, alqt as String, linePrice)
        }
        mi.write()
      }
    }
  }

  /**
   *Read item master data (MITMAS) to get MMITDS, MMITNO, MMCFI2, MMCHCD, and MMUNMS.
   * @params String itemNumber
   * @return HashMap <String, String>
   */
  private HashMap <String, String> getItemMasterData (String itemNumber) {
    HashMap <String, String> itemMaster = new HashMap <String, String> ()
    DBAction query = database.table("MITMAS").index("00").selection("MMITNO", "MMITDS", "MMCFI2", "MMCHCD", "MMUNMS").build()
    DBContainer container = query.getContainer()
    container.set("MMCONO", iCONO)
    container.set("MMITNO", itemNumber)
    if (query.read(container)) {
      itemMaster.put("MMITDS", container.get("MMITDS") as String)
      itemMaster.put("MMITNO", container.get("MMITNO") as String)
      itemMaster.put("MMCFI2", container.get("MMCFI2") as String)
      itemMaster.put("MMCHCD", container.get("MMCHCD") as String)
      itemMaster.put("MMUNMS", container.get("MMUNMS") as String)
      itemMaster.put("MMITNO", container.get("MMITNO") as String)
    }
    return itemMaster
  }

  /**
   *Read order header data and return them in a hash map
   * @params String ordNumber
   * @return HashMap <String, String>
   */
  private HashMap <String, String> getOrderHeadData(String ordNumber) {
    HashMap <String, String> orderHead = new HashMap <String, String> ()
    if (orderCategory == 3) {
      DBAction query = database.table("OOHEAD").index("00").selection("OAORNO", "OATEPY", "OACUNO", "OAORTP", "OACUOR","OADIVI", "OATEDL", "OACUCD").build()
      DBContainer container = query.getContainer()
      container.set("OACONO", iCONO)
      container.set("OAORNO", ordNumber)
      if (query.read(container)) {
        orderHead.put("OAORNO", container.get("OAORNO") as String)
        orderHead.put("OATEPY", container.get("OATEPY") as String)
        orderHead.put("OACUNO", container.get("OACUNO") as String)
        orderHead.put("OAORTP", container.get("OAORTP") as String)
        orderHead.put("OACUOR", container.get("OACUOR") as String)
        orderHead.put("OADIVI", container.get("OADIVI") as String)
        orderHead.put("OATEDL", container.get("OATEDL") as String)
        orderHead.put("OACUCD", container.get("OACUCD") as String)
      }
    } else if (orderCategory == 5 || orderCategory == 9) {
      DBAction query = database.table("MGHEAD").index("00").selection("MGTRNR", "MGRESP", "MGTRTP", "MGTEDL").build()
      DBContainer container = query.getContainer()
      container.set("MGCONO", iCONO)
      container.set("MGTRNR", ordNumber)
      if (query.read(container)) {
        orderHead.put("OAORNO", container.get("MGTRNR") as String)
        orderHead.put("OATEPY", "")
        orderHead.put("OACUNO", container.get("MGRESP") as String)
        orderHead.put("OAORTP", container.get("MGTRTP") as String)
        orderHead.put("OACUOR", container.get("MGTRNR") as String)
        orderHead.put("OADIVI", getTranslation("FRTDODIVI"))
        orderHead.put("OATEDL", container.get("MGTEDL") as String)
        orderHead.put("OACUCD", getTranslation("FRTDOCUCD"))
      }
    }
    return orderHead
  }

  /**
   *Read order line data and return them in a hash map
   * @params String ordNumber, int lineNumber, int lineSuffix
   * @return HashMap <String, String>
   */
  private HashMap <String, String> getOrderLineData(String ordNumber, int lineNumber, int lineSuffix) {
    HashMap <String, String> orderLine = new HashMap <String, String> ()
    if (orderCategory == 3) {
      DBAction query = database.table("OOLINE").index("00").selection("OBORNO", "OBPONR", "OBPOSX", "OBNEPR", "OBORQT").build()
      DBContainer container = query.getContainer()
      container.set("OBCONO", iCONO)
      container.set("OBORNO", ordNumber)
      container.set("OBPONR", lineNumber as Integer)
      container.set("OBPOSX", lineSuffix as Integer)
      if (query.read(container)) {
        orderLine.put("OBORNO", container.get("OBORNO") as String)
        orderLine.put("OBPONR", container.get("OBPONR") as String)
        orderLine.put("OBPOSX", container.get("OBPOSX") as String)
        orderLine.put("OBNEPR", container.get("OBNEPR") as String)
        orderLine.put("OBORQT", container.get("OBORQT") as String)
      }
    } else if (orderCategory == 5 || orderCategory == 9) {
      DBAction query = database.table("MGLINE").index("00").selection("MRTRNR", "MRPONR", "MRPOSX", "MRTRPR", "MRTRQT").build()
      DBContainer container = query.getContainer()
      container.set("MRCONO", iCONO)
      container.set("MRTRNR", ordNumber)
      container.set("MRPONR", lineNumber as Integer)
      container.set("MRPOSX", lineSuffix as Integer)
      if (query.read(container)) {
        orderLine.put("OBORNO", container.get("MRTRNR") as String)
        orderLine.put("OBPONR", container.get("MRPONR") as String)
        orderLine.put("OBPOSX", container.get("MRPOSX") as String)
        orderLine.put("OBNEPR", container.get("MRTRPR") as String)
        orderLine.put("OBORQT", container.get("MRTRQT") as String)
      }
    }
    return orderLine
  }

  /**
   *Read style data (MITMAH) and return the style number
   * @params String itemNumber
   * @return String
   */
  private String getStyleNumber(String itemNumber) {
    String styleNumber = null
    DBAction query = database.table("MITMAH").index("00").selection("HMSTYN").build()
    DBContainer container = query.getContainer()
    container.set("HMCONO", iCONO)
    container.set("HMITNO", itemNumber)
    if (query.read(container)) {
      styleNumber = container.get("HMSTYN")
    }
    return styleNumber
  }

  /**
   *Read style master info data (MMODMA) and return hash map containing data HHFM15, and HHFM16
   * @params String itemNumber
   * @return HashMap <String, String>
   */
  private HashMap <String, String> getStyleMasterData(String itemNumber) {
    HashMap <String, String> styleMasterInfo = new HashMap <String, String> ()
    String styleNumber = getStyleNumber(itemNumber)
    if (styleNumber != null) {
      DBAction query = database.table("MMODMA").index("00").selection("HHFM15", "HHFM16").build()
      DBContainer container = query.getContainer()
      container.set("HHCONO", iCONO)
      container.set("HHSTYN", styleNumber)
      if (query.read(container)) {
        styleMasterInfo.put("HHFM15", container.get("HHFM15") as String)
        styleMasterInfo.put("HHFM16", container.get("HHFM16") as String)
      }
    }
    return styleMasterInfo
  }

  /**
   *Read item location data (MITLOC) and return location type (MLWHLT) data
   * @params String whlo, String itno, String whsl, String bano, String camu, String repn
   * @return String
   */
  private String getLocationType(String whlo, String itno, String whsl, String bano, String camu, String repn) {
    String locationType = null
    DBAction query = database.table("MITLOC").index("00").selection("MLWHLT").build()
    DBContainer container = query.getContainer()
    container.set("MLCONO", iCONO)
    container.set("MLWHLO", whlo)
    container.set("MLITNO", itno)
    container.set("MLWHSL", whsl)
    container.set("MLBANO", bano)
    container.set("MLCAMU", camu)
    container.set("MLREPN", repn as Long)
    if (query.read(container)) {
      locationType = container.get("MLWHLT")
    }
    return locationType
  }

  /**
   * buildContents - set the extra output data for international deliveries
   * @params HashMap <String, String> itemData, String qty, double netPrice
   * @return void
   */
  private void buildContents(HashMap <String, String> itemData, String qty, double netPrice) {
    HashMap <String, String> styleMasterInfo = getStyleMasterData(itemData.get("MMITNO"))
    String htsovrcde = getTranslation("HTSOVRCDE")
    String hhfm15 = styleMasterInfo.get("HHFM15")
    String hhfm16 = styleMasterInfo.get("HHFM16")
    boolean intldns = false
    //commodityitemcode
    if (hhfm15 == null || hhfm15.trim().isEmpty()) {
      mi.outData.put("CITC", htsovrcde.substring(0, htsovrcde.length() - 2))
      intldns = true
    } else {
      mi.outData.put("CITC", hhfm15)
    }
    //description
    String description = itemData.get("MMITNO") + " " + itemData.get("MMITDS")
    description = description.replace("'", "''")
    description = description.replace("&", "and")
    description = description.replace("<", "lt")
    description = description.replace(">", "gt")
    description = description.replace("#", "")
    description = description.replace("/", "")
    mi.outData.put("TX60", description)
    //customsvalue
    if (netPrice < 0.01) {
      mi.outData.put("CUVA", "0.01")
    } else {
      mi.outData.put("CUVA", netPrice as String)
    }
    //quantity 2
    mi.outData.put("QTY2", qty as String)
    //unit of measure
    mi.outData.put("UNMS", itemData.get("MMUNMS"))
    //producercountry
    if (hhfm16 == null || hhfm16.trim().isEmpty()) {
      mi.outData.put("PRCY", htsovrcde.substring(htsovrcde.length() - 2))
      intldns = true
    } else {
      mi.outData.put("PRCY", hhfm16)
    }
    //eccn number
    mi.outData.put("ECCN", getTranslation("ECCN"))
    //origin criterion
    mi.outData.put("OCRT", "")
    //Do not ship
    if (intldns) {
      mi.outData.put("IDNS", "Y")
    } else {
      mi.outData.put("IDNS", "N")
    }

  }

  /**
   * getLineCharge - Get the total line charge data
   * @params String orno, int ponr, int pos
   * @return double
   */
  private double getLineCharge(String orno, int ponr, int posx) {
    double lineCharge = 0.0
    //Validate Delivery Number
    def paramsLstLineCharge = ["CONO": iCONO.toString().trim(), "ORNO": orno.toString().trim(), "PONR": ponr.toString().trim(), "POSX": posx.toString().trim()]
    def callbackLstLineCharge = {
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
   * getTranslation - Get the CRS881 data translation using a String keyword
   * @params String keyword
   * @return String
   */
  private String getTranslation(String keyword) {
    if (translationId == 0) {
      def params = ["CONO": iCONO.toString().trim(),"DIVI":"","TRQF":"0","MSTD":"PREPSHIP","MVRS":"1","BMSG":"PROPERTY","IBOB":"O","ELMP":"PROPERTY","ELMD":"Properties"]
      def callback = {
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
      Closure<?> getTranslatedData = {
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
      container.set("TDMVXD",keyword)
      query.readAll(container, 4, maxPageSize, getTranslatedData)
    }
    return mbmd.trim()
  }

  /**
   * Retrieve the delivery address data of the order
   * @params String deliveryNumber
   * @return HashMap <String, String>
   */
  private HashMap <String, String> getDeliveryAddress(String deliveryNumber) {
    HashMap <String, String> addressData = new HashMap <String, String> ()
    def params = ["CONO": iCONO.toString().trim(),"DLIX":deliveryNumber,"ADRT":"11"]
    def callback = {
      Map < String,
        String > response ->
        if (response.CONA != null) {
          addressData.put("CUA1", response.CUA1)
          addressData.put("PONO", response.PONO)
          addressData.put("ECAR", response.ECAR)
          addressData.put("CSCD", response.CSCD)
        }
    }
    miCaller.call("MWS410MI", "GetAdr", params, callback)
    return addressData
  }

  /**
   * Process header, address, roster data to check if shipment should be packaged in a special boxing logic
   * @params HashMap <String, String> orderHead, HashMap <String, String> addressData
   * @return void
   */
  private void specialBoxingCheck(HashMap <String, String> orderHead, HashMap <String, String> addressData) {
    String cuno = orderHead.get("OACUNO")
    String ortp = orderHead.get("OAORTP")
    String cua1 = addressData.get("CUA1")
    String pono = addressData.get("PONO")
    String ecar = addressData.get("ECAR")
    if (cua1 == null || cua1.trim().isEmpty()) {
      cua1 = "XXXXX"
    } else if (cua1.length() >= 5) {
      cua1 = cua1.substring(0, 5).trim()
    }

    if (pono == null || pono.trim().isEmpty()) {
      pono = "XXXXX"
    } else if (pono.length() >= 5) {
      pono = pono.substring(0, 5).trim()
    }
    ecar = ecar == null ? "": ecar.trim()
    String RCXCHK_MVX = cuno.trim() + cua1 + pono + ecar
    boxBySKU = (getTranslation("BBS" + cuno.trim()).equalsIgnoreCase("y")) || (getTranslation("BBS" + RCXCHK_MVX).equalsIgnoreCase("y"))
    if (boxBySKU) {
      indexBoxBySku = 0
    }

    // Omit special boxing per warehouse
    boolean omitSpecialBoxing = (getTranslation("OSB" + warehouse.trim()).equalsIgnoreCase("y"))
    if (!omitSpecialBoxing) {
      // Box by league Check - Order Type Level
      ortp = ortp == null ? "": ortp.trim()
      boxByLeague = (getTranslation("BBL" + ortp).equalsIgnoreCase("y"))
      // Box by kit Check
      String cuor = orderHead.get("OACUOR") == null ? "": orderHead.get("OACUOR").trim()
      //FSCustChk
      boolean fSCust = false
      int index = cuor.indexOf("-")
      if (index != -1) {
        index = index + 1
        cuor = cuor.substring(0, index).trim()
        fSCust = (getTranslation("FSPC" + cuno.trim() + "|" + cuor + "|" + warehouse.trim()).equalsIgnoreCase("y"))
        if (fSCust) {
          boxByStg2 = (getTranslation("BBT" + cuno.trim()).equalsIgnoreCase("y"))
          boxByKit = (getTranslation("BBK" + warehouse.trim() + "|" + cuno.trim()).equalsIgnoreCase("y"))
          index = 0
          String team = ""

          //Update UDF2 data in EXTSTH table
          Closure<?> updateUDF2CallBack = { LockedResult lockedResult ->
            if (index == 0) {
              index = index + 1
              lockedResult.set("EXUDF2", index as String)
              lockedResult.set("EXLMDT", currentDate)
              int chno = lockedResult.get("EXCHNO") as Integer
              chno = chno + 1
              lockedResult.set("EXCHNO", chno)
              lockedResult.set("EXCHID", program.getUser())
              lockedResult.update()
              team = lockedResult.get("EXTEAM").toString().trim()
            } else {
              if (team.equals(lockedResult.get("EXTEAM").toString().trim())) {
                lockedResult.set("EXUDF2", index as String)
                lockedResult.set("EXLMDT", currentDate)
                int chno = lockedResult.get("EXCHNO") as Integer
                chno = chno + 1
                lockedResult.set("EXCHNO", chno)
                lockedResult.set("EXCHID", program.getUser())
                lockedResult.update()
              } else {
                index = index + 1
                lockedResult.set("EXUDF2", index as String)
                lockedResult.set("EXLMDT", currentDate)
                int chno = lockedResult.get("EXCHNO") as Integer
                chno = chno + 1
                lockedResult.set("EXCHNO", chno)
                lockedResult.set("EXCHID", program.getUser())
                lockedResult.update()
                team = lockedResult.get("EXTEAM").toString().trim()
              }
            }
          }
          DBAction query = database.table("EXTSTH").index("13").selection("EXTEAM","EXCHNO").build()
          DBContainer container = query.getContainer()
          container.set("EXCONO", iCONO)
          container.set("EXDIVI", orderHead.get("OADIVI").trim())
          container.set("EXORNO", orderHead.get("OAORNO").trim())
          query.readAllLock(container, 3, updateUDF2CallBack)
        }

        // Box by team Check
        boxByTeam = false
        if (!boxByKit) {
          Closure<?> getTeamData = {
            DBContainer container ->
              String grby = container.get("EXGRBY").toString().trim()
              if (grby != null && grby.equalsIgnoreCase("team")) {
                boxByTeam = true
              }
          }
          DBAction query = database.table("EXTSTH").index("11").selection("EXGRBY").build()
          DBContainer container = query.getContainer()
          container.set("EXCONO", iCONO)
          container.set("EXDIVI", orderHead.get("OADIVI").trim())
          container.set("EXORNO", orderHead.get("OAORNO").trim())
          query.readAll(container, 3, maxPageSize, getTeamData)
        }
      }
    }
  }

  /**
   *Get Temporary Order Number associated to a final Order Number
   * @params String RIDN
   * @return String
   */
  private String getTemporaryOrderNumber(String RIDN) {
    String orno = ""
    def params = ["CONO": iCONO.toString().trim(),"ORNO":RIDN.toString().trim()]
    def callback = {
      Map < String,
        String > response ->
        if (response.ORNO != null) {
          orno = response.ORNO
        }
    }
    miCaller.call("OIS275MI", "GetTmpOrderStat", params, callback)
    return orno
  }

  /**
   *Get costing price from MCHEAD
   * @params String facility, String itemNumber, String costType
   * @return double
   */
  private double getCostPrice(String facility, String itemNumber, String costType) {
    double costPrice = 0.0
    Closure<?> getCosting = {
      DBContainer container ->
        int costDate = container.get("KOPCDT") as Integer
        if (costDate > 0 && currentDate >= costDate) {
          costPrice = container.get("KOCSU1") as Double
        }
    }
    DBAction query = database.table("MCHEAD").index("00").selection("KOPCDT", "KOCSU1").build()
    DBContainer container = query.getContainer()
    container.set("KOCONO", iCONO)
    container.set("KOFACI", facility)
    container.set("KOITNO", itemNumber)
    container.set("KOSTRT", "")
    container.set("KOVASE", "")
    container.set("KOCROC", 0)
    container.set("KORORN", "")
    container.set("KORORL", 0)
    container.set("KORORX", 0)
    container.set("KOECVS", 0)
    container.set("KOSMFN", "")
    container.set("KOPCTP", costType)
    query.readAll(container, 12, maxPageSize, getCosting)
    return costPrice
  }
  
  /**
  *Get the price list date for the price list and Currency
  *@params PriceList (String), Currency(Stiring)
  *@return int
  */
  private int getPriceListDate(String priceList, String currency) {
    int fromDate = 0
    Closure<?> getPriceList = {
      DBContainer container ->
        int fvDate = container.get("OJFVDT")
        if (fvDate > 0 && currentDate >= fvDate) {
          fromDate = fvDate
        }
    }
    DBAction query = database.table("OPRICH").index("00").selection("OJFVDT").build()
    DBContainer container = query.getContainer()
    container.set("OJCONO", iCONO)
    container.set("OJPRRF", priceList)
    container.set("OJCUCD", currency)
    container.set("OJCUNO", "")
    query.readAll(container, 4, maxPageSize, getPriceList)
    return fromDate
  }

  /**
   *Get base sales price using OIS017MI.GetBasePrice call
   * @params String priceList, String currency, int fromDate, String itemNumber
   * @return double
   */
  private double getBasePrice(String priceList, String currency, int fromDate, String itemNumber) {
    double basePrice = 0.0
    def params = ["CONO": iCONO.toString().trim(),"PRRF": priceList,"CUNO": "","CUCD":currency,"FVDT": fromDate as String,"ITNO": itemNumber]
    def callback = {
      Map < String,
        String > response ->
        if (response.SAPR != null) {
          basePrice = response.SAPR as Double
        }
    }
    miCaller.call("OIS017MI", "GetBasePrice", params, callback)
    return basePrice
  }

  /**
   *Get Facility data associated for a warehouse
   * @params String whlo
   * @return String
   */
  private String getFacility(String whlo) {
    String facility = ""
    DBAction query = database.table("MITWHL").index("00").selection("MWWHLO", "MWFACI").build()
    DBContainer container = query.getContainer()
    container.set("MWCONO", iCONO)
    container.set("MWWHLO", whlo)
    if (query.read(container)) {
      facility = container.get("MWFACI")
    }
    return facility
  }
}
