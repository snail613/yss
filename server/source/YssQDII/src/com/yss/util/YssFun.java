//要求j2se 1.4
package com.yss.util;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.yss.dsub.DbBase;
import com.yss.serve.UserCheck;

/**
 * <p>Title:公用函数，请勿加入数据库访问类函数！ </p>
 * <p>Description: 这个类包含了一些常用的函数，如日期的分析处理，数值的判断等等
 * <br>一些VB中常用java中使用不便的函数</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @author alex
 * @version 1.0
 */
public class YssFun {
    public YssFun() {
    }

//时间日期类函数****************************************************************
//这些函数都是基于java.util.date，java.sql.date类型的参数也可填入处理

    /**两种日期类型转换
     * java.sql.date本身就是java.util.date不用转换
     * java.util.date到java.sql.date的转换用这个函数实现
     */
    public static java.sql.Date toSqlDate(Date dDate) {
        return new java.sql.Date(dDate.getTime());
    }

    public static java.sql.Date toSqlDate(String strDate) throws YssException {
    	if(strDate!=null && !strDate.trim().equals("")){//modified by guyichuan STORY #741 20110516
    		Date dDate = toDate(strDate);
    		return new java.sql.Date(dDate.getTime());
    	} 
        return null;
    }

    /**
     * 日期格式化，类似vb的format
     * @param dDate Date
     * @param format String：日期格式
     * @return String：返回格式化好的日期字符串
     */
    public static String formatDate(Date dDate, String format) {
        return (new SimpleDateFormat(format)).format(dDate);
    }

    /**
     * 日期格式化，采用日期字符串做源日期参数
     * @param sDate String
     * @param format String
     * @throws YssException
     * @return String
     */
    public static String formatDate(String sDate, String format) throws
        YssException {
        if (format.equalsIgnoreCase(YssCons.YSS_DATEFORMAT)) {
            sDate = YssFun.left(sDate, 10);
        }
        return formatDate(toDate(sDate), format);
    }

    public static String formatDate(String sDate) throws
        YssException {
        return formatDate(sDate, YssCons.YSS_DATEFORMAT);
    }

    /**日期格式化，用标准格式yyyy-MM-dd*/
    public static String formatDate(Date dDate) {
        return (new SimpleDateFormat(YssCons.YSS_DATEFORMAT)).format(dDate);
    }

    /**时间格式化，用标准格式yyyyMMdd hh:mm:ss*/
    public static String formatDatetime(Date dDate) {
        return (new SimpleDateFormat(YssCons.YSS_DATETIMEFORMAT)).format(dDate);
    }

    /**字符串to日期时间
     * 注意要指定日期格式，不像vb可以自动识别
     */
    public static Date parseDate(String sDate, String format) throws
        YssException {
        GregorianCalendar cl = new GregorianCalendar();
        final String DERR = "解析日期出错！";
        int year;

        try {
            cl.setTime( (new SimpleDateFormat(format)).parse(sDate));
            year = cl.get(Calendar.YEAR);
            //年份要控制一下
            if (year < 1000 || year > 9999) {
                throw new YssException(DERR + "\n\t年份必须在1000－9999之间");
            }

            return cl.getTime();
        } catch (ParseException pe) {
            throw new YssException(DERR, pe);
        }
    }

    /**日期解析，用标准格式yyyy-MM-dd*/
    public static Date parseDate(String sDate) throws YssException {
        return parseDate(sDate, YssCons.YSS_DATEFORMAT);
    }

    /**是否可以转换成日期，仅解析用/-.间隔的日期
     * dDate用于返回日期，改写dDate中已经存在的日期
     */
    public static boolean isDate(String sDate, Date dDate){
        long ltmp;
        try {
            ltmp = toDate(sDate).getTime();

            if (dDate != null) {
                dDate.setTime(ltmp);
            }
            return true;
        //edit by songjie 2013.01.07 STORY #2343 QDV4建行2012年3月2日04_A 
        //由 YssException 改为 Exception 
        } catch (Exception ye) {
        	return false;
        }
    }

    public static final boolean isDate(String sDate){
        return isDate(sDate, null);
    }

    /**类似vb的CDate函数，自动分析sDate，如格式正常，返回日期，否则报错。
     * 注意这里只能处理单纯日期，不处理时间，年份正常范围在0-99和1000－9999
     *仅解析用/-.间隔的日期
     */
    public static Date toDate(String sDate) throws YssException {
        int jj;
        char ss, cc;
        String[] sss = {
            "-", "/", "."};
        String[] result;
        int kk, mm;
        final String emsg = "非法日期格式！";

        GregorianCalendar cl = null;

        //检查分隔符
        for (jj = 0; jj < sss.length; jj++) {
            if (sDate.indexOf(sss[jj]) >= 0) {
                break;
            }
        }
        if (jj >= sss.length) {
            throw new YssException(emsg);
        }

        ss = sss[jj].charAt(0);
        //检查数字有效性即除了数字和分隔符，不应该再包括其它字符
        for (int i = 0; i < sDate.length(); i++) {
            cc = sDate.charAt(i);
            if (cc != ss && (cc < '0' || cc > '9')) {
                throw new YssException(emsg);
            }
        }

        //劈开，获取3个数字
        result = sDate.split(sss[jj], -1); //检查全部，包括空的元素，用0会忽略空
        if (result.length != 3) {
            throw new YssException(emsg);
        }
        jj = Integer.parseInt(result[0]);
        kk = Integer.parseInt(result[1]);
        mm = Integer.parseInt(result[2]);

        //判断是否符合一种日期格式
        //1、y/M/d格式
        if (isValidDate(jj, kk, mm)) {
            cl = new GregorianCalendar(jj < 30 ? jj + 2000 :
                                       (jj <= 99 ? jj + 1900 : jj), kk - 1, mm);
        } else {
            if (mm < 30) {
                mm += 2000;
            } else if (mm <= 99) {
                mm += 1900;
                //2、M/d/y格式
            }
            if (isValidDate(mm, jj, kk)) {
                cl = new GregorianCalendar(mm, jj - 1, kk);
                //3、d/M/y格式
            } else if (isValidDate(mm, kk, jj)) {
                cl = new GregorianCalendar(mm, kk - 1, jj);
            } else {
                throw new YssException(emsg);
            }
        }
        return cl.getTime();
    }

