/**
 * README
 * This extension is being used to Set records to EXTSTH table.
 *
 * Name: EXT001MI.SetPlayerHead
 * Description: Set records to EXTSTH table
 * Date	      Changed By                      Description
 *20230403  SuriyaN@fortude.co     Set records to EXTSTH table */

public class SetPlayerHead extends ExtendM3Transaction {
  private final MIAPI mi
  private final DatabaseAPI database
  private final ProgramAPI program
  private final MICallerAPI miCaller
  private final UtilityAPI utility

  private String iDIVI, iPLID, iORNR, iORNO, iOREF, iPLNM, iPLNU, iTEAM, iLEAG, iGRBY, iBGBY, iCUNO, iADID, iUDF1, iUDF2, iUDF3, iUDF4, iUDF5, iUDF6,playerID
  private int iCONO, iORDT
  boolean isOrderFound = false,validInput = true,recordExists = false

  public SetPlayerHead(MIAPI mi, DatabaseAPI database, ProgramAPI program,MICallerAPI miCaller,UtilityAPI utility) {
    this.mi = mi
    this.database = database
    this.program = program
    this.miCaller = miCaller
    this.utility = utility
  }
  /**
   ** Main function
   * @param
   * @return
   */
  public void main() {
    iDIVI = (mi.inData.get("DIVI") == null || mi.inData.get("DIVI").trim().isEmpty()) ? program.LDAZD.DIVI : mi.inData.get("DIVI")
    iPLID = (mi.inData.get("PLID") == null || mi.inData.get("PLID").trim().isEmpty()) ? "" : mi.inData.get("PLID")
    iORNR = (mi.inData.get("ORNR") == null || mi.inData.get("ORNR").trim().isEmpty()) ? "" : mi.inData.get("ORNR")
    iORNO = (mi.inData.get("ORNO") == null || mi.inData.get("ORNO").trim().isEmpty()) ? "" : mi.inData.get("ORNO")
    iOREF = (mi.inData.get("OREF") == null || mi.inData.get("OREF").trim().isEmpty()) ? "" : mi.inData.get("OREF")
    iPLNM = (mi.inData.get("PLNM") == null || mi.inData.get("PLNM").trim().isEmpty()) ? "" : mi.inData.get("PLNM")
    iPLNU = (mi.inData.get("PLNU") == null || mi.inData.get("PLNU").trim().isEmpty()) ? "" : mi.inData.get("PLNU")
    iTEAM = (mi.inData.get("TEAM") == null || mi.inData.get("TEAM").trim().isEmpty()) ? "" : mi.inData.get("TEAM")
    iLEAG = (mi.inData.get("LEAG") == null || mi.inData.get("LEAG").trim().isEmpty()) ? "" : mi.inData.get("LEAG")
    iGRBY = (mi.inData.get("GRBY") == null || mi.inData.get("GRBY").trim().isEmpty()) ? "" : mi.inData.get("GRBY")
    iBGBY = (mi.inData.get("BGBY") == null || mi.inData.get("BGBY").trim().isEmpty()) ? "" : mi.inData.get("BGBY")
    iCUNO = (mi.inData.get("CUNO") == null || mi.inData.get("CUNO").trim().isEmpty()) ? "" : mi.inData.get("CUNO")
    iADID = (mi.inData.get("ADID") == null || mi.inData.get("ADID").trim().isEmpty()) ? "" : mi.inData.get("ADID")
    iUDF1 = (mi.inData.get("UDF1") == null || mi.inData.get("UDF1").trim().isEmpty()) ? "" : mi.inData.get("UDF1")
    iUDF2 = (mi.inData.get("UDF2") == null || mi.inData.get("UDF2").trim().isEmpty()) ? "" : mi.inData.get("UDF2")
    iUDF3 = (mi.inData.get("UDF3") == null || mi.inData.get("UDF3").trim().isEmpty()) ? "" : mi.inData.get("UDF3")
    iUDF4 = (mi.inData.get("UDF4") == null || mi.inData.get("UDF4").trim().isEmpty()) ? "" : mi.inData.get("UDF4")
    iUDF5 = (mi.inData.get("UDF5") == null || mi.inData.get("UDF5").trim().isEmpty()) ? "" : mi.inData.get("UDF5")
    iUDF6 = (mi.inData.get("UDF6") == null || mi.inData.get("UDF6").trim().isEmpty()) ? "" : mi.inData.get("UDF6")


    iCONO = (mi.inData.get("CONO") == null || mi.inData.get("CONO").trim().isEmpty()) ? program.LDAZD.CONO as Integer : mi.inData.get("CONO") as Integer
    iORDT = (mi.inData.get("ORDT") == null || mi.inData.get("ORDT").trim().isEmpty()) ? 0 : mi.inData.get("ORDT") as Integer
    validateInput()
    if(!iPLID.trim().isEmpty())
    {
      checkIfOrderExists()
    }
    
    if(validInput&&!recordExists)
    {
      insertRecord()
    }
  }

