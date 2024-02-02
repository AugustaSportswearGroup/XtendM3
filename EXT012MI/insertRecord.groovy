
/**
* README
* Add a record to the table EXTOOL
*
* Name: EXT012MI.AddLineInfo
* Description: Add a record to the EXTOOL table
* Date	      Changed By            Description
* 20230815	  NATARAJKB        Add a record to the EXTOOL table
*
*/


public class AddLineInfo extends ExtendM3Transaction {
	private final MIAPI mi
	private final DatabaseAPI database
	private final LoggerAPI logger
	private final ProgramAPI program
	private final UtilityAPI utility

	//Input fields
	private String iORNO, iBYPN, iVNPN, iCPCN, iEANN, iGTIN, iUPCC, iQTUM, iSHDT, iLNDT
	private int iCONO, iPONR, iPOSX, iLISN, iORQT
	private double iPUPR

	public AddLineInfo(MIAPI mi, DatabaseAPI database, LoggerAPI logger, ProgramAPI program, UtilityAPI utility) {
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
		iBYPN = (mi.inData.get("BYPN") == null || mi.inData.get("BYPN").trim().isEmpty()) ? "" : mi.inData.get("BYPN")
		iVNPN = (mi.inData.get("VNPN") == null || mi.inData.get("VNPN").trim().isEmpty()) ? "" : mi.inData.get("VNPN")
		iCPCN = (mi.inData.get("CPCN") == null || mi.inData.get("CPCN").trim().isEmpty()) ? "" : mi.inData.get("CPCN")
		iEANN = (mi.inData.get("EANN") == null || mi.inData.get("EANN").trim().isEmpty()) ? "" : mi.inData.get("EANN")
		iGTIN = (mi.inData.get("GTIN") == null || mi.inData.get("GTIN").trim().isEmpty()) ? "" : mi.inData.get("GTIN")
		iUPCC = (mi.inData.get("UPCC") == null || mi.inData.get("UPCC").trim().isEmpty()) ? "" : mi.inData.get("UPCC")
		iQTUM = (mi.inData.get("QTUM") == null || mi.inData.get("QTUM").trim().isEmpty()) ? "" : mi.inData.get("QTUM")
		iSHDT = (mi.inData.get("SHDT") == null || mi.inData.get("SHDT").trim().isEmpty()) ? "" : mi.inData.get("SHDT")
		iLNDT = (mi.inData.get("LNDT") == null || mi.inData.get("LNDT").trim().isEmpty()) ? "" : mi.inData.get("LNDT")
		
		iPONR = (mi.inData.get("PONR") == null || mi.inData.get("PONR").trim().isEmpty()) ? 0 : mi.inData.get("PONR") as Integer
		iPOSX = (mi.inData.get("POSX") == null || mi.inData.get("POSX").trim().isEmpty()) ? 0 : mi.inData.get("POSX") as Integer
		iLISN = (mi.inData.get("LISN") == null || mi.inData.get("LISN").trim().isEmpty()) ? 0 : mi.inData.get("LISN") as Integer
		iORQT = (mi.inData.get("ORQT") == null || mi.inData.get("ORQT").trim().isEmpty()) ? 0 : mi.inData.get("ORQT") as Integer
		iPUPR = (mi.inData.get("PUPR") == null || mi.inData.get("PUPR").trim().isEmpty()) ? 0 : mi.inData.get("PUPR") as Double
    
    //Validate ORNO
    if(!validORNO(iCONO, iORNO, iPONR, iPOSX)){
      mi.error("Order number ${iORNO} with line number ${iPONR} and suffix ${iPOSX} does not exist");
      return;
    }else{
      insertRecord();
    }
    
	}
	
	/**
  * Validate ORNO
  **/
  private boolean validORNO(int CONO, String ORNO, int PONR, int POSX){
    DBAction dbAction = database.table("OXLINE").index("00").build();
    DBContainer OXLINE = dbAction.createContainer();
    OXLINE.set("OBCONO", CONO as Integer);
    OXLINE.set("OBORNO", ORNO);
    OXLINE.set("OBPONR", PONR as Integer);
    OXLINE.set("OBPOSX", POSX as Integer);
    if(dbAction.read(OXLINE)){
      return true;
    } else {
      return false;
    }
  }
	
	/**
	 * Inserts record in the EXTOOL table
	 *
	 */
	private void insertRecord(){
		DBAction action = database.table("EXTOOL").index("00").selection("CONO", "ORNO", "PONR", "POSX").build()
		DBContainer container = action.getContainer()

		container.set("EXCONO", iCONO as Integer)
		container.set("EXORNO", iORNO)
		container.set("EXPONR", iPONR as Integer)
		container.set("EXPOSX", iPOSX as Integer)
		container.set("EXLISN", iLISN as Integer)
		container.set("EXBYPN", iBYPN)
		container.set("EXVNPN", iVNPN)
		container.set("EXCPCN", iCPCN)
		container.set("EXEANN", iEANN)
		container.set("EXGTIN", iGTIN)
		container.set("EXUPCC", iUPCC)
		container.set("EXORQT", iORQT as Integer)
		container.set("EXQTUM", iQTUM)
		container.set("EXPUPR", iPUPR as Double)
		container.set("EXSHDT", iSHDT)
		container.set("EXLNDT", iLNDT)


		container.set("EXRGDT", utility.call("DateUtil", "currentDateY8AsInt"))
		container.set("EXRGTM", utility.call("DateUtil", "currentTimeAsInt"))
		container.set("EXLMDT", utility.call("DateUtil", "currentDateY8AsInt"))
		container.set("EXCHNO", 0)
		container.set("EXCHID", program.getUser())

		action.insert(container,recordExists)

	}

	Closure recordExists = {
		mi.error("Record already exists")
	}
}
