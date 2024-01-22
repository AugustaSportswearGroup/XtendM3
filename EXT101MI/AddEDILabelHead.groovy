/**
 * README
 * This extension is being used to Add records to EXTGLB table. 
 *
 * Name: EXT101MI.AddEDILabelHead
 * Description: Adding records to EXTGLB table 
 * Date	      Changed By                      Description
 *20230825  SuriyaN@fortude.co     Adding records to EXTGLB table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddEDILabelHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iORNR, iORNO, iCUOR, iCUNO, iDEPT, iDEPN, iSHPT, iUD01, iUD02, iUD03, iUD04, iUD05, iUD06, iUD07, iUD08, iUD09, iUD10
  private int iCONO
  private boolean validInput = true

  public AddEDILabelHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
      insertRecord()
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
        return
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
          return
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }

    //Validate Temporary Order Number
    DBAction query = database.table("OXHEAD").index("00").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    container.set("OAORNO", iORNR.trim())
    if (!query.read(container)) {
      mi.error("Temporary Order Number not found " + iORNR)
      validInput = false
      return
    }

    //Validate Order Number
    params = ["ORNO": iORNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.ORNO == null) {
        mi.error("Invalid Order Number " + iORNO)
        validInput = false
        return
      }
    }
    miCaller.call("OIS100MI", "GetOrderHead", params, callback)
  }

  /**
   *Insert records to EXTGLB table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTGLB").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXDIVI", iDIVI)
    query.set("EXORNR", iORNR)
    query.set("EXORNO", iORNO)
    query.set("EXCUOR", iCUOR)
    query.set("EXCUNO", iCUNO)
    query.set("EXDEPT", iDEPT)
    query.set("EXDEPN", iDEPN)
    query.set("EXSHPT", iSHPT)
    query.set("EXUD01", iUD01)
    query.set("EXUD02", iUD02)
    query.set("EXUD03", iUD03)
    query.set("EXUD04", iUD04)
    query.set("EXUD05", iUD05)
    query.set("EXUD06", iUD06)
    query.set("EXUD07", iUD07)
    query.set("EXUD08", iUD08)
    query.set("EXUD09", iUD09)
    query.set("EXUD10", iUD10)
    query.set("EXCONO", iCONO as Integer)
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