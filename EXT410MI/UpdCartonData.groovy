/**
 * README
 * This extension is being used to Update records to EXTXFL table. 
 *
 * Name: EXT410MI.UpdCartonData
 * Description: Updating records to EXTXFL table
 * Date       Changed By                      Description
 *20230510  SuriyaN@fortude.co    Updating records to EXTXFL table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdCartonData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  

  private String iDIVI, iWHLO, iORNO, iRCID, iRLPN, iITNO, iBXNO, iUDF0, iUDF1, iUDF2, iUDF3, iUDF4, iUDF5, iUDF6, iUDF7, iUDF8, iUDF9,iQUAN, iQWGT, iTWGT
  private int iCONO, iTRNO, iSTAT, iPONR
  private Long iDLIX
  boolean validInput = true
  
  public UpdCartonData(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iRCID = (mi.inData.get("RCID") == null || mi.inData.get("RCID").trim().isEmpty()) ? "" : mi.inData.get("RCID")
    iRLPN = (mi.inData.get("RLPN") == null || mi.inData.get("RLPN").trim().isEmpty()) ? "" : mi.inData.get("RLPN")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iBXNO = (mi.inData.get("BXNO") == null || mi.inData.get("BXNO").trim().isEmpty()) ? "" : mi.inData.get("BXNO")
    iUDF0 = (mi.inData.get("UDF0") == null || mi.inData.get("UDF0").trim().isEmpty()) ? "" : mi.inData.get("UDF0")
    iUDF1 = (mi.inData.get("UDF1") == null || mi.inData.get("UDF1").trim().isEmpty()) ? "" : mi.inData.get("UDF1")
    iUDF2 = (mi.inData.get("UDF2") == null || mi.inData.get("UDF2").trim().isEmpty()) ? "" : mi.inData.get("UDF2")
    iUDF3 = (mi.inData.get("UDF3") == null || mi.inData.get("UDF3").trim().isEmpty()) ? "" : mi.inData.get("UDF3")
    iUDF4 = (mi.inData.get("UDF4") == null || mi.inData.get("UDF4").trim().isEmpty()) ? "" : mi.inData.get("UDF4")
    iUDF5 = (mi.inData.get("UDF5") == null || mi.inData.get("UDF5").trim().isEmpty()) ? "" : mi.inData.get("UDF5")
    iUDF6 = (mi.inData.get("UDF6") == null || mi.inData.get("UDF6").trim().isEmpty()) ? "" : mi.inData.get("UDF6")
    iUDF7 = (mi.inData.get("UDF7") == null || mi.inData.get("UDF7").trim().isEmpty()) ? "" : mi.inData.get("UDF7")
    iUDF8 = (mi.inData.get("UDF8") == null || mi.inData.get("UDF8").trim().isEmpty()) ? "" : mi.inData.get("UDF8")
    iUDF9 = (mi.inData.get("UDF9") == null || mi.inData.get("UDF9").trim().isEmpty()) ? "" : mi.inData.get("UDF9")
    iQUAN = (mi.inData.get("QUAN") == null || mi.inData.get("QUAN").trim().isEmpty()) ? "" : mi.inData.get("QUAN") 
    iQWGT = (mi.inData.get("QWGT") == null || mi.inData.get("QWGT").trim().isEmpty()) ? "" : mi.inData.get("QWGT") 
    iTWGT = (mi.inData.get("TWGT") == null || mi.inData.get("TWGT").trim().isEmpty()) ? "" : mi.inData.get("TWGT") 

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iSTAT = (mi.inData.get("STAT") == null || mi.inData.get("STAT").trim().isEmpty()) ? 0 : mi.inData.get("STAT") as Integer
    iTRNO = (mi.inData.get("TRNO") == null || mi.inData.get("TRNO").trim().isEmpty()) ? 0 : mi.inData.get("TRNO") as Integer
    iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
   
    validateInput()
    if(validInput)
    {
       updateRecord()
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
    if(!iORNO.trim().isEmpty())
    {
      //Validate Order Number
      params = ["CONO": iCONO.toString().trim(), "ORNO": iORNO.toString().trim()]
      callback = {
        Map < String,
          String > response ->
          if (response.ORNO == null) {
           validInput=false
          }
      }
      miCaller.call("OIS100MI", "GetHead", params, callback)
      
      //If not CO, check if valid DO
      if(!validInput)
      {
        validInput = true
        params = ["CONO": iCONO.toString().trim(), "TRNR": iORNO.toString().trim()]
        callback = {
        Map < String,
          String > response ->
          if (response.TRNR == null) {
            mi.error("Invalid Order Number " + iORNO)
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
            return false
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
   *Update records to EXTXFL table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTXFL").selection("EXCHNO").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXORNO", iORNO)
    container.set("EXDLIX", iDLIX)
    container.set("EXRCID", iRCID)
    container.set("EXRLPN", iRLPN)
    container.set("EXITNO", iITNO)
    container.set("EXTRNO", iTRNO)
    if (query.read(container)) {
      query.readLock(container, updateCallBack)
    } else {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
    if (!iBXNO.trim().isEmpty()) {
      if (iBXNO.trim().equals("?")) {
        lockedResult.set("EXBXNO", "")
      } else {
        lockedResult.set("EXBXNO", iBXNO)
      }
    }
    if (!iUDF0.trim().isEmpty()) {
      if (iUDF0.trim().equals("?")) {
        lockedResult.set("EXUDF0", "")
      } else {
        lockedResult.set("EXUDF0", iUDF0)
      }
    }
    if (!iUDF1.trim().isEmpty()) {
      if (iUDF1.trim().equals("?")) {
        lockedResult.set("EXUDF1", "")
      } else {
        lockedResult.set("EXUDF1", iUDF1)
      }
    }
    if (!iUDF2.trim().isEmpty()) {
      if (iUDF2.trim().equals("?")) {
        lockedResult.set("EXUDF2", "")
      } else {
        lockedResult.set("EXUDF2", iUDF2)
      }
    }
    if (!iUDF3.trim().isEmpty()) {
      if (iUDF3.trim().equals("?")) {
        lockedResult.set("EXUDF3", "")
      } else {
        lockedResult.set("EXUDF3", iUDF3)
      }
    }
    if (!iUDF4.trim().isEmpty()) {
      if (iUDF4.trim().equals("?")) {
        lockedResult.set("EXUDF4", "")
      } else {
        lockedResult.set("EXUDF4", iUDF4)
      }
    }
    if (!iUDF5.trim().isEmpty()) {
      if (iUDF5.trim().equals("?")) {
        lockedResult.set("EXUDF5", "")
      } else {
        lockedResult.set("EXUDF5", iUDF5)
      }
    }
    if (!iUDF6.trim().isEmpty()) {
      if (iUDF6.trim().equals("?")) {
        lockedResult.set("EXUDF6", "")
      } else {
        lockedResult.set("EXUDF6", iUDF6)
      }
    }
    if (!iUDF7.trim().isEmpty()) {
      if (iUDF7.trim().equals("?")) {
        lockedResult.set("EXUDF7", "")
      } else {
        lockedResult.set("EXUDF7", iUDF7)
      }
    }
    if (!iUDF8.trim().isEmpty()) {
      if (iUDF8.trim().equals("?")) {
        lockedResult.set("EXUDF8", "")
      } else {
        lockedResult.set("EXUDF8", iUDF8)
      }
    }
    if (!iUDF9.trim().isEmpty()) {
      if (iUDF9.trim().equals("?")) {
        lockedResult.set("EXUDF9", "")
      } else {
        lockedResult.set("EXUDF9", iUDF9)
      }
    }
    if (mi.inData.get("QUAN")!=null&&!mi.inData.get("QUAN").trim().isEmpty()) {
      lockedResult.set("EXQUAN", iQUAN as float)
    } 
    if (mi.inData.get("QWGT")!=null&&!mi.inData.get("QWGT").trim().isEmpty()) {
      lockedResult.set("EXQWGT", iQWGT as float)
    }
    if (mi.inData.get("TWGT")!=null&&!mi.inData.get("TWGT").trim().isEmpty()) {
      lockedResult.set("EXTWGT", iTWGT as float)
    } 
    if (mi.inData.get("STAT")!=null&&!mi.inData.get("STAT").trim().isEmpty()) {
      lockedResult.set("EXSTAT", iSTAT as Integer)
    } 
    if (mi.inData.get("PONR")!=null&&!mi.inData.get("PONR").trim().isEmpty()) {
      lockedResult.set("PONR", iPONR as Integer)
    } 
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO+1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }

}