/**
 * README
 * This extension is being used to Delete records from EXTCUS table. 
 *
 * Name: EXT610MI.DelCustomerData
 * Description: Deleting records from EXTCUS table
 * Date	      Changed By                      Description
 *20230519  SuriyaN@fortude.co    Deleting records from EXTCUS table
 *
 */

public class DelCustomerData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO
  private String iCUNO
  private boolean validInput = true
  
  public DelCustomerData(MIAPI mi, DatabaseAPI database, ProgramAPI program,MICallerAPI miCaller) {
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
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
    
    validateInput()
        if(validInput)
        {
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
    def params = ["CONO": iCONO.toString().trim()]
    def callback = {
      Map < String,
        String > response ->
        if (response.CONO == null) {
          mi.error("Invalid Company Number " + iCONO)
          validInput=false
          return false
        }
    }
    miCaller.call("MNS095MI", "Get", params, callback)
    
     //Validate Customer Number
    params = ["CUNO": iCUNO.toString().trim()]
    callback = {
      Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + iCUNO)
          validInput=false
          return false
        }
    }
    miCaller.call("CRS610MI", "GetBasicData", params, callback)
    
    
    }
    
  /**
   *Delete records from EXTCUS table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTCUS").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCUNO", iCUNO)
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