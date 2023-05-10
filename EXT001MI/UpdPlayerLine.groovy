/**
 * README
 * This extension is being used to Update records to EXTSTL table.
 *
 * Name: EXT001MI.UpdPlayerLine
 * Description: Updating records to EXTSTL table
 * Date       Changed By                      Description
 *20230404  SuriyaN@fortude.co    Updating records to EXTSTL table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdPlayerLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iPLID, iKLOC, iORNO, iITNO, iSNUM, iWHLO, iRORN, iUDF1, iUDF2, iUDF3, iUDF4, iUDF5, iUDF6, iORNR
  private int iCONO,iPONR,iPOSX,iRORL,iRORX
  private double iORQT
  private boolean validInput = true
  private long iDLIX

  public UpdPlayerLine(MIAPI mi, DatabaseAPI database, ProgramAPI program,MICallerAPI miCaller) {
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
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? "" : mi.inData.get("DIVI")
    iPLID = (mi.inData.get("PLID") == null || mi.inData.get("PLID").trim().isEmpty()) ? "" : mi.inData.get("PLID")
    iKLOC = (mi.inData.get("KLOC") == null || mi.inData.get("KLOC").trim().isEmpty()) ? "" : mi.inData.get("KLOC")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
    iPOSX = (mi.inData.get("POSX") == null || mi.inData.get("POSX").trim().isEmpty()) ? 0 : mi.inData.get("POSX") as Integer
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iORQT = (mi.inData.get("ORQT") == null || mi.inData.get("ORQT").trim().isEmpty()) ? 0 : mi.inData.get("ORQT") as double
    iSNUM = (mi.inData.get("SNUM") == null || mi.inData.get("SNUM").trim().isEmpty()) ? "" : mi.inData.get("SNUM")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iRORN = (mi.inData.get("RORN") == null || mi.inData.get("RORN").trim().isEmpty()) ? "" : mi.inData.get("RORN")
    iRORL = (mi.inData.get("RORL") == null || mi.inData.get("RORL").trim().isEmpty()) ? 0 : mi.inData.get("RORL") as Integer
    iRORX = (mi.inData.get("RORX") == null || mi.inData.get("RORX").trim().isEmpty()) ? 0 : mi.inData.get("RORX") as Integer
    iUDF1 = (mi.inData.get("UDF1") == null || mi.inData.get("UDF1").trim().isEmpty()) ? "" : mi.inData.get("UDF1")
    iUDF2 = (mi.inData.get("UDF2") == null || mi.inData.get("UDF2").trim().isEmpty()) ? "" : mi.inData.get("UDF2")
    iUDF3 = (mi.inData.get("UDF3") == null || mi.inData.get("UDF3").trim().isEmpty()) ? "" : mi.inData.get("UDF3")
    iUDF4 = (mi.inData.get("UDF4") == null || mi.inData.get("UDF4").trim().isEmpty()) ? "" : mi.inData.get("UDF4")
    iUDF5 = (mi.inData.get("UDF5") == null || mi.inData.get("UDF5").trim().isEmpty()) ? "" : mi.inData.get("UDF5")
    iUDF6 = (mi.inData.get("UDF6") == null || mi.inData.get("UDF6").trim().isEmpty()) ? "" : mi.inData.get("UDF6")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    validateInput()
    if(validInput) {
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
          validInput = false
          return false
        }
    }
    miCaller.call("MNS095MI", "Get", params, callback)
    //Validate Player Number
    params = ["CONO": iCONO.toString().trim(),"DIVI":iDIVI.toString().trim(),"PLID":iPLID.toString().trim()]
    callback = {
      Map < String,
              String > response ->
        if (response.PLID == null) {
          mi.error("Invalid PlayerID Number " + iPLID+". Header Record not Found.")
          validInput = false
          return false
        }
    }
    miCaller.call("EXT001MI", "GetPlayerHead", params, callback)

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

    //Validate Temporary Order Number
    DBAction query = database.table("OXHEAD").index("00").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    container.set("OAORNO", iORNR)

    if(!query.read(container))
    {
      mi.error("Temporary Order Number not found "+iORNR)
      validInput=false
      return false
    }

    //Validate Final Order Number
    if(!iORNO.trim().isEmpty())
    {
      query = database.table("OOHEAD").index("00").build()
      container = query.getContainer()
      container.set("OACONO", iCONO)
      container.set("OAORNO", iORNO)

      if(!query.read(container))
      {
        mi.error("Final Order Number not found "+iORNO)
        validInput=false
        return false
      }
    }
  }

  /**
   *Update records to EXTSTL table
   * @params
   * @return
   */
  public updateRecord() {
    DBAction query = database.table("EXTSTL").index("00").selection("EXCHNO").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXPLID", iPLID)
    container.set("EXORNR", iORNR)
    container.set("EXITNO", iITNO)
    container.set("EXPONR", iPONR)
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
      if (!iKLOC.trim().isEmpty()) {
        if (iKLOC.trim().equals("?")) {
          lockedResult.set("EXKLOC", "")
        } else {
          lockedResult.set("EXKLOC", iKLOC)
        }
      }
      if (iPOSX!=0) {
        lockedResult.set("EXPOSX", iPOSX)
      }
      if (iORQT!=0) {
        lockedResult.set("EXORQT", iORQT)
      }
      if (!iSNUM.trim().isEmpty()) {
        if (iSNUM.trim().equals("?")) {
          lockedResult.set("EXSNUM", "")
        } else {
          lockedResult.set("EXSNUM", iSNUM)
        }
      }
      if (!iWHLO.trim().isEmpty()) {
        if (iWHLO.trim().equals("?")) {
          lockedResult.set("EXWHLO", "")
        } else {
          lockedResult.set("EXWHLO", iWHLO)
        }
      }
      if (iDLIX!=0) {
        lockedResult.set("EXDLIX", iDLIX)
      }
      if (!iRORN.trim().isEmpty()) {
        if (iRORN.trim().equals("?")) {
          lockedResult.set("EXRORN", "")
        } else {
          lockedResult.set("EXRORN", iRORN)
        }
      }
      if (iRORL!=0) {
        lockedResult.set("EXRORL", iRORL)
      }
      if (iRORX!=0) {
        lockedResult.set("EXRORX", iRORX)
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
      if (!iORNO.trim().isEmpty()) {
        if (iORNO.trim().equals("?")) {
          lockedResult.set("EXORNO", "")
        } else {
          lockedResult.set("EXORNO", iORNO)
        }
      }
      lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
      lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
      lockedResult.set("EXCHID", program.getUser())
      lockedResult.set("EXCHNO", CHNO+1)
      lockedResult.update()
  }
}
