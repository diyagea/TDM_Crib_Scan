package application.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.modal.Comp;
import application.modal.CostCenter;
import application.modal.User;
import application.util.Common;
import application.util.JdbcHelper;

public class StockDAO {
	JdbcHelper jdbc = new JdbcHelper();

	/***********************ADD*****************************/
	public static void main(String[] args) {
	}
	
	/**
	 * create new physical ID
	 * @param timestamp
	 * @param compID
	 * @param physicalID
	 * @throws SQLException 
	 */
	@SuppressWarnings("static-access")
	public int createPhysicalID(String timestamp, String compID, String physicalID) throws SQLException {
		String sql = "INSERT INTO TDM_COMPINV (TIMESTAMP, COMPID, COMPINVID, IDTYPE) VALUES (?, ?, ?, 1)";
		return jdbc.update(sql, timestamp, compID, physicalID);
	}
	
	/**
	 * Add cancel detail
	 * 
	 * @param type  1=Crib, 2=machine, 3=maintain
	 * @param timestamp
	 * @param cancelKey
	 * @param compID
	 * @param physicalID
	 * @param costunit  (old CostCenter)
	 * @param workplace (old CostCenter)
	 * @param stockplaceID(old stocklocation)
	 * @param countNew (-1)
	 * @param countUsed (-1)
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("static-access")
	public int addCancelDetail(int type, String timestamp, BigDecimal cancelKey, String compID, String physicalID, String costunit, String workplace, String stockplaceID, int countNew, int countUsed) throws SQLException {
		String sql_getMaxPos = "SELECT MAX(CANCELLISTPOS) FROM LGM_CANCELLIST WHERE CANCELNR = ? ";
		int pos = Common.objectToInt(jdbc.getSingle(sql_getMaxPos, cancelKey));
		if (pos == 0) {
			pos = 1;
		} else {
			pos += 1;
		}

		//?: timestamp, cancelKey, pos, costunit(old), workplace(old), type, compID, physicalID, stockplaceID, countNew(-1), countUsed(-1) 
		String sql_insert = "INSERT INTO LGM_CANCELLIST (TIMESTAMP , CANCELNR , CANCELLISTPOS , COSTUNIT , WORKPLACE, TYPE , ID , IDTYPE ,    STOCKTYPEID ,    INVID ,    STOCKPLACEID ,    COUNTNEW ,    COUNTUSED ,    COUNTREPAIR )   VALUES  (  ?,    ?,    ?,    ?,    ?,    ?,    ?,    1,    null,    ?,    ?,    ?,    ?,    0 ) ";
		return jdbc.update(sql_insert, timestamp, cancelKey, pos, costunit, workplace, type, compID, physicalID, stockplaceID, countNew, countUsed);
	}

	/**
	 * add cancel key
	 * @param key
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("static-access")
	public int addCancelKey(BigDecimal key) throws SQLException {
		String sql = "INSERT INTO LGM_CANCEL ( TIMESTAMP , CANCELNR , BOOKTYPE , MASTERNR , CANCELSTATE) VALUES (?, ?, -3, NULL, NULL) ";
		return jdbc.update(sql, Common.getTimestamp_TDM(), key);
	}

	/**
	 * add Issue History in TDM
	 * type : -1=New Entry, -3=Issue
	 */
	@SuppressWarnings("static-access")
	public void addIssueHistory(Comp comp, String costunit, String workplace, String tdmInitials, int type) throws Exception {
		//timestamp
		String timestamp = System.currentTimeMillis() + "";
		timestamp = timestamp.substring(0, 10);

		//cancel key
		double time = System.currentTimeMillis() * 0.01;
		NumberFormat nf = NumberFormat.getInstance();
		// without "," not like -> [123,456,789.128]
		nf.setGroupingUsed(false);
		String key = nf.format(time);

		//?: timestamp, cancelKey, from costunit, from workplace, to costunit, to workplace, compID, timestamp, tdmInitials, count new , count used
		String sql_issueHistory = "INSERT INTO LGM_HISTORYBASE (TIMESTAMP,CANCELNR,COSTUNITFROM,WORKPLACEFROM,COSTUNITTO,WORKPLACETO,COSTUNITCOSTS,WORKPLACECOSTS,BOOKTYPE,ID,IDTYPE,LISTID,LISTTYPE,BOOKTIMESTAMP,FACTOR,COSTCORRELATIONFLAG,STATE,USERID,ACCOUNTID,STOCKCLASSFLAG,BOOKTEXT,COUNTNEW,COUNTUSED,COUNTREPAIR,AREA) VALUES (?,?,?,?,?,?,null,null,?,?,1,null,null,?,0.000000,0,0,?,null,0,null,?,?,0,1) ";
		jdbc.update(sql_issueHistory, timestamp, key, comp.getCostunit(), comp.getWorkplace(), costunit, workplace, type, comp.getCompID(), timestamp, tdmInitials, comp.getCountNew(), comp.getCountUsed());

		//?: cancelKey, physicalID, stockLocation, timestamp
		String sql_physical_history = "INSERT INTO LGM_HISTORYBASELIST ( CANCELNR, HISTORYBASELISTPOS, STOCKTYPEID, INVID, STOCKPLACEID, COUNTNEW, COUNTUSED , COUNTREPAIR  , VALIDATESTATE   , TIMESTAMP  , COUNTNEWBEFORE  , COUNTUSEDBEFORE  , COUNTREPAIRBEFORE ) VALUES (?, 1, NULL, ?, ?, 0, 1, 0, 1, ?, 0,0,0) ";
		jdbc.update(sql_physical_history, key, comp.getPhysicalID(), comp.getStockplaceID(), timestamp);

	}

