package com.yss.main.operdata;

import java.sql.*;
import java.util.*;
import java.util.Date;

//QDV4赢时胜（上海）2009年04月15日01_B MS00382
import com.yss.commeach.*;
import com.yss.dsub.*;
import com.yss.main.cashmanage.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.*;
import com.yss.main.storagemanage.*;
import com.yss.main.syssetting.RightBean;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 *
 * <p>Title:外汇交易 </p>
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
public class RateTradeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String Num = "";
    private Date tradeDate;
    private String tradeTime = "00:00:00";
    private String portCode = "";
    private String portName = "";
    private String analysisCode1 = "";
    private String analysisName1 = "";
    private String analysisCode2 = "";
    private String analysisName2 = "";
    private String analysisCode3 = "";
    private String analysisName3 = "";

    private String bPortCode = "";
    private String bPortName = "";
    private String bAnalysisCode1 = "";
    private String bAnalysisName1 = "";
    private String bAnalysisCode2 = "";
    private String bAnalysisName2 = "";
    private String bAnalysisCode3 = "";
    private String bAnalysisName3 = "";

    private String bCashAccCode = "";
    private String bCashAccName = "";
    private String sCashAccCode = "";
    private String sCashAccName = "";
    private String settleTime = "00:00:00";
    private Date settleDate;
    private String bSettleTime = "00:00:00";
    private Date bSettleDate;
    private String tradeType = "";
    private String tradeTypeName;
    private String catType = "";
    private String catTypeName;
    private String sPayCode = "";       //付款人代码 新增字段 080220 by ly
    private String sPayName = "";
    private String sReceiverCode = "";  //收款人代码 新增字段 080220 by ly
    private String sReceiverName = "";
    private double exCuryRate;
    private double lingCuryRate;
    private double bMoney;
    private double sMoney;
    private double baseMoney;
    private double portMoney;
    private double rateFx;
    private double upDown;
    private String desc = "";

    private String bCuryCode = "";
    private String bCuryName = "";

    private String sCuryCode = "";
    private String sCuryName = "";

    private String oldNum = "";
    private Date oldTradeDate;
    private String oldTradeTime = "00:00:00";
    private String oldPortCode = "";
    private String oldBCashAccCode = "";
    private String oldSCashAccCode = "";

    CashTransAdmin tranAdmin = null;
    String sTranNum = "";

    private boolean analy1;
    private boolean analy2;
    private boolean analy3;

    private double dscale = 0;
