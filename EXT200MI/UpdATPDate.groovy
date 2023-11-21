/**
 * README
 * This extension is being used to Update records to EXTPDT table. 
 *
 * Name: EXT200MI.UpdATPDate
 * Description: Updating records to EXTPDT table
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co    Updating records to EXTPDT table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdATPDate extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private String iWHLO, iITNO, iDATE, iUPDS

  private int iCONO, iORQ9
  public UpdATPDate(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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
    iDATE = (mi.inData.get("DATE") == null || mi.inData.get("DATE").trim().isEmpty()) ? "" : mi.inData.get("DATE")
    iUPDS = (mi.inData.get("UPDS") == null || mi.inData.get("UPDS").trim().isEmpty()) ? "0" : mi.inData.get("UPDS")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iORQ9 = (mi.inData.get("ORQ9") == null || mi.inData.get("ORQ9").trim().isEmpty()) ? 0 : mi.inData.get("ORQ9") as Integer

    updateRecord()
  }
  /**
   *Update records to EXTPDT table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTPDT").index("00").build()
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

    if (!iDATE.trim().isEmpty()) {
      if (iDATE.trim().equals("?")) {
        lockedResult.set("EXDATE", "")
      } else {
        lockedResult.set("EXDATE", iDATE)
      }
    }
    if (!iUPDS.trim().isEmpty()) {
      if (iUPDS.trim().equals("?")) {
        lockedResult.set("EXUPDS", "")
      } else {
        lockedResult.set("EXUPDS", iUPDS)
      }
    }
    
    if (mi.inData.get("ORQ9")!=null&&!mi.inData.get("ORQ9").trim().isEmpty()) {
            lockedResult.set("EXORQ9", iORQ9)
        }

    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.set("EXCHNO", CHNO+1)
    lockedResult.update()
  }


}