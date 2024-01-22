/**
 * README
 * This extension is being used to Add records to EXTIHD table. 
 *
 * Name: EXT007MI.AddInvoiceLines
 * Description: Adding records to EXTIHD table 
 * Date	      Changed By                      Description
 *20230210  SuriyaN@fortude.co     Adding records to EXTIHD table 
*/

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


public class AddInvoiceLines extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller
    private final UtilityAPI utility

    private String iHSEQ, iDSEQ, iITNO, iITTY, iFUDS, iWHLO, iPRLV, iPRLN, iTXID, iUCLR, iURFT, iURFN, iUDSG, iUDGR, iUCLC, iUCLS
    private int iCONO, iIVNO
    private Double iUNPR, iUNCS, iSHQT, iORQT, iBOQT
    private boolean validInput = true

    public AddInvoiceLines(MIAPI mi, DatabaseAPI database, ProgramAPI program,MICallerAPI miCaller,UtilityAPI utility) {
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
        iDSEQ = (mi.inData.get("DSEQ") == null || mi.inData.get("DSEQ").trim().isEmpty()) ? "" : mi.inData.get("DSEQ")
        iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
        iITTY = (mi.inData.get("ITTY") == null || mi.inData.get("ITTY").trim().isEmpty()) ? "" : mi.inData.get("ITTY")
        iFUDS = (mi.inData.get("FUDS") == null || mi.inData.get("FUDS").trim().isEmpty()) ? "" : mi.inData.get("FUDS")
        iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
        iPRLV = (mi.inData.get("PRLV") == null || mi.inData.get("PRLV").trim().isEmpty()) ? "0" : mi.inData.get("PRLV")
        iPRLN = (mi.inData.get("PRLN") == null || mi.inData.get("PRLN").trim().isEmpty()) ? "" : mi.inData.get("PRLN")
        iTXID = (mi.inData.get("TXID") == null || mi.inData.get("TXID").trim().isEmpty()) ? "" : mi.inData.get("TXID")
        iUCLR = (mi.inData.get("UCLR") == null || mi.inData.get("UCLR").trim().isEmpty()) ? "" : mi.inData.get("UCLR")
        iURFT = (mi.inData.get("URFT") == null || mi.inData.get("URFT").trim().isEmpty()) ? "" : mi.inData.get("URFT")
        iURFN = (mi.inData.get("URFN") == null || mi.inData.get("URFN").trim().isEmpty()) ? "" : mi.inData.get("URFN")
        iUDSG = (mi.inData.get("UDSG") == null || mi.inData.get("UDSG").trim().isEmpty()) ? "" : mi.inData.get("UDSG")
        iUDGR = (mi.inData.get("UDGR") == null || mi.inData.get("UDGR").trim().isEmpty()) ? "" : mi.inData.get("UDGR")
        iUCLC = (mi.inData.get("UCLC") == null || mi.inData.get("UCLC").trim().isEmpty()) ? "" : mi.inData.get("UCLC")
        iUCLS = (mi.inData.get("UCLS") == null || mi.inData.get("UCLS").trim().isEmpty()) ? "" : mi.inData.get("UCLS")

        iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
        iIVNO = (mi.inData.get("IVNO") == null || mi.inData.get("IVNO").trim().isEmpty()) ? 0 : mi.inData.get("IVNO") as Integer
        iUNPR = (mi.inData.get("UNPR") == null || mi.inData.get("UNPR").trim().isEmpty()) ? 0 : mi.inData.get("UNPR") as Double
        iUNCS = (mi.inData.get("UNCS") == null || mi.inData.get("UNCS").trim().isEmpty()) ? 0 : mi.inData.get("UNCS") as Double
        iSHQT = (mi.inData.get("SHQT") == null || mi.inData.get("SHQT").trim().isEmpty()) ? 0 : mi.inData.get("SHQT") as Double
        iORQT = (mi.inData.get("ORQT") == null || mi.inData.get("ORQT").trim().isEmpty()) ? 0 : mi.inData.get("ORQT") as Double
        iBOQT = (mi.inData.get("BOQT") == null || mi.inData.get("BOQT").trim().isEmpty()) ? 0 : mi.inData.get("BOQT") as Double

        validateInput(iCONO, iIVNO, iITNO, iITTY, iWHLO)
        if (validInput) {
          insertRecord()
        }
    }
    
    /**
   *Validate inputs
   * @params int CONO, String IVNO, String ITNO, String ITTY, String WHLO
   * @return void
   */
  private validateInput(int CONO, int IVNO, String ITNO, String ITTY, String WHLO) {

    //Validate Company Number
    Map<String, String> params = ["CONO": CONO.toString().trim()]
    Closure<?> callback = {
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
    if(IVNO==0){
       mi.error("Invoice must be entered")
       validInput = false
       return
      
    }
    
    if (!WHLO.trim().isEmpty()) {
      //Validate Warehouse Number
      Map<String, String> paramsWHLO = ["CONO": CONO.toString().trim(), "WHLO": WHLO.toString().trim()]
      Closure<?> callbackWHLO = {
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
    
    if(!ITTY.trim().isEmpty())
    {
      //Validate Item Type
      params = ["CONO": CONO.toString().trim(), "ITTY": ITTY.toString().trim()]
      callback = {
        Map < String,
          String > response ->
          if (response.TX40 == null) {
            mi.error("Invalid Item Type " + ITTY)
            validInput = false
            return false
          }
      }
      miCaller.call("CRS040MI", "GetItmType", params, callback)
    }
    
    if(!ITNO.trim().isEmpty())
    {
    //Validate Item Number
    params = ["CONO": iCONO.toString().trim(),"ITNO":iITNO.toString().trim()]
    callback = {
      Map < String,
        String > response ->
        if (response.ITNO == null) {
          mi.error("Invalid Item Number " + iITNO)
          validInput = false
          return false
        }
    }
    miCaller.call("MMS200MI", "Get", params, callback)
    }


    
  }
    
    
    /**
     *Insert records to EXTIHD table
     * @params 
     * @return 
     */
    public insertRecord() {
        DBAction action = database.table("EXTIHD").index("00").build()
        DBContainer query = action.getContainer()

        query.set("EXHSEQ", iHSEQ)
        query.set("EXDSEQ", iDSEQ)
        query.set("EXITNO", iITNO)
        query.set("EXITTY", iITTY)
        query.set("EXFUDS", iFUDS)
        query.set("EXWHLO", iWHLO)
        query.set("EXPRLV", iPRLV)
        query.set("EXPRLN", iPRLN)
        query.set("EXTXID", iTXID)
        query.set("EXUCLR", iUCLR)
        query.set("EXURFT", iURFT)
        query.set("EXURFN", iURFN)
        query.set("EXUDSG", iUDSG)
        query.set("EXUDGR", iUDGR)
        query.set("EXUCLC", iUCLC)
        query.set("EXUCLS", iUCLS)
        query.set("EXCONO", iCONO as Integer)
        query.set("EXIVNO", iIVNO as Integer)
        query.set("EXSHQT", iSHQT as Double)
        query.set("EXORQT", iORQT as Double)
        query.set("EXBOQT", iBOQT as Double)
        query.set("EXUNPR", iUNPR as Double)
        query.set("EXUNCS", iUNCS as Double)
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