//    private String isOnlyColumnss = "0";
    private Date beginDate;
    private Date endDate;

    private double bCuryFee;        //买入货币费用
    private double sCuryFee;
    private String sRecycled = "";  //保存未解析前的字符串

    private RateTradeBean filterType;
    private String sDateSource;     //数据来源，HD 手工输入；ZD 自动读取
    private String multAuditString = ""; //批量处理数据 MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩

    private String sRateTradeType= "0";//edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A
    
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    private String strAttrClsCode = "";//所属分类
    private String strAttrClsName = "";
    private String strBAttrClsCode = "";//所属分类
    private String strBAttrClsName = "";
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    /** add by huangqirong 2012-08-14 story #2822 保证金  **/
    private String bailType = " ";   //保证金类型 
	private double bailScale = 0;//保证金比例 
    private double bailFix = 0; //固定保证金 
    private String bailCashCode = ""; //保证金账户
    private String bailCashName = "";
    private String RateReason = "";//换汇原因   add by zhaoxianlin 20121218 STORY #3383 外管局报表
    
    
    public String getRateReason() {
		return RateReason;
	}

	public void setRateReason(String rateReason) {
		RateReason = rateReason;
	}

	public String getBailCashCode() {
		return bailCashCode;
	}

	public void setBailCashCode(String bailCashCode) {
		this.bailCashCode = bailCashCode;
	}
	
	public String getBailCashName() {
		return bailCashName;
	}

	public void setBailCashName(String bailCashName) {
		this.bailCashName = bailCashName;
	}

	public String getBailType() {
		return bailType;
	}

	public void setBailType(String bailType) {
		this.bailType = bailType;
	}

	public double getBailScale() {
		return bailScale;
	}

	public void setBailScale(double bailScale) {
		this.bailScale = bailScale;
	}

	public double getBailFix() {
		return bailFix;
	}

	public void setBailFix(double bailFix) {
		this.bailFix = bailFix;
	}    
    //---end---
    
    
    public RateTradeBean getFilterType() {
        return filterType;
    }

    //---edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A---//
    public String getRateTradeType(){
    	return sRateTradeType;
    }
    //---edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A---//
    
    public String getDesc() {
        return desc;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getTradeType() {
        return tradeType;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public String getNum() {
        return Num;
    }

    public double getUpDown() {
        return upDown;
    }

    public double getBMoney() {
        return bMoney;
    }

    public String getAnalysisCode3() {
        return analysisCode3;
    }

    public double getExCuryRate() {
        return exCuryRate;
    }

    public String getOldNum() {
        return oldNum;
    }

    public double getPortMoney() {
        return portMoney;
    }

    public double getSMoney() {
        return sMoney;
    }

    public String getCatType() {
        return catType;
    }

    public String getAnalysisCode2() {
        return analysisCode2;
    }

    public String getSettleTime() {
        return settleTime;
    }

    public String getSCashAccCode() {
        return sCashAccCode;
    }

    public String getAnalysisCode1() {
        return analysisCode1;
    }

    public double getBaseMoney() {
        return baseMoney;
    }

    public String getBCashAccCode() {
        return bCashAccCode;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public double getRateFx() {
        return rateFx;
    }

    public void setLingCuryRate(double lingCuryRate) {
        this.lingCuryRate = lingCuryRate;
    }

    public void setFilterType(RateTradeBean filterType) {
        this.filterType = filterType;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public void setNum(String Num) {
        this.Num = Num;
    }

    public void setUpDown(double upDown) {
        this.upDown = upDown;
    }

    public void setBMoney(double bMoney) {
        this.bMoney = bMoney;
    }

    public void setAnalysisCode3(String analysisCode3) {
        this.analysisCode3 = analysisCode3;
    }

    public void setExCuryRate(double exCuryRate) {
        this.exCuryRate = exCuryRate;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

    public void setPortMoney(double portMoney) {
        this.portMoney = portMoney;
    }

    public void setSMoney(double sMoney) {
        this.sMoney = sMoney;
    }

    public void setCatType(String catType) {
        this.catType = catType;
    }

    public void setAnalysisCode2(String analysisCode2) {
        this.analysisCode2 = analysisCode2;
    }

    public void setSettleTime(String settleTime) {
        this.settleTime = settleTime;
    }

    public void setSCashAccCode(String sCashAccCode) {
        this.sCashAccCode = sCashAccCode;
    }

    public void setAnalysisCode1(String analysisCode1) {
        this.analysisCode1 = analysisCode1;
    }

    public void setBaseMoney(double baseMoney) {
        this.baseMoney = baseMoney;
    }

    public void setBCashAccCode(String bCashAccCode) {
        this.bCashAccCode = bCashAccCode;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public void setRateFx(double rateFx) {
        this.rateFx = rateFx;
    }

    public void setBCashAccName(String bCashAccName) {
        this.bCashAccName = bCashAccName;
    }

    public void setAnalysisName2(String analysisName2) {
        this.analysisName2 = analysisName2;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setAnalysisName3(String analysisName3) {
        this.analysisName3 = analysisName3;
    }

    public void setTradeTypeName(String tradeTypeName) {
        this.tradeTypeName = tradeTypeName;
    }

    public void setAnalysisName1(String analysisName1) {
        this.analysisName1 = analysisName1;
    }

    public void setSCashAccName(String sCashAccName) {
        this.sCashAccName = sCashAccName;
    }

    public void setCatTypeName(String catTypeName) {
        this.catTypeName = catTypeName;
    }

    public void setOldSCashAccCode(String oldSCashAccCode) {
        this.oldSCashAccCode = oldSCashAccCode;
    }

    public void setOldTradeTime(String oldTradeTime) {
        this.oldTradeTime = oldTradeTime;
    }

    public void setOldTradeDate(Date oldTradeDate) {
        this.oldTradeDate = oldTradeDate;
    }

    public void setOldBCashAccCode(String oldBCashAccCode) {
        this.oldBCashAccCode = oldBCashAccCode;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setBCuryCode(String bCuryCode) {
        this.bCuryCode = bCuryCode;
    }

    public void setSCuryName(String sCuryName) {
        this.sCuryName = sCuryName;
    }

    public void setSCuryCode(String sCuryCode) {
        this.sCuryCode = sCuryCode;
    }

    public void setBCuryName(String bCuryName) {
        this.bCuryName = bCuryName;
    }

    public void setSCuryFee(double sCuryFee) {
        this.sCuryFee = sCuryFee;
    }

    public void setBCuryFee(double bCuryFee) {
        this.bCuryFee = bCuryFee;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    //---edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A start---//
    public void setRateTradeType(String stype){
    	this.sRateTradeType = stype;
    }
    //---edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A end---//
    
    public double getLingCuryRate() {
        return lingCuryRate;
    }

    public String getBCashAccName() {
        return bCashAccName;
    }

    public String getAnalysisName2() {
        return analysisName2;
    }

    public String getPortName() {
        return portName;
    }

    public String getAnalysisName3() {
        return analysisName3;
    }

    public String getTradeTypeName() {
        return tradeTypeName;
    }

    public String getAnalysisName1() {
        return analysisName1;
    }

    public String getSCashAccName() {
        return sCashAccName;
    }

    public String getCatTypeName() {
        return catTypeName;
    }

    public String getOldSCashAccCode() {
        return oldSCashAccCode;
    }

    public String getOldTradeTime() {
        return oldTradeTime;
    }

    public Date getOldTradeDate() {
        return oldTradeDate;
    }

    public String getOldBCashAccCode() {
        return oldBCashAccCode;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getBCuryCode() {
        return bCuryCode;
    }

    public void setBPortName(String bPortName) {
        this.bPortName = bPortName;
    }

    public void setBAnalysisCode3(String bAnalysisCode3) {
        this.bAnalysisCode3 = bAnalysisCode3;
    }

    public void setBAnalysisName3(String bAnalysisName3) {
        this.bAnalysisName3 = bAnalysisName3;
    }

    public void setBAnalysisName1(String bAnalysisName1) {
        this.bAnalysisName1 = bAnalysisName1;
    }

    public void setBAnalysisCode2(String bAnalysisCode2) {
        this.bAnalysisCode2 = bAnalysisCode2;
    }

    public void setBAnalysisCode1(String bAnalysisCode1) {
        this.bAnalysisCode1 = bAnalysisCode1;
    }

    public void setBAnalysisName2(String bAnalysisName2) {
        this.bAnalysisName2 = bAnalysisName2;
    }

    public void setBPortCode(String bPortCode) {
        this.bPortCode = bPortCode;
    }

    public void setBSettleTime(String bSettleTime) {
        this.bSettleTime = bSettleTime;
    }

    public void setBSettleDate(Date bSettleDate) {
        this.bSettleDate = bSettleDate;
    }

//    public void setStrisOnlyColumnss(String strisOnlyColumnss) {
//        this.strisOnlyColumnss = strisOnlyColumnss;
//    }

    public void setSReceiverName(String sReceiverName) {
        this.sReceiverName = sReceiverName;
    }

    public void setSReceiverCode(String sReceiverCode) {
        this.sReceiverCode = sReceiverCode;
    }

    public void setSPayName(String sPayName) {
        this.sPayName = sPayName;
    }

    public void setSPayCode(String sPayCode) {
        this.sPayCode = sPayCode;
    }

    public void setSDateSource(String sDateSource) {
        this.sDateSource = sDateSource;
    }

    public String getSCuryName() {
        return sCuryName;
    }

    public String getSCuryCode() {
        return sCuryCode;
    }

    public String getBCuryName() {
        return bCuryName;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public double getSCuryFee() {
        return sCuryFee;
    }

    public double getBCuryFee() {
        return bCuryFee;
    }

    public String getBSettleTime() {
        return bSettleTime;
    }

    public Date getBSettleDate() {
        return bSettleDate;
    }

//    public String getStrisOnlyColumnss() {
//        return strisOnlyColumnss;
//    }

    public String getSReceiverName() {
        return sReceiverName;
    }

    public String getSReceiverCode() {
        return sReceiverCode;
    }

    public String getSPayName() {
        return sPayName;
    }

    public String getSPayCode() {
        return sPayCode;
    }

    public String getSDateSource() {
        return sDateSource;
    }
    
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    public String getStrAttrClsCode() {
		return strAttrClsCode;
	}

	public void setStrAttrClsCode(String strAttrClsCode) {
		this.strAttrClsCode = strAttrClsCode;
	}

	public String getStrAttrClsName() {
		return strAttrClsName;
	}

	public void setStrAttrClsName(String strAttrClsName) {
		this.strAttrClsName = strAttrClsName;
	}

	 public String getStrBAttrClsCode() {
			return strBAttrClsCode;
		}

		public void setStrBAttrClsCode(String strBAttrClsCode) {
			this.strBAttrClsCode = strBAttrClsCode;
		}

		public String getStrBAttrClsName() {
			return strBAttrClsName;
		}

		public void setStrBAttrClsName(String strBAttrClsName) {
			this.strBAttrClsName = strBAttrClsName;
		}
	
//	public String getStrOldAttrClsCode() {
//		return strOldAttrClsCode;
//	}
//
//	public void setStrOldAttrClsCode(String strOldAttrClsCode) {
//		this.strOldAttrClsCode = strOldAttrClsCode;
//	}
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    
    //add by jiangshichao 2010.07.01 MS01303  QDV4工银2010年6月13日01_A 
    public String getbPortCode() {
		return bPortCode;
	}
    
    public RateTradeBean() {
    }

    /**
     * 产生资金调拨
     * 将private 改为public 因为在 CommonPretFun.java中要处理外汇交易导入时的资金调拨
     * @param sRateTradeNum String
     * @param sOldNum String
     * @throws YssException
     */
    public void createSavCashTrans(String sRateTradeNum, String sOldNum) throws YssException {
        createSavCashTrans(sRateTradeNum, sOldNum, 0);
    }

    public void createSavCashTrans(String sRateTradeNum, String sOldNum, int iCheckState) throws YssException {
        EachRateOper rateIn = null;     //定义取资金调拨的流入汇率方法　QDV4赢时胜（上海）2009年04月15日01_B MS00382
        EachRateOper rateOut = null;    //定义取资金调拨的流出汇率方法　QDV4赢时胜（上海）2009年04月15日01_B MS00382
        PortfolioBean bPort = new PortfolioBean();
    	//------add by yanghaiming 20090122 MS00888 QDV4中保2009年12月25日01_A  补充需求 合并太平版本代码 
      	String strSql = "";
      	ResultSet rs = null;
      	boolean bTPCash =false;//区分是太平资产的外汇交易资金调拨还是QDII的外汇产生资金调拨,合并版本时调整
    	CtlPubPara pubPara = null; //区分太平资产与QD产生资金调拨不一致参数，合并版本时调整 by leeyu
    	pubPara =new CtlPubPara();
    	pubPara.setYssPub(pub);
    	String sPara =pubPara.getNavType();//通过净值表类型来判断
    	boolean isHashBail = false; 		//add by huangqirong 2012-08-16 story #2822 外汇交易保证金
    	double bailMoney = 0 ;				//add by huangqirong 2012-08-16 story #2822 外汇交易保证金
    	boolean isDelete = true;			//add by huangqirong 2012-08-16 story #2822
    	if(sPara!=null && sPara.trim().equalsIgnoreCase("new")){
    		bTPCash=false;//国内QDII统计模式
    	}else{
    		bTPCash=true;//太平资产统计模式
    	}
    	//------MS00888 QDV4中保2009年12月25日01_A end 合并太平版本代码--------------------------------   
        bPort.setYssPub(pub);
        bPort.setPortCode(this.bPortCode);
        bPort.getSetting();

        PortfolioBean sPort = new PortfolioBean();
        sPort.setYssPub(pub);
        sPort.setPortCode(this.portCode);
        sPort.getSetting();
        
        CtlPubPara pubpara = new CtlPubPara();// add by wangzuochun 2010.06.08 MS01135 外汇交易中有关人民币账户的资金调拨汇率计算不正确 QDV4华夏2010年4月27日01_AB
        pubpara.setYssPub(pub); // add by wangzuochun 2010.06.08 MS01135 外汇交易中有关人民币账户的资金调拨汇率计算不正确 QDV4华夏2010年4月27日01_AB

        TransferBean tran = new TransferBean();
        TransferSetBean transfersetIn = new TransferSetBean();
        TransferSetBean transfersetOut = new TransferSetBean();
        TransferSetBean transfersetInFee = new TransferSetBean();
        TransferSetBean transfersetOutFee = new TransferSetBean();
        ArrayList tranSetList = new ArrayList();

        CashTransAdmin tranAdmin = new CashTransAdmin();
        tranAdmin.setYssPub(pub);

        /** add by huangqirong 2012-08-16 story #2822 外汇交易保证金 资金调拨
         * 保证金账户不为空 且  (固定保证金大于0 或  保证金比例大于0)
         * */
        if((this.bailCashCode != null && this.bailCashCode.length() > 0 ) && (this.bailFix > 0 || this.bailScale > 0)){
        	isHashBail = true;
            
            if("BL".equalsIgnoreCase(this.bailType))
            	bailMoney = YssD.mul(this.sMoney, this.bailScale) ; //保证金比例计算 
            else if("GD".equalsIgnoreCase(this.bailType))
            	bailMoney = this.bailFix ; //固定保证金 
        	
            if(bailMoney > 0){
            	/** T日 **/
	        	tran.setYssPub(pub);
	            tran.setDtTransDate(this.tradeDate); //业务日期
	            tran.setDtTransferDate(this.tradeDate);//调拨日期
	            tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
	            tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_RateTrade);
	            tran.setStrTransferTime("00:00:00");
	            tran.setDataSource(1);  //这里应为自动标记 
	            tran.setRateTradeNum(sRateTradeNum);	            
	            tran.checkStateId = iCheckState;
	            tran.creatorTime = YssFun.formatDate(new java.util.Date(),"yyyyMMdd HH:mm:ss");
	            
	            //保证金流入
	            transfersetIn.setDMoney(bailMoney); //保证金
	            transfersetIn.setSPortCode(this.portCode);
	            transfersetIn.setSAnalysisCode1(this.analysisCode1);
	            transfersetIn.setSAnalysisCode2(this.analysisCode2);
	            //用户需要对组合按资本类别进行子组合的分类 
	            transfersetIn.setStrAttrClsCode(this.strAttrClsCode);
	            
	            if (this.bCuryCode.equalsIgnoreCase(sPort.getCurrencyCode()) &&
	    	            !this.sCuryCode.equalsIgnoreCase(sPort.getCurrencyCode())) {
	    	            //当买入货币是组合货币时,本位币成本采用买入货币金额
	    	            //当baseMoney等于０时，应取当日的汇率
	    	            if (this.baseMoney == 0.0D) {
	    	            	rateIn = new EachRateOper();
	    	            	rateIn.setYssPub(pub);
	    	            	rateIn.getInnerPortRate(tradeDate, sCuryCode, portCode);
	    	                transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(
	    	                    tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
	    	                transfersetIn.setDPortRate(rateIn.getDPortRate());
	    	            } else {
	    	            	transfersetIn.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
	    	            	transfersetIn.setDPortRate(YssD.div(this.baseMoney, this.bMoney));
	    	            }
	    	        } else if (this.sCuryCode.equalsIgnoreCase(sPort.getCurrencyCode()) &&
	    	                   !this.bCuryCode.equalsIgnoreCase(sPort.getCurrencyCode())) {
	    	            //当卖出货币是组合货币时按照卖出货币金额做成本
	    	            //当baseMoney等于０时，取当日的汇率 
	    	        	
	    	        	//外汇交易中有关人民币账户的资金调拨汇率计算不正确 
	    	        	String strMode = pubpara.getRateTradeMode();
	    	        	
	    	        	if (strMode != null && strMode.length() > 0 && strMode.equals("1")){
	    	        		rateIn = new EachRateOper();
	    	        		rateIn.setYssPub(pub);
	    	        		rateIn.getInnerPortRate(tradeDate, sCuryCode, portCode);
	    	    			transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(
	    	    					tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
	    	    			transfersetIn.setDPortRate(rateIn.getDPortRate());
	    	        	}
	    	        	else if (strMode != null && strMode.length() > 0 && strMode.equals("0")){
	    	        		transfersetIn.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
	    	        		transfersetIn.setDPortRate(YssD.div(this.baseMoney, this.sMoney));
	    	        	}
	    	        	else{
	    	        		
	    	        		if (this.baseMoney == 0.0D) {
	    	        			rateIn = new EachRateOper();
	    	        			rateIn.setYssPub(pub);
	    	        			rateIn.getInnerPortRate(tradeDate, sCuryCode, portCode);
	    	        			transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(
	    	        					tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
	    	        			transfersetIn.setDPortRate(rateIn.getDPortRate());
	    	        		} else {
	    	        			transfersetIn.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
	    	        			transfersetIn.setDPortRate(YssD.div(this.baseMoney, this.sMoney));
	    	        		}
	    	        	}	            	
	    	        } else if (!this.sCuryCode.equalsIgnoreCase(sPort.getCurrencyCode()) &&
	    	                   !this.bCuryCode.equalsIgnoreCase(sPort.getCurrencyCode())) {
	    	            //当卖出货币和买入货币都不是组合货币时计算汇兑损益,转出账户的本位币成本按照日终汇率处理fazmm20071008
	    	        	transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
	    	                sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));	    	
	    	            
	    	            rateIn = new EachRateOper();
	    	            rateIn.setYssPub(pub);
	    	            rateIn.getInnerPortRate(tradeDate, sCuryCode, portCode);
	    	            transfersetIn.setDPortRate(rateIn.getDPortRate());
	    	            //----------------------------------------------------------------------
	    	        }
	    	
		            transfersetIn.setSCashAccCode(this.bailCashCode); //保证金账户
		            transfersetIn.setIInOut(1); //流入
		            transfersetIn.checkStateId = iCheckState;
	    	        
	    	        String strMode1 = pubpara.getRateTradeMode1();
	    	        if (strMode1 != null && strMode1.length() > 0 && strMode1.equals("0")){
	    	        	if (this.baseMoney == 0.0D) {
	    	        		rateIn = new EachRateOper();
	    	        		rateIn.setYssPub(pub);
	    	        		rateIn.getInnerPortRate(tradeDate, sCuryCode, portCode);
	            			transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(
	            					tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
	            			transfersetIn.setDPortRate(rateIn.getDPortRate());
	            		} else {
	            			transfersetIn.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
	            			transfersetIn.setDPortRate(YssD.div(this.baseMoney, this.portMoney));
	            		}
	            	}
	    	        
	    	        
	    	        //资金流出帐户
	    	        transfersetOut.setDMoney(bailMoney);
	    	        transfersetOut.setSPortCode(this.portCode);
	    	        transfersetOut.setSAnalysisCode1(this.analysisCode1);
	    	        transfersetOut.setSAnalysisCode2(this.analysisCode2);
	    	       //用户需要对组合按资本类别进行子组合的分类//
	    	        transfersetOut.setStrAttrClsCode(this.strAttrClsCode);
	    	        //用户需要对组合按资本类别进行子组合的分类//
	    	        //不保留位数，根据数据表的汇率字段小数位保留
	    	        transfersetOut.setSCashAccCode(this.sCashAccCode); //流出账户
	    	        transfersetOut.setIInOut(-1); //流出
	    	        transfersetOut.checkStateId = iCheckState;
	    	       
	    	        transfersetOut.setDBaseRate(transfersetIn.getDBaseRate());
	    	        transfersetOut.setDPortRate(transfersetIn.getDPortRate());
	    	        	    	        	    	        
		            tranSetList.add(transfersetIn);
		            tranSetList.add(transfersetOut);
		            tranAdmin.addList(tran, tranSetList);
		            tranAdmin.insert(sOldNum, isDelete);		            
		            		            
	    	        //tran = new TransferBean();
	    	        tranSetList = new ArrayList();
	    	        tranAdmin = new CashTransAdmin();
	    	        tranAdmin.setYssPub(pub);
	    	        	    	        
	    	        isDelete = false ;
	    	        
	    	        //保证金账户
	    	        tran.setDtTransDate(this.settleDate); //保证金业务日期
		            tran.setDtTransferDate(this.settleDate);//保证金调拨日期    
	    	        transfersetOut.setSCashAccCode(this.bailCashCode); //流出账户
	    	        
		            tranSetList.add(transfersetOut);
		            tranAdmin.addList(tran, tranSetList);
		            tranAdmin.insert(sOldNum, isDelete);
		            	    	        
	    	        tran = new TransferBean();
	    	        tranSetList = new ArrayList();
	    	        tranAdmin = new CashTransAdmin();
	    	        tranAdmin.setYssPub(pub);
            }
        }
        /*****end*****/
        
        //增加资金调拨记录
        tran.setYssPub(pub);
        tran.setDtTransDate(this.tradeDate); //存入时间
        tran.setDtTransferDate(this.bSettleDate);
        tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
        tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_RateTrade);
        tran.setStrTransferTime("00:00:00");
        tran.setDataSource(1);  //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
        tran.setRateTradeNum(sRateTradeNum);
        //2008.05.09 蒋锦 添加
        tran.checkStateId = iCheckState;
        tran.creatorTime = YssFun.formatDate(new java.util.Date(),"yyyyMMdd HH:mm:ss");
        tran.setDataSource(1); //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
        //资金流入帐户
        transfersetIn.setDMoney(this.bMoney);
        transfersetIn.setSPortCode(this.bPortCode);
        transfersetIn.setSAnalysisCode1(this.bAnalysisCode1);
        transfersetIn.setSAnalysisCode2(this.bAnalysisCode2);
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        transfersetIn.setStrAttrClsCode(this.strBAttrClsCode);
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        if(bTPCash){
        	//------add by yanghaiming 20090122 MS00888 QDV4中保2009年12月25日01_A  补充需求 
    		strSql = "select b.fusername as FSUSERNAME, c.fusername as FCASHACCNAME, d.fcashaccname as FSCASHACCNAME, e.fcashaccname as FBCASHACCNAME from "
    				+ pub.yssGetTableName("Tb_Data_RateTrade")
    				+ " a"
    				+ " left join (select FuserName,Fusercode from tb_sys_userlist) b on a.fanalysiscode1 = b.fusercode"
    				+ " left join (select FuserName,Fusercode from tb_sys_userlist) c on a.fbanalysiscode1 = c.fusercode "
    				+ " left join (select FCASHACCCODE, FCASHACCNAME from " + pub.yssGetTableName("Tb_Para_CashAccount") + ") d on a.fscashacccode = d.fcashacccode"
    				+ " left join (select FCASHACCCODE, FCASHACCNAME from " + pub.yssGetTableName("Tb_Para_CashAccount") + ") e on a.fbcashacccode = e.fcashacccode"
    				+ " where a.FNUM = '" + this.Num + "'";
    		try {
    			rs = dbl.openResultSet(strSql);
    			while (rs.next()) {
    				this.analysisName1 = rs.getString("FSUSERNAME");
    				this.bAnalysisName1 = rs.getString("FCASHACCNAME");
    				this.sCashAccName = rs.getString("FSCASHACCNAME");
    				this.bCashAccName = rs.getString("FBCASHACCNAME");
    			}
    		} catch (Exception e) {
    			throw new YssException(e.getMessage(), e);
    		} finally {
    			dbl.closeResultSetFinal(rs);
    		}
	        if(this.desc.equalsIgnoreCase("")){
	      	  this.desc = "[" + YssFun.formatDate(this.tradeDate) + "]"; //调拨日期
	      	  this.desc += "[" + this.bAnalysisName1 + "]";//投资经理
	      	  this.desc += "换汇";//交易方式
	      	  this.desc += "[OUT:" + this.sCashAccCode + ":" + this.sCashAccName + "]";//流出方
	      	  this.desc += "[IN:" + this.bCashAccCode + ":" + this.bCashAccName + "]";//流入方
	        }
	        transfersetIn.setSDesc(this.desc);
	        //------------------------------------------------------------------------------
          
	        //--- QDV4中保2009年12月30日01_B add by jiangshichao 2009.12.30  
	        rateIn =new EachRateOper();
	        rateIn.setYssPub(pub);
	        rateIn.getInnerPortRate(tradeDate,bCuryCode,bPortCode);
	        transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
          		bCuryCode, bPortCode, YssOperCons.YSS_RATE_BASE));
	        transfersetIn.setDPortRate(rateIn.getDPortRate());
         /* 
          if (this.bCuryCode.equalsIgnoreCase(bPort.getCurrencyCode()) &&
              !this.sCuryCode.equalsIgnoreCase(bPort.getCurrencyCode())) {
             //当买入货币是组合货币时,流入汇率采用买入货币金额进行计算fazmm20071008
             //判断一下，若baseMoney为0时，则直接取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
             if (this.baseMoney == 0.0D) {
                rateIn =new EachRateOper();
                rateIn.setYssPub(pub);
                rateIn.getInnerPortRate(tradeDate,bCuryCode,bPortCode);
                transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
                		bCuryCode, bPortCode, YssOperCons.YSS_RATE_BASE));
                transfersetIn.setDPortRate(rateIn.getDPortRate());
             }
             else {
               // transfersetIn.setDBaseRate(YssD.div(this.baseMoney, this.bMoney));
               // transfersetIn.setDPortRate(YssD.div(this.baseMoney, this.bMoney));
            	 // 流入货币是组合货币：基础货币应该取交易日的基础汇率，组合汇率为1   jiangshichao 2009.12.31 修改
            	 transfersetIn.setDPortRate(1.0D);
             }
          }
          else if (this.sCuryCode.equalsIgnoreCase(bPort.getCurrencyCode()) &&
                   !this.bCuryCode.equalsIgnoreCase(bPort.getCurrencyCode())) {
             //当卖出货币是组合货币时,流入汇率采用卖出货币金额计算fazmm20071008
             //判断，当baseMoney为０时，则直接取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
             if(this.baseMoney ==0.0D){
                rateIn =new EachRateOper();
                rateIn.setYssPub(pub);
                rateIn.getInnerPortRate(tradeDate,bCuryCode,bPortCode);
                transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
                      bCuryCode, bPortCode, YssOperCons.YSS_RATE_BASE));
                transfersetIn.setDPortRate(rateIn.getDPortRate());
             }else{
                //transfersetIn.setDBaseRate(YssD.div(this.baseMoney, this.bMoney));
                transfersetIn.setDPortRate(YssD.div(this.baseMoney, this.sMoney));
                
             }
          }
          else if (!this.sCuryCode.equalsIgnoreCase(bPort.getCurrencyCode()) &&
                   !this.bCuryCode.equalsIgnoreCase(bPort.getCurrencyCode())) {
             //当卖出货币和买入货币都不是组合货币时,采用日终汇率做本位币成本fazmm20071008
             transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
                   bCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
//             transfersetIn.setDPortRate(this.getSettingOper().getCuryRate(tradeDate,
//                   bPort.getCurrencyCode(), portCode, YssOperCons.YSS_RATE_PORT));
             //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
             rateIn =new EachRateOper();
             rateIn.setYssPub(pub);
             rateIn.getInnerPortRate(tradeDate,bPort.getCurrencyCode(),portCode); 
             transfersetIn.setDPortRate(rateIn.getDPortRate());
             //----------------------------------------------------------------------

          }
          */
        
	        //QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
	        if(bCuryCode.equalsIgnoreCase(pub.getPortBaseCury(this.bPortCode))){//如果买入货币与基础货币相同基础汇率就赋值为１// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
	        	transfersetIn.setDBaseRate(1.0D);
	        }
	        //QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
	        if(pub.getPortBaseCury(this.bPortCode).equalsIgnoreCase(bPortCode)){//如果基础货币与买入组合货币相同组合汇率就取１// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
	        	transfersetIn.setDPortRate(1.0D);
	        }
          
	         transfersetIn.setSCashAccCode(this.bCashAccCode);
	         transfersetIn.setIInOut(1);
	         transfersetIn.checkStateId = iCheckState;

       //费用帐户
	          if (this.bCuryFee != 0) {
	             transfersetInFee.setDMoney(this.bCuryFee);
	             transfersetInFee.setSPortCode(this.bPortCode);
	             transfersetInFee.setSAnalysisCode1(this.bAnalysisCode1);
	             transfersetInFee.setSAnalysisCode2(this.bAnalysisCode2);
	             //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
	             transfersetInFee.setStrAttrClsCode(this.strBAttrClsCode);
	             //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
	             transfersetInFee.setDBaseRate(this.getSettingOper().getCuryRate(
	                   tradeDate, bCuryCode, bPortCode, YssOperCons.YSS_RATE_BASE));
	//             transfersetInFee.setDPortRate(this.getSettingOper().getCuryRate(
	//                   tradeDate, bPort.getCurrencyCode(), bPortCode,
	//                   YssOperCons.YSS_RATE_PORT));
	             //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
	             rateIn =new EachRateOper();
	             rateIn.setYssPub(pub);
	             rateIn.getInnerPortRate(tradeDate,bPort.getCurrencyCode(),bPortCode);
	             transfersetInFee.setDPortRate(rateIn.getDPortRate());
	             
	             //----------------------------------------------------------------------
	
	             transfersetInFee.setSCashAccCode(this.bCashAccCode);
	             transfersetInFee.setIInOut( -1);
	             transfersetInFee.checkStateId = iCheckState;
	          }
	       //如果现金流入结算日期跟现金流出结算日期不一致时，需要处理成两条资金调拨数据fazmm20071020
	          if (YssFun.dateDiff(this.settleDate, this.bSettleDate) != 0) {
	             tranSetList.add(transfersetIn);
	             tranSetList.add(transfersetInFee);
	             tranAdmin.addList(tran, tranSetList);
	             tranAdmin.insert(sOldNum, true);
	
	             tranSetList = new ArrayList();
	             tran = new TransferBean();
	             tranAdmin = new CashTransAdmin();
	             tranAdmin.setYssPub(pub);
	
	             //增加资金调拨记录
	             tran.setYssPub(pub);
	             tran.setDtTransDate(this.tradeDate); //存入时间
	             tran.setDtTransferDate(this.settleDate);
	             tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
	             tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_RateTrade);
	             tran.setStrTransferTime("00:00:00");
	//             tran.setDataSource(0);
	             tran.setDataSource(1);//这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
	             tran.setRateTradeNum(sRateTradeNum);
	             tran.checkStateId = iCheckState;
	             tran.creatorTime = YssFun.formatDate(new java.util.Date(),
	                                                  "yyyyMMdd HH:mm:ss");
	//             tran.setDataSource(0); //自动计算标志
	             tran.setDataSource(1);//这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
	          }
	
	       //资金流出帐户
	          transfersetOut.setDMoney(this.sMoney);
	          transfersetOut.setSPortCode(this.portCode);
	          transfersetOut.setSAnalysisCode1(this.analysisCode1);
	          transfersetOut.setSAnalysisCode2(this.analysisCode2);
	          transfersetOut.setSDesc(this.desc);//add by yanghaiming 20090122 MS00885 QDV4中保2009年12月22日01_B 补充需求
	          //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
	          transfersetOut.setStrAttrClsCode(this.strAttrClsCode);
	          //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
	          if (this.baseMoney == 0.0D) {
	              rateOut =new EachRateOper();
	              rateOut.setYssPub(pub);
	              rateOut.getInnerPortRate(tradeDate,sCuryCode,portCode);
	              transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(
	                    tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
	              transfersetOut.setDPortRate(rateOut.getDPortRate());
	           }else{
	              transfersetOut.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
	              transfersetOut.setDPortRate(YssD.div(this.baseMoney,this.portMoney ));//modify by jiangshichao 2010.01.05 该变动只针对中保
	           }
          
       //不保留位数，根据数据表的汇率字段小数位保留fazmm20071001
      /*    if (this.bCuryCode.equalsIgnoreCase(sPort.getCurrencyCode()) &&
              !this.sCuryCode.equalsIgnoreCase(sPort.getCurrencyCode())) {
             //当买入货币是组合货币时,本位币成本采用买入货币金额
             //当baseMoney等于０时，应取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
             if (this.baseMoney == 0.0D) {
                rateOut =new EachRateOper();
                rateOut.setYssPub(pub);
                rateOut.getInnerPortRate(tradeDate,sCuryCode,portCode);
                transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(
                      tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
                transfersetOut.setDPortRate(rateOut.getDPortRate());
             }else{
                transfersetOut.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
                //transfersetOut.setDPortRate(YssD.div(this.baseMoney, this.bMoney));
                transfersetOut.setDPortRate(YssD.div(this.portMoney,this.baseMoney ));//modify by jiangshichao 2010.01.05 该变动只针对中保
             }
          }
          else if (this.sCuryCode.equalsIgnoreCase(sPort.getCurrencyCode()) &&
                   !this.bCuryCode.equalsIgnoreCase(sPort.getCurrencyCode())) {
             //当卖出货币是组合货币时按照卖出货币金额做成本
             //当baseMoney等于０时，取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
             if(this.baseMoney ==0.0D){
                rateOut =new EachRateOper();
                rateOut.setYssPub(pub);
                rateOut.getInnerPortRate(tradeDate,sCuryCode,portCode);
                transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(
                      tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
                transfersetOut.setDPortRate(rateOut.getDPortRate());
             }else{
                transfersetOut.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
                //transfersetOut.setDPortRate(YssD.div(this.baseMoney, this.sMoney));
              //--- QDV4中保2009年12月30日01_B add by jiangshichao 2009.12.30  
                transfersetOut.setDPortRate(YssD.div(this.portMoney,this.baseMoney ));//modify by jiangshichao 2010.01.05 该变动只针对中保
             }
          }
          else if (!this.sCuryCode.equalsIgnoreCase(sPort.getCurrencyCode()) &&
                   !this.bCuryCode.equalsIgnoreCase(sPort.getCurrencyCode())) {
             //当卖出货币和买入货币都不是组合货币时计算汇兑损益,转出账户的本位币成本按照日终汇率处理fazmm20071008
             transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
                   sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
//             transfersetOut.setDPortRate(this.getSettingOper().getCuryRate(tradeDate,
//                   sPort.getCurrencyCode(), portCode, YssOperCons.YSS_RATE_PORT));
             //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
             rateIn =new EachRateOper();
             rateIn.setYssPub(pub);
             rateIn.getInnerPortRate(tradeDate,sCuryCode,portCode);
             transfersetOut.setDPortRate(rateIn.getDPortRate());
             //----------------------------------------------------------------------
          }*/


          //QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
          if(pub.getPortBaseCury(this.portCode).equalsIgnoreCase(sPort.getCurrencyCode())){//如果基础货币与卖出组合货币相同组合汇率就取１// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
             transfersetOut.setDPortRate(1.0D);
          }
          //QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
          if(sCuryCode.equalsIgnoreCase(pub.getPortBaseCury(portCode))){//如果卖出货币与基础货币相同基础汇率就赋值为１// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
        	  transfersetOut.setDBaseRate(1.0D);
          }
       //transfersetOut.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
       //transfersetOut.setDPortRate(YssD.div(this.baseMoney, this.portMoney));

          //流出汇率按照当日汇率
       //      transfersetIn.setDPortRate(this.portMoney / this.baseMoney);
       //      transfersetOut.setDPortRate(YssD.div(this.baseMoney, this.portMoney, 12));

          transfersetOut.setSCashAccCode(this.sCashAccCode);
          transfersetOut.setIInOut( -1);
          transfersetOut.checkStateId = iCheckState;
          //------add by yanghaiming 20090122 MS00888 QDV4中保2009年12月25日01_A  补充需求 
          if(this.desc.equalsIgnoreCase("")){
        	  this.desc = "[" + YssFun.formatDate(this.tradeDate) + "]"; //调拨日期
        	  this.desc += "[" + this.analysisCode1 + "]";//投资经理
        	  this.desc += "换汇";//交易方式
        	  this.desc += "[OUT:" + this.sCashAccCode + ":" + this.sCashAccName + "]";//流出方
        	  this.desc += "[IN:" + this.bCashAccCode + ":" + this.bCashAccName + "]";//流入方
          }
          transfersetOut.setSDesc(this.desc);
          //------------------------------
          if (this.sCuryFee != 0) {
             transfersetOutFee.setDMoney(this.sCuryFee);
             transfersetOutFee.setSPortCode(this.portCode);
             transfersetOutFee.setSAnalysisCode1(this.analysisCode1);
             transfersetOutFee.setSAnalysisCode2(this.analysisCode2);
	         //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
	         transfersetOutFee.setStrAttrClsCode(this.strAttrClsCode);
		     //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
             transfersetOutFee.setDBaseRate(this.getSettingOper().getCuryRate(
                   tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
//             transfersetOutFee.setDPortRate(this.getSettingOper().getCuryRate(
//                   tradeDate, sPort.getCurrencyCode(), portCode,
//                   YssOperCons.YSS_RATE_PORT));
             //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
             rateIn = new EachRateOper();
             rateIn.setYssPub(pub);
             rateIn.getInnerPortRate(tradeDate,sCuryCode,portCode);
             transfersetOutFee.setDPortRate(rateIn.getDPortRate());
             //----------------------------------------------------------------------

             transfersetOutFee.setSCashAccCode(this.sCashAccCode);
             transfersetOutFee.setIInOut( -1);
             transfersetOutFee.checkStateId = iCheckState;
          }

          tranSetList.add(transfersetOut);
          tranSetList.add(transfersetOutFee);
          if (YssFun.dateDiff(this.settleDate, this.bSettleDate) == 0) {
             tranSetList.add(transfersetInFee);
             tranSetList.add(transfersetIn);
             tranAdmin.addList(tran, tranSetList);
             tranAdmin.insert(sOldNum, true);
          }
          else {
             tranAdmin.addList(tran, tranSetList);
             tranAdmin.insert(sOldNum, false);
          }
        }else{

        	if (this.bCuryCode.equalsIgnoreCase(bPort.getCurrencyCode()) &&
        			!this.sCuryCode.equalsIgnoreCase(bPort.getCurrencyCode())) {
        		//当买入货币是组合货币时,流入汇率采用买入货币金额进行计算fazmm20071008
        		//判断一下，若baseMoney为0时，则直接取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416

        	
        		if (this.baseMoney == 0.0D) {
            	
        			rateIn = new EachRateOper();
        			rateIn.setYssPub(pub);
        			rateIn.getInnerPortRate(tradeDate, bCuryCode, bPortCode);
        			transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
        					bCuryCode, bPortCode, YssOperCons.YSS_RATE_BASE));
        			transfersetIn.setDPortRate(rateIn.getDPortRate());
        		} else {
        			transfersetIn.setDBaseRate(YssD.div(this.baseMoney, this.bMoney));
        			transfersetIn.setDPortRate(YssD.div(this.baseMoney, this.bMoney));
        		}
        	} else if (this.sCuryCode.equalsIgnoreCase(bPort.getCurrencyCode()) &&
        			!this.bCuryCode.equalsIgnoreCase(bPort.getCurrencyCode())) {
        		//当卖出货币是组合货币时,流入汇率采用卖出货币金额计算fazmm20071008
        		//判断，当baseMoney为０时，则直接取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
        		//------ modify by wangzuochun 2010.06.08 MS01135 外汇交易中有关人民币账户的资金调拨汇率计算不正确 QDV4华夏2010年4月27日01_AB
        		String strMode = pubpara.getRateTradeMode();
        	
        		if (strMode != null && strMode.length() > 0 && strMode.equals("1")){
        			rateIn = new EachRateOper();
        			rateIn.setYssPub(pub);
        			rateIn.getInnerPortRate(tradeDate, bCuryCode, bPortCode);
        			transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
        					bCuryCode, bPortCode, YssOperCons.YSS_RATE_BASE));
        			transfersetIn.setDPortRate(rateIn.getDPortRate());
        		}
        		else if (strMode != null && strMode.length() > 0 && strMode.equals("0")){
        			transfersetIn.setDBaseRate(YssD.div(this.baseMoney, this.bMoney));
        			transfersetIn.setDPortRate(YssD.div(this.baseMoney, this.bMoney));
        		}
        		else{
        			if (this.baseMoney == 0.0D) {
        				rateIn = new EachRateOper();
        				rateIn.setYssPub(pub);
        				rateIn.getInnerPortRate(tradeDate, bCuryCode, bPortCode);
        				transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
        						bCuryCode, bPortCode, YssOperCons.YSS_RATE_BASE));
        				transfersetIn.setDPortRate(rateIn.getDPortRate());
        			} else {
        				transfersetIn.setDBaseRate(YssD.div(this.baseMoney, this.bMoney));
        				transfersetIn.setDPortRate(YssD.div(this.baseMoney, this.sMoney));
        			}
        		}
        		//-------------------------------MS01135 ----------------------------------//
        	} else if (!this.sCuryCode.equalsIgnoreCase(bPort.getCurrencyCode()) &&
        			!this.bCuryCode.equalsIgnoreCase(bPort.getCurrencyCode())) {
        		//当卖出货币和买入货币都不是组合货币时,采用日终汇率做本位币成本fazmm20071008
        		transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
        				bCuryCode, portCode, YssOperCons.YSS_RATE_BASE));

        		//---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
        		rateIn = new EachRateOper();
	            rateIn.setYssPub(pub);
	            rateIn.getInnerPortRate(tradeDate, bPort.getCurrencyCode(), portCode);
	            transfersetIn.setDPortRate(rateIn.getDPortRate());
            //----------------------------------------------------------------------
        	}
        	
        	
        	/******此处重新计算基础汇率和组合汇率的目的：当卖出货币帐户上的余额为0，计算得到的基础余额basemoney为0，并且买卖一方为组合货币时，
        	 * 生成资金调拨的过程中，在计算基础和组合汇率时拿此0值的余额去除以买卖金额，这样计算出的汇率就会出现为0的情况。
        	 * 但这种情况在上面计算汇率时，已经考虑当若baseMoney为0时，则直接取当日的汇率，所以此处无需重新计算汇率 
        	 * modify by zhangjun 2012.07.12 BUG#4943
        	 * 
        	//QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
        	if (bCuryCode.equalsIgnoreCase(pub.getPortBaseCury(this.bPortCode))) { //如果买入货币与基础货币相同基础汇率就赋值为１// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
        		transfersetIn.setDPortRate(YssD.div(transfersetIn.getDPortRate(), transfersetIn.getDBaseRate())); //xuqiji 20090615:QDV4交银施罗德2009年6月10日01_B  MS00495 外汇交易业务美元应收换汇拆借款本位币成本不正确
        		transfersetIn.setDBaseRate(1.0D);
        	}
        	//QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416  modify by zhangjun 2012.07.12 BUG#4943
        	if (bPort.getCurrencyCode().equalsIgnoreCase(bCuryCode)) { //如果组合货币币种和买入货币币种相同组合汇率就取１ //MS00566 QDV4赢时胜（上海）2009年7月13日01_B sj 将bPortCode转换为bCuryCode，及买入货币
        		transfersetIn.setDBaseRate(YssD.div(transfersetIn.getDBaseRate(),transfersetIn.getDPortRate()));//重新计算基础汇率金额
        		transfersetIn.setDPortRate(1.0D);
        	}
        	*/
        	transfersetIn.setSCashAccCode(this.bCashAccCode);
        	transfersetIn.setIInOut(1);
        	transfersetIn.checkStateId = iCheckState;

        	//费用帐户
        	if (this.bCuryFee != 0) {
	        	transfersetInFee.setDMoney(this.bCuryFee);
	            transfersetInFee.setSPortCode(this.bPortCode);
	            transfersetInFee.setSAnalysisCode1(this.bAnalysisCode1);
	            transfersetInFee.setSAnalysisCode2(this.bAnalysisCode2);
	            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
	            transfersetInFee.setStrAttrClsCode(this.strBAttrClsCode);
		        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
	            transfersetInFee.setDBaseRate(this.getSettingOper().getCuryRate(
	                tradeDate, bCuryCode, bPortCode, YssOperCons.YSS_RATE_BASE));

            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
	            rateIn = new EachRateOper();
	            rateIn.setYssPub(pub);
	            rateIn.getInnerPortRate(tradeDate, bPort.getCurrencyCode(), bPortCode);
	            transfersetInFee.setDPortRate(rateIn.getDPortRate());
	            //----------------------------------------------------------------------
	
	            transfersetInFee.setSCashAccCode(this.bCashAccCode);
	            transfersetInFee.setIInOut( -1);
	            transfersetInFee.checkStateId = iCheckState;
        	}

        	//如果现金流入结算日期跟现金流出结算日期不一致时，需要处理成两条资金调拨数据fazmm20071020
        	if (YssFun.dateDiff(this.settleDate, this.bSettleDate) != 0) {
	            tranSetList.add(transfersetIn);
	            tranSetList.add(transfersetInFee);
	            tranAdmin.addList(tran, tranSetList);
	            
	            /**Start added by liubo.Bug #8882.QDV4中行2013年7月31日01_B
	             * 使用60版本发布的BUG 7683的修改代码可以解决8882的问题.
	             * 另外添加一个逻辑，当oldNum为空字符串，表示当前进行的是新增或者复制的操作，这是不需要进行自动删除的操作*/
	            
	            //--- edit by songjie 2013.05.10 BUG 7683 QDV4建行2013年05月2日01_B start---//
	            //改为根据 外汇交易编号、调拨日期、自动删除标识 删除 资金调拨数据
	            tranAdmin.insert(sOldNum, this.oldNum.trim().equals("") ? false : isDelete, 
	            		tran.getDtTransferDate()); //modify huangqirong 2012-08-14 story #2822
	            //--- edit by songjie 2013.05.10 BUG 7683 QDV4建行2013年05月2日01_B end---//
	            
	            /**End added by liubo.Bug #8882.QDV4中行2013年7月31日01_B*/
	            
	            tranSetList = new ArrayList();
	            tran = new TransferBean();
	            tranAdmin = new CashTransAdmin();
	            tranAdmin.setYssPub(pub);
	
	            //增加资金调拨记录
	            tran.setYssPub(pub);
	            tran.setDtTransDate(this.tradeDate); //存入时间
	            tran.setDtTransferDate(this.settleDate);
	            tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
	            tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_RateTrade);
	            tran.setStrTransferTime("00:00:00");
	            tran.setDataSource(1); //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
	            tran.setRateTradeNum(sRateTradeNum);
	            tran.checkStateId = iCheckState;
	            tran.creatorTime = YssFun.formatDate(new java.util.Date(),
	                                                 "yyyyMMdd HH:mm:ss");
	            tran.setDataSource(1); //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
        	}

	        //资金流出帐户
	        transfersetOut.setDMoney(isHashBail ? YssD.sub(this.sMoney, bailMoney) : this.sMoney); //modify by huangqirong 2012-08-16 story #2822 外汇交易保证金
	        transfersetOut.setSPortCode(this.portCode);
	        transfersetOut.setSAnalysisCode1(this.analysisCode1);
	        transfersetOut.setSAnalysisCode2(this.analysisCode2);
	       //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
	        transfersetOut.setStrAttrClsCode(this.strAttrClsCode);
	        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
	        //不保留位数，根据数据表的汇率字段小数位保留fazmm20071001
	
	        if (this.bCuryCode.equalsIgnoreCase(sPort.getCurrencyCode()) &&
	            !this.sCuryCode.equalsIgnoreCase(sPort.getCurrencyCode())) {
	            //当买入货币是组合货币时,本位币成本采用买入货币金额
	            //当baseMoney等于０时，应取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
	            if (this.baseMoney == 0.0D) {
	                rateOut = new EachRateOper();
	                rateOut.setYssPub(pub);
	                rateOut.getInnerPortRate(tradeDate, sCuryCode, portCode);
	                transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(
	                    tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
	                transfersetOut.setDPortRate(rateOut.getDPortRate());
	            } else {
	                transfersetOut.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
	                transfersetOut.setDPortRate(YssD.div(this.baseMoney, this.bMoney));
	            }
	        } else if (this.sCuryCode.equalsIgnoreCase(sPort.getCurrencyCode()) &&
	                   !this.bCuryCode.equalsIgnoreCase(sPort.getCurrencyCode())) {
	            //当卖出货币是组合货币时按照卖出货币金额做成本
	            //当baseMoney等于０时，取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
	        	
	        	//------ modify by wangzuochun 2010.06.08 MS01135 外汇交易中有关人民币账户的资金调拨汇率计算不正确 QDV4华夏2010年4月27日01_AB
	        	String strMode = pubpara.getRateTradeMode();
	        	
	        	if (strMode != null && strMode.length() > 0 && strMode.equals("1")){
	        		rateOut = new EachRateOper();
	    			rateOut.setYssPub(pub);
	    			rateOut.getInnerPortRate(tradeDate, sCuryCode, portCode);
	    			transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(
	    					tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
	    			transfersetOut.setDPortRate(rateOut.getDPortRate());
	        	}
	        	else if (strMode != null && strMode.length() > 0 && strMode.equals("0")){
	        		transfersetOut.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
	    			transfersetOut.setDPortRate(YssD.div(this.baseMoney, this.sMoney));
	        	}
	        	else{
	        		
	        		if (this.baseMoney == 0.0D) {
	        			rateOut = new EachRateOper();
	        			rateOut.setYssPub(pub);
	        			rateOut.getInnerPortRate(tradeDate, sCuryCode, portCode);
	        			transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(
	        					tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
	        			transfersetOut.setDPortRate(rateOut.getDPortRate());
	        		} else {
	        			transfersetOut.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
	        			transfersetOut.setDPortRate(YssD.div(this.baseMoney, this.sMoney));
	        		}
	        	}
        	//---------------------------------MS01135-----------------------------------//
	        } else if (!this.sCuryCode.equalsIgnoreCase(sPort.getCurrencyCode()) &&
	                   !this.bCuryCode.equalsIgnoreCase(sPort.getCurrencyCode())) {
	            //当卖出货币和买入货币都不是组合货币时计算汇兑损益,转出账户的本位币成本按照日终汇率处理fazmm20071008
	            transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(tradeDate,
	                sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
	
	            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
	            rateIn = new EachRateOper();
	            rateIn.setYssPub(pub);
	            rateIn.getInnerPortRate(tradeDate, sCuryCode, portCode);
	            transfersetOut.setDPortRate(rateIn.getDPortRate());
	            //----------------------------------------------------------------------
	        }
	
	        transfersetOut.setSCashAccCode(this.sCashAccCode);
	        transfersetOut.setIInOut( -1);
	        transfersetOut.checkStateId = iCheckState;
	        
	        //add by fangjiang 2011.07.08 STORY #1280
	        String strMode1 = pubpara.getRateTradeMode1();
	        if (strMode1 != null && strMode1.length() > 0 && strMode1.equals("0")){
	        	if (this.baseMoney == 0.0D) {
        			rateOut = new EachRateOper();
        			rateOut.setYssPub(pub);
        			rateOut.getInnerPortRate(tradeDate, sCuryCode, portCode);
        			transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(
        					tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
        			transfersetOut.setDPortRate(rateOut.getDPortRate());
        		} else {
        			transfersetOut.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
        			transfersetOut.setDPortRate(YssD.div(this.baseMoney, this.portMoney));
        		}
        	}
	        String strMode2 = pubpara.getRateTradeMode2();
	        if(this.sCuryCode.equalsIgnoreCase(this.bCuryCode)){
	        	if (strMode2 != null && strMode2.length() > 0 && strMode2.equals("0")){
	    			transfersetIn.setDBaseRate(transfersetOut.getDBaseRate());
	    			transfersetIn.setDPortRate(transfersetOut.getDPortRate());
	        	}
	        }       	
	        //----------------------
	
	        if (this.sCuryFee != 0) {
	            transfersetOutFee.setDMoney(this.sCuryFee);
	            transfersetOutFee.setSPortCode(this.portCode);
	            transfersetOutFee.setSAnalysisCode1(this.analysisCode1);
	            transfersetOutFee.setSAnalysisCode2(this.analysisCode2);
	            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
	            transfersetOutFee.setStrAttrClsCode(this.strAttrClsCode);
		        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
	            transfersetOutFee.setDBaseRate(this.getSettingOper().getCuryRate(
	                tradeDate, sCuryCode, portCode, YssOperCons.YSS_RATE_BASE));
	
	            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
	            rateIn = new EachRateOper();
	            rateIn.setYssPub(pub);
	            rateIn.getInnerPortRate(tradeDate, sCuryCode, portCode);
	            transfersetOutFee.setDPortRate(rateIn.getDPortRate());
	            //----------------------------------------------------------------------
	
	            transfersetOutFee.setSCashAccCode(this.sCashAccCode);
	            transfersetOutFee.setIInOut( -1);
	            transfersetOutFee.checkStateId = iCheckState;
	        }
	        tranSetList.add(transfersetOut);
	        tranSetList.add(transfersetOutFee);
	        if (YssFun.dateDiff(this.settleDate, this.bSettleDate) == 0) {
	            tranSetList.add(transfersetInFee);
	            tranSetList.add(transfersetIn);
	            tranAdmin.addList(tran, tranSetList);
	            tranAdmin.insert(sOldNum, isDelete);	//modify huangqirong 2012-08-14 story #2822 
	        } else {
	            tranAdmin.addList(tran, tranSetList);

	            /**Start added by liubo.Bug #8882.QDV4中行2013年7月31日01_B
	             * 使用60版本发布的BUG 7683的修改代码可以解决8882的问题
	             * 另外添加一个逻辑，当oldNum为空字符串，表示当前进行的是新增或者复制的操作，这是不需要进行自动删除的操作*/
	            //--- edit by songjie 2013.05.10 BUG 7683 QDV4建行2013年05月2日01_B start---//
	            //改为根据 外汇交易编号、调拨日期、自动删除标识 删除 资金调拨数据
	            tranAdmin.insert(sOldNum, this.oldNum.trim().equals("") ? false : isDelete, 
	            		tran.getDtTransferDate());
	            //--- edit by songjie 2013.05.10 BUG 7683 QDV4建行2013年05月2日01_B end---//
	            /**End added by liubo.Bug #8882.QDV4中行2013年7月31日01_B*/
	        }
        }//合并太平版本代码
    }

    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String strNumberDate = "";
        try {
            strNumberDate = YssFun.formatDate(this.settleDate,
                                              YssCons.YSS_DATETIMEFORMAT).
                substring(0, 8);

            this.Num = "T" + strNumberDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_RateTrade"),
                                       dbl.sqlRight("FNum", 6), "000001",
                                       " where FNum like 'T"
                                       + strNumberDate + "%'", 1);
            
            strSql =
            	//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 添加FATTRCLSCODE、FBATTRCLSCODE
                "insert into " + pub.yssGetTableName("Tb_Data_RateTrade") + "(FNum, FTradeDate,FTradeTime,FPortCode,FBPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FBAnalysisCode1,FBAnalysisCode2,FBAnalysisCode3, FBCashAccCode,FSCashAccCode,FReceiverCode,FPayCode," + //新增收款人,付款人字段 by ly 080220
                " FSettleTime, FSettleDate,FBSettleTime, FBSettleDate,FTradeType,FCatType,FExCuryRate,FLongCuryRate,FBMoney,FSMoney,FBaseMoney,FPortMoney,FRateFx,FUpDown,FDesc,FBCuryCode,FSCuryCode,FBCuryFee,FSCuryFee,FATTRCLSCODE,FBATTRCLSCODE," +
                //edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A
                " FDataSource,FCheckState, FCreator, FCreateTime,FCheckUser,FRateTradeType ,FBAILTYPE,FBAILSCALE ,FBAILFIX, FBAILCashCode,FRateReason) " + //增加数据来源字段 BugNo:0000462 edit by jc //modify by  huangqirong 2012-08-16 story #2822 外汇交易保证金
                " values(" + //MS01409 去掉字段FRateTradeType by fangjiang 2010.07.23
                //  新增字段 FRateReason add by zhaoxianlin 20121218 STORY #3383 外管局报表
                dbl.sqlString(this.Num) + "," +
                dbl.sqlDate(this.tradeDate) + "," +
                dbl.sqlString(this.tradeTime) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.bPortCode) + "," +
                dbl.sqlString(this.analysisCode1.length() == 0 ? " " :
                              this.analysisCode1) + "," +
                dbl.sqlString(this.analysisCode2.length() == 0 ? " " :
                              this.analysisCode2) + "," +
                dbl.sqlString(this.analysisCode3.length() == 0 ? " " :
                              this.analysisCode3) + "," +
                dbl.sqlString(this.bAnalysisCode1.length() == 0 ? " " :
                              this.bAnalysisCode1) + "," +
                dbl.sqlString(this.bAnalysisCode2.length() == 0 ? " " :
                              this.bAnalysisCode2) + "," +
                dbl.sqlString(this.bAnalysisCode3.length() == 0 ? " " :
                              this.bAnalysisCode3) + "," +

                dbl.sqlString(this.bCashAccCode) + "," +
                dbl.sqlString(this.sCashAccCode) + "," +
                dbl.sqlString(this.sReceiverCode) + "," +
                dbl.sqlString(this.sPayCode) + "," +
                dbl.sqlString(this.settleTime) + "," +
                dbl.sqlDate(this.settleDate) + "," +
                dbl.sqlString(this.bSettleTime) + "," + //ALTER  BY  SUNNY  10 19
                dbl.sqlDate(this.bSettleDate) + "," +
                0 + "," +
                0 + "," +
                //dbl.sqlString(this.sRateTradeType)+","+// MS01137 QDV4中金2010年04月29日02_A  add by jiangshichao 2010.05.05
              //MS01409 QDV4赢时胜(30上线测试)2010年7月6日07_AB modify by fangjiang 2010.07.23
                (this.exCuryRate) + "," +
                (this.lingCuryRate) + "," +
                (this.bMoney) + "," +
                (this.sMoney) + "," +
                (this.baseMoney) + "," +
                (this.portMoney) + "," +
                (this.rateFx) + "," +
                (this.upDown) + "," +
                dbl.sqlString(this.desc) + "," +
                dbl.sqlString(this.bCuryCode) + "," +
                dbl.sqlString(this.sCuryCode) + "," +
                (this.bCuryFee) + "," +
                (this.sCuryFee) + "," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                (this.strAttrClsCode.trim().length()==0?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode))+","+
                (this.strBAttrClsCode.trim().length()==0?dbl.sqlString(" "):dbl.sqlString(this.strBAttrClsCode))+","+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                dbl.sqlString(this.sDateSource) + "," + //BugNo:0000462 edit by jc
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) +
                ", " + dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ", " + dbl.sqlString(this.sRateTradeType) +//edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A
                //add by  huangqirong 2012-08-16 story #2822 外汇交易保证金
                "," + dbl.sqlString(this.bailType) + 
                "," + this.bailScale +
                "," + this.bailFix +
                "," + dbl.sqlString(this.bailCashCode) +
                //---end---
                "," + dbl.sqlString(this.RateReason) +//add by zhaoxianlin 20121218 STORY #3383 外管局报表 
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            createSavCashTrans(this.Num, this.oldNum);
            if (sPayCode.trim().length() != 0 && sReceiverCode.trim().length() != 0) {
                //若收款人与付款人代码都有时产生划款指令
                CommandBean comm = new CommandBean();
                comm.setYssPub(pub);
                comm.setSRelaNum(this.Num);
                comm.setSRelaType("RateTrade");
                comm.setPortCode(this.portCode); //------ 没有设置组合代码；add by wangzuochun 2010.08.11  MS01575    外汇交易生成的划款指令无法浏览和操作    QDV4赢时胜2010年8月9日01_B     
                comm.setAccountDate(YssFun.formatDate(this.settleDate, "yyyy-MM-dd"));
                comm.setAccountTime(this.settleTime);
                comm.setOrder("0");
                comm.setSCommandDate(YssFun.formatDate(this.tradeDate, "yyyy-MM-dd"));
                comm.setSCommandTime(this.tradeTime);
                comm.setPayName(this.sPayCode); //由于Command Bean中没有 sPayCode了,故用这个传值 下同 by ly
                comm.setPayCuryCode(this.sCuryCode);
                if (this.exCuryRate == 0) {
                    this.exCuryRate = 1.0;
                }
                comm.setReMoney(this.bMoney);   //买入金额
                comm.setDRate(this.exCuryRate);
                comm.setReceiverName(this.sReceiverCode); // by ly
                comm.setReCuryCode(this.bCuryCode);
                //edit by licai 20111115 BUG #346 换汇业务中计算出的汇兑损益除不进时，划款数据不正确== 
//                comm.setPayMoney(YssD.mul(this.bMoney, this.exCuryRate)); //金额更改为 卖出金额=买入金额*兑换汇率
                comm.setPayMoney(this.sMoney); 
              //edit by licai 20111115 BUG #346 换汇业务中计算出的汇兑损益除不进时，划款数据不正确===end= 
                this.addCashCommon(comm); //产生划款指令
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增换汇交易数据设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * buildRowStr
     * 获取数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.Num).append("\t");
        buf.append(YssFun.formatDate(this.tradeDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.tradeTime).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.analysisCode1).append("\t");
        buf.append(this.analysisName1).append("\t");
        buf.append(this.analysisCode2).append("\t");
        buf.append(this.analysisName2).append("\t");
        buf.append(this.analysisCode3).append("\t");
        buf.append(this.analysisName3).append("\t");
        buf.append(this.bCashAccCode).append("\t");
        buf.append(this.bCashAccName).append("\t");
        buf.append(this.sCashAccCode).append("\t");
        buf.append(this.sCashAccName).append("\t");
        buf.append(this.settleTime).append("\t");
        buf.append(YssFun.formatDate(this.settleDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.tradeType + "").append("\t");
        buf.append(this.tradeTypeName).append("\t");
        buf.append(this.catType).append("\t");
        buf.append(this.catTypeName).append("\t");
        buf.append(this.exCuryRate).append("\t");
        buf.append(this.lingCuryRate).append("\t");
        buf.append(this.bMoney).append("\t");
        buf.append(this.sMoney).append("\t");
        buf.append(this.baseMoney).append("\t");
        buf.append(this.portMoney).append("\t");
        buf.append(YssFun.formatNumber(this.rateFx, "#,##0.####")).append("\t");
        buf.append(this.upDown).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.bCuryCode).append("\t");
        buf.append(this.sCuryCode).append("\t");
        buf.append(this.bCuryName).append("\t");
        buf.append(this.sCuryName).append("\t");
        buf.append(this.bCuryFee).append("\t");
        buf.append(this.sCuryFee).append("\t");

        buf.append(this.bPortCode).append("\t");
        buf.append(this.bPortName).append("\t");
        buf.append(this.bAnalysisCode1).append("\t");
        buf.append(this.bAnalysisName1).append("\t");
        buf.append(this.bAnalysisCode2).append("\t");
        buf.append(this.bAnalysisName2).append("\t");
        buf.append(this.bAnalysisCode3).append("\t");
        buf.append(this.bAnalysisName3).append("\t");
        buf.append(YssFun.formatDate(this.bSettleDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.bSettleTime).append("\t");
        buf.append(this.sPayCode).append("\t");
        buf.append(this.sPayName).append("\t");
        buf.append(this.sReceiverCode).append("\t");
        buf.append(this.sReceiverName).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        buf.append(this.strBAttrClsCode).append("\t");
        buf.append(this.strBAttrClsName).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        buf.append(this.sRateTradeType).append("\t");//edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A
        buf.append(this.bailType).append("\t");		//add by huangqirong 2012-08-14 story #2822 保证金类型
        buf.append(this.bailScale).append("\t");	//add by huangqirong 2012-08-14 story #2822 保证金比例
        buf.append(this.bailFix).append("\t");		//add by huangqirong 2012-08-14 story #2822 固定保证金
        buf.append(this.bailCashCode).append("\t"); //add by huangqirong 2012-08-14 story #2822 固定保证金
        buf.append(this.bailCashName).append("\t");//add by huangqirong 2012-08-14 story #2822 固定保证金
        buf.append(this.RateReason).append("\t");//add by zhaoxianlin 20121218 STORY #3383 外管局报表
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 验证数据
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_RateTrade"),
                               "FNum", this.Num, this.oldNum);
    }

    /**
     * 修改时间：2008年3月28号
     * 修改人：单亮
     * 原方法功能：只能处理外汇交易的审核和未审核的单条信息。
     * 新方法功能：可以处理外汇交易审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理外汇交易审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_Data_RateTrade") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum = " + dbl.sqlString(this.Num);
                    conn.setAutoCommit(false);
                    bTrans = true;
                    dbl.executeSql(strSql);

                    tranAdmin = new CashTransAdmin();
                    tranAdmin.setYssPub(pub);
                    sTranNum = tranAdmin.getTransNums("", "", "", this.Num, "", "");
                    if (!sTranNum.equals("")) {
                        sTranNum = sTranNum.substring(0, sTranNum.length() - 1);
                        tranAdmin.check(this.checkStateId, sTranNum);
                    }
                    strSql = "update " + pub.yssGetTableName("Tb_Cash_Command") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FRelaNum=" + dbl.sqlString(this.Num) +
                        " and FNumType='RateTrade'";
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而Num不为空，则按照Num来执行sql语句
            else if (Num!=null&&(!Num.equalsIgnoreCase(""))) {
                strSql = "update " + pub.yssGetTableName("Tb_Data_RateTrade") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FNum = " + dbl.sqlString(this.Num);
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);

                tranAdmin = new CashTransAdmin();
                tranAdmin.setYssPub(pub);
                sTranNum = tranAdmin.getTransNums("", "", "", this.Num, "", "");
                if (!sTranNum.equals("")) {
                    sTranNum = sTranNum.substring(0, sTranNum.length() - 1);
                    tranAdmin.check(this.checkStateId, sTranNum);
                }
                strSql = "update " + pub.yssGetTableName("Tb_Cash_Command") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FRelaNum=" + dbl.sqlString(this.Num) +
                    " and FNumType='RateTrade'";
                dbl.executeSql(strSql);

            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核货币兑换、账户划拨信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 删除数据，即将数据放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        TransferBean transfer = null; //资金调拨 BugID:MS00167 QDV4赢时胜上海2009年1月7日01_B
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_RateTrade") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum = " + dbl.sqlString(this.Num);
            bTrans = true;
            dbl.executeSql(strSql);

            //对应的删除资金调拨的信息
            tranAdmin = new CashTransAdmin();
            tranAdmin.setYssPub(pub);

//         tranAdmin.delete("", "", this.Num);//BugID:MS00167 QDV4赢时胜上海2009年1月7日01_B sj modified 20090122 不能直接物理删除

            //更新划款指令
            //-----删除资金调拨时,只是逻辑删除.以便在还原时可以找回数据.sj modified 20090122 BugID:MS00167 QDV4赢时胜上海2009年1月7日01_B
            transfer = new TransferBean();
            sTranNum = tranAdmin.getTransNums("", "", "", this.Num, "", ""); //获取资金调拨编号
            if (sTranNum.endsWith(",")) { //getTransNums获取的是带，的数据。需要将其删除。
                sTranNum = sTranNum.substring(0, sTranNum.length() - 1);
                sTranNum = sTranNum.replaceAll("'", "");
            }
            transfer.setStrNum(sTranNum); //向资金调拨编号赋值.
            transfer.checkStateId = this.checkStateId; //向资金调拨状态号赋值.
            transfer.setYssPub(pub);
            transfer.delSetting(); //逻辑删除资金调拨

            strSql = "update " + pub.yssGetTableName("Tb_Cash_Command") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FRelaNum=" + dbl.sqlString(this.Num) +
                " and FNumType='RateTrade'";
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除汇率行情设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editOperData
     * 修改汇率行情设置
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_RateTrade") +
                " where FNum = " +
                dbl.sqlString(this.oldNum) +
                " and FPortCode=" +
                dbl.sqlString(this.oldPortCode.length() == 0 ? " " :
                              this.oldPortCode) +
                " and FTradeDate=" + dbl.sqlDate(this.oldTradeDate) +
                " and FTradeTime=" + dbl.sqlString(this.oldTradeTime) +
                " and FBCashAccCode=" + dbl.sqlString(this.oldBCashAccCode) +
                " and FSCashAccCode=" + dbl.sqlString(this.oldSCashAccCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            strSql = " delete from " + pub.yssGetTableName("Tb_Cash_command") +
                " where FRelaNum=" + dbl.sqlString(this.oldNum) +
                " and FNumType='RateTrade'";
            dbl.executeSql(strSql); //删除划款指令中的数据
            addSetting();

            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改汇率行情设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.isOnlyColumns.equals("1")) {
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);

                sVocStr = vocabulary.getVoc(YssCons.YSS_TA_TradeType + "," +
                                            YssCons.YSS_TA_CatType+","+YssCons.YSS_RATE_BFJType+","+YssCons.YSS_RATE_REASON);//modify by huangqirong 2012-08-14 story #2822 保证金类型 YssCons.YSS_RATE_BFJType
                                                                                                                            //modified by zhaoxianlin 20121226 STORY #3383 换汇原因  YssCons.YSS_RATE_REASON
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr;//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            if (strSql.length() != 0) {
                //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
                //rs = dbl.openResultSet(strSql);
                yssPageInationBean.setsQuerySQL(strSql);
                yssPageInationBean.setsTableName("RateTrade");
                rs =dbl.openResultSet(yssPageInationBean);
                //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
                while (rs.next()) {
                	//---add by songjie 2011.07.20 需求 1282 QDV4博时基金2011年6月29日01_A start---//
                	bufShow.append(rs.getDate("FTradeDate") == null ? "" : YssFun.formatDate(rs.getDate("FTradeDate"))).append("\t");
                	bufShow.append(rs.getString("FTradeTime") + "").append("\t");
                	bufShow.append(rs.getString("FPortCode") + "").append("\t");
                	bufShow.append(rs.getString("FPortName") + "").append("\t");
                	bufShow.append(rs.getString("FBCashAccCode") + "").append("\t");
                	bufShow.append(rs.getString("FBCashAccName") + "").append("\t");
                	bufShow.append(rs.getString("FSCashAccCode") + "").append("\t");
                	bufShow.append(rs.getString("FSCashAccName") + "").append("\t");
                	bufShow.append(rs.getString("FSettleTime") + "").append("\t");
                	bufShow.append(rs.getDate("FSettleDate") == null ? "" : YssFun.formatDate(rs.getDate("FSettleDate"))).append("\t");
                	bufShow.append(rs.getString("FAttrClsCode") + "").append("\t");
                	bufShow.append(rs.getString("FAttrClsName") + "").append("\t");
                	bufShow.append(rs.getString("FBSettleTime") + "").append("\t");
                	bufShow.append(rs.getDate("FBSettleDate") == null ? "" : YssFun.formatDate(rs.getDate("FBSettleDate"))).append("\t");
                	bufShow.append(rs.getString("FBAttrClsCode") + "").append("\t");
                	bufShow.append(rs.getString("FBAttrClsName") + "").append("\t");
                	//delete by zhaoxianlin 20120912 Bug #5558
//                	bufShow.append(rs.getDouble("FTradeType")).append("\t");
//                	bufShow.append(rs.getDouble("FCatType")).append("\t");
                	bufShow.append(rs.getDouble("FExCuryRate")).append("\t");
                	bufShow.append(rs.getDouble("FLongCuryRate")).append("\t");
                	bufShow.append(rs.getDouble("FBMoney")).append("\t");
                	bufShow.append(rs.getDouble("FSMoney")).append("\t");
                	bufShow.append(rs.getDouble("FUpDown")).append("\t");
                	bufShow.append(rs.getString("FBCuryCode") + "").append("\t");
                	bufShow.append(rs.getString("FSCuryCode") + "").append("\t");
                	bufShow.append(rs.getString("FrateReasonName") + "").append("\t"); //add by zhaoxianlin 20121218 STORY #3383 外管局报表  换汇原因
                	//delete by zhaoxianlin 20120912 Bug #5558
//                	bufShow.append(rs.getString("FAnalysisCode1") + "").append("\t");
//                	bufShow.append(rs.getString("FAnalysisCode2") + "").append("\t");
                	if((rs.getString("FRateTradeType") + "").equals("") ||
                	   (rs.getString("FRateTradeType") + "").equals(" ") ||
                	   (rs.getString("FRateTradeType") + "").equals("1")){
                		bufShow.append(" ");
                	}else if((rs.getString("FRateTradeType") + "").equals("2")){
                		bufShow.append("外汇交易业务");
                	}else if((rs.getString("FRateTradeType") + "").equals("3")){
                		bufShow.append("换汇业务");
                	}else{
                		bufShow.append(" ");
                	}
                	bufShow.append(YssCons.YSS_LINESPLITMARK);
                	//---add by songjie 2011.07.20 需求 1282 QDV4博时基金2011年6月29日01_A end---//
                	//---delete by songjie 2011.07.20 需求 1282 QDV4博时基金2011年6月29日01_A start---//
//                    bufShow.append(super.buildRowShowStr(rs,
//                        this.getListView1ShowCols())).
//                        append(YssCons.YSS_LINESPLITMARK);
                	//---delete by songjie 2011.07.20 需求 1282 QDV4博时基金2011年6月29日01_A end---//
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_TA_TradeType + "," +
                                        YssCons.YSS_TA_CatType);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr;//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji

        } catch (Exception e) {
            throw new YssException("获取外汇交易数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    /**
     * 获得分析代码
     * @return String
     * @throws YssException
     * @throws SQLException
     */
    private String getCashStorageAnalysisSql() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_Cash);
        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                        " left join (select y.FBrokerCode as FBBrokerCode ,y.FBrokerName  as FBAnalysisName" +
                        i +
                        " from  (select FBrokerCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_broker") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FBrokerCode )x " +
                        " join (select * from " +
                        pub.yssGetTableName("tb_para_broker") + ") y on x.FBrokerCode = y.FBrokerCode and x.FStartDate = y.FStartDate) broker2 on a.FBAnalysisCode" +
                        i + " = broker2.FBBrokerCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("003")) {
                    sResult = sResult +
                        " left join (select FExchangeCode as FBExchangeCode,FExchangeName as FBAnalysisName" +
                        i +
                        " from tb_base_exchange) e on a.FBAnalysisCode" + i +
                        //edited by zhouxiang MS01504    库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错    
                        " = e.FBExchangeCode " +
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        " join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) exchange2 on a.FBAnalysisCode" +
                        //edited by zhouxiang MS01504    库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错    
                        i + " = exchange2.FInvMgrCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("004")) {
                    sResult = sResult +
                    	//edited by zhouxiang MS01504    库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错    
                        " left join (select FCatCode as FBCatCode,FCatName as FBAnalysisName"+i+
                        " from Tb_Base_Category where FCheckState = 1) category2 on a.FBAnalysisCode" +
                        i + " = category2.FBCatCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
       
                    " left join (select n.FInvMgrCode as FBInvMgrCode,n.FInvMgrName as FBAnalysisName" +
                    i +
                    "  from  " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where  n.FCheckState = 1 ) invmgr2 on a.FBAnalysisCode" +
                    i + " = invmgr2.FBInvMgrCode ";
                    
                    //end by lidaolong
                }
                else {
                    sResult = sResult +
                        " left join (select ' ' as FBAnalysisNull , ' ' as FBAnalysisName" +//分页需求,这里采用空格，合并太平版本调整  by leeyu 20100907
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FBAnalysisCode" + i + " = tn" +
                        i + ".FBAnalysisNull ";
                }
            }
        }
        dbl.closeResultSetFinal(rs); //QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-07-09 关闭结果集
        return sResult;
    }
    
