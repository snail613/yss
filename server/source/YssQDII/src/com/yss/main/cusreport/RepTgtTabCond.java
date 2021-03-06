package com.yss.main.cusreport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.datainterface.DaoTgtTabCond;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class RepTgtTabCond extends BaseDataSettingBean implements IDataSetting {

	private String dPDsCode = "";
    private int orderIndex = -1;
    private String targetField;
    private String dsField;
    private String desc;
    private String dPDsName;
    private String oldDPDsCode;
    private String targetFieldName;
    private String dsFieldName;
    private RepTgtTabCond filterType;
    private String sMutilData = ""; //保存目标字段与源字段
    private int oldOrderIndex;

	public String getdPDsCode() {
		return dPDsCode;
	}

	public void setdPDsCode(String dPDsCode) {
		this.dPDsCode = dPDsCode;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getTargetField() {
		return targetField;
	}

	public void setTargetField(String targetField) {
		this.targetField = targetField;
	}

	public String getDsField() {
		return dsField;
	}

	public void setDsField(String dsField) {
		this.dsField = dsField;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getdPDsName() {
		return dPDsName;
	}

	public void setdPDsName(String dPDsName) {
		this.dPDsName = dPDsName;
	}

	public String getOldDPDsCode() {
		return oldDPDsCode;
	}

	public void setOldDPDsCode(String oldDPDsCode) {
		this.oldDPDsCode = oldDPDsCode;
	}

	public String getTargetFieldName() {
		return targetFieldName;
	}

	public void setTargetFieldName(String targetFieldName) {
		this.targetFieldName = targetFieldName;
	}

	public String getDsFieldName() {
		return dsFieldName;
	}

	public void setDsFieldName(String dsFieldName) {
		this.dsFieldName = dsFieldName;
	}

	public RepTgtTabCond getFilterType() {
		return filterType;
	}

	public void setFilterType(RepTgtTabCond filterType) {
		this.filterType = filterType;
	}

	public String getsMutilData() {
		return sMutilData;
	}

	public void setsMutilData(String sMutilData) {
		this.sMutilData = sMutilData;
	}

	public int getOldOrderIndex() {
		return oldOrderIndex;
	}

	public void setOldOrderIndex(int oldOrderIndex) {
		this.oldOrderIndex = oldOrderIndex;
	}

    public RepTgtTabCond() {
    }
	
	
	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub

	}
	public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    sMutilData = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            if (reqAry.length <= 1) {
                return;
            }
            this.dPDsCode = reqAry[0];
            if (reqAry[0].length() == 0) {
                this.dPDsCode = " ";
            }
            if (reqAry[1].length() > 0) {
                this.targetField = reqAry[1];
            }
            this.dsField = reqAry[2];
            this.desc = reqAry[3];
            this.oldDPDsCode = reqAry[4];
            this.checkStateId = YssFun.toInt(reqAry[5]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RepTgtTabCond();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析目标表删除条件出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.dPDsCode).append("\t");
        buf.append(this.dPDsName).append("\t");
        buf.append(this.targetField).append("\t");
        buf.append(this.targetFieldName).append("\t");
        buf.append(this.dsField).append("\t");
        buf.append(this.dsFieldName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.orderIndex).append("\t");
        buf.append(this.checkStateId).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    public String getOperValue(String sType) throws YssException {
        String strSql = "", strTabName = "";
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            if (sType.equalsIgnoreCase("tablename")) { //get TabName

                strSql = "select * from " + pub.yssGetTableName("Tb_Dao_Pretreat") +
                    " a where FDPDsCode =" + dbl.sqlString(this.dPDsCode);
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    strTabName = rs.getString("FTargetTab");
                    if (strTabName.length() == 0) {
                        strTabName = "false";
                    }
                }
                return strTabName;
            }
            if (sType.equalsIgnoreCase("target")) {
                sHeader = "目标源字段\t目标源字段描述";
                conn = dbl.loadConnection();
                strSql =
                    "select y.*,f.FFieldDesc as FDsFieldName,g.FFieldDesc as FTargetFieldName from " +
                    "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName, " +
                    "e.FDPDsName as FDPDsName,e.FTargetTab as FTargetTab from " +
                    pub.yssGetTableName("Tb_Dao_TgtTabCond") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                    " left join (select FDPDsCode,FDPDsName,FTargetTab from " +
                    pub.yssGetTableName("Tb_Dao_Pretreat") +
                    ") e on e.FDPDsCode=a.FDPDsCode ) y" +
                    " left join (select FTabName,FFieldName,FFieldDesc from tb_fun_datadict) f on f.FTabName=y.FTargetTab and f.FFieldName=y.FDsField " +
                    " left join (select FTabName,FFieldName,FFieldDesc from tb_fun_datadict) g on g.FTabName=y.FTargetTab and g.FFieldName=y.FTargetField " +
                    " where y.FDPDsCode=" + dbl.sqlString(dPDsCode) +
                    " and FTargetField<>' ' order by y.FCheckState, y.FCheckTime desc,y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append(rs.getString("FTargetField")).append("\t");
                    bufShow.append(rs.getString("FTargetFieldName")).append(YssCons.YSS_LINESPLITMARK);
                    setTgtTabCond(rs);
                    this.targetField = rs.getString("FTargetField");
                    this.targetFieldName = rs.getString("FTargetFieldName");
                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() - 2);
                }
                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
                }
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            }
            if (sType.equalsIgnoreCase("dsfield")) {

                sHeader = "数据源字段\t数据源字段描述";
                conn = dbl.loadConnection();
                strSql =
                    "select y.*,f.FFieldDesc as FDsFieldName,g.FFieldDesc as FTargetFieldName from " +
                    "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName, " +
                    "e.FDPDsName as FDPDsName,e.FTargetTab as FTargetTab from " +
                    pub.yssGetTableName("Tb_Rep_TgtTabCond") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                    " left join (select FDPDsCode,FDPDsName,FTargetTab from " +
                    pub.yssGetTableName("Tb_Rep_Pretreat") +
                    ") e on e.FDPDsCode=a.FDPDsCode ) y" +
                    " left join (select FTabName,FFieldName,FFieldDesc from tb_fun_datadict) f on f.FTabName=y.FTargetTab and f.FFieldName=y.FDsField " +
                    " left join (select FTabName,FFieldName,FFieldDesc from tb_fun_datadict) g on g.FTabName=y.FTargetTab and g.FFieldName=y.FTargetField " +
                    " where y.FDPDsCode=" + dbl.sqlString(dPDsCode) +
                    " and FDsField<>' ' order by y.FCheckState, y.FCheckTime desc,y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append(rs.getString("FDsField")).append("\t");
                    bufShow.append(rs.getString("FDsFieldName")).append(YssCons.YSS_LINESPLITMARK);
                    setTgtTabCond(rs);
                    this.dsField = rs.getString("FDsField");
                    this.dsFieldName = rs.getString("FDsFieldName");
                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
                }
                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
                }
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            }
            //---------------------2007.11.06 添加 查询目标表字段和数据源字段 蒋锦------------------------//
            if (sType.equalsIgnoreCase("targetanddsfield")) {

                sHeader = "目标表字段\t数据源字段\t目标表字段描述";
                conn = dbl.loadConnection();
                strSql =
                    "select y.*,f.FFieldDesc as FDsFieldName,case when g.FFieldDesc='' or g.FFieldDesc=' ' or g.FFieldDesc is null then f.FFieldDesc else g.FFieldDesc end as FTargetFieldName from " +
                    "(select a.FDpDsCode,a.FOrderIndex,upper(a.FTargetField) as FTargetField,upper(a.FDsField) as FDsField,a.FCreator,a.FCreateTime,a.FCheckUser,a.FCheckTime,a.FDesc,a.FCheckState,b.FUserName as FCreatorName,c.FUserName as FCheckUserName, " +
                    "e.FDPDsName as FDPDsName,e.FTargetTab as FTargetTab from " +
                    pub.yssGetTableName("Tb_Rep_TgtTabCond") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                    " left join (select FDPDsCode,FDPDsName,FTargetTab from " +
                    pub.yssGetTableName("Tb_Rep_Pretreat") +
                    ") e on e.FDPDsCode=a.FDPDsCode ) y" +
                    " left join (select FTabName,Upper(FFieldName) as FFieldName,FFieldDesc from tb_fun_datadict) f on f.FTabName=y.FTargetTab and f.FFieldName=y.FDsField " +
                    " left join (select FTabName,Upper(FFieldName) as FFieldName,FFieldDesc from tb_fun_datadict) g on g.FTabName=y.FTargetTab and g.FFieldName=y.FTargetField " +
                    " where y.FDPDsCode=" + dbl.sqlString(dPDsCode) +
                    " and FDsField<>' ' order by y.FCheckState, y.FCheckTime desc,y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append(rs.getString("FTargetField")).append("\t");
                    bufShow.append(rs.getString("FDsField")).append("\t");
                    bufShow.append(rs.getString("FTargetFieldName")).append(YssCons.YSS_LINESPLITMARK);
                    setTgtTabCond(rs);
                    this.targetField = rs.getString("FTargetField");
                    this.dsField = rs.getString("FDsField");
                    this.dsFieldName = rs.getString("FTargetFieldName");
                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() - 2);
                }
                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
                }
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            }
            //---------------------------------------------------------------------------------------//
            return "";
        } catch (Exception e) {
            throw new YssException("解析接口代码信息出错");
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }
	
	private String builerFilter() {
        String sqlStr = "";
        if (this.filterType != null) {
            sqlStr = " where 1=1 ";
            if (filterType.dPDsCode != null && this.filterType.dPDsCode.trim().length() != 0) {
                sqlStr += " and a.FDPDsCode like '" + filterType.dPDsCode.replaceAll("'", "''") + "%'";
            }
        }
        return sqlStr;
    }

    private void setTgtTabCond(ResultSet rs) throws SQLException, YssException {
        this.dPDsCode = rs.getString("FDPDsCode");
        this.dPDsName = rs.getString("FDPDsName");
        this.desc = rs.getString("FDesc");
        this.checkStateId = rs.getInt("FCheckState");
    }
	//=======================================================
	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getListViewData1() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = getListView1Headers();
            conn = dbl.loadConnection();
            sqlStr = "select distinct(a.FDPDsCode) as FDPDsCode,e.FDPDsName as FDPDsName,FDesc,Fcheckstate, " +
                " '' as FCreatorName, '' as FCheckUserName " +
                " from " + pub.yssGetTableName("Tb_Rep_TgtTabCond") + " a " +
                // " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                //   " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FDPDsCode,FDPDsName from " + pub.yssGetTableName("Tb_Dao_Pretreat") +
                " ) e on e.FDPDsCode=a.FDPDsCode " +
                builerFilter() +
                " order by a.FCheckState ";

            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setTgtTabCond(rs);
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
            throw new YssException("获取预处理接口信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }


	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	//=======================================================
	private String addSetting(String str) throws YssException {
        String strSql = "", str1 = "";
        boolean bTrans = false; //代表是否开始了事务
        String sOrderIndex = "";
        Connection conn = dbl.loadConnection();
        try {
            this.parseRowStr(str);
            str1 = "select max(FOrderIndex) from " + pub.yssGetTableName("Tb_Rep_TgtTabCond") +
                " where FDPDsCode=" + dbl.sqlString(this.dPDsCode);
            ResultSet rs = dbl.openResultSet(str1);
            while (rs.next()) {
                this.orderIndex = rs.getInt(1) + 1;
            }

            if (this.targetField == null || this.dsField == null || this.dPDsCode == null) {
                return "";
            }
            strSql = "insert into " + pub.yssGetTableName("Tb_Rep_TgtTabCond") +
                "(FDPDsCode,FOrderIndex,FTargetField,FDsField,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.dPDsCode) + "," +
                this.orderIndex + "," +
                dbl.sqlString(this.targetField) + "," +
                dbl.sqlString(this.dsField) + " ," +
                dbl.sqlString(this.desc) + "," +
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
        } catch (Exception e) {
            throw new YssException("增加目标表删除条件出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

	
	public String addSetting() throws YssException {
		return this.saveMutliSetting(sMutilData);
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		parseRowStr(sMutilRowStr);
        //-------------2007.11.20 蒋锦 添加 为适应不同规格的字符串---------------//
        if (sMutilRowStr.split("\r\t") != null && sMutilRowStr.split("\r\t").length >= 3) {
            sMutilRowStr = sMutilRowStr.split("\r\t")[2];
        }
        //-------------------------------------------------------------------//
        String str = "";
        ResultSet rs = null;
        boolean bTrans = false;
        String[] sData = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
        Connection conn = dbl.loadConnection();
        try {

            str = "delete from " + pub.yssGetTableName("Tb_Rep_TgtTabCond") +
                " where FDPDsCode =" + dbl.sqlString(this.dPDsCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(str);
            if (sMutilRowStr.length() > 0) {
                for (int i = 0; i < sData.length; i++) {
                    this.addSetting(sData[i]);
                }
            } else {
                str = "insert into " + pub.yssGetTableName("Tb_Rep_TgtTabCond") +
                    "(FDPDsCode,FOrderIndex,FTargetField,FDsField,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values(" + dbl.sqlString(this.dPDsCode) + "," +
                    this.orderIndex + "," +
                    "' '," +
                    "' ' ," +
                    dbl.sqlString(this.desc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(str);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("保存错误", e);
        }
        return "";
    }

    public void delSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            sqlStr = " delete from  " + pub.yssGetTableName("Tb_Rep_TgtTabCond") +
                " where FDPDsCode =" + dbl.sqlString(this.oldDPDsCode);
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除目标表删除条件出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub

	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            sqlStr = " update " + pub.yssGetTableName("Tb_Rep_TgtTabCond") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FDPDsCode =" + dbl.sqlString(this.oldDPDsCode);
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核目标表删除条件出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }




	

	
}
