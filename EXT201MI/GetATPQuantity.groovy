/**
 * README
 * This extension is being used to Get records from EXTPQT table. 
 *
 * Name: EXT201MI.GetATPQuantity
 * Description: Get records from EXTPQT table
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co     Get records from EXTPQT table
 *
 */


public class GetATPQuantity extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private String iWHLO, iITNO
  private int iCONO

  public GetATPQuantity(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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
   *Get records from EXTPQT table
   * @params 
   * @return 
   */
  public getRecord() {
    DBAction query = database.table("EXTPQT").selection("EXWHLO", "EXITNO", "EXUPDS","EXVAL9").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXWHLO", iWHLO)
    container.set("EXITNO", iITNO)
    if (query.read(container)) {
      mi.outData.put("WHLO", container.get("EXWHLO").toString())
      mi.outData.put("ITNO", container.get("EXITNO").toString())
      mi.outData.put("UPDS", container.get("EXUPDS").toString())
      mi.outData.put("VAL9", container.get("EXVAL9").toString())
      mi.write()

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}