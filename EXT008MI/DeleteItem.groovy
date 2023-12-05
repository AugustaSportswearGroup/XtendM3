/**
 * README
 * This extension is being used to Delete records from EXTITM table. 
 *
 * Name: EXT008MI.DeleteItem
 * Description: Deleting records from EXTITM table
 * Date	      Changed By                      Description
 *20230214  SuriyaN@fortude.co    Deleting records from EXTITM table
 *
 */


public class DeleteItem extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private int iCONO
    private String iITNO
    private boolean validInput = true
    
    public DeleteItem(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
        iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
        
        validateInput(iCONO,iITNO)
        if(validInput)
        {
          deleteRecord()
        }
        
    }
    
    /**
     *Validate inputs
     * @params int CONO ,String ITNO
     * @return void
     */
    private validateInput(int CONO,String ITNO) {
      
        //Validate Company Number
       Map<String, String> params = ["CONO": CONO.toString().trim()]
        Closure<?> callback = {
            Map < String,
            String > response ->
            if (response.CONO == null) {
                mi.error("Invalid Company Number " + CONO)
                validInput = false
                return
            }
        }
        miCaller.call("MNS095MI", "Get", params, callback)
        
        //Validate Item Number
        Map<String, String> paramsITNO = ["CONO": CONO.toString().trim(), "ITNO": ITNO.toString().trim()]
        Closure<?> callbackITNO = {
            Map < String,
            String > response ->
            if (response.ITNO == null) {
                mi.error("Invalid Item Number " + ITNO)
               validInput = false
                return
            }
        }
        miCaller.call("MMS200MI", "Get", paramsITNO, callbackITNO) 
        
    }
    
    /**
     *Delete records from EXTITM table
     * @params 
     * @return 
     */
    public deleteRecord() {
        DBAction query = database.table("EXTITM").index("00").build()
        DBContainer container = query.getContainer()
        container.set("EXITNO", iITNO)
        container.set("EXCONO", iCONO)
        if (!query.readLock(container, deleteCallBack)) {
        mi.error("Record does not Exist.")
        return
    }
    }

    Closure<?> deleteCallBack = {
        LockedResult lockedResult ->
        lockedResult.delete()
    }
}