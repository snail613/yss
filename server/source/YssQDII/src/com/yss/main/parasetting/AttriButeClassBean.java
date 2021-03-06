package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: AttriButeClassBean</p>
 * <p>Description:属性分类设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class AttriButeClassBean
    extends BaseDataSettingBean implements IDataSetting {

    private String attrCode = ""; //属性分类代码
    private String attrName = ""; //属性分类名称
    private String attrDesc = ""; //属性分类描述
    private String status = ""; //是否记入系统信息状态  lzp 12.3 add
    private AttriButeClassBean filterType;
    private String oldAttrCode = "";
    private String sRecycled = "";

    public AttriButeClassBean() {
    }

    /**
     * parseRowStr
     * 解析属性设置信息
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

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
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.attrCode = reqAry[0];
            this.attrName = reqAry[1];
            this.attrDesc = reqAry[2];
            super.checkStateId = Integer.parseInt(reqAry[3]);
            this.oldAttrCode = reqAry[4];
            this.status = reqAry[5]; //lzp add 12.3
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new AttriButeClassBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析属性设置请求出错", e);
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.attrCode).append("\t");
        buf.append(this.attrName).append("\t");
        buf.append(this.attrDesc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入条件是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Para_AttributeClass"),
                               "FAttrClsCode",
                               this.attrCode, this.oldAttrCode);
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
     * 获取可用的属性设置信息
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql =
            "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_AttributeClass") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);

    }

    /**
     * getListViewData2
     * 获取已审核的属性设置信息
     * @return String
     */
    public String getListViewData2() throws YssException {
        /*String strSql = "";
               strSql =
              "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
              pub.yssGetTableName("Tb_Para_AttributeClass") + " a " +
              " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
              " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
              " where fcheckstate = 1 order by FCheckState, FCreateTime desc";
               return this.builderListViewData(strSql);*/
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "属性分类代码\t属性分类名称";
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //-------------------------xuqiji 20100414--------------------------//
                (buildFilterSql().length() > 0 ? buildFilterSql() + " and ":" where ")+
                " a.fcheckstate = 1 order by a.FCheckState, a.FCreateTime desc";
            	//-----------------------------------end---------------------------//

            rs = dbl.openResultSet(strSql);
            bufShow = new StringBuffer();
            bufAll = new StringBuffer();
            while (rs.next()) {
                bufShow.append( (rs.getString("FAttrClsCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FAttrClsName") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.attrCode = rs.getString("FAttrClsCode") + "";
                this.attrName = rs.getString("FAttrClsName") + "";
                this.attrDesc = rs.getString("FDesc") + "";
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取属性分类数据出错", e);
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
     * 获取所有属性设置信息
     * @return String
     */
    public String getListViewData4() throws YssException {
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
     *buildFilterSql
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.attrCode.trim().length() != 0) { // wdy add 20070903 添加表别名:a
            	//edit by songjie 2011.10.31 BUG 2342 QDV4赢时胜(测试)2011年8月1日3_B 属性分类代码需支持模糊查询
                sResult = sResult + " and a.FAttrClsCode like '" + filterType.attrCode.replaceAll("'", "''") + "%'";//xuqiji 20100414
            }
            if (this.filterType.attrName.length() != 0) {
                sResult = sResult + " and a.FAttrClsName like '" +
                    filterType.attrName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.attrDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.attrDesc.replaceAll("'", "''") + "%'";
            }
            return sResult;
        }

        return "";
    }

    /**
     *
     * @param strSql String
     * @throws YssException
     * @return String
     */
    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.attrCode = rs.getString("FAttrClsCode") + "";
                this.attrName = rs.getString("FAttrClsName") + "";
                this.attrDesc = rs.getString("FDesc") + "";
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取属性信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() throws YssException {
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
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) throws YssException {
         String strSql = "";
         boolean bTrans = false; //代表是否开始了事务
         Connection conn = dbl.loadConnection();
         try {
            if (btOper == YssCons.OP_ADD) {
               strSql = "insert into " +
                     pub.yssGetTableName("Tb_Para_AttributeClass") +
                     "(FAttrClsCode,FAttrClsName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                     dbl.sqlString(this.attrCode) + "," +
                     dbl.sqlString(this.attrName) + "," +
                     dbl.sqlString(this.attrDesc) + "," +
                     (pub.getSysCheckState()?"0":"1") + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
                     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
            }
            if (btOper == YssCons.OP_EDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_AttributeClass") +
                     " set FAttrClsCode =" +
                     dbl.sqlString(this.attrCode) + ",FAttrClsName = " +
                     dbl.sqlString(this.attrName) + ",FDesc = " +
                     dbl.sqlString(this.attrDesc) + ",FCheckState = " +
                     (pub.getSysCheckState()?"0":"1") + ",FCreator = " +
                     dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                     dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                     " where FAttrClsCode = " + dbl.sqlString(this.oldAttrCode);
            }
            else if (btOper == YssCons.OP_DEL) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_AttributeClass") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FAttrClsCode = " +
                     dbl.sqlString(this.attrCode);
            }

            else if (btOper == YssCons.OP_AUDIT) {
               System.out.println(this.checkStateId);
               strSql = "update " + pub.yssGetTableName("Tb_Para_AttributeClass") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FAttrClsCode = " +
                     dbl.sqlString(this.attrCode);

            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

         }
         catch (Exception e) {
            throw new YssException("设置发行人信息出错！", e);
         }
         finally {
            dbl.endTransFinal(conn, bTrans);
         }

      }*/






    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) throws YssException {
         String strSql = "";
         boolean bTrans = false; //代表是否开始了事务
         Connection conn = dbl.loadConnection();
         try {
            if (btOper == YssCons.OP_ADD) {
               strSql = "insert into " +
                     pub.yssGetTableName("Tb_Para_AttributeClass") +
                     "(FAttrClsCode,FAttrClsName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                     dbl.sqlString(this.attrCode) + "," +
                     dbl.sqlString(this.attrName) + "," +
                     dbl.sqlString(this.attrDesc) + "," +
                     (pub.getSysCheckState()?"0":"1") + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
            }
            if (btOper == YssCons.OP_EDIT) {
     strSql = "update " + pub.yssGetTableName("Tb_Para_AttributeClass") +
                     " set FAttrClsCode =" +
                     dbl.sqlString(this.attrCode) + ",FAttrClsName = " +
                     dbl.sqlString(this.attrName) + ",FDesc = " +
                     dbl.sqlString(this.attrDesc) + ",FCheckState = " +
                     (pub.getSysCheckState()?"0":"1") + ",FCreator = " +
                     dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                     dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                     " where FAttrClsCode = " + dbl.sqlString(this.oldAttrCode);
            }
            else if (btOper == YssCons.OP_DEL) {
     strSql = "update " + pub.yssGetTableName("Tb_Para_AttributeClass") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FAttrClsCode = " +
                     dbl.sqlString(this.attrCode);
            }

            else if (btOper == YssCons.OP_AUDIT) {
               System.out.println(this.checkStateId);
     strSql = "update " + pub.yssGetTableName("Tb_Para_AttributeClass") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FAttrClsCode = " +
                     dbl.sqlString(this.attrCode);

            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

         }
         catch (Exception e) {
            throw new YssException("设置发行人信息出错！", e);
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
            strSql = "insert into " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                "(FAttrClsCode,FAttrClsName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                dbl.sqlString(this.attrCode) + "," +
                dbl.sqlString(this.attrName) + "," +
                dbl.sqlString(this.attrDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.28
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode(pub.getPrefixTB().trim());
                sysdata.setStrFunName("增加-属性分类信息");
                sysdata.setStrCode(this.attrCode);
                sysdata.setStrName(this.attrName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加属性分类信息出错！", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_AttributeClass") +
                " set FAttrClsCode =" +
                dbl.sqlString(this.attrCode) + ",FAttrClsName = " +
                dbl.sqlString(this.attrName) + ",FDesc = " +
                dbl.sqlString(this.attrDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FAttrClsCode = " + dbl.sqlString(this.oldAttrCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.28
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode(pub.getPrefixTB().trim());
                sysdata.setStrFunName("修改-属性分类信息");
                sysdata.setStrCode(this.attrCode);
                sysdata.setStrName(this.attrName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改属性分类信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据即把数据放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_AttributeClass") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FAttrClsCode = " +
                dbl.sqlString(this.attrCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.28
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode(pub.getPrefixTB().trim());
                sysdata.setStrFunName("删除-属性分类信息");
                sysdata.setStrCode(this.attrCode);
                sysdata.setStrName(this.attrName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除属性分类信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 修改时间：2008年3月26号
     * 修改人：单亮
     * 原方法功能：只能处理属性分类信息的审核和未审核的单条信息。
     * 新方法功能：可以处理属性分类信息审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理属性分类信息审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//     String strSql = "";
//     boolean bTrans = false; //代表是否开始了事务
//     Connection conn = dbl.loadConnection();
//     try {
//        strSql = "update " + pub.yssGetTableName("Tb_Para_AttributeClass") +
//              " set FCheckState = " +
//              this.checkStateId + ", FCheckUser = " +
//              dbl.sqlString(pub.getUserCode()) +
//              ", FCheckTime = '" +
//              YssFun.formatDatetime(new java.util.Date()) + "'" +
//              " where FAttrClsCode = " +
//              dbl.sqlString(this.attrCode);
//        conn.setAutoCommit(false);
//        bTrans = true;
//        dbl.executeSql(strSql);
//        //---------lzp add 11.28
//        if (this.status.equalsIgnoreCase("1")) {
//           com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
//                 funsetting.SysDataBean();
//           sysdata.setYssPub(pub);
//           sysdata.setStrAssetGroupCode(pub.getPrefixTB().trim());
//           if(this.checkStateId==1){
//          sysdata.setStrFunName("审核-属性分类信息");
//          }else{
//           sysdata.setStrFunName("反审核-属性分类信息");
//           }
//
//           sysdata.setStrCode(this.attrCode);
//           sysdata.setStrName(this.attrName);
//           sysdata.setStrUpdateSql(strSql);
//           sysdata.setStrCreator(pub.getUserName());
//           sysdata.addSetting();
//        }
//       //-----------------------
//
//        conn.commit();
//        bTrans = false;
//        conn.setAutoCommit(true);
//     }
//
//     catch (Exception e) {
//        throw new YssException("审核属性分类信息出错！", e);
//     }
//     finally {
//        dbl.endTransFinal(conn, bTrans);
//     }
        //修改后的代码
        //-------------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " +
                        pub.yssGetTableName("Tb_Para_AttributeClass") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FAttrClsCode = " +
                        dbl.sqlString(this.attrCode);
                    dbl.executeSql(strSql);
                }
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A    
            } else if (attrCode != null && (!attrCode.equalsIgnoreCase(""))) { //如果sRecycled为空，而attrCode不为空，则按照attrCode来执行sql语句
                strSql = "update " +
                    pub.yssGetTableName("Tb_Para_AttributeClass") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FAttrClsCode = " +
                    dbl.sqlString(this.attrCode);
            }
            //---------lzp add 11.28
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode(pub.getPrefixTB().trim());
                if (this.checkStateId == 1) {
                    sysdata.setStrFunName("审核-属性分类信息");
                } else {
                    sysdata.setStrFunName("反审核-属性分类信息");
                }
                sysdata.setStrCode(this.attrCode);
                sysdata.setStrName(this.attrName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核属性分类信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
//--------------end

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
        AttriButeClassBean befEditBean = new AttriButeClassBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  FAttrClsCode =" + dbl.sqlString(this.oldAttrCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.attrCode = rs.getString("FAttrClsCode") + "";
                befEditBean.attrName = rs.getString("FAttrClsName") + "";
                befEditBean.attrDesc = rs.getString("FDesc") + "";

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

    /**
     * 删除回收站的数据，即从数据库彻底删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
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
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Para_AttributeClass") +
                        " where FAttrClsCode = " +
                        dbl.sqlString(this.attrCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而attrCode不为空，则按照attrCode来执行sql语句
            else if (attrCode != "" && attrCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_AttributeClass") +
                    " where FAttrClsCode = " +
                    dbl.sqlString(this.attrCode);
                //执行sql语句
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
