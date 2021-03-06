package com.yss.main.parasetting;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import oracle.jdbc.OracleResultSet;
import oracle.sql.CLOB;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.pojo.sys.YssCancel;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 运营收支品种信息设置
 * @author Administrator
 * 修改记录：
 * 添加节假日群 MS00817:QDV4工银2009年11月17日01_A sunkey@Modify
 */
public class InvestPayBean
    extends BaseDataSettingBean implements IDataSetting {
    private String ivPayCatCode = "";   //运营收支品种代码
    private String ivPayCatName = "";   //运营收支品种名称
    private String feeType = "";        //运营品种类型名称
    private int ivPayType;              //运营收支品种类型，20070806，杨
    private String tsfTypeCode;
    private String subTsfTypeCode;
    private String portCode = "";       //组合代码
    private String portName = "";       //组合名称
    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    private boolean bOverGroup = false;//判断是否跨组合群 ，MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090813
    //--------------------------------------------------------------------------------
    
    //-------add by zhangjun 2011-12-07 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
    private String ariExpCode = "";  //算法公式代码
    private String ariExpName = "";  //算法公式名称
    private String highArith = "";   //是否使用算法公式
    //------------end----------------
    private String roundCode = "";      //舍入代码
    private String roundName = "";      //舍入名称
    private String perExpCode = "";     //比率公式代码
    private String perExpName = "";     //比率公式名称
    private String periodCode = "";     //期间代码
    private String periodName = "";     //期间名称
    private BigDecimal fixRate = null;             //固定比率。panjunfang modify 20090815，解决精度问题
    private java.util.Date fACBeginDate;    //开始日期
    private java.util.Date fACEndDate;      //结束日期

    //------ MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A-
    private java.util.Date ExpirDate;   //终止日期
    private String cashAccCode = "";    //现金帐户代码
    private String cashAccName = "";    //现金帐户名称
    //------ End MS00017 add by wangzuochun 2009.06.25----------------

    private String ApportionType = "";		//20110808 added by liubo.Story #1227.均摊方式

    private double sPaidIn = 0;			//added by liubo.Story #2139.预提转待摊中的实付金额。
    
    private java.util.Date sTransitionDate;	//added by liubo.Story #2139.预提转待摊中的转换日期

    private String sTransition = "";		//added by liubo.Story #2139.预提转待摊。0为否，1为是
    

    private double sLimitedAmount = 0.0000;            //20120425 added by liubo.Story #2217.支付下限
    private String sPeriodOfBC = "";                //20120425 added by liubo.Story #2217.补差时期
    private String sPayDate = "";					//20120425 added by liubo.Story #2217.支付日设置


	private double fACTotalMoney;   //总金额
    private String AcroundCode = "";
    private String AcroundName = "";
    private int payOrigin;          //收支来源

    //MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A
    private String accrueTypeCode;  //计提方式代码
    private String accrueTypeName;  //计提方式名称
    //End MS00018 panjunfang add 20090714===================

    private String FIVType;         //运营品种类型
    private java.util.Date startDate;
    private String desc = "";

    private String analysisCode1 = "";
    private String analysisCode2 = "";
    private String analysisCode3 = "";

    private String analysisName1 = "";
    private String analysisName2 = "";
    private String analysisName3 = "";

    private String curyCode = "";
    private String curyName = "";
    
    private String holidaysCode="";	//节假日代码
    private String holidaysName="";	//节假日名称

    private InvestPayBean filterType;

    private String oldIvPayCatCode = "";
    private String oldPortCode = "";
    private java.util.Date oldStartDate;

    private String oldAnalysisCode1 = "";
    private String oldAnalysisCode2 = "";
    private String oldAnalysisCode3 = "";

    private String strIsOnlyColumns = "0"; //是否显示数据
    private String sRecycled = "";
    private String sRelaInvest="";//保存运营关联项数据 QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu\
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 ----//
    private String attrClsCode = "";
	//--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao end -----------//
 
    //add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
    private String autoPay = "";//是否自动支付
    private String autoPayType = "";//自动支付类型
    private int autoPayDay ;//自动支付日期
    //end by lidaolong 20110309 
    //story 2253 add by zhouwei 20120221 分级组合  start----
    private String clsPortCode="";
    private String clsPortName="";
    private String oldClsPortCode="";
   //story 2253 add by zhouwei 20120221 分级组合  end----
   
    //add by huangqirong 2013-01-21 story #3488
    private String lowerCurrencyCode = "";	//币种代码
    private String lowerCurrencyName = "";	//币种名称
    private String supplementDate = ""; 	//补差日期
    
    /*added by yeshenghong 2013-6-17 Story 3958 */
    private String sProfitLoss = "";

	private String sDeferral = "";

    private String sDigest = "";
    
    public String getsProfitLoss() {
		return sProfitLoss;
	}

	public void setsProfitLoss(String sProfitLoss) {
		this.sProfitLoss = sProfitLoss;
	}

	public String getsDeferral() {
		return sDeferral;
	}

	public void setsDeferral(String sDeferral) {
		this.sDeferral = sDeferral;
	}

	public String getsDigest() {
		return sDigest;
	}

	public void setsDigest(String sDigest) {
		this.sDigest = sDigest;
	}

	/*end by yeshenghong 2013-6-17 Story 3958 */
    
    private String sCommission = "0";		//20130129 added by liubo.Story #3414.支付两费时是否自动支付划款手续费
    
    public String getCommission() {
		return sCommission;
	}

	public void setCommission(String sCommission) {
		this.sCommission = sCommission;
	}

	public String getLowerCurrencyCode() {
		return lowerCurrencyCode;
	}

	public void setLowerCurrencyCode(String lowerCurrencyCode) {
		this.lowerCurrencyCode = lowerCurrencyCode;
	}

	public String getLowerCurrencyName() {
		return lowerCurrencyName;
	}

	public void setLowerCurrencyName(String lowerCurrencyName) {
		this.lowerCurrencyName = lowerCurrencyName;
	}

	public String getSupplementDate() {
		return supplementDate;
	}

	public void setSupplementDate(String supplementDate) {
		this.supplementDate = supplementDate;
	}
    //---end---

	public InvestPayBean() {
    }


    public String getClsPortCode() {
		return clsPortCode;
	}


	public void setClsPortCode(String clsPortCode) {
		this.clsPortCode = clsPortCode;
	}


	public String getClsPortName() {
		return clsPortName;
	}


	public void setClsPortName(String clsPortName) {
		this.clsPortName = clsPortName;
	}


    public void setInvestPayAttr(ResultSet rs, String cashAccName) throws SQLException, SQLException,
        YssException {
        this.ivPayCatCode = rs.getString("FIVPAYCATCODE");      //运营收支品种代码
        this.ivPayCatName = rs.getString("FIvPayCatName") + ""; //运营收支品种名称

        //------ MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
        this.feeType = rs.getString("FFeeType") + "";   // 运营品种类型
        //------ End MS00017 add by wangzuochun 2009.06.25 ----------

        this.portCode = rs.getString("FPortCode") + "";     //组合代码
        this.portName = rs.getString("FPortName") + "";     //组合名称
        this.roundCode = rs.getString("FRoundCode") + "";   //舍入代码
        this.roundName = rs.getString("FRoundName") + "";   //舍入名称
        this.AcroundCode = rs.getString("FACROUNDCODE") + "";
        this.AcroundName = rs.getString("FACROUNDName") + "";
        this.perExpCode = rs.getString("FPerExpCode") + ""; //比率公式代码
        this.perExpName = rs.getString("FPerExpName") + ""; //比率公式名称
        this.periodCode = rs.getString("FPeriodCode") + ""; //期间代码
        this.periodName = rs.getString("FPeriodName") + ""; //期间名称
        this.fixRate = rs.getBigDecimal("FFixRate");            //固定比率
        this.fACBeginDate = rs.getDate("FACBeginDate") == null ?
            YssFun.parseDate("9998-12-31") : rs.getDate("FACBeginDate");
        this.fACEndDate = rs.getDate("FACEndDate") == null ?
            YssFun.parseDate("9998-12-31") : rs.getDate("FACEndDate");

        //------ Start MS00017  给“终止日期”赋值 ---------------------
        this.ExpirDate = rs.getDate("FExpirDate") == null ?
            YssFun.parseDate("9998-12-31") : rs.getDate("FExpirDate");
        //------ End MS00017 add by wangzuochun 2009.06.25 ----------

        this.fACTotalMoney = rs.getDouble("FACTotalMoney");
        this.FIVType = rs.getString("FIVType") == null ? "" :
            rs.getString("FIVType");
        this.payOrigin = rs.getInt("FPayOrigin");

        //------ Start MS00018 计提方式代码赋值 ------------------------------
        this.accrueTypeCode = rs.getString("FAccrueType"); //计提方式代码
        //------ End MS00018 panjunfang add 20090714 -----------------------

        this.startDate = rs.getDate("FStartDate"); //启动日期
        this.desc = rs.getString("FDESC");

        //------ Start MS00017 收支币种变更为现金帐户 ---------
        this.cashAccCode = rs.getString("FCashAccCode");
        this.cashAccName = cashAccName; //#2279 modify by fangjiang 20110218
        //------ End MS00017 add by wangzuochun 2009.06.25 -

        this.analysisCode1 = rs.getString("FAnalysisCode1");
        this.analysisCode2 = rs.getString("FAnalysisCode2");
        // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090605
        ResultSetMetaData rsmd = rs.getMetaData();                          //得到结果集里的内容
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {                   //循环字段名称
            if (rsmd.getColumnName(i).equals("FASSETGROUPCODE")) {          //把字段名称进行对比看是否有当前字段名称
                this.assetGroupCode = rs.getString("fassetgroupcode") + ""; //给组合群代码赋值
                this.assetGroupName = rs.getString("fassetgroupname") + ""; //给组合群名称赋值
            }
            if(rsmd.getColumnName(i).equalsIgnoreCase("FInvMgrName")){
                this.analysisName1 = rs.getString("FInvMgrName") + "";//如果存在投资经理，则将投资经理名称进行赋值
            }
        }
        //---------------------------------------------------------------------------------------------
        this.holidaysCode = rs.getString("FHolidaysCode") + ""; 
        this.holidaysName = rs.getString("FHolidaysName") + "";
        /*added by yeshenghong 2013-6-17 Story 3958 */
        this.sProfitLoss = rs.getString("FProfitLoss") + ""; 
        this.sDeferral = rs.getString("FDeferral") + ""; 
        this.sDigest = rs.getString("FDigest") + ""; 
		/*end by yeshenghong 2013-6-17 Story 3958 */
        
        //add by zhangjun 2011-12-08 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
        this.ariExpCode = rs.getString("FARIEXPCODE") + "";
        this.ariExpName = rs.getString("FSIName") + "";
        this.highArith = rs.getString("FHIGHARITH") + "";
        //----------------------end----------------------------
        
      //add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
        this.autoPay = rs.getString("FAutoPay")+"";
        this.autoPayType = rs.getString("FAutoPayType")+"";
        this.autoPayDay = rs.getInt("FAutoPayDay");      
      //end by lidaolong 20110309 
        
        this.ApportionType = rs.getString("FApportionType");	//20110809 added by liubo.Story #1227.均摊方式
        this.sTransition = rs.getString("FTransition");			 //added by liubo.Story #2139.预提转待摊
        this.sPaidIn = rs.getDouble("FPaidIn");					//added by liubo.Story #2139.预提转待摊中的实付金额
        this.sTransitionDate = rs.getDate("FTransitionDate");	//added by liubo.Story #2139.预提转待摊中的转换日期
		//story 2253 add by zhouwei 20120221 分级组合  start----
        this.clsPortCode=rs.getString("FPortClsCode");
        this.clsPortName=rs.getString("FPortClsName");
        //story 2253 add by zhouwei 20120221 分级组合  end----
        
        this.sLimitedAmount = rs.getDouble("FLimitedAmount");	//20120425 added by liubo.Story #2217.支付下限
        this.sPeriodOfBC = rs.getString("FPeriodOfBC");			//20120425 added by liubo.Story #2217.补差时期
        this.sPayDate = dbl.clobStrValue(rs.getClob("FPayDate")); 		//20120510 added by liubo.Story #2217.支付日
        //add by huangqirong 2013-01-21 story #3488
        this.lowerCurrencyCode = rs.getString("FLowerCurrencyCode");
        this.lowerCurrencyName = rs.getString("FLCuryName") == null ? "" :  rs.getString("FLCuryName");
        this.supplementDate = rs.getString("FSupplementDates") == null ? " " : rs.getString("FSupplementDates");
        //---end---
        this.sCommission = rs.getString("FCommission");			//20130129 added by liubo.Story #3414.支付两费时是否自动支付划款手续费
        
        super.setRecLog(rs);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        ResultSet rs1 = null; //#2279 add by fangjiang 20110218
        
        try {
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.Yss_INVESTPAY + "," + YssCons.YSS_YSSACCRUETYPE); //增加计提方式  MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A panjunfang modify 20090714
            if(this.bOverGroup){
                sHeader = this.getListView3Headers();
            }else{
                sHeader = this.getListView1Headers();
            }
          //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
          //if (strSql == "") {
          if (strSql.equals("")) {
          //---end--- 
                if(this.bOverGroup){//跨组合群。MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090813
                    return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                        "\r\f" +
                        this.getListView3ShowCols() + "\r\f" + "voc" + sVocStr;
                }else{
                    return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                        "\r\f" +
                        this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
                }
            }
            //20120314 modified by liubo.Bug #3933.实现分页显示
            //============================
//          rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
    		yssPageInationBean.setsTableName("InvestPay");
    		rs = dbl.openResultSet(yssPageInationBean);  
            //===============end=============   
            //#2279 modify by fangjiang 20110218          
            while (rs.next()) { 
            	String[] cashAccCode_arr = null;
                String cashAccName = "";
            	cashAccCode_arr = rs.getString("FCashAccCode").split(",");
            	for(int i=0; i<cashAccCode_arr.length; i++){
            		strSql = " select FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") +
           		 			 " where FCheckState = 1 and FCashAccCode = " + dbl.sqlString(cashAccCode_arr[i]);
            		rs1 = dbl.openResultSet(strSql);
            		while (rs1.next()) {  
            			cashAccName += rs1.getString("FCashAccName") + ",";
            		}
            	}
            	dbl.closeResultSetFinal(rs1);
            	if(cashAccName.endsWith(",")){
            		cashAccName = cashAccName.substring(0,cashAccName.length()-1);
            	}       	
            	
                if(this.bOverGroup){//跨组合群。MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090813
                    bufShow.append(buildRowShowStr(rs, this.getListView3ShowCols(), cashAccName)).
                        append(YssCons.YSS_LINESPLITMARK);
                }else{
                    bufShow.append(buildRowShowStr(rs, this.getListView1ShowCols(), cashAccName)).
                        append(YssCons.YSS_LINESPLITMARK);
                }
                setInvestPayAttr(rs, cashAccName);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);           
            }
            //---------------------
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            //20120314 modified by liubo.Bug #3933.实现分页显示
            //============================
            if(this.bOverGroup){//跨组合群。MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090813
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView3ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + "\r\f" + "voc" + sVocStr;
            }else{
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + "\r\f" + "voc" + sVocStr;
            }
            //===========end=================
        } catch (Exception e) {
            throw new YssException("获取投资运营收资出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs1); //#2279 add by fangjiang 20110218
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }
    
    /***
     * #2279 add by wuweiqi 20110125 QDV4农行2010年11月30日01_A
     * 支持现金账户多账户显示进行解析
     * @param rs
     * @param sShowFields
     * @param key
     * @param result
     * @return
     * @throws YssException
     */
    public String buildRowShowStr(ResultSet rs, String sShowFields, String cashAccName) throws YssException {
	    StringBuffer buf = new StringBuffer();
	    String[] sFieldAry = sShowFields.split("\t");
	    HashMap hmFieldType = null;
	    String sFieldType = null;
	    String sResult = "";
	    YssCancel before = new YssCancel();
	    String sFieldName = "";
	    String sFieldFormat = "";
	    try {
	        hmFieldType = dbFun.getFieldsType(rs);
	        if (hmFieldType == null) {
	            return "";
	        }
	        for (int i = 0; i < sFieldAry.length; i++) {
	            before.setCancel(false);
	            beforeBuildRowShowStr(before, sFieldAry[i], rs, buf);
	            if (!before.isCancel()) {
	                sFieldFormat = "";
	                if (sFieldAry[i].indexOf(";") > 0) {
	                    sFieldName = sFieldAry[i].split(";")[0];
	                    sFieldFormat = sFieldAry[i].split(";")[1];
	                } else {
	                    sFieldName = sFieldAry[i];
	                }
	                sFieldType = (String) hmFieldType.get(sFieldName.toUpperCase());
	                if (sFieldType != null) {
	                    if ( (sFieldType).indexOf("DATE") > -1) {
	                        if (rs.getDate(sFieldName) != null) {
	                            buf.append(YssFun.formatDate(rs.getDate(sFieldName)));
	                        } else {
	                            buf.append("");
	                        }
	                    } else if ( (sFieldType).indexOf("NUMBER") > -1) {
	                        if (sFieldFormat.length() > 0) {
	                            buf.append(YssFun.formatNumber(rs.getDouble(sFieldName),
	                                sFieldFormat) + "");
	                        } else {
	                            buf.append(rs.getString(sFieldName) + "");
	                        }
	                    } else if ( (sFieldType).indexOf("CLOB") > -1) {
	                        buf.append(dbl.clobStrValue(rs.getClob(sFieldName)));
	                    } else {
	                    	if(sFieldName.equals("FCashAccName")){
	                    		buf.append(cashAccName + "");	                    		
	                    	}else{
	                    		buf.append(rs.getString(sFieldName) + "");
	                    	}       
	                    }
	                    buf.append("\t");
	                }
	                if (isJudge(sFieldName)) {
	                    buildRowOtherShowStr(sFieldName, rs, buf);
	                }
	            }
	        }
	        sResult = buf.toString();
	        if (sResult.trim().length() > 1) {
	            sResult = sResult.substring(0, sResult.length() - 1);
	        }
	        return sResult;
	    } catch (Exception e) {
	        throw new YssException("生成显示数据出错");
	    }
	}
    
  
    /***
     * add by wuweiqi 20101231 QDV4工银2010年11月1日01_A  
     * 在收益计提中选择组合点击查询是否显示管理费和托管费
     * 验证是否需要在资产估值中自动计提运营两费（收益计提中调用）
     * @return
     * @throws YssException
     */
       public String  getAutoCharge(String portCode,String tb_investPay)throws YssException{
           String strSql = null;//用于储存sql语句
           ResultSet rs = null;//声明结果集
           String FctlValue[]=null;
           String strResult="";
           String strCode="";//组合代码
           int i = 0;
           try{
           	strSql= " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
               " where FPUBPARACODE = 'AutoCharge' and FPARAID!=0 ";
           	 rs = dbl.openResultSet_antReadonly(strSql);  
           	 rs.last();
	         FctlValue = new String[rs.getRow()];
	         rs.beforeFirst();
	         while(rs.next()){
	             FctlValue[i] = rs.getString("FCTLVALUE");
	             i++;
	            }
	         for(int j=0;j < i;j++)
	         {
        		 if(portCode.indexOf(FctlValue[j].substring(0,FctlValue[j].indexOf("|")))!=-1){		 
        			 strCode=FctlValue[j].substring(0,FctlValue[j].indexOf("|"));
        			 strResult += " and (Fivpaycatcode, fportcode) not in (select Fivpaycatcode, fportcode from "
        				           +pub.yssGetTableName(tb_investPay)+      //story 2253 update by zhouwei 20120221 增加一个受托费（系统固定费用代码）
        				           //edit by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A 添加 IV103 FA_Admin
        			              " where Fivpaycatcode in('IV001', 'IV002','IV103','YSS_STF') and fportcode="+dbl.sqlString(strCode)+")";
        		 }
        		 else{
        			 strResult += "";
        		 }       		 
	         }  
             return strResult;
           }
           catch(Exception e){
               throw new YssException("验证是否自动计提运营两费信息出错！", e);
           }
           finally{
               dbl.closeResultSetFinal(rs);
           }
      }
     /***
      * add by wuweiqi 20110115 QDV4工银2010年11月1日01_A    
      * 运营库存统计时不统计管理费和托管费 （ 资产估值是调用）
      * @param portCode
      * @return
      * @throws YssException
      */
       public String  getAutoCharge(String portCode) throws YssException{
           String strSql = null;//用于储存sql语句
           ResultSet rs = null;//声明结果集
           String FctlValue =null;
           String strResult="";
           String strCode="";//组合代码
           try{
           	strSql= " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
               " where FPUBPARACODE = 'AutoCharge' and FPARAID!=0 ";
           	 rs = dbl.openResultSet(strSql);  
           	 while(rs.next()){
           		FctlValue = rs.getString("FCTLVALUE");
           		strCode=FctlValue.substring(0,FctlValue.indexOf("|"));
           		strCode="'"+strCode+"'";
           		if(strCode.equals(portCode)){
           			//edit by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A 添加 IV103 FA_Admin费用
           			strResult = " and (Fivpaycatcode not in ('IV001', 'IV002', 'IV103', 'YSS_STF', 'YSS_FXJ') or FTsfTypeCode <> '07') "; //modify by fangjiang 2011.02.10 BUG #1066 
           			break;
           		}
           	 }
             return strResult;
           }
           catch(Exception e){
               throw new YssException("验证是否自动计提运营两费信息出错！", e);
           }
           finally{
               dbl.closeResultSetFinal(rs);
           }
      }
    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String strResult = "";
        if (this.filterType != null) {
            strResult = " where 1=1 ";
            if (this.filterType.strIsOnlyColumns.equalsIgnoreCase("1")) {
                strResult = strResult + " and 1=2 ";
                return strResult;
            }
            if (this.filterType.checkStateId == 1) {
                strResult = strResult + " and a.FCheckState = 1 ";
            }
            if (this.filterType.ivPayCatCode.length() != 0) {
                strResult = strResult + " and a.FIVPayCatCode like '" +
                    filterType.ivPayCatCode.replaceAll("'", "''") + "%'";
            }
            //------ Start MS00017  收支币种变更为现金帐户 --------------------
            if (this.filterType.cashAccCode.length() != 0) {
                strResult = strResult + " and a.FCashAccCode like '" +
                    filterType.cashAccCode.replaceAll("'", "''") + "%'";
            }
            //201108167 added by liubo.Story #1227
            //筛选条件中增加“均摊方式”一项
            //***************************************
            if (this.filterType.ApportionType.length() != 0)
            {
            	strResult = strResult = strResult + " and a.FApportionType = " + dbl.sqlString(filterType.ApportionType);
            }
            //*******************end********************
            if (this.filterType.holidaysCode.length() != 0) {
                strResult = strResult + " and a.FHolidaysCode like '" +
                    filterType.holidaysCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.portCode.trim().length() != 0) {
            	//MS00694 QDV4赢时胜（深圳）2009年9月10日01_B fanghaoln 20091023
                strResult = strResult + " and a.FPortCode in("+ this.operSql.sqlCodes(this.filterType.portCode)+")";
                // add by wuweiqi 20101231 去除管理费和托管费  QDV4工银2010年12月22日01_A
                String tb_investPay="Tb_Para_InvestPay";
                strResult = strResult + getAutoCharge(this.filterType.portCode,tb_investPay);  
              //--------------------------------end -MS00694-----------------------------
            }
            if (this.filterType.startDate != null &&
                !this.filterType.startDate.equals(YssFun.toDate("9998-12-31"))) {
                strResult = strResult + " and a.FStartDate = " +
                    dbl.sqlDate(filterType.startDate);
            }
            if (this.filterType.periodCode != null &&
                this.filterType.periodCode.trim().length() != 0) {
                strResult = strResult + " and a.FPeriodCode like '" +
                    filterType.periodCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.perExpCode != null &&
                this.filterType.perExpCode.trim().length() != 0) {
                strResult = strResult + " and a.FPerExpCode like '" +
                    filterType.perExpCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.fACBeginDate != null &&
                !this.filterType.fACBeginDate.equals(YssFun.toDate("9998-12-31"))) {
                strResult = strResult + " and a.fACBeginDate =" +
                    dbl.sqlDate(filterType.fACBeginDate);
            }
            if (this.filterType.fACEndDate != null &&
                !this.filterType.fACEndDate.equals(YssFun.toDate("9998-12-31"))) {
                strResult = strResult + " and a.fACEndDate = " +
                    dbl.sqlDate(filterType.fACEndDate);
            }
            //------ Start MS00017 添加筛选条件：“终止日期”----------------------
            if (this.filterType.ExpirDate != null &&
                !this.filterType.ExpirDate.equals(YssFun.toDate("9998-12-31"))) {
                strResult = strResult + " and a.FExpirDate = " +
                    dbl.sqlDate(filterType.ExpirDate);
            }
            //------ End MS00017 add by wangzuochun 2009.06.29------------------
            
          //add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
            if (this.filterType.autoPay.equals("1")){
            	strResult = strResult + " and a.FAutoPay = '1' and a.FAutoPayType = '"
            				+ this.filterType.autoPayType + "' and a.FAutoPayDay ="
            				+ this.filterType.autoPayDay;
            }
            
            //add by zhangjun 2011-12-08 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
            if (this.filterType.highArith.equals("1")){
            	strResult = strResult + " and a.FHIGHARITH = '1' and a.FARIEXPCODE = '"
            				+ this.filterType.ariExpCode + "'";
            }
            //end by zhangjun -----------------------------
            
          //end by lidaolong 20110309 
            //---add by sognjie 2011.05.23 BUG 1826 QDV4赢时胜（测试）2011年4月27日02_B---//
            if(this.filterType.roundCode != null && !this.filterType.roundCode.trim().equals("")){
            	strResult = strResult + " and a.FRoundCode = " +
                dbl.sqlString(filterType.roundCode);
            }
            //---add by sognjie 2011.05.23 BUG 1826 QDV4赢时胜（测试）2011年4月27日02_B---//
            //story 2253 add by zhouwei 20120221 组合分级的筛选条件 start-------
            if(this.filterType.clsPortCode!=null && !this.filterType.clsPortCode.equals("")){
            	strResult = strResult + " and a.FPortClsCode = " +
                           dbl.sqlString(filterType.clsPortCode);
            }
            //story 2253 add by zhouwei 20120221 组合分级的筛选条件 end-------
        }
        return strResult;
    }

    /**
     * 获取投资运营收支设置信息
     * 此方法已被修改
     * 修改时间：2008年2月26号
     * 修改人：单亮
     * 原方法的功能：查询出投资运营设置数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        String sAry[] = null;
        try {
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1",
                YssOperCons.YSS_KCLX_InvestPayRec); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2",
                YssOperCons.YSS_KCLX_InvestPayRec);
            sAry = this.operSql.storageAnalysisSql(YssOperCons.
                YSS_KCLX_InvestPayRec); //获得分析代码
            strSql =
                "select y.*,h.fassetgroupcode, h.fassetgroupname from " +
                //fanghaoln 20090924 MS00694 QDV4赢时胜（深圳）2009年9月10日01_B
                "(select FIVPayCatCode, max(FStartDate) as FStartDate,fportcode" +
                //=========================end MS00694=============================================
                " from " + pub.yssGetTableName("Tb_Para_InvestPay") +
                //----------------begin
                " where FStartDate<=" +
                //----------------end
                dbl.sqlDate(new java.util.Date()) +
                
                //fanghaoln 20100325 MS01037 QDV4南方2010年3月18日01_B 
                //" and FCheckState = 1"+//xuqiji 20100225 MS00938  QDV4赢时胜(上海)2010年1月18日1_B
                //fanghaoln 20100325 MS01037 QDV4南方2010年3月18日01_B 
                
                //MS00694 QDV4赢时胜（深圳）2009年9月10日01_B fanghaoln 20091023
                //dbl.sqlString(this.filterType != null&&this.filterType.portCode.trim().length() != 0 ? "and fportcode in("+ this.operSql.sqlCodes(this.filterType.portCode)+")" : " ")+
                //--------------------------------end -MS00694-----------------------------
                //fanghaoln 20090924 MS00694 QDV4赢时胜（深圳）2009年9月10日01_B 使它按组合分组，这样就能显示每个组合的管理费和托管费
                " group by FIVPayCatCode,fportcode)" +//去掉FStartDate，只查询出启用日期最近的那个 panjunfang modify 20090813,BUG:MS00638 QDV4赢时胜（上海）2009年8月15日01_B
                //=========================end MS00694=============================================
                "x join " +
                "(select a.*,b.FIVPayCatName,b.FIVType,c.FPortName,d.FRoundName,dd.FRoundName as FACRoundName,e.FFormulaName as FPerExpName," +
                //------ Start MS00017、MS00018 收支币种名称变更为现金帐户名称，运营品种类型名称；处理计提方式
                " vb.FVocName as FAccrueTypeName,vbpo.FVocName as FPayOriginName ," +
                /*added by yeshenghong 2013-6-8 Story 3958 */
				" d1.fcnvconent as FProfitLoss, d2.fcnvconent as FDeferral, d3.fcnvconent as FDigest, " + 
                /*end by yeshenghong 2013-6-8 Story 3958 */
                "f.FPeriodName,g.FUserName as FCreatorName,h.FUserName as FCheckUserName,k.FCashAccName as FCashAccName,kk.FFeeType " +
                //------ End MS00017、MS00018 modify by wangzuochun 2009.06.25、、panjunfang add 20090714
                sAry[0]                           //story 2253 add by zhouwei 20120221 分级组合查询
                + "  ,m.FHolidaysName ,cal.FSIName,cp.fportclsname " +
                		",pc.fcuryname as FLCuryName " + //add by huangqirong 2013-01-21 story #3488
                		" from " + //edit by zhangjun 2011-12-09
             pub.yssGetTableName("Tb_Para_InvestPay") +
                " a left join (select FIVPayCatCode,FIVPayCatName,FIVType from " +
                "Tb_Base_InvestPayCat where FCheckState=1) b on a.FIVPayCatCode=b.FIVPayCatCode" +
                //------ MS00017  取得运营品种类型名称 modify by wangzuochun 2009.06.25---------
                " left join (select m.FIVPayCatCode,m.FIVType,fiv.FVocName as FFeeType from " +
                "Tb_Base_InvestPayCat m left join Tb_Fun_Vocabulary fiv on m.FIVType = fiv.FVocCode and " +
                "fiv.FVocTypeCode = 'fiv_feeType') kk on kk.FIVPayCatCode = b.FIVPayCatCode" +
                //------MS00018 关联词汇表，取得计提方式名称   panjunfang add 20090714 -----------
                " left join Tb_Fun_Vocabulary vb on a.FAccrueType = vb.FVocCode and vb.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_YSSACCRUETYPE) +
                //------MS00018 关联词汇表，取得收支来源名称   panjunfang add 20090810 -----------
                " left join Tb_Fun_Vocabulary vbpo on a.FPayOrigin = vbpo.FVocCode and vbpo.FVocTypeCode = " +
                dbl.sqlString(YssCons.Yss_INVESTPAY) +
                //------ modify by wangzuochun  2010.07.16  MS01449    组合代码相同而启用日期不同的组合时，新建买入证券据，进行库存统计后，现金库存会增倍 QDV4赢时胜(测试)2010年7月15日01_B 
                //----------------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                " left join (select FPortCode, FPortName, FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1) c on a.FPortCode = c.FPortCode " +
              
                // end by lidaolong 
                //-------------------------------------------- MS01449 -------------------------------------------//
                
                
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) d on a.FRoundCode=d.FRoundCode" +
                
                //取得节假日的名称
                " left join (select FHolidaysCode,FHolidaysName from  Tb_Base_Holidays" +
                 
                " where FCheckState=1) m on a.FHolidaysCode=m.FHolidaysCode" +
                //edit by zhangjun 2011-12-08  关联利息算法设置表，取得算法名称  FCIMNAME
                " left join ( select FSICode,FSIName from TB_FUN_SPINGINVOKE " +
                "  ) cal on a.FARIEXPCODE = cal.FSICode " + 
                //----------end----------------------------------------
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) dd on a.FACROUNDCODE=dd.FRoundCode" +
                //------ Start MS00017  收支币种变更为现金帐户--------------
                " left join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) k on a.FCashAccCode = k.FCashAccCode " + 
                //------ End MS00017 modify by wangzuochun 2009.06.29 --- 
                " left join (select FFormulaCode,FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState=1) e on a.FPerExpCode=e.FFormulaCode" +
                " left join (select FPeriodCode,FPeriodName from " +
                pub.yssGetTableName("Tb_Para_Period") +
                " where FCheckState=1) f on a.FPeriodCode=f.FPeriodCode" +
                /*added by yeshenghong 2013-6-8 Story 3958 */
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_ProfitLossCost) + ") d1 " + 
				"  on a.FIVPayCatCode = d1.findcode " + 
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_AccrualDeferral) + ") d2 " + 
				"  on a.FIVPayCatCode = d2.findcode " + 
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_VoucherAbstract) + ") d3 " + 
				"  on a.FIVPayCatCode = d3.findcode " + 
				/*end by yeshenghong 2013-6-8 Story 3958 */
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) g on a.FCreator=g.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) h on a.FCheckUser=h.FUserCode" +
                //story 2253 add by zhouwei 20120221 分级组合查询 start-----------
                " left join (select * from "+pub.yssGetTableName("Tb_TA_PortCls")+" where fcheckstate=1) cp on a.FPortClsCode=cp.FPortClsCode"+
                //add by huangqirong 2013-01-21 story #3488
                " left join (select FCuryCode as flowercurrencycode ,FCuryName from " + pub.yssGetTableName("tb_para_currency") + 
                " where fcheckstate = 1) pc on a.flowercurrencycode = pc.flowercurrencycode " + 
                //---end---
                //story 2253 add by zhouwei 20120221 分级组合查询  end-------------
                sAry[1] +
                buildFilterSql() +
                //fanghaoln 20090924 MS00694 QDV4赢时胜（深圳）2009年9月10日01_B 使它按组合分组，加个组合的连接条件
                ") y on x.FIVPayCatCode = y.FIVPayCatCode and x.FStartDate = y.FStartDate and x.fportcode=y.fportcode" +
                //=========================end MS00694=============================================
                // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
                //=====================================================================================
                " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
                pub.getPrefixTB() + "' " +
                //===============================================================================
                " order by y.FCheckState, y.FCreateTime desc , y.FCheckTime desc";
        } catch (Exception e) {
            throw new YssException("获取投资运营收支设置信息" + "\r\n" + e.getMessage(), e);
        }
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {

        String strSql = "";
        String sAry[] = null;
        try {
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", YssOperCons.YSS_KCLX_InvestPayRec); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", YssOperCons.YSS_KCLX_InvestPayRec);
            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_InvestPayRec); //获得分析代码

            strSql =
                "select y.* from " +
                "(select FIVPayCatCode, max(FStartDate) as FStartDate" +
                " from " + pub.yssGetTableName("Tb_Para_InvestPay") +
                " where FCheckState<>2 and FStartDate<=" +
                dbl.sqlDate(new java.util.Date()) +
                " group by FIVPayCatCode)" + //去掉FStartDate，只查询出启用日期最近的那个 panjunfang modify 20090813,BUG:MS00638 QDV4赢时胜（上海）2009年8月15日01_B
                "x join " +
                "(select a.*,b.FIVPayCatName,b.FIVType,c.FPortName,d.FRoundName,dd.FRoundName as FACRoundName,e.FFormulaName as FPerExpName," +
                /*added by yeshenghong 2013-6-8 Story 3958 */
				" d1.fcnvconent as FProfitLoss, d2.fcnvconent as FDeferral, d3.fcnvconent as FDigest, " + 
                /*end by yeshenghong 2013-6-8 Story 3958 */
                " vb.FVocName as FAccrueTypeName,cal.FSIName, " + //edit by zhangjun 2011-12-09                         //story 2253 add by zhouwei 20120221
                "f.FPeriodName,g.FUserName as FCreatorName,h.FUserName as FCheckUserName,k.FCashAccName as FCashAccName,cp.fportclsname " +
                sAry[0]
                + " from " +
                pub.yssGetTableName("Tb_Para_InvestPay") +
                " a left join (select FIVPayCatCode,FIVPayCatName,FIVType from " +
                "Tb_Base_InvestPayCat where FCheckState=1) b on a.FIVPayCatCode=b.FIVPayCatCode" +
                " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState=1) c on a.FPortCode=c.FPortCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) d on a.FRoundCode=d.FRoundCode" +
                /*added by yeshenghong 2013-6-8 Story 3958 */
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_ProfitLossCost) + ") d1 " + 
				"  on a.FIVPayCatCode = d1.findcode " + 
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_AccrualDeferral) + ") d2 " + 
				"  on a.FIVPayCatCode = d2.findcode " + 
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_VoucherAbstract) + ") d3 " + 
				"  on a.FIVPayCatCode = d3.findcode " + 
				/*end by yeshenghong 2013-6-8 Story 3958 */
                " left join Tb_Fun_Vocabulary vb on a.FAccrueType = vb.FVocCode and vb.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_YSSACCRUETYPE) +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) dd on a.FACROUNDCODE=dd.FRoundCode" +
                //edit by zhangjun 2011-12-08  关联利息算法设置表，取得算法名称
                " left join ( select FSICode,FSIName from TB_FUN_SPINGINVOKE " +
                " ) cal on a.FARIEXPCODE = cal.FSICode " + 
                //----------end----------------------------------------
                " left join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) k on a.FCashAccCode = k.FCashAccCode " + 
                " left join (select FFormulaCode,FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState=1) e on a.FPerExpCode=e.FFormulaCode" +
                " left join (select FPeriodCode,FPeriodName from " +
                pub.yssGetTableName("Tb_Para_Period") +
                " where FCheckState=1) f on a.FPeriodCode=f.FPeriodCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) g on a.FCreator=g.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) h on a.FCheckUser=h.FUserCode" +
                //story 2253 add by zhouwei 20120221 分级组合查询 start-----------
                " left join (select * from "+pub.yssGetTableName("Tb_TA_PortCls")+" where fcheckstate=1) cp on a.FPortClsCode=cp.FPortClsCode"+
                //story 2253 add by zhouwei 20120221 分级组合查询  end-------------
                sAry[1] +
                buildFilterSql() +
                ") y on x.FIVPayCatCode = y.FIVPayCatCode and x.FStartDate = y.FStartDate" +
                " order by y.FCheckState, y.FCreateTime desc , y.FCheckTime desc";
        } catch (Exception e) {
            throw new YssException("获取投资运营收支设置信息" + "\r\n" + e.getMessage(), e);
        }
        return this.builderListViewData(strSql);

    }

    /**
     * 获取投资运营收支设置信息
     * 此方法已被修改
     * 修改时间：2010年2月25号
     * 修改人：方浩
     * MS01037 QDV4南方2010年3月18日01_B 
     * 方法的功能：处理收益计提两费计提的查询显示和跨组合群的查询处理
     * @throws YssException
     * @return String
     */
    public String getListViewData3() throws YssException {
        String strSql = "";
        String sAry[] = null;
        try {
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1",
                YssOperCons.YSS_KCLX_InvestPayRec); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2",
                YssOperCons.YSS_KCLX_InvestPayRec);
            sAry = this.operSql.storageAnalysisSql(YssOperCons.
                YSS_KCLX_InvestPayRec); //获得分析代码
            strSql =
                "select y.*,h.fassetgroupcode, h.fassetgroupname from " +
                //fanghaoln 20090924 MS00694 QDV4赢时胜（深圳）2009年9月10日01_B
                "(select FIVPayCatCode, max(FStartDate) as FStartDate,fportcode" +
                //=========================end MS00694=============================================
                " from " + pub.yssGetTableName("Tb_Para_InvestPay") +
                //----------------begin
                " where FStartDate<=" +
                //----------------end
                dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 "+//xuqiji 20100225 MS00938  QDV4赢时胜(上海)2010年1月18日1_B
                //MS00694 QDV4赢时胜（深圳）2009年9月10日01_B fanghaoln 20091023
                //dbl.sqlString(this.filterType != null&&this.filterType.portCode.trim().length() != 0 ? "and fportcode in("+ this.operSql.sqlCodes(this.filterType.portCode)+")" : " ")+
                //--------------------------------end -MS00694-----------------------------
                //fanghaoln 20090924 MS00694 QDV4赢时胜（深圳）2009年9月10日01_B 使它按组合分组，这样就能显示每个组合的管理费和托管费
                " group by FIVPayCatCode,fportcode)" +//去掉FStartDate，只查询出启用日期最近的那个 panjunfang modify 20090813,BUG:MS00638 QDV4赢时胜（上海）2009年8月15日01_B
                //=========================end MS00694=============================================
                "x join " +
                "(select a.*,b.FIVPayCatName,b.FIVType,c.FPortName,d.FRoundName,dd.FRoundName as FACRoundName,e.FFormulaName as FPerExpName," +
                //------ Start MS00017、MS00018 收支币种名称变更为现金帐户名称，运营品种类型名称；处理计提方式
                /*added by yeshenghong 2013-6-8 Story 3958 */
				" d1.fcnvconent as FProfitLoss, d2.fcnvconent as FDeferral, d3.fcnvconent as FDigest, " + 
                /*end by yeshenghong 2013-6-8 Story 3958 */
                " vb.FVocName as FAccrueTypeName,vbpo.FVocName as FPayOriginName ," +
                "f.FPeriodName,g.FUserName as FCreatorName,h.FUserName as FCheckUserName,k.FCashAccName as FCashAccName,kk.FFeeType " +
                //------ End MS00017、MS00018 modify by wangzuochun 2009.06.25、、panjunfang add 20090714
                sAry[0]
                + "  ,m.FHolidaysName ,cal.FSIName,cp.fportclsname from " +  //edit by zhangjun 2011-12-09
             pub.yssGetTableName("Tb_Para_InvestPay") +
                " a left join (select FIVPayCatCode,FIVPayCatName,FIVType from " +
                "Tb_Base_InvestPayCat where FCheckState=1) b on a.FIVPayCatCode=b.FIVPayCatCode" +
                /*added by yeshenghong 2013-6-8 Story 3958 */
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_ProfitLossCost) + ") d1 " + 
				"  on a.FIVPayCatCode = d1.findcode " + 
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_AccrualDeferral) + ") d2 " + 
				"  on a.FIVPayCatCode = d2.findcode " + 
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_VoucherAbstract) + ") d3 " + 
				"  on a.FIVPayCatCode = d3.findcode " + 
				/*end by yeshenghong 2013-6-8 Story 3958 */
                //------ MS00017  取得运营品种类型名称 modify by wangzuochun 2009.06.25---------
                " left join (select m.FIVPayCatCode,m.FIVType,fiv.FVocName as FFeeType from " +
                "Tb_Base_InvestPayCat m left join Tb_Fun_Vocabulary fiv on m.FIVType = fiv.FVocCode and " +
                "fiv.FVocTypeCode = 'fiv_feeType') kk on kk.FIVPayCatCode = b.FIVPayCatCode" +
                //------MS00018 关联词汇表，取得计提方式名称   panjunfang add 20090714 -----------
                " left join Tb_Fun_Vocabulary vb on a.FAccrueType = vb.FVocCode and vb.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_YSSACCRUETYPE) +
                //------MS00018 关联词汇表，取得收支来源名称   panjunfang add 20090810 -----------
                " left join Tb_Fun_Vocabulary vbpo on a.FPayOrigin = vbpo.FVocCode and vbpo.FVocTypeCode = " +
                dbl.sqlString(YssCons.Yss_INVESTPAY) +
                " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState=1) c on a.FPortCode=c.FPortCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) d on a.FRoundCode=d.FRoundCode" +
                
                //取得节假日的名称
                " left join (select FHolidaysCode,FHolidaysName from  Tb_Base_Holidays" +
                 
                " where FCheckState=1) m on a.FHolidaysCode=m.FHolidaysCode" +
                //edit by zhangjun 2011-12-08  关联利息算法设置表，取得算法名称
                " left join ( select FSICode,FSIName from TB_FUN_SPINGINVOKE " +
                "  ) cal on a.FARIEXPCODE = cal.FSICode " + 
                //----------end----------------------------------------
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) dd on a.FACROUNDCODE=dd.FRoundCode" +
                //------ Start MS00017  收支币种变更为现金帐户--------------
                " left join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) k on a.FCashAccCode = k.FCashAccCode " + //modify by lidaolong  #386 增加一个功能，能够自动支付管理费和托管费
                //------ End MS00017 modify by wangzuochun 2009.06.29 ---
                " left join (select FFormulaCode,FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState=1) e on a.FPerExpCode=e.FFormulaCode" +
                " left join (select FPeriodCode,FPeriodName from " +
                pub.yssGetTableName("Tb_Para_Period") +
                " where FCheckState=1) f on a.FPeriodCode=f.FPeriodCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) g on a.FCreator=g.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) h on a.FCheckUser=h.FUserCode" +
                //story 2253 add by zhouwei 20120221 分级组合查询 start-----------
                " left join (select * from "+pub.yssGetTableName("Tb_TA_PortCls")+" where fcheckstate=1) cp on a.FPortClsCode=cp.FPortClsCode"+
                //story 2253 add by zhouwei 20120221 分级组合查询  end-------------
                sAry[1] +
                buildFilterSql() +
                //fanghaoln 20090924 MS00694 QDV4赢时胜（深圳）2009年9月10日01_B 使它按组合分组，加个组合的连接条件
                ") y on x.FIVPayCatCode = y.FIVPayCatCode and x.FStartDate = y.FStartDate and x.fportcode=y.fportcode" +
                //=========================end MS00694=============================================
                // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
                //=====================================================================================
                " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
                pub.getPrefixTB() + "' " +
                //===============================================================================
                " order by y.FCheckState, y.FCreateTime desc , y.FCheckTime desc";
        } catch (Exception e) {
            throw new YssException("获取投资运营收支设置信息" + "\r\n" + e.getMessage(), e);
        }
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        String sAry[] = null;
        try {
            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_InvestPayRec); //获得分析代码
            strSql =
                "select a.*,b.FIVPayCatName,b.FIVType,c.FPortName,d.FRoundName,dd.FRoundName as FAcRoundName,e.FFormulaName as FPerExpName," +
                /*added by yeshenghong 2013-6-8 Story 3958 */
				" d1.fcnvconent as FProfitLoss, d2.fcnvconent as FDeferral, d3.fcnvconent as FDigest, " + 
                /*end by yeshenghong 2013-6-8 Story 3958 */
                " vb.FVocName as FAccrueTypeName, vbpo.FVocName as FPayOriginName,kk.FFeeType,pc.fcuryname as FLCuryName," + //modify huangqirong 2013-01-21 story #3488
                "f.FPeriodName,g.FUserName as FCreatorName,h.FUserName as FCheckUserName,k.FCashAccName as FCashAccName,m.FHolidaysName ,cal.FSIName,cp.fportclsname" + sAry[0] + // modify by wangzuochun 2010.02.12 MS00989  点击投资运营收支设置界面的全部显示复选框，报错  //ediy by zhangjun   
                " from " +
                pub.yssGetTableName("Tb_Para_InvestPay") +
                " a left join (select FIVPayCatCode,FIVPayCatName,FIVType from " +
                "Tb_Base_InvestPayCat where FCheckState=1) b on a.FIVPayCatCode=b.FIVPayCatCode" +
                " left join (select m.FIVPayCatCode,m.FIVType,fiv.FVocName as FFeeType from " +
                "Tb_Base_InvestPayCat m left join Tb_Fun_Vocabulary fiv on m.FIVType = fiv.FVocCode and " +
                "fiv.FVocTypeCode = 'fiv_feeType') kk on kk.FIVPayCatCode = b.FIVPayCatCode" +
                /*added by yeshenghong 2013-6-8 Story 3958 */
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_ProfitLossCost) + ") d1 " + 
				"  on a.FIVPayCatCode = d1.findcode " + 
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_AccrualDeferral) + ") d2 " + 
				"  on a.FIVPayCatCode = d2.findcode " + 
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_VoucherAbstract) + ") d3 " + 
				"  on a.FIVPayCatCode = d3.findcode " + 
				/*end by yeshenghong 2013-6-8 Story 3958 */
                " left join Tb_Fun_Vocabulary vbpo on a.FPayOrigin = vbpo.FVocCode and vbpo.FVocTypeCode = " +
                dbl.sqlString(YssCons.Yss_INVESTPAY) +
                " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState=1) c on a.FPortCode=c.FPortCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) d on a.FRoundCode=d.FRoundCode" +
                
                //------- add by wangzuochun 2010.02.12 MS00989    点击投资运营收支设置界面的全部显示复选框，报错    
                //取得节假日的名称
                " left join (select FHolidaysCode,FHolidaysName from  Tb_Base_Holidays" +
                 
                " where FCheckState=1) m on a.FHolidaysCode=m.FHolidaysCode" +
                //------- add by wangzuochun 2010.02.12 MS00989    点击投资运营收支设置界面的全部显示复选框，报错    
                //edit by zhangjun 2011-12-08  关联利息算法设置表，取得算法名称
                " left join ( select FSICode,FSIName from TB_FUN_SPINGINVOKE " +
                "  ) cal on a.FARIEXPCODE = cal.FSICode " + 
                //----------end----------------------------------------
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) dd on a.FAcRoundCode=dd.FRoundCode" +
                " left join (select FFormulaCode,FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState=1) e on a.FPerExpCode=e.FFormulaCode" +
                " left join (select FPeriodCode,FPeriodName from " +
                pub.yssGetTableName("Tb_Para_Period") +
                " where FCheckState=1) f on a.FPeriodCode=f.FPeriodCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) g on a.FCreator=g.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) h on a.FCheckUser=h.FUserCode" +
                //story 2253 add by zhouwei 20120221 分级组合查询 start-----------
                " left join (select * from "+pub.yssGetTableName("Tb_TA_PortCls")+" where fcheckstate=1) cp on a.FPortClsCode=cp.FPortClsCode"+
                //add by huangqirong 2013-01-21 story #3488
                " left join (select FCuryCode as flowercurrencycode, FCuryName from " + pub.yssGetTableName("tb_para_currency") +
                " where fcheckstate = 1) pc on a.flowercurrencycode = pc.flowercurrencycode " +
                //---end---
                //story 2253 add by zhouwei 20120221 分级组合查询  end-------------
                " left join Tb_Fun_Vocabulary vb on a.FAccrueType = vb.FVocCode and vb.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_YSSACCRUETYPE) +
                " left join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) k on a.FCashAccCode = k.FCashAccCode " + 
                sAry[1] +
                buildFilterSql() +
