package com.yss.main.voucher;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.vsub.YssFinance;

public class VchPortSetLinkBean
    extends BaseDataSettingBean implements IDataSetting {
    private String linkCode = ""; //	链接代码
    private String linkName = ""; //	链接名称
    private String portCode = ""; //	组合代码
    private String bookSetCode = ""; //	套帐代码
    private String desc = ""; //	描述

    private String oldLinkCode = "";

    private VchPortSetLinkBean filterType = null;

    private HashMap hmPortBook = null;
    private String sRecycled = ""; //增加回收站处理字段 by leeyu 2008-10-21 BUG:0000491
    public VchPortSetLinkBean() {
        hmPortBook = new HashMap();
    }

    public void setVchBookSet(ResultSet rs) throws SQLException {
        this.linkCode = rs.getString("FLinkCode");
        this.linkName = rs.getString("FLinkName");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);
    }

    private String buildListViewData(String strSql) throws YssException {
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

                setVchBookSet(rs);
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
            throw new YssException("获取套账信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sql = "";
        sql =
            "select a.FLinkCode,a.FLinkName,a.FDesc,a.FCheckState,a.FCreator,a.FCreateTime,a.FCheckUser,a.FCheckTime,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Vch_PortSetLink") + " a"
            + " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode"
            + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode"
            + this.buildFilterSql() +
            " group by a.FLinkCode,a.FLinkName,a.FDesc,a.FCheckState,a.FCreator,a.FCreateTime,a.FCheckUser,a.FCheckTime,b.FUserName,c.FUserName" +
            " order by a.FCheckState, a.FCreateTime desc";

//      sql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FPortName as FPortName,e.FBookSetName as FBookSetName from " +
//            pub.yssGetTableName("Tb_Vch_PortSetLink") + " a"
//            + " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode"
//            + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode"
//            + " left join (select FPortCode,FPortName from " +
//            pub.yssGetTableName("Tb_Para_Portfolio") +
//            ") d on a.FPortCode=d.FPortCode"
//            + " left join (select FBookSetCode,FBookSetName from " +
//            pub.yssGetTableName("Tb_Vch_BookSet") +
//            ") e on a.FBookSetCode=e.FBookSetCode"
//            + this.buildFilterSql() + " order by FCheckState,FCreateTime desc";
        return this.buildListViewData(sql);
    }

    /**
     * buildFilterSql
     *
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.linkCode.length() != 0) {
                sResult = sResult + " and a.FLinkCode like '" +
                    filterType.linkCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.linkName.length() != 0) {
                sResult = sResult + " and a.FLinkName like '" +
                    filterType.linkName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '%" + // wdy 修改为模糊查询:like '%XXX%'
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        Connection conn = null;
        String sqlStr = "", sHeader = "", sShowDataStr = "", sAllDataStr = "";
        StringBuffer sData = new StringBuffer();
        StringBuffer sAllData = new StringBuffer();
        ResultSet rs = null;
        try {
            conn = dbl.loadConnection();
            sHeader = "链接代码\t链接名称\t描述";
            sqlStr =
                "select a.FLinkCode,a.FLinkName,a.FDesc,a.FCheckState,a.FCreator,a.FCreateTime,a.FCheckUser,a.FCheckTime,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Vch_PortSetLink") + " a"
                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode"
                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode"
                + " where FcheckState=1" +
                " group by a.FLinkCode,a.FLinkName,a.FDesc,a.FCheckState,a.FCreator,a.FCreateTime,a.FCheckUser,a.FCheckTime,b.FUserName,c.FUserName" +
                " order by a.FCheckState, a.FCreateTime desc";

            conn.setAutoCommit(false);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                sData.append(rs.getString("FLinkCode")).append("\t");
                sData.append(rs.getString("FLinkName")).append("\t");
                sData.append(rs.getString("FDesc")).append(YssCons.YSS_LINESPLITMARK);
                setVchBookSet(rs);
                sAllData.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (sData.toString().length() > 2) {
                sShowDataStr = sData.toString().substring(0,
                    sData.toString().length() - 2);
            }

            if (sAllData.toString().length() > 2) {
                sAllDataStr = sAllData.toString().substring(0,
                    sAllData.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

        } catch (Exception e) {
            throw new YssException("获取组合链接出错", e);
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
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        String sql = "";
        ResultSet rs = null;
        try {
            strName = "组合套账";
            sHeader = "组合代码\t组合名称\t套账代码\t套账名称";

            sql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FPortName as FPortName,e.FBookSetName as FBookSetName from (select * from " +
                pub.yssGetTableName("Tb_Vch_PortSetLink") +
                //" where FCheckState <> 2 and FLinkCode=" +
                " where  FLinkCode=" + //将删除的数据也显示到前台 by leeyu 2008-10-27 BUG:0000491
                dbl.sqlString(this.oldLinkCode) + //Modify by Mao Qiwen 20080724  bugNo.:0000279
                " and FPortCode not like 'null' and FBookSetCode not like 'null'" +
                ") a"
                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode"
                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode"
                + " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                ") d on a.FPortCode=d.FPortCode"//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
                + " left join (select trim(to_char(FSetCode,'000')) as FBookSetCode,FSetName as FBookSetName from  lsetlist) e on a.FBookSetCode=e.FBookSetCode"
                + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FPortCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FPortName") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FBookSetCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FBookSetName") + "").trim()).append(
                    YssCons.YSS_LINESPLITMARK);
                VchPortBookBean portBook = new VchPortBookBean();
                portBook.setYssPub(pub);
                portBook.setPortBookAttr(rs);
                bufAll.append(portBook.buildRowStr()).append(YssCons.
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
        } catch (Exception ex) {
            throw new YssException("获取可用" + strName + "信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Vch_PortSetLink"), "FLinkCode", this.linkCode, this.oldLinkCode);
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        Connection con = dbl.loadConnection();
        PreparedStatement pstmt = null;
        try {
            // strSql = "delete from "+pub.yssGetTableName("Tb_Vch_PortSetLink")+
            // " where FPortCode='null' and FBookSetCode='null'";
            // dbl.executeSql(strSql);
            strSql = "insert into " +
                pub.yssGetTableName("Tb_Vch_PortSetLink") +
                " (FLinkCode,FLinkName,FPortCode,FBookSetCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(strSql);

            for (int i = 0; i < this.hmPortBook.size(); i++) {
                if ( ( (VchPortBookBean)this.hmPortBook.get(String.valueOf(i))).getPortCode().trim().length() != 0) {
                    portCode = ( (VchPortBookBean)this.hmPortBook.get(String.valueOf(i))).getPortCode();
                }
                if ( ( (VchPortBookBean)this.hmPortBook.get(String.valueOf(i))).getBookSetCode().trim().length() != 0) {
                    bookSetCode = ( (VchPortBookBean)this.hmPortBook.get(String.valueOf(i))).getBookSetCode();
                }
                pstmt.setString(1, this.linkCode);
                pstmt.setString(2, this.linkName);
                pstmt.setString(3, portCode);
                pstmt.setString(4, bookSetCode);
                pstmt.setString(5, this.desc);
                pstmt.setInt(6, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(7, this.creatorCode);
                pstmt.setString(8, this.creatorTime);
                pstmt.setString(9, (pub.getSysCheckState() ? " " : this.creatorCode));
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new YssException("增加组合套账链接出错!");
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
        return "";
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String sql = "";
        try {
            //    con.setAutoCommit(false);
            //    bTrans = true;
            sql = "delete from " + pub.yssGetTableName("Tb_Vch_PortSetLink") +
                " where FlinkCode=" + dbl.sqlString(this.oldLinkCode);
            dbl.executeSql(sql);
            this.addSetting();
            //    con.commit();
            //   bTrans = false;
            //    con.setAutoCommit(true);
        } catch (Exception es) {
        	throw new YssException("修改组合套账链接出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String sql = "";
        try {
            sql = "update " + pub.yssGetTableName("Tb_Vch_PortSetLink") +
                " set FCheckState=" + this.checkStateId + ",FCheckUser=" +
                dbl.sqlString(pub.getUserCode()) + ",FCheckTime=" +
                dbl.sqlString(this.checkTime) + " where FlinkCode=" +
                dbl.sqlString(this.linkCode);
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
        	throw new YssException("删除组合套账链接出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String sql = "";
        try {
            //====回收站处理 by leeyu 2008-10-21 BUG:0000491
            con.setAutoCommit(false);
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sql = "update " + pub.yssGetTableName("Tb_Vch_PortSetLink") +
                    " set FCheckState=" + this.checkStateId + ",FCheckUser=" +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime=" +
                    dbl.sqlString(this.checkTime) + " where FlinkCode=" +
                    dbl.sqlString(this.linkCode);
                dbl.executeSql(sql);
            }
            //con.setAutoCommit(false);
            bTrans = true;
            //dbl.executeSql(sql);
            //======2008-10-21
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
        	throw new YssException("审核组合套账链接出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection con = dbl.loadConnection();
        Statement st = null;
        boolean bTrans = false;
        String sql = "";
        try {
            con.setAutoCommit(false);
            bTrans = true;
            sql = "delete from " + pub.yssGetTableName("Tb_Vch_PortSetLink") +
                " where FlinkCode=" + dbl.sqlString(this.linkCode); //Modify by Mao Qiwen 20080724  bugNo.:0000279
            dbl.executeSql(sql);
            st = con.createStatement();
            for (int i = 0; i < this.hmPortBook.size(); i++) {
                sql = "insert into " +
                    pub.yssGetTableName("Tb_Vch_PortSetLink") +
                    " (FLinkCode,FLinkName,FPortCode,FBookSetCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values(" + dbl.sqlString(this.linkCode) + "," +
                    dbl.sqlString(this.linkName) + "," +
                    dbl.sqlString( ( (VchPortBookBean)this.hmPortBook.get(String.valueOf(i))).getPortCode()) + "," +
                    dbl.sqlString( ( (VchPortBookBean)this.hmPortBook.get(String.valueOf(i))).getBookSetCode()) + "," +
                    dbl.sqlString(this.desc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
                    ")";
                st.addBatch(sql);
            }
            st.executeBatch();
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception es) {
        	throw new YssException("操作组合套账链接出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";

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
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
		//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题        
		//VchPortBookBean book = new VchPortBookBean();
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.parsePortBookSubStr(sRowStr.split("\r\t")[2]);
                }
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            sRecycled = sTmpStr; //BUG:0000491 by leeyu 增加回收站处理
            this.linkCode = reqAry[0];
            if (reqAry[0].trim().length() == 0) {
                this.linkCode = ""; // wdy modify 去掉双引号中的空格
            }
            this.linkName = reqAry[1];
            if (reqAry[1].trim().length() == 0) {
                this.linkName = ""; // wdy modify 去掉双引号中的空格
            }
            this.portCode = reqAry[2];
            if (reqAry[2].trim().length() == 0) {
                this.portCode = "null";
            }
            this.bookSetCode = reqAry[3];
            if (reqAry[3].trim().length() == 0) {
                this.bookSetCode = "null";
            }
            this.desc = reqAry[4];
            super.checkStateId = Integer.parseInt(reqAry[5]);
            this.oldLinkCode = reqAry[6];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new VchPortSetLinkBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析组合套账链接信息出错!");
        }
    }

    private void parsePortBookSubStr(String strBookSets) throws YssException {
        String[] bookSetStr = strBookSets.split("\f\f");
        for (int i = 0; i < bookSetStr.length; i++) {
            VchPortBookBean portBook = new VchPortBookBean();
            portBook.setYssPub(pub);
            portBook.parseRowStr(bookSetStr[i]);
            hmPortBook.put(String.valueOf(i), portBook);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.linkCode).append("\t");
        buf.append(this.linkName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

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
    public String getBeforeEditData() {
        return "";
    }

    public String getPortCode() {
        return portCode;
    }

    public String getOldLinkCode() {
        return oldLinkCode;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getLinkCode() {
        return linkCode;
    }

    public HashMap getHmPortBook() {
        return hmPortBook;
    }

    public VchPortSetLinkBean getFilterType() {
        return filterType;
    }

    public String getDesc() {
        return desc;
    }

    public void setBookSetCode(String bookSetCode) {
        this.bookSetCode = bookSetCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setOldLinkCode(String oldLinkCode) {
        this.oldLinkCode = oldLinkCode;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public void setHmPortBook(HashMap hmPortBook) {
        this.hmPortBook = hmPortBook;
    }

    public void setFilterType(VchPortSetLinkBean filterType) {
        this.filterType = filterType;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBookSetCode() {
        return bookSetCode;
    }

    /**
     * deleteRecycleData 增加回收站处理功能 by leeyu 2008-10-21 BUG:0000491
     */
    public void deleteRecycleData() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String sql = "";
        try {
            con.setAutoCommit(false);
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sql = "delete " + pub.yssGetTableName("Tb_Vch_PortSetLink") +
                    " where FlinkCode=" + dbl.sqlString(this.linkCode);
                dbl.executeSql(sql);
            }
            bTrans = true;
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
        	throw new YssException("清除组合套账链接出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
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
    
    /**
     * 通过组合代码获取套账
     * panjunfang add 20100608 
     * modify by huangqirong 2013-04-24 bug #7486 调整组合套帐链接相关代码
     * MS01189 QDV4赢时胜（深圳）2010年5月18日01_B 
     * @param strPortCode
     * @return
     * @throws YssException
     */
    public String getSet(String strPortCode) throws YssException {
        String sResult = "";
        //String strSql = "";
        //ResultSet rs = null;
        YssFinance finace = new YssFinance(); 
        try{
        	finace.setYssPub(this.pub);
			String tmpSetId = finace.getBookSetId(pub.getAssetGroupCode() , portCode);
			if(tmpSetId != null && tmpSetId.trim().length() > 0 )
				sResult = tmpSetId;
        	/*
        	strSql = "Select FBookSetCode From " + pub.yssGetTableName("Tb_Vch_PortSetLink") +
        			" Where FCheckState = 1 And FPortCode = " + dbl.sqlString(strPortCode);
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		sResult = rs.getString("FBookSetCode");
        	}*/
        }catch (Exception e) {
        	throw new YssException("通过组合套帐链接获取组合对应的套账出错！",e);
        }finally{
        	//dbl.closeResultSetFinal(rs);
        }
    	return sResult;
    }
}
