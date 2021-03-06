package com.yss.main.cashmanage;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.syssetting.RightBean;
import com.yss.util.*;

/**
 * <p>Title: TransferBean </p>
 * <p>Description: 现金管理--现金调拨（主表） </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */
public class TransferBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strNum = "";             //现金调拨编号
    private String strPortCode = "";
    private String strPortName = "";
    private String strTsfTypeCode = "";     //调拨类型代码
    private String strTsfTypeName = "";     //调拨类型名称
    private String strSubTsfTypeCode = "";  //调拨子类型代码
    private String strSubTsfTypeName = "";  //调拨子类型名称
    private String strAttrClsCode = "";     //属性代码
    private String strAttrClsName = "";     //属性名称
    private String strSecurityCode = "";    //投资品种代码
    private String strSecurityName = "";    //投资品种名称
    private java.util.Date dtTransferDate;  //调拨日期
  //----add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围---start---//
    private java.util.Date dtTransferEndDate;  //调拨结束日期
    private java.util.Date dtTransEndDate;  //业务结束日期
  //----add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围---end---//
    private String strTransferTime = "";    //调拨时间
    private java.util.Date dtTransDate;     //业务日期
    private String strTradeNum = "";        //交易记录
    private String strDesc = "";            //现金调拨描述
    private String strOldNum = "";
    private String srcCashAccCode = "";     //来源帐户代码
    private String srcCashAccName = "";     //来源帐户名称
    private String strIsOnlyColumns = "0";  //在初始登陆时是否只显示标题，不查询数据
    private String savingNum = "";          //定存编号
    private String cprNum = "";             //现金应收应付编号
    private String FIPRNum = "";            //运营应收应付编号
    private String rateTradeNum = "";       //汇率交易的编号
    private TransferBean filterType;
    private String strTansferSet = "";
    private String FRelaNum = "";           //关联编号  wdy add
    private String FNumType = "";           //编号类型  wdy add
    private int dataSource = 0;             //这里应默认数据来源类型为手工添加 0 by leeyu BUG:MS00020 2008-11-24
    private int inOut = 0;                  //凋拨反向
    //-----------------为了在收益支付时可单独添加各自的子调拨 sj add 20080123---------//
    private ArrayList subTrans = null;
    private String sRecycled = "";

    //------ MS00141 QDV4交银施罗德2009年01月4日02_B sj modified ---------//
    private String relaOrderNum = ""; //关联数据排序编号。
    
    private String sCashAccCode = ""; //20130221 added by liubo.Story #3414

    public String getCashAccCode() {
		return sCashAccCode;
	}

	public void setCashAccCode(String sCashAccCode) {
		this.sCashAccCode = sCashAccCode;
	}

	public String getRelaOrderNum() {
        return relaOrderNum;
    }

    public void setRelaOrderNum(String sRelaOrderNum) {
        relaOrderNum = sRelaOrderNum;
    }

    //------------------------------------------------------------------//
    
    
    

    public void setSubTrans(ArrayList list) {
        this.subTrans = list;
    }

    public java.util.Date getDtTransferEndDate() {
		return dtTransferEndDate;
	}

	public void setDtTransferEndDate(java.util.Date dtTransferEndDate) {
		this.dtTransferEndDate = dtTransferEndDate;
	}

	public java.util.Date getDtTransEndDate() {
		return dtTransEndDate;
	}

	public void setDtTransEndDate(java.util.Date dtTransEndDate) {
		this.dtTransEndDate = dtTransEndDate;
	}

	public ArrayList getSubTrans() {
        return this.subTrans;
    }

    //---------------------------------------------------------------------------
    public void setFRelaNum(String FRelaNum) { // wdy add
        this.FRelaNum = FRelaNum;
    }

    public void setFNumType(String FNumType) { // wdy add
        this.FNumType = FNumType;
    }

    public void setFIPRNum(String FIPRNum) {
        this.FIPRNum = FIPRNum;
    }

    public void setCheckStateId(int checkStateId) {
        this.checkStateId = checkStateId;
    }

    public void setStrTradeNum(String strTradeNum) {
        this.strTradeNum = strTradeNum;
    }

    public void setFilterType(TransferBean filterType) {
        this.filterType = filterType;
    }

    public void setStrTsfTypeName(String strTsfTypeName) {
        this.strTsfTypeName = strTsfTypeName;
    }

    public void setStrTransferTime(String strTransferTime) {
        this.strTransferTime = strTransferTime;
    }

    public void setStrTsfTypeCode(String strTsfTypeCode) {
        this.strTsfTypeCode = strTsfTypeCode;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrOldNum(String strOldNum) {
        this.strOldNum = strOldNum;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public void setStrSubTsfTypeName(String strSubTsfTypeName) {
        this.strSubTsfTypeName = strSubTsfTypeName;
    }

    public void setDtTransDate(java.util.Date dtTransDate) {
        this.dtTransDate = dtTransDate;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setStrAttrClsCode(String strAttrClsCode) {
        this.strAttrClsCode = strAttrClsCode;
    }

    public void setStrAttrClsName(String strAttrClsName) {
        this.strAttrClsName = strAttrClsName;
    }

    public void setStrNum(String strNum) {
        this.strNum = strNum;
    }

    public void setDtTransferDate(java.util.Date dtTransferDate) {
        this.dtTransferDate = dtTransferDate;
    }

    public void setStrIsOnlyColumns(String strIsOnlyColumns) {
        this.strIsOnlyColumns = strIsOnlyColumns;
    }

    public void setStrTansferSet(String strTansferSet) {
        this.strTansferSet = strTansferSet;
    }

    public void setStrSubTsfTypeCode(String strSubTsfTypeCode) {
        this.strSubTsfTypeCode = strSubTsfTypeCode;
    }

    public void setSrcCashAccName(String srcCashAccName) {
        this.srcCashAccName = srcCashAccName;
    }

    public void setSrcCashAccCode(String srcCashAccCode) {
        this.srcCashAccCode = srcCashAccCode;
    }

    public void setCprNum(String cprNum) {
        this.cprNum = cprNum;
    }

    public void setSavingNum(String savingNum) {
        this.savingNum = savingNum;
    }

    public void setDataSource(int dataSource) {
        this.dataSource = dataSource;
    }

    public void setRateTradeNum(String rateTradeNum) {
        this.rateTradeNum = rateTradeNum;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public String getFIPRNum() {
        return FIPRNum;
    }

    public String getStrTradeNum() {
        return strTradeNum;
    }

    public TransferBean getFilterType() {
        return filterType;
    }

    public String getStrTsfTypeName() {
        return strTsfTypeName;
    }

    public String getStrTransferTime() {
        return strTransferTime;
    }

    public String getStrTsfTypeCode() {
        return strTsfTypeCode;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public String getStrOldNum() {
        return strOldNum;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    public String getStrSubTsfTypeName() {
        return strSubTsfTypeName;
    }

    public java.util.Date getDtTransDate() {
        return dtTransDate;
    }

    public String getStrSecurityName() {
        return strSecurityName;
    }

    public String getStrAttrClsCode() {
        return strAttrClsCode;
    }

    public String getStrAttrClsName() {
        return strAttrClsName;
    }

    public String getStrNum() {
        return strNum;
    }

    public java.util.Date getDtTransferDate() {
        return dtTransferDate;
    }

    public String getStrIsOnlyColumns() {
        return strIsOnlyColumns;
    }

    public String getStrTansferSet() {
        return strTansferSet;
    }

    public String getStrSubTsfTypeCode() {
        return strSubTsfTypeCode;
    }

    public String getSrcCashAccName() {
        return srcCashAccName;
    }

    public String getSrcCashAccCode() {
        return srcCashAccCode;
    }

    public String getFRelaNum() { // wdy add
        return FRelaNum;
    }

    public String getFNumType() { // wdy add
        return FNumType;
    }

    public String getCprNum() {
        return cprNum;
    }

    public String getSavingNum() {
        return savingNum;
    }

    public int getCheckStateId() {
        return checkStateId;
    }

    public int getDataSource() {
        return dataSource;
    }

    public String getRateTradeNum() {
        return rateTradeNum;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public String getStrPortName() {
        return strPortName;
    }

    public TransferBean() {
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
                if (sRowStr.split("\r\t").length == 3) {
                    this.strTansferSet = sRowStr.split("\r\t")[2];
                }

            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sTmpStr;
            reqAry = sTmpStr.split("\t");
            this.strNum = reqAry[0];
            this.strTsfTypeCode = reqAry[1];
            this.strSubTsfTypeCode = reqAry[2];
            this.strAttrClsCode = reqAry[3];
            this.strSecurityCode = reqAry[4];
            this.dtTransferDate = YssFun.toDate(reqAry[5]);
            this.dtTransferEndDate = YssFun.toDate(reqAry[6]); //add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围
            this.strTransferTime = reqAry[7];
            this.dtTransDate = YssFun.toDate(reqAry[8]);
            this.dtTransEndDate = YssFun.toDate(reqAry[9]);//add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围
            this.strTradeNum = reqAry[10];
            this.strDesc = reqAry[11];
            this.strIsOnlyColumns = reqAry[12];
            this.checkStateId = Integer.parseInt(reqAry[13]);
            this.strOldNum = reqAry[14];
            this.strDesc = reqAry[15];
            if (reqAry[16].trim().length() == 0) {
                this.srcCashAccCode = " ";
            }
            this.srcCashAccCode = reqAry[16];
            this.srcCashAccName = reqAry[17];
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new TransferBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析现金调拨请求信息出错\r\n" + e.getMessage(), e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strNum).append("\t");
        buf.append(this.strTsfTypeCode).append("\t");
        buf.append(this.strTsfTypeName).append("\t");
        buf.append(this.strSubTsfTypeCode).append("\t");
        buf.append(this.strSubTsfTypeName).append("\t");
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        buf.append(this.strSecurityCode).append("\t");
        buf.append(this.strSecurityName).append("\t");
        buf.append(YssFun.formatDate(this.dtTransferDate)).append("\t");
        buf.append(this.strTransferTime).append("\t");
        buf.append(YssFun.formatDate(this.dtTransDate)).append("\t");
        buf.append(this.strTradeNum).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(this.srcCashAccCode).append("\t");
        buf.append(this.srcCashAccName).append("\t");
        //------ add by wangzuochun 2010.02.23  MS00922  资金调拨和综合业务界面没有判断组合的权限  QDV4赢时胜上海2010年01月12日01_B    
        buf.append(this.strPortCode).append("\t");
        //--------------- MS00922 ----------------//
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting(boolean bAutoCommit) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        this.strNum = "C" + YssFun.formatDate(this.dtTransferDate, "yyyyMMdd") +
            dbFun.getNextDataInnerCode();

        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
                "(FNum,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode,FTransferDate,FTransferTime,FTradeNum,FTransDate," +
                //------ 20090420 MS00395 QDV4海富通2009年04月20日01_AB --
                "FRelaNum," +
                "FNumType," + //增加编号类型
                //------------------
                //--- MS00411 QDV4赢时胜（上海）2009年4月24日01_B sj--
                "FRateTradeNum," + //增加换汇编号
                //-------------------------------------------------
                "FSAVINGNUM," + //xuqiji 20090526:QDV4赢时胜（上海）2009年5月5日02_B  MS00437    进行存款业务处理时产生的资金调拨会重复
                "FSecurityCode,FDATASOURCE,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FSrcCashAcc)" +
                " values(" + dbl.sqlString(this.strNum) + "," +
                dbl.sqlString(this.strTsfTypeCode) + "," +
                dbl.sqlString(this.strSubTsfTypeCode) + "," +
                dbl.sqlString(this.strAttrClsCode) + "," +
                dbl.sqlDate(this.dtTransferDate) + "," +
                dbl.sqlString(this.strTransferTime) + "," +
                dbl.sqlString(this.strTradeNum) + "," +
                dbl.sqlDate(this.dtTransDate) + "," +
                //--- 20090420 MS00395 QDV4海富通2009年04月20日01_AB -----
                dbl.sqlString(this.FRelaNum) + "," + //将关联编号的值赋于
                dbl.sqlString(this.FNumType) + "," + //增加编号类型的值
                //-----------------------------------
                //--- MS00411 QDV4赢时胜（上海）2009年4月24日01_B sj--
                dbl.sqlString(this.rateTradeNum) + "," + //增加换汇编号
                //-------------------------------------------------
                dbl.sqlString(this.savingNum) + "," + //xuqiji 20090526:QDV4赢时胜（上海）2009年5月5日02_B  MS00437    进行存款业务处理时产生的资金调拨会重复
                dbl.sqlString(this.strSecurityCode) + "," +
                dataSource + "," + //因为这里是手动加的，所以这里的标识为手动处理  by leeyu BUG:MS00020 2008-11-24
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(pub.getUserCode()) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                dbl.sqlString(this.checkUserCode) + "," +
                dbl.sqlString(this.checkTime) + "," +
                dbl.sqlString(this.srcCashAccCode) +
                ")";
            if (!bAutoCommit) {
                conn.setAutoCommit(false);
                bTrans = true;
            }
            dbl.executeSql(strSql);
            if (this.strTansferSet != null && this.strTansferSet.length() != 0) {
                TransferSetBean tansferset = new TransferSetBean();
                tansferset.setYssPub(pub);
                tansferset.setSCashAccCode(this.sCashAccCode);	//20130221 added by liubo.Story #3414
                //MS00319 QDV4华夏2009年3月16日01_B 增加旧编号的参数 oldStrNum -------------------
                tansferset.saveMutliOperData(this.strTansferSet, true, this.strNum, strOldNum); //若复制、修改时oldNum有值
                //----------------------------------------------------------------------------
            }

            if (!bAutoCommit) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return null;
        } catch (Exception e) {
            throw new YssException("新增资金调拨息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String addSetting() throws YssException {
        return addSetting(false);
    }

    /**
     * 获取资金调拨的其它信息
     * @throws YssException
     * 20090420 MS00395 QDV4海富通2009年04月20日01_AB
     */
    private void getRelaInfo() throws YssException {
        String strSql = "";
        ResultSet relaRs = null; //获取其他信息的ResultSet,如关联编号
        strSql = "select * from " + pub.yssGetTableName("Tb_Cash_Transfer") +
            " where FNum =" + dbl.sqlString(strOldNum);
        try {
            relaRs = dbl.openResultSet(strSql);
            if (relaRs.next()) {
                this.FRelaNum = relaRs.getString("FRelaNum") == null ? "" : relaRs.getString("FRelaNum"); //将关联编号赋值
                this.FNumType = relaRs.getString("FNumType") == null ? "" : relaRs.getString("FNumType"); //编号类型
                // MS00411 QDV4赢时胜（上海）2009年4月24日01_B  增加获取换汇编号的获取 -------------------------------------------
                this.rateTradeNum = relaRs.getString("FRateTradeNum") == null ? "" : relaRs.getString("FRateTradeNum"); //换汇编号
                this.dataSource = relaRs.getInt("FDataSource"); //新要求获取数据来源;
                //----------------------------------------------------------------------------------------------------------
                //xuqiji 20090526:QDV4赢时胜（上海）2009年5月5日02_B  MS00437    进行存款业务处理时产生的资金调拨会重复---
                this.savingNum = relaRs.getString("FSAVINGNUM") == null ? "" : relaRs.getString("FSAVINGNUM");
                //------------------------------------end--------------------------------------------------------//
            }
        } catch (Exception ex) {
            throw new YssException("获取资金调拨关联编号出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(relaRs);
        }
    }

    public String editSetting() throws YssException {

        String strSql = "";
        ResultSet rs = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "select count(*) from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FNum = " +
                dbl.sqlString(strOldNum);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                int iRow = rs.getInt(1);
                if (iRow > 0) {
                    //--- MS00395 QDV4海富通2009年04月20日01_AB 20090420  -------
                    getRelaInfo(); //获取资金调拨的其它信息
                    //---------------------------------------------------------
                    strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                        " where FNum =" + dbl.sqlString(strOldNum);
                    dbl.executeSql(strSql);
                }
            }
            this.addSetting();

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            return null;
        } catch (Exception e) {
            throw new YssException("修改资金调拨信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void delSetting() throws YssException {

        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String arrData[] = null;    //存储每一条要删除的数据
        try {
            //=======Start MS00554:QDV4建行2009年6月26日01_AB  增加批量删除功能====
            arrData = sRecycled.split("\r\n");
            //先根据原始数据进行删除，然后再
            for (int i = 0; i <= arrData.length; i ++ ) {
                strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FNum = " +
                    dbl.sqlString(this.strNum);
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                strSql = "update " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FNum = " +
                    dbl.sqlString(this.strNum);
                dbl.executeSql(strSql);
                //解析数据
                if(i!=arrData.length){
                    if (arrData[i].trim().length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                }
            }
            //================ End MS00554 sunkey@Modify 20090827 ==============
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("删除资金调拨信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void checkSetting() throws YssException {

        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String[] arrData = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled.trim().length() == 0) {
                strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    "where FNum=" + dbl.sqlString(this.strNum);
                dbl.executeSql(strSql);
                //审核调拨子表
                strSql = "update " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    "where FNum=" + dbl.sqlString(this.strNum);
                dbl.executeSql(strSql);
            } else {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].trim().length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        "where FNum=" + dbl.sqlString(this.strNum);
                    dbl.executeSql(strSql);
                    //审核调拨子表
                    strSql = "update " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        "where FNum=" + dbl.sqlString(this.strNum);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核资金调拨信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String saveMutliOperData(String sMutilRowStr) throws YssException {
        return "";
    }

    public void getOperData() throws YssException {

    }

    public String builderListViewData(String strSql) throws YssException {
        return builderListViewData(strSql, 1);
    }

    public String builderListViewData(String strSql, int ilistViewIndex) throws YssException {
        String sHeader = "", sShowCols = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            if (ilistViewIndex == 1) {
                sHeader = this.getListView1Headers();
                sShowCols = this.getListView1ShowCols();
            } else {
                sHeader = this.getListView3Headers();
                sShowCols = this.getListView3ShowCols();
            }
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
          //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            //add by yangheng 20100820 MS01310
            if (this.filterType!=null&&this.filterType.strIsOnlyColumns.equals("1")&&!(pub.isBrown())) {	//20111027 modified by liubo.STORY #1285.
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr();// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji

            }
            //--------------------------------------end MS01310--------------------------------------------------------
			// QDV4赢时胜上海2010年03月18日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("Transfer");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月18日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, sShowCols)).
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr();// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji

        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }

    }

    public String getListViewData1() throws YssException {
    	//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
		RightBean right = new RightBean();
		right.setYssPub(pub);
		//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
        String strSql = "";
        strSql = "select y.* from " +
            "(select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") +
            " group by FNum,FCheckState) x join" + // by ly 去掉FcheckState<>2 因为前台的回收站要用

            "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName as FtsfTypeName, " +
            "e.FSubTsfTypeName as FsubtsfTypeName,f.FAttrClsName as FattrClsName,g.FSecurityName as FSecurityName, " +
            " gg.FCashAccCode as FCashAccCode,gg.FCashAccName as FCashAccName, kk.FPortCode as FPortCode " + //------ modify by wangzuochun 2010.02.23  MS00922  资金调拨和综合业务界面没有判断组合的权限  QDV4赢时胜上海2010年01月12日01_B
            " from " + pub.yssGetTableName("Tb_Cash_Transfer") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FTsfTypeCode,FTsfTypeName from  Tb_Base_TransferType where FCheckState = 1) d on a.FTsfTypeCode = d.FTsfTypeCode" +

            " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType where FCheckState = 1) e on a.FSubTsfTypeCode = e.FSubTsfTypeCode" +

            " left join (select FAttrClsCode,FAttrClsName from " +
            pub.yssGetTableName("Tb_Para_AttributeClass") +
            " where FCheckState = 1) f on a.FAttrClsCode= f.FAttrClsCode" +

            " left join (select ef.*, eg.fsecurityname from (select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") + " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) ef join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") eg on ef.FSecurityCode = eg.FSecurityCode and ef.FStartDate = eg.FStartDate) g on a.FSecurityCode = g.FSecurityCode " +
            " left join (select FCashAccCode,FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") + ") gg on gg.FCashAccCode=a.FSrcCashAcc " +
            //------ add by wangzuochun 2010.02.23  MS00922  资金调拨和综合业务界面没有判断组合的权限  QDV4赢时胜上海2010年01月12日01_B 
            " left join (select FNum,Fportcode from " + pub.yssGetTableName("Tb_Cash_SubTransfer") + 
            " group by FNum,FPortcode) kk on a.fnum = kk.fnum "+
            //------------------- MS00922 ---------------------//
          //--modify by 黄啟荣 2011-06-01 story #937 --用于系统查询出来的结果必须与用户的浏览权限一致。
			buildFilterSql()+
            ") y on x.FNum = y.FNum " +
            " join (select FPortcode from "+pub.yssGetTableName("tb_para_portfolio")+" where fcheckstate = 1) p on p.fportcode = y.FPortCode " //modify by wangzuochun 2010.11.15 BUG #358 浏览划款指令产生的资金调拨数据时，提示【无权限】。 
//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
//            +" where FPortcode in (select distinct(tsu.fportcode) as fportcode from(select fportcode from Tb_Sys_Userright"
//	        		+" where fusercode ="+dbl.sqlString(pub.getUserCode())					    
//				    +" and frighttype = 'port'"
//				    +" and FOPERTYPES like '%brow%'"
//				    +" and frightcode = 'cashtransfer') tsu"
//				    +" inner join "+pub.yssGetTableName("tb_Para_Portfolio")
//				    +" tpp on tpp.fportcode=tsu.fportcode"
//				    +" where tpp.fenabled=1"
//				    +" and tpp.FCheckState=1)";
//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
			//add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B
			//edit by songjie 2011.07.26 BUG 2308 QDV4博时2011年07月26日01_B
            + " where y.FPortcode in (" + operSql.sqlCodes(right.getUserPortCodes("cashtransfer")) + ")";
			//---end---	 
            strSql+=" order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);

    }

    public String getListViewData2() throws YssException {

        return "";

    }

    public String getListViewData3() throws YssException {
        String strSql = "";
        String sAry[] = null;
        //edit by rujiangpeng 20100504 MS01116 QDV4工银2010年04月22日01_B (FNum&FDesc列重复，创建视图会报错)
        try {
        	//------ modify by wangzuochun 2011.01.27 BUG #994 资金调拨，查询时，选择显示级别为调拨子类型，查询时出错 
        	//------ 由于资金调拨子表增加了所属分类字段FAttrClsCode，主表子表关联后造成FAttrClsCode列重复，创建视图会报错
            strSql =
                "select a.*,tsf.*,case when a.Finout=1 then '流入' else '流出' end as FInOutName, b.fusername as fcreatorname, c.fusername as fcheckusername, d.FPortName, e.FCashAccName,f1.FAttrClsName ";
            strSql = strSql +
                ( (this.getCashStorageAnalysisSql().trim().length() == 0) ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ");

            strSql = strSql + " from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") + " a " +
                " join (select t1.*,d1.FTsfTypeName,e1.FSubTsfTypeName,g1.fsecurityname from " +
                " (select FNum as FNum1,FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransferTime,FTransDate, " +
                " FTradeNum,FSecurityCode,FDesc as FDesc1 from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                "  ) t1 " + // 去掉FCheckState<>2 因为前台的回收站要用到
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType where FCheckState = 1) d1 " +
                " on t1.FTsfTypeCode = d1.FTsfTypeCode left join (select FSubTsfTypeCode,FSubTsfTypeName  from " +
                " Tb_Base_SubTransferType where FCheckState = 1) e1 on t1.FSubTsfTypeCode = e1.FSubTsfTypeCode " +
                
                " left join (select ef.*, eg.fsecurityname from " +
                " (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") + " where " +
                " FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ef " +
                " join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount from " +
                pub.yssGetTableName("Tb_Para_Security") + ") eg " +
                " on ef.FSecurityCode = eg.FSecurityCode and ef.FStartDate = eg.FStartDate) g1 on t1.FSecurityCode = g1.FSecurityCode " +

                ") tsf on a.FNum=tsf.FNum1 " +
                //------ modify by wangzuochun 2011.01.27 BUG #994 资金调拨，查询时，选择显示级别为调拨子类型，查询时出错 
                " left join (select FAttrClsCode, FAttrClsName from " + pub.yssGetTableName("Tb_Para_AttributeClass") +
                " where FCheckState = 1) f1 on a.FAttrClsCode = f1.FAttrClsCode " + 
                //----------------------------------BUG #994--------------------------------//
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                //-----------------------------------------------------------------------------------------------
                " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from " +
                //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Portfolio") + " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                "(select FPortCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 and FASSETGROUPCODE = " +
//                dbl.sqlString(pub.getAssetGroupCode()) +
//                " group by FPortCode) p " +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                " ) d on a.FPortCode = d.FPortCode" +
                //-------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                " select FCashAccCode, FCashAccName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                " where FCheckState = 1) e on a.FCashAccCode = e.FCashAccCode " +
                //-----------------------------------------------------------------------------------------------

                this.getCashStorageAnalysisSql() +
                buildSubFilterSql() +
                " order by a.FNum desc, a.FInOut desc ";

            return this.builderListViewData(strSql, 3);
        } catch (Exception e) {
            throw new YssException("获取资金调拨信息出错\r\n" + e.getMessage(), e);
        }

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

    /**
     * 子调拨筛选条件
     * @return String
     */
    private String buildSubFilterSql() throws YssException {
        String sResult = " where 1=1 ";
        TransferSetBean trset = null;
        if (this.filterType != null) {
            if (this.strTansferSet.length() != 0) {
                trset = new TransferSetBean();
                trset.setYssPub(pub);
                trset.parseRowStr(this.strTansferSet);
                //edit by licai 20101216 BUG #661 资金调拨分页浏览时不能显示调拨子编号。
                if (trset.getSPortCode().length() != 0) {
                    sResult = sResult + " and a.FPortCode like '" +
                        trset.getSPortCode().replaceAll("'", "''") + "%'";
                }
                if (trset.getSCashAccCode().trim().length() != 0) {
                    sResult = sResult + " and a.FCashAccCode like '" +
                        trset.getSCashAccCode().replaceAll("'", "''") + "%'";
                }
              //edit by licai 20101216 BUG #661=================================end
            }
            if (this.filterType.dtTransferDate != null &&
                !YssFun.formatDate(this.filterType.dtTransferDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and tsf.FTransferDate = " +
                    dbl.sqlDate(filterType.dtTransferDate);
            }
            if (this.filterType.dtTransDate != null &&
                !YssFun.formatDate(this.filterType.dtTransDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and FTransDate = " +
                    dbl.sqlDate(filterType.dtTransDate);
            }

            if (this.filterType.strTsfTypeCode.length() != 0) {
                sResult = sResult + " and tsf.FTsfTypeCode like '" +
                    filterType.strTsfTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strSubTsfTypeCode.length() != 0) {
                sResult = sResult + " and tsf.FSubTsfTypeCode like '" +
                    filterType.strSubTsfTypeCode.replaceAll("'", "''") +
                    "%'";
            }

            if (this.filterType.strAttrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.strAttrClsCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strSecurityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.strSecurityCode.replaceAll("'", "''") + "%'";
            }
            //edit by licai 20101216 BUG #661 资金调拨分页浏览时不能显示调拨子编号。
            if (trset!=null&&trset.getIInOut() != 99) { //彭鹏 2008.03.10 BUG0000025 资金调拨列表查询问题
                sResult = sResult + " and a.FInOut = " +
                    trset.getIInOut();
            }
        }
        return sResult;
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        //20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return " where 1=1";
    	//=============end=================
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strIsOnlyColumns.equals("1")&&pub.isBrown()==false) {	//20111027 modified by liubo.STORY #1285. 
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.filterType.srcCashAccCode.trim().length() != 0) {
                sResult = sResult + " and a.FSrcCashAcc like '" +
                    this.filterType.srcCashAccCode.replaceAll("'", "''") + "%'";
            }
            //---delete by zhaoxianlin 20130115 STORY #3441 ---start--//
//            if (this.filterType.dtTransferDate != null &&
//                !YssFun.formatDate(this.filterType.dtTransferDate).equals(
//                    "9998-12-31")) {
//                sResult = sResult + " and a.FTransferDate = " +
//                    dbl.sqlDate(filterType.dtTransferDate);
//            }
//            if (this.filterType.dtTransDate != null &&
//                !YssFun.formatDate(this.filterType.dtTransDate).equals(
//                    "9998-12-31")) {
//                sResult = sResult + " and a.FTransDate = " +
//                    dbl.sqlDate(filterType.dtTransDate);
//            }
            //---delete by zhaoxianlin 20130115 STORY #3441 ---end--//
          //----add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围---start---//
            if (this.filterType.dtTransferDate != null &&
                    !YssFun.formatDate(this.filterType.dtTransferDate).equals(
                        "9998-12-31")) {
            	         if(this.filterType.dtTransferEndDate != null &&
            	                    !YssFun.formatDate(this.filterType.dtTransferEndDate).equals(
                                    "9998-12-31")){
            	        	 sResult = sResult + " and a.FTransferDate between " +
                             dbl.sqlDate(filterType.dtTransferDate)+" and "+dbl.sqlDate(filterType.dtTransferEndDate);
            	        	 
            	         }else{
            	        	 sResult = sResult + " and a.FTransferDate = " +
                             dbl.sqlDate(filterType.dtTransferDate);
            	         }
                   
                }
            if (this.filterType.dtTransDate != null &&
                    !YssFun.formatDate(this.filterType.dtTransDate).equals(
                        "9998-12-31")) {
            	         if(this.filterType.dtTransEndDate != null &&
            	                    !YssFun.formatDate(this.filterType.dtTransEndDate).equals(
                                    "9998-12-31")){
            	        	 sResult = sResult + " and a.FTransDate between " +
                             dbl.sqlDate(filterType.dtTransDate)+" and "+dbl.sqlDate(filterType.dtTransEndDate);
            	        	 
            	         }else{
            	        	 sResult = sResult + " and a.FTransDate = " +
                             dbl.sqlDate(filterType.dtTransDate);
            	         }
                }
          //----add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围---end---//
            if (this.filterType.strTsfTypeCode.length() != 0) {
                sResult = sResult + " and a.FTsfTypeCode like '" +
                    filterType.strTsfTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strSubTsfTypeCode.length() != 0) {
                sResult = sResult + " and a.FSubTsfTypeCode like '" +
                    filterType.strSubTsfTypeCode.replaceAll("'", "''") +
                    "%'";
            }

            if (this.filterType.strAttrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.strAttrClsCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strSecurityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.strSecurityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strNum.trim().length() != 0) { //若用到资金调拨主表的编号集里用 by ly 080203
                if (filterType.strNum.indexOf(",") > 0) {
                    sResult += " and a.FNum in(" + operSql.sqlCodes(filterType.strNum) + ") ";
                } else {
                    sResult += " and a.FNum like'" + filterType.strNum.replaceAll("'", "''") + "%' ";
                }
            }

            if (this.strTansferSet.length() != 0) {
                //获取资金调拨子表的主编号
                TransferSetBean tfSetBean = new TransferSetBean();
                tfSetBean.setYssPub(pub);
                tfSetBean.parseRowStr(this.strTansferSet);
                sResult = sResult + " and a.FNum in (select distinct FNum from " +//xuqiji 20100409 MS01069 QDV4工银2010年04月07日01_B 资金调拨界面，查询报错      
                    pub.yssGetTableName("Tb_Cash_SubTransfer") + " where 1=1";
                if (tfSetBean.getSPortCode().trim().length() > 0) {
                    sResult = sResult + " and FPortCode = " +
                        dbl.sqlString(tfSetBean.getSPortCode().trim());
                }
                if (tfSetBean.getSCashAccCode().trim().length() > 0) {
                    sResult = sResult + " and FCashAccCode = " +
                        dbl.sqlString(tfSetBean.getSCashAccCode().trim());
                }
                if (tfSetBean.getIInOut() != 99) {
                    sResult = sResult + " and FInOut = " + tfSetBean.getIInOut();
                }
                if (tfSetBean.getSAnalysisCode1().trim().length() > 0) {
                    sResult = sResult + " and FAnalysisCode1 = " +
                        dbl.sqlString(tfSetBean.getSAnalysisCode1().trim());
                }
                if (tfSetBean.getSAnalysisCode2().trim().length() > 0) {
                    sResult = sResult + " and FAnalysisCode2 = " +
                        dbl.sqlString(tfSetBean.getSAnalysisCode2().trim());
                }
                sResult = sResult + " ) ";
            }

        }
        return sResult;
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException,
        YssException {
        this.strNum = rs.getString("FNum");
        this.strTsfTypeCode = rs.getString("FTsfTypeCode");
        this.strTsfTypeName = rs.getString("FTsfTypeName");
        this.strSubTsfTypeCode = rs.getString("FSubTsfTypeCode");
        this.strSubTsfTypeName = rs.getString("FSubTsfTypeName");
        this.strAttrClsCode = rs.getString("FAttrClsCode");
        this.strAttrClsName = rs.getString("FAttrClsName");
        this.strSecurityCode = rs.getString("FSecurityCode");
        this.strSecurityName = rs.getString("FSecurityName");

        this.dtTransferDate = rs.getDate("FTransferDate");
        this.strTransferTime = rs.getString("FTransferTime");
        this.strTradeNum = rs.getString("FTradeNum");
        this.dtTransDate = rs.getDate("FTransDate");
        this.strDesc = rs.getString("FDesc");
        this.srcCashAccCode = rs.getString("FCashAccCode");
        this.srcCashAccName = rs.getString("FCashAccName");
        
        if(dbl.isFieldExist(rs, "FPortCode")){ // add by jiangshichao  Swift查看资金调拨数据不会有FPortCode字段，所以这里进行判断
        //------ add by wangzuochun 2010.02.23  MS00922  资金调拨和综合业务界面没有判断组合的权限  QDV4赢时胜上海2010年01月12日01_B  
        this.strPortCode = rs.getString("FPortCode");
        //---------------- MS00922 -----------------//
        }
        super.setRecLog(rs);
    }

    public String getOperValue(String sType) throws YssException {

        double dCashAccBalance = 0;
        if(sType.equalsIgnoreCase("getCashTransferInfo")){//SWIFT 导出数据使用 by 李道龙 20091120
        	return this.getCashTransferInfo();
        }
        
        return YssFun.formatNumber(dCashAccBalance, "#,##0.00");

    }
	/**
	**SWIFT 导出数据使用 by 李道龙 20091120
	*/
	private String getCashTransferInfo() throws YssException{
		ResultSet rs=null;
		String strSql = "";
	    strSql = "select y.* from " +
	        "(select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") +
	        " group by FNum,FCheckState) x join" + 

	        "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName as FtsfTypeName, " +
	        "e.FSubTsfTypeName as FsubtsfTypeName,f.FAttrClsName as FattrClsName,g.FSecurityName as FSecurityName, " +
	        " gg.FCashAccCode as FCashAccCode,gg.FCashAccName as FCashAccName " +
	        " from " + pub.yssGetTableName("Tb_Cash_Transfer") + " a " +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	        " left join (select FTsfTypeCode,FTsfTypeName from  Tb_Base_TransferType where FCheckState = 1) d on a.FTsfTypeCode = d.FTsfTypeCode" +

	        " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType where FCheckState = 1) e on a.FSubTsfTypeCode = e.FSubTsfTypeCode" +

	        " left join (select FAttrClsCode,FAttrClsName from " +
	        pub.yssGetTableName("Tb_Para_AttributeClass") +
	        " where FCheckState = 1) f on a.FAttrClsCode= f.FAttrClsCode" +

	        " left join (select ef.*, eg.fsecurityname from (select FSecurityCode, max(FStartDate) as FStartDate from " +
	        pub.yssGetTableName("Tb_Para_Security") + " where FStartDate <= " +
	        dbl.sqlDate(new java.util.Date()) +
	        " and FCheckState = 1 group by FSecurityCode) ef join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount from " +
	        pub.yssGetTableName("Tb_Para_Security") +
	        ") eg on ef.FSecurityCode = eg.FSecurityCode and ef.FStartDate = eg.FStartDate) g on a.FSecurityCode = g.FSecurityCode " +
	        " left join (select FCashAccCode,FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") + ") gg on gg.FCashAccCode=a.FSrcCashAcc " +
	        ") y on x.FNum = y.FNum " +
	        " where y.FNum ="+dbl.sqlString(strNum)+
	        " order by y.FCheckState, y.FCreateTime desc";
	    try {
			rs =dbl.openResultSet(strSql);
			if(rs.next()){
				this.setResultSetAttr(rs);
			}
		
			return this.buildRowStr();
		} catch (Exception ex) {
			throw new YssException(ex.getMessage(),ex);
		}	
		//---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
		finally{
			dbl.closeResultSetFinal(rs);
		}
		//---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
	}
	
    private String getCashStorageAnalysisSql() throws YssException, SQLException {
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
                        //------ modify by wangzuochun 2010.12.02 BUG #539 资金调拨，选择按照调拨子编号查询时，会提示报错 
                        " left join (select FCatCode,FCatName as FAnalysisName" + i + " from Tb_Base_Category where FCheckState = 1) category on a.FAnalysisCode" +
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
                
                    // end by lidaolong
                }

                else {
                	//------ 改为select空格，否则生成分页table的时候报错  modify by wangzuochun 2010.12.02 BUG #539 资金调拨，选择按照调拨子编号查询时，会提示报错 
                    sResult = sResult +
                        " left join (select ' ' as FAnalysisNull , ' ' as FAnalysisName" +
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                        i + ".FAnalysisNull ";
                    //-----------------------------BUG #539---------------------------//
                }
            }
        }
        dbl.closeResultSetFinal(rs);//QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-07-09 关闭结果集
        return sResult;
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
    public String getBeforeEditData() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        TransferBean beforeEditBean = new TransferBean();
        //--------//MS00226 QDV4华宝兴业2009年2月4日01_B ---------
        TransferSetBean logTransferset = new TransferSetBean();
        TransferSetBean filter = new TransferSetBean();
        String transferLogInfo = "";
        //------------------------------------------------------
        try {
            strSql = "select y.* from " +
                "(select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FCheckState <> 2 group by FNum,FCheckState) x join" +

                "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName as FtsfTypeName," +
                "e.FSubTsfTypeName as FsubtsfTypeName,f.FAttrClsName as FattrClsName,g.FSecurityName as FSecurityName " +
                " from " + pub.yssGetTableName("Tb_Cash_Transfer") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FTsfTypeCode,FTsfTypeName from  Tb_Base_TransferType where FCheckState = 1) d on a.FTsfTypeCode = d.FTsfTypeCode" +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType where FCheckState = 1) e on a.FSubTsfTypeCode = e.FSubTsfTypeCode" +

                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                " where FCheckState = 1) f on a.FAttrClsCode= f.FAttrClsCode" +

                " left join (select ef.*, eg.fsecurityname from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ef join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eg on ef.FSecurityCode = eg.FSecurityCode and ef.FStartDate = eg.FStartDate) g on a.FSecurityCode = g.FSecurityCode " +
                //       buildFilterSql() +
                " where FNum=" + dbl.sqlString(this.strOldNum) +
                ") y on x.FNum = y.FNum " +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strNum = rs.getString("FNum");
                //-------------------------------------------------------------------------
                beforeEditBean.strNum = rs.getString("FNum"); //MS00226 QDV4华宝兴业2009年2月4日01_B
                //-------------------------------------------------------------------------
                beforeEditBean.strTsfTypeCode = rs.getString("FTsfTypeCode");
                beforeEditBean.strTsfTypeName = rs.getString("FTsfTypeName");
                beforeEditBean.strSubTsfTypeCode = rs.getString("FSubTsfTypeCode");
                beforeEditBean.strSubTsfTypeName = rs.getString("FSubTsfTypeName");
                beforeEditBean.strAttrClsCode = rs.getString("FAttrClsCode");
                beforeEditBean.strAttrClsName = rs.getString("FAttrClsName");
                beforeEditBean.strSecurityCode = rs.getString("FSecurityCode");
                beforeEditBean.strSecurityName = rs.getString("FSecurityName");

                beforeEditBean.dtTransferDate = rs.getDate("FTransferDate");
                beforeEditBean.strTransferTime = rs.getString("FTransferTime");
                beforeEditBean.strTradeNum = rs.getString("FTradeNum");
                beforeEditBean.dtTransDate = rs.getDate("FTransDate");
                beforeEditBean.strDesc = rs.getString("FDesc");

            }
            //MS00226 QDV4华宝兴业2009年2月4日01_B -----------------------------------------------------------------------------
            logTransferset.setYssPub(pub);
            logTransferset.setSNum(beforeEditBean.strNum);
            filter.setSNum(beforeEditBean.strNum); //设置资金调拨的编号
            logTransferset.setFilterType(filter);
            transferLogInfo = logTransferset.getListViewData1(); //获取资金子调拨的数据
            if (transferLogInfo.indexOf("\f\f") > 0) {
                transferLogInfo = transferLogInfo.replaceAll("\f\f", "\bsubset\b"); //将不同的资金子调拨数据的分割符进行转换，避免在查询日志时出现错误的解析。
            }
            return beforeEditBean.buildRowStr() + "\f\b\f\b\f\b" + transferLogInfo;
            //-----------------------------------------------------------------------------------------------------------------
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String[] arrData = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].trim().length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FNum=" + dbl.sqlString(this.strNum);
                dbl.executeSql(strSql);
                //审核调拨子表
                strSql = "delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " where FNum=" + dbl.sqlString(this.strNum);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除资金调拨信息出错\r\n" + e.getMessage(), e);
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
