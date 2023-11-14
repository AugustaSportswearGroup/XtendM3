/**
 * README
 * This extension is being used to Delete records from EXTHPD table. 
 *
 * Name: EXT412MI.DeleteDelTracking
 * Description: Deleting records from EXTHPD table
 * Date       Changed By                      Description
 *20230906  SuriyaN@fortude.co    Deleting records from EXTHPD table
 *
 */

public class DltDelTracking extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private int iCONO
  private long iDLIX
  private String iSHDT, iCUOR, iORNO
  private validInput = true 
  
  public DltDelTracking(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller, UtilityAPI utility) {
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
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long

    iSHDT = (mi.inData.get("SHDT") == null || mi.inData.get("SHDT").trim().isEmpty()) ? "" : mi.inData.get("SHDT")
    iCUOR = (mi.inData.get("CUOR") == null || mi.inData.get("CUOR").trim().isEmpty()) ? "" : mi.inData.get("CUOR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")

    validateInput()
    if(validInput)
    {
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
          validInput=false
          return false
        }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

     
    
      //Validate Order Number
      params = ["CONO": iCONO.toString().trim(), "ORNO": iORNO.toString().trim()]
      callback = {
        Map < String,
          String > response ->
          if (response.ORNO == null) {
              mi.error("Invalid Order Number " + iORNO)
            validInput=false
            return false
          }
      }
    
      miCaller.call("OIS100MI", "GetHead", params, callback)
    
    
    
      //Validate Delivery Number
      params = ["CONO": iCONO.toString().trim(),"DLIX":iDLIX.toString().trim()]
      callback = {
        Map < String,
          String > response ->
          if (response.DLIX == null) {
            mi.error("Invalid Delivery Number " + iDLIX)
            validInput = false
            return false
          }
      }
      miCaller.call("MWS410MI", "GetHead", params, callback)
    
    
     //Validate SHDT
    String sourceFormat = utility.call("DateUtil", "getFullFormat", mi.getDateFormat())
    validInput = utility.call("DateUtil", "isDateValid", iSHDT.trim(), sourceFormat.toString())
    if (!validInput) {
      mi.error("Order Date not in " + sourceFormat + " format. Please Check " + iSHDT)
      return false
    } else {
      if (!sourceFormat.trim().equals("yyyyMMdd")) {
        iSHDT = utility.call("DateUtil", "convertDate", sourceFormat, "yyyyMMdd", iSHDT.trim()) //Maintain date in YMD8 format in the table
      }
    }

  }
  

  /**
   *Delete records from EXTHPD table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTHPD").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXSHDT", iSHDT)
    container.set("EXCUOR", iCUOR)
    container.set("EXORNO", iORNO)
    container.set("EXCONO", iCONO)
    container.set("EXDLIX", iDLIX)
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