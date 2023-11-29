/**
 * README
 * This extension is being used to Get records from EXTPDT table. 
 *
 * Name: EXT200MI.GetATPDate
 * Description: Get records from EXTPDT table
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co     Get records from EXTPDT table
 *
 */



public class GetATPDate extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iITNO
  private int iCONO
  private boolean validInput = true

  public GetATPDate(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    
    validateInput()
    if (validInput) {
    getRecord()
    }
  }
  
    /**
   *Validate records 
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

    //Validate Item Number
    params = ["CONO": iCONO.toString().trim(),"ITNO":iITNO.toString().trim()]
    callback = {
      Map < String,
        String > response ->
        if (response.ITNO == null) {
          mi.error("Invalid Item Number " + iITNO)
          validInput = false
          return false
        }
    }
    
    miCaller.call("MMS200MI", "Get", params, callback)
    
     //Validate Warehouse Number
      params = ["CONO": iCONO.toString().trim(),"WHLO":iWHLO.toString().trim()]
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
  
  /**
   *Get records from EXTPDT table
   * @params 
   * @return 
   */
  public getRecord() {
    DBAction query = database.table("EXTPDT").selection("EXWHLO", "EXITNO", "EXDATE", "EXUPDS").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXWHLO", iWHLO)
    container.set("EXITNO", iITNO)
    if (query.read(container)) {
      mi.outData.put("WHLO", container.get("EXWHLO").toString())
      mi.outData.put("ITNO", container.get("EXITNO").toString())
      mi.outData.put("DATE", container.get("EXDATE").toString())
      mi.outData.put("UPDS", container.get("EXUPDS").toString())
      mi.outData.put("ORQ9", container.get("EXORQ9").toString())
      mi.write()
    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}