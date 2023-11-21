/**
 * README
 * This extension is being used to Get records from EXTCUS table. 
 *
 * Name: EXT610MI.GetCustomerData
 * Description: Get records from EXTCUS table
 * Date	      Changed By                      Description
 *20230519  SuriyaN@fortude.co     Get records from EXTCUS table
 *
 */

public class GetCustomerData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iCUNO
  private int iCONO
  private boolean validInput = true

  public GetCustomerData(MIAPI mi, DatabaseAPI database, ProgramAPI program,MICallerAPI miCaller) {
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
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    validateInput()
    if(validInput)
      {
        getRecord()
      }
    
  }
  
   /**
     *Validate records 
     * @params 
     * @return 
     */
    public validateInput() {
      
    //Validate Company Number
    def params = ["CONO": iCONO.toString().trim()]
    def callback = {
      Map < String,
        String > response ->
        if (response.CONO == null) {
          mi.error("Invalid Company Number " + iCONO)
          validInput=false
          return false
        }
    }
    miCaller.call("MNS095MI", "Get", params, callback)
    
     //Validate Customer Number
    params = ["CUNO": iCUNO.toString().trim()]
    callback = {
      Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + iCUNO)
          validInput=false
          return false
        }
    }
    miCaller.call("CRS610MI", "GetBasicData", params, callback)
    
    
    }
    
  /**
   *Get records from EXTCUS table
   * @params 
   * @return 
   */
  public getRecord() {
    DBAction query = database.table("EXTCUS").selection("EXCUNO", "EXCUNM", "EXADR1", "EXADR2", "EXADR3", "EXTOWN", "EXECAR", "EXPONO", "EXCSCD", "EXPHNO", "EXTFNO", "EXEMAL", "EXTEPY", "EXTXID", "EXULZO", "EXPRLV", "EXBLCD","EXM3RG","EXM3LM").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXCUNO", iCUNO)
    if (query.read(container)) {
      mi.outData.put("CUNO", container.get("EXCUNO").toString())
      mi.outData.put("CUNM", container.get("EXCUNM").toString())
      mi.outData.put("ADR1", container.get("EXADR1").toString())
      mi.outData.put("ADR2", container.get("EXADR2").toString())
      mi.outData.put("ADR3", container.get("EXADR3").toString())
      mi.outData.put("TOWN", container.get("EXTOWN").toString())
      mi.outData.put("ECAR", container.get("EXECAR").toString())
      mi.outData.put("PONO", container.get("EXPONO").toString())
      mi.outData.put("CSCD", container.get("EXCSCD").toString())
      mi.outData.put("PHNO", container.get("EXPHNO").toString())
      mi.outData.put("TFNO", container.get("EXTFNO").toString())
      mi.outData.put("EMAL", container.get("EXEMAL").toString())
      mi.outData.put("TEPY", container.get("EXTEPY").toString())
      mi.outData.put("TXID", container.get("EXTXID").toString())
      mi.outData.put("ULZO", container.get("EXULZO").toString())
      mi.outData.put("PRLV", container.get("EXPRLV").toString())
      mi.outData.put("BLCD", container.get("EXBLCD").toString())
      mi.outData.put("M3RG", container.get("EXM3RG").toString())
      mi.outData.put("M3LM", container.get("EXM3LM").toString())

      mi.write()

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}