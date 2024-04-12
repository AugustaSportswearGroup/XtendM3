/**
 * README
 * This extension is being used to list customer order lines
 *
 * Name: EXT104MI.GetRepInfo
 * Description: Transaction used to get Rep Info for ecom orders
 * Date	      Changed By                      Description
 *20231019  SuriyaN@fortude.co     Get Rep Info for ecom orders */

public class GetRepInfo extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private int iCONO, translationId, maxPageSize = 10000
  private String iCUNO, sSMCD, sLSID, sLSIDCUNO
  private boolean validInput = true

  public GetRepInfo(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
  }

  /**
   * Main method to execute the transaction
   */

  public void main() {
    iCONO = program.LDAZD.CONO as Integer
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO").trim()

    if (!validateInput()) {
      mi.error("Customer order number " + iCUNO + " is invalid.")
    }
    if (sSMCD != null || !sSMCD.isEmpty()) {
      setCUNOData(sSMCD)
    }
    if (sLSID != null || !sLSID.isEmpty()) {
      String cuno = getTranslation("LSID" + sLSID)
      if (!cuno.isEmpty()) {
        sLSIDCUNO = cuno
        setCUNOData(cuno)
      }
    }
  }

  /**
   * Method to validate input data
   * @return true if input is valid, false otherwise
   */

  public boolean validateInput() {
    if (!iCUNO.trim().isEmpty()) {
      //Validate Customer Number
      DBAction query = database.table("OCUSMA").index("00").selection("OKSMCD", "OKLSID").build()
      DBContainer container = query.getContainer()
      container.set("OKCONO", iCONO)
      container.set("OKCUNO", iCUNO)
      if (query.read(container)) {
        sSMCD = container.get("OKSMCD").toString().trim()
        sLSID = container.get("OKLSID").toString().trim()
        validInput = true
      } else {
        validInput = false
      }
    } else {
      mi.error("Customer order number must be entered.")
      validInput = false
    }
    return validInput
  }

  /**
   * Method to set customer data
   * @param cuno Customer number
   */

  public void setCUNOData(String cuno) {

    Map < String, String > params = ["CONO": iCONO.toString().trim(), "CUNO": cuno.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CUNO != null) {
        if (sLSIDCUNO != null && sLSIDCUNO.equals(cuno)) {
          mi.outData.put("CUNM", sLSID + ": " + response.CUNM)
        } else {
          mi.outData.put("CUNM", response.CUNM)
        }

        mi.outData.put("CUA1", response.CUA1)
        mi.outData.put("CUA2", response.CUA2)
        mi.outData.put("TOWN", response.TOWN)
        mi.outData.put("ECAR", response.ECAR)
        mi.outData.put("PONO", response.PONO)
        mi.outData.put("CSCD", response.CSCD)
        mi.outData.put("PHNO", response.PHNO)
        mi.outData.put("PHN2", response.PHN2)
        mi.outData.put("TFNO", response.TFNO)
        mi.outData.put("EMAL", response.MAIL)
        mi.write()
      }
    }
    miCaller.call("CRS610MI", "GetBasicData", params, callback)
  }

  /**
   * getTranslation - Get the CRS881 data translation using a String keyword
   * @params String keyword
   * @return String
   */
  private String getTranslation(String keyword) {
    if (translationId == 0) {
      Map < String, String > params = ["CONO": iCONO.toString().trim(), "DIVI": "", "TRQF": "0", "MSTD": "ECOM", "MVRS": "1", "BMSG": "ECZ001MI", "IBOB": "O", "ELMP": "API", "ELMD": "Properties"]
      Closure < ? > callback = {
        Map < String,
        String > response ->
        if (response.IDTR != null) {
          translationId = response.IDTR as Integer
        }
      }
      miCaller.call("CRS881MI", "GetTranslation", params, callback)
    }

    String mbmd = ""
    if (translationId != 0) {
      Closure < ? > getTranslatedData = {
        DBContainer container ->
        mbmd = container.get("TDMBMD")
        if (mbmd == null) {
          mbmd = ""
        }
      }
      DBAction query = database.table("MBMTRD").index("30").selection("TDMBMD").build()
      DBContainer container = query.getContainer()
      container.set("TDCONO", iCONO)
      container.set("TDDIVI", "")
      container.set("TDIDTR", translationId as Integer)
      container.set("TDMVXD", keyword)
      query.readAll(container, 4, maxPageSize, getTranslatedData)
    }
    return mbmd.trim()
  }
}/**
 * README
 * This extension is being used to list customer order lines
 *
 * Name: EXT104MI.GetRepInfo
 * Description: Transaction used to get Rep Info for ecom orders
 * Date	      Changed By                      Description
 *20231019  SuriyaN@fortude.co     Get Rep Info for ecom orders */

