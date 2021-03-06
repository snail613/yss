package com.yss.main.platform.pfsystem.inoutcfg;

import java.sql.*;

import oracle.jdbc.OracleResultSet; //------ add by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
import oracle.sql.CLOB; //------ add by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

//Tb_PF_InOutCfg 通用导入导出配置
public class InOutCfgBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strInOutCode = ""; //配置编号
    private String strInOutName = ""; //配置名称
    private String strInCfgScript = ""; //配置导入脚本
    private String strOutCfgScript = ""; //配置导出脚本
    private String strDesc = ""; //描述
    private InOutCfgBean filterType;
    private String sRecycled = "";
    private String strOldInOutCode = "";

    public InOutCfgBean() {
    }

    private String builerFilter() {
        String reSql = "";
        if (this.filterType != null) {
            reSql = " where 1=1";
            if (filterType.strInOutCode != null && this.filterType.strInOutCode.trim().length() != 0) {
                reSql += " and a.FInOutCode = '" +
                    filterType.strInOutCode + "'";
            }
            if (filterType.strInOutName != null && this.filterType.strInOutName.trim().length() != 0) {
                reSql += " and a.FInOutName like '" +
                    filterType.strInOutName.replaceAll("'", "''") + "%'";
            }
            if (filterType.strInCfgScript != null && this.filterType.strInCfgScript.trim().length() != 0) {
                reSql += " and a.FInCfgScript like '" +
                    filterType.strInCfgScript.replaceAll("'", "''") + "%'";
            }
            if (filterType.strOutCfgScript != null && this.filterType.strOutCfgScript.trim().length() != 0) {
                reSql += " and a.FOutCfgScript like '" +
                    filterType.strOutCfgScript.replaceAll("'", "''") + "%'";
            }

            if (filterType.strDesc != null && this.filterType.strDesc.trim().length() != 0) {
                reSql += " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }
        }
        return reSql;
    }

    public void setRepAttr(ResultSet rs) throws SQLException, YssException  {
        this.strInOutCode = rs.getString("FInOutCode") + "";
        this.strInOutName = rs.getString("FInOutName") + "";
        //------ modify by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
        this.strOutCfgScript = dbl.clobStrValue(rs.getClob("FOutCfgScript")).replaceAll("\t", "   ");
        this.strInCfgScript = dbl.clobStrValue(rs.getClob("FInCfgScript")).replaceAll("\t", "   ");
        //----------------------BUG #389 导入 通用信息配置.mdb 时报错 ---------------------------//
        this.strDesc = rs.getString("FDesc");
        super.setRecLog(rs);
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = getListView1Headers();
            sqlStr =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_PFSys_InOutCfg " +
                " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                builerFilter() + " order by a.FCheckState, a.FCreateTime desc, a.FCheckTime desc";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setRepAttr(rs);
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
            throw new YssException("获取通用导入导出配置出错!", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "配置代码\t配置名称";
            sqlStr =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_PFSys_InOutCfg " +
                " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " where 1=1 and a.Fcheckstate =1 order by a.FCheckState, a.FCreateTime desc, a.FCheckTime desc";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FInOutCode")).append(YssCons.YSS_ITEMSPLITMARK1);
                bufShow.append(rs.getString("FInOutName")).
                    append(YssCons.YSS_LINESPLITMARK);
                setRepAttr(rs);
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

        } catch (Exception ex) {
            throw new YssException("获取导入数据配置信息出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
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
    public String getListViewData4() {
        return "";
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String strSql = "";
        ResultSet rs = null; //------ add by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
        
        try {
            conn = dbl.loadConnection();
            //------ modify by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
            strSql = "insert into Tb_PFSys_InOutCfg (FInOutCode,FInOutName,FInCfgScript,FOutCfgScript," +
                "FDesc,FCheckState,FCreator,FCreateTime) values(" +
                dbl.sqlString(this.strInOutCode) + "," +
                dbl.sqlString(this.strInOutName) + ",";
                
                if (dbl.getDBType() == YssCons.DB_ORA) { //添加了数据库类型的判断，不同类型数据库的CLOB类型插入方法不同
                    strSql = strSql + "EMPTY_CLOB()" + "," + "EMPTY_CLOB()" + "," ;
                } else {
                    strSql = strSql + dbl.sqlString(this.strInCfgScript) + ","
                    			+ dbl.sqlString(this.strOutCfgScript) + ",";
                }
            	
                strSql = strSql +
	                dbl.sqlString(this.strDesc) + "," +
	                (pub.getSysCheckState() ? "0" : "1") + "," +
	                //------ modify by wangzuochun 2010.11.12 BUG #397 通用导入导出界面问题 
	                dbl.sqlString(this.creatorCode) + "," +//xuqiji 2010-02-08 MS00954 :QDV4华夏2010年01月26日01_B  
	                "'" + YssFun.formatDatetime(new java.util.Date()) + "')";
	               //-----------------------BUG #397----------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            
            //------ add by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
            if (dbl.getDBType() == YssCons.DB_ORA) {
                strSql = "select FInCfgScript,FOutCfgScript from Tb_PFSys_InOutCfg " + 
                    " where FInOutCode = " + dbl.sqlString(this.strInOutCode);
                rs = dbl.openResultSet(strSql);
                
                if (rs.next()) {
                	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	  // modify by jsc 20120809 连接池对大对象的特殊处理
                	CLOB clobIn = dbl.CastToCLOB(rs.getClob("FInCfgScript"));
                    //CLOB clobIn = ((OracleResultSet) rs).getCLOB("FInCfgScript");
                    clobIn.putString(1, this.strInCfgScript);
                    
                    //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
              	  // modify by jsc 20120809 连接池对大对象的特殊处理
                    CLOB clobOut = dbl.CastToCLOB(rs.getClob("FOutCfgScript"));
                    //CLOB clobOut = ((OracleResultSet) rs).getCLOB("FOutCfgScript");
                    /**shashijie 2011-10-13 BUG 2229*/
                    clobOut.putString(1, this.strOutCfgScript);//放下标,都是以1开始
                    /**end*/
                    
                    String sql = "update Tb_PFSys_InOutCfg " +
                        	" set FInCfgScript=? , FOutCfgScript=? where FInOutCode = " +
                        	dbl.sqlString(this.strInOutCode);
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setClob(1, clobIn);
                    pstmt.setClob(2, clobOut);
                    pstmt.executeUpdate();
                    pstmt.close();
                }
            }
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增通用导入导出配置出错", e);
        } finally {
        	dbl.closeResultSetFinal(rs); //------ add by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_PFSys_InOutCfg", "FInOutCode",
                               this.strInOutCode, this.strOldInOutCode);
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] arrData = sRecycled.split("\r\n");
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                parseRowStr(arrData[i]);
                strSql = "update Tb_PFSys_InOutCfg set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "' where FInOutCode = " + dbl.sqlString(this.strInOutCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核通用导入导出配置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update Tb_PFSys_InOutCfg set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FInOutCode = " +
                dbl.sqlString(this.strInOutCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除通用导入导出配置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String[] arrData = sRecycled.split("\r\n");
        String strSql = "";
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                parseRowStr(arrData[i]);
                strSql = "delete from Tb_PFSys_InOutCfg where FInOutCode = " + dbl.sqlString(strInOutCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = true;
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            try {
            	if(conn!=null){
            		//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
            		 conn.rollback();
            	}
               
            } catch (Exception e) {
            }
            throw new YssException("清除数据出错", ex);
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
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false;
        ResultSet rs = null; //------ add by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
        try {
            conn = dbl.loadConnection();
			//------ modify by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
            strSql = "update Tb_PFSys_InOutCfg" +
                " set FInOutCode=" + dbl.sqlString(this.strInOutCode) + "," +
                " FInOutName=" + dbl.sqlString(this.strInOutName) + ",";
            
            if (dbl.getDBType() == YssCons.DB_ORA) { //添加了数据库类型的判断，不同类型数据库的CLOB类型插入方法不同
                strSql = strSql + "FOutCfgScript = EMPTY_CLOB(),FInCfgScript = EMPTY_CLOB(),";
            } else {
                strSql = strSql + "FOutCfgScript = " + dbl.sqlString(this.strOutCfgScript) 
                		+ ",FInCfgScript = " + dbl.sqlString(this.strInCfgScript) + ",";
            }
                
            strSql = strSql + 
                " FDesc=" + dbl.sqlString(this.strDesc) + "," +
                " FCheckState = " + (pub.getSysCheckState() ? "0" : "1") + "," +
                " FCreator = " + dbl.sqlString(this.creatorCode) + "," +
                " FCreateTime = " + dbl.sqlString(this.creatorTime) + "," +
                " FCheckUser = " + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FInOutCode=" + dbl.sqlString(this.strOldInOutCode);
			//----------------------------------BUG #389 -------------------------------//	
		    
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            
            //------ add by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
            if (dbl.getDBType() == YssCons.DB_ORA) {
                strSql = "select FInCfgScript,FOutCfgScript from Tb_PFSys_InOutCfg " + 
                    " where FInOutCode = " + dbl.sqlString(this.strInOutCode);
                rs = dbl.openResultSet(strSql);
                
                if (rs.next()) {
                	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	  // modify by jsc 20120809 连接池对大对象的特殊处理
                	CLOB clobIn = dbl.CastToCLOB(rs.getClob("FInCfgScript"));
                    //CLOB clobIn = ((OracleResultSet) rs).getCLOB("FInCfgScript");
                    clobIn.putString(1, this.strInCfgScript);
                    
                  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
              	  // modify by jsc 20120809 连接池对大对象的特殊处理
                    CLOB clobOut = dbl.CastToCLOB(rs.getClob("FOutCfgScript"));
                    //CLOB clobOut = ((OracleResultSet) rs).getCLOB("FOutCfgScript");
                    /**shashijie 2011-10-13 BUG 2229*/
                    clobOut.putString(1, this.strOutCfgScript);//放下标,都是以1开始
                    /**end*/
                    String sql = "update Tb_PFSys_InOutCfg " +
                        	" set FInCfgScript=? , FOutCfgScript=? where FInOutCode = " +
                        	dbl.sqlString(this.strInOutCode);
                    
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setClob(1, clobIn);
                    pstmt.setClob(2, clobOut);
                    pstmt.executeUpdate();
                    pstmt.close();
                }
            }
			//----------------------------------BUG #389 -------------------------------//
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改通用导入导出配置出错", e);
        }
		//------ add by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
        finally {
        	dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
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
    public IDataSetting getSetting() throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        try {
        	sqlStr = "select * from TB_PFSys_INOUTCFG where FInOutCode =" + dbl.sqlString(strInOutCode);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                this.strInOutName = rs.getString("FInOutName");
				//------ modify by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
                this.strOutCfgScript = dbl.clobStrValue(rs.getClob("FOutCfgScript"));
                this.strInCfgScript = dbl.clobStrValue(rs.getClob("FInCfgScript"));
				//-------------  BUG #389 导入 通用信息配置.mdb 时报错 --------------//
                
                this.strDesc = rs.getString("FDesc");
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
        buf.append(this.strInOutCode).append("\t");
        buf.append(this.strInOutName).append("\t");
        buf.append(this.strOutCfgScript).append("\t");
        buf.append(this.strInCfgScript).append("\t");
        buf.append(this.strDesc).append("\t");
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
//        if (sType.equalsIgnoreCase("getData")) {
//
//        }
        return "";
    }

    /**
     * parseRowStr
     * 解析数据
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
                sTmpStr = sRowStr.split("\r\f")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sTmpStr;
            reqAry = sTmpStr.split("\t");
            this.strInOutCode = reqAry[0];
            this.strInOutName = reqAry[1];
            /**shashijie 2012-3-9 BUG 3837 通用导入导出配置的问题 */
            if (reqAry.length > 2 ) {
            	this.strOutCfgScript = reqAry[2].replaceAll("【Enter】", "\r\n");
                this.strInCfgScript = reqAry[3].replaceAll("【Enter】", "\r\n");
			} else {
				return ;
			}
			/**end*/
            //------ add by wangzuochun 2010.11.12 BUG #397 通用导入导出界面问题 
            if (reqAry[4] != null ){
            	if (reqAry[4].indexOf("【Enter】") >= 0){
            		this.strDesc = reqAry[4].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.strDesc = reqAry[4];
            	}
            }
            //----------------- BUG #397 ----------------//
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.strOldInOutCode = reqAry[6];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new InOutCfgBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析导入导出配置出错", e);
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

    public InOutCfgBean getFilterType() {
        return filterType;
    }

    public void setFilterType(InOutCfgBean filterType) {
        this.filterType = filterType;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public String getStrInOutCode() {
        return strInOutCode;
    }

    public void setStrInOutCode(String strInOutCode) {
        this.strInOutCode = strInOutCode;
    }

    public String getStrInOutName() {
        return strInOutName;
    }

    public void setStrInOutName(String strInOutName) {
        this.strInOutName = strInOutName;
    }

    public String getStrOldInOutCode() {
        return strOldInOutCode;
    }

    public String getStrOutCfgScript() {
        return strOutCfgScript;
    }

    public String getStrInCfgScript() {
        return strInCfgScript;
    }

    public void setStrOldInOutCode(String strOldInOutCode) {
        this.strOldInOutCode = strOldInOutCode;
    }

    public void setStrOutCfgScript(String strOutCfgScript) {
        this.strOutCfgScript = strOutCfgScript;
    }

    public void setStrInCfgScript(String strInCfgScript) {
        this.strInCfgScript = strInCfgScript;
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
