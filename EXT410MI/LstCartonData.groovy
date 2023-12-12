/**
 * README
 * This extension is being used to List records from EXTXFL table. 
 *
 * Name: EXT410MI.LstCartonData
 * Description: Listing records to EXTXFL table
 * Date	      Changed By                      Description
 *20230510  SuriyaN@fortude.co      Listing records from  EXTXFL table
 *
 */


public class LstCartonData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private String iDIVI, iWHLO, iORNO, iRCID, iRLPN, iITNO

  private int iCONO, iTRNO,pageSize = 10000
  private long iDLIX

  public LstCartonData(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
    this.mi = mi
    this.database = database
    this.program = program
  }
  /**
   ** Main function
   * @param
   * @return
   */
  public void main() {
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iRCID = (mi.inData.get("RCID") == null || mi.inData.get("RCID").trim().isEmpty()) ? "" : mi.inData.get("RCID")
    iRLPN = (mi.inData.get("RLPN") == null || mi.inData.get("RLPN").trim().isEmpty()) ? "" : mi.inData.get("RLPN")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iDLIX = (mi.inData.get("DLIX") == null || mi.inData.get("DLIX").trim().isEmpty()) ? 0 : mi.inData.get("DLIX") as Long
    iTRNO = (mi.inData.get("TRNO") == null || mi.inData.get("TRNO").trim().isEmpty()) ? 0 : mi.inData.get("TRNO") as Integer

    listRecord()
  }
  /**
   *List records from EXTXFL table
   * @params 
   * @return 
   */
  public listRecord() {
    DBAction query = database.table("EXTXFL").selection("EXDIVI", "EXWHLO", "EXORNO", "EXRCID","EXPONR", "EXRLPN", "EXITNO", "EXBXNO", "EXUDF0", "EXUDF1", "EXUDF2", "EXUDF3", "EXUDF4", "EXUDF5", "EXUDF6", "EXUDF7", "EXUDF8", "EXUDF9", "EXCONO", "EXDLIX", "EXTRNO", "EXQUAN", "EXQWGT", "EXTWGT","EXQUAN","EXQWGT","EXTWGT","EXSTAT").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXDIVI", iDIVI)
    container.set("EXWHLO", iWHLO)
    container.set("EXORNO", iORNO)
    container.set("EXRCID", iRCID)
    container.set("EXRLPN", iRLPN)
    container.set("EXITNO", iITNO)
    container.set("EXCONO", iCONO)
    container.set("EXDLIX", iDLIX)
    container.set("EXTRNO", iTRNO)
    
    if(iCONO!=0&&iDIVI!=0&&iDLIX!=0&&iTRNO!=0&&!iWHLO.trim().isEmpty()&&!iORNO.trim().isEmpty()&&!iRCID.trim().isEmpty()&&!iRLPN.trim().isEmpty()&&!iITNO.trim().isEmpty())
    {
      query.readAll(container, 9,pageSize, resultset)
    }
    else if(iCONO!=0&&iDIVI!=0&&iDLIX!=0&&!iWHLO.trim().isEmpty()&&!iORNO.trim().isEmpty()&&!iRCID.trim().isEmpty()&&!iRLPN.trim().isEmpty()&&!iITNO.trim().isEmpty())
    {
      query.readAll(container, 8,pageSize, resultset)
    }
    else if(iCONO!=0&&iDIVI!=0&&iDLIX!=0&&!iWHLO.trim().isEmpty()&&!iORNO.trim().isEmpty()&&!iRCID.trim().isEmpty()&&!iRLPN.trim().isEmpty())
    {
      query.readAll(container, 7,pageSize, resultset)
    }
    else if(iCONO!=0&&iDIVI!=0&&iDLIX!=0&&!iWHLO.trim().isEmpty()&&!iORNO.trim().isEmpty()&&!iRCID.trim().isEmpty())
    {
      query.readAll(container, 6,pageSize, resultset)
    }
    else if(iCONO!=0&&iDIVI!=0&&iDLIX!=0&&!iWHLO.trim().isEmpty()&&!iORNO.trim().isEmpty())
    {
      query.readAll(container, 5,pageSize, resultset)
    }
    else if(iCONO!=0&&iDIVI!=0&&!iWHLO.trim().isEmpty()&&!iORNO.trim().isEmpty())
    {
      query.readAll(container, 4,pageSize, resultset)
    }
     else if(iCONO!=0&&iDIVI!=0&&!iWHLO.trim().isEmpty())
    {
      query.readAll(container, 3,pageSize, resultset)
    } else if(iCONO!=0&&iDIVI!=0)
    {
      query.readAll(container, 2,pageSize, resultset)
    }
    
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("DIVI", container.get("EXDIVI").toString())
    mi.outData.put("WHLO", container.get("EXWHLO").toString())
    mi.outData.put("ORNO", container.get("EXORNO").toString())
    mi.outData.put("RCID", container.get("EXRCID").toString())
    mi.outData.put("RLPN", container.get("EXRLPN").toString())
    mi.outData.put("ITNO", container.get("EXITNO").toString())
    mi.outData.put("BXNO", container.get("EXBXNO").toString())
    mi.outData.put("UDF0", container.get("EXUDF0").toString())
    mi.outData.put("UDF1", container.get("EXUDF1").toString())
    mi.outData.put("UDF2", container.get("EXUDF2").toString())
    mi.outData.put("UDF3", container.get("EXUDF3").toString())
    mi.outData.put("UDF4", container.get("EXUDF4").toString())
    mi.outData.put("UDF5", container.get("EXUDF5").toString())
    mi.outData.put("UDF6", container.get("EXUDF6").toString())
    mi.outData.put("UDF7", container.get("EXUDF7").toString())
    mi.outData.put("UDF8", container.get("EXUDF8").toString())
    mi.outData.put("UDF9", container.get("EXUDF9").toString())
    mi.outData.put("CONO", container.get("EXCONO").toString())
    mi.outData.put("DLIX", container.get("EXDLIX").toString())
    mi.outData.put("TRNO", container.get("EXTRNO").toString())
    mi.outData.put("QUAN", container.get("EXQUAN").toString())
    mi.outData.put("QWGT", container.get("EXQWGT").toString())
    mi.outData.put("TWGT", container.get("EXTWGT").toString())
    mi.outData.put("STAT", container.get("EXSTAT").toString())
    mi.outData.put("PONR", container.get("EXPONR").toString())
    mi.write()

  }
}