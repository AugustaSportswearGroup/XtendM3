/**
 * README
 * This extension is being used to list PriceLine
 *
 * Name: EXT104MI.LstPriceLine
 * Description: Transaction used to list Price Line for ecom orders
 * Date	      Changed By                      Description
 *20231019  SuriyaN@fortude.co     List Price Line for ecom orders */

public class LstPriceLine extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private int iCONO, translationId, maxPageSize = 10000
  private String iCUNO, iFACI, iCUCD
  private ArrayList < HashMap < String, String >> priceList = new ArrayList < HashMap < String, String >> ()
  private boolean validInput = true

  public LstPriceLine(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
  }

  /**
   * Main method to retrieve price information for items in an order.
   */

  public void main() {
    iCONO = program.LDAZD.CONO as Integer
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO") as String
    iFACI = (mi.inData.get("FACI") == null || mi.inData.get("FACI").trim().isEmpty()) ? "" : mi.inData.get("FACI") as String
    iCUCD = (mi.inData.get("CUCD") == null || mi.inData.get("CUCD").trim().isEmpty()) ? "USD" : mi.inData.get("CUCD") as String
    String status = null
    String iITNO = null
    String iORQT = null
    if (validateInput()) {
      iITNO = (mi.inData.get("IT01") == null || mi.inData.get("IT01").trim().isEmpty()) ? "" : mi.inData.get("IT01") as String
      iORQT = (mi.inData.get("OR01") == null || mi.inData.get("OR01").trim().isEmpty()) ? "1" : mi.inData.get("OR01") as String
      status = getPriceLine(iITNO, iORQT)
      if (status != null) {
        mi.error(status)
        return
      }
      iITNO = (mi.inData.get("IT02") == null || mi.inData.get("IT02").trim().isEmpty()) ? "" : mi.inData.get("IT02") as String
      iORQT = (mi.inData.get("OR02") == null || mi.inData.get("OR02").trim().isEmpty()) ? "1" : mi.inData.get("OR02") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT03") == null || mi.inData.get("IT03").trim().isEmpty()) ? "" : mi.inData.get("IT03") as String
      iORQT = (mi.inData.get("OR03") == null || mi.inData.get("OR03").trim().isEmpty()) ? "1" : mi.inData.get("OR03") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT04") == null || mi.inData.get("IT04").trim().isEmpty()) ? "" : mi.inData.get("IT04") as String
      iORQT = (mi.inData.get("OR04") == null || mi.inData.get("OR04").trim().isEmpty()) ? "1" : mi.inData.get("OR04") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT05") == null || mi.inData.get("IT05").trim().isEmpty()) ? "" : mi.inData.get("IT05") as String
      iORQT = (mi.inData.get("OR05") == null || mi.inData.get("OR05").trim().isEmpty()) ? "1" : mi.inData.get("OR05") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT06") == null || mi.inData.get("IT06").trim().isEmpty()) ? "" : mi.inData.get("IT06") as String
      iORQT = (mi.inData.get("OR06") == null || mi.inData.get("OR06").trim().isEmpty()) ? "1" : mi.inData.get("OR06") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT07") == null || mi.inData.get("IT07").trim().isEmpty()) ? "" : mi.inData.get("IT07") as String
      iORQT = (mi.inData.get("OR07") == null || mi.inData.get("OR07").trim().isEmpty()) ? "1" : mi.inData.get("OR07") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT08") == null || mi.inData.get("IT08").trim().isEmpty()) ? "" : mi.inData.get("IT08") as String
      iORQT = (mi.inData.get("OR08") == null || mi.inData.get("OR08").trim().isEmpty()) ? "1" : mi.inData.get("OR08") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT09") == null || mi.inData.get("IT09").trim().isEmpty()) ? "" : mi.inData.get("IT09") as String
      iORQT = (mi.inData.get("OR09") == null || mi.inData.get("OR09").trim().isEmpty()) ? "1" : mi.inData.get("OR09") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT10") == null || mi.inData.get("IT10").trim().isEmpty()) ? "" : mi.inData.get("IT10") as String
      iORQT = (mi.inData.get("OR10") == null || mi.inData.get("OR10").trim().isEmpty()) ? "1" : mi.inData.get("OR10") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT11") == null || mi.inData.get("IT11").trim().isEmpty()) ? "" : mi.inData.get("IT11") as String
      iORQT = (mi.inData.get("OR11") == null || mi.inData.get("OR11").trim().isEmpty()) ? "1" : mi.inData.get("OR11") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT12") == null || mi.inData.get("IT12").trim().isEmpty()) ? "" : mi.inData.get("IT12") as String
      iORQT = (mi.inData.get("OR12") == null || mi.inData.get("OR12").trim().isEmpty()) ? "1" : mi.inData.get("OR12") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT13") == null || mi.inData.get("IT13").trim().isEmpty()) ? "" : mi.inData.get("IT13") as String
      iORQT = (mi.inData.get("OR13") == null || mi.inData.get("OR13").trim().isEmpty()) ? "1" : mi.inData.get("OR13") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT14") == null || mi.inData.get("IT14").trim().isEmpty()) ? "" : mi.inData.get("IT14") as String
      iORQT = (mi.inData.get("OR14") == null || mi.inData.get("OR14").trim().isEmpty()) ? "1" : mi.inData.get("OR14") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT15") == null || mi.inData.get("IT15").trim().isEmpty()) ? "" : mi.inData.get("IT15") as String
      iORQT = (mi.inData.get("OR15") == null || mi.inData.get("OR15").trim().isEmpty()) ? "1" : mi.inData.get("OR15") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT16") == null || mi.inData.get("IT16").trim().isEmpty()) ? "" : mi.inData.get("IT16") as String
      iORQT = (mi.inData.get("OR16") == null || mi.inData.get("OR16").trim().isEmpty()) ? "1" : mi.inData.get("OR16") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT17") == null || mi.inData.get("IT17").trim().isEmpty()) ? "" : mi.inData.get("IT17") as String
      iORQT = (mi.inData.get("OR17") == null || mi.inData.get("OR17").trim().isEmpty()) ? "1" : mi.inData.get("OR17") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT18") == null || mi.inData.get("IT18").trim().isEmpty()) ? "" : mi.inData.get("IT18") as String
      iORQT = (mi.inData.get("OR18") == null || mi.inData.get("OR18").trim().isEmpty()) ? "1" : mi.inData.get("OR18") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT19") == null || mi.inData.get("IT19").trim().isEmpty()) ? "" : mi.inData.get("IT19") as String
      iORQT = (mi.inData.get("OR19") == null || mi.inData.get("OR19").trim().isEmpty()) ? "1" : mi.inData.get("OR19") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT20") == null || mi.inData.get("IT20").trim().isEmpty()) ? "" : mi.inData.get("IT20") as String
      iORQT = (mi.inData.get("OR20") == null || mi.inData.get("OR20").trim().isEmpty()) ? "1" : mi.inData.get("OR20") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT21") == null || mi.inData.get("IT21").trim().isEmpty()) ? "" : mi.inData.get("IT21") as String
      iORQT = (mi.inData.get("OR21") == null || mi.inData.get("OR21").trim().isEmpty()) ? "1" : mi.inData.get("OR21") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT22") == null || mi.inData.get("IT22").trim().isEmpty()) ? "" : mi.inData.get("IT22") as String
      iORQT = (mi.inData.get("OR22") == null || mi.inData.get("OR22").trim().isEmpty()) ? "1" : mi.inData.get("OR22") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT23") == null || mi.inData.get("IT23").trim().isEmpty()) ? "" : mi.inData.get("IT23") as String
      iORQT = (mi.inData.get("OR23") == null || mi.inData.get("OR23").trim().isEmpty()) ? "1" : mi.inData.get("OR23") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT24") == null || mi.inData.get("IT24").trim().isEmpty()) ? "" : mi.inData.get("IT24") as String
      iORQT = (mi.inData.get("OR24") == null || mi.inData.get("OR24").trim().isEmpty()) ? "1" : mi.inData.get("OR24") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      iITNO = (mi.inData.get("IT25") == null || mi.inData.get("IT25").trim().isEmpty()) ? "" : mi.inData.get("IT25") as String
      iORQT = (mi.inData.get("OR25") == null || mi.inData.get("OR25").trim().isEmpty()) ? "1" : mi.inData.get("OR25") as String
      if (!iITNO.isEmpty()) {
        status = getPriceLine(iITNO, iORQT)
        if (status != null) {
          mi.error(status)
          return
        }
      }
      for (HashMap < String, String > price: priceList) {
        mi.outData.put("ITNO", price.ITNO)
        mi.outData.put("SAPR", price.SAPR)
        mi.outData.put("LNAM", price.LNAM)
        mi.outData.put("ORQA", price.ORQA)
        mi.outData.put("CUCD", price.CUCD)
        mi.outData.put("NETP", price.NETP)
        mi.write()
      }
    }
  }

  /**
   * Method to validate input data.
   * @return true if input is valid, false otherwise.
   */

  public boolean validateInput() {
    //Validate Payer is entered
    if (iCUNO.isEmpty()) {
      mi.error("Customer number must be entered.")
      validInput = false
    } else {
      //Validate Customer Number
      Map < String, String > params = ["CONO": iCONO.toString().trim(), "CUNO": iCUNO.toString().trim()]
      Closure < ? > callback = {
        Map < String,
        String > response ->
        if (response.CUNO == null) {
          mi.error("Invalid Customer Number " + iCUNO)
          validInput = false
          return false
        }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }
    if (iFACI.isEmpty()) {
      mi.error("Facility must be entered.")
      validInput = false
    } else {
      //Validate Facility Number
      Map < String, String > params = ["CONO": iCONO.toString().trim(), "FACI": iFACI.toString().trim()]
      Closure < ? > callback = {
        Map < String,
        String > response ->
        if (response.FACI == null) {
          mi.error("Invalid Facility Number " + iFACI)
          validInput = false
          return false
        }
      }
      miCaller.call("CRS008MI", "Get", params, callback)
    }
    return validInput
  }

  /**
   * Method to retrieve price line information.
   * @param itno The item number.
   * @param orqt The order quantity.
   * @return The status message.
   */

  public String getPriceLine(String itno, String orqt) {
    String status = null
    Map < String, String > params = ["CONO": iCONO.toString().trim(), "FACI": iFACI.toString().trim(), "CUNO": iCUNO.toString().trim(), "CUCD": iCUCD.toString().trim(), "ITNO": itno, "ORQA": orqt]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.error != null) {
        status = response.errorMessage
        validInput = false
        return false
      } else {
        HashMap < String, String > price = new HashMap < String, String > ()
        price.put("ITNO", itno)
        price.put("SAPR", response.SAPR as String)
        price.put("LNAM", response.LNAM as String)
        price.put("ORQA", orqt)
        price.put("CUCD", iCUCD)
        price.put("NETP", response.NETP as String)
        priceList.add(price)
      }
    }
    miCaller.call("OIS320MI", "GetPriceLine", params, callback)
    return status
  }

}