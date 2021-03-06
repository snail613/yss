package com.yss.main.operdeal;

import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.dao.ICostCalculate;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.stgstat.StgSecurity;
import com.yss.pojo.cache.YssCost;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
/**
 *
 * <p>Title: 加权平均法计算成本</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BaseAvgCostCalculate
    extends BaseBean implements ICostCalculate {
    private double dAmount;
    private java.util.Date dDate;
    private String portCode;
    private String invmgrCode;
    private String attrClsCode; //属性分类代码
    private String brokerCode;
    private double dPortRate;
    private double dBaseRate;
    private boolean bIfRefreshIntegrated = false;	//20120801 added by liubo.Bug #5051
    
	public boolean getIfRefreshIntegrated() {
		return bIfRefreshIntegrated;
	}

	public void setIfRefreshIntegrated(boolean bIfRefreshIntegrated) {
		this.bIfRefreshIntegrated = bIfRefreshIntegrated;
	}

	public BaseAvgCostCalculate() {
    }

    /**
     * 2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
     * MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
     * @param dDate Date
     * @param sPortCode String
     * @param sInvmgrCode String
     * @param sBrokerCode String
     * @param sAttrClsCode String：属性分类代码，如果为空字符串则不区分属性分类
     * @throws YssException
     */
    public void initCostCalcutate(java.util.Date dDate,
                                  String sPortCode,
                                  String sInvmgrCode,
                                  String sBrokerCode,
                                  String sAttrClsCode) {
        this.dDate = dDate;
        this.portCode = sPortCode;
        this.invmgrCode = sInvmgrCode;
        this.brokerCode = sBrokerCode;
        this.attrClsCode = sAttrClsCode;
    }

    /**
     * getUnitCost
     *
     * @return YssCost
     */
    public YssCost getUnitCost() {
        return null;
    }

    /**
     * getCarryOldCost
     *
     * @return YssCost
     */
    //获取旧的成本--2008.4.7--单亮
    public YssCost getCarryOldCost(String sSecurityCode, double dAmount,
                                   String sTradeNum,
                                   double dBaseCuryRate, double dPortCuryRate) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        YssCost cost = new YssCost();
        String sInvmgrField = "";
        String sBrokerField = "";
        try {
            sInvmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            sBrokerField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);

            strSql = "select FCost,FMCost,FVCost, FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost, FPortCuryCost,FMPortCuryCost,FVPortCuryCost from  " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where fnum = " + dbl.sqlString(sTradeNum);

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                cost.setCost(rs.getDouble("FCost"));
                cost.setMCost(rs.getDouble("FMCost"));
                cost.setVCost(rs.getDouble("FVCost"));

                cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
                cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));
                cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));

                cost.setPortCost(rs.getDouble("FPortCuryCost"));
                cost.setPortMCost(rs.getDouble("FMPortCuryCost"));
                cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
            }
            //fanghaoln 20090805 MS00568 QDV4赢时胜（上海）2009年7月14日01_B 当这比交易没有统计成本时，统计出它的成本传给前台的成本浏览
            if (cost.getCost() == 0 && cost.getMCost() == 0 && cost.getVCost() == 0) {
                cost = getCarryCost(sSecurityCode,
                                    dAmount,
                                    sTradeNum, dBaseCuryRate,
                                    dPortCuryRate); //调用统计成本的方法
            }
            //================================================================================================================
            return cost;
            } catch (Exception e) {
            throw new YssException("获取结转成本出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public YssCost getCarryCost(String sSecurityCode, double dAmount,
                                String sTradeNum,
                                double dBaseCuryRate, double dPortCuryRate) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        YssCost cost = new YssCost();
        String sInvmgrField = "";
        String sBrokerField = "";
        String sErrInfo = "";
        boolean bFlagHV = false; //是否进行了估值成本和核算成本的计算
        boolean bFlag = false; //是否在当天有交易
        int iTmpNum = 0;
        int iNum = 0;
        String isNeedTrade = "";
        try {
        	
        	//20120731 added by liubo.Bug #5051
        	//=========================
        	if (bIfRefreshIntegrated)
        	{
	        	StgSecurity sec = new StgSecurity();
	        	sec.setYssPub(pub);
	        	sec.refreshIntegratedSecurityCost(dDate,dDate,portCode);
        	}
        	//=============end============
        	
            sInvmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            sBrokerField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
            //story 1936 by zhouwei 20111124 invmgrCode不能为空
            if(this.invmgrCode==null || this.invmgrCode.equals("")){
            	this.invmgrCode=" ";
            }
            //先取出前一日的库存成本和数量
            strSql =
                "select FSecurityCode, sum(FStorageAmount) as FStorageAmount, " +
                " sum(FStorageCost) as FStorageCost,sum(FMStorageCost) as FMStorageCost,sum(FVStorageCost) as FVStorageCost," +
                " sum(FPortCuryCost) as FPortCuryCost,sum(FMPortCuryCost) as FMPortCuryCost,sum(FVPortCuryCost) as FVPortCuryCost," +
                " sum(FBaseCuryCost) as FBaseCuryCost,sum(FMBaseCuryCost) as FMBaseCuryCost,sum(FVBaseCuryCost) as FVBaseCuryCost, " +
                " 'M' as FCostType from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " where FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                " and " +
                operSql.sqlStoragEve(dDate) + " and FPortCode = " +
                dbl.sqlString(portCode);
            if (sInvmgrField.trim().length() > 0) {
                strSql = strSql + " and " + sInvmgrField + " = " +
                    dbl.sqlString(invmgrCode);
            }
            //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
            //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
            if (attrClsCode != null && attrClsCode.length() > 0) {
                strSql += (" AND FAttrClsCode = " + dbl.sqlString(attrClsCode));
            }
            strSql = strSql + " and FCheckState = 1" +
                " group by FSecurityCode,'M' union " +
                //---------------------------------------------------------------
                "select FSecurityCode, sum(FStorageAmount) as FStorageAmount, " +
                " sum(FStorageCost) as FStorageCost,sum(FMStorageCost) as FMStorageCost,sum(FVStorageCost) as FVStorageCost," +
                " sum(FPortCuryCost) as FPortCuryCost,sum(FMPortCuryCost) as FMPortCuryCost,sum(FVPortCuryCost) as FVPortCuryCost," +
                " sum(FBaseCuryCost) as FBaseCuryCost,sum(FMBaseCuryCost) as FMBaseCuryCost,sum(FVBaseCuryCost) as FVBaseCuryCost, " +
                " 'HV' as FCostType from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " where FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                " and " +
                operSql.sqlStoragEve(dDate) + " and FPortCode = " +
                dbl.sqlString(portCode);
            //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
            //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
            if (attrClsCode != null && attrClsCode.length() > 0) {
                strSql += (" AND FAttrClsCode = " + dbl.sqlString(attrClsCode));
            }
            strSql = strSql + " and FCheckState = 1 group by FSecurityCode,'HV' ";
            rs = dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);//add by xuxuming,2010.01.14..MS00902 
			// ======add by xuxuming,2010.01.14..MS00902
			// 指数信息调整后不能正确获取昨日成本余额=========
			if (attrClsCode != null
					&& (attrClsCode.equals("CEQ") || attrClsCode
							.equals("IDXEQ"))) {// 此类型才是指数信息调整类型的数据
				if (!rs.next()) {// 没有查询到昨日成本余额，则取另一类型的昨日成本余额
					if (attrClsCode.equals("CEQ")) {
						strSql = strSql.replaceAll("CEQ", "IDXEQ");
					} else {
						strSql = strSql.replaceAll("IDXEQ", "CEQ");
					}
					rs.close();// 使用之前，先关闭游标
					rs = dbl.openResultSet(strSql,
							ResultSet.TYPE_SCROLL_INSENSITIVE);// add by
																// xuxuming,2010.01.14..MS00902
				}
				rs.beforeFirst();
			}
			//=============end====================================================
            while (rs.next()) {
                if (rs.getString("FCostType").equalsIgnoreCase("M")) {
                    cost.setMAmount(rs.getDouble("FStorageAmount"));
                    cost.setMCost(rs.getDouble("FMStorageCost"));
                    cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));
                    cost.setPortMCost(rs.getDouble("FMPortCuryCost"));
                } else if (rs.getString("FCostType").equalsIgnoreCase("HV")) {
                    cost.setAmount(rs.getDouble("FStorageAmount"));
                    cost.setCost(rs.getDouble("FStorageCost"));
                    cost.setVCost(rs.getDouble("FVStorageCost"));
                    cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
                    cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));
                    cost.setPortCost(rs.getDouble("FPortCuryCost"));
                    cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
                }
            }
            //2009.04.02 蒋锦 修改 关闭游标
            dbl.closeResultSetFinal(rs);
            if (sTradeNum.length() == 20) { //更改了编号的获取值 sj edit 20080603
                iNum = this.getTradeNum(sTradeNum);
            } else {
                iNum = YssFun.toInt(YssFun.right(sTradeNum, 6)); //统计库存时调用方法时,此编号截去了后5位。
            }
            //--------------获取此组合的加权平均成本的计算方式 sj edit 20080401 ---
            CtlPubPara pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            isNeedTrade = pubpara.getAvgCost(this.portCode);
            //----------------------------------------------------------------
            //story 1936 by zhouwei 20111222 移动加权考虑当天综合业务数据的证券成本与交易关联数据的流入成本 
            //QDV4赢时胜(上海开发部)2011年11月28日01_A
            if(isNeedTrade.equalsIgnoreCase("Yes(Integrated)")){//加权平均成本计算当天综合，关联交易
            	getIntegratedCostByMoving(cost,sSecurityCode);
            	getTradeRelaCostByMoving(cost, sSecurityCode);
            }
            //-----------------end----------
            if (isNeedTrade.equalsIgnoreCase("no")) { //加权平均成本不计算当天交易
                //计算成本
                bFlagHV = true;
                if (cost.getAmount() != 0) {

                    if (YssD.sub(cost.getAmount(), dAmount) != 0) { //如果这笔正好卖空，就直接等于剩余成本不用再计算，否则可能出现尾差 胡昆 20071219
                        cost.setCost(YssD.mul(YssD.div(cost.getCost(),
                            cost.getAmount()), dAmount));
                        cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(),
                            cost.getAmount()), dAmount));
                        cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(),
                            cost.getAmount()), dAmount));
                        cost.setVCost(YssD.mul(YssD.div(cost.getVCost(),
                            cost.getAmount()), dAmount));
                        cost.setBaseVCost(YssD.mul(YssD.div(cost.getBaseVCost(),
                            cost.getAmount()), dAmount));
                        cost.setPortVCost(YssD.mul(YssD.div(cost.getPortVCost(),
                            cost.getAmount()), dAmount));
                    }
                }

                //管理成本
                if (cost.getMAmount() != 0) {
                    if (YssD.sub(cost.getAmount(), dAmount) != 0) { //如果这笔正好卖空，就直接等于剩余成本不用再计算，否则可能出现尾差 胡昆 20071219
                        cost.setMCost(YssD.mul(YssD.div(cost.getMCost(),
                            cost.getMAmount()), dAmount));
                        cost.setBaseMCost(YssD.mul(YssD.div(cost.getBaseMCost(),
                            cost.getMAmount()), dAmount));
                        cost.setPortMCost(YssD.mul(YssD.div(cost.getPortMCost(),
                            cost.getMAmount()), dAmount));
                    }
                }
            } else { //加权平均成本计算当天交易
                //取出当天交易的成本和数量
                strSql = "select FNum,FSecurityCode,a.FTradeTypeCode,(FTradeAmount*FAmountInd) as FTradeAmount," +
                    " 0 as FCost, 0 as FBaseCost, 0 as FPortCost, " +
                    " (FMCost*FAmountInd) as FMCost," +
                    " (FMBaseCuryCost*FAmountInd) as FMBaseCost, (FMPortCuryCost*FAmountInd) as FMPortCost," +
                    " 0 as FVCost, 0 as FVBaseCost, 0 as FVPortCost," +
                    " FBaseCuryRate,FPortCuryRate,'M' as FCostType" +
                    " from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                    " a left join " +
                    " (select FTradeTypeCode,FAmountInd from Tb_Base_TradeType) b on a.FTradeTypeCode = b.FTradeTypeCode" +
                    " where a.FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                    " and a.FBargainDate = " + dbl.sqlDate(dDate) +
                    " and a.FInvMgrCode = " + dbl.sqlString(invmgrCode) +
//               " and a.FBrokerCode = " + dbl.sqlString(brokerCode) +
                    " and a.FPortCode = " + dbl.sqlString(portCode) +
                    " and a.FCheckState = 1 union " +
                    //---------------------------------------------------------------
                    "select FNum,FSecurityCode,a.FTradeTypeCode,(FTradeAmount*FAmountInd) as FTradeAmount,(FCost*FAmountInd) as FCost," +
                    " (FBaseCuryCost*FAmountInd) as FBaseCost, (FPortCuryCost*FAmountInd) as FPortCost," +
                    " 0 as FMCost, 0 as FMBaseCost, 0 as FMPortCost," +
                    " (FVCost*FAmountInd) as FVCost,(FVBaseCuryCost*FAmountInd) as FVBaseCost," +
                    " (FVPortCuryCost*FAmountInd) as FVPortCost," +
                    " FBaseCuryRate,FPortCuryRate,'HV' as FCostType" +
                    " from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                    " a left join " +
                    " (select FTradeTypeCode,FAmountInd from Tb_Base_TradeType) b on a.FTradeTypeCode = b.FTradeTypeCode" +
                    " where a.FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                    " and a.FBargainDate = " + dbl.sqlDate(dDate) +
                    " and a.FPortCode = " + dbl.sqlString(portCode) +
                    " and a.FCheckState = 1 order by FCostType, FNum";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) { //更改了记录集引用 sj edit 20080603
                    iTmpNum = this.getTradeNum(rs.getString("FNum"));
                    //核算成本，估值成本
                    if (rs.getString("FCostType").equalsIgnoreCase("HV")) {
                        if (bFlagHV) {
                            continue;
                        }
                        if (iTmpNum >= iNum) {
                            //计算成本
                            bFlagHV = true;
                            if (cost.getAmount() != 0) {

                                if (YssD.sub(cost.getAmount(), dAmount) != 0) { //如果这笔正好卖空，就直接等于剩余成本不用再计算，否则可能出现尾差 胡昆 20071219
                                    cost.setCost(YssD.mul(YssD.div(cost.getCost(),
                                        cost.getAmount()), dAmount));
                                    cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(),
                                        cost.getAmount()), dAmount));
                                    cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(),
                                        cost.getAmount()), dAmount));
                                    cost.setVCost(YssD.mul(YssD.div(cost.getVCost(),
                                        cost.getAmount()), dAmount));
                                    cost.setBaseVCost(YssD.mul(YssD.div(cost.
                                        getBaseVCost(),
                                        cost.getAmount()), dAmount));
                                    cost.setPortVCost(YssD.mul(YssD.div(cost.
                                        getPortVCost(),
                                        cost.getAmount()), dAmount));
                                }
                            }
                        } else {
                            //累计当天的成本和数量
                            cost.setAmount(YssD.add(cost.getAmount(),
                                rs.getDouble("FTradeAmount")));
                            cost.setCost(YssD.add(cost.getCost(),
                                                  rs.getDouble("FCost")));
                            cost.setVCost(YssD.add(cost.getVCost(),
                                rs.getDouble("FVCost")));
                            cost.setBaseCost(YssD.add(cost.getBaseCost(),
                                rs.getDouble("FBaseCost")));
                            cost.setBaseVCost(YssD.add(cost.getBaseVCost(),
                                rs.getDouble("FVBaseCost")));
                            //组合货币金额＝交易金额*基础汇率/组合汇率
                            cost.setPortCost(YssD.add(cost.getPortCost(),
                                rs.getDouble("FPortCost")));
                            cost.setPortVCost(YssD.add(cost.getPortVCost(),
                                rs.getDouble("FVPortCost")));
                        }
                    } else if (rs.getString("FCostType").equalsIgnoreCase("M")) { //管理成本
                        if (iTmpNum >= iNum) {
                            if (cost.getMAmount() != 0) {
                                if (YssD.sub(cost.getAmount(), dAmount) != 0) { //如果这笔正好卖空，就直接等于剩余成本不用再计算，否则可能出现尾差 胡昆 20071219
                                    cost.setMCost(YssD.mul(YssD.div(cost.getMCost(),
                                        cost.getMAmount()), dAmount));
                                    cost.setBaseMCost(YssD.mul(YssD.div(cost.
                                        getBaseMCost(),
                                        cost.getMAmount()), dAmount));
                                    cost.setPortMCost(YssD.mul(YssD.div(cost.
                                        getPortMCost(),
                                        cost.getMAmount()), dAmount));
                                }
                            }
                            break;
                        } else {
                            cost.setMAmount(YssD.add(cost.getMAmount(),
                                rs.getDouble("FTradeAmount")));
                            cost.setMCost(YssD.add(cost.getMCost(),
                                rs.getDouble("FMCost")));
                            cost.setBaseMCost(YssD.add(cost.getBaseMCost(),
                                rs.getDouble("FMBaseCost")));
                            cost.setPortMCost(YssD.add(cost.getPortMCost(),
                                rs.getDouble("FMPortCost")));

                        }
                    }
                }
            }
            if (!bFlagHV) {
                if (cost.getAmount() != 0) {
                    if (YssD.sub(cost.getAmount(), dAmount) != 0) {
                        cost.setCost(YssD.mul(YssD.div(cost.getCost(),
                            cost.getAmount()), dAmount));
                        cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(),
                            cost.getAmount()), dAmount));
                        cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(),
                            cost.getAmount()), dAmount));
                        cost.setVCost(YssD.mul(YssD.div(cost.getVCost(),
                            cost.getAmount()), dAmount));
                        cost.setBaseVCost(YssD.mul(YssD.div(cost.getBaseVCost(),
                            cost.getAmount()), dAmount));
                        cost.setPortVCost(YssD.mul(YssD.div(cost.getPortVCost(),
                            cost.getAmount()), dAmount));
                        cost.setMCost(YssD.mul(YssD.div(cost.getMCost(),
                                                  cost.getMAmount()),//合并太平版本代码
                                               dAmount));
                        cost.setBaseMCost(YssD.mul(YssD.div(cost.getBaseMCost(),
                        		cost.getMAmount()), dAmount));//合并太平版本代码
                        cost.setPortMCost(YssD.mul(YssD.div(cost.getPortMCost(),
                        		cost.getMAmount()), dAmount));//合并太平版本代码
                    }
                }
            }
            return cost;
        } catch (Exception e) {
            throw new YssException("获取结转成本出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /*
     * story 1936 by zhouwei 201112 移动加权计算证券成本（根据通用参数计算当天交易数据，综合业务证券成本数据，交易关联数据的 流入成本总和）
     * */
    public YssCost getNewCarryCost(String sSecurityCode, double dAmount,
                String sTradeNum,
	            double dBaseCuryRate, double dPortCuryRate) throws YssException {
	String strSql = "";
	ResultSet rs = null;
	YssCost cost = new YssCost();
	String sInvmgrField = "";
	String sBrokerField = "";
	String sErrInfo = "";
	boolean bFlagHV = false; //是否进行了估值成本和核算成本的计算
	boolean bFlag = false; //是否在当天有交易
	int iTmpNum = 0;
	int iNum = 0;
	String isNeedTrade = "";
	try {
		sInvmgrField = this.getSettingOper().getStorageAnalysisField(
		YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
		sBrokerField = this.getSettingOper().getStorageAnalysisField(
		YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
		//story 1936 by zhouwei 20111124 invmgrCode不能为空
        if(this.invmgrCode==null || this.invmgrCode.equals("")){
        	this.invmgrCode=" ";
        }
		//先取出前一日的库存成本和数量
		strSql =
		"select FSecurityCode, sum(FStorageAmount) as FStorageAmount, " +
		" sum(FStorageCost) as FStorageCost,sum(FMStorageCost) as FMStorageCost,sum(FVStorageCost) as FVStorageCost," +
		" sum(FPortCuryCost) as FPortCuryCost,sum(FMPortCuryCost) as FMPortCuryCost,sum(FVPortCuryCost) as FVPortCuryCost," +
		" sum(FBaseCuryCost) as FBaseCuryCost,sum(FMBaseCuryCost) as FMBaseCuryCost,sum(FVBaseCuryCost) as FVBaseCuryCost, " +
		" 'M' as FCostType from " +
		pub.yssGetTableName("Tb_Stock_Security") +
		" where FSecurityCode = " + dbl.sqlString(sSecurityCode) +
		" and " +
		operSql.sqlStoragEve(dDate) + " and FPortCode = " +
		dbl.sqlString(portCode);
		if (sInvmgrField.trim().length() > 0) {
		strSql = strSql + " and " + sInvmgrField + " = " +
		dbl.sqlString(invmgrCode);
		}
		//2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
		//MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
		if (attrClsCode != null && attrClsCode.length() > 0) {
		strSql += (" AND FAttrClsCode = " + dbl.sqlString(attrClsCode));
		}
		strSql = strSql + " and FCheckState = 1" +
		" group by FSecurityCode,'M' union " +
		//---------------------------------------------------------------
		"select FSecurityCode, sum(FStorageAmount) as FStorageAmount, " +
		" sum(FStorageCost) as FStorageCost,sum(FMStorageCost) as FMStorageCost,sum(FVStorageCost) as FVStorageCost," +
		" sum(FPortCuryCost) as FPortCuryCost,sum(FMPortCuryCost) as FMPortCuryCost,sum(FVPortCuryCost) as FVPortCuryCost," +
		" sum(FBaseCuryCost) as FBaseCuryCost,sum(FMBaseCuryCost) as FMBaseCuryCost,sum(FVBaseCuryCost) as FVBaseCuryCost, " +
		" 'HV' as FCostType from " +
		pub.yssGetTableName("Tb_Stock_Security") +
		" where FSecurityCode = " + dbl.sqlString(sSecurityCode) +
		" and " +
		operSql.sqlStoragEve(dDate) + " and FPortCode = " +
		dbl.sqlString(portCode);
		//2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
		//MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
		if (attrClsCode != null && attrClsCode.length() > 0) {
		strSql += (" AND FAttrClsCode = " + dbl.sqlString(attrClsCode));
		}
		strSql = strSql + " and FCheckState = 1 group by FSecurityCode,'HV' ";
		rs = dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);//add by xuxuming,2010.01.14..MS00902 
		// ======add by xuxuming,2010.01.14..MS00902
		// 指数信息调整后不能正确获取昨日成本余额=========
		if (attrClsCode != null
		&& (attrClsCode.equals("CEQ") || attrClsCode
				.equals("IDXEQ"))) {// 此类型才是指数信息调整类型的数据
		if (!rs.next()) {// 没有查询到昨日成本余额，则取另一类型的昨日成本余额
		if (attrClsCode.equals("CEQ")) {
			strSql = strSql.replaceAll("CEQ", "IDXEQ");
		} else {
			strSql = strSql.replaceAll("IDXEQ", "CEQ");
		}
		rs.close();// 使用之前，先关闭游标
		rs = dbl.openResultSet(strSql,
				ResultSet.TYPE_SCROLL_INSENSITIVE);// add by
													// xuxuming,2010.01.14..MS00902
		}
		rs.beforeFirst();
		}
		//=============end====================================================
		while (rs.next()) {
			if (rs.getString("FCostType").equalsIgnoreCase("M")) {
					cost.setMAmount(rs.getDouble("FStorageAmount"));
					cost.setMCost(rs.getDouble("FMStorageCost"));
					cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));
					cost.setPortMCost(rs.getDouble("FMPortCuryCost"));
			} else if (rs.getString("FCostType").equalsIgnoreCase("HV")) {
					cost.setAmount(rs.getDouble("FStorageAmount"));
					cost.setCost(rs.getDouble("FStorageCost"));
					cost.setVCost(rs.getDouble("FVStorageCost"));
					cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
					cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));
					cost.setPortCost(rs.getDouble("FPortCuryCost"));
					cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
			}
		}
		//2009.04.02 蒋锦 修改 关闭游标
		dbl.closeResultSetFinal(rs);
		//--------------获取此组合的加权平均成本的计算方式 sj edit 20080401 ---
		CtlPubPara pubpara = new CtlPubPara();
		pubpara.setYssPub(pub);
		isNeedTrade = pubpara.getAvgCost(this.portCode);
		//----------------------------------------------------------------
		//story 1936 by zhouwei 20111222 移动加权考虑当天综合业务数据的证券成本与交易关联数据的流入成本 
		//QDV4赢时胜(上海开发部)2011年11月28日01_A
		if(isNeedTrade.equalsIgnoreCase("Yes(Integrated)")){//加权平均成本计算当天综合，关联交易
			getIntegratedCostByMoving(cost,sSecurityCode);
			getTradeRelaCostByMoving(cost, sSecurityCode);
		}
		//-----------------end----------
		if (isNeedTrade.equalsIgnoreCase("no")) { //加权平均成本不计算当天交易
			//计算成本
			if (cost.getAmount() != 0) {
			
				if (YssD.sub(cost.getAmount(), dAmount) != 0) { //如果这笔正好卖空，就直接等于剩余成本不用再计算，否则可能出现尾差 胡昆 20071219
				    cost.setCost(YssD.mul(YssD.div(cost.getCost(),
				        cost.getAmount()), dAmount));
				    cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(),
				        cost.getAmount()), dAmount));
				    cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(),
				        cost.getAmount()), dAmount));
				    cost.setVCost(YssD.mul(YssD.div(cost.getVCost(),
				        cost.getAmount()), dAmount));
				    cost.setBaseVCost(YssD.mul(YssD.div(cost.getBaseVCost(),
				        cost.getAmount()), dAmount));
				    cost.setPortVCost(YssD.mul(YssD.div(cost.getPortVCost(),
				        cost.getAmount()), dAmount));
				}
			}
			
			//管理成本
				if (cost.getMAmount() != 0) {
					if (YssD.sub(cost.getAmount(), dAmount) != 0) { //如果这笔正好卖空，就直接等于剩余成本不用再计算，否则可能出现尾差 胡昆 20071219
					    cost.setMCost(YssD.mul(YssD.div(cost.getMCost(),
					        cost.getMAmount()), dAmount));
					    cost.setBaseMCost(YssD.mul(YssD.div(cost.getBaseMCost(),
					        cost.getMAmount()), dAmount));
					    cost.setPortMCost(YssD.mul(YssD.div(cost.getPortMCost(),
					        cost.getMAmount()), dAmount));
					}
				}
			} else { //加权平均成本计算当天交易
				//取出当天交易的成本和数量(流入)
				strSql = "select FNum,FSecurityCode,a.FTradeTypeCode,(FTradeAmount*FAmountInd) as FTradeAmount," +
				" 0 as FCost, 0 as FBaseCost, 0 as FPortCost, " +
				" (FMCost*FAmountInd) as FMCost," +
				" (FMBaseCuryCost*FAmountInd) as FMBaseCost, (FMPortCuryCost*FAmountInd) as FMPortCost," +
				" 0 as FVCost, 0 as FVBaseCost, 0 as FVPortCost," +
				" FBaseCuryRate,FPortCuryRate,'M' as FCostType" +
				" from " + pub.yssGetTableName("Tb_Data_SubTrade") +
				" a left join " +
				" (select FTradeTypeCode,FAmountInd from Tb_Base_TradeType) b on a.FTradeTypeCode = b.FTradeTypeCode" +
				" where a.FSecurityCode = " + dbl.sqlString(sSecurityCode) +
				" and a.FBargainDate = " + dbl.sqlDate(dDate) +
				" and a.FInvMgrCode = " + dbl.sqlString(invmgrCode) +
				//" and a.FBrokerCode = " + dbl.sqlString(brokerCode) +
				" and a.FPortCode = " + dbl.sqlString(portCode) +
				" and b.FAmountInd=1"+
				" and a.FCheckState = 1 union " +
				//---------------------------------------------------------------
				"select FNum,FSecurityCode,a.FTradeTypeCode,(FTradeAmount*FAmountInd) as FTradeAmount,(FCost*FAmountInd) as FCost," +
				" (FBaseCuryCost*FAmountInd) as FBaseCost, (FPortCuryCost*FAmountInd) as FPortCost," +
				" 0 as FMCost, 0 as FMBaseCost, 0 as FMPortCost," +
				" (FVCost*FAmountInd) as FVCost,(FVBaseCuryCost*FAmountInd) as FVBaseCost," +
				" (FVPortCuryCost*FAmountInd) as FVPortCost," +
				" FBaseCuryRate,FPortCuryRate,'HV' as FCostType" +
				" from " + pub.yssGetTableName("Tb_Data_SubTrade") +
				" a left join " +
				" (select FTradeTypeCode,FAmountInd from Tb_Base_TradeType) b on a.FTradeTypeCode = b.FTradeTypeCode" +
				" where a.FSecurityCode = " + dbl.sqlString(sSecurityCode) +
				" and a.FBargainDate = " + dbl.sqlDate(dDate) +
				" and a.FPortCode = " + dbl.sqlString(portCode) +
				" and b.FAmountInd=1"+
				" and a.FCheckState = 1 order by FCostType, FNum";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) { 
					if (rs.getString("FCostType").equalsIgnoreCase("HV")) {
	
				        //累计当天的成本和数量
				        cost.setAmount(YssD.add(cost.getAmount(),
				            rs.getDouble("FTradeAmount")));
				        cost.setCost(YssD.add(cost.getCost(),
				                              rs.getDouble("FCost")));
				        cost.setVCost(YssD.add(cost.getVCost(),
				            rs.getDouble("FVCost")));
				        cost.setBaseCost(YssD.add(cost.getBaseCost(),
				            rs.getDouble("FBaseCost")));
				        cost.setBaseVCost(YssD.add(cost.getBaseVCost(),
				            rs.getDouble("FVBaseCost")));
				        //组合货币金额＝交易金额*基础汇率/组合汇率
				        cost.setPortCost(YssD.add(cost.getPortCost(),
				            rs.getDouble("FPortCost")));
				        cost.setPortVCost(YssD.add(cost.getPortVCost(),
				            rs.getDouble("FVPortCost")));
				    
					} else if (rs.getString("FCostType").equalsIgnoreCase("M")) {
	
				        cost.setMAmount(YssD.add(cost.getMAmount(),
				            rs.getDouble("FTradeAmount")));
				        cost.setMCost(YssD.add(cost.getMCost(),
				            rs.getDouble("FMCost")));
				        cost.setBaseMCost(YssD.add(cost.getBaseMCost(),
				            rs.getDouble("FMBaseCost")));
				        cost.setPortMCost(YssD.add(cost.getPortMCost(),
				            rs.getDouble("FMPortCost")));						    
				   }
			    }
				if (cost.getAmount() != 0) {
					if (YssD.sub(cost.getAmount(), dAmount) != 0) {
					    cost.setCost(YssD.mul(YssD.div(cost.getCost(),
					        cost.getAmount()), dAmount));
					    cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(),
					        cost.getAmount()), dAmount));
					    cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(),
					        cost.getAmount()), dAmount));
					    cost.setVCost(YssD.mul(YssD.div(cost.getVCost(),
					        cost.getAmount()), dAmount));
					    cost.setBaseVCost(YssD.mul(YssD.div(cost.getBaseVCost(),
					        cost.getAmount()), dAmount));
					    cost.setPortVCost(YssD.mul(YssD.div(cost.getPortVCost(),
					        cost.getAmount()), dAmount));
					    cost.setMCost(YssD.mul(YssD.div(cost.getMCost(),
					                              cost.getMAmount()),//合并太平版本代码
					                           dAmount));
					    cost.setBaseMCost(YssD.mul(YssD.div(cost.getBaseMCost(),
					    		cost.getMAmount()), dAmount));//合并太平版本代码
					    cost.setPortMCost(YssD.mul(YssD.div(cost.getPortMCost(),
					    		cost.getMAmount()), dAmount));//合并太平版本代码
					}
				}
		}
		
		return cost;
	} catch (Exception e) {
		throw new YssException("获取结转成本出错" + e.getMessage());
	} finally {
		dbl.closeResultSetFinal(rs);
	}
}

    /* story 1936 移动加权算法计算库存成本时，考虑综合业务数据的证券成本数据
     * zhouwei 20111222 QDV4赢时胜(上海开发部)2011年11月28日01_A
     * */
    private void getIntegratedCostByMoving(YssCost cost,String sSecurityCode) throws YssException{
    	
    	ResultSet rs=null;
    	String sInvmgrField="";
    	String sBrokerField="";
    	try{
    		sInvmgrField = this.getSettingOper().getStorageAnalysisField(
    				YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
			sBrokerField = this.getSettingOper().getStorageAnalysisField(
						YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
    		 String strSql="select sum(FAMOUNT) as FAMOUNT,sum(FEXCHANGECOST) as FEXCHANGECOST,sum(FMEXCOST) as FMEXCOST,"
    					  +"sum(FVEXCOST) as FVEXCOST,sum(FPORTEXCOST) as FPORTEXCOST,"
			  		      +"sum(FMPORTEXCOST) as FMPORTEXCOST,sum(FVPORTEXCOST) as FVPORTEXCOST,sum(FBASEEXCOST) as FBASEEXCOST,"
			  		      +"sum(FMBASEEXCOST) as FMBASEEXCOST,sum(FVBASEEXCOST) as FVBASEEXCOST"
			  		    +" from "+pub.yssGetTableName("tb_data_Integrated")+" a where ((a.FRelaNum = ' ' and a.FNumType = ' ') or "
			  		      +" (a.FNumType = 'securitymanage') or (a.FNumType = 'FutruesTrade') or "
			  		      +" (a.FNumType = 'IBB') or (a.FNumType = 'OptionsTrade') or "
			  		      +" (a.FNumType = 'AutoDropOpRight') or (a.FNumType = 'OPurRE') or (a.FNumType = 'openfund'))"
			  		      +" and a.fcheckstate=1  and a.finouttype=1"
			  		      +" and a.FSecurityCode="+dbl.sqlString(sSecurityCode)
			  		      +" and a.FPortCode="+dbl.sqlString(portCode)
			  		      +" and a.FOperDate="+dbl.sqlDate(dDate);
		      if (attrClsCode != null && attrClsCode.length() > 0) {
		          strSql += (" AND a.FAttrClsCode = " + dbl.sqlString(attrClsCode));
		      }
	          strSql+=" group by a.FSecurityCode";
		      rs=dbl.openResultSet(strSql);
		      while(rs.next()){
		    	  cost.setMAmount(YssD.add(cost.getMAmount(), rs.getDouble("FAMOUNT")));
		    	  cost.setAmount(YssD.add(cost.getAmount(), rs.getDouble("FAMOUNT")));
		    	  cost.setCost(YssD.add(cost.getCost(), rs.getDouble("FEXCHANGECOST")));
		    	  cost.setVCost(YssD.add(cost.getVCost(), rs.getDouble("FVEXCOST")));
		    	  cost.setMCost(YssD.add(cost.getMCost(), rs.getDouble("FMEXCOST")));
	              cost.setBaseCost(YssD.add(cost.getBaseCost(), rs.getDouble("FBASEEXCOST")));
	              cost.setBaseVCost(YssD.add(cost.getBaseVCost(), rs.getDouble("FVBASEEXCOST")));
	              cost.setBaseMCost(YssD.add(cost.getBaseMCost(), rs.getDouble("FMBASEEXCOST")));
	              cost.setPortCost(YssD.add(cost.getPortCost(),rs.getDouble("FPORTEXCOST")));
	              cost.setPortVCost(YssD.add(cost.getPortVCost(),rs.getDouble("FVPORTEXCOST")));
	              cost.setPortMCost(YssD.add(cost.getPortMCost(),rs.getDouble("FMPORTEXCOST")));            
		      }
    	}catch(Exception e){
    		throw new YssException("移动加权涉及综合业务数据的证券成本出错"+e.getMessage(),e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    /* story 1936 移动加权算法计算库存成本时，考虑交易关联数据
     * zhouwei 20111222 
     * */
    private void getTradeRelaCostByMoving(YssCost cost,String sSecurityCode) throws YssException{
    	
    	ResultSet rs=null;
    	try{
    		 String strSql="select sum(a.FAMOUNT) as FAMOUNT,sum(a.FCOST) as FCOST,sum(a.FVCOST) as FVCOST,sum(a.FMCOST) as FMCOST,"
    					  +"sum(a.FBASECURYCOST) as FBASECURYCOST,sum(a.FVBASECURYCOST) as FVBASECURYCOST,sum(a.FMBASECURYCOST) as FMBASECURYCOST,"
			  		      +"sum(a.FPORTCURYCOST) as FPORTCURYCOST,sum(a.FVPORTCURYCOST) as FVPORTCURYCOST,sum(a.FMPORTCURYCOST) as FMPORTCURYCOST"
			  		      +" from "+pub.yssGetTableName("Tb_Data_TradeRela")+" a "
			  		      +" join (select * from "+pub.yssGetTableName("Tb_Data_SubTrade")+" b1"
			  		      +" where b1.FCheckState = 1) b on a.FNum = b.FNum"
			  		      +" where a.fcheckstate=1  and a.FINOUT=1"
			  		      +" and a.FSecurityCode="+dbl.sqlString(sSecurityCode)
			  		      +" and a.FPortCode="+dbl.sqlString(portCode)
			  		      +" and b.FBargainDate="+dbl.sqlDate(dDate);
		      if (attrClsCode != null && attrClsCode.length() > 0) {
		          strSql += (" AND a.FAttrClsCode = " + dbl.sqlString(attrClsCode));
		      }
	          strSql+=" group by a.FSecurityCode";
		      rs=dbl.openResultSet(strSql);
		      while(rs.next()){
		    	  cost.setMAmount(YssD.add(cost.getMAmount(), rs.getDouble("FAMOUNT")));
		    	  cost.setAmount(YssD.add(cost.getAmount(), rs.getDouble("FAMOUNT")));
		    	  cost.setCost(YssD.add(cost.getCost(), rs.getDouble("FCOST")));
		    	  cost.setVCost(YssD.add(cost.getVCost(), rs.getDouble("FVCOST")));
		    	  cost.setMCost(YssD.add(cost.getMCost(), rs.getDouble("FMCOST")));
	              cost.setBaseCost(YssD.add(cost.getBaseCost(), rs.getDouble("FBASECURYCOST")));
	              cost.setBaseVCost(YssD.add(cost.getBaseVCost(), rs.getDouble("FVBASECURYCOST")));
	              cost.setBaseMCost(YssD.add(cost.getBaseMCost(), rs.getDouble("FMBASECURYCOST")));
	              cost.setPortCost(YssD.add(cost.getPortCost(),rs.getDouble("FPORTCURYCOST")));
	              cost.setPortVCost(YssD.add(cost.getPortVCost(),rs.getDouble("FVPORTCURYCOST")));
	              cost.setPortMCost(YssD.add(cost.getPortMCost(),rs.getDouble("FMPORTCURYCOST")));            
		      }
    	}catch(Exception e){
    		throw new YssException("移动加权涉及交易关联数据出错"+e.getMessage(),e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    /**
     * 2009-07-03 蒋锦 修改 获取结转成本
     * MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
     * @param sSecurityCode String：证券代码
     * @param dAmount double：结转数量
     * @param sTradeNum String：结转的交易编号
     * @param fSettleDate      结算日期
     * @param operType String：业务类型
     * @param tradeType String：需要统计的交易类型，同逗号隔开
     * @return YssCost：结转成本
     * @throws YssException
     */
    public YssCost getCarryCost(String sSecurityCode,
                                double dAmount,
                                String sTradeNum,
                                java.util.Date fSettleDate, //add by guolongchao 20110815  STORY #1207  添加结算日期参数
                                String operType,
                                String tradeType) throws YssException {
        YssCost cost = null;
        try {
            cost = getAvgCost(sSecurityCode);

            //只有银行间债券交易在统计卖出成本的时候需要计算当天的交易，否则其他都不计算当天交易
            if (operType.equalsIgnoreCase("interbankbond")) { //银行间债券交易
            	//add by zhouwei 20120423  考虑到债券转托管生成的综合业务数据
            	getIntegratedCostOfDevTrustBond(cost,sSecurityCode);
                getCarryCostForInterBankBond(cost, sSecurityCode, dAmount, sTradeNum,fSettleDate, tradeType);
            }else{
                if (cost.getAmount() != dAmount) {
                    cost.setCost(YssD.mul(YssD.div(cost.getCost(), cost.getAmount()), dAmount));
                    cost.setMCost(cost.getCost());
                    cost.setVCost(cost.getCost());

                    cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(), cost.getAmount()), dAmount));
                    cost.setBaseMCost(cost.getBaseCost());
                    cost.setBaseVCost(cost.getBaseCost());

                    cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(), cost.getAmount()), dAmount));
                    cost.setPortMCost(cost.getPortCost());
                    cost.setPortVCost(cost.getPortCost());
                    cost.setAmount(dAmount);
                }
            }
        } catch (Exception ex) {
            throw new YssException("获取结转成本出错！", ex);
        }
        return cost;
    }
    
    /** 
     * add by zhouwei 20120423 获取债券转托管生成的综合业务数据的成本
    * @Title: getIntegratedCostOfDevTrustBond 
    * @Description: TODO
    * @param @param cost
    * @param @param sSecurityCode
    * @param @throws YssException    设定文件 
    * @return void    返回类型 
    * @throws 
    */
    private void getIntegratedCostOfDevTrustBond(YssCost cost,String sSecurityCode) throws YssException{   	
    	ResultSet rs=null;
    	try{
    		 String strSql="select sum(FAMOUNT) as FAMOUNT,sum(FEXCHANGECOST) as FEXCHANGECOST,sum(FMEXCOST) as FMEXCOST,"
    					  +"sum(FVEXCOST) as FVEXCOST,sum(FPORTEXCOST) as FPORTEXCOST,"
			  		      +"sum(FMPORTEXCOST) as FMPORTEXCOST,sum(FVPORTEXCOST) as FVPORTEXCOST,sum(FBASEEXCOST) as FBASEEXCOST,"
			  		      +"sum(FMBASEEXCOST) as FMBASEEXCOST,sum(FVBASEEXCOST) as FVBASEEXCOST"
			  		      +" from "+pub.yssGetTableName("tb_data_Integrated")+" a where "
			  		      +" a.FNumType = 'securitymanage'"			  		  
			  		      +" and a.fcheckstate=1  and a.finouttype=1"
			  		      +" and a.FSecurityCode="+dbl.sqlString(sSecurityCode)
			  		      +" and a.FPortCode="+dbl.sqlString(portCode)
			  		      +" and a.FOperDate="+dbl.sqlDate(dDate);
		      if (attrClsCode != null && attrClsCode.length() > 0) {
		          strSql += (" AND a.FAttrClsCode = " + dbl.sqlString(attrClsCode));
		      }
	          strSql+=" group by a.FSecurityCode";
		      rs=dbl.openResultSet(strSql);
		      while(rs.next()){
		    	  cost.setMAmount(YssD.add(cost.getMAmount(), rs.getDouble("FAMOUNT")));
		    	  cost.setAmount(YssD.add(cost.getAmount(), rs.getDouble("FAMOUNT")));
		    	  cost.setCost(YssD.add(cost.getCost(), rs.getDouble("FEXCHANGECOST")));
		    	  cost.setVCost(YssD.add(cost.getVCost(), rs.getDouble("FVEXCOST")));
		    	  cost.setMCost(YssD.add(cost.getMCost(), rs.getDouble("FMEXCOST")));
	              cost.setBaseCost(YssD.add(cost.getBaseCost(), rs.getDouble("FBASEEXCOST")));
	              cost.setBaseVCost(YssD.add(cost.getBaseVCost(), rs.getDouble("FVBASEEXCOST")));
	              cost.setBaseMCost(YssD.add(cost.getBaseMCost(), rs.getDouble("FMBASEEXCOST")));
	              cost.setPortCost(YssD.add(cost.getPortCost(),rs.getDouble("FPORTEXCOST")));
	              cost.setPortVCost(YssD.add(cost.getPortVCost(),rs.getDouble("FVPORTEXCOST")));
	              cost.setPortMCost(YssD.add(cost.getPortMCost(),rs.getDouble("FMPORTEXCOST")));            
		      }
    	}catch(Exception e){
    		throw new YssException("计算综合业务数据的证券成本出错"+e.getMessage(),e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    /**
     * 获取债券转托管的结转成本
     * @param cost YssCost
     * @param sSecurityCode String
     * @param dAmount double
     * @param sTradeNum String
     * @param sTypeCode String
     * @return YssCost
     * @throws YssException
     */
    private YssCost getCarryCostForDevTrustBond(YssCost cost,
                                                 String sSecurityCode,
                                                 double dAmount,
                                                 String sTradeNum,
                                                 String sTypeCode) throws YssException {
        ResultSet rs = null;
        StringBuffer bufSql = new StringBuffer();
        String sInvmgrField = "";
        try {
            bufSql.append(" SELECT * ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_DevTrustBond"));
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            bufSql.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            bufSql.append(" AND FBARGAINDATE = ").append(dbl.sqlDate(dDate));
            bufSql.append(" AND FNum < ").append(dbl.sqlString(sTradeNum));
            if (attrClsCode != null && attrClsCode.length() > 0) {
                bufSql.append(" AND FAttrClsCode = " + dbl.sqlString(attrClsCode));
            }
            if (sInvmgrField.trim().length() > 0) {
                bufSql.append(" AND FInvMgrCode = ").append(dbl.sqlString(this.invmgrCode));
            }
            bufSql.append(" ORDER BY FNum");


            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                double dbCarryedAmount = rs.getDouble("FAmount");
                cost.setCost(
                    YssD.sub(cost.getCost(),
                             YssD.mul(YssD.div(cost.getCost(), cost.getAmount()), dbCarryedAmount)));
                cost.setMCost(cost.getCost());
                cost.setVCost(cost.getCost());

                cost.setBaseCost(
                    YssD.sub(cost.getBaseCost(),
                             YssD.mul(YssD.div(cost.getBaseCost(), cost.getAmount()), dbCarryedAmount)));
                cost.setBaseMCost(cost.getBaseCost());
                cost.setBaseVCost(cost.getBaseCost());

                cost.setPortCost(
                    YssD.sub(cost.getPortCost(),
                             YssD.mul(YssD.div(cost.getPortCost(), cost.getAmount()), dbCarryedAmount)));
                cost.setPortMCost(cost.getPortCost());
                cost.setPortVCost(cost.getPortCost());

                cost.setAmount(YssD.sub(cost.getAmount(), dbCarryedAmount));
            }

            if (cost.getAmount() != dAmount) {
                cost.setCost(
                    YssD.mul(YssD.div(cost.getCost(), cost.getAmount()), dAmount));
                cost.setMCost(cost.getCost());
                cost.setVCost(cost.getCost());

                cost.setBaseCost(
                    YssD.mul(YssD.div(cost.getBaseCost(), cost.getAmount()), dAmount));
                cost.setBaseMCost(cost.getBaseCost());
                cost.setBaseVCost(cost.getBaseCost());

                cost.setPortCost(
                    YssD.mul(YssD.div(cost.getPortCost(), cost.getAmount()), dAmount));
                cost.setPortMCost(cost.getPortCost());
                cost.setPortVCost(cost.getPortCost());
            }

        } catch (Exception ex) {
            throw new YssException("获取债券转托管的结转成本", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;

    }


    /**
     * 获取银行间债券交易的结转成本
     * @param cost YssCost：昨日加权平均成本
     * @param sSecurityCode String：证券代码
     * @param dAmount double
     * @param sTradeNum String
     * @param sTypeCode String
     * @return YssCost
     * @throws YssException
     */
    private YssCost getCarryCostForInterBankBond(YssCost cost,
                                                 String sSecurityCode,
                                                 double dAmount,
                                                 String sTradeNum,
                                                 java.util.Date fSettleDate, //add by guolongchao 20110815 添加结算日期参数
                                                 String sTypeCode) throws YssException {
        //HashMap hmOrderPorts = null;
        HashMap hmSelCostPorts = null;//add by guolongchao 20110815 获取成本核算方式
        ResultSet rs = null;
        StringBuffer bufSql = new StringBuffer();
        String sInvmgrField = "";
        HashMap hmFee = null;
        double baseCuryRate = 0;
        double portCuryRate = 0;
        try {
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            //hmOrderPorts = pubPara.getInterBankOrderPorts();
            hmSelCostPorts=pubPara.getInterBankSelCostPorts("interBankSelCost");//add by guolongchao 20110815 获取成本核算方式
            hmFee = pubPara.getInterBankBondFee();

            bufSql.append(" SELECT a.*, b.FTradeCury ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("TB_Data_IntBakBond")).append(" a");
            bufSql.append(" LEFT JOIN " + pub.yssGetTableName("TB_Para_Security") + " b ");
            bufSql.append(" ON a.FSecurityCode = b.FSecurityCode");
            bufSql.append(" WHERE a.FCheckState = 1 ");
            bufSql.append(" AND a.FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            bufSql.append(" AND a.FPortCode = ").append(dbl.sqlString(portCode));
            bufSql.append(" AND a.FBARGAINDATE = ").append(dbl.sqlDate(dDate));
            if (attrClsCode != null && attrClsCode.length() > 0) {
                bufSql.append(" AND a.FAttrClsCode = " + dbl.sqlString(attrClsCode));
            }
            if (sInvmgrField.trim().length() > 0) {
                bufSql.append(" AND a.FInvMgrCode = ").append(dbl.sqlString(this.invmgrCode));
            }
//            //按成交比号排序
//            if (hmOrderPorts.get(portCode) != null) {
//                bufSql.append(" AND a.FNum < ").append(dbl.sqlString(sTradeNum));
//                bufSql.append(" ORDER BY a.FNum ");
//            } else { //按先买后卖计算
//                //如果按照先买后卖计算则卖出的交易编号不能大于需要计算的编号
//                bufSql.append(" AND (a.FTradeTypeCode = '01' OR (a.FTradeTypeCode <> '01' AND a.FNum < " + dbl.sqlString(sTradeNum) + "))");
//                bufSql.append(" ORDER BY a.FTradeTypeCode");
//            }
            
            //add by guolongchao 20110815  STORY #1207  获取成本核算方式
            if (hmSelCostPorts.get(portCode)!=null && hmSelCostPorts.get(portCode).equals("0,0"))//按成交比号排序
            {
            	//卖出成本=按交易顺序（可以认为按照成交编号从小到大排序）
                bufSql.append(" AND a.FNum < ").append(dbl.sqlString(sTradeNum));
                bufSql.append(" ORDER BY a.FNum ");
            } 
            else if (hmSelCostPorts.get(portCode)!=null && hmSelCostPorts.get(portCode).equals("1,1"))//按先后日期
            {
            	//卖出成本=（前一日的库存成本 + 不大于结算日期的所有买入成本）/（前一日的库存数量+不大于结算日期的所有买入数量）*卖出数量
            	bufSql.append(" and a.FSettleDate <="+dbl.sqlDate(fSettleDate));
            	bufSql.append(" and  a.FTradeTypeCode= '01'");
            	bufSql.append(" ORDER BY a.FSettleDate ");
            } 
            else if (hmSelCostPorts.get(portCode)!=null && hmSelCostPorts.get(portCode).equals("2,2"))//按买卖方式
            {
            	//卖出成本=（前一日的库存成本 + 成交日期当天的所有买入成本）/（前一日的库存数量+成交日期当天的所有买入数量）*卖出数量
            	bufSql.append(" and  a.FTradeTypeCode= '01'");
            	bufSql.append(" ORDER BY a.FSettleDate ");
            } 
            else 
            {
                bufSql.append(" AND (a.FTradeTypeCode = '01' OR (a.FTradeTypeCode <> '01' AND a.FNum < " + dbl.sqlString(sTradeNum) + "))");
                bufSql.append(" ORDER BY a.FTradeTypeCode");
            }
            //---------------end add by guolongchao 20110815  STORY #1207 获取成本核算方式
            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                double dbCarryedAmount = rs.getDouble("FTradeAmount");

                if(rs.getString("FTradeTypeCode").equalsIgnoreCase("01")){
                    double dbTradeMoney = 0;
                    baseCuryRate = rs.getDouble("FBaseCuryRate");
                    portCuryRate = rs.getDouble("FPortCuryRate");
                    if (hmFee.get(rs.getString("FPortCode")) != null) {
                        dbTradeMoney = YssD.add(rs.getDouble("FTradeMoney"), rs.getDouble("FBankFee"));
                    } else {
                        dbTradeMoney = rs.getDouble("FTradeMoney");
                    }
                    cost.setCost(YssD.add(cost.getCost(), dbTradeMoney));
                    cost.setMCost(cost.getCost());
                    cost.setVCost(cost.getCost());
                    cost.setBaseCost(YssD.add(this.getSettingOper().calBaseMoney(dbTradeMoney,
                        baseCuryRate, 2), cost.getBaseCost()));
                    cost.setPortCost(YssD.add(this.getSettingOper().calPortMoney(dbTradeMoney,
                        baseCuryRate, portCuryRate,
                        rs.getString("FTradeCury"), rs.getDate("FBARGAINDATE"), rs.getString("FPortCode"), 2), cost.getPortCost()));
                    cost.setBaseMCost(cost.getBaseCost());
                    cost.setBaseVCost(cost.getBaseCost());
                    cost.setPortMCost(cost.getPortCost());
                    cost.setPortVCost(cost.getPortCost());
                    cost.setAmount(YssD.add(cost.getAmount(), rs.getDouble("FTradeAmount")));
                } else{
                    cost.setCost(
                        YssD.sub(cost.getCost(),
                                 YssD.mul(YssD.div(cost.getCost(), cost.getAmount()), dbCarryedAmount)));
                    cost.setMCost(cost.getCost());
                    cost.setVCost(cost.getCost());

                    cost.setBaseCost(
                        YssD.sub(cost.getBaseCost(),
                                 YssD.mul(YssD.div(cost.getBaseCost(), cost.getAmount()), dbCarryedAmount)));
                    cost.setBaseMCost(cost.getBaseCost());
                    cost.setBaseVCost(cost.getBaseCost());

                    cost.setPortCost(
                        YssD.sub(cost.getPortCost(),
                                 YssD.mul(YssD.div(cost.getPortCost(), cost.getAmount()), dbCarryedAmount)));
                    cost.setPortMCost(cost.getPortCost());
                    cost.setPortVCost(cost.getPortCost());

                    cost.setAmount(YssD.sub(cost.getAmount(), dbCarryedAmount));
                }
            }

            if (cost.getAmount() != dAmount) {
                cost.setCost(
                    YssD.mul(YssD.div(cost.getCost(), cost.getAmount()), dAmount));
                cost.setMCost(cost.getCost());
                cost.setVCost(cost.getCost());

                cost.setBaseCost(
                    YssD.mul(YssD.div(cost.getBaseCost(), cost.getAmount()), dAmount));
                cost.setBaseMCost(cost.getBaseCost());
                cost.setBaseVCost(cost.getBaseCost());

                cost.setPortCost(
                    YssD.mul(YssD.div(cost.getPortCost(), cost.getAmount()), dAmount));
                cost.setPortMCost(cost.getPortCost());
                cost.setPortVCost(cost.getPortCost());
            }

        } catch (Exception ex) {
            throw new YssException("获取银行间债券的结转成本出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    /**
     * 获取开放式基金业务的结转成本
     * @param cost YssCost： 昨日加权平均成本
     * @param sSecurityCode String：证券代码
     * @param dAmount double：结转数量
     * @param sTradeNum String：交易编号
     * @param sTypeCode String：需要统计的结转类型，用逗号隔开
     * @return YssCost
     * @throws YssException
     */
    private YssCost getCarryCostForOpenFund(YssCost cost,
                                            String sSecurityCode,
                                            double dAmount,
                                            String sTradeNum,
                                            String sTypeCode) throws YssException {
        ResultSet rs = null;
        StringBuffer bufSql = new StringBuffer();
        String sInvmgrField = "";
        double dbCarryedAmount = 0;
        try {
            sInvmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            bufSql.append(" SELECT * ");
            bufSql.append(" FROM ").append("Tb_Data_OpenFundTrade");
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FDataType = ").append(dbl.sqlString("confirm"));
            bufSql.append(" AND FNum < ").append(dbl.sqlString(sTradeNum));
            bufSql.append(" AND FComfDate = ").append(dbl.sqlDate(dDate));
            bufSql.append(" AND FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            bufSql.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            if (sInvmgrField.trim().length() > 0) {
                bufSql.append(" AND FInvMgrCode = ").append(dbl.sqlString(this.invmgrCode));
            }
            bufSql.append(" AND FTradeTypeCode IN (" + operSql.sqlCodes(sTypeCode) + ") ");

            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                dbCarryedAmount = rs.getDouble("FComfMoney");
                cost.setCost(
                    YssD.sub(cost.getCost(),
                             YssD.mul(YssD.div(cost.getCost(), cost.getAmount()), dbCarryedAmount)));
                cost.setMCost(cost.getCost());
                cost.setVCost(cost.getCost());

                cost.setBaseCost(
                    YssD.sub(cost.getBaseCost(),
                             YssD.mul(YssD.div(cost.getBaseCost(), cost.getAmount()), dbCarryedAmount)));
                cost.setBaseMCost(cost.getBaseCost());
                cost.setBaseVCost(cost.getBaseCost());

                cost.setPortCost(
                    YssD.sub(cost.getPortCost(),
                             YssD.mul(YssD.div(cost.getPortCost(), cost.getAmount()), dbCarryedAmount)));
                cost.setPortMCost(cost.getPortCost());
                cost.setPortVCost(cost.getPortCost());

                cost.setAmount(YssD.sub(cost.getAmount(), dbCarryedAmount));
            }

            if (cost.getAmount() != dAmount) {
                cost.setCost(
                    YssD.mul(YssD.div(cost.getCost(), cost.getAmount()), dAmount));
                cost.setMCost(cost.getCost());
                cost.setVCost(cost.getCost());

                cost.setBaseCost(
                    YssD.mul(YssD.div(cost.getBaseCost(), cost.getAmount()), dAmount));
                cost.setBaseMCost(cost.getBaseCost());
                cost.setBaseVCost(cost.getBaseCost());

                cost.setPortCost(
                    YssD.mul(YssD.div(cost.getPortCost(), cost.getAmount()), dAmount));
                cost.setPortMCost(cost.getPortCost());
                cost.setPortVCost(cost.getPortCost());
            }
        } catch (Exception ex) {
            throw new YssException("获取开放式基金业务结转成本出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return cost;
    }

    /**
     * 获取新股新债业务的结转成本
     * @param cost YssCost： 昨日加权平均成本
     * @param sSecurityCode String：证券代码
     * @param dAmount double：结转数量
     * @param sTradeNum String：交易编号
     * @param sTypeCode String：需要统计的结转类型，用逗号隔开
     * @return YssCost
     * @throws YssException
     */
    private YssCost getCarryCostForNewIssue(YssCost cost,
                                            String sSecurityCode,
                                            double dAmount,
                                            String sTradeNum,
                                            String sTypeCode) throws YssException {
        ResultSet rs = null;
        StringBuffer bufSql = new StringBuffer();
        String sInvmgrField = "";
        try {
            sInvmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            bufSql.append(" SELECT * ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("TB_Data_NewIssueTrade"));
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FNum < ").append(dbl.sqlString(sTradeNum));
            bufSql.append(" AND FTransDate = ").append(dbl.sqlDate(dDate));
            bufSql.append(" AND FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            bufSql.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            if (sInvmgrField.trim().length() > 0) {
                bufSql.append(" AND FInvMgrCode = ").append(dbl.sqlString(this.invmgrCode));
            }
            if (attrClsCode != null || attrClsCode.length() > 0) {
                bufSql.append(" AND FAttrClsCode = " + dbl.sqlString(attrClsCode));
            }
            bufSql.append(" AND FTradeTypeCode IN (" + operSql.sqlCodes(sTypeCode) + ") ");

            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                double dbCarryedAmount = rs.getDouble("FAmount");
                cost.setCost(
                    YssD.sub(cost.getCost(),
                             YssD.mul(YssD.div(cost.getCost(), cost.getAmount()), dbCarryedAmount)));
                cost.setMCost(cost.getCost());
                cost.setVCost(cost.getCost());

                cost.setBaseCost(
                    YssD.sub(cost.getBaseCost(),
                             YssD.mul(YssD.div(cost.getBaseCost(), cost.getAmount()), dbCarryedAmount)));
                cost.setBaseMCost(cost.getBaseCost());
                cost.setBaseVCost(cost.getBaseCost());

                cost.setPortCost(
                    YssD.sub(cost.getPortCost(),
                             YssD.mul(YssD.div(cost.getPortCost(), cost.getAmount()), dbCarryedAmount)));
                cost.setPortMCost(cost.getPortCost());
                cost.setPortVCost(cost.getPortCost());

                cost.setAmount(YssD.sub(cost.getAmount(), dbCarryedAmount));
            }

            if (cost.getAmount() != dAmount) {
                cost.setCost(
                    YssD.mul(YssD.div(cost.getCost(), cost.getAmount()), dAmount));
                cost.setMCost(cost.getCost());
                cost.setVCost(cost.getCost());

                cost.setBaseCost(
                    YssD.mul(YssD.div(cost.getBaseCost(), cost.getAmount()), dAmount));
                cost.setBaseMCost(cost.getBaseCost());
                cost.setBaseVCost(cost.getBaseCost());

                cost.setPortCost(
                    YssD.mul(YssD.div(cost.getPortCost(), cost.getAmount()), dAmount));
                cost.setPortMCost(cost.getPortCost());
                cost.setPortVCost(cost.getPortCost());
            }
        } catch (Exception ex) {
            throw new YssException("获取新股新债业务结转成本出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return cost;
    }

    /**
     * 2009-07-03 蒋锦 修改 获取昨日加权平均成本
     * MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
     * @param sSecurityCode String：证券代码
     * @return YssCost：昨日成本
     * @throws YssException
     */
    public YssCost getAvgCost(String sSecurityCode) throws YssException {
        YssCost cost = new YssCost(); ;
        StringBuffer bufSql = new StringBuffer();
        String sInvmgrField = "";
        String sBrokerField = "";
        ResultSet rs = null;
        try {
            sInvmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);

            bufSql.append(" select FSecurityCode, sum(FStorageAmount) as FStorageAmount, ");
            bufSql.append(" sum(FStorageCost) as FStorageCost, ");
            bufSql.append(" sum(FMStorageCost) as FMStorageCost, ");
            bufSql.append(" sum(FVStorageCost) as FVStorageCost, ");
            bufSql.append(" sum(FPortCuryCost) as FPortCuryCost, ");
            bufSql.append(" sum(FMPortCuryCost) as FMPortCuryCost, ");
            bufSql.append(" sum(FVPortCuryCost) as FVPortCuryCost, ");
            bufSql.append(" sum(FBaseCuryCost) as FBaseCuryCost, ");
            bufSql.append(" sum(FMBaseCuryCost) as FMBaseCuryCost, ");
            bufSql.append(" sum(FVBaseCuryCost) as FVBaseCuryCost");
            bufSql.append(" from ").append(pub.yssGetTableName("Tb_Stock_Security"));
            bufSql.append(" where FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            bufSql.append(" and ").append(operSql.sqlStoragEve(dDate));
            bufSql.append(" and FPortCode = ").append(dbl.sqlString(portCode));
            if (sInvmgrField.trim().length() > 0) {
                bufSql.append(" and FAnalysisCode1 = ").append(dbl.sqlString(invmgrCode));
            }
            if (attrClsCode != null && attrClsCode.length() > 0) {
                bufSql.append(" AND FAttrClsCode = ").append(dbl.sqlString(attrClsCode));
            }
            bufSql.append(" and FCheckState = 1 ");
            bufSql.append(" group by FSecurityCode");

            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                cost.setAmount(rs.getDouble("FStorageAmount"));
                cost.setCost(rs.getDouble("FStorageCost"));
                cost.setMCost(rs.getDouble("FMStorageCost"));
                cost.setVCost(rs.getDouble("FVStorageCost"));
                cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
                cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));
                cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));
                cost.setPortCost(rs.getDouble("FPortCuryCost"));
                cost.setPortMCost(rs.getDouble("FMPortCuryCost"));
                cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
            }
        } catch (Exception ex) {
            throw new YssException("获取昨日加权平均成本！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return cost;
    }

    /**
     * 新股新债业务的获取结转的应收应付
     * @param pay SecPecPayBean
     * @param sSecurityCode String
     * @param dAmount double
     * @param sTradeNum String
     * @param sTypeCode String
     * @return SecPecPayBean
     * @throws YssException
     */
    public SecPecPayBean getCarryRecPayForNewIssue(SecPecPayBean pay,
        String sSecurityCode,
        double dAmount,
        String sTradeNum,
        String sTypeCode) throws YssException {
        ResultSet rs = null;
        StringBuffer bufSql = new StringBuffer();
        String sInvmgrField = "";
        try {
            sInvmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            bufSql.append(" SELECT * ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("TB_Data_NewIssueTrade"));
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FNum < ").append(dbl.sqlString(sTradeNum));
            bufSql.append(" AND FTransDate = ").append(dbl.sqlDate(dDate));
            bufSql.append(" AND FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            bufSql.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            if (sInvmgrField.trim().length() > 0) {
                bufSql.append(" AND FInvMgrCode = ").append(dbl.sqlString(this.invmgrCode));
            }
            bufSql.append(" AND FTradeTypeCode IN (" + operSql.sqlCodes(sTypeCode) + ") ");

            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                double dbCarryedAmount = rs.getDouble("FAmount");
                pay.setMoney(
                    YssD.sub(pay.getMoney(),
                             YssD.mul(YssD.div(pay.getMoney(), pay.getAmount()), dbCarryedAmount)));
                pay.setBaseCuryMoney(
                    YssD.sub(pay.getBaseCuryMoney(),
                             YssD.mul(YssD.div(pay.getBaseCuryMoney(), pay.getAmount()), dbCarryedAmount)));
                pay.setPortCuryMoney(
                    YssD.sub(pay.getPortCuryMoney(),
                             YssD.mul(YssD.div(pay.getPortCuryMoney(), pay.getAmount()), dbCarryedAmount)));
                pay.setMMoney(pay.getMoney());
                pay.setVMoney(pay.getMoney());
                pay.setMBaseCuryMoney(pay.getMBaseCuryMoney());
                pay.setVBaseCuryMoney(pay.getBaseCuryMoney());
                pay.setMPortCuryMoney(pay.getMPortCuryMoney());
                pay.setVPortCuryMoney(pay.getVPortCuryMoney());
                pay.setAmount(YssD.sub(pay.getAmount(), dbCarryedAmount));
            }

            if (pay.getAmount() != dAmount) {
                pay.setMoney(YssD.mul(YssD.div(pay.getMoney(), pay.getAmount()), dAmount));
                pay.setBaseCuryMoney(YssD.mul(YssD.div(pay.getBaseCuryMoney(), pay.getAmount()), dAmount));
                pay.setPortCuryMoney(YssD.mul(YssD.div(pay.getPortCuryMoney(), pay.getAmount()), dAmount));
                pay.setMMoney(pay.getMoney());
                pay.setVMoney(pay.getMoney());
                pay.setMBaseCuryMoney(pay.getMBaseCuryMoney());
                pay.setVBaseCuryMoney(pay.getBaseCuryMoney());
                pay.setMPortCuryMoney(pay.getMPortCuryMoney());
                pay.setVPortCuryMoney(pay.getVPortCuryMoney());
                pay.setAmount(dAmount);
            }

        } catch (Exception ex) {
            throw new YssException("获取新股新债业务结转估值增值出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
        return pay;
    }

    /**
     * 开放式基金业务的获取结转的应收应付
     * @param pay SecPecPayBean
     * @param sSecurityCode String
     * @param dAmount double
     * @param sTradeNum String
     * @param sTypeCode String
     * @return SecPecPayBean
     * @throws YssException
     */
    private SecPecPayBean getCarryRecPayForOpenFund(SecPecPayBean pay,
        String sSecurityCode,
        double dAmount,
        String sTradeNum,
        String sTypeCode) throws YssException {
        ResultSet rs = null;
        StringBuffer bufSql = new StringBuffer();
        String sInvmgrField = "";
        double dbCarryedAmount = 0;

        try {
            sInvmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            bufSql.append(" SELECT * ");
            bufSql.append(" FROM ").append("Tb_Data_OpenFundTrade");
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FDataType = ").append(dbl.sqlString("confirm"));
            bufSql.append(" AND FNum < ").append(dbl.sqlString(sTradeNum));
            bufSql.append(" AND FComfDate = ").append(dbl.sqlDate(dDate));
            bufSql.append(" AND FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            bufSql.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            if (sInvmgrField.trim().length() > 0) {
                bufSql.append(" AND FInvMgrCode = ").append(dbl.sqlString(this.invmgrCode));
            }
            bufSql.append(" AND FTradeTypeCode IN (" + operSql.sqlCodes(sTypeCode) + ") ");

            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                dbCarryedAmount = rs.getDouble("FComfMoney");
                pay.setMoney(
                    YssD.sub(pay.getMoney(),
                             YssD.mul(YssD.div(pay.getMoney(), pay.getAmount()), dbCarryedAmount)));
                pay.setBaseCuryMoney(
                    YssD.sub(pay.getBaseCuryMoney(),
                             YssD.mul(YssD.div(pay.getBaseCuryMoney(), pay.getAmount()), dbCarryedAmount)));
                pay.setPortCuryMoney(
                    YssD.sub(pay.getPortCuryMoney(),
                             YssD.mul(YssD.div(pay.getPortCuryMoney(), pay.getAmount()), dbCarryedAmount)));
                pay.setMMoney(pay.getMoney());
                pay.setVMoney(pay.getMoney());
                pay.setMBaseCuryMoney(pay.getMBaseCuryMoney());
                pay.setVBaseCuryMoney(pay.getBaseCuryMoney());
                pay.setMPortCuryMoney(pay.getMPortCuryMoney());
                pay.setVPortCuryMoney(pay.getVPortCuryMoney());
                pay.setAmount(YssD.sub(pay.getAmount(), dbCarryedAmount));
            }

            if (pay.getAmount() != dAmount) {
                pay.setMoney(YssD.mul(YssD.div(pay.getMoney(), pay.getAmount()), dAmount));
                pay.setBaseCuryMoney(YssD.mul(YssD.div(pay.getBaseCuryMoney(), pay.getAmount()), dAmount));
                pay.setPortCuryMoney(YssD.mul(YssD.div(pay.getPortCuryMoney(), pay.getAmount()), dAmount));
                pay.setMMoney(pay.getMoney());
                pay.setVMoney(pay.getMoney());
                pay.setMBaseCuryMoney(pay.getMBaseCuryMoney());
                pay.setVBaseCuryMoney(pay.getBaseCuryMoney());
                pay.setMPortCuryMoney(pay.getMPortCuryMoney());
                pay.setVPortCuryMoney(pay.getVPortCuryMoney());
                pay.setAmount(dAmount);
            }
        } catch (Exception ex) {
            throw new YssException("获取开放式基金业务结转估值增值出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return pay;
    }

    /**
     * 交易子表的获取结转应收应付
     * @param pay SecPecPayBean
     * @param sSecurityCode String
     * @param dAmount double
     * @param sTradeNum String
     * @return SecPecPayBean
     * @throws YssException
     */
    private SecPecPayBean getCarryRecPayForSubTrade(SecPecPayBean pay,
        String sSecurityCode,
        double dAmount,
        String sTradeNum) throws YssException {
        ResultSet rs = null;
        StringBuffer bufSql = new StringBuffer();
        String sInvmgrField = "";
        try {
            sInvmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            bufSql.append(" SELECT * ");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_Subtrade") + " a ");
            bufSql.append(" LEFT JOIN TB_Base_TradeType b ON a.Ftradetypecode = b.Ftradetypecode ");
            bufSql.append(" WHERE FNum < ").append(dbl.sqlString(sTradeNum));
            bufSql.append(" AND FBarGainDate = ").append(dbl.sqlDate(dDate));
            if (sInvmgrField.trim().length() > 0) {
                bufSql.append(" AND FInvmgrCode = ").append(dbl.sqlString(invmgrCode));
            }
            bufSql.append(" AND b.famountind = -1");
            bufSql.append(" AND FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            bufSql.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            bufSql.append(" AND FCheckState = 1 ");

            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                double dbCarryedAmount = rs.getDouble("FTradeAmount");
                pay.setMoney(
                    YssD.sub(pay.getMoney(),
                             YssD.mul(YssD.div(pay.getMoney(), pay.getAmount()), dbCarryedAmount)));
                pay.setBaseCuryMoney(
                    YssD.sub(pay.getBaseCuryMoney(),
                             YssD.mul(YssD.div(pay.getBaseCuryMoney(), pay.getAmount()), dbCarryedAmount)));
                pay.setPortCuryMoney(
                    YssD.sub(pay.getPortCuryMoney(),
                             YssD.mul(YssD.div(pay.getPortCuryMoney(), pay.getAmount()), dbCarryedAmount)));
                pay.setMMoney(pay.getMoney());
                pay.setVMoney(pay.getMoney());
                pay.setMBaseCuryMoney(pay.getMBaseCuryMoney());
                pay.setVBaseCuryMoney(pay.getBaseCuryMoney());
                pay.setMPortCuryMoney(pay.getMPortCuryMoney());
                pay.setVPortCuryMoney(pay.getVPortCuryMoney());
                pay.setAmount(YssD.sub(pay.getAmount(), dbCarryedAmount));
            }

            if (pay.getAmount() != dAmount) {
                pay.setMoney(YssD.mul(YssD.div(pay.getMoney(), pay.getAmount()), dAmount));
                pay.setBaseCuryMoney(YssD.mul(YssD.div(pay.getBaseCuryMoney(), pay.getAmount()), dAmount));
                pay.setPortCuryMoney(YssD.mul(YssD.div(pay.getPortCuryMoney(), pay.getAmount()), dAmount));
                pay.setMMoney(pay.getMoney());
                pay.setVMoney(pay.getMoney());
                pay.setMBaseCuryMoney(pay.getMBaseCuryMoney());
                pay.setVBaseCuryMoney(pay.getBaseCuryMoney());
                pay.setMPortCuryMoney(pay.getMPortCuryMoney());
                pay.setVPortCuryMoney(pay.getVPortCuryMoney());
                pay.setAmount(dAmount);
            }
        } catch (Exception ex) {
            throw new YssException("获取交易数据结转估值增值出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return pay;
    }

    /**
     * 获取结转的应收应付
     * @param sSecurityCode String：证券代码
     * @param dAmount double：结转数量
     * @param sTradeNum String：没用
     * @param operType String：没用
     * @param tradeType String：没用
     * @param sTsfTypeCode String：调拨类型
     * @param sSubTsfTypeCode String：调拨子类型
     * @return SecPecPayBean
     * @throws YssException
     */
    public SecPecPayBean getCarryRecPay(String sSecurityCode,
                                        double dAmount,
                                        String sTradeNum,
                                        String operType,
                                        String tradeType,
                                        String sTsfTypeCode,
                                        String sSubTsfTypeCode) throws YssException {
        SecPecPayBean pay = null;
        try {
            pay = getAvgRecPay(sSecurityCode, dAmount, sTsfTypeCode, sSubTsfTypeCode);
            if (pay == null) {
                return null;
            }

            if (pay.getAmount() != dAmount) {
                pay.setMoney(YssD.round(YssD.mul(YssD.div(pay.getMoney(), pay.getAmount()), dAmount), 2));
                pay.setBaseCuryMoney(YssD.round(YssD.mul(YssD.div(pay.getBaseCuryMoney(), pay.getAmount()), dAmount), 2));
                pay.setPortCuryMoney(YssD.round(YssD.mul(YssD.div(pay.getPortCuryMoney(), pay.getAmount()), dAmount), 2));
                pay.setMMoney(YssD.round(pay.getMoney(), 2));
                pay.setVMoney(YssD.round(pay.getMoney(), 2));
                //--xuqiji 20100407 MS00965 网下非公开发行的新债，当有一部分流通上市后，06FI_B应收债券利息数据有误    QDV4赢时胜（测试）2010年04月01日04_B--//    
                pay.setMBaseCuryMoney(YssD.round(pay.getBaseCuryMoney(), 2));
                pay.setVBaseCuryMoney(YssD.round(pay.getBaseCuryMoney(), 2));
                pay.setMPortCuryMoney(YssD.round(pay.getPortCuryMoney(), 2));
                pay.setVPortCuryMoney(YssD.round(pay.getPortCuryMoney(), 2));
                //---------------------------------------end---------------------------------//
            }
        } catch (Exception ex) {
            throw new YssException("获取结转的估值增值出错！", ex);
        }
        return pay;
    }

    /**
     * 2009-06-26 蒋锦 添加 MS00013 QDV4.1赢时胜（上海）2009年4月20日13_A
     * 用于获取昨日加权平均的估值增值
     * @param sSecurityCode String：证券代码
     * @param dAmount double：获取数量
     * @param sTsfTypeCode String：调拨类型
     * @param sSubTsfTypeCode String：调拨子类型
     * @return SecRecPayBalBean：返回实体
     * @throws YssException
     */
    public SecPecPayBean getAvgRecPay(String sSecurityCode,
                                      double dAmount,
                                      String sTsfTypeCode,
                                      String sSubTsfTypeCode) throws YssException {
        ResultSet rs = null;
        StringBuffer sqlBuf = new StringBuffer();
        String sInvmgrField = "";
        SecPecPayBean pay = null;
        double dbStorageAmount = 0;
        try {
            sInvmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            sqlBuf.append("SELECT rec.*, sto.FStorageAmount");
            sqlBuf.append(" FROM (select FSecurityCode, FAttrClsCode, sum(FBal) as FBal, sum(FMBal) as FMBal,");
            sqlBuf.append(" sum(FVBal) as FVBal, sum(FPortCuryBal) as FPortCuryBal,");
            sqlBuf.append(" sum(FMPortCuryBal) as FMPortCuryBal,");
            sqlBuf.append(" sum(FVPortCuryBal) as FVPortCuryBal,");
            sqlBuf.append(" sum(FBaseCuryBal) as FBaseCuryBal,");
            sqlBuf.append(" sum(FMBaseCuryBal) as FMBaseCuryBal,");
            sqlBuf.append(" sum(FVBaseCuryBal) as FVBaseCuryBal,");
            sqlBuf.append(" sum(FBalF) as FBalF, ");
            sqlBuf.append(" sum(FPortCuryBalF) as FPortCuryBalF, ");
            sqlBuf.append(" sum(FBaseCuryBalF) as FBaseCuryBalF ");
            sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Stock_Secrecpay"));
            sqlBuf.append(" where FSecurityCode = " + dbl.sqlString(sSecurityCode));
            sqlBuf.append(" AND " + operSql.sqlStoragEve(dDate));
            sqlBuf.append(" and FPortCode = " + dbl.sqlString(portCode));
            if (sInvmgrField.trim().length() > 0) {
                sqlBuf.append(" and " + sInvmgrField + " = " + dbl.sqlString(invmgrCode));
            }
            sqlBuf.append(" and FCheckState = 1 ");
            if (sTsfTypeCode.length() > 0) {
                sqlBuf.append(" AND FTsfTypeCode = " + dbl.sqlString(sTsfTypeCode));
            }
            if (sSubTsfTypeCode.length() > 0) {
                sqlBuf.append(" AND FSubTsfTypeCode = " + dbl.sqlString(sSubTsfTypeCode));
            }
            sqlBuf.append(" group by FSecurityCode, FAttrClsCode) rec ");
            sqlBuf.append(" JOIN (SELECT FSecurityCode, FAttrClsCode, SUM(FStorageAmount) AS FStorageAmount ");
            sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Stock_Security"));
            sqlBuf.append(" where FSecurityCode = " + dbl.sqlString(sSecurityCode));
            sqlBuf.append(" AND " + operSql.sqlStoragEve(dDate));
            sqlBuf.append(" and FPortCode = " + dbl.sqlString(portCode));
            if (sInvmgrField.trim().length() > 0) {
                sqlBuf.append(" and " + sInvmgrField + " = " + dbl.sqlString(invmgrCode));
            }
            if (attrClsCode.length() > 0) {
                sqlBuf.append(" AND FAttrClsCode = ").append(dbl.sqlString(attrClsCode));
            }
            sqlBuf.append(" and FCheckState = 1 ");
            sqlBuf.append(" GROUP BY FSecurityCode, FAttrClsCode) sto ON rec.FSecurityCode = ");
            sqlBuf.append(" sto.FSecurityCode ");
            if (attrClsCode.length() > 0) {
                sqlBuf.append(" AND rec.FAttrClsCode = sto.FAttrClsCode");
            }

            rs = dbl.openResultSet(sqlBuf.toString());
            if (rs.next()) {
                pay = new SecPecPayBean();
                pay.setTransDate(dDate);
                pay.setStrPortCode(portCode);
                pay.setStrSecurityCode(sSecurityCode);
                pay.setStrTsfTypeCode(sTsfTypeCode);
                pay.setStrSubTsfTypeCode(sSubTsfTypeCode);
                pay.setMoney(rs.getDouble("FBal"));
                pay.setBaseCuryMoney(rs.getDouble("FBaseCuryBal"));
                pay.setPortCuryMoney(rs.getDouble("FPortCuryBal"));
                pay.setMMoney(rs.getDouble("FMBal"));
                pay.setMBaseCuryMoney(rs.getDouble("FMBaseCuryBal"));
                pay.setMPortCuryMoney(rs.getDouble("FMPortCuryBal"));
                pay.setVMoney(rs.getDouble("FVBal"));
                pay.setVBaseCuryMoney(rs.getDouble("FVBaseCuryBal"));
                pay.setVPortCuryMoney(rs.getDouble("FVPortCuryBal"));
                pay.setMoneyF(rs.getDouble("FBalF"));
                pay.setBaseCuryMoneyF(rs.getDouble("FBaseCuryBalF"));
                pay.setPortCuryMoneyF(rs.getDouble("FPortCuryBalF"));
                pay.setAmount(rs.getDouble("FStorageAmount"));
            }
        } catch (Exception ex) {
            throw new YssException("加权平均获取应收应付余额出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return pay;
    }

    /**shashijie 2011-08-20 STORY 1202 , 添加注视 
     * 保留scale小数
     * @param cost YssCost
     * @param scale int
     */
    public void roundCost(YssCost cost, int scale) {
        cost.setCost(YssD.round(cost.getCost(), scale));
        cost.setBaseCost(YssD.round(cost.getBaseCost(), scale));
        cost.setPortCost(YssD.round(cost.getPortCost(), scale));

        cost.setMCost(YssD.round(cost.getMCost(), scale));
        cost.setBaseMCost(YssD.round(cost.getBaseMCost(), scale));
        cost.setPortMCost(YssD.round(cost.getPortMCost(), scale));

        cost.setVCost(YssD.round(cost.getVCost(), scale));
        cost.setBaseVCost(YssD.round(cost.getBaseVCost(), scale));
        cost.setPortVCost(YssD.round(cost.getPortVCost(), scale));
    }

    protected int getTradeNum(String sNum) {
//      String s = "";
        sNum = YssFun.left(sNum, 15);
        sNum = YssFun.right(sNum, 6);
        return YssFun.toInt(sNum);
    }
    
    /**shashijie 2012-5-7 STORY 2565 */
	public YssCost getCarryCost(String sSecurityCode, double dAmount,
			String sTradeNum, double dBaseCuryRate, double dPortCuryRate,String dDate)
			throws YssException {
		String strSql = "";
        ResultSet rs = null;
        YssCost cost = new YssCost();
        boolean bFlagHV = false; //是否进行了估值成本和核算成本的计算
        int iTmpNum = 0;
        int iNum = 0;
        String isNeedTrade = "";
        try {
            if(this.invmgrCode==null || this.invmgrCode.equals("")){
            	this.invmgrCode=" ";
            }
            //先取出前一日的库存成本和数量
            strSql =
                "select FSecurityCode, sum(FStorageAmount) as FStorageAmount, " +
                " sum(FStorageCost) as FStorageCost,sum(FMStorageCost) as FMStorageCost,sum(FVStorageCost) as FVStorageCost," +
                " sum(FPortCuryCost) as FPortCuryCost,sum(FMPortCuryCost) as FMPortCuryCost,sum(FVPortCuryCost) as FVPortCuryCost," +
                " sum(FBaseCuryCost) as FBaseCuryCost,sum(FMBaseCuryCost) as FMBaseCuryCost,sum(FVBaseCuryCost) as FVBaseCuryCost, " +
                " 'M' as FCostType from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " where FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                " and " +
                operSql.sqlStoragEve(YssFun.toDate(dDate)) + " and FPortCode = " +
                dbl.sqlString(portCode);
            //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
            //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
            if (attrClsCode != null && attrClsCode.length() > 0) {
                strSql += (" AND FAttrClsCode = " + dbl.sqlString(attrClsCode));
            }
            strSql = strSql + " and FCheckState = 1" +
                " group by FSecurityCode,'M' union " +
                //---------------------------------------------------------------
                "select FSecurityCode, sum(FStorageAmount) as FStorageAmount, " +
                " sum(FStorageCost) as FStorageCost,sum(FMStorageCost) as FMStorageCost,sum(FVStorageCost) as FVStorageCost," +
                " sum(FPortCuryCost) as FPortCuryCost,sum(FMPortCuryCost) as FMPortCuryCost,sum(FVPortCuryCost) as FVPortCuryCost," +
                " sum(FBaseCuryCost) as FBaseCuryCost,sum(FMBaseCuryCost) as FMBaseCuryCost,sum(FVBaseCuryCost) as FVBaseCuryCost, " +
                " 'HV' as FCostType from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " where FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                " and " +
                operSql.sqlStoragEve(YssFun.toDate(dDate)) + " and FPortCode = " +
                dbl.sqlString(portCode);
            //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
            //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
            if (attrClsCode != null && attrClsCode.length() > 0) {
                strSql += (" AND FAttrClsCode = " + dbl.sqlString(attrClsCode));
            }
            strSql = strSql + " and FCheckState = 1 group by FSecurityCode,'HV' ";
            rs = dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);//add by xuxuming,2010.01.14..MS00902 
			// ======add by xuxuming,2010.01.14..MS00902
			// 指数信息调整后不能正确获取昨日成本余额=========
			if (attrClsCode != null
					&& (attrClsCode.equals("CEQ") || attrClsCode
							.equals("IDXEQ"))) {// 此类型才是指数信息调整类型的数据
				if (!rs.next()) {// 没有查询到昨日成本余额，则取另一类型的昨日成本余额
					if (attrClsCode.equals("CEQ")) {
						strSql = strSql.replaceAll("CEQ", "IDXEQ");
					} else {
						strSql = strSql.replaceAll("IDXEQ", "CEQ");
					}
					rs.close();// 使用之前，先关闭游标
					rs = dbl.openResultSet(strSql,
							ResultSet.TYPE_SCROLL_INSENSITIVE);// add by
																// xuxuming,2010.01.14..MS00902
				}
				rs.beforeFirst();
			}
			//=============end====================================================
            while (rs.next()) {
                if (rs.getString("FCostType").equalsIgnoreCase("M")) {
                    cost.setMAmount(rs.getDouble("FStorageAmount"));
                    cost.setMCost(rs.getDouble("FMStorageCost"));
                    cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));
                    cost.setPortMCost(rs.getDouble("FMPortCuryCost"));
                } else if (rs.getString("FCostType").equalsIgnoreCase("HV")) {
                    cost.setAmount(rs.getDouble("FStorageAmount"));
                    cost.setCost(rs.getDouble("FStorageCost"));
                    cost.setVCost(rs.getDouble("FVStorageCost"));
                    cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
                    cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));
                    cost.setPortCost(rs.getDouble("FPortCuryCost"));
                    cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
                }
            }
            //2009.04.02 蒋锦 修改 关闭游标
            dbl.closeResultSetFinal(rs);
            if (sTradeNum.length() == 20) { //更改了编号的获取值 sj edit 20080603
                iNum = this.getTradeNum(sTradeNum);
            } else {
                iNum = YssFun.toInt(YssFun.right(sTradeNum, 6)); //统计库存时调用方法时,此编号截去了后5位。
            }
            //--------------获取此组合的加权平均成本的计算方式 sj edit 20080401 ---
            CtlPubPara pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            isNeedTrade = pubpara.getAvgCost(this.portCode);
            //----------------------------------------------------------------
            //story 1936 by zhouwei 20111222 移动加权考虑当天综合业务数据的证券成本与交易关联数据的流入成本 
            //QDV4赢时胜(上海开发部)2011年11月28日01_A
            if(isNeedTrade.equalsIgnoreCase("Yes(Integrated)")){//加权平均成本计算当天综合，关联交易
            	getIntegratedCostByMoving(cost,sSecurityCode);
            	getTradeRelaCostByMoving(cost, sSecurityCode);
            }
            //-----------------end----------
            if (isNeedTrade.equalsIgnoreCase("no")) { //加权平均成本不计算当天交易
                //计算成本
                bFlagHV = true;
                if (cost.getAmount() != 0) {

                    if (YssD.sub(cost.getAmount(), dAmount) != 0) { //如果这笔正好卖空，就直接等于剩余成本不用再计算，否则可能出现尾差 胡昆 20071219
                        cost.setCost(YssD.mul(YssD.div(cost.getCost(),
                            cost.getAmount()), dAmount));
                        cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(),
                            cost.getAmount()), dAmount));
                        cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(),
                            cost.getAmount()), dAmount));
                        cost.setVCost(YssD.mul(YssD.div(cost.getVCost(),
                            cost.getAmount()), dAmount));
                        cost.setBaseVCost(YssD.mul(YssD.div(cost.getBaseVCost(),
                            cost.getAmount()), dAmount));
                        cost.setPortVCost(YssD.mul(YssD.div(cost.getPortVCost(),
                            cost.getAmount()), dAmount));
                    }
                }

                //管理成本
                if (cost.getMAmount() != 0) {
                    if (YssD.sub(cost.getAmount(), dAmount) != 0) { //如果这笔正好卖空，就直接等于剩余成本不用再计算，否则可能出现尾差 胡昆 20071219
                        cost.setMCost(YssD.mul(YssD.div(cost.getMCost(),
                            cost.getMAmount()), dAmount));
                        cost.setBaseMCost(YssD.mul(YssD.div(cost.getBaseMCost(),
                            cost.getMAmount()), dAmount));
                        cost.setPortMCost(YssD.mul(YssD.div(cost.getPortMCost(),
                            cost.getMAmount()), dAmount));
                    }
                }
            } else { //加权平均成本计算当天交易
                //取出当天交易的成本和数量
                strSql = "select FNum,FSecurityCode,a.FTradeTypeCode,(FTradeAmount*FAmountInd) as FTradeAmount," +
                    " 0 as FCost, 0 as FBaseCost, 0 as FPortCost, " +
                    " (FMCost*FAmountInd) as FMCost," +
                    " (FMBaseCuryCost*FAmountInd) as FMBaseCost, (FMPortCuryCost*FAmountInd) as FMPortCost," +
                    " 0 as FVCost, 0 as FVBaseCost, 0 as FVPortCost," +
                    " FBaseCuryRate,FPortCuryRate,'M' as FCostType" +
                    " from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                    " a left join " +
                    " (select FTradeTypeCode,FAmountInd from Tb_Base_TradeType) b on a.FTradeTypeCode = b.FTradeTypeCode" +
                    " where a.FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                    " and a.FBargainDate = " + dbl.sqlDate(dDate) +
                    " and a.FInvMgrCode = " + dbl.sqlString(invmgrCode) +
//               " and a.FBrokerCode = " + dbl.sqlString(brokerCode) +
                    " and a.FPortCode = " + dbl.sqlString(portCode) +
                    " and a.FCheckState = 1 union " +
                    //---------------------------------------------------------------
                    "select FNum,FSecurityCode,a.FTradeTypeCode,(FTradeAmount*FAmountInd) as FTradeAmount,(FCost*FAmountInd) as FCost," +
                    " (FBaseCuryCost*FAmountInd) as FBaseCost, (FPortCuryCost*FAmountInd) as FPortCost," +
                    " 0 as FMCost, 0 as FMBaseCost, 0 as FMPortCost," +
                    " (FVCost*FAmountInd) as FVCost,(FVBaseCuryCost*FAmountInd) as FVBaseCost," +
                    " (FVPortCuryCost*FAmountInd) as FVPortCost," +
                    " FBaseCuryRate,FPortCuryRate,'HV' as FCostType" +
                    " from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                    " a left join " +
                    " (select FTradeTypeCode,FAmountInd from Tb_Base_TradeType) b on a.FTradeTypeCode = b.FTradeTypeCode" +
                    " where a.FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                    " and a.FBargainDate = " + dbl.sqlDate(dDate) +
                    " and a.FPortCode = " + dbl.sqlString(portCode) +
                    " and a.FCheckState = 1 order by FCostType, FNum";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) { //更改了记录集引用 sj edit 20080603
                    iTmpNum = this.getTradeNum(rs.getString("FNum"));
                    //核算成本，估值成本
                    if (rs.getString("FCostType").equalsIgnoreCase("HV")) {
                        if (bFlagHV) {
                            continue;
                        }
                        if (iTmpNum >= iNum) {
                            //计算成本
                            bFlagHV = true;
                            if (cost.getAmount() != 0) {

                                if (YssD.sub(cost.getAmount(), dAmount) != 0) { //如果这笔正好卖空，就直接等于剩余成本不用再计算，否则可能出现尾差 胡昆 20071219
                                    cost.setCost(YssD.mul(YssD.div(cost.getCost(),
                                        cost.getAmount()), dAmount));
                                    cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(),
                                        cost.getAmount()), dAmount));
                                    cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(),
                                        cost.getAmount()), dAmount));
                                    cost.setVCost(YssD.mul(YssD.div(cost.getVCost(),
                                        cost.getAmount()), dAmount));
                                    cost.setBaseVCost(YssD.mul(YssD.div(cost.
                                        getBaseVCost(),
                                        cost.getAmount()), dAmount));
                                    cost.setPortVCost(YssD.mul(YssD.div(cost.
                                        getPortVCost(),
                                        cost.getAmount()), dAmount));
                                }
                            }
                        } else {
                            //累计当天的成本和数量
                            cost.setAmount(YssD.add(cost.getAmount(),
                                rs.getDouble("FTradeAmount")));
                            cost.setCost(YssD.add(cost.getCost(),
                                                  rs.getDouble("FCost")));
                            cost.setVCost(YssD.add(cost.getVCost(),
                                rs.getDouble("FVCost")));
                            cost.setBaseCost(YssD.add(cost.getBaseCost(),
                                rs.getDouble("FBaseCost")));
                            cost.setBaseVCost(YssD.add(cost.getBaseVCost(),
                                rs.getDouble("FVBaseCost")));
                            //组合货币金额＝交易金额*基础汇率/组合汇率
                            cost.setPortCost(YssD.add(cost.getPortCost(),
                                rs.getDouble("FPortCost")));
                            cost.setPortVCost(YssD.add(cost.getPortVCost(),
                                rs.getDouble("FVPortCost")));
                        }
                    } else if (rs.getString("FCostType").equalsIgnoreCase("M")) { //管理成本
                        if (iTmpNum >= iNum) {
                            if (cost.getMAmount() != 0) {
                                if (YssD.sub(cost.getAmount(), dAmount) != 0) { //如果这笔正好卖空，就直接等于剩余成本不用再计算，否则可能出现尾差 胡昆 20071219
                                    cost.setMCost(YssD.mul(YssD.div(cost.getMCost(),
                                        cost.getMAmount()), dAmount));
                                    cost.setBaseMCost(YssD.mul(YssD.div(cost.
                                        getBaseMCost(),
                                        cost.getMAmount()), dAmount));
                                    cost.setPortMCost(YssD.mul(YssD.div(cost.
                                        getPortMCost(),
                                        cost.getMAmount()), dAmount));
                                }
                            }
                            break;
                        } else {
                            cost.setMAmount(YssD.add(cost.getMAmount(),
                                rs.getDouble("FTradeAmount")));
                            cost.setMCost(YssD.add(cost.getMCost(),
                                rs.getDouble("FMCost")));
                            cost.setBaseMCost(YssD.add(cost.getBaseMCost(),
                                rs.getDouble("FMBaseCost")));
                            cost.setPortMCost(YssD.add(cost.getPortMCost(),
                                rs.getDouble("FMPortCost")));

                        }
                    }
                }
            }
            if (!bFlagHV) {
                if (cost.getAmount() != 0) {
                    if (YssD.sub(cost.getAmount(), dAmount) != 0) {
                        cost.setCost(YssD.mul(YssD.div(cost.getCost(),
                            cost.getAmount()), dAmount));
                        cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(),
                            cost.getAmount()), dAmount));
                        cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(),
                            cost.getAmount()), dAmount));
                        cost.setVCost(YssD.mul(YssD.div(cost.getVCost(),
                            cost.getAmount()), dAmount));
                        cost.setBaseVCost(YssD.mul(YssD.div(cost.getBaseVCost(),
                            cost.getAmount()), dAmount));
                        cost.setPortVCost(YssD.mul(YssD.div(cost.getPortVCost(),
                            cost.getAmount()), dAmount));
                        cost.setMCost(YssD.mul(YssD.div(cost.getMCost(),
                                                  cost.getMAmount()),//合并太平版本代码
                                               dAmount));
                        cost.setBaseMCost(YssD.mul(YssD.div(cost.getBaseMCost(),
                        		cost.getMAmount()), dAmount));//合并太平版本代码
                        cost.setPortMCost(YssD.mul(YssD.div(cost.getPortMCost(),
                        		cost.getMAmount()), dAmount));//合并太平版本代码
                    }
                }
            }
            return cost;
        } catch (Exception e) {
            throw new YssException("获取结转成本出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	/**end*/
    
    
}
