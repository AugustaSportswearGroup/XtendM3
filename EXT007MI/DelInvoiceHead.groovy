/**
 * README
 * This extension is being used to Delete records from EXTIHH table. 
 *
 * Name: EXT007MI.DelInvoiceHead
 * Description: Deleting records from EXTIHH table
 * Date	      Changed By                      Description
 *20230210  SuriyaN@fortude.co    Deleting records from EXTIHH table
 *
 */

public class DelInvoiceHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private int iCONO, iIVNO
  private boolean validInput = true
  public DelInvoiceHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iIVNO = (mi.inData.get("IVNO") == null || mi.inData.get("IVNO").trim().isEmpty()) ? 0 : mi.inData.get("IVNO") as Integer

    validateInput(iCONO, iIVNO)
    if (validInput) {
      deleteRecord()
    }

  }

  /**
   *Validate inputs
   * @params int CONO ,String IVNO
   * @return void
   */
  private validateInput(int CONO, int IVNO) {
    String m3CUOR = ""

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

    //Validate Invoice Number
    if (IVNO == 0) {
      mi.error("Invoice Number must be entered")
      validInput = false
      return
    }

    //Validate Lines Record
    params = ["CONO": CONO.toString().trim(), "IVNO": IVNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.IVNO != null) {
        mi.error("Lines exists for the invoice " + IVNO + ". Please delete lines before deleting header.")
        validInput = false
        return
      }
    }
    miCaller.call("EXT007MI", "LstInvoiceLines", params, callback)

  }

  /**
   *Delete records from EXTIHH table
   * @params 
   * @return 
   */
  public deleteRecord() {
    DBAction query = database.table("EXTIHH").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXIVNO", iIVNO)
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