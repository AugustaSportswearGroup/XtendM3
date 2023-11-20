/**
 * README
 * This extension is being used to Update records to EXTMAS table. 
 *
 * Name: EXT416MI.UpdBINMaster
 * Description: Updating records to EXTMAS table
 * Date	      Changed By                      Description
 *20230927  SuriyaN@fortude.co    Updating records to EXTMAS table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdBINMaster extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iBNNO, iTX30
  private int iCONO
  private Double iWEIG, iVOL3, iVOM3, iVOMT, iPACL, iPACW, iPACH
  private boolean validInput = true
  
  public UpdBINMaster(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iBNNO = (mi.inData.get("BNNO") == null || mi.inData.get("BNNO").trim().isEmpty()) ? "" : mi.inData.get("BNNO")
    iTX30 = (mi.inData.get("TX30") == null || mi.inData.get("TX30").trim().isEmpty()) ? "" : mi.inData.get("TX30")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iWEIG = (mi.inData.get("WEIG") == null || mi.inData.get("WEIG").trim().isEmpty()) ? 0 : mi.inData.get("WEIG") as Double
    iVOL3 = (mi.inData.get("VOL3") == null || mi.inData.get("VOL3").trim().isEmpty()) ? 0 : mi.inData.get("VOL3") as Double
    iVOM3 = (mi.inData.get("VOM3") == null || mi.inData.get("VOM3").trim().isEmpty()) ? 0 : mi.inData.get("VOM3") as Double
    iVOMT = (mi.inData.get("VOMT") == null || mi.inData.get("VOMT").trim().isEmpty()) ? 0 : mi.inData.get("VOMT") as Double
    iPACL = (mi.inData.get("PACL") == null || mi.inData.get("PACL").trim().isEmpty()) ? 0 : mi.inData.get("PACL") as Double
    iPACW = (mi.inData.get("PACW") == null || mi.inData.get("PACW").trim().isEmpty()) ? 0 : mi.inData.get("PACW") as Double
    iPACH = (mi.inData.get("PACH") == null || mi.inData.get("PACH").trim().isEmpty()) ? 0 : mi.inData.get("PACH") as Double

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
    
  }
  
  /**
   *Update records to EXTMAS table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTMAS").selection("EXCHNO").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXWHLO", iWHLO)
    container.set("EXBNNO", iBNNO)
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
    if (!iTX30.trim().isEmpty()) {
      if (iTX30.trim().equals("?")) {
        lockedResult.set("EXTX30", "")
      } else {
        lockedResult.set("EXTX30", iTX30)
      }
    }
    if (mi.inData.get("WEIG") != null && !mi.inData.get("WEIG").trim().isEmpty()) {
      lockedResult.set("EXWEIG", iWEIG)
    }
    if (mi.inData.get("VOL3") != null && !mi.inData.get("VOL3").trim().isEmpty()) {
      lockedResult.set("EXVOL3", iVOL3)
    }
    if (mi.inData.get("VOM3") != null && !mi.inData.get("VOM3").trim().isEmpty()) {
      lockedResult.set("EXVOM3", iVOM3)
    }
    if (mi.inData.get("VOMT") != null && !mi.inData.get("VOMT").trim().isEmpty()) {
      lockedResult.set("EXVOMT", iVOMT)
    }
    if (mi.inData.get("PACL") != null && !mi.inData.get("PACL").trim().isEmpty()) {
      lockedResult.set("EXPACL", iPACL)
    }
    if (mi.inData.get("PACW") != null && !mi.inData.get("PACW").trim().isEmpty()) {
      lockedResult.set("EXPACW", iPACW)
    }
    if (mi.inData.get("PACH") != null && !mi.inData.get("PACH").trim().isEmpty()) {
      lockedResult.set("EXPACH", iPACH)
    }
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}