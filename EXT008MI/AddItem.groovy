/**
 * README
 * This extension is being used to Add records to EXTITM table. 
 *
 * Name: EXT008MI.AddItem
 * Description: Adding records to EXTITM table 
 * Date	      Changed By                      Description
 *20230213  SuriyaN@fortude.co     Adding records to EXTITM table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


public class AddItem extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private String iITNO, iITTY, iFUDS, iPRCD, iPRLN, iPRTP, iWHLO, iCTG1, iCTG2, iCTG3, iCTG4, iTXID, iUCLR, iALBO, iINAI, iUBCL, iUCLO, iUCFA, iUCRC, iUCRS, iUEMT, iUEYC, iFPCL, iUPIC, iSWET, iUUCL, iUVIS, iUVAC, iUVIC, iUDCG, iPRTY, iUCLC, iUCLS, iLSGI, iM3IT
    private int iCONO, iSTUC, iSTUP
    private boolean validInput = true

    public AddItem(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
        iPRLN = (mi.inData.get("PRLN") == null || mi.inData.get("PRLN").trim().isEmpty()) ? "" : mi.inData.get("PRLN")
        iPRTP = (mi.inData.get("PRTP") == null || mi.inData.get("PRTP").trim().isEmpty()) ? "" : mi.inData.get("PRTP")
        iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
        iCTG1 = (mi.inData.get("CTG1") == null || mi.inData.get("CTG1").trim().isEmpty()) ? "" : mi.inData.get("CTG1")
        iCTG2 = (mi.inData.get("CTG2") == null || mi.inData.get("CTG2").trim().isEmpty()) ? "" : mi.inData.get("CTG2")
        iCTG3 = (mi.inData.get("CTG3") == null || mi.inData.get("CTG3").trim().isEmpty()) ? "" : mi.inData.get("CTG3")
        iCTG4 = (mi.inData.get("CTG4") == null || mi.inData.get("CTG4").trim().isEmpty()) ? "" : mi.inData.get("CTG4")
        iTXID = (mi.inData.get("TXID") == null || mi.inData.get("TXID").trim().isEmpty()) ? "" : mi.inData.get("TXID")
        iUCLR = (mi.inData.get("UCLR") == null || mi.inData.get("UCLR").trim().isEmpty()) ? "" : mi.inData.get("UCLR")
        iALBO = (mi.inData.get("ALBO") == null || mi.inData.get("ALBO").trim().isEmpty()) ? "" : mi.inData.get("ALBO")
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
        iSTUC = (mi.inData.get("STUC") == null || mi.inData.get("STUC").trim().isEmpty()) ? 0 : mi.inData.get("STUC") as Integer
        iSTUP = (mi.inData.get("STUP") == null || mi.inData.get("STUP").trim().isEmpty()) ? 0 : mi.inData.get("STUP") as Integer

        validateInput(iCONO,iITNO,iWHLO,iITTY)
        if(validInput)
        {
          insertRecord()
        }
    }
    
    /**
     *Validate inputs
     * @params int CONO ,String ITNO,String WHLO,String iTTY
     * @return boolean
     */
    private boolean validateInput(int CONO,String ITNO,String WHLO,String ITTY) {
      
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
        
        //Validate Item Number
        Map<String, String> paramsITNO = ["CONO": CONO.toString().trim(), "ITNO": ITNO.toString().trim()]
        Closure<?> callbackITNO = {
            Map < String,
            String > response ->
            if (response.ITNO == null) {
                mi.error("Invalid Item Number " + ITNO)
                validInput = false
                return 
            }
        }
        miCaller.call("MMS200MI", "Get", paramsITNO, callbackITNO) 
        
        if(!WHLO.trim().isEmpty())
        {
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
        Map<String, String> paramsITTY = ["CONO": CONO.toString().trim(), "ITTY": ITTY.toString().trim()]
        Closure<?> callbackITTY = {
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
      
        
        return true
    }
    
    /**
     *Insert records to EXTITM table
     * @params 
     * @return 
     */
    public insertRecord() {
        DBAction action = database.table("EXTITM").index("00").build()
        DBContainer query = action.getContainer()


        query.set("EXITNO", iITNO)
        query.set("EXITTY", iITTY)
        query.set("EXFUDS", iFUDS)
        query.set("EXPRCD", iPRCD)
        query.set("EXPRLN", iPRLN)
        query.set("EXPRTP", iPRTP)
        query.set("EXWHLO", iWHLO)
        query.set("EXCTG1", iCTG1)
        query.set("EXCTG2", iCTG2)
        query.set("EXCTG3", iCTG3)
        query.set("EXCTG4", iCTG4)
        query.set("EXTXID", iTXID)
        query.set("EXUCLR", iUCLR)
        query.set("EXALBO", iALBO)
        query.set("EXINAI", iINAI)
        query.set("EXUBCL", iUBCL)
        query.set("EXUCLO", iUCLO)
        query.set("EXUCFA", iUCFA)
        query.set("EXUCRC", iUCRC)
        query.set("EXUCRS", iUCRS)
        query.set("EXUEMT", iUEMT)
        query.set("EXUEYC", iUEYC)
        query.set("EXFPCL", iFPCL)
        query.set("EXUPIC", iUPIC)
        query.set("EXSWET", iSWET)
        query.set("EXUUCL", iUUCL)
        query.set("EXUVIS", iUVIS)
        query.set("EXUVAC", iUVAC)
        query.set("EXUVIC", iUVIC)
        query.set("EXUDCG", iUDCG)
        query.set("EXPRTY", iPRTY)
        query.set("EXUCLC", iUCLC)
        query.set("EXUCLS", iUCLS)
        query.set("EXLSGI", iLSGI)
        query.set("EXM3IT", iM3IT)
        query.set("EXCONO", iCONO as Integer)
        query.set("EXSTUC", iSTUC as Integer)
        query.set("EXSTUP", iSTUP as Integer)
        query.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
        query.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
        query.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
        query.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
        query.set("EXCHNO", 0)
        query.set("EXCHID", program.getUser())
        query.set("EXCRID", program.getUser())
        action.insert(query, recordExists)
    }

    Closure recordExists = {
        mi.error("Record Already Exists")
        return
    }
}