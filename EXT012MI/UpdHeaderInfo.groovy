
/**
* README
* Update a record in the table EXTOOH
*
* Name: EXT012MI.UpdHeaderInfo
* Description: Update a record in the EXTOOH table
* Date	      Changed By            Description
* 20230815	  NATARAJKB        Update a record in the EXTOOH table
*
*/

public class UpdHeaderInfo extends ExtendM3Transaction {
	private final MIAPI mi
	private final DatabaseAPI database
	private final LoggerAPI logger
	private final ProgramAPI program
	private final UtilityAPI utility

	//Input fields
	private String iORNO, iCEDI, iTPID, iPYNO, iDEPT, iVEND,iFOBC, iADST, iADBT, iADDS, iCANR, iSDIN, iCDRP, iREZA, iRELO, iCUOR, iSDNR, iROUT, iRLSN, iCUNF, iSTUM, iSTVU, iADBY, iUNID
	private int iCONO, iSTLN, iSTQT
	private double iSTOT, iSTWT, iSTVL

	public UpdHeaderInfo(MIAPI mi, DatabaseAPI database, LoggerAPI logger, ProgramAPI program, UtilityAPI utility) {
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
      updateRecord();
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
	 * Updates record in the EXTOOH table
	 *
	 */
	private void updateRecord(){
		DBAction action = database.table("EXTOOH").index("00").selection("CONO", "ORNO").build()
		DBContainer container = action.getContainer()
		
		container.set("EXCONO", iCONO as Integer)
		container.set("EXORNO", iORNO)
		
    // Update changed information
		if (!action.readLock(container, updateCallBack)) {
		  mi.error("Record does not exist")
		}
		
	}

	/**
	* Closure for EXTOOH Update
	*/

	Closure < ? > updateCallBack = {
		LockedResult lockedResult ->

		if (iCEDI.trim() != "") {
			if (iCEDI.trim() == "?") {
				lockedResult.set("EXCEDI", "")
			} else {
				lockedResult.set("EXCEDI", iCEDI)
			}
		}

		if (iTPID.trim() != "") {
			if (iTPID.trim() == "?") {
				lockedResult.set("EXTPID", "")
			} else {
				lockedResult.set("EXTPID", iTPID)
			}
		}

		if (iPYNO.trim() != "") {
			if (iPYNO.trim() == "?") {
				lockedResult.set("EXPYNO", "")
			} else {
				lockedResult.set("EXPYNO", iPYNO)
			}
		}

		if (iDEPT.trim() != "") {
			if (iDEPT.trim() == "?") {
				lockedResult.set("EXDEPT", "")
			} else {
				lockedResult.set("EXDEPT", iDEPT)
			}
		}

		if (iVEND.trim() != "") {
			if (iVEND.trim() == "?") {
				lockedResult.set("EXVEND", "")
			} else {
				lockedResult.set("EXVEND", iVEND)
			}
		}

		if (iFOBC.trim() != "") {
			if (iFOBC.trim() == "?") {
				lockedResult.set("EXFOBC", "")
			} else {
				lockedResult.set("EXFOBC", iFOBC)
			}
		}

		if (iADST.trim() != "") {
			if (iADST.trim() == "?") {
				lockedResult.set("EXADST", "")
			} else {
				lockedResult.set("EXADST", iADST)
			}
		}

		if (iADBT.trim() != "") {
			if (iADBT.trim() == "?") {
				lockedResult.set("EXADBT", "")
			} else {
				lockedResult.set("EXADBT", iADBT)
			}
		}

		if (iADDS.trim() != "") {
			if (iADDS.trim() == "?") {
				lockedResult.set("EXADDS", "")
			} else {
				lockedResult.set("EXADDS", iADDS)
			}
		}

    if (iADBY.trim() != "") {
			if (iADBY.trim() == "?") {
				lockedResult.set("EXADBY", "")
			} else {
				lockedResult.set("EXADBY", iADBY)
			}
		}
		
		if (iUNID.trim() != "") {
			if (iUNID.trim() == "?") {
				lockedResult.set("EXUNID", "")
			} else {
				lockedResult.set("EXUNID", iUNID)
			}
		}
		if (iCANR.trim() != "") {
			if (iCANR.trim() == "?") {
				lockedResult.set("EXCANR", "")
			} else {
				lockedResult.set("EXCANR", iCANR)
			}
		}

		if (iSDIN.trim() != "") {
			if (iSDIN.trim() == "?") {
				lockedResult.set("EXSDIN", "")
			} else {
				lockedResult.set("EXSDIN", iSDIN)
			}
		}

		if (iCDRP.trim() != "") {
			if (iCDRP.trim() == "?") {
				lockedResult.set("EXCDRP", "")
			} else {
				lockedResult.set("EXCDRP", iCDRP)
			}
		}

		if (iREZA.trim() != "") {
			if (iREZA.trim() == "?") {
				lockedResult.set("EXREZA", "")
			} else {
				lockedResult.set("EXREZA", iREZA)
			}
		}

		if (iRELO.trim() != "") {
			if (iRELO.trim() == "?") {
				lockedResult.set("EXRELO", "")
			} else {
				lockedResult.set("EXRELO", iRELO)
			}
		}

		if (iCUOR.trim() != "") {
			if (iCUOR.trim() == "?") {
				lockedResult.set("EXCUOR", "")
			} else {
				lockedResult.set("EXCUOR", iCUOR)
			}
		}

		if (iSDNR.trim() != "") {
			if (iSDNR.trim() == "?") {
				lockedResult.set("EXSDNR", "")
			} else {
				lockedResult.set("EXSDNR", iSDNR)
			}
		}

		if (iROUT.trim() != "") {
			if (iROUT.trim() == "?") {
				lockedResult.set("EXROUT", "")
			} else {
				lockedResult.set("EXROUT", iROUT)
			}
		}

		if (iRLSN.trim() != "") {
			if (iRLSN.trim() == "?") {
				lockedResult.set("EXRLSN", "")
			} else {
				lockedResult.set("EXRLSN", iRLSN)
			}
		}

		if (iCUNF.trim() != "") {
			if (iCUNF.trim() == "?") {
				lockedResult.set("EXCUNF", "")
			} else {
				lockedResult.set("EXCUNF", iCUNF)
			}
		}

		if (iSTOT != 0) {
			lockedResult.set("EXSTOT", iSTOT as Double)
		} else if(iSTOT == 0) {
			lockedResult.set("EXSTOT", 0)
		}

		if (iSTLN != 0) {
			lockedResult.set("EXSTLN", iSTLN as Integer)
		} else if(iSTLN == 0) {
			lockedResult.set("EXSTLN", 0)
		}

		if (iSTQT != 0) {
			lockedResult.set("EXSTQT", iSTQT as Integer)
		} else if(iSTQT == 0) {
			lockedResult.set("EXSTQT", 0)
		}

		if (iSTWT != 0) {
			lockedResult.set("EXSTWT", iSTWT as Double)
		} else if(iSTWT == 0) {
			lockedResult.set("EXSTWT", 0)
		}

		if (iSTUM.trim() != "") {
			if (iSTUM.trim() == "?") {
				lockedResult.set("EXSTUM", "")
			} else {
				lockedResult.set("EXSTUM", iSTUM)
			}
		}

		if (iSTVL != 0) {
			lockedResult.set("EXSTOT", iSTVL as Double)
		} else if(iSTVL == 0) {
			lockedResult.set("EXSTVL", 0)
		}

		if (iSTVU.trim() != "") {
			if (iSTVU.trim() == "?") {
				lockedResult.set("EXSTVU", "")
			} else {
				lockedResult.set("EXSTVU", iSTVU)
			}
		}



		// Update changed information
		lockedResult.set("EXLMDT", utility.call("DateUtil", "currentDateY8AsInt"))
		lockedResult.set("EXCHNO", lockedResult.getInt("EXCHNO") + 1)
		lockedResult.set("EXCHID", program.getUser())
		lockedResult.update()
	}
}
