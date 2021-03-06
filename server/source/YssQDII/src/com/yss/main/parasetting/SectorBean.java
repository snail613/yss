package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title: SectorBean</p>
 * <p>Description: 板块设置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SectorBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strSectorCode = ""; //板块代码
    private String strSectorName = ""; //板块名称
    private String strStartDate = ""; //起用日期
    private String strSectorType = ""; //板块类型
    private String strSectorDesc = ""; //描述
    private String strSectorChineseName = "";//板块中文名称 add by yanghaiming 20091123 MS00807 QDV4赢时胜（北京）2009年11月12日01_A

    private String strOldSectorCode = "";
    private String strOldStartDate = "";
    private String sRecycled = "";
    private SectorBean filterType;

    public SectorBean() {
    }

    /**
     * 返回属性
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.strSectorCode.trim()).append("\t");
        buffer.append(this.strSectorType.trim()).append("\t");
        buffer.append(this.strSectorName.trim()).append("\t");
        buffer.append(this.strStartDate.trim()).append("\t");
        buffer.append(this.strSectorDesc.trim()).append("\t");
        buffer.append(this.strSectorChineseName.trim()).append("\t");//添加板块中文名称 add by yanghaiming 20091123 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
        buffer.append(super.buildRecLog());
        return buffer.toString();
    }

    /**
     * 数据验证
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
    	//------ 不再使用启用日期 modify by wangzuochun  2010.09.06  MS01602    板块分类设置，新建板块分类代码相同、启用日期不同的数据报错    QDV4赢时胜(测试)2010年08月12日03_B    
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Sector"), "FSectorCode",
                               this.strSectorCode,
                               this.strOldSectorCode);
        //------------------------------------------MS01602------------------------------------------//
    }

    public String getAllSetting() {
        return "";
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String strResult = "";
        if (this.filterType != null) {
            strResult = " where 1=1 ";
            if (this.filterType.strSectorCode.length() != 0) {
                strResult = strResult + " and a.FSectorCode like '" +
                    filterType.strSectorCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strSectorName.length() != 0) {
                strResult = strResult + " and a.FSectorName like '" +
                    filterType.strSectorName.replaceAll("'", "''") + "%'";
            }
            /*        if(this.filterType.strSectorType.length()!=0)
                    {
                       strResult=strResult + " and FSectorType like '" +
                             filterType.strSectorType.replaceAll("'", "''") + "%'";
                   }
             */
            if (this.filterType.strStartDate.length() != 0 &&
                !this.filterType.strStartDate.equals("9998-12-31") 
                //edit by songjie 2011.03.14 启用日期默认值改为1900-01-01
                && !this.filterType.strStartDate.equals("1900-01-01")) {
                strResult = strResult + " and a.FStartDate <= " +
                    dbl.sqlDate(filterType.strStartDate);
            }
        }
        return strResult;
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
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSectorAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_RND_SYMBOL + "," +
                                        YssCons.YSS_RND_RANGE + "," +
                                        YssCons.YSS_RND_WAY + "," +
                                        YssCons.YSS_STR_TYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取板块信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData1
     * 获取板块信息数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//            "(select FSectorCode,FCheckState,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_Sector") + " " +
