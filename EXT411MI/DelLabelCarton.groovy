/**
 * README
 * This extension is being used to Delete records from EXTSLB table. 
 *
 * Name: EXT411MI.DelLabelCarton
 * Description: Deleting records from EXTSLB table
 * Date	      Changed By                      Description
 *20230831  SuriyaN@fortude.co    Deleting records from EXTSLB table
 *
 */

public class DelLabelCarton extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO, iBXNO
  private String iDIVI, iRCID, iCSID
  
  public DelLabelCarton(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iBXNO = (mi.inData.get("BXNO") == null || mi.inData.get("BXNO").trim().isEmpty()) ? 0 : mi.inData.get("BXNO") as Integer

    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iRCID = (mi.inData.get("RCID") == null || mi.inData.get("RCID").trim().isEmpty()) ? "" : mi.inData.get("RCID")
    iCSID = (mi.inData.get("CSID") == null || mi.inData.get("CSID").trim().isEmpty()) ? "" : mi.inData.get("CSID")

    deleteRecord()
  }
  /**
   *Delete records from EXTSLB table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTSLB").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXDIVI", iDIVI)
    container.set("EXRCID", iRCID)
    container.set("EXCSID", iCSID)
    container.set("EXCONO", iCONO)
    container.set("EXBXNO", iBXNO)
    if (query.read(container)) {
      query.readLock(container, deleteCallBack)
    } else {
      mi.error("Record does not Exist.")
      return
    }
  }

  Closure < ? > deleteCallBack = {
    LockedResult lockedResult ->
    lockedResult.delete()
  }
}