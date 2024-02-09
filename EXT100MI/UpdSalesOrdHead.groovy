/**
 * README
 * This extension is being used to Update records to EXTSOH table. 
 *
 * Name: EXT100MI.UpdSalesOrdHead
 * Description: Updating records to EXTSOH table
 * Date       Changed By                      Description
 *20230210  SuriyaN@fortude.co    Updating records to EXTSOH table
 *20240208  AbhishekA@fortude.co  Updating Validation logic
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdSalesOrdHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iORTP, iSTAT, iCUNO, iBTNM, iBTA1, iBTA2, iBTTO, iBTEC, iBTPO, iBTCS, iSTNM, iSTA1, iSTA2, iSTTO, iSTEC, iSTPO, iSTCS, iSHVI, iSHWT, iCUOR, iFOBC, iWHLO, iTEDL, iFAXT, iEMAL, iRSAD, iUFED, iUUPS, iUCUS, iUPHO, iUCSR, iURUS, iUPM3, iORNO, iDLFG, iM3CH
  private int iCONO, iORDT, iSHED, iTXID
  private double iTXAM, iNTAM, iSTAM, iFRAM
  private boolean validInput = true
  public UpdSalesOrdHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iORTP = (mi.inData.get("ORTP") == null || mi.inData.get("ORTP").trim().isEmpty()) ? "" : mi.inData.get("ORTP")
    iSTAT = (mi.inData.get("STAT") == null || mi.inData.get("STAT").trim().isEmpty()) ? "" : mi.inData.get("STAT")
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
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
    iSHWT = (mi.inData.get("SHWT") == null || mi.inData.get("SHWT").trim().isEmpty()) ? "" : mi.inData.get("SHWT")
    iCUOR = (mi.inData.get("CUOR") == null || mi.inData.get("CUOR").trim().isEmpty()) ? "" : mi.inData.get("CUOR")
    iFOBC = (mi.inData.get("FOBC") == null || mi.inData.get("FOBC").trim().isEmpty()) ? "" : mi.inData.get("FOBC")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iTEDL = (mi.inData.get("TEDL") == null || mi.inData.get("TEDL").trim().isEmpty()) ? "" : mi.inData.get("TEDL")
    iFAXT = (mi.inData.get("FAXT") == null || mi.inData.get("FAXT").trim().isEmpty()) ? "" : mi.inData.get("FAXT")
    iEMAL = (mi.inData.get("EMAL") == null || mi.inData.get("EMAL").trim().isEmpty()) ? "" : mi.inData.get("EMAL")
    iRSAD = (mi.inData.get("RSAD") == null || mi.inData.get("RSAD").trim().isEmpty()) ? "" : mi.inData.get("RSAD")
    iUFED = (mi.inData.get("UFED") == null || mi.inData.get("UFED").trim().isEmpty()) ? "" : mi.inData.get("UFED")
    iUUPS = (mi.inData.get("UUPS") == null || mi.inData.get("UUPS").trim().isEmpty()) ? "" : mi.inData.get("UUPS")
    iUCUS = (mi.inData.get("UCUS") == null || mi.inData.get("UCUS").trim().isEmpty()) ? "" : mi.inData.get("UCUS")
    iUPHO = (mi.inData.get("UPHO") == null || mi.inData.get("UPHO").trim().isEmpty()) ? "" : mi.inData.get("UPHO")
    iUCSR = (mi.inData.get("UCSR") == null || mi.inData.get("UCSR").trim().isEmpty()) ? "" : mi.inData.get("UCSR")
    iURUS = (mi.inData.get("URUS") == null || mi.inData.get("URUS").trim().isEmpty()) ? "" : mi.inData.get("URUS")
    iUPM3 = (mi.inData.get("UPM3") == null || mi.inData.get("UPM3").trim().isEmpty()) ? "" : mi.inData.get("UPM3")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iDLFG = (mi.inData.get("DLFG") == null || mi.inData.get("DLFG").trim().isEmpty()) ? "" : mi.inData.get("DLFG")
    iM3CH = (mi.inData.get("M3CH") == null || mi.inData.get("M3CH").trim().isEmpty()) ? "" : mi.inData.get("M3CH")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iORDT = (mi.inData.get("ORDT") == null || mi.inData.get("ORDT").trim().isEmpty()) ? iORDT : mi.inData.get("ORDT") as Integer
    iTXID = (mi.inData.get("TXID") == null || mi.inData.get("TXID").trim().isEmpty()) ? iTXID : mi.inData.get("TXID") as Integer
    iSHED = (mi.inData.get("SHED") == null || mi.inData.get("SHED").trim().isEmpty()) ? iSHED : mi.inData.get("SHED") as Integer
    iTXAM = (mi.inData.get("TXAM") == null || mi.inData.get("TXAM").trim().isEmpty()) ? iTXAM : mi.inData.get("TXAM") as Double
    iNTAM = (mi.inData.get("NTAM") == null || mi.inData.get("NTAM").trim().isEmpty()) ? iNTAM : mi.inData.get("NTAM") as Double
    iSTAM = (mi.inData.get("STAM") == null || mi.inData.get("STAM").trim().isEmpty()) ? iSTAM : mi.inData.get("STAM") as Double
    iFRAM = (mi.inData.get("FRAM") == null || mi.inData.get("FRAM").trim().isEmpty()) ? iFRAM : mi.inData.get("FRAM") as Double
    validateInput(iCONO, iORNO, iCUNO, iCUOR, iWHLO, iORTP)
    if (validInput) {
      updateRecord()
    }
  }

  /**
   *Validate inputs
   * @params int CONO ,String ORNO,String CUNO,String CUOR,String WHLO
   * @return 
   */
  private void validateInput(int CONO, String ORNO, String CUNO, String CUOR, String WHLO, String ORTP) {
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

    if (!CUOR.trim().isEmpty()) {
      //Validate Purchase Order Number
      if (!m3CUOR.trim().equals(iCUOR.trim())) {
        mi.error("Invalid Purchase Order Number " + iCUOR)
        validInput = false
        return 
      }
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

  }

  /**
   *Update records to EXTSOH table
   * @params 
   * @return 
   */
  public void updateRecord() {
    DBAction query = database.table("EXTSOH").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXORNO", iORNO)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }

  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
    if (!iORTP.trim().isEmpty()) {
      if (iORTP.trim().equals("?")) {
        lockedResult.set("EXORTP", "")
      } else {
        lockedResult.set("EXORTP", iORTP)
      }
    }
    if (!iSTAT.trim().isEmpty()) {
      if (iSTAT.trim().equals("?")) {
        lockedResult.set("EXSTAT", "")
      } else {
        lockedResult.set("EXSTAT", iSTAT)
      }
    }
    if (!iCUNO.trim().isEmpty()) {
      if (iCUNO.trim().equals("?")) {
        lockedResult.set("EXCUNO", "")
      } else {
        lockedResult.set("EXCUNO", iCUNO)
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
    if (!iSHWT.trim().isEmpty()) {
      if (iSHWT.trim().equals("?")) {
        lockedResult.set("EXSHWT", "")
      } else {
        lockedResult.set("EXSHWT", iSHWT)
      }
    }
    if (!iCUOR.trim().isEmpty()) {
      if (iCUOR.trim().equals("?")) {
        lockedResult.set("EXCUOR", "")
      } else {
        lockedResult.set("EXCUOR", iCUOR)
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

    if (!iTEDL.trim().isEmpty()) {
      if (iTEDL.trim().equals("?")) {
        lockedResult.set("EXTEDL", "")
      } else {
        lockedResult.set("EXTEDL", iTEDL)
      }
    }
    if (!iFAXT.trim().isEmpty()) {
      if (iFAXT.trim().equals("?")) {
        lockedResult.set("EXFAXT", "")
      } else {
        lockedResult.set("EXFAXT", iFAXT)
      }
    }
    if (!iEMAL.trim().isEmpty()) {
      if (iEMAL.trim().equals("?")) {
        lockedResult.set("EXEMAL", "")
      } else {
        lockedResult.set("EXEMAL", iEMAL)
      }
    }
    if (!iRSAD.trim().isEmpty()) {
      if (iRSAD.trim().equals("?")) {
        lockedResult.set("EXRSAD", "")
      } else {
        lockedResult.set("EXRSAD", iRSAD)
      }
    }
    if (!iUFED.trim().isEmpty()) {
      if (iUFED.trim().equals("?")) {
        lockedResult.set("EXUFED", "")
      } else {
        lockedResult.set("EXUFED", iUFED)
      }
    }
    if (!iUUPS.trim().isEmpty()) {
      if (iUUPS.trim().equals("?")) {
        lockedResult.set("EXUUPS", "")
      } else {
        lockedResult.set("EXUUPS", iUUPS)
      }
    }
    if (!iUCUS.trim().isEmpty()) {
      if (iUCUS.trim().equals("?")) {
        lockedResult.set("EXUCUS", "")
      } else {
        lockedResult.set("EXUCUS", iUCUS)
      }
    }
    if (!iUPHO.trim().isEmpty()) {
      if (iUPHO.trim().equals("?")) {
        lockedResult.set("EXUPHO", "")
      } else {
        lockedResult.set("EXUPHO", iUPHO)
      }
    }
    if (!iUCSR.trim().isEmpty()) {
      if (iUCSR.trim().equals("?")) {
        lockedResult.set("EXUCSR", "")
      } else {
        lockedResult.set("EXUCSR", iUCSR)
      }
    }
    if (!iURUS.trim().isEmpty()) {
      if (iURUS.trim().equals("?")) {
        lockedResult.set("EXURUS", "")
      } else {
        lockedResult.set("EXURUS", iURUS)
      }
    }
    if (!iUPM3.trim().isEmpty()) {
      if (iUPM3.trim().equals("?")) {
        lockedResult.set("EXUPM3", "")
      } else {
        lockedResult.set("EXUPM3", iUPM3)
      }
    }
    if (!iDLFG.trim().isEmpty()) {
      if (iDLFG.trim().equals("?")) {
        lockedResult.set("EXDLFG", "")
      } else {
        lockedResult.set("EXDLFG", iDLFG)
      }
    }
    if (!iM3CH.trim().isEmpty()) {
      if (iDLFG.trim().equals("?")) {
        lockedResult.set("EXM3CH", "")
      } else {
        lockedResult.set("EXM3CH", iM3CH)
      }
    }

    if (mi.inData.get("ORDT") != null && !mi.inData.get("ORDT").trim().isEmpty()) {
      lockedResult.set("EXORDT", iORDT)
    }

    if (mi.inData.get("TXID") != null && !mi.inData.get("TXID").trim().isEmpty()) {
      lockedResult.set("EXTXID", iTXID)
    }

    if (mi.inData.get("SHED") != null && !mi.inData.get("SHED").trim().isEmpty()) {
      lockedResult.set("EXSHED", iSHED)
    }
    if (mi.inData.get("TXAM") != null && !mi.inData.get("TXAM").trim().isEmpty()) {
      lockedResult.set("EXTXAM", iTXAM)
    }
    if (mi.inData.get("NTAM") != null && !mi.inData.get("NTAM").trim().isEmpty()) {
      lockedResult.set("EXNTAM", iNTAM)
    }
    if (mi.inData.get("STAM") != null && !mi.inData.get("STAM").trim().isEmpty()) {
      lockedResult.set("EXSTAM", iSTAM)
    }
    if (mi.inData.get("FRAM") != null && !mi.inData.get("FRAM").trim().isEmpty()) {
      lockedResult.set("EXFRAM", iFRAM)
    }
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.set("EXCHNO", CHNO+1)
    lockedResult.update()
  }
}