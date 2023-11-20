/**
 * README
 * This extension is being used to Delete records from EXTYPE table. 
 *
 * Name: EXT415MI.DelSwissCarton
 * Description: Deleting records from EXTYPE table
 * Date	      Changed By                      Description
 *20230927  SuriyaN@fortude.co    Deleting records from EXTYPE table
 *
 */

public class DelSwissCarton extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO, iTTYP
  private String iWHLO, iTRTP
  private boolean validInput = true
   
  public DelSwissCarton(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iTTYP = (mi.inData.get("TTYP") == null || mi.inData.get("TTYP").trim().isEmpty()) ? 0 : mi.inData.get("TTYP") as Integer

    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iTRTP = (mi.inData.get("TRTP") == null || mi.inData.get("TRTP").trim().isEmpty()) ? "" : mi.inData.get("TRTP")
    
    validateInput()
    if (validInput) {
      deleteRecord()
    }
    
  }
  
  /**
   *Validate Records
   * @params
   * @return 
   */
  public validateInput() {

    //Validate Company Number
    def params = ["CONO": iCONO.toString().trim()]
    def callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Warehouse Number
    params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim()]
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
    
    //Validate Order Type
    params = ["CONO": iCONO.toString().trim(), "TRTP": iTRTP.toString().trim(),"TTYP": iTTYP.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.TRTP == null) {
        mi.error("Invalid Order Type - Transaction Type")
        validInput = false
        return false
      }
    }
    miCaller.call("CRS200MI", "LstOrderType", params, callback)
  }
  
  
  /**
   *Delete records from EXTYPE table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTYPE").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXWHLO", iWHLO)
    container.set("EXTRTP", iTRTP)
    container.set("EXCONO", iCONO)
    container.set("EXTTYP", iTTYP)
    if (query.read(container)) {
      query.readLock(container, deleteCallBack)
    } else {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > deleteCallBack = {
    LockedResult lockedResult ->
    lockedResult.delete()
  }
}