	/**
	 * add History Base
	 * @param bookType : -1=New Entry, -3=Issue
	 * @param timestamp
	 * @param cancelKey
	 * @param compID
	 * @param costunit
	 * @param workplace
	 * @param tdmInitials
	 * @param countNew
	 * @param countUsed
	 * @throws SQLException 
	 */
	@SuppressWarnings("static-access")
	public void addHistoryBase(int bookType, String timestamp, BigDecimal cancelKey, String compID, String costunitFrom, String workplaceFrom, String costunitTo, String workplaceTo, String tdmInitials, int countNew, int countUsed) throws SQLException {
		//?: timestamp, cancelKey, costunitFrom, workplaceFrom, costunitTo, workplaceTo, bookType, compID, timestamp, tdmInitials, countNew, countUsed
		String sql = "INSERT INTO  TMS.LGM_HISTORYBASE      ( TIMESTAMP ,    CANCELNR ,    COSTUNITFROM ,    WORKPLACEFROM ,    COSTUNITTO ,    WORKPLACETO ,    COSTUNITCOSTS ,    WORKPLACECOSTS ,    BOOKTYPE ,    ID ,    IDTYPE ,    LISTID ,    LISTTYPE ,    BOOKTIMESTAMP ,    FACTOR ,    COSTCORRELATIONFLAG ,    STATE ,    USERID ,    ACCOUNTID ,    STOCKCLASSFLAG ,    BOOKTEXT ,    COUNTNEW ,    COUNTUSED ,    COUNTREPAIR ,    AREA )    VALUES   ( ? ,  ? , ?, ? , ? , ? ,    null ,    null ,    ? ,    ? ,    1 ,    null ,    null ,    ? ,    0.000000 ,    0 ,    0 ,    ? ,    null ,    0 ,    null ,    ? ,    ? ,    0 ,    1 )   ";

		jdbc.update(sql, timestamp, cancelKey, costunitFrom, workplaceFrom, costunitTo, workplaceTo, bookType, compID, timestamp, tdmInitials, countNew, countUsed);
	}
	
