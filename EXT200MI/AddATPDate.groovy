/**
 * README
 * This extension is being used to Add records to EXTPDT table. 
 *
 * Name: EXT200MI.AddATPDate
 * Description: Adding records to EXTPDT table 
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co     Adding records to EXTPDT table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddATPDate extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private String iWHLO, iITNO, iDATE, iUPDS
  private int iCONO, iORQ9

  public AddATPDate(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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

    insertRecord()
  }
  /**
   *Insert records to EXTPDT table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTPDT").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXWHLO", iWHLO)
    query.set("EXITNO", iITNO)
    query.set("EXDATE", iDATE)
    query.set("EXUPDS", iUPDS)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXORQ9", iORQ9 as Integer)
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