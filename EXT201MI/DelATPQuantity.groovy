/**
 * README
 * This extension is being used to Delete records from EXTPQT table. 
 *
 * Name: EXT201MI.DelATPQuantity
 * Description: Deleting records from EXTPQT table
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co    Deleting records from EXTPQT table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class DelATPQuantity extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private int iCONO

  private String iWHLO, iITNO
  public DelATPQuantity(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    deleteRecord()
  }
  /**
   *Delete records from EXTPQT table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTPQT").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXWHLO", iWHLO)
    container.set("EXITNO", iITNO)
    container.set("EXCONO", iCONO)
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