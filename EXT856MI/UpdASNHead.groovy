/**
 * README
 * This extension is being used to Update records to EXTNCT table. 
 *
 * Name: EXT856MI.UpdASNHead
 * Description: Updating records to EXTNCT table
 * Date	      Changed By                      Description
 *20230829  SuriyaN@fortude.co    Updating records to EXTNCT table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdASNHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iPYCU, iSPOC, iASNP, iDEMH, iDFLB, iSFLB, iASNF, iSNRP, iSNFO, iSTAT, iLFCR, iLFSH, iDCLQ, iCPKI, iPPKI, iSPKI, iEANI, iIDSD, iUD01, iUD02, iUD04, iUD05, iUD06, iUD07, iUD08, iUD09, iUD10, iUD11, iUD12, iUD13, iUD14, iUD15, iUD16, iUD17, iUD18
  private int iCONO, iCPID, iSNSN, iSCSN, iPLSN, iUD03
  private boolean validInput = true

  public UpdASNHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iPYCU = (mi.inData.get("PYCU") == null || mi.inData.get("PYCU").trim().isEmpty()) ? "" : mi.inData.get("PYCU")
    iSPOC = (mi.inData.get("SPOC") == null || mi.inData.get("SPOC").trim().isEmpty()) ? "0" : mi.inData.get("SPOC")
    iASNP = (mi.inData.get("ASNP") == null || mi.inData.get("ASNP").trim().isEmpty()) ? "" : mi.inData.get("ASNP")
    iDEMH = (mi.inData.get("DEMH") == null || mi.inData.get("DEMH").trim().isEmpty()) ? "" : mi.inData.get("DEMH")
    iDFLB = (mi.inData.get("DFLB") == null || mi.inData.get("DFLB").trim().isEmpty()) ? "" : mi.inData.get("DFLB")
    iSFLB = (mi.inData.get("SFLB") == null || mi.inData.get("SFLB").trim().isEmpty()) ? "" : mi.inData.get("SFLB")
    iASNF = (mi.inData.get("ASNF") == null || mi.inData.get("ASNF").trim().isEmpty()) ? "" : mi.inData.get("ASNF")
    iSNRP = (mi.inData.get("SNRP") == null || mi.inData.get("SNRP").trim().isEmpty()) ? "" : mi.inData.get("SNRP")
    iSNFO = (mi.inData.get("SNFO") == null || mi.inData.get("SNFO").trim().isEmpty()) ? "" : mi.inData.get("SNFO")
    iSTAT = (mi.inData.get("STAT") == null || mi.inData.get("STAT").trim().isEmpty()) ? "" : mi.inData.get("STAT")
    iLFCR = (mi.inData.get("LFCR") == null || mi.inData.get("LFCR").trim().isEmpty()) ? "" : mi.inData.get("LFCR")
    iLFSH = (mi.inData.get("LFSH") == null || mi.inData.get("LFSH").trim().isEmpty()) ? "" : mi.inData.get("LFSH")
    iDCLQ = (mi.inData.get("DCLQ") == null || mi.inData.get("DCLQ").trim().isEmpty()) ? "" : mi.inData.get("DCLQ")
    iCPKI = (mi.inData.get("CPKI") == null || mi.inData.get("CPKI").trim().isEmpty()) ? "0" : mi.inData.get("CPKI")
    iPPKI = (mi.inData.get("PPKI") == null || mi.inData.get("PPKI").trim().isEmpty()) ? "0" : mi.inData.get("PPKI")
    iSPKI = (mi.inData.get("SPKI") == null || mi.inData.get("SPKI").trim().isEmpty()) ? "0" : mi.inData.get("SPKI")
    iEANI = (mi.inData.get("EANI") == null || mi.inData.get("EANI").trim().isEmpty()) ? "" : mi.inData.get("EANI")
    iIDSD = (mi.inData.get("IDSD") == null || mi.inData.get("IDSD").trim().isEmpty()) ? "0" : mi.inData.get("IDSD")
    iUD01 = (mi.inData.get("UD01") == null || mi.inData.get("UD01").trim().isEmpty()) ? "" : mi.inData.get("UD01")
    iUD02 = (mi.inData.get("UD02") == null || mi.inData.get("UD02").trim().isEmpty()) ? "" : mi.inData.get("UD02")
    iUD04 = (mi.inData.get("UD04") == null || mi.inData.get("UD04").trim().isEmpty()) ? "" : mi.inData.get("UD04")
    iUD05 = (mi.inData.get("UD05") == null || mi.inData.get("UD05").trim().isEmpty()) ? "" : mi.inData.get("UD05")
    iUD06 = (mi.inData.get("UD06") == null || mi.inData.get("UD06").trim().isEmpty()) ? "" : mi.inData.get("UD06")
    iUD07 = (mi.inData.get("UD07") == null || mi.inData.get("UD07").trim().isEmpty()) ? "" : mi.inData.get("UD07")
    iUD08 = (mi.inData.get("UD08") == null || mi.inData.get("UD08").trim().isEmpty()) ? "" : mi.inData.get("UD08")
    iUD09 = (mi.inData.get("UD09") == null || mi.inData.get("UD09").trim().isEmpty()) ? "" : mi.inData.get("UD09")
    iUD10 = (mi.inData.get("UD10") == null || mi.inData.get("UD10").trim().isEmpty()) ? "" : mi.inData.get("UD10")
    iUD11 = (mi.inData.get("UD11") == null || mi.inData.get("UD11").trim().isEmpty()) ? "" : mi.inData.get("UD11")
    iUD12 = (mi.inData.get("UD12") == null || mi.inData.get("UD12").trim().isEmpty()) ? "" : mi.inData.get("UD12")
    iUD13 = (mi.inData.get("UD13") == null || mi.inData.get("UD13").trim().isEmpty()) ? "" : mi.inData.get("UD13")
    iUD14 = (mi.inData.get("UD14") == null || mi.inData.get("UD14").trim().isEmpty()) ? "" : mi.inData.get("UD14")
    iUD15 = (mi.inData.get("UD15") == null || mi.inData.get("UD15").trim().isEmpty()) ? "" : mi.inData.get("UD15")
    iUD16 = (mi.inData.get("UD16") == null || mi.inData.get("UD16").trim().isEmpty()) ? "" : mi.inData.get("UD16")
    iUD17 = (mi.inData.get("UD17") == null || mi.inData.get("UD17").trim().isEmpty()) ? "" : mi.inData.get("UD17")
    iUD18 = (mi.inData.get("UD18") == null || mi.inData.get("UD18").trim().isEmpty()) ? "" : mi.inData.get("UD18")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iCPID = (mi.inData.get("CPID") == null || mi.inData.get("CPID").trim().isEmpty()) ? 0 : mi.inData.get("CPID") as Integer
    iSNSN = (mi.inData.get("SNSN") == null || mi.inData.get("SNSN").trim().isEmpty()) ? 0 : mi.inData.get("SNSN") as Integer
    iSCSN = (mi.inData.get("SCSN") == null || mi.inData.get("SCSN").trim().isEmpty()) ? 0 : mi.inData.get("SCSN") as Integer
    iPLSN = (mi.inData.get("PLSN") == null || mi.inData.get("PLSN").trim().isEmpty()) ? 0 : mi.inData.get("PLSN") as Integer
    iUD03 = (mi.inData.get("UD03") == null || mi.inData.get("UD03").trim().isEmpty()) ? 0 : mi.inData.get("UD03") as Integer

    validateInput()
    if (validInput) {
      updateRecord()
    }

  }

  /**
   *Validate inputs
   * @params int CONO ,String ITNO,String WHLO,String iWHSL
   * @return void
   */
  public validateInput() {

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
    if (!iDIVI.toString().trim()) {
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
    }

    //Validate Customer Number
    params = ["CUNO": iPYCU.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.CUNO == null) {
        mi.error("Invalid Customer Numebr Number " + iPYCU)
        validInput = false
        return
      }
    }
    miCaller.call("CRS610MI", "GetBasicData", params, callback)

    //Validate Warehouse Number
    params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.WHLO == null) {
        mi.error("Invalid Warehouse Number " + iWHLO)
        validInput = false
        return
      }
    }
    miCaller.call("MMS005MI", "GetWarehouse", params, callback)
  }
  /**
   *Update records to EXTNCT table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTNCT").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXPYCU", iPYCU)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
    if (!iSPOC.trim().isEmpty()) {
      if (iSPOC.trim().equals("?")) {
        lockedResult.set("EXSPOC", "")
      } else {
        lockedResult.set("EXSPOC", iSPOC)
      }
    }
    if (!iASNP.trim().isEmpty()) {
      if (iASNP.trim().equals("?")) {
        lockedResult.set("EXASNP", "")
      } else {
        lockedResult.set("EXASNP", iASNP)
      }
    }
    if (!iDEMH.trim().isEmpty()) {
      if (iDEMH.trim().equals("?")) {
        lockedResult.set("EXDEMH", "")
      } else {
        lockedResult.set("EXDEMH", iDEMH)
      }
    }
    if (!iDFLB.trim().isEmpty()) {
      if (iDFLB.trim().equals("?")) {
        lockedResult.set("EXDFLB", "")
      } else {
        lockedResult.set("EXDFLB", iDFLB)
      }
    }
    if (!iSFLB.trim().isEmpty()) {
      if (iSFLB.trim().equals("?")) {
        lockedResult.set("EXSFLB", "")
      } else {
        lockedResult.set("EXSFLB", iSFLB)
      }
    }
    if (!iASNF.trim().isEmpty()) {
      if (iASNF.trim().equals("?")) {
        lockedResult.set("EXASNF", "")
      } else {
        lockedResult.set("EXASNF", iASNF)
      }
    }
    if (!iSNRP.trim().isEmpty()) {
      if (iSNRP.trim().equals("?")) {
        lockedResult.set("EXSNRP", "")
      } else {
        lockedResult.set("EXSNRP", iSNRP)
      }
    }
    if (!iSNFO.trim().isEmpty()) {
      if (iSNFO.trim().equals("?")) {
        lockedResult.set("EXSNFO", "")
      } else {
        lockedResult.set("EXSNFO", iSNFO)
      }
    }
    if (!iSTAT.trim().isEmpty()) {
      if (iSTAT.trim().equals("?")) {
        lockedResult.set("EXSTAT", "")
      } else {
        lockedResult.set("EXSTAT", iSTAT)
      }
    }
    if (!iLFCR.trim().isEmpty()) {
      if (iLFCR.trim().equals("?")) {
        lockedResult.set("EXLFCR", "")
      } else {
        lockedResult.set("EXLFCR", iLFCR)
      }
    }
    if (!iLFSH.trim().isEmpty()) {
      if (iLFSH.trim().equals("?")) {
        lockedResult.set("EXLFSH", "")
      } else {
        lockedResult.set("EXLFSH", iLFSH)
      }
    }
    if (!iDCLQ.trim().isEmpty()) {
      if (iDCLQ.trim().equals("?")) {
        lockedResult.set("EXDCLQ", "")
      } else {
        lockedResult.set("EXDCLQ", iDCLQ)
      }
    }
    if (!iCPKI.trim().isEmpty()) {
      if (iCPKI.trim().equals("?")) {
        lockedResult.set("EXCPKI", "")
      } else {
        lockedResult.set("EXCPKI", iCPKI)
      }
    }
    if (!iPPKI.trim().isEmpty()) {
      if (iPPKI.trim().equals("?")) {
        lockedResult.set("EXPPKI", "")
      } else {
        lockedResult.set("EXPPKI", iPPKI)
      }
    }
    if (!iSPKI.trim().isEmpty()) {
      if (iSPKI.trim().equals("?")) {
        lockedResult.set("EXSPKI", "")
      } else {
        lockedResult.set("EXSPKI", iSPKI)
      }
    }
    if (!iEANI.trim().isEmpty()) {
      if (iEANI.trim().equals("?")) {
        lockedResult.set("EXEANI", "")
      } else {
        lockedResult.set("EXEANI", iEANI)
      }
    }
    if (!iIDSD.trim().isEmpty()) {
      if (iIDSD.trim().equals("?")) {
        lockedResult.set("EXIDSD", "")
      } else {
        lockedResult.set("EXIDSD", iIDSD)
      }
    }
    if (!iUD01.trim().isEmpty()) {
      if (iUD01.trim().equals("?")) {
        lockedResult.set("EXUD01", "")
      } else {
        lockedResult.set("EXUD01", iUD01)
      }
    }
    if (!iUD02.trim().isEmpty()) {
      if (iUD02.trim().equals("?")) {
        lockedResult.set("EXUD02", "")
      } else {
        lockedResult.set("EXUD02", iUD02)
      }
    }
    if (!iUD04.trim().isEmpty()) {
      if (iUD04.trim().equals("?")) {
        lockedResult.set("EXUD04", "")
      } else {
        lockedResult.set("EXUD04", iUD04)
      }
    }
    if (!iUD05.trim().isEmpty()) {
      if (iUD05.trim().equals("?")) {
        lockedResult.set("EXUD05", "")
      } else {
        lockedResult.set("EXUD05", iUD05)
      }
    }
    if (!iUD06.trim().isEmpty()) {
      if (iUD06.trim().equals("?")) {
        lockedResult.set("EXUD06", "")
      } else {
        lockedResult.set("EXUD06", iUD06)
      }
    }
    if (!iUD07.trim().isEmpty()) {
      if (iUD07.trim().equals("?")) {
        lockedResult.set("EXUD07", "")
      } else {
        lockedResult.set("EXUD07", iUD07)
      }
    }
    if (!iUD08.trim().isEmpty()) {
      if (iUD08.trim().equals("?")) {
        lockedResult.set("EXUD08", "")
      } else {
        lockedResult.set("EXUD08", iUD08)
      }
    }
    if (!iUD09.trim().isEmpty()) {
      if (iUD09.trim().equals("?")) {
        lockedResult.set("EXUD09", "")
      } else {
        lockedResult.set("EXUD09", iUD09)
      }
    }
    if (!iUD10.trim().isEmpty()) {
      if (iUD10.trim().equals("?")) {
        lockedResult.set("EXUD10", "")
      } else {
        lockedResult.set("EXUD10", iUD10)
      }
    }
    if (!iUD11.trim().isEmpty()) {
      if (iUD11.trim().equals("?")) {
        lockedResult.set("EXUD11", "")
      } else {
        lockedResult.set("EXUD11", iUD11)
      }
    }
    if (!iUD12.trim().isEmpty()) {
      if (iUD12.trim().equals("?")) {
        lockedResult.set("EXUD12", "")
      } else {
        lockedResult.set("EXUD12", iUD12)
      }
    }
    if (!iUD13.trim().isEmpty()) {
      if (iUD13.trim().equals("?")) {
        lockedResult.set("EXUD13", "")
      } else {
        lockedResult.set("EXUD13", iUD13)
      }
    }
    if (!iUD14.trim().isEmpty()) {
      if (iUD14.trim().equals("?")) {
        lockedResult.set("EXUD14", "")
      } else {
        lockedResult.set("EXUD14", iUD14)
      }
    }
    if (!iUD15.trim().isEmpty()) {
      if (iUD15.trim().equals("?")) {
        lockedResult.set("EXUD15", "")
      } else {
        lockedResult.set("EXUD15", iUD15)
      }
    }
    if (!iUD16.trim().isEmpty()) {
      if (iUD16.trim().equals("?")) {
        lockedResult.set("EXUD16", "")
      } else {
        lockedResult.set("EXUD16", iUD16)
      }
    }
    if (!iUD17.trim().isEmpty()) {
      if (iUD17.trim().equals("?")) {
        lockedResult.set("EXUD17", "")
      } else {
        lockedResult.set("EXUD17", iUD17)
      }
    }
    if (!iUD18.trim().isEmpty()) {
      if (iUD18.trim().equals("?")) {
        lockedResult.set("EXUD18", "")
      } else {
        lockedResult.set("EXUD18", iUD18)
      }
    }
    if (mi.inData.get("CPID") != null && !mi.inData.get("CPID").trim().isEmpty()) {
      lockedResult.set("EXCPID", iCPID)
    }
    if (mi.inData.get("SNSN") != null && !mi.inData.get("SNSN").trim().isEmpty()) {
      lockedResult.set("EXSNSN", iSNSN)
    }
    if (mi.inData.get("SCSN") != null && !mi.inData.get("SCSN").trim().isEmpty()) {
      lockedResult.set("EXSCSN", iSCSN)
    }
    if (mi.inData.get("PLSN") != null && !mi.inData.get("PLSN").trim().isEmpty()) {
      lockedResult.set("EXPLSN", iPLSN)
    }
    if (mi.inData.get("UD03") != null && !mi.inData.get("UD03").trim().isEmpty()) {
      lockedResult.set("EXUD03", iUD03)
    }
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}