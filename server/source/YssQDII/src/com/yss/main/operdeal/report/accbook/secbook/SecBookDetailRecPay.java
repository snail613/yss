package com.yss.main.operdeal.report.accbook.secbook;

import java.sql.*;
import java.util.*;

import com.yss.util.*;
import com.yss.main.operdeal.report.accbook.pojo.*;
import com.yss.main.cusreport.*;

import com.yss.main.operdeal.report.accbook.BaseAccBook;

public class SecBookDetailRecPay
    extends BaseAccBook {
    public SecBookDetailRecPay() {
    }

    /**
     * 获取报表数据入口 （基类方法的实现）
     * @param sType String
     * @throws YssException
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        HashMap hmResult = null;
        ArrayList arrResult = null;
        try {
            arrResult = getRecPayResultHashTable(); // 得到数据
            sResult = buildRowCompResult(arrResult); //得到含格式的数据
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取证券应收应付明细台帐出错： \n" + e.getMessage());
        }
    }

    /**
     * 将数据与报表数据源传入得到相应格式数据
     * @throws YssException
     */
    protected String buildRowCompResult(ArrayList arrResult) throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";
        RepTabCellBean rtc = null;
        SecBookDetailRecPayBean secAccBook;
        try {
            for (int i = 0; i < arrResult.size(); i++) {
                secAccBook = (SecBookDetailRecPayBean) arrResult.get(i);
                hmCellStyle = getCellStyles("DsSecAccRecPay002");
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                    " where FRepDsCode = " + dbl.sqlString("DsSecAccRecPay002") +
                    " and FCheckState = 1 order by FOrderIndex";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    //获得样式
                    sKey = "DsSecAccRecPay002" + "\tDSF\t-1\t" +
                        rs.getString("FOrderIndex");

                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");

                    buf.append(YssReflection.getPropertyValue(secAccBook,
                        rs.getString("FDsField")) +
                               "\t");
                }
                dbl.closeResultSetFinal(rs);
                if (buf.toString().trim().length() > 1) {
                    strReturn = strReturn + buf.toString().substring(0,
                        buf.toString().length() - 1);
                    buf.delete(0, buf.toString().length()); //将BUF 清空
                    strReturn = strReturn + "\r\n"; //每一个单元格用\r\n隔开
                }
            }
            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取格式出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 将应收应付明细台帐结果装入哈希表
     * @throws YssException
     * @return HashMap
     */
    public ArrayList getRecPayResultHashTable() throws YssException {
        HashMap hmBeginResult = new HashMap();
        ArrayList arrRecResult = new ArrayList();
        ArrayList arrPayResult = new ArrayList();
        ArrayList arrEndResult = new ArrayList();
        ResultSet rs = null;
        SecBookDetailRecPayBean secAccBook = null;
        String sHmRecPayKey = "";
        try {
            //取应收应付期初数
            rs = dbl.openResultSet(this.getStartRecPayStorageSql().toString());
            while (rs.next()) {
                secAccBook = new SecBookDetailRecPayBean();
                secAccBook.setSecurityCode(rs.getString("FSecurityCode"));
                secAccBook.setSecurityName(rs.getString("FSecurityName"));
                secAccBook.setCuryCode(rs.getString("FCuryCode"));
                secAccBook.setEndMoney(rs.getDouble("FBal"));
                secAccBook.setTsfTypeCode("期初数");
                if (this.bIsPort) {
                    secAccBook.setPortEndMoney(rs.getDouble("FPortCuryBal"));
                }
                sHmRecPayKey = secAccBook.getSecurityCode();
                hmBeginResult.put(sHmRecPayKey, secAccBook);
            }
            dbl.closeResultSetFinal(rs);

            //取流入的发生额
            rs = dbl.openResultSet(this.getTradeRecSql().toString());
            while (rs.next()) {
                sHmRecPayKey = "";
                secAccBook = new SecBookDetailRecPayBean();
                secAccBook.setSecurityCode(rs.getString("FSecurityCode"));
                secAccBook.setSecurityName(rs.getString("FSecurityName"));
                secAccBook.setTransDate(rs.getDate("FTransDate"));
                secAccBook.setCuryCode(rs.getString("FCuryCode"));
                secAccBook.setTsfTypeCode(rs.getString("FTsfTypeName"));
                secAccBook.setSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
                secAccBook.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                secAccBook.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                secAccBook.setInMoney(rs.getDouble("FMoney"));
                secAccBook.setPortInMoney(rs.getDouble("FPortCuryMoney"));
                secAccBook.setNum(rs.getString("FNum"));

                //--------判断此只交易证券是否有期初数--------//
                SecBookDetailRecPayBean tmpBook = null;
                tmpBook = (SecBookDetailRecPayBean) hmBeginResult.get(secAccBook.
                    getSecurityCode());
                //如果 tmpBook 为空证明此只交易证券在期初数的哈希表中没有数据
                if (tmpBook == null) {
                    tmpBook = new SecBookDetailRecPayBean();
                    tmpBook.setSecurityCode(secAccBook.getSecurityCode());
                    tmpBook.setSecurityName(secAccBook.getSecurityName());
                    tmpBook.setCuryCode(secAccBook.getCuryCode());
                    tmpBook.setTsfTypeCode("期初数");
                    hmBeginResult.put(secAccBook.getSecurityCode(), tmpBook);
                }
                //----------------------------------------//
                arrRecResult.add(secAccBook);
            }
            dbl.closeResultSetFinal(rs);

            //取流出的发生额
            StringBuffer bufPaySql = this.getTradePaySql();
            if (bufPaySql.length() != 0) {
                rs = dbl.openResultSet(bufPaySql.toString());
                while (rs.next()) {
                    sHmRecPayKey = "";
                    secAccBook = new SecBookDetailRecPayBean();
                    secAccBook.setSecurityCode(rs.getString("FSecurityCode"));
                    secAccBook.setSecurityName(rs.getString("FSecurityName"));
                    secAccBook.setTransDate(rs.getDate("FTransDate"));
                    secAccBook.setCuryCode(rs.getString("FCuryCode"));
                    secAccBook.setTsfTypeCode(rs.getString("FTsfTypeName"));
                    secAccBook.setSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
                    secAccBook.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                    secAccBook.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                    secAccBook.setOutMoney(rs.getDouble("FMoney"));
                    secAccBook.setPortOutMoney(rs.getDouble("FPortCuryMoney"));
                    secAccBook.setNum(rs.getString("FNum"));

                    arrPayResult.add(secAccBook);
                }
            }
            arrEndResult = this.getRecPayOrderList(hmBeginResult, arrRecResult,
                arrPayResult);
        } catch (Exception e) {
            throw new YssException("将证券应收应付明细台帐结果装入哈希表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return arrEndResult;
    }

    /**
     * 获取证券应收应付明细台账期初的 SQL 语句
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getStartRecPayStorageSql() throws YssException {
        StringBuffer bufSql = new StringBuffer(1000);
        String sWhereFiled = ""; //Where 条件中的字段
        String sWhereTsfType = ""; //带调拨类型的 Where 条件
        String sWhereSubTsfType = ""; //带调拨子类型的 Where 条件
        boolean bHaveMV = false; //判断调拨类型是否含有估值增值
        String invmgrSecField = "";
        String brokerSecField = "";
        try {
            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Broker")) { //如果链接中有券商
                    brokerSecField = this.getSettingOper().getStorageAnalysisField( //取券商所在的分析代码
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                    sWhereFiled = sWhereFiled + " AND " + brokerSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                if (this.aryAccBookDefine[i].equalsIgnoreCase("InvMgr")) { //如果链接中有投资经理
                    invmgrSecField = this.getSettingOper().getStorageAnalysisField( //取投资经理所在的分析代码
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                    sWhereFiled = sWhereFiled + " AND " + invmgrSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                if (this.aryAccBookDefine[i].equalsIgnoreCase("TsfType")) { //如果链接中有调拨类型
                    if (this.aryAccBookLink[i + 1].equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_MV)) {
                        bHaveMV = true;
                    }
                    sWhereTsfType = " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) +
                        ")";
                    continue;
                }
                if (this.aryAccBookDefine[i].equalsIgnoreCase("SubTsfType")) { //如果链接中有调拨子类型
                    sWhereSubTsfType = " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]) +
                        " OR FSubTsfTypeCode LIKE " +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX + this.aryAccBookLink[i + 1]) +
                        ")";
                    continue;
                }

                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }
            //----------在 WHERE 条件中添加调拨类型和调拨子类型的条件语句---------//
            if (sWhereTsfType.length() != 0) {
                sWhereFiled += sWhereTsfType;
            }
            if (sWhereSubTsfType.length() != 0) {
                if (bHaveMV) {
                    sWhereSubTsfType = " AND (" +
                        sWhereSubTsfType.substring(5) +
                        " OR FSubTsfTypeCode LIKE " +
                        dbl.sqlString("9909%") +
                        " OR FSubTsfTypeCode LIKE " +
                        dbl.sqlString("9905%") + ")";
                }
                sWhereFiled += sWhereSubTsfType;
            }
            //---------------------------------------------------------------//

            bufSql.append("SELECT SUM(FBal) AS FBal,");
            bufSql.append(" SUM(FPortCuryBal) AS FPortCuryBal,");
            bufSql.append(" FSecurityCode,");
            bufSql.append(" FSecurityName,");
            bufSql.append(" FCuryCode");
            bufSql.append(" FROM (SELECT FYearMonth,");
            bufSql.append(" FStorageDate,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FSecurityCode AS FSecCode,");
            bufSql.append(" FTsfTypeCode,");
            bufSql.append(" FSubTsfTypeCode,");
            bufSql.append(" FCuryCode,");
            bufSql.append(" FBal,");
            bufSql.append(" FPortCuryBal,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_SecRecPay"));
            bufSql.append(" WHERE ");
            bufSql.append(" FTsfTypeCode IN ('07', '06', '09', '99')) a");
            bufSql.append(" JOIN (SELECT * FROM " + pub.yssGetTableName("TB_Para_Security") + ") b ON a.FSecCode = b.FSecurityCode");
            bufSql.append(" WHERE a.FCheckState = 1");
            bufSql.append(" AND " + this.operSql.sqlStoragEve(dBeginDate));
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY FSecurityCode, FSecurityName, FCuryCode");
        } catch (Exception e) {
            throw new YssException("获取证券应收应付明细台账期初的 SQL 语句出错！", e);
        }
        return bufSql;
    }

    /**
     * 获取证券台帐应收发生额明细数据 Sql
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getTradeRecSql() throws YssException {
        StringBuffer bufSql = new StringBuffer(1150);
        String sWhereFiled = ""; //Where 条件中的字段
        String sWhereTsfType = ""; //带调拨类型的 Where 条件
        String sWhereSubTsfType = ""; //带调拨子类型的 Where 条件
        boolean bHaveMV = false; //判断调拨类型是否含有估值增值
        String invmgrSecField = "";
        String brokerSecField = "";
        try {
            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //如果链接中有券商
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Broker")) {
                    //取券商所在的分析代码
                    brokerSecField = this.getSettingOper().getStorageAnalysisField(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                    sWhereFiled = sWhereFiled + " AND " + brokerSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                //如果链接中有投资经理
                if (this.aryAccBookDefine[i].equalsIgnoreCase("InvMgr")) {
                    //取投资经理所在的分析代码
                    invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                    sWhereFiled = sWhereFiled + " AND " + invmgrSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                //如果链接中有调拨类型
                if (this.aryAccBookDefine[i].equalsIgnoreCase("TsfType")) {
                    if (this.aryAccBookLink[i +
                        1].equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_MV)) {
                        bHaveMV = true;
                    }
                    sWhereTsfType = " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) +
                        ")";
                    continue;
                }
                //如果链接中有调拨子类型
                if (this.aryAccBookDefine[i].equalsIgnoreCase("SubTsfType")) {
                    sWhereSubTsfType = " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]) +
                        " OR FSubTsfTypeCode LIKE " +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX + this.aryAccBookLink[i + 1]) +
                        ")";
                    continue;
                }

                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }

            //----------在 WHERE 条件中添加调拨类型和调拨子类型的条件语句---------//
            if (sWhereTsfType.length() != 0) {
                sWhereFiled += sWhereTsfType;
            }
            if (sWhereSubTsfType.length() != 0) {
                if (bHaveMV) {
                    sWhereSubTsfType = " AND (" +
                        sWhereSubTsfType.substring(5) +
                        " OR FSubTsfTypeCode LIKE " +
                        dbl.sqlString("9909%") +
                        " OR FSubTsfTypeCode LIKE " +
                        dbl.sqlString("9905%") + ")";
                }
                sWhereFiled += sWhereSubTsfType;
            }
            //---------------------------------------------------------------//
            bufSql.append("SELECT a1.*, z.FTsfTypeName");
            bufSql.append(" FROM (SELECT a.FNum,");
            bufSql.append(" a.FTransDate,");
            bufSql.append(" a.FBaseCuryRate,");
            bufSql.append(" a.FPortCuryRate,");
            bufSql.append(" a.FSecurityCode,");
            bufSql.append(" b.FSecurityName,");
            bufSql.append(" a.FMoney,");
            bufSql.append(" a.FPortCuryMoney,");
            bufSql.append(" a.FCuryCode,");
            bufSql.append(" a.FSubTsfTypeCode,");
            bufSql.append(" a.FTsfTypeCode");
            bufSql.append(" FROM (SELECT FNum,");
            bufSql.append(" FTransDate,");
            bufSql.append(" FBaseCuryRate,");
            bufSql.append(" FPortCuryRate,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FSecurityCode,");
            bufSql.append(" FTsfTypeCode,");
            bufSql.append(" FSubTsfTypeCode,");
            bufSql.append(" FCuryCode,");
            bufSql.append(" FMoney,");
            bufSql.append(" FPortCuryMoney,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_SecRecPay"));
            bufSql.append(" WHERE FTsfTypeCode IN ('07', '06', '09', '99')) a");
            bufSql.append(" JOIN (SELECT FSecurityCode AS FSecCode, FSecurityName, FCatCode, FSubCatCode");
            bufSql.append(" FROM " + pub.yssGetTableName("TB_Para_Security"));
            bufSql.append(" ) b ON a.FSecurityCode = b.FSecCode");
            bufSql.append(" WHERE a.FCheckState = 1");
            bufSql.append(" AND FTransDate BETWEEN " + dbl.sqlDate(this.dBeginDate) + " AND ");
            bufSql.append(dbl.sqlDate(this.dEndDate));
            bufSql.append(sWhereFiled);
            bufSql.append(" ) a1");
            bufSql.append(" LEFT JOIN (SELECT FTsfTypeCode, FTsfTypeName");
            bufSql.append(" FROM Tb_Base_TransferType");
            bufSql.append(" WHERE FCheckState = 1) z ON a1.FTsfTypeCode = z.FTsfTypeCode");
        } catch (Exception e) {
            throw new YssException("获取证券台帐应收应付发生额明细数据 Sql 语句出错！", e);
        }
        return bufSql;
    }

    /**
     * 获取证券台帐应付发生额明细数据 Sql
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getTradePaySql() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        String sWhereFiled = ""; //Where 条件中的字段
        String invmgrSecField = "";
        String brokerSecField = "";
        boolean bIsPay = false; //判断是否有应付
        try {
            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //如果链接中有券商
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Broker")) {
                    //取券商所在的分析代码
                    brokerSecField = this.getSettingOper().getStorageAnalysisField(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                    sWhereFiled = sWhereFiled + " AND " + brokerSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                //如果链接中有投资经理
                if (this.aryAccBookDefine[i].equalsIgnoreCase("InvMgr")) {
                    //取投资经理所在的分析代码
                    invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                    sWhereFiled = sWhereFiled + " AND " + invmgrSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                //获取调拨类型
                if (this.aryAccBookDefine[i].equalsIgnoreCase("TsfType") &&
                    this.aryAccBookLink[i + 1].equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Rec)) {
                    sWhereFiled = sWhereFiled + " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString( (this.aryAccBookLink[i +
                                        1]).replaceAll(YssOperCons.YSS_ZJDBLX_Rec,
                        YssOperCons.YSS_ZJDBLX_Pay)) +
                        " OR " + (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " + dbl.sqlString( (this.aryAccBookLink[i +
                                                1]).replaceAll(YssOperCons.YSS_ZJDBLX_Rec,
                        YssOperCons.YSS_ZJDBLX_Income)) + ")";
                    bIsPay = true;
                    continue;
                }
                //获取调拨子类型
                if (this.aryAccBookDefine[i].equalsIgnoreCase("SubTsfType") &&
                    this.aryAccBookLink[i + 1].substring(0, 2).equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Rec)) {
                    sWhereFiled = sWhereFiled + " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString( (this.aryAccBookLink[i +
                                        1]).replaceAll(YssOperCons.YSS_ZJDBLX_Rec,
                        YssOperCons.YSS_ZJDBLX_Pay)) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " + dbl.sqlString( (this.aryAccBookLink[i +
                                                1]).replaceAll(YssOperCons.
                        YSS_ZJDBLX_Rec,
                        YssOperCons.YSS_ZJDBLX_Income)) + ")";
                    bIsPay = true;
                    continue;
                }
                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }
            if (bIsPay) {
                bufSql.append("SELECT a1.*, z.FTsfTypeName");
                bufSql.append(" FROM (SELECT a.FNum,");
                bufSql.append(" a.FTransDate,");
                bufSql.append(" a.FBaseCuryRate,");
                bufSql.append(" a.FPortCuryRate,");
                bufSql.append(" a.FSecurityCode,");
                bufSql.append(" b.FSecurityName,");
                bufSql.append(" a.FMoney,");
                bufSql.append(" a.FPortCuryMoney,");
                bufSql.append(" a.FCuryCode,");
                bufSql.append(" a.FSubTsfTypeCode,");
                bufSql.append(" a.FTsfTypeCode");
                bufSql.append(" FROM (SELECT FNum,");
                bufSql.append(" FTransDate,");
                bufSql.append(" FBaseCuryRate,");
                bufSql.append(" FPortCuryRate,");
                bufSql.append(" FPortCode,");
                bufSql.append(" FAnalysisCode1,");
                bufSql.append(" FAnalysisCode2,");
                bufSql.append(" FAnalysisCode3,");
                bufSql.append(" FSecurityCode,");
                bufSql.append(" FTsfTypeCode,");
                bufSql.append(" FSubTsfTypeCode,");
                bufSql.append(" FCuryCode,");
                bufSql.append(" FMoney,");
                bufSql.append(" FPortCuryMoney,");
                bufSql.append(" FCheckState");
                bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_SecRecPay"));
                bufSql.append(" WHERE FTsfTypeCode IN (");
                bufSql.append(dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay));
                bufSql.append(", " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + ")) a");
                bufSql.append(
                    " JOIN (SELECT FSecurityCode AS FSecCode, FSecurityName, FCatCode, FSubCatCode");
                bufSql.append(
                    " FROM " + pub.yssGetTableName("TB_Para_Security") + ") b ON a.FSecurityCode = b.FSecCode");
                bufSql.append(" WHERE a.FCheckState = 1");
                bufSql.append(
                    " AND FTransDate BETWEEN to_date('2007-12-10', 'yyyy-MM-dd') AND ");
                bufSql.append(" to_date('2007-12-10', 'yyyy-MM-dd')");
                bufSql.append(sWhereFiled);
                bufSql.append(" ORDER BY FSecurityCode, FTransDate ASC");
                bufSql.append(" ) a1");
                bufSql.append(" LEFT JOIN (SELECT FTsfTypeCode, FTsfTypeName");
                bufSql.append(" FROM Tb_Base_TransferType");
                bufSql.append(
                    " WHERE FCheckState = 1) z ON a1.FTsfTypeCode = z.FTsfTypeCode");
            }
        } catch (Exception e) {
            throw new YssException("", e);
        }
        return bufSql;
    }

    /**
     * 将证券应收应付明细台帐结果排序和计算
     * @param hmBeginResult HashMap: 装载期初数据的哈希表
     * @param arrRecResult ArrayList: 装载流入数据的链表
     * @param arrPayResult ArrayList: 装载流出数据的链表
     * @throws YssException
     * @return ArrayList: 最终结果
     */
    public ArrayList getRecPayOrderList(HashMap hmBeginResult,
                                        ArrayList arrRecResult,
                                        ArrayList arrPayResult) throws
        YssException {
        StringBuffer bufSql = new StringBuffer(500);
        ArrayList arrEndResult = new ArrayList();
        Connection conn = dbl.loadConnection();
        PreparedStatement pstmt = null;
        boolean bTrans = false;
        ResultSet rs = null;
        try {
            //如果表已存在则删除
            if (dbl.yssTableExist(pub.yssGetTableName("TB_Temp_DetailBook_" +
                pub.getUserCode()))) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                               pub.yssGetTableName("TB_Temp_DetailBook_" +
                    pub.getUserCode())));
                /**end*/
            }
            //------------------创建储存明晰台帐数据的临时表--------------------//
            bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_Temp_DetailBook_" +
                                              pub.getUserCode()));
            bufSql.append(" (");
            bufSql.append(" FTransDate DATE,");
            bufSql.append(" FSecurityCode VARCHAR(20),");
            bufSql.append(" FSecurityName VARCHAR(50),");
            bufSql.append(" FTsfTypeCode VARCHAR(20),");
            bufSql.append(" FSubTsfTypeCode VARCHAR(20),");
            bufSql.append(" FCuryCode VARCHAR(20),");
            bufSql.append(" FInMoney DECIMAL(18, 4),");
            bufSql.append(" FOutMoney DECIMAL(18, 4),");
            bufSql.append(" FEndMoney DECIMAL(18, 4),");
            bufSql.append(" FBaseCuryRate DECIMAL(20, 15),");
            bufSql.append(" FPortCuryRate DECIMAL(20, 15),");
            bufSql.append(" FPortInMoney DECIMAL(18, 4),");
            bufSql.append(" FPortOutMoney DECIMAL(18, 4),");
            bufSql.append(" FPortEndMoney DECIMAL(18, 4),");
            bufSql.append(" FNum VARCHAR(20),");
            bufSql.append(" FOrder VARCHAR(30)"); //用来排序
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            //-------------------------------------------------------------------//
            bufSql.append("INSERT INTO ");
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" +
                                              pub.getUserCode()));
            bufSql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmt = conn.prepareStatement(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            //----------------遍历应收的 ArrayList 将数据插入临时表----------------//
            conn.setAutoCommit(false);
            bTrans = true;
            int arrLen = arrRecResult.size();
            for (int i = 0; i < arrLen; i++) {
                SecBookDetailRecPayBean tmpTrdBook = null;
                tmpTrdBook = (SecBookDetailRecPayBean) arrRecResult.get(i);
                if (tmpTrdBook == null) {
                    continue;
                }
                pstmt.setDate(1,
                              new java.sql.Date(tmpTrdBook.getTransDate().getTime()));
                pstmt.setString(2, tmpTrdBook.getSecurityCode());
                pstmt.setString(3, tmpTrdBook.getSecurityName());
                pstmt.setString(4, tmpTrdBook.getTsfTypeCode());
                pstmt.setString(5, tmpTrdBook.getSubTsfTypeCode());
                pstmt.setString(6, tmpTrdBook.getCuryCode());
                pstmt.setDouble(7, tmpTrdBook.getInMoney());
                pstmt.setDouble(8, tmpTrdBook.getOutMoney());
                pstmt.setDouble(9, tmpTrdBook.getEndMoney());
                pstmt.setDouble(10, tmpTrdBook.getBaseCuryRate());
                pstmt.setDouble(11, tmpTrdBook.getPortCuryRate());
                pstmt.setDouble(12, tmpTrdBook.getPortInMoney());
                pstmt.setDouble(13, tmpTrdBook.getPortOutMoney());
                pstmt.setDouble(14, tmpTrdBook.getPortEndMoney());
                pstmt.setString(15, tmpTrdBook.getNum());
                pstmt.setString(16, tmpTrdBook.getSecurityCode() + "InOut");

                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //-----------------------------------------------------------------//
            //-----------------遍历应付的 ArrayList 将数据插入临时表---------------//
            conn.setAutoCommit(false);
            bTrans = true;
            arrLen = arrPayResult.size();
            for (int i = 0; i < arrLen; i++) {
                SecBookDetailRecPayBean tmpTrdBook = null;
                tmpTrdBook = (SecBookDetailRecPayBean) arrPayResult.get(i);
                if (tmpTrdBook == null) {
                    continue;
                }
                pstmt.setDate(1,
                              new java.sql.Date(tmpTrdBook.getTransDate().getTime()));
                pstmt.setString(2, tmpTrdBook.getSecurityCode());
                pstmt.setString(3, tmpTrdBook.getSecurityName());
                pstmt.setString(4, tmpTrdBook.getTsfTypeCode());
                pstmt.setString(5, tmpTrdBook.getSubTsfTypeCode());
                pstmt.setString(6, tmpTrdBook.getCuryCode());
                pstmt.setDouble(7, tmpTrdBook.getInMoney());
                pstmt.setDouble(8, tmpTrdBook.getOutMoney());
                pstmt.setDouble(9, tmpTrdBook.getEndMoney());
                pstmt.setDouble(10, tmpTrdBook.getBaseCuryRate());
                pstmt.setDouble(11, tmpTrdBook.getPortCuryRate());
                pstmt.setDouble(12, tmpTrdBook.getPortInMoney());
                pstmt.setDouble(13, tmpTrdBook.getPortOutMoney());
                pstmt.setDouble(14, tmpTrdBook.getPortEndMoney());
                pstmt.setString(15, tmpTrdBook.getNum());
                pstmt.setString(16, tmpTrdBook.getSecurityCode() + "InOut");

                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //-----------------------------------------------------------------//
            dbl.closeStatementFinal(pstmt);
            bufSql.append("INSERT INTO ");
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" +
                                              pub.getUserCode()));
            bufSql.append("(FSecurityCode, FSecurityName, FCuryCode, FEndMoney, FPortEndMoney, FOrder)");
            bufSql.append(" VALUES(?,?,?,?,?,?)");
            pstmt = conn.prepareStatement(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            //--------------------枚举哈希表将期初数插入临时表---------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            java.util.Iterator it = hmBeginResult.values().iterator();
            while (it.hasNext()) {
                SecBookDetailRecPayBean tmpTrdBook = null;
                tmpTrdBook = (SecBookDetailRecPayBean) it.next();
                if (tmpTrdBook == null) {
                    continue;
                }

                pstmt.setString(1, tmpTrdBook.getSecurityCode());
                pstmt.setString(2, tmpTrdBook.getSecurityName());
                pstmt.setString(3, tmpTrdBook.getCuryCode());
                pstmt.setDouble(4, tmpTrdBook.getEndMoney());
                pstmt.setDouble(5, tmpTrdBook.getPortEndMoney());
                pstmt.setString(6, tmpTrdBook.getSecurityCode());

                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------------------------------------------------------------//
            //------------将临时表中所有数据查询出来装入 ArrayList------------------//
            bufSql.append("SELECT * FROM　");
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" +
                                              pub.getUserCode()));
            bufSql.append(" ORDER BY FOrder,FNum ASC");

            rs = dbl.openResultSet(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //-----------定义同一帐户的期末数量、期末金额、本位币期末金额 便于累加

            double dbEndMoney = 0.0;
            double dbPoryEndMoney = 0.0;

            //-----------

            while (rs.next()) {
                SecBookDetailRecPayBean detailBook = new SecBookDetailRecPayBean();

                if (rs.getString("FOrder") == null ||
                    rs.getString("FOrder").equals("")) {
                    continue;
                }

                //期末成本
                if (!rs.getString("FOrder").equals(rs.getString("FSecurityCode"))) {
                    dbEndMoney = dbEndMoney + rs.getDouble("FInMoney") -
                        rs.getDouble("FOutMoney");
                } else {
                    dbEndMoney = rs.getDouble("FEndMoney");
                }
                //组合货币期末成本
                if (!rs.getString("FOrder").equals(rs.getString("FSecurityCode"))) {
                    dbPoryEndMoney = dbPoryEndMoney + rs.getDouble("FPortInMoney") -
                        rs.getDouble("FPortOutMoney");
                } else {
                    dbPoryEndMoney = rs.getDouble("FPortEndMoney");
                }

                detailBook.setTransDate(rs.getDate("FTransDate"));
                detailBook.setSecurityCode(rs.getString("FSecurityCode"));
                //detailBook.setSecurityName(rs.getString("FSecurityName"));
                detailBook.setCuryCode(rs.getString("FCuryCode"));
                detailBook.setTsfTypeCode(rs.getString("FTsfTypeCode"));
                detailBook.setSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
                detailBook.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                detailBook.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                detailBook.setInMoney(rs.getDouble("FInMoney"));
                detailBook.setOutMoney(rs.getDouble("FOutMoney"));
                detailBook.setEndMoney(dbEndMoney);
                detailBook.setPortInMoney(rs.getDouble("FPortInMoney"));
                detailBook.setPortOutMoney(rs.getDouble("FPortOutMoney"));
                detailBook.setPortEndMoney(dbPoryEndMoney);
                detailBook.setNum(rs.getString("FNum"));
                if (rs.getString("FOrder").indexOf("InOut") > 0) {
                    detailBook.setSecurityName(". " + rs.getString("FSecurityName"));
                    detailBook.setOrder(0);
                    detailBook.setIndex("");
                } else {
                    detailBook.setSecurityName(rs.getString("FSecurityName"));
                    detailBook.setOrder(1);
                    detailBook.setIndex("-");
                }

                arrEndResult.add(detailBook);
            }
            //-----------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("证券明细台帐，序列化应收应付数据出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(pstmt);
            dbl.closeResultSetFinal(rs);
        }
        return arrEndResult;
    }

    /**
     * 完成哈希表的初始化工作
     * @throws YssException
     */
    public void initHashTable() throws YssException {
        try {

            this.hmSelectField = new HashMap();
            this.hmSelectField.put("InvMgr", "FInvMgrCode as FStkCode");
            this.hmSelectField.put("CatType", "FCatCode as FStkCode");
            this.hmSelectField.put("Port", "FPortCode as FStkCode");
            this.hmSelectField.put("SubCatType", "FSubCatCode as FStkCode");
            this.hmSelectField.put("Broker", "FBrokerCode as FStkCode");
            this.hmSelectField.put("Cury", "FCuryCode as FStkCode");
            this.hmSelectField.put("SecCode", "FSecurityCode as FStkCode");
            this.hmSelectField.put("SubTsfType", "FSubTsfTypeCode as FStkCode");
            this.hmSelectField.put("TsfType", "FTsfTypeCode as FStkCode");

            this.hmFieldRela = new HashMap();
            this.hmFieldRela.put("SecCode", "FSecurityCode");
            this.hmFieldRela.put("Broker", "FBrokerCode");
            this.hmFieldRela.put("InvMgr", "FInvMgrCode");
            this.hmFieldRela.put("Port", "FPortCode");
            this.hmFieldRela.put("Cury", "FCuryCode");
            this.hmFieldRela.put("CatType", "FCatCode");
            this.hmFieldRela.put("SubCatType", "FSubCatCode");
            this.hmFieldRela.put("TsfType", "FTsfTypeCode");
            this.hmFieldRela.put("SubTsfType", "FSubTsfTypeCode");

            hmFieldIndRela = new HashMap();
            hmFieldIndRela.put("InvMgr", "FInvMgr");
            hmFieldIndRela.put("CatType", "FCat");
            hmFieldIndRela.put("Port", "FPort");
            hmFieldIndRela.put("SubCatType", "FSubCat");
            hmFieldIndRela.put("Broker", "FBroker");
            hmFieldIndRela.put("Cury", "FCury");
            hmFieldIndRela.put("SecCode", "FSecurity");
            hmFieldIndRela.put("TsfType", "FTsfType"); //调拨类型
            hmFieldIndRela.put("SubTsfType", "FSubTsfType"); //调拨子类型

            hmTableRela = new HashMap();
            hmTableRela.put("InvMgr",
                            "(select FInvMgrCode,FInvMgrName as FAccName from " +
                            pub.yssGetTableName("tb_para_investmanager") +
                            " where FCheckState = 1) z on a1.FAccCode = z.FInvMgrCode");
            hmTableRela.put("CatType",
                            "(select FCatCode,FCatName as FAcName from Tb_Base_Category" +
                            " where FCheckState = 1) z on a1.FAccCode = z.FCatCode");
            hmTableRela.put("Port",
                            "(select FPortCode,FPortName as FAccName from " +
                            pub.yssGetTableName("Tb_Para_Portfolio") +
                            " where FCheckState = 1) z on a1.FAccCode = z.FPortCode");
            hmTableRela.put("SubCatType",
                            "(select FSubCatCode,FSubCatName as FAccName from Tb_Base_SubCategory" +
                            " where FCheckState = 1) z on a1.FAccCode = z.FSubCatCode");
            hmTableRela.put("SecCode",
                            "(select FSecurityCode,FSecurityName as FAccName from " +
                            pub.yssGetTableName("tb_para_security") +
                            " where FCheckState = 1) z on a1.FAccCode = z.FSecurityCode");
            hmTableRela.put("Broker",
                            "(select FBankCode,FBankName as FAccName from " +
                            pub.yssGetTableName("Tb_Para_Bank") +
                            " where FCheckState = 1) z on a1.FAccCode = z.FBankCode");
            hmTableRela.put("Cury",
                            "(select FCuryCode,FCuryName as FAccName from " +
                            pub.yssGetTableName("Tb_Para_Currency") +
                            " where FCheckState = 1) z on a1.FAccCode = z.FCuryCode");
        } catch (Exception e) {
            throw new YssException("证券台帐初始化哈希表出错！", e);
        }
    }
}
