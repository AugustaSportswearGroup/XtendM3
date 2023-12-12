/**
 * README
 * This extension is being used to Update records to EXTCCF table. 
 *
 * Name: EXT611MI.UpdEDICustomer
 * Description: Updating records to EXTCCF table
 * Date	      Changed By                      Description
 *20230913  SuriyaN@fortude.co    Updating records to EXTCCF table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdEDICustomer extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iPYCU, iSPOC, iSTAT, iHPDD, iHIVD, iHORD, iDPDD, iLPDD, iDIVD, iLIVD, iDORD, iPORD, iLORD, iDOSD, iLOSD, iDAND, iLAND, iDACD, iLACD, iDSCD, iLSCD, iDROD, iPROD, iLROD, iDIVC, iLIVC, iDOAK, iLOAK, iDEX2, iLEX2, iDEX3, iLEX3, iDEX4, iLEX4, iUD01, iUD02, iSTNM, iSDFL
  private int iCONO, iUD03
  private boolean validInput = true
  
  public UpdEDICustomer(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iSPOC = (mi.inData.get("SPOC") == null || mi.inData.get("SPOC").trim().isEmpty()) ? "" : mi.inData.get("SPOC")
    iSTAT = (mi.inData.get("STAT") == null || mi.inData.get("STAT").trim().isEmpty()) ? "" : mi.inData.get("STAT")
    iHPDD = (mi.inData.get("HPDD") == null || mi.inData.get("HPDD").trim().isEmpty()) ? "" : mi.inData.get("HPDD")
    iHIVD = (mi.inData.get("HIVD") == null || mi.inData.get("HIVD").trim().isEmpty()) ? "" : mi.inData.get("HIVD")
    iHORD = (mi.inData.get("HORD") == null || mi.inData.get("HORD").trim().isEmpty()) ? "" : mi.inData.get("HORD")
    iDPDD = (mi.inData.get("DPDD") == null || mi.inData.get("DPDD").trim().isEmpty()) ? "" : mi.inData.get("DPDD")
    iLPDD = (mi.inData.get("LPDD") == null || mi.inData.get("LPDD").trim().isEmpty()) ? "" : mi.inData.get("LPDD")
    iDIVD = (mi.inData.get("DIVD") == null || mi.inData.get("DIVD").trim().isEmpty()) ? "" : mi.inData.get("DIVD")
    iLIVD = (mi.inData.get("LIVD") == null || mi.inData.get("LIVD").trim().isEmpty()) ? "" : mi.inData.get("LIVD")
    iDORD = (mi.inData.get("DORD") == null || mi.inData.get("DORD").trim().isEmpty()) ? "" : mi.inData.get("DORD")
    iPORD = (mi.inData.get("PORD") == null || mi.inData.get("PORD").trim().isEmpty()) ? "" : mi.inData.get("PORD")
    iLORD = (mi.inData.get("LORD") == null || mi.inData.get("LORD").trim().isEmpty()) ? "" : mi.inData.get("LORD")
    iDOSD = (mi.inData.get("DOSD") == null || mi.inData.get("DOSD").trim().isEmpty()) ? "" : mi.inData.get("DOSD")
    iLOSD = (mi.inData.get("LOSD") == null || mi.inData.get("LOSD").trim().isEmpty()) ? "" : mi.inData.get("LOSD")
    iDAND = (mi.inData.get("DAND") == null || mi.inData.get("DAND").trim().isEmpty()) ? "" : mi.inData.get("DAND")
    iLAND = (mi.inData.get("LAND") == null || mi.inData.get("LAND").trim().isEmpty()) ? "" : mi.inData.get("LAND")
    iDACD = (mi.inData.get("DACD") == null || mi.inData.get("DACD").trim().isEmpty()) ? "" : mi.inData.get("DACD")
    iLACD = (mi.inData.get("LACD") == null || mi.inData.get("LACD").trim().isEmpty()) ? "" : mi.inData.get("LACD")
    iDSCD = (mi.inData.get("DSCD") == null || mi.inData.get("DSCD").trim().isEmpty()) ? "" : mi.inData.get("DSCD")
    iLSCD = (mi.inData.get("LSCD") == null || mi.inData.get("LSCD").trim().isEmpty()) ? "" : mi.inData.get("LSCD")
    iDROD = (mi.inData.get("DROD") == null || mi.inData.get("DROD").trim().isEmpty()) ? "" : mi.inData.get("DROD")
    iPROD = (mi.inData.get("PROD") == null || mi.inData.get("PROD").trim().isEmpty()) ? "" : mi.inData.get("PROD")
    iLROD = (mi.inData.get("LROD") == null || mi.inData.get("LROD").trim().isEmpty()) ? "" : mi.inData.get("LROD")
    iDIVC = (mi.inData.get("DIVC") == null || mi.inData.get("DIVC").trim().isEmpty()) ? "" : mi.inData.get("DIVC")
    iLIVC = (mi.inData.get("LIVC") == null || mi.inData.get("LIVC").trim().isEmpty()) ? "" : mi.inData.get("LIVC")
    iDOAK = (mi.inData.get("DOAK") == null || mi.inData.get("DOAK").trim().isEmpty()) ? "" : mi.inData.get("DOAK")
    iLOAK = (mi.inData.get("LOAK") == null || mi.inData.get("LOAK").trim().isEmpty()) ? "" : mi.inData.get("LOAK")
    iDEX2 = (mi.inData.get("DEX2") == null || mi.inData.get("DEX2").trim().isEmpty()) ? "" : mi.inData.get("DEX2")
    iLEX2 = (mi.inData.get("LEX2") == null || mi.inData.get("LEX2").trim().isEmpty()) ? "" : mi.inData.get("LEX2")
    iDEX3 = (mi.inData.get("DEX3") == null || mi.inData.get("DEX3").trim().isEmpty()) ? "" : mi.inData.get("DEX3")
    iLEX3 = (mi.inData.get("LEX3") == null || mi.inData.get("LEX3").trim().isEmpty()) ? "" : mi.inData.get("LEX3")
    iDEX4 = (mi.inData.get("DEX4") == null || mi.inData.get("DEX4").trim().isEmpty()) ? "" : mi.inData.get("DEX4")
    iLEX4 = (mi.inData.get("LEX4") == null || mi.inData.get("LEX4").trim().isEmpty()) ? "" : mi.inData.get("LEX4")
    iUD01 = (mi.inData.get("UD01") == null || mi.inData.get("UD01").trim().isEmpty()) ? "" : mi.inData.get("UD01")
    iUD02 = (mi.inData.get("UD02") == null || mi.inData.get("UD02").trim().isEmpty()) ? "" : mi.inData.get("UD02")
    iSTNM = (mi.inData.get("STNM") == null || mi.inData.get("STNM").trim().isEmpty()) ? "" : mi.inData.get("STNM")
    iSDFL = (mi.inData.get("SDFL") == null || mi.inData.get("SDFL").trim().isEmpty()) ? "" : mi.inData.get("SDFL")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iUD03 = (mi.inData.get("UD03") == null || mi.inData.get("UD03").trim().isEmpty()) ? 0 : mi.inData.get("UD03") as Integer
    
     validateInput()
    if (validInput) {
      updateRecord()
    }
    
  }
  
  
  /**
   *Validate records 
   * @params 
   * @return 
   */
  public validateInput() {
    //Validate Company Number
    Map<String, String> params = ["CONO": iCONO.toString().trim()]
    Closure<?>  callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Customer Number
    params = ["CUNO": iPYCU.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.CUNO == null) {
        mi.error("Invalid Customer Numebr Number " + iPYCU)
        validInput = false
        return false
      }
    }
    miCaller.call("CRS610MI", "GetBasicData", params, callback)
    
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
  
  /**
   *Update records to EXTCCF table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTCCF").index("00").build()
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
    if (!iSTAT.trim().isEmpty()) {
      if (iSTAT.trim().equals("?")) {
        lockedResult.set("EXSTAT", "")
      } else {
        lockedResult.set("EXSTAT", iSTAT)
      }
    }
    if (!iHPDD.trim().isEmpty()) {
      if (iHPDD.trim().equals("?")) {
        lockedResult.set("EXHPDD", "")
      } else {
        lockedResult.set("EXHPDD", iHPDD)
      }
    }
    if (!iHIVD.trim().isEmpty()) {
      if (iHIVD.trim().equals("?")) {
        lockedResult.set("EXHIVD", "")
      } else {
        lockedResult.set("EXHIVD", iHIVD)
      }
    }
    if (!iHORD.trim().isEmpty()) {
      if (iHORD.trim().equals("?")) {
        lockedResult.set("EXHORD", "")
      } else {
        lockedResult.set("EXHORD", iHORD)
      }
    }
    if (!iDPDD.trim().isEmpty()) {
      if (iDPDD.trim().equals("?")) {
        lockedResult.set("EXDPDD", "")
      } else {
        lockedResult.set("EXDPDD", iDPDD)
      }
    }
    if (!iLPDD.trim().isEmpty()) {
      if (iLPDD.trim().equals("?")) {
        lockedResult.set("EXLPDD", "")
      } else {
        lockedResult.set("EXLPDD", iLPDD)
      }
    }
    if (!iDIVD.trim().isEmpty()) {
      if (iDIVD.trim().equals("?")) {
        lockedResult.set("EXDIVD", "")
      } else {
        lockedResult.set("EXDIVD", iDIVD)
      }
    }
    if (!iLIVD.trim().isEmpty()) {
      if (iLIVD.trim().equals("?")) {
        lockedResult.set("EXLIVD", "")
      } else {
        lockedResult.set("EXLIVD", iLIVD)
      }
    }
    if (!iDORD.trim().isEmpty()) {
      if (iDORD.trim().equals("?")) {
        lockedResult.set("EXDORD", "")
      } else {
        lockedResult.set("EXDORD", iDORD)
      }
    }
    if (!iPORD.trim().isEmpty()) {
      if (iPORD.trim().equals("?")) {
        lockedResult.set("EXPORD", "")
      } else {
        lockedResult.set("EXPORD", iPORD)
      }
    }
    if (!iLORD.trim().isEmpty()) {
      if (iLORD.trim().equals("?")) {
        lockedResult.set("EXLORD", "")
      } else {
        lockedResult.set("EXLORD", iLORD)
      }
    }
    if (!iDOSD.trim().isEmpty()) {
      if (iDOSD.trim().equals("?")) {
        lockedResult.set("EXDOSD", "")
      } else {
        lockedResult.set("EXDOSD", iDOSD)
      }
    }
    if (!iLOSD.trim().isEmpty()) {
      if (iLOSD.trim().equals("?")) {
        lockedResult.set("EXLOSD", "")
      } else {
        lockedResult.set("EXLOSD", iLOSD)
      }
    }
    if (!iDAND.trim().isEmpty()) {
      if (iDAND.trim().equals("?")) {
        lockedResult.set("EXDAND", "")
      } else {
        lockedResult.set("EXDAND", iDAND)
      }
    }
    if (!iLAND.trim().isEmpty()) {
      if (iLAND.trim().equals("?")) {
        lockedResult.set("EXLAND", "")
      } else {
        lockedResult.set("EXLAND", iLAND)
      }
    }
    if (!iDACD.trim().isEmpty()) {
      if (iDACD.trim().equals("?")) {
        lockedResult.set("EXDACD", "")
      } else {
        lockedResult.set("EXDACD", iDACD)
      }
    }
    if (!iLACD.trim().isEmpty()) {
      if (iLACD.trim().equals("?")) {
        lockedResult.set("EXLACD", "")
      } else {
        lockedResult.set("EXLACD", iLACD)
      }
    }
    if (!iDSCD.trim().isEmpty()) {
      if (iDSCD.trim().equals("?")) {
        lockedResult.set("EXDSCD", "")
      } else {
        lockedResult.set("EXDSCD", iDSCD)
      }
    }
    if (!iLSCD.trim().isEmpty()) {
      if (iLSCD.trim().equals("?")) {
        lockedResult.set("EXLSCD", "")
      } else {
        lockedResult.set("EXLSCD", iLSCD)
      }
    }
    if (!iDROD.trim().isEmpty()) {
      if (iDROD.trim().equals("?")) {
        lockedResult.set("EXDROD", "")
      } else {
        lockedResult.set("EXDROD", iDROD)
      }
    }
    if (!iPROD.trim().isEmpty()) {
      if (iPROD.trim().equals("?")) {
        lockedResult.set("EXPROD", "")
      } else {
        lockedResult.set("EXPROD", iPROD)
      }
    }
    if (!iLROD.trim().isEmpty()) {
      if (iLROD.trim().equals("?")) {
        lockedResult.set("EXLROD", "")
      } else {
        lockedResult.set("EXLROD", iLROD)
      }
    }
    if (!iDIVC.trim().isEmpty()) {
      if (iDIVC.trim().equals("?")) {
        lockedResult.set("EXDIVC", "")
      } else {
        lockedResult.set("EXDIVC", iDIVC)
      }
    }
    if (!iLIVC.trim().isEmpty()) {
      if (iLIVC.trim().equals("?")) {
        lockedResult.set("EXLIVC", "")
      } else {
        lockedResult.set("EXLIVC", iLIVC)
      }
    }
    if (!iDOAK.trim().isEmpty()) {
      if (iDOAK.trim().equals("?")) {
        lockedResult.set("EXDOAK", "")
      } else {
        lockedResult.set("EXDOAK", iDOAK)
      }
    }
    if (!iLOAK.trim().isEmpty()) {
      if (iLOAK.trim().equals("?")) {
        lockedResult.set("EXLOAK", "")
      } else {
        lockedResult.set("EXLOAK", iLOAK)
      }
    }
    if (!iDEX2.trim().isEmpty()) {
      if (iDEX2.trim().equals("?")) {
        lockedResult.set("EXDEX2", "")
      } else {
        lockedResult.set("EXDEX2", iDEX2)
      }
    }
    if (!iLEX2.trim().isEmpty()) {
      if (iLEX2.trim().equals("?")) {
        lockedResult.set("EXLEX2", "")
      } else {
        lockedResult.set("EXLEX2", iLEX2)
      }
    }
    if (!iDEX3.trim().isEmpty()) {
      if (iDEX3.trim().equals("?")) {
        lockedResult.set("EXDEX3", "")
      } else {
        lockedResult.set("EXDEX3", iDEX3)
      }
    }
    if (!iLEX3.trim().isEmpty()) {
      if (iLEX3.trim().equals("?")) {
        lockedResult.set("EXLEX3", "")
      } else {
        lockedResult.set("EXLEX3", iLEX3)
      }
    }
    if (!iDEX4.trim().isEmpty()) {
      if (iDEX4.trim().equals("?")) {
        lockedResult.set("EXDEX4", "")
      } else {
        lockedResult.set("EXDEX4", iDEX4)
      }
    }
    if (!iLEX4.trim().isEmpty()) {
      if (iLEX4.trim().equals("?")) {
        lockedResult.set("EXLEX4", "")
      } else {
        lockedResult.set("EXLEX4", iLEX4)
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
    if (!iSTNM.trim().isEmpty()) {
      if (iSTNM.trim().equals("?")) {
        lockedResult.set("EXSTNM", "")
      } else {
        lockedResult.set("EXSTNM", iSTNM)
      }
    }
    if (!iSDFL.trim().isEmpty()) {
      if (iSDFL.trim().equals("?")) {
        lockedResult.set("EXSDFL", "")
      } else {
        lockedResult.set("EXSDFL", iSDFL)
      }
    }
    if (mi.inData.get("UD03") != null && !mi.inData.get("UD03").trim().isEmpty()) {
      lockedResult.set("EXUD03", iUD03)
    } else {
      lockedResult.set("EXUD03", 0)
    }
    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}