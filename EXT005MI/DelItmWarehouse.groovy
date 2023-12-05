/**
 * README
 * This extension is being used to Delete records from EXTWHL table. 
 *
 * Name: EXT005MI.DelItmWarehouse
 * Description: Deleting records from EXTWHL table
 * Date	               Changed By                      Description
 * 2023-02-10          SuriyaN@fortude.co         	Deleting records from EXTWHL table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


public class DelItmWarehouse extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private String iITNO, iWHLO

    private int iCONO
    public DelItmWarehouse(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
        iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
        iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
        iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
        
        boolean validInput = validateInput(iCONO,iITNO,iWHLO)
        if(validInput)
        {
          deleteRecord()
        }
    }
    
     /**
     *Validate inputs
     * @params int CONO ,String ITNO,String WHLO
     * @return boolean
     */
    private boolean validateInput(int CONO,String ITNO,String WHLO) {
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

         //Validate Warehouse Number
        Map<String, String> paramsWHLO = ["CONO": CONO.toString().trim(), "WHLO": WHLO.toString().trim()]
        Closure<?> callbackWHLO = {
            Map < String,
            String > response ->
            if (response.WHLO == null) {
                mi.error("Invalid Warehouse Number " + WHLO)
                return false
            }
        }
        miCaller.call("MMS005MI", "GetWarehouse", paramsWHLO, callbackWHLO) 
        
         //Validate Item Number
        Map<String, String> paramsITNO = ["CONO": CONO.toString().trim(), "ITNO": ITNO.toString().trim()]
        Closure<?> callbackITNO = {
            Map < String,
            String > response ->
            if (response.ITNO == null) {
                mi.error("Invalid Item Number " + ITNO)
                return false
            }
        }
        miCaller.call("MMS200MI", "Get", paramsITNO, callbackITNO) 
        
        
        return true
    }
    
    /**
     *Delete records from EXTWHL table
     * @params 
     * @return 
     */
    public deleteRecord() {
        DBAction query = database.table("EXTWHL").index("00").build()
        DBContainer container = query.getContainer()
        container.set("EXCONO", iCONO)
        container.set("EXITNO", iITNO)
        container.set("EXWHLO", iWHLO)
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