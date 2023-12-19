/**
 * README
 * This extension is being used to List records from EXTPCE table. 
 *
 * Name: EXT010MI.LstStockInfo
 * Description: Listing records to EXTPCE table
 * Date	      Changed By                      Description
 *20230627  SuriyaN@fortude.co      Listing records from  EXTPCE table
 *
 */

public class LstStockInfo extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iWHSL
  private int iCONO, pageSize = 10000
  boolean validInput = true

  public LstStockInfo(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iWHSL = (mi.inData.get("WHSL") == null || mi.inData.get("WHSL").trim().isEmpty()) ? "" : mi.inData.get("WHSL")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    validateInput()
    if (validInput) {
      listRecord()
    }

  }

  /**
   *Validate Records
   * @params
   * @return 
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

    //Validate Location Number
    params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim(), "WHSL": iWHSL.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.WHLO == null) {
        mi.error("Invalid Location " + iWHSL)
        validInput = false
        return
      }
    }
    miCaller.call("MMS010MI", "GetLocation", params, callback)

  }

  /**
   *List records from EXTPCE table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTPCE").selection("EXWHLO", "EXCHST", "EXSEQN", "EXSLTP", "EXAISL", "EXLBAY", "EXLEVL", "EXSLOT", "EXWHLT", "EXATV1", "EXATV2", "EXATV3", "EXATV4", "EXATV5", "EXWHSL", "EXCONO").index("00").build()
    DBContainer container = query.getContainer()

    if (!iWHLO.trim().isEmpty() && !iWHSL.trim().isEmpty()) {
      container.set("EXWHLO", iWHLO)
      container.set("EXWHSL", iWHSL)
      container.set("EXCONO", iCONO)
      query.readAll(container, 3, pageSize, resultset)
    } else if (!iWHLO.trim().isEmpty()) {
      container.set("EXWHLO", iWHLO)
      container.set("EXCONO", iCONO)
      query.readAll(container, 2, pageSize, resultset)
    } else {
      container.set("EXCONO", iCONO)
      query.readAll(container, 1, pageSize, resultset)
    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("WHLO", container.get("EXWHLO").toString())
    mi.outData.put("CHST", container.get("EXCHST").toString())
    mi.outData.put("SEQN", container.get("EXSEQN").toString())
    mi.outData.put("SLTP", container.get("EXSLTP").toString())
    mi.outData.put("AISL", container.get("EXAISL").toString())
    mi.outData.put("LBAY", container.get("EXLBAY").toString())
    mi.outData.put("LEVL", container.get("EXLEVL").toString())
    mi.outData.put("SLOT", container.get("EXSLOT").toString())
    mi.outData.put("WHLT", container.get("EXWHLT").toString())
    mi.outData.put("ATV1", container.get("EXATV1").toString())
    mi.outData.put("ATV2", container.get("EXATV2").toString())
    mi.outData.put("ATV3", container.get("EXATV3").toString())
    mi.outData.put("ATV4", container.get("EXATV4").toString())
    mi.outData.put("ATV5", container.get("EXATV5").toString())
    mi.outData.put("WHSL", container.get("EXWHSL").toString())
    mi.outData.put("CONO", container.get("EXCONO").toString())

    mi.write()

  }
}