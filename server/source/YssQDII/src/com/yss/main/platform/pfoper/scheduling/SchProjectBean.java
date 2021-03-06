package com.yss.main.platform.pfoper.scheduling;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.basesetting.*;
import com.yss.main.dao.*;
import com.yss.main.datainterface.DaoCusConfigureBean;
import com.yss.main.datainterface.DaoGroupSetBean;
import com.yss.main.funsetting.*;
import com.yss.main.parasetting.*;
import com.yss.main.voucher.*;
import com.yss.util.*;

import oracle.jdbc.*;
import oracle.sql.*;

public class SchProjectBean
    extends BaseDataSettingBean implements IDataSetting {

    private String projectCode = ""; //调度方案代码
    private String projectName = ""; //调度方案名称
    private String funModules = ""; //功能模块
    private String funModName = ""; //功能模块名称
    private String parentCode = ""; //父节点代码
    private String attrCode = ""; //属性代码
    private int exeOrderCode; //执行序号代码
    private String desc = ""; //描述
    private String sMutil = ""; //多行数据
    private SchProjectBean filterType = null;
    private String oldProjectCode = "";
    private String[] arrModules =
        new String[] {
    	//add by lvhx 2010.06.24 MS01297 计息业务的明细通过业务日期和组合动态获取  QDV4赢时胜（深圳）2010年06月02日01_A  

            //20120321 modified by liubo.Story #2353
    		//添加DataInterfaceItem_Ex（接口导出）项
            //---------------------------------------
        "settletype", "incometype", "invest", "valcheck", "valuation", "vchproject", "report","business","DataInterfaceItem","DataInterfaceItem_Ex"};//增加业务处理business    MS01179 QDV4赢时胜（深圳）2010年05月25日01_A   panjunfang add 20100525
    private String sRecycled = ""; //添加回收站的功能 by leeyu 0000491
    private int iHandCheck = 0; //添加手工选项字段 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410
    
    private String sBreakOff = "";		//added by liubo.Story #1770.可终止项
	private String sAssetGroup = "";	//added by liubo.Story #1770.跨组合群方案
	private String sAutorun = "";		//added by liubo.Story #1770.自动执行
	
	private String sOperType = "";		//added by liubo.Story #1770.权限代码。此变量不需要进行模块间的交互，So不需要Getter和Setter
	

	private String sAssetGroupCode = "";	//added by liubo.Story #1770.组合群代码
	private String sIfOverride = "";		//added by liubo.Story #1770.是否覆盖原有已存在的数据。此变量只从前台接受，不回传至前台

	public String getsAutorun() {
		return sAutorun;
	}

	public void setsAutorun(String sAutorun) {
		this.sAutorun = sAutorun;
	}
	
    public String getAssetGroup() {
		return sAssetGroup;
	}

	public void setAssetGroup(String sAssetGroup) {
		this.sAssetGroup = sAssetGroup;
	}

	public String getBreakOff() {
		return sBreakOff;
	}

	public void setBreakOff(String sBreakOff) {
		this.sBreakOff = sBreakOff;
	}
    public String getAttrCode() {
        return attrCode;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getExeOrderCode() {
        return exeOrderCode;
    }

    public void setExeOrderCode(int exeOrderCode) {
        this.exeOrderCode = exeOrderCode;
    }

    public String getFunModules() {
        return funModules;
    }

    public void setFunModules(String funModules) {
        this.funModules = funModules;
    }

    public String getFunModName() {
        return funModName;
    }

    public void setFunModName(String funModName) {
        this.funModName = funModName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getMutil() {
        return this.sMutil;
    }

    public void setMutil(String sMutil) {
        this.sMutil = sMutil;
    }

    public SchProjectBean() {
    }

    public void setMutliRowFiled(ResultSet rs) throws YssException {
        try {
            this.projectCode = rs.getString("FProjectCode");
            this.projectName = rs.getString("FProjectName");
            this.funModules = rs.getString("FFunModules");
            this.funModName = rs.getString("FunModName");
            this.attrCode = dbl.clobStrValue(rs.getClob("FAttrCode"));
            this.exeOrderCode = rs.getInt("FExeOrderCode");
            this.desc = rs.getString("FDesc");
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.projectCode).append("\t");
        buf.append(this.projectName).append("\t");
        buf.append(this.funModules).append("\t");
        buf.append(this.funModName).append("\t");
        buf.append(this.parentCode).append("\t");
        buf.append(this.attrCode).append("\t");
        buf.append(this.exeOrderCode).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.iHandCheck).append("\t"); //添加手工选项字段 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410

        buf.append(this.sBreakOff).append("\t");		//added by liubo.Story #1770.可终止项
        buf.append(this.sAssetGroup).append("\t");		//added by liubo.Story #1770.跨组合群方案
        buf.append(this.sAutorun).append("\t");		//added by liubo.Story #1770.自动执行
        buf.append(this.sAssetGroupCode).append("\t");		//added by liubo.Story #1916.组合群代码
        
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void parseRowStr(String sRowStr) {
        if (sRowStr.length() == 0) {
            return;
        }
        String[] arrTmp = sRowStr.split("\f\f");
        String[] arrFiled = arrTmp[0].split("\t");
        if (arrFiled.length == 0) {
            return;
        }
        String sMutil = "";
        sRecycled = sRowStr; //添加回收站的功能 by leeyu 0000491
        //---add by songjie 2012.05.24 BUG 4651 QDV4赢时胜(上海)2012年05月23日02_B start---//
        if(arrFiled.length < 13){
        	return;
        }
        //---add by songjie 2012.05.24 BUG 4651 QDV4赢时胜(上海)2012年05月23日02_B end---//
        this.projectCode = arrFiled[0];
        this.projectName = arrFiled[1];
        this.funModName = arrFiled[2];
        this.attrCode = arrFiled[3];
        this.exeOrderCode = YssFun.toInt(arrFiled[4]);
        this.desc = arrFiled[5];
        this.checkStateId = YssFun.toInt(arrFiled[6]);
        this.oldProjectCode = arrFiled[7];
        if (YssFun.isNumeric(arrFiled[8])) { //添加手工选项字段 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410
            this.iHandCheck = YssFun.toInt(arrFiled[8]);
        }
        this.sBreakOff = arrFiled[9];		//added by liubo.Story #1770.可终止项
        this.sAssetGroup = arrFiled[10];	//added by liubo.Story #1770.跨组合群方案
        this.sAutorun = arrFiled[11];	//added by liubo.Story #1770.自动执行
        this.sAssetGroupCode = arrFiled[12];	//added by liubo.Story #1916
        this.sIfOverride = arrFiled[13];	//added by liubo.Story #1770.是否覆盖原有数据
        
        if (arrTmp.length > 1) {
            this.sMutil = arrTmp[1];
        }
        super.parseRecLog();
        if (sRowStr.indexOf("\r\t") >= 0) {
            this.filterType = new SchProjectBean();
            this.filterType.setYssPub(pub);
            this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
        }
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = " WHERE 1 = 1";
        if (this.filterType != null) {
            if (this.filterType.projectCode.length() != 0) {
                sResult = sResult + " AND a.FProjectCode LIKE '" +
                    filterType.projectCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.projectName.length() != 0) {
                sResult = sResult + " AND a.FProjectName LIKE '" +
                    filterType.projectName.replaceAll("'", "''") + "%'";
            }
            //添加手工选项字段查询条件 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090413
            if (this.filterType.iHandCheck != 99) {
            	//MS01296    调度方案设置已审核界面中点击『反审核』或『审核』或『删除』，系统提示“缺失表达式” 
            	sResult = sResult + " and (a.FHandCheck =" + filterType.iHandCheck + " or a.FHandCheck is null)";
            }
        }
        return sResult;
    }

    public String getOperValue(String sType) throws YssException {
    	String result = "";
    	if (sType.equalsIgnoreCase("update")){
    		result = this.doUpdateProjects(this.sMutil);
    		this.updateExeorderCode();
    	}
        return "";
    }

    public String getAllSetting() {
        return "";
    }

    public IDataSetting getSetting() {
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        StringBuffer bufSql = new StringBuffer(500);
        String strSql = ""; 
        String strUpdate = "";
        String[] arrMutilAttr = sMutilRowStr.split("\t");
        ResultSet rs = null;
        ResultSet rSet = null;  //add by zhangjun BUG 3836
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	
        	//added by liubo.Story #1770
        	//当“跨组合群方案”的值不为1，即不启用“跨组合群方案”时，将sAssetGroupCode的值设置为当前组合群的代码
        	//===================================
        	
        	if ("1".equals(this.sAssetGroup))
        	{
        		
        	}
        	else
        	{
        		this.sAssetGroupCode = pub.getAssetGroupCode();
        	}
        	
        	if (!returnRightsOfAssetGroup(sAssetGroupCode,pub.getUserCode(),sOperType))
        	{
        		return sAssetGroupCode;
        	}
        	
        	//=============end======================
        	
            conn.setAutoCommit(false);
            bTrans = true;
            if ("1".equals(sIfOverride))
            {
            	
            }
            else if ("0".equals(sIfOverride))
            {
            	strSql = "select * from " + "tb_" + sAssetGroupCode + "_PFOper_SchProject" +	//modified by liubo.Story #1770.确定取某个组合群的“调度方案设置”表的时候，以sAssetGroupCode变量为准
                " where  FProjectCode = " + dbl.sqlString(projectCode); //添加对执行代码的判断, by ly 080528
            	rSet = dbl.openResultSet(strSql);
            
            	while (rSet.next()) {
                	return "";
            	}
            }
            else
            {
	            strSql = "select * from " + "tb_" + sAssetGroupCode + "_PFOper_SchProject" +	//modified by liubo.Story #1770.确定取某个组合群的“调度方案设置”表的时候，以sAssetGroupCode变量为准
	                " where Fcheckstate<>2 " +
	                " and FProjectCode=" + dbl.sqlString(projectCode) +
	                (oldProjectCode.length() > 0 ? " and FProjectCode<>" + dbl.sqlString(oldProjectCode) : ""); //添加对执行代码的判断, by ly 080528
	            rSet = dbl.openResultSet(strSql);
	            
	            while (rSet.next()) {
	                throw new YssException("系统中已经存在调度方案代码为【" + projectCode + "】的项,请选择其他的代码");
	            }
            }
            
            strSql = "DELETE FROM " + "tb_" + sAssetGroupCode + "_PFOper_SchProject" +	//modified by liubo.Story #1770.确定取某个组合群的“调度方案设置”表的时候，以sAssetGroupCode变量为准
            " WHERE FProjectCode = " + dbl.sqlString(this.oldProjectCode);
            dbl.executeSql(strSql);
            for (int i = 0; i < this.arrModules.length; i++) {
                if (arrMutilAttr[i] != null && arrMutilAttr[i].length() != 0) {
                    bufSql.append("INSERT INTO ");
                    bufSql.append("tb_" + sAssetGroupCode + "_PFOper_SchProject");		//modified by liubo.Story #1770.确定取某个组合群的“调度方案设置”表的时候，以sAssetGroupCode变量为准
                    //bufSql.append(" (FProjectCode, FProjectName, FFunModules, FAttrCode, FExeOrderCode, FDesc, FCheckState, FCreator, FCreateTime)");
                    
                    //modified by liubo.Story #1770.增加FBreakOff（可终止项），FAssetGroup（跨组合群方案）两个字段
                    //=================================
                    bufSql.append(" (FProjectCode, FProjectName, FFunModules, FAttrCode, FExeOrderCode, FDesc,FHandCheck, FBreakOff, FAssetGroup, FAutorun, FCheckState, FCreator, FCreateTime)"); //添加手工选项字段 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410

                    //============end=====================
                    bufSql.append(" Values(");
                    bufSql.append(dbl.sqlString(this.projectCode)).append(",");
                    bufSql.append(dbl.sqlString(this.projectName)).append(",");
                    bufSql.append(dbl.sqlString(this.arrModules[i])).append(",");
                    if (dbl.dbType == YssCons.DB_ORA) {
                        bufSql.append("EMPTY_CLOB(),");
                    } else {
                        bufSql.append(dbl.sqlString(arrMutilAttr[i])).append(",");
                    }
                    bufSql.append(this.exeOrderCode).append(",");
                    bufSql.append(dbl.sqlString(this.desc)).append(",");
                    bufSql.append(this.iHandCheck).append(","); //添加手工选项字段，并赋值 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410
                    bufSql.append(dbl.sqlString(("1".equals(this.sBreakOff) ? "1" : "0"))).append(",");			//added by liubo.Story #1770.
                    bufSql.append(dbl.sqlString(("1".equals(this.sAssetGroup) ? "1" : "0"))).append(",");		//added by liubo.Story #1770.
                    bufSql.append(dbl.sqlString(("1".equals(this.sAutorun) ? "1" : "0"))).append(",");			//added by liubo.Story #1770
                    bufSql.append(1).append(",");//edit by yanghaiming 20100707 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 
                    bufSql.append(dbl.sqlString(this.creatorCode)).append(",");
                    bufSql.append(dbl.sqlString(this.creatorTime)).append(")");
                    dbl.executeSql(bufSql.toString());
                    bufSql.delete(0, bufSql.length());

                    if (dbl.dbType == YssCons.DB_ORA) {
                        strSql = "SELECT FAttrCode FROM " +
                        	"tb_" + sAssetGroupCode + "_PFOper_SchProject" +	//modified by liubo.Story #1770.确定取某个组合群的“调度方案设置”表的时候，以sAssetGroupCode变量为准
                            " WHERE FProjectCode = " +
                            dbl.sqlString(this.projectCode) +
                            " AND FFunModules = " + dbl.sqlString(this.arrModules[i]) +
                            " AND FExeOrderCode = " + this.exeOrderCode;
                        rs = dbl.openResultSet(strSql);
                        while (rs.next()) {
                        	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                        	  // modify by jsc 20120809 连接池对大对象的特殊处理
                        	CLOB clob = dbl.CastToCLOB(rs.getClob("FAttrCode"));
                            //CLOB clob = ( (OracleResultSet) rs).getCLOB("FAttrCode");
                            clob.putString(1, arrMutilAttr[i]);
                            strUpdate = "UPDATE " +
                            	"tb_" + sAssetGroupCode + "_PFOper_SchProject" +	//modified by liubo.Story #1770.确定取某个组合群的“调度方案设置”表的时候，以sAssetGroupCode变量为准
                                " SET FAttrCode = ? " +
                                " WHERE FProjectCode = " + dbl.sqlString(this.projectCode) +
                                " AND FFunModules = " + dbl.sqlString(this.arrModules[i]) +
                                " AND FExeOrderCode = " + this.exeOrderCode;
                            PreparedStatement pstmt = conn.prepareStatement(strUpdate);
                            pstmt.setClob(1, clob);
                            pstmt.executeUpdate();
                            pstmt.close();
                        }
                    }
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rSet);  //add by zhangjun BUG 3836            
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String addSetting() throws YssException {
        String sReturn = "";
        String sOperResult = "";	//added by liubo.Story #1916.对当前用户对被操作的组合群的权限有无与否的判断结果。若返回空，表示对此组合群有该项操作的权限。若返回的是组合群代码，表示无权限
        try {
        	//modified by liubo.Story #1770.
        	//当“跨组合群方案”的值为1时，表示当前要进行操作的调度方案需要进行跨组合群的操作。
        	//=====================================
        	sOperType = "add";
        	if ("1".equals(this.sAssetGroup))
        	{
	        	String[] strGroupCode = this.getAssdeGroup().split("\t");	//通过getAssdeGroup方法获取所有的组合群代码
	        	for (int i = 0; i < strGroupCode.length; i++)
	        	{
	        		this.sAssetGroupCode = strGroupCode[i];
	        		sOperResult = this.saveMutliSetting(this.sMutil);
	        		
	        		sReturn = sReturn + ("".equals(sOperResult.trim()) ? "" : sOperResult + ",");
	        		
	        	}
        	}
        	else
        	{
        		sOperResult = this.saveMutliSetting(this.sMutil);
        		
        		sReturn = sReturn + ("".equals(sOperResult.trim()) ? "" : sOperResult + ",");
        		
        	}
        	//=====================end=====================
        } catch (Exception e) {
            throw new YssException("新建调度方案出错！", e);
        }
        return "".equals(sReturn.trim()) ? "" : sReturn.substring(0,sReturn.length() - 1);
    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        
        String sOperResult = "";
        
    	sOperType = "del";
        
        try {        	
	        	if ("1".equals(this.sAssetGroup))
		    	{
		    		
		    	}
		    	else
		    	{
		    		this.sAssetGroupCode = pub.getAssetGroupCode();
		    	}
		    	
		    	if (!returnRightsOfAssetGroup(sAssetGroupCode,pub.getUserCode(),sOperType))
		    	{
		    		return;
		    	}
	        	//modified by liubo.Story #1770.
	        	//当“跨组合群方案”的值为1时，表示当前要进行操作的调度方案需要进行跨组合群的操作。
	        	if (!"1".equals(this.sAssetGroup))
	        	{
		            strSql = "delete " + pub.yssGetTableName("Tb_PFOper_SchProject") +//edit by yanghaiming 20100707 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 
		                " WHERE FProjectCode = " + dbl.sqlString(this.projectCode);
		            conn.setAutoCommit(false);
		            bTrans = true;
		            dbl.executeSql(strSql);
		            conn.commit(); ;
		            bTrans = false;
		            conn.setAutoCommit(true);
	        	}
	        	else
	        	{	
	        		String[] strGroupCode = this.getAssdeGroup().split("\t");	//通过getAssdeGroup方法获取所有的组合群代码
		        	for (int i = 0; i < strGroupCode.length; i++)
		        	{
		        		this.sAssetGroupCode = strGroupCode[i];
		            	if (!returnRightsOfAssetGroup(sAssetGroupCode,pub.getUserCode(),sOperType))
		            	{
		            		continue;
		            	}
		        		strSql = "delete " + "Tb_" + strGroupCode[i] + "_PFOper_SchProject" +//edit by yanghaiming 20100707 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 
		                	" WHERE FProjectCode = " + dbl.sqlString(this.projectCode);
			            conn.setAutoCommit(false);
			            bTrans = true;
			            dbl.executeSql(strSql);
			            conn.commit(); ;
			            bTrans = false;
			            conn.setAutoCommit(true);
		        	}
	        	}
        } catch (Exception e) {
            throw new YssException("删除调度方案出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /***
     * 完善回收站的功能 by leeyu 2008-10-24
     */

    public void deleteRecycleData() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            //======添加回收站的功能 by leeyu 0000491
            String[] arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "delete " + pub.yssGetTableName("Tb_PFOper_SchProject") +
                    " WHERE FProjectCode = " + dbl.sqlString(this.projectCode);
                // conn.setAutoCommit(false);
                // bTrans = true;
                dbl.executeSql(strSql);
            }
            //=======//添加回收站的功能 by leeyu 0000491
            conn.commit(); ;
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核调度方案出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void checkInput(byte btOper) throws YssException {
    	//edit by yanghaiming 20100707 MS01319 QDV4赢时胜（上海）2010年6月21日01_A  根据需求描述，不区分checkstate，因此修改时不做验证
//    	if(btOper != YssCons.OP_EDIT && btOper != YssCons.OP_DEL){
//	        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_PFOper_SchProject"),
//	                               "FProjectCode",
//	                               this.projectCode,
//	                               this.oldProjectCode);
//    	}
    }

    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            //======添加回收站的功能 by leeyu 0000491
            String[] arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "UPDATE " + pub.yssGetTableName("Tb_PFOper_SchProject") +
                    " SET FCheckState = " + this.checkStateId +
                    " WHERE FProjectCode = " + dbl.sqlString(this.projectCode);
                // conn.setAutoCommit(false);
                // bTrans = true;
                dbl.executeSql(strSql);
            }
            //=======//添加回收站的功能 by leeyu 0000491
            conn.commit(); ;
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核调度方案出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String editSetting() throws YssException {
        String sReturn = "";
        String sOperResult = "";
        try {
        	//modified by liubo.Story #1770.
        	//当“跨组合群方案”的值为1时，表示当前要进行操作的调度方案需要进行跨组合群的操作。
        	//=====================================
        	sOperType = "edit";
        	if ("1".equals(this.sAssetGroup))
        	{
	        	String[] strGroupCode = this.getAssdeGroup().split("\t");	//通过getAssdeGroup方法获取所有的组合群代码
	        	for (int i = 0; i < strGroupCode.length; i++)
	        	{
	        		this.sAssetGroupCode = strGroupCode[i];
	        		sOperResult = this.saveMutliSetting(this.sMutil);
	        		
	        		sReturn = sReturn + ("".equals(sOperResult.trim()) ? "" : sOperResult + ",");
	        		
	        	}
        	}
        	else
        	{
        		
        		sOperResult = this.saveMutliSetting(this.sMutil);
        		
        		sReturn = sReturn + ("".equals(sOperResult.trim()) ? "" : sOperResult + ",");
        	}
        	//=====================end=====================
        } catch (Exception e) {
            throw new YssException("修改调度方案出错！", e);
        }
        return "".equals(sReturn.trim()) ? "" : sReturn.substring(0,sReturn.length() - 1);
        

    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public String getListViewData1() throws YssException {
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sHeader = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            //strSql = "SELECT DISTINCT FProjectCode, FProjectName, FExeOrderCode, FDesc, FCheckState, " +
            strSql = "SELECT DISTINCT FProjectCode, FProjectName, FExeOrderCode, FDesc, FCheckState,FHandCheck, " + //增加手工处理字段  QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410
            	"FAutorun, FBreakOff, " + 	//added by liubo.Story #1770
            	"FCreator, FCreateTime, FCheckUser, FCheckTime,b.FUserName AS FCreatorName, c.FUserName AS FCheckUserName " +
                "FROM " + pub.yssGetTableName("Tb_PFOper_SchProject") + " a " +
                "left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                "left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                buildFilterSql() + " and fcheckstate = '1' ORDER BY a.FEXEORDERCODE";//edit by yanghaiming 20100707 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.projectCode = rs.getString("FProjectCode");
                this.projectName = rs.getString("FProjectName");
                this.exeOrderCode = rs.getInt("FExeOrderCode");
                this.iHandCheck = rs.getInt("FHandCheck"); //增加对手工选项的处理 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410
                this.sBreakOff = rs.getString("FBreakOff");		//added by liubo.Story #1770
                this.sAutorun = rs.getString("FAutorun");		//added by liubo.Story #1770
                this.desc = rs.getString("FDesc");

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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取调度方案设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sHeader = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "方案代码\t方案名称\t执行序号代码";
            strSql = "SELECT DISTINCT FProjectCode, FProjectName, FExeOrderCode, FFUNMODULES, FDesc, FCheckState, b.FUserName AS FCreatorName, c.FUserName AS FCheckUserName " +
                ",FHandCheck " + //添加手工选项字段 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410
                ",FAutorun,FBreakOff " + 	//added by liubo.Story #1770
                "FROM " + pub.yssGetTableName("Tb_PFOper_SchProject") + " a " +
                "left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                "left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                buildFilterSql() +
                //" AND a.FCheckState = 1 ORDER BY a.FCheckState DESC";
                " AND a.FCheckState = 1 ORDER BY a.FExeOrderCode "; //改为按执行编号的递增顺序来加载 by ly 080528
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.projectCode = rs.getString("FProjectCode");
                this.projectName = rs.getString("FProjectName");
                this.exeOrderCode = rs.getInt("FExeOrderCode");
                this.iHandCheck = rs.getInt("FHandCheck"); //增加对手工选项的处理 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410
                this.sBreakOff = rs.getString("FBreakOff");		//added by liubo.Story #1770
                this.sAutorun = rs.getString("FAutorun");		//added by liubo.Story #1770
                this.funModules = rs.getString("FFUNMODULES");	//added by liubo.Story #1916
                bufShow.append(this.projectCode).append(YssCons.YSS_ITEMSPLITMARK1);
                bufShow.append(this.projectName).append(YssCons.YSS_ITEMSPLITMARK1);
                bufShow.append(this.exeOrderCode).append(YssCons.YSS_LINESPLITMARK);

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
            throw new YssException("获取方案信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData3() throws YssException {
        String sHeaderSettle = "";
        String sHeaderBond = "";
        String sHeaderCash = "";
        String sHeaderFee = "";
        String sHeaderRepurch = "";
        String sHeaderInvest = "";
        String sHeaderValCheck = "";
        String sHeaderValuation = "";
        String sHeaderVch = "";
        String sHeaderReport = "";
        //add by lvhx 2010.06.24 MS01297 计息业务的明细通过业务日期和组合动态获取 QDV4赢时胜（深圳）2010年06月02日01_A  
        String sHeaderIncomeType=""; 
        String sHeaderBusiness = "";//业务处理  QDV4赢时胜（深圳）2010年05月25日01_A.xls  panjunfang add 20100525
        String sHeaderDataInterface = "";		//added by liubo.Story #1770
        try {
            sHeaderSettle = "结算类型代码\t结算类型名称\t组合群代码";
            sHeaderBond = "债券代码\t债券名称\t发行价格\t发行日期\t计息起始日\t计息截止日\t" +
                "兑付日期\t组合群代码";
            sHeaderCash = "帐户代码\t帐户名称\t启用日期\t帐户类型代码\t帐户类型\t开户银行代码\t" +
                "开户银行\t银行帐号\t货币代码\t币种\t组合代码\t" +
                "利息公式代码\t期间代码\t期间名称\t组合群代码";
            sHeaderFee = "运营收支品种代码\t运营收支品种名称\t收支类型\t描述\t组合群代码";
            sHeaderRepurch = "证券代码\t证券名称\t期限代码\t期限名称\t回购汇率;R\t回购类型\t回购类型名称\t期间设置\t期间设置名称\t" +
                "描述信息\t组合群代码";
            sHeaderInvest = "业务类别代码\t业务类别名称\t组合群代码";
            sHeaderValCheck = "代码\t检查项目\t组合群代码";
            sHeaderValuation = "代码\t估值类别\t组合群代码";
            sHeaderVch = "生成方案代码\t生成方案名称\t组合群代码";	//modified by liubo.Story #1916.增加组合群代码
            sHeaderReport = "报表代码\t报表名称\t组合群代码";
          //add by lvhx 2010.06.24 MS01297 计息业务的明细通过业务日期和组合动态获取 QDV4赢时胜（深圳）2010年06月02日01_A 
            sHeaderIncomeType="收益计提类型代码\t收益计提类型名称\t组合群代码"; 
            sHeaderBusiness = "业务类别代码\t业务类别名称\t组合群代码";
            sHeaderDataInterface = "接口名称\t接口代码\t序号\t接口路径\t组合群代码";	//added by liubo.Story #1770
        } catch (Exception e) {
            throw new YssException("获取列标题出错！", e);
        }
        return sHeaderBond + "\r\f" + sHeaderCash + "\r\f" + sHeaderFee + "\r\f" +
            sHeaderRepurch + "\r\f" + sHeaderInvest + "\r\f" + sHeaderValCheck + "\r\f" + sHeaderValuation + "\r\f" +
            //增加业务处理  QDV4赢时胜（深圳）2010年05月25日01_A.xls  panjunfang modify 20100525
            sHeaderVch + "\r\f" + sHeaderReport + "\r\f" + sHeaderSettle+ "\r\f" +sHeaderIncomeType+ "\r\f" +sHeaderBusiness + "\r\f" + sHeaderDataInterface;//add by lvhx 2010.06.24 MS01297
        //计息业务的明细通过业务日期和组合动态获取 QDV4赢时胜（深圳）2010年06月02日01_A 
    }

    /**
     * 查询一个调度方案
     * @throws YssException
     * @return String
     */
    public String getListViewData4() throws YssException {
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufAll = new StringBuffer(40000);
        ResultSet rs = null;
        try {
            strSql = "SELECT a.*, m.FVocName AS FunModName, a.FCheckState, b.FUserName AS FCreatorName, c.FUserName AS FCheckUserName, a.FAssetGroup as FAssetGroup, a.FBreakOFf as BreakOFf, a.FAutorun as FAutorun " +
                "FROM " + pub.yssGetTableName("Tb_PFOper_SchProject") + " a " +
                "LEFT JOIN Tb_Fun_Vocabulary m ON a.FFunModules = m.FVocCode " +
                " AND m.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PFOPER_FUNMODULES) +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                "left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                buildFilterSql();
            rs = dbl.openResultSet(strSql);
            int iIsFirst = 0;
            while (rs.next()) {
                if (iIsFirst == 0) {
                    this.projectCode = rs.getString("FProjectCode");
                    this.projectName = rs.getString("FProjectName");
                    this.exeOrderCode = rs.getInt("FExeOrderCode");
                    this.desc = rs.getString("FDesc");
                    this.iHandCheck = rs.getInt("FHandCheck"); //增加对手工选项字段的处理 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410
                    this.sAssetGroup = rs.getString("FAssetGroup");
                    this.sBreakOff = rs.getString("FBreakOff");
                    this.sAutorun = rs.getString("FAutorun");
                }
                iIsFirst++;
                String sAttrCode = dbl.clobStrValue(rs.getClob("FAttrCode"));
                if (sAttrCode != null) {
                    bufAll.append(rs.getString("FFunModules") + "\f\n").append(this.getItemInfo(rs.getString("FFunModules"),
                        sAttrCode)).
                        append(
                            YssCons.YSS_LINESPLITMARK);
                }
            }
            bufAll.insert(0, this.buildRowStr() + "\r\f");
            return bufAll.toString();
        } catch (Exception e) {
            throw new YssException("获取方案信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据属性代码查询详细信息
     * @param sFunModules String：功能模块代码
     * @param sAttrCode String：属性代码
     * @throws YssException
     * @return String：拼接好的字符串
     */
    public String getItemInfo(String sFunModules, String sAttrCode) throws YssException {
        StringBuffer bufShow = new StringBuffer(1000);
        StringBuffer bufAll = new StringBuffer(300);
        String[] sDataDetail = null;	//added by liubo.Story #1916
    	String[] sDataItems = null;		//added by liubo.Story #1916
    	String sCusSerial = "";			//added by liubo.Story #1916.接口序号
        ResultSet rs = null;
        String sqlBond = "";
        String sqlCash = "";
        String sqlFee = "";
        String sqlRepurch = "";
        String sqlInvest = "";
        String sqlValuation = "";
        String sqlVch = "";
        String sqlReport = "";
        String sqlSettle = "";
        String sVocType = "";
        String sqlBusiness = "";//增加业务处理    MS01179 QDV4赢时胜（深圳）2010年05月25日01_A   panjunfang add 20100525
        String strsAttrCodeOriginal = "";	//added by liubo.Story #1916.此变量用于保存原始的sAttrCode变量中的数据，不要被加工。此变量将在接口处理的分支中被解析
        int iIdx = 0;
        try {
        	strsAttrCodeOriginal = sAttrCode;		//added by liubo.Story #1916
        	sDataItems = strsAttrCodeOriginal.split(",");	//added by liubo.Story #1916
        	
            sAttrCode = sAttrCode.replaceAll(",", "','");
            sAttrCode = "'" + sAttrCode + "'";
            
            /**Start 20131204 modified by liubo.Bug #84939.QDV4赢时胜(上海开发)2013年12月04日03_B
             * 嘉实年结测试发现的问题:
             * 在测试过程中，发现直接以类似“select '001'”这种形式来获取组合群代码时，若用dbl.openResultSet来打开结果集，
             * 使用rs.getString的时候会在001后面跟一长串的空格，而使用dbl.queryByPreparedStatement则不会有这种问题
             * 原因不明*/
            
        	/**Start 20131118 modified by liubo.Bug #83678.QDV4赢时胜(上海开发)2013年11月18日01_B
        	 * 在测试过程中发现存储调度方案设置时，组合群代码后面会被带上一串空格，原因不详。
        	 * 这样的情况会导致用组合群取表名时，SQL会报无效字符的异常。因此需要在拼接表的时候做个去空格的操作*/
            if (sFunModules.equalsIgnoreCase("bond")) {
            	sDataItems = strsAttrCodeOriginal.split(",");
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if (sDataDetail.length == 1)
            		{
		                sqlBond = sqlBond + " SELECT a.FSecurityCode, b.FSecurityName, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode, a.FIssuePrice, a.FIssueDate, a.FInsStartDate, a.FInsEndDate, a.FInsCashDate " +
		                    " FROM (SELECT FSecurityCode, FIssuePrice, FIssueDate, FInsStartDate, FInsEndDate,  FInsCashDate, '" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp" +
		                    " FROM " + pub.yssGetTableName("tb_para_fixinterest") +
		                    " WHERE FCheckState = 1 " +
		                    " AND FSecurityCode = '" + sDataDetail[0] + "' a" +
		                    " LEFT JOIN " + pub.yssGetTableName("TB_Para_Security") + " b ON a.FSecurityCode = b.FSecurityCode union";
            		}
            		else if (sDataDetail.length == 2)
            		{
            			sqlBond = sqlBond + " SELECT a.FSecurityCode, b.FSecurityName, '" + sDataDetail[1] + "' as FAssetGroupCode, a.FIssuePrice, a.FIssueDate, a.FInsStartDate, a.FInsEndDate, a.FInsCashDate " +
	                    " FROM (SELECT FSecurityCode, FIssuePrice, FIssueDate, FInsStartDate, FInsEndDate,  FInsCashDate, '" + sDataDetail[1] + "' as FAssetGroupCodeTemp" +
	                    " FROM tb_" + sDataDetail[1].trim() + "_para_fixinterest" +
	                    " WHERE FCheckState = 1 " +
	                    " AND FSecurityCode = '" + sDataDetail[0] + "' a" +
	                    " LEFT JOIN " + "TB_" + sDataDetail[1].trim() + "_Para_Security" + " b ON a.FSecurityCode = b.FSecurityCode union";
            		}
            			
            	}

                rs = dbl.queryByPreparedStatement(sqlBond.substring(0,sqlBond.length() - 5));
            } else if (sFunModules.equalsIgnoreCase("cash")) {
            	sDataItems = strsAttrCodeOriginal.split(",");
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if (sDataDetail.length == 1)
            		{
		                sqlCash = sqlCash + " SELECT a.FCashAccCode, a.FCashAccName,'" + pub.getAssetGroupCode() + "' as FAssetGroupCode, a.FStartDate, a.FAccType, b.FAccTypeName, a.FBankCode, c.FBankName, a.FBankAccount, a.FCuryCode, d.FCuryName, a.FPortCode, a.FFormulaCode, a.FPeriodCode, e.FPeriodName" +
		                    " FROM (SELECT FCashAccCode, FCashAccName, FStartDate, FAccType, FBankCode, FBankAccount," +
		                    " FCuryCode, FPortCode, FFormulaCode, FPeriodCode,'" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp" +
		                    " FROM " + pub.yssGetTableName("Tb_Para_CashAccount") +
		                    " WHERE FCheckState = 1 AND FCashAccCode = '" + sDataDetail[0] + "') a" +
		                    " LEFT JOIN Tb_Base_AccountType b ON a.FAccType = b.FAccTypeCode" +
		                    " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Bank") + " c ON a.FBankCode = c.FBankCode" + //modify by sunkey 20090313 BugNO:MS00306 将组合群号为前缀的表修改为自动生成，而非写死的001
		                    " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Currency") + " d ON a.FCuryCode = d.FCuryCode" + //modify by sunkey 20090313 BugNO:MS00306 将组合群号为前缀的表修改为自动生成，而非写死的001
		                    " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Period") + " e ON a.FPeriodCode = e.FPeriodCode union"; //modify by sunkey 20090313 BugNO:MS00306 将组合群号为前缀的表修改为自动生成，而非写死的001
            		}
            		else if (sDataDetail.length == 2)
            		{
            			sqlCash = sqlCash + " SELECT a.FCashAccCode, a.FCashAccName, '" + sDataDetail[1] + "' as FAssetGroupCode,a.FStartDate, a.FAccType, b.FAccTypeName, a.FBankCode, c.FBankName, a.FBankAccount, a.FCuryCode, d.FCuryName, a.FPortCode, a.FFormulaCode, a.FPeriodCode, e.FPeriodName" +
	                    " FROM (SELECT FCashAccCode, FCashAccName, FStartDate, FAccType, FBankCode, FBankAccount," +
	                    " FCuryCode, FPortCode, FFormulaCode, FPeriodCode, '" + sDataDetail[1] + "' as FAssetGroupCodeTemp" +
	                    " FROM " + "Tb_" + sDataDetail[1].trim() + "_Para_CashAccount" +
	                    " WHERE FCheckState = 1 AND FCashAccCode = '" + sDataDetail[0] + "') a" +
	                    " LEFT JOIN Tb_Base_AccountType b ON a.FAccType = b.FAccTypeCode" +
	                    " LEFT JOIN " + "Tb_" + sDataDetail[1].trim() + "_Para_Bank" + " c ON a.FBankCode = c.FBankCode" + //modify by sunkey 20090313 BugNO:MS00306 将组合群号为前缀的表修改为自动生成，而非写死的001
	                    " LEFT JOIN " + "Tb_" + sDataDetail[1].trim() + "_Para_Currency" + " d ON a.FCuryCode = d.FCuryCode" + //modify by sunkey 20090313 BugNO:MS00306 将组合群号为前缀的表修改为自动生成，而非写死的001
	                    " LEFT JOIN " + "Tb_" + sDataDetail[1].trim() + "_Para_Period" + " e ON a.FPeriodCode = e.FPeriodCode union"; //modify by sunkey 20090313 BugNO:MS00306 将组合群号为前缀的表修改为自动生成，而非写死的001
            		}
            	}
                rs = dbl.queryByPreparedStatement(sqlCash.substring(0,sqlCash.length() - 5));
            } else if (sFunModules.equalsIgnoreCase("fee")) {
            	sDataItems = strsAttrCodeOriginal.split(",");
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if (sDataDetail.length == 1)
            		{
		                sqlFee = sqlFee + " SELECT FIVPayCatCode, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode,FIVPayCatName, FPayType, FDesc, '" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp" +
		                    " FROM Tb_Base_InvestPayCat" +
		                    " WHERE FCheckState = 1 AND FIVPayCatCode = '" + sDataDetail[0] + "' union";
            		}
            		else if(sDataDetail.length == 2)
            		{

		                sqlFee = sqlFee + " SELECT FIVPayCatCode, '" +  sDataDetail[1].trim() + "' as FAssetGroupCode,FIVPayCatName, FPayType, FDesc, '" +  sDataDetail[1] + "' as FAssetGroupCodeTemp" +
		                    " FROM Tb_Base_InvestPayCat" +
		                    " WHERE FCheckState = 1 AND FIVPayCatCode = '" + sDataDetail[0] + "' union";
            		}
            	}

                rs = dbl.queryByPreparedStatement(sqlFee.substring(0,sqlFee.length() - 5));
            } else if (sFunModules.equalsIgnoreCase("purchase")) {
            	sDataItems = strsAttrCodeOriginal.split(",");
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if (sDataDetail.length == 1)
            		{
		                sqlRepurch = sqlRepurch + 
		                    " SELECT a.FSecurityCode, b.FSecurityName, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode, a.FDepDurCode, c.FDepDurName, a.FPurchaseRate, a.FPurchaseType, g.FVocName as FPurchaseTypeName, a.FPeriodCode, d.FPeriodName, a.FDesc, '" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp" +
		                    " FROM (SELECT FSecurityCode, FDepDurCode, FPurchaseType, FPurchaseRate, FPeriodCode, FDesc" +
		                    " FROM " + pub.yssGetTableName("Tb_Para_Purchase") +
		                    " WHERE FCheckState = 1 AND FSecurityCode = '" + sDataDetail[0] + "') a" +
		                    " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Security") + " b ON a.FSecurityCode = b.FSecurityCode" +
		                    " LEFT JOIN " + pub.yssGetTableName("Tb_Para_DepositDuration") + " c ON a.FDepDurCode = c.FDepDurCode" +
		                    " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Period") + " d ON a.FPeriodCode = d.FPeriodCode" +
		                    " LEFT JOIN Tb_Fun_Vocabulary g on a.FPurchaseType = g.FVocCode and g.FVocTypeCode = " +
		                    dbl.sqlString(YssCons.YSS_PARA_PurchaseType) + " union";
            		}
            		else if(sDataDetail.length == 2)
            		{
            			sqlRepurch = sqlRepurch + 
		                    " SELECT a.FSecurityCode, b.FSecurityName, '" + sDataDetail[1] + "' as FAssetGroupCode, a.FDepDurCode, c.FDepDurName, a.FPurchaseRate, a.FPurchaseType, g.FVocName as FPurchaseTypeName, a.FPeriodCode, d.FPeriodName, a.FDesc, '" + sDataDetail[1] + "' as FAssetGroupCodeTemp" +
		                    " FROM (SELECT FSecurityCode, FDepDurCode, FPurchaseType, FPurchaseRate, FPeriodCode, FDesc" +
		                    " FROM " + "Tb_" + sDataDetail[1].trim() + "_Para_Purchase" +
		                    " WHERE FCheckState = 1 AND FSecurityCode  = '" + sDataDetail[0] + "' a" +
		                    " LEFT JOIN " + "Tb_" + sDataDetail[1].trim() + "_Para_Security" + " b ON a.FSecurityCode = b.FSecurityCode" +
		                    " LEFT JOIN " + "Tb_" + sDataDetail[1].trim() + "_Para_DepositDuration" + " c ON a.FDepDurCode = c.FDepDurCode" +
		                    " LEFT JOIN " + "Tb_" + sDataDetail[1].trim() + "_Para_Period" + " d ON a.FPeriodCode = d.FPeriodCode" +
		                    " LEFT JOIN Tb_Fun_Vocabulary g on a.FPurchaseType = g.FVocCode and g.FVocTypeCode = " +
		                    dbl.sqlString(YssCons.YSS_PARA_PurchaseType) + " union";
            		}
            	}

                rs = dbl.queryByPreparedStatement(sqlRepurch.substring(0,sqlRepurch.length() - 5));
            } else if (sFunModules.equalsIgnoreCase("invest")) {
            	sDataItems = strsAttrCodeOriginal.split(",");
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if (sDataDetail.length == 1)
            		{
		                sqlInvest = sqlInvest + " SELECT a.FVocCode, a.FVocName, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode, a.FCheckState, '" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                    "WHERE a.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_INVEST_OPERTYPE) +
		                    "AND a.FVocCode = '" + sDataDetail[0] + "' union";
            		}
            		else if(sDataDetail.length == 2)
            		{
		                sqlInvest = sqlInvest + " SELECT a.FVocCode, a.FVocName, '" + sDataDetail[1] + "' as FAssetGroupCode, a.FCheckState, '" + sDataDetail[1] + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                    "WHERE a.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_INVEST_OPERTYPE) +
		                    "AND a.FVocCode = '" + sDataDetail[0] + "' union";
            		}
            	}
                rs = dbl.queryByPreparedStatement(sqlInvest.substring(0,sqlInvest.length() - 5));
            } else if (sFunModules.equalsIgnoreCase("valcheck")) 
            {
                sVocType = dbl.sqlString("val_check");
                sDataItems = strsAttrCodeOriginal.split(",");
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if (sDataDetail.length == 1)
            		{
		                sqlValuation = sqlValuation + " SELECT a.FVocCode, a.FVocName, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode, a.FCheckState, '" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                    "WHERE a.FVocTypeCode = " + sVocType +
		                    "AND a.FVocCode = '" + sDataDetail[0] + "' union";
            		}
            		else if(sDataDetail.length == 2)
            		{
            			sqlValuation = sqlValuation + " SELECT a.FVocCode, a.FVocName, '" + sDataDetail[1] + "' as FAssetGroupCode, a.FCheckState, '" + sDataDetail[1] + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
					                    "WHERE a.FVocTypeCode = " + sVocType +
					                    "AND a.FVocCode = '" + sDataDetail[0] + "' union";
            		}
            	}
                rs = dbl.queryByPreparedStatement(sqlValuation.substring(0,sqlValuation.length() - 5));
            } else if (sFunModules.equalsIgnoreCase("valuation")) {
                sVocType = dbl.sqlString("val_content");
                sDataItems = strsAttrCodeOriginal.split(",");
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if (sDataDetail.length == 1)
            		{
		                sqlValuation = sqlValuation + " SELECT a.FVocCode, a.FVocName, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode, a.FCheckState, '" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                    "WHERE a.FVocTypeCode = " + sVocType +
		                    "AND a.FVocCode = '" + sDataDetail[0] + "' union";
            		}
            		else if(sDataDetail.length == 2)
            		{
            				sqlValuation = sqlValuation + " SELECT a.FVocCode, a.FVocName, '" + sDataDetail[1] + "' as FAssetGroupCode, a.FCheckState, '" + sDataDetail[1] + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                    "WHERE a.FVocTypeCode = " + sVocType +
		                    "AND a.FVocCode = '" + sDataDetail[0] + "' union";
            		}
            	}
                rs = dbl.queryByPreparedStatement(sqlValuation.substring(0,sqlValuation.length() - 5));
            } 
            //modified by liubo.Story #1916
            //=============================
            else if (sFunModules.equalsIgnoreCase("vchproject")) {
            	sDataItems = strsAttrCodeOriginal.split(",");
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if (sDataDetail.length == 1)
            		{
            			sqlVch = sqlVch + " SELECT a.FProjectCode, a.FProjectName, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode" +
                        " FROM " + pub.yssGetTableName("Tb_Vch_Project") + " a" +
                        " WHERE a.FCheckState = 1" +
                        " AND a.FProjectCode = '" + sDataDetail[0] + "' union";
            		}
            		else if (sDataDetail.length == 2)
            		{
            			sqlVch = sqlVch + " SELECT a.FProjectCode, a.FProjectName, '" + sDataDetail[1] + "' as FAssetGroupCode" +
                        " FROM " + "Tb_" + sDataDetail[1].trim() + "_Vch_Project"+ " a" +
                        " WHERE a.FCheckState = 1" +
                        " AND a.FProjectCode = '" + sDataDetail[0] + "' union";
            		}
            	}
            	
            	if ("".equals(sqlVch.trim()))
            	{
            		return "\t\t";
            	}

                rs = dbl.queryByPreparedStatement(sqlVch.substring(0,sqlVch.length() - 5));
            //=============end================
            } else if (sFunModules.equalsIgnoreCase("report")) {
            	sDataItems = strsAttrCodeOriginal.split(",");
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if (sDataDetail.length == 1)
            		{
		                sqlReport = sqlReport + " SELECT a.FVocCode, a.FVocName, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode, a.FCheckState, '" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                    "WHERE a.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PFOPER_DAYREPORT) +
		                    "AND a.FVocCode = '" + sDataDetail[0] + "' union";
            		}
            		else if (sDataDetail.length == 2)
            		{
            				sqlReport = sqlReport + " SELECT a.FVocCode, a.FVocName, '" + sDataDetail[1] + "' as FAssetGroupCode, a.FCheckState, '" + sDataDetail[1] + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                    "WHERE a.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PFOPER_DAYREPORT) +
		                    "AND a.FVocCode = '" + sDataDetail[0] + "' union";
            		}
            	}
                rs = dbl.queryByPreparedStatement(sqlReport.substring(0,sqlReport.length() - 5));
            } else if (sFunModules.equalsIgnoreCase("settletype")) {
            	sDataItems = strsAttrCodeOriginal.split(",");
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if (sDataDetail.length == 1)
            		{
		                sqlSettle = sqlSettle + " SELECT a.FVocCode, a.FVocName, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode, a.FCheckState, '" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                    "WHERE a.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PFOPER_SETTLETYPE) +
		                    "AND a.FVocCode = '" + sDataDetail[0] + "' union";
            		}
            		else if (sDataDetail.length == 2)
            		{
            			sqlSettle = sqlSettle + " SELECT a.FVocCode, a.FVocName, '" + sDataDetail[1] + "' as FAssetGroupCode, a.FCheckState, '" + sDataDetail[1] + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
	                    "WHERE a.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PFOPER_SETTLETYPE) +
	                    "AND a.FVocCode = '" + sDataDetail[0] + "' union";
            		}
            	}
                rs = dbl.queryByPreparedStatement(sqlSettle.substring(0,sqlSettle.length() - 5));
              //add by lvhx 2010.06.24 MS01297 计息业务的明细通过业务日期和组合动态获取 QDV4赢时胜（深圳）2010年06月02日01_A 
            } 
            else if (sFunModules.equalsIgnoreCase("incometype")) 
            {
            	sDataItems = strsAttrCodeOriginal.split(",");
	        	for (int i = 0;i < sDataItems.length;i++)
	        	{
	        		sDataDetail = sDataItems[i].split(">>>");
	        		if (sDataDetail.length == 1)
	        		{
		                sqlSettle = sqlSettle + " SELECT a.FVocCode, a.FVocName, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode, a.FCheckState, '" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                "WHERE a.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PFOPER_INCOMETYPE) +//edit by yanghaiming 20100730 MS01508 QDV4赢时胜深圳2010年7月29日02_B  变量居然加了引号，导致查询不到相应词汇 
		                "AND a.FVocCode = '" + sDataDetail[0] + "' union";
	        		}
	        		else if (sDataDetail.length == 2)
	        		{
	        			sqlSettle = sqlSettle + " SELECT a.FVocCode, a.FVocName, '" + sDataDetail[1] + "' as FAssetGroupCode, a.FCheckState, '" + sDataDetail[1] + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                "WHERE a.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PFOPER_INCOMETYPE) +//edit by yanghaiming 20100730 MS01508 QDV4赢时胜深圳2010年7月29日02_B  变量居然加了引号，导致查询不到相应词汇 
		                "AND a.FVocCode = '" + sDataDetail[0] + "' union";
	        		}
	        	}
                rs = dbl.queryByPreparedStatement(sqlSettle.substring(0,sqlSettle.length() - 5));
            } 
            else if (sFunModules.equalsIgnoreCase("business")) 
            {//增加业务处理    MS01179 QDV4赢时胜（深圳）2010年05月25日01_A   panjunfang add 20100525
            	sDataItems = strsAttrCodeOriginal.split(",");
	        	for (int i = 0;i < sDataItems.length;i++)
	        	{
	        		sDataDetail = sDataItems[i].split(">>>");
	        		if (sDataDetail.length == 1)
	        		{
		            	sqlBusiness = sqlBusiness + " SELECT a.FVocCode, a.FVocName, '" + pub.getAssetGroupCode() + "' as FAssetGroupCode, a.FCheckState, '" + pub.getAssetGroupCode() + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                "WHERE a.FVocTypeCode = 'val_business' AND a.FVocCode = '" + sDataDetail[0] + "' union";
	        		}
	        		else if (sDataDetail.length == 2)
	        		{
	        			sqlBusiness = sqlBusiness + " SELECT a.FVocCode, a.FVocName, '" + sDataDetail[1] + "' as FAssetGroupCode, a.FCheckState, '" + sDataDetail[1] + "' as FAssetGroupCodeTemp FROM Tb_Fun_Vocabulary a " +
		                "WHERE a.FVocTypeCode = 'val_business' AND a.FVocCode = '" + sDataDetail[0] + "' union";
	        		}
	        	}
            	rs = dbl.queryByPreparedStatement(sqlBusiness.substring(0,sqlBusiness.length() - 5));
            }
            //added by liubo.Story #1916
            //=====================================
            
            //20120321 modified by liubo.Story #2353
            //---------------------------------------