    /**判断年月日是否在正常范围
     * 年份正常范围在0-99和1000－9999
     */
    public static boolean isValidDate(int year, int month, int day) {
        GregorianCalendar cl;

        if (year < 0 || (year > 99 && (year < 1000 || year > 9999))) {
            return false;
        }
        if (year < 30) {
            year += 2000;
        } else if (year <= 99) {
            year += 1900;

        }
        if (month < 1 || month > 12) {
            return false;
        }

        cl = new GregorianCalendar(year, month - 1, 1); //参数月份从0开始所以减一
        if (day < cl.getActualMinimum(Calendar.DAY_OF_MONTH) ||
            day > cl.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            return false;
        }

        return true;
    }

    /**得到年份(月份getMonth，日getDay，星期getWeekDay)
     * 包装了GregorianCalendar类的get方法
     * Calendar类中定义了月份，星期几的常数。
     * <br><big><b>注意月份常数0开始，一般不要使用</b></big>
     */
    public static int getYear(Date dDate) {
        return getDateItems(dDate, Calendar.YEAR);
    }

    //得到月份
    public static int getMonth(Date dDate) {
        return getDateItems(dDate, Calendar.MONTH) + 1;
    }

    //日
    public static int getDay(Date dDate) {
        return getDateItems(dDate, Calendar.DAY_OF_MONTH);
    }

    //星期几
    public static int getWeekDay(Date dDate) {
        return getDateItems(dDate, Calendar.DAY_OF_WEEK);
    }

    /**返回日期中的任何元素
     * @param field：Calendar类中的常数，如YEAR/MONTH/DAY_OF_MONTH...
     * <br><b><big>注意返回的month一月份是从0开始的！</big></b>
     */
    public static final int getDateItems(Date dDate, int field) {
        GregorianCalendar cl = new GregorianCalendar();
        cl.setTime(dDate);
        return cl.get(field);
    }

    /**加减day，月份是addMonth，年份addYear
     */
    public static final Date addDay(Date dDate, int days) {
        return addDate(dDate, days, Calendar.DAY_OF_MONTH);
    }

    public static final Date addMonth(Date dDate, int months) {
        return addDate(dDate, months, Calendar.MONTH);
    }

    public static final Date addYear(Date dDate, int years) {
        return addDate(dDate, years, Calendar.YEAR);
    }

    /**加减日期
     * field指定是年、月、日，amount是数量
     */
    public static Date addDate(Date dDate, int amount, int field) {
        GregorianCalendar cl = new GregorianCalendar();
        cl.setTime(dDate);
        cl.add(field, amount);

        return cl.getTime();
    }

    /**
     * 返回两个日期相差date2-date1的天数
     * @param dDate1：被减日期
     * @param dDate2：日期
     */
    public static int dateDiff(java.util.Date dDate1, java.util.Date dDate2) {
        int year = 0;
        int month = 0;
        int day = 0;
//算法说明：这里考虑到了时分秒的差异可能造成的差一天，只设置年月日相减算出差异
//失败算法：毫秒数相减除一天毫秒；毫秒数先除再减；毫秒数弥补时区差后先除再减
//失败算法：设置calendar的时分秒为0后相减（有时设置不灵）
        GregorianCalendar cl1 = new GregorianCalendar();
        GregorianCalendar cl2 = null;

        cl1.setTime(dDate2);
        //cl1.set(Calendar.MONTH,YssFun.getMonth(dDa);
        year = cl1.get(Calendar.YEAR);
        month = cl1.get(Calendar.MONTH);
        day = cl1.get(Calendar.DAY_OF_MONTH);

        cl2 = new GregorianCalendar(year, month, day);

        cl1.setTime(dDate1);
        year = cl1.get(Calendar.YEAR);
        month = cl1.get(Calendar.MONTH);
        day = cl1.get(Calendar.DAY_OF_MONTH);
        cl1.clear();
        cl1.set(year, month, day);

        return (int) ( (cl2.getTimeInMillis() - cl1.getTimeInMillis()) /
                      (1000 * 3600 * 24));
    }

    /**
     * date2-当前日期的差
     */
    public static int dateDiff(java.util.Date dDate2) {
        return dateDiff(new java.util.Date(), dDate2);
    }

