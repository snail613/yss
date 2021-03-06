package com.yss.main.platform.pfsystem.facecfg;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class FaceCfgInfoBean
    extends BaseDataSettingBean implements
    IDataSetting {

    private String sCtlGrpCode = ""; //参数控件组代码
    private String sCtlGrpName = ""; //参数控件组名称
    private String sCtlCode = ""; //控件代码
    private int iParamIndex = 0; //控件索引
    private int iCtlType = 0; //控件类型
    private String sParams = ""; //控件参数
    private String sCtlInd = ""; //控件标识
    private String sFunModules = ""; //功能模块
    private String sFunModulesName = ""; //功能模块名称
    private String IsOnlyColumn = ""; //是否只显示列名

    private String RepParamCtrls; //参数控件

    private String oldCtlGrpCode = "";
    private String oldCtlCode = "";

    private FaceCfgInfoBean filterType;
    private String sRecycled = "";
    public FaceCfgInfoBean() {
    }

    /**
     * parseRowStr
     * 解析数据
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
                if (sRowStr.split("\r\t").length == 3) {
                    this.RepParamCtrls = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sTmpStr;
            reqAry = sTmpStr.split("\t");
            this.sCtlGrpCode = reqAry[0];
            this.sCtlGrpName = reqAry[1];
            this.sCtlCode = reqAry[2];
            if (YssFun.isNumeric(reqAry[3])) {
                this.iParamIndex = YssFun.toInt(reqAry[3]);
            }
            if (YssFun.isNumeric(reqAry[4])) {
                this.iCtlType = YssFun.toInt(reqAry[4]);
            }
            this.sParams = reqAry[5];
            this.sCtlInd = reqAry[6];
            this.sFunModules = reqAry[7];
            ///
            if (YssFun.isNumeric(reqAry[8])) {
                super.checkStateId = Integer.parseInt(reqAry[8]);
            }
            this.oldCtlGrpCode = reqAry[9];
            this.oldCtlCode = reqAry[10];
            // this.IsOnlyColumn = reqAry[10];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new FaceCfgInfoBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析参数控件组设置出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sCtlGrpCode).append("\t");
        buf.append(this.sCtlGrpName).append("\t");
        buf.append(this.sCtlCode).append("\t");
        buf.append(this.iParamIndex).append("\t");
        buf.append(this.iCtlType).append("\t");
        buf.append(this.sParams).append("\t");
        buf.append(this.sCtlInd).append("\t");
        buf.append(this.sFunModules).append("\t");
        buf.append(this.sFunModulesName).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_PFSys_FaceCfgInfo", "FCtlGrpCode",
                               this.sCtlGrpCode, this.oldCtlGrpCode);
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = null;
        PreparedStatement stm = null;
        String[] arrData = null;
        String sqlStr = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            bTrans = true;
            conn.setAutoCommit(false);
            this.parseRowStr(sMutilRowStr);
            //2008.03.13 添加 蒋锦
            if (RepParamCtrls == null) {
                throw new YssException("控件不能为空，请至少添加一个控件！");
            }
            arrData = RepParamCtrls.split(YssCons.YSS_LINESPLITMARK);
            //----MS00323 QDV4赢时胜（上海）2009年3月16日01_B add by songjie 2009.03.26----//
            if (!this.oldCtlGrpCode.equals("")) {
                sqlStr = "delete from Tb_PFSys_FaceCfgInfo where FCtlGrpCode ="
                    + dbl.sqlString(this.oldCtlGrpCode); //删除关于修改前的代码的所有信息
                dbl.executeSql(sqlStr);
            }
            //----MS00323 QDV4赢时胜（上海）2009年3月16日01_B add by songjie 2009.03.26----//
            sqlStr = "delete from Tb_PFSys_FaceCfgInfo where FCtlGrpCode ="
                + dbl.sqlString(this.sCtlGrpCode); //删除这个组
            dbl.executeSql(sqlStr);
            sqlStr = "insert into Tb_PFSys_FaceCfgInfo " +
                "(FCtlGrpCode,FCtlGrpName,FCtlCode,FParamIndex,FCtlType,FParam,FCtlInd,FFunModules,FCheckState,FCreator,FCreateTime)" +
                " values(?,?,?,?,?,?,?,?,?,?,?)";
            stm = dbl.openPreparedStatement(sqlStr);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    return "";
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.sCtlGrpCode);
                stm.setString(2, this.sCtlGrpName);
                stm.setString(3, this.sCtlCode);
                stm.setInt(4, this.iParamIndex);
                stm.setInt(5, this.iCtlType);
                stm.setString(6, this.sParams);
                stm.setString(7, this.sCtlInd);
                stm.setString(8, this.sFunModules);
                stm.setInt(9, pub.getSysCheckState() ? 0 : 1);
                stm.setString(10, this.creatorCode);
                stm.setString(11, this.creatorTime);
                // stm.setString(12,this.sCtlGrpCode);
                stm.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存窗体配置数据出错", e);
        } finally {
        	dbl.closeStatementFinal(stm);//add by rujiangpeng 20100603打开多张报表系统需重新登录
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*,e.FVocName as FFunModulesName from Tb_PFSys_FaceCfgInfo " +
                " a " +
                " left join Tb_Fun_Vocabulary e on a.FFunModules = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PF_FUNMODULES) +
                " where a.FCheckState = 1 and a.FCtlGrpCode = " +
                dbl.sqlString(sCtlGrpCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                setRepAttr(rs);
            }
            return null;
        } catch (Exception e) {
            throw new YssException("获取报表参数控件组设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = " where 1 = 1 ";
        if (this.IsOnlyColumn.equalsIgnoreCase("1")) {
            sResult += " and 1 = 2 ";
            return sResult;
        }
        if (this.filterType != null) {
            if (this.filterType.sCtlGrpCode.length() != 0) {
                sResult = sResult + " and a.FCtlGrpCode = '" +
                    filterType.sCtlGrpCode + "'";
            }
            if (this.filterType.sCtlGrpName.length() != 0) {
                sResult = sResult + " and a.FCtlGrpName like '" +
                    filterType.sCtlGrpName.replaceAll("'", "''") + "%'";
            }
            //edit by rujiangpeng 20100428 MS01108 QDV4上海2010年04月20日01_B 
            if (this.filterType.sFunModules.length() != 0 &&
                !filterType.sFunModules.equals("99")) {
                sResult += "and a.FFunModules like '%" +
                    filterType.sFunModules.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public void setRepAttr(ResultSet rs) throws SQLException {
        this.sCtlGrpCode = rs.getString("FCtlGrpCode") + "";
        this.sCtlGrpName = rs.getString("FCtlGrpName") + "";
        this.sCtlCode = rs.getString("FCtlCode");
        this.iParamIndex = rs.getInt("FParamIndex");
        this.iCtlType = rs.getInt("FctlType");
        this.sParams = rs.getString("FParam");
        this.sCtlInd = rs.getString("FCtlInd");
        this.sFunModules = rs.getString("FFunModules");
        this.sFunModulesName = rs.getString("FFunModulesName");

        super.setRecLog(rs);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.setRepAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_PF_FUNMODULES);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取参数控件组信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData1
     * 获取参数控件组数据
     * @return String
     */
    public String getListViewData1() throws YssException { //显示所有的控件组
        String strSql = "";
        strSql = "select a.*,'' as FCtlCode,0 as FParamIndex,0 as FCtlType,'' as FParam,'' as FCtlInd," +
            //" '' as FFunModules,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,'' as FCreator,"+ //修改,原本不用的 by ly 080618
            " '' as FFunModules,'' as FCreatorName,'' as FCheckUserName,'' as FCreator," +
            " '' as FCheckUser,'' as FCheckTime,'' as FcreateTime,e.FVocName as FFunModulesName from " +
            " (select distinct(FCtlGrpCode),FCtlGrpName,FCheckState,FFunModules,'' as FCreator,'' as FCheckUser from Tb_PFSys_FaceCfgInfo ) a " +
            //" left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            //" left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join Tb_Fun_Vocabulary e on a.FFunModules = e.FVocCode and e.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PF_FUNMODULES) +
            buildFilterSql() + " order by a.FCheckState "; //这里得去掉fcheckstate==2的
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        /* String strSql = "";
         strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Rep_ParamCtlGrp")+" a " +
               " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
               " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
         buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
         return this.builderListViewData(strSql);*/
        return "";
    }

    public String getListViewData2() throws YssException { //一组控件的 显示
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FVocName as FFunModulesName from Tb_PFSys_FaceCfgInfo  a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary e on a.FFunModules = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PF_FUNMODULES) +
                buildFilterSql() +
                /**shashijie 2011-09-23 BUG 2235 注视原因,回收站的数据无法双击显示具体信息,看不到具体详细信息*/
                //" and a.FCheckState <> 2 " +
                /**end*/
                " order by a.FCheckState, a.FCreateTime desc";
            sHeader = this.getListView3Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView3ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.setRepAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
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
                "\r\f" +
                this.getListView3ShowCols();
        } catch (Exception e) {
            throw new YssException("解析控件出错", e);
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
     * addSetting
     * 新增参数控件组
     * @return String
     */
    public String addSetting() throws YssException {
        /*  String strSql = "";
          boolean bTrans = false; //代表是否开始了事务
          Connection conn = dbl.loadConnection();
          try {
             strSql =
         "insert into " + pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                      "(FCtlGrpCode, FCtlGrpName, "+
                      " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                      " values(" + dbl.sqlString(this.sCtlGrpCode) + "," +
                      dbl.sqlString(this.sCtlGrpName) + "," +
                      (pub.getSysCheckState()?"0":"1") + "," +
                      dbl.sqlString(this.creatorCode) + "," +
                      dbl.sqlString(this.creatorTime) + "," +
         (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";

             dbl.executeSql(strSql);

             if (this.RepParamCtrls != null) {
                FaceCfgParamBean faceCfgInfo = new FaceCfgParamBean();
                faceCfgInfo.setYssPub(pub);
         faceCfgInfo.saveMutliSetting(this.RepParamCtrls, true, this.CtlGrpCode);
             }

             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
             return buildRowStr();
          }
          catch (Exception e) {
             throw new YssException("新增界面配置数据信息出错", e);
          }
          finally {
             dbl.endTransFinal(conn, bTrans);
          }*/
        return "";
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] arrData = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].trim().length() == 0) {
                    continue;
                }
                parseRowStr(arrData[i]);
                strSql = "update tb_pfSys_facecfginfo set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FCtlGrpCode = " +
                    dbl.sqlString(this.sCtlGrpCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核参数控件组信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update tb_pfSys_facecfginfo set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FCtlGrpCode = " +
                dbl.sqlString(this.sCtlGrpCode);

            dbl.executeSql(strSql);

            /*strSql = "update " + pub.yssGetTableName("Tb_Rep_ParamCtl")  + " set FCheckState = " +
                  this.checkStateId +
                  ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
             ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                  "' where FCtlGrpCode = " +
                  dbl.sqlString(this.CtlGrpCode);

                      dbl.executeSql(strSql);*/

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除参数控件组信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        /* boolean bTrans = false; //代表是否开始了事务
         Connection conn = dbl.loadConnection();
         try {
            strSql = "update Tb_PFSys_FaceCfgInfo"  + " set FCtlGrpCode = " +
                  dbl.sqlString(this.sCtlGrpCode) + ", FCtlGrpName = " +
                  dbl.sqlString(this.sCtlGrpName) + ",FCheckState = " +
                  (pub.getSysCheckState()?"0":"1") + ",FCreator = " +
                  dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                  dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
         (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                  " where FCtlGrpCode = " +
                  dbl.sqlString(this.oldCtlGrpCode);

            dbl.executeSql(strSql);

         if (this.RepParamCtrls != null && this.RepParamCtrls.length() > 0) {
               FaceCfgParamBean faceCfgInfo = new FaceCfgParamBean();
               faceCfgInfo.setYssPub(pub);
         faceCfgInfo.saveMutliSetting(this.RepParamCtrls, true, this.oldCtlGrpCode);
            }
            else
            {
         strSql = "delete from " + pub.yssGetTableName("Tb_Rep_ParamCtl") +
                     " where FCtlGrpCode = " +
                     dbl.sqlString(this.oldCtlGrpCode);
               dbl.executeSql(strSql);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
         }
         catch (Exception e) {
            throw new YssException("修改参数控件组信息出错", e);
         }
         finally {
            dbl.endTransFinal(conn, bTrans);
         }*/
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        if (sType.equalsIgnoreCase("Title")) {
            return this.getListView3Headers() + "\r\f\r\f\r\f" +
                this.getListView3ShowCols();
        }
        if (sType.equalsIgnoreCase("getCtls")) {
            String strSql = "";
            String sAllDataStr = "";
            StringBuffer bufAll = new StringBuffer();
            ResultSet rs = null;
            try {
                strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FVocName as FFunModulesName from " +
                    " Tb_PFSys_FaceCfgInfo " + " a " +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " left join Tb_Fun_Vocabulary e on a.FFunModules = e.FVocCode and e.FVocTypeCode = " +
                    dbl.sqlString(YssCons.YSS_PF_FUNMODULES) +
                    " where a.FCheckState <> 2 and a.FCtlGrpCode = " +
                    dbl.sqlString(this.sCtlGrpCode) +
                    " order by a.FCheckState, a.FCreateTime desc";

                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    this.setRepAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }

                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0,
                        bufAll.toString().length() - 2);
                }

                return sAllDataStr;
            } catch (Exception e) {
                throw new YssException("获取参数控件信息出错", e);
            } finally {
                dbl.closeResultSetFinal(rs);
            }
        }

        return "";

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
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
//            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                parseRowStr(arrData[i]);
                strSql =
                    " delete from Tb_PFSys_FaceCfgInfo where FCtlGrpCode =" +
                    dbl.sqlString(sCtlGrpCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            try {
                conn.rollback();
            } catch (SQLException sqlE) {
                throw new YssException("清除数据失败", ex);
            }
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String getSParams() {
        return sParams;
    }

    public String getSFunModulesName() {
        return sFunModulesName;
    }

    public String getSFunModules() {
        return sFunModules;
    }

    public String getSCtlInd() {
        return sCtlInd;
    }

    public String getSCtlGrpName() {
        return sCtlGrpName;
    }

    public String getSCtlGrpCode() {
        return sCtlGrpCode;
    }

    public String getSCtlCode() {
        return sCtlCode;
    }

    public String getOldCtlGrpCode() {
        return oldCtlGrpCode;
    }

    public String getOldCtlCode() {
        return oldCtlCode;
    }

    public int getIParamIndex() {
        return iParamIndex;
    }

    public int getICtlType() {
        return iCtlType;
    }

    public void setFilterType(FaceCfgInfoBean filterType) {
        this.filterType = filterType;
    }

    public void setSParams(String sParams) {
        this.sParams = sParams;
    }

    public void setSFunModulesName(String sFunModulesName) {
        this.sFunModulesName = sFunModulesName;
    }

    public void setSFunModules(String sFunModules) {
        this.sFunModules = sFunModules;
    }

    public void setSCtlInd(String sCtlInd) {
        this.sCtlInd = sCtlInd;
    }

    public void setSCtlGrpName(String sCtlGrpName) {
        this.sCtlGrpName = sCtlGrpName;
    }

    public void setSCtlGrpCode(String sCtlGrpCode) {
        this.sCtlGrpCode = sCtlGrpCode;
    }

    public void setSCtlCode(String sCtlCode) {
        this.sCtlCode = sCtlCode;
    }

    public void setOldCtlGrpCode(String oldCtlGrpCode) {
        this.oldCtlGrpCode = oldCtlGrpCode;
    }

    public void setOldCtlCode(String oldCtlCode) {
        this.oldCtlCode = oldCtlCode;
    }

    public void setIParamIndex(int iParamIndex) {
        this.iParamIndex = iParamIndex;
    }

    public void setICtlType(int iCtlType) {
        this.iCtlType = iCtlType;
    }

    public FaceCfgInfoBean getFilterType() {
        return filterType;
    }

    public void setRepParamCtrls(String RepParamCtrls) {
        this.RepParamCtrls = RepParamCtrls;
    }

    public String getRepParamCtrls() {
        return RepParamCtrls;
    }

    public void setIsOnlyColumn(String IsOnlyColumn) {
        this.IsOnlyColumn = IsOnlyColumn;
    }

    public String getIsOnlyColumn() {
        return IsOnlyColumn;
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
