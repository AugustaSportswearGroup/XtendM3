/**
 * README
 * This extension is being used to delete records from EXTOPT table.
 *
 * Name: EXT003MI.DelScheduleInfo
 * Description: Delete records to EXTOPT table
 * Date	         Changed By                      Description
 *2023-02-03      SuriyaN@fortude.co          	 Delete records to EXTOPT table
 *
 */

public class DelScheduleInfo extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  
  public DelScheduleInfo(MIAPI mi,DatabaseAPI database,ProgramAPI program, MICallerAPI miCaller) {
    this.mi = mi
    this.database = database
    this.program = program
    this.miCaller = miCaller
  }
  
  private int iSCHN, iMFNO, iCONO
  private String iMTNO
  
   /**
     ** Main function
     * @param
     * @return
     */
    public void main() {
        iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
        iSCHN = (mi.inData.get("SCHN") == null || mi.inData.get("SCHN").trim().isEmpty()) ? 0 : mi.inData.get("SCHN") as Integer
        iMFNO = (mi.inData.get("MFNO") == null || mi.inData.get("MFNO").trim().isEmpty()) ? 0 : mi.inData.get("MFNO") as Integer
        iMTNO = (mi.inData.get("MTNO") == null || mi.inData.get("MTNO").trim().isEmpty()) ? "" : mi.inData.get("MTNO") 
      
      boolean validInput = validateInput(iCONO,iSCHN,iMFNO,iMTNO)
      if(validInput)
      {
        deleteRecords()
      }
    }
    
    /**
     *Validate inputs
     * @params int CONO , int SCHN, int MFNO, String MTNO
     * @return boolean
     */
    private boolean validateInput(int CONO, int SCHN, int MFNO, String MTNO) {
        //Validate Company Number
      Map<String, String> params = ["CONO": CONO.toString().trim()]
        Closure<?> callback = {
            Map < String,
            String > response ->
            if (response.CONO == null) {
                mi.error("Invalid Company Number " + CONO)
                return false
            }
        }
        miCaller.call("MNS095MI", "Get", params, callback)

        //Validate Schedule Number
      Map<String, String>  paramsSCHN = ["CONO": CONO.toString().trim(), "SCHN": SCHN.toString().trim()]
        Closure<?> callbackSCHN = {
            Map < String,
            String > response ->
            if (response.SCHN == null) {
                mi.error("Invalid Schedule Number " + SCHN)
                return false
            }
        }
        miCaller.call("PMS270MI", "GetScheduleNo", paramsSCHN, callbackSCHN)
        
        
         //Validate Manufacturing Number
       Map<String, String> paramsMFNO = ["CONO": CONO.toString().trim(), "QERY": "VHMFNO from MWOHED where VHMFNO = '"+MFNO+"'"]
        Closure<?> callbackMFNO = {
            Map < String,
            String > response ->
            if (response.REPL == null) {
                mi.error("Invalid Manufacturing Number " + MFNO)
                return false
            }
        }
        miCaller.call("EXPORTMI", "Select", paramsMFNO, callbackMFNO)
        
        //Validate Item Number
      Map<String, String> paramsMTNO = ["ITNO": MTNO]
        Closure<?> callbackMTNO = {
            Map < String,
            String > response ->
            if (response.ITNO == null) {
                mi.error("Invalid Component Number " + MTNO)
                return false
            }
        }
        miCaller.call("MMS200MI", "Get", paramsMTNO, callbackMTNO)
        
        return true
    }
    
    
      /**
     *Delete records from EXTOPT table
     * @params 
     * @return 
     */
    
    public deleteRecords()
    {
     DBAction query  = database.table("EXTOPT").index("00").build()
     DBContainer container  = query.getContainer() 

      container.set("EXCONO", iCONO)
      container.set("EXSCHN", iSCHN)
      container.set("EXMFNO", iMFNO)
      container.set("EXMTNO", iMTNO)
      if (!query.readLock(container, deleteCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
       
    }
    Closure < ? > deleteCallBack = {
    LockedResult lockedResult ->
    lockedResult.delete()
  }
}