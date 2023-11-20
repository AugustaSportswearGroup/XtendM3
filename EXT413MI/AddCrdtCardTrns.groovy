/**
 * README
 * This extension is being used to Add records to EXTCCT table. 
 *
 * Name: EXT413MI.AddCrdtCardTrns
 * Description: Adding records to EXTCCT table 
 * Date	      Changed By                      Description
 *20230912  SuriyaN@fortude.co     Adding records to EXTCCT table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddCrdtCardTrns extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDLST
  private int iCONO, iDLSP
  private long iDLIX
  private boolean validInput = true

  public AddCrdtCardTrns(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iDLST = (mi.inData.get("DLST") == null || mi.inData.get("DLST").trim().isEmpty()) ? "" : mi.inData.get("DLST")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iDLSP = (mi.inData.get("DLSP") == null || mi.inData.get("DLSP").trim().isEmpty()) ? 0 : mi.inData.get("DLSP") as Integer

    validateInput()
    if (validInput) {
      insertRecord()
    }
  }

  /**
   *Validate Records
   * @params
   * @return 
   */
  public validateInput() {

    //Validate Company Number
    def params = ["CONO": iCONO.toString().trim()]
    def callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Delivery Number
    params = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.DLIX == null) {
        mi.error("Invalid Delivery Number " + iDLIX)
        validInput = false
      }
    }
    miCaller.call("MWS410MI", "GetHead", params, callback)

  }

  /**
   *Insert records to EXTCCT table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTCCT").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXDLST", iDLST)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXDLIX", iDLIX as Long)
    query.set("EXDLSP", iDLSP as Integer)
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