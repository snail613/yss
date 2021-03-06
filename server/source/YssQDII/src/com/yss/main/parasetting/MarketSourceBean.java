package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class MarketSourceBean
    extends BaseDataSettingBean implements IDataSetting {

    private String markSourceCode = "";
    private String markSourceName = "";
    private String markSourceDesc = "";

    private MarketSourceBean filterType;
    private String oldMarkSourceCode = "";
    private String sRecycled = "";

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
            this.markSourceCode = reqAry[0];
            this.markSourceName = reqAry[1];
            //---edit by songjie 2011.12.12 BUG 3201 QDV4赢时胜(测试)2011年11月22日02_B start---//
			if (reqAry[2] != null) {
				if (reqAry[2].indexOf("【Enter】") >= 0) {
					this.markSourceDesc = reqAry[2].replaceAll("【Enter】", "\r\n");
				} else {
					this.markSourceDesc = reqAry[2];
				}
			}
			//---edit by songjie 2011.12.12 BUG 3201 QDV4赢时胜(测试)2011年11月22日02_B end---//
            super.checkStateId = Integer.parseInt(reqAry[3]);
            this.oldMarkSourceCode = reqAry[4];
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new MarketSourceBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("行情设置请求出错", e);
        }

    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.markSourceCode).append("\t");
        buf.append(this.markSourceName).append("\t");
        buf.append(this.markSourceDesc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_para_MarketSource"),
                               "FMktSrcCode",
                               this.markSourceCode, this.oldMarkSourceCode);
    }

    public String getAllSetting() {
        return "";
    }

    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql =
            "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_para_MarketSource") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);

    }

    public String getListViewData2() throws YssException {
        /*String strSql = "";
               strSql =
              "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
              pub.yssGetTableName("Tb_para_MarketSource") + " a " +
              " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
              " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
              ( (buildFilterSql().length() == 0) ? " where " :
               buildFilterSql() + " and ") +
              " fcheckstate = 1 order by FCheckState, FCreateTime desc";
               return this.builderListViewData(strSql);*/
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "行情来源代码\t行情来源名称\t描述";
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_para_MarketSource") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ( (buildFilterSql().length() == 0) ? " where " :
                 buildFilterSql() + " and ") +
                " a.fcheckstate = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FMktSrcCode") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FMktSrcName") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FDesc") + "").trim()).append(YssCons.YSS_LINESPLITMARK);

                this.markSourceCode = rs.getString("FMktSrcCode") + "";
                this.markSourceName = rs.getString("FMktSrcName") + "";
                this.markSourceDesc = rs.getString("FDesc") + "";
                super.setRecLog(rs);

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
            throw new YssException("获取行情来源信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    // by yanghaiming 20100325 MS00927 QDII4.1赢时胜上海2010年03月19日01_AB 
    public String getListViewData3() throws YssException{
    	String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "价格来源代码\t价格来源名称\t描述";
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_para_MarketSource") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ( (buildFilterSql().length() == 0) ? " where " :
                 buildFilterSql() + " and ") +
                " a.fcheckstate = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FMktSrcCode") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FMktSrcName") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FDesc") + "").trim()).append(YssCons.YSS_LINESPLITMARK);

                this.markSourceCode = rs.getString("FMktSrcCode") + "";
                this.markSourceName = rs.getString("FMktSrcName") + "";
                this.markSourceDesc = rs.getString("FDesc") + "";
                super.setRecLog(rs);

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
            throw new YssException("获取价格来源信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public IDataSetting getSetting() {
        return null;
    }

    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.markSourceCode != null && this.filterType.markSourceCode.length() != 0) {
                sResult = sResult + " and a.FMktSrcCode like '" +
                    filterType.markSourceCode.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.markSourceName != null && this.filterType.markSourceName.length() != 0) {
                sResult = sResult + " and a.FMktSrcName like '" +
                    filterType.markSourceName.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.markSourceDesc != null && this.filterType.markSourceDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.markSourceDesc.replaceAll("'", "''") + "%'";
            }
            return sResult;
        }

        return "";
    }

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

                this.markSourceCode = rs.getString("FMktSrcCode") + "";
                this.markSourceName = rs.getString("FMktSrcName") + "";
                this.markSourceDesc = rs.getString("FDesc") + "";
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
            throw new YssException("获取行情信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() {

        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /*  public void saveSetting(byte btOper) throws YssException {
         String strSql = "";
         boolean bTrans = false; //代表是否开始了事务
         Connection conn = dbl.loadConnection();
         try {
            if (btOper == YssCons.OP_ADD) {
               strSql = "insert into " +
                     pub.yssGetTableName("Tb_para_MarketSource") +
                     "(FMktSrcCode,FMktSrcName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                     dbl.sqlString(this.markSourceCode) + "," +
                     dbl.sqlString(this.markSourceName) + "," +
                     dbl.sqlString(this.markSourceDesc) + "," +
                     (pub.getSysCheckState() ? "0" : "1") + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
                     (pub.getSysCheckState() ? "' '" :
                      dbl.sqlString(this.creatorCode)) + ")";
            }
            if (btOper == YssCons.OP_EDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_para_MarketSource") +
                     " set FMktSrcCode =" +
                     dbl.sqlString(this.markSourceCode) + ",FMktSrcName= " +
                     dbl.sqlString(this.markSourceName) + ",FDesc = " +
                     dbl.sqlString(this.markSourceDesc) + ",FCheckState = " +
                     (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                     dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                     dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                     (pub.getSysCheckState() ? "' '" :
                      dbl.sqlString(this.creatorCode)) +
                     " where FMktSrcCode = " +
                     dbl.sqlString(this.oldMarkSourceCode);
            }
            else if (btOper == YssCons.OP_DEL) {
               strSql = "update " + pub.yssGetTableName("Tb_para_MarketSource") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FMktSrcCode = " +
                     dbl.sqlString(this.markSourceCode);
            }

            else if (btOper == YssCons.OP_AUDIT) {
               System.out.println(this.checkStateId);
               strSql = "update " + pub.yssGetTableName("Tb_para_MarketSource") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FMktSrcCode = " +
                     dbl.sqlString(this.markSourceCode);

            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

         }
         catch (Exception e) {
            throw new YssException("设置行情来源信息出错！", e);
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
                pub.yssGetTableName("Tb_para_MarketSource") +
                "(FMktSrcCode,FMktSrcName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                dbl.sqlString(this.markSourceCode) + "," +
                dbl.sqlString(this.markSourceName) + "," +
                dbl.sqlString(this.markSourceDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加行情设置出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_para_MarketSource") +
                " set FMktSrcCode =" +
                dbl.sqlString(this.markSourceCode) + ",FMktSrcName= " +
                dbl.sqlString(this.markSourceName) + ",FDesc = " +
                dbl.sqlString(this.markSourceDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FMktSrcCode = " +
                dbl.sqlString(this.oldMarkSourceCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改行情设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_para_MarketSource") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FMktSrcCode = " +
                dbl.sqlString(this.markSourceCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除行情设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 修改时间：2008年3月26号
     * 修改人：单亮
     * 原方法功能：只能处理行情设置的审核和未审核的单条信息。
     * 新方法功能：可以处理行情设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理行情设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//    String strSql = "";
//    boolean bTrans = false; //代表是否开始了事务
//    Connection conn = dbl.loadConnection();
//    try {
//       strSql = "update " + pub.yssGetTableName("Tb_para_MarketSource") +
//           " set FCheckState = " +
//           this.checkStateId + ", FCheckUser = " +
//           dbl.sqlString(pub.getUserCode()) +
//           ", FCheckTime = '" +
//           YssFun.formatDatetime(new java.util.Date()) + "'" +
//           " where FMktSrcCode = " +
//           dbl.sqlString(this.markSourceCode);
//       conn.setAutoCommit(false);
//       bTrans = true;
//       dbl.executeSql(strSql);
//       conn.commit();
//       bTrans = false;
//       conn.setAutoCommit(true);
//    }
//
//    catch (Exception e) {
//       throw new YssException("审核行情设置出错", e);
//    }
//    finally {
//       dbl.endTransFinal(conn, bTrans);
//    }
        //修改后的代码
        //---------------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_para_MarketSource") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FMktSrcCode = " +
                        dbl.sqlString(this.markSourceCode);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而markSourceCode不为空，则按照markSourceCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (markSourceCode != null && !markSourceCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("Tb_para_MarketSource") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FMktSrcCode = " +
                    dbl.sqlString(this.markSourceCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核行情设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
//---------------------end

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
        MarketSourceBean befEditBean = new MarketSourceBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_para_MarketSource") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  a.FMktSrcCode =" + dbl.sqlString(this.oldMarkSourceCode) +
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.markSourceCode = rs.getString("FMktSrcCode") + "";
                befEditBean.markSourceName = rs.getString("FMktSrcName") + "";
                befEditBean.markSourceDesc = rs.getString("FDesc") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /**
     * 从回收站删除数据，即从数据库彻底删除
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
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equals("")) {
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
                        pub.yssGetTableName("Tb_para_MarketSource") +
                        " where FMktSrcCode = " +
                        dbl.sqlString(this.markSourceCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (markSourceCode != null && !markSourceCode.equals("")) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_para_MarketSource") +
                    " where FMktSrcCode = " +
                    dbl.sqlString(this.markSourceCode);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
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