  /**
   *Validate Records
   * @params
   * @return boolean
   */
  public void validateInput() {
    //Validate Company Number
    Map<String, String> params = ["CONO": iCONO.toString().trim()]
    Closure<?> callback = {
      Map < String,
        String > response ->
        if (response.CONO == null) {
          mi.error("Invalid Company Number " + iCONO)
          validInput=false
          return 
        }
    }
    miCaller.call("MNS095MI", "Get", params, callback)
    
    //Validate Division
    params = ["CONO": iCONO.toString().trim(), "DIVI": iDIVI.toString().trim()]
    callback = {
      Map < String,
      String > response ->
      if (response.DIVI == null) {
        mi.error("Invalid Division " + iDIVI)
        validInput = false
        return
      }
    }

    miCaller.call("MNS100MI", "GetBasicData", params, callback)

    if(!iCUNO.trim().isEmpty()) {
      //Validate Customer Number
      params = ["CONO": iCONO.toString().trim(), "CUNO": iCUNO.toString().trim()]
      callback = {
        Map<String,
          String> response ->
          if (response.CUNO == null) {
            mi.error("Invalid Customer Number " + iCUNO)
            validInput=false
            return 
          }
      }
      miCaller.call("CRS610MI", "GetBasicData", params, callback)
    }

    if(!iORNR.trim().isEmpty())
    {
      //Validate Temporary Order Number
      DBAction query = database.table("OXHEAD").index("00").build()
      DBContainer container = query.getContainer()
      container.set("OACONO", iCONO)
      container.set("OAORNO", iORNR)

      if(!query.read(container))
      {
        mi.error("Temporary Order Number not found "+iORNR)
        validInput=false
        return 
      }
    }
    
    if(!iORNO.trim().isEmpty())
    {
      //Validate Order Number
      params = ["ORNO": iORNO.toString().trim()]
      callback = {
        Map < String,
          String > response ->
          if (response.ORNO == null) {
            mi.error("Invalid Order Number " + iORNO)
            validInput=false
            return 
          }
      }
      miCaller.call("OIS100MI", "GetOrderHead", params, callback)
    }

    if(!iADID.trim().isEmpty())
    {
      //Validate Address ID
      DBAction query = database.table("OCUSAD").selection("OPADID").index("10").build()
      DBContainer container = query.getContainer()
      container.set("OPCONO", iCONO)
      container.set("OPCUNO", iCUNO)
      container.set("OPADRT", 1)
      container.set("OPADID", iADID)

      if(!query.read(container))
      {
        mi.error("Address ID Not Found "+iADID)
        validInput=false
        return 
      }
    }
    
  if(iORDT!=0)
    {
      //Validate Date
      String sourceFormat = utility.call("DateUtil","getFullFormat",mi.getDateFormat())
      validInput = utility.call("DateUtil","isDateValid",iORDT,sourceFormat.toString())
      if (!validInput)
      {
          mi.error("Order Date not in "+sourceFormat+" format. Please Check "+iORDT)
          return 
        }else
        {
          if(!sourceFormat.trim().equals("yyyyMMdd"))
          {
            iORDT = utility.call("DateUtil","convertDate",sourceFormat,"yyyyMMdd",iORDT) //Maintain date in YMD8 format in the table
          }
        }
    }

  }

  /**
   * Check if the order already exists in EXTSTH table and get PLID if availble.
   * @params
   * @return
   */

  public void checkIfOrderExists()
  {
    DBAction query = database.table("EXTSTH").index("00").build()
    DBContainer container = query.getContainer()
    container.set("EXCONO", iCONO)
    container.set("EXDIVI", iDIVI)
    container.set("EXPLID", iPLID)
    if(query.read(container))
    {
      recordExists = true
      Map<String, String>  params = ["CONO": iCONO.toString().trim(),"DIVI":iDIVI.toString().trim(),"PLID":iPLID.toString().trim()
                     ,"ORNR":iORNR.toString().trim(),"ORNO":iORNO.toString().trim(),"OREF":iOREF.toString().trim()
                     ,"PLNM":iPLNM.toString().trim(),"PLNU":iPLNU.toString().trim(),"TEAM":iTEAM.toString().trim()
                     ,"LEAG":iLEAG.toString().trim(),"GRBY":iGRBY.toString().trim(),"BGBY":iBGBY.toString().trim()
                     ,"CUNO":iCUNO.toString().trim(),"ADID":iADID.toString().trim(),"ORDT":iORDT.toString().trim()
                     ,"UDF1":iUDF1.toString().trim(),"UDF2":iUDF2.toString().trim(),"UDF3":iUDF3.toString().trim()
                     ,"UDF4":iUDF4.toString().trim(),"UDF5":iUDF5.toString().trim(),"UDF6":iUDF6.toString().trim()]

      Closure<?> callback = {
        Map < String,
          String > response ->
          mi.error("Response: "+response)
          if(response.errorMessage!=null)
          {
            mi.error("Error: "+response.errorMessage)
            return false
          }
          
      }
      miCaller.call("EXT001MI", "UpdPlayerHead", params,callback)
      mi.outData.put("PLID",iPLID)
      mi.outData.put("PROC","UPD")
      mi.write()
    }
  }
  

  /**
   *Insert records to EXTSTH table
   * @params
   * @return
   */
  public void insertRecord() {
    Map<String, String>  params = ["CONO": iCONO.toString().trim(),"DIVI":iDIVI.toString().trim(),"PLID":iPLID.toString().trim()
                   ,"ORNR":iORNR.toString().trim(),"ORNO":iORNO.toString().trim(),"OREF":iOREF.toString().trim()
                   ,"PLNM":iPLNM.toString().trim(),"PLNU":iPLNU.toString().trim(),"TEAM":iTEAM.toString().trim()
                   ,"LEAG":iLEAG.toString().trim(),"GRBY":iGRBY.toString().trim(),"BGBY":iBGBY.toString().trim()
                   ,"CUNO":iCUNO.toString().trim(),"ADID":iADID.toString().trim(),"ORDT":iORDT.toString().trim()
                   ,"UDF1":iUDF1.toString().trim(),"UDF2":iUDF2.toString().trim(),"UDF3":iUDF3.toString().trim()
                   ,"UDF4":iUDF4.toString().trim(),"UDF5":iUDF5.toString().trim(),"UDF6":iUDF6.toString().trim()]

    Closure<?> callback = {
      Map < String,
        String > response ->
        if(response.errorMessage!=null)
        {
          mi.error("Error : "+response.errorMessage)
        }
        else
        {
          mi.outData.put("PLID",response.PLID)
          mi.outData.put("PROC","ADD")
          mi.write()
        }
    }
    miCaller.call("EXT001MI", "AddPlayerHead", params,callback)

  }


}