	/**
	 * add History Base List
	 * @param timestamp
	 * @param cancelKey
	 * @param physicalID
	 * @param stockplaceID
	 * @param countNew
	 * @param countUsed
	 * @param countNewBefore
	 * @param countUsedBefore
	 * @throws SQLException
	 */
	@SuppressWarnings("static-access")
	public void addHistoryBaseList(String timestamp, BigDecimal cancelKey, String physicalID, String stockplaceID, int countNew, int countUsed, int countNewBefore, int countUsedBefore) throws SQLException{
		String sql_getMaxPos = "SELECT MAX(HISTORYBASELISTPOS) FROM LGM_HISTORYBASELIST WHERE CANCELNR=?";
		int pos = Common.objectToInt(jdbc.getSingle(sql_getMaxPos, cancelKey));
		if (pos == 0) {
			pos = 1;
		} else {
			pos += 1;
		}
		// ?: timestamp, cancelKey, pos, stockplaceID, countNew, countUsed, countNewBefore, countUsedBefore, physicalID
		String sql = "INSERT INTO  TMS.LGM_HISTORYBASELIST      ( TIMESTAMP ,    CANCELNR ,    HISTORYBASELISTPOS ,    STOCKPLACEID ,    COUNTNEW ,    COUNTUSED ,    COUNTREPAIR ,    COUNTNEWBEFORE ,    COUNTUSEDBEFORE ,    COUNTREPAIRBEFORE ,    VALIDATESTATE ,    STOCKTYPEID ,    INVID )   VALUES  (  ?,    ?,    ?,    ?,    ?,    ?,    0,    ?,    ?,    0,    1,    null,    ? )";
		
		jdbc.update(sql, timestamp, cancelKey, pos, stockplaceID, countNew, countUsed, countNewBefore, countUsedBefore, physicalID);
		
	}
	

	/**
	 * add CostCenter Consumption in TDM
	 */
	@SuppressWarnings("static-access")
	public void addConsumption(Comp comp, CostCenter toWhere, User operator) throws SQLException {
		//?: to costunit, to workplace, comp physicalID, compID, compID
		String sql_addConsumption_new = "INSERT INTO  LGM_CHECKLISTBASE      ( TIMESTAMP ,    COSTUNIT ,    WORKPLACE ,    INVID ,    ID ,    IDTYPE ,    COUNTNEWCOMP ,    COUNTSORTOUTWEAR ,    COUNTSORTOUTWEARNOCOSTS ,    COUNTSORTOUTBREAK ,    COUNTSORTOUTBREAKNOCOSTS ,    COUNTSORTOUTNEW ,    COUNTSORTOUTUSED ,    COUNTSORTOUTREPAIR ,    COUNTBOOKNEW ,    COUNTBOOKUSED ,    COUNTBOOKREPAIR ,    COUNTREPAIR ,    COUNTREPAIRWITHFACTOR ,    COUNTREPAIRREPAIR ,    ACCOUNTID ,    CANCELNR ,    COSTSNEWCOMP ,    COSTSSORTOUTWEAR ,    COSTSSORTOUTBREAK ,    COSTSSORTOUTNEW ,    COSTSSORTOUTUSED ,    COSTSSORTOUTREPAIR ,    COSTSBOOKNEW ,    COSTSBOOKUSED ,    COSTSBOOKREPAIR ,    COSTSREPAIR ,    COSTSREPAIRWITHFACTOR ,    COSTSREPAIRREPAIR ,    BOOKTIMESTAMP )   SELECT    ?,    ?,    ?,    ?,    ?,    1,    0,    0,    0,    0,    0,    0,    0,    0,    1,    0,    0,    0,    0.000000,    0,    null,    00000000000.00000,    0 * LGM_COSTSV.NETPRICE,    0 * LGM_COSTSV.SORTOUTWEAR,    0 * LGM_COSTSV.SORTOUTBREAK,    0 * LGM_COSTSV.BOOKNEW,    0 * LGM_COSTSV.BOOKUSED,    0 * LGM_COSTSV.REPAIR,    1 * LGM_COSTSV.BOOKNEW,    0 * LGM_COSTSV.BOOKUSED,    0 * LGM_COSTSV.REPAIR,    0 * LGM_COSTSV.REPAIR,    0.000000 * LGM_COSTSV.REPAIR,    0 * LGM_COSTSV.REPAIR,    ? FROM  LGM_COSTSV  WHERE  LGM_COSTSV.ID  = ?  AND LGM_COSTSV.IDTYPE  = 1 ";
		String sql_addConsumption_used = "INSERT INTO  TMS.LGM_CHECKLISTBASE      ( TIMESTAMP ,    COSTUNIT ,    WORKPLACE ,    INVID ,    ID ,    IDTYPE ,    COUNTNEWCOMP ,    COUNTSORTOUTWEAR ,    COUNTSORTOUTWEARNOCOSTS ,    COUNTSORTOUTBREAK ,    COUNTSORTOUTBREAKNOCOSTS ,    COUNTSORTOUTNEW ,    COUNTSORTOUTUSED ,    COUNTSORTOUTREPAIR ,    COUNTBOOKNEW ,    COUNTBOOKUSED ,    COUNTBOOKREPAIR ,    COUNTREPAIR ,    COUNTREPAIRWITHFACTOR ,    COUNTREPAIRREPAIR ,    ACCOUNTID ,    CANCELNR ,    COSTSNEWCOMP ,    COSTSSORTOUTWEAR ,    COSTSSORTOUTBREAK ,    COSTSSORTOUTNEW ,    COSTSSORTOUTUSED ,    COSTSSORTOUTREPAIR ,    COSTSBOOKNEW ,    COSTSBOOKUSED ,    COSTSBOOKREPAIR ,    COSTSREPAIR ,    COSTSREPAIRWITHFACTOR ,    COSTSREPAIRREPAIR ,    BOOKTIMESTAMP )   SELECT    ?,    ?,    ?,    ?,    ?,    1,    0,    0,    0,    0,    0,    0,    0,    0,    0,    1,    0,    0,    0.000000,    0,    null,    00000000000.00000,    0 * LGM_COSTSV.NETPRICE,    0 * LGM_COSTSV.SORTOUTWEAR,    0 * LGM_COSTSV.SORTOUTBREAK,    0 * LGM_COSTSV.BOOKNEW,    0 * LGM_COSTSV.BOOKUSED,    0 * LGM_COSTSV.REPAIR,    0 * LGM_COSTSV.BOOKNEW,    1 * LGM_COSTSV.BOOKUSED,    0 * LGM_COSTSV.REPAIR,    0 * LGM_COSTSV.REPAIR,    0.000000 * LGM_COSTSV.REPAIR,    0 * LGM_COSTSV.REPAIR,    ? FROM  LGM_COSTSV  WHERE  LGM_COSTSV.ID  = ?  AND LGM_COSTSV.IDTYPE  = 1 ";
		String timestamp = System.currentTimeMillis() + "";
		timestamp = timestamp.substring(0, 10);
		if (comp.getCountNew() > 0 && comp.getCountUsed() == 0) {
			// new comp
			jdbc.update(sql_addConsumption_new, timestamp, toWhere.getCostunit(), toWhere.getWorkplace(), comp.getPhysicalID(), comp.getCompID(), timestamp, comp.getCompID());
		} else {
			//used comp
			jdbc.update(sql_addConsumption_used, timestamp, toWhere.getCostunit(), toWhere.getWorkplace(), comp.getPhysicalID(), comp.getCompID(), timestamp, comp.getCompID());
		}
	}