//            else if (sFunModules.equalsIgnoreCase("DataInterfaceItem")) 
            else if (sFunModules.indexOf("DataInterfaceItem") > -1) 
            //------------------------end---------------
            {
            	
        		sqlSettle = "select * from (";
        			
            	for (int i = 0;i < sDataItems.length;i++)
            	{
            		sDataDetail = sDataItems[i].split(">>>");
            		if(sDataDetail.length <= 2)
            		{
            			sCusSerial = "0";
            		}
            		else
            		{
            			sCusSerial = sDataDetail[2];
            		}
	                sqlSettle = sqlSettle + " Select FCusCfgName,FCusCfgCode,'" + sCusSerial + "' as AssetGroupName,FFileNameDesc,'" + sDataDetail[1] + 
	                			"' as FAssetGroupCode from Tb_" + sDataDetail[1].trim() + "_Dao_CusConfig " +
	                			" where FCusCfgCode = " + dbl.sqlString(sDataDetail[0]) + " union";
            	}
                if (sqlSettle.trim().length() <= 15)
                {
                	return "\t\t\t\t";
                }
                else
                {
                	sqlSettle = sqlSettle.substring(0,sqlSettle.length() - 5) + ") allData order by FAssetGroupCode,AssetGroupName";
                }
                rs = dbl.queryByPreparedStatement(sqlSettle);
            }
            /**End 20131118 modified by liubo.Bug #83678.QDV4赢时胜(上海开发)2013年11月18日01_B*/
            
            /**End 20131204 modified by liubo.Bug #84939.QDV4赢时胜(上海开发)2013年12月04日03_B*/
            
            //=================end====================
            if (rs != null) {
                while (rs.next()) {
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        bufShow.append(rs.getString(i)).append("\t");
                    }
                    bufShow.delete(bufShow.length() - 1, bufShow.length());
                    if (sFunModules.equalsIgnoreCase("bond")) {
                        FixInterestBean bean = new FixInterestBean();
                        bean.setStrSecurityCode(rs.getString(1));
                        bean.setDtStartDate(new java.util.Date());
                        bean.setDtInsEndDate(new java.util.Date());
                        bean.setDtIssueDate(new java.util.Date());
                        bean.setDtInsCashDate(new java.util.Date());
                        bean.setDtInsStartDate(new java.util.Date());
                        bufAll.append(bean.buildRowStr());
                    } else if (sFunModules.equalsIgnoreCase("cash")) {
                        CashAccountBean bean = new CashAccountBean();
                        bean.setStrCashAcctCode(rs.getString(1));
                        bean.setDtStartDate(new java.util.Date());
                        bean.setDtMatureDate(new java.util.Date());
                        bufAll.append(bean.buildRowStr());
                    } else if (sFunModules.equalsIgnoreCase("fee")) {
                        InvestPayCatBean bean = new InvestPayCatBean();
                        bean.setFIVPayCatCode(rs.getString(1));
                        bufAll.append(bean.buildRowStr());
                    } else if (sFunModules.equalsIgnoreCase("purchase")) {
                        PurchaseBean bean = new PurchaseBean();
                        bean.setSecurityCode(rs.getString(1));
                        bufAll.append(bean.buildRowStr());
                    } else if (sFunModules.equalsIgnoreCase("invest") ||
                               sFunModules.equalsIgnoreCase("valcheck") ||
                               sFunModules.equalsIgnoreCase("valuation") ||
                               sFunModules.equalsIgnoreCase("report") ||
                               sFunModules.equalsIgnoreCase("settletype")||
                             //add by lvhx 2010.06.24 MS01297 计息业务的明细通过业务日期和组合动态获取 QDV4赢时胜（深圳）2010年06月02日01_A 
                               sFunModules.equalsIgnoreCase("incometype")||
                               //增加业务处理    MS01179 QDV4赢时胜（深圳）2010年05月25日01_A   panjunfang add 20100525
                               sFunModules.equalsIgnoreCase("business")) {
                        VocabularyBean bean = new VocabularyBean();
                        bean.setVocCode(rs.getString(1));
                        bean.setVocName(rs.getString(2));
                        bean.checkStateId = rs.getInt(3);
                        bufAll.append(bean.buildRowStr());
                    } else if (sFunModules.equalsIgnoreCase("vchproject")) {
                        VchProjectBean bean = new VchProjectBean();
                        bean.setProjectCode(rs.getString(1));
                        bufAll.append(bean.buildRowStr());
                    }

                    //20120321 modified by liubo.Story #2353
                    //---------------------------------------
//                    else if (sFunModules.equalsIgnoreCase("DataInterfaceItem")) 
                    else if (sFunModules.indexOf("DataInterfaceItem") > -1) 
                    //---------------------end------------------
                    {
                        DaoCusConfigureBean bean = new DaoCusConfigureBean();
                        bufAll.append(bean.buildRowStr());
                    }
                    bufShow.append("\r\n").append(bufAll);
                    bufAll.delete(0, bufAll.length());
                    bufShow.append("\f\n");
                }
            }
            if (bufShow.length() > 0) {
                if (bufShow.substring(bufShow.length() - 2, bufShow.length()).
                    equalsIgnoreCase("\f\n")) {
                    bufShow.delete(bufShow.length() - 2, bufShow.length());
                }
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return bufShow.toString();
    }

    public String getTreeViewData1() throws YssException {
        String strSql = "";
        String result = "";
        ResultSet rs = null;
        try {
            strSql = "SELECT a.* FROM (SELECT * FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " WHERE FParaGroupCode IN (SELECT FPubParaCode FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " WHERE FParaGroupCode = 'Scheduling')" +
                " UNION " +
                " SELECT * FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") + " WHERE FParaGroupCode = 'Scheduling') a" +
                " Order By FOrderCode";

//         strSql = "SELECT a.FFunModules, m.FVocName AS FunModName" +
//               " FROM (SELECT FFunModules FROM " + pub.yssGetTableName("tb_pfoper_schproject") +
//               " GROUP BY FFunModules) a" +
//               " LEFT JOIN Tb_Fun_Vocabulary m ON a.FFunModules = m.FVocCode" +
//               " AND m.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PFOPER_FUNMODULES);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.funModules = rs.getString("FPubParaCode");
                this.funModName = rs.getString("FPubParaName");
                this.parentCode = rs.getString("FParaGroupCode");
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception e) {
            throw new YssException("获取调度方案节点出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //modified by liubo.Story #1916
    //返回给前台所有组合群中，“跨组合群方案”为“是”的项
    public String getTreeViewData2() throws YssException{
    	String sShowDataStr = "";
        String sAllDataStr = "";
        String sHeader = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "方案代码\t方案名称\t执行序号代码\t组合群代码";
            
            String[] sAllGroup = getAssdeGroup().split("\t");
            
            strSql = "select * from (";
            
            for (int i = 0;i < sAllGroup.length;i++)
            {
	            strSql = strSql + " SELECT DISTINCT FProjectCode, FProjectName, FFUNMODULES, FExeOrderCode, '" + sAllGroup[i] + "' as FAssetGroupCode, FDesc, FCheckState, b.FUserName AS FCreatorName, c.FUserName AS FCheckUserName " +
	                ",FHandCheck " + //添加手工选项字段 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410
	                ",FAutorun,FBreakOff " + 	//added by liubo.Story #1770
	                "FROM " + "Tb_" + sAllGroup[i] + "_PFOper_SchProject" + " a " +
	                "left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
	                "left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
	                buildFilterSql() +
	                //" AND a.FCheckState = 1 ORDER BY a.FCheckState DESC";
	                " AND a.FCheckState = 1 union"; //改为按执行编号的递增顺序来加载 by ly 080528
            }
            
            if (strSql.length() <= 15)
            {
            	return "\t\t\t";
            }
            else
            {
            	strSql = strSql.substring(0,strSql.length() - 5) + ") allData order by FAssetGroupCode,FExeOrderCode";
            }
            
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.projectCode = rs.getString("FProjectCode");
                this.projectName = rs.getString("FProjectName");
                this.exeOrderCode = rs.getInt("FExeOrderCode");
                this.funModules = rs.getString("FFUNMODULES");
                this.iHandCheck = rs.getInt("FHandCheck"); //增加对手工选项的处理 QDV4南方2009年04月7日01_B MS00356 byleeyu 20090410
                this.sBreakOff = rs.getString("FBreakOff");		//added by liubo.Story #1770
                this.sAutorun = rs.getString("FAutorun");		//added by liubo.Story #1770
                this.sAssetGroupCode = rs.getString("FAssetGroupCode");		//added by liubo.Story #1916
                bufShow.append(this.projectCode).append(YssCons.YSS_ITEMSPLITMARK1);
                bufShow.append(this.projectName).append(YssCons.YSS_ITEMSPLITMARK1);
                bufShow.append(this.exeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                bufShow.append(this.sAssetGroupCode).append(YssCons.YSS_LINESPLITMARK);

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
            throw new YssException("获取方案信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getTreeViewData3() {
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
    
    //add by yanghaiming 20100707 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 
    private String doUpdateProjects(String sMutilRowStr) throws YssException {
    	PreparedStatement pst = null;
        String strSql = "";
        String[] arrMutilAttr = sMutilRowStr.split("\r\f");
        ResultSet rs = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "UPDATE " + pub.yssGetTableName("Tb_PFOper_SchProject") + " SET FEXEORDERCODE = ? " + " WHERE FProjectCode = ?";
            pst = dbl.openPreparedStatement(strSql);
            
            for(int i = 0; i < arrMutilAttr.length; i++){
            	pst.setInt(1, Integer.parseInt(arrMutilAttr[i].split("\t")[1]));
            	pst.setString(2, arrMutilAttr[i].split("\t")[0]);
            	pst.addBatch();
            }
            pst.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);  //add by zhangjun 2012-04-09 BUG 3836
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }
    //add by yanghaiming 20100707 MS01319 QDV4赢时胜（上海）2010年6月21日01_A  将执行编号按从小到大的顺序中心编号
    private void updateExeorderCode() throws YssException {
    	int num = 0;
		Connection conn = null;
		ResultSet rs = null;
		boolean bTrans = false;
		PreparedStatement pst = null;
		String strSql = "";
		try {
			
			conn = dbl.loadConnection();
			strSql = " select FPROJECTCODE,fexeordercode from " + pub.yssGetTableName("Tb_PFOper_SchProject")
					+ " where FcheckState = 1 group by FPROJECTCODE,fexeordercode order by fexeordercode"; 
			rs = dbl.openResultSet(strSql);
			conn.setAutoCommit(false);
            bTrans = true;
            strSql = "UPDATE " + pub.yssGetTableName("Tb_PFOper_SchProject") + " SET FEXEORDERCODE = ? " + " WHERE FProjectCode = ?";
            pst = dbl.openPreparedStatement(strSql);
			while (rs.next()) {
				pst.setInt(1, num);
            	pst.setString(2, rs.getString("FPROJECTCODE"));
            	pst.addBatch();
            	num ++;
			}
			pst.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新调度方案设置出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);  //add by zhangjun 2012-04-09 BUG 3836
			dbl.endTransFinal(conn, bTrans);
		}
    }
    
	/**
	 * add baopingping #story 1167 20110717
	 * 查询当前表中所有组合群
	 * return ResultSet
	 * @throws YssException 
	 */
	public String getAssdeGroup() throws YssException{
		ResultSet rs=null;
		String sql=null;
		String FAssetGroupCode="";
		try{
			sql="select * from Tb_Sys_AssetGroup order by FAssetGroupCode";
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				FAssetGroupCode+=rs.getString("FAssetGroupCode")+"\t";
			}
			return FAssetGroupCode;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * added by liubo. #story 1770
	 * 此方法用于在跨组合群调度方案设置操作时，判断某个组合群是否拥有进行操作的权限。此方法备用
	 * return boolean
	 * @throws YssException 
	 */
	public boolean returnRightsOfAssetGroup(String sAssetGroupCode,String sUserCode,String sOperType) throws YssException
	{
		boolean bResult = false;
		String strSql = "";
		ResultSet rs = null;
		
		try{//modified by yeshenghong  调度方案设置报没有权限错   20130604
			strSql = " SELECT rig.fopertypes FROM (select g.fbarcode as frighttypecode,g.fbarcode as fmenubarcode " +
					 " from Tb_Fun_MenuBar g where g.frighttype like '%public%') menu " +
					 " JOIN (SELECT * FROM Tb_Sys_Userright " +
					 " WHERE FUserCode = " + dbl.sqlString(sUserCode) + " AND frighttype = 'public'" +
        	    	 " ) rig ON menu.FMenubarCode = rig.FRightCode " +
        	   		 " where menu.frighttypecode = 'schproject' " +
					" UNION SELECT ur.fopertypes FROM Tb_Sys_Userright a  " +
					" LEFT JOIN Tb_Sys_Roleright ur ON a.FRightCode = ur.frolecode  " +
					" LEFT JOIN Tb_Fun_MenuBar m ON ur.frightcode = m.fbarcode  " +
					" WHERE FUserCode = " + dbl.sqlString(sUserCode)   +
					" AND a.Frightind = 'Role' and ur.frightcode = 'schproject'";
			//---end modified by yeshenghong  调度方案设置报没有权限错   20130604
			
			rs = dbl.openResultSet(strSql);
		
			while(rs.next())
			{
				/**Start 20131113 modified by liubo.Bug #83392.QDV4赢时胜(上海开发)2013年11月13日01_B
				 * 当本身权限有权限，而角色权限没有时，因为角色权限的判断是在之后进行，所以会被整个认为没有权限。
				 * 所以在这里加个判断，当本身有权限时，跳出循环*/
				bResult = (rs.getString("FOpertypes").indexOf(sOperType) > -1 ? true : false);
				
				if (bResult)
				{
					break;
				}
				/**Start 20131113 modified by liubo.Bug #83392.QDV4赢时胜(上海开发)2013年11月13日01_B*/
			}
			
			return bResult;
			
		}
		catch(Exception e)
		{
			throw new YssException("获取调度方案设置的权限出错 ：" + e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
	}

	//added by liubo.Story #1916
	public String checkProjectCode(String sProjectCode) throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			String[] strGroupCode = this.getAssdeGroup().split("\t");	//通过getAssdeGroup方法获取所有的组合群代码
			
			for (int i = 0;i < strGroupCode.length;i++)
			{
				strSql = "select * from " + "tb_" + strGroupCode[i] + "_PFOper_SchProject" +	//modified by liubo.Story #1770.确定取某个组合群的“调度方案设置”表的时候，以sAssetGroupCode变量为准
                " where FProjectCode = " + dbl.sqlString(sProjectCode);
				rs = dbl.openResultSet(strSql);
				
				while(rs.next())
				{
					sReturn = sReturn + strGroupCode[i] + ",";
				}
				dbl.closeResultSetFinal(rs);  //add by zhangjun 2012-04-09 BUG 3836
			}

			return "".equals(sReturn.trim()) ? "" : sReturn.substring(0,sReturn.length() - 1);
			
		}
		catch(Exception e)
		{
			throw new YssException("检查调度方案代号出错 ：" + e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
	}
	
	//added by liubo.Story #1916
	//根据前台传回的调度方案代码 + “>>>” + 组合群代码，解析出调度方案代码和组合群代码，返回给前台改调度方案代码所包含的接口代码
	public String getCusConfigCode(String sRequest) throws YssException
	{
		String sReturn = "";
		String[] sQueryResult = null;
		String[] sCusConfigDetail = null;
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			String[] sRequestList = sRequest.split(">>>");
			strSql = "select * from tb_" + sRequestList[1] + "_PFOper_SchProject where FProjectCode = " + dbl.sqlString(sRequestList[0]) +
					" and FFUNMODULES like 'DataInterfaceItem%'";
			rs = dbl.openResultSet(strSql);
			
			while(rs.next())
			{
				sQueryResult = dbl.clobStrValue(rs.getClob("FATTRCODE")).split(",");
				
				for(int i = 0; i < sQueryResult.length; i++)
				{
					sCusConfigDetail = sQueryResult[i].split(">>>");
					sReturn = sReturn + sCusConfigDetail[0] + ",";
				}
			}

			return (sReturn.length() > 1 ? sReturn.substring(0, sReturn.length() - 1) : "");
			
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
	}
	
    
}
