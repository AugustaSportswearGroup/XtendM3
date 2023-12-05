/**
 * README
 * This extension is being used to Add records to EXTLOG table. 
 *
 * Name: EXT009MI.AddLogs
 * Description: Adding records to EXTLOG table 
 * Date	      Changed By                      Description
 *20230221  SuriyaN@fortude.co     Adding records to EXTLOG table */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


public class AddLogs extends ExtendM3Transaction {
    private final MIAPI mi
    private final DatabaseAPI database
    private final ProgramAPI program
    private final MICallerAPI miCaller

    private String iDIVI, iTABL, iFLDN, iKVA1, iKVA2, iOVAL, iVALU
    private int iCONO
    private boolean validInput = true

    public AddLogs(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
     *Insert records to EXTLOG table
     * @params 
     * @return 
     */
    public insertRecord() {
        DBAction action = database.table("EXTLOG").index("00").build()
        DBContainer query = action.getContainer()

        query.set("EXDIVI", iDIVI)
        query.set("EXTABL", iTABL)
        query.set("EXFLDN", iFLDN)
        query.set("EXKVA1", iKVA1)
        query.set("EXKVA2", iKVA2)
        query.set("EXOVAL", iOVAL)
        query.set("EXVALU", iVALU)
        query.set("EXCONO", iCONO as Integer)
        query.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
        query.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
        query.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
        query.set("EXLMTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
        query.set("EXCHID", program.getUser())
        query.set("EXCHNO", 0)
        query.set("EXOCHB", program.getUser())
        query.set("EXCHBY", program.getUser())
        query.set("EXCRID", program.getUser())
        action.insert(query, recordExists)
    }

    Closure recordExists = {
        mi.error("Record Already Exists")
        return
    }
}