	/**
	 * add CostCenter Consumption in TDM (for issueing)
	 */
	@SuppressWarnings("static-access")
	public void addConsumptionWithCancel(String timestamp, BigDecimal cancelKey, String compID, String physicalID, String costunitTo, String workplaceTo, int countNew, int countUsed) throws SQLException {
		//?: timestamp, costunitTo, workplaceTo, physicalID, compID, cancelKey, countNew, countUsed, timestamp, compID
		String sql_addConsumption = "INSERT INTO  LGM_CHECKLISTBASE      ( TIMESTAMP ,    COSTUNIT ,    WORKPLACE ,    INVID ,    ID ,    IDTYPE ,    COUNTNEWCOMP ,    COUNTSORTOUTWEAR ,    COUNTSORTOUTWEARNOCOSTS ,    COUNTSORTOUTBREAK ,    COUNTSORTOUTBREAKNOCOSTS ,    COUNTSORTOUTNEW ,    COUNTSORTOUTUSED ,    COUNTSORTOUTREPAIR ,    COUNTBOOKNEW ,    COUNTBOOKUSED ,    COUNTBOOKREPAIR ,    COUNTREPAIR ,    COUNTREPAIRWITHFACTOR ,    COUNTREPAIRREPAIR ,    ACCOUNTID ,    CANCELNR ,    COSTSNEWCOMP ,    COSTSSORTOUTWEAR ,    COSTSSORTOUTBREAK ,    COSTSSORTOUTNEW ,    COSTSSORTOUTUSED ,    COSTSSORTOUTREPAIR ,    COSTSBOOKNEW ,    COSTSBOOKUSED ,    COSTSBOOKREPAIR ,    COSTSREPAIR ,    COSTSREPAIRWITHFACTOR ,    COSTSREPAIRREPAIR ,    BOOKTIMESTAMP )   SELECT    ?,    ?,    ?,    ?,    ?,    1,    0,    0,    0,    0,    0,    0,    0,    0,    ?,    ?,    0,    0,    0.000000,    0,    null,    ?,    0 * LGM_COSTSV.NETPRICE,    0 * LGM_COSTSV.SORTOUTWEAR,    0 * LGM_COSTSV.SORTOUTBREAK,    0 * LGM_COSTSV.BOOKNEW,    0 * LGM_COSTSV.BOOKUSED,    0 * LGM_COSTSV.REPAIR,    ? * LGM_COSTSV.BOOKNEW,    ? * LGM_COSTSV.BOOKUSED,    0 * LGM_COSTSV.REPAIR,    0 * LGM_COSTSV.REPAIR,    0.000000 * LGM_COSTSV.REPAIR,    0 * LGM_COSTSV.REPAIR,    ? FROM  LGM_COSTSV  WHERE  LGM_COSTSV.ID  = ?  AND LGM_COSTSV.IDTYPE  = 1 ";
		jdbc.update(sql_addConsumption, timestamp, costunitTo, workplaceTo, physicalID, compID, countNew, countUsed, cancelKey, countNew, countUsed, timestamp, compID);
	}
	
