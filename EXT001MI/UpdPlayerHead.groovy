/**
 * README
 * This extension is being used to Update records to EXTSTH table. 
 *
 * Name: EXT001MI.UpdPlayerHead
 * Description: Updating records to EXTSTH table
 * Date	      Changed By                      Description
 *20230403  SuriyaN@fortude.co    Updating records to EXTSTH table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdPlayerHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iDIVI, iPLID, iORNR, iORNO, iOREF, iPLNM, iPLNU, iTEAM, iLEAG, iGRBY, iBGBY, iCUNO, iADID, iUDF1, iUDF2, iUDF3, iUDF4, iUDF5, iUDF6
  private int iCONO, CHNO, iORDT
  private boolean validInput = true
  public UpdPlayerHead(MIAPI mi, DatabaseAPI database, ProgramAPI program,MICallerAPI miCaller,UtilityAPI utility) {
    this.mi = mi
    this.database = database
    this.program = program
    this.miCaller = miCaller
    this.utility = utility
  }
  /**
   ** Main function
   * @param
   * @return
   */
  public void main() {
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iPLID = (mi.inData.get("PLID") == null || mi.inData.get("PLID").trim().isEmpty()) ? "" : mi.inData.get("PLID")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iOREF = (mi.inData.get("OREF") == null || mi.inData.get("OREF").trim().isEmpty()) ? "" : mi.inData.get("OREF")
    iPLNM = (mi.inData.get("PLNM") == null || mi.inData.get("PLNM").trim().isEmpty()) ? "" : mi.inData.get("PLNM")
    iPLNU = (mi.inData.get("PLNU") == null || mi.inData.get("PLNU").trim().isEmpty()) ? "" : mi.inData.get("PLNU")
    iTEAM = (mi.inData.get("TEAM") == null || mi.inData.get("TEAM").trim().isEmpty()) ? "" : mi.inData.get("TEAM")
    iLEAG = (mi.inData.get("LEAG") == null || mi.inData.get("LEAG").trim().isEmpty()) ? "" : mi.inData.get("LEAG")
    iGRBY = (mi.inData.get("GRBY") == null || mi.inData.get("GRBY").trim().isEmpty()) ? "" : mi.inData.get("GRBY")
    iBGBY = (mi.inData.get("BGBY") == null || mi.inData.get("BGBY").trim().isEmpty()) ? "" : mi.inData.get("BGBY")
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
    iADID = (mi.inData.get("ADID") == null || mi.inData.get("ADID").trim().isEmpty()) ? "" : mi.inData.get("ADID")
    iUDF1 = (mi.inData.get("UDF1") == null || mi.inData.get("UDF1").trim().isEmpty()) ? "" : mi.inData.get("UDF1")
    iUDF2 = (mi.inData.get("UDF2") == null || mi.inData.get("UDF2").trim().isEmpty()) ? "" : mi.inData.get("UDF2")
    iUDF3 = (mi.inData.get("UDF3") == null || mi.inData.get("UDF3").trim().isEmpty()) ? "" : mi.inData.get("UDF3")
    iUDF4 = (mi.inData.get("UDF4") == null || mi.inData.get("UDF4").trim().isEmpty()) ? "" : mi.inData.get("UDF4")
    iUDF5 = (mi.inData.get("UDF5") == null || mi.inData.get("UDF5").trim().isEmpty()) ? "" : mi.inData.get("UDF5")
    iUDF6 = (mi.inData.get("UDF6") == null || mi.inData.get("UDF6").trim().isEmpty()) ? "" : mi.inData.get("UDF6")
    

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iORDT = (mi.inData.get("ORDT") == null || mi.inData.get("ORDT").trim().isEmpty()) ? 0 : mi.inData.get("ORDT") as Integer
 
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
  public void validateInput() {

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
    
    //Validate Division
    params = ["CONO": iCONO.toString().trim(), "DIVI": iDIVI.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.DIVI == null) {
        mi.error("Invalid Division " + iDIVI)
        validInput = false
        return
      }
    }

    miCaller.call("MNS100MI", "GetBasicData", params, callback)

    if(!iCUNO.trim().isEmpty()) {
      //Validate Customer Number
      params = ["CONO": iCONO.toString().trim(), "CUNO": iCUNO.toString().trim()]
      callback = {
        Map<String,
          String> response ->
          if (response.CUNO == null) {
            mi.error("Invalid Customer Number " + iCUNO)
            validInput = false
            return 
          }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }

    if(!iORNR.trim().isEmpty())
    {
      //Validate Temporary Order Number
      DBAction query = database.table("OXHEAD").index("00").build()
      DBContainer container = query.getContainer()
      container.set("OACONO", iCONO)
      container.set("OAORNO", iORNR)

      if(!query.read(container))
      {
        mi.error("Temporary Order Number not found "+iORNR)
        validInput=false
        return 
      }
    }

    if(!iORNO.trim().isEmpty()) {
      //Validate Order Number
      params = ["CONO": iCONO.toString().trim(), "ORNO": iORNO.toString().trim()]
      callback = {
        Map<String,
          String> response ->
          if (response.ORNO == null) {
            mi.error("Invalid Order Number " + iORNO)
            validInput = false
            return 
          }
      }
      miCaller.call("OIS100MI", "GetOrderHead", params, callback)
    }
    
    if(!iADID.trim().isEmpty()) {
      //Validate Address ID
      DBAction query = database.table("OCUSAD").selection("OPADID").index("10").build()
      DBContainer container = query.getContainer()
      container.set("OPCONO", iCONO)
      container.set("OPCUNO", iCUNO)
      container.set("OPADRT", 1)
      container.set("OPADID", iADID)

      if (!query.read(container)) {
        mi.error("Address ID Not Found " + iADID)
        validInput = false
        return 
      }
    }
    
  if(mi.inData.get("ORDT")!=null&&!mi.inData.get("ORDT").trim().isEmpty())
    {
      //Validate Date
      String ORDT = iORDT+"" //Converstion to String
      String sourceFormat = utility.call("DateUtil","getFullFormat",mi.getDateFormat())
      validInput = utility.call("DateUtil","isDateValid",ORDT.trim(),sourceFormat.toString())
      if (!validInput)
      {
          mi.error("Order Date not in "+sourceFormat+" format. Please Check "+ORDT)
          return 
        }else
        {
          if(!sourceFormat.trim().equals("yyyyMMdd"))
          {
            ORDT = utility.call("DateUtil","convertDate",sourceFormat,"yyyyMMdd",ORDT.trim()) //Maintain date in YMD8 format in the table
            iORDT = Integer.parseInt(ORDT)
          }
        }
    }
  }


  /**
   *Update records to EXTSTH table
   * @params
   * @return
   */
  public void updateRecord() {
    DBAction query = database.table("EXTSTH").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXPLID", iPLID)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
      CHNO = lockedResult.get("EXCHNO")
      if (!iORNR.trim().isEmpty()) {
        if (iORNR.trim().equals("?")) {
          lockedResult.set("EXORNR", "")
        } else {
          lockedResult.set("EXORNR", iORNR)
        }
      }
      if (!iORNO.trim().isEmpty()) {
        if (iORNO.trim().equals("?")) {
          lockedResult.set("EXORNO", "")
        } else {
          lockedResult.set("EXORNO", iORNO)
        }
      }
      if (!iOREF.trim().isEmpty()) {
        if (iOREF.trim().equals("?")) {
          lockedResult.set("EXOREF", "")
        } else {
          lockedResult.set("EXOREF", iOREF)
        }
      }
      if (!iPLNM.trim().isEmpty()) {
        if (iPLNM.trim().equals("?")) {
          lockedResult.set("EXPLNM", "")
        } else {
          lockedResult.set("EXPLNM", iPLNM)
        }
      }
      if (!iPLNU.trim().isEmpty()) {
        if (iPLNU.trim().equals("?")) {
          lockedResult.set("EXPLNU", "")
        } else {
          lockedResult.set("EXPLNU", iPLNU)
        }
      }
      if (!iTEAM.trim().isEmpty()) {
        if (iTEAM.trim().equals("?")) {
          lockedResult.set("EXTEAM", "")
        } else {
          lockedResult.set("EXTEAM", iTEAM)
        }
      }
      if (!iLEAG.trim().isEmpty()) {
        if (iLEAG.trim().equals("?")) {
          lockedResult.set("EXLEAG", "")
        } else {
          lockedResult.set("EXLEAG", iLEAG)
        }
      }
      if (!iGRBY.trim().isEmpty()) {
        if (iGRBY.trim().equals("?")) {
          lockedResult.set("EXGRBY", "")
        } else {
          lockedResult.set("EXGRBY", iGRBY)
        }
      }
      if (!iBGBY.trim().isEmpty()) {
        if (iBGBY.trim().equals("?")) {
          lockedResult.set("EXBGBY", "")
        } else {
          lockedResult.set("EXBGBY", iBGBY)
        }
      }
      if (!iCUNO.trim().isEmpty()) {
        if (iCUNO.trim().equals("?")) {
          lockedResult.set("EXCUNO", "")
        } else {
          lockedResult.set("EXCUNO", iCUNO)
        }
      }
      if (!iADID.trim().isEmpty()) {
        if (iADID.trim().equals("?")) {
          lockedResult.set("EXADID", "")
        } else {
          lockedResult.set("EXADID", iADID)
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
      if (mi.inData.get("ORDT")!=null&&!mi.inData.get("ORDT").trim().isEmpty()) {
        lockedResult.set("EXORDT", iORDT as Integer)
      } 
      lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
      lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
      lockedResult.set("EXCHID", program.getUser())
      lockedResult.set("EXCHNO", CHNO+1)
      lockedResult.update()
  }
}