//    private String tmpSec = ""; 
    /**
     * 此方法已被修改
     * 修改时间：2008年2月23号
     * 修改人：单亮
     * 原方法的功能：查询出费用连接数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
//    	String sHeader = "";
//        String sShowDataStr = "";
//        String sAllDataStr = "";
        String strSql = "";
//        String sVocStr = "";
//        StringBuffer bufShow = new StringBuffer();
//        StringBuffer bufAll = new StringBuffer();
//        String sAry[] = null;
//        String sAry2[] = null;
        try {
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
//        	sHeader = this.getListView1Headers();//根据代码暂未改正   将注释代码 还原 即可用 ysh 20111025
			RightBean right = new RightBean();
			right.setYssPub(pub);
			
//			if (this.filterType != null && this.filterType.strisOnlyColumnss.equals("1")&&!(pub.isBrown())) {
//            	VocabularyBean vocabulary = new VocabularyBean();
//                vocabulary.setYssPub(pub);
//                sVocStr = vocabulary.getVoc(YssCons.YSS_TRD_AUTO);
//                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
//                    "\r\f" +
//                    this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr + //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
//                    (tmpSec.length() == 0 ? "" : "\r\f" + tmpSec);
//            }
			
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
//begin by zhouxiang MS01504    库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错    
//            analy1 = operSql.storageAnalysis("FAnalysisCode1",
//                                             YssOperCons.YSS_KCLX_Cash); //判断分析代码存不存在
//            analy2 = operSql.storageAnalysis("FAnalysisCode2",
//                                             YssOperCons.YSS_KCLX_Cash);
//            analy3 = operSql.storageAnalysis("FAnalysisCode3",
//                                             YssOperCons.YSS_KCLX_Cash);
//            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Cash); //获得分析代码
//end   by zhouxiang MS01504    库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错    

            //if (!this.strisOnlyColumnss.trim().equals("0")) {//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji 这里调用基类的方法处理加载后的数据
                strSql =
                    "select  /*distinct*/ y.* from " +//BUG2565 QDV4招商基金2011年8月26日01_B distinct在此处无实际意思，因此去掉以优化sql并确保在oracle 11g下能正常执行。panjunfang modify 20111011
                    "(select FNum,FTradeDate,FTradeTime,FPortCode,FBCashAccCode,FSCashAccCode from " +
                    pub.yssGetTableName("Tb_Data_RateTrade") + " " +
                    "  group by FNum,FTradeDate,FTradeTime,FPortCode,FBCashAccCode,FSCashAccCode) x join" +
                    " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,lp.FReceiverName as FPayName,lr.FReceiverName as FReceiverName, " +
                    " d.FVocName as FTradeTypeName,i.FVocName as FCatTypeName,r.Fvocname as FRateReasonName,f.FCashAccName as FBCashAccName,g.FCashAccName as FSCashAccName," +
                    " ab.fcashaccname as FBailCashName," + //add by huangqirong 2012-08-14 story #2822   //add by zhaoxianlin 20121218 STORY #3383 FRateReasonName
                    " h.FCuryName as FBCuryName,j.FCuryName as FSCuryName,e.FPortName as FPortName" +
                    
                    (storageAnalysis().length() == 0 ?
                            ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                            ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                    (getCashStorageAnalysisSql().length() == 0 ?
                                    ", ' ' as FBAnalysisName1, ' ' as FBAnalysisName2, ' ' as FBAnalysisName3 " :
                                    ", FBAnalysisName1, FBAnalysisName2, FBAnalysisName3 ") +
//begin by zhouxiang MS01504    库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错                    
//                    sAry[0] +
//                    (analy1 ? ", FBAnalysisName1" : "") +
//                    (analy2 ? ",FBAnalysisName2" : "") +
//                    (analy3 ? ",FBAnalysisName3" : "") +
//end   by zhouxiang MS01504    库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错                     
                    ",l.FPortName as FBPortName,nvl(k.FAttrClsName,' ') as FAttrClsName ,nvl(m.FBAttrClsName,' ') as FBAttrClsName " +
                    " from " +
                    pub.yssGetTableName("Tb_Data_RateTrade") + " a" +                  
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList ) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList ) c on a.FCheckUser = c.FUserCode" +
                    " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FTradeType") + " = d.FVocCode and d.FVocTypeCode = " + //lzp  modify 2007 12.7
                    dbl.sqlString(YssCons.YSS_TA_TradeType) +
                    " left join Tb_Fun_Vocabulary i on " + dbl.sqlToChar("a.FCatType") + " = i.FVocCode and i.FVocTypeCode = " + //lzp  modify 2007 12.7
                    dbl.sqlString(YssCons.YSS_TA_CatType) +
                    //----add by zhaoxianlin 20121218 STORY #3383 外管局报表  换汇原因
                    " left join Tb_Fun_Vocabulary r on " + dbl.sqlToChar("a.FRateReason") + " = r.FVocCode and r.FVocTypeCode = " + //lzp  modify 2007 12.7
                    dbl.sqlString(YssCons.YSS_RATE_REASON) +
                    //---end----
                    " left join (select FPortCode,FPortName from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +

                    " where FCheckState = 1 " + //MS00171 QDV4赢时胜上海2009年1月7日05_B sj modified 20090122 增加对状态的筛选，避免获取重复的数据。

                    " ) e on a.FPortCode = e.FPortCode" +

                    " left join (select FPortCode,FPortName from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +

                    " where FCheckState = 1 " + //MS00171 QDV4赢时胜上海2009年1月7日05_B sj modified 20090122 增加对状态的筛选，避免获取重复的数据。
                    ") l on a.FBPortCode = l.FPortCode" +

                    " left join (select FReceiverCode,FReceiverName from " + pub.yssGetTableName("Tb_Para_Receiver") +

                    " where FCheckState = 1 " + //MS00171 QDV4赢时胜上海2009年1月7日05_B sj modified 20090122 增加对状态的筛选，避免获取重复的数据。

                    ") lr " +
                    " on a.FReceiverCode = lr.FReceiverCode " +
                    " left join (select FReceiverCode,FReceiverName from " + pub.yssGetTableName("Tb_Para_Receiver") +

                    " where FCheckState = 1 " + //MS00171 QDV4赢时胜上海2009年1月7日05_B sj modified 20090122 增加对状态的筛选，避免获取重复的数据。

                    ") lp " +
                    " on a.FPayCode = lp.FReceiverCode " +
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    " left join (select FAttrClsCode,FAttrClsName from " +
                    pub.yssGetTableName("Tb_Para_AttributeClass") +
                    ") k on a.FAttrClsCode = k.FAttrClsCode " +
                    " left join (select FAttrClsCode,FAttrClsName as FBAttrClsName from " +
                    pub.yssGetTableName("Tb_Para_AttributeClass") +
                    ") m on a.FBAttrClsCode = m.FAttrClsCode " +
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    " left join (select FCashAccCode,FCashAccName from " +
                    pub.yssGetTableName("Tb_Para_CashAccount") +

                    " where FCheckState = 1 " + //MS00171 QDV4赢时胜上海2009年1月7日05_B sj modified 20090122 增加对状态的筛选，避免获取重复的数据。

                    ") f on a.FBCashAccCode = f.FCashAccCode" +
                    " left join (select FCashAccCode,FCashAccName from " +
                    pub.yssGetTableName("Tb_Para_CashAccount") +

                    " where FCheckState = 1 " + //MS00171 QDV4赢时胜上海2009年1月7日05_B sj modified 20090122 增加对状态的筛选，避免获取重复的数据。

                    ") g on a.FSCashAccCode = g.FCashAccCode" +
                  //add by huangqirong 2012-08-14 story #2822 保证金账户
                    " left join (select FCashAccCode, FCashAccName from " +
                    		pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where FCheckState = 1) ab on a.fbailcashcode = ab.fcashacccode " +
                    //---end---
                    " left join (select FCuryCode,FCuryName from " +
                    pub.yssGetTableName("Tb_Para_Currency") +

                    " where FCheckState = 1 " + //MS00171 QDV4赢时胜上海2009年1月7日05_B sj modified 20090122 增加对状态的筛选，避免获取重复的数据。

                    ") h on a.FBCuryCode = h.FCuryCode " +
                    " left join (select FCuryCode,FCuryName from " +
                    pub.yssGetTableName("Tb_Para_Currency") +

                    " where FCheckState = 1 " + //MS00171 QDV4赢时胜上海2009年1月7日05_B sj modified 20090122 增加对状态的筛选，避免获取重复的数据。

                    ") j on a.FSCuryCode = j.FCuryCode " +
                  //begin by zhouxiang MS01504    库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错 
                    //                    sAry[1] +
                    storageAnalysis()+
                  //end   by zhouxiang MS01504    库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错   
                    getCashStorageAnalysisSql()+
                  //--modify by 黄啟荣 2011-06-01 story  #937 --用于系统查询出来的结果必须与用户的浏览权限一致。
    				buildFilterSql()+ 
                    ")y on y.FNum=x.FNum and y.FTradeDate=x.FTradeDate and y.FPortCode=x.FPortCode and y.FTradeTime=x.FTradeTime and y.FBCashAccCode=x.FBCashAccCode and y.FSCashAccCode = x.FSCashAccCode "
    				//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
