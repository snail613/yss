package com.yss.main.operdeal;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.basesetting.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;
import java.math.BigDecimal;

public class BaseTradeDeal
    extends BaseBean implements ITradeOperation {
    private String securityCode;
    private String brokerCode;
    private String userCode;
    private String subType; //拆分类型 2008.02.25 蒋锦 添加
    private java.util.Date dDate;
    private java.util.Date storageDate;
    private BigDecimal dHandAmount; //改为和数据库decimal匹配的类型 MS00525:QDV4赢时胜（上海）2009年6月21日01_B modify by sunkey 20090703
    private String sCatCode;
    private String sOrderNum;
    private String sExchangeCode;
    private String sDvpInd;
    private String sBaseCuryCode;
    private double baseCuryRate;
    private double sumAmountInStorage;
    private double sumMoneyInStorage;
    private double sumAmountInTrade;
    private double sumMoneyInTrade;
    private double sumMoneyInSettle;
    private double sumNetValue; //所有组合净值 2008.02.25 蒋锦 添加
    private double factor;
    private String tailPortCode; //尾差帐户 2008.03.03 蒋锦 添加
    private String tradeType;
    private String sqlAllPortCode;
    private String cashInvmgrField;
    private String cashCatField;
    private String secInvmgrField;
    private String secBrokerField;
    private String allPortCodeAry[] = null;
    private HashMap hmSecurityAmountInPort; //证券在组合中的持有量 2008.03.03 蒋锦 添加
    private HashMap hmSecurityAmountInStorage;
    private HashMap hmSecurityMoneyInStorage;
    private HashMap hmSecurityAmountInTrade;
    private HashMap hmSecurityMoneyInTrade;
    private HashMap hmSecurityMoneyInSettle;
    private HashMap hmPortNetValue;

    private Hashtable htPortinfo; //处理 MS00125
    private HashMap hmSecurityAllAmountInTrade; //查单支证券当前几个组合下的所有发生额数量 MS00125
    private HashMap hmSecurityAllAmountInStorage; //获取单支证券的前日的所有库存数量 MS00125
    private double dSecurityFreezeAmount = 0; //到当日的冻结数量 MS00125

//   private boolean analyCashInvmgr;
//   private boolean analyCashCat;
//   private boolean analySecInvmgr;
//   private boolean analySecBroker;

   private String sTradeCuryCode = "";
   //================中保的交易拆分问题 by leeyu 20090727 QDV4中保2009年07月27日04_B MS00586
   private boolean bBaseMoney=true;//添加币种计算类型，默认为按基础货币金额计算 
   private String sCashAcctCode="";//添加现金帐户字段，目的是用于计算当前帐户金额时用 
   public void setBaseMoneyType(boolean baseMoneyType){
      this.bBaseMoney = baseMoneyType;
   }
   public void setCashAcctCode(String cashAcctCode){
      this.sCashAcctCode = cashAcctCode;
   }
   //====================by leeyu 20090727 QDV4中保2009年07月27日04_B MS00586
   public void setSTradeCuryCode(String sTradeCuryCode) {
      this.sTradeCuryCode = sTradeCuryCode;
   }

    public String getSTradeCuryCode() {
        return sTradeCuryCode;
    }

    public BaseTradeDeal() {
    }

    /**
     * initTradeOperation
     *
     * @param sSecurityCode String
     * @param sUserCode String
     * @param dDate Date
     * @param sSubType String: 蒋锦 2008-02-02 添加 拆分方式
     */
    public void initTradeOperation(String sSecurityCode, String sBrokerCode,
                                   String sUserCode,
                                   String sTradeType, String sOrderNum,
                                   Date dDate, String sSubType, String sPortCode) throws
        YssException {
        this.securityCode = sSecurityCode;
        this.brokerCode = sBrokerCode;
        this.userCode = sUserCode;
        this.dDate = dDate;
        this.tradeType = sTradeType;
        this.sOrderNum = sOrderNum;
        //蒋锦 2008.02.25 添加 拆分类型
        this.subType = sSubType;
        this.storageDate = loadStorageDate(this.dDate);

        SecurityBean security = new SecurityBean();
        security.setYssPub(pub);
        security.setSecurityCode(this.securityCode);
        security.getSetting();
        this.sTradeCuryCode = security.getStrTradeCuryCode();

        factor = security.getDblFactor();
        dHandAmount = security.getHandAmount();
        sCatCode = security.getCategoryCode();
        sExchangeCode = security.getExchangeCode();
        sBaseCuryCode = security.getTradeCuryCode();
        baseCuryRate = this.getSettingOper().getCuryRate(dDate, sBaseCuryCode, "", YssOperCons.YSS_RATE_BASE);
        this.cashInvmgrField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_InvMgr);
        this.cashCatField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_CatType);
        this.secInvmgrField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
        this.secBrokerField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);

        ExchangeBean exchange = new ExchangeBean();
        exchange.setYssPub(pub);
        exchange.setStrExchangeCode(this.sExchangeCode);
        exchange.getSetting();
        this.sDvpInd = exchange.getStrDvpInd();
        //因为是0比较，直接取intvalue MS00525:QDV4赢时胜（上海）2009年6月21日01_B modify by sunkey 20090703
        //edit by songjie 2011.12.06 报空指针异常
        if (dHandAmount == null || (dHandAmount != null && dHandAmount.intValue() == 0)) {
            dHandAmount = new BigDecimal(1);
        }
        getAllPortCode(sPortCode);
        if (allPortCodeAry.length > 0) {
            String sPort = "";
            for (int i = 0; i < allPortCodeAry.length; i++) {
                sPort += allPortCodeAry[i] + ",";
            }
            if (sPort.endsWith(",")) {
                sPort = sPort.substring(0, sPort.length() - 1);
            }
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            htPortinfo = pubPara.getBondShareInfo(sPort);
        }

    }

    /**
     *
     * @param sSecurityCode String
     * @param sBrokerCode String
     * @param sUserCode String
     * @param sTradeType String
     * @param sOrderNum String
     * @param dDate Date
     * @param sSubType String: 蒋锦 2008-02-02 添加 拆分方式
     * @throws YssException
     */
    public void initTradeOperation(String sSecurityCode, String sBrokerCode,
                                   String sUserCode,
                                   String sTradeType, String sOrderNum,
                                   Date dDate, String sSubType) throws YssException {

        initTradeOperation(sSecurityCode, sBrokerCode, sUserCode,
                           sTradeType, sOrderNum,
                           dDate, sSubType, "");

    }

    public void loadTradeData() throws YssException {
        setAllPortSumAmountInStorage();
        setAllPortSumMoneyInStorage();
        setAllPortAmountInTrade();
        setAllPortMoneyInTrade();
        //2008.03.03 添加 蒋锦 设置某只证券在各个组合中的数量，不使用分析代码作为条件
        setSecurityAmountInPort();
        setPortSecurityAmountInStorage();
        setPortMoneyInStorage();
        setPortAmountInTrade();
        setPortMoneyInTrade();
        this.setAllPortMoneyInSettle();
        this.setPortMoneyInSettle();
        //2008.02.25 添加 蒋锦 获取所有组合的合计净值
        this.setAllPortSumNetValue();
        //2008.02.25 添加 蒋锦 获取各个组合的净值
        this.setPortNetValue();
    }

    protected java.util.Date loadStorageDate(java.util.Date dtDate) throws
        YssException {
        if (YssFun.formatDate(dtDate, "MMdd").equals("0101")) {
            return dtDate;
        } else {
            return YssFun.addDay(dtDate, -1);
        }
    }

    protected void setAllPortSumAmountInStorage() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        StringBuffer buf = new StringBuffer(); //MS00125
        String sBuf = ""; //MS00125
        try {
            for (int i = 0; i < allPortCodeAry.length; i++) {
                if (htPortinfo == null) {
                    break;
                }
                if (allPortCodeAry[i].length() == 0) {
                    continue;
                }
                if (htPortinfo.get(allPortCodeAry[i]) != null && (Boolean.valueOf(htPortinfo.get(allPortCodeAry[i]).toString()).booleanValue()) == true) {
                    buf.append(" when ").append(dbl.sqlString(allPortCodeAry[i])).append(" then -FFreezeAmount ");
                }
            }
            sBuf = buf.toString();
            if (sBuf.length() > 0) {
                sBuf = "sum(case FPortCode " + sBuf;
                sBuf = sBuf + " else 0 end ) as FFreezeAmount ";
            } //添加所选组合的冻结数量处理 MS00125 by leeyu
            //strSql = "select sum(FStorageAmount) as FSumAmount from " +
            strSql = "select sum(FStorageAmount) as FSumAmount, " + (sBuf.length() > 0 ? sBuf : "0 as FFreezeAmount") + " from " +
                pub.yssGetTableName("Tb_Stock_Security") + " a " +
                " where 1 = 1 " +
                (this.secInvmgrField.length() != 0 ?
                 (" and a." + this.secInvmgrField + " = " +
                  dbl.sqlString(this.userCode)) : " ") +
                (this.secBrokerField.length() != 0 ?
                 (" and a." + this.secBrokerField + " = " +
                  dbl.sqlString(this.brokerCode)) : " ") +
                " and a.FSecurityCode = " + dbl.sqlString(securityCode) +
                " and " + operSql.sqlStoragEve(this.dDate);
				  //QDV4中保2009年07月02日01_B MS00558 by leeyu 20090702 添加选择的组合进去处理
                  strSql += " and a.FPortCode in (" + sqlAllPortCode +
                        ") and FCheckState = 1";//添加选择的组合进去处理 不要屏掉此行代码
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.sumAmountInStorage = rs.getDouble("FSumAmount");
                this.sumAmountInStorage = this.sumAmountInStorage + rs.getDouble("FFreezeAmount"); //MS00125 add by leeyu 总的数量减掉冻结的数量
            }
        } catch (YssException e) {
            throw new YssException("获取投资经理【" + userCode + "】所有可用证券库存出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取投资经理【" + userCode + "】所有可用证券库存出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 设置某只证券在各个组合中的数量，不使用分析代码作为条件
     * 2008-03-03 创建 蒋锦
     * @throws YssException
     */
    protected void setSecurityAmountInPort() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        Double dAmount = null;
        this.hmSecurityAmountInPort = new HashMap();
        StringBuffer buf = new StringBuffer(); //MS00125
        String sBuf = ""; //MS00125
        try {
            for (int i = 0; i < allPortCodeAry.length; i++) {
                if (htPortinfo == null) {
                    break;
                }
                if (allPortCodeAry[i].length() == 0) {
                    continue;
                }
                if (htPortinfo.get(allPortCodeAry[i]) != null && (Boolean.valueOf(htPortinfo.get(allPortCodeAry[i]).toString()).booleanValue()) == true) {
                    buf.append(" when ").append(dbl.sqlString(allPortCodeAry[i])).append(" then -FFreezeAmount ");
                }
            }
            sBuf = buf.toString();
            if (sBuf.length() > 0) {
                sBuf = "sum(case FPortCode " + sBuf;
                sBuf = sBuf + " else 0 end ) as FFreezeAmount ";
            } //添加所选组合的冻结数量处理 MS00125 by leeyu
            strSql = "SELECT SUM(FStorageAmount) AS FSumAmount," + (sBuf.length() > 0 ? sBuf : "0 as FFreezeAmount") + ", FPortCode FROM " + pub.yssGetTableName("Tb_Stock_Security") + " a " +
                " WHERE a.FSecurityCode = " + dbl.sqlString(this.securityCode) +
                " AND a.FCheckState = 1 " +
                " AND " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00' " +
                " AND " + operSql.sqlStoragEve(this.dDate) +
                " AND a.FPortCode IN (" + operSql.sqlCodes(this.sqlAllPortCode) + ")" + //这里用这个方法添加分隔符
                " GROUP BY FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                hmSecurityAmountInPort.put(rs.getString("FPortCode"),
                                           new Double(rs.getDouble("FSumAmount") +
                    rs.getDouble("FFreezeAmount") +
                    getPortAmounts(rs.getString("FPortCode"), false))); //除减掉前日冻结数量外还要加上当日到帐日的送股权益的冻结数量
            }
        } catch (Exception e) {
            throw new YssException("设置某只证券在各个组合中的数量出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //设置某只证券在各个组合上的数量
    protected void setPortSecurityAmountInStorage() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        StringBuffer buf = new StringBuffer(); //MS00125
        String sBuf = ""; //MS00125
        try {
            for (int i = 0; i < allPortCodeAry.length; i++) {
                if (htPortinfo == null) {
                    break;
                }
                if (allPortCodeAry[i].length() == 0) {
                    continue;
                }
                if (htPortinfo.get(allPortCodeAry[i]) != null && (Boolean.valueOf(htPortinfo.get(allPortCodeAry[i]).toString()).booleanValue()) == true) {
                    buf.append(" when ").append(dbl.sqlString(allPortCodeAry[i])).append(" then -FFreezeAmount ");
                }
            }
            sBuf = buf.toString();
            if (sBuf.length() > 0) {
                sBuf = "sum(case FPortCode " + sBuf;
                sBuf = sBuf + " else 0 end ) as FFreezeAmount ";
            } //添加所选组合的冻结数量处理 MS00125 by leeyu
            hmSecurityAmountInStorage = new HashMap();
            strSql = "select a.FPortCode," + (sBuf.length() > 0 ? sBuf : "0 as FFreezeAmount") + ", sum(FStorageAmount) as FAmount from " +
                pub.yssGetTableName("Tb_Stock_Security") + " a " +
                " where 1=1 " +
                (this.secInvmgrField.length() != 0 ?
                 (" and a." + this.secInvmgrField + " = " +
                  dbl.sqlString(this.userCode)) : " ") +
                (this.secBrokerField.length() != 0 ?
                 (" and a." + this.secBrokerField + " = " +
                  dbl.sqlString(this.brokerCode)) : " ") +
                " and a.FSecurityCode = " + dbl.sqlString(this.securityCode) +
                " and " + operSql.sqlStoragEve(this.dDate);
            strSql += " and a.FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //这里用这个方法添加分隔符
                ") and FCheckState = 1" +
                " group by a.FPortCode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                hmSecurityAmountInStorage.put(rs.getString("FPortCode"),
                                              //new Double(rs.getDouble("FAmount")));
                                              new Double(rs.getDouble("FAmount") + rs.getDouble("FFreezeAmount") +
                    getPortAmounts(rs.getString("FPortCode"), true))); //除减掉前日冻结数量外还要加上当日到帐日的送股权益的冻结数量

            }
        } catch (YssException e) {
            throw new YssException("获取投资经理【" + userCode + "】在各投资组合可用证券库存出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取投资经理【" + userCode + "】在各投资组合可用证券库存出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void setAllPortSumMoneyInStorage() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "select sum(FBaseCuryBal*" +
                //fanghaoln 20090616 MS00508 QDV4中保2009年06月15日01_B 统计现金的时候要过虑虚拟账户0411-0415
                "(CASE WHEN b.FSubAccType = '0402' THEN -1 " +
                " when  b.FSubAccType in('0411','0412','0413','0414','0415') then 0 " +
                " ELSE 1 END" + //为应付帐户设置现金流方向
                //---------------------------------------------------------------------------------------------------------------------------------
                ")) as FSumAccBalance from " +
                pub.yssGetTableName("Tb_Stock_Cash") + " a " +
                //----------------------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    
                
                " left join (select FCashAccCode, FSubAccType, FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where  FCheckState = 1 ) b on a.FCashAccCode = b.FCashAccCode " +
             
                //end by lidaoolong
                //-----------------------------------------------------------------------------------------------------------------
                " where 1=1 " +
                (this.cashInvmgrField.length() != 0 ? (" and a." + this.cashInvmgrField + " = " +
                dbl.sqlString(this.userCode)) : " ") +
                (this.cashCatField.length() != 0 ? (" and a." + this.cashCatField + " = " +
                dbl.sqlString(this.sCatCode)) : " ") +
                " and " + operSql.sqlStoragEve(this.dDate);
            strSql += " and a.FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //这里用这个方法添加分隔符
                ") and FCheckState = 1 ";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.sumMoneyInStorage = rs.getDouble("FSumAccBalance");
            }
        } catch (Exception e) {
            throw new YssException("获取投资经理【" + userCode + "】所有可用现金库存出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //设置某只证券在各个组合上的金额
    //sum(round(FAccBalance*FBaseCuryRate,2)
    protected void setPortMoneyInStorage() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            hmSecurityMoneyInStorage = new HashMap();
            strSql = "select a.FPortCode, sum(FBaseCuryBal*" +
                //fanghaoln 20090616 MS00508 QDV4中保2009年06月15日01_B 统计现金的时候要过虑虚拟账户0411-0415
                // "CASE WHEN b.FSubAccType = '0402' THEN -1 ELSE 1 END" + //为应付帐户设置现金流方向
                "CASE WHEN b.FSubAccType = '0402' THEN -1 " +
                " when  b.FSubAccType in('0411','0412','0413','0414','0415') then 0 " +
                " ELSE 1 END" + //为应付帐户设置现金流方向
                //---------------------------------------------------------------------------------------------------------------------------------
                ") as FAccBalance from " +
                pub.yssGetTableName("Tb_Stock_Cash") + " a " +
                //----------------------------------------------------------------------------------------------------------------
                " left join (select bb.* from (select FCashAccCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FCashAccCode) ba join (select FCashAccCode, FSubAccType, FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                ") bb on ba.FCashAccCode = bb.FCashAccCode and ba.FStartDate = bb.FStartDate) b on a.FCashAccCode = b.FCashAccCode " +
                //-----------------------------------------------------------------------------------------------------------------
                " where 1=1 " +
                (this.cashInvmgrField.length() != 0 ? (" and a." + this.cashInvmgrField + " = " +
                dbl.sqlString(this.userCode)) : " ") +
                (this.cashCatField.length() != 0 ? (" and a." + this.cashCatField + " = " +
                dbl.sqlString(this.sCatCode)) : " ") +
                " and " + operSql.sqlStoragEve(this.dDate);
            strSql += " and a.FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //这里用个方法添加分隔符
                ") and FCheckState = 1" +
                " group by a.FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                hmSecurityMoneyInStorage.put(rs.getString("FPortCode"),
                                             new Double(rs.getDouble("FAccBalance")));
            }
        } catch (YssException e) {
            throw new YssException("获取投资经理【" + userCode + "】在各投资组合可用现金库存出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取投资经理【" + userCode + "】在各投资组合可用现金库存出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //MS00125 实现接口中的方法 by leeyu 2009-01-05
    public double getSecurityFreezeAmount() throws YssException {
        return dSecurityFreezeAmount;
    }

    protected void setAllPortMoneyInTrade() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "select sum(FTotalCost*FCashInd) as FSumTradeMoney from " +
                pub.yssGetTableName("Tb_Data_SubTrade") + " a" +
                " left join (select FTradeTypeCode, FCashInd from " +
                " Tb_Base_TradeType where FCheckState = 1) b " +
                " on a.FTradeTypeCode = b.FTradeTypeCode " +
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FCatCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) c on a.FSecurityCode = c.FSecurityCode " +
                " where FInvMgrCode = " + dbl.sqlString(this.userCode) +
                " and c.FCatCode = " + dbl.sqlString(this.sCatCode) +
                (sOrderNum.length() == 0 ? " " : " and a.FOrderNum <> " + dbl.sqlString(this.sOrderNum)) + //修改，当sOrderNum的长度为0时就不用做删除条件了by leeyu 2009-1-5MS00125
                " and a.FSettleState = 0 " + //读取交易数据拆分表中未结算的金额
                (this.securityCode.length() == 0 ? " " : " and a.FSecurityCode=" + dbl.sqlString(this.securityCode)) + //添加上证券条件 QDV4中保2009年03月13日03_B MS00317 by leeyu 20090416
                " and a.FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //这里用这个方法添加分隔符
                ") and a.FBargainDate <= " +
                dbl.sqlDate(this.dDate) + " and a.FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.sumMoneyInTrade = rs.getDouble("FSumTradeMoney"); //需要乘汇率，并round到小数点后两位(待修正)
            }
        } catch (YssException e) {
            throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                                   " 投资经理【" + userCode + "】所有未结算成交金额出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                    " 投资经理【" + userCode + "】所有未结算成交金额出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void setAllPortMoneyInSettle() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "select sum(FMoney*FInOut) as FSumTradeMoney from (" +
                " select FTransDate,FTransferDate, FMoney, FSecurityCode,FCheckState,FPortCode" +
                (this.cashInvmgrField.length() != 0 ? ("," + this.cashInvmgrField) : " ") +
                (this.cashCatField.length() != 0 ? ("," + this.cashCatField) : " ") +
                ",FInOut from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " a1 left join (select (FMoney*FBaseCuryRate) as FMoney, FInOut,FNum,FPortCode" +
                (this.cashInvmgrField.length() != 0 ? ("," + this.cashInvmgrField) : " ") +
                (this.cashCatField.length() != 0 ? ("," + this.cashCatField) : " ") +
                " from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FCheckState = 1) a2 on a1.FNum = a2.FNum" +
                ") a " +
				" where 1=1 " +
                (this.cashInvmgrField.length() != 0 ? (" and " + this.cashInvmgrField + " = " +
                dbl.sqlString(this.userCode)) : " ") +
                (this.cashCatField.length() != 0 ? (" and " + this.cashCatField + " = " +
                dbl.sqlString(this.sCatCode)) : " ") +
                " and FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //这里用这个方法添加分隔符
                //") and FTransDate = " +
                //dbl.sqlDate(this.dDate) +
                " ) and (" + dbl.sqlDate(this.dDate) + " between " +
                " FTransDate and FTransferDate)" +
                " and FCheckState = 1"; //?
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.sumMoneyInSettle = rs.getDouble("FSumTradeMoney"); //需要乘汇率，并round到小数点后两位(待修正)
            }
//         this.sumMoneyInSettle = this.getSettingOper().converMoney(
//               sBaseCuryCode,
//               pub.getBaseCury(), sumMoneyInTrade, baseCuryRate, 1);
        } catch (YssException e) {
            throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                                   " 投资经理【" + userCode + "】所有未结算成交金额出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                    " 投资经理【" + userCode + "】所有未结算成交金额出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void setPortMoneyInTrade() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sPortCode = "";
        try {
            hmSecurityMoneyInTrade = new HashMap();
            strSql =
                "select FPortCode, sum(FTotalCost*FCashInd) as FSumTradeMoney from " +
                pub.yssGetTableName("Tb_Data_SubTrade") + " a" +
                " left join (select FTradeTypeCode, FCashInd from " +
                " Tb_Base_TradeType where FCheckState = 1) b " +
                " on a.FTradeTypeCode = b.FTradeTypeCode " +
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FCatCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) c on a.FSecurityCode = c.FSecurityCode " +
                " where FInvMgrCode = " + dbl.sqlString(this.userCode) +
                " and c.FCatCode = " + dbl.sqlString(this.sCatCode) +
                (sOrderNum.length() == 0 ? " " : " and a.FOrderNum <> " + dbl.sqlString(this.sOrderNum)) + //MS00125
                " and a.FSettleState = 0 " + //读取交易数据拆分表中未结算的金额
                (this.securityCode.length() == 0 ? " " : " and a.FSecurityCode=" + dbl.sqlString(this.securityCode)) + //添加上证券条件 QDV4中保2009年03月13日03_B MS00317 by leeyu 20090416
                " and a.FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //这里用这个方法添加分隔符
                ") and a.FBargainDate <= " +
                dbl.sqlDate(this.dDate) +
                " and a.FCheckState = 1 group by a.FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sPortCode = rs.getString("FPortCode");
            }
        } catch (YssException e) {
            throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                                   " 投资经理【" + userCode +
                                   "】在投资组合【" + sPortCode + "】上的未结算成交金额出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                    " 投资经理【" + userCode +
                    "】在投资组合【" + sPortCode + "】上的未结算成交金额出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void setPortMoneyInSettle() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sPortCode = "";
        try {
            hmSecurityMoneyInSettle = new HashMap();
            strSql =
                "select FPortCode, sum(FMoney*FInOut) as FSumTradeMoney from (" +
                " select FTransDate,FTransferDate, FMoney, FSecurityCode,FCheckState,FPortCode" +
                (this.cashInvmgrField.length() != 0 ? ("," + this.cashInvmgrField) : " ") +
                (this.cashCatField.length() != 0 ? ("," + this.cashCatField) : " ") +
                ",FInOut from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " a1 left join (select (FMoney*FBaseCuryRate) as FMoney, FInOut,FNum,FPortCode" +
                (this.cashInvmgrField.length() != 0 ? ("," + this.cashInvmgrField) : " ") +
                (this.cashCatField.length() != 0 ? ("," + this.cashCatField) : " ") +
                " from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FCheckState = 1) a2 on a1.FNum = a2.FNum" +
                ") a" +
                " where 1=1 " +
                (this.cashInvmgrField.length() != 0 ? (" and " + this.cashInvmgrField + " = " +
                dbl.sqlString(this.userCode)) : " ") +
                (this.cashCatField.length() != 0 ? (" and " + this.cashCatField + " = " +
                dbl.sqlString(this.sCatCode)) : " ") +
                " and FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //这里用这个方法添加分隔符
//               " ) and (" + dbl.sqlDate(this.dDate) + " between " +
//               " FTransDate and FTransferDate-1)" +
                " ) and (" + dbl.sqlDate(this.dDate) + " between " +
                " FTransDate and FTransferDate)" +
                " and FCheckState = 1 group by FPortCode"; //?
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sPortCode = rs.getString("FPortCode");
            hmSecurityMoneyInSettle.put(sPortCode,
                                       new Double(rs.getDouble("FSumTradeMoney")));//此处代码去掉下面的汇率转换（重复汇率转换），因为已在上面的SQL中转换了。 by leeyu 20090727 QDV4中保2009年07月27日04_B MS00586
         }
        } catch (YssException e) {
            throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                                   " 投资经理【" + userCode +
                                   "】在投资组合【" + sPortCode + "】上的未结算成交金额出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                    " 投资经理【" + userCode +
                    "】在投资组合【" + sPortCode + "】上的未结算成交金额出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void setAllPortAmountInTrade() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql =
                "select sum(FTradeAmount*FAmountInd) as FSumTradeAmount from " +
                pub.yssGetTableName("Tb_Data_SubTrade") + " a" +
                " left join (select FTradeTypeCode, FAmountInd from " +
                " Tb_Base_TradeType where FCheckState = 1) b " +
                " on a.FTradeTypeCode = b.FTradeTypeCode " +
                " where FInvMgrCode = " + dbl.sqlString(this.userCode) +
                " and a.FBrokerCode = " + dbl.sqlString(this.brokerCode) +
                (sOrderNum.length() == 0 ? " " : " and a.FOrderNum <> " + dbl.sqlString(this.sOrderNum)) + //MS00125
                (this.securityCode.length() == 0 ? " " : " and a.FSecurityCode=" + dbl.sqlString(this.securityCode)) + //添加上证券条件 QDV4中保2009年03月13日03_B MS00317 by leeyu 20090416
                " and a.FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //这里用这个方法添加分隔符
                ") and a.FBargainDate = " +
                dbl.sqlDate(this.dDate) + " and a.FCheckState = 1";
            if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) {
                strSql += " and a.FTradeTypeCode in('" + YssOperCons.YSS_JYLX_Buy + "','" + YssOperCons.YSS_JYLX_Sale + "')"; //选择组合的当日交易数据的合计，只取买卖的数据。
            }
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.sumAmountInTrade = rs.getDouble("FSumTradeAmount");
            }
        } catch (Exception e) {
            throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                                   " 投资经理【" + userCode +
                                   "】持有证券【" + securityCode + "】的所有未结算数量出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void setPortAmountInTrade() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sPortCode = "";
        try {
            hmSecurityAmountInTrade = new HashMap();
            strSql =
                "select FPortCode, sum(FTradeAmount*FAmountInd) as FSumTradeAmount from " +
                pub.yssGetTableName("Tb_Data_SubTrade") + " a" +
                " left join (select FTradeTypeCode, FAmountInd from " +
                " Tb_Base_TradeType where FCheckState = 1) b " +
                " on a.FTradeTypeCode = b.FTradeTypeCode " +
                " where FInvMgrCode = " + dbl.sqlString(this.userCode) +
                " and a.FBrokerCode = " + dbl.sqlString(this.brokerCode) +
                (this.securityCode.length() == 0 ? " " : " and a.FSecurityCode=" + dbl.sqlString(this.securityCode)) + //添加上证券条件 QDV4中保2009年03月13日03_B MS00317 by leeyu 20090416
                (sOrderNum.length() == 0 ? " " : " and a.FOrderNum <> " + dbl.sqlString(this.sOrderNum)); //MS00125
            if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) {
                strSql += " and a.FTradeTypeCode in('" + YssOperCons.YSS_JYLX_Buy + "','" + YssOperCons.YSS_JYLX_Sale + "')"; //查找出当日买、卖的业务数据 by leeyu 2008-12-31
            }
            strSql += " and a.FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //MS00125 这里用这个方法添加分隔符
                ") and a.FBargainDate = " + dbl.sqlDate(this.dDate) +
                " and a.FCheckState = 1 group by a.FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sPortCode = rs.getString("FPortCode");
                hmSecurityAmountInTrade.put(sPortCode,
                                            new Double(rs.getDouble(
                                                "FSumTradeAmount")));
            }
        } catch (YssException e) {
            throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                                   " 投资经理【" + userCode +
                                   "】在投资组合【" + sPortCode + "】上持有的证券【" +
                                   securityCode +
                                   "】的未结算数量出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                    " 投资经理【" + userCode +
                    "】在投资组合【" + sPortCode + "】上持有的证券【" +
                    securityCode +
                    "】的未结算数量出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public double getAllPortSumNetValue() {
        return this.sumNetValue;
    }

    /**
     * 获取所有组合的净值
     * 蒋锦 2008-02-25 创建
     * @throws YssException
     */
    protected void setAllPortSumNetValue() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "SELECT SUM(FPortNetValue) AS FNetValue " +
                "FROM " + pub.yssGetTableName("Tb_Data_NetValue") +
                " WHERE FType = '01' " +
                "AND FNAVDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                "AND FPortCode IN(" + operSql.sqlCodes(this.sqlAllPortCode) + ")"; // 这里用这个方法添加分隔符
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.sumNetValue = rs.getDouble("FNetValue");
            }
        } catch (Exception e) {
            throw new YssException("获取所有组合的合计净值出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取各个组合的净值
     * 2008.02.25 蒋锦 创建
     * @throws YssException
     */
    protected void setPortNetValue() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sPortCode = "";
        this.hmPortNetValue = new HashMap();
        try {
            strSql = "SELECT SUM(FPortNetValue) AS FNetValue, FPortCode " +
                "FROM " + pub.yssGetTableName("Tb_Data_NetValue") +
                " WHERE FType = '01' " +
                "AND FNAVDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                "AND FPortCode IN(" + operSql.sqlCodes(this.sqlAllPortCode) + ")" + //这里用这个方法添加分隔符
                " GROUP BY FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sPortCode = rs.getString("FPortCode");
                hmPortNetValue.put(sPortCode, new Double(rs.getDouble("FNetValue")));
            }
        } catch (Exception e) {
            throw new YssException("获取各个组合的净值出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取某一个组合中某只证券占总净值的比例
     * 蒋锦 2008.02.25 创建
     * @param sPortCode String：组合代码
     * @throws YssException
     * @return double：比例
     */
    public double getPortNetValueScale(String sPortCode) throws YssException {
        double dbResult = 0.0;
        double dv1 = 0.0;
        double dv2 = 0.0;
        try {
            dv1 = this.getHave(sPortCode);
            dv2 = this.getAllPortSumNetValue();
            if (dv2 == 0) {
                dbResult = 0;
            } else {
                dbResult = YssD.round(YssD.div(dv1, dv2), 3);
            }
        } catch (Exception e) {
            throw new YssException("获取组合：" + sPortCode + "净值占总净值的比例出错！", e);
        }
        return dbResult;
    }

    /**
     * 从哈希表中提取单个组合的净值
     * 蒋锦 2008.02.25 创建
     * @param sPortCode String：组合编号
     * @throws YssException
     * @return double：净值
     */
    public double getPortNetValue(String sPortCode) throws YssException {
        double dbResult = 0.0;
        try {
            if (this.hmPortNetValue != null) {
                if (this.hmPortNetValue.containsKey(sPortCode)) {
                    dbResult = ( (Double)this.hmPortNetValue.get(sPortCode)).
                        doubleValue();
                }
            } else {
                this.setPortNetValue();
                dbResult = getPortNetValue(sPortCode);
            }
        } catch (Exception e) {
            throw new YssException("获取组合：" + sPortCode + "的净值出错！", e);
        }
        return dbResult;
    }

    /**
     * getPortsSumAmountInStorage
     *
     *
     * @return double
     */
    public double getAllPortSumAmountInStorage() throws YssException {
        return this.sumAmountInStorage;
    }

    /**
     * getPortsSumAmountInTrade
     *
     * @return double
     */
    public double getAllPortSumAmountInTrade() {
        return this.sumAmountInTrade;
    }

    /**
     * getPortAmount
     *
     * @param sPortCode String
     * @return double
     */
    public double getPortAmountInStorage(String sPortCode) throws YssException {
        double dResult = 0;
        if (this.hmSecurityAmountInStorage != null) {
            if (this.hmSecurityAmountInStorage.get(sPortCode) != null) {
                dResult = ( (Double)this.hmSecurityAmountInStorage.get(sPortCode)).
                    doubleValue();
            }
        } else {
            this.setPortSecurityAmountInStorage();
            dResult = getPortAmountInStorage(sPortCode);
        }
        return dResult;
    }

    public double getPortAmountInTrade(String sPortCode) throws YssException {
        double dResult = 0;
        if (this.hmSecurityAmountInTrade != null) {
            if (this.hmSecurityAmountInTrade.get(sPortCode) != null) {
                dResult = ( (Double)this.hmSecurityAmountInTrade.get(sPortCode)).
                    doubleValue();
            }
        } else {
            setPortAmountInTrade();
            dResult = getPortAmountInTrade(sPortCode);
        }
        return dResult;
    }

    /**
     * getPortAmountScale
     *计算某组合数量的整分配比例
     * 用(当前组合的库存+组合发生额)/(当前所勾选的全部组合库存量+组合发生额)
     * @param sPortCode String
     * @return double
     */
    public double getPortAmountScale(String sPortCode) throws YssException {
        double dResult = 0;
        double dv1 = 0;
        double dv2 = 0;
        dv1 = YssD.add(this.getPortAmountInStorage(sPortCode),
                       this.getPortAmountInTrade(sPortCode));
        dv2 = YssD.add(this.getAllPortSumAmountInStorage(),
                       this.getAllPortSumAmountInTrade());
        if (dv2 == 0) {
            dResult = 0;
        } else {
            dResult = YssD.round(YssD.div(dv1, dv2), 3);
        }
        return dResult;
    }

    /**
     * getPortsSumMoneyInStorage
     * 全部组合的库存总量
     * @return double
     */
    public double getAllPortSumMoneyInStorage() {
        return this.sumMoneyInStorage;
    }

    /**
     * getPortsSumMoneyInTrade
     *全部组合的发生额
     * @return double
     */
    public double getAllPortSumMoneyInTrade() {
        return this.sumMoneyInTrade;
    }

    /**
     * getPortMoney
     *获取单个组合的库存金额
     * @param sPortCode String
     * @return double
     */
    public double getPortMoneyInStorage(String sPortCode) throws YssException {
        double dResult = 0;
        if (this.hmSecurityMoneyInStorage != null) {
            if (this.hmSecurityMoneyInStorage.containsKey(sPortCode)) {
                dResult = ( (Double)this.hmSecurityMoneyInStorage.get(sPortCode)).
                    doubleValue();
            }
        } else {
            setPortMoneyInStorage();
            dResult = getPortMoneyInStorage(sPortCode);
        }
        return dResult;
    }

    public double getPortMoneyInTrade(String sPortCode) throws YssException {
        double dResult = 0;
        if (this.hmSecurityMoneyInTrade != null) {
            if (this.hmSecurityMoneyInTrade.containsKey(sPortCode)) {
                dResult = ( (Double)this.hmSecurityMoneyInTrade.get(sPortCode)).
                    doubleValue();
            }
        } else {
            setPortMoneyInTrade();
            dResult = getPortMoneyInTrade(sPortCode);
        }
        return dResult;
    }

    /**
     * getPortMoneyScale
     *计算组合的金额比例
     * 采用库存量的比例计算
     * @param sPortCode String
     * @return double
     */
    public double getPortMoneyScale(String sPortCode) throws YssException {
        double dResult = 0;
        double dv1 = 0;
        double dv2 = 0;
        dv1 = this.getHave(sPortCode);
        dv2 = YssD.add(this.getAllPortSumMoneyInStorage(),
                       this.getAllPortSumMoneyInSettle());
        if (dv2 == 0) {
            dResult = 0;
        } else {
            dResult = YssD.round(YssD.div(dv1, dv2), 3);
        }
        return dResult;
    }

    /**
     * getWipedDistAmount
     * 根据比例取数量
     * @param sPortCode String
     * @return double
     */
    public double getWipedDistAmount(String sPortCode, double dExchangeAmount,
                                     double dTotalAmount,
                                     double dPrice) throws
        YssException {

        double dResult = 0;
        double dSumAmount = 0.0;
        double dHaveAmount = 0; //持有股数
        Double dHaveAmountInHm = null;
        double dHaveCash = 0; //持有现金
        double dRoundOriginDist = 0; //
        double cury = 0;

        if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)) { //买入
            dHaveCash = this.getHave(sPortCode);
            cury = this.getSettingOper().getCuryRate(dDate,
                this.getSTradeCuryCode(),
                sPortCode,
                YssOperCons.YSS_RATE_BASE);
            //按净值拆分 2008.03.02 添加 蒋锦
            //Bug 编号 0000073
            if (this.subType.equalsIgnoreCase("02")) {
                dSumAmount = getSecurityAmountInAllPort(this.sqlAllPortCode, this.securityCode);
                dHaveAmountInHm = ( (Double)this.hmSecurityAmountInPort.get(
                    sPortCode));
                if (dHaveAmountInHm != null) {
                    dHaveAmount = dHaveAmountInHm.doubleValue();
                }
                dExchangeAmount = YssD.sub(YssD.mul(YssD.add(
                    dSumAmount, dTotalAmount),
                    this.getScale(
                        sPortCode, this.tailPortCode)),
                                           dHaveAmount);
            }
            //MS00525:QDV4赢时胜（上海）2009年6月21日01_B modify by sunkey 20090703
            //dHadAmount已调整为BigDecimal类型，所以dExchangeAmount也取BigDecimal类型，降低误差
            dRoundOriginDist = YssD.round(YssD.divD(new BigDecimal(dExchangeAmount),
                dHandAmount), 0);
            //sRoundOriginDist 也调整为BigDecimal类型值
            dRoundOriginDist = YssD.mul(new BigDecimal(dRoundOriginDist), dHandAmount);
            dPrice = YssD.div(dPrice, factor);
            //取模还是使用doubleValue，目前没有较好的方法从两个BigDecimal类型取模
            if ( (dExchangeAmount % dHandAmount.doubleValue()) == 0) {
                if (YssD.sub(YssD.div(dHaveCash, cury),
                             YssD.mul(dRoundOriginDist, dPrice)) <= 0) {
                    dResult = 0;
                } else {
                    dResult = dRoundOriginDist;
                }
            } else {
                if (YssD.sub(YssD.div(dHaveCash, cury),
                             YssD.mul(dRoundOriginDist, dPrice)) <= 0) {
                    dResult = dExchangeAmount;
                } else {
                    dResult = dRoundOriginDist;
                }
            }
            //======================End MS00525===============================
        } else if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) { //卖出
            dHaveAmount = this.getHave(sPortCode);
            if (Math.abs(this.sumAmountInStorage-dTotalAmount)<0.00000000001) {
                dResult = dHaveAmount;
            } else {
                if (dHaveAmount == dExchangeAmount) {
                    dResult = dExchangeAmount;
                } else {
                    //MS00525:QDV4赢时胜（上海）2009年6月21日01_B modify by sunkey 20090703
                    //调整为采用BigDecimal运算，降低误差
                    dResult = YssD.round(YssD.divD(new BigDecimal(dExchangeAmount), dHandAmount), 0);
                    dResult = YssD.mul(new BigDecimal(dResult), dHandAmount);
                    //=========================End MS00525==============================
                }
            }
        }
        return dResult;
    }

    /**
     * getScale
     *计算比例值
     * @return double
     */
    public double getScale(String sPortCode, String sTailPortCode) throws
        YssException {
        double dResult = 0;
        double dTailScale = 0;
        if (sPortCode.equalsIgnoreCase(sTailPortCode)) {
            dTailScale = getTailScale();
        }
        if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)) { //买入
            //按资金比例拆分 2008.02.25 蒋锦
            if (this.subType.equalsIgnoreCase("01")) {
                dResult = this.getPortMoneyScale(sPortCode) + dTailScale;
            }
            //按前一日净值比例拆分
            else if (this.subType.equalsIgnoreCase("02")) {
                dResult = this.getPortNetValueScale(sPortCode) + dTailScale;
            }
        } else if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) { //卖出
            dResult = this.getPortAmountScale(sPortCode) + dTailScale;
        }
        return dResult;
    }

    public double getTailScale() throws YssException {
        int i;
        double dTailScale = 100;
        try {
            for (i = 0; i < allPortCodeAry.length; i++) {
                //按资金比例
                if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)) { //买入
                    if (this.subType.equalsIgnoreCase("01")) {
                        dTailScale -=
                            YssD.mul(this.getPortMoneyScale(allPortCodeAry[i]),
                                     100);
                    }
                    //按前一日资产净值比例
                    else if (this.subType.equalsIgnoreCase("02")) {
                        dTailScale -=
                            YssD.mul(this.getPortNetValueScale(allPortCodeAry[i]),
                                     100);
                    }
                } else if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) { //卖出
                    dTailScale -=
                        YssD.mul(this.getPortAmountScale(allPortCodeAry[i]),
                                 100);
                }
            }
            return dTailScale / 100;
        } catch (Exception e) {
            throw new YssException("");
        }
    }

    /**
     * getTailAmount
     *计算实际数量
     * @param dSumAmout double
     * @return double
     */
    public double getTailAmount(double dSumAmount, double dPrice) throws
        YssException {
        int i;
        double dTailAmount = dSumAmount;
        double dOriginAmount;
        try {
            for (i = 0; i < allPortCodeAry.length; i++) {
                dOriginAmount = getOriginDistAmount(allPortCodeAry[i], dSumAmount);
                dTailAmount -=
                    getWipedDistAmount(allPortCodeAry[i], dOriginAmount,
                                       dSumAmount, dPrice);
            }
            return dTailAmount;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * getHave
    * 获取单个组合的拆分到的数量 按现金比例拆分时为 基础货币金额
    * @return double
     */
    public double getHave(String sPortCode) throws YssException {
        double dResult = 0;
        double dV1 = 0;
        double dV2 = 0;
        double dV3 = 0;
        if (sPortCode.trim().length() > 0) {
            if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)) { //买入
                //按资金比列  2008.02.25 蒋锦
                if (this.subType.equalsIgnoreCase("01")) {
               if(!bBaseMoney){//判断，如果值为假，则重新取一次现金帐户下的原币金额 by leeyu 20090727 QDV4中保2009年07月27日04_B MS00586
                  this.setCuryPortMoneyInStorage();
                  this.setCuryPortMoneyInSettle();
               }
                    dV1 = this.getPortMoneyInStorage(sPortCode);
                    dV3 = this.getPortMoneyInSettle(sPortCode);
                    dResult = YssD.add(YssD.add(dV1, dV2), dV3);
                }
                //按前一日净值比例
                else if (this.subType.equalsIgnoreCase("02")) {
                    dResult = this.getPortNetValue(sPortCode);
                }
            } else if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) { //卖出
                if (sPortCode.equalsIgnoreCase("*allPort*")) { //这个标识表示这里的组合是全部
                    for (int i = 0; i < this.allPortCodeAry.length; i++) {
                        dV1 += this.getSecurityAmountInStorage(allPortCodeAry[i]); //重新写方法
                        dV2 += this.getAllSecurityAmount(allPortCodeAry[i]); //重新写方法
                    }
                } else {
                    sPortCode = sPortCode.replaceAll("'", ""); //这里将单引号去掉。
                    dV1 = this.getSecurityAmountInStorage(sPortCode);
                    dV2 = this.getAllSecurityAmount(sPortCode);
                }
                dResult = YssD.add(dV1, dV2);
            }
        }
        return dResult;
    }

    /**
     * getOriginDistAmount
     *
     * @param sPortCode String
     * @param sTotalAmount double
     * @return double
     */
    public double getOriginDistAmount(String sPortCode, double sTotalAmount) throws
        YssException {
        double dResult = 0;
        if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)) { //买入
            //蒋锦 2008.02.25 修改 按资金比例
            if (this.subType.equalsIgnoreCase("01")) {
                dResult = YssD.mul(this.getPortMoneyScale(sPortCode), sTotalAmount);
                dResult = YssD.round(dResult, 0);
            }
            //按前一日净值比例
            else if (this.subType.equalsIgnoreCase("02")) {
                dResult = YssD.mul(this.getPortNetValueScale(sPortCode), sTotalAmount);
                dResult = YssD.round(dResult, 0);
            }
        } else if (this.tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) { //卖出
            dResult = YssD.mul(this.getPortAmountScale(sPortCode), sTotalAmount);
            dResult = YssD.round(dResult, 0);
        }
        return dResult;
    }

    /**
     * getTailWipedDistAmount
     *
     * @param sPortCode String
     * @param dExchangeAmount double
     * @param dPrice double
     * @return double
     */
    public double getTailWipedDistAmount(String sPortCode, String sTailPortCode,
                                         double dTotalAmount,
                                         double dExchangeAmount, double dPrice) throws
        YssException {
        double dResult = 0;
        double dTailAmount = 0;
        this.tailPortCode = sTailPortCode;
        if (sPortCode.equalsIgnoreCase(sTailPortCode)) {
            dTailAmount = getTailAmount(dTotalAmount, dPrice);
        }
        dResult = this.getWipedDistAmount(sPortCode, dExchangeAmount,
                                          dTotalAmount, dPrice) +
            dTailAmount;
        return dResult;
    }

    /**
     * getAllPortCode
     *
     * @param sPortCode String： in形式组成
     * @throws YssException
     */
    public void getAllPortCode(String sPortCode) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        sqlAllPortCode = "";
        String ReStr = "";
        try {
            strSql =
            	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
          
            	  "select a.FPortCode from (select FPortCode from " +
                  pub.yssGetTableName("Tb_Para_Portfolio") +
                  " where  FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) +
                  " and FCheckState = 1 and FEnabled = 1";
            //end by lidaolong
            if (sPortCode.equalsIgnoreCase("*allport*")) { //MS00125 当前台要用全部组合的话，就不用组合关联表进行关联了
                strSql = strSql + " ) a ";
            } else {
                if (sPortCode.trim().length() > 0) {
                    strSql = strSql + " and FPortCode in (" + sPortCode + ")";
                }
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
       
                strSql = strSql + " ) a join " +
                " (select FPortCode from " +
                pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                " where FRelaType = 'InvestManager'" +
                (this.userCode.length() > 0 ? (" and FSubCode = " + dbl.sqlString(this.userCode)) : " ") + //增加对用户的判断，因为前台有订单时是不将投资经理传过来的MS00125
                " and FCheckState = 1 ) b on a.FPortCode = b.FPortCode ";
         
                //end by lidaolong
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append(rs.getString("FPortCode")).append(",");
                sqlAllPortCode += "'" + rs.getString("FPortCode") + "',";
            }
            if (sqlAllPortCode.length() > 1) {
                sqlAllPortCode = sqlAllPortCode.substring(0,
                    sqlAllPortCode.length() - 1);
            } else {
                sqlAllPortCode = "''";
            }
            ReStr = buf.toString();
            allPortCodeAry = ReStr.split(",");

        } catch (YssException e) {
            throw new YssException("获取可用组合代码出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取可用组合代码出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getAllPortSettleMoney
     *
     * @return double
     */
    public double getAllPortSumMoneyInSettle() {
        return this.sumMoneyInSettle;
    }

    /**
     * getPortMoneyInSettle
     *
     * @param sPortCode String
     * @return double
     */
    public double getPortMoneyInSettle(String sPortCode) throws YssException {
        double dResult = 0;
        if (this.hmSecurityMoneyInSettle != null) {
            if (this.hmSecurityMoneyInSettle.containsKey(sPortCode)) {
                dResult = ( (Double)this.hmSecurityMoneyInSettle.get(sPortCode)).
                    doubleValue();
            }
        } else {
            setPortMoneyInSettle();
            dResult = getPortMoneyInSettle(sPortCode);
        }
        return dResult;
    }

    /**
     * 获取证券总库存
     * 2008-03-03 创建 蒋锦
     * @param sPorts String：IN 格式的组合代码
     * @param sSecurityCode String：证券编号
     * @throws YssException
     * @return double：持有量
     */
    public double getSecurityAmountInAllPort(String sPorts, String sSecurityCode) throws YssException {
        double dbAmount = 0.0;
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "SELECT SUM(FStorageAmount) AS FSumAmount FROM " + pub.yssGetTableName("Tb_Stock_Security") + " a " +
                " WHERE a.FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                " AND a.FCheckState = 1 " +
                " AND " + operSql.sqlStoragEve(this.dDate) +
                " AND a.FPortCode IN (" + sPorts + ")";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dbAmount = rs.getDouble("FSumAmount");
            }
        } catch (Exception e) {
            throw new YssException("获取证券总库存出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dbAmount;
    }

    /**
     * 根据各组合库存信息计算每个组合的当日有送股权益在到帐日的冻结数量
     * @param hmPortValue HashMap
     * @param isAnalysisCodes boolean
     * @throws YssException
     */
    private double getPortAmounts(String sPortCode, boolean isAnalysisCodes) throws YssException {
        boolean bPort = false;
        String sqlStr = "";
        double dResult = 0;
        ResultSet rs = null;
        try {
            if (this.htPortinfo.get(sPortCode) == null) {
                return dResult;
            }
            bPort = Boolean.valueOf(htPortinfo.get(sPortCode).toString()).booleanValue();
            if (bPort == false) {
                return dResult;
            } else {
                sqlStr =
                    "select a.FPortCode,sum(a.FFreezeAmount) as FFreezeAmount from ( " +
                    " select FTSecurityCode as FSecurityCode from " +
                    pub.yssGetTableName("tb_data_bonusshare") +
                    " where FPayDate= " + dbl.sqlDate(dDate) + " and FCheckState=1 ) b " +
                    " join ( " +
                    " select a.FPortCode,a.FSecurityCode, FFreezeAmount " +
                    " from " + pub.yssGetTableName("Tb_Stock_Security") + " a  where 1 = 1 " +
                    (isAnalysisCodes ?
                     (this.secBrokerField.length() != 0 ? (" and a." + this.secBrokerField + " = " + dbl.sqlString(this.brokerCode)) : " ")
                     : " ") +
                    " and a.FSecurityCode = " + dbl.sqlString(this.securityCode) +
                    " and " + operSql.sqlStoragEve(this.dDate) +
                    " and a.FPortCode =" + dbl.sqlString(sPortCode) +
                    " and FCheckState = 1 ) a on a.Fsecuritycode = b.FSecurityCode group by a.FPortCode";
                rs = dbl.openResultSet(sqlStr);
                if (rs.next()) {
                    dResult = rs.getDouble("FFreezeAmount");
                }
            }
        } catch (Exception ex) {
            throw new YssException("计算组合" + sPortCode + "的库存在日期" + YssFun.formatDate(dDate) + "冻结数量出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dResult;
    }

    /**
     * 取某只证券当日所有组合的买卖数量 MS00125
     * @param sPortCode String
     * @return double
     * @throws YssException
     */
    private double getAllSecurityAmount(String sPortCode) throws YssException {
        double dResult = 0;
        if (this.hmSecurityAllAmountInTrade != null) {
            if (this.hmSecurityAllAmountInTrade.get(sPortCode) != null) {
                dResult = ( (Double)this.hmSecurityAllAmountInTrade.get(sPortCode)).
                    doubleValue();
            }
        } else {
            setSecurityAllAmount();
            dResult = getAllSecurityAmount(sPortCode);
        }
        return dResult;

    }

    /**
     * 取某只证券当日所有组合的买卖数量
     * MS00125
     * @throws YssException
     */
    private void setSecurityAllAmount() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sPortCode = "";
        try {
            hmSecurityAllAmountInTrade = new HashMap();
            strSql =
                "select FPortCode, sum(FTradeAmount*FAmountInd) as FSumTradeAmount from " +
                pub.yssGetTableName("Tb_Data_SubTrade") + " a" +
                " left join (select FTradeTypeCode, FAmountInd from " +
                " Tb_Base_TradeType where FCheckState = 1) b " +
                " on a.FTradeTypeCode = b.FTradeTypeCode " +
                " where 1=1 " +
                (this.userCode.length() > 0 ? " and a.FInvMgrCode=" + dbl.sqlString(this.userCode) : " ") +
                (this.brokerCode.length() > 0 ? " and a.FBrokerCode = " + dbl.sqlString(this.brokerCode) : " ") +
                " and a.FSecurityCode=" + dbl.sqlString(this.securityCode) +
                (sOrderNum.length() == 0 ? " " : " and a.FOrderNum <> " + dbl.sqlString(this.sOrderNum)) + //MS00125
                " and a.FTradeTypeCode in('" + YssOperCons.YSS_JYLX_Buy + "','" + YssOperCons.YSS_JYLX_Sale + "')" + //查找出当日买、卖的业务数据 by leeyu 2008-12-31
                " and a.FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //用这个方法添加分隔符
                ") and a.FBargainDate = " + dbl.sqlDate(this.dDate) +
                " and a.FCheckState = 1 group by a.FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sPortCode = rs.getString("FPortCode");
                hmSecurityAllAmountInTrade.put(sPortCode,
                                               new Double(rs.getDouble(
                    "FSumTradeAmount")));
            }
        } catch (YssException e) {
            throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                                   " 投资经理【" + userCode +
                                   "】在投资组合【" + sPortCode + "】上持有的证券【" +
                                   securityCode +
                                   "】的未结算数量出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                    " 投资经理【" + userCode +
                    "】在投资组合【" + sPortCode + "】上持有的证券【" +
                    securityCode +
                    "】的未结算数量出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * MS00125
     * @param sPortCode String
     * @return double
     * @throws YssException
     */
    private double getSecurityAmountInStorage(String sPortCode) throws YssException {
        double dResult = 0;
        if (this.hmSecurityAllAmountInStorage != null) {
            if (this.hmSecurityAllAmountInStorage.get(sPortCode) != null) {
                dResult = ( (Double)this.hmSecurityAllAmountInStorage.get(sPortCode)).
                    doubleValue();
            }
        } else {
            setSecurityAmountInStorage();
            dResult = getSecurityAmountInStorage(sPortCode);
        }
        return dResult;
    }

    //设置某只证券在所有组合上的数量,分析代码最多只考虑券商
    private void setSecurityAmountInStorage() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        StringBuffer buf = new StringBuffer(); //MS00125
        String sBuf = ""; //MS00125
        try {
            for (int i = 0; i < allPortCodeAry.length; i++) {
                if (htPortinfo == null) {
                    break;
                }
                if (allPortCodeAry[i].length() == 0) {
                    continue;
                }
                if (htPortinfo.get(allPortCodeAry[i]) != null && (Boolean.valueOf(htPortinfo.get(allPortCodeAry[i]).toString()).booleanValue()) == true) {
                    buf.append(" when ").append(dbl.sqlString(allPortCodeAry[i])).append(" then -FFreezeAmount ");
                }
            }
            sBuf = buf.toString();
            if (sBuf.length() > 0) {
                sBuf = "sum(case FPortCode " + sBuf;
                sBuf = sBuf + " else 0 end ) as FFreezeAmount ";
            } //添加所选组合的冻结数量处理 MS00125 by leeyu
            hmSecurityAllAmountInStorage = new HashMap();
            strSql = "select a.FPortCode," + (sBuf.length() > 0 ? sBuf : "0 as FFreezeAmount") + ", sum(FStorageAmount) as FAmount from " +
                pub.yssGetTableName("Tb_Stock_Security") + " a " +
                " where 1=1 " +
                (this.secInvmgrField.length() != 0 && this.userCode.length() != 0 ?
                 (" and a." + this.secInvmgrField + " = " +
                  dbl.sqlString(this.userCode)) : " ") +
                (this.secBrokerField.length() != 0 && brokerCode.length() != 0 ?
                 (" and a." + this.secBrokerField + " = " +
                  dbl.sqlString(this.brokerCode)) : " ") +
                " and a.FSecurityCode = " + dbl.sqlString(this.securityCode) +
                " and " + operSql.sqlStoragEve(this.dDate);

            strSql += " and a.FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) + //这里用这个方法添加分隔符
                ") and FCheckState = 1" +
                " group by a.FPortCode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                hmSecurityAllAmountInStorage.put(rs.getString("FPortCode"),
                                                 new Double(rs.getDouble("FAmount") + rs.getDouble("FFreezeAmount") +
                    getPortAmounts(rs.getString("FPortCode"), true))); //除减掉前日冻结数量外还要加上当日到帐日的送股权益的冻结数量
                dSecurityFreezeAmount += rs.getDouble("FFreezeAmount") + getPortAmounts(rs.getString("FPortCode"), true); //设置冻结数量
            }
        } catch (YssException e) {
            throw new YssException("获取投资经理【" + userCode + "】在各投资组合可用证券库存出错");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	 throw new YssException("获取投资经理【" + userCode + "】在各投资组合可用证券库存出错");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	/**
	** 获取 单个现金帐户的昨日可用库存 by leeyu 20090727
	** QDV4中保2009年07月27日04_B MS00586
	*/
   private void setCuryPortMoneyInStorage() throws YssException {
      ResultSet rs = null;
      String strSql = "";
      try {
         hmSecurityMoneyInStorage = new HashMap();
         strSql = "select a.FPortCode, sum(FAccBalance) as FAccBalance from " +
             pub.yssGetTableName("Tb_Stock_Cash") + " a " +             
             " where 1=1 " +
             (this.cashInvmgrField.length() != 0 ? (" and a." + this.cashInvmgrField + " = " +
             dbl.sqlString(this.userCode)) : " ") +
             (this.cashCatField.length() != 0 ? (" and a." + this.cashCatField + " = " +
                                                 dbl.sqlString(this.sCatCode)) : " ") +
             " and " + operSql.sqlStoragEve(this.dDate)+
             " and a.FCashAccCode ="+dbl.sqlString(this.sCashAcctCode);//添加帐户
       strSql += " and a.FPortCode in (" +operSql.sqlCodes(sqlAllPortCode) + //这里用个方法添加分隔符
               ") and FCheckState = 1" +
               " group by a.FPortCode";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            hmSecurityMoneyInStorage.put(rs.getString("FPortCode"),
                                         new Double(rs.getDouble("FAccBalance")));
         }
      }
      catch (YssException e) {
         throw new YssException("获取投资经理【" + userCode + "】在各投资组合可用现金库存出错");
      } catch (SQLException e) {
		// TODO Auto-generated catch block
    	  throw new YssException("获取投资经理【" + userCode + "】在各投资组合可用现金库存出错");
	}
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }
   /**
   ** 获取单个现金帐户的当日可用现金余额 by leeyu 20090727
   ** QDV4中保2009年07月27日04_B MS00586
   */
   private void setCuryPortMoneyInSettle() throws YssException {
      ResultSet rs = null;
      String strSql = "";
      String sPortCode = "";
      try {
         hmSecurityMoneyInSettle = new HashMap();
         strSql =
               "select FPortCode, sum(FMoney*FInOut) as FSumTradeMoney from (" +
               " select FTransDate,FTransferDate, FMoney, FSecurityCode,FCheckState,FPortCode" +
               (this.cashInvmgrField.length()!=0?("," + this.cashInvmgrField):" ") +
               (this.cashCatField.length()!=0?("," + this.cashCatField):" ") +
               ",FInOut from " +
               pub.yssGetTableName("Tb_Cash_Transfer") +
               " a1 join (select FMoney as FMoney, FInOut,FNum,FPortCode" +
               (this.cashInvmgrField.length()!=0?("," +this.cashInvmgrField):" ") +
               (this.cashCatField.length()!=0?("," + this.cashCatField):" ") +
               " from " +
               pub.yssGetTableName("Tb_Cash_SubTransfer") +
               " where FCheckState = 1 and FCashAccCode="+dbl.sqlString(this.sCashAcctCode)+
               ") a2 on a1.FNum = a2.FNum " +
               ") a" +" where 1=1 " +
               (this.cashInvmgrField.length()!=0?(" and " + this.cashInvmgrField + " = " +
               dbl.sqlString(this.userCode)):" ") +
               (this.cashCatField.length()!=0?(" and " + this.cashCatField + " = " +
               dbl.sqlString(this.sCatCode)):" ") +
               " and FPortCode in (" + operSql.sqlCodes(sqlAllPortCode) +
               " ) and (" + dbl.sqlDate(this.dDate) + " between " +
               " FTransDate and FTransferDate)" +
               " and FCheckState = 1 group by FPortCode";  
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            sPortCode = rs.getString("FPortCode");
            hmSecurityMoneyInSettle.put(sPortCode,
                                       new Double(rs.getDouble("FSumTradeMoney")));
         }
      }
      catch (YssException e) {
         throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                                " 投资经理【" + userCode +
                                "】在投资组合【" + sPortCode + "】上的未结算成交金额出错");
      } catch (SQLException e) {
		// TODO Auto-generated catch block
    	  throw new YssException("获取 " + YssFun.formatDate(this.dDate) +
                  " 投资经理【" + userCode +
                  "】在投资组合【" + sPortCode + "】上的未结算成交金额出错");
	}
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }
}
