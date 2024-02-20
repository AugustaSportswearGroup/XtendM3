/**
 * README
 * This extension is being used to Add records to EXTMTK table. 
 *
 * Name: EXT004MI.AddLucasData
 * Description: Adding records to EXTMTK table 
 * Date	      Changed By                      Description
 *20230622  SuriyaN@fortude.co     Adding records to EXTMTK table 
 *20240212  AbhishekA@fortude.co   Updating Validation logic
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddLucasData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iFACI, iWHLO, iRCID, iLICP, iORNO, iSTAT, iLMFS, iLMFD, iCUNO, iMODL, iTEPY, iTEPA, iWGFG, iQAFG, iSMFG, iONFM, iPKDV, iPSDV, iPTDV, iLNDV, iFD01, iFD02, iZONE, iZON2, iUD01, iUD02
  private int iCONO, iTTYP, iBXNO, iWGTO, iTXID
  private long iDLIX
  private Double iESTW, iACTW, iUD03
  boolean validInput = true

  public AddLucasData(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iFACI = (mi.inData.get("FACI") == null || mi.inData.get("FACI").trim().isEmpty()) ? "" : mi.inData.get("FACI")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iRCID = (mi.inData.get("RCID") == null || mi.inData.get("RCID").trim().isEmpty()) ? "" : mi.inData.get("RCID")
    iLICP = (mi.inData.get("LICP") == null || mi.inData.get("LICP").trim().isEmpty()) ? "" : mi.inData.get("LICP")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iSTAT = (mi.inData.get("STAT") == null || mi.inData.get("STAT").trim().isEmpty()) ? "" : mi.inData.get("STAT")
    iSTAT = (mi.inData.get("STAT") == null || mi.inData.get("STAT").trim().isEmpty()) ? "" : mi.inData.get("STAT")
    iLMFS = (mi.inData.get("LMFS") == null || mi.inData.get("LMFS").trim().isEmpty()) ? "" : mi.inData.get("LMFS")
    iLMFD = (mi.inData.get("LMFD") == null || mi.inData.get("LMFD").trim().isEmpty()) ? "" : mi.inData.get("LMFD")
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
    iMODL = (mi.inData.get("MODL") == null || mi.inData.get("MODL").trim().isEmpty()) ? "" : mi.inData.get("MODL")
    iTEPY = (mi.inData.get("TEPY") == null || mi.inData.get("TEPY").trim().isEmpty()) ? "" : mi.inData.get("TEPY")
    iTEPA = (mi.inData.get("TEPA") == null || mi.inData.get("TEPA").trim().isEmpty()) ? "" : mi.inData.get("TEPA")
    iWGFG = (mi.inData.get("WGFG") == null || mi.inData.get("WGFG").trim().isEmpty()) ? "0" : mi.inData.get("WGFG")
    iQAFG = (mi.inData.get("QAFG") == null || mi.inData.get("QAFG").trim().isEmpty()) ? "0" : mi.inData.get("QAFG")
    iSMFG = (mi.inData.get("SMFG") == null || mi.inData.get("SMFG").trim().isEmpty()) ? "0" : mi.inData.get("SMFG")
    iONFM = (mi.inData.get("ONFM") == null || mi.inData.get("ONFM").trim().isEmpty()) ? "0" : mi.inData.get("ONFM")
    iPKDV = (mi.inData.get("PKDV") == null || mi.inData.get("PKDV").trim().isEmpty()) ? "" : mi.inData.get("PKDV")
    iPSDV = (mi.inData.get("PSDV") == null || mi.inData.get("PSDV").trim().isEmpty()) ? "" : mi.inData.get("PSDV")
    iPTDV = (mi.inData.get("PTDV") == null || mi.inData.get("PTDV").trim().isEmpty()) ? "" : mi.inData.get("PTDV")
    iLNDV = (mi.inData.get("LNDV") == null || mi.inData.get("LNDV").trim().isEmpty()) ? "" : mi.inData.get("LNDV")
    iFD01 = (mi.inData.get("FD01") == null || mi.inData.get("FD01").trim().isEmpty()) ? "" : mi.inData.get("FD01")
    iFD02 = (mi.inData.get("FD02") == null || mi.inData.get("FD02").trim().isEmpty()) ? "" : mi.inData.get("FD02")
    iZONE = (mi.inData.get("ZONE") == null || mi.inData.get("ZONE").trim().isEmpty()) ? "0" : mi.inData.get("ZONE")
    iZON2 = (mi.inData.get("ZON2") == null || mi.inData.get("ZON2").trim().isEmpty()) ? "0" : mi.inData.get("ZON2")
    iUD01 = (mi.inData.get("UD01") == null || mi.inData.get("UD01").trim().isEmpty()) ? "" : mi.inData.get("UD01")
    iUD02 = (mi.inData.get("UD02") == null || mi.inData.get("UD02").trim().isEmpty()) ? "" : mi.inData.get("UD02")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iTTYP = (mi.inData.get("TTYP") == null || mi.inData.get("TTYP").trim().isEmpty()) ? 0 : mi.inData.get("TTYP") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iBXNO = (mi.inData.get("BXNO") == null || mi.inData.get("BXNO").trim().isEmpty()) ? 0 : mi.inData.get("BXNO") as Integer
    iESTW = (mi.inData.get("ESTW") == null || mi.inData.get("ESTW").trim().isEmpty()) ? 0 : mi.inData.get("ESTW") as Double
    iACTW = (mi.inData.get("ACTW") == null || mi.inData.get("ACTW").trim().isEmpty()) ? 0 : mi.inData.get("ACTW") as Double
    iWGTO = (mi.inData.get("WGTO") == null || mi.inData.get("WGTO").trim().isEmpty()) ? 0 : mi.inData.get("WGTO") as Integer
    iTXID = (mi.inData.get("TXID") == null || mi.inData.get("TXID").trim().isEmpty()) ? 0 : mi.inData.get("TXID") as Integer
    iUD03 = (mi.inData.get("UD03") == null || mi.inData.get("UD03").trim().isEmpty()) ? 0 : mi.inData.get("UD03") as Double

    validateInput()
    if (validInput) {
      insertRecord()
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

    if (!iCUNO.trim().isEmpty()) {
      //Validate Customer Number
      params = ["CONO": iCONO.toString().trim(), "CUNO": iCUNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + iCUNO)
          validInput = false
          return
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }

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

  }

  /**
   *Insert records to EXTMTK table
   * @params
   * @return
   */
  public void insertRecord() {
    DBAction action = database.table("EXTMTK").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXDIVI", iDIVI)
    query.set("EXFACI", iFACI)
    query.set("EXWHLO", iWHLO)
    query.set("EXRCID", iRCID)
    query.set("EXLICP", iLICP)
    query.set("EXORNO", iORNO)
    query.set("EXSTAT", iSTAT)
    query.set("EXSTAT", iSTAT)
    query.set("EXLMFS", iLMFS)
    query.set("EXLMFD", iLMFD)
    query.set("EXCUNO", iCUNO)
    query.set("EXMODL", iMODL)
    query.set("EXTEPY", iTEPY)
    query.set("EXTEPA", iTEPA)
    query.set("EXWGFG", iWGFG)
    query.set("EXQAFG", iQAFG)
    query.set("EXSMFG", iSMFG)
    query.set("EXONFM", iONFM)
    query.set("EXPKDV", iPKDV)
    query.set("EXPSDV", iPSDV)
    query.set("EXPTDV", iPTDV)
    query.set("EXLNDV", iLNDV)
    query.set("EXFD01", iFD01)
    query.set("EXFD02", iFD02)
    query.set("EXZONE", iZONE)
    query.set("EXZON2", iZON2)
    query.set("EXUD01", iUD01)
    query.set("EXUD02", iUD02)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXTTYP", iTTYP as Integer)
    query.set("EXDLIX", iDLIX as Long)
    query.set("EXBXNO", iBXNO as Integer)
    query.set("EXESTW", iESTW as Double)
    query.set("EXACTW", iACTW as Double)
    query.set("EXWGTO", iWGTO as Integer)
    query.set("EXTXID", iTXID as Integer)
    query.set("EXUD03", iUD03 as Double)
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