//                 	+" where x.FPortcode in (select distinct(tsu.fportcode) as fportcode from(select fportcode from Tb_Sys_Userright"
//  		        		+" where fusercode ="+dbl.sqlString(pub.getUserCode())					    
//  					    +" and frighttype = 'port'"
//  					    +" and FOPERTYPES like '%brow%'"
//  					    +" and frightcode = 'FrmRateTrade') tsu"
//  					    +" inner join "+pub.yssGetTableName("tb_Para_Portfolio")
//  					    +" tpp on tpp.fportcode=tsu.fportcode"
//  					    +" where tpp.fenabled=1"
//  					    +" and tpp.FCheckState=1)";
    				//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
    				//add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B
    				+ " where x.FPortcode in (" + operSql.sqlCodes(right.getUserPortCodes("FrmRateTrade")) + ")";
                	strSql+=" order by y.FTradeDate,y.FNum,y.FPortCode,y.FTradeTime,y.FBCashAccCode,y.FSCashAccCode";
               //---end---
            //}//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji 这里调用基类的方法处理加载后的数据
        } catch (Exception e) {
            throw new YssException("获取外汇交易数据设置信息" + "\r\n" + e.getMessage(), e);
        }
        return builderListViewData(strSql);
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        return "";
    }

    /**
     * getOperData
     */
    public void getOperData() {
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
     * parseRowStr
     * 解析汇率行情数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";

        String sMutiAudit = ""; //MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩 批量处理的数据
        try {
            //MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩 提取批量处理数据
            if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];  //得到的是从前台传来需要审核与反审核的批量数据
                multAuditString = sMutiAudit;                   //保存在全局变量中
                sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];     //前台传来的要更新的一些数据
            }

            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.tradeDate = YssFun.parseDate(reqAry[0], "yyyy-MM-dd");
            this.tradeTime = reqAry[1];
            this.portCode = reqAry[2];
            this.analysisCode1 = reqAry[3];
            this.analysisCode2 = reqAry[4];
            this.analysisCode3 = reqAry[5];
            this.bCashAccCode = reqAry[6];
            this.sCashAccCode = reqAry[7];
            this.settleDate = YssFun.parseDate(reqAry[8], "yyyy-MM-dd");
            this.settleTime = reqAry[9];
            if (reqAry[10].length() != 0) {
                this.tradeType = reqAry[10];
            }
            if (reqAry[11].length() != 0) {
                this.catType = reqAry[11];
            }
            this.exCuryRate = YssFun.toDouble(reqAry[12]);
            this.lingCuryRate = YssFun.toDouble(reqAry[13]);
            this.bMoney = YssFun.toDouble(reqAry[14]);
            this.sMoney = YssFun.toDouble(reqAry[15]);
            this.baseMoney = YssFun.toDouble(reqAry[16]);
            this.portMoney = YssFun.toDouble(reqAry[17]);
            this.rateFx = YssFun.toDouble(reqAry[18]);
            this.upDown = YssFun.toDouble(reqAry[19]);
            this.desc = reqAry[20];
            this.checkStateId = YssFun.toInt(reqAry[21]);
            this.oldTradeDate = YssFun.parseDate(reqAry[22], "yyyy-MM-dd");
            this.oldTradeTime = reqAry[23];
            this.oldPortCode = reqAry[24];
            this.oldBCashAccCode = reqAry[25];
            this.oldSCashAccCode = reqAry[26];
            this.oldNum = reqAry[27];
            this.bCuryCode = reqAry[28];
            this.sCuryCode = reqAry[29];
            this.Num = reqAry[30];
            this.beginDate = YssFun.parseDate(reqAry[31], "yyyy-MM-dd");
            this.endDate = YssFun.parseDate(reqAry[32], "yyyy-MM-dd");
            this.isOnlyColumns = reqAry[33];
            this.bCuryFee = YssFun.toDouble(reqAry[34]);
            this.sCuryFee = YssFun.toDouble(reqAry[35]);
            this.bPortCode = reqAry[36];
            this.bAnalysisCode1 = reqAry[37];
            this.bAnalysisCode2 = reqAry[38];
            this.bAnalysisCode3 = reqAry[39];
            this.bSettleDate = YssFun.parseDate(reqAry[40], "yyyy-MM-dd");
            this.bSettleTime = reqAry[41];
            this.sPayCode = reqAry[42];
            this.sReceiverCode = reqAry[43];
            this.sDateSource = reqAry[44]; //BugNo:0000462 edit by jc
            
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            this.strAttrClsCode = reqAry[45];
            this.strBAttrClsCode = reqAry[46];
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            this.sRateTradeType = reqAry[47];//edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A
            
            //add by huangqirong 2012-08-14 story #2822 保证金
            this.bailType = reqAry[48];
            this.bailScale = YssFun.toDouble(reqAry[49]);
            this.bailFix = YssFun.toDouble(reqAry[50]);
            this.bailCashCode = reqAry[51];
            //---end---
            this.RateReason = reqAry[52];//add by zhaoxianlin 20121218 STORY #3383 外管局报表
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RateTradeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析外汇交易数据设置请求出错", e);
        }
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji 这里调用基类的方法处理加载后的数据
            if (this.filterType.isOnlyColumns.equalsIgnoreCase("1")) {
                sResult = sResult + " and 1=2 ";
                return sResult;
            }
            //----------------------------end 20100316-----------------------------------//
            if (this.filterType.portCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }
            //------ add by wangzuochun 2010.08.03  MS01495    进入业务资料——外汇交易界面，点击筛选按钮后，不能根据外汇类型和投资组合进行筛选。    QDV4赢时胜(测试)2010年07月27日01_B   
            if (this.filterType.bPortCode.length() != 0) {
                sResult = sResult + " and a.FBPortCode like '" +
                    filterType.bPortCode.replaceAll("'", "''") + "%'";
            }
            //-------------------------MS01495--------------------------//
            
            if (this.filterType.bCuryCode.length() != 0) {
                sResult = sResult + " and a.FBCuryCode like '" +
                    filterType.bCuryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sCuryCode.length() != 0) {
                sResult = sResult + " and a.FSCuryCode like '" +
                    filterType.sCuryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.bCashAccCode.length() != 0) {
                sResult = sResult + " and a.FbCashAccCode like '" +
                    filterType.bCashAccCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sCashAccCode.length() != 0) {
                sResult = sResult + " and a.FsCashAccCode like '" +
                    filterType.sCashAccCode.replaceAll("'", "''") + "%'";
            }
            if (analy1) {
                if (this.filterType.analysisCode1.length() != 0) {
                    sResult = sResult + " and a.FanalysisCode1 like '" +
                        filterType.analysisCode1.replaceAll("'", "''") + "%'";
                }
            }
            if (analy2) {
                if (this.filterType.analysisCode2.length() != 0) {
                    sResult = sResult + " and a.FanalysisCode2 like '" +
                        filterType.analysisCode2.replaceAll("'", "''") + "%'";
                }
            }
            if (analy3) {
                if (this.filterType.analysisCode3.length() != 0) {
                    sResult = sResult + " and a.FanalysisCode3 like '" +
                        filterType.analysisCode3.replaceAll("'", "''") + "%'";
                }
            }

            if (this.filterType.tradeDate != null &&
                !YssFun.formatDate(filterType.tradeDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and FtradeDate = " +
                    dbl.sqlDate(filterType.tradeDate);
            }
            if (this.filterType.settleDate != null &&
                !YssFun.formatDate(filterType.settleDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and FsettleDate = " +
                    dbl.sqlDate(filterType.settleDate);
            }
            if (this.filterType.tradeType.length() != 0) {
                sResult = sResult + " and FtradeType = " +
                    dbl.sqlString(filterType.tradeType);
            }
            if (this.filterType.catType.length() != 0) {
                sResult = sResult + " and FcatType = " +
                    dbl.sqlString(filterType.catType);
            }
            if (this.filterType.exCuryRate != 0) {
                sResult = sResult + " and FexCuryRate = " +
                    filterType.exCuryRate;
            }
            if (this.filterType.lingCuryRate != 0) {
                sResult = sResult + " and FlongCuryRate = " +
                    filterType.lingCuryRate;
            }
            if (this.filterType.bMoney != 0) {
                sResult = sResult + " and FbMoney = " +
                    filterType.bMoney;
            }
            if (this.filterType.sMoney != 0) {
                sResult = sResult + " and FsMoney = " +
                    filterType.sMoney;
            }
            if (this.filterType.baseMoney != 0) {
                sResult = sResult + " and FbaseMoney = " +
                    filterType.baseMoney;
            }
            if (this.filterType.portMoney != 0) {
                sResult = sResult + " and FportMoney = " +
                    filterType.portMoney;
            }
            if (this.filterType.rateFx != 0) {
                sResult = sResult + " and FrateFx = " +
                    filterType.rateFx;
            }
            if (this.filterType.upDown != 0) {
                sResult = sResult + " and FupDown = " +
                    filterType.upDown;
            }
            if (this.filterType.bCuryFee != 0) {
                sResult = sResult + " and FBCuryFee = " +
                    filterType.bCuryFee;
            }
            if (this.filterType.sCuryFee != 0) {
                sResult = sResult + " and FSCuryFee = " +
                    filterType.sCuryFee;
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.Fdesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
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
          //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            if (this.filterType.strAttrClsCode.trim().length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.strAttrClsCode.replaceAll("'", "''") + "%'";
            }
            
            if(this.filterType.strBAttrClsCode.trim().length() !=0){
            	sResult = sResult + " and a.FBAttrClsCode like '" +
                filterType.strBAttrClsCode.replaceAll("'", "''") + "%'";
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            //---edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A start---//
            if(this.filterType.sRateTradeType.length() != 0 && 
              !this.filterType.sRateTradeType.equals("0") && 
              !this.filterType.sRateTradeType.equals("ALL")){
            	sResult = sResult + " and a.FRateTradeType like '" +
            	filterType.sRateTradeType.replaceAll("'", "''") + "%'";
            }
            //---edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A end---//
        }
        return sResult;
    }

    /**
     * saveMutliOperData
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliOperData(String sMutilRowStr) {
        return "";
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
//        boolean analy1 = operSql.storageAnalysis("FAnalysisCode1",
//                                                 YssOperCons.YSS_KCLX_Cash); //判断分析代码存不存在
//        boolean analy2 = operSql.storageAnalysis("FAnalysisCode2",
//                                                 YssOperCons.YSS_KCLX_Cash);
//        boolean analy3 = operSql.storageAnalysis("FAnalysisCode3",
//                                                 YssOperCons.YSS_KCLX_Cash);//findbugs风险调整，定义的变量未使用，定义的变量与成员变量名  胡坤  20120625
        this.Num = rs.getString("FNum");
        this.tradeDate = rs.getDate("FtradeDate");
        this.tradeTime = rs.getString("FtradeTime");
        this.portCode = rs.getString("FportCode");
        this.portName = rs.getString("FportName");
        
        
        
        this.analysisCode1 = rs.getString("FAnalysisCode1");
        this.analysisName1= rs.getString("FAnalysisName1");
        this.analysisCode2 = rs.getString("FAnalysisCode2");
        this.analysisName2  = rs.getString("FAnalysisName2");
        this.analysisCode3 = rs.getString("FAnalysisCode3");
        this.analysisName3= rs.getString("FAnalysisName3");
        
        
        this.bAnalysisCode1=rs.getString("FBanalysisCode1");
        this.bAnalysisCode2=rs.getString("FBanalysisCode2");
        this.bAnalysisCode3=rs.getString("FBanalysisCode3");
        this.bAnalysisName1=rs.getString("FBanalysisName1");
        this.bAnalysisName2=rs.getString("FBanalysisName2");
        this.bAnalysisName3=rs.getString("FBanalysisName3");

//        if (analy1) {
//            this.analysisCode1 = rs.getString("FanalysisCode1") + "";
//            //edited by zhouxiang MS01504   库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错    
//            this.analysisName1 = rs.getString("FanalysisName1") + "";
//            
//            //end--  by zhouxiang MS01504   库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错    
//            this.bAnalysisCode1 = rs.getString("FBanalysisCode1") + "";
//            this.bAnalysisName1 = rs.getString("FBanalysisName1") + "";
//        }
//        if (analy2) {
//            this.analysisCode2 = rs.getString("FanalysisCode2") + "";
//            //edited by zhouxiang MS01504   库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错    
//            //this.analysisName2 = rs.getString("FCatName") + "";
//            this.analysisName2 = rs.getString("FanalysisName2") + "";
//            //end--  by zhouxiang MS01504   库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错    
//            
//            this.bAnalysisCode2 = rs.getString("FBanalysisCode2") + "";
//            this.bAnalysisName2 = rs.getString("FBanalysisName2") + "";
//        }
//        if (analy3) {
//            this.analysisCode3 = rs.getString("FanalysisCode3") + "";
//            this.bAnalysisCode3 = rs.getString("FBanalysisCode3") + "";
//        }
        this.bPortCode = rs.getString("FBPortCode");
        this.bPortName = rs.getString("FBPortName");
        this.bCashAccCode = rs.getString("FbCashAccCode");
        this.bCashAccName = rs.getString("FbCashAccName");
        this.sCashAccCode = rs.getString("FsCashAccCode");
        this.sCashAccName = rs.getString("FsCashAccName");
        this.settleTime = rs.getString("FsettleTime");
        this.settleDate = rs.getDate("FsettleDate");
        this.bSettleTime = rs.getString("FBSettleTime"); //ALTER BY SUNNY
        this.bSettleDate = rs.getDate("FBSettleDate");
        this.tradeType = rs.getString("FtradeType");
        this.tradeTypeName = rs.getString("FtradeTypeName");
        this.catType = rs.getString("FcatType");
        this.catTypeName = rs.getString("FcatTypeName");
        this.exCuryRate = rs.getDouble("FexCuryRate");
        this.lingCuryRate = rs.getDouble("FlongCuryRate");
        this.bMoney = rs.getDouble("FbMoney");
        this.sMoney = rs.getDouble("FsMoney");
        this.baseMoney = rs.getDouble("FbaseMoney");
        this.portMoney = rs.getDouble("FportMoney");
        this.rateFx = rs.getDouble("FrateFx");
        this.upDown = rs.getDouble("FupDown");
        this.desc = rs.getString("FDesc");
        this.bCuryCode = rs.getString("FBCuryCode");
        this.bCuryName = rs.getString("FBCuryName");
        this.sCuryCode = rs.getString("FSCuryCode");
        this.sCuryName = rs.getString("FSCuryName");
        this.bCuryFee = rs.getDouble("FBCuryFee");
        this.sCuryFee = rs.getDouble("FSCuryFee");
        this.sPayCode = rs.getString("FPayCode");
        this.sPayName = rs.getString("FPayName");
        this.sReceiverCode = rs.getString("FReceiverCode");
        this.sReceiverName = rs.getString("FReceiverName");
        
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        if(dbl.isFieldExist(rs, "FATTRCLSCODE")&&dbl.isFieldExist(rs, "FBATTRCLSCODE")){
        	this.strAttrClsCode = rs.getString("FATTRCLSCODE");
        	this.strAttrClsName = rs.getString("FAttrClsName");
        	this.strBAttrClsCode = rs.getString("FBATTRCLSCODE");
        	this.strBAttrClsName = rs.getString("FBAttrClsName");
        }else{
        	this.strAttrClsCode = "";
        	this.strAttrClsName = "";
        	this.strBAttrClsCode = "";
        	this.strBAttrClsName = "";
        }
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        
      //---edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A start---//
        if("1".equalsIgnoreCase(rs.getString("FRateTradeType"))){
        	this.sRateTradeType = " ";
        }else if("2".equalsIgnoreCase(rs.getString("FRateTradeType"))){
        	this.sRateTradeType = "外汇交易业务";
        }else if("3".equalsIgnoreCase(rs.getString("FRateTradeType"))){
        	this.sRateTradeType = "换汇业务";
        }else {
        	this.sRateTradeType=" ";
        }
        this.bailType = rs.getString("FBAILTYPE"); 		//add by  huangqirong 2012-08-16 story #2822 外汇交易保证金
        this.bailScale = rs.getDouble("FBAILSCALE"); 	//add by  huangqirong 2012-08-16 story #2822 外汇交易保证金
        this.bailFix = rs.getDouble("FBAILFIX");		//add by  huangqirong 2012-08-16 story #2822 外汇交易保证金
        this.bailCashCode = rs.getString("FBAILCashCode") == null ? "" : rs.getString("FBAILCashCode"); //add by  huangqirong 2012-08-16 story #2822 外汇交易保证金
        this.bailCashName = rs.getString("FBAILCashName") == null ? "" : rs.getString("FBAILCashName");//add by  huangqirong 2012-08-16 story #2822 外汇交易保证金
        //---edit by songjie 2011.07.19 需求 1282 QDV4博时基金2011年6月29日01_A end---//
        this.RateReason = rs.getString("FRateReason");//add by zhaoxianlin 20121218 STORY #3383 外管局报表 
        super.setRecLog(rs);

    }

    /**
     * 得到一个比例
     * @throws YssException
     */
    public void getScale() throws YssException {
        double accMoney = 0;
        YssOperFun fun = new YssOperFun(pub);
        try {
            accMoney = fun.getCashAccBalance(this.tradeDate,
                                             this.sCashAccCode,
                                             this.portCode,
                                             this.analysisCode1,
                                             this.analysisCode2);
            if (accMoney != 0) {
                this.dscale = YssD.div(this.sMoney, accMoney);
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage() + "\n" + "获取比例出错");
        }
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String sResult = "";
        double dScale = 0;
        CashStorageBean cashStg = null;
        double accBaseMoney = 0;
        double accPortMoney = 0;
        double dhdsy = 0;
        double dBBaseRate = 1; //买入货币基础汇率
        double dSBaseRate = 1; //卖出汇率基础汇率
        double dPortRate = 1; //组合汇率
        String strReturnMoney = "0";
        double dBSetMoney = 0; //买入本位币金额
        double dSSetMoney = 0; //卖出本位币金额
        try {
            if (sType.equalsIgnoreCase("costfx")) { //成本和汇兑损益计算
//                cashStg = operFun.getCashAccStg(this.tradeDate,
//                                                this.sCashAccCode,
//                                                this.portCode,
//                                                this.analysisCode1,
//                                                this.analysisCode2,
//                                                this.analysisCode3,
//                                                false);//MS00538 QDV4海富通2009年06月21日02_AB，添加此boolean参数，目的是为了在获取现金金额是不获取当日资金调拨的外汇交易部分。
                cashStg = operFun.getCashAccStgForRateTrade(this.tradeDate,
                    this.sCashAccCode,
                    this.portCode,
                    this.analysisCode1,
                    this.analysisCode2,
                    this.analysisCode3); //MS00538 QDV4海富通2009年06月21日02_AB，使用外汇交易专用的获取余额的方式。
                if (cashStg == null) {
                    return "";
                }
                if (YssFun.toDouble(cashStg.getStrAccBalance()) != 0) {
                    dScale = YssD.div(this.sMoney,
                                      YssFun.toDouble(cashStg.getStrAccBalance()));
                }
				// begin by zhouxiang MS01612 外汇交易算法变更 将卖出货币的基础汇率计算出来方便计算基础货币的余额
				dSBaseRate = this.getSettingOper().getCuryRate(tradeDate,
						this.sCuryCode, portCode, YssOperCons.YSS_RATE_BASE);
				CtlPubPara pubPara = new CtlPubPara(); //引用通用参数设置 对象pubPara
				pubPara.setYssPub(pub);
				String sPara = pubPara.getRateCalculateType("rateTradeBaseMoney","ComboBox1",1);//获取控件的内容
				if (sPara.equals("0,0")) {									//判断是否采用新算法计算基础余额（基础货币金额=卖出货币金额*当日基础货币汇率）
					accBaseMoney = YssD.round(YssD.mul(this.sMoney, dSBaseRate), 2);
				} else{
					accBaseMoney = YssD.round(YssD.mul(YssFun.toDouble(cashStg
							.getStrBaseCuryBal()), dScale), 2);
					accPortMoney = YssD.round(YssD.mul(YssFun.toDouble(cashStg
							.getStrPortCuryBal()), dScale), 2); //add by fangjiang 2011.07.08 STORY #1280
				} 
				//---------------------end MS01612  20100902----------------------------------------------------
				PortfolioBean port = new PortfolioBean();
                port.setYssPub(pub);
                port.setPortCode(this.portCode);
                port.getSetting();

                if (this.bCuryCode.equalsIgnoreCase(port.getCurrencyCode()) &&
                    !this.sCuryCode.equalsIgnoreCase(port.getCurrencyCode())) { //当买入货币是组合货币时计算汇兑损益
                    dhdsy = YssD.round(YssD.sub(this.bMoney, accPortMoney), 2); //汇兑损益的计算方法是 流入货币－计算出的组合货币成本
                } else if (this.sCuryCode.equalsIgnoreCase(port.getCurrencyCode()) &&
                           !this.bCuryCode.equalsIgnoreCase(port.getCurrencyCode())) { //当卖出货币是组合货币时计算汇兑损益
                    dBBaseRate = this.getSettingOper().getCuryRate(tradeDate,
                        this.bCuryCode,
                        portCode, YssOperCons.YSS_RATE_BASE);
                    //dhdsy = YssD.sub(YssD.round(YssD.mul(bMoney, this.exCuryRate), 2),
                    //                 YssD.round(YssD.mul(bMoney, dBBaseRate), 2)); //汇兑损益的计算方法是 买入货币金额*交易汇率－买入货币金额*中间价汇率(汇率数据表中的汇率)
                    //QDV4中保2009年05月04日02_B  MS00431 by leeyu 20090505 采用 (中间价汇率－当日汇率)×买入货币金额
                    dhdsy = YssD.sub(YssD.round(YssD.mul(bMoney, dBBaseRate), 2),
                                     YssD.round(YssD.mul(bMoney, this.exCuryRate), 2)); //汇兑损益的计算方法是 买入货币金额*中间价汇率(汇率数据表中的汇率)-买入货币金额*交易汇率.
                } else if (!this.sCuryCode.equalsIgnoreCase(port.getCurrencyCode()) &&
                           !this.bCuryCode.equalsIgnoreCase(port.getCurrencyCode())) { //当卖出货币和买入货币都不是组合货币时计算汇兑损益 胡昆 20070925
                    dBBaseRate = this.getSettingOper().getCuryRate(tradeDate,
                        this.bCuryCode,
                        portCode, YssOperCons.YSS_RATE_BASE);
                    
                    dPortRate = this.getSettingOper().getCuryRate(tradeDate,
                        port.getCurrencyCode(),
                        portCode, YssOperCons.YSS_RATE_PORT);
                    dBSetMoney = this.getSettingOper().calPortMoney(bMoney, dBBaseRate, dPortRate, //计算买入货币本位币金额
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        this.bCuryCode, this.tradeDate, this.portCode);
                    dSSetMoney = this.getSettingOper().calPortMoney(sMoney, dSBaseRate, dPortRate, //计算卖出货币本位币金额
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        this.sCuryCode, this.tradeDate, this.portCode);
                    dhdsy = YssD.sub(dSSetMoney, dBSetMoney);
                }
                this.baseMoney = accBaseMoney;
                this.portMoney = accPortMoney;
                this.rateFx = dhdsy;
//            strReturnMoney = accBaseMoney + "\t" + accPortMoney + "\t" + dhdsy;
                sResult = this.buildRowStr();
            }
            //-------------------------------------------------------------------------------
            //MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩 批量审核/反审核
            if (sType.equalsIgnoreCase("multauditTradeSub")) { //判断是否要进行批量审核与反审核
                if (multAuditString.length() > 0) { //判断批量审核与反审核的内容是否为空
                    return this.auditMutli(this.multAuditString); //执行批量审核/反审核
                }
            }
            // MS00581 获取外汇交易的账户余额，从而在进行资金调拨的时候判断账户余额是否足够
            if (sType.equalsIgnoreCase("sumaccbalance")) {
                cashStg = operFun.getCashAccStgForRateTrade(this.tradeDate,
                    this.sCashAccCode,
                    this.portCode,
                    this.analysisCode1,
                    this.analysisCode2,
                    this.analysisCode3);
                return cashStg.getStrAccBalance();
            }
            if(sType.equalsIgnoreCase("getRateTradeInfo")){//SWIFT 导出数据使用 by 李道龙 20091120
            	return this.getRateTradeInfo();
            }
            // End MS00581 edited by libo QDV4海富通2009年7月17日01_AB 针对换汇业务产生资金调拨时能自动判断存款余额
            return sResult;
        }
        //---------------------------------------------------------------------------------
        catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }
    
	/***
	** SWIFT 导出数据使用 by 李道龙 20091120
	**/
    private String getRateTradeInfo() throws YssException{
    	 String strSql = "";
    	 ResultSet rs=null;
         String sAry[] = null;
         String sAry2[] = null;
         try {
             analy1 = operSql.storageAnalysis("FAnalysisCode1",
                                              YssOperCons.YSS_KCLX_Cash); //判断分析代码存不存在
             analy2 = operSql.storageAnalysis("FAnalysisCode2",
                                              YssOperCons.YSS_KCLX_Cash);
             analy3 = operSql.storageAnalysis("FAnalysisCode3",
                                              YssOperCons.YSS_KCLX_Cash);
             sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Cash); //获得分析代码

             
                 strSql =
                     "select  distinct y.* from " +
                     "(select FNum,FTradeDate,FTradeTime,FPortCode,FBCashAccCode,FSCashAccCode from " +
                     pub.yssGetTableName("Tb_Data_RateTrade") + " " +
                     "  group by FNum,FTradeDate,FTradeTime,FPortCode,FBCashAccCode,FSCashAccCode) x join" +
                     " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,lp.FReceiverName as FPayName,lr.FReceiverName as FReceiverName, " +
                     " d.FVocName as FTradeTypeName,i.FVocName as FCatTypeName,f.FCashAccName as FBCashAccName,g.FCashAccName as FSCashAccName,h.FCuryName as FBCuryName,j.FCuryName as FSCuryName,e.FPortName as FPortName" +
                     sAry[0] +
                     (analy1 ? ", FBAnalysisName1" : "") +
                     (analy2 ? ",FBAnalysisName2" : "") +
                     (analy3 ? ",FBAnalysisName3" : "") +
                     ",l.FPortName as FBPortName " +
                     //add by zhangfa 20100920 MS01758    Swift报文导出，选中外汇业务数据查看报错    QDV4赢时胜(测试)2010年09月16日3_B 
                     " ,invmgr.FAnalysisName1,tn2.FAnalysisName2,tn2.FBAnalysisName2,tn3.FAnalysisName3,tn3.FBAnalysisName3 "+
                     //----------------------------------------------------------------------------------------------------------
                     " from " +
                     pub.yssGetTableName("Tb_Data_RateTrade") + " a" +

                     " left join (select FUserCode,FUserName from Tb_Sys_UserList ) b on a.FCreator = b.FUserCode" +
                     " left join (select FUserCode,FUserName from Tb_Sys_UserList ) c on a.FCheckUser = c.FUserCode" +
                     " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FTradeType") + " = d.FVocCode and d.FVocTypeCode = " + //lzp  modify 2007 12.7
                     dbl.sqlString(YssCons.YSS_TA_TradeType) +
                     " left join Tb_Fun_Vocabulary i on " + dbl.sqlToChar("a.FCatType") + " = i.FVocCode and i.FVocTypeCode = " + //lzp  modify 2007 12.7
                     dbl.sqlString(YssCons.YSS_TA_CatType) +
                     " left join (select FPortCode,FPortName from " +
                     pub.yssGetTableName("Tb_Para_Portfolio") +

                     " where FCheckState = 1 " + 

                     " ) e on a.FPortCode = e.FPortCode" +

                     " left join (select FPortCode,FPortName from " +
                     pub.yssGetTableName("Tb_Para_Portfolio") +

                     " where FCheckState = 1 " + 

                     ") l on a.FBPortCode = l.FPortCode" +

                     " left join (select FReceiverCode,FReceiverName from " + pub.yssGetTableName("Tb_Para_Receiver") +

                     " where FCheckState = 1 " + 

                     ") lr " +
                     " on a.FReceiverCode = lr.FReceiverCode " +
                     " left join (select FReceiverCode,FReceiverName from " + pub.yssGetTableName("Tb_Para_Receiver") +

                     " where FCheckState = 1 " + 

                     ") lp " +
                     " on a.FPayCode = lp.FReceiverCode " +

                     " left join (select FCashAccCode,FCashAccName from " +
                     pub.yssGetTableName("Tb_Para_CashAccount") +

                     " where FCheckState = 1 " + 

                     ") f on a.FBCashAccCode = f.FCashAccCode" +
                     " left join (select FCashAccCode,FCashAccName from " +
                     pub.yssGetTableName("Tb_Para_CashAccount") +

                     " where FCheckState = 1 " + 

                     ") g on a.FSCashAccCode = g.FCashAccCode" +
                     " left join (select FCuryCode,FCuryName from " +
                     pub.yssGetTableName("Tb_Para_Currency") +

                     " where FCheckState = 1 " + 

                     ") h on a.FBCuryCode = h.FCuryCode " +
                     " left join (select FCuryCode,FCuryName from " +
                     pub.yssGetTableName("Tb_Para_Currency") +

                     " where FCheckState = 1 " + 

                     ") j on a.FSCuryCode = j.FCuryCode " +
                     sAry[1] +
                     getCashStorageAnalysisSql() +
                     //buildFilterSql() +
                     ")y on y.FNum=x.FNum and y.FTradeDate=x.FTradeDate and y.FPortCode=x.FPortCode and y.FTradeTime=x.FTradeTime and y.FBCashAccCode=x.FBCashAccCode and y.FSCashAccCode = x.FSCashAccCode " +
                     " where y.FNum ="+dbl.sqlString(this.Num)+
                     " order by y.FTradeDate,y.FNum,y.FPortCode,y.FTradeTime,y.FBCashAccCode,y.FSCashAccCode";

                 rs =dbl.openResultSet(strSql);
                 if(rs.next()){
                	 this.setResultSetAttr(rs);
                 }
			
			return this.buildRowStr();
		} catch (Exception ex) {
			throw new YssException(ex.getMessage(),ex);
		}
    }
    

