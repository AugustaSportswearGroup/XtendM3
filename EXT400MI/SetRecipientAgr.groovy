/**
 * README
 * This extension is being used to update Recipient Agreement 5 in OOHEAC table. 
 *
 * Name: EXT400MI.UpdRecipientAgr
 * Description: Update Recipient Agreement 5 in OOHEAC table 
 * Date	      Changed By                      Description
 *20230908  SuriyaN@fortude.co        Update Recipient Agreement 5 in OOHEAC table 
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class SetRecipientAgr extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  
  private int iCONO
  private String iORNO,iAGN5
  boolean validInput = true
  
  public SetRecipientAgr(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
    this.mi = mi
    this.database = database
    this.program = program
    this.miCaller = miCaller
  }
  
  public void main() {
    
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iAGN5 = (mi.inData.get("AGN5") == null || mi.inData.get("AGN5").trim().isEmpty()) ? "" : mi.inData.get("AGN5")
    
    validateInput()
    if (validInput) {
      updateRecord()
    }
  }
  
  /**
   *Validate Records
   * @params
   * @return 
   */
  public validateInput() {

    //Validate Company Number
    Map<String, String> params = ["CONO": iCONO.toString().trim()]
    Closure<?> callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return 
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

      //Validate Order Number
      params = ["ORNO": iORNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ORNO == null) {
          validInput = false
          return
        }
      }

    miCaller.call("OIS100MI", "GetOrderHead", params, callback)
   

    if (!iAGN5.trim().isEmpty()) {
      //Validate Customer Number
      params = ["CONO": iCONO.toString().trim(), "CUNO": iAGN5.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Recipient agreement type 5 - commission "+iAGN5+" does not exist")
          validInput = false
          return
        }else
        {
          String cutp = response.CUTP
          String stat = response.STAT
          if(!cutp.trim().equals("0")&&!cutp.trim().equals("1")&&!cutp.trim().equals("2"))
          {
            mi.error("Bonus/commision recipient with customer type "+cutp+" is not permitted")
            validInput = false
            return
          }
          if(!stat.trim().equals("20"))
          {
             mi.error("Customer Status must be 20.")
            validInput = false
            return
          }
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }
  }
  
    /**
   *Update records to OOHEAC table
   * @params 
   * @return 
   */
  public updateRecord() {
    DBAction query = database.table("OOHEAC").index("00").build()
    DBContainer container = query.getContainer()

    container.set("BECONO", iCONO)
    container.set("BEORNO", iORNO)
    if (query.read(container)) {
      query.readLock(container, updateCallBack)
    } else {
      insertRecord()
    }
  }
  
   Closure < ? > updateCallBack = {
    LockedResult lockedResult ->
    int CHNO = lockedResult.get("BECHNO")
    lockedResult.set("BEAGN5", iAGN5)
    lockedResult.set("BELMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("BECHNO", CHNO+1)
    lockedResult.set("BECHID", program.getUser())
    lockedResult.update()
  }
  
  /**
   *Insert records to EXTPQT table
   * @params 
   * @return 
   */
  public insertRecord() {
    DBAction action = database.table("OOHEAC").index("00").build()
    DBContainer query = action.getContainer()

    query.set("BEORNO", iORNO)
    query.set("BEAGN5", iAGN5)
    query.set("BECONO", iCONO as Integer)
    query.set("BERGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("BERGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("BELMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("BECHNO", 0)
    query.set("BECHID", program.getUser())
    action.insert(query)
  }
  
}