	/**
	 * add CostCenter Consumption in TDM (for new entry)
	 */
	@SuppressWarnings("static-access")
	public void addConsumptionWithCancel(String timestamp, BigDecimal cancelKey, String compID, String physicalID, String costunitTo, String workplaceTo, int countNew) throws SQLException {
		//?: timestamp, costunitTo, workplaceTo, physicalID, compID, countNew, cancelKey, countNew, timestamp, compID
		String sql_addConsumption = "INSERT INTO  TMS.LGM_CHECKLISTBASE      ( TIMESTAMP ,    COSTUNIT ,    WORKPLACE ,    INVID ,    ID ,    IDTYPE ,    COUNTNEWCOMP ,    COUNTSORTOUTWEAR ,    COUNTSORTOUTWEARNOCOSTS ,    COUNTSORTOUTBREAK ,    COUNTSORTOUTBREAKNOCOSTS ,    COUNTSORTOUTNEW ,    COUNTSORTOUTUSED ,    COUNTSORTOUTREPAIR ,    COUNTBOOKNEW ,    COUNTBOOKUSED ,    COUNTBOOKREPAIR ,    COUNTREPAIR ,    COUNTREPAIRWITHFACTOR ,    COUNTREPAIRREPAIR ,    ACCOUNTID ,    CANCELNR ,    COSTSNEWCOMP ,    COSTSSORTOUTWEAR ,    COSTSSORTOUTBREAK ,    COSTSSORTOUTNEW ,    COSTSSORTOUTUSED ,    COSTSSORTOUTREPAIR ,    COSTSBOOKNEW ,    COSTSBOOKUSED ,    COSTSBOOKREPAIR ,    COSTSREPAIR ,    COSTSREPAIRWITHFACTOR ,    COSTSREPAIRREPAIR ,    BOOKTIMESTAMP )   SELECT    ?,    ?,    ?,    ?,    ?,    1,    ?,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0.000000,    0,    null,    ?,    ? * LGM_COSTSV.NETPRICE,    ISNULL(null, 0 * LGM_COSTSV.SORTOUTWEAR),    ISNULL(null, 0 * LGM_COSTSV.SORTOUTBREAK),    0 * LGM_COSTSV.BOOKNEW,    0 * LGM_COSTSV.BOOKUSED,    0 * LGM_COSTSV.REPAIR,    0 * LGM_COSTSV.BOOKNEW,    0 * LGM_COSTSV.BOOKUSED,    0 * LGM_COSTSV.REPAIR,    0 * LGM_COSTSV.REPAIR,    0.000000 * LGM_COSTSV.REPAIR,    0 * LGM_COSTSV.REPAIR,    ? FROM  LGM_COSTSV  WHERE  LGM_COSTSV.ID  = ?  AND LGM_COSTSV.IDTYPE  = 1 ";
		jdbc.update(sql_addConsumption, timestamp, costunitTo, workplaceTo, physicalID, compID, countNew, cancelKey, countNew, timestamp, compID);
	}

