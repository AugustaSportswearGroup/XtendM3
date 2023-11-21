/**
 * README
 * This extension is being used to Update records to EXTPQT table. 
 *
 * Name: EXT201MI.UpdATPQuantity
 * Description: Updating records to EXTPQT table
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co    Updating records to EXTPQT table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdATPQuantity extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private String iWHLO, iITNO, iUPDS

  private int iCONO
  private double iVAL9
  public UpdATPQuantity(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iUPDS = (mi.inData.get("UPDS") == null || mi.inData.get("UPDS").trim().isEmpty()) ? "0" : mi.inData.get("UPDS")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iVAL9 = (mi.inData.get("VAL9") == null || mi.inData.get("VAL9").trim().isEmpty()) ? 0 : mi.inData.get("VAL9") as Double

    updateRecord()
  }
  /**
   *Update records to EXTPQT table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTPQT").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXWHLO", iWHLO)
    container.set("EXITNO", iITNO)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
    
    if (!iUPDS.trim().isEmpty()) {
      if (iUPDS.trim().equals("?")) {
        lockedResult.set("EXUPDS", "")
      } else {
        lockedResult.set("EXUPDS", iUPDS)
      }
    }
    
    if (mi.inData.get("VAL9")!=null&&!mi.inData.get("VAL9").trim().isEmpty()) {
            lockedResult.set("EXVAL9", iVAL9)
        }
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO+1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}