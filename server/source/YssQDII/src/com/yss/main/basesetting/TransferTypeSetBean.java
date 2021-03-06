package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * MS00007
 * QDV4.1赢时胜上海2009年2月1日06_A
 * 用于保存业务设置子表的调拨类型设置信息
 * create by 宋洁
 * 2009-03-02
 * @author not attributable
 * @version 1.0
 */
public class TransferTypeSetBean
    extends BaseDataSettingBean implements
    IDataSetting {
    /**
     * 构造函数
     */
    public TransferTypeSetBean() {

    }

    private String businessCode = ""; //业务设置代码
    private String businessName = ""; //业务设置名称
    private String dataFlow = ""; //数据流
    private String tsfTypeCode = ""; //调拨类型代码
    private String tsfTypeName = ""; //调拨类型名称
    private String subTsfTypeCode = ""; //调拨子类型代码
    private String subTsfTypeName = ""; //调拨子类型名称
    private String isOnlyColumns = ""; //是否只显示列名
    private String sMutilRowStr = "";

    private TransferTypeSetBean filterType; //查询类型
    /**
     * 获取业务设置名称
     * @return String
     */
    public String getBusinessName() {
        return businessName;
    }

    /**
     * 设置业务设置名称
     * @param businessName String
     */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    /**
     * 获取业务设置代码
     * @return String
     */
    public String getBusinessCode() {
        return businessCode;
    }

    /**
     * 设置业务设置代码
     * @param businessCode String
     */
    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    /**
     * 获取数据流
     * @return String
     */
    public String getDataFlow() {
        return dataFlow;
    }

    /**
     * 设置数据流
     * @param dataFlow String
     */
    public void setDataFlow(String dataFlow) {
        this.dataFlow = dataFlow;
    }

    /**
     * 获取调拨类型代码
     * @return String
     */
    public String getTsfTypeCode() {
        return tsfTypeCode;
    }

    /**
     * 设置调拨类型代码
     * @param tsfTypeCode String
     */
    public void setTsfTypeCode(String tsfTypeCode) {
        this.tsfTypeCode = tsfTypeCode;
    }

    /**
     * 获取调拨类型名称
     * @return String
     */
    public String getTsfTypeName() {
        return tsfTypeName;
    }

    /**
     * 设置调拨类型名称
     * @param tsfTypeName String
     */
    public void setTsfTypeName(String tsfTypeName) {
        this.tsfTypeName = tsfTypeName;
    }

    /**
     * 获取调拨子类型代码
     * @return String
     */
    public String getSubTsfTypeCode() {
        return subTsfTypeCode;
    }

    /**
     * 设置调拨子类型代码
     * @param subTsfTypeCode String
     */
    public void setSubTsfTypeCode(String subTsfTypeCode) {
        this.subTsfTypeCode = subTsfTypeCode;
    }

    /**
     * 获取调拨子类型名称
     * @return String
     */
    public String getSubTsfTypeName() {
        return subTsfTypeName;
    }

    /**
     * 设置调拨子类型名称
     * @param subTsfTypeName String
     */
    public void setSubTsfTypeName(String subTsfTypeName) {
        this.subTsfTypeName = subTsfTypeName;
    }

    /**
     * 获取是否只显示列名的属性
     * @return String
     */
    public String getIsOnlyColumns() {
        return isOnlyColumns;
    }

    /**
     * 设置是否只显示列名的属性
     * @param isOnlyColumns String
     */
    public void setIsOnlyColumns(String isOnlyColumns) {
        this.isOnlyColumns = isOnlyColumns;
    }

    /**
     * 获取查询类型
     * @return TransferTypeSetBean
     */
    public TransferTypeSetBean getFilterType() {
        return filterType;
    }

    /**
     * 设置查询类型
     * @param filterType TransferTypeSetBean
     */
    public void setFilterType(TransferTypeSetBean filterType) {
        this.filterType = filterType;
    }

    /**
     * 解析前台传过来的字符串信息，解析成具体的调拨类型设置信息
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException { //sRowStr表示要解析的信息
        String[] reqAry = null;
        String[] arrInfo = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) { //若为空则返回
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0]; //获取调拨类型设置信息
                arrInfo = sTmpStr.split("\f\f"); //拆分成多条调拨类型设置信息
                for (int i = 0; i < arrInfo.length; i++) {
                    if (arrInfo[i].equals("null")) {
                        continue;
                    } else {
                        sMutilRowStr = sTmpStr;
                    }
                }
            } else {
                sTmpStr = sRowStr;
                arrInfo = sTmpStr.split("\f\f"); //拆分成多条调拨类型设置信息
                for (int i = 0; i < arrInfo.length; i++) {
                    if (arrInfo[i].equals("null")) {
                        continue;
                    } else {
                        sMutilRowStr = sTmpStr;
                        reqAry = arrInfo[i].split("\t"); //拆分正明细信息
                        this.businessCode = reqAry[0]; //设置业务设置代码
                        this.dataFlow = reqAry[1]; //设置数据流
                        this.tsfTypeCode = reqAry[2]; //设置调拨类型代码
                        this.subTsfTypeCode = reqAry[3]; //设置调拨子类型代码
                        this.isOnlyColumns = reqAry[4]; //设置是否只显示列名的属性
                        super.checkStateId = Integer.parseInt(reqAry[5]); //设置审核状态
                        super.parseRecLog();
                    }
                }
            }

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TransferTypeSetBean(); //新建查询类型
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]); //解析查询类型
            }
        } catch (Exception e) {
            throw new YssException("解析估值方法筛选条件请求信息出错", e);
        }
    }

    /**
     * 将具体的调拨类型设置信息组成一个字符串
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.businessCode + "").append("\t"); //拼接业务设置代码
        buf.append(this.businessName + "").append("\t"); //拼接业务设置名称
        buf.append(this.dataFlow + "").append("\t"); //拼接数据流
        buf.append(this.tsfTypeCode + "").append("\t"); //拼接调拨类型代码
        buf.append(this.tsfTypeName + "").append("\t"); //拼接调拨类型名称
        buf.append(this.subTsfTypeCode + "").append("\t"); //拼接调拨子类型代码
        buf.append(this.subTsfTypeName + "").append("\t"); //拼接调拨子类型名称
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * 拼接证券成本，证券应收应付，资金调拨，现金应收应付，运营应收应付界面与业务设置代码相关的调拨类型信息
     * @return String
     * @throws YssException
     */
    public String buildTsfRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.tsfTypeCode + "").append("\t"); //拼接调拨类型代码
        buf.append(this.tsfTypeName + "").append("\t"); //拼接调拨类型名称
        buf.append(this.dataFlow + "").append("\t"); //拼接数据流
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 拼接证券成本，证券应收应付，资金调拨，现金应收应付，运营应收应付界面与业务设置代码相关的调拨子类型信息
     * @return String
     * @throws YssException
     */
    public String buildSubTsfRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.subTsfTypeCode + "").append("\t"); //拼接调拨子类型代码
        buf.append(this.subTsfTypeName + "").append("\t"); //拼接调拨子类型名称
        buf.append(this.tsfTypeCode + "").append("\t"); //拼接调拨类型代码
        buf.append(this.tsfTypeName + "").append("\t"); //拼接调拨类型名称
        buf.append(this.dataFlow + "").append("\t"); //拼接数据流
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    public void saveSetting(byte btOper) {
    }

    /**
     * getSetting
     *
     * @return IDaraSetting
     */
    public IDataSetting getSetting() {
        IDataSetting idata = null;
        return idata;
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
     * 用于返回业务设置的调拨类型设置页面要显示在listView中的调拨类型信息
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = ""; //声明sql语句
        String sHeader = ""; //声明列名信息
        String sShowDataStr = ""; //声明显示信息
        String sAllDataStr = ""; //声明所有信息
        ResultSet rs = null; //声明结果集
        StringBuffer buf = new StringBuffer();
        StringBuffer buf1 = new StringBuffer();
        try {
            sHeader = "调拨类型代码\t调拨类型名称\t调拨子类型代码\t调拨子类型名称";
            strSql = "select distinct a.*, e.FTsfTypeName as FTsfTypeName, f.FSubTsfTypeName as FSubTsfTypeName," +
                " d.FBusinessTypeName as FBusinessTypeName, b.fusername as FCreatorName, c.fusername as FCheckUserName " +
                " from Tb_Base_SubBusinessSet a " +
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) e " +
                " on a.FTsfTypeCode = e.FTsfTypeCode " +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) f " +
                " on a.FSubTsfTypeCode = f.FSubTsfTypeCode " +
                " left join (select FBusinessTypeCode,FBusinessTypeName from Tb_Base_BusinessSet) d " +
                " on a.fbusinesstypecode = d.fbusinesstypecode " +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                buildFilterSql() + " order by a.FBusinessTypeCode desc "; //查询所有业务设置子表中相关业务设置代码及分页信息的所有调拨类型信息

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append( (rs.getString("FTsfTypeCode") + "").trim()); //拼接调拨类型代码
                buf.append("\t");
                buf.append( (rs.getString("FTsfTypeName") + "").trim()); //拼接调拨类型名称
                buf.append("\t");
                buf.append( (rs.getString("FSubTsfTypeCode") + "").trim()); //拼接调拨子类型代码
                buf.append("\t");
                buf.append( (rs.getString("FSubTsfTypeName") + "").trim()); //拼接调拨子类型名称
                buf.append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs); //设置属性

                buf1.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2); //得到显示信息
            }

            if (buf1.toString().length() > 2) {
                if (this.tsfTypeCode == null && (this.tsfTypeCode.equals("null") ) 
                    && this.subTsfTypeCode == null && this.subTsfTypeCode.equals("null")) { //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                    sAllDataStr = ""; //得到所有信息
                } else {
                    sAllDataStr = buf1.toString().substring(0,
                        buf1.toString().length() - 2); //得到所有信息
                }
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取业务设置调拨类型信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData2
     * 获取所有相关业务设置代码的分页信息
     * @return String
     */
    public String getListViewData2() throws YssException {
        String strSql = "";
        String sShowDataStr = "";
        String sShowDataStr1 = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
		/**shashijie 2012-7-2 STORY 2475 */
        //StringBuffer buf1 = new StringBuffer();
		/**end*/
        try {
            strSql = "select * from tb_base_businessset where fcheckstate = 1 and fshow = 1 and fBusinessTypeCode='" +
                this.businessCode + "'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("fdataflow").equals("Sec")) {
                    buf.append("SecCost");
                } else if (rs.getString("fdataflow").equals("Cash")) {
                    buf.append("CashTransfer");
                } else {
                    buf.append( (rs.getString("fdataflow") + "").trim());
                }
                buf.append("\f\f");
            }
            if (buf.toString().equals("")) {
                return "allPageHide";
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }
            return sShowDataStr;
        } catch (Exception e) {
            throw new YssException("获取综合业务界面分页显示信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData3
     * 查询综合业务界面中打开的证券成本，证券应收应付，资金调拨，现金应收应付，运营应收应付界面与业务设置代码相关的已审核的调拨类型信息
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = ""; //声明列名信息
        String sShowDataStr = ""; //声明显示信息
        String strSql = ""; //声明sql语句
        ResultSet rs = null; //声明结果集
        String sAllDataStr = ""; //声明所有信息
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "调拨类型代码\t调拨类型名称"; //设置显示的列名
            //查询综合业务界面中打开的证券成本，证券应收应付，资金调拨，现金应收应付，运营应收应付界面与业务设置代码相关的已审核的调拨类型信息
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName from Tb_Base_SubBusinessSet a " +
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) d on a.FTsfTypeCode = d.FTsfTypeCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where FBusinessTypeCode = '" + this.businessCode +
                "' and FDataFlow = '" + this.dataFlow +
                "' and a.FCheckState = 1 order by a.FTsfTypeCode, a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql); //获取结果集
            bufShow = new StringBuffer();
            bufAll = new StringBuffer();
            while (rs.next()) {
                bufShow.append( (rs.getString("FTsfTypeCode") + "").trim()).append("\t"); //拼接调拨类型代码
                bufShow.append( (rs.getString("FTsfTypeName") + "").trim()).append(YssCons.YSS_LINESPLITMARK); //拼接调拨类型名称
                this.businessCode = rs.getString("FBusinessTypeCode") + ""; //设置业务设置代码
                this.tsfTypeCode = rs.getString("FTsfTypeCode") + ""; //设置调拨类型代码
                this.tsfTypeName = rs.getString("FTsfTypeName") + ""; //设置调拨类型名称
                this.dataFlow = rs.getString("FDataFlow") + ""; //设置数据流
                this.checkStateId = rs.getInt("FCheckState"); //设置审核状态
                this.checkStateName = YssFun.getCheckStateName(rs.getInt("FCheckState")); //设置审核状态
                this.creatorCode = rs.getString("FCreator") + ""; //设置创建人
                this.creatorTime = rs.getString("FCreateTime") + ""; //设置创建时间
                this.checkUserCode = rs.getString("FCheckUser") + ""; //设置审核人
                this.checkTime = rs.getString("FCheckTime") + ""; //设置审核时间
                bufAll.append(this.buildTsfRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2); //得到所有显示信息
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2); //得到所有信息
            }
            if (sShowDataStr.equals("")) { //若查不到任何信息
                TransferTypeBean tsfType = new TransferTypeBean(); //新建调拨类型实例
                tsfType.setYssPub(pub);
                return tsfType.getListViewData2(); //返回所有调拨类型表中的已审核信息
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用交易类型数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
     * getTreeViewData1
     * 查询综合业务界面中打开的证券成本，证券应收应付，资金调拨，现金应收应付，运营应收应付界面与业务设置代码相关的已审核的调拨子类型信息
     * @return String
     */
    public String getTreeViewData1() throws YssException {
        String sHeader = ""; //声明列名信息
        String sShowDataStr = ""; //声明显示信息
        String strSql = ""; //声明sql语句
        ResultSet rs = null; //声明结果集
        String sAllDataStr = ""; //声明所有信息
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "调拨子类型代码\t调拨子类型名称"; //设置显示的列名
            //查询综合业务界面中打开的证券成本，证券应收应付，资金调拨，现金应收应付，运营应收应付界面与业务设置代码相关的已审核的调拨子类型信息
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,m.FSubTsfTypeName,d.FTsfTypeName from " +
                " Tb_Base_SubBusinessSet a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) d on a.FTsfTypeCode=d.FTsfTypeCode " +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) m " +
                " on m.FSubTsfTypeCode = a.FSubTsfTypeCode" +
                " where FBusinessTypeCode = '" + this.businessCode +
                "' and FDataFlow = '" + this.dataFlow +
                (this.tsfTypeCode.equals("") ? "" :
                 "' and a.FTsfTypeCode = '" + this.tsfTypeCode) +
                "' and a.FCheckState = 1 order by a.FSubTsfTypeCode, a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql); //获取结果集
            bufShow = new StringBuffer();
            bufAll = new StringBuffer();
            while (rs.next()) {
                bufShow.append( (rs.getString("FSubTsfTypeCode") + "").trim()).append("\t"); //拼接调拨子类型代码
                bufShow.append( (rs.getString("FSubTsfTypeName") + "").trim()).append(YssCons.YSS_LINESPLITMARK); //拼接调拨子类型名称

                this.subTsfTypeCode = rs.getString("FSubTsfTypeCode") + ""; //设置调拨子类型代码
                this.subTsfTypeName = rs.getString("FSubTsfTypeName") + ""; //设置调拨子类型名称
                this.tsfTypeCode = rs.getString("FTsfTypeCode") + ""; //设置调拨类型代码
                this.tsfTypeName = rs.getString("FTsfTypeName") + ""; //设置调拨类型名称
                this.dataFlow = rs.getString("FDataFlow") + ""; //设置数据流信息

                super.setRecLog(rs);

                bufAll.append(this.buildSubTsfRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2); //获取显示信息
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2); //获取所有信息
            }
            if (sShowDataStr.equals("")) { //若没有查到信息
                SubTransferTypeBean subTsfType = new SubTransferTypeBean(); //新建调拨子类型实例
                SubTransferTypeBean filterType = new SubTransferTypeBean(); //新建查询实例
                filterType.setSuperTypeCode(this.tsfTypeCode); //设置调拨类型代码
                filterType.setTypeCode(""); //设置调拨子类型代码
                filterType.setTypeDesc(""); //设置描述
                filterType.setTypeName(""); //设置调拨子类型名称
                subTsfType.setFilterType(filterType); //设置查询实例
                subTsfType.setYssPub(pub);
                return subTsfType.getListViewData2(); //返回所有调拨子类型表中的已审核信息
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用调拨子类型数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
     * 存储调拨类型设置明细信息到调拨类型设置明细信息表中
     * @return String
     */
    public String addSetting() throws YssException {
        String[] sMutilRowAry = null; //用于拆分调拨类型设置信息
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection(); //得到一个数据库链接
        boolean bTrans = false;
        String strSql = ""; //声明sql语句
        String createtime = ""; //声明创建时间
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sMutilRowStr.equals("")) { //若为空则返回
                return "";
            }
            sMutilRowAry = sMutilRowStr.split("\f\f"); //拆分调拨类型设置信息
            for (int i = 0; i < sMutilRowAry.length; i++) {
                if (sMutilRowAry[i].equals("null")) {
                    continue;
                } else {
                    this.businessCode = sMutilRowAry[i].split("\t")[0]; //获取业务设置代码
                    break;
                }
            }

            strSql = "delete from " +
                pub.yssGetTableName("Tb_Base_SubBusinessSet") +
                " where FBusinessTypeCode=" +
                dbl.sqlString(this.businessCode); //删除所有与业务设置代码相关的调拨类型设置信息

            dbl.executeSql(strSql);

            strSql =
                "insert into Tb_Base_SubBusinessSet " +
                "(FBusinessTypeCode, FDataFlow, FTsfTypeCode, FSubTsfTypeCode,FCheckState,FCreator,FCreateTime)" +
                " values (?,?,?,?,?,?,?)"; //添加传进来的调拨类型设置信息
            pstmt = conn.prepareStatement(strSql);
            for (int i = 0; i < sMutilRowAry.length; i++) {
                if (i == 0) {
                    createtime = this.creatorTime; //获取创建时间
                }
                if (sMutilRowAry[i].equals("null")) {
                    continue;
                }
                if (sMutilRowAry[i].indexOf("~~") > 0) {
                    String[] tsfInfo = sMutilRowAry[i].split("~~"); //拆分调拨类型设置信息
                    for (int j = 0; j < tsfInfo.length; j++) {
                        this.parseRowStr(tsfInfo[j]); //解析调拨类型设置信息
                        pstmt.setString(1, this.businessCode); //设置业务设置代码
                        pstmt.setString(2, this.dataFlow); //设置数据流
                        if (this.tsfTypeCode.equals("")) {
                            pstmt.setString(3, " ");
                        } else {
                            pstmt.setString(3, this.tsfTypeCode); //设置调拨类型代码
                        }
                        if (this.subTsfTypeCode.equals("")) {
                            pstmt.setString(4, " ");
                        } else {
                            pstmt.setString(4, this.subTsfTypeCode); //设置调拨子类型代码
                        }
                        pstmt.setInt(5, (pub.getSysCheckState() ? 0 : 1)); //设置审核状态
                        pstmt.setString(6, this.creatorCode); //设置创始人
                        pstmt.setString(7, createtime); //设置创建时间
                        pstmt.executeUpdate();
                    }
                } else {
                    this.parseRowStr(sMutilRowAry[i]); //解析调拨类型设置信息
                    pstmt.setString(1, this.businessCode); //设置业务设置代码
                    pstmt.setString(2, this.dataFlow); //设置数据流
                    if (this.tsfTypeCode.equals("")) {
                        pstmt.setString(3, " ");
                    } else {
                        pstmt.setString(3, this.tsfTypeCode); //设置调拨类型代码
                    }
                    if (this.subTsfTypeCode.equals("")) {
                        pstmt.setString(4, " ");
                    } else {
                        pstmt.setString(4, this.subTsfTypeCode); //设置调拨子类型代码
                    }
                    pstmt.setInt(5, (pub.getSysCheckState() ? 0 : 1)); //设置审核状态
                    pstmt.setString(6, this.creatorCode); //设置创始人
                    pstmt.setString(7, createtime); //设置创建时间
                    pstmt.executeUpdate();
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";//modified by yeshenghong for CCB security check 20121018 
        } catch (Exception e) {
            throw new YssException("保存业务设置调拨类型信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting
     */
    public void delSetting() {
    }

    /**
     * editSetting
     * @return String
     */
    public String editSetting() {
        return "";
    }

    /**
     * getOperValue
     * 用于调用getTreeViewData1()
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        return getTreeViewData1();
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

    /**
     * 根据查询出的结果集为各个变量赋值
     */
    public void setResultSetAttr(ResultSet rs) throws SQLException, //rs表示结果集
        YssException {
        this.businessCode = rs.getString("FBusinessTypeCode") + ""; //业务设置代码
        this.dataFlow = rs.getString("FDataFlow") + ""; //数据流
        this.tsfTypeCode = rs.getString("FTsfTypeCode") + ""; //调拨类型代码
        this.tsfTypeName = rs.getString("FTsfTypeName") + ""; //调拨类型名称
        this.subTsfTypeCode = rs.getString("FSubTsfTypeCode") + ""; //调拨子类型代码
        this.subTsfTypeName = rs.getString("FSubTsfTypeName") + ""; //调拨子类型名称
        super.setRecLog(rs);
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
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * 用于编写sql语句 where 关键字后的条件语句
     */
    private String buildFilterSql() throws YssException {
        String subResult = "";
        if (this.filterType != null) {
            subResult = " where ";
            if (this.filterType.isOnlyColumns.equals("1")) { //若isOnlyColums = "1"则只返回列名
                subResult = subResult + " 1=2 ";
                return subResult;
            }
            if (this.filterType.businessCode.length() != 0) {
                subResult = subResult + " a.FBusinessTypeCode='" +
                    filterType.businessCode.replaceAll("'", "''") + "'"; //以业务设置代码为查询条件
            }
            if (this.filterType.dataFlow.length() != 0) {
                subResult = subResult + " and a.FDataFlow='" +
                    filterType.dataFlow.replaceAll("'", "''") + "'"; //以数据流为查询条件
            }
        }
        return subResult;
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
