package com.yss.main.cashmanage;

import java.sql.*;

import com.yss.commeach.*;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

public class TransferSetBean
    extends BaseDataSettingBean implements IDataSetting {
    private String sNum = ""; //编号
    private String sSubNum = ""; //子编号
    private int iInOut = 99; //资金流向 1代表流入;-1代表流出
    private String sPortCode = ""; //组合代码
    private String sPortName = ""; //组合名称
    private String sAnalysisCode1 = ""; //分析代码1
    private String sAnalysisName1 = ""; //分析名称1
    private String sAnalysisCode2 = ""; //分析代码2
    private String sAnalysisName2 = ""; //分析名称2
    private String sAnalysisCode3 = ""; //分析代码3
    private String sAnalysisName3 = ""; //分析名称3
    private String sCashAccCode = ""; //现金帐户代码
    private String sCashAccName = ""; //现金帐户名称
    private double dMoney; //调拨金额
    private double dBaseRate; //基础汇率
    private double dPortRate; //组合汇率
    private String sOldNum = "";
    private String strIsOnlyColumns = "0";
    private TransferSetBean filterType;
    private String sDesc = "";
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    private String strAttrClsCode = "";//所属分类
    private String strAttrClsName = "";
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    public String getSAnalysisName3() {
        return sAnalysisName3;
    }

    public String getSNum() {
        return sNum;
    }

    public int getCheckStateId() {
        return checkStateId;
    }

    public String getSAnalysisCode3() {
        return sAnalysisCode3;
    }

    public double getDBaseRate() {
        return dBaseRate;
    }

    public String getSAnalysisName2() {
        return sAnalysisName2;
    }

    public TransferSetBean getFilterType() {
        return filterType;
    }

    public String getSDesc() {
        return sDesc;
    }

    public String getSPortName() {
        return sPortName;
    }

    public String getSAnalysisCode2() {
        return sAnalysisCode2;
    }

    public String getSSubNum() {
        return sSubNum;
    }

    public String getSOldNum() {
        return sOldNum;
    }

    public String getSAnalysisName1() {
        return sAnalysisName1;
    }

    public String getSCashAccCode() {
        return sCashAccCode;
    }

    public String getSAnalysisCode1() {
        return sAnalysisCode1;
    }

    public String getSCashAccName() {
        return sCashAccName;
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public double getDMoney() {
        return dMoney;
    }

    public double getDPortRate() {
        return dPortRate;
    }

    public void setCheckStateId(int checkStateId) {
        this.checkStateId = checkStateId;
    }

    public void setIInOut(int iInOut) {
        this.iInOut = iInOut;
    }

    public void setSAnalysisName3(String sAnalysisName3) {
        this.sAnalysisName3 = sAnalysisName3;
    }

    public void setSNum(String sNum) {
        this.sNum = sNum;
    }

    public void setSAnalysisCode3(String sAnalysisCode3) {
        this.sAnalysisCode3 = sAnalysisCode3;
    }

    public void setDBaseRate(double dBaseRate) {
        this.dBaseRate = dBaseRate;
    }

    public void setSAnalysisName2(String sAnalysisName2) {
        this.sAnalysisName2 = sAnalysisName2;
    }

    public void setFilterType(TransferSetBean filterType) {
        this.filterType = filterType;
    }

    public void setSDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    public void setSPortName(String sPortName) {
        this.sPortName = sPortName;
    }

    public void setSAnalysisCode2(String sAnalysisCode2) {
        this.sAnalysisCode2 = sAnalysisCode2;
    }

    public void setSSubNum(String sSubNum) {
        this.sSubNum = sSubNum;
    }

    public void setSOldNum(String sOldNum) {
        this.sOldNum = sOldNum;
    }

    public void setSAnalysisName1(String sAnalysisName1) {
        this.sAnalysisName1 = sAnalysisName1;
    }

    public void setSCashAccCode(String sCashAccCode) {
        this.sCashAccCode = sCashAccCode;
    }

    public void setSAnalysisCode1(String sAnalysisCode1) {
        this.sAnalysisCode1 = sAnalysisCode1;
    }

    public void setSCashAccName(String sCashAccName) {
        this.sCashAccName = sCashAccName;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setDMoney(double dMoney) {
        this.dMoney = dMoney;
    }

    public void setDPortRate(double dPortRate) {
        this.dPortRate = dPortRate;
    }

    public int getIInOut() {
        return iInOut;
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
    
    
    public TransferSetBean() {
    }
    
	public void parseRowStr(String sRowStr) throws YssException { //前台数解析
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
            this.sNum = reqAry[0];
            this.sSubNum = reqAry[1];
            this.iInOut = YssFun.toInt(reqAry[2]);
            this.sPortCode = reqAry[3];
            this.sAnalysisCode1 = reqAry[4].equalsIgnoreCase("null") ? " " : reqAry[4];
            this.sAnalysisCode2 = reqAry[5].equalsIgnoreCase("null") ? " " : reqAry[5];
            this.sAnalysisCode3 = reqAry[6].equalsIgnoreCase("null") ? " " : reqAry[6];
            this.sCashAccCode = reqAry[7];
            this.dMoney = YssFun.toDouble(reqAry[8]);
            this.dBaseRate = YssFun.toDouble(reqAry[9]);
            this.dPortRate = YssFun.toDouble(reqAry[10]);
            super.checkStateId = Integer.parseInt(reqAry[11]);
            this.sOldNum = reqAry[12];
            this.sDesc = reqAry[13]; 
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            if(reqAry.length==16){
            	 this.strAttrClsCode = reqAry[14];
            }else{
            	this.strAttrClsCode = " ";
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            //add code;
            //     System.out.println("reqAry[13]="+reqAry[13]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TransferSetBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析资金调拨设置请求信息出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sNum.trim()).append("\t");
        buf.append(this.sSubNum.trim()).append("\t");
        buf.append(this.iInOut).append("\t");
        buf.append(this.sPortCode.trim()).append("\t");
        buf.append(this.sPortName.trim()).append("\t");
        buf.append(this.sAnalysisCode1.trim()).append("\t");
        buf.append(this.sAnalysisName1.trim()).append("\t");
        buf.append(this.sAnalysisCode2.trim()).append("\t");
        buf.append(this.sAnalysisName2.trim()).append("\t");
        buf.append(this.sAnalysisCode3.trim()).append("\t");
        buf.append(this.sAnalysisName3.trim()).append("\t");
        buf.append(this.sCashAccCode.trim()).append("\t");
        buf.append(this.sCashAccName.trim()).append("\t");
        buf.append(this.dMoney).append("\t");
        buf.append(this.dBaseRate).append("\t");
        buf.append(this.dPortRate).append("\t");
        buf.append(this.sDesc).append("\t"); //appended;
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查联系人输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Cash_SubTransfer"),
                               "FNum,FSubNum",
                               this.sNum + "," + this.sSubNum,
                               this.sOldNum + "," + this.sSubNum);
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException,
        YssException {
        this.sNum = rs.getString("FNum");
        this.sSubNum = rs.getString("FSubNum");
        this.iInOut = rs.getInt("FInOut");
        this.sPortCode = rs.getString("FPortCode") + "";
        this.sPortName = rs.getString("FPortName") + "";
        this.sAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
        this.sAnalysisName1 = rs.getString("FAnalysisname1") + "";
        this.sAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
        this.sAnalysisName2 = rs.getString("FAnalysisName2") + "";
        this.sAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
        this.sAnalysisName3 = rs.getString("FAnalysisName3") + "";
        this.sCashAccCode = rs.getString("FCashAccCode") + "";
        this.sCashAccName = rs.getString("FCashAccName") + "";
        this.dMoney = rs.getDouble("FMoney");
        this.dBaseRate = rs.getDouble("FBaseCuryRate");
        this.dPortRate = rs.getDouble("FPortCuryRate");
        this.sDesc = rs.getString("FDesc") + ""; //add code at 4.9;
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

    public String getOperValue(String sType) throws YssException {
        if (sType != null && sType.equalsIgnoreCase("getCashTransferSetInfo")) {
            String strSql = "";
            ResultSet rs = null;
            StringBuffer buf = new StringBuffer();
            try {
                strSql = "select a.*, b.fusername as fcreatorname, c.fusername as fcheckusername, d.FPortName, e.FCashAccName ";
                strSql = strSql +
                    ( (this.getCashStorageAnalysisSql().trim().length() == 0) ?
                     ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                     ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ");
                strSql = strSql + " from " +
                    pub.yssGetTableName("Tb_Cash_SubTransfer") + " a" +
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
                    " ) d on a.FPortCode = d.FPortCode" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    //-------------------------------------------------------------------------------------------------
                    " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                    pub.yssGetTableName("Tb_Para_CashAccount") +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                    " select FCashAccCode, FCashAccName, FStartDate from " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where FCheckState = 1) e on a.FCashAccCode = e.FCashAccCode " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    //-----------------------------------------------------------------------------------------------

                    this.getCashStorageAnalysisSql() +
                    " where a.FNum = " + dbl.sqlString(this.sNum) +
                    " and a.FSubNum = " + dbl.sqlString(this.sSubNum);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    setResultSetAttr(rs);
                    buf.append(this.buildRowStr());
                }

                if (buf.toString().length() > 0) {
                    return buf.toString();
                } else {
                    return "";
                }
            } catch (Exception e) {
                throw new YssException("获取资金调拨信息出错", e);
            } finally {
                dbl.closeResultSetFinal(rs);
            }
        }

        else {
            //获取现金库存配置信息
            AssetStorageCfgBean assetgroupcfg = new AssetStorageCfgBean();
            assetgroupcfg.setYssPub(pub);
            return assetgroupcfg.getPartSetting("Cash");
        }
    }

    public String saveMutliOperData(String sMutilRowStr) throws YssException {
        return saveMutliOperData(sMutilRowStr, false, "", ""); //MS00319 QDV4华夏2009年3月16日01_B 增加旧编号的参数
    }

    public String saveMutilOperData(String sMutilRowStr, boolean bIsTrans, String strNum) throws YssException {
        return saveMutliOperData(sMutilRowStr, bIsTrans, strNum, ""); //MS00319 QDV4华夏2009年3月16日01_B 增加旧编号的参数
    }

    public String saveMutliOperData(String sMutilRowStr, boolean bIsTrans,
                                    String strNum, String oldStrNum) throws YssException { //MS00319 QDV4华夏2009年3月16日01_B 增加旧编号的参数 oldStrNum
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            if (!bIsTrans) {
                conn.setAutoCommit(false);
                bTrans = true;
            }

            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);

            strSql = "delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FNum = " +
                //MS00319 QDV4华夏2009年3月16日01_B 增加旧编号的参数 oldStrNum ------
                dbl.sqlString(oldStrNum.length() > 0 ? oldStrNum : strNum);
            //---------------------------------------------------------------
            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                "(FNum, FSubNum, FInOut, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FCashAccCode," +
                "FMoney, FBaseCuryRate, FPortCuryRate,FATTRCLSCODE," +//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
                " FCheckState, FCreator, FCreateTime,FCheckUser,FDesc) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                this.parseRowStr(sMutilRowAry[i]);
                if (strNum.trim().length() > 0) {
                    this.sNum = strNum;
                    this.sSubNum = YssFun.formatNumber(i + 1, "00000");
                    pstmt.setString(1, this.sNum);
                    pstmt.setString(2, this.sSubNum);
                    pstmt.setInt(3, this.iInOut);
                    pstmt.setString(4, this.sPortCode);
                    //FAnalysisCode如果为空时保存的值跟自动产生的资金调拨数据不一致，所以在这边修改为空值时，保存为一个空格。fazmm20070910,以免取数有问题
                    pstmt.setString(5,
                                    this.sAnalysisCode1.trim().length() == 0 ? " " :
                                    this.sAnalysisCode1);
                    pstmt.setString(6,
                                    this.sAnalysisCode2.trim().length() == 0 ? " " :
                                    this.sAnalysisCode2);
                    pstmt.setString(7,
                                    this.sAnalysisCode3.trim().length() == 0 ? " " :
                                    this.sAnalysisCode3);
                    pstmt.setString(8, this.sCashAccCode);
                    pstmt.setDouble(9, this.dMoney);
                    pstmt.setDouble(10, this.dBaseRate);
                    pstmt.setDouble(11, this.dPortRate);
                   //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    pstmt.setString(12, this.strAttrClsCode.trim().length()==0?" ":this.strAttrClsCode);
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    pstmt.setInt(13, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(14, this.creatorCode);
                    pstmt.setString(15, this.creatorTime);
                    pstmt.setString(16,
                                    (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.setString(17, this.sDesc);
                    //     System.out.println("Desc="+this.sDesc);   //add code
                    pstmt.executeUpdate();
                }
            }
            if (!bIsTrans) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return "";//modified by yeshenghong for CCB security check 20121018 
        } catch (SQLException e) {
            throw new YssException("保存资金调拨信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getListViewData1
     * 获取资金调拨数据
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
        double dPortCuryRate = 0, dBaseCuryRate = 0; //MS00177
        EachRateOper eachOper = null; //MS00177
        try {
            sHeader =
                "调拨方向\t现金帐户代码\t现金帐户名称\t调拨金额\t投资组合\t所属分类\t所属分类名称\t分析配置1\t分析配置2\t分析配置3\t基础汇率\t组合汇率\t描  述";
            if (strIsOnlyColumns.trim().equals("0")) {
                strSql =
                    "select a.*,case when a.Finout=1 then '流入' else '流出' end as FInOutName, b.fusername as fcreatorname, c.fusername as fcheckusername, d.FPortName, e.FCashAccName ";
                strSql += ",d.FPortCury as FPortCuryCode,e.FCuryCode,f.FTsfTypeCode,f.FSubTsfTypeCode,f.FTransDate,nvl(k.FAttrClsName,' ') as FAttrClsName "; //MS00177 添加现金帐户币种与组合货币的币种,并添加调拨类型与子类型
                strSql = strSql +
                    ( (this.getCashStorageAnalysisSql().trim().length() == 0) ?
                     ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                     ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ");
                strSql = strSql + " from " +
                    pub.yssGetTableName("Tb_Cash_SubTransfer") + " a" +
                    " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                    " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                    //-----------------------------------------------------------------------------------------------
                    " left join " + pub.yssGetTableName("tb_cash_transfer") + " f on a.FNum = f.Fnum " + //MS00177关联资金调拨主表 //modify by sunkey 20090313 BugNO:MS00306 将组合群号为前缀的表修改为自动生成，而非写死的001
                    " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName,o.FPortCury from " + //MS00177 将组合货币代码添加进来
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
                    " ) d on a.FPortCode = d.FPortCode" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    //-------------------------------------------------------------------------------------------------
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    " left join (select FAttrClsCode,FAttrClsName from " +
                    pub.yssGetTableName("Tb_Para_AttributeClass") +
                    ") k on a.FAttrClsCode = k.FAttrClsCode " +
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                    pub.yssGetTableName("Tb_Para_CashAccount") +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                    //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    " select FCashAccCode, FCashAccName, FStartDate,FCuryCode from " + //MS00177 将现金帐户的币种添加进来
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where FCheckState = 1) e on a.FCashAccCode = e.FCashAccCode " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    //-----------------------------------------------------------------------------------------------

                    this.getCashStorageAnalysisSql() +
                    " where a.FNum = " + dbl.sqlString(this.filterType.sNum) +
                    " order by a.FInOut desc ";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    //QDV4赢时胜（上海）2009年04月15日01_B MS00382 屏蔽掉下面的代码，与本BUG冲突 by leeyu 20090417
                    //----------------MS00177 当调拨子类型为换汇时重新取一次汇率。 by leeyu 2009-1-9 MS00177
//               if (rs.getString("FSubTsfTypeCode")!=null &&
//                   rs.getString("FSubTsfTypeCode").equalsIgnoreCase(YssOperCons.YSS_ZJDBZLX_COST_RateTrade)) {
//                  //当调拨类型为内部资金调拨，调拨子类型为换汇时
//                  eachOper =new EachRateOper();
//                  eachOper.setYssPub(pub);
//                  eachOper.setDRateDate(rs.getDate("FTransDate"));
//                  eachOper.setSPortCode(rs.getString("FPortCode"));
//                  eachOper.setSCuryCode(rs.getString("FCuryCode"));
//                  eachOper.getOperValue("rate");
//                  dBaseCuryRate = eachOper.getDBaseRate();
//                  dPortCuryRate = eachOper.getDPortRate();
//               }else{
                    dBaseCuryRate = rs.getDouble("FBaseCuryRate");
                    dPortCuryRate = rs.getDouble("FPortCuryRate");
//               }
                    //----------------MS00177
                    buf.append( (rs.getString("FInOutName") + "").trim());
                    buf.append("\t");
                    buf.append( (rs.getString("FCashAccCode") + "").trim());
                    buf.append("\t");
                    buf.append( (rs.getString("FCashAccName") + "").trim());
                    buf.append("\t");
                    buf.append(YssFun.formatNumber(rs.getDouble("FMoney"),
                        "#,##0.##"));
                    buf.append("\t");
                    buf.append( (rs.getString("FPortName") + "").trim());
                    buf.append("\t");
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    buf.append((rs.getString("FATTRCLSCODE") + "").trim());
                    buf.append("\t");
                    buf.append((rs.getString("FATTRCLSName") + "").trim());
                    buf.append("\t");
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    buf.append( (rs.getString("FAnalysisName1") + "").trim());
                    buf.append("\t");
                    buf.append( (rs.getString("FAnalysisName2") + "").trim());
                    buf.append("\t");
                    buf.append( (rs.getString("FAnalysisName3") + "").trim());
                    buf.append("\t");
//               buf.append(rs.getDouble("FBaseCuryRate"));
                    buf.append(dBaseCuryRate); //MS00177 此处取变量中的汇率
                    buf.append("\t");
//               buf.append(rs.getDouble("FPortCuryRate"));
                    buf.append(dPortCuryRate); //MS00177
                    buf.append("\t");
                    buf.append( (rs.getString("FDesc") + "").trim());
                    buf.append("\t");
                    buf.append(YssCons.YSS_LINESPLITMARK);

                    setResultSetAttr(rs);

                    this.dBaseRate = dBaseCuryRate; //MS00177
                    this.dPortRate = dPortCuryRate; //MS00177
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
            throw new YssException("获取资金调拨信息出错", e);
        }finally{
        	dbl.closeResultSetFinal(rs);//关闭游标 by leeyu 20100909
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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /**
     * 获取辅助字段之查询Sql语句
     * @return String
     */
    private String getCashStorageAnalysisSql() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        try { //为关游标增加try...catch功能 by leeyu 2009-01-07 MS00177
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
                            " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                            i +
                            " from  (select FBrokerCode,max(FStartDate) as FStartDate  from " +
                            pub.yssGetTableName("tb_para_broker") +
                            " where FStartDate < " +
                            dbl.sqlDate(new java.util.Date()) +
                            " and FCheckState = 1 group by FBrokerCode )x " +
                            " join (select * from " +
                            pub.yssGetTableName("tb_para_broker") + ") y on x.FBrokerCode = y.FBrokerCode and x.FStartDate = y.FStartDate) broker on a.FAnalysisCode" +
                            i + " = broker.FBrokerCode";
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
                        	//------ modify by wangzuochun 2010.07.26  MS01471    打开资金调拨页面，弹出错误对话框    QDV4赢时胜(测试)2010年7月21日2_B   
                            " left join (select FCatCode,FCatName as FAnalysisName" + i + " from Tb_Base_Category where FCheckState = 1) category on a.FAnalysisCode" +
                            i + " = category.FCatCode";
                        	//-----------------------------------------MS01471--------------------------------------------------------------//
                    } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                               rs.getString("FAnalysisCode" + String.valueOf(i)).
                               equalsIgnoreCase("001")) {
                        sResult = sResult +
                        // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
               
                        
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                        i +
                        "  from  " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " n where  n.FCheckState = 1) invmgr on a.FAnalysisCode" +
                        i + " = invmgr.FInvMgrCode ";
                    
                        
                        //end by lidaolong
                    }

                    else {
                        sResult = sResult +
                            " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                            i + " from  " +
                            pub.yssGetTableName("Tb_Para_StorageCfg") +
                            " where 1=2) tn" + i + " on a.FAnalysisCode" + i +
                            " = tn" +
                            i + ".FAnalysisNull ";
                    }
                }
            }
        } catch (Exception ex) {

        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标 by leeyu 2009-01-07 MS00177
        }
        return sResult;
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
    }

    public String editSetting(boolean bAutoCommit) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        try {

            this.addSetting(false);
            /*  if (this.sOldNum.equals("")) {
                 this.addSetting(false);
              }
              else {
                 strSql = "update  " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                       " set " +
                       "FInOut=?,FPortCode=?,FAnalysisCode1=?,FAnalysisCode2=?,FAnalysisCode3=?,FCashAccCode=?," +
                       "FMoney=?,FBaseCuryRate=?,FPortCuryRate=?,FCheckState=?,FDesc=?,FCheckUser=?,FCheckTime=?,FNum=?" +
                       " where FNum=" + dbl.sqlString(this.sOldNum) +
                       " and FSubNum=" + dbl.sqlString(this.sSubNum);
                 pstmt = conn.prepareStatement(strSql);
                 if (!bAutoCommit) {
                    conn.setAutoCommit(false);
                    bTrans = true;
                 }
                 pstmt.setInt(1, this.iInOut);
                 pstmt.setString(2, this.sPortCode);
                 pstmt.setString(3, this.sAnalysisCode1);
                 pstmt.setString(4, this.sAnalysisCode2);
                 pstmt.setString(5, this.sAnalysisCode3);
                 pstmt.setString(6, this.sCashAccCode);
                 pstmt.setDouble(7, this.dMoney);
                 pstmt.setDouble(8, this.dBaseRate);
                 pstmt.setDouble(9, this.dPortRate);
                 pstmt.setInt(10, this.checkStateId);
                 pstmt.setString(11, this.sDesc);
                 pstmt.setString(12, pub.getUserCode());
                 pstmt.setString(13,
                                 dbl.sqlString(YssFun.formatDate(new java.util.Date(),
                       "yyyyMMdd HH:mm:ss")));
                 pstmt.setString(14, this.sNum);
                 pstmt.executeUpdate();

              }*/

            if (!bAutoCommit) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }

            return null;
        } catch (Exception e) {
            throw new YssException("修改调拨子表信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String addSetting(boolean bAutoCommit) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Cash_SubTransfer") + " (FNum," +
                "FSubNum,FInOut,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode," +
                "FMoney,FBaseCuryRate,FPortCuryRate,FATTRCLSCODE," +//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
                "FCheckState,FDesc,FCreator,FCreateTime) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);
            if (!bAutoCommit) {
                conn.setAutoCommit(false);
                bTrans = true;
            }
            pstmt.setString(1, this.sNum);
            pstmt.setString(2, this.sSubNum);
            pstmt.setInt(3, this.iInOut);
            pstmt.setString(4, this.sPortCode);
            //FAnalysisCode如果为空时保存的值跟自动产生的资金调拨数据不一致，所以在这边修改为空值时，保存为一个空格。fazmm20070910,以免取数有问题
            pstmt.setString(5, this.sAnalysisCode1.trim().length() == 0 ? " " : this.sAnalysisCode1);
            pstmt.setString(6, this.sAnalysisCode2.trim().length() == 0 ? " " : this.sAnalysisCode2);
            pstmt.setString(7, this.sAnalysisCode3.trim().length() == 0 ? " " : this.sAnalysisCode3);
            pstmt.setString(8, this.sCashAccCode);
            pstmt.setDouble(9, this.dMoney);
            pstmt.setDouble(10, this.dBaseRate);
            pstmt.setDouble(11, this.dPortRate);
          //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            pstmt.setString(12, this.strAttrClsCode.trim().length() ==0 ?" ":this.strAttrClsCode);
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            pstmt.setInt(13, this.checkStateId);
            pstmt.setString(14, this.sDesc);
            pstmt.setString(15, pub.getUserCode());
            pstmt.setString(16, dbl.sqlString(YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss")));
            pstmt.executeUpdate();
            if (!bAutoCommit) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }

            return null;
        } catch (Exception e) {
            throw new YssException("增加调拨子表信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(pstmt);
        }

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

    /*public Object clone() {
        return this.clone();
    }*/

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
