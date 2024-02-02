
/**
* README
* Get a record from the table EXTOOL
*
* Name: EXT012MI.GetLineInfo
* Description: Get a record from the EXTOOL table
* Date	      Changed By            Description
* 20230815	  NATARAJKB        Get a record from the EXTOOL table
*
*/


public class LstLineInfo extends ExtendM3Transaction {
	private final MIAPI mi
	private final DatabaseAPI database
	private final LoggerAPI logger
	private final ProgramAPI program
	private final UtilityAPI utility
	private final MICallerAPI miCaller

	//Input fields
	private int iCONO
	private String iORNO
	private boolean validInput = true


	public LstLineInfo(MIAPI mi, DatabaseAPI database, LoggerAPI logger, ProgramAPI program, UtilityAPI utility, MICallerAPI miCaller) {
		this.mi = mi
		this.database = database
		this.logger = logger
		this.program = program
		this.utility = utility
		this.miCaller = miCaller
	}

	/**
	 * Main method
	 * @param
	 * @return
	 */
	public void main() {
    
		iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
		iORNO = mi.inData.get("ORNO") == null ? "" : mi.inData.get("ORNO")
    validateInput()
    if (validInput) {
		getRecord()
    }
	}
	
	/**
   *Validate Records
   * @params
   * @return 
   */
  public validateInput() {
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

    //Validate Order Number
    if (!iORNO.toString().trim().isEmpty()) {
      params = ["ORNO": iORNO.toString().trim()]
      callback = {
        Map < String,
        String > response ->
        if (response.ORNO == null) {
          mi.error("Invalid Order Number " + iORNO)
          validInput = false
          return false
        }
      }
      miCaller.call("OIS100MI", "GetOrderHead", params, callback)
    }
  }
	
	/**
	 * Gets a record from the EXTOOL table
	 *
	 */
	private void getRecord(){
		DBAction action = database.table("EXTOOL").index("00").selectAllFields().build()
		DBContainer container = action.getContainer()

		container.set("EXCONO", iCONO.toInteger())
		container.set("EXORNO", iORNO.toString())
		action.readAll(container,1, callback);
	}

		// Fetch the information form the table and output it
		Closure<?> callback = {DBContainer container ->
			mi.outData.put("CONO", container.get("EXCONO").toString())
			mi.outData.put("ORNO", container.get("EXORNO").toString())
			mi.outData.put("PONR", container.get("EXPONR").toString())
			mi.outData.put("POSX", container.get("EXPOSX").toString())
			mi.outData.put("LISN", container.get("EXLISN").toString())
			mi.outData.put("BYPN", container.get("EXBYPN").toString())
			mi.outData.put("VNPN", container.get("EXVNPN").toString())
			mi.outData.put("CPCN", container.get("EXCPCN").toString())
			mi.outData.put("EANN", container.get("EXEANN").toString())
			mi.outData.put("GTIN", container.get("EXGTIN").toString())
			mi.outData.put("UPCC", container.get("EXUPCC").toString())
			mi.outData.put("ORQT", container.get("EXORQT").toString())
			mi.outData.put("QTUM", container.get("EXQTUM").toString())
			mi.outData.put("PUPR", container.get("EXPUPR").toString())
			mi.outData.put("SHDT", container.get("EXSHDT").toString())
			mi.outData.put("LNDT", container.get("EXLNDT").toString())
			
			mi.outData.put("RGDT", container.getInt("EXRGDT").toString())
			mi.outData.put("RGTM", container.getInt("EXRGTM").toString())
			mi.outData.put("LMDT", container.getInt("EXLMDT").toString())
			mi.outData.put("CHNO", container.getInt("EXCHNO").toString())
			mi.outData.put("CHID", container.get("EXCHID").toString())
			mi.outData.put("LMTS", container.get("EXLMTS").toString())
			
      mi.write()
	}
}
