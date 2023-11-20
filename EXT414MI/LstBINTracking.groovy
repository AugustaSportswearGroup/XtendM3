/**
 * README
 * This extension is being used to List records from EXTNBN table. 
 *
 * Name: EXT414MI.LstBINTracking
 * Description: Listing records to EXTNBN table
 * Date	      Changed By                      Description
 *20230922  SuriyaN@fortude.co      Listing records from  EXTNBN table
 *
 */

public class LstBINTracking extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iRCID, iLICP, iORNO
  private int iCONO, iTTYP
  private boolean validInput = true
  private long iDLIX

  public LstBINTracking(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iRCID = (mi.inData.get("RCID") == null || mi.inData.get("RCID").trim().isEmpty()) ? "" : mi.inData.get("RCID")
    iLICP = (mi.inData.get("LICP") == null || mi.inData.get("LICP").trim().isEmpty()) ? "" : mi.inData.get("LICP")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iTTYP = (mi.inData.get("TTYP") == null || mi.inData.get("TTYP").trim().isEmpty()) ? 0 : mi.inData.get("TTYP") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    
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
    
    if(!iORNO.trim().isEmpty())
    {
    //Validate Order Number
    params = ["CONO": iCONO.toString().trim(), "ORNO": iORNO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.ORNO == null) {
        validInput = false
      }
    }
    miCaller.call("OIS100MI", "GetHead", params, callback)
    } 
    
    if(mi.inData.get("DLIX") != null && !mi.inData.get("DLIX").trim().isEmpty())
    {
    //Validate Delivery Number
    params = ["CONO": iCONO.toString().trim(), "DLIX": iDLIX.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.DLIX == null) {
        mi.error("Invalid Delivery Number " + iDLIX)
        validInput = false
      }
    }
    miCaller.call("MWS410MI", "GetHead", params, callback)
    }
    
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
   *List records from EXTNBN table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTNBN").selection("EXDIVI", "EXWHLO", "EXFACI", "EXRCID", "EXLICP", "EXORNO", "EXSTAT", "EXCUNO", "EXMODL", "EXTEPY", "EXTEPA", "EXWGFG", "EXQAFG", "EXSMFG", "EXONFM", "EXLMFS", "EXLMFD", "EXPKDV", "EXPSDV", "EXPTDV", "EXLNDV", "EXFD01", "EXFD02", "EXUD01", "EXUD02", "EXCONO", "EXCISN", "EXTTYP", "EXDLIX", "EXBXNO", "EXESTW", "EXACTW", "EXWGTO", "EXUD03").index("00").build()
    DBContainer container = query.getContainer()
    
    if(!iWHLO.trim().isEmpty()&&!iRCID.trim().isEmpty()&&!iLICP.trim().isEmpty()&&!iORNO.trim().isEmpty()&&(mi.inData.get("TTYP") != null && !mi.inData.get("TTYP").trim().isEmpty())&&(mi.inData.get("DLIX") != null && !mi.inData.get("DLIX").trim().isEmpty()))
    {
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXRCID", iRCID)
    container.set("EXLICP", iLICP)
    container.set("EXORNO", iORNO)
    container.set("EXCONO", iCONO)
    container.set("EXTTYP", iTTYP)
    container.set("EXDLIX", iDLIX)
    query.readAll(container, 8, resultset)
    }
    else if(!iWHLO.trim().isEmpty()&&!iRCID.trim().isEmpty()&&!iLICP.trim().isEmpty()&&!iORNO.trim().isEmpty()&&(mi.inData.get("TTYP") != null && !mi.inData.get("TTYP").trim().isEmpty()))
    {
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXRCID", iRCID)
    container.set("EXLICP", iLICP)
    container.set("EXORNO", iORNO)
    container.set("EXCONO", iCONO)
    container.set("EXTTYP", iTTYP)
    query.readAll(container, 7, resultset)
    }
    else if(!iWHLO.trim().isEmpty()&&!iRCID.trim().isEmpty()&&!iLICP.trim().isEmpty()&&(mi.inData.get("TTYP") != null && !mi.inData.get("TTYP").trim().isEmpty()))
    {
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXRCID", iRCID)
    container.set("EXLICP", iLICP)
    container.set("EXTTYP", iTTYP)
    container.set("EXCONO", iCONO)
    query.readAll(container, 6, resultset)
    }
    else if(!iWHLO.trim().isEmpty()&&!iRCID.trim().isEmpty()&&!iLICP.trim().isEmpty())
    {
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXRCID", iRCID)
    container.set("EXLICP", iLICP)
    container.set("EXCONO", iCONO)
    query.readAll(container, 5, resultset)
    }
    else if(!iWHLO.trim().isEmpty()&&!iRCID.trim().isEmpty())
    {
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXRCID", iRCID)
    container.set("EXCONO", iCONO)
    query.readAll(container,4, resultset)
    }
    else if(!iWHLO.trim().isEmpty())
    {
    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXCONO", iCONO)
    query.readAll(container,3, resultset)
    }
    else
    {
    container.set("EXDIVI", iDIVI)
    container.set("EXCONO", iCONO)
    query.readAll(container,2, resultset)
    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("DIVI", container.get("EXDIVI").toString())
    mi.outData.put("WHLO", container.get("EXWHLO").toString())
    mi.outData.put("FACI", container.get("EXFACI").toString())
    mi.outData.put("RCID", container.get("EXRCID").toString())
    mi.outData.put("LICP", container.get("EXLICP").toString())
    mi.outData.put("ORNO", container.get("EXORNO").toString())
    mi.outData.put("STAT", container.get("EXSTAT").toString())
    mi.outData.put("CUNO", container.get("EXCUNO").toString())
    mi.outData.put("MODL", container.get("EXMODL").toString())
    mi.outData.put("TEPY", container.get("EXTEPY").toString())
    mi.outData.put("TEPA", container.get("EXTEPA").toString())
    mi.outData.put("WGFG", container.get("EXWGFG").toString())
    mi.outData.put("QAFG", container.get("EXQAFG").toString())
    mi.outData.put("SMFG", container.get("EXSMFG").toString())
    mi.outData.put("ONFM", container.get("EXONFM").toString())
    mi.outData.put("LMFS", container.get("EXLMFS").toString())
    mi.outData.put("LMFD", container.get("EXLMFD").toString())
    mi.outData.put("PKDV", container.get("EXPKDV").toString())
    mi.outData.put("PSDV", container.get("EXPSDV").toString())
    mi.outData.put("PTDV", container.get("EXPTDV").toString())
    mi.outData.put("LNDV", container.get("EXLNDV").toString())
    mi.outData.put("FD01", container.get("EXFD01").toString())
    mi.outData.put("FD02", container.get("EXFD02").toString())
    mi.outData.put("UD01", container.get("EXUD01").toString())
    mi.outData.put("UD02", container.get("EXUD02").toString())
    mi.outData.put("CONO", container.get("EXCONO").toString())
    mi.outData.put("CISN", container.get("EXCISN").toString())
    mi.outData.put("TTYP", container.get("EXTTYP").toString())
    mi.outData.put("DLIX", container.get("EXDLIX").toString())
    mi.outData.put("BXNO", container.get("EXBXNO").toString())
    mi.outData.put("ESTW", container.get("EXESTW").toString())
    mi.outData.put("ACTW", container.get("EXACTW").toString())
    mi.outData.put("WGTO", container.get("EXWGTO").toString())
    mi.outData.put("UD03", container.get("EXUD03").toString())

    mi.write()

  }
}