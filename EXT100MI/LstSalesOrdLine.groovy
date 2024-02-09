/**
 * README
 * This extension is being used to List records from EXTSOL table. 
 *
 * Name: EXT100MI.LstSalesOrdLine
 * Description: Listing records to EXTSOL table
 * Date	          Changed By                  Description
 *20230310        SuriyaN@fortude.co      Listing records from EXTSOL table
 *20240208        AbhishekA@fortude.co    Updating Validation logic
 *
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class LstSalesOrdLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iORNO, iLNKY
  private int iCONO, maxPageSize = 10000, iPONR
  private boolean validInput = true

  public LstSalesOrdLine(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iLNKY = (mi.inData.get("LNKY") == null || mi.inData.get("LNKY").trim().isEmpty()) ? "" : mi.inData.get("LNKY")

    validateInput(iCONO, iORNO)
    if (validInput) {
      listRecord()
    }

  }

  /**
   *Validate inputs
   * @params int CONO ,String ORNO,String LNKY
   * @return void
   */
  private void validateInput(int CONO, String ORNO) {

    //Validate Company Number
    Map < String, String > params = ["CONO": CONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + CONO)
        validInput = false
        return
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)
    
   //Validate Header Record
    if (!iORNO.trim().isEmpty() && !iLNKY.trim().isEmpty()) {
      params = ["CONO": CONO.toString().trim(), "ORNO": ORNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ORNO == null) {
          mi.error("Header Record doesn't exist for the order " + ORNO)
          validInput = false
          return false
        }
      }
      miCaller.call("EXT100MI", "GetSalesOrdHead", params, callback)
    }
  }

  /**
   *List records from EXTSOL table
   * @params 
   * @return 
   */
  public void listRecord() {
    DBAction query = database.table("EXTSOL").selection("EXORNO", "EXLNKY", "EXPONR", "EXITNO", "EXITTY", "EXFUDS", "EXWHLO", "EXPRLV", "EXTXID", "EXUDCR", "EXURFT", "EXURFN", "EXUDSG", "EXCRCO", "EXCRSH", "EXMFNO", "EXUDCL", "EXUDDC", "EXORQT", "EXORRV", "EXTRQT", "EXORBO", "EXSAPR").index("00").build()
    DBContainer container = query.getContainer()

    if (!iORNO.trim().isEmpty() && !iLNKY.trim().isEmpty() && iPONR!=0) {
      container.set("EXCONO", iCONO)
      container.set("EXORNO", iORNO)
      container.set("EXLNKY", iLNKY)
      container.set("EXPONR", iPONR)
      if (query.read(container)) {
        mi.outData.put("ORNO", container.get("EXORNO").toString())
        mi.outData.put("LNKY", container.get("EXLNKY").toString())
        mi.outData.put("PONR", container.get("EXPONR").toString())
        mi.outData.put("ITNO", container.get("EXITNO").toString())
        mi.outData.put("ITTY", container.get("EXITTY").toString())
        mi.outData.put("FUDS", container.get("EXFUDS").toString())
        mi.outData.put("WHLO", container.get("EXWHLO").toString())
        mi.outData.put("PRLV", container.get("EXPRLV").toString())
        mi.outData.put("TXID", container.get("EXTXID").toString())
        mi.outData.put("UDCR", container.get("EXUDCR").toString())
        mi.outData.put("URFT", container.get("EXURFT").toString())
        mi.outData.put("URFN", container.get("EXURFN").toString())
        mi.outData.put("UDSG", container.get("EXUDSG").toString())
        mi.outData.put("CRCO", container.get("EXCRCO").toString())
        mi.outData.put("CRSH", container.get("EXCRSH").toString())
        mi.outData.put("MFNO", container.get("EXMFNO").toString())
        mi.outData.put("UDCL", container.get("EXUDCL").toString())
        mi.outData.put("UDDC", container.get("EXUDDC").toString())
        mi.outData.put("ORQT", container.get("EXORQT").toString())
        mi.outData.put("ORRV", container.get("EXORRV").toString())
        mi.outData.put("TRQT", container.get("EXTRQT").toString())
        mi.outData.put("ORBO", container.get("EXORBO").toString())
        mi.outData.put("SAPR", container.get("EXSAPR").toString())

        mi.write()
      }
    } else if (!iORNO.trim().isEmpty() && !iLNKY.trim().isEmpty()) {
      container.set("EXCONO", iCONO)
      container.set("EXORNO", iORNO)
      container.set("EXLNKY", iLNKY)
      query.readAll(container, 3, maxPageSize, resultset)
    } else if (!iORNO.trim().isEmpty()) {
      container.set("EXCONO", iCONO)
      container.set("EXORNO", iORNO)
      query.readAll(container, 2, maxPageSize, resultset)
    } else {
      container.set("EXCONO", iCONO)
      query.readAll(container, 1, maxPageSize, resultset)
    }
  }
  Closure < ? > resultset = {
    DBContainer container ->
    mi.outData.put("ORNO", container.get("EXORNO").toString())
    mi.outData.put("LNKY", container.get("EXLNKY").toString())
    mi.outData.put("PONR", container.get("EXPONR").toString())
    mi.outData.put("ITNO", container.get("EXITNO").toString())
    mi.outData.put("ITTY", container.get("EXITTY").toString())
    mi.outData.put("FUDS", container.get("EXFUDS").toString())
    mi.outData.put("WHLO", container.get("EXWHLO").toString())
    mi.outData.put("PRLV", container.get("EXPRLV").toString())
    mi.outData.put("TXID", container.get("EXTXID").toString())
    mi.outData.put("UDCR", container.get("EXUDCR").toString())
    mi.outData.put("URFT", container.get("EXURFT").toString())
    mi.outData.put("URFN", container.get("EXURFN").toString())
    mi.outData.put("UDSG", container.get("EXUDSG").toString())
    mi.outData.put("CRCO", container.get("EXCRCO").toString())
    mi.outData.put("CRSH", container.get("EXCRSH").toString())
    mi.outData.put("MFNO", container.get("EXMFNO").toString())
    mi.outData.put("UDCL", container.get("EXUDCL").toString())
    mi.outData.put("UDDC", container.get("EXUDDC").toString())
    mi.outData.put("ORQT", container.get("EXORQT").toString())
    mi.outData.put("ORRV", container.get("EXORRV").toString())
    mi.outData.put("TRQT", container.get("EXTRQT").toString())
    mi.outData.put("ORBO", container.get("EXORBO").toString())
    mi.outData.put("SAPR", container.get("EXSAPR").toString())

    mi.write()

  }
}