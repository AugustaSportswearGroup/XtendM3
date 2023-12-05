/**
 * README
 * This extension is being used to Get records from EXTWHL table. 
 *
 * Name: EXT005MI.GetItmWarehouse
 * Description: Get records from EXTWHL table
 * Date	             Changed By                      Description
 * 2023-02-09       SuriyaN@fortude.co            Get records from EXTWHL table
 *
 */


public class GetItmWarehouse extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private String iITNO, iWHLO
    private int iCONO

    public GetItmWarehouse(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
          getRecord()
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
     *get records from EXTWHL table
     * @params 
     * @return 
     */
    public getRecord() {
        DBAction query = database.table("EXTWHL").selection("EXITNO", "EXWHLO", "EXWHSL", "EXSTQT", "EXORQA", "EXORQT", "EXORBO", "EXM3RG", "EXM3LM").index("00").build()
        DBContainer container = query.getContainer()

        container.set("EXCONO", iCONO)
        container.set("EXITNO", iITNO)
        container.set("EXWHLO", iWHLO)
        if (query.read(container)) {
            mi.outData.put("ITNO", container.get("EXITNO").toString())
            mi.outData.put("WHLO", container.get("EXWHLO").toString())
            mi.outData.put("WHSL", container.get("EXWHSL").toString())
            mi.outData.put("STQT", container.get("EXSTQT").toString())
            mi.outData.put("ORQA", container.get("EXORQA").toString())
            mi.outData.put("ORQT", container.get("EXORQT").toString())
            mi.outData.put("ORBO", container.get("EXORBO").toString())
            mi.outData.put("M3RG", container.get("EXM3RG").toString())
            mi.outData.put("M3LM", container.get("EXM3LM").toString())
            mi.write()

        } else {
            mi.error("Record does not Exist.")
            return
        }
    }
}