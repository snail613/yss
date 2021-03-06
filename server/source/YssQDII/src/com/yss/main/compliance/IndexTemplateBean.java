package com.yss.main.compliance;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.basesetting.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class IndexTemplateBean
    extends BaseDataSettingBean implements IDataSetting {

    private String indexTemplateCode = ""; //模板代码
    private String indexTemplateName = ""; //模板名称
    private String Desc = ""; //描述

    private String secTypes = "";
    private String accTypes = "";

    private String oldIndexTemplateCode = "";

    private String subType = "";
    private java.util.Date startDate = new java.util.Date(); //启用日期
    private java.util.Date oldStartDate;
    private String hRecycled = ""; //回收站 MS01263 QDV4赢时胜(测试)2010年6月2日4_B 2010-06-22 fanghaoln
    private String category = "";
    private String account = "";

    private IndexTemplateBean filterType;
    public String getDesc() {
        return Desc;
    }

    public String getIndexTemplateCode() {
        return indexTemplateCode;
    }

    public java.util.Date getOldStartDate() {
        return oldStartDate;
    }

    public IndexTemplateBean getFilterType() {
        return filterType;
    }

    public String getOldIndexTemplateCode() {
        return oldIndexTemplateCode;
    }

    public String getIndexTemplateName() {
        return indexTemplateName;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public void setIndexTemplateCode(String indexTemplateCode) {
        this.indexTemplateCode = indexTemplateCode;
    }

    public void setOldStartDate(Date oldStartDate) {
        this.oldStartDate = oldStartDate;
    }

    public void setFilterType(IndexTemplateBean filterType) {
        this.filterType = filterType;
    }

    public void setOldIndexTemplateCode(String oldIndexTemplateCode) {
        this.oldIndexTemplateCode = oldIndexTemplateCode;
    }

    public void setIndexTemplateName(String indexTemplateName) {
        this.indexTemplateName = indexTemplateName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setSecTypes(String secTypes) {
        this.secTypes = secTypes;
    }

    public void setAccTypes(String accTypes) {
        this.accTypes = accTypes;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public String getCategory() {
        return category;
    }

    public String getAccount() {
        return account;
    }

    public String getSecTypes() {
        if (secTypes.length() == 0) {
            try {
                secTypes = getRandValue(this.indexTemplateCode, 0);
            } catch (YssException ex) {
            }
        }
        return secTypes;
    }

    public String getAccTypes() {
        if (accTypes.length() == 0) {
            try {
                accTypes = getRandValue(this.indexTemplateCode, 1);
            } catch (YssException ex) {
            }
        }
        return accTypes;
    }

    public IndexTemplateBean() {
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.category = sRowStr.split("\r\t")[2];
                }
                if (sRowStr.split("\r\t").length == 4) {
                    this.category = sRowStr.split("\r\t")[2];
                    this.account = sRowStr.split("\r\t")[3];
                }
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.hRecycled = sRowStr;//MS01263 QDV4赢时胜(测试)2010年6月2日4_B 2010-06-22 fanghaoln
            this.indexTemplateCode = reqAry[0];
            this.indexTemplateName = reqAry[1];
            this.Desc = reqAry[2];
            this.checkStateId = Integer.parseInt(reqAry[3]);
            this.startDate = YssFun.toDate(reqAry[4]);
            this.oldIndexTemplateCode = reqAry[5];
            this.subType = reqAry[6];
            this.oldStartDate = YssFun.toDate(reqAry[7]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new IndexTemplateBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析监控模板出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.indexTemplateCode).append("\t");
        buf.append(this.indexTemplateName).append("\t");
        buf.append(YssFun.formatDate(this.startDate)).append("\t");
        buf.append(this.Desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Comp_IndexTemplate"), "FIndexTempCode,FStartDate",
                               this.indexTemplateCode + "," + YssFun.formatDate(this.startDate),
                               this.oldIndexTemplateCode + "," + YssFun.formatDate(this.oldStartDate));

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
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            "(select FIndexTempCode,FCheckState,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " " +
            " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            "and FCheckState <> 3 group by FIndexTempCode,FCheckState) x join" +//edited by zhouxiang MS01356    监控范本删除数据后在回收站中无法显示被删除的数据    QDV4赢时胜(测试)2010年06月25日01_B 
            " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            ") y on x.FIndexTempCode = y.FIndexTempCode and x.FStartDate = y.FStartDate" +
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);

    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setIndexTemplateAttr(rs);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取监控模板信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.indexTemplateCode.length() != 0) {
                sResult = sResult + " and a.FIndexTempCode like '" +
                    filterType.indexTemplateCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.indexTemplateName.length() != 0) {
                sResult = sResult + " and a.FIndexTempName like '" +
                    filterType.indexTemplateName.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public void setIndexTemplateAttr(ResultSet rs) throws YssException {
        try {
            this.indexTemplateCode = rs.getString("FIndexTempCode") + "";
            this.indexTemplateName = rs.getString("FIndexTempName") + "";
            this.startDate = rs.getDate("FStartDate");
            this.Desc = rs.getString("FDesc") + "";
//       this.secTypes = getRandValue(this.indexTemplateCode,0);
//       this.accTypes = getRandValue(this.indexTemplateCode,1);
            super.setRecLog(rs);
        } catch (Exception e) {
            throw new YssException("设置信息出错！", e);
        }
    }

    public String getRandValue(String templateCode, int catInd) throws YssException {
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        String reStr = "";
        String strSql = "";

        strSql = "select * from " +
            pub.yssGetTableName("Tb_Comp_IndexRange") +
            " where FCatInd=" + catInd + " and FIndexTempCode=" + dbl.sqlString(templateCode) +
            " and FCheckState = 1";
        try {
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append(rs.getString("FCatCode")).append(",");
            }
            if (buf.toString().length() > 2) {
                reStr = buf.toString().substring(0,
                                                 buf.toString().length() - 1);
            }
        } catch (Exception e) {
            throw new YssException("获取信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return reStr;
    }

    /**
     * getListViewData2
     *
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
            sHeader = "指标模版代码\t指标模版名称\t启用日期";
            strSql = "select y.* from " +
                "(select FIndexTempCode,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " " +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                "and FCheckState = 1 group by FIndexTempCode) x join" +
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ") y on x.FIndexTempCode = y.FIndexTempCode and x.FStartDate = y.FStartDate" +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FIndexTempCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FIndexTempName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"))).append(
                    YssCons.YSS_LINESPLITMARK);
                this.setIndexTemplateAttr(rs);
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
            throw new YssException("获取监控模板信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        String strName = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            if (this.subType.equalsIgnoreCase("Category")) {
                strName = "品种类型";
                sHeader = "品种代码\t品种名称";
                strSql = "select y.* from " +
                    "(select FCatCode,FCheckState from  Tb_Base_Category " + " " +
                    " where FCheckState <> 2  and FCatCode in(select Fcatcode from " + pub.yssGetTableName("Tb_Comp_IndexRange") +
                    " where FCatInd=0 and FIndexTempCode=" + dbl.sqlString(this.oldIndexTemplateCode) +
                    " ) group by FCatCode,FCheckState " +
                    ") x join" +
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from Tb_Base_Category " + " a " +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " ) y on x.FCatCode = y.FCatCode" +
                    " order by  y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FCatCode") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FCatName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);
                    CategoryBean category = new CategoryBean();
                    category.setYssPub(pub);
                    category.setCategoryAttr(rs);
                    bufAll.append(category.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
            }
            if (this.subType.equalsIgnoreCase("Account")) {
                strName = "帐户类型";
                sHeader = "帐户类型代码\t帐户类型名称";
                strSql = "select y.* from " +
                    "(select FAccTypeCode,FCheckState from  Tb_Base_AccountType " + " " +
                    " where FCheckState <> 2  and FAccTypeCode in(select Fcatcode from " + pub.yssGetTableName("Tb_Comp_IndexRange") +
                    " where FCatInd=1 and FIndexTempCode=" + dbl.sqlString(this.oldIndexTemplateCode) +
                    ") group by FAccTypeCode,FCheckState " +
                    ") x join" +
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from Tb_Base_AccountType " + " a " +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " ) y on x.FAccTypeCode = y.FAccTypeCode" +
                    " order by  y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FAccTypeCode") + "").trim()).
                        append(
                            "\t");
                    bufShow.append( (rs.getString("FAccTypeName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);
                    AccountTypeBean account = new AccountTypeBean();
                    account.setYssPub(pub);
                    account.setAccountTypeAttr(rs);
                    bufAll.append(account.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
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
            throw new YssException("获取可用" + strName + "信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);

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
            strSql = "select * from " +
                pub.yssGetTableName("Tb_Comp_IndexTemplate") +
                " where FIndexTempCode = " +
                dbl.sqlString(this.indexTemplateCode) +
                " and FStartDate = (select max(FStartDate) from " +
                pub.yssGetTableName("Tb_Comp_IndexTemplate") +
                " where FIndexTempCode = " +
                dbl.sqlString(this.indexTemplateCode) +
                " and FStartDate <= " + dbl.sqlDate(startDate) + ")";

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
//            setIndexTemplateAttr(rs);
                this.indexTemplateCode = rs.getString("FIndexTempCode") + "";
                this.indexTemplateName = rs.getString("FIndexTempName") + "";
                this.startDate = rs.getDate("FStartDate");
                this.Desc = rs.getString("FDesc") + "";
                this.secTypes = getRandValue(this.indexTemplateCode, 0);
                this.accTypes = getRandValue(this.indexTemplateCode, 1);
            }
            return null;//modified by yeshenghong for CCB security check 20121018 
        } catch (Exception e) {
            throw new YssException("获取监控模版信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * saveSetting
     *
     * @param btOper byte
     */
    /*
        public void saveSetting(byte btOper) throws YssException{
      Connection conn = dbl.loadConnection();
      boolean bTrans = false;
      String strSql = "";
      try {
        //--------------------------------------------Add-------------------------------------------------
         if (btOper == YssCons.OP_ADD) {
            strSql =
                  "insert into " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + "(FIndexTempCode,FIndexTempName," +
                  " FStartDate,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                  " values(" + dbl.sqlString(this.indexTemplateCode) + "," +
                  dbl.sqlString(this.indexTemplateName) + "," +
                  dbl.sqlDate(this.startDate) + "," +
                  dbl.sqlString(this.Desc) + "," +
                  (pub.getSysCheckState()?"0":"1") + "," +
                  dbl.sqlString(this.creatorCode) + "," +
                  dbl.sqlString(this.creatorTime) + "," +
                  (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
         }
        //---------------------------------------------Edit------------------------------------------------
         else if (btOper == YssCons.OP_EDIT) {
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " set FIndexTempCode = " +
                  dbl.sqlString(this.indexTemplateCode) + ", FIndexTempName = "
                  + dbl.sqlString(this.indexTemplateName) + ",FStartDate = "
                  + dbl.sqlDate(this.startDate) + ", FDesc = " +
                  dbl.sqlString(this.Desc) + ", FCreator = " +
                  dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                  dbl.sqlString(this.creatorTime) +
                  " where FIndexTempCode = " + dbl.sqlString(this.oldIndexTemplateCode) +
                  " and FStartDate=" + dbl.sqlDate(this.oldStartDate);
         }
         //--------------------------------------Del-------------------------------------------
         else if (btOper == YssCons.OP_DEL) {
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " set FCheckState = " +
                  this.checkStateId + ", FCheckUser = " +
                  dbl.sqlString(pub.getUserCode()) +
                  ", FCheckTime = '" +
                  YssFun.formatDatetime(new java.util.Date()) + "'" +
                  " where FIndexTempCode = " +
                  dbl.sqlString(this.indexTemplateCode) + " and FStartDate=" +
                  dbl.sqlDate(this.startDate);
         }
         //---------------------------------------Audit---------------------------------------------
         else if (btOper == YssCons.OP_AUDIT) {
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " set FCheckState = " +
                  this.checkStateId + ", FCheckUser = " +
                  dbl.sqlString(pub.getUserCode()) +
                  ", FCheckTime = '" +
                  YssFun.formatDatetime(new java.util.Date()) + "'" +
                  " where FIndexTempCode = " +
                  dbl.sqlString(this.indexTemplateCode) + " and FStartDate=" +
                  dbl.sqlDate(this.startDate);
         }
         conn.setAutoCommit(false);
         bTrans = true;
         dbl.executeSql(strSql);
        //------------------------------子窗体的操作-----------------------------
        //-------------------------------Add/Edit------------------------------
         if (btOper == YssCons.OP_ADD) {
              if(this.category!=null && !(this.category.length()==0)) {
                 String[] tempCat =category.split("\f\f");
                 strSql = "delete from " +
                          pub.yssGetTableName("Tb_Comp_IndexRange") +
                          " where FCatInd=0 and FIndexTempCode=" +
                          dbl.sqlString(this.indexTemplateCode);
                    dbl.executeSql(strSql);
                 if(!(tempCat[0].split("\t")[2].length()==0))
                 {
                    for (int i = 0; i < tempCat.length; i++) {
                       strSql = "insert into " +
                             pub.yssGetTableName("Tb_Comp_IndexRange") +
                             "(FIndexTempCode,FRangeNum," +
                             " FCatCode,FCatInd,FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                             " values(" + dbl.sqlString(this.indexTemplateCode) +
                             "," +
                             dbl.sqlString("001") + "," +
                             dbl.sqlString(tempCat[i].split("\t")[0]) + "," +
                             0 + "," +
                             dbl.sqlDate(this.startDate) + "," +
                             (pub.getSysCheckState() ? "0" : "1") + "," +
                             dbl.sqlString(this.creatorCode) + "," +
                             dbl.sqlString(this.creatorTime) + "," +
                             (pub.getSysCheckState() ? "' '" :
                              dbl.sqlString(this.creatorCode)) + ")";
                       dbl.executeSql(strSql);
                    }
                 }
              }
             //----------------------------------------------------
              if(this.account!=null && !(this.account.length()==0))
              {
                 String[] tempAcc =account.split("\f\f");
                 strSql = "delete from " +
                        pub.yssGetTableName("Tb_Comp_IndexRange") +
                        " where FCatInd=1 and FIndexTempCode=" +
                        dbl.sqlString(this.indexTemplateCode);
                  dbl.executeSql(strSql);
                 if(!(tempAcc[0].split("\t")[2].length()==0))
                 {
                    for (int i = 0; i < tempAcc.length; i++) {
                       strSql = "insert into " +
                             pub.yssGetTableName("Tb_Comp_IndexRange") +
                             "(FIndexTempCode,FRangeNum," +
                             " FCatCode,FCatInd,FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                             " values(" + dbl.sqlString(this.indexTemplateCode) +
                             "," +
                             dbl.sqlString("001") + "," +
                             dbl.sqlString(tempAcc[i].split("\t")[0]) + "," +
                             1 + "," +
                             dbl.sqlDate(this.startDate) + "," +
                             (pub.getSysCheckState() ? "0" : "1") + "," +
                             dbl.sqlString(this.creatorCode) + "," +
                             dbl.sqlString(this.creatorTime) + "," +
                             (pub.getSysCheckState() ? "' '" :
                              dbl.sqlString(this.creatorCode)) + ")";
                       dbl.executeSql(strSql);
                    }
                 }
            }
      }
      else if (btOper == YssCons.OP_EDIT)
              {
                 if(this.category!=null && !(this.category.length()==0)) {
            String[] tempCat =category.split("\f\f");
            strSql = "delete from " +
                     pub.yssGetTableName("Tb_Comp_IndexRange") +
                     " where FCatInd=0 and FIndexTempCode=" +
                     dbl.sqlString(this.oldIndexTemplateCode);
               dbl.executeSql(strSql);
            if(!(tempCat[0].split("\t")[2].length()==0))
            {
               for (int i = 0; i < tempCat.length; i++) {
                  strSql = "insert into " +
                        pub.yssGetTableName("Tb_Comp_IndexRange") +
                        "(FIndexTempCode,FRangeNum," +
                        " FCatCode,FCatInd,FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                        " values(" + dbl.sqlString(this.indexTemplateCode) +
                        "," +
                        dbl.sqlString("001") + "," +
                        dbl.sqlString(tempCat[i].split("\t")[0]) + "," +
                        0 + "," +
                        dbl.sqlDate(this.startDate) + "," +
                        (pub.getSysCheckState() ? "0" : "1") + "," +
                        dbl.sqlString(this.creatorCode) + "," +
                        dbl.sqlString(this.creatorTime) + "," +
                        (pub.getSysCheckState() ? "' '" :
                         dbl.sqlString(this.creatorCode)) + ")";
                  dbl.executeSql(strSql);
               }
            }
         }
        //----------------------------------------------------
         if(this.account!=null && !(this.account.length()==0))
         {
            String[] tempAcc =account.split("\f\f");
            strSql = "delete from " +
                   pub.yssGetTableName("Tb_Comp_IndexRange") +
                   " where FCatInd=1 and FIndexTempCode=" +
                   dbl.sqlString(this.oldIndexTemplateCode);
             dbl.executeSql(strSql);
            if(!(tempAcc[0].split("\t")[2].length()==0))
            {
               for (int i = 0; i < tempAcc.length; i++) {
                  strSql = "insert into " +
                        pub.yssGetTableName("Tb_Comp_IndexRange") +
                        "(FIndexTempCode,FRangeNum," +
                        " FCatCode,FCatInd,FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                        " values(" + dbl.sqlString(this.indexTemplateCode) +
                        "," +
                        dbl.sqlString("001") + "," +
                        dbl.sqlString(tempAcc[i].split("\t")[0]) + "," +
                        1 + "," +
                        dbl.sqlDate(this.startDate) + "," +
                        (pub.getSysCheckState() ? "0" : "1") + "," +
                        dbl.sqlString(this.creatorCode) + "," +
                        dbl.sqlString(this.creatorTime) + "," +
                        (pub.getSysCheckState() ? "' '" :
                         dbl.sqlString(this.creatorCode)) + ")";
                  dbl.executeSql(strSql);
               }
            }
       }

       strSql="update " + pub.yssGetTableName("Tb_Comp_Index") + " set FIndexTempCode = " +
              dbl.sqlString(this.indexTemplateCode) + " where FIndexTempCode= " +
              dbl.sqlString(this.oldIndexTemplateCode);
              dbl.executeSql(strSql);

        strSql="update " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " set FIndexTempCode = " +
               dbl.sqlString(this.indexTemplateCode) + " where FIndexTempCode= " +
               dbl.sqlString(this.oldIndexTemplateCode);
               dbl.executeSql(strSql);
     }
//------------------------------Del-------------------------------
         else if (btOper == YssCons.OP_DEL) {
             strSql =
                   "update " + pub.yssGetTableName("Tb_Comp_Index") + " set FCheckState = " +
                   this.checkStateId + ", FCheckUser = " +
                   dbl.sqlString(pub.getUserCode()) +
                   ", FCheckTime = '" +
                   YssFun.formatDatetime(new java.util.Date()) + "'" +
                   " where FIndexTempCode = " + dbl.sqlString(this.indexTemplateCode);
                   dbl.executeSql(strSql);
             strSql=
                   "delete from " + pub.yssGetTableName("Tb_Comp_IndexRange") +
                   " where FIndexTempCode = " + dbl.sqlString(this.indexTemplateCode);
                   dbl.executeSql(strSql);
              strSql=
                  "delete from " + pub.yssGetTableName("Tb_Comp_IndexCondition") +
                  " where FIndexTempCode = " + dbl.sqlString(this.indexTemplateCode);
                  dbl.executeSql(strSql);
          }
        //-----------------------------------Audit------------------------------
        else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexRange") +
                       " set FCheckState = " +
                    this.checkStateId;
              if (this.checkStateId == 1) {
                 strSql += ", FCheckUser = '" +
                       pub.getUserCode() + "' , FCheckTime = '" +
                       YssFun.formatDatetime(new java.util.Date()) + "'";
              }
              strSql += " where FIndexTempCode = " +
                    dbl.sqlString(this.indexTemplateCode);
              dbl.executeSql(strSql);
              //---------------------------------
              strSql = "update " + pub.yssGetTableName("Tb_Comp_Index") +
                       " set FCheckState = " +
                       this.checkStateId;
                 if (this.checkStateId == 1) {
                       strSql += ", FCheckUser = '" +
                    pub.getUserCode() + "' , FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'";
              }
              strSql += " where FIndexTempCode = " +
              dbl.sqlString(this.indexTemplateCode);
              dbl.executeSql(strSql);
              //-------------------------------------
              strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") +
                   " set FCheckState = " +
                   this.checkStateId;
             if (this.checkStateId == 1) {
                   strSql += ", FCheckUser = '" +
                pub.getUserCode() + "' , FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'";
             }
             strSql += " where FIndexTempCode = " +
             dbl.sqlString(this.indexTemplateCode);
             dbl.executeSql(strSql);

       }
       conn.commit();
       bTrans = false;
       conn.setAutoCommit(true);
        }
      catch (Exception e) {
         throw new YssException("设置监控模板信息出错！", e);
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }
        }

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
             strSql =
                 strSql =
                 "insert into " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + "(FIndexTempCode,FIndexTempName," +
                 " FStartDate,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                 " values(" + dbl.sqlString(this.indexTemplateCode) + "," +
                 dbl.sqlString(this.indexTemplateName) + "," +
                 dbl.sqlDate(this.startDate) + "," +
                 dbl.sqlString(this.Desc) + "," +
                 (pub.getSysCheckState() ? "0" : "1") + "," +
                 dbl.sqlString(this.creatorCode) + "," +
                 dbl.sqlString(this.creatorTime) + "," +
                 (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ")";

             dbl.executeSql(strSql);
             if (this.category != null && ! (this.category.length() == 0)) {
                 String[] tempCat = category.split("\f\f");
                 strSql = "delete from " +
                     pub.yssGetTableName("Tb_Comp_IndexRange") +
                     " where FCatInd=0 and FIndexTempCode=" +
                     dbl.sqlString(this.indexTemplateCode);
                 dbl.executeSql(strSql);
                 //  if(!(tempCat[0].split("\t")[2].length()==0))
                 if (! (tempCat[0].split("\t")[0].length() == 0)) {
                     for (int i = 0; i < tempCat.length; i++) {
                         strSql = "insert into " +
                             pub.yssGetTableName("Tb_Comp_IndexRange") +
                             "(FIndexTempCode,FRangeNum," +
                             " FCatCode,FCatInd,FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                             " values(" + dbl.sqlString(this.indexTemplateCode) +
                             "," +
                             dbl.sqlString("001") + "," +
                             dbl.sqlString(tempCat[i].split("\t")[0]) + "," +
                             0 + "," +
                             dbl.sqlDate(this.startDate) + "," +
                             (pub.getSysCheckState() ? "0" : "1") + "," +
                             dbl.sqlString(this.creatorCode) + "," +
                             dbl.sqlString(this.creatorTime) + "," +
                             (pub.getSysCheckState() ? "' '" :
                              dbl.sqlString(this.creatorCode)) + ")";
                         dbl.executeSql(strSql);
                     }
                 }
             }
             //----------------------------------------------------
             if (this.account != null && ! (this.account.length() == 0)) {
                 String[] tempAcc = account.split("\f\f");
                 strSql = "delete from " +
                     pub.yssGetTableName("Tb_Comp_IndexRange") +
                     " where FCatInd=1 and FIndexTempCode=" +
                     dbl.sqlString(this.indexTemplateCode);
                 dbl.executeSql(strSql);
                 //  if (! (tempAcc[0].split("\t")[2].length() == 0)) {
                 // lzp  modify
                 //之前判断的数组的第三个直是否为空  它刚好是描叙  所以可能为空  不合理
                 if (! (tempAcc[0].split("\t")[0].length() == 0)) {
                     for (int i = 0; i < tempAcc.length; i++) {
                         strSql = "insert into " +
                             pub.yssGetTableName("Tb_Comp_IndexRange") +
                             "(FIndexTempCode,FRangeNum," +
                             " FCatCode,FCatInd,FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                             " values(" + dbl.sqlString(this.indexTemplateCode) +
                             "," +
                             dbl.sqlString("001") + "," +
                             dbl.sqlString(tempAcc[i].split("\t")[0]) + "," +
                             1 + "," +
                             dbl.sqlDate(this.startDate) + "," +
                             (pub.getSysCheckState() ? "0" : "1") + "," +
                             dbl.sqlString(this.creatorCode) + "," +
                             dbl.sqlString(this.creatorTime) + "," +
                             (pub.getSysCheckState() ? "' '" :
                              dbl.sqlString(this.creatorCode)) + ")";
                         dbl.executeSql(strSql);
                     }
                 }
             }

             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
         } catch (Exception e) {
             throw new YssException("增加监控指标模板出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }
         return null;

     }

    /**
     * checkSetting
     */

    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
          //modify by nimengjing 2010.12.10BUG #634 监控等级回收站中不能一次还原多条数据 
            String[] arrData=null;
            if(hRecycled!=null&&hRecycled.length()>0){
			 arrData = hRecycled.split("\r\n");
            }
            
            //---add by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
            if(arrData == null){
            	return;
            }
            //---add by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
            
			for (int i = 0; i < arrData.length; i++) {
				if (arrData[i].length() == 0) {
					continue;
				}
				this.parseRowStr(arrData[i]);
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FIndexTempCode = " +
                dbl.sqlString(this.indexTemplateCode) + " and FStartDate=" +
                dbl.sqlDate(this.startDate);

            dbl.executeSql(strSql);
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexRange") +
                " set FCheckState = " +
                this.checkStateId;
            if (this.checkStateId == 1) {
                strSql += ", FCheckUser = '" +
                    pub.getUserCode() + "' , FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'";
            }
            strSql += " where FIndexTempCode = " +
                dbl.sqlString(this.indexTemplateCode);
            dbl.executeSql(strSql);
            //---------------------------------
            strSql = "update " + pub.yssGetTableName("Tb_Comp_Index") +
                " set FCheckState = " +
                this.checkStateId;
            if (this.checkStateId == 1) {
                strSql += ", FCheckUser = '" +
                    pub.getUserCode() + "' , FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'";
            }
            strSql += " where FIndexTempCode = " +
                dbl.sqlString(this.indexTemplateCode);
            dbl.executeSql(strSql);
            //-------------------------------------
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") +
                " set FCheckState = " +
                this.checkStateId;
            if (this.checkStateId == 1) {
                strSql += ", FCheckUser = '" +
                    pub.getUserCode() + "' , FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'";
            }
            strSql += " where FIndexTempCode = " +
                dbl.sqlString(this.indexTemplateCode);
            dbl.executeSql(strSql);
		}	//--------------------------------end #634----------------------------------------------------
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核监控指标模板出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting
     */

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FIndexTempCode = " +
                dbl.sqlString(this.indexTemplateCode) + " and FStartDate=" +
                dbl.sqlDate(this.startDate);

            dbl.executeSql(strSql);
            strSql =
                "update " + pub.yssGetTableName("Tb_Comp_Index") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FIndexTempCode = " + dbl.sqlString(this.indexTemplateCode);
            dbl.executeSql(strSql);
            strSql =
                "delete from " + pub.yssGetTableName("Tb_Comp_IndexRange") +
                " where FIndexTempCode = " + dbl.sqlString(this.indexTemplateCode);
            dbl.executeSql(strSql);
            strSql =
                "delete from " + pub.yssGetTableName("Tb_Comp_IndexCondition") +
                " where FIndexTempCode = " + dbl.sqlString(this.indexTemplateCode);
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除监控指标模板出错", e);
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
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " set FIndexTempCode = " +
                dbl.sqlString(this.indexTemplateCode) + ", FIndexTempName = "
                + dbl.sqlString(this.indexTemplateName) + ",FStartDate = "
                + dbl.sqlDate(this.startDate) + ", FDesc = " +
                dbl.sqlString(this.Desc) + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                dbl.sqlString(this.creatorTime) +
                " where FIndexTempCode = " + dbl.sqlString(this.oldIndexTemplateCode) +
                " and FStartDate=" + dbl.sqlDate(this.oldStartDate);

            dbl.executeSql(strSql);
            if (this.category != null && ! (this.category.length() == 0)) {
                String[] tempCat = category.split("\f\f");
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Comp_IndexRange") +
                    " where FCatInd=0 and FIndexTempCode=" +
                    dbl.sqlString(this.oldIndexTemplateCode);
                dbl.executeSql(strSql);
                // if(!(tempCat[0].split("\t")[2].length()==0))  // lzp  modify
                //之前判断的数组的第三个直是否为空  它刚好是描叙  所以可能为空  不合理
                if (! (tempCat[0].split("\t")[0].length() == 0)) {
                    for (int i = 0; i < tempCat.length; i++) {
                        strSql = "insert into " +
                            pub.yssGetTableName("Tb_Comp_IndexRange") +
                            "(FIndexTempCode,FRangeNum," +
                            " FCatCode,FCatInd,FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                            " values(" + dbl.sqlString(this.indexTemplateCode) +
                            "," +
                            dbl.sqlString("001") + "," +
                            dbl.sqlString(tempCat[i].split("\t")[0]) + "," +
                            0 + "," +
                            dbl.sqlDate(this.startDate) + "," +
                            (pub.getSysCheckState() ? "0" : "1") + "," +
                            dbl.sqlString(this.creatorCode) + "," +
                            dbl.sqlString(this.creatorTime) + "," +
                            (pub.getSysCheckState() ? "' '" :
                             dbl.sqlString(this.creatorCode)) + ")";
                        dbl.executeSql(strSql);
                    }
                }
            }
            //----------------------------------------------------
            if (this.account != null && ! (this.account.length() == 0)) {
                String[] tempAcc = account.split("\f\f");
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Comp_IndexRange") +
                    " where FCatInd=1 and FIndexTempCode=" +
                    dbl.sqlString(this.oldIndexTemplateCode);
                dbl.executeSql(strSql);
                //    if(!(tempAcc[0].split("\t")[2].length()==0))
                if (! (tempAcc[0].split("\t")[0].length() == 0)) {
                    for (int i = 0; i < tempAcc.length; i++) {
                        strSql = "insert into " +
                            pub.yssGetTableName("Tb_Comp_IndexRange") +
                            "(FIndexTempCode,FRangeNum," +
                            " FCatCode,FCatInd,FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                            " values(" + dbl.sqlString(this.indexTemplateCode) +
                            "," +
                            dbl.sqlString("001") + "," +
                            dbl.sqlString(tempAcc[i].split("\t")[0]) + "," +
                            1 + "," +
                            dbl.sqlDate(this.startDate) + "," +
                            (pub.getSysCheckState() ? "0" : "1") + "," +
                            dbl.sqlString(this.creatorCode) + "," +
                            dbl.sqlString(this.creatorTime) + "," +
                            (pub.getSysCheckState() ? "' '" :
                             dbl.sqlString(this.creatorCode)) + ")";
                        dbl.executeSql(strSql);
                    }
                }
            }

            strSql = "update " + pub.yssGetTableName("Tb_Comp_Index") + " set FIndexTempCode = " +
                dbl.sqlString(this.indexTemplateCode) + " where FIndexTempCode= " +
                dbl.sqlString(this.oldIndexTemplateCode);
            dbl.executeSql(strSql);

            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " set FIndexTempCode = " +
                dbl.sqlString(this.indexTemplateCode) + " where FIndexTempCode= " +
                dbl.sqlString(this.oldIndexTemplateCode);
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改监控指标模板出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
        IndexTemplateBean befEditBean = new IndexTemplateBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FIndexTempCode,FCheckState,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " " +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                "and FCheckState <> 2 group by FIndexTempCode,FCheckState) x join" +
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_IndexTemplate") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  FIndexTempCode =" + dbl.sqlString(this.oldIndexTemplateCode) +
                " and FStartDate=" + dbl.sqlDate(this.oldStartDate) +
                ") y on x.FIndexTempCode = y.FIndexTempCode and x.FStartDate = y.FStartDate" +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.indexTemplateCode = rs.getString("FIndexTempCode") + "";
                befEditBean.indexTemplateName = rs.getString("FIndexTempName") + "";
                befEditBean.startDate = rs.getDate("FStartDate");
                befEditBean.Desc = rs.getString("FDesc") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * deleteRecycleData
     */
    /**
     * bug MS01263 QDV4赢时胜(测试)2010年6月2日4_B 2010-06-22 fanghaoln
     * 修改人：方浩
     * 回收站的删除功能调用此方法deleteRecycleData()
     * 从数据库删除数据，即彻底删除数据,可以多个一删除
     * @throws YssException
     * Time: 2010-06-22
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //判断回收站是否为空，如果不为空则根据解析的字符串执行SQL语句删除数据
            if (hRecycled != null && hRecycled.length() != 0) {
                //按照规定的解析规则对数据进行解析
                arrData = hRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Comp_IndexTemplate") +
                        " where FIndexTempCode = " +
                        dbl.sqlString(this.indexTemplateCode) + " and FStartDate=" +
                        dbl.sqlDate(this.startDate);
                    /*strChildSql = "delete from " + pub.yssGetTableName("Tb_Base_ChildHoliday") +
                     " where FHolidaysCode= " + dbl.sqlString(this.strHolidaysCode);*/
                    dbl.executeSql(strSql);
                    strSql ="delete from " +
                    pub.yssGetTableName("Tb_Comp_Index") +
                    " where FIndexTempCode = " + dbl.sqlString(this.indexTemplateCode);
                    dbl.executeSql(strSql);
                    //dbl.executeSql(strChildSql);
                    //从数据库中彻底删除节假日子表对应数据 edit by jc
                    //strSql = "delete from " + pub.yssGetTableName("Tb_Base_ChildHoliday") +
                    //      " where FHolidaysCode = " + dbl.sqlString(this.strHolidaysCode);
                    //dbl.executeSql(strSql);
                    //---------------------------------------jc
                }
                conn.commit(); //提交事物
                bTrans = false;
                conn.setAutoCommit(false);
            }
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
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
