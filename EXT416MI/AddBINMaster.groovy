/**
 * README
 * This extension is being used to Add records to EXTMAS table. 
 *
 * Name: EXT416MI.AddBINMaster
 * Description: Adding records to EXTMAS table 
 * Date	      Changed By                      Description
 *20230927  SuriyaN@fortude.co     Adding records to EXTMAS table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddBINMaster extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iBNNO, iTX30
  private int iCONO
  private Double iWEIG, iVOL3, iVOM3, iVOMT, iPACL, iPACW, iPACH
  private boolean validInput = true
  
  public AddBINMaster(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iBNNO = (mi.inData.get("BNNO") == null || mi.inData.get("BNNO").trim().isEmpty()) ? "" : mi.inData.get("BNNO")
    iTX30 = (mi.inData.get("TX30") == null || mi.inData.get("TX30").trim().isEmpty()) ? "" : mi.inData.get("TX30")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iWEIG = (mi.inData.get("WEIG") == null || mi.inData.get("WEIG").trim().isEmpty()) ? 0 : mi.inData.get("WEIG") as Double
    iVOL3 = (mi.inData.get("VOL3") == null || mi.inData.get("VOL3").trim().isEmpty()) ? 0 : mi.inData.get("VOL3") as Double
    iVOM3 = (mi.inData.get("VOM3") == null || mi.inData.get("VOM3").trim().isEmpty()) ? 0 : mi.inData.get("VOM3") as Double
    iVOMT = (mi.inData.get("VOMT") == null || mi.inData.get("VOMT").trim().isEmpty()) ? 0 : mi.inData.get("VOMT") as Double
    iPACL = (mi.inData.get("PACL") == null || mi.inData.get("PACL").trim().isEmpty()) ? 0 : mi.inData.get("PACL") as Double
    iPACW = (mi.inData.get("PACW") == null || mi.inData.get("PACW").trim().isEmpty()) ? 0 : mi.inData.get("PACW") as Double
    iPACH = (mi.inData.get("PACH") == null || mi.inData.get("PACH").trim().isEmpty()) ? 0 : mi.inData.get("PACH") as Double

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
    
  }
  
  
  /**
   *Insert records to EXTMAS table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTMAS").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXWHLO", iWHLO)
    query.set("EXBNNO", iBNNO)
    query.set("EXTX30", iTX30)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXWEIG", iWEIG as Double)
    query.set("EXVOL3", iVOL3 as Double)
    query.set("EXVOM3", iVOM3 as Double)
    query.set("EXVOMT", iVOMT as Double)
    query.set("EXPACL", iPACL as Double)
    query.set("EXPACW", iPACW as Double)
    query.set("EXPACH", iPACH as Double)
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