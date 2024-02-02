
/**
* README
* Get a record from the table EXTOOH
*
* Name: EXT012MI.GetHeaderInfo
* Description: Get a record from the EXTOOH table
* Date	      Changed By            Description
* 20230815	  NATARAJKB        Get a record from the EXTOOH table
*
*/


public class GetHeaderInfo extends ExtendM3Transaction {
	private final MIAPI mi
	private final DatabaseAPI database
	private final LoggerAPI logger
	private final ProgramAPI program
	private final UtilityAPI utility

	//Input fields
	private String iCONO
	private String iORNO


	public GetHeaderInfo(MIAPI mi, DatabaseAPI database, LoggerAPI logger, ProgramAPI program, UtilityAPI utility) {
		this.mi = mi
		this.database = database
		this.logger = logger
		this.program = program
		this.utility = utility
	}

	/**
	 * Main method
	 * @param
	 * @return
	 */
	public void main() {

		iCONO = mi.inData.get("CONO") == null ? "" : mi.inData.get("CONO")
		iORNO = mi.inData.get("ORNO") == null ? "" : mi.inData.get("ORNO")

		getRecord()
	}
	
	/**
	 * Gets a record from the EXTOOH table
	 *
	 */
	private void getRecord(){
		DBAction action = database.table("EXTOOH").index("00").selectAllFields().build()
		DBContainer container = action.getContainer()

		container.set("EXCONO", iCONO.toInteger())
		container.set("EXORNO", iORNO.toString())

		// Fetch the information form the table and output it
		if (action.read(container)) {
			mi.outData.put("CONO", container.get("EXCONO").toString())
			mi.outData.put("ORNO", container.get("EXORNO").toString())
			mi.outData.put("CEDI", container.get("EXCEDI").toString())
			mi.outData.put("TPID", container.get("EXTPID").toString())
			mi.outData.put("PYNO", container.get("EXPYNO").toString())
			mi.outData.put("DEPT", container.get("EXDEPT").toString())
			mi.outData.put("VEND", container.get("EXVEND").toString())
			mi.outData.put("FOBC", container.get("EXFOBC").toString())
			mi.outData.put("ADST", container.get("EXADST").toString())
			mi.outData.put("ADBT", container.get("EXADBT").toString())
			mi.outData.put("ADDS", container.get("EXADDS").toString())
			mi.outData.put("CANR", container.get("EXCANR").toString())
			mi.outData.put("SDIN", container.get("EXSDIN").toString())
			mi.outData.put("CDRP", container.get("EXCDRP").toString())
			mi.outData.put("REZA", container.get("EXREZA").toString())
			mi.outData.put("RELO", container.get("EXRELO").toString())
			mi.outData.put("CUOR", container.get("EXCUOR").toString())
			mi.outData.put("SDNR", container.get("EXSDNR").toString())
			mi.outData.put("ROUT", container.get("EXROUT").toString())
			mi.outData.put("RLSN", container.get("EXRLSN").toString())
			mi.outData.put("CUNF", container.get("EXCUNF").toString())
			mi.outData.put("STOT", container.get("EXSTOT").toString())
			mi.outData.put("STLN", container.get("EXSTLN").toString())
			mi.outData.put("STQT", container.get("EXSTQT").toString())
			mi.outData.put("STWT", container.get("EXSTWT").toString())
			mi.outData.put("STUM", container.get("EXSTUM").toString())
			mi.outData.put("STVL", container.get("EXSTVL").toString())
			mi.outData.put("STVU", container.get("EXSTVU").toString())

			mi.outData.put("RGDT", container.getInt("EXRGDT").toString())
			mi.outData.put("RGTM", container.getInt("EXRGTM").toString())
			mi.outData.put("LMDT", container.getInt("EXLMDT").toString())
			mi.outData.put("CHNO", container.getInt("EXCHNO").toString())
			mi.outData.put("CHID", container.get("EXCHID").toString())
			mi.outData.put("LMTS", container.get("EXLMTS").toString())
			
            mi.write()
		} else {
		  mi.error("Record does not exist")
		  return
		}

	}
}
