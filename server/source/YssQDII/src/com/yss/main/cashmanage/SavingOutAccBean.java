package com.yss.main.cashmanage;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssException;
import com.yss.util.YssFun;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.util.YssCons;
import java.sql.*;
import java.util.*;
import com.yss.util.*;
import com.yss.vsub.YssOperFun;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.income.stat.BaseIncomeStatDeal;
import com.yss.manager.*;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdata.RateTradeBean;
import com.yss.commeach.EachRateOper;

/**
 *
 * <p>Title: 流出帐户</p>
 * <p>Description: 对流出帐户进行操作的 BEAN</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ysstech</p>
 * @author not attributable
 * @version 1.0
 */
public class SavingOutAccBean
    extends BaseDataSettingBean implements IDataSetting {
    private String num = ""; //自动编号
    private String inAccNum = ""; //流入帐户编号
    private String cashAccCode = ""; //现金帐户
    private String cashAccName = ""; //name
    private String portCode = ""; //组合
    private String portName = ""; //name
    private String invMgrCode = ""; //分析代码1
    private String invMgrName = ""; //name
    private String catCode = "";
    private String catName = "";
    private String brokerCode = ""; //分析代码3
    private String brokerName = ""; //name
    private double outMoney = 0; //流出金额
    private String oldNum = ""; //old num
    private String strIsOnlyColumns = "0";
    private String desc = ""; //描述信息
    private java.util.Date outAccDate; //流出日期
    private String intrestCachAccCode = ""; //利息现金帐户
    private double recIntrest = 0; //利息金额
    private String strTransNum = ""; //把资金调拨编号 反过来保存到流入帐户
    private String strLxTransNum = ""; //把利息资金调拨编号 反过来保存到流入帐户
    SavingBean saving = null; // 保存流入帐户的信息  以便插入到资金调拨表里面去

    private double baseRate; // 基础汇率
    private double portRate; //组合汇率
    private double avgBaseRate; // 平均基础汇率
    private double avgPortRate; //平均组合汇率

    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    private String strAttrClsCode = "";//所属分类
    private String strAttrClsName = "";
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    //------add by wangzuochun 2010.06.08  MS01166    普通定存，做“转出”交易后，原账户利息库存没有冲减掉    QDV4国内（测试）2010年05月10日02_AB   
    private String outCashAccCode = ""; //流出现金帐户
    
    public String getOutCashAccCode(){
    	return outCashAccCode;
    }
    
    public void setOutCashAccCode(String outCashAccCode){
    	this.outCashAccCode = outCashAccCode;
    }
    //---------------------MS01166---------------------//
    public String getPortCode() {
        return portCode;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getOldNum() {
        return oldNum;
    }

    public String getCatCode() {
        return catCode;
    }

    public String getCashAccName() {
        return cashAccName;
    }

    public String getCatName() {
        return catName;
    }

    public String getNum() {
        return num;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getInvMgrName() {
        return invMgrName;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public String getStrIsOnlyColumns() {
        return strIsOnlyColumns;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public void setCashAccName(String cashAccName) {
        this.cashAccName = cashAccName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setInvMgrName(String invMgrName) {
        this.invMgrName = invMgrName;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setStrIsOnlyColumns(String strIsOnlyColumns) {
        this.strIsOnlyColumns = strIsOnlyColumns;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setInAccNum(String inAccNum) {
        this.inAccNum = inAccNum;
    }

    public void setOutAccDate(java.util.Date outAccDate) {
        this.outAccDate = outAccDate;
    }

    public void setStrTransNum(String strTransNum) {
        this.strTransNum = strTransNum;
    }

    public void setRecIntrest(double recIntrest) {
        this.recIntrest = recIntrest;
    }

    public void setIntrestCachAccCode(String intrestCachAccCode) {
        this.intrestCachAccCode = intrestCachAccCode;
    }

    public void setOutMoney(double outMoney) {
        this.outMoney = outMoney;
    }

    public void setSaving(SavingBean saving) {
        this.saving = saving;
    }

    public void setPortRate(double portRate) {
        this.portRate = portRate;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    public void setAvgBaseRate(double avgBaseRate) {
        this.avgBaseRate = avgBaseRate;
    }

    public void setAvgPortRate(double avgPortRate) {
        this.avgPortRate = avgPortRate;
    }

    public String getPortName() {
        return portName;
    }

    public String getDesc() {
        return desc;
    }

    public String getInAccNum() {
        return inAccNum;
    }

    public java.util.Date getOutAccDate() {
        return outAccDate;
    }

    public String getStrTransNum() {
        return strTransNum;
    }

    public double getRecIntrest() {
        return recIntrest;
    }

    public String getIntrestCachAccCode() {
        return intrestCachAccCode;
    }

    public double getOutMoney() {
        return outMoney;
    }

    public SavingBean getSaving() {
        return saving;
    }

    public double getPortRate() {
        return portRate;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public double getAvgBaseRate() {
        return avgBaseRate;
    }

    public double getAvgPortRate() {
        return avgPortRate;
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
    
    public SavingOutAccBean() {
    }

   

	/**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() {
        return "";
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
     *获得要加载的流入帐户 所对应的流出帐户的信息
     * @return String
     */
    public String getListViewData3() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShowDataStr = new StringBuffer();
        StringBuffer bufAllDataStr = new StringBuffer();
        String sAry[] = null;
        try {
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            sHeader = this.getListView1Headers();
            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Cash); //获得分析代码
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*, b.fusername as fcreatorname, c.fusername as fcheckusername," +
                " nvl(k.FAttrClsName,' ') as FAttrClsName,d.FPortName,e.FCashAccName" + sAry[0];
            strSql = strSql + " from " +
                pub.yssGetTableName("Tb_Cash_SavingOutAcc") + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                //-----------------------------------------------------------------------------------------------
                " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName,o.FPortCury as FPortCury from " +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                pub.yssGetTableName("Tb_Para_Portfolio") + " o where FCheckState = 1 and FASSETGROUPCODE = " + 
                dbl.sqlString(pub.getAssetGroupCode()) +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                "(select FPortCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 and FASSETGROUPCODE = " +
//                dbl.sqlString(pub.getAssetGroupCode()) +
//                " group by FPortCode) p " +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                ") d on a.FPortCode = d.FPortCode" +
                
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") k on a.FAttrClsCode = k.FAttrClsCode " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                //-------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                " select FCashAccCode, FCashAccName, FStartDate,FCuryCode as FCashCuryCode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) e on a.FCashAccCode = e.FCashAccCode " +
                //---------------------------------------------------------------------------------
                //---------------------------------------------------------------------------------------
                sAry[1] +
                " where a.FInAccNum = " + dbl.sqlString(this.inAccNum);

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShowDataStr.append( (rs.getString("FNum") + "").trim());
                bufShowDataStr.append("\t");
                bufShowDataStr.append( (rs.getString("FCashAccCode") + "").trim());
                bufShowDataStr.append("\t");
                bufShowDataStr.append( (rs.getString("FCashAccName") + "").trim());
                bufShowDataStr.append("\t");
                bufShowDataStr.append( (rs.getString("FPortName") + "").trim());
                bufShowDataStr.append("\t");
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                bufShowDataStr.append( (rs.getString("FATTRCLSCODE") + "").trim());
                bufShowDataStr.append("\t");
                bufShowDataStr.append( (rs.getString("FAttrClsName") + "").trim());
                bufShowDataStr.append("\t");
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                bufShowDataStr.append(YssFun.formatNumber(rs.getDouble("FOutMoney"),
                    "#,##0.##"));
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
                setResultSetAttr(rs);
                bufAllDataStr.append(this.buildRowStr()).append(YssCons.
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

    public void setResultSetAttr(ResultSet rs) throws SQLException,
        YssException {
        boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
        boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
        this.num = rs.getString("FNum") + "";
        this.inAccNum = rs.getString("FInAccNum") + "";
        this.cashAccCode = rs.getString("FCashAccCode") + "";
        this.cashAccName = rs.getString("FCashAccName") + "";
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

        //   this.brokerCode = rs.getString("FAnalysisCode3") + "";
        // this.brokerName = rs.getString("FAnalysisName3") + "";
        this.outMoney = rs.getDouble("FOutMoney");
        this.desc = rs.getString("FDesc") + "";
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        if(dbl.isFieldExist(rs, "FATTRCLSCODE")){
        	this.strAttrClsCode = rs.getString("FATTRCLSCODE");
        	this.strAttrClsName = rs.getString("FATTRCLSNAME");
        }else{
        	this.strAttrClsCode = "";
        	this.strAttrClsName = "";
        }
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        super.setRecLog(rs);
    }
    
    /**
     * add by zhangjun 2012.06.15 story#2579滚存业务
     * getListViewData4
     *
     * @return String
     */   
public void setResultSetAttr1(ResultSet rs,double value,double recInterest, String sCalcType) throws SQLException,
    YssException {
    boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
    boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
    this.num = rs.getString("FNum") + "";
    this.inAccNum = rs.getString("FInAccNum") + "";
    this.cashAccCode = rs.getString("FCashAccCode") + "";
    this.cashAccName = rs.getString("FCashAccName") + "";
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

  
    if( "fixrate".equalsIgnoreCase(sCalcType))//固定利率
    {
    	this.outMoney = rs.getDouble("FOutMoney") + value ;
    }else{ //固定收益
    	this.outMoney = rs.getDouble("FOutMoney") + recInterest ;
    }
    
    //this.outMoney = rs.getDouble("FOutMoney");
    this.desc = rs.getString("FDesc") + "";
   
    if(dbl.isFieldExist(rs, "FATTRCLSCODE")){
    	this.strAttrClsCode = rs.getString("FATTRCLSCODE");
    	this.strAttrClsName = rs.getString("FATTRCLSNAME");
    }else{
    	this.strAttrClsCode = "";
    	this.strAttrClsName = "";
    }
    
    super.setRecLog(rs);
}

    /**
     * add by zhangjun 2012.06.15 story#2579滚存业务
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {

        
    	/*String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShowDataStr = new StringBuffer();
        StringBuffer bufAllDataStr = new StringBuffer();
        String sAry[] = null;
        
        ArrayList bondList = new ArrayList();
    	CashPecPayBean cashpecpay = null;
    	double dValue = 0;        	        	
    	
        
        
        try {
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            sHeader = this.getListView1Headers();
            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Cash); //获得分析代码
            sHeader = this.getListView1Headers();
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
                " where a.FInAccNum = " + dbl.sqlString(this.inAccNum);

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
                bufShowDataStr.append(YssFun.formatNumber(rs.getDouble("FOutMoney"),
                    "#,##0.##"));
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
                setResultSetAttr(rs);
                bufAllDataStr.append(this.buildRowStr()).append(YssCons.
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
        } */
    	 return "";
    
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     */
    public void delSetting() {
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() {
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
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return saveMutliSetting(sMutilRowStr, false, "");
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr, boolean bIsTrans,
                                   String strInAccNum) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        java.sql.Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
		/**shashijie 2012-7-2 STORY 2475 */
        //ArrayList alOutAcc = new ArrayList();
		/**end*/
        try {
            if (!bIsTrans) {
                conn.setAutoCommit(false);
                bTrans = true;
            }
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            strSql = "delete from " + pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
                " where FInAccNum = " +
                dbl.sqlString(strInAccNum);
            dbl.executeSql(strSql);
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
                "(FNum, FInAccNum, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FCashAccCode," +
                "FOutMoney,FATTRCLSCODE, FCheckState, FCreator, FCreateTime,FCheckUser,FDesc) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                this.parseRowStr(sMutilRowAry[i]);
                this.baseRate = this.getOutBaseRate(this.cashAccCode,
                    this.outAccDate); //获得基础汇率
                // -- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  通过账户代码来获取基础汇率 ------------
                this.portRate = this.getOutPortRate(this.cashAccCode, this.outAccDate); //获得组合汇率
                //----------------------------------------------------------------------------------
                if (strInAccNum.trim().length() > 0) {
                    this.num = YssFun.formatNumber(i + 1, "00000");
                    this.inAccNum = strInAccNum;
                    pstmt.setString(1, this.num);
                    pstmt.setString(2, this.inAccNum);
                    pstmt.setString(3, this.portCode);
                    pstmt.setString(4,
                                    (this.invMgrCode == null ||
                                     this.invMgrCode.equals("")) ? " " :
                                    this.invMgrCode);
                    pstmt.setString(5,
                                    (this.catCode == null || this.catCode.equals("")) ?
                                    " " : this.catCode);
                    pstmt.setString(6,
                                    (this.brokerCode == null ||
                                     this.brokerCode.equals("")) ? " " :
                                    this.brokerCode);
                    pstmt.setString(7, this.cashAccCode);
                    pstmt.setDouble(8, this.outMoney);
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    pstmt.setString(9, (this.strAttrClsCode==null||this.strAttrClsCode.equals(""))?" ":this.strAttrClsCode);
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    pstmt.setInt(10, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(11, this.creatorCode);
                    pstmt.setString(12, this.creatorTime);
                    pstmt.setString(13,
                                    (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.setString(14, this.desc);
                    pstmt.executeUpdate();
                }

//            this.savingSettlement(false); //保存到资金调拨表
//            strSql = "update " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
//                  " set FTransNum = " + dbl.sqlString(strTransNum) +
//                  ", FLxTransNum=" + dbl.sqlString(strLxTransNum) +
//                  " where FNum=" + dbl.sqlString(this.inAccNum);
//            dbl.executeSql(strSql);
            }
            // MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj --去除在此产生资金调拨，统一在业务处理时产生资金调拨--
//         createCashTrans(this.saving, sMutilRowAry);
            //----------------------------------------------------------------------------------------------

            if (!bIsTrans) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (YssException ex) {
            if (pstmt != null) {
                pstmt = null;
            }
            throw new YssException("保存流出帐户信息出错" + "\r\n" + ex.getMessage(), ex);
        } catch (SQLException ex) {
            throw new YssException("保存流出帐户信息出错" + "\r\n" + ex.getMessage(), ex);
        } finally
        {
        	dbl.closeStatementFinal(pstmt);
        }
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
        buf.append(this.num).append("\t");
        buf.append(this.inAccNum).append("\t");
        buf.append(this.cashAccCode).append("\t");
        buf.append(this.cashAccName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.invMgrCode).append("\t");
        buf.append(this.invMgrName).append("\t");
        buf.append(this.catCode).append("\t");
        buf.append(this.catName).append("\t");
        buf.append(this.brokerCode).append("\t");
        buf.append(this.brokerName).append("\t");
        buf.append(this.outMoney).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.recIntrest).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String strReturn = "";
        double accMoney = 0;
        //  double accRateMoney =0.0;//利息余额
        try {
            if (sType.trim().equalsIgnoreCase("")) {
                YssOperFun fun = new YssOperFun(pub);
                accMoney = fun.getCashAccBalance(this.outAccDate, this.cashAccCode,
                                                 this.portCode, this.invMgrCode,
                                                 this.catCode);
                //  accRateMoney=fun.getCashLX(this.outAccDate,this.cashAccCode,this.portCode,this.invMgrCode,this.catCode);
                strReturn = accMoney + "";

            } else if (sType.equalsIgnoreCase("change")) {
                createCashTransDefaut();
            } else if (sType.equalsIgnoreCase("update")) {
                updateRateTradeCashTrans();
            }
            return strReturn;
        } catch (Exception ex) {
            throw new YssException("获取帐户余额与利息余额出错");
        }
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.num = reqAry[0];
            this.cashAccCode = reqAry[1];
            this.cashAccName = reqAry[2];
            this.portCode = reqAry[3];
            this.portName = reqAry[4];
            this.invMgrCode = reqAry[5];
            this.invMgrName = reqAry[6];
            this.catCode = reqAry[7];
            this.catName = reqAry[8];
            this.brokerCode = reqAry[9];
            this.brokerName = reqAry[10];
            if (YssFun.isNumeric(reqAry[11])) {
                this.outMoney = Double.parseDouble(reqAry[11]);
            }
            this.desc = reqAry[12];
            this.inAccNum = reqAry[13];
            if (YssFun.isDate(reqAry[14])) {
                this.outAccDate = YssFun.toDate(reqAry[14]);
            }
            if (YssFun.isNumeric(reqAry[15])) {
                this.baseRate = Double.parseDouble(reqAry[15]);
            }
            if (YssFun.isNumeric(reqAry[16])) {
                this.portRate = Double.parseDouble(reqAry[16]);
            }
            if (YssFun.isNumeric(reqAry[17])) {
                this.avgBaseRate = Double.parseDouble(reqAry[17]);
            }
            if (YssFun.isNumeric(reqAry[18])) {
                this.avgPortRate = Double.parseDouble(reqAry[18]);
            }

            if (YssFun.isNumeric(reqAry[19])) {
                this.recIntrest = Double.parseDouble(reqAry[19]);
            }

            this.oldNum = reqAry[20];
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            this.strAttrClsCode = reqAry[21];
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            super.parseRecLog();
        } catch (Exception e) {
            throw new YssException("解析流出帐户信息出错\r\n" + e.getMessage(), e);
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

    public double getOutBaseRate(String cashCode, java.util.Date dDate) throws
        YssException {
        ResultSet rs = null;
        String cashCuryCode = "";
        double dReturn = 0;
        try {
            String strSql = "select FCuryCode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCashAccCode=" +
                dbl.sqlString(cashCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                cashCuryCode = rs.getString("FCuryCode");
            }
            BaseOperDeal obj = new BaseOperDeal();
            obj.setYssPub(pub);
            dReturn = obj.getCuryRate(dDate, cashCuryCode, this.portCode,
                                      YssOperCons.YSS_RATE_BASE);
            if (rs != null) {
                rs.close();
            }
            return dReturn;
        } catch (Exception e) {
            throw new YssException("获取基础汇率出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public double getOutPortRate(String cashCode, java.util.Date dDate) throws
        YssException {
        ResultSet rs = null;
        double dReturn = 0;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        String CuryCode = "";
        try {
            String strSql = "select FCuryCode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FCashAccCode=" +
                dbl.sqlString(cashCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                CuryCode = rs.getString("FCuryCode");
            }
            BaseOperDeal obj = new BaseOperDeal();
            obj.setYssPub(pub);
//         dReturn = obj.getCuryRate(dDate, portCuryCode, this.portCode,
//                                   YssOperCons.YSS_RATE_PORT);
            rateOper.getInnerPortRate(dDate, CuryCode, this.portCode);
            dReturn = rateOper.getDPortRate();
            //------------------------------------------------------------

            if (rs != null) {
                rs.close();
            }
            return dReturn;
        } catch (Exception e) {
            throw new YssException("获取组合汇率出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //给定存业务创建一笔资金调拨
    private void createCashTrans(SavingBean inAcc, String[] sOutAccAry) throws
        YssException {
        TransferBean tran = null;
        TransferSetBean tranSet = null;

        ArrayList alTranSets = new ArrayList();

        CashTransAdmin transAdmin = new CashTransAdmin();
        transAdmin.setYssPub(pub);

        tran = new TransferBean();
        tran.setDtTransDate(this.outAccDate); //存入时间
        tran.setDtTransferDate(this.outAccDate);
        tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount); //内部资金调拨
        if (inAcc.getSavingType().equalsIgnoreCase("3")) { //存款类型为"同业拆借"时
            tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_LEND); //同业拆借
        } else {
            tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_SAVING); //存款发生
        }
        tran.setStrTransferTime("00:00:00");
        tran.setSavingNum(inAcc.getNum());
        tran.setFNumType("Saving");
        tran.checkStateId = 0;
        tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                             "yyyyMMdd HH:mm:ss");

        tranSet = new TransferSetBean();
        tranSet.setDMoney(inAcc.getInMoney());
        tranSet.setIInOut(1);
        tranSet.setSCashAccCode(inAcc.getCashAccCode());
        tranSet.setSPortCode(inAcc.getPortCode());
        tranSet.setSAnalysisCode1(inAcc.getInvMgrCode());
        tranSet.setSAnalysisCode2(inAcc.getCatCode());
        tranSet.setSAnalysisCode3(" ");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        tranSet.setStrAttrClsCode(inAcc.getStrAttrClsCode());
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        tranSet.setDBaseRate(inAcc.getBaseCuryRate());
        tranSet.setDPortRate(inAcc.getPortCuryRate());
        tranSet.checkStateId = 0;

        alTranSets.add(tranSet);

        for (int i = 0; i < sOutAccAry.length; i++) {
            this.parseRowStr(sOutAccAry[i]);
            tranSet = new TransferSetBean();
            tranSet.setDMoney(this.getOutMoney());
            tranSet.setIInOut( -1);
            tranSet.setSCashAccCode(this.getCashAccCode());
            tranSet.setSPortCode(this.getPortCode());
            tranSet.setSAnalysisCode1(this.getInvMgrCode());
            tranSet.setSAnalysisCode2(this.getCatCode());
            tranSet.setSAnalysisCode3(" ");
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            tranSet.setStrAttrClsCode(this.strAttrClsCode);
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            tranSet.setDBaseRate(this.getBaseRate());
            tranSet.setDPortRate(this.getPortRate());
            tranSet.checkStateId = 0;
            alTranSets.add(tranSet);
        }
        transAdmin.addList(tran, alTranSets);
        transAdmin.insert(inAcc.getNum(), "", "Saving");
    }

    //给定存业务创建一笔资金调拨 临时处理中金的问题 by liyu 080423
    private void createCashTransDefaut() throws YssException {
        ResultSet rs = null;
        ResultSet rs1 = null;
        String sSql = "", sSql1 = "";
        TransferBean tran = null;
        TransferSetBean tranSet = null;
        Connection conn = null;
        try {
            conn = dbl.loadConnection();
            sSql = "select * from " + pub.yssGetTableName("tb_cash_savinginacc") +
                " where Fcheckstate=1 ";
            rs = dbl.openResultSet(sSql);
            while (rs.next()) {
                ArrayList alTranSets = new ArrayList();

                CashTransAdmin transAdmin = new CashTransAdmin();
                transAdmin.setYssPub(pub);

                tran = new TransferBean();
                tran.setDtTransDate(rs.getDate("FSavingDate")); //存入时间
                tran.setDtTransferDate(rs.getDate("FSavingDate"));
                tran.setStrTsfTypeCode("01"); //内部资金调拨
                tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_SAVING); //存款发生
                tran.setStrTransferTime("00:00:00");
                tran.setSavingNum(rs.getString("FNum"));
                tran.setFNumType("Saving");
                tran.checkStateId = 1;
                tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                    "yyyyMMdd HH:mm:ss");

                tranSet = new TransferSetBean();
                tranSet.setDMoney(rs.getDouble("FInMoney"));
                tranSet.setIInOut(1);
                tranSet.setSCashAccCode(rs.getString("FCashAccCode"));
                tranSet.setSPortCode(rs.getString("FPortCode"));
                tranSet.setSAnalysisCode1(rs.getString("FANALySisCode1"));
                tranSet.setSAnalysisCode2(rs.getString("FANALySisCode1"));
                tranSet.setSAnalysisCode3(" ");
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                tranSet.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                tranSet.setDBaseRate(rs.getDouble("FBaseCuryRate"));
                tranSet.setDPortRate(rs.getDouble("FPortCuryRate"));
                tranSet.checkStateId = 1;

                alTranSets.add(tranSet);
                sSql1 = "select * from " + pub.yssGetTableName("tb_cash_savingoutacc") +
                    " where Finaccnum=" + dbl.sqlString(rs.getString("FNum"));
                rs1 = dbl.openResultSet(sSql1);
                while (rs1.next()) {
                    tranSet = new TransferSetBean();
                    tranSet.setDMoney(rs1.getDouble("FOutMoney"));
                    tranSet.setIInOut( -1);
                    tranSet.setSCashAccCode(rs1.getString("FCashAccCode"));
                    tranSet.setSPortCode(rs1.getString("FPortCode"));
                    tranSet.setSAnalysisCode1(rs1.getString("FAnalySisCode1"));
                    tranSet.setSAnalysisCode2(rs1.getString("FAnalySisCode1"));
                    tranSet.setSAnalysisCode3(" ");
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    tranSet.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    tranSet.setDBaseRate(rs.getDouble("FBaseCuryRate"));
                    tranSet.setDPortRate(rs.getDouble("FPortCuryRate"));
                    tranSet.checkStateId = 1;
                    alTranSets.add(tranSet);
                }
                transAdmin.addList(tran, alTranSets);
                transAdmin.insert(rs.getString("FNum"), "", "Saving");

            }
            conn.setAutoCommit(false);
            sSql = "update " + pub.yssGetTableName("Tb_Data_Cashpayrec") + " a set fcurycode = (select fcurycode from (select * from " + pub.yssGetTableName("tb_para_cashaccount") +
                " where FCheckState = 1) b where a.fcashacccode = b.fcashacccode) where  exists (select 1   from   " + pub.yssGetTableName("tb_para_cashaccount") +
                " b  where  b.fcashacccode=a.fcashacccode   )  and fcurycode=' '";
            dbl.executeSql(sSql);
            sSql = "update " + pub.yssGetTableName("tb_cash_transfer") + " set FNumType = 'SavingIns' where FSavingNum is not null and FTsfTypeCode = '02' and FSubTSfTypeCode = '02DE'";
            dbl.executeSql(sSql);
            sSql = "update " + pub.yssGetTableName("Tb_Data_Cashpayrec") +
                " a  set FportcuryRATE = (select FEXRATE1 from (select * from " + pub.yssGetTableName("tb_data_exchangerate") +
                " where FCheckState = 1 and FCuryCode = 'CNY') b where  A.FTRANSDATE = B.FEXRATEDATE)" +
                " where exists (select 1 from (select * from " + pub.yssGetTableName("tb_data_exchangerate") +
                " where FCheckState = 1 and FCuryCode = 'CNY') b where A.FTRANSDATE = B.FEXRATEDATE) and ftsftypecode = '02' and fsubtsftypecode = '02DE'";
            dbl.executeSql(sSql);
            sSql = "update " + pub.yssGetTableName("Tb_Data_Cashpayrec") +
                " a set FBASECURYRATE = (select FEXRATE1 from (select * from " + pub.yssGetTableName("tb_data_exchangerate") +
                " where FCheckState = 1) b where a.FCURYCODE = b.FCURYCODE AND A.FTRANSDATE=B.FEXRATEDATE ) " +
                " where  exists (select 1   from  " + pub.yssGetTableName("tb_data_exchangerate") +
                " b  where  b.FCURYCODE=a.FCURYCODE AND A.FTRANSDATE=B.FEXRATEDATE )  and ftsftypecode='02' and fsubtsftypecode='02DE' and fcurycode<>'USD'";
            dbl.executeSql(sSql);
            sSql = "update " + pub.yssGetTableName("Tb_Data_Cashpayrec") +
                " set fbasecurymoney = round(fmoney* FBASECURYRATE,2) ,fportcurymoney = round(fmoney* FBASECURYRATE/FportcuryRATE,2)  where ftsftypecode='02' and fsubtsftypecode='02DE' ";
            dbl.executeSql(sSql);
            sSql = "update " + pub.yssGetTableName("tb_cash_subtransfer") +
                " f set FBASECURYRATE = (select distinct FExRate1 from (select * from (select a.*,b.FCuryCode from (select a1.*,a2.FTransDate from " + pub.yssGetTableName("tb_cash_subtransfer") +
                " a1 join (select * from " + pub.yssGetTableName("tb_cash_transfer") +
                " where (FTsfTypeCode = '01' and FSubTsfTypeCode = '0003') or (FTsfTypeCode = '02' and FSubTsfTypeCode = '02DE')) a2 on a1.fnum = a2.fnum) a join (select FCashAccCode,FCuryCode from " + pub.yssGetTableName("tb_para_cashaccount") +
                " ) b on a.FCashAccCode = b.FCashAccCode) aa left join (select * from " + pub.yssGetTableName("tb_data_exchangerate") +
                " where FCheckState = 1) bb on aa.FTransDate = bb.FExRateDate and aa.FCuryCode = bb.FCuryCode where aa.FBaseCuryRate <> bb.FExRate1 ) g where f.Fnum = g.Fnum) where exists " +
                " (select 1 from (select * from (select a.*,b.FCuryCode from (select a1.*,a2.FTransDate from " + pub.yssGetTableName("tb_cash_subtransfer") +
                " a1 join (select * from " + pub.yssGetTableName("tb_cash_transfer") + " where (FTsfTypeCode = '01' and FSubTsfTypeCode = '0003') or (FTsfTypeCode = '02' and FSubTsfTypeCode = '02DE')) a2 on a1.fnum = a2.fnum) a join (select FCashAccCode,FCuryCode from " +
                pub.yssGetTableName("tb_para_cashaccount") +
                " ) b on a.FCashAccCode = b.FCashAccCode) aa left join (select * from " + pub.yssGetTableName("tb_data_exchangerate") +
                " where FCheckState = 1) bb on aa.FTransDate = bb.FExRateDate and aa.FCuryCode = bb.FCuryCode where aa.FBaseCuryRate <> bb.FExRate1 ) g where f.Fnum = g.Fnum)";
            dbl.executeSql(sSql);
            sSql = "update " + pub.yssGetTableName("tb_cash_subtransfer") +
                " f set FPORTCURYRATE = (select distinct FExRate1 from (select * from (select a.*,b.FCuryCode from (select a1.*,a2.FTransDate,'CNY' as FCuryCode1 from " + pub.yssGetTableName("tb_cash_subtransfer") +
                " a1 join (select * from " + pub.yssGetTableName("tb_cash_transfer") +
                " where (FTsfTypeCode = '01' and FSubTsfTypeCode = '0003') or (FTsfTypeCode = '02' and FSubTsfTypeCode = '02DE')) a2 on a1.fnum = a2.fnum) a  join (select FCashAccCode,FCuryCode from " + pub.yssGetTableName("tb_para_cashaccount") +
                " ) b on a.FCashAccCode = b.FCashAccCode) aa left join (select * from " + pub.yssGetTableName("tb_data_exchangerate") +
                " where FCheckState = 1 and FCuryCode = 'CNY') bb on aa.FTransDate = bb.FExRateDate and aa.FCuryCode1 = bb.FCuryCode where aa.FBaseCuryRate <> bb.FExRate1 ) g where f.Fnum = g.Fnum) where exists " +
                " (select 1 from (select * from (select a.*,b.FCuryCode from (select a1.*,a2.FTransDate,'CNY' as FCuryCode1 from " + pub.yssGetTableName("tb_cash_subtransfer") +
                " a1 join (select * from " + pub.yssGetTableName("tb_cash_transfer") +
                " where (FTsfTypeCode = '01' and FSubTsfTypeCode = '0003') or (FTsfTypeCode = '02' and FSubTsfTypeCode = '02DE')) a2 on a1.fnum = a2.fnum) a  join (select FCashAccCode,FCuryCode from " + pub.yssGetTableName("tb_para_cashaccount") +
                " ) b on a.FCashAccCode = b.FCashAccCode) aa left join (select * from " + pub.yssGetTableName("tb_data_exchangerate") +
                " where FCheckState = 1 and FCuryCode = 'CNY') bb on aa.FTransDate = bb.FExRateDate and aa.FCuryCode1 = bb.FCuryCode where aa.FBaseCuryRate <> bb.FExRate1 ) g where f.Fnum = g.Fnum)";
            dbl.executeSql(sSql);
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException(ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs1);
        }
        /*TransferBean tran = null;
                 TransferSetBean tranSet = null;

                 ArrayList alTranSets = new ArrayList();

                 CashTransAdmin transAdmin = new CashTransAdmin();
                 transAdmin.setYssPub(pub);

                 tran = new TransferBean();
                 tran.setDtTransDate(this.outAccDate); //存入时间
                 tran.setDtTransferDate(this.outAccDate);
                 tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);//内部资金调拨
                 if (inAcc.getSavingType().equalsIgnoreCase("3")){//存款类型为"同业拆借"时
           tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_LEND);//同业拆借
                 }else{
           tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_SAVING); //存款发生
                 }
                 tran.setStrTransferTime("00:00:00");
                 tran.setSavingNum(inAcc.getNum());
                 tran.setFNumType("Saving");
                 tran.checkStateId = 0;
                 tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                             "yyyyMMdd HH:mm:ss");

                 tranSet = new TransferSetBean();
                 tranSet.setDMoney(inAcc.getInMoney());
                 tranSet.setIInOut(1);
                 tranSet.setSCashAccCode(inAcc.getCashAccCode());
                 tranSet.setSPortCode(inAcc.getPortCode());
                 tranSet.setSAnalysisCode1(inAcc.getInvMgrCode());
                 tranSet.setSAnalysisCode2(inAcc.getCatCode());
                 tranSet.setSAnalysisCode3(" ");
                 tranSet.setDBaseRate(inAcc.getBaseCuryRate());
                 tranSet.setDPortRate(inAcc.getPortCuryRate());
                 tranSet.checkStateId = 0;

                 alTranSets.add(tranSet);

                 for (int i = 0; i < sOutAccAry.length; i++) {
           this.parseRowStr(sOutAccAry[i]);
           tranSet = new TransferSetBean();
           tranSet.setDMoney(this.getOutMoney());
           tranSet.setIInOut( -1);
           tranSet.setSCashAccCode(this.getCashAccCode());
           tranSet.setSPortCode(this.getPortCode());
           tranSet.setSAnalysisCode1(this.getInvMgrCode());
           tranSet.setSAnalysisCode2(this.getCatCode());
           tranSet.setSAnalysisCode3(" ");
           tranSet.setDBaseRate(this.getBaseRate());
           tranSet.setDPortRate(this.getPortRate());
           tranSet.checkStateId = 0;
           alTranSets.add(tranSet);
                 }
                 transAdmin.addList(tran, alTranSets);
                 transAdmin.insert(inAcc.getNum(), "","Saving");
         */
    }

    //保存到资金调拨表
    protected void savingSettlement(boolean isSavContinue) throws YssException {
        TransferBean tran = null;
        TransferSetBean tranSetIn = null;
        TransferSetBean tranSetLXIn = null;
        TransferSetBean tranSetOut = null;
        java.sql.Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            tran = new TransferBean();
            tran.setYssPub(pub);
            tran.setDtTransDate(this.outAccDate); //存入时间
            tran.setDtTransferDate(this.outAccDate);
            tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
            tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_SAVING);
            tran.setStrTransferTime("00:00:00");
            tran.checkStateId = 0;
            tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                                 "yyyyMMdd HH:mm:ss");
            tran.setDataSource(1); //这里应为自动标记为1 add by leeyu BUG:MS00020 2008-11-24
            tran.addSetting(true);
            strTransNum = tran.getStrNum();

            tranSetIn = new TransferSetBean();
            tranSetIn.setYssPub(pub);

            tranSetOut = new TransferSetBean();
            tranSetOut.setYssPub(pub);
            tranSetOut.setSSubNum("00001"); //流出
            loadTranSetAttr(this, null, tranSetOut, -1, tran.getStrNum());
            tranSetOut.addSetting(true);

            //流入
            loadTranSetAttr(null, saving, tranSetIn, 1, tran.getStrNum());
            tranSetIn.setSSubNum("00001");
            tranSetIn.setDMoney(saving.getInMoney()); //流入金额
            tranSetIn.addSetting(true);

            if (saving.getRecInterest() != 0) { //如果有利息金额  就保存到资金调拨表里面
                tran.setStrTsfTypeCode("02");
                tran.setStrSubTsfTypeCode("02DE"); //存款利息收入从1001调整为02DE fazmm20071010
                tran.setDataSource(1); //这里应为自动标记为1 add by leeyu BUG:MS00020 2008-11-24
                tran.addSetting(true);
                this.strLxTransNum = tran.getStrNum();

                tranSetOut.setDMoney(saving.getRecInterest());
                tranSetOut.setSNum(tran.getStrNum());
                tranSetOut.addSetting(true);

                tranSetIn.setDMoney(saving.getRecInterest());
                tranSetIn.setSNum(tran.getStrNum());
                tranSetIn.addSetting(true);

            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    private void loadTranSetAttr(SavingOutAccBean savingoutacc,
                                 SavingBean saving,
                                 TransferSetBean tranSet,
                                 int inOut, String sNum) throws YssException {
        try {
            tranSet.setSNum(sNum);
            if (savingoutacc != null && saving == null) {
                tranSet.setDMoney(savingoutacc.outMoney);
                tranSet.setSPortCode(savingoutacc.portCode);
                tranSet.setSAnalysisCode1(savingoutacc.invMgrCode);
                tranSet.setSAnalysisCode2(savingoutacc.catCode);
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                tranSet.setStrAttrClsCode(savingoutacc.strAttrClsCode);
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                tranSet.setDBaseRate(savingoutacc.baseRate);
                tranSet.setDPortRate(savingoutacc.portRate);
                tranSet.setSCashAccCode(savingoutacc.cashAccCode);
            } else if (savingoutacc == null && saving != null) {
                tranSet.setSPortCode(saving.getPortCode());
                tranSet.setSAnalysisCode1(saving.getInvMgrCode());
                tranSet.setSAnalysisCode2(saving.getCatCode());
              //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                tranSet.setStrAttrClsCode(saving.getStrAttrClsCode()); //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                tranSet.setDBaseRate(saving.getBaseCuryRate());
                tranSet.setDPortRate(saving.getPortCuryRate());
                tranSet.setSCashAccCode(saving.getCashAccCode());
            }
            tranSet.setIInOut(inOut);
            tranSet.checkStateId = 0;
        } catch (Exception e) {
            throw new YssException("加载资金调拨信息出错\n" + e.getMessage());
        }

    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }

    public void updateRateTradeCashTrans() throws YssException {
        String sSelRateSql = "";
        String sUpdateSql = "";
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        RateTradeBean rateTrade = null;
        try {
            sSelRateSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Data_RateTrade") +
                " WHERE FCheckState = 1";
            rs = dbl.openResultSet(sSelRateSql);
            conn.setAutoCommit(false);
            bTrans = true;
            while (rs.next()) {
                rateTrade = new RateTradeBean();
                rateTrade.setYssPub(pub);
                rateTrade.setAnalysisCode1(rs.getString("FAnalysisCode1"));
                rateTrade.setAnalysisCode2(rs.getString("FAnalysisCode2"));
                rateTrade.setAnalysisCode3(rs.getString("FAnalysisCode3"));
                rateTrade.setBAnalysisCode1(rs.getString("FBAnalysisCode1"));
                rateTrade.setBAnalysisCode2(rs.getString("FBAnalysisCode2"));
                rateTrade.setBAnalysisCode3(rs.getString("FBAnalysisCode3"));
                rateTrade.setBaseMoney(rs.getDouble("FBaseMoney"));
                rateTrade.setBCashAccCode(rs.getString("FBCashAccCode"));
                rateTrade.setBCuryCode(rs.getString("FBCuryCode"));
                rateTrade.setBCuryFee(rs.getDouble("FBCuryFee"));
                rateTrade.setBMoney(rs.getDouble("FBMoney"));
                rateTrade.setBPortCode(rs.getString("FBPortCode"));
                rateTrade.setBSettleDate(rs.getDate("FBSettleDate"));
                rateTrade.setBSettleTime(rs.getString("FBSettleTime"));
                rateTrade.setCatType(rs.getString("FCatType"));
                rateTrade.setExCuryRate(rs.getDouble("FExCuryRate"));
                rateTrade.setLingCuryRate(rs.getDouble("FLongCuryRate"));
                rateTrade.setNum(rs.getString("FNum"));
                rateTrade.setOldNum(rs.getString("FNum"));
                rateTrade.setPortCode(rs.getString("FPortCode"));
                rateTrade.setPortMoney(rs.getDouble("FPortMoney"));
                rateTrade.setRateFx(rs.getDouble("FRateFx"));
                rateTrade.setSCashAccCode(rs.getString("FSCashAccCode"));
                rateTrade.setSCuryCode(rs.getString("FSCuryCode"));
                rateTrade.setSCuryFee(rs.getDouble("FSCuryFee"));
                rateTrade.setSettleDate(rs.getDate("FSettleDate"));
                rateTrade.setSettleTime(rs.getString("FSettleTime"));
                rateTrade.setSMoney(rs.getDouble("FSMoney"));
                rateTrade.setSReceiverCode(rs.getString("FReceiverCode"));
                rateTrade.setTradeDate(rs.getDate("FTradeDate"));
                rateTrade.setTradeTime(rs.getString("FTradeTime"));
                rateTrade.setTradeType(rs.getString("FTradeType"));
                rateTrade.getOperValue("costfx");
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                rateTrade.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
                rateTrade.setStrBAttrClsCode(rs.getString("FBATTRCLSCODE"));
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                sUpdateSql = "UPDATE " + pub.yssGetTableName("Tb_Data_RateTrade") +
                    " SET FBaseMoney = " + rateTrade.getBaseMoney() + "," +
                    " FPortMoney = " + rateTrade.getPortMoney() + "," +
                    " FRateFx = " + rateTrade.getRateFx() +
                    " WHERE FNum = " + dbl.sqlString(rateTrade.getNum());
                dbl.executeSql(sUpdateSql);
                rateTrade.createSavCashTrans(rateTrade.getNum(), rateTrade.getNum(), 1);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(false);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
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

}
