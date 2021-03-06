package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class SubTransferTypeBean
    extends BaseDataSettingBean implements
    IDataSetting {

    private String typeCode;
    private String typeName;
    private String superTypeCode;
    private String superTypeName;
    private String typeDesc;
    private String oldTypeCode;
    private SubTransferTypeBean filterType;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
    private String status = ""; //是否记入系统信息状态  lzp 11.30 add
    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {

        StringBuffer buf = new StringBuffer();
        buf.append(typeCode.trim()).append("\t");
        buf.append(typeName.trim()).append("\t");
        buf.append(superTypeCode.trim()).append("\t");
        buf.append(superTypeName.trim()).append("\t");
        buf.append(typeDesc.trim()).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               "Tb_Base_SubTransferType",
                               "FSubTsfTypeCode",
                               this.typeCode, this.oldTypeCode);
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
        /*      strSql = " select y.* from " +
         " (select FSubTsfTypeCode from Tb_Base_SubTransferType " +
         " where FCheckState <> 2 group by FSubTsfTypeCode) x join " +
                       " (select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName  from " +
                       " Tb_Base_SubTransferType a " +
                       " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                       " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
         " left join( select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType" +
                       ")d on a.FTsfTypeCode=d.FTsfTypeCode " +
                       buildFilterSql() +
                       ") y on x.FSubTsfTypeCode =y.FSubTsfTypeCode " +
         "order by y.FCheckState, y.FCreateTime desc "; //最外层的命名应该是y,fazmm20070907
         */
        /**
         *下面从新写的SQL语句是用来查出所有调拨子类型的所有相关条目，上面以前写的查不出回收站里的内容。MS00149 QDV4南方2009年1月5日05_B
         * 2009.01.15 方浩
         */
        strSql =
            " select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName  from " +
            " Tb_Base_SubTransferType a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
            " left join( select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType" +
            ")d on a.FTsfTypeCode=d.FTsfTypeCode " +

            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";

        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {

        /* String strSql = "";
         strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName  from " +
               " Tb_Base_SubTransferType a " +
               " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
               " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
               " left join( select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) d on a.FTsfTypeCode=d.FTsfTypeCode " +
               ( (buildFilterSql().length() > 0) ? buildFilterSql() + " and " :
                " where ") +
         " FCheckState = 1 order by FSubTsfTypeCode, FCheckState, FCreateTime desc";
         return this.builderListViewData(strSql);*/


        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "调拨子类型代码\t调拨子类型名称";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName  from " +
                " Tb_Base_SubTransferType a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join( select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) d on a.FTsfTypeCode=d.FTsfTypeCode " +
                ( (buildFilterSql().length() > 0) ?
                 buildFilterSql() + " and " :
                 " where ") +
                " a.FCheckState = 1 order by a.FSubTsfTypeCode, a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            bufShow = new StringBuffer();
            bufAll = new StringBuffer();
            while (rs.next()) {
                bufShow.append( (rs.getString("FSubTsfTypeCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FSubTsfTypeName") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.typeCode = rs.getString("FSubTsfTypeCode") + "";
                this.typeName = rs.getString("FSubTsfTypeName") + "";
                this.superTypeCode = rs.getString("FTsfTypeCode") + "";
                this.superTypeName = rs.getString("FTsfTypeName") + "";
                this.typeDesc = rs.getString("FDesc") + "";

                super.setRecLog(rs);

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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用调拨子类型数据出错", e);
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

        strSql = " select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSubTsfTypeName from Tb_Base_SubTransferType a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
            " left join(select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_TransferType)d on a.FSubTsfTypeCode=d.FSubTsfTypeCode" +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);

    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    /*  public IParaSetting getSetting() {
         return null;
      }*/

    /**
     * getTreeViewData1
     *
     * @return String
     * @throws YssException 
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
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
            reqAry = sTmpStr.split("\t");
            this.typeCode = reqAry[0];
            this.typeName = reqAry[1];
            this.superTypeCode = reqAry[2];
            this.superTypeName = reqAry[3];
            this.typeDesc = reqAry[4];
            super.checkStateId = Integer.parseInt(reqAry[5]);
            this.oldTypeCode = reqAry[6];
            this.status = reqAry[7]; //lzp add 11.30
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SubTransferTypeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析调拨子类型信息出错", e);
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
     *
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
           if (btOper == YssCons.OP_ADD) {
              strSql =
                    "insert into Tb_Base_SubTransferType(FSubTsfTypeCode,FSubTsfTypeName,FTsfTypeCode, " +
                    " FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                    " values(" + dbl.sqlString(this.typeCode) + "," +
                    dbl.sqlString(this.typeName) + "," +
                    dbl.sqlString(this.superTypeCode) + "," +
                    //   dbl.sqlString(this.superTypeName) + "," +
                    dbl.sqlString(this.typeDesc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + ", " +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update Tb_Base_SubTransferType set FSubTsfTypeCode=" +
                    dbl.sqlString(this.typeCode) + ",FSubTsfTypeName=" +
                    dbl.sqlString(this.typeName) + ",FTsfTypeCode=" +
                    dbl.sqlString(this.superTypeCode) + ",FDesc=" +
                    dbl.sqlString(this.typeDesc) + ",FCheckState = " +
                    (pub.getSysCheckState() ? "0" : "1") + ",FCreator=" +
                    dbl.sqlString(this.creatorCode) + ",FCreateTime=" +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
                    "where FSubTsfTypeCode=" + dbl.sqlString(this.oldTypeCode);
           }

           else if (btOper == YssCons.OP_DEL) {
              strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSubTsfTypeCode = " +
                    dbl.sqlString(this.typeCode);

           }
           else if (btOper == YssCons.OP_AUDIT) {

              System.out.println(this.checkStateId);

              strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    "where FSubTsfTypeCode=" + dbl.sqlString(this.typeCode);

           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("设置调拨子类型信息出错！", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
        }

     }*/

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql =
                "insert into Tb_Base_SubTransferType(FSubTsfTypeCode,FSubTsfTypeName,FTsfTypeCode, " +
                " FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.typeCode) + "," +
                dbl.sqlString(this.typeName) + "," +
                dbl.sqlString(this.superTypeCode) + "," +
                //   dbl.sqlString(this.superTypeName) + "," +
                dbl.sqlString(this.typeDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + ", " +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-调拨子类型设置");
                sysdata.setStrCode(this.typeCode);
                sysdata.setStrName(this.typeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增调拨子类型设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理回购品种信息设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */

    public void checkSetting() throws YssException {
        /*    String strSql = "";
            boolean bTrans = false; //代表是否开始了事务
            Connection conn = dbl.loadConnection();
            try {

               strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ",FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     "where FSubTsfTypeCode=" + dbl.sqlString(this.typeCode);


               conn.setAutoCommit(false);
               bTrans = true;
               dbl.executeSql(strSql);
               //---------lzp add 11.30
               if (this.status.equalsIgnoreCase("1")) {
         com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                  sysdata.setYssPub(pub);
                  sysdata.setStrAssetGroupCode("Common");
                  if(this.checkStateId==1){
                  sysdata.setStrFunName("审核-调拨子类型设置");
              }else{
                   sysdata.setStrFunName("反审核-调拨子类型设置");
              }

                  sysdata.setStrCode(this.typeCode);
                  sysdata.setStrName(this.typeName);
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
               throw new YssException("审核调拨子类型设置信息出错", e);
            }
            finally {
               dbl.endTransFinal(conn, bTrans);
            }
         */
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环还原
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false); //开启一个事物
            bTrans = true; //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql =
                        "update Tb_Base_SubTransferType set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSubTsfTypeCode=" +
                        dbl.sqlString(this.typeCode); //更新数据的SQL语句
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而typeCode不为空，则按照typeCode来执行sql语句
            else if (typeCode != null && (!typeCode.equalsIgnoreCase(""))) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSubTsfTypeCode=" +
                    dbl.sqlString(this.typeCode); //更新数据的SQL语句
                dbl.executeSql(strSql); //执行更新操作
            }
            if (this.status.equalsIgnoreCase("1")) { //判断status是否等于1,当传入1的时候就记录系统的信息状态
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub); //设置pub的值
                sysdata.setStrAssetGroupCode("Common"); //设置StrAssetGroupCode的值
                if (this.checkStateId == 1) { //如果checkStateId==1就是它要的状态是审核状
                    sysdata.setStrFunName("审核-调拨子类型设置"); //设置StrFunName的值
                } else {
                    sysdata.setStrFunName("反审核-调拨子类型设置"); //设置StrFunName的值
                }
                sysdata.setStrCode(this.typeCode); //设置StrCode的值
                sysdata.setStrName(this.typeName); //设置StrName的值
                sysdata.setStrUpdateSql(strSql); //设置StrUpdateSql的值
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting(); //把这些以上数据添加到系统数据表Tb_Fun_SysData
            }
//-----------------------
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核调拨子类型设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
//----------------end

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FSubTsfTypeCode = " +
                dbl.sqlString(this.typeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-调拨子类型设置");
                sysdata.setStrCode(this.typeCode);
                sysdata.setStrName(this.typeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除调拨子类型设置信息出错", e);
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
            strSql = "update Tb_Base_SubTransferType set FSubTsfTypeCode=" +
                dbl.sqlString(this.typeCode) + ",FSubTsfTypeName=" +
                dbl.sqlString(this.typeName) + ",FTsfTypeCode=" +
                dbl.sqlString(this.superTypeCode) + ",FDesc=" +
                dbl.sqlString(this.typeDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator=" +
                dbl.sqlString(this.creatorCode) + ",FCreateTime=" +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                "where FSubTsfTypeCode=" + dbl.sqlString(this.oldTypeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-调拨子类型设置");
                sysdata.setStrCode(this.typeCode);
                sysdata.setStrName(this.typeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新调拨子类型设置信息出错", e);
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
     * @throws YssException 
     */
    public String getOperValue(String sType) throws YssException {
    	if(sType.startsWith("mandata"))
    	{
	    	String sHeader = "";
	        String sShowDataStr = "";
	        String strSql = "";
	        ResultSet rs = null;
	        String sAllDataStr = "";
	        StringBuffer bufShow = new StringBuffer();
	        StringBuffer bufAll = new StringBuffer();
	        try {
	            sHeader = "调拨子类型代码\t调拨子类型名称";
	            if(sType.equals("mandata_all"))
	            {
		            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName  from " +
		                " Tb_Base_SubTransferType a " +
		                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
		                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
		                " left join( select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) d on a.FTsfTypeCode=d.FTsfTypeCode " +
		                ( (buildFilterSql().length() > 0) ?
		                 buildFilterSql() + " and " :
		                 " where ") +
		                " a.FCheckState = 1 order by a.FSubTsfTypeCode, a.FCheckState, a.FCreateTime desc";
	            }else
	            {
	            	String[] filterStr = sType.split(":");
	            	
	            	 strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName  from " +
		                " Tb_Base_SubTransferType a " +
		                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
		                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
		                " left join( select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) d on a.FTsfTypeCode=d.FTsfTypeCode " +
		                ( (buildFilterSql().length() > 0) ?
		                 buildFilterSql() + " and " :
		                 " where ") +
		                 " a.FSubTsfTypeCode in (" + filterStr[1] + ") and " + 
		                " a.FCheckState = 1 order by a.FSubTsfTypeCode, a.FCheckState, a.FCreateTime desc";
	            }
	
	            rs = dbl.openResultSet(strSql);
	            bufShow = new StringBuffer();
	            bufAll = new StringBuffer();
	            while (rs.next()) {
	                bufShow.append( (rs.getString("FSubTsfTypeCode") + "").trim()).
	                    append(
	                        "\t");
	                bufShow.append( (rs.getString("FSubTsfTypeName") + "").trim()).
	                    append(
	                        YssCons.YSS_LINESPLITMARK);
	
	                this.typeCode = rs.getString("FSubTsfTypeCode") + "";
	                this.typeName = rs.getString("FSubTsfTypeName") + "";
	                this.superTypeCode = rs.getString("FTsfTypeCode") + "";
	                this.superTypeName = rs.getString("FTsfTypeName") + "";
	                this.typeDesc = rs.getString("FDesc") + "";
	
	                super.setRecLog(rs);
	
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
	
	            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
	        } catch (Exception e) {
	            throw new YssException("获取可用调拨子类型数据出错", e);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
        }
    	return "";
    }

    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.typeCode.length() != 0) { // wdy add 20070902 添加表别名：a
                sResult = sResult + "and a.FSubTsfTypeCode like'" +
                    filterType.typeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.typeName.length() != 0) {
                sResult = sResult + "and a.FSubTsfTypeName like'" +
                    filterType.typeName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.superTypeCode.length() != 0) {
                sResult = sResult + "and a.FTsfTypeCode like'" +
                    filterType.superTypeCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.typeDesc.length() != 0) {
                sResult = sResult + "and a.FDesc like'" +
                    filterType.typeDesc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public void setSecrityIssuserAttr(ResultSet rs) throws SQLException {
        this.typeCode = rs.getString("FSubTsfTypeCode") + "";
        this.typeName = rs.getString("FSubTsfTypeName") + "";
        this.superTypeCode = rs.getString("FTsfTypeCode") + "";

        this.superTypeName = rs.getString("FTsfTypeName") + "";
        this.typeDesc = rs.getString("FDesc") + "";

        super.setRecLog(rs);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.typeCode = rs.getString("FSubTsfTypeCode") + "";
                this.typeName = rs.getString("FSubTsfTypeName") + "";
                this.superTypeCode = rs.getString("FTsfTypeCode") + "";
                this.superTypeName = rs.getString("FTsfTypeName") + "";
                this.typeDesc = rs.getString("FDesc") + "";

                super.setRecLog(rs);
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
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取调拨子类型信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getSetting
     *
     * @return IBaseSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        SubTransferTypeBean befEditBean = new SubTransferTypeBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                " select y.* from " +
                " (select FSubTsfTypeCode from Tb_Base_SubTransferType " +
                " where FCheckState <> 2 group by FSubTsfTypeCode) x join " +
                " (select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName  from " +
                " Tb_Base_SubTransferType a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join( select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType" +
                ")d on a.FTsfTypeCode=d.FTsfTypeCode " +
                " where  FSubTsfTypeCode =" +
                dbl.sqlString(this.oldTypeCode) +
                ") y on x.FSubTsfTypeCode =y.FSubTsfTypeCode " +
                "order by y.FCheckState, y.FCreateTime desc "; // wdy modify 20080902

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.typeCode = rs.getString("FSubTsfTypeCode") + "";
                befEditBean.typeName = rs.getString("FSubTsfTypeName") + "";
                befEditBean.superTypeCode = rs.getString("FTsfTypeCode") + "";
                befEditBean.superTypeName = rs.getString("FTsfTypeName") + "";
                befEditBean.typeDesc = rs.getString("FDesc") + "";
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

    public String getTypeCode() {
        return typeCode;
    }

    public String getSuperTypeCode() {
        return superTypeCode;
    }

//--MS00007 add by songjie 2009-03-16
    /**
     * 获取调拨子类型查询实例
     * @return SubTransferTypeBean
     */
    public SubTransferTypeBean getFilterType() {
        return filterType;
    }

    /**
     * 获取调拨类型名称
     * @return String
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * 获取描述信息
     * @return String
     */
    public String getTypeDesc() {
        return typeDesc;
    }

    //--MS00007 add by songjie 2009-03-16

    /**
     * deleteRecycleData

         public void deleteRecycleData() {
         }
     */
    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 回收站的删除功能调用此方法deleteRecycleData()
     * 从数据库删除数据，即彻底删除数据,可以多个一删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
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
                        pub.yssGetTableName("Tb_Base_SubTransferType") +
                        " where FSubTsfTypeCode=" +
                        dbl.sqlString(this.typeCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而typeCode不为空，则按照typeCode来执行sql语句
            else if (typeCode != "" && typeCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Base_SubTransferType") +
                    " where FSubTsfTypeCode=" +
                    dbl.sqlString(this.typeCode);

                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public void setSuperTypeCode(String superTypeCode) {
        this.superTypeCode = superTypeCode;
    }

    //--MS00007 add by songjie 2009-03-16
    /**
     * 设置调拨子类型查询实例
     * @param filterType SubTransferTypeBean
     */
    public void setFilterType(SubTransferTypeBean filterType) {
        this.filterType = filterType;
    }

    /**
     * 设置调拨类型名称
     * @param typeName String
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 设置描述信息
     * @param typeDesc String
     */
    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
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
    //--MS00007 add by songjie 2009-03-16
}
