package com.yss.main.taoperation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.CommonPretFun;
import com.yss.main.operdeal.linkInfo.BaseLinkInfoDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.report.navrep.CtlNavRep;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.parasetting.FeeBean;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.parasetting.PubParaBean;
import com.yss.main.syssetting.RightBean;
import com.yss.manager.CashTransAdmin;
import com.yss.pojo.cache.YssFeeType;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.vsub.YssFinance;

/**
 * @author Admin
 *
 */
public class TaTradeBean
    extends BaseDataSettingBean implements IDataSetting {
	
	//--------- add by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A -----------//
	public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }
	
	public String getAssetGroupCode() {
		return assetGroupCode;
	}
	//--------- MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A -----------//
	
    public void setFilterType(TaTradeBean filterType) {
        this.filterType = filterType;
    }

    public void setStrCashAcctCode(String strCashAcctCode) {
        this.strCashAcctCode = strCashAcctCode;
    }

    public void setStrSellTypeCode(String strSellTypeCode) {
        this.strSellTypeCode = strSellTypeCode;
    }

    public void setDPortCuryRate(double dPortCuryRate) {
        this.dPortCuryRate = dPortCuryRate;
    }

    public void setStrAnalysisCode1(String strAnalysisCode1) {
        this.strAnalysisCode1 = strAnalysisCode1;
    }

    public void setStrAnalysisCode3(String strAnalysisCode3) {
        this.strAnalysisCode3 = strAnalysisCode3;
    }

    public void setStrSellNetCode(String strSellNetCode) {
        this.strSellNetCode = strSellNetCode;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setDIncomeBal(double dIncomeBal) {
        this.dIncomeBal = dIncomeBal;
    }

    public void setDIncomeNotBal(double dIncomeNotBal) {
        this.dIncomeNotBal = dIncomeNotBal;
    }

    public void setStrCashAcctName(String strCashAcctName) {
        this.strCashAcctName = strCashAcctName;
    }

    public void setDSettleDate(Date dSettleDate) {
        this.dSettleDate = dSettleDate;
    }

    public void setStrAnalysisCode2(String strAnalysisCode2) {
        this.strAnalysisCode2 = strAnalysisCode2;
    }

    public void setDSellPrice(double dSellPrice) {
        this.dSellPrice = dSellPrice;
    }

    public void setStrAnalysisName2(String strAnalysisName2) {
        this.strAnalysisName2 = strAnalysisName2;
    }

    public void setDSellMoney(double dSellMoney) {
        this.dSellMoney = dSellMoney;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public void setStrAnalysisName1(String strAnalysisName1) {
        this.strAnalysisName1 = strAnalysisName1;
    }

    public void setDConfimDate(Date dConfimDate) {
        this.dConfimDate = dConfimDate;
    }

    public void setStrSellNetName(String strSellNetName) {
        this.strSellNetName = strSellNetName;
    }

    public void setStrSellTypeName(String strSellTypeName) {
        this.strSellTypeName = strSellTypeName;
    }

    public void setStrNum(String strNum) {
        this.strNum = strNum;
    }

    public void setStrOldNum(String strOldNum) { // wdy add
        this.strOldNum = strOldNum;
    }

    public void setDSellAmount(double dSellAmount) {
        this.dSellAmount = dSellAmount;
    }

    public void setStrCuryName(String strCuryName) {
        this.strCuryName = strCuryName;
    }

    public void setDBaseCuryRate(double dBaseCuryRate) {
        this.dBaseCuryRate = dBaseCuryRate;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public void setStrAnalysisName3(String strAnalysisName3) {
        this.strAnalysisName3 = strAnalysisName3;
    }

    public void setStrCuryCode(String strCuryCode) {
        this.strCuryCode = strCuryCode;
    }

//    public void setIsOnlyColumn(String isOnlyColumns) {
//        this.isOnlyColumns = isOnlyColumns;
//    }

    public void setDScale(double dScale) {
        this.dScale = dScale;
    }

    public void setDCost(double dCost) {
        this.dCost = dCost;
    }

    public void setDAmount(double dAmount) {
        this.dAmount = dAmount;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    public void setSTranNum(String sTranNum) {
        this.sTranNum = sTranNum;
    }

    public void setBeginDate(java.util.Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setSettleState(int settleState) {
        this.settleState = settleState;
    }

    public void setSNums(String sNums) {
        this.sNums = sNums;
    }

    public void setSPortClsName(String sPortClsName) {
        this.strPortClsName = sPortClsName;
    }

    public void setSPortClsCode(String sPortClsCode) {
        this.strPortClsCode = sPortClsCode;
    }

    public void setDTradeDate(Date dTradeDate) {
        this.dTradeDate = dTradeDate;
    }

    public TaTradeBean getFilterType() {
        return filterType;
    }

    public String getStrCashAcctCode() {
        return strCashAcctCode;
    }
    //20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
    public double getdCashBalFee() {
		return dCashBalFee;
	}
	//20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
	public void setdCashBalFee(double dCashBalFee) {
		this.dCashBalFee = dCashBalFee;
	}
    

    public String getStrSellTypeCode() {
        return strSellTypeCode;
    }

    public double getDPortCuryRate() {
        return dPortCuryRate;
    }

    public String getStrAnalysisCode1() {
        return strAnalysisCode1;
    }

    public String getStrAnalysisCode3() {
        return strAnalysisCode3;
    }

    public String getStrSellNetCode() {
        return strSellNetCode;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public double getDIncomeBal() {
        return dIncomeBal;
    }

    public double getDIncomeNotBal() {
        return dIncomeNotBal;
    }

    public String getStrCashAcctName() {
        return strCashAcctName;
    }

    public java.util.Date getDSettleDate() {
        return dSettleDate;
    }

    public String getStrAnalysisCode2() {
        return strAnalysisCode2;
    }

    public double getDSellPrice() {
        return dSellPrice;
    }

    public String getStrAnalysisName2() {
        return strAnalysisName2;
    }

    public double getDSellMoney() {
        return dSellMoney;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public String getStrPortName() {
        return strPortName;
    }

    public String getStrAnalysisName1() {
        return strAnalysisName1;
    }

    public java.util.Date getDConfimDate() {
        return dConfimDate;
    }

    public String getStrSellNetName() {
        return strSellNetName;
    }

    public String getStrSellTypeName() {
        return strSellTypeName;
    }

    public String getStrOldNum() { // wdy add
        return strOldNum;
    }

    public String getStrNum() {
        return strNum;
    }

    public double getDSellAmount() {
        return dSellAmount;
    }

    public String getStrCuryName() {
        return strCuryName;
    }

    public double getDBaseCuryRate() {
        return dBaseCuryRate;
    }

    public String getFees() {
        return fees;
    }

    public String getStrAnalysisName3() {
        return strAnalysisName3;
    }

    public String getStrCuryCode() {
        return strCuryCode;
    }

    public java.util.Date getDTradeDate() {
        return dTradeDate;
    }

//    public String getIsOnlyColumn() {
//        return isOnlyColumns;
//    }

    public double getDScale() {
        return dScale;
    }

    public double getDCost() {
        return dCost;
    }

    public double getDAmount() {
        return dAmount;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public String getSTranNum() {
        return sTranNum;
    }

    public java.util.Date getBeginDate() {
        return beginDate;
    }

    public int getSettleState() {
        return settleState;
    }

    /**add---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
    public String getConvertType() {
        return strConvertType;
    }
    public double getSplitNetValue() {
        return dSplitNetValue;
    }
    public void setConvertType(String strConvertType) {
        this.strConvertType=strConvertType;
    }
    public void setSplitNetValue(double dSplitNetValue) {
        this.dSplitNetValue=dSplitNetValue;
    }
	/**end---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框*/
	
    public String getSNums() {
        return sNums;
    }

    public String getSPortClsName() {
        return strPortClsName;
    }

    public String getSPortClsCode() {
        return strPortClsCode;
    }

    public double getdPaidInMoney() {
		return dPaidInMoney;
	}

	public void setdPaidInMoney(double dPaidInMoney) {
		this.dPaidInMoney = dPaidInMoney;
	}

    public TaTradeBean() {
    }
    //===add by xuxuming,20091026.MS00760.需要增加基金拆分的功能    QDV4交银施罗德2009年10月22日01_A======
    public double getdSplitRatio() {
		return dSplitRatio;
	}

	public void setdSplitRatio(double dSplitRatio) {
		this.dSplitRatio = dSplitRatio;
	}
    //=========end============================================================
	
	//------ modify by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A ------//
	private String assetGroupCode = "";
	private boolean bOverGroup = false;
	//------ MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A ------//
	
	boolean analy1 = false;
    boolean analy2 = false;
    private TaTradeBean filterType;
    private String strNum = ""; //编号
    private String strOldNum = ""; //旧编号   wdy add
    private String strPortCode = ""; //组合代码
    private String strPortName = ""; //组合名称
    private String strPortClsCode = ""; //添加组合分级字段
    private String strPortClsName = "";
    
    private double dPaidInMoney = 0.0;	//20120611 added by liubo.Story #2683.实收基金金额


	private String strCashAcctCode = ""; //现金帐户代码
    private String strCashAcctName = ""; //现金帐户名称
    private String strCuryCode = ""; //销售的货币代码
    private String strCuryName = "";

    private String strSellNetCode = ""; //销售网点代码
    private String strSellNetName = ""; //销售网点名称
    private String strSellTypeCode = ""; //销售网点类型
    private String strSellTypeName = "";

    private java.util.Date dMarkDate; //基准日期
    private java.util.Date dTradeDate; //交易日期
    private java.util.Date dConfimDate; //确认日期
    private java.util.Date dSettleDate; //结算日期

    private String strAnalysisCode1 = ""; //分析代码1
    private String strAnalysisName1 = "";
    private String strAnalysisCode2 = ""; //分析代码2
    private String strAnalysisName2;
    private String strAnalysisCode3 = ""; //分析代码3
    private String strAnalysisName3 = "";
    
    /**add---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
	private String strConvertType="";//折算类型
	private double dSplitNetValue= 0;//拆分前单位净值
	/**end---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框*/

    private double dPortCuryRate = 0; //组合汇率
    private double dBaseCuryRate = 0; //基础汇率

    private double dSellAmount = 0; //交易数量
    private double dSellPrice = 0; //交易价格
    private double dSellMoney = 0; //交易总额

    private double dSettleMoney = 0; //实际结算金额
    private double dIncomeNotBal = 0; //未实现损益平准金
    private double dIncomeBal = 0; //实现损益平准金

    private boolean flag = true; //代表是新增或者复制 alter by sunny
    private String fees = "";
    private String strDesc = "";
//    private String isOnlyColumns = "";
    private java.util.Date beginDate;
    private java.util.Date endDate;
//------------------------------------------//
    private java.util.Date confimBeginDate; //确认开始日期
    private java.util.Date confimEndDate; //确认结束日期
//------------------------------------------//
    private double dScale = 0;//计算未实现损益平准金过度变量(未乘以基准金额)
    private double dCost = 0;
    private double dAmount = 0;

    private double fee = 0.0;
    private int settleState = 0; //结算方式 liyu 添加 1028
    private String sTranNum = "";
    private String sNums; //存放多条 Num
    CashTransAdmin tranAdmin = null; // wdy add

    private double dBeMarkMoney = 0; //基准金额 sj modify 20081125
    private double dSplitRatio = 0;  //拆分比例. add by xuxuming,20091026.MS00760.需要增加基金拆分的功能    QDV4交银施罗德2009年10月22日01_A  
    private String sRecycled = null; //保存未解析前的字符串
    //--------xuqiji 20091019- MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A----------------------//
    private double dConvertNum = 0;//份额折算数量
    private double dCashBal = 0;//现金差额
    private double dCashRepAmount = 0;//现金替代金额
    private String bETFData="false";//是否是ETF数据
    private String multAuditString = ""; //批量处理数据  add by fangjiang 2010.08.11 MS01568 QDV4上海2010年08月06日02_AB 
    private int accWayState=0;//story 2253 add by zhouwei 标识通参设置的分类核算方式，默认为0既净值的方式(博时),1为基准净值(嘉实)
    
    /*add by huangqirong 2012-07-10 STORY #1434 */
    private String dCashReplaceDate = "";
    private String dCashBalanceDate = ""; 
    
    public String getdCashBalanceDate() {
		return dCashBalanceDate;
	}

	public void setdCashBalanceDate(String dCashBalanceDate) {
		this.dCashBalanceDate = dCashBalanceDate;
	}

	public String getdCashReplaceDate() {
		return dCashReplaceDate;
	}

	public void setdCashReplaceDate(String dCashReplaceDate) {
		this.dCashReplaceDate = dCashReplaceDate;
	}
	/*	end  */
    
    
    /**
     * MS00002
     * QDV4.1赢时胜（上海）2009年9月28日01_A
     * 修改人：操忠虎
     * 修改时间：2009/11/23
     */
    private double dCashBalFee=0;	// 现金差额使用费
	//------------------------end 20091019-------------------------------//
    
    /**shashijie 2011-10-18 STORY 1589 */
    private double FPortdegree = 0;//本位币份额
    /**end*/
    
    private double yfshf = 0; // 应付赎回费
    
	private double xsfsr = 0; // 销售费收入
	
	public double getYfshf() {
		return yfshf;
	}

	public void setYfshf(double yfshf) {
		this.yfshf = yfshf;
	}

	public double getXsfsr() {
		return xsfsr;
	}

	public void setXsfsr(double xsfsr) {
		this.xsfsr = xsfsr;
	}

	//20121212 added by liubo.Bug #6584
	//=========================
    public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	//=========================


    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1 ";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            //------QDV4赢时胜上海2009年12月21日06_B MS00884 by xuqiji-------------//
           if (this.filterType.isOnlyColumns.equals("1")) {
                 sResult = sResult + " and 1=2 ";
                 return sResult;
              }
           //----------------------------end------------------------------------//
            //添加，修改，复制时数据过大，只查询当天的数据  edit by jc
            if (this.dTradeDate != null &&
                !YssFun.formatDate(this.dTradeDate).equals("9998-12-31")) {
                sResult = sResult + " and a.FTradeDate = " +
                    dbl.sqlDate(this.dTradeDate);
                //edited by zhouxiang MS01509 TA交易结算界面选定交易日期和其他任意字段后系统仅按照交易日期进行查询 
                //return sResult;
                //end--- by zhouxiang MS01509 TA交易结算界面选定交易日期和其他任意字段后系统仅按照交易日期进行查询 
            }
            //----------------------------------------------jc

            if (this.filterType.strCuryCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.strCuryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strSellTypeCode.length() != 0) {
                sResult = sResult + " and a.FSellType like '" +
                    filterType.strSellTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strSellNetCode.trim().length() != 0) { //彭鹏 2008.2.3 补全判断条件 BUG:0000045
                sResult = sResult + " and a.FSellNetCode = '" +
                    filterType.strSellNetCode + "'";
            }
            if (this.filterType.strPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.strPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strCashAcctCode.length() != 0) {
                sResult = sResult + " and a.FCashAccCode = '" +
                    filterType.strCashAcctCode.replaceAll("'", "''") + "'";
            }
            
            //------ modify by nimengjing 2010.11.22 BUG #464 点击查询没有提示信息
            if (this.filterType.strAnalysisCode1.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode1 like '" +
                    filterType.strAnalysisCode1.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strAnalysisCode2.length() == 1) {
                sResult = sResult + " and a.FAnalysisCode2 = " +
                    filterType.strAnalysisCode2;
            }

            if (this.filterType.strAnalysisCode3.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode3 like '" +
                    filterType.strAnalysisCode3.replaceAll("'", "''") + "%'";
            }
            //---------------------------------BUG #464------------------------------//
 
            if (this.filterType.dMarkDate != null &&
                !YssFun.formatDate(filterType.dMarkDate).equals("9998-12-31")) {
                sResult = sResult + " and a.FMarkDate = " +
                    dbl.sqlDate(filterType.dMarkDate);
            }

            if (this.filterType.dTradeDate != null &&
                !YssFun.formatDate(filterType.dTradeDate).equals("9998-12-31")) {
                sResult = sResult + " and a.FTradeDate = " +
                    dbl.sqlDate(filterType.dTradeDate);
            }

            if (this.filterType.dConfimDate != null &&
                !YssFun.formatDate(filterType.dConfimDate).equals("9998-12-31")) {
                sResult = sResult + " and a.FConfimDate = " +
                    dbl.sqlDate(filterType.dConfimDate);
            }
            if (this.filterType.dSettleDate != null &&
                !YssFun.formatDate(filterType.dSettleDate).equals("9998-12-31")) {
                sResult = sResult + " and a.FSettleDate = " +
                    dbl.sqlDate(filterType.dSettleDate);
            }
            if (this.filterType.endDate != null &&
                !YssFun.formatDate(filterType.endDate).equals("9998-12-31")) {
                if (this.filterType.beginDate != null &&
                    !YssFun.formatDate(filterType.beginDate).equals(
                        "9998-12-31")) {
                    sResult += " and a.FTradeDate between " +
                        dbl.sqlDate(this.filterType.beginDate) +
                        " and " + dbl.sqlDate(this.filterType.endDate);
                }
            }
            if (filterType.strPortClsCode != null &&
                filterType.strPortClsCode.length() > 0) {
                sResult += " and a.FPortClsCode like'" +
                    filterType.strPortClsCode.replaceAll("'", "''") + "%'"; ;
            }
            //----------------------确认日查寻交易数据 sj edit 20080806 bug 0000365 -------//
            if (this.filterType.confimEndDate != null &&
                !YssFun.formatDate(filterType.confimEndDate).equalsIgnoreCase(
                    "9998-12-31")) {
                if (this.filterType.confimBeginDate != null &&
                    !YssFun.formatDate(filterType.confimBeginDate).equalsIgnoreCase(
                        "9998-12-31")) {
                    sResult += " and a.FConfimDate between " +
                        dbl.sqlDate(this.filterType.confimBeginDate) +
                        " and " + dbl.sqlDate(this.filterType.confimEndDate);
                }
            }
            //--------------------------------------------------------------------------//
            /**shashijie 2011-10-18 STORY 1589 */
            if (this.filterType.FPortdegree != 0 ) {
                sResult += " and a.FPortdegree = " +
                    dbl.sqlString(String.valueOf(this.FPortdegree));
            }
            /**end*/
        }
        return sResult;

    }

    /**
     * getListViewData1
     *
     * @return String
     *
     * 修改日期：2008年7月23日
     * 修改人：  蒋春
     * BugNo：0000310 1
     */
    public String getListViewData1() throws YssException {
    	System.out.println("---");
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String sAry[] = null;
        try {
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
			RightBean right = new RightBean();
			right.setYssPub(pub);
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
        	
            sHeader = this.getListView1Headers();
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "TA"); //判断分析代码存不存在
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "TA");
            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_TA); //获得分析代码
//QDV4赢时胜上海2009年12月21日06_B MS00884 by xuqiji
//            if (! (this.isOnlyColumns.equalsIgnoreCase("1") &&
//                   this.isOnlyColumns.trim().length() > 0
//                   && this.filterType.isOnlyColumns.equalsIgnoreCase("1") && //2008-5-27 单亮添加
//                   this.filterType.isOnlyColumns.trim().length() > 0) //2008-5-27 单亮添加
//                ) { //by liyu 1218 isOnlyColumn首次加载时为空
//------------------------end--------------------------//
            strSql =
                    "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                    " d.FPortName as FPortName,d.FSubAssetType as FSubAssetType,e.FSellNetName as FSellNetName,f.FSellTypeName as FSellTypeName,g.FCuryName as FCuryName,o.FCashAccName as FCashAccName" +//xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                    //", h.FPortClsName as FPortClsName " + //sj add xuqiji 20100415 QDV4赢时胜上海2009年12月21日06_B MS00884 
                    sAry[0] + " ,h.FPortClsName as FPortClsName " +
                    " from " + pub.yssGetTableName("Tb_TA_Trade") + " a " +            
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    //----------------------------------------------------------------------------------------------------
                    " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                    //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                    pub.yssGetTableName("Tb_Para_Portfolio") +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                    //edit by songjie 2011.03.16 不以最大的启用日期查询数据
                    " select FPortCode, FPortName, FStartDate,FSubAssetType from " +//xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                    pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where FCheckState = 1) d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                    //----------------------------------------------------------------------------------------------------
                    " left join (select FSellNetCode,FSellNetName from " +
                    pub.yssGetTableName("Tb_TA_SellNet") +
                    ") e on a.FSellNetCode = e.FSellNetCode" +
                    " left join (select FSellTypeCode,FSellTypeName from " +
                    pub.yssGetTableName("Tb_TA_SellType") +
                    ") f on a.FSellType = f.FSellTypeCode" +
                    //----------------------------------------------------------------------------------------------------
                    " left join (select FCuryCode,FCuryName from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    ") g on a.FCuryCode = g.FCuryCode" +
                    //--------------------------------------------------组合分级--------------------------------------
                    " left join (select distinct(FPortClsCode) as FPortClsCode,FPortClsName from " +
                    pub.yssGetTableName("TB_TA_Portcls") +
                    ") h on h.FPortClsCode = a.FPortClsCode " +
                    //----------------------------------------------------------------------------------------------------
                    //----------------------------------------------------------------------------------------------------
                    " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                    //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                    pub.yssGetTableName("Tb_Para_CashAccount") +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                    " select FCashAccCode, FCashAccName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where FCheckState = 1" +") o on a.FCashAccCode = o.FCashAccCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                    sAry[1]
                    //--modify by 黄啟荣 2011-06-01 story #937 --用于系统查询出来的结果必须与用户的浏览权限一致。
    				+buildFilterSql()
    				//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
//    					+" and a.FPortcode in (select distinct(tsu.fportcode) as fportcode from(select fportcode from Tb_Sys_Userright"
//      		        		+" where fusercode ="+dbl.sqlString(pub.getUserCode())					    
//      					    +" and frighttype = 'port'"
//      					    +" and FOPERTYPES like '%brow%'"
//      					    +" and frightcode = 'tatrade') tsu"
//      					    +" inner join "+pub.yssGetTableName("tb_Para_Portfolio")
//      					    +" tpp on tpp.fportcode=tsu.fportcode"
//      					    +" where tpp.fenabled=1"
//      					    +" and tpp.FCheckState=1)";
    				//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
    				//add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B
				+ " and a.FPortcode in (" + operSql.sqlCodes(right.getUserPortCodes("tatrade")) + ")";
    				strSql+=" order by a.FCheckState, a.FCreateTime desc";
    				System.out.println(strSql);
    			//---end---	
                //QDV4赢时胜上海2009年12月21日06_B MS00884 by xuqiji
                //rs = dbl.openResultSet(strSql);
                yssPageInationBean.setsQuerySQL(strSql);
                yssPageInationBean.setsTableName("TATrade");
                /**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
    			//yssPageInationBean.setsTableName("SubTrade");
                /**end*/
                rs =dbl.openResultSet(yssPageInationBean);
                //QDV4赢时胜上海2009年12月21日06_B MS00884 by xuqiji
                while (rs.next()) {
                    bufShow.append( (rs.getString("FNum") + "").trim()).append("\t");
                    bufShow.append(YssFun.formatDate( (rs.getString("FTRADEDATE")))).
                        append("\t");
                    bufShow.append( (rs.getString("FPORTCODE") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FSELLNETName") + "").trim()).   //前台自定义表头显示的是销售类型名称 modify by wangzuochun 2009.09.04 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A
                        append("\t");
                    bufShow.append( (rs.getString("FSellTypeName") + "").trim()).  //前台自定义表头显示的是销售类型名称 modify by wangzuochun 2009.08.31 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A
                        append("\t");
                    bufShow.append( (rs.getString("FCURYCODE") + "").trim()).
                        append("\t");
                    bufShow.append(YssFun.formatNumber(rs.getDouble("FSELLMONEY"),
                        "#,##0.##")).append("\t");
                    //----------------------------------------------------------------
                    bufShow.append(YssFun.formatNumber(rs.getDouble("FBeMarkMoney"),
                        "#,##0.##")).append("\t");
                    //----------------------------------------------------------------
                    bufShow.append(YssFun.formatNumber(rs.getDouble("FSELLAMOUNT"),
                        "#,##0.##")).append("\t");
                    //fanghaoln 20090611 BUG:MS00480 QDV4华夏2009年6月04日02_B 保证listveiw里和allshow里的数据致
                    bufShow.append( (rs.getDouble("FSELLPRICE") + "").trim()).append("\t");
                    bufShow.append(YssFun.formatNumber(rs.getDouble("FINCOMEBAL"),
                        "#,##0.##")).append("\t");
                    bufShow.append(YssFun.formatNumber(rs.getDouble("FINCOMENOTBAL"),
                        "#,##0.##")).append("\t");
                    //=========add by xuxuming,20091026.MS00760.==================
                    bufShow.append(YssFun.formatNumber(rs.getDouble("FSPLITRATIO"),
                    "#,##0.##")).append("\t");//拆分比例
                    //=====end=======================================================
                    bufShow.append( (rs.getString("FANALYSISCODE1") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FANALYSISCODE2") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FANALYSISCODE3") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FCashAccName") + "").trim()).  //前台自定义表头显示的是现金账户名称 modify by wangzuochun 2009.08.31 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A
                        append("\t");
                    bufShow.append(YssFun.formatDate( (rs.getString("FCONFIMDATE")))).
                        append("\t");
                    bufShow.append(YssFun.formatDate( (rs.getString("FSETTLEDATE")))).
                        append("\t");
                    bufShow.append( (rs.getString("FDESC") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FCREATOR") + "").trim()).append(
                        "\t");
                    bufShow.append( (rs.getString("FCREATETIME") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FCHECKUSER") + "").trim()).append(
                        "\t");
                    bufShow.append( (rs.getString("FCHECKTIME") + "").trim()).append(
                        YssCons.YSS_LINESPLITMARK);
                    setResultSetAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            //}//QDV4赢时胜上海2009年12月21日06_B MS00884 by xuqiji
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_TRD_AUTO+ "," +
            		/*added by yeshenghong 2013-5-23 Story 3759 */
            		YssCons.YSS_TA_DiscountType);
					/*end by yeshenghong 2013-5-23 Story 3759 */
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()  
                /**注释原因,开发TA交易结算页面分页,传至时需要顺序需要改变*/
                //+ yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr //QDV4赢时胜上海2009年12月21日06_B MS00884 by xuqiji
                /**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
                					/*added by yeshenghong 2013-5-23 Story 3759 */
                + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr ;
            						/*end by yeshenghong 2013-5-23 Story 3759 */
            	/**end*/
        } catch (Exception e) {
            throw new YssException("获取交易数据信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }

    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strNum = rs.getString("FNum") + "";
        this.strSellNetCode = rs.getString("FSellNetCode") + "";
        this.strSellNetName = rs.getString("FSellNetName") + "";

        this.strSellTypeCode = rs.getString("FSellType") + "";
        this.strSellTypeName = rs.getString("FSellTypeName") + "";

        this.strCuryCode = rs.getString("FCuryCode") + "";
        this.strCuryName = rs.getString("FCuryName") + "";
        this.strPortCode = rs.getString("FPortCode") + "";
        this.strPortName = rs.getString("FPortName") + "";
        //如果装载分析代码，进入前台时错误，导致报错fazmm20071021
        if (analy1) {
            this.strAnalysisCode1 = rs.getString("FInvMgrCode") + "";
            this.strAnalysisName1 = rs.getString("FInvMgrName") + "";
        } else {
            this.strAnalysisCode1 = "";
            this.strAnalysisName1 = "";
        }
        if (analy2) {
        	//edit by licai 20101213 BUG #642 打开TA交易数据界面，点击【查询按钮】，系统报错。
        	//modify by huangqirong story #937  打开TA交易数据界面，点击【查询按钮】，系统报错。
            //this.strAnalysisCode2 = rs.getString("FBrokerCode") + "";
            //this.strAnalysisName2 = rs.getString("FBrokerName") + "";
        	this.strAnalysisCode2 = rs.getString("FCatCode") + "";
            this.strAnalysisName2 = rs.getString("FCatName") + "";
        	//---end---
            //edit by licai 20101213 BUG #642 =====================================end
        } else {
            this.strAnalysisCode2 = "";
            this.strAnalysisName2 = "";
        }

        this.strAnalysisCode3 = "";
        this.strAnalysisName3 = "";

        this.strCashAcctCode = rs.getString("FCashAccCode") + "";
        this.strCashAcctName = rs.getString("FCashAccName") + "";

        this.dMarkDate = rs.getDate("FMarkDate");
        this.dTradeDate = rs.getDate("FTradeDate");
        this.dConfimDate = rs.getDate("FConfimDate");
        this.dSettleDate = rs.getDate("FSettleDate");

        this.dPortCuryRate = rs.getDouble("FPortCuryRate");
        this.dBaseCuryRate = rs.getDouble("FBaseCuryRate");

        this.dSellAmount = rs.getDouble("FSellAmount");
        this.dSellPrice = rs.getDouble("FSellPrice");
        this.dSellMoney = rs.getDouble("FSellMoney");

        this.strDesc = rs.getString("FDesc") + "";
        this.fee = rs.getDouble("FTradeFee1");

        this.dIncomeBal = rs.getDouble("FIncomeBal");
        this.dIncomeNotBal = rs.getDouble("FIncomeNotBal");
        this.settleState = rs.getInt("FSettleState"); // liyu 添加 结算方式  1028
        this.strPortClsCode = rs.getString("FPortClsCode");
        this.strPortClsName = rs.getString("FPortClsName");
        this.dSettleMoney = rs.getDouble("FSettleMoney");
        //------------------------------------------------------------------------
        this.dBeMarkMoney = rs.getDouble("FBeMarkMoney");
        //------------------------------------------------------------------------
        //------xuqiji 20091019 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A---------------------//
        if("0106".equals(rs.getString("FSubAssetType"))){ //------ 防止报空指针异常 modify by wangzuochun 2010.09.01  MS01680    TA交易数据，直接点击查询报错    QDV4赢时胜(上海开发部)2010年08月31日02_B    
        	this.bETFData="true";
        	this.dConvertNum=rs.getDouble("FConvertNum");
        	this.dCashBal=rs.getDouble("FCashBal");
        	this.dCashRepAmount=rs.getDouble("FCashRepAmount");
        	if(rs.getDate("FCASHBALANCEDATE") != null)//add by huangqirong 2012-07-10 STORY #1434
        		this.dCashBalanceDate = YssFun.formatDate((java.util.Date)rs.getDate("FCASHBALANCEDATE"));	//add by huangqirong 2012-07-10 STORY #1434
        	if(rs.getDate("FCASHREPLACEDATE") != null)//add by huangqirong 2012-07-10 STORY #1434
        		this.dCashReplaceDate = YssFun.formatDate((java.util.Date)rs.getDate("FCASHREPLACEDATE"));	//add by huangqirong 2012-07-10 STORY #1434
        	/**
        	  * MS00002
        	  * QDV4.1赢时胜（上海）2009年9月28日01_A
        	  * 修改人：操忠虎
        	  * 修改时间：2009/11/23
        	 */
        	this.dCashBalFee=rs.getDouble("FCASHBALFEE");
        }else{
        	this.bETFData="false";
        }
		//------------------------------------------end 20091019---------------------------------//
		//-------add by xuxuming,20091026.MS00760.-------------
        this.dSplitRatio = rs.getDouble("FSplitRatio");//拆分比例        
		//------------------------------------------------------------------------
        /**shashijie 2011-10-18 STORY 1589 */
        this.FPortdegree = rs.getDouble("FPortdegree");//本位币份额
        /**end*/
        
        this.dPaidInMoney = rs.getDouble("FPaidInMoney");	//20120611 added by liubo.Story #2683.实收基金金额
        /**add---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
       this.strConvertType=rs.getString("FConvertType");
       this.dSplitNetValue=rs.getDouble("FSplitNetValue");
        /**end---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框*/
        loadFees(rs);
        super.setRecLog(rs);

    }

    public void loadFees(ResultSet rs) throws SQLException, YssException {
        String sName = "";
        double dFeeMoney = 0;
        double dTotalFee = 0;
        StringBuffer buf = new StringBuffer();
        FeeBean fee = new FeeBean();
        fee.setYssPub(pub);

        for (int i = 1; i <= 8; i++) {
            if (rs.getString("FFeeCode" + i) != null &&
                rs.getString("FFeeCode" + i).trim().length() > 0) {
                fee.setFeeCode(rs.getString("FFeeCode" + i));
                fee.getSetting();
                //------ add by wangzuochun 2010.09.11 MS01708    交易结算未结算中进行结算时会报错    QDV4建行2010年09月08日01_B    
                //------ 根据交易子表中的费用代码去查费用设置中的费用，若费用不存在，则跳过此次循环；
                if (fee.getFeeCode() == null){
                	continue;
                }
                //----------MS01708-----------//
                sName = fee.getFeeName();
                if (rs.getString("FTradeFee" + i) != null) {
                    dFeeMoney = rs.getDouble("FTradeFee" + i);
                }
                dTotalFee = YssD.add(dTotalFee, dFeeMoney);
                buf.append(rs.getString("FFeeCode" + i)).append("\n");
                buf.append(sName).append("\n");
                buf.append(dFeeMoney).append("\n");
                buf.append(fee.buildRowStr().replaceAll("\t", "~")).append("\f\n");
            }
        }
        if (buf.toString().length() > 2) {
            buf.append("total").append("\n");
            buf.append("Total: ").append("\n");
            buf.append(dTotalFee).append("\n");
            fee.setAccountingWay("0"); //不计入成本
            buf.append(fee.buildRowStr().replaceAll("\t", "~"));
            this.fees = buf.toString();
        } else {
            this.fees = "";
        }

    }

    /**
     * getListViewData2
     *用于显示TA交易结算的数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
			RightBean right = new RightBean();
			right.setYssPub(pub);
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
        	sHeader = this.getListView1Headers();
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "TA"); //判断分析代码存不存在
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "TA");
            if (!this.isOnlyColumns.equalsIgnoreCase("1") &&
                this.isOnlyColumns.length() != 0) {
            	/**add---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
            	strSql=this.returnSql();
            	/**end---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
                if (! (this.settleState == 99)) { //彭鹏 2008.2.3 修改判断条件 BUG:0000045
                    strSql += "where y.FSettleState = " + this.settleState;
                }
                //rs = dbl.openResultSet(strSql);
                /**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
                yssPageInationBean.setsQuerySQL(strSql);
    			yssPageInationBean.setsTableName("SubTrade_TaTrade2");
                rs = dbl.openResultSet(yssPageInationBean);
                /**end*/
                while (rs.next()) {
                    bufShow.append(super.buildRowShowStr(rs,
                        this.getListView1ShowCols())).
                        append(YssCons.YSS_LINESPLITMARK);

                    setResultSetAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_TRD_AUTO+ "," +
            		/*added by yeshenghong 2013-5-23 Story 3759 */
            		YssCons.YSS_TA_DiscountType);
					/*end by yeshenghong 2013-5-23 Story 3759 */
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() 
                /**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
                					/*added by yeshenghong 2013-5-23 Story 3759 */
                + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;
            						/*end by yeshenghong 2013-5-23 Story 3759 */
            	/**end*/
        } catch (Exception e) {
            throw new YssException("获取交易数据信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }
    
    /**
    * huhuichao 2013-5-17 STORY  3759 获取用于显示TA交易结算的数据的SQL语句
    * @return String
    */
    private String returnSql() throws YssException{
		try {
			String strSql = "";
			String sAry[] = null;
			RightBean right = new RightBean();
			right.setYssPub(pub);
			sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_TA); // 获得分析代码
			strSql = " select y.* from ( " +
            "select a.FNUm,a.fsellnetcode,a.fportcode,a.FPortClsCode,a.fselltype,a.fcurycode," +
            /**shashijie 2011-11-08 STORY 1589*/
            " a.FPortdegree , a.FConvertType,a.FSplitNetValue,"+
            /**end*/
            "a.fanalysiscode1,a.fanalysiscode2,a.fanalysiscode3,a.fcashacccode," +
            "a.fsellmoney,a.fsellamount,a.fsellprice,a.fincomenotbal,a.fincomebal," +
            "a.fconfimdate,a.ftradedate,a.fsettledate,a.fportcuryrate,a.fbasecuryrate," +
            "a.ftradefee1,a.ffeecode1,a.ftradefee2,a.ffeecode2,a.ftradefee3,a.ffeecode3," +
            "a.ftradefee4,a.ffeecode4,a.ftradefee5,a.ffeecode5,a.ftradefee6,a.ffeecode6," +
            "a.ftradefee7,a.ffeecode7,a.ftradefee8,a.ffeecode8,a.fsettlestate as fcheckstate," +
            "a.fsettlestate,a.fdesc,a.fcreator,a.fcreatetime,a.fcheckuser,a.fchecktime," +
            //"a.FInsCashAccCode," +
            "a.FMarkDate,a.FSettleMoney,a.FConvertNum,a.FCashBal,a.FCASHBALFEE,a.FCashRepAmount," + //差两个字段 by 
            //--------------------------------------liyu 080402//xuqiji 20091019 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
            "a.fcashbalancedate,a.fcashreplacedate,a.FBeMarkMoney as FBeMarkMoney," +
            //----------------------------------
          //-----add by xuxuming,20091026.MS00760.---
            "a.FSplitRatio as FSplitRatio," +//拆分比例
            //----------------------------------
            "b.FUserName as FCreatorName, c.FUserName as FCheckUserName, a.Fcheckstate as Fcheckstates," +
            " d.FPortName as FPortName,d.FSubAssetType as FSubAssetType,e.FSellNetName as FSellNetName,"+//xuqiji 20091019 MS00002 
            "f.FSellTypeName as FSellTypeName,g.FCuryName as FCuryName,o.FCashAccName as FCashAccName" +//QDV4.1赢时胜（上海）2009年9月28日01_A
            sAry[0] + ",h.FPortClsName as FPortClsName " +
            ", a.FPaidInMoney" +	//20120611 added by liubo.Story #2683.基金实收金额
            " from " + pub.yssGetTableName("Tb_TA_Trade") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            //----------------------------------------------------------------------------------------------------
            " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            pub.yssGetTableName("Tb_Para_Portfolio") +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            //edit by songjie 2011.03.16 不以最大的启用日期查询数据
            " select FPortCode, FPortName, FStartDate ,FSubAssetType from " +//xuqiji 20091019 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
            pub.yssGetTableName("Tb_Para_Portfolio") +
            " where FCheckState = 1) d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----------------------------------------------------------------------------------------------------
            " left join (select FSellNetCode,FSellNetName from " +
            pub.yssGetTableName("Tb_TA_SellNet") +
            ") e on a.FSellNetCode = e.FSellNetCode" +
            " left join (select FSellTypeCode,FSellTypeName from " +
            pub.yssGetTableName("Tb_TA_SellType") +
            ") f on a.FSellType = f.FSellTypeCode" +
            //----------------------------------------------------------------------------------------------------
            " left join (select FCuryCode,FCuryName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            ") g on a.FCuryCode = g.FCuryCode" +
            //--------------------------------------组合分级---------------------------------------------------------
            " left join (select distinct(FPortClsCode) as FPortClsCode,FPortClsName from " +
            pub.yssGetTableName("tb_ta_portcls") +
            ") h on h.FPortClsCode=a.FPortClsCode " +
            //----------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------
            " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            pub.yssGetTableName("Tb_Para_CashAccount") +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " select FCashAccCode, FCashAccName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            pub.yssGetTableName("Tb_Para_CashAccount") +
            " where FCheckState = 1) o on a.FCashAccCode = o.FCashAccCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据

            sAry[1]
          //--modify by 黄啟荣 2011-06-01 story #937 --用于系统查询出来的结果必须与用户的浏览权限一致。
			+buildFilterSql()+" and a.fselltype <>'09' and a.FCheckState=1 "    // add by dongqingsong 2013-09-17 新禅道bug#79573      	
            +" and a.FCheckState=1 "   
            //---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
//        		+" and a.FPortcode in (select distinct(tsu.fportcode) as fportcode from(select fportcode from Tb_Sys_Userright"
//		        		+" where fusercode ="+dbl.sqlString(pub.getUserCode())					    
//					    +" and frighttype = 'port'"
//					    +" and FOPERTYPES like '%brow%'"
//					    +" and frightcode = 'tatradesettleview') tsu"
//					    +" inner join "+pub.yssGetTableName("tb_Para_Portfolio")
//					    +" tpp on tpp.fportcode=tsu.fportcode"
//					    +" where tpp.fenabled=1"
//					    +" and tpp.FCheckState=1)";
            //---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
			//add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B
			+ " and a.FPortcode in (" + operSql.sqlCodes(right.getUserPortCodes("tatradesettleview")) + ")";
        	strSql+="order by a.FCheckState, a.FCreateTime desc" +
        	//---end---
            " ) y "; //2008-4-23 单亮 未审核的交易数据不能进行结算
			return strSql;
		} catch (Exception e) {
			throw new YssException("获取交易数据信息出错：" + e.getMessage(), e);
		}
	}

	/**
     * getListViewData3
     *
     * @return String
     */
//-------------------------------------20071021   chenyibo  计算ta费用的方法---------------
    public String getListViewData3() throws YssException {
        ArrayList alFeeBeans = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        FeeBean fee = null;
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sHeader = "";
        double dFeeMoney;
        YssFeeType feeType = null;
        DecimalFormat format = new DecimalFormat("#,##0.##");
        sHeader = this.getListView3Headers();
        TAFeeLink link = null;
        try {
            BaseOperDeal baseOper = this.getSettingOper();
            BaseLinkInfoDeal feeOper = (BaseLinkInfoDeal) pub.getOperDealCtx().
                getBean(
                    "TaFeeLinkDeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);
            link = new TAFeeLink();
            link.setSellNetCode(this.strSellNetCode);
            link.setSellTypeCode(this.strSellTypeCode);
            link.setCuryCode(this.strCuryCode);
            feeOper.setLinkAttr(link);
            alFeeBeans = feeOper.getLinkInfoBeans();
            if (alFeeBeans != null) {
                feeType = new YssFeeType();
                feeType.setMoney(this.dSellMoney);
                feeType.setAmount(this.dSellAmount);
                for (int i = 0; i < alFeeBeans.size(); i++) {
                    fee = (FeeBean) alFeeBeans.get(i);
                    dFeeMoney = baseOper.calFeeMoney(feeType, fee);
                    bufShow.append(fee.getFeeName()).append("\t");
                    bufShow.append(format.format(dFeeMoney)).append(YssCons.
                        YSS_LINESPLITMARK);
                    bufAll.append(fee.getFeeCode()).append(YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append(fee.getFeeName()).append(YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append(YssFun.formatNumber(dFeeMoney, "###0.##")).append(
                        YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append(fee.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() - 2);
                }

                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0,
                        bufAll.toString().length() - 2);
                }
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取费用列表出错");
        }
    }

    /**
     * getListViewData4
     *
     * @return String
     */

    //---------------------产生资金调拨  chenyibo 20071025
    public String getListViewData4() throws YssException {
        String strSql = "";
        try {
            return "";
        } catch (Exception e) {
            throw new YssException("资金调拨出错!");
        }
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        String strNumDate = "";
        boolean bTrans = false; //代表是否开始了事务
        java.sql.Connection conn = dbl.loadConnection();
        try {	
            if (this.strNum.length() == 0) {
                strNumDate = YssFun.formatDatetime(this.dSettleDate).
                    substring(0, 8);
                this.strNum = strNumDate + dbFun.getNextDataInnerCode();
//                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_TA_Trade"), //
//                                           dbl.sqlRight("FNUM", 6), "000001",
//                                           " where FNum like 'T"
//                                           + strNumDate + "%'", 1);
                this.strNum = "T" + this.strNum;
            }
            conn.setAutoCommit(false);
            bTrans = true;
            /**add---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
            strSql=this.getSqlValue();
            /**end---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增TA交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * huhuichao 2013-5-17 STORY  3759 获取往数据库插入数据的SQL语句
     * @return String
     */
	private String getSqlValue() throws YssException {
		try {
			String strSql = "";
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_TA_Trade")
					+ "(FConvertType,FSplitNetValue,FNUM,FMarkDate,FTradeDate,FPORTCODE,FSellNetCode,FSellType,FCuryCode,"
					+ " FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FSellMoney,"
					+ " FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FConfimDate,FSettleDate,FBASECURYRATE,FPortCuryRate,"
					+ " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4,"
					+ " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8"
					+ ", FDesc, FCheckState, FCreator, FCreateTime,FCheckUser,FSettleState,FPortClsCode,FSettleMoney"
					+ ",FBeMarkMoney"
					+ (this.bETFData.equals("true") ? ",FConvertNum,FCashBal,FCashRepAmount,FCASHBALFEE,"
				    +"FCASHBALANCEDATE,FCASHREPLACEDATE": "")
					+ // xuqiji 20091020 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
						// //modify by huangqirong 2012-07-10 STORY #1434
					// ==add by xuxuming,20091026.MS00760.需要增加基金拆分的功能
					// QDV4交银施罗德2009年10月22日01_A
					",FSplitRatio"
					+
					// ==end====================
					/** shashijie 2011-10-18 STORY 1589 */
					",FPortdegree"
					+
					/** end */
					", FPaidInMoney"
					+ // 20120611 added by liubo.Story #2683.实收基金金额
					")"
					+ // edit by zhouwei 20120626 bug 4870 QDV4建行2012年06月25日02_B
					" values("
					+ dbl.sqlString(this.strConvertType)
					+ ","
					+ this.dSplitNetValue
					+ ","
					+ dbl.sqlString(this.strNum)
					+ ","
					+ (dMarkDate.equals(dTradeDate) ? null : dbl
							.sqlDate(this.dMarkDate))
					+ ","
					+ dbl.sqlDate(this.dTradeDate)
					+ ","
					+ dbl.sqlString(this.strPortCode)
					+ ","
					+ dbl.sqlString(this.strSellNetCode)
					+ ","
					+ dbl.sqlString(this.strSellTypeCode)
					+ ","
					+ dbl.sqlString(this.strCuryCode)
					+ ","
					+ dbl
							.sqlString(this.strAnalysisCode1.length() != 0 ? this.strAnalysisCode1
									: " ")
					+ ","
					+ dbl
							.sqlString(this.strAnalysisCode2.length() != 0 ? this.strAnalysisCode2
									: " ")
					+ ","
					+ dbl
							.sqlString(this.strAnalysisCode3.length() != 0 ? this.strAnalysisCode3
									: " ")
					+ ","
					+ dbl.sqlString(this.strCashAcctCode.length() == 0 ? " "
							: this.strCashAcctCode)
					+ ","
					+ this.dSellMoney
					+ ","
					+ this.dSellAmount
					+ ","
					+ this.dSellPrice
					+ ","
					+ this.dIncomeNotBal
					+ ","
					+ this.dIncomeBal
					+ ","
					+ dbl.sqlDate(dConfimDate)
					+ ","
					+ dbl.sqlDate(dSettleDate)
					+ ","
					+ this.dBaseCuryRate
					+ ","
					+ this.dPortCuryRate
					+ ","
					+ this.operSql.buildSaveFeesSql(YssCons.OP_ADD, this.fees)
					+ dbl.sqlString(this.strDesc)
					+ ","
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ","
					+ dbl.sqlString(this.creatorCode)
					+ ","
					+ dbl.sqlString(this.creatorTime)
					+ ","
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode)) + ","
					+ this.settleState
					+ ","
					+ dbl.sqlString(this.strPortClsCode)
					+ ","
					+ this.dSettleMoney
					+
					// ------------------------
					","
					+ this.dBeMarkMoney
					+
					// ------------------------

					/** shashijie 2011-12-13 STORY 1434 修复:修改TA交易数据时,数值乱跳BUG */
					// ----------xuqiji 20091020-MS00002
					// QDV4.1赢时胜（上海）2009年9月28日01_A--------------------//

					/**
					 * MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A 修改人：操忠虎
					 * 修改时间：2009/11/23
					 */
					// 加了一个字段：dCashBalFee（现金差额使用费）
					(this.bETFData.equals("true") ? "," + this.dConvertNum
							+ "," + this.dCashBal + "," + this.dCashRepAmount
							+ "," + this.dCashBalFee + ","
							+ dbl.sqlDate(this.dCashBalanceDate) + ","
							+ dbl.sqlDate(this.dCashReplaceDate) : "") + // modify
																			// by
																			// huangqirong
																			// 2012-07-10
																			// STORY
																			// #1434
					// --------------------------------------end
					// 20091020------------------------------//
					// ---add by xuxuming,20091026.MS00760.需要增加基金拆分的功能
					// QDV4交银施罗德2009年10月22日01_A---
					"," + this.dSplitRatio +
					// ------------------------
					/** end */

					/** shashijie 2011-10-18 STORY 1589 */
					"," + this.FPortdegree +
					/** end */
					"," + this.dPaidInMoney + // 20120611 added by liubo.Story
												// #2683.实收基金金额
					")";// edit by zhouwei 20120626 bug 4870
						// QDV4建行2012年06月25日02_B
			return strSql;
		} catch (Exception e) {
			throw new YssException("新增TA交易数据信息出错", e);
		}
	}

	/**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    public void checkSetting() throws YssException {
        java.sql.Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = " select FSettleState from " +
                pub.yssGetTableName("Tb_TA_Trade") +
                " where FSettleState = 1 and FNum=" + dbl.sqlString(this.strNum);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                throw new YssException("本条交易编号为【" + this.strNum + "】的TA交易数据已经结算，不能进行反审核操作");  //补充提示信息 modify by wangzuochun 2009.08.31 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A
            }
            rs.close();

            String[] arrData = null;
            conn.setAutoCommit(false);
            bTrans = true;
			/**shashijie 2012-7-2 STORY 2475 */
            if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_TA_Trade") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum = " +
                        dbl.sqlString(this.strNum);
                    dbl.executeSql(strSql);
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            // modify by wangzuochun 2009.09.04 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A
            throw new YssException("审核TA交易数据信息出错",e); //这样修改避免控制台有异常抛出 liyu 1030
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        java.sql.Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_TA_Trade") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = " +
                dbl.sqlString(this.strNum);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除TA交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        String strNumDate = "";
        boolean bTrans = false; //代表是否开始了事务
        java.sql.Connection conn = dbl.loadConnection();
        try {
            strSql = "delete from " + pub.yssGetTableName("Tb_TA_Trade") +
                " where FNum = " + dbl.sqlString(this.strNum);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
          //modify by nimengjing bug #502 在修改TA结算日期时，编号不能随之改变
            if (this.strNum.length() != 0) {
                strNumDate = YssFun.formatDatetime(this.dSettleDate).
                    substring(0, 8);
                this.strNum = strNumDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_TA_Trade"), //
                                           dbl.sqlRight("FNUM", 6), "000001",
                                           " where FNum like 'T"
                                           + strNumDate + "%'", 1);
                this.strNum = "T" + this.strNum;
            }
            //-----------end bug#502---------------------------------------------------
            addSetting();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改TA交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strNum).append("\t");
        buf.append(this.strSellNetCode).append("\t");
        buf.append(this.strSellNetName).append("\t");
        buf.append(this.strSellTypeCode).append("\t");
        buf.append(this.strSellTypeName).append("\t");
        buf.append(this.strCuryCode).append("\t");
        buf.append(this.strCuryName).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.strCashAcctCode).append("\t");
        buf.append(this.strCashAcctName).append("\t");

        buf.append(this.strAnalysisCode1).append("\t");
        buf.append(this.strAnalysisName1).append("\t");
        buf.append(this.strAnalysisCode2).append("\t");
        buf.append(this.strAnalysisName2).append("\t");
        buf.append(this.strAnalysisCode3).append("\t");
        buf.append(this.strAnalysisName3).append("\t");
        if (this.dMarkDate != null) {
            buf.append(YssFun.formatDate(this.dMarkDate)).append("\t");
        } else {
            buf.append("").append("\t");
        }

        if (this.dTradeDate != null) {
            buf.append(YssFun.formatDate(this.dTradeDate)).append("\t");
        } else {
            buf.append("").append("\t");
        }

        if (this.dConfimDate != null) {
            buf.append(YssFun.formatDate(this.dConfimDate)).append("\t");
        } else {
            buf.append("").append("\t");
        }
        if (this.dSettleDate != null) {
            buf.append(YssFun.formatDate(this.dSettleDate)).append("\t");
        } else {
            buf.append("").append("\t");
        }
        buf.append(this.dPortCuryRate).append("\t");
        buf.append(this.dBaseCuryRate).append("\t");
        buf.append(this.dSellAmount).append("\t");
        buf.append(this.dSellPrice).append("\t");
        buf.append(this.dSellMoney).append("\t");

        buf.append(this.dIncomeNotBal).append("\t");
        buf.append(this.dIncomeBal).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(this.fees).append("\t");
        buf.append(this.dAmount).append("\t");
        buf.append(this.dCost).append("\t");
        buf.append(this.dScale).append("\t");
        buf.append(this.fee).append("\t");
        buf.append(this.settleState).append("\t"); //liyu 添加 结算方式 1028
        buf.append(this.strPortClsCode).append("\t");
        buf.append(this.strPortClsName).append("\t");
        buf.append(this.dSettleMoney).append("\t");
        //----------------------------------------
        buf.append(this.dBeMarkMoney).append("\t");
        //----------------------------------------
        //---add by xuxuming,20091026.MS00760.需要增加基金拆分的功能    QDV4交银施罗德2009年10月22日01_A----
       	buf.append(this.dSplitRatio).append("\t");//拆分比例
	  	//----------------------------------------
        //--------xuqiji 20091019 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A--------------//
        buf.append(this.bETFData).append("\t");
        if(this.bETFData.equals("true")){
	        buf.append(this.dConvertNum).append("\t");
	        buf.append(this.dCashBal).append("\t");
	        buf.append(this.dCashRepAmount).append("\t");
	        
	        /**
	         * MS00002
	         * QDV4.1赢时胜（上海）2009年9月28日01_A
	         * 修改人：操忠虎
	         * 修改时间：2009/11/23
	         */
	        buf.append(this.dCashBalFee).append("\t");
	        //add by huangqirong 2012-07-10 STORY #1434
        	buf.append(this.dCashBalanceDate).append("\t"); 
 	        buf.append(this.dCashReplaceDate).append("\t"); 
 	     //---end---
	        
        }        
		//-------------------------end 20091019------------------------------//
        /**shashijie 2011-10-18 STORY 1589 */
        buf.append(this.FPortdegree).append("\t");
        /**end*/
        buf.append(this.dPaidInMoney).append("\t");	//20120611 added by liubo.Story #2683.实收基金金额
        /**add---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
        buf.append(this.strConvertType).append("\t");
        buf.append(this.dSplitNetValue).append("\t");
        /**end---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    // wdy modify
    public String getOperValue(String sType) throws YssException {
        double sBaseRate = 0.0;
        double sPortRate = 0.0;
        double dlxMoney = 0.0;
        String strReturn = "";
        String SellNetCode = "";
        String PortClsCode = "";
        String PortCode = "";
        String SellTypeCode = "";
        String CuryCode = "";
        String TradeDate = "";
        ResultSet rs = null;
        TaCashAccLinkBean taCashAccLink = null;
        ArrayList reList = null;
        try {
            if (sType.equals("baserate")) {
                BaseOperDeal obj = new BaseOperDeal();
                obj.setYssPub(pub);
                sBaseRate = obj.getCuryRate(this.dConfimDate, this.strCuryCode,
                                            this.strPortCode,
                                            YssOperCons.YSS_RATE_BASE);
                strReturn = sBaseRate + "";
            } else if (sType.equals("portrate")) {
                BaseOperDeal obj = new BaseOperDeal();
                obj.setYssPub(pub);
                sPortRate = obj.getCuryRate(this.dConfimDate, this.strCuryCode,
                                            this.strPortCode,
                                            YssOperCons.YSS_RATE_PORT);
                strReturn = sPortRate + "";
            } else if (sType.equals("pl")) { //获得损益平准金
				//xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
            	if(this.isBETFData().equals("true")){
            		strReturn = getETFPL();
            	}else{
            		strReturn = getPL();
            	}                
            } else if (sType.equalsIgnoreCase("getCashAcc")) {
                BaseLinkInfoDeal taCashAccOper = (BaseLinkInfoDeal) pub.
                    getOperDealCtx().getBean(
                        "TaCashLinkDeal");
                taCashAccOper.setYssPub(pub);
                taCashAccLink = new TaCashAccLinkBean();
                taCashAccLink.setSellNetCode(this.strSellNetCode);
                taCashAccLink.setPortClsCode(this.strPortClsCode);
                taCashAccLink.setPortCode(this.strPortCode);
                taCashAccLink.setSellTypeCode(this.strSellTypeCode);
                taCashAccLink.setCuryCode(this.strCuryCode);
                taCashAccLink.setStartDate(YssFun.formatDate(this.dTradeDate));
                taCashAccOper.setLinkAttr(taCashAccLink);
                reList = taCashAccOper.getLinkInfoBeans();
                if (reList != null) {
                    this.strCashAcctCode = ( (CashAccountBean) reList.get(0)).
                        getStrCashAcctCode();
                    this.strCashAcctName = ( (CashAccountBean) reList.get(0)).
                        getStrCashAcctName();
                }
                return this.buildRowStr();
            } else if (sType.equalsIgnoreCase("getTwoDate")) { //修改了方法的编写方式，使之规范化 sj
                getSettleDay(this.strSellNetCode,
                             this.strPortClsCode,
                             this.strPortCode, this.strSellTypeCode,
                             this.strCuryCode,
                             this.dTradeDate);
                getConfirmDay(this.strSellNetCode,
                              this.strPortClsCode,
                              this.strPortCode,
                              this.strSellTypeCode,
                              this.strCuryCode,
                              this.dTradeDate);
                return this.buildRowStr();
            } else if (sType.equalsIgnoreCase("getSettleMoney")) { //获取实际结算的费用 liyu
                return this.calcFees() + "";
            }
            //------ add by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A ------//
            else if (sType.equalsIgnoreCase("getCashAccount")) {
            	return getCashAccount();
            }
            else if (sType.equalsIgnoreCase("getGroupCashAcc")) {
            	return getGroupCashAcc();
            }
            //add by fangjiang 2010.08.11 MS01568 QDV4上海2010年08月06日02_AB 
            else if (sType.equalsIgnoreCase("multauditTATradeSub")){
            	//批量审核/反审核/删除
                if (multAuditString.length() > 0) { //判断批量审核与反审核的内容是否为空
                    return this.auditMutli(this.multAuditString); //执行批量审核/反审核
                }
                return strReturn;
             //add by nimengjing 2011.1.26 BUG #995 批量结算后，反审核TA交易数据，不会提示已结算 
            }else if(sType.equalsIgnoreCase("beforeBatchUnAudit")){
            	return this.beforeBatchUnAudit();       	
            }else if(sType.equalsIgnoreCase("beforeBatchSettle")){
            	return this.beforeBatchUnSettle();       	
            }
            //------------------------end bug #995-----------------------------------------------
            //------------------
            //------ MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A ------//
            /**shashijie 2010-10-18 STORY 1589 本位币份额是否显示*/
            else if(sType.equalsIgnoreCase("doOperDegree")){
            	Boolean b =this.isMultiClass(this.strPortCode);
            	String isClass = "false";
            	//story 2253 add by zhouwei 20120222 对于本位币是否显示进一步来控制  start-----
            	if(b){//多class需要根据通参来确定核算类型
            		int state=this.getAccWayState(this.strPortCode);
            		if(state == 0){//按资产净值 （博时）显示
            			isClass = "true";
            		}else if(state==1){//按基准资产份额 不显示
            			isClass = "false";
            		}
            	}
            	//story 2253 add by zhouwei 20120222 对于本位币是否显示进一步来控制  end-----
            	return isClass;
            }
            /**end*/
        } catch (Exception ex) {
        	//edit by songjie 2011.03.19 BUG:1158 QDV4赢时胜(测试)2011年2月25日02_B
            throw new YssException("获取数据出错",ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return strReturn;
    }
    
    /**
     * 查询TA多CLASS基金份额成本设置,是否有符合条件的记录,有则是多币种份额计算的,前台显示"本位币份额"文本框
     * @return
     * @author shashijie ,2011-10-19 , STORY 1589
     * @modified
     */
    private String doOperPortCodeDegree() {
		String boole = "False";
		ResultSet rs = null;
    	try {
    		String strSql = " select a.* From "+pub.yssGetTableName("Tb_TA_ClassFundDegree")+
    			" a where a.FPortClsCode = "+dbl.sqlString(this.strPortClsCode)+" and a.FPortCode = "+
    			dbl.sqlString(this.strPortCode)+" and a.FCuryCode = "+dbl.sqlString(this.strCuryCode)+
    			" and a.FCheckState = 1 ";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
				boole = "true";
			}
		} catch (Exception e) {
			boole = "False";
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return boole;
	}
    
    //add by fangjiang 2011.11.08 story 1589
    public boolean isMultiClass(String portCode) throws YssException{
    	boolean boole = false;
		ResultSet rs = null;
		int i = 0;
    	try {
    		String strSql = " select a.* From " + pub.yssGetTableName("Tb_TA_ClassFundDegree") +
    			            " a where a.FPortCode = " + dbl.sqlString(portCode) + " and a.FCheckState = 1 "
    			            +" and (a.FPORTCLSCODE<>' ' or a.FCURYCODE<>' ')"; //story 2683 by zhouwei 20120611是否为分级判断条件的修改
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	i++;	
			}
            if(i>1){
            	boole = true;
            }
		} catch (Exception e) {
			throw new YssException("获取多Class数据出错", e);
		} finally {
            dbl.closeResultSetFinal(rs); //释放资源
        }

		return boole;
	}

	//add by nimengjing 2011.1.26 BUG #995 批量结算后，反审核TA交易数据，不会提示已结算 
    public String beforeBatchUnAudit()throws YssException{
        String strSql = "";
        ResultSet rs = null;
        StringBuffer sbReturn = new StringBuffer();
        try{
            String[] strTmp = this.strNum.split("~n~");
            for (int i = 0; i < strTmp.length; i++) {
                strSql = "select FNum,FSettleState from " +
                    pub.yssGetTableName("Tb_TA_Trade") + //查出TA数据表里的内容
                    " where FNum like '" + strTmp[i] +
                    "%' and FSettleState = 1 "; //查询条件为结算状态,因为审核状态也要数据完整性所以不加审核状态条件做为查询条件
                rs = dbl.openResultSet(strSql);
//             
                while(rs.next()){
                    sbReturn.append(rs.getString("FNum")).append(",");
                }
                dbl.closeResultSetFinal(rs);//BUG5560 add by yeshenghong 20120906
            }
            if(sbReturn.toString().length() > 1){
                return sbReturn.toString().substring(0,sbReturn.toString().length() - 1);//将已审核的交易编号返回前台
            }else{
                return "";
            }
        } catch (Exception e) {
            throw new YssException("获取交易拆分数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //释放资源
        }
    }
    //-------------------------------------end bug #995-----------------------------------------------------------------------------
    
    //add by yeshenghong BUG5075 TA交易结算需要先检查审核状态
    public String beforeBatchUnSettle()throws YssException{
        String strSql = "";
        ResultSet rs = null;
        StringBuffer sbReturn = new StringBuffer();
        try{
            String[] strTmp = this.strNum.split("~n~");
            for (int i = 0; i < strTmp.length; i++) {
                strSql = "select FNum,FSettleState from " +
                    pub.yssGetTableName("Tb_TA_Trade") + //查出TA数据表里的内容
                    " where FNum like '" + strTmp[i] +
                    "%' and FCheckstate = 0 "; //查询条件为未审核状态,
                rs = dbl.openResultSet(strSql);
//             
                while(rs.next()){
                    sbReturn.append(rs.getString("FNum")).append(",");
                }
                dbl.closeResultSetFinal(rs);//BUG5560 add by yeshenghong 20120906
            }
            if(sbReturn.toString().length() > 1){
                return sbReturn.toString().substring(0,sbReturn.toString().length() - 1);//将未审核的交易编号返回前台
            }else{
                return "";
            }
        } catch (Exception e) {
            throw new YssException("获取交易拆分数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //释放资源
        }
    }
    
    //add by fangjiang 2010.08.11 MS01568 QDV4上海2010年08月06日02_AB 
    public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null; //建立一个数据库连接
        String sqlStr = ""; //创建一个字符串
        PreparedStatement psmt1 = null;
        boolean bTrans = true; //建一个boolean变量，默认自动回滚
        TaTradeBean bean = null; //创建一个pojo类
        String[] multAudit = null; //建一个字符串数组

        try {
            conn = dbl.loadConnection(); //和数据库进行连接
            //审核、反审核、删除交易数据
            sqlStr = "update " + pub.yssGetTableName("Tb_TA_Trade") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = ? "  ; //更新数据库审核与未审核的SQL语句
            
            psmt1 = conn.prepareStatement(sqlStr); //执行SQL语句


            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                    	bean = new TaTradeBean(); //new 一个pojo类
                    	bean.setYssPub(pub); //设置一些基础信息
                    	bean.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                    	psmt1.setString(1,bean.strNum);
                        psmt1.addBatch(); 
                    }
                }
                conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
                psmt1.executeBatch();
                conn.commit(); //提交事物
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("批量审核交易数据出错!");
        } finally
        {
        	dbl.closeStatementFinal(psmt1);
        }
        return "";
    }
    //-----------------------

    /**
     * 获取延迟结算的日期 sj 因之前写的方法在其他地方
     * 有引用，所以没有把两个日期的获取方法合并。
     * @param SellNetCode String
     * @param PortClsCode String
     * @param PortCode String
     * @param SellTypeCode String
     * @param CuryCode String
     * @param TradeDate Date
     * @throws YssException
     * @return Date
     */
    public java.util.Date getSettleDay(String SellNetCode, String PortClsCode,
                                       String PortCode, String SellTypeCode,
                                       String CuryCode, java.util.Date TradeDate) throws
        YssException {
        ResultSet rs = null;
        TACashSettleBean taCashSettle = null;
        TACashSettleBean reTaCashSettle = null;
        ArrayList reList = null;
        try {
            //-----------------------------------------------------------------------------
            BaseLinkInfoDeal taCashSettleLink = (BaseLinkInfoDeal) pub.
                getOperDealCtx().getBean(
                    "TaCashSettleLinkDeal");
            taCashSettleLink.setYssPub(pub);
            taCashSettle = new TACashSettleBean();
            taCashSettle.setSellNetCode(SellNetCode);
            taCashSettle.setPortClsCode(PortClsCode);
            taCashSettle.setPortCode(PortCode);
            taCashSettle.setSellTypeCode(SellTypeCode);
            taCashSettle.setCuryCode(CuryCode);
            taCashSettle.setStartDate(TradeDate);
            taCashSettleLink.setLinkAttr(taCashSettle);
            reList = taCashSettleLink.getLinkInfoBeans();
            if (reList != null) {
                reTaCashSettle = (TACashSettleBean) reList.get(0);
                if (reTaCashSettle.getSettleDayType() == 0) { //工作日,通过getWorkDay获取天数，需要传入节假日群等信息
                    dSettleDate = this.getSettingOper().getWorkDay(reTaCashSettle.
                        getStrHolidaysCode(), TradeDate
                        , reTaCashSettle.getSettleDays());
                } else if (reTaCashSettle.getSettleDayType() == 1) { //自然日
                    dSettleDate = YssFun.addDay(TradeDate,
                                                reTaCashSettle.getSettleDays());
                }
            }
            
            return dSettleDate;
        }
        //------ modify by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
        catch (YssException ye){
        	throw new YssException(ye.getMessage()); 
        }
        //--------------- BUG #723 -------------------//
        catch (Exception e) {
            throw new YssException("访问节假日表出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
            //return dSettleDate; //------ modify by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
        }

    }

    /**
     * 获取延迟确认的日期 sj
     * @param SellNetCode String
     * @param PortClsCode String
     * @param PortCode String
     * @param SellTypeCode String
     * @param CuryCode String
     * @param TradeDate Date
     * @throws YssException
     * @return Date
     */
    public java.util.Date getConfirmDay(String SellNetCode, String PortClsCode,
                                        String PortCode, String SellTypeCode,
                                        String CuryCode,
                                        java.util.Date TradeDate) throws
        YssException {
        ResultSet rs = null;
        TACashSettleBean taCashSettle = null;
        TACashSettleBean reTaCashSettle = null;
        ArrayList reList = null;
        try {
            BaseLinkInfoDeal taCashSettleLink = (BaseLinkInfoDeal) pub.
                getOperDealCtx().getBean(
                    "TaCashSettleLinkDeal");
            taCashSettleLink.setYssPub(pub);
            taCashSettle = new TACashSettleBean();
            taCashSettle.setSellNetCode(SellNetCode);
            taCashSettle.setPortClsCode(PortClsCode);
            taCashSettle.setPortCode(PortCode);
            taCashSettle.setSellTypeCode(SellTypeCode);
            taCashSettle.setCuryCode(CuryCode);
            taCashSettle.setStartDate(TradeDate);
            taCashSettleLink.setLinkAttr(taCashSettle);
            reList = taCashSettleLink.getLinkInfoBeans();
            if (reList != null) {
                reTaCashSettle = (TACashSettleBean) reList.get(0);
                if (reTaCashSettle.getSettleDayType() == 0) {
                    dConfimDate = this.getSettingOper().getWorkDay(reTaCashSettle.
                        getStrHolidaysCode(), TradeDate
                        , reTaCashSettle.getConfirmDays());
                } else if (reTaCashSettle.getSettleDayType() == 1) {
                    dConfimDate = YssFun.addDay(TradeDate,
                                                reTaCashSettle.getSettleDays());
                }
            }
            return dConfimDate;
        //add by zhaoxianlin 20130313 STORY #3445--start    
        } catch(YssException ye){
        	throw new YssException(ye.getMessage());
        }
        //add by zhaoxianlin 20130313 STORY #3445--end
        catch (Exception e) {
            throw new YssException("访问节假日表出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
            
        }

    }

    /**
     * 获取确认日对应的申请日期 zhouss
     * @param SellNetCode String
     * @param PortClsCode String
     * @param PortCode String
     * @param SellTypeCode String
     * @param CuryCode String
     * @param TradeDate Date
     * @throws YssException
     * @return Date
     */
    public java.util.Date gettaTradeDateByconfirmDate(String SellNetCode,
        String PortClsCode,
        String PortCode, String SellTypeCode,
        String CuryCode,
        java.util.Date confirmDate) throws
        YssException {
        ResultSet rs = null;
        TACashSettleBean taCashSettle = null;
        TACashSettleBean reTaCashSettle = null;
        ArrayList reList = null;
        try {
            BaseLinkInfoDeal taCashSettleLink = (BaseLinkInfoDeal) pub.
                getOperDealCtx().getBean(
                    "TaCashSettleLinkDeal");
            taCashSettleLink.setYssPub(pub);
            taCashSettle = new TACashSettleBean();
            taCashSettle.setSellNetCode(SellNetCode);
            taCashSettle.setPortClsCode(PortClsCode);
            taCashSettle.setPortCode(PortCode);
            taCashSettle.setSellTypeCode(SellTypeCode);
            taCashSettle.setCuryCode(CuryCode);
            taCashSettle.setStartDate(confirmDate);
            taCashSettleLink.setLinkAttr(taCashSettle);
            reList = taCashSettleLink.getLinkInfoBeans();
            if (reList != null) {
                reTaCashSettle = (TACashSettleBean) reList.get(0);
                if (reTaCashSettle.getSettleDayType() == 0) {
                    dTradeDate = this.getSettingOper().getWorkDay(reTaCashSettle.
                        getStrHolidaysCode(), confirmDate
                        , -reTaCashSettle.getConfirmDays());
                } else if (reTaCashSettle.getSettleDayType() == 1) {
                    dTradeDate = YssFun.addDay(confirmDate,
                                               -reTaCashSettle.getSettleDays());
                }
            }
        } catch (Exception e) {
            throw new YssException("访问节假日表出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
            return dTradeDate;
        }
    }

    public String getPL() throws YssException {
    	/**shashijie 2011-10-26 STORY 1589 */
    	Boolean bool = this.isMultiClass(this.strPortCode);
    	//如果是多class基金份额计算损益平准金则走新流程
    	this.accWayState =getAccWayState(this.strPortCode);//story 2253 add by zhouwei 20120222 根据参数设置来确定采用哪种分类核算方式
    	if (bool) { //modify huangqirong 2012-05-10 story #2565 
    		if(0 == this.accWayState || 1 == this.accWayState){
    			this.doOperClassIncome();
        		return this.buildRowStr();
    		}else if(3 == this.accWayState || 4 == this.accWayState){ //modify by fangjiang story #2782 2012.09.12 story 3264 2012.11.19 fangjiang
    			this.doMClass();
        		return this.buildRowStr();
    		}    		
		}
    	/**end*/
        //获得未实现损益平准金
    	//String isCridet = null;//获取股指期货估值增值是否记入未实现损益平准金   add by yanghaiming 20091117 MS00773 QDV4华夏2009年10月29日01_A //modify by fangjiang 2011.07.13 STORY #1291
        String strSql = "";
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rsStorage = null; //获得TA库存的库存成本和数量
        double dTemp = 0;
        PortfolioBean port = null;
        CtlPubPara pubpara = null;
        double ctlMoney = 0; //2008-6-3 单亮 计算损益平准金时需要用到的金额
        String sTotalFee = null; // 2008-5-17 单亮
        //#1040 目前KB QFII组合存在频繁的缴款入账，无标准接口数据，需开同相应功能。 add by jiangshichao 2011.05.16  ----------------//
        String ErrorMsg = "";
        CtlPubPara pubPara = new CtlPubPara();
        pubPara.setYssPub(pub);
        //#1040 目前KB QFII组合存在频繁的缴款入账，无标准接口数据，需开同相应功能。 add by jiangshichao 2011.05.16 end -------------// 
        double ctlMoney1 = 0; //add by fangjiang 2012.05.27 story 2565
        try {
            port = new PortfolioBean();
            port.setYssPub(pub);
            port.setPortCode(strPortCode);
            port.getSetting();
            //----测试获取通用参数的值得代码 sj add 20080321 --------------//
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            //String testPubPara = pubpara.getTaIncome(this.strPortCode);
            //----------------------------------------------------------
            //计算所以费用的总额 2008-5-17 单亮
            sTotalFee = getTotalFee();
            strSql = "select a.*,b.*,c.*,d.* from (";
            //2008.07.16 蒋锦 修改 在查询净值表的 WHERE 条件中添加了投资经理
            strSql = strSql + "select sum(FBaseNetValue) as FTotalBaseNetValue,sum(FPortNetValue) as FTotalPortNetValue,FNAVDate,FPortCode from " +
                pub.yssGetTableName("Tb_Data_NetValue") +
                " where FType= '01' AND FInvMgrCode = ' ' group by FNAVDate,FPortCode) a left join (select " +
                " sum(FBaseNetValue) as FGZBaseNetValue,sum(FPortNetValue) as FGZPortNetValue,FNAVDate,FPortCode from " +
                pub.yssGetTableName("Tb_Data_NetValue") +
                " where FType = '03' AND FInvMgrCode = ' ' group by FNAVDate,FPortCode) b on a.FNAVDate = b.FNAVDate and a.FPortCode = b.FPortCode left join (select ";
            if (pubpara.getInCome(strPortCode).equalsIgnoreCase("Yes")) { //计入已实现收益的情况
                //华夏的汇兑损益是计入已实现收益,这边需考虑汇兑损益是否计入已实现收益fazmm20071120
                strSql = strSql +
                    " 0 as FHSBaseNetValue,0 as FHSPortNetValue,FNAVDate,FPortCode from ";
            } else if (pubpara.getInCome(strPortCode).equalsIgnoreCase("No")) { //计入未实现收益的情况
                strSql = strSql +
                    " sum(FBaseNetValue) as FHSBaseNetValue,sum(FPortNetValue) as FHSPortNetValue,FNAVDate,FPortCode from ";
            }
            //---------------------------------end
            else {
                strSql = strSql +
                    " 0 as FHSBaseNetValue,0 as FHSPortNetValue,FNAVDate,FPortCode from "; //默认以实现 sj edit 20080121
            }
            strSql = strSql + pub.yssGetTableName("Tb_Data_NetValue") +
                " where FType = '04' AND FInvMgrCode = ' ' group by FNAVDate,FPortCode) c  on a.FNAVDate = c.FNAVDate and a.FPortCode = c.FPortCode left join (select " +
                " sum(FBaseNetValue) as FUnPLBaseNetValue,sum(FPortNetValue) as FUnPLPortNetValue,FNAVDate,FPortCode from " +
                pub.yssGetTableName("Tb_Data_NetValue") +
                " where FType = '06' AND FInvMgrCode = ' ' group by FNAVDate,FPortCode) d on a.FNAVDate = d.FNAVDate and a.FPortCode = d.FPortCode";
            strSql = strSql + " where a.FNAVDate=" + //dbl.sqlDate(this.dTradeDate) +
                //----------------如果设置了基准日期，则用基准日期的值，如若不然则用交易日期的值 sj edit 20080312 -------------//
                //2008.05.15 蒋锦 添加 判断 dMarkDate 的值是否为空
                (this.dMarkDate == null || this.dMarkDate.equals(this.dTradeDate) ?
                 dbl.sqlDate(this.dTradeDate) : dbl.sqlDate(this.dMarkDate)) +
                //------------------------------------------------------------------------------------------------------
                " and a.FPortCode=" + dbl.sqlString(this.strPortCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dTemp = YssD.add(rs.getDouble("FGZPortNetValue"),
                                 rs.getDouble("FHSPortNetValue"));
                //---add by songjie 2011.07.14 BUG 2269 QDV4嘉实2011年07月13日01_B 合并陈杰明代码 start ---//
				//#1040 目前KB QFII组合存在频繁的缴款入账，无标准接口数据，需开同相应功能。测试后，有问题，在获取损益平准金时获取的是净值表的本位币金额，改为从库存里获取
                //直接获取原币即可，已经过测试 2011-07-13 
                if(pubpara.getIncomeBalMode(this.strPortCode)){
                	/**add---huhuichao 2013-11-6 BUG  82724 多币分级组合导入TA数据时未实现损益平准金是用TA库存中原币未实现损益平准金的合计余额来计算的*/
                	//add huhuichao 2013-11-6 原币未实现损益平准金的合计余额fcuryunpl 改为取 本币未实现损益平准金的合计余额
                	String strSql3 = "select sum(FPortCuryUnpl) as fcuryunpl from " + pub.yssGetTableName("Tb_Stock_TA") +
                	/**end---huhuichao 2013-11-6 BUG  82724*/
                " where FStorageDate = " + 
                //----------------如果设置了基准日期，则用基准日期的值，如若不然则用交易日期的值 sj edit 20080312 -------------//
                //2008.05.15 蒋锦 添加 判断 dMarkDate 的值是否为空
                (this.dMarkDate == null || this.dMarkDate.equals(this.dTradeDate) ?
                 dbl.sqlDate(this.dTradeDate) : dbl.sqlDate(this.dMarkDate)) +
                //------------------------------------------------------------------------------------------------------
                " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                " and FYearMonth <> " +
                //dbl.sqlString(YssFun.formatDate(this.dTradeDate, "yyyy") + "00"); //sj add 不获取起初数
                //----------------如果设置了基准日期，则用基准日期的值，如若不然则用交易日期的值 sj edit 20080312 -------------//
                //2008.05.15 蒋锦 添加 判断 dMarkDate 的值是否为空
                dbl.sqlString(YssFun.formatDate( (this.dMarkDate == null ||
                                                  this.dMarkDate.equals(this.
                dTradeDate) ? this.dTradeDate : this.dMarkDate), "yyyy") + "00");
                	rs3 = dbl.openResultSet(strSql3);
                	while (rs3.next()) {
                	dTemp = YssD.add(dTemp, rs3.getDouble("fcuryunpl"));
                	}
                	dbl.closeResultSetFinal(rs3);
            	}else{
				//---add by songjie 2011.07.14 BUG 2269 QDV4嘉实2011年07月13日01_B 合并陈杰明代码 end---//
            		dTemp = YssD.add(dTemp, rs.getDouble("FUnPLPortNetValue"));
				}//add by songjie 2011.07.14 BUG 2269 QDV4嘉实2011年07月13日01_B 合并陈杰明代码
				
                //modify by fangjiang 2011.07.13 STORY #1291
                //----------判断系统是否设置了股指期货估值增值记入未实现损益平准金----------------------------------------
                //add by yanghaiming 20091117 MS00773 QDV4华夏2009年10月29日01_A
                // 期货估增余额改成从财务估值表取数
                /*if(pubpara.getParaValue("ISCredit", "selPort", "cboISCredit", this.strPortCode)){
            		dTemp = YssD.add(dTemp, this.getFportcuryMoney(this.dTradeDate, YssOperCons.YSS_ZJDBZLX_FU01_MV));              	
                }
                if(pubpara.getParaValue("ISCredit_ZQ", "selPort", "cboISCredit", this.strPortCode)){
            		dTemp = YssD.add(dTemp, this.getFportcuryMoney(this.dTradeDate, YssOperCons.YSS_ZJDBZLX_FU02_MV));              	
                }
                if(pubpara.getParaValue("ISCredit_WH", "selPort", "cboISCredit", this.strPortCode)){
            		dTemp = YssD.add(dTemp, this.getFportcuryMoney(this.dTradeDate, YssOperCons.YSS_ZJDBZLX_FU03_MV));              	
                } 
				//add by huangqirong 2012-08-22 商品期货 估值增值加入损益平准金
                if(pubpara.getParaValue("ISCredit_SP", "selPort", "cboISCredit", this.strPortCode)){
            		dTemp = YssD.add(dTemp, this.getFportcuryMoney(this.dTradeDate, YssOperCons.YSS_ZJDBZLX_FU04_MV));              	
                }
				*/              
                //--------------------
				if(pubpara.getParaValue("ISCredit", "selPort", "cboISCredit", this.strPortCode)){
            		dTemp = YssD.add(dTemp, this.getQHGZ(this.strPortCode,this.dTradeDate,"证券清算款_股指期货"));              	
                }
                if(pubpara.getParaValue("ISCredit_ZQ", "selPort", "cboISCredit", this.strPortCode)){
            		dTemp = YssD.add(dTemp, this.getQHGZ(this.strPortCode,this.dTradeDate,"证券清算款_债券期货"));              	
                }
                if(pubpara.getParaValue("ISCredit_WH", "selPort", "cboISCredit", this.strPortCode)){
            		dTemp = YssD.add(dTemp, this.getQHGZ(this.strPortCode,this.dTradeDate,"证券清算款_外汇期货"));              	
                }
                if(pubpara.getParaValue("ISCredit_SP", "selPort", "cboISCredit", this.strPortCode)){
            		dTemp = YssD.add(dTemp, this.getQHGZ(this.strPortCode,this.dTradeDate,"证券清算款_商品期货"));              	
                }
                if (rs.getDouble("FTotalPortNetValue") != 0) {
                    this.dScale = YssD.div(dTemp, rs.getDouble("FTotalPortNetValue"));
                }
            }
            //获得TA库存的原币成本 和 数量 获取交易日期的库存,以便用来计算损益平准金(未实现)fazmm20071116
            strSql =
                "select sum(fstorageamount) as FStorageAmount,sum(fportcurycost) as FCost from " +
                pub.yssGetTableName("Tb_Stock_TA") +
                " where FStorageDate = " + 
                //----------------如果设置了基准日期，则用基准日期的值，如若不然则用交易日期的值 sj edit 20080312 -------------//
                //2008.05.15 蒋锦 添加 判断 dMarkDate 的值是否为空
                (this.dMarkDate == null || this.dMarkDate.equals(this.dTradeDate) ?
                 dbl.sqlDate(this.dTradeDate) : dbl.sqlDate(this.dMarkDate)) +
                //------------------------------------------------------------------------------------------------------
                " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                " and FYearMonth <> " +
                //dbl.sqlString(YssFun.formatDate(this.dTradeDate, "yyyy") + "00"); //sj add 不获取起初数
                //----------------如果设置了基准日期，则用基准日期的值，如若不然则用交易日期的值 sj edit 20080312 -------------//
                //2008.05.15 蒋锦 添加 判断 dMarkDate 的值是否为空
                dbl.sqlString(YssFun.formatDate( (this.dMarkDate == null ||
                                                  this.dMarkDate.equals(this.
                dTradeDate) ? this.dTradeDate : this.dMarkDate), "yyyy") + "00");
            rs2 = dbl.openResultSet(strSql);
            while (rs2.next()) {
                dCost = rs2.getDouble("FCost");
                dAmount = rs2.getDouble("FStorageAmount");
            }
            if (dAmount == 0.0) { //sj add 计算损益平准金
                this.dIncomeNotBal = 0.0;
            } else {

                //--------------所有的计算都直接用基准金额 sj modify MS00019  ------------------//
            	
            	//#1040 目前KB QFII组合存在频繁的缴款入账，无标准接口数据，需开同相应功能。------------//
            	if(pubpara.getIncomeBalMode(this.strPortCode)){
            		if(this.dBaseCuryRate==0 && this.dPortCuryRate ==0 ){
            			ErrorMsg = "请先设置汇率，再进行计算!";
            			throw new YssException(ErrorMsg);
            		}
            		String result = pubpara.getDigitsPortMethod("CtlIncomeBal","TA_TAInCome","CtlIncomeBal","selPort","cboIncomeBalMode1",strPortCode,"0,0");
            		if("0,0".equals(result)){
            			ctlMoney = YssD.div(YssD.mul(this.dBeMarkMoney, this.dBaseCuryRate), this.dPortCuryRate, 2); 
            		}else{
            			//add by fangjiang 2012.05.27 story 2565
            			ctlMoney = YssD.add
            			           (
        			        		   YssD.div
        			        		   (
    			        				   YssD.mul
    			        				   (
			        						   YssD.sub(this.dBeMarkMoney, this.yfshf, this.xsfsr), 
			        						   this.dBaseCuryRate
		        						   ), 
		        						   this.dPortCuryRate,
		        						   2
	        						   ),
	        						   YssD.div
        			        		   (
    			        				   YssD.mul
    			        				   (
			        						   this.yfshf,
			        						   this.dBaseCuryRate
		        						   ), 
		        						   this.dPortCuryRate,
		        						   2
	        						   ),
	        						   YssD.div
        			        		   (
    			        				   YssD.mul
    			        				   (
			        						   this.xsfsr, 
			        						   this.dBaseCuryRate
		        						   ), 
		        						   this.dPortCuryRate,
		        						   2
	        						   )
        						   );
            		}
            		ctlMoney1 = YssD.div(YssD.mul(this.dBeMarkMoney, this.dBaseCuryRate), this.dPortCuryRate, 2); 
            	}else{
            		ctlMoney = this.dBeMarkMoney;
            		ctlMoney1 = this.dBeMarkMoney;
            	}
                //#1040 目前KB QFII组合存在频繁的缴款入账，无标准接口数据，需开同相应功能。 end -------//
                //--------------------------------------------------------------------------//
                //计算未实现损益平准金
                this.dIncomeNotBal = YssD.round(YssD.mul(ctlMoney1,
                    this.dScale),
                                                2);

                //---直接用金额-数量-未实现损益平准金= 已实现损益平准金 sj edit 20080121 --//
                //story #1040 目前KB QFII组合存在频繁的缴款入账，无标准接口数据，需开同相应功能。------------//
                
                /**Start 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001
                 * 根据通参“分级本币实收基金”设置的值进行计算*/
                //按“申购时原币折算，赎回时移动加权”进行计算。在计算之前先以交易类型区分申购和赎回
                if (getPaidInCalcType())
                {
                	if (this.strSellTypeCode.trim().equals("01"))
                	{
                		this.getPaidInMoney_HFTAllot();
                	}
                	else if (this.strSellTypeCode.trim().equals("02"))
                	{
                		this.getPaidInMoney_HFTRedeem();
                	}
                }
                //按默认算法进行计算
                else
                {
                	this.getPaidUpFundsMoney();
                }
                /**End 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001*/
                
                if(pubpara.getIncomeBalMode(this.strPortCode)){
                	//story 2683 modify by zhouwei 20120611 已实现=金额-实收基金金额-未实现损益平准金
                	this.dIncomeBal = YssD.round(YssD.sub(ctlMoney, this.dPaidInMoney, this.dIncomeNotBal), 2); //this.dSellAmount
            	}else{
            		this.dIncomeBal = YssD.sub(ctlMoney,this.dPaidInMoney,this.dIncomeNotBal); //fj 2012.02.25 考虑初始份额成本 YssD.mul(this.dSellAmount, getUnitCostByCury())
            	}
                //story #1040 目前KB QFII组合存在频繁的缴款入账，无标准接口数据，需开同相应功能。------------//
                //--------------------------------------------------------------------
            }
            return this.buildRowStr();
        } catch (Exception e) {
            throw new YssException(ErrorMsg.length()!=0?ErrorMsg:"获取未实现损益平准金报错");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs2);
            dbl.closeResultSetFinal(rs3);//add by songjie 2011.07.14 BUG 2269 QDV4嘉实2011年07月13日01_B 合并陈杰明代码
        }
    }
    
    /**story 2253 add by zhouwei 20120222
     * 根据通参得到当前组合的分类核算方式
     * @param pCode
     * @return
     * @throws YssException 
     */
    public int getAccWayState(String pCode) throws YssException{
    	CtlPubPara pubPara=new CtlPubPara();
    	pubPara.setYssPub(pub);
    	Map portClsMap=pubPara.getClassAccMethod();
    	if(portClsMap.get(pCode)==null){
    		return 0;
    	}
        String accWays=(String) portClsMap.get(pCode);
        String accMethod="";
        String clsPort="";
        if(accWays!=null && accWays.length()>0){
        	//inBasicNetValue 基准资产份额（如嘉实），inNetValue 资产净值（如博时），默认
        	accMethod=accWays.split("\f\t")[0];
        	if(accWays.split("\f\t").length > 1){
        		clsPort=accWays.split("\f\t")[1];
        	}
        	if("inBasicNetValue".equalsIgnoreCase(accMethod) && clsPort.equals("")){//基准资产份额没有设置分级组合
        		throw new YssException("请在通用参数分类核算方式中设置组合"+this.strPortCode+"的分级组合信息！");
        	}
        }
        if(accWays==null || accWays.equals("") || accMethod.equalsIgnoreCase("inNetValue")){//资产净值
    		return 0;
    	}else if(accMethod.equalsIgnoreCase("inBasicNetValue")){//基准资产份额（如嘉实）
    		return 1;
    	}else if(accMethod.equalsIgnoreCase("inExRate")){//按汇率折算（如易方达联接基金）
    		return 2;
    	}
        //add by huangqirong 2012-09-12 story #2822
    	else if(accWays==null || accWays.equals("") || accMethod.equalsIgnoreCase("inNetValue_china")){//资产净值   国内
    		return 3;
    	}
        //add by fangjiang story 3264 2012.11.19
    	else if(accWays==null || accWays.equals("") || accMethod.equalsIgnoreCase("inNetValue_chinaM")){//资产净值   国内
    		return 4;
    	}
        //---end---
        //add by yeshenghong story 3759 2013.05.17
    	else if(accWays==null || accWays.equals("") || accMethod.equalsIgnoreCase("inNetValue_chinaL")){//资产净值   国内
    		return 5;
    	}
        //---end--- add by yeshenghong story 3759 2013.05.17
        return 0;
    }
    /**多class基金份额计算损益平准金
     * @author shashijie ,2011-10-26 , STORY 1589
     * @modified 
     */
    private void doOperClassIncome() throws YssException {
    	//设置需要计算损益平准金的值
		doOperSetDScale();
		//TA库存,获取基准日期或是交易日期的成本和数量,计算损益平准金
		doTbStockTA();
	}
    
	/**
	 * //获得TA库存的组合货币核算成本 和 数量 获取交易日期的库存,以便用来计算损益平准金(未实现)
	 * @author shashijie ,2011-10-26 , STORY 1589
	 * @modified 
	 */
	private void doTbStockTA() throws YssException {
		ResultSet rs2 = null;
		try {
			String strSql =
	            "select sum(FStorageAmount) as FStorageAmount,sum(FPortCuryCost) as FCost from " +
	            pub.yssGetTableName("Tb_Stock_TA") +
	            " where FStorageDate = " + 
	            //如果设置了基准日期，则用基准日期的值，如若不然则用交易日期的值
	            (this.dMarkDate == null || this.dMarkDate.equals(this.dTradeDate) ?
	            dbl.sqlDate(this.dTradeDate) : dbl.sqlDate(this.dMarkDate)) +
	            
	            " and FPortCode=" + dbl.sqlString(this.strPortCode) +
	            " and FYearMonth <> " +
	            //如果设置了基准日期，则用基准日期的值，如若不然则用交易日期的值
	            dbl.sqlString(YssFun.formatDate( (this.dMarkDate == null ||
	                                              this.dMarkDate.equals(this.
	            dTradeDate) ? this.dTradeDate : this.dMarkDate), "yyyy") + "00");
			//add by fangjiang 2012.02.21
			if(this.accWayState == 0){
				strSql += " and FCuryCode = " + dbl.sqlString(this.strCuryCode); 
			}else if(this.accWayState == 1){
				strSql += " and FPortClsCode = " + dbl.sqlString(this.strPortClsCode); 
			}			
	        rs2 = dbl.openResultSet(strSql);
	        while (rs2.next()) {
	            dCost = rs2.getDouble("FCost");//组合货币核算成本
	            dAmount = rs2.getDouble("FStorageAmount");//库存数量  
	        }
	        if (dAmount == 0.0) {//如没有数量则损益平准金为0
	            this.dIncomeNotBal = 0.0;
	        } else {
	            //所有的计算都直接用基准金额
	        	double ctlMoney = this.dBeMarkMoney;
	            //计算未实现损益平准金  * 过度
	            this.dIncomeNotBal = YssD.round(YssD.mul(ctlMoney,this.dScale),2);
	            //基准金额  - 数量  - 未实现损益平准金  = 已实现损益平准金
        		this.dIncomeBal = getIncomeBal(ctlMoney,this.dSellAmount,this.dIncomeNotBal);
	        }
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs2);
			throw new YssException("获得TA库存的原币成本 和 数量出错!");
		} finally{
			dbl.closeResultSetFinal(rs2);
		}
	}
	

	/**已实现损益平准金  = 基准金额  - 数量  - 未实现损益平准金 
	 * @param ctlMoney 基准金额
	 * @param dSellAmount2  数量
	 * @param dIncomeNotBal2 未实现损益平准金 
	 * @return 已实现损益平准金
	 * @author shashijie ,2011-11-2 , STORY 1589
	 * @modified 
	 */
	private double getIncomeBal(double ctlMoney, double dSellAmount2,
			double dIncomeNotBal2) {
		double value = 0;
		try {
			dSellAmount2 = YssD.mul(dSellAmount2, getUnitCostByCury());
			value = YssD.sub(ctlMoney, dSellAmount2,dIncomeNotBal2);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}

	/**获取需要计算损益平准金的值
	 * @author shashijie ,2011-10-26 , STORY 1589
	 * @modified 
	 */
	private void doOperSetDScale() throws YssException {
		ResultSet rs = null;
		try {
			String Sql = getSql();
			rs = dbl.openResultSet(Sql);
			processNetValue(rs);
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("设置多class基金份额计算损益平准金时出错!");
		} finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**处理数据
	 * @param rs
	 * @author shashijie ,2011-10-26 , STORY 1589
	 * @modified 
	 */
	private void processNetValue(ResultSet rs) throws SQLException {
		double dTemp = 0.0;//临时汇总金额
		while (rs.next()) {
			//估值增值3 + 汇兑损益4
            dTemp = YssD.add(rs.getDouble("FGZPortNetValue"),
                             rs.getDouble("FHSPortNetValue"));
            // +为实现损益平准金6(总额)
        	dTemp = YssD.add(dTemp, rs.getDouble("FUnPLPortNetValue"));
        	//除以资产净值1
            if (rs.getDouble("FTotalPortNetValue") != 0) {
            	//过度  = 临时汇总金额  / 资产净值1
                this.dScale = YssD.div(dTemp, rs.getDouble("FTotalPortNetValue"));
            }
        }
	}

	/**查询多class单位净值表
	 * @return
	 * @author shashijie ,2011-10-26 , STORY 1589
	 * @modified 
	 */
	private String getSql() throws YssException {
		String strSql = "select a.*,b.*,c.*,d.* from ( select " +
			" sum(FClassNetValue) as FTotalPortNetValue,FNAVDate,FPortCode from " +
            pub.yssGetTableName("TB_DATA_MultiClassNet")+" where FType= '01' and FCuryCode = "+
            dbl.sqlString(this.accWayState==1?this.strPortClsCode:this.strCuryCode/*story 2253 edit by zhouwei 根据类型选择参数值*/)+" group by FNAVDate,FPortCode) a left join (select " +
            " sum(FClassNetValue) as FGZPortNetValue,FNAVDate,FPortCode from " +
            pub.yssGetTableName("TB_DATA_MultiClassNet") +
            " where FType = '03' and FCuryCode = "+
            dbl.sqlString(this.accWayState==1?this.strPortClsCode:this.strCuryCode/*story 2253 edit by zhouwei 根据类型选择参数值*/)+" group by FNAVDate,FPortCode) b on a.FNAVDate = b.FNAVDate and " +
            " a.FPortCode = b.FPortCode left join (select ";
		//通用业务参数
		CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        if (pubpara.getInCome(strPortCode).equalsIgnoreCase("Yes")) { //计入已实现收益的情况
            //华夏的汇兑损益是计入已实现收益,这边需考虑汇兑损益是否计入已实现收益
            strSql += " 0 as FHSPortNetValue,FNAVDate,FPortCode From ";
        } else if (pubpara.getInCome(strPortCode).equalsIgnoreCase("No")) { //计入未实现收益的情况
            strSql += " sum(FClassNetValue) as FHSPortNetValue,FNAVDate,FPortCode From ";
        } else {
            strSql += " 0 as FHSPortNetValue,FNAVDate,FPortCode From "; //默认以实现 
        }
        
        strSql += pub.yssGetTableName("TB_DATA_MultiClassNet") +
            " where FType = '04' and FCuryCode = "+dbl.sqlString(this.accWayState==1?this.strPortClsCode:this.strCuryCode/*story 2253 edit by zhouwei 根据类型选择参数值*/)+
            " group by FNAVDate,FPortCode) c on a.FNAVDate = c.FNAVDate and a.FPortCode = c.FPortCode " +
            " left join (select sum(FClassNetValue) as FUnPLPortNetValue," +
            " FNAVDate,FPortCode from "+pub.yssGetTableName("TB_DATA_MultiClassNet") +
            " where FType = '06' and FCuryCode = "+dbl.sqlString(this.accWayState==1?this.strPortClsCode:this.strCuryCode/*story 2253 edit by zhouwei 根据类型选择参数值*/)+
            " group by FNAVDate,FPortCode) d on a.FNAVDate = d.FNAVDate and a.FPortCode = d.FPortCode" +
            " where a.FNAVDate = " + 
            //如果设置了基准日期，则用基准日期的值，如若不然则用交易日期的值 //
            (this.dMarkDate == null || this.dMarkDate.equals(this.dTradeDate) ?
             dbl.sqlDate(this.dTradeDate) : dbl.sqlDate(this.dMarkDate)) +
            " and a.FPortCode=" + dbl.sqlString(this.strPortCode);
		return strSql;
	}

	/**
     * 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @return
     * @throws YssException
     */
    public String getETFPL() throws YssException {
    	String strSql = "";
    	ResultSet rs = null;
    	CtlPubPara pubpara = null;
    	Date date = null;
    	double dTmp = 0;
    	try{
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            date =  (this.dMarkDate == null || this.dMarkDate.equals(this.dTradeDate)) ? this.dTradeDate : this.dMarkDate;//如果设置了基准日期，则用基准日期的值，如若不然则用交易日期的值
            
			strSql = "select c.FMVPortMarketValue,d.FFXPortMarketValue,e.FUnPLPortMarketValue,f.FTotalValue,crb.FCR from " + 
					" (select FPortCode,FPortMarketValue as FMVPortMarketValue from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'MV' and " + 
					" FNAVDate = " + dbl.sqlDate(date) + 
					" and FPortCode = " + dbl.sqlString(strPortCode) + 
					" ) c left join (select " ;
		    if (pubpara.getInCome(strPortCode).equalsIgnoreCase("Yes")) { //计入已实现收益的情况
		        strSql = strSql +
		            " 0 as FFXPortMarketValue,FPortCode from ";
		    } else if (pubpara.getInCome(strPortCode).equalsIgnoreCase("No")) { //计入未实现收益的情况
		        strSql = strSql +
		            " FPortMarketValue as FFXPortMarketValue,FPortCode from ";
		    }
		    //---------------------------------end
		    else {
		        strSql = strSql +
		            " 0 as FFXPortMarketValue,FPortCode from "; //默认已实现
		    }
			strSql = strSql + pub.yssGetTableName("Tb_ETF_NavData") +  
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'FX' and " + 
					" FNAVDate = " + dbl.sqlDate(date) + 
					" and FPortCode = " + dbl.sqlString(strPortCode) + 
					" ) d on d.FPortCode = c.FPortCode" + 
					" left join (" + 
					" select FPortCode,FPortMarketValue as FUnPLPortMarketValue from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'UnPL' and " + 
					" FNAVDate = " + dbl.sqlDate(date) + 
					" and FPortCode = " + dbl.sqlString(strPortCode) + 
					" ) e on e.FPortCode = c.FPortCode" + 
					" left join (" + 
					" select FPortCode,FPortMarketValue as FTotalValue from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'TotalValue' and " + 
					" FNAVDate = " + dbl.sqlDate(date) + 
					" and FPortCode = " + dbl.sqlString(strPortCode) + 
					" ) f on f.FPortCode = c.FPortCode" +
					//可退替代款估值增值
					" left join(select sum(FPortMarketValue) as FCR,fportcode from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FNAVDate = " + dbl.sqlDate(date) + 
					" and FInvMgrCode = 'total' and FReTypeCode = 'Cash' " +
					" and fkeycode like '%-09CR%' group by fportcode) crb on crb.fportcode = c.fportcode";
			rs = dbl.openResultSet(strSql);
    		if(rs.next()){
    			//T日ETF净值表的（未实现损益平准金 + 估值增值合计值 + 汇兑损益合计值） / 资产净值 *  TA交易数据的基准金额
				dTmp = YssD.add(rs
						.getDouble("FMVPortMarketValue"), rs
						//.getDouble("FFXPortMarketValue"), rs
						.getDouble("FUnPLPortMarketValue"),rs
						.getDouble("FCR"));	
				if(pubpara.getParaValue("ISCredit", "selPort", "cboISCredit", this.strPortCode)){
					dTmp = YssD.add(dTmp, this.getQHGZ(this.strPortCode,this.dTradeDate,"证券清算款_股指期货"));              	
                }
                if(pubpara.getParaValue("ISCredit_ZQ", "selPort", "cboISCredit", this.strPortCode)){
                	dTmp = YssD.add(dTmp, this.getQHGZ(this.strPortCode,this.dTradeDate,"证券清算款_债券期货"));              	
                }
                if(pubpara.getParaValue("ISCredit_WH", "selPort", "cboISCredit", this.strPortCode)){
                	dTmp = YssD.add(dTmp, this.getQHGZ(this.strPortCode,this.dTradeDate,"证券清算款_外汇期货"));              	
                }
                if(pubpara.getParaValue("ISCredit_SP", "selPort", "cboISCredit", this.strPortCode)){
                	dTmp = YssD.add(dTmp, this.getQHGZ(this.strPortCode,this.dTradeDate,"证券清算款_商品期货"));              	
                }
                dTmp = YssD.div(dTmp, rs.getDouble("FTotalValue"));
    		}
            //计算未实现损益平准金
			this.dIncomeNotBal = YssD.round(YssD.mul(this.dBeMarkMoney,
					dTmp), 2);

            //销售金额 - 销售数量  + 份额折算金额 – TA未实现损益平准金
			if(this.strSellTypeCode.equals("02")){
	            this.dIncomeBal = YssD.sub(this.dBeMarkMoney , this.dSellAmount , this.dConvertNum , this.dIncomeNotBal);
			}else{
	            this.dIncomeBal = YssD.sub(this.dBeMarkMoney , this.dSellAmount , -this.dConvertNum , this.dIncomeNotBal);
			}

    		return this.buildRowStr();
    	}catch(Exception e){
    		throw new YssException("获取未实现损益平准金出错", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}    	
    }
    /**
     * parseRowStr
     * modify by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        String sMutiAudit = ""; //批量处理的数据 add by fangjiang 2010.08.11 MS01568 QDV4上海2010年08月06日02_AB 
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            //add by fangjiang 2010.08.11 MS01568 QDV4上海2010年08月06日02_AB 
            if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];  //得到的是从前台传来需要审核与反审核的批量数据
                multAuditString = sMutiAudit;                   //保存在全局变量中
                sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];     //前台传来的要更新的一些数据
            }
            //------------------------------------------------
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.strNum = reqAry[0];
            this.strSellTypeCode = reqAry[1];
            if (reqAry[2].equals("")) { //如果销售网点没有值的时候是需要插空 alter by sunny 2007-10-27
                this.strSellNetCode = " ";
            } else {
                this.strSellNetCode = reqAry[2];
            }
            this.strAnalysisCode1 = reqAry[3];
            this.strAnalysisCode2 = reqAry[4];
            this.strAnalysisCode3 = reqAry[5];
            this.strPortCode = reqAry[6];

            this.strCashAcctCode = reqAry[7];
            if (YssFun.isDate(reqAry[8])) {
                this.dMarkDate = YssFun.toDate(reqAry[8]);
            }
            if (YssFun.isDate(reqAry[9])) {
                this.dTradeDate = YssFun.toDate(reqAry[9]);
            }
            if (YssFun.isDate(reqAry[10])) {
                this.dConfimDate = YssFun.toDate(reqAry[10]);
            }
            if (YssFun.isDate(reqAry[11])) {
                this.dSettleDate = YssFun.toDate(reqAry[11]);
            }
            if (reqAry[12].length() != 0) {
                this.dPortCuryRate = Double.parseDouble(
                    reqAry[12]);
            }
            if (reqAry[13].length() != 0) {
                this.dBaseCuryRate = Double.parseDouble(
                    reqAry[13]);
            }
            if (reqAry[14].length() != 0) {
                this.dSellAmount = Double.parseDouble(
                    reqAry[14]);
            }
            if (reqAry[15].length() != 0) {
                this.dSellPrice = Double.parseDouble(
                    reqAry[15]);
            }
            if (reqAry[16].length() != 0) {
                this.dSellMoney = Double.parseDouble(
                    reqAry[16]);
            }
            if (reqAry[17].length() != 0) {
                this.dIncomeNotBal = Double.parseDouble(
                    reqAry[17]);
            }
            if (reqAry[18].length() != 0) {
                this.dIncomeBal = Double.parseDouble(
                    reqAry[18]);
            }

            this.strCuryCode = reqAry[19];

            this.strDesc = reqAry[20];
            this.fees = reqAry[21].replaceAll("~", "\n"); //alter   by  sunny "\n"  replace "\t"
            this.checkStateId = Integer.parseInt(reqAry[22]);
            this.strOldNum = reqAry[23];
            this.isOnlyColumns = reqAry[24];
            if (YssFun.isDate(reqAry[25])) {
                this.beginDate = YssFun.parseDate(reqAry[25]);
            }
            if (YssFun.isDate(reqAry[26])) {
                this.endDate = YssFun.parseDate(reqAry[26]);
            }
            if (reqAry[27].length() != 0) {
                this.fee = Double.parseDouble(
                    reqAry[27]);
            }
            if (YssFun.isNumeric(reqAry[28])) {
                this.settleState = YssFun.toInt(reqAry[28]); //liyu 添加 结算方式  1028
            }
            this.strPortClsCode = (reqAry[29].length() == 0 ? "" : reqAry[29]);
            //this.insCashAccCode = reqAry[29];
            if (YssFun.isNumeric(reqAry[30])) {
                this.dSettleMoney = YssFun.toDouble(reqAry[30]);
            }
            //--------------------------------------------------------------------//
            if (YssFun.isDate(reqAry[31])) { //确认开始日期
                this.confimBeginDate = YssFun.parseDate(reqAry[31]);
            }
            if (YssFun.isDate(reqAry[32])) { //确认结束日期
                this.confimEndDate = YssFun.parseDate(reqAry[32]);
            }
            //------------------------------------------------------------------------------
            if (YssFun.isNumeric(reqAry[33])) { //基准金额 sj modify 20081125 MS00019
                this.dBeMarkMoney = YssFun.toNumber(reqAry[33]);
            } else if (reqAry[33].trim().length() == 0) { //若没有基准金额的值，则取销售金额为默认值。
                this.dBeMarkMoney = this.dSellMoney;
            }
            //--------------------------------------------------------------------//
            //=======add by xuxuming,20091026.MS00760.需要增加基金拆分的功能    QDV4交银施罗德2009年10月22日01_A====
            if (reqAry[34].length() != 0) {
                this.dSplitRatio = Double.parseDouble(
                    reqAry[34]);//拆分比例
            }
            //===end ==============================
            
            //------ add by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A ------//
            this.assetGroupCode = reqAry[35];
            //------ MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A ------//
            
			//-----------xuqiji 20091020-MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A--------------------//
            this.bETFData=reqAry[36];
            if(this.bETFData.equals("true")){
            	this.dConvertNum=YssFun.toNumber(reqAry[37]);
            	this.dCashBal=YssFun.toNumber(reqAry[38]);
            	this.dCashRepAmount=YssFun.toNumber(reqAry[39]);
            	
            	 /**
                 * MS00002
                 * QDV4.1赢时胜（上海）2009年9月28日01_A
                 * 修改人：操忠虎
                 * 修改时间：2009/11/23
                 */
            	this.dCashBalFee=YssFun.toNumber(reqAry[40]);
            }
            //-----------------------------end 20091020----------------------------//
            /**shashijie 2011-10-18 STORY 1589 */
            if(this.bETFData.equals("true")) {
            	//modify by zhangjun 2012.06.21 BUG4856调度方案执行到“业务处理类”--“自动结算业务资料和TA交易数据”时报错 
            	if(reqAry[41] !=null && reqAry[41].trim().length() > 0 && !reqAry[41].trim().equals("null") ){
            		this.dPaidInMoney = YssFun.toNumber(reqAry[41]);
            	}            	
                //this.dPaidInMoney = YssFun.toNumber(reqAry[41]);//20120611 added by liubo.Story #2683.实收基金金额
               //modify by zhangjun 2012.06.21 BUG4856调度方案执行到“业务处理类”--“自动结算业务资料和TA交易数据”时报错 
            	
            	if (reqAry[42]!=null && reqAry[42].trim().length() > 0 && !reqAry[42].trim().equals("null")) {
                	this.FPortdegree = Double.valueOf(reqAry[42]).doubleValue();	
    			}

			} else {
				//modify by zhangjun 2012.06.21 BUG4856调度方案执行到“业务处理类”--“自动结算业务资料和TA交易数据”时报错 
            	if(reqAry[37] !=null && reqAry[37].trim().length() > 0 && !reqAry[37].trim().equals("null") ){
            		this.dPaidInMoney = YssFun.toNumber(reqAry[37]);
            	}            	
                //this.dPaidInMoney = YssFun.toNumber(reqAry[37]);//20120611 added by liubo.Story #2683.实收基金金额
                //modify by zhangjun 2012.06.21 BUG4856调度方案执行到“业务处理类”--“自动结算业务资料和TA交易数据”时报错 
	            
				if (reqAry[38]!=null && reqAry[38].trim().length() > 0 && !reqAry[38].trim().equals("null")) {
                	this.FPortdegree = Double.valueOf(reqAry[38]).doubleValue();	
    			}
            /**end*/
			}
            //add by huangqirong 2012-07-10 STORY #1434
            if(this.bETFData.equals("true")) {
            	this.dCashBalanceDate = reqAry[43];
            	this.dCashReplaceDate = reqAry[44];
            }
            //---end---
            /**add---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
            if(this.bETFData.equals("true")) {
            	if(reqAry[45] !=null && reqAry[45].trim().length() > 0 && !reqAry[45].trim().equals("null") ){
            		this.strConvertType=reqAry[45];//modified by  yeshenghong  DB2报错  20130628
            	}            	
                if (reqAry[46]!=null && reqAry[46].trim().length() > 0 && !reqAry[46].trim().equals("null")) {
                	this.dSplitNetValue = Double.valueOf(reqAry[46]).doubleValue();	//modified by  yeshenghong  DB2报错  20130628
    			}
            }else {
			    if(reqAry[39] !=null && reqAry[39].trim().length() > 0 && !reqAry[39].trim().equals("null") ){
			    	this.strConvertType=reqAry[39];
            	}            	
                if (reqAry[40]!=null && reqAry[40].trim().length() > 0 && !reqAry[40].trim().equals("null")) {
                	this.dSplitNetValue = Double.parseDouble(reqAry[40]);
    			}
            }
            /**end---huhuichao 2013-5-17 STORY  3759 新增[拆分前单位净值]输入框和[折算类型]下拉框 */
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TaTradeBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
            
        } catch (Exception e) {
            throw new YssException("解析交易数据设置请求出错", e);
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    //获得我资金的流向 通过销售类型去得到
    private String getInOut() throws YssException { // wdy add
        String strSql = "";
        String strResult = "";
        ResultSet rs = null;
        try {
            strSql = "select FCashInd from " +
                pub.yssGetTableName("Tb_TA_SellType") +
                " where FSellTypeCode = " + dbl.sqlString(this.strSellTypeCode) +
                " and FCheckState =1 ";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                strResult = rs.getString("FCashInd");
            }
            return strResult;
        } catch (Exception ex) {
            throw new YssException("获取数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //产生资金调拨 wdy add
    private void createSavCashTrans(String sOldNum,CashTransAdmin tranAdmin) throws
        YssException {
        ResultSet rs = null;
        TransferBean tran = new TransferBean();
        TransferSetBean transferset = new TransferSetBean();
        ArrayList tranSetList = new ArrayList();

        //------ add by wangzuochun 2009.08.10 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A ------//
        //------ 在TA销售类型为基金拆分时，资金方向为无，则资金方向字段FCashInd为0，
        //------ 在这里要判断为0的情况，当资金方向为无时，不产生资金调拨，跳出此方法------//
        if (getInOut().equals("0")) {
            return ;
        }
        //---------------------------------------------------END MS00023---------------------------------------------------------------//

        //增加资金调拨记录
        tran.setYssPub(pub);
        //应收应付日期应该是确认日期fazmm20071021
        tran.setDtTransDate(this.dConfimDate); //存入时间:使用的是"交易日期"
        tran.setDtTransferDate(this.dSettleDate); //结算日期
        tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Capital);
        tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Capital +
                                  this.strSellTypeCode); //这里还要待定
        tran.setStrTransferTime("00:00:00");
//      tran.setDataSource(0);
        tran.setDataSource(1); //这里应为自动标记为1 by leeyu BUG:MS00020 2008-11-24
        tran.setFRelaNum(this.strNum);
        tran.setFNumType("3");
        tran.checkStateId = 1;
        tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                             "yyyyMMdd HH:mm:ss");
//      tran.setDataSource(0); //自动计算标志
        tran.setDataSource(1); //这里应为自动标记为1 by leeyu BUG:MS00020 2008-11-24
        //资金流入流出帐户
        //---------------------------------------------------------------------------------------------------------
        //alter by sunny  2007-10-27 原因是当销售类型为赎回 销售金额是包括费用的 而费用又包括给基金资产的费用 和 销售机构的费用 而归基金资产的是不需要划出去
        //所以销售金额要减掉费用由销售机构承担的
        String arr[] = null;
        double dFeesTotal = 0; //2008-5-19  单亮 累加费用
        try {
            arr = this.fees.split("\f\n"); //得到费用的个数
            for (int i = 0; i < arr.length; i++) { //循环其费用代码  根据费用代码去找其费用承担者
                String strSql = "select * from " +
                    pub.yssGetTableName("Tb_Para_Fee") +
                    " where fissettle = 1 and FFeeCode=" +
                    dbl.sqlString(arr[i].split("\n")[0]); //每个费用的信息 包含代码 名称 值
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    //2008-5-19  单亮 修改后 begin 累加费用
                    dFeesTotal += YssFun.toDouble(arr[i].split("\n")[2]);
                    //chenyibo  20071030     要改成用 YssD.sub()方法来减,不然会产生尾差
                }
                dbl.closeResultSetFinal(rs);
            }
            //2008-5-19 单亮 获取资金的流动方向进行进行费用加减
            //---------------begin
            String strSql = " select FCashInd from " +
                pub.yssGetTableName("Tb_TA_SellType") +
                " where FSellTypeCode = " + dbl.sqlString(this.strSellTypeCode);//edited by zhouxiang 201009MS01697    调度方案中的结算步骤增加一参数控制结算按成交日或结算日 
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FCashInd").equals("1")) {
                    this.dSellMoney = dSellMoney - dFeesTotal;
                } else if (rs.getString("FCashInd").equals("-1")) {
                    this.dSellMoney = dSellMoney + dFeesTotal;
                }
            }
            //---------------end
        } catch (Exception ex) {
            throw new YssException("获取数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

        //---------------------------------------------------------------
        
        //------ modify by wangzuochun 2011.01.30 BUG #999 分红再投资业务结算，产生资金调拨有问题  BUG #1002 分红转投计算出的未实现损益平准金不正确
        if ("08".equals(this.strSellTypeCode)){
        	transferset.setDMoney(0); 
        }
        else{
        	transferset.setDMoney(this.dSellMoney); // 销售金额
            if (dSettleMoney != 0) {
                transferset.setDMoney(dSettleMoney); //若实际结算金额不为 0 结算时用结算金额
            }
        }
        //----------------------------BUG #999 BUG #1002---------------------------//
        transferset.setSPortCode(this.strPortCode); // 组合代码
        transferset.setSAnalysisCode1(this.strAnalysisCode1); // 分析代码1
        transferset.setSAnalysisCode2(this.strAnalysisCode2); // 分析代码2
        transferset.setDBaseRate(YssFun.roundIt(this.dBaseCuryRate, 15)); // 基础汇率 hxqdii
        transferset.setDPortRate(YssFun.roundIt(this.dPortCuryRate, 15)); // 组合汇率 hxqdii
        transferset.setSCashAccCode(this.strCashAcctCode); // 现金帐户代码
        transferset.checkStateId = 1;

        if (getInOut().equals("-1")) {
            transferset.setIInOut( -1);
        }
        if (getInOut().equals("1")) {
            transferset.setIInOut(1);
        }

        tranSetList.add(transferset);
		//modify huangqirong 2012-09-07 bug #5490 
        tranAdmin = new CashTransAdmin();
        tranAdmin.setYssPub(pub);
        tranAdmin.addList(tran, tranSetList);
//        //获得资金调拨编号
        String sTranNum = "";
        if (!this.flag) {
            sTranNum = tranAdmin.getTransNums(sOldNum, "3"); //alter by sunny 这样的情况导致复制的时候会把资金调拨给删掉
        }
        if (sOldNum.equals("") || sTranNum.equals("")) { //20071016 chenyibo   现在由于ta数据是在审核的时候产生资金调拨的
            tranAdmin.insert();
        } else {
            //然后根据资金调拨编号 进行先删后增
            tranAdmin.insert(sTranNum, 0);
        }
		//---end---
    }

    /**
     * 此方法用于处理 TA结算数据
     * sType 为 do 结算 undo 为反结算
     *  add liyu 1028
     *  xuqiji 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     */
    public void doSettle(String sType,String sAllData) throws YssException {
        ResultSet rs=null;
        String sSubAssetType="";//资产子类型
        String[] sArr = sAllData.split("\r\n");
        Boolean tradeMode = getDealMode();
        String strSql = "";
        String sTranNum = "";
        try {
        	
        		CashTransAdmin tranAdmin = new CashTransAdmin();
                tranAdmin.setYssPub(pub);
                //modified by yeshenghong story2633 TA结算优化 20120725
		        for (int i = 0; i < sArr.length; i++) {
		            this.parseRowStr(sArr[i]);
	        
		            strSql = " select FportCode,FSubAssetType from " + pub.yssGetTableName("tb_para_portfolio")
		            		+ " where FPortCode =" + dbl.sqlString(this.strPortCode);
	
	            	rs = dbl.openResultSet(strSql);
		            if (rs.next()) {
		                sSubAssetType = rs.getString("FSubAssetType");
		            }
		            dbl.closeResultSetFinal(rs);//modified by ye shenghong BUG5793
	            	 //begin  zhouxiang MS01744   系统需支持基金分红时，现金分红在分红转投日之前的情况    
	            	//tradeMode||!this.getStrSellTypeCode().equals("10")||!sSubAssetType.equals("0106") || this.strSellTypeCode.equals("00")//资产子类型不是ETF基金，就产生资金调拨
		            if((this.getStrSellTypeCode().equals("10") && !tradeMode) || 
		            		/**add---huhuichao 2013-8-16 BUG  9060 跨境ETF测试对分红到账数据结算后未产生相对应的资金调拨*/
		            		(sSubAssetType.equals("0106") && this.strSellTypeCode.equals("01")) ||//ETF资产，申购不进行资金调拨
		            		(sSubAssetType.equals("0106") && this.strSellTypeCode.equals("02")) ||//ETF资产，赎回不进行资金调拨
		            		/**end---huhuichao 2013-8-16 BUG  9060*/
		            		//add by songjie 2013.03.01 STORY #3413 需求上海-[YSS_SH]QDIIV4.0[中]20121214002 如果为TA分红转投数据，则不生成资金调拨
		            		this.getStrSellTypeCode().equals("08")){//如果是TA红利发放就不进行资金调拨
		            	continue;//modified by yeshenghong  notice: be careful while modifing the old codes!!!
		            }
		            
	            	createSavCashTrans(this.strOldNum,tranAdmin);
			   //modify huangqirong 2012-09-07 bug #5490 
	            	//获得资金调拨编号
	            //    sTranNum += tranAdmin.getTransNums(this.strOldNum, "3"); //alter by sunny 这样的情况导致复制的时候会把资金调拨给删掉
		        }
		        //if (this.strOldNum.equals("") || sTranNum.equals("")) { //20071016 chenyibo   现在由于ta数据是在审核的时候产生资金调拨的
	            //    tranAdmin.insert();
	            //} else {
	                //然后根据资金调拨编号 进行先删后增
	            //    tranAdmin.insert(sTranNum, 0);
	            //}  
	            //---end---
//	        	for (int i = 0; i < sArr.length; i++) {
//		            this.parseRowStr(sArr[i]);
//		            strSql = " select FportCode,FSubAssetType from " + pub.yssGetTableName("tb_para_portfolio")
//            					+ " where FPortCode =" + dbl.sqlString(this.strPortCode);

//		            rs = dbl.openResultSet(strSql);
//		            if (rs.next()) {
//		                sSubAssetType = rs.getString("FSubAssetType");
//		            }
//	                if(!sSubAssetType.equals("0106") || this.strSellTypeCode.equals("00")){//资产子类型不是ETF基金，就不删除资金调拨
//	                    doDelete();
//	                }
//	        	}
//            }
        } catch (Exception e) {
            throw new YssException("结算数据出错", e);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    public void changeSettleState(String sType) throws YssException {
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sType.equalsIgnoreCase("do")) {
                strSql = " update " + pub.yssGetTableName("Tb_TA_Trade") +
                    " set FSettleState =1" +
                    " where FNum in(" + operSql.sqlCodes(this.sNums) + ")";
            } else if (sType.equalsIgnoreCase("undo")) {
                strSql = " update " + pub.yssGetTableName("Tb_TA_Trade") +
                    " set FSettleState =0" +
                    " where FNum in(" + operSql.sqlCodes(this.sNums) + ")";
            }
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更改TA交易数据的结算状态出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 删除资金调拔表与调拨子表中对应的数据 add liyu 1029
     * @throws YssException
     * xuqiji 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     */
    public void doDelete() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = true;
        String strSql = "", sNum = "";
        ResultSet rs = null;
        StringBuffer buff=null;
        String strNums="";
        try {
            buff=new StringBuffer(1000);
            buff.append(" select t.fnum, t.fportcode from (");
            buff.append(" select ta.fnum, ta.fportcode,po.fsubassettype,ta.fselltype from ");
            buff.append(pub.yssGetTableName("tb_ta_trade")).append(" ta ");
            buff.append(" left join (select FportCode, FSubAssetType from ");
            buff.append(pub.yssGetTableName("tb_para_portfolio"));
            buff.append(" ) po on po.FportCode = ta.FportCode");
            buff.append(" where fnum in(").append(operSql.sqlCodes(this.sNums)).append(")");
            buff.append(" ) t where t.fsubassettype <> '0106' or t.fselltype = '00'");

            rs=dbl.openResultSet(buff.toString());
            while(rs.next()){
//                if(!rs.getString("fsubassettype").equals("0106")|| "00".equals(strSellTypeCode)){//modified by yeshenghong 20120715 story2633 条件通过SQL实现
                    strNums += rs.getString("fnum") + ",";
//                }
            }
            dbl.closeResultSetFinal(rs);
            strSql = "select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FRelaNum in("
                + operSql.sqlCodes(strNums) +
                " ) and FNumType=" +
                dbl.sqlString("3");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sNum += rs.getString("FNum") + ",";
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            conn.setAutoCommit(false);
            strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FRelaNum in("
                + operSql.sqlCodes(strNums) +
                " ) and FNumType=" +
                dbl.sqlString("3");
            dbl.executeSql(strSql);
            strSql = "delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FNum in(" + operSql.sqlCodes(sNum) + " )";
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("删除资金调拔关键表出错", e);
        }finally{
        	//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
        	dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn,bTrans);
        }
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
			/**shashijie 2012-7-2 STORY 2475 */
            if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                conn.setAutoCommit(false);
                bTrans = true;
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_TA_Trade") +
                        " where FNum = " +
                        dbl.sqlString(this.strNum);
                    dbl.executeSql(strSql);

                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除TA交易数据信息出错" + "\r\n" + e.getMessage()); //这样修改避免控制台有异常抛出 liyu 1030
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public java.util.Date getDMarkDate() {
        return dMarkDate;
    }

    public double getDSettleMoney() {
        return dSettleMoney;
    }

    public Date getConfimBeginDate() {
        return confimBeginDate;
    }

    public Date getConfimEndDate() {
        return confimEndDate;
    }

    public double getBeMarkMoney() {
        return dBeMarkMoney;
    }

    public void setDMarkDate(java.util.Date dMarkDate) {
        this.dMarkDate = dMarkDate;
    }

    public void setDSettleMoney(double dSettleMoney) {
        this.dSettleMoney = dSettleMoney;
    }

    public void setConfimBeginDate(Date confimBeginDate) {
        this.confimBeginDate = confimBeginDate;
    }

    public void setConfimEndDate(Date confimEndDate) {
        this.confimEndDate = confimEndDate;
    }

    public void setBeMarkMoney(double BeMarkMoney) {
        this.dBeMarkMoney = BeMarkMoney;
    }

    /**
     * 获取费用
     * 从前台传过来fees进行解析然后把进入清算款的累加
     * @return double
     * @throws YssException
     */
    private double getSFees() throws YssException {
        FeeBean fee = null;
        TASellTypeBean selType = null;
        double dFees = 0; //默认是0开始
        String[] arrFee = null;
        String feeStr = "";
        try {

            selType = new TASellTypeBean();
            selType.setYssPub(pub);
            selType.setSellTypeCode(strSellTypeCode);
            selType.getSetting();

            arrFee = this.fees.split("\f\n");
            if (arrFee != null) {
                for (int i = 0; i < arrFee.length; i++) {
                    fee = new FeeBean();
                    feeStr = arrFee[i];
                    fee.setYssPub(pub);
                    fee.setFeeCode(feeStr.split("\n")[0]);
                    fee.getSetting();
                    if (fee.getIsSettle().equals("1")) {
                        if (YssFun.isNumeric(feeStr.split("\n")[2])) {
                            dFees += YssFun.toDouble(feeStr.split("\n")[2]);
                        }
                    }
                }
                dFees = dFees * selType.getCashInd() * -1;
            }

        } catch (Exception e) {
            throw new YssException("计算实际结算费用出错", e);
        }
        return dFees;
    }

    private double calcFees() throws YssException { //2008-6-1   单亮 calcSettleMoney
        TAFeeLink link = null;
        FeeBean fee = null;
        ArrayList alFeeBeans = null;
        TASellTypeBean selType = null;
        YssFeeType feeType = null;
        double dFees = 0; //默认是0开始
        String[] arrFee = null;
        try {
            BaseOperDeal baseOper = this.getSettingOper();
            BaseLinkInfoDeal feeOper = (BaseLinkInfoDeal) pub.getOperDealCtx().
                getBean(
                    "TaFeeLinkDeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);
            /**shashijie 2011-09-22 添加注释*/
            //费用连接类
            link = new TAFeeLink();
            link.setSellNetCode(this.strSellNetCode);//销售网点
            link.setSellTypeCode(this.strSellTypeCode);//销售类型
            link.setCuryCode(this.strCuryCode);//货币
            feeOper.setLinkAttr(link);
            alFeeBeans = feeOper.getLinkInfoBeans();

            selType = new TASellTypeBean();
            selType.setYssPub(pub);
            selType.setSellTypeCode(strSellTypeCode);
            selType.getSetting();
            /**shashijie 2012-7-2 STORY 2475 */
            if (this.fees != null) {
            	arrFee = this.fees.split("\n");
			} else {
				arrFee = new String[0];
			}
            /**end*/
            /**shashijie 2011-09-22 BUG 2691 */
            if (alFeeBeans != null && this.fees!=null && this.fees.trim().length()>0 && arrFee.length>0) {
                feeType = new YssFeeType();
                feeType.setMoney(this.dSellMoney);
                feeType.setAmount(this.dSellAmount);
                for (int i = 0; i < alFeeBeans.size(); i++) {
                    fee = (FeeBean) alFeeBeans.get(i);
                    if (fee.getIsSettle().equals("0")) { //在此处将获取类型改为归基金资产的费用 sj modify 20081125 MS00019
                        if (arrFee[i] != null && YssFun.isNumeric(arrFee[i])) {
                            dFees += YssFun.toDouble(arrFee[i]);
                        } else {
                            dFees += baseOper.calFeeMoney(feeType, fee);
                        }
                    }
                }
                dFees = dFees * selType.getCashInd() * -1;
            }
            /**-end-*/
        } catch (Exception e) {
            throw new YssException("计算实际结算费用出错", e);
        }
        return dFees;
    }

    /**
     * 2008-5-19
     * 单亮
     * 获取一个总的费用
     * @return String
     * @throws YssException
     */
    private String getTotalFee() throws YssException {
        double fee = 0;
        String feesAry[] = null;
        /**shashijie 2012-7-2 STORY 2475 */
		if (this.fees != null) {
			feesAry = fees.split("\f\n");
		} else {
			feesAry = new String[0];
		}
		/**end*/
        if (fees != null && !fees.equalsIgnoreCase("null") && fees.length() != 0) {
            for (int i = 0; i < feesAry.length; i++) {
                fee += Double.parseDouble(feesAry[i].split("\n")[2].toString());
            }
        }
        return fee + "";
    }
    
    /**
	 * xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * 获取交易费用的方法，在ETF估值中更新TA交易数据时调用
     * 工银ETF预提交易收入
     */
    public double getTradeFee(String strNum) throws YssException {
        ArrayList alFeeBeans = null;
        FeeBean fee = null;
        double dFeeMoney = 0;
        String strFeeCode = "";
        YssFeeType feeType = null;
        TAFeeLink link = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false;
        try {
            BaseOperDeal baseOper = this.getSettingOper();
            BaseLinkInfoDeal feeOper = (BaseLinkInfoDeal) pub.getOperDealCtx().
                getBean(
                    "TaFeeLinkDeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);
            link = new TAFeeLink();
            link.setSellNetCode(this.strSellNetCode);
            link.setSellTypeCode(this.strSellTypeCode);
            link.setCuryCode(this.strCuryCode);
            feeOper.setLinkAttr(link);
            alFeeBeans = feeOper.getLinkInfoBeans();
            if (alFeeBeans != null) {
                feeType = new YssFeeType();
                feeType.setMoney(this.dSellMoney);
                feeType.setAmount(this.dSellAmount);
                for (int i = 0; i < alFeeBeans.size(); i++) {
                    fee = (FeeBean) alFeeBeans.get(i);
                    dFeeMoney = baseOper.calFeeMoney(feeType, fee);
                    strFeeCode = fee.getFeeCode();
                }
            }

			conn.setAutoCommit(false);
			bTrans = true;

            strSql = " update " + pub.yssGetTableName("Tb_TA_Trade") +
                      " set FTradeFee1 = " + dFeeMoney + " , FFeeCode1 = " + dbl.sqlString(strFeeCode) + 
                      " where FNum = " + dbl.sqlString(strNum);

			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
            
        } catch (Exception e) {
            throw new YssException("计算交易费用出错");
        }finally{
        	dbl.endTransFinal(conn, bTrans);
        }
        return dFeeMoney;
    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }
    /**
     * 股指期货估值增值记入未实现损益平准金
     * @throws YssException
     * @return String
     * add by yanghaiming 20091117 MS00773 QDV4华夏2009年10月29日01_A
     * modify by fangjiang 2011.07.13 STORY #1291
     */
	private double getFportcuryMoney(Date time, String tsftypeCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
//		Format format = new SimpleDateFormat("yyyyMMdd");
//		String dt = format.format(time);
		double money = 0;
		double basecurymoney = 0;
		double portcurymoney = 0;
		try {
			strSql = "select sum(FMONEY) as money , sum(FBASECURYMONEY) as basecurymoney, sum(FPORTCURYMONEY) as portcurymoney from "+
				pub.yssGetTableName("tb_data_futtraderela") +
				" where FTSFTYPECODE = '" + tsftypeCode +
				//------ modify by wangzuochun 2011.05.09 添加上组合代码条件， BUG 1859 计算TA交易数据的未实现损益平准金包含期货估值增值
				"' and FPortCode = " + dbl.sqlString(this.strPortCode) + " and FTRANSDATE = " + dbl.sqlDate(time);
			rs = dbl.openResultSet(strSql);
	        while (rs.next()) {
	        	money = rs.getDouble("money");
	        	basecurymoney = rs.getDouble("basecurymoney");
	        	portcurymoney = rs.getDouble("portcurymoney");
	        }
		}catch (SQLException e) {
			throw new YssException("获取未实现损益平准金报错", e);
		}finally {
            dbl.closeResultSetFinal(rs);
        }
		return portcurymoney;
	}
//  ------xuqiji 20091019 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A------------------//
	public double getDCashBal() {
		return dCashBal;
	}

	public void setDCashBal(double cashBal) {
		dCashBal = cashBal;
	}

	public double getDCashRepAmount() {
		return dCashRepAmount;
	}

	public void setDCashRepAmount(double cashRepAmount) {
		dCashRepAmount = cashRepAmount;
	}

	public double getDConvertNum() {
		return dConvertNum;
	}

	public void setDConvertNum(double convertNum) {
		dConvertNum = convertNum;
	}
	public String isBETFData() {
		return bETFData;
	}

	public void setBETFData(String data) {
		bETFData = data;
	}
	//--------------------------------end 20091019------------------------------------//
	
	/**
	 * add by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A -----------//
	 */
	public String getCashAccount() throws YssException {
		ResultSet rs = null;
		String strCashAccCode = "";
		String strSql = "";
		String str = "";
		String reStr = "";
		String buildSql = "";
		
		try {
			strSql = " Select FCashAccCode from "
					+ pub.yssGetTableName("Tb_Ta_Trade")
					+ " Where FCheckState = 1 AND FSellType = '01' AND FPortCode IN ("
					+ operSql.sqlCodes(this.strPortCode) + " )"
//					+ " and (FSettleDate Between " + dbl.sqlDate(this.beginDate)
//					+ " and " + dbl.sqlDate(this.endDate)
//					+ " or FConfimDate Between " + dbl.sqlDate(this.beginDate)
//					+ " and " + dbl.sqlDate(this.endDate) + ")"
					
					+ " and FSettleDate >= " 
					+ dbl.sqlDate(this.beginDate)
					+ " and FConfimDate <= "
					+ dbl.sqlDate(this.endDate)
					+ " Group by FCashAccCode";
			
			rs = dbl.openResultSet(strSql);
			
			while (rs.next()) {
				
				if (rs.getString("FCashAccCode") != null && rs.getString("FCashAccCode").length() > 0) {
					strCashAccCode = strCashAccCode + dbl.sqlString(rs.getString("FCashAccCode"))  + ",";
				}
			}
			dbl.closeResultSetFinal(rs);
			
			if (strCashAccCode.indexOf(",") != -1) {
				strCashAccCode = strCashAccCode.substring(0, strCashAccCode.length() - 1 );
				buildSql = " and a.FCashAccCode in (" + strCashAccCode + ")";
			}
			else {
				buildSql = " and 1 = 2 ";
			}
				
			str = "select show.*,h.fassetgroupcode, h.fassetgroupname from (select y.* from " + 
			//----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
//	            "(select FCashAccCode,FCheckState,max(FStartDate) as FStartDate from " +
//	            pub.yssGetTableName("Tb_Para_CashAccount") + " " +
//	            " where FStartDate <= " +
//	            dbl.sqlDate(new java.util.Date()) +
//
//	            //将利息来源不等于不计息作为查询条件, 使现金计息中不显示已设置为不计息的账户
//	            "and FCheckState <> 2 and FInterestorigin <> 2 group by FCashAccCode,FCheckState) x join" +
			//----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
	            " (select a.*, l.FVocName as FStateValue, m.FVocName as FInterestCycleValue, n.FVocName as FInterestOriginValue," +
	            "b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FAccTypeName, e.FSubAccTypeName,rp.FRECEIVERNAME as frecpayname," +
	            "f.FBankName,g.FCurrencyName, h.FPortName,j.FPeriodName,k.FRoundName, i.FFormulaName ,xx.FDepDurName,nn.FVOCNAME as InterestWay " +
	            //----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
	            " from (select * from " + pub.yssGetTableName("Tb_Para_CashAccount") +
	            " where FCheckState <> 2 and FInterestorigin <> 2 ) a " +
	            //----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
	            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	            //story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
				" left join (select FRECEIVERCODE,FRECEIVERNAME from "+pub.yssGetTableName("TB_PARA_RECEIVER")+" where fcheckstate=1 ) rp on rp.FRECEIVERCODE=a.FRECPAYCODE"+
				//-------------end------------
	            " left join " +
	            pub.yssGetTableName("Tb_Para_DepositDuration") +
	            " xx on a.FDepDurCode =xx.FDepDurCode " +
	            " left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) d on a.FAccType = d.FAccTypeCode" +
	            " left join (select FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType where FCheckState = 1) e on a.FSubAccType = e.FSubAccTypeCode" +
	            " left join (select FBankCode,FBankName from " +
	            pub.yssGetTableName("Tb_Para_Bank") +
	            " where FCheckState = 1) f on a.FBankCode = f.FBankCode" +
	            " left join (select FCuryCode as FCurrencyCode,FCuryName as FCurrencyName from " +
	            pub.yssGetTableName("Tb_Para_Currency") +
	            " where FCheckState = 1) g on a.FCuryCode = g.FCurrencyCode" +
	            " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from " +
	            //edit by songjie 2011.03.16 不以最大的启用日期查询数据
	            pub.yssGetTableName("Tb_Para_Portfolio") + " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getPrefixTB()) +
	            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//	            "(select FPortCode,max(FStartDate) as FStartDate from " +
//	            pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//	            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//	            " and FCheckState = 1 and FASSETGROUPCODE = " +
//	            dbl.sqlString(pub.getPrefixTB()) +
//	            " group by FPortCode) p " +
	            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
	            " ) h on a.FPortCode = h.FPortCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
	            " left join (select FFormulaCode, FFormulaName from " +
	            pub.yssGetTableName("Tb_Para_Performula") +
	            " where FCheckState = 1) i on a.FFormulaCode = i.FFormulaCode" +
	            " left join (select FPeriodCode,FPeriodName from " +
	            pub.yssGetTableName("Tb_Para_Period") +
	            " where FCheckState = 1 ) j on a.FPeriodCode = j.FPeriodCode" +
	            " left join (select FRoundCode,FRoundName from " +
	            pub.yssGetTableName("Tb_Para_Rounding") +
	            "  where FCheckState = 1) k on a.FRoundCode = k.FRoundCode" +
	            " left join Tb_Fun_Vocabulary l on a.FState = l.FVocCode and l.FVocTypeCode = " +
	            dbl.sqlString(YssCons.YSS_CNT_STATE) +
	            " left join Tb_Fun_Vocabulary m on a.FInterestCycle = m.FVocCode and m.FVocTypeCode = " +
	            dbl.sqlString(YssCons.YSS_CNT_INTCYL) +
	            " left join Tb_Fun_Vocabulary n on a.FInterestOrigin = n.FVocCode and n.FVocTypeCode = " +
	            dbl.sqlString(YssCons.YSS_CNT_INTORG) + " " +
	            " left join Tb_Fun_Vocabulary nn on a.FInterestWay = nn.FVocCode  and nn.FVocTypeCode = " +
	            dbl.sqlString(YssCons.YSS_PCA_INTERESTWAY) + " " +
	            
	            " Where a.FCheckState = 1 and a.FPortCode in (" + operSql.sqlCodes(this.strPortCode) + ")" +
	            buildSql + 
	            
	            ") y " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
	            " order by y.FCheckState, y.FCreateTime desc ) show" +

//	            " join (select distinct FCashAccCode from (select FCashAccCode from " + //获取唯一值
//
//	            pub.yssGetTableName("tb_stock_Cash") +
//	            " where FCheckState = 1 and FPortCode in (" +
//	            this.operSql.sqlCodes(this.strPortCode) +
//	            ") and FStorageDate between  " +
//
//	            dbl.sqlDate(YssFun.addDay(this.beginDate, -1)) +
//	            " and " + // 日期段应为前一天至结束日期，只要这一段时间内有库存帐户信息，就显示。
//	            //--------------------------------------------------------------------//
//	            dbl.sqlDate(this.endDate) +
//
//	            " union select b.fcashacccode as FCashAccCode from " +
//	            pub.yssGetTableName("tb_cash_transfer") + " a," +
//	            pub.yssGetTableName("tb_cash_subtransfer") +
//	            " b where " +
//	            " b.FCheckState = 1 and b.FPortCode in (" +
//	            this.operSql.sqlCodes(this.strPortCode) +
//	            ") and a.FNum = b.FNum and a.FTransferDate between  " +
//	            dbl.sqlDate(YssFun.addDay(this.beginDate,
//	                                      -1)) + " and " +
//	            dbl.sqlDate(this.endDate) +
//	            ") al) stock on show.FCashAccCode = stock.FCashAccCode " +

	            " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
	            pub.getPrefixTB() + "' ";
							
				
			CashAccountBean cashAccount = (CashAccountBean) pub.getParaSettingCtx().getBean("cashaccount");
			cashAccount.setYssPub(pub);
			if (this.bOverGroup == true) {
				cashAccount.setBOverGroup(true);
			}
				
			reStr = ((CashAccountBean)cashAccount).builderListViewData(str);
		}
		catch(Exception e) {
			throw new YssException("查询TA申购款数据出错", e);
		}
		finally {
            dbl.closeResultSetFinal(rs);
        }
		return reStr;
	}
	
	/**
	 * add by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A -----------//
	 * @return
	 * @throws YssException
	 */
	public String getGroupCashAcc() throws YssException {
		
		CashAccountBean cashAccount = (CashAccountBean) pub.getParaSettingCtx().getBean("cashaccount");
		cashAccount.setYssPub(pub);

		this.bOverGroup = true; //跨组合群
        String sAllGroup = ""; //定义一个字符用来保存执行后的结果传到前台
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码
        String[] assetGroupCodes = this.assetGroupCode.split(YssCons.YSS_GROUPSPLITMARK); //按组合群的解析符解析组合群代码
        String[] strPortCodes = this.strPortCode.split(YssCons.YSS_GROUPSPLITMARK); //按组合群的解析符解析组合代码
        try {
            for (int i = 0; i < assetGroupCodes.length; i++) { //循环遍历每一个组合群
                this.assetGroupCode = assetGroupCodes[i]; //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode); //修改公共变量的当前组合群代码
                this.strPortCode = strPortCodes[i]; //得到一个组合群下的组合代码
                String sGroup = this.getOperValue("getCashAccount"); //调用以前的执行方法
                sAllGroup = sAllGroup + sGroup + YssCons.YSS_GROUPSPLITMARK; //组合得到的结果集
            }
            if (sAllGroup.length() > 7) { //去除尾部多余的组合群解析符
                sAllGroup = sAllGroup.substring(0, sAllGroup.length() - 7);
            }
        } catch (Exception e) {
            throw new YssException("获取现金帐户信息出错", e);
        } finally {
            pub.setPrefixTB(sPrefixTB); //还原公共变的里的组合群代码
            this.bOverGroup = false;
        }
        return sAllGroup; //把结果返回到前台进行显示
    }

	public double getFPortdegree() {
		return FPortdegree;
	}

	public void setFPortdegree(double fPortdegree) {
		FPortdegree = fPortdegree;
	}
	
	//add by fangjiang 2011.11.08 story 1589
	private double getUnitCostByCury() throws YssException {
		ResultSet rs = null;
		String strSql = "";
		double result = 1.0;	
		try {
			strSql = " select FDegreeCost from " + pub.yssGetTableName("Tb_TA_ClassFundDegree")
					 + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.strPortCode);
			if(this.isMultiClass(this.strPortCode)){ //多class
				if(this.accWayState == 0){
					strSql += " and FCuryCode = " + dbl.sqlString(this.strCuryCode);
				}else {
					strSql += " and FPortClsCode = " + dbl.sqlString(this.strPortClsCode);
				}	
			}else{ //单class
				strSql += " and FCuryCode = ' ' and FPORTCLSCODE = ' ' ";
			}
				
			rs = dbl.openResultSet(strSql);			
			while (rs.next()) {				
				result = rs.getDouble("FDegreeCost");
			}							
		}
		catch(Exception e) {
			throw new YssException("查询份额成本出错", e);
		}
		finally {
            dbl.closeResultSetFinal(rs);
        }
		return result;
	}
	
	public double getUnitCostByCury(String portCode, String curyCode) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		double result = 1.0;	
		try {
			strSql = " select FDegreeCost from " + pub.yssGetTableName("Tb_TA_ClassFundDegree")
					 + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode)
					 + " and FCuryCode = " + dbl.sqlString(curyCode);		
			rs = dbl.openResultSet(strSql);			
			while (rs.next()) {				
				result = rs.getDouble("FDegreeCost");
			}							
		}
		catch(Exception e) {
			throw new YssException("查询份额成本出错", e);
		}
		finally {
            dbl.closeResultSetFinal(rs);
        }
		return result;
	}
	
	public double getUnitCostByFJCode(String portCode, String portclscode) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		double result = 1.0;	
		try {
			strSql = " select FDegreeCost from " + pub.yssGetTableName("Tb_TA_ClassFundDegree")
					 + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode)
					 + " and FPortClsCode = " + dbl.sqlString(portclscode);		
			rs = dbl.openResultSet(strSql);			
			while (rs.next()) {				
				result = rs.getDouble("FDegreeCost");
			}							
		}
		catch(Exception e) {
			throw new YssException("查询份额成本出错", e);
		}
		finally {
            dbl.closeResultSetFinal(rs);
        }
		return result;
	}
	
	
	/**
	 * add by fangjiang 2011.12.23 STORY #2020 
	 * 如果当天没有已审核的ta交易数据，则返回true
	 * @param portCode
	 * @param d
	 * @return
	 * @throws YssException
	 */
	public boolean checkTaTradeInfo(String portCode, Date d) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		boolean result = true;	
		try {
			strSql = " select fportcode from " + pub.yssGetTableName("Tb_Ta_Trade")
					 + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode)
					 //+ " and FTradeDate = " + dbl.sqlDate(d);	
					 + " and FConfimDate = " + dbl.sqlDate(d);	//BUG4865在做资产估值的日志中显示组合【001】在【2012-04-27】没有录入TA交易数据  modify by zhangjun 2012.06.28
			rs = dbl.openResultSet(strSql);			
			if (rs.next()) {				
				result = false;
			}							
		}
		catch(Exception e) {
			throw new YssException("查询ta交易数据出错", e);
		}
		finally {
            dbl.closeResultSetFinal(rs);
        }
		return result;
	}
	//story2683  add by zhouwei 20120611 计算实收基金金额
	//20120808 modified by liubo.Bug #5215
	//实收基金金额的计算公式变为：申购款*（申请日实收基金金额/申请日总净值）
	public String getPaidUpFundsMoney() throws YssException{
		ResultSet rs=null;
		ResultSet rsDetail = null;
		String sql="";
		try{
			
			/**Start 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001
             * 根据通参“分级本币实收基金”设置的值进行计算*/
            //按“申购时原币折算，赎回时移动加权”进行计算。在计算之前先以交易类型区分申购和赎回
			if (getPaidInCalcType())
            {
            	if (this.strSellTypeCode.trim().equals("01"))
            	{
            		this.getPaidInMoney_HFTAllot();
            	}
            	else if (this.strSellTypeCode.trim().equals("02"))
            	{
            		this.getPaidInMoney_HFTRedeem();
            	}
            }
			//以下是原有的默认算法
            else
            {
            	
				//根据讨论，BUG 5215实现逻辑最终确定如下：
				//1、从“TA多CLASS基金份额成本设置”中查找组合代码与该条TA交易数据相符，币种、分级组合未填的数据，并选取启用日期最早的一条数据
				//2、若不存在符合条件1的TA份额成本数据，则直接返回数量
				//3、存在符合条件1的数据，则判断该条数据的“基金份额成本”字段。该字段值为1，则直接返回数量。
				//4、若不为1，则以“实收基金金额=申购款*（申请日实收基金金额/申请日总净值）”的公式返回实收基金金额
				sql=" select FDEGREECOST From " + pub.yssGetTableName("Tb_TA_ClassFundDegree") +
	            " a where a.FPortCode = " + dbl.sqlString(this.strPortCode) + " and a.FCheckState = 1 "
	            
	            //20120905 modified by liubo.将此处的判断确认日期改为判断交易日期
	//            +" and a.FPORTCLSCODE=' ' and  a.FCURYCODE=' ' and a.FStartDate<="+dbl.sqlDate(this.dConfimDate)
	            +" and a.FPORTCLSCODE=' ' and  a.FCURYCODE=' ' and a.FStartDate<="+dbl.sqlDate(this.dTradeDate)
	            +"  order by a.FStartDate"; 
	
				rs=dbl.openResultSet(sql);
				if(rs.next())
				{
					if (rs.getDouble("FDEGREECOST") == 1)
					{
						this.dPaidInMoney=this.dSellAmount;
					}
					else
					{
						//实收基金金额=申购款*（申请日实收基金金额/申请日总净值）。
						sql = "select a.FPortMarketValue, sum(b.FCost) as Cost " +
					  	  " from " + pub.yssGetTableName("tb_data_navdata") + " a " +
					  	  " left join " + pub.yssGetTableName("tb_stock_ta") + " b on a.fnavdate = b.fstoragedate " +
					  	  " where a.fnavdate = " + dbl.sqlDate(dTradeDate) + 
					  	  " and a.fkeycode = 'TotalValue' and a.FPortCode = " + dbl.sqlString(this.strPortCode) +
					  	  " and b.FPortCode = " + dbl.sqlString(this.strPortCode) +
					  	  " group by a.fportmarketvalue ";
						
						rsDetail = dbl.queryByPreparedStatement(sql);
						
						if(rsDetail.next())
						{
						//实收基金金额=申购款*（申请日实收基金金额/申请日总净值）。
							if (rsDetail.getDouble("FPortMarketValue") == 0 || rsDetail.getDouble("Cost") == 0)
							{
								this.dPaidInMoney=this.dSellAmount;
							}
							else
							{
								this.dPaidInMoney = YssD.div(rsDetail.getDouble("Cost"), rsDetail.getDouble("FPortMarketValue"));
								this.dPaidInMoney = YssD.mul(this.dSellMoney == 0 ? this.dBeMarkMoney : this.dSellMoney, this.dPaidInMoney);
							}
						}
						else
						{
							this.dPaidInMoney=this.dSellAmount;
						}
					}
				}else{
					this.dPaidInMoney=this.dSellAmount;
				}
				
				//20130218 deleted by liubo.
				//BUG 6279可能是由于数据原因导致，先注销掉这段代码
				//*************************************
				
				//20130109 added by liubo.Bug #6279
				//计算已实现损益平准金 = 基准金额-实收基金金额-未实现损益平准金
				//其中基准金额和未实现损益平准金都已经做了汇率转换，实收基金金额未做汇率转换，依然是原币，
				//这种情况下，若一个组合由单币种申赎，转为多币种申赎，已实现损益平准金的计算就会出现问题
				//在此为实收基金金额加上汇率转换
				//========================================
	//			this.dPaidInMoney = YssD.div(YssD.mul(this.dPaidInMoney, this.dBaseCuryRate), this.dPortCuryRate, 2);
				//==================end======================
				
				//****************end*********************
				
				/**End 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001*/
            }
		}catch (Exception e) {
			throw new YssException("获取实收基金金额出错!", e);
		}finally{
			dbl.closeResultSetFinal(rs,rsDetail);
		}
		return this.buildRowStr();
	}
	
	/**
	 * 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001
	 * 计算实收基金，申购时原币折算
	 * @return
	 * @throws YssException
	 */
	public String getPaidInMoney_HFTAllot() throws YssException
	{
		String strSql = "";
		ResultSet rs = null;
		double dDEGREECOST = 1;		//分级份额成本
		
		try
		{
			strSql = "select * from " + pub.yssGetTableName("tb_ta_classfunddegree") +
					 " where FPORTCLSCODE = " + dbl.sqlString(this.strPortClsCode) + 
					 " and FPortCode = " + dbl.sqlString(this.strPortCode) +
					 " and FSTARTDATE = (select max(FSTARTDATE) as FSTARTDATE from " + pub.yssGetTableName("tb_ta_classfunddegree") +
					 " where FSTARTDATE <= " + dbl.sqlDate(this.dConfimDate) + " and FPORTCLSCODE = " + dbl.sqlString(this.strPortClsCode) +
					 " and FPortCode = " + dbl.sqlString(this.strPortCode) + ")";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				dDEGREECOST = rs.getDouble("FDEGREECOST");	//获取分级份额成本
			}
			
			dbl.closeResultSetFinal(rs);
			
			//实收基金=原币实收基金*确认日汇率=销售数量*｛TA份额成本中维护的分级份额成本｝*确认日汇率
			this.dPaidInMoney = YssFun.roundIt(YssD.mul(YssFun.roundIt(YssD.mul(this.dSellAmount, dDEGREECOST), 2), 
									YssD.div(this.dBaseCuryRate, this.dPortCuryRate)),2);
		}
		catch(Exception ye)
		{
			throw new YssException(ye);
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return this.buildRowStr();
	}
	

	/**
	 * 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001
	 * 计算实收基金，赎回时移动加权
	 * @return
	 * @throws YssException
	 */
	public String getPaidInMoney_HFTRedeem() throws YssException
	{
		String strSql = "";
		ResultSet rs = null;
		double dDEGREECOST = 1;		//分级份额成本
		double dSgtAmount = 1;		//交易日库存数量
		double dPortCuryCost = 1;	//交易日本位币成本
		
		try
		{
			strSql = "select * from " + pub.yssGetTableName("tb_ta_classfunddegree") +
			 " where FPORTCLSCODE = " + dbl.sqlString(this.strPortClsCode) + 
			 " and FPortCode = " + dbl.sqlString(this.strPortCode) +
			 " and FSTARTDATE = (select max(FSTARTDATE) as FSTARTDATE from " + pub.yssGetTableName("tb_ta_classfunddegree") +
			 " where FSTARTDATE <= " + dbl.sqlDate(this.dConfimDate) + " and FPORTCLSCODE = " + dbl.sqlString(this.strPortClsCode) +
			 " and FPortCode = " + dbl.sqlString(this.strPortCode) + ")";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				dDEGREECOST = rs.getDouble("FDEGREECOST");	//获取分级份额成本
			}
			
			dbl.closeResultSetFinal(rs);
			
			strSql = "select * from " + pub.yssGetTableName("tb_stock_ta")+
					 " where FPortCode = " + dbl.sqlString(this.strPortCode)+
					 " and FPortClsCode = " + dbl.sqlString(this.strPortClsCode) +
					 " and FStorageDate = " + dbl.sqlDate(this.dTradeDate);
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				dSgtAmount = rs.getDouble("FSTORAGEAMOUNT");		//获取交易日库存数量
				dPortCuryCost = rs.getDouble("FPORTCURYCOST");		//获取交易日本位币成本
			}
			
			//实收基金=（赎回数量/申请日分级库存数量）*申请日分级实收基金库存
			this.dPaidInMoney = YssFun.roundIt(YssD.mul(YssD.div(this.dSellAmount, dSgtAmount), dPortCuryCost), 2);
		}
		catch(Exception ye)
		{
			throw new YssException(ye);
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return this.buildRowStr();
	}
	
	/**
	 * 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001
	 * 获取通参“分级本币实收基金”的值，并对这个值进行判断
	 * 选定的值为1，表示默认算法，返回false
	 * 选定的值为2，表示需要按“申购时原币折算，赎回时移动加权”进行计算，返回true
	 * 通参未设置的情况下，使用默认算法
	 * @return
	 * @throws YssException
	 */
	private boolean getPaidInCalcType() throws YssException
	{
		boolean bReturn = false;
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			strSql = "select * from " + pub.yssGetTableName("tb_pfoper_pubpara") + 
					 " where fpubparacode='CtlPaidInCalcType' and FCtlCode = 'cboCalcType'";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				if (rs.getString("FCtlValue").split(",")[0].equals("2"))
				{
					bReturn = true;
				}
			}
			
		}
		catch(Exception ye)
		{
			throw new YssException();
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return bReturn;
	}
	
	
	 /**
     * 通过判断交易类型来判断模式，
     * 分红类型 03  金额方向  无         数量方向 无   --- 手工   true
     * 分红类型 03  金额方向  流出    数量方向 无   --- 自动  false
     * @return
     * @throws YssException
     */
    private boolean getDealMode()throws YssException{
		StringBuffer queryBuf = new StringBuffer();
		ResultSet rs = null;
		try {

			queryBuf
					.append(" select 1 from ")
					.append(pub.yssGetTableName("tb_ta_selltype"))
					.append(
							" where fcheckstate=1 and fselltypecode='03' and fcashind=0 and famountind=0 ");

			rs = dbl.openResultSet(queryBuf.toString());
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new YssException("判断Ta分红处理方式出错... ...");
		} finally {
			queryBuf.setLength(0);
			dbl.closeResultSetFinal(rs);
		}
    }
    //--- end --- 
	
    /**
     * add by fangjiang story #2782 2012.09.12
     */
    private void doMClass() throws YssException {
    	//add by fangjiang story 3264 2012.11.19 
    	if(4 == this.accWayState){ 
    		this.strPortClsCode = this.getParrentCode();
    	}
    	//end by fangjiang story 3264 2012.11.19 
        getWSXScale(); //获取未实现比例        
        String ErrorMsg = "";
        CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);         
        double ctlMoney = 0; 
        double ctlMoney1 = 0;   	
    	if(pubpara.getIncomeBalMode(this.strPortCode)){
    		if(this.dBaseCuryRate==0 && this.dPortCuryRate ==0 ){
    			ErrorMsg = "请先设置汇率，再进行计算!";
    			throw new YssException(ErrorMsg);
    		}
    		String result = pubpara.getDigitsPortMethod("CtlIncomeBal","TA_TAInCome","CtlIncomeBal","selPort","cboIncomeBalMode1",strPortCode,"0,0");
    		if("0,0".equals(result)){
    			ctlMoney = YssD.div(YssD.mul(this.dBeMarkMoney, this.dBaseCuryRate), this.dPortCuryRate, 2); 
    		}else{
    			ctlMoney = YssD.add
    			           (
			        		   YssD.div
			        		   (
		        				   YssD.mul
		        				   (
	        						   YssD.sub(this.dBeMarkMoney, this.yfshf, this.xsfsr), 
	        						   this.dBaseCuryRate
        						   ), 
        						   this.dPortCuryRate,
        						   2
    						   ),
    						   YssD.div
			        		   (
		        				   YssD.mul
		        				   (
	        						   this.yfshf,
	        						   this.dBaseCuryRate
        						   ), 
        						   this.dPortCuryRate,
        						   2
    						   ),
    						   YssD.div
			        		   (
		        				   YssD.mul
		        				   (
	        						   this.xsfsr, 
	        						   this.dBaseCuryRate
        						   ), 
        						   this.dPortCuryRate,
        						   2
    						   )
						   );
    		}
    		ctlMoney1 = YssD.div(YssD.mul(this.dBeMarkMoney, this.dBaseCuryRate), this.dPortCuryRate, 2); 
    	}else{
    		ctlMoney = this.dBeMarkMoney;
    		ctlMoney1 = this.dBeMarkMoney;
    	}
    	//损益平准金未实现
        this.dIncomeNotBal = YssD.round
        					 (
        					 	 YssD.mul
        					 	 (
        					 	     ctlMoney1, 
    					 			 this.dScale
					 			 ), 
					 			 2
				 			 );
        //实收基金
        this.dPaidInMoney = YssD.mul(this.dSellAmount, getUnitCostByCury());
        //modify by fangjiang story 3264 2012.11.20 
        if(3 == this.accWayState){ 
        	//--- add by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 start---//
        	if(this.strSellTypeCode.equals("02")){//若为赎回
        		TaPortClsBean portCls = new TaPortClsBean(); //通过组合分级得到组合代码
        		portCls.setYssPub(pub);
            	portCls.setPortClsCode(strPortClsCode);
            	portCls.getSetting();
            	//获取组合分级级别
            	String portClsRank = portCls.getPortClsRank();//组合分级级别
        	
            	CommonPretFun pretFun = new CommonPretFun();
            	pretFun.setYssPub(pub);
            	//实收基金金额 = round（（赎回数/成交日库存数 ）* 成交日该分级实收资本，2）
            	this.dPaidInMoney = pretFun.calTaPaidInMoney(dTradeDate, strPortClsCode, dSellAmount,
        	                                             strPortCode, portClsRank);
        	}else{//若为申购
        	//--- add by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 end---//
        		//修改实收基金金额 算法
        		this.dPaidInMoney = YssD.div
	        						(
	        								YssD.mul
	        								(
	        										this.dPaidInMoney, 
	        										this.dBaseCuryRate
	        								), 
	        								this.dPortCuryRate,
	        								2
	        						);
        	}//add by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001
        }else if(4 == this.accWayState){
        	this.dPaidInMoney = YssD.round(this.dPaidInMoney, 2);
        }
        //end by fangjiang story 3264 2012.11.20 
        //损益平准金已实现
    	this.dIncomeBal = YssD.sub(ctlMoney, this.dPaidInMoney, this.dIncomeNotBal);    	
	}
    
    /*
     * add by huangqirong 2012-09-16 story #2782
     * */
    private void getWSXScale() throws YssException {
    	CtlNavRep navRep = new CtlNavRep();
    	navRep.setYssPub(this.pub);
    	navRep.setPortCode(this.strPortCode);
    	double wsx =  navRep.getClassNetValue(this.strPortClsCode , this.dMarkDate == null ? this.dTradeDate : this.dMarkDate, "wsx" , "FNetValue");
    	double netValue = navRep.getClassNetValue(this.strPortClsCode , this.dMarkDate == null ? this.dTradeDate : this.dMarkDate, "01" , "FNetValue");
    	//--- add by songjie 2013.07.15 BUG 8624 QDV4赢时胜(北京)2013年07月15日01_B start---//
    	if(3 == this.accWayState){
    		//add by yeshenghong story4127 20130905   ---start  汇率使用交易日汇率
    		BaseOperDeal operDeal = new BaseOperDeal();
        	operDeal.setYssPub(pub);
    		double baseRate = operDeal.getCuryRate(dTradeDate, "yyyy-MM-dd", strCuryCode, strPortCode, "base");
    		double portRate = operDeal.getCuryRate(dTradeDate, "yyyy-MM-dd", strCuryCode, strPortCode, "port");      
    		//add by yeshenghong story4127 20130905   ---end
    		netValue = YssD.div(YssD.mul(netValue, baseRate), portRate);
    	}
    	//--- add by songjie 2013.07.15 BUG 8624 QDV4赢时胜(北京)2013年07月15日01_B end---//
    	this.dScale = YssD.div(wsx, netValue);       
    }
    
	public double getQHGZ(String portcode, Date d, String acctattr) throws YssException{
		ResultSet rs=null;
		String sql="";
		double result = 0.0;
		try{			
			sql= " select sum(fstandardmoneymarketvalue * -1) as fmoney from " +
				 pub.yssGetTableName("tb_rep_guessvalue") +
                 " where fdate = " + dbl.sqlDate(d) +
                 " and facctattr = " + dbl.sqlString(acctattr) +
                 " and facctdetail = 1 and fportcode = " + dbl.sqlString(this.getBookSetId(portcode));
			rs=dbl.openResultSet(sql);
			if(rs.next()){
				result = rs.getDouble("fmoney");
			}
			return result;
		}catch (Exception e) {
			throw new YssException("获取期货估增出错!", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * modify by huangqirong 2013-04-24 bug #7486 调整组合套帐链接相关代码
	 * */
	public String getBookSetId(String sPortCode) throws YssException {
        String sResult = "";
        //String strSql = "";
        //ResultSet portRs = null;
        YssFinance finace = new YssFinance(); 
        
        try {
        	finace.setYssPub(this.pub);
			String tmpSetId = finace.getBookSetId(pub.getAssetGroupCode() , sPortCode);
			if(tmpSetId != null && tmpSetId.trim().length() > 0 )
				sResult = tmpSetId;
            /*strSql = " select distinct to_Number(FbookSetCode) as FbookSetCode from " +
            	pub.yssGetTableName("Tb_Vch_PortSetLink")+" WHERE FPORTCODE IN ( "+dbl.sqlString(sPortCode)+
            	" ) and FCHECKSTATE = 1 ";
            portRs = dbl.openResultSet(strSql);
            if (portRs.next()) {
                sResult = portRs.getString("FbookSetCode");
            }*/
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
        	//dbl.closeResultSetFinal(portRs);
        }
    }
	
	//add by fangjiang story 3264 2012.11.20 
	public String getParrentCode() throws YssException {
		String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        try {        			
            strSql = " select fparrentcode from " + pub.yssGetTableName("tb_ta_portcls") +
                     " WHERE FPORTCODE = " + dbl.sqlString(this.strPortCode) +
            	     " and FCHECKSTATE = 1 and Fportclscode = " + dbl.sqlString(this.strPortClsCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                sResult = rs.getString("fparrentcode");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
        	dbl.closeResultSetFinal(rs);
        }
	}
	
	//add by huangqirong 2012-07-10 STORY #1434   getsubassetcode
	public String getSubAssetCode(String portCode) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		String result = "";	
		try {
			strSql = " select pf2.fsubassettype as fsubassettype from ( " +
						" select fportcode,max(fstartdate) as fstartdate from " + pub.yssGetTableName("tb_para_portfolio")+ 
						" pf where pf.fportcode = " + dbl.sqlString(portCode) + "  and pf.fcheckstate = 1 group by pf.fportcode " +
						" ) pf1 " +
						" left join " + pub.yssGetTableName("tb_para_portfolio")+
						" pf2 on  pf2.fportcode= pf1.fportcode and pf2.fstartdate = pf1.fstartdate";		
			rs = dbl.openResultSet(strSql);			
			if (rs.next()) {				
				result = rs.getString("fsubassettype");
			}
		}
		catch(Exception e) {
			throw new YssException("查询组合资产子类型数据出错", e);
		}
		finally {
            dbl.closeResultSetFinal(rs);
        }
		return result;
	}
	
	
}
