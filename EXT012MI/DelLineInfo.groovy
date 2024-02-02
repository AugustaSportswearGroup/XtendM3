
/**
* README
* Deletes a record from the table EXTOOL
*
* Name: EXT012MI.DelLineInfo
* Description: Deletes a record from the EXTOOL table
* Date	      Changed By            Description
* 20230815	  NATARAJKB        Deletes a record from the EXTOOL table
*
*/


public class DelLineInfo extends ExtendM3Transaction {
	private final MIAPI mi
	private final DatabaseAPI database
	private final LoggerAPI logger
	private final ProgramAPI program
	private final UtilityAPI utility

	//Input fields
	private String iCONO, iORNO, iPONR, iPOSX


	public DelLineInfo(MIAPI mi, DatabaseAPI database, LoggerAPI logger, ProgramAPI program, UtilityAPI utility) {
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
		
		iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
		iPOSX = (mi.inData.get("POSX") == null || mi.inData.get("POSX").trim().isEmpty()) ? 0 : mi.inData.get("POSX") as Integer
		
		deleteRecord()
	}
	
	/**
	 * Deletes a record from the EXTOOL table
	 *
	 */
	private void deleteRecord(){
		DBAction action = database.table("EXTOOL").index("00").selection("CONO", "ORNO", "PONR", "POSX").build()
		DBContainer container = action.getContainer()

		container.set("EXCONO", iCONO as Integer)
		container.set("EXORNO", iORNO)
		container.set("EXPONR", iPONR as Integer)
		container.set("EXPOSX", iPOSX as Integer)


		if (!action.readLock(container, deleteCallBack)) {
		  mi.error("Record does not exist")
		  return
		}

	}
    /**
	 * Closure for table EXTOOL deletion
	 */

	Closure<?> deleteCallBack = { LockedResult lockedResult ->
		lockedResult.delete()
	}
}
