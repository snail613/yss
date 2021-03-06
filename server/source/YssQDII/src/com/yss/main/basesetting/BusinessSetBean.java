package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * MS00007
 * QDV4.1赢时胜上海2009年2月1日06_A
 * 用于增删改查业务设置信息
 * create by 宋洁
 * 2009-03-02
 */
public class BusinessSetBean
    extends BaseDataSettingBean implements
    IDataSetting {
    private String businessCode = ""; //业务设置代码
    private String businessName = ""; //业务设置名称
    private String tsfTypeCode = ""; //调拨类型代码
    private String subTsfTypeCode = ""; //调拨子类型代码
    private String dataFlow = ""; //数据流
    private String show = ""; //是否显示分页
    private String desc = ""; //描述
    private String isOnlyColumns = "0"; //是否只读取列名
    private String oldBusinessSetCode = ""; //修改用业务设置代码
    private String transferTypeSet = ""; //调拨类型设置
    private BusinessSetBean filterType; //筛选类型
    private String sRecycled = ""; //删除信息
    private String allBusinessData = ""; //所有业务设置信息
    private String allFilterData = ""; //所有查询信息
    /**
     * 构造函数
     */
    public BusinessSetBean() {
    }

    /**
     * 获取分页显示信息
     * @return String
     */
    public String getShow() {
        return show;
    }

    /**
     * 设置分页显示信息
     * @param show String
     */
    public void setShow(String show) {
        this.show = show;
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
     * 获取描述
     * @return String
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 设置描述
     * @param desc String
     */
    public void setDesc(String desc) {
        this.desc = desc;
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
     * 获取老的业务设置代码
     * @return String
     */
    public String getOldBusinessSetCode() {
        return oldBusinessSetCode;
    }

    /**
     * 设置老的业务设置代码
     * @param oldBusinessSetCode String
     */
    public void setOldBusinessSetCode(String oldBusinessSetCode) {
        this.oldBusinessSetCode = oldBusinessSetCode;
    }

    /**
     * 获取查询类型
     * @return BusinessSetBean
     */
    public BusinessSetBean getFilterType() {
        return filterType;
    }

    /**
     * 设置查询类型
     * @param filterType BusinessSetBean
     */
    public void setFilterType(BusinessSetBean filterType) {
        this.filterType = filterType;
    }

    /**
     * 获取调拨类型设置
     * @return String
     */
    public String getTransferTypeSet() {
        return transferTypeSet;
    }

    /**
     * 设置调拨类型设置
     * @param transferTypeSet String
     */
    public void setTransferTypeSet(String transferTypeSet) {
        this.transferTypeSet = transferTypeSet;
    }

    /**
     * 解析前台传的字符串
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException { //sRowStr表示要解析的信息
        String[] allData; //用于拆分所有业务设置信息
        sRecycled = sRowStr; //用于获取删除，清除信息
        String[] arrData = null; //用于拆分成明细信息
        try {
            if (sRowStr.equals("")) { //若为空，则返回
                return;
            }
            allData = sRowStr.split("\r\t"); //拆分所有业务设置信息
            allBusinessData = allData[0]; //获取第一条业务设置信息
            this.businessCode = (allBusinessData.split("\f\f")[0]).split("\t")[
                0]; //获取业务设置代码
            this.oldBusinessSetCode = (allBusinessData.split("\f\f")[0]).split(
                "\t")[6]; //获取老的业务设置代码
            if (allBusinessData.indexOf("\f\f") == -1) { //若为单条业务设置信息
                arrData = allBusinessData.split("\t"); //则拆分成明细信息
                if (arrData.length >= 8) {
                    this.businessCode = arrData[0]; //获取业务设置代码
                    this.businessName = arrData[1]; //获取业务设置名称
                    this.dataFlow = arrData[2]; //获取数据流
                    this.show = arrData[3]; //获取分页显示信息
                    this.desc = arrData[4]; //获取描述信息
                    if (arrData[5].equals("0")) {
                        this.checkStateId = 0; //获取审核信息
                    } else if (arrData[5].equals("1")) {
                        this.checkStateId = 1;
                    } else if (arrData[5].equals("2")) {
                        this.checkStateId = 2;
                    }
                    this.oldBusinessSetCode = arrData[6]; //获取老的业务设置信息
                    this.isOnlyColumns = arrData[7]; //获取是否只显示列名的属性
                    super.parseRecLog();
                }
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new BusinessSetBean(); //新建查询类型
                    this.filterType.setYssPub(pub);
                }
                if (allData.length >= 3) {
                    allFilterData = allData[2]; //获取查询类型
                    this.filterType.parseRowStr(allFilterData); //解析查询信息
                }
            }
        } catch (Exception e) {
            throw new YssException("解析业务设置请求信息出错", e);
        }
    }

    /**
     * 获取数据字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.businessCode).append("\t"); //拼接业务设置代码
        buf.append(this.businessName).append("\t"); //拼接业务设置名称
        buf.append(this.dataFlow).append("\t"); //拼接数据流
        buf.append(this.show).append("\t"); //拼接分页显示信息
        buf.append(this.desc).append("\t"); //拼接描述信息
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 检查业务设置数据是否合法
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException { //btOper表示操作类型
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Base_BusinessSet"),
                               "FBusinessTypeCode", this.businessCode,
                               this.oldBusinessSetCode); //判断业务设置是否唯一
    }

    /**
     * 拼接where语句
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = " where 1=1 ";
        if (this.isOnlyColumns.equalsIgnoreCase("1")) {
            sResult = sResult + " and 1=2 ";
            return sResult;
        }
        try {
            if (this.filterType != null) {

                if (this.filterType.businessCode.length() != 0) {
                    sResult = sResult + " and a.FBUSINESSTYPECode='" +
                        filterType.businessCode.replaceAll("'", "''") + //以业务设置代码为查询条件
                        "'";
                }
                if (this.filterType.businessName.length() != 0) {
                    sResult = sResult + " and a.FBUSINESSTYPENAME='" +
                        filterType.businessName.replaceAll("'", "''") + //以业务设置名称为查询条件
                        "'";
                }
                if (this.filterType.desc.length() != 0) {
                    sResult = sResult + " and a.FDESC='" +
                        filterType.desc.replaceAll("'", "''") + //以描述信息为查询条件
                        "'";
                }
            }
        } catch (Exception e) {
            throw new YssException("业务设置数据出错", e);
        }
        return sResult;
    }

    /**
     * getAllSetting
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * 用于拼接显示到listView中的字符串
     * @param strSql String
     * @return String
     * @throws YssException
     */

    /**
     * 获取业务设置列表中的所有业务设置信息
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String strSql = ""; //声明sql语句
        String sHeader = ""; //声明列名信息
        String sAllDataStr = ""; //声明所有数据
        String showInfo = ""; //声明显示信息
        String columnName =
            "FBUSINESSTYPECODE\tFBUSINESSTYPENAME\tFDESC\tFCREATOR"; //数据库中的列名
        String showData = ""; //显示信息
        ResultSet rs = null; //结果集
        StringBuffer buf = new StringBuffer();
        StringBuffer buf1 = new StringBuffer();
        try {
            sHeader = "业务类型代码\t业务类型名称\t描述说明\t制作人"; //显示在业务设置列表上的列名
            BusinessSetBean businessSet = new BusinessSetBean(); //新建业务设置实例
            businessSet.setYssPub(pub);
            strSql = "select distinct a.FBUSINESSTYPECODE,a.FBUSINESSTYPENAME,a.FDESC,a.FCREATOR,a.FCHECKSTATE,a.fcheckuser," +
                "a.FCheckTime,a.FCreateTime,b.fusername as fcreatorname, c.fusername as fcheckusername from Tb_Base_BusinessSet a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode " +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode " +
                buildFilterSql() + " order by FBUSINESSTYPECODE desc "; //查询所有业务设置信息
            rs = dbl.openResultSet(strSql); //得到结果集
            while (rs.next()) {
                buf.append( (rs.getString("FBUSINESSTYPECODE") + "").trim()); //拼接业务设置代码
                buf.append("\t");
                buf.append( (rs.getString("FBUSINESSTYPENAME") + "").trim()); //拼接业务设置名称
                buf.append("\t");
                buf.append( (rs.getString("FDESC") + "").trim()); //拼接描述信息
                buf.append("\t");
                buf.append( (rs.getString("FCREATOR") + "").trim()); //拼接创建人ID
                buf.append(YssCons.YSS_LINESPLITMARK); //用"\f\f"分割
                setResultSetAttr(rs); //给属性赋值

                buf1.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                showData = buf1.toString().substring(0,
                    buf1.toString().length() - 2); //获取显示信息
                String[] subShowData = showData.split("\f\f"); //拆分显示信息
                String[] showDatas = null; //声明显示信息
                for (int i = 0; i < subShowData.length; i++) {
                    showDatas = subShowData[i].split("\t"); //拆分成明细信息
                    showInfo += showDatas[0] + "\t" + showDatas[1] + "\t" +
                        showDatas[4] + "\t" + showDatas[7]; //获取业务设置代码，业务设置名称，描述，创建人信息
                    if (i != subShowData.length - 1) {
                        showInfo += "\f\f"; //拼接显示信息
                    }
                }
            }
            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2); //获取所有信息
            }
            return sHeader + "\r\f" + showInfo + "\r\f" + sAllDataStr +
                "\r\f" + columnName;
        } catch (Exception e) {
            throw new YssException("获取业务设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取业务设置数据(业务设置代码，业务设置名称)
     * @return String
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        String sHeader = ""; //声明列名信息
        String sShowDataStr = ""; //声明显示信息
        String strSql = ""; //声明sql语句
        ResultSet rs = null; //声明结果集
        String sAllDataStr = ""; //声明所有信息
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "业务类型代码\t业务类型名称";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_Base_BusinessSet a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FCheckState = 1 order by a.FBusinessTypeCode, a.FCheckState, a.FCreateTime desc"; //查询所有已审核的业务设置信息

            rs = dbl.openResultSet(strSql); //得到结果集
            bufShow = new StringBuffer();
            bufAll = new StringBuffer();
            while (rs.next()) {
                bufShow.append( (rs.getString("FBusinessTypeCode") + "").trim()). //拼接业务设置代码
                    append(
                        "\t");
                bufShow.append( (rs.getString("FBusinessTypeName") + "").trim()). //拼接业务设置名称
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.businessCode = rs.getString("FBusinessTypeCode") + ""; //设置业务设置代码
                this.businessName = rs.getString("FBusinessTypeName") + ""; //设置业务设置名称
                this.desc = rs.getString("FDesc"); //设置描述信息
                this.dataFlow = rs.getString("FDataFlow") + ""; //设置数据流信息
                this.show = rs.getString("FShow") + ""; //设置分页显示信息
                this.checkStateId = rs.getInt("FCheckState"); //设置审核状态
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState")); //设置审核状态
                this.creatorCode = rs.getString("FCreator") + ""; //设置创建人
                this.creatorTime = rs.getString("FCreateTime") + ""; //设置创建时间
                this.checkUserCode = rs.getString("FCheckUser") + ""; //设置审核人
                this.checkTime = rs.getString("FCheckTime") + ""; //设置审核时间
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2); //获取显示信息
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2); //获取所有信息
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用交易类型数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * @return String
     * @throws YssException
     */
    public String getListViewData3() throws YssException {
        return "";
    }

    /**
     * 获取业务设置的全部数据
     * @return String
     * @throws YssException
     */
    public String getListViewData4() throws YssException {
        return "";
    }

    public IDataSetting getSetting() {
        return null;
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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * 添加业务设置信息
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String strSql = ""; //声明sql语句
        String createTime = ""; //声明创建时间
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //得到链接
        String[] businessData = null; //用于拆分业务设置信息
        try {
            conn.setAutoCommit(false); //设置自动提交为false
            bTrans = true;
            if (allBusinessData.indexOf("\f\f") > 0) { //若有多个业务设置信息
                businessData = allBusinessData.split("\f\f"); //分割业务设置信息
                for (int i = 0; i < businessData.length; i++) {
                    parseRowStr(businessData[i]); //解析业务设置信息
                    if (i == 0) {
                        createTime = this.creatorTime; //获取创建时间
                    }
                    strSql = "insert into Tb_Base_BusinessSet " +
                        "(FBusinessTypeCode,FBusinessTypeName,FDataFlow,FShow,FDESC," +
                        "FCheckState,FCreator,FCreateTime,FCheckUser) " +
                        " values(";
                    strSql += dbl.sqlString(this.businessCode.trim()) + "," +
                        dbl.sqlString(this.businessName.trim()) + "," +
                        dbl.sqlString(this.dataFlow) + "," +
                        Integer.parseInt(this.show) + "," +
                        dbl.sqlString(this.desc) + "," +
                        (pub.getSysCheckState() ? "0" : "1") + "," +
                        dbl.sqlString(this.creatorCode) + "," +
                        dbl.sqlString(createTime) + "," +
                        (pub.getSysCheckState() ? "' '" :
                         dbl.sqlString(this.creatorCode)) + ")";
                    dbl.executeSql(strSql); //添加业务设置信息到业务设置表中
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加估值方法信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * 修改业务设置信息
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = ""; //声明sql语句
        String createTime = ""; //声明创建时间
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //得到链接
        String[] businessData = null; //用于拆分业务设置信息
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (allBusinessData.indexOf("\f\f") > 0) { //若有多个业务设置信息
                businessData = allBusinessData.split("\f\f"); //分割业务设置信息
                for (int i = 0; i < businessData.length; i++) {
                    parseRowStr(businessData[i]); //解析业务设置信息
                    if (i == 0) {
                        createTime = this.creatorTime; //获取创建时间
                    }
                    strSql = "update Tb_Base_BusinessSet set " +
                        " FBusinessTypeCode =" +
                        dbl.sqlString(this.businessCode.trim()) +
                        ",FBusinessTypeName =" +
                        dbl.sqlString(this.businessName.trim()) +
                        ",FDataFlow =" + dbl.sqlString(this.dataFlow) +
                        ",FShow =" + Integer.parseInt(this.show) +
                        ",FDESC =" + dbl.sqlString(this.desc) +
                        ",FCheckState =" +
                        (pub.getSysCheckState() ? "0" : "1") +
                        ",FCreator =" + dbl.sqlString(this.creatorCode) +
                        ",FCreateTime =" + dbl.sqlString(createTime) +
                        ",FCheckUser =" +
                        (pub.getSysCheckState() ? "' '" :
                         dbl.sqlString(this.creatorCode)) +
                        " where FbusinessTypeCode =" +
                        dbl.sqlString(this.oldBusinessSetCode) +
                        " and FDataFlow=" + dbl.sqlString(this.dataFlow);
                    dbl.executeSql(strSql); //更新业务设置表中相关业务设置代码的信息
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改业务设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * 删除业务设置信息
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = ""; //声明sql语句
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //获取一个链接
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update tb_base_businessSet" +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FBusinessTypeCode = " +
                dbl.sqlString(this.businessCode);
            dbl.executeSql(strSql); //更新相关业务设置代码的审核状态为2

            strSql = "update " + pub.yssGetTableName("Tb_base_SubBusinessSet") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FBusinessTypeCode = " +
                dbl.sqlString(this.businessCode);
            dbl.executeSql(strSql); //更新相关业务设置代码的调拨类型设置信息的审核状态为2
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除业务设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 修改业务设置审核状态
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = ""; //声明sql语句
        String[] businessData = null; //用于拆分业务设置信息
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //获取一个数据库链接
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (!sRecycled.equals("")) { //爬满短删除信息是否为空
                businessData = sRecycled.split("\r\n"); //若不为空，则用"\r\n"分割
                for (int i = 0; i < businessData.length; i++) {
                    parseRowStr(businessData[i]); //解析业务设置信息
                    strSql = "update Tb_base_businessSet " +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = '" +
                        pub.getUserCode() +
                        "', FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FBusinessTypeCode = " +
                        dbl.sqlString(this.businessCode);
                    dbl.executeSql(strSql); //更新业务设置表中相关业务设置代码的审核状态

                    strSql = "update Tb_base_SubBusinessSet " +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "'" +
                        " where FBusinessTypeCode = " +
                        dbl.sqlString(this.businessCode);
                    dbl.executeSql(strSql); //更新相关业务设置代码的调拨类型设置信息的审核状态
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核业务设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * getOperValue
     * 获取分页显示信息
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException { //sType表示业务设置代码
        String strSql = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            strSql =
                "select * from TB_BASE_BUSINESSSET where FBusinessTypeCode='" +
                sType + "' order by FDataFlow desc";
            rs = dbl.openResultSet(strSql);
            int i = 0;
            while (rs.next()) {
                buf.append( (rs.getString("FDATAFLOW") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FSHOW") + "").trim());
                if (i != 4) {
                    buf.append(YssCons.YSS_LINESPLITMARK);
                }
                i++;
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("获取业务设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 得到修改前的业务设置信息
     * @return String
     * @throws YssException
     */
    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     * 清除业务设置信息
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //声明sql语句
        String[] arrData = null; //用于拆分要删除的业务设置信息
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //获取一个连接
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
                    this.parseRowStr(arrData[i]); //解析业务设置信息
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_base_businessSet") +
                        " where FBusinessTypeCode = " +
                        dbl.sqlString(this.businessCode);
                    dbl.executeSql(strSql); //删除业务设置表中有关相关的业务设置代码的所有信息

                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Base_SubBusinessSet") +
                        " where FBusinessTypeCode = " +
                        dbl.sqlString(this.businessCode);
                    dbl.executeSql(strSql); //删除业务设置子表中有关相关的业务设置代码的所有信息
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

    public void setResultSetAttr(ResultSet rs) throws SQLException, //rs表示结果集
        YssException {
        this.businessCode = rs.getString("FBUSINESSTYPECODE") + ""; //设置业务设置代码
        this.businessName = rs.getString("FBUSINESSTYPENAME") + ""; //设置业务设置名称
        this.desc = rs.getString("FDESC") + ""; //设置描述信息
        this.creatorName = rs.getString("FCREATOR") + ""; //设置创建人
        this.creatorTime = rs.getString("FCREATETIME"); //设置创建时间
        super.setRecLog(rs);
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
