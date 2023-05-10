/**
 * README
 * This extension is being used to List records from EXTSTL table. 
 *
 * Name: EXT001MI.LstPlayerLine
 * Description: Listing records to EXTSTL table
 * Date	      Changed By                      Description
 *20230404  SuriyaN@fortude.co      Listing records from  EXTSTL table
 *
 */

public class LstPlayerLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private String iDIVI, iPLID, iORNR, iITNO

  private int iCONO,iPONR,maxPageSize = 10000

  public LstPlayerLine(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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
    iPLID = (mi.inData.get("PLID") == null || mi.inData.get("PLID").trim().isEmpty()) ? "" : mi.inData.get("PLID")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iITNO = (mi.inData.get("ITNO") == null || mi.inData.get("ITNO").trim().isEmpty()) ? "" : mi.inData.get("ITNO")
    iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer

    listRecord()
  }
  /**
   *List records from EXTSTL table
   * @params
   * @return
   */
  public listRecord() {
    DBAction query = database.table("EXTSTL").selection("EXDIVI", "EXPLID", "EXKLOC","EXORNR", "EXORNO", "EXPONR", "EXPOSX", "EXITNO", "EXORQT", "EXSNUM", "EXWHLO", "EXDLIX", "EXRORN", "EXRORL", "EXRORX", "EXUDF1", "EXUDF2", "EXUDF3", "EXUDF4", "EXUDF5", "EXUDF6").index("00").build()
    DBContainer container = query.getContainer()

    if(!iPLID.trim().isEmpty()&&!iORNR.trim().isEmpty()&&!iITNO.trim().isEmpty()&&iPONR!=0)
    {
      container.set("EXDIVI", iDIVI)
      container.set("EXPLID", iPLID)
      container.set("EXORNR", iORNR)
      container.set("EXITNO", iITNO)
      container.set("EXPONR", iPONR as Integer)
      container.set("EXCONO", iCONO)
      query.readAll(container, 6,maxPageSize, resultset)
    } else if (!iPLID.trim().isEmpty()&&!iORNR.trim().isEmpty()&&!iITNO.trim().isEmpty())
    {
      container.set("EXDIVI", iDIVI)
      container.set("EXPLID", iPLID)
      container.set("EXORNR", iORNR)
      container.set("EXITNO", iITNO)
      container.set("EXCONO", iCONO)
      query.readAll(container, 5,maxPageSize, resultset)
    }else if (!iPLID.trim().isEmpty()&&!iORNR.trim().isEmpty())
    {
      container.set("EXDIVI", iDIVI)
      container.set("EXPLID", iPLID)
      container.set("EXORNR", iORNR)
      container.set("EXCONO", iCONO)
      query.readAll(container, 4,maxPageSize, resultset)
    }else if (!iPLID.trim().isEmpty())
    {
      container.set("EXDIVI", iDIVI)
      container.set("EXPLID", iPLID)
      container.set("EXCONO", iCONO)
      query.readAll(container, 3,maxPageSize, resultset)
    }else
    {
      container.set("EXDIVI", iDIVI)
      container.set("EXCONO", iCONO)
      query.readAll(container, 2,maxPageSize, resultset)
    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
      mi.outData.put("PLID", container.get("EXPLID").toString())
      mi.outData.put("KLOC", container.get("EXKLOC").toString())
      mi.outData.put("ORNR", container.get("EXORNR").toString())
      mi.outData.put("ORNO", container.get("EXORNO").toString())
      mi.outData.put("PONR", container.get("EXPONR").toString())
      mi.outData.put("POSX", container.get("EXPOSX").toString())
      mi.outData.put("ITNO", container.get("EXITNO").toString())
      mi.outData.put("ORQT", container.get("EXORQT").toString())
      mi.outData.put("SNUM", container.get("EXSNUM").toString())
      mi.outData.put("WHLO", container.get("EXWHLO").toString())
      mi.outData.put("DLIX", container.get("EXDLIX").toString())
      mi.outData.put("RORN", container.get("EXRORN").toString())
      mi.outData.put("RORL", container.get("EXRORL").toString())
      mi.outData.put("RORX", container.get("EXRORX").toString())
      mi.outData.put("UDF1", container.get("EXUDF1").toString())
      mi.outData.put("UDF2", container.get("EXUDF2").toString())
      mi.outData.put("UDF3", container.get("EXUDF3").toString())
      mi.outData.put("UDF4", container.get("EXUDF4").toString())
      mi.outData.put("UDF5", container.get("EXUDF5").toString())
      mi.outData.put("UDF6", container.get("EXUDF6").toString())

      mi.write()

  }
}