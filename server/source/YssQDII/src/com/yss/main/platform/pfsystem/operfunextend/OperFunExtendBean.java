package com.yss.main.platform.pfsystem.operfunextend;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;
import oracle.jdbc.*;
import oracle.sql.*;

/**
 * <p>Title: 通用业务扩展配置</p>
 *
 * <p>Description: 进行配置信息设置</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OperFunExtendBean
    extends BaseDataSettingBean implements IDataSetting {

    private String extCode = ""; //配置代码
    private String extName = ""; //配置名称
    private String pubParaCode = ""; //参数编号
    private String pubParaName = ""; //参数名称
    private String linkModule = ""; //调用模块代码
    private String linkModuleName = ""; //调用模块名称
    private String extScript = ""; //配置脚本
    private int enable; //是否可用代码
    private String enableName = ""; //是否可用名称
    private String desc = ""; //描述
    private String oldExtCode = "";
    private OperFunExtendBean filterType = null;
    private String sRecycled = "";

    public String getDesc() {
        return desc;
    }

    public int getEnable() {
        return enable;
    }

    public String getExtCode() {
        return extCode;
    }

    public String getExtName() {
        return extName;
    }

    public String getExtScript() {
        return extScript;
    }

    public String getLinkModule() {
        return linkModule;
    }

    public String getPubParaCode() {
        return pubParaCode;
    }

    public String getEnableName() {
        return enableName;
    }

    public String getLinkNoduleName() {
        return linkModuleName;
    }

    public OperFunExtendBean getFilterType() {
        return filterType;
    }

    public String getOldExtCode() {
        return oldExtCode;
    }

    public String getPubParaName() {
        return pubParaName;
    }

    public String getLinkModuleName() {
        return linkModuleName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public void setExtScript(String extScript) {
        this.extScript = extScript;
    }

    public void setLinkModule(String linkModule) {
        this.linkModule = linkModule;
    }

    public void setPubParaCode(String pubParaCode) {
        this.pubParaCode = pubParaCode;
    }

    public void setEnableName(String enableName) {
        this.enableName = enableName;
    }

    public void setLinkNoduleName(String linkModuleName) {
        this.linkModuleName = linkModuleName;
    }

    public void setFilterType(OperFunExtendBean filterType) {
        this.filterType = filterType;
    }

    public void setOldExtCode(String oldExtCode) {
        this.oldExtCode = oldExtCode;
    }

    public void setPubParaName(String pubParaName) {
        this.pubParaName = pubParaName;
    }

    public void setLinkModuleName(String linkModuleName) {
        this.linkModuleName = linkModuleName;
    }

    public OperFunExtendBean() {
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = " WHERE 1=1";

        if (this.filterType != null) {
            if (this.filterType.extCode.length() != 0) {
                sResult = sResult + " and a.FExtCode like '" +
                    filterType.extCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.extName.length() != 0) {
                sResult = sResult + " and a.FExtName like '" +
                    filterType.extName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.pubParaCode.length() != 0) {
            	//add by yangheng MS01694 QDV4赢时胜(上海开发部)2010年09月03日04_B  2010.09.06
                /*sResult = sResult + " and a.FPubParaCode like = '" +
                    filterType.pubParaCode.replaceAll("'", "''") + "%'";*/
                sResult = sResult + " and a.FPubParaCode like  '" +
                filterType.pubParaCode.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.linkModule.equalsIgnoreCase("99")) {
            	//add by yangheng MS01694 QDV4赢时胜(上海开发部)2010年09月03日04_B  2010.09.06
                /*sResult = sResult + " and a.FLinkModule = '" +
                    filterType.linkModule.replaceAll("'", "''") + "%'";*/
            	sResult = sResult + " and a.FLinkModule = '" +
                filterType.linkModule.replaceAll("'", "''") + "'";
            }
        }
        return sResult;
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.extCode).append("\t");
        buf.append(this.extName).append("\t");
        buf.append(this.pubParaCode).append("\t");
        buf.append(this.pubParaName).append("\t");
        buf.append(this.linkModule).append("\t");
        buf.append(this.linkModuleName).append("\t");
        buf.append(this.extScript).append("\t");
        buf.append(this.enable).append("\t");
        buf.append(this.enableName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void parseRowStr(String sRowStr) throws YssException {
        if (sRowStr.length() == 0) {
            return;
        }
        this.sRecycled = sRowStr;
        String[] tmpAry = sRowStr.split("\t");
        this.extCode = tmpAry[0];
        this.extName = tmpAry[1];
        this.pubParaCode = tmpAry[2];
        this.linkModule = tmpAry[3];
        /**shashijie 2011-09-26 BUG 2234 脚本中有回车还原和清除有问题 */
        if (tmpAry[4] != null && tmpAry[4].indexOf("【Enter】") > -1) {
        	this.extScript= tmpAry[4].replaceAll("【Enter】", "\r\n");
        }else{
        	this.extScript = tmpAry[4];
        }
        /**end*/
        this.enable = (tmpAry[5].equalsIgnoreCase("True") ? 1 : 0);
        //---add by songjie 2011.04.13 BUG 1669 QDV4赢时胜(测试)2011年4月8日02_B---//
       	if (tmpAry[6] != null && tmpAry[6].indexOf("【Enter】") > -1){
       		 this.desc= tmpAry[6].replaceAll("【Enter】", "\r\n");
        }else{
           	 this.desc = tmpAry[6];
        }
        //---add by songjie 2011.04.13 BUG 1669 QDV4赢时胜(测试)2011年4月8日02_B---//
        this.oldExtCode = tmpAry[7];
        if (tmpAry[8].length() != 0) {
            this.checkStateId = Integer.parseInt(tmpAry[8]);
        }
        super.parseRecLog();

        if (sRowStr.indexOf("\r\t") >= 0) {
            if (this.filterType == null) {
                this.filterType = new OperFunExtendBean();
                this.filterType.setYssPub(pub);
            }
            if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        }
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "TB_PFSys_OperFunExtend",
                               "FExtCode",
                               this.extCode, this.oldExtCode);
    }

    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false;
        String[] arrRecy = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled.length() != 0) {
                arrRecy = sRecycled.split("\r\n");
                for (int i = 0; i < arrRecy.length; i++) {
                    if (arrRecy[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrRecy[i]);
                    strSql = "UPDATE TB_PFSys_OperFunExtend SET " +
                        " FCheckState = " + this.checkStateId + "," +
                        " FCheckUser = " + dbl.sqlString(pub.getUserCode()) + "," +
                        " FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        " WHERE FExtCode = " + dbl.sqlString(this.extCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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

    public String addSetting() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        String strSql = "";
        String strUpdate = "";
        boolean bTrans = false;
        try {
            bufSql.append("INSERT INTO TB_PFSys_OperFunExtend(FExtCode, FExtName, FPubParaCode, FLinkModule, FExtScript, FEnable, FDesc, FCheckState, FCreator, FCreateTime) ");
            bufSql.append("VALUES(");
            bufSql.append(dbl.sqlString(this.extCode) + ",");
            bufSql.append(dbl.sqlString(this.extName) + ",");
            bufSql.append(dbl.sqlString(this.pubParaCode) + ",");
            bufSql.append(dbl.sqlString(this.linkModule) + ",");
            if (dbl.dbType == YssCons.DB_ORA) {
                bufSql.append("EMPTY_CLOB()" + ",");
            } else {
                bufSql.append(dbl.sqlString(this.extScript) + ",");
            }
            bufSql.append(this.enable + ",");
            bufSql.append(dbl.sqlString(this.desc) + ",");
            bufSql.append(this.checkStateId + ",");
            bufSql.append(dbl.sqlString(this.creatorCode) + ",");
            bufSql.append(dbl.sqlString(this.creatorTime) + ")");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());

            if (dbl.dbType == YssCons.DB_ORA) {
                strSql = "SELECT FExtScript FROM TB_PFSys_OperFunExtend " +
                    " WHERE FExtCode = " + dbl.sqlString(this.extCode);
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	  // modify by jsc 20120809 连接池对大对象的特殊处理
                	CLOB clob =dbl.CastToCLOB(rs.getClob("FExtScript"));  
                    //CLOB clob = ( (OracleResultSet) rs).getCLOB("FExtScript");
                    clob.putString(1, this.extScript);
                    strUpdate = "UPDATE TB_PFSys_OperFunExtend" +
                        " SET FExtScript = ? WHERE FExtCode = " +
                        dbl.sqlString(this.extCode);
                    PreparedStatement pstmt = conn.prepareStatement(strUpdate);
                    pstmt.setClob(1, clob);
                    pstmt.executeUpdate();
                    pstmt.close();
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增通用业务扩展配置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String editSetting() throws YssException {
        StringBuffer buf = new StringBuffer();
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        String strSql = "";
        String strUpdate = "";
        boolean bTrans = false;
        try {
            buf.append("UPDATE TB_PFSys_OperFunExtend SET ");
            buf.append(" FExtCode = " + dbl.sqlString(this.extCode) + ",");
            buf.append(" FExtName = " + dbl.sqlString(this.extName) + ",");
            buf.append(" FPubParaCode = " + dbl.sqlString(this.pubParaCode) + ",");
            buf.append(" FLinkModule = " + dbl.sqlString(this.linkModule) + ",");
            if (dbl.dbType == YssCons.DB_ORA) {
                buf.append(" FExtScript = EMPTY_CLOB(),");
            } else {
                buf.append(" FExtScript = " + dbl.sqlString(this.extScript) + ",");
            }
            buf.append(" FEnable = " + this.enable + ",");
            buf.append(" FDesc = " + dbl.sqlString(this.desc));
            buf.append(" WHERE FExtCode = " + dbl.sqlString(this.oldExtCode));

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(buf.toString());

            if (dbl.dbType == YssCons.DB_ORA) {
                strSql = "SELECT FExtScript FROM TB_PFSys_OperFunExtend " +
                    " WHERE FExtCode = " + dbl.sqlString(this.extCode);
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	  // modify by jsc 20120809 连接池对大对象的特殊处理
                	CLOB clob = dbl.CastToCLOB(rs.getClob("FExtScript"));
                    //CLOB clob = ( (OracleResultSet) rs).getCLOB("FExtScript");
                    clob.putString(1, this.extScript);
                    strUpdate = "UPDATE TB_PFSys_OperFunExtend" +
                        " SET FExtScript = ? WHERE FExtCode = " +
                        dbl.sqlString(this.extCode);
                    PreparedStatement pstmt = conn.prepareStatement(strUpdate);
                    pstmt.setClob(1, clob);
                    pstmt.executeUpdate();
                    pstmt.close();
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改通用业务扩展配置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     * 删除数据即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false;
        try {
            strSql = "UPDATE TB_PFSys_OperFunExtend SET" +
                " FCheckState = " + this.checkStateId + "," +
                " FCheckUser = " + dbl.sqlString(pub.getUserCode()) + "," +
                " FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " WHERE FExtCode = " + dbl.sqlString(this.extCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除通用业务扩展配置信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 删除回收站的数据即从回收站彻底删除数据
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false;
        String[] arrRecy = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled.length() != 0) {
                arrRecy = sRecycled.split("\r\n");
                for (int i = 0; i < arrRecy.length; i++) {
                    if (arrRecy[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrRecy[i]);
                    strSql = "DELETE FROM TB_PFSys_OperFunExtend " +
                        " WHERE FExtCode = " + dbl.sqlString(this.extCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("彻底删除通用业务扩展配置信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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

    public String getAllSetting() {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        try {

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return "";
    }

    public String getListViewData1() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sVocStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "SELECT a.FExtCode, a.FExtName, a.FPubParaCode, d.FPubParaName, a.FLinkModule, m.fvocname AS FLinkModuleName, a.FExtScript, a.FEnable, n.fvocname AS FEnableName, a.FDesc, " +
                "b.FUserName as FCreatorName, a.FCreateTime, c.FUserName as FCheckUserName, a.FCheckTime, a.FCheckState, a.FCreator, a.FCheckUser FROM " +
                "TB_PFSys_OperFunExtend a " +
                "LEFT JOIN (SELECT FUserCode,FUserName FROM Tb_Sys_UserList) b ON a.FCreator = b.FUserCode " +
                "LEFT JOIN (SELECT FUserCode,FUserName FROM Tb_Sys_UserList) c ON a.FCheckUser = c.FUserCode " +
                "LEFT JOIN (SELECT DISTINCT FPubParaCode, FPubParaName FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                ") d ON a.FPubParaCode =  d.FPubParaCode " +
                "LEFT JOIN Tb_Fun_Vocabulary m ON a.FLinkModule = m.FVocCode AND m.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PFSYS_OPEREXTENDLINKMOD) +
                " LEFT JOIN Tb_Fun_Vocabulary n ON a.FEnable = n.FVocCode AND n.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PFSYS_OPEREXTENDENABLE) +
                this.buildFilterSql();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.extCode = rs.getString("FExtCode");
                this.extName = rs.getString("FExtName");
                this.pubParaCode = rs.getString("FPubParaCode");
                this.pubParaName = rs.getString("FPubParaName");
                this.linkModule = rs.getString("FLinkModule");
                this.linkModuleName = rs.getString("FLinkModuleName");
                this.extScript = dbl.clobStrValue(rs.getClob("FExtScript"));
                this.enable = rs.getInt("FEnable");
                this.enableName = rs.getString("FEnableName");
                this.desc = rs.getString("FDesc");
                super.setRecLog(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_PFSYS_OPEREXTENDLINKMOD + "," + YssCons.YSS_PFSYS_OPEREXTENDENABLE);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        try {

        } catch (Exception e) {
            throw new YssException(e);
        }
        return "";
    }

    public String getListViewData3() throws YssException {
        try {

        } catch (Exception e) {
            throw new YssException(e);
        }
        return "";
    }

    public String getListViewData4() throws YssException {
        try {

        } catch (Exception e) {
            throw new YssException(e);
        }
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
