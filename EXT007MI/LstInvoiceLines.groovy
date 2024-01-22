/**
 * README
 * This extension is being used to List records from EXTIHD table. 
 *
 * Name: EXT007MI.LstInvoiceLines
 * Description: Listing records to EXTIHD table
 * Date	      Changed By                      Description
 *20230210  SuriyaN@fortude.co      Listing records from  EXTIHD table
 *
 */

public class LstInvoiceLines extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDSEQ
  private int iCONO, iIVNO, maxPageSize = 10000
  private boolean validInput = true

  public LstInvoiceLines(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iIVNO = (mi.inData.get("IVNO") == null || mi.inData.get("IVNO").trim().isEmpty()) ? 0 : mi.inData.get("IVNO") as Integer
    iDSEQ = (mi.inData.get("DSEQ") == null || mi.inData.get("DSEQ").trim().isEmpty()) ? "" : mi.inData.get("DSEQ")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    validateInput(iCONO, iIVNO, iDSEQ)
    if (validInput) {
      listRecord()
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
    Map < String, String > params = ["CONO": CONO.toString().trim()]
    Closure < ? > callback = {
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
    if (!IVNO == 0) {
      params = ["CONO": CONO.toString().trim(), "QERY": "UHIVNO from OINVOH where UHIVNO = '" + IVNO + "'"]
      callback = {
        Map < String,
        String > response ->
        if (response.REPL == null) {
          mi.error("Invalid Invoice Number " + IVNO)
          validInput = false
          return
        } else {

        }
      }
      miCaller.call("EXPORTMI", "Select", params, callback)
    }

  }
  /**
   *List records from EXTIHD table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTIHD").selection("EXIVNO", "EXHSEQ", "EXDSEQ", "EXITNO", "EXITTY", "EXFUDS", "EXWHLO", "EXPRLV", "EXPRLN", "EXTXID", "EXUCLR", "EXURFT", "EXURFN", "EXUDSG", "EXUDGR", "EXUCLC", "EXUCLS", "EXSHQT", "EXORQT", "EXBOQT", "EXUNPR", "EXUNCS").index("00").build()
    DBContainer container = query.getContainer()

    if (iIVNO != 0 && !iDSEQ.trim().isEmpty()) {
      container.set("EXIVNO", iIVNO)
      container.set("EXDSEQ", iDSEQ)
      container.set("EXCONO", iCONO)
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
      }
    } else if (iIVNO != 0) {
      container.set("EXIVNO", iIVNO)
      container.set("EXCONO", iCONO)
      query.readAll(container, 2, maxPageSize, resultset)
    } else {
      container.set("EXCONO", iCONO)
      query.readAll(container, 1, maxPageSize, resultset)
    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
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

  }
}