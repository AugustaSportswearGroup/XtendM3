/**
 * README
 * This extension is being used to Add records to EXTCOM table. 
 *
 * Name: EXT004MI.AddLucasLines
 * Description: Adding records to EXTCOM table 
 * Date	      Changed By                      Description
 *20230627  SuriyaN@fortude.co     Adding records to EXTCOM table 
 *20240212  AbhishekA@fortude.co   Updating Validation logic
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddLucasLines extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iRCID, iLICP, iPKTS, iDSNA, iWHLT, iSLTP, iWHSL, iTWSL, iSLT2, iCAMU, iORNO, iITNO, iITDS, iSTAT, iUNMS, iGRTS, iSZNM, iCLNM, iRESP, iQAR1, iQAR2, iFACI, iCISN
  private int iCONO, iWKFW, iPRTY, iPMCD, iPCCD, iTTYP, iSMPF, iDCCD
  private double iTDNT, iVOL3, iNEWE, iPCKQ, iTPQT, iPCQT, iSHQT, iEPWT, iAUWT, iACWT
  private long iDLIX
  private boolean validInput = true

  public AddLucasLines(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iPKTS = (mi.inData.get("PKTS") == null || mi.inData.get("PKTS").trim().isEmpty()) ? "" : mi.inData.get("PKTS")
    iDSNA = (mi.inData.get("DSNA") == null || mi.inData.get("DSNA").trim().isEmpty()) ? "" : mi.inData.get("DSNA")
    iWHLT = (mi.inData.get("WHLT") == null || mi.inData.get("WHLT").trim().isEmpty()) ? "" : mi.inData.get("WHLT")
    iSLTP = (mi.inData.get("SLTP") == null || mi.inData.get("SLTP").trim().isEmpty()) ? "" : mi.inData.get("SLTP")
    iWHSL = (mi.inData.get("WHSL") == null || mi.inData.get("WHSL").trim().isEmpty()) ? "" : mi.inData.get("WHSL")
    iTWSL = (mi.inData.get("TWSL") == null || mi.inData.get("TWSL").trim().isEmpty()) ? "" : mi.inData.get("TWSL")
    iSLT2 = (mi.inData.get("SLT2") == null || mi.inData.get("SLT2").trim().isEmpty()) ? "" : mi.inData.get("SLT2")
    iCAMU = (mi.inData.get("CAMU") == null || mi.inData.get("CAMU").trim().isEmpty()) ? "" : mi.inData.get("CAMU")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iITDS = (mi.inData.get("ITDS") == null || mi.inData.get("ITDS").trim().isEmpty()) ? "" : mi.inData.get("ITDS")
    iSTAT = (mi.inData.get("STAT") == null || mi.inData.get("STAT").trim().isEmpty()) ? "" : mi.inData.get("STAT")
    iUNMS = (mi.inData.get("UNMS") == null || mi.inData.get("UNMS").trim().isEmpty()) ? "" : mi.inData.get("UNMS")
    iGRTS = (mi.inData.get("GRTS") == null || mi.inData.get("GRTS").trim().isEmpty()) ? "" : mi.inData.get("GRTS")
    iSZNM = (mi.inData.get("SZNM") == null || mi.inData.get("SZNM").trim().isEmpty()) ? "" : mi.inData.get("SZNM")
    iCLNM = (mi.inData.get("CLNM") == null || mi.inData.get("CLNM").trim().isEmpty()) ? "" : mi.inData.get("CLNM")
    iRESP = (mi.inData.get("RESP") == null || mi.inData.get("RESP").trim().isEmpty()) ? "" : mi.inData.get("RESP")
    iQAR1 = (mi.inData.get("QAR1") == null || mi.inData.get("QAR1").trim().isEmpty()) ? "" : mi.inData.get("QAR1")
    iQAR2 = (mi.inData.get("QAR2") == null || mi.inData.get("QAR2").trim().isEmpty()) ? "" : mi.inData.get("QAR2")
    iFACI = (mi.inData.get("FACI") == null || mi.inData.get("FACI").trim().isEmpty()) ? "" : mi.inData.get("FACI")
    iCISN = (mi.inData.get("CISN") == null || mi.inData.get("CISN").trim().isEmpty()) ? 0 : mi.inData.get("CISN")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iWKFW = (mi.inData.get("WKFW") == null || mi.inData.get("WKFW").trim().isEmpty()) ? 0 : mi.inData.get("WKFW") as Integer
    iPRTY = (mi.inData.get("PRTY") == null || mi.inData.get("PRTY").trim().isEmpty()) ? 0 : mi.inData.get("PRTY") as Integer
    iPMCD = (mi.inData.get("PMCD") == null || mi.inData.get("PMCD").trim().isEmpty()) ? 0 : mi.inData.get("PMCD") as Integer
    iPCCD = (mi.inData.get("PCCD") == null || mi.inData.get("PCCD").trim().isEmpty()) ? 0 : mi.inData.get("PCCD") as Integer
    iTTYP = (mi.inData.get("TTYP") == null || mi.inData.get("TTYP").trim().isEmpty()) ? 0 : mi.inData.get("TTYP") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iSMPF = (mi.inData.get("SMPF") == null || mi.inData.get("SMPF").trim().isEmpty()) ? 0 : mi.inData.get("SMPF") as Integer
    iDCCD = (mi.inData.get("DCCD") == null || mi.inData.get("DCCD").trim().isEmpty()) ? 0 : mi.inData.get("DCCD") as Integer
    iVOL3 = (mi.inData.get("VOL3") == null || mi.inData.get("VOL3").trim().isEmpty()) ? 0 : mi.inData.get("VOL3") as Double
    iNEWE = (mi.inData.get("NEWE") == null || mi.inData.get("NEWE").trim().isEmpty()) ? 0 : mi.inData.get("NEWE") as Double
    iPCKQ = (mi.inData.get("PCKQ") == null || mi.inData.get("PCKQ").trim().isEmpty()) ? 0 : mi.inData.get("PCKQ") as Double
    iTPQT = (mi.inData.get("TPQT") == null || mi.inData.get("TPQT").trim().isEmpty()) ? 0 : mi.inData.get("TPQT") as Double
    iPCQT = (mi.inData.get("PCQT") == null || mi.inData.get("PCQT").trim().isEmpty()) ? 0 : mi.inData.get("PCQT") as Double
    iSHQT = (mi.inData.get("SHQT") == null || mi.inData.get("SHQT").trim().isEmpty()) ? 0 : mi.inData.get("SHQT") as Double
    iEPWT = (mi.inData.get("EPWT") == null || mi.inData.get("EPWT").trim().isEmpty()) ? 0 : mi.inData.get("EPWT") as Double
    iAUWT = (mi.inData.get("AUWT") == null || mi.inData.get("AUWT").trim().isEmpty()) ? 0 : mi.inData.get("AUWT") as Double
    iACWT = (mi.inData.get("ACWT") == null || mi.inData.get("ACWT").trim().isEmpty()) ? 0 : mi.inData.get("ACWT") as Double
    iTDNT = (mi.inData.get("TDNT") == null || mi.inData.get("TDNT").trim().isEmpty()) ? 0 : mi.inData.get("TDNT") as Double

    validateInput()
    if (validInput) {
      insertRecord()
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

    //Validate Item Number
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
          return false
        }
      }
      miCaller.call("MMS100MI", "GetHead", params, callback)
    }

    //Validate Delivery Number
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

    if (!iFACI.trim().isEmpty()) {
      //Validate Facility Number
      params = ["CONO": iCONO.toString().trim(), "FACI": iFACI.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.FACI == null) {
          mi.error("Invalid Facility Number " + iFACI)
          validInput = false
          return false
        }
      }
      miCaller.call("CRS008MI", "Get", params, callback)
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
   *Insert records to EXTCOM table
   * @params 
   * @return 
   */
  public void insertRecord() {
    DBAction action = database.table("EXTCOM").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXDIVI", iDIVI)
    query.set("EXWHLO", iWHLO)
    query.set("EXRCID", iRCID)
    query.set("EXLICP", iLICP)
    query.set("EXPKTS", iPKTS)
    query.set("EXDSNA", iDSNA)
    query.set("EXWHLT", iWHLT)
    query.set("EXSLTP", iSLTP)
    query.set("EXWHSL", iWHSL)
    query.set("EXTWSL", iTWSL)
    query.set("EXSLT2", iSLT2)
    query.set("EXCAMU", iCAMU)
    query.set("EXORNO", iORNO)
    query.set("EXITNO", iITNO)
    query.set("EXITDS", iITDS)
    query.set("EXSTAT", iSTAT)
    query.set("EXUNMS", iUNMS)
    query.set("EXGRTS", iGRTS)
    query.set("EXSZNM", iSZNM)
    query.set("EXCLNM", iCLNM)
    query.set("EXRESP", iRESP)
    query.set("EXQAR1", iQAR1)
    query.set("EXQAR2", iQAR2)
    query.set("EXFACI", iFACI)
    query.set("EXCISN", iCISN)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXWKFW", iWKFW as Integer)
    query.set("EXPRTY", iPRTY as Integer)
    query.set("EXPMCD", iPMCD as Integer)
    query.set("EXPCCD", iPCCD as Integer)
    query.set("EXTTYP", iTTYP as Integer)
    query.set("EXDLIX", iDLIX as Long)
    query.set("EXSMPF", iSMPF as Integer)
    query.set("EXDCCD", iDCCD as Integer)
    query.set("EXVOL3", iVOL3 as Double)
    query.set("EXNEWE", iNEWE as Double)
    query.set("EXPCKQ", iPCKQ as Double)
    query.set("EXTPQT", iTPQT as Double)
    query.set("EXPCQT", iPCQT as Double)
    query.set("EXSHQT", iSHQT as Double)
    query.set("EXEPWT", iEPWT as Double)
    query.set("EXAUWT", iAUWT as Double)
    query.set("EXACWT", iACWT as Double)
    query.set("EXTDNT", iTDNT as Double)
    query.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXCHNO", 0)
    query.set("EXCHID", program.getUser())
    action.insert(query, recordExists)
  }

  Closure recordExists = {
    mi.error("Record Already Exists")
    return
  }
}