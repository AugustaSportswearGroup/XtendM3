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

    validateInput()
    if(validInput)
    {
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
          validInput=false
          return false
        }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    if(!iRIDN.trim().isEmpty())
    {
      //Validate Order Number
      params = ["CONO": iCONO.toString().trim(), "ORNO": iRIDN.toString().trim()]
      callback = {
        Map < String,
          String > response ->
          if (response.ORNO == null) {
            validInput=false
          }
      }
      miCaller.call("OIS100MI", "GetOrderHead", params, callback)
    
     //If not CO, check if valid DO
      if(!validInput)
      {
        validInput = true
        params = ["CONO": iCONO.toString().trim(), "TRNR": iRIDN.toString().trim()]
        callback = {
        Map < String,
          String > response ->
          if (response.TRNR == null) {
            mi.error("Invalid Order Number " + iRIDN)
            validInput=false
            return false
          }
        }
        miCaller.call("MMS100MI", "GetHead", params, callback)
      }
    }
    
    if(iDLIX!=0)
    {
      //Validate Delivery Number
      params = ["CONO": iCONO.toString().trim(),"DLIX":iDLIX.toString().trim()]
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
    
    if(!iWHLO.trim().isEmpty())
    {
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