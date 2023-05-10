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

  private int iCONO
  private String iPLID,iDIVI
  private boolean lineExists = false

  public DelPlayerHead(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
    this.mi = mi
    this.database = database
    this.program = program
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
    checkIfLineExists()
    if(!lineExists) {
      deleteRecord()
    }else
    {
      mi.error("Please delete the lines for PlayerID : "+iPLID+" before deleting the header.")
    }
  }

  /**
   *Check if lines exist for the player ID in EXTSTL table
   * @params
   * @return
   */
  public checkIfLineExists() {
    DBAction query = database.table("EXTSTL").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXPLID", iPLID)
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    query.readAll(container,3,readLines)

  }
  Closure<?> readLines = {
    DBContainer container ->
      lineExists = true

  }

  /**
   *Delete records from EXTSTH table
   * @params
   * @return
   */
  public deleteRecord() {
    DBAction query = database.table("EXTSTH").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXPLID", iPLID)
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    if (query.read(container)) {
      query.readLock(container, deleteCallBack)
    } else {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > deleteCallBack = {
    LockedResult lockedResult ->
      lockedResult.delete()
  }
}
