/**
 * README
 * This extension is being used to Get records from EXTXFL table. 
 *
 * Name: EXT410MI.GetCartonData
 * Description: Get records from EXTXFL table
 * Date	      Changed By                      Description
 *20230510  SuriyaN@fortude.co     Get records from EXTXFL table
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class GetCartonData extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program

  private String iDIVI, iWHLO, iORNO, iRCID, iRLPN, iITNO
  private int iCONO, iTRNO
  private long iDLIX

  public GetCartonData(MIAPI mi, DatabaseAPI database, ProgramAPI program) {
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

    getRecord()
  }
  /**
   *Get records from EXTXFL table
   * @params 
   * @return 
   */
  public getRecord() {
    DBAction query = database.table("EXTXFL").selection("EXDIVI","EXPONR", "EXWHLO", "EXORNO", "EXRCID", "EXRLPN", "EXITNO", "EXBXNO", "EXUDF0", "EXUDF1", "EXUDF2", "EXUDF3", "EXUDF4", "EXUDF5", "EXUDF6", "EXUDF7", "EXUDF8", "EXUDF9","EXQUAN","EXQWGT","EXTWGT","EXSTAT").index("00").build()
    DBContainer container = query.getContainer()

    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXDLIX", iDLIX)
    container.set("EXTRNO", iTRNO)
    container.set("EXWHLO", iWHLO)
    container.set("EXORNO", iORNO)
    container.set("EXRCID", iRCID)
    container.set("EXRLPN", iRLPN)
    container.set("EXITNO", iITNO)
    if (query.read(container)) {
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

    } else {
      mi.error("Record does not Exist.")
      return
    }
  }
}