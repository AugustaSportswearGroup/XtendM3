/**
 * README
 * This extension is being used to list on hold orders by credit rep
 *
 * Name: EXT104MI.LstStopOrdByCR
 * Description: Transaction used to list on hold orders by credit rep
 * Date	      Changed By                      Description
 *20231019  SuriyaN@fortude.co     List on hold orders by credit rep*/
 
 
import groovy.json.JsonSlurper

public class LstStopOrdByCR extends ExtendM3Transaction {
  private final MIAPI mi
  private final IonAPI ion
  private final LoggerAPI logger
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private int iCONO, translationId, rowCount, maxPageSize = 1000
  private String iCDRC, iOBLC, iOBLU, iORSL, iORSU, iDIVI
  private boolean validInput = true
  private String queryId, location, jobStatus

  public LstStopOrdByCR(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller, IonAPI ion, LoggerAPI logger) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
    this.ion = ion
    this.logger = logger
  }

  /**
   * Executes the main logic of the transaction.
   */

  public void main() {
    iCONO = program.LDAZD.CONO as Integer
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI as String : mi.inData.get("DIVI") as String
    iCDRC = (mi.inData.get("CDRC") == null || mi.inData.get("CDRC").trim().isEmpty()) ? "" : mi.inData.get("CDRC") as String
    iOBLC = (mi.inData.get("OBLC") == null || mi.inData.get("OBLC").trim().isEmpty()) ? "" : mi.inData.get("OBLC") as String
    iOBLU = (mi.inData.get("OBLU") == null || mi.inData.get("OBLU").trim().isEmpty()) ? "" : mi.inData.get("OBLU") as String
    iORSL = (mi.inData.get("ORSL") == null || mi.inData.get("ORSL").trim().isEmpty()) ? "" : mi.inData.get("ORSL") as String
    iORSU = (mi.inData.get("ORSU") == null || mi.inData.get("ORSU").trim().isEmpty()) ? "" : mi.inData.get("ORSU") as String
    if (validateInput()) {
      postJob()
      if (queryId != null) {
        //Check job status
        checkJobStatus()
        if (jobStatus.equals("FINISHED")) {
          getJobResult(0, maxPageSize)
        }
      }
    }
  }

  /**
   * Validates the input data provided for the transaction.
   * 
   * @return true if the input data is valid, false otherwise.
   */

  public boolean validateInput() {
    //Validate Inputs are entered
    if (iCDRC.isEmpty()) {
      mi.error("Credit rep must be entered.")
      validInput = false
      return
    }
    if (iOBLC.isEmpty()) {
      mi.error("Credit stop from value must be entered.")
      validInput = false
      return
    } else {
      if (iOBLC.isNumber()) {
        int oblc = iOBLC as Integer
        if (oblc < 1 || oblc > 8) {
          validInput = false
        }
      } else {
        validInput = false
      }
      if (!validInput) {
        mi.error("Credit stop from value must be between 1 and 8.")
        return
      }
    }
    if (iOBLU.isEmpty()) {
      mi.error("Credit stop to value must be entered.")
      validInput = false
      return
    } else {
      if (iOBLU.isNumber()) {
        int oblc = iOBLU as Integer
        if (oblc < 1 || oblc > 8) {
          validInput = false
        }
      } else {
        validInput = false
      }
      if (!validInput) {
        mi.error("Credit stop to value must be between 1 and 8.")
        return
      }
    }
    if (iORSL.isEmpty()) {
      mi.error("Order lower status from value must be entered.")
      validInput = false
      return
    } else {
      if (iORSL.isNumber()) {
        int orsl = iORSL as Integer
        if (orsl < 22 || orsl > 33) {
          validInput = false
        }
      } else {
        validInput = false
      }
      if (!validInput) {
        mi.error("Order lower status from value must be between 22 and 33.")
        return
      }
    }
    if (iORSU.isEmpty()) {
      mi.error("Order lower status to value must be entered.")
      validInput = false
      return
    } else {
      if (iORSU.isNumber()) {
        int orsl = iORSU as Integer
        if (orsl < 22 || orsl > 33) {
          validInput = false
        }
      } else {
        validInput = false
      }
      if (!validInput) {
        mi.error("Order lower status to value must be between 22 and 33.")
        return
      }
    }
    return validInput
  }

  /**
   * Posts a job to fetch data based on specified criteria.
   */

  public void postJob() {
    String endpoint = "/DATAFABRIC/compass/v2/jobs"
    Map <String, String> headers = ["Accept": "application/json"]
    Map <String, String> queryParameters = ["queryExecutor": "datalake", "records": "0"]
    String query = "select ohead.CUNO, cusma.CUNM, ohead.PYNO, ohead.ORNO, ohead.OBLC, ohead.ORSL, ohead.ORST, ohead.ORDT, cusma.CDRC, ohead.ORTP, ohead.YREF, ohead.OREF, ohead.CUOR, ohead.NTLA, ohead.TEPY, ohead.WHLO, ohead.MODL "
    query = query + "from OOHEAD ohead Join OCUSMA cusma on ohead.CONO = cusma.CONO and ohead.CUNO = cusma.CUNO and cusma.CDRC <> '' and cusma.STAT='20' "
    query = query + "where ohead.CONO = 1 and ohead.DIVI='" + iDIVI + "' and ohead.OBLC >= " + iOBLC + " and ohead.OBLC <= " + iOBLU + " and ohead.ORSL >= '" + iORSL + "' and ohead.ORSL <='" + iORSU + "' and cusma.CDRC = '" + iCDRC + "' order by ohead.ORDT asc"
    IonResponse response = ion.post(endpoint, headers, queryParameters, query)
    if (response.getStatusCode() <= 202) {
      //mi.error("Response " + response.getContent())
      JsonSlurper slurper = new JsonSlurper()
      Map jobInfo = (Map) slurper.parseText(response.getContent())
      queryId = jobInfo.get("queryId")
      location = jobInfo.get("location")
      jobStatus = jobInfo.get("status")
      return
    }
    if (response.getError()) {
      mi.error("Failed calling ION API, detailed error message: ${response.getErrorMessage()}")
      logger.debug("Failed calling ION API, detailed error message: ${response.getErrorMessage()}")
      return
    }
    if (response.getStatusCode() > 202) {
      mi.error("Expected status 200 but got ${response.getStatusCode()} instead")
      logger.debug("Expected status 200 but got ${response.getStatusCode()} instead")
      return
    }
  }

  /**
   * Checks the status of the job posted earlier.
   * 
   * @return the status of the job.
   */

  public String checkJobStatus() {
    String endpoint = "/DATAFABRIC/compass/v2/" + location
    Map <String, String> headers = ["Accept": "application/json"]
    Map <String, String> queryParameters = ["queryExecutor": "datalake", "timeout": "25"]
    IonResponse response = ion.get(endpoint, headers, queryParameters)
    if (response.getStatusCode() <= 202) {
      JsonSlurper slurper = new JsonSlurper()
      Map jobInfo = (Map) slurper.parseText(response.getContent())
      jobStatus = jobInfo.get("status")
      if (jobStatus.equals("FINISHED")) {
        rowCount = jobInfo.get("rowCount") as Integer
      }
      return jobStatus
    } else {
      mi.error("Expected status 200 but got ${response.getStatusCode()} instead")
      logger.debug("Expected status 200 but got ${response.getStatusCode()} instead")
      return
    }
  }

  /**
   * Retrieves the result of the job based on the provided offset and limit.
   * 
   * @param offset the starting index of the result set.
   * @param limit the maximum number of records to retrieve.
   */

  public void getJobResult(int offset, int limit) {
    String endpoint = "/DATAFABRIC/compass/v2/jobs/" + queryId + "/result/"
    Map <String, String> headers = ["Accept": "application/json"]
    Map <String, String> queryParameters = ["queryExecutor": "datalake", "offset": offset as String, "limit": limit as String]
    IonResponse response = ion.get(endpoint, headers, queryParameters)
    if (response.getStatusCode() <= 202) {
      //mi.error("Response " + response.getContent())
      JsonSlurper slurper = new JsonSlurper()
      List < Map > jobResult = (List) slurper.parseText(response.getContent())
      showOutput(jobResult)
    } else {
      mi.error("Expected status 200 but got ${response.getStatusCode()} instead")
      logger.debug("Expected status 200 but got ${response.getStatusCode()} instead")
      return
    }
  }

  /**
   * Shows the output data retrieved from the job result.
   * 
   * @param orders the list of orders to display.
   */
  public void showOutput(List < Map > orders) {
    for (Map order: orders) {
      mi.outData.put("CUNO", order.get("CUNO").toString())
      mi.outData.put("CUNM", order.get("CUNM").toString())
      mi.outData.put("PYNO", order.get("PYNO").toString())
      mi.outData.put("ORNO", order.get("ORNO").toString())
      mi.outData.put("OBLC", order.get("OBLC").toString())
      mi.outData.put("ORSL", order.get("ORSL").toString())
      mi.outData.put("ORST", order.get("ORST").toString())
      mi.outData.put("ORDT", order.get("ORDT").toString())
      mi.outData.put("CDRC", order.get("CDRC").toString())
      mi.outData.put("ORTP", order.get("ORTP").toString())
      mi.outData.put("YREF", order.get("YREF").toString())
      mi.outData.put("OREF", order.get("OREF").toString())
      mi.outData.put("CUOR", order.get("CUOR").toString())
      String ntla = String.format("%.2f", order.get("NTLA") as Double)
      mi.outData.put("NTLA", ntla)
      mi.outData.put("TEPY", order.get("TEPY").toString())
      mi.outData.put("WHLO", order.get("WHLO").toString())
      mi.outData.put("MODL", order.get("MODL").toString())
      mi.write()
    }
  }
}