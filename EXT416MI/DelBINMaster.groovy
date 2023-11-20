/**
 * README
 * This extension is being used to Delete records from EXTMAS table. 
 *
 * Name: EXT416MI.DelBINMaster
 * Description: Deleting records from EXTMAS table
 * Date	      Changed By                      Description
 *20230928  SuriyaN@fortude.co    Deleting records from EXTMAS table
 *
 */

public class DelBINMaster extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO
  private String iWHLO, iBNNO
  private boolean validInput = true
  
  public DelBINMaster(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iBNNO = (mi.inData.get("BNNO") == null || mi.inData.get("BNNO").trim().isEmpty()) ? "" : mi.inData.get("BNNO")

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
    
    
  }
  
  /**
   *Delete records from EXTMAS table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTMAS").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXWHLO", iWHLO)
    container.set("EXBNNO", iBNNO)
    container.set("EXCONO", iCONO)
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