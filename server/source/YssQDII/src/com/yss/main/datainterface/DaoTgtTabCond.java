package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class DaoTgtTabCond
    extends BaseDataSettingBean implements IDataSetting {
    private String dPDsCode = "";
    private int orderIndex = -1;
    private String targetField;
    private String dsField;
    private String desc;
    private String dPDsName;
    private String oldDPDsCode;
    private String targetFieldName;
    private String dsFieldName;
    private DaoTgtTabCond filterType;
    private String sMutilData = ""; //保存目标字段与源字段
    private int oldOrderIndex;

    /**add---shashijie 2013-2-25 STORY 3366 增加存储类型-公共表复选框*/
	private String saveType = "0";//默认不选中状态
	/**end---shashijie 2013-2-25 STORY 3366*/
	
    public DaoTgtTabCond() {
    	
    }

    public void checkInput(byte btOper) throws YssException {
    	/**add---shashijie 2013-2-26 STORY 3366 增加公共表数据处理*/
    	//非选中状态处理组合群表数据,反之处理公共表数据
    	if (this.saveType.equals("0")) {
    		dbFun.checkInputCommon(btOper,
                    pub.yssGetTableName("Tb_Dao_TgtTabCond"),
                    "FDPDsCode",
                    this.dPDsCode,
                    this.oldDPDsCode);
		} else {
			dbFun.checkInputCommon(btOper,
                    "Tb_Dao_TgtTabCond",
                    "FDPDsCode",
                    this.dPDsCode,
                    this.oldDPDsCode);
		}
		/**end---shashijie 2013-2-26 STORY 3366*/
        
    }

    private String addSetting(String str) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        //String sOrderIndex = "";
        Connection conn = dbl.loadConnection();
        try {
        	/**add---shashijie 2013-2-25 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        	conn.setAutoCommit(false);
            bTrans = true;
            this.parseRowStr(str);
            //设置最大排序编号
            setSelectOrderIndex();

            if (this.targetField == null || this.dsField == null || this.dPDsCode == null) {
                return "";
            }
            //获取添加SQL
            strSql = getInsertSql();
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**end---shashijie 2013-2-25 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        } catch (Exception e) {
            throw new YssException("增加目标表删除条件出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**shashijie 2013-2-26 STORY 3366 */
	private String getInsertSql() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " insert into " + pub.yssGetTableName("Tb_Dao_TgtTabCond");
		} else {
			sql = " insert into Tb_Dao_TgtTabCond ";
		}
		sql +=  "(FDPDsCode,FOrderIndex,FTargetField,FDsField,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
	        " values(" + dbl.sqlString(this.dPDsCode) + "," +
	        this.orderIndex + ",";
		//目标表字段
        if(this.targetField == null || this.targetField.trim().equals("")){
        	sql += " ' ' , ";
        } else {
        	sql += dbl.sqlString(this.targetField) + ",";
		}
        //数据源字段
	    if (this.dsField == null || this.dsField.trim().equals("")) {
	    	sql += " ' ' , ";
		} else {
			sql += dbl.sqlString(this.dsField) + " ,";
		}
	    sql += dbl.sqlString(this.desc) + "," +
	        (pub.getSysCheckState() ? "0" : "1") + "," +
	        dbl.sqlString(this.creatorCode) + "," +
	        dbl.sqlString(this.creatorTime) + "," +
	        (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ")";
		return sql;
	}

	/**shashijie 2013-2-26 STORY 3366 设置最大排序编号*/
	private void setSelectOrderIndex() throws YssException {
		String sqlStr = "";
		ResultSet rs = null;
		try {
			//获取最大排序编号SQL
			sqlStr = getSelectOrderIndexSql();
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                this.orderIndex = rs.getInt(1) + 1;
            }
		} catch (Exception e) {
			throw new YssException("设置最大排序编号出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2013-2-26 STORY 3366 */
	private String getSelectOrderIndexSql() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " Select Max(FOrderIndex) From " + pub.yssGetTableName("Tb_Dao_TgtTabCond");
		} else {
			sql = " Select Max(FOrderIndex) From Tb_Dao_TgtTabCond ";
		}
		sql += " where FDPDsCode=" + dbl.sqlString(this.dPDsCode);
		return sql;
	}

	public String addSetting() throws YssException {
        return this.saveMutliSetting(sMutilData);
    }

    public String editSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        //ResultSet rs = null;
        try {
        	/**add---shashijie 2013-2-26 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            if (sMutilData.length() == 0) {
                conn = dbl.loadConnection();
                conn.setAutoCommit(false);
                bTrans = true;
                //获取SQL
                sqlStr = getUpdateSql();
                dbl.executeSql(sqlStr);
                
                conn.commit();
                conn.setAutoCommit(true);
                bTrans = false;
            } else {
                return this.saveMutliSetting(sMutilData);
            }
            /**end---shashijie 2013-2-26 STORY 3366 */
        } catch (Exception e) {
            throw new YssException("修改目标表删除条件出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    /**shashijie 2013-2-26 STORY 3366 */
	private String getUpdateSql() {
		String sql = "";
		//如果是非选中状态处理组合群表,反之则处理公共表
		if (saveType.equals("0")) {
			sql = " update " + pub.yssGetTableName("Tb_Dao_TgtTabCond");
		} else {
			sql = " update Tb_Dao_TgtTabCond ";
		}
		sql += "  set " +
	        " FDPDsCode =" + dbl.sqlString(this.dPDsCode) + "," +
	        " FTargetField =' '," +
	        " FDsField= ' '," +
	        " FDesc =" + dbl.sqlString(this.desc) +
	        " where FDPDsCode =" + dbl.sqlString(this.oldDPDsCode);
		return sql;
	}

	public void delSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
        	/**add---shashijie 2013-2-25 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        	conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            
            sqlStr = getDeleteSql(this.oldDPDsCode);
            dbl.executeSql(sqlStr);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**end---shashijie 2013-2-25 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        } catch (Exception e) {
            throw new YssException("删除目标表删除条件出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**shashijie 2013-2-26 STORY 3366*/
	private String getDeleteSql(String FDPDsCode) {
		String sql = "";
		//如果是非选中状态处理组合群表,反之则处理公共表
		if (saveType.equals("0")) {
			sql = " delete from " + pub.yssGetTableName("Tb_Dao_TgtTabCond");
		} else {
			sql = " delete from Tb_Dao_TgtTabCond ";
		}
		sql += " where FDPDsCode =" + dbl.sqlString(FDPDsCode);
		return sql;
	}

	public void checkSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
        	/**add---shashijie 2013-2-26 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        	conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
			
            sqlStr = getChenkSql();
            dbl.executeSql(sqlStr);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**end---shashijie 2013-2-26 STORY 3366 */
        } catch (Exception e) {
            throw new YssException("审核目标表删除条件出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**shashijie 2013-2-26 STORY 3366*/
	private String getChenkSql() {
		String sql = "";
		//如果是非选中状态处理组合群表,反之则处理公共表
		if (saveType.equals("0")) {
			sql = " update " + pub.yssGetTableName("Tb_Dao_TgtTabCond");
		} else {
			sql = " update Tb_Dao_TgtTabCond ";
		}
		sql += " set FCheckState = " + this.checkStateId +
	        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
	        "' where FDPDsCode =" + dbl.sqlString(this.oldDPDsCode);
		return sql;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
        //-------------2007.11.20 蒋锦 添加 为适应不同规格的字符串---------------//
        if (sMutilRowStr.split("\r\t") != null && sMutilRowStr.split("\r\t").length >= 3) {
            sMutilRowStr = sMutilRowStr.split("\r\t")[2];
        }
        //-------------------------------------------------------------------//
        String str = "";
        //ResultSet rs = null;
        boolean bTrans = false;
        String[] sData = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
        Connection conn = dbl.loadConnection();
        try {
        	/**add---shashijie 2013-2-26 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        	conn.setAutoCommit(false);
            bTrans = true;
        	
            str = getDeleteSql(this.dPDsCode);
            dbl.executeSql(str);
            
            if (sMutilRowStr.length() > 0) {
                for (int i = 0; i < sData.length; i++) {
                    this.addSetting(sData[i]);
                }
            } else {
                str = getInsertSql();
                dbl.executeSql(str);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**end---shashijie 2013-2-26 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        } catch (Exception e) {
            throw new YssException("保存错误", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public IDataSetting getSetting() throws YssException {
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
        /**add---shashijie 2013-2-25 STORY 3366 存储类型赋值*/
		setSaveTypeValue(rs);
		/**end---shashijie 2013-2-25 STORY 3366 */
    }
	
	/**shashijie 2013-2-18 STORY 3366 存储类型副职 */
	private void setSaveTypeValue(ResultSet rs) {
		try {
			String temp = rs.getString("saveType");
			if (temp.equalsIgnoreCase("公共")) {
				this.saveType = "1";
			} else {
				this.saveType = "0";
			}
		} catch (Exception e) {
			//默认是组合群数据,非选中状态
			this.saveType = "0";
		} finally {

		}
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
            
            /**add---shashijie 2013-2-18 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
    		//组合群SQL
            sqlStr = getSelectList(pub.yssGetTableName("Tb_Dao_TgtTabCond"),"0");
            
            sqlStr += " Union All ";
            //公共表SQL
            sqlStr += getSelectList("Tb_Dao_TgtTabCond","1");
    		/**end---shashijie 2013-2-18 STORY 3366 */
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

    /**shashijie 2013-2-26 STORY 3366 */
	private String getSelectList(String tableName, String saveType) {
		String sql = "";
		String tablePre = pub.yssGetTableName("Tb_Dao_Pretreat");
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select Distinct '组合群' as saveType , ";
		} else {
			sql = " select Distinct '公共' as saveType , ";
			tablePre = "Tb_Dao_Pretreat";
		}
		sql += " a.FDPDsCode as FDPDsCode,e.FDPDsName as FDPDsName,FDesc,Fcheckstate, " +
	        " '' as FCreatorName, '' as FCheckUserName " +
	        " from " + tableName + " a " +
	        // " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        //   " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
	        " left join (select FDPDsCode,FDPDsName from " + tablePre +
	        " ) e on e.FDPDsCode=a.FDPDsCode " +
	        builerFilter();
	        //" order by a.FCheckState ";
		return sql;
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
            /**add---shashijie 2013-2-17 STORY 3366 增加存储类型-公共表复选框*/
            this.saveType = reqAry[7];
			/**end---shashijie 2013-2-17 STORY 3366*/
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoTgtTabCond();
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
        /**add---shashijie 2013-2-25 STORY 3366 增加存储类型-公共表复选框*/
        buf.append(this.saveType.trim()).append("\t");
		/**end---shashijie 2013-2-25 STORY 3366*/
        buf.append(super.buildRecLog());
        return buf.toString();

    }
	
	/**shashijie 2013-2-25 STORY 3366 获取SQL*/
	private String getSelectObject() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " select * from " + pub.yssGetTableName("Tb_Dao_Pretreat");
		} else {
			sql = " select * from Tb_Dao_Pretreat ";
		}
		sql += " where FDPDsCode =" + dbl.sqlString(this.dPDsCode);
		return sql;
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
        	/**add---shashijie 2013-2-26 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            if (sType.equalsIgnoreCase("tablename")) { //get TabName
            	//获取SQL
                strSql = getSelectObject();
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
                    pub.yssGetTableName("Tb_Dao_TgtTabCond") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                    " left join (select FDPDsCode,FDPDsName,FTargetTab from " +
                    pub.yssGetTableName("Tb_Dao_Pretreat") +
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
            	//文件列表
                sHeader = "目标表字段\t数据源字段\t目标表字段描述";
                conn = dbl.loadConnection();
                //获取SQL
                strSql = getSelectListTgtTabCond();
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
            /**end---shashijie 2013-2-26 STORY 3366 */
        } catch (Exception e) {
            throw new YssException("解析接口代码信息出错");
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**shashijie 2013-2-26 STORY 3366*/
	private String getSelectListTgtTabCond() {
		String tableName = "";
		String tablepre = "";
		String select = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			tableName = pub.yssGetTableName("Tb_Dao_TgtTabCond");
			tablepre = pub.yssGetTableName("Tb_Dao_Pretreat");
			select = " '组合群' as saveType , ";
		} else {
			tableName = "Tb_Dao_TgtTabCond";
			tablepre = "Tb_Dao_Pretreat";
			select = " '公共' as saveType , ";
		}
		String sql = " select "+select+" y.*,f.FFieldDesc as FDsFieldName,case when g.FFieldDesc='' or g.FFieldDesc=' ' or " +
			" g.FFieldDesc is null then f.FFieldDesc else g.FFieldDesc end as FTargetFieldName from " +
	        " (select a.FDpDsCode,a.FOrderIndex,upper(a.FTargetField) as FTargetField,upper(a.FDsField) as FDsField," +
	        " a.FCreator,a.FCreateTime,a.FCheckUser,a.FCheckTime,a.FDesc,a.FCheckState,b.FUserName as FCreatorName," +
	        " c.FUserName as FCheckUserName, " +
	        " e.FDPDsName as FDPDsName,e.FTargetTab as FTargetTab from " +
	        tableName + " a" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
	        " left join (select FDPDsCode,FDPDsName,FTargetTab from " +
	        tablepre +
	        ") e on e.FDPDsCode=a.FDPDsCode ) y" +
	        " left join (select FTabName,Upper(FFieldName) as FFieldName,FFieldDesc from tb_fun_datadict) f " +
	        " on f.FTabName=y.FTargetTab and f.FFieldName=y.FDsField " +
	        " left join (select FTabName,Upper(FFieldName) as FFieldName,FFieldDesc from tb_fun_datadict) g " +
	        " on g.FTabName=y.FTargetTab and g.FFieldName=y.FTargetField " +
	        " where y.FDPDsCode=" + dbl.sqlString(this.dPDsCode) +
	        " and FDsField<>' ' order by y.FCheckState, y.FCheckTime desc,y.FCreateTime desc";
		return sql;
	}

	public void setDPDsCode(String dPDsCode) {
        this.dPDsCode = dPDsCode;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public void setDsField(String dsField) {
        this.dsField = dsField;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDPDsName(String dPDsName) {
        this.dPDsName = dPDsName;
    }

    public void setOldDPDsCode(String oldDPDsCode) {
        this.oldDPDsCode = oldDPDsCode;
    }

    public void setTargetFieldName(String targetFieldName) {
        this.targetFieldName = targetFieldName;
    }

    public void setDsFieldName(String dsFieldName) {
        this.dsFieldName = dsFieldName;
    }

    public void setFilterType(DaoTgtTabCond filterType) {
        this.filterType = filterType;
    }

    public void setOldOrderIndex(int oldOrderIndex) {
        this.oldOrderIndex = oldOrderIndex;
    }

    public void setSMutilData(String sMutilData) {
        this.sMutilData = sMutilData;
    }

    public String getDPDsCode() {
        return dPDsCode;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public String getTargetField() {
        return targetField;
    }

    public String getDsField() {
        return dsField;
    }

    public String getDesc() {
        return desc;
    }

    public String getDPDsName() {
        return dPDsName;
    }

    public String getOldDPDsCode() {
        return oldDPDsCode;
    }

    public String getTargetFieldName() {
        return targetFieldName;
    }

    public String getDsFieldName() {
        return dsFieldName;
    }

    public DaoTgtTabCond getFilterType() {
        return filterType;
    }

    public int getOldOrderIndex() {
        return oldOrderIndex;
    }

    public String getSMutilData() {
        return sMutilData;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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

	/**add---shashijie 2013-2-26 返回 saveType 的值*/
	public String getSaveType() {
		return saveType;
	}

	/**add---shashijie 2013-2-26 传入saveType 设置  saveType 的值*/
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
}
