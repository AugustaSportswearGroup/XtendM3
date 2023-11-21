/**
 * README
 * This extension is being used to Get records from EXTPDT table. 
 *
 * Name: EXT200MI.GetATPDate
 * Description: Get records from EXTPDT table
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co     Get records from EXTPDT table
 *
 */



public class GetATPDate extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private String iWHLO, iITNO
  private int iCONO

  public GetATPDate(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    getRecord()
  }
  /**
   *Get records from EXTPDT table
   * @params 
   * @return 
   */
  public getRecord() {
    DBAction query = database.table("EXTPDT").selection("EXWHLO", "EXITNO", "EXDATE", "EXUPDS").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXWHLO", iWHLO)
    container.set("EXITNO", iITNO)
    if (query.read(container)) {
      mi.outData.put("WHLO", container.get("EXWHLO").toString())
      mi.outData.put("ITNO", container.get("EXITNO").toString())
      mi.outData.put("DATE", container.get("EXDATE").toString())
      mi.outData.put("UPDS", container.get("EXUPDS").toString())
      mi.outData.put("ORQ9", container.get("EXORQ9").toString())

      mi.write()

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}