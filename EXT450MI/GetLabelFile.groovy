/**
 * README
 * This extension is being used to get records from EXTNSX table. 
 *
 * Name: EXT450MI.GetLabelFile
 * Description: Get records from EXTNSX table
 * Date	      Changed By                      Description
 *23-12-21    AbhishekA@fortude.co         Get records from EXTNSX table
 *
 */

public class GetLabelFile extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iTYPE, iPUNO, iSUDO, iSSCC
  private int iCONO
  private boolean validInput = true

  public GetLabelFile(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iTYPE = (mi.inData.get("TYPE") == null || mi.inData.get("TYPE").trim().isEmpty()) ? "" : mi.inData.get("TYPE")
    iPUNO = (mi.inData.get("PUNO") == null || mi.inData.get("PUNO").trim().isEmpty()) ? "" : mi.inData.get("PUNO")
    iSUDO = (mi.inData.get("SUDO") == null || mi.inData.get("SUDO").trim().isEmpty()) ? "" : mi.inData.get("SUDO")
    iSSCC = (mi.inData.get("SSCC") == null || mi.inData.get("SSCC").trim().isEmpty()) ? "" : mi.inData.get("SSCC")

    validateInput()
    if (validInput) {
      getRecords()
    }
  }

  /**
   *Validate records 
   * @params 
   * @return 
   */
  public void validateInput() {
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

    //Validate Type
    if (iTYPE.toString().trim() == null || iTYPE.toString().trim().isEmpty()) {
      mi.error("Type must be entered")
      validInput = false
      return
    }

    //Validate Purchase Order Number

    params = ["CONO": iCONO.toString().trim(), "PUNO": iPUNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->

      if (response.PUNO == null) {
        mi.error("Invalid Purchase Order Number " + iPUNO)
        validInput = false
        return
      }
    }
    miCaller.call("PPS001MI", "GetHeadBasic", params, callback)
    
  }

  /**
   *Get record from EXTNSX table
   * @params 
   * @return 
   */
  private void getRecords() {
    DBAction query = database.table("EXTNSX").selection("EXTYPE", "EXPUNO", "EXSUDO", "EXSSCC", "EXHDPR", "EXOPTY", "EXTX30", "EXOPTX", "EXDELV", "EXITDS", "EXABFC", "EXWHLO", "EXLNQT").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXTYPE", iTYPE)
    container.set("EXPUNO", iPUNO)
    container.set("EXSUDO", iSUDO)
    container.set("EXSSCC", iSSCC)
    if (query.read(container)) {

      mi.outData.put("TYPE", container.get("EXTYPE").toString())
      mi.outData.put("PUNO", container.get("EXPUNO").toString())
      mi.outData.put("SUDO", container.get("EXSUDO").toString())
      mi.outData.put("SSCC", container.get("EXSSCC").toString())
      mi.outData.put("HDPR", container.get("EXHDPR").toString())
      mi.outData.put("OPTY", container.get("EXOPTY").toString())
      mi.outData.put("TX30", container.get("EXTX30").toString())
      mi.outData.put("OPTX", container.get("EXOPTX").toString())
      mi.outData.put("DELV", container.get("EXDELV").toString())
      mi.outData.put("ITDS", container.get("EXITDS").toString())
      mi.outData.put("ABFC", container.get("EXABFC").toString())
      mi.outData.put("WHLO", container.get("EXWHLO").toString())
      mi.outData.put("LNQT", container.get("EXLNQT").toString())
      mi.write()
    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}