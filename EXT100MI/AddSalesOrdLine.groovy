/**
 * README
 * This extension is being used to Add records to EXTSOL table. 
 *
 * Name: EXT100MI.AddSalesOrdLine
 * Description: Adding records to EXTSOL table
 * Date	          Changed By                      Description
 *20230802        SuriyaN@fortude.co      Adding records to EXTSOL table
 *20240208        AbhishekA@fortude.co    Updating Validation logic
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddSalesOrdLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iORNO, iLNKY, iITNO, iITTY, iFUDS, iWHLO, iPRLV, iUDCR, iURFT, iURFN, iUDSG, iCRCO, iCRSH, iMFNO, iUDCL, iUDDC
  private int iCONO, iPONR, iTXID
  private int trimmedLNKY
  private double iORQT, iORRV, iTRQT, iORBO, iSAPR
  private boolean validInput = true
  public AddSalesOrdLine(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iPRLV = (mi.inData.get("PRLV") == null || mi.inData.get("PRLV").trim().isEmpty()) ? "0" : mi.inData.get("PRLV")
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
    iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
    iTXID = (mi.inData.get("TXID") == null || mi.inData.get("TXID").trim().isEmpty()) ? 0 : mi.inData.get("TXID") as Integer
    iORQT = (mi.inData.get("ORQT") == null || mi.inData.get("ORQT").trim().isEmpty()) ? 0 : mi.inData.get("ORQT") as Double
    iORRV = (mi.inData.get("ORRV") == null || mi.inData.get("ORRV").trim().isEmpty()) ? 0 : mi.inData.get("ORRV") as Double
    iTRQT = (mi.inData.get("TRQT") == null || mi.inData.get("TRQT").trim().isEmpty()) ? 0 : mi.inData.get("TRQT") as Double
    iORBO = (mi.inData.get("ORBO") == null || mi.inData.get("ORBO").trim().isEmpty()) ? 0 : mi.inData.get("ORBO") as Double
    iSAPR = (mi.inData.get("SAPR") == null || mi.inData.get("SAPR").trim().isEmpty()) ? 0 : mi.inData.get("SAPR") as Double

    validateInput(iCONO, iORNO, iWHLO, iLNKY, iITNO)
    if (validInput) {
      insertRecord()
    }
  }

  /**
   *Validate inputs
   * @params int CONO ,String ORNO,String WHLO,String LNKY
   * @return 
   */
  private void validateInput(int CONO, String ORNO, String WHLO, String LNKY, String ITNO) {

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

    //Validate Header Record
    params = ["CONO": CONO.toString().trim(), "ORNO": ORNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.ORNO == null) {
        mi.error("Header Record doesn't exist for the order " + ORNO)
        validInput = false
        return 
      }
    }
    miCaller.call("EXT100MI", "GetSalesOrdHead", params, callback)

    if (!WHLO.trim().isEmpty()) {
      //Validate Warehouse Number
      params = ["CONO": CONO.toString().trim(), "WHLO": WHLO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.WHLO == null) {
          mi.error("Invalid Warehouse Number " + WHLO)
          validInput = false
          return 
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
          return 
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
          return 
        }
      }
      miCaller.call("CRS040MI", "GetItmType", params, callback)
    }

  }

  /**
   *Insert records to EXTSOL table
   * @params
   * @return
   */
  public void insertRecord() {
    DBAction action = database.table("EXTSOL").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXORNO", iORNO)
    query.set("EXLNKY", iLNKY)
    query.set("EXITNO", iITNO)
    query.set("EXITTY", iITTY)
    query.set("EXFUDS", iFUDS)
    query.set("EXWHLO", iWHLO)
    query.set("EXPRLV", iPRLV.toString())
    query.set("EXUDCR", iUDCR)
    query.set("EXURFT", iURFT)
    query.set("EXURFN", iURFN)
    query.set("EXUDSG", iUDSG)
    query.set("EXCRCO", iCRCO)
    query.set("EXCRSH", iCRSH)
    query.set("EXMFNO", iMFNO)
    query.set("EXUDCL", iUDCL)
    query.set("EXUDDC", iUDDC)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXPONR", iCONO as Integer)
    query.set("EXTXID", iCONO as Integer)
    query.set("EXORQT", iORQT as Double)
    query.set("EXORRV", iORRV as Double)
    query.set("EXTRQT", iTRQT as Double)
    query.set("EXORBO", iORBO as Double)
    query.set("EXSAPR", iSAPR as Double)
    query.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXCHID", program.getUser())
    query.set("EXCHNO", 0)
    action.insert(query, recordExists)
  }

  Closure recordExists = {
    mi.error("Record Already Exists")
    return
  }
}