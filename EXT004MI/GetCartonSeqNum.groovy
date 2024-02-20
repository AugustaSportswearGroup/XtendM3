/**
 * README
 * This extension is being used to get the carton sequence Number from M3
 *
 * Name: EXT004MI.GetCartonSeqNum
 * Description:  Get the carton sequence Number from M3
 * Date	      Changed By                 Description
 *20230605  SuriyaN@fortude.co     Get the carton sequence Number from M3
 *20240212  AbhishekA@fortude.co   Updating Validation logic
 */

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class GetCartonSeqNum extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller

  private String iWHLO, iCAMU, m3CAMU, cartonSeqNum
  private int iCONO
  private boolean validInput = true, containerExists = false

  public GetCartonSeqNum(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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

    validateInput()
    if(validInput)
    {
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
    Map<String, String> params = ["CONO": iCONO.toString().trim()]
    Closure<?> callback = {
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
    
  }
  
  /**
   *SetOutput
   * @params
   * @return
   */
  public setOutput() {
    updateMBMTRDRecord()
    mi.outData.put("CISN", cartonSeqNum)
  }
  
  /**
   *Update records to MBMTRD table
   * @params 
   * @return 
   */
  public updateMBMTRDRecord() {
    DBAction query = database.table("MBMTRD").index("00").selection("TDTX15").build()
    DBContainer container = query.getContainer()
    container.set("TDCONO", iCONO)
    container.set("TDDIVI", "")
    container.set("TDIDTR", 254)
    container.set("TDMVXP", "")
    container.set("TDEXTP", "")
    container.set("TDMVXD", "CISN" + iWHLO)
    container.set("TDMBMD", "CISN" + iWHLO)
    if (query.read(container)) {
      query.readLock(container, updateCallBack)
    } else {
      insertMBMTRDRecord()
      return
      }
  }

  Closure<?> updateCallBack = {
    LockedResult lockedResult ->
    String cisn = lockedResult.get("TDTX15")
    cisn = cisn.substring(0, 6)
		String firstDigits = cisn.substring(0, 3)
		if (firstDigits.isInteger() == false) {
			firstDigits = "001"
		} else {
			int fd = Integer.parseInt(firstDigits)
			fd = fd + 1;
			if (fd >= 998) {
				firstDigits = "001"
			} else {
				firstDigits = "000" + fd
				firstDigits = firstDigits.substring(firstDigits.length() - 3)
			}
		}

		String lastDigits = cisn.substring(3);
		if (lastDigits.isInteger() == false) {
			lastDigits = "999"
		} else {
			int ld = Integer.parseInt(lastDigits)
			ld = ld - 1
			if (ld <= 1) {
				lastDigits = "999"
			} else {
				lastDigits = "000" + ld
				lastDigits = lastDigits.substring(lastDigits.length() - 3)
			}
		}

		cartonSeqNum = firstDigits + lastDigits
		lockedResult.set("TDTX15", cartonSeqNum)
    lockedResult.set("TDCHNO", Integer.parseInt(firstDigits))
    lockedResult.set("TDLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    lockedResult.set("TDCHID", program.getUser())
    lockedResult.update()
  }
    
  public insertMBMTRDRecord() {
    DBAction action = database.table("MBMTRD").index("00").build()
    DBContainer query = action.getContainer()
    cartonSeqNum = "001999"
    query.set("TDCONO", iCONO)
    query.set("TDDIVI", "")
    query.set("TDIDTR", 254)
    query.set("TDMVXP", "")
    query.set("TDEXTP", "")
    query.set("TDMVXD", "CISN" + iWHLO)
    query.set("TDMBMD", "CISN" + iWHLO)
    query.set("TDTX15", cartonSeqNum)
    query.set("TDTX40", "CISN" + iWHLO)
    query.set("TDRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("TDRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    query.set("TDLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    query.set("TDCHNO", 1)
    query.set("TDCHID", program.getUser())
    action.insert(query, recordExists)
  }

  Closure recordExists = {
    mi.error("Record Already Exists")
    return
  }
}