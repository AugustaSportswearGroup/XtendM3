/**
 * README
 * This extension is being used to Update records to EXTSMF table. 
 *
 * Name: EXT510MI.UpdLabelHead
 * Description: Updating records to EXTSMF table
 * Date	      Changed By                      Description
 *20230817  SuriyaN@fortude.co    Updating records to EXTSMF table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdLabelHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iRIDN, iFACI, iWHLO, iPYCU, iSPOC, iITNO, iSPID, iCSID, iGTIN, iBANO, iUDF1, iUDF4
  private int iCONO, iRIDL, iIVNO, iBXNO, iQUAN, iUDF2, iUDF3
  private long iDLIX
  boolean validInput = true

  public UpdLabelHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
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
    
    if(!iFACI.trim().isEmpty())
    {
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
      //Validate Order Number
      params = ["CONO": iCONO.toString().trim(), "ORNO": iRIDN.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ORNO == null) {
          validInput = false
        }
      }
      miCaller.call("OIS100MI", "GetHead", params, callback)

      //If not CO, check if valid DO
      if (!validInput) {
        validInput = true
        params = ["CONO": iCONO.toString().trim(), "TRNR": iRIDN.toString().trim()]
        callback = {
          Map < String,
          String > response ->
          if (response.TRNR == null) {
            mi.error("Invalid Order Number " + iRIDN)
            validInput = false
            return false
          }
        }
        miCaller.call("MMS100MI", "GetHead", params, callback)
      }
    }

    if (iDLIX != 0) {
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
          return false
        }
      }
      miCaller.call("MMS005MI", "GetWarehouse", params, callback)
    }

  }

  /**
   *Update records to EXTSMF table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTSMF").selection("EXCHNO").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXRIDN", iRIDN)
    container.set("EXDLIX", iDLIX as Long)
    if (query.read(container)) {
      query.readLock(container, updateCallBack)
    } else {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
    if (!iFACI.trim().isEmpty()) {
      if (iFACI.trim().equals("?")) {
        lockedResult.set("EXFACI", "")
      } else {
        lockedResult.set("EXFACI", iFACI)
      }
    }
    if (!iPYCU.trim().isEmpty()) {
      if (iPYCU.trim().equals("?")) {
        lockedResult.set("EXPYCU", "")
      } else {
        lockedResult.set("EXPYCU", iPYCU)
      }
    }
    if (!iSPOC.trim().isEmpty()) {
      if (iSPOC.trim().equals("?")) {
        lockedResult.set("EXSPOC", "")
      } else {
        lockedResult.set("EXSPOC", iSPOC)
      }
    }
    if (!iITNO.trim().isEmpty()) {
      if (iITNO.trim().equals("?")) {
        lockedResult.set("EXITNO", "")
      } else {
        lockedResult.set("EXITNO", iITNO)
      }
    }
    if (!iSPID.trim().isEmpty()) {
      if (iSPID.trim().equals("?")) {
        lockedResult.set("EXSPID", "")
      } else {
        lockedResult.set("EXSPID", iSPID)
      }
    }
    if (!iCSID.trim().isEmpty()) {
      if (iCSID.trim().equals("?")) {
        lockedResult.set("EXCSID", "")
      } else {
        lockedResult.set("EXCSID", iCSID)
      }
    }
    if (!iGTIN.trim().isEmpty()) {
      if (iGTIN.trim().equals("?")) {
        lockedResult.set("EXGTIN", "")
      } else {
        lockedResult.set("EXGTIN", iGTIN)
      }
    }
    if (!iBANO.trim().isEmpty()) {
      if (iBANO.trim().equals("?")) {
        lockedResult.set("EXBANO", "")
      } else {
        lockedResult.set("EXBANO", iBANO)
      }
    }
    if (!iUDF1.trim().isEmpty()) {
      if (iUDF1.trim().equals("?")) {
        lockedResult.set("EXUDF1", "")
      } else {
        lockedResult.set("EXUDF1", iUDF1)
      }
    }
    if (!iUDF4.trim().isEmpty()) {
      if (iUDF4.trim().equals("?")) {
        lockedResult.set("EXUDF4", "")
      } else {
        lockedResult.set("EXUDF4", iUDF4)
      }
    }
    
    if (mi.inData.get("BXNO") != null && !mi.inData.get("UDF2").trim().isEmpty()) {
      lockedResult.set("EXBXNO", iBXNO)
    }
    if (mi.inData.get("QUAN") != null && !mi.inData.get("QUAN").trim().isEmpty()) {
      lockedResult.set("EXQUAN", iQUAN)
    }
    if (mi.inData.get("IVNO") != null && !mi.inData.get("UDF2").trim().isEmpty()) {
      lockedResult.set("EXIVNO", iIVNO)
    }
    if (mi.inData.get("UDF2") != null && !mi.inData.get("UDF2").trim().isEmpty()) {
      lockedResult.set("EXUDF2", iUDF2)
    }
    if (mi.inData.get("UDF3") != null && !mi.inData.get("UDF3").trim().isEmpty()) {
      lockedResult.set("EXUDF3", iUDF3)
    }
    if (mi.inData.get("RIDL") != null && !mi.inData.get("RIDL").trim().isEmpty()) {
      lockedResult.set("EXRIDL", iRIDL)
    }
    if (mi.inData.get("RIDL") != null && !mi.inData.get("RIDL").trim().isEmpty()) {
      lockedResult.set("EXRIDL", iRIDL)
    }
    if (mi.inData.get("RIDL") != null && !mi.inData.get("RIDL").trim().isEmpty()) {
      lockedResult.set("EXRIDL", iRIDL)
    }

    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO+1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}