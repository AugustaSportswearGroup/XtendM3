/**
 * README
 * This extension is being used to Update records to EXTOPT table for EDI turnaround data. 
 *
 * Name: EXT003MI.UpdScheduleInfo
 * Description: Update records to EXTOPT table
 * Date	      Changed By                      Description
 *23-02-03    SuriyaN@fortude.co         Update records to EXTOPT table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdScheduleInfo extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iFACI, iSTYL, iFMT1, iFABR, iCOLR, iSIZE, iPRNO, iPRCL, iMTNO, iDWPO, iFMT2, iCNQT, iDIM1, iDIM2, iDIM3, iWHLO, iSPE1, iSPE2, iITCL
  private int iCONO, iSCHN, iMFNO, iMSEQ, iSTDT, iFIDT, iSQNX
  private double iORQA
  private boolean validDate = true

  public UpdScheduleInfo(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iSCHN = (mi.inData.get("SCHN") == null || mi.inData.get("SCHN").trim().isEmpty()) ? 0 : mi.inData.get("SCHN") as Integer
    iMFNO = (mi.inData.get("MFNO") == null || mi.inData.get("MFNO").trim().isEmpty()) ? 0 : mi.inData.get("MFNO") as Integer
    iMSEQ = (mi.inData.get("MSEQ") == null || mi.inData.get("MSEQ").trim().isEmpty()) ? 0 : mi.inData.get("MSEQ") as Integer
    iORQA = (mi.inData.get("ORQA") == null || mi.inData.get("ORQA").trim().isEmpty()) ? 0 : mi.inData.get("ORQA") as Double
    iSTDT = (mi.inData.get("STDT") == null || mi.inData.get("STDT").trim().isEmpty()) ? 0 : mi.inData.get("STDT") as Integer
    iFIDT = (mi.inData.get("FIDT") == null || mi.inData.get("FIDT").trim().isEmpty()) ? 0 : mi.inData.get("FIDT") as Integer
    iSQNX = (mi.inData.get("SQNX") == null || mi.inData.get("SQNX").trim().isEmpty()) ? 0 : mi.inData.get("SQNX") as Integer

    boolean validInput = validateInput(iCONO, iSCHN, iMFNO, iMTNO, iFACI, iWHLO, iSTDT, iFIDT, iITCL, iPRNO)
    if (validInput) {
      updateRecord()
    }
  }

  /**
   *Validate inputs
   * @params int CONO , int SCHN, int MFNO, String MTNO
   * @return boolean
   */
  private boolean validateInput(int CONO, int SCHN, int MFNO, String MTNO, String FACI, String WHLO, int STDT, int FIDT, String ITCL, String PRNO) {
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
      Closure < ? > callbackWHLO = {
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

    //Validate Product group
    if (!ITCL.toString().trim().isEmpty()) {
      params = ["ITCL": ITCL.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ITCL == null) {
          mi.error("Invalid Product Group " + ITCL)
          return false
        }
      }
      miCaller.call("CRS035MI", "GetProductGroup", params, callback)
    }

    //Validate Product Number
    if (!PRNO.toString().trim().isEmpty()) {
      params = ["CONO": CONO.toString().trim(), "QERY": "PHPRNO from MPDHED where PHRNO = ' " + PRNO + " and PHFACI = " + FACI + " '"]
      callback = {
        Map < String,
        String > response ->
        if (response.REPL == null) {
          mi.error("Invalid Product Number " + PRNO)
          return false
        }
      }
      miCaller.call("EXPORTMI", "Select", params, callback)
    }

    return true

  }

  /**
   *Update records to EXTOPT table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTOPT").index("00").build()
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO)
    container.set("EXSCHN", iSCHN)
    container.set("EXMFNO", iMFNO)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")

    if (!iFACI.trim().isEmpty()) {
      if (iFACI.trim().equals("?")) {
        lockedResult.set("EXFACI", "")
      } else {
        lockedResult.set("EXFACI", iFACI)
      }
    }
    if (!iSTYL.trim().isEmpty()) {
      if (iSTYL.trim().equals("?")) {
        lockedResult.set("EXSTYL", "")
      } else {
        lockedResult.set("EXSTYL", iSTYL)
      }
    }
    if (!iFMT1.trim().isEmpty()) {
      if (iFMT1.trim().equals("?")) {
        lockedResult.set("EXFMT1", "")
      } else {
        lockedResult.set("EXFMT1", iFMT1)
      }
    }
    if (!iFABR.trim().isEmpty()) {
      if (iFABR.trim().equals("?")) {
        lockedResult.set("EXFABR", "")
      } else {
        lockedResult.set("EXFABR", iFABR)
      }
    }
    if (!iCOLR.trim().isEmpty()) {
      if (iCOLR.trim().equals("?")) {
        lockedResult.set("EXCOLR", "")
      } else {
        lockedResult.set("EXCOLR", iCOLR)
      }
    }
    if (!iSIZE.trim().isEmpty()) {
      if (iSIZE.trim().equals("?")) {
        lockedResult.set("EXSIZE", "")
      } else {
        lockedResult.set("EXSIZE", iSIZE)
      }
    }
    if (!iPRNO.trim().isEmpty()) {
      if (iPRNO.trim().equals("?")) {
        lockedResult.set("EXPRNO", "")
      } else {
        lockedResult.set("EXPRNO", iPRNO)
      }
    }
    if (!iPRCL.trim().isEmpty()) {
      if (iPRCL.trim().equals("?")) {
        lockedResult.set("EXPRCL", "")
      } else {
        lockedResult.set("EXPRCL", iPRCL)
      }
    }
    if (!iDWPO.trim().isEmpty()) {
      if (iDWPO.trim().equals("?")) {
        lockedResult.set("EXDWPO", "")
      } else {
        lockedResult.set("EXDWPO", iDWPO)
      }
    }
    if (!iFMT2.trim().isEmpty()) {
      if (iFMT2.trim().equals("?")) {
        lockedResult.set("EXFMT2", "")
      } else {
        lockedResult.set("EXFMT2", iFMT2)
      }
    }
    if (!iCNQT.trim().isEmpty()) {
      if (iCNQT.trim().equals("?")) {
        lockedResult.set("EXCNQT", "")
      } else {
        lockedResult.set("EXCNQT", iCNQT)
      }
    }
    if (!iDIM1.trim().isEmpty()) {
      if (iDIM1.trim().equals("?")) {
        lockedResult.set("EXDIM1", "")
      } else {
        lockedResult.set("EXDIM1", iDIM1)
      }
    }
    if (!iDIM2.trim().isEmpty()) {
      if (iDIM2.trim().equals("?")) {
        lockedResult.set("EXDIM2", "")
      } else {
        lockedResult.set("EXDIM2", iDIM2)
      }
    }
    if (!iDIM3.trim().isEmpty()) {
      if (iDIM3.trim().equals("?")) {
        lockedResult.set("EXDIM3", "")
      } else {
        lockedResult.set("EXDIM3", iDIM3)
      }
    }
    if (!iWHLO.trim().isEmpty()) {
      if (iWHLO.trim().equals("?")) {
        lockedResult.set("EXWHLO", "")
      } else {
        lockedResult.set("EXWHLO", iWHLO)
      }
    }
    if (!iSPE1.trim().isEmpty()) {
      if (iSPE1.trim().equals("?")) {
        lockedResult.set("EXSPE1", "")
      } else {
        lockedResult.set("EXSPE1", iSPE1)
      }
    }
    if (!iSPE2.trim().isEmpty()) {
      if (iSPE2.trim().equals("?")) {
        lockedResult.set("EXSPE2", "")
      } else {
        lockedResult.set("EXSPE2", iSPE2)
      }
    }
    if (!iITCL.trim().isEmpty()) {
      if (iITCL.trim().equals("?")) {
        lockedResult.set("EXITCL", "")
      } else {
        lockedResult.set("EXITCL", iITCL)
      }
    }

    if (mi.inData.get("MSEQ") != null && !mi.inData.get("MSEQ").trim().isEmpty()) {
      lockedResult.set("EXMSEQ", iMSEQ)
    }

    if (mi.inData.get("ORQA") != null && !mi.inData.get("ORQA").trim().isEmpty()) {
      lockedResult.set("EXORQA", iORQA)
    }

    if (mi.inData.get("STDT") != null && !mi.inData.get("STDT").trim().isEmpty()) {
      lockedResult.set("EXSTDT", iSTDT)
    }

    if (mi.inData.get("FIDT") != null && !mi.inData.get("FIDT").trim().isEmpty()) {
      lockedResult.set("EXFIDT", iFIDT)
    }

    if (mi.inData.get("SQNX") != null && !mi.inData.get("SQNX").trim().isEmpty()) {
      lockedResult.set("EXSQNX", iSQNX)
    }

    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}