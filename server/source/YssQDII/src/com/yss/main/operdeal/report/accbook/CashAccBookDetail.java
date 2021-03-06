package com.yss.main.operdeal.report.accbook;

import java.sql.*;
import com.yss.util.*;
import com.yss.main.operdeal.report.accbook.pojo.*;
import com.yss.main.cusreport.*;
import java.util.*;

public class CashAccBookDetail
    extends BaseAccBook {
    public CashAccBookDetail() {
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
            arrResult = getResultList(); // 得到数据
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
        CashAccBookDetailBean cashAccBook;
        try {
            for (int i = 0; i < arrResult.size(); i++) {
                cashAccBook = (CashAccBookDetailBean) arrResult.get(i);
                hmCellStyle = getCellStyles("DsCashAcc002");
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                    " where FRepDsCode = " + dbl.sqlString("DsCashAcc002") +
                    " and FCheckState = 1 order by FOrderIndex";
                rs = dbl.openResultSet(strSql);

                while (rs.next()) {
                    //获得样式
                    sKey = "DsCashAcc002" + "\tDSF\t-1\t" +
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
     * 从数据库中取数存入哈希表
     * @throws YssException
     */
    public ArrayList getResultList() throws YssException {
        ArrayList arrResult = null;
        try {
            if (this.bIsRecPay) {
                arrResult = this.getCashRecPayResultList();
            } else {
                arrResult = this.getCashResultList();
            }
        } catch (Exception e) {
            throw new YssException("证券台帐获取结果出错！", e);
        }
        return arrResult;
    }

    /**
     * 现金台帐获取涉及应收应付的的明细数据列表
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
                CashAccBookDetailBean cashBook = new CashAccBookDetailBean();
                cashBook.setAccountCode(rs.getString("FCashAccCode"));
                cashBook.setAccountName(rs.getString("FCashAccName"));
                cashBook.setCuryCode(rs.getString("FCuryCode"));
                cashBook.setTsfTypeCode("期初数");
                cashBook.setEndFund(rs.getDouble("FBal"));
                if (this.bIsPort) {
                    cashBook.setPortEndFund(rs.getDouble("FPortCuryBal"));
                }
                hmBeginResult.put(cashBook.getAccountCode(), cashBook);
            }
            dbl.closeResultSetFinal(rs);
            //取台帐期间交易
            rs = dbl.openResultSet(this.getTradeRecPaySql().toString());
            while (rs.next()) {
                CashAccBookDetailBean cashBook = new CashAccBookDetailBean();
                cashBook.setTransDate(rs.getDate("FTransDate"));
                cashBook.setAccountCode(rs.getString("FCashAccCode"));
                cashBook.setAccountName(rs.getString("FCashAccName"));
                cashBook.setCuryCode(rs.getString("FCuryCode"));
                cashBook.setTsfTypeCode(rs.getString("FTsfTypeName"));
                cashBook.setSubTsfTypeCode(rs.getString("FSubTsfTypeName"));
                cashBook.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                cashBook.setTransNum(rs.getString("FNum"));
                if (this.bIsPort) {
                    cashBook.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                }
                //判断方向为流入还是流出
                //如果调拨类型为应收或者应付，则方向算为流入，否则为流出
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
                CashAccBookDetailBean tmpCashBook = null;
                tmpCashBook = (CashAccBookDetailBean) hmBeginResult.get(cashBook.getAccountCode());
                if (tmpCashBook == null) {
                    tmpCashBook = new CashAccBookDetailBean();
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
     *
     * @throws YssException
     * @return ArrayList
     */
    public ArrayList getCashResultList() throws YssException {
        HashMap hmResult = new HashMap();
        ArrayList arrTranResult = new ArrayList();
        ArrayList arrResult = new ArrayList();
        ResultSet rs = null;
        String sHmKey = "";
        CashAccBookDetailBean cashDetail = new CashAccBookDetailBean();
        try {
            //获取期初
            rs = dbl.openResultSet(this.getStartStorageSql().toString());
            while (rs.next()) {
                cashDetail.setAccountCode(rs.getString("FCashAccCode"));
                cashDetail.setAccountName(rs.getString("FCashAccName"));
                cashDetail.setCuryCode(rs.getString("FCuryCode"));
                cashDetail.setEndFund(rs.getDouble("FAccBalance"));
                cashDetail.setTsfTypeCode("期初数");
                if (this.bIsPort) {
                    cashDetail.setPortEndFund(rs.getDouble("FPortCuryBal"));
                }
                sHmKey = cashDetail.getAccountCode();
                hmResult.put(sHmKey, cashDetail);
            }
            dbl.closeResultSetFinal(rs);

            //获取台帐期间
            rs = dbl.openResultSet(this.getTradeSql().toString());
            while (rs.next()) {
                cashDetail = new CashAccBookDetailBean();
                sHmKey = "";
                cashDetail.setAccountCode(rs.getString("FCashAccCode"));
                cashDetail.setAccountName(rs.getString("FCashAccName"));
                cashDetail.setCuryCode(rs.getString("FCuryCode"));
                cashDetail.setTransDate(rs.getDate("FTransDate"));
                cashDetail.setTransferDate(rs.getDate("FTransferDate"));
                cashDetail.setTsfTypeCode(rs.getString("FTsfTypeName"));
                cashDetail.setSubTsfTypeCode(rs.getString("FSubTsfTypeName"));
                cashDetail.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                cashDetail.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                cashDetail.setTransNum(rs.getString("FNum"));
                //如果资金方向为流入
                if (rs.getInt("FInOut") == 1) {
                    cashDetail.setInMoney(rs.getDouble("FMoney"));
                }
                //如果资金方向为流出
                if (rs.getInt("FInOut") == -1) {
                    cashDetail.setOutMoney(rs.getDouble("FMoney"));
                }
                if (this.bIsPort) {
                    cashDetail.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                    //计算组合金额
                    double dbPortInMoney = this.getSettingOper().calPortMoney(
                        cashDetail.getInMoney(),
                        cashDetail.getBaseCuryRate(),
                        cashDetail.getPortCuryRate(),
                        //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        "", cashDetail.getTransDate(), rs.getString("FPortCode"));
                    double dbPortOutMoney = this.getSettingOper().calPortMoney(
                        cashDetail.getOutMoney(),
                        cashDetail.getBaseCuryRate(),
                        cashDetail.getPortCuryRate(),
                        //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        "", cashDetail.getTransDate(), rs.getString("FPortCode"));

                    cashDetail.setPortInMoney(cashDetail.getPortInMoney() + dbPortInMoney);
                    cashDetail.setPortOutMoney(cashDetail.getPortOutMoney() + dbPortOutMoney);
                }
                //----------判断调拨是否有期初数------------//
                CashAccBookDetailBean tmpBook = null;
                tmpBook = (CashAccBookDetailBean) hmResult.get(cashDetail.getAccountCode());
                if (tmpBook == null) {
                    tmpBook = new CashAccBookDetailBean();
                    tmpBook.setAccountCode(cashDetail.getAccountCode());
                    tmpBook.setAccountName(cashDetail.getAccountName());
                    tmpBook.setCuryCode(cashDetail.getCuryCode());
                    tmpBook.setTsfTypeCode("期初数");
                    hmResult.put(cashDetail.getAccountCode(), tmpBook);
                }
                //---------------------------------------//
                arrTranResult.add(cashDetail);
            }
            arrResult = this.getOrderList(hmResult, arrTranResult);
        } catch (Exception e) {
            throw new YssException("将现金台帐数据装入哈希表出错！", e);
        }
        return arrResult;
    }

    /**
     * 排序和计算储存在结果 List 中的数据
     * @throws YssException
     * @return ArrayList
     */
    public ArrayList getOrderList(HashMap hmBeginResult, ArrayList arrTranResult) throws YssException {
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
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                               pub.yssGetTableName("TB_Temp_DetailBook_" +
                    pub.getUserCode())));
                /**end*/
            }
            //------------------创建储存明晰台帐数据的临时表--------------------//
            bufSql.append("CREATE TABLE " + pub.yssGetTableName("TB_Temp_DetailBook_" + pub.getUserCode()));
            bufSql.append(" (");
            bufSql.append(" FTransferDate DATE,");
            bufSql.append(" FTransDate DATE,");
            bufSql.append(" FAccountCode VARCHAR(20),");
            bufSql.append(" FAccountName VARCHAR(50),");
            bufSql.append(" FCuryCode VARCHAR(20),");
            bufSql.append(" FTsfTypeCode VARCHAR(20),");
            bufSql.append(" FSubTsfTypeCode VARCHAR(20),");
            bufSql.append(" FBaseCuryRate DECIMAL(20, 5),");
            bufSql.append(" FPortCuryRate DECIMAL(20, 5),");
            bufSql.append(" FInMoney DECIMAL(18, 4),");
            bufSql.append(" FOutMoney DECIMAL(18, 4),");
            bufSql.append(" FEndFund DECIMAL(18, 4),");
            bufSql.append(" FPortInMoney DECIMAL(18, 4),");
            bufSql.append(" FPortOutMoney DECIMAL(18, 4),");
            bufSql.append(" FPortEndFund DECIMAL(18, 4),");
            bufSql.append(" FTransNum VARCHAR(20),");
            bufSql.append(" FOrder VARCHAR(30)"); //用来排序
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            //--------------------------------------------------------------//

            bufSql.append("INSERT INTO ");
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" + pub.getUserCode()));
            bufSql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstmt = conn.prepareStatement(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //----------------遍历 ArrayList 将调拨明细数据插入临时表---------------//
            int arrLen = arrTranResult.size();
            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < arrLen; i++) {
                CashAccBookDetailBean tranBook = null;
                tranBook = (CashAccBookDetailBean) arrTranResult.get(i);
                if (tranBook == null) {
                    continue;
                }
                pstmt.setDate(1, new java.sql.Date(tranBook.getTransferDate().getTime()));
                pstmt.setDate(2, new java.sql.Date(tranBook.getTransDate().getTime()));
                pstmt.setString(3, tranBook.getAccountCode());
                pstmt.setString(4, tranBook.getAccountName());
                pstmt.setString(5, tranBook.getCuryCode());
                pstmt.setString(6, tranBook.getTsfTypeCode());
                pstmt.setString(7, tranBook.getSubTsfTypeCode());
                pstmt.setDouble(8, tranBook.getBaseCuryRate());
                pstmt.setDouble(9, tranBook.getPortCuryRate());
                pstmt.setDouble(10, tranBook.getInMoney());
                pstmt.setDouble(11, tranBook.getOutMoney());
                pstmt.setDouble(12, tranBook.getEndFund());
                pstmt.setDouble(13, tranBook.getPortInMoney());
                pstmt.setDouble(14, tranBook.getPortOutMoney());
                pstmt.setDouble(15, tranBook.getPortEndFund());
                pstmt.setString(16, tranBook.getTransNum());
                pstmt.setString(17, tranBook.getAccountCode() + "InOut");
                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------------------------------------------------------------//
            dbl.closeStatementFinal(pstmt);

            bufSql.append("INSERT INTO ");
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" + pub.getUserCode()));
            bufSql.append("(FAccountCode, FAccountName, FCuryCode, FEndFund, FPortEndFund, FTsfTypeCode,FOrder)");
            bufSql.append(" VALUES(?,?,?,?,?,?,?)");
            pstmt = conn.prepareStatement(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //---------------------枚举哈希表将期初数插入临时表---------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            java.util.Iterator it = hmBeginResult.values().iterator();
            while (it.hasNext()) {
                CashAccBookDetailBean beginBook = null;
                beginBook = (CashAccBookDetailBean) it.next();
                if (beginBook == null) {
                    continue;
                }

                pstmt.setString(1, beginBook.getAccountCode());
                pstmt.setString(2, beginBook.getAccountName());
                pstmt.setString(3, beginBook.getCuryCode());
                pstmt.setDouble(4, beginBook.getEndFund());
                pstmt.setDouble(5, beginBook.getPortEndFund());
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
            bufSql.append(" ORDER BY FOrder, FTransNum ASC");

            rs = dbl.openResultSet(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            double EndFund = 0;
            double PortEndFund = 0;

            while (rs.next()) {
                CashAccBookDetailBean endResultBook = new CashAccBookDetailBean();

                //期末成本
                if (!rs.getString("FOrder").equals(rs.getString("FAccountCode"))) {
                    EndFund = EndFund + rs.getDouble("FInMoney") -
                        rs.getDouble("FOutMoney");
                } else {
                    EndFund = rs.getDouble("FEndFund");
                }
                //组合货币期末成本
                if (!rs.getString("FOrder").equals(rs.getString("FAccountCode"))) {
                    PortEndFund = PortEndFund + rs.getDouble("FPortInMoney") -
                        rs.getDouble("FPortOutMoney");
                } else {
                    PortEndFund = rs.getDouble("FPortEndFund");
                }

                endResultBook.setTransferDate(rs.getDate("FTransferDate"));
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
                endResultBook.setEndFund(EndFund);
                endResultBook.setPortInMoney(rs.getDouble("FPortInMoney"));
                endResultBook.setPortOutMoney(rs.getDouble("FPortOutMoney"));
                endResultBook.setPortEndFund(PortEndFund);
                endResultBook.setTransNum(rs.getString("FTransNum"));

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
            	/**shashijie ,2011-10-12 , STORY 1698*/
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
            bufSql.append(" FCuryCode VARCHAR(20),");
            bufSql.append(" FTsfTypeCode VARCHAR(20),");
            bufSql.append(" FSubTsfTypeCode VARCHAR(20),");
            bufSql.append(" FBaseCuryRate DECIMAL(20, 5),");
            bufSql.append(" FPortCuryRate DECIMAL(20, 5),");
            bufSql.append(" FInMoney DECIMAL(18, 4),");
            bufSql.append(" FOutMoney DECIMAL(18, 4),");
            bufSql.append(" FEndFund DECIMAL(18, 4),");
            bufSql.append(" FPortInMoney DECIMAL(18, 4),");
            bufSql.append(" FPortOutMoney DECIMAL(18, 4),");
            bufSql.append(" FPortEndFund DECIMAL(18, 4),");
            bufSql.append(" FTransNum VARCHAR(20),");
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
                CashAccBookDetailBean tranBook = null;
                tranBook = (CashAccBookDetailBean) arrTranResult.get(i);
                if (tranBook == null) {
                    continue;
                }
                pstmt.setDate(1, new java.sql.Date(tranBook.getTransDate().getTime()));
                pstmt.setString(2, tranBook.getAccountCode());
                pstmt.setString(3, tranBook.getAccountName());
                pstmt.setString(4, tranBook.getCuryCode());
                pstmt.setString(5, tranBook.getTsfTypeCode());
                pstmt.setString(6, tranBook.getSubTsfTypeCode());
                pstmt.setDouble(7, tranBook.getBaseCuryRate());
                pstmt.setDouble(8, tranBook.getPortCuryRate());
                pstmt.setDouble(9, tranBook.getInMoney());
                pstmt.setDouble(10, tranBook.getOutMoney());
                pstmt.setDouble(11, tranBook.getEndFund());
                pstmt.setDouble(12, tranBook.getPortInMoney());
                pstmt.setDouble(13, tranBook.getPortOutMoney());
                pstmt.setDouble(14, tranBook.getPortEndFund());
                pstmt.setString(15, tranBook.getTransNum());
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
            bufSql.append("(FAccountCode, FAccountName, FCuryCode, FEndFund, FPortEndFund, FTsfTypeCode,FOrder)");
            bufSql.append(" VALUES(?,?,?,?,?,?,?)");
            pstmt = conn.prepareStatement(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //---------------------枚举哈希表将期初数插入临时表---------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            java.util.Iterator it = hmBeginResult.values().iterator();
            while (it.hasNext()) {
                CashAccBookDetailBean beginBook = null;
                beginBook = (CashAccBookDetailBean) it.next();
                if (beginBook == null) {
                    continue;
                }

                pstmt.setString(1, beginBook.getAccountCode());
                pstmt.setString(2, beginBook.getAccountName());
                pstmt.setString(3, beginBook.getCuryCode());
                pstmt.setDouble(4, beginBook.getEndFund());
                pstmt.setDouble(5, beginBook.getPortEndFund());
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
            bufSql.append(" ORDER BY FOrder, FTransNum ASC");

            rs = dbl.openResultSet(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            double EndFund = 0;
            double PortEndFund = 0;

            while (rs.next()) {
                CashAccBookDetailBean endResultBook = new CashAccBookDetailBean();

                //期末成本
                if (!rs.getString("FOrder").equals(rs.getString("FAccountCode"))) {
                    EndFund = EndFund + rs.getDouble("FInMoney") -
                        rs.getDouble("FOutMoney");
                } else {
                    EndFund = rs.getDouble("FEndFund");
                }
                //组合货币期末成本
                if (!rs.getString("FOrder").equals(rs.getString("FAccountCode"))) {
                    PortEndFund = PortEndFund + rs.getDouble("FPortInMoney") -
                        rs.getDouble("FPortOutMoney");
                } else {
                    PortEndFund = rs.getDouble("FPortEndFund");
                }

                endResultBook.setTransferDate(rs.getDate("FTransDate")); //应收应付中没有调拨日期 ，但为了方便做同一个帐户的数据的展开和收缩此列不能为空故将业务日期也作为调拨日期
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
                endResultBook.setEndFund(EndFund);
                endResultBook.setPortInMoney(rs.getDouble("FPortInMoney"));
                endResultBook.setPortOutMoney(rs.getDouble("FPortOutMoney"));
                endResultBook.setPortEndFund(PortEndFund);
                endResultBook.setTransNum(rs.getString("FTransNum"));

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
            bufSql.append(" WHERE " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'");
            bufSql.append(" AND FTsfTypeCode IN (" + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec));
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
     * 获取 现金明细台帐期初信息 SQL
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getStartStorageSql() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        String sWhereFiled = "";
        String sSelectFiled = "";
        String sGroupFiled = "";
        boolean bGroupPort = false;
        try {
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //如果链接中有投资组合
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Port")) {
                    bGroupPort = true;
                }
                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }
            //如果链接中有组合就要按组合分组
            if (bGroupPort) {
                sGroupFiled = ",FPortCode";
            }

            bufSql.append("SELECT ");
            bufSql.append(" FCuryCode,");
            bufSql.append(" FCashAccCode,");
            bufSql.append(" FCashAccName,");
            bufSql.append(" SUM(FAccBalance) AS FAccBalance,");
            bufSql.append(" SUM(FPortCuryBal) AS FPortCuryBal");
            bufSql.append(" FROM (SELECT a.*, b.FAccType, b.FSubAccType, FCashAccName,FBankCode");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Stock_Cash")).append(" a"); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
            bufSql.append(" LEFT JOIN (SELECT FCashAccCode, FAccType, FSubAccType, FCashAccName, FBankCode");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_CashAccount")).append(") b ON a.FCashAccCode ="); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
            bufSql.append(" b.FCashAccCode) a");
            bufSql.append(" WHERE FCheckState = 1");
            bufSql.append(" AND " + this.operSql.sqlStoragEve(dBeginDate));
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY FCuryCode, FCashAccCode, FCashAccName");
            if (sGroupFiled.length() != 0) {
                bufSql.append(sGroupFiled);
            }
        } catch (Exception e) {
            throw new YssException("获取现金明细台帐期初数据 SQL 语句出错！", e);
        }
        return bufSql;
    }

    /**
     * 证券台帐 获取交易期间的 SQL 语句
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getTradeSql() throws YssException {
        StringBuffer bufSql = new StringBuffer(1300);
        String sWhereFiled = "";
        try {
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //如果链接中有投资组合
                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }

            bufSql.append("SELECT b.*,");
            bufSql.append(" a.FTransferDate,");
            bufSql.append(" a.FTransDate,");
            bufSql.append(" a.FTradeNum,");
            bufSql.append(" e.FTsfTypeName,");
            bufSql.append(" f.FSubTsfTypeName,");
            bufSql.append(" c.FCuryCode,");
            bufSql.append(" c.FBankCode,");
            bufSql.append(" c.FCashAccCode,");
            bufSql.append(" c.FCashAccName,");
            bufSql.append(" c.FSubAccType,");
            bufSql.append(" c.FAccType");
            bufSql.append(" FROM (SELECT FNum,");
            bufSql.append(" FTsfTypeCode,");
            bufSql.append(" FSubTsfTypeCode,");
            bufSql.append(" FSecurityCode,");
            bufSql.append(" FTransferDate,");
            bufSql.append(" FTransDate,");
            bufSql.append(" FTradeNum,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Cash_Transfer")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
            bufSql.append(" WHERE FCheckState = 1) a");
            bufSql.append(" LEFT JOIN (SELECT FNum,");
            bufSql.append(" FSubNum,");
            bufSql.append(" FInOut,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FCashAccCode,");
            bufSql.append(" FMoney,");
            bufSql.append(" FBaseCuryRate,");
            bufSql.append(" FPortCuryRate,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Cash_SubTransfer"));
            bufSql.append(" WHERE FCheckState = 1) b ON a.FNum = b.FNum");
            bufSql.append(" LEFT JOIN (SELECT FCashAccCode, FCuryCode, FBankCode, FCashAccName,FSubAccType,FAccType");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_CashAccount")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
            bufSql.append(") c ON b.FCashAccCode = c.FCashAccCode");
            bufSql.append(" LEFT JOIN (SELECT FTsfTypeCode, FTsfTypeName FROM Tb_Base_TransferType) e ON a.FTsfTypeCode =");
            bufSql.append(" e.FTsfTypeCode");
            bufSql.append(" LEFT JOIN (SELECT FSubTsfTypeCode, FSubTsfTypeName");
            bufSql.append(" FROM Tb_Base_SubTransferType) f ON a.FSubTsfTypeCode =");
            bufSql.append(" f.FSubTsfTypeCode");
            bufSql.append(" WHERE FTransferDate BETWEEN " + dbl.sqlDate(this.dBeginDate) + " AND ");
            bufSql.append(dbl.sqlDate(this.dEndDate));
            bufSql.append(sWhereFiled);
            bufSql.append(" ORDER BY b.FNum, b.FSubNum");
        } catch (Exception e) {
            throw new YssException("现金台帐获取台帐期间 SQL 语句出错！", e);
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
