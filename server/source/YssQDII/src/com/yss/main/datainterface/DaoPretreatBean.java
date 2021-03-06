package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

import oracle.sql.*;

///预处理接口数据源配置
public class DaoPretreatBean
    extends BaseDataSettingBean implements IDataSetting {
    private String dPDsCode = ""; //数据源代码
    private String sRecycled = ""; //保存未解析前的字符串
    private String dPDsName = ""; //数据源名称
    private int dsType = 99; //数据源类型,更改为默认值 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
    private String targetTabCode = ""; //目标表
    private boolean bIsShow = false;
    private String targetTabName = "";
    //MS01337   add by zhangfa 2010.06.30   QDV4海富通2010年06月22日02_AB  
    private String mGroupshare="";
    //------------------------------------------------------------------
    private String beanId = ""; //配置的BeanID
    private String dataSource = ""; //数据源
    private String desc = ""; //描述
    private String dPreTreatRecycled = ""; //回收站
    private String oldDPDsCode = "";
    private String relaCompareCode = ""; //关联核对源代码 by leeyu 20090402 QDV4深圳2009年01月13日01_RA MS00192
    private DaoPretreatBean filterType;
    
    private String showImpNum="";//导入时是否提示数据量    'true'表示是,add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 

    /**add---shashijie 2013-2-25 STORY 3366 增加存储类型-公共表复选框*/
	private String saveType = "0";//默认不选中状态
	/**end---shashijie 2013-2-25 STORY 3366*/
	
    //---add by yangshaokai 2011.12.31 STORY 2007 
	private String AssetGroupName = "";	//组合群名称
	
	public String getAssetGroupName() {
		return AssetGroupName;
	}
	public void setAssetGroupName(String assetGroupName) {
		AssetGroupName = assetGroupName;
	}
	//------------end----------------------------
    
	public String getShowImpNum() {
		return showImpNum;
	}

	public void setShowImpNum(String showImpNum) {
		this.showImpNum = showImpNum;
	}

	//MS01337   add by zhangfa 2010.06.30   QDV4海富通2010年06月22日02_AB  
    public String ismGroupshare() {
		return mGroupshare;
	}

	public void setmGroupshare(String mGroupshare) {
		this.mGroupshare = mGroupshare;
	}
	//---------------------------------------------------------------------
	public DaoPretreatBean() {
    }

    public String getDPDsCode() {
        return this.dPDsCode;
    }

    public void setDPDsCode(String dPDsCode) {
        this.dPDsCode = dPDsCode;
    }

    public String getDPDsName() {
        return this.dPDsName;
    }

    public void setDPDsName(String dPDsName) {
        this.dPDsName = dPDsName;
    }

    public int getDsType() {
        return this.dsType;
    }

    public void setDsType(int dsType) {
        this.dsType = dsType;
    }

    public String getTargetTabCode() {
        return this.targetTabCode;
    }

    public void setTargetTabCode(String targetTab) {
        this.targetTabCode = targetTab;
    }

    public String getBeanId() {
        return this.beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getOldDPDsCode() {
        return oldDPDsCode;
    }

    public String getTargetTabName() {
        return targetTabName;
    }

    /**
     * relaCompareCode Getting method
     * by leeyu 20090402 QDV4深圳2009年01月13日01_RA MS00192
     * @return String
     */
    public String getRelaCompareCode() {
        return relaCompareCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldDPDsCode(String oldDPDsCode) {
        this.oldDPDsCode = oldDPDsCode;
    }

    public void setTargetTabName(String targetTabName) {
        this.targetTabName = targetTabName;
    }
    
    //---add by yangshaokai 2011.12.31 STORY 2007 
    private String getGroupNameFromGroupCode(String FAssetGroupCode) throws YssException {
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		String[] groupCode = null;
		String requestGroupCode = "";
		try {
			if(FAssetGroupCode == null){
				return " ";
			}
			groupCode = FAssetGroupCode.split(",");
			for (int i = 0;i<groupCode.length;i++) {
				requestGroupCode = requestGroupCode +"'" + groupCode[i] + "',";
			}
			strSql = "select * from tb_sys_AssetGroup where FAssetGroupCode in (" + requestGroupCode.substring(0,requestGroupCode.length() - 1) + ")";
			rs = dbl.openResultSet(strSql);
			while(rs.next()) {
				sReturn = sReturn + rs.getString("FAssetGroupName") + ",";
			}
			return ("".equals(sReturn.trim()) ? "" : sReturn.substring(0, sReturn.length() - 1));
		}
		catch(Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
    
	public String returnAssetGroupName() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String sReturn = "";
		try {
			if ("".equals(this.mGroupshare.trim())) {
				return "";
			}
			String[] sGroupCode = this.mGroupshare.trim().split(",");
			for (int i = 1;i <= sGroupCode.length;i++ ) {
				strSql = "SELECT FAssetGroupName FROM tb_sys_AssetGroup where FAssetGroupCode = '" + sGroupCode[i-1] + "'";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					sReturn = sReturn + rs.getString("FAssetGroupName") + ",";
				}
			}
			return ("".equals(sReturn.trim()) ? "" : sReturn.substring(0, sReturn.length() - 1));
		}
		catch(Exception e) {
			throw new YssException("获取组合群名称出错：" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
    //---------------end--------------------------------

    /**
     * relaCompareCode Setting method
     * by leeyu 20090402 QDV4深圳2009年01月13日01_RA MS00192
     * @param relaCompareCode String
     */
    public void setRelaCompareCode(String relaCompareCode) {
        this.relaCompareCode = relaCompareCode;
    }

    public void checkInput(byte btOper) throws YssException {
    	/**add---shashijie 2013-2-18 STORY 3366 增加公共表数据处理*/
    	if (btOper == YssCons.OP_ADD) {
	    	//公共表
	    	dbFun.checkInputCommon(btOper,
		            "Tb_Dao_Pretreat",
		            "FDPDsCode",
		            this.dPDsCode,
		            this.oldDPDsCode);
	    	//非选中状态处理组合群表数据,反之处理公共表数据
	    	//组合群表
			dbFun.checkInputCommon(btOper,
		            pub.yssGetTableName("Tb_Dao_Pretreat"),
		            "FDPDsCode",
		            this.dPDsCode,
		            this.oldDPDsCode);
    	} else {//修改,审核等等
    		if (this.saveType.equals("1")) {
    			//公共表
    	    	dbFun.checkInputCommon(btOper,
    		            "Tb_Dao_Pretreat",
    		            "FDPDsCode",
    		            this.dPDsCode,
    		            this.oldDPDsCode);
    		} else {
    			//组合群表
    			dbFun.checkInputCommon(btOper,
    		            pub.yssGetTableName("Tb_Dao_Pretreat"),
    		            "FDPDsCode",
    		            this.dPDsCode,
    		            this.oldDPDsCode);
			}
		}
		/**end---shashijie 2013-2-18 STORY 3366*/
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.dPDsCode).append("\t");
        buf.append(this.dPDsName).append("\t");
        buf.append(this.dsType).append("\t");
        buf.append(this.targetTabCode).append("\t");
        buf.append(this.targetTabName).append("\t");
        buf.append(this.beanId).append("\t");
        buf.append(this.dataSource).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.relaCompareCode).append("\t"); // by leeyu 20090402 QDV4深圳2009年01月13日01_RA MS00192
        //MS01337   add by zhangfa 2010.06.30   QDV4海富通2010年06月22日02_AB  
        buf.append(this.mGroupshare).append("\t");
        buf.append(this.showImpNum).append("\t");//add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
        //------------------------------------------------------------------
        //add by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A
		buf.append(this.AssetGroupName).append("\t");
		
		/**add---shashijie 2013-2-25 STORY 3366 增加存储类型-公共表复选框*/
        buf.append(this.saveType.trim()).append("\t");
		/**end---shashijie 2013-2-25 STORY 3366*/
        
        buf.append(super.buildRecLog());
        return buf.toString();
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
            } else {
                sTmpStr = sRowStr;
            }
            //edited by zhouxiang MS01344  接口预处理设置回收站界面中的全选按钮功能失效 
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled 
            //edited by zhouxiang
            reqAry = sTmpStr.split("\t");
            this.dPreTreatRecycled = sRowStr;
            this.dPDsCode = reqAry[0];
            if (reqAry[0].length() == 0) {
                this.dPDsCode = " ";
            }
            this.dPDsName = reqAry[1];
            if (reqAry[2].length() > 0) {
                this.dsType = Integer.parseInt(reqAry[2]);
            }
            this.targetTabCode = reqAry[3];
            this.beanId = reqAry[4];
            this.dataSource = reqAry[5];
            //edit by licai 20101112 BUG #374 接口字典设置描述含有回车的问题 
            if (reqAry[6] != null ){
            	if (reqAry[6].indexOf("【Enter】") >= 0){
            		this.desc = reqAry[6].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.desc = reqAry[6];
            	}
            }            
//            this.desc = reqAry[6];
          //edit by licai 20101112 BUG #374 接口字典设置描述含有回车的问题 ==end=   
            
            this.oldDPDsCode = reqAry[7];
            this.checkStateId = YssFun.toInt(reqAry[8]);
            //MS01337   add by zhangfa 2010.06.30   QDV4海富通2010年06月22日02_AB  
			//edit by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A
            this.mGroupshare=reqAry[11];
            
            this.showImpNum =  reqAry[13];//add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
            //-------------------------------------------------------------------
            if (reqAry[9].equalsIgnoreCase("true")) {
                this.bIsShow = true;
            } else {
                this.bIsShow = false;
            }
            this.relaCompareCode = reqAry[10]; //by leeyu 20090402 QDV4深圳2009年01月13日01_RA MS00192
            /**add---shashijie 2013-2-17 STORY 3366 增加存储类型-公共表复选框*/
            this.saveType = reqAry[14];
			/**end---shashijie 2013-2-17 STORY 3366*/
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoPretreatBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析预处理接口信息出错", e);
        }
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	/**add---shashijie 2013-2-25 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        	conn.setAutoCommit(false);
            bTrans = true;
            
            if (dbl.getDBType() == YssCons.DB_ORA) { //2007.12.04 修改 蒋锦 增加了数据库类型的判断，大数据类型的字段插入方法不同
            	//增加sql语句
            	strSql = getInsterSql();
                dbl.executeSql(strSql);
                
                //修改预处理进数据库
                UpdateDateSource(this.dPDsCode);
                
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                strSql = getInsterSqlDb2();
                dbl.executeSql(strSql);
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**end---shashijie 2013-2-17 STORY 3366 */
        } catch (Exception e) {
            throw new YssException("增加预处理设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**shashijie 2013-2-25 STORY 3366 无用注释*/
    /*private Clob getClob(String str) throws SQLException, YssException {
        try {
            CLOB clob = null;
            String sqlStr = "select FDataSource from " +
                pub.yssGetTableName("Tb_Dao_Pretreat") +
                " where FDPDsCode =" + dbl.sqlString(str);
            ResultSet rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
            	  // modify by jsc 20120809 连接池对大对象的特殊处理
            	 clob = dbl.CastToCLOB(rs.getClob("FDataSource"));
                //clob = ( (oracle.jdbc.OracleResultSet) rs).getCLOB("FDataSource");
            			  
                clob.putString(1, this.dataSource);
            }
            rs.close();
            return clob;
        } catch (SQLException e) {
            throw new YssException(e);
        }
    }*/

    /**shashijie 2013-2-25 STORY 3366 获取SQL */
	private String getInsterSqlDb2() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " insert into " + pub.yssGetTableName("Tb_Dao_Pretreat");
		} else {
			sql = " insert into Tb_Dao_Pretreat ";
		}
		sql += "(FDPDsCode,FDPDsName,FDsType,FTargetTab,FBeanId,FDataSource," +
			//添加核对关联预处理代码字段 by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
	        "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FRelaCompCode)" + 
	        " values(" + dbl.sqlString(this.dPDsCode) + "," +
	        dbl.sqlString(this.dPDsName) + "," +
	        this.dsType + "," +
	        dbl.sqlString(this.targetTabCode) + " ," +
	        dbl.sqlString(this.beanId) + " ," +
	        dbl.sqlString(this.dataSource) + "," +
	        dbl.sqlString(this.desc) + "," +
	        (pub.getSysCheckState() ? "0" : "1") + "," +
	        dbl.sqlString(this.creatorCode) + "," +
	        dbl.sqlString(this.creatorTime) + "," +
	        (pub.getSysCheckState() ? "' '" :
	         dbl.sqlString(this.creatorCode)) +
	        "," + dbl.sqlString(this.relaCompareCode) + ")"; //by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
		return sql;
	}
	
	/**shashijie 2013-2-25 STORY 3366 获取SQL*/
	private String getUpdateDateSoure(String dPDsCode) {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " update " + pub.yssGetTableName("Tb_Dao_Pretreat");
		} else {
			sql = " update Tb_Dao_Pretreat ";
		}
		sql += " set FDataSource = ? where FDPDsCode=" +
        	dbl.sqlString(dPDsCode);
		return sql;
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
	
	/**shashijie 2013-2-25 STORY 3366 获取SQL*/
	private String getInsterSql() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " insert into " + pub.yssGetTableName("Tb_Dao_Pretreat");
		} else {
			sql = " insert into Tb_Dao_Pretreat ";
		}
		sql += " (FDPDsCode,FDPDsName,FDsType,FTargetTab,FBeanId,FDataSource,FmGroupshare," +
	        " FshowImpNum,"+//add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A
	        //添加核对关联预处理代码 by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
	        " FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FRelaCompCode)" + 
	        " values(" + dbl.sqlString(this.dPDsCode) + "," +
	        dbl.sqlString(this.dPDsName) + "," +
	        this.dsType + "," +
	        dbl.sqlString(this.targetTabCode) + " ," +
	        dbl.sqlString(this.beanId) + " ," +
	        "EMPTY_CLOB()" + "," +
			//edit by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A
	        dbl.sqlString(this.mGroupshare) + " ," +
	        dbl.sqlString(this.showImpNum)+" ,"+//add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
	        dbl.sqlString(this.desc) + "," +
	        (pub.getSysCheckState() ? "0" : "1") + "," +
	        dbl.sqlString(this.creatorCode) + "," +
	        dbl.sqlString(this.creatorTime) + "," +
	        (pub.getSysCheckState() ? "' '" :  dbl.sqlString(this.creatorCode)) +
	        "," + dbl.sqlString(this.relaCompareCode) + ")"; //QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090402
		return sql;
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
	
    public String editSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
        	/**add---shashijie 2013-2-25 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            
            if (dbl.getDBType() == YssCons.DB_ORA) { //2007.12.04 添加 添加了数据库类型的判断，Clob 类型字段的修改方式不同
            	//获取修改SQL
                sqlStr = getUpdateSql();
                dbl.executeSql(sqlStr);
                //修改数据源
                UpdateDateSource(this.oldDPDsCode);
                
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                sqlStr = getUpdateSqlDb2();
                dbl.executeSql(sqlStr);
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
            //修改预处理表里同时修改预处理字段表 add liyu 09118
            sqlStr = getUpdateJoin(pub.yssGetTableName("tb_dao_pretreatfield"));
            dbl.executeSql(sqlStr);
            
            //同时修改删除条件
            sqlStr = getUpdateJoin(pub.yssGetTableName("Tb_Dao_TgtTabCond"));
            dbl.executeSql(sqlStr);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**end---shashijie 2013-2-17 STORY 3366 */
        } catch (Exception e) {
            throw new YssException("修改预处理设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    /**shashijie 2013-2-25 STORY 3366 获取SQL*/
	private String getUpdateJoin(String tableName) {
		String tabName = "";
		//非选中状态处理组合群表,反之处理公共表
		if (this.saveType.equals("0")) {
			tabName = tableName;
		} else {
			tabName = tableName.substring(0,3) + tableName.substring(7);;
		}
		String sql = " update " + tabName +
	        " set FDPDsCode=" + dbl.sqlString(this.dPDsCode) +
	        " where FDPDsCode=" + dbl.sqlString(this.oldDPDsCode);
		return sql;
	}
	
	/**shashijie 2013-2-25 STORY 3366 获取SQL*/
	private String getUpdateSqlDb2() {
		String sql = "";
		//如果是非选中状态处理组合群表,反之则处理公共表
		if (saveType.equals("0")) {
			sql = " update " + pub.yssGetTableName("Tb_Dao_Pretreat");
		} else {
			sql = " update Tb_Dao_Pretreat ";
		}
		sql += "  set " +
	        " FDPDsCode =" + dbl.sqlString(this.dPDsCode) + "," +
	        " FDPDsName =" + dbl.sqlString(this.dPDsName) + "," +
	        " FDsType=" + this.dsType + "," +
	        " FTargetTab=" + dbl.sqlString(this.targetTabCode) + "," +
	        " FBeanId=" + dbl.sqlString(this.beanId) + "," +
	        " FDesc =" + dbl.sqlString(this.desc) + "," +
	        //添加核对关联预处理代码字段 by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
	        " FRelaCompCode = " + dbl.sqlString(this.relaCompareCode) + "," + 
	        " FDataSource = " + dbl.sqlString(this.dataSource) +
	        " where FDPDsCode =" + dbl.sqlString(this.oldDPDsCode);
		return sql;
	}
	
	/**shashijie 2013-2-25 STORY 修改数据源*/
	private void UpdateDateSource(String code) throws YssException{
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String sqlStr = "";
		ResultSet rs = null;
		try {
            conn.setAutoCommit(false);
            bTrans = true;
            
			sqlStr = getSelectObject();
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	//STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
            	// modify by jsc 20120809 连接池对大对象的特殊处理
            	CLOB clob = DbBase.CastToCLOB(rs.getClob("FDataSource"));
                //CLOB clob = ( (oracle.jdbc.OracleResultSet) rs).getCLOB("FDataSource");
                clob.putString(1, this.dataSource);
                sqlStr = getUpdateDateSoure(code);
                
                PreparedStatement pst = conn.prepareStatement(sqlStr);
                pst.setClob(1, clob);
                pst.executeUpdate();
                pst.close();
            }
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("修改预处理设置信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**shashijie 2013-2-25 STORY 3366 获取SQL */
	private String getUpdateSql() {
		String sql = "";
		//如果是非选中状态处理组合群表,反之则处理公共表
		if (saveType.equals("0")) {
			sql = " update " + pub.yssGetTableName("Tb_Dao_Pretreat");
		} else {
			sql = " update Tb_Dao_Pretreat ";
		}
		sql += "  set " +
	        " FDPDsCode =" + dbl.sqlString(this.dPDsCode) + "," +
	        " FDPDsName =" + dbl.sqlString(this.dPDsName) + "," +
	        " FDsType=" + this.dsType + "," +
	        //add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A
	        " FShowImpNum="+dbl.sqlString(this.showImpNum)+" ," +  
	        " FTargetTab=" + dbl.sqlString(this.targetTabCode) + "," +
	        " FBeanId=" + dbl.sqlString(this.beanId) + "," +
	        " FDesc =" + dbl.sqlString(this.desc) + "," +
	        //添加核对关联预处理代码字段 by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
	        " FRelaCompCode = " + dbl.sqlString(this.relaCompareCode) + "," + 
	        //edit by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A
			" FmGroupshare=" + dbl.sqlString(this.mGroupshare) + "," +
	        " FDataSource = EMPTY_CLOB()" +
	        " where FDPDsCode =" + dbl.sqlString(this.oldDPDsCode);
		return sql;
	}
	
	public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false;
        String sqlStr = "";
        try {
        	/**add---shashijie 2013-2-25 STORY 3366 原先删除时直接把关联表中的记录删除了,导致还原出现问题,外加重构代码*/
        	//新方法具有多条记录操作功能 edited by zhouxiang
        	conn.setAutoCommit(false);
            bTrans = true;
			// edited by zhouxiang MS01344   接口预处理设置回收站界面中的全选按钮功能失效  
			if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {
				arrData = sRecycled.split("\r\n"); 
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					// ------------end-------------

					//获取删除Update SQL语句
					sqlStr = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_Pretreat"),this.oldDPDsCode);
					dbl.executeSql(sqlStr);
					//字段设置
					sqlStr = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_PretreatField"),this.oldDPDsCode);
					dbl.executeSql(sqlStr);
					//删除条件
					sqlStr = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_TgtTabCond"),this.oldDPDsCode);
					dbl.executeSql(sqlStr); // liyu add 增加目标表删除条件处理
				}
			}
			//---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
            /**end---shashijie 2013-2-25 STORY 3366 */
        } catch (Exception e) {
            throw new YssException("删除预处理设置信息出错", e);
        } finally {
        	//---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
        	if(conn != null){
        		dbl.endTransFinal(conn, bTrans);
        	}
        	//---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
        }
    }
    
    /**shashijie 2013-2-19 STORY 3366 获取SQL */
	private String getChenkSqlJoin(String tableName,String FDPDsCode) {
		String tabName = "";
		//非选中状态处理组合群表,反之处理公共表
		if (this.saveType.equals("0")) {
			tabName = tableName;
		} else {
			tabName = tableName.substring(0,3) + tableName.substring(7);;
		}
		String sql = " update " + tabName +
	        " set FCheckState = " + this.checkStateId +
	        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	        ", FCheckTime = '" +
	        YssFun.formatDatetime(new java.util.Date()) +
	        "' where FDPDsCode = " + dbl.sqlString(FDPDsCode);
		return sql;
	}

    public void checkSetting() throws YssException {
        Connection conn = null;
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false;
        String sqlStr = "";
        try {
        	/**add---shashijie 2013-2-25 STORY 3366 原先删除时直接把关联表中的记录删除了,导致还原出现问题,外加重构代码*/
        	conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
        	//新方法具有多条记录操作功能 edited by zhouxiang
			if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { 
				arrData = sRecycled.split("\r\n"); 
				for (int i = 0; i < arrData.length; i++) 
				{
					if (arrData[i].length() == 0) 
					{
						continue;
					}
					this.parseRowStr(arrData[i]); 
					// ------------end-------------
					
					//获取审核SQL
					sqlStr = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_Pretreat"), this.dPDsCode);
					dbl.executeSql(sqlStr);
					//字段设置
					sqlStr = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_PretreatField"),this.dPDsCode);
					dbl.executeSql(sqlStr);
					//删除条件
					sqlStr = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_TgtTabCond"),this.dPDsCode);
					dbl.executeSql(sqlStr); // liyu add 增加目标表删除条件处理
				}
			}
			//--- edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--- edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
			/**end---shashijie 2013-2-25 STORY 3366 */
        } catch (Exception e) {
            throw new YssException("审核凭证数据源出错", e);
        } finally {
        	//--- edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
        	if(conn != null){
        		dbl.endTransFinal(conn, bTrans);
        	}
        	//--- edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
        }
    }

    private void setVchDs(ResultSet rs) throws SQLException, YssException {
        this.dPDsCode = rs.getString("FDPDsCode");
        this.dPDsName = rs.getString("FDPDsName");
        this.beanId = rs.getString("FBeanID");
        this.targetTabCode = rs.getString("FTargetTab");
        this.targetTabName = rs.getString("FTargetTabName");
      //MS01337   add by zhangfa 2010.06.30   QDV4海富通2010年06月22日02_AB  
        //---edit by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A start---//
        this.mGroupshare=rs.getString("FmGroupshare");
        this.AssetGroupName = rs.getString("FAssetGroupName");
		//---edit by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A end---//
        this.showImpNum = rs.getString("FShowImpNum")+"";//add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
      //-------------------------------------------------------------------  
        this.dsType = rs.getInt("FDsType");

        this.desc = rs.getString("FDesc");
        this.relaCompareCode = rs.getString("FRelaCompCode") + ""; //添加核对关联预处理代码字段 by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
        if (this.bIsShow) {
            this.dataSource = dbl.clobStrValue(rs.getClob("FDataSource")).
                replaceAll("\t", "   ");
        } else {
            this.dataSource = "null";
        }
        /**add---shashijie 2013-2-25 STORY 3366 存储类型赋值*/
		setSaveTypeValue(rs);
		/**end---shashijie 2013-2-25 STORY 3366 */
        super.setRecLog(rs);
    }

    private String builerFilter() {
        String sqlStr = "";
        if (this.filterType != null) {
            sqlStr = " where 1=1 ";
            if (filterType.dPDsCode != null && this.filterType.dPDsCode.trim().length() != 0) {
                if (filterType.dPDsCode.split(",").length > 0) { //如果前台传过来的是一组合预处理代码，则应采用 in来处理 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090305
                    sqlStr += " and a.FDPDsCode in (" +
                        operSql.sqlCodes(filterType.dPDsCode.replaceAll("'", "''")) +
                        ")";
                } else {
                    sqlStr += " and a.FDPDsCode like '" +
                        filterType.dPDsCode.replaceAll("'", "''") + "%'";
                }
            }
            if (filterType.dPDsName != null && filterType.dPDsName.length() != 0) {
                sqlStr += " and a.FDPDsName like '" +
                    filterType.dPDsName.replaceAll("'", "''") + "%'";
            }
            if (filterType.beanId != null && filterType.beanId.length() != 0) {
                sqlStr += " and a.FBeanID like '" +
                    filterType.beanId.replaceAll("'", "''") + "%'";
            }
            if (filterType.targetTabCode != null && filterType.targetTabCode.length() != 0) {
                sqlStr += " and a.FTargetTab like '" +
                    filterType.targetTabCode.replaceAll("'", "''") + "%'";
            }
            if (filterType.dsType != 99) {
                sqlStr += " and a.FDsType=" + filterType.dsType;
            }
            if (filterType.desc != null && filterType.desc.length() != 0 &&
                !filterType.desc.equalsIgnoreCase("null")) { //2008-5-28 单亮
                sqlStr += " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
            
          //add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
            if (filterType.showImpNum != null && filterType.showImpNum.length() != 0) { 
                    sqlStr += " and a.FShowImpNum = 'true'";
                }
            //end by lidaolong
            
        }
        return sqlStr;
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String s = "";
        return s;
    }

    public IDataSetting getSetting() throws YssException {
        //  IDataSetting s = new DaoPretreatBean();
        //  return s;
    	
        String strSql = "";
        ResultSet rs = null;
        try {
        	/**add---shashijie 2013-2-28 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
    		//组合群SQL
        	strSql = getSelectObjectByFDPDsCode(pub.yssGetTableName("Tb_Dao_Pretreat"), "0", this.dPDsCode);
            
        	strSql += " Union All ";
            //公共表SQL
        	strSql += getSelectObjectByFDPDsCode("Tb_Dao_Pretreat", "1", this.dPDsCode);
        	/**end---shashijie 2013-2-28 STORY 3366 */
            
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.dPDsCode = rs.getString("FDPDsCode");
                this.dPDsName = rs.getString("FDPDsName");
                this.dsType = rs.getInt("FDsType");
                this.targetTabCode = rs.getString("FTargetTab");
                this.beanId = rs.getString("FBeanId");
                this.dataSource = dbl.clobStrValue(rs.getClob("FDataSource"));
                this.relaCompareCode = rs.getString("FRelaCompCode"); //添加核对关联预处理代码字段 by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
                //MS01337   add by zhangfa 2010.06.30   QDV4海富通2010年06月22日02_AB   
				//edit by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A
                this.mGroupshare=rs.getString("FmGroupshare");
                	
                this.showImpNum = rs.getString("FShowImpNum"); //add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
                //------------------------------------------------------------------
                /**add---shashijie 2013-3-1 STORY 3366 设置存储类型*/
                setSaveTypeValue(rs);
				/**end---shashijie 2013-3-1 STORY 3366*/
                
            }
        } catch (Exception e) {
            throw new YssException("获取预处理数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    /**shashijie 2013-3-1 STORY 3366 获取SQL*/
	private String getSelectObjectByFDPDsCode(String tableName,
			String saveType, String FDPDsCode) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.* from " + tableName +
	        " a where a.FDPDsCode=" + dbl.sqlString(FDPDsCode) +
	        " and a.FCheckState=1";
		return sql;
	}
	
	public String getAllSetting() throws YssException {
        String s = "";
        return s;
    }

    /**
     * buildSql
     * 功能：处理数据源中<D>,<S>之类的参数
     * @return String
     * 修改时间：2008年6月24号
     * 修改人：蒋春
     *
     * 修改时间：2008-6-30
     * 修改人：单亮
     * 修改原因：不支持以前的变量替换方式，现在修改为两种同时都可用
     */
    public String buildSql(String sDs) { //将此方法公共方法,在外面要调用 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
        //替换老的标识 单亮 2008-6-30  begin
        String sInd = ""; //参数的标识
        String sDataType = ""; //数据类型的标识 S:字符型,I:数字型,D:日期型
        int iPos = 0;
        String sSqlValue = "";
        for (int i = 0; i < 100; i++) {
            sInd = "<" + (i + 1) + ">";
            iPos = sDs.indexOf(sInd);
            if (iPos <= 0) {
                sInd = " < " + (i + 1) + " >";
                iPos = sDs.indexOf(sInd);
            }
            if (iPos > 0) {
                sDataType = sDs.substring(iPos - 1, iPos);
                if (sDataType.equalsIgnoreCase("S")) {
                    sSqlValue = dbl.sqlString("");
                } else if (sDataType.equalsIgnoreCase("I")) {
                    sSqlValue = "0";
                } else if (sDataType.equalsIgnoreCase("D")) {
                    sSqlValue = dbl.sqlDate("1900-01-01");
                } else if (sDataType.equalsIgnoreCase("N")) {
                    sSqlValue = "''";
                } 
                
                sDs = sDs.replaceAll(sDataType + sInd, sSqlValue);
            }
            sDs = parseSqlFuns(sDs); //2009-01-07 MS00175 解析接口公式
            sDs = replaceSplitStr(sDs); //添加对特殊字符的处理byleeyu QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
        }
        
        //添加对<PP>、<Port>函数的处理 <PP>代表组合所占比例<Port>代表当前处理的组合
		//MS00817:QDV4工银2009年11月17日01_A sunkey@Modify 20091125
        sDs = sDs.replaceAll("<PP>", "1").replaceAll("<Port>", "''");
        //解析轧差函数
        sDs = parseTOB(sDs);
        
        
        
        sDs = sDs.replace('[', ' ');
        sDs = sDs.replace(']', ' ');
        if (sDs.indexOf("<U>") > 0) {
            sDs = sDs.replaceAll("<U>", pub.getUserCode());
        }
        if (sDs.indexOf("< U >") > 0) {
            sDs = sDs.replaceAll("< U >", pub.getUserCode());
        }
        if (sDs.indexOf("<s>") > 0) {
            sDs = sDs.replaceAll("<s>", "''");
        }
        if (sDs.indexOf("< s >") > 0) {
            sDs = sDs.replaceAll("< s >", "''");
        }
        if (sDs.indexOf("<S>") > 0) {
            sDs = sDs.replaceAll("<S>", "''");
        }
        if (sDs.indexOf("< S >") > 0) {
            sDs = sDs.replaceAll("< S >", "''");
        }
        if (sDs.indexOf("<s1>") > 0) {
            sDs = sDs.replaceAll("<s1>", "''");
        }
        if (sDs.indexOf("< s1 >") > 0) {
            sDs = sDs.replaceAll("< s1 >", "''");
        }
        if (sDs.indexOf("<S1>") > 0) {
            sDs = sDs.replaceAll("<S1>", "''");
        }
        if (sDs.indexOf("< S1 >") > 0) {
            sDs = sDs.replaceAll("< S1 >", "''");
        }
        //-------- add by wangzuochun 2009.11.23 MS00822 接口预处理时，对预处理数据源里的组合参数没有做解析 QDV4南方2009年11月19日01_B 
        if (sDs.indexOf("<s2>") > 0) {
            sDs = sDs.replaceAll("<s2>", "''");
        }
        if (sDs.indexOf("< s2 >") > 0) {
            sDs = sDs.replaceAll("< s2 >", "''");
        }
        if (sDs.indexOf("<S2>") > 0) {
            sDs = sDs.replaceAll("<S2>", "''");
        }
        if (sDs.indexOf("< S2 >") > 0) {
            sDs = sDs.replaceAll("< S2 >", "''");
        }
        if (sDs.indexOf("<s3>") > 0) {
            sDs = sDs.replaceAll("<s3>", "''");
        }
        if (sDs.indexOf("< s3 >") > 0) {
            sDs = sDs.replaceAll("< s3 >", "''");
        }
        if (sDs.indexOf("<S3>") > 0) {
            sDs = sDs.replaceAll("<S3>", "''");
        }
        if (sDs.indexOf("< S3 >") > 0) {
            sDs = sDs.replaceAll("< S3 >", "''");
        }
        //----------------------- MS00822 ---------------------//
        if (sDs.indexOf("<d>") > 0) {
            sDs = sDs.replaceAll("<d>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< d >") > 0) {
            sDs = sDs.replaceAll("< d >", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("<D>") > 0) {
            sDs = sDs.replaceAll("<D>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< D >") > 0) {
            sDs = sDs.replaceAll("< D >", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("<d1>") > 0) {
            sDs = sDs.replaceAll("<d1>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< d1 >") > 0) {
            sDs = sDs.replaceAll("< d1 >", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("<D1>") > 0) {
            sDs = sDs.replaceAll("<D1>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< D1 >") > 0) {
            sDs = sDs.replaceAll("< D1 >", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("<d2>") > 0) {
            sDs = sDs.replaceAll("<d2>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< d2 >") > 0) {
            sDs = sDs.replaceAll("< d2 >", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("<D2>") > 0) {
            sDs = sDs.replaceAll("<D2>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< D2 >") > 0) {
            sDs = sDs.replaceAll("< D2 >", "to_date('1900-01-01','yyyy-MM-dd')");
        }

        //add by leeyu 080729
        if (sDs.indexOf("<U>") > 0) {
            sDs = sDs.replaceAll("<U>", pub.getUserCode());
        } else if (sDs.indexOf("< U >") > 0) {
            sDs = sDs.replaceAll("< U >", pub.getUserCode());
        }

        if (sDs.indexOf("<Year>") > 0) { //把"<Year>"的标识替换成结束日期的年份
            sDs = sDs.replaceAll("<Year>",
                                 YssFun.formatDate(new java.util.Date(), "yyyy"));
        } else if (sDs.indexOf("< Year >") > 0) { // add by leeyu 080729
            sDs = sDs.replaceAll("< Year >",
                                 YssFun.formatDate(new java.util.Date(), "yyyy"));
        }
        if (sDs.indexOf("<Set>") > 0) { //把"<Year>"的标识替换成套帐号
            sDs = sDs.replaceAll("<Set>", "001");
        } else if (sDs.indexOf("< Set >") > 0) { // add by leeyu 080729
            sDs = sDs.replaceAll("< Set >", "001");
        }
        
        if (sDs.indexOf("<Group>") > 0) { //把"<Group>"的标识替换成群
            sDs = sDs.replaceAll("<Group>", pub.getAssetGroupCode());
        } else if (sDs.indexOf("< Group >") > 0) {
            sDs = sDs.replaceAll("< Group >", pub.getAssetGroupCode());
        }
        
        //添加资产代码的处理 MS00817:QDV4工银2009年11月17日01_A sunkey@Modify
        if(sDs.indexOf("<Asset>") != -1){
        	sDs = sDs.replaceAll("<Asset>", dbl.sqlString("001"));
        }
        
        /**add---shashijie 2013-3-1 STORY 3366 增加组合群判断*/
        if (sDs.indexOf("<WGroups>") != -1) {
        	sDs = sDs.replaceAll("<WGroups>", pub.getAssetGroupCode());
		}
		/**end---shashijie 2013-3-1 STORY 3366*/
        /**add---shashijie 2013-3-8 STORY 2869 增加组合代码判断*/
		if (sDs.indexOf("<WFPortCodes>") != -1) {
			sDs = sDs.replaceAll("<WFPortCodes>", operSql.sqlCodes(" "));
		}
    	/**end---shashijie 2013-3-8 STORY 2869*/
		
        sDs = sDs.replaceAll("~Base", "0");
        return sDs;
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        String sShowDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        ResultSet rs = null;
        try {
        	/**add---shashijie 2013-2-18 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
    		//组合群SQL
        	strSql = getSelectByFDPDsCode(pub.yssGetTableName("Tb_Dao_Pretreat"),"0",this.oldDPDsCode);
            
        	strSql += " Union All ";
            //公共表SQL
        	strSql += getSelectByFDPDsCode("Tb_Dao_Pretreat","1",this.oldDPDsCode);
    		/**end---shashijie 2013-2-18 STORY 3366 */
		    //edit by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A 添加 fassetgroupname
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.setVchDs(rs);
                bufShow.append(this.buildRowStr()).
                    append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            return sShowDataStr; //返回单条数据源代码
        } catch (Exception e) {
            throw new YssException("获取预处理接口信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**shashijie 2013-2-26 STORY 获取SQL单条取数*/
	private String getSelectByFDPDsCode(String tableName, String saveType,
			String FDPDsCode) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTableDesc as FTargetTabName," +
			" e.fassetgroupname from " + tableName + " a" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	        " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT )" +
	        " d on d.FTabName = a.FTargetTab " +
			//add by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A 添加 fassetgroupname
	        " left join (select FAssetGroupCode, FAssetGroupName from Tb_Sys_Assetgroup) e on e.fassetgroupcode = a.fmgroupshare " + 
	        " where a.FDPDsCode = " + dbl.sqlString(FDPDsCode);
	        //" order by a.FCheckState, a.FCreateTime desc";
		return sql;
	}
	
	public String getListViewData1() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = getListView1Headers();
            conn = dbl.loadConnection();
            
            /**add---shashijie 2013-2-18 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            sqlStr = " select * from ( ";
    		//组合群SQL
            sqlStr += getSelectList(pub.yssGetTableName("Tb_Dao_Pretreat"),"0");
            
            sqlStr += " Union All ";
            //公共表SQL
            sqlStr += getSelectList("Tb_Dao_Pretreat","1");
            sqlStr += " ) a Order By a.FDPDsCode ";
    		/**end---shashijie 2013-2-18 STORY 3366 */
                
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	
            	//add by yangshaokai 2011.12.31 STORY 2007
            	String tmpAssetGroupName = this.getGroupNameFromGroupCode(rs.getString("FMGROUPSHARE"));
            	//modify by yangshaokai 2011.12.31 STORY 2007
				bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols(),tmpAssetGroupName))
						.append(YssCons.YSS_LINESPLITMARK);
            	//---------------------end------------------------------------
                setVchDs(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_DAO_DSTYPE + "," +
                                        YssCons.YSS_DAO_PRETTYPE + "," +
                                        YssCons.YSS_FUNCTION_TYPE + "," + //更改为接口数据源,与之前的报表数据源分开 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                                        //add by xuqiji 20090522  QDV4交银施罗德2009年4月29日01_AB MS00426 MS00426    数据源检查与改为多字段的检查
                                        YssCons.YSS_FUN_SHOWALL + "," +
                                        YssCons.YSS_FUN_CONTINUE + "," +
                                        YssCons.YSS_FUN_MESSAGE);
            //----------------------------------------end----------------------------------------------//
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取预处理接口信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭ＲＳ QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
            dbl.endTransFinal(conn, bTrans);
        }

    }

	/**shashijie 2013-2-25 STORY 3366 获取SQL */
	private String getSelectList(String tableName, String saveType) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
        	" ass.FAssetGroupName as FAssetGroupName," +//added by yangshaokai 2011.12.31 STORY 2007
	        " d.FTableDesc as FTargetTabName,e.FVocName as FDsTypeCode from " +
	        tableName + " a" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
	        " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT ) d on d.FTabName =a.FTargetTab " +
	        //added by yangshaokai 2011.12.31 STORY 2007
	        " left join (select FAssetGroupCode,FAssetGroupName from tb_sys_AssetGroup) ass on a.FMGroupShare = ass.FAssetGroupCode" +
	       
	        " left join Tb_Fun_Vocabulary e on " + dbl.sqlToChar("a.FDsType") +
	        //更改为接口数据源,与之前的报表数据源分开 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
	        " = e.FVocCode and e.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_DAO_DSTYPE) + 
	        builerFilter();
	        //" order by a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
		return sql;
	}
	
	/** shashijie 2013-2-27 3366 自定义接口选择预处理接口数据源
	 * 添加　基准字段 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303 */
    public String getListViewData2() throws YssException {
        /*Connection conn = null;
        boolean bTrans = false;*/
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
        	/**add---shashijie 2013-2-27 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            sHeader = "数据源代码\t数据源名称\t描述\t基准源"; //QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
            //conn = dbl.loadConnection();
            sqlStr = getSelectListSql();
            rs = dbl.openResultSet(sqlStr);
            /**end---shashijie 2013-2-27 STORY 3366*/
            
            while (rs.next()) {
                bufShow.append(rs.getString("FDPDsCode")).append("\t");
                bufShow.append(rs.getString("FDPDsName")).append("\t");
                bufShow.append(rs.getString("FDesc")).append("\t");
                bufShow.append("").append(YssCons.YSS_LINESPLITMARK); //添加的字段，这里取空 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                // bufShow.append(rs.getString("FDPDsCode")).append(YssCons.YSS_LINESPLITMARK);
                setVchDs(rs);
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
            throw new YssException("获取预处理接口信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭结果集  QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
            //dbl.endTransFinal(conn, bTrans);
        }

    }

    /**shashijie 2013-2-27 STORY 3366 获取SQL */
	private String getSelectListSql() {
		String sql = "";
		String tablePre = pub.yssGetTableName("Tb_Dao_Pretreat");
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
			tablePre = " Tb_Dao_Pretreat ";
		}
		sql += 
		//edit by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A 添加 fassetgroupname
		" a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTableDesc as FTargetTabName, " +
		" asset.FAssetGroupName from " + tablePre + " a" +
        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
        " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT ) d on d.FTabName =a.FTargetTab " +
        //edit by yangshaokai 需求 2007 QDV411建行2011年12月09日01_A 添加 fassetgroupname
		" left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_Assetgroup) asset " +
		" On asset.FAssetGroupCode = a.Fmgroupshare " + 
        //" where a.FCheckState =1 order by a.FDPDsCode "+
        builerFilter() + //添加查询条件 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
        " and a.FCheckState=1 ";
		return sql;
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

    public String getOperValue(String sType) throws YssException {
        String strSql = "", sReturn = "", sError = "";
        String sHeader = "", sShowDataStr = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            if (sType.equalsIgnoreCase("getField")) {
                sError = "获取预处理接口信息出错";
                sHeader = "字段名称\t字段类型";
                strSql = this.dataSource.trim();
                strSql = buildSql(strSql);
                rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
                ResultSetMetaData rsmd = rs.getMetaData();
                int FieldsCount = rsmd.getColumnCount(); //原始表字段数
                int[] fDataType = new int[FieldsCount]; //记录数据字段数据类型
                String[] fDataTypeName = new String[FieldsCount];
                String[] fDataName = new String[FieldsCount]; //记录数据字段名称
                for (int i = 0; i < FieldsCount; i++) {
                    fDataName[i] = rsmd.getColumnName(i + 1);
                    fDataTypeName[i] = rsmd.getColumnTypeName(i + 1);
                    fDataType[i] = rsmd.getColumnType(i + 1);
                    buf.append(fDataName[i]).append("\t");
                    buf.append(fDataTypeName[i]).append(YssCons.YSS_LINESPLITMARK);
                }
                //      BaseReportBean rep = (BaseReportBean) pub.getOperDealCtx().getBean(this.beanID);
                //获得字段得数据类型 以及  字段名称
                //       String[] strData=rep.getReportFields1().split("\t");
                //       String[] fDataName = new String[strData.length]; //记录数据字段名称
                //       for (int i = 0; i < strData.length; i++) {
                //         buf.append(strData[i]).append("\t");
                //   buf.append("varchar").append(YssCons.YSS_LINESPLITMARK);
                //    }
                if (buf.toString().length() > 2) {
                    sShowDataStr = buf.toString().substring(0,
                        buf.toString().length() - 2);
                }
                sReturn = sHeader + "\r\f" + sShowDataStr + "\r\f" + sShowDataStr;
            }
            /**
             * 2008-5-28
             * 单亮
             * 在自定义接口浏览数据源时用到此方法
             */
            if (sType.equalsIgnoreCase("getPretreat")) {
                String sqlStr = "";
                String sAllDataStr = "";
                String sVocStr = "";
                StringBuffer bufShow = new StringBuffer();
                StringBuffer bufAll = new StringBuffer();
                //      try{
                sHeader = getListView1Headers();
                sqlStr =
                    "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                    "d.FTableDesc as FTargetTabName,e.FVocName as FDsTypeCode from " +
                    pub.yssGetTableName("Tb_Dao_Pretreat") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                    " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT ) d on d.FTabName =a.FTargetTab " +
                    " left join Tb_Fun_Vocabulary e on " +
                    dbl.sqlToChar("a.FDsType") +
                    " = e.FVocCode and e.FVocTypeCode = " +
                    dbl.sqlString(YssCons.YSS_DAO_DSTYPE) + //更改为接口数据源,与之前的报表数据源分开 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                    "where a.FDPDsCode = " + dbl.sqlString(this.dPDsCode) + "" +
                    " order by a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    bufShow.append(super.buildRowShowStr(rs,
                        this.getListView1ShowCols())).
                        append(YssCons.YSS_LINESPLITMARK);
                    setVchDs(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.
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
                VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_DAO_DSTYPE + "," +
                                            YssCons.YSS_DAO_PRETTYPE); //更改为接口数据源,与之前的报表数据源分开 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303

                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                    "\r\f" +
                    this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

            }
            return sReturn;

        } catch (Exception e) {
            throw new YssException(sError + "\n\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * deleteRecycleData 方法
     * 功能：实现回收站中的清除功能，将所选数据从数据库中删除
     * @throws YssException
     * Time: 2008-08-12
     * Creator: Mao Qiwen
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
        	/**add---shashijie 2013-2-25 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            if (dPreTreatRecycled != null && dPreTreatRecycled.length() != 0) {
            	//edited by zhouxiang MS01344
            	arrData = dPreTreatRecycled.split("\r\n");
            	//end-------------
            	conn.setAutoCommit(false);
                bTrans = true;
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    //获取删除SQL语句
                    strSql = getDeleteSql(pub.yssGetTableName("Tb_Dao_Pretreat"));
                    dbl.executeSql(strSql);
                    //字段设置
                    strSql = getDeleteSql(pub.yssGetTableName("Tb_Dao_PretreatField"));
                    dbl.executeSql(strSql);
                    //删除条件
                    strSql = getDeleteSql(pub.yssGetTableName("Tb_Dao_TgtTabCond"));
                    dbl.executeSql(strSql);
                }
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            /**end---shashijie 2013-2-25 STORY 3366*/
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

	/**shashijie 2013-2-25 STORY 3366 获取SQL */
	private String getDeleteSql(String tableName) {
		String tabName = "";
		//非选中状态处理组合群表,反之处理公共表
		if (this.saveType.equals("0")) {
			tabName = tableName;
		} else {
			tabName = tableName.substring(0,3) + tableName.substring(7);;
		}
		String sql = " delete from " + tabName + " where FDPDsCode = " + dbl.sqlString(this.dPDsCode);
		return sql;
	}
	
	/**
     * 解析公式的入口 MS00175 by leeyu
     * @param sSql String
     * @return String
     */
    private String parseSqlFuns(String sSql) {
        String sqls = "";
        sqls = WDay(sSql); //WDay
        return sqls;
    }

    /**
     * WDay公式部分 by leeyu 2009-01-07 MS00175
     * @param sSql String
     * @return String
     */
    private String WDay(String sSql) {
        String sFunCode = ""; //函数名
        String strReplace = ""; //要替代的字符串
        String strCalc = ""; //通过计算得到的字符串
        String sParams = ""; //相关参数字符串
        if (sSql.lastIndexOf("[") > 0 && sSql.lastIndexOf("]") > 0) {
            if (sSql.lastIndexOf("]") > sSql.lastIndexOf("[")) { //确保"]" 在"[" 的后面
                sParams = sSql.substring(sSql.lastIndexOf("[") + 1,
                                         sSql.lastIndexOf("]"));
                sFunCode = sSql.substring(sSql.lastIndexOf("[") - 4,
                                          sSql.lastIndexOf("["));
                if (sFunCode.equalsIgnoreCase("WDay")) {
                    strReplace = "WDay" + "[\\[]" + sParams + "[\\]]";
                    strCalc = dbl.sqlDate("2008-01-01"); //先给一个默认值
                }
            }
        }
        sSql = sSql.replaceAll(strReplace, strCalc);
        return sSql;
    }

    /**
     * 替换掉数据源中特定的转换字符 by leeyu
     * 如果Sql中包括 #[]# 这种字符，就替换掉
     * QDV4深圳2009年01月13日01_RA MS00192
     * 20090331
     * @param sDsSql String
     * @return String
     */
    private String replaceSplitStr(String sDsSql) {
        String sParam = ""; //保存#[]#内部的数据的
        String sStar = "", sEnd = ""; //保存#
        int iStarLen = 0, iEndLen = 0;
        if (sDsSql.lastIndexOf("]") > 0 && sDsSql.lastIndexOf("[") > 0) {
            iStarLen = sDsSql.lastIndexOf("[");
            iEndLen = sDsSql.lastIndexOf("]");
            sStar = sDsSql.substring(iStarLen - 1, iStarLen); //取最后一个的下一个字符
            sEnd = sDsSql.substring(iEndLen + 1, iEndLen + 2); //取最后一个的下一个字符
            sParam = sDsSql.substring(iStarLen + 1, iEndLen);
            if (sStar.equalsIgnoreCase("#") && sEnd.equalsIgnoreCase("#")) {
                sParam = "#[\\[]" + sParam + "[\\]]#";
                sDsSql = sDsSql.replaceAll(sParam, "' '"); //将查出的数据替换成相应的SQL
            }
        }
        return sDsSql;
    }
    
    /**
     * 解析钆差函数
	 * MS00817:QDV4工银2009年11月17日01_A sunkey@Modify
     * @param sDs
     * @return
     */
    private String parseTOB(String sDs) {

		//先将特殊字符进行转换，主要是将[]转换成【】，将()替换（）,*换成#
		sDs = sDs.replaceAll("\\[", "【").replaceAll("\\]", "】").replaceAll("\\(", "（").replaceAll("\\)", "）").replaceAll("\\*", "#");
		
		// 此处的钆差函数仅用于字段处理，不需实际值隐藏可直接将函数擦掉
		while (sDs.indexOf("TOB【") != -1) {
			// 截取TOB[]中括号内的字段，因为可能存在嵌套的情况，因此要进行特别处理
			// 1.获取到TOB[后面的字符串
			String sTailDs = sDs.substring(sDs.indexOf("TOB【") + 4);
			// 2.根据[的个数判断从哪个]进行截断
			while (sTailDs.indexOf("【")!=-1 && sTailDs.indexOf("【") < sTailDs.indexOf("】")) {
				sTailDs = sTailDs.replaceFirst("【", "[").replaceFirst("】", "]");
			}
			// 3.获取函数
			String sFunPara = sTailDs.substring(0, sTailDs.indexOf("】"));
			sFunPara = sFunPara.replaceAll("\\[", "【").replaceAll("]", "】");

			// 4.使用函数进行替换
			sDs = sDs.replaceAll("TOB【" + sFunPara + "】", sFunPara.split(";")[1]);
		}
		
		//还原成原来的形式
		sDs = sDs.replaceAll("【", "[").replaceAll("】", "]").replaceAll("（", "(").replaceAll("）", ")").replaceAll("#", "*");
		return sDs;
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
	/**add---shashijie 2013-2-25 返回 saveType 的值*/
	public String getSaveType() {
		return saveType;
	}
	/**add---shashijie 2013-2-25 传入saveType 设置  saveType 的值*/
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
}
