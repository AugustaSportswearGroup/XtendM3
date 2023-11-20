/**
 * README
 * This extension is being used to List records from EXTMAS table. 
 *
 * Name: EXT416MI.LstBINMaster
 * Description: Listing records to EXTMAS table
 * Date	      Changed By                      Description
 *20230928  SuriyaN@fortude.co      Listing records from  EXTMAS table
 *
 */

public class LstBINMaster extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iBNNO
  private int iCONO
  private boolean validInput = true

  public LstBINMaster(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iBNNO = (mi.inData.get("BNNO") == null || mi.inData.get("BNNO").trim().isEmpty()) ? "" : mi.inData.get("BNNO")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    validateInput()
    if (validInput) {
      listRecord()
    }
  }
  
  /**
   *Validate Records
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
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)
    
    if(!iWHLO.trim().isEmpty())
    {
    //Validate Warehouse Number
    params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.WHLO == null) {
        mi.error("Invalid Warehouse Number " + iWHLO)
        validInput = false
        return false
      }
    }
    miCaller.call("MMS005MI", "GetWarehouse", params, callback)
    }
  }
  
  /**
   *List records from EXTMAS table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTMAS").selection("EXWHLO", "EXBNNO", "EXTX30", "EXCONO", "EXWEIG", "EXVOL3", "EXVOM3", "EXVOMT", "EXPACL", "EXPACW", "EXPACH").index("00").build()
    DBContainer container = query.getContainer()
    
    if(!iBNNO.trim().isEmpty()&&!iWHLO.trim().isEmpty())
    {
      container.set("EXWHLO", iWHLO)
      container.set("EXBNNO", iBNNO)
      container.set("EXCONO", iCONO)
      query.readAll(container, 3, resultset)
    }else if(!iWHLO.trim().isEmpty())
    {
      container.set("EXWHLO", iWHLO)
      container.set("EXCONO", iCONO)
      query.readAll(container, 2, resultset)
    }else
    {
      container.set("EXCONO", iCONO)
      query.readAll(container, 1, resultset)
    }
      
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("WHLO", container.get("EXWHLO").toString())
    mi.outData.put("BNNO", container.get("EXBNNO").toString())
    mi.outData.put("TX30", container.get("EXTX30").toString())
    mi.outData.put("CONO", container.get("EXCONO").toString())
    mi.outData.put("WEIG", container.get("EXWEIG").toString())
    mi.outData.put("VOL3", container.get("EXVOL3").toString())
    mi.outData.put("VOM3", container.get("EXVOM3").toString())
    mi.outData.put("VOMT", container.get("EXVOMT").toString())
    mi.outData.put("PACL", container.get("EXPACL").toString())
    mi.outData.put("PACW", container.get("EXPACW").toString())
    mi.outData.put("PACH", container.get("EXPACH").toString())

    mi.write()

  }
}