/**
 * README
 * This extension is being used to Delete records to EXTSOL table. 
 *
 * Name: EXT100MI.DelSalesOrdLine
 * Description: Deleting records to EXTSOL table
 * Date	            Changed By                      Description
 *20230210        SuriyaN@fortude.co      Deleting records to EXTSOL table
 *20240208        AbhishekA@fortude.co    Updating Validation logic
 */



public class DelSalesOrdLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iORNO, iLNKY
  private int iCONO, iPONR
  private boolean validInput = true

  public DelSalesOrdLine(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iLNKY = (mi.inData.get("LNKY") == null || mi.inData.get("LNKY").trim().isEmpty()) ? "" : mi.inData.get("LNKY")
    iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    validateInput(iCONO, iORNO)
    if (validInput) {
      deleteRecord()
    }

  }
  /**
   *Validate inputs
   * @params int CONO ,String ORNO,String LNKY
   * @return boolean
   */
  private void validateInput(int CONO, String ORNO) {

    //Validate Company Number
    Map < String, String > params = ["CONO": CONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + CONO)
        validInput = false
        return
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Header Record
    params = ["CONO": CONO.toString().trim(), "ORNO": ORNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.ORNO == null) {
        mi.error("Header Record doesn't exist for the order " + ORNO)
        validInput = false
        return 
      }
    }
    miCaller.call("EXT100MI", "GetSalesOrdHead", params, callback)

  }

  /**
   *Delete records to EXTSOL table
   * @params 
   * @return 
   */
  public void deleteRecord() {
    DBAction query = database.table("EXTSOL").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXORNO", iORNO)
    container.set("EXLNKY", iLNKY)
    container.set("EXPONR", iPONR)
    if (!query.readLock(container, deleteCallBack)) {
      mi.error("Record does not Exist.")
      return
    }

  }

  Closure < ? > deleteCallBack = {
    LockedResult lockedResult ->
    lockedResult.delete()
  }
}