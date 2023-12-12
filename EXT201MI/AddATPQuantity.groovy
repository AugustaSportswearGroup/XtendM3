/**
 * README
 * This extension is being used to Add records to EXTPQT table. 
 *
 * Name: EXT201MI.AddATPQuantity
 * Description: Adding records to EXTPQT table 
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co     Adding records to EXTPQT table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddATPQuantity extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iITNO, iUPDS
  private int iCONO
  private double iVAL9
  private boolean validInput = true

  public AddATPQuantity(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iUPDS = (mi.inData.get("UPDS") == null || mi.inData.get("UPDS").trim().isEmpty()) ? "0" : mi.inData.get("UPDS")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iVAL9 = (mi.inData.get("VAL9") == null || mi.inData.get("VAL9").trim().isEmpty()) ? 0 : mi.inData.get("VAL9") as Double
    
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
    Map<String, String> params = ["CONO": iCONO.toString().trim()]
    Closure<?>  callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Item Number
    params = ["CONO": iCONO.toString().trim(),"ITNO":iITNO.toString().trim()]
    callback = {
      Map < String,
        String > response ->
        if (response.ITNO == null) {
          mi.error("Invalid Item Number " + iITNO)
          validInput = false
          return false
        }
    }
    
    miCaller.call("MMS200MI", "Get", params, callback)
    
     //Validate Warehouse Number
      params = ["CONO": iCONO.toString().trim(),"WHLO":iWHLO.toString().trim()]
      callback = {
        Map < String,
          String > response ->
          if (response.WHLO == null) {
            mi.error("Invalid Warehouse Number " + iWHLO)
            validInput = false
            return false
          }
      }
      miCaller.call("MMS005MI", "GetWarehouse", params, callback)

  }
  /**
   *Insert records to EXTPQT table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTPQT").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXWHLO", iWHLO)
    query.set("EXITNO", iITNO)
    query.set("EXUPDS", iUPDS)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXVAL9", iVAL9 as Double)
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