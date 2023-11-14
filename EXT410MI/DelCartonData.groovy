/**
 * README
 * This extension is being used to Delete records from EXTXFL table. 
 *
 * Name: EXT410MI.DelCaronData
 * Description: Deleting records from EXTXFL table
 * Date	      Changed By                      Description
 *20230510  SuriyaN@fortude.co    Deleting records from EXTXFL table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class DelCartonData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private int iCONO, iDLIX, iTRNO
  private String iDIVI, iWHLO, iORNO, iRCID, iRLPN, iITNO
  
  public DelCartonData(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Integer
    iTRNO = (mi.inData.get("TRNO") == null || mi.inData.get("TRNO").trim().isEmpty()) ? 0 : mi.inData.get("TRNO") as Integer

    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iRCID = (mi.inData.get("RCID") == null || mi.inData.get("RCID").trim().isEmpty()) ? "" : mi.inData.get("RCID")
    iRLPN = (mi.inData.get("RLPN") == null || mi.inData.get("RLPN").trim().isEmpty()) ? "" : mi.inData.get("RLPN")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    
      deleteRecord()
    
    
  }
  
  
  /**
   *Delete records from EXTXFL table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTXFL").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXORNO", iORNO)
    container.set("EXRCID", iRCID)
    container.set("EXRLPN", iRLPN)
    container.set("EXITNO", iITNO)
    container.set("EXDLIX", iDLIX)
    container.set("EXTRNO", iTRNO)
    if (query.read(container)) {
      query.readLock(container, deleteCallBack)
    } else {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > deleteCallBack = {
    LockedResult lockedResult ->
    lockedResult.delete()
  }
}