package com.yss.main.dao;

import com.yss.dsub.YssPub;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.pojo.cache.YssCost;
import com.yss.util.YssException;

public interface ICostCalculate {
    public void setYssPub(YssPub pub);

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
                                  String sAttrClsCode) throws YssException;

    //20120801 added by liubo.Bug #5051
    //=============================
	public boolean getIfRefreshIntegrated();

	public void setIfRefreshIntegrated(boolean bIfRefreshIntegrated);
    //=============end================
    
    //获取单位成本
    public YssCost getUnitCost() throws YssException;

    public void roundCost(YssCost cost, int scale) throws YssException;

    //获取结转成本
    public YssCost getCarryCost(String sSecurityCode, double dAmount, String sTradeNum,
                                double dBaseCuryRate, double dPortCuryRate) throws
        YssException;
    
    
    /**shashijie 2012-5-7 STORY 2565 重构*/
    //获取结转成本
    public YssCost getCarryCost(String sSecurityCode, double dAmount, String sTradeNum,
                                double dBaseCuryRate, double dPortCuryRate,
                                String dDate) throws YssException;
	/**end*/
    
    
    
    //story 1936 by zhouwei 20111224 获取结转成本（根据通用参数设置）
    public YssCost getNewCarryCost(String sSecurityCode, double dAmount, String sTradeNum,
            double dBaseCuryRate, double dPortCuryRate) throws
            YssException;
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
                                String tradeType) throws YssException;

    //获取旧的成本--2008.4.7--单亮--
    public YssCost getCarryOldCost(String sSecurityCode, double dAmount, String sTradeNum,
                                   double dBaseCuryRate, double dPortCuryRate) throws
        YssException;

    public SecPecPayBean getCarryRecPay(String sSecurityCode,
                                       double dAmount,
                                       String sTradeNum,
                                       String operType,
                                       String tradeType,
                                       String sTsfTypeCode,
                                       String sSubTsfTypeCode) throws YssException;
}
