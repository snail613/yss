package com.yss.main.storagemanage;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class CashRecPayBalBean
    extends BaseDataSettingBean implements IDataSetting {
    private String sYearMonth = "";
    private java.util.Date dtStorageDate;
    private String sPortCode = "";
    private String sPortName = "";
    private String sCashAccCode = "";
    private String sCashAccName = "";
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
    private int iStorageState = 0;
    private double dBal = 0;
    private double dBaseBal = 0;
    private double dPortBal = 0;
    private String sIsOnlyColumns = "0";
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    private String sAttrClsCode = ""; 
    private String sAttrClsName = "";
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    
    private String bBegin; //是否为期初数

	private CashRecPayBalBean filterType;
	// --- QDV4中保2010年1月11日01_B add by jiangshichao 2010.01.14
	// -----------------------------
	private double dBaseRate = 1;
	private double dPortRate = 1;
	private String sIsDif = " "; // 444 QDV4汇添富2010年12月21日01_A 20110121 通过描述标识尾差数据

	public double getDBaseRate() {
		return dBaseRate;
	}

	public void setDBaseRate(double baseRate) {
		dBaseRate = baseRate;
	}

	public double getDPortRate() {
		return dPortRate;
	}

	public void setDPortRate(double portRate) {
		dPortRate = portRate;
	}

	// --- QDV4中保2010年1月11日01_B end --------------------------------------------------------  
    public void setBBegin(String bBegin) {
        this.bBegin = bBegin;
    }

    public void setSAnalysisCode3(String sAnalysisCode3) {
        this.sAnalysisCode3 = sAnalysisCode3;
    }

    public void setSCuryCode(String sCuryCode) {
        this.sCuryCode = sCuryCode;
    }

    public void setSAnalysisName2(String sAnalysisName2) {
        this.sAnalysisName2 = sAnalysisName2;
    }

    public void setSAnalysisCode2(String sAnalysisCode2) {
        this.sAnalysisCode2 = sAnalysisCode2;
    }

    public void setDtStorageDate(java.util.Date dtStorageDate) {
        this.dtStorageDate = dtStorageDate;
    }

    public void setSCashAccCode(String sCashAccCode) {
        this.sCashAccCode = sCashAccCode;
    }

    public void setSAnalysisCode1(String sAnalysisCode1) {
        this.sAnalysisCode1 = sAnalysisCode1;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setDBaseBal(double dBaseBal) {
        this.dBaseBal = dBaseBal;
    }

    public void setSCashAccName(String sCashAccName) {
        this.sCashAccName = sCashAccName;
    }

    public void setSSubTsfTypeCode(String sSubTsfTypeCode) {
        this.sSubTsfTypeCode = sSubTsfTypeCode;
    }

    public void setSYearMonth(String sYearMonth) {
        this.sYearMonth = sYearMonth;
    }

    public void setSTsfTypeCode(String sTsfTypeCode) {
        this.sTsfTypeCode = sTsfTypeCode;
    }

    public void setFilterType(CashRecPayBalBean filterType) {
        this.filterType = filterType;
    }

    public void setDBal(double dBal) {
        this.dBal = dBal;
    }

    public void setDPortBal(double dPortBal) {
        this.dPortBal = dPortBal;
    }

    public String getBBegin() {
        return bBegin;
    }

    public String getSAnalysisCode3() {
        return sAnalysisCode3;
    }

    public String getSCuryCode() {
        return sCuryCode;
    }

    public String getSAnalysisName2() {
        return sAnalysisName2;
    }

    public String getSAnalysisCode2() {
        return sAnalysisCode2;
    }

    public java.util.Date getDtStorageDate() {
        return dtStorageDate;
    }

    public String getSCashAccCode() {
        return sCashAccCode;
    }

    public String getSAnalysisCode1() {
        return sAnalysisCode1;
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public double getDBaseBal() {
        return dBaseBal;
    }

    public String getSCashAccName() {
        return sCashAccName;
    }

    public String getSSubTsfTypeCode() {
        return sSubTsfTypeCode;
    }

    public String getSYearMonth() {
        return sYearMonth;
    }

    public String getSTsfTypeCode() {
        return sTsfTypeCode;
    }

    public CashRecPayBalBean getFilterType() {
        return filterType;
    }

    public double getDBal() {
        return dBal;
    }

    public double getDPortBal() {
        return dPortBal;
    }

  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    public void setAttrClsCode(String attrClsCode) {
        this.sAttrClsCode = attrClsCode;
    }
    
    public String getAttrClsCode() {
        return sAttrClsCode;
    }
    
    public void setAttrClsName(String attrClsName) {
        this.sAttrClsName = attrClsName;
    }
    
    public String getAttrClsName() {
        return sAttrClsName;
    }
  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//

    public String getsIsDif() {
		return sIsDif;
	}

	public void setsIsDif(String sIsDif) {
		this.sIsDif = sIsDif;
	}
    
    public CashRecPayBalBean() {
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
            this.sCashAccCode = reqAry[1];
            this.sPortCode = reqAry[2];
            this.sCuryCode = reqAry[3];
            this.sAnalysisCode1 = reqAry[4];
            this.sAnalysisCode2 = reqAry[5];
            this.sAnalysisCode3 = reqAry[6];
            this.sTsfTypeCode = reqAry[7];
            this.sSubTsfTypeCode = reqAry[8];
            this.dBal = YssFun.toDouble(reqAry[9]);
            this.dBaseBal = YssFun.toDouble(reqAry[10]);
            this.dPortBal = YssFun.toDouble(reqAry[11]);
            this.bBegin = reqAry[12];
            super.checkStateId = Integer.parseInt(reqAry[13]);
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            this.sAttrClsCode = reqAry[14].trim().length() ==0 ? " ":reqAry[14];
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
           
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {

                if (this.filterType == null) {
                    this.filterType = new CashRecPayBalBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);

            }
        } catch (Exception e) {
            throw new YssException("解析现金库存应收应付请求信息出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.dtStorageDate).append("\t");
        buf.append(this.sCashAccCode.trim()).append("\t");
        buf.append(this.sCashAccName.trim()).append("\t");
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
        buf.append(this.dBaseBal).append("\t");
        buf.append(this.dPortBal).append("\t");
        buf.append(this.iStorageState).append("\t");
        buf.append(this.sYearMonth).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        buf.append(this.sAttrClsCode.trim()).append("\t");
        buf.append(this.sAttrClsName.trim()).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        
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
        this.sCashAccCode = rs.getString("FCashAccCode") + "";
        this.sCashAccName = rs.getString("FCashAccName") + "";
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
        this.dBaseBal = rs.getDouble("FBaseCuryBal");
        this.dPortBal = rs.getDouble("FPortCuryBal");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        this.sAttrClsCode = rs.getString("FAttrClsCode");
        this.sAttrClsName = rs.getString("FAttrClsName");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
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
                                       CashStorageBean cashBean) throws
        YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        //     String strYearMonth = "";
        try {
            if (!bIsTrans) {
                conn.setAutoCommit(false);
                bTrans = true;
            }

            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);

            if (cashBean.getBBegin().equalsIgnoreCase("true")) {
                this.sYearMonth = YssFun.formatDate(cashBean.getStrStorageDate(), "yyyy") +
                    "00";
            } else {
                this.sYearMonth = YssFun.formatDate(cashBean.getStrStorageDate(), "yyyyMM");
            }

            /*       if (this.bBegin.equalsIgnoreCase("true")) {
             strYearMonth = YssFun.formatDate(cashBean.getStrStorageDate(),
                                                       "yyyy") + "00";
                   }
                   else {
             strYearMonth = YssFun.formatDate(cashBean.getStrStorageDate(),
                                                       "yyyyMM");
                   }*/

            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " where FYearMonth = " + dbl.sqlString(this.sYearMonth) +
                " and FStorageDate=" +
                dbl.sqlDate(cashBean.getStrStorageDate()) + " and FPortCode=" +
                dbl.sqlString(cashBean.getStrPortCode()) +
                " and FCashAccCode=" + dbl.sqlString(cashBean.getStrCashAccCode());
            if (cashBean.getStrFAnalysisCode1() != null &&
                cashBean.getStrFAnalysisCode1().trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1=" +
                    dbl.sqlString(cashBean.getStrFAnalysisCode1());
            }
            if (cashBean.getStrFAnalysisCode2() != null &&
                cashBean.getStrFAnalysisCode2().trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2=" +
                    dbl.sqlString(cashBean.getStrFAnalysisCode2());
            }
            if (cashBean.getStrFAnalysisCode3() != null &&
                cashBean.getStrFAnalysisCode3().trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode3=" +
                    dbl.sqlString(cashBean.getStrFAnalysisCode3());
            }
            
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            if(cashBean.getStrAttrClsCode() !=null && cashBean.getStrAttrClsCode().trim().length()!=0){
            	 strSql = strSql + " and FAttrClsCode=" +
                 dbl.sqlString(cashBean.getStrAttrClsCode());
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                "(FYearMonth, FStorageDate, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FCashAccCode," +
                "FTsfTypeCode, FSubTsfTypeCode, FCuryCode, FBal, FBaseCuryBal, FPortCuryBal, FStorageInd, FCheckState, " +
                "FCreator, FCreateTime, FCheckUser,FCheckTime,FAttrClsCode) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                this.parseRowStr(sMutilRowAry[i]);
                /*         if (this.bBegin.equalsIgnoreCase("true")) {
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
                pstmt.setString(7, this.sCashAccCode);
                pstmt.setString(8, this.sTsfTypeCode);
                pstmt.setString(9, this.sSubTsfTypeCode);
                pstmt.setString(10, this.sCuryCode);
                pstmt.setDouble(11, this.dBal);
                pstmt.setDouble(12, this.dBaseBal);
                pstmt.setDouble(13, this.dPortBal);
                pstmt.setInt(14, this.bBegin.equalsIgnoreCase("false") ? 0 : 2);
                pstmt.setInt(15, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(16, this.creatorCode);
                pstmt.setString(17, this.creatorTime);
                pstmt.setString(18,
                                (pub.getSysCheckState() ? " " : this.creatorCode));
                pstmt.setString(19,
                                (pub.getSysCheckState() ? " " : this.creatorTime));
				//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                pstmt.setString(20,this.sAttrClsCode.trim().length()==0?" ":this.sAttrClsCode);
				//--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                pstmt.executeUpdate();

            }
            if (!bIsTrans) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return "";
        } catch (Exception e) {
            throw new YssException("保存现金库存应收应付信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
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
            if (this.filterType.bBegin.equalsIgnoreCase("false")) { // wdy 添加别名a
                sResult = " where 1=1 and " + dbl.sqlRight("FYearMonth", 2) +
                    " <> '00'";
            } else if (this.filterType.bBegin.equalsIgnoreCase("true")) {
                sResult = " where 1=1 and a.FYearMonth = '" +
                    YssFun.formatDate(this.filterType.dtStorageDate, "yyyy") +
                    "00'";
                this.filterType.dtStorageDate =
                    YssFun.parseDate(YssFun.formatDate(this.filterType.
                    dtStorageDate, "yyyy") + "-01-01");
            }
            if (this.filterType.sCashAccCode.length() != 0) {
                sResult = sResult + " and a.FCashAccCode = '" +
                    filterType.sCashAccCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.sPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode = '" +
                    filterType.sPortCode.replaceAll("'", "''") + "'";
            }

            if (this.filterType.sCuryCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode = '" +
                    filterType.sCuryCode.replaceAll("'", "''") + "'";
            }

            if (this.filterType.sAnalysisCode1.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode1 = '" +
                    filterType.sAnalysisCode1.replaceAll("'", "''") + "'";
            }
            if (this.filterType.sAnalysisCode2.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode2 = '" +
                    filterType.sAnalysisCode2.replaceAll("'", "''") + "'";
            }
            if (this.filterType.sAnalysisCode3.length() != 0 &&
                !this.filterType.sAnalysisCode3.equals("0")) {
                sResult = sResult + " and a.FAnalysisCode3 = '" +
                    filterType.sAnalysisCode3.replaceAll("'", "''") + "'";
            }
            if (this.filterType.sTsfTypeCode.length() != 0) {
                sResult = sResult + " and a.FTsfTypeCode = '" +
                    filterType.sTsfTypeCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.sSubTsfTypeCode.length() != 0) {
                sResult = sResult + " and a.FSubTsfTypeCode = '" +
                    filterType.sSubTsfTypeCode.replaceAll("'", "''") + "'";
            }

            if (this.filterType.dtStorageDate != null &&
                YssFun.dateDiff(this.filterType.dtStorageDate,
                                YssFun.toDate("9998-12-31")) != 0) {
                sResult = sResult + " and a.FStorageDate = " +
                    dbl.sqlDate(filterType.dtStorageDate);
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            if (this.filterType.sAttrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.sAttrClsCode.replaceAll("'", "''") + "%'";
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            
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
            sHeader = "业务类型\t业务子类型\t原币余额\t基础货币余额\t组合货币余额";
            if (sIsOnlyColumns.trim().equals("0")) {
                CashStorageBean cashstorage = new CashStorageBean();
                cashstorage.setYssPub(pub);
                String cashanalysis = cashstorage.FilterSql().trim();

                strSql = "select a.*, d1.FTsfTypeName, e1.FSubTsfTypeName,g.FCuryName, b.fusername as fcreatorname, " +
                		 " c.fusername as fcheckusername, d.FPortName, e.FCashAccName,nvl(j.FAttrClsName,' ') as FAttrClsName  ";//add by jiangshichao 2010.11.22 NO.125 用户需要对组合按资本类别进行子组合的分类
                strSql = strSql +
                    ( (cashanalysis.trim().length() == 0) ?
                     ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                     ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ");
                strSql = strSql + " from " +
                    pub.yssGetTableName("Tb_Stock_CashPayRec") + " a" +
                    " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                    " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                    //-----------------------------------------------------------------------------------------------
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
          
                    
                    " left join (select  FPortCode, FPortName from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") + 
                    " where  FCheckState = 1 and FASSETGROUPCODE = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +                   
                    " ) d on a.FPortCode = d.FPortCode" +
                  
                    //end by lidaolong
                    //-------------------------------------------------------------------------------------------------
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
           
                    " left join (select FCashAccCode, FCashAccName from " +
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where  FCheckState = 1) e on a.FCashAccCode = e.FCashAccCode " +
                  
                    //end by lidaolong
                    //-----------------------------------------------------------------------------------------------
                    " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType where FCheckState = 1) d1 " +
                    " on a.FTsfTypeCode = d1.FTsfTypeCode " +
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    " left join (select FAttrClsCode,FAttrClsName from "+pub.yssGetTableName("Tb_Para_AttributeClass")+" ) j on a.FAttrClsCode = j.FAttrClsCode "+
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//   
                    
                    " left join (select FSubTsfTypeCode,FSubTsfTypeName  from " +
                    " Tb_Base_SubTransferType where FCheckState = 1) e1 on a.FSubTsfTypeCode = e1.FSubTsfTypeCode " +
                    //---------------------------------------------------------------------------------
                    " left join (select FCuryCode,FCuryName from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode " +
                    //----------------------------------------------------------------------------------
                    cashanalysis + buildFilterSql() +
                    " order by a.FStorageDate desc ";

                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    buf.append( (rs.getString("FTsfTypeName") + "").trim());
                    buf.append("\t");
                    buf.append( (rs.getString("FSubTsfTypeName") + "").trim());
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FBal"), "#,##0.##"));
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FBaseCuryBal"),
                        "#,##0.##"));
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FPortCuryBal"),
                        "#,##0.##"));
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
            throw new YssException("获取现金应收应付信息出错", e);
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
