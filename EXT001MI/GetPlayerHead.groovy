/**
 * README
 * This extension is being used to Get records from EXTSTH table. 
 *
 * Name: EXT001MI.GetPlayerHead
 * Description: Get records from EXTSTH table
 * Date	      Changed By                      Description
 *20230403  SuriyaN@fortude.co     Get records from EXTSTH table
 *
 */

public class GetPlayerHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private String iPLID,iDIVI
  private int iCONO

  public GetPlayerHead(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
    this.mi = mi
    this.database = database
    this.program = program
  }
  /**
   ** Main function
   * @param
   * @return
   */
  public void main() {
    iPLID = (mi.inData.get("PLID") == null || mi.inData.get("PLID").trim().isEmpty()) ? "" : mi.inData.get("PLID")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? "" : mi.inData.get("DIVI")
    getRecord()
  }
  /**
   *Get records from EXTSTH table
   * @params
   * @return
   */
  public getRecord() {
    DBAction query = database.table("EXTSTH").selection("EXPLID", "EXORNR", "EXORNO", "EXOREF", "EXPLNM", "EXPLNU", "EXTEAM", "EXLEAG", "EXGRBY", "EXBGBY", "EXCUNO", "EXADID", "EXUDF1", "EXUDF2", "EXUDF3", "EXUDF4", "EXUDF5", "EXUDF6", "EXORDT").index("00").build()
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXPLID", iPLID)
    if (query.read(container)) {
      mi.outData.put("PLID", container.get("EXPLID").toString())
      mi.outData.put("ORNR", container.get("EXORNR").toString())
      mi.outData.put("ORNO", container.get("EXORNO").toString())
      mi.outData.put("OREF", container.get("EXOREF").toString())
      mi.outData.put("PLNM", container.get("EXPLNM").toString())
      mi.outData.put("PLNU", container.get("EXPLNU").toString())
      mi.outData.put("TEAM", container.get("EXTEAM").toString())
      mi.outData.put("LEAG", container.get("EXLEAG").toString())
      mi.outData.put("GRBY", container.get("EXGRBY").toString())
      mi.outData.put("BGBY", container.get("EXBGBY").toString())
      mi.outData.put("CUNO", container.get("EXCUNO").toString())
      mi.outData.put("ADID", container.get("EXADID").toString())
      mi.outData.put("UDF1", container.get("EXUDF1").toString())
      mi.outData.put("UDF2", container.get("EXUDF2").toString())
      mi.outData.put("UDF3", container.get("EXUDF3").toString())
      mi.outData.put("UDF4", container.get("EXUDF4").toString())
      mi.outData.put("UDF5", container.get("EXUDF5").toString())
      mi.outData.put("UDF6", container.get("EXUDF6").toString())
      mi.outData.put("ORDT", container.get("EXORDT").toString())

      mi.write()

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}