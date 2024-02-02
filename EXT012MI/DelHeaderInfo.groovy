
/**
* README
* Deletes a record from the table EXTOOH
*
* Name: EXT012MI.DelHeaderInfo
* Description: Deletes a record from the EXTOOH table
* Date	      Changed By            Description
* 20230815	  NATARAJKB        Deletes a record from the EXTOOH table
*
*/


public class DelHeaderInfo extends ExtendM3Transaction {
	private final MIAPI mi
	private final DatabaseAPI database
	private final LoggerAPI logger
	private final ProgramAPI program
	private final UtilityAPI utility

	//Input fields
	private String iCONO, iORNO


	public DelHeaderInfo(MIAPI mi, DatabaseAPI database, LoggerAPI logger, ProgramAPI program, UtilityAPI utility) {
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

		iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
		iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
		deleteRecord()
		
	}
	
	/**
	 * Deletes a record from the EXTOOH table
	 *
	 */
	private void deleteRecord(){
		DBAction action = database.table("EXTOOH").index("00").selection("CONO", "ORNO").build()
		DBContainer container = action.getContainer()
		
		container.set("EXCONO", iCONO as Integer)
		container.set("EXORNO", iORNO)
		
		if (!action.readLock(container, deleteCallBack)) {
		  mi.error("Record does not exist")
		  return
		}
	}
  /**
	 * Closure for table EXTOOH deletion
	 */
	Closure<?> deleteCallBack = { LockedResult lockedResult ->
		lockedResult.delete()
	}
}
