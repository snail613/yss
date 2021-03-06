package com.yss.main.operdeal.report.accbook.cashbook;

import java.sql.*;
import com.yss.util.*;
import com.yss.main.operdeal.report.accbook.pojo.*;
import com.yss.main.cusreport.*;
import java.util.*;

import com.yss.main.operdeal.report.accbook.BaseAccBook;

public class CashBookDetailRecPay
    extends BaseAccBook {
    public CashBookDetailRecPay() {
    }

    /**
     * 获取报表数据入口 （基类方法的实现）
     * @param sType String
     * @throws YssException
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        ArrayList arrResult = null;
        try {
            arrResult = getCashRecPayResultList(); // 得到数据
            sResult = buildRowCompResult(arrResult); //得到含格式的数据  父类中
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取现金汇总台帐出错： \n" + e.getMessage());
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
        CashBookDetailRecPayBean cashAccBook;
        try {
            for (int i = 0; i < arrResult.size(); i++) {
                cashAccBook = (CashBookDetailRecPayBean) arrResult.get(i);
                hmCellStyle = getCellStyles("DsCashAccRecPay002");
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                    " where FRepDsCode = " + dbl.sqlString("DsCashAccRecPay002") +
                    " and FCheckState = 1 order by FOrderIndex";
                rs = dbl.openResultSet(strSql);

                while (rs.next()) {
                    //获得样式
                    sKey = "DsCashAccRecPay002" + "\tDSF\t-1\t" +
                        rs.getString("FOrderIndex");

                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");

                    buf.append(YssReflection.getPropertyValue(cashAccBook,
                        rs.getString("FDsField")) +
                               "\t");
                }
                dbl.closeResultSetFinal(rs);
                if (buf.toString().trim().length() > 1) {
                    strReturn = strReturn + buf.toString().substring(0,
                        buf.toString().length() - 1);
                    buf.delete(0, buf.toString().length());
                    strReturn = strReturn + "\r\n"; //每一行用\r\n隔开
                }

            }
        } catch (Exception e) {
            throw new YssException("获取格式出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return strReturn;
    }

    /**
     * 获取现金应收应付明细台帐数据列表
     * @throws YssException
     * @return ArrayList
     */
    public ArrayList getCashRecPayResultList() throws YssException {
        ArrayList arrRecPayResult = new ArrayList();
        ArrayList arrResult = new ArrayList();
        HashMap hmBeginResult = new HashMap();
        ResultSet rs = null;
        try {
            //取期初
            rs = dbl.openResultSet(this.getStartRecPayStorageSql().toString());
            while (rs.next()) {
                CashBookDetailRecPayBean cashBook = new CashBookDetailRecPayBean();
                cashBook.setAccountCode(rs.getString("FCashAccCode"));
                cashBook.setAccountName(rs.getString("FCashAccName"));
                cashBook.setCuryCode(rs.getString("FCuryCode"));
                cashBook.setTsfTypeCode("期初数");
                cashBook.setEndMoney(rs.getDouble("FBal"));
                if (this.bIsPort) {
                    cashBook.setPortEndMoney(rs.getDouble("FPortCuryBal"));
                }
                hmBeginResult.put(cashBook.getAccountCode(), cashBook);
            }
            dbl.closeResultSetFinal(rs);
            //取台帐期间交易
            rs = dbl.openResultSet(this.getTradeRecPaySql().toString());
            while (rs.next()) {
                CashBookDetailRecPayBean cashBook = new CashBookDetailRecPayBean();
                cashBook.setTransDate(rs.getDate("FTransDate"));
                cashBook.setAccountCode(rs.getString("FCashAccCode"));
                cashBook.setAccountName(rs.getString("FCashAccName"));
                cashBook.setCuryCode(rs.getString("FCuryCode"));
                cashBook.setTsfTypeCode(rs.getString("FTsfTypeName"));
                cashBook.setSubTsfTypeCode(rs.getString("FSubTsfTypeName"));
                cashBook.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                cashBook.setNum(rs.getString("FNum"));
                if (this.bIsPort) {
                    cashBook.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                }
                //判断方向为流入还是流出
                //如果调拨类型为应收或者应付或者汇兑损益，则方向算为流入，否则为流出
                String sTmpTsfType = rs.getString("FTsfTypeCode");
                if (sTmpTsfType.equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Rec) ||
                    sTmpTsfType.equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Pay) ||
                    sTmpTsfType.equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_FX)) {
                    cashBook.setInMoney(rs.getDouble("FMoney"));
                    if (this.bIsPort) {
                        cashBook.setPortInMoney(rs.getDouble("FPortCuryMoney"));
                    }
                } else {
                    cashBook.setOutMoney(rs.getDouble("FMoney"));
                    if (this.bIsPort) {
                        cashBook.setPortOutMoney(rs.getDouble("FPortCuryMoney"));
                    }
                }
                //判断是否有期初数
                CashBookDetailRecPayBean tmpCashBook = null;
                tmpCashBook = (CashBookDetailRecPayBean) hmBeginResult.get(cashBook.
                    getAccountCode());
                if (tmpCashBook == null) {
                    tmpCashBook = new CashBookDetailRecPayBean();
                    tmpCashBook.setTsfTypeCode("期初数");
                    tmpCashBook.setAccountCode(cashBook.getAccountCode());
                    tmpCashBook.setAccountName(cashBook.getAccountName());
                    tmpCashBook.setCuryCode(cashBook.getCuryCode());
                }
                hmBeginResult.put(tmpCashBook.getAccountCode(), tmpCashBook);
                arrRecPayResult.add(cashBook);
            }
            arrResult = this.getOrderRecPayList(hmBeginResult, arrRecPayResult);
        } catch (Exception e) {
            throw new YssException("现金台帐获取应收应付的的明细数据列表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return arrResult;
    }

    /**
     * 排序和计算应收应付数据
     * @throws YssException
     * @return ArrayList
     */
    public ArrayList getOrderRecPayList(HashMap hmBeginResult, ArrayList arrTranResult) throws YssException {
        ArrayList arrResult = new ArrayList();
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        PreparedStatement pstmt = null;
        boolean bTrans = false;
        ResultSet rs = null;
        try {
            //如果表已存在则删除
            if (dbl.yssTableExist(pub.yssGetTableName("TB_Temp_DetailBook_" +
                pub.getUserCode()))) {
            	/**shashijie ,2011-10-12 , STORY 1698 */
                dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                               pub.yssGetTableName("TB_Temp_DetailBook_" +
                    pub.getUserCode())));
                /**end*/
            }
            //------------------创建储存明晰台帐数据的临时表--------------------//
            bufSql.append("CREATE TABLE " + pub.yssGetTableName("TB_Temp_DetailBook_" + pub.getUserCode()));
            bufSql.append(" (");
            bufSql.append(" FTransDate DATE,");
            bufSql.append(" FAccountCode VARCHAR(20),");
            bufSql.append(" FAccountName VARCHAR(50),");
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
            //--------------------------------------------------------------//

            bufSql.append("INSERT INTO ");
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" + pub.getUserCode()));
            bufSql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmt = conn.prepareStatement(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //----------------遍历 ArrayList 将调拨明细数据插入临时表---------------//
            int arrLen = arrTranResult.size();
            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < arrLen; i++) {
                CashBookDetailRecPayBean tranBook = null;
                tranBook = (CashBookDetailRecPayBean) arrTranResult.get(i);
                if (tranBook == null) {
                    continue;
                }
                pstmt.setDate(1, new java.sql.Date(tranBook.getTransDate().getTime()));
                pstmt.setString(2, tranBook.getAccountCode());
                pstmt.setString(3, tranBook.getAccountName());
                pstmt.setString(4, tranBook.getTsfTypeCode());
                pstmt.setString(5, tranBook.getSubTsfTypeCode());
                pstmt.setString(6, tranBook.getCuryCode());
                pstmt.setDouble(7, tranBook.getInMoney());
                pstmt.setDouble(8, tranBook.getOutMoney());
                pstmt.setDouble(9, tranBook.getEndMoney());
                pstmt.setDouble(10, tranBook.getBaseCuryRate());
                pstmt.setDouble(11, tranBook.getPortCuryRate());
                pstmt.setDouble(12, tranBook.getPortInMoney());
                pstmt.setDouble(13, tranBook.getPortOutMoney());
                pstmt.setDouble(14, tranBook.getPortEndMoney());
                pstmt.setString(15, tranBook.getNum());
                pstmt.setString(16, tranBook.getAccountCode() + "InOut");
                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------------------------------------------------------------//
            dbl.closeStatementFinal(pstmt);

            bufSql.append("INSERT INTO ");
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" + pub.getUserCode()));
            bufSql.append("(FAccountCode, FAccountName, FCuryCode, FEndMoney, FPortEndMoney, FTsfTypeCode, FOrder)");
            bufSql.append(" VALUES(?,?,?,?,?,?,?)");
            pstmt = conn.prepareStatement(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //---------------------枚举哈希表将期初数插入临时表---------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            java.util.Iterator it = hmBeginResult.values().iterator();
            while (it.hasNext()) {
                CashBookDetailRecPayBean beginBook = null;
                beginBook = (CashBookDetailRecPayBean) it.next();
                if (beginBook == null) {
                    continue;
                }

                pstmt.setString(1, beginBook.getAccountCode());
                pstmt.setString(2, beginBook.getAccountName());
                pstmt.setString(3, beginBook.getCuryCode());
                pstmt.setDouble(4, beginBook.getEndMoney());
                pstmt.setDouble(5, beginBook.getPortEndMoney());
                pstmt.setString(6, beginBook.getTsfTypeCode());
                pstmt.setString(7, beginBook.getAccountCode());
                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------------------------------------------------------------//
            //------------将临时表中所有数据查询出来装入 ArrayList------------------//
            bufSql.append("SELECT * FROM　");
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" + pub.getUserCode()));
            bufSql.append(" ORDER BY FOrder, FNum ASC");

            rs = dbl.openResultSet(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            double dbEndMoney = 0;
            double dbPortEndMoney = 0;

            while (rs.next()) {
                CashBookDetailRecPayBean endResultBook = new CashBookDetailRecPayBean();

                //期末成本
                if (!rs.getString("FOrder").equals(rs.getString("FAccountCode"))) {
                    dbEndMoney = dbEndMoney + rs.getDouble("FInMoney") -
                        rs.getDouble("FOutMoney");
                } else {
                    dbEndMoney = rs.getDouble("FEndMoney");
                }
                //组合货币期末成本
                if (!rs.getString("FOrder").equals(rs.getString("FAccountCode"))) {
                    dbPortEndMoney = dbPortEndMoney + rs.getDouble("FPortInMoney") -
                        rs.getDouble("FPortOutMoney");
                } else {
                    dbPortEndMoney = rs.getDouble("FPortEndMoney");
                }

                //endResultBook.setTransferDate(rs.getDate("FTransDate"));//应收应付中没有调拨日期 ，但为了方便做同一个帐户的数据的展开和收缩此列不能为空故将业务日期也作为调拨日期
                endResultBook.setTransDate(rs.getDate("FTransDate"));
                endResultBook.setAccountCode(rs.getString("FAccountCode"));
                //endResultBook.setAccountName(rs.getString("FAccountName"));
                endResultBook.setCuryCode(rs.getString("FCuryCode"));
                endResultBook.setTsfTypeCode(rs.getString("FTsfTypeCode"));
                endResultBook.setSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
                endResultBook.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                endResultBook.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                endResultBook.setInMoney(rs.getDouble("FInMoney"));
                endResultBook.setOutMoney(rs.getDouble("FOutMoney"));
                endResultBook.setEndMoney(dbEndMoney);
                endResultBook.setPortInMoney(rs.getDouble("FPortInMoney"));
                endResultBook.setPortOutMoney(rs.getDouble("FPortOutMoney"));
                endResultBook.setPortEndMoney(dbPortEndMoney);
                endResultBook.setNum(rs.getString("FNum"));

                if (rs.getString("FOrder").indexOf("InOut") > 0) {
                    endResultBook.setAccountName(". " + rs.getString("FAccountName"));
                    endResultBook.setOrder(0);
                    endResultBook.setIndex("");
                } else {
                    endResultBook.setAccountName(rs.getString("FAccountName"));
                    endResultBook.setOrder(1);
                    endResultBook.setIndex("-");
                }

                arrResult.add(endResultBook);
            }
            //---------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("现金明晰台帐序列化台帐哈希表出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(pstmt);
            dbl.closeResultSetFinal(rs);
        }
        return arrResult;
    }

    /**
     * 获取应收应付期初的 SQL 语句
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getStartRecPayStorageSql() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        String sWhereFiled = "";
        try {
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //----------如果 WHERE 条件中有调拨类型和调拨子类型，必须进行相应的处理，要加上汇兑损益和相应的流出的代码----------//
                if (this.aryAccBookDefine[i].equalsIgnoreCase("TsfType")) {
                    sWhereFiled = sWhereFiled + " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = ";
                    if (this.aryAccBookLink[i + 1].equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Rec)) {
                        sWhereFiled = sWhereFiled + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + ")";
                    } else if (this.aryAccBookLink[i + 1].equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Pay)) {
                        sWhereFiled = sWhereFiled + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + ")";
                    }
                    continue;
                } else if (this.aryAccBookDefine[i].equalsIgnoreCase("SubTsfType")) {
                    sWhereFiled = sWhereFiled + " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX + this.aryAccBookLink[i + 1]) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = ";
                    if (this.aryAccBookLink[i + 1].substring(0, 2).equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Rec)) {
                        sWhereFiled = sWhereFiled + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income + this.aryAccBookLink[i + 1].substring(2)) + ")";
                    } else if (this.aryAccBookLink[i + 1].substring(0, 2).equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Pay)) {
                        sWhereFiled = sWhereFiled + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee + this.aryAccBookLink[i + 1].substring(2)) + ")";
                    }
                    continue;
                }
                //------------------------------------------------------------------------------------------------------//
                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }

            bufSql.append("SELECT FCashAccCode,");
            bufSql.append(" FCashAccName,");
            bufSql.append(" FCuryCode,");
            bufSql.append(" SUM(FBal) AS FBal,");
            bufSql.append(" SUM(FPortCuryBal) AS FPortCuryBal");
            bufSql.append(" FROM (SELECT a.*, FCashAccCode, FCashAccName");
            bufSql.append(" FROM (SELECT FYearMonth,");
            bufSql.append(" FStorageDate,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FCashAccCode AS FCashCode,");
            bufSql.append(" FTsfTypeCode,");
            bufSql.append(" FSubTsfTypeCode,");
            bufSql.append(" FCuryCode,");
            bufSql.append(" FBal,");
            bufSql.append(" FPortCuryBal,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_CashPayRec"));
            bufSql.append(" WHERE ");
            bufSql.append(" FTsfTypeCode IN (" + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec));
            bufSql.append("," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay));
            bufSql.append("," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) + ")) a");
            bufSql.append(" JOIN (SELECT FCashAccCode,");
            bufSql.append(" FCashAccName,");
            bufSql.append(" FAccType,");
            bufSql.append(" FSubAccType,");
            bufSql.append(" FBankCode");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Para_CashAccount"));
            bufSql.append(" ) b ON a.FCashCode = b.FCashAccCode");
            bufSql.append(" WHERE a.FCheckState = 1");
            bufSql.append(" AND " + this.operSql.sqlStoragEve(dBeginDate));
            bufSql.append(sWhereFiled + " ) a1");
            bufSql.append(" GROUP BY FCashAccCode, FCashAccName, FCuryCode");

        } catch (Exception e) {
            throw new YssException("现金明细台帐，获取应收应付期初的 SQL 出错！", e);
        }
        return bufSql;
    }

    /**
     * 应收应付台帐期间交易明细数据的 SQL 语句
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getTradeRecPaySql() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        String sWhereFiled = "";
        try {
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //----------如果 WHERE 条件中有调拨类型和调拨子类型，必须进行相应的处理，要加上汇兑损益和相应的流出的代码----------//
                if (this.aryAccBookDefine[i].equalsIgnoreCase("TsfType")) {
                    sWhereFiled = sWhereFiled + " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = ";
                    if (this.aryAccBookLink[i + 1].equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Rec)) {
                        sWhereFiled = sWhereFiled + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + ")";
                    } else if (this.aryAccBookLink[i + 1].equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Pay)) {
                        sWhereFiled = sWhereFiled + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + ")";
                    }
                    continue;
                } else if (this.aryAccBookDefine[i].equalsIgnoreCase("SubTsfType")) {
                    sWhereFiled = sWhereFiled + " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX + this.aryAccBookLink[i + 1]) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = ";
                    if (this.aryAccBookLink[i + 1].substring(0, 2).equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Rec)) {
                        sWhereFiled = sWhereFiled + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income + this.aryAccBookLink[i + 1].substring(2)) + ")";
                    } else if (this.aryAccBookLink[i + 1].substring(0, 2).equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Pay)) {
                        sWhereFiled = sWhereFiled + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee + this.aryAccBookLink[i + 1].substring(2)) + ")";
                    }
                    continue;
                }
                //------------------------------------------------------------------------------------------------------//
                //如果链接中有投资组合
                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }

            bufSql.append("SELECT a1.*, z.FTsfTypeName, x.FSubTsfTypeName");
            bufSql.append(" FROM (SELECT a.*, FCashAccCode, FCashAccName");
            bufSql.append(" FROM (SELECT FTransDate,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FCashAccCode AS FCashCode,");
            bufSql.append(" FTsfTypeCode,");
            bufSql.append(" FSubTsfTypeCode,");
            bufSql.append(" FCuryCode,");
            bufSql.append(" FMoney,");
            bufSql.append(" FPortCuryMoney,");
            bufSql.append(" FBaseCuryRate,");
            bufSql.append(" FPortCuryRate,");
            bufSql.append(" FNum,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_CashPayRec"));
            bufSql.append(" WHERE FTsfTypeCode IN (" + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec));
            bufSql.append("," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay));
            bufSql.append("," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income));
            bufSql.append("," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee));
            bufSql.append("," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) + ")) a");
            bufSql.append(" JOIN (SELECT FCashAccCode,");
            bufSql.append(" FCashAccName,");
            bufSql.append(" FAccType,");
            bufSql.append(" FSubAccType,");
            bufSql.append(" FBankCode");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Para_CashAccount") + ") b ON a.FCashCode = b.FCashAccCode");
            bufSql.append(" WHERE a.FCheckState = 1");
            bufSql.append(" AND FTransDate BETWEEN " + dbl.sqlDate(this.dBeginDate) + " AND ");
            bufSql.append(dbl.sqlDate(this.dEndDate));
            bufSql.append(sWhereFiled + ") a1");
            bufSql.append(" LEFT JOIN (SELECT FTsfTypeCode, FTsfTypeName");
            bufSql.append(" FROM Tb_Base_TransferType");
            bufSql.append(" WHERE FCheckState = 1) z ON a1.FTsfTypeCode = z.FTsfTypeCode");
            bufSql.append(" LEFT JOIN (SELECT FSubTsfTypeCode, FSubTsfTypeName");
            bufSql.append(" FROM Tb_Base_SubTransferType");
            bufSql.append(" WHERE FCheckState = 1) x ON a1.FSubTsfTypeCode = x.FSubTsfTypeCode");
        } catch (Exception e) {
            throw new YssException("现金台帐,获取应收应付台帐期间交易明细数据的 SQL 出错！", e);
        }
        return bufSql;
    }

    /**
     * 完成哈希表的初始化工作
     * @throws YssException
     */
    public void initHashTable() throws YssException {
        String invmgrField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.
            YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_InvMgr);

        this.hmSelectField = new HashMap();
        this.hmSelectField.put("Acc", "FCashAccCode as AccCode"); //帐户
        this.hmSelectField.put("Bank", "FBankCode as as AccCode"); //银行
        this.hmSelectField.put("Cury", "FCuryCode as AccCode"); //货币
        this.hmSelectField.put("InvMgr", invmgrField + " as AccCode"); //投资经理
        this.hmSelectField.put("Port", "FPortCode as AccCode"); //投资组合

        this.hmSelectField.put("AccType", "FAccTypeCode  as AccCode"); //帐户类型
        this.hmSelectField.put("SubAccType", "FSubAccTypeCode  as AccCode"); //帐户子类型
        this.hmSelectField.put("TsfType", "FTsfTypeCode  as AccCode"); //调拨类型
        this.hmSelectField.put("SubTsfType", "FSubTsfTypeCode  as AccCode"); //调拨子类型

        this.hmFieldRela = new HashMap();
        this.hmFieldRela.put("InvMgr", invmgrField);
        this.hmFieldRela.put("Port", "FPortCode");
        this.hmFieldRela.put("Acc", "FCashAccCode");
        this.hmFieldRela.put("Cury", "FCuryCode");
        this.hmFieldRela.put("Bank", "FBankCode");
        this.hmFieldRela.put("AccType", "FAccType"); //帐户类型
        this.hmFieldRela.put("SubAccType", "FSubAccType"); //帐户子类型
        this.hmFieldRela.put("TsfType", "FTsfTypeCode"); //调拨类型
        this.hmFieldRela.put("SubTsfType", "FSubTsfTypeCode"); //调拨子类型

        hmFieldIndRela = new HashMap();
        hmFieldIndRela.put("InvMgr", "FInvMgr");
        hmFieldIndRela.put("CatType", "FCat");
        hmFieldIndRela.put("Port", "FPort");
        hmFieldIndRela.put("Acc", "FCashAcc");
        hmFieldIndRela.put("Bank", "FBank");
        hmFieldIndRela.put("Cury", "FCury");
        hmFieldIndRela.put("AccType", "FAccType"); //帐户类型
        hmFieldIndRela.put("SubAccType", "FSubAccType"); //帐户子类型
        hmFieldIndRela.put("TsfType", "FTsfType"); //调拨类型
        hmFieldIndRela.put("SubTsfType", "FSubTsfType"); //调拨子类型

        hmTableRela = new HashMap();
        hmTableRela.put("InvMgr",
                        "(select FInvMgrCode,FInvMgrName as FAccName from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FCheckState = 1) z on a1.FAccCode = z.FInvMgrCode");
        hmTableRela.put("Port",
                        "(select FPortCode,FPortName as FAccName from " +
                        pub.yssGetTableName("Tb_Para_Portfolio") +
                        " where FCheckState = 1) z on a1.FAccCode = z.FPortCode");
        hmTableRela.put("Acc",
                        "(select FCashAccCode,FCashAccName as FAccName from " +
                        pub.yssGetTableName("tb_para_cashaccount") +
                        " where FCheckState = 1) z on a1.FAccCode = z.FCashAccCode");
        hmTableRela.put("Bank",
                        "(select FBankCode,FBankName as FAccName from " +
                        pub.yssGetTableName("Tb_Para_Bank") +
                        " where FCheckState = 1) z on a1.FAccCode = z.FBankCode");
        hmTableRela.put("Cury",
                        "(select FCuryCode,FCuryName as FAccName from " +
                        pub.yssGetTableName("Tb_Para_Currency") +
                        " where FCheckState = 1) z on a1.FAccCode = z.FCuryCode");

        hmTableRela.put("AccType",
                        "(select FAccTypeCode,FAccTypeName as FAccName from Tb_Base_AccountType" +
                        " where FCheckState = 1) z on a1.FAccCode = z.FAccTypeCode");

        hmTableRela.put("SubAccType",
                        "(select FSubAccTypeCode,FSubAccTypeName as FAccName from Tb_Base_SubAccountType" +
                        " where FCheckState = 1) z on a1.FAccCode = z.FSubAccTypeCode");

        hmTableRela.put("TsfType",
                        "(select FTsfTypeCode,FTsfTypeName as FAccName from Tb_Base_TransferType " +
                        " where FCheckState = 1) z on a1.FAccCode = z.FTsfTypeCode");

        hmTableRela.put("SubTsfType",
                        "(select FSubTsfTypeCode,FSubTsfTypeName as FAccName from Tb_Base_SubTransferType " +
                        " where FCheckState = 1) z on a1.FAccCode = z.FSubTsfTypeCode");

    }
}