    /**
     * add by songjie 2012.06.08 
     * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
     * @param date1 减数
     * @param date2 被减数
     * @return 以时分秒格式 返回 两个日期之差
     */
    public static String dateTimeDiff(java.util.Date date1, java.util.Date date2){
    	String hour = "";
    	String minutes = "";
    	String seconds = "";
    	String logDealTime = "";
        GregorianCalendar cl1 = new GregorianCalendar();
        GregorianCalendar cl2 = new GregorianCalendar();;

        cl1.setTime(date1);
        cl2.setTime(date2);
        
        int i = (int)(cl2.getTimeInMillis() - cl1.getTimeInMillis())/1000;
        if(i <= 59){
        	//一分钟以内
        	logDealTime = "00:00:" +  YssFun.formatNumber(i, "00");
        } else{
        	if(i <= 3599){
        		//一小时以内
        		logDealTime = "00:" + YssFun.formatNumber(i/60,"00") + ":" + YssFun.formatNumber(i%60,"00");
        	}else{
        		//一天以内
        		if(i <= 86399){
        		    hour = YssFun.formatNumber(i/(60 * 60),"00");
        		    int j = i%(60 * 60);
        			if(j <= 59){
        				minutes = "00";
        				seconds = YssFun.formatNumber(j,"00");
        			}else{
        				minutes = YssFun.formatNumber(j/60,"00");
        				seconds = YssFun.formatNumber(j%60,"00");
        			}
        			logDealTime  =  hour + ":" + minutes + ":" + seconds;
        		}else{
        			//一天以上
        			int j = i%(60 * 60 * 24);
        			hour = YssFun.formatNumber(j/(60 * 60),"00");
        			int k = j%(60 * 60);
        			if(k <= 59){
        				minutes = "00";
        				seconds = YssFun.formatNumber(k,"00");
        			}else{
        				minutes = YssFun.formatNumber(k/60,"00");
        				seconds = YssFun.formatNumber(k%60,"00");
        			}
        			logDealTime  = (int)i/(60 * 60 * 24) + " "+ hour + ":" + minutes + ":" + seconds;
        		}
        	}
        }
    	return logDealTime;
    }
    
    /**
     * 通过只算年份和月份，获取月份差，不计四舍五入
     * @param dDate1 Date：起始月
     * @param dDate2 Date：终止月
     * @return int：dDate2-dDate1的月份差
     */
    public static int monthDiff(java.util.Date dDate1, java.util.Date dDate2) {
        int year, month;
        GregorianCalendar cld = new GregorianCalendar();

        cld.setTime(dDate2);
        year = cld.get(Calendar.YEAR);
        month = cld.get(Calendar.MONTH);

        cld.setTime(dDate1);
        year -= cld.get(Calendar.YEAR);
        month -= cld.get(Calendar.MONTH);

        return year * 12 + month;
    }
    
    /**
     * add by huhuichao story 3899 指定日期资产负债表和期间段利润表
     * 通过只算年份，获取年份差，不计四舍五入
     * @param dDate1 Date：起始年
     * @param dDate2 Date：终止年
     * @return int：dDate2-dDate1的年份差
     */
    public static int yearDiff(java.util.Date dDate1, java.util.Date dDate2) {
        int year;
        GregorianCalendar cld = new GregorianCalendar();

        cld.setTime(dDate2);
        year = cld.get(Calendar.YEAR);
        cld.setTime(dDate1);
        year -= cld.get(Calendar.YEAR);
        return year ;
    }

    /**
     * date2-当前日期，月份差
     */
    public static int monthDiff(java.util.Date dDate2) {
        return monthDiff(new java.util.Date(), dDate2);
    }

    /**判断闰年
     * return new GregorianCalendar().isLeapYear(year);
     */
    public static boolean isLeapYear(int year) {
        return new GregorianCalendar().isLeapYear(year);
    }

    public static boolean isLeapYear(Date dDate) {
        GregorianCalendar cl = new GregorianCalendar();
        cl.setTime(dDate);
        return cl.isLeapYear(cl.get(Calendar.YEAR));
    }

    /**获取指定年份范围内的闰年数
     * @param startYear：起始年（包含）
     * @param endYear：结束年（包含）
     */
    public static int getLeapYears(int startYear, int endYear) {
        int count = 0;
        GregorianCalendar cl = new GregorianCalendar();
        for (int i = startYear; i <= endYear; i++) {
            if (cl.isLeapYear(i)) {
                count++;
            }
        }
        return count;
    }

    /**获取指定日期范围内的闰年数
     * @param startDate：起始日期（包含）
     * @param endDate：结束日期（包含）
     */
    public static int getLeapYears(Date startDate, Date endDate) {
        int syear, eyear;
        syear = getYear(startDate);
        eyear = getYear(endDate);
	//------ modify by wangzuochun 2010.04.17 MS00980    程序在获取计息天数时因闰年判断有误导致债券业务数据有错    QDII4.1赢时胜上海2010年04月14日01_B 
        if (formatDate(startDate, "MMdd").compareTo("0229") > 0) {
            syear++;
        }
        if (formatDate(endDate, "MMdd").compareTo("0229") < 0) {
            eyear--;
        }
	//---------------------- MS00980 ---------------------------//
        return getLeapYears(syear, eyear);
    }

