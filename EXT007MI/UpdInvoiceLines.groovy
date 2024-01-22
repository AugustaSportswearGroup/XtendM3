/**
 * README
 * This extension is being used to Update records to EXTIHD table. 
 *
 * Name: EXT007MI.UpdInvoiceLines
 * Description: Updating records to EXTIHD table
 * Date       Changed By                      Description
 *20230210  SuriyaN@fortude.co    Updating records to EXTIHD table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


public class UpdInvoiceLines extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller
    private final UtilityAPI utility

    private String iHSEQ, iDSEQ, iITNO, iITTY, iFUDS, iWHLO, iPRLV, iPRLN, iTXID, iUCLR, iURFT, iURFN, iUDSG, iUDGR, iUCLC, iUCLS
    private int iCONO, iIVNO
    private Double iUNPR, iUNCS, iSHQT, iORQT, iBOQT
    private boolean validInput = true
     
    public UpdInvoiceLines(MIAPI mi, DatabaseAPI database, ProgramAPI program,MICallerAPI miCaller,UtilityAPI utility) {
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
        iPRLV = (mi.inData.get("PRLV") == null || mi.inData.get("PRLV").trim().isEmpty()) ? "" : mi.inData.get("PRLV")
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
        iSHQT = (mi.inData.get("SHQT") == null || mi.inData.get("SHQT").trim().isEmpty()) ? 0 : mi.inData.get("SHQT") as Double
        iORQT = (mi.inData.get("ORQT") == null || mi.inData.get("ORQT").trim().isEmpty()) ? 0 : mi.inData.get("ORQT") as Double
        iBOQT = (mi.inData.get("BOQT") == null || mi.inData.get("BOQT").trim().isEmpty()) ? 0 : mi.inData.get("BOQT") as Double
        iUNPR = (mi.inData.get("UNPR") == null || mi.inData.get("UNPR").trim().isEmpty()) ? 0 : mi.inData.get("UNPR") as Double
        iUNCS = (mi.inData.get("UNCS") == null || mi.inData.get("UNCS").trim().isEmpty()) ? 0 : mi.inData.get("UNCS") as Double
        iIVNO = (mi.inData.get("IVNO") == null || mi.inData.get("IVNO").trim().isEmpty()) ? 0 : mi.inData.get("IVNO") as Integer

        validateInput(iCONO, iIVNO, iITNO, iITTY, iWHLO)
        if (validInput) 
        {
          updateRecord()
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
    Map<String, String> paramsIVNO = ["CONO": CONO.toString().trim(), "QERY": "UHIVNO from OINVOH where UHIVNO = '" + IVNO + "'"]
    Closure<?> callbackIVNO = {
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
     *Update records to EXTIHD table
     * @params 
     * @return 
     */
    public updateRecord() {
        DBAction query = database.table("EXTIHD").index("00").build()
        DBContainer container = query.getContainer()

        container.set("EXCONO", iCONO)
        container.set("EXIVNO", iIVNO)
        container.set("EXDSEQ", iDSEQ)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
    }

    Closure<?> updateCallBack = {
        LockedResult lockedResult ->
        if (!iHSEQ.trim().isEmpty()) {
            if (iHSEQ.trim().equals("?")) {
                lockedResult.set("EXHSEQ", "")
            } else {
                lockedResult.set("EXHSEQ", iHSEQ)
            }
        }
        if (!iITNO.trim().isEmpty()) {
            if (iITNO.trim().equals("?")) {
                lockedResult.set("EXITNO", "")
            } else {
                lockedResult.set("EXITNO", iITNO)
            }
        }
        if (!iITTY.trim().isEmpty()) {
            if (iITTY.trim().equals("?")) {
                lockedResult.set("EXITTY", "")
            } else {
                lockedResult.set("EXITTY", iITTY)
            }
        }
        if (!iFUDS.trim().isEmpty()) {
            if (iFUDS.trim().equals("?")) {
                lockedResult.set("EXFUDS", "")
            } else {
                lockedResult.set("EXFUDS", iFUDS)
            }
        }
        if (!iWHLO.trim().isEmpty()) {
            if (iWHLO.trim().equals("?")) {
                lockedResult.set("EXWHLO", "")
            } else {
                lockedResult.set("EXWHLO", iWHLO)
            }
        }
        if (!iPRLV.trim().isEmpty()) {
            if (iPRLV.trim().equals("?")) {
                lockedResult.set("EXPRLV", "")
            } else {
                lockedResult.set("EXPRLV", iPRLV)
            }
        }
        if (!iPRLN.trim().isEmpty()) {
            if (iPRLN.trim().equals("?")) {
                lockedResult.set("EXPRLN", "")
            } else {
                lockedResult.set("EXPRLN", iPRLN)
            }
        }
        if (!iTXID.trim().isEmpty()) {
            if (iTXID.trim().equals("?")) {
                lockedResult.set("EXTXID", "")
            } else {
                lockedResult.set("EXTXID", iTXID)
            }
        }
        if (!iUCLR.trim().isEmpty()) {
            if (iUCLR.trim().equals("?")) {
                lockedResult.set("EXUCLR", "")
            } else {
                lockedResult.set("EXUCLR", iUCLR)
            }
        }
        if (!iURFT.trim().isEmpty()) {
            if (iURFT.trim().equals("?")) {
                lockedResult.set("EXURFT", "")
            } else {
                lockedResult.set("EXURFT", iURFT)
            }
        }
        if (!iURFN.trim().isEmpty()) {
            if (iURFN.trim().equals("?")) {
                lockedResult.set("EXURFN", "")
            } else {
                lockedResult.set("EXURFN", iURFN)
            }
        }
        if (!iUDSG.trim().isEmpty()) {
            if (iUDSG.trim().equals("?")) {
                lockedResult.set("EXUDSG", "")
            } else {
                lockedResult.set("EXUDSG", iUDSG)
            }
        }
        if (!iUDGR.trim().isEmpty()) {
            if (iUDGR.trim().equals("?")) {
                lockedResult.set("EXUDGR", "")
            } else {
                lockedResult.set("EXUDGR", iUDGR)
            }
        }
        if (!iUCLC.trim().isEmpty()) {
            if (iUCLC.trim().equals("?")) {
                lockedResult.set("EXUCLC", "")
            } else {
                lockedResult.set("EXUCLC", iUCLC)
            }
        }
        if (!iUCLS.trim().isEmpty()) {
            if (iUCLS.trim().equals("?")) {
                lockedResult.set("EXUCLS", "")
            } else {
                lockedResult.set("EXUCLS", iUCLS)
            }
        }
        
        if (mi.inData.get("SHQT")!=null&&!mi.inData.get("SHQT").trim().isEmpty()) {
            lockedResult.set("EXSHQT", iSHQT)
        }
        if (mi.inData.get("ORQT")!=null&&!mi.inData.get("ORQT").trim().isEmpty()) {
            lockedResult.set("EXORQT", iORQT)
        }
        if (mi.inData.get("BOQT")!=null&&!mi.inData.get("BOQT").trim().isEmpty()) {
            lockedResult.set("EXBOQT", iBOQT)
        }
        if (mi.inData.get("UNPR")!=null&&!mi.inData.get("UNPR").trim().isEmpty()) {
            lockedResult.set("EXUNPR", iUNPR)
        }
        if (mi.inData.get("UNCS")!=null&&!mi.inData.get("UNCS").trim().isEmpty()) {
            lockedResult.set("EXUNCS", iUNCS)
        }

        lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
        lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
        lockedResult.set("EXCHID", program.getUser())
        lockedResult.update()
    }
}