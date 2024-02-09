/**
 * README
 * This extension is being used to Update records to EXTSOL table. 
 *
 * Name: EXT100MI.UpdSalesOrdLine
 * Description: Updating records to EXTSOL table
 * Date           Changed By                      Description
 *20230210        SuriyaN@fortude.co           Updating records to EXTSOL table
 *20240208        AbhishekA@fortude.co         Updating Validation logic
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdSalesOrdLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iORNO, iLNKY, iITNO, iITTY, iFUDS, iWHLO, iPRLV, iTXID, iUDCR, iURFT, iURFN, iUDSG, iCRCO, iCRSH, iMFNO, iUDCL, iUDDC
  private int iCONO, iPONR
  private int trimmedLNKY
  private double iORQT, iORRV, iTRQT, iORBO, iSAPR
  private boolean validInput = true

  public UpdSalesOrdLine(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iLNKY = (mi.inData.get("LNKY") == null || mi.inData.get("LNKY").trim().isEmpty()) ? "" : mi.inData.get("LNKY")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iITTY = (mi.inData.get("ITTY") == null || mi.inData.get("ITTY").trim().isEmpty()) ? "" : mi.inData.get("ITTY")
    iFUDS = (mi.inData.get("FUDS") == null || mi.inData.get("FUDS").trim().isEmpty()) ? "" : mi.inData.get("FUDS")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iPRLV = (mi.inData.get("PRLV") == null || mi.inData.get("PRLV").trim().isEmpty()) ? "" : mi.inData.get("PRLV")
    iUDCR = (mi.inData.get("UDCR") == null || mi.inData.get("UDCR").trim().isEmpty()) ? "" : mi.inData.get("UDCR")
    iURFT = (mi.inData.get("URFT") == null || mi.inData.get("URFT").trim().isEmpty()) ? "" : mi.inData.get("URFT")
    iURFN = (mi.inData.get("URFN") == null || mi.inData.get("URFN").trim().isEmpty()) ? "" : mi.inData.get("URFN")
    iUDSG = (mi.inData.get("UDSG") == null || mi.inData.get("UDSG").trim().isEmpty()) ? "" : mi.inData.get("UDSG")
    iCRCO = (mi.inData.get("CRCO") == null || mi.inData.get("CRCO").trim().isEmpty()) ? "" : mi.inData.get("CRCO")
    iCRSH = (mi.inData.get("CRSH") == null || mi.inData.get("CRSH").trim().isEmpty()) ? "" : mi.inData.get("CRSH")
    iMFNO = (mi.inData.get("MFNO") == null || mi.inData.get("MFNO").trim().isEmpty()) ? "" : mi.inData.get("MFNO")
    iUDCL = (mi.inData.get("UDCL") == null || mi.inData.get("UDCL").trim().isEmpty()) ? "" : mi.inData.get("UDCL")
    iUDDC = (mi.inData.get("UDDC") == null || mi.inData.get("UDDC").trim().isEmpty()) ? "" : mi.inData.get("UDDC")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    iTXID = (mi.inData.get("TXID") == null || mi.inData.get("TXID").trim().isEmpty()) ? 0 : mi.inData.get("TXID") as Integer
    iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
    iORQT = (mi.inData.get("ORQT") == null || mi.inData.get("ORQT").trim().isEmpty()) ? 0 : mi.inData.get("ORQT") as Double
    iORRV = (mi.inData.get("ORRV") == null || mi.inData.get("ORRV").trim().isEmpty()) ? 0 : mi.inData.get("ORRV") as Double
    iTRQT = (mi.inData.get("TRQT") == null || mi.inData.get("TRQT").trim().isEmpty()) ? 0 : mi.inData.get("TRQT") as Double
    iORBO = (mi.inData.get("ORBO") == null || mi.inData.get("ORBO").trim().isEmpty()) ? 0 : mi.inData.get("ORBO") as Double
    iSAPR = (mi.inData.get("SAPR") == null || mi.inData.get("SAPR").trim().isEmpty()) ? 0 : mi.inData.get("SAPR") as Double

    validateInput(iCONO, iORNO, iWHLO, iLNKY, iPONR, iITNO)
    if (validInput) {
      updateRecord()
    }

  }
  /**
   *Validate inputs
   * @params int CONO ,String ORNO,String WHLO,String LNKY, String PONR, String ITNO
   * @return 
   */
  private boolean validateInput(int CONO, String ORNO, String WHLO, String LNKY, int PONR, String ITNO) {
    //Validate Company Number
    Map < String, String > params = ["CONO": CONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + CONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //trimmedLNKY=LNKY.toInteger()
    //Validate Order Number
    params = ["CONO": CONO.toString().trim(), "ORNO": ORNO.toString().trim(), "LNKY": LNKY.toString().trim(), "PONR": PONR.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.ITNO == null) {
        mi.error("Invalid Order Line " + ORNO)
        validInput = false
        return false
      }
    }
    miCaller.call("EXT100MI", "GetSalesOrdLine", params, callback)

    if (!WHLO.trim().isEmpty()) {
      //Validate Warehouse Number
      params = ["CONO": CONO.toString().trim(), "WHLO": WHLO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.WHLO == null) {
          mi.error("Invalid Warehouse Number " + WHLO)
          validInput = false
          return false
        }
      }
      miCaller.call("MMS005MI", "GetWarehouse", params, callback)
    }

    if (!ITNO.trim().isEmpty()) {
      //Validate Item Number
      params = ["CONO": CONO.toString().trim(), "ITNO": ITNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ITNO == null) {
          mi.error("Invalid Item Number " + ITNO)
          validInput = false
          return false
        }
      }
      miCaller.call("MMS200MI", "Get", params, callback)
    }

    if (!iITTY.trim().isEmpty()) {
      //Validate Item Type
      params = ["CONO": CONO.toString().trim(), "ITTY": iITTY.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.TX40 == null) {
          mi.error("Invalid Item Type " + iITTY)
          validInput = false
          return false
        }
      }
      miCaller.call("CRS040MI", "GetItmType", params, callback)
    }
  }

  /**
   *Update records to EXTSOL table
   * @params 
   * @return 
   */
  public void updateRecord() {
    DBAction query = database.table("EXTSOL").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXORNO", iORNO)
    container.set("EXLNKY", iLNKY)
    container.set("EXPONR", iPONR)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }

  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
      int CHNO = lockedResult.get("EXCHNO")
    if (!iITNO.trim().isEmpty()) {
      if (iITNO.trim().equals("?")) {
        lockedResult.set("EXITNO", "")
      } else {
        lockedResult.set("EXITNO", iITNO)
      }
    }
    if (!iITTY.trim().isEmpty()) {
      if (iITTY.trim().equals("?")) {
        lockedResult.set("EXITTY", "")
      } else {
        lockedResult.set("EXITTY", iITTY)
      }
    }
    if (!iFUDS.trim().isEmpty()) {
      if (iFUDS.trim().equals("?")) {
        lockedResult.set("EXFUDS", "")
      } else {
        lockedResult.set("EXFUDS", iFUDS)
      }
    }
    if (!iWHLO.trim().isEmpty()) {
      if (iWHLO.trim().equals("?")) {
        lockedResult.set("EXWHLO", "")
      } else {
        lockedResult.set("EXWHLO", iWHLO)
      }
    }
    if (!iPRLV.trim().isEmpty()) {
      if (iPRLV.trim().equals("?")) {
        lockedResult.set("EXPRLV", "")
      } else {
        lockedResult.set("EXPRLV", iPRLV)
      }
    }

    if (!iUDCR.trim().isEmpty()) {
      if (iUDCR.trim().equals("?")) {
        lockedResult.set("EXUDCR", "")
      } else {
        lockedResult.set("EXUDCR", iUDCR)
      }
    }
    if (!iURFT.trim().isEmpty()) {
      if (iURFT.trim().equals("?")) {
        lockedResult.set("EXURFT", "")
      } else {
        lockedResult.set("EXURFT", iURFT)
      }
    }
    if (!iURFN.trim().isEmpty()) {
      if (iURFN.trim().equals("?")) {
        lockedResult.set("EXURFN", "")
      } else {
        lockedResult.set("EXURFN", iURFN)
      }
    }
    if (!iUDSG.trim().isEmpty()) {
      if (iUDSG.trim().equals("?")) {
        lockedResult.set("EXUDSG", "")
      } else {
        lockedResult.set("EXUDSG", iUDSG)
      }
    }
    if (!iCRCO.trim().isEmpty()) {
      if (iCRCO.trim().equals("?")) {
        lockedResult.set("EXCRCO", "")
      } else {
        lockedResult.set("EXCRCO", iCRCO)
      }
    }
    if (!iCRSH.trim().isEmpty()) {
      if (iCRSH.trim().equals("?")) {
        lockedResult.set("EXCRSH", "")
      } else {
        lockedResult.set("EXCRSH", iCRSH)
      }
    }
    if (!iMFNO.trim().isEmpty()) {
      if (iMFNO.trim().equals("?")) {
        lockedResult.set("EXMFNO", "")
      } else {
        lockedResult.set("EXMFNO", iMFNO)
      }
    }
    if (!iUDCL.trim().isEmpty()) {
      if (iUDCL.trim().equals("?")) {
        lockedResult.set("EXUDCL", "")
      } else {
        lockedResult.set("EXUDCL", iUDCL)
      }
    }
    if (!iUDDC.trim().isEmpty()) {
      if (iUDDC.trim().equals("?")) {
        lockedResult.set("EXUDDC", "")
      } else {
        lockedResult.set("EXUDDC", iUDDC)
      }
    }

    if (mi.inData.get("TXID") != null && !mi.inData.get("TXID").trim().isEmpty()) {
      lockedResult.set("EXTXID", iTXID)
    }

    if (mi.inData.get("ITTY") != null && !mi.inData.get("ITTY").trim().isEmpty()) {
      lockedResult.set("EXITTY", iITTY)
    }

    if (mi.inData.get("ORQT") != null && !mi.inData.get("ORQT").trim().isEmpty()) {
      lockedResult.set("EXORQT", iORQT)
    }

    if (mi.inData.get("ORRV") != null && !mi.inData.get("ORRV").trim().isEmpty()) {
      lockedResult.set("EXORRV", iORRV)
    }

    if (mi.inData.get("TRQT") != null && !mi.inData.get("TRQT").trim().isEmpty()) {
      lockedResult.set("EXTRQT", iTRQT)
    }

    if (mi.inData.get("ORBO") != null && !mi.inData.get("ORBO").trim().isEmpty()) {
      lockedResult.set("EXORBO", iORBO)
    }

    if (mi.inData.get("SAPR") != null && !mi.inData.get("SAPR").trim().isEmpty()) {
      lockedResult.set("EXSAPR", iSAPR)
    }

    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.set("EXCHNO", CHNO+1)
    lockedResult.update()
  }
}