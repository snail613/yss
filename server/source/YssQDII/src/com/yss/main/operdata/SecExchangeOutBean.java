package com.yss.main.operdata;

import java.sql.*;
import java.util.Date;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

public class SecExchangeOutBean
    extends BaseDataSettingBean implements IDataSetting {
    private String num = "";
    private String fInSecNum = ""; //证券流入编号 > SecExchange's FNum
    private String securityCode = "";
    private String securityName = "";
    private String portCode = "";
    private String portName = "";
    private String analysisCode1 = "";
    private String analysisCode2 = "";
    private String analysisCode3 = "";

    private String analysisName1 = "";
    private String analysisName2 = "";
    private String analysisName3 = "";

    private double exchangeAmount; //兑换数量
    private double exchangeCost; //核算成本
    private double mExchangeCost; //管理成本
    private double vExchangeCost; //估值成本
    private double portExchangeCost; //组合货币核算成本
    private double mPortExchangeCost; //组合货币管理成本
    private double vPortExchangeCost; //组合货币估值成本
    private double baseExchangeCost;
    private double mBaseExchangeCost;
    private double vBaseExchangeCost;

    private String oldNum = "";
    private String oldFInSecNum = "";

    private String attrClsCode = ""; //所属分类 sj add 20071202
    private String attrClsName = "";
    private String catTypeCode = ""; //品种类型
    private String catTypeName = "";

    private java.util.Date exchangeDate; //为了获取证券应收应付库存而需要的日期 sj 20071123
    //---add by songjie 2012.01.12 BUG 3376 QDV4赢时胜（测试）2011年12月12日03_B start---//
    private String desc = "";
    
    public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
    //---add by songjie 2012.01.12 BUG 3376 QDV4赢时胜（测试）2011年12月12日03_B end---//
	
	private SecExchangeOutBean filterType;
    public double getVPortExchangeCost() {
        return vPortExchangeCost;
    }

    public double getMBaseExchangeCost() {
        return mBaseExchangeCost;
    }

    public String getPortCode() {
        return portCode;
    }

    public double getExchangeAmount() {
        return exchangeAmount;
    }

    public String getAnalysisName3() {
        return analysisName3;
    }

    public SecExchangeOutBean getFilterType() {
        return filterType;
    }

    public double getPortExchangeCost() {
        return portExchangeCost;
    }

    public double getMPortExchangeCost() {
        return mPortExchangeCost;
    }

    public double getExchangeCost() {
        return exchangeCost;
    }

    public String getAnalysisCode3() {
        return analysisCode3;
    }

    public double getVBaseExchangeCost() {
        return vBaseExchangeCost;
    }

    public String getOldNum() {
        return oldNum;
    }

    public double getMExchangeCost() {
        return mExchangeCost;
    }

    public String getNum() {
        return num;
    }

    public String getAnalysisName1() {
        return analysisName1;
    }

    public double getBaseExchangeCost() {
        return baseExchangeCost;
    }

    public String getAnalysisCode2() {
        return analysisCode2;
    }

    public String getAnalysisCode1() {
        return analysisCode1;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public double getVExchangeCost() {
        return vExchangeCost;
    }

    public String getOldFInSecNum() {
        return oldFInSecNum;
    }

    public String getAnalysisName2() {
        return analysisName2;
    }

    public void setFInSecNum(String fInSecNum) {
        this.fInSecNum = fInSecNum;
    }

    public void setVPortExchangeCost(double vPortExchangeCost) {
        this.vPortExchangeCost = vPortExchangeCost;
    }

    public void setMBaseExchangeCost(double mBaseExchangeCost) {
        this.mBaseExchangeCost = mBaseExchangeCost;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setExchangeAmount(double exchangeAmount) {
        this.exchangeAmount = exchangeAmount;
    }

    public void setAnalysisName3(String analysisName3) {
        this.analysisName3 = analysisName3;
    }

    public void setFilterType(SecExchangeOutBean filterType) {
        this.filterType = filterType;
    }

    public void setPortExchangeCost(double portExchangeCost) {
        this.portExchangeCost = portExchangeCost;
    }

    public void setMPortExchangeCost(double mPortExchangeCost) {
        this.mPortExchangeCost = mPortExchangeCost;
    }

    public void setExchangeCost(double exchangeCost) {
        this.exchangeCost = exchangeCost;
    }

    public void setAnalysisCode3(String analysisCode3) {
        this.analysisCode3 = analysisCode3;
    }

    public void setVBaseExchangeCost(double vBaseExchangeCost) {
        this.vBaseExchangeCost = vBaseExchangeCost;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

    public void setMExchangeCost(double mExchangeCost) {
        this.mExchangeCost = mExchangeCost;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setAnalysisName1(String analysisName1) {
        this.analysisName1 = analysisName1;
    }

    public void setBaseExchangeCost(double baseExchangeCost) {
        this.baseExchangeCost = baseExchangeCost;
    }

    public void setAnalysisCode2(String analysisCode2) {
        this.analysisCode2 = analysisCode2;
    }

    public void setAnalysisCode1(String analysisCode1) {
        this.analysisCode1 = analysisCode1;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setVExchangeCost(double vExchangeCost) {
        this.vExchangeCost = vExchangeCost;
    }

    public void setOldFInSecNum(String oldFInSecNum) {
        this.oldFInSecNum = oldFInSecNum;
    }

    public void setAnalysisName2(String analysisName2) {
        this.analysisName2 = analysisName2;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setExchangeDate(Date exchangeDate) {
        this.exchangeDate = exchangeDate;
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

    public String getFInSecNum() {
        return fInSecNum;
    }

    public String getPortName() {
        return portName;
    }

    public String getSecurityName() {
        return securityName;
    }

    public Date getExchangeDate() {
        return exchangeDate;
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

    public SecExchangeOutBean() {
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
            this.num = reqAry[0];
            this.fInSecNum = reqAry[1];
            this.securityCode = reqAry[2];
            this.portCode = reqAry[3];
            this.analysisCode1 = reqAry[4];
            this.analysisCode2 = reqAry[5];
            this.analysisCode3 = reqAry[6];
            if (reqAry[7].length() != 0) {
                this.exchangeAmount = Double.parseDouble(reqAry[7]);
            }
            if (reqAry[8].length() != 0) {
                this.exchangeCost = Double.parseDouble(reqAry[8]);
            }
            if (reqAry[9].length() != 0) {
                this.mExchangeCost = Double.parseDouble(reqAry[9]);
            }
            if (reqAry[10].length() != 0) {
                this.vExchangeCost = Double.parseDouble(reqAry[10]);
            }
            if (reqAry[11].length() != 0) {
                this.baseExchangeCost = Double.parseDouble(reqAry[11]);
            }
            if (reqAry[12].length() != 0) {
                this.mBaseExchangeCost = Double.parseDouble(reqAry[12]);
            }
            if (reqAry[13].length() != 0) {
                this.vBaseExchangeCost = Double.parseDouble(reqAry[13]);
            }
            if (reqAry[14].length() != 0) {
                this.portExchangeCost = Double.parseDouble(reqAry[14]);
            }
            if (reqAry[15].length() != 0) {
                this.mPortExchangeCost = Double.parseDouble(reqAry[15]);
            }
            if (reqAry[16].length() != 0) {
                this.vPortExchangeCost = Double.parseDouble(reqAry[16]);
            }
            // this.checkStateId = YssFun.toInt(reqAry[17]);
            this.oldNum = reqAry[17];
            this.oldFInSecNum = reqAry[18];
            if (YssFun.isDate(reqAry[19])) {
                this.exchangeDate = YssFun.toDate(reqAry[19]);
            }
            this.desc = reqAry[20];
            //this.attrClsCode = reqAry[20];
            //this.catTypeCode = reqAry[21];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SecExchangeOutBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析证券兑换-流出请求出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();

        buf.append(this.fInSecNum).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.exchangeAmount).append("\t");
        buf.append(this.exchangeCost).append("\t");
        buf.append(this.mExchangeCost).append("\t");
        buf.append(this.vExchangeCost).append("\t");
        buf.append(this.baseExchangeCost).append("\t");
        buf.append(this.mBaseExchangeCost).append("\t");
        buf.append(this.vBaseExchangeCost).append("\t");
        buf.append(this.portExchangeCost).append("\t");
        buf.append(this.mPortExchangeCost).append("\t");
        buf.append(this.vPortExchangeCost).append("\t");
        buf.append(this.analysisCode1).append("\t");
        buf.append(this.analysisName1).append("\t");
        buf.append(this.analysisCode2).append("\t");
        buf.append(this.analysisName2).append("\t");
        //add by songjie 2012.01.12 BUG 3376 QDV4赢时胜（测试）2011年12月12日03_B
        buf.append(this.desc).append("\t");
        //buf.append(this.attrClsCode).append("\t");
        //buf.append(this.attrClsName).append("\t");
        //buf.append(this.catTypeCode).append("\t");
        //buf.append(this.catTypeName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
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
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security"); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            sHeader = this.getListView1Headers();
            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Security); //获得分析代码
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*, b.fusername as fcreatorname, c.fusername as fcheckusername," +
                " d.FPortName,e.FSecurityName, j.FAttrClsName as FAttrClsName " + sAry[0];
            strSql = strSql + " from " +
                pub.yssGetTableName("Tb_Data_SecExchangeOut") + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                //-----------------------------------------------------------------------------------------------
                " left join (select FPortCode,FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState <> 2) d on a.FPortCode = d.FPortCode " +
                //-------------------------------------------------------------------------------------------------
                " left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState <> 2 ) e on a.FSecurityCode = e.FSecurityCode  " +
                " left join (select FAttrClsCode,FAttrClsName from " + //sj add 20071204
                pub.yssGetTableName("Tb_Para_AttributeClass") + ") j on a.FAttrClsCode = j.FAttrClsCode " +
                sAry[1] +
                " where a.FInSecNum = " + dbl.sqlString(this.fInSecNum);

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                setResultSetAttr(rs);
                bufShowDataStr.append(this.buildRowStr()).
                    append(YssCons.YSS_LINESPLITMARK);
                bufAllDataStr.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
                //bufShowDataStr.append(YssFun.formatNumber(rs.getDouble("FOutMoney"), "#,##0.##"));
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
            throw new YssException("获取证券兑换-流出信息出错" + "\r\n" + e.getMessage(), e);
        }
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException,
        YssException {
        boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security"); //判断分析代码存不存在
        boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
        this.num = rs.getString("FNum") + "";
        this.fInSecNum = rs.getString("FInSecNum") + "";
        this.securityCode = rs.getString("FSecurityCode") + "";
        this.securityName = rs.getString("FSecurityName") + "";
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        if (analy1) {
            this.analysisCode1 = rs.getString("FInvMgrCode") + "";
            this.analysisName1 = rs.getString("FInvMgrName") + "";
        }
        if (analy2) {
            this.analysisCode2 = rs.getString("FBrokerCode") + "";
            this.analysisName2 = rs.getString("FBrokerName") + "";
        }
        this.analysisCode3 = rs.getString("FAnalysisCode3") + "";
        this.exchangeAmount = rs.getDouble("FOUTAMOUNT");
        this.exchangeCost = rs.getDouble("FExchangeCost");
        this.mExchangeCost = rs.getDouble("FMExchangeCost");
        this.vExchangeCost = rs.getDouble("FVExchangeCost");
        this.baseExchangeCost = rs.getDouble("FBaseExchangeCost");
        this.mBaseExchangeCost = rs.getDouble("FMBaseExchangeCost");
        this.vBaseExchangeCost = rs.getDouble("FVBaseExchangeCost");
        this.portExchangeCost = rs.getDouble("FPortExchangeCost");
        this.mPortExchangeCost = rs.getDouble("FMPortExchangeCost");
        this.vPortExchangeCost = rs.getDouble("FVPortExchangeCost");
        this.attrClsCode = rs.getString("FAttrClsCode");
        this.attrClsName = rs.getString("FAttrClsName");
        this.desc = rs.getString("FDESC");
        super.setRecLog(rs);

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
                                   String strInSecNum) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        java.sql.Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            if (!bIsTrans) {
                conn.setAutoCommit(false);
                bTrans = true;
            }
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_SecExchangeOut") +
                " where FInSecNum = " +
                dbl.sqlString(strInSecNum);
            dbl.executeSql(strSql);
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Data_SecExchangeOut") +
                "(FNum, FInSecNum, FSecurityCode,FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FOUTAMOUNT" +
                ",FExchangeCost,FMExchangeCost,FVExchangeCost,FPortExchangeCost,FMPortExchangeCost,FVPortExchangeCost" +
                ",FBaseExchangeCost,FMBaseExchangeCost,FVBaseExchangeCost" +
                ",FAttrClsCode,FCatType" +
                //edit by songjie 2012.01.12 BUG 3376 QDV4赢时胜（测试）2011年12月12日03_B
                ",FCheckState, FCreator, FCreateTime,FCheckUser,FDESC) " +
                " values (?,?,?,?,?,?,?,?" +
                ",?,?,?,?,?,?" +
                //edit by songjie 2012.01.12 BUG 3376 QDV4赢时胜（测试）2011年12月12日03_B
                ",?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                this.parseRowStr(sMutilRowAry[i]);
                if (strInSecNum.trim().length() > 0) {
                    this.num = YssFun.formatNumber(i + 1, "00001");
                    this.fInSecNum = strInSecNum;
                    pstmt.setString(1, this.num);
                    pstmt.setString(2, this.fInSecNum);
                    pstmt.setString(3, this.securityCode);
                    pstmt.setString(4, this.portCode);
                    pstmt.setString(5,
                                    (this.analysisCode1 == null ||
                                     this.analysisCode1.equals("")) ? " " :
                                    this.analysisCode1);
                    pstmt.setString(6,
                                    (this.analysisCode2 == null || this.analysisCode2.equals("")) ?
                                    " " : this.analysisCode2);
                    pstmt.setString(7,
                                    (this.analysisCode3 == null ||
                                     this.analysisCode3.equals("")) ? " " :
                                    this.analysisCode3);
                    pstmt.setDouble(8, this.exchangeAmount);
                    pstmt.setDouble(9, this.exchangeCost);
                    pstmt.setDouble(10, this.mExchangeCost);
                    pstmt.setDouble(11, this.vExchangeCost);
                    pstmt.setDouble(12, this.portExchangeCost);
                    pstmt.setDouble(13, this.mPortExchangeCost);
                    pstmt.setDouble(14, this.vPortExchangeCost);
                    pstmt.setDouble(15, this.baseExchangeCost);
                    pstmt.setDouble(16, this.mBaseExchangeCost);
                    pstmt.setDouble(17, this.vBaseExchangeCost);
                    pstmt.setString(18, this.attrClsCode == null || this.attrClsCode.length() == 0 ? " " : this.attrClsCode);
                    pstmt.setString(19, this.catTypeCode == null || this.catTypeCode.length() == 0 ? " " : this.catTypeCode);
                    pstmt.setInt(20, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(21, this.creatorCode);
                    pstmt.setString(22, this.creatorTime);
                    pstmt.setString(23,
                                    (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.setString(24, this.desc);
                    pstmt.executeUpdate();
                }
            }
            //createCashTrans(this.saving, sMutilRowAry);
            if (!bIsTrans) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (YssException ex) {
            if (pstmt != null) {
                pstmt = null;
            }
            throw new YssException("保存证券兑换-流出信息出错" + "\r\n" + ex.getMessage(), ex);
        } catch (SQLException ex) {
            throw new YssException("保存证券兑换-流出信息出错" + "\r\n" + ex.getMessage(), ex);
        } finally{
        	dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

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

    public String getOperValue(String sType) throws YssException {
        return this.buildRowStr();
    }

    public String getBeforeEditData() {
        return "";
    }

    /**
     * sj 20071123 add 获取流出证券的成本
     * @param rs ResultSet
     * @throws YssException
     */
    public void calCost(ResultSet rs) throws YssException { //交易关联表调用 by liyu 1203
        YssCost cost = null;
        try {
            ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
                "avgcostcalculate");
            // sqlStr = "" +
            //operSql.sqlStock(dDate, "Tb_Stock_Security", 0) + //-1代表取前一天的，0代表不取前一天;
            //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
            //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
            costCal.initCostCalcutate(rs.getDate("FStorageDate"),
                                      rs.getString("FPortCode"),
                                      (rs.getString("FAnalysisCode1") == null || rs.getString("FAnalysisCode1").length() == 0 ? " " : rs.getString("FAnalysisCode1")),
                                      (rs.getString("FAnalysisCode2") == null || rs.getString("FAnalysisCode2").length() == 0 ? " " : rs.getString("FAnalysisCode2")),
                                      rs.getString("FAttrClsCode"));
            costCal.setYssPub(pub);
            cost = costCal.getCarryCost(rs.getString("FSecurityCode"),
                                        this.exchangeAmount,
                                        YssFun.left(rs.getString("FNum") +
                "", (rs.getString("FNum") + "").length() - 5),
                                        rs.getDouble("FBaseCuryRate"),
                                        rs.getDouble("FPortCuryRate"));
            costCal.roundCost(cost, 2);
            this.exchangeCost = cost.getCost();
            this.mExchangeCost = cost.getMCost();
            this.vExchangeCost = cost.getVCost();
            this.baseExchangeCost = cost.getBaseCost();
            this.mBaseExchangeCost = cost.getBaseMCost();
            this.vBaseExchangeCost = cost.getBaseVCost();
            this.portExchangeCost = cost.getPortCost();
            this.mPortExchangeCost = cost.getPortMCost();
            this.vPortExchangeCost = cost.getPortVCost();
        } catch (Exception e) {
            dbl.closeResultSetFinal(rs);
            throw new YssException("获取证券兑换信息出错" + "\r\n" + e.getMessage(), e);
        }

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
