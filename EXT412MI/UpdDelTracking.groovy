/**
 * README
 * This extension is being used to Update records to EXTHPD table. 
 *
 * Name: EXT412MI.UpdDelTracking
 * Description: Updating records to EXTHPD table
 * Date       Changed By                      Description
 *20230906  SuriyaN@fortude.co    Updating records to EXTHPD table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdDelTracking extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iSHDT, iCUOR, iORNO, iWHLO, iCUNO, iMAWB, iETRN, iCNUM, iBTRN, iRESP, iCSCD
  private int iCONO
  private long iDLIX
  private validInput = true
  
  public UpdDelTracking(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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
    if(validInput)
    {
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
          validInput=false
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
            validInput=false
            return false
          }
      }
    
      miCaller.call("OIS100MI", "GetOrderHead", params, callback)
    
    if(!iCUNO.trim().isEmpty())
    {
      //Validate Customer Number
      params = ["CONO": iCONO.toString().trim(),"CUNO":iCUNO.toString().trim()]
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
      params = ["CONO": iCONO.toString().trim(),"DLIX":iDLIX.toString().trim()]
      callback = {
        Map < String,
          String > response ->
          if (response.DLIX == null) {
            mi.error("Invalid Delivery Number " + iDLIX)
            validInput = false
          }
      }
      miCaller.call("MWS410MI", "GetHead", params, callback)
    
    
    if(!iWHLO.trim().isEmpty())
    {
      //Validate Warehouse Number
      params = ["CONO": iCONO.toString().trim(),"WHLO":iWHLO.toString().trim()]
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
   *Update records to EXTHPD table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTHPD").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXSHDT", iSHDT)
    container.set("EXCUOR", iCUOR)
    container.set("EXORNO", iORNO)
    container.set("EXDLIX", iDLIX)
    boolean recordExists = query.readLock(container, updateCallBack)
    if(!recordExists)
    {
      mi.error("Record Doesn't Exist.")
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")

    if (!iWHLO.trim().isEmpty()) {
      if (iWHLO.trim().equals("?")) {
        lockedResult.set("EXWHLO", "")
      } else {
        lockedResult.set("EXWHLO", iWHLO)
      }
    }
    if (!iCUNO.trim().isEmpty()) {
      if (iCUNO.trim().equals("?")) {
        lockedResult.set("EXCUNO", "")
      } else {
        lockedResult.set("EXCUNO", iCUNO)
      }
    }
    if (!iMAWB.trim().isEmpty()) {
      if (iMAWB.trim().equals("?")) {
        lockedResult.set("EXMAWB", "")
      } else {
        lockedResult.set("EXMAWB", iMAWB)
      }
    }
    if (!iETRN.trim().isEmpty()) {
      if (iETRN.trim().equals("?")) {
        lockedResult.set("EXETRN", "")
      } else {
        lockedResult.set("EXETRN", iETRN)
      }
    }
    if (!iCNUM.trim().isEmpty()) {
      if (iCNUM.trim().equals("?")) {
        lockedResult.set("EXCNUM", "")
      } else {
        lockedResult.set("EXCNUM", iCNUM)
      }
    }
    if (!iBTRN.trim().isEmpty()) {
      if (iBTRN.trim().equals("?")) {
        lockedResult.set("EXBTRN", "")
      } else {
        lockedResult.set("EXBTRN", iBTRN)
      }
    }
    if (!iRESP.trim().isEmpty()) {
      if (iRESP.trim().equals("?")) {
        lockedResult.set("EXRESP", "")
      } else {
        lockedResult.set("EXRESP", iRESP)
      }
    }
    if (!iCSCD.trim().isEmpty()) {
      if (iCSCD.trim().equals("?")) {
        lockedResult.set("EXCSCD", "")
      } else {
        lockedResult.set("EXCSCD", iCSCD)
      }
    }
   
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}