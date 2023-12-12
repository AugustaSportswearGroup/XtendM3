/**
 * README
 * This extension is being used to Add records to EXTSMF table. 
 *
 * Name: EXT510MI.AddLabelHead
 * Description: Adding records to EXTSMF table 
 * Date	      Changed By                      Description
 *20230817  SuriyaN@fortude.co     Adding records to EXTSMF table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddLabelHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iRIDN, iFACI, iWHLO, iPYCU, iSPOC, iITNO, iSPID, iCSID, iGTIN, iBANO, iUDF1, iUDF4
  private int iCONO, iRIDL, iIVNO, iBXNO, iQUAN, iUDF2, iUDF3
  private long iDLIX
  boolean validInput = true

  public AddLabelHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iRIDN = (mi.inData.get("RIDN") == null || mi.inData.get("RIDN").trim().isEmpty()) ? "" : mi.inData.get("RIDN")
    iFACI = (mi.inData.get("FACI") == null || mi.inData.get("FACI").trim().isEmpty()) ? "" : mi.inData.get("FACI")
    iPYCU = (mi.inData.get("PYCU") == null || mi.inData.get("PYCU").trim().isEmpty()) ? "" : mi.inData.get("PYCU")
    iSPOC = (mi.inData.get("SPOC") == null || mi.inData.get("SPOC").trim().isEmpty()) ? "0" : mi.inData.get("SPOC")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iSPID = (mi.inData.get("SPID") == null || mi.inData.get("SPID").trim().isEmpty()) ? "" : mi.inData.get("SPID")
    iCSID = (mi.inData.get("CSID") == null || mi.inData.get("CSID").trim().isEmpty()) ? "" : mi.inData.get("CSID")
    iGTIN = (mi.inData.get("GTIN") == null || mi.inData.get("GTIN").trim().isEmpty()) ? "" : mi.inData.get("GTIN")
    iBANO = (mi.inData.get("BANO") == null || mi.inData.get("BANO").trim().isEmpty()) ? "" : mi.inData.get("BANO")
    iUDF1 = (mi.inData.get("UDF1") == null || mi.inData.get("UDF1").trim().isEmpty()) ? "" : mi.inData.get("UDF1")
    iUDF4 = (mi.inData.get("UDF4") == null || mi.inData.get("UDF4").trim().isEmpty()) ? "0" : mi.inData.get("UDF4")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iRIDL = (mi.inData.get("RIDL") == null || mi.inData.get("RIDL").trim().isEmpty()) ? 0 : mi.inData.get("RIDL") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iIVNO = (mi.inData.get("IVNO") == null || mi.inData.get("IVNO").trim().isEmpty()) ? 0 : mi.inData.get("IVNO") as Integer
    iUDF2 = (mi.inData.get("UDF2") == null || mi.inData.get("UDF2").trim().isEmpty()) ? 0 : mi.inData.get("UDF2") as Integer
    iUDF3 = (mi.inData.get("UDF3") == null || mi.inData.get("UDF3").trim().isEmpty()) ? 0 : mi.inData.get("UDF3") as Integer
    iBXNO = (mi.inData.get("BXNO") == null || mi.inData.get("BXNO").trim().isEmpty()) ? 0 : mi.inData.get("BXNO") as Integer
    iQUAN = (mi.inData.get("QUAN") == null || mi.inData.get("QUAN").trim().isEmpty()) ? 0 : mi.inData.get("QUAN") as Integer

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
    
    //Validate Division 
    if(!iDIVI.trim().isEmpty())
    {
        //Validate Invoice Number
      DBAction action = database.table("CMNDIV").index("00").build()
      DBContainer query = action.getContainer()
      query.set("CCCONO", iCONO)
      query.set("CCDIVI", iDIVI)
      if (!action.read(query)) {
        mi.error("Division does not exist.")
        validInput = false
        return false
      }
    }
    
    
    if (!iITNO.trim().isEmpty()) {
      //Validate Item Number
      params = ["CONO": iCONO.toString().trim(), "ITNO": iITNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ITNO == null) {
          mi.error("Invalid Item Number " + iITNO)
          validInput = false
          return false
        }
      }
      
    miCaller.call("MMS200MI", "Get", params, callback)
    }

    if (!iFACI.trim().isEmpty()) {
      //Validate Facility Number
      params = ["CONO": iCONO.toString().trim(), "FACI": iFACI.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.FACI == null) {
          mi.error("Invalid Facility Number " + iFACI)
          validInput = false
          return false
        }
      }
      miCaller.call("CRS008MI", "Get", params, callback)
    }

    if (!iRIDN.trim().isEmpty()) {
      //Validate Order Number Order Line combo
      params = ["CONO": iCONO.toString().trim(), "ORNO": iRIDN.toString().trim(), "PONR": iRIDL.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ITNO == null) {
          validInput = false
        }
      }
      miCaller.call("OIS100MI", "GetLine", params, callback)

      //If not CO, check if valid DO
      if (!validInput) {
        validInput = true
        params = ["CONO": iCONO.toString().trim(), "TRNR": iRIDN.toString().trim(), "PONR": iRIDL.toString().trim()]
        callback = {
          Map < String,
          String > response ->
          if (response.ITNO == null) {
            mi.error("Invalid Order Number - Order Line " + iRIDN)
            validInput = false
            return false
          }
        }
        miCaller.call("MMS100MI", "GetHead", params, callback)
      }
    }

    //Validate Delivery Number
    params = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.DLIX == null) {
        mi.error("Invalid Delivery Number " + iDLIX)
        validInput = false
        return false
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

    if (!iBANO.trim().isEmpty()) {
      //Validate Lot Number
      params = ["CONO": iCONO.toString().trim(), "BANO": iBANO.toString().trim(), "ITNO": iITNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.BANO == null) {
          mi.error("Item doesn't exist in the Lot:  " + iBANO)
          validInput = false
          return false
        }
      }
      miCaller.call("MMS235MI", "GetItmLot", params, callback)
    }

    if ((mi.inData.get("IVNO") != null && !mi.inData.get("IVNO").trim().isEmpty())) {
      //Validate Invoice Number
      DBAction action = database.table("OINVOH").index("96").build()
      DBContainer query = action.getContainer()
      query.set("UHCONO", iCONO)
      query.set("UHDIVI", iDIVI)
      query.set("UHEXIN", iIVNO.toString())
      Closure < ? > resultset = {}
      if (action.readAll(query, 3, 1, resultset)>0) {
        mi.error("Invoice Number does not exist.")
        validInput = false
        return false
      }
    }

    if (!iPYCU.trim().isEmpty()) {
      //Validate Customer Number
      params = ["CUNO": iPYCU.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + iPYCU)
          validInput = false
          return false
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }

  }
  /**
   *Insert records to EXTSMF table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTSMF").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXDIVI", iDIVI)
    query.set("EXRIDN", iRIDN)
    query.set("EXFACI", iFACI)
    query.set("EXWHLO", iWHLO)
    query.set("EXPYCU", iPYCU)
    query.set("EXSPOC", iSPOC)
    query.set("EXIVNO", iIVNO as Integer)
    query.set("EXITNO", iITNO)
    query.set("EXSPID", iSPID)
    query.set("EXCSID", iCSID)
    query.set("EXGTIN", iGTIN)
    query.set("EXBXNO", iBXNO as Integer)
    query.set("EXQUAN", iQUAN as Integer)
    query.set("EXBANO", iBANO)
    query.set("EXUDF1", iUDF1)
    query.set("EXUDF2", iUDF2 as Integer)
    query.set("EXUDF3", iUDF3 as Integer)
    query.set("EXUDF4", iUDF4)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXRIDL", iRIDL as Integer)
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