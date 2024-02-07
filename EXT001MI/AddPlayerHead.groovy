/**
 * README
 * This extension is being used to Add records to EXTSTH table. 
 *
 * Name: EXT001MI.AddPlayerHead
 * Description: Adding records to EXTSTH table 
 * Date	      Changed By                      Description
 *20230403  SuriyaN@fortude.co     Adding records to EXTSTH table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.LocalDate

public class AddPlayerHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iDIVI, iPLID, iORNR, iORNO, iOREF, iPLNM, iPLNU, iTEAM, iLEAG, iGRBY, iBGBY, iCUNO, iADID, iUDF1, iUDF2, iUDF3, iUDF4, iUDF5, iUDF6, playerID
  private int iCONO, maxPageSize = 10000, iORDT
  boolean isOrderFound = false, validInput = true

  public AddPlayerHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iPLID = (mi.inData.get("PLID") == null || mi.inData.get("PLID").trim().isEmpty()) ? "" : mi.inData.get("PLID")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iOREF = (mi.inData.get("OREF") == null || mi.inData.get("OREF").trim().isEmpty()) ? "" : mi.inData.get("OREF")
    iPLNM = (mi.inData.get("PLNM") == null || mi.inData.get("PLNM").trim().isEmpty()) ? "" : mi.inData.get("PLNM")
    iPLNU = (mi.inData.get("PLNU") == null || mi.inData.get("PLNU").trim().isEmpty()) ? "" : mi.inData.get("PLNU")
    iTEAM = (mi.inData.get("TEAM") == null || mi.inData.get("TEAM").trim().isEmpty()) ? "" : mi.inData.get("TEAM")
    iLEAG = (mi.inData.get("LEAG") == null || mi.inData.get("LEAG").trim().isEmpty()) ? "" : mi.inData.get("LEAG")
    iGRBY = (mi.inData.get("GRBY") == null || mi.inData.get("GRBY").trim().isEmpty()) ? "" : mi.inData.get("GRBY")
    iBGBY = (mi.inData.get("BGBY") == null || mi.inData.get("BGBY").trim().isEmpty()) ? "" : mi.inData.get("BGBY")
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
    iADID = (mi.inData.get("ADID") == null || mi.inData.get("ADID").trim().isEmpty()) ? "" : mi.inData.get("ADID")
    iUDF1 = (mi.inData.get("UDF1") == null || mi.inData.get("UDF1").trim().isEmpty()) ? "" : mi.inData.get("UDF1")
    iUDF2 = (mi.inData.get("UDF2") == null || mi.inData.get("UDF2").trim().isEmpty()) ? "" : mi.inData.get("UDF2")
    iUDF3 = (mi.inData.get("UDF3") == null || mi.inData.get("UDF3").trim().isEmpty()) ? "" : mi.inData.get("UDF3")
    iUDF4 = (mi.inData.get("UDF4") == null || mi.inData.get("UDF4").trim().isEmpty()) ? "" : mi.inData.get("UDF4")
    iUDF5 = (mi.inData.get("UDF5") == null || mi.inData.get("UDF5").trim().isEmpty()) ? "" : mi.inData.get("UDF5")
    iUDF6 = (mi.inData.get("UDF6") == null || mi.inData.get("UDF6").trim().isEmpty()) ? "" : mi.inData.get("UDF6")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iORDT = (mi.inData.get("ORDT") == null || mi.inData.get("ORDT").trim().isEmpty()) ? 0 : mi.inData.get("ORDT") as Integer

    validateInput()
    if (validInput) {
      checkIfOrderExists()
      insertRecord()
      mi.outData.put("PLID", playerID)
      mi.write()
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

    if (!iORNR.trim().isEmpty()) {
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
    }

    if (!iORNO.trim().isEmpty()) {
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

    if (!iADID.trim().isEmpty()) {
      //Validate Address ID
      DBAction query = database.table("OCUSAD").index("00").build()
      DBContainer container = query.getContainer()
      container.set("OPCONO", iCONO)
      container.set("OPCUNO", iCUNO)
      container.set("OPADRT", 1)
      container.set("OPADID", iADID)

      if (!query.read(container)) {
        mi.error("Address ID Not Found " + iADID)
        validInput = false
        return 
      }
    }

    if (iORDT != 0) {
      //Validate Date
      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", iORDT.toString().trim(), sourceFormat.toString())
      if (!validInput) {
        mi.error("Order Date not in " + sourceFormat + " format. Please Check " + iORDT)
        return 
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iORDT = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", iORDT.toString().trim()) //Maintain date in YMD8 format in the table
        }
      }
    }

  }

  /**
   * Check if the order already exists in EXTSTH table and get PLID if availble.
   * @params
   * @return
   */

  public void checkIfOrderExists() {
    DBAction query = database.table("EXTSTH").selection("EXPLID").index("10").build()
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXORNR", iORNR)
    query.readAll(container, 3, maxPageSize, resultset)
  }

  Closure < ? > resultset = {
    DBContainer container ->
    isOrderFound = true
    if (playerID == null || playerID.trim().isEmpty()) {
      playerID = container.get("EXPLID")

    }
  }

  /**
   *Insert records to EXTSTH table
   * @params
   * @return
   */
  public void insertRecord() {
    DBAction action = database.table("EXTSTH").index("00").build()
    DBContainer query = action.getContainer()

    if ((iPLID.trim().isEmpty() || iPLID.trim().equals("*AUTO"))) //Generate Player ID if it doesn't exists. 
    {
      if (isOrderFound) {
        int SEQN = Integer.parseInt(playerID.substring(10)) + 1 //Increment the sequence number if player id found. 
        playerID = iORNR + String.format("%05d", SEQN)
      } else {
        playerID = iORNR + "00001" //Start from sequence one if new player ID. 
      }
    } else {
      playerID = iPLID
    }
    query.set("EXPLID", playerID)
    query.set("EXDIVI", iDIVI)
    query.set("EXORNR", iORNR)
    query.set("EXORNO", iORNO)
    query.set("EXOREF", iOREF)
    query.set("EXPLNM", iPLNM)
    query.set("EXPLNU", iPLNU)
    query.set("EXTEAM", iTEAM)
    query.set("EXLEAG", iLEAG)
    query.set("EXGRBY", iGRBY)
    query.set("EXBGBY", iBGBY)
    query.set("EXCUNO", iCUNO)
    query.set("EXADID", iADID)
    query.set("EXUDF1", iUDF1)
    query.set("EXUDF2", iUDF2)
    query.set("EXUDF3", iUDF3)
    query.set("EXUDF4", iUDF4)
    query.set("EXUDF5", iUDF5)
    query.set("EXUDF6", iUDF6)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXORDT", iORDT as Integer)
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