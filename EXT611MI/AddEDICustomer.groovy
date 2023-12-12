/**
 * README
 * This extension is being used to Add records to EXTCCF table. 
 *
 * Name: EXT611MI.AddEDICustomer
 * Description: Adding records to EXTCCF table 
 * Date	      Changed By                      Description
 *20230913  SuriyaN@fortude.co     Adding records to EXTCCF table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddEDICustomer extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iPYCU, iSPOC, iSTAT, iHPDD, iHIVD, iHORD, iDPDD, iLPDD, iDIVD, iLIVD, iDORD, iPORD, iLORD, iDOSD, iLOSD, iDAND, iLAND, iDACD, iLACD, iDSCD, iLSCD, iDROD, iPROD, iLROD, iDIVC, iLIVC, iDOAK, iLOAK, iDEX2, iLEX2, iDEX3, iLEX3, iDEX4, iLEX4, iUD01, iUD02, iSTNM, iSDFL
  private int iCONO, iUD03
  private boolean validInput = true

  public AddEDICustomer(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
      insertRecord()
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
   *Insert records to EXTCCF table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("EXTCCF").index("00").build()
    DBContainer query = action.getContainer()

    query.set("EXDIVI", iDIVI)
    query.set("EXWHLO", iWHLO)
    query.set("EXPYCU", iPYCU)
    query.set("EXSPOC", iSPOC)
    query.set("EXSTAT", iSTAT)
    query.set("EXHPDD", iHPDD)
    query.set("EXHIVD", iHIVD)
    query.set("EXHORD", iHORD)
    query.set("EXDPDD", iDPDD)
    query.set("EXLPDD", iLPDD)
    query.set("EXDIVD", iDIVD)
    query.set("EXLIVD", iLIVD)
    query.set("EXDORD", iDORD)
    query.set("EXPORD", iPORD)
    query.set("EXLORD", iLORD)
    query.set("EXDOSD", iDOSD)
    query.set("EXLOSD", iLOSD)
    query.set("EXDAND", iDAND)
    query.set("EXLAND", iLAND)
    query.set("EXDACD", iDACD)
    query.set("EXLACD", iLACD)
    query.set("EXDSCD", iDSCD)
    query.set("EXLSCD", iLSCD)
    query.set("EXDROD", iDROD)
    query.set("EXPROD", iPROD)
    query.set("EXLROD", iLROD)
    query.set("EXDIVC", iDIVC)
    query.set("EXLIVC", iLIVC)
    query.set("EXDOAK", iDOAK)
    query.set("EXLOAK", iLOAK)
    query.set("EXDEX2", iDEX2)
    query.set("EXLEX2", iLEX2)
    query.set("EXDEX3", iDEX3)
    query.set("EXLEX3", iLEX3)
    query.set("EXDEX4", iDEX4)
    query.set("EXLEX4", iLEX4)
    query.set("EXUD01", iUD01)
    query.set("EXUD02", iUD02)
    query.set("EXSTNM", iSTNM)
    query.set("EXSDFL", iSDFL)
    query.set("EXCONO", iCONO as Integer)
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