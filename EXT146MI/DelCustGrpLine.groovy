/**
 * README
 * This extension is being used to Delete records from EXTRPL table. 
 *
 * Name: EXT146MI.DelCustGrpLine
 * Description: Deleting records from EXTRPL table
 * Date	      Changed By                      Description
 *20231011  SuriyaN@fortude.co    Deleting records from EXTRPL table
 *
 */

public class DelCustGrpLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO, iGPID
  private String iCUNO
  private boolean validInput = true

  public DelCustGrpLine(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iGPID = (mi.inData.get("GPID") == null || mi.inData.get("GPID").trim().isEmpty()) ? 0 : mi.inData.get("GPID") as Integer
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")

    validateInput()
    if (validInput) {
      deleteRecord()
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
      return false
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
   *Delete records from EXTRPL table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTRPL").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCUNO", iCUNO)
    container.set("EXCONO", iCONO)
    container.set("EXGPID", iGPID)
    if (!query.readLock(container, deleteCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > deleteCallBack = {
    LockedResult lockedResult ->
    lockedResult.delete()
  }
}