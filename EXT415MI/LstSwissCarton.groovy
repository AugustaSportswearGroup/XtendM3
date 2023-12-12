/**
 * README
 * This extension is being used to List records from EXTYPE table. 
 *
 * Name: EXT415MI.LstSwissCarton
 * Description: Listing records to EXTYPE table
 * Date	      Changed By                      Description
 *20230927  SuriyaN@fortude.co      Listing records from  EXTYPE table
 *
 */

public class LstSwissCarton extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iTRTP
  private int iCONO, iTTYP, pageSize = 10000
  private boolean validInput = true

  public LstSwissCarton(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iTRTP = (mi.inData.get("TRTP") == null || mi.inData.get("TRTP").trim().isEmpty()) ? "" : mi.inData.get("TRTP")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iTTYP = (mi.inData.get("TTYP") == null || mi.inData.get("TTYP").trim().isEmpty()) ? 0 : mi.inData.get("TTYP") as Integer

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
    def params = ["CONO": iCONO.toString().trim()]
    def callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    if (!iWHLO.trim().isEmpty()) {
      //Validate Warehouse Number
      params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.WHLO == null) {
          mi.error("Invalid Warehouse Number " + iWHLO)
          validInput = false
          return false
        }
      }
      miCaller.call("MMS005MI", "GetWarehouse", params, callback)
    }

    if (!iTRTP.trim().isEmpty()) {
      //Validate Order Type
      params = ["CONO": iCONO.toString().trim(), "TRTP": iTRTP.toString().trim(), "TTYP": iTTYP.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.TRTP == null) {
          mi.error("Invalid Order Type - Transaction Type")
          validInput = false
          return false
        }
      }
      miCaller.call("CRS200MI", "LstOrderType", params, callback)

    }

  }
  /**
   *List records from EXTYPE table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTYPE").selection("EXWHLO", "EXTRTP", "EXLFMT", "EXSFMT", "EXCONO", "EXVOL3", "EXLANE", "EXTTYP").index("00").build()
    DBContainer container = query.getContainer()

    if (!iWHLO.trim().isEmpty() && !iTRTP.trim().isEmpty() && (mi.inData.get("TRTP") != null && !mi.inData.get("TRTP").trim().isEmpty())) {
      container.set("EXWHLO", iWHLO)
      container.set("EXTRTP", iTRTP)
      container.set("EXCONO", iCONO)
      container.set("EXTTYP", iTTYP)
      query.readAll(container, 4, pageSize, resultset)
    } else if (!iWHLO.trim().isEmpty() && (mi.inData.get("TTYP") != null && !mi.inData.get("TTYP").trim().isEmpty())) {
      container.set("EXWHLO", iWHLO)
      container.set("EXCONO", iCONO)
      container.set("EXTTYP", iTTYP)
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
    mi.outData.put("TRTP", container.get("EXTRTP").toString())
    mi.outData.put("LFMT", container.get("EXLFMT").toString())
    mi.outData.put("SFMT", container.get("EXSFMT").toString())
    mi.outData.put("CONO", container.get("EXCONO").toString())
    mi.outData.put("VOL3", container.get("EXVOL3").toString())
    mi.outData.put("LANE", container.get("EXLANE").toString())
    mi.outData.put("TTYP", container.get("EXTTYP").toString())

    mi.write()

  }
}