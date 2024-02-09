/**
 * README
 * This extension is being used to Delete records from EXTSOH table. 
 *
 * Name: EXT100MI.DelSalesOrdHead
 * Description: Deleting records from EXTSOH table
 * Date	      Changed By                      Description
 *20230216  SuriyaN@fortude.co    Deleting records from EXTSOH table
 *20240208  AbhishekA@fortude.co  Updating Validation logic
 */

public class DelSalesOrdHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO
  private String iORNO
  private boolean validInput = true

  public DelSalesOrdHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    if (validInput) {
      deleteRecord()
    }
  }

  /**
   *Validate inputs
   * @params int CONO ,String ORNO,String CUNO,String CUOR,String WHLO
   * @return void
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

    //Validate Order Number
    Map < String, String > paramsORNO = ["ORNO": ORNO.toString().trim()]
    Closure < ? > callbackORNO = {
      Map < String,
      String > response ->
      if (response.ORNO == null) {
        mi.error("Invalid Order Number " + ORNO)
        validInput = false
        return
      }
    }
    miCaller.call("OIS100MI", "GetOrderHead", paramsORNO, callbackORNO)

    //Validate Lines Record
    params = ["CONO": CONO.toString().trim(), "ORNO": ORNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.ORNO != null) {
        mi.error("Lines exists for the order " + ORNO + ". Please delete lines before deleting header.")
        validInput = false
        return
      }
    }
    miCaller.call("EXT100MI", "LstSalesOrdLine", params, callback)
  }
  /**
   *Delete records from EXTSOH table
   * @params 
   * @return 
   */
  public void deleteRecord() {
    DBAction query = database.table("EXTSOH").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXORNO", iORNO)
    container.set("EXCONO", iCONO)
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