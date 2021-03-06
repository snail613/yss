package com.yss.main.funsetting;

import java.sql.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class RecycleBean
    extends BaseDataSettingBean implements IDataSetting {
    private String menuBar = "";
    private String URLRoot = "";
    public RecycleBean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
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

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String menuBarCode = "";
        String refinvokeCode = "";
        String paramStr = "";
        String ctx = "";
        String sResult = "";
        String beanId = "";
        String temp = "";
        String sSetDllName = "";
        String sSetClassName = "";
        String sPojoClassName = "";
        try {
            strSql = " select * from tb_fun_menubar" +
                " where FBARCode=" + dbl.sqlString(this.menuBar);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                refinvokeCode = rs.getString("FREFINVOKECODE");
            }
            dbl.closeResultSetFinal(rs);
            strSql = " select * from tb_fun_refinvoke" +
                " where FREFINVOKECode=" + dbl.sqlString(refinvokeCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                paramStr = rs.getString("FPARAMS");
            }
            if (paramStr != null && paramStr != "") {
                temp = paramStr.split("cmd=")[0];
                ctx = temp.substring(1, temp.length() - 1);
                beanId = (paramStr.split("cmd=")[1]).split("&")[0];
                sSetDllName = paramStr.split(",")[1];
                sSetClassName = paramStr.split(",")[2];
                sPojoClassName = paramStr.split(",")[3];

            }
            if (ctx.equalsIgnoreCase("basesetting")) {
                sResult = this.getData("basesetting", beanId);
            } else if (ctx.equalsIgnoreCase("parasetting")) {
                sResult = this.getData("parasetting", beanId);
            } else if (ctx.equalsIgnoreCase("cashmanager")) {
                sResult = this.getData("cashmanager", beanId);
            } else if (ctx.equalsIgnoreCase("operdata")) {
                sResult = this.getData("operdata", beanId);
            } else if (ctx.equalsIgnoreCase("accbook")) {
                sResult = this.getData("accbook", beanId);
            } else if (ctx.equalsIgnoreCase("funsetting")) {
                sResult = this.getData("funsetting", beanId);
            } else if (ctx.equalsIgnoreCase("compliance")) {
                sResult = this.getData("compliance", beanId);
            } else if (ctx.equalsIgnoreCase("systemsetting")) {
                sResult = this.getData("systemsetting", beanId);
            }
            return sResult + "/split/" +
                sSetDllName + "/split/" +
                sSetClassName + "/split/" +
                sPojoClassName;
        } catch (Exception e) {
            throw new YssException("获取数据出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
        String reqAry[] = null;
        if (sRowStr.trim().length() == 0) {
            return;
        }
        reqAry = sRowStr.split("\t");
        this.menuBar = reqAry[0];
        this.URLRoot = reqAry[1];

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    public String getData(String ctx, String beanId) throws YssException {
        ApplicationContext appCtx = null;
        String sResult = "";
        Object obj = null;
        IDataSetting bean = null;
        try {
		    //调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			appCtx = new FileSystemXmlApplicationContext(
					YssUtil.getAppConContextPath(YssCons.YSS_WebRealPath, ctx + ".xml"));
            pub.setBaseSettingCtx(appCtx);
            obj = appCtx.getBean(beanId);
            if (obj instanceof IDataSetting) {
                bean = (IDataSetting) obj;
                bean.setYssPub(pub);
                sResult = bean.getListViewData1();
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
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
