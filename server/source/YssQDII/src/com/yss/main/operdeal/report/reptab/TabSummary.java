package com.yss.main.operdeal.report.reptab;

import java.sql.*;
import java.util.*;

import com.yss.base.*;
import com.yss.main.dayfinish.OffAcctBean;
import com.yss.main.report.*;
import com.yss.util.*;

/**
 * <p>Title:Summary报表的临时表</p>
 *
 * <p>Description:获取各个之前生成的报表中的数据，加以处理获取相应的值</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TabSummary
    extends BaseAPOperValue {
    private java.util.Date dBeginDate;
    private java.util.Date dEndDate;
    private String portCode;

    private static final String specCashAccCode = ""; //用于获取期初的投资成本的特殊账户。
    private static final String specTsfTypeCode = ""; //用于获取期初的投资成本的特殊类型。

    private static final int Summary_PortCost_Equities = 0;
    private static final int Summary_PortCost_Debt = 1;
    private static final int Summary_PortCost_Cash = 2;

    //-------QDV4中保2009年07月20日01_B 增加类型设置 sj modified ------
    private static final int Summary_PortCost_DR = 32;
    private static final int Summary_PortCost_WT = 33;
    //--------------------------------------------------------------

    private static final int Summary_PortCost_HKDValue = 3; //投资成本港币等值

    private static final int Summary_Current_ActivitesIn = 4; //注资
    private static final int Summary_Current_ActivitesOut = 5;
    private static final int Summary_Current_ActivitesTotal = 18; //净额

    private static final int Summary_Last_Accumulated = 6; //前期滚存金额

    private static final int Summary_Expense = 7; //费用
    private static final int Summary_InterestAndIncome = 8;

    private static final int Summary_UnrealisedCapital = 9;
    private static final int Summary_UnrealisedCapital_MV = 10;
    private static final int Summary_UnrealisedCapital_FX = 11;

    private static final int Summary_RealisedCapital_Total = 12;
    private static final int Summary_RealisedCapital_MV = 20;
    private static final int Summary_RealisedCapital_FX = 21;

    private static final int Summary_UnsettleTrade_In = 13;
    private static final int Summary_UnsettleTrade_Out = 14;
    private static final int Summary_UnsettleTrade_Total = 19;

    private static final int Summary_Curr_Accumulated = 15; //当月滚存金额

    private static final int Summary_Net_First = 16; //第一个净值
    private static final int Summary_Net_Second = 17; //第二个净值
    //--- sj modified 200907077 MS00549 增加显示当月管理费 --------
    private static final int Summary_Net_Three = 31;//第3个净值
    //-----------------------------------------------------------

    private static final int Summary_Valution_Cash = 22;
    private static final int Summary_Valution_Debt = 23;
    private static final int Summary_Valution_Equities = 24;
    private static final int Summary_Valution_FixedDeposit = 25;
    private static final int Summary_Valution_DueToFromBroker = 26;
    private static final int Summary_Valution_DueToFrom = 27;
    private static final int Summary_Valution_Total = 28;
    //-------------------补充基金分类
    private static final int Summary_Valution_Funds = 29;


    //private static final String Summary_Tab_CashMovement = "Tb_Data_FundInOut";
    private static final String Summary_Tab_CashMovement = "Tb_Rep_FundInOut";
    //private static final String Summary_Tab_Untrade = "Tb_SummaryUntrade";
    private static final String Summary_Tab_Untrade = "Tb_rep_SummaryUntrade";
    
    private static final String Summary_Tab_Realised = "tb_Data_Realised";
    private static final String Summary_Tab_UnRealised = "Tb_Data_Unrealised";
   // private static final String Summary_Tab_ValutionDueToFromBroker =  "tb_temp_RecpayDetail"; 
    private static final String Summary_Tab_ValutionDueToFromBroker =  "tb_rep_RecpayDetail";

    //---- MS00432 QDV4中保2009年05月04日03_B add by sj-------------------------
    private boolean isCreate; //是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询
    //-------------------------------------------------------------------------

    //---- sj modified 20090707 MS00549 增加显示当月管理费------------------------
    private static final int Summary_ManageFee = 30;//管理费

   public String getPortCode() {
      return portCode;
   }

   public void setDEndDate(java.util.Date dEndDate) {
      this.dEndDate = dEndDate;
   }

   public void setPortCode(String portCode) {
      this.portCode = portCode;
   }

   public java.util.Date getDEndDate() {
      return dEndDate;
   }
//===================================================
    //-------------------------------------------------------------------------

    private class SummaryBean {
        String Code; //标示字段，用于排序
        String Name; //项目名
        String CatCode; //品种类型
        String SubCatCode; //品种子类型
        String CuryCode; //货币
        double Bal; //原币金额
        int SummaryType; //纪录类型
        double BaseCuryBal; //基础货币金额
        String portCode; //组合
        double Proportion = 0; //比例值
        String fattrclscode; //添加资本分类字段，历史数据为historical 20100818
        //添加制作人，制作时间，审核人，审核时间四个字段 20100819
        String creator;    //制作人
        String createtime; //制作时间
        String checkuser;  //审核人
        String checktime;  //审核时间

        java.sql.Date Date;

        public void SummaryBean() {
        }
    }

    public TabSummary() {
    }

    public void init(Object bean) throws YssException {
        String reqAry[] = null;
        String reqAry1[] = null;
        String sRowStr = (String) bean;
        if (sRowStr.trim().length() == 0) {
            return;
        }
        reqAry = sRowStr.split("\n");
        reqAry1 = reqAry[0].split("\r");
        this.dBeginDate = YssFun.toDate(reqAry1[1]);
        reqAry1 = reqAry[1].split("\r");
        this.dEndDate = YssFun.toDate(reqAry1[1]);
        reqAry1 = reqAry[2].split("\r");
        this.portCode = reqAry1[1];
        //--------MS00432 QDV4中保2009年05月04日03_B add by sj---------------
        reqAry1 = reqAry[3].split("\r"); //此参数为新加入，需更新报表配置
        if (reqAry1[1].equalsIgnoreCase("0")) { //若为0，则只查询已生成的报表数据
            this.isCreate = false;
        } else { //生成报表
            this.isCreate = true;
        }
        //------------------------------------------------------------------
    }

    public Object invokeOperMothed() throws YssException {
        HashMap valueMap = null;
        createTempSummary();
        try {
            valueMap = new HashMap();
            //---- MS00432 QDV4中保2009年05月04日03_B add by sj-------------
            if (this.isCreate) { //若为true,则生成.若为false，则不生成，只查询
	    		 //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
				 OffAcctBean offAcct = new OffAcctBean();
				 offAcct.setYssPub(this.pub);
				 String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
				 String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
				 if(!tmpInfo.trim().equalsIgnoreCase("")) {
					 return "<OFFACCT>" + tmpInfo;
				 }
				 //=================end=================
                processGetSummary(valueMap);
            }
            //------------------------------------------------------------
        } catch (YssException ex) {
            throw new YssException(ex.getMessage());
        }
        return "";
    }

    /**
     * 生成Summary数据的临时表
     * @throws YssException
     */
    private void createTempSummary() throws YssException {
        String strSql = "";
        try {
            if (dbl.yssTableExist(pub.yssGetTableName("tb_Data_Summary"))) {
                return;
            } else {
                strSql = "create table " +
                    pub.yssGetTableName("tb_Data_Summary") +
                    " (FCode varchar2(70) not null," +
                    " FName varchar2(50)," +
                    " FCatCode varchar2(100) not null," +
                    " FSubCatCode varchar2(100)," +
                    " FCuryCode varchar2(20) not null," +
                    " FBal number(18,4)," +
                    " FBaseCuryBal number(18,4)," +
                    " FPortCode varchar2(20)," +
                    " FProportion number(10,5)," +
                    " FDate Date not null," +
                    " FSummaryType number(2)," +
                    //创建summary数据临时表时添加fattrclscode字段 20100818
                    " fattrclscode varchar2(100)," +
                    //添加制作人，制作时间，审核人，审核时间 20100819
                    " fcreator varchar2(20) not null," +
                    " fcreatetime varchar2(20) not null," +
                    " fcheckuser varchar2(20)," +
                    " fchecktime varchar2(20))" ;
                dbl.executeSql(strSql);
            }
        } catch (Exception e) {
            throw new YssException("生成临时Summary表出错！");
        }
    }

    /**
     * 执行从各个报表获取数据的动作
     * @throws YssException
     */
    private void processGetSummary(HashMap valueMap) throws YssException {
        double PortCost = 0;
        double Accumulated = 0;
        double monthCost = 0;
        double netFirst = 0;
        if (null == valueMap) {
            throw new YssException("未实例化Map！");
        }
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //--- sj modified 20090707 第2个净值数据和管理费
        double netSecond = 0D;
        double manageFee = 0D;
        //------------------------------------
        try {
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
        }
        bTrans = true;

        if (!dbl.yssTableExist(pub.yssGetTableName("tb_Data_Summary"))) {
            throw new YssException("Summary表不存在！");
        }
        deleteSummary();
        PortCost = calcPrePortCost();
        monthCost = getCurMonthCost();
    //    calcPreAccumulated();
    //    getLastPreAccumulated();
    //    calcCurAccumulated();
    //    calcCurMonthActivites();
    //    calcUnrealisedCapital();
    //    calcRealisedCapital();
        Accumulated = calcCurAccumulated();
        netFirst = YssD.add(PortCost, monthCost, Accumulated);
        if (netFirst != 0) {
            SummaryBean summary = new SummaryBean();
            summary.Code = "NetFirst";
            summary.Name = "NetFirst";
            summary.CatCode = "Net";
            summary.SubCatCode = "First";
            summary.CuryCode = "HKD";
            summary.Bal = 0;
            summary.BaseCuryBal = netFirst;
            summary.portCode = this.portCode;
            summary.Proportion = 0;
            summary.Date = YssFun.toSqlDate(this.dEndDate);
            summary.SummaryType = Summary_Net_First;
            //添加fattrclscode字段 20100818
            summary.fattrclscode = " ";
            valueMap.put(summary.Code, summary);
            insertToTempSummary(valueMap);
        }

        getCurPortValue();
        calcUnsettleTrades();
        //----------------------------------------------------------------------
        netSecond = getSecondNet();//sj modified 20090707 need a return param
        manageFee = calcManageFee();//sj modified 20090707 管理费
        calcThreeNet(netSecond,manageFee);//最终的净值数据
        //----------------------------------------------------------------------
        try {
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (SQLException ex1) {
            throw new YssException(ex1.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 将数据封装放入HashMap中。
     * @param valueMap HashMap
     * @param rs ResultSet
     * @throws YssException
     */
    private void setResultValue(HashMap valueMap, ResultSet rs) throws
        YssException {
        if (null == valueMap) {
            throw new YssException("未实例化Map！");
        }
        if (null == rs) {
            return;
        }
        SummaryBean summary = null;
        try {
            summary = new SummaryBean();
            summary.Code = rs.getString("FCode");
            summary.Name = rs.getString("FName");
            summary.CatCode = rs.getString("FCatCode");
            summary.SubCatCode = rs.getString("FSubCatCode");
            summary.CuryCode = rs.getString("FCuryCode");
            summary.Bal = rs.getDouble("FBal");
            summary.BaseCuryBal = rs.getDouble("FBaseCuryBal");
            summary.Date = rs.getDate("FDate");
            summary.SummaryType = rs.getInt("FSummaryType");
            summary.portCode = rs.getString("FPortCode");
            summary.Proportion = rs.getShort("FProportion");
            summary.fattrclscode = rs.getString("Fattrclscode");
            valueMap.put(summary.Code, summary);
        } catch (SQLException ex) {
            throw new YssException(ex.getMessage());
        }
//      finally {
//         dbl.closeResultSetFinal(rs);
//      }
    }

    private void updateTempSummaryProportion(SummaryBean summary) throws
        YssException {
        PreparedStatement prst = null;
        String sqlStr = "update " +
            pub.yssGetTableName("tb_Data_Summary") +
            " set FProportion = ? where FSummaryType = ? and FDate = " +
            dbl.sqlDate(this.dEndDate);
        try {
            prst = dbl.openPreparedStatement(sqlStr);
            prst.setDouble(1, summary.Proportion);
            prst.setDouble(2, summary.SummaryType);
            prst.executeUpdate();
        } catch (SQLException ex) {
        } finally {
            dbl.closeStatementFinal(prst);
        }
    }

    /**
     * 将数据插入数据库
     * @param valueMap HashMap
     * @throws YssException
     */
    private void insertToTempSummary(HashMap valueMap) throws YssException {
        if (null == valueMap || valueMap.isEmpty()) {
            return;
        }
        SummaryBean summary = null;
        Object object = null;
        PreparedStatement prst = null;
        String aStr = "";
        String sqlStr = "insert into " +
            pub.yssGetTableName("tb_Data_Summary") +
            "(FCode,FName,FCatCode,FSubCatCode,FCuryCode,FBal,FBaseCuryBal,FPortCode,FProportion,FDate,FSummaryType,Fattrclscode,FCreator,FCreatetime,FCheckuser,FChecktime)" +
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//添加了fattrclscode,制作人，制作时间，审核人，审核时间等字段 20100819
        try {
            prst = dbl.openPreparedStatement(sqlStr);
            Iterator it = valueMap.keySet().iterator();
            while (it.hasNext()) {
                summary = (com.yss.main.operdeal.report.reptab.TabSummary.
                           SummaryBean) valueMap.get( (String) it.next());
                //将hash表中数据存入到数据库表中时将fcode字段上辅助信息去掉，因为只有现估值日投资组合部分fcode字段前添加了辅助信息，所以使用判断条件处理
                aStr=summary.Code.substring(0,2);
                if(aStr.equals(summary.fattrclscode)){
                	aStr = summary.Code.substring(2);
                	prst.setString(1, aStr);
                }else{
                prst.setString(1, summary.Code);
                }
                //prst.setString(1, summary.Code);
                prst.setString(2, summary.Name);
                prst.setString(3, summary.CatCode);
                prst.setString(4, summary.SubCatCode);
                prst.setString(5, summary.CuryCode);
                prst.setDouble(6, YssFun.roundIt(summary.Bal, 4));
                prst.setDouble(7, YssFun.roundIt(summary.BaseCuryBal, 4));
                prst.setString(8, summary.portCode);
                prst.setDouble(9, summary.Proportion);
                prst.setDate(10, summary.Date);
                prst.setInt(11, summary.SummaryType);
                //添加fattrclscode字段 20100818
                prst.setString(12,summary.fattrclscode);
                //新增制作人，制作时间，审核人，审核时间信息  20100819
                prst.setString(13, pub.getUserCode());
                prst.setString(14, YssFun.formatDatetime(new java.util.Date()));
                prst.setString(15, pub.getUserCode());
                prst.setString(16, YssFun.formatDatetime(new java.util.Date()));
                prst.executeUpdate();
            }
        } catch (YssException ex) {
            throw new YssException("insert error", ex);
        } catch (SQLException ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeStatementFinal(prst);
        }
    }

    /**
     * 从Summary表中按要求删除相关数据
     * @throws YssException
     */
    private void deleteSummary() throws YssException {
        String sqlStr = "Delete from " +
            pub.yssGetTableName("tb_Data_Summary") +
            " where FDate = " + dbl.sqlDate(this.dEndDate) + " and Fportcode ='" + portCode + "'";
        try {
            dbl.executeSql(sqlStr);
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }

    }

    /**
     * 当月资产变动。
     * @return double
     * @throws YssException
     */
    private double getCurMonthCost() throws YssException {
        HashMap valueMap = null;
        double monthCost = 0;
        String sqlStr = "";
        ResultSet rs = null;
        //------MS00491 QDV4中保2009年06月09日01_B sj
        TabFundInOut inOut = new TabFundInOut(); //生成报表实例
        inOut.setYssPub(pub);
        String reportParams = "1\r" + YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "\rnull\n2\r" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd") + "\rnull\n" +
            "3\r" + this.portCode + "\rnull"; //报表参数";
        inOut.init(reportParams);
        inOut.invokeOperMothed();
        //------------------------------------------------------------------------
        //if (!dbl.yssTableExist(this.Summary_Tab_CashMovement)) {
        if (!dbl.yssTableExist(pub.yssGetTableName(TabSummary.Summary_Tab_CashMovement))) {
            throw new YssException("请先完成CashMovement报表！");
        }
        //sqlStr = "select * from " + Summary_Tab_CashMovement +
          sqlStr = "select * from " +pub.yssGetTableName(TabSummary.Summary_Tab_CashMovement)+
            " where FTransDate between " +
            dbl.sqlDate(this.dBeginDate) +
            " and " + dbl.sqlDate(this.dEndDate);
        try {
            rs = dbl.openResultSet(sqlStr);
            if (!rs.next()) {
                throw new YssException("请先完成当月CashMovement报表！");
            }
        } catch (SQLException ex1) {
            throw new YssException("请先完成CashMovement报表！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        //------------------------------------------------------------------------
        sqlStr = "select case when FInOut = -1 then 'Amount Withdrawn' " +
            " else 'Fund Received' end" +
            " as FCode," +
            " case when FInOut = -1 then '提取款項' " +
            " else '注資款項' end" +
            " as FName,CashMovement.FInOut,CashMovement.FPortAccBalance*CashMovement.FInOut as FPortAccBalance,CashMovement.FExRate from " +
            " (select -1 as FInOut,sum(FOutFlowHKD) as FPortAccBalance,avg(FExRate) as FExRate from  " +
            pub.yssGetTableName(Summary_Tab_CashMovement) +
            " where FTransDate between " + dbl.sqlDate(this.dBeginDate) +
            " and " + dbl.sqlDate(this.dEndDate) + " and FOutFlowHKD > 0" +
            //----苏程辉---2009年10月20日  加入 fportcode
            " and fportcode ="+ dbl.sqlString(this.portCode) +
            "  union all " +
            " select 1 as FInOut," + dbl.sqlIsNull("sum(FInFlowHKD)", "0") +
            " as FPortAccBalance," + dbl.sqlIsNull("avg(FExRate)", "0") +
            " as FExRate from  " + pub.yssGetTableName(Summary_Tab_CashMovement)  +
            " where FTransDate between " + dbl.sqlDate(this.dBeginDate) +
            " and " + dbl.sqlDate(this.dEndDate) +
            " and FInFlowHKD > 0  and  " +
            //----苏程辉---2009年10月20日 加入 fportcode
            " fportcode ="+ dbl.sqlString(this.portCode)+ ") cashmovement";
        try {
            valueMap = new HashMap();
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                monthCost += rs.getDouble("FPortAccBalance");
                if (rs.getDouble("FInOut") == -1) {
                    SummaryBean summary = new SummaryBean();
                    summary.Code = rs.getString("FCode");
                    summary.Name = rs.getString("FName");
                    summary.CatCode = "CashMovement";
                    summary.SubCatCode = "CashMovementOut";
                    summary.CuryCode = "HKD";
                    summary.Bal = 0;
                    summary.BaseCuryBal = rs.getDouble("FPortAccBalance");
                    summary.portCode = this.portCode;
                    summary.Proportion = 0;
                    summary.Date = YssFun.toSqlDate(this.dEndDate);
                    summary.SummaryType = Summary_Current_ActivitesOut;
                    //添加fattrclscode字段 20100818
                    summary.fattrclscode = " ";
                    valueMap.put(summary.Code, summary);
                } else if (rs.getDouble("FInOut") == 1) {
                    SummaryBean summary = new SummaryBean();
                    summary.Code = rs.getString("FCode");
                    summary.Name = rs.getString("FName");
                    summary.CatCode = "CashMovement";
                    summary.SubCatCode = "CashMovementIn";
                    summary.CuryCode = "HKD";
                    summary.Bal = 0;
                    summary.BaseCuryBal = rs.getDouble("FPortAccBalance");
                    summary.portCode = this.portCode;
                    summary.Proportion = 0;
                    summary.Date = YssFun.toSqlDate(this.dEndDate);
                    summary.SummaryType = Summary_Current_ActivitesIn;
                    //添加fattrclscode字段 20100818
                    summary.fattrclscode = " ";
                    valueMap.put(summary.Code, summary);
                }
            }
            if (monthCost != 0) {
                SummaryBean summary = new SummaryBean();
                summary.Code = "CashMovememt";
                summary.Name = "CashMovememt";
                summary.CatCode = "CashMovement";
                summary.SubCatCode = "CashMovementTotal";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = monthCost;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = Summary_Current_ActivitesTotal;
                //添加fattrclscode字段 20100818
                summary.fattrclscode = " ";
                valueMap.put(summary.Code, summary);
            }

            insertToTempSummary(valueMap);
        } catch (SQLException ex) {
            throw new YssException("获取当月CashMovememt数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return monthCost;
    }

    /**
     * 获取期初的投资成本
     * @throws YssException
     */
    private double getBeginPrePortCost(HashMap valueMap) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double prePortCost = 0;
        //----------------------------------------------------------------------------
        String sqlStr = "Delete from " +
            pub.yssGetTableName("tb_Data_Summary") +
            " where FSummaryType in (" + Summary_PortCost_Debt + "," +
            Summary_PortCost_Cash + "," + Summary_PortCost_Equities +
            "," + Summary_PortCost_HKDValue +","+ Summary_PortCost_WT +","+
	    Summary_PortCost_DR +
	    //获取期初数据时其时间字段设置为月初，故每次生成时先删除该记录  版本合并（合并邵宏伟修改代码）
            ") and FDate = " + dbl.sqlDate(dBeginDate) +
            " and Fportcode='" + portCode + "'";
        try {
            dbl.executeSql(sqlStr);
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        //-----------------------------------------------------------------------------
        strSql = "select " + "FCuryCode" + dbl.sqlJoinString() + "FAnalysisCode2" +
            " as FCode," +
//            "FCuryCode" + dbl.sqlJoinString() + "FAnalysisCode2" +
            " case when FAnalysisCode2 = 'EQ' then 'Equities'" +
            " when FAnalysisCode2 = 'FI' then 'Debt Securities'" +
            " when FAnalysisCode2 = 'DE' then 'Money Market'" +
          //---苏程辉2009年10月29日 把等号改成 in ， 加入TR。
            " when FAnalysisCode2 IN ('DR','TR') then 'Derivatives'" +
            " when FAnalysisCode2 = 'WT' then 'Warrant'" +
            " end as FName," +
            dbl.sqlString(this.portCode) + " as FPortCode,0 as FProportion," +
//            dbl.sqlDate(this.dEndDate) +
            //获取期初投资成本，将存储日期改为月初  版本合并（合并邵宏伟修改代码）
             dbl.sqlDate(dBeginDate) +
            " as FDate," +
            " case when FAnalysisCode2 = 'EQ' then " +
            this.Summary_PortCost_Equities +
            " when FAnalysisCode2 = 'FI' then " + this.Summary_PortCost_Debt +
            " when FAnalysisCode2 = 'DE' then " + this.Summary_PortCost_Cash +
            " when FAnalysisCode2 = 'WT' then " + this.Summary_PortCost_WT +
            //---苏程辉2009年10月29日 把等号改成 in ， 加入TR。
            " when FAnalysisCode2 in ('DR','TR')  then " + this.Summary_PortCost_DR +
            " end as FSummaryType," +
            //添加fattrclscode字段 20100818
            " '' as fattrclscode,FCuryCode,'PortCost' as FCatCode,FAnalysisCode2 as FSubCatCode,sum(FAccBalance) as FBal,sum(FBaseCuryBal) as FBaseCuryBal,sum(FPortCuryBal) as FPortCuryBal from (" +
            "select CashStorage.* from (select * " +
            " from " + pub.yssGetTableName("tb_stock_cash") +
            " where FCheckState = 1 and FPortCode = " +
            dbl.sqlString(this.portCode) + " and FYearMonth = " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyy") + "00") +
            " ) CashStorage " +
            " join (select * from " + pub.yssGetTableName("tb_para_cashaccount") +
            " where FCheckState = 1 and FPortCode = " +
            dbl.sqlString(this.portCode) +
            //苏程辉 2009-12-17 虚拟账户有0411 改为0415 :一月份前估值日投资成本差一分钱问题
            " and FAccType = '04'  and FSubAccType = '0415')" + //04为虚拟账户类型,0415为前估值日投资成本
            " cashCount on CashStorage.FCashAccCode = cashCount.FCashAccCode" +
            " ) group by FCuryCode,FAnalysisCode2";
        try {
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                prePortCost += rs.getDouble("FBaseCuryBal");
                setResultValue(valueMap, rs);
            }
        } catch (SQLException ex) {
            throw new YssException("获取期初投资成本出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return prePortCost;
    }

    /**
     * 获取上期的投资成本
     * @param valueMap HashMap
     * @return double
     * @throws YssException
     */
    private double getLastPrePortCost(HashMap valueMap) throws YssException {
        double lastPrePortCost = 0;
        String strSql = "";
        ResultSet rs = null;
//删除本月末结余数据，本月结余数据存放月末  版本合并（合并邵宏伟修改代码）
        String sqlStr = "Delete from " +
            pub.yssGetTableName("tb_Data_Summary") +
            " where FSummaryType = " + Summary_PortCost_HKDValue +
            " and FDate = " + dbl.sqlDate(dEndDate) +
            " and Fportcode='" + portCode + "'";
        try {
            dbl.executeSql(sqlStr);
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        //------------------------------------------------------------------------------------------------------
//       strSql = "select FCode,FName,FCatCode,FSubCatCode,FCuryCode,FBal,FBaseCuryBal,FPortCode,FProportion," +
//           dbl.sqlDate(this.dEndDate) + " as FDate,FSummaryType " + 
//            " from " + pub.yssGetTableName("tb_Data_Summary") +     
//            " where FSummaryType in (" + this.Summary_PortCost_Cash + "," + 
            //--苏程辉 2009年10月20日 加入 this.Summary_PortCost_DR 和this.Summary_PortCost_WT
//            this.Summary_PortCost_Debt + ","+this.Summary_PortCost_DR+","+this.Summary_PortCost_WT+"," + this.Summary_PortCost_Equities +
//            ") and FDate = add_months(" + dbl.sqlDate(dEndDate) +
//            ",-1) and Fportcode='" + portCode + "' and FBaseCuryBal <> 0";
       //-------------------------------------------------------------------------------------------------------
       //获得当月末时投资成本结余,前提是当月资金注入注出表已生成 20100715   版本合并（合并邵宏伟修改代码）
       strSql = "select 'HKD' FCode, '' FName, 'PortCost' FCatCode, '' FSubCatCode, 'HKD' FCuryCode, 0 FBal, sum(fportaccbalance) as FBaseCuryBal, '' FPortCode, '' FProportion, " +
               	dbl.sqlDate(this.dEndDate) + " as FDate, '' FSummaryType, '' fattrclscode " +
              	" from (select fdesc, finflow, finflowhkd, foutflow, foutflowhkd, " +
                " case when fdesc like '流出%' then foutflowhkd * -1 " +
                " when fdesc like '流入%' then finflowhkd " +
                " else fportaccbalance end as fportaccbalance " +
                " from  " +pub.yssGetTableName(Summary_Tab_CashMovement) +" a"+
                " where forder like to_char(" + dbl.sqlDate(dEndDate) + ", 'yyyymm')" + " || '%'"  +
                " and a.fportcode = " + dbl.sqlString(this.portCode) +
                " and a.forder not like '%11') ";

        try {
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                lastPrePortCost += rs.getDouble("FBaseCuryBal");
                setResultValue(valueMap, rs);
            }
        } catch (SQLException ex) {
            throw new YssException("获取上一估值日投资成本出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return lastPrePortCost;
    }

    /**
     * 获取当期的投资
     * @param valueMap HashMap
     * @throws YssException
     */
    private void getCurrPortCost(HashMap valueMap) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        SummaryBean summary = null;
        strSql = "select " +
            " cashAccount.FCuryCode " + dbl.sqlJoinString() +
            " inout.FSubCatCode as FCode," +
//            " cashAccount.FCuryCode " + dbl.sqlJoinString() +
//            " inout.FSubCatCode as FName," +
            " case when FSubCatCode = 'EQ' then 'Equities'" +
            " when FSubCatCode = 'FI' then 'Debt Securities'" +
            " when FSubCatCode = 'DE' then 'Money Market'" +
            //--------QDV4中保2009年07月20日01_B sj modifid ----
            " when FSubCatCode = 'WT' then 'Warrant'" +
            " when FSubCatCode in ('DR','TR') then 'Derivatives'" +
            //-------------------------------------------------
            " end as FName," +
            "inout.FPortCode,inout.FInOut,inout.FAnalysisCode1," +
            "'PortCost' as FCatCode," +
            "inout.FSubCatCode,inout.FAnalysisCode3," +
            //---- QDV4中保2009年07月20日01_B sj modified -----------------------//
//            "inout.FBal," +
            "sum(FMoney) over(partition by FNum,FSubCatCode,cashAccount.FCuryCode,FAnalysisCode1) as FBal," + //调整数据的计算至此，并加以汇总计算，增加对币种，分析代码的区分
            " FBaseCuryRate,inout.FPortCuryRate,FSummaryType" +
            //------------------------------------------------------------------//
            ",cashAccount.FCuryCode from (" +
            //------------------QDV4中保2009年07月20日01_B  sj modified ------------------------------------------------
            "select FNum,FInOut,FPortCode,FAnalysisCode1,FAnalysisCode2 as FSubCatCode,FAnalysisCode3,FCashAccCode," +
//            " sum(FMoney) over(partition by FNum) as FBal" +
            "FMoney" +//调整金额的计算，此处只是获取金额。将金额的汇总调整到外层
            ",FBaseCuryRate,FPortCuryRate, " +
            //---------------------------------------------------------------------------------------------
            " case when FAnalysisCode2 = 'EQ' then " +
            Summary_PortCost_Equities +
            " when FAnalysisCode2  = 'FI' then " + Summary_PortCost_Debt +
            " when FAnalysisCode2  = 'DE' then " + Summary_PortCost_Cash +
            //---- QDV4中保2009年07月20日01_B sj modified 20090721 ----
            " when FAnalysisCode2 = 'WT' then " + this.Summary_PortCost_WT + //添加了对WT类型的标识
            " when FAnalysisCode2 in ('DR','TR') then " + this.Summary_PortCost_DR + //添加了对DR类型的标识
            //----------------------------------------------------------------------------------
            " end as FSummaryType " +
            " from " +
            " (select sub.FNum as FNum,sub.FInOut as FInOut,sub.FPortCode as FPortCode,sub.FAnalysisCode1 as FAnalysisCode1,sub.FAnalysisCode2 as FAnalysisCode2," +
            "  sub.FAnalysisCode3 as FAnalysisCode3,sub.FCashAccCode as FCashAccCode,sub.FInOut * sub.FMoney as FMoney,sub.FBaseCuryRate as FBaseCuryRate ," +
            " sub.FPortCuryRate as FPortCuryRate from (select * from " +
            pub.yssGetTableName("tb_cash_transfer") +
            " where FTsfTypeCode = '04'" +
            " and FTransferDate between " + dbl.sqlDate(this.dBeginDate) +
            " and " +
            dbl.sqlDate(this.dEndDate) + ") tran " +
            " join (select * from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
            " where FPortCode = " + dbl.sqlString(this.portCode) +
            ") sub on tran.FNum = sub.FNum)) inout" +
            " join (select * from " + pub.yssGetTableName("tb_para_cashaccount") + //modify by sunkey 20090313 BugNO:MS00306 将组合群号为前缀的表修改为自动生成，而非写死的001
            " where FCheckState = 1) cashAccount on inout.FCashAccCode = cashAccount.FCashAccCode" ;
            //----苏程辉 2009年11月2日 ，以估值日汇率挂账 ----严重错误，要以业务日期汇率挂账
            //"  LEFT JOIN ( select r.FCURYCODE,r.fportcode ,r.FEXRATE1  from (SELECT s.FCURYCODE,s.fportcode, max(s.fvaldate) as FEXRATEDATE  "+
           // "  FROM  " + pub.yssGetTableName("tb_data_valrate") + "  s where s.fvaldate <=  "+dbl.sqlDate(this.dEndDate)+"  group by s.FCURYCODE,s.fportcode) t "+
            //"  left join (select g.FCURYCODE, g.fvaldate,g.fportcode,CASE WHEN g.FCURYCODE ='JPY' THEN g.fbaserate/100 ELSE g.fbaserate END FEXRATE1   from  " + pub.yssGetTableName("Tb_Data_valRate") + "  g) r on t.FCURYCODE = r.FCURYCODE "+
            //"  and t.fportcode=r.fportcode and t.FEXRATEDATE =  r.fvaldate ) rate on rate.FCURYCODE=cashAccount.Fcurycode and rate.fportcode=inout.fportcode";

        try {
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (valueMap.containsKey(rs.getString("FCuryCode") +
                                         rs.getString("FSubCatCode"))) {

                    summary = (com.yss.main.operdeal.report.reptab.TabSummary.
                               SummaryBean) valueMap.get(rs.getString("FCuryCode") +
                        rs.getString("FSubCatCode"));
                    summary.Bal = YssD.add(summary.Bal, rs.getDouble("FBal"));
                    summary.BaseCuryBal = YssD.add(summary.BaseCuryBal,
                        YssD.mul(rs.getDouble("FBal"),
                                 rs.getDouble("FBaseCuryRate")));
                    summary.Date = YssFun.toSqlDate(this.dEndDate);
                    valueMap.put(summary.Code, summary);
                } else {
                    summary = new SummaryBean();
                    summary.Code = rs.getString("FCode");
                    summary.Name = rs.getString("FName");
                    summary.CatCode = rs.getString("FCatCode");
                    summary.SubCatCode = rs.getString("FSubCatCode");
                    summary.CuryCode = rs.getString("FCuryCode");
                    summary.Bal = rs.getDouble("FBal");
                    summary.BaseCuryBal = YssD.mul(summary.Bal,
                        rs.getDouble("FBaseCuryRate"));
                    summary.portCode = rs.getString("FPortCode");
                    summary.Proportion = 0;
                    summary.Date = YssFun.toSqlDate(this.dEndDate);
                    summary.SummaryType = rs.getInt("FSummaryType");
                    //添加fattrclscode字段 20100818
                    summary.fattrclscode = " ";
                    valueMap.put(summary.Code, summary);
                }
            }
            Iterator it = valueMap.keySet().iterator();
            while (it.hasNext()) {
                summary = (com.yss.main.operdeal.report.reptab.TabSummary.
                           SummaryBean) valueMap.get( (String) it.next());
                summary.Date = YssFun.toSqlDate(this.dEndDate);
            }
        } catch (SQLException ex) {
            throw new YssException("获取当月的资本流动出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 前估值日投资成本，期初的市值加上其后的当月资本金的注入/提取
     * @throws YssException
     */
    private double calcPrePortCost() throws YssException {
        HashMap valueMap = new HashMap();
        double prePortCost = 0;
        if (YssFun.dateDiff(this.dEndDate,
                            YssFun.parseDate(YssFun.formatDate(this.dEndDate,
            "yyyy") + "-01-31")) == 0) { //若为1月份。
            prePortCost = getBeginPrePortCost(valueMap);
            insertToTempSummary(valueMap);
            // 插入生成期初数 
            //更改判断条件，考虑期初数为负数的情况	  版本合并（合并邵宏伟修改代码）
           if (prePortCost != 0) {
                SummaryBean summary = new SummaryBean();
                summary.Code = "PrePortCost";
                summary.Name = "PrePortCostHKD";
                summary.CatCode = "PrePortCost";
                summary.SubCatCode = "PrePortCostHKD";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = prePortCost;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                //本月末投资成本结余 改-1为0 this.dBenginDate改为this.dEndDate
                summary.Date = YssFun.toSqlDate(YssFun.addDay(this.dBeginDate, 0));
                summary.SummaryType = Summary_PortCost_HKDValue;
                //添加fattrclscode字段 20100818
                summary.fattrclscode = " ";
                valueMap.put(summary.Code, summary);
                insertToTempSummary(valueMap);
            }
           valueMap.clear();
            //获取一月月末结余
            prePortCost = getLastPrePortCost(valueMap);
        } else {
            prePortCost = getLastPrePortCost(valueMap);
        }
        getCurrPortCost(valueMap);
        insertToTempSummary(valueMap);
        valueMap.clear();
        //更改判断条件，考虑期初数为负数的情况  版本合并（合并邵宏伟修改代码）
        if (prePortCost != 0) {
            SummaryBean summary = new SummaryBean();
            summary.Code = "PrePortCost";
            summary.Name = "PrePortCostHKD";
            summary.CatCode = "PrePortCost";
            summary.SubCatCode = "PrePortCostHKD";
            summary.CuryCode = "HKD";
            summary.Bal = 0;
            summary.BaseCuryBal = prePortCost;
            summary.portCode = this.portCode;
            summary.Proportion = 0;
            //本月末投资成本结余 改-1为0 this.dBenginDate改为this.dEndDate
            summary.Date = YssFun.toSqlDate(YssFun.addDay(this.dEndDate, 0));
            summary.SummaryType = Summary_PortCost_HKDValue;
            //添加fattrclscode字段 20100818
            summary.fattrclscode = " ";
            valueMap.put(summary.Code, summary);
            insertToTempSummary(valueMap);
        }
        
  // 返回前估值日投资成本
        double lastprePortCost = 0;
        ResultSet rs = null;
        String sqlStr = "select FBaseCuryBal from " + pub.yssGetTableName("tb_Data_Summary") + 
      " where fsummarytype = " + Summary_PortCost_HKDValue + 
      " and FDate = (select case when to_char(" + dbl.sqlDate(this.dBeginDate) + 
      " , 'mm') = '01' then " + dbl.sqlDate(this.dBeginDate) + 
      " else " + dbl.sqlDate(this.dBeginDate) + 
      " - 1 end as fdate from dual) " +  
      " and FPortCode = '" + this.portCode + "'";
    try{
          rs = dbl.openResultSet(sqlStr);
    while(rs.next()) {
    	lastprePortCost += rs.getDouble("FBaseCuryBal"); 
    } 
       }catch(SQLException ex ){
           throw new YssException ("获取前估值日投资成本出错。");
       } finally {
      dbl.closeResultSetFinal(rs);
    }
        return lastprePortCost;
    }

    /**
     * 获取前期滚存金额的上期数
     * @return double
     * @throws YssException
     */
    private double getLastPreAccumulated() throws YssException {
        double reVal = 0;
        String sqlStr = "";
        ResultSet rs = null;
        SummaryBean summary = null;
        HashMap valueMap = new HashMap();
        sqlStr = "select * from " + pub.yssGetTableName("tb_Data_Summary") +
            " where FDate = add_months(" + dbl.sqlDate(dEndDate) + ",-1) " +

            " and FSummaryType = " + Summary_Curr_Accumulated + " and Fportcode='" + portCode + "'";
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                reVal = rs.getDouble("FBaseCuryBal");
                summary = new SummaryBean();
                summary.Code = "LastAccumulated";
                summary.Name = "LastAccumulated";
                summary.CatCode = "Accumulated";
                summary.SubCatCode = "LastAccumulated";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = reVal;
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = Summary_Last_Accumulated;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                //添加fattrclscode字段 20100818
                summary.fattrclscode = " ";
                valueMap.put(summary.Code, summary);
            } else if (YssFun.getMonth(this.dBeginDate) == 1 &&
                       YssFun.getDay(this.dBeginDate) == 1) {
//                sqlStr = "select FCuryCode,FAccBalance,FBaseCuryBal,FPortCuryBal from (select CashStorage.* from " +
//20100331改
               sqlStr = "select  sum(FPortCuryBal) as FPortCuryBal from (select CashStorage.* from " +
                    " (select * from " + pub.yssGetTableName("Tb_Stock_Cash") +
                    " where FCheckState = 1 and FPortCode = " +
                    dbl.sqlString(this.portCode) + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyy") + "00") +
                    ") CashStorage" +
                    " join (select * from " +
                    pub.yssGetTableName("Tb_Para_cashaccount") +
                    " where FCheckState = 1 " +
                    " and FPortCode = " + dbl.sqlString(this.portCode) + " and FAccType = '04' and FSubAccType = '0412') cashCount on CashStorage.FCashAccCode = cashCount.FCashAccCode)";
                rs = dbl.openResultSet(sqlStr);
                if (rs.next()) {
                    reVal = rs.getDouble("FPortCuryBal");
                } else {
                    throw new YssException("請設置基準前期滾存金額的數據！");
                }
                summary = new SummaryBean();
                summary.Code = "LastAccumulated";
                summary.Name = "LastAccumulated";
                summary.CatCode = "Accumulated";
                summary.SubCatCode = "LastAccumulated";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = reVal;
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = Summary_Last_Accumulated;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                //添加fattrclscode字段 20100818
                summary.fattrclscode = " ";
                valueMap.put(summary.Code, summary);
            }
            insertToTempSummary(valueMap);
        } catch (SQLException ex) {
            throw new YssException("获取前期滚存金额的上期数出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return reVal;
    }

    /**
     * 当期滚存金额。
     * @throws YssException
     */
    private double calcCurAccumulated() throws YssException {
        double curAccumulated = 0;
        double preAccumulated = 0;
        double expense = 0;
        double InterestAndIncome = 0;
        double UnrealisedCapital = 0;
        double RealisedCpatial = 0;
        preAccumulated = getLastPreAccumulated();
        expense = calcExpense();
        InterestAndIncome = calcInterestAndIncome();
        UnrealisedCapital = calcUnrealisedCapital();
        RealisedCpatial = calcRealisedCapital();
        curAccumulated = YssD.add(preAccumulated, expense, InterestAndIncome,
                                  UnrealisedCapital, RealisedCpatial);
        HashMap valueMap = new HashMap();
        SummaryBean summary = new SummaryBean();
        summary.Code = "CurrAccumulated";
        summary.Name = "CurrAccumulated";
        summary.CatCode = "Accumulated";
        summary.SubCatCode = "CurrAccumulated";
        summary.CuryCode = "HKD";
        summary.Bal = 0;
        summary.BaseCuryBal = curAccumulated;
        summary.Date = YssFun.toSqlDate(this.dEndDate);
        summary.SummaryType = Summary_Curr_Accumulated;
        summary.portCode = this.portCode;
        summary.Proportion = 0;
        //添加fattrclscode字段 20100818
        summary.fattrclscode = " ";
        valueMap.put(summary.Code, summary);
        insertToTempSummary(valueMap);
        return curAccumulated;
    }

    /**
     * 本月交易事项
     * @throws YssException
     */
    private void calcCurMonthActivites() throws YssException {
        calcExpense();
        calcInterestAndIncome();
    }

    /**
     * 费用
     * @throws YssException
     */
    private double calcExpense() throws YssException {
        double expense = 0;
        String sqlStr = "";
        ResultSet rs = null;

sqlStr = " select sum(FBaseMoney) as fexpense from ( "
      + "select a.FIVPAYCATCODE, a.fivpaycatname,  FMoney,a.fcurycode, FBaseCuryRate,round(FMoney * fbasecuryrate,2) as FBaseMoney ,A.FPORTCODE "
      //数据库升级9208报错未明确定义列，FSubTsfTypeCode前添加a2限定
      + " from (select a2.FSubTsfTypeCode as FIvPayCatCode, FSubTsfTypeName as FIvPayCatName, sum(FMoney *-1 * a1.finout) as FMoney, FCuryCode,Fportcode, a1.fbasecuryrate"
      + " from " + pub.yssGetTableName("Tb_Cash_SubTransfer") + " a1 "
      + " join (select FNum, FSubTsfTypeCode "
      + " from " + pub.yssGetTableName("Tb_Cash_Transfer") + " where FCheckState = 1 "  /*手工数据*/
      + " and FDataSource = 0 and FTsfTypeCode = '03' and FTransferDate between "
      +  dbl.sqlDate(this.dBeginDate) + "  and " + dbl.sqlDate(this.dEndDate) + ") a2 on a1.FNum =   a2.FNum "
      + " left join (select FCashAccCode, FCuryCode from " + pub.yssGetTableName("tb_para_cashaccount")
      + " where FCheckState = 1) a3 on a1.FCashAccCode = a3.FCashAccCode "
      + " left join (select FSubTsfTypeCode, FSubTsfTypeName from Tb_Base_SubTransferType where FCheckState = 1) a4 "
      + " on a2.FSubTsfTypeCode = a4.FSubTsfTypeCode where FCheckState = 1 "
      + " and FPortCode = " + dbl.sqlString(this.portCode)
      //数据库升级9208报错未明确定义列，调整更改
      + " group by a2.FSubTsfTypeCode, FSubTsfTypeName, FCuryCode, Fportcode,fbasecuryrate "
      + " union all "
      + " select ivp.FIvPayCatCode as FIvPayCatCode,invest.FIvPayCatName as FIvPayCatName,"
      + " ivp.FMoney as FMoney,ivp.FCuryCode as FCuryCode,ivp.Fportcode, fbasecuryrate "
      + " from (select FIvpaycatCode,FCuryCode,sum(FMoney) as FMoney,Fportcode, fbasecuryrate "
      + " from " + pub.yssGetTableName("tb_data_investpayrec") + " where FCheckState = 1 "
      + " and FTsfTypeCode in ('07','98') and FPortCode = " + dbl.sqlString(this.portCode)
      + " and FTransDate between " + dbl.sqlDate(this.dBeginDate) + "  and " + dbl.sqlDate(this.dEndDate)
      + " group by FIvpaycatcode, FCuryCode, Fportcode,fbasecuryrate) ivp "
      + " left join (select FivPayCatCode, FIvPayCatName from tb_base_investpaycat "
      + " where FCheckState = 1) invest on ivp.FIvPayCatCode = invest.FIvPayCatCode) a "
      + ")";


        try {
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                expense += rs.getDouble("fexpense");
            }
            HashMap valueMap = new HashMap();
            SummaryBean summary = new SummaryBean();
            summary.Code = "Expenses";
            summary.Name = "費用";
            summary.CatCode = "Activities";
            summary.SubCatCode = "ActivitiesExpense";
            summary.CuryCode = "HKD";
            summary.Bal = 0;
            expense = YssD.mul(expense, -1);
            summary.BaseCuryBal = expense;
            summary.Date = YssFun.toSqlDate(this.dEndDate);
            summary.SummaryType = Summary_Expense;
            summary.portCode = this.portCode;
            summary.Proportion = 0;
            //添加fattrclscode字段 20100818
            summary.fattrclscode = " ";
            valueMap.put(summary.Code, summary);
            insertToTempSummary(valueMap);
        } catch (SQLException ex) {
            throw new YssException("获取费用数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return expense;
    }

    /**
     * 利息/收入
     * @throws YssException
     */
    private double calcInterestAndIncome() throws YssException {
        double InterestAndIncome = 0;
        String sqlStr = "";
        ResultSet rs = null;

        sqlStr = "select sum(FAllMoney) as FAllMoney from "
               + "	(select FCaption,FAllMoney from ( "
               + "select '1' as FOrder,'INCOME CASH RECEIVED (現金收入－利息)' as FCaption,NVL(sum(FPortBal),0) as FAllMoney from"
               + "(  select sum(FPortBal) as FPortBal from ( "
               + " select a1.FCashAccCode, a2.FCashAccName, sum(FMoney) as FBal,"
               + " sum(round(FMoney * FBaseCuryRate,2)) as FPortBal, a2.FCuryCode "
               + " from (select FCashaccCode, FInOut * FMoney as FMoney, FBaseCuryRate "
               + " from " + pub.yssGetTableName("tb_cash_subtransfer") + " a11 "
               + " join " + pub.yssGetTableName("tb_cash_transfer") + " a12 on a11.FNum = a12.FNum "
               + " where a12.FTransferDate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + " "
               + " and a11.FPortCode =  " + dbl.sqlString(this.portCode)
               + " and a12.FTsfTypeCode = '02'"
               //利息收入中添加02SI调拨子类型 20100819
               + " and a12.FSubTsfTypeCode in ('02DE', '02SI')"
               + " and a12.FCheckState = 1) a1"
               + " join (select FCashAccCode, FCashAccName, FCuryCode"
               + " from " + pub.yssGetTableName("tb_para_cashaccount")
               + " where FCheckState = 1) a2 on a1.FCashAccCode = a2.FCashAccCode"
               + " group by a2.FCuryCode, a1.FCashAccCode, a2.FCashAccName "
               + " having sum(FMoney) <> 0"
               + " order by a2.FCuryCode, a1.FCashAccCode))"
               + " union"
               + " select '2' as FOrder,'INCOME CASH DIVIDEND (現金收入－股息)' as FCaption,NVL(FPortBal,0) as FAllMoney "
               + " from("
               + " select "
               + " sum(round(FMoney * FBaseCuryRate,2)) as FPortBal"
               + " from (select"
               + " FInOut * FMoney as FMoney,"
               + " FBaseCuryRate"
               + " from " + pub.yssGetTableName("tb_cash_subtransfer") + " a11"
               + " join " + pub.yssGetTableName("tb_cash_transfer") + " a12 on a11.FNum = a12.FNum"
               + " where a12.FTransferDate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + ""
               + " and a11.FPortCode = " + dbl.sqlString(this.portCode)
               + " and a12.FTsfTypeCode = '02'"
               + " and a12.FSubTsfTypeCode = '02DV'"
               + " and a12.FCheckState = 1"
               + " and length(FSecurityCode) > 0) a1"
               + " having sum(FMoney) <> 0)"
               + " union"
               + " select '4' as FOrder,'INTEREST BOUGHT(买入利息)' as Caption,"
               + " (select (danyue-shangyue-buyinterest) as portinterest from ("
               + " select nvl(sum(round(FPortCuryCost,2)),0) as danyue ,1 as a from " + pub.yssGetTableName("Tb_Stock_PurBond") + "  u where u.fcheckstate=1 and  u.fstoragedate=add_months(" + dbl.sqlDate(this.dBeginDate) + ",1) "
               + " and u.fportcode= " + dbl.sqlString(this.portCode) + "/*and u.fyearmonth=to_char(add_months(" + dbl.sqlDate(this.dBeginDate) + ",1),'yyyyMM')*/"
               + " ) a "
               + " left join (select nvl(sum(round(FPortCuryCost,2)),0) as shangyue,1 as a from " + pub.yssGetTableName("Tb_Stock_PurBond") + " c where c.fcheckstate=1 and c.fstoragedate=" + dbl.sqlDate(this.dBeginDate) + ""
               + " and c.fportcode= " + dbl.sqlString(this.portCode) + "/*and c.fyearmonth=to_char(" + dbl.sqlDate(this.dBeginDate) + ",'yyyyMM')*/"
               + " ) b  on a.a=b.a"
               + " left join ( select nvl(sum(round(round(f.faccruedinterest,2)*f.fbasecuryrate,2)),0) as buyinterest ,1 as a from " + pub.yssGetTableName("tb_data_subtrade") + " f  join " + pub.yssGetTableName("tb_para_security") + " k on f.fsecuritycode=k.fsecuritycode"
               + " where f.fcheckstate=1 and  k.fcheckstate=1 and k.fcatcode='FI' and f.fportcode= " + dbl.sqlString(this.portCode) + "and f.ftradetypecode='01' and "
               + " f.fbargaindate between  " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + " )"
               + " c on a.a=c.a"
               + " ) as Accruedinterest"
               + " from dual"
               + " union "
               + " select '5' as FOrder,"
               + " 'DIVIDEND CHANGE (应收利息/红利变动)' as FCaption,"
               + " DIVIDEND  as FAllMoney"
               + " from (select sum(NVL(aa,0)-nvl(bb,0)) as DIVIDEND"
               + " from (select 1 as a, sum(round(ffactsettlemoney*cc.fbasecuryrate,2)) as aa"
               + " from " + pub.yssGetTableName("tb_data_subtrade") + " cc"
               + " where cc.fportcode =  " + dbl.sqlString(this.portCode)
               + " and cc.fcheckstate=1"
               + " and cc.ftradetypecode = '06'"
               + " and cc.fbargaindate between "
               + dbl.sqlDate(this.dBeginDate) + " and "
               + dbl.sqlDate(this.dEndDate) + ""
               + ") aa "
//               + " group by  fbasecuryrate) aa " 邵宏伟 -- 多余的分组
               + " full join ("
               + " select 1 as a, NVL(sum(round(ffactsettlemoney*cc.fbasecuryrate,2)),0) as bb"
               + " from " + pub.yssGetTableName("tb_data_subtrade") + " cc"
               + " where cc.fportcode =  " + dbl.sqlString(this.portCode)
               + " and cc.fcheckstate=1"
               + " and cc.ftradetypecode = '06' and "
               + " cc.ffactsettledate between "
               + dbl.sqlDate(this.dBeginDate) + " and "
               + dbl.sqlDate(this.dEndDate) + ""
               + ")     bb on  aa.a=bb.a)"
               + " union "
               + " select '3' as FOrder,'INCOME CASH RECEIVABLE(应收现金收入)' as FCaption,sum(FRec) as FAllMoney from("
               + " select round((sum(round(FLXVBal,2))-sum(round(FBFLXBal,2))),2) as Frec from ("
               + " select * from tb_data_PortfolioVal where  FPORTCODE= " + dbl.sqlString(this.portCode) + "and FValDate=" + dbl.sqlDate(this.dEndDate) + ""
               + " and   forder like 'FI__%'  and  forder like '%__total%')        "
               + " union"
               + " select round(sum(FLXVBal),2)-round(sum(FBFLXBal),2) as FRec from ("
               + " select FLXVBal,FBFLXBal from tb_data_PortfolioVal where  FPORTCODE= " + dbl.sqlString(this.portCode) + "and FValDate=" + dbl.sqlDate(this.dEndDate) + ""
               + " and forder like '%0102%'   and   forder like '%total%' )"
               + " union"
               + " select FBaseCuryBal as FRec from ("
               + " select sum(FMoney) as FBaseCuryBal"
               + " from (select "
               + " sum(ROUND(FAccruedInterest*TRA.FBASECURYRATE,2)) as FMoney"
               + " from " + pub.yssGetTableName("tb_data_subtrade") + " tra"
               + " where FBargainDate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + ""
               + " and FPortCode =  " + dbl.sqlString(this.portCode)
               + " and tra.fcheckstate=1"
               + " and exists (select * from " + pub.yssGetTableName("tb_para_security") + " par where fcatcode='FI' and par.fcheckstate=1 AND tra.fsecuritycode=fsecuritycode)"
               + " and FTradeTypeCode in ('02', '17')"
               + " and FAccruedInterest > 0"
               + " union "
               + " select sum(round(FInout*FMoney*FBaseCuryRate,2)) as FMoney"
               + " from " + pub.yssGetTableName("tb_cash_subtransfer") + " a11"
               + " join " + pub.yssGetTableName("tb_cash_transfer") + " a12 "
               + " on a11.FNum = a12.FNum "
               + " and a11.FCheckState = 1"
               + " and a12.FCheckState = 1 "
               + " and a12.FTransferDate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + "     "
               + " and a11.FPortCode =  " + dbl.sqlString(this.portCode)
               + " and a12.FSubTsfTypeCode = '02FI')))))";
        try {
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                InterestAndIncome += rs.getDouble("FAllMoney");
            }
            HashMap valueMap = new HashMap();
            SummaryBean summary = new SummaryBean();
            summary.Code = "Interest / Income";
            summary.Name = "利息/收入";
            summary.CatCode = "Activities";
            summary.SubCatCode = "ActivitiesIAI";
            summary.CuryCode = "HKD";
            summary.Bal = 0;
            summary.BaseCuryBal = InterestAndIncome;
            summary.Date = YssFun.toSqlDate(this.dEndDate);
            summary.SummaryType = this.Summary_InterestAndIncome;
            summary.portCode = this.portCode;
            summary.Proportion = 0;
            //添加fattrclscode字段 20100818
            summary.fattrclscode = " ";
            valueMap.put(summary.Code, summary);
            insertToTempSummary(valueMap);
        } catch (SQLException ex) {
            throw new YssException("获取收入/费用数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return InterestAndIncome;
    }

//------begin,by xuxuming,20090904.MS00666,未兑现资产本金增值/贬值分布表（汇率）    QDV4中保2009年09月02日04_B ---------------
   /**
    * 构建此方法，是为了在外部调用calcUnrealisedCapital。
    * 在修改了tb_001_Data_Unrealised表中的数据后，需要重新调用此方法计算
    * @throws YssException
    * @return double
    */
   public double calcUnrealisedCapiPub() throws YssException {
      try {
         return this.calcUnrealisedCapital();
      }
      catch (YssException ex) {
         throw new YssException("获取未兑现数出错！");
      }

   }

   /**
    * 在修改了tb_001_Data_Unrealised表中的数据后，需要重新调用此方法计算,
    * 在此之前，应该删除已经存在的记录
    * @throws YssException
    */
   public void delCalcUnrealisedCapiPub() throws YssException,
         SQLException {
      SummaryBean summary = new SummaryBean();
      summary.Code = "UnrealisedCapital";
      summary.Name = "Total";
      summary.CatCode = "Unrealised";
      summary.SubCatCode = "Total";
      summary.portCode = this.portCode;
      summary.Date = YssFun.toSqlDate(this.dEndDate);
      summary.SummaryType = this.Summary_UnrealisedCapital;

      String sqlStr = "Delete from " +
            pub.yssGetTableName("tb_Data_Summary") +
            " where FDate = " + dbl.sqlDate(summary.Date) +
            " and Fportcode = " + dbl.sqlString(summary.portCode) +
            " and (FSummaryType = 10 or FSummaryType = 11 or FSummaryType = 9)" ;
      try {
         dbl.executeSql(sqlStr);
      }
      catch (YssException ex) {
         throw new YssException("删除未兑现数出错！");
      }

   }

   //---end---------------------------------------
   /**
    * 未兑现
    * @throws YssException
    */
    private double calcUnrealisedCapital() throws YssException {
        double UnrealisedCapital = 0;
        double UnrealisedCapitalMV = 0;
        double UnrealisedCapitalFX = 0;
        String sqlStr = "";
        ResultSet rs = null;
        HashMap valueMap = new HashMap();

        String requestParam = "RepVal00000300002\f\t1\r" + YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "\rnull\n2\r" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd") + "\rnull\n" +
        "3\r" + this.portCode + "\rnull\n4\r1\rnull"; //报表参数
//	    autoCreateReport(requestParam); //生成报表 未兑现汇兑涉及手工调整，不能重新生成
        requestParam = "RepVal00000300001\f\t1\r" + YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "\rnull\n2\r" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd") + "\rnull\n" +
        "3\r" + this.portCode + "\rnull\n4\r1\rnull"; //报表参数
	    autoCreateReport(requestParam); //生成报表
	    if (!dbl.yssTableExist(pub.yssGetTableName(Summary_Tab_UnRealised))) {
	        throw new YssException("请先完成UnRealisedFX报表！");
	    }

        sqlStr = "select " + Summary_UnrealisedCapital_MV + " as FSummaryType,'Unrealised' as FCatCode ,'UnrealisedMV' as FSubCatCode,'Market' as FName," +
            " 'UnrealisedCapitalMV' as FCode," +
            dbl.sqlDate(this.dEndDate) + " as FDate," +
            dbl.sqlString(this.portCode) + " as FPortCode," +
            " 'HKD' as FCuryCode," +
            " 0 as FProportion," +
            //添加fattrclscode字段 20100818
            " sum(FBal) as FBal, ' ' as fattrclscode," +
            " sum(FBaseCuryBal) as FBaseCuryBal from " +
            pub.yssGetTableName("Tb_Data_Unrealised") +
            " where FUnrealisedType = 2 and FDate = " +
            dbl.sqlDate(this.dEndDate) +
//            " and FUnrealisedType = 2 " +
            " and FPortCode = " + dbl.sqlString(this.portCode) + // MV
            " union all " +
            "select " + Summary_UnrealisedCapital_FX + " as FSummaryType,'Unrealised' as FCatCode ,'UnrealisedFX' as FSubCatCode,'Foreign Exchange' as FName," +
            " 'UnrealisedCapitalFX' as FCode," +
            dbl.sqlDate(this.dEndDate) + " as FDate," +
            dbl.sqlString(this.portCode) + " as FPortCode," +
            " 'HKD' as FCuryCode," +
            " 0 as FProportion," +
            //添加fattrclscode字段 20100818
            " sum(FBal) as FBal, ' ' as fattrclscode," +
            " sum(FBaseCuryBal) as FBaseCuryBal from " +
            pub.yssGetTableName("Tb_Data_Unrealised") +
            " where  FUnrealisedType = 5 " +
            " and FDate = " + dbl.sqlDate(this.dEndDate) +
            " and FPortCode = " + dbl.sqlString(this.portCode); // FX
        try {
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                if (rs.getInt("FSummaryType") == Summary_UnrealisedCapital_MV) {
                    UnrealisedCapitalMV = rs.getDouble("FBaseCuryBal");
                } else if (rs.getInt("FSummaryType") == Summary_UnrealisedCapital_FX) {
                    UnrealisedCapitalFX = rs.getDouble("FBaseCuryBal");
                }
                setResultValue(valueMap, rs);
            }
            UnrealisedCapital = YssD.add(UnrealisedCapitalMV, UnrealisedCapitalFX);
            if (UnrealisedCapital != 0) {
                SummaryBean summary = new SummaryBean();
                summary.Code = "UnrealisedCapital";
                summary.Name = "Total";
                summary.CatCode = "Unrealised";
                summary.SubCatCode = "Total";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = UnrealisedCapital;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = this.Summary_UnrealisedCapital;
                //添加fattrclscode字段 20100818
                summary.fattrclscode = " ";
                valueMap.put(summary.Code, summary);
            }
            insertToTempSummary(valueMap);

        } catch (SQLException ex) {
            throw new YssException("获取未兑现数出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return UnrealisedCapital;
    }

    /**
     * 已兑现
     * @throws YssException
     */
    private double calcRealisedCapital() throws YssException {
        double RealisedCapital = 0;
        double RealisedCapitalMV = 0;
        double RealisedCapitalFX = 0;
        String sqlStr = "";
        ResultSet rs = null;
        HashMap valueMap = new HashMap();
        SummaryBean summary = null;
        // --- MS00491 QDV4中保2009年06月09日01_B  sj 生成依赖报表----------------------------------
        String requestParam = "RepDays100000086\f\t1\r" + YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "\rnull\n2\r" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd") + "\rnull\n" +
            "3\r" + this.portCode + "\rnull"; //报表参数
        autoCreateReport(requestParam); //生成报表
        //-----------------------------------------------------------------------------
        if (!dbl.yssTableExist(pub.yssGetTableName(Summary_Tab_Realised))) {
            throw new YssException("请先完成RealisedFX报表！");
        }

        sqlStr = " select sum(round((FFactSettleMoney - FAccruedInterest - FVCost) * FBaseCuryRate / FPortCuryRate,2)) as GZ "
        	+ " from " + pub.yssGetTableName("tb_data_subtrade") + " t1 "
        	+ " join " + pub.yssGetTableName("tb_para_security") + " t2 on t1.fsecuritycode=t2.fsecuritycode "
        	+ " where t1.FCheckState = 1 and t2.fcheckstate=1 "
        	+ " and t2.fcatcode in ('FI','EQ','TR') and t1.ftradetypecode in ('02','17') "
        	+ " and t1.fbargaindate between " + dbl.sqlDate(this.dBeginDate)+" and "+ dbl.sqlDate(this.dEndDate)
        	+ " and t1.fportcode=" + dbl.sqlString(this.portCode);

        try {
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                RealisedCapitalMV += rs.getDouble("GZ");
            }
            summary = new SummaryBean();
            summary.Code = "RealisedCapitalMV";
            summary.Name = "Market";
            summary.CatCode = "RealisedCapital";
            summary.SubCatCode = "RealisedCapitalMV";
            summary.CuryCode = "HKD";
            summary.Bal = 0;
            summary.BaseCuryBal = RealisedCapitalMV;
            summary.Date = YssFun.toSqlDate(this.dEndDate);
            summary.SummaryType = this.Summary_RealisedCapital_MV;
            summary.portCode = this.portCode;
            summary.Proportion = 0;
            //添加fattrclscode字段 20100818
            summary.fattrclscode = " ";
            valueMap.put(summary.Code, summary);
        } catch (SQLException ex) {
            throw new YssException("获取已兑现估增数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    sqlStr=  " select SUM(J.FVALUE6) AS huidui from "+pub.yssGetTableName(Summary_Tab_Realised)+" J WHERE j.fcode LIKE '%ALL%' "+
    	" and j.fdate3= "+ dbl.sqlDate(this.dEndDate)+" AND Fportcode="+dbl.sqlString(this.portCode);
        try {
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                RealisedCapitalFX += rs.getDouble("huidui");
            }

           dbl.closeResultSetFinal(rs);

            //--------------------------------------------------------------------------------------------------------------//
            summary = new SummaryBean();
            summary.Code = "RealisedCapitalFX";
            summary.Name = "Foreign Exchange";
            summary.CatCode = "RealisedCapital";
            summary.SubCatCode = "RealisedCapitalFX";
            summary.CuryCode = "HKD";
            summary.Bal = 0;
            summary.BaseCuryBal = RealisedCapitalFX;
            summary.Date = YssFun.toSqlDate(this.dEndDate);
            summary.SummaryType = this.Summary_RealisedCapital_FX;
            summary.portCode = this.portCode;
            summary.Proportion = 0;
            //添加fattrclscode字段 20100818
            summary.fattrclscode = " ";
            valueMap.put(summary.Code, summary);
        } catch (SQLException ex1) {
            throw new YssException("获取已兑现汇兑损益数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        //---------------------------------------------------------------------------
        summary = new SummaryBean();
        summary.Code = "RealisedCapital";
        summary.Name = "Total";
        summary.CatCode = "RealisedCapital";
        summary.SubCatCode = "RealisedCapitalTotal";
        summary.CuryCode = "HKD";
        summary.Bal = 0;
        RealisedCapital = YssD.add(RealisedCapitalFX, RealisedCapitalMV);
        summary.BaseCuryBal = RealisedCapital;
        summary.Date = YssFun.toSqlDate(this.dEndDate);
        summary.SummaryType = this.Summary_RealisedCapital_Total;
        summary.portCode = this.portCode;
        summary.Proportion = 0;
        //添加fattrclscode字段 20100818
        summary.fattrclscode = " ";
        valueMap.put(summary.Code, summary);
        //---------------------------------------------------------------------------
        insertToTempSummary(valueMap);
        return RealisedCapital;
    }

    /**
     * 现估值日投资组合
     * @throws YssException
     */
    private void getCurPortValue() throws YssException {

        double cash = 0;
        double debt = 0;
        double equities = 0;
        double funds = 0;
        double fixedDeposit = 0;
        double duetofrombroker = 0;
        double duetofrom = 0;
        double total = 0;
        double Proportion = 0;
        double tempAdjustment = 0;
        double tempStorage = 0;
        int times = 0; //计算比例次数。
        String SummaryCode = "";
        SummaryBean summary = null;
        SummaryBean maxSummary = null; //比例值最大的项
        HashMap valueMap = new HashMap();
//      HashMap tempMap = null;
        cash = getCurPortCash(valueMap);
        debt = getCurDebtSecurities(valueMap);
        equities = getCurEquities(valueMap);
        funds = getCurFunds(valueMap);
        fixedDeposit = getCurFixedDeposit(valueMap);
        duetofrombroker = getDueToFromBroker(valueMap);
        duetofrom = getDueToFrom(valueMap);
        total = YssFun.roundIt(YssD.add(YssFun.roundIt(cash,2), YssFun.roundIt(debt,2),YssFun.roundIt(equities,2)
            ,YssFun.roundIt(funds,2), YssFun.roundIt(fixedDeposit,2),
        		YssFun.roundIt(duetofrombroker,2),
        		YssFun.roundIt(duetofrom,2)), 2); //现估值日投资组合总和

        upateValScale(total);  //更新VAL表的百分比字段
        Iterator it = valueMap.keySet().iterator();
        while (it.hasNext()) {
            ++times;
            summary = (com.yss.main.operdeal.report.reptab.TabSummary.
                       SummaryBean) valueMap.get( (String) it.next());
            tempStorage = YssD.mul(YssFun.roundIt(YssD.div(YssFun.roundIt(
                summary.BaseCuryBal, 2), total), 6), 100);
            if (tempStorage > tempAdjustment) {
                tempAdjustment = tempStorage;
                SummaryCode = summary.Code;
            }
            Proportion += YssFun.roundIt(tempStorage, 2);
            summary.Proportion = YssFun.roundIt(tempStorage, 2);
            //-------------------------将比例的尾差值并入比例值最大的类型中-----------------------------
            //if (times == 6) {
            //    maxSummary = (com.yss.main.operdeal.report.reptab.TabSummary.
            //                  SummaryBean) valueMap.get(SummaryName);
            //    maxSummary.Proportion = YssD.add(YssD.sub(100, Proportion),
            //                                     maxSummary.Proportion);
            //}
            //-------------------------------------------------------------------------------------
        }
        //调整将尾差并入比例值最大的类型中，在循环体外执行
        if (times != 0) {
            maxSummary = (com.yss.main.operdeal.report.reptab.TabSummary.
            SummaryBean) valueMap.get(SummaryCode);
            maxSummary.Proportion = YssD.add(YssD.sub(100, Proportion),
                                             maxSummary.Proportion);
        }
        insertToTempSummary(valueMap);
        valueMap.clear();
        summary = new SummaryBean();
        summary.Code = "ValutionTotal";
        summary.Name = "Total";
        summary.CatCode = "Valution";
        summary.SubCatCode = "Total";
        summary.CuryCode = "HKD";
        summary.Bal = 0;
        summary.BaseCuryBal = total;
        summary.Date = YssFun.toSqlDate(this.dEndDate);
        summary.SummaryType = this.Summary_Valution_Total;
        summary.portCode = this.portCode;
        summary.Proportion = 100;
        //添加fattrclscode字段 20100818
        summary.fattrclscode = " ";
        valueMap.put(summary.Code, summary);
        //------------------------------------------------------------------------
        insertToTempSummary(valueMap);


    }

    /**
     * 更新VAL表的基金占比
     * @throws YssException
     */
    private void upateValScale(double total) throws YssException {
    	try {

    	String sql = " update tb_data_portfolioval t2 set t2.ffundallotproportion=round((t2.fmvalue + t2.flxvbal) * fbasecuryrate /" + total +",4) "
    		+ " where t2.fportcode= " + dbl.sqlString(this.portCode)
    	    + " and t2.fvaldate= " + dbl.sqlDate(this.dEndDate);

    	dbl.executeSql(sql);

        } catch (SQLException ex) {
            throw new YssException("更新Val表中基金占比出错！");
        }
    }

    private double getCurPortCash(HashMap valueMap) throws YssException {
        double portCash = 0;
        String sqlStr = "";
        ResultSet rs = null;
        SummaryBean summary = null;
//        sqlStr =" select 'Cash' as FCode,'現金' as FName, NVL(sum(FTotalCost),2) as FTotalCost from ("+
//        	" select  sum(a.fmvalue)  as FTotalCost "+   //modify by ctq 20091224 统计项此字段存储的即为港币等值金额
//            " from tb_data_PortfolioVal a  "+
//            "  where (FOrder like '01__0101%total' or FOrder like '01__0105%total') "+
//            "  and a.FValDate = "+dbl.sqlDate(this.dEndDate)+" and a.FPortCode = "+dbl.sqlString(this.portCode)+"  group by a.FCuryCode )";
        //edit by qiuxufeng 20101122 与原报表比对数据，修改与原报表相同
        sqlStr =" select fattrclscode ||'Cash' as FCode,'現金' as FName, fattrclscode, NVL(sum(FTotalCost),2) as FTotalCost from ("+
	    	" select  sum(a.fmvalue)  as FTotalCost, substr(forder,11,2) as fattrclscode"+   //modify by ctq 20091224 统计项此字段存储的即为港币等值金额
	        " from tb_data_PortfolioVal a  "+
	        "  where (FOrder like '01__0101%total' or FOrder like '01__0105%total') "+
	        "  and a.FValDate = "+dbl.sqlDate(this.dEndDate)+" and a.FPortCode = "+dbl.sqlString(this.portCode)+"  group by a.FCuryCode, a.forder ) group by fattrclscode";


        try {
            rs = dbl.openResultSet(sqlStr);
            //查询结果返回多条记录，循环控制插入表中
            while (rs.next()) {
			//if (rs.next()) {
                portCash += rs.getDouble("FTotalCost");
                summary = new SummaryBean();
                summary.Code = rs.getString("FCode");
                summary.Name = rs.getString("FName");
                summary.CatCode = "Valution";
                summary.SubCatCode = "Cash";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = rs.getDouble("FTotalCost");
                //summary.BaseCuryBal = portCash;
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = this.Summary_Valution_Cash;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                summary.fattrclscode = rs.getString("Fattrclscode");
                valueMap.put(summary.Code, summary);
            }
        } catch (SQLException ex) {
            throw new YssException("获取Val表中Cash数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return portCash;
    }

    //债券市值
    private double getCurDebtSecurities(HashMap valueMap) throws YssException {
        double portDebt = 0;
        String sqlStr = "";
        ResultSet rs = null;
        SummaryBean summary = null;
        //添加新增债券品种FI18,FI17,FI15,FI16,FI12,FI08
        sqlStr = "select 'Debt Securities（'||ftype||'）' as FCode, '債務票據' as FName,fattrclscode, sum(FMarketValue) as FMarketValue "
        	+ " from (select decode(substr(forder,5,4),'FI18','FV_T','FI17','FVCB_T','FI16','AFS_T','FI15','HTM_T','FI12','普通_T','FI08','FV','FI07','FVCB','FI06','AFS','FI05','HTM','普通')  as ftype, "
            //查找出资产分类信息
        	+ "substr(forder,11,2) as fattrclscode, "
            + " round(nvl(a.FTotalCost,0),2) + round(nvl(a.FYKVBal,0),2) + round(nvl(a.FSyvBaseCuryBal,0),2) + round(nvl(a.FBoughtInt,0), 2) + round(nvl(a.FLXVBal,0), 2) as FMarketValue "
            + " from tb_data_portfolioval a "
            + " where a.FOrder like 'FI__FI%total' "
            + " and a.FValDate = " + dbl.sqlDate(this.dEndDate)
            + " and a.FPortCode = " + dbl.sqlString(this.portCode)
            + " ) group by ftype, fattrclscode" ;

        try {
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                portDebt += rs.getDouble("FMarketValue");
                summary = new SummaryBean();
                summary.Code = rs.getString("FCode");
                summary.Name = rs.getString("FName");
                summary.CatCode = "Valution";
                summary.SubCatCode = "DebtSecurityes";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = rs.getDouble("FMarketValue");
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = this.Summary_Valution_Debt;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                summary.fattrclscode = rs.getString("Fattrclscode");
                valueMap.put(summary.Code, summary);
            }
        } catch (SQLException ex) {
            throw new YssException("获取Valution中Debt Securities数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return portDebt;
    }

    //股票市值
    private double getCurEquities(HashMap valueMap) throws YssException {
        double portEquities = 0;
        String sqlStr = "";
        ResultSet rs = null;
        SummaryBean summary = null;
        sqlStr =
        	// 根据需求调整添加fattrclscode 判断资本分类信息,调整fcode字段使其保持唯一 20100818
            " select  e.fattrclscode ||'Equities' as FCode, '股票' as FName,e.fattrclscode as fattrclscode, sum(FMarketValue) as FMarketValue from ("+
        	" select   a.FTotalCost + a.FYKVBal + a.FSysVBaseCuryBal + nvl(b.FLXVBal, 0) as FMarketValue, a.fattrclscode "+
        	"  from (select FCuryCode, substr(forder,11,2) as fattrclscode,  sum(FTotalCost) as FTotalCost, "+
        	"    sum(FYKVBal) as FYKVBal,    sum(FSyvBaseCuryBal) as FSysVBaseCuryBal "+
        	"    from tb_data_PortfolioVal  where FOrder like 'EQ__EQ%total' "+
        	"   and FOrder not like 'EQ__EQ98%total' "+
        	"  and FValDate = "+dbl.sqlDate(this.dEndDate)+"   and FPortCode = "+dbl.sqlString(this.portCode)+"    group by FCuryCode, substr(forder,11,2)) a  "+
        	"  left join (select FCuryCode, sum(FMvalue) as FLXVBal, substr(forder,11,2) as fattrclscode  from tb_data_PortfolioVal "+
        	"   where FOrder like 'EQ__EQ98%total' "+
        	" and FValDate = "+dbl.sqlDate(this.dEndDate)+"    and FPortCode = "+dbl.sqlString(this.portCode)+"  group by FCuryCode, substr(forder,11,2)) b" +
        	"  on a.FCurycode = b.FCurycode and a.fattrclscode = b.fattrclscode  left join " + pub.yssGetTableName("tb_para_currency") + " c on " +
        	" a.FCurycode = c.FCuryCode )e group by fattrclscode";

        try {
            rs = dbl.openResultSet(sqlStr);
            //根据返回的结果集为多条，采用while循环插入  版本合并（合并邵宏伟修改代码）
            while (rs.next()){
            	//if (rs.next()) {
                portEquities += rs.getDouble("FMarketValue");
                summary = new SummaryBean();
                summary.Code = rs.getString("FCode");
                summary.Name = rs.getString("FName");
                summary.CatCode = "Valution";
                summary.SubCatCode = "Equities";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = rs.getDouble("FMarketValue");
                //summary.BaseCuryBal = portEquities;
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = this.Summary_Valution_Equities;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                summary.fattrclscode = rs.getString("Fattrclscode");
                valueMap.put(summary.Code, summary);
                //valueMap.put(summary.Code, summary);
            }
        } catch (SQLException ex) {
            throw new YssException("获取Valution中Equities数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return portEquities;
    }

    //基金市值
    private double getCurFunds(HashMap valueMap) throws YssException {
        double portFunds = 0;
        String sqlStr = "";
        ResultSet rs = null;
        SummaryBean summary = null;
        sqlStr =
        	// 根据需求调整添加fattrclscode 判断资产分类信息，调整fcode字段使其唯一 20100818
            " select  e.fattrclscode ||'Equities Fund' as FCode, '基金' as FName, e.fattrclscode as fattrclscode, sum(FMarketValue) as FMarketValue from ("+
         " select   a.FTotalCost + a.FYKVBal + a.FSysVBaseCuryBal + nvl(b.FLXVBal, 0) as FMarketValue, a.fattrclscode "+
         "  from (select FCuryCode,   sum(FTotalCost) as FTotalCost, "+
         "    sum(FYKVBal) as FYKVBal, substr(forder,11,2) as fattrclscode,  sum(FSyvBaseCuryBal) as FSysVBaseCuryBal "+
         "    from tb_data_PortfolioVal  where FOrder like 'TR__TR%total' "+
         "   and FOrder not like 'TR__TR98%total' "+
         "  and FValDate = "+dbl.sqlDate(this.dEndDate)+"   and FPortCode = "+dbl.sqlString(this.portCode)+"    group by FCuryCode, substr(forder,11,2)) a  "+
         "  left join (select FCuryCode, sum(FMvalue) as FLXVBal, substr(forder, 11, 2) as fattrclscode  from tb_data_PortfolioVal "+
         "   where FOrder like 'TR__TR98%total' "+
         " and FValDate = "+dbl.sqlDate(this.dEndDate)+"    and FPortCode = "+dbl.sqlString(this.portCode)+"  group by FCuryCode, substr(forder,11,2)) b" +
         "  on a.FCurycode = b.FCurycode and a.fattrclscode = b.fattrclscode  left join " + pub.yssGetTableName("tb_para_currency") + " c on " +
         " a.FCurycode = c.FCuryCode )e group by fattrclscode";

        try {
            rs = dbl.openResultSet(sqlStr);
            //根据查询结果有多条，使用while循环插入  版本合并（合并邵宏伟修改代码）
            while (rs.next()) {
			//if (rs.next()) {
                portFunds += rs.getDouble("FMarketValue");
                summary = new SummaryBean();
                summary.Code = rs.getString("FCode");
                summary.Name = rs.getString("FName");
                summary.CatCode = "Valution";
                summary.SubCatCode = "Funds";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = rs.getDouble("FMarketValue");
                //summary.BaseCuryBal = portFunds;
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = this.Summary_Valution_Funds;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                summary.fattrclscode = rs.getString("Fattrclscode");
                valueMap.put(summary.Code, summary);
            }
        } catch (SQLException ex) {
            throw new YssException("获取Valution中Funds数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return portFunds;
    }

    //定存市值
    private double getCurFixedDeposit(HashMap valueMap) throws YssException {
        double portFixedDeposit = 0;
        String sqlStr = "";
        ResultSet rs = null;
        SummaryBean summary = null;

//        sqlStr=" select 'Fixed Deposit' as FCode,'定期存款' as FName, sum(FMarketValue) as FMarketValue from ("+
//        " select  sum(round(fmvalue, 2)) + sum(round(FLXVBal, 2)) as FMarketValue "+
//        " from tb_data_portfolioval a "+
//        " where a.FOrder like '01__0102%total' "+
//        " and a.FValDate = "+dbl.sqlDate(this.dEndDate)+"  and a.FPortCode = "+dbl.sqlString(this.portCode)+" group by a.FCurycode )";
        //edit by qiuxufeng 20101122 与原报表比对数据，修改与原报表相同
        sqlStr=" select fattrclscode || 'Fixed Deposit' as FCode, '定期存款' as FName, fattrclscode, sum(FMarketValue) as FMarketValue from ("+
	        " select  sum(round(fmvalue, 2)) + sum(round(FLXVBal, 2)) as FMarketValue, substr(a.forder,11,2) as fattrclscode "+
	        " from tb_data_portfolioval a "+
	        " where a.FOrder like '01__0102%total' "+
	        " and a.FValDate = "+dbl.sqlDate(this.dEndDate)+"  and a.FPortCode = "+dbl.sqlString(this.portCode)+" group by a.FCurycode, a.forder ) group by fattrclscode";

        try {
            rs = dbl.openResultSet(sqlStr);
            //根据查询结果返回非一条数据，将if更改为while
            while (rs.next()){
            //if (rs.next()) {
                portFixedDeposit += rs.getDouble("FMarketValue");
                summary = new SummaryBean();
                summary.Code = rs.getString("FCode");
                summary.Name = rs.getString("FName");
                summary.CatCode = "Valution";
                summary.SubCatCode = "FixedDeposit";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                //使用rs.getDouble("FMarketValue")给BaseCuryBal赋值
                summary.BaseCuryBal = rs.getDouble("FMarketValue");
                //summary.BaseCuryBal = portFixedDeposit;
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = this.Summary_Valution_FixedDeposit;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                summary.fattrclscode = rs.getString("Fattrclscode");
                valueMap.put(summary.Code, summary);
            }
        } catch (SQLException ex) {
            throw new YssException("获取Valution中FixedDeposit数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return portFixedDeposit;
    }
   //应收应付明细表--其他应收应付
    private double getDueToFromBroker(HashMap valueMap) throws YssException {
        double portDueToFromBroker = 0;
        String sqlStr = "";
        ResultSet rs = null;
        SummaryBean summary = null;

        String requestParams = "RepDays00007V1.0\f\t" +
            "1\r" + YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "\rnull\n2\r" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd") + "\rnull\n" +
            "3\r" + this.portCode + "\rnull"; //报表参数;
        autoCreateReport(requestParams);
        //------------------------------------------
       // if (!dbl.yssTableExist(this.Summary_Tab_ValutionDueToFromBroker )) {
        if (!dbl.yssTableExist(pub.yssGetTableName(this.Summary_Tab_ValutionDueToFromBroker))) {
            throw new YssException("请先完成应收应付明细报表！");
        }
        sqlStr =
        	" select round(sum(FPortCuryBal), 2) as Bal, fattrclscode || 'Other payable and receivables' as fcode, fattrclscode " +
        	" from (select sum(case when ftsftypecode = '07' then -1 * round(nvl(FBal * fbaserate, 0), 2) else round(nvl(FBal * fbaserate, 0), 2) end) " +
        	" as FPortCuryBal, rr.fanalysiscode3 as fattrclscode, FPortcode from " + pub.yssGetTableName("tb_stock_investpayrec") +
        	" rr left join (select n.fcurycode, fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " n where n.fvaldate = " + dbl.sqlDate(this.dEndDate) +
        	" and n.fportcode = " + dbl.sqlString(this.portCode) + " and n.fcheckstate = 1) r on r.fcurycode = rr.fcurycode " +
        	" where rr.fstoragedate = " + dbl.sqlDate(this.dEndDate) + "and rr.fivpaycatcode <> 'IV001' and rr.ftsftypecode <> '99' and fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portCode) +
        	" group by fportcode, fanalysiscode3 union all select round(sum(fportcurybal), 2) as FPortCuryBal, fattrclscode, fportcode from (select sum(case when d.ftsftypecode = '06' then d.fbal * m.fbaserate " +
        	" else -1 * d.fbal * m.fbaserate end) as fportcurybal, d.fportcode, d.fanalysiscode2 as fattrclscode, d.fcurycode from " + pub.yssGetTableName("tb_stock_cashpayrec") +
        	" d left join (select ns.fcurycode, ns.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " ns where ns.fcheckstate = 1 and ns.fportcode = " + dbl.sqlString(this.portCode) +
        	" and ns.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") m on d.fcurycode = m.fcurycode where d.fportcode = " + dbl.sqlString(this.portCode) +
        	" and d.fcheckstate = 1 and d.fstoragedate = " + dbl.sqlDate(this.dEndDate) + " and d.fyearmonth <> to_char(add_months(" + dbl.sqlDate(this.dBeginDate) +
        	" , 1), 'yyyy') || '00' and d.ftsftypecode in ('06', '07') and d.fsubtsftypecode not in ('02DV','02DE','02FI','02TD','06DE','06FI','06TD','06DV','07TD','07DE','07FI','07DV') " +
        	" group by d.fportcode, d.fcurycode, d.fanalysiscode2) group by fportcode, fattrclscode) group by fportcode, fattrclscode ";
        /*sqlStr =
            "select round(sum(fjd),2) as Bal from " + Summary_Tab_ValutionDueToFromBroker +
            " where  Fportcode ="+dbl.sqlString(this.portCode)+
            " and fgussdate="+dbl.sqlDate(this.dEndDate) ;*/

        try {
            rs = dbl.openResultSet(sqlStr);
            //summary = new SummaryBean(); //将对象调整到循环之外，以便数据的累计。
            while (rs.next()) {
            	portDueToFromBroker += rs.getDouble("Bal");
            	summary = new SummaryBean();
            //}
            	summary.Code = rs.getString("Fcode");
            	//summary.Code = "Other payable and receivables";
            	summary.Name = "其他应收应付款";
            	summary.CatCode = "Valution";
            	summary.SubCatCode = "ToFromBroker";
            	summary.CuryCode = "HKD";
            	summary.Bal = 0;
            	summary.BaseCuryBal = rs.getDouble("Bal");
            	//summary.BaseCuryBal = portDueToFromBroker;
            	summary.Date = YssFun.toSqlDate(this.dEndDate);
            	summary.SummaryType = this.Summary_Valution_DueToFromBroker;
            	summary.portCode = this.portCode;
            	summary.Proportion = 0;
            	//添加fattrclscode字段 20100818
            	summary.fattrclscode = rs.getString("Fattrclscode");
            	valueMap.put(summary.Code, summary);
            }
        } catch (SQLException ex) {
            throw new YssException("获取Valution中应收应付明细报表数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return portDueToFromBroker;
    }

    private double getDueToFrom(HashMap valueMap) throws YssException {
        double duetofrom = 0;
        String sqlStr = "";
        ResultSet rs = null;
        SummaryBean summary = null;
        sqlStr =
        	//调整fcode字段，使其含有资本分类信息，增加fattrclscode字段 20100818
        	" select -1*(NVL(FPortCuryMoney,0)-NVL(zifu,0))  AS FPortCuryMoney, aa.fanalysiscode3 || 'Accrual Management fees' as fcode, aa.fanalysiscode3 as fattrclscode from ( "+
        	" select round(sum(si.fbal*sr.baserate),2) as FPortCuryMoney, fanalysiscode3, 1 as a  from  " + pub.yssGetTableName("tb_stock_invest") + " si "+
            "  left join ( select distinct i.fcurycode,case when i.fcurycode='HKD' THEN 1 ELSE i.fbasecuryrate END baserate " +
            " from " + pub.yssGetTableName("tb_stock_invest") + " i where i.fcheckstate=1  "+
        	"  and i.fstoragedate= "+dbl.sqlDate(this.dEndDate)+") sr on si.fcurycode=sr.fcurycode "+
        	" where FIVPayCatCode = 'IV001'   and FPortCode = "+dbl.sqlString(this.portCode)+"   and Fcheckstate = 1  and " +
        	operSql.sqlStoragEve(this.dBeginDate) + " group by fanalysiscode3 ) aa "+
  //原来是使用资金调拨冲减，但是存在有资金调拨与被冲减的运营应收应付不等的情况，现在改为用运营应收应付冲减。
         " left join (  select sum((j.fmoney * y.fbaserate)) as zifu ,fanalysiscode3 , 1 a from " + pub.yssGetTableName("tb_data_investpayrec") + " j"
         + " left join (select b.fcurycode, b.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " b "
         + " where b.fcheckstate = 1 and b.fportcode = "+dbl.sqlString(this.portCode)
         + " and b.fvaldate = "+dbl.sqlDate(this.dEndDate)+") y on y.fcurycode = j.fcurycode "
         + " where j.fcheckstate = 1 and j.ftransdate between "
         + dbl.sqlDate(this.dBeginDate)+" and " + dbl.sqlDate(this.dEndDate)
         + " and j.fivpaycatcode = 'IV001' and j.ftsftypecode = '03' "
         + " and j.fportcode = "+dbl.sqlString(this.portCode)
         + "  group by  fanalysiscode3 ) bb on bb.a=aa.a and aa.fanalysiscode3 = bb.fanalysiscode3";


      try {
         rs = dbl.openResultSet(sqlStr);
         while (rs.next()) {
            duetofrom += rs.getDouble("FPortCuryMoney");
            summary = new SummaryBean();
            summary.Code = rs.getString("Fcode");
            summary.Name = "应付管理费";
            summary.CatCode = "Valution";
            summary.SubCatCode = "DueToFrom";
            summary.CuryCode = "HKD";
            summary.Bal = 0;
            summary.BaseCuryBal =  rs.getDouble("FPortCuryMoney");
            summary.Date = YssFun.toSqlDate(this.dEndDate);
            summary.SummaryType = this.Summary_Valution_DueToFrom;
            summary.portCode = this.portCode;
            summary.Proportion = 0;
            //添加fattrclscode字段 20100818
            summary.fattrclscode = rs.getString("Fattrclscode");
            valueMap.put(summary.Code, summary);
         }
      }
      catch (SQLException ex) {
         throw new YssException("获取Valution中Due to / due 数据出错！");
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
      return duetofrom;
   }

    /**
     * 未完成交易
     * @throws YssException
     */
    private void calcUnsettleTrades() throws YssException {
        String sqlStr = "";
        double unTradeBy = 0;
        double unTradeSold = 0;
        ResultSet rs = null;
        HashMap valueMap = new HashMap();
        //--- MS00491 QDV4中保2009年06月09日01_B sj - 买入

        String requestParams = "RepDays00006V1.0\f\t" +
        "1\r" + YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "\rnull\n2\r" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd") + "\rnull\n" +
        "3\r" + this.portCode + "\rnull"; //报表参数;
        autoCreateReport(requestParams);

        if (!dbl.yssTableExist(pub.yssGetTableName(this.Summary_Tab_Untrade ))) {
            throw new YssException("请先完成Unsettled Trades报表！");
        }

        //查询结果添加fattrclscode字段，调整focde字段使其唯一 20100818
        sqlStr = "select sum(round(NVL(FBaseCuryMoney,0),2) + round(NVL(FBasecuryAccint,0),2))  as PortMoney, s.fcatcode || 'Bought' as fcode, s.fcatcode as fattrclscode "+
               " from "+pub.yssGetTableName(Summary_Tab_Untrade)+" s   where s.ftradetypecode = '01' "+
               "  and s.fbargaindate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+
               "  and s.fsettledate > "+dbl.sqlDate(this.dEndDate) + " and FPortcode = " + dbl.sqlString(this.portCode)+
               " group by fcatcode ";

        try {
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	unTradeBy += rs.getDouble("PortMoney");
            if (unTradeBy != 0) {
                    SummaryBean summary = new SummaryBean();
                    summary.Code = rs.getString("Fcode");
                    summary.Name = "買入";
                    summary.CatCode = "UnsettledTrades";
                    summary.SubCatCode = "Buy";
                    summary.CuryCode = "HKD";
                    summary.Bal = 0;
                    //通过rs.getDouble("PortMoney")获取买入交易数值  任智 20101011 
                    //summary.BaseCuryBal = YssD.mul(unTradeBy, -1);
                    summary.BaseCuryBal = YssD.mul(rs.getDouble("PortMoney"), -1);
                    summary.portCode = this.portCode;
                    summary.Proportion = 0;
                    summary.Date = YssFun.toSqlDate(this.dEndDate);
                    summary.SummaryType = this.Summary_UnsettleTrade_In;
                    //添加fattrclscode字段 20100818
                    summary.fattrclscode = rs.getString("Fattrclscode");
                    valueMap.put(summary.Code, summary);
                }
               
            }
            dbl.closeResultSetFinal(rs);
            sqlStr = "select sum(round(NVL(FBaseCuryMoney,0),2)+round(NVL(FBasecuryAccint,0),2)) as FPortTotalCost, s.fcatcode || 'Sold' as fcode, s.fcatcode as fattrclscode "+
            " from "+pub.yssGetTableName(Summary_Tab_Untrade)+" s   where s.ftradetypecode = '02' "+
            "  and s.fbargaindate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+
            "  and s.fsettledate > "+dbl.sqlDate(this.dEndDate) + " and FPortCode = " + dbl.sqlString(this.portCode)+
            " group by fcatcode ";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	unTradeSold += rs.getDouble("FPortTotalCost");
            	if (unTradeSold != 0) {
                	SummaryBean summary = new SummaryBean();
                    summary.Code = rs.getString("fcode");
                    summary.Name = "賣出";
                    summary.CatCode = "UnsettledTrades";
                    summary.SubCatCode = "Sold";
                    summary.CuryCode = "HKD";
                    summary.Bal = 0;
                    //直接通过rs.getDouble("FPortTotalCost")获得卖出交易数值 任智 20101011
                    //summary.BaseCuryBal = unTradeSold;
                    summary.BaseCuryBal = rs.getDouble("FPortTotalCost");
                    summary.portCode = this.portCode;
                    summary.Proportion = 0;
                    summary.Date = YssFun.toSqlDate(this.dEndDate);
                    summary.SummaryType = this.Summary_UnsettleTrade_Out;
                    //添加fattrclscode字段 20100818
                    summary.fattrclscode = rs.getString("Fattrclscode");
                    valueMap.put(summary.Code, summary);
                }
             }
            if (YssD.add(YssD.mul(unTradeBy, -1), unTradeSold) != 0) {
                SummaryBean summary = new SummaryBean();
                summary.Code = "UnsettledTradesTotal";
                summary.Name = "UnsettledTradesTotal";
                summary.CatCode = "UnsettledTrades";
                summary.SubCatCode = "Total";
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = YssD.add(YssD.mul(unTradeBy, -1), unTradeSold);
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = this.Summary_UnsettleTrade_Total;
                //添加fattrclscode字段 20100818
                summary.fattrclscode = " ";
                valueMap.put(summary.Code, summary);
            }
            insertToTempSummary(valueMap);
        } catch (SQLException ex) {
            throw new YssException("获取未完成交易出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     *
     * @throws YssException
     * 此处调整返回参数，以便在之后的计算中获取其数据，将返回double值
     * MS00549 增加显示当月管理费 sj modified 20090708
     */
    private double getSecondNet() throws YssException {
        double secondNet = 0;
        String sqlStr = "";
        ResultSet rs = null;
        HashMap valueMap = new HashMap();
        SummaryBean summary = null;
        sqlStr = "select * from " + pub.yssGetTableName("tb_data_summary") + " where FCatCode in ('UnsettledTrades','Valution') and FSubCatCode = 'Total' and FDate = " +
            dbl.sqlDate(this.dEndDate) + " and Fportcode ='" + portCode + "'"; ;
        try {
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                secondNet += rs.getDouble("FBaseCuryBal");
            }
            summary = new SummaryBean();
            summary.Code = "NetSecond";
            summary.Name = "NetSecond";
            summary.CatCode = "Net";
            summary.SubCatCode = "Second";
            summary.CuryCode = "HKD";
            summary.Bal = 0;
            summary.BaseCuryBal = secondNet;
            summary.portCode = this.portCode;
            summary.Proportion = 0;
            summary.Date = YssFun.toSqlDate(this.dEndDate);
            summary.SummaryType = this.Summary_Net_Second;
            //添加fattrclscode字段 20100818
            summary.fattrclscode = " ";
            valueMap.put(summary.Code, summary);
            insertToTempSummary(valueMap);
        } catch (SQLException ex) {
            throw new YssException("获取現估值日投資組合净值出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return secondNet;//sj modified 20090707 返回净值数据 MS00549
    }

    /**
     * 计算当月管理费
     * @return double
     * @throws YssException
     * MS00549 增加显示当月管理费 sj modified 20090708
     */
    private double calcManageFee() throws YssException {
        double manageFeeSum = 0D;
        double manageFeeEach = 0D;
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        HashMap valueMap = new HashMap();
        SummaryBean summary = null;
        try {
            buf.append("select sum(case when FAnalysisCode3 is null then 0 ");
            buf.append(" when FAnalysisCode3 = '' then 0 ");
            buf.append(" else FBaseCuryMoney end ");
            buf.append(" ) as FBaseCuryMoney,");//获取除存在分析代码数据的管理费，其它的设置为0
            buf.append(" case when FAnalysisCode3 = 'EQ' then 'Equities'");
            buf.append(" when FAnalysisCode3 = 'FI' then 'Debt Securities'");
            buf.append(" when FAnalysisCode3 = 'DE' then 'Money Market'");
            buf.append(" when FAnalysisCode3 in  ('DR','TR') then 'Derivatives'");
            buf.append(" when FAnalysisCode3 = 'WT' then 'Warrant'");
            buf.append(" else FAnalysisCode3 end as FAnalysisCode3");//直接转换成相应的品种
            buf.append(" from ");
            buf.append(pub.yssGetTableName("Tb_data_investpayrec"));
            buf.append(" where FIvPayCatCode = 'IV001' ");//获取管理费
            buf.append(" and FSubTsfTypeCode = '07IV'");//应付费用
            buf.append(" and fcheckstate = 1 ");
            buf.append(" and FPortCode = ").append(dbl.sqlString(this.portCode));
            buf.append(" and FTransDate between ").append(dbl.sqlDate(this.dBeginDate)).append(" and ").append(dbl.sqlDate(this.dEndDate));//当月的发生额
            buf.append(" group by FAnalysisCode3 ");//以分析代码3来分组
            rs = dbl.openResultSet(buf.toString());
            while (rs.next()) {//分类
                manageFeeEach = rs.getDouble("FBaseCuryMoney");
                manageFeeSum += rs.getDouble("FBaseCuryMoney");
                summary = new SummaryBean();
                summary.Code =  rs.getString("FAnalysisCode3");//管理费分类
                summary.Name = rs.getString("FAnalysisCode3");//管理费分类
                summary.CatCode = "Manage";
                summary.SubCatCode = "Manage" + summary.Code;
                summary.CuryCode = "HKD";
                summary.Bal = 0;
                summary.BaseCuryBal = manageFeeEach;
                summary.portCode = this.portCode;
                summary.Proportion = 0;
                summary.Date = YssFun.toSqlDate(this.dEndDate);
                summary.SummaryType = this.Summary_ManageFee;
                //添加fattrclscode字段 20100818
                summary.fattrclscode = " ";
                valueMap.put(summary.Code, summary);
            }
            summary = new SummaryBean();//汇总
            summary.Code = "ManageFeeSum";
            summary.Name = "ManageFeeSum";
            summary.CatCode = "ManageSum";
            summary.SubCatCode = "ManageSum";
            summary.CuryCode = "HKD";
            summary.Bal = 0;
            summary.BaseCuryBal = manageFeeSum;
            summary.portCode = this.portCode;
            summary.Proportion = 0;
            summary.Date = YssFun.toSqlDate(this.dEndDate);
            summary.SummaryType = this.Summary_ManageFee;
            //添加fattrclscode字段 20100818
            summary.fattrclscode = " ";
            valueMap.put(summary.Code, summary);
            insertToTempSummary(valueMap);
        }
        catch (SQLException ex) {
            throw new YssException("获取当月管理费用出错！",ex);
        }
        finally {
            dbl.closeResultSetFinal(rs);
        }
        return manageFeeSum;
    }

    /**
     * 计算最终的净值数据
     * @param netSecond double 上一净值数据
     * @param manageFee double 管理费当月
     * @throws YssException
     * MS00549 增加显示当月管理费 sj modified 20090708
     */
    private void calcThreeNet(double netSecond, double manageFee) throws YssException {
        double netThree = 0D;
        HashMap valueMap = new HashMap();
        SummaryBean summary = null;
        netThree = YssD.sub(netSecond, manageFee);//上一净值数据 - 管理费当月
        summary = new SummaryBean(); //汇总
        summary.Code = "NetThree";
        summary.Name = "NetThree";
        summary.CatCode = "Net";
        summary.SubCatCode = "Three";
        summary.CuryCode = "HKD";
        summary.Bal = 0;
        summary.BaseCuryBal = netThree;
        summary.portCode = this.portCode;
        summary.Proportion = 0;
        summary.Date = YssFun.toSqlDate(this.dEndDate);
        summary.SummaryType = this.Summary_Net_Three;
        //添加fattrclscode字段 20100818
        summary.fattrclscode = " ";
        valueMap.put(summary.Code, summary);
        insertToTempSummary(valueMap);
    }

    /**
     * 生成SUMMARY报表的依赖报表
     * @param requestParams String 报表所需参数
     * @throws YssException
     * MS00491 QDV4中保2009年06月09日01_B
     */
    private void autoCreateReport(String requestParams) throws YssException {
        CommonRepBean rep = new CommonRepBean();
        rep.setYssPub(pub);
        try {
            rep.parseRowStr(requestParams);
            rep.getReportData("");
        } catch (YssException ex) {
            throw new YssException("生成SUMMARY报表所需的依赖报表出现异常！", ex);
        }
    }
}

