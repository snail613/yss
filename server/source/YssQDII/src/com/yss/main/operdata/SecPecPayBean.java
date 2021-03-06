package com.yss.main.operdata;

import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.manager.SecRecPayAdmin;

/**
 *
 * <p>Title: </p>
 * <p>Description:应收应付 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SecPecPayBean
    extends BaseDataSettingBean implements IDataSetting {

    private SecPecPayBean filterType;
    private String strNum= "";              //编号
    private String invMgrCode= "";          //投资经理代码
    private String invMgrCodeName = ""; //投资经理名称
    private String brokerCodeName = ""; //券商代码名称
    private String exchangeName = "";   //交易名称
    private String strPortCode= "";         //组合代码
    private String strPortName = "";    //组合名称

    // MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090521
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    private boolean bOverGroup = false; //是否为跨组合群，用于收益支付中修改界面加载出债券利息 panjunfang add 20090815,MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    //--------------------------------------------------------------------------

    private String brokerCode= "";              //券商代码
    private String exchangeCode= "";            //交易代码
    private String strSecurityCode= "";         //证券代码
    private String strSecurityName = "";    //证券名称
    private String strCuryCode;             //货币代码
    private String strCuryName = "";        //交易货币名称
    private int iInOutType = 1;             //流入流出方向,默认为正方向
    private java.util.Date transDate;       //业务日期

    private double baseCuryMoney;   //基础货币金额
    private double baseCuryRate;    //基础汇率
    private double money;           //金额
    private double mMoney;
    private double vMoney;
    private double mBaseCuryMoney;
    private double vBaseCuryMoney;
    private double mPortCuryMoney;
    private double vPortCuryMoney;

    //-----------2008.11.13 蒋锦 添加-------------//
    //储存保留8位小数的原币，基础货币，本位币金额
    //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
    private double moneyF;
    private double baseCuryMoneyF;
    private double portCuryMoneyF;
    //-------------------------------------------//

    private double portCuryRate;        //组合汇率
    private double portCuryMoney;       //组合货币金额
    private String strTsfTypeCode= "";      //调拨类型
    private String strTsfTypeName= "";      //调拨类型名称
    private String strSubTsfTypeCode= "";   //调拨子类型
    private String strSubTsfTypeName= "";   //调拨子类型名称

    private double amount;          //数量
    private double per100;          //百元利息
    private double rateBaseIncome;  //基础货币汇兑损益
    private double ratePortIncome;  //组合货币汇兑损益
    private double valuationAdded;  //估值增值
    private double mktPrice = 0;    //行情价格 yujx 20070226

//    private String isOnlyColumns = "0";  //在初始登陆时是否只显示列，不查询数据
    private java.util.Date startDate;       //起始日期
    private java.util.Date endDate;         //终止日期

    private String attrClsCode = "";    //所属分类 sj add 20071202
    private String attrClsName = "";
    private String catTypeCode = "";    //品种类型
    private String catTypeName = "";
    private String desc = "";           //添加描述  单亮  2008-4-22
    private String sRecycled = "";      //保存未解析前的字符串 单亮  2008-5-19
    private String multAuditString = "";//批量处理数据 linjunyun 2008-11-28 bug:MS00029

    //--------2009.06.29 蒋锦 添加 国内基金业务 关联编号 关联编号类型---------//
    //MS00013   QDV4.1赢时胜（上海）2009年4月20日13_A
    private String relaNum;
    private String relaNumType = null;
    //------------------------------------------------------------------//

    //------ 2009-08-14 add by wangzuochun MS00024 交易数据拆分 -----//
    private String investType = ""; //投资类型
    //------ End MS00024 QDV4.1赢时胜（上海）2009年4月20日24_A--------//

    private java.util.Date OldTransDate;

    private String strOldPortCode = "";

    private String strOldSecurityCode = "";

    private String strOldFAnalysisCode1 = "";

    private String strOldFAnalysisCode2 = "";

    private String strOldFAnalysisCode3 = "";
    private String strOldNum = "";
    //=========MS00902,add by xuxuming,20100104.增加入账标识字段========
  //=========MS00902,估值增值和汇兑损益未转到新的库存中去    QDV4国泰2010年1月5日01_B====
    private int strFStockInd = 0;
    private int dataSource =0; //add by jiangshichao 
	private SingleLogOper logOper;
	
	
    public int getDataSource() {
		return dataSource;
	}
	public void setDataSource(int dataSource) {
		this.dataSource = dataSource;
	}
	
	public int getStrFStockInd(){
    	return this.strFStockInd;
    }
    public void setStrFStockInd(int strFStockInd){
    	this.strFStockInd=strFStockInd;
    }
    //================end.=====================================
    public int getCheckState() {
        return this.checkStateId;
    }

    public void setCheckState(int checkState) {
        checkStateId = checkState;
    }

    public String getStrNum() {
        return strNum;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setStrNum(String strNum) {
        this.strNum = strNum;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupCode = assetGroupName;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public String getStrCuryCode() {
        return strCuryCode;
    }

    public void setStrCuryCode(String strCuryCode) {
        this.strCuryCode = strCuryCode;
    }

    public String getStrTsfTypeCode() {
        return strTsfTypeCode;
    }

    public void setStrTsfTypeCode(String strTsfTypeCode) {
        this.strTsfTypeCode = strTsfTypeCode;
    }

    public String getStrSubTsfTypeCode() {
        return strSubTsfTypeCode;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public String getInvMgrCodeName() {
        return invMgrCodeName;
    }

    public String getBrokerCodeName() {
        return brokerCodeName;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public double getMoney() {
        return money;
    }

    public double getBaseCuryMoney() {
        return baseCuryMoney;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public double getPortCuryMoney() {
        return portCuryMoney;
    }

    public double getMktPrice() {
        return this.mktPrice;
    }

    public void setMktPrice(double price) {
        this.mktPrice = price;
    }

    public Date getTransDate() {
        return transDate;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

//    public String getisOnlyColumn() {
//        return isOnlyColumns;
//    }

    public double getAmount() {
        return amount;
    }

    public double getPer100() {
        return per100;
    }

    public double getMMoney() {
        return mMoney;
    }

    public double getMBaseCuryMoney() {
        return mBaseCuryMoney;
    }

    public double getMPortCuryMoney() {
        return mPortCuryMoney;
    }

    public double getVPortCuryMoney() {
        return vPortCuryMoney;
    }

    public double getVMoney() {
        return vMoney;
    }

    public double getVBaseCuryMoney() {
        return vBaseCuryMoney;
    }

    public double getValuationAdded() {
        return valuationAdded;
    }

    public double getRatePortIncome() {
        return ratePortIncome;
    }

    public double getRateBaseIncome() {
        return rateBaseIncome;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getOldTransDate() {
        return OldTransDate;
    }

    public String getStrOldPortCode() {
        return strOldPortCode;
    }

    public String getStrTsfTypeName() {
        return strTsfTypeName;
    }

    public String getStrOldFAnalysisCode3() {
        return strOldFAnalysisCode3;
    }

    public String getStrOldNum() {
        return strOldNum;
    }

    public SecPecPayBean getFilterType() {
        return filterType;
    }

    public String getStrPortName() {
        return strPortName;
    }

    public String getStrSubTsfTypeName() {
        return strSubTsfTypeName;
    }

    public String getStrSecurityName() {
        return strSecurityName;
    }

    public String getStrCuryName() {
        return strCuryName;
    }

    public String getStrOldSecurityCode() {
        return strOldSecurityCode;
    }

    public String getStrOldFAnalysisCode1() {
        return strOldFAnalysisCode1;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStrOldFAnalysisCode2() {
        return strOldFAnalysisCode2;
    }

    public String getAttrClsName() {
        return attrClsName;
    }

    public String getCatTypeCode() {
        return catTypeCode;
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    public String getCatTypeName() {
        return catTypeName;
    }

    public double getBaseCuryMoneyF() {
        return baseCuryMoneyF;
    }

    public double getMoneyF() {
        return moneyF;
    }

    public double getPortCuryMoneyF() {
        return portCuryMoneyF;
    }

    public String getRelaNum() {
        return relaNum;
    }

    public String getRelaNumType() {
        return relaNumType;
    }

    public String getInvestType() {
        return investType;
    }

    public int getInOutType() {
        return iInOutType;
    }

    public void setStrSubTsfTypeCode(String strSubTsfTypeCode) {
        this.strSubTsfTypeCode = strSubTsfTypeCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setInvMgrCodeName(String invMgrCodeName) {
        this.invMgrCodeName = invMgrCodeName;
    }

    public void setBrokerCodeName(String brokerCodeName) {
        this.brokerCodeName = brokerCodeName;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setBaseCuryMoney(double baseCuryMoney) {
        this.baseCuryMoney = baseCuryMoney;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setPortCuryMoney(double portCuryMoney) {
        this.portCuryMoney = portCuryMoney;
    }

    public void setTransDate(java.util.Date transDate) {
        this.transDate = transDate;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

//    public void setisOnlyColumn(String isOnlyColumns) {
//        this.isOnlyColumns = isOnlyColumns;
//    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPer100(double per100) {
        this.per100 = per100;
    }

    public void setMMoney(double mMoney) {
        this.mMoney = mMoney;
    }

    public void setMBaseCuryMoney(double mBaseCuryMoney) {
        this.mBaseCuryMoney = mBaseCuryMoney;
    }

    public void setMPortCuryMoney(double mPortCuryMoney) {
        this.mPortCuryMoney = mPortCuryMoney;
    }

    public void setVPortCuryMoney(double vPortCuryMoney) {
        this.vPortCuryMoney = vPortCuryMoney;
    }

    public void setVMoney(double vMoney) {
        this.vMoney = vMoney;
    }

    public void setVBaseCuryMoney(double vBaseCuryMoney) {
        this.vBaseCuryMoney = vBaseCuryMoney;
    }

    public void setValuationAdded(double valuationAdded) {
        this.valuationAdded = valuationAdded;
    }

    public void setRatePortIncome(double ratePortIncome) {
        this.ratePortIncome = ratePortIncome;
    }

    public void setRateBaseIncome(double rateBaseIncome) {
        this.rateBaseIncome = rateBaseIncome;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setOldTransDate(Date OldTransDate) {
        this.OldTransDate = OldTransDate;
    }

    public void setStrOldPortCode(String strOldPortCode) {
        this.strOldPortCode = strOldPortCode;
    }

    public void setStrTsfTypeName(String strTsfTypeName) {
        this.strTsfTypeName = strTsfTypeName;
    }

    public void setStrOldFAnalysisCode3(String strOldFAnalysisCode3) {
        this.strOldFAnalysisCode3 = strOldFAnalysisCode3;
    }

    public void setStrOldNum(String strOldNum) {
        this.strOldNum = strOldNum;
    }

    public void setFilterType(SecPecPayBean filterType) {
        this.filterType = filterType;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public void setStrSubTsfTypeName(String strSubTsfTypeName) {
        this.strSubTsfTypeName = strSubTsfTypeName;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setStrCuryName(String strCuryName) {
        this.strCuryName = strCuryName;
    }

    public void setStrOldSecurityCode(String strOldSecurityCode) {
        this.strOldSecurityCode = strOldSecurityCode;
    }

    public void setStrOldFAnalysisCode1(String strOldFAnalysisCode1) {
        this.strOldFAnalysisCode1 = strOldFAnalysisCode1;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStrOldFAnalysisCode2(String strOldFAnalysisCode2) {
        this.strOldFAnalysisCode2 = strOldFAnalysisCode2;
    }

    public void setAttrClsName(String attrClsName) {
        this.attrClsName = attrClsName;
    }

    public void setCatTypeCode(String catTypeCode) {
        this.catTypeCode = catTypeCode;
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public void setCatTypeName(String catTypeName) {
        this.catTypeName = catTypeName;
    }

    public void setBaseCuryMoneyF(double baseCuryMoneyF) {
        this.baseCuryMoneyF = baseCuryMoneyF;
    }

    public void setMoneyF(double moneyF) {
        this.moneyF = moneyF;
    }

    public void setPortCuryMoneyF(double portCuryMoneyF) {
        this.portCuryMoneyF = portCuryMoneyF;
    }

    public void setRelaNum(String relaNum) {
        this.relaNum = relaNum;
    }

    public void setRelaNumType(String relaNumType) {
        this.relaNumType = relaNumType;
    }

    public void setInvestType(String investType) {
        this.investType = investType;
    }

    public void setInOutType(int iInOutType) {
        this.iInOutType = iInOutType;
    }

    public SecPecPayBean() {
    }

    /**
     * addOperData
     *新增应收应付信息
     * @return String
     */
    public String addSetting() throws YssException {

        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String strNumberDate = "";
        //add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
        SecRecPayAdmin secRecPayAdmin = new SecRecPayAdmin();
        try {
        	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//            //将自动生成编号的方法放到这里 by ly 080221
//            if (this.strNum.trim().length() == 0) { //这里加上判断,防止有多次操作时每次都取.
//                strNumberDate = YssFun.formatDate(this.transDate, "yyyyMMdd").
//                    substring(0, 8);
//                this.strNum = strNumberDate +
//                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_SecRecPay"),
//                                           dbl.sqlRight("FNum", 9),
//                                           "000000001",
//                                           " where Ftransdate =" + dbl.sqlDate(this.transDate));
//                this.strNum = "SRP" + this.strNum;
//            }
        	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        	
        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        	secRecPayAdmin.setYssPub(pub);
        	this.strNum = secRecPayAdmin.getKeyNum();
        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        	
        	strSql = "insert into " + pub.yssGetTableName("Tb_Data_SecRecPay") +
                "(FNum,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FSecurityCode,FCuryCode ,FDataSource,FStockInd"
                + ",FTsfTypeCode,FSubTsfTypeCode ,FBaseCuryRate,FPortCuryRate,FMoney ,FMMoney,FVMoney,FBaseCuryMoney,FMBaseCuryMoney,FVBaseCuryMoney, FPortCuryMoney,FMPortCuryMoney,FVPortCuryMoney,FCatType,FAttrClsCode" //add 所属分类,品种类型 sj 20071202
                //edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin
                + ",FCheckState,FCreator,FCreateTime,FCheckUser,FInOut,fdesc,FInvestType,FRelaType, FDataOrigin)" + // 添加字段FInvestType, modify by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                																				//添加字段 FRelaType modify by wangzuochun 2010.08.02  MS01520    综合业务中维护换股数据时，证券应收应付数据经库存统计后被删除    QDV4赢时胜上海2010年07月30日01_B   
                " values(" +
                dbl.sqlString(this.strNum) + "," +

                dbl.sqlDate(this.transDate) + "," +
                dbl.sqlString(this.strPortCode) + "," +
                dbl.sqlString(this.invMgrCode.length() == 0 ? " " : this.invMgrCode) + "," +
                dbl.sqlString(this.brokerCode.length() == 0 ? " " : this.brokerCode) + "," +
                dbl.sqlString(this.exchangeCode.length() == 0 ? " " : this.exchangeCode) + "," +
                dbl.sqlString(this.strSecurityCode) + "," +
                dbl.sqlString(this.strCuryCode) + "," +
                1 + "," + //为手工
//                0 + "," + //未入帐                                           //edit by xuxuming,2010.01.18.MS00917    分组估值需求    ;因为之前默认插入’0’，不明白之前为何在此处写死。现在需要插入其它类型标识，怕影响以前的程序，故改为下面的方式
                //可以考虑直接改为：this.strFStockInd+","+
                (this.strFStockInd!=9&&this.strFStockInd!=-1?0:this.strFStockInd)+","+//MS00917 edit by xuxuming,2010.01.04.增加9类型的入账标识，在估值时不删除这类记录
                dbl.sqlString(this.strTsfTypeCode) + "," +
                dbl.sqlString(this.strSubTsfTypeCode) + "," +
                this.baseCuryRate + "," +
                portCuryRate + "," +
                this.money + "," +
                this.mMoney + "," +
                this.vMoney + "," +
                baseCuryMoney + "," +
                mBaseCuryMoney + "," +
                vBaseCuryMoney + "," +
                portCuryMoney + "," +
                mPortCuryMoney + "," +
                vPortCuryMoney + "," +
                (this.catTypeCode.length() > 0 ? dbl.sqlString(this.catTypeCode) :
                 dbl.sqlString(" ")) + "," +
                (this.attrClsCode.length() > 0 ? dbl.sqlString(this.attrClsCode) :
                 dbl.sqlString(" ")) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) +
                ", " + dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," +
                this.iInOutType + "," + dbl.sqlString(this.desc) + //新增字段
                //------ add by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -------//
                "," +
                //edit by songjie 2010.09.20 若投资类型为空，则插入证券应收应付表报错，所以现改为若投资类型为空，则插入默认值'C'
                dbl.sqlString(this.investType.equals("")? "C" : this.investType) + //投资类型
                //--------------------------------------------------------------------------------------------------//
                "," +//添加字段 FRelaType modify by wangzuochun 2010.08.02  MS01520    综合业务中维护换股数据时，证券应收应付数据经库存统计后被删除    QDV4赢时胜上海2010年07月30日01_B   
                (this.relaNumType == null ? null :  dbl.sqlString(this.relaNumType)) + 
                //edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B  FDataOrigin = 1 表示为手工录入的数据
                ",1)";

            conn.setAutoCommit(false);
            System.out.println(this.transDate.toString());
            System.out.println("增加的语句为:" + strSql);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------ add by wangzuochun 2010.09.10  MS01695    TA尾差调整报表在调整现金汇兑损益时有问题    QDV4赢时胜上海2010年9月3日01_B    
			YssGlobal.hmSecRecNums.put(this.transDate,this.strNum);
			//-------------------- MS01695 ------------------------//
            return buildRowStr();

        } catch (Exception ex) {
            throw new YssException("新增应收应付信息出错", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strNum.trim()).append("\t");
        buf.append(YssFun.formatDate(this.transDate).trim()).append("\t");
        buf.append(this.strPortCode.trim()).append("\t");
        buf.append(this.strPortName.trim()).append("\t");
        buf.append(this.invMgrCode.trim()).append("\t");
        buf.append(this.invMgrCodeName.trim()).append("\t");
        buf.append(this.brokerCode.trim()).append("\t");
        buf.append(this.brokerCodeName.trim()).append("\t");
        buf.append(this.exchangeCode.trim()).append("\t");
        buf.append(this.exchangeName.trim()).append("\t");
        buf.append(this.strSecurityCode.trim()).append("\t");
        buf.append(this.strSecurityName.trim()).append("\t");
        buf.append(this.strCuryCode.trim()).append("\t");
        buf.append(this.strCuryName.trim()).append("\t");
        buf.append(this.strTsfTypeCode.trim()).append("\t");
        buf.append(this.strTsfTypeName.trim()).append("\t");
        buf.append(this.strSubTsfTypeCode.trim()).append("\t");
        buf.append(this.strSubTsfTypeName.trim()).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(this.money).append("\t");
        buf.append(this.mMoney).append("\t");
        buf.append(this.vMoney).append("\t");

        buf.append(this.baseCuryMoney).append("\t");
        buf.append(this.mBaseCuryMoney).append("\t");
        buf.append(this.vBaseCuryMoney).append("\t");

        buf.append(this.portCuryMoney).append("\t");
        buf.append(this.mPortCuryMoney).append("\t");
        buf.append(this.vPortCuryMoney).append("\t");
        buf.append(this.attrClsCode).append("\t");
        buf.append(this.attrClsName).append("\t");
        buf.append(this.catTypeCode).append("\t");
        buf.append(this.catTypeName).append("\t");
        buf.append(this.iInOutType).append("\t");
        buf.append(this.desc).append("\t"); //  2008-4-23  单亮  添加描述属性

        //MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
        buf.append(this.assetGroupCode).append("\t");
        buf.append(this.assetGroupName).append("\t");
        //======== End MS00001 panjunfang add 20090522 ======

        //=== 添加投资类型  交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A ===
        buf.append(this.investType).append("\t");
        //=========== End MS00024 add by wangzuochun 2009.08.14 ============

        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     *检查输入的是否正确
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_data_SecRecPay"), "FNum,FSecurityCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FTransDate",
                               this.strNum + "," + this.strSecurityCode + "," +
                               this.strPortCode +
                               "," + this.invMgrCode + "," +
                               this.brokerCode + "," +
                               this.exchangeCode + "," +
                               YssFun.formatDate(this.transDate),
                               this.strOldNum + "," +
                               this.strOldSecurityCode + "," +
                               this.strOldPortCode + "," +
                               this.strOldFAnalysisCode1 + "," +
                               this.strOldFAnalysisCode2 + "," +
                               this.strOldFAnalysisCode3 + "," +
                               YssFun.formatDate(OldTransDate));
    }

    /**
     * 修改时间：2008年5月19号
     * 修改人：单亮
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改后的代码
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

                    strSql = "update " + pub.yssGetTableName("Tb_data_SecRecPay") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FNum = " + dbl.sqlString(this.strNum) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FTransDate=" + dbl.sqlDate(this.transDate);
                    System.out.println("审核的sql语句:" + strSql);
                    conn.setAutoCommit(false);
                    bTrans = true;
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核应收应付信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delOperData
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_data_SecRecPay") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = " + dbl.sqlString(filterType.strOldNum);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除证券应收应付信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * editOperData
     *
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String nowNum = "";
        //--- delete by songjie 2013.03.19 BUG 7338 QDV4建信基金2013年03月19日01_B start---//
//        String oldDateStr = "";
//        String nowNumDate = "";
        //--- delete by songjie 2013.03.19 BUG 7338 QDV4建信基金2013年03月19日01_B end---//
        try {
        	//--- delete by songjie 2013.03.19 BUG 7338 QDV4建信基金2013年03月19日01_B start---//
//            if (strOldNum.trim().length() > 0) { //bug 000499
//                oldDateStr = this.strOldNum.substring(3, 11);
//                oldDateStr = oldDateStr.substring(0, 4) + "-" +
//                    oldDateStr.substring(4, 6) +
//                    "-" + oldDateStr.substring(6, oldDateStr.length()); //格式化日期值。用以获取Date
//            }
//            if (YssFun.dateDiff(YssFun.toDate(oldDateStr), this.transDate) != 0) {
//                nowNumDate = YssFun.formatDate(this.transDate, "yyyyMMdd").
//                    substring(0, 8);
//                nowNum = nowNumDate +
//                    dbFun.getNextInnerCode(pub.yssGetTableName(
//                    "Tb_Data_SecRecPay"),
//                                           dbl.sqlRight("FNum", 9),
//                                           "000000001",
//                                           " where Ftransdate =" +
//                                           dbl.sqlDate(this.transDate)
//                                           );
//                nowNum = "SRP" + nowNum;
//
//            }
        	//--- delete by songjie 2013.03.19 BUG 7338 QDV4建信基金2013年03月19日01_B end---//
            //add by songjie 2013.03.19 BUG 7338 QDV4建信基金2013年03月19日01_B
            nowNum = strOldNum;
            
            //======BUG:000499
            strSql = "update " + pub.yssGetTableName("Tb_Data_SecRecPay") +
                " set FPortCode=" +
                dbl.sqlString(this.strPortCode) + ", FAnalysisCode1=" +
                dbl.sqlString(this.invMgrCode.length() == 0 ? " " :
                              this.invMgrCode) + ", FAnalysisCode2=" +
                dbl.sqlString(this.brokerCode.length() == 0 ? " " :
                              this.brokerCode) + ", FAnalysisCode3=" +
                dbl.sqlString(this.exchangeCode.length() == 0 ? " " :
                              this.exchangeCode) + ", FSecurityCode=" +
                dbl.sqlString(this.strSecurityCode) + ", FCuryCode=" +
                dbl.sqlString(this.strCuryCode) + ", FTsfTypeCode=" +
                dbl.sqlString(this.strTsfTypeCode) + ", FSubTsfTypeCode=" +
                dbl.sqlString(this.strSubTsfTypeCode) + ", FBaseCuryRate=" +
                this.baseCuryRate + ", FPortCuryRate=" +
                portCuryRate + ", FMoney=" +
                money + ", FMMoney=" +
                mMoney + ", FVMoney=" +
                vMoney + ", FBaseCuryMoney=" +
                baseCuryMoney + ", FMBaseCuryMoney=" + //
                mBaseCuryMoney + ", FVBaseCuryMoney=" +
                vBaseCuryMoney + ", FPortCuryMoney=" +
                portCuryMoney + ", FMPortCuryMoney=" +
                mPortCuryMoney + ", FVPortCuryMoney=" +
                vPortCuryMoney + ",FCatType = " +
                (catTypeCode.length() > 0 ? dbl.sqlString(catTypeCode) :
                 dbl.sqlString(" ")) + ",FAttrClsCode = " +
                (attrClsCode.length() > 0 ? dbl.sqlString(attrClsCode) :
                 dbl.sqlString(" ")) + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + " , FTransDate = "
                + dbl.sqlDate(this.transDate) + " , FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                " ,FInOut=" + this.iInOutType +
                " ,FDesc=" + dbl.sqlString(this.desc) +
                //------ add by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -------//
                ",FInvestType = " + dbl.sqlString(this.investType) +
                //-------------------------------------------------------------------------------------------------------//
                (nowNum.length() > 0 ? ",FNum = " + dbl.sqlString(nowNum) : "") + //===BUG:000499
                //add by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 
                ",FDataOrigin = 1 " + //FDataOrigin = 1表示为手工录入
                " where FNum = " +
                dbl.sqlString(this.strOldNum);

            conn.setAutoCommit(false);
            System.out.println("修改的语句为:" + strSql);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();

        } catch (Exception ex) {
            throw new YssException("修改应收应付信息出错", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 获取辅助字段之查询Sql语句
     * @return String
     */
    private String SecPecPayAnalysis() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        //2009.01.14 蒋锦 添加 异常处理
        try {
            strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
                pub.yssGetTableName("Tb_Para_StorageCfg") +
                " where FCheckState = 1 and FStorageType = " +
                dbl.sqlString(YssOperCons.YSS_KCLX_Security);

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                for (int i = 1; i <= 3; i++) {
                    if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                        rs.getString("FAnalysisCode" + String.valueOf(i)).
                        equalsIgnoreCase("002")) {
                        sResult = sResult +
                        //edit by yanghaiming 20100913 券商设置已去掉启用日期  MS01700 QDV411建行2010年09月07日01_A 
//                            " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
//                            i +
//                            " from  (select FBrokerCode,max(FStartDate) as FStartDate  from " +
//                            pub.yssGetTableName("tb_para_broker") +
//                            " where FStartDate < " +
//                            dbl.sqlDate(new java.util.Date()) +
//                            " and FCheckState = 1 group by FBrokerCode )x " +
//                            " join (select * from " +
//                            pub.yssGetTableName("tb_para_broker") + ") y on x.FBrokerCode = y.FBrokerCode and x.FStartDate = y.FStartDate) broker on a.FAnalysisCode" +
//                            i + " = broker.FBrokerCode";
                        	 " left join (select FBrokerCode,FBrokerName  as FAnalysisName" +
                        	 i +
                        	 " from " + pub.yssGetTableName("tb_para_broker") +
                        	 " where FCheckState = 1) broker on a.FAnalysisCode" +
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
                        //edit by yanghaiming 20100913 投资经理设置已去掉启用日期   MS01700 QDV411建行2010年09月07日01_A 
//                            " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
//                            i +
//                            "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
//                            pub.yssGetTableName("tb_para_investmanager") +
//                            " where FStartDate < " +
//                            dbl.sqlDate(new java.util.Date()) +
//                            " and FCheckState = 1 group by FInvMgrCode )m " +
//                            "join (select * from " +
//                            pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr on a.FAnalysisCode" +
//                            i + " = invmgr.FInvMgrCode";
                        	" left join (select FInvMgrCode ,FInvMgrName as FAnalysisName" +
                        	i +
                        	" from " + pub.yssGetTableName("tb_para_investmanager") +
                        	" where FCheckState = 1) invmgr on a.FAnalysisCode" +
                        	i + " = invmgr.FInvMgrCode";
                    } else {
                        sResult = sResult +
                            " left join (select ' ' as FAnalysisNull , ' ' as FAnalysisName" +//调整为有空格的字段，防止在创建分页表时报错 by leeyu 20100813 合并太平版本时调整
                            i + " from  " +
                            pub.yssGetTableName("Tb_Para_StorageCfg") +
                            " where 1=2) tn" + i + " on a.FAnalysisCode" + i +
                            " = tn" +
                            i + ".FAnalysisNull ";
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            //2009.01.08 蒋锦 关掉记录集
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    private String buildFilterSql() throws YssException {
        return buildFilterSql(true);
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql(boolean bWhereSql) throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            if (bWhereSql) {
                sResult = " where 1=1";
            }
            if (this.filterType.isOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
         if (this.filterType.strNum!=null&&this.filterType.strNum.trim().length() != 0) {//edit by xuxuming,20090921.MS00700
                if (filterType.strNum.indexOf(",") > 0) { //这里要用到编号集 by ly 080203
                    sResult = sResult + " and a.FNum in (" +
                        operSql.sqlCodes(filterType.strNum) + ")";
                } else {
                    sResult = sResult + " and a.FNum like '" +
                        filterType.strNum.replaceAll("'", "''") + "%'";
                }
            }

         if (this.filterType.invMgrCode!=null&&this.filterType.invMgrCode.trim().length() != 0) {//edit by xuxuming,20090921.MS00700
                sResult = sResult + " and a.FAnalysisCode1 like '" +
                    filterType.invMgrCode.replaceAll("'", "''") + "%'";
            }
         if (this.filterType.brokerCode!=null&&this.filterType.brokerCode.trim().length() != 0) {//edit by xuxuming,20090921.MS00700
                sResult = sResult + " and a.FAnalysisCode2 like '" +
                    filterType.brokerCode.replaceAll("'", "''") + "%'";
            }
         if (this.filterType.exchangeCode!=null&&this.filterType.exchangeCode.trim().length() != 0) {//edit by xuxuming,20090921.MS00700
                sResult = sResult + " and a.FAnalysisCode3 like '" +
                    filterType.exchangeCode.replaceAll("'", "''") + "%'";
            }
         if (this.filterType.strPortCode!=null&&this.filterType.strPortCode.trim().length() != 0) {//edit by xuxuming,20090921.MS00700
                if (filterType.strPortCode.split(",").length > 0) { // by leeyu 这里有可能要传入一个组合集进来
                    sResult = sResult + " and a.FPortCode in(" +
                        operSql.sqlCodes(filterType.strPortCode) + ")";
                } else {
                    sResult = sResult + " and a.FPortCode like '" +
                        filterType.strPortCode.replaceAll("'", "''") + "%'";
                }
            }

            if (this.filterType.strSecurityCode != null && this.filterType.strSecurityCode.trim().length() != 0) { //edit by xuxuming,20090921.MS00700
                if (filterType.strSecurityCode.split(",").length > 0) {
                    sResult = sResult + " and a.FSecurityCode in(" +
                        operSql.sqlCodes(filterType.strSecurityCode) + ")";
                } else {
                    sResult = sResult + " and a.FSecurityCode = " +
                        dbl.sqlString(filterType.strSecurityCode);
                }
            }

            if (this.filterType.strCuryCode!=null&&!"".equals(this.filterType.strCuryCode.trim())) {//edit by xuxuming,20090929.MS00700.加上了非空判断，否则会报空指针异常
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.strCuryCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.attrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.attrClsCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.catTypeCode.length() != 0) {
                sResult = sResult + " and a.FCatType like '" +
                    filterType.catTypeCode.replaceAll("'", "''") + "%'";
            }
            // edit by xuxuming,20090921.MS00700,加上非空判断
            if (this.filterType.strTsfTypeCode!=null&&!"".equals(this.filterType.strTsfTypeCode.trim())) { //sj edit 20071118 FTsfTypeCode replace to a.FTsfTypeCode
                if (this.filterType.strTsfTypeCode.indexOf(",") > 0) {
                    sResult += " and a.FTsfTypeCode in (" +
                        operSql.sqlCodes(this.filterType.strTsfTypeCode) + ")";
                } else {
                    sResult += " and a.FTsfTypeCode = " +
                        dbl.sqlString(this.filterType.strTsfTypeCode);
                }

            }
            // edit by xuxuming,20090921.MS00700,加上非空判断
            if (this.filterType.strSubTsfTypeCode!=null&&!"".equals(this.filterType.strSubTsfTypeCode.trim())) { //sj edit 20071118 FSubTsfTypeCode replace to a.FSubTsfTypeCode
                if (this.filterType.strSubTsfTypeCode.indexOf(",") > 0) {
                    sResult += " and a.FSubTsfTypeCode in (" +
                        operSql.sqlCodes(this.filterType.strSubTsfTypeCode) + ")";
                } else if (this.filterType.strSubTsfTypeCode.indexOf("%") > 0) {
                    sResult += " and a.FSubTsfTypeCode like " +
                        operSql.sqlCodes(this.filterType.strSubTsfTypeCode);
                } else {
                    sResult += " and a.FSubTsfTypeCode = " +
                        dbl.sqlString(this.filterType.strSubTsfTypeCode);
                }
            }

            //BugNo:0000316 edit by jc
            if (this.filterType.startDate != null &&
                this.filterType.startDate.toString().length() > 0 &&
                this.filterType.endDate != null &&
                this.filterType.endDate.toString().length() > 0) {
                if (this.filterType.startDate.before(this.filterType.endDate)) {
                    sResult = sResult + " and a.FTransDate between " +
                        dbl.sqlDate(this.filterType.startDate) +
                        " and " +
                        dbl.sqlDate(this.filterType.endDate);
                } else {
                    sResult = sResult + " and a.FTransDate between " +
                        dbl.sqlDate(this.filterType.endDate) +
                        " and " +
                        dbl.sqlDate(this.filterType.startDate);
                }
            }
            //----------------------jc
            //==add by xuxuming,20090921.MS00700=========
         else if (this.filterType.transDate != null &&
             !"".equals(this.filterType.transDate.toString().trim()) &&
             !YssFun.formatDate(this.filterType.transDate).toString().equals(
                   "9998-12-31")) {
            sResult = sResult + " and a.FTransDate = " +
                  dbl.sqlDate(filterType.transDate);
         }
         //=====================================================

            if(this.filterType.strFStockInd!=0){//默认值 为0.0表示未入账，1表示入账。这两种情形都不会以此标识来查询。9表示非估值时自动产生的估增数据，‘－1’表示尾差调整数据，此时需要根据此标识来查询
            	sResult += " and a.FStockInd ="+this.filterType.strFStockInd;            	
            }

        }
        return sResult;
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        //----2009-08-14 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -----//
        String sVocStr = ""; //词汇类型对照字符串
        //-----------------------------------------------------------------//
        String sDateStr = "";
        String strSql = "";
        ResultSet rs = null;
        boolean bTrans = false;
        String strSql1 = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.isOnlyColumns.equals("1")) {
            	//----2009-08-14 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -----//
                VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType);
                //-----------------------------------------------------------------------------//

                bufAll.setLength(0);
    			//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
                bufAll.append(sHeader).append("\r\f").append(sShowDataStr).append(
                    "\r\f").append(sAllDataStr).append("\r\f").append(this.
                    getListView1ShowCols()).append("\r\f").append(yssPageInationBean.buildRowStr()).append("\r\f").append("voc").append(sVocStr); // 添加投资类型的词汇， modify by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A

                return bufAll.toString();
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " g.FCuryName,f.FSecurityName,h.FPortName as FPortName,e.FTsfTypeName as FTsfTypeName,i.FSubTsfTypeName as FSubTsfTypeName" +
                " ,j.FAttrClsName as FAttrClsName";
            strSql = strSql +
                (this.SecPecPayAnalysis().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_data_SecRecPay") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode" +
                this.SecPecPayAnalysis()
                +
                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) f on a.FSecurityCode = f.FSecurityCode"
                +
                //edit by yanghaiming 20100913 组合设置去掉启用日期   MS01700 QDV411建行2010年09月07日01_A 
//                " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from (select FPortCode,max(FStartDate) as FStartDate  from " +
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u " +
//                " join (select * from " +
//                pub.yssGetTableName("tb_para_portfolio") + " where FCheckState = 1) v on u.FPortCode = v.FPortCode and u.FStartDate = v.FStartDate) h on a.FPortCode = h.FPortCode"
                " left join (select FPortCode,FPortName from " + 
                pub.yssGetTableName("tb_para_portfolio") +
                " where FCheckState = 1) h on a.FPortCode = h.FPortCode"
                +
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) e on a.FTsfTypeCode = e.FTsfTypeCode" +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) i on a.FSubTsfTypeCode = i.FSubTsfTypeCode" +
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +
                //----2009-08-14 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -----//
                " left join (SELECT FVocCode, FVocName from Tb_Fun_Vocabulary WHERE FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_InvestType) + ") v1 ON v1.FVocCode = a.FInvestType" +
                //-------------------------
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc";
            //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("SecRecPay");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji

            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);

                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            //----2009-08-14 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -----//
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType);
            //-----------------------------------------------------------------------------//

            bufAll.setLength(0);
			//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            bufAll.append(sHeader).append("\r\f").append(sShowDataStr).append(
                "\r\f").append(sAllDataStr).append("\r\f").append(this.
                getListView1ShowCols()).append("\r\f").append(yssPageInationBean.buildRowStr()).append("\r\f").append("voc").append(sVocStr); // 添加投资类型的词汇， modify by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A

            return bufAll.toString();

        }

        catch (Exception ex) {
            throw new YssException(ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
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
    public String getListViewData3() throws YssException {
        String sHeader = ""; //表头
        String sShowDataStr = ""; //用于显示的数据
        String sAllDataStr = ""; //全部的数据
        String strSql = "";
        String portCode = ""; //SQL语句中的组合代码
        int i = 0;
        String[] reqAry = null;
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            reqAry = this.filterType.strPortCode.split(","); //解析当前组合群下的所有组合，MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang modify 20090522
            for (i = 0; i < reqAry.length; i++) {
                portCode = portCode + "'" + reqAry[i] + "',";
            }
            if (portCode.length() > 0) {
                portCode = portCode.substring(0, portCode.length() - 1);

            }
            sHeader = this.getListView1Headers();
            strSql = "select show.*,gp.FAssetGroupCode,gp.FAssetGroupName from( "; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
            strSql = strSql +
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " g.FCuryName,f.FSecurityName,h.FPortName as FPortName,e.FTsfTypeName as FTsfTypeName,i.FSubTsfTypeName as FSubTsfTypeName" +
                " ,j.FAttrClsName as FAttrClsName";
            strSql = strSql +
                (this.SecPecPayAnalysis().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_data_SecRecPay") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode" +
                this.SecPecPayAnalysis()
                +
                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) f on a.FSecurityCode = f.FSecurityCode"
                +
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                " left join (select FPortCode ,FPortName  from  " +
                pub.yssGetTableName("tb_para_portfolio") + "  where FCheckState =1 ) h on a.FPortCode = h.FPortCode" 
             
                
                //end by lidaolong
                +
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) e on a.FTsfTypeCode = e.FTsfTypeCode" +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) i on a.FSubTsfTypeCode = i.FSubTsfTypeCode" +
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +
                " where a.FPortCode in ( " + portCode + // wdy 添加表别名:a
                //edit by songjie 2012.04.06 添加 查询自动生成数据的条件 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B
                ") and a.FDataOrigin = 0 and a.FTransDate between " +
                dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate) +
                this.buildFilterSql(false) +
                " order by a.FCheckState, a.FCreateTime desc";
            //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090521
            strSql = strSql + ") show join TB_SYS_ASSETGROUP gp on gp.fassetgroupcode='" + pub.getPrefixTB() + "'";
            //---------------------------------------------------------------------------------------------------
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
                this.assetGroupCode = rs.getString("FAssetGroupCode"); //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
                this.assetGroupName = rs.getString("FAssetGroupName"); //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取应收应付数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getOperData
     */
    public void getOperData() {
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String sPrefixTB = pub.getPrefixTB(); //保存当前组合群代码
        try {
            if (sType.equalsIgnoreCase("getinvestPaid")) {
                if (this.bOverGroup) {
                    pub.setPrefixTB(this.assetGroupCode); //设置表前缀为前台传进来的当前组合群
                }
                this.money = operFun.getBondLx(this.transDate, this.strSecurityCode,
                                               this.strPortCode,
                                               this.invMgrCode.length() > 0 ?
                                               this.invMgrCode : " ",
                                               this.brokerCode.length() > 0 ?
                                               this.brokerCode : " ",
                                               this.exchangeCode.length() > 0 ?
                                               this.exchangeCode : " ",
                                               //----MS00327 QDV4赢时胜（上海）2009年3月16日05_B 增加02，02FI_B的调拨类型和调拨子类型 ---
                                               "06,02",
                                               "06FI,06FI_B,02FI_B",//BugId:MS00144 QDV4赢时胜(上海)2009年1月5日02_B 增加06FI_B的调拨子类型。当在买入的次日就支付债券利息时，不能只获取06FI。sj modified 20090121
                                                this.attrClsCode); //2009-09-14 蒋锦 修改 添加属性分类代码作为形参 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A

                //---MS00278  QDV4中保2009年02月24日03_B  ------
//                this.money = money; //在计算money的过程中已经round过，并判断过舍入的位数了，所以这里就去除再次舍入。
                //-------------------------------------------
            }
            //linjunyun 2008-11-28 bug:MS00029 批量审核/反审核/删除
            if (sType.equalsIgnoreCase("multauditTradeSub")) {
                if (multAuditString.length() > 0) {
                    return this.auditMutli(this.multAuditString); //执行批量审核/反审核/删除
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            pub.setPrefixTB(sPrefixTB);
            this.bOverGroup = false;
        }
        return buildRowStr();

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
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        String sMutiAudit = ""; //linjunyun 2008-11-28 bug:MS00029 批量处理的数据
        try {
            //linjunyun 2008-11-28 bug:MS00029 提取批量处理数据
            if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];
                multAuditString = sMutiAudit;
                sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];
            }

            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            if (!reqAry[1].equalsIgnoreCase("")&& YssFun.isDate(reqAry[1])) {//添加对字符串的判断 by leeyu 20100812 合并太平版本调整
                this.transDate = YssFun.toDate(reqAry[1]);
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            this.strPortCode = reqAry[2];
            this.strPortName = reqAry[3];
            this.invMgrCode = reqAry[4];
            this.invMgrCodeName = reqAry[5];
            this.brokerCode = reqAry[6];
            this.brokerCodeName = reqAry[7];
            this.exchangeCode = reqAry[8];
            this.exchangeName = reqAry[9];
            this.strSecurityCode = reqAry[10];
            this.strSecurityName = reqAry[11];
            this.strCuryCode = reqAry[12];
            this.strCuryName = reqAry[13];

            this.strTsfTypeCode = reqAry[14];
            this.strTsfTypeName = reqAry[15];
            this.strSubTsfTypeCode = reqAry[16];
            this.strSubTsfTypeName = reqAry[17];

            if (YssFun.isNumeric(reqAry[18])) {
                this.baseCuryRate = Double.parseDouble(reqAry[18]);
            }
            if (YssFun.isNumeric(reqAry[19])) {
                this.portCuryRate = Double.parseDouble(reqAry[19]);
            }

            if (YssFun.isNumeric(reqAry[20])) {
                this.money = Double.parseDouble(reqAry[20]);
            }
            if (YssFun.isNumeric(reqAry[21])) {
                this.mMoney = Double.parseDouble(reqAry[21]);
            }

            if (YssFun.isNumeric(reqAry[22])) {
                this.vMoney = Double.parseDouble(reqAry[22]);
            }

            if (YssFun.isNumeric(reqAry[23])) {
                this.baseCuryMoney = Double.parseDouble(reqAry[23]);
            }

            if (YssFun.isNumeric(reqAry[24])) {
                this.mBaseCuryMoney = Double.parseDouble(reqAry[24]);
            }

            if (YssFun.isNumeric(reqAry[25])) {
                this.vBaseCuryMoney = Double.parseDouble(reqAry[25]);
            }

            if (YssFun.isNumeric(reqAry[26])) {
                this.portCuryMoney = Double.parseDouble(reqAry[26]);
            }
            if (YssFun.isNumeric(reqAry[27])) {
                this.mPortCuryMoney = Double.parseDouble(reqAry[27]);
            }
            if (YssFun.isNumeric(reqAry[28])) {
                this.vPortCuryMoney = Double.parseDouble(reqAry[28]);
            }

            super.checkStateId = Integer.parseInt(reqAry[29]);
            this.strOldPortCode = reqAry[30];
            this.strOldSecurityCode = reqAry[31];
            if (!reqAry[32].equalsIgnoreCase("")) {
                this.OldTransDate = YssFun.toDate(reqAry[32]);
            } else {
                this.OldTransDate = YssFun.toDate("9998-12-31");
            }

            this.strOldFAnalysisCode1 = reqAry[33];
            this.strOldFAnalysisCode2 = reqAry[34];
            this.strOldFAnalysisCode3 = reqAry[35];

            this.strOldNum = reqAry[36];

            if (!reqAry[37].equalsIgnoreCase("")) {
                this.startDate = YssFun.toDate(reqAry[37]);
            }
            if (!reqAry[38].equalsIgnoreCase("")) {
                this.endDate = YssFun.toDate(reqAry[38]);
            }
            this.isOnlyColumns = reqAry[39];
            this.attrClsCode = reqAry[40];
            this.catTypeCode = reqAry[41];
            if (reqAry[42].trim().length() > 0 && YssFun.isNumeric(reqAry[42])) {
                this.iInOutType = YssFun.toInt(reqAry[42]);
            }
            //edited by zhouxiang MS01595  证券应收应付，描述栏输入回车，回收站清除时，报错  
			if (reqAry[43].indexOf("【Enter】") > -1) {
				this.desc = reqAry[43].replaceAll("【Enter】", "\r\n");
			} else {
				this.desc = reqAry[43];
			}
            //end   by zhouxiang MS01595  证券应收应付，描述栏输入回车，回收站清除时，报错  
            // MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090521
            this.assetGroupCode = reqAry[44];
            this.assetGroupName = reqAry[45];
            //---------------------------------------------------------------------------------------------
            //------ add by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -------//
            this.investType = reqAry[46];
            //--------------------------------------------------------------------------------------------------//
            if (reqAry[47].equals("true")) { //panjunfang add 20090817,MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
                this.bOverGroup = true;
            }
            //==========add by xuxuming,2010.01.04===============
            //=========MS00902,估值增值和汇兑损益未转到新的库存中去    QDV4国泰2010年1月5日01_B====
            //if(reqAry.length>47&&reqAry[47]!=null&&!reqAry[47].equalsIgnoreCase("null")&&reqAry[47].trim().length() > 0 && YssFun.isNumeric(reqAry[47])){
            //	this.strFStockInd=YssFun.toInt(reqAry[47]);//入账标识
            //}
            //调整序号，因为reqAry[47]已经为bOverGroup赋了值，故这里应采用reqAry[48]序号 by leeyu 20100817 合并太平版本调整
            if(reqAry.length>48&&reqAry[48]!=null&&!reqAry[48].equalsIgnoreCase("null")&&reqAry[48].trim().length() > 0 && YssFun.isNumeric(reqAry[48])){
            	this.strFStockInd=YssFun.toInt(reqAry[48]);//入账标识
            }
            //by leeyu 20100817 合并太平版本调整
            //=====================end===========================
            try {
                if (reqAry[0].equalsIgnoreCase("add")) {
                    String strNumberDate = "";
                    strNumberDate = YssFun.formatDate(this.transDate, "yyyyMMdd").
                        substring(0, 8);
                    this.strNum = strNumberDate +
                        dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_SecRecPay"),
                                               dbl.sqlRight("FNum", 9),
                                               "000000001",
                                               " where FNum like 'SRP"
                                               + strNumberDate + "%'", 1);
                    this.strNum = "SRP" + this.strNum;
                } else {
                    this.strNum = reqAry[0];
                }

            } catch (Exception e) {}

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SecPecPayBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception ex) {
            throw new YssException("解析应收应付证券信息出错", ex);
        }

    }

    /**
     * 与ParseRowStr解析顺序一致的方法，整合成字符串
     * @return String
     * add by xuxuming,20090909,MS00473,QDV4国泰2009年6月01日01_A,国泰需根据最新的纳斯达克指数100信息来调整即将发行的LOF基金中的股票信息
     */
    public String buildRowStrForParse() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strNum.trim()).append("\t");
        buf.append(YssFun.formatDate(this.transDate).trim()).append("\t");
        buf.append(this.strPortCode.trim()).append("\t");
        buf.append(this.strPortName.trim()).append("\t");
        buf.append(this.invMgrCode.trim()).append("\t");
        buf.append(this.invMgrCodeName.trim()).append("\t");
        buf.append(this.brokerCode.trim()).append("\t");
        buf.append(this.brokerCodeName.trim()).append("\t");
        buf.append(this.exchangeCode.trim()).append("\t");
        buf.append(this.exchangeName.trim()).append("\t");
        buf.append(this.strSecurityCode.trim()).append("\t");
        buf.append(this.strSecurityName.trim()).append("\t");
        buf.append(this.strCuryCode.trim()).append("\t");
        buf.append(this.strCuryName.trim()).append("\t");
        buf.append(this.strTsfTypeCode.trim()).append("\t");
        buf.append(this.strTsfTypeName.trim()).append("\t");
        buf.append(this.strSubTsfTypeCode.trim()).append("\t");
        buf.append(this.strSubTsfTypeName.trim()).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(this.money).append("\t");
        buf.append(this.mMoney).append("\t");
        buf.append(this.vMoney).append("\t");

        buf.append(this.baseCuryMoney).append("\t");
        buf.append(this.mBaseCuryMoney).append("\t");
        buf.append(this.vBaseCuryMoney).append("\t");

        buf.append(this.portCuryMoney).append("\t");
        buf.append(this.mPortCuryMoney).append("\t");
        buf.append(this.vPortCuryMoney).append("\t");
        buf.append(this.checkStateId).append("\t");
        buf.append(this.strOldPortCode).append("\t");
        buf.append(this.strOldSecurityCode).append("\t");
        buf.append(YssFun.formatDate(this.OldTransDate).trim()).append("\t");
        buf.append(this.strOldFAnalysisCode1).append("\t");
        buf.append(this.strOldFAnalysisCode2).append("\t");
        buf.append(this.strOldFAnalysisCode3).append("\t");
        buf.append(this.strOldNum).append("\t");
        buf.append(YssFun.formatDate(this.startDate).trim()).append("\t");
        buf.append(YssFun.formatDate(this.endDate).trim()).append("\t");
        buf.append(this.isOnlyColumns).append("\t");

        buf.append(this.attrClsCode).append("\t");

        buf.append(this.catTypeCode).append("\t");

        buf.append(this.iInOutType).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.assetGroupCode).append("\t");
        buf.append(this.assetGroupName).append("\t");

        buf.append(this.investType).append("\t");
        buf.append(this.bOverGroup).append("\t");
        buf.append(this.strFStockInd).append("\t");//add by xuxuming,2010.01.04======
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * saveMutliOperData
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliOperData(String sMutilRowStr) throws YssException {
        return "";
    }

    /**
     * setResultSetAttr
     *
     * @param rs ResultSet
     */
    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strNum = rs.getString("FNum") + "";
        this.transDate = rs.getDate("FTransDate");
        this.strPortCode = rs.getString("FPortCode") + "";
        this.strPortName = rs.getString("FPortName") + "";
        this.invMgrCode = rs.getString("FAnalysisCode1") + "";
        this.invMgrCodeName = rs.getString("FAnalysisName1") + "";
        this.brokerCode = rs.getString("FAnalysisCode2") + "";
        this.brokerCodeName = rs.getString("FAnalysisName2") + "";
        this.exchangeCode = rs.getString("FAnalysisCode3") + "";
        this.exchangeName = rs.getString("FAnalysisName3") + "";
        this.strSecurityCode = rs.getString("FSecurityCode") + "";
        this.strSecurityName = rs.getString("FSecurityName") + "";
        this.strCuryCode = rs.getString("FCuryCode") + "";
        this.strCuryName = rs.getString("FCuryName") + "";

        this.strTsfTypeCode = rs.getString("FTsfTypeCode") + "";
        this.strTsfTypeName = rs.getString("FTsfTypeName") + "";
        this.strSubTsfTypeCode = rs.getString("FSubTsfTypeCode") + "";
        this.strSubTsfTypeName = rs.getString("FSubTsfTypeName") + "";

        this.baseCuryRate = rs.getDouble("FBaseCuryRate");

        this.portCuryRate = rs.getDouble("FPortCuryRate");
        //fanghaoln 20100301 MS00808 QDV4建行2009年11月12日01_B 
        if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("09OP")){//判断是否为权证
        	this.money = YssD.round(rs.getDouble("FMoney"), 2);//权证证卷应收应付显示两位小数
            this.mMoney = YssD.round(rs.getDouble("FMMoney"), 2);//权证证卷应收应付显示两位小数
            this.vMoney = YssD.round(rs.getDouble("FVMoney"), 2);//权证证卷应收应付显示两位小数
            //this.money = rs.getDouble("FMoney");
        }else{
        	this.money = rs.getDouble("FMoney");//其它的应收应付按原有规定显示其小数位数
            this.mMoney = rs.getDouble("FMMoney");//其它的应收应付按原有规定显示其小数位数
            this.vMoney = rs.getDouble("FVMoney");//其它的应收应付按原有规定显示其小数位数
        }
        //---------------------end ----MS00808----------------------------------
        this.baseCuryMoney = rs.getDouble("FBaseCuryMoney");
        this.mBaseCuryMoney = rs.getDouble("FMBaseCuryMoney");
        this.vBaseCuryMoney = rs.getDouble("FVBaseCuryMoney");
        this.portCuryMoney = rs.getDouble("FPortCuryMoney");
        this.mPortCuryMoney = rs.getDouble("FMPortCuryMoney");
        this.vPortCuryMoney = rs.getDouble("FVPortCuryMoney");
        this.attrClsCode = rs.getString("FAttrClsCode");
        this.attrClsName = rs.getString("FAttrClsName");
        this.desc = rs.getString("FDesc"); //  2008-4-23  单亮  添加描述属性
        iInOutType = rs.getInt("FInOut");
        //------ add by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -------//
        this.investType = rs.getString("FInvestType");
        //--------------------------------------------------------------------------------------------------//
        super.setRecLog(rs);

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */

    public String getBeforeEditData() throws YssException {

        SecPecPayBean befEditBean = new SecPecPayBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " g.FCuryName,f.FSecurityName,h.FPortName as FPortName,e.FTsfTypeName as FTsfTypeName,i.FSubTsfTypeName as FSubTsfTypeName" +
                ",j.FAttrClsName as FAttrClsName";
            strSql = strSql +
                (this.SecPecPayAnalysis().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_data_SecRecPay") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode" +
                this.SecPecPayAnalysis()
                +
                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) f on a.FSecurityCode = f.FSecurityCode"
                +
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
         
                " left join (select FPortCode ,FPortName  from  " +
                pub.yssGetTableName("tb_para_portfolio") + "  where FCheckState =1 ) h on a.FPortCode = h.FPortCode" 
             
                //end by lidaolong 
                +
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) e on a.FTsfTypeCode = e.FTsfTypeCode" +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) i on a.FSubTsfTypeCode = i.FSubTsfTypeCode" +
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +
                " where  FNum =" + dbl.sqlString(this.strNum) +
                " order by a.FCheckState, a.FCreateTime desc";

           
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strNum = rs.getString("FNum") + "";
                befEditBean.transDate = rs.getDate("FTransDate");
                befEditBean.strPortCode = rs.getString("FPortCode") + "";
                befEditBean.strPortName = rs.getString("FPortName") + "";
                befEditBean.invMgrCode = rs.getString("FAnalysisCode1") + "";
                befEditBean.invMgrCodeName = rs.getString("FAnalysisName1") + "";
                befEditBean.brokerCode = rs.getString("FAnalysisCode2") + "";
                befEditBean.brokerCodeName = rs.getString("FAnalysisName2") + "";
                befEditBean.exchangeCode = rs.getString("FAnalysisCode3") + "";
                befEditBean.exchangeName = rs.getString("FAnalysisName3") + "";
                befEditBean.strSecurityCode = rs.getString("FSecurityCode") + "";
                befEditBean.strSecurityName = rs.getString("FSecurityName") + "";
                befEditBean.strCuryCode = rs.getString("FCuryCode") + "";
                befEditBean.strCuryName = rs.getString("FCuryName") + "";
                befEditBean.strTsfTypeCode = rs.getString("FTsfTypeCode") + "";
                befEditBean.strTsfTypeName = rs.getString("FTsfTypeName") + "";
                befEditBean.strSubTsfTypeCode = rs.getString("FSubTsfTypeCode") +
                    "";
                befEditBean.strSubTsfTypeName = rs.getString("FSubTsfTypeName") +
                    "";

                befEditBean.baseCuryRate = rs.getDouble("FBaseCuryRate");

                befEditBean.portCuryRate = rs.getDouble("FPortCuryRate");
                befEditBean.money = rs.getDouble("FMoney");
                befEditBean.mMoney = rs.getDouble("FMMoney");
                befEditBean.vMoney = rs.getDouble("FVMoney");
                befEditBean.baseCuryMoney = rs.getDouble("FBaseCuryMoney");
                befEditBean.mBaseCuryMoney = rs.getDouble("FMBaseCuryMoney");
                befEditBean.vBaseCuryMoney = rs.getDouble("FVBaseCuryMoney");
                befEditBean.portCuryMoney = rs.getDouble("FPortCuryMoney");
                befEditBean.mPortCuryMoney = rs.getDouble("FMPortCuryMoney");
                befEditBean.vPortCuryMoney = rs.getDouble("FVPortCuryMoney");
                befEditBean.attrClsCode = rs.getString("FAttrClsCode");
                befEditBean.attrClsName = rs.getString("FAttrClsName");
            }
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
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getSetting
     *获取证券应收应付的单个实例数据
     * by xuxuming,20090914.MS00706,债券应收和转货基本数据要相应，不用重新输入.QDV4中保2009年09月15日02_B
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
       ResultSet rs = null;
       String strSql = "";
       try {
          strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Data_SecRecPay") +
                " a "
                + buildFilterSql();
          rs = dbl.openResultSet(strSql);
          while (rs.next()) {
             this.strNum = rs.getString("FNum") + "";
             this.transDate = rs.getDate("FTransDate");
             this.strPortCode = rs.getString("FPortCode") + "";
             this.invMgrCode = rs.getString("FAnalysisCode1") + "";
             this.brokerCode = rs.getString("FAnalysisCode2") + "";
             this.exchangeCode = rs.getString("FAnalysisCode3") + "";
             this.strSecurityCode = rs.getString("FSecurityCode") + "";
             this.strCuryCode = rs.getString("FCuryCode") + "";
             this.strTsfTypeCode = rs.getString("FTsfTypeCode") + "";
             this.strSubTsfTypeCode = rs.getString("FSubTsfTypeCode") + "";
             this.baseCuryRate = rs.getDouble("FBaseCuryRate");
             this.portCuryRate = rs.getDouble("FPortCuryRate");
             this.money = rs.getDouble("FMoney");
             this.mMoney = rs.getDouble("FMMoney");
             this.vMoney = rs.getDouble("FVMoney");
             this.baseCuryMoney = rs.getDouble("FBaseCuryMoney");
             this.mBaseCuryMoney = rs.getDouble("FMBaseCuryMoney");
             this.vBaseCuryMoney = rs.getDouble("FVBaseCuryMoney");
             this.portCuryMoney = rs.getDouble("FPortCuryMoney");
             this.mPortCuryMoney = rs.getDouble("FMPortCuryMoney");
             this.vPortCuryMoney = rs.getDouble("FVPortCuryMoney");
             this.catTypeCode = rs.getString("FCatType");
             this.attrClsCode = rs.getString("FAttrClsCode");
             this.desc = rs.getString("FDesc");
             iInOutType = rs.getInt("FInOut");
          }
          return this;
       }
       catch (Exception e) {
          throw new YssException("获取证券应收应付表信息出错！", e);
       }
       finally {
          dbl.closeResultSetFinal(rs);
       }
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
     * 2008-5-19
     * 单亮
     * 删除回收站的数据，即彻底从数据库删除数据
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
                        pub.yssGetTableName("Tb_data_SecRecPay") +
                        " where FNum = " + dbl.sqlString(this.strNum);
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

    /**
     * linjunyun 2008-11-28 bug:MS00029 批量更新审核/反审核/删除后的数据
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     */
    public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null;
        String sqlStr = "";
        java.sql.PreparedStatement psmt = null;
        boolean bTrans = false;
        SecPecPayBean data = null;
        String[] multAudit = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = "update " + pub.yssGetTableName("Tb_Data_SecRecPay") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = ?";

            psmt = conn.prepareStatement(sqlStr);
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f");
                if (multAudit.length > 0) {
                    for (int i = 0; i < multAudit.length; i++) {
                        data = new SecPecPayBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multAudit[i]);
                        psmt.setString(1, data.strNum);
                        psmt.addBatch();
                        // ---增加批量删除的日志记录功能----guojianhua add 20100907-------//
                        data=this;
                        logOper = SingleLogOper.getInstance();
						if (this.checkStateId == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (this.checkStateId == 1) {
							data.checkStateId = 1;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						} else if (this.checkStateId == 0) {
							data.checkStateId = 0;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						}
                        // -----------------------------------------//
                    }
                }
                conn.setAutoCommit(false);
                bTrans = true;
                psmt.executeBatch();
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("批量审核凭证数据表出错!");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
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

    /**
     * 从后台加载出跨组合群的内容
     * 修改人：panjunfang
     * 修改人时间:20090521
     * MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * @return String
     * @throws YssException
     */
    public String getListViewGroupData3() throws YssException {
        String sGroups = ""; //定义一个变量用于保存处理后的结果数据
        String sPrefixTB = pub.getPrefixTB(); //保存当前组合群代码
        String[] assetGroupCodes = this.filterType.assetGroupCode.split(YssCons.YSS_GROUPSPLITMARK);    //按组合群的解析符解析组合群代码
        String[] strPortCodes = this.filterType.strPortCode.split(YssCons.YSS_GROUPSPLITMARK);          //按组合群的解析符解析组合代码
        String[] strSecurityCodes = this.filterType.strSecurityCode.split(YssCons.YSS_GROUPSPLITMARK);  //按组合群的解析符解析证券代码
        try {
            for (int i = 0; i < assetGroupCodes.length; i++) {  //遍历组合群代码
                this.assetGroupCode = assetGroupCodes[i];       //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode);           //将该组合群代码设为表前缀
                this.filterType.strPortCode = strPortCodes[i];  //得到该组合群下的所有组合代码
                this.filterType.strSecurityCode = strSecurityCodes[i];  //得到该组合群下的所有证券代码
                String sGroup = this.getListViewData3();        //调用处理方法
                sGroups = sGroups + sGroup + YssCons.YSS_GROUPSPLITMARK; //各组合群的处理结果用<-AGP->间隔
            }
            if (sGroups.length() > 7) {
                sGroups = sGroups.substring(0, sGroups.length() - 7);   //去除多出的组合群解析符
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            pub.setPrefixTB(sPrefixTB); //设回原有表前缀
        }
        return sGroups; //将处理结果返回给前台
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }

    /**
     * 证券应收应付的深拷贝方法，返回本对象的深拷贝对象
     * 蒋锦 2009-9-11 添加 实际利率 MS00656:QDV4赢时胜(上海)2009年8月24日01_A
     * @return SecPecPayBean
     * @throws YssException
     */
    public SecPecPayBean deepCopy() throws YssException{
        SecPecPayBean secPay = new SecPecPayBean();

        secPay.strNum = strNum;                 //编号
        secPay.invMgrCode = invMgrCode;         //投资经理代码
        secPay.invMgrCodeName = invMgrCodeName; //投资经理名称
        secPay.brokerCodeName = brokerCodeName; //券商代码名称
        secPay.exchangeName = exchangeName;     //交易名称
        secPay.strPortCode = strPortCode;       //组合代码
        secPay.strPortName = strPortName;
        secPay.assetGroupCode = assetGroupCode; //组合群代码
        secPay.assetGroupName = assetGroupName; //组合群名称
        secPay.bOverGroup = bOverGroup;
        secPay.brokerCode = brokerCode;         //券商代码
        secPay.exchangeCode = exchangeCode;     //交易代码
        secPay.strSecurityCode = strSecurityCode; //证券代码
        secPay.strSecurityName = strSecurityName; //证券名称
        secPay.strCuryCode = strCuryCode;       //货币代码
        secPay.strCuryName = strCuryName;       //交易货币名称
        secPay.iInOutType = iInOutType;         //流入流出方向,默认为正方向
        secPay.transDate = transDate;           //业务日期
        secPay.baseCuryMoney = baseCuryMoney;   //基础货币金额
        secPay.baseCuryRate = baseCuryRate;     //基础汇率
        secPay.money = money; //金额
        secPay.mMoney = mMoney;
        secPay.vMoney = vMoney;
        secPay.mBaseCuryMoney = mBaseCuryMoney;
        secPay.vBaseCuryMoney = vBaseCuryMoney;
        secPay.mPortCuryMoney = mPortCuryMoney;
        secPay.vPortCuryMoney = vPortCuryMoney;
        secPay.moneyF = moneyF;
        secPay.baseCuryMoneyF = baseCuryMoneyF;
        secPay.portCuryMoneyF = portCuryMoneyF;
        secPay.portCuryRate = portCuryRate;         //组合汇率
        secPay.portCuryMoney = portCuryMoney;       //组合货币金额
        secPay.strTsfTypeCode = strTsfTypeCode;     //调拨类型
        secPay.strTsfTypeName = strTsfTypeName;     //调拨类型名称
        secPay.strSubTsfTypeCode = strSubTsfTypeCode; //调拨子类型
        secPay.strSubTsfTypeName = strSubTsfTypeName; //调拨子类型名称

        secPay.amount = amount; //数量
        secPay.per100 = per100; //百元利息
        secPay.rateBaseIncome = rateBaseIncome; //基础货币汇兑损益
        secPay.ratePortIncome = ratePortIncome; //组合货币汇兑损益
        secPay.valuationAdded = valuationAdded; //估值增值
        secPay.mktPrice = mktPrice; //行情价格 yujx 20070226

        secPay.isOnlyColumns = isOnlyColumns; //在初始登陆时是否只显示列，不查询数据
        secPay.startDate = startDate;   //起始日期
        secPay.endDate = endDate;       //终止日期

        secPay.attrClsCode = attrClsCode; //所属分类 sj add 20071202
        secPay.attrClsName = attrClsName;
        secPay.catTypeCode = catTypeCode; //品种类型
        secPay.catTypeName = catTypeName;
        secPay.desc = desc;             //添加描述 单亮  2008-4-22
        secPay.sRecycled = sRecycled;   //保存未解析前的字符串 单亮  2008-5-19
        secPay.multAuditString = multAuditString;
        secPay.relaNum = relaNum;
        secPay.relaNumType = relaNumType;
        secPay.investType = investType; //投资类型

        return secPay;
    }
}
