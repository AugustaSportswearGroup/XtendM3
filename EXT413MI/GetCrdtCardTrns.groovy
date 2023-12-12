/**
 * README
 * This extension is being used to Get records from EXTCCT table. 
 *
 * Name: EXT413MI.GetCrdtCardTrns
 * Description: Get records from EXTCCT table
 * Date	      Changed By                      Description
 *20230912  SuriyaN@fortude.co     Get records from EXTCCT table
 *
 */

public class GetCrdtCardTrns extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private long iDLIX
  private int iCONO
  private boolean validInput = true

  public GetCrdtCardTrns(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as long
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    
    validateInput()
    if (validInput) {
       getRecord()
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

    //Validate Delivery Number
    params = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.DLIX == null) {
        mi.error("Invalid Delivery Number " + iDLIX)
        validInput = false
      }
    }
    miCaller.call("MWS410MI", "GetHead", params, callback)

  }


  /**
   *Get records from EXTCCT table
   * @params 
   * @return 
   */
  public getRecord() {
    DBAction query = database.table("EXTCCT").selection("EXDLST", "EXCONO", "EXDLIX", "EXDLSP").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDLIX", iDLIX)
    if (query.read(container)) {
      mi.outData.put("DLST", container.get("EXDLST").toString())
      mi.outData.put("CONO", container.get("EXCONO").toString())
      mi.outData.put("DLIX", container.get("EXDLIX").toString())
      mi.outData.put("DLSP", container.get("EXDLSP").toString())

      mi.write()

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}