//            //edit by licai 20101206 BUG #533 板块设置隐藏启用日期后，存在问题
//            " where FStartDate <= " +
////            dbl.sqlDate(new java.util.Date()) +
//            " to_date('9998-12-31','yyyy-mm-dd')"+
//          //edit by licai 20101206 BUG #533===============================end
//            " group by FSectorCode,FCheckState) x join" +
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
        	/**shashijie 2012-3-26 BUG 3963*/
            " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName ,n.FVocName as FSectorTypeValue " +
            " from " + pub.yssGetTableName("Tb_Para_Sector") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FSectorType") + " = n.FVocCode and n.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_STR_TYPE) +
			/**end*/
            buildFilterSql() +
            ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     * 获取板块信息全部数据
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        /**shashijie 2012-3-26 BUG 3963*/
        strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName,n.FVocName as FSectorTypeValue from " + pub.yssGetTableName("Tb_Para_Sector") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FSectorType") + " = n.FVocCode and n.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_STR_TYPE) +
            buildFilterSql() +
            " order by  a.FCheckState, a.FCreateTime desc";
        /**end*/
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     * 获取板块信息数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "板块代码\t板块名称\t启用日期";
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                "(select FSectorCode,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_Sector") + " " +
//              //edit by licai 20101215 BUG #533 板块设置隐藏启用日期后，存在问题
//                " where FStartDate <= " +
////                dbl.sqlDate(new java.util.Date()) +
//                " to_date('9998-12-31','yyyy-mm-dd')"+
//              //edit by licai 20101215 BUG #533===============================end
//                "and FCheckState = 1 group by FSectorCode) x join" +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                //----edit by songjie 2011.03.14 不以最大的启用日期查询数据----//
                " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + 
                " (select * from " + pub.yssGetTableName("Tb_Para_Sector") + " where FCheckState = 1) a " +
                //----edit by songjie 2011.03.14 不以最大的启用日期查询数据----//
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSectorCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FSectorName") + "").trim()).append(
                    "\t");
                bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate")) + "").
                               trim()).append(YssCons.YSS_LINESPLITMARK);
                setSectorAttr(rs);
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
            throw new YssException("获取可用板块信息出错", e);
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
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.strSectorCode = reqAry[0];
            this.strSectorType = reqAry[1];
            this.strSectorName = reqAry[2];
            this.strStartDate = reqAry[3];
            this.strSectorDesc = reqAry[4];
            this.strSectorChineseName = reqAry[5];//添加板块中文名称 add by yanghaiming 20091123 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
            this.checkStateId = Integer.parseInt(reqAry[6]);
            this.strOldSectorCode = reqAry[7];
            this.strOldStartDate = reqAry[8];
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SectorBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析板块设置请求信息出错", e);
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
     * @param btOper byte
     * @throws YssException
     */
    /* public void saveSetting(byte btOper) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
           if (btOper == YssCons.OP_ADD) {
              strSql =
                    "insert into " + pub.yssGetTableName("Tb_Para_Sector") + "(FSectorCode, FSectorName,FSectorType, FDesc, " +
                    " FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                    " values(" + dbl.sqlString(this.strSectorCode) + "," +
                    dbl.sqlString(this.strSectorName) + ",'" +
                    "ysstech"+"',"+
                    dbl.sqlString(this.strSectorDesc) + "," +
                    dbl.sqlDate(this.strStartDate) + "," +
                    (pub.getSysCheckState()?"0":"1") + "," +
                    dbl.sqlString(this.creatorCode) + ", " +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";

           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Sector") + " set FSectorCode = " +
                    dbl.sqlString(this.strSectorCode) + ", FSectorName = "
                    + dbl.sqlString(this.strSectorName) + ",FDesc = " +
                    dbl.sqlString(this.strSectorDesc) + ",FCheckState = " +
                    (pub.getSysCheckState()?"0":"1") + ",FStartDate = "
                    + dbl.sqlDate(this.strStartDate) + ",FCreator = " +
                    dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                    (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                    " where FSectorCode = " + dbl.sqlString(this.strOldSectorCode) +
                    " and FStartDate=" + dbl.sqlDate(this.strOldStartDate);
           }
           else if (btOper == YssCons.OP_DEL) {
//            strSql = "delete from " + pub.yssGetTableName("Tb_Para_Sector") + " where FSectorCode = " +
//                  dbl.sqlString(this.strSectorCode) +" and FStartDate=" + dbl.sqlDate(this.strStartDate);
              strSql = "update " + pub.yssGetTableName("Tb_Para_Sector") + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSectorCode = " +
                    dbl.sqlString(this.strSectorCode) + " and FStartDate=" +
                    dbl.sqlDate(this.strStartDate);

           }
           else if (btOper == YssCons.OP_AUDIT) {
//            if (this.checkStateId == 2) {//删除审核
//               strSql = "delete from " + pub.yssGetTableName("Tb_Para_Sector") + " where FSectorCode = " +
//                     dbl.sqlString(this.strSectorCode) + " and FStartDate=" +
//                     dbl.sqlDate(this.strStartDate);
//            }
              strSql = "update " + pub.yssGetTableName("Tb_Para_Sector") + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSectorCode = " +
                    dbl.sqlString(this.strSectorCode) + " and FStartDate=" +
                    dbl.sqlDate(this.strStartDate);
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("设置板块信息出错！", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
        }
     }*/






    /**
     * saveSetting
     * @param btOper byte
     * @throws YssException
     */
    /* public void saveSetting(byte btOper) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
           if (btOper == YssCons.OP_ADD) {
              strSql =
                    "insert into " + pub.yssGetTableName("Tb_Para_Sector") + "(FSectorCode, FSectorName,FSectorType, FDesc, " +
     " FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                    " values(" + dbl.sqlString(this.strSectorCode) + "," +
                    dbl.sqlString(this.strSectorName) + ",'" +
                    "ysstech"+"',"+
                    dbl.sqlString(this.strSectorDesc) + "," +
                    dbl.sqlDate(this.strStartDate) + "," +
                    (pub.getSysCheckState()?"0":"1") + "," +
                    dbl.sqlString(this.creatorCode) + ", " +
                    dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";

           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Sector") + " set FSectorCode = " +
                    dbl.sqlString(this.strSectorCode) + ", FSectorName = "
                    + dbl.sqlString(this.strSectorName) + ",FDesc = " +
                    dbl.sqlString(this.strSectorDesc) + ",FCheckState = " +
                    (pub.getSysCheckState()?"0":"1") + ",FStartDate = "
                    + dbl.sqlDate(this.strStartDate) + ",FCreator = " +
                    dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
     " where FSectorCode = " + dbl.sqlString(this.strOldSectorCode) +
                    " and FStartDate=" + dbl.sqlDate(this.strOldStartDate);
           }
           else if (btOper == YssCons.OP_DEL) {
//            strSql = "delete from " + pub.yssGetTableName("Tb_Para_Sector") + " where FSectorCode = " +
//                  dbl.sqlString(this.strSectorCode) +" and FStartDate=" + dbl.sqlDate(this.strStartDate);
              strSql = "update " + pub.yssGetTableName("Tb_Para_Sector") + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSectorCode = " +
                    dbl.sqlString(this.strSectorCode) + " and FStartDate=" +
                    dbl.sqlDate(this.strStartDate);

           }
           else if (btOper == YssCons.OP_AUDIT) {
//            if (this.checkStateId == 2) {//删除审核
//               strSql = "delete from " + pub.yssGetTableName("Tb_Para_Sector") + " where FSectorCode = " +
//                     dbl.sqlString(this.strSectorCode) + " and FStartDate=" +
//                     dbl.sqlDate(this.strStartDate);
//            }
              strSql = "update " + pub.yssGetTableName("Tb_Para_Sector") + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSectorCode = " +
                    dbl.sqlString(this.strSectorCode) + " and FStartDate=" +
                    dbl.sqlDate(this.strStartDate);
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("设置板块信息出错！", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
        }
     }*/

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
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_Sector") +
                "(FSectorCode, FSectorName,FSectorType, FDesc, " +
                " FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser,FSECTORCHINESENAME) " +
                " values(" + dbl.sqlString(this.strSectorCode) + "," +
                dbl.sqlString(this.strSectorName) + "," +
                dbl.sqlString(this.strSectorType) + "," +
                dbl.sqlString(this.strSectorDesc) + "," +
                dbl.sqlDate(this.strStartDate) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + ", " +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + "," +
                dbl.sqlString(this.strSectorChineseName) + //添加板块中文名称 add by yanghaiming 20091123 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加板块信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_Sector") +
                " set FSectorCode = " +
                dbl.sqlString(this.strSectorCode) + ", FSectorName = "
                + dbl.sqlString(this.strSectorName) +
                " ,FSectorType = " + dbl.sqlString(this.strSectorType) +
                ",FDesc = " +
                dbl.sqlString(this.strSectorDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FStartDate = " + 
                dbl.sqlDate(this.strStartDate) + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ", FSECTORCHINESENAME = " +
                dbl.sqlString(this.strSectorChineseName) +  //添加板块中文名称 add by yanghaiming 20091123 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
                " where FSectorCode = " + dbl.sqlString(this.strOldSectorCode) +
                " and FStartDate=" + dbl.sqlDate(this.strOldStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改板块信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_Sector") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FSectorCode = " +
                dbl.sqlString(this.strSectorCode) + " and FStartDate=" +
                dbl.sqlDate(this.strStartDate);
            dbl.executeSql(strSql);
            strSql = "update " + pub.yssGetTableName("Tb_Para_SectorClass") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FSectorCode = " +
                dbl.sqlString(this.strSectorCode);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除板块信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理板块设置的审核和未审核的单条信息。
     *  新方法功能：可以处理板块设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      try {
//         strSql = "update " + pub.yssGetTableName("Tb_Para_Sector") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = " +
//               dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" +
//               YssFun.formatDatetime(new java.util.Date()) + "'" +
//               " where FSectorCode = " +
//               dbl.sqlString(this.strSectorCode) + " and FStartDate=" +
//               dbl.sqlDate(this.strStartDate);
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
        //修改后的代码
        //--------------------------------
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

                strSql = "update " + pub.yssGetTableName("Tb_Para_Sector") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSectorCode = " +
                    dbl.sqlString(this.strSectorCode) + " and FStartDate=" +
                    dbl.sqlDate(this.strStartDate);

                dbl.executeSql(strSql);

                strSql = "update " + pub.yssGetTableName("Tb_Para_SectorClass") + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSectorCode = " +
                    dbl.sqlString(this.strSectorCode);
                dbl.executeSql(strSql);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        //--------------------------------

        catch (Exception e) {
            System.out.print(e.getMessage());
            throw new YssException("审核板块信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void setSectorAttr(ResultSet rs) throws SQLException {
        this.strSectorCode = rs.getString("FSectorCode") + "";
        this.strSectorName = rs.getString("FSectorName") + "";
        this.strSectorType = rs.getString("FSectorType") + "";
        this.strStartDate = YssFun.formatDate(rs.getDate("FStartDate"));
        this.strSectorDesc = rs.getString("FDesc") + "";
        this.strSectorChineseName = rs.getString("FSECTORCHINESENAME") + "";//添加板块中文名称 add by yanghaiming 20091123 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
        super.setRecLog(rs);
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
        SectorBean befEditBean = new SectorBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                "(select FSectorCode,FCheckState,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Sector") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState <> 2 group by FSectorCode,FCheckState) x join" +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                " (select * from " + pub.yssGetTableName("Tb_Para_Sector") + " where FCheckState <> 2) a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  a.FSectorCode =" + dbl.sqlString(this.strOldSectorCode) +
                ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strSectorCode = rs.getString("FSectorCode") + "";
                befEditBean.strSectorName = rs.getString("FSectorName") + "";
                befEditBean.strStartDate = YssFun.formatDate(rs.getDate(
                    "FStartDate"));
                befEditBean.strSectorDesc = rs.getString("FDesc") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
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
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_Sector") +
                    " where FSectorCode = " +
                    dbl.sqlString(this.strSectorCode) + " and FStartDate=" +
                    dbl.sqlDate(this.strStartDate);

                dbl.executeSql(strSql);
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_SectorClass") +
                    " where FSectorCode = " +
                    dbl.sqlString(this.strSectorCode);
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
