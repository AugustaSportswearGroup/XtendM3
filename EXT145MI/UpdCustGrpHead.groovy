/**
 * README
 * This extension is being used to Update records to EXTRPH table. 
 *
 * Name: EXT145MI.UpdCustGrpHead
 * Description: Updating records to EXTRPH table
 * Date	      Changed By                      Description
 *20231011  SuriyaN@fortude.co    Updating records to EXTRPH table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdCustGrpHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iGPNM, iGOWN, iDESC, iCIN1, iCIN2
  private int iCONO, iGPID
  private boolean validInput = true

  public UpdCustGrpHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iGPNM = (mi.inData.get("GPNM") == null || mi.inData.get("GPNM").trim().isEmpty()) ? "" : mi.inData.get("GPNM")
    iGOWN = (mi.inData.get("GOWN") == null || mi.inData.get("GOWN").trim().isEmpty()) ? "" : mi.inData.get("GOWN")
    iDESC = (mi.inData.get("DESC") == null || mi.inData.get("DESC").trim().isEmpty()) ? "" : mi.inData.get("DESC")
    iCIN1 = (mi.inData.get("CIN1") == null || mi.inData.get("CIN1").trim().isEmpty()) ? "" : mi.inData.get("CIN1")
    iCIN2 = (mi.inData.get("CIN2") == null || mi.inData.get("CIN2").trim().isEmpty()) ? "" : mi.inData.get("CIN2")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iGPID = (mi.inData.get("GPID") == null || mi.inData.get("GPID").trim().isEmpty()) ? 0 : mi.inData.get("GPID") as Integer

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
    Map < String, String > params = ["CONO": iCONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate GPID
    if (iGPID.toString().trim() == null || iGPID.toString().trim().isEmpty()) {
      mi.error("Group ID must be entered")
      validInput = false
      return
    }

  }

  /**
   *Update records to EXTRPH table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTRPH").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXGPID", iGPID)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
    if (!iGPNM.trim().isEmpty()) {
      if (iGPNM.trim().equals("?")) {
        lockedResult.set("EXGPNM", "")
      } else {
        lockedResult.set("EXGPNM", iGPNM)
      }
    }
    if (!iGOWN.trim().isEmpty()) {
      if (iGOWN.trim().equals("?")) {
        lockedResult.set("EXGOWN", "")
      } else {
        lockedResult.set("EXGOWN", iGOWN)
      }
    }
    if (!iDESC.trim().isEmpty()) {
      if (iDESC.trim().equals("?")) {
        lockedResult.set("EXDESC", "")
      } else {
        lockedResult.set("EXDESC", iDESC)
      }
    }
    if (!iCIN1.trim().isEmpty()) {
      if (iCIN1.trim().equals("?")) {
        lockedResult.set("EXCIN1", "")
      } else {
        lockedResult.set("EXCIN1", iCIN1)
      }
    }
    if (!iCIN2.trim().isEmpty()) {
      if (iCIN2.trim().equals("?")) {
        lockedResult.set("EXCIN2", "")
      } else {
        lockedResult.set("EXCIN2", iCIN2)
      }
    }
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}