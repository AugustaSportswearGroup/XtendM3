/**
 * README
 * This extension is being used to Update records to EXTMKT table. 
 *
 * Name: EXT004MI.UpdLucasData
 * Description: Updating records to EXTMKT table
 * Date       Changed By                      Description
 *20230622  SuriyaN@fortude.co    Updating records to EXTMKT table
 *20240212  AbhishekA@fortude.co  Updating Validation logic
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdMLucasData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iFACI, iWHLO, iRCID, iLICP, iORNO, iSTAT, iLMFS, iLMFD, iCUNO, iMODL, iTEPY, iTEPA, iWGFG, iQAFG, iSMFG, iONFM, iPKDV, iPSDV, iPTDV, iLNDV, iFD01, iFD02, iZONE, iZON2, iUD01, iUD02
  private int iCONO, iTTYP, iBXNO, iWGTO, iTXID, iUD03, maxPageSize = 10000, count = 0
  private long iDLIX
  boolean validInput = true
  private Double iESTW, iACTW

  public UpdMLucasData(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iUD03 = (mi.inData.get("UD03") == null || mi.inData.get("UD03").trim().isEmpty()) ? 0 : mi.inData.get("UD03") as Integer

    validateInput()
    if (validInput) {
      updateRecord()
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
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    if (!iCUNO.trim().isEmpty()) {
      //Validate Customer Number
      params = ["CONO": iCONO.toString().trim(), "CUNO": iCUNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + iCUNO)
          validInput = false
          return false
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
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
            return false
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

    if (iDLIX != 0) {
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
  }

  /**
   *Update records to EXTMTK table
   * @params 
   * @return 
   */
  public void updateRecord() {
    DBAction query = database.table("EXTMTK").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXRCID", iRCID)
    query.readAllLock(container, 3, updateCallBack)
    if (count == 0) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    count = count + 1
    int CHNO = lockedResult.get("EXCHNO")
    if (!iFACI.trim().isEmpty()) {
      if (iFACI.trim().equals("?")) {
        lockedResult.set("EXFACI", "")
      } else {
        lockedResult.set("EXFACI", iFACI)
      }
    }

    if (!iSTAT.trim().isEmpty()) {
      if (iSTAT.trim().equals("?")) {
        lockedResult.set("EXSTAT", "")
      } else {
        lockedResult.set("EXSTAT", iSTAT)
      }
    }
    if (!iLMFS.trim().isEmpty()) {
      if (iLMFS.trim().equals("?")) {
        lockedResult.set("EXLMFS", "")
      } else {
        lockedResult.set("EXLMFS", iLMFS)
      }
    }
    if (!iLMFD.trim().isEmpty()) {
      if (iLMFD.trim().equals("?")) {
        lockedResult.set("EXLMFD", "")
      } else {
        lockedResult.set("EXLMFD", iLMFD)
      }
    }
    if (!iCUNO.trim().isEmpty()) {
      if (iCUNO.trim().equals("?")) {
        lockedResult.set("EXCUNO", "")
      } else {
        lockedResult.set("EXCUNO", iCUNO)
      }
    }
    if (!iMODL.trim().isEmpty()) {
      if (iMODL.trim().equals("?")) {
        lockedResult.set("EXMODL", "")
      } else {
        lockedResult.set("EXMODL", iMODL)
      }
    }
    if (!iTEPY.trim().isEmpty()) {
      if (iTEPY.trim().equals("?")) {
        lockedResult.set("EXTEPY", "")
      } else {
        lockedResult.set("EXTEPY", iTEPY)
      }
    }
    if (!iTEPA.trim().isEmpty()) {
      if (iTEPA.trim().equals("?")) {
        lockedResult.set("EXTEPA", "")
      } else {
        lockedResult.set("EXTEPA", iTEPA)
      }
    }
    if (!iWGFG.trim().isEmpty()) {
      if (iWGFG.trim().equals("?")) {
        lockedResult.set("EXWGFG", "")
      } else {
        lockedResult.set("EXWGFG", iWGFG)
      }
    }
    if (!iQAFG.trim().isEmpty()) {
      if (iQAFG.trim().equals("?")) {
        lockedResult.set("EXQAFG", "")
      } else {
        lockedResult.set("EXQAFG", iQAFG)
      }
    }
    if (!iSMFG.trim().isEmpty()) {
      if (iSMFG.trim().equals("?")) {
        lockedResult.set("EXSMFG", "")
      } else {
        lockedResult.set("EXSMFG", iSMFG)
      }
    }
    if (!iONFM.trim().isEmpty()) {
      if (iONFM.trim().equals("?")) {
        lockedResult.set("EXONFM", "")
      } else {
        lockedResult.set("EXONFM", iONFM)
      }
    }
    if (!iPKDV.trim().isEmpty()) {
      if (iPKDV.trim().equals("?")) {
        lockedResult.set("EXPKDV", "")
      } else {
        lockedResult.set("EXPKDV", iPKDV)
      }
    }
    if (!iPSDV.trim().isEmpty()) {
      if (iPSDV.trim().equals("?")) {
        lockedResult.set("EXPSDV", "")
      } else {
        lockedResult.set("EXPSDV", iPSDV)
      }
    }
    if (!iPTDV.trim().isEmpty()) {
      if (iPTDV.trim().equals("?")) {
        lockedResult.set("EXPTDV", "")
      } else {
        lockedResult.set("EXPTDV", iPTDV)
      }
    }
    if (!iLNDV.trim().isEmpty()) {
      if (iLNDV.trim().equals("?")) {
        lockedResult.set("EXLNDV", "")
      } else {
        lockedResult.set("EXLNDV", iLNDV)
      }
    }
    if (!iFD01.trim().isEmpty()) {
      if (iFD01.trim().equals("?")) {
        lockedResult.set("EXFD01", "")
      } else {
        lockedResult.set("EXFD01", iFD01)
      }
    }
    if (!iFD02.trim().isEmpty()) {
      if (iFD02.trim().equals("?")) {
        lockedResult.set("EXFD02", "")
      } else {
        lockedResult.set("EXFD02", iFD02)
      }
    }
    if (!iZONE.trim().isEmpty()) {
      if (iZONE.trim().equals("?")) {
        lockedResult.set("EXZONE", "")
      } else {
        lockedResult.set("EXZONE", iZONE)
      }
    }
    if (!iZON2.trim().isEmpty()) {
      if (iZON2.trim().equals("?")) {
        lockedResult.set("EXZON2", "")
      } else {
        lockedResult.set("EXZON2", iZON2)
      }
    }
    if (!iUD01.trim().isEmpty()) {
      if (iUD01.trim().equals("?")) {
        lockedResult.set("EXUD01", "")
      } else {
        lockedResult.set("EXUD01", iUD01)
      }
    }
    if (!iUD02.trim().isEmpty()) {
      if (iUD02.trim().equals("?")) {
        lockedResult.set("EXUD02", "")
      } else {
        lockedResult.set("EXUD02", iUD02)
      }
    }
    if (mi.inData.get("BXNO") != null && !mi.inData.get("BXNO").trim().isEmpty()) {
      lockedResult.set("EXBXNO", iBXNO as Integer)
    }
    if (mi.inData.get("ESTW") != null && !mi.inData.get("ESTW").trim().isEmpty()) {
      lockedResult.set("EXESTW", iESTW as Double)
    }
    if (mi.inData.get("ACTW") != null && !mi.inData.get("ACTW").trim().isEmpty()) {
      lockedResult.set("EXACTW", iACTW as Double)
    }
    if (mi.inData.get("WGTO") != null && !mi.inData.get("WGTO").trim().isEmpty()) {
      lockedResult.set("EXWGTO", iWGTO as Integer)
    }
    if (mi.inData.get("TXID") != null && !mi.inData.get("TXID").trim().isEmpty()) {
      lockedResult.set("EXTXID", iTXID as Integer)
    }
    if (mi.inData.get("UD03") != null && !mi.inData.get("UD03").trim().isEmpty()) {
      lockedResult.set("EXUD03", iUD03 as Integer)
    }
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}