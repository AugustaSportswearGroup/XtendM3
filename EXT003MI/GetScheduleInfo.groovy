/**
 * README
 * This extension is being used to Get records from EXTOPT table.
 *
 * Name: EXT003MI.GetScheduleInfo
 * Description: Get records from EXTOPT table
 * Date	         Changed By                      Description
 *2023-02-06      SuriyaN@fortude.co          	 Get record from EXTOPT table
 *
 */


public class GetScheduleInfo extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private int iSCHN, iMFNO, iCONO
    private String iMTNO
    
    public GetScheduleInfo(MIAPI mi, DatabaseAPI database,ProgramAPI program,  MICallerAPI miCaller) {
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
        iSCHN = (mi.inData.get("SCHN") == null || mi.inData.get("SCHN").trim().isEmpty()) ? 0 : mi.inData.get("SCHN") as Integer
        iMFNO = (mi.inData.get("MFNO") == null || mi.inData.get("MFNO").trim().isEmpty()) ? 0 : mi.inData.get("MFNO") as Integer
        iMTNO = (mi.inData.get("MTNO") == null || mi.inData.get("MTNO").trim().isEmpty()) ? "" : mi.inData.get("MTNO")
        
         boolean validInput = validateInput(iCONO,iSCHN,iMFNO,iMTNO)
        if(validInput)
        {
          getRecords()
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
     *Get record from EXTOPT table
     * @params 
     * @return 
     */
    private getRecords() {
        DBAction query = database.table("EXTOPT").selection("EXFACI", "EXSTYL", "EXFMT1", "EXFABR", "EXCOLR", "EXSIZE", "EXPRNO", "EXPRCL", "EXMTNO", "EXDWPO", "EXFMT2", "EXCNQT", "EXDIM1", "EXDIM2", "EXDIM3", "EXWHLO", "EXSPE1", "EXSPE2", "EXITCL", "EXSCHN", "EXMFNO", "EXMSEQ", "EXORQA", "EXSTDT", "EXFIDT", "EXSQNX").index("00").build()
        DBContainer container = query.getContainer()
        
        container.set("EXCONO", iCONO)
        container.set("EXSCHN", iSCHN)
        container.set("EXMFNO", iMFNO)
        container.set("EXMTNO", iMTNO)
        if (query.read(container)) {
          
            mi.outData.put("FACI", container.get("EXFACI").toString())
            mi.outData.put("STYL", container.get("EXSTYL").toString())
            mi.outData.put("FMT1", container.get("EXFMT1").toString())
            mi.outData.put("FABR", container.get("EXFABR").toString())
            mi.outData.put("COLR", container.get("EXCOLR").toString())
            mi.outData.put("SIZE", container.get("EXSIZE").toString())
            mi.outData.put("PRNO", container.get("EXPRNO").toString())
            mi.outData.put("PRCL", container.get("EXPRCL").toString())
            mi.outData.put("MTNO", container.get("EXMTNO").toString())
            mi.outData.put("DWPO", container.get("EXDWPO").toString())
            mi.outData.put("FMT2", container.get("EXFMT2").toString())
            mi.outData.put("CNQT", container.get("EXCNQT").toString())
            mi.outData.put("DIM1", container.get("EXDIM1").toString())
            mi.outData.put("DIM2", container.get("EXDIM2").toString())
            mi.outData.put("DIM3", container.get("EXDIM3").toString())
            mi.outData.put("WHLO", container.get("EXWHLO").toString())
            mi.outData.put("SPE1", container.get("EXSPE1").toString())
            mi.outData.put("SPE2", container.get("EXSPE2").toString())
            mi.outData.put("ITCL", container.get("EXITCL").toString())
            mi.outData.put("SCHN", container.get("EXSCHN").toString())
            mi.outData.put("MFNO", container.get("EXMFNO").toString())
            mi.outData.put("MSEQ", container.get("EXMSEQ").toString())
            mi.outData.put("ORQA", container.get("EXORQA").toString())
            mi.outData.put("STDT", container.get("EXSTDT").toString())
            mi.outData.put("FIDT", container.get("EXFIDT").toString())
            mi.outData.put("SQNX", container.get("EXSQNX").toString())
            mi.outData.put("SCHN", container.get("EXSCHN").toString())
            mi.outData.put("MFNO", container.get("EXMFNO").toString())

            mi.write()
        }
        else
        {
          mi.error("Record does not Exist.")
          return
        }
    }
}