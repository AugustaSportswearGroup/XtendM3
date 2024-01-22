/**
 * README
 * This extension is being used to Update records to EXTGLB table. 
 *
 * Name: EXT101MI.UpdateEDILabelHead
 * Description: Updating records to EXTGLB table
 * Date	      Changed By                      Description
 *20230825  SuriyaN@fortude.co    Updating records to EXTGLB table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdateEDILabelHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iORNR, iORNO, iCUOR, iCUNO, iDEPT, iDEPN, iSHPT, iUD01, iUD02, iUD03, iUD04, iUD05, iUD06, iUD07, iUD08, iUD09, iUD10
  private int iCONO
  private boolean validInput = true

  public UpdateEDILabelHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iCUOR = (mi.inData.get("CUOR") == null || mi.inData.get("CUOR").trim().isEmpty()) ? "" : mi.inData.get("CUOR")
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
    iDEPT = (mi.inData.get("DEPT") == null || mi.inData.get("DEPT").trim().isEmpty()) ? "" : mi.inData.get("DEPT")
    iDEPN = (mi.inData.get("DEPN") == null || mi.inData.get("DEPN").trim().isEmpty()) ? "" : mi.inData.get("DEPN")
    iSHPT = (mi.inData.get("SHPT") == null || mi.inData.get("SHPT").trim().isEmpty()) ? "" : mi.inData.get("SHPT")
    iUD01 = (mi.inData.get("UD01") == null || mi.inData.get("UD01").trim().isEmpty()) ? "" : mi.inData.get("UD01")
    iUD02 = (mi.inData.get("UD02") == null || mi.inData.get("UD02").trim().isEmpty()) ? "" : mi.inData.get("UD02")
    iUD03 = (mi.inData.get("UD03") == null || mi.inData.get("UD03").trim().isEmpty()) ? "" : mi.inData.get("UD03")
    iUD04 = (mi.inData.get("UD04") == null || mi.inData.get("UD04").trim().isEmpty()) ? "" : mi.inData.get("UD04")
    iUD05 = (mi.inData.get("UD05") == null || mi.inData.get("UD05").trim().isEmpty()) ? "" : mi.inData.get("UD05")
    iUD06 = (mi.inData.get("UD06") == null || mi.inData.get("UD06").trim().isEmpty()) ? "" : mi.inData.get("UD06")
    iUD07 = (mi.inData.get("UD07") == null || mi.inData.get("UD07").trim().isEmpty()) ? "" : mi.inData.get("UD07")
    iUD08 = (mi.inData.get("UD08") == null || mi.inData.get("UD08").trim().isEmpty()) ? "" : mi.inData.get("UD08")
    iUD09 = (mi.inData.get("UD09") == null || mi.inData.get("UD09").trim().isEmpty()) ? "" : mi.inData.get("UD09")
    iUD10 = (mi.inData.get("UD10") == null || mi.inData.get("UD10").trim().isEmpty()) ? "" : mi.inData.get("UD10")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    validateInput()
    if (validInput) {
      updateRecord()
    }

  }
  /**
   *Validate Records
   * @params
   * @return 
   */
  public validateInput() {
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

    //Validate Temporary Order Number
    DBAction query = database.table("OXHEAD").index("00").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    container.set("OAORNO", iORNR)

    if (!query.read(container)) {
      mi.error("Temporary Order Number not found " + iORNR)
      validInput = false
      return false
    }

    //Validate Order Number
    params = ["ORNO": iORNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.ORNO == null) {
        mi.error("Invalid Order Number " + iORNO)
        validInput = false
        return false
      }
    }
    miCaller.call("OIS100MI", "GetOrderHead", params, callback)
  }

  /**
   *Update records to EXTGLB table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTGLB").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXORNR", iORNR)
    container.set("EXORNO", iORNO)

    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
    if (!iCUOR.trim().isEmpty()) {
      if (iCUOR.trim().equals("?")) {
        lockedResult.set("EXCUOR", "")
      } else {
        lockedResult.set("EXCUOR", iCUOR)
      }
    }
    if (!iCUNO.trim().isEmpty()) {
      if (iCUNO.trim().equals("?")) {
        lockedResult.set("EXCUNO", "")
      } else {
        lockedResult.set("EXCUNO", iCUNO)
      }
    }
    if (!iDEPT.trim().isEmpty()) {
      if (iDEPT.trim().equals("?")) {
        lockedResult.set("EXDEPT", "")
      } else {
        lockedResult.set("EXDEPT", iDEPT)
      }
    }
    if (!iDEPN.trim().isEmpty()) {
      if (iDEPN.trim().equals("?")) {
        lockedResult.set("EXDEPN", "")
      } else {
        lockedResult.set("EXDEPN", iDEPN)
      }
    }
    if (!iSHPT.trim().isEmpty()) {
      if (iSHPT.trim().equals("?")) {
        lockedResult.set("EXSHPT", "")
      } else {
        lockedResult.set("EXSHPT", iSHPT)
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
    if (!iUD03.trim().isEmpty()) {
      if (iUD03.trim().equals("?")) {
        lockedResult.set("EXUD03", "")
      } else {
        lockedResult.set("EXUD03", iUD03)
      }
    }
    if (!iUD04.trim().isEmpty()) {
      if (iUD04.trim().equals("?")) {
        lockedResult.set("EXUD04", "")
      } else {
        lockedResult.set("EXUD04", iUD04)
      }
    }
    if (!iUD05.trim().isEmpty()) {
      if (iUD05.trim().equals("?")) {
        lockedResult.set("EXUD05", "")
      } else {
        lockedResult.set("EXUD05", iUD05)
      }
    }
    if (!iUD06.trim().isEmpty()) {
      if (iUD06.trim().equals("?")) {
        lockedResult.set("EXUD06", "")
      } else {
        lockedResult.set("EXUD06", iUD06)
      }
    }
    if (!iUD07.trim().isEmpty()) {
      if (iUD07.trim().equals("?")) {
        lockedResult.set("EXUD07", "")
      } else {
        lockedResult.set("EXUD07", iUD07)
      }
    }
    if (!iUD08.trim().isEmpty()) {
      if (iUD08.trim().equals("?")) {
        lockedResult.set("EXUD08", "")
      } else {
        lockedResult.set("EXUD08", iUD08)
      }
    }
    if (!iUD09.trim().isEmpty()) {
      if (iUD09.trim().equals("?")) {
        lockedResult.set("EXUD09", "")
      } else {
        lockedResult.set("EXUD09", iUD09)
      }
    }
    if (!iUD10.trim().isEmpty()) {
      if (iUD10.trim().equals("?")) {
        lockedResult.set("EXUD10", "")
      } else {
        lockedResult.set("EXUD10", iUD10)
      }
    }
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}