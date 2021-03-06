package com.yss.main.syssetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 * <p>Title:数据表字典设置 </p>
 * <p>Description:数据表字典表bean </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company:ysstech.com </p>
 * @author pengjinggang
 * @version 1.0
 */
public class TableDictBean
    extends BaseDataSettingBean {
    //表字段
    private String sFTableCode = ""; //数据表代码
    private String sFTableName = ""; //数据表名称
    private String sFFieldCode = ""; //字段代码
    private String sFFieldName = ""; //字段名称
    private String sFFieldDesc = ""; //字段描述
    private String sFTableDesc = ""; //数据表描述
    private String sFFieldType = ""; //字段类型
    private String sSubData = ""; // 保存字段列表记录
    private String sOldTableName = "";
    private String sOldFieldName = "";
    private String sState = "";

    //字段属性
    private TableDictBean filterType;
    public TableDictBean() {
    }

    public TableDictBean(YssPub pub) {
        setYssPub(pub);
    }

    public void setState(String sState) {
        this.sState = sState;
    }

    public String getState() {
        return sState;
    }

    public void setOldFieldName(String OldFieldName) {
        this.sOldFieldName = OldFieldName;
    }

    public String getOldFieldName() {
        return sOldFieldName;
    }

    public void setOldTableName(String OldTableName) {
        this.sOldTableName = OldTableName;
    }

    public String getOldTableName() {
        return sOldTableName;
    }

    public void setSubData(String SubData) {
        this.sSubData = SubData;
    }

    public String getSubData() {
        return sSubData;
    }

    public void setStrFTableCode(String strFTableCode) {
        this.sFTableCode = strFTableCode;
    }

    public String getStrFTableCode() {
        return sFTableCode;
    }

    public void setStrFTableName(String strFTableName) {
        this.sFTableName = strFTableName;
    }

    public String getStrFTableName() {
        return sFTableName;
    }

    public void setStrFTableDesc(String strFTableDesc) {
        this.sFTableDesc = strFTableDesc;
    }

    public String getStrFTableDesc() {
        return sFTableDesc;
    }

    public void setStrFFieldCode(String strFFieldCode) {
        this.sFFieldCode = strFFieldCode;
    }

    public String getStrFFieldCode() {
        return sFFieldCode;
    }

    public void setFFieldName(String strFFieldName) {
        this.sFFieldName = strFFieldName;
    }

    public String getFFieldName() {
        return sFFieldName;
    }

    public void setsFFieldType(String sFFieldType) {
    	//edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
        if (sFFieldType.equals("0")) {
            sFFieldType = "string";
        //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
        } else if (sFFieldType.equals("1")) {
            sFFieldType = "number";
        //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
        } else if (sFFieldType.equals("2")) {
            sFFieldType = "date";
        }
        this.sFFieldType = sFFieldType;
    }

    public String getsFFieldType() {
    	//edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
        if (sFFieldType.equals("0")) {
            sFFieldType = "string";
        //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
        } else if (sFFieldType.equals("1")) {
            sFFieldType = "number";
        //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
        } else if (sFFieldType.equals("2")) {
            sFFieldType = "date";
        }
        return sFFieldType;
    }

    public void setStrFFieldDesc(String strFFieldDesc) {
        this.sFFieldDesc = strFFieldDesc;
    }

    public String getStrFFieldDesc() {
        return sFFieldDesc;
    }

    //获取所有数据字典
    public String getAllTableDict() throws YssException {
        String sql = "";
        try {
            sql = "select * from Tb_Fun_TableDict order by FTableCode, FFieldCode ";
            return buildSendStr(sql); //仅取自数据表字典
        } catch (Exception ex) {
            throw new YssException("获取所有数据字典信息出错", ex);
        }
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        String strSql = "", strTmp = "";
        if (btOper == YssCons.OP_ADD) {
            strSql =
                "select FFieldName from Tb_Fun_TableDict where FTableName = " +
                dbl.sqlString(this.sFTableName.trim()) + " and FFieldName = " +
                dbl.sqlString(this.sFFieldName.trim()) + "";
            strTmp = dbFun.GetValuebySql(strSql);
            if (strTmp.length() > 0) {
                throw new YssException("数据字典中表【" + this.sFTableName.trim() +
                                       "】的字段【" + this.sFFieldName.trim() +
                                       "】已经被占用，请重新输入");
            }
        } else if (btOper == YssCons.OP_EDIT) {
            if (!this.sFTableName.trim().equalsIgnoreCase(this.sOldTableName)) {
                strSql =
                    "select FTableName from Tb_Fun_TableDict where FTableName = " +
                    dbl.sqlString(this.sFTableName.trim());
                strTmp = dbFun.GetValuebySql(strSql);
                if (strTmp.length() > 0) {
                    throw new YssException("数据表字典中表【" + this.sFTableName.trim() +
                                           "】已经被占用，请重新输入");
                }
            } else if (!this.sFTableName.trim().equalsIgnoreCase(this.sOldTableName) ||
                       !this.sFFieldName.trim().equalsIgnoreCase(this.sOldFieldName)) {
                strSql =
                    "select FFieldName from Tb_Fun_TableDict where FTableName = " +
                    dbl.sqlString(this.sFTableName.trim()) + " and FFieldName = " +
                    dbl.sqlString(this.sFFieldName.trim()) + "";
                strTmp = dbFun.GetValuebySql(strSql);
                if (strTmp.length() > 0) {
                    throw new YssException("数据表字典中表【" + this.sFTableName.trim() +
                                           "】的字段【" + this.sFFieldName.trim() +
                                           "】已经被占用，请重新输入");
                }
            }
        }
    }

    //构建过滤，筛选串
    private String buildFilterSql() {
        String sResult = "";

        if (this.filterType != null) {
            sResult = " where 1=1 ";

            if (this.filterType.sFTableName.trim().length() != 0) {
                sResult = sResult + " and a.FTableName like '" +
                    filterType.sFTableName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sFTableDesc.trim().length() != 0) {
                sResult = sResult + " and a.FTableDesc like '" +
                    filterType.sFTableDesc.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.sFFieldName.trim().length() != 0) {
                sResult = sResult + " and a.FFieldName like '" +
                    filterType.sFFieldName.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.sFFieldDesc.trim().length() != 0) {
                sResult = sResult + " and a.FFieldDesc like '" +
                    filterType.sFFieldDesc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

//添加数据表
    public String addTableDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //YssDbFun fun = new YssDbFun(pub);//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        ResultSet rs = null;
        String sqlStr = "";
        boolean showError = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            sqlStr = "insert into Tb_Fun_TableDict" +
                "(FTableCode ,FTableName ,FFieldCode ,FFieldName ,FFieldDesc,FTableDesc ,FFieldType)" +
                " values(" +
                dbl.sqlString(this.sFTableCode) + "," +
                dbl.sqlString(this.sFTableName) + "," +
                dbl.sqlString(this.sFFieldCode) + "," +
                dbl.sqlString(this.sFFieldName) + "," +
                dbl.sqlString(this.sFFieldDesc) + "," +
                dbl.sqlString(this.sFTableDesc) + "," +
                dbl.sqlString(this.sFFieldType) + ")";
            dbl.executeSql(sqlStr); //根据解析得到数据插入到数据表字典
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sState.equalsIgnoreCase("1")) { //是否写入日志
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-数据表字典设置");
                sysdata.setStrCode(this.sFTableName);
                sysdata.setStrName(this.sFFieldName);
                sysdata.setStrUpdateSql(sqlStr);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            if (showError) {
                throw new YssException("增加数据字典设置出错", e);
            } else {
                throw new YssException(e.getMessage());
            }
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 此函数用于保存数据的添加与修改: 保存 1 主表 2 辅助表
     * @throws YssException
     * @return String
     */
    public String editMultTableDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] sArr = this.sSubData.split("\f\f"); //多行数据分解
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sArr.length > 0) { //如果有多行数据
                strSql = "delete from Tb_Fun_TableDict where FTableName  = " +
                    dbl.sqlString(this.sOldTableName);
                dbl.executeSql(strSql); //先删除原有数据,再插入新数据
                for (int i = 0; i < sArr.length; i++) {
                    this.protocolParse(sArr[i]);
                    this.addTableDict();
                }
                if (this.sState.equalsIgnoreCase("1")) { //是否写入日志
                    com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                    sysdata.setYssPub(pub);
                    sysdata.setStrAssetGroupCode("Common");
                    sysdata.setStrFunName("删除-数据字典设置");
                    sysdata.setStrCode(this.sOldTableName);
                    sysdata.setStrName(this.sFFieldName);
                    sysdata.setStrUpdateSql(strSql);
                    sysdata.setStrCreator(pub.getUserName());
                    sysdata.addSetting();
                }
            } else { //如果没有数据体
                this.addTableDict(); //保存只有表名与备注信息的表.
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改数据字典设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //修改数据表字典
    public String editTableDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update Tb_Fun_TableDict set " +
                " FFieldCode  = " + dbl.sqlString(this.sFFieldCode) +
                " FFieldName = " + dbl.sqlString(this.sFFieldName) +
                ",FTableDesc = " + dbl.sqlString(this.sFTableDesc) +
                ",FFieldDesc = " + dbl.sqlString(this.sFFieldDesc) +
                ",FFieldType = " + dbl.sqlString(this.sFFieldType) +
                " where FTableName  = " + dbl.sqlString(this.sOldTableName) +
                " and FFieldName = " + dbl.sqlString(this.sOldFieldName);
            System.out.println(strSql);
            dbl.executeSql(strSql);
            if (this.sState.equalsIgnoreCase("1")) { //是否写入日志
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-数据字典设置");
                sysdata.setStrCode(this.sOldTableName);
                sysdata.setStrName(this.sOldFieldName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改数据字典设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    //删除数据表字典里的字段
    public String delTableDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "delete from Tb_Fun_TableDict  where FTableName  = " +
                dbl.sqlString(this.sFTableName) + " and FFieldName = " +
                dbl.sqlString(this.sFFieldName);
            dbl.executeSql(strSql);//删除数据表里字典记录

            if (this.sState.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-数据字典设置");
                sysdata.setStrCode(this.sFTableName);
                sysdata.setStrName(this.sFFieldName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            conn.commit();
            bTrans = true;
            conn.setAutoCommit(true);
            return "true";
        } catch (Exception e) {
            throw new YssException("删除数据字典设置出错", e);
        }
    }
    //删除表的全部字段
    public String delAllTableDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from Tb_Fun_TableDict  where FTableName  = " +
                dbl.sqlString(this.sFTableName);//根据表名称删除所有字段
            dbl.executeSql(strSql);
            if (this.sState.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-数据字典设置");
                sysdata.setStrCode(this.sFTableName);
                sysdata.setStrName(this.sFFieldName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            conn.commit();
            bTrans = true;
            conn.setAutoCommit(true);
            return "true";
        } catch (Exception e) {
            throw new YssException("删除数据字典设置出错", e);
        }
    }
    //解析前台发来的数据串
    public void protocolParse(String sReq) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sReq.trim().length() == 0) {
                return;
            }
            if (sReq.indexOf("\r\t") >= 0) {
                sTmpStr = sReq.split("\r\t")[0];
                if (sReq.split("\r\t").length == 3) {
                    sSubData = sReq.split("\r\t")[2]; //第三组数据是字段数据
                }
            } else { //sReq.indexOf("\r\t") < 0时的，就是不存在\r\t时
                sTmpStr = sReq;
            }
            reqAry = sTmpStr.split("\t");
            if (reqAry.length >= 7) {
                this.sFTableCode = reqAry[0];
                if (reqAry[0].length() == 0) {
                    this.sFTableCode = " ";
                }
                this.sFTableName = reqAry[1];
                if (reqAry[1].length() == 0) {
                    this.sFTableName = " ";
                }
                this.sFTableDesc = reqAry[2];
                if (reqAry[2].length() == 0) {
                    this.sFTableDesc = " ";
                }
                this.sFFieldCode = reqAry[3];
                if (reqAry[3].length() == 0) {
                    this.sFFieldCode = " ";
                }
                this.sFFieldName = reqAry[4];
                if (reqAry[4].length() == 0) {
                    this.sFFieldName = " ";
                }
                this.sFFieldType = reqAry[5];
                if (reqAry[5].length() == 0) {
                    this.sFFieldType = " ";
                }
                this.sFFieldDesc = reqAry[6];
                if (reqAry[6].length() == 0) {
                    this.sFFieldDesc = "  ";
                }
                this.sOldTableName = reqAry[8];
                if (reqAry[8].length() == 0) {
                    this.sFFieldName = "  ";
                }
                this.sFFieldCode = reqAry[3];
                this.sFFieldName = reqAry[4];
                this.sFFieldType = reqAry[5];

            }
            super.parseRecLog();
            if (sReq.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TableDictBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sReq.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.protocolParse(sReq.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析数据字典出错", e);
        }
    }
    //获取要显示到listView控件上数据
    public String getListViewData3() throws YssException {
        String sHeader = ""; //中文表头
        String sShowDataStr = ""; //根据表名仅取得的"表名\t表描\t字名\t字描"数据串
        String sAllDataStr = ""; //全码串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String strSql = "";
        ResultSet rs = null;
        try {
            sHeader = "系统表名称\t系统表描述\t系统表字段名称\t系统表字段描述";
            strSql = "select * from Tb_Fun_TableDict a" +
                " where a.FTableName =" + dbl.sqlString(this.sFTableName) +
                " order by a.FTableName , a.FFieldName";
            System.out.println(strSql);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FTableName ")).append("\t"); //表名
                bufShow.append(rs.getString("FTableDesc")).append("\t"); //表描
                bufShow.append(rs.getString("FFieldName")).append("\t"); //字名
                bufShow.append(rs.getString("FFieldDesc")).append(YssCons. //字描
                    YSS_LINESPLITMARK);
                this.sFTableName = rs.getString("FTabName") + "";
                this.sFTableDesc = rs.getString("FTableDesc") + "";
                this.sFFieldName = rs.getString("FFieldName") + "";
                this.sFFieldDesc = rs.getString("FFieldDesc") + "";
                this.checkStateId = 1; //审核状态
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\fFTableName\tFTableDesc\tFFieldName\tFFieldDesc";
        } catch (Exception e) {
            throw new YssException("获取数据字典数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    //获取要显示到listView控件上数据集
    public String getListViewData() throws YssException {
        String sVocStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String strSql = "";
        ResultSet rs = null;
        try {
            sHeader = "系统表代码\t系统表名称\t系统表描述";
            strSql = "select distinct a.FTableCode,a.FTableName,a.FTableDesc from Tb_Fun_TableDict a" +
                buildFilterSql() +
                " order by FTableName ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FTableCode")).append("\t");
                bufShow.append(rs.getString("FTableName")).append("\t");
                bufShow.append(rs.getString("FTableDesc")).append(YssCons.
                    YSS_LINESPLITMARK);

                this.sFTableCode = rs.getString("FTableCode") + "";
                this.sFTableName = rs.getString("FTableName") + "";
                this.sFFieldDesc = rs.getString("FTableDesc") + "";

                this.checkStateId = 1; //审核状态
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
            sVocStr = vocabulary.getVoc("Sys_FieldType" + ",");

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\fFTableCode\tFTableName\tFTableDesc" + "\r\f" +
                "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取数据字典数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    //获取要显示到listView控件上数据
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String strSql = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "字段代码\t字段名称\t字段类型\t字段描述";
            strSql = "select DISTINCT FTableName,FTableDesc from Tb_Fun_TableDict group by FTableName,FTableDesc order by FTableName,FTableDesc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FTableName")).append("\t");
                bufShow.append(rs.getString("FTableDesc") +
                               "").append(YssCons.YSS_LINESPLITMARK);

                this.sFTableName = rs.getString("FTableName") + "";
                this.sFFieldName = "";
                this.sFTableDesc = rs.getString("FTableDesc") + "";
                this.sFFieldDesc = "";
                buf.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (buf.toString().length() > 2) {
                sAllDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取数据字典数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    //构建返回发送到前台的数据集串
    public String buildRowStr() { //构行串
        StringBuffer buf = new StringBuffer();
        buf.append(this.sFTableCode.trim()).append("\t");
        buf.append(this.sFTableName.trim()).append("\t");
        buf.append(this.sFTableDesc.trim()).append("\t");
        buf.append(this.sFFieldCode.trim()).append("\t");
        buf.append(this.sFFieldName.trim()).append("\t");
        buf.append(this.sFFieldType.trim()).append("\t");
        buf.append(this.sFFieldDesc.trim()).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }
    //根据SQL语句构建发送回前台的数据串集
    private String buildSendStr(String strSql) throws YssException {
        //StringBuffer buf = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        ResultSet rs = null;
        String sResult = "";
        try {
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.sFTableName = rs.getString("FTabName") + "";
                this.sFTableDesc = rs.getString("FTableDesc") + "";
                this.sFFieldName = rs.getString("FFieldName") + "";
                this.sFFieldDesc = rs.getString("FFieldDesc") + "";
                this.sFFieldType = rs.getString("FFieldType") + "";
                sResult += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("访问数据字典表出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    //获取要显示到listView控件上数据
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = "字段代码\t字段名称\t字段类型\t字段描述";
            String sql =
                "select * from " +
                "Tb_Fun_TableDict " +
                " where FTableName = " +
                dbl.sqlString(this.filterType.getStrFTableName()) +
                " and FFieldName<>' ' order by FTableName  desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                buf.append( (rs.getString("FFieldCode") + "").trim()).append("\t");
                buf.append( (rs.getString("FFIELDNAME") + "").trim()).append("\t");
                buf.append( (rs.getString("FFieldType") + "").trim()).append("\t");
                buf.append( (rs.getString("FFieldDesc") + "").trim()).append("\t");

                buf.append( (rs.getString("FTableCode") + "").trim()).append("\t");
                buf.append( (rs.getString("FTableName") + "").trim()).append("\t");
                buf.append( (rs.getString("FTableDesc") + "").trim());
                buf.append(YssCons.YSS_LINESPLITMARK);

                this.sFFieldCode = rs.getString("FFieldCode") + "";
                this.sFFieldName = rs.getString("FFIELDNAME") + "";
                this.sFFieldType = rs.getString("FFieldType") + "";
                this.sFFieldDesc = rs.getString("FFieldDesc") == null ? "" : rs.getString("FFieldDesc");

                buf1.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0, //表相关信息
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0, //字段相关信息
                    buf1.toString().length() - 2);
            }
            String temp = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            return temp;
        } catch (Exception e) {
            throw new YssException("获取数据字典信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    //根据不同的数据库获取数据字段的类型,暂时没用到
    public String getFieldType(String fieldtype) {
        String strFieldType = "";
        if (fieldtype.equalsIgnoreCase("0")) {
            if (dbl.dbType == YssCons.DB_ORA) {
                strFieldType = "VARCHAR2";
            } else {
                strFieldType = "VARCHAR";
            }
        } else if (fieldtype.equalsIgnoreCase("1")) {
            if (dbl.dbType == YssCons.DB_ORA) {
                strFieldType = "NUMBER";
            } else {
                strFieldType = "DECIMAL";
            }
        } else if (fieldtype.equalsIgnoreCase("2")) {
            strFieldType = "DATE";
        } else if (fieldtype.equalsIgnoreCase("3")) {
            if (dbl.dbType == YssCons.DB_ORA) {
                strFieldType = "VARCHAR2";
            } else {
                strFieldType = "VARCHAR";
            }
        }
        return strFieldType;
    }

    /**
     * 本方法用于编辑创建表的所有字段;
     * @param fields String 所有信息
     * @throws YssException
     * @return String 创建表的SQL语句,包括建表语句,主键等.
     */
    public String buildFields(String fields) throws YssException {
        String[] FieldsArr = null;
        String Field = ""; //编辑单个的字段;
        String sPK = ""; //primary key string
        String Fields = ""; //编辑所有的字段;
        FieldsArr = fields.split("\f\f");
        for (int i = 0; i < FieldsArr.length; i++) {
            this.protocolParse(FieldsArr[i]);
            if (this.sFFieldName.trim().length() != 0) { //字段
                Field = this.sFFieldName;
            }
            if (this.sFFieldType.trim().length() != 0) { //类型
                Field += " " + getFieldType(this.sFFieldType);
            }
        }
        if (Fields.length() > 0) {
            Fields = Fields.substring(0, Fields.length() - 1);
        }
        if (sPK.length() > 0) {
            sPK = sPK.substring(0, sPK.length() - 1);
            sPK = " alter table " + sFTableName + " add " + " constraint PK_" +
                sFTableName +
                " primary key (" + sPK + ")";
        }
        if (Fields.trim().equalsIgnoreCase("NULL")) {
            return null;
        }
        return "create table " + sFTableName + "(" + Fields + ")\t" + sPK;
    }
    //创建数据表结构
    public void createTable(String tabName) throws YssException {
        String strSql = "";
        String[] buildSQL = null;
        String getFields = "";
        try {
            if (dbl.yssTableExist(tabName)) {
            	
            	/**shashijie 2011-10-20 STORY 1698 */
                dbl.executeSql(dbl.doOperSqlDrop("drop table " + tabName));
                String ss = dbl.doOperSqlDrop("drop table " + tabName);
                /**end*/
                
                if (this.sState.equalsIgnoreCase("1")) { //写入日志
                    com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                    sysdata.setYssPub(pub);
                    sysdata.setStrAssetGroupCode("Common");
                    sysdata.setStrFunName("删表-数据字典");
                    sysdata.setStrCode(this.sFTableName);
                    sysdata.setStrName(" ");
                    sysdata.setStrUpdateSql(ss);
                    sysdata.setStrCreator(pub.getUserName());
                    sysdata.addSetting();
                }
            }
            if (sSubData.length() != 0) {
                buildSQL = buildFields(this.sSubData).split("\t");
                if (buildSQL != null && buildSQL[0].length() != 0) {
                    dbl.executeSql(buildSQL[0]);
                }
                if (this.sState.equalsIgnoreCase("1") && buildSQL != null &&
                    buildSQL[0].length() != 0) {
                    com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                    sysdata.setYssPub(pub);
                    sysdata.setStrAssetGroupCode("Common");
                    sysdata.setStrFunName("建表-数据字典");
                    sysdata.setStrCode(this.sFTableName);
                    sysdata.setStrName(" ");
                    sysdata.setStrUpdateSql(buildSQL[0]);
                    sysdata.setStrCreator(pub.getUserName());
                    sysdata.addSetting();
                }
                if (buildSQL.length == 2 && buildSQL[1].length() != 0) {
                    dbl.executeSql(buildSQL[1]);
                }
                if (this.sState.equalsIgnoreCase("1") && buildSQL.length == 2 &&
                    buildSQL[1].length() != 0) {
                    com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                    sysdata.setYssPub(pub);
                    sysdata.setStrAssetGroupCode("Common");
                    sysdata.setStrFunName("建表-数据字典");
                    sysdata.setStrCode(this.sFTableName);
                    sysdata.setStrName(this.sFFieldName);
                    sysdata.setStrUpdateSql(buildSQL[1]);
                    sysdata.setStrCreator(pub.getUserName());
                    sysdata.addSetting();
                }
            }
        } catch (Exception e) {
            throw new YssException("创建临时表出错！");
        }
    }

    /**
     * 将default值转换成相对应的值
     * @param sFieldType String 数据类型
     * @param sDefaultValue String  默认值
     * @return String
     */
    private String defaultStr(String sFieldType, String sDefaultValue) {
        String sRes = "";
        if (sDefaultValue.trim().length() != 0) {
            if (sFieldType.equalsIgnoreCase("0")) { //char
                sRes = " default " + dbl.sqlString(sDefaultValue);
            } else if (sFieldType.equalsIgnoreCase("1") && !sDefaultValue.equalsIgnoreCase("null")) { //number
                sRes = " default " + sDefaultValue;
            } else if (sFieldType.equalsIgnoreCase("2") && !sDefaultValue.equalsIgnoreCase("null")) { //date
                sRes = " default " + dbl.sqlDate(sDefaultValue);
            } else if (sFieldType.equalsIgnoreCase("3")) { //varchar2
                sRes = " default " + dbl.sqlString(sDefaultValue);
            }
        }
        return sRes;
    }
    //获取表信息
    public void getTableInfo(String sFTableName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String sResult = "";
        StringBuffer buf = null;
        StringBuffer bufAll = new StringBuffer();
        try {
            strSql = " select * from Tb_Fun_TableDict " +
                " where FTABNAME=" + dbl.sqlString(sFTableName);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf = new StringBuffer();
                buf.append(rs.getString("FTabName")).append("\t");
                buf.append(rs.getString("FFieldName")).append("\t");
                buf.append(rs.getString("FTableDesc")).append("\t");
                buf.append(rs.getString("FFieldDesc")).append("\t");
                buf.append(rs.getString("FFieldType")).append("\t");
                buf.append(1).append("\t");
                buf.append(rs.getString("FFIELDPRE")).append("\t");
                buf.append("").append("\t1\tnull");

                bufAll.append(buf.toString()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufAll.toString().length() > 2) {
                sResult = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            this.sSubData = sResult;
        } catch (Exception e) {
            throw new YssException("获取数据出错!");
        }finally{
             dbl.closeResultSetFinal(rs); //在finally中关闭结果集
        }
    }

    /**
     * 用于判断表的类型
     * 考虑很多地方都要用到当前表的类型
     * @param sTabName String 当前的表名
     * @return int 0:系统表,1:临时表 -1:当前表不存在
     * @throws YssException
     */
    public int getTabType(String sTabName) throws YssException {
        String strSql = "";
        int iType = -1;
        ResultSet rs = null;
        try {
            strSql = "select distinct(FTabName) as FTabName,FTableType from Tb_Fun_TableDict where FTabName=" +
                dbl.sqlString(sTabName);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                iType = rs.getInt("FTableType");
            }
            return iType;
        } catch (Exception e) {
            throw new YssException("获取表【" + sTabName + "】的类型出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //在finally中关闭结果集
        }
    }

}
