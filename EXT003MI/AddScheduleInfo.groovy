/**
 * README
 * This extension is being used to Add records to EXTOPT table. 
 *
 * Name: EXT003MI.AddScheduleInfo
 * Description: Adding records to EXTOPT table
 * Date	      Changed By                      Description
 *23-02-03    SuriyaN@fortude.co         Adding records to EXTOPT table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddScheduleInfo extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iFACI, iSTYL, iFMT1, iFABR, iCOLR, iSIZE, iPRNO, iPRCL, iMTNO, iDWPO, iFMT2, iCNQT, iDIM1, iDIM2, iDIM3, iWHLO, iSPE1, iSPE2, iITCL
  private int iCONO, iSCHN, iMFNO, iMSEQ, iSTDT, iFIDT, iSQNX
  private double iORQA
  private boolean validDate = true

  public AddScheduleInfo(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iFACI = (mi.inData.get("FACI") == null || mi.inData.get("FACI").trim().isEmpty()) ? "" : mi.inData.get("FACI")
    iSTYL = (mi.inData.get("STYL") == null || mi.inData.get("STYL").trim().isEmpty()) ? "" : mi.inData.get("STYL")
    iFMT1 = (mi.inData.get("FMT1") == null || mi.inData.get("FMT1").trim().isEmpty()) ? "" : mi.inData.get("FMT1")
    iFABR = (mi.inData.get("FABR") == null || mi.inData.get("FABR").trim().isEmpty()) ? "" : mi.inData.get("FABR")
    iCOLR = (mi.inData.get("COLR") == null || mi.inData.get("COLR").trim().isEmpty()) ? "" : mi.inData.get("COLR")
    iSIZE = (mi.inData.get("SIZE") == null || mi.inData.get("SIZE").trim().isEmpty()) ? "" : mi.inData.get("SIZE")
    iPRNO = (mi.inData.get("PRNO") == null || mi.inData.get("PRNO").trim().isEmpty()) ? "" : mi.inData.get("PRNO")
    iPRCL = (mi.inData.get("PRCL") == null || mi.inData.get("PRCL").trim().isEmpty()) ? "" : mi.inData.get("PRCL")
    iMTNO = (mi.inData.get("MTNO") == null || mi.inData.get("MTNO").trim().isEmpty()) ? "" : mi.inData.get("MTNO")
    iDWPO = (mi.inData.get("DWPO") == null || mi.inData.get("DWPO").trim().isEmpty()) ? "" : mi.inData.get("DWPO")
    iFMT2 = (mi.inData.get("FMT2") == null || mi.inData.get("FMT2").trim().isEmpty()) ? "" : mi.inData.get("FMT2")
    iCNQT = (mi.inData.get("CNQT") == null || mi.inData.get("CNQT").trim().isEmpty()) ? "" : mi.inData.get("CNQT")
    iDIM1 = (mi.inData.get("DIM1") == null || mi.inData.get("DIM1").trim().isEmpty()) ? "" : mi.inData.get("DIM1")
    iDIM2 = (mi.inData.get("DIM2") == null || mi.inData.get("DIM2").trim().isEmpty()) ? "" : mi.inData.get("DIM2")
    iDIM3 = (mi.inData.get("DIM3") == null || mi.inData.get("DIM3").trim().isEmpty()) ? "" : mi.inData.get("DIM3")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iSPE1 = (mi.inData.get("SPE1") == null || mi.inData.get("SPE1").trim().isEmpty()) ? "" : mi.inData.get("SPE1")
    iSPE2 = (mi.inData.get("SPE2") == null || mi.inData.get("SPE2").trim().isEmpty()) ? "" : mi.inData.get("SPE2")
    iITCL = (mi.inData.get("ITCL") == null || mi.inData.get("ITCL").trim().isEmpty()) ? "" : mi.inData.get("ITCL")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? 0 : mi.inData.get("CONO") as Integer
    iSCHN = (mi.inData.get("SCHN") == null || mi.inData.get("SCHN").trim().isEmpty()) ? 0 : mi.inData.get("SCHN") as Integer
    iMFNO = (mi.inData.get("MFNO") == null || mi.inData.get("MFNO").trim().isEmpty()) ? 0 : mi.inData.get("MFNO") as Integer
    iMSEQ = (mi.inData.get("MSEQ") == null || mi.inData.get("MSEQ").trim().isEmpty()) ? 0 : mi.inData.get("MSEQ") as Integer
    iORQA = (mi.inData.get("ORQA") == null || mi.inData.get("ORQA").trim().isEmpty()) ? 0 : mi.inData.get("ORQA") as Double
    iSTDT = (mi.inData.get("STDT") == null || mi.inData.get("STDT").trim().isEmpty()) ? 0 : mi.inData.get("STDT") as Integer
    iFIDT = (mi.inData.get("FIDT") == null || mi.inData.get("FIDT").trim().isEmpty()) ? 0 : mi.inData.get("FIDT") as Integer
    iSQNX = (mi.inData.get("SQNX") == null || mi.inData.get("SQNX").trim().isEmpty()) ? 0 : mi.inData.get("SQNX") as Integer

    boolean validInput = validateInput(iCONO, iSCHN, iMFNO, iMTNO, iFACI, iWHLO, iSTDT, iFIDT)
    if (validInput) {
      insertRecord()
    }
  }

  /**
   *Validate inputs
   * @params int CONO , int SCHN, int MFNO, String MTNO
   * @return boolean
   */
  private boolean validateInput(int CONO, int SCHN, int MFNO, String MTNO, String FACI, String WHLO, int STDT, int FIDT) {
    //Validate Company Number
    Map < String, String > params = ["CONO": CONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + CONO)
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Schedule Number
    params = ["CONO": CONO.toString().trim(), "SCHN": SCHN.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.SCHN == null) {
        mi.error("Invalid Schedule Number " + SCHN)
        return false
      }
    }
    miCaller.call("PMS270MI", "GetScheduleNo", params, callback)

    //Validate Manufacturing Number
    params = ["CONO": CONO.toString().trim(), "QERY": "VHMFNO from MWOHED where VHMFNO = '" + MFNO + "'"]
    callback = {
      Map < String,
      String > response ->
      if (response.REPL == null) {
        mi.error("Invalid Manufacturing Number " + MFNO)
        return false
      }
    }
    miCaller.call("EXPORTMI", "Select", params, callback)

    //Validate Item Number
    params = ["ITNO": MTNO]
    callback = {
      Map < String,
      String > response ->
      if (response.ITNO == null) {
        mi.error("Invalid Component Number " + MTNO)
        return false
      }
    }
    miCaller.call("MMS200MI", "Get", params, callback)

    if (!FACI.trim().isEmpty()) {
      //Validate Facility Number 
      params = ["CONO": CONO.toString().trim(), "FACI": FACI.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.FACI == null) {
          mi.error("Invalid Facility  " + FACI)
          return false
        }
      }
      miCaller.call("CRS008MI", "Get", params, callback)
    }
    if (!WHLO.trim().isEmpty()) {
      //Validate Warehouse Number
      params = ["CONO": CONO.toString().trim(), "WHLO": WHLO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.WHLO == null) {
          mi.error("Invalid Warehouse Number " + WHLO)
          return false
        }
      }
      miCaller.call("MMS005MI", "GetWarehouse", params, callback)
    }

    if (!STDT.toString().trim().isEmpty()) {
      //Validate Start date
      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validDate = utility.call("DateUtil", "isDateValid", STDT.toString().trim(), sourceFormat.toString())
      if (!validDate) {
        mi.error("Order Date not in " + sourceFormat + " format. Please Check " + STDT)
        return false
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          STDT = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", STDT.toString().trim()) //Maintain date in YMD8 format in the table
        }
      }
    }

    if (!FIDT.toString().trim().isEmpty()) {
      //Validate Finish date
      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validDate = utility.call("DateUtil", "isDateValid", FIDT.toString().trim(), sourceFormat.toString())
      if (!validDate) {
        mi.error("Order Date not in " + sourceFormat + " format. Please Check " + FIDT)
        return false
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          FIDT = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", FIDT.toString().trim()) //Maintain date in YMD8 format in the table
        }
      }
    }

    return true

  }

  /**
   *Insert records to EXTOPT table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTOPT").index("00").build()
    DBContainer query = action.getContainer()
    query.set("EXCONO", iCONO)
    query.set("EXFACI", iFACI)
    query.set("EXSTYL", iSTYL)
    query.set("EXFMT1", iFMT1)
    query.set("EXFABR", iFABR)
    query.set("EXCOLR", iCOLR)
    query.set("EXSIZE", iSIZE)
    query.set("EXPRNO", iPRNO)
    query.set("EXPRCL", iPRCL)
    query.set("EXMTNO", iMTNO)
    query.set("EXDWPO", iDWPO)
    query.set("EXFMT2", iFMT2)
    query.set("EXCNQT", iCNQT)
    query.set("EXDIM1", iDIM1)
    query.set("EXDIM2", iDIM2)
    query.set("EXDIM3", iDIM3)
    query.set("EXWHLO", iWHLO)
    query.set("EXSPE1", iSPE1)
    query.set("EXSPE2", iSPE2)
    query.set("EXITCL", iITCL)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXSCHN", iSCHN as Integer)
    query.set("EXMFNO", iMFNO as Integer)
    query.set("EXMSEQ", iMSEQ as Integer)
    query.set("EXORQA", iORQA as Double)
    query.set("EXSTDT", iSTDT as Integer)
    query.set("EXFIDT", iFIDT as Integer)
    query.set("EXSQNX", iSQNX as Integer)
    query.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXCHNO", 0)
    query.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXCHID", program.getUser())
    query.set("EXCRID", program.getUser())
    action.insert(query, recordExists)
  }

  Closure recordExists = {
    mi.error("Record Already Exists")
    return
  }
}