//                //--------- add by wangzuochun 
//                " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
//                pub.getPrefixTB() + "' " +
//                //---------
                
                " order by a.FCheckState, a.FCreateTime desc ,a.FCheckTime desc";
        } catch (Exception e) {
            throw new YssException("获取投资运营收支设置信息" + "\r\n" + e.getMessage(), e);
        }
        return this.builderListViewData(strSql);

    }

    /**
     * add by songjie 
     * 2011.04.27
     * QDV4赢时胜（测试）2011年4月27日02_B
     * @return
     * @throws YssException
     */
    private String getListViewData6() throws YssException {
        String strSql = "";
        String sAry[] = null;
        try {
            sAry = this.operSql.storageAnalysisSql(YssOperCons.
                YSS_KCLX_InvestPayRec); //获得分析代码
            strSql =
                "select y.*,h.fassetgroupcode, h.fassetgroupname from" +
                "(select FIVPayCatCode, max(FStartDate) as FStartDate,fportcode" +
                " from " + pub.yssGetTableName("Tb_Para_InvestPay") +
                //edit by songjie 2012.01.29 BUG 3685 QDV4农业银行2012年01月17日01_B 查询启用日期小于等于 收益计提界面的结束日期 的运营应收应付数据
                " where FStartDate<=" + dbl.sqlDate(this.fACEndDate) + " and FCheckState = 1 " + 
                " group by FIVPayCatCode,fportcode) x join " +
                "(select a.*,b.FIVPayCatName,b.FIVType,c.FPortName,d.FRoundName,dd.FRoundName as FACRoundName,e.FFormulaName as FPerExpName," +
                /*added by yeshenghong 2013-6-8 Story 3958 */
				" d1.fcnvconent as FProfitLoss, d2.fcnvconent as FDeferral, d3.fcnvconent as FDigest, " + 
                /*end by yeshenghong 2013-6-8 Story 3958 */
                " vb.FVocName as FAccrueTypeName,vbpo.FVocName as FPayOriginName ,pc.fcuryname as FLCuryName ," + //modify huangqirong 2013-01-21 story #3488
                "f.FPeriodName,g.FUserName as FCreatorName,h.FUserName as FCheckUserName,k.FCashAccName as FCashAccName,kk.FFeeType " +
                sAry[0] + " ,m.FHolidaysName ,cal.FSIName,cp.fportclsname from " + pub.yssGetTableName("Tb_Para_InvestPay") + //edit by zhangjun 2011-12-09
                " a left join (select FIVPayCatCode,FIVPayCatName,FIVType from " +
                "Tb_Base_InvestPayCat where FCheckState=1) b on a.FIVPayCatCode=b.FIVPayCatCode" +
                " left join (select m.FIVPayCatCode,m.FIVType,fiv.FVocName as FFeeType from " +
                "Tb_Base_InvestPayCat m left join Tb_Fun_Vocabulary fiv on m.FIVType = fiv.FVocCode and " +
                "fiv.FVocTypeCode = 'fiv_feeType') kk on kk.FIVPayCatCode = b.FIVPayCatCode" +
                /*added by yeshenghong 2013-6-8 Story 3958 */
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_ProfitLossCost) + ") d1 " + 
				"  on a.FIVPayCatCode = d1.findcode " + 
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_AccrualDeferral) + ") d2 " + 
				"  on a.FIVPayCatCode = d2.findcode " + 
				"  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") + 
				"  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_VoucherAbstract) + ") d3 " + 
				"  on a.FIVPayCatCode = d3.findcode " + 
				/*end by yeshenghong 2013-6-8 Story 3958 */
                " left join Tb_Fun_Vocabulary vb on a.FAccrueType = vb.FVocCode and vb.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_YSSACCRUETYPE) +
                " left join Tb_Fun_Vocabulary vbpo on a.FPayOrigin = vbpo.FVocCode and vbpo.FVocTypeCode = " +
                dbl.sqlString(YssCons.Yss_INVESTPAY) + " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState=1) c on a.FPortCode=c.FPortCode" +
                " left join (select FRoundCode,FRoundName from " + pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) d on a.FRoundCode=d.FRoundCode" +
                " left join (select FHolidaysCode,FHolidaysName from  Tb_Base_Holidays" +
                " where FCheckState=1) m on a.FHolidaysCode=m.FHolidaysCode" +
                " left join (select FRoundCode,FRoundName from " + pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) dd on a.FACROUNDCODE=dd.FRoundCode" + " left join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) k on a.FCashAccCode = k.FCashAccCode " + //modify by lidaolong  #386 增加一个功能，能够自动支付管理费和托管费
                //edit by zhangjun 2011-12-08  关联利息算法设置表，取得算法名称
                " left join (  select FSICode,FSIName from TB_FUN_SPINGINVOKE " +
                " ) cal on a.FARIEXPCODE = cal.FSICode " + 
                //----------end----------------------------------------
                " left join (select FFormulaCode,FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState=1) e on a.FPerExpCode=e.FFormulaCode" +
                " left join (select FPeriodCode,FPeriodName from " + pub.yssGetTableName("Tb_Para_Period") +
                " where FCheckState=1) f on a.FPeriodCode=f.FPeriodCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) g on a.FCreator=g.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) h on a.FCheckUser=h.FUserCode" +
                //story 2253 add by zhouwei 20120221 分级组合查询 start-----------
                " left join (select * from "+pub.yssGetTableName("Tb_TA_PortCls")+" where fcheckstate=1) cp on a.FPortClsCode=cp.FPortClsCode"+
                //add by huangqirong 2013-01-21 story #3488
                " left join (select FCuryCode as flowercurrencycode, FCuryName from " + pub.yssGetTableName("tb_para_currency") + 
                " where fcheckstate = 1) pc on a.flowercurrencycode = pc.flowercurrencycode " +
                //---end---
                //story 2253 add by zhouwei 20120221 分级组合查询  end-------------
                sAry[1] + buildFilterSql() +
                ") y on x.FIVPayCatCode = y.FIVPayCatCode and x.FStartDate = y.FStartDate and x.fportcode=y.fportcode" +
                " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" + pub.getPrefixTB() + "' " +

                " order by y.FCheckState, y.FCreateTime desc , y.FCheckTime desc";
        } catch (Exception e) {
            throw new YssException("获取投资运营收支设置信息" + "\r\n" + e.getMessage(), e);
        }
        return this.builderListViewData(strSql);
    }
    
    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        YssPreparedStatement pstPayDate = null;
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_InvestPay") +
                "(FIVPayCatCode,FPortCode,FHolidaysCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FRoundCode,FACROUNDCODE,FPerExpCode,FPeriodCode," +
                "FAccrueType," + 
                "FARIEXPCODE,FHIGHARITH," + //add by zhangjun 2011-12-09  STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
                "FAutoPay,FAutoPayType,FAutoPayDay,"+//add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
                "FFixRate,FPayOrigin,FACBeginDate,FACEndDate,FExpirDate,FACTotalMoney,FDesc,FStartDate," +
                " FTransition, FPaidIn, FTransitionDate, " + 
                "FCuryCode,FCashAccCode,FApportionType,FPortClsCode,FCheckState,FCreator,FCreateTime,FCheckUser," +
                "FLimitedAmount,FPeriodOfBC,FPayDate" +	//20120425 added by liubo.Story #2217.增加支付下限、补差时期、支付日三个个字段
                ",FLowerCurrencyCode,FSupplementDates" + //add by huangqirong 2013-01-21 story #3488
                ",FCommission" +	//20130204 added by liubo.Story #3414.支付两费时是否自动产生划款手续费
                ") values(" +
                dbl.sqlString(this.ivPayCatCode) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.holidaysCode) + "," +
                dbl.sqlString(this.analysisCode1 == null || this.analysisCode1.length() == 0 ? " " : this.analysisCode1) + "," +
                dbl.sqlString(this.analysisCode2 == null || this.analysisCode2.length() == 0 ? " " : this.analysisCode2) + "," +
                dbl.sqlString(this.analysisCode3 == null || this.analysisCode3.length() == 0 ? " " : this.analysisCode3) + "," +
                dbl.sqlString(this.roundCode) + "," +
                dbl.sqlString(this.AcroundCode) + "," +
                dbl.sqlString(this.perExpCode) + "," +
                dbl.sqlString(this.periodCode) + "," +
                dbl.sqlString(this.accrueTypeCode) + "," + //计提方式
                
                //add by zhangjun 2011-12-09 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
                dbl.sqlString(this.ariExpCode) + "," + //算法公式代码
                dbl.sqlString(this.highArith) + "," + //是否使用算法公式
                //end by zhangjun 2011-12-09
                
              //add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
                dbl.sqlString(this.autoPay)+","+
                dbl.sqlString(this.autoPayType)+","+
                this.autoPayDay+","+
              //end by lidaolong 20110309 
                
                (this.fACBeginDate == null ? this.fixRate : null) + "," +//通过开始日期是否为空来判断运营品种类型为两费还是预提待摊，若为预提待摊，则固定比率插入null值到数据表中 panjunfang modify 20090819
                (this.fACBeginDate == null ? Integer.valueOf(String.valueOf(this.payOrigin)) : null) + "," + //通过开始日期是否为空来判断运营品种类型为两费还是预提待摊，若为预提待摊，则收支来源插入null值到数据表中 panjunfang modify 20090819
                (this.fACBeginDate == null ? null : dbl.sqlDate(this.fACBeginDate)) + "," +//通过开始日期是否为空来判断运营品种类型为两费还是预提待摊，若为两费，则开始日期插入null值到数据表中 panjunfang modify 20090819
                (this.fACEndDate == null ? null : dbl.sqlDate(this.fACEndDate)) + "," +//通过结束日期是否为空来判断运营品种类型为两费还是预提待摊，若为两费，则结束日期插入null值到数据表中 panjunfang modify 20090819
                (this.ExpirDate == null ? null : dbl.sqlDate(this.ExpirDate)) + "," +//通过终止日期是否为空来判断运营品种类型为两费还是预提待摊，若为两费，则终止日期插入null值到数据表中 panjunfang modify 20090819
                (this.ExpirDate == null ? null : Double.valueOf(String.valueOf(this.fACTotalMoney))) + "," +//通过终止日期是否为空来判断运营品种类型为两费还是预提待摊，若为两费，则总金额插入null值到数据表中 panjunfang modify 20090819
                dbl.sqlString(this.desc) + "," +
                dbl.sqlDate(this.startDate) + "," +
                dbl.sqlString(this.sTransition) + "," +			//added by liubo.Story #2139.预提转待摊
                this.sPaidIn + "," +							//added by liubo.Story #2139.预提转待摊中的实付金额
                dbl.sqlDate(this.sTransitionDate) + "," +		//added by liubo.Story #2139.预提转待摊中的转换日期
                dbl.sqlString(" ") + "," +
                dbl.sqlString(this.cashAccCode) + "," +
                //------
                dbl.sqlString("".equals(this.ApportionType)?"0":this.ApportionType) + "," +	//20110809 added by liubo.Story #1227.均摊方式
                dbl.sqlString(this.clsPortCode)+","+//story 2253 add by zhouwei 20120221 组合分级代码
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + ", " +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + "," +
                this.sLimitedAmount + "," +				//20120425 added by liubo.Story #2217.支付下限
                dbl.sqlString(this.sPeriodOfBC) + ",";	//20120425 added by liubo.Story #2217.补差时期
                if (dbl.dbType == YssCons.DB_ORA) {
                	strSql = strSql +"EMPTY_CLOB()";
                } else {
                	strSql = strSql + dbl.sqlString(this.sPayDate);
                }
                strSql = strSql + "," + dbl.sqlString(this.lowerCurrencyCode) + "," + dbl.sqlString(this.supplementDate); //add by huangqirong 2013-01-21 story #3488
                strSql = strSql + "," + this.sCommission;	//20130204 added by liubo.Story #3414.支付两费时是否自动产生划款手续费
                strSql = strSql + ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            
            //20120510 added by liubo.Story #2217
            //为clob类型的FPayDate字段进行赋值
            //===============================
            if (dbl.dbType == YssCons.DB_ORA) {
                strSql = "SELECT FPayDate FROM " +
                	pub.yssGetTableName("Tb_Para_InvestPay") +	
                    " where FIVPAYCATCODE = " +
                    dbl.sqlString(this.ivPayCatCode) +
                    " and FStartDate = " + dbl.sqlDate(this.startDate) +
                    " and FPortCode=" + dbl.sqlString(this.portCode) +
                    " and FPortClsCode="+dbl.sqlString(this.clsPortCode)+//story 2253 add by zhouwei 20120221 组合分级
                    //------------2007.11.22 添加 蒋锦 使用主键作为查询条件 否则有可能违反唯一约束------------//
                    " and FAnalysisCode1 = " + dbl.sqlString(this.analysisCode1 == null || this.analysisCode1.length() == 0 ? " " : this.analysisCode1) +
                    " and FAnalysisCode2 = " + dbl.sqlString(this.analysisCode2 == null || this.analysisCode2.length() == 0 ? " " : this.analysisCode2) +
                    " and FAnalysisCode3 = " + dbl.sqlString(this.analysisCode3 == null || this.analysisCode3.length() == 0 ? " " : this.analysisCode3);

                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	  // modify by jsc 20120809 连接池对大对象的特殊处理
                	CLOB clob = dbl.CastToCLOB(rs.getClob("FPayDate"));
                   // CLOB clob = ( (OracleResultSet) rs).getCLOB("FPayDate");
                    clob.putString(1, sPayDate);
                    strSql = "UPDATE " +
                    	pub.yssGetTableName("Tb_Para_InvestPay") +		
                        " SET FPayDate = ? " +
                        " where FIVPAYCATCODE = " +
                        dbl.sqlString(this.ivPayCatCode) +
                        " and FStartDate = " + dbl.sqlDate(this.startDate) +
                        " and FPortCode=" + dbl.sqlString(this.portCode) +
                        " and FPortClsCode="+dbl.sqlString(this.clsPortCode)+//story 2253 add by zhouwei 20120221 组合分级
                        //------------2007.11.22 添加 蒋锦 使用主键作为查询条件 否则有可能违反唯一约束------------//
                        " and FAnalysisCode1 = " + dbl.sqlString(this.analysisCode1 == null || this.analysisCode1.length() == 0 ? " " : this.analysisCode1) +
                        " and FAnalysisCode2 = " + dbl.sqlString(this.analysisCode2 == null || this.analysisCode2.length() == 0 ? " " : this.analysisCode2) +
                        " and FAnalysisCode3 = " + dbl.sqlString(this.analysisCode3 == null || this.analysisCode3.length() == 0 ? " " : this.analysisCode3);

                    pstPayDate = dbl.getYssPreparedStatement(strSql);
                    pstPayDate.setClob(1, clob);
                    pstPayDate.executeUpdate();
                }
            }
            //=============end==================
            
            //--- add by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B start---//
			strSql = " delete from " + pub.yssGetTableName("tb_vch_dict") 
			   + " where findcode = " + dbl.sqlString(this.ivPayCatCode) + " and fportcode = ' ' "
			   + " and fdictcode in (" + dbl.sqlString(YssCons.YSS_VCH_DICT_ProfitLossCost)
			   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_AccrualDeferral)
			   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_VoucherAbstract) + ")";
		
			dbl.executeSql(strSql);
			//--- add by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B end---//
            
            /*added by yeshenghong 2013-6-8 Story 3958 */
            this.insertVchDict();
            /*end by yeshenghong 2013-6-8 Story 3958 */
            
            //添加运营关联项设置保存 QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
            InvestRelaSetBean investRelaBean = new InvestRelaSetBean();
            investRelaBean.setYssPub(pub);
            investRelaBean.setiVPayCatCode(ivPayCatCode);
            investRelaBean.setPortCode(portCode);
            investRelaBean.setAnalysisCode1(analysisCode1==null||analysisCode1.length()==0?" ":analysisCode1);
            investRelaBean.setAnalysisCode2(analysisCode2==null||analysisCode2.length()==0?" ":analysisCode2);
            investRelaBean.setAnalysisCode3(analysisCode3==null||analysisCode3.length()==0?" ":analysisCode3);
            investRelaBean.saveMutliSetting(sRelaInvest);
            //QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加投资运营收支设置失败！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstPayDate);
        }

        return null;
    }
    
    /*
     * 插入到凭证字典中
     * modified by yeshenghong story3958 20130618
     * 
     */
    private void insertVchDict() throws YssException
    {
    	String strSql = "";
		try {
			/*added by yeshenghong 2013-6-8 Story 3958 */
			//--- edit by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B start---//
			if(this.sProfitLoss.length() != 0){
				//--- edit by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B end---//
				strSql = " insert into " + pub.yssGetTableName("tb_vch_dict") + "(FDICTCODE,FINDCODE,FPORTCODE,FDICTNAME,"
						+ " FCNVCONENT,FDESC, FSUBDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" 
						+ " select distinct fdictcode, " + dbl.sqlString(this.ivPayCatCode) + ", ' ',fdictname, " 
						+ dbl.sqlString(this.sProfitLoss) + ",FDESC," + dbl.sqlString(this.ivPayCatName) 
						+ ", FCheckState,FCreator," + dbl.sqlString(this.creatorTime) + ","
						+ " FCHECKUSER,FCHECKTIME  from " + pub.yssGetTableName("tb_vch_dict") + " where fdictcode = "  
						+ dbl.sqlString(YssCons.YSS_VCH_DICT_ProfitLossCost) + " and fportcode = ' '  and rownum = 1 ";
				dbl.executeSql(strSql);
			}//add by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B
			//--- edit by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B start---//
			if(this.sDeferral.length() != 0){
				//--- edit by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B end---//
				strSql = " insert into " + pub.yssGetTableName("tb_vch_dict") + "(FDICTCODE,FINDCODE,FPORTCODE,FDICTNAME,"
						+ " FCNVCONENT,FDESC, FSUBDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" 
						+ " select distinct fdictcode, " + dbl.sqlString(this.ivPayCatCode) + ", ' ',fdictname, " 
						+ dbl.sqlString(this.sDeferral) + ",FDESC," + dbl.sqlString(this.ivPayCatName) 
						+ ", FCheckState,FCreator," + dbl.sqlString(this.creatorTime) + ","
						+ " FCHECKUSER,FCHECKTIME  from " + pub.yssGetTableName("tb_vch_dict") + " where fdictcode = "  
						+ dbl.sqlString(YssCons.YSS_VCH_DICT_AccrualDeferral) + " and fportcode = ' '  and rownum = 1 ";
				dbl.executeSql(strSql);
			}//add by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B
			//--- edit by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B start---//
			if(this.sDigest.length() != 0){
				//--- edit by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B end---//
				strSql = " insert into " + pub.yssGetTableName("tb_vch_dict") + "(FDICTCODE,FINDCODE,FPORTCODE,FDICTNAME,"
						+ " FCNVCONENT,FDESC, FSUBDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" 
						+ " select distinct fdictcode, " + dbl.sqlString(this.ivPayCatCode) + ", ' ',fdictname, " 
						+ dbl.sqlString(this.sDigest) + ",FDESC," + dbl.sqlString(this.ivPayCatName) 
						+ ", FCheckState,FCreator," + dbl.sqlString(this.creatorTime) + ","
						+ " FCHECKUSER,FCHECKTIME  from " + pub.yssGetTableName("tb_vch_dict") + " where fdictcode = "  
						+ dbl.sqlString(YssCons.YSS_VCH_DICT_VoucherAbstract) + " and fportcode = ' '  and rownum = 1 ";
				dbl.executeSql(strSql);
			}//add by songjie 2013.07.24 BUG 8757 QDV4鹏华基金2013年7月24日01_B
			/*end by yeshenghong 2013-6-8 Story 3958 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new YssException("插入到凭证字典出错！", e);
		} 
		
    }
    

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        if (this.analysisCode1 == null || this.analysisCode1.trim().length() == 0) {
            dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_InvestPay"),
                                   "FIVPayCatCode,FPortCode,FStartDate,fportclscode",
                                   this.ivPayCatCode + "," +
                                   this.portCode + "," +
                                   YssFun.formatDate(this.startDate)+","+
                                   this.clsPortCode,
                                   this.oldIvPayCatCode + "," +
                                   this.oldPortCode + "," +
                                   YssFun.formatDate(this.oldStartDate)+","+
                                   this.oldClsPortCode);
        } else {
            dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_InvestPay"),
                                   "FAnalysisCode1,FIVPayCatCode,FPortCode,FStartDate,fportclscode",
                                   this.analysisCode1 + "," + this.ivPayCatCode + "," +
                                   this.portCode + "," +
                                   YssFun.formatDate(this.startDate)+","+
                                   this.clsPortCode,
                                   this.oldAnalysisCode1 + "," + this.oldIvPayCatCode + "," +
                                   this.oldPortCode + "," +
                                   YssFun.formatDate(this.oldStartDate)+","+
                                   this.oldClsPortCode);
        }
    }

    /**
     * 修改时间：2008年3月26号
     * 修改人：单亮
     * 原方法功能：只能处理运营收支品种设置的审核和未审核的单条信息。
     * 新方法功能：可以处理运营收支品种设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理运营收支品种设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
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
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_Para_InvestPay") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = '" + pub.getUserCode() +
                        "', FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FIVPayCatCode = " +
                        dbl.sqlString(this.ivPayCatCode) +
                        " and FPortCode=" + dbl.sqlString(this.portCode) +
                        " and FStartDate = " + dbl.sqlDate(this.startDate) +
                        " and FPortClsCode="+dbl.sqlString(this.clsPortCode)+//story 2253 add by zhouwei 20120221 组合分级
                        //------------2007.11.22 添加 蒋锦 使用主键作为查询条件 否则有可能违反唯一约束------------//
                        " and FAnalysisCode1 = " +
                        dbl.sqlString(this.analysisCode1 == null ||
                                      this.analysisCode1.length() == 0 ? " " :
                                      this.analysisCode1) +
                        " and FAnalysisCode2 = " +
                        dbl.sqlString(this.analysisCode2 == null ||
                                      this.analysisCode2.length() == 0 ? " " :
                                      this.analysisCode2) +
                        " and FAnalysisCode3 = " +
                        dbl.sqlString(this.analysisCode3 == null ||
                                      this.analysisCode3.length() == 0 ? " " :
                                      this.analysisCode3);
                    //---------------------------------------------------------------------------------//
                    dbl.executeSql(strSql);
                    //添加运营关联项设置更新 QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
                    InvestRelaSetBean investRelaBean = new InvestRelaSetBean();
                    investRelaBean.setYssPub(pub);
                    investRelaBean.setiVPayCatCode(ivPayCatCode);
                    investRelaBean.setPortCode(portCode);
                    investRelaBean.setAnalysisCode1(analysisCode1==null||analysisCode1.length()==0?" ":analysisCode1);
                    investRelaBean.setAnalysisCode2(analysisCode2==null||analysisCode2.length()==0?" ":analysisCode2);
                    investRelaBean.setAnalysisCode3(analysisCode3==null||analysisCode3.length()==0?" ":analysisCode3);
                    investRelaBean.checkStateId = this.checkStateId;
                    investRelaBean.checkSetting();
                    //QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
                }
            }
            //如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (oldIvPayCatCode != null && !oldIvPayCatCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_InvestPay") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = '" + pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FIVPayCatCode = " +
                    dbl.sqlString(this.ivPayCatCode) +
                    " and FPortCode=" + dbl.sqlString(this.portCode) +
                    " and FStartDate = " + dbl.sqlDate(this.startDate) +
                    //------------2007.11.22 添加 蒋锦 使用主键作为查询条件 否则有可能违反唯一约束------------//
                    " and FAnalysisCode1 = " +
                    dbl.sqlString(this.analysisCode1 == null ||
                                  this.analysisCode1.length() == 0 ? " " :
                                  this.analysisCode1) +
                    " and FAnalysisCode2 = " +
                    dbl.sqlString(this.analysisCode2 == null ||
                                  this.analysisCode2.length() == 0 ? " " :
                                  this.analysisCode2) +
                    " and FAnalysisCode3 = " +
                    dbl.sqlString(this.analysisCode3 == null ||
                                  this.analysisCode3.length() == 0 ? " " :
                                  this.analysisCode3);
                //---------------------------------------------------------------------------------//
                dbl.executeSql(strSql);
                //添加运营关联项设置更新 QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
                InvestRelaSetBean investRelaBean = new InvestRelaSetBean();
                investRelaBean.setYssPub(pub);
                investRelaBean.setiVPayCatCode(ivPayCatCode);
                investRelaBean.setPortCode(portCode);
                investRelaBean.setAnalysisCode1(analysisCode1==null||analysisCode1.length()==0?" ":analysisCode1);
                investRelaBean.setAnalysisCode2(analysisCode2==null||analysisCode2.length()==0?" ":analysisCode2);
                investRelaBean.setAnalysisCode3(analysisCode3==null||analysisCode3.length()==0?" ":analysisCode3);
                investRelaBean.checkStateId = this.checkStateId;
                investRelaBean.checkSetting();
                //QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu

            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核投资运营收支设置信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //---------------------end
    }

    /**
     * 将信息放入到回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_InvestPay") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FIVPAYCATCODE = " +
                dbl.sqlString(this.oldIvPayCatCode) +
                " and FStartDate = " + dbl.sqlDate(this.oldStartDate) +
                " and FPortCode=" + dbl.sqlString(this.oldPortCode) +
                " and FPortClsCode="+dbl.sqlString(this.clsPortCode)+//story 2253 add by zhouwei 20120221 组合分级
                //------------2007.11.22 添加 蒋锦 使用主键作为查询条件 否则有可能违反唯一约束------------//
                " and FAnalysisCode1 = " + dbl.sqlString(this.analysisCode1 == null || this.analysisCode1.length() == 0 ? " " : this.analysisCode1) +
                " and FAnalysisCode2 = " + dbl.sqlString(this.analysisCode2 == null || this.analysisCode2.length() == 0 ? " " : this.analysisCode2) +
                " and FAnalysisCode3 = " + dbl.sqlString(this.analysisCode3 == null || this.analysisCode3.length() == 0 ? " " : this.analysisCode3);
            //---------------------------------------------------------------------------------//

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
          //添加运营关联项设置保存 QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
            InvestRelaSetBean investRelaBean = new InvestRelaSetBean();
            investRelaBean.setYssPub(pub);
            investRelaBean.setiVPayCatCode(oldIvPayCatCode);
            investRelaBean.setPortCode(oldPortCode);
            investRelaBean.setAnalysisCode1(analysisCode1==null||analysisCode1.length()==0?" ":analysisCode1);
            investRelaBean.setAnalysisCode2(analysisCode2==null||analysisCode2.length()==0?" ":analysisCode2);
            investRelaBean.setAnalysisCode3(analysisCode3==null||analysisCode3.length()==0?" ":analysisCode3);
            investRelaBean.checkStateId = this.checkStateId;
            investRelaBean.delSetting();
            //QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除投资运营收支设置信息出错！", e);
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
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        YssPreparedStatement pstPayDate = null;
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_InvestPay") +
                " set FIVPAYCATCODE = " +
                dbl.sqlString(this.ivPayCatCode) +
                ", FPORTCODE = " + dbl.sqlString(this.portCode) +
                ", FHOLIDAYSCODE = " + dbl.sqlString(this.holidaysCode) +
                ", FAnalysisCode1 = " + dbl.sqlString(this.analysisCode1 == null || this.analysisCode1.length() == 0 ? " " : this.analysisCode1) +
                ", FAnalysisCode2 = " + dbl.sqlString(this.analysisCode2 == null || this.analysisCode2.length() == 0 ? " " : this.analysisCode2) +
                ", FAnalysisCode3 = " + dbl.sqlString(this.analysisCode3 == null || this.analysisCode3.length() == 0 ? " " : this.analysisCode3) +
                ", FROUNDCODE = " + dbl.sqlString(this.roundCode) +
                ", FACROUNDCODE = " + dbl.sqlString(this.AcroundCode) +
                ", FPEREXPCODE = " + dbl.sqlString(this.perExpCode) +
                ", FPERIODCODE = " + dbl.sqlString(this.periodCode) +
                ", FTransition = " + dbl.sqlString(this.sTransition) +			//added by liubo.Story #2139.预提转待摊
                ", FPaidIn = " + this.sPaidIn +									//added by liubo.Story #2139.预提转待摊中的实付金额
                ", FTransitionDate = " + dbl.sqlDate(this.sTransitionDate) +	//added by liubo.Story #2139.预提转待摊中的转换日期
                ", FCommission = " + this.sCommission +							//20130204 added by liubo.Story #3414.支付两费时是否自动生成划款手续费
                
                //add by zhangjun 2011-12-09 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
                ",FARIEXPCODE = " + dbl.sqlString(this.ariExpCode) +  //算法公式代码
                ",FHIGHARITH = " + dbl.sqlString(this.highArith) +  //是否使用算法公式
                //end by zhangjun 2011-12-09
                
              //add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
                ",FAutoPay = "+dbl.sqlString(this.autoPay)+
            	",FAutoPayType = "+dbl.sqlString(this.autoPayType)+
            	",FAutoPayDay ="+this.autoPayDay+
              //end by lidaolong 20110309 
                
                ", FPAYORIGIN = " + (this.fACBeginDate == null ? Integer.valueOf(String.valueOf(this.payOrigin)) : null) +//通过开始日期是否为空来判断运营品种类型为两费还是预提待摊，若为预提待摊，则收支来源插入null值到数据表中 panjunfang modify 20090819
                ", FAccrueType = " + dbl.sqlString(this.accrueTypeCode) + //计提方式 MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A panjunfang add 20090714
                ", FFIXRATE = " + (this.fACBeginDate == null ? this.fixRate : null) +//通过开始日期是否为空来判断运营品种类型为两费还是预提待摊，若为预提待摊，则固定比率插入null值到数据表中 panjunfang modify 20090819
                ", FACBeginDate =" + (this.fACBeginDate == null ? null : dbl.sqlDate(this.fACBeginDate)) +//通过开始日期是否为空来判断运营品种类型为两费还是预提待摊，若为两费，则开始日期插入null值到数据表中 panjunfang modify 20090819
                ",FACEndDate =" + (this.fACEndDate == null ? null : dbl.sqlDate(this.fACEndDate)) +//通过结束日期是否为空来判断运营品种类型为两费还是预提待摊，若为两费，则结束日期插入null值到数据表中 panjunfang modify 20090819
                //------ Start MS00017 取得“终止日期”的值、收支币种变更为现金帐户---
                ",FExpirDate =" + (this.ExpirDate == null ? null : dbl.sqlDate(this.ExpirDate)) +//通过终止日期是否为空来判断运营品种类型为两费还是预提待摊，若为两费，则终止日期插入null值到数据表中 panjunfang modify 20090819
                ", FACTotalMoney =" + (this.ExpirDate == null ? null : Double.valueOf(String.valueOf(this.fACTotalMoney))) +//通过终止日期是否为空来判断运营品种类型为两费还是预提待摊，若为两费，则总金额插入null值到数据表中 panjunfang modify 20090819
                ", FDESC = " + dbl.sqlString(this.desc) +
                ", FSTARTDATE = " + dbl.sqlDate(this.startDate) +
                ", FCashAccCode = " + dbl.sqlString(this.cashAccCode) +
                ", FApportionType = " + dbl.sqlString("".equals(this.ApportionType)?"0":this.ApportionType) +   //20110808 added by liubo.Story #1227.均摊方式
                //------ End MS00017 add by wangzuochun 2009.06.25 -------------
                ", FLimitedAmount = " + this.sLimitedAmount + 				//20120425 added by liubo.Story #2217.支付下限
                ", FPeriodOfBC = " + dbl.sqlString(this.sPeriodOfBC);		//20120425 added by liubo.Story #2217.补差时期
            	//20120510 added by liubo.Story #2217
            	//更新支付日设置
            	//======================================
                if (dbl.dbType == YssCons.DB_ORA) {
                	strSql = strSql +", FPayDate = EMPTY_CLOB()";
                } else {
                	strSql = strSql + ", FPayDate = " + dbl.sqlString(this.sPayDate);
                }
            	//=================end=====================
                strSql = strSql + ",FLowerCurrencyCode =" + dbl.sqlString(this.lowerCurrencyCode) + ",FSupplementDates = " + dbl.sqlString(this.supplementDate) ; //add by huangqirong 2013-01-21 story #3488
                
                strSql = strSql + ", FPortClsCode = "+dbl.sqlString(this.clsPortCode)+//story 2253 add by zhouwei 20120221 组合分级
                ",FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FIVPAYCATCODE = " + dbl.sqlString(this.oldIvPayCatCode) +
                " and FPORTCODE=" + dbl.sqlString(this.oldPortCode) +
                " and FStartDate=" + dbl.sqlDate(this.oldStartDate) +
                " and FPortClsCode="+dbl.sqlString(this.oldClsPortCode)+//story 2253 add by zhouwei 20120221 组合分级
                //------------2007.11.22 添加 蒋锦 使用主键作为查询条件 否则有可能违反唯一约束------------//
                " and FAnalysisCode1 = " + dbl.sqlString(this.oldAnalysisCode1 == null || this.oldAnalysisCode1.length() == 0 ? " " : this.oldAnalysisCode1) + //
                " and FAnalysisCode2 = " + dbl.sqlString(this.oldAnalysisCode2 == null || this.oldAnalysisCode2.length() == 0 ? " " : this.oldAnalysisCode2) + //彭鹏 2008.3.26 BUG0000144
                " and FAnalysisCode3 = " + dbl.sqlString(this.oldAnalysisCode3 == null || this.oldAnalysisCode3.length() == 0 ? " " : this.oldAnalysisCode3); //
            //---------------------------------------------------------------------------------//
                
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            //20120510 added by liubo.Story #2217
            //为clob类型的FPayDate字段进行赋值
            //===============================
            if (dbl.dbType == YssCons.DB_ORA) {
                strSql = "SELECT FPayDate FROM " +
                	pub.yssGetTableName("Tb_Para_InvestPay") +	
                    " where FIVPAYCATCODE = " +
                    dbl.sqlString(this.ivPayCatCode) +
                    " and FStartDate = " + dbl.sqlDate(this.startDate) +
                    " and FPortCode=" + dbl.sqlString(this.portCode) +
                    " and FPortClsCode="+dbl.sqlString(this.clsPortCode)+//story 2253 add by zhouwei 20120221 组合分级
                    //------------2007.11.22 添加 蒋锦 使用主键作为查询条件 否则有可能违反唯一约束------------//
                    " and FAnalysisCode1 = " + dbl.sqlString(this.analysisCode1 == null || this.analysisCode1.length() == 0 ? " " : this.analysisCode1) +
                    " and FAnalysisCode2 = " + dbl.sqlString(this.analysisCode2 == null || this.analysisCode2.length() == 0 ? " " : this.analysisCode2) +
                    " and FAnalysisCode3 = " + dbl.sqlString(this.analysisCode3 == null || this.analysisCode3.length() == 0 ? " " : this.analysisCode3);

                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	  // modify by jsc 20120809 连接池对大对象的特殊处理
                	CLOB clob = dbl.CastToCLOB(rs.getClob("FPayDate"));
                    //CLOB clob = ( (OracleResultSet) rs).getCLOB("FPayDate");
                    clob.putString(1, sPayDate);
                    strSql = "UPDATE " +
                    	pub.yssGetTableName("Tb_Para_InvestPay") +		
                        " SET FPayDate = ? " +
                        " where FIVPAYCATCODE = " +
                        dbl.sqlString(this.ivPayCatCode) +
                        " and FStartDate = " + dbl.sqlDate(this.startDate) +
                        " and FPortCode=" + dbl.sqlString(this.portCode) +
                        " and FPortClsCode="+dbl.sqlString(this.clsPortCode)+//story 2253 add by zhouwei 20120221 组合分级
                        //------------2007.11.22 添加 蒋锦 使用主键作为查询条件 否则有可能违反唯一约束------------//
                        " and FAnalysisCode1 = " + dbl.sqlString(this.analysisCode1 == null || this.analysisCode1.length() == 0 ? " " : this.analysisCode1) +
                        " and FAnalysisCode2 = " + dbl.sqlString(this.analysisCode2 == null || this.analysisCode2.length() == 0 ? " " : this.analysisCode2) +
                        " and FAnalysisCode3 = " + dbl.sqlString(this.analysisCode3 == null || this.analysisCode3.length() == 0 ? " " : this.analysisCode3);

                    pstPayDate = dbl.getYssPreparedStatement(strSql);
                    pstPayDate.setClob(1, clob);
                    pstPayDate.executeUpdate();
                }
            }
            //=============end==================
            
            
            /*added by yeshenghong 2013-6-8 Story 3958 */
			strSql = " delete from " + pub.yssGetTableName("tb_vch_dict") 
				   + " where findcode = " + dbl.sqlString(this.ivPayCatCode) + " and fportcode = ' ' "
				   + " and fdictcode in (" + dbl.sqlString(YssCons.YSS_VCH_DICT_ProfitLossCost)
				   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_AccrualDeferral)
				   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_VoucherAbstract) + ")";
			
			dbl.executeSql(strSql);
            this.insertVchDict();
            /*end by yeshenghong 2013-6-8 Story 3958 */
            
            //添加运营关联项设置保存 QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
            InvestRelaSetBean investRelaBean = new InvestRelaSetBean();
            investRelaBean.setYssPub(pub);
            investRelaBean.setiVPayCatCode(oldIvPayCatCode);
            investRelaBean.setPortCode(oldPortCode);
            investRelaBean.setAnalysisCode1(oldAnalysisCode1==null||oldAnalysisCode1.length()==0?" ":oldAnalysisCode1);
            investRelaBean.setAnalysisCode2(oldAnalysisCode2==null||oldAnalysisCode2.length()==0?" ":oldAnalysisCode2);
            investRelaBean.setAnalysisCode3(oldAnalysisCode3==null||oldAnalysisCode3.length()==0?" ":oldAnalysisCode3);
            investRelaBean.saveMutliSetting(sRelaInvest);
            //QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新投资运营收支设置信息出错!", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstPayDate);
        }
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

    /**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        boolean analys1 = false;
        boolean analys2 = false;
        boolean analys3 = false;
        try {
            analys1 = operSql.storageAnalysis("FAnalysisCode1", "InvestPayRec");
            analys2 = operSql.storageAnalysis("FAnalysisCode2", "InvestPayRec");
            analys3 = operSql.storageAnalysis("FAnalysisCode3", "InvestPayRec");
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_InvestPay") +
                " where FIVPayCatCode =" + dbl.sqlString(this.ivPayCatCode) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                (analys1 ? " and FAnalysisCode1 = " + dbl.sqlString(this.analysisCode1) : "") +
                (analys2 ? " and FAnalysisCode2 = " + dbl.sqlString(this.analysisCode2) : "") +
                (analys3 ? " and FAnalysisCode3 = " + dbl.sqlString(this.analysisCode3) : "");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.ivPayCatCode = rs.getString("FIVPayCatCode");
                this.portCode = rs.getString("FPortCode");
                this.holidaysCode = rs.getString("FHolidaysCode");
                this.analysisCode1 = rs.getString("FAnalysisCode1");
                this.analysisCode2 = rs.getString("FAnalysisCode2");
                this.analysisCode3 = rs.getString("FAnalysisCode3");
                this.cashAccCode = rs.getString("FCashAccCode"); //收支币种变更为现金帐户
                this.roundCode = rs.getString("FRoundCode");
                this.perExpCode = rs.getString("FPerExpCode");
                this.periodCode = rs.getString("FPeriodCode");
                this.fixRate = rs.getBigDecimal("FFixRate");
                this.payOrigin = rs.getInt("FPayOrigin");
                this.accrueTypeCode = rs.getString("FAccrueType"); //计提方式
                //add by zhangjun 2011-12-09 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
                this.ariExpCode = rs.getString("FARIEXPCODE"); //算法公式代码
                this.highArith = rs.getString("FHIGHARITH");  //是否使用算法公式
                //end by zhangjun 2011-12-09-------------------------
                this.AcroundCode = rs.getString("FACRoundCode");
                this.fACBeginDate = rs.getDate("FACBeginDate");
                this.fACEndDate = rs.getDate("FACEndDate");
                this.ExpirDate = rs.getDate("FExpirDate"); //设置“终止日期”的值
                this.fACTotalMoney = rs.getDouble("FACTotalMoney");
                this.desc = rs.getString("FDesc");
                this.startDate = rs.getDate("FStartDate");
                this.ApportionType = rs.getString("FApportionType");	//20110809 added by liubo.Story #1227.均摊方式
                this.lowerCurrencyCode = rs.getString("FLOWERCURRENCYCODE");	//20130419 added by liubo.Story #3853.纳斯达克指数使用费的算法公式中，需要获取下限币种
            }
        } catch (Exception ex) {
            throw new YssException("获取运营收支的基本信息出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }
    
    public IDataSetting getSetting2() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_InvestPay") +
                " where FIVPayCatCode =" + dbl.sqlString(this.ivPayCatCode) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FPortClsCode = " + dbl.sqlString(this.clsPortCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.ivPayCatCode = rs.getString("FIVPayCatCode");
                this.portCode = rs.getString("FPortCode");
                this.holidaysCode = rs.getString("FHolidaysCode");
                this.cashAccCode = rs.getString("FCashAccCode"); //收支币种变更为现金帐户
                this.roundCode = rs.getString("FRoundCode");
                this.perExpCode = rs.getString("FPerExpCode");
                this.periodCode = rs.getString("FPeriodCode");
                this.fixRate = rs.getBigDecimal("FFixRate");
                this.payOrigin = rs.getInt("FPayOrigin");
                this.accrueTypeCode = rs.getString("FAccrueType"); //计提方式
                //add by zhangjun 2011-12-09 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
                this.ariExpCode = rs.getString("FARIEXPCODE"); //算法公式代码
                this.highArith = rs.getString("FHIGHARITH");  //是否使用算法公式
                //end by zhangjun 2011-12-09-------------------------
                this.AcroundCode = rs.getString("FACRoundCode");
                this.fACBeginDate = rs.getDate("FACBeginDate");
                this.fACEndDate = rs.getDate("FACEndDate");
                this.ExpirDate = rs.getDate("FExpirDate"); //设置“终止日期”的值
                this.fACTotalMoney = rs.getDouble("FACTotalMoney");
                this.desc = rs.getString("FDesc");
                this.startDate = rs.getDate("FStartDate");
                this.ApportionType = rs.getString("FApportionType");	//20110809 added by liubo.Story #1227.均摊方式
            }
        } catch (Exception ex) {
            throw new YssException("获取运营收支的基本信息出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     *把要返回的数据构造起来
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        try {
            buf.append(this.ivPayCatCode.trim()).append("\t");
            buf.append(this.ivPayCatName.trim()).append("\t");

            buf.append(this.portCode.trim()).append("\t");
            buf.append(this.portName.trim()).append("\t");

            buf.append(this.roundCode.trim()).append("\t");
            buf.append(this.roundName.trim()).append("\t");

            buf.append(this.AcroundCode.trim()).append("\t");
            buf.append(this.AcroundName.trim()).append("\t");

            buf.append(this.periodCode.trim()).append("\t");
            buf.append(this.periodName.trim()).append("\t");

            buf.append(this.perExpCode.trim()).append("\t");
            buf.append(this.perExpName.trim()).append("\t");

            buf.append(String.valueOf(this.fixRate)).append("\t");
            buf.append(YssFun.formatDate(this.fACBeginDate == null ?
                                         YssFun.parseDate("9998-12-31") :
                                         this.fACBeginDate)).append("\t");
            buf.append(YssFun.formatDate(this.fACEndDate == null ?
                                         YssFun.parseDate("9998-12-31") :
                                         this.fACEndDate)).append("\t");
            //拼接“终止日期”的字符串
            buf.append(YssFun.formatDate(this.ExpirDate == null ?
                                         YssFun.parseDate("9998-12-31") :
                                         this.ExpirDate)).append("\t");
            buf.append(String.valueOf(this.fACTotalMoney)).append("\t");
            buf.append(String.valueOf(this.payOrigin)).append("\t");
            buf.append(YssFun.formatDate(this.startDate)).append("\t");
            buf.append(this.desc).append("\t");
            buf.append(this.FIVType).append("\t");
            buf.append(this.analysisCode1).append("\t");
            buf.append(this.analysisCode2).append("\t");
            buf.append(this.analysisCode3).append("\t");
            buf.append(this.analysisName1).append("\t");
            //MS00694 QDV4赢时胜（深圳）2009年9月10日01_B fanghaoln 20091023多组合设置投资运营收支时，启用设置只显示最近启用的投资运营品种
            buf.append(this.cashAccCode).append("\t");
            buf.append(this.cashAccName).append("\t");
            //--------------------------------------------end MS00694-----------------------------------------------------------------
            // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
            buf.append(this.assetGroupCode).append("\t");
            buf.append(this.assetGroupName).append("\t");
            //---------------------------------------------------------------------------------------------
            
            buf.append(this.accrueTypeCode).append("\t"); //计提方式
			
			buf.append(this.holidaysCode.trim()).append("\t");
            buf.append(this.holidaysName.trim()).append("\t");
            //add by zhangjun 2011-12-08 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
            buf.append(this.ariExpCode.trim()).append("\t");
            buf.append(this.ariExpName.trim()).append("\t");
            buf.append(this.highArith.trim()).append("\t");
            //-----------------end----------------------------
          //add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
           buf.append(this.autoPay).append("\t");
           buf.append(this.autoPayType).append("\t");
           buf.append(this.autoPayDay).append("\t");
           buf.append(this.ApportionType).append("\t");	//20110809 added by liubo.Story #1227.均摊方式
            //end by lidaolong 20110309 
           buf.append(this.sTransition).append("\t");		//added by liubo.Story #2139.预提转待摊
           buf.append(String.valueOf(this.sPaidIn)).append("\t"); //added by liubo.Story #2139.预提转待摊中的实付金额
           //modify by zhangjun 2012.06.28 BUG4882浏览审核和未审核投资运营收支设置日志的时候报错 
           //buf.append(this.sTransitionDate).append("\t");	//added by liubo.Story #2139.预提转待摊中的转换日期
           
           buf.append(YssFun.formatDate(this.sTransitionDate == null ?
                   YssFun.parseDate("9998-12-31") :
                   this.sTransitionDate)).append("\t");
           
           //modify by zhangjun 2012.06.28 BUG4882浏览审核和未审核投资运营收支设置日志的时候报错 
           
           buf.append(this.clsPortCode).append("\t");
           buf.append(this.clsPortName).append("\t");//story 2253 add by zhouwei 20120221
           
           buf.append(String.valueOf(this.sLimitedAmount)).append("\t");	//20120425 added by liubo.Story #2217.补差时期
           buf.append(this.sPeriodOfBC).append("\t");		//20120425 added by liubo.Story #2217.支付下限
           buf.append(this.sPayDate).append("\t");			//20120425 added by liubo.Story #2217.支付日设置
           //add by huangqirong 2013-01-21 story #3488
           buf.append(this.lowerCurrencyCode).append("\t");
           buf.append(this.lowerCurrencyName).append("\t");
           buf.append(this.supplementDate).append("\t");
           //---end---
           buf.append(this.sCommission).append("\t");	//20130129 added by liubo.Story #3414.支付两费时是否自动支付划款手续费
           /*added by yeshenghong 2013-6-17 Story 3958 */
           buf.append(this.sProfitLoss).append("\t");
           buf.append(this.sDeferral).append("\t");
           buf.append(this.sDigest).append("\t");
		   /*end by yeshenghong 2013-6-17 Story 3958 */
           buf.append(super.buildRecLog());
        } catch (YssException e) {
            throw new YssException("更新投资运营收支设置信息出错!", e);
        }
        return buf.toString();
    }

    /**
     * 获取辅助字段之查询Sql语句
     * @return String
     */
    private String getInvestPayAnalysisSql() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_InvestPay") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_Cash);
        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                      /*  " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                        i +
                        " from  (select FBrokerCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_broker") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FBrokerCode )x " +
                        " join (select * from " +
                        pub.yssGetTableName("tb_para_broker") + ") y on x.FBrokerCode = y.FBrokerCode and x.FStartDate = y.FStartDate) broker on a.FAnalysisCode" +
                        i + " = broker.FBrokerCode";*/
                    
                    " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                    i +
                    " from " +
                    pub.yssGetTableName("tb_para_broker") +
                    " y where y.FCheckState = 1) broker on a.FAnalysisCode" +
                    i + " = broker.FBrokerCode";
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("003")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                     /*   " left join (select FExchangeCode,FExchangeName as FAnalysisName" +
                        i +
                        " from tb_base_exchange) e on a.FAnalysisCode" + i +
                        " = e.FExchangeCode " +
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        " join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) exchange on a.FAnalysisCode" +
                        i + " = exchange.FInvMgrCode";*/
                    
                    " left join (select FExchangeCode,FExchangeName as FAnalysisName" +
                    i +
                    " from tb_base_exchange) e on a.FAnalysisCode" + i +
                    " = e.FExchangeCode " +
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where n.FCheckState = 1 ) exchange on a.FAnalysisCode" +
                    i + " = exchange.FInvMgrCode";
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("004")) {
                    sResult = sResult +
                        " left join (select FCatCode,FCatName as FAnalysisName2 from Tb_Base_Category where FCheckState = 1) category on a.FAnalysisCode" +
                        i + " = category.FCatCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                /*        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                        i +
                        "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        "join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr on a.FAnalysisCode" +
                        i + " = invmgr.FInvMgrCode ";*/
                    
                    
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                    i +
                    "  from " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where n.FCheckState = 1 ) invmgr on a.FAnalysisCode" +
                    i + " = invmgr.FInvMgrCode ";
                    
                    //end by lidaolong
                }

                else {
                    sResult = sResult +
                        " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_InvestPay") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                        i + ".FAnalysisNull ";
                }
            }
        }
        return sResult;
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String strSql = "";
        String[] sAry = null;
        try {
            if (sType != null && sType.equalsIgnoreCase("listview5")) {
                //此方法用于取一个日期段内与一定组合的 费用 信息 by leeyu 080806
                String sList5 = "";
                boolean analy1 = operSql.storageAnalysis("FAnalysisCode1",
                    YssOperCons.YSS_KCLX_InvestPayRec); //判断分析代码存不存在
                boolean analy2 = operSql.storageAnalysis("FAnalysisCode2",
                    YssOperCons.YSS_KCLX_InvestPayRec);
                sAry = this.operSql.storageAnalysisSql(YssOperCons.
                    YSS_KCLX_InvestPayRec); //获得分析代码

                strSql =
                    "select show.* from (" +
                    "select y.* from " +
                    "(select FIVPayCatCode, max(FStartDate) as FStartDate" +
                    " from " + pub.yssGetTableName("Tb_Para_InvestPay") +
                    " where FCheckState<>2 and FStartDate<=" +
                    dbl.sqlDate(new java.util.Date()) +
                    " group by FIVPayCatCode,FStartDate)" +
                    "x join " +
                    "(select a.*,b.FIVPayCatName,b.FIVType,c.FPortName,d.FRoundName,dd.FRoundName as FACRoundName,e.FFormulaName as FPerExpName," +
                    " vb.FVocName as FAccrueTypeName, " + //计提方式
                    "f.FPeriodName,g.FUserName as FCreatorName,h.FUserName as FCheckUserName,k.FCashAccName as FCashAccName,cp.fportclsname " +
                    sAry[0]
                    + " from " +
                    pub.yssGetTableName("Tb_Para_InvestPay") +
                    " a left join (select FIVPayCatCode,FIVPayCatName,FIVType from " +
                    "Tb_Base_InvestPayCat where FCheckState=1) b on a.FIVPayCatCode=b.FIVPayCatCode" +
                    " left join (select FPortCode,FPortName from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where FCheckState=1) c on a.FPortCode=c.FPortCode" +
                    " left join (select FRoundCode,FRoundName from " +
                    pub.yssGetTableName("Tb_Para_Rounding") +
                    " where FCheckState=1) d on a.FRoundCode=d.FRoundCode" +

                    " left join (select FRoundCode,FRoundName from " +
                    pub.yssGetTableName("Tb_Para_Rounding") +
                    " where FCheckState=1) dd on a.FACROUNDCODE=dd.FRoundCode" +
                    " left join (select FCashAccCode,FCashAccName from " +
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    ") k on a.FCashAccCode = k.FCashAccCode" +
                    " left join (select FFormulaCode,FFormulaName from " +
                    pub.yssGetTableName("Tb_Para_Performula") +
                    " where FCheckState=1) e on a.FPerExpCode=e.FFormulaCode" +
                    //edit by zhangjun 2011-12-08  关联利息算法设置表，取得算法名称
                    " left join ( select FSICode,FSIName from TB_FUN_SPINGINVOKE " +
                    "  ) cal on a.FARIEXPCODE = cal.FSICode " + 
                    //----------end----------------------------------------
                    " left join (select FPeriodCode,FPeriodName from " +
                    pub.yssGetTableName("Tb_Para_Period") +
                    " where FCheckState=1) f on a.FPeriodCode=f.FPeriodCode" +
                    " left join Tb_Fun_Vocabulary vb on a.FAccrueType = vb.FVocCode and vb.FVocTypeCode = " +
                    dbl.sqlString(YssCons.YSS_YSSACCRUETYPE) +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) g on a.FCreator=g.FUserCode" +

                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) h on a.FCheckUser=h.FUserCode" +
                    //story 2253 add by zhouwei 20120221 分级组合查询 start-----------
                    " left join (select * from "+pub.yssGetTableName("Tb_TA_PortCls")+" where fcheckstate=1) cp on a.FPortClsCode=cp.FPortClsCode"+
                    //story 2253 add by zhouwei 20120221 分级组合查询  end-------------
                    sAry[1];
                if (filterType != null) {
                    strSql += " where 1=1 ";
                    if (filterType.portCode != null && filterType.portCode.length() > 0) {
                        strSql += " and a.FPortCode in(" + operSql.sqlCodes(filterType.portCode) + ")";
                    }

                }
                strSql +=
                    ") y on x.FIVPayCatCode = y.FIVPayCatCode and x.FStartDate = y.FStartDate" +
                    " order by y.FCheckState, y.FCreateTime desc , y.FCheckTime desc" +
                    ") show join " +
                    "(select distinct(FIvpayCatCode) as FIvPayCatCode from " +
                    pub.yssGetTableName("tb_stock_invest") +
                    " where FCheckState = 1 " +
                    " and FPortCode in(" + operSql.sqlCodes(filterType.portCode) +
                    ")  and FStorageDate between " +
                    dbl.sqlDate(filterType.fACBeginDate) + " and " +
                    dbl.sqlDate(filterType.fACEndDate) +
                    ")stock on show.FivPaycatCode=stock.FivPaycatCode";
                sList5 = this.builderListViewData(strSql);
                return sList5;
            }
            //---add by songjie 2011.04.27 BUG QDV4赢时胜（测试）2011年4月27日02_B---//
            else if(sType != null && sType.equalsIgnoreCase("listview6")) {
            	return getListViewData6();
            } 
            //---add by songjie 2011.04.27 BUG QDV4赢时胜（测试）2011年4月27日02_B---//
            else if(sType != null && sType.equalsIgnoreCase("cashaccinfo")){ // add by fangjiang 2011.02.14 #2279
            	return getCashAccInfo(this.portCode);
            } else {
                AssetStorageCfgBean assetgroupcfg = new AssetStorageCfgBean();
                assetgroupcfg.setYssPub(pub);
                return assetgroupcfg.getPartSetting(YssOperCons.
                    YSS_KCLX_InvestPayRec);
            }
        } catch (NullPointerException ex) {
            throw new YssException(ex.getMessage());  
        } catch (Exception ex) {
            throw new YssException("处理数据出错", ex);
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
            if (sRowStr.equals("")) {
                return;
            }
            
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==============end=======================
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.ivPayCatCode = reqAry[0];
            this.ivPayCatName = reqAry[1];
            if (reqAry[2].length() == 0) {
                this.portCode = " ";
            } else {
                this.portCode = reqAry[2];
            }
            this.roundCode = reqAry[3];
            this.AcroundCode = reqAry[4];
            this.perExpCode = reqAry[5];
            this.periodCode = reqAry[6];
            if(YssFun.isNumeric(reqAry[7])){
                fixRate = new BigDecimal(reqAry[7]);
            }
            if (reqAry[8].length() > 0) {
                this.fACBeginDate = YssFun.toDate(reqAry[8]);
            }
            if (reqAry[9].length() > 0) {
                this.fACEndDate = YssFun.toDate(reqAry[9]);
            }
            if (reqAry[10].length() > 0) {
                this.ExpirDate = YssFun.toDate(reqAry[10]);
            }
            if (reqAry[11].length() > 0) {
                this.fACTotalMoney = Double.parseDouble(reqAry[11]);
            }
            if (reqAry[12].length() > 0) {
                this.payOrigin = Integer.parseInt(reqAry[12]);
            }
            this.startDate = YssFun.toDate(reqAry[13]);
            this.desc = reqAry[14];
            this.checkStateId = Integer.parseInt(reqAry[15]);
            this.strIsOnlyColumns = reqAry[16];
            this.oldIvPayCatCode = reqAry[17]; ////////////////
            if (reqAry[18].length() == 0) {
                this.oldPortCode = " ";
            } else {
                this.oldPortCode = reqAry[18];
            }
            this.oldStartDate = YssFun.toDate(reqAry[19]);

            if (reqAry[20].length() == 0) {
                this.analysisCode1 = " ";
            } else {
                this.analysisCode1 = reqAry[20];
            }
            if (reqAry[21].length() == 0) {
                this.analysisCode2 = " ";
            } else {
                this.analysisCode2 = reqAry[21];
            }

            if (reqAry[22].length() == 0) {
                this.analysisCode3 = " ";
            } else {
                this.analysisCode3 = reqAry[22];
            }
            //------ Start MS00017 收支币种变更为现金帐户------------------------
            this.cashAccCode = reqAry[23];
            this.oldAnalysisCode1 = reqAry[24]; //修改时CHECK要判断，因为分析代码是主键。杨文奇20071008
            // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
            this.assetGroupCode = reqAry[25];
            this.assetGroupName = reqAry[26];
            this.holidaysCode=reqAry[27];
            //---------------------------------------------------------------------------------------------
            this.accrueTypeCode = reqAry[28]; //计提方式代码
			//------ End MS00017 modify by annexation 2009.06.29--------------
          //add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
            
            //add by zhangjun 2011-12-08 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
            this.ariExpCode = reqAry[29];
            this.ariExpName = reqAry[30];
            this.highArith = reqAry[31];
            //--------------end-----------------------------------
            this.autoPay = reqAry[32];
            this.autoPayType = reqAry[33];
            if(reqAry[31].length() !=0){
            	  this.autoPayDay = Integer.parseInt(reqAry[34]);
            }else{
            	this.autoPayDay =1;
            }

            this.ApportionType = reqAry[35];	//20110809 added by liubo.Story #1227.均摊方式
            
            this.sTransition = reqAry[36];		//added by liubo.Story #2139.预提转待摊
            this.sPaidIn = YssFun.toDouble(reqAry[37]);			//added by liubo.Story #2139.预提转待摊中的实付金额
            this.clsPortCode=reqAry[38];//story 2253 add by zhouwei 20120221 分级组合
            this.oldClsPortCode=reqAry[39];
            //---edit by songjie 2012.05.02 BUG 4394 QDV4赢时胜(上海)2012年04月26日03_B start---//
            if(reqAry[40].trim().length() == 0){//若reqAry[40]为""，则赋默认值"9998-12-31"
            	this.sTransitionDate = YssFun.toDate("9998-12-31");
            }else{
            	this.sTransitionDate = YssFun.toDate(reqAry[40]);	//added by liubo.Story #2139.预提转待摊中的转换日期
            }
            //---edit by songjie 2012.05.02 BUG 4394 QDV4赢时胜(上海)2012年04月26日03_B end---//
		    
		    this.sLimitedAmount = YssFun.toDouble(reqAry[41]);		//20120425 added by liubo.Story #2217.补差时期
		    this.sPeriodOfBC = reqAry[42];			//20120425 added by liubo.Story #2217.支付下限
		    this.sPayDate = reqAry[43];				//20120425 added by liubo.Story #2217.支付日设置
		    
          //end by lidaolong 20110309 
		    
		    // BUG4882浏览审核和未审核投资运营收支设置日志的时候报错  add by zhangjun 2012.06.28
            this.portName= reqAry[44];
            this.roundName = reqAry[45]; 
            this.AcroundName = reqAry[46]; 
            this.perExpName = reqAry[47];
            this.periodName = reqAry[48];
            this.cashAccName = reqAry[49];
            this.holidaysName = reqAry[50];
            // BUG4882浏览审核和未审核投资运营收支设置日志的时候报错  add by zhangjun 2012.06.28
            // add by huangqirong 2013-01-21 story #3488
            this.lowerCurrencyCode = reqAry[51];
            //this.lowerCurrencyName = reqAry[52];
            this.supplementDate = reqAry[52];
            //---end---
            
            this.sCommission = reqAry[53];		//20130129 added by liubo.Story #3414.支付两费时是否自动支付划款手续费
            /*added by yeshenghong 2013-6-17 Story 3958 */
            this.sProfitLoss = reqAry[54];	
            this.sDeferral = reqAry[55];	
            this.sDigest = reqAry[56];	
            /*end by yeshenghong 2013-6-17 Story 3958 */
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new InvestPayBean();

                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
                //添加运营关联项设置处理 QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
                if(sRowStr.split("\r\t").length == 3){
                	sRelaInvest =sRowStr.split("\r\t")[2];
                }
                //QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
            }
        } catch (Exception e) {
            throw new YssException("解析投资运营收支设置信息出错！", e);
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        InvestPayBean befInvestPayBean = new InvestPayBean();
        ResultSet rs = null;
        String strSql = "";
        try {

            strSql =
                "select a.*,b.FIVPayCatName,c.FPortName,d.FRoundName,e.FFormulaName as FPerExpName," +
                "f.FPeriodName,g.FUserName as FCreatorName,h.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_InvestPay") +
                " a left join (select FIVPayCatCode,FIVPayCatName from " +
                "Tb_Base_InvestPayCat where FCheckState=1) b on a.FIVPayCatCode=b.FIVPayCatCode" +
                " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState=1) c on a.FPortCode=c.FPortCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState=1) d on a.FRoundCode=d.FRoundCode" +
                " left join (select FFormulaCode,FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState=1) e on a.FPerExpCode=e.FFormulaCode" +
                " left join (select FPeriodCode,FPeriodName from " +
                pub.yssGetTableName("Tb_Para_Period") +
                " where FCheckState=1) f on a.FPeriodCode=f.FPeriodCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) g on a.FCreator=g.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) h on a.FCheckUser=h.FUserCode" +
                " where a.FIVPayCatCode=" + dbl.sqlString(this.oldIvPayCatCode) + " and a.FStartDate = " + dbl.sqlDate(this.oldStartDate);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befInvestPayBean.ivPayCatCode = rs.getString("FIVPAYCATCODE"); //
                befInvestPayBean.ivPayCatName = rs.getString("FIvPayCatName") + ""; //运营收支品种名称
                befInvestPayBean.portCode = rs.getString("FPortCode") + ""; //组合代码
                befInvestPayBean.portName = rs.getString("FPortName") + ""; //组合名称
                befInvestPayBean.holidaysCode = rs.getString("FHolidaysCode") + ""; 
                befInvestPayBean.roundCode = rs.getString("FRoundCode") + ""; //舍入代码
                befInvestPayBean.roundName = rs.getString("FRoundName") + ""; //舍入名称
                befInvestPayBean.AcroundCode = rs.getString("FACROUNDCODE") + "";
                befInvestPayBean.AcroundName = rs.getString("FACROUNDCODE") + "";
                befInvestPayBean.perExpCode = rs.getString("FPerExpCode") + ""; //比率公式代码
                befInvestPayBean.perExpName = rs.getString("FPerExpName") + ""; //比率公式名称
                befInvestPayBean.periodCode = rs.getString("FPeriodCode") + ""; //期间代码
                befInvestPayBean.periodName = rs.getString("FPeriodName") + ""; //期间名称
                befInvestPayBean.fixRate = rs.getBigDecimal("FFixRate"); //固定比率
                befInvestPayBean.fACBeginDate = rs.getDate("fACBeginDate");
                befInvestPayBean.fACEndDate = rs.getDate("fACEndDate");
                //------ Start MS00017 截止日期的处理-----------------------
                befInvestPayBean.ExpirDate = rs.getDate("FExpirDate");
                //------ End MS00017 add by wangzuochun 2009.06.2 --------
                
                /**shashijie 2012-7-5 BUG 4950 为空时插入日志报错,这里加上 + ""符号 */
                //--------edit by zhangjun 2011-12-08  关联利息算法设置表，取得算法名称
                befInvestPayBean.ariExpCode = rs.getString("FARIEXPCODE") + "";
                befInvestPayBean.highArith =rs.getString("FHIGHARITH") + "";
                //--------ediy by zhangjun 20121-12-08------------------
				/**end*/
                
                
                //add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
                befInvestPayBean.autoPay =rs.getString("FAutoPay");
                befInvestPayBean.autoPayType = rs.getString("FAutoPayType");
                befInvestPayBean.autoPayDay = rs.getInt("FAutoPayDay");
              //end by lidaolong 20110309 
                
                befInvestPayBean.fACTotalMoney = rs.getDouble("fACTotalMoney");
                befInvestPayBean.payOrigin = rs.getInt("FPayOrigin");
                befInvestPayBean.startDate = rs.getDate("FStartDate"); //启动日期
                befInvestPayBean.desc = rs.getString("FDESC");

            }
            return befInvestPayBean.buildRowStr();

        } catch (Exception e) {
            throw new YssException("获取投资运营收支设置修改前数据出错", e);
        }finally{
        	dbl.closeResultSetFinal(rs);//关闭RS by leeyu 2011-5-5
        }
    }

    /**
     * 从回收站删除数据，即彻底从数据库删除数据
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
                        pub.yssGetTableName("Tb_Para_InvestPay") +
                        " where FIVPayCatCode = " +
                        dbl.sqlString(this.ivPayCatCode) +
                        " and FPortCode=" + dbl.sqlString(this.portCode) +
                        " and FStartDate = " + dbl.sqlDate(this.startDate) +
                        " and FPortClsCode="+dbl.sqlString(this.clsPortCode)+//story 2253 add by zhouwei 20120221 组合分级
                        " and FAnalysisCode1 = " +
                        dbl.sqlString(this.analysisCode1 == null ||
                                      this.analysisCode1.length() == 0 ? " " :
                                      this.analysisCode1) +
                        " and FAnalysisCode2 = " +
                        dbl.sqlString(this.analysisCode2 == null ||
                                      this.analysisCode2.length() == 0 ? " " :
                                      this.analysisCode2) +
                        " and FAnalysisCode3 = " +
                        dbl.sqlString(this.analysisCode3 == null ||
                                      this.analysisCode3.length() == 0 ? " " :
                                      this.analysisCode3);

                    //执行sql语句
                    dbl.executeSql(strSql);
                	/*added by yeshenghong 2013-6-8 Story 3958 */
                    strSql = " delete from " + pub.yssGetTableName("tb_vch_dict") 
			 				   + " where findcode = " + dbl.sqlString(this.ivPayCatCode) + " and fportcode = ' ' "
			 				   + " and fdictcode in (" + dbl.sqlString(YssCons.YSS_VCH_DICT_ProfitLossCost)
			 				   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_AccrualDeferral)
			 				   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_VoucherAbstract) + ")";
					
					dbl.executeSql(strSql);
					/*end by yeshenghong 2013-6-8 Story 3958 */
                    //添加运营关联项设置更新 QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
                    InvestRelaSetBean investRelaBean = new InvestRelaSetBean();
                    investRelaBean.setYssPub(pub);
                    investRelaBean.setiVPayCatCode(ivPayCatCode);
                    investRelaBean.setPortCode(portCode);
                    investRelaBean.setAnalysisCode1(analysisCode1==null||analysisCode1.length()==0?" ":analysisCode1);
                    investRelaBean.setAnalysisCode2(analysisCode2==null||analysisCode2.length()==0?" ":analysisCode2);
                    investRelaBean.setAnalysisCode3(analysisCode3==null||analysisCode3.length()==0?" ":analysisCode3);
                    investRelaBean.deleteRecycleData();
                    //QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
                }
            }
            //sRecycled如果sRecycled为空，而oldIvPayCatCode不为空，则按照oldIvPayCatCode来执行sql语句
            else if (oldIvPayCatCode != "" && oldIvPayCatCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_InvestPay") +
                    " where FIVPayCatCode = " +
                    dbl.sqlString(this.ivPayCatCode) +
                    " and FPortCode=" + dbl.sqlString(this.portCode) +
                    " and FStartDate = " + dbl.sqlDate(this.startDate) +
                    " and FAnalysisCode1 = " +
                    dbl.sqlString(this.analysisCode1 == null ||
                                  this.analysisCode1.length() == 0 ? " " :
                                  this.analysisCode1) +
                    " and FAnalysisCode2 = " +
                    dbl.sqlString(this.analysisCode2 == null ||
                                  this.analysisCode2.length() == 0 ? " " :
                                  this.analysisCode2) +
                    " and FAnalysisCode3 = " +
                    dbl.sqlString(this.analysisCode3 == null ||
                                  this.analysisCode3.length() == 0 ? " " :
                                  this.analysisCode3);
                //执行sql语句
                dbl.executeSql(strSql);
                /*added by yeshenghong 2013-6-8 Story 3958 */
                strSql = " delete from " + pub.yssGetTableName("tb_vch_dict") 
				   + " where findcode = " + dbl.sqlString(this.ivPayCatCode) + " and fportcode = ' ' "
				   + " and fdictcode in (" + dbl.sqlString(YssCons.YSS_VCH_DICT_ProfitLossCost)
				   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_AccrualDeferral)
				   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_VoucherAbstract) + ")";
				
				dbl.executeSql(strSql);
				/*end by yeshenghong 2013-6-8 Story 3958 */
                //添加运营关联项设置更新 QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
                InvestRelaSetBean investRelaBean = new InvestRelaSetBean();
                investRelaBean.setYssPub(pub);
                investRelaBean.setiVPayCatCode(ivPayCatCode);
                investRelaBean.setPortCode(portCode);
                investRelaBean.setAnalysisCode1(analysisCode1==null||analysisCode1.length()==0?" ":analysisCode1);
                investRelaBean.setAnalysisCode2(analysisCode2==null||analysisCode2.length()==0?" ":analysisCode2);
                investRelaBean.setAnalysisCode3(analysisCode3==null||analysisCode3.length()==0?" ":analysisCode3);
                investRelaBean.deleteRecycleData();
                //QDV4中保2010年06月18日03_A MS01332 20100809 by leeyu
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
    
    // add by fangjiang 2011.02.14 #2279
    public String getCashAccInfo(String portCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String FctlValue ="";
        String FctlValue1="";
        String strCode="";
        String flag = "0" ;
        try {
         	strSql= " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
         			" where FPUBPARACODE = 'incomeFee' and FPARAID!=0 and FCtlCode='selctlPortCode'";
        	 rs = dbl.openResultSet(strSql);  
	         while(rs.next()){
	             FctlValue = rs.getString("FCTLVALUE");
	             strCode=FctlValue.substring(0,FctlValue.indexOf("|"));
	             if(strCode.equals(portCode)){
	            	 strSql = " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
		    	              " where FPUBPARACODE = 'incomeFee' " +
		    	              " and FCtlCode = 'cobYesNo'" +
		    	              " and FPARAID=(select min(FPARAID) from "+
		    	              pub.yssGetTableName("TB_PFOper_PUBPARA") +
		    	              " where FPUBPARACODE ='incomeFee' and FPARAID!=0 and FCTLVALUE=" +
		    	              dbl.sqlString(FctlValue) +")" ;
		    	      rs = dbl.openResultSet(strSql);  
   		              while(rs.next()){
    		        	  FctlValue1 = rs.getString("FCTLVALUE");
    		            } 
    		          if(FctlValue1.substring(0, FctlValue1.indexOf(",")).equals("1")){
    		        	  flag = "1";
   		              }    
	            	 break;
	             }
	         }
           return flag;
        }
        catch (Exception e) {
           throw new YssException("计算费用信息出错", e);
        }
        finally {
           dbl.closeResultSetFinal(rs);
        }
     }   

    public String getOldAnalysisCode2() {
        return oldAnalysisCode2;
    }

    public String getOldAnalysisCode3() {
        return oldAnalysisCode3;
    }

    public void setOldAnalysisCode3(String oldAnalysisCode3) {
        this.oldAnalysisCode3 = oldAnalysisCode3;
    }

    public void setOldAnalysisCode2(String oldAnalysisCode2) {
        this.oldAnalysisCode2 = oldAnalysisCode2;
    }

    public String getOldIvPayCatCode() {
        return oldIvPayCatCode;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public java.util.Date getOldStartDate() {
        return oldStartDate;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public String getCashAccName() {
        return cashAccName;
    }

    public String getAccrueTypeCode() {
        return accrueTypeCode;
    }

    public Date getExpirDate() {
        return ExpirDate;
    }

    public void setOldStartDate(java.util.Date oldStartDate) {
        this.oldStartDate = oldStartDate;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setOldIvPayCatCode(String oldIvPayCatCode) {
        this.oldIvPayCatCode = oldIvPayCatCode;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setCashAccName(String cashAccName) {
        this.cashAccName = cashAccName;
    }

    public void setAccrueTypeCode(String accrueTypeCode) {
        this.accrueTypeCode = accrueTypeCode;
    }

    public void setExpirDate(Date ExpirDate) {
        this.ExpirDate = ExpirDate;
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

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 从后台加载出我们跨组合群的内容
    public String getListViewGroupData1() throws YssException {
        this.bOverGroup = true;
        String sAllGroup = ""; //定义一个字符用来保存执行后的结果传到前台
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码
        String[] assetGroupCodes = this.filterType.assetGroupCode.split(YssCons.
            YSS_GROUPSPLITMARK); //按组合群的解析符解析组合群代码
        String[] strPortCodes = this.filterType.portCode.split(YssCons.
            YSS_GROUPSPLITMARK); //按组合群的解析符解析组合代码
        try {
            for (int i = 0; i < assetGroupCodes.length; i++) { //循环遍历每一个组合群
                this.assetGroupCode = assetGroupCodes[i]; //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode); //修改公共变量的当前组合群代码
                //fanghaoln 20100325 MS01037 QDV4南方2010年3月18日01_B 
                String sGroup = this.getListViewData3(); //调用以前的执行方法
                //-----------------------end---20100325--------------------------------
                sAllGroup = sAllGroup + sGroup + YssCons.YSS_GROUPSPLITMARK; //组合得到的结果集
            }
            if (sAllGroup.length() > 7) { //去除尾部多余的组合群解析符
                sAllGroup = sAllGroup.substring(0, sAllGroup.length() - 7);
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            pub.setPrefixTB(sPrefixTB); //还原公共变的里的组合群代码
            this.bOverGroup = false;
        }
        return sAllGroup; //把结果返回到前台进行显示

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
    
    public String getFAnalysisName1() {
        return analysisName1;
    }

    public void setFAnalysisName1(String FAnalysisName1) {
        this.analysisName1 = FAnalysisName1;
    }

    public String getFAnalysisName2() {
        return analysisName2;
    }

    public void setFAnalysisName2(String FAnalysisName2) {
        this.analysisName2 = FAnalysisName2;
    }

    public String getFAnalysisName3() {
        return analysisName3;
    }

    public void setFAnalysisName3(String FAnalysisName3) {
        this.analysisName3 = FAnalysisName3;
    }

    public String getFAnalysisCode2() {
        return analysisCode2;
    }

    public void setFAnalysisCode2(String FAnalysisCode2) {
        this.analysisCode2 = FAnalysisCode2;
    }

    public String getFAnalysisCode3() {
        return analysisCode3;
    }

    public void setFAnalysisCode3(String FAnalysisCode3) {
        this.analysisCode3 = FAnalysisCode3;
    }

    public String getFAnalysisCode1() {
        return analysisCode1;
    }

    public void setFAnalysisCode1(String FAnalysisCode1) {
        this.analysisCode1 = FAnalysisCode1;
    }

    public String getAcroundCode() {
        return AcroundCode;
    }

    public String getAcroundName() {
        return AcroundName;
    }

    public void setAcroundName(String AcroundName) {
        this.AcroundName = AcroundName;
    }

    public void setAcroundCode(String AcroundCode) {
        this.AcroundCode = AcroundCode;
    }

    public String getFIVType() {
        return FIVType;
    }

    public double getfACTotalMoney() {
        return fACTotalMoney;
    }

    public java.util.Date getfACBeginDate() {
        return fACBeginDate;
    }

    public java.util.Date getfACEndDate() {
        return fACEndDate;
    }

    public String getIvPayCatName() {
        return ivPayCatName;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getIvPayCatCode() {
        return ivPayCatCode;
    }

    public String getRoundCode() {
        return roundCode;
    }

    public String getRoundName() {
        return roundName;
    }

    public String getPeriodCode() {
        return periodCode;
    }

    public String getPerExpCode() {
        return perExpCode;
    }

    public String getPerExpName() {
        return perExpName;
    }

    public String getPeriodName() {
        return periodName;
    }

    public String getPortName() {
        return portName;
    }

    public void setFIVType(String FIVType) {
        this.FIVType = FIVType;
    }

    public void setfACTotalMoney(double fACTotalMoney) {
        this.fACTotalMoney = fACTotalMoney;
    }

    public void setfACEndDate(Date fACEndDate) {
        this.fACEndDate = fACEndDate;
    }

    public void setfACBeginDate(Date fACBeginDate) {
        this.fACBeginDate = fACBeginDate;
    }

    public void setFixRate(BigDecimal fixRate) {
        this.fixRate = fixRate;
    }

    public void setIvPayCatName(String ivPayCatName) {
        this.ivPayCatName = ivPayCatName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setIvPayCatCode(String ivPayCatCode) {
        this.ivPayCatCode = ivPayCatCode;
    }

    public void setRoundCode(String roundCode) {
        this.roundCode = roundCode;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public void setPeriodCode(String periodCode) {
        this.periodCode = periodCode;
    }

    public void setPerExpCode(String perExpCode) {
        this.perExpCode = perExpCode;
    }

    public void setPerExpName(String perExpName) {
        this.perExpName = perExpName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setIvPayType(int ivPayType) {
        this.ivPayType = ivPayType;
    }

    public void setSubTsfTypeCode(String subTsfTypeCode) {
        this.subTsfTypeCode = subTsfTypeCode;
    }

    public void setTsfTypeCode(String tsfTypeCode) {
        this.tsfTypeCode = tsfTypeCode;
    }

    public void setPayOrigin(int payOrigin) {
        this.payOrigin = payOrigin;
    }

    public void setOldAnalysisCode1(String oldAnalysisCode1) {
        this.oldAnalysisCode1 = oldAnalysisCode1;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public BigDecimal getFixRate() {
        return fixRate;
    }

    public int getIvPayType() {
        return ivPayType;
    }

    public String getSubTsfTypeCode() {
        return subTsfTypeCode;
    }

    public String getTsfTypeCode() {
        return tsfTypeCode;
    }

    public int getPayOrigin() {
        return payOrigin;
    }

    public String getOldAnalysisCode1() {
        return oldAnalysisCode1;
    }

    public String getCuryName() {
        return curyName;
    }

    public String getCuryCode() {
        return curyCode;
    }
    
    public String getHolidaysName() {
		return holidaysName;
	}

	public void setHolidaysName(String holidaysName) {
		this.holidaysName = holidaysName;
	}

	public String getHolidaysCode() {
		return holidaysCode;
	}

	public void setHolidaysCode(String holidaysCode) {
		this.holidaysCode = holidaysCode;
	}
    public String getApportionType() {
		return ApportionType;
	}

	public void setApportionType(String apportionType) {
		ApportionType = apportionType;
	}
	
	//--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 ----//
    public String getAttrClsCode() {
		return attrClsCode;
	}

	public void setAttrClsCode(String attrClsCode) {
		this.attrClsCode = attrClsCode;
	}
	//--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao end -----------//


	public String getAriExpCode() {
		return ariExpCode;
	}


	public void setAriExpCode(String ariExpCode) {
		this.ariExpCode = ariExpCode;
	}


	public String getAriExpName() {
		return ariExpName;
	}


	public void setAriExpName(String ariExpName) {
		this.ariExpName = ariExpName;
	}


	public String getHighArith() {
		return highArith;
	}


	public void setHighArith(String highArith) {
		this.highArith = highArith;
	}
	
	public String getTransition() {
		return sTransition;
	}


	public void setTransition(String sTransition) {
		this.sTransition = sTransition;
	}


	public double getPaidIn() {
		return sPaidIn;
	}


	public void setPaidIn(double sPaidIn) {
		this.sPaidIn = sPaidIn;
	}


	public Date getTransitionDate() {
		return sTransitionDate;
	}


	public void setTransitionDate(Date sTransitionDate) {
		this.sTransitionDate = sTransitionDate;
	}

    
	public String getPeriodOfBC() {
		return sPeriodOfBC;
	}


	public void setPeriodOfBC(String sPeriodOfBC) {
		this.sPeriodOfBC = sPeriodOfBC;
	}


	public double getLimitedAmount() {
		return sLimitedAmount;
	}


	public void setLimitedAmount(double sLimitedAmount) {
		this.sLimitedAmount = sLimitedAmount;
	}



	public String getPayDate() {
		return sPayDate;
	}


	public void setPayDate(String sPayDate) {
		this.sPayDate = sPayDate;
	}


}
