/**
 * README
 * This extension is being used to List records from EXTSOH table. 
 *
 * Name: EXT100MI.LstSalesOrdHead
 * Description: Listing records to EXTSOH table
 * Date	      Changed By                      Description
 *20230210  SuriyaN@fortude.co      Listing records from  EXTSOH table
 *20240208  AbhishekA@fortude.co    Updating Validation logic
 *
 */




public class LstSalesOrdHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iORNO,iSRDT,iUPM3,indexValue = "00"

  private int iCONO,iOFST =0,count = 1, maxPageSize = 10000
  private boolean validInput = true

  public LstSalesOrdHead(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iOFST = (mi.inData.get("OFST") == null || mi.inData.get("OFST").trim().isEmpty()) ? 0 : mi.inData.get("OFST") as Integer
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iSRDT = (mi.inData.get("SRDT") == null || mi.inData.get("SRDT").trim().isEmpty()) ? "des" : mi.inData.get("SRDT")
    iUPM3 = (mi.inData.get("UPM3") == null || mi.inData.get("UPM3").trim().isEmpty()) ? "" : mi.inData.get("UPM3")

    validateInput(iCONO,iORNO)
    if(validInput)
    {
      listRecord()
    }

  }

  /**
   *Validate inputs
   * @params int CONO ,String ORNO
   * @return boolean
   */
  private void validateInput(int CONO,String ORNO) {
    //Validate Company Number
    Map<String, String> params = ["CONO": CONO.toString().trim()]
    Closure<?> callback = {
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
    if (!iORNO.toString().trim().isEmpty()) {
    params = ["ORNO": iORNO.toString().trim()]
    callback = {
      Map<String,
        String> response ->
        if (response.ORNO == null) {
          mi.error("Invalid Order Number " + iORNO)
          validInput = false
          return
        }
    }
    miCaller.call("OIS100MI", "GetOrderHead", params, callback)
    }

  }

  /**
   *List records from EXTSOH table
   * @params
   * @return
   */
  public void listRecord() {

    if(!iORNO.trim().isEmpty())
    {
      indexValue = "00"
    }else if(iSRDT.trim().equals("asc")&&!iUPM3.trim().isEmpty())
    {
      indexValue = "02"
    }
    else if(iSRDT.trim().equals("asc"))
    {
      indexValue = "01"
    }
    else if(!iUPM3.trim().isEmpty())
    {
      indexValue = "03"
    }

    DBAction query = database.table("EXTSOH").selection("EXORTP", "EXSTAT", "EXCUNO", "EXBTNM", "EXBTA1", "EXBTA2", "EXBTTO", "EXBTEC","EXM3CH", "EXBTPO", "EXBTCS", "EXSTNM", "EXSTA1", "EXSTA2", "EXSTTO", "EXSTEC", "EXSTPO", "EXSTCS", "EXSHVI", "EXSHWT", "EXCUOR", "EXFOBC", "EXWHLO", "EXTXID", "EXTEDL", "EXFAXT", "EXEMAL", "EXRSAD", "EXUFED", "EXUUPS", "EXUCUS", "EXUPHO", "EXUCSR", "EXURUS", "EXUPM3", "EXORNO", "EXORDT", "EXSHED", "EXTXAM", "EXNTAM", "EXSTAM", "EXFRAM").index(indexValue).build()
    DBContainer container = query.getContainer()

    if(!iORNO.trim().isEmpty())
    {
      container.set("EXCONO", iCONO)
      container.set("EXORNO", iORNO)
      query.readAll(container, 2,maxPageSize, resultset)
    }
    else if(iSRDT.trim().equals("asc")&&!iUPM3.trim().isEmpty())
    {
      container.set("EXCONO", iCONO)
      container.set("EXUPM3", iUPM3)
      query.readAll(container, 2,maxPageSize, resultset)
    } 
    else
    {
      container.set("EXCONO", iCONO)
      query.readAll(container, 1,maxPageSize, resultset)
    }



  }
  Closure<?> resultset = {
    DBContainer container ->

      if(count>iOFST)
      {
        mi.outData.put("ORTP", container.get("EXORTP").toString())
        mi.outData.put("STAT", container.get("EXSTAT").toString())
        mi.outData.put("CUNO", container.get("EXCUNO").toString())
        mi.outData.put("BTNM", container.get("EXBTNM").toString())
        mi.outData.put("BTA1", container.get("EXBTA1").toString())
        mi.outData.put("BTA2", container.get("EXBTA2").toString())
        mi.outData.put("BTTO", container.get("EXBTTO").toString())
        mi.outData.put("BTEC", container.get("EXBTEC").toString())
        mi.outData.put("BTPO", container.get("EXBTPO").toString())
        mi.outData.put("BTCS", container.get("EXBTCS").toString())
        mi.outData.put("STNM", container.get("EXSTNM").toString())
        mi.outData.put("STA1", container.get("EXSTA1").toString())
        mi.outData.put("STA2", container.get("EXSTA2").toString())
        mi.outData.put("STTO", container.get("EXSTTO").toString())
        mi.outData.put("STEC", container.get("EXSTEC").toString())
        mi.outData.put("STPO", container.get("EXSTPO").toString())
        mi.outData.put("STCS", container.get("EXSTCS").toString())
        mi.outData.put("SHVI", container.get("EXSHVI").toString())
        mi.outData.put("SHWT", container.get("EXSHWT").toString())
        mi.outData.put("CUOR", container.get("EXCUOR").toString())
        mi.outData.put("FOBC", container.get("EXFOBC").toString())
        mi.outData.put("WHLO", container.get("EXWHLO").toString())
        mi.outData.put("TXID", container.get("EXTXID").toString())
        mi.outData.put("TEDL", container.get("EXTEDL").toString())
        mi.outData.put("FAXT", container.get("EXFAXT").toString())
        mi.outData.put("EMAL", container.get("EXEMAL").toString())
        mi.outData.put("RSAD", container.get("EXRSAD").toString())
        mi.outData.put("UFED", container.get("EXUFED").toString())
        mi.outData.put("UUPS", container.get("EXUUPS").toString())
        mi.outData.put("UCUS", container.get("EXUCUS").toString())
        mi.outData.put("UPHO", container.get("EXUPHO").toString())
        mi.outData.put("UCSR", container.get("EXUCSR").toString())
        mi.outData.put("URUS", container.get("EXURUS").toString())
        mi.outData.put("UPM3", container.get("EXUPM3").toString())
        mi.outData.put("ORNO", container.get("EXORNO").toString())
        mi.outData.put("ORDT", container.get("EXORDT").toString())
        mi.outData.put("SHED", container.get("EXSHED").toString())
        mi.outData.put("TXAM", container.get("EXTXAM").toString())
        mi.outData.put("NTAM", container.get("EXNTAM").toString())
        mi.outData.put("STAM", container.get("EXSTAM").toString())
        mi.outData.put("FRAM", container.get("EXFRAM").toString())
        mi.outData.put("M3CH", container.get("EXM3CH").toString())
        mi.write()
      }
      count++

  }
}
