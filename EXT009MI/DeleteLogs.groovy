/**
 * README
 * This extension is being used to Delete records from EXTLOG table. 
 *
 * Name: EXT009MI.DeleteLogs
 * Description: Deleting records from EXTLOG table
 * Date	      Changed By                      Description
 *20230221  SuriyaN@fortude.co    Deleting records from EXTLOG table
 *
 */


public class DeleteLogs extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private int iCONO, maxPageSize = 10000
    private String iDIVI, iTABL, iFLDN, iKVA1, iKVA2
    private boolean foundRecord = false
    private boolean validInput = true
    
    public DeleteLogs(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
        iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? "" : mi.inData.get("DIVI")
        iTABL = (mi.inData.get("TABL") == null || mi.inData.get("TABL").trim().isEmpty()) ? "" : mi.inData.get("TABL")
        iFLDN = (mi.inData.get("FLDN") == null || mi.inData.get("FLDN").trim().isEmpty()) ? "" : mi.inData.get("FLDN")
        iKVA1 = (mi.inData.get("KVA1") == null || mi.inData.get("KVA1").trim().isEmpty()) ? "" : mi.inData.get("KVA1")
        iKVA2 = (mi.inData.get("KVA2") == null || mi.inData.get("KVA2").trim().isEmpty()) ? "" : mi.inData.get("KVA2")
        
        validateInput()
        if (validInput) {
        deleteRecord()
        }
        
        if (!foundRecord) {
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
     *Delete records from EXTLOG table
     * @params 
     * @return 
     */
    public deleteRecord() {
        DBAction query = database.table("EXTLOG").selection("EXCONO", "EXDIVI", "EXTABL", "EXFLDN", "EXKVA1", "EXCHNO").index("10").build()
        DBContainer container = query.getContainer()
        container.set("EXDIVI", iDIVI)
        container.set("EXTABL", iTABL)
        container.set("EXFLDN", iFLDN)
        container.set("EXCONO", iCONO)
        container.set("EXKVA1", iKVA1)
        container.set("EXKVA2", iKVA2)
        
        Closure <?> resultset = {
          DBContainer container1 -> 
          String tabl = container1.get("EXTABL").toString()
          if (tabl != null) {
            query.readLock(container1, deleteCallBack)
          }
        }
        query.readAll(container, 6,maxPageSize, resultset)
        

    }

    Closure<?> deleteCallBack = {
        LockedResult lockedResult ->
        foundRecord = true
        lockedResult.delete()
    }
}