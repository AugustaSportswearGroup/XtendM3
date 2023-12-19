/**
 * README
 * This extension is being used to Get records from EXTNCT table. 
 *
 * Name: EXT856MI.GetASNHead
 * Description: Get records from EXTNCT table
 * Date	      Changed By                      Description
 *20230829  SuriyaN@fortude.co     Get records from EXTNCT table
 *
 */

public class GetASNHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iPYCU
  private int iCONO
  private boolean validInput = true

  public GetASNHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iPYCU = (mi.inData.get("PYCU") == null || mi.inData.get("PYCU").trim().isEmpty()) ? "" : mi.inData.get("PYCU")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    validateInput()
    if (validInput) {
      getRecord()
    }

  }

  /**
   *Validate inputs
   * @params int CONO ,String ITNO,String WHLO,String iWHSL
   * @return void
   */
  public validateInput() {

    //Validate Company Number
    Map < String, String > params = ["CONO": iCONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Customer Number
    params = ["CUNO": iPYCU.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.CUNO == null) {
        mi.error("Invalid Customer Numebr Number " + iPYCU)
        validInput = false
        return
      }
    }
    miCaller.call("CRS610MI", "GetBasicData", params, callback)

    //Validate Warehouse Number
    params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.WHLO == null) {
        mi.error("Invalid Warehouse Number " + iWHLO)
        validInput = false
        return
      }
    }
    miCaller.call("MMS005MI", "GetWarehouse", params, callback)
  }

  /**
   *Get records from EXTNCT table
   * @params 
   * @return 
   */
  public getRecord() {
    DBAction query = database.table("EXTNCT").selection("EXDIVI", "EXWHLO", "EXPYCU", "EXSPOC", "EXASNP", "EXDEMH", "EXDFLB", "EXSFLB", "EXASNF", "EXSNRP", "EXSNFO", "EXSTAT", "EXLFCR", "EXLFSH", "EXDCLQ", "EXCPKI", "EXPPKI", "EXSPKI", "EXEANI", "EXIDSD", "EXUD01", "EXUD02", "EXUD04", "EXUD05", "EXUD06", "EXUD07", "EXUD08", "EXUD09", "EXUD10", "EXUD11", "EXUD12", "EXUD13", "EXUD14", "EXUD15", "EXUD16", "EXUD17", "EXUD18", "EXCONO", "EXCPID", "EXSNSN", "EXSCSN", "EXPLSN", "EXUD03").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXPYCU", iPYCU)
    if (query.read(container)) {
      mi.outData.put("DIVI", container.get("EXDIVI").toString())
      mi.outData.put("WHLO", container.get("EXWHLO").toString())
      mi.outData.put("PYCU", container.get("EXPYCU").toString())
      mi.outData.put("SPOC", container.get("EXSPOC").toString())
      mi.outData.put("ASNP", container.get("EXASNP").toString())
      mi.outData.put("DEMH", container.get("EXDEMH").toString())
      mi.outData.put("DFLB", container.get("EXDFLB").toString())
      mi.outData.put("SFLB", container.get("EXSFLB").toString())
      mi.outData.put("ASNF", container.get("EXASNF").toString())
      mi.outData.put("SNRP", container.get("EXSNRP").toString())
      mi.outData.put("SNFO", container.get("EXSNFO").toString())
      mi.outData.put("STAT", container.get("EXSTAT").toString())
      mi.outData.put("LFCR", container.get("EXLFCR").toString())
      mi.outData.put("LFSH", container.get("EXLFSH").toString())
      mi.outData.put("DCLQ", container.get("EXDCLQ").toString())
      mi.outData.put("CPKI", container.get("EXCPKI").toString())
      mi.outData.put("PPKI", container.get("EXPPKI").toString())
      mi.outData.put("SPKI", container.get("EXSPKI").toString())
      mi.outData.put("EANI", container.get("EXEANI").toString())
      mi.outData.put("IDSD", container.get("EXIDSD").toString())
      mi.outData.put("UD01", container.get("EXUD01").toString())
      mi.outData.put("UD02", container.get("EXUD02").toString())
      mi.outData.put("UD04", container.get("EXUD04").toString())
      mi.outData.put("UD05", container.get("EXUD05").toString())
      mi.outData.put("UD06", container.get("EXUD06").toString())
      mi.outData.put("UD07", container.get("EXUD07").toString())
      mi.outData.put("UD08", container.get("EXUD08").toString())
      mi.outData.put("UD09", container.get("EXUD09").toString())
      mi.outData.put("UD10", container.get("EXUD10").toString())
      mi.outData.put("UD11", container.get("EXUD11").toString())
      mi.outData.put("UD12", container.get("EXUD12").toString())
      mi.outData.put("UD13", container.get("EXUD13").toString())
      mi.outData.put("UD14", container.get("EXUD14").toString())
      mi.outData.put("UD15", container.get("EXUD15").toString())
      mi.outData.put("UD16", container.get("EXUD16").toString())
      mi.outData.put("UD17", container.get("EXUD17").toString())
      mi.outData.put("UD18", container.get("EXUD18").toString())
      mi.outData.put("CONO", container.get("EXCONO").toString())
      mi.outData.put("CPID", container.get("EXCPID").toString())
      mi.outData.put("SNSN", container.get("EXSNSN").toString())
      mi.outData.put("SCSN", container.get("EXSCSN").toString())
      mi.outData.put("PLSN", container.get("EXPLSN").toString())
      mi.outData.put("UD03", container.get("EXUD03").toString())

      mi.write()

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}