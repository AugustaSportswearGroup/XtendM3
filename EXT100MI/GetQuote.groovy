/**
 * README
 * This extension is being used to get fright charge based on the delivery method, customer and the order amount
 *
 * Name: EXT100.GetQuote
 * Description:This extension is being used to get fright charge based on the delivery method, customer and the order amount
 * Date	             Changed By                      Description
 * 20230210        SuriyaN@fortude.co           Adding records to EXTWHL table
 * 20240208        AbhishekA@fortude.co         Updating Validation logic
 *
 */

import java.time.LocalDate
import java.time.format.DateTimeFormatter

public class GetQuote extends ExtendM3Transaction {
  private final MIAPI mi
  private final MICallerAPI miCaller

  private String iMODL, iCUNO
  private double iNTAM, limit = 0

  public GetQuote(MIAPI mi, MICallerAPI miCaller) {
    this.mi = mi
    this.miCaller = miCaller
  }

  public void main() {
    iMODL = (mi.inData.get("MODL") == null || mi.inData.get("MODL").trim().isEmpty()) ? "" : mi.inData.get("MODL")
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")

    iNTAM = (mi.inData.get("NTAM") == null || mi.inData.get("NTAM").trim().isEmpty()) ? 0 : mi.inData.get("NTAM") as double

    this.readFreightRecords()
    if (this.limit == 0) {
      this.readFreightRecordsWitoutCustomer()
    }

    mi.error(this.limit.toString())
    if (this.limit >= 0) {
      this.getChargeAmount()
    } else {
      mi.outData.put("CRAM", "0.00")
    }
  }

  public void readFreightRecords() {

    List<String> chargeAmounts = new ArrayList<>()
    Map < String, String > params = ["FILE": "ZZCHRG", "PK01": iMODL.toString().trim(), "PK02": iCUNO]
    
    LocalDate today = LocalDate.now()
    int currentDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd")) as Integer

    Closure < ? > callback = {
      Map < String,
      String > response ->
      int validDate = response.N096 as Integer
      if (currentDate >= validDate) {
        chargeAmounts.add(response.PK03)
      }
    }

    miCaller.call("CUSEXTMI", "LstFieldValue", params, callback)
    for (int i = 0; i < chargeAmounts.size(); i++) {
      double currentLimit = chargeAmounts[i] as double
      if (iNTAM <= currentLimit) {
        if (iNTAM == currentLimit) {
          this.limit = currentLimit
        }
        break;
      } else {
        this.limit = currentLimit
      }
    }
  }

  public void readFreightRecordsWitoutCustomer() {

    List<String> chargeAmounts = new ArrayList<>()
    Map < String, String > params = ["FILE": "ZZCHRG", "PK01": iMODL.toString().trim()]

    LocalDate today = LocalDate.now()
    int currentDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd")) as Integer

    Closure < ? > callback = {
      Map < String,
      String > response ->
      int validDate = response.N096 as Integer
      if (response.PK02 == "" && currentDate >= validDate) {
        chargeAmounts.add(response.PK03)
      }
    }

    miCaller.call("CUSEXTMI", "LstFieldValue", params, callback)

    for (int i = 0; i < chargeAmounts.size(); i++) {
      double currentLimit = chargeAmounts[i] as double
      if (iNTAM <= currentLimit) {
        if (iNTAM == currentLimit) {
          this.limit = currentLimit
        }
        break;
      } else {
        this.limit = currentLimit
      }
    }
  }

  public void getChargeAmount() {

    List<String> chargeAmounts = new ArrayList<>()
    String strLimit = this.limit.toString()
    String formatedLimit = String.format("%.2f", strLimit)
    
    Map < String, String > params = ["FILE": "ZZCHRG", "PK01": iMODL.toString().trim(), "PK02": iCUNO, "PK03": formatedLimit]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      mi.outData.put("CRAM", response.N196)
    }

    miCaller.call("CUSEXTMI", "GetFieldValue", params, callback)

  }

}