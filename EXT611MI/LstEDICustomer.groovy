/**
 * README
 * This extension is being used to List records from EXTCCF table. 
 *
 * Name: EXT611MI.LstEDICustomer
 * Description: Listing records to EXTCCF table
 * Date	      Changed By                      Description
 *20230913  SuriyaN@fortude.co      Listing records from  EXTCCF table
 *
 */

public class LstEDICustomer extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iPYCU
  private int iCONO,pageSize = 10000
  private boolean validInput = true

  public LstEDICustomer(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iPYCU = (mi.inData.get("PYCU") == null || mi.inData.get("PYCU").trim().isEmpty()) ? "" : mi.inData.get("PYCU")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    
    validateInput()
    if (validInput) {
      listRecord()
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
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Customer Number
    params = ["CUNO": iPYCU.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.CUNO == null) {
        mi.error("Invalid Customer Numebr Number " + iPYCU)
        validInput = false
        return false
      }
    }
    miCaller.call("CRS610MI", "GetBasicData", params, callback)
    
     //Validate Warehouse Number
      params = ["CONO": iCONO.toString().trim(),"WHLO":iWHLO.toString().trim()]
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


  /**
   *List records from EXTCCF table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTCCF").selection("EXDIVI", "EXWHLO", "EXPYCU", "EXSPOC", "EXSTAT", "EXHPDD", "EXHIVD", "EXHORD", "EXDPDD", "EXLPDD", "EXDIVD", "EXLIVD", "EXDORD", "EXPORD", "EXLORD", "EXDOSD", "EXLOSD", "EXDAND", "EXLAND", "EXDACD", "EXLACD", "EXDSCD", "EXLSCD", "EXDROD", "EXPROD", "EXLROD", "EXDIVC", "EXLIVC", "EXDOAK", "EXLOAK", "EXDEX2", "EXLEX2", "EXDEX3", "EXLEX3", "EXDEX4", "EXLEX4", "EXUD01", "EXUD02", "EXSTNM", "EXSDFL", "EXCONO", "EXUD03").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXPYCU", iPYCU)
    container.set("EXCONO", iCONO)
    query.readAll(container, 4,pageSize, resultset)
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("DIVI", container.get("EXDIVI").toString())
    mi.outData.put("WHLO", container.get("EXWHLO").toString())
    mi.outData.put("PYCU", container.get("EXPYCU").toString())
    mi.outData.put("SPOC", container.get("EXSPOC").toString())
    mi.outData.put("STAT", container.get("EXSTAT").toString())
    mi.outData.put("HPDD", container.get("EXHPDD").toString())
    mi.outData.put("HIVD", container.get("EXHIVD").toString())
    mi.outData.put("HORD", container.get("EXHORD").toString())
    mi.outData.put("DPDD", container.get("EXDPDD").toString())
    mi.outData.put("LPDD", container.get("EXLPDD").toString())
    mi.outData.put("DIVD", container.get("EXDIVD").toString())
    mi.outData.put("LIVD", container.get("EXLIVD").toString())
    mi.outData.put("DORD", container.get("EXDORD").toString())
    mi.outData.put("PORD", container.get("EXPORD").toString())
    mi.outData.put("LORD", container.get("EXLORD").toString())
    mi.outData.put("DOSD", container.get("EXDOSD").toString())
    mi.outData.put("LOSD", container.get("EXLOSD").toString())
    mi.outData.put("DAND", container.get("EXDAND").toString())
    mi.outData.put("LAND", container.get("EXLAND").toString())
    mi.outData.put("DACD", container.get("EXDACD").toString())
    mi.outData.put("LACD", container.get("EXLACD").toString())
    mi.outData.put("DSCD", container.get("EXDSCD").toString())
    mi.outData.put("LSCD", container.get("EXLSCD").toString())
    mi.outData.put("DROD", container.get("EXDROD").toString())
    mi.outData.put("PROD", container.get("EXPROD").toString())
    mi.outData.put("LROD", container.get("EXLROD").toString())
    mi.outData.put("DIVC", container.get("EXDIVC").toString())
    mi.outData.put("LIVC", container.get("EXLIVC").toString())
    mi.outData.put("DOAK", container.get("EXDOAK").toString())
    mi.outData.put("LOAK", container.get("EXLOAK").toString())
    mi.outData.put("DEX2", container.get("EXDEX2").toString())
    mi.outData.put("LEX2", container.get("EXLEX2").toString())
    mi.outData.put("DEX3", container.get("EXDEX3").toString())
    mi.outData.put("LEX3", container.get("EXLEX3").toString())
    mi.outData.put("DEX4", container.get("EXDEX4").toString())
    mi.outData.put("LEX4", container.get("EXLEX4").toString())
    mi.outData.put("UD01", container.get("EXUD01").toString())
    mi.outData.put("UD02", container.get("EXUD02").toString())
    mi.outData.put("STNM", container.get("EXSTNM").toString())
    mi.outData.put("SDFL", container.get("EXSDFL").toString())
    mi.outData.put("CONO", container.get("EXCONO").toString())
    mi.outData.put("UD03", container.get("EXUD03").toString())

    mi.write()

  }
}