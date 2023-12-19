/**
 * README
 * This extension is being used to List records from EXTRPH table. 
 *
 * Name: EXT145MI.LstCustGrpHead
 * Description: Listing records to EXTRPH table
 * Date	      Changed By                      Description
 *20231011  SuriyaN@fortude.co      Listing records from  EXTRPH table
 *
 */

public class LstCustGrpHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO, iGPID, pageSize = 10000
  private boolean validInput = true

  public LstCustGrpHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iGPID = (mi.inData.get("GPID") == null || mi.inData.get("GPID").trim().isEmpty()) ? 0 : mi.inData.get("GPID") as Integer

    validateInput()
    if (validInput) {
      listRecord()
    }

  }

  /**
   *Validate records 
   * @params 
   * @return 
   */
  public validateInput() {

    //Validate Company Number
    Map < String, String > params = ["CONO": iCONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate GPID
    if (iGPID.toString().trim() == null || iGPID.toString().trim().isEmpty()) {
      mi.error("Group ID must be entered")
      validInput = false
      return
    }

  }
  /**
   *List records from EXTRPH table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTRPH").selection("EXGPNM", "EXGOWN", "EXDESC", "EXCIN1", "EXCIN2", "EXCONO", "EXGPID").index("00").build()
    DBContainer container = query.getContainer()

    if (iGPID != 0) {
      container.set("EXCONO", iCONO)
      container.set("EXGPID", iGPID)
      query.readAll(container, 2, pageSize, resultset)
    } else {
      container.set("EXCONO", iCONO)
      query.readAll(container, 1, pageSize, resultset)

    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("GPNM", container.get("EXGPNM").toString())
    mi.outData.put("GOWN", container.get("EXGOWN").toString())
    mi.outData.put("DESC", container.get("EXDESC").toString())
    mi.outData.put("CIN1", container.get("EXCIN1").toString())
    mi.outData.put("CIN2", container.get("EXCIN2").toString())
    mi.outData.put("CONO", container.get("EXCONO").toString())
    mi.outData.put("GPID", container.get("EXGPID").toString())

    mi.write()

  }
}