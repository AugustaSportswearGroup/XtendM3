/**
 * README
 * This extension is being used to List records from EXTRPL table. 
 *
 * Name: EXT146MI.LstCustGrpLine
 * Description: Listing records to EXTRPL table
 * Date	      Changed By                      Description
 *20231011  SuriyaN@fortude.co      Listing records from  EXTRPL table
 *
 */

public class LstCustGrpLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iCUNO
  private int iCONO, iGPID, pageSize = 10000
  private boolean validInput = true

  public LstCustGrpLine(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
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

    //Validate CUNO
    if (iCUNO.toString().trim() == null || iCUNO.toString().trim().isEmpty()) {
      mi.error("Customer Number must be entered")
      validInput = false
      return
    }

    if (iCUNO.trim().isEmpty()) {
      //Validate Customer Number
      params = ["CUNO": iCUNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + iCUNO)
          validInput = false
          return
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }
  }

  /**
   *List records from EXTRPL table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTRPL").selection("EXCUNO", "EXGLBL", "EXCIN1", "EXCIN2", "EXCONO", "EXGPID").index("00").build()
    DBContainer container = query.getContainer()

    if (iGPID != 0 && !iCUNO.trim().isEmpty()) {
      container.set("EXCUNO", iCUNO)
      container.set("EXCONO", iCONO)
      container.set("EXGPID", iGPID)
      if (query.read(container)) {
        mi.outData.put("CUNO", container.get("EXCUNO").toString())
        mi.outData.put("GLBL", container.get("EXGLBL").toString())
        mi.outData.put("CIN1", container.get("EXCIN1").toString())
        mi.outData.put("CIN2", container.get("EXCIN2").toString())
        mi.outData.put("CONO", container.get("EXCONO").toString())
        mi.outData.put("GPID", container.get("EXGPID").toString())

        mi.write()
      }
    } else if (iGPID != 0) {
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
    mi.outData.put("CUNO", container.get("EXCUNO").toString())
    mi.outData.put("GLBL", container.get("EXGLBL").toString())
    mi.outData.put("CIN1", container.get("EXCIN1").toString())
    mi.outData.put("CIN2", container.get("EXCIN2").toString())
    mi.outData.put("CONO", container.get("EXCONO").toString())
    mi.outData.put("GPID", container.get("EXGPID").toString())

    mi.write()

  }
}