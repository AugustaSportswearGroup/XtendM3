/**
 * README
 * This extension is being used to Update records to EXTCUS table. 
 *
 * Name: EXT610MI.UpdateCustomerData
 * Description: Updating records to EXTCUS table
 * Date	      Changed By                      Description
 *20230519  SuriyaN@fortude.co    Updating records to EXTCUS table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdCustomerData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iCUNO, iCUNM, iADR1, iADR2, iADR3, iTOWN, iECAR, iPONO, iCSCD, iPHNO, iTFNO, iEMAL, iTEPY, iTXID, iULZO, iPRLV, iBLCD, iM3RG, iM3LM
  private int iCONO
  private boolean validInput = true

  public UpdCustomerData(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
    iCUNM = (mi.inData.get("CUNM") == null || mi.inData.get("CUNM").trim().isEmpty()) ? "" : mi.inData.get("CUNM")
    iADR1 = (mi.inData.get("ADR1") == null || mi.inData.get("ADR1").trim().isEmpty()) ? "" : mi.inData.get("ADR1")
    iADR2 = (mi.inData.get("ADR2") == null || mi.inData.get("ADR2").trim().isEmpty()) ? "" : mi.inData.get("ADR2")
    iADR3 = (mi.inData.get("ADR3") == null || mi.inData.get("ADR3").trim().isEmpty()) ? "" : mi.inData.get("ADR3")
    iTOWN = (mi.inData.get("TOWN") == null || mi.inData.get("TOWN").trim().isEmpty()) ? "" : mi.inData.get("TOWN")
    iECAR = (mi.inData.get("ECAR") == null || mi.inData.get("ECAR").trim().isEmpty()) ? "" : mi.inData.get("ECAR")
    iPONO = (mi.inData.get("PONO") == null || mi.inData.get("PONO").trim().isEmpty()) ? "" : mi.inData.get("PONO")
    iCSCD = (mi.inData.get("CSCD") == null || mi.inData.get("CSCD").trim().isEmpty()) ? "" : mi.inData.get("CSCD")
    iPHNO = (mi.inData.get("PHNO") == null || mi.inData.get("PHNO").trim().isEmpty()) ? "" : mi.inData.get("PHNO")
    iTFNO = (mi.inData.get("TFNO") == null || mi.inData.get("TFNO").trim().isEmpty()) ? "" : mi.inData.get("TFNO")
    iEMAL = (mi.inData.get("EMAL") == null || mi.inData.get("EMAL").trim().isEmpty()) ? "" : mi.inData.get("EMAL")
    iTEPY = (mi.inData.get("TEPY") == null || mi.inData.get("TEPY").trim().isEmpty()) ? "" : mi.inData.get("TEPY")
    iTXID = (mi.inData.get("TXID") == null || mi.inData.get("TXID").trim().isEmpty()) ? "" : mi.inData.get("TXID")
    iULZO = (mi.inData.get("ULZO") == null || mi.inData.get("ULZO").trim().isEmpty()) ? "" : mi.inData.get("ULZO")
    iPRLV = (mi.inData.get("PRLV") == null || mi.inData.get("PRLV").trim().isEmpty()) ? "" : mi.inData.get("PRLV")
    iBLCD = (mi.inData.get("BLCD") == null || mi.inData.get("BLCD").trim().isEmpty()) ? "" : mi.inData.get("BLCD")
    iBLCD = (mi.inData.get("BLCD") == null || mi.inData.get("BLCD").trim().isEmpty()) ? "" : mi.inData.get("BLCD")
    iM3RG = (mi.inData.get("M3RG") == null || mi.inData.get("M3RG").trim().isEmpty()) ? "0" : mi.inData.get("M3RG")
    iM3LM = (mi.inData.get("M3LM") == null || mi.inData.get("M3LM").trim().isEmpty()) ? "0" : mi.inData.get("M3LM")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

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
  public validateInput() {

    //Validate Company Number
    Map<String, String> params = ["CONO": iCONO.toString().trim()]
    Closure<?>  callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Customer Number
    params = ["CUNO": iCUNO.toString().trim()]
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

    //Validate M3 Created Date
    if (!iM3RG.trim().equals("0")) {
      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", iM3RG.trim(), sourceFormat.toString())
      if (!validInput) {
        mi.error("M3 Created Date not in " + sourceFormat + " format. Please Check " + iM3RG)
        validInput = false
        return
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iM3RG = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", iM3RG.trim()) //Maintain date in YMD8 format in the table
        }
      }
    }

    //Validate M3 Modified Date
    if (!iM3LM.trim().equals("0")) {
      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", iM3LM.trim(), sourceFormat.toString())
      if (!validInput) {
        mi.error("M3 Modified Date not in " + sourceFormat + " format. Please Check " + iM3LM)
        validInput = false
        return
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iM3LM = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", iM3LM.trim()) //Maintain date in YMD8 format in the table
        }
      }
    }

    //Validate Country
    if (!iCSCD.trim().isEmpty()) {
      params = ["CSCD": iCSCD.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.CSCD == null) {
          mi.error("Invalid Country Code " + iCSCD)
          validInput = false
          return false
        }
      }
      miCaller.call("CRS045MI", "GetBasicData", params, callback)
    }

    if (!iTEPY.trim().isEmpty()) {
      //Validate Payment Terms
      DBAction action = database.table("CSYTAB").index("00").build()
      DBContainer query = action.getContainer()
      query.set("CTCONO", iCONO)
      query.set("CTDIVI", "")
      query.set("CTSTCO", "TEPY")
      query.set("CTSTKY", iTEPY)
      query.set("CTLNCD", "GB")
      if (!action.read(query)) {
        mi.error("Invalid Payment Terms: " + iTEPY)
        return false
      }
    }

  }

  /**
   *Update records to EXTCUS table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTCUS").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXCUNO", iCUNO)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
    if (!iCUNM.trim().isEmpty()) {
      if (iCUNM.trim().equals("?")) {
        lockedResult.set("EXCUNM", "")
      } else {
        lockedResult.set("EXCUNM", iCUNM)
      }
    }
    if (!iADR1.trim().isEmpty()) {
      if (iADR1.trim().equals("?")) {
        lockedResult.set("EXADR1", "")
      } else {
        lockedResult.set("EXADR1", iADR1)
      }
    }
    if (!iADR2.trim().isEmpty()) {
      if (iADR2.trim().equals("?")) {
        lockedResult.set("EXADR2", "")
      } else {
        lockedResult.set("EXADR2", iADR2)
      }
    }
    if (!iADR3.trim().isEmpty()) {
      if (iADR3.trim().equals("?")) {
        lockedResult.set("EXADR3", "")
      } else {
        lockedResult.set("EXADR3", iADR3)
      }
    }
    if (!iTOWN.trim().isEmpty()) {
      if (iTOWN.trim().equals("?")) {
        lockedResult.set("EXTOWN", "")
      } else {
        lockedResult.set("EXTOWN", iTOWN)
      }
    }
    if (!iECAR.trim().isEmpty()) {
      if (iECAR.trim().equals("?")) {
        lockedResult.set("EXECAR", "")
      } else {
        lockedResult.set("EXECAR", iECAR)
      }
    }
    if (!iPONO.trim().isEmpty()) {
      if (iPONO.trim().equals("?")) {
        lockedResult.set("EXPONO", "")
      } else {
        lockedResult.set("EXPONO", iPONO)
      }
    }
    if (!iCSCD.trim().isEmpty()) {
      if (iCSCD.trim().equals("?")) {
        lockedResult.set("EXCSCD", "")
      } else {
        lockedResult.set("EXCSCD", iCSCD)
      }
    }
    if (!iPHNO.trim().isEmpty()) {
      if (iPHNO.trim().equals("?")) {
        lockedResult.set("EXPHNO", "")
      } else {
        lockedResult.set("EXPHNO", iPHNO)
      }
    }
    if (!iTFNO.trim().isEmpty()) {
      if (iTFNO.trim().equals("?")) {
        lockedResult.set("EXTFNO", "")
      } else {
        lockedResult.set("EXTFNO", iTFNO)
      }
    }
    if (!iEMAL.trim().isEmpty()) {
      if (iEMAL.trim().equals("?")) {
        lockedResult.set("EXEMAL", "")
      } else {
        lockedResult.set("EXEMAL", iEMAL)
      }
    }
    if (!iTEPY.trim().isEmpty()) {
      if (iTEPY.trim().equals("?")) {
        lockedResult.set("EXTEPY", "")
      } else {
        lockedResult.set("EXTEPY", iTEPY)
      }
    }
    if (!iTXID.trim().isEmpty()) {
      if (iTXID.trim().equals("?")) {
        lockedResult.set("EXTXID", "")
      } else {
        lockedResult.set("EXTXID", iTXID)
      }
    }
    if (!iULZO.trim().isEmpty()) {
      if (iULZO.trim().equals("?")) {
        lockedResult.set("EXULZO", "")
      } else {
        lockedResult.set("EXULZO", iULZO)
      }
    }
    if (!iPRLV.trim().isEmpty()) {
      if (iPRLV.trim().equals("?")) {
        lockedResult.set("EXPRLV", "")
      } else {
        lockedResult.set("EXPRLV", iPRLV)
      }
    }
    if (!iBLCD.trim().isEmpty()) {
      if (iBLCD.trim().equals("?")) {
        lockedResult.set("EXBLCD", "")
      } else {
        lockedResult.set("EXBLCD", iBLCD)
      }
    }
    if (!iM3RG.trim().equals("0")) {
      if (iM3RG.trim().equals("?")) {
        lockedResult.set("EXM3RG", "")
      } else {
        lockedResult.set("EXM3RG", iM3RG as Integer)
      }
    }
    if (!iM3LM.trim().equals("0")) {
      if (iM3LM.trim().equals("?")) {
        lockedResult.set("EXM3LM", "")
      } else {
        lockedResult.set("EXM3LM", iM3LM as Integer)
      }
    }
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }

}