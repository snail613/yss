package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.manager.InvestPayAdimin;
import com.yss.util.*;

public class InvestPayRecBean
    extends BaseDataSettingBean implements IDataSetting {

    private String Num = ""; //编号
    private String FIVPayCatCode = ""; //运营收支品种代码
    private String FIVPayCatName = "";
    private java.util.Date TradeDate; //业务日期
    //MS00205 QDV4赢时胜上海2009年01月16日02_A add by 宋洁 2009-01-23 初始化查询开始的业务日期
    private java.util.Date BeginTradeDate;
    //MS00205 QDV4赢时胜上海2009年01月16日02_A add by 宋洁 2009-01-23 初始化查询结束的业务日期
    private java.util.Date EndTradeDate;
    private String PortCode = ""; //组合代码
    private String PortName = "";
    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090519
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    //------------------------------------------------------------------------------------


    private String AnalysisCode1 = ""; //分析代码1
    private String AnalysisName1 = "";
    private String AnalysisCode2 = ""; //分析代码2
    private String AnalysisName2;
    private String AnalysisCode3 = ""; //分析代码3
    private String AnalysisName3 = "";

    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    private String strAttrClsCode = "";//所属分类
    private String strAttrClsName = "";
    private String strOldAttrClsCode = "";
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    
    private int FDataSource = 0; //来源标识  0－自动计算；1－手工
    private int FStockInd = 0; //入账标识 0-未入账；1-已入帐

    private double Money; //原币金额

    private double BaseCuryRate; //基础汇率
    private double BaseCuryMoney; //基础货币金额

    private double PortCuryRate; //组合汇率
    private double PortCuryMoney; //组合货币金额
    private InvestPayRecBean filterType;
    private String strIsOnlyColumns = "0"; //在初始登陆时是否只显示列，不查询数据
    private String TsftTypeCode = ""; //业务类型
    private String TsftTypeName = "";
    private String SubTsfTypeCode = ""; //业务子类型
    private String SubTsfTypeName = "";

    private String CuryCode = ""; //币种代码
    private String FCuryName = "";

    private java.util.Date startDate; //起始日期
    private java.util.Date endDate; //终止日期
    private String sRecycled = ""; //保存未解析前的字符串
    private String sdesc = ""; //  2008-4-22 单亮
//======add by xuxuming,2010.01.18.MS00917=============
    private String clsPortCode="";
    private String clsPortName="";
    private String relaType="";
    public String getRelaType() {
		return relaType;
	}

	public void setRelaType(String relaType) {
		this.relaType = relaType;
	}

	public int getFStockInd() {
		return FStockInd;
	}

	public void setFStockInd(int fStockInd) {
		FStockInd = fStockInd;
	}
	
public InvestPayRecBean getFilterType() {
		return filterType;
	}

	public void setFilterType(InvestPayRecBean filterType) {
		this.filterType = filterType;
	}

	//===========end=========================================
	//--MS00007 add by songjie 2009-03-13
    public String getNum() {
        return Num;
    }

    public void setNum(String Num) {
        this.Num = Num;
    }

//--MS00007 add by songjie 2009-03-13
    /**
     * 获取审核状态
     * @return int
     */
    public int getCheckState() {
        return this.checkStateId;
    }

//-------------------------sj edit 20080626-------------------------------------
    public void setCheckState(int checkState) {
        checkStateId = checkState;
    }

    public String getDesc() {
        return sdesc;
    }

//------------------------------------------------------------------------------
    public void setDesc(String sdesc) {
        this.sdesc = sdesc;
    }

    public String getCuryCode() {
        return CuryCode;
    }

    public void setCuryCode(String CuryCode) {
        this.CuryCode = CuryCode;
    }

    public String getFCuryName() {
        return FCuryName;
    }

    public void setFCuryName(String FCuryName) {
        this.FCuryName = FCuryName;
    }

    public void setTsftTypeCode(String TsftTypeCode) {
        this.TsftTypeCode = TsftTypeCode;
    }

    public String getTsftTypeCode() {
        return TsftTypeCode;
    }

    public void setSubTsfTypeCode(String SubTsfTypeCode) {
        this.SubTsfTypeCode = SubTsfTypeCode;
    }

    public String getSubTsfTypeCode() {
        return SubTsfTypeCode;
    }

    public void setPortCuryRate(double PortCuryRate) {
        this.PortCuryRate = PortCuryRate;
    }

    public double getPortCuryRate() {
        return PortCuryRate;
    }

    public void setBaseCuryRate(double BaseCuryRate) {
        this.BaseCuryRate = BaseCuryRate;
    }

    public double getBaseCuryRate() {
        return BaseCuryRate;
    }

    public void setFIVPayCatCode(String FIVPayCatCode) {
        this.FIVPayCatCode = FIVPayCatCode;
    }

    public String getFIVPayCatCode() {
        return FIVPayCatCode;
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

	public String getStrOldAttrClsCode() {
		return strOldAttrClsCode;
	}

	public void setStrOldAttrClsCode(String strOldAttrClsCode) {
		this.strOldAttrClsCode = strOldAttrClsCode;
	}
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    
    public void setTradeDate(java.util.Date TradeDate) {
        this.TradeDate = TradeDate;
    }

    public java.util.Date getTradeDate() {
        return TradeDate;
    }

    //-----------MS00205 QDV4赢时胜上海2009年01月16日02_A  add by 宋洁 2009-01-23 用于添加新加的变量BeginTradeDate的set方法
    public void setBeginTradeDate(java.util.Date BeginTradeDate) {
        this.BeginTradeDate = BeginTradeDate;
    }

    //----------MS00205 QDV4赢时胜上海2009年01月16日02_A add by 宋洁 2009-01-23 用于添加新加的变量BeginTradeDate的set方法

    //----------MS00205 QDV4赢时胜上海2009年01月16日02_A add by 宋洁 2009-01-23 用于添加新加的变量BeginTradeDate的get方法
    public java.util.Date getBeginTradeDate() {
        return BeginTradeDate;
    }

    //----------MS00205 QDV4赢时胜上海2009年01月16日02_A add by 宋洁 2009-01-23 用于添加新加的变量BeginTradeDate的get方法

    //----------MS00205 QDV4赢时胜上海2009年01月16日02_A add by 宋洁 2009-01-23 用于添加新加的变量EndTradeDate的set方法
    public void setEndTradeDate(java.util.Date EndTradeDate) {
        this.EndTradeDate = EndTradeDate;
    }

    //----------MS00205 QDV4赢时胜上海2009年01月16日02_A add by 宋洁 2009-01-23 用于添加新加的变量EndTradeDate的set方法

    //----------MS00205 QDV4赢时胜上海2009年01月16日02_A add by 宋洁 2009-01-23 用于添加新加的变量EndTradeDate的get方法
    public java.util.Date getEndTradeDate() {
        return EndTradeDate;
    }

    //----------MS00205 QDV4赢时胜上海2009年01月16日02_A add by 宋洁 2009-01-23 用于添加新加的变量EndTradeDate的get方法

    public void setPortCode(String PortCode) {
        this.PortCode = PortCode;
    }

    public String getPortCode() {
        return PortCode;
    }

    // MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090521
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

    //------------------------------------------------------------------------------------


    public void setAnalysisCode1(String AnalysisCode1) {
        this.AnalysisCode1 = AnalysisCode1;
    }

    public String getAnalysisCode1() {
        return AnalysisCode1;
    }

    public void setAnalysisCode2(String AnalysisCode2) {
        this.AnalysisCode2 = AnalysisCode2;
    }

    public void setAnalysisCode3(String AnalysisCode3) {
        this.AnalysisCode3 = AnalysisCode3;
    }

    public String getAnalysisCode2() {
        return AnalysisCode2;
    }

    public String getAnalysisCode3() {
        return AnalysisCode3;
    }

    public void setMoney(double Money) {
        this.Money = Money;
    }

    public double getMoney() {
        return Money;
    }

    public void setBaseCuryMoney(double BaseCuryMoney) {
        this.BaseCuryMoney = BaseCuryMoney;
    }

    public double getBaseCuryMoney() {
        return BaseCuryMoney;
    }

    public void setPortCuryMoney(double PortCuryMoney) {
        this.PortCuryMoney = PortCuryMoney;
    }

    public double getPortCuryMoney() {
        return PortCuryMoney;
    }

    public int getFDataSource() {
		return FDataSource;
	}

	public void setFDataSource(int fDataSource) {
		FDataSource = fDataSource;
	}

	public InvestPayRecBean() {
    }

    /**
     * 此方法已被修改
     * 修改时间：2008年2月28号
     * 修改人：单亮
     * 原方法的功能：查询出应收应付数据数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sVocStr = "";
        String sShowCols = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            sShowCols = this.getListView1ShowCols();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.strIsOnlyColumns.equals("1")) {
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_CSP_DATASOURCE + "," +
                                            YssCons.YSS_CSP_STOCKIND);

                String reStr = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                    "\r\f" + sShowCols + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
                return reStr;
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql = " select y.* from " +
                "(select FNum from " +
                pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " " +
                //修改前的代码
                //" where FCheckState <> 2 ) x join";
                //修改后的代码
                //----------------------------begin
                " ) x join";
            //----------------------------end
            strSql = strSql +
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " h.FPortName as FPortName, s.FTsfTypeName as FTsfTypeName, m.FSubTsfTypeName as FSubTsfTypeName,n.FCuryName as FCuryName,z.FINVMGRNAME as FINVMGRNAME" +
                " ,w.FBrokerName as FBrokerName" + //添加获取券商的信息 sj modified 20081218 MS00108
                //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
                ",q.FCatName as FCatName,cp.fportclsname" +
                //--------------------------------------------------------------------------------//
                //",voc1.FVocName as  DataSourceName, voc2.FVocName as StockIndName
                ",r.FIVPayCatName as FIVPayCatName"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " ,nvl(j.FAttrClsName,' ') as FAttrClsName";
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            strSql = strSql +
                (this.FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " a " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) s on a.FTsfTypeCode = s.FTsfTypeCode " +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) m on m.FSubTsfTypeCode = a.FSubTsfTypeCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " ) n on n.FCuryCode = a.FCuryCode " +
                " left join (select FINVMGRCODE,FINVMGRNAME from  " +
                pub.yssGetTableName("tb_para_investmanager") +
                " group by FINVMGRCODE,FINVMGRNAME ) z on z.FINVMGRCODE = a.FAnalysisCode1  " +
                //------添加获取券商的信息 sj modified 20081218  MS00108 ------------------------------------//
                " left join (select FBrokerCode,FBrokerName from  " +
                pub.yssGetTableName("tb_Para_Broker") +
                " group by FBrokerCode,FBrokerName ) w on w.FBrokerCode = a.FAnalysisCode2  " +
                //--------------------------------------------------------------------------------//
                //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
                " left join (select FCatCode,FCatName from  " +
                pub.yssGetTableName("Tb_Base_Category") +
                " group by FCatCode,FCatName ) q on q.FCatCode = a.FAnalysisCode3  " +
                //--------------------------------------------------------------------------------//

                // " left join Tb_Fun_Vocabulary voc1 on  a.FDataSource = voc1.FPayType and voc1.FPayType = " + dbl.sqlString(YssCons.YSS_CSP_DATASOURCE) +
                // " left join Tb_Fun_Vocabulary voc2 on  a.FStockInd = voc2.FPayType and voc1.FPayType = " + dbl.sqlString(YssCons.YSS_CSP_STOCKIND) +
                " left join (select FIVPayCatCode,FIVPayCatName from Tb_Base_InvestPayCat) r on r.FIVPayCatCode = a.FIVPayCatCode" +
                //story 2254 add by zhouwei 20120224 分级组合查询 start-----------
                " left join (select * from "+pub.yssGetTableName("Tb_TA_PortCls")+" where fcheckstate=1) cp on a.FPortClsCode=cp.FPortClsCode"+
                //story 2254 add by zhouwei 20120224 分级组合查询  end-------------
                this.FilterSql() +
                " left join (select v.FPortCode ,v.FPortName from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u   " +
//                " join (select * from " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("tb_para_portfolio") + " v where FCheckState = 1) h on a.FPortCode = h.FPortCode" +
                buildFilterSql() +
                ") y on x.FNum =y.FNum " +
                "order by y.FCheckState, y.FCreateTime desc";
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("InvestPayRec");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_CSP_DATASOURCE + "," +
                                        YssCons.YSS_CSP_STOCKIND);

            String reStr = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + sShowCols + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            return reStr;
        } catch (Exception ex) {
        	//add by songjie 2011.04.08 BUG 1626 QDV4赢时胜(测试)2011年3月31日02_B
        	throw new YssException("获取运营应收应付数据出错！", ex);
        	//delete by songjie 2011.04.08 BUG 1626 QDV4赢时胜(测试)2011年3月31日02_B
//            System.out.println(ex.toString());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    public String getListViewData2() throws YssException {
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
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {

            sHeader = this.getListView1Headers();
            strSql = "select show.*,gp.FAssetGroupCode,gp.FAssetGroupName from( "; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090520
            strSql = strSql + " select y.* from " +
                "(select FNum from " +
                pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " " +
                " where FCheckState <> 2 ) x join";
            strSql = strSql +
                "(select a.*, cp.fportclsname,b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " h.FPortName as FPortName, s.FTsfTypeName as FTsfTypeName, m.FSubTsfTypeName as FSubTsfTypeName,n.FCuryName as FCuryName,z.FINVMGRNAME as FINVMGRNAME" +
                " ,w.FBrokerName as FBrokerName" + //添加获取券商的信息 sj modified 20081218 MS00108
                //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
                ",q.FCatName as FCatName" +
                //--------------------------------------------------------------------------------//
                ",r.FIVPayCatName as FIVPayCatName,nvl(j.FAttrClsName,' ') as FAttrClsName ";//add by jiangshichao 2010.11.22 NO.125 用户需要对组合按资本类别进行子组合的分类
            strSql = strSql +
                (this.FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from "+pub.yssGetTableName("Tb_Para_AttributeClass")+" ) j on a.FAttrClsCode = j.FAttrClsCode "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) s on a.FTsfTypeCode = s.FTsfTypeCode " +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) m on m.FSubTsfTypeCode = a.FSubTsfTypeCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " ) n on n.FCuryCode = a.FCuryCode " +
                " left join (select FINVMGRCODE,FINVMGRNAME from  " +
                pub.yssGetTableName("tb_para_investmanager") +
                " group by FINVMGRCODE,FINVMGRNAME ) z on z.FINVMGRCODE = a.FAnalysisCode1  " +
                //------添加获取券商的信息 sj modified 20081218  MS00108 ------------------------------------//
                " left join (select FBrokerCode,FBrokerName from  " +
                pub.yssGetTableName("tb_Para_Broker") +
                " group by FBrokerCode,FBrokerName ) w on w.FBrokerCode = a.FAnalysisCode2  " +
                //--------------------------------------------------------------------------------//
                //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
                " left join (select FCatCode,FCatName from  " +
                pub.yssGetTableName("Tb_Base_Category") +
                " group by FCatCode,FCatName ) q on q.FCatCode = a.FAnalysisCode3  " +
                //--------------------------------------------------------------------------------//

                " left join (select FIVPayCatCode,FIVPayCatName from Tb_Base_InvestPayCat) r on r.FIVPayCatCode = a.FIVPayCatCode" +
                this.FilterSql() +
                " left join (select v.FPortCode ,v.FPortName from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u   " +
//                " join (select * from " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("tb_para_portfolio") + " v where FCheckState = 1) h on a.FPortCode = h.FPortCode" +
                //story 2254 add by zhouwei 20120224 分级组合查询 start-----------
                " left join (select * from "+pub.yssGetTableName("Tb_TA_PortCls")+" where fcheckstate=1) cp on a.FPortClsCode=cp.FPortClsCode"+
                //story 2254 add by zhouwei 20120224 分级组合查询  end-------------
                ") y on x.FNum =y.FNum " +
                " where FPortCode in ( " + operSql.sqlCodes(this.filterType.PortCode) +
                //edit by songjie 2012.04.06 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B
                ") and y.FDataOrigin = 0 and FTransDate between " +
                dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +
                " and FTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) +
                "," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_PAYOUT) + //待摊计提后对应业务类型为16，panjunfang add 20090629 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                //"," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + //原有对应的业务类型06注释掉。panjunfang modify 20090709 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                "," + dbl.sqlString(YssOperCons.Yss_ZJDBLX_SUPPLEMENT) + //add by huangqirong 2013-02-02 story #3488 增加补差款类型
                " )and FSubTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_IV_Pay) + "," +
                dbl.sqlString(YssOperCons.Yss_ZJDBLX_SUPPLEMENT_IV) + "," + //add by huangqirong 2013-02-02 story #3488 增加补差款子类型
                //------------------------------------------------------------
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_PRE_PAYOUT) + ")" + //待摊计提后对应子业务类型为16iv，原有对应的06iv则注释掉。panjunfang add 20090629 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                //dbl.sqlString(YssOperCons.YSS_ZJDBZLX_IV_Rec) + ")" + //获取计提费用的数据，由调拨类型来区分，20070808，杨
                //----------------------------------------------------------------------
                (FIVPayCatCode.length() > 0 ? (" and FIVPayCatCode in(" + operSql.sqlCodes(this.filterType.FIVPayCatCode) + ")") : " ") + //再加上应收应付的代码 by leeyu 080814
                " order by y.FCheckState, y.FCreateTime desc";
            //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090521
            strSql = strSql + ") show join TB_SYS_ASSETGROUP gp on gp.fassetgroupcode='" + pub.getPrefixTB() + "'";
            //---------------------------------------------------------------------------------------------------
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
                this.assetGroupCode = rs.getString("FAssetGroupCode"); //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090521
                this.assetGroupName = rs.getString("FAssetGroupName"); //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090521
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

    public String getListViewData4() throws YssException {
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
        buf.append(this.Num.trim()).append("\t");
        buf.append(this.FIVPayCatCode.trim()).append("\t");
        buf.append( (this.FIVPayCatName + "").trim()).append("\t");
        buf.append(YssFun.formatDate(this.TradeDate)).append("\t");
        buf.append(this.PortCode.trim()).append("\t");
        buf.append(this.PortName.trim()).append("\t");
        buf.append(this.AnalysisCode1.trim()).append("\t");
        buf.append(this.AnalysisName1.trim()).append("\t");
        buf.append(this.TsftTypeCode.trim()).append("\t");
        buf.append(this.TsftTypeName.trim()).append("\t");
        buf.append(this.SubTsfTypeCode.trim()).append("\t");
        buf.append(this.SubTsfTypeName.trim()).append("\t");
        // buf.append(this.FDataSource).append("\t");
        //buf.append(this.FStockInd).append("\t");
        buf.append(this.Money).append("\t");
        buf.append(this.BaseCuryRate).append("\t");
        buf.append(this.BaseCuryMoney).append("\t");
        buf.append(this.PortCuryRate).append("\t");
        buf.append(this.PortCuryMoney).append("\t");
        // buf.append(this.TsftTypeCode).append("\t");
        //buf.append(this.SubTsfTypeCode).append("\t");
        buf.append(this.CuryCode).append("\t");
        buf.append(this.FCuryName).append("\t");
        buf.append(this.sdesc).append("\t"); // 2008-4-23 单亮 添加描述属性
        //----增加对分析代码2的获取 sj modified 20081218 MS00108 --//
        buf.append(this.AnalysisCode2).append("\t");
        buf.append(this.AnalysisName2).append("\t");
        //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
        buf.append(this.AnalysisCode3).append("\t");
        buf.append(this.AnalysisName3).append("\t");
        buf.append(this.assetGroupCode).append("\t"); //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090521
        buf.append(this.assetGroupName).append("\t"); //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090521
        //--------------------------------------------------------------------------------//
       //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        buf.append(this.clsPortCode).append("\t");
        buf.append(this.clsPortName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        try {
            if (sType.equalsIgnoreCase("getinvestPaid")) {
                this.Money = operFun.getInvestPaid(this.TradeDate, this.PortCode,
                    this.AnalysisCode1.length() > 0 ?
                    this.AnalysisCode1 : " ",
                    this.AnalysisCode2.length() > 0 ?
                    this.AnalysisCode2 : " ",
                    this.AnalysisCode3.length() > 0 ?
                    this.AnalysisCode3 : " ",
                    this.FIVPayCatCode);
                this.Money = YssD.round(Money, 2);
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return buildRowStr();

    }

    public void parseRowStr(String sRowStr) throws YssException {
        /* buf.Append(this.m_sNum.Trim()).Append("\t");
         buf.Append(this.m_sTradeDate.Trim()).Append("\t");
         buf.Append(this.m_sPortCode.Trim()).Append("\t");
         buf.Append(this.m_sAnalysisCode1.Trim()).Append("\t");
         buf.Append(this.m_sAnalysisCode2.Trim()).Append("\t");
         buf.Append(this.m_sAnalysisCode3.Trim()).Append("\t");
         //buf.Append(ClsUtil.formatNumber(ClsConstant.stringToNumber(),this.m_sFDataSource))
         buf.Append(this.m_sFDataSource).Append("\t");
         buf.Append(this.m_sFStockInd).Append("\t");
         buf.Append(ClsUtil.formatNumber(ClsConstant.stringToNumber,this.m_sMoney.Trim())).Append("\t");
         buf.Append(ClsUtil.formatNumber(ClsConstant.stringToNumber,this.m_sBaseCuryRate.Trim())).Append("\t");
         buf.Append(ClsUtil.formatNumber(ClsConstant.stringToNumber,this.m_sBaseCuryMoney.Trim())).Append("\t");
         buf.Append(ClsUtil.formatNumber(ClsConstant.stringToNumber,this.m_sPortCuryRate.Trim())).Append("\t");
         buf.Append(ClsUtil.formatNumber(ClsConstant.stringToNumber,this.m_sPortCuryMoney.Trim())).Append("\t");
         buf.Append(this.CheckStateId).Append("\tnull");*/
        try {
            String[] reqAry = null;
            String sTmpStr = "";
            if (sRowStr.length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.Num = reqAry[0];

            if (YssFun.isDate(reqAry[1])) {
                this.TradeDate = YssFun.toDate(reqAry[1]);

            }

            this.PortCode = reqAry[2];
            this.AnalysisCode1 = reqAry[3];
            this.AnalysisName1 = reqAry[4];
            this.TsftTypeCode = reqAry[5];
            this.TsftTypeName = reqAry[6];
            this.SubTsfTypeCode = reqAry[7];
            this.SubTsfTypeName = reqAry[8];

            // this.FDataSource = YssFun.toInt( reqAry[9]);
            //this.FStockInd = YssFun.toInt( reqAry[10]);
            if (reqAry[9].length() != 0) {
                this.Money = YssFun.toDouble(reqAry[9]);
            }
            if (reqAry[10].length() != 0) {
                this.BaseCuryRate = YssFun.toDouble(
                    reqAry[10]);
            }
            if (reqAry[11].length() != 0) {
                this.BaseCuryMoney = YssFun.toDouble(
                    reqAry[11]);
            }
            if (reqAry[12].length() != 0) {
                this.PortCuryRate = YssFun.toDouble(
                    reqAry[12]);
            }
            if (reqAry[13].length() != 0) {
                this.PortCuryMoney = YssFun.toDouble(
                    reqAry[13]);
            }
            super.checkStateId = YssFun.toInt(reqAry[14]);
            if (reqAry[15].length() != 0) {
                this.strIsOnlyColumns = reqAry[15];
            }
            //this.TsftTypeCode = reqAry[18];
            //this.SubTsfTypeCode = reqAry[16];
            this.CuryCode = reqAry[16];
            this.FCuryName = reqAry[17];
            if (!reqAry[18].equalsIgnoreCase("")) {
                this.startDate = YssFun.toDate(reqAry[18]);
            }
            if (!reqAry[19].equalsIgnoreCase("")) {
                this.endDate = YssFun.toDate(reqAry[19]);
            }
            this.FIVPayCatCode = reqAry[20];
            this.sdesc = reqAry[21];
            //----- 增加分析代码2的设置 sj modified 20081218 MS00108 --//
            this.AnalysisCode2 = reqAry[22];
            //----------------------------------------------//
            //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
            this.AnalysisCode3 = reqAry[23]; //之后所有的index向后递增一位
            //MS00205 add by 宋洁 2009-01-23
            if (reqAry.length >= 26) {
                if (YssFun.isDate(reqAry[24])) {
                    this.BeginTradeDate = YssFun.toDate(reqAry[24]); //获取业务开始日期
                }
                if (YssFun.isDate(reqAry[25])) {
                    this.EndTradeDate = YssFun.toDate(reqAry[25]); //获取业务结束日期
                }
            }
            //MS00205 add by 宋洁 2009-01-23
            //-------------------------------------------------------------------------------//
            // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090520
            this.assetGroupCode = reqAry[26];
            this.assetGroupName = reqAry[27];
            
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            this.strAttrClsCode = reqAry[28];
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            //---------------------------------------------------------------------------------------------
            this.clsPortCode=reqAry[29];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (filterType == null) {
                    this.filterType = new InvestPayRecBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析运营收支款出错!");
        }

    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    //public void setYssPub(YssPub pub);

    /**
     * checkInput : 验证要保存的设置信息
     * @param btOper byte ： 操作类型，见YssCons中的操作类型
     */
    public void checkInput(byte btOper) throws YssException {

    }

    /**
     * saveSetting ：
     * 新增，修改，删除，审核设置信息
     * @param btOper byte ： 操作类型，见YssCons中的操作类型
     */
//   public void saveData(byte btOper) throws YssException;

    /**
     * addOperData：
     * 增加一条设置信息，先通过parseRowStr解析发送过来的请求，再通过类的属性增加到数据库中
     * @return String： 因为有些属性的值需要在后台进行计算，所以可能和发送过来的请求不一致，故这条信息返回给客户端。
     */
    public String addSetting() throws YssException {
        String strSql = "";
        String strNumberDate = "";
        String[] tmpDate = null;
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        //---add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        InvestPayAdimin investPayAdmin = new InvestPayAdimin();
        investPayAdmin.setYssPub(pub);
        //---add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {

            //       for(int i=0;i<tmpDate.length;i++)
            //     {
            //        strNumberDate=strNumberDate+tmpDate[i];
            //  }
            strNumberDate = YssFun.formatDate(this.TradeDate,
                                              YssCons.YSS_DATETIMEFORMAT).
                substring(0, 8);
            //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//            this.Num = strNumberDate +
//                dbFun.getNextInnerCode(pub.yssGetTableName(
//                    "Tb_Data_InvestPayRec"),
//                                       dbl.sqlRight("FNum", 9), "000000001",
//                                       " where FNum like 'IPR"
//                                       + strNumberDate + "%'", 1);
//            this.Num = "IPR" + this.Num;
            //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            //add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
            this.Num = investPayAdmin.getNum();
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                "(FNum,FIVPayCatCode,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FATTRCLSCODE," +//添加所属类别字段 FATTRCLSCODE add by jiangshichao 2010.11.22 NO.125 用户需要对组合按资本类别进行子组合的分类
                "FCuryCode,FTsfTypeCode,FSubTsfTypeCode,FDataSource,FStockInd,FMoney,FBaseCuryRate," +
                "FBaseCuryMoney,FPortCuryRate,FPortCuryMoney,fportclscode,FCheckState," +
                //edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin
                "FCreator,FCreateTime,FCheckUser,FDesc,FDataOrigin)" +
                "values(" + dbl.sqlString(this.Num) + "," +
                dbl.sqlString(this.FIVPayCatCode) + "," +
                dbl.sqlDate(this.TradeDate) + "," +
                dbl.sqlString(this.PortCode) + "," +
                dbl.sqlString(this.AnalysisCode1.length() == 0 ? " " :
                              this.AnalysisCode1) + "," +
                dbl.sqlString(this.AnalysisCode2.length() == 0 ? " " :
                              this.AnalysisCode2) + "," +
                dbl.sqlString(this.AnalysisCode3.length() == 0 ? " " :
                              this.AnalysisCode3) + "," +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
               (this.strAttrClsCode.trim().length() == 0 ?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode))+","+           
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//             

                dbl.sqlString(this.CuryCode) + "," +
                dbl.sqlString(this.TsftTypeCode) + "," +
                dbl.sqlString(this.SubTsfTypeCode) + "," +
                1 + "," +
//                0 + "," +//edit by xuxuming,2010.01.19.这里不应该写死，现在要写入其它类型的入账标识 MS00917.怕影响以前的，只好改为下面的方式
                (this.FStockInd!=-1?0:this.FStockInd)+","+//add by xxm,2010.01.19.可以写入其它类型的入账标识
                //dbl.sqlString(this.cashAccCode) + "," +
                //dbl.sqlString(this.tsfTypeCode) + "," +
                // dbl.sqlString(this.subTsfTypeCode) + "," +
                // dbl.sqlString(this.curyCode) + "," +
                this.Money + "," +
                this.BaseCuryRate + "," +
                this.BaseCuryMoney + "," +
                this.PortCuryRate + "," +
                this.PortCuryMoney + "," +
                dbl.sqlString(this.clsPortCode)+","+
                // 1 + "," +
                //0 + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," + dbl.sqlString(this.sdesc) +
                ",1)";//edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B  FDataOrigin = 1 表示为手工录入的数据
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YssException("保存运营收支款出错！");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

        return "";

    }

    /**
     * editSetting：
     * 修改一条设置信息，先通过parseRowStr解析发送过来的请求，再通过类的属性修改到数据库中
     * @return String： 因为有些属性的值需要在后台进行计算，所以可能和发送过来的请求不一致，故这条信息返回给客户端。
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        String nowNum = "";
        //--- delete by songjie 2013.03.19 BUG 7338 QDV4建信基金2013年03月19日01_B start---//
//        String oldDateStr = "";
//        String nowNumDate = "";
        //--- delete by songjie 2013.03.19 BUG 7338 QDV4建信基金2013年03月19日01_B end---//
        try {
        	//--- delete by songjie 2013.03.19 BUG 7338 QDV4建信基金2013年03月19日01_B start---//
//            if (Num.trim().length() > 0) { ////bug 0000442
//                oldDateStr = this.Num.substring(3, 11);
//                oldDateStr = oldDateStr.substring(0, 4) + "-" +
//                    oldDateStr.substring(4, 6) +
//                    "-" + oldDateStr.substring(6, oldDateStr.length()); //格式化日期值。用以获取Date
//            }
//            if (YssFun.dateDiff(YssFun.toDate(oldDateStr), this.TradeDate) != 0) {
//                nowNumDate = YssFun.formatDate(this.TradeDate, "yyyyMMdd").
//                    substring(0, 8);
//                nowNum = nowNumDate +
//                    dbFun.getNextInnerCode(pub.yssGetTableName(
//                        "Tb_Data_InvestPayRec"),
//                                           dbl.sqlRight("FNum", 9),
//                                           "000000001",
//                                           " where Ftransdate =" +
//                                           dbl.sqlDate(this.TradeDate)
//                    );
//                nowNum = "IPR" + nowNum;
//            }
        	//--- delete by songjie 2013.03.19 BUG 7338 QDV4建信基金2013年03月19日01_B end---//
            
        	//add by songjie 2013.03.19 BUG 7338 QDV4建信基金2013年03月19日01_B
            nowNum = Num;
            
            //=====BUG:000499
            strSql = "update " + pub.yssGetTableName("Tb_Data_InvestPayRec") + " " +
                "set FTransDate=" + dbl.sqlDate(this.TradeDate) +
                ",FIVPayCatCode=" + dbl.sqlString(this.FIVPayCatCode) +
                ",FPortCode=" + dbl.sqlString(this.PortCode) +
                ",FAnalysisCode1=" +
                dbl.sqlString(this.AnalysisCode1.length() == 0 ? " " :
                              this.AnalysisCode1) +
                //-----恢复对分析代码2的修改 sj modified 20081218  MS00108 --------//
                ",FAnalysisCode2=" +
                dbl.sqlString(this.AnalysisCode2.length() == 0 ? " " :
                              this.AnalysisCode2) +
                //-----------------------------------------------------//
                //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
                ",FAnalysisCode3=" +
                dbl.sqlString(this.AnalysisCode3.length() == 0 ? " " :
                              this.AnalysisCode3) +
                //--------------------------------------------------------------------------------//
                //",FCashAccCode=" + dbl.sqlString(this.cashAccCode) +
                //",FTsfTypeCode=" + dbl.sqlString(this.tsfTypeCode) +
                //",FSubTsfTypeCode=" + dbl.sqlString(this.subTsfTypeCode) +
                // ",FCuryCode=" + dbl.sqlString(this.curyCode) +
                //",FDataSource=" + this.FDataSource +
                //",FStockInd=" + this.FStockInd +
                              
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
               (this.strAttrClsCode.trim().length()!=0?",FATTRCLSCODE ="+dbl.sqlString(this.strAttrClsCode):"")+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//              
                ",FMoney=" + this.Money +
                ",FBaseCuryRate=" + this.BaseCuryRate +
                ",FBaseCuryMoney=" + this.BaseCuryMoney +
                ",FPortCuryRate=" + this.PortCuryRate +
                ",FPortCuryMoney=" + this.PortCuryMoney +
                //",FDataSource=" + 1 +
                //     ",FStockInd="+0+
                ",FCuryCode =" + dbl.sqlString(this.CuryCode) +
                ",FTsfTypeCode =" + dbl.sqlString(this.TsftTypeCode) +
                ",FSubTsfTypeCode =" + dbl.sqlString(this.SubTsfTypeCode) +
                ",fportclscode="+dbl.sqlString(this.clsPortCode)+
                ",FCREATOR = " + dbl.sqlString(this.creatorCode) +
                ",FCREATETIME = " + dbl.sqlString(this.creatorTime) +
                ",FDesc = " + dbl.sqlString(this.sdesc) +
                (nowNum.length() > 0 ? ",FNum = " + dbl.sqlString(nowNum) : "") + //====BUG:000499
                //add by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 
                ",FDataOrigin = 1 " + //FDataOrigin = 1表示为手工录入
                " where FNum = " + dbl.sqlString(this.Num);
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YssException("修改运营收支款出错！");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";

    }

    /**
     * delSetting : 删除一条设置信息，即将信息放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_InvestPayRec") + " " +
                "set FCheckState=" + super.checkStateId + ",FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum = " + dbl.sqlString(this.Num);
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YssException("删除运营收支款出错！");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

    }

    /**
     * 修改时间：2008年3月28号
     * 修改人：单亮
     * 原方法功能：只能处运营收支款的审核和未审核的单条信息。
     * 新方法功能：可以处理运营收支款审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理运营收支款审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//      Connection con = dbl.loadConnection();
//      boolean bTrans = false;
//      String strSql = "";
//      try {
//         strSql = "update " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = " +
//               dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" +
//               YssFun.formatDatetime(new java.util.Date()) + "'" +
//               " where FNum = " +
//               dbl.sqlString(this.Num);
//
//         con.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         con.commit();
//         bTrans = false;
//         con.setAutoCommit(true);
//
//      }
//      catch (Exception e) {
//         e.printStackTrace();
//         throw new YssException("审核运营收支款出错！");
//      }
//      finally {
//         dbl.endTransFinal(con, bTrans);
//      }
        //修改后的代码
        //--------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled!=null&&(!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum = " +
                        dbl.sqlString(this.Num);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而Num不为空，则按照Num来执行sql语句
            else if (Num!=null&&(!Num.equalsIgnoreCase(""))) {
                strSql = "update " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FNum = " +
                    dbl.sqlString(this.Num);
                dbl.executeSql(strSql);

            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            e.printStackTrace();
            throw new YssException("审核运营收支款出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
//---------------end
    }

    /**
     * saveMutliSetting ：
     * 多条设置信息同时保存
     * @param sMutilRowStr String ： 发送过来的多行请求
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return null;
    }

    /**
     * getSetting ：
     * 获取一条设置信息
     * @return ParaSetting
     */
    public IDataSetting getSetting() throws YssException {
    	ResultSet rs = null;
        String strSql = "";
        try {
           strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                 " a "
                 + buildFilterSql();
           rs = dbl.openResultSet(strSql);
           while (rs.next()) {
              this.Num = rs.getString("FNum") + "";
           }
           return this;
        }
        catch (Exception e) {
           throw new YssException("获取运营应收应付表信息出错！", e);
        }
        finally {
           dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getAllSetting ：
     * 获取所有的设置信息
     * @return String
     */
    public String getAllSetting() throws YssException {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.strIsOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.filterType.Num.length() != 0) {
                //--MS00007 add by songjie 2009-03-13
                if (filterType.Num.indexOf(",") > 0) {
                    sResult = sResult + " and a.FNum in(" +
                        operSql.sqlCodes(filterType.Num) + " )"; //若编号中有逗号，则用in()的格式进行查询
                } else {
                    //--MS00007 add by songjie 2009-03-13

                    sResult = sResult + " and a.FNum like '" +
                        filterType.Num.replaceAll("'", "''") + "%'";
                }
            }
            //MS00205 QDV4赢时胜上海2009年01月16日02_A edit by 宋洁 2009-01-23 由于现在要根据日期段判断，所以sql语句判断条件要改变
            /*if (YssFun.formatDate(filterType.TradeDate).length() != 0 &&
                !YssFun.formatDate(filterType.TradeDate).equals("9998-12-31")) {
               sResult = sResult + " and a.FTransDate>= " +
                     dbl.sqlDate(filterType.TradeDate) + "and a.FTransDate<= " + dbl.sqlDate(filterType.TradeDate);
                      }*/
            //MS00205 QDV4赢时胜上海2009年01月16日02_A edit by 宋洁 2009-01-23 由于现在要根据日期段判断，所以sql语句判断条件要改变
            //----------MS00205 QDV4赢时胜上海2009年01月16日02_A edit by 宋洁 2009-01-23 由于现在要根据日期段判断，所以sql语句判断条件改变
            if (filterType.BeginTradeDate != null && YssFun.formatDate(filterType.BeginTradeDate).length() != 0 && //判断查询开始的业务日期是否符合标准
                !YssFun.formatDate(filterType.BeginTradeDate).equals("9998-12-31") &&
                filterType.EndTradeDate != null && YssFun.formatDate(filterType.EndTradeDate).length() != 0 && //判断查询结束的业务日期是否符合标准
                !YssFun.formatDate(filterType.EndTradeDate).equals("9998-12-31")) {
                //将业务日期的查询条件由精确查询格式“=”变为模糊查询格式“where z between x and y”
                sResult = sResult + " and a.FTransDate between " +
                    dbl.sqlDate(filterType.BeginTradeDate) + " and " + dbl.sqlDate(filterType.EndTradeDate);
            }
            //---------MS00205 QDV4赢时胜上海2009年01月16日02_A edit by 宋洁 2009-01-23 由于现在要根据日期段判断，所以sql语句判断条件改变
            //----add by songjie 2011.04.08 BUG 1626 QDV4赢时胜(测试)2011年3月31日02_B----//
            else if(filterType.TradeDate != null && YssFun.formatDate(filterType.TradeDate).length() != 0 &&
            		!YssFun.formatDate(filterType.TradeDate).equals("9998-12-31")){
            	sResult = sResult + " and a.FTransDate = " + dbl.sqlDate(filterType.TradeDate);
            }
            //----add by songjie 2011.04.08 BUG 1626 QDV4赢时胜(测试)2011年3月31日02_B----//
            if (this.filterType.FIVPayCatCode.length() != 0) {
                if (filterType.FIVPayCatCode.split(",").length > 0) {
                    sResult = sResult + " and a.FIVPayCatCode in ( " +
                        operSql.sqlCodes(filterType.FIVPayCatCode) + ")";
                } else {
                    sResult = sResult + " and a.FIVPayCatCode = " +
                        dbl.sqlString(filterType.FIVPayCatCode);
                }
            }
            /*  if (this.filterType.FIVPayCatCode.length() != 0) {
                 sResult = sResult + " and FAnalysisCode1 like '" +
                       filterType.investManagerCode.replaceAll("'", "''") + "%'";
              }*/
            //----add by wuweiqi 20101213 增加业务类型和业务子类型查询条件  QDV4赢时胜(测试)2010年11月3日01_AB---------------------------------//
            if (this.filterType.TsftTypeCode.length() != 0) {
                if (filterType.TsftTypeCode.split(",").length > 0) {
                    sResult = sResult + " and a.FTSFTYPECODE in (" +
                        operSql.sqlCodes(filterType.TsftTypeCode) + ")";
                } else {
                    sResult = sResult + " and a.FTSFTYPECODE like '" +
                        filterType.TsftTypeCode.replaceAll("'", "''") + "%'";
                }
            }
            if (this.filterType.SubTsfTypeCode.length() != 0) {
                if (filterType.SubTsfTypeCode.split(",").length > 0) {
                    sResult = sResult + " and a.FSUBTSFTYPECODE in (" +
                        operSql.sqlCodes(filterType.SubTsfTypeCode) + ")";
                } else {
                    sResult = sResult + " and a.FSUBTSFTYPECODE like '" +
                        filterType.SubTsfTypeCode.replaceAll("'", "''") + "%'";
                }
            }
            //-----------------------end by wuweiqi ---------------------------------------------------------//
//
//            if (this.filterType.TsftTypeCode.length() != 0) {
//                sResult = sResult + " and a.FTsfTypeCode like '" +
//                    filterType.TsftTypeCode.replaceAll("'", "''") + "%'";
//            }
//            if (this.filterType.SubTsfTypeCode.length() != 0) {
//                sResult = sResult + " and a.FSubTsfTypeCode like '" +
//                    filterType.SubTsfTypeCode.replaceAll("'", "''") + "%'";
//            }
            //---------------------------------------------------------------------
            //MS00229 QDV4中保2009年02月05日01_ B 2009.02.10方浩
            //运营收支应收应付中的筛选功能，增加基金经理的筛选功能。
            if (this.filterType.AnalysisCode1.length() != 0) { //判断基金经理可从前台传来内容
                sResult = sResult + " and a.FAnalysisCode1 like '" +
                    filterType.AnalysisCode1.replaceAll("'", "''") + "%'"; //在SQL语句里加入基金经理筛选条件。
            }
            //---------------------------------------------------------------------
            if (this.filterType.PortCode.length() != 0) {
                if (filterType.PortCode.split(",").length > 0) {
                    sResult = sResult + " and a.FPortCode in (" +
                        operSql.sqlCodes(filterType.PortCode) + ")";
                } else {
                    sResult = sResult + " and a.FPortCode like '" +
                        filterType.PortCode.replaceAll("'", "''") + "%'";
                }
            }
            /*if (this.filterType.length() != 0) {
               sResult = sResult + " and FCashAccCode like '" +
                     filterType.cashAccCode.replaceAll("'", "''") + "%'";
                      }*/
         
            if (this.filterType.CuryCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.CuryCode.replaceAll("'", "''") + "%'";
            }
            /*if (this.filterType.tsfTypeCode.length() != 0) {
               sResult = sResult + " and FTsfTypeCode like '" +
                     filterType.tsfTypeCode.replaceAll("'", "''") + "%'";
                      }
                      if (this.filterType.subTsfTypeCode.length() != 0) {
               sResult = sResult + " and FSubTsfTypeCode like '" +
                     filterType.subTsfTypeCode.replaceAll("'", "''") + "%'";
                      }*/
      
            //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
            if (this.filterType.AnalysisCode3.length() != 0) { //判断品种类型内容
                sResult = sResult + " and a.FAnalysisCode3 like '" +
                    filterType.AnalysisCode3.replaceAll("'", "''") + "%'"; //在SQL语句里加入品种类型筛选条件。
            }
            //--------------------------------------------------------------------------------//

            //=======add by xxm,2010.01.20===============MS00917======
            if(this.filterType.FStockInd!=0){//默认值 为0.0表示未入账，1表示入账。这两种情形都不会以此标识来查询。9表示非估值时自动产生的估增数据，‘－1’表示尾差调整数据，此时需要根据此标识来查询
            	sResult += " and a.FStockInd ="+this.filterType.FStockInd;            	
            }
            //===============end=================================
            
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            if (this.filterType.strAttrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.strAttrClsCode.replaceAll("'", "''") + "%'";
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        }
        return sResult;

    }

    private String FilterSql() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;

        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_InvestPayRec);
        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("004")) {
                    sResult = sResult +
                        " left join (select uu.FCatCode ,uu.FCatName  as FAnalysisName" +
                        i +
                        " from  (select FCatCode  from " +
                        " Tb_Base_Category " +
                        " where FCheckState = 1 group by FCatCode )kk " +
                        " join (select * from " +
                        " Tb_Base_Category ) uu on kk.FCatCode = uu.FCatCode ) category on a.FAnalysisCode" +
                        i + " = category.FCatCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                    i +
                    "  from  " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where  n.FCheckState = 1 ) invmgr on a.FAnalysisCode" +
                    i + " = invmgr.FInvMgrCode ";
                
                    
                    //end by lidaolong
                } else {
                    sResult = sResult +
                        " left join (select ' ' as FAnalysisNull , ' ' as FAnalysisName" +//调整为有空格的字段，防止在创建分页表时报错 by leeyu 20100813 合并太平版本时调整
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                        i + ".FAnalysisNull ";
                }
            }
        }
        dbl.closeResultSetFinal(rs);//QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-07-09 关闭结果集
        return sResult;
    }

    public void setResultSetAttr(ResultSet rs) throws YssException {
        try {
            this.Num = rs.getString("FNum") + "";
            this.TradeDate = rs.getDate("FTransDate");
            this.PortCode = rs.getString("FPortCode") + "";
            this.PortName = rs.getString("FportName") + "";
            this.AnalysisCode1 = rs.getString("FAnalysisCode1") + "";
            this.AnalysisName1 = rs.getString("FINVMGRNAME") + "";
            // this.AnalysisCode2= rs.getString("FAnalysisCode2") + "";
            // this.AnalysisName2 = rs.getString("FAnalysisName2") + "";
            // this.AnalysisCode3 = rs.getString("FAnalysisCode3") + "";
            // this.AnalysisName3 = rs.getString("FAnalysisName3") + "";
            // this.cashAccCode = rs.getString("FCashAccCode") + "";
            // this.cashAccName = rs.getString("FCashAccName") + "";
            //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码2、3的处理 sj modified ------------//
            this.AnalysisCode2 = rs.getString("FAnalysisCode2") + "";
            this.AnalysisName2 = rs.getString("FBrokerName") + "";
            this.AnalysisCode3 = rs.getString("FAnalysisCode3") + "";
            this.AnalysisName3 = rs.getString("FCatName") + "";
            //--------------------------------------------------------------------------------//
            this.TsftTypeCode = rs.getString("FTsfTypeCode") + "";
            this.TsftTypeName = rs.getString("FTsfTypeName") + "";
            this.SubTsfTypeCode = rs.getString("FSubTsfTypeCode") + "";
            this.SubTsfTypeName = rs.getString("FSubTsfTypeName") + "";
            this.CuryCode = rs.getString("FCuryCode") + "";
            this.FCuryName = rs.getString("FCuryName") + "";
            this.Money = rs.getDouble("FMoney");
            this.BaseCuryRate = rs.getDouble("FBaseCuryRate");
            this.BaseCuryMoney = rs.getDouble("FBaseCuryMoney");
            this.PortCuryRate = rs.getDouble("FPortCuryRate");
            this.PortCuryMoney = rs.getDouble("FPortCuryMoney");
            //this.FDataSource = rs.getInt("FDataSource");
            //this.FStockInd = rs.getInt("FStockInd");
            this.FIVPayCatCode = rs.getString("FIVPayCatCode");
            this.FIVPayCatName = rs.getString("FIVPayCatName");
            this.sdesc = rs.getString("FDesc"); // 2008-4-23  单亮  添加描述属性
            //-----添加获取券商的信息 sj modified 20081218  MS00108 --------------------//
            this.AnalysisCode2 = rs.getString("FAnalysisCode2");
            this.AnalysisName2 = rs.getString("FBrokerName");
            //---------------------------------------------------------------
            //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
            this.AnalysisCode3 = rs.getString("FAnalysisCode3");
            this.AnalysisName3 = rs.getString("FCatName");
            //--------------------------------------------------------------------------------//
            
          //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            if(dbl.isFieldExist(rs, "FATTRCLSCODE")){
            	this.strAttrClsCode = rs.getString("FATTRCLSCODE");
            	this.strAttrClsName = rs.getString("FATTRCLSName");
            }else{
            	this.strAttrClsCode = "";
            	this.strAttrClsName = "";
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            
            if(dbl.isFieldExist(rs, "fportclscode")){
            	this.clsPortCode = rs.getString("fportclscode");
            }
            if(dbl.isFieldExist(rs, "fportclsname")){
            	this.clsPortName = rs.getString("fportclsname");            }
            super.setRecLog(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除回收站的数据，即从数据库彻底删除数据
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
                        pub.yssGetTableName("Tb_Data_InvestPayRec") +
                        " where FNum = " + dbl.sqlString(this.Num);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而Num不为空，则按照Num来执行sql语句
            else if (Num != "" && Num != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_InvestPayRec") +
                    " where FNum = " + dbl.sqlString(this.Num);
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
     * 修改人时间:20090522
     * MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * @return String
     * @throws YssException
     */
    public String getListViewGroupData3() throws YssException {
        String sGroups = "";                    //定义一个变量用于保存处理后的结果数据
        String sPrefixTB = pub.getPrefixTB();   //保存当前表前缀
        String[] assetGroupCodes = this.filterType.assetGroupCode.split(YssCons.YSS_GROUPSPLITMARK);    //按组合群的解析符解析组合群代码
        String[] strPortCodes = this.filterType.PortCode.split(YssCons.YSS_GROUPSPLITMARK);             //按组合群的解析符解析组合代码
        String[] strFIVPayCatCode = this.filterType.FIVPayCatCode.split(YssCons.YSS_GROUPSPLITMARK);    //按组合群的解析符解析运营投资品种代码
        try {
            for (int i = 0; i < assetGroupCodes.length; i++) {  //遍历所有组合群
                this.assetGroupCode = assetGroupCodes[i];       //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode);           //将该组合群代码设置为表前缀
                this.filterType.PortCode = strPortCodes[i];     //获取当前组合群下的所有组合
                this.filterType.FIVPayCatCode = strFIVPayCatCode[i];    //获取当前组合群下的所有投资运营品种代码
                String sGroup = this.getListViewData3();        //调用处理方法
                sGroups = sGroups + sGroup + YssCons.YSS_GROUPSPLITMARK;//各组合群的处理结果用组合群解析符隔开
            }
            if (sGroups.length() > 7) {
                sGroups = sGroups.substring(0, sGroups.length() - 7);   //去除尾部多出的组合群解析符
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            pub.setPrefixTB(sPrefixTB);     //设回表前缀
        }
        return sGroups;     //返回结果到前台
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }
}
