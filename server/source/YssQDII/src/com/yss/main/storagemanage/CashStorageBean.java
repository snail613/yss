package com.yss.main.storagemanage;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.parasetting.*;
import com.yss.util.*;

/**
 * <p>Title: CashStorageBean </p>
 * <p>Description: 现金库存 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */

public class CashStorageBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strCashAccCode = ""; //帐户代码
    private String strCashAccName = ""; //帐户名称
    private String strStorageDate = ""; //库存日期
    private String strStorageStartDate = ""; //库存初始日期
    private String strYearMonth = ""; //库存年月
    private String strAccBalance = ""; //帐户余额
    private String strCuryCode = ""; //交易货币代码
    private String strCuryName = ""; //交易货币名称
    private String strPortCode = ""; //组合代码
    private String strPortName = ""; //组合名称
    private String strPortCuryRate = ""; //原币汇率
    private String strBaseCuryRate = ""; //基础汇率

//   private String strGainedInterest = ""; //累计实收收益
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
    private String strOldCashAccCode = "";
    private String strOldPortCode = "";
    private String strOldStorageDate = "";
    private String strOldFAnalysisCode1 = "";
    private String strOldFAnalysisCode2 = "";
    private String strOldFAnalysisCode3 = "";
    private String strIsOnlyColumn = "0";
    private String sRecycled = "";

    private CashStorageBean filterType;
    private String sCashRecPayBal = "";

    private String bBegin; //是否取期初数

    private String flag = "";
    private String strOldYearMonth="";//fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
    public CashStorageBean() {
    }

    public int getIntStorageState() {
        return this.intStorageState;
    }

    public void setIntStorageState(int intStorageState) {
        this.intStorageState = intStorageState;
    }

    public String getStrAccBalance() {
        return this.strAccBalance;
    }

    public void setStrAccBalance(String strAccBalance) {
        this.strAccBalance = strAccBalance;
    }

    public String getStrBaseCuryRate() {
        return this.strBaseCuryRate;
    }

    public void setStrBaseCuryRate(String strBaseCuryRate) {
        this.strBaseCuryRate = strBaseCuryRate;
    }

    public String getStrCashAccCode() {
        return this.strCashAccCode;
    }

    public void setStrCashAccCode(String strCashAccCode) {
        this.strCashAccCode = strCashAccCode;
    }

    public String getStrCashAccName() {
        return this.strCashAccName;
    }

    public void setStrCashAccName(String strCashAccName) {
        this.strCashAccName = strCashAccName;
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

    public String getBBegin() {
        return bBegin;
    }

    public String getStrYearMonth() {
        return strYearMonth;
    }

    public String getStrOldYearMonth() {
        return strOldYearMonth;
    }

    public void setStrStorageDate(String strStorageDate) {
        this.strStorageDate = strStorageDate;
    }

    public void setBBegin(String bBegin) {
        this.bBegin = bBegin;
    }

    public void setStrYearMonth(String strYearMonth) {
        this.strYearMonth = strYearMonth;
    }

    public void setStrOldYearMonth(String strOldYearMonth) {
        this.strOldYearMonth = strOldYearMonth;
    }

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
                    this.sCashRecPayBal = sRowStr.split("\r\t")[2];
                }

            } else {
                sTmpStr = sRowStr;
            }
            this.sRecycled = sRowStr;

            reqAry = sTmpStr.split("\t");
            this.strCashAccCode = reqAry[0];
            this.strCashAccName = reqAry[1];
            this.strStorageDate = reqAry[2];
            this.strAccBalance = reqAry[3];
            this.strCuryCode = reqAry[4];
            this.strCuryName = reqAry[5];
            this.strPortCode = reqAry[6];
            this.strPortName = reqAry[7];
            this.strPortCuryRate = reqAry[8];
            this.strBaseCuryRate = reqAry[9];
            //     this.strGainedInterest = reqAry[10];
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
            this.strOldCashAccCode = reqAry[20];
            this.strOldPortCode = reqAry[21];
            this.strOldStorageDate = reqAry[22];
            this.strOldFAnalysisCode1 = reqAry[23];
            this.strOldFAnalysisCode2 = reqAry[24];
            this.strOldFAnalysisCode3 = reqAry[25];
            this.strStorageStartDate = reqAry[26];
            this.strIsOnlyColumn = reqAry[27];
            this.bBegin = reqAry[28];
            this.strYearMonth = reqAry[29];
             this.strOldYearMonth = reqAry[30];//fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
             //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
             this.strAttrClsCode = reqAry[31].trim().length() ==0 ? " ":reqAry[31];
             this.strOldAttrClsCode = reqAry[32].trim().length() ==0 ? " ":reqAry[32];
             //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new CashStorageBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析现金库存请求信息出错", e);
        }
    }

    /**
     * buildRowStr
     * 返回属性信息
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strCashAccCode).append("\t");
        buf.append(this.strCashAccName).append("\t");
        buf.append(this.strStorageDate).append("\t");
        buf.append(this.strAccBalance).append("\t");
        buf.append(this.strCuryCode).append("\t");
        buf.append(this.strCuryName).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.strPortCuryRate).append("\t");
        buf.append(this.strBaseCuryRate).append("\t");
//      buf.append(this.strGainedInterest).append("\t");
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
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 新增一条现金库存信息
     * @throws YssException
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        CashRecPayBalBean cashRecPayBal = new CashRecPayBalBean();
        cashRecPayBal.setYssPub(pub);
        try {
            // lzp modify 数字型去单引号 只字符型加单引号 20080123
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Stock_Cash") +
                "(FCashAccCode, FStorageDate, FPortCode, FCuryCode, FAccBalance, " +
                " FPortCuryRate,FPortCuryBal, FBaseCuryRate,FBaseCuryBal,FAnalysisCode1, FAnalysisCode2, " +
                //" FAnalysisCode3, FStorageInd, FYearMonth, FCheckState, FCreator, FCreateTime,FCheckUser) " +
                " FAnalysisCode3,FATTRCLSCODE, FStorageInd, FYearMonth, FCheckState, FCreator, FCreateTime,FCheckUser) " +//添加所属类别字段 add by jiangshichao 2010.11.22
                " values(" + dbl.sqlString(this.strCashAccCode) + "," +
                dbl.sqlDate(this.strStorageDate) + "," +
                dbl.sqlString(this.strPortCode) + "," +
                dbl.sqlString(this.strCuryCode) + "," +
                this.strAccBalance + "," +

                this.strPortCuryRate + "," +
                this.strPortCuryBal + "," +
                this.strBaseCuryRate + "," +
                this.strBaseCuryBal + "," +
                dbl.sqlString(this.strFAnalysisCode1.length() == 0 ? " " :
                              this.strFAnalysisCode1) + "," +
                dbl.sqlString(this.strFAnalysisCode2.length() == 0 ? " " :
                              this.strFAnalysisCode2) + "," +
                dbl.sqlString(this.strFAnalysisCode3.length() == 0 ? " " :
                //              this.strFAnalysisCode3) + ",2," + 
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                	            this.strFAnalysisCode3) + ","+
                	            (this.strAttrClsCode.trim().length() == 0 ?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode))+",2,"+
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

            if (this.sCashRecPayBal != null &&
                this.sCashRecPayBal.trim().length() != 0) {
                CashRecPayBalBean cashpaybal = new CashRecPayBalBean();
                cashpaybal.setYssPub(pub);
                //        cashpaybal.setBBegin(this.bBegin);
                cashpaybal.saveMutliStorageData(this.sCashRecPayBal, true, this);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------
            /**shashijie 2012-7-2 STORY 2475 */
            CashRecPayBalBean filType = new CashRecPayBalBean();
            filType.setSYearMonth(this.strYearMonth);
            filType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filType.setSPortCode(this.strPortCode);
            filType.setSAnalysisCode1(this.strFAnalysisCode1);
            filType.setSAnalysisCode2(this.strFAnalysisCode2);
            filType.setSAnalysisCode3(this.strFAnalysisCode3);
            filType.setSCashAccCode(this.strCashAccCode);
            filType.setBBegin("false");
            cashRecPayBal.setFilterType(filType);
            filType = cashRecPayBal.getFilterType();
            this.setASubData(cashRecPayBal.getListViewData1());
            /**end*/
            //-----------------------------------------------------------
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增现金库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkInput
     * 数据验证
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        flag = Byte.toString(btOper);
        //xuqiji 20090603:QDV4中保2009年05月26日01_B  MS00469 添加现金库存时因没有Yearmonth字段导致判断条件不满足而无法增加数据
        //jiangshichao 2010.11.22 NO.125 用户需要对组合按资本类别进行子组合的分类  添加所属分类字段
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Stock_Cash"), "FCashAccCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FStorageDate,FYearMonth,FATTRCLSCODE",

                               this.strCashAccCode + "," + this.strPortCode + "," +
                               this.strFAnalysisCode1 + "," +
                               this.strFAnalysisCode2 + "," +
                               this.strFAnalysisCode3 + "," +
                               YssFun.formatDate(this.strStorageDate, "yyyy-MM-dd") + "," +
                               this.strYearMonth+","+
                               this.strAttrClsCode,

                               this.strOldCashAccCode + "," + this.strOldPortCode + "," +
                               this.strOldFAnalysisCode1 + "," +
                               this.strOldFAnalysisCode2 + "," +
                               this.strOldFAnalysisCode3 + "," +
                               YssFun.formatDate( (this.strOldStorageDate.length() == 0 ? "1900-01-01" : this.strOldStorageDate), "yyyy-MM-dd") + "," +
                               //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                               (this.strOldYearMonth.length() == 0 ? YssFun.formatDate(strOldStorageDate, "yyyyMM") : strOldYearMonth)+","+
                               this.strOldAttrClsCode);
        //----------------------------------------------end-------------------------------------------------------//

    }

    /**
     * checkSetting
     * 功能：可以处理库存管理业务中现金库存的设置审核、反审核、和回收站的还原功能。
     *      还原功能可以同时处理多条信息
     * @throws YssException
     * 修改时间：2008年6月10号
     * 修改人：蒋春
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] arrData = null;
        CashRecPayBalBean cashRecPayBal = new CashRecPayBalBean();
        cashRecPayBal.setYssPub(pub);
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
                    strSql = "update " + pub.yssGetTableName("Tb_Stock_Cash") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FCashAccCode = " +
                        dbl.sqlString(this.strCashAccCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate);
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

                    strSql = "update " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FCashAccCode = " +
                        dbl.sqlString(this.strCashAccCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate);
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
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
                if (this.strCashAccCode != null &&
                    this.strCashAccCode.trim().length() > 0) {
                    strSql = "update " + pub.yssGetTableName("Tb_Stock_Cash") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FCashAccCode = " +
                        dbl.sqlString(this.strCashAccCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate);
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

                    strSql = "update " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FCashAccCode = " +
                        dbl.sqlString(this.strCashAccCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate);
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
            CashRecPayBalBean filterType = new CashRecPayBalBean();
            filterType.setSYearMonth(this.strYearMonth);
            filterType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filterType.setSPortCode(this.strPortCode);
            filterType.setSAnalysisCode1(this.strFAnalysisCode1);
            filterType.setSAnalysisCode2(this.strFAnalysisCode2);
            filterType.setSAnalysisCode3(this.strFAnalysisCode3);
            filterType.setSCashAccCode(this.strCashAccCode);
          //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            //filterType.set
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            filterType.setBBegin("false");
            cashRecPayBal.setFilterType(filterType);
            filterType = cashRecPayBal.getFilterType();
            this.setASubData(cashRecPayBal.getListViewData1());
            //-----------------------------------------------------------
        } catch (Exception e) {
            throw new YssException("审核现金库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 删除一条现金库存信息
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        CashRecPayBalBean cashRecPayBal = new CashRecPayBalBean();
        cashRecPayBal.setYssPub(pub);
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Stock_Cash") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCashAccCode = " + dbl.sqlString(this.strCashAccCode) +
                " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)+
                 //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " and FAttrClsCode ="+(this.strAttrClsCode.trim().length() == 0 ?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode));            
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------// 
                
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
            strSql = "update " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCashAccCode = " + dbl.sqlString(this.strCashAccCode) +
                " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " and FAttrClsCode ="+(this.strAttrClsCode.trim().length() == 0 ?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode));            
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------// 
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
            CashRecPayBalBean filterType = new CashRecPayBalBean();
            filterType.setSYearMonth(this.strYearMonth);
            filterType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filterType.setSPortCode(this.strPortCode);
            filterType.setSAnalysisCode1(this.strFAnalysisCode1);
            filterType.setSAnalysisCode2(this.strFAnalysisCode2);
            filterType.setSAnalysisCode3(this.strFAnalysisCode3);
            filterType.setSCashAccCode(this.strCashAccCode);
            filterType.setBBegin("false");
            cashRecPayBal.setFilterType(filterType);
            filterType = cashRecPayBal.getFilterType();
            this.setASubData(cashRecPayBal.getListViewData1());
            /**end*/
            //-----------------------------------------------------------

        } catch (Exception e) {
            throw new YssException("删除现金库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editOperData
     * 编辑一条现金库存信息
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        CashRecPayBalBean cashRecPayBal = new CashRecPayBalBean();
        cashRecPayBal.setYssPub(pub);
        try {
            //--------------------------------------------------
            CashRecPayBalBean filterType2 = new CashRecPayBalBean();
            filterType2.setSYearMonth(this.strYearMonth);
            filterType2.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filterType2.setSPortCode(this.strPortCode);
            filterType2.setSAnalysisCode1(this.strFAnalysisCode1);
            filterType2.setSAnalysisCode2(this.strFAnalysisCode2);
            filterType2.setSAnalysisCode3(this.strFAnalysisCode3);
            filterType2.setSCashAccCode(this.strCashAccCode);
            filterType2.setBBegin("false");
            cashRecPayBal.setFilterType(filterType2);
            filterType2 = cashRecPayBal.getFilterType();
            this.setBSubData(cashRecPayBal.getListViewData1());
            //-----------------------------------------------------------

            // lzp modify 数字型去单引号 只字符型加单引号 20080123
            strSql = "update " + pub.yssGetTableName("Tb_Stock_Cash") +
                " set FCashAccCode = " +
                dbl.sqlString(this.strCashAccCode) + ", FStorageDate = "
                + dbl.sqlDate(this.strStorageDate) + ",FPortCode = "
                + dbl.sqlString(this.strPortCode) + ", FCuryCode = "
                + dbl.sqlString(this.strCuryCode) + ", FAccBalance ="
                + this.strAccBalance + ", FPortCuryRate="
                //    + dbl.sqlString(this.strGainedInterest) + ", FPortCuryRate = "
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
                " , Fattrclscode = "+
                (this.strAttrClsCode.trim().length() == 0 ?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode))+             
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//                              
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = "
                + dbl.sqlString(this.creatorCode) + " , FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                ",FYearMonth = " +
                (this.bBegin.equalsIgnoreCase("false") ?
                 dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM")) :
                 dbl.sqlString(this.strYearMonth)) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FCashAccCode = " +
                dbl.sqlString(this.strOldCashAccCode) +
                " and FPortCode=" + dbl.sqlString(this.strOldPortCode) +
                " and FStorageDate=" + dbl.sqlDate(this.strOldStorageDate)+
                
                //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                " and FYearMonth =" + dbl.sqlString(this.strOldYearMonth.length() == 0 ? YssFun.formatDate(strOldStorageDate, "yyyyMM") : strOldYearMonth);
           

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

            if (this.sCashRecPayBal != null &&
                this.sCashRecPayBal.trim().length() != 0) {
                CashRecPayBalBean cashpaybal = new CashRecPayBalBean();
                cashpaybal.setYssPub(pub);
                cashpaybal.saveMutliStorageData(this.sCashRecPayBal, true, this);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //--------------------------------------------------
            /**shashijie 2012-7-2 STORY 2475 */
            CashRecPayBalBean filType = new CashRecPayBalBean();
            filType.setSYearMonth(this.strYearMonth);
            filType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filType.setSPortCode(this.strPortCode);
            filType.setSAnalysisCode1(this.strFAnalysisCode1);
            filType.setSAnalysisCode2(this.strFAnalysisCode2);
            filType.setSAnalysisCode3(this.strFAnalysisCode3);
            filType.setSCashAccCode(this.strCashAccCode);
            filType.setBBegin("false");
            cashRecPayBal.setFilterType(filType);
            filType = cashRecPayBal.getFilterType();
			/**end*/
            this.setASubData(cashRecPayBal.getListViewData1());
            //-----------------------------------------------------------

            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改现金库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
        	//edit by rujiangpeng 20100426 MS01066 QDV4赢时胜上海2010年04月02日02_AB
            if (this.filterType.bBegin.equalsIgnoreCase("false")) {
//            	if(!this.filterType.strStorageDate.equalsIgnoreCase("9998-12-31"))
//            	{
            		sResult = " where 1=1 and " + dbl.sqlRight("FYearMonth", 2) +
                    " <> '00'";
//            	}                
            } else{
            	if(!this.filterType.strStorageDate.equalsIgnoreCase("9998-12-31"))
            	{
            		 sResult = " where 1=1 and a.FYearMonth = '" +
                     this.filterType.strStorageDate.substring(0, 4) + "00'";
                 this.filterType.strStorageDate = this.filterType.strStorageDate.
                     substring(0, 5) +
                     "01-01";
            	}
            	else{
            		sResult = " where 1=1 and a.FYearMonth like'____00'";
            	}               
            }
            //edit by songjie 2011.05.26 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B
            if (this.filterType.strIsOnlyColumn.equals("1")) {
                sResult = sResult + " and 1 = 2";
                return sResult;
            }

            if (this.filterType.strCashAccCode.length() != 0) {
                sResult = sResult + " and a.FCashAccCode like '" +
                    filterType.strCashAccCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.strPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strAccBalance.length() != 0) {
                sResult = sResult + " and a.FAccBalance like '" +
                    filterType.strAccBalance.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strCuryCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.strCuryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strPortCuryRate.length() != 0) {
                sResult = sResult + " and a.FPortCuryRate = " +
                    dbl.sqlString(filterType.strPortCuryRate);
            }
            if (this.filterType.strPortCuryBal.length() != 0) {
                sResult = sResult + " and a.FPortCuryBal= " +
                    dbl.sqlString(filterType.strPortCuryBal);
            }

            if (this.filterType.strBaseCuryRate.length() != 0) {
                sResult = sResult + " and a.FBaseCuryRate = " +
                    dbl.sqlString(filterType.strBaseCuryRate);
            }

            if (this.filterType.strBaseCuryBal.length() != 0) {
                sResult = sResult + " and a.FBaseCuryBal = " +
                    dbl.sqlString(filterType.strBaseCuryBal);
            }
            //add by yangheng MS01603 QDV4赢时胜(测试)2010年08月12日04_B  复制证券库存出错 2010.09.09
            if (this.filterType.strFAnalysisCode1.trim().length() != 0) {
                sResult = sResult + " and a.FAnalysisCode1 like '" +
                    filterType.strFAnalysisCode1.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strFAnalysisCode2.trim().length() != 0) {
                sResult = sResult + " and a.FAnalysisCode2 like '" +
                    filterType.strFAnalysisCode2.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strFAnalysisCode3.trim().length() != 0 &&
                !this.filterType.strFAnalysisCode3.equals("0")) {
                sResult = sResult + " and a.FAnalysisCode3 like '" +
                    filterType.strFAnalysisCode3.replaceAll("'", "''") + "%'";
            }
          //add by yangheng MS01603 QDV4赢时胜(测试)2010年08月12日04_B  复制证券库存出错 2010.09.09
            if (this.filterType.strStorageDate.length() != 0 &&
                !this.filterType.strStorageDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FStorageDate = " +
                    dbl.sqlDate(filterType.strStorageDate);
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
            dbl.sqlString(YssOperCons.YSS_KCLX_Cash);
        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
          
                    " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                    i +
                    " from " +
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
                        " = e.FExchangeCode " +
                     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            
                        
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " n where  n.FCheckState = 1 ) exchange on a.FAnalysisCode" +
                        i + " = exchange.FInvMgrCode";
                    
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("004")) {
                    sResult = sResult +
                        //edited by zhouxiang MS01504 库存信息配置中设置【现金】类，1）设置配置代码2，在外汇交易数据，点击查询时报错 
                    	" left join (select FCatCode,FCatName as FAnalysisName"+i+" from Tb_Base_Category where FCheckState = 1) category on a.FAnalysisCode" +
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
                }

                else {
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
            //--add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B--//
            if (this.filterType.strIsOnlyColumn.equals("1")) {
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                       this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + 
                       sDateStr + "\r\f";
            }
            //--add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B--//
            
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName,d.FStorageInitDate, g.FCurrencyName, " +
            		 "j.FCashAccName,d.Fportcury as FPortCuryCode ,nvl(k.FAttrClsName,' ') as FAttrClsName"; //MS00177 添加上组合币种
            strSql = strSql +
                (FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 //edit by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B
                 ", nvl(FAnalysisName1, ' ') as FAnalysisName1, nvl(FAnalysisName2, ' ') as FAnalysisName2, nvl(FAnalysisName3, ' ') as FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Stock_Cash") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
       		 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
   
                
                " left join (select FPortCode , FPortName,FStorageInitDate,Fportcury from " + //MS00177 添加上组合币种
                pub.yssGetTableName("Tb_Para_Portfolio") + 
                " where FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
                " ) d on a.FPortCode = d.FPortCode" +
             
                //end by lidaolong
                /*       " left join (select FPortCode, FPortName, max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                       " and FCheckState = 1 group by FPortCode, FPortName) d on a.FPortCode = d.FPortCode " +
                 */
                " left join (select FCuryCode as FCurrencyCode, FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ") g on a.FCuryCode = g.FCurrencyCode" +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") k on a.FAttrClsCode = k.FAttrClsCode " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
        
                
                FilterSql() + " left join (select FCashAccCode, FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where  FCheckState = 1 ) j on a.FCashAccCode = j.FCashAccCode " +
                buildFilterSql() + " and a.fportcode is not null order by a.FCheckState, a.FCreateTime desc";
            
            //end by lidaolong
            //delete by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B 
            //rs = dbl.openResultSet(strSql);
            //---add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B--//
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("CashStorage");
            rs =dbl.openResultSet(yssPageInationBean);
            //---add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B--//
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.strCashAccCode = rs.getString("FCashAccCode") + "";
                this.strCashAccName = rs.getString("FCashAccName") + "";
                this.strStorageDate = rs.getDate("FStorageDate") + "";
                this.strStorageStartDate = rs.getDate("FStorageInitDate") + "";
                this.strAccBalance = rs.getDouble("FAccBalance") + "";
                this.strCuryCode = rs.getString("FCuryCode") + "";
                this.strCuryName = rs.getString("FCurrencyName") + "";
                this.strPortCode = rs.getString("FPortCode") + "";
                this.strPortName = rs.getString("FPortName") + "";
                this.strPortCuryRate = rs.getDouble("FPortCuryRate") + "";
                this.strPortCuryBal = rs.getDouble("FPortCuryBal") + "";

                this.strBaseCuryRate = rs.getDouble("FBaseCuryRate") + "";

                this.strBaseCuryBal = rs.getDouble("FBaseCuryBal") + "";
                //     this.strGainedInterest = rs.getDouble("FGainedInterest") + "";
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

                setResultSetAttr(rs);
                if (pub.getPortBaseCury(this.strPortCode).equalsIgnoreCase(rs.getString("FCuryCode"))) { //MS00177 如果原币等于基础货币，基础货币余额都取原币余额 byleeyu// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                    strBaseCuryBal = strAccBalance;
                }
                if (rs.getString("FPortCuryCode") != null && rs.getString("FPortCuryCode").equalsIgnoreCase(rs.getString("FCuryCode"))) { //MS00177 如果原币等于组合货币，组合货币余额都取原币余额 byleeyu
                    strPortCuryBal = strAccBalance;
                }
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }

            AssetStorageCfgBean assetgroupcfg = new AssetStorageCfgBean();
            assetgroupcfg.setYssPub(pub);
            sDateStr = assetgroupcfg.getPartSetting("Cash");

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
            throw new YssException("获取现金帐户库存信息出错 \r\n" + e.getMessage(), e);
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
     * getOperData
     */
    public void getStorageData() {
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

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strCashAccCode = rs.getString("FCashAccCode") + "";
        this.strStorageDate = rs.getDate("FStorageDate") + "";
        this.strStorageStartDate = rs.getDate("FStorageInitDate") + "";
        this.strAccBalance = rs.getDouble("FAccBalance") + "";
        this.strCuryCode = rs.getString("FCuryCode") + "";
        this.strPortCode = rs.getString("FPortCode") + "";
        this.strPortCuryRate = rs.getDouble("FPortCuryRate") + "";
        this.strPortCuryBal = rs.getDouble("FPortCuryBal") + "";
        this.strBaseCuryRate = rs.getDouble("FBaseCuryRate") + "";
        this.strBaseCuryBal = rs.getDouble("FBaseCuryBal") + "";
        //  this.strGainedInterest = rs.getDouble("FGainedInterest") + "";
        this.intStorageState = rs.getInt("FStorageInd");
        this.strFAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
        this.strFAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
        this.strFAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
        this.strYearMonth = rs.getString("FYearMonth") + "";
        
      //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        if(dbl.isFieldExist(rs, "FATTRCLSCODE")){
        	this.strAttrClsCode = rs.getString("FATTRCLSCODE");
        	this.strAttrClsName = rs.getString("FATTRCLSNAME");
        }else{
        	this.strAttrClsCode = "";
        	this.strAttrClsName = "";
        }
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        
    }

    /**
     * saveMutliOperData
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliStorageData(String sMutilRowStr) throws YssException {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
    	//modify by zhangfa 证券借贷业务-抵押物补交数据 2010-11-3
    	if(sType.equalsIgnoreCase("checkStockCash")){
			return checkStockCash();
		}
    	//-----------end---------------------------------------
        return "";
    }
    //add by zhangfa 证券借贷业务-抵押物补交数据 2010-11-3
    public String checkStockCash() throws YssException
    {
    	String temp="null";
		String strSql = "";
        ResultSet rs = null;
		try{
			strSql=" select FCashAccCode,FAccBalance from "+pub.yssGetTableName("Tb_Stock_Cash") +
			       " where FCashAccCode="+dbl.sqlString(this.strCashAccCode)+" and FStorageDate="+
			        dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.strStorageDate), -1))+
			       " and FPortCode="+dbl.sqlString(this.strPortCode) ;
			rs=dbl.openResultSet(strSql);
			while(rs.next()){
				if(rs.getDouble("FAccBalance")<=0){
					temp="true";
				}else{
					temp="false";
				}
				
			}
			return temp;
		}catch (Exception e) {
			throw new YssException("检查现金库存出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    //---------------------end--------------------------------------
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

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        CashStorageBean befEditBean = new CashStorageBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName,d.FStorageInitDate, " +
            		" g.FCurrencyName, j.FCashAccName ,nvl(k.FAttrClsName,' ') as FAttrClsName"; //MS00177 添加上组合币种
            strSql = strSql +
                (FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Stock_Cash") + " a " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") k on a.FAttrClsCode = k.FAttrClsCode " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            
       		 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    
                " left join (select  FPortCode,FPortName, FStorageInitDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +          
                ") d on a.FPortCode = d.FPortCode" +
             
                //end by lidaolong
                " left join (select FCuryCode as FCurrencyCode, FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ") g on a.FCuryCode = g.FCurrencyCode" +
                
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
           
                FilterSql() + " left join (select FCashAccCode, FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where  FCheckState = 1 ) j on a.FCashAccCode = j.FCashAccCode " +
               
                //end by lidaolong
                " where a.FCashAccCode = " +
                dbl.sqlString(this.strOldCashAccCode) +
                " and a.FPortCode=" + dbl.sqlString(this.strOldPortCode) +
                " and a.FStorageDate=" + dbl.sqlDate(this.strOldStorageDate);
            if (this.bBegin.equalsIgnoreCase("true")) {
                strSql = strSql + " and a.FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strOldStorageDate,
                    "yyyy") +
                                  "00");
            } else {
                strSql = strSql + " and a.FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strOldStorageDate,
                    "yyyyMM"));
            }

            if (!this.strOldFAnalysisCode1.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode1.length() > 0) {
                strSql = strSql + " and a.FAnalysisCode1=" +
                    dbl.sqlString(this.strOldFAnalysisCode1);
            }
            if (!this.strOldFAnalysisCode2.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode2.length() > 0) {
                strSql = strSql + " and a.FAnalysisCode2=" +
                    dbl.sqlString(this.strOldFAnalysisCode2);
            }
            if (!this.strOldFAnalysisCode3.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode3.length() > 0) {
                strSql = strSql + " and a.FAnalysisCode3=" +
                    dbl.sqlString(this.strOldFAnalysisCode3);
            }

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strCashAccCode = rs.getString("FCashAccCode") + "";
                befEditBean.strCashAccName = rs.getString("FCashAccName") + "";
                befEditBean.strStorageDate = rs.getDate("FStorageDate") + "";
                befEditBean.strStorageStartDate = rs.getDate("FStorageInitDate") +
                    "";
                befEditBean.strAccBalance = rs.getDouble("FAccBalance") + "";
                befEditBean.strCuryCode = rs.getString("FCuryCode") + "";
                befEditBean.strCuryName = rs.getString("FCurrencyName") + "";
                befEditBean.strPortCode = rs.getString("FPortCode") + "";
                befEditBean.strPortName = rs.getString("FPortName") + "";
                befEditBean.strPortCuryRate = rs.getDouble("FPortCuryRate") + "";
                befEditBean.strPortCuryBal = rs.getDouble("FPortCuryBal") + "";
                befEditBean.strBaseCuryRate = rs.getDouble("FBaseCuryRate") + "";
                befEditBean.strBaseCuryBal = rs.getDouble("FBaseCuryBal") + "";
                befEditBean.intStorageState = rs.getInt("FStorageInd");
                befEditBean.strFAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
                befEditBean.strFAnalysisName1 = rs.getString("FAnalysisName1") + "";
                befEditBean.strFAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
                befEditBean.strFAnalysisName2 = rs.getString("FAnalysisName2") + "";
                befEditBean.strFAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
                befEditBean.strFAnalysisName3 = rs.getString("FAnalysisName3") + "";

                befEditBean.strYearMonth = rs.getString("FYearMonth") + "";

                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                befEditBean.strAttrClsCode = rs.getString("FAttrClsCode");
                befEditBean.strAttrClsName = rs.getString("FAttrClsName");
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
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
                    strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Cash") +
                        " where FCashAccCode = " +
                        dbl.sqlString(this.strCashAccCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate);
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
                        pub.yssGetTableName("Tb_Stock_CashPayRec") +
                        " where FCashAccCode = " +
                        dbl.sqlString(this.strCashAccCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate);
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
                if (this.strCashAccCode != null &&
                    this.strCashAccCode.trim().length() > 0) {
                    strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Cash") +
                        " where FCashAccCode = " +
                        dbl.sqlString(this.strCashAccCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate);
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
                        pub.yssGetTableName("Tb_Stock_CashPayRec") +
                        " where FCashAccCode = " +
                        dbl.sqlString(this.strCashAccCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate);
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