	/**
	 * Put In Stock (new entry)
	 */
	@SuppressWarnings("static-access")
	public int putInStock(String costunit, String workplace, String stockID, String compID, String physicalID) throws SQLException {
		String sql_putIntoStock = "INSERT INTO LGM_COMPSTOCKBASELIST (COSTUNIT,WORKPLACE,COMPID,COMPINVID,STOCKPLACEID,COUNTNEW,COUNTUSED,COUNTREPAIR,LOCKED,BOOKSTATENR,TIMESTAMP) VALUES ('" + costunit + "','" + workplace + "','" + compID + "','" + physicalID + "','" + stockID + "',1,0,0,0,0,1502443894) ";
		return Common.objectToInt(jdbc.update(sql_putIntoStock));
	}

	/**
	 * New Physical ID And Put In Stock
	 */
	@SuppressWarnings("static-access")
	public boolean newPhysicalAndPutInStock(String costunit, String workplace, String stockID, String compID, String physicalID) throws SQLException {
		String sql_addPhysical = "INSERT INTO TDM_COMPINV (COMPID,COMPINVID,TIMESTAMP,IDTYPE) VALUES ('" + compID + "','" + physicalID + "',1502443894,1) ";
		String sql_putIntoStock = "INSERT INTO LGM_COMPSTOCKBASELIST (COSTUNIT,WORKPLACE,COMPID,COMPINVID,STOCKPLACEID,COUNTNEW,COUNTUSED,COUNTREPAIR,LOCKED,BOOKSTATENR,TIMESTAMP) VALUES ('" + costunit + "','" + workplace + "','" + compID + "','" + physicalID + "','" + stockID + "',1,0,0,0,0,1502443894) ";

		int[] result = jdbc.batchUpdate(sql_addPhysical, sql_putIntoStock);

		if (result.length == 2) {
			if (result[0] == 1 && result[1] == 1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * put into stock with cancel key
	 * @param timestamp
	 * @param cancelKey
	 * @param compID
	 * @param physicalID
	 * @param costunit
	 * @param workplace
	 * @param stockplaceID
	 * @param countNew
	 * @param countUsed
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("static-access")
	public int putInStockWithCancel(String timestamp, BigDecimal cancelKey, String compID, String physicalID, String costunit, String workplace, String stockplaceID, int countNew, int countUsed) throws SQLException{
		//timestamp, costunit, workplace, compID, physicalID, stockplaceID, cancelKey, countNew, countUsed
		String sql = "INSERT INTO  TMS.LGM_COMPSTOCKBASELIST      ( TIMESTAMP ,    COSTUNIT ,    WORKPLACE ,    COMPID ,    STOCKTYPEID ,    COMPINVID ,    STOCKPLACEID ,    BOOKSTATENR ,    CANCELNR ,    COUNTNEW ,    COUNTUSED ,    COUNTREPAIR ,    LOCKED ,    ACCOUNTID )    VALUES   ( ? ,    ? ,    ? ,    ? ,    null ,    ? ,    ? ,    0 ,    ? ,    ? ,    ? ,    0 ,    0 ,    null )   ";
		return jdbc.update(sql, timestamp, costunit, workplace, compID, physicalID, stockplaceID, cancelKey, countNew, countUsed);
	}
	

	/***************************DELETE****************************/

	/***************************UPDATE****************************/

	/**
	 * issue action
	 */
	@SuppressWarnings("static-access")
	public void issue(Comp comp, CostCenter toWhere, User operator) throws Exception {
		String sql_issueNew = "UPDATE LGM_COMPSTOCKBASELIST SET COSTUNIT=?, WORKPLACE=?, COUNTNEW=0, COUNTUSED=COUNTNEW WHERE COMPID=? AND COMPINVID=? ";
		String sql_issueUsed = "UPDATE LGM_COMPSTOCKBASELIST SET COSTUNIT=?, WORKPLACE=? WHERE COMPID=? AND COMPINVID=? ";

		if (comp.getCountNew() > 0 && comp.getCountUsed() == 0) {
			//new comp
			jdbc.update(sql_issueNew, toWhere.getCostunit(), toWhere.getWorkplace(), comp.getCompID(), comp.getPhysicalID());

		} else {
			//used comp
			jdbc.update(sql_issueUsed, toWhere.getCostunit(), toWhere.getWorkplace(), comp.getCompID(), comp.getPhysicalID());
		}
	}

	/**
	 * issue action
	 */
	@SuppressWarnings("static-access")
	public int issueWithCancel(String timestamp, BigDecimal cancelKey, String compID, String physicalID, String costunit, String workplace, int countNew, int countUsed) throws Exception {
		String sql_issue = "UPDATE LGM_COMPSTOCKBASELIST SET TIMESTAMP=?, CANCELNR=?, COSTUNIT=?, WORKPLACE=?, COUNTNEW=?, COUNTUSED=? WHERE COMPID=? AND COMPINVID=? ";

		return jdbc.update(sql_issue, timestamp, cancelKey, costunit, workplace, countNew, countUsed, compID, physicalID);
	}

	/***************************QUERY****************************/

	/**
	 * Get an available cancel key
	 * @return cancel key
	 */
	@SuppressWarnings("static-access")
	public BigDecimal newCancelKey() throws SQLException {
		String query_sql = "SELECT MAX(CANCELNR) FROM LGM_CANCEL";
		//Double key = Common.objectToDouble(jdbc.getSingle(query_sql));
		BigDecimal key = Common.objectToBigDecimal(jdbc.getSingle(query_sql));
		if (key.compareTo(new BigDecimal(0)) == 1) {
			key = key.add(new BigDecimal(Common.getRandom(2) * 100));
		} else {
			key = Common.objectToBigDecimal(Common.getTimestamp_TDM() + "0");
		}
		return key;
	}

	/**
	 * get comp detail info
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	public Map queryCompInfoMap(String compID) throws SQLException {
		String sql = "SELECT NAME, NAME2, TOOLCLASSID, TOOLGROUPID, CADID FROM TDM_COMP WHERE COMPID=?";
		List list = jdbc.query(sql, compID);
		if (list.isEmpty()) {
			return null;
		}
		return ((Map) list.get(0));

	}

	/**
	 * query all stockplaces in the cost center
	 * @throws SQLException 
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	public List<String> queryStocks(String costunit, String workplace) throws SQLException {
		String sql = "SELECT STOCKPLACEID, NAME FROM TMS_COSTUNITSTOCKPLACE WHERE COSTUNIT=? AND WORKPLACE=? AND STATE=0 ";
		List<String> stockList = new ArrayList<String>();
		List list = jdbc.query(sql, costunit, workplace);
		for (Object o : list) {
			Map m = (Map) o;
			String desc = Common.objectToString(m.get("NAME"));
			if (!Common.isNull(desc)) {
				//except null string like "null|123"
				desc = desc + "|";
			}
			stockList.add(desc + Common.objectToString(m.get("STOCKPLACEID")));
		}
		return stockList;
	}

	/**
	 * check comp if in stock
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	public Map checkStock(String compID, String physicalID) throws SQLException {
		String sql = "SELECT COSTUNIT, WORKPLACE, COMPID, COMPINVID, STOCKPLACEID FROM LGM_COMPSTOCKBASELIST WHERE COMPID=? AND COMPINVID=? ";
		List l = jdbc.query(sql, compID, physicalID);
		if (l.size() > 0) {
			return (Map) l.get(0);
		}
		return null;
	}

	/**
	 * check comp if exist
	 */
	@SuppressWarnings("static-access")
	public boolean isExistPhysical(String compID, String physicalID) throws SQLException {
		boolean result = false;
		String sql = "SELECT COUNT(1) FROM TDM_COMPINV WHERE COMPID = ? AND COMPINVID = ? ";

		Integer count = Common.objectToInt(jdbc.getSingle(sql, compID, physicalID));
		if (count > 0) {
			result = true;
		}
		return result;
	}

	/**
	 * check comp if exist
	 */
	@SuppressWarnings("static-access")
	public boolean isExistComp(String compID) throws SQLException {
		boolean result = false;
		String sql = "SELECT COUNT(1) FROM TDM_COMP WHERE COMPID = ? ";

		Integer count = Common.objectToInt(jdbc.getSingle(sql, compID));
		if (count > 0) {
			result = true;
		}
		return result;
	}

	/**
	 * query cost center list by type
	 * type : 1=Tool Crib, 2=Machine, 3=Maintance, 4=Allocation
	 */
	@SuppressWarnings({ "rawtypes", "static-access" })
	public List<CostCenter> queryCostCenters(int type) throws SQLException {
		String sql = "SELECT COSTUNIT, WORKPLACE, NAME, TYPE, LOCKED FROM TMS_COSTUNIT WHERE TYPE=? ";
		List list = jdbc.query(sql, type);
		if (list.isEmpty()) {
			return null;
		}
		List<CostCenter> costCenterList = new ArrayList<CostCenter>();

		for (Object o : list) {
			Map m = (Map) o;
			costCenterList.add(convertCostCenter(m));
		}

		return costCenterList;
	}

	/**
	 * Query ALL Cost Center
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings({ "rawtypes", "static-access" })
	public List<CostCenter> queryCostCenters() throws SQLException {
		String sql = "SELECT COSTUNIT, WORKPLACE, NAME, TYPE, LOCKED FROM TMS_COSTUNIT";
		List list = jdbc.query(sql);
		if (list.isEmpty()) {
			return null;
		}
		List<CostCenter> costCenterList = new ArrayList<CostCenter>();

		for (Object o : list) {
			Map m = (Map) o;
			costCenterList.add(convertCostCenter(m));
		}

		return costCenterList;
	}

	/**
	 * query one cost center
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	public CostCenter queryCostCenter(String costunit, String workplace) throws SQLException {
		String sql = "SELECT COSTUNIT, WORKPLACE, NAME, TYPE, LOCKED FROM TMS_COSTUNIT WHERE COSTUNIT=? AND WORKPLACE=? ";
		List list = jdbc.query(sql, costunit, workplace);
		if (list.isEmpty()) {
			return null;
		}
		return convertCostCenter((Map) list.get(0));
	}

	/**
	 * query comp stock info (before issue)
	 */
	@SuppressWarnings({ "rawtypes", "static-access" })
	public List<Comp> queryCompStock(String compID, String physicalID) throws Exception {
		//Query COMP STOCK INFO
		String sql_queryStock = "SELECT L.COSTUNIT, L.WORKPLACE, T.TYPE COSTCENTERTYPE, L.STOCKPLACEID, S.NAME STOCKPLACEDESC, L.COMPID, L.COMPINVID, C.NAME COMPDESC, C.NAME2 COMPDESC2, L.COUNTNEW, L.COUNTUSED FROM LGM_COMPSTOCKBASELIST L LEFT JOIN TDM_COMP C ON L.COMPID = C.COMPID LEFT JOIN TMS_COSTUNITSTOCKPLACE S ON S.COSTUNIT=L.COSTUNIT AND S.WORKPLACE=L.WORKPLACE AND S.STOCKPLACEID=L.STOCKPLACEID LEFT JOIN TMS_COSTUNIT T ON T.COSTUNIT=L.COSTUNIT AND T.WORKPLACE=L.WORKPLACE      WHERE L.COMPID = ? AND L.COMPINVID = ? ORDER BY COSTUNIT ";
		List list = jdbc.query(sql_queryStock, compID, physicalID);
		List<Comp> compList = new ArrayList<Comp>();
		for (Object o : list) {
			Map m = (Map) o;
			compList.add(convertComp(m));
		}
		return compList;
	}

	/***********************CONVERT******************************/

	/**
	 * convert to comp
	 */
	@SuppressWarnings({ "rawtypes" })
	private Comp convertComp(Map m) {
		Comp comp = null;
		//new comp object
		comp = new Comp(Common.objectToString(m.get("COMPID")), Common.objectToString(m.get("COMPINVID")), Common.objectToString(m.get("COMPDESC")), Common.objectToString(m.get("COMPDESC2")), Common.objectToString(m.get("COSTUNIT")), Common.objectToString(m.get("WORKPLACE")), Common.objectToString(m.get("STOCKPLACEID")), Common.objectToString(m.get("STOCKPLACEDESC")), Common.objectToInt(m.get("COUNTNEW")), Common.objectToInt(m.get("COUNTUSED")));
		comp.setCostCenterType(Common.objectToInt(m.get("TYPE"), 1));
		return comp;
	}

	/**
	 * convert to CostCenter
	 */
	@SuppressWarnings("rawtypes")
	private CostCenter convertCostCenter(Map m) {
		CostCenter cs = null;
		//new cost center
		cs = new CostCenter(Common.objectToString(m.get("COSTUNIT")), Common.objectToString(m.get("WORKPLACE")), Common.objectToString(m.get("NAME")), Common.objectToInt(m.get("TYPE")), Common.objectToInt(m.get("LOCKED")));
		return cs;
	}


}
