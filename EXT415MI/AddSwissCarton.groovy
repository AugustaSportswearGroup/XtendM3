/**
 * README
 * This extension is being used to Add records to EXTYPE table. 
 *
 * Name: EXT415MI.AddSwissCarton
 * Description: Adding records to EXTYPE table 
 * Date	      Changed By                      Description
 *20230927  SuriyaN@fortude.co     Adding records to EXTYPE table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddSwissCarton extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iTRTP, iLFMT, iSFMT
  private int iCONO, iLANE, iTTYP
  private Double iVOL3
  private boolean validInput = true

  public AddSwissCarton(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iTRTP = (mi.inData.get("TRTP") == null || mi.inData.get("TRTP").trim().isEmpty()) ? "" : mi.inData.get("TRTP")
    iLFMT = (mi.inData.get("LFMT") == null || mi.inData.get("LFMT").trim().isEmpty()) ? "" : mi.inData.get("LFMT")
    iSFMT = (mi.inData.get("SFMT") == null || mi.inData.get("SFMT").trim().isEmpty()) ? "" : mi.inData.get("SFMT")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iVOL3 = (mi.inData.get("VOL3") == null || mi.inData.get("VOL3").trim().isEmpty()) ? 0 : mi.inData.get("VOL3") as Double
    iLANE = (mi.inData.get("LANE") == null || mi.inData.get("LANE").trim().isEmpty()) ? 0 : mi.inData.get("LANE") as Integer
    iTTYP = (mi.inData.get("TTYP") == null || mi.inData.get("TTYP").trim().isEmpty()) ? 0 : mi.inData.get("TTYP") as Integer

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

    //Validate Warehouse Number
    params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim()]
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
    
    //Validate Order Type
    params = ["CONO": iCONO.toString().trim(), "TRTP": iTRTP.toString().trim(),"TTYP": iTTYP.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.TRTP == null) {
        mi.error("Invalid Order Type - Transaction Type")
        validInput = false
        return false
      }
    }
    miCaller.call("CRS200MI", "LstOrderType", params, callback)
    
  }
  
  /**
   *Insert records to EXTYPE table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTYPE").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXWHLO", iWHLO)
    query.set("EXTRTP", iTRTP)
    query.set("EXLFMT", iLFMT)
    query.set("EXSFMT", iSFMT)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXVOL3", iVOL3 as Double)
    query.set("EXLANE", iLANE as Integer)
    query.set("EXTTYP", iTTYP as Integer)
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