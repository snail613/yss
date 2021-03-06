package com.yss.main.compliance;

import java.sql.*;
import java.util.regex.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

import oracle.jdbc.*;
import oracle.sql.*;

public class CompIndexCfgBean
    extends BaseDataSettingBean implements IDataSetting {

    private String indexCfgCode = ""; //指标配置代码
    private String indexCfgName = ""; //指标配置名称
    private String indexType = ""; //指标类型
    private String indexTypeName = ""; //指标类型名称
    private String beanID = ""; //BeanID
    private String compParam = ""; //监控参数
    private String compParamName = "";
    private String repCode = ""; //关联报表代码
    private String repName = "";
    private String indexDS = ""; //指标数据源
    private String memoyWay = ""; //存储方式
    private String memoyWayName = ""; //储存方式名称
    private String tgtTableView = ""; //存储表或视图名
    private String beforeComp = ""; //事前监控
    private String beforeCompName = ""; //事前监控名称
    private String finalComp = ""; //日终监控
    private String finalCompName = ""; //日终监控名称
    private String warnAnalysis = ""; //预警分析脚本
    private String violateAnalysis = ""; //违规分析脚本
    private String forbidAnalysis = ""; //禁止分析脚本
    private String desc = ""; //描述
    private String oldIndexCfgCode = "";
    private String hRecycled = ""; //add by nimengjing 2010.12.10BUG #634 BUG #634 监控指标配置界面和监控范本界面的回收站中不能一次还原或清除多条数据  
    private CompIndexCfgBean filterType = null;

    public CompIndexCfgBean getFilterType() {
        return filterType;
    }

    
    
    public String gethRecycled() {
		return hRecycled;
	}



	public void sethRecycled(String hRecycled) {
		this.hRecycled = hRecycled;
	}



	public String getBeanID() {
        return beanID;
    }

    public String getBeforeComp() {
        return beforeComp;
    }

    public void setBeanID(String beanID) {
        this.beanID = beanID;
    }

    public void setBeforeComp(String beforeComp) {
        this.beforeComp = beforeComp;
    }

    public String getCompParam() {
        return compParam;
    }

    public String getDesc() {
        return desc;
    }

    public void setFilterType(CompIndexCfgBean filterType) {
        this.filterType = filterType;
    }

    public void setCompParam(String compParam) {
        this.compParam = compParam;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFinalComp() {
        return finalComp;
    }

    public String getForbidAnalysis() {
        return forbidAnalysis;
    }

    public String getIndexCfgCode() {
        return indexCfgCode;
    }

    public String getIndexCfgName() {
        return indexCfgName;
    }

    public String getIndexDS() {
        return indexDS;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setFinalComp(String finalComp) {
        this.finalComp = finalComp;
    }

    public void setForbidAnalysis(String forbidAnalysis) {
        this.forbidAnalysis = forbidAnalysis;
    }

    public void setIndexCfgCode(String indexCfgCode) {
        this.indexCfgCode = indexCfgCode;
    }

    public void setIndexCfgName(String indexCfgname) {
        this.indexCfgName = indexCfgname;
    }

    public void setIndexDS(String indexDS) {
        this.indexDS = indexDS;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public void setMemoyWay(String memoyWay) {
        this.memoyWay = memoyWay;
    }

    public void setRepCode(String repCode) {
        this.repCode = repCode;
    }

    public void setTgtTableView(String tgtTableView) {
        this.tgtTableView = tgtTableView;
    }

    public void setViolateAnalysis(String violateAnalysis) {
        this.violateAnalysis = violateAnalysis;
    }

    public void setWarnAnalysis(String warnAnalysis) {
        this.warnAnalysis = warnAnalysis;
    }

    public String getTgtTableView() {
        return tgtTableView;
    }

    public String getRepCode() {
        return repCode;
    }

    public String getMemoyWay() {
        return memoyWay;
    }

    public String getViolateAnalysis() {
        return violateAnalysis;
    }

    public String getWarnAnalysis() {
        return warnAnalysis;
    }

    public String getBeforeCompName() {
        return beforeCompName;
    }

    public void setBeforeCompName(String beforeCompName) {
        this.beforeCompName = beforeCompName;
    }

    public String getFinalCompName() {
        return finalCompName;
    }

    public void setFinalCompName(String finalCompName) {
        this.finalCompName = finalCompName;
    }

    public String getIndexTypeName() {
        return indexTypeName;
    }

    public void setIndexTypeName(String indexTypeName) {
        this.indexTypeName = indexTypeName;
    }

    public String getMemoyWayName() {
        return memoyWayName;
    }

    public void setMemoyWayName(String memoyWayName) {
        this.memoyWayName = memoyWayName;
    }

    public String getOldIndexCfgCode() {
        return oldIndexCfgCode;
    }

    public String getRepName() {
        return repName;
    }

    public String getCompParamName() {
        return compParamName;
    }

    public void setOldIndexCfgCode(String oldIndexCfgCode) {
        this.oldIndexCfgCode = oldIndexCfgCode;
    }

    public void setRepName(String repName) {
        this.repName = repName;
    }

    public void setCompParamName(String compParamName) {
        this.compParamName = compParamName;
    }

    public CompIndexCfgBean() {
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.indexCfgCode).append("\t");
        buf.append(this.indexCfgName).append("\t");
        buf.append(this.indexType).append("\t");
        buf.append(this.indexTypeName).append("\t");
        buf.append(this.beanID).append("\t");
        buf.append(this.compParam).append("\t");
        buf.append(this.compParamName).append("\t");
        buf.append(this.repCode).append("\t");
        buf.append(this.repName).append("\t");
        buf.append(this.indexDS).append("\t");
        buf.append(this.memoyWay).append("\t");
        buf.append(this.memoyWayName).append("\t");
        buf.append(this.tgtTableView).append("\t");
        buf.append(this.beforeComp).append("\t");
        buf.append(this.beforeCompName).append("\t");
        buf.append(this.finalComp).append("\t");
        buf.append(this.finalCompName).append("\t");
        buf.append(this.warnAnalysis).append("\t");
        buf.append(this.violateAnalysis).append("\t");
        buf.append(this.forbidAnalysis).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
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
            //modify  by nimengjing 2010.12.17 BUG #708 监控指标配置只能逐条还原数据且不能清除回收站中的数据 
            this.hRecycled=sRowStr;
            this.indexCfgCode = reqAry[0];
            this.indexCfgName = reqAry[1];
            this.indexType = reqAry[2];
            this.beanID = reqAry[3];
            this.compParam = reqAry[4];
            this.repCode = reqAry[5];
            if(reqAry[6]!=null){
            	if(reqAry[6].indexOf("【Enter】")>=0){
            		this.indexDS=reqAry[6].replaceAll("【Enter】", "\r\n");
            	}else{
                    this.indexDS = reqAry[6];
            	}
            }
            this.memoyWay = reqAry[7];
            this.tgtTableView = reqAry[8];
            this.beforeComp = reqAry[9];
            this.finalComp = reqAry[10];
            if(reqAry[11]!=null){
            	if(reqAry[11].indexOf("【Enter】")>=0){
            		this.warnAnalysis=reqAry[11].replaceAll("【Enter】", "\r\n");
            	}else{
                    this.warnAnalysis = reqAry[11];
            	}
            }
           //this.warnAnalysis = reqAry[11];
            if(reqAry[12]!=null){
            	if(reqAry[12].indexOf("【Enter】")>=0){
            		this.violateAnalysis=reqAry[12].replaceAll("【Enter】", "\r\n");
            	}else{
                    this.violateAnalysis = reqAry[12];
            	}
            }
            //this.violateAnalysis = reqAry[12];
            if(reqAry[13]!=null){
            	if(reqAry[13].indexOf("【Enter】")>=0){
            		this.forbidAnalysis=reqAry[13].replaceAll("【Enter】", "\r\n");
            	}else{
                    this.forbidAnalysis = reqAry[13];
            	}
            }
            //this.forbidAnalysis = reqAry[13];
            if (YssFun.isNumeric(reqAry[14])) {
                this.checkStateId = YssFun.toInt(reqAry[14]);
            }
            this.oldIndexCfgCode = reqAry[15];
            if(reqAry[16]!=null){
            	if(reqAry[16].indexOf("【Enter】")>=0){
            		this.desc=reqAry[16].replaceAll("【Enter】", "\r\n");
            	}else{
                    this.desc = reqAry[16];
            	}
            }
            //this.desc = reqAry[16]; //2008-6-16 单亮
            //------------------------------end BUG #708------------------------------------------------
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CompIndexCfgBean();
                    this.filterType.setYssPub(pub);
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析监控指标配置出错", e);
        }

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.indexCfgCode.length() != 0) {
                sResult = sResult + " and a.FIndexCfgCode like '" +
                    filterType.indexCfgCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.indexCfgName.length() != 0) {
                sResult = sResult + " and a.FIndexCfgName like '" +
                    filterType.indexCfgName.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.indexType.equalsIgnoreCase("ALL")) {
                sResult = sResult + " and a.FIndexType = " +
                    dbl.sqlString(filterType.indexType);
            }
            if (this.filterType.beanID.length() != 0) {
                sResult = sResult + " and a.FBeanId like '" +
                    filterType.beanID.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.compParam.length() != 0) {
                sResult = sResult + " and a.FCompParam like '" +
                    filterType.compParam.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.repCode.length() != 0) {
                sResult = sResult + " and a.FRepCode like '" +
                    filterType.repCode.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.memoyWay.equalsIgnoreCase("ALL")) {
                sResult += " and FMemoyWay =" + this.filterType.memoyWay;
            }
            if (this.filterType.tgtTableView.length() != 0) {
                sResult += " and FTgtTableView like '" +
                    filterType.tgtTableView.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.beforeComp.equalsIgnoreCase("ALL")) {
                sResult += " and FBeforeComp = " + this.filterType.beforeComp;
            }
            if (!this.filterType.finalComp.equalsIgnoreCase("ALL")) {
                sResult += " and FFinalComp = " + this.filterType.finalComp;
            }
        }
        return sResult;
    }

    public String getOperValue(String sType) throws YssException {
        try {
            if (sType.equalsIgnoreCase("testDS")) {
                this.testDataSource();
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return " ";
    }

    public String getAllSetting() {
        return "";
    }

    public IDataSetting getSetting() {
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    public String addSetting() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        String strSql = "";
        String strUpdate = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        try {
            bufSql.append("INSERT INTO " + pub.yssGetTableName("Tb_Comp_IndexCfg"));
            bufSql.append(" (FIndexCfgCode,FIndexCfgName,FIndexType,FBeanId,FCompParam,FRepCode,FIndexDS,FMemoyWay,FTgtTableView,");
            bufSql.append("FBeforeComp,FFinalComp,FWarnAnalysis,FViolateAnalysis,FForbidAnalysis,FDesc,FCheckState,FCreator,FCreateTime)");
            bufSql.append(" VALUES(");
            bufSql.append(dbl.sqlString(this.indexCfgCode) + ",");
            bufSql.append(dbl.sqlString(this.indexCfgName) + ",");
            bufSql.append(dbl.sqlString(this.indexType) + ",");
            bufSql.append(dbl.sqlString(this.beanID) + ",");
            bufSql.append(dbl.sqlString(this.compParam) + ",");
            bufSql.append(dbl.sqlString(this.repCode) + ",");
            if (dbl.dbType == YssCons.DB_ORA) {
                bufSql.append("EMPTY_CLOB()" + ",");
            } else {
                bufSql.append(dbl.sqlString(this.indexDS) + ",");
            }
            bufSql.append(dbl.sqlString(this.memoyWay) + ",");
            bufSql.append(dbl.sqlString(this.tgtTableView) + ",");
            bufSql.append(dbl.sqlString(this.beforeComp) + ",");
            bufSql.append(dbl.sqlString(this.finalComp) + ",");
            bufSql.append(dbl.sqlString(this.warnAnalysis) + ",");
            bufSql.append(dbl.sqlString(this.violateAnalysis) + ",");
            bufSql.append(dbl.sqlString(this.forbidAnalysis) + ",");
            bufSql.append(dbl.sqlString(this.desc) + ",");
            bufSql.append(this.checkStateId).append(",");
            bufSql.append(dbl.sqlString(this.creatorCode) + ",");
            bufSql.append(dbl.sqlString(this.creatorTime)).append(")");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());

            if (dbl.dbType == YssCons.DB_ORA) {
                strSql = "SELECT FIndexDS FROM " +
                    pub.yssGetTableName("Tb_Comp_IndexCfg") +
                    " WHERE FIndexCfgCode = " + dbl.sqlString(this.indexCfgCode);
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                	//STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	// modify by jsc 20120809 连接池对大对象的特殊处理
                    //CLOB clob = ( (OracleResultSet) rs).getCLOB("FIndexDS");
                	CLOB clob = dbl.CastToCLOB(rs.getClob("FIndexDS"));
                    clob.putString(1, this.indexDS);
                    strUpdate = "UPDATE " + pub.yssGetTableName("Tb_Comp_IndexCfg") +
                        " SET FIndexDS = ? WHERE FIndexCfgCode = " +
                        dbl.sqlString(this.indexCfgCode);
                    PreparedStatement pstmt = conn.prepareStatement(strUpdate);
                    pstmt.setClob(1, clob);
                    pstmt.executeUpdate();
                    pstmt.close();
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增监控指标配置信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCfg") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FIndexCfgCode = " + dbl.sqlString(this.indexCfgCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除监控指标配置信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    //add  by nimengjing 2010.12.17 BUG #708 监控指标配置只能逐条还原数据且不能清除回收站中的数据
    public void deleteRecycleData() throws YssException {
    	String strSql="";
    	boolean bTrans = false; // 代表是否开始了事务
		Connection con = dbl.loadConnection();
		try {
			con.setAutoCommit(false);
			bTrans = true;
			String[] arrData = hRecycled.split("\r\n");
			for(int i=0;i<arrData.length;i++){
				if(arrData.length==0){
					continue;
				}
				this.parseRowStr(arrData[i]);
				strSql="delete from "
					+pub.yssGetTableName("Tb_Comp_IndexCfg")
					+" where FIndexCfgCode="+dbl.sqlString(this.indexCfgCode);
				dbl.executeSql(strSql);
			}
			con.commit();
			bTrans = false;
			con.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("清除凭证分录信息出错!");
		} finally {
			dbl.endTransFinal(con, bTrans);
		}
       
    }
    //------------------------------end BUG #708------------------------------------------------
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Comp_IndexCfg"),
                               "FIndexCfgCode",
                               this.indexCfgCode, this.oldIndexCfgCode);
    }

    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        String[] arrData=null;
        try {
        	//modify by nimengjing 2010.12.17 BUG #708 监控指标配置只能逐条还原数据且不能清除回收站中的数据 
        	if(hRecycled!=null && hRecycled.length()>0){
        		arrData=hRecycled.split("\r\n");
        	}
        	
			//---add by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
			if(arrData == null){
				return;
			}
			//---add by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
        	
        	for(int i=0;i<arrData.length;i++){
        		this.parseRowStr(arrData[i]);
                strSql = "UPDATE " + pub.yssGetTableName("Tb_Comp_IndexCfg") +
                " SET FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FIndexCfgCode = " + dbl.sqlString(this.indexCfgCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
        	}
        	//--------------------------------end #708----------------------------------------------------
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核监控指标配置信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String editSetting() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        String strSql = "";
        String strUpdate = "";
        ResultSet rs = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            bufSql.append("UPDATE " + pub.yssGetTableName("Tb_Comp_IndexCfg"));
            bufSql.append(" SET FIndexCfgCode = " +
                          dbl.sqlString(this.indexCfgCode) + ",");
            bufSql.append("FIndexCfgName = " + dbl.sqlString(this.indexCfgName) +
                          ",");
            bufSql.append("FIndexType = " + dbl.sqlString(this.indexType) + ",");
            bufSql.append("FBeanId = " + dbl.sqlString(this.beanID) + ",");
            bufSql.append("FCompParam = " + dbl.sqlString(this.compParam) + ",");
            bufSql.append("FRepCode = " + dbl.sqlString(this.repCode) + ",");
            if (dbl.dbType == YssCons.DB_ORA) {
                bufSql.append("FIndexDS = EMPTY_CLOB(),");
            } else {
                bufSql.append("FIndexDS = " + dbl.sqlString(this.indexDS) + ",");
            }
            bufSql.append("FMemoyWay = " + dbl.sqlString(this.memoyWay) + ",");
            bufSql.append("FTgtTableView = " + dbl.sqlString(this.tgtTableView) +
                          ",");
            bufSql.append("FBeforeComp = " + dbl.sqlString(this.beforeComp) + ",");
            bufSql.append("FFinalComp = " + dbl.sqlString(this.finalComp) + ",");
            bufSql.append("FWarnAnalysis = " + dbl.sqlString(this.warnAnalysis) +
                          ",");
            bufSql.append("FViolateAnalysis = " +
                          dbl.sqlString(this.violateAnalysis) + ",");
            bufSql.append("FForbidAnalysis = " + dbl.sqlString(this.forbidAnalysis) +
                          ",");
            bufSql.append("FDesc = " + dbl.sqlString(this.desc) + ",");
            bufSql.append("FCreator = " + dbl.sqlString(this.creatorCode) + ",");
            bufSql.append("FCreateTime = " + dbl.sqlString(this.creatorTime));
            bufSql.append(" WHERE FIndexCfgCode = " +
                          dbl.sqlString(this.oldIndexCfgCode));

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());

            if (dbl.dbType == YssCons.DB_ORA) {
                strSql = "SELECT FIndexDS FROM " +
                    pub.yssGetTableName("Tb_Comp_IndexCfg") +
                    " WHERE FIndexCfgCode = " + dbl.sqlString(this.indexCfgCode);
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                	//STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	// modify by jsc 20120809 连接池对大对象的特殊处理
                    //CLOB clob = ( (OracleResultSet) rs).getCLOB("FIndexDS");
                	CLOB clob = dbl.CastToCLOB(rs.getClob("FIndexDS"));
                    clob.putString(1, this.indexDS);
                    strUpdate = "UPDATE " + pub.yssGetTableName("Tb_Comp_IndexCfg") +
                        " SET FIndexDS = ? WHERE FIndexCfgCode = " +
                        dbl.sqlString(this.indexCfgCode);
                    PreparedStatement pstmst = conn.prepareStatement(strUpdate);
                    pstmst.setClob(1, clob);
                    pstmst.executeUpdate();
                    pstmst.close();
                }
                rs.close();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改监控指标配置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public String getListViewData1() throws YssException {
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String sHeader = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "SELECT a.*, b.FUserName AS FCreatorName, c.FUserName AS FCheckUserName, " +
                " m.FVocName AS FIndexTypeName, n.FVocName AS FMemoyWayName, o.FVocName AS FBefore, p.FVocName AS FFinal" +
                " ,d.FCtlGrpName as FCompParamName,e.FTableDesc as FTgtTableViewName,f.FCusRepName as FRepName " +
                " FROM " + pub.yssGetTableName("Tb_Comp_IndexCfg") + " a " +
                " LEFT JOIN (SELECT FUserCode, FUserName FROM Tb_Sys_UserList) b " +
                " ON a.FCreator = b.FUserName " +
                " LEFT JOIN (SELECT FUserCode, FUserName FROM Tb_Sys_UserList) c " +
                " ON a.FCheckUser = c.FUserName " +
                " LEFT JOIN Tb_Fun_Vocabulary m ON a.FIndexType = m.FVocCode AND m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CIC_INDEXTYPE) +
                " LEFT JOIN Tb_Fun_Vocabulary n ON a.FMemoyWay = n.FVocCode AND n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CIC_MEMOYWAY) +
                " LEFT JOIN Tb_Fun_Vocabulary o ON a.FBeforeComp = o.FVocCode AND o.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CIC_BEFORECOMP) +
                " LEFT JOIN Tb_Fun_Vocabulary p ON a.FFinalComp = p.FVocCode AND p.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CIC_FINALCOMP) +
                " left join (select distinct(FCtlGrpCode),FCtlGrpName from Tb_PFSys_FaceCfgInfo where FFunModules='CompParam') d on a.FCompParam = d.FCtlGrpCode " +
                " left join (select distinct(FTabName),FTableDesc from TB_FUN_DATADICT) e on a.FTgtTableView = e.FTabName " +
                " left join " + pub.yssGetTableName("tb_rep_custom") + " f on a.FRepCode = f.FCusRepCode " +
                buildFilterSql() + " ORDER BY a.FCheckState, a.FCreateTime DESC";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.indexCfgCode = rs.getString("FIndexCfgCode");
                this.indexCfgName = rs.getString("FIndexCfgName");
                this.indexType = rs.getString("FIndexType");
                this.indexTypeName = rs.getString("FIndexTypeName");
                this.beanID = rs.getString("FBeanId") + "";
                this.compParam = rs.getString("FCompParam");
                this.compParamName = rs.getString("FCompParamName");
                this.repCode = rs.getString("FRepCode") + "";
                this.repName = rs.getString("FRepName");
                this.indexDS = dbl.clobStrValue(rs.getClob("FIndexDS"));
                this.memoyWay = rs.getString("FMemoyWay");
                this.memoyWayName = rs.getString("FMemoyWayName");
                this.tgtTableView = rs.getString("FTgtTableView") + "";
                this.beforeComp = rs.getString("FBeforeComp");
                this.beforeCompName = rs.getString("FBefore");
                this.finalComp = rs.getString("FFinalComp");
                this.finalCompName = rs.getString("FFinal");
                this.warnAnalysis = rs.getString("FWarnAnalysis") + "";
                this.violateAnalysis = rs.getString("FViolateAnalysis") + "";
                this.forbidAnalysis = rs.getString("FForbidAnalysis") + "";
                this.desc = rs.getString("FDesc") + "";

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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_CIC_INDEXTYPE + "," +
                                        YssCons.YSS_CIC_MEMOYWAY + "," +
                                        YssCons.YSS_CIC_BEFORECOMP + "," +
                                        YssCons.YSS_CIC_FINALCOMP);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取监控指标配置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String sHeader = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "指标配置代码\t指标配置名称\t控件组代码";
            strSql =
                "SELECT a.*, b.FUserName AS FCreatorName, c.FUserName AS FCheckUserName, " +
                " m.FVocName AS FIndexTypeName, n.FVocName AS FMemoyWayName, o.FVocName AS FBefore, p.FVocName AS FFinal" +
                " ,d.FCtlGrpName as FCompParamName,e.FTableDesc as FTgtTableViewName,f.FCusRepName as FRepName " +
                " FROM " + pub.yssGetTableName("Tb_Comp_IndexCfg") + " a " +
                " LEFT JOIN (SELECT FUserCode, FUserName FROM Tb_Sys_UserList) b " +
                " ON a.FCreator = b.FUserName " +
                " LEFT JOIN (SELECT FUserCode, FUserName FROM Tb_Sys_UserList) c " +
                " ON a.FCheckUser = c.FUserName " +
                " LEFT JOIN Tb_Fun_Vocabulary m ON a.FIndexType = m.FVocCode AND m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CIC_INDEXTYPE) +
                " LEFT JOIN Tb_Fun_Vocabulary n ON a.FMemoyWay = n.FVocCode AND n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CIC_MEMOYWAY) +
                " LEFT JOIN Tb_Fun_Vocabulary o ON a.FBeforeComp = o.FVocCode AND o.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CIC_BEFORECOMP) +
                " LEFT JOIN Tb_Fun_Vocabulary p ON a.FFinalComp = p.FVocCode AND p.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CIC_FINALCOMP) +
                " left join (select distinct(FCtlGrpCode),FCtlGrpName from Tb_PFSys_FaceCfgInfo where FFunModules='CompParam') d on a.FCompParam = d.FCtlGrpCode " +
                " left join (select distinct(FTabName),FTableDesc from TB_FUN_DATADICT) e on a.FTgtTableView = e.FTabName " +
                " left join " + pub.yssGetTableName("tb_rep_custom") + " f on a.FRepCode = f.FCusRepCode " +
                (buildFilterSql().length() > 0 ? buildFilterSql() + " and a.FCheckState=1" : " where a.FCheckState=1") +
                " ORDER BY a.FCheckState, a.FCreateTime DESC";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FIndexCfgCode")).append("\t");
                bufShow.append(rs.getString("FIndexCfgName")).append("\t");
                bufShow.append(rs.getString("FCompParam")).
                    append(YssCons.YSS_LINESPLITMARK);

                this.indexCfgCode = rs.getString("FIndexCfgCode");
                this.indexCfgName = rs.getString("FIndexCfgName");
                this.indexType = rs.getString("FIndexType");
                this.beanID = rs.getString("FBeanId") + "";
                this.compParam = rs.getString("FCompParam");
                this.compParamName = rs.getString("FCompParamName");
                this.repCode = rs.getString("FRepCode") + "";
                this.repName = rs.getString("FRepName");
                this.indexDS = dbl.clobStrValue(rs.getClob("FIndexDS"));
                this.memoyWay = rs.getString("FMemoyWay");
                this.tgtTableView = rs.getString("FTgtTableView") + "";
                this.beforeComp = rs.getString("FBeforeComp");
                this.finalComp = rs.getString("FFinalComp");
                this.warnAnalysis = rs.getString("FWarnAnalysis") + "";
                this.violateAnalysis = rs.getString("FViolateAnalysis") + "";
                this.forbidAnalysis = rs.getString("FForbidAnalysis") + "";
                this.desc = rs.getString("FDesc") + "";

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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_ECG_DVPIND + "," +
                                        YssCons.YSS_SCY_SDAYTYPE);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取监控指标配置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData3() throws YssException {
        try {

        } catch (Exception e) {
            throw new YssException("");
        }
        return "";
    }

    public String getListViewData4() throws YssException {
        try {

        } catch (Exception e) {
            throw new YssException("");
        }
        return "";
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

    /**
     * 用于测试数据源 SQL 的合法性
     * @throws YssException
     */
    public void testDataSource() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = this.indexDS;
            //替换数据源中的组合标示
            strSql = strSql.replaceAll("S<port>", dbl.sqlString("001"));
            //替换开始时间
            strSql = strSql.replaceAll("D<startdate>", dbl.sqlDate(new java.util.Date()));
            //替换结束时间
            strSql = strSql.replaceAll("D<enddate>", dbl.sqlDate(new java.util.Date()));
            //替换组合群代码 2008.06.16 蒋锦 添加
            strSql = Pattern.compile("<group>", Pattern.CASE_INSENSITIVE).matcher(strSql).replaceAll(pub.getAssetGroupCode());

            strSql = strSql.replaceAll("([D][<](\\w)+[>])", dbl.sqlDate(new java.util.Date()));
            strSql = strSql.replaceAll("([S][<](\\w)+[>])", dbl.sqlString("s"));
            strSql = strSql.replaceAll("([I][<](\\w)+[>])", "1");
            strSql = strSql.replaceAll("([N][<](\\w)+[>])", dbl.sqlString("s"));
            //strSql = strSql.replaceAll("(WDay){1}[\\[](.)*[;](.)*[;](.)*[\\]]", "2003-01-01");

            rs = dbl.openResultSet(strSql);

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
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
