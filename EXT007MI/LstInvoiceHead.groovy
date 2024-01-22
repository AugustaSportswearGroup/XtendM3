/**
 * README
 * This extension is being used to List records from EXTIHH table. 
 *
 * Name: EXT007MI.LstInvoiceHead
 * Description: Listing records to EXTIHH table
 * Date	      Changed By                      Description
 *20230210  SuriyaN@fortude.co      Listing records from  EXTIHH table
 *
 */

public class LstInvoiceHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO, maxPageSize = 10000, iIVNO
  private boolean validInput = true

  public LstInvoiceHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    validateInput(iCONO, iIVNO)
    if (validInput) {
      listRecord()
    }

  }

  /**
   *Validate inputs
   * @params int CONO ,String IVNO
   * @return void
   */
  private validateInput(int CONO, int IVNO) {
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

    if (IVNO == 0) {
      mi.error("Invoice Number must be entered")
      validInput = false
      return
    }

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

  /**
   *List records from EXTIHH table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTIHH").selection("EXIVNO", "EXHSEQ", "EXM3RG", "EXIVTP", "EXCUNO", "EXTRCD", "EXCUOR", "EXORNO", "EXORTP", "EXBTNM", "EXBTA1", "EXBTA2", "EXBTTO", "EXBTEC", "EXBTPO", "EXBTCS", "EXSTNM", "EXSTA1", "EXSTA2", "EXSTTO", "EXSTEC", "EXSTPO", "EXSTCS", "EXSHVI", "EXFOBC", "EXWHLO", "EXSHWT", "EXRSAD", "EXEMAL", "EXBTCN", "EXUPHO", "EXURUS", "EXUCSR", "EXBTA4", "EXSTA4", "EXIVDT", "EXTRDT", "EXORDT", "EXSHDT", "EXTXSA", "EXNTSA", "EXFRAM", "EXUIND").index("00").build()
    DBContainer container = query.getContainer()

    if (iIVNO!= 0) {
      container.set("EXIVNO", iIVNO)
      container.set("EXCONO", iCONO)
      if (query.read(container)) {
        mi.outData.put("IVNO", container.get("EXIVNO").toString())
        mi.outData.put("HSEQ", container.get("EXHSEQ").toString())
        mi.outData.put("IVTP", container.get("EXIVTP").toString())
        mi.outData.put("CUNO", container.get("EXCUNO").toString())
        mi.outData.put("TRCD", container.get("EXTRCD").toString())
        mi.outData.put("CUOR", container.get("EXCUOR").toString())
        mi.outData.put("ORNO", container.get("EXORNO").toString())
        mi.outData.put("ORTP", container.get("EXORTP").toString())
        mi.outData.put("BTNM", container.get("EXBTNM").toString())
        mi.outData.put("BTA1", container.get("EXBTA1").toString())
        mi.outData.put("BTA2", container.get("EXBTA2").toString())
        mi.outData.put("BTTO", container.get("EXBTTO").toString())
        mi.outData.put("BTEC", container.get("EXBTEC").toString())
        mi.outData.put("BTPO", container.get("EXBTPO").toString())
        mi.outData.put("BTCS", container.get("EXBTCS").toString())
        mi.outData.put("STNM", container.get("EXSTNM").toString())
        mi.outData.put("STA1", container.get("EXSTA1").toString())
        mi.outData.put("STA2", container.get("EXSTA2").toString())
        mi.outData.put("STTO", container.get("EXSTTO").toString())
        mi.outData.put("STEC", container.get("EXSTEC").toString())
        mi.outData.put("STPO", container.get("EXSTPO").toString())
        mi.outData.put("STCS", container.get("EXSTCS").toString())
        mi.outData.put("SHVI", container.get("EXSHVI").toString())
        mi.outData.put("FOBC", container.get("EXFOBC").toString())
        mi.outData.put("WHLO", container.get("EXWHLO").toString())
        mi.outData.put("SHWT", container.get("EXSHWT").toString())
        mi.outData.put("RSAD", container.get("EXRSAD").toString())
        mi.outData.put("EMAL", container.get("EXEMAL").toString())
        mi.outData.put("BTCN", container.get("EXBTCN").toString())
        mi.outData.put("UPHO", container.get("EXUPHO").toString())
        mi.outData.put("URUS", container.get("EXURUS").toString())
        mi.outData.put("UCSR", container.get("EXUCSR").toString())
        mi.outData.put("BTA4", container.get("EXBTA4").toString())
        mi.outData.put("STA4", container.get("EXSTA4").toString())
        mi.outData.put("IVDT", container.get("EXIVDT").toString())
        mi.outData.put("TRDT", container.get("EXTRDT").toString())
        mi.outData.put("ORDT", container.get("EXORDT").toString())
        mi.outData.put("SHDT", container.get("EXSHDT").toString())
        mi.outData.put("TXSA", container.get("EXTXSA").toString())
        mi.outData.put("NTSA", container.get("EXNTSA").toString())
        mi.outData.put("FRAM", container.get("EXFRAM").toString())
        mi.outData.put("UIND", container.get("EXUIND").toString())
        mi.outData.put("M3RG", container.get("EXM3RG").toString())
        mi.write()
      }
    } else {
      container.set("EXCONO", iCONO)
      query.readAll(container, 1, maxPageSize, resultset)
    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("IVNO", container.get("EXIVNO").toString())
    mi.outData.put("HSEQ", container.get("EXHSEQ").toString())
    mi.outData.put("IVTP", container.get("EXIVTP").toString())
    mi.outData.put("CUNO", container.get("EXCUNO").toString())
    mi.outData.put("TRCD", container.get("EXTRCD").toString())
    mi.outData.put("CUOR", container.get("EXCUOR").toString())
    mi.outData.put("ORNO", container.get("EXORNO").toString())
    mi.outData.put("ORTP", container.get("EXORTP").toString())
    mi.outData.put("BTNM", container.get("EXBTNM").toString())
    mi.outData.put("BTA1", container.get("EXBTA1").toString())
    mi.outData.put("BTA2", container.get("EXBTA2").toString())
    mi.outData.put("BTTO", container.get("EXBTTO").toString())
    mi.outData.put("BTEC", container.get("EXBTEC").toString())
    mi.outData.put("BTPO", container.get("EXBTPO").toString())
    mi.outData.put("BTCS", container.get("EXBTCS").toString())
    mi.outData.put("STNM", container.get("EXSTNM").toString())
    mi.outData.put("STA1", container.get("EXSTA1").toString())
    mi.outData.put("STA2", container.get("EXSTA2").toString())
    mi.outData.put("STTO", container.get("EXSTTO").toString())
    mi.outData.put("STEC", container.get("EXSTEC").toString())
    mi.outData.put("STPO", container.get("EXSTPO").toString())
    mi.outData.put("STCS", container.get("EXSTCS").toString())
    mi.outData.put("SHVI", container.get("EXSHVI").toString())
    mi.outData.put("FOBC", container.get("EXFOBC").toString())
    mi.outData.put("WHLO", container.get("EXWHLO").toString())
    mi.outData.put("SHWT", container.get("EXSHWT").toString())
    mi.outData.put("RSAD", container.get("EXRSAD").toString())
    mi.outData.put("EMAL", container.get("EXEMAL").toString())
    mi.outData.put("BTCN", container.get("EXBTCN").toString())
    mi.outData.put("UPHO", container.get("EXUPHO").toString())
    mi.outData.put("URUS", container.get("EXURUS").toString())
    mi.outData.put("UCSR", container.get("EXUCSR").toString())
    mi.outData.put("BTA4", container.get("EXBTA4").toString())
    mi.outData.put("STA4", container.get("EXSTA4").toString())
    mi.outData.put("IVDT", container.get("EXIVDT").toString())
    mi.outData.put("TRDT", container.get("EXTRDT").toString())
    mi.outData.put("ORDT", container.get("EXORDT").toString())
    mi.outData.put("SHDT", container.get("EXSHDT").toString())
    mi.outData.put("TXSA", container.get("EXTXSA").toString())
    mi.outData.put("NTSA", container.get("EXNTSA").toString())
    mi.outData.put("FRAM", container.get("EXFRAM").toString())
    mi.outData.put("UIND", container.get("EXUIND").toString())
    mi.outData.put("M3RG", container.get("EXM3RG").toString())
    mi.write()

  }
}