/**
 * README
 * This extension is being used to get the reporting Number from M3
 *
 * Name: EXT004MI.GetReportNum
 * Description:  Get the reporting Number from M3
 * Date	      Changed By                 Description
 *20230605  SuriyaN@fortude.co     Get the reporting Number from M3
 *20240212  AbhishekA@fortude.co   Updating Validation logic
 */

public class GetReportNum extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iCAMU, m3CAMU
  private int iCONO
  private boolean validInput = true, containerExists = false

  public GetReportNum(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iCAMU = (mi.inData.get("CAMU") == null || mi.inData.get("CAMU").trim().isEmpty()) ? "" : mi.inData.get("CAMU")

    validateInput()
    if (validInput) {
      setOutput()
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
        return
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Warehouse Number
    params = ["CONO": iCONO.toString().trim(), "WHLO": iWHLO.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.WHLO == null) {
        mi.error("Invalid Warehouse Number " + iWHLO)
        validInput = false
        return
      }
    }
    miCaller.call("MMS005MI", "GetWarehouse", params, callback)

    //Validate if record exists

    ExpressionFactory expression = database.getExpressionFactory("EXTCOM")
    expression = expression.eq("EXWHLO", iWHLO.toString().trim()).and(expression.eq("EXCAMU", iCAMU.toString().trim()))
    DBAction query = database.table("EXTCOM").matching(expression).build()
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO)
    if (!query.readAll(container, 1, validateCAMU)) {
      mi.error("Lucas Record doesn't exist for the warehouse container")
      validInput = false
      return
    }

    //Check if Lot exists
    ExpressionFactory expressionMITLOC = database.getExpressionFactory("MITLOC")
    expressionMITLOC = expressionMITLOC.eq("MLCAMU", iCAMU.toString().trim())
    DBAction queryMITLOC = database.table("MITLOC").matching(expressionMITLOC).build()
    DBContainer containerMITLOC = queryMITLOC.getContainer()
    containerMITLOC.set("MLCONO", iCONO)
    if (!queryMITLOC.readAll(containerMITLOC, 1, validateMITLOC)) {
      containerExists = false
    }

    //If Container doesn't exist, try with prefix 307843 (Augusta Business Logic)
    if (!containerExists) {
      ExpressionFactory expressionMITLOC1 = database.getExpressionFactory("MITLOC")
      expressionMITLOC1 = expressionMITLOC1.eq("MLCAMU", "307843" + iCAMU.trim())
      DBAction queryMITLOC1 = database.table("MITLOC").matching(expressionMITLOC1).build()
      DBContainer containerMITLOC1 = queryMITLOC1.getContainer()
      containerMITLOC1.set("MLCONO", iCONO)
      if (!queryMITLOC1.readAll(containerMITLOC1, 1, validateMITLOC1)) {
        containerExists = false
      }

    }

    //If Container doesn't exist, try with prefix 307656 (Augusta Business Logic)
    if (!containerExists) {
      ExpressionFactory expressionMITLOC2 = database.getExpressionFactory("MITLOC")
      expressionMITLOC2 = expressionMITLOC2.eq("MLCAMU", "307656" + iCAMU.trim())
      DBAction queryMITLOC2 = database.table("MITLOC").matching(expressionMITLOC2).build()
      DBContainer containerMITLOC2 = queryMITLOC2.getContainer()
      containerMITLOC2.set("MLCONO", iCONO)
      if (!queryMITLOC2.readAll(containerMITLOC2, 1, validateMITLOC2)) {
        containerExists = false
      }

    }

    if (!containerExists) {
      mi.error("Container " + iCAMU + " doesn't exist in M3.")
      validInput = false
      return
    }

  }

  Closure < ? > validateCAMU = {
    DBContainer container ->

  }

  Closure < ? > validateMITLOC = {
    DBContainer container ->
    m3CAMU = iCAMU.trim()
    containerExists = true

  }

  Closure < ? > validateMITLOC1 = {
    DBContainer container ->
    m3CAMU = "307843" + iCAMU.trim()
    containerExists = true

  }

  Closure < ? > validateMITLOC2 = {
    DBContainer container ->
    m3CAMU = "307656" + iCAMU.trim()
    containerExists = true

  }

  /**
   *SetOutput
   * @params
   * @return
   */
  public void setOutput() {
    ExpressionFactory expression = database.getExpressionFactory("MITALO")
    expression = expression.eq("MQTTYP", "92").and(expression.eq("MQWHLO", iWHLO)).and(expression.eq("MQCAMU", m3CAMU))
    DBAction query = database.table("MITALO").index("00").matching(expression).selection("MQPLRN").build()
    DBContainer container = query.getContainer()
    container.set("MQCONO", iCONO)
    Closure < ? > getReportingNumber = {
      DBContainer containerResult ->
      mi.outData.put("PLRN", containerResult.get("MQPLRN").toString())
    }

    query.readAll(container, 1, getReportingNumber)

  }
}