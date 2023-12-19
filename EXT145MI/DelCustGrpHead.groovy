/**
 * README
 * This extension is being used to Delete records from EXTRPH table. 
 *
 * Name: EXT145MI.DelCustGrpHead
 * Description: Deleting records from EXTRPH table
 * Date	      Changed By                      Description
 *20231011  SuriyaN@fortude.co    Deleting records from EXTRPH table
 *
 */

public class DelCustGrpHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO, iGPID
  private boolean validInput = true

  public DelCustGrpHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
      return
    }

    //Validate if line exists
    params = ["CONO": iCONO.toString().trim(), "GPID": iGPID.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.GPID == null) {
        mi.error("Unable to delete record. Lines exists for GPID : " + iGPID)
        validInput = false
        return
      }
    }
    miCaller.call("EXT146MI", "LstCustGrpLine", params, callback)

  }
  /**
   *Delete records from EXTRPH table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTRPH").index("00").build()
    DBContainer container = query.getContainer()

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