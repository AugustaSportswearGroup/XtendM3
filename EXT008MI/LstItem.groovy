/**
 * README
 * This extension is being used to List records from EXTITM table. 
 *
 * Name: EXT008MI.LstItem
 * Description: Listing records to EXTITM table
 * Date	      Changed By                      Description
 *20230213  SuriyaN@fortude.co      Listing records from  EXTITM table
 *
 */

public class LstItem extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private String iITNO
    private int iCONO, maxPageSize = 10000
    private boolean validInput = true

    public LstItem(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
        iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
        
        validateInput(iCONO,iITNO)
        if(validInput)
        {
          listRecord()
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
        Closure<?>  callback = {
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
        Closure<?>  callbackITNO = {
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
     *List records from EXTITM table
     * @params 
     * @return 
     */
    public listRecord() {
        DBAction query = database.table("EXTITM").selection("EXITNO", "EXITTY", "EXFUDS", "EXPRCD", "EXPRLN", "EXPRTP", "EXWHLO", "EXCTG1", "EXCTG2", "EXCTG3", "EXCTG4", "EXTXID", "EXUCLR", "EXALBO", "EXINAI", "EXUBCL", "EXUCLO", "EXUCFA", "EXUCRC", "EXUCRS", "EXUEMT", "EXUEYC", "EXFPCL", "EXUPIC", "EXSWET", "EXUUCL", "EXUVIS", "EXUVAC", "EXUVIC", "EXUDCG", "EXPRTY", "EXUCLC", "EXUCLS", "EXLSGI", "EXM3IT", "EXSTUC", "EXSTUP").index("00").build()
        DBContainer container = query.getContainer()
        
        if(!iITNO.trim().isEmpty())
        {
          container.set("EXITNO", iITNO)
          container.set("EXCONO", iCONO)
          query.readAll(container, 2,maxPageSize, resultset)
        }else 
        {
          container.set("EXCONO", iCONO)
          query.readAll(container, 1,maxPageSize, resultset)
        }
    }
    Closure<?> resultset = {
        DBContainer container ->
        mi.outData.put("ITNO", container.get("EXITNO").toString())
        mi.outData.put("ITTY", container.get("EXITTY").toString())
        mi.outData.put("FUDS", container.get("EXFUDS").toString())
        mi.outData.put("PRCD", container.get("EXPRCD").toString())
        mi.outData.put("PRLN", container.get("EXPRLN").toString())
        mi.outData.put("PRTP", container.get("EXPRTP").toString())
        mi.outData.put("WHLO", container.get("EXWHLO").toString())
        mi.outData.put("CTG1", container.get("EXCTG1").toString())
        mi.outData.put("CTG2", container.get("EXCTG2").toString())
        mi.outData.put("CTG3", container.get("EXCTG3").toString())
        mi.outData.put("CTG4", container.get("EXCTG4").toString())
        mi.outData.put("TXID", container.get("EXTXID").toString())
        mi.outData.put("UCLR", container.get("EXUCLR").toString())
        mi.outData.put("ALBO", container.get("EXALBO").toString())
        mi.outData.put("INAI", container.get("EXINAI").toString())
        mi.outData.put("UBCL", container.get("EXUBCL").toString())
        mi.outData.put("UCLO", container.get("EXUCLO").toString())
        mi.outData.put("UCFA", container.get("EXUCFA").toString())
        mi.outData.put("UCRC", container.get("EXUCRC").toString())
        mi.outData.put("UCRS", container.get("EXUCRS").toString())
        mi.outData.put("UEMT", container.get("EXUEMT").toString())
        mi.outData.put("UEYC", container.get("EXUEYC").toString())
        mi.outData.put("FPCL", container.get("EXFPCL").toString())
        mi.outData.put("UPIC", container.get("EXUPIC").toString())
        mi.outData.put("SWET", container.get("EXSWET").toString())
        mi.outData.put("UUCL", container.get("EXUUCL").toString())
        mi.outData.put("UVIS", container.get("EXUVIS").toString())
        mi.outData.put("UVAC", container.get("EXUVAC").toString())
        mi.outData.put("UVIC", container.get("EXUVIC").toString())
        mi.outData.put("UDCG", container.get("EXUDCG").toString())
        mi.outData.put("PRTY", container.get("EXPRTY").toString())
        mi.outData.put("UCLC", container.get("EXUCLC").toString())
        mi.outData.put("UCLS", container.get("EXUCLS").toString())
        mi.outData.put("LSGI", container.get("EXLSGI").toString())
        mi.outData.put("M3IT", container.get("EXM3IT").toString())
        mi.outData.put("STUC", container.get("EXSTUC").toString())
        mi.outData.put("STUP", container.get("EXSTUP").toString())

        mi.write()

    }
}