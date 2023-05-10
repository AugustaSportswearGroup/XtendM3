/**
 * README
 * This extension is being used to Delete records from EXTSTL table. 
 *
 * Name: EXT001MI.DelPlayerLine
 * Description: Deleting records from EXTSTL table
 * Date	      Changed By                      Description
 *20230406  SuriyaN@fortude.co    Deleting records from EXTSTL table
 *
 */

public class DelPlayerLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private int iCONO, iPONR

  private String iDIVI, iPLID, iORNR, iITNO
  public DelPlayerLine(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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
    iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? "" : mi.inData.get("DIVI")
    iPLID = (mi.inData.get("PLID") == null || mi.inData.get("PLID").trim().isEmpty()) ? "" : mi.inData.get("PLID")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")

    deleteRecord()
  }
  /**
   *Delete records from EXTSTL table
   * @params
   * @return
   */
  public deleteRecord() {
    DBAction query = database.table("EXTSTL").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXDIVI", iDIVI)
    container.set("EXPLID", iPLID)
    container.set("EXORNR", iORNR)
    container.set("EXITNO", iITNO)
    container.set("EXCONO", iCONO)
    container.set("EXPONR", iPONR)
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