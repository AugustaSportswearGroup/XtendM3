/**
 * README
 * This extension is being used to Add records to EXTNCT table. 
 *
 * Name: EXT856MI.AddASNHead
 * Description: Adding records to EXTNCT table 
 * Date	      Changed By                      Description
 *20230829  SuriyaN@fortude.co     Adding records to EXTNCT table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddASNHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iPYCU, iSPOC, iASNP, iDEMH, iDFLB, iSFLB, iASNF, iSNRP, iSNFO, iSTAT, iLFCR, iLFSH, iDCLQ, iCPKI, iPPKI, iSPKI, iEANI, iIDSD, iUD01, iUD02, iUD04, iUD05, iUD06, iUD07, iUD08, iUD09, iUD10, iUD11, iUD12, iUD13, iUD14, iUD15, iUD16, iUD17, iUD18
  private int iCONO, iCPID, iSNSN, iSCSN, iPLSN, iUD03
  private boolean validInput = true

  public AddASNHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
      insertRecord()
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
   *Insert records to EXTNCT table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTNCT").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXDIVI", iDIVI)
    query.set("EXWHLO", iWHLO)
    query.set("EXPYCU", iPYCU)
    query.set("EXSPOC", iSPOC)
    query.set("EXASNP", iASNP)
    query.set("EXDEMH", iDEMH)
    query.set("EXDFLB", iDFLB)
    query.set("EXSFLB", iSFLB)
    query.set("EXASNF", iASNF)
    query.set("EXSNRP", iSNRP)
    query.set("EXSNFO", iSNFO)
    query.set("EXSTAT", iSTAT)
    query.set("EXLFCR", iLFCR)
    query.set("EXLFSH", iLFSH)
    query.set("EXDCLQ", iDCLQ)
    query.set("EXCPKI", iCPKI)
    query.set("EXPPKI", iPPKI)
    query.set("EXSPKI", iSPKI)
    query.set("EXEANI", iEANI)
    query.set("EXIDSD", iIDSD)
    query.set("EXUD01", iUD01)
    query.set("EXUD02", iUD02)
    query.set("EXUD04", iUD04)
    query.set("EXUD05", iUD05)
    query.set("EXUD06", iUD06)
    query.set("EXUD07", iUD07)
    query.set("EXUD08", iUD08)
    query.set("EXUD09", iUD09)
    query.set("EXUD10", iUD10)
    query.set("EXUD11", iUD11)
    query.set("EXUD12", iUD12)
    query.set("EXUD13", iUD13)
    query.set("EXUD14", iUD14)
    query.set("EXUD15", iUD15)
    query.set("EXUD16", iUD16)
    query.set("EXUD17", iUD17)
    query.set("EXUD18", iUD18)
    query.set("EXCONO", iCONO as Integer)
    query.set("EXCPID", iCPID as Integer)
    query.set("EXSNSN", iSNSN as Integer)
    query.set("EXSCSN", iSCSN as Integer)
    query.set("EXPLSN", iPLSN as Integer)
    query.set("EXUD03", iUD03 as Integer)
    query.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("EXCHNO", 0)
    query.set("EXCHID", program.getUser())
    action.insert(query, recordExists)
  }

  Closure recordExists = {
    mi.error("Record Already Exists")
    return
  }
}