package com.yss.log;

import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: 流程的日志</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 * MS00003 QDV4.1-参数布局散乱不便操作
 */
public class FlowLogBean
    extends BaseDataSettingBean implements IYssLog, IDataSetting {
    private String LogCode = "";
    private Date LogDate = null;
    private String LogTime = "";
    private String OperUser = "";
    private String FlowCode = "";
    private String FlowPointCode = "";
    private String OperContent = "";
    private String OperResult = "";
    public FlowLogBean() {
    }

    public void checkInput(byte btOper) throws YssException {
    }

    /**
     * 插入日志信息
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String sqlStr = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            this.LogCode =
                dbFun.getNextInnerCode("Tb_Sys_FlowOperLog",
                                       dbl.sqlRight("FLogCode", 5), "00001",
                                       " where 1=1", 1);
            sqlStr = "insert into " + pub.yssGetTableName("Tb_Sys_FlowOperLog") +
                "(FLogCode,FLogDate,FLogTime,FOperUser,FFlowCode,FFlowPointCode,FOperContent,FOperResult) values " +
                "(" +
                dbl.sqlString(this.LogCode) + "," +
                dbl.sqlDate(new java.util.Date()) + "," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
                dbl.sqlString(this.OperUser) + "," +
                dbl.sqlString(this.FlowCode) + "," +
                dbl.sqlString(this.FlowPointCode) + "," +
                dbl.sqlString(this.OperContent) + "," +
                dbl.sqlString(this.OperResult) +
                ")";
            dbl.executeSql(sqlStr);
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("插入流程日志信息出现异常！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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

    public String getListViewData1() throws YssException {
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

    public void insertLog(Object obj) throws YssException {
    }

    public void parseRowStr(String sRowStr) throws YssException {
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String getFlowCode() {
        return FlowCode;
    }

    public void setFlowCode(String FlowCode) {
        this.FlowCode = FlowCode;
    }

    public String getFlowPointCode() {
        return FlowPointCode;
    }

    public void setFlowPointCode(String FlowPointCode) {
        this.FlowPointCode = FlowPointCode;
    }

    public String getLogCode() {
        return LogCode;
    }

    public void setLogCode(String LogCode) {
        this.LogCode = LogCode;
    }

    public Date getLogDate() {
        return LogDate;
    }

    public void setLogDate(Date LogDate) {
        this.LogDate = LogDate;
    }

    public String getLogTime() {
        return LogTime;
    }

    public void setLogTime(String LogTime) {
        this.LogTime = LogTime;
    }

    public String getOperContent() {
        return OperContent;
    }

    public void setOperContent(String OperContent) {
        this.OperContent = OperContent;
    }

    public String getOperResult() {
        return OperResult;
    }

    public void setOperResult(String OperResult) {
        this.OperResult = OperResult;
    }

    public String getOperUser() {
        return OperUser;
    }

    public void setOperUser(String OperUser) {
        this.OperUser = OperUser;
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
