/**
* README
* Add a record to the table EXTOOH
*
* Name: EXT012MI.AddHeaderInfo
* Description: Add a record to the EXTOOH table
* Date	      Changed By            Description
* 20230815	  NATARAJKB        Add a record to the EXTOOH table
*
*/
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class AddHeaderInfo extends ExtendM3Transaction {
	private final MIAPI mi
	private final DatabaseAPI database
	private final LoggerAPI logger
	private final ProgramAPI program
	private final UtilityAPI utility

	//Input fields
	private String iORNO, iCEDI, iTPID, iPYNO, iDEPT, iVEND,iFOBC, iADST, iADBT, iADDS, iCANR, iSDIN, iCDRP, iREZA, iRELO, iCUOR, iSDNR, iROUT, iRLSN, iCUNF, iSTUM, iSTVU, iADBY, iUNID
	private int iCONO, iSTLN, iSTQT
	private double iSTOT, iSTWT, iSTVL

	public AddHeaderInfo(MIAPI mi, DatabaseAPI database, LoggerAPI logger, ProgramAPI program, UtilityAPI utility) {
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
		iCEDI = (mi.inData.get("CEDI") == null || mi.inData.get("CEDI").trim().isEmpty()) ? "" : mi.inData.get("CEDI") 
    iTPID = (mi.inData.get("TPID") == null || mi.inData.get("TPID").trim().isEmpty()) ? "" : mi.inData.get("TPID")
    iPYNO = (mi.inData.get("PYNO") == null || mi.inData.get("PYNO").trim().isEmpty()) ? "" : mi.inData.get("PYNO")
    iDEPT = (mi.inData.get("DEPT") == null || mi.inData.get("DEPT").trim().isEmpty()) ? "" : mi.inData.get("DEPT")
    iVEND = (mi.inData.get("VEND") == null || mi.inData.get("VEND").trim().isEmpty()) ? "" : mi.inData.get("VEND")
    iFOBC = (mi.inData.get("FOBC") == null || mi.inData.get("FOBC").trim().isEmpty()) ? "" : mi.inData.get("FOBC")
    iADST = (mi.inData.get("ADST") == null || mi.inData.get("ADST").trim().isEmpty()) ? "" : mi.inData.get("ADST")
    iADBT = (mi.inData.get("ADBT") == null || mi.inData.get("ADBT").trim().isEmpty()) ? "" : mi.inData.get("ADBT")
    iADDS = (mi.inData.get("ADDS") == null || mi.inData.get("ADDS").trim().isEmpty()) ? "" : mi.inData.get("ADDS")
    iADBY = (mi.inData.get("ADBY") == null || mi.inData.get("ADBY").trim().isEmpty()) ? "" : mi.inData.get("ADBY")
    iUNID = (mi.inData.get("UNID") == null || mi.inData.get("UNID").trim().isEmpty()) ? "" : mi.inData.get("UNID")
    iCANR = (mi.inData.get("CANR") == null || mi.inData.get("CANR").trim().isEmpty()) ? "" : mi.inData.get("CANR")
    iSDIN = (mi.inData.get("SDIN") == null || mi.inData.get("SDIN").trim().isEmpty()) ? "" : mi.inData.get("SDIN")
    iCDRP = (mi.inData.get("CDRP") == null || mi.inData.get("CDRP").trim().isEmpty()) ? "" : mi.inData.get("CDRP")
    iREZA = (mi.inData.get("REZA") == null || mi.inData.get("REZA").trim().isEmpty()) ? "" : mi.inData.get("REZA")
    iRELO = (mi.inData.get("RELO") == null || mi.inData.get("RELO").trim().isEmpty()) ? "" : mi.inData.get("RELO")
    iCUOR = (mi.inData.get("CUOR") == null || mi.inData.get("CUOR").trim().isEmpty()) ? "" : mi.inData.get("CUOR")
    iSDNR = (mi.inData.get("SDNR") == null || mi.inData.get("SDNR").trim().isEmpty()) ? "" : mi.inData.get("SDNR")
    iROUT = (mi.inData.get("ROUT") == null || mi.inData.get("ROUT").trim().isEmpty()) ? "" : mi.inData.get("ROUT")
    iRLSN = (mi.inData.get("RLSN") == null || mi.inData.get("RLSN").trim().isEmpty()) ? "" : mi.inData.get("RLSN")
    iCUNF = (mi.inData.get("CUNF") == null || mi.inData.get("CUNF").trim().isEmpty()) ? "" : mi.inData.get("CUNF")
    iSTUM = (mi.inData.get("STUM") == null || mi.inData.get("STUM").trim().isEmpty()) ? "" : mi.inData.get("STUM")
    iSTVU = (mi.inData.get("STVU") == null || mi.inData.get("STVU").trim().isEmpty()) ? "" : mi.inData.get("STVU")
    
    iSTOT = (mi.inData.get("STOT") == null || mi.inData.get("STOT").trim().isEmpty()) ? 0 : mi.inData.get("STOT") as Double
    iSTLN = (mi.inData.get("STLN") == null || mi.inData.get("STLN").trim().isEmpty()) ? 0 : mi.inData.get("STLN") as Integer
    iSTQT = (mi.inData.get("STQT") == null || mi.inData.get("STQT").trim().isEmpty()) ? 0 : mi.inData.get("STQT") as Integer
    iSTWT = (mi.inData.get("STWT") == null || mi.inData.get("STWT").trim().isEmpty()) ? 0 : mi.inData.get("STWT") as Double
    iSTVL = (mi.inData.get("STVL") == null || mi.inData.get("STVL").trim().isEmpty()) ? 0 : mi.inData.get("STVL") as Double
    
    //Validate ORNO
    if (!validORNO(iCONO,iORNO)){
      mi.error("Order ${iORNO} does not exist");
      return;
    }else {
      insertRecord();
    }
    
	}
	
	/**
  * Validate ORNO
  **/
  private boolean validORNO(int CONONo, String ORNONo){
    DBAction dbAction = database.table("OXHEAD").index("00").build();
    DBContainer OOHEAD = dbAction.createContainer();
    OOHEAD.set("OACONO", CONONo as Integer);
    OOHEAD.set("OAORNO", ORNONo);
    if(dbAction.read(OOHEAD)){
      return true;
    } else {
      return false;
    }
  }
  
	/**
	 * Inserts record in the EXTOOH table
	 *
	 */
	private void insertRecord(){
		DBAction action = database.table("EXTOOH").index("00").selection("CONO", "ORNO").build()
		DBContainer container = action.getContainer()

		container.set("EXCONO", iCONO as Integer)
		container.set("EXORNO", iORNO)
		container.set("EXCEDI", iCEDI)
		container.set("EXTPID", iTPID)
		container.set("EXPYNO", iPYNO)
		container.set("EXDEPT", iDEPT)
		container.set("EXVEND", iVEND)
		container.set("EXFOBC", iFOBC)
		container.set("EXADST", iADST)
		container.set("EXADBT", iADBT)
		container.set("EXADDS", iADDS)
		container.set("EXADBY", iADBY)
		container.set("EXUNID", iUNID)
		container.set("EXCANR", iCANR)
		container.set("EXSDIN", iSDIN)
		container.set("EXCDRP", iCDRP)
		container.set("EXREZA", iREZA)
		container.set("EXRELO", iRELO)
		container.set("EXCUOR", iCUOR)
		container.set("EXSDNR", iSDNR)
		container.set("EXROUT", iROUT)
		container.set("EXRLSN", iRLSN)
		container.set("EXCUNF", iCUNF)
		container.set("EXSTOT", iSTOT as Double)
		container.set("EXSTLN", iSTLN as Integer)
		container.set("EXSTQT", iSTQT as Integer)
		container.set("EXSTWT", iSTWT as Double)
		container.set("EXSTUM", iSTUM)
		container.set("EXSTVL", iSTVL as Double)
		container.set("EXSTVU", iSTVU)
		
		container.set("EXRGDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
    container.set("EXRGTM", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
    container.set("EXLMDT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger())
		container.set("EXCHNO", 0)
		container.set("EXCHID", program.getUser())
		
		action.insert(container,recordExists)
		
	}
	Closure recordExists = {
		mi.error("Record already exists")
	}
}