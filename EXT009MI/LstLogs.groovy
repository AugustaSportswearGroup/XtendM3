/**
 * README
 * This extension is being used to List records from EXTLOG table. 
 *
 * Name: EXT009MI.ListLogs
 * Description: Listing records to EXTLOG table
 * Date	      Changed By                      Description
 *20230221  SuriyaN@fortude.co      Listing records from  EXTLOG table
 *
 */


public class ListLogs extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private String iDIVI, iTABL, iFLDN, iKVA1, iKVA2
    private HashMap <String, String> logData
    private int iCONO, pageSize = 10000
    private boolean validInput = true


    public ListLogs(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
        iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? "" : mi.inData.get("DIVI")
        iTABL = (mi.inData.get("TABL") == null || mi.inData.get("TABL").trim().isEmpty()) ? "" : mi.inData.get("TABL")
        iFLDN = (mi.inData.get("FLDN") == null || mi.inData.get("FLDN").trim().isEmpty()) ? "" : mi.inData.get("FLDN")
        iKVA1 = (mi.inData.get("KVA1") == null || mi.inData.get("KVA1").trim().isEmpty()) ? "" : mi.inData.get("KVA1")
        iKVA2 = (mi.inData.get("KVA2") == null || mi.inData.get("KVA2").trim().isEmpty()) ? "" : mi.inData.get("KVA2")
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
     *List records from EXTLOG table
     * @params 
     * @return 
     */
    public listRecord() {
        DBAction query = database.table("EXTLOG").selection("EXDIVI", "EXTABL", "EXFLDN", "EXKVA1", "EXKVA2", "EXOVAL", "EXVALU", "EXCONO").index("10").build()
        DBContainer container = query.getContainer()
        if(!iDIVI.trim().isEmpty()&&!iTABL.trim().isEmpty()&&!iFLDN.trim().isEmpty()&&!iKVA1.trim().isEmpty()&&!iKVA2.trim().isEmpty())
        {
          container.set("EXCONO", iCONO)
          container.set("EXDIVI", iDIVI)
          container.set("EXTABL", iTABL)
          container.set("EXFLDN", iFLDN)
          container.set("EXKVA1", iKVA1)
          container.set("EXKVA2", iKVA2)
          query.readAll(container, 6, pageSize, resultset)
        } else if (!iDIVI.trim().isEmpty()&&!iTABL.trim().isEmpty()&&!iFLDN.trim().isEmpty()&&!iKVA1.trim().isEmpty())
        {
          container.set("EXCONO", iCONO)
          container.set("EXDIVI", iDIVI)
          container.set("EXTABL", iTABL)
          container.set("EXFLDN", iFLDN)
          container.set("EXKVA1", iKVA1)
          query.readAll(container, 5, pageSize, resultset)
        }
        else if (!iDIVI.trim().isEmpty() && !iTABL.trim().isEmpty()&&!iFLDN.trim().isEmpty())
        {
         container.set("EXCONO", iCONO)
          container.set("EXDIVI", iDIVI)
          container.set("EXTABL", iTABL)
          container.set("EXFLDN", iFLDN)
          query.readAll(container, 4, pageSize, resultset)
        }
        else if (!iDIVI.trim().isEmpty() && !iTABL.trim().isEmpty())
        {
          container.set("EXCONO", iCONO)
          container.set("EXDIVI", iDIVI)
          container.set("EXTABL", iTABL)
          query.readAll(container, 3, pageSize, resultset)
        }
        else if (!iDIVI.trim().isEmpty())
        {
          container.set("EXCONO", iCONO)
          container.set("EXDIVI", iDIVI)
          query.readAll(container, 2, pageSize, resultset)
        }else 
        {
          container.set("EXCONO", iCONO)
          query.readAll(container, 1, pageSize, resultset)
        }
        
       
    }
    Closure<?> resultset = {
        DBContainer container ->
        mi.outData.put("DIVI", container.get("EXDIVI").toString())
        mi.outData.put("TABL", container.get("EXTABL").toString())
        mi.outData.put("FLDN", container.get("EXFLDN").toString())
        mi.outData.put("KVA1", container.get("EXKVA1").toString())
        mi.outData.put("KVA2", container.get("EXKVA2").toString())
        mi.outData.put("OVAL", container.get("EXOVAL").toString())
        mi.outData.put("VALU", container.get("EXVALU").toString())
        mi.outData.put("CONO", container.get("EXCONO").toString())

        mi.write()

    }
}