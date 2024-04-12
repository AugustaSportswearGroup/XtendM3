/**
 * README
 * This extension is being used to list ATP
 *
 * Name: EXT104MI.LstATP
 * Description: Transaction used to list ATP for ecom orders
 * Date	      Changed By                      Description
 *20231019  SuriyaN@fortude.co     List ATP for ecom orders */

import java.text.DateFormat
import java.text.SimpleDateFormat

public class LstATP extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private int iCONO, maxPageSize = 10000, atpDate, ccaFlag, translationId, xReqt, zTime, xxPlnf = 0
  private String iORNO, iDIVI, xWHLO, currentDate, currentTime, mode
  private double atpQty, xTrqt, xAqty
  private boolean validInput = true, authFlag = false
  private HashMap < String, String > mitatp

  public LstATP(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller) {
    this.mi = mi
    this.program = program
    this.database = database
    this.miCaller = miCaller
  }

  /**
   * Main method to process input data and retrieve ATP (Available to Promise) for each item.
   */

  public void main() {

    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO").trim() as Integer
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI as String : mi.inData.get("DIVI") as String
    String iWH01 = (mi.inData.get("WH01") == null || mi.inData.get("WH01").trim().isEmpty()) ? "" : mi.inData.get("WH01") as String
    String iIT01 = (mi.inData.get("IT01") == null || mi.inData.get("IT01").trim().isEmpty()) ? "" : mi.inData.get("IT01") as String
    String iMO01 = (mi.inData.get("IT01") == null || mi.inData.get("MO01").trim().isEmpty()) ? "" : mi.inData.get("MO01") as String
    int iRE01 = (mi.inData.get("RE01") == null || mi.inData.get("RE01").trim().isEmpty()) ? 0 : mi.inData.get("RE01") as Integer

    String iWH02 = (mi.inData.get("WH02") == null || mi.inData.get("WH02").trim().isEmpty()) ? "" : mi.inData.get("WH02") as String
    String iIT02 = (mi.inData.get("IT02") == null || mi.inData.get("IT02").trim().isEmpty()) ? "" : mi.inData.get("IT02") as String
    String iMO02 = (mi.inData.get("IT02") == null || mi.inData.get("MO02").trim().isEmpty()) ? "" : mi.inData.get("MO02") as String
    int iRE02 = (mi.inData.get("RE02") == null || mi.inData.get("RE02").trim().isEmpty()) ? 0 : mi.inData.get("RE02") as Integer

    String iWH03 = (mi.inData.get("WH03") == null || mi.inData.get("WH03").trim().isEmpty()) ? "" : mi.inData.get("WH03") as String
    String iIT03 = (mi.inData.get("IT03") == null || mi.inData.get("IT03").trim().isEmpty()) ? "" : mi.inData.get("IT03") as String
    String iMO03 = (mi.inData.get("MO03") == null || mi.inData.get("MO03").trim().isEmpty()) ? "" : mi.inData.get("MO03") as String
    int iRE03 = (mi.inData.get("RE03") == null || mi.inData.get("RE03").trim().isEmpty()) ? 0 : mi.inData.get("RE03") as Integer

    String iWH04 = (mi.inData.get("WH04") == null || mi.inData.get("WH04").trim().isEmpty()) ? "" : mi.inData.get("WH04") as String
    String iIT04 = (mi.inData.get("IT04") == null || mi.inData.get("IT04").trim().isEmpty()) ? "" : mi.inData.get("IT04") as String
    String iMO04 = (mi.inData.get("MO04") == null || mi.inData.get("MO04").trim().isEmpty()) ? "" : mi.inData.get("MO04") as String
    int iRE04 = (mi.inData.get("RE04") == null || mi.inData.get("RE04").trim().isEmpty()) ? 0 : mi.inData.get("RE04") as Integer

    String iWH05 = (mi.inData.get("WH05") == null || mi.inData.get("WH05").trim().isEmpty()) ? "" : mi.inData.get("WH05") as String
    String iIT05 = (mi.inData.get("IT05") == null || mi.inData.get("IT05").trim().isEmpty()) ? "" : mi.inData.get("IT05") as String
    String iMO05 = (mi.inData.get("MO05") == null || mi.inData.get("MO05").trim().isEmpty()) ? "" : mi.inData.get("MO05") as String
    int iRE05 = (mi.inData.get("RE05") == null || mi.inData.get("RE05").trim().isEmpty()) ? 0 : mi.inData.get("RE05") as Integer

    String iWH06 = (mi.inData.get("WH06") == null || mi.inData.get("WH06").trim().isEmpty()) ? "" : mi.inData.get("WH06") as String
    String iIT06 = (mi.inData.get("IT06") == null || mi.inData.get("IT06").trim().isEmpty()) ? "" : mi.inData.get("IT06") as String
    String iMO06 = (mi.inData.get("MO06") == null || mi.inData.get("MO06").trim().isEmpty()) ? "" : mi.inData.get("MO06") as String
    int iRE06 = (mi.inData.get("RE06") == null || mi.inData.get("RE06").trim().isEmpty()) ? 0 : mi.inData.get("RE06") as Integer

    String iWH07 = (mi.inData.get("WH07") == null || mi.inData.get("WH07").trim().isEmpty()) ? "" : mi.inData.get("WH07") as String
    String iIT07 = (mi.inData.get("IT07") == null || mi.inData.get("IT07").trim().isEmpty()) ? "" : mi.inData.get("IT07") as String
    String iMO07 = (mi.inData.get("MO07") == null || mi.inData.get("MO07").trim().isEmpty()) ? "" : mi.inData.get("MO07") as String
    int iRE07 = (mi.inData.get("RE07") == null || mi.inData.get("RE07").trim().isEmpty()) ? 0 : mi.inData.get("RE07") as Integer

    String iWH08 = (mi.inData.get("WH08") == null || mi.inData.get("WH08").trim().isEmpty()) ? "" : mi.inData.get("WH08") as String
    String iIT08 = (mi.inData.get("IT08") == null || mi.inData.get("IT08").trim().isEmpty()) ? "" : mi.inData.get("IT08") as String
    String iMO08 = (mi.inData.get("MO08") == null || mi.inData.get("MO08").trim().isEmpty()) ? "" : mi.inData.get("MO08") as String
    int iRE08 = (mi.inData.get("RE08") == null || mi.inData.get("RE08").trim().isEmpty()) ? 0 : mi.inData.get("RE08") as Integer

    String iWH09 = (mi.inData.get("WH09") == null || mi.inData.get("WH09").trim().isEmpty()) ? "" : mi.inData.get("WH09") as String
    String iIT09 = (mi.inData.get("IT09") == null || mi.inData.get("IT09").trim().isEmpty()) ? "" : mi.inData.get("IT09") as String
    String iMO09 = (mi.inData.get("MO09") == null || mi.inData.get("MO09").trim().isEmpty()) ? "" : mi.inData.get("MO09") as String
    int iRE09 = (mi.inData.get("RE09") == null || mi.inData.get("RE09").trim().isEmpty()) ? 0 : mi.inData.get("RE09") as Integer

    String iWH10 = (mi.inData.get("WH10") == null || mi.inData.get("WH10").trim().isEmpty()) ? "" : mi.inData.get("WH10") as String
    String iIT10 = (mi.inData.get("IT10") == null || mi.inData.get("IT10").trim().isEmpty()) ? "" : mi.inData.get("IT10") as String
    String iMO10 = (mi.inData.get("MO10") == null || mi.inData.get("MO10").trim().isEmpty()) ? "" : mi.inData.get("MO10") as String
    int iRE10 = (mi.inData.get("RE10") == null || mi.inData.get("RE10").trim().isEmpty()) ? 0 : mi.inData.get("RE10") as Integer

    String iWH11 = (mi.inData.get("WH11") == null || mi.inData.get("WH11").trim().isEmpty()) ? "" : mi.inData.get("WH11") as String
    String iIT11 = (mi.inData.get("IT11") == null || mi.inData.get("IT11").trim().isEmpty()) ? "" : mi.inData.get("IT11") as String
    String iMO11 = (mi.inData.get("MO11") == null || mi.inData.get("MO11").trim().isEmpty()) ? "" : mi.inData.get("MO11") as String
    int iRE11 = (mi.inData.get("RE11") == null || mi.inData.get("RE11").trim().isEmpty()) ? 0 : mi.inData.get("RE11") as Integer

    String iWH12 = (mi.inData.get("WH12") == null || mi.inData.get("WH12").trim().isEmpty()) ? "" : mi.inData.get("WH12") as String
    String iIT12 = (mi.inData.get("IT12") == null || mi.inData.get("IT12").trim().isEmpty()) ? "" : mi.inData.get("IT12") as String
    String iMO12 = (mi.inData.get("MO12") == null || mi.inData.get("MO12").trim().isEmpty()) ? "" : mi.inData.get("MO12") as String
    int iRE12 = (mi.inData.get("RE12") == null || mi.inData.get("RE12").trim().isEmpty()) ? 0 : mi.inData.get("RE12") as Integer

    String iWH13 = (mi.inData.get("WH13") == null || mi.inData.get("WH13").trim().isEmpty()) ? "" : mi.inData.get("WH13") as String
    String iIT13 = (mi.inData.get("IT13") == null || mi.inData.get("IT13").trim().isEmpty()) ? "" : mi.inData.get("IT13") as String
    String iMO13 = (mi.inData.get("MO13") == null || mi.inData.get("MO13").trim().isEmpty()) ? "" : mi.inData.get("MO13") as String
    int iRE13 = (mi.inData.get("RE13") == null || mi.inData.get("RE13").trim().isEmpty()) ? 0 : mi.inData.get("RE13") as Integer

    String iWH14 = (mi.inData.get("WH14") == null || mi.inData.get("WH14").trim().isEmpty()) ? "" : mi.inData.get("WH14") as String
    String iIT14 = (mi.inData.get("IT14") == null || mi.inData.get("IT14").trim().isEmpty()) ? "" : mi.inData.get("IT14") as String
    String iMO14 = (mi.inData.get("MO14") == null || mi.inData.get("MO14").trim().isEmpty()) ? "" : mi.inData.get("MO14") as String
    int iRE14 = (mi.inData.get("RE14") == null || mi.inData.get("RE14").trim().isEmpty()) ? 0 : mi.inData.get("RE14") as Integer

    String iWH15 = (mi.inData.get("WH15") == null || mi.inData.get("WH15").trim().isEmpty()) ? "" : mi.inData.get("WH15") as String
    String iIT15 = (mi.inData.get("IT15") == null || mi.inData.get("IT15").trim().isEmpty()) ? "" : mi.inData.get("IT15") as String
    String iMO15 = (mi.inData.get("MO15") == null || mi.inData.get("MO15").trim().isEmpty()) ? "" : mi.inData.get("MO15") as String
    int iRE15 = (mi.inData.get("RE15") == null || mi.inData.get("RE15").trim().isEmpty()) ? 0 : mi.inData.get("RE15") as Integer

    String iWH16 = (mi.inData.get("WH16") == null || mi.inData.get("WH16").trim().isEmpty()) ? "" : mi.inData.get("WH16") as String
    String iIT16 = (mi.inData.get("IT16") == null || mi.inData.get("IT16").trim().isEmpty()) ? "" : mi.inData.get("IT16") as String
    String iMO16 = (mi.inData.get("MO16") == null || mi.inData.get("MO16").trim().isEmpty()) ? "" : mi.inData.get("MO16") as String
    int iRE16 = (mi.inData.get("RE16") == null || mi.inData.get("RE16").trim().isEmpty()) ? 0 : mi.inData.get("RE16") as Integer

    String iWH17 = (mi.inData.get("WH17") == null || mi.inData.get("WH17").trim().isEmpty()) ? "" : mi.inData.get("WH17") as String
    String iIT17 = (mi.inData.get("IT17") == null || mi.inData.get("IT17").trim().isEmpty()) ? "" : mi.inData.get("IT17") as String
    String iMO17 = (mi.inData.get("MO17") == null || mi.inData.get("MO17").trim().isEmpty()) ? "" : mi.inData.get("MO17") as String
    int iRE17 = (mi.inData.get("RE17") == null || mi.inData.get("RE17").trim().isEmpty()) ? 0 : mi.inData.get("RE17") as Integer

    String iWH18 = (mi.inData.get("WH18") == null || mi.inData.get("WH18").trim().isEmpty()) ? "" : mi.inData.get("WH18") as String
    String iIT18 = (mi.inData.get("IT18") == null || mi.inData.get("IT18").trim().isEmpty()) ? "" : mi.inData.get("IT18") as String
    String iMO18 = (mi.inData.get("MO18") == null || mi.inData.get("MO18").trim().isEmpty()) ? "" : mi.inData.get("MO18") as String
    int iRE18 = (mi.inData.get("RE18") == null || mi.inData.get("RE18").trim().isEmpty()) ? 0 : mi.inData.get("RE18") as Integer

    String iWH19 = (mi.inData.get("WH19") == null || mi.inData.get("WH19").trim().isEmpty()) ? "" : mi.inData.get("WH19") as String
    String iIT19 = (mi.inData.get("IT19") == null || mi.inData.get("IT19").trim().isEmpty()) ? "" : mi.inData.get("IT19") as String
    String iMO19 = (mi.inData.get("MO19") == null || mi.inData.get("MO19").trim().isEmpty()) ? "" : mi.inData.get("MO19") as String
    int iRE19 = (mi.inData.get("RE19") == null || mi.inData.get("RE19").trim().isEmpty()) ? 0 : mi.inData.get("RE19") as Integer

    String iWH20 = (mi.inData.get("WH20") == null || mi.inData.get("WH20").trim().isEmpty()) ? "" : mi.inData.get("WH20") as String
    String iIT20 = (mi.inData.get("IT20") == null || mi.inData.get("IT20").trim().isEmpty()) ? "" : mi.inData.get("IT20") as String
    String iMO20 = (mi.inData.get("MO20") == null || mi.inData.get("MO20").trim().isEmpty()) ? "" : mi.inData.get("MO20") as String
    int iRE20 = (mi.inData.get("RE20") == null || mi.inData.get("RE20").trim().isEmpty()) ? 0 : mi.inData.get("RE20") as Integer

    String iWH21 = (mi.inData.get("WH21") == null || mi.inData.get("WH21").trim().isEmpty()) ? "" : mi.inData.get("WH21") as String
    String iIT21 = (mi.inData.get("IT21") == null || mi.inData.get("IT21").trim().isEmpty()) ? "" : mi.inData.get("IT21") as String
    String iMO21 = (mi.inData.get("MO21") == null || mi.inData.get("MO21").trim().isEmpty()) ? "" : mi.inData.get("MO21") as String
    int iRE21 = (mi.inData.get("RE21") == null || mi.inData.get("RE21").trim().isEmpty()) ? 0 : mi.inData.get("RE21") as Integer

    String iWH22 = (mi.inData.get("WH22") == null || mi.inData.get("WH22").trim().isEmpty()) ? "" : mi.inData.get("WH22") as String
    String iIT22 = (mi.inData.get("IT22") == null || mi.inData.get("IT22").trim().isEmpty()) ? "" : mi.inData.get("IT22") as String
    String iMO22 = (mi.inData.get("MO22") == null || mi.inData.get("MO22").trim().isEmpty()) ? "" : mi.inData.get("MO22") as String
    int iRE22 = (mi.inData.get("RE22") == null || mi.inData.get("RE22").trim().isEmpty()) ? 0 : mi.inData.get("RE22") as Integer

    String iWH23 = (mi.inData.get("WH23") == null || mi.inData.get("WH23").trim().isEmpty()) ? "" : mi.inData.get("WH23") as String
    String iIT23 = (mi.inData.get("IT23") == null || mi.inData.get("IT23").trim().isEmpty()) ? "" : mi.inData.get("IT23") as String
    String iMO23 = (mi.inData.get("MO23") == null || mi.inData.get("MO23").trim().isEmpty()) ? "" : mi.inData.get("MO23") as String
    int iRE23 = (mi.inData.get("RE23") == null || mi.inData.get("RE23").trim().isEmpty()) ? 0 : mi.inData.get("RE23") as Integer

    String iWH24 = (mi.inData.get("WH24") == null || mi.inData.get("WH24").trim().isEmpty()) ? "" : mi.inData.get("WH24") as String
    String iIT24 = (mi.inData.get("IT24") == null || mi.inData.get("IT24").trim().isEmpty()) ? "" : mi.inData.get("IT24") as String
    String iMO24 = (mi.inData.get("MO24") == null || mi.inData.get("MO24").trim().isEmpty()) ? "" : mi.inData.get("MO24") as String
    int iRE24 = (mi.inData.get("RE24") == null || mi.inData.get("RE24").trim().isEmpty()) ? 0 : mi.inData.get("RE24") as Integer

    String iWH25 = (mi.inData.get("WH25") == null || mi.inData.get("WH25").trim().isEmpty()) ? "" : mi.inData.get("WH25") as String
    String iIT25 = (mi.inData.get("IT25") == null || mi.inData.get("IT25").trim().isEmpty()) ? "" : mi.inData.get("IT25") as String
    String iMO25 = (mi.inData.get("MO25") == null || mi.inData.get("MO25").trim().isEmpty()) ? "" : mi.inData.get("MO25") as String
    int iRE25 = (mi.inData.get("RE25") == null || mi.inData.get("RE25").trim().isEmpty()) ? 0 : mi.inData.get("RE25") as Integer
    mode = (mi.inData.get("MODE") == null || mi.inData.get("MODE").trim().isEmpty()) ? "0" : mi.inData.get("MODE") as String

    if (validateInput()) {
      Calendar cal = Calendar.getInstance()
      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd")
      DateFormat hourFormat = new java.text.SimpleDateFormat("HHmmss")
      dateFormat.setTimeZone(TimeZone.getTimeZone("EST"))
      currentDate = dateFormat.format(cal.getTime())
      hourFormat.setTimeZone(TimeZone.getTimeZone("EST"))
      currentTime = hourFormat.format(cal.getTime())
      //Get data for 25 inputs
      if (!iIT01.isEmpty()) {
        xWHLO = iWH01
        getATP(iWH01, iIT01, iMO01, iRE01)
      }
      if (!iIT02.isEmpty()) {
        xWHLO = iWH02
        getATP(iWH02, iIT02, iMO02, iRE02)
      }
      if (!iIT03.isEmpty()) {
        xWHLO = iWH03
        getATP(iWH03, iIT03, iMO03, iRE03)
      }
      if (!iIT04.isEmpty()) {
        xWHLO = iWH04
        getATP(iWH04, iIT04, iMO04, iRE04)
      }
      if (!iIT05.isEmpty()) {
        xWHLO = iWH05
        getATP(iWH05, iIT05, iMO05, iRE05)
      }
      if (!iIT06.isEmpty()) {
        xWHLO = iWH06
        getATP(iWH06, iIT06, iMO06, iRE06)
      }
      if (!iIT07.isEmpty()) {
        xWHLO = iWH07
        getATP(iWH07, iIT07, iMO07, iRE07)
      }
      if (!iIT08.isEmpty()) {
        xWHLO = iWH08
        getATP(iWH08, iIT08, iMO08, iRE08)
      }
      if (!iIT09.isEmpty()) {
        xWHLO = iWH09
        getATP(iWH09, iIT09, iMO09, iRE09)
      }
      if (!iIT10.isEmpty()) {
        xWHLO = iWH10
        getATP(iWH10, iIT10, iMO10, iRE10)
      }
      if (!iIT11.isEmpty()) {
        xWHLO = iWH11
        getATP(iWH11, iIT11, iMO11, iRE11)
      }
      if (!iIT12.isEmpty()) {
        xWHLO = iWH12
        getATP(iWH12, iIT12, iMO12, iRE12)
      }
      if (!iIT13.isEmpty()) {
        xWHLO = iWH13
        getATP(iWH13, iIT13, iMO13, iRE13)
      }
      if (!iIT14.isEmpty()) {
        xWHLO = iWH14
        getATP(iWH14, iIT14, iMO14, iRE14)
      }
      if (!iIT15.isEmpty()) {
        xWHLO = iWH15
        getATP(iWH15, iIT15, iMO15, iRE15)
      }
      if (!iIT16.isEmpty()) {
        xWHLO = iWH16
        getATP(iWH16, iIT16, iMO16, iRE16)
      }
      if (!iIT17.isEmpty()) {
        xWHLO = iWH17
        getATP(iWH17, iIT17, iMO17, iRE17)
      }
      if (!iIT18.isEmpty()) {
        xWHLO = iWH18
        getATP(iWH18, iIT18, iMO18, iRE18)
      }
      if (!iIT19.isEmpty()) {
        xWHLO = iWH19
        getATP(iWH19, iIT19, iMO19, iRE19)
      }
      if (!iIT20.isEmpty()) {
        xWHLO = iWH20
        getATP(iWH20, iIT20, iMO20, iRE20)
      }
      if (!iIT21.isEmpty()) {
        xWHLO = iWH21
        getATP(iWH21, iIT21, iMO21, iRE21)
      }
      if (!iIT22.isEmpty()) {
        xWHLO = iWH22
        getATP(iWH22, iIT22, iMO22, iRE22)
      }
      if (!iIT23.isEmpty()) {
        xWHLO = iWH23
        getATP(iWH23, iIT23, iMO23, iRE23)
      }
      if (!iIT24.isEmpty()) {
        xWHLO = iWH24
        getATP(iWH24, iIT24, iMO24, iRE24)
      }
      if (!iIT25.isEmpty()) {
        xWHLO = iWH25
        getATP(iWH25, iIT25, iMO25, iRE25)
      }
    }
  }

  /**
   * Validates the input data to ensure essential parameters are provided and valid.
   *
   * @return True if input is valid, otherwise False.
   */

  public boolean validateInput() {
    //Validate Company Number
    Map < String, String > params = ["CONO": iCONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return false
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)
    return validInput
  }

  /**
   * Retrieves ATP (Available to Promise) data for a given item in a warehouse.
   *
   * @param whlo The warehouse code.
   * @param itno The item number.
   * @param modl The mode.
   * @param reqt The request type.
   */

  public void getATP(String whlo, String itno, String modl, int reqt) {
    String warehouseOmit = getTranslation("EOMITWHLO" + whlo)
    if (warehouseOmit.equals("Y") && !mode.equals("1")) {
      return
    }
    authFlag = false
    ccaFlag = 0
    xxPlnf = 0
    xReqt = 0
    zTime = 0
    xTrqt = 0
    xAqty = 0
    if (!whlo.equals("185")) {
      xWHLO = whlo
    }
    // Check if closeout item, convert to normal item
    if (itno.indexOf("-C") >= 0 && !mode.equals("1")) {
      itno = itno.replace("-C", "")
    }
    //Check Item 
    HashMap < String, String > item = checkItem(itno)
    if (item == null) {
      return
    }
    // Check for warehouse override
    String wOverride = getTranslation("WOVRDE" + item.get("HDPR"))
    if (!wOverride.isEmpty() && !mode.equals("1")) {
      xWHLO = wOverride
    }

    int counter = 0
    Closure < ? > rsReadATP = {
      DBContainer container ->
      String rItno = container.get("MAITNO").toString()
      //read only first record
      if (rItno != null && counter == 0) {
        counter = counter + 1
        atpDate = (container.get("MAPLDT").toString()) as Integer
        atpQty = (container.get("MAAVTP").toString()) as Double
        mitatp = new HashMap < String, String > ()
        mitatp.put("WHLO", container.get("MAWHLO").toString())
        mitatp.put("ITNO", container.get("MAITNO").toString())
        mitatp.put("PLDT", container.get("MAPLDT").toString())
        mitatp.put("AVTP", container.get("MAAVTP").toString())
        ATPFound(xWHLO, itno, modl, atpQty, reqt)
        if (xWHLO.equals("180") && !mode.equals("1")) {
          if (!futureDate180) {
            atpDate180 = atpDate
          }
        }
        if (xWHLO.equals("185") && !mode.equals("1")) {
          if (!futureDate185) {
            atpDate185 = atpDate
          }
          atpQty = atpQty180 + atpQty185

          // Default to earliest ATP Date
          if (atpDate180 < atpDate185) {
            atpDate = atpDate180
          } else {
            atpDate = atpDate185
          }

          if (atpQty < reqt) {
            int curDate = currentDate as Integer
            if (atpDate180 > curDate) {
              if (atpQty180F >= reqt) {
                atpDate180 = atpDate180F
              } else {
                atpDate180 = atpDate180PTF
              }
            } else {
              atpDate180 = 99999999
            }

            if (atpDate185 > curDate) {
              if (atpQty185F >= reqt) {
                atpDate185 = atpDate185F
              } else {
                atpDate185 = atpDate185PTF
              }
            } else {
              atpDate185 = 99999999
            }

            if (atpDate180 <= atpDate185) {
              atpDate = atpDate180
            } else {
              atpDate = atpDate185
            }
          }

          if (atpQty < 0) {
            atpQty = 0
          }

          ccaFlag = 0
          if (atpQty >= reqt && reqt != 0) {
            ccaFlag = 1
          }
        }

        mi.outData.put("ATDT", atpDate as String)
        mi.outData.put("AQTY", atpQty as String)
        mi.outData.put("AUTH", ccaFlag as String)
        mi.outData.put("ITNO", itno)
        if (xWHLO.equals("180") && !mode.equals("1")) {
          xWHLO = "185"
          getATP("185", itno, modl, reqt)
        } else {
          mi.outData.put("AQTY", atpQty as String)
          mi.write()
        }
      }
    }

    ExpressionFactory expression = database.getExpressionFactory("MITATP")
    // set date filter
    expression = expression.le("MAPLDT", currentDate)
    DBAction query = database.table("MITATP").index("00").reverse().matching(expression).selection("MAWHLO", "MAITNO", "MAPLDT", "MATIHM", "MAAVTP").build()
    DBContainer container = query.getContainer()
    container.set("MACONO", iCONO)
    container.set("MAWHLO", xWHLO)
    container.set("MAITNO", itno)
    query.readAll(container, 3, 1, rsReadATP) //limit to one returned record

    if (counter == 0) { //ATP not found
      ATPNotFound(xWHLO, itno, modl, reqt)
      if (xWHLO.equals("180") && !mode.equals("1")) {
        atpQty180 = atpQty
        atpDate180 = atpDate
      }
      if (xWHLO.equals("185") && !mode.equals("1")) {
        atpQty185 = atpQty
        atpDate185 = atpDate
        atpQty = atpQty180 + atpQty185
        //	Default to earliest ATP Date
        if (atpDate180 < atpDate185) {
          atpDate = atpDate180
        } else {
          atpDate = atpDate185
        }

        if (atpQty < reqt) {
          if (atpQty180 < 0 && atpQty185 > 0 || atpQty180 > 0 && atpQty185 < 0) {
            if (atpDate180 > atpDate185) {
              atpDate = atpDate180
            } else {
              atpDate = atpDate185
            }
          }
        }
        if (atpQty < 0) {
          atpQty = 0
        }
        ccaFlag = 0
        if (atpQty >= reqt && reqt != 0) {
          ccaFlag = 1
        }
      }

      mi.outData.put("ATDT", atpDate as String)
      mi.outData.put("AQTY", atpQty as String)
      mi.outData.put("AUTH", ccaFlag as String)
      mi.outData.put("ITNO", itno)
      if (xWHLO.equals("180") && !mode.equals("1")) {
        xWHLO = "185"
        getATP("185", itno, modl, reqt)
      } else {
        mi.write()
      }
    }
  }

  /**
   * Checks the details of an item in the database.
   *
   * @param itno The item number.
   * @return A HashMap containing item details if found, otherwise null.
   */

  public HashMap < String, String > checkItem(String itno) {
    HashMap < String, String > item = null
    DBAction query = database.table("MITMAS").index("00").selection("MMITNO", "MMSTAT", "MMHDPR").build()
    DBContainer container = query.getContainer()
    container.set("MMCONO", iCONO)
    container.set("MMITNO", itno)
    if (query.read(container)) {
      item = new HashMap < String, String > ()
      item.put("ITNO", container.get("MMITNO").toString().trim())
      item.put("STAT", container.get("MMSTAT").toString().trim())
      item.put("HDPR", container.get("MMHDPR").toString().trim())
    }
    return item
  }
  private boolean futureDate180, futureDate185
  private double atpQty180, atpQty185, atpQty180F, atpQty185F
  private int atpDate180, atpDate185, atpDate180F, atpDate185F, atpDate180PTF, atpDate185PTF

  /**
   * Handles the scenario when ATP is found for an item.
   *
   * @param whlo    The warehouse number.
   * @param itno    The item number.
   * @param modl    The model.
   * @param avtp    Available to promise quantity.
   * @param xxReqt  Required quantity.
   */

  public void ATPFound(String whlo, String itno, String modl, double avtp, double xxReqt) {
    if (whlo.equals("180") && !mode.equals("1")) {
      futureDate180 = false
      atpQty180 = 0
      atpQty180F = 0
      atpDate180 = 0
      atpDate180F = 0
      atpDate180PTF = 0
    }
    if (whlo.equals("185") && !mode.equals("1")) {
      futureDate185 = false
      atpQty185 = 0
      atpQty185F = 0
      atpDate185 = 0
      atpDate185F = 0
      atpDate185PTF = 0
    }
    atpDate = currentDate as Integer
    if (avtp > 0 || (whlo.equals("180") && !mode.equals("1")) || (whlo.equals("185") && !mode.equals("1"))) {
      atpQty = avtp
    }
    double ATPqtyXX = 0
    if (whlo.equals("180") && !mode.equals("1")) {
      atpQty180 = avtp
      DBAction query = database.table("MITBAL").index("00").selection("MBLEAT", "MBSTQT", "MBREQT").build()
      DBContainer container = query.getContainer()
      container.set("MBCONO", iCONO)
      container.set("MBITNO", itno)
      container.set("MBWHLO", whlo)
      if (query.read(container)) {
        int leadTime = (container.get("MBLEAT").toString()) as Integer
        calculateATPDate(leadTime)
        setCCAuthFlag(itno, whlo, xxReqt)
        atpDate180PTF = atpDate
        atpDate180 = atpDate
      }
      atpDate = currentDate as Integer
    }

    if (whlo.equals("185") && !mode.equals("1")) {
      atpQty185 = avtp
      ATPqtyXX = atpQty180 + avtp
      DBAction query = database.table("MITBAL").index("00").selection("MBLEAT", "MBSTQT", "MBREQT").build()
      DBContainer container = query.getContainer()
      container.set("MBCONO", iCONO)
      container.set("MBITNO", itno)
      container.set("MBWHLO", whlo)
      if (query.read(container)) {
        int leadTime = (container.get("MBLEAT").toString()) as Integer
        calculateATPDate(leadTime)
        setCCAuthFlag(itno, whlo, xxReqt)
        atpDate185PTF = atpDate
        atpDate185 = atpDate
      }
      atpDate = currentDate as Integer
    }

    if (avtp >= xxReqt || ATPqtyXX >= xxReqt) {
      getATPDate(whlo, modl)
      setCCAuthFlag(itno, whlo, xxReqt)
      return
    }

    if (avtp < xxReqt) {
      int counter = 0
      Closure < ? > rsReadATP = {
        DBContainer container ->
        String rItno = container.get("MAITNO").toString()
        //read only first record
        if (rItno != null && counter == 0) {
          String xxWhlo = container.get("MAWHLO").toString().trim()
          double xxAtpQty = (container.get("MAAVTP").toString()) as Double
          int planDate = (container.get("MAPLDT").toString()) as Integer
          int curDate = currentDate as Integer
          if (!(xxWhlo.equals("180") && !mode.equals("1")) && !(xxWhlo.equals("185") && !mode.equals("1"))) {
            if (xxAtpQty >= xxReqt) {
              atpDate = planDate as Integer
              getATPDate(whlo, modl)
              setCCAuthFlag(itno, whlo, xxReqt)
              counter = 1 //Set to break
              return
            }
          } else {
            if (xxAtpQty >= xxReqt && planDate > curDate) {
              if (xxWhlo.equals("180") && !mode.equals("1")) {
                futureDate180 = true
                atpQty180F = xxAtpQty
              } else {
                futureDate185 = true
                atpQty185F = xxAtpQty
              }
              atpDate = planDate as Integer
              if (planDate != 0) {
                getATPDate(whlo, modl)
                setCCAuthFlag(itno, whlo, xxReqt)
                if (xxWhlo.equals("180") && !mode.equals("1")) {
                  atpDate180F = atpDate
                  atpDate180 = atpDate
                } else {
                  atpDate185F = atpDate
                  atpDate185 = atpDate
                }
              }
              counter = 1 //Set to break
              return
            }
          }
        }
      }
      DBAction query = database.table("MITATP").index("00").selection("MAWHLO", "MAITNO", "MAPLDT", "MATIHM", "MAAVTP").build()
      DBContainer container = query.getContainer()
      container.set("MACONO", iCONO)
      container.set("MAWHLO", whlo)
      container.set("MAITNO", itno)
      query.readAll(container, 3, maxPageSize, rsReadATP)
      if (counter == 1) {
        return
      }
      if ((whlo.equals("180") && !mode.equals("1")) || (whlo.equals("185") && !mode.equals("1"))) {
        if (whlo.equals("180")) {
          futureDate180 = true
        } else {
          futureDate185 = true
        }
        return
      } else {
        DBAction query1 = database.table("MITBAL").index("00").selection("MBLEAT", "MBSTQT", "MBREQT").build()
        DBContainer container1 = query1.getContainer()
        container1.set("MBCONO", iCONO)
        container1.set("MBITNO", itno)
        container1.set("MBWHLO", whlo)
        if (query1.read(container1)) {
          int leadTime = (container1.get("MBLEAT").toString()) as Integer
          calculateATPDate(leadTime)
          setCCAuthFlag(itno, whlo, xxReqt)
          return
        }
      }
    }
  }
  /**
   * Calculates the ATP (Available to Promise) date based on lead time.
   *
   * @param leadTime The lead time in days.
   */

  public void calculateATPDate(int leadTime) {
    Map < String, String > params = ["FRDT": currentDate, "DAYS": leadTime as String]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.TODT != null) {
        if (authFlag) {
          xxPlnf = response.TODT as Integer
        } else {
          atpDate = response.TODT as Integer
        }
      }
    }
    miCaller.call("CRS900MI", "AddWorkingDays", params, callback)
  }

  /**
   * Sets the credit card authorization flag based on certain conditions.
   *
   * @param itno   The item number.
   * @param whlo   The warehouse number.
   * @param xxReqt The required quantity.
   */

  public void setCCAuthFlag(String itno, String whlo, double xxReqt) {
    int curDate = currentDate as Integer
    if (atpDate == curDate) {
      ccaFlag = 1
      return
    }
    ccaFlag = 0
    DBAction query = database.table("MITBAL").index("00").selection("MBLEAT", "MBSTQT", "MBREQT", "MBAVAL").build()
    DBContainer container = query.getContainer()
    container.set("MBCONO", iCONO)
    container.set("MBITNO", itno)
    container.set("MBWHLO", whlo)
    if (query.read(container)) {
      double xAlqt = 0
      double aval = (container.get("MBAVAL").toString().trim()) as Double
      if (aval >= xxReqt) {
        double xTrqt = 0
        double stqt = (container.get("MBSTQT").toString().trim()) as Double
        double reqt = (container.get("MBREQT").toString().trim()) as Double
        xAlqt = stqt - reqt
        authFlag = true
        int leadTime = (container.get("MBLEAT").toString()) as Integer
        calculateATPDate(leadTime)
        //int counter = 0
        Closure < ? > rsReadMITPLO = {
          DBContainer container2 ->
          String rItno = container2.get("MOITNO").toString().trim()
          if (rItno != null) {
            String rOrca = container2.get("MOORCA").toString().trim()
            int rPldt = (container2.get("MOPLDT").toString().trim()) as Integer
            double rTrqt = (container2.get("MOTRQT").toString().trim()) as Double
            if (rPldt < xxPlnf) {
              xTrqt = rTrqt
              if (rTrqt < 0) {
                xTrqt = rTrqt * -1
              }
              xAlqt = xAlqt - xTrqt
            }
          }
        }
        DBAction query1 = database.table("MITPLO").index("00").selection("MOWHLO", "MOITNO", "MOORCA", "MOPLDT", "MOTRQT").build()
        DBContainer container1 = query1.getContainer()
        container1.set("MOCONO", iCONO)
        container1.set("MOWHLO", whlo)
        container1.set("MOITNO", itno)
        query1.readAll(container1, 3, maxPageSize, rsReadMITPLO)

      }
      if (xAlqt >= xxReqt) {
        ccaFlag = 1
      }
    }
  }

  /**
   * Retrieves the ATP (Available to Promise) date based on certain conditions.
   *
   * @param whlo The warehouse number.
   * @param modl The model.
   */

  public void getATPDate(String whlo, String modl) {
    int zzTime = 1
    int cutOffTime = 180000
    String cutOffSetting = getTranslation("CO" + whlo + modl)
    if (cutOffSetting.isEmpty()) {
      cutOffSetting = getTranslation("CO" + whlo)
    }
    if (!cutOffSetting.isEmpty()) {
      cutOffTime = cutOffSetting as Integer
    }
    int count = 0
    Closure < ? > rsReadCSYCAL = {
      DBContainer container ->
      String strDate = container.get("CDYMD8")
      if (strDate != null && count == 0) {
        int ddday = (container.get("CDDDAY").toString().trim()) as Integer
        if (ddday == 1 && zzTime < cutOffTime) {
          count = 1
          atpDate = strDate.trim() as Integer
        }
      }
    }
    ExpressionFactory expression = database.getExpressionFactory("CSYCAL")
    // set date filter
    expression = expression.ge("CDYMD8", atpDate as String)
    DBAction query = database.table("CSYCAL").index("00").matching(expression).selection("CDDDAY", "CDYMD8").build()
    DBContainer container = query.getContainer()
    container.set("CDCONO", iCONO)
    container.set("CDDIVI", iDIVI)
    int limit = 10 // 10 days only
    query.readAll(container, 2, limit, rsReadCSYCAL)
  }

  /**
   * Handles the scenario when ATP (Available to Promise) is not found for an item.
   *
   * @param whlo   The warehouse number.
   * @param itno   The item number.
   * @param modl   The model.
   * @param xxReqt The required quantity.
   */

  public void ATPNotFound(String whlo, String itno, String modl, double xxReqt) {
    DBAction query = database.table("MITBAL").index("00").selection("MBLEAT", "MBSTQT", "MBREQT").build()
    DBContainer container = query.getContainer()
    container.set("MBCONO", iCONO)
    container.set("MBITNO", itno)
    container.set("MBWHLO", whlo)
    double xxAqty = 0
    if (query.read(container)) {
      int leadTime = (container.get("MBLEAT").toString()) as Integer
      double stqt = (container.get("MBSTQT").toString().trim()) as Double
      double reqt = (container.get("MBREQT").toString().trim()) as Double
      xxAqty = stqt - reqt
      if (xxAqty > 0 || (whlo.equals("180") && !mode.equals("1")) || (whlo.equals("185") && !mode.equals("1"))) {
        atpQty = xxAqty
      }

      double ATPqtyXX = 0
      if (whlo.equals("185") && !mode.equals("1")) {
        ATPqtyXX = atpQty180 + xxAqty
      }

      if (xxAqty >= xxReqt || ATPqtyXX >= xxReqt) {
        atpDate = currentDate as Integer
        getATPDate(whlo, modl)
      }

      if (xxAqty < xxReqt || ATPqtyXX < xxReqt) {
        calculateATPDate(leadTime)
      }
      setCCAuthFlag(itno, whlo, xxReqt)
    }
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