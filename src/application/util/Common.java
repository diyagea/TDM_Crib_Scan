package application.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Common {
	protected static Logger logger = LogManager.getLogger(Common.class);

	/**
	 * check blank string
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isBlankStr(String s) {
		if (s == null || "".equals(s) || " ".equals(s) || "".equals(s.trim())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 检查null
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNull(Object obj) {
		boolean nullFlag = false;
		if (obj == null || "".equals(obj) || " ".equals(obj)) {
			nullFlag = true;
		}
		return nullFlag;
	}

	/**
	 * convert object to String
	 */
	public static String objectToString(Object o) {
		String str = "";
		if (o == null) {
			return str;
		} else {
			return o.toString();
		}
	}

	/**
	 * convert object to Int
	 */
	public static Integer objectToInt(Object o) {
		Integer i = 0;
		try{
			if (o == null) {
				return 0;
			} else {
				return new Integer(o.toString());
			}
		}catch(Exception e){
			return i;
		}
	}
	/**
	 * convert object to Int with default
	 */
	public static Integer objectToInt(Object o, int defaultVal) {
		try{
			if (o == null) {
				return defaultVal;
			} else {
				return new Integer(o.toString());
			}
		}catch(Exception e){
			return defaultVal;
		}
	}
	
	/**
	 * convert object to Float
	 */
	public static Float objectToFloat(Object o) {
		Float f = 0.0f;
		try{
			if (o == null) {
				return f;
			} else {
				return new Float(o.toString());
			}
		}catch(Exception e){
			return f;
		}
	}
	
	/**
	 * convert object to Float
	 */
	public static Double objectToDouble(Object o) {
		Double d = 0.0;
		try{
			if (o == null) {
				return d;
			} else {
				return new Double(o.toString());
			}
		}catch(Exception e){
			return d;
		}
	}
	
	/**
	 * convert object to BigDecimal
	 */
	public static BigDecimal objectToBigDecimal(Object o) {
		BigDecimal b = new BigDecimal(0.0);
		try{
			if (o == null) {
				return b;
			} else {
				return new BigDecimal(o.toString());
			}
		}catch(Exception e){
			return b;
		}
	}

	/**
	 * 获得随机数
	 * 
	 * @param digit
	 *            位数
	 * @return 随机数
	 */
	public static int getRandom(int digit) {
		int max = (int) Math.pow(10, digit);
		int randomNumber = (int) Math.round(Math.random() * (max - 1) + 1);
		return randomNumber;
	}

	/**
	 * 获得时间字符串（毫秒级别，17位）
	 * 
	 * @return : 20170626095621123
	 */
	public static String getTimestamp2() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sdf.format(date);
	}

	/**
	 * 获得时间字符串（秒级别，14位）
	 * 
	 * @return : 20170626095621
	 */
	public static String getTimestamp() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(date);
	}

	/**
	 * 获得标准年月日时分秒
	 * 
	 * @return : 2017-06-26 09:56:21
	 */
	public static String getYMDHMS() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * 获取月日年
	 * 
	 * @return
	 */
	public static String getYMD() {
		FastDateFormat s = FastDateFormat.getInstance("yyyy-MM-dd");
		Date date = new Date();
		return s.format(date);
	}

	/**
	 * 根据开始时间和结束时间获取相差天数（广义上的天，并且不能跨年）都是以这天的0点开始计算 比如 1号（任何时间）都和二号错一天 2016-01-01
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @return int
	 */
	public static int getDaysSpace(String startDate, String endDate) {

		try {
			Date sDate = DateUtils.parseDate(startDate, "yyyy-MM-dd");
			Date eDate = DateUtils.parseDate(endDate, "yyyy-MM-dd");
			return getDaysSpace(sDate, eDate);
		} catch (ParseException e) {
			logger.error("GetDaysDistance：startDate【" + startDate + "】endDate【" + endDate + "】", e);
			return 0;
		}
	}

	/**
	 * 根据开始时间和结束时间获取相差天数（广义上的天，并且不能跨年） 比如 1号（任何时间）都和二号错一天
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 * @return int
	 */
	public static int getDaysSpace(Date sDate, Date eDate) {

		Calendar aCalendar = Calendar.getInstance();

		aCalendar.setTime(sDate);

		long time1 = aCalendar.getTimeInMillis();

		aCalendar.setTime(eDate);

		long time2 = aCalendar.getTimeInMillis();

		return new Long((time2 - time1) / (1000 * 3600 * 24)).intValue();
	}
	
	/**
	 * 根据开始时间和结束时间获取相差天数（广义上的天，并且不能跨年）都是以这天的0点开始计算 比如 1号（任何时间）都和二号错一天 2016-01-01
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @return int
	 */
	public static long getSecondsSpace(String startDate, String endDate) {

		try {
			Date sDate = DateUtils.parseDate(startDate, "yyyy-MM-dd");
			Date eDate = DateUtils.parseDate(endDate, "yyyy-MM-dd");
			return getSecondsSpace(sDate, eDate);
		} catch (ParseException e) {
			logger.error("GetDaysDistance：startDate【" + startDate + "】endDate【" + endDate + "】", e);
			return 0;
		}
	}

	/**
	 * 根据开始时间和结束时间获取相差天数（广义上的天，并且不能跨年） 比如 1号（任何时间）都和二号错一天
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 * @return int
	 */
	public static long getSecondsSpace(Date sDate, Date eDate) {

		Calendar aCalendar = Calendar.getInstance();

		aCalendar.setTime(sDate);

		long time1 = aCalendar.getTimeInMillis();

		aCalendar.setTime(eDate);

		long time2 = aCalendar.getTimeInMillis();

		return new Long((time2 - time1) / (1000));
	}
	
	/**
	 * 1970-1-1~now的秒数，TDM时间戳, 德国时差+8
	 * @return
	 * @throws ParseException
	 */
	public static String getTimestamp_TDM(){
		String result="";
		try {
			result = getSecondsSpace(DateUtils.parseDate("1970-01-01 08:00:00","yyyy-MM-dd HH:mm:ss"), new Date())+"";
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}
	
	/**
	 * 1600-1-1~now的天数，TDM日期
	 * @return
	 * @throws ParseException
	 */
	public static String getDate_TDM(){
		return getDaysSpace("1600-01-01", getYMD())+"";
		
	}

	public static void main(String[] args) throws ParseException {
		//TDM-6位时间戳
		System.out.println(getDate_TDM());
		//TDM-10位时间戳
		System.out.println(getTimestamp_TDM());

	}

}
