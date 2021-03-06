package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class DaoDictBean
    extends BaseDataSettingBean implements IDataSetting {
    private String dictCode = "";
    private String DictName = "";
    //private String sRowStr = "";
    private String srcConent = ""; //BugNO:MS00066 20081205     王晓光
    //private String Num = "";
    private String sRecycled = "";
    private String cnvConent = "";
    private String desc = "";
    private String OldDictCode = "";
    private DaoDictBean filterType;
    private String subDict = "";
    
    /**add---shashijie 2013-02-22 STORY 3366 增加存储类型-公共表复选框字段 */
    private String saveType = "0";//默认是非选中状态
    /**end---shashijie 2013-02-22 STORY 3366*/
    
    public DaoDictBean() {
    }

    public void checkInput(byte btOper) throws YssException {
        /**add---shashijie 2013-2-18 STORY 3366 增加公共表数据处理*/
    	//非选中状态处理组合群表数据,反之处理公共表数据
    	if (this.saveType.equals("0")) {
    		dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Dao_Dict"),
                    "FDictCode", this.dictCode, this.OldDictCode);
		} else {
			dbFun.checkInputCommon(btOper, "Tb_Dao_Dict",
                    "FDictCode", this.dictCode, this.OldDictCode);
		}
		/**end---shashijie 2013-2-18 STORY 3366*/
    }

    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String reSql = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
        	/**add---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            //获取删除数据SQL
            reSql = getDeleteSql(this.OldDictCode);
            dbl.executeSql(reSql);
            
            reSql = getInsertSql();
            dbl.executeSql(reSql);
            
            if (this.subDict.trim().length() != 0) {
                this.saveMutliSetting(subDict);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**end---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            
        } catch (Exception e) {
            throw new YssException("添加字典设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**shashijie 2013-2-22 STORY 3366 获取SQL*/
	private String getInsertSql() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " insert into " + pub.yssGetTableName("Tb_Dao_Dict");
		} else {
			sql = " insert into Tb_Dao_Dict ";
		}
		sql += " (FDictCode,FDictName,FSrcConent,FCnvConent,FDesc,FCheckState," +
	        " FCreator,FCreateTime) values(" +
	        dbl.sqlString(this.dictCode) + "," +
	        dbl.sqlString(this.DictName) + "," +
	        dbl.sqlString(this.srcConent) + "," +
	        dbl.sqlString(this.cnvConent) + "," +
	        dbl.sqlString(this.desc) + "," +
	        (pub.getSysCheckState() ? "0" : "1") + "," +
	        dbl.sqlString(this.creatorCode) + "," +
	        dbl.sqlString(this.creatorTime) + ")";
		return sql;
	}

	/**shashijie 2013-2-22 STORY 3366 获取SQL*/
	private String getDeleteSql(String FDictCode) {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " delete from " + pub.yssGetTableName("Tb_Dao_Dict");
		} else {
			sql = " delete from Tb_Dao_Dict ";
		}
		sql += " where FDictCode = " + dbl.sqlString(FDictCode);
		return sql;
	}

	public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String reStr = "";
        boolean bTrans = false;
        try {
        	conn.setAutoCommit(false);
            bTrans = true;
        	/**add---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            //获取修改数据SQL
            reStr = getUpdateSql();
            dbl.executeSql(reStr);
            
            if (this.subDict.trim().length() >= 0) { //jiangchun
                this.saveMutliSetting(subDict);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**end---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            
        } catch (Exception e) {
            throw new YssException("修改字典设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**shashijie 2013-2-22 STORY 3366 获取SQL */
	private String getUpdateSql() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " update " + pub.yssGetTableName("Tb_Dao_Dict");
		} else {
			sql = " update Tb_Dao_Dict ";
		}
		sql += " set FDictCode=" + dbl.sqlString(this.dictCode) +
	        ",FDictName=" + dbl.sqlString(this.DictName) +
	        ",FDesc =" + dbl.sqlString(this.desc) +
	        " where FDictCode=" + dbl.sqlString(this.OldDictCode);
		return sql;
	}

	/**修改时间：2008年05月06日
     * 修改人：韩冠男
     * 原方法功能：从数据库删除信息去回收站
     * 新方法功能：从数据库删除信息去回收站，并可以同时处理多条信息
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String reStr = "";
        String[] arrData = null;
        boolean bTrans = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            /**add---shashijie 2013-2-22 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {            	
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    if(arrData[i].length()==8){
                    	
                    }
                    this.parseRowStr(arrData[i]);

                    //获取删除SQL语句
                    reStr = getChenkSql();
                    dbl.executeSql(reStr);
                }
            }
            /**end---shashijie 2013-2-22 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("删除字典设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年05月06日
     * 修改人：韩冠男
     * 原方法功能：可以把信息从回收站还原到数据库
     * 新方法功能：可以把信息从回收站还原到数据库，并可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String reStr = "";
        String[] arrData = null;
        boolean bTrans = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            /**add---shashijie 2013-2-22 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    //获取审核SQL
                    reStr = getChenkSql();
                    dbl.executeSql(reStr);
                }
            }
            //如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (dictCode != null && (!dictCode.equalsIgnoreCase(""))) {
            	//获取审核SQL
                reStr = getChenkSql();
                reStr += " and FSRCCONENT=" + dbl.sqlString(this.srcConent);
                dbl.executeSql(reStr);

            }
            /**end---shashijie 2013-2-22 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核字典设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**shashijie 2013-2-22 STORY 3366 获取SQL */
	private String getChenkSql() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " update " + pub.yssGetTableName("Tb_Dao_Dict");
		} else {
			sql = " update Tb_Dao_Dict ";
		}
		sql += " set FCheckState=" + this.checkStateId +
	        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
	        "' where FDictCode=" + dbl.sqlString(this.dictCode);
		return sql;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = null;
        String reStr = "";
        boolean bTrans = false;
        //boolean bType = true;
        //int iRow = 0;
        try {
            conn = dbl.loadConnection();
            String[] sData = sMutilRowStr.split("\f\f");
            
            /**add---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            //获取删除数据SQL
            reStr = getDeleteSql(dictCode);
            dbl.executeSql(reStr);
            
            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < sData.length; i++) {
                this.parseRowStr(sData[i]);
                //获取添加SQL
                reStr = getInsertSql();
                dbl.executeSql(reStr);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**end---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            
        } catch (Exception ex) {
            throw new YssException("字典代码设置出错", ex);
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

    private void setDictAttr(ResultSet rs) throws SQLException, YssException {
        this.dictCode = rs.getString("FDictCode");
        this.DictName = rs.getString("FDictName");
        this.srcConent = rs.getString("FSrcConent");
        this.cnvConent = rs.getString("FCnvConent");
        this.desc = rs.getString("FDesc");
    }

    private String filterSql() {
        String reSql = "";
        if (this.filterType != null) {
            reSql = " where 1=1";
            if (this.filterType.dictCode != null && filterType.dictCode.length() != 0) {
                //reSql += " and FDictCode like '" + filterType.dictCode.replaceAll("'", "''") + "%'";//edited by zhouxiang MS01314    在凭证字典配置中有用 like ‘**%’来匹配记录显示，导致浏览、修改等操作时有误  
            	reSql += " and FDictCode = '" + filterType.dictCode.replaceAll("'", "''") + "'";
            }
			if (filterType.DictName != null && filterType.DictName.length() != 0)
			{
				//fanghaoln 20100623 MS01265 QDV4赢时胜(测试)2010年6月2日6_B 
				reSql += " and FDictName = '" + filterType.DictName.replaceAll("'", "''") + "'";
			}
			if (filterType.desc != null && filterType.desc.trim().length() != 0)
			{
				//fanghaoln 20100623 MS01265 QDV4赢时胜(测试)2010年6月2日6_B 
				reSql += " and FDesc = '" + filterType.desc.replaceAll("'", "''") + "'";
			}
        }
        return reSql;
    }

    public String getListViewData1() throws YssException {
        String reStr = "";
        ResultSet rs = null;
        String sHeader = "", sAllDataStr = "", sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            
            /**add---shashijie 2013-2-18 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
    		//组合群SQL
            reStr = getSelectList(pub.yssGetTableName("Tb_Dao_Dict"),"0");
            
            reStr += " Union All ";
            //公共表SQL
            reStr += getSelectList("Tb_Dao_Dict","1");
    		/**end---shashijie 2013-2-18 STORY 3366 */
            
            rs = dbl.openResultSet(reStr);
            while (rs.next()) {
            	bufShow.append(super.buildRowShowStr(rs,this.getListView1ShowCols())).append(YssCons.YSS_LINESPLITMARK);
                
                this.dictCode = rs.getString("FDictCode");
                this.DictName = rs.getString("FDictName");
                this.desc = rs.getString("FDesc");
                /**add---shashijie 2013-2-18 STORY 3366 存储类型赋值*/
        		setSaveTypeValue(rs);
        		/**end---shashijie 2013-2-18 STORY 3366 */
                super.checkStateId = rs.getInt("Fcheckstate");
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
            throw new YssException("获取字典设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**shashijie 2013-2-22 STORY 3366 获取 SQL*/
	private String getSelectList(String tableName, String saveType) {
		String reStr = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			reStr = " select Distinct '组合群' as saveType , ";
		} else {
			reStr = " select Distinct '公共' as saveType , ";
		}
		reStr += " FDictCode,FDictName,FDesc,Fcheckstate from " + tableName
	        + this.filterSql()
	        + " ";
		return reStr;
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
	
	public String getListViewData2() throws YssException {
        String reStr = "";
        ResultSet rs = null;
        String sHeader = "", sAllDataStr = "", sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "源内容\t转换内容";
            //获取关联SQL
            reStr = getSelectListJoin();
            rs = dbl.openResultSet(reStr);
            
            while (rs.next()) {
                bufShow.append(rs.getString("FSrcConent")).append("\t");
                bufShow.append(rs.getString("FCnvConent")).append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                // bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                //   append(YssCons.YSS_LINESPLITMARK);
                this.setDictAttr(rs);
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
            throw new YssException("获取字典设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**shashijie 2013-2-22 STORY 3366 获取SQL*/
	private String getSelectListJoin() {
		String reStr = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			reStr = " select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName "
		        + " from " + pub.yssGetTableName("Tb_Dao_Dict");
		} else {
			reStr = " select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName "
		        + " from Tb_Dao_Dict ";
		}
		reStr += " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode"
			+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode"
	        + (this.filterSql().length() > 0 ? this.filterSql() + " and FSrcConent<>'null' " : "")
	        + " order by a.FCreateTime desc, a.FCheckTime desc, a.FDictCode";
		return reStr;
	}

	public String getListViewData3() throws YssException {
        String reStr = "";
        ResultSet rs = null;
        String sHeader = "", sAllDataStr = "", sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "字典代码\t字典名称";
            //数据字典的显示不再按字段名列示，只列示表名就可以了。fazmm20071006
            /*reStr ="select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName "
                  +" from "+pub.yssGetTableName("Tb_Dao_Dict")
                  +" a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode"
                  +" left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode"
                  + (this.filterSql().length()>0?this.filterSql()+" and FSrcConent<>'null' ":"")
                  +" order by a.FCreateTime desc, a.FCheckTime desc, a.FDictCode";*/
            reStr = "select distinct FDictCode,FDictName from " + pub.yssGetTableName("Tb_Dao_Dict") +
                " where FCheckState=1";
            rs = dbl.openResultSet(reStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FDictCode")).append("\t");
                bufShow.append(rs.getString("FDictName")).append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                // bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                //   append(YssCons.YSS_LINESPLITMARK);
                // this.setDictAttr(rs);
                this.dictCode = rs.getString("FDictCode");
                this.DictName = rs.getString("FDictName");
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
            throw new YssException("获取字典设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    
    /**shashijie 2013-2-22 STORY 3366 获取公共表的字典数据内容*/
    public String getListViewData4() throws YssException {
    	String reStr = "";
        ResultSet rs = null;
        String sHeader = "", sAllDataStr = "", sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            //公共表
            reStr = "select distinct FDictCode,FDictName,FDesc,Fcheckstate from Tb_Dao_Dict "
                + this.filterSql()
                + " order by FDictCode";
            rs = dbl.openResultSet(reStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FDictCode")).append("\t");
                bufShow.append(rs.getString("FDictName")).append("\t");
                bufShow.append(rs.getString("FDesc")).append("\t").append(YssCons.YSS_LINESPLITMARK);
                
                this.dictCode = rs.getString("FDictCode");
                this.DictName = rs.getString("FDictName");
                this.desc = rs.getString("FDesc");
                super.checkStateId = rs.getInt("Fcheckstate");
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
            throw new YssException("获取公共表的字典设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    subDict = sRowStr.split("\r\t")[2];
                }
            
            }else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.dictCode = reqAry[0];
            this.DictName = reqAry[1];
            this.srcConent = reqAry[2];
            if (reqAry[2].trim().length() == 0) {
                srcConent = "null";
            }
            this.cnvConent = reqAry[3];
            if (reqAry[3].trim().length() == 0) {
                cnvConent = "null";
            }
            
            //edit by licai 20101112 BUG #374 接口字典设置描述含有回车的问题 
            if (reqAry[4] != null ){
            	if (reqAry[4].indexOf("【Enter】") >= 0){
            		this.desc = reqAry[4].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.desc = reqAry[4];
            	}
            }            
            //this.desc = reqAry[4];
          //edit by licai 20101112 BUG #374 接口字典设置描述含有回车的问题 ==end=      
            
            this.OldDictCode = reqAry[5];
            this.checkStateId = Integer.parseInt(reqAry[6]);
            
            /**add---shashijie 2013-02-17 STORY 3366 增加存储类型-公共表复选框字段*/
            this.saveType = reqAry[7];
            /**end---shashijie 2013-02-17 STORY 3366 */
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoDictBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析凭证资源信息出错!");
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.dictCode).append("\t");
        buf.append(this.DictName).append("\t");
        buf.append(this.srcConent).append("\t");
        buf.append(this.cnvConent).append("\t");
        buf.append(this.desc).append("\t");
        /**add---shashijie 2013-02-17 STORY 3366 增加存储类型-公共表复选框字段*/
        buf.append(this.saveType).append("\t");
        /**end---shashijie 2013-02-17 STORY 3366 */
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public void setSrcConent(String srcConent) {
        this.srcConent = srcConent;
    }

    public void setCnvConent(String cnvConent) {
        this.cnvConent = cnvConent;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setFilterType(DaoDictBean filterType) {
        this.filterType = filterType;
    }

    public void setOldDictCode(String OldDictCode) {
        this.OldDictCode = OldDictCode;
    }

    public void setDictName(String DictName) {
        this.DictName = DictName;
    }

    public String getDictCode() {
        return dictCode;
    }

    public String getSrcConent() {
        return srcConent;
    }

    public String getCnvConent() {
        return cnvConent;
    }

    public String getDesc() {
        return desc;
    }

    public DaoDictBean getFilterType() {
        return filterType;
    }

    public String getOldDictCode() {
        return OldDictCode;
    }

    public String getDictName() {
        return DictName;
    }

    /**修改时间：2008年05月06日
     * 修改人：韩冠男
     * 原方法功能：从回收站删除数据，即从数据库彻底删除数据
     * 新方法功能：从回收站删除数据，即从数据库彻底删除数据，并可以同时处理多条信息
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String reStr = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
        	/**add---shashijie 2013-2-22 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
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
                    
                    //获取删除语句
                    reStr = getDeleteSql(this.dictCode);
                    dbl.executeSql(reStr);
                }
            }
            //sRecycled如果sRecycled为空,而oldBankCode不为空,则按照oldBankCode来执行sql语句
            else if (dictCode != "" && dictCode != null) {
            	//获取删除语句
                reStr = getDeleteSql(this.dictCode);
                reStr += " and FSRCCONENT=" + dbl.sqlString(this.srcConent);
                //执行sql语句
                dbl.executeSql(reStr);
                
            }
            /**end---shashijie 2013-2-22 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
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

	/**add---shashijie 2013-2-22 返回 saveType 的值*/
	public String getSaveType() {
		return saveType;
	}

	/**add---shashijie 2013-2-22 传入saveType 设置  saveType 的值*/
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
}
