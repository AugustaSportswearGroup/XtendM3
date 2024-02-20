/**
 * README
 * This extension is being used to Del records from EXTNSX table. 
 *
 * Name: EXT450MI.DelLabelFile
 * Description: Delete records from EXTNSX table
 * Date	      Changed By                      Description
 *23-12-21    AbhishekA@fortude.co         Delete records from EXTNSX table
 *
 */

public class DelLabelFile extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iTYPE, iPUNO, iSUDO, iSSCC
  private int iCONO, pageSize = 10000
  private boolean validInput = true

  public DelLabelFile(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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

      if ((!iTYPE.toString().trim().isEmpty() && !iSUDO.toString().trim().isEmpty() &&
          !iPUNO.toString().trim().isEmpty() && !iSSCC.toString().trim().isEmpty())) {
        deleteRecords00()
      } else {
        deleteRecords01()
      }
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

    //Validate Purchase Order Number
    if (!iPUNO.toString().trim().isEmpty()) {
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

  }

  /**
   *Delete records from EXTNSX00 table
   * @params 
   * @return 
   */

  public void deleteRecords00() {
    DBAction query = database.table("EXTNSX").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXTYPE", iTYPE)
    container.set("EXPUNO", iPUNO)
    container.set("EXSUDO", iSUDO)
    container.set("EXSSCC", iSSCC)
    if (!query.readLock(container, deleteCallBack00)) {
      mi.error("Record does not Exist.")
      return
    }

  }
  Closure < ? > deleteCallBack00 = {
    LockedResult lockedResult ->
    lockedResult.delete()
  }

  /**
   *Delete records from EXTNSX01 table
   * @params 
   * @return 
   */

  public deleteRecords01() {

    DBAction query = database.table("EXTNSX").selection("EXPUNO", "EXSSCC").index("01").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXTYPE", "POST")
    container.set("EXSUDO", iSUDO)
    Closure < ? > deleteCallBack01 = {
      DBContainer containerResults ->

      String puno = containerResults.get("EXPUNO").toString()
      String sscc = containerResults.get("EXSSCC").toString()

      DBAction query_adllFields = database.table("EXTNSX").index("00").build()
      DBContainer container_allFields = query.getContainer()
      container_allFields.set("EXCONO", iCONO)
      container_allFields.set("EXTYPE", "POST")
      container_allFields.set("EXSUDO", iSUDO)
      container_allFields.set("EXPUNO", puno)
      container_allFields.set("EXSSCC", sscc)
      query_adllFields.readLock(container_allFields, deleteCallBack00)
    }

    query.readAll(container, 3, pageSize, deleteCallBack01)

  }

}