public class GetRepInfo extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private int iCONO, translationId, maxPageSize = 10000
  private String iCUNO, sSMCD, sLSID, sLSIDCUNO
  private boolean validInput = true

  public GetRepInfo(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
  }

  /**
   * Main method to execute the transaction
   */

  public void main() {
    iCONO = program.LDAZD.CONO as Integer
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO").trim()

    if (!validateInput()) {
      mi.error("Customer order number " + iCUNO + " is invalid.")
    }
    if (sSMCD != null || !sSMCD.isEmpty()) {
      setCUNOData(sSMCD)
    }
    if (sLSID != null || !sLSID.isEmpty()) {
      String cuno = getTranslation("LSID" + sLSID)
      if (!cuno.isEmpty()) {
        sLSIDCUNO = cuno
        setCUNOData(cuno)
      }
    }
  }

  /**
   * Method to validate input data
   * @return true if input is valid, false otherwise
   */

  public boolean validateInput() {
    if (!iCUNO.trim().isEmpty()) {
      //Validate Customer Number
      DBAction query = database.table("OCUSMA").index("00").selection("OKSMCD", "OKLSID").build()
      DBContainer container = query.getContainer()
      container.set("OKCONO", iCONO)
      container.set("OKCUNO", iCUNO)
      if (query.read(container)) {
        sSMCD = container.get("OKSMCD").toString().trim()
        sLSID = container.get("OKLSID").toString().trim()
        validInput = true
      } else {
        validInput = false
      }
    } else {
      mi.error("Customer order number must be entered.")
      validInput = false
    }
    return validInput
  }

  /**
   * Method to set customer data
   * @param cuno Customer number
   */

  public void setCUNOData(String cuno) {

    Map < String, String > params = ["CONO": iCONO.toString().trim(), "CUNO": cuno.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CUNO != null) {
        if (sLSIDCUNO != null && sLSIDCUNO.equals(cuno)) {
          mi.outData.put("CUNM", sLSID + ": " + response.CUNM)
        } else {
          mi.outData.put("CUNM", response.CUNM)
        }

        mi.outData.put("CUA1", response.CUA1)
        mi.outData.put("CUA2", response.CUA2)
        mi.outData.put("TOWN", response.TOWN)
        mi.outData.put("ECAR", response.ECAR)
        mi.outData.put("PONO", response.PONO)
        mi.outData.put("CSCD", response.CSCD)
        mi.outData.put("PHNO", response.PHNO)
        mi.outData.put("PHN2", response.PHN2)
        mi.outData.put("TFNO", response.TFNO)
        mi.outData.put("EMAL", response.MAIL)
        mi.write()
      }
    }
    miCaller.call("CRS610MI", "GetBasicData", params, callback)
  }

  /**
   * getTranslation - Get the CRS881 data translation using a String keyword
   * @params String keyword
   * @return String
   */
  private String getTranslation(String keyword) {
    if (translationId == 0) {
      Map < String, String > params = ["CONO": iCONO.toString().trim(), "DIVI": "", "TRQF": "0", "MSTD": "ECOM", "MVRS": "1", "BMSG": "ECZ001MI", "IBOB": "O", "ELMP": "API", "ELMD": "Properties"]
      Closure < ? > callback = {
        Map < String,
        String > response ->
        if (response.IDTR != null) {
          translationId = response.IDTR as Integer
        }
      }
      miCaller.call("CRS881MI", "GetTranslation", params, callback)
    }

    String mbmd = ""
    if (translationId != 0) {
      Closure < ? > getTranslatedData = {
        DBContainer container ->
        mbmd = container.get("TDMBMD")
        if (mbmd == null) {
          mbmd = ""
        }
      }
      DBAction query = database.table("MBMTRD").index("30").selection("TDMBMD").build()
      DBContainer container = query.getContainer()
      container.set("TDCONO", iCONO)
      container.set("TDDIVI", "")
      container.set("TDIDTR", translationId as Integer)
      container.set("TDMVXD", keyword)
      query.readAll(container, 4, maxPageSize, getTranslatedData)
    }
    return mbmd.trim()
  }
}