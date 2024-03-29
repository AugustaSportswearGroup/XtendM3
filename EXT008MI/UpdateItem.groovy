/**
 * README
 * This extension is being used to Update records to EXTITM table. 
 *
 * Name: EXT008MI.UpdateItem
 * Description: Updating records to EXTITM table
 * Date       Changed By                      Description
 *20230213  SuriyaN@fortude.co    Updating records to EXTITM table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class UpdateItem extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iITNO, iITTY, iFUDS, iPRCD, iPRTP, iWHLO, iCTG1, iCTG2, iCTG3, iCTG4, iUCLR, iINAI, iUBCL, iUCLO, iUCFA, iUCRC, iUCRS, iUEMT, iUEYC, iFPCL, iUPIC, iSWET, iUUCL, iUVIS, iUVAC, iUVIC, iUDCG, iPRTY, iUCLC, iUCLS, iLSGI, iM3IT
  private int iCONO, iPRLN
  private double iTXID, iSTUC, iSTUP, iALBO
  private boolean validInput = true
  public UpdateItem(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iITTY = (mi.inData.get("ITTY") == null || mi.inData.get("ITTY").trim().isEmpty()) ? "" : mi.inData.get("ITTY")
    iFUDS = (mi.inData.get("FUDS") == null || mi.inData.get("FUDS").trim().isEmpty()) ? "" : mi.inData.get("FUDS")
    iPRCD = (mi.inData.get("PRCD") == null || mi.inData.get("PRCD").trim().isEmpty()) ? "" : mi.inData.get("PRCD")
    iPRTP = (mi.inData.get("PRTP") == null || mi.inData.get("PRTP").trim().isEmpty()) ? "" : mi.inData.get("PRTP")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iCTG1 = (mi.inData.get("CTG1") == null || mi.inData.get("CTG1").trim().isEmpty()) ? "" : mi.inData.get("CTG1")
    iCTG2 = (mi.inData.get("CTG2") == null || mi.inData.get("CTG2").trim().isEmpty()) ? "" : mi.inData.get("CTG2")
    iCTG3 = (mi.inData.get("CTG3") == null || mi.inData.get("CTG3").trim().isEmpty()) ? "" : mi.inData.get("CTG3")
    iCTG4 = (mi.inData.get("CTG4") == null || mi.inData.get("CTG4").trim().isEmpty()) ? "" : mi.inData.get("CTG4")
    iUCLR = (mi.inData.get("UCLR") == null || mi.inData.get("UCLR").trim().isEmpty()) ? "" : mi.inData.get("UCLR")
    iINAI = (mi.inData.get("INAI") == null || mi.inData.get("INAI").trim().isEmpty()) ? "" : mi.inData.get("INAI")
    iUBCL = (mi.inData.get("UBCL") == null || mi.inData.get("UBCL").trim().isEmpty()) ? "" : mi.inData.get("UBCL")
    iUCLO = (mi.inData.get("UCLO") == null || mi.inData.get("UCLO").trim().isEmpty()) ? "" : mi.inData.get("UCLO")
    iUCFA = (mi.inData.get("UCFA") == null || mi.inData.get("UCFA").trim().isEmpty()) ? "" : mi.inData.get("UCFA")
    iUCRC = (mi.inData.get("UCRC") == null || mi.inData.get("UCRC").trim().isEmpty()) ? "" : mi.inData.get("UCRC")
    iUCRS = (mi.inData.get("UCRS") == null || mi.inData.get("UCRS").trim().isEmpty()) ? "" : mi.inData.get("UCRS")
    iUEMT = (mi.inData.get("UEMT") == null || mi.inData.get("UEMT").trim().isEmpty()) ? "" : mi.inData.get("UEMT")
    iUEYC = (mi.inData.get("UEYC") == null || mi.inData.get("UEYC").trim().isEmpty()) ? "" : mi.inData.get("UEYC")
    iFPCL = (mi.inData.get("FPCL") == null || mi.inData.get("FPCL").trim().isEmpty()) ? "" : mi.inData.get("FPCL")
    iUPIC = (mi.inData.get("UPIC") == null || mi.inData.get("UPIC").trim().isEmpty()) ? "" : mi.inData.get("UPIC")
    iSWET = (mi.inData.get("SWET") == null || mi.inData.get("SWET").trim().isEmpty()) ? "" : mi.inData.get("SWET")
    iUUCL = (mi.inData.get("UUCL") == null || mi.inData.get("UUCL").trim().isEmpty()) ? "" : mi.inData.get("UUCL")
    iUVIS = (mi.inData.get("UVIS") == null || mi.inData.get("UVIS").trim().isEmpty()) ? "" : mi.inData.get("UVIS")
    iUVAC = (mi.inData.get("UVAC") == null || mi.inData.get("UVAC").trim().isEmpty()) ? "" : mi.inData.get("UVAC")
    iUVIC = (mi.inData.get("UVIC") == null || mi.inData.get("UVIC").trim().isEmpty()) ? "" : mi.inData.get("UVIC")
    iUDCG = (mi.inData.get("UDCG") == null || mi.inData.get("UDCG").trim().isEmpty()) ? "" : mi.inData.get("UDCG")
    iPRTY = (mi.inData.get("PRTY") == null || mi.inData.get("PRTY").trim().isEmpty()) ? "" : mi.inData.get("PRTY")
    iUCLC = (mi.inData.get("UCLC") == null || mi.inData.get("UCLC").trim().isEmpty()) ? "" : mi.inData.get("UCLC")
    iUCLS = (mi.inData.get("UCLS") == null || mi.inData.get("UCLS").trim().isEmpty()) ? "" : mi.inData.get("UCLS")
    iLSGI = (mi.inData.get("LSGI") == null || mi.inData.get("LSGI").trim().isEmpty()) ? "0" : mi.inData.get("LSGI")
    iM3IT = (mi.inData.get("M3IT") == null || mi.inData.get("M3IT").trim().isEmpty()) ? "" : mi.inData.get("M3IT")

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iPRLN = (mi.inData.get("PRLN") == null || mi.inData.get("PRLN").trim().isEmpty()) ? 0 : mi.inData.get("PRLN") as Integer

    iSTUC = (mi.inData.get("STUC") == null || mi.inData.get("STUC").trim().isEmpty()) ? 0 : mi.inData.get("STUC") as Double
    iSTUP = (mi.inData.get("STUP") == null || mi.inData.get("STUP").trim().isEmpty()) ? 0 : mi.inData.get("STUP") as Double
    iALBO = (mi.inData.get("ALBO") == null || mi.inData.get("ALBO").trim().isEmpty()) ? 0 : mi.inData.get("ALBO") as Double
    iTXID = (mi.inData.get("TXID") == null || mi.inData.get("TXID").trim().isEmpty()) ? 0 : mi.inData.get("TXID") as Double

    validateInput(iCONO, iITNO, iWHLO, iITTY)
    if (validInput) {
      updateRecord()
    }

  }

  /**
   *Validate inputs
   * @params int CONO ,String ITNO,String WHLO,String iTTY
   * @return void
   */
  private validateInput(int CONO, String ITNO, String WHLO, String ITTY) {

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

    //Validate Item Number
    Map < String, String > paramsITNO = ["CONO": CONO.toString().trim(), "ITNO": ITNO.toString().trim()]
    Closure < ? > callbackITNO = {
      Map < String,
      String > response ->
      if (response.ITNO == null) {
        mi.error("Invalid Item Number " + ITNO)
        validInput = false
        return
      }
    }
    miCaller.call("MMS200MI", "Get", paramsITNO, callbackITNO)

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

    if (!ITTY.trim().isEmpty()) {
      //Validate Item Type
      Map < String, String > paramsITTY = ["CONO": CONO.toString().trim(), "ITTY": ITTY.toString().trim()]
      Closure < ? > callbackITTY = {
        Map < String,
        String > response ->
        if (response.TX40 == null) {
          mi.error("Invalid Item Type " + ITTY)
          validInput = false
          return
        }
      }
      miCaller.call("CRS040MI", "GetItmType", paramsITTY, callbackITTY)
    }

  }

  /**
   *Update records to EXTITM table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("EXTITM").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXITNO", iITNO)
    if (!query.readLock(container, updateCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("EXCHNO")
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
    if (!iPRCD.trim().isEmpty()) {
      if (iPRCD.trim().equals("?")) {
        lockedResult.set("EXPRCD", "")
      } else {
        lockedResult.set("EXPRCD", iPRCD)
      }
    }

    if (!iPRTP.trim().isEmpty()) {
      if (iPRTP.trim().equals("?")) {
        lockedResult.set("EXPRTP", "")
      } else {
        lockedResult.set("EXPRTP", iPRTP)
      }
    }
    if (!iWHLO.trim().isEmpty()) {
      if (iWHLO.trim().equals("?")) {
        lockedResult.set("EXWHLO", "")
      } else {
        lockedResult.set("EXWHLO", iWHLO)
      }
    }
    if (!iCTG1.trim().isEmpty()) {
      if (iCTG1.trim().equals("?")) {
        lockedResult.set("EXCTG1", "")
      } else {
        lockedResult.set("EXCTG1", iCTG1)
      }
    }
    if (!iCTG2.trim().isEmpty()) {
      if (iCTG2.trim().equals("?")) {
        lockedResult.set("EXCTG2", "")
      } else {
        lockedResult.set("EXCTG2", iCTG2)
      }
    }
    if (!iCTG3.trim().isEmpty()) {
      if (iCTG3.trim().equals("?")) {
        lockedResult.set("EXCTG3", "")
      } else {
        lockedResult.set("EXCTG3", iCTG3)
      }
    }
    if (!iCTG4.trim().isEmpty()) {
      if (iCTG4.trim().equals("?")) {
        lockedResult.set("EXCTG4", "")
      } else {
        lockedResult.set("EXCTG4", iCTG4)
      }
    }

    if (!iUCLR.trim().isEmpty()) {
      if (iUCLR.trim().equals("?")) {
        lockedResult.set("EXUCLR", "")
      } else {
        lockedResult.set("EXUCLR", iUCLR)
      }
    }

    if (!iINAI.trim().isEmpty()) {
      if (iINAI.trim().equals("?")) {
        lockedResult.set("EXINAI", "")
      } else {
        lockedResult.set("EXINAI", iINAI)
      }
    }
    if (!iUBCL.trim().isEmpty()) {
      if (iUBCL.trim().equals("?")) {
        lockedResult.set("EXUBCL", "")
      } else {
        lockedResult.set("EXUBCL", iUBCL)
      }
    }
    if (!iUCLO.trim().isEmpty()) {
      if (iUCLO.trim().equals("?")) {
        lockedResult.set("EXUCLO", "")
      } else {
        lockedResult.set("EXUCLO", iUCLO)
      }
    }
    if (!iUCFA.trim().isEmpty()) {
      if (iUCFA.trim().equals("?")) {
        lockedResult.set("EXUCFA", "")
      } else {
        lockedResult.set("EXUCFA", iUCFA)
      }
    }
    if (!iUCRC.trim().isEmpty()) {
      if (iUCRC.trim().equals("?")) {
        lockedResult.set("EXUCRC", "")
      } else {
        lockedResult.set("EXUCRC", iUCRC)
      }
    }
    if (!iUCRS.trim().isEmpty()) {
      if (iUCRS.trim().equals("?")) {
        lockedResult.set("EXUCRS", "")
      } else {
        lockedResult.set("EXUCRS", iUCRS)
      }
    }
    if (!iUEMT.trim().isEmpty()) {
      if (iUEMT.trim().equals("?")) {
        lockedResult.set("EXUEMT", "")
      } else {
        lockedResult.set("EXUEMT", iUEMT)
      }
    }
    if (!iUEYC.trim().isEmpty()) {
      if (iUEYC.trim().equals("?")) {
        lockedResult.set("EXUEYC", "")
      } else {
        lockedResult.set("EXUEYC", iUEYC)
      }
    }
    if (!iFPCL.trim().isEmpty()) {
      if (iFPCL.trim().equals("?")) {
        lockedResult.set("EXFPCL", "")
      } else {
        lockedResult.set("EXFPCL", iFPCL)
      }
    }
    if (!iUPIC.trim().isEmpty()) {
      if (iUPIC.trim().equals("?")) {
        lockedResult.set("EXUPIC", "")
      } else {
        lockedResult.set("EXUPIC", iUPIC)
      }
    }
    if (!iSWET.trim().isEmpty()) {
      if (iSWET.trim().equals("?")) {
        lockedResult.set("EXSWET", "")
      } else {
        lockedResult.set("EXSWET", iSWET)
      }
    }
    if (!iUUCL.trim().isEmpty()) {
      if (iUUCL.trim().equals("?")) {
        lockedResult.set("EXUUCL", "")
      } else {
        lockedResult.set("EXUUCL", iUUCL)
      }
    }
    if (!iUVIS.trim().isEmpty()) {
      if (iUVIS.trim().equals("?")) {
        lockedResult.set("EXUVIS", "")
      } else {
        lockedResult.set("EXUVIS", iUVIS)
      }
    }
    if (!iUVAC.trim().isEmpty()) {
      if (iUVAC.trim().equals("?")) {
        lockedResult.set("EXUVAC", "")
      } else {
        lockedResult.set("EXUVAC", iUVAC)
      }
    }
    if (!iUVIC.trim().isEmpty()) {
      if (iUVIC.trim().equals("?")) {
        lockedResult.set("EXUVIC", "")
      } else {
        lockedResult.set("EXUVIC", iUVIC)
      }
    }
    if (!iUDCG.trim().isEmpty()) {
      if (iUDCG.trim().equals("?")) {
        lockedResult.set("EXUDCG", "")
      } else {
        lockedResult.set("EXUDCG", iUDCG)
      }
    }
    if (!iPRTY.trim().isEmpty()) {
      if (iPRTY.trim().equals("?")) {
        lockedResult.set("EXPRTY", "")
      } else {
        lockedResult.set("EXPRTY", iPRTY)
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
    if (!iLSGI.trim().isEmpty()) {
      if (iLSGI.trim().equals("?")) {
        lockedResult.set("EXLSGI", "")
      } else {
        lockedResult.set("EXLSGI", iLSGI)
      }
    }
    if (!iM3IT.trim().isEmpty()) {
      if (iM3IT.trim().equals("?")) {
        lockedResult.set("EXM3IT", "")
      } else {
        lockedResult.set("EXM3IT", iM3IT)
      }
    }

    if (mi.inData.get("STUC") != null && !mi.inData.get("STUC").trim().isEmpty()) {
      lockedResult.set("EXSTUC", iSTUC)
    }

    if (mi.inData.get("STUP") != null && !mi.inData.get("STUP").trim().isEmpty()) {
      lockedResult.set("EXSTUP", iSTUP)
    }

    if (mi.inData.get("PRLN") != null && !mi.inData.get("PRLN").trim().isEmpty()) {
      lockedResult.set("EXPRLN", iPRLN)
    }

    if (mi.inData.get("TXID") != null && !mi.inData.get("TXID").trim().isEmpty()) {
      lockedResult.set("EXTXID", iTXID)
    }

    if (mi.inData.get("ALBO") != null && !mi.inData.get("ALBO").trim().isEmpty()) {
      lockedResult.set("EXALBO", iALBO)
    }

    lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    lockedResult.set("EXCHNO", CHNO + 1)
    lockedResult.set("EXCHID", program.getUser())
    lockedResult.update()
  }
}