/**
 * README
 * This extension is being used to Add records to EXTPCE table. 
 *
 * Name: EXT010MI.AddStockInfo
 * Description: Adding records to EXTPCE table 
 * Date	      Changed By                      Description
 *20230627  SuriyaN@fortude.co     Adding records to EXTPCE table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddStockInfo extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iCHST, iSEQN, iSLTP, iAISL, iLBAY, iLEVL, iSLOT, iWHLT, iATV1, iATV2, iATV3, iATV4, iATV5, iWHSL
  private int iCONO
  boolean validInput = true

  public AddStockInfo(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iCHST = (mi.inData.get("CHST") == null || mi.inData.get("CHST").trim().isEmpty()) ? "" : mi.inData.get("CHST")
    iSEQN = (mi.inData.get("SEQN") == null || mi.inData.get("SEQN").trim().isEmpty()) ? "" : mi.inData.get("SEQN")
    iSLTP = (mi.inData.get("SLTP") == null || mi.inData.get("SLTP").trim().isEmpty()) ? "" : mi.inData.get("SLTP")
    iAISL = (mi.inData.get("AISL") == null || mi.inData.get("AISL").trim().isEmpty()) ? "" : mi.inData.get("AISL")
    iLBAY = (mi.inData.get("LBAY") == null || mi.inData.get("LBAY").trim().isEmpty()) ? "" : mi.inData.get("LBAY")
    iLEVL = (mi.inData.get("LEVL") == null || mi.inData.get("LEVL").trim().isEmpty()) ? "" : mi.inData.get("LEVL")
    iSLOT = (mi.inData.get("SLOT") == null || mi.inData.get("SLOT").trim().isEmpty()) ? "" : mi.inData.get("SLOT")
    iWHLT = (mi.inData.get("WHLT") == null || mi.inData.get("WHLT").trim().isEmpty()) ? "" : mi.inData.get("WHLT")
    iATV1 = (mi.inData.get("ATV1") == null || mi.inData.get("ATV1").trim().isEmpty()) ? "" : mi.inData.get("ATV1")
    iATV2 = (mi.inData.get("ATV2") == null || mi.inData.get("ATV2").trim().isEmpty()) ? "" : mi.inData.get("ATV2")
    iATV3 = (mi.inData.get("ATV3") == null || mi.inData.get("ATV3").trim().isEmpty()) ? "" : mi.inData.get("ATV3")
    iATV4 = (mi.inData.get("ATV4") == null || mi.inData.get("ATV4").trim().isEmpty()) ? "" : mi.inData.get("ATV4")
    iATV5 = (mi.inData.get("ATV5") == null || mi.inData.get("ATV5").trim().isEmpty()) ? "" : mi.inData.get("ATV5")
    iWHSL = (mi.inData.get("WHSL") == null || mi.inData.get("WHSL").trim().isEmpty()) ? "" : mi.inData.get("WHSL")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

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
    Map < String, String > params = ["CONO": iCONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return
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
        return
      }
    }
    miCaller.call("MMS005MI", "GetWarehouse", params, callback)

    //Validate Location Number
    params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim(), "WHSL": iWHSL.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.WHLO == null) {
        mi.error("Invalid Location " + iWHSL)
        validInput = false
        return
      }
    }
    miCaller.call("MMS010MI", "GetLocation", params, callback)

    //Validate Stock Zone
    if (!iSLTP.toString().trim().isEmpty()) {
      params = ["WHLO": iWHLO.toString().trim(), "SLTP": iSLTP.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.SLTP == null) {
          mi.error("Invalid Stock Zone " + iSLTP)
          validInput = false
          return
        }
      }
      miCaller.call("MMS040MI", "GetStockZone", params, callback)
    }

    //Validate Location Type
    if (!iWHLT.toString().trim().isEmpty()) {
      DBAction query = database.table("MITPTY").index("00").build()
      DBContainer container = query.getContainer()
      container.set("MJCONO", iCONO)
      container.set("MJWHLT", iWHLT.toString().trim())
      if (!query.read(container)) {
        mi.error("Invalid Location Type " + iWHLT)
        validInput = false
        return
      }
    }
  }

  /**
   *Insert records to EXTPCE table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTPCE").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXWHLO", iWHLO)
    query.set("EXWHSL", iWHSL)
    query.set("EXCHST", iCHST)
    query.set("EXSEQN", iSEQN)
    query.set("EXSLTP", iSLTP)
    query.set("EXAISL", iAISL)
    query.set("EXLBAY", iLBAY)
    query.set("EXLEVL", iLEVL)
    query.set("EXSLOT", iSLOT)
    query.set("EXWHLT", iWHLT)
    query.set("EXATV1", iATV1)
    query.set("EXATV2", iATV2)
    query.set("EXATV3", iATV3)
    query.set("EXATV4", iATV4)
    query.set("EXATV5", iATV5)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXCHNO", 0)
    query.set("EXCHID", program.getUser())
    action.insert(query, recordExists)
  }

  Closure recordExists = {
    mi.error("Record Already Exists")
    return
  }
}