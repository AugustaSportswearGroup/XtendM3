/**
 * README
 * This extension is being used to Update records to EXTNSX table. 
 *
 * Name: EXT450MI.UpdLabelFile
 * Description: Adding records to EXTNSX table
 * Date	      Changed By                      Description
 *23-12-21    AbhishekA@fortude.co         Updating records to EXTNSX table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdLabelFile extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iTYPE, iPUNO, iSUDO, iSSCC, iHDPR, iOPTY, iTX30, iOPTX, iITDS, iABFC, iWHLO
  private int iCONO
  private double iDELV, iLNQT
  private boolean validInput = true

  public UpdLabelFile(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iABFC = (mi.inData.get("ABFC") == null || mi.inData.get("ABFC").trim().isEmpty()) ? "0" : mi.inData.get("ABFC")

    iDELV = (mi.inData.get("DELV") == null || mi.inData.get("DELV").trim().isEmpty()) ? 0 : mi.inData.get("DELV") as Double
    iLNQT = (mi.inData.get("LNQT") == null || mi.inData.get("LNQT").trim().isEmpty()) ? 0 : mi.inData.get("LNQT") as Double

    validateInput()
    if (validInput) {
      updateRecord()
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
   *Update records to EXTNSX table
   * @params 
   * @return 
   */
  public void updateRecord() {
    DBAction query = database.table("EXTNSX").index("00").build()
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO)
    container.set("EXTYPE", iTYPE)
    container.set("EXPUNO", iPUNO)
    container.set("EXSUDO", iSUDO)
    container.set("EXSSCC", iSSCC)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")

    if (!iTYPE.trim().isEmpty()) {
      if (iTYPE.trim().equals("?")) {
        lockedResult.set("EXTYPE", "")
      } else {
        lockedResult.set("EXTYPE", iTYPE)
      }
    }
    if (!iPUNO.trim().isEmpty()) {
      if (iPUNO.trim().equals("?")) {
        lockedResult.set("EXPUNO", "")
      } else {
        lockedResult.set("EXPUNO", iPUNO)
      }
    }
    if (!iSUDO.trim().isEmpty()) {
      if (iSUDO.trim().equals("?")) {
        lockedResult.set("EXSUDO", "")
      } else {
        lockedResult.set("EXSUDO", iSUDO)
      }
    }
    if (!iSSCC.trim().isEmpty()) {
      if (iSSCC.trim().equals("?")) {
        lockedResult.set("EXSSCC", "")
      } else {
        lockedResult.set("EXSSCC", iSSCC)
      }
    }
    if (!iHDPR.trim().isEmpty()) {
      if (iHDPR.trim().equals("?")) {
        lockedResult.set("EXHDPR", "")
      } else {
        lockedResult.set("EXHDPR", iHDPR)
      }
    }
    if (!iOPTY.trim().isEmpty()) {
      if (iOPTY.trim().equals("?")) {
        lockedResult.set("EXOPTY", "")
      } else {
        lockedResult.set("EXOPTY", iOPTY)
      }
    }
    if (!iTX30.trim().isEmpty()) {
      if (iTX30.trim().equals("?")) {
        lockedResult.set("EXTX30", "")
      } else {
        lockedResult.set("EXTX30", iTX30)
      }
    }
    if (!iOPTX.trim().isEmpty()) {
      if (iOPTX.trim().equals("?")) {
        lockedResult.set("EXOPTX", "")
      } else {
        lockedResult.set("EXOPTX", iOPTX)
      }
    }
    if (!iITDS.trim().isEmpty()) {
      if (iITDS.trim().equals("?")) {
        lockedResult.set("EXITDS", "")
      } else {
        lockedResult.set("EXITDS", iITDS)
      }
    }
    
    if (!iABFC.trim().isEmpty()) {
      if (iABFC.trim().equals("?")) {
        lockedResult.set("EXABFC", "")
      } else {
        lockedResult.set("EXABFC", iABFC)
      }
    }
    
    if (!iWHLO.trim().isEmpty()) {
      if (iWHLO.trim().equals("?")) {
        lockedResult.set("EXWHLO", "")
      } else {
        lockedResult.set("EXWHLO", iWHLO)
      }
    }

    if (mi.inData.get("DELV") != null && !mi.inData.get("DELV").trim().isEmpty()) {
      lockedResult.set("EXDELV", iDELV)
    }

    if (mi.inData.get("LNQT") != null && !mi.inData.get("LNQT").trim().isEmpty()) {
      lockedResult.set("EXLNQT", iLNQT)
    }

    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXDTTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}