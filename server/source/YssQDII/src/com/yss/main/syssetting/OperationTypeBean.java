package com.yss.main.syssetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title: OperationTypeBean </p>
 * <p>Description: 3.9.	操作类型设置设置 </p>
 * <p>Company: Ysstech </p>
 * @author not attributable
 * @version 1.0
 */


public class OperationTypeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strOperationCode = ""; //操作类型代码
    private String strOperationName = ""; //操作类型名称
    private String strOperationType = ""; //操作类型
    private String strOldOperationCode = "";
    private OperationTypeBean filterType;
    public OperationTypeBean() {
    }

    public OperationTypeBean(YssPub pub) {
        setYssPub(pub);
    }

    /**
     * buildRowStr
     * 操作类型设置设置数据
     * @param sRowStr String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strOperationCode.trim()).append("\t");
        buf.append(this.strOperationName.trim()).append("\t");
        buf.append(this.strOperationType.trim()).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * parseRowStr
     * 解析操作类型设置设置数据
     * @param sRowStr String
     */
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
            reqAry = sTmpStr.split("\t", -1);
            this.strOperationCode = reqAry[1];
            this.strOperationName = reqAry[2];
            this.strOperationType = reqAry[3];
            this.strOldOperationCode = reqAry[0];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new OperationTypeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析操作类型设置设置请求出错", e);
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
            strSql = "select FOperTypeName from Tb_Sys_OperationType where FOperTypeCode = " +
                dbl.sqlString(this.strOperationCode.trim()) + "";
            strTmp = dbFun.GetValuebySql(strSql);
            if (strTmp.length() > 0) {
                throw new YssException("操作类型代码【" + this.strOperationCode.trim() + "】已经被操作类型【" + strTmp + "】所占用，请重新输入");
            }
        }
        if (btOper == YssCons.OP_EDIT) {
            if (!dbl.sqlString(this.strOperationCode).equalsIgnoreCase(dbl.sqlString(this.strOldOperationCode))) {
                strSql = "select FOperTypeName from Tb_Sys_OperationType where FOperTypeCode = " +
                    dbl.sqlString(this.strOperationCode.trim()) + "";
                strTmp = dbFun.GetValuebySql(strSql);
                if (strTmp.length() > 0) {
                    throw new YssException("操作类型代码【" + this.strOperationCode.trim() + "】已经被操作类型【" + strTmp + "】所占用，请重新输入");
                }

            }

        }
    }

    /**
     * 新增操作类型设置记录
     * modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
     * 删除不用的代码和关闭数据库连接的代码，单一的新增没必要事务控制，而且之前的控制也是无效的
     * @param frmValue：传入二维数组，里面为需要保存的记录数据
     * @throws YssException
     */
    public void saveOperationType() throws YssException {
        String strSql = "";
        String errorInfo = "保存操作类型设置信息设定时出错!"; //定义错误提示信息
        try {
            strSql = "insert into Tb_Sys_OperationType(FOperTypeCode,FOperTypeName,FType) values(" + dbl.sqlString(this.strOperationCode) + "," +
                dbl.sqlString(this.strOperationName) + "," + dbl.sqlString(this.strOperationType.equalsIgnoreCase("0") ? "fund" : "system") + ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException(errorInfo, e);
        }
    }

    /**
     * 修改操作类型设置记录
     * modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
     * 删除不用的代码和关闭数据库连接的代码，单一的更新没必要事务控制，而且之前的控制也是无效的
     * @param frmValue：传入二维数组，里面为需要保存的记录数据
     * @throws YssException
     */

    public void editOperationType() throws YssException {
        String strSql = "";
        String errorInfo = "修改操作类型设置信息设定时出错!"; //定义错误提示信息
        try {
            strSql = "update Tb_Sys_OperationType set FOperTypeCode =" + dbl.sqlString(this.strOperationCode) +
                ",FOperTypeName = " + dbl.sqlString(this.strOperationName) + ",FType = " + dbl.sqlString(this.strOperationType.equalsIgnoreCase("0") ? "fund" : "system") +
                " where FOperTypeCode =" + dbl.sqlString(this.strOldOperationCode);
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException(errorInfo, e);
        }
    }

    /**
     * 删除操作类型设置记录
     * modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
     * 删除不用的代码和关闭数据库连接的代码，单一的删除没必要事务控制，而且之前的控制也是无效的
     * @param frmValue：传入二维数组，里面为需要保存的记录数据
     * @throws YssException
     */

    public void delOperationType() throws YssException {
        String strSql = "";
        String errorInfo = "删除操作类型设置信息设定时出错!"; //定义错误提示信息
        try {
            strSql = "delete from Tb_Sys_OperationType" +
                " where FOperTypeCode = " + dbl.sqlString(this.strOperationCode);
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException(errorInfo, e);
        }
    }

    /**
     * 筛选条件
     * @return String
     */
    public String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1 = 1";
            if (this.filterType.strOperationCode.length() != 0) {
            	//edit by songjie 2011.12.12 BUG 3187 QDV4赢时胜(测试)2011年11月21日01_B 改为首字母匹配
                sResult = sResult + " and FOperTypeCode like '" +
                    filterType.strOperationCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strOperationName.length() != 0) {
            	//edit by songjie 2011.12.12 BUG 3187 QDV4赢时胜(测试)2011年11月21日01_B 改为首字母匹配
                sResult = sResult + " and FOperTypeName like '" +
                    filterType.strOperationName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strOperationType.length() != 0 
            		&& !"99".equals(filterType.strOperationType)) {//by guyichuan 2011.07.25 BUG2221操作类型设置界面存在问题
            	//edit by songjie 2011.12.12 BUG 3187 QDV4赢时胜(测试)2011年11月21日01_B 改为首字母匹配
                sResult = sResult + " and FType like '" +
                    ( (filterType.strOperationType).equalsIgnoreCase("0") ? "fund" : "system").replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取"操作类型设置数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sHeader = "";
        String strSql = "";
        ResultSet rs = null;
        String sVocStr = "";
        VocabularyBean vocabulary = new VocabularyBean();
        vocabulary.setYssPub(pub);
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sVocStr = vocabulary.getVoc(YssCons.YSS_OPE_TYPE);
            sHeader = "操作类型代码\t操作类型名称\t类型";
            strSql = "select * from Tb_Sys_OperationType  " +
                buildFilterSql() + " order by FOperTypeCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FOperTypeCode"));
                bufShow.append("\t");
                bufShow.append(rs.getString("FOperTypeName"));
                bufShow.append("\t");
                bufShow.append(rs.getString("FType").equalsIgnoreCase("system") ? "系统" : "业务");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.strOperationCode = rs.getString("FOperTypeCode");
                this.strOperationName = rs.getString("FOperTypeName");
                this.strOperationType = rs.getString("FType");
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\fFOperTypeCode\tFOperTypeName\tFType"
                + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取操作类型信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {
    }

    public void checkSetting() throws YssException {
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
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

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
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
