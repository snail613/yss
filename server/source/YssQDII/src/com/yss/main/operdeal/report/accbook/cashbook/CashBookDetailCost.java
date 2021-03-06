package com.yss.main.operdeal.report.accbook.cashbook;

import com.yss.main.operdeal.report.accbook.BaseAccBook;
import java.sql.*;
import com.yss.util.*;
import com.yss.main.operdeal.report.accbook.pojo.*;
import com.yss.main.cusreport.*;
import java.util.*;

public class CashBookDetailCost
    extends BaseAccBook {
    public CashBookDetailCost() {
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
            arrResult = getCashResultList(); // 得到数据
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
     * 获取明细台帐数据
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

                    cashDetail.setPortInMoney(cashDetail.getPortInMoney() +
                                              dbPortInMoney);
                    cashDetail.setPortOutMoney(cashDetail.getPortOutMoney() +
                                               dbPortOutMoney);
                }
                //----------判断调拨是否有期初数------------//
                CashAccBookDetailBean tmpBook = null;
                tmpBook = (CashAccBookDetailBean) hmResult.get(cashDetail.
                    getAccountCode());
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
    public ArrayList getOrderList(HashMap hmBeginResult, ArrayList arrTranResult) throws
        YssException {
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
            bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_Temp_DetailBook_" +
                                              pub.getUserCode()));
            bufSql.append(" (");
            bufSql.append(" FTransferDate DATE,");
            bufSql.append(" FTransDate DATE,");
            bufSql.append(" FAccountCode VARCHAR(20),");
            bufSql.append(" FAccountName VARCHAR(50),");
            bufSql.append(" FCuryCode VARCHAR(20),");
            bufSql.append(" FTsfTypeCode VARCHAR(20),");
            bufSql.append(" FSubTsfTypeCode VARCHAR(20),");
            bufSql.append(" FBaseCuryRate DECIMAL(20, 15),");
            bufSql.append(" FPortCuryRate DECIMAL(20, 15),");
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
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" +
                                              pub.getUserCode()));
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
                pstmt.setDate(1,
                              new java.sql.Date(tranBook.getTransferDate().getTime()));
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
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" +
                                              pub.getUserCode()));
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
            bufSql.append(pub.yssGetTableName("TB_Temp_DetailBook_" +
                                              pub.getUserCode()));
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
            bufSql.append(
                " FROM (SELECT a.*, b.FAccType, b.FSubAccType, FCashAccName,FBankCode");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_Cash") + " a");
            bufSql.append(" LEFT JOIN (SELECT FCashAccCode, FAccType, FSubAccType, FCashAccName, FBankCode");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Para_CashAccount") + ") b ON a.FCashAccCode =");
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
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Cash_Transfer"));
            bufSql.append(" WHERE FCheckState = 1) a");
            bufSql.append(" LEFT JOIN (SELECT FNum,");
            bufSql.append(" FSubNum,");
            bufSql.append(" FInOut,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FCashAccCode AS FCashCode,");
            bufSql.append(" FMoney,");
            bufSql.append(" FBaseCuryRate,");
            bufSql.append(" FPortCuryRate,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Cash_SubTransfer"));
            bufSql.append(" WHERE FCheckState = 1) b ON a.FNum = b.FNum");
            bufSql.append(" LEFT JOIN (SELECT FCashAccCode, FCuryCode, FBankCode, FCashAccName,FSubAccType,FAccType");
            bufSql.append(
                " FROM " + pub.yssGetTableName("Tb_Para_CashAccount") + ") c ON b.FCashCode = c.FCashAccCode");
            bufSql.append(" LEFT JOIN (SELECT FTsfTypeCode, FTsfTypeName FROM Tb_Base_TransferType) e ON a.FTsfTypeCode =");
            bufSql.append(" e.FTsfTypeCode");
            bufSql.append(" LEFT JOIN (SELECT FSubTsfTypeCode, FSubTsfTypeName");
            bufSql.append(
                " FROM Tb_Base_SubTransferType) f ON a.FSubTsfTypeCode =");
            bufSql.append(" f.FSubTsfTypeCode");
            bufSql.append(" WHERE FTransferDate BETWEEN " +
                          dbl.sqlDate(this.dBeginDate) + " AND ");
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
