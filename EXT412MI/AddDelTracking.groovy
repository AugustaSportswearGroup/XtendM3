/**
 * README
 * This extension is being used to Add records to EXTHPD table. 
 *
 * Name: EXT412MI.AddDelTracking
 * Description: Adding records to EXTHPD table 
 * Date       Changed By                      Description
 *20230906  SuriyaN@fortude.co     Adding records to EXTHPD table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddDelTracking extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iSHDT, iCUOR, iORNO, iWHLO, iCUNO, iMAWB, iETRN, iCNUM, iBTRN, iRESP, iCSCD
  private int iCONO
  private long iDLIX
  private boolean validInput = true

  public AddDelTracking(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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
    iSHDT = (mi.inData.get("SHDT") == null || mi.inData.get("SHDT").trim().isEmpty()) ? "" : mi.inData.get("SHDT")
    iCUOR = (mi.inData.get("CUOR") == null || mi.inData.get("CUOR").trim().isEmpty()) ? "" : mi.inData.get("CUOR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
    iMAWB = (mi.inData.get("MAWB") == null || mi.inData.get("MAWB").trim().isEmpty()) ? "" : mi.inData.get("MAWB")
    iETRN = (mi.inData.get("ETRN") == null || mi.inData.get("ETRN").trim().isEmpty()) ? "" : mi.inData.get("ETRN")
    iCNUM = (mi.inData.get("CNUM") == null || mi.inData.get("CNUM").trim().isEmpty()) ? "" : mi.inData.get("CNUM")
    iBTRN = (mi.inData.get("BTRN") == null || mi.inData.get("BTRN").trim().isEmpty()) ? "" : mi.inData.get("BTRN")
    iRESP = (mi.inData.get("RESP") == null || mi.inData.get("RESP").trim().isEmpty()) ? "" : mi.inData.get("RESP")
    iCSCD = (mi.inData.get("CSCD") == null || mi.inData.get("CSCD").trim().isEmpty()) ? "" : mi.inData.get("CSCD")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long

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
    def params = ["CONO": iCONO.toString().trim()]
    def callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

      //Validate Order Number
      params = ["CONO": iCONO.toString().trim(), "ORNO": iORNO.toString().trim()]
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

    if (!iCUNO.trim().isEmpty()) {
      //Validate Customer Number
      params = ["CONO": iCONO.toString().trim(), "CUNO": iCUNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + iCUNO)
          validInput = false
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }

      //Validate Delivery Number
      params = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.DLIX == null) {
          mi.error("Invalid Delivery Number " + iDLIX)
          validInput = false
        }
      }
      miCaller.call("MWS410MI", "GetHead", params, callback)
    

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
    
    if (!iCSCD.trim().isEmpty()) {
      //Validate Warehouse Number
      params = ["CONO": iCONO.toString().trim(), "CSCD": iCSCD.toString().trim()]
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

    //Validate SHDT
    String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
    validInput = utility.call("DateUtil", "isDateValid", iSHDT.trim(), sourceFormat.toString())
    if (!validInput) {
      mi.error("Order Date not in " + sourceFormat + " format. Please Check " + iSHDT)
      return false
    } else {
      if (!sourceFormat.trim().equals("yyyyMMdd")) {
        iSHDT = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", iSHDT.trim()) //Maintain date in YMD8 format in the table
      }
    }

  }

  /**
   *Insert records to EXTHPD table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTHPD").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXSHDT", iSHDT)
    query.set("EXCUOR", iCUOR)
    query.set("EXORNO", iORNO)
    query.set("EXWHLO", iWHLO)
    query.set("EXCUNO", iCUNO)
    query.set("EXMAWB", iMAWB)
    query.set("EXETRN", iETRN)
    query.set("EXCNUM", iCNUM)
    query.set("EXBTRN", iBTRN)
    query.set("EXRESP", iRESP)
    query.set("EXCSCD", iCSCD)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXDLIX", iDLIX as Long)
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