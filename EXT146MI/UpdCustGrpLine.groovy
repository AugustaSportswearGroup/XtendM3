/**
 * README
 * This extension is being used to Update records to EXTRPL table. 
 *
 * Name: EXT146MI.UpdCustGrpLine
 * Description: Updating records to EXTRPL table
 * Date	      Changed By                      Description
 *20231011  SuriyaN@fortude.co    Updating records to EXTRPL table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdCustGrpLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iCUNO, iGLBL, iCIN1, iCIN2
  private int iCONO, iGPID
  private boolean validInput = true

  public UpdCustGrpLine(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
    iGLBL = (mi.inData.get("GLBL") == null || mi.inData.get("GLBL").trim().isEmpty()) ? "" : mi.inData.get("GLBL")
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

    //Validate CUNO
    if (iCUNO.toString().trim() == null || iCUNO.toString().trim().isEmpty()) {
      mi.error("Customer Number must be entered")
      validInput = false
      return
    }

    //Validate Customer Number
    params = ["CUNO": iCUNO.toString().trim()]
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

  /**
   *Update records to EXTRPL table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTRPL").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXGPID", iGPID)
    container.set("EXCUNO", iCUNO)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
    if (!iCUNO.trim().isEmpty()) {
      if (iCUNO.trim().equals("?")) {
        lockedResult.set("EXCUNO", "")
      } else {
        lockedResult.set("EXCUNO", iCUNO)
      }
    }
    if (!iGLBL.trim().isEmpty()) {
      if (iGLBL.trim().equals("?")) {
        lockedResult.set("EXGLBL", "")
      } else {
        lockedResult.set("EXGLBL", iGLBL)
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