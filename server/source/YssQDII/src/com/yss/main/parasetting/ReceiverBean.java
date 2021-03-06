package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class ReceiverBean
    extends BaseDataSettingBean implements IDataSetting {
    public ReceiverBean() {
        ///收款人设置
    }

    private String receiverCode = "";
    private String receiverName = "";
    private String receiverShortName = "";
    private String title = ""; //划款指令抬头名称 2008-11-20 linjunyun bug:MS00018
    private String officeAddr = "";
    private String postalCode = "";
    private String operBank = "";
    private String accountNumber = "";
    private String sCuryCode = "";
    private String sCuryName = "";
    private String desc = "";
    private String sPortCode = "";
    private String sPortName = "";
    private String sCashAccCode = "";
    private String sCashAccName = "";
    private String sAnalysisCode1 = "";
    private String sAnalysisName1 = "";
    private String sAnalysisCode2 = "";
    private String sAnalysisName2 = "";
    private String sAnalysisCode3 = "";
    private String sAnalysisName3 = "";
    private String oldReceiverCode = "";
    private ReceiverBean filterType = null;
    private String sRecycled = "";

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Receiver"),
                               "FReceiverCode", this.receiverCode, this.oldReceiverCode);
    }

    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            sqlStr = "insert into " + pub.yssGetTableName("Tb_Para_Receiver") +
                " (FReceiverCode,FReceiverName,FReceiverShortName,FTitle,FOfficeAddr,FPostalCode," +
                "FOperBank,FAccountNumber,FPortCode,FCashAccCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCuryCode) values(" +
                dbl.sqlString(this.receiverCode) + "," +
                dbl.sqlString(this.receiverName) + "," +
                dbl.sqlString(this.receiverShortName) + "," +
                dbl.sqlString(this.title) + "," + //存入划款指令抬头名称 2008-11-20 linjunyun bug:MS00018
                dbl.sqlString(this.officeAddr) + "," +
                dbl.sqlString(this.postalCode) + "," +
                dbl.sqlString(this.operBank) + "," +
                dbl.sqlString(this.accountNumber) + "," +
                dbl.sqlString(this.sPortCode) + "," +
                dbl.sqlString(this.sCashAccCode) + "," +
                dbl.sqlString(this.sAnalysisCode1) + "," +
                dbl.sqlString(this.sAnalysisCode2) + "," +
                dbl.sqlString(this.sAnalysisCode3) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + "," +
                dbl.sqlString(this.sCuryCode) +
                ")";
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加收款人设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String editSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            sqlStr = "update " + pub.yssGetTableName("Tb_Para_Receiver") +
                " set FReceiverCode=" + dbl.sqlString(this.receiverCode) + "," +
                "FReceiverName=" + dbl.sqlString(this.receiverName) + "," +
                "FReceiverShortName=" + dbl.sqlString(this.receiverShortName) + "," +
                "FTitle=" + dbl.sqlString(this.title) + "," + //更新划款指令抬头名称 2008-11-20 linjunyun bug:MS00018
                "FOfficeAddr=" + dbl.sqlString(this.officeAddr) + "," +
                "FPostalCode=" + dbl.sqlString(this.postalCode) + "," +
                "FOperBank=" + dbl.sqlString(this.operBank) + "," +
                "FAccountNumber=" + dbl.sqlString(this.accountNumber) + "," +
                "FPortCode=" + dbl.sqlString(this.sPortCode) + "," +
                "FCashAccCode=" + dbl.sqlString(this.sCashAccCode) + "," +
                "FAnalysisCode1=" + dbl.sqlString(this.sAnalysisCode1) + "," +
                "FAnalysisCode2=" + dbl.sqlString(this.sAnalysisCode2) + "," +
                "FAnalysisCode3=" + dbl.sqlString(this.sAnalysisCode3) + "," +
                "FDesc=" + dbl.sqlString(this.desc) + "," +
                "FCuryCode=" + dbl.sqlString(this.sCuryCode) + "," +
                "FCheckState=" + (pub.getSysCheckState() ? "0" : "1") + "," +
                "FCreator=" + dbl.sqlString(this.creatorCode) + "," +
                "FCreateTime=" + dbl.sqlString(this.creatorTime) + "," +
                "FCheckUser=" + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FReceiverCode =" + dbl.sqlString(this.oldReceiverCode);
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改收款人设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public void delSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            sqlStr = "update " + pub.yssGetTableName("Tb_Para_Receiver") +
                " set FCheckState=" + this.checkStateId + "," +
                " FCheckUser=" + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " ,FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FReceiverCode =" + dbl.sqlString(this.oldReceiverCode);
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除收款人设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理审核和未审核的单条信息。
     *  新方法功能：可以处理审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */
    public void checkSetting() throws YssException {
        //下面是被修改的原代码
//      Connection conn =null;
//      boolean bTrans =false;
//      String sqlStr ="";
//      try{
//         conn =dbl.loadConnection();
//         conn.setAutoCommit(false);
//         bTrans = true;
//         sqlStr ="update "+pub.yssGetTableName("Tb_Para_Receiver")+
//               " set FCheckState="+this.checkStateId + "," +
//               " FCheckUser="+(pub.getSysCheckState() ? "' '" :dbl.sqlString(this.creatorCode))+
//               " ,FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//               "' where FReceiverCode ="+dbl.sqlString(this.oldReceiverCode);
//         dbl.executeSql(sqlStr);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }catch(Exception e){
//         throw new YssException("审核收款人设置出错",e);
//      }finally{
//         dbl.endTransFinal(conn,bTrans);
//      }
        //修改好的代码
        Connection conn = null;
        String[] arrData = null;
        boolean bTrans = false;
        PreparedStatement stm = null;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            sqlStr = "update " + pub.yssGetTableName("Tb_Para_Receiver") +
                " set FCheckState=" + this.checkStateId + "," +
                " FCheckUser=" + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " ,FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FReceiverCode =?";
            //把sql语句付给PreparedStatement
            stm = dbl.openPreparedStatement(sqlStr);
            //循环执行这些更新语句
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.receiverCode);
                stm.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核收款人设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        Connection conn = null;
        ResultSet rs = null;
        String strSql = "";
        try {
            //conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            strSql = " select * from " + pub.yssGetTableName("Tb_Para_Receiver") +
                " where FReceiverCode =" + dbl.sqlString(this.oldReceiverCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.receiverCode = rs.getString("FReceiverCode");
                this.receiverName = rs.getString("FReceiverName");
                this.receiverShortName = rs.getString("FReceiverShortName");
                this.title = rs.getString("FTitle"); //获取划款指令抬头名称 2008-11-20 linjunyun bug:MS00018
                this.officeAddr = rs.getString("FOfficeAddr");
                this.postalCode = rs.getString("FPostalCode");
                this.operBank = rs.getString("FOperBank");
                this.accountNumber = rs.getString("FAccountNumber");
                this.sPortCode = rs.getString("FPortcode");
                this.sCashAccCode = rs.getString("FCashAccCode");
                this.sAnalysisCode1 = rs.getString("FAnalysisCode1");
                this.sAnalysisCode2 = rs.getString("FAnalysisCode2");
                this.sAnalysisCode3 = rs.getString("FAnalysisCode3");
                this.desc = rs.getString("FDesc");
                this.sCuryCode = rs.getString("FCuryCode");
            }
        } catch (Exception e) {
            throw new YssException("获取收款人代码出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
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

                setAttr(rs);
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
            throw new YssException("获取收款人设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData1() throws YssException {
        String strSql = "";
        //---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
        String AnalysisNameSql = "";
        String AnalysisNames = "";
        String[] AnalysisInfo = null;
        //---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
        try {
        	//---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
        	AnalysisInfo = getCashStorageAnalysisSql().split("\t\t");
        	
        	if(AnalysisInfo.length >=2){
        		AnalysisNameSql = AnalysisInfo[0];
        		AnalysisNames = AnalysisInfo[1];
        	}
        	//---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
        	
            strSql =
                "select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FCuryName as FCuryName,e.FPortName as FPortName,f.FCashAccName as FCashAccName, " +
                //edit by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
                (AnalysisNames.length() == 0 ? " ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " : AnalysisNames) +
                //delete by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
//                " h.FAnalysisName AS FAnalysisName1, i.FAnalysisName AS FAnalysisName2, j.FAnalysisName AS FAnalysisName3 ") +
                " from "
                + pub.yssGetTableName("tb_para_receiver") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " ) d " +
                " on a.FCuryCode=d.FCuryCode " +
                " left join (select FPortCode,FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") + " ) e " +
                " on a.FPortCode = e.FPortCode " + " left join (select FCashAccCode,FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") + " ) f " +
                " on a.FCashAccCode = f.FCashAccCode " +
                //add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
                (AnalysisNameSql.length() == 0 ? "" : AnalysisNameSql) +
                //---delete by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
//                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) h on a.FAnalysisCode1 = h.FAnalysisCode" +
//                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) i on a.FAnalysisCode2 = i.FAnalysisCode" +
//                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) j on a.FAnalysisCode3 = j.FAnalysisCode" +
                //---delete by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime";
            return this.builderListViewData(strSql);
        } catch (Exception e) {
            throw new YssException(e.toString());
        }
    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        //---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
        String AnalysisNameSql = "";
        String AnalysisNames = "";
        String[] AnalysisInfo = null;
        //---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
        try {
        	//---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
        	AnalysisInfo = getCashStorageAnalysisSql().split("\t\t");
        	
        	if(AnalysisInfo.length >=2){
        		AnalysisNameSql = AnalysisInfo[0];
        		AnalysisNames = AnalysisInfo[1];
        	}
        	//---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
        	
            sHeader = "付款人收款人代码\t付人款收款人名称";
            strSql =
                "select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FCuryName as FCuryName,e.FPortName as FPortName,f.FCashAccName as FCashAccName, " +
                //edit by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
                (AnalysisNames.length() == 0 ? " ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " : AnalysisNames) +
                //delete by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
//                 " h.FAnalysisName AS FAnalysisName1, i.FAnalysisName AS FAnalysisName2, j.FAnalysisName AS FAnalysisName3 ") +
                " from " + " ( select * from "
                + pub.yssGetTableName("tb_para_receiver") + " where FCheckState=1) a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " ) d " +
                " on a.FCuryCode=d.FCuryCode " +
                " left join (select FPortCode,FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") + " ) e " +
                " on a.FPortCode = e.FPortCode " +
                " left join (select FCashAccCode,FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") + " ) f " +
                " on a.FCashAccCode = f.FCashAccCode " +
                //add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
                (AnalysisNameSql.length() == 0 ? "" : AnalysisNameSql) +
                //---delete by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
//                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) h on a.FAnalysisCode1 = h.FAnalysisCode" +
//                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) i on a.FAnalysisCode2 = i.FAnalysisCode" +
//                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) j on a.FAnalysisCode3 = j.FAnalysisCode" +
                //---delete by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FReceiverCode")).append("\t").append(rs.getString("FReceiverName"))
                    .append(YssCons.YSS_LINESPLITMARK);
                setAttr(rs);
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
            throw new YssException("获取收款人设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * edit by songjie 
     * 2011.05.13
     * 需求 759
     * QDV4工银2011年3月7日05_A
     * 获取已审核的付款人数据
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        //---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
        String AnalysisNameSql = "";
        String AnalysisNames = "";
        String[] AnalysisInfo = null;
        //---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
        try {
        	//---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
        	AnalysisInfo = getCashStorageAnalysisSql().split("\t\t");
        	
        	if(AnalysisInfo.length >=2){
        		AnalysisNameSql = AnalysisInfo[0];
        		AnalysisNames = AnalysisInfo[1];
        	}
        	//---add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
        	
            sHeader = "付款人收款人代码\t付人款收款人名称";
            strSql =
                "select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FCuryName as FCuryName,e.FPortName as FPortName,f.FCashAccName as FCashAccName, " +
                //edit by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
                (AnalysisNames.length() == 0 ? " ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " : AnalysisNames) +
                //delete by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
//                 " h.FAnalysisName AS FAnalysisName1, i.FAnalysisName AS FAnalysisName2, j.FAnalysisName AS FAnalysisName3 ") +
                " from " + " ( select * from "
                + pub.yssGetTableName("tb_para_receiver") + " where FCheckState=1) a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " ) d " +
                " on a.FCuryCode=d.FCuryCode " +
                " left join (select FPortCode,FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") + " ) e " +
                " on a.FPortCode = e.FPortCode " +
                " left join (select FCashAccCode,FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") + " ) f " +
                " on a.FCashAccCode = f.FCashAccCode " +
                //add by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
                (AnalysisNameSql.length() == 0 ? "" : AnalysisNameSql) +
                //---delete by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
//                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) h on a.FAnalysisCode1 = h.FAnalysisCode" +
//                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) i on a.FAnalysisCode2 = i.FAnalysisCode" +
//                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) j on a.FAnalysisCode3 = j.FAnalysisCode" +
                //---delete by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FReceiverCode")).append("\t").append(rs.getString("FReceiverName"))
                    .append(YssCons.YSS_LINESPLITMARK);
                setAttr(rs);
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
            throw new YssException("获取收款人设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

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
            this.receiverCode = reqAry[0];
            this.receiverName = reqAry[1];
            this.receiverShortName = reqAry[2];
            //---edit by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
            if(reqAry[3].indexOf("【Enter】") != -1){
            	this.officeAddr = reqAry[3].replaceAll("【Enter】", "\r\n");
            }else{
            	this.officeAddr = reqAry[3];
            }
            //---edit by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
            this.postalCode = reqAry[4];
            this.operBank = reqAry[5];
            this.accountNumber = reqAry[6];
            //---edit by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
            if(reqAry[7].indexOf("【Enter】") != -1){
            	this.desc = reqAry[7].replaceAll("【Enter】", "\r\n");;
            }else{
            	this.desc = reqAry[7];
            }
            //---edit by songjie 2011.11.03 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
            this.checkStateId = Integer.parseInt(reqAry[8]);
            this.sCuryCode = reqAry[9];
            if (reqAry[9].trim().length() == 0) {
                this.sCuryCode = " ";
            }
            this.sPortCode = reqAry[10];
            if (reqAry[10].trim().length() == 0) {
                this.sPortCode = " ";
            }
            this.sCashAccCode = reqAry[11];
            if (reqAry[11].trim().length() == 0) {
                this.sCashAccCode = " ";
            }
            this.sAnalysisCode1 = reqAry[12];
            if (reqAry[12].trim().length() == 0) {
                this.sAnalysisCode1 = " ";
            }
            this.sAnalysisCode2 = reqAry[13];
            if (reqAry[13].trim().length() == 0) {
                this.sAnalysisCode2 = " ";
            }
            this.sAnalysisCode3 = reqAry[14];
            if (reqAry[14].trim().length() == 0) {
                this.sAnalysisCode3 = " ";
            }
            this.title = reqAry[15]; //赋值划款指令抬头名称 2008-11-20 linjunyun bug:MS00018
            this.oldReceiverCode = reqAry[16];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ReceiverBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析收款人设置出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.receiverCode).append("\t");
        buf.append(this.receiverName).append("\t");
        buf.append(this.receiverShortName).append("\t");
        buf.append(this.officeAddr).append("\t");
        buf.append(this.postalCode).append("\t");
        buf.append(this.operBank).append("\t");
        buf.append(this.accountNumber).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.sCuryCode).append("\t");
        buf.append(this.sCuryName).append("\t");
        buf.append(this.sPortCode).append("\t");
        buf.append(this.sPortName).append("\t");
        buf.append(this.sCashAccCode).append("\t");
        buf.append(this.sCashAccName).append("\t");
        buf.append(this.sAnalysisCode1).append("\t");
        buf.append(this.sAnalysisName1).append("\t");
        buf.append(this.sAnalysisCode2).append("\t");
        buf.append(this.sAnalysisName2).append("\t");
        buf.append(this.sAnalysisCode3).append("\t");
        buf.append(this.sAnalysisName3).append("\t");
        buf.append(this.title).append("\t"); //获取划款指令抬头名称 2008-11-20 linjunyun bug:MS00018
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    private void setAttr(ResultSet rs) throws SQLException, YssException {
        this.receiverCode = rs.getString("FReceiverCode");
        this.receiverName = rs.getString("FReceiverName");
        this.receiverShortName = rs.getString("FReceiverShortName");
        this.title = rs.getString("FTitle"); //赋值划款指令抬头名称 2008-11-20 linjunyun bug:MS00018
        this.officeAddr = rs.getString("FOfficeAddr");
        this.postalCode = rs.getString("FPostalCode");
        this.operBank = rs.getString("FOperBank");
        this.accountNumber = rs.getString("FAccountNumber");
        this.desc = rs.getString("FDesc");
        this.sCuryCode = rs.getString("FCuryCode");
        this.sCuryName = rs.getString("FCuryName");
        this.sPortCode = rs.getString("FPortCode");
        this.sPortName = rs.getString("FPortName");
        this.sCashAccCode = rs.getString("FCashAccCode");
        this.sCashAccName = rs.getString("FCashAccName");
        this.sAnalysisCode1 = rs.getString("FAnalysisCode1");
        this.sAnalysisName1 = rs.getString("FAnalysisName1");
        this.sAnalysisCode2 = rs.getString("FAnalysisCode2");
        this.sAnalysisName2 = rs.getString("FAnalysisName2");
        this.sAnalysisCode3 = rs.getString("FAnalysisCode3");
        this.sAnalysisName3 = rs.getString("FAnalysisName3");
        super.setRecLog(rs);
    }

    private String buildFilterSql() throws YssException {
        String sqlStr = "";
        try {
            if (this.filterType != null) {
                sqlStr = " where 1=1";
                if (this.filterType.receiverCode != null && this.filterType.receiverCode.trim().length() != 0) {
                    sqlStr += " and a.FReceiverCode like '" + this.filterType.receiverCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.receiverName != null && this.filterType.receiverName.trim().length() != 0) {
                    sqlStr += " and a.FReceiverName like '" + this.filterType.receiverName.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.receiverShortName != null && this.filterType.receiverShortName.trim().length() != 0) {
                    sqlStr += " and a.FReceiverShortName like '" + this.filterType.receiverShortName.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.officeAddr != null && this.filterType.officeAddr.trim().length() != 0) {
                    sqlStr += " and a.FOfficeAddr like '" + this.filterType.officeAddr.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.postalCode != null && this.filterType.postalCode.trim().length() != 0) {
                    sqlStr += " and a.FPostalCode like '" + this.filterType.postalCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.operBank != null && this.filterType.operBank.trim().length() != 0) {
                    sqlStr += " and a.FOperBank like '" + this.filterType.operBank.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.accountNumber != null && this.filterType.accountNumber.trim().length() != 0) {
                    sqlStr += " and a.FAccountNumber like '" + filterType.accountNumber.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.desc != null && this.filterType.desc.trim().length() != 0) {
                    sqlStr += " and a.FDesc like '" + filterType.desc.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.sCuryCode != null && this.filterType.sCuryCode.trim().length() != 0) {
                    sqlStr += " and a.FCuryCode like '" + filterType.sCuryCode.replaceAll("'", "''") + "%'";
                }
                //添加划款指令抬头名称筛选 2008-11-20 linjunyun bug:MS00018
                if (this.filterType.title != null && this.filterType.title.trim().length() != 0) {
                    sqlStr += " and a.FTitle like '" + this.filterType.title.replaceAll("'", "''") + "%'";
                }
            }
            return sqlStr;
        } catch (Exception e) {
            throw new YssException("筛选收款人设置出错", e);
        }
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String getReceiverShortName() {
        return receiverShortName;
    }

    public String getTitle() {
        return title;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverCode() {
        return receiverCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getOperBank() {
        return operBank;
    }

    public String getOldReceiverCode() {
        return oldReceiverCode;
    }

    public String getOfficeAddr() {
        return officeAddr;
    }

    public ReceiverBean getFilterType() {
        return filterType;
    }

    public String getDesc() {
        return desc;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setReceiverShortName(String receiverShortName) {
        this.receiverShortName = receiverShortName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setReceiverCode(String receiverCode) {
        this.receiverCode = receiverCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setOperBank(String operBank) {
        this.operBank = operBank;
    }

    public void setOldReceiverCode(String oldReceiverCode) {
        this.oldReceiverCode = oldReceiverCode;
    }

    public void setOfficeAddr(String officeAddr) {
        this.officeAddr = officeAddr;
    }

    public void setFilterType(ReceiverBean filterType) {
        this.filterType = filterType;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setSCuryCode(String sCuryCode) {
        this.sCuryCode = sCuryCode;
    }

    public void setSCuryName(String sCuryName) {
        this.sCuryName = sCuryName;
    }

    public void setSPortName(String sPortName) {
        this.sPortName = sPortName;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setSCashAccName(String sCashAccName) {
        this.sCashAccName = sCashAccName;
    }

    public void setSCashAccCode(String sCashAccCode) {
        this.sCashAccCode = sCashAccCode;
    }

    public void setSAnalysisName3(String sAnalysisName3) {
        this.sAnalysisName3 = sAnalysisName3;
    }

    public void setSAnalysisName2(String sAnalysisName2) {
        this.sAnalysisName2 = sAnalysisName2;
    }

    public void setSAnalysisName1(String sAnalysisName1) {
        this.sAnalysisName1 = sAnalysisName1;
    }

    public void setSAnalysisCode3(String sAnalysisCode3) {
        this.sAnalysisCode3 = sAnalysisCode3;
    }

    public void setSAnalysisCode2(String sAnalysisCode2) {
        this.sAnalysisCode2 = sAnalysisCode2;
    }

    public void setSAnalysisCode1(String sAnalysisCode1) {
        this.sAnalysisCode1 = sAnalysisCode1;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSCuryCode() {
        return sCuryCode;
    }

    public String getSCuryName() {
        return sCuryName;
    }

    public String getSPortName() {
        return sPortName;
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public String getSCashAccName() {
        return sCashAccName;
    }

    public String getSCashAccCode() {
        return sCashAccCode;
    }

    public String getSAnalysisName3() {
        return sAnalysisName3;
    }

    public String getSAnalysisName2() {
        return sAnalysisName2;
    }

    public String getSAnalysisName1() {
        return sAnalysisName1;
    }

    public String getSAnalysisCode3() {
        return sAnalysisCode3;
    }

    public String getSAnalysisCode2() {
        return sAnalysisCode2;
    }

    public String getSAnalysisCode1() {
        return sAnalysisCode1;
    }

    //获得分析代码
    private String getCashStorageAnalysisSql() throws YssException, SQLException {
        String sResult = "";
		//add by songjie 2011.11.04 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
        String columnName = "";
        String strSql = "";
        ResultSet rs = null;

        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
			//edit by songjie 2011.11.04 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B
            dbl.sqlString(YssOperCons.YSS_KCLX_Cash);

        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                    " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
					//---edit by songjie 2011.11.04 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B start---//
                    i + " from " + pub.yssGetTableName("tb_para_broker") +
                    " y where y.FCheckState = 1) broker on a.FAnalysisCode" +
                    i + " = broker.FBrokerCode";
                    columnName += " case when FAnalysisName" + i + " is null then ' ' else FAnalysisName" + i + " end as FAnalysisName" + i +",";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("003")) {
                    sResult = sResult + " left join (select FExchangeCode , FExchangeName as FAnalysisName" +
                        i + " from tb_base_exchange where FCheckState = 1 ) exchange on a.FAnalysisCode" +
                        i + " = exchange.FExchangeCode";
                    columnName += " case when FAnalysisName" + i + " is null then ' ' else FAnalysisName" + i + " end as FAnalysisName" + i +",";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                    i + " from " + pub.yssGetTableName("tb_para_investmanager") +
                    " n where n.FCheckState = 1 ) invmgr on a.FAnalysisCode" +
                    i + " = invmgr.FInvMgrCode ";
                    columnName += " case when FAnalysisName" + i + " is null then ' ' else FAnalysisName" + i + " end as FAnalysisName" + i +",";
                } else if(rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                        rs.getString("FAnalysisCode" + String.valueOf(i)).
                        equalsIgnoreCase("004")){
                	sResult = sResult + 
                	" left join (select fcatcode, fcatname as FAnalysisName" + i + 
                	" from tb_base_category where fcheckstate = 1 ) cat on a.FAnalysisCode" + 
                	i + " = cat.fcatcode";
                	columnName += " case when FAnalysisName" + i + " is null then ' ' else FAnalysisName" + i + " end as FAnalysisName" + i +",";
                } else {
                    sResult = sResult +
                        " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                        i + ".FAnalysisNull ";
                    //add by songjie 2012.03.20 BUG 4029 QDV4赢时胜(上海)2012年03月14日01_B
                    columnName += " ' ' as FAnalysisName" + i + ",";
                }
            }
        }

        if(columnName.trim().length() > 0){
        	columnName = columnName.substring(0, columnName.length() - 1);
        }
        
        dbl.closeResultSetFinal(rs);//QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-07-09 关闭结果集
        return sResult + "\t\t" + columnName;
		//---edit by songjie 2011.11.04 BUG 2340 QDV4赢时胜(测试)2011年8月1日1_B end---//
    }

    /**
     * 从回收站中彻底删除数据
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        PreparedStatement stm = null;
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //根据规定的符号，把多个sql语句分别放入数组
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Para_Receiver") +
                " where FReceiverCode = ?";
            //把sql语句付给PreparedStatement
            stm = dbl.openPreparedStatement(strSql);
            //循环执行这些删除语句
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.receiverCode);
                stm.executeUpdate();
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
