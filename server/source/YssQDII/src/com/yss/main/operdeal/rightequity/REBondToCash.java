package com.yss.main.operdeal.rightequity;

import java.sql.*;
import java.util.*;

import com.yss.main.operdata.*;
import com.yss.main.operdeal.*;
import com.yss.main.parasetting.*;
import com.yss.manager.TradeDataAdmin;
import com.yss.pojo.cache.*;
import com.yss.util.*;

/**
 *
 * <p>Title: REBondToCash</p>
 * <p>Description:计算债券兑付  </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */
public class REBondToCash
    extends BaseRightEquity {
    public REBondToCash() {
    }

    public ArrayList getDayRightEquitys(java.util.Date dDate,String sPortCode) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        double dSecurityAmount = 0; //证券数量
        double dSecurityCost = 0; //证券成本
        //double dRight = 0; //权益（主表）
        double dRightSub = 0; //权益（子表）
        String strRightType = ""; //权益类型
        String strSubRightType = ""; //权益类型
        String strCashAccCode = " "; //现金帐户
        //String strNumDate = "";
        String strYearMonth = "";
        CashAccountBean caBean = null;
        TradeSubBean subTrade = null;
        Connection conn = dbl.loadConnection();
        double dBaseRate = 1;
        double dPortRate = 1;
        boolean bTrans = false;
        boolean analy1;
        boolean analy2;
        boolean analy3;
        ArrayList reArr = new ArrayList();
        YssCost cost = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B start---//
        	TradeDataAdmin tradeData = new TradeDataAdmin();//交易数据操作类
        	tradeData.setYssPub(pub);
        	tradeData.delete("", dDate,dDate,null,null,"",sPortCode, "", 
        			"",YssOperCons.Yss_JYLX_ZQ, "", "",false, "HD_QY");
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B end---//
            
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.
                getOperDealCtx().getBean("cashacclinkdeal");
            operFun.setYssPub(pub);
            strYearMonth = YssFun.left(this.strOperStartDate, 4) + "00";
            strRightType = YssOperCons.Yss_JYLX_ZQ;
            //strCurDate = YssFun.formatDate(dDate); //设置当前日期，赋予用作删除条件的Bean
            //YssType lAmount = new YssType();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            //---------------------------------操作子表-------------------------------------
            strSql =
                " select a.*,b.*,c.*,d.*,e.*,f.* from " +
                " (select FsecurityCode,FStorageDate,FPortCode,FStorageCost," +
                "  FMStorageCost,FVStorageCost," +
                "  FPortCuryCost,FMPortCuryCost," +
                "  FVPortCuryCost,FBaseCuryCost," +
                "  FMBaseCuryCost,FVBaseCuryCost," +
                "  FStorageAmount  from " +
                pub.yssGetTableName("tb_stock_security") +
                "  where FPortCode in (" + operSql.sqlCodes(sPortCode) +
                ")" +
                "  and FYearMonth<>'" + strYearMonth +
                /*"'  and FsecurityCode=" +
                 dbl.sqlString(rs.getString("FsecurityCode")) +*/
                "'  and FStorageDate=" +
                //dbl.sqlDate(rs.getDate("FStorageDate")) +
                dbl.sqlDate(dDate) +
                " and FCheckState=1" +
                ")a join" +
                " (select FPortCode,FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " )b on b.FPortCode=a.FPortCode" +
                "  and a.FPortCode=b.FPortCode" +
                "  left join(select FsecurityCode,FPortCode,FStorageDate," +
                "  FBal, FMBal, FVBal," +
                "  FPortCuryBal,FMPortCuryBal," +
                "  FVPortCuryBal,FBaseCuryBal, " +
                "  FMBaseCuryBal,FVBaseCuryBal" +
                "  from " + pub.yssGetTableName("tb_stock_secrecpay") +
                "  where FPortCode in (" + operSql.sqlCodes(sPortCode) +
                ")" +
                "  and FYearMonth<>'" + strYearMonth +
                " ' and FCheckState=1" +
                "  and FTsfTypeCode=" + dbl.sqlString("06") +
                "  and FSubTsfTypeCode=" + dbl.sqlString("06FI") +
                //"  and FsecurityCode=" +
                //dbl.sqlString(rs.getString("FsecurityCode")) +
                "  and FStorageDate=" +
                //dbl.sqlDate(YssFun.addDay(rs.getDate("FStorageDate"),-1)) +
                dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                "  )c on c.FsecurityCode=a.FsecurityCode and c.FPortCode=a.FPortCode" +
                "  join " +
                " (select FsecurityCode,FFaceValue,FInsCashDate from " +
                pub.yssGetTableName("tb_para_fixinterest") +
                //" where FInsCashDate between " +
                //dbl.sqlDate(this.strOperStartDate) + " and " +
                //dbl.sqlDate(this.strOperEndDate) +
                " where FInsCashDate = " + dbl.sqlDate(dDate) +
                " and FCheckState=1" +
                ")d on d.FsecurityCode=a.FsecurityCode " +
                " join (select FsecurityCode,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " )e on e.FsecurityCode=a.FsecurityCode" +
                " join " +
                " (select  FsecurityCode,FPortCode,FStorageDate" +
                (analy1 ? ",FAnalysisCode1" : " ") +
                (analy2 ? ",FAnalysisCode2 " : " ") +
                (analy3 ? ",FAnalysisCode3 " : " ") +
                "  from " + pub.yssGetTableName("tb_stock_security") +
                " )f on f.FsecurityCode=a.FsecurityCode and f.FPortCode=a.FPortCode" +
                " and f.FStorageDate=a.FStorageDate";
            rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            if (rs.next()) {
                cashacc.setYssPub(pub);
                cashacc.setLinkParaAttr( (analy1 ?
                                          rs.getString("FAnalysisCode1") :
                                          " "),
                                        rs.getString("FPortCode"),
                                        rs.getString("FSecurityCode"),
                                        (analy2 ?
                                         rs.getString("FAnalysisCode2") :
                                         " "),
                                        strRightType,
                                        YssFun.addDay(rs.getDate(
                                            "FInsCashDate"), -1));
                rs.beforeFirst();
                while (rs.next()) {
                    subTrade = new TradeSubBean();
                    /*double portCuryRate = this.getSettingOper().getCuryRate(rs.
                          getDate("FInsCashDate"),
                          rs.getString("FPortCury"),
                          rs.getString("FPortCode"),
                          YssOperCons.YSS_RATE_PORT);
                     double baseCuryRate = this.getSettingOper().getCuryRate(rs.
                          getDate("FInsCashDate"),
                          rs.getString("FTradeCury"),
                          rs.getString("FPortCode"),
                          YssOperCons.YSS_RATE_BASE);*/
                    //屏蔽了旧的获取汇率的方法。 sj
                    double portCuryRate = this.getSettingOper().
                        getCuryRate(rs.
                                    getDate("FInsCashDate"),
                                    rs.getString("FTradeCury"),
                                    rs.getString("FPortCode"),
                                    YssOperCons.YSS_RATE_PORT,
                                    pub.getPortBaseCury(rs.getString("FPortCode")));// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A

                    PortfolioBean port = new PortfolioBean();
                    port.setPortCode(sPortCode.length() > 0 ?
                                     sPortCode : rs.getString("FPortCode"));
                    port.setYssPub(pub);
                    port.getSetting();

                    double baseCuryRate = this.getSettingOper().
                        getCuryRate(rs.
                                    getDate("FInsCashDate"),
                                    rs.getString("FTradeCury"),
                                    rs.getString("FPortCode"),
                                    YssOperCons.YSS_RATE_BASE,
                                    port.getCurrencyCode());

                    String strNumDate = YssFun.formatDatetime(dDate).
                        substring(0, 8);
                    strNumDate = strNumDate +
                        dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Trade"),
                                               dbl.sqlRight("FNUM", 6), "800000",//将000000改为800000 债券兑付编号调整为800000开始  QDV4中保2010年07月02日01_B 合并太平版本代码调整
                                               " where FNum like 'T"
                                               + strNumDate + "8%'", 1);//改为8% 债券兑付编号调整为800000开始  QDV4中保2010年07月02日01_B 合并太平版本代码调整
                    strNumDate = "T" + strNumDate;
                    strNumDate = strNumDate +
                        dbFun.getNextInnerCode(pub.yssGetTableName(
                            "Tb_Data_SubTrade"),
                                               dbl.sqlRight("FNUM", 5), "00000",
                                               " where FNum like '"
                                               + strNumDate.replaceAll("'", "''") +
                                               "%'");
                    caBean = cashacc.getCashAccountBean();
                    if (caBean != null) {
                        strCashAccCode = caBean.getStrCashAcctCode();
                    }
                    subTrade.setNum(strNumDate);

                    subTrade.setSecurityCode(rs.getString("FSecurityCode"));

                    subTrade.setPortCode(rs.getString("FPortCode"));

                    if (analy1) {
                        subTrade.setInvMgrCode(rs.getString("FAnalysisCode2"));
                    } else {
                        subTrade.setInvMgrCode(" ");
                    }
                    if (analy2) {
                        subTrade.setBrokerCode(rs.getString("FAnalysisCode2"));
                    } else {
                        subTrade.setBrokerCode(" ");
                    }

                    subTrade.setTradeCode(strRightType);

                    subTrade.setTailPortCode(strCashAccCode);

                    subTrade.setAllotProportion(0);

                    subTrade.setOldAllotAmount(0);

                    subTrade.setAllotFactor(0);

                    subTrade.setBargainDate(YssFun.formatDate(rs.getDate(
                        "FInsCashDate")));

                    subTrade.setBargainTime("00:00:00");

                    subTrade.setSettleDate(YssFun.formatDate(rs.getDate(
                        "FInsCashDate")));

                    subTrade.setSettleTime("00:00:00");

                    subTrade.setAutoSettle(new Integer(1).toString());

                    subTrade.setPortCuryRate(portCuryRate);

                    subTrade.setBaseCuryRate(baseCuryRate);

                    subTrade.setTradeAmount(rs.getDouble("FStorageAmount"));

                    subTrade.setTradePrice(rs.getDouble("FFaceValue"));

                    subTrade.setTradeMoney(rs.getDouble("FStorageAmount") *
                                           rs.getDouble("FFaceValue"));

                    subTrade.setAccruedInterest(rs.getDouble("FBal"));

                    cost = new YssCost();
                    cost.setCost(rs.getDouble("FStorageCost"));

                    cost.setMCost(rs.getDouble("FMStorageCost"));

                    cost.setVCost(rs.getDouble("FVStorageCost"));

                    cost.setBaseCost(rs.getDouble("FBaseCuryCost"));

                    cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));

                    cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));

                    cost.setPortCost(rs.getDouble("FPortCuryCost"));

                    cost.setPortMCost(rs.getDouble("FMPortCuryCost"));

                    cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
                    subTrade.setCost(cost);

                    subTrade.setDataSource(0);

                    subTrade.checkStateId = 1;

                    subTrade.creatorCode = pub.getUserCode();

                    subTrade.checkTime = YssFun.formatDatetime(new java.util.Date());

                    subTrade.checkUserCode = pub.getUserCode();

                    subTrade.creatorTime = YssFun.formatDatetime(new java.util.Date());

                    subTrade.setTotalCost(YssD.add(YssD.mul(rs.getDouble(
                        "FStorageAmount"), rs.getDouble("FFaceValue")),
                        rs.getDouble("FBal")));

                    subTrade.setFactSettleDate(YssFun.formatDate(rs.getDate(
                        "FInsCashDate")));

                    subTrade.setFactCashAccCode(strCashAccCode);

                    subTrade.setFactSettleMoney(rs.getDouble("FStorageAmount") *
                                                rs.getDouble("FFaceValue"));

                    subTrade.setExRate(1);

                    subTrade.setFactPortRate(portCuryRate);

                    subTrade.setFactBaseRate(baseCuryRate);

                    reArr.add(subTrade);
                }
                strDealInfo = "true";
            } else {
                strDealInfo = "no";
            }
            return reArr;
        } catch (Exception e) {
            strDealInfo = "false";
            throw new YssException("计算债券兑付数据出错" + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void saveRightEquitys(ArrayList alRightEquitys,java.util.Date dDate,String sPortCode) throws YssException {
        super.saveRightEquitys(alRightEquitys,dDate,sPortCode);
    }

    public TradeBean filterBean(java.util.Date dDate,String sPortCode) {
        TradeBean trade = new TradeBean();
        trade.setTradeCode(YssOperCons.Yss_JYLX_ZQ);
        trade.setPortCode(sPortCode);
        trade.setBargainDate(dDate.toString());
        return trade;
    }
}
