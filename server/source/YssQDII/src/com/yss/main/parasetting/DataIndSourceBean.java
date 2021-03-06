package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class DataIndSourceBean
    extends BaseDataSettingBean implements IDataSetting {
    private String IndexSrcCode = "";
    private String IndexSrcName = "";
    private String Desc = "";
    private String OldIndexSrcCode = "";
    private String sRecycled = "";
    private DataIndSourceBean FilterType;
    public DataIndSourceBean() {}

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public void setIndexSrcName(String IndexSrcName) {
        this.IndexSrcName = IndexSrcName;
    }

    public String getIndexSrcName() {
        return IndexSrcName;
    }

    public void setFilterType(DataIndSourceBean FilterType) {
        this.FilterType = FilterType;
    }

    public DataIndSourceBean getFilterType() {
        return FilterType;
    }

    public void setOldIndexSrcCode(String OldIndexSrcCode) {
        this.OldIndexSrcCode = OldIndexSrcCode;
    }

    public String getOldIndexSrcCode() {
        return OldIndexSrcCode;
    }

    public void setIndexSrcCode(String IndexSrcCode) {
        this.IndexSrcCode = IndexSrcCode;
    }

    public String getIndexSrcCode() {
        return IndexSrcCode;
    }

    /**
     * 解析字符串
     * @param sRowStr String
     * @throws YssException
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
            this.IndexSrcCode = reqAry[0];
            this.IndexSrcName = reqAry[1];
            
            //---edit by songjie 2011.12.12 BUG 3201 QDV4赢时胜(测试)2011年11月22日02_B start---//
			if (reqAry[2] != null) {
				if (reqAry[2].indexOf("【Enter】") >= 0) {
					this.Desc = reqAry[2].replaceAll("【Enter】", "\r\n");
				} else {
					this.Desc = reqAry[2];
				}
			}
			//---edit by songjie 2011.12.12 BUG 3201 QDV4赢时胜(测试)2011年11月22日02_B end---//
            super.checkStateId = Integer.parseInt(reqAry[3]);
            this.OldIndexSrcCode = reqAry[4];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new DataIndSourceBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析指数来源设置出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.IndexSrcCode.trim()).append("\t");
        buf.append(this.IndexSrcName.trim()).append("\t");
        buf.append(this.Desc.trim()).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_IndexSource"),
                               "FIndexSrcCode", this.IndexSrcCode, this.OldIndexSrcCode);
    }

    public String getAllSetting() {
        return "";
    }

    private String buildFilterSql() {
        String strSql = "";
        if (this.FilterType != null) {
            strSql = " where 1=1 ";
            if (this.FilterType.IndexSrcCode.trim().length() > 0) {
                strSql += " and a.FIndexSrcCode like '" + this.FilterType.IndexSrcCode.trim().replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.IndexSrcName.trim().length() > 0) {
                strSql += " and a.FIndexSrcName like '" + this.FilterType.IndexSrcName.trim().replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.Desc.trim().length() > 0) {
                strSql += " and a.FDesc like '" + this.FilterType.Desc.trim().replaceAll("'", "''") + "%'";
            }
        }
        return strSql;
    }

    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_IndexSource") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "指数来源代码\t指数来源名称\t描  述";
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_IndexSource") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.fcheckstate = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FIndexSrcCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FIndexSrcName") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FDesc") + "").trim()).append(
                    YssCons.YSS_LINESPLITMARK);

                this.IndexSrcCode = rs.getString("FIndexSrcCode") + "";
                this.IndexSrcName = rs.getString("FIndexSrcName") + "";
                this.Desc = rs.getString("FDesc") + "";
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
            throw new YssException("获取指数来源信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData3() {
        return "";
    }

    public String getListViewData4() {
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

                this.IndexSrcCode = rs.getString("FIndexSrcCode") + "";
                this.IndexSrcName = rs.getString("FIndexSrcName") + "";
                this.Desc = rs.getString("FDesc") + "";
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
            throw new YssException("获取指数来源信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public IDataSetting getSetting() {
        return null;
    }

    public String getTreeViewData1() {
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

    public String addSetting() throws YssException {
        String strSql = "";
        boolean btrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_IndexSource") +
                " (FIndexSrcCode,FIndexSrcName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser ) values(" +
                dbl.sqlString(this.IndexSrcCode) + "," +
                dbl.sqlString(this.IndexSrcName) + "," +
                dbl.sqlString(this.Desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            btrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            btrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("添加指数来源设置出错", e);
        } finally {
            dbl.endTransFinal(conn, btrans);
        }
        return null;
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean btrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_IndexSource") +
                " set FIndexSrcCode= " + dbl.sqlString(this.IndexSrcCode) + ", FIndexSrcName= " + dbl.sqlString(this.IndexSrcName)
                + ", FDesc=" + dbl.sqlString(this.Desc) + ", FCheckState=" + (pub.getSysCheckState() ? "0" : "1")
                + ", FCreator=" + dbl.sqlString(this.creatorCode) + ", FCreateTime=" + dbl.sqlString(this.creatorTime) + ", FCheckUser="
                + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + " where FIndexSrcCode = " + dbl.sqlString(this.OldIndexSrcCode);
            conn.setAutoCommit(false);
            btrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            btrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改指数来源设置出错", e);
        } finally {
            dbl.endTransFinal(conn, btrans);
        }
        return null;
    }

    /***
     * 删除数据即放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean btrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("Tb_Para_IndexSource") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser =" + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime ='" + YssFun.formatDate(new java.util.Date()) + "'" +
                " where FIndexSrcCode =" + dbl.sqlString(this.IndexSrcCode);
            conn.setAutoCommit(false);
            btrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            btrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除指数来源设置出错", e);
        } finally {
            dbl.endTransFinal(conn, btrans);
        }
    }

    /**
     * 修改时间：2008年3月23号
     * 修改人：单亮
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql ="";
//      boolean btrans=false;
//      Connection conn=dbl.loadConnection();
//      try{
//         strSql =" update "+pub.yssGetTableName("Tb_Para_IndexSource")+
//               " set FCheckState = "+ this.checkStateId +
//               ", FCheckUser ="+dbl.sqlString(pub.getUserCode())+
//               ", FCheckTime ='"+YssFun.formatDate(new java.util.Date())+"'"+
//               " where FIndexSrcCode ="+dbl.sqlString(this.IndexSrcCode);
//         conn.setAutoCommit(false);
//         btrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         btrans = false;
//         conn.setAutoCommit(true);
//      }catch(Exception e){
//         throw new YssException ("审核指数来源设置出错",e);
//      }finally{
//         dbl.endTransFinal(conn,btrans);
//      }
        //修改后的代码
        //-----------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = " update " + pub.yssGetTableName("Tb_Para_IndexSource") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser =" + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime ='" + YssFun.formatDate(new java.util.Date()) +
                        "'" +
                        " where FIndexSrcCode =" + dbl.sqlString(this.IndexSrcCode);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而IndexSrcCode不为空，则按照IndexSrcCode来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (IndexSrcCode != null && !IndexSrcCode.equalsIgnoreCase("")) {
                strSql = " update " + pub.yssGetTableName("Tb_Para_IndexSource") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser =" + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime ='" + YssFun.formatDate(new java.util.Date()) +
                    "'" +
                    " where FIndexSrcCode =" + dbl.sqlString(this.IndexSrcCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核指数来源设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //------------------end

    }

    public String getOperValue(String sType) {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        DataIndSourceBean indexsource = new DataIndSourceBean();
        String strSql = "";
        ResultSet rs = null;
        boolean btrans = false;
        try {
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_IndexSource") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  a.FIndexSrcCode =" + dbl.sqlString(this.OldIndexSrcCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                indexsource.IndexSrcCode = rs.getString("FIndexSrcCode") + "";
                indexsource.IndexSrcName = rs.getString("FIndexSrcName") + "";
                indexsource.Desc = rs.getString("FDesc") + "";
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
        return indexsource.buildRowStr();
    }

    /**
     * 从回收站删除数据，即从数据库彻底删除数据
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
                        pub.yssGetTableName("Tb_Para_IndexSource") +
                        " where FIndexSrcCode =" + dbl.sqlString(this.IndexSrcCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (IndexSrcCode != "" && IndexSrcCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_IndexSource") +
                    " where FIndexSrcCode =" + dbl.sqlString(this.IndexSrcCode);
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
