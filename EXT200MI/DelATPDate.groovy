/**
 * README
 * This extension is being used to Delete records from EXTPDT table. 
 *
 * Name: EXT200MI.DelATPDate
 * Description: Deleting records from EXTPDT table
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co    Deleting records from EXTPDT table
 *
 **/

public class DelATPDate extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO
  private boolean validInput = true
  private String iWHLO, iITNO
  
  public DelATPDate(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    
    validateInput()
    if (validInput) {
    deleteRecord()
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
   *Delete records from EXTPDT table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTPDT").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXWHLO", iWHLO)
    container.set("EXITNO", iITNO)
    container.set("EXCONO", iCONO)
    if (!query.readLock(container, deleteCallBack)) {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > deleteCallBack = {
    LockedResult lockedResult ->
    lockedResult.delete()
  }
}