    /**月末日28/29/30/31？
     *使用GregorianCalendar.getActualMaximum
     */
    public static int endOfMonth(int year, int month) {
        return new GregorianCalendar(year, month - 1,
                                     1).getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int endOfMonth(Date dDate) {
        GregorianCalendar cl = new GregorianCalendar();
        cl.setTime(dDate);
        return cl.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

//数字类函数********************************************************************
//(因为金额小数只有两位，所以double可以表示足够大的数据)

    /**判断是否数值
     *Decimal
     */
    public static boolean isNumeric(String sNum) {
        try {
            if (sNum == null || sNum.trim().length() == 0) {
                return false;
            }
            toNumber(sNum);
            return true;

        } catch (YssException ye) {
            return false;
        }
    }

    /**
     * 自动分析sNum格式，解析成double类型返回
     * 支持千分符，科学计数，百分数...前后空格，前导的+号和0
     * 遇其它非法格式报错
     */
    public static double toNumber(String sNum) throws YssException {
        int i = 0;
        DecimalFormat df = new DecimalFormat(sNum.indexOf("%") > 0 ? "#,###.#%" :
                                             "#,###.#E0");

        try {
            sNum = trim(sNum);
            if (sNum.length() > 1) {
                if (sNum.charAt(0) == '0') { //去前导连续0，注意只有一个0的情况
                    if (sNum.charAt(1) != '.') {
                        for (i = 0; i < sNum.length() - 1; i++) {
                            if (sNum.charAt(i) != '0') {
                                break;
                            }
                        }
                    }
                } else if (sNum.charAt(0) == '+') {
                    i = 1;
                }
            }
            if (sNum == "") {
                sNum = "0";
            }
            return df.parse(i == 0 ? sNum : sNum.substring(i)).doubleValue();
        } catch (Exception pe) {
            throw new YssException("非法数值格式！", pe);
        }
    }

    /**
     * 把字符串变双精度，格式不对返回0，不报错
     * @param sNum String
     * @return int
     */
    public static double toDouble(String sNum) {
        try {
            if (sNum.equalsIgnoreCase("")) {
                return 0.0;
            }
            return toNumber(sNum);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 把字符串变整数，格式不对返回0，不报错
     * @param sNum String
     * @return int
     */
    public static int toInt(String sNum) {
        try {
            return (int) toNumber(sNum);
        } catch (Exception e) {
            return 0;
        }
    }

    /**数字格式化，注意不一定四舍五入，有时候（偶数后的5）舍5！
     * 相当于vb中的format，不过只能是数字
     */
    public static final String formatNumber(double number, String format) {
        return (new DecimalFormat(format)).format(number);
    }

    public static final String formatNumber(long number, String format) {
        return (new DecimalFormat(format)).format(number);
    }

    /**数字取指定小数位数，默认四舍五入
     * 如果bTranc=true则舍去下一位
     */
    public static double roundIt(double val1, int lDecs, boolean bTrunc) {
        return YssD.round(val1, lDecs, bTrunc); //新算法，旧算法在5入加法的时候会出现不精确
        /*      //这里不能用其它方式把数据变成string，考虑科学记数法。另外，在初始化BigDecimal
              //时应该用double转成String以后得内容，如果直接用double可能会造成不精确
         StringBuffer stmp = new StringBuffer(new BigDecimal(String.valueOf(val1)).
                                                   toString());
              int ldec = stmp.indexOf(".");
              double dtmp;
              char ctmp;

              if (lDecs < 0 || ldec <= 0)
                 dtmp= val1;
              else{
                 if (stmp.length() <= ldec + lDecs + 1) {
                    dtmp= Double.parseDouble(stmp.toString());
                 }
                 else {
         dtmp = Double.parseDouble(stmp.substring(0, ldec + lDecs + 1));
                    if (bTrunc)
                       return dtmp;

                    ctmp = stmp.charAt(ldec + lDecs + 1);
                    if (ctmp >= '5' && ctmp <= '9')
                       dtmp += Math.pow(10, -lDecs) * (val1 > 0 ? 1 : -1);
                 }
              }
              //double值居然会是-0.0 :(
              if (dtmp==0)
         dtmp=0;//防止出现-0.00，奇怪的是如果dtmp=-0.00，evaluation时它不等于0，在代码中却=0，浪费我不少时间
              return dtmp;*/
    }

    public static final double roundIt(double val1, int lDecs) {
        return roundIt(val1, lDecs, false);
    }

    /**
     * 数值格式化函数，与formatNumber不同，这个可以保证四舍五入！
     * 另有同名的日期格式化函数，以兼容原vb下的同名函数
     */
    public static String roundF(double val1, String sForm) {
        int ldec = 2;
        String stmp;
        boolean percent = false;

        //先round再format
        if (sForm.length() != 0) {
            stmp = sForm.trim();
            if (stmp.charAt(stmp.length() - 1) == '%') {
                stmp = stmp.substring(0, stmp.length() - 1).trim();
                percent = true;
            }
            ldec = stmp.indexOf(".");
            if (ldec >= 0) {
                ldec = stmp.length() - ldec - 1;
            } else {
                ldec = 0;

            }
            if (percent) {
                ldec += 2;
            }
            val1 = roundIt(val1, ldec);
        }

        return new DecimalFormat(sForm).format(val1);
    }

    public static final String roundF(Date val1, String sForm) {
        return formatDate(val1, sForm);
    }

//字符串类函数********************************************************************
    /**java可以实现left,right,mid，可是如果超过长度，会报错，这里提供容错版本*/
    public static final String left(String sSrc, int iLen) {
    	//modfiy by zhangfa 20101011 MS01791    纽约银行数据接口，选择接口类型INFORM给出文件不存在的提示    QDV4赢时胜(上海开发部)2010年09月09日10_B 
        if(sSrc==null)  return " ";
      //---------------------------------------------------------------------------------------------------------------------------------
        if (iLen >= sSrc.length()) {
            return sSrc;
        }
        return sSrc.substring(0, iLen);
        
    }

    public static final String right(String sSrc, int iLen) {
        if (iLen >= sSrc.length()) {
            return sSrc;
        }
        return sSrc.substring(sSrc.length() - iLen);
    }

    public static final String mid(String sSrc, int iStart, int iLen) {
        if (iStart + iLen >= sSrc.length()) {
            return sSrc.substring(iStart);
        }
        return sSrc.substring(iStart, iStart + iLen);
    }

    public static final String mid(String sSrc, int iStart) {
        return sSrc.substring(iStart);
    }

    /**
     * 去除字符串尾部空格
     * @param sSrc String：源串
     * @return String：去除尾部空格后的结果
     */
    public static final String rTrim(String sSrc) {
        int i;

        for (i = sSrc.length() - 1; i >= 0; i--) {
            if (sSrc.charAt(i) != ' ') {
                break;
            }
        }
        if (i < 0) {
            return "";
        }
        return sSrc.substring(0, i + 1);
    }

    /**
     * 去除字符串开头空格
     * @param sSrc String：源串
     * @return String：去除开头空格后的结果
     */
    public static final String lTrim(String sSrc) {
        int len = sSrc.length();
        int i;

        for (i = 0; i < len; i++) {
            if (sSrc.charAt(i) != ' ') {
                break;
            }
        }
        if (i >= len) {
            return "";
        }
        return sSrc.substring(i);
    }

    /**
     * 去除字符串首尾的空格
     * @param sSrc String：源串
     * @return String：去除空格后的结果串
     */
    public static final String trim(String sSrc) {
        int i, j;
        //去除尾部空格
        for (i = sSrc.length() - 1; i >= 0; i--) {
            if (sSrc.charAt(i) != ' ') {
                break;
            }
        }
        if (i < 0) {
            return "";
        }
        //去除开头空格
        for (j = 0; j < i; j++) {
            if (sSrc.charAt(j) != ' ') {
                break;
            }
        }
        return sSrc.substring(j, i + 1); //返回从j到i的字符
    }

    /**其实string的split方法，如果limit参数为负数，也可以显示这一功能，只是默认是0
     *
     * 代替java的String类的split方法，即使最后以分隔符结束，也能正确返回最后的空串
     * @param sSrc String：要split的原始串
     * @param delimiter String：分隔符
     * @return String[]：返回字符串数组
     */
    public static final String[] split(String sSrc, String delimiter) {
        String[] sarr;
        int l, len;

        sarr = java.util.regex.Pattern.compile(delimiter, 0).split(sSrc + " ", 0);
        l = sarr.length - 1;
        len = sarr[l].length();
        if (len <= 1) {
            sarr[l] = "";
        } else {
            sarr[l] = sarr[l].substring(0, len - 1);
        }
        return sarr;
    }

    /**
     * 将指定范围的数组元素组合起来成一个字符串
     * @param sArr String[]
     * @param delimiter String：连接字符
     * @param iStart int：起始元素位置
     * @param iEnd int：截止元素
     * @return String：组合成的字符串
     */
    public static final String join(String[] sArr, String delimiter, int iStart,
                                    int iEnd) {
        StringBuffer buf = new StringBuffer();

        for (int i = (iStart != 0 ? iStart : 0);
             i < (iEnd != 0 ? iEnd + 1 : sArr.length); i++) {
            buf.append(delimiter).append(sArr[i]);

        }
        return (buf.length() > 0 && delimiter.length() > 0 ?
                buf.substring(delimiter.length()) : buf.toString());
    }

    public static final String join(String[] sArr, String delimiter, int iStart) {
        return join(sArr, delimiter, iStart, 0);
    }

    public static final String join(String[] sArr, String delimiter) {
        return join(sArr, delimiter, 0, 0);
    }

//取得本月最大日期
    public static Date yssGetMaxDate(int year, int month) throws YssException {
        GregorianCalendar cl = null;
        try {
            cl = new GregorianCalendar();
            cl.setTime(new Date());
            cl.set(year, month - 1, YssFun.endOfMonth(year, month));

        } catch (Exception ex) {
            throw new YssException("日期转换错误！", ex);
        }
        return cl.getTime();
    }

    //取得本月最小日期
    public static Date yssGetMinDate(int year, int month) throws YssException {
        GregorianCalendar cl = null;
        try {
            cl = new GregorianCalendar();
            cl.setTime(new Date());
            cl.set(year, month - 1, 1);

        } catch (Exception ex) {
            throw new YssException("日期转换错误！", ex);
        }
        return cl.getTime();
    }

    /**
     * 获得工作日
     * loffset=0：如果ddate不是节假日，就返回ddate，否则取下一个工作日
     * loffset<0取T-n，>0取T+n
     * Fbz 工作日方式：0：交易所，1：银行
     * 算法：ddate不停递增/减，如果不是节假日则递增/减n，如果n已经达到，并且ddate不是节假日，则OK
     * @param dDate Date
     * @param lOffset int
     * @param Fs int
     * @throws YssException
     * @return Date
     */
    public static java.util.Date Get_WorkDay(DbBase db1, java.util.Date dDate,
                                             int lOffset, int Fs) throws
        YssException {
        ResultSet Rs = null;
        int lTmp = 0, lStep;

        try {
            lStep = (lOffset < 0) ? -1 : 1;
            Rs = db1.openResultSet("select FDate from CsHoliDay where fdate" +
                                   ( (lOffset < 0) ? "<=" : ">=")
                                   + db1.sqlDate(dDate, false) + " and FBz = " +
                                   Fs + " order by fdate" +
                                   ( (lOffset < 0) ? " desc" : ""),
                                   ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (Rs.next()) {
                do {
                    if (YssFun.formatDate(Rs.getDate("FDate"),
                                          "yyyy-MM-dd").equalsIgnoreCase(YssFun.
                        formatDate(
                            dDate, "yyyy-MM-dd"))) {
                        Rs.next();
                        if (lTmp == 0) {
                            lTmp += lStep;
                        }
                    } else {
                        if (Math.abs(lTmp) >= Math.abs(lOffset)) {
                            break;
                        }
                        lTmp += lStep;
                    }
                    dDate = YssFun.addDay(dDate, lStep);

                } while (!Rs.isAfterLast());
            }
            //如果eof的话，就是查找工作日出了节假日边界，需要定义更多节假日！
            if (Rs.isAfterLast()) {
                throw new YssException( ( (lOffset < 0) ? "上" : "下") +
                                       "一个工作日已经超越节假日的边界，请增加节假日定义！");
            }
            Rs.getStatement().close();
            Rs = null;
            return dDate;
        } catch (SQLException sqle) {
            throw new YssException("访问节假日表出错！", sqle);
        } finally {
            try {
                if (Rs != null) {
                    Rs.getStatement().close();
                }
            } catch (Exception se) {
            	System.out.println(se.getMessage()); //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            }
        }
    }

    public static java.util.Date Get_WorkDay(DbBase db1, java.util.Date dDate,
                                             int lOffset) throws
        YssException {
        return Get_WorkDay(db1, dDate, lOffset, 0);

    }

    public static java.util.Date Get_WorkDay(DbBase db1, java.util.Date dDate) throws
        YssException {
        return Get_WorkDay(db1, dDate, 0, 0);

    }

    public static String getCheckStateName(int iCheckState) {
        String sResult = "";
        if (iCheckState == 0) {
            sResult = "未审核";
        } else if (iCheckState == 1) {
            sResult = "已审核";
        } else if (iCheckState == 2) {
            sResult = "回收站";
        }
        return sResult;
    }

    public static String getImpExpType(int iType) {
        String sResult = "";
        if (iType == 0) {
            sResult = "导入";
        } else if (iType == 1) {
            sResult = "导出";
        }
        return sResult;
    }

    //读取文本文件
    public static String loadTxtFile(String sFileName) throws YssException {
        java.io.File ff = null;
        File password = null;//加密文件路径流
        java.io.FileInputStream fi = null;
        int i = 0, j = 0;
        int size = -1;
        try {
        	ff = new java.io.File(sFileName);
        	/**shashijie 2012-7-11 BUG 4864 修改原有逻辑,优先找/WEB-INF/下加密dbsetting.txt文件*/
        	//加密路径
        	/**shashijie 2012-11-23 STORY 3327 BUG 6406 控制路径后面的"\"符号只出现一个 */
			String urlString = YssUtil.getPath(UserCheck.requestPath)
					+ "WEB-INF" + ff;
			/**end shashijie 2012-11-23 STORY 3327 */
			password = new java.io.File(urlString);
			if (password.exists()) {
				//解密方法
				String value = getPassWordFile(urlString);
				return value;
			} else {
				fi = new java.io.FileInputStream(ff);
			}
			/**end*/
        	
			//原先逻辑
            /*ff = new java.io.File(sFileName);
            *//**shashijie 2012-3-14 STORY 2340 *//*
			if (ff.exists()) {//如果文件存在则走原流程,否则就去/WEB-INF/下找
				fi = new java.io.FileInputStream(ff);
			} else {
				//加密路径
				String urlString = UserCheck.requestPath+File.separator+"WEB-INF"+File.separator+ff;
				//解密方法
				String value = getPassWordFile(urlString);
				return value;
			}*/
			/**end*/
            
            size = (int) ff.length();
            byte[] fb = new byte[size];

            while (i < size) {
                j = fi.read(fb, i, size - i);
                if (j < 0) {
                    break;
                }
                i += j;
            }
            return new String(fb, "gb2312");
        } catch (Exception e) {
            throw new YssException("读取文件【" + sFileName + "】错误", e);
        } finally {
            ff = null;
            password = null;
            
            try {
            	/**shashijie 2012-3-14 STORY 2340*/
            	if (fi!=null) {
            		fi.close();
				}
				/**end*/
            } catch (IOException ex) {
            }
            fi = null;
        }
    }

    /**shashijie 2012-3-14 STORY 2340 解密方法
	* @param urlString
	* @return*/
	private static String getPassWordFile(String urlString) {
		String value = "";
		try {
			//初始构造
			/**shashijie 2012-8-2 BUG 4864 解决Linux环境下无法登录问题 */
			//DESUtil des = new DESUtil("Copyright @ 2001-2010 Ysstech,All Rights Reserved 粤ICP备05050184号");
			YssEncrypt des = new YssEncrypt("Copyright @ 2001-2010 Ysstech,All Rights Reserved 粤ICP备05050184号");
			/**end*/
			//读取加密文件内容
			java.io.File ff = new java.io.File(urlString);
			java.io.FileInputStream fi = new java.io.FileInputStream(ff);
			int size = (int) ff.length();
			byte[] fb = new byte[size];
			int i = 0, j = 0;// 行,列
			while (i < size) {
				j = fi.read(fb, i, size - i);
				if (j < 0)
					break;
				i += j;
			}
			//获取估值系统的数据库连接设置，忽略空行和星号开头的行
			value = new String(fb, "gb2312");
			//解密
			value = des.decryptStr(value);
		} catch (Exception e) {
			value = "";
		} finally {

		}
		return value;
	}
	

	/**
     * 获取一个字符中的参数
     * @param str String
     * @param sP1 String：标识字符1
     * @param sP2 String：标识字符2
     * @return String：返回字符中存在的参数，中间用","分开
     */
    public static String getStrParams(String str, String sP1, String sP2) throws YssException {
        String sParams = "";
        int iFrom = 0;
        int iEnd = -1;
        if (str.length() == 0 || str.equalsIgnoreCase("null")) {
            return "";
        } while (iFrom != -1) {
            iFrom = str.indexOf(sP1, iFrom);
            iEnd = str.indexOf(sP2, iFrom);
            if (iEnd == -1 && iFrom > 0) {
                throw new YssException("没有找到与【" + sP1 + "】相匹配的【" + sP2 + "】字符");
            }
            if (iEnd > -1 && iFrom != -1) {
                sParams += str.substring(iFrom + 1, iEnd) + ",";
                iFrom = iEnd;
            }
        }
        if (sParams.length() > 1) {
            sParams = sParams.substring(0, sParams.length() - 1);
        }
        return sParams;
    }

    public static String getStrParams(String str) throws YssException {
        return getStrParams(str, "<", ">");
    }

    //根据标识把字符中的字符擦去
    protected String wipeStr(String str) {
        int iBIndex = -1;
        int iEIndex = -1;
        int iLen = 0;
        boolean bFlag = false;
        char[] chrAry = str.toCharArray();
        for (int i = 0; i < chrAry.length; i++) {
            if (chrAry[i] == '[') {
                iBIndex = i;
                iLen++;
            }
            if (iBIndex > -1) {
                iLen++;
            }
            if (iLen > 0 && ( (chrAry[i] == '<' && chrAry[i + 2] == '>') || (chrAry[i] == '<' && chrAry[i + 3] == '>'))) {
                bFlag = true;
            }
            if (chrAry[i] == ']') {
                iLen++;
                if (bFlag) {
                    for (int j = iBIndex; j < iBIndex + iLen - 2; j++) {
                        chrAry[j] = ' ';
                    }
                } else {
                    chrAry[iBIndex] = ' ';
                    chrAry[i] = ' ';
                }
                bFlag = false;
                iBIndex = -1;
                iLen = 0;
            }
        }
        return String.valueOf(chrAry);
    }

    /**
     * 这个方法已经转到 com.yss.vsub中的 NumberToChinese中, by ly
     * 把数字转换成中文大写金额，分以下小数截位
     * @param dblMoney double：数字
     * @param blnFull boolean：
     *               =False,2001.30->贰仟零壹元叁角整
     *               =True,2001.30->贰仟零佰零拾壹元叁角零分
     * @param bZheng boolean：是否强制末尾加整字（如果不是到分，那么一定有整）
     * @return String
     */
    public static final String chineseAmount(double dblMoney, boolean blnFull, boolean bZheng) {

        String conChineseNum = "零壹贰叁肆伍陆柒捌玖";
        String conChineseMUnit = "分角元拾佰仟万拾佰仟亿";
        String strMoney = null;
        String T1 = "";
        char T2, t0 = ' ';
        int ii, jj, kk;
        boolean noZero = false;

        strMoney = new DecimalFormat("0.00").format(YssD.round(dblMoney, 2, true)); //去掉前导0

        kk = 0;
        if (blnFull) {
            for (ii = strMoney.length() - 1; ii >= 0; ii--) {
                T2 = strMoney.charAt(ii);
                if (T2 >= '0' && T2 <= '9') {
                    T1 = String.valueOf(conChineseNum.charAt(T2 - '0')) +
                        String.valueOf(conChineseMUnit.charAt(kk++)) + T1;
                    if (kk > 10) {
                        kk -= 8;
                    }
                }
            }
        } else {
            for (ii = strMoney.length() - 1; ii >= 0; ii--) {
                T2 = strMoney.charAt(ii);
                if (T2 == '0' && !noZero) {
                    if (kk == 2 || kk == 6) {
                        T1 = String.valueOf(conChineseMUnit.charAt(kk)) + T1;
                    } else if (kk == 10) {
                        T1 = String.valueOf(conChineseMUnit.charAt(kk)) +
                            (T1.charAt(0) == '万' ? T1.substring(1) : T1);
                    }

                    if (++kk > 10) {
                        kk -= 8;
                    }
                } else if (T2 == '.' && !noZero) {

                } else {
                    noZero = true;
                    if (T2 >= '0' && T2 <= '9') {
                        if (T2 == '0') {
                            if (T2 != t0) {
                                t0 = T2;
                                jj = T2 - '0';
                                if (kk == 2 || kk == 6 || kk == 10) {
                                    T1 = String.valueOf(conChineseMUnit.charAt(kk)) + T1;
                                } else {
                                    T1 = String.valueOf(conChineseNum.charAt(jj)) + T1;
                                }
                            } else {
                                if (kk == 2 || kk == 6) {
                                    T1 = String.valueOf(conChineseMUnit.charAt(kk)) + T1;
                                } else if (kk == 10) {
                                    T1 = String.valueOf(conChineseMUnit.charAt(kk)) +
                                        (T1.charAt(0) == '万' ? T1.substring(1) : T1);
                                }

                            }
                        } else {
                            t0 = T2;
                            jj = T2 - '0';
                            if (kk == 10 && T1.charAt(0) == '万') {
                                T1 = T1.substring(1);
                            }
                            T1 = String.valueOf(conChineseNum.charAt(jj)) +
                                String.valueOf(conChineseMUnit.charAt(kk)) + T1;
                        }
                        if (++kk > 10) {
                            kk -= 8;
                        }
                    }
                }
            }
            T2 = T1.charAt(0);
            while (conChineseMUnit.indexOf(T2) >= 0 || T2 == '零') {
                T1 = T1.substring(1);
                if (T1.length() > 1) {
                    T2 = T1.charAt(0);
                } else {
                    break;
                }
            }
            if (T1.length() == 0) {
                T1 = "零元";
            }
        }

        return T1.concat(bZheng || T1.charAt(T1.length() - 1) != '分' ? "整" : "");
    }
    //edited by zhouxiang 去除逗号 字符串的最后一位  2011.01.07
    public  static final String getSubString(String sSubString) {
		if (sSubString.trim().length() > 0) {
			sSubString = sSubString.substring(0, sSubString.length() - 1);
		}
		return sSubString;
	}
    /** 
     * 20100810 added by liubo.得到指定年份某个月的天数      
     * @param year int：指定的年份
     * @param month int：指定的月份
     * @return int：返回该月份的最后一天，实际也就是指定月份的天数
     * */  
    public static int getMonthLastDay(int year, int month) throws YssException
    {  
    	try
    	{
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天   
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天   
        int maxDate = a.get(Calendar.DATE); 
        return maxDate;   
    	}
    	catch(Exception e)
    	{
    		throw new YssException(e.getMessage());
    	}
    }  
    
    //add by fangjiang 2011.12.08 story 1890
    public static String getTime() throws YssException {
    	try {
    		Calendar c = Calendar.getInstance();  
    		String s = c.get(Calendar.HOUR_OF_DAY) + ":" 
    		           + c.get(Calendar.MINUTE) + ":"
    		           + c.get(Calendar.SECOND) + ":"
    		           + c.get(Calendar.MILLISECOND);
    		return s;
    	} catch(Exception e) {
    		throw new YssException(e.getMessage());
    	}
    }

    /**
     * add by jsc 20120529
     * 获取间隔时间
     * @return
     * @throws YssException
     */
    public static String timeDiff(Date beginDate)throws YssException{
    	
    	java.util.Date endDate = new Date();
    	StringBuffer Msg = new StringBuffer();
    	try{
    		   long between=(endDate.getTime()-beginDate.getTime())/1000;//除以1000是为了转换成秒


//    		   long day1=between/(24*3600);
//    		   long hour1=between/3600;
//    		   long minute1=between/60;
    		   long second1=between;
    		   Msg.append(second1).append(" 秒");
//    		   if(day1!=0){
//    			   Msg.append(day1).append("天");
//    		   }
//    		   if(hour1!=0){
//    			   Msg.append(hour1).append("小时");
//    		   }
//    		   if(minute1!=0){
//    			   Msg.append(minute1).append("分");
//    		   }
//    		   if(second1!=0){
//    			   Msg.append(second1).append("秒");
//    		   }
//    		   if(second1==0){
//    			   second1=(endDate.getTime()-beginDate.getTime())%60/60*1000;
//    			   Msg.append(second1).append("秒");
//    		   }
    		   return Msg.toString();

    	}catch(Exception e){
    		throw new YssException(e.getMessage());
    	}
    }

	/**shashijie 2013-4-27 STORY 3343 获取上个月的某一天,考虑跨年问题*/
	public static Date getLastMonthAndDay(Date pDate,String date) {
		Date dDate = null;
		try {
			//获取业务日期的年份
			int year = getYear(pDate);
			//获取业务日期对应的月份
	        int month = getMonth(pDate) - 1 ;
	        //考虑跨年
	        if (month==0) {
				month = 12;
				year = year - 1;
			}
	        //月实际天数
			int day = getMonthLastDay(year, month);//传入年和月
			//拼接日期
			String sDate = "";
	        //若不传值默认获取本月的最后一天
	        if (YssUtil.isNullOrEmpty(date)) {
				sDate = formatNumber(year,"0000") + "-" + formatNumber(month,"00") + "-" + formatNumber(day,"00");
			} else {
				//获取本月的某一天
				sDate = formatNumber(year,"0000") + "-" + formatNumber(month,"00") + "-" + date;
			}
			dDate = parseDate(sDate);
		} catch (Exception e) {

		} finally {

		}
		return dDate;
	}
    
    
    
}