//   public String calcCostFx(){
//
//   }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        ExchangeRateBean befEditBean = new ExchangeRateBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /***
     *产生划款指令方法
     * CommandBean by ly
     */

    public void addCashCommon(CommandBean comm) throws YssException {
        comm.setBTransfer(false); //不产生资金调拨
        try {
            ReceiverBean pay = new ReceiverBean();
            ReceiverBean Receiver = new ReceiverBean();
            Receiver.setYssPub(pub);
            pay.setYssPub(pub);
            Receiver.setOldReceiverCode(comm.getReceiverName());
            pay.setOldReceiverCode(comm.getPayName()); //接受上面传过来的值 by liyu 080526
            Receiver.getSetting();
            pay.getSetting();
            comm.setReceiverName(Receiver.getReceiverName());
            comm.setPayOperBank(pay.getOperBank());
            comm.setPayAccountNO(pay.getAccountNumber());
            comm.setPayName(pay.getReceiverName());
            comm.setReOperBank(Receiver.getOperBank());
            comm.setReAccountNO(Receiver.getAccountNumber());
            String sqlStr = "delete from " + pub.yssGetTableName("Tb_Cash_Command") +
                " where FRelaNum=" + dbl.sqlString(comm.getSRelaNum()) +
                " and FNumType=" + dbl.sqlString(comm.getSRelaType());
            dbl.executeSql(sqlStr);
			comm.addSetting();
        } catch (Exception e) {
            throw new YssException("产生资金调拨出错", e);
        }
    }

    /**
     * 删除回收站内的数据，即将数据从数据库彻底删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_RateTrade") +
                        " where FNum = " + dbl.sqlString(this.Num);
                    //执行sql语句
                    dbl.executeSql(strSql);
                    //对应的删除资金调拨的信息
                    tranAdmin = new CashTransAdmin();
                    tranAdmin.setYssPub(pub);
                    tranAdmin.delete("", "", this.Num);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Cash_Command") +
                        " where FRelaNum=" + dbl.sqlString(this.Num) +
                        " and FNumType='RateTrade'";
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而Num不为空，则按照Num来执行sql语句
            else if (Num != "" && Num != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_RateTrade") +
                    " where FNum = " + dbl.sqlString(this.Num);
                //执行sql语句
                dbl.executeSql(strSql);
                //对应的删除资金调拨的信息
                tranAdmin = new CashTransAdmin();
                tranAdmin.setYssPub(pub);
                tranAdmin.delete("", "", this.Num);
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Cash_Command") +
                    " where FRelaNum=" + dbl.sqlString(this.Num) +
                    " and FNumType='RateTrade'";
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     * 更新批量审核与反审核数据的内容
     */
    public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null; //建立一个数据库连接
        String sqlStr = ""; //创建一个字符串
        PreparedStatement psmt1 = null;
        PreparedStatement psmt2 = null;
        boolean bTrans = true; //建一个boolean变量，默认自动回滚
        RateTradeBean tmpRate = null; //创建一个外汇交易pojo类
        String[] multAudit = null; //建一个字符串数组

        try {
            conn = dbl.loadConnection(); //和数据库进行连接
            //审核/反审核外汇交易时，要同时处理资金调拨和划款指令 sunkey 20090317=========
            //审核/反审核外汇交易信息
            sqlStr = "update " + pub.yssGetTableName("Tb_Data_RateTrade") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = ?"; //更新数据库审核与未审核的SQL语句
            psmt1 = conn.prepareStatement(sqlStr); //执行SQL语句

            //审核/反审核划款指令
            sqlStr = "update " + pub.yssGetTableName("Tb_Cash_Command") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FRelaNum=? and FNumType='RateTrade'";
            psmt2 = conn.prepareStatement(sqlStr);

            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                        tmpRate = new RateTradeBean(); //new 一个pojo类
                        tmpRate.setYssPub(pub); //设置一些基础信息
                        tmpRate.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                        //更新外汇交易
                        psmt1.setString(1, tmpRate.Num); //设置SQL语句的查寻条件
                        psmt1.addBatch();
                        //更新划款指令
                        psmt2.setString(1, tmpRate.Num);
                        psmt2.addBatch();
                        //更新资金调拨
                        tranAdmin = new CashTransAdmin();
                        tranAdmin.setYssPub(pub);
                        sTranNum = tranAdmin.getTransNums("", "", "", tmpRate.Num, "","");                        
                        if (!sTranNum.equals("")) {
                            sTranNum = sTranNum.substring(0, sTranNum.length() - 1);
                            tranAdmin.check(this.checkStateId, sTranNum);
                        }
                    }
                }
                conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
                psmt1.executeBatch();
                psmt2.executeBatch();
                conn.commit(); //提交事物
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("批量审核换汇交易表出错!");
        } finally {
            //关闭游标，结束事物
            dbl.closeStatementFinal(psmt1, psmt2);
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

   
    /**
     * 获取辅助字段之查询Sql语句
     * @return String
     * copyed by zhouxiang MS01504   库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错    
     */
    public String storageAnalysis() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_Cash);
        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                        " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                        i +
                        " from  (select FBrokerCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_broker") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FBrokerCode )x " +
                        " join (select * from " +
                        pub.yssGetTableName("tb_para_broker") + ") y on x.FBrokerCode = y.FBrokerCode and x.FStartDate = y.FStartDate) broker on a.FAnalysisCode" +
                        i + " = broker.FBrokerCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("003")) {
                    sResult = sResult +
                        " left join (select m.FExchangeCode ,m.FExchangeName  as FAnalysisName" +
                        i +
                        " from  (select FExchangeCode from tb_base_exchange " +

                        " where  FCheckState = 1 group by FExchangeCode )x " +
                        " join (select * from tb_base_exchange " +
                        ") m on x.FExchangeCode = m.FExchangeCode) exchange on a.FAnalysisCode" +
                        i + " = exchange.FExchangeCode";

                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                        i +
                        "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        "join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr on a.FAnalysisCode" +
                        i + " = invmgr.FInvMgrCode ";
                } else {
                    sResult = sResult +
                        " left join (select ' ' as FAnalysisNull , ' ' as FAnalysisName" +//分页需求,这里采用空格，合并太平版本调整  by leeyu 20100907
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                        i + ".FAnalysisNull ";
                }
            }
        }

        //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        dbl.closeResultSetFinal(rs);

        return sResult;

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
}
