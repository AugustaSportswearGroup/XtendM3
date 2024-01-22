/**
 * README
 * This extension is being used to Get records from EXTIHD table. 
 *
 * Name: EXT007MI.GetInvoiceLines
 * Description: Get records from EXTIHD table
 * Date	      Changed By                      Description
 *20230210  SuriyaN@fortude.co     Get records from EXTIHD table
 *
 */



public class GetInvoiceLines extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private String iDSEQ
    private int iCONO,iIVNO
    private boolean validInput = true

    public GetInvoiceLines(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller){
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
        iDSEQ = (mi.inData.get("DSEQ") == null || mi.inData.get("DSEQ").trim().isEmpty()) ? "" : mi.inData.get("DSEQ")
        iIVNO = (mi.inData.get("IVNO") == null || mi.inData.get("IVNO").trim().isEmpty()) ? 0 : mi.inData.get("IVNO") as Integer
        iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
        validateInput(iCONO,iIVNO, iDSEQ)
        if(validInput)
        {
        getRecord()
        }
    }
    
    
     /**
     *Validate inputs
     * @params int CONO ,String IVNO
     * @return 
     */
    private validateInput(int CONO,int IVNO, String DSEQ){
        String m3CUOR = ""
      
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
        
         //Validate Invoice Number
         
      if(IVNO==0){
          mi.error("Invoice Number must be entered")
          validInput = false
         return
        }
        
     if(DSEQ.trim().isEmpty()){
          mi.error("Invoice Number must be entered")
          validInput = false
         return
        }
        
       Map<String, String> paramsIVNO = ["CONO": CONO.toString().trim(), "QERY": "UHIVNO from OINVOH where UHIVNO = '"+IVNO+"'"]
        Closure<?> callbackIVNO = {
            Map < String,
            String > response ->
            if (response.REPL == null) {
                mi.error("Invalid Invoice Number " + IVNO)
                validInput = false
                return 
            }else
            {
              
            }
        }
        miCaller.call("EXPORTMI", "Select", paramsIVNO, callbackIVNO)

    }
    /**
     *Get records from EXTIHD table
     * @params 
     * @return 
     */
    public getRecord() {
        DBAction query = database.table("EXTIHD").selection("EXIVNO", "EXHSEQ", "EXDSEQ", "EXITNO", "EXITTY", "EXFUDS", "EXWHLO", "EXPRLV", "EXPRLN", "EXTXID", "EXUCLR", "EXURFT", "EXURFN", "EXUDSG", "EXUDGR", "EXUCLC", "EXUCLS", "EXSHQT","EXORQT","EXBOQT","EXUNPR","EXUNCS").index("00").build()
        DBContainer container = query.getContainer()

        container.set("EXCONO", iCONO)
        container.set("EXIVNO", iIVNO)
        container.set("EXDSEQ", iDSEQ)
        if (query.read(container)) {
            mi.outData.put("IVNO", container.get("EXIVNO").toString())
            mi.outData.put("HSEQ", container.get("EXHSEQ").toString())
            mi.outData.put("DSEQ", container.get("EXDSEQ").toString())
            mi.outData.put("ITNO", container.get("EXITNO").toString())
            mi.outData.put("ITTY", container.get("EXITTY").toString())
            mi.outData.put("FUDS", container.get("EXFUDS").toString())
            mi.outData.put("WHLO", container.get("EXWHLO").toString())
            mi.outData.put("PRLV", container.get("EXPRLV").toString())
            mi.outData.put("PRLN", container.get("EXPRLN").toString())
            mi.outData.put("TXID", container.get("EXTXID").toString())
            mi.outData.put("UCLR", container.get("EXUCLR").toString())
            mi.outData.put("URFT", container.get("EXURFT").toString())
            mi.outData.put("URFN", container.get("EXURFN").toString())
            mi.outData.put("UDSG", container.get("EXUDSG").toString())
            mi.outData.put("UDGR", container.get("EXUDGR").toString())
            mi.outData.put("UCLC", container.get("EXUCLC").toString())
            mi.outData.put("UCLS", container.get("EXUCLS").toString())
            mi.outData.put("SHQT", container.get("EXSHQT").toString())
            mi.outData.put("ORQT", container.get("EXORQT").toString())
            mi.outData.put("BOQT", container.get("EXBOQT").toString())
            mi.outData.put("UNPR", container.get("EXUNPR").toString())
            mi.outData.put("UNCS", container.get("EXUNCS").toString())

            mi.write()

        } else {
            mi.error("Record does not Exist.")
            return
        }
    }
}