package com.yss.main.storagemanage;

import java.sql.*;

import com.yss.main.dao.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

public class InvestBean
    extends com.yss.dsub.BaseDataSettingBean implements
    com.yss.main.dao.IDataSetting {
    private String strIvPayCatCode = ""; //运营收支品种代码
    private String strIvPayCatName = ""; //运营收支品种名称
    private String strStorageDate = ""; //库存日期
    private String strStorageStartDate = ""; //库存初始日期
    private String strYearMonth = ""; //库存年月
    private String strBal = ""; //余额
    private String strCuryCode = ""; //交易货币代码
    private String strCuryName = ""; //交易货币名称
    private String strPortCode = ""; //组合代码
    private String strPortName = ""; //组合名称
    private String strPortCuryRate = ""; //原币汇率
    private String strBaseCuryRate = ""; //基础汇率

    private int intStorageState = 2; //库存状态：0－自动计算（未锁定），1－自动计算（锁定），2－初始化
    private String strFAnalysisCode1 = ""; //辅助代码1
    private String strFAnalysisName1 = ""; //辅助名称1
    private String strFAnalysisCode2 = ""; //辅助代码2
    private String strFAnalysisName2 = ""; //辅助名称2
    private String strFAnalysisCode3 = ""; //辅助代码3
    private String strFAnalysisName3 = ""; //辅助名称3

    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    private String strAttrClsCode = "";//所属分类
    private String strAttrClsName = "";
    private String strOldAttrClsCode = "";
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    
    private String strBaseCuryBal = ""; //基础货币余额(新增)
    private String strPortCuryBal = ""; //组合货币余额(新增)
    private String strOldIvPayCatCode = "";
    private String strOldPortCode = "";
    private String strOldStorageDate = "";
    private String strOldFAnalysisCode1 = "";
    private String strOldFAnalysisCode2 = "";
    private String strOldFAnalysisCode3 = "";
    private String strOldYearMonth = "";
    private String strIsOnlyColumn = "0";
    //---#2279::农行计提管理费需求  add by wuweiqi 20110126-------------//
    private String strOldCuryCode="";
    // ---add by wuweiqi 20110126 -----------------------------------//

	private InvestBean filterType;
    private String sInvestRecPayBal = "";
    private String sRecycled = "";

    private String flag = "";

    private String bBegin; //是否取期初数

    private String sInvestPayBal = "";

    public InvestBean() {}

    public int getIntStorageState() {
        return this.intStorageState;
    }

    public String getBBegin() {
        return bBegin;
    }

    public void setBBegin(String bBegin) {
        this.bBegin = bBegin;
    }

    //添加YearMonth的get、set方法 MS00435 QDV4赢时胜（上海）2009年5月7日01_B libo
    public String getStrYearMonth() {
        return strYearMonth;
    }

    public void setStrYearMonth(String strYearMonth) {
        this.strYearMonth = strYearMonth;
    }

    public void setIntStorageState(int intStorageState) {
        this.intStorageState = intStorageState;
    }

    public String getStrAccBalance() {
        return this.strBal;
    }

    public void setStrAccBalance(String strBal) {
        this.strBal = strBal;
    }

    public String getStrBaseCuryRate() {
        return this.strBaseCuryRate;
    }

    public void setStrBaseCuryRate(String strBaseCuryRate) {
        this.strBaseCuryRate = strBaseCuryRate;
    }

    public String getStrIvPayCatCode() {
        return this.strIvPayCatCode;
    }

    public void setStrIvPayCatCode(String strCashAccCode) {
        this.strIvPayCatCode = strCashAccCode;
    }

    public String getStrIvPayCatName() {
        return this.strIvPayCatName;
    }

    public void setStrIvPayCatName(String strCashAccName) {
        this.strIvPayCatName = strCashAccName;
    }

    public String getStrCuryCode() {
        return this.strCuryCode;
    }

    public void setStrCuryCode(String strCuryCode) {
        this.strCuryCode = strCuryCode;
    }

    public String getStrCuryName() {
        return this.strCuryName;
    }

    public void setStrCuryName(String strCuryName) {
        this.strCuryName = strCuryName;
    }

    public String getStrFAnalysisCode1() {
        return this.strFAnalysisCode1;
    }

    public void setStrFAnalysisCode1(String strFAnalysisCode1) {
        this.strFAnalysisCode1 = strFAnalysisCode1;
    }

    public String getStrFAnalysisCode2() {
        return this.strFAnalysisCode2;
    }

    public void setStrFAnalysisCode2(String strFAnalysisCode2) {
        this.strFAnalysisCode2 = strFAnalysisCode2;
    }

    public String getStrFAnalysisCode3() {
        return this.strFAnalysisCode3;
    }

    public void setStrFAnalysisCode3(String strFAnalysisCode3) {
        this.strFAnalysisCode3 = strFAnalysisCode3;
    }

    public String getStrFAnalysisName1() {
        return this.strFAnalysisName1;
    }

    public void setStrFAnalysisName1(String strFAnalysisName1) {
        this.strFAnalysisName1 = strFAnalysisName1;
    }

    public String getStrFAnalysisName2() {
        return this.strFAnalysisName2;
    }

    public void setStrFAnalysisName2(String strFAnalysisName2) {
        this.strFAnalysisName2 = strFAnalysisName2;
    }

    public String getStrFAnalysisName3() {
        return this.strFAnalysisName3;
    }

    public void setStrFAnalysisName3(String strFAnalysisName3) {
        this.strFAnalysisName3 = strFAnalysisName3;
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
    
    
    public String getStrBaseCuryBal() {
        return this.strBaseCuryBal;
    }

    public void setStrBaseCuryBal(String strBaseCuryBal) {
        this.strBaseCuryBal = strBaseCuryBal;
    }

    public String getStrPortCuryBal() {
        return this.strPortCuryBal;
    }

    public void setStrPortCuryBal(String strPortCuryBal) {
        this.strPortCuryBal = strPortCuryBal;
    }

    public String getStrPortCode() {
        return this.strPortCode;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public String getStrPortCuryRate() {
        return this.strPortCuryRate;
    }

    public void setStrPortCuryRate(String strPortCuryRate) {
        this.strPortCuryRate = strPortCuryRate;
    }

    public String getStrPortName() {
        return this.strPortName;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public String getStrStorageDate() {
        return this.strStorageDate;
    }

    public String getStrOldYearMonth() {
        return strOldYearMonth;
    }

    public void setStrStorageDate(String strStorageDate) {
        this.strStorageDate = strStorageDate;
    }

    //------------edited by zhouxiang 有分析代码的运营收支库存内的应收应付报错  MS01350 ------------
    public void setStrStorageStartDate(String strStorageStartDate)
    {
    	this.strStorageStartDate=strStorageStartDate;
    }
    public String getStrStorageStartDate()
    {
    	return strStorageStartDate;
    }
    //--------------end-------------------------------------------------------------------------
    public void setStrOldYearMonth(String strOldYearMonth) {
        this.strOldYearMonth = strOldYearMonth;
    }
    //------#2279::用户支持多币种  add by wuweiqi 20110126-----------------------------//
    public String getStrOldCuryCode() {
		return strOldCuryCode;
	}

	public void setStrOldCuryCode(String strOldCuryCode) {
		this.strOldCuryCode = strOldCuryCode;
	}
    //----------------------end by wuweiqi 20110126 ------------------------------------//
    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sDateStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName,d.FStorageInitDate, " +
            		 "g.FCurrencyName, j.FIvPayCatName ,nvl(k.FAttrClsName,' ') as FAttrClsName";//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
            strSql = strSql +
                (FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 //edit by songjie 2011.05.31 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B
                 ", nvl(FAnalysisName1, ' ') as FAnalysisName1, nvl(FAnalysisName2, ' ') as FAnalysisName2, nvl(FAnalysisName3, ' ') as FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Stock_Invest") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
        
                " left join (select  FPortCode, FPortName,FStorageInitDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +  
                " ) d on a.FPortCode = d.FPortCode" +
            
                //end by lidaolong
                " left join (select FCuryCode as FCurrencyCode, FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ") g on a.FCuryCode = g.FCurrencyCode" +
                FilterSql() +
              //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") k on a.FAttrClsCode = k.FAttrClsCode " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " left join (select FIVPayCatCode, FIVPayCatName from Tb_Base_InvestPayCat" +
                " where FCheckState = 1 group by FIvPayCatCode, FIvPayCatName) j on a.FIvPayCatCode = j.FIvPayCatCode " +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            //---add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B---//
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("InvestStorage");
            rs =dbl.openResultSet(yssPageInationBean);
            //---add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B---//
            //delete by songjie 2011.05.26 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B
            //rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.strIvPayCatCode = rs.getString("FIVPayCatCode") + "";
                this.strIvPayCatName = rs.getString("FIVPayCatName") + "";
                this.strStorageDate = rs.getDate("FStorageDate") + "";
                this.strStorageStartDate = rs.getDate("FStorageInitDate") + "";
                this.strBal = rs.getDouble("FBal") + "";
                this.strCuryCode = rs.getString("FCuryCode") + "";
                this.strCuryName = rs.getString("FCurrencyName") + "";
                this.strPortCode = rs.getString("FPortCode") + "";
                this.strPortName = rs.getString("FPortName") + "";
                this.strPortCuryRate = rs.getDouble("FPortCuryRate") + "";
                this.strPortCuryBal = rs.getDouble("FPortCuryBal") + "";

                this.strBaseCuryRate = rs.getDouble("FBaseCuryRate") + "";

                this.strBaseCuryBal = rs.getDouble("FBaseCuryBal") + "";
                this.intStorageState = rs.getInt("FStorageInd");
                this.strFAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
                this.strFAnalysisName1 = rs.getString("FAnalysisName1") + "";
                this.strFAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
                this.strFAnalysisName2 = rs.getString("FAnalysisName2") + "";
                this.strFAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
                this.strFAnalysisName3 = rs.getString("FAnalysisName3") + "";
                this.strYearMonth = rs.getString("FYearMonth") + "";
                
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                this.strAttrClsCode = rs.getString("FAttrClsCode") + "";
                this.strAttrClsName = rs.getString("FAttrClsName") + "";
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }

            AssetStorageCfgBean assetgroupcfg = new AssetStorageCfgBean();
            assetgroupcfg.setYssPub(pub);
            sDateStr = assetgroupcfg.getPartSetting("investpay");

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
            //edit by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + "\r\f" + sDateStr;
        } catch (Exception e) {
            throw new YssException("获取运营收支库存信息出错！ \r\n" + e.getMessage(), e);
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
    public String getListViewData3() {
        return "";
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
     * checkInput    新增，修改数据做输入检查
     * xuqiji 20090603:QDV4中保2009年05月26日01_B  MS00469 添加现金库存时因没有Yearmonth字段导致判断条件不满足而无法增加数据
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
    	//---- add by wuweiqi 20110126 用于解决所属分类中初始化不为空 的问题-------------//
    	if(this.strAttrClsCode.length()==0){
    		this.strAttrClsCode=" ";
    	}	
    	//----------------------end by wuweiqi  20110126 ----------------------------//
    	//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Stock_Invest"), "FIVPayCatCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FStorageDate,FYearMonth,FATTRCLSCODE,FCuryCode",
                               this.strIvPayCatCode + "," + this.strPortCode + "," +
                               this.strFAnalysisCode1 + "," +
                               this.strFAnalysisCode2 + "," +
                               this.strFAnalysisCode3 + "," +
                               YssFun.formatDate(this.strStorageDate,
                                                 "yyyy-MM-dd") + "," + this.strYearMonth + 
                                                 ","+this.strAttrClsCode+//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
                                                 ","+this.strCuryCode,// #2279:: add by wuweiqi 20110126 用户需要支持多币种进行分类 
                               this.strOldIvPayCatCode + "," + this.strOldPortCode +
                               "," + this.strOldFAnalysisCode1 + "," +
                               this.strOldFAnalysisCode2 + "," +
                               this.strOldFAnalysisCode3 + "," +
                               YssFun.formatDate( (this.strOldStorageDate.length() == 0 ?
            "1900-01-01" : this.strOldStorageDate), "yyyy-MM-dd") +
            //--- MS00573 QDV4赢时胜（上海）2009年7月19日02_B ---------------------
                               "," + this.strOldYearMonth+ //增加对旧年月的判断
            //------------------------------------------------------------------
//                               ","+this.strAttrClsCode+//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
//                               ","+this.strOldCuryCode+// #2279:: add by wuweiqi 20110126 用户需要支持多币种进行分类
//                               ","+this.strOldAttrClsCode//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
                               
                         //update by guolongchao 20110711 BUG2156  原来的没有将新数据和老数据进行一一对应，导致修改子组合的分类 时出错
                               ","+this.strOldAttrClsCode+//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
                               ","+this.strOldCuryCode// #2279:: add by wuweiqi 20110126 用户需要支持多币种进行分类
                               
            );
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        InvestPayRecBean invRecPay = new InvestPayRecBean();
        invRecPay.setYssPub(pub);
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Stock_Invest") +
                "(FIVPayCatCode, FStorageDate, FPortCode, FCuryCode, FBal, " +
                " FPortCuryRate,FPortCuryBal, FBaseCuryRate,FBaseCuryBal,FAnalysisCode1, FAnalysisCode2,FAnalysisCode3,FATTRCLSCODE," +//添加所属类别字段 FATTRCLSCODE add by jiangshichao 2010.11.22 NO.125 用户需要对组合按资本类别进行子组合的分类
                " FStorageInd, FYearMonth, FCheckState, FCreator, FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.strIvPayCatCode) + "," +
                dbl.sqlDate(this.strStorageDate) + "," +
                dbl.sqlString(this.strPortCode) + "," +
                dbl.sqlString(this.strCuryCode) + "," +
                this.strBal + "," +
                this.strPortCuryRate + "," +
                this.strPortCuryBal + "," +
                this.strBaseCuryRate + "," +
                this.strBaseCuryBal + "," +

                dbl.sqlString(this.strFAnalysisCode1.length() == 0 ? " " :
                              this.strFAnalysisCode1) + "," +
                dbl.sqlString(this.strFAnalysisCode2.length() == 0 ? " " :
                              this.strFAnalysisCode2) + "," +
                dbl.sqlString(this.strFAnalysisCode3.length() == 0 ? " " :
                              this.strFAnalysisCode3) + "," + //  lzp  modify
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
               (this.strAttrClsCode.trim().length() == 0 ? dbl.sqlString(" "): dbl.sqlString(this.strAttrClsCode))+",2,"+           
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//   
                (this.bBegin.equalsIgnoreCase("false") ?
                 dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM")) :
                 dbl.sqlString(this.strYearMonth)) + " , " +

                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) +
                ", " + dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            if (this.sInvestRecPayBal != null &&
                this.sInvestRecPayBal.trim().length() != 0) {
                InvestPayRecBean investpaybal = new InvestPayRecBean();
                investpaybal.setYssPub(pub);
                investpaybal.saveMutliStorageData(this.sInvestRecPayBal, true, this);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------
            /**shashijie 2012-7-2 STORY 2475 */
            InvestPayRecBean filType = new InvestPayRecBean();
            filType.setSYearMonth(this.strYearMonth);
            filType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filType.setSPortCode(this.strPortCode);
            filType.setSAnalysisCode1(this.strFAnalysisCode1);
            filType.setSAnalysisCode2(this.strFAnalysisCode2);
            filType.setSAnalysisCode3(this.strFAnalysisCode3);
            filType.setSIvPayCatCode(this.strIvPayCatCode);
            filType.setBBegin("false");
            invRecPay.setFilterType(filType);
            filType = invRecPay.getFilterType();
            this.setASubData(invRecPay.getListViewData1());
            /**end*/
            //-----------------------------------------------------------
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增运营收支库存信息出错", e); //--- modify by wangzuochun 2010.10.23 BUG #178 新建两条相同数据时，提示信息错误---QDV4赢时胜(测试)2010年10月21日04_B.xls
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
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        InvestPayRecBean investPayRec = new InvestPayRecBean();
        investPayRec.setYssPub(pub);
        try {
            //--------------------------------------------------
            InvestPayRecBean filterType2 = new InvestPayRecBean();
            filterType2.setSYearMonth(this.strYearMonth);
            filterType2.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filterType2.setSPortCode(this.strPortCode);
            filterType2.setSAnalysisCode1(this.strFAnalysisCode1);
            filterType2.setSAnalysisCode2(this.strFAnalysisCode2);
            filterType2.setSAnalysisCode3(this.strFAnalysisCode3);
            filterType2.setSIvPayCatCode(this.strIvPayCatCode);
            filterType2.setBBegin("false");
            investPayRec.setFilterType(filterType2);
            filterType2 = investPayRec.getFilterType();
            this.setBSubData(investPayRec.getListViewData1());
            //-----------------------------------------------------------
            strSql = "update " + pub.yssGetTableName("Tb_Stock_Invest") +
                " set FIVPayCatCode = " +
                dbl.sqlString(this.strIvPayCatCode) + ", FStorageDate = "
                + dbl.sqlDate(this.strStorageDate) + ",FPortCode = "
                + dbl.sqlString(this.strPortCode) + ", FCuryCode = "
                + dbl.sqlString(this.strCuryCode) + ", FBal ="
                //---- lzp  modify 2007 12.12
                + this.strBal + ", FPortCuryRate="
                + this.strPortCuryRate + ",FPortCuryBal = "
                + this.strPortCuryBal + ",FBaseCuryRate ="
                + this.strBaseCuryRate + ",FBaseCuryBal = "
                + this.strBaseCuryBal + ",FAnalysisCode1 = "
                +
                dbl.sqlString(this.strFAnalysisCode1.length() == 0 ? " " :
                              this.strFAnalysisCode1) +
                ", FAnalysisCode2="
                +
                dbl.sqlString(this.strFAnalysisCode2.length() == 0 ? " " :
                              this.strFAnalysisCode2) +
                ", FAnalysisCode3="
                +
                dbl.sqlString(this.strFAnalysisCode3.length() == 0 ? " " :
                              this.strFAnalysisCode3) +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                //delete by songjie 2011.03.18 BUG:1275 QDV4赢时胜(上海开发部)2011年03月8日01_B 删除所属分类字段对应的多余的sql语句
                //(this.strAttrClsCode.trim().length() == 0 ? dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode))+             
                ", Fattrclscode ="+
                (this.strAttrClsCode.trim().length() == 0 ?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode))+             
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//                
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = "
                + dbl.sqlString(this.creatorCode) + " , FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                ",FYearMonth = " + dbl.sqlString(this.strYearMonth) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FIvPayCatCode = " +
                dbl.sqlString(this.strOldIvPayCatCode) +
                " and FPortCode=" + dbl.sqlString(this.strOldPortCode) +
                " and FStorageDate=" + dbl.sqlDate(this.strOldStorageDate)+
                " and FCuryCode=" +dbl.sqlString(this.strOldCuryCode);//  #2279:: add by wuweiqi 20110126 支持多币种

            strSql = strSql + " and FYearMonth =" + dbl.sqlString(this.strOldYearMonth); //modified libo 20090514 针对运营收支库存进行操作时期初数复选框的功能存在缺陷    QDV4赢时胜（上海）2009年5月7日01_B

            if (!this.strOldFAnalysisCode1.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode1.length() > 0) {
                strSql = strSql + " and FAnalysisCode1=" +
                    dbl.sqlString(this.strOldFAnalysisCode1);
            }
            if (!this.strOldFAnalysisCode2.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode2.length() > 0) {
                strSql = strSql + " and FAnalysisCode2=" +
                    dbl.sqlString(this.strOldFAnalysisCode2);
            }
            if (!this.strOldFAnalysisCode3.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode3.length() > 0) {
                strSql = strSql + " and FAnalysisCode3=" +
                    dbl.sqlString(this.strOldFAnalysisCode3);
            }
            if(!this.strOldAttrClsCode.equalsIgnoreCase("null") && this.strOldAttrClsCode.length()>0){
                strSql = strSql + " and fattrclscode="+dbl.sqlString(this.strOldAttrClsCode);	
             }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            if (this.sInvestRecPayBal != null &&
                this.sInvestRecPayBal.trim().length() != 0) {
                InvestPayRecBean investPay = new InvestPayRecBean();
                investPay.setYssPub(pub);
                investPay.saveMutliStorageData(this.sInvestRecPayBal, true, this);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //--------------------------------------------------
            /**shashijie 2012-7-2 STORY 2475 */
            InvestPayRecBean filType = new InvestPayRecBean();
            filType.setSYearMonth(this.strYearMonth);
            filType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filType.setSPortCode(this.strPortCode);
            filType.setSAnalysisCode1(this.strFAnalysisCode1);
            filType.setSAnalysisCode2(this.strFAnalysisCode2);
            filType.setSAnalysisCode3(this.strFAnalysisCode3);
            filType.setSIvPayCatCode(this.strIvPayCatCode);
            filType.setBBegin("false");
            investPayRec.setFilterType(filType);
            filType = investPayRec.getFilterType();
            this.setASubData(investPayRec.getListViewData1());
            /**end*/
            //-----------------------------------------------------------
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改运营收支库存信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        InvestPayRecBean invRecPay = new InvestPayRecBean();
        invRecPay.setYssPub(pub);
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Stock_Invest") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FIVPayCatCode = " + dbl.sqlString(this.strIvPayCatCode) +
                " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " and FAttrClsCode ="+(this.strAttrClsCode.trim().length() == 0 ?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode))           
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------// 
                // ---#2279 农行计提管理费需求  add by fangjiang 20110214---------------------------------//
                + " and FCuryCode= " + dbl.sqlString(this.strCuryCode);
                //---------------------------
            if (this.bBegin.equalsIgnoreCase("true")) {
                strSql = strSql + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyy") +
                                  "00");
            } else {
                strSql = strSql + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM"));
            }
            if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                this.strFAnalysisCode1.length() > 0) {
                strSql = strSql + " and FAnalysisCode1=" +
                    dbl.sqlString(this.strFAnalysisCode1);
            }
            if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                this.strFAnalysisCode2.length() > 0) {
                strSql = strSql + " and FAnalysisCode2=" +
                    dbl.sqlString(this.strFAnalysisCode2);
            }
            if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                this.strFAnalysisCode3.length() > 0) {
                strSql = strSql + " and FAnalysisCode3=" +
                    dbl.sqlString(this.strFAnalysisCode3);
            }

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            strSql = "update " + pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FIVPayCatCode = " + dbl.sqlString(this.strIvPayCatCode) +
                " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " and FAttrClsCode ="+(this.strAttrClsCode.trim().length() == 0 ?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode))            
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------// 
                // ---#2279 农行计提管理费需求  add by fangjiang 20110214---------------------------------//
                + " and FCuryCode= " + dbl.sqlString(this.strCuryCode);
                //---------------------------
            if (this.bBegin.equalsIgnoreCase("true")) {
                strSql = strSql + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyy") +
                                  "00");
            } else {
                strSql = strSql + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM"));
            }

            if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                this.strFAnalysisCode1.length() > 0) {
                strSql = strSql + " and FAnalysisCode1=" +
                    dbl.sqlString(this.strFAnalysisCode1);
            }
            if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                this.strFAnalysisCode2.length() > 0) {
                strSql = strSql + " and FAnalysisCode2=" +
                    dbl.sqlString(this.strFAnalysisCode2);
            }
            if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                this.strFAnalysisCode3.length() > 0) {
                strSql = strSql + " and FAnalysisCode3=" +
                    dbl.sqlString(this.strFAnalysisCode3);
            }
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------
            /**shashijie 2012-7-2 STORY 2475 */
            InvestPayRecBean filType = new InvestPayRecBean();
            filType.setSYearMonth(this.strYearMonth);
            filType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filType.setSPortCode(this.strPortCode);
            filType.setSAnalysisCode1(this.strFAnalysisCode1);
            filType.setSAnalysisCode2(this.strFAnalysisCode2);
            filType.setSAnalysisCode3(this.strFAnalysisCode3);
            filType.setSIvPayCatCode(this.strIvPayCatCode);
            filType.setBBegin("false");
            invRecPay.setFilterType(filType);
            filType = invRecPay.getFilterType();
            this.setASubData(invRecPay.getListViewData1());
            /**end*/
            //-----------------------------------------------------------
        } catch (Exception e) {
            throw new YssException("删除运营收支库存信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkSetting
     * 功能：可以处理库存管理业务中运营收支库存的设置审核、反审核、和回收站的还原功能。
     *      还原功能可以同时处理多条信息
     * @throws YssException
     * 修改时间：2008年6月11号
     * 修改人：蒋春
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] arrData = null;
        InvestPayRecBean invRecPayBal = new InvestPayRecBean();
        invRecPayBal.setYssPub(pub);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Stock_Invest") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FIVPayCatCode = " +
                        dbl.sqlString(this.strIvPayCatCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)
                        // ---#2279 农行计提管理费需求  add by fangjiang 20110214---------------------------------//
                        + " and FCuryCode= " + dbl.sqlString(this.strCuryCode);
                        //---------------------------
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1=" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2=" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3=" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    if(this.strAttrClsCode.trim().length()>0){
                    	strSql =strSql+" and FATTRCLSCODE="+dbl.sqlString(this.strAttrClsCode);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    dbl.executeSql(strSql);

                    strSql = "update " + pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FIVPayCatCode = " +
                        dbl.sqlString(this.strIvPayCatCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)
                        // ---#2279 农行计提管理费需求  add by fangjiang 20110214---------------------------------//
                        + " and FCuryCode= " + dbl.sqlString(this.strCuryCode);
                        //---------------------------
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1=" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2=" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3=" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    if(this.strAttrClsCode.trim().length()>0){
                    	strSql =strSql+" and FATTRCLSCODE="+dbl.sqlString(this.strAttrClsCode);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    dbl.executeSql(strSql);
                }
            } else {
                if (this.strIvPayCatCode != null &&
                    this.strIvPayCatCode.trim().length() > 0) {
                    strSql = "update " + pub.yssGetTableName("Tb_Stock_Invest") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FIVPayCatCode = " +
                        dbl.sqlString(this.strIvPayCatCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)
                        // ---#2279 农行计提管理费需求  add by fangjiang 20110214---------------------------------//
                        + " and FCuryCode= " + dbl.sqlString(this.strCuryCode);
                    	//---------------------------
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1=" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2=" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3=" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    if(this.strAttrClsCode.trim().length()>0){
                    	strSql =strSql+" and FATTRCLSCODE="+dbl.sqlString(this.strAttrClsCode);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    dbl.executeSql(strSql);

                    strSql = "update " + pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FIVPayCatCode = " +
                        dbl.sqlString(this.strIvPayCatCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)
                        // ---#2279 农行计提管理费需求  add by fangjiang 20110214---------------------------------//
		                + " and FCuryCode= " + dbl.sqlString(this.strCuryCode);
		                //---------------------------
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1=" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2=" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3=" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    if(this.strAttrClsCode.trim().length()>0){
                    	strSql =strSql+" and FATTRCLSCODE="+dbl.sqlString(this.strAttrClsCode);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------
            InvestPayRecBean filterType = new InvestPayRecBean();
            filterType.setSYearMonth(this.strYearMonth);
            filterType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filterType.setSPortCode(this.strPortCode);
            filterType.setSAnalysisCode1(this.strFAnalysisCode1);
            filterType.setSAnalysisCode2(this.strFAnalysisCode2);
            filterType.setSAnalysisCode3(this.strFAnalysisCode3);
            filterType.setSIvPayCatCode(this.strIvPayCatCode);
            filterType.setBBegin("false"); //------------------------
            invRecPayBal.setFilterType(filterType);
            filterType = invRecPayBal.getFilterType();
            this.setASubData(invRecPayBal.getListViewData1());
            //-----------------------------------------------------------
        } catch (Exception e) {
            throw new YssException("审核运营收支库存信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.sInvestRecPayBal = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            this.sRecycled = sRowStr;

            reqAry = sTmpStr.split("\t");
            this.strIvPayCatCode = reqAry[0];
            this.strIvPayCatName = reqAry[1];
            this.strStorageDate = reqAry[2];
            this.strBal = reqAry[3];
            this.strCuryCode = reqAry[4];
            this.strCuryName = reqAry[5];
            this.strPortCode = reqAry[6];
            this.strPortName = reqAry[7];
            this.strPortCuryRate = reqAry[8];
            this.strBaseCuryRate = reqAry[9];
            this.intStorageState = Integer.parseInt(reqAry[10]);
            this.strFAnalysisCode1 = reqAry[11];
            this.strFAnalysisName1 = reqAry[12];
            this.strFAnalysisCode2 = reqAry[13];
            this.strFAnalysisName2 = reqAry[14];
            this.strFAnalysisCode3 = reqAry[15];
            this.strFAnalysisName3 = reqAry[16];

            this.strPortCuryBal = reqAry[17];
            this.strBaseCuryBal = reqAry[18];
            this.checkStateId = Integer.parseInt(reqAry[19]);
            this.strOldIvPayCatCode = reqAry[20];
            this.strOldPortCode = reqAry[21];
            this.strOldStorageDate = reqAry[22];
            this.strOldFAnalysisCode1 = reqAry[23];
            this.strOldFAnalysisCode2 = reqAry[24];
            this.strOldFAnalysisCode3 = reqAry[25];
            this.strStorageStartDate = reqAry[26];
            this.strIsOnlyColumn = reqAry[27];
            this.bBegin = reqAry[28];
            this.strYearMonth = reqAry[29];
            this.strOldYearMonth = reqAry[30];
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            this.strAttrClsCode = reqAry[31].trim().length() ==0 ? " ":reqAry[31];           
            this.strOldAttrClsCode = reqAry[32].trim().length() ==0 ? " ":reqAry[32];  
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            this.strOldCuryCode=reqAry[33]; // #2279::  add by wuweiqi 20110126  用户需求支持多币种
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new InvestBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析运营收支库存请求信息出错！", e);
        }
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            if (this.filterType.strIsOnlyColumn.equals("0")) {
                sResult = " where 1 = 2";
                return sResult;
            } else {
                sResult = " where 1 = 1";
            }
            //如果是期初，直接筛选所有期初，和库存日期无关；如果不是期初，根据库存日期筛选
            //libo modified 20090513 MS00435 QDV4赢时胜（上海）2009年5月7日01_B
            //add by rujiangpeng 20100426 MS01066 QDV4赢时胜上海2010年04月02日02_AB 
            if (this.filterType.bBegin.equalsIgnoreCase("false")) {
            	if(!this.filterType.strStorageDate.equalsIgnoreCase("9998-12-31"))
            	{
            		sResult = sResult + "  and " + dbl.sqlRight("FYearMonth", 2) + " <> '00'" +
                    " and a.FStorageDate = " + dbl.sqlDate(filterType.strStorageDate);
            	}                
            } else{
            	if(!this.filterType.strStorageDate.equalsIgnoreCase("9998-12-31"))
            	{
            		sResult = sResult + "  and a.FYearMonth = '" +
                    this.filterType.strStorageDate.substring(0, 4) + "00'";
            	}else
            	{
            		sResult = sResult + "  and a.FYearMonth like'____00'";
            	}                
            }
            //=============================End MS00435 ===========================
            if (this.filterType.strIvPayCatCode.length() != 0) {
                sResult = sResult + " and a.FIVPayCatCode like '" +
                    filterType.strIvPayCatCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.strPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strBal.length() != 0) {
                sResult = sResult + " and a.FBal =" +
                    filterType.strBal;
            }
            if (this.filterType.strCuryCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.strCuryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strPortCuryRate.length() != 0) {
                sResult = sResult + " and a.FPortCuryRate = " +
                    filterType.strPortCuryRate;
            }
            if (this.filterType.strPortCuryBal.length() != 0) {
                sResult = sResult + " and a.FPortCuryBal = " +
                    filterType.strPortCuryBal;
            }
            if (this.filterType.strBaseCuryRate.length() != 0) {
                sResult = sResult + " and a.FBaseCuryRate = " +
                    filterType.strBaseCuryRate;
            }
            if (this.filterType.strBaseCuryBal.length() != 0) {
                sResult = sResult + " and a.FBaseCuryBal = " +
                    filterType.strBaseCuryBal;
            }
            if (this.filterType.strFAnalysisCode1.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode1 like '" +
                    filterType.strFAnalysisCode1.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strFAnalysisCode2.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode2 like '" +
                    filterType.strFAnalysisCode2.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strFAnalysisCode3.length() != 0 &&
                !this.filterType.strFAnalysisCode3.equals("0")) {
                sResult = sResult + " and a.FAnalysisCode3 like '" +
                    filterType.strFAnalysisCode3.replaceAll("'", "''") + "%'";
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            if (this.filterType.strAttrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.strAttrClsCode.replaceAll("'", "''") + "%'";
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            
        }
        return sResult;
    }

    /**
     * 获取辅助字段之查询Sql语句
     * @return String
     */
    public String FilterSql() throws YssException, SQLException {
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
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                     /*   " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
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
                    " from  " +
                    pub.yssGetTableName("tb_para_broker") +
                    " y where y.FCheckState = 1 ) broker on a.FAnalysisCode" +
                    i + " = broker.FBrokerCode";
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("003")) {
                    sResult = sResult +
                        " left join (select FExchangeCode,FExchangeName as FAnalysisName" +
                        i +
                        " from tb_base_exchange) e on a.FAnalysisCode" + i +
                        " = e.FExchangeCode " ;
                        // modify by fangjiang 2010.09.25 MS01770 QDV4赢时胜(测试)2010年09月17日1_B 
                        /*" left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        //fanghaoln 20100330 MS01051 QDV4赢时胜(测试)2010年03月23日02_AB
                        dbl.sqlDate(this.strStorageDate)+//edited by zhouxiang  MS01350
                        //----------------end MS01051--------------------------------
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        " join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) exchange on a.FAnalysisCode" +
                        i + " = exchange.FInvMgrCode";*/
                        //-----------------
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("004")) {
                    //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
                    if (sResult.indexOf("FAnalysisName2") > 0) { //若之前的分析代码拼装语句中已经有了FAnalysisName2的设置，则使用FAnalysisName3的设置。
                        sResult = sResult +
                            " left join (select FCatCode,FCatName as FAnalysisName3 from Tb_Base_Category where FCheckState = 1) category on a.FAnalysisCode" +
                            i + " = category.FCatCode";
                    } else { //若之前没有FAnalysisName2的设置，就依然使用之前的设置。
                        sResult = sResult +
                            //edited by zhouxiang MS01610 库存信息配置，设置分析代码 将FAnalysisName2改为FAnalysisName+i
                        	" left join (select FCatCode,FCatName as FAnalysisName"+i+" from Tb_Base_Category where FCheckState = 1) category on a.FAnalysisCode" +
                            i + " = category.FCatCode";
                    }
                    //--------------------------------------------------------------------------------//
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                      /*  " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                        i +
                        "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        //fanghaoln 20100330 MS01051 QDV4赢时胜(测试)2010年03月23日02_AB
                        dbl.sqlDate(this.strStorageDate) +//edited by zhouxiang  MS01350 有分析代码的运营收支库存内的应收应付报错 2010.6.24
                        //----------------end MS01051--------------------------------
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        "join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr on a.FAnalysisCode" +
                        i + " = invmgr.FInvMgrCode ";
                    */
                    
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                    i +
                    "  from  " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where n.FCheckState = 1) invmgr on a.FAnalysisCode" +
                    i + " = invmgr.FInvMgrCode ";
                
                    //end by lidaolong
                } else {
                    sResult = sResult +
                        " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
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

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strIvPayCatCode).append("\t");
        buf.append(this.strIvPayCatName).append("\t");
        buf.append(this.strStorageDate).append("\t");
        buf.append(this.strBal).append("\t");
        buf.append(this.strCuryCode).append("\t");
        buf.append(this.strCuryName).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.strPortCuryRate).append("\t");
        buf.append(this.strBaseCuryRate).append("\t");
        buf.append(this.intStorageState).append("\t");
        buf.append(this.strFAnalysisCode1).append("\t");
        buf.append(this.strFAnalysisName1).append("\t");
        buf.append(this.strFAnalysisCode2).append("\t");
        buf.append(this.strFAnalysisName2).append("\t");
        buf.append(this.strFAnalysisCode3).append("\t");
        buf.append(this.strFAnalysisName3).append("\t");
        buf.append(this.strPortCuryBal).append("\t");
        buf.append(this.strBaseCuryBal).append("\t");
        buf.append(this.strStorageStartDate).append("\t");
        buf.append(this.strYearMonth).append("\t");
        buf.append(this.bBegin).append("\t");
        buf.append(this.bBegin).append("\t");
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
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    /**
     * deleteRecycleData
     * 功能：从数据库彻底删除数据
     * @throws YssException
     * 时间：2008年6月11号
     * 修改人：蒋春
     */
    public void deleteRecycleData() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] arrData = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Invest") +
                        " where  FIVPayCatCode = " +
                        dbl.sqlString(this.strIvPayCatCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)
                        // ---#2279 农行计提管理费需求  add by fangjiang 20110214---------------------------------//
                        + " and FCuryCode= " + dbl.sqlString(this.strCuryCode);
                    	//---------------------------
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                   //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                   strSql = strSql + " and FYearMonth = " +
                       dbl.sqlString(this.strYearMonth);
                   //================================================fanghaoln end=========================================
                    if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1=" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2=" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3=" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    if(this.strAttrClsCode.trim().length()!=0){
                    	strSql = strSql + " and FAttrClsCode = " +
                        dbl.sqlString(this.strAttrClsCode);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    dbl.executeSql(strSql);

                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                        " where  FIVPayCatCode = " +
                        dbl.sqlString(this.strIvPayCatCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)
                        // ---#2279 农行计提管理费需求  add by fangjiang 20110214---------------------------------//
                        + " and FCuryCode= " + dbl.sqlString(this.strCuryCode);
                    	//---------------------------
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1=" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2=" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3=" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    if(this.strAttrClsCode.trim().length()!=0){
                    	strSql = strSql + " and FAttrClsCode = " +
                        dbl.sqlString(this.strAttrClsCode);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    dbl.executeSql(strSql);
                }
            } else {
                if (this.strIvPayCatCode != null &&
                    this.strIvPayCatCode.trim().length() > 0) {
                    strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Invest") +
                        " where  FIVPayCatCode = " +
                        dbl.sqlString(this.strIvPayCatCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)
                        // ---#2279 农行计提管理费需求  add by fangjiang 20110214---------------------------------//
                        + " and FCuryCode= " + dbl.sqlString(this.strCuryCode);
                    	//---------------------------
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1=" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2=" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3=" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    if(this.strAttrClsCode.trim().length()!=0){
                    	strSql = strSql + " and FAttrClsCode = " +
                        dbl.sqlString(this.strAttrClsCode);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    dbl.executeSql(strSql);

                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                        " where  FIVPayCatCode = " +
                        dbl.sqlString(this.strIvPayCatCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)
                        // ---#2279 农行计提管理费需求  add by fangjiang 20110214---------------------------------//
		                + " and FCuryCode= " + dbl.sqlString(this.strCuryCode);
		                //---------------------------
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1=" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2=" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3=" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    if(this.strAttrClsCode.trim().length()!=0){
                    	strSql = strSql + " and FAttrClsCode = " +
                        dbl.sqlString(this.strAttrClsCode);
                    }
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
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
}
