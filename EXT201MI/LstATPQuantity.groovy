/**
 * README
 * This extension is being used to List records from EXTPQT table. 
 *
 * Name: EXT201MI.LstATPQuantity
 * Description: Listing records to EXTPQT table
 * Date	      Changed By                      Description
 *20230407  SuriyaN@fortude.co      Listing records from  EXTPQT table
 *
 */


public class LstATPQuantity extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private String iWHLO, iITNO

  private int iCONO, pageSize = 100000

  public LstATPQuantity(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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

    listRecord()
  }
  /**
   *List records from EXTPQT table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTPQT").selection("EXWHLO", "EXITNO", "EXUPDS", "EXCONO", "EXVAL9").index("00").build()
    DBContainer container = query.getContainer()

    if(!iWHLO.trim().isEmpty()&&!iITNO.trim().isEmpty()) {
      container.set("EXCONO", iCONO)
      container.set("EXWHLO", iWHLO)
      container.set("EXITNO", iITNO)
      query.readAll(container, 3,pageSize, resultset)
    }else if (!iWHLO.trim().isEmpty())
    {
      container.set("EXCONO", iCONO)
      container.set("EXWHLO", iWHLO)
      query.readAll(container, 2,pageSize, resultset)
    }else 
    {
      container.set("EXCONO", iCONO)
      query.readAll(container, 1,pageSize, resultset)
    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("WHLO", container.get("EXWHLO").toString())
    mi.outData.put("ITNO", container.get("EXITNO").toString())
    mi.outData.put("UPDS", container.get("EXUPDS").toString())
    mi.outData.put("VAL9", container.get("EXVAL9").toString())

    mi.write()

  }
}