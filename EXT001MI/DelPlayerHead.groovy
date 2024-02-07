/**
 * README
 * This extension is being used to Delete records from EXTSTH table.
 *
 * Name: EXT001MI.DelPlayerHead
 * Description: Deleting records from EXTSTH table
 * Date	      Changed By                      Description
 *20230403  SuriyaN@fortude.co    Deleting records from EXTSTH table
 *
 */

public class DelPlayerHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO
  private String iPLID, iDIVI
  private boolean lineExists = false, validInput = true

  public DelPlayerHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iPLID = (mi.inData.get("PLID") == null || mi.inData.get("PLID").trim().isEmpty()) ? "" : mi.inData.get("PLID")

    validateInput()
    if (validInput) {
      checkIfLineExists()
      if (!lineExists) {
        deleteRecord()
      } else {
        mi.error("Please delete the lines for PlayerID : " + iPLID + " before deleting the header.")
      }
    }
  }

  /**
   *Validate Records
   * @params
   * @return 
   */
  public void validateInput() {

    //Validate Company Number
    Map<String, String> params = ["CONO": iCONO.toString().trim()]
    Closure<?> callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return 
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Division
    params = ["CONO": iCONO.toString().trim(), "DIVI": iDIVI.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.DIVI == null) {
        mi.error("Invalid Division " + iDIVI)
        validInput = false
        return
      }
    }

    miCaller.call("MNS100MI", "GetBasicData", params, callback)

    //Validate Player ID
    DBAction query = database.table("EXTSTH").index("00").build()
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXPLID", iPLID)

    if (!query.read(container)) {
      mi.error("Player ID Not Found " + iPLID)
      validInput = false
      return 
    }

  }

  /**
   *Check if lines exist for the player ID in EXTSTL table
   * @params
   * @return
   */
  public void checkIfLineExists() {
    DBAction query = database.table("EXTSTL").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXPLID", iPLID)
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    query.readAll(container, 3, readLines)

  }
  Closure < ? > readLines = {
    DBContainer container ->
    lineExists = true

  }

  /**
   *Delete records from EXTSTH table
   * @params
   * @return
   */
  public void deleteRecord() {
    DBAction query = database.table("EXTSTH").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXPLID", iPLID)
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
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