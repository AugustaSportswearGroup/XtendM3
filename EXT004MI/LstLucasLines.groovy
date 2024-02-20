/**
 * README
 * This extension is being used to List records from EXTCOM table. 
 *
 * Name: EXT004MI.LstLucasLines
 * Description: Listing records to EXTCOM table
 * Date	      Changed By                      Description
 *20230628  SuriyaN@fortude.co      Listing records from  EXTCOM table
 *20240212  AbhishekA@fortude.co    Updating Validation logic
 */

public class LstLucasLines extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iRCID, iLICP, iORNO, iITNO, iWHSL, iSLTP
  private int iCONO, pageSize = 10000
  private long iDLIX
  private boolean validInput = true

  public LstLucasLines(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iWHSL = (mi.inData.get("WHSL") == null || mi.inData.get("WHSL").trim().isEmpty()) ? "" : mi.inData.get("WHSL")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iSLTP = (mi.inData.get("SLTP") == null || mi.inData.get("SLTP").trim().isEmpty()) ? "" : mi.inData.get("SLTP")
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
  public void validateInput() {

    //Validate Company Number
    Map < String, String > params = ["CONO": iCONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Division

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

    //Validate Item Number
    if (!iITNO.toString().trim().isEmpty()) {
      params = ["CONO": iCONO.toString().trim(), "ITNO": iITNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ITNO == null) {
          mi.error("Invalid Item Number " + iITNO)
          validInput = false
          return false
        }
      }
      miCaller.call("MMS200MI", "Get", params, callback)
    }

    //Validate Order Number
    if (!iORNO.toString().trim().isEmpty()) {
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
            return false
          }
        }
        miCaller.call("MMS100MI", "GetHead", params, callback)
      }
    }

    //Validate Delivery Number
    if (iDLIX != 0) {
      params = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.DLIX == null) {
          mi.error("Invalid Delivery Number " + iDLIX)
          validInput = false
          return false
        }
      }
      miCaller.call("MWS410MI", "GetHead", params, callback)
    }

    //Validate Warehouse Number
    if (!iWHLO.toString().trim().isEmpty()) {
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

    //Validate Location Number
    if (!iWHSL.toString().trim().isEmpty()) {
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

    //Validate Stock Zone
    if (!iSLTP.toString().trim().isEmpty()) {

      params = ["WHLO": iWHLO.toString().trim(), "SLTP": iSLTP.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.SLTP == null) {
          mi.error("Invalid Stock Zone " + iSLTP)
          validInput = false
          return
        }
      }
      miCaller.call("MMS040MI", "GetStockZone", params, callback)
    }

  }
  /**
   *List records from EXTCOM table
   * @params 
   * @return 
   */
  public void listRecord() {

    DBAction query = database.table("EXTCOM").selection("EXDIVI", "EXCISN", "EXFACI", "EXWHLO", "EXRCID", "EXLICP", "EXPKTS", "EXDSNA", "EXWHLT", "EXSLTP", "EXWHSL", "EXTWSL", "EXSLT2", "EXCAMU", "EXORNO", "EXITNO", "EXITDS", "EXSTAT", "EXUNMS", "EXGRTS", "EXSZNM", "EXCLNM", "EXRESP", "EXQAR1", "EXQAR2", "EXCONO", "EXWKFW", "EXPRTY", "EXPMCD", "EXPCCD", "EXTTYP", "EXDLIX", "EXSMPF", "EXDCCD", "EXVOL3", "EXNEWE", "EXPCKQ", "EXTPQT", "EXPCQT", "EXSHQT", "EXEPWT", "EXAUWT", "EXACWT", "EXTDNT").index("00").build()
    DBContainer container = query.getContainer()

    if (!iWHLO.trim().isEmpty() && !iORNO.trim().isEmpty() && iDLIX != 0 && iRCID.trim().isEmpty() && iLICP.trim().isEmpty() && iITNO.trim().isEmpty() && iWHSL.trim().isEmpty() && !iSLTP.trim().isEmpty()) {
      query = database.table("EXTCOM").selection("EXDIVI", "EXCISN", "EXFACI", "EXWHLO", "EXRCID", "EXLICP", "EXPKTS", "EXDSNA", "EXWHLT", "EXSLTP", "EXWHSL", "EXTWSL", "EXSLT2", "EXCAMU", "EXORNO", "EXITNO", "EXITDS", "EXSTAT", "EXUNMS", "EXGRTS", "EXSZNM", "EXCLNM", "EXRESP", "EXQAR1", "EXQAR2", "EXCONO", "EXWKFW", "EXPRTY", "EXPMCD", "EXPCCD", "EXTTYP", "EXDLIX", "EXSMPF", "EXDCCD", "EXVOL3", "EXNEWE", "EXPCKQ", "EXTPQT", "EXPCQT", "EXSHQT", "EXEPWT", "EXAUWT", "EXACWT", "EXTDNT").index("10").build()
      container.set("EXDIVI", iDIVI)
      container.set("EXWHLO", iWHLO)
      container.set("EXORNO", iORNO)
      container.set("EXCONO", iCONO)
      container.set("EXDLIX", iDLIX)
      query.readAll(container, 5, resultset)
    } else if (!iWHLO.trim().isEmpty() && !iRCID.trim().isEmpty() && !iLICP.trim().isEmpty() && !iORNO.trim().isEmpty() && iDLIX != 0 && !iITNO.trim().isEmpty() && !iWHSL.trim().isEmpty()) {
      container.set("EXDIVI", iDIVI)
      container.set("EXWHLO", iWHLO)
      container.set("EXRCID", iRCID)
      container.set("EXLICP", iLICP)
      container.set("EXORNO", iORNO)
      container.set("EXCONO", iCONO)
      container.set("EXDLIX", iDLIX)
      container.set("EXITNO", iITNO)
      container.set("EXWHSL", iWHSL)
      if (query.read(container)) {
        mi.outData.put("DIVI", container.get("EXDIVI").toString())
        mi.outData.put("FACI", container.get("EXFACI").toString())
        mi.outData.put("WHLO", container.get("EXWHLO").toString())
        mi.outData.put("RCID", container.get("EXRCID").toString())
        mi.outData.put("LICP", container.get("EXLICP").toString())
        mi.outData.put("PKTS", container.get("EXPKTS").toString())
        mi.outData.put("DSNA", container.get("EXDSNA").toString())
        mi.outData.put("WHLT", container.get("EXWHLT").toString())
        mi.outData.put("SLTP", container.get("EXSLTP").toString())
        mi.outData.put("WHSL", container.get("EXWHSL").toString())
        mi.outData.put("TWSL", container.get("EXTWSL").toString())
        mi.outData.put("SLT2", container.get("EXSLT2").toString())
        mi.outData.put("CAMU", container.get("EXCAMU").toString())
        mi.outData.put("ORNO", container.get("EXORNO").toString())
        mi.outData.put("ITNO", container.get("EXITNO").toString())
        mi.outData.put("ITDS", container.get("EXITDS").toString())
        mi.outData.put("STAT", container.get("EXSTAT").toString())
        mi.outData.put("UNMS", container.get("EXUNMS").toString())
        mi.outData.put("GRTS", container.get("EXGRTS").toString())
        mi.outData.put("SZNM", container.get("EXSZNM").toString())
        mi.outData.put("CLNM", container.get("EXCLNM").toString())
        mi.outData.put("RESP", container.get("EXRESP").toString())
        mi.outData.put("QAR1", container.get("EXQAR1").toString())
        mi.outData.put("QAR2", container.get("EXQAR2").toString())
        mi.outData.put("CONO", container.get("EXCONO").toString())
        mi.outData.put("WKFW", container.get("EXWKFW").toString())
        mi.outData.put("PRTY", container.get("EXPRTY").toString())
        mi.outData.put("PMCD", container.get("EXPMCD").toString())
        mi.outData.put("PCCD", container.get("EXPCCD").toString())
        mi.outData.put("TTYP", container.get("EXTTYP").toString())
        mi.outData.put("DLIX", container.get("EXDLIX").toString())
        mi.outData.put("SMPF", container.get("EXSMPF").toString())
        mi.outData.put("DCCD", container.get("EXDCCD").toString())
        mi.outData.put("VOL3", container.get("EXVOL3").toString())
        mi.outData.put("NEWE", container.get("EXNEWE").toString())
        mi.outData.put("PCKQ", container.get("EXPCKQ").toString())
        mi.outData.put("TPQT", container.get("EXTPQT").toString())
        mi.outData.put("PCQT", container.get("EXPCQT").toString())
        mi.outData.put("SHQT", container.get("EXSHQT").toString())
        mi.outData.put("EPWT", container.get("EXEPWT").toString())
        mi.outData.put("AUWT", container.get("EXAUWT").toString())
        mi.outData.put("ACWT", container.get("EXACWT").toString())
        mi.outData.put("TDNT", container.get("EXTDNT").toString())
        mi.outData.put("CISN", container.get("EXCISN").toString())
        mi.write()
      }
    } else if (!iWHLO.trim().isEmpty() && !iRCID.trim().isEmpty() && !iLICP.trim().isEmpty() && !iORNO.trim().isEmpty() && iDLIX != 0 && !iITNO.trim().isEmpty()) {
      container.set("EXDIVI", iDIVI)
      container.set("EXWHLO", iWHLO)
      container.set("EXRCID", iRCID)
      container.set("EXLICP", iLICP)
      container.set("EXORNO", iORNO)
      container.set("EXCONO", iCONO)
      container.set("EXDLIX", iDLIX)
      container.set("EXITNO", iITNO)
      query.readAll(container, 8, pageSize, resultset)
    } else if (!iWHLO.trim().isEmpty() && !iRCID.trim().isEmpty() && !iLICP.trim().isEmpty() && !iORNO.trim().isEmpty() && iDLIX != 0) {
      container.set("EXDIVI", iDIVI)
      container.set("EXWHLO", iWHLO)
      container.set("EXRCID", iRCID)
      container.set("EXLICP", iLICP)
      container.set("EXORNO", iORNO)
      container.set("EXCONO", iCONO)
      container.set("EXDLIX", iDLIX)
      query.readAll(container, 7, pageSize, resultset)
    } else if (!iWHLO.trim().isEmpty() && !iRCID.trim().isEmpty() && !iLICP.trim().isEmpty() && !iORNO.trim().isEmpty()) {
      container.set("EXDIVI", iDIVI)
      container.set("EXWHLO", iWHLO)
      container.set("EXRCID", iRCID)
      container.set("EXLICP", iLICP)
      container.set("EXORNO", iORNO)
      container.set("EXCONO", iCONO)
      query.readAll(container, 6, pageSize, resultset)
    } else if (!iWHLO.trim().isEmpty() && !iRCID.trim().isEmpty() && !iLICP.trim().isEmpty()) {
      container.set("EXDIVI", iDIVI)
      container.set("EXWHLO", iWHLO)
      container.set("EXRCID", iRCID)
      container.set("EXLICP", iLICP)
      container.set("EXCONO", iCONO)
      query.readAll(container, 5, pageSize, resultset)
    } else if (!iWHLO.trim().isEmpty() && !iRCID.trim().isEmpty()) {
      container.set("EXDIVI", iDIVI)
      container.set("EXWHLO", iWHLO)
      container.set("EXRCID", iRCID)
      container.set("EXCONO", iCONO)
      query.readAll(container, 4, pageSize, resultset)
    } else if (!iWHLO.trim().isEmpty()) {
      container.set("EXDIVI", iDIVI)
      container.set("EXWHLO", iWHLO)
      container.set("EXCONO", iCONO)
      query.readAll(container, 3, pageSize, resultset)
    } else {
      container.set("EXCONO", iCONO)
      container.set("EXDIVI", iDIVI)
      query.readAll(container, 2, pageSize, resultset)
    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("DIVI", container.get("EXDIVI").toString())
    mi.outData.put("FACI", container.get("EXFACI").toString())
    mi.outData.put("WHLO", container.get("EXWHLO").toString())
    mi.outData.put("RCID", container.get("EXRCID").toString())
    mi.outData.put("LICP", container.get("EXLICP").toString())
    mi.outData.put("PKTS", container.get("EXPKTS").toString())
    mi.outData.put("DSNA", container.get("EXDSNA").toString())
    mi.outData.put("WHLT", container.get("EXWHLT").toString())
    mi.outData.put("SLTP", container.get("EXSLTP").toString())
    mi.outData.put("WHSL", container.get("EXWHSL").toString())
    mi.outData.put("TWSL", container.get("EXTWSL").toString())
    mi.outData.put("SLT2", container.get("EXSLT2").toString())
    mi.outData.put("CAMU", container.get("EXCAMU").toString())
    mi.outData.put("ORNO", container.get("EXORNO").toString())
    mi.outData.put("ITNO", container.get("EXITNO").toString())
    mi.outData.put("ITDS", container.get("EXITDS").toString())
    mi.outData.put("STAT", container.get("EXSTAT").toString())
    mi.outData.put("UNMS", container.get("EXUNMS").toString())
    mi.outData.put("GRTS", container.get("EXGRTS").toString())
    mi.outData.put("SZNM", container.get("EXSZNM").toString())
    mi.outData.put("CLNM", container.get("EXCLNM").toString())
    mi.outData.put("RESP", container.get("EXRESP").toString())
    mi.outData.put("QAR1", container.get("EXQAR1").toString())
    mi.outData.put("QAR2", container.get("EXQAR2").toString())
    mi.outData.put("CONO", container.get("EXCONO").toString())
    mi.outData.put("WKFW", container.get("EXWKFW").toString())
    mi.outData.put("PRTY", container.get("EXPRTY").toString())
    mi.outData.put("PMCD", container.get("EXPMCD").toString())
    mi.outData.put("PCCD", container.get("EXPCCD").toString())
    mi.outData.put("TTYP", container.get("EXTTYP").toString())
    mi.outData.put("DLIX", container.get("EXDLIX").toString())
    mi.outData.put("SMPF", container.get("EXSMPF").toString())
    mi.outData.put("DCCD", container.get("EXDCCD").toString())
    mi.outData.put("VOL3", container.get("EXVOL3").toString())
    mi.outData.put("NEWE", container.get("EXNEWE").toString())
    mi.outData.put("PCKQ", container.get("EXPCKQ").toString())
    mi.outData.put("TPQT", container.get("EXTPQT").toString())
    mi.outData.put("PCQT", container.get("EXPCQT").toString())
    mi.outData.put("SHQT", container.get("EXSHQT").toString())
    mi.outData.put("EPWT", container.get("EXEPWT").toString())
    mi.outData.put("AUWT", container.get("EXAUWT").toString())
    mi.outData.put("ACWT", container.get("EXACWT").toString())
    mi.outData.put("TDNT", container.get("EXTDNT").toString())
    mi.outData.put("CISN", container.get("EXCISN").toString())
    mi.write()

  }
}