/**
 * README
 * This extension is being used to Delete records from EXTIHD table. 
 *
 * Name: EXT007MI.DelInvoiceLines
 * Description: Deleting records from EXTIHD table
 * Date	      Changed By                      Description
 *20230210  SuriyaN@fortude.co    Deleting records from EXTIHD table
 *
 */



public class DelInvoiceLines extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private int iCONO, iIVNO
    private String iDSEQ
    private boolean validInput = true
    public DelInvoiceLines(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
        iIVNO = (mi.inData.get("IVNO") == null || mi.inData.get("IVNO").trim().isEmpty()) ? 0 : mi.inData.get("IVNO") as Integer
        iDSEQ = (mi.inData.get("DSEQ") == null || mi.inData.get("DSEQ").trim().isEmpty()) ? "" : mi.inData.get("DSEQ")
        
        validateInput(iCONO, iIVNO, iDSEQ)
        if (validInput) {
        deleteRecord()
      }
    }
    
    
     /**
   *Validate inputs
   * @params int CONO ,String IVNO
   * @return void
   */
  private validateInput(int CONO, int IVNO, String DSEQ) {
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
    
    //Validate Sequence Number
    if(DSEQ.trim().isEmpty()){
      mi.error("Sequence Number must be entered")
      validInput = false
      return
    }
    
    //Validate Lines Record
    params = ["CONO": CONO.toString().trim(), "IVNO": IVNO.toString().trim(), "DSEQ": DSEQ.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.IVNO != null) {
        mi.error("Lines exists for the invoice " + IVNO + ". Please delete lines before deleting header.")
        validInput = false
        return
      }
    }
    miCaller.call("EXT007MI", "LstInvoiceLines", params, callback)

  }
    
    /**
     *Delete records from EXTIHD table
     * @params 
     * @return 
     */
    public deleteRecord() {
        DBAction query = database.table("EXTIHD").index("00").build()
        DBContainer container = query.getContainer()

        container.set("EXIVNO", iIVNO)
        container.set("EXDSEQ", iDSEQ)
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