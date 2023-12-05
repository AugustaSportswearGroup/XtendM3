/**
 * README
 * This extension is being used to Update records to EXTWHL table. 
 *
 * Name: EXT005MI.UpdItmWarehouse
 * Description: Updating records to EXTWHL table
 * Date       Changed By                      Description
 *                                     Updating records to EXTWHL table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


public class UpdItmWarehouse extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller
    private final UtilityAPI utility

    private String iITNO, iWHLO, iWHSL, iM3RG, iM3LM
    private int iCONO
    private double iSTQT, iORQA, iORQT, iORBO
    private boolean validInput =true
    
    public UpdItmWarehouse(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller,UtilityAPI utility) {
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
        iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
        iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
        iWHSL = (mi.inData.get("WHSL") == null || mi.inData.get("WHSL").trim().isEmpty()) ? "" : mi.inData.get("WHSL")
        iM3RG = (mi.inData.get("M3RG") == null || mi.inData.get("M3RG").trim().isEmpty()) ? "0" : mi.inData.get("M3RG")
        iM3LM = (mi.inData.get("M3LM") == null || mi.inData.get("M3LM").trim().isEmpty()) ? "0" : mi.inData.get("M3LM")
        iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
        iSTQT = (mi.inData.get("STQT") == null || mi.inData.get("STQT").trim().isEmpty()) ? 0 : mi.inData.get("STQT") as Double
        iORQA = (mi.inData.get("ORQA") == null || mi.inData.get("ORQA").trim().isEmpty()) ? 0 : mi.inData.get("ORQA") as Double
        iORQT = (mi.inData.get("ORQT") == null || mi.inData.get("ORQT").trim().isEmpty()) ? 0 : mi.inData.get("ORQT") as Double
        iORBO = (mi.inData.get("ORBO") == null || mi.inData.get("ORBO").trim().isEmpty()) ? 0 : mi.inData.get("ORBO") as Double


        validateInput(iCONO,iITNO,iWHLO,iWHSL)
        if(validInput)
        {
          updateRecord()
        }
        
    }
    
    /**
     *Validate inputs
     * @params int CONO ,String ITNO,String WHLO,String iWHSL
     * @return void
     */
    private validateInput(int CONO,String ITNO,String WHLO,String WHSL) {
      
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
        
        if(!WHSL.trim().isEmpty())
        {
        //Validate Location
        Map<String, String> paramsWHSL = ["CONO": CONO.toString().trim(), "WHSL": WHSL.toString().trim(), "WHLO": WHLO.toString().trim()]
        Closure<?> callbackWHSL = {
            Map < String,
            String > response ->
            if (response.WHSL == null) {
                mi.error("Invalid Location " + WHSL)
                validInput = false
                return
            }
        }
        miCaller.call("MMS010MI", "GetLocation", paramsWHSL, callbackWHSL)
        }
        
    //Validate M3 Created Date
    if (!iM3RG.trim().equals("0")) {
      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", iM3RG.trim(), sourceFormat.toString())
      if (!validInput) {
        mi.error("M3 Created Date not in " + sourceFormat + " format. Please Check " + iM3RG)
        validInput=false
        return 
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iM3RG = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", iM3RG.trim()) //Maintain date in YMD8 format in the table
        }
      }
    }

    //Validate M3 Modified Date
    if (!iM3LM.trim().equals("0")) {
      String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
      validInput = utility.call("DateUtil", "isDateValid", iM3LM.trim(), sourceFormat.toString())
      if (!validInput) {
        mi.error("M3 Modified Date not in " + sourceFormat + " format. Please Check " + iM3LM)
        validInput=false
        return 
      } else {
        if (!sourceFormat.trim().equals("yyyyMMdd")) {
          iM3LM = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", iM3LM.trim()) //Maintain date in YMD8 format in the table
        }
      }
    }
    }
    
    /**
     *Update records to EXTWHL table
     * @params 
     * @return 
     */
    public updateRecord() {
        DBAction query = database.table("EXTWHL").index("00").build()
        DBContainer container = query.getContainer()

        container.set("EXCONO", iCONO)
        container.set("EXITNO", iITNO)
        container.set("EXWHLO", iWHLO)
        if (!query.readLock(container, updateCallBack)) {
        mi.error("Record does not Exist.")
        return
      }
    }

    Closure<?> updateCallBack = {
        LockedResult lockedResult ->
        int CHNO = lockedResult.get("EXCHNO")
        if (!iWHSL.trim().isEmpty()) {
            if (iWHSL.trim().equals("?")) {
                lockedResult.set("EXWHSL", "")
            } else {
                lockedResult.set("EXWHSL", iWHSL)
            }
        }
        
 
        if (mi.inData.get("STQT")!=null&&!mi.inData.get("STQT").trim().isEmpty()) {
            lockedResult.set("EXSTQT", iSTQT)
        }
        if (mi.inData.get("ORQA")!=null&&!mi.inData.get("ORQA").trim().isEmpty()) {
            lockedResult.set("EXORQA", iORQA)
        }
        if (mi.inData.get("ORQT")!=null&&!mi.inData.get("ORQT").trim().isEmpty()) {
            lockedResult.set("EXORQT", iORQT)
        }
        if (mi.inData.get("ORBO")!=null&&!mi.inData.get("ORBO").trim().isEmpty()) {
            lockedResult.set("EXORBO", iORBO)
        }
        
        if (!iM3RG.trim().equals("0")) {
            if (iM3RG.trim().equals("?")) {
                lockedResult.set("EXM3RG", "")
            } else {
                lockedResult.set("EXM3RG", iM3RG as Integer)
            }
        }
         if (!iM3LM.trim().equals("0")) {
            if (iM3LM.trim().equals("?")) {
                lockedResult.set("EXM3LM", "")
            } else {
                lockedResult.set("EXM3LM", iM3LM as Integer)
            }
        }
        lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
        lockedResult.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
        lockedResult.set("EXCHID", program.getUser())
        lockedResult.set("EXCHNO", CHNO+1)
        lockedResult.update()
    }
}