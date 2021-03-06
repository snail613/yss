package com.yss.main.operdeal.stgstat;

import com.yss.dsub.BaseBean;
import com.yss.dsub.YssPreparedStatement;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;//xuqiji 20091019- MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A

import com.yss.main.operdeal.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssException;
import com.yss.util.YssOperCons;
import com.yss.manager.*;
import java.sql.*;
import com.yss.main.storagemanage.*;
import com.yss.util.YssFun;
import java.util.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;
// xuqiji 20091019- MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
//---------------------------end-----------------------------//
public class BaseStgStatDeal
    extends BaseBean {
    public BaseStgStatDeal() {
    }

    protected java.util.Date startDate;
    protected java.util.Date endDate;
    protected String portCodes;
    protected boolean bReCost = false;
    protected boolean bYearChange = false; //sj
    protected String invmgrSecField;
    protected String brokerSecField;
    protected String catCashField;
    protected String invmgrCashField;

    protected double bal;
    protected double mbal;
    protected double vbal;
    protected double baseBal;

    protected double vbaseBal;
    protected double portBal;

    protected double vportBal;

    private boolean bETFStat = false;//用于判断是否为ETF统计 panjunfang add 20091012， MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A
    private String strETFStatType = "";//根据补票方式进行现金应收应付的统计
	protected String statCodes="";//添加证券代码，目的是只统计本证券的库存，以提高执行速度 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
	
	//start add by huangqirong 2013-04-16 bug #7545 操作类型 判断是估值操作或计提操作 xjjt
	protected String operType = ""; 
	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}
	// end add by huangqirong 2013-04-16 bug #7545 操作类型 判断是估值操作或计提操作 gz , jt
	//---add by songjie 2011.12.31 BUG 3491 QDV4海富通2011年12月22日01_B start---//
	protected String stgFrom = "";//判断是在做什么处理的时候，做的库存统计
	
	public void setStgFrom(String stgFrom){
		this.stgFrom = stgFrom;
	}
	//---add by songjie 2011.12.31 BUG 3491 QDV4海富通2011年12月22日01_B end---//
	
	/**
	 * Setting Method by leeyu 20100330
	 * QDV4中保2010年03月03日03_A MS1011
	 * @param securityCode
	 */
	public void setStatCodes(String statCodes){
		this.statCodes = statCodes;
	}
    /**
     * initStorageStat
     *
     * @param dStartDate Date
     * @param dEndDate Date
     * @param portCode String
     * @param sStatType String
     * @param sReCost String
     */
    public void initStorageStat(Date dStartDate, Date dEndDate, String portCodes,
                                boolean bReCost, boolean bYearChange) throws
        YssException {
        this.startDate = dStartDate;
        this.endDate = dEndDate;
        this.portCodes = portCodes;
        this.bReCost = bReCost;
        this.bYearChange = bYearChange;
    }

    /**
     * 2009-04-25 蒋锦 添加 重载统计库存方法 添加了是否更新估值汇率和估值行情的参数
     * MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
     * @param dStartDate Date
     * @param dEndDate Date
     * @param portCodes String
     * @param bReCost boolean
     * @param bYearChange boolean
     * @param isUpdValPriceRate boolean：是否更新估值汇率和估值行情
     * @throws YssException
     */
    public void stroageStat(Date dStartDate, Date dEndDate, String portCodes,
                            boolean bReCost, boolean bYearChange, boolean isUpdValPriceRate) throws
        YssException {
        initStorageStat(dStartDate, dEndDate, portCodes, bReCost, bYearChange);
        ArrayList alStatData = getStorageStatData(dStartDate);
        //增加了判断是否更新行情汇率 2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
        saveStorageStatData(alStatData, dStartDate, isUpdValPriceRate);
    }
    public void stroageStat1(Date dStartDate, Date dEndDate, String portCodes,
            boolean bReCost, boolean bYearChange, boolean isUpdValPriceRate) throws
		YssException {
		initStorageStat(dStartDate, dEndDate, portCodes, bReCost, bYearChange);
		ArrayList alStatData = getStorageStatData1(dStartDate);// add by wuweiqi 20110114 QDV4工银2010年12月22日01_A 
		//增加了判断是否更新行情汇率 2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
		saveStorageStatData(alStatData, dStartDate, isUpdValPriceRate);
     }

    public void stroageStat(Date dStartDate, Date dEndDate, String portCodes,
                            boolean bReCost, boolean bYearChange) throws
        YssException {
        stroageStat(dStartDate, dEndDate, portCodes, bReCost, bYearChange, true);
    }
    public void stroageStat1(Date dStartDate, Date dEndDate, String portCodes,
                            boolean bReCost, boolean bYearChange) throws
		YssException {
		stroageStat1(dStartDate, dEndDate, portCodes, bReCost, bYearChange, true);
	}

    /**
     * 进行非完全的库存统计1
     * 用于不需要执行所有库存统计方法的地方
     * 2009-04-25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
     * @param dStartDate Date：开始日期
     * @param dEndDate Date：结束日期
     * @param portCodes String：组合代码 SQLIN 形式
     * @param bReCost boolean：是否重新计算成本
     * @param bYearChange boolean：是否年终结转
     * @throws YssException
     */
    public void partStroageStat1(Date dStartDate, Date dEndDate, String portCodes,
                                 boolean bReCost, boolean bYearChange) throws
        YssException {
        initStorageStat(dStartDate, dEndDate, portCodes, bReCost, bYearChange);
        ArrayList alStatData = getPartStorageStatData1(dStartDate);
        saveStorageStatData(alStatData, dStartDate, false);
    }

    public void stroageStat(Date dStartDate, Date dEndDate, String portCodes) throws
        YssException {
        stroageStat(dStartDate, dEndDate, portCodes, true, false);
    }
    
    public void stroageStat1(Date dStartDate, Date dEndDate, String portCodes) throws
       YssException {
       stroageStat1(dStartDate, dEndDate, portCodes, true, false);
   }
    /**
     * getStorageStatData
     *
     * @return ArrayList
     */
    public ArrayList getStorageStatData(java.util.Date dDate) throws
        YssException {
        return null;
    }
    /**
     * getStorageStatData
     *
     * @return ArrayList
     */
    public ArrayList getStorageStatData1(java.util.Date dDate) throws
        YssException {
        return null;
    }
    /**
     * 2009-04-25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》 多用户并发处理优化
     * 获取部分库存统计数据1
     * @param dDate Date：业务处理日期
     * @return ArrayList：结果数据
     * @throws YssException
     */
    public ArrayList getPartStorageStatData1(java.util.Date dDate) throws YssException {
        return null;
    }

    protected void afterSaveStorage(java.util.Date dDate) throws YssException {

    }

    protected void beforeStatStorage(java.util.Date dDate) throws YssException {

    }

    /**
     * saveStorageStatData
     *
     * @param statData ArrayList
     * @param isUpdValPriceRate 是否更新行情 2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
     * @return String
     */
    public String saveStorageStatData(ArrayList statData, java.util.Date dDate, boolean isUpdValPriceRate) throws
        YssException {
        SecurityStorageAdmin security = new SecurityStorageAdmin(); //证券库存
        CashStorageAdmin cash = new CashStorageAdmin();
        InvestStorageAdmin invest = new InvestStorageAdmin();
        SecRecPayStorageAdmin secrecpay = new SecRecPayStorageAdmin();
        CashPayRecStorageAdmin cashrecpay = new CashPayRecStorageAdmin();
        TAStorageAdmin tastorage = new TAStorageAdmin();
        InvestPayRecStorageAdmin investrecpay = new InvestPayRecStorageAdmin();
        Object bean = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
		// ----- QDV4中保2010年1月4日01_B add by jiangshichao 2010.01.06---------
		String sPara = "";
		CtlPubPara pubPara = null;//new CtlPubPara(); // 用户获取现金库存统计方式
		// ---- QDV4中保2010年1月4日01_B end --------------------------------------
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (statData == null) { //当为空时 直接返回为空
                return "";
            }
            beforeStatStorage(dDate); //sj add 20080217 在统计之前进行一些相应的操作
            for (int i = 0; i < statData.size(); i++) {
                bean = (Object) statData.get(i);
                if (bean instanceof SecurityStorageBean) {
                    security.addList( (SecurityStorageBean) bean); //先把数据放到ArrayList 然后进行批量插入
                    //bsecurity = true;  2008.07.31 蒋锦 删除 不再使用标记判断是否调用了子项的库存统计 BUG：0000350
                }
                if (bean instanceof CashStorageBean) {
                    cash.addList( (CashStorageBean) bean); //现金库存
                    //bcash = true;  2008.07.31 蒋锦 删除 不再使用标记判断是否调用了子项的库存统计 BUG：0000350
                }
                if (bean instanceof InvestBean) {
                    invest.addList( (InvestBean) bean); //运营库存
                    //binvest = true;  2008.07.31 蒋锦 删除 不再使用标记判断是否调用了子项的库存统计 BUG：0000350
                }
                if (bean instanceof SecRecPayBalBean) {
                    secrecpay.addList( (SecRecPayBalBean) bean); //证券库存应收应付
                    //bsecrecpay = true;  2008.07.31 蒋锦 删除 不再使用标记判断是否调用了子项的库存统计 BUG：0000350
                }
                if (bean instanceof TAStorageBean) {
                    tastorage.addList( (TAStorageBean) bean); //TA库存
                    //btastorage = true;  2008.07.31 蒋锦 删除 不再使用标记判断是否调用了子项的库存统计 BUG：0000350
                }
                if (bean instanceof CashRecPayBalBean) {
                    cashrecpay.addList( (CashRecPayBalBean) bean); //现金应收应付
                    //bcashrecpay = true;  2008.07.31 蒋锦 删除 不再使用标记判断是否调用了子项的库存统计 BUG：0000350
                }
                if (bean instanceof InvestPayRecBean) {
                    investrecpay.addList( (InvestPayRecBean) bean); //运营应收应付
                    //binvestrecpay = true;  2008.07.31 蒋锦 删除 不再使用标记判断是否调用了子项的库存统计 BUG：0000350
                }
            }
            //2008.07.31 蒋锦 修改 使用 instanceof 关键字代替原来使用 boolean 标记判断是否进行 insert 操作 BUG：0000350
            if (this instanceof StgSecurity) {
                security.setYssPub(pub);
                security.insert(dDate, dDate,
                                this.portCodes, this.bReCost,statCodes);
                //判断是否更新行情汇率 2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
                if (isUpdValPriceRate) {
                    updateSecValPriceRate(portCodes, dDate, dDate); //更新行情
                }
            }
            if (this instanceof StgCash) {
                cash.setYssPub(pub);
                cash.insert(dDate, dDate,
                            this.portCodes ,this.statCodes);	//add by huangqirong 2013-04-15 bug #7545 选中的现金账户
				// ----- QDV4中保2010年1月4日01_B add by jiangshichao
				// 2010.01.06---------
				pubPara = new CtlPubPara(); 
				pubPara.setYssPub(pub);
				sPara = pubPara.getStgCashPara();
				if (sPara.equalsIgnoreCase("1")) {
					cash.updateAvgRate();
				}
				// ---- QDV4中保2010年1月4日01_B end
				// --------------------------------------
                //判断是否更新行情汇率 2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
                //if (isUpdValPriceRate) {
				if (isUpdValPriceRate && sPara.equalsIgnoreCase("0")) {//合并太平版本代码，这里另加上从通用参数中获取的参数判断
                    //统计现金库存时用
                    updateCashValRate(portCodes, dDate, dDate); //更新汇率
                }
            }
            if (this instanceof StgInvest) {
                invest.setYssPub(pub);
                invest.insert(dDate, dDate,
                              this.portCodes);
                
              //add by lidaolong 20110314 #386 增加一个功能，能够自动支付管理费和托管费
                /*investrecpay.setYssPub(pub);
                investrecpay.insertHDSY(dDate, dDate,
                        this.portCodes);*/
              //end by lidaolong 20110314       
                //判断是否更新行情汇率 2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
                if (isUpdValPriceRate) {
                    updateInvestValRate(portCodes, dDate, dDate); // 更新汇率fazmm20071107
                }
            }
            if (this instanceof StgTAStorage) {
                tastorage.setYssPub(pub);
                tastorage.insert(dDate, dDate,
                                 this.portCodes, false); //test
            }
            if (this instanceof StgSecRecPay) {
                secrecpay.setYssPub(pub);
                secrecpay.insert(dDate, dDate,
                                 this.portCodes,statCodes);//添加证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
				// ----- QDV4中保2010年1月4日01_B add by jiangshichao 2010.01.06---------
				pubPara = new CtlPubPara(); 
				pubPara.setYssPub(pub);
				sPara = pubPara.getRecPayPara();
                //判断是否更新行情汇率 2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
                //if (isUpdValPriceRate) {
				if (isUpdValPriceRate && sPara.equalsIgnoreCase("0")) {//合并太平版本代码，这里另加上从通用参数中获取的参数判断
                    //当是统计证券应收应付库存时 也要把行情更新到证券库存表里面去
                    updateSecValPriceRate(portCodes, dDate, dDate); //更新行情
                }
            }
            if (this instanceof StgCashRecPay) {
                cashrecpay.setYssPub(pub);
                cashrecpay.insert(dDate, dDate,
                                  this.portCodes , this.statCodes); //add by huangqirong 2013-04-15 bug #7545 选中的现金账户
				// ----- QDV4中保2010年1月4日01_B add by jiangshichao 2010.01.06---------
				pubPara = new CtlPubPara(); 
				pubPara.setYssPub(pub);
				sPara = pubPara.getRecPayPara();
				// ----- QDV4中保2010年1月4日01_B end -------------------------------------
                //判断是否更新行情汇率 2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
                //if (isUpdValPriceRate) {
				if (isUpdValPriceRate && sPara.equalsIgnoreCase("0")) {//合并太平版本代码，这里另加上从通用参数中获取的参数判断
                    //统计现金应收应付库存时 更新汇率和行情
                    updateCashValRate(portCodes, dDate, dDate); //更新汇率
                }
            }
            if (this instanceof StgInvestRecPay) {
                investrecpay.setYssPub(pub);
                investrecpay.insert(dDate, dDate,
                                    this.portCodes);
        
				// ----- QDV4中保2010年1月4日01_B add by jiangshichao 2010.01.06---------
				pubPara = new CtlPubPara(); 
				pubPara.setYssPub(pub);
				sPara = pubPara.getRecPayPara();
				// ----- QDV4中保2010年1月4日01_B end-------------------------------------									
                //判断是否更新行情汇率 2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
                //if (isUpdValPriceRate) {
				if (isUpdValPriceRate && sPara.equalsIgnoreCase("0")) {//合并太平版本代码，这里另加上从通用参数中获取的参数判断
                    //2008.05.14 蒋锦 添加 统计运营收支库存
                    updateInvestValRate(portCodes, dDate, dDate);
                }
            }
            afterSaveStorage(dDate);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("系统写入库存统计数据时出现异常!\n", e); //by 曹丞 2009.01.22 写入库存统计数据异常 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            //2009.04.27 添加事务的回滚 蒋锦 修改
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            dbl.endTransFinal(bTrans);
        }
    }

    //从估值行情和估值汇率表更新证券库存的行情和汇率
    private void updateSecValPriceRate(String sPortCode,
                                       java.util.Date beginDate,
                                       java.util.Date endDate) throws
        YssException {
        String strSql = "";
        try {
            //2009.07.09 蒋锦 估值行情表新增了属性分类代码作为主键，所以更新证券库存行情时也要相应增加属性分类代码作为查询条件 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A
            //更新证券库存的行情数据
            strSql = "update " + pub.yssGetTableName("tb_stock_security") +
                " a set FMarketPrice = (select FPrice from " +
                pub.yssGetTableName("tb_data_valmktprice") +
                " b where a.FSecurityCode = b.FSecurityCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode and a.FAttrClsCode = b.FAttrClsCode)" +
                " where  exists (select 1 from " +
                pub.yssGetTableName("tb_data_valmktprice") +
                " b where  a.FSecurityCode = b.FSecurityCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode and a.FAttrClsCode = b.FAttrClsCode)" +
                " and (FStorageDate between " + dbl.sqlDate(beginDate) + " and " +
                dbl.sqlDate(endDate) +
                ") and FPortCode in (" + sPortCode + ")";
            dbl.executeSql(strSql);

            //更新证券库存的基础汇率 和 组合汇率
            strSql = "update " + pub.yssGetTableName("tb_stock_security") +
                " a set FBaseCuryRate = (select FBaseRate from " +
                pub.yssGetTableName("tb_data_ValRate") +
                " b where a.FCuryCode = b.FCuryCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode),FPortCuryRate = (select FPortRate from " +
                pub.yssGetTableName("tb_data_ValRate") +
                " c where a.FCuryCode = c.FCuryCode and a.FStorageDate = c.FValDate and a.FPortCode = c.FPortCode)" +
                " where  exists (select 1 from " +
                pub.yssGetTableName("tb_data_ValRate") +
                " b where  a.FCuryCode = b.FCuryCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)" +
                " and (FStorageDate between " + dbl.sqlDate(beginDate) + " and " +
                dbl.sqlDate(endDate) +
                ") and FPortCode in (" + sPortCode + ")";

            dbl.executeSql(strSql);

//         //更新组合汇率
//         strSql = "update " + pub.yssGetTableName("tb_stock_security") +
//               " a set FPortCuryRate = (select FPortRate from " +
//               pub.yssGetTableName("tb_data_ValRate") +
//               " b where a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)" +
//               " where  exists (select 1 from " +
//               pub.yssGetTableName("tb_data_ValRate") +
//               " b where a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)" +
//               " and FStorageDate = " + dbl.sqlDate(dDate) + " and FPortCode = (" + sPortCode + ")";
//         dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("系统进库存统计,在从估值行情和估值汇率表更新证券库存的行情和汇率时出现异常!\n", e); //by 曹丞 2009.01.22 估值行情和估值汇率表更新证券库存的行情和汇率异常 MS00004 QDV4.1-2009.2.1_09A
        }
    }

    public void adjustStorageCost(java.util.Date dStartDate,
                                  java.util.Date dEndDate, String sPortCode) throws
        YssException {
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
    	YssPreparedStatement pst = null;
        //=============end====================
        String strSql = "";
        Connection conn = dbl.loadConnection();
        HashMap hm = new HashMap();
        HashMap cm = new HashMap(); //放置同一证券因投资经理不同而出现的次数。sj edit 20080718
        Hashtable cValue = null; //用于放置所有类型的成本。sj edit 20080721
        ResultSet rs = null;
        double dCost = 0;
        YssCost cost = null;
        String sKey = "";
        boolean bTrans = false;
        int times = 0;
        try {
        	boolean bTPCost =false;//区分是太平资产的库存统计还是QDII的库存统计,合并版本时调整
        	CtlPubPara pubPara = null; //区分太平资产与QD统计库存不一致参数，合并版本时调整 by leeyu
        	pubPara =new CtlPubPara();
        	pubPara.setYssPub(pub);
        	String sPara =pubPara.getNavType();//通过净值表类型来判断
        	if(sPara!=null && sPara.trim().equalsIgnoreCase("new")){
        		bTPCost=false;//国内QDII统计模式
        	}else{
        		bTPCost=true;//太平资产统计模式
        	}
            hm = calculateHVCost(dStartDate, dEndDate, sPortCode,bTPCost);//此方法通过参数控制 合并版本时调整
//         conn.setAutoCommit(false);
            bTrans = true;
            //添加属性分类FAttrClsCode的处理 沈杰 20090901 MS00473:国泰需根据最新的纳斯达克指数100信息来调整即将发行的LOF基金中的股票信息
            //太平资产的库存统计不区分所属分类，这里必须用参数进行区分，合并版本时添加调整
            strSql = "update " + pub.yssGetTableName("Tb_Stock_Security") +
                " set " +
                " FStorageCost = ? " +
                ", FVStorageCost = ? " +
                ", FBaseCuryCost = ?" +
                ", FVBasecuryCost = ?" +
                ", FPortCuryCost = ?" +
                ", FVPortCuryCost = ?" +
                " where FPortCode = ? and FAnalysisCode1 = ? and FAnalysisCode2 = ?" +
                //" and FAttrClsCode = ? and FStorageDate = ?  and FSecurityCode = ?" +
                " and FStorageDate = ?  and FSecurityCode = ? "+(bTPCost?"":" and FAttrClsCode = ? ") +//这里将所属分类放在最后，便于写代码 合并版本时调整
                " and " + dbl.sqlRight("FYearMonth", 2) + " <> '00'";

			//modified by liubo.Story #2145
			//==============================
//            pst = dbl.openPreparedStatement(strSql);
            pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
            strSql = "select * from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FStorageDate between " + dbl.sqlDate(dStartDate) +
                " and " + dbl.sqlDate(dEndDate) + " and " +
                dbl.sqlRight("FYearMonth", 2) + " <> '00'" +
                " and FPortCode in (" + operSql.sqlCodes(sPortCode) + ")" +
                " order by FStorageCost desc,FSecurityCode, FAnalysisCode1,FAttrClsCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                boolean gc = false; //是否需要轧差 sj edit 20080718
                double curTotal = 0.0; //当前的成本总数 sj edit 20080721
                sKey = YssFun.formatDate(rs.getDate("FStorageDate"), "yyyy-MM-dd") +
                    "\f" + rs.getString("FPortCode") + "\f" +
                    rs.getString("FSecurityCode") +
                    "\f" + (bTPCost?"":rs.getString("FAttrClsCode"));//区分太平资产与QDII库存统计  合并版本时调整

                //设置次数的初始值
                if (cm.get(sKey) == null) {
                    cValue = new Hashtable(); //将所有的成本数据设置初始值。
                    cValue.put("Cost", new Double(0));
                    cValue.put("vCost", new Double(0));
                    cValue.put("BaseCost", new Double(0));
                    cValue.put("vBaseCost", new Double(0));
                    cValue.put("PortCost", new Double(0));
                    cValue.put("vPortCost", new Double(0));
                    cm.put(sKey, cValue);
                }
                cost = (YssCost) hm.get(sKey);
                if (cost != null) {
//               if (rs.getString("FPortCode").equalsIgnoreCase("001") &&
//                   rs.getString("FSecurityCode").equalsIgnoreCase("183")){
//                  dCost = YssD.mul(YssD.div(cost.getCost(), cost.getAmount()),
//                                   rs.getDouble("FStorageAmount"));
//                  System.out.println("投资经理："+rs.getString("FAnalysisCode1"));
//                  System.out.println("券商："+rs.getString("FAnalysisCode2"));
//                  System.out.println(dCost);
//               }
                    //这里的成本是包括利息的 所以要减去利息
//                getBuyRate(rs.getString("FSecurityCode"),rs.getString("FPortCode"),rs.getDate("FStorageDate"),
//                           rs.getString("FAnalysisCode1"),rs.getString("FAnalysisCode2"),rs.getString("FAnalysisCode3"));

                    cValue = (Hashtable) cm.get(sKey);
                    if (cost.getAmount() != 0) {
                        dCost = YssD.mul(YssD.div(cost.getCost(), cost.getAmount()),
                                         rs.getDouble("FStorageAmount"));
                        curTotal = ( (Double) cValue.get("Cost")).doubleValue(); //获取上次的总值。
                        if (YssD.round(Math.abs(YssD.sub(cost.getCost(),
                            YssD.add(curTotal, dCost))), bTPCost?4:2) == 0.0) { //如果是最后一次出现这一证券(总的成本-上次的总值-此投资经理的成本)，则用轧差计算。sj edit 20080718//调整舍入比例，合并太平版本调整
                            dCost = YssD.sub(cost.getCost(),
                                             YssD.round(curTotal, bTPCost?4:2));//调整舍入比例，合并太平片调整
                            gc = true; //之后的相关计算都用轧差计算。
                        } else {
                            dCost = dCost;
                            cValue.put("Cost", new Double(curTotal + dCost));
                            cm.put(sKey, cValue);
                        }
                    } else {
                        dCost = 0;
                    }
                    pst.setDouble(1, YssD.round(dCost, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                    if (cost.getAmount() != 0) {
                    	curTotal = ( (Double) cValue.get("vCost")).doubleValue(); //获取上次的总值。避免值重复使用 by leeyu 20100819 合并太平版本调整
                        dCost = YssD.mul(YssD.div(cost.getVCost(), cost.getAmount()),
                                         rs.getDouble("FStorageAmount"));
                        if (gc) {
                            curTotal = ( (Double) cValue.get("vCost")).doubleValue();
                            dCost = YssD.sub(cost.getVCost(),
                                             YssD.round(curTotal, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                        } else {
                            dCost = dCost;
                            cValue.put("vCost", new Double(curTotal + dCost));
                            cm.put(sKey, cValue);
                        }
                    } else {
                        dCost = 0;
                    }
                    pst.setDouble(2, YssD.round(dCost, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                    if (cost.getAmount() != 0) {
                    	curTotal = ( (Double) cValue.get("BaseCost")).doubleValue(); //获取上次的总值。避免值重复使用 by leeyu 20100819 合并太平版本调整
                        dCost = YssD.mul(YssD.div(cost.getBaseCost(), cost.getAmount()),
                                         rs.getDouble("FStorageAmount"));
                        if (gc) {
                            curTotal = ( (Double) cValue.get("BaseCost")).doubleValue();
                            dCost = YssD.sub(cost.getBaseCost(),
                                             YssD.round(curTotal, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                        } else {
                            dCost = dCost;
                            cValue.put("BaseCost", new Double(curTotal + dCost));
                            cm.put(sKey, cValue);
                        }
//                  dCost = 0;
                    } else {
                        dCost = 0;
                    }
                    pst.setDouble(3, YssD.round(dCost, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                    if (cost.getAmount() != 0) {
                    	curTotal = ( (Double) cValue.get("vBaseCost")).doubleValue(); //获取上次的总值。避免值重复使用 by leeyu 20100819 合并太平版本调整
                        dCost = YssD.mul(YssD.div(cost.getBaseVCost(), cost.getAmount()),
                                         rs.getDouble("FStorageAmount"));
                        if (gc) {
                            curTotal = ( (Double) cValue.get("vBaseCost")).doubleValue();
                            dCost = YssD.sub(cost.getBaseVCost(),
                                             YssD.round(curTotal, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                        } else {
                            dCost = dCost;
                            cValue.put("vBaseCost", new Double(curTotal + dCost));
                            cm.put(sKey, cValue);
                        }
//                  dCost = 0;
                    } else {
                        dCost = 0;
                    }
                    pst.setDouble(4, YssD.round(dCost, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                    if (cost.getAmount() != 0) {
                    	curTotal = ( (Double) cValue.get("PortCost")).doubleValue(); //获取上次的总值。避免值重复使用 by leeyu 20100819 合并太平版本调整
                        dCost = YssD.mul(YssD.div(cost.getPortCost(), cost.getAmount()),
                                         rs.getDouble("FStorageAmount"));
                        if (gc) {
                            curTotal = ( (Double) cValue.get("PortCost")).doubleValue();
                            dCost = YssD.sub(cost.getPortCost(),
                                             YssD.round(curTotal, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                        } else {
                            dCost = dCost;
                            cValue.put("PortCost", new Double(curTotal + dCost));
                            cm.put(sKey, cValue);
                        }
//                  dCost = 0;
                    } else {
                        dCost = 0;
                    }
                    pst.setDouble(5, YssD.round(dCost, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                    if (cost.getAmount() != 0) {
                    	curTotal = ( (Double) cValue.get("vPortCost")).doubleValue(); //获取上次的总值。避免值重复使用 by leeyu 20100819 合并太平版本调整
                        dCost = YssD.mul(YssD.div(cost.getPortVCost(), cost.getAmount()),
                                         rs.getDouble("FStorageAmount"));
                        if (gc) {
                            curTotal = ( (Double) cValue.get("vPortCost")).doubleValue();
                            dCost = YssD.sub(cost.getPortVCost(),
                                             YssD.round(curTotal, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                        } else {
                            dCost = dCost;
                            cValue.put("vPortCost", new Double(curTotal + dCost));
                            cm.put(sKey, cValue);
                        }
//                  dCost = 0;
                    } else {
                        dCost = 0;
                    }
                    pst.setDouble(6, YssD.round(dCost, bTPCost?4:2));//调整舍入比例，合并太平版本调整
                    pst.setString(7, rs.getString("FPortCode"));
                    pst.setString(8, rs.getString("FAnalysisCode1"));
                    pst.setString(9, rs.getString("FAnalysisCode2"));
                    //pst.setString(10, rs.getString("FAttrClsCode"));//将此句调整到下面执行
                    pst.setDate(10, rs.getDate("FStorageDate"));
                    pst.setString(11, rs.getString("FSecurityCode"));
                    if(!bTPCost){//这里只有国内QDII版本才用所属分类  合并版本时调整
                    	pst.setString(12, rs.getString("FAttrClsCode"));
                    }
                    pst.executeUpdate();
                }
            }
//         conn.commit();
//         conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("系统进库存统计,在调整证券库存时出现异常!\n", e); //by 曹丞 2009.01.22 调整证券库存异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private HashMap calculateHVCost(java.util.Date dStartDate,
                                    java.util.Date dEndDate, String sPortCode,
                                    boolean bTPCost) throws//添加参数用于判断是否为太平资产库存调整,合并版本时添加
        YssException {
        String strSql = "";
        ResultSet rs = null;
        HashMap hm = new HashMap();
        YssCost cost = null;
        String sKey = "";
        try {
            //添加属性分类FAttrClsCode的处理 沈杰 20090901 MS00473:国泰需根据最新的纳斯达克指数100信息来调整即将发行的LOF基金中的股票信息
        	//太平资产的库存统计不区分所属分类，这里必须用参数进行区分，合并版本时添加调整
            strSql = "select FStorageDate,FPortCode,FSecurityCode,sum(FStorageAmount) as FStorageAmount," +
                " sum(FStorageCost) as FStorageCost,sum(FVStorageCost) as FVStorageCost," +
                " sum(FBaseCuryCost) as FBaseCuryCost,sum(FVBasecuryCost) as FVBasecuryCost," +
                " sum(FPortCuryCost) as FPortCuryCost,sum(FVPortCuryCost) as FVPortCuryCost" +
                //delete by songjie 2011.04.14 BUG 1677 QDV4太平2011年04月06日01_B
//                (bTPCost?"":",FAttrClsCode" )+//当为太平资产库存统计时不区分所属分类,合并版本时添加
                " ,FAttrClsCode" +//add by songjie 2011.04.14 BUG 1677 QDV4太平2011年04月06日01_B
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FStorageDate between " + dbl.sqlDate(dStartDate) +
                " and " + dbl.sqlDate(dEndDate) +
                " and FPortCode in("+operSql.sqlCodes(sPortCode)+")"+//添加组合条件
                " and " + dbl.sqlRight("FYearMonth", 2) + " <> '00'" +
                " group by FStorageDate,FPortCode,FSecurityCode" +
                //delete by songjie 2011.04.14 BUG 1677 QDV4太平2011年04月06日01_B
//                (bTPCost?"":",FAttrClsCode");//当为太平资产库存统计时不区分所属分类,合并版本时添加
                ",FAttrClsCode";//add by songjie 2011.04.14 BUG 1677 QDV4太平2011年04月06日01_B
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sKey = YssFun.formatDate(rs.getDate("FStorageDate"), "yyyy-MM-dd") +
                    "\f" + rs.getString("FPortCode") + "\f" +
                    rs.getString("FSecurityCode")
                    //delete by songjie 2011.04.14 BUG 1677 QDV4太平2011年04月06日01_B
//                    + "\f" + (bTPCost?"":rs.getString("FAttrClsCode"));//当为太平资产库存统计时不区分所属分类,合并版本时添加
                    + "\f" + rs.getString("FAttrClsCode");//add by songjie 2011.04.14 BUG 1677 QDV4太平2011年04月06日01_B
                cost = new YssCost();
                cost.setAmount(rs.getDouble("FStorageAmount"));
                cost.setCost(rs.getDouble("FStorageCost"));
                cost.setVCost(rs.getDouble("FVStorageCost"));
                cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
                cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));
                cost.setPortCost(rs.getDouble("FPortCuryCost"));
                cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
                hm.put(sKey, cost);
            }
            return hm;
        } catch (Exception e) {
            throw new YssException("系统进库存统计,在计算估值金额时出现异常!\n", e); //by 曹丞 2009.01.22 计算估值金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //得到应收利息
    private void getBuyRate(String strSecurityCode, String strPortCode,
                            java.util.Date dDate, String strAny1, String strAny2,
                            String strAny3) throws YssException { //当调整成本的时候  要把它买入 或者卖出所带入的利息 进行相应的加减
        double buyRate = 0;
        ResultSet rs = null;
        try {
            String strSql = "select * from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " where FSecurityCode=" + dbl.sqlString(strSecurityCode) +
                " and FStorageDate= " + dbl.sqlDate(dDate)
                + " and FAnalysisCode1=" + dbl.sqlString(strAny1) +
                " and FAnalysisCode2=" + dbl.sqlString(strAny2) +
                " and FAnalysisCode3 =" + dbl.sqlString(strAny3) +
                " and FTsfTypeCode='06' and FSubTsfTypeCode='06FI_B'" +
                " and " + //不取期初数 sj add
                dbl.sqlRight("FYearMonth", 2) + " <> '00'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.bal = rs.getDouble("FBal");
                this.mbal = rs.getDouble("FMBal");
                this.vbal = rs.getDouble("FVBal");
                this.baseBal = rs.getDouble("FBaseCuryBal");

                this.vbaseBal = rs.getDouble("FVBaseCuryBal");
                this.portBal = rs.getDouble("FPortCuryBal");

                this.vportBal = rs.getDouble("FVPortCuryBal");
            }
        } catch (Exception e) {
            throw new YssException("获取应收应付的买入利息出错!\n");
        }

    }

    private void updateCashValRate(String sPortCode, java.util.Date beginDate,
                                   java.util.Date endDate) throws YssException {
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("tb_stock_cash") +
                " a set FBaseCuryRate = (select FBaseRate from " +
                pub.yssGetTableName("tb_data_ValRate") +
                " b where a.FCuryCode = b.FCuryCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)," +
                " FPortCuryRate = (select FPortRate from " +
                pub.yssGetTableName("tb_data_ValRate") +
                " b where a.FCuryCode = b.FCuryCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)" +
                " where  exists (select 1 from " +
                pub.yssGetTableName("tb_data_ValRate") +
                " b where a.FCuryCode = b.FCuryCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)" +
                " and (FStorageDate between " + dbl.sqlDate(beginDate) + " and " +
                dbl.sqlDate(endDate) +
                " ) and FPortCode in (" + sPortCode + ")";
            dbl.executeSql(strSql);

//         strSql = "update " + pub.yssGetTableName("tb_stock_cash") +
//               " a set FBaseCuryRate = (select FBaseRate from " +
//               pub.yssGetTableName("tb_data_ValRate") +
//               " b where a.FCuryCode = b.FCuryCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)" +
//               " where  exists (select 1 from " +
//               pub.yssGetTableName("tb_data_ValRate") +
//               " b where a.FCuryCode = b.FCuryCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)" +
//               " and FStorageDate = " + dbl.sqlDate(dDate) + " and FPortCode = (" + sPortCode + ")";
//         dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("系统进库存统计,在从汇率表更新现金库存的行情和汇率时出现异常!\n", e); //by 曹丞 2009.01.22 汇率表更新现金库存的行情和汇率异常 MS00004 QDV4.1-2009.2.1_09A
        }
    }

    private void updateInvestValRate(String sPortCode, java.util.Date beginDate,
                                     java.util.Date endDate) throws YssException {
        //更新运营库存的汇率fazmm20071107
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("tb_stock_invest") +
                " a set FBaseCuryRate = (select FBaseRate from " +
                pub.yssGetTableName("tb_data_ValRate") +
                " b where a.FCuryCode = b.FCuryCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)," +
                " FPortCuryRate = (select FPortRate from " +
                pub.yssGetTableName("tb_data_ValRate") +
                " b where a.FCuryCode = b.FCuryCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)" +
                " where  exists (select 1 from " +
                pub.yssGetTableName("tb_data_ValRate") +
                " b where a.FCuryCode = b.FCuryCode and a.FStorageDate = b.FValDate and a.FPortCode = b.FPortCode)" +
                " and (FStorageDate between " + dbl.sqlDate(beginDate) + " and " +
                dbl.sqlDate(endDate) +
                " ) and FPortCode in (" + sPortCode + ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("系统进库存统计,在从汇率表更新运营库存的行情和汇率时出现异常!\n", e); //by 曹丞 2009.01.22 汇率表更新运营库存的行情和汇率异常 MS00004 QDV4.1-2009.2.1_09A
        }
    }
	// xuqiji 20091019- MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
    public void setBETFStat(boolean bETFStat) {
        this.bETFStat = bETFStat;
    }

    public boolean isBETFStat() {
        return bETFStat;
    }

	public String getStrETFStatType() {
		return strETFStatType;
	}

	public void setStrETFStatType(String strETFStatType) {
		this.strETFStatType = strETFStatType;
	}
	//------------------------------end-----------------------------//
	 /**
     * 获取有条件的组合代码,主要是针对各家公司的补票方式不同而进行不同的处理方式  xuqiji 20091207 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @return
     * @throws YssException
     */
    public String[] getDifferencePortCode() throws YssException{
        String [] sPortCode = null;
        String sHuaBaoPortCode ="";
        String sOtherCompanyPortCode ="";
        String [] sDifferencePortCode = new String[2];
        ETFParamSetBean paramSet = null;// ETF参数的实体类
		ETFParamSetAdmin paramSetAdmin = null;
		HashMap etfParam = null;
    	try{
            sPortCode = this.portCodes.split(",");
            paramSetAdmin = new ETFParamSetAdmin();
			paramSetAdmin.setYssPub(pub);
			etfParam = paramSetAdmin.getETFParamInfo(portCodes); // 根据已选组合代码用于获取相关ETF参数数据
			for(int i=0; i< sPortCode.length; i++){
				if(etfParam.containsKey(sPortCode[i].substring(1,sPortCode[i].length()-1))){
					paramSet = (ETFParamSetBean) etfParam.get(sPortCode[i].substring(1,sPortCode[i].length()-1));
					if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMESUB) ||
							paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) || 
								paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_ONE)
								|| paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)){//STORY #1434 QDV4易方达基金2011年7月27日01_A panjunfang modify 20110810
						sHuaBaoPortCode += sPortCode[i] + ",";
					}else{
						sOtherCompanyPortCode += sPortCode[i] + ",";
					}
				}
			}
			if(sOtherCompanyPortCode.endsWith(",")){
    			sOtherCompanyPortCode = sOtherCompanyPortCode.substring(0,sOtherCompanyPortCode.length()-1);
    		}
			if(sHuaBaoPortCode.endsWith(",")){
    			sHuaBaoPortCode = sHuaBaoPortCode.substring(0,sHuaBaoPortCode.length()-1);
    		}
			sDifferencePortCode[0] = sHuaBaoPortCode;
			sDifferencePortCode[1] = sOtherCompanyPortCode;
    	}catch (Exception e) {
			throw new YssException("获取有条件的组合代码出错！",e);
		}
    	return sDifferencePortCode;
    }
}
