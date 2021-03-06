package com.yss.main.funsetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 *
 * <p>Title: RefInvokeBean </p>
 * <p>Description: 反射调用 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class RefInvokeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String invokeName = "";
    private String dllName = "";
    private String className = "";
    private String methodName = "";
    private String params = "";
    private String desc = "";
    private String invokeCode = "";
    private String oldinvokeCode = "";
    private String AllShowKeyStr = "";
    private String status = ""; //是否记入系统信息状态  lzp 11.29 add
    private RefInvokeBean filterType;
    public void setYssPub(YssPub pub) {
        super.setYssPub(pub);
    }

    public String getInvokeName() {
        return invokeName;
    }

    public void setInvokeName(String invokeName) {
        this.invokeName = invokeName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDllName() {
        return dllName;
    }

    public void setDllName(String dllName) {
        this.dllName = dllName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setAllShowKeyStr(String AllShowKeyStr) {
        this.AllShowKeyStr = AllShowKeyStr;
    }

    public String getParams() {
        return params;
    }

    public String getAllShowKeyStr() {
        return AllShowKeyStr;
    }

    public void parseRowStr(String sReq) throws YssException {
        String reqAry[] = null;
        try {
            if (sReq.trim().length() == 0) {
                return;
            }
            reqAry = sReq.split("\t");
            this.invokeCode = reqAry[0];
            this.invokeName = reqAry[1];
            this.dllName = reqAry[2];
            this.className = reqAry[3];
            this.methodName = reqAry[4];
            this.params = reqAry[5];
            this.desc = reqAry[6];
            this.oldinvokeCode = reqAry[7];
            this.status = reqAry[8]; //lzp add 11.28
            if (sReq.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RefInvokeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sReq.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析反射调用出错", e);
        }

    }

    public String getFilterSQL() throws YssException {
        String sqlstr = "";
        if (this.filterType != null) {
            sqlstr = " where 1=1";
            if (filterType.invokeCode.trim().length() > 0) {
                sqlstr += " and FRefInvokeCode like '" + filterType.invokeCode.trim().replaceAll("'", "''") + "%'";
            }
            if (filterType.invokeName.trim().length() > 0) {
                sqlstr += " and FRefInvokeName like '" + filterType.invokeName.trim().replaceAll("'", "''") + "%'";
            }
            if (filterType.dllName.trim().length() > 0) {
                sqlstr += " and FDllName like '" + filterType.dllName.trim().replaceAll("'", "''") + "%'";
            }
            if (filterType.className.trim().length() > 0) {
                sqlstr += " and FClassName like '" + filterType.className.trim().replaceAll("'", "''") + "%'";
            }
            if (filterType.methodName.trim().length() > 0) {
                sqlstr += " and FMethodName like '" + filterType.methodName.trim().replaceAll("'", "''") + "%'";
            }
            if (filterType.params.trim().length() > 0) {
                sqlstr += " and FParams like '" + filterType.params.trim().replaceAll("'", "''") + "%'";
            }
            if (filterType.desc.trim().length() > 0) {
                sqlstr += " and FDesc like '" + filterType.desc.trim().replaceAll("'", "''") + "%'";
            }
        }
        return sqlstr;
    }

    public String getAllSetting() throws YssException {

        return null;
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
                "select FRefInvokeName from Tb_Fun_RefInvoke where FRefInvokeCode=" +
                dbl.sqlString(this.invokeCode.trim()) + "";
            strTmp = dbFun.GetValuebySql(strSql);
            if (strTmp.length() > 0) {
            	//edit by songjie 2011.08.16 BUG QDV4赢时胜(测试)2011年7月5日06_B
                throw new YssException("调用代码【" + this.invokeCode.trim() +
                                       "】已经被占用，请重新输入");
            }
        } else if (btOper == YssCons.OP_EDIT) {
            if (!this.invokeCode.trim().equalsIgnoreCase(this.oldinvokeCode)) {
                strSql =
                    "select FRefInvokeName from Tb_Fun_RefInvoke where FRefInvokeCode=" +
                    dbl.sqlString(this.invokeCode.trim()) + "";
                strTmp = dbFun.GetValuebySql(strSql);
                if (strTmp.length() > 0) {
                	//edit by songjie 2011.08.16 BUG QDV4赢时胜(测试)2011年7月5日06_B
                    throw new YssException("调用代码【" + this.invokeCode.trim() +
                                           "】已经被占用，请重新输入");
                }
            }
        }

    }

    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            PreparedStatement pst = conn.prepareStatement(
                "insert into Tb_Fun_RefInvoke" +
                "(FRefInvokeCode,FRefInvokeName,FDllName,FClassName,FMethodName,FParams,FDesc)" +
                " values(?,?,?,?,?,?,?)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pst.setString(1, this.invokeCode);
            pst.setString(2, this.invokeName);
            pst.setString(3, this.dllName);
            pst.setString(4, this.className);
            pst.setString(5, this.methodName);
            pst.setString(6, this.params);
            pst.setString(7, this.desc);
            pst.executeUpdate();
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                String strSql = "insert into Tb_Fun_RefInvoke" +
                    "(FRefInvokeCode,FRefInvokeName,FDllName,FClassName,FMethodName,FParams,FDesc)" +
                    " values(" + dbl.sqlString(this.invokeCode) + "," + dbl.sqlString(this.invokeName) + "," +
                    dbl.sqlString(this.dllName) + "," + dbl.sqlString(this.className) + "," + dbl.sqlString(this.methodName) +
                    "," + dbl.sqlString(this.params) + "," + dbl.sqlString(this.desc) + ")";
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-系统功能调用");
                sysdata.setStrCode(this.invokeCode);
                sysdata.setStrName(this.invokeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return null;
        } catch (Exception e) {
            throw new YssException("增加反射调用设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //   System.out.println("InvokeCode="+this.invokeCode);
            strSql = "update Tb_Fun_RefInvoke set FRefInvokeCode=" +
                dbl.sqlString(this.invokeCode) + ", ";
            strSql = strSql + "FRefInvokeName=" + dbl.sqlString(this.invokeName) + " " +
                ", ";
            strSql = strSql + "FDllName=" + dbl.sqlString(this.dllName) + " " + ", ";
            strSql = strSql + "FClassName=" + dbl.sqlString(this.className) + " " + ", ";
            strSql = strSql + "FMethodName=" + dbl.sqlString(this.methodName) + " " +
                ", ";
            strSql = strSql + "FParams=" + dbl.sqlString(this.params) + " " + ", ";
            strSql = strSql + "FDesc=" + dbl.sqlString(this.desc) + " " + "";
            strSql = strSql + "where FRefInvokeCode=" +
                dbl.sqlString(this.oldinvokeCode) +
                "";
            System.out.println("This SQL is :" + strSql);
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-系统功能调用");
                sysdata.setStrCode(this.oldinvokeCode);
                sysdata.setStrName(this.invokeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return null;
        } catch (Exception e) {
            throw new YssException("修改反射调用设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "delete from Tb_Fun_RefInvoke where FRefInvokeCode = '" +
                this.invokeCode + "'";
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-系统功能调用");
                sysdata.setStrCode(this.invokeCode);
                sysdata.setStrName(this.invokeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = true;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除反射调用设置出错", e);
        } finally{
        	dbl.endTransFinal(conn, bTrans);
        }
    }

    public void checkSetting() throws YssException {

    }

    public String getListViewData1() throws YssException {
        String sHeader = "";
        String strSql = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            // sHeader = "调用代码\t调用名称\t描述";
            strSql = "select * from Tb_Fun_RefInvoke "
                + this.getFilterSQL() + " order by FRefInvokeCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FRefInvokeCode")).append("\t");
                bufShow.append(rs.getString("FRefInvokeName")).append("\t");
                bufShow.append(rs.getString("FDllName")).append("\t");
                bufShow.append(rs.getString("FClassName")).append("\t");
                bufShow.append(rs.getString("FMethodName")).append("\t");
                bufShow.append(rs.getString("FParams") + "").append("\t");
                bufShow.append(rs.getString("FDesc") +
                               "").append(YssCons.YSS_LINESPLITMARK);

                buf.append(rs.getString("FRefInvokeCode")).append("\t");
                buf.append(rs.getString("FRefInvokeName")).append("\t");
                buf.append(rs.getString("FDllName")).append("\t");
                buf.append(rs.getString("FClassName")).append("\t");
                buf.append(rs.getString("FMethodName")).append("\t");
                buf.append(rs.getString("FParams") + "").append("\t");
                buf.append(rs.getString("FDesc") +
                           "").append("\t").append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (buf.toString().length() > 2) {
                sAllDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取调用设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData2() throws YssException {
        return null;
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return null;
    }

    /**
     * 获取单条信息 liyu 修改 080407
     * @return IDataSetting
     * @throws YssException
     */
    public IDataSetting getSetting() throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        try {
            sqlStr = "select * from TB_FUN_REFINVOKE where FRefInvokeCode =" +
                dbl.sqlString(this.invokeCode);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                this.invokeName = rs.getString("FRefInvokeName");
                this.dllName = rs.getString("FDllName");
                this.className = rs.getString("FClassName");
                this.methodName = rs.getString("FMethodName");
                this.params = rs.getString("FParams");
                this.desc = rs.getString("FDesc");
            }
        } catch (Exception e) {
            throw new YssException("获取反射调用信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //close cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
        }
        return null;
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.invokeCode.trim()).append("\t");
        buf.append(this.invokeName.trim()).append("\t");
        buf.append(this.dllName.trim()).append("\t");
        buf.append(this.className.trim()).append("\t");
        buf.append(this.methodName.trim()).append("\t");
        buf.append(this.params.trim()).append("\t");
        buf.append(this.desc.trim()).append("\tnull");
        return buf.toString();
    }

    /**
     * getTreeViewData1 ：
     * 获取选择TreeView控件的数据
     * @return String
     */
    public String getTreeViewData1() throws YssException {
        return null;
    }

    /**
     * getTreeViewData2 ：
     * 获取选择TreeView控件的数据
     * @return String
     */
    public String getTreeViewData2() throws YssException {
        return null;
    }

    /**
     * getTreeViewData3 ：
     * 获取选择TreeView控件的数据
     * @return String
     */
    public String getTreeViewData3() throws YssException {
        return null;
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
    public String getBeforeEditData() throws YssException {
        RefInvokeBean befEditBean = new RefInvokeBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from Tb_Fun_RefInvoke" +
                " where  FRefInvokeCode =" + dbl.sqlString(this.oldinvokeCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.invokeCode = rs.getString("FRefInvokeCode") + "";
                befEditBean.invokeName = rs.getString("FRefInvokeName") + "";
                befEditBean.dllName = rs.getString("FDllName") + "";
                befEditBean.className = rs.getString("FClassName") + "";
                befEditBean.methodName = rs.getString("FMethodName") + "";
                befEditBean.params = rs.getString("FParams") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //close cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    public String getStatus() {
        return status;
    }

    public String getOldinvokeCode() {
        return oldinvokeCode;
    }

    public String getInvokeCode() {
        return invokeCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setOldinvokeCode(String oldinvokeCode) {
        this.oldinvokeCode = oldinvokeCode;
    }

    public void setInvokeCode(String invokeCode) {
        this.invokeCode = invokeCode;
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
