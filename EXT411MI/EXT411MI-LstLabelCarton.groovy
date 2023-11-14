/**
 * README
 * This extension is being used to List records from EXTSLB table. 
 *
 * Name: EXT411MI.LstLabelCarton
 * Description: Listing records to EXTSLB table
 * Date	      Changed By                      Description
 *20230831  SuriyaN@fortude.co      Listing records from  EXTSLB table
 *
 */

public class LstLabelCarton extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iDIVI, iWHLO, iRCID, iCSID
  private int iCONO, iBXNO

  public LstLabelCarton(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iCSID = (mi.inData.get("CSID") == null || mi.inData.get("CSID").trim().isEmpty()) ? "" : mi.inData.get("CSID")
    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iBXNO = (mi.inData.get("BXNO") == null || mi.inData.get("BXNO").trim().isEmpty()) ? 0 : mi.inData.get("BXNO") as Integer

    listRecord()
  }
  /**
   *List records from EXTSLB table
   * @params 
   * @return 
   */
  public listRecord() {
      DBAction query = database.table("EXTSLB").selection("EXDIVI", "EXWHLO", "EXRCID", "EXRIDN", "EXCSID", "EXLNAM", "EXLDEV", "EXMAND", "EXMDTA", "EXCONO", "EXDLIX", "EXBXNO", "EXROSS", "EXLQTY", "EXRSET").index("00").build()
      DBContainer container = query.getContainer()

      if (!iRCID.trim().isEmpty() && !iWHLO.trim().isEmpty() && !iCSID.trim().isEmpty() && iBXNO != 0) {
        container.set("EXDIVI", iDIVI)
        container.set("EXWHLO", iWHLO)
        container.set("EXRCID", iRCID)
        container.set("EXCSID", iCSID)
        container.set("EXCONO", iCONO)
        container.set("EXBXNO", iBXNO)
        query.readAll(container, 6, resultset)
      } else if (!iRCID.trim().isEmpty() && !iWHLO.trim().isEmpty() && !iCSID.trim().isEmpty()) {
        container.set("EXDIVI", iDIVI)
        container.set("EXWHLO", iWHLO)
        container.set("EXRCID", iRCID)
        container.set("EXCSID", iCSID)
        container.set("EXCONO", iCONO)
        query.readAll(container, 5, resultset)
      } else if (!iRCID.trim().isEmpty() && !iWHLO.trim().isEmpty()) {
          container.set("EXDIVI", iDIVI)
          container.set("EXWHLO", iWHLO)
          container.set("EXRCID", iRCID)
          container.set("EXCONO", iCONO)
          query.readAll(container, 4, resultset)
        } else if (!iWHLO.trim().isEmpty()) {
            container.set("EXDIVI", iDIVI)
            container.set("EXWHLO", iWHLO)
            container.set("EXCONO", iCONO)
            query.readAll(container, 3, resultset)
          } else {
            container.set("EXDIVI", iDIVI)
            container.set("EXCONO", iCONO)
            query.readAll(container, 2, resultset)
          }

        }
        Closure < ? > resultset = {
          DBContainer container ->
          mi.outData.put("DIVI", container.get("EXDIVI").toString())
          mi.outData.put("WHLO", container.get("EXWHLO").toString())
          mi.outData.put("RCID", container.get("EXRCID").toString())
          mi.outData.put("RIDN", container.get("EXRIDN").toString())
          mi.outData.put("CSID", container.get("EXCSID").toString())
          mi.outData.put("LNAM", container.get("EXLNAM").toString())
          mi.outData.put("LDEV", container.get("EXLDEV").toString())
          mi.outData.put("MAND", container.get("EXMAND").toString())
          mi.outData.put("MDTA", container.get("EXMDTA").toString())
          mi.outData.put("CONO", container.get("EXCONO").toString())
          mi.outData.put("DLIX", container.get("EXDLIX").toString())
          mi.outData.put("BXNO", container.get("EXBXNO").toString())
          mi.outData.put("ROSS", container.get("EXROSS").toString())
          mi.outData.put("LQTY", container.get("EXLQTY").toString())
          mi.outData.put("RSET", container.get("EXRSET").toString())

          mi.write()

        }
      }