/**
 * README
 * This extension is being used to Add order charges to customer order based on data from globalship
 *
 * Name: EXT103MI.AddOrderCharges
 * Description: Transaction used to Add order charges to customer order based on data from globalship
 * Date	      Changed By                      Description
 *20231019  SuriyaN@fortude.co     Add order charges to customer order based on data from globalship */

import java.util.HashMap
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

public class AddOrderCharges extends ExtendM3Transaction {
  private final MIAPI mi
  private final MICallerAPI miCaller
  private final DatabaseAPI database
  private final ProgramAPI program
  private final IonAPI ion
  private final LoggerAPI logger

  private int iCONO, pageSize=1000
  private String iORNO, TEPY, shipDate, codeFlag, unloadingZone
  private HashMap < String, String > warehouses = new HashMap < String, String > ();
  private boolean validInput = true

  public AddOrderCharges(MIAPI mi, MICallerAPI miCaller, ProgramAPI program, IonAPI ion, LoggerAPI logger, DatabaseAPI database) {
    this.mi = mi
    this.miCaller = miCaller
    this.program = program
    this.ion = ion
    this.logger = logger
    this.database = database
  }

  public void main() {
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")

    validateInput()
    if (validInput) {

      getShipDate()
      getUnloadingZone()
      getAllWarehouses()

      Iterator < Map.Entry < String, String >> iterator = warehouses.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry < String, String > entry = iterator.next();
        String warehouse = entry.getKey();
        String deliveryNumber = entry.getValue();
        AddWarehouseCharges(warehouse, deliveryNumber)
      }
    }

  }

  /**
   *Validate Records
   * @params
   * @return 
   */

  public validateInput() {

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

    //Validate Order Number

   params = ["ORNO":iORNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->

      if (response.ORNO == null) {
        mi.error("Invalid Order Number " + iORNO)
        validInput = false
        return 
      } else {
        TEPY = response.TEPY
        if (TEPY != null && (TEPY.trim().equals("002") || TEPY.trim().equals("003"))) {
          codeFlag = "Y"
        } else {
          codeFlag = "N"
        }
      }
    }
    miCaller.call("OIS100MI", "GetOrderHead", params, callback)


  }

  /**
   *Get unloading zone of the order
   * @params
   * @return
   */
  public getUnloadingZone() {
    Map<String, String> params = ["CONO": iCONO.toString().trim(), "ORNO": iORNO.toString().trim(), "ADRT": "1"]
    Closure<?> callback = {
      Map < String,
      String > response ->
      if (response.ULZO != null) {
        unloadingZone = response.ULSO
      }
    }
    miCaller.call("OIS100MI", "GetAddress", params, callback)
  }

  /**
   *Get all unique warehouses of the delivery
   * @params
   * @return
   */
  public getAllWarehouses() {
    ExpressionFactory expression = database.getExpressionFactory("MHDISH")
    expression = expression.eq("OQRIDN", iORNO)
    DBAction query = database.table("MHDISH").selection("OQDLIX", "OQWHLO").matching(expression).index("00").build()
    DBContainer container = query.getContainer()
    container.set("OQCONO", iCONO)
    query.readAll(container, 1, pageSize, resultset)
  }

  Closure < ? > resultset = {
    DBContainer container ->
    String warehouse = container.get("OQWHLO")
    String deliveryNumber = container.get("OQDLIX")
    if (!warehouses.containsKey(warehouse)) {
      warehouses.put(warehouse, deliveryNumber)
    }
  }

  /**
   *GetShipDate
   * @params
   * @return 
   */
  public getShipDate() {
    LocalDate today = LocalDate.now();
    // Check if today is Saturday (DayOfWeek.SATURDAY)
    if (today.getDayOfWeek() == DayOfWeek.SATURDAY) {
      // If it's Saturday, adjust to the previous day (Friday)
      today = today.minusDays(1);
    }
    // Format the date as "yyyy-MM-dd"
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    shipDate = today.format(formatter);
  }

  /**
   *getParameter
   * @params shipvia
   * @return parameter
   */
  public String getpaymentType(shipvia) {
    String parameter = ""
    DBAction query = database.table("CSYTAB").selection("CTPARM").index("00").build()
    DBContainer container = query.getContainer()
    container.set("CTCONO", iCONO)
    container.set("CTDIVI", "")
    container.set("CTSTCO", "MODL")
    container.set("CTLNCD", "GB")
    container.set("CTSTKY", shipvia)
    if (query.read(container)) {
      parameter = container.get("CTPARM").toString()
    }

    if (parameter.trim().equals("2")) {
      return "COLLECT"
    } else if (parameter.trim().equals("3")) {
      return "3RDPARTY"
    } else if (TEPY.trim().equals("002") || TEPY.trim().equals("003")) {
      return "COLLECT"
    } else {
      return "PREPAID"
    }
  }
  
  /**
   *getSaturdayDelivery
   * @params MODL
   * @return string
   */
  public String getSaturdayDelivery(MODL) {
    //Get Saturday Delivery
    String saturdayDelivery = "N"
    Map<String, String> params = ["CONO": iCONO.toString().trim(),"DIVI":"", "TRQF": "0", "MSTD": "FRTUTIL", "MVRS": "1", "BMSG": "PROPERTY", "IBOB": "O", "ELMP": "PROPERTY", "ELMD": "Properties", "MVXD": "SAT" + MODL]
    Closure<?> callback = {
      Map < String,
      String > response ->
      if (response.MBMD != null) {
        saturdayDelivery = "Y"
      } else {
        saturdayDelivery = "N"
      }
    }
    miCaller.call("CRS881MI", "GetTranslData", params, callback)
     return saturdayDelivery
  }
  
  /**
   *getChargeCode
   * @params WHLO
   * @return string
   */
  public String getChargeCode(WHLO) {
    //Get Saturday Delivery
    String chargeCode = ""
    Map<String, String> params = ["CONO": iCONO.toString().trim(),"DIVI":"", "TRQF": "0", "MSTD": "FRTUTIL", "MVRS": "1", "BMSG": "PROPERTY", "IBOB": "O", "ELMP": "PROPERTY", "ELMD": "Properties", "MVXD": "FRT" + WHLO]
    Closure<?> callback = {
      Map < String,
      String > response ->
      if (response.MBMD != null) {
       chargeCode = response.MBMD
      } 
    }
    miCaller.call("CRS881MI", "GetTranslData", params, callback)
     return chargeCode
    
  }

  /**
   *Add Warehouse Charges
   * @params warehouse, deliveryNumber
   * @return
   */
  public AddWarehouseCharges(String warehouse,String deliveryNumber) {
    
    //Declare Variables
    String shipvia, name, ADR1, ADR2, TOWN, ECAR, PONO, CSCD, MODL, saturdayDelivery, paymentType,chargeCode, contentBody = ""
    Double ntam = 0
    
    
    //Get Delivery Details
    Map<String, String> params = ["CONO": iCONO.toString().trim(), "DLIX": deliveryNumber.toString().trim()]
    Closure<?> callback = {
      Map < String,
      String > response ->
      MODL = response.MODL
      shipvia = response.MODF
      if (shipvia == null || shipvia.trim().isEmpty()) {
        shipvia = MODL
      }
    }
    miCaller.call("MWS410MI", "GetHead", params, callback)
    
    
    paymentType = getpaymentType(shipvia)
    saturdayDelivery = getSaturdayDelivery(MODL)
    chargeCode = getChargeCode(warehouse)
    
    //Get Address Details
    params = ["CONO": iCONO.toString().trim(), "DLIX": deliveryNumber.toString().trim(), "ADRT": "11"]
    callback = {
      Map < String,
      String > response ->
      name = response.NAME
      ADR1 = response.ADR1
      ADR2 = response.ADR2
      TOWN = response.TOWN
      ECAR = response.ECAR
      PONO = response.PONO
      CSCD = response.CSCD
    }
    miCaller.call("MWS410MI", "GetAdr", params, callback)

    //List Order Lines 
    params = ["CONO": iCONO.toString().trim(), "ORNO": iORNO.trim()]
    callback = {
      Map < String,
      String > response ->
      String apiWHLO = response.WHLO
      if (apiWHLO == null) apiWHLO = ""
      if (apiWHLO.trim().equals(warehouse)) {
        contentBody = contentBody + "<content>\n" +
          "   <commodityitemcode>" + response.ITNO.trim() + "</commodityitemcode>\n" +
          "   <quantity>" + response.ORQT.trim() + "</quantity>\n" +
          "</content>"
        ntam = ntam + Double.parseDouble(response.NLAM)
      }
    }
    miCaller.call("OIS100MI", "LstLine", params, callback)
    
    if(shipvia==null) shipvia = ""
    if(codeFlag==null) codeFlag = ""
    if(unloadingZone==null) unloadingZone = ""
    if(saturdayDelivery==null) saturdayDelivery = ""
    if(name==null) name = ""
    if(ADR1==null) ADR1 = ""
    if(ADR2==null) ADR2 = ""
    if(TOWN==null) TOWN = ""
    if(ECAR==null) ECAR = ""
    if(PONO==null) PONO = ""
    if(CSCD==null) CSCD = ""
    if(contentBody==null) contentBody = ""
    if(iORNO==null) iORNO = ""
    
    String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<shipment>\n" +
      "   <header>\n" +
      "      <shippingprofile>" + warehouse.trim() + "</shippingprofile>\n" +
      "      <ordernumber>" + iORNO.trim() + "</ordernumber>\n" +
      "      <shipvia>" + shipvia.trim() + "</shipvia>\n" +
      "      <shipdate>" + shipDate.trim() + "</shipdate>\n" +
      "      <shipaction>RATE</shipaction>\n" +
      "      <paymenttype>PREPAID</paymenttype>\n" +
      "      <codflag>" + codeFlag.trim() + "</codflag>\n" +
      "      <addresstype>" + unloadingZone.trim() + "</addresstype>\n" +
      "      <saturdaydelivery>" + saturdayDelivery.trim() + "</saturdaydelivery>\n" +
      "      <address type=\"shipto\">\n" +
      "         <company>" + name.trim() + "</company>\n" +
      "         <attention>" + name.trim() + "</attention>\n" +
      "         <address1>" + ADR1.trim() + " " + ADR2.trim() + "</address1>\n" +
      "         <city>" + TOWN.trim() + "</city>\n" +
      "         <state>" + ECAR.trim() + "</state>\n" +
      "         <zipcode>" + PONO.trim() + "</zipcode>\n" +
      "         <country>" + CSCD.trim() + "</country>\n" +
      "      </address>\n" +
      "   </header>\n" +
      "   <contents>\n" +
      contentBody.trim() + "\n" +
      "   </contents>\n" +
      "   <declaredValue>" + ntam + "</declaredValue>\n" +
      "   <loads>\n" +
      "      <load loadid=\"" + iORNO.trim() + "\" containerselectionrule=\"2\" />\n" +
      "   </loads>\n" +
      "</shipment>"

    def endpoint = "/CustomerApi/GlobalShip/api/api/LogicorIntegration"
    def headers = ["Accept": "application/xml", "Content-Type": "application/xml"]
    logger.debug(xmlString)
    def queryParameters = (Map) null // define as map if there are any query parameters e.g. ["name1": "value1", "name2": "value2"]
    IonResponse response = ion.post(endpoint, headers, queryParameters, xmlString)
    String content = response.getContent()
    logger.debug("CONTENT: " + content)
    if (response.getError()) {
      mi.error("Failed calling ION API, detailed error message: ${response.getErrorMessage()}")
      return
    }
    if (response.getStatusCode() != 200) {
      mi.error("Expected status 200 but got ${response.getStatusCode()} instead")
      return
    }
    if (content == null) {
     mi.error("Expected content from the request but got no content")
      return
    }
    
    String startTag = "&lt;shiprate&gt;";
    String endTag = "lt;/shiprate&gt;";
    int startIndex = content.indexOf(startTag);
    int endIndex = content.indexOf(endTag, startIndex);
    String shiprateValue = "0"
    if (startIndex != -1 && endIndex != -1) {
      // Extract the value between the opening and closing tags
      shiprateValue = content.substring(startIndex + startTag.length(), endIndex-1);
    } else {
      mi.error("Unable to fetch charges from GlobalShip")
      return
    }
    logger.debug("chargeCode: " +chargeCode+" CRAM: "+shiprateValue)
   //Add Order Charges
    params = ["CONO": iCONO.toString().trim(), "ORNO": iORNO.toString().trim(), "CRID": chargeCode.trim(), "CRAM": shiprateValue.trim()]
    callback = {
      Map < String,
      String > response1 ->
      mi.error("Error from OIS100: "+response1)
    }
    miCaller.call("OIS100MI", "AddConnCOCharge", params, callback)
  }
}