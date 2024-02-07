/**
 * README
 * This extension is being used to Add records to EXTSTL table.
 *
 * Name: EXT001MI.AddPlayerLine
 * Description: Adding records to EXTSTL table
 * Date	      Changed By                      Description
 *20230403  SuriyaN@fortude.co     Adding records to EXTSTL table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddPlayerLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iPLID, iKLOC, iORNO, iITNO, iSNUM, iWHLO, iRORN, iUDF1, iUDF2, iUDF3, iUDF4, iUDF5, iUDF6, iORNR, klocValue = ""
  private int iCONO, iPONR, iPOSX, iRORL, iRORX, interval = 0
  private double iORQT
  private boolean validInput = true
  private long iDLIX

  public AddPlayerLine(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? "" : mi.inData.get("DIVI")
    iPLID = (mi.inData.get("PLID") == null || mi.inData.get("PLID").trim().isEmpty()) ? "" : mi.inData.get("PLID")
    iKLOC = (mi.inData.get("KLOC") == null || mi.inData.get("KLOC").trim().isEmpty()) ? "" : mi.inData.get("KLOC")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
    iPOSX = (mi.inData.get("POSX") == null || mi.inData.get("POSX").trim().isEmpty()) ? 0 : mi.inData.get("POSX") as Integer
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iORQT = (mi.inData.get("ORQT") == null || mi.inData.get("ORQT").trim().isEmpty()) ? 0 : mi.inData.get("ORQT") as double
    iSNUM = (mi.inData.get("SNUM") == null || mi.inData.get("SNUM").trim().isEmpty()) ? "" : mi.inData.get("SNUM")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iRORN = (mi.inData.get("RORN") == null || mi.inData.get("RORN").trim().isEmpty()) ? "" : mi.inData.get("RORN")
    iRORL = (mi.inData.get("RORL") == null || mi.inData.get("RORL").trim().isEmpty()) ? 0 : mi.inData.get("RORL") as Integer
    iRORX = (mi.inData.get("RORX") == null || mi.inData.get("RORX").trim().isEmpty()) ? 0 : mi.inData.get("RORX") as Integer
    iUDF1 = (mi.inData.get("UDF1") == null || mi.inData.get("UDF1").trim().isEmpty()) ? "" : mi.inData.get("UDF1")
    iUDF2 = (mi.inData.get("UDF2") == null || mi.inData.get("UDF2").trim().isEmpty()) ? "" : mi.inData.get("UDF2")
    iUDF3 = (mi.inData.get("UDF3") == null || mi.inData.get("UDF3").trim().isEmpty()) ? "" : mi.inData.get("UDF3")
    iUDF4 = (mi.inData.get("UDF4") == null || mi.inData.get("UDF4").trim().isEmpty()) ? "" : mi.inData.get("UDF4")
    iUDF5 = (mi.inData.get("UDF5") == null || mi.inData.get("UDF5").trim().isEmpty()) ? "" : mi.inData.get("UDF5")
    iUDF6 = (mi.inData.get("UDF6") == null || mi.inData.get("UDF6").trim().isEmpty()) ? "" : mi.inData.get("UDF6")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    validateInput()
    if (iKLOC.trim().isEmpty() && validInput) {
      getLocation()
    }
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
    Map<String, String> params = ["CONO": iCONO.toString().trim()]
    Closure<?> callback = {
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

    //Validate Player Number
    params = ["CONO": iCONO.toString().trim(), "DIVI": iDIVI.toString().trim(), "PLID": iPLID.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.PLID == null) {
        mi.error("Invalid PlayerID Number " + iPLID + ". Header Record not Found.")
        validInput = false
        return 
      }
    }
    miCaller.call("EXT001MI", "GetPlayerHead", params, callback)

    //Validate Item Number
    params = ["CONO": iCONO.toString().trim(), "ITNO": iITNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.ITNO == null) {
        mi.error("Invalid Item Number " + iITNO)
        validInput = false
        return 
      }
    }
    miCaller.call("MMS200MI", "Get", params, callback)

    if (iDLIX != 0) {
      //Validate Delivery Number
      params = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.DLIX == null) {
          mi.error("Invalid Delivery Number " + iDLIX)
          validInput = false
          return 
        }
      }
      miCaller.call("MWS410MI", "GetHead", params, callback)
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
          return 
        }
      }
      miCaller.call("MMS005MI", "GetWarehouse", params, callback)
    }

    //Validate Temporary Order Number
    DBAction query = database.table("OXHEAD").index("00").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    container.set("OAORNO", iORNR)
    if (!query.read(container)) {
      mi.error("Temporary Order Number not found " + iORNR)
      validInput = false
      return 
    }

    //Validate Final Order Number
    if (!iORNO.trim().isEmpty()) {
      query = database.table("OOHEAD").index("00").build()
      container = query.getContainer()
      container.set("OACONO", iCONO)
      container.set("OAORNO", iORNO)

      if (!query.read(container)) {
        mi.error("Final Order Number not found " + iORNO)
        validInput = false
        return 
      }
    }
  }

  /**
   *Get Location
   * @params
   * @return
   */
  public void getLocation() {
    String translationIdentity = ""
    Map<String, String> params = ["CONO": iCONO.toString().trim(), "DIVI": "", "TRQF": "0", "MSTD": "FULCO", "MVRS": "1", "BMSG": "PROPERTY", "IBOB": "I", "ELMP": "FULCOOrder", "ELMD": "Properties"]
    Closure<?> callback = {
      Map < String,
      String > response ->
      if (response.IDTR != null) {
        translationIdentity = response.IDTR
      }
    }
    miCaller.call("CRS881MI", "GetTranslation", params, callback)

    if (!translationIdentity.trim().isEmpty()) {
      DBAction query = database.table("MBMTRD").index("20").selection("TDMVXD").build()
      DBContainer container = query.getContainer()
      container.set("TDCONO", iCONO)
      container.set("TDDIVI", "")
      container.set("TDIDTR", translationIdentity as Integer)
      container.set("TDMBMD", "KLOCLOGIC")
      query.readAll(container, 4, getLocationType)
    }
    int SEQN = Integer.parseInt(iPLID.substring(10).trim())

    if (klocValue.trim().equals("A")) {
      DBAction query = database.table("MBMTRD").index("20").selection("TDMVXD").build()
      DBContainer container = query.getContainer()
      container.set("TDCONO", iCONO)
      container.set("TDDIVI", "")
      container.set("TDIDTR", translationIdentity as Integer)
      container.set("TDMBMD", "KLOCAINT")
      query.readAll(container, 4, getInterval)

      int suffix = ((SEQN - 1) % interval) + 1
      int integerValue = ((SEQN - 1) / interval).intValue()
      double prefix = integerValue % 26
      iKLOC = String.valueOf((char)(65 + prefix)) + suffix

    } else if (klocValue.trim().equals("L")) {
      params = ["CONO": iCONO.toString().trim(), "DIVI": "", "TRQF": "0", "MSTD": "FULCO", "MVRS": "1", "BMSG": "PROPERTY", "IBOB": "I", "ELMP": "FULCOOrder", "ELMD": "Properties", "MBMD": "KLOC" + SEQN]
      callback = {
        Map < String,
        String > response ->
        if (response.MVXD != null) {
          iKLOC = Integer.parseInt((response.MVXD).toString().trim())
        }
      }
      miCaller.call("CRS881MI", "GetTranslData", params, callback)
    }
    if (iKLOC.trim().isEmpty()) {
      mi.error("Location must be entered ")
      validInput = false
      return 
    }

  }

  Closure < ? > getLocationType = {
    DBContainer container ->
    klocValue = container.getString(("TDMVXD"))
  }

  Closure < ? > getInterval = {
    DBContainer container ->
    String MVXD = container.getString("TDMVXD")
    interval = Integer.parseInt(MVXD)
  }

  /**
   *Insert records to EXTSTL table
   * @params
   * @return
   */
  public void insertRecord() {
    DBAction action = database.table("EXTSTL").index("00").build()
    DBContainer query = action.getContainer()
    query.set("EXDIVI", iDIVI)
    query.set("EXPLID", iPLID)
    query.set("EXKLOC", iKLOC)
    query.set("EXORNR", iORNR)
    query.set("EXORNO", iORNO)
    query.set("EXPONR", iPONR as Integer)
    query.set("EXPOSX", iPOSX as Integer)
    query.set("EXITNO", iITNO)
    query.set("EXORQT", iORQT as Integer)
    query.set("EXSNUM", iSNUM)
    query.set("EXWHLO", iWHLO)
    query.set("EXRORN", iRORN)
    query.set("EXRORL", iRORL as Integer)
    query.set("EXRORX", iRORX as Integer)
    query.set("EXDLIX", iDLIX as Long)
    query.set("EXUDF1", iUDF1)
    query.set("EXUDF2", iUDF2)
    query.set("EXUDF3", iUDF3)
    query.set("EXUDF4", iUDF4)
    query.set("EXUDF5", iUDF5)
    query.set("EXUDF6", iUDF6)
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