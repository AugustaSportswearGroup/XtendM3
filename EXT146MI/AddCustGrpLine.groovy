/**
 * README
 * This extension is being used to Add records to EXTRPL table. 
 *
 * Name: EXT146MI.AddCustGrpLine
 * Description: Adding records to EXTRPL table 
 * Date	      Changed By                      Description
 *20231011  SuriyaN@fortude.co     Adding records to EXTRPL table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddCustGrpLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iCUNO, iGLBL, iCIN1, iCIN2
  private int iCONO, iGPID
  private boolean validInput = true

  public AddCustGrpLine(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
      insertRecord()
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
      return false
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

    //Validate if header exists
    params = ["CONO": iCONO.toString().trim(), "GPID": iGPID.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.GPID == null) {
        mi.error("Header record doesn't exist for the Group ID " + iGPID)
        validInput = false
        return
      }
    }
    miCaller.call("EXT145MI", "GetCustGrpHead", params, callback)
  }

  /**
   *Insert records to EXTRPL table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTRPL").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXCUNO", iCUNO)
    query.set("EXGLBL", iGLBL)
    query.set("EXCIN1", iCIN1)
    query.set("EXCIN2", iCIN2)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXGPID", iGPID as Integer)
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