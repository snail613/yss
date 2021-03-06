package com.yss.main.storagemanage;

import java.math.*;
import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class SecRecPayBalBean
    extends BaseDataSettingBean implements IDataSetting {
    private String sYearMonth = "";
    private java.util.Date dtStorageDate;
    private String sPortCode = "";
    private String sPortName = "";
    private String sSecurityCode = "";
    private String sSecurityName = "";
    private String sCuryCode = "";
    private String sCuryName = "";
    private String sAnalysisCode1 = "";
    private String sAnalysisName1 = "";
    private String sAnalysisCode2 = "";
    private String sAnalysisName2 = "";
    private String sAnalysisCode3 = "";
    private String sAnalysisName3 = "";
    private String sTsfTypeCode = "";
    private String sTsfTypeName = "";
    private String sSubTsfTypeCode = "";
    private String sSubTsfTypeName = "";
    //add by zhangfa 证券借贷业务需求-证券应收应付界面添加数量参数  2010-11-15
    private double amount;          //数量
    //---------------end--------------------------------------------------

    //=== MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A ===
    private String sInvestType = ""; // 投资类型
    //========== End MS00024 2009-08-15 add by wangzuochun =========

    private int iStorageState = 0;
    private double dBal = 0;
    private double dMBal = 0;
    private double dVBal = 0;
    private double dBaseBal = 0;
    private double dMBaseBal = 0;
    private double dVBaseBal = 0;
    private double dPortBal = 0;
    private double dMPortBal = 0;
    private double dVPortBal = 0;
    //-----------2008.11.13 蒋锦 添加-------------//
    //储存保留8位小数的原币，基础货币，本位币金额
    //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
    private double balF;
    private double baseBalF;
    private double portBalF;
    //-------------------------------------------//
    private String sIsOnlyColumns = "0";

    private String attrClsCode = ""; //所属分类 sj add 20071202
    private String attrClsName = "";
    private String catTypeCode = ""; //品种类型
    private String catTypeName = "";

    private String bBegin; //是否为期初数

    private SecRecPayBalBean filterType;
	// --- QDV4中保2010年1月11日01_B add by jiangshichao 2010.01.14
	// -----------------------------
	private double dBaseRate = 1;
	private double dPortRate = 1;

	public double getDBaseRate() {
		return dBaseRate;
	}

	public void setDBaseRate(double baseRate) {
		this.dBaseRate = baseRate;
	}

	public double getDPortRate() {
		return dPortRate;
	}

	public void setDPortRate(double portRate) {
		this.dPortRate = portRate;
	}

	// --- QDV4中保2010年1月11日01_B end --------------------------------------------------------  
    public void setBBegin(String bBegin) {
        this.bBegin = bBegin;
    }

    public void setSSecurityCode(String sSecurityCode) {
        this.sSecurityCode = sSecurityCode;
    }

    public void setSAnalysisCode3(String sAnalysisCode3) {
        this.sAnalysisCode3 = sAnalysisCode3;
    }

    public void setDMBal(double dMBal) {
        this.dMBal = dMBal;
    }

    public void setSPortName(String sPortName) {
        this.sPortName = sPortName;
    }

    public void setSSecurityName(String sSecurityName) {
        this.sSecurityName = sSecurityName;
    }

    public void setSAnalysisName1(String sAnalysisName1) {
        this.sAnalysisName1 = sAnalysisName1;
    }

    public void setDVBaseBal(double dVBaseBal) {
        this.dVBaseBal = dVBaseBal;
    }

    public void setSTsfTypeName(String sTsfTypeName) {
        this.sTsfTypeName = sTsfTypeName;
    }

    public void setSCuryName(String sCuryName) {
        this.sCuryName = sCuryName;
    }

    public void setDVBal(double dVBal) {
        this.dVBal = dVBal;
    }

    public void setSIsOnlyColumns(String sIsOnlyColumns) {
        this.sIsOnlyColumns = sIsOnlyColumns;
    }

    public void setSCuryCode(String sCuryCode) {
        this.sCuryCode = sCuryCode;
    }

    public void setDMBaseBal(double dMBaseBal) {
        this.dMBaseBal = dMBaseBal;
    }

    public void setSAnalysisName3(String sAnalysisName3) {
        this.sAnalysisName3 = sAnalysisName3;
    }

    public void setDBal(double dBal) {
        this.dBal = dBal;
    }

    public void setIStorageState(int iStorageState) {
        this.iStorageState = iStorageState;
    }

    public void setSSubTsfTypeName(String sSubTsfTypeName) {
        this.sSubTsfTypeName = sSubTsfTypeName;
    }

    public void setSTsfTypeCode(String sTsfTypeCode) {
        this.sTsfTypeCode = sTsfTypeCode;
    }

    public void setDPortBal(double dPortBal) {
        this.dPortBal = dPortBal;
    }

    public void setDMPortBal(double dMPortBal) {
        this.dMPortBal = dMPortBal;
    }

    public void setSAnalysisName2(String sAnalysisName2) {
        this.sAnalysisName2 = sAnalysisName2;
    }

    public void setFilterType(SecRecPayBalBean filterType) {
        this.filterType = filterType;
    }

    public void setDBaseBal(double dBaseBal) {
        this.dBaseBal = dBaseBal;
    }

    public void setDVPortBal(double dVPortBal) {
        this.dVPortBal = dVPortBal;
    }

    public void setSAnalysisCode2(String sAnalysisCode2) {
        this.sAnalysisCode2 = sAnalysisCode2;
    }

    public void setDtStorageDate(java.util.Date dtStorageDate) {
        this.dtStorageDate = dtStorageDate;
    }

    public void setSAnalysisCode1(String sAnalysisCode1) {
        this.sAnalysisCode1 = sAnalysisCode1;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setSSubTsfTypeCode(String sSubTsfTypeCode) {
        this.sSubTsfTypeCode = sSubTsfTypeCode;
    }

    public void setSYearMonth(String sYearMonth) {
        this.sYearMonth = sYearMonth;
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

    public void setBalF(double balF) {
        this.balF = balF;
    }

    public void setBaseBalF(double baseBalF) {
        this.baseBalF = baseBalF;
    }

    public void setPortBalF(double portBalF) {
        this.portBalF = portBalF;
    }

    public void setSInvestType(String sInvestType) {
        this.sInvestType = sInvestType;
    }

    public String getBBegin() {
        return bBegin;
    }

    public String getSSecurityCode() {
        return sSecurityCode;
    }

    public String getSAnalysisCode3() {
        return sAnalysisCode3;
    }

    public double getDMBal() {
        return dMBal;
    }

    public String getSPortName() {
        return sPortName;
    }

    public String getSSecurityName() {
        return sSecurityName;
    }

    public String getSAnalysisName1() {
        return sAnalysisName1;
    }

    public double getDVBaseBal() {
        return dVBaseBal;
    }

    public String getSTsfTypeName() {
        return sTsfTypeName;
    }

    public String getSCuryName() {
        return sCuryName;
    }

    public double getDVBal() {
        return dVBal;
    }

    public String getSIsOnlyColumns() {
        return sIsOnlyColumns;
    }

    public String getSCuryCode() {
        return sCuryCode;
    }

    public double getDMBaseBal() {
        return dMBaseBal;
    }

    public String getSAnalysisName3() {
        return sAnalysisName3;
    }

    public double getDBal() {
        return dBal;
    }

    public int getIStorageState() {
        return iStorageState;
    }

    public String getSSubTsfTypeName() {
        return sSubTsfTypeName;
    }

    public String getSTsfTypeCode() {
        return sTsfTypeCode;
    }

    public double getDPortBal() {
        return dPortBal;
    }

    public double getDMPortBal() {
        return dMPortBal;
    }

    public String getSAnalysisName2() {
        return sAnalysisName2;
    }

    public SecRecPayBalBean getFilterType() {
        return filterType;
    }

    public double getDBaseBal() {
        return dBaseBal;
    }

    public double getDVPortBal() {
        return dVPortBal;
    }

    public String getSAnalysisCode2() {
        return sAnalysisCode2;
    }

    public java.util.Date getDtStorageDate() {
        return dtStorageDate;
    }

    public String getSAnalysisCode1() {
        return sAnalysisCode1;
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public String getSSubTsfTypeCode() {
        return sSubTsfTypeCode;
    }

    public String getSYearMonth() {
        return sYearMonth;
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

    public double getBalF() {
        return balF;
    }

    public double getBaseBalF() {
        return baseBalF;
    }

    public double getPortBalF() {
        return portBalF;
    }

    public String getSInvestType() {
        return sInvestType;
    }

    public SecRecPayBalBean() {
    }

    public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
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
            } else {
                sTmpStr = sRowStr;
            }

            reqAry = sTmpStr.split("\t");
            this.dtStorageDate = YssFun.toDate(reqAry[0]);
            this.sSecurityCode = reqAry[1];
            this.sPortCode = reqAry[2];
            this.sCuryCode = reqAry[3];
            this.sAnalysisCode1 = reqAry[4];
            this.sAnalysisCode2 = reqAry[5];
            this.sAnalysisCode3 = reqAry[6];
            this.sTsfTypeCode = reqAry[7];
            this.sSubTsfTypeCode = reqAry[8];
            this.dBal = YssFun.toDouble(reqAry[9]);
            this.dMBal = YssFun.toDouble(reqAry[10]);
            this.dVBal = YssFun.toDouble(reqAry[11]);
            this.dBaseBal = YssFun.toDouble(reqAry[12]);
            this.dMBaseBal = YssFun.toDouble(reqAry[13]);
            this.dVBaseBal = YssFun.toDouble(reqAry[14]);
            this.dPortBal = YssFun.toDouble(reqAry[15]);
            this.dMPortBal = YssFun.toDouble(reqAry[16]);
            this.dVPortBal = YssFun.toDouble(reqAry[17]);
            this.bBegin = reqAry[18];
            super.checkStateId = Integer.parseInt(reqAry[19]);
            this.catTypeCode = reqAry[20];
            this.attrClsCode = reqAry[21];
            //add by zhangfa 证券借贷业务需求-证券应收应付界面添加数量参数  2010-11-15
            if (YssFun.isNumeric(reqAry[22])) {
            	this.amount=Double.parseDouble(reqAry[22]);
            }
            //-----------------end-------------------------------------------------
            this.sInvestType = reqAry[23];
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SecRecPayBalBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);

            }
        } catch (Exception e) {
            throw new YssException("解析证券库存应收应付请求信息出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.dtStorageDate).append("\t");
        buf.append(this.sSecurityCode.trim()).append("\t");
        buf.append(this.sSecurityName.trim()).append("\t");
        buf.append(this.sPortCode.trim()).append("\t");
        buf.append(this.sPortName.trim()).append("\t");
        buf.append(this.sCuryCode.trim()).append("\t");
        buf.append(this.sCuryName.trim()).append("\t");
        buf.append(this.sAnalysisCode1.trim()).append("\t");
        buf.append(this.sAnalysisName1.trim()).append("\t");
        buf.append(this.sAnalysisCode2.trim()).append("\t");
        buf.append(this.sAnalysisName2.trim()).append("\t");
        buf.append(this.sAnalysisCode3.trim()).append("\t");
        buf.append(this.sAnalysisName3.trim()).append("\t");
        buf.append(this.sTsfTypeCode.trim()).append("\t");
        buf.append(this.sTsfTypeName.trim()).append("\t");
        buf.append(this.sSubTsfTypeCode.trim()).append("\t");
        buf.append(this.sSubTsfTypeName.trim()).append("\t");
        buf.append(this.dBal).append("\t");
        buf.append(this.dMBal).append("\t");
        buf.append(this.dVBal).append("\t");
        buf.append(this.dBaseBal).append("\t");
        buf.append(this.dMBaseBal).append("\t");
        buf.append(this.dVBaseBal).append("\t");
        buf.append(this.dPortBal).append("\t");
        buf.append(this.dMPortBal).append("\t");
        buf.append(this.dVPortBal).append("\t");
        buf.append(this.iStorageState).append("\t");
        buf.append(this.sYearMonth).append("\t");
        buf.append(this.catTypeCode).append("\t");
        buf.append(this.catTypeName).append("\t");
        buf.append(this.attrClsCode).append("\t");
        buf.append(this.attrClsName).append("\t");
      //add by zhangfa 证券借贷业务需求-证券应收应付界面添加数量参数  2010-11-15
        buf.append(this.amount).append("\t");
      //--------------------end-------------------------------  
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    public void checkInput(byte btOper) throws YssException {

    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.sYearMonth = rs.getString("FYearMonth") + "";
        this.dtStorageDate = rs.getDate("FStorageDate");
        this.sPortCode = rs.getString("FPortCode") + "";
        this.sPortName = rs.getString("FPortName") + "";
        this.sSecurityCode = rs.getString("FSecurityCode") + "";
        this.sSecurityName = rs.getString("FSecurityName") + "";
        this.sCuryCode = rs.getString("FCuryCode") + "";
        this.sCuryName = rs.getString("FCuryName") + "";
        this.sAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
        this.sAnalysisName1 = rs.getString("FAnalysisname1") + "";
        this.sAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
        this.sAnalysisName2 = rs.getString("FAnalysisName2") + "";
        this.sAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
        this.sAnalysisName3 = rs.getString("FAnalysisName3") + "";
        this.sTsfTypeCode = rs.getString("FTsfTypeCode") + "";
        this.sTsfTypeName = rs.getString("FTsfTypeName") + "";
        this.sSubTsfTypeCode = rs.getString("FSubTsfTypeCode") + "";
        this.sSubTsfTypeName = rs.getString("FSubTsfTypeName") + "";
        this.dBal = rs.getDouble("FBal");
        this.dMBal = rs.getDouble("FMBal");
        this.dVBal = rs.getDouble("FVBal");
        this.dBaseBal = rs.getDouble("FBaseCuryBal");
        this.dMBaseBal = rs.getDouble("FMBaseCuryBal");
        this.dVBaseBal = rs.getDouble("FVBaseCuryBal");
        this.dPortBal = rs.getDouble("FPortCuryBal");
        this.dMPortBal = rs.getDouble("FMPortCuryBal");
        this.dVPortBal = rs.getDouble("FVPortCuryBal");
        this.attrClsCode = rs.getString("FAttrClsCode");
        this.attrClsName = rs.getString("FAttrClsName");
        //add by zhangfa 证券借贷业务需求-证券应收应付界面添加数量参数  2010-11-15
        this.amount=rs.getDouble("FAmount");
        	
        //---------------------------end--------------------------------------
        super.setRecLog(rs);

    }

    public String addStorageData() throws YssException {
        return "";
    }

    public String editStorageData() throws YssException {
        return "";
    }

    public void delStorageData() throws YssException {

    }

    public void checkStorageData() throws YssException {

    }

    public String saveMutliStorageData(String sMutilRowStr) throws YssException {
        return saveMutliStorageData(sMutilRowStr, false, null);
    }

    public String saveMutliStorageData(String sMutilRowStr, boolean bIsTrans,
                                       SecurityStorageBean securityBean) throws
        YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String strYearMonth = "";
        try {
            if (!bIsTrans) {
                conn.setAutoCommit(false);
                bTrans = true;
            }

            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);

            if (securityBean.getBBegin().equalsIgnoreCase("true")) {
                this.sYearMonth = YssFun.formatDate(securityBean.getStrStorageDate(),
                    "yyyy") + "00";
            } else {
                this.sYearMonth = YssFun.formatDate(securityBean.getStrStorageDate(),
                    "yyyyMM");
            }

            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " where FYearMonth = " + dbl.sqlString(this.sYearMonth) +
                " and FStorageDate=" +
                dbl.sqlDate(securityBean.getStrStorageDate()) +
                " and FPortCode=" + dbl.sqlString(securityBean.getStrPortCode()) +
                " and FSecurityCode=" +
                dbl.sqlString(securityBean.getStrSecurityCode());
                //-----------------------------------------------------------------------------------------------------------------
            if (securityBean.getStrFAnalysisCode1() != null &&
                securityBean.getStrFAnalysisCode1().trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1=" +
                    dbl.sqlString(securityBean.getStrFAnalysisCode1());
            }
            if (securityBean.getStrFAnalysisCode2() != null &&
                securityBean.getStrFAnalysisCode2().trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2=" +
                    dbl.sqlString(securityBean.getStrFAnalysisCode2());
            }
            
          //add by zhangfa 20100914 MS01729    所属分类不同的两笔证券库存数据，应收应付数据操作有误    QDV4赢时胜(上海开发部)2010年09月11日02_B      
            if(securityBean.getAttrCode()!=null){
            	 strSql = strSql +" and FAttrClsCode="+
                 dbl.sqlString(securityBean.getAttrCode());
            }
          //------------------------------------------------------------------------------------------------------------------  
//         if (securityBean.getStrFAnalysisCode3() != null &&
//             securityBean.getStrFAnalysisCode3().trim().length() > 0) {
//            strSql = strSql + " and FAnalysisCode3=" +
//                  dbl.sqlString(securityBean.getStrFAnalysisCode3());
//         }
            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                "(FYearMonth, FStorageDate, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FSecurityCode," +
                "FTsfTypeCode, FSubTsfTypeCode, FCuryCode, FBal, FMBal, FVBal, FBaseCuryBal, FMBaseCuryBal, FVBaseCuryBal, " +
                " FPortCuryBal, FMPortCuryBal, FVPortCuryBal, FStorageInd, FCatType, FAttrClsCode," +
                //---edit by songjie 2011.06.25 BUG 2150 QDV4中国银行2011年06月22日01_B---//
				" FCheckState, FCreator, FCreateTime, FCheckUser,FCheckTime,FInvestType) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				//---edit by songjie 2011.06.25 BUG 2150 QDV4中国银行2011年06月22日01_B---//
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                this.parseRowStr(sMutilRowAry[i]);
                /*    if (this.bBegin.equalsIgnoreCase("true")) {
                 this.sYearMonth = YssFun.formatDate(this.dtStorageDate, "yyyy") +
                             "00";
                    }
                    else {
                 this.sYearMonth = YssFun.formatDate(this.dtStorageDate, "yyyyMM");
                    }*/

                pstmt.setString(1, this.sYearMonth);
                pstmt.setDate(2, YssFun.toSqlDate(this.dtStorageDate));
                pstmt.setString(3, this.sPortCode);
                pstmt.setString(4,
                                this.sAnalysisCode1.trim().length() != 0 ?
                                this.sAnalysisCode1 :
                                " ");
                pstmt.setString(5,
                                this.sAnalysisCode2.trim().length() != 0 ?
                                this.sAnalysisCode2 :
                                " ");
                pstmt.setString(6,
                                this.sAnalysisCode3.trim().length() != 0 ?
                                this.sAnalysisCode3 :
                                " ");
                pstmt.setString(7, this.sSecurityCode);
                pstmt.setString(8, this.sTsfTypeCode);
                pstmt.setString(9, this.sSubTsfTypeCode);
                pstmt.setString(10, this.sCuryCode);
                pstmt.setDouble(11, this.dBal);
                pstmt.setDouble(12, this.dMBal);
                pstmt.setDouble(13, this.dVBal);
                pstmt.setDouble(14, this.dBaseBal);
                pstmt.setDouble(15, this.dMBaseBal);
                pstmt.setDouble(16, this.dVBaseBal);
                pstmt.setDouble(17, this.dPortBal);
                pstmt.setDouble(18, this.dMPortBal);
                pstmt.setDouble(19, this.dVPortBal);
                pstmt.setInt(20, this.bBegin.equalsIgnoreCase("false") ? 0 : 2);
                pstmt.setString(21, this.catTypeCode.length() > 0 ? this.catTypeCode : " ");
                pstmt.setString(22, this.attrClsCode.length() > 0 ? this.attrClsCode : " ");
                pstmt.setInt(23, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(24, this.creatorCode);
                pstmt.setString(25, this.creatorTime);
                pstmt.setString(26,
                                (pub.getSysCheckState() ? " " : this.creatorCode));
                pstmt.setString(27,
                                (pub.getSysCheckState() ? " " : this.creatorTime));
			    //add by songjie 2011.06.25 BUG 2150 QDV4中国银行2011年06月22日01_B
                pstmt.setString(28, this.sInvestType);
                pstmt.executeUpdate();

            }
            if (!bIsTrans) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            throw new YssException("保存证券库存应收应付信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            return "";
        }

    }

    /**
     * getOperData ：
     * 获取一条设置信息
     * @return IBaseOperData
     */
    public void getStorageData() throws YssException {

    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {

            if (this.filterType.bBegin.equalsIgnoreCase("false")) {
                sResult = " where 1=1 and " + dbl.sqlRight("FYearMonth", 2) +
                    " <> '00'";
                //=====by xuqiji 20090506 :QDV4中保2009年05月04日01_B  MS00430=======
                //因为期初数是无需日期作为筛选条件，因此提到此处作为筛选条件
                if (this.filterType.dtStorageDate != null &&
                    YssFun.dateDiff(this.filterType.dtStorageDate,
                                    YssFun.toDate("9998-12-31")) != 0) {
                    sResult = sResult + " and a.FStorageDate = " +
                        dbl.sqlDate(filterType.dtStorageDate);
                }
                //==========================End MS00430============================
            } else if (this.filterType.bBegin.equalsIgnoreCase("true")) {
                sResult = " where 1=1 and a.FYearMonth = '" +
                    YssFun.formatDate(this.filterType.dtStorageDate, "yyyy") +
                    "00'";
                /*====期初数不仅只为1月1日，因此此段日期筛选条件屏蔽=====================
                 *====modify by xuqiji 20090506 :QDV4中保2009年05月04日01_B  MS00430
                       this.filterType.dtStorageDate =
                             YssFun.parseDate(YssFun.formatDate(this.filterType.
                             dtStorageDate,"yyyy-MM-dd"));
                      =============================End MS00430==========================*/

            }

            if (this.filterType.sSecurityCode.length() != 0) {
                //sResult = sResult + " and a.FSecurityCode like '" +
                    //filterType.sSecurityCode.replaceAll("'", "''") + "%'";
            	//这里应更改为等于，是按证券成本库存条件来加载，不能用like条件 by leeyu 20100813 合并太平版本调整
            	sResult = sResult + " and a.FSecurityCode = '" +
                	filterType.sSecurityCode.replaceAll("'", "''") + "'";
            	//这里应更改为等于，是按证券成本库存条件来加载，不能用like条件 by leeyu 20100813 合并太平版本调整
            }
            if (this.filterType.sPortCode.length() != 0) {
                //sResult = sResult + " and a.FPortCode like '" +
                //    filterType.sPortCode.replaceAll("'", "''") + "%'";
              //这里应更改为等于，是按证券成本库存条件来加载，不能用like条件 by leeyu 20100813 合并太平版本调整
                sResult = sResult + " and a.FPortCode = '" +
                	filterType.sPortCode.replaceAll("'", "''") + "'";
              //这里应更改为等于，是按证券成本库存条件来加载，不能用like条件 by leeyu 20100813 合并太平版本调整
            }

            if (this.filterType.sCuryCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.sCuryCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.sAnalysisCode1.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode1 like '" +
                    filterType.sAnalysisCode1.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sAnalysisCode2.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode2 like '" +
                    filterType.sAnalysisCode2.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sAnalysisCode3.length() != 0 &&
                !this.filterType.sAnalysisCode3.equals("0")) {
                sResult = sResult + " and a.FAnalysisCode3 like '" +
                    filterType.sAnalysisCode3.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sTsfTypeCode.length() != 0) {
                sResult = sResult + " and a.FTsfTypeCode like '" +
                    filterType.sTsfTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sSubTsfTypeCode.length() != 0) {
                sResult = sResult + " and a.FSubTsfTypeCode like '" +
                    filterType.sSubTsfTypeCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.catTypeCode.length() != 0) {
                sResult = sResult + " and a.FCatType like '" +
                    filterType.catTypeCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.attrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.attrClsCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sInvestType.length() != 0) { //panjunfang add 20110514 库存区分投资类型
                sResult = sResult + " and a.FINVESTTYPE like '" +
                    filterType.sInvestType.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1 ：
     * 获取选择listView控件的数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        StringBuffer buf1 = new StringBuffer();

        try {
            sHeader = "业务类型\t业务子类型\t原币核算余额\t原币管理余额\t原币估值余额\t基础货币核算余额\t基础货币管理余额\t基础货币估值余额\t组合货币核算余额\t组合货币管理余额\t组合货币估值余额\t数量";
            if (sIsOnlyColumns.trim().equals("0")) {
                SecurityStorageBean secstorage = new SecurityStorageBean();
                secstorage.setYssPub(pub);
                String secanalysis = secstorage.storageAnalysis().trim();

                strSql = "select a.*, d1.FTsfTypeName, e1.FSubTsfTypeName,g.FCuryName, b.fusername as fcreatorname, c.fusername as fcheckusername, d.FPortName, e.FSecurityName ,j.FAttrClsName as FAttrClsName ";
                strSql = strSql +
                    ( (secanalysis.trim().length() == 0) ?
                     ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                     ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ");
                strSql = strSql + " from " +
                    pub.yssGetTableName("Tb_Stock_SecRecPay") + " a" +
                    " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                    " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                    //-----------------------------------------------------------------------------------------------
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                    
            
                    " left join (select FPortCode , FPortName from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +                   
                    " where  FCheckState = 1 and FASSETGROUPCODE = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +                 
                    " ) d on a.FPortCode = d.FPortCode" +
                  
                    
                    //end by lidaolong
                    //-------------------------------------------------------------------------------------------------
                    " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Security") +
                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Security") +
                    ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
                    //-----------------------------------------------------------------------------------------------
                    " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType where FCheckState = 1) d1 " +
                    " on a.FTsfTypeCode = d1.FTsfTypeCode " +
                    //---------------------------------------------------------------------------------
                    " left join (select FSubTsfTypeCode,FSubTsfTypeName  from " +
                    " Tb_Base_SubTransferType where FCheckState = 1) e1 on a.FSubTsfTypeCode = e1.FSubTsfTypeCode " +
                    //---------------------------------------------------------------------------------
                    " left join (select FCuryCode,FCuryName from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode " +
                    //----------------------------------------------------------------------------------
                    " left join (select FAttrClsCode,FAttrClsName from " + pub.yssGetTableName("Tb_Para_AttributeClass") + ") j on a.FAttrClsCode = j.FAttrClsCode " +
                    secanalysis + buildFilterSql() +
                    " order by a.FStorageDate desc ";

                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    buf.append( (rs.getString("FTsfTypeName") + "").trim());
                    buf.append("\t");
                    buf.append( (rs.getString("FSubTsfTypeName") + "").trim());
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FBal"), "#,##0.##"));
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FMBal"), "#,##0.##"));
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FVBal"), "#,##0.##"));
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FBaseCuryBal"),
                        "#,##0.##"));
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FMBaseCuryBal"),
                        "#,##0.##"));
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FVBaseCuryBal"),
                        "#,##0.##"));
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FPortCuryBal"),
                        "#,##0.##"));
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FMPortCuryBal"),
                        "#,##0.##"));
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FVPortCuryBal"),
                        "#,##0.##"));
                    buf.append("\t");
                  
                    buf.append(YssFun.formatNumber(rs.getDouble("FAmount"), "#,###"));
                   
                    buf.append(YssCons.YSS_LINESPLITMARK);

                    setResultSetAttr(rs);

                    buf1.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取证券应收应付信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
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
     * delSetting
     */
    public void delSetting() {
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
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
     * 获取证券应收应付的单个实例数据
     * 20090828. implement by xuxuming MS00473:QDV4国泰2009年6月01日01_A
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Stock_SecRecPay") + " a "
                + buildFilterSql();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.sYearMonth = rs.getString("FYearMonth") + "";
                this.dtStorageDate = rs.getDate("FStorageDate");
                this.sPortCode = rs.getString("FPortCode") + "";
                this.sSecurityCode = rs.getString("FSecurityCode") + "";
                this.sCuryCode = rs.getString("FCuryCode") + "";
                this.sAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
                this.sAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
                this.sAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
                this.sTsfTypeCode = rs.getString("FTsfTypeCode") + "";
                this.sSubTsfTypeCode = rs.getString("FSubTsfTypeCode") + "";
                this.dBal = rs.getDouble("FBal");
                this.dMBal = rs.getDouble("FMBal");
                this.dVBal = rs.getDouble("FVBal");
                this.dBaseBal = rs.getDouble("FBaseCuryBal");
                this.dMBaseBal = rs.getDouble("FMBaseCuryBal");
                this.dVBaseBal = rs.getDouble("FVBaseCuryBal");
                this.dPortBal = rs.getDouble("FPortCuryBal");
                this.dMPortBal = rs.getDouble("FMPortCuryBal");
                this.dVPortBal = rs.getDouble("FVPortCuryBal");
                this.attrClsCode = rs.getString("FAttrClsCode");
                this.checkStateId = rs.getInt("FCheckState");
                this.creatorCode = rs.getString("FCreator");
                this.creatorTime = rs.getString("FCreateTime");
                this.checkUserCode = rs.getString("FCheckUser");
                this.checkTime = rs.getString("FCheckTime");
                //edited  by zhouxiang MS01479    在业务平台-指数信息调整界面点击手工调整分页报错，报错内容为“调用的目标发生了异常 
                this.sInvestType = rs.getString("FInvestType"); 
                //end---  by zhouxiang MS01479    在业务平台-指数信息调整界面点击手工调整分页报错，报错内容为“调用的目标发生了异常 
            }
        } catch (Exception e) {
            throw new YssException("获取证券应收应付库存信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return this;
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
    public String getBeforeEditData() {
        return "";
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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
