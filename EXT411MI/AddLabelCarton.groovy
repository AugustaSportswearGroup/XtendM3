/**
 * README
 * This extension is being used to Add records to EXTSLB table. 
 *
 * Name: EXT411MI.AddLabelCarton
 * Description: Adding records to EXTSLB table 
 * Date	      Changed By                      Description
 *20230831  SuriyaN@fortude.co     Adding records to EXTSLB table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddLabelCarton extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iRCID, iRIDN, iCSID, iLNAM, iLDEV, iMAND, iMDTA
  private int iCONO, iBXNO, iROSS, iLQTY, iRSET
  private long iDLIX
  boolean validInput = true

  public AddLabelCarton(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iRCID = (mi.inData.get("RCID") == null || mi.inData.get("RCID").trim().isEmpty()) ? "" : mi.inData.get("RCID")
    iRIDN = (mi.inData.get("RIDN") == null || mi.inData.get("RIDN").trim().isEmpty()) ? "" : mi.inData.get("RIDN")
    iCSID = (mi.inData.get("CSID") == null || mi.inData.get("CSID").trim().isEmpty()) ? "" : mi.inData.get("CSID")
    iLNAM = (mi.inData.get("LNAM") == null || mi.inData.get("LNAM").trim().isEmpty()) ? "" : mi.inData.get("LNAM")
    iLDEV = (mi.inData.get("LDEV") == null || mi.inData.get("LDEV").trim().isEmpty()) ? "" : mi.inData.get("LDEV")
    iMAND = (mi.inData.get("MAND") == null || mi.inData.get("MAND").trim().isEmpty()) ? "" : mi.inData.get("MAND")
    iMDTA = (mi.inData.get("MDTA") == null || mi.inData.get("MDTA").trim().isEmpty()) ? "" : mi.inData.get("MDTA")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iBXNO = (mi.inData.get("BXNO") == null || mi.inData.get("BXNO").trim().isEmpty()) ? 0 : mi.inData.get("BXNO") as Integer
    iROSS = (mi.inData.get("ROSS") == null || mi.inData.get("ROSS").trim().isEmpty()) ? 0 : mi.inData.get("ROSS") as Integer
    iLQTY = (mi.inData.get("LQTY") == null || mi.inData.get("LQTY").trim().isEmpty()) ? 0 : mi.inData.get("LQTY") as Integer
    iRSET = (mi.inData.get("RSET") == null || mi.inData.get("RSET").trim().isEmpty()) ? 0 : mi.inData.get("RSET") as Integer

    insertRecord()
  }
  /**
   *Insert records to EXTSLB table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTSLB").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXDIVI", iDIVI)
    query.set("EXWHLO", iWHLO)
    query.set("EXRCID", iRCID)
    query.set("EXRIDN", iRIDN)
    query.set("EXCSID", iCSID)
    query.set("EXLNAM", iLNAM)
    query.set("EXLDEV", iLDEV)
    query.set("EXMAND", iMAND)
    query.set("EXMDTA", iMDTA)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXDLIX", iDLIX as Long)
    query.set("EXBXNO", iBXNO as Integer)
    query.set("EXROSS", iROSS as Integer)
    query.set("EXLQTY", iLQTY as Integer)
    query.set("EXRSET", iRSET as Integer)
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