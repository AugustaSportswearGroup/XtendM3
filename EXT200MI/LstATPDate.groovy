/**
 * README
 * This extension is being used to List records from EXTPDT table. 
 *
 * Name: EXT200MI.LstATPDate
 * Description: Listing records to EXTPDT table
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co      Listing records from  EXTPDT table
 *
 */



public class LstATPDate extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iITNO

  private int iCONO, pageSize = 10000
  private boolean validInput = true

  public LstATPDate(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    listRecord()
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
   *List records from EXTPDT table
   * @params
   * @return
   */
  public listRecord() {
    DBAction query = database.table("EXTPDT").selection("EXWHLO", "EXITNO", "EXDATE", "EXUPDS", "EXORQ9").index("00").build()
    DBContainer container = query.getContainer()
    
    if(!iWHLO.trim().isEmpty()&&!iITNO.trim().isEmpty()) {
      container.set("EXCONO", iCONO)
      container.set("EXWHLO", iWHLO)
      container.set("EXITNO", iITNO)
      query.readAll(container, 3,pageSize, resultset)
    }else if (!iWHLO.trim().isEmpty())
    {
      container.set("EXCONO", iCONO)
      container.set("EXWHLO", iWHLO)
      query.readAll(container, 2,pageSize, resultset)
    }else 
    {
      container.set("EXCONO", iCONO)
      query.readAll(container, 1,pageSize, resultset)
    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
      mi.outData.put("WHLO", container.get("EXWHLO").toString())
      mi.outData.put("ITNO", container.get("EXITNO").toString())
      mi.outData.put("DATE", container.get("EXDATE").toString())
      mi.outData.put("UPDS", container.get("EXUPDS").toString())
      mi.outData.put("ORQ9", container.get("EXORQ9").toString())
      mi.write()

  }
}