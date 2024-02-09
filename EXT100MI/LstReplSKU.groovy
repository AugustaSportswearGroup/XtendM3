/**
 * README
 * This extension is being used to list customer order lines
 *
 * Name: EXT100MI.LstReplSKU
 * Description: Transaction used to get SKU's needing Replenishment
 * Date	      Changed By                      Description
 *20240115    sswarna@augustasportswear.com      List SKU's needing Replenishment
 *20240208    AbhishekA@fortude.co               Updating Validation logic*/
 
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.util.HashMap
import java.util.TreeMap
import java.util.Collections
import java.time.LocalDateTime

public class LstReplSKU extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final MICallerAPI miCaller
  private final LoggerAPI logger
  private int iCONO, translationId, maxPageSize = 10000
  private String iWHLO, iPLDT, iDWDT, iINFL
  private boolean validInput = true

  public LstReplSKU(MIAPI mi, ProgramAPI program, DatabaseAPI database, MICallerAPI miCaller, LoggerAPI logger) {
    this.mi = mi;
    this.program = program
    this.database = database
    this.miCaller = miCaller
    this.logger = logger
  }

  HashMap < String, String > getOOLINERecords = new HashMap < String, String > ()
  LinkedHashMap < String, String > sortedHashMap = new LinkedHashMap < > ()
  TreeMap < Integer, List < HashMap < String, String >>> sortedMap = new TreeMap < > (Collections.reverseOrder());

  public void main() {
    iCONO = program.LDAZD.CONO as Integer
    iWHLO = (mi.inData.get("WHLO") == null || mi.inData.get("WHLO").trim().isEmpty()) ? "" : mi.inData.get("WHLO")
    iPLDT = (mi.inData.get("PLDT") == null || mi.inData.get("PLDT").trim().isEmpty()) ? "" : mi.inData.get("PLDT")
    iDWDT = (mi.inData.get("DWDT") == null || mi.inData.get("DWDT").trim().isEmpty()) ? "" : mi.inData.get("DWDT")
    iINFL = (mi.inData.get("INFL") == null || mi.inData.get("INFL").trim().isEmpty()) ? "" : mi.inData.get("INFL")

    validateInput()
    if (validInput) {
      listRecord()
      sortHashMap()
      getWarehouseQuantity()

    }
  }

  /**
   *Validate records 
   * @params 
   * @return 
   */
  public void validateInput() {
    //Validate Company Number
    Map < String, String > params = ["CONO": iCONO.toString().trim()]
    Closure < ? > callback = {
      Map < String,
      String > response ->
      if (response.CONO == null) {
        mi.error("Invalid Company Number " + iCONO)
        validInput = false
        return 
      }
    }
    miCaller.call("MNS095MI", "Get", params, callback)

    //Validate Warehouse Number
    if (!iWHLO.toString().trim().isEmpty()) {
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
  }
  public void listRecord() {
     if(iPLDT.equals("")){
      iPLDT = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    String DWDTTo, DWDTFrom
    if (!iDWDT.equals("")) {
      LocalDate originalDate = LocalDate.parse(iDWDT, formatter)
      LocalDate resultDate = originalDate.minusDays(180)
      DWDTTo = originalDate.format(formatter)
      DWDTFrom = resultDate.format(formatter)
    } else {
      LocalDate originalDate = LocalDate.parse(iPLDT, formatter)
      LocalDate resultDate = originalDate.minusDays(180)
      DWDTTo = originalDate.format(formatter)
      DWDTFrom = resultDate.format(formatter)
    }

    ExpressionFactory expression = database.getExpressionFactory("OOLINE")
    expression = expression.ge("OBORST", "22").and(expression.le("OBORST", "27")).and(expression.eq("OBWHLO", iWHLO)).and(expression.ge("OBDWDT", DWDTFrom)).and(expression.le("OBDWDT", DWDTTo)).and(expression.le("OBPLDT", iPLDT)).or(expression.eq("OBPLDT", iPLDT))
    DBAction query = database.table("OOLINE").index("00").matching(expression).selection("OBITNO", "OBWHLO", "OBORQT").build()
    DBContainer container = query.getContainer()
    container.set("OBCONO", iCONO)
    query.readAll(container, 1, resultset)

  }

  Closure < ? > resultset = {
    DBContainer container ->
    String ITNO = container.get("OBITNO")
    String WHLO = container.get("OBWHLO")
    String ORQT = container.get("OBORQT")

    String key = ITNO + "," + WHLO
    String value = ORQT + ",1"
    if (getOOLINERecords.containsKey(key)) {
      value = getOOLINERecords.get(key)
      getOOLINERecords.remove(key)
      String tempORQT = value.split(",")[0]
      String count = value.split(",")[1]
      Double newORQT = Double.parseDouble(ORQT) + Double.parseDouble(tempORQT)
      value = newORQT + "," + (Integer.parseInt(count) + 1)
      getOOLINERecords.put(key, value)
    } else {
      getOOLINERecords.put(key, value)
    }

  }

  /**
   *Sort Hashmap
   * @params 
   * @return 
   */
  public void sortHashMap() {

    for (Map.Entry < String, String > entry: getOOLINERecords.entrySet()) {
      int count = Integer.parseInt(entry.getValue().split(",")[1])
      HashMap < String, String > map1 = new HashMap < > ()
      map1.put(entry.getKey(), entry.getValue())
      List < HashMap < String, String >> existingList = sortedMap.get(count)
      if (existingList == null) {
        existingList = new ArrayList < > ()
        existingList.add(map1)
        sortedMap.put(count, existingList)
      } else {
        existingList.add(map1)
        sortedMap.put(count, existingList)
      }
    }

  }

  public void getWarehouseQuantity() {

    for (Integer key: sortedMap.keySet()) {
      List < HashMap < String, String >> values = sortedMap.get(key)
      for (HashMap < String, String > map: values) {
        // Iterating through each HashMap
        for (String hashKey: map.keySet()) {
          String value = map.get(hashKey);
          String ITNO = hashKey.split(",")[0]
          String WHLO = hashKey.split(",")[1]
          String ORQT = value.split(",")[0]
          String count = value.split(",")[1]
          DBAction query = database.table("MITBAL").index("00").selection("MBSTQT", "MBREQT").build()
          DBContainer container = query.getContainer()
          container.set("MBCONO", iCONO)
          container.set("MBITNO", ITNO)
          container.set("MBWHLO", WHLO)
          if (query.read(container)) {
            Double STQT = container.getDouble("MBSTQT")
            Double REQT = container.getDouble("MBREQT")
            Double AVQT = STQT - REQT
            if (iINFL.trim().equals("1")) {
              if (AVQT > 0) {
                mi.outData.put("ITNO", ITNO)
                mi.outData.put("LNCT", count)
                mi.outData.put("ORQT", ORQT)
                mi.outData.put("AVQT", AVQT + "")
                mi.write()
              }
            } else {
              mi.outData.put("ITNO", ITNO)
              mi.outData.put("LNCT", count)
              mi.outData.put("ORQT", ORQT)
              mi.outData.put("AVQT", AVQT + "")
              mi.write()
            }

          }
        }
      }
    }

  }
}