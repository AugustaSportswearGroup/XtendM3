/**
 * README
 * This extension is being used to Add records to EXTIHH table. 
 *
 * Name: EXT007MI.AddInvoiceHead
 * Description: Adding records to EXTIHH table
 * Date	      Changed By                      Description
 *20230210  SuriyaN@fortude.co     Adding records to EXTIHH table*/

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddInvoiceHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iHSEQ, iIVTP, iCUNO, iCUOR, iORNO, iORTP, iBTNM, iBTA1, iBTA2, iBTTO, iBTEC, iBTPO, iBTCS, iSTNM, iSTA1, iSTA2, iSTTO, iSTEC, iSTPO, iSTCS, iSHVI, iFOBC, iWHLO, iSHWT, iRSAD, iEMAL, iBTCN, iUPHO, iURUS, iUCSR, iBTA4, iSTA4, iCHID, iCRID
  private int iCONO, iUIND, iIVNO, iTRCD, iIVDT, iTRDT, iORDT, iSHDT, iM3RG
  private double iTXSA, iNTSA, iFRAM
  private boolean validInput = true
  public AddInvoiceHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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
      insertRecord()
    }

  }

  /**
   *Validate inputs
   * @params int CONO ,String IVNO,String ORNO,String CUNO,String CUOR,String WHLO, String ORTP, String IVDT, String TRDT, String ORDT, String SHDT
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
    if (IVNO == 0) {
      mi.error("Invoice Number must be entered")
      validInput = false
      return
    }

    if (!ORNO.trim().isEmpty()) {
      //Validate Order Number
      Map < String, String > paramsORNO = ["ORNO": ORNO.toString().trim()]
      Closure < ? > callbackORNO = {
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

    //Validate Customer Number
    if (!CUNO.trim().isEmpty()) {
      Map < String, String > paramsCUNO = ["CONO": CONO.toString().trim(), "CUNO": CUNO.trim()]
      Closure < ? > callbackCUNO = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + CUNO)
          validInput = false
          return
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", paramsCUNO, callbackCUNO)
    }

    //Validate Warehouse Number
    if (!WHLO.trim().isEmpty()) {

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
   *Insert records to EXTIHH table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTIHH").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXIVNO", iIVNO)
    query.set("EXHSEQ", iHSEQ)
    query.set("EXIVTP", iIVTP)
    query.set("EXCUNO", iCUNO)
    query.set("EXTRCD", iTRCD)
    query.set("EXCUOR", iCUOR)
    query.set("EXORNO", iORNO)
    query.set("EXORTP", iORTP)
    query.set("EXBTNM", iBTNM)
    query.set("EXBTA1", iBTA1)
    query.set("EXBTA2", iBTA2)
    query.set("EXBTTO", iBTTO)
    query.set("EXBTEC", iBTEC)
    query.set("EXBTPO", iBTPO)
    query.set("EXBTCS", iBTCS)
    query.set("EXSTNM", iSTNM)
    query.set("EXSTA1", iSTA1)
    query.set("EXSTA2", iSTA2)
    query.set("EXSTTO", iSTTO)
    query.set("EXSTEC", iSTEC)
    query.set("EXSTPO", iSTPO)
    query.set("EXSTCS", iSTCS)
    query.set("EXSHVI", iSHVI)
    query.set("EXFOBC", iFOBC)
    query.set("EXWHLO", iWHLO)
    query.set("EXSHWT", iSHWT)
    query.set("EXRSAD", iRSAD)
    query.set("EXEMAL", iEMAL)
    query.set("EXBTCN", iBTCN)
    query.set("EXUPHO", iUPHO)
    query.set("EXURUS", iURUS)
    query.set("EXUCSR", iUCSR)
    query.set("EXBTA4", iBTA4)
    query.set("EXSTA4", iSTA4)
    query.set("EXCHID", iCHID)
    query.set("EXCRID", iCRID)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXIVDT", iIVDT as Integer)
    query.set("EXTRDT", iTRDT as Integer)
    query.set("EXORDT", iORDT as Integer)
    query.set("EXSHDT", iSHDT as Integer)
    query.set("EXTXSA", iTXSA as Double)
    query.set("EXNTSA", iNTSA as Double)
    query.set("EXFRAM", iFRAM as Double)
    query.set("EXUIND", iUIND as Integer)
    query.set("EXUIND", iM3RG as Integer)
    query.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
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