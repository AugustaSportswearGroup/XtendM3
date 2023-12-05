/**
 * README
 * This extension is being used to Update records to EXTLOG table. 
 *
 * Name: EXT009MI.UpdateLogs
 * Description: Updating records to EXTLOG table
 * Date	      Changed By                      Description
 *20230221  SuriyaN@fortude.co    Updating records to EXTLOG table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


public class UpdateLogs extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller
    

    private String iDIVI, iTABL, iFLDN, iKVA1, iKVA2, iOVAL, iVALU
    private boolean foundRecord = false
    private boolean validInput = true

    private int iCONO, CHNO =1, maxPageSize = 10000
    public UpdateLogs(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
        iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? "" : mi.inData.get("DIVI")
        iTABL = (mi.inData.get("TABL") == null || mi.inData.get("TABL").trim().isEmpty()) ? "" : mi.inData.get("TABL")
        iFLDN = (mi.inData.get("FLDN") == null || mi.inData.get("FLDN").trim().isEmpty()) ? "" : mi.inData.get("FLDN")
        iKVA1 = (mi.inData.get("KVA1") == null || mi.inData.get("KVA1").trim().isEmpty()) ? "" : mi.inData.get("KVA1")
        iKVA2 = (mi.inData.get("KVA2") == null || mi.inData.get("KVA2").trim().isEmpty()) ? "" : mi.inData.get("KVA2")
        iOVAL = (mi.inData.get("OVAL") == null || mi.inData.get("OVAL").trim().isEmpty()) ? "" : mi.inData.get("OVAL")
        iVALU = (mi.inData.get("VALU") == null || mi.inData.get("VALU").trim().isEmpty()) ? "" : mi.inData.get("VALU")
        iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
        
        validateInput()
        if (validInput) {
        updateRecord()
        }
        if (!foundRecord) {
          mi.error("Record does not Exist.")
          return
        }
    }
    
          /**
   *Validate records 
   * @params 
   * @return 
   */
  public validateInput() {
    //Validate Company Number
    Map<String, String>  params = ["CONO": iCONO.toString().trim()]
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
    
    //Validate Division
    params = ["CONO": iCONO.toString().trim(),"DIVI":iDIVI.toString().trim()]
    callback = {
      Map < String,
        String > response ->
        if (response.DIVI == null) {
          mi.error("Invalid Division " + iDIVI)
          validInput = false
          return false
        }
    }
    
    miCaller.call("MNS100MI", "GetBasicData", params, callback)
    
    //Validate Table
    if (iTABL == null || iTABL.trim().isEmpty()) {
      mi.error("Table must be entered")
      validInput = false
      return false
    }
    
    //Validate Field
    if (iFLDN == null || iFLDN.trim().isEmpty()) {
      mi.error("Field must be entered")
      validInput = false
      return false
    }
      
  }
    
    /**
     *Update records to EXTLOG table
     * @params 
     * @return 
     */
    public updateRecord() {
        DBAction query = database.table("EXTLOG").index("10").build()
        DBContainer container = query.getContainer()

        container.set("EXCONO", iCONO)
        container.set("EXDIVI", iDIVI)
        container.set("EXTABL", iTABL)
        container.set("EXFLDN", iFLDN)
        container.set("EXKVA1", iKVA1)
        container.set("EXKVA2", iKVA2)
        
        Closure <?> resultset = {
          DBContainer container1 -> 
          String tabl = container1.get("EXTLOG").toString()
          if (tabl != null) {
            query.readLock(container1, updateCallBack)
          }
        }
        query.readAll(container, 6,maxPageSize, resultset)
    }

    Closure<?> updateCallBack = {
        LockedResult lockedResult ->
        foundRecord = true
        if (!iKVA1.trim().isEmpty()) {
            if (iKVA1.trim().equals("?")) {
                lockedResult.set("EXKVA1", "")
            } else {
                lockedResult.set("EXKVA1", iKVA1)
            }
        }
        if (!iKVA2.trim().isEmpty()) {
            if (iKVA2.trim().equals("?")) {
                lockedResult.set("EXKVA2", "")
            } else {
                lockedResult.set("EXKVA2", iKVA2)
            }
        }
        if (!iOVAL.trim().isEmpty()) {
            if (iOVAL.trim().equals("?")) {
                lockedResult.set("EXOVAL", "")
            } else {
                lockedResult.set("EXOVAL", iOVAL)
            }
        }
        if (!iVALU.trim().isEmpty()) {
            if (iVALU.trim().equals("?")) {
                lockedResult.set("EXVALU", "")
            } else {
                lockedResult.set("EXVALU", iVALU)
            }
        }
        CHNO = lockedResult.get("EXCHNO") as Integer
        CHNO = CHNO + 1
        lockedResult.set("EXCHNO", CHNO)
        lockedResult.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
        lockedResult.set("EXOCHB", program.getUser())
        lockedResult.set("EXCHBY", program.getUser())
        lockedResult.set("EXCRID", program.getUser())
        lockedResult.update()
    }
}