/**
 * README
 * This extension is being used to Get records from EXTLOG table. 
 *
 * Name: EXT009MI.GetLogs
 * Description: Get records from EXTLOG table
 * Date	      Changed By                      Description
 *20230221  SuriyaN@fortude.co     Get records from EXTLOG table
 *
 */




public class GetLogs extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private String iDIVI, iTABL, iFLDN, iKVA1, iKVA2
    private int iCONO, maxPageSize = 10000
    private boolean validInput = true
    private HashMap <String, String> logData

    public GetLogs(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
        this.mi = mi
        this.database = database
        this.program = program
    }
    /**
     ** Main function
     * @param
     * @return
     */
    public void main() {
        iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? "" : mi.inData.get("DIVI")
        iTABL = (mi.inData.get("TABL") == null || mi.inData.get("TABL").trim().isEmpty()) ? "" : mi.inData.get("TABL")
        iFLDN = (mi.inData.get("FLDN") == null || mi.inData.get("FLDN").trim().isEmpty()) ? "" : mi.inData.get("FLDN")
        iKVA1 = (mi.inData.get("KVA1") == null || mi.inData.get("KVA1").trim().isEmpty()) ? "" : mi.inData.get("KVA1")
        iKVA2 = (mi.inData.get("KVA2") == null || mi.inData.get("KVA2").trim().isEmpty()) ? "" : mi.inData.get("KVA2")
        iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
        
        validateInput()
        if (validInput) {
        getRecord()
        }
        if (logData != null) {
          mi.outData.put("CONO", logData.get("CONO").toString())
          mi.outData.put("DIVI", logData.get("DIVI").toString())
          mi.outData.put("TABL", logData.get("TABL").toString())
          mi.outData.put("FLDN", logData.get("FLDN").toString())
          mi.outData.put("KVA1", logData.get("KVA1").toString())
          mi.outData.put("KVA2", logData.get("KVA2").toString())
          mi.outData.put("OVAL", logData.get("OVAL").toString())
          mi.outData.put("VALU", logData.get("VALU").toString())
          mi.write()
        } else {
          mi.error("Record does not Exist.")
          return
        }
    }
    
          /**
   *Validate records 
   * @params 
   * @return 
   */
  public validateInput() {
    //Validate Company Number
    Map<String, String>  params = ["CONO": iCONO.toString().trim()]
    Closure<?>  callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)
    
    //Validate Division
    params = ["CONO": iCONO.toString().trim(),"DIVI":iDIVI.toString().trim()]
    callback = {
      Map < String,
        String > response ->
        if (response.DIVI == null) {
          mi.error("Invalid Division " + iDIVI)
          validInput = false
          return false
        }
    }
    
    miCaller.call("MNS100MI", "GetBasicData", params, callback)
    
    //Validate Table
    if (iTABL == null || iTABL.trim().isEmpty()) {
      mi.error("Table must be entered")
      validInput = false
      return false
    }
    
    //Validate Field
    if (iFLDN == null || iFLDN.trim().isEmpty()) {
      mi.error("Field must be entered")
      validInput = false
      return false
    }
      
  }
    /**
     *Get records from EXTLOG table
     * @params 
     * @return 
     */
    public getRecord() {
        DBAction query = database.table("EXTLOG").selection("EXCONO", "EXDIVI", "EXTABL", "EXFLDN", "EXKVA1", "EXKVA2", "EXOVAL", "EXVALU").index("10").build()
        DBContainer container = query.getContainer()

        container.set("EXCONO", iCONO)
        container.set("EXDIVI", iDIVI)
        container.set("EXTABL", iTABL)
        container.set("EXFLDN", iFLDN)
        container.set("EXKVA1", iKVA1)
        container.set("EXKVA2", iKVA2)
        query.readAll(container, 6,maxPageSize, resultset)
    }
    Closure < ? > resultset = {
      DBContainer container -> 
      String tabl = container.get("EXTABL").toString()
      if (tabl != null) {
        if (logData == null) {
          logData = new HashMap <String, String> ()
        }
        logData.put("CONO", container.get("EXCONO").toString())
        logData.put("DIVI", container.get("EXDIVI").toString())
        logData.put("TABL", container.get("EXTABL").toString())
        logData.put("FLDN", container.get("EXFLDN").toString())
        logData.put("KVA1", container.get("EXKVA1").toString())
        logData.put("KVA2", container.get("EXKVA2").toString())
        logData.put("OVAL", container.get("EXOVAL").toString())
        logData.put("VALU", container.get("EXVALU").toString())
      }

    }
}