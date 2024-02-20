/**
 * README
 * This extension is being used to get the carton sequence Number from M3
 *
 * Name: EXT004MI.GetAllocData
 * Description:  Get the allocation data from M3
 * Date	      Changed By                 Description
 *20230717  SuriyaN@fortude.co     Get the allocation data from M3
 *20240212  AbhishekA@fortude.co   Updating Validation logic
 */



public class GetAllocData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iITNO, iRIDN, iCAMU
  private int iCONO, count
  private boolean validInput = true
  private long iDLIX

  public GetAllocData(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("RIDI") == null || mi.inData.get("RIDI").trim().isEmpty()) ? 0L : mi.inData.get("RIDI") as Long
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO") as String
    iRIDN = (mi.inData.get("RIDN") == null || mi.inData.get("RIDN").trim().isEmpty()) ? "" : mi.inData.get("RIDN") as String
    iCAMU = (mi.inData.get("CAMU") == null || mi.inData.get("CAMU").trim().isEmpty()) ? "" : mi.inData.get("CAMU") as String
    validateInput()
    if(validInput)
    {
      setOutput()
    }
    
  }

  /**
   *Validate Records
   * @params
   * @return
   */
  public void validateInput() {

    //Validate Company Number
    Map < String, String > params = ["CONO": iCONO.toString().trim()]
    Closure < ? > callbackCONO = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callbackCONO)

    Map < String, String > paramsDLIX = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
    Closure < ? > callbackDLIX = {
      Map < String,
        String > response ->
        if (response.DLIX == null) {
          mi.error("Invalid delivery Number " + iDLIX)
          validInput = false
          return 
        } 
    }
    miCaller.call("MWS410MI", "GetHead", paramsDLIX, callbackDLIX)
    
    //Validate Item Number
    params = ["CONO": iCONO.toString().trim(),"ITNO":iITNO.toString().trim()]
    Closure < ? > callbackITNO = {
      Map < String,
        String > response ->
        if (response.ITNO == null) {
          mi.error("Invalid Item Number " + iITNO)
          validInput = false
          return false
        }
    }
    miCaller.call("MMS200MI", "Get", params, callbackITNO)
    
    if(!iRIDN.toString().trim().isEmpty()){
          //Validate Order Number
      params = ["ORNO": iRIDN.toString().trim()]
      Closure < ? > callback = {
        Map < String,
                String > response ->
          if (response.ORNO == null) {
            validInput=false
          }
      }
      miCaller.call("OIS100MI", "GetOrderHead", params, callback)
      
      //If not CO, check if valid DO
      if(!validInput)
      {
        validInput = true
        params = ["CONO": iCONO.toString().trim(), "TRNR": iRIDN.toString().trim()]
        callback = {
        Map < String,
          String > response ->
          if (response.TRNR == null) {
            mi.error("Invalid Order Number " + iRIDN)
            validInput=false
            return false
          }
        }
        miCaller.call("MMS100MI", "GetHead", params, callback)
      }
    }
    
  }
  
  /**
   *SetOutput
   * @params
   * @return
   */
  public void setOutput() {
    readMITALO()
  }
  
  
  /**
   * Read MITALO record
   * @params 
   * @return 
   */
  public void readMITALO() {
    count = 0
    DBAction query = database.table("MITALO").index("80").selection("MQWHLO", "MQITNO", "MQWHSL", "MQBANO", "MQCAMU", "MQTTYP", "MQRIDN","MQRIDO", "MQRIDI", "MQPLSX", "MQRIDL", "MQSTAT", "MQSLTP", "MQALQT", "MQTWSL").build()
    DBContainer container = query.getContainer()
    container.set("MQCONO", iCONO)
    container.set("MQRIDI", iDLIX as Long)
    container.set("MQPLSX", 1)
    container.set("MQCAMU", iCAMU)
    container.set("MQITNO", iITNO)
    query.readAll(container, 5, resultset)
    
  }
  
  Closure < ? > resultset = {
    DBContainer container ->
    String ridn = container.get("MQRIDN").toString()
    boolean output = false
    if (!iRIDN.isEmpty() && iRIDN.equals(ridn)) {
      output = true
    }
    if (iRIDN.isEmpty()) {
      output = true
    }
    if (output && count == 0) {
      count = count + 1
      String whlo = container.get("MQWHLO").toString()
      String whsl = container.get("MQWHSL").toString()
      mi.outData.put("WHLO", whlo)
      mi.outData.put("WHSL", whsl)
      mi.outData.put("ITNO", container.get("MQITNO").toString())
      mi.outData.put("BANO", container.get("MQBANO").toString())
      mi.outData.put("CAMU", container.get("MQCAMU").toString())
      mi.outData.put("TTYP", container.get("MQTTYP").toString())
      mi.outData.put("RIDN", container.get("MQRIDN").toString())
      mi.outData.put("RIDO", container.get("MQRIDO").toString())
      mi.outData.put("RIDI", container.get("MQRIDI").toString())
      mi.outData.put("PLSX", container.get("MQPLSX").toString())
      mi.outData.put("RIDL", container.get("MQRIDL").toString())
      mi.outData.put("STAT", container.get("MQSTAT").toString())
      mi.outData.put("SLTP", container.get("MQSLTP").toString())
      mi.outData.put("ALQT", container.get("MQALQT").toString())
      mi.outData.put("WHLT", rtvLocationType(whlo, whsl))
      String twsl = container.get("MQTWSL").toString()
      String slt2 = ""
      if (twsl != null && !twsl.isEmpty()) {
       slt2 = rtvStockZone(whlo, twsl)
      }
      mi.outData.put("TWSL", twsl)
      mi.outData.put("SLT2", slt2)
      mi.write()
    }
  }
  
  public String rtvLocationType(String whlo, String whsl) { 
    String locationType = ""
    Map < String, String > params = ["CONO": iCONO.toString().trim(),"WHLO": whlo, "WHSL": whsl]
    Closure < ? > callback = {
      Map < String,
        String > response ->
        if (response.WHSL != null) {
          locationType = response.WHLT
        }
      }
    miCaller.call("MMS010MI", "GetLocation", params, callback)
    return locationType
  }
  
  public String rtvStockZone(String whlo, String whsl) { 
    String stockZone = ""
    Map < String, String > params = ["CONO": iCONO.toString().trim(),"WHLO": whlo, "WHSL": whsl]
    Closure < ? > callback = {
      Map < String,
        String > response ->
        if (response.WHSL != null) {
          stockZone = response.SLTP
        }
      }
    miCaller.call("MMS010MI", "GetLocation", params, callback)
    return stockZone
  }
  
}