/**
 * README
 * This extension is being used to Update records to EXTYPE table. 
 *
 * Name: EXT415MI.UpdSwissCarton
 * Description: Updating records to EXTYPE table
 * Date	      Changed By                      Description
 *20230927  SuriyaN@fortude.co    Updating records to EXTYPE table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdSwissCarton extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iTRTP, iLFMT, iSFMT
  private int iCONO, iLANE, iTTYP
  private Double iVOL3
  private boolean validInput = true
  
  public UpdSwissCarton(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iTRTP = (mi.inData.get("TRTP") == null || mi.inData.get("TRTP").trim().isEmpty()) ? "" : mi.inData.get("TRTP")
    iLFMT = (mi.inData.get("LFMT") == null || mi.inData.get("LFMT").trim().isEmpty()) ? "" : mi.inData.get("LFMT")
    iSFMT = (mi.inData.get("SFMT") == null || mi.inData.get("SFMT").trim().isEmpty()) ? "" : mi.inData.get("SFMT")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iVOL3 = (mi.inData.get("VOL3") == null || mi.inData.get("VOL3").trim().isEmpty()) ? 0 : mi.inData.get("VOL3") as Double
    iLANE = (mi.inData.get("LANE") == null || mi.inData.get("LANE").trim().isEmpty()) ? 0 : mi.inData.get("LANE") as Integer
    iTTYP = (mi.inData.get("TTYP") == null || mi.inData.get("TTYP").trim().isEmpty()) ? 0 : mi.inData.get("TTYP") as Integer
    
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
    
    
    //Validate Order Type
    params = ["CONO": iCONO.toString().trim(), "TRTP": iTRTP.toString().trim(),"TTYP": iTTYP.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.TRTP == null) {
        mi.error("Invalid Order Type - Transaction Type")
        validInput = false
        return false
      }
    }
    miCaller.call("CRS200MI", "LstOrderType", params, callback)
  
  }
  
  /**
   *Update records to EXTYPE table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTYPE").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXWHLO", iWHLO)
    container.set("EXTRTP", iTRTP)
    container.set("EXTTYP", iTTYP)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
    if (!iLFMT.trim().isEmpty()) {
      if (iLFMT.trim().equals("?")) {
        lockedResult.set("EXLFMT", "")
      } else {
        lockedResult.set("EXLFMT", iLFMT)
      }
    }
    if (!iSFMT.trim().isEmpty()) {
      if (iSFMT.trim().equals("?")) {
        lockedResult.set("EXSFMT", "")
      } else {
        lockedResult.set("EXSFMT", iSFMT)
      }
    }
    if (mi.inData.get("VOL3") != null && !mi.inData.get("VOL3").trim().isEmpty()) {
      lockedResult.set("EXVOL3", iVOL3)
    } 
    if(mi.inData.get("LANE") != null && !mi.inData.get("LANE").trim().isEmpty()) {
      lockedResult.set("EXLANE", iLANE)
    } 
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}