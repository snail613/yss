package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class PerformulaBean
    extends BaseDataSettingBean implements IDataSetting {
    private String formulaCode = ""; //比率公式代码
    private String formulaName = ""; //比率公式名称
    private String linkType = "0"; //链接类型
    private String perType = "0"; //比率类型
    private String desc = ""; //描述说明

    private PerformulaBean filterType;
    private String performularelas;
    private String oldFormulaCode = "";
    private String sRecycled = "";

    public void setFormulaCode(String formulaCode) {
        this.formulaCode = formulaCode;
    }

    public void setPerType(String perType) {
        this.perType = perType;
    }

    public String getFormulaCode() {
        return formulaCode;
    }

    public String getPerType() {
        return perType;
    }

    public PerformulaBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.formulaCode).append("\t");
        buf.append(this.formulaName).append("\t");
        buf.append(this.linkType).append("\t");
        buf.append(this.perType).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查比率公式输入数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Performula"),
                               "FFormulaCode",
                               this.formulaCode, this.oldFormulaCode);
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
     * getListViewData1
     * 获取比率公式数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql =
            "select a.*, d.FVocName as FLinkTypeValue, b.FUserName as FCreatorName," +
            "c.FUserName as FCheckUserName,e.FVocName as FPerTypeValue from " +
            pub.yssGetTableName("Tb_Para_Performula") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FLinkType") + " = d.FVocCode and d.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PFM_LINKTYPE) +
            " left join Tb_Fun_Vocabulary e on " + dbl.sqlToChar("a.FPerType") + " = e.FVocCode and e.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PFM_PERTYPE) +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     * 获取已审核的比率公式数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "比率公式代码\t比率公式名称\t比率公式描述";
            strSql =
                "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Performula") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FFormulaCode") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FFormulaName") + "").trim());
                bufShow.append("\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40));
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                setPerformula(rs);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用比率公式信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * getSetting
     *
     * @return IParaSetting
     */
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
     * parseRowStr
     * 解析比率公式数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.performularelas = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.formulaCode = reqAry[0];
            this.formulaName = reqAry[1];
            this.linkType = reqAry[2];
            this.perType = reqAry[3];
            this.desc = reqAry[4];
            super.checkStateId = Integer.parseInt(reqAry[5]);
            this.oldFormulaCode = reqAry[6];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new PerformulaBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析比率公式设置请求出错", e);
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

    /**
     * saveSetting
     * 更新比率公式数据
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) throws YssException {
         String strSql = "";
         boolean bTrans = false; //代表是否开始了事务
         Connection conn = dbl.loadConnection();
         try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (btOper == YssCons.OP_ADD) {
               strSql = "insert into " + pub.yssGetTableName("Tb_Para_Performula") +
                     "" +
                     " (FFormulaCode,FFormulaName,FLinkType,FDesc," +
                     "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                     " values(" + dbl.sqlString(this.formulaCode) + "," +
                     dbl.sqlString(this.formulaName) + "," +
                     this.linkType + "," +
                     dbl.sqlString(this.desc) + "," +
                     (pub.getSysCheckState() ? "0" : "1") + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
                     (pub.getSysCheckState() ? "' '" :
                      dbl.sqlString(this.creatorCode)) + ")";
            }
            else if (btOper == YssCons.OP_EDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Performula") +
                     " set FFormulaCode = " +
                     dbl.sqlString(this.formulaCode) + ",FFormulaName = " +
                     dbl.sqlString(this.formulaName) + ",FLinkType = " +
                     this.linkType + ",FDesc = " +
                     dbl.sqlString(this.desc) + ",FCheckState = " +
                     (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                     dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                     dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                     (pub.getSysCheckState() ? "' '" :
                      dbl.sqlString(this.creatorCode)) +
                     " where FFormulaCode = " +
                     dbl.sqlString(this.oldFormulaCode);
            }
            else if (btOper == YssCons.OP_DEL) {
               //  strSql = "delete from " + pub.yssGetTableName("Tb_Para_Performula") + " where FFormulaCode = " +
               //删除时将审核标志修改为2
               strSql = "update " + pub.yssGetTableName("Tb_Para_Performula") +
                     " set FCheckState = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FFormulaCode = " +
                     dbl.sqlString(this.formulaCode);
            }
            else if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Performula") +
                     " set FCheckState = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FFormulaCode = " +
                     dbl.sqlString(this.formulaCode);
            }
            dbl.executeSql(strSql);

// 关联处理
            if (btOper == YssCons.OP_EDIT &&
                this.formulaCode != this.oldFormulaCode) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                     " set FFormulaCode = " +
                     dbl.sqlString(this.formulaCode) +
                     " where FFormulaCode = " +
                     dbl.sqlString(this.oldFormulaCode);
               dbl.executeSql(strSql);
            }

            if (this.performularelas != null) {
               if (btOper == YssCons.OP_ADD || btOper == YssCons.OP_EDIT) {
                  PerformulaRelaBean performulaRela = new PerformulaRelaBean();
                  performulaRela.setYssPub(pub);
                  performulaRela.saveMutliSetting(this.performularelas);
               }
            }
            if (btOper == YssCons.OP_DEL) {
               //  strSql = "delete from Tb_Para_Performula_Rela where frelacode = " +
               //删除时将审核标志修改为2
               strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                     " set FCheckState = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FFormulaCode = " +
                     dbl.sqlString(this.formulaCode);
               dbl.executeSql(strSql);
            }
            if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                     " set FCheckState = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FFormulaCode = " +
                     dbl.sqlString(this.formulaCode);
               dbl.executeSql(strSql);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
         }
         catch (Exception e) {
            throw new YssException("更新比率公式信息出错", e);
         }
         finally {
            dbl.endTransFinal(conn, bTrans);
         }
      }
     */
    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_Performula") +
                "" +
                " (FFormulaCode,FFormulaName,FLinkType,FPerType,FDesc," +
                "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.formulaCode) + "," +
                dbl.sqlString(this.formulaName) + "," +
                this.linkType + "," +
                this.perType + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";
            dbl.executeSql(strSql);

            if (this.performularelas != null) {

                PerformulaRelaBean performulaRela = new PerformulaRelaBean();
                performulaRela.setYssPub(pub);
                performulaRela.saveMutliSetting(this.performularelas);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加比率公式信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_Performula") +
                " set FFormulaCode = " +
                dbl.sqlString(this.formulaCode) + ",FFormulaName = " +
                dbl.sqlString(this.formulaName) + ",FLinkType = " +
                this.linkType + ",FPerType = " +
                this.perType +
                ",FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FFormulaCode = " +
                dbl.sqlString(this.oldFormulaCode);
            dbl.executeSql(strSql);
			/**shashijie 2012-7-2 STORY 2475 */
            if (!this.formulaCode.equals(this.oldFormulaCode)) {
			/**end*/
                strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " set FFormulaCode = " +
                    dbl.sqlString(this.formulaCode) +
                    " where FFormulaCode = " +
                    dbl.sqlString(this.oldFormulaCode);
                dbl.executeSql(strSql);
            }

            if (this.performularelas != null) {

                PerformulaRelaBean performulaRela = new PerformulaRelaBean();
                performulaRela.setYssPub(pub);
                performulaRela.saveMutliSetting(this.performularelas);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改比率公式信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，即是放入数据库
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_Performula") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FFormulaCode = " +
                dbl.sqlString(this.formulaCode);
            dbl.executeSql(strSql);
            strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FFormulaCode = " +
                dbl.sqlString(this.formulaCode);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除比率公式信息信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理货币方向的审核和未审核的单条信息。
     *  新方法功能：可以处理货币方向审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      try {
//         conn.setAutoCommit(false);
//         bTrans = true;
//         strSql = "update " + pub.yssGetTableName("Tb_Para_Performula") +
//              " set FCheckState = " +
//              this.checkStateId +
//              ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//              ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//              "' where FFormulaCode = " +
//              dbl.sqlString(this.formulaCode);
//          dbl.executeSql(strSql);
//          strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
//                     " set FCheckState = " +
//                     this.checkStateId +
//                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//                     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//                     "' where FFormulaCode = " +
//                     dbl.sqlString(this.formulaCode);
//              dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//
//      catch (Exception e) {
//         throw new YssException("审核比率公式信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改前的代码
        //------------------------
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            arrData = sRecycled.split("\r\n");
            bTrans = true;
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);

                strSql = "update " + pub.yssGetTableName("Tb_Para_Performula") +
                    " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "' where FFormulaCode = " +
                    dbl.sqlString(this.formulaCode);
                dbl.executeSql(strSql);
                strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "' where FFormulaCode = " +
                    dbl.sqlString(this.formulaCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核比率公式信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //--------------------------

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.formulaCode.length() != 0) {
                sResult = sResult + " and a.FFormulaCode like '" +
                    filterType.formulaCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.formulaName.length() != 0) {
                sResult = sResult + " and a.FFormulaName like '" +
                    filterType.formulaName.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.linkType.equalsIgnoreCase("99") &&
                this.filterType.linkType.length() != 0) {
                sResult = sResult + " and a.FLinkType = " +
                    filterType.linkType;
            }
			//add by rujiangpeng MS01034 比率公式设置界面筛选条件的问题 
            if (this.filterType.perType.length() != 0 && 
            		!this.filterType.perType.equalsIgnoreCase("99")) {
                    sResult = sResult + " and a.FPerType = " +
                        filterType.perType;
                }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public void setPerformula(ResultSet rs) throws SQLException {
        this.formulaCode = rs.getString("FFormulaCode") + "";
        this.formulaName = rs.getString("FFormulaName") + "";
        this.linkType = rs.getString("FLinkType") + "";
        this.perType = rs.getString("FPerType") + "";
        this.desc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setPerformula(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_PFM_LINKTYPE + "," + YssCons.YSS_PFM_PERTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取比率公式设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
        PerformulaBean befEditBean = new PerformulaBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, d.FVocName as FLinkTypeValue, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Performula") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FLinkType") + " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PFM_LINKTYPE) +
                " where  a.FFormulaCode =" + dbl.sqlString(this.oldFormulaCode) +
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.formulaCode = rs.getString("FFormulaCode") + "";
                befEditBean.formulaName = rs.getString("FFormulaName") + "";
                befEditBean.linkType = rs.getString("FLinkType") + "";
                befEditBean.desc = rs.getString("FDesc") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    /**
     * 从回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //根据规定的符号，把多个sql语句分别放入数组
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            //循环执行这些删除语句
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_Performula") +
                    " where FFormulaCode =  " + dbl.sqlString(this.formulaCode);
                dbl.executeSql(strSql);
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " where FFormulaCode =  " + dbl.sqlString(this.formulaCode);
                dbl.executeSql(strSql);

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("清除数据出错", e);
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
