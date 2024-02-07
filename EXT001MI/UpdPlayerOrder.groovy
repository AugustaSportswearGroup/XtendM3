/**
 * README
 * This extension is being used to update order number in EXTSTH and EXTSTL table
 *
 * Name: EXT001MI.UpdPlayerOrder
 * Description: Update Order number in EXTSTH and EXTSTL tables
 * Date	      Changed By                      Description
 *20230403  SuriyaN@fortude.co     Update Order number in EXTSTH and EXTSTL tables */

public class UpdPlayerOrder extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final LoggerAPI logger
  
  private String iDIVI, iORNR, iORNO
  private int iCONO
  private boolean validInput = true

  public UpdPlayerOrder(MIAPI mi, DatabaseAPI database, ProgramAPI program,MICallerAPI miCaller,LoggerAPI logger) {
    this.mi = mi
    this.database = database
    this.program = program
    this.miCaller = miCaller
    this.logger = logger
  }
  /**
   ** Main function
   * @param
   * @return
   */
  public void main() {
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    validateInput()
    if(validInput)
    {
      listPlayerHeaderDetails()
    }
  }

  /**
   *Validate Records
   * @params
   * @return
   */
  public void validateInput() {

    //Validate Company Number
    Map < String, String > params = ["CONO": iCONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
        String > response ->
        if (response.CONO == null) {
          mi.error("Invalid Company Number " + iCONO)
          validInput = false
          return 
        }
    }
    miCaller.call("MNS095MI", "Get", params, callback)
    
    //Validate Division
    params = ["CONO": iCONO.toString().trim(), "DIVI": iDIVI.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.DIVI == null) {
        mi.error("Invalid Division " + iDIVI)
        validInput = false
        return
      }
    }

    miCaller.call("MNS100MI", "GetBasicData", params, callback)

    //Validate Temporary Order Number
    DBAction query = database.table("OXHEAD").index("00").build()
    DBContainer container = query.getContainer()
    container.set("OACONO", iCONO)
    container.set("OAORNO", iORNR)

    if(!query.read(container))
    {
      mi.error("Temporary Order Number not found "+iORNR)
      validInput = false
      return 
    }


    //Validate Final order Number
    params = ["ORNO":iORNO.toString().trim()]
    callback = {
      Map < String,
        String > response ->
        if (response.ORNO == null) {
          mi.error("Invalid Order Number " + iORNO)
          validInput = false
          return 
        }
    }
    miCaller.call("OIS100MI", "GetOrderHead", params, callback)
    

    
  }

  /**
   *List records from EXTSTH table
   * @params
   * @return
   */
  public void listPlayerHeaderDetails() {
    DBAction query = database.table("EXTSTH").index("10").selection("EXPLID").build()
    DBContainer container = query.getContainer()
    container.set("EXORNR", iORNR)
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    query.readAll(container, 3, resultset)

  }

  Closure < ? > resultset = {
    DBContainer container ->
      String PLID = container.get("EXPLID")
      Map < String, String >  params = ["CONO": iCONO.toString().trim(),"DIVI":iDIVI.toString().trim(),"PLID":PLID.toString().trim(),"ORNR":iORNR.toString().trim(),"ORNO":iORNO.toString().trim()]
      Closure < ? > callback = {
        Map < String,
          String > response ->
          if(response.errorMessage!=null)
          {
            mi.error(response.errorMessage)
          }
      }
      
      miCaller.call("EXT001MI", "UpdPlayerHead", params,callback)
      listLineDetails(PLID)
  }

  /**
   *List records to EXTSTL table
   * @params String PLID
   * @return
   */
  public void listLineDetails(String PLID) {
    DBAction query = database.table("EXTSTL").index("00").selection("EXCONO","EXDIVI","EXPLID","EXORNR","EXITNO","EXPONR").build()
    DBContainer container = query.getContainer()
    container.set("EXPLID", PLID)
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    query.readAll(container, 3, resultset_LstLineDetails)
  }

  Closure < ? > resultset_LstLineDetails = {
    DBContainer container ->
      String CONO = container.get("EXCONO")
      String DIVI = container.get("EXDIVI")
      String PLID = container.get("EXPLID")
      String ORNR = container.get("EXORNR")
      String PONR = container.get("EXPONR")
      String ITNO = container.get("EXITNO")
      Map < String, String > params = ["CONO": CONO,"DIVI":DIVI,"ORNR":ORNR,
                "PLID":PLID,"ORNO":iORNO,"PONR":PONR,"ITNO":ITNO]
      Closure < ? > callback = {
        Map < String,
          String > response ->
          if(response.errorMessage!=null)
          {
            mi.error("API ERROR: "+response.errorMessage)
          }
      }
      miCaller.call("EXT001MI", "UpdPlayerLine", params,callback)
  }
}
