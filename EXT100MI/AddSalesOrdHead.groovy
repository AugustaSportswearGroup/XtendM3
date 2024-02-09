/**
 * README
 * This extension is being used to Add records to EXTSOH table. 
 *
 * Name: EXT100MI.AddSalesOrdHead
 * Description: Adding records to EXTSOH table
 * Date	      Changed By                      Description
 *20230210  SuriyaN@fortude.co     Adding records to EXTSOH
 *20240208  AbhishekA@fortude.co   Updating Validation logic
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddSalesOrdHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iORTP, iSTAT, iCUNO, iBTNM, iBTA1, iBTA2, iBTTO, iBTEC, iBTPO, iBTCS, iSTNM, iSTA1, iSTA2, iSTTO, iSTEC, iSTPO, iSTCS, iSHVI, iSHWT, iCUOR, iFOBC, iWHLO, iTEDL, iFAXT, iEMAL, iRSAD, iUFED, iUUPS, iUCUS, iUPHO, iUCSR, iURUS, iUPM3, iNM3S, iORNO, iM3CH
  private int iCONO, iUINH, iUWOA, iUKLR, iORDT, iSHED, iTXID
  private double iTXAM, iNTAM, iSTAM, iFRAM
  private boolean validInput = true
  public AddSalesOrdHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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
    iORTP = (mi.inData.get("ORTP") == null || mi.inData.get("ORTP").trim().isEmpty()) ? " " : mi.inData.get("ORTP")
    iSTAT = (mi.inData.get("STAT") == null || mi.inData.get("STAT").trim().isEmpty()) ? " " : mi.inData.get("STAT")
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? " " : mi.inData.get("CUNO")
    iBTNM = (mi.inData.get("BTNM") == null || mi.inData.get("BTNM").trim().isEmpty()) ? " " : mi.inData.get("BTNM")
    iBTA1 = (mi.inData.get("BTA1") == null || mi.inData.get("BTA1").trim().isEmpty()) ? " " : mi.inData.get("BTA1")
    iBTA2 = (mi.inData.get("BTA2") == null || mi.inData.get("BTA2").trim().isEmpty()) ? " " : mi.inData.get("BTA2")
    iBTTO = (mi.inData.get("BTTO") == null || mi.inData.get("BTTO").trim().isEmpty()) ? " " : mi.inData.get("BTTO")
    iBTEC = (mi.inData.get("BTEC") == null || mi.inData.get("BTEC").trim().isEmpty()) ? " " : mi.inData.get("BTEC")
    iBTPO = (mi.inData.get("BTPO") == null || mi.inData.get("BTPO").trim().isEmpty()) ? " " : mi.inData.get("BTPO")
    iBTCS = (mi.inData.get("BTCS") == null || mi.inData.get("BTCS").trim().isEmpty()) ? " " : mi.inData.get("BTCS")
    iSTNM = (mi.inData.get("STNM") == null || mi.inData.get("STNM").trim().isEmpty()) ? " " : mi.inData.get("STNM")
    iSTA1 = (mi.inData.get("STA1") == null || mi.inData.get("STA1").trim().isEmpty()) ? " " : mi.inData.get("STA1")
    iSTA2 = (mi.inData.get("STA2") == null || mi.inData.get("STA2").trim().isEmpty()) ? " " : mi.inData.get("STA2")
    iSTTO = (mi.inData.get("STTO") == null || mi.inData.get("STTO").trim().isEmpty()) ? " " : mi.inData.get("STTO")
    iSTEC = (mi.inData.get("STEC") == null || mi.inData.get("STEC").trim().isEmpty()) ? " " : mi.inData.get("STEC")
    iSTPO = (mi.inData.get("STPO") == null || mi.inData.get("STPO").trim().isEmpty()) ? " " : mi.inData.get("STPO")
    iSTCS = (mi.inData.get("STCS") == null || mi.inData.get("STCS").trim().isEmpty()) ? " " : mi.inData.get("STCS")
    iSHVI = (mi.inData.get("SHVI") == null || mi.inData.get("SHVI").trim().isEmpty()) ? " " : mi.inData.get("SHVI")
    iSHWT = (mi.inData.get("SHWT") == null || mi.inData.get("SHWT").trim().isEmpty()) ? " " : mi.inData.get("SHWT")
    iCUOR = (mi.inData.get("CUOR") == null || mi.inData.get("CUOR").trim().isEmpty()) ? " " : mi.inData.get("CUOR")
    iFOBC = (mi.inData.get("FOBC") == null || mi.inData.get("FOBC").trim().isEmpty()) ? " " : mi.inData.get("FOBC")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? " " : mi.inData.get("WHLO")
    iTEDL = (mi.inData.get("TEDL") == null || mi.inData.get("TEDL").trim().isEmpty()) ? " " : mi.inData.get("TEDL")
    iFAXT = (mi.inData.get("FAXT") == null || mi.inData.get("FAXT").trim().isEmpty()) ? " " : mi.inData.get("FAXT")
    iEMAL = (mi.inData.get("EMAL") == null || mi.inData.get("EMAL").trim().isEmpty()) ? " " : mi.inData.get("EMAL")
    iRSAD = (mi.inData.get("RSAD") == null || mi.inData.get("RSAD").trim().isEmpty()) ? " " : mi.inData.get("RSAD")
    iUFED = (mi.inData.get("UFED") == null || mi.inData.get("UFED").trim().isEmpty()) ? " " : mi.inData.get("UFED")
    iUUPS = (mi.inData.get("UUPS") == null || mi.inData.get("UUPS").trim().isEmpty()) ? " " : mi.inData.get("UUPS")
    iUCUS = (mi.inData.get("UCUS") == null || mi.inData.get("UCUS").trim().isEmpty()) ? " " : mi.inData.get("UCUS")
    iUPHO = (mi.inData.get("UPHO") == null || mi.inData.get("UPHO").trim().isEmpty()) ? " " : mi.inData.get("UPHO")
    iUCSR = (mi.inData.get("UCSR") == null || mi.inData.get("UCSR").trim().isEmpty()) ? " " : mi.inData.get("UCSR")
    iURUS = (mi.inData.get("URUS") == null || mi.inData.get("URUS").trim().isEmpty()) ? " " : mi.inData.get("URUS")
    iUPM3 = (mi.inData.get("UPM3") == null || mi.inData.get("UPM3").trim().isEmpty()) ? " " : mi.inData.get("UPM3")
    iNM3S = (mi.inData.get("NM3S") == null || mi.inData.get("NM3S").trim().isEmpty()) ? " " : mi.inData.get("NM3S")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? " " : mi.inData.get("ORNO")
    iM3CH = (mi.inData.get("M3CH") == null || mi.inData.get("M3CH").trim().isEmpty()) ? " " : mi.inData.get("M3CH")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iTXAM = (mi.inData.get("TXAM") == null || mi.inData.get("TXAM").trim().isEmpty()) ? 0 : mi.inData.get("TXAM") as Double
    iNTAM = (mi.inData.get("NTAM") == null || mi.inData.get("NTAM").trim().isEmpty()) ? 0 : mi.inData.get("NTAM") as Double
    iSTAM = (mi.inData.get("STAM") == null || mi.inData.get("STAM").trim().isEmpty()) ? 0 : mi.inData.get("STAM") as Double
    iFRAM = (mi.inData.get("FRAM") == null || mi.inData.get("FRAM").trim().isEmpty()) ? 0 : mi.inData.get("FRAM") as Double
    iORDT = (mi.inData.get("ORDT") == null || mi.inData.get("ORDT").trim().isEmpty()) ? 0 : mi.inData.get("ORDT") as Integer
    iSHED = (mi.inData.get("SHED") == null || mi.inData.get("SHED").trim().isEmpty()) ? 0 : mi.inData.get("SHED") as Integer
    iTXID = (mi.inData.get("TXID") == null || mi.inData.get("TXID").trim().isEmpty()) ? 0 : mi.inData.get("TXID") as Integer
    iUINH = (mi.inData.get("UINH") == null || mi.inData.get("UINH").trim().isEmpty()) ? 0 : mi.inData.get("UINH") as Integer
    iUWOA = (mi.inData.get("UWOA") == null || mi.inData.get("UWOA").trim().isEmpty()) ? 0 : mi.inData.get("UWOA") as Integer
    iUKLR = (mi.inData.get("UKLR") == null || mi.inData.get("UKLR").trim().isEmpty()) ? 0 : mi.inData.get("UKLR") as Integer

    validateInput(iCONO, iORNO, iCUNO, iCUOR, iWHLO, iORDT, iSHED)
    if (validInput) {
      insertRecord()
    }
  }

  /**
   *Validate inputs
   * @params int CONO ,String ORNO,String CUNO,String CUOR,String WHLO, String ORDT, String SHED
   * @return boolean
   */
  private void validateInput(int CONO, String ORNO, String CUNO, String CUOR, String WHLO, int ORDT, int SHED) {
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

    //Validate Order Number
    params = ["ORNO": ORNO.toString().trim()]
    callback = {
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
    miCaller.call("OIS100MI", "GetOrderHead", params, callback)

    //Validate Purchase Order Number
    if (!m3CUOR.trim().equals(CUOR.trim()) && !iCUOR.trim().isEmpty()) {
      mi.error("Invalid Purchase Order Number " + CUOR)
      validInput = false
      return 
    }

    if (!CUNO.trim().isEmpty()) {
      //Validate Customer Number
      params = ["CONO": CONO.toString().trim(), "CUNO": CUNO.trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + response)
          validInput = false
          return 
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }

    if (!WHLO.trim().isEmpty()) {
      //Validate Warehouse Number
      params = ["CONO": CONO.toString().trim(), "WHLO": WHLO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.WHLO == null) {
          mi.error("Invalid Warehouse Number " + WHLO)
          validInput = false
          return 
        }
      }
      miCaller.call("MMS005MI", "GetWarehouse", params, callback)
    }

    if (ORDT != 0) {
      //Validate Date
      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", ORDT.toString().trim(), sourceFormat.toString())
      if (!validInput) {
        mi.error("Order Date not in " + sourceFormat + " format. Please Check " + ORDT)
        return 
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iORDT = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", ORDT.toString().trim()) //Maintain date in YMD8 format in the table
        }
      }
    }

    if (SHED != 0) {
      //Validate Ship Expire Date
      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", SHED.toString().trim(), sourceFormat.toString())
      if (!validInput) {
        mi.error("Order Date not in " + sourceFormat + " format. Please Check " + SHED)
        return 
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iSHED = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", SHED.toString().trim()) //Maintain date in YMD8 format in the table
        }
      }
    }
  }
  /**
   *Insert records to EXTSOH table
   * @params 
   * @return 
   */
  public void insertRecord() {
    DBAction action = database.table("EXTSOH").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXORTP", iORTP)
    query.set("EXSTAT", iSTAT)
    query.set("EXCUNO", iCUNO)
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
    query.set("EXSHWT", iSHWT)
    query.set("EXCUOR", iCUOR)
    query.set("EXFOBC", iFOBC)
    query.set("EXWHLO", iWHLO)
    query.set("EXTEDL", iTEDL)
    query.set("EXFAXT", iFAXT)
    query.set("EXEMAL", iEMAL)
    query.set("EXRSAD", iRSAD.toString())
    query.set("EXUFED", iUFED)
    query.set("EXUUPS", iUUPS)
    query.set("EXUCUS", iUCUS)
    query.set("EXUPHO", iUPHO)
    query.set("EXUCSR", iUCSR)
    query.set("EXURUS", iURUS)
    query.set("EXUPM3", iUPM3)
    query.set("EXORNO", iORNO)
    query.set("EXM3CH", iM3CH)
    query.set("EXTXID", iTXID as Integer)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXORDT", iORDT as Integer)
    query.set("EXSHED", iSHED as Integer)
    query.set("EXTXAM", iTXAM as Double)
    query.set("EXNTAM", iNTAM as Double)
    query.set("EXSTAM", iSTAM as Double)
    query.set("EXFRAM", iFRAM as Double)
    query.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXCHID", program.getUser())
    query.set("EXDLFG", "0")
    query.set("EXCHNO", 0)
    action.insert(query, recordExists)
  }

  Closure recordExists = {
    mi.error("Record Already Exists")
    return
  }
}