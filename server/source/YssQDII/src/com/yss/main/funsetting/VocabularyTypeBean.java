package com.yss.main.funsetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title:操作词汇类型的Bean </p>
 *
 * <p>Description: 本类为词汇类型对应的实体Bean，负责数据的解析、封装以及数据库操作 </p>
 *
 * <p>
 *     完善回收站的数据显示和删除功能
 *     modify by xuqiji MS00514:QDV4赢时胜（上海）2009年6月10日06_B
 * </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class VocabularyTypeBean
    extends BaseDataSettingBean implements IDataSetting {

    private String strVocCode = "";
    private String strVocName = "";
    private String strVocDesc = "";
    private String vocs = "";
    private String status = ""; //是否记入系统信息状态  lzp 11.29 add
    private VocabularyTypeBean filterType = null;
    private String sRecycled = ""; //回收站
    byte btOper;

    private String strOldVocCode = "";
    public void setVocabulary(String vocabularys) {
        this.vocs = vocabularys;
    }

    public void setStrVocName(String strVocName) {
        this.strVocName = strVocName;
    }

    public void setStrVocDesc(String strVocDesc) {
        this.strVocDesc = strVocDesc;
    }

    public void setFilterType(VocabularyTypeBean filterType) {
        this.filterType = filterType;
    }

    public void setStrVocCode(String strVocCode) {
        this.strVocCode = strVocCode;
    }

    public void setOldVocCode(String oldVocCode) {
        this.strOldVocCode = oldVocCode;
    }

    public VocabularyTypeBean getFilterType() {
        return filterType;
    }

    public String getOldVocCode() {
        return strOldVocCode;
    }

    public String getStrVocName() {
        return strVocName;
    }

    public String getStrVocDesc() {
        return strVocDesc;
    }

    public String getStrVocCode() {
        return strVocCode;
    }

    public String getVocabulary() {
        return vocs;
    }

    public VocabularyTypeBean() {
    }

    /**
     * addSetting
     *
     * @return String
     */

    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        //----------------------------------------------
        VocabularyBean voc = new VocabularyBean();
        voc.setYssPub(pub);
        //----------------------------------------------
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "insert into " + "Tb_Fun_VocabularyType" +
                "(FVocTypeCode,FVocTypeName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.strVocCode) + "," +
                dbl.sqlString(this.strVocName) + "," +
                dbl.sqlString(this.strVocDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";
            dbl.executeSql(strSql); //先插词汇类型
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-词汇类型");
                sysdata.setStrCode(this.strVocCode);
                sysdata.setStrName(this.strVocName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            //-----------------------------------------
            if (this.vocs != null) { //如果词汇串不为空，就循环插入词汇到词汇表
                //lzp ADD 11.30

                voc.saveMutliSetting(this.vocs, this.status);
            }
            //-----------------------------------------
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------
            VocabularyBean filterType = new VocabularyBean();
            filterType.setVocTypeCode(this.strVocCode);
            voc.setFilterType(filterType);
            filterType = voc.getFilterType();
            this.setASubData(voc.getListViewData1());
            //--------------------------------------------------
        } catch (Exception e) {
            throw new YssException("更新词汇类型信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        //    subOper();
        return null;
    }

    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        //---------------------------------------
        VocabularyBean voc = new VocabularyBean();
        voc.setYssPub(pub);
        //---------------------------------------
        try {
            //--------------------------------------------------
            VocabularyBean bfilterType = new VocabularyBean();
            bfilterType.setVocTypeCode(this.strVocCode);
            voc.setFilterType(bfilterType);
            bfilterType = voc.getFilterType();
            this.setBSubData(voc.getListViewData1());
            //--------------------------------------------------
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + "Tb_Fun_VocabularyType" + " set FVocTypeCode = " +
                dbl.sqlString(this.strVocCode) + ", FVocTypeName= "
                + dbl.sqlString(this.strVocName) + ", FDesc= " +
                dbl.sqlString(this.strVocDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FVocTypeCode=" + dbl.sqlString(this.strOldVocCode);
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-词汇类型");
                sysdata.setStrCode(this.strVocCode);
                sysdata.setStrName(this.strVocName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            if (!this.strVocCode.equals(this.strOldVocCode)) {
                strSql = "update " + "Tb_Fun_Vocabulary" +
                    " set FVocTypeCode = " +
                    dbl.sqlString(this.strVocCode) +
                    " where FVocTypeCode = " +
                    dbl.sqlString(this.strOldVocCode);
                dbl.executeSql(strSql);

                //---------lzp add 11.29
                if (this.status.equalsIgnoreCase("1")) {
                    com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                    sysdata.setYssPub(pub);
                    sysdata.setStrAssetGroupCode("Common");
                    sysdata.setStrFunName("修改-常用词汇");
                    sysdata.setStrCode(this.strVocCode);
                    sysdata.setStrName(this.strVocName);
                    sysdata.setStrUpdateSql(strSql);
                    sysdata.setStrCreator(pub.getUserName());
                    sysdata.addSetting();
                }
                //-----------------------

            }
            if (this.vocs != null) {
                //lzp ADD 11.30
                voc.saveMutliSetting(this.vocs, this.status);

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //--------------------------------------------------
            VocabularyBean filterType = new VocabularyBean();
            filterType.setVocTypeCode(this.strVocCode);
            voc.setFilterType(filterType);
            filterType = voc.getFilterType();
            this.setASubData(voc.getListViewData1());
            //--------------------------------------------------

        } catch (Exception e) {
            throw new YssException("更新词汇类型信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //   subOper();
        return null;
    }

    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        //---------------------------------------
        VocabularyBean voc = new VocabularyBean();
        voc.setYssPub(pub);
//---------------------------------------

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + "Tb_Fun_VocabularyType" + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FVocTypeCode = " +
                dbl.sqlString(this.strVocCode);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-词汇类型");
                sysdata.setStrCode(this.strVocCode);
                sysdata.setStrName(this.strVocName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            dbl.executeSql(strSql);
            strSql = "update " + "Tb_Fun_Vocabulary" +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FVocTypeCode = " +
                dbl.sqlString(this.strVocCode);
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-常用词汇");
                sysdata.setStrCode(this.strVocCode);
                sysdata.setStrName(this.strVocName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //--------------------------------------------------
            VocabularyBean filterType = new VocabularyBean();
            filterType.setVocTypeCode(this.strVocCode);
            voc.setFilterType(filterType);
            filterType = voc.getFilterType();
            this.setASubData(voc.getListViewData1());
            //--------------------------------------------------

        } catch (Exception e) {
            throw new YssException("更新词汇类型信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        //   subOper();
    }
    
    //update by guolongchao 20111209 bug3191  回收站还原功能：将原来只能单条处理数据改为批量处理数据
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            String[] arrData =sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) 
            {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);                
           
	            strSql = "update " + "Tb_Fun_VocabularyType" + " set FCheckState = " +
	                this.checkStateId + ", FCheckUser = " +
	                dbl.sqlString(pub.getUserCode()) +
	                ", FCheckTime = '" +
	                YssFun.formatDatetime(new java.util.Date()) + "'" +
	                " where FVocTypeCode = " +
	                dbl.sqlString(this.strVocCode);
	            dbl.executeSql(strSql);
	            //---------lzp add 11.29
	            if (this.status.equalsIgnoreCase("1")) {
	                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
	                    funsetting.SysDataBean();
	                sysdata.setYssPub(pub);
	                sysdata.setStrAssetGroupCode("Common");
	                if (this.checkStateId == 1) {
	                    sysdata.setStrFunName("审核-词汇类型");
	                } else {
	                    sysdata.setStrFunName("反审核-词汇类型");
	                }
	                sysdata.setStrCode(this.strVocCode);
	                sysdata.setStrName(this.strVocName);
	                sysdata.setStrUpdateSql(strSql);
	                sysdata.setStrCreator(pub.getUserName());
	                sysdata.addSetting();
	            }
	            //-----------------------
	
	            //--------------------------------------------------------------------
	            strSql = "update " + "Tb_Fun_Vocabulary" +
	                " set FCheckState = " + this.checkStateId +
	                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	                ", FCheckTime = '" +
	                YssFun.formatDatetime(new java.util.Date()) +
	                "' where FVocTypeCode = " +
	                dbl.sqlString(this.strVocCode);
	            dbl.executeSql(strSql);
	            //---------lzp add 11.29
	            if (this.status.equalsIgnoreCase("1")) {
	                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
	                    funsetting.SysDataBean();
	                sysdata.setYssPub(pub);
	                sysdata.setStrAssetGroupCode("Common");
	                if (this.checkStateId == 1) {
	                    sysdata.setStrFunName("审核-常用词汇");
	                } else {
	                    sysdata.setStrFunName("反审核-常用词汇");
	                }
	
	                sysdata.setStrCode(this.strVocCode);
	                sysdata.setStrName(this.strVocName);
	                sysdata.setStrUpdateSql(strSql);
	                sysdata.setStrCreator(pub.getUserName());
	                sysdata.addSetting();
	            }            
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新词汇类型信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //   subOper();

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */




    /**
     * checkSetting
     */

    public void checkInput(byte btOper) throws YssException {
        this.btOper = btOper;
        dbFun.checkInputCommon(btOper, "Tb_Fun_VocabularyType", "FVocTypeCode",
                               this.strVocCode, this.strOldVocCode);

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
            "(select FVocTypeCode from " + "Tb_Fun_VocabularyType" + " " +
            //删除where FCheckState <> 2，否则回收站数据加载不出来，by xuqiji 20090702:QDV4赢时胜（上海）2009年6月10日06_B MS00514
            " group by FVocTypeCode) x join" +
            " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            "Tb_Fun_VocabularyType" + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            ") y on x.FVocTypeCode = y.FVocTypeCode " +
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strVocCode.length() != 0) {
                sResult = sResult + " and a.FVocTypeCode like '" +
                    filterType.strVocCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strVocName.length() != 0) {
                sResult = sResult + " and a.FVocTypeName like '" +
                    filterType.strVocName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strVocDesc.length() != 0) {
                sResult = sResult + " and a.FDesc = " +
                    dbl.sqlString(filterType.strVocDesc);
            }
        }
        return sResult;
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
                if(bufShow !=null)//by guyichuan bug 2225
                bufShow=new StringBuffer(bufShow.toString().replaceAll("\b", ""));

                setVocabularyType(rs);

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
            throw new YssException("获取词汇类型信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
            sHeader = "词汇代码\t词汇名称\t描述";
            strSql = "select y.* from " +
                "(select FVocTypeCode from " + "Tb_Fun_VocabularyType" + " " +
                " where FCheckState = 1 group by FVocTypeCode) x join" +
                " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                "Tb_Fun_VocabularyType" + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ") y on x.FVocTypeCode = y.FVocTypeCode" +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FVocTypeCode") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FVocTypeName") + "").trim());
                bufShow.append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                setVocabularyType(rs);
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
            throw new YssException("获取可用词汇类型信息出错！", e);
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
    public String getListViewData4() throws YssException {
        String strSql = "";

        strSql =
            "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
            "Tb_Fun_VocabularyType" + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);

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
     *
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
                sTmpStr = sRowStr.split("\r\t")[0]; //取第一串为词汇类型串
                if (sRowStr.split("\r\t").length == 3) {
                    this.vocs = sRowStr.split("\r\t")[2]; //取第三串为词汇
                }
            } else {
                sTmpStr = sRowStr; //默认为词汇类型串
            }
            //============将数据放到回收站便来那个中，备用=====================
            sRecycled = sRowStr;
            //==========xuqiji MS00514 QDV4赢时胜（上海）2009年6月10日06_B====
            reqAry = sTmpStr.split("\t");
            this.strVocCode = reqAry[0]; //词汇类型码
            this.strVocName = reqAry[1]; //词汇类型名
            this.strVocDesc = reqAry[2]; //描述
            this.checkStateId = Integer.parseInt(reqAry[3]); //上级日志标志0:1
            this.strOldVocCode = reqAry[4]; //原词汇类型码
            this.status = reqAry[5]; //lzp add 11.29//本级日志标志0:1
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new VocabularyTypeBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]); //取第二串为过滤串
                }
            }

        } catch (Exception e) {
            throw new YssException("解析词汇类型请求信息出错", e);
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
     * builderRowStr
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strVocCode).append("\t");
        buf.append(this.strVocName).append("\t");
        buf.append(this.strVocDesc.replaceAll("\b", "\r\n")).append("\t");//by guyichuan bug 2225 2011.08.12
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    public void setVocabularyType(ResultSet rs) {
        try {
            this.strVocCode = rs.getString("FVocTypeCode") + "";
            this.strVocName = rs.getString("FVocTypeName") + "";
            if(rs.getString("FDesc")!=null)
            this.strVocDesc = rs.getString("FDesc").replaceAll("\b", "\r\n") + "";//by guyichuan bug 2225 2011.08.12
            super.setRecLog(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//--------------------------------------------------------------
    /*
       public void subOper() throws YssException {
          {
             Connection conn = dbl.loadConnection();
             boolean bTrans = false;
             String strSql = "";
             try {
                if (btOper == YssCons.OP_EDIT &&
                    (this.strVocCode != this.strOldVocCode)) {
                   strSql = "update " + "Tb_Fun_Vocabulary" +
                         " set FVocTypeCode = " +
                         dbl.sqlString(this.strVocCode) +
                         " where FVocTypeCode = " +
                         dbl.sqlString(this.strOldVocCode);
                   dbl.executeSql(strSql);

                }
                if (this.vocs != null) {
                   if (btOper == YssCons.OP_ADD || btOper == YssCons.OP_EDIT) {
                      VocabularyBean voc = new VocabularyBean();
                      voc.setYssPub(pub);
                      voc.saveMutliSetting(this.vocs);
                   }
                }

                if (btOper == YssCons.OP_DEL) {
                   strSql = "update " + "Tb_Fun_Vocabulary" +
                         " set FCheckState = " + this.checkStateId +
                         ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                         ", FCheckTime = '" +
                         YssFun.formatDatetime(new java.util.Date()) +
                         "' where FVocTypeCode = " +
                         dbl.sqlString(this.strVocCode);
                   dbl.executeSql(strSql);
                }

                if (btOper == YssCons.OP_AUDIT) {
                   strSql = "update " + "Tb_Fun_Vocabulary" +
                         " set FCheckState = " + this.checkStateId +
                         ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                         ", FCheckTime = '" +
                         YssFun.formatDatetime(new java.util.Date()) +
                         "' where FVocTypeCode = " +
                         dbl.sqlString(this.strVocCode);
                   dbl.executeSql(strSql);
                }
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);

             }
             catch (Exception e) {
                throw new YssException("更新词汇类型信息出错！", e);
             }
             finally {
                dbl.endTransFinal(conn, bTrans);
             }

          }
       }

     */
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
        VocabularyTypeBean befEditBean = new VocabularyTypeBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select y.* from " +
                "(select FVocTypeCode from " + "Tb_Fun_VocabularyType" + " " +
                " where FCheckState <> 2 group by FVocTypeCode) x join" +
                " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                "Tb_Fun_VocabularyType" + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where FVocTypeCode=" + dbl.sqlString(this.strOldVocCode) +
                ") y on x.FVocTypeCode = y.FVocTypeCode " +
                " order by y.FCheckState, y.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strVocCode = rs.getString("FVocTypeCode") + "";
                befEditBean.strVocName = rs.getString("FVocTypeName") + "";
                if(rs.getString("FDesc")!=null)
                //by guyichuan bug 2225 2011.08.12
                befEditBean.strVocDesc = rs.getString("FDesc").replaceAll("\b", "\r\n") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * deleteRecycleData 回收站清除功能
     * author : xuqiji
     * BugNO  ：QDV4赢时胜（上海）2009年6月10日06_B MS00514
     * Modify Date : 20090702
     * Modify Desc : 实现删除回收站数据功能
     */
    public void deleteRecycleData() throws YssException {
        String strSql = null;
        String[] array = null;
        boolean bTrans = true; //代表是否开始事务
        Statement st = null;
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (null != sRecycled && !"".equalsIgnoreCase(sRecycled.trim())) {
                //根据规定的符号进行数据分割，并存放到数组中
                array = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                st = conn.createStatement();
                //循环执行这些删除数据
                for (int i = 0; i < array.length; i++) {
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    //删除词汇类型的同时，删除掉词汇数据
                    strSql = "delete from " + pub.yssGetTableName("Tb_Fun_VocabularyType") +
                        " where FVocTypeCode = " + dbl.sqlString(this.strVocCode);
                    st.addBatch(strSql);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Fun_Vocabulary") +
                        " where FVocTypeCode = " + dbl.sqlString(this.strVocCode);
                    st.addBatch(strSql);
                }
                st.executeBatch(); //执行删除操作
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除词汇类型数据出错！", e);
        } finally {
            dbl.closeStatementFinal(st);
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
