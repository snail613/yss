package com.yss.commeach;

import com.yss.util.*;
import java.util.*;
import java.sql.*;

/**
 * 新增　QDV4南方2009年1月20日01_A MS00210 by leeyu 20090227
 * <p>Title: 处理提示信息数据的加载处理</p>
 *
 * <p>Description: </p>
 *
 * <p>Company: ysstech</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EachExchangeHolidays
    extends BaseCommEach {
    public EachExchangeHolidays() {
    }

    private String chinaHolidayCode = "";
    java.util.Date natureDate; //自然日,还需通过中国节假日算出工作日
    private int offerDay = 0; //偏离日
    private java.util.Date workDate; //工作日 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090513
    /**
     * buildRowStr： 产生一行的数据
     *
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        return "";
    }

    /**
     * getOperValue : 获取对象中特定变量的值
     *
     * @return String
     * @param sType String
     * @throws YssException
     */
    public String getOperValue(String sType) throws YssException {
        String sResult = "";
        //QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090513
        if (sType != null && sType.equalsIgnoreCase("getWorkDate")) {
            //参数：节假日代码,工作日,偏离天数
            return YssFun.formatDate(getWorkDay(chinaHolidayCode, workDate, offerDay), "yyyy-MM-dd");
        } else {
            sResult = getExchangeMes();
        }
        return sResult;
    }

    /**
     * parseRowStr： 解析一行数据，放入类的对应属性中
     *
     * @param sRowStr String：发送过来的一行请求
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] arrReq = sRowStr.split("\t");
        chinaHolidayCode = arrReq[0];
        if (YssFun.isNumeric(arrReq[1])) {
            offerDay = YssFun.toInt(arrReq[1]);
        } else {
            offerDay = 0;
        }
        natureDate = new java.util.Date(); //取当前服务日期
        //添加工作日的情况 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090513
        if (arrReq.length > 2) {
            if (YssFun.isDate(arrReq[2])) {
                workDate = YssFun.toDate(arrReq[2]);
            } else {
                workDate = new java.util.Date(); //取系统日期
            }
        }
    }

    /**
     * 获取所有市场的节假日提示信息
     * @return String
     * @throws YssException
     */
    private String getExchangeMes() throws YssException {
        ResultSet rs = null;
        java.util.Date dChinaDate = null; //中国节假日群取的工作日
        java.util.Date dWorkDate = null; //工作日
        String sqlStr = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        Hashtable htHolday = null;
        try {
            htHolday = new Hashtable(); //
            if (chinaHolidayCode.trim().length() == 0) {
                return "中国节假日没有指定，请双击信息设置...";
            }
            dChinaDate = getWorkDay(chinaHolidayCode, natureDate, 0);
            if (YssFun.dateDiff(dChinaDate, YssFun.toDate("1900-01-01")) == 0) {
                return "中国节假日代码" + chinaHolidayCode + "获取日期有误，请检查此节假日代码的设置";
            }
            if (natureDate.compareTo(dChinaDate) != 0) { //如果查出的工作日与当前给的日期不同的话，说明当前日期为节假日
                dChinaDate = getWorkDay(chinaHolidayCode, natureDate, -1); //这里需取前一天的工作日出来
            }
            dChinaDate = getWorkDay(chinaHolidayCode, dChinaDate, offerDay); //这里需取T-N天的工作日
            if (YssFun.dateDiff(dChinaDate, YssFun.toDate("1900-01-01")) == 0) {
                return "中国节假日代码" + chinaHolidayCode + "获取日期有误，请检查此节假日代码的设置";
            }
            sqlStr = "select FExchangeCode,FExchangeName,FHolidaysCode from " +
                pub.yssGetTableName("Tb_Base_Exchange") +
                " where FCheckState=1 and " + dbl.sqlLen("FHolidaysCode") +
                "<>0 order by FHolidaysCode ";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                if (htHolday.get(rs.getString("FHolidaysCode")) != null) {
                    if (YssFun.dateDiff(dWorkDate, YssFun.toDate("1900-01-01")) == 0) {
                        buf.append(rs.getString("FExchangeCode")).append("-").append(rs.getString("FExchangeName"))
                            .append(":获取日期有误，请检查此节假日代码的设置")
                            .append(YssCons.YSS_LINESPLITMARK);
                        continue;
                    }
                    buf.append(rs.getString("FExchangeCode") + "-" + rs.getString("FExchangeName") + ":" + YssFun.formatDate(YssFun.toDate( (String) htHolday.get(rs.getString("FHolidaysCode"))), "yyyy-MM-dd") + "为" +
                               (YssFun.dateDiff(dWorkDate, YssFun.toDate( (String) htHolday.get(rs.getString("FHolidaysCode")))) == 0 ? "工作日" : "节假日"))
                        .append(YssCons.YSS_LINESPLITMARK);
                } else {
                    dWorkDate = getWorkDay(rs.getString("FHolidaysCode"), dChinaDate, 0); //这里取出T-N日某节假日的工作日，便于下面判断
                    if (YssFun.dateDiff(dWorkDate, YssFun.toDate("1900-01-01")) == 0) {
                        buf.append(rs.getString("FExchangeCode")).append("-").append(rs.getString("FExchangeName"))
                            .append(":获取日期有误，请检查此节假日代码的设置")
                            .append(YssCons.YSS_LINESPLITMARK);
                        htHolday.put(rs.getString("FHolidaysCode"), YssFun.toDate("1900-01-01"));
                        continue;
                    }
                    if (dWorkDate.compareTo(dChinaDate) == 0) { //如果T-N日为工作日的话
                        htHolday.put(rs.getString("FHolidaysCode"), YssFun.formatDate(dWorkDate));
                    } else {
                        htHolday.put(rs.getString("FHolidaysCode"), YssFun.formatDate(dChinaDate));
                    }
                    buf.append(rs.getString("FExchangeCode") + "-" + rs.getString("FExchangeName") + ":" + YssFun.formatDate(YssFun.toDate( (String) htHolday.get(rs.getString("FHolidaysCode"))), "yyyy-MM-dd") + "为" +
                               (YssFun.dateDiff(dWorkDate, YssFun.toDate( (String) htHolday.get(rs.getString("FHolidaysCode")))) == 0 ? "工作日" : "节假日"))
                        .append(YssCons.YSS_LINESPLITMARK);
                }
            }
            sResult = buf.toString();
            if (sResult.endsWith(YssCons.YSS_LINESPLITMARK)) {
                sResult = sResult.substring(0, sResult.length() - YssCons.YSS_LINESPLITMARK.length());
            }
        } catch (Exception ex) {
            //throw new YssException("获取市场T-"+offerDay+"日交易信息出错",ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * 返回工作日，但不返回异常信息
     * 此方法为本方法专用，其他对象引用请不要引用此方法
     *
     * @param getHolidaysCode String   //节假日群代码
     * @dDate Date                     //传入日期
     * @lOffset int                    //传入延迟天数
     * @return Date
     */
    private java.util.Date getWorkDay(String getHolidaysCode,
                                      java.util.Date dDate, int lOffset) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        int lTmp = 0, lStep;
        try {
            lStep = (lOffset < 0) ? -1 : 1;
            strSql =
                "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = " +
                dbl.sqlString(getHolidaysCode) + " and FCheckState=1" + " and FDate " +
                ( (lOffset < 0) ? "<=" : ">=")
                + dbl.sqlDate(dDate, false) + " order by FDate " +
                ( (lOffset < 0) ? " desc" : "");
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (rs.next()) {
                do {
                    if (YssFun.dateDiff(rs.getDate("FDate"), dDate) == 0) {
                        rs.next();
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
                } while (!rs.isAfterLast());

                if (rs.isAfterLast()) {
                    dDate = YssFun.toDate("1900-01-01"); //如果超过范围，也返回最小日期
                }
            } else {
                dDate = YssFun.toDate("1900-01-01"); //如果没有，就返回最小日期
            }
            return dDate;
        } catch (Exception e) {
            throw new YssException(e.getMessage() /*"访问节假日表出错！"*/);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
