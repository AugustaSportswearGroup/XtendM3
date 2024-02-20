/**
 * README
 * This extension is being used to Get records from EXTMKT table. 
 *
 * Name: EXT004MI.GetLucasData
 * Description: Get records from EXTMKT table
 * Date	      Changed By                      Description
 *20230623  SuriyaN@fortude.co     Get records from EXTMKT table
 *20240212  AbhishekA@fortude.co   Updating Validation logic 
 */

public class GetLucasData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iRCID, iLICP, iORNO
  private int iCONO, iTTYP
  private long iDLIX
  boolean validInput = true

  public GetLucasData(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iRCID = (mi.inData.get("RCID") == null || mi.inData.get("RCID").trim().isEmpty()) ? "" : mi.inData.get("RCID")
    iLICP = (mi.inData.get("LICP") == null || mi.inData.get("LICP").trim().isEmpty()) ? "" : mi.inData.get("LICP")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iTTYP = (mi.inData.get("TTYP") == null || mi.inData.get("TTYP").trim().isEmpty()) ? 0 : mi.inData.get("TTYP") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long

    validateInput()
    if (validInput) {
      getRecord()
    }
  }

  public void validateInput() {

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

    //Validate Division
    if (!iDIVI.toString().trim()) {
      params = ["CONO": iCONO.toString().trim(), "DIVI": iDIVI.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.DIVI == null) {
          mi.error("Invalid Division " + iDIVI)
          validInput = false
          return
        }
      }

      miCaller.call("MNS100MI", "GetBasicData", params, callback)
    }

    if (!iORNO.trim().isEmpty()) {
      //Validate Order Number
      params = ["ORNO": iORNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ORNO == null) {
          validInput = false
        }
      }
      miCaller.call("OIS100MI", "GetOrderHead", params, callback)

      //If not CO, check if valid DO
      if (!validInput) {
        validInput = true
        params = ["CONO": iCONO.toString().trim(), "TRNR": iORNO.toString().trim()]
        callback = {
          Map < String,
          String > response ->
          if (response.TRNR == null) {
            mi.error("Invalid Order Number " + iORNO)
            validInput = false
            return
          }
        }
        miCaller.call("MMS100MI", "GetHead", params, callback)
      }
    }

    if (!iWHLO.trim().isEmpty()) {
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

    if (iDLIX != 0) {
      //Validate Delivery Number
      params = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.DLIX == null) {
          mi.error("Invalid Delivery Number " + iDLIX)
          validInput = false
          return
        }
      }
      miCaller.call("MWS410MI", "GetHead", params, callback)
    }
  }

  /**
   *Get records from EXTMKT table
   * @params 
   * @return 
   */
  public void getRecord() {
    DBAction query = database.table("EXTMTK").selection("EXDIVI", "EXFACI", "EXWHLO", "EXRCID", "EXLICP", "EXORNO", "EXSTAT", "EXLMFS", "EXLMFD", "EXCUNO", "EXMODL", "EXTEPY", "EXTEPA", "EXWGFG", "EXQAFG", "EXSMFG", "EXONFM", "EXPKDV", "EXPSDV", "EXPTDV", "EXLNDV", "EXFD01", "EXFD02", "EXZONE", "EXZON2", "EXUD01", "EXUD02", "EXCONO", "EXTTYP", "EXDLIX", "EXBXNO", "EXESTW", "EXACTW", "EXWGTO", "EXTXID", "EXUD03").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXTTYP", iTTYP)
    container.set("EXDLIX", iDLIX)
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXRCID", iRCID)
    container.set("EXLICP", iLICP)
    container.set("EXORNO", iORNO)
    if (query.read(container)) {
      mi.outData.put("FACI", container.get("EXFACI").toString())
      mi.outData.put("STAT", container.get("EXSTAT").toString())
      mi.outData.put("LMFS", container.get("EXLMFS").toString())
      mi.outData.put("LMFD", container.get("EXLMFD").toString())
      mi.outData.put("CUNO", container.get("EXCUNO").toString())
      mi.outData.put("MODL", container.get("EXMODL").toString())
      mi.outData.put("TEPY", container.get("EXTEPY").toString())
      mi.outData.put("TEPA", container.get("EXTEPA").toString())
      mi.outData.put("WGFG", container.get("EXWGFG").toString())
      mi.outData.put("QAFG", container.get("EXQAFG").toString())
      mi.outData.put("SMFG", container.get("EXSMFG").toString())
      mi.outData.put("ONFM", container.get("EXONFM").toString())
      mi.outData.put("PKDV", container.get("EXPKDV").toString())
      mi.outData.put("PSDV", container.get("EXPSDV").toString())
      mi.outData.put("PTDV", container.get("EXPTDV").toString())
      mi.outData.put("LNDV", container.get("EXLNDV").toString())
      mi.outData.put("FD01", container.get("EXFD01").toString())
      mi.outData.put("FD02", container.get("EXFD02").toString())
      mi.outData.put("ZONE", container.get("EXZONE").toString())
      mi.outData.put("ZON2", container.get("EXZON2").toString())
      mi.outData.put("UD01", container.get("EXUD01").toString())
      mi.outData.put("BXNO", container.get("EXBXNO").toString())
      mi.outData.put("ESTW", container.get("EXESTW").toString())
      mi.outData.put("ACTW", container.get("EXACTW").toString())
      mi.outData.put("WGTO", container.get("EXWGTO").toString())
      mi.outData.put("TXID", container.get("EXTXID").toString())
      mi.outData.put("UD03", container.get("EXUD03").toString())

      mi.write()

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}