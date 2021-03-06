package com.yss.main.compliance;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title:组合指标关联设置</p>


 * @author not attributable
 * @version 1.0
 */
public class CompPortIndexLinkBean
    extends BaseDataSettingBean implements IDataSetting {
    private String sPortCode = "";
    private String sIndexCfgCode = "";
    private String sIndexCfgName = "";
    private String sCtlGrpCode = "";
    private String sCtlGrpName = "";
    private String sCtlCode = "";
    private String sCtlValue = "";
    private String sDesc = "";
    private String sIndexCfgCodes = "";

    private String sOldPortCode = "";
    private String sOldIndexCfgCode = "";
    private String sOldCtlGrpCode = "";
    private String sOldCtlCode = "";
    private CompPortIndexLinkBean filterType;
    public CompPortIndexLinkBean() {
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        /*String sqlStr="";
               String[] arrIndexCfg=null;
               Connection conn=null;
               PreparedStatement stm=null;
               boolean bTrans = false;
               try{
           conn = dbl.loadConnection();
           bTrans = true;
           conn.setAutoCommit(false);
           sqlStr="delete from "+pub.yssGetTableName("Tb_Comp_PortIndexLink") +
                 " where FPortCode="+dbl.sqlString(this.sPortCode);
           dbl.executeSql(sqlStr);
           sqlStr = "insert into " + pub.yssGetTableName("Tb_Comp_PortIndexLink") +
                 "(FPortCode,FIndexCfgCode,FCtlGrpCode,FCtlCode,FCtlValue,FDesc,FCheckState,FCreator,FCreateTime)"+
                 " values(?,?,?,?,?,?,?,?,?)";
           stm =dbl.openPreparedStatement(sqlStr);
           arrIndexCfg= this.sIndexCfgCodes.split(",");
           for(int i=0;i<arrIndexCfg.length;i++){
              if( arrIndexCfg[i].length()==0)
                 continue;
              stm.setString(1,this.sPortCode);
              stm.setString(2,arrIndexCfg[i]);
              stm.setString(3, " ");
              stm.setString(4, " ");
              stm.setString(5, " ");
              stm.setString(6, " ");
              stm.setInt(7, 0);
              stm.setString(8, this.creatorCode);
              stm.setString(9, this.creatorTime);
              stm.executeUpdate();
           }
           conn.commit();
           conn.setAutoCommit(true);
           bTrans = false;
               }catch(Exception e){
           throw new YssException("保存指标出错",e);
               }finally{
           dbl.endTransFinal(conn,bTrans);
               }*/
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {
    }

    public void checkSetting() throws YssException {
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        PreparedStatement stm = null;
        Connection conn = null;
        String sqlStr = "";
        String sReturn = "";
        String[] arrData = sMutilRowStr.split("\r\t");
        String[] arrCtlGrp = null, arrCtl;
        String sCtlCode = "", sValue = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            this.parseRowStr(arrData[0]);
            sqlStr = "delete from " + pub.yssGetTableName("Tb_Comp_PortIndexLink") +
                " where FPortCode=" + dbl.sqlString(this.sPortCode); //删除这个组合下的所有指标
            dbl.executeSql(sqlStr);
            sqlStr = "insert into " + pub.yssGetTableName("Tb_Comp_PortIndexLink") +
                "(FPortCode,FIndexCfgCode,FCtlGrpCode,FCtlCode,FCtlValue,FDesc,FCheckState," +
                "FCreator,FCreateTime) values(?,?,?,?,?,?,?,?,?)";
            stm = dbl.openPreparedStatement(sqlStr);
            if (arrData.length <= 2) {
                return "";
            }
            arrCtlGrp = arrData[2].split("\f\f");
            for (int i = 0; i < arrCtlGrp.length; i++) { //有多少组
                if (arrCtlGrp[i].trim().length() == 0) {
                    continue;
                }
                this.parseRowStr(arrCtlGrp[i]);
                arrCtl = this.sIndexCfgCodes.split("\n");
                for (int j = 0; j < arrCtl.length; j++) { //有多少个
                    if (arrCtl[j].trim().length() == 0) {
                        continue;
                    }
                    sCtlCode = arrCtl[j].split("\b")[0];
                    if (arrCtl[j].split("\b").length > 1) { //这个地方为\b
                        sValue = arrCtl[j].split("\b")[1];
                    }
                    stm.setString(1, this.sPortCode);
                    stm.setString(2, this.sIndexCfgCode);
                    stm.setString(3, this.sCtlGrpCode);
                    stm.setString(4, sCtlCode);
                    stm.setString(5, sValue);
                    stm.setString(6, this.sDesc);
                    stm.setInt(7, 1); //保存时即为审核
                    stm.setString(8, this.creatorCode);
                    stm.setString(9, this.creatorTime);
                    stm.executeUpdate();
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
            sReturn = "true";
        } catch (Exception e) {
            sReturn = "false";
            throw new YssException("保存阀值出错", e);
        } finally {
            dbl.endTransFinal(conn, false);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeStatementFinal(stm);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
        return sReturn;
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
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

    private void setPLRes(ResultSet rs) throws SQLException, YssException {
        this.sPortCode = rs.getString("FPortCode");
        this.sIndexCfgCode = rs.getString("FIndexCfgCode");
        this.sIndexCfgName = rs.getString("FIndexCfgName");
        this.sCtlGrpCode = rs.getString("FCtlGrpCode");
        this.sCtlGrpName = rs.getString("FCtlGrpName");
        this.sCtlCode = rs.getString("FCtlCode");
        this.sCtlValue = rs.getString("FCtlValue");
        this.sDesc = rs.getString("FDesc");
    }

    private String filterSql() throws YssException {
        String sqlStr = " where 1=1 ";
        if (filterType != null) {
            if (filterType.sPortCode != null && filterType.sPortCode.length() > 0) {
                sqlStr += " and a.FPortcode ='" +
                    filterType.sPortCode.replaceAll("'", "''") + "'";
            }
        }
        return sqlStr;
    }

    public String getListViewData1() throws YssException { //获取 组合下的 监控指标
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sqlStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;

        try {
            sHeader = this.getListView1Headers();
            sqlStr = "select distinct(a.FIndexCfgCode) as FIndexCfgCode,a.FPortcode,a.FCtlGrpCode,b.FIndexCfgName,c.FCtlGrpName from " +
                pub.yssGetTableName("Tb_Comp_PortIndexLink") + " a " +
                " left join (select FIndexCfgCode,FIndexCfgName from " + pub.yssGetTableName("Tb_Comp_IndexCfg") +
                " ) b on a.FIndexCfgCode = b.FIndexCfgCode " +
                " left join (select distinct(FCtlGrpCode),FCtlGrpName from Tb_PFSys_FaceCfgInfo ) c " +
                " on a.FCtlGrpCode = c.FCtlGrpCode " +
                filterSql() + " order by FIndexCfgCode ";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FIndexCfgCode")).append("\t");
                bufShow.append(rs.getString("FIndexCfgName")).append("\t");
                bufShow.append(rs.getString("FCtlGrpCode")).append(YssCons.YSS_LINESPLITMARK);
                this.sIndexCfgCode = rs.getString("FIndexCfgCode");
                this.sIndexCfgName = rs.getString("FIndexCfgName");
                this.sPortCode = rs.getString("FPortCode");
                this.sCtlGrpCode = rs.getString("FCtlGrpCode");
                this.sCtlGrpName = rs.getString("FCtlGrpName");
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
            throw new YssException("获取参数控件信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

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
            reqAry = sTmpStr.split("\t");
            this.sPortCode = reqAry[0];
            this.sIndexCfgCode = reqAry[1];
            this.sCtlGrpCode = reqAry[2];
            this.sCtlCode = reqAry[3];
            this.sCtlValue = reqAry[4];
            this.sDesc = reqAry[5];
            if (YssFun.isNumeric(reqAry[6])) {
                this.checkStateId = YssFun.toInt(reqAry[6]);
            }
            this.sOldPortCode = reqAry[7];
            this.sOldIndexCfgCode = reqAry[8];
            this.sOldCtlGrpCode = reqAry[9];
            this.sOldCtlCode = reqAry[10];
            this.sIndexCfgCodes = reqAry[11];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new CompPortIndexLinkBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析控件组链接设置出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(sPortCode).append("\t");
        buf.append(sIndexCfgCode).append("\t");
        buf.append(sIndexCfgName).append("\t");
        buf.append(sCtlGrpCode).append("\t");
        buf.append(sCtlGrpName).append("\t");
        buf.append(sCtlCode).append("\t");
        buf.append(sCtlValue).append("\t");
        buf.append(sDesc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        String sqlStr = "";
        int i = 0;
        StringBuffer buf = new StringBuffer();
        String sResult = "";
        ResultSet rs = null;
        try {
            //2009.04.29 蒋锦 修改 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
            //修改了 SQL 语句，获取可用组合的部分 TB_Sys_UserRight 中可用组合的储存方式已在 MS00010 中被修改
            if (sType.equalsIgnoreCase("portcode")) { //取当前的可用组合
            	sqlStr = "select y.* from " + 
            	    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                sqlStr = "select y.* from (select FPortCode,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_Portfolio") +
//                    " where FStartDate <= " + dbl.sqlDate(YssFun.toSqlDate(new java.util.Date())) +
//                    " and FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) + " group by FPortCode) x " +
                    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
            	    //edit by songjie 2011.03.15 不以最大的启用日期查询数据
            	    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, f.FAssetGroupName as FAssetGroupName," +
                    " e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName,g.FPortRateSrcName " +
                    //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                    " from (select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
                    " where FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " + 
                    dbl.sqlString(pub.getAssetGroupCode()) + ") a " +
                    //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                    " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) f on a.FAssetGroupCode = f.FAssetGroupCode " +
                    " left join (select FCuryCode,FCuryName as FCurrencyName from " + pub.yssGetTableName("Tb_Para_Currency") + " where FCheckState = 1) e on a.FPortCury = e.FCuryCode " +
                    " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " + pub.yssGetTableName("Tb_Para_ExRateSource") + " where FCheckState = 1) f " +
                    " on a.FBaseRateSrcCode = f.FExRateSrcCode left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName " +
                    " from " + pub.yssGetTableName("Tb_Para_ExRateSource") + " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode " +
                    " join (select DISTINCT FPortCode from Tb_Sys_Userright where FAssetGroupCode =  " + dbl.sqlString(pub.getAssetGroupCode()) + " and FUserCode = " + dbl.sqlString(pub.getUserCode()) +
                    " and FRightType = " + dbl.sqlString(YssCons.YSS_SYS_RIGHTTYPE_PORT) + ") d on a.FPortCode = d.FPortCode " +
                    " where a.fcheckstate = 1 and a.FEnabled = 1) y " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                    " order by y.FPortCode, y.FCheckState, y.FCreateTime desc ";
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    buf.append(rs.getString("FPortCode")).append("\t");
                    buf.append(rs.getString("FPortName")).append("\t");
                    buf.append("[root]").append("\t");
                    buf.append(i++).append("\f\f");
                }
                sResult = buf.toString();
                if (sResult.length() > 2) {
                    sResult = sResult.substring(0, sResult.length() - 2);
                }
            } else if (sType.equalsIgnoreCase("ctlsValue")) { //取控件的值
                sqlStr = "select FCtlCode,FCtlValue from " + pub.yssGetTableName("Tb_Comp_PortIndexLink") +
                    " where FPortCode=" + dbl.sqlString(this.sPortCode) + " and FIndexCfgCode=" +
                    dbl.sqlString(this.sIndexCfgCode) + " and FCtlGrpCode =" + dbl.sqlString(this.sCtlGrpCode) +
                    " and FCheckState<>2";
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    buf.append(rs.getString("FCtlCode")).append("\t");
                    //2008.10.09 蒋锦 修改 该字段已经改为大数据类型
                    buf.append(dbl.clobStrValue(rs.getClob("FCtlValue"))).append("\f\f");
                }
                sResult = buf.toString();
                if (sResult.length() > 2) {
                    sResult = sResult.substring(0, sResult.length() - 2);
                }
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException(ex.toString());
        } finally {
            dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public String getSOldPortCode() {
        return sOldPortCode;
    }

    public String getSOldIndexCfgCode() {
        return sOldIndexCfgCode;
    }

    public String getSOldCtlGrpCode() {
        return sOldCtlGrpCode;
    }

    public String getSOldCtlCode() {
        return sOldCtlCode;
    }

    public String getSIndexCfgCode() {
        return sIndexCfgCode;
    }

    public String getSDesc() {
        return sDesc;
    }

    public String getSCtlValue() {
        return sCtlValue;
    }

    public String getSCtlGrpCode() {
        return sCtlGrpCode;
    }

    public String getSCtlCode() {
        return sCtlCode;
    }

    public void setFilterType(CompPortIndexLinkBean filterType) {
        this.filterType = filterType;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setSOldPortCode(String sOldPortCode) {
        this.sOldPortCode = sOldPortCode;
    }

    public void setSOldIndexCfgCode(String sOldIndexCfgCode) {
        this.sOldIndexCfgCode = sOldIndexCfgCode;
    }

    public void setSOldCtlGrpCode(String sOldCtlGrpCode) {
        this.sOldCtlGrpCode = sOldCtlGrpCode;
    }

    public void setSOldCtlCode(String sOldCtlCode) {
        this.sOldCtlCode = sOldCtlCode;
    }

    public void setSIndexCfgCode(String sIndexCfgCode) {
        this.sIndexCfgCode = sIndexCfgCode;
    }

    public void setSDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    public void setSCtlValue(String sCtlValue) {
        this.sCtlValue = sCtlValue;
    }

    public void setSCtlGrpCode(String sCtlGrpCode) {
        this.sCtlGrpCode = sCtlGrpCode;
    }

    public void setSCtlCode(String sCtlCode) {
        this.sCtlCode = sCtlCode;
    }

    public void setSCtlGrpName(String sCtlGrpName) {
        this.sCtlGrpName = sCtlGrpName;
    }

    public void setSIndexCfgName(String sIndexCfgName) {
        this.sIndexCfgName = sIndexCfgName;
    }

    public CompPortIndexLinkBean getFilterType() {
        return filterType;
    }

    public String getSCtlGrpName() {
        return sCtlGrpName;
    }

    public String getSIndexCfgName() {
        return sIndexCfgName;
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
