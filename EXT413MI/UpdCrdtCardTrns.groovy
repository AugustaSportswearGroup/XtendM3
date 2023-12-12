/**
 * README
 * This extension is being used to Update records to EXTCCT table. 
 *
 * Name: EXT413MI.UpdCrdtCardTrns
 * Description: Updating records to EXTCCT table
 * Date	      Changed By                      Description
 *20230912  SuriyaN@fortude.co    Updating records to EXTCCT table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdCrdtCardTrns extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDLST
  private int iCONO, iDLSP
  private long iDLIX
  private boolean validInput = true
  
  public UpdCrdtCardTrns(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iDLST = (mi.inData.get("DLST") == null || mi.inData.get("DLST").trim().isEmpty()) ? "" : mi.inData.get("DLST")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iDLSP = (mi.inData.get("DLSP") == null || mi.inData.get("DLSP").trim().isEmpty()) ? 0 : mi.inData.get("DLSP") as Integer
    
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
  
  /**
   *Update records to EXTCCT table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTCCT").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
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
    if (!iDLST.trim().isEmpty()) {
      if (iDLST.trim().equals("?")) {
        lockedResult.set("EXDLST", "")
      } else {
        lockedResult.set("EXDLST", iDLST)
      }
    }
    if (mi.inData.get("DLSP") != null && !mi.inData.get("DLSP").trim().isEmpty()) {
      lockedResult.set("EXDLSP", iDLSP)
    } 
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}