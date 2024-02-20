/**
 * README
 * This extension is being used to Add records to EXTNSX table. 
 *
 * Name: EXT450MI.AddLabelFile
 * Description: Adding records to EXTNSX table
 * Date	      Changed By                      Description
 *23-12-21    AbhishekA@fortude.co         Adding records to EXTNSX table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddLabelFile extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iTYPE, iPUNO, iSUDO, iSSCC, iHDPR, iOPTY, iTX30, iOPTX, iITDS, iWHLO, iABFC
  private int iCONO
  private double iDELV, iLNQT
  private boolean validInput = true

  public AddLabelFile(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
    this.mi = mi
    this.database = database
    this.program = program
    this.miCaller = miCaller
    this.utility = utility
  }
  /**
   ** Main function
   * @param
   * @return
   */
  public void main() {
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iTYPE = (mi.inData.get("TYPE") == null || mi.inData.get("TYPE").trim().isEmpty()) ? "" : mi.inData.get("TYPE")
    iPUNO = (mi.inData.get("PUNO") == null || mi.inData.get("PUNO").trim().isEmpty()) ? "" : mi.inData.get("PUNO")
    iSUDO = (mi.inData.get("SUDO") == null || mi.inData.get("SUDO").trim().isEmpty()) ? "" : mi.inData.get("SUDO")
    iSSCC = (mi.inData.get("SSCC") == null || mi.inData.get("SSCC").trim().isEmpty()) ? "" : mi.inData.get("SSCC")
    iHDPR = (mi.inData.get("HDPR") == null || mi.inData.get("HDPR").trim().isEmpty()) ? "" : mi.inData.get("HDPR")
    iOPTY = (mi.inData.get("OPTY") == null || mi.inData.get("OPTY").trim().isEmpty()) ? "" : mi.inData.get("OPTY")
    iTX30 = (mi.inData.get("TX30") == null || mi.inData.get("TX30").trim().isEmpty()) ? "" : mi.inData.get("TX30")
    iOPTX = (mi.inData.get("OPTX") == null || mi.inData.get("OPTX").trim().isEmpty()) ? "" : mi.inData.get("OPTX")
    iITDS = (mi.inData.get("ITDS") == null || mi.inData.get("ITDS").trim().isEmpty()) ? "" : mi.inData.get("ITDS")
    iABFC = (mi.inData.get("ABFC") == null || mi.inData.get("ABFC").trim().isEmpty()) ? "0" : mi.inData.get("ABFC")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iDELV = (mi.inData.get("DELV") == null || mi.inData.get("DELV").trim().isEmpty()) ? 0 : mi.inData.get("DELV") as Double
    iLNQT = (mi.inData.get("LNQT") == null || mi.inData.get("LNQT").trim().isEmpty()) ? 0 : mi.inData.get("LNQT") as Double

    validateInput()
    if (validInput) {
      insertRecord()
    }
  }

  /**
   *Validate records 
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
        return
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Type
    if (iTYPE.toString().trim() == null || iTYPE.toString().trim().isEmpty()) {
      mi.error("Type must be entered")
      validInput = false
      return
    }

    //Validate Purchase Order Number

    params = ["CONO": iCONO.toString().trim(), "PUNO": iPUNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->

      if (response.PUNO == null) {
        mi.error("Invalid Purchase Order Number " + iPUNO)
        validInput = false
        return
      }
    }
    miCaller.call("PPS001MI", "GetHeadBasic", params, callback)

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

  }

  /**
   *Insert records to EXTNSX table
   * @params 
   * @return 
   */
  public void insertRecord() {
    DBAction action = database.table("EXTNSX").index("00").build()
    DBContainer query = action.getContainer()
    query.set("EXCONO", iCONO as Integer)
    query.set("EXTYPE", iTYPE)
    query.set("EXPUNO", iPUNO)
    query.set("EXSUDO", iSUDO)
    query.set("EXSSCC", iSSCC)
    query.set("EXHDPR", iHDPR)
    query.set("EXOPTY", iOPTY)
    query.set("EXTX30", iTX30)
    query.set("EXOPTX", iOPTX)
    query.set("EXITDS", iITDS)
    query.set("EXABFC", iABFC)
    query.set("EXWHLO", iWHLO)
    query.set("EXDELV", iDELV as Double)
    query.set("EXLNQT", iLNQT as Double)
    query.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXDTTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXCHNO", 0)
    query.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXCHID", program.getUser())
    action.insert(query, recordExists)
  }

  Closure recordExists = {
    mi.error("Record Already Exists")
    return
  }
}