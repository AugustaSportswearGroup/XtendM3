/**
 * README
 * This extension is being used to Update records to EXTIHH table. 
 *
 * Name: EXT007MI.UpdInvoiceHead
 * Description: Updating records to EXTIHH table
 * Date       Changed By                      Description
 *20230210  SuriyaN@fortude.co    Updating records to EXTIHH table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdInvoiceHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iHSEQ, iIVTP, iCUNO, iCUOR, iORNO, iORTP, iBTNM, iBTA1, iBTA2, iBTTO, iBTEC, iBTPO, iBTCS, iSTNM, iSTA1, iSTA2, iSTTO, iSTEC, iSTPO, iSTCS, iSHVI, iFOBC, iWHLO, iSHWT, iRSAD, iEMAL, iBTCN, iUPHO, iURUS, iUCSR, iBTA4, iSTA4, iCHID, iCRID
  private int iCONO, iUIND, iIVNO, iTRCD, iIVDT, iTRDT, iORDT, iSHDT, iM3RG
  private double iTXSA, iNTSA, iFRAM
  private boolean validInput = true
  public UpdInvoiceHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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

    iHSEQ = (mi.inData.get("HSEQ") == null || mi.inData.get("HSEQ").trim().isEmpty()) ? "" : mi.inData.get("HSEQ")
    iIVTP = (mi.inData.get("IVTP") == null || mi.inData.get("IVTP").trim().isEmpty()) ? "" : mi.inData.get("IVTP")
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")

    iCUOR = (mi.inData.get("CUOR") == null || mi.inData.get("CUOR").trim().isEmpty()) ? "" : mi.inData.get("CUOR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iORTP = (mi.inData.get("ORTP") == null || mi.inData.get("ORTP").trim().isEmpty()) ? "0" : mi.inData.get("ORTP")
    iBTNM = (mi.inData.get("BTNM") == null || mi.inData.get("BTNM").trim().isEmpty()) ? "" : mi.inData.get("BTNM")
    iBTA1 = (mi.inData.get("BTA1") == null || mi.inData.get("BTA1").trim().isEmpty()) ? "" : mi.inData.get("BTA1")
    iBTA2 = (mi.inData.get("BTA2") == null || mi.inData.get("BTA2").trim().isEmpty()) ? "" : mi.inData.get("BTA2")
    iBTTO = (mi.inData.get("BTTO") == null || mi.inData.get("BTTO").trim().isEmpty()) ? "" : mi.inData.get("BTTO")
    iBTEC = (mi.inData.get("BTEC") == null || mi.inData.get("BTEC").trim().isEmpty()) ? "" : mi.inData.get("BTEC")
    iBTPO = (mi.inData.get("BTPO") == null || mi.inData.get("BTPO").trim().isEmpty()) ? "" : mi.inData.get("BTPO")
    iBTCS = (mi.inData.get("BTCS") == null || mi.inData.get("BTCS").trim().isEmpty()) ? "" : mi.inData.get("BTCS")
    iSTNM = (mi.inData.get("STNM") == null || mi.inData.get("STNM").trim().isEmpty()) ? "" : mi.inData.get("STNM")
    iSTA1 = (mi.inData.get("STA1") == null || mi.inData.get("STA1").trim().isEmpty()) ? "" : mi.inData.get("STA1")
    iSTA2 = (mi.inData.get("STA2") == null || mi.inData.get("STA2").trim().isEmpty()) ? "" : mi.inData.get("STA2")
    iSTTO = (mi.inData.get("STTO") == null || mi.inData.get("STTO").trim().isEmpty()) ? "" : mi.inData.get("STTO")
    iSTEC = (mi.inData.get("STEC") == null || mi.inData.get("STEC").trim().isEmpty()) ? "" : mi.inData.get("STEC")
    iSTPO = (mi.inData.get("STPO") == null || mi.inData.get("STPO").trim().isEmpty()) ? "" : mi.inData.get("STPO")
    iSTCS = (mi.inData.get("STCS") == null || mi.inData.get("STCS").trim().isEmpty()) ? "" : mi.inData.get("STCS")
    iSHVI = (mi.inData.get("SHVI") == null || mi.inData.get("SHVI").trim().isEmpty()) ? "" : mi.inData.get("SHVI")
    iFOBC = (mi.inData.get("FOBC") == null || mi.inData.get("FOBC").trim().isEmpty()) ? "" : mi.inData.get("FOBC")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iSHWT = (mi.inData.get("SHWT") == null || mi.inData.get("SHWT").trim().isEmpty()) ? "" : mi.inData.get("SHWT")
    iRSAD = (mi.inData.get("RSAD") == null || mi.inData.get("RSAD").trim().isEmpty()) ? "0" : mi.inData.get("RSAD")
    iEMAL = (mi.inData.get("EMAL") == null || mi.inData.get("EMAL").trim().isEmpty()) ? "" : mi.inData.get("EMAL")
    iBTCN = (mi.inData.get("BTCN") == null || mi.inData.get("BTCN").trim().isEmpty()) ? "" : mi.inData.get("BTCN")
    iUPHO = (mi.inData.get("UPHO") == null || mi.inData.get("UPHO").trim().isEmpty()) ? "" : mi.inData.get("UPHO")
    iURUS = (mi.inData.get("URUS") == null || mi.inData.get("URUS").trim().isEmpty()) ? "0" : mi.inData.get("URUS")
    iUCSR = (mi.inData.get("UCSR") == null || mi.inData.get("UCSR").trim().isEmpty()) ? "" : mi.inData.get("UCSR")
    iBTA4 = (mi.inData.get("BTA4") == null || mi.inData.get("BTA4").trim().isEmpty()) ? "" : mi.inData.get("BTA4")
    iSTA4 = (mi.inData.get("STA4") == null || mi.inData.get("STA4").trim().isEmpty()) ? "" : mi.inData.get("STA4")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iTXSA = (mi.inData.get("TXSA") == null || mi.inData.get("TXSA").trim().isEmpty()) ? 0 : mi.inData.get("TXSA") as Double
    iNTSA = (mi.inData.get("NTSA") == null || mi.inData.get("NTSA").trim().isEmpty()) ? 0 : mi.inData.get("NTSA") as Double
    iFRAM = (mi.inData.get("FRAM") == null || mi.inData.get("FRAM").trim().isEmpty()) ? 0 : mi.inData.get("FRAM") as Double
    iUIND = (mi.inData.get("UIND") == null || mi.inData.get("UIND").trim().isEmpty()) ? 0 : mi.inData.get("UIND") as Integer
    iIVNO = (mi.inData.get("IVNO") == null || mi.inData.get("IVNO").trim().isEmpty()) ? 0 : mi.inData.get("IVNO") as Integer
    iTRCD = (mi.inData.get("TRCD") == null || mi.inData.get("TRCD").trim().isEmpty()) ? 0 : mi.inData.get("TRCD") as Integer
    iIVDT = (mi.inData.get("IVDT") == null || mi.inData.get("IVDT").trim().isEmpty()) ? 0 : mi.inData.get("IVDT") as Integer
    iTRDT = (mi.inData.get("TRDT") == null || mi.inData.get("TRDT").trim().isEmpty()) ? 0 : mi.inData.get("TRDT") as Integer
    iORDT = (mi.inData.get("ORDT") == null || mi.inData.get("ORDT").trim().isEmpty()) ? 0 : mi.inData.get("ORDT") as Integer
    iSHDT = (mi.inData.get("SHDT") == null || mi.inData.get("SHDT").trim().isEmpty()) ? 0 : mi.inData.get("SHDT") as Integer
    iM3RG = (mi.inData.get("M3RG") == null || mi.inData.get("M3RG").trim().isEmpty()) ? 0 : mi.inData.get("M3RG") as Integer

    validateInput(iCONO, iIVNO, iORNO, iCUNO, iCUOR, iWHLO, iORTP, iIVDT, iTRDT, iORDT, iSHDT, iM3RG)
    if (validInput) {
      updateRecord()
    }

  }

  /**
   *Validate inputs
   * @params int CONO ,String IVNO,String ORNO,String CUNO,String CUOR,String WHLO, String ORTP
   * @return void
   */
  private validateInput(int CONO, int IVNO, String ORNO, String CUNO, String CUOR, String WHLO, String ORTP, int IVDT, int TRDT, int ORDT, int SHDT, int M3RG) {
    String m3CUOR = ""

    //Validate Company Number
    Map < String, String > params = ["CONO": CONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + CONO)
        validInput = false
        return
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Invoice Number
    Map < String, String > paramsIVNO = ["CONO": CONO.toString().trim(), "QERY": "UHIVNO from OINVOH where UHIVNO = '" + IVNO + "'"]
    Closure < ? > callbackIVNO = {
      Map < String,
      String > response ->
      if (response.REPL == null) {
        mi.error("Invalid Invoice Number " + IVNO)
        validInput = false
        return
      } else {

      }
    }
    miCaller.call("EXPORTMI", "Select", paramsIVNO, callbackIVNO)

    if (!ORNO.trim().isEmpty()) {
      //Validate Order Number
      Map<String, String> paramsORNO = ["ORNO": ORNO.toString().trim()]
      Closure<?> callbackORNO = {
        Map < String,
        String > response ->
        if (response.ORNO == null) {
          mi.error("Invalid Order Number " + ORNO)
          validInput = false
          return
        } else {
          m3CUOR = response.CUOR
        }
      }
      miCaller.call("OIS100MI", "GetOrderHead", paramsORNO, callbackORNO)
    }
    if (!CUOR.trim().isEmpty()) {
      //Validate Purchase Order Number
      if (!m3CUOR.trim().equals(CUOR.trim())) {
        mi.error("Invalid Purchase Order Number " + CUOR)
        validInput = false
        return
      }
    }

    if (!CUNO.trim().isEmpty()) { //Validate Customer Number
      Map < String, String > paramsCUNO = ["CONO": CONO.toString().trim(), "CUNO": CUNO.trim()]
      Closure < ? > callbackCUNO = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + response)
          validInput = false
          return
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", paramsCUNO, callbackCUNO)
    }

    if (!WHLO.trim().isEmpty()) {
      //Validate Warehouse Number
      Map < String, String > paramsWHLO = ["CONO": CONO.toString().trim(), "WHLO": WHLO.toString().trim()]
      Closure < ? > callbackWHLO = {
        Map < String,
        String > response ->
        if (response.WHLO == null) {
          mi.error("Invalid Warehouse Number " + WHLO)
          validInput = false
          return
        }
      }
      miCaller.call("MMS005MI", "GetWarehouse", paramsWHLO, callbackWHLO)
    }

    //Validate Date
    if (!iORDT == 0) {
      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", iORDT, sourceFormat.toString())
      if (!validInput) {
        mi.error("Order Date not in " + sourceFormat + " format. Please Check " + iORDT)
        validInput = false
        return
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iORDT = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", iORDT) //Maintain date in YMD8 format in the table
        }
      }
    }

    //Validate Date
    if (!IVDT == 0) {

      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", IVDT, sourceFormat.toString())
      if (!validInput) {
        mi.error("Invoice Date not in " + sourceFormat + " format. Please Check " + IVDT)
        validInput = false
        return
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iIVDT = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", IVDT) //Maintain date in YMD8 format in the table
        }
      }
    }

    //Validate Date
    if (!TRDT == 0) {

      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", TRDT, sourceFormat.toString())
      if (!validInput) {
        mi.error("Transaction Date not in " + sourceFormat + " format. Please Check " + TRDT)
        validInput = false
        return
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iTRDT = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", TRDT) //Maintain date in YMD8 format in the table
        }
      }
    }

    //Validate Date
    if (!SHDT == 0) {

      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", SHDT, sourceFormat.toString())
      if (!validInput) {
        mi.error("Ship Date not in " + sourceFormat + " format. Please Check " + SHDT)
        validInput = false
        return
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iSHDT = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", SHDT) //Maintain date in YMD8 format in the table
        }
      }
    }

    //Validate Date
    if (!M3RG == 0) {

      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", M3RG, sourceFormat.toString())
      if (!validInput) {
        mi.error("M3 Created Date not in " + sourceFormat + " format. Please Check " + M3RG)
        validInput = false
        return
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iM3RG = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", M3RG) //Maintain date in YMD8 format in the table
        }
      }
    }

  }

  /**
   *Update records to EXTIHH table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTIHH").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXIVNO", iIVNO)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    if (!iHSEQ.trim().isEmpty()) {
      if (iHSEQ.trim().equals("?")) {
        lockedResult.set("EXHSEQ", "")
      } else {
        lockedResult.set("EXHSEQ", iHSEQ)
      }
    }
    if (!iIVTP.trim().isEmpty()) {
      if (iIVTP.trim().equals("?")) {
        lockedResult.set("EXIVTP", "")
      } else {
        lockedResult.set("EXIVTP", iIVTP)
      }
    }
    if (!iCUNO.trim().isEmpty()) {
      if (iCUNO.trim().equals("?")) {
        lockedResult.set("EXCUNO", "")
      } else {
        lockedResult.set("EXCUNO", iCUNO)
      }
    }

    if (!iCUOR.trim().isEmpty()) {
      if (iCUOR.trim().equals("?")) {
        lockedResult.set("EXCUOR", "")
      } else {
        lockedResult.set("EXCUOR", iCUOR)
      }
    }
    if (!iORNO.trim().isEmpty()) {
      if (iORNO.trim().equals("?")) {
        lockedResult.set("EXORNO", "")
      } else {
        lockedResult.set("EXORNO", iORNO)
      }
    }
    if (!iORTP.trim().isEmpty()) {
      if (iORTP.trim().equals("?")) {
        lockedResult.set("EXORTP", "")
      } else {
        lockedResult.set("EXORTP", iORTP)
      }
    }
    if (!iBTNM.trim().isEmpty()) {
      if (iBTNM.trim().equals("?")) {
        lockedResult.set("EXBTNM", "")
      } else {
        lockedResult.set("EXBTNM", iBTNM)
      }
    }
    if (!iBTA1.trim().isEmpty()) {
      if (iBTA1.trim().equals("?")) {
        lockedResult.set("EXBTA1", "")
      } else {
        lockedResult.set("EXBTA1", iBTA1)
      }
    }
    if (!iBTA2.trim().isEmpty()) {
      if (iBTA2.trim().equals("?")) {
        lockedResult.set("EXBTA2", "")
      } else {
        lockedResult.set("EXBTA2", iBTA2)
      }
    }
    if (!iBTTO.trim().isEmpty()) {
      if (iBTTO.trim().equals("?")) {
        lockedResult.set("EXBTTO", "")
      } else {
        lockedResult.set("EXBTTO", iBTTO)
      }
    }
    if (!iBTEC.trim().isEmpty()) {
      if (iBTEC.trim().equals("?")) {
        lockedResult.set("EXBTEC", "")
      } else {
        lockedResult.set("EXBTEC", iBTEC)
      }
    }
    if (!iBTPO.trim().isEmpty()) {
      if (iBTPO.trim().equals("?")) {
        lockedResult.set("EXBTPO", "")
      } else {
        lockedResult.set("EXBTPO", iBTPO)
      }
    }
    if (!iBTCS.trim().isEmpty()) {
      if (iBTCS.trim().equals("?")) {
        lockedResult.set("EXBTCS", "")
      } else {
        lockedResult.set("EXBTCS", iBTCS)
      }
    }
    if (!iSTNM.trim().isEmpty()) {
      if (iSTNM.trim().equals("?")) {
        lockedResult.set("EXSTNM", "")
      } else {
        lockedResult.set("EXSTNM", iSTNM)
      }
    }
    if (!iSTA1.trim().isEmpty()) {
      if (iSTA1.trim().equals("?")) {
        lockedResult.set("EXSTA1", "")
      } else {
        lockedResult.set("EXSTA1", iSTA1)
      }
    }
    if (!iSTA2.trim().isEmpty()) {
      if (iSTA2.trim().equals("?")) {
        lockedResult.set("EXSTA2", "")
      } else {
        lockedResult.set("EXSTA2", iSTA2)
      }
    }
    if (!iSTTO.trim().isEmpty()) {
      if (iSTTO.trim().equals("?")) {
        lockedResult.set("EXSTTO", "")
      } else {
        lockedResult.set("EXSTTO", iSTTO)
      }
    }
    if (!iSTEC.trim().isEmpty()) {
      if (iSTEC.trim().equals("?")) {
        lockedResult.set("EXSTEC", "")
      } else {
        lockedResult.set("EXSTEC", iSTEC)
      }
    }
    if (!iSTPO.trim().isEmpty()) {
      if (iSTPO.trim().equals("?")) {
        lockedResult.set("EXSTPO", "")
      } else {
        lockedResult.set("EXSTPO", iSTPO)
      }
    }
    if (!iSTCS.trim().isEmpty()) {
      if (iSTCS.trim().equals("?")) {
        lockedResult.set("EXSTCS", "")
      } else {
        lockedResult.set("EXSTCS", iSTCS)
      }
    }
    if (!iSHVI.trim().isEmpty()) {
      if (iSHVI.trim().equals("?")) {
        lockedResult.set("EXSHVI", "")
      } else {
        lockedResult.set("EXSHVI", iSHVI)
      }
    }
    if (!iFOBC.trim().isEmpty()) {
      if (iFOBC.trim().equals("?")) {
        lockedResult.set("EXFOBC", "")
      } else {
        lockedResult.set("EXFOBC", iFOBC)
      }
    }
    if (!iWHLO.trim().isEmpty()) {
      if (iWHLO.trim().equals("?")) {
        lockedResult.set("EXWHLO", "")
      } else {
        lockedResult.set("EXWHLO", iWHLO)
      }
    }
    if (!iSHWT.trim().isEmpty()) {
      if (iSHWT.trim().equals("?")) {
        lockedResult.set("EXSHWT", "")
      } else {
        lockedResult.set("EXSHWT", iSHWT)
      }
    }
    if (!iRSAD.trim().isEmpty()) {
      if (iRSAD.trim().equals("?")) {
        lockedResult.set("EXRSAD", "")
      } else {
        lockedResult.set("EXRSAD", iRSAD)
      }
    }
    if (!iEMAL.trim().isEmpty()) {
      if (iEMAL.trim().equals("?")) {
        lockedResult.set("EXEMAL", "")
      } else {
        lockedResult.set("EXEMAL", iEMAL)
      }
    }
    if (!iBTCN.trim().isEmpty()) {
      if (iBTCN.trim().equals("?")) {
        lockedResult.set("EXBTCN", "")
      } else {
        lockedResult.set("EXBTCN", iBTCN)
      }
    }
    if (!iUPHO.trim().isEmpty()) {
      if (iUPHO.trim().equals("?")) {
        lockedResult.set("EXUPHO", "")
      } else {
        lockedResult.set("EXUPHO", iUPHO)
      }
    }
    if (!iURUS.trim().isEmpty()) {
      if (iURUS.trim().equals("?")) {
        lockedResult.set("EXURUS", "")
      } else {
        lockedResult.set("EXURUS", iURUS)
      }
    }
    if (!iUCSR.trim().isEmpty()) {
      if (iUCSR.trim().equals("?")) {
        lockedResult.set("EXUCSR", "")
      } else {
        lockedResult.set("EXUCSR", iUCSR)
      }
    }
    if (!iBTA4.trim().isEmpty()) {
      if (iBTA4.trim().equals("?")) {
        lockedResult.set("EXBTA4", "")
      } else {
        lockedResult.set("EXBTA4", iBTA4)
      }
    }
    if (!iSTA4.trim().isEmpty()) {
      if (iSTA4.trim().equals("?")) {
        lockedResult.set("EXSTA4", "")
      } else {
        lockedResult.set("EXSTA4", iSTA4)
      }
    }

    if (mi.inData.get("TRCD") != null && !mi.inData.get("TRCD").trim().isEmpty()) {
      lockedResult.set("EXTRCD", iTRCD)
    }
    if (mi.inData.get("M3RG") != null && !mi.inData.get("M3RG").trim().isEmpty()) {
      lockedResult.set("EXM3RG", iM3RG)
    }

    if (mi.inData.get("IVDT") != null && !mi.inData.get("IVDT").trim().isEmpty()) {
      lockedResult.set("EXIVDT", iIVDT)
    }
    if (mi.inData.get("TRDT") != null && !mi.inData.get("TRDT").trim().isEmpty()) {
      lockedResult.set("EXTRDT", iTRDT)
    }
    if (mi.inData.get("ORDT") != null && !mi.inData.get("ORDT").trim().isEmpty()) {
      lockedResult.set("EXORDT", iORDT)
    }
    if (mi.inData.get("SHDT") != null && !mi.inData.get("SHDT").trim().isEmpty()) {
      lockedResult.set("EXSHDT", iSHDT)
    }
    if (mi.inData.get("TXSA") != null && !mi.inData.get("TXSA").trim().isEmpty()) {
      lockedResult.set("EXTXSA", iTXSA)
    }
    if (mi.inData.get("NTSA") != null && !mi.inData.get("NTSA").trim().isEmpty()) {
      lockedResult.set("EXNTSA", iNTSA)
    }
    if (mi.inData.get("FRAM") != null && !mi.inData.get("FRAM").trim().isEmpty()) {
      lockedResult.set("EXFRAM", iFRAM)
    }
    if (mi.inData.get("UIND") != null && !mi.inData.get("UIND").trim().isEmpty()) {
      lockedResult.set("EXUIND", iUIND)
    }

    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}