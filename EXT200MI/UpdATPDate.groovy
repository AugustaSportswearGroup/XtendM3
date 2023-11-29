/**
 * README
 * This extension is being used to Update records to EXTPDT table. 
 *
 * Name: EXT200MI.UpdATPDate
 * Description: Updating records to EXTPDT table
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co    Updating records to EXTPDT table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdATPDate extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iWHLO, iITNO, iDATE, iUPDS

  private int iCONO, iORQ9
  private boolean validInput = true
  
  public UpdATPDate(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iDATE = (mi.inData.get("DATE") == null || mi.inData.get("DATE").trim().isEmpty()) ? "" : mi.inData.get("DATE")
    iUPDS = (mi.inData.get("UPDS") == null || mi.inData.get("UPDS").trim().isEmpty()) ? "0" : mi.inData.get("UPDS")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iORQ9 = (mi.inData.get("ORQ9") == null || mi.inData.get("ORQ9").trim().isEmpty()) ? 0 : mi.inData.get("ORQ9") as Integer
    
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
    params = ["CONO": iCONO.toString().trim(),"ITNO":iITNO.toString().trim()]
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
      
    //Validate DATE
    String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
    validInput = utility.call("DateUtil", "isDateValid", iDATE.trim(), sourceFormat.toString())
    if (!validInput) {
      mi.error("Order Date not in " + sourceFormat + " format. Please Check " + iDATE)
      return false
    } else {
      if (!sourceFormat.trim().equals("yyyyMMdd")) {
        iDATE = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", iDATE.trim()) //Maintain date in YMD8 format in the table
      }
    }
  }
  /**
   *Update records to EXTPDT table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTPDT").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXWHLO", iWHLO)
    container.set("EXITNO", iITNO)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")

    if (!iDATE.trim().isEmpty()) {
      if (iDATE.trim().equals("?")) {
        lockedResult.set("EXDATE", "")
      } else {
        lockedResult.set("EXDATE", iDATE)
      }
    }
    if (!iUPDS.trim().isEmpty()) {
      if (iUPDS.trim().equals("?")) {
        lockedResult.set("EXUPDS", "")
      } else {
        lockedResult.set("EXUPDS", iUPDS)
      }
    }
    
    if (mi.inData.get("ORQ9")!=null&&!mi.inData.get("ORQ9").trim().isEmpty()) {
            lockedResult.set("EXORQ9", iORQ9)
        }

    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.set("EXCHNO", CHNO+1)
    lockedResult.update()
  }


}