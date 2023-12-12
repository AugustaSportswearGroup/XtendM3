/**
 * README
 * This extension is being used to List records from EXTCCT table. 
 *
 * Name: EXT413MI.LstCrdtCardTrns
 * Description: Listing records to EXTCCT table
 * Date	      Changed By                      Description
 *20230912  SuriyaN@fortude.co      Listing records from  EXTCCT table
 *
 */

public class LstCrdtCardTrns extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private long iDLIX
  private int iCONO, pageSize = 100000
  private boolean validInput = true

  public LstCrdtCardTrns(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
      listRecord()
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
    
    if(mi.inData.get("DLIX") != null && !mi.inData.get("DLIX").trim().isEmpty())
    {
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

  }


  /**
   *List records from EXTCCT table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTCCT").selection("EXDLST", "EXCONO", "EXDLIX", "EXDLSP").index("00").build()
    DBContainer container = query.getContainer()

    if (mi.inData.get("DLIX") != null && !mi.inData.get("DLIX").trim().isEmpty()) {
      container.set("EXDLIX", iDLIX as long)
      container.set("EXCONO", iCONO)
      query.readAll(container, 2,pageSize, resultset)
    } else {
      container.set("EXCONO", iCONO)
      query.readAll(container, 1,pageSize, resultset)
    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("DLST", container.get("EXDLST").toString())
    mi.outData.put("CONO", container.get("EXCONO").toString())
    mi.outData.put("DLIX", container.get("EXDLIX").toString())
    mi.outData.put("DLSP", container.get("EXDLSP").toString())

    mi.write()

  }
}