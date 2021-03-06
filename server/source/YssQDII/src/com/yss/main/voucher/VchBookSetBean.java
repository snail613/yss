package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class VchBookSetBean
    extends BaseDataSettingBean implements IDataSetting {
    private String bookSetCode = "";
    private String bookSetName = "";
    private String curyCode = "";
    private String curyName = "";
    private String desc = "";
    private String oldBookSetCode = "";
    private VchBookSetBean filterType = null;
    private String sRecycled = ""; //增加对回收站的处理功能  by leeyu 2008-10-21 BUG:0000491

    String[] allReqAry = null;
    String[] oneReqAry = null;

    public String getBookSetCode() {
        return this.bookSetCode;
    }

    public String getBookSetName() {
        return this.bookSetName;
    }

    public VchBookSetBean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sql = "";
        sql = "select a.*,b.FUserName as FCreatorName," +
            " c.FUserName as FCheckUserName,d.FCuryName as FCuryName from " +
            pub.yssGetTableName("Tb_Vch_BookSet") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode" +
            " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ") d on a.FCuryCode=d.FCuryCode" +
            this.buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
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
            if (this.filterType.bookSetCode.length() != 0) {
                sResult = sResult + " and a.FBookSetCode like '" +
                    filterType.bookSetCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.bookSetName.length() != 0) {
                sResult = sResult + " and a.FBookSetName like '" +
                    filterType.bookSetName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.curyCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '" + // wdy modify 使用模糊查询
                    filterType.curyCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '%" + // wdy modify 使用模糊查询并把模糊查询修改为:like '%XXX%'
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
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
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取套账信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setVchBookSet(ResultSet rs) throws SQLException {
        this.bookSetCode = rs.getString("FBookSetCode");
        this.bookSetName = rs.getString("FBookSetName");
        this.curyCode = rs.getString("FCuryCode");
        this.curyName = rs.getString("FCuryName");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);
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
        //String strSql = "";
        StringBuffer queryBuf = new StringBuffer();
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "套账代码\t套账名称";
            queryBuf.append(" select a.*,b.FUserName as FCreatorName,");
        	queryBuf.append(" c.FUserName as FCheckUserName,d.FCuryName as FCuryName from ");
            if(YssCons.YSS_VCH_BUILDER_MODE.equalsIgnoreCase("batch")){
            	queryBuf.append("(select a1.*,a2.* from ");
            	//edit by songjie 2012.09.12 BUG 5556 QDV4赢时胜(测试)2012年09月06日01_B 将套帐号 由 数字格式 转为 000 字符串格式
            	queryBuf.append(" (select  fsetid, TRIM(' ' from to_char(fsetcode,'000')) as FBookSetCode,fsetname as FBookSetName from lsetlist where fyear= (select max(fyear) from lsetlist))a1");
            	queryBuf.append(" join ");
            	queryBuf.append(" (select fassetcode,fportcury as FCuryCode,fdesc,fcheckstate,fcreator,fcreatetime,fcheckuser,fchecktime from ").append(pub.yssGetTableName("tb_para_portfolio")).append(" where fcheckstate=1)a2");
                queryBuf.append(" on a1.fsetid = a2.fassetcode)a ");
            }else{
            	queryBuf.append(pub.yssGetTableName("Tb_Vch_BookSet")).append(" a ");
            }
            
            queryBuf.append(" left join (select FUserCode,FUserName from Tb_Sys_UserList)b on a.FCreator = b.FUserCode ");
            queryBuf.append(" left join (select FUserCode,FUserName from Tb_Sys_UserList)c on a.FCreator = c.FUserCode ");
            queryBuf.append(" left join (select FCuryCode,FCuryName from ").append(pub.yssGetTableName("Tb_Para_Currency")).append(") d on a.FCuryCode=d.FCuryCode");
            queryBuf.append(" where a.FCheckState =1 order by a.FCheckState, a.FCreateTime desc ");
            
//                " select a.*,b.FUserName as FCreatorName," +
//                " c.FUserName as FCheckUserName,d.FCuryName as FCuryName " +
//                " from " + pub.yssGetTableName("Tb_Vch_BookSet") + " a " +
//                " left join (select FUserCode,FUserName from Tb_Sys_UserList)b on a.FCreator = b.FUserCode " +
//                " left join (select FUserCode,FUserName from Tb_Sys_UserList)c on a.FCheckUser = c.FUserCode" +
//                " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ") d on a.FCuryCode=d.FCuryCode" +
//                " where a.FCheckState =1" +
//                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(queryBuf.toString());
            while (rs.next()) {
                bufShow.append( (rs.getString("FBookSetCode") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FBookSetName") + "").trim()).append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.setVchBookSet(rs);
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
            throw new YssException("获取可用套账信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**shashijie 2011.03.31 STORY #417 当套帐设置被反审核时，如果存在引用系统给出 */
    public String getListViewData3() throws YssException{
    	String strValue = "";
    	String sql = "";
    	ResultSet rs = null;
    	//ResultSet rse = null;
    	//组合套帐链接设置
        sql = "SELECT l.FSetCode FROM " + pub.yssGetTableName("Tb_Para_Portfolio") + " p join lsetlist l on p.fassetcode = " +
        		" l.fsetid WHERE FSetCode = "+ dbl.sqlString(this.bookSetCode) 
        	+"  AND FCheckState = 1";
        try {
        	rs = dbl.openResultSet(sql);
            while (rs.next()) {
            	strValue += "组合套帐链接"+rs.getString("FSetCode")+"\r\n";//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
            	//凭证模板设置,维护部(周述晟)讨论得出,只需取消“组合套帐链接”的引用，不需要取消凭证模板的引用。(赢时胜(SH)QDII项目组,周述晟 说: (2011-05-04 15:56:00))
            	/**String sqlString = "SELECT FVchTplCode FROM " + pub.yssGetTableName("TB_VCH_VCHTPL") + " WHERE FLinkCode = "
            					+ dbl.sqlString(rs.getString("FLinkCode")) + " AND FCheckState = 1";
            	rse = dbl.openResultSet(sqlString);
            	while (rse.next()) {
            		strValue += "凭证模板设置" + rse.getString("FVchTplCode") + "\r\n";
				}
            	dbl.closeResultSetFinal(rse);*/
            }
		} catch (Exception e) {
			throw new YssException();
		}
        return strValue;
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String sql = "";
        try {
            sql = "insert into " + pub.yssGetTableName("Tb_Vch_BookSet") +
                " (FBookSetCode,FBookSetName,FCuryCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.bookSetCode) + "," +
                dbl.sqlString(this.bookSetName) + "," +
                dbl.sqlString(this.curyCode) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Vch_BookSet"), "FBookSetCode", this.bookSetCode, this.oldBookSetCode);
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String sql = "";
        try {
            //======增加对回收站的处理功能 by leeyu 2008-10-21 BUG:0000491
            con.setAutoCommit(bTrans);
            bTrans = true;
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sql = "update " + pub.yssGetTableName("Tb_Vch_BookSet") +
                    " set FCheckState=" + this.checkStateId + ",FCheckUser=" +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime=" +
                    dbl.sqlString(this.checkTime) + " where FBookSetCode=" +
                    dbl.sqlString(this.bookSetCode);
                dbl.executeSql(sql);
            }
//         con.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(sql);
            //===============2008-10-21
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
            new YssException("审核套账信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
    }


	/**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String sql = "";
        try {
            sql = "update " + pub.yssGetTableName("Tb_Vch_BookSet") +
                " set FCheckState=" + this.checkStateId + ",FCheckUser=" +
                dbl.sqlString(pub.getUserCode()) + ",FCheckTime=" +
                dbl.sqlString(this.checkTime) + " where FBookSetCode=" + dbl.sqlString(this.bookSetCode);
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
            new YssException("删除套账信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
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
            sql = "update " + pub.yssGetTableName("Tb_Vch_BookSet") +
                " set FBookSetCode=" + dbl.sqlString(this.bookSetCode) +
                ",FBookSetName=" + dbl.sqlString(this.bookSetName) +
                ",FCuryCode=" + dbl.sqlString(this.curyCode) + ",FDesc=" +
                dbl.sqlString(this.desc) + ",FCheckstate= " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FBookSetCode = " + dbl.sqlString(this.oldBookSetCode);
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
            new YssException("修改套账信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.bookSetCode).append("\t");
        buf.append(this.bookSetName).append("\t");
        buf.append(this.curyCode).append("\t");
        buf.append(this.curyName).append("\t");
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
            if (sRowStr.indexOf("\r\f") >= 0) {
                allReqAry = sRowStr.split("\r\f");
                this.bookSetCode = allReqAry[0].split("\t")[0];
                this.bookSetName = allReqAry[0].split("\t")[1];
            } else {
                sTmpStr = sRowStr;
                reqAry = sTmpStr.split("\t");
                sRecycled = sTmpStr; //BUG:0000491 增加对回收站的处理功能 by leeyu 2008-10-21
                this.bookSetCode = reqAry[0];
                this.bookSetName = reqAry[1];
                this.curyCode = reqAry[2];
                // modify by fangjiang 2010.11.22 BUG #378 套帐设置界面问题
                if (reqAry[3] != null ){
                	if (reqAry[3].indexOf("【Enter】") >= 0){
                		this.desc = reqAry[3].replaceAll("【Enter】", "\r\n");
                	}
                	else{
                		this.desc = reqAry[3];
                	}
                }
                //------------------
                this.checkStateId = Integer.parseInt(reqAry[4]);
                this.oldBookSetCode = reqAry[5];
                super.parseRecLog();
                if (sRowStr.indexOf("\r\t") >= 0) {
                    if (this.filterType == null) {
                        this.filterType = new VchBookSetBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析套账信息出错!");
        }
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
     * deleteRecycleData 完善回收站的处理功能 by leeyu 2008-10-21 BUG:0000491
     */
    public void deleteRecycleData() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String sql = "";
        try {
            con.setAutoCommit(bTrans);
            bTrans = true;
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sql = "delete " + pub.yssGetTableName("Tb_Vch_BookSet") +
                    " where FBookSetCode=" + dbl.sqlString(this.bookSetCode);
                dbl.executeSql(sql);
            }
            con.commit();
            con.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            new YssException("清除凭证套账信息出错!", ex);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

    }

    public String getListViewGroupData1() throws YssException {
    	 String sHeader = "";
         String sShowDataStr = "";
         String sAllDataStr = "";
         //String strSql = "";
         StringBuffer queryBuf = new StringBuffer();
         ResultSet rs = null;
         StringBuffer bufShow = new StringBuffer();
         StringBuffer bufAll = new StringBuffer();
         try {
             sHeader = "套账代码\t套账名称";
             queryBuf.append(" select a.*,b.FUserName as FCreatorName,");
         	queryBuf.append(" c.FUserName as FCheckUserName,d.FCuryName as FCuryName from ");
             if(YssCons.YSS_VCH_BUILDER_MODE.equalsIgnoreCase("batch")){
             	queryBuf.append("(select a1.*,a2.* from ");
             	//edit by songjie 2012.09.12 BUG 5556 QDV4赢时胜(测试)2012年09月06日01_B 将套帐号 由 数字格式 转为 000 字符串格式
             	queryBuf.append(" (select  fsetid, TRIM(' ' from to_char(fsetcode,'000')) as FBookSetCode,fsetname as FBookSetName,max(Fyear) from lsetlist group by fsetid, fsetcode, fsetname )a1");
             	queryBuf.append(" join ");
             	queryBuf.append(" (select fassetcode,fportcury as FCuryCode,fdesc,fcheckstate,fcreator,fcreatetime,fcheckuser,fchecktime from ").append(pub.yssGetTableName("tb_para_portfolio")).append(" where fcheckstate=1)a2");
                 queryBuf.append(" on a1.fsetid = a2.fassetcode)a ");
             }else{
             	queryBuf.append(pub.yssGetTableName("Tb_Vch_BookSet")).append(" a ");
             }
             
             queryBuf.append(" left join (select FUserCode,FUserName from Tb_Sys_UserList)b on a.FCreator = b.FUserCode ");
             queryBuf.append(" left join (select FUserCode,FUserName from Tb_Sys_UserList)c on a.FCreator = c.FUserCode ");
             queryBuf.append(" left join (select FCuryCode,FCuryName from ").append(pub.yssGetTableName("Tb_Para_Currency")).append(") d on a.FCuryCode=d.FCuryCode");
             queryBuf.append(" where a.FCheckState =1 order by a.FCheckState, a.FCreateTime desc ");
             
//                 " select a.*,b.FUserName as FCreatorName," +
//                 " c.FUserName as FCheckUserName,d.FCuryName as FCuryName " +
//                 " from " + pub.yssGetTableName("Tb_Vch_BookSet") + " a " +
//                 " left join (select FUserCode,FUserName from Tb_Sys_UserList)b on a.FCreator = b.FUserCode " +
//                 " left join (select FUserCode,FUserName from Tb_Sys_UserList)c on a.FCheckUser = c.FUserCode" +
//                 " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ") d on a.FCuryCode=d.FCuryCode" +
//                 " where a.FCheckState =1" +
//                 " order by a.FCheckState, a.FCreateTime desc";
             rs = dbl.openResultSet(queryBuf.toString());
             while (rs.next()) {
                 bufShow.append( (rs.getString("FBookSetCode") + "").trim()).append("\t");
                 bufShow.append( (rs.getString("FBookSetName") + "").trim()).append("\t");
                 bufShow.append(YssCons.YSS_LINESPLITMARK);
                 this.setVchBookSet(rs);
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
             throw new YssException("获取可用套账信息出错！");
         } finally {
             dbl.closeResultSetFinal(rs);
         }
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

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }
    
}
