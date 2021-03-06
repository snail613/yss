package com.yss.main.funsetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class SpringInvokeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String SICode = "";
    private String SIName = "";
    private String BeanID = "";
    private String Params = "";
    private String ReturnType = "";
    private String ModuleCode = "";
    private String ReturnTypeName = "";
    private String ModuleName = "";
    private String oldSICode = "";
    private String desc;

    private String FormCode = "";
    private String FormName = "";
    private String CtlName = "";
    private String status = ""; //是否记入系统信息状态  lzp 11.28 add
    private SpringInvokeBean filterType;
    private boolean bShow = true; //若为true筛先
    public SpringInvokeBean() {
    }

    public void checkInput(byte btOper) throws YssException {
     
          /* dbFun.checkInputCommon(btOper,
                                 "TB_FUN_SPINGINVOKE",
                                 "FSICode",
                                 this.SICode,
                                 this.oldSICode);*/
    	//edited by zhouxiang MS01590    spring调用复制数据点击确定时，提示已存在    

    	ResultSet rs = null;
    	String strSql="select * from "+pub.yssGetTableName("TB_FUN_SPINGINVOKE")+
    	" where FSiCode="+dbl.sqlString(this.SICode);//modify by guojianhua 20100903  MS01685    spring调用，复制一条数据点击确定时,系统报错    QDV4赢时胜(上海开发部)2010年09月01日03_B    
    	try{
    		//---edit by songjie 2011.06.03 BUG 2035 QDV4赢时胜(测试)2011年06月03日02_B---//
        	if (btOper == YssCons.OP_EDIT || btOper == YssCons.OP_ADD){
        		if(this.SICode.equals(this.oldSICode)){
        			return;
        		}else{
        			rs = dbl.openResultSet(strSql);
        			if(rs.next()){
        				throw new YssException("【" + this.SICode + "】信息已经存在，请重新输入");
        			}
        		}
        	}
        	//---edit by songjie 2011.06.03 BUG 2035 QDV4赢时胜(测试)2011年06月03日02_B---//
    	}catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        //end-- by zhoxiang  MS01590    spring调用复制数据点击确定时，提示已存在    
    }
    

    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String reSql = "";
        try {
            conn = dbl.loadConnection();
            reSql = "delete from TB_FUN_SPINGINVOKE where FSICode=" + dbl.sqlString(SICode);
            dbl.executeSql(reSql);
            reSql = " insert into TB_FUN_SPINGINVOKE " +
                " (FSICode,FSIName,FBeanId,FParams,FReturnType,FFORMCODE,FCTLNAME,FModuleCode,FDesc ) values(" +
                dbl.sqlString(this.SICode) + "," +
                dbl.sqlString(this.SIName) + "," +
                dbl.sqlString(this.BeanID) + "," +
                dbl.sqlString(this.Params) + "," +
                dbl.sqlString(this.ReturnType) + "," +
                dbl.sqlString(this.FormCode) + "," +
                dbl.sqlString(this.CtlName) + "," +
                dbl.sqlString(this.ModuleCode) + "," +
                dbl.sqlString(this.desc) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(reSql);
            //---------lzp add 11.28
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-Spring调用");
                sysdata.setStrCode(this.SICode);
                sysdata.setStrName(this.SIName);
                sysdata.setStrUpdateSql(reSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("添加Spring调用出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        this.bShow = false;
        return "";

    }

    public String editSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "update TB_FUN_SPINGINVOKE" +
                " set FSICode=" + dbl.sqlString(this.SICode) + "," +
                " FSIName=" + dbl.sqlString(this.SIName) + "," +
                " FBeanId=" + dbl.sqlString(this.BeanID) + "," +
                " FParams=" + dbl.sqlString(this.Params) + "," +
                " FReturnType=" + dbl.sqlString(this.ReturnType) + "," +
                " FFormCode=" + dbl.sqlString(this.FormCode) + "," +
                " FCtlName=" + dbl.sqlString(this.CtlName) + "," +
                " FModuleCode=" + dbl.sqlString(this.ModuleCode) + "," +
                " FDesc=" + dbl.sqlString(this.desc) +
                " where FSICode=" + dbl.sqlString(this.oldSICode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.28
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-Spring调用");
                sysdata.setStrCode(this.SICode);
                sysdata.setStrName(this.SIName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改Spring调用设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        this.bShow = false;
        return "";

    }

    public void delSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "delete from TB_FUN_SPINGINVOKE" +
                " where FSICode=" + dbl.sqlString(this.oldSICode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.28
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-Spring调用");
                sysdata.setStrCode(this.oldSICode);
                sysdata.setStrName(this.SIName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除Spring调用设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        this.bShow = false;
    }

    public void checkSetting() throws YssException {
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = " select * from TB_FUN_SPINGINVOKE" +
                " where FSICode=" + dbl.sqlString(this.SICode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.SICode = rs.getString("FSICode");
                this.SIName = rs.getString("FSIName");
                this.BeanID = rs.getString("FBeanId");
                this.Params = rs.getString("FParams");
                this.ReturnType = rs.getString("FReturnType");
                this.FormCode = rs.getString("FFormCode");
                this.CtlName = rs.getString("FCtlName");

                this.ModuleCode = rs.getString("FModuleCode");
            }
        } catch (Exception e) {
            throw new YssException("获取Spring调用设置出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    private void setSpringAttr(ResultSet rs) throws SQLException {
        this.SICode = rs.getString("FSICode");
        this.SIName = rs.getString("FSIName");
        this.BeanID = rs.getString("FBeanId");
        this.desc = rs.getString("FDesc");
        this.Params = rs.getString("FParams");
        this.ReturnType = rs.getString("FReturnType");
        this.FormCode = rs.getString("FFormCode");
        this.FormName = rs.getString("FFormName");
        this.CtlName = rs.getString("FCtlName");
        this.ReturnTypeName = rs.getString("FReturnTypeName");
        this.ModuleCode = rs.getString("FModuleCode");
        this.ModuleName = rs.getString("FModuleName");
        //  super.setRecLog(rs);
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

    private String builerFilter() throws SQLException, YssException {
        String reStr = "";
        if (this.filterType != null) {
            reStr = " where 1=1 ";
            if (this.filterType.SICode != null && this.filterType.SICode.trim().length() != 0) {
                reStr += " and FSIcode like '" + filterType.SICode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.SIName != null && this.filterType.SIName.trim().length() != 0) {
                reStr += " and FSIName like '" + filterType.SIName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.BeanID != null && this.filterType.BeanID.length() != 0) {
                reStr += " and FBeanID like '" + filterType.BeanID.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.Params.equals("99") && this.filterType.Params.length() != 0) {
                reStr += " and FParams =" + dbl.sqlString(filterType.Params);
            }
            if (!this.filterType.ReturnType.equals("99") && this.filterType.ReturnType.length() != 0) {
                reStr += " and FReturnType =" + dbl.sqlString(filterType.ReturnType);
            }

            if (this.filterType.FormCode != null && this.filterType.FormCode.trim().length() != 0) {
                reStr += " and FFormCode like '" + filterType.FormCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.CtlName != null && this.filterType.CtlName.trim().length() != 0) {
                reStr += " and FCtlName like '" + filterType.CtlName.replaceAll("'", "''") + "%'";
            }

            if (!this.filterType.ModuleCode.equals("99") && this.filterType.ModuleCode.length() != 0) {
                reStr += " and FModuleCode =" + dbl.sqlString(filterType.ModuleCode);
            }
        }
        return reStr;
    }

    public String getListViewData1() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "", sVocStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = getListView1Headers();
            sqlStr = "select a.*,n.Fvocname as FReturnTypeName,m.Fvocname as FModuleName,k.FBARNAME as FFormName from TB_FUN_SPINGINVOKE a " +
                " left join Tb_Fun_Vocabulary n on a.FReturnType = n.FVocCode and n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_RETURNTYPE) +
                " left join Tb_Fun_Vocabulary m on a.FModuleCode = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_MODULECODE) +
                " left join (select FBARCODE,FBARNAME from TB_Fun_Menubar) k on a.FFormCode = k.FBARCODE" +
                (bShow ? builerFilter() : "") +
                " order by a.FSICode";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setSpringAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_FUN_RETURNTYPE + "," +
                                        YssCons.YSS_FUN_MODULECODE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取Spring调用信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标 modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String getListViewData2() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "Spring调用代码\tSpring调用名称";
            sqlStr = "select a.*,n.Fvocname as FReturnTypeName,m.Fvocname as FModuleName,k.FBARNAME as FFormName from TB_FUN_SPINGINVOKE a " +
                " left join Tb_Fun_Vocabulary n on a.FReturnType = n.FVocCode and n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_RETURNTYPE) +
                " left join Tb_Fun_Vocabulary m on a.FModuleCode = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_MODULECODE) +
                " left join (select FBARCODE,FBARNAME from TB_Fun_Menubar) k on a.FFormCode = k.FBARCODE" +
                (bShow ? builerFilter() : "") +
                " order by a.FSICode";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FSICode")).append("\t");
                bufShow.append(rs.getString("FSIName")).append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                setSpringAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

        } catch (Exception e) {
            throw new YssException("获取Spring调用信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标 modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
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

    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\f") >= 0) {
                sTmpStr = sRowStr.split("\r\f")[0];
            } else {
                sTmpStr = sRowStr;
                reqAry = sTmpStr.split("\t");
            }
            if (reqAry == null)
            {
            	return ;
            }
            this.SICode = reqAry[0];
            if (reqAry[0].length() == 0) {
                this.SICode = " ";
            }
            this.SIName = reqAry[1];
            if (reqAry[1].length() == 0) {
                this.SIName = " ";
            }
            this.ReturnType = reqAry[2];

            this.FormCode = reqAry[3];
            this.CtlName = reqAry[4];

            this.ModuleCode = reqAry[5];
            this.BeanID = reqAry[6];
            this.Params = reqAry[7];
            this.desc = reqAry[8];
            //this.checkStateId =Integer.parseInt(reqAry[7]);
            this.oldSICode = reqAry[9];
            if (reqAry[9].length() == 0) {
                this.oldSICode = " ";
            }
            this.status = reqAry[10]; //lzp add 11.28
            //  super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SpringInvokeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析Spring调用出错!");
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.SICode).append("\t");
        buf.append(this.SIName).append("\t");
        buf.append(this.ReturnType).append("\t");
        buf.append(this.ReturnTypeName).append("\t");

        buf.append(this.FormCode).append("\t");
        buf.append(this.FormName).append("\t");
        buf.append(this.CtlName).append("\t");

        buf.append(this.ModuleCode).append("\t");
        buf.append(this.ModuleName).append("\t");
        buf.append(this.BeanID).append("\t");
        buf.append(this.Params).append("\t");
        buf.append(this.desc).append("\t");
        return buf.toString();

    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String getOldSICode() {
        return oldSICode;
    }

    public void setFilterType(SpringInvokeBean filterType) {
        this.filterType = filterType;
    }

    public void setOldSICode(String oldSICode) {
        this.oldSICode = oldSICode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public SpringInvokeBean getFilterType() {
        return filterType;
    }

    public String getDesc() {
        return desc;
    }

    public void setSIName(String SIName) {
        this.SIName = SIName;
    }

    public String getSIName() {
        return SIName;
    }

    public void setSICode(String SICode) {
        this.SICode = SICode;
    }

    public String getSICode() {
        return SICode;
    }

    public void setReturnTypeName(String ReturnTypeName) {
        this.ReturnTypeName = ReturnTypeName;
    }

    public String getReturnTypeName() {
        return ReturnTypeName;
    }

    public void setReturnType(String ReturnType) {
        this.ReturnType = ReturnType;
    }

    public String getReturnType() {
        return ReturnType;
    }

    public void setParams(String Params) {
        this.Params = Params;
    }

    public String getParams() {
        return Params;
    }

    public void setModuleName(String ModuleName) {
        this.ModuleName = ModuleName;
    }

    public String getModuleName() {
        return ModuleName;
    }

    public void setModuleCode(String ModuleCode) {
        this.ModuleCode = ModuleCode;
    }

    public String getModuleCode() {
        return ModuleCode;
    }

    public void setBeanID(String BeanID) {
        this.BeanID = BeanID;
    }

    public String getBeanID() {
        return BeanID;
    }

    public String getFormCode() {
        return FormCode;
    }

    public String getFormName() {
        return FormName;
    }

    public void setFormName(String FormName) {
        this.FormName = FormName;
    }

    public void setFormCode(String FormCode) {
        this.FormCode = FormCode;
    }

    public String getCtlName() {
        return CtlName;
    }

    public void setCtlName(String CtlName) {
        this.CtlName = CtlName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
