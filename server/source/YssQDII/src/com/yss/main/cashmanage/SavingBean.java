package com.yss.main.cashmanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.income.stat.BaseIncomeStatDeal;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class SavingBean
    extends BaseDataSettingBean implements IDataSetting {
    private String num = ""; //自动编号
    private String cashAccCode = ""; //现金帐户
    private String cashAccName = ""; //name
    private String interestAccCode = ""; //利息帐户
    private String interestAccName = ""; //name
    private String depDurCode = ""; //存款期限
    private String depDurName = ""; //name
    private String savingType = ""; //存款类型
    private java.util.Date savingDate; //存入日期
    private String savingTime; //存入时间
    private java.util.Date matureDate; //到期日期
    private String portCode = ""; //组合
    private String portName = ""; //name
    private String invMgrCode = ""; //分析代码1
    private String invMgrName = ""; //name
    private String catCode = "";
    private String catName = "";
    private String brokerCode = ""; //分析代码3
    private String brokerName = ""; //name
    private String transNum = ""; //调拨编号
    private double inMoney = 0; //存入金额
    private double recInterest = 0; //利息金额
    private java.util.Date endDate; //截止日期
    private double baseCuryRate = 0; //基础汇率
    private double portCuryRate = 0; //组合汇率
    private double avgBaseCuryRate = 0; //平均基础汇率
    private double avgPortCuryRate = 0; //平均组合汇率
    private String desc = ""; //描述信息
    private String formulaCode = ""; //计息公式
    private String formulaName = ""; //name
    private String roundCode = ""; //舍入公式
    private String roundName = ""; //name
    private String oldNum = ""; //old num
    private SavingBean filterType;
    private String strIsOnlyColumns = "0";
    private String curyCode = ""; //币种代码
    private String strDuration = ""; //数量
    private String sRecycled = "";

    private String strLxTransNum = ""; //利息调拨编号

    private String strSubAccType = ""; //帐户子类型

    private String strDurUnit = ""; //单位
    private String strCuryCode = ""; //货币

    private int dataSource = 1; //标识
    private String strSaving = ""; //保存的流出帐户信息

    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    private String strAttrClsCode = "";//所属分类
    private String strAttrClsName = "";
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    //------add by zhangjun 2012-05-29 story#2579 RQFII需要系统定期存款可以支持滚存-------------
    private String strFlag = "0";
    public String getStrFlag() {
		return strFlag;
	}

	public void setStrFlag(String strFlag) {
		this.strFlag = strFlag;
	}
	//------add by zhangjun 2012-05-29 story#2579 RQFII需要系统定期存款可以支持滚存-------------
	//------ sj modify 20090630 MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A 添加以下字段 -------------
    private String sTradeType;          //交易类型
    private String sSaveNum;            //存单编号
    private double dIncludeInterest;    //包含利息
    private String sCalcType;           //计息方式
    private double dBasicMoney;         //基本额度
    private double dBasicRate;          //基本利率
    private int iCirculDays;            //通知天数
    private double dSavingMoney;        //存单面值
    
    /**shashijie 2012-7-18 STORY 2796 */
	private String FTakeType = "";//提取类型 0:本金,1利息
	/**end*/
    
    //MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据 add by jiangshichao 2010.09.08
    public void setStrSaving(String strSaving) {
		this.strSaving = strSaving;
	}

	public void setDSavingMoney(double dSavingMoney) {
        this.dSavingMoney = dSavingMoney;
    }

    public double setDSavingMoney() {
        return dSavingMoney;
    }

    public void setSTradeType(String sTradeType) {
        this.sTradeType = sTradeType;
    }

    public String getSTradeType() {
        return sTradeType;
    }

    public void setSSaveNum(String sSaveNum) {
        this.sSaveNum = sSaveNum;
    }

    public String getSSaveNum() {
        return sSaveNum;
    }

    public void setDIncludeInterest(double dIncludeInterest) {
        this.dIncludeInterest = dIncludeInterest;
    }

    public double getDIncludeInterest() {
        return dIncludeInterest;
    }

    public void setSCalcType(String sCalcType) {
        this.sCalcType = sCalcType;
    }

    public String getSCalcTyp() {
        return sCalcType;
    }

    public void setDBasicMoney(double dBasicMoney) {
        this.dBasicMoney = dBasicMoney;
    }

    public double getDBasicMoney() {
        return dBasicMoney;
    }

    public void setDBasicRate(double dBasicRate) {
        this.dBasicRate = dBasicRate;
    }

    public double getDBasicRate() {
        return dBasicRate;
    }

    public void setICirculDays(int iCirculDays) {
        this.iCirculDays = iCirculDays;
    }

    public int getICirculDays() {
        return iCirculDays;
    }

    //-------------------------------------------------------------------------------------//添加字段end
    private ArrayList allOutAcc = new ArrayList(); //保存流出帐户

    public void addOutAcc(SavingOutAccBean outAcc) {
        allOutAcc.add(outAcc);
    }

    public String getPortCode() {
        return portCode;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getFormulaCode() {
        return formulaCode;
    }

    public String getRoundCode() {
        return roundCode;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getCatCode() {
        return catCode;
    }

    public String getSavingTime() {
        return savingTime;
    }

    public String getOldNum() {
        return oldNum;
    }

    public String getStrDurUnit() {
        return strDurUnit;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public String getCatName() {
        return catName;
    }

    public Date getSavingDate() {
        return savingDate;
    }

    public String getNum() {
        return num;
    }

    public String getFormulaName() {
        return formulaName;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getInvMgrName() {
        return invMgrName;
    }

    public double getInMoney() {
        return inMoney;
    }

    public String getSavingType() {
        return savingType;
    }

    public Date getMatureDate() {
        return matureDate;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getDepDurCode() {
        return depDurCode;
    }

    public String getStrDuration() {
        return strDuration;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public String getStrIsOnlyColumns() {
        return strIsOnlyColumns;
    }

    public String getPortName() {
        return portName;
    }

    public String getDesc() {
        return desc;
    }

    public String getTransNum() {
        return transNum;
    }

    public SavingBean getFilterType() {
        return filterType;
    }

    public String getCashAccName() {
        return cashAccName;
    }

    public String getDepDurName() {
        return depDurName;
    }

    public String getInterestAccCode() {
        return interestAccCode;
    }

    public String getRoundName() {
        return roundName;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getInterestAccName() {
        return interestAccName;
    }

    public void setRecInterest(double recInterest) {
        this.recInterest = recInterest;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setFormulaCode(String formulaCode) {
        this.formulaCode = formulaCode;
    }

    public void setRoundCode(String roundCode) {
        this.roundCode = roundCode;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public void setSavingTime(String savingTime) {
        this.savingTime = savingTime;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

    public void setStrDurUnit(String strDurUnit) {
        this.strDurUnit = strDurUnit;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setSavingDate(Date savingDate) {
        this.savingDate = savingDate;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setFormulaName(String formulaName) {
        this.formulaName = formulaName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setInvMgrName(String invMgrName) {
        this.invMgrName = invMgrName;
    }

    public void setInMoney(double inMoney) {
        this.inMoney = inMoney;
    }

    public void setSavingType(String savingType) {
        this.savingType = savingType;
    }

    public void setMatureDate(Date matureDate) {
        this.matureDate = matureDate;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setDepDurCode(String depDurCode) {
        this.depDurCode = depDurCode;
    }

    public void setStrDuration(String strDuration) {
        this.strDuration = strDuration;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setStrIsOnlyColumns(String strIsOnlyColumns) {
        this.strIsOnlyColumns = strIsOnlyColumns;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTransNum(String transNum) {
        this.transNum = transNum;
    }

    public void setFilterType(SavingBean filterType) {
        this.filterType = filterType;
    }

    public void setCashAccName(String cashAccName) {
        this.cashAccName = cashAccName;
    }

    public void setDepDurName(String depDurName) {
        this.depDurName = depDurName;
    }

    public void setInterestAccCode(String interestAccCode) {
        this.interestAccCode = interestAccCode;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setInterestAccName(String interestAccName) {
        this.interestAccName = interestAccName;
    }

    public void setAllOutAcc(ArrayList allOutAcc) {
        this.allOutAcc = allOutAcc;
    }

    public void setStrSubAccType(String strSubAccType) {
        this.strSubAccType = strSubAccType;
    }

    public void setAvgBaseCuryRate(double avgBaseCuryRate) {
        this.avgBaseCuryRate = avgBaseCuryRate;
    }

    public void setAvgPortCuryRate(double avgPortCuryRate) {
        this.avgPortCuryRate = avgPortCuryRate;
    }

    public void setStrLxTransNum(String strLxTransNum) {
        this.strLxTransNum = strLxTransNum;
    }

    public void setDataSource(int dataSource) {
        this.dataSource = dataSource;
    }

    public double getRecInterest() {
        return recInterest;
    }

    public ArrayList getAllOutAcc() {
        return allOutAcc;
    }

    public String getStrSubAccType() {
        return strSubAccType;
    }

    public double getAvgBaseCuryRate() {
        return avgBaseCuryRate;
    }

    public double getAvgPortCuryRate() {
        return avgPortCuryRate;
    }

    public String getStrLxTransNum() {
        return strLxTransNum;
    }

    public int getDataSource() {
        return dataSource;
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
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    
    public SavingBean() {

    }

    /**
     * 记录集的数据设置
     * @param rs ResultSet
     * @param analy1 boolean 增加了两个分析代码
     * @param analy2 boolean
     * @throws SQLException
     * @throws YssException
     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj
     */
    public void setResultSetAttr(ResultSet rs,boolean analy1,boolean analy2) throws SQLException,
        YssException {
        //--- MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj 屏蔽了在此方法中生成分析代码的代码 ---------
//        boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
//        boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
        //-------------------------------------------------------------------------------------------
        this.num = rs.getString("FNum") + "";
        this.oldNum = rs.getString("FNum") + "";
        this.cashAccCode = rs.getString("FCashAccCode") + "";
        this.cashAccName = rs.getString("FCashAccName") + "";
        this.depDurCode = rs.getString("FDepDurCode") + "";
        this.depDurName = rs.getString("FDepDurName") + "";
        this.savingType = rs.getString("FSavingType") + "";
        this.savingDate = rs.getDate("FSavingDate");
        this.savingTime = rs.getString("FSavingTime") + "";
        this.matureDate = rs.getDate("FMatureDate");
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        if (analy1) {
            this.invMgrCode = rs.getString("FInvMgrCode") + "";
            this.invMgrName = rs.getString("FInvMgrName") + "";
        }
        if (analy2) {
            this.catCode = rs.getString("FCatCode") + "";
            this.catName = rs.getString("FCatName") + "";
        }
        //this.brokerCode = rs.getString("FAnalysisCode3") + "";
        //this.brokerName = rs.getString("FAnalysisName3") + "";
        this.transNum = rs.getString("FTransNum") + "";
        this.inMoney = rs.getDouble("FInMoney");
        this.formulaCode = rs.getString("FFormulaCode") + "";
        this.formulaName = rs.getString("FFormulaName") + "";
        this.roundCode = rs.getString("FRoundCode") + "";
        this.roundName = rs.getString("FRoundName") + "";
        this.baseCuryRate = rs.getDouble("FBaseCuryRate");
        this.portCuryRate = rs.getDouble("FPortCuryRate");
        this.desc = rs.getString("FDesc") + "";
        this.strDuration = rs.getString("FDuration");
        this.strDurUnit = rs.getString("FDurUnit");
        this.interestAccCode = rs.getString("FInterestAccCode");
        this.interestAccName = rs.getString("FInterstCashAccName");
        this.recInterest = rs.getDouble("FRecInterest");
        // this.strLxTransNum = rs.getString("FLxTransNum");
        this.avgBaseCuryRate = rs.getDouble("FAvgBaseCuryRate");
        this.avgPortCuryRate = rs.getDouble("FAvgPortCuryRate");
        this.dataSource = rs.getInt("FDataSource");
        //---- sj modify 20090630 MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A 对新建的字段赋值---
        this.sTradeType = null == rs.getString("FTRADETYPE") ? "" : rs.getString("FTRADETYPE");
        this.sSaveNum = null == rs.getString("FSAVINGNUM") ? "" : rs.getString("FSAVINGNUM");
        this.dIncludeInterest = rs.getDouble("FIncludeInterest");
        this.sCalcType = null == rs.getString("FCALCTYPE") ? "" : rs.getString("FCALCTYPE");
        this.dBasicMoney = rs.getDouble("FBASICMONEY");
        this.dBasicRate = rs.getDouble("FBASICRATE");
        this.iCirculDays = rs.getInt("FCirculDays");
        this.dSavingMoney = rs.getDouble("FSAVINGMONEY");
        //------------------------------------------------------------------------------------
        
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        if(dbl.isFieldExist(rs, "FATTRCLSCODE")){
        	this.strAttrClsCode = rs.getString("FATTRCLSCODE");
        	this.strAttrClsName = rs.getString("FATTRCLSNAME");
        }else{
        	this.strAttrClsCode = "";
        	this.strAttrClsName = "";
        }
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        //-------add by zhangjun 2012.05.30 story #2579----------
        this.strFlag = Double.toString(rs.getDouble("Flag"));
        //-------add by zhangjun 2012.05.30 story #2579----------
        super.setRecLog(rs);

    }

    public String buildFilterSql() throws YssException {
        String strSql = "";
        //20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return " where 1=1";
    	//=============end=================
        if (this.filterType != null) {
            strSql = " where 1=1"; //050602 jiangchun
            try {																								//20111027 modified by liubo.Story #1285
                if (this.strIsOnlyColumns.equalsIgnoreCase("0")||this.strIsOnlyColumns.equalsIgnoreCase("")&&pub.isBrown()==false) {//edited by zhouxiang MS01222    首次打开存款业务菜单，建议不要加载出数据   
                    strSql = strSql + " and 1=2 ";
                    return strSql;
                }
                if (this.filterType.num.trim() != null &&
                    this.filterType.num.trim().length() > 0) {
                    strSql += " and a.FNum like '" +
                        this.filterType.num.trim().replaceAll("'", "''") + "%'";
                }
                if (this.filterType.cashAccCode.trim() != null &&
                    this.filterType.cashAccCode.trim().length() > 0) {
                    strSql += " and a.FCashAccCode like '" +
                        this.filterType.cashAccCode.replaceAll("'", "''") +
                        "%'";
                }
                if (this.filterType.depDurCode.trim() != null &&
                    this.filterType.depDurCode.trim().length() > 0) {
                    strSql += " and a.FDepDurCode like '" +
                        this.filterType.depDurCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.savingType.trim() != null &&
                    !this.filterType.savingType.equalsIgnoreCase("99")
                    && this.filterType.savingType.trim().length() > 0) {
                    strSql += " and a.FSavingType like '" +
                        this.filterType.savingType.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.endDate != null &&
                    !YssFun.formatDate(filterType.endDate).equals("9998-12-31")) {
                    if (this.filterType.savingDate != null &&
                        !YssFun.formatDate(filterType.savingDate).equals(
                            "9998-12-31")) {
                        strSql += " and a.FSavingDate between " +
                            dbl.sqlDate(this.filterType.savingDate) +
                            " and " + dbl.sqlDate(this.filterType.endDate);
                    }
                } else if (this.filterType.savingDate != null &&
                           !YssFun.formatDate(filterType.savingDate).equals(
                               "9998-12-31")) {
                    strSql += " and a.FSavingDate = " +
                        dbl.sqlDate(this.filterType.savingDate);
                }
                if (this.filterType.matureDate != null &&
                    !YssFun.formatDate(filterType.matureDate).equals("9998-12-31")) {
                    strSql += " and a.FMatureDate =" +
                        dbl.sqlDate(this.filterType.matureDate);
                }
                if (this.filterType.portCode.trim() != null
                    && this.filterType.portCode.trim().length() > 0) {
                    strSql += " and a.FPortCode like '" +
                        this.filterType.portCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.invMgrCode.trim() != null &&
                    this.filterType.invMgrCode.trim().length() > 0) {
                    strSql += " and a.FAnalysisCode1 like '" +
                        this.filterType.invMgrCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.catCode.trim() != null &&
                    this.filterType.catCode.trim().length() > 0) {
                    strSql += " and a.FAnalysisCode2 like '" +
                        this.filterType.catCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.brokerCode.trim() != null &&
                    this.filterType.brokerCode.trim().length() > 0) {
                    strSql += " and a.FAnalysisCode3 like '" +
                        this.filterType.brokerCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.transNum.trim() != null &&
                    this.filterType.transNum.trim().length() > 0) {
                    strSql += " and a.FTransNum like '" +
                        this.filterType.transNum.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.desc.trim() != null &&
                    this.filterType.desc.trim().length() > 0) {
                    strSql += " and a.FDesc like '" +
                        this.filterType.desc.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.formulaCode.trim() != null &&
                    this.filterType.formulaCode.trim().length() > 0) {
                    strSql += " and a.FFormulaCode like '" +
                        this.filterType.formulaCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.roundCode.trim() != null &&
                    this.filterType.roundCode.trim().length() > 0) {
                    strSql += " and a.FRoundCode like '" +
                        this.filterType.roundCode.replaceAll("'", "''") + "%'";

                }
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                if (this.filterType.strAttrClsCode.length() != 0) {
                	strSql = strSql + " and a.FAttrClsCode like '" +
                        filterType.strAttrClsCode.replaceAll("'", "''") + "%'";
                }
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            } catch (Exception e) {
                throw new YssException("过滤存款业务出错\r\n" + e.getMessage(), e);
            }
        }
        return strSql;
    }

    public String getListViewData1() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = ""; //存储的是存款类型的词汇类型
        StringBuffer bufShowDataStr = new StringBuffer();
        StringBuffer bufAllDataStr = new StringBuffer();
        String sAry[] = null;
        try {
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            sHeader = this.getListView1Headers();
            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Cash); //获得分析代码
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.strIsOnlyColumns.equalsIgnoreCase("0")||this.strIsOnlyColumns.equalsIgnoreCase("")&&!(pub.isBrown())) {	//20111027 modified by liubo.STORY #1285
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                //---- sj modify 20090630 MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A 添加词汇类型中的数据 ---------------------------------------------
                sVocStr = vocabulary.getVoc(YssCons.YSS_CSH_SavingType + "," + YssCons.YSS_CASHSAVE_TRADETYPE + "," + YssCons.YSS_CASHSAVE_CALCTYPE);
                //-----------------------------------------------------------------------------------------------------------------------------------
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + "\r\f" + "voc" + sVocStr;// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------

            //if (!strIsOnlyColumns.trim().equals("0")) {//xuqiji 20100318 QDV4赢时胜上海2010年03月17日06_B MS00884
                strSql =
                    "select a.*, b.fusername as fcreatorname, c.fusername as fcheckusername," +
                    " d.FPortName,e.FCashAccName,f.FDepDurName,f.FDuration,f.FDurUnit,g.FFormulaName,h.FRoundName,l.FCashAccName as FInterstCashAccName" +
                    //--- 20090827 sj 显示出定存类型名称 -----
                    ",n.FVocName as FSavingName,nvl(k.FAttrClsName,' ') as FAttrClsName " +
                    //-------------------------------------
                    sAry[0];
                strSql = strSql + " from " +
                    pub.yssGetTableName("Tb_Cash_SavingInAcc") + " a" +
                    " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                    " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                    //-----------------------------------------------------------------------------------------------
                    " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from " +
                    //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    pub.yssGetTableName("Tb_Para_Portfolio") + " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) +
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                    "(select FPortCode,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_Portfolio") +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FCheckState = 1 and FASSETGROUPCODE = " +
//                    dbl.sqlString(pub.getAssetGroupCode()) +
//                    " group by FPortCode) p " +
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                    //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    " ) d on a.FPortCode = d.FPortCode" +
                    //-------------------------------------------------------------------------------------------------
                    " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                    pub.yssGetTableName("Tb_Para_CashAccount") +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                    //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    " select FCashAccCode, FCashAccName, FStartDate from " +
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    " where FCheckState = 1) e on a.FCashAccCode = e.FCashAccCode " +
                    //---------------------------------------------------------------------------------
                    " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                    pub.yssGetTableName("Tb_Para_CashAccount") +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                    //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    " select FCashAccCode, FCashAccName, FStartDate from " +
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    " where FCheckState = 1) l on a.FCashAccCode = l.FCashAccCode " +
                    //---------------------------------------------------------------------------------------
                    //---------------------------------------------------------------------------------------
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    " left join (select FAttrClsCode,FAttrClsName from " +
                    pub.yssGetTableName("Tb_Para_AttributeClass") +
                    ") k on a.FAttrClsCode = k.FAttrClsCode " +
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    " left join(select FDepDurCode,FDepDurName,FDuration,FDurUnit from " +
                    pub.yssGetTableName("Tb_Para_DepositDuration") +
                    ") f on a.FDepDurCode=f.FDepDurCode " +
                    " left join(select FFormulaCode,FFormulaName from " +
                    pub.yssGetTableName("Tb_Para_Performula") +
                    ") g on a.FFormulaCode=g.FFormulaCode " +
                    " left join(select FRoundCode,FRoundName from " +
                    pub.yssGetTableName("Tb_Para_Rounding") +
                    ") h on a.FRoundCode=h.FRoundCode " +
                    sAry[1] +
                    " left join Tb_Fun_Vocabulary n on " +
                    dbl.sqlToChar("a.FSavingType") +
                    " = n.FVocCode  and n.FVocTypeCode = " +
                    dbl.sqlString(YssCons.YSS_CSH_SavingType) +
                    this.buildFilterSql() + //050602 jiangchun
                    " order by a.FSavingDate, a.FSavingTime desc ";
    			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
    			// rs = dbl.openResultSet(strSql);
    			yssPageInationBean.setsQuerySQL(strSql);
    			yssPageInationBean.setsTableName("SavingInAcc");
    			rs = dbl.openResultSet(yssPageInationBean);
    			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
                while (rs.next()) {
                    bufShowDataStr.append( (rs.getString("FNum") + "").trim());
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append( (rs.getString("FCashAccCode") + "").trim());
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append( (rs.getString("FCashAccName") + "").trim());
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append( (rs.getString("FPortName") + "").trim());
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append( (rs.getString("FDepDurName") + "").trim());
                    bufShowDataStr.append("\t");
                    //--- 20090827 sj 显示出定存类型名称 -----
                    bufShowDataStr.append( (rs.getString("FSavingName") + "").trim());
                    //--------------------------------------
                    bufShowDataStr.append("\t");

                    bufShowDataStr.append(YssFun.formatNumber(rs.getDouble(
                        "FInMoney"),
                        "#,##0.##"));
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append(rs.getDouble("FBaseCuryRate"));
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append(rs.getDouble("FPortCuryRate"));
                    bufShowDataStr.append("\t");

                    bufShowDataStr.append( (rs.getDate("FSavingDate") + "").trim());
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append( (rs.getString("FSavingTime") + "").trim());
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append( (rs.getDate("FMatureDate") + "").trim());
                    bufShowDataStr.append("\t");
                    //bufShowDataStr.append( (rs.getString("FTransNum") + "").trim());
                    //bufShowDataStr.append("\t");
                    if (analy1) {
                        bufShowDataStr.append( (rs.getString("FInvMgrName") + "").
                                              trim());
                    }
                    bufShowDataStr.append("\t");
                    if (analy2) {
                    	//add by zhangfa 20101228 BUG #753 当设置了现金类的库存信息配置时，点击存款业务界面的的查询时，系统强制退出。
                    	try{
                            bufShowDataStr.append( (rs.getString("FCatName") + "").
                                                 trim());
                        }catch(Exception e){
                        	throw new Exception("现金类的库存信息配置，分析代码2应设为品种类型!");
                        }
                        //------------end 20101228------------------------------------------------------------------------------
                    	
                    }
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append("\t");
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    bufShowDataStr.append( (rs.getString("FATTRCLSCODE") + "").trim());
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append( (rs.getString("FAttrClsName") + "").trim());
                    bufShowDataStr.append("\t");
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    bufShowDataStr.append( (rs.getString("FDesc") + "").trim());
                    bufShowDataStr.append("\t");

                    bufShowDataStr.append( (rs.getString("FCreator") + "").trim());
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append( (rs.getString("FCreateTime") + "").trim());
                    bufShowDataStr.append("\t");

                    bufShowDataStr.append( (rs.getString("FCheckUser") + "").trim());
                    bufShowDataStr.append("\t");
                    bufShowDataStr.append( (rs.getString("FCheckTime") + "").trim());
                    bufShowDataStr.append("\t");

                    bufShowDataStr.append(YssCons.YSS_LINESPLITMARK);
                    //--- MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj 将分析代码传入 --
                    setResultSetAttr(rs,analy1,analy2);
                    //------------------------------------------------------------------
                    bufAllDataStr.append(this.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            //}//xuqiji 20100318 QDV4赢时胜上海2010年03月17日06_B MS00884
            if (bufShowDataStr.toString().length() > 2) {
                sShowDataStr = bufShowDataStr.toString().substring(0,
                    bufShowDataStr.toString().length() - 2);
            }

            if (bufAllDataStr.toString().length() > 2) {
                sAllDataStr = bufAllDataStr.toString().substring(0,
                    bufAllDataStr.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            //---- sj modify 20090630 MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A 添加词汇类型中的数据 ---------------------------------------------
            sVocStr = vocabulary.getVoc(YssCons.YSS_CSH_SavingType + "," + YssCons.YSS_CASHSAVE_TRADETYPE + "," + YssCons.YSS_CASHSAVE_CALCTYPE);
            //-----------------------------------------------------------------------------------------------------------------------------------
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + "\r\f" + "voc" + sVocStr;// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取存入帐户信息出错" + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }

    }

    //modify by fangjiang 2010.11.30 STORY #97 协议存款业务需支持提前提取本金的功能
    public void setResultSetAttr(ResultSet rs) throws SQLException,YssException {
	    this.num = rs.getString("FNum") + "";
	    this.cashAccCode = rs.getString("FCashAccCode") + "";
	    this.cashAccName = rs.getString("FCashAccName") + "";
	    this.savingDate = rs.getDate("FSavingDate");
	    this.matureDate = rs.getDate("FMatureDate");
	    this.portCode = rs.getString("FPortCode") + "";
	    this.portName = rs.getString("FPortName") + "";
	    this.savingType=rs.getString("FSavingType") + "";//add by lidaolong
	    this.inMoney = rs.getDouble("FInMoney");//add by lidaolong
    }
    
    public String getListViewData2() throws YssException {
    	String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		String strSql = "";
		try {
			sHeader = "编号\t现金帐户代码\t现金帐户名称\t币种\t组合代码\t组合名称\t存入日期\t到期日期";

		    strSql = "select a.*,b.FPortName as FPortName,c.FCashAccName as FCashAccName, c.FCuryCode as FCuryCode from " +
					pub.yssGetTableName("Tb_Cash_SavingInAcc") + " a " +
		            
		            " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from " +
		            //edit by songjie 2011.03.15 不以最大的启用日期查询数据
		            pub.yssGetTableName("Tb_Para_Portfolio") + " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) +
		            //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//		            "(select FPortCode,max(FStartDate) as FStartDate from " +
//		            pub.yssGetTableName("Tb_Para_Portfolio") +
//		            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//		            " and FCheckState = 1 and FASSETGROUPCODE = " +
//		            dbl.sqlString(pub.getAssetGroupCode()) +
//		            " group by FPortCode) p " +
		            //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
		            //edit by songjie 2011.03.15 不以最大的启用日期查询数据
		            " ) b on a.FPortCode = b.FPortCode" +
		            
		            " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
		            //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//		            pub.yssGetTableName("Tb_Para_CashAccount") +
//		            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
		            //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
		            //edit by songjie 2011.03.15 不以最大的启用日期查询数据
		            " select FCashAccCode, FCashAccName, FStartDate, FCuryCode from " +
		            pub.yssGetTableName("Tb_Para_CashAccount") +
		            //edit by songjie 2011.03.15 不以最大的启用日期查询数据
		            " where FCheckState = 1) c on a.FCashAccCode = c.FCashAccCode "
		            //edit by lidaolong QDV4长信基金2011年1月14日01_A--
		    		+ " where a.FCheckState = 1  and a.FSavingDate <= " + dbl.sqlDate(this.filterType.savingDate)
		    		+ " and a.FMatureDate >= " + dbl.sqlDate(this.filterType.matureDate);
		    		//---end---
		    		/**shashijie 2012-7-18 STORY 2796 如果是利息提取则不考虑之前是否有过提取记录*/
					/*strSql += " and ((a.FTRADETYPE in("+dbl.sqlString(YssOperCons.YSS_SAVING_FIRST)+
		    		","+dbl.sqlString(YssOperCons.YSS_SAVING_BUY)+") and a.FSavingType = 4 " +
		    		" and  a.fnum not in (select Distinct cc.fconsavingnum from " + 
		    		pub.yssGetTableName("TB_Cash_Consavingpriext") + " cc where Cc.FExtDate = " +
    				dbl.sqlDate(this.filterType.savingDate)+" )"+
		    		")or a.FSavingType = 2)";*/
					/**end*/
		    		
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				
				bufShow.append((rs.getString("FNum") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FCashAccCode") + "").trim())
				.append("\t");
				
				bufShow.append((rs.getString("FCashAccName") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FCuryCode") + "").trim())
				.append("\t");

				bufShow.append((rs.getString("FPortCode") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FPortName") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FSavingDate") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FMatureDate") + "").trim())
						.append("\t");
				
				bufShow.append(YssCons.YSS_LINESPLITMARK);
				
				setResultSetAttr(rs);
				
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
			}
		
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}
	
			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
	
		    return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
		    
		} catch (Exception e) {
			throw new YssException("获取存入帐户信息出错" + "\r\n" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    //--------------------------

    /**
     * add by zhangjun 2012.05.30 
     * Story #2579 RQFII需要系统定期存款可以支持滚存
     * 计算定存期间总利息
     */
    public String getListViewData3() throws YssException {
    	
    	ArrayList bondList = new ArrayList();
    	CashPecPayBean cashpecpay = null;
    	double dValue = 0;
    	StringBuffer bufDataStr = new StringBuffer();
    	String sDataStr = "";
    	
    	String strSql = "";
        boolean bTrans = false;
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
    	
        try{
           
            //先删后增
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "select FCashAccCode from " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                " where FNum=" + dbl.sqlString(this.num);
            rs = dbl.openResultSet(strSql);
        	if(rs.next())
        	{
        		//调用收益计提处理过程
           	 	BaseIncomeStatDeal incomestat = (BaseIncomeStatDeal) pub.getOperDealCtx().getBean("stataccinterest");
                incomestat.setYssPub(pub);
                //incomestat.initIncomeStat(this.savingDate, this.matureDate, this.portCode,
                		//this.SelCodes, this.modeCode,this.sOtherParams);
                
                incomestat.initIncomeStat(this.savingDate, this.matureDate, this.portCode,
                 		rs.getString("FCashAccCode"), "","");
                bondList = incomestat.getIncomes();
                for (int i = 0; i < bondList.size(); i++) {
                    cashpecpay = (CashPecPayBean) bondList.get(i);
                    dValue = dValue + cashpecpay.getMoney(); //原币利息
                    
                }
        	}        	
            this.recInterest = dValue;            
            
            bufDataStr.append(this.buildRowStr()).append(
					YssCons.YSS_LINESPLITMARK);
            if (bufDataStr.toString().length() > 2) {
            	sDataStr = bufDataStr.toString().substring(0,
            			bufDataStr.toString().length() - 2);
			}
            
        }catch (Exception e){
        	throw new YssException("计算定存期间总利息时出现异常！\r\n" + e.getMessage(), e);
        }
        return sDataStr;
    	
    	
    }

    /**
     * add by zhangjun 2012.05.30 
     * Story #2579 RQFII需要系统定期存款可以支持滚存
     * 计算定存期间总利息
     */
    public String getListViewData4() throws YssException {
    	 String strSql = "";
         String sHeader = "";
         String sShowDataStr = "";
         String sAllDataStr = "";
         ResultSet rs = null;
         StringBuffer bufShowDataStr = new StringBuffer();
         StringBuffer bufAllDataStr = new StringBuffer();
         String sAry[] = null;
         SavingOutAccBean savingOutAcc = new SavingOutAccBean();
         
         ArrayList bondList = new ArrayList();
      	CashPecPayBean cashpecpay = null;
      	double dValue = 0;        	        	
      	
         
         try {
        	 savingOutAcc.setYssPub(pub);
             boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
             boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
             //sHeader = savingOutAcc.getListView1Headers();
             sHeader = "自动编号\t现金帐户代码\t现金帐户名称\t组合名称\t所属分类\t所属分类名称\t流出金额;R\t投资经理\t品种类型\t分析配置3\t描述信息\t制作人\t制作时间\t审核人\t审核时间";
             sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Cash); //获得分析代码
             //sHeader = savingOutAcc.getListView1Headers();
             strSql ="select a.*, b.fusername as fcreatorname, c.fusername as fcheckusername," +
                     " nvl(k.FAttrClsName,' ') as FAttrClsName,d.FPortName,e.FCashAccName" + sAry[0];
             strSql = strSql + " from " +
                 pub.yssGetTableName("Tb_Cash_SavingOutAcc") + " a" +
                 " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                 " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +                
                 " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName,o.FPortCury as FPortCury from " +
                 
                 pub.yssGetTableName("Tb_Para_Portfolio") + " o where FCheckState = 1 and FASSETGROUPCODE = " + 
                 dbl.sqlString(pub.getAssetGroupCode()) +                
                 ") d on a.FPortCode = d.FPortCode" +
                 " left join (select FAttrClsCode,FAttrClsName from " +
                 pub.yssGetTableName("Tb_Para_AttributeClass") +
                 ") k on a.FAttrClsCode = k.FAttrClsCode " +               
                 " left join (" +
                 " select FCashAccCode, FCashAccName, FStartDate,FCuryCode as FCashCuryCode from " +
                 pub.yssGetTableName("Tb_Para_CashAccount") +
                 " where FCheckState = 1) e on a.FCashAccCode = e.FCashAccCode " +             
                 sAry[1] +
                 " where a.FInAccNum = " + dbl.sqlString(this.num);

             rs = dbl.openResultSet(strSql);
             
             
     		//调用收益计提处理过程
        	 	BaseIncomeStatDeal incomestat = (BaseIncomeStatDeal) pub.getOperDealCtx().getBean("stataccinterest");
             incomestat.setYssPub(pub);
             //incomestat.initIncomeStat(this.savingDate, this.matureDate, this.portCode,
             		//this.SelCodes, this.modeCode,this.sOtherParams);
             
             incomestat.initIncomeStat(this.savingDate, this.matureDate, this.portCode,
              		this.cashAccCode, "","");
             bondList = incomestat.getIncomes();
             for (int i = 0; i < bondList.size(); i++) {            	 
                 cashpecpay = (CashPecPayBean) bondList.get(i);
                 
                 if(cashpecpay.getRelaNum().equalsIgnoreCase(this.num)){
                	 dValue = dValue + cashpecpay.getMoney(); //原币利息
                 }
                 
             }
         	    	
             //this.recInterest = dValue;     
             
             
             while (rs.next()) {
                 bufShowDataStr.append( (rs.getString("FNum") + "").trim());
                 bufShowDataStr.append("\t");
                 bufShowDataStr.append( (rs.getString("FCashAccCode") + "").trim());
                 bufShowDataStr.append("\t");
                 bufShowDataStr.append( (rs.getString("FCashAccName") + "").trim());
                 bufShowDataStr.append("\t");
                 bufShowDataStr.append( (rs.getString("FPortName") + "").trim());
                 bufShowDataStr.append("\t");
                 
                 bufShowDataStr.append( (rs.getString("FATTRCLSCODE") + "").trim());
                 bufShowDataStr.append("\t");
                 bufShowDataStr.append( (rs.getString("FAttrClsName") + "").trim());
                 bufShowDataStr.append("\t");   
                 
                 if( "fixrate".equalsIgnoreCase(this.sCalcType))//固定利率
                 {
                	 bufShowDataStr.append(YssFun.formatNumber(rs.getDouble("FOutMoney") + dValue ,"#,##0.##"));
                 }else{ //固定收益
                	 bufShowDataStr.append(YssFun.formatNumber(rs.getDouble("FOutMoney") + this.recInterest ,"#,##0.##"));
                 }
                 
                 
                 bufShowDataStr.append("\t");
                 if (analy1) {
                     bufShowDataStr.append( (rs.getString("FInvMgrName") + "").trim());
                 }
                 bufShowDataStr.append("\t");
                 if (analy2) {
                     bufShowDataStr.append( (rs.getString("FCatName") + "").trim());
                 }
                 bufShowDataStr.append("\t");
                 bufShowDataStr.append("\t");
                 bufShowDataStr.append( (rs.getString("FDesc") + "").trim());
                 bufShowDataStr.append("\t");
                 bufShowDataStr.append( (rs.getString("FCreator") + "").trim());
                 bufShowDataStr.append("\t");
                 bufShowDataStr.append( (rs.getString("FCreateTime") + "").trim());
                 bufShowDataStr.append("\t");
                 bufShowDataStr.append( (rs.getString("FCheckUser") + "").trim());
                 bufShowDataStr.append("\t");
                 bufShowDataStr.append( (rs.getString("FCheckTime") + "").trim());
                 bufShowDataStr.append("\t");

                 bufShowDataStr.append(YssCons.YSS_LINESPLITMARK);
                 savingOutAcc.setResultSetAttr1(rs,dValue,recInterest,sCalcType);
                 bufAllDataStr.append(savingOutAcc.buildRowStr()).append(YssCons.
                     YSS_LINESPLITMARK);
             }

             if (bufShowDataStr.toString().length() > 2) {
                 sShowDataStr = bufShowDataStr.toString().substring(0,
                     bufShowDataStr.toString().length() - 2);
             }
             
             if (bufAllDataStr.toString().length() > 2) {
                 sAllDataStr = bufAllDataStr.toString().substring(0,
                     bufAllDataStr.toString().length() - 2);
             }
             if (rs != null) { //关闭记录集
                 dbl.closeResultSetFinal(rs);
             }
             return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
         } catch (Exception e) {
             dbl.closeResultSetFinal(rs);
             throw new YssException("获取流出帐户信息出错" + "\r\n" + e.getMessage(), e);
         }

    }
    


    public String addSetting(boolean bAutoCommit) throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {

            if (this.num.equals("")) {
                this.num = "CA" +
                    YssFun.formatDate(this.savingDate, "yyyyMMdd") +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Cash_SavingInAcc"),
                                           dbl.sqlRight("FNUM", 6),
                                           "000001");
            }
            strSql = "insert into " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                " (FNum,FCashAccCode,FDepDurCode,FSavingType,FSavingDate,FSavingTime," +
                " FMatureDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FTransNum,FInMoney,FRecInterest,FInterestAccCode," +
                //---------- sj modify 20090630 MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A ----------------------------------------------
                "FTRADETYPE,FSAVINGNUM,FIncludeInterest,FCALCTYPE,FBASICMONEY,FBASICRATE,FCirculDays,FSAVINGMONEY," +
                //-----------------------------------------------------------------------------------------------------------------------
                " FBaseCuryRate,FPortCuryRate,FAvgBaseCuryRate,FAvgPortCuryRate,FATTRCLSCODE," +//添加所属类别字段 add by jiangshichao 2010.11.22
                "FDataSource,FDesc,FFormulaCode,FRoundCode,FCheckState,FCreator,FCreateTime,Flag) values(" +   //modify by zhangjun 2012-05-29 story #2579
                dbl.sqlString(this.num) + ", " +
                dbl.sqlString(this.cashAccCode) + ", " +
                dbl.sqlString(this.depDurCode) + ", " +
                this.savingType + ", " +
                dbl.sqlDate(this.savingDate) + ", " +
                dbl.sqlString(this.savingTime) + ", " +
                dbl.sqlDate(this.matureDate) + ", " +
                dbl.sqlString(this.portCode) + ", " +
                dbl.sqlString( (this.invMgrCode == null ||
                                this.invMgrCode.equals("")) ? " " :
                              this.invMgrCode) + ", " +
                dbl.sqlString( (this.catCode == null || this.catCode.equals("")) ?
                              " " : this.catCode) + ", " +
                dbl.sqlString( (this.brokerCode == null ||
                                this.brokerCode.equals("")) ? " " :
                              this.brokerCode) + ", " +
                dbl.sqlString(this.transNum + " ") + ", " +
                // dbl.sqlString(this.strLxTransNum + " ") + ", " +
                this.inMoney + "" + "," +
                this.recInterest + "" + "," +
                dbl.sqlString(this.interestAccCode + "") + "," +
                //---- sj modify 20090630 MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A -
                dbl.sqlString(this.sTradeType) + "," +
                dbl.sqlString(this.sSaveNum) + "," +
                this.dIncludeInterest + "," +
                dbl.sqlString(this.sCalcType) + "," +
                this.dBasicMoney + "," +
                this.dBasicRate + "," +
                this.iCirculDays + "," +
                this.dSavingMoney + "," +
                //--------------------------------------------------------------------
                this.baseCuryRate + "" + ", " +
                this.portCuryRate + "" + "," +
                this.avgBaseCuryRate + "" + ", " +
                this.avgPortCuryRate + "" + "," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                (this.strAttrClsCode.trim().length()==0?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode))+","+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                this.dataSource + ", " +
                dbl.sqlString(this.desc) + ", " +
                dbl.sqlString(this.formulaCode) + ", " +
                dbl.sqlString(this.roundCode) + ", " +
                // this.checkStateId + ", " +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode + " ") + ", " +
                dbl.sqlString(this.creatorTime + " ") + ", " +
                this.strFlag + ")";  //modify by zhangjun 2012-05-29 story #2579
            if (!bAutoCommit) {
                conn.setAutoCommit(false);
                bTrans = true;
            }
            dbl.executeSql(strSql);
            if (this.strSaving != null && this.strSaving.length() != 0) {
                SavingOutAccBean outacc = new SavingOutAccBean();
                outacc.saving = new SavingBean(); //给流入帐户赋值
                outacc.saving.setNum(this.num);
                outacc.saving.setCashAccCode(this.cashAccCode);
                outacc.saving.setInMoney(this.inMoney);
                outacc.saving.setPortCode(this.portCode);
                outacc.saving.setInvMgrCode(this.invMgrCode);
                outacc.saving.setCatCode(this.catCode);
                outacc.saving.setBrokerCode(this.brokerCode);
                outacc.saving.setBaseCuryRate(this.baseCuryRate);
                outacc.saving.setPortCuryRate(this.portCuryRate);
                outacc.saving.setRecInterest(this.recInterest);
                outacc.saving.setSavingType(this.savingType);
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                outacc.saving.setStrAttrClsCode(this.strAttrClsCode);
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                outacc.setYssPub(pub);
//            if (!this.transNum.equals("")) {
                this.deleteSettlement(); //如果是有资金调拨编号 那么先把资金调拨里面的信息删掉
//            }
                outacc.saveMutliSetting(this.strSaving, true, this.num);
            }

            if (!bAutoCommit) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return null;
        } catch (Exception e) {
//         dbl.closeConnection();//QDV4南方2009年1月6日02_B MS00165 by leeyu 去掉这句关闭连接的语句
            throw new YssException("新增流入帐户信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //从资金调拨表里面删掉
    protected void deleteSettlement() throws YssException {
        boolean bTrans = false; //代表是否开始了事务
        java.sql.Connection conn = dbl.loadConnection();
        String strSql = "";
        String str = "";
        try {
            CashTransAdmin ctAdmin = new CashTransAdmin();
            ctAdmin.setYssPub(pub);
            str = ctAdmin.getTransNums("", this.num, "");

//         conn.setAutoCommit(false);
//         bTrans = true;
            if (str.length() > 0) {
                strSql = "update " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " set FCheckState= " + this.checkStateId + ",FCheckUser =" +
                    dbl.sqlString(this.checkUserCode) + " ,FCheckTime ='" +
                    YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss") +
                    "'" +
                    " where FNum in (" + operSql.sqlCodes(str) + ")";

                dbl.executeSql(strSql);

                strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " set FCheckState=" + this.checkStateId + ", FCheckUser =" +
                    dbl.sqlString(this.checkUserCode) + " ,FCheckTime ='" +
                    YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss") +
                    "'" +
                    " where FNum in (" + operSql.sqlCodes(str) + ")";

                dbl.executeSql(strSql);
            }
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("删除资金调拨信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * deleteFromDB
     * 功能：从数据库资金调拨表里面删掉
     * @throws YssException
     * 修改时间：2008年6月3号
     * 修改人：蒋春
     */
    protected void deleteFromDB() throws YssException {
        boolean bTrans = false; //代表是否开始了事务
        java.sql.Connection conn = dbl.loadConnection();
        String strSql = "";
        String str = "";
        try {
            CashTransAdmin ctAdmin = new CashTransAdmin();
            ctAdmin.setYssPub(pub);
            str = ctAdmin.getTransNums("", this.num, "");

            if (str.length() > 0) {
                strSql = "delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " where FNum in (" + operSql.sqlCodes(str) + ")";
                dbl.executeSql(strSql);

                strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FNum in (" + operSql.sqlCodes(str) + ")";
                dbl.executeSql(strSql);
            }
        } catch (Exception e) {
            throw new YssException("删除资金调拨信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //当把存款业务里面的记录审核的同时把资金调拨表里面的数据也审核掉
    protected void checkSettlement() throws YssException {
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String strSql = "";
        String str = "";
        try {
            CashTransAdmin ctAdmin = new CashTransAdmin();
            ctAdmin.setYssPub(pub);
            str = ctAdmin.getTransNums("", this.num, "");

            if (str.length() > 0) {
                strSql = "update " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " set FCheckState= " + this.checkStateId + ",FCheckUser =" +
                    dbl.sqlString(this.checkUserCode) + " ,FCheckTime ='" +
                    YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss") +
                    "'" +
                    " where FNum in (" + operSql.sqlCodes(str) + ")";
//            conn.setAutoCommit(false);
//            bTrans = true;
                dbl.executeSql(strSql);
                str = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " set FCheckState=" + this.checkStateId + ", FCheckUser =" +
                    dbl.sqlString(this.checkUserCode) + " ,FCheckTime ='" +
                    YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss") +
                    "'" +
                    " where FNum in (" + operSql.sqlCodes(str) + ")";
                dbl.executeSql(str);
//            conn.commit();
//            bTrans = false;
//            conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            throw new YssException("审核资金调拨信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String addSetting() throws YssException {
        return addSetting(false);
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Cash_SavingInAcc"),
                               "FNum", this.num, this.oldNum);
    }

    /**
     * checkSetting
     * 功能：可以处理存款业务设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     * 修改时间：2008年6月3号
     * 修改人：蒋春
     */
    public void checkSetting() throws YssException {
        //      String strSql = "";
        //      String[] arrData = null;
        //      boolean bTrans = false; //代表是否开始了事务
        //      Connection conn = dbl.loadConnection();
        //      try {
        //         conn.setAutoCommit(false);
        //         checkSettlement(); //审核掉资金调拨表里面的信息
        //         strSql = "update " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
        //               " set FCheckState = " +
        //               this.checkStateId + ", FCheckUser = " +
        //               dbl.sqlString(pub.getUserCode()) +
        //               ",FCheckTime = '" +
        //               YssFun.formatDatetime(new java.util.Date()) + "'" +
        //               "where FNum=" + dbl.sqlString(this.num);
        //         conn.setAutoCommit(false);
        //         bTrans = true;
        //         dbl.executeSql(strSql);
        //         //审核定存流出帐户信息
        //         strSql = "update " + pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
        //               " set FCheckState = " +
        //               this.checkStateId + ", FCheckUser = " +
        //               dbl.sqlString(pub.getUserCode()) +
        //               ",FCheckTime = '" +
        //               YssFun.formatDatetime(new java.util.Date()) + "'" +
        //               "where FNum=" + dbl.sqlString(this.num);
        //         dbl.executeSql(strSql);
        //         conn.commit();
        //         bTrans = false;
        //         conn.setAutoCommit(true);
        //      }
        //      catch (Exception e) {
        //         throw new YssException("审核定存业务信息出错\r\n" + e.getMessage(), e);
        //      }
        //      finally {
        //         dbl.endTransFinal(conn, bTrans);
        //      }

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
                checkSettlement(); //审核掉资金调拨表里面的信息
                //循环执行这些语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum=" + dbl.sqlString(this.num);

                    //执行sql语句
                    dbl.executeSql(strSql);

                    //审核定存流出账户信息
                    strSql = "update " + pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where finaccnum = " + dbl.sqlString(this.num); //2008-6-13 单亮 添加流出帐户的帐号作为条件

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else {
                if (this.num != "" && this.num != null) {
                    strSql = "update " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum=" + dbl.sqlString(this.num);

                    //执行sql语句
                    dbl.executeSql(strSql);

                    //审核定存流出账户信息
                    strSql = "update " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where finaccnum = " + dbl.sqlString(this.num); //2008-6-13 单亮 添加流出帐户的帐号作为条件

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            getSavingInfo();//add by songjie 2011.09.27 BUG 2783 QDV4招商基金2011年09月15日01_B
        } catch (Exception e) {
            throw new YssException("审核定存业务信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * add by songjie 
     * 2011.09.27 
     * BUG 2783 QDV4招商基金2011年09月15日01_B
     * 根据编号查询数据
     * @throws YssException
     */
    private void getSavingInfo() throws YssException{
        ResultSet rs = null;
        String strSql = "";
        String[] sAry = null;
        boolean analy1 = false;
        boolean analy2 = false;
        try
        {
			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); // 判断分析代码存不存在
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
			sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Cash); // 获得分析代码
			strSql =
        	"select a.*, b.fusername as fcreatorname, c.fusername as fcheckusername," +
            " d.FPortName,e.FCashAccName,f.FDepDurName,f.FDuration,f.FDurUnit,g.FFormulaName,h.FRoundName," +
            "l.FCashAccName as FInterstCashAccName,n.FVocName as FSavingName,nvl(k.FAttrClsName,' ') as FAttrClsName " + 
            sAry[0] + " from " + pub.yssGetTableName("Tb_Cash_SavingInAcc") + " a" +
            " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
            " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
            " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") + " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) +
            " ) d on a.FPortCode = d.FPortCode left join ( select FCashAccCode, FCashAccName, FStartDate from " +
            pub.yssGetTableName("Tb_Para_CashAccount") + " where FCheckState = 1) e on a.FCashAccCode = e.FCashAccCode " +
            " left join (select FCashAccCode, FCashAccName, FStartDate from " +
            pub.yssGetTableName("Tb_Para_CashAccount") + " where FCheckState = 1) l on a.FCashAccCode = l.FCashAccCode " +
            " left join (select FAttrClsCode,FAttrClsName from " + pub.yssGetTableName("Tb_Para_AttributeClass") +
            ") k on a.FAttrClsCode = k.FAttrClsCode left join(select FDepDurCode,FDepDurName,FDuration,FDurUnit from " +
            pub.yssGetTableName("Tb_Para_DepositDuration") + ") f on a.FDepDurCode=f.FDepDurCode " +
            " left join(select FFormulaCode,FFormulaName from " + pub.yssGetTableName("Tb_Para_Performula") +
            ") g on a.FFormulaCode=g.FFormulaCode left join(select FRoundCode,FRoundName from " +
            pub.yssGetTableName("Tb_Para_Rounding") +") h on a.FRoundCode=h.FRoundCode " + sAry[1] +
            " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FSavingType") +
            " = n.FVocCode  and n.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_CSH_SavingType) +
            " where FNum = " + dbl.sqlString(this.num);
			
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				setResultSetAttr(rs, analy1, analy2);
			}
		} catch (Exception e) {
			throw new YssException("根据编号查询存款业务数据出错！");
		} finally{
			dbl.closeResultSetFinal(rs);
		}
    }
    
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            deleteSettlement(); //删除资金调拨信息
            strSql = "update " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ",FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum=" + dbl.sqlString(this.num);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //删除定存流出账户信息
            strSql = "update " + pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ",FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where finaccnum = " + dbl.sqlString(this.num); //2008-6-13 单亮 添加流出帐户的帐号作为条件
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            getSavingInfo();//add by songjie 2011.09.27 BUG 2783 QDV4招商基金2011年09月15日01_B
        } catch (Exception e) {
            throw new YssException("删除定存业务信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //先删后增
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                " where FNum=" +
                dbl.sqlString(this.num);
            dbl.executeSql(strSql);
            this.addSetting();
            /*    if (this.strSaving != null && this.strSaving.length() != 0) {
                   SavingOutAccBean savingoutacc = new SavingOutAccBean();
                   savingoutacc.setYssPub(pub);
                   savingoutacc.saveMutliSetting(this.strSaving, true, this.num);
                }*/
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改入存帐户信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String getAllSetting() {
        return "";
    }

    public IDataSetting getSetting() {
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public String getTreeViewData1() {
        return "";
    }

    public String getTreeViewData2() {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.num).append("\t");
        buf.append(this.cashAccCode).append("\t");
        buf.append(this.cashAccName).append("\t");
        buf.append(this.interestAccCode).append("\t");
        buf.append(this.interestAccName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.invMgrCode).append("\t");
        buf.append(this.invMgrName).append("\t");
        buf.append(this.catCode).append("\t");
        buf.append(this.catName).append("\t");
        buf.append(this.brokerCode).append("\t");
        buf.append(this.brokerName).append("\t");
        buf.append(this.inMoney).append("\t");
        buf.append(this.recInterest).append("\t");
        buf.append(this.depDurCode).append("\t");
        buf.append(this.depDurName).append("\t");
        buf.append(this.savingType).append("\t");
        if (this.savingDate != null) {
            buf.append(YssFun.formatDate(this.savingDate)).append("\t");
        } else {
            buf.append("").append("\t");
        }

        buf.append(this.savingTime).append("\t");
        if (this.matureDate != null) {
            buf.append(YssFun.formatDate(this.matureDate)).append("\t");
        } else {
            buf.append("").append("\t");
        }
        buf.append(this.transNum).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(this.formulaCode).append("\t");
        buf.append(this.formulaName).append("\t");
        buf.append(this.roundCode).append("\t");
        buf.append(this.roundName).append("\t");
        buf.append(this.strIsOnlyColumns).append("\t");
        buf.append(this.strDuration).append("\t");
        buf.append(this.strDurUnit).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.avgBaseCuryRate).append("\t");
        buf.append(this.avgPortCuryRate).append("\t");
        buf.append(this.strLxTransNum).append("\t");
        buf.append(this.dataSource).append("\t");
        //------ sj modify 20090630  MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A --------
        buf.append(this.sTradeType).append("\t");
        buf.append(this.sSaveNum).append("\t");
        buf.append(this.dIncludeInterest).append("\t");
        buf.append(this.sCalcType).append("\t");
        buf.append(this.dBasicMoney).append("\t");
        buf.append(this.dBasicRate).append("\t");
        buf.append(this.iCirculDays).append("\t");
        buf.append(this.dSavingMoney).append("\t");
        //-------------------------------------------------------------------------------
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        
        buf.append(this.strFlag).append("\t");  //add by zhangjun 2012.06.15 story#2579
        
        /**shashijie 2012-7-18 STORY 2796 */
		buf.append(this.FTakeType).append("\t");
		/**end*/
        
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        double sBaseRate = 0.0;
        double sPortRate = 0.0;
        double dlxMoney = 0.0;
        String strReturn = "";
        ResultSet rs = null;
        try {
            //不同的汇率数据来源，应该区分组合货币汇率来源和基础货币汇率来源fazmm20070929
            if (sType.equals("baserate")) {
                BaseOperDeal obj = new BaseOperDeal();
                obj.setYssPub(pub);
                sBaseRate = obj.getCuryRate(this.savingDate, this.curyCode,
                                            this.portCode,
                                            YssOperCons.YSS_RATE_BASE);
                strReturn = sBaseRate + "";
            } else if (sType.equals("portrate")) {
                BaseOperDeal obj = new BaseOperDeal();
                obj.setYssPub(pub);
                sPortRate = obj.getCuryRate(this.savingDate, this.curyCode,
                                            this.portCode,
                                            YssOperCons.YSS_RATE_PORT);
                strReturn = sPortRate + "";
            } else if (sType.equals("lxMoney")) {
                dlxMoney = operFun.getCashLX(this.savingDate, this.cashAccCode,
                                             this.portCode, this.invMgrCode,
                                             this.catCode);
                strReturn = dlxMoney + "";
            } else if (sType.equalsIgnoreCase("check")) { //这里验证如果有相同的流入帐户 就提示用户是否保存
                String strSql = "select * from " +
                    pub.yssGetTableName("tb_cash_savinginacc") +
                    " where FSavingDate=" + dbl.sqlDate(this.savingDate) +
                    " and FCashAccCode =" + dbl.sqlString(this.cashAccCode) +
                    " and FPortCode= " + dbl.sqlString(this.portCode) +
                    " and FAnalysisCode1=" +
                    dbl.sqlString(this.invMgrCode) + " and FAnalysisCode2=" +
                    dbl.sqlString(this.catCode) + " and FMatureDate=" +
                    dbl.sqlDate(this.matureDate);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    strReturn = "true";
                } else {
                    strReturn = "false";
                }
            }
        } catch (Exception ex) {
            throw new YssException("获取数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return strReturn;
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.strSaving = sRowStr.split("\r\t")[2];
                }

            } else {
                sTmpStr = sRowStr;
            }
            this.sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.num = reqAry[0];
            this.cashAccCode = reqAry[1];
            this.cashAccName = reqAry[2];
            this.interestAccCode = reqAry[3];
            this.interestAccName = reqAry[4];
            this.portCode = reqAry[5];
            this.portName = reqAry[6];
            this.invMgrCode = reqAry[7];
            this.invMgrName = reqAry[8];
            this.catCode = reqAry[9];
            this.catName = reqAry[10];
            this.brokerCode = reqAry[11];
            this.brokerName = reqAry[12];
            if (YssFun.isNumeric(reqAry[13])) {
                this.inMoney = Double.parseDouble(reqAry[13]);
            }
            if (YssFun.isNumeric(reqAry[14])) {
                this.recInterest = Double.parseDouble(reqAry[14]);
            }
            this.depDurCode = reqAry[15];
            this.depDurName = reqAry[16];
            this.savingType = reqAry[17];
            if (YssFun.isDate(reqAry[18])) {
                this.savingDate = YssFun.toDate(reqAry[18]);
            }
            this.savingTime = reqAry[19];
            if (YssFun.isDate(reqAry[20])) {
                this.matureDate = YssFun.toDate(reqAry[20]);
            }
            this.transNum = reqAry[21];
            if (YssFun.isNumeric(reqAry[22])) {
                this.baseCuryRate = Double.parseDouble(reqAry[22]);
            }
            if (YssFun.isNumeric(reqAry[23])) {
                this.portCuryRate = Double.parseDouble(reqAry[23]);
            }

            this.formulaCode = reqAry[24];
            this.formulaName = reqAry[25];
            this.roundCode = reqAry[26];
            this.roundName = reqAry[27];
            this.desc = reqAry[28];
            this.strIsOnlyColumns = reqAry[29];
            this.oldNum = reqAry[30];
            if (YssFun.isNumeric(reqAry[31])) {
                this.checkStateId = Integer.parseInt(reqAry[31]);
            } else {
                this.checkStateId = 0;
            }
            this.curyCode = reqAry[32];
            this.strSubAccType = reqAry[33];
            if (YssFun.isNumeric(reqAry[34])) {
                this.avgBaseCuryRate = Double.parseDouble(reqAry[34]);
            }
            if (YssFun.isNumeric(reqAry[35])) {
                this.avgPortCuryRate = Double.parseDouble(reqAry[35]);
            }
            this.strLxTransNum = reqAry[36];
            if (YssFun.isNumeric(reqAry[37])) {
                this.dataSource = Integer.parseInt(reqAry[37]);
            } else {
                this.dataSource = 1; //为手工
            }

            if (YssFun.isDate(reqAry[38])) {
                this.endDate = YssFun.toDate(reqAry[38]);
            }
            //------- sj  modify 20090630 MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A
            this.sTradeType = reqAry[39];
            this.sSaveNum = reqAry[40];
            if (YssFun.isNumeric(reqAry[41])) {
                this.dIncludeInterest = Double.parseDouble(reqAry[41]);
            }
            this.sCalcType = reqAry[42];
            if (YssFun.isNumeric(reqAry[43])) {
                this.dBasicMoney = Double.parseDouble(reqAry[43]);
            }
            if (YssFun.isNumeric(reqAry[44])) {
                this.dBasicRate = Double.parseDouble(reqAry[44]);
            }
            if (YssFun.isNumeric(reqAry[45])) {
                this.iCirculDays = Integer.parseInt(reqAry[45]);
            }
            if (YssFun.isNumeric(reqAry[46])) {
            	//edit by songjie 2011.09.27 BUG 2647 QDV4兴业银行2011年9月05日08_B
                this.dSavingMoney =Double.parseDouble(reqAry[46]);
            }
            //----------------------------------------------------------------------
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            this.strAttrClsCode = reqAry[47];
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            
           //----add by zhangjun 2012-05-29 story #2579滚存业务----------------------
            this.strFlag = reqAry[48];
            //----add by zhangjun 2012-05-29 story #2579滚存业务----------------------
            
            /**shashijie 2012-7-18 STORY 2796*/
            this.FTakeType = reqAry[49];
			/**end*/
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new SavingBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析流入帐户信息出错\r\n" + e.getMessage(), e);
        }
    }

    public String getBeforeEditData() {
        return "";
    }

    /**
     * deleteRecycleData
     * 功能：从数据库彻底删除数据
     * @throws YssException
     * 时间：2008年6月3号
     * 修改人：蒋春
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
                this.deleteFromDB();
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("tb_cash_savinginacc") +
                        " where FNum = " +
                        dbl.sqlString(this.num);
                    //执行sql语句
                    dbl.executeSql(strSql);

                    //删除定存流出账户信息
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
                        " where finaccnum = " + dbl.sqlString(this.num); //2008-6-13 单亮 添加流出帐户的帐号作为条件
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else {
                if (this.num != "" && this.num != null) {
                    strSql = "delete from " +
                        pub.yssGetTableName("tb_cash_savinginacc") +
                        " where FNum = " +
                        dbl.sqlString(this.num);
                    //执行sql语句
                    dbl.executeSql(strSql);

                    //删除定存流出账户信息
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
                        " where finaccnum = " + dbl.sqlString(this.num); //2008-6-13 单亮 添加流出帐户的帐号作为条件
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
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

	/**返回 fTakeType 的值*/
	public String getFTakeType() {
		return FTakeType;
	}

	/**传入fTakeType 设置  fTakeType 的值*/
	public void setFTakeType(String fTakeType) {
		FTakeType = fTakeType;
	}
}
