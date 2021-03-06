package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.dao.IDataSetting;
import com.yss.main.voucher.VchAssistantSettingBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

public class MaintenanceTemplateBean extends BaseDataSettingBean implements IDataSetting {
	private String sTplName = "";		//模板名称
	private String sResume = "";		//摘要
	private String sDesc = "";			//模板说明
	private String sTplNum = "";		//模板编号
	private String sDCWay = "";			//辅助核算项
	private String sAcctCode = "";	//科目代码
	private String sSubTsfTypeCode = "";	//调拨子类型代码
	private String sAccountingType = "";	//核算类型
	private String sSubTsfTypeName = "";	//调拨子类型名称
	private String sSubjectName = "";	//科目名称
	private String sPortCode = "";		//组合代码
	private String sPortName = "";		//组合名称
	private String sSelectedYear = "";	//选择的年份
	private String sEntityCode = "";	//分录代码
	

	private MaintenanceTemplateBean filterType = null;

	private String sOldAcctCode = "";	//科目代码

	private String sOldTplNum = "";		//模板编号

	private String sRecycled = "";
	
	public String getsSubjectName() {
		return sSubjectName;
	}
	public void setsSubjectName(String sSubjectName) {
		this.sSubjectName = sSubjectName;
	}
	public String getsSubTsfTypeName() {
		return sSubTsfTypeName;
	}
	public void setsSubTsfTypeName(String sSubTsfTypeName) {
		this.sSubTsfTypeName = sSubTsfTypeName;
	}
	public String getsResume() {
		return sResume;
	}
	public void setsResume(String sResume) {
		this.sResume = sResume;
	}
	public String getsAccountingType() {
		return sAccountingType;
	}
	public void setsAccountingType(String sAccountingType) {
		this.sAccountingType = sAccountingType;
	}
	public String getsSubTsfTypeCode() {
		return sSubTsfTypeCode;
	}
	public void setsSubTsfTypeCode(String sSubTsfTypeCode) {
		this.sSubTsfTypeCode = sSubTsfTypeCode;
	}
	public String getsAcctCode() {
		return sAcctCode;
	}
	public void setsAcctCode(String sSubjectCode) {
		this.sAcctCode = sSubjectCode;
	}
	public String getsDCWay() {
		return sDCWay;
	}
	public void setsDCWay(String sDCWay) {
		this.sDCWay = sDCWay;
	}
	public String getsTplNum() {
		return sTplNum;
	}
	public void setsTplNum(String sTplNum) {
		this.sTplNum = sTplNum;
	}
	public String getsTplName() {
		return sTplName;
	}
	public void setsTplName(String sTplName) {
		this.sTplName = sTplName;
	}
	public String getsDesc() {
		return sDesc;
	}
	public void setsDesc(String sDesc) {
		this.sDesc = sDesc;
	}
	

	public String getsEntityCode() {
		return sEntityCode;
	}
	public void setsEntityCode(String sEntityCode) {
		this.sEntityCode = sEntityCode;
	}
	
	private String getTplNumAutomatic() throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		int iResult = 0;
		
		try
		{
			strSql = "Select max(FTplNum) as maxNum from " + pub.yssGetTableName("tb_data_maintenancetpl");
			rs = dbl.queryByPreparedStatement(strSql);
			
			if (rs.next())
			{
				iResult = rs.getInt("maxNum");
			}
			
			iResult = iResult + 1;
			
			sReturn = String.valueOf(iResult);
			
			for (int i = sReturn.length(); i < 8; i++)
			{
				sReturn = "0" + sReturn;
			}
			
		}
		catch(Exception ye)
		{
			throw new YssException("自动获取模板编号出现错误：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return sReturn;
	}
	
	public String addSetting() throws YssException {
		
        boolean bTrans = false;
        String strSql = "";
        YssPreparedStatement yssPst = null;
		
        try
        {
            Connection conn = dbl.loadConnection();
            
            this.sTplNum = getTplNumAutomatic();
            
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_MaintenanceTpl") + "(" +
            		"FTplNum,FTplName,FTplResume,FDesc,FCreator,FCreatetime,FCHECKSTATE,FCHECKUSER)" +
            		" values(" + dbl.sqlString(sTplNum) + "," +
            		 dbl.sqlString(sTplName) + "," + 
            		 dbl.sqlString(sResume) + "," + 
            		 dbl.sqlString(sDesc) + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
                     (pub.getSysCheckState() ? "0" : "1") + "," +
                     (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                     ")";

        
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        	
        }
        catch(Exception ye)
        {
        	throw new YssException("插入模板主表数据出现错误：" + ye.getMessage());
        }
        return sTplNum;
	}
	
	public String AddTplDataDetail() throws YssException
	{
        boolean bTrans = false;
        String strSql = "";
		
        try
        {
            Connection conn = dbl.loadConnection();
            
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_MTplDataDetail") +
            		 "(FTplNum,FAcctCode,FDCWay,FAccountingType,FSubTsfTypeCode,FEntityCode,FCreator,FCreatetime,FCHECKSTATE,FCHECKUSER)" +
            		 " values(" + dbl.sqlString(sTplNum) + "," +
            		 dbl.sqlString(sAcctCode) + "," +
            		 dbl.sqlString(sDCWay) + "," +
            		 dbl.sqlString(sAccountingType) + "," +
            		 dbl.sqlString(sSubTsfTypeCode) + "," +
            		 dbl.sqlString(sEntityCode) + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
                     (pub.getSysCheckState() ? "0" : "1") + "," +
                     (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                     ")";
            
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            		 
        }
        catch(Exception ye)
        {
        	throw new YssException("插入模板明细表数据出现错误：" + ye.getMessage());
        }
        
		return buildRowStr();
	}
	
	public String getTplDataDetail(String sTplCode) throws YssException
	{
		String sReturn = "";
		ResultSet rs = null;
		String strSql = "";
		
		try
		{
			strSql = "select * from " + pub.yssGetTableName("Tb_Data_MTplDataDetail") + " where FTplNum = " + dbl.sqlString(sTplCode) + 
					 " order by FEntityCode";
			rs = dbl.openResultSet(strSql);
			
			while(rs.next())
			{
				sReturn = sReturn + rs.getString("FAcctCode") + "\t" + rs.getString("FDCWay") + "\t" + rs.getString("FAccountingType") + "\t" + (rs.getString("FSubTsfTypeCode") == null ? "" : rs.getString("FSubTsfTypeCode")) + "\r\n";
				
			}
			
			if (sReturn.length() > 2)
			{
				sReturn = sReturn.substring(0,sReturn.length() - 2);
			}
		
		}
		catch(Exception ye)
		{
			throw new YssException("获取模板明细表数据出现错误：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		return sReturn;
	}
	
	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper, pub.yssGetTableName("tb_data_maintenancetpl"),
                "FTplNum", this.sTplNum,
                this.sOldTplNum);
		
	}
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
                sql = "update " + pub.yssGetTableName("tb_data_maintenancetpl") +
                    " set FCheckState=" + this.checkStateId + ",FCheckUser=" +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime=" +
                    dbl.sqlString(this.checkTime) + " where FTplNum=" +
                    dbl.sqlString(this.sTplNum);
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
            throw new YssException("审核辅助核算项设置信息出错!");//findbugs风险调整，异常没有被抛出 胡坤 20120627
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
		
	}
	public void delSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Data_MaintenanceTpl") +
                " set FCheckState = 2" +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FTplNum = " + dbl.sqlString(this.sTplNum);
            dbl.executeSql(strSql);

            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("删除辅助核算项设置信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
		
	}
	public void deleteRecycleData() throws YssException {
		String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(bTrans);
            bTrans = true;
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "delete " + pub.yssGetTableName("Tb_Data_MaintenanceTpl") +
                    " where FTplNum=" + dbl.sqlString(this.sTplNum);
                dbl.executeSql(strSql);
                

                strSql = "delete " + pub.yssGetTableName("Tb_Data_MTplDataDetail") +
                    " where FTplNum=" + dbl.sqlString(this.sTplNum);
                dbl.executeSql(strSql);
            }
            con.commit();
            con.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("清除辅助核算项设置信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
		
	}
	public String editSetting() throws YssException {
        boolean bTrans = false;
        String strSql = "";
		
        try
        {
            Connection conn = dbl.loadConnection();
            
            strSql = "update " + pub.yssGetTableName("Tb_Data_MaintenanceTpl") +
            		 " set FTplNum = " + dbl.sqlString(sTplNum) + "," +
            		 " FTplName = " + dbl.sqlString(sTplName) + "," +
            		 " FTplResume = " + dbl.sqlString(sResume) + "," +
            		 " FDesc = " + dbl.sqlString(sDesc) + 
            		 " where FTplNum = " + dbl.sqlString(this.sOldTplNum);

            dbl.executeSql(strSql);

            strSql = "delete " + pub.yssGetTableName("Tb_Data_MTplDataDetail") +
                " where FTplNum=" + dbl.sqlString(this.sOldTplNum);
            dbl.executeSql(strSql);
            
            conn.setAutoCommit(false);
            bTrans = true;
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        	
        }
        catch(Exception ye)
        {
        	throw new YssException("更新模板主表数据出现错误：" + ye.getMessage());
        }
        return "";
	}

	
	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.sTplNum.trim().length() != 0) {
                sResult = sResult + " and a.FTplNum like '%" +
                    filterType.sTplNum.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sTplName.trim().length() != 0) {
                sResult = sResult + " and a.FTplName like '%" +
                    filterType.sTplName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sResume.trim().length() != 0) {
                sResult = sResult + " and a.FTplResume like '%" + // wdy modify 使用模糊查询
                    filterType.sResume.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sAcctCode.trim().length() != 0) {
                sResult = sResult + " and a.FAcctCode like '" + // wdy modify 使用模糊查询
                    filterType.sAcctCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sDesc.trim().length() != 0) {
                sResult = sResult + " and a.FDesc like '%" + // wdy modify 使用模糊查询并把模糊查询修改为:like '%XXX%'
                    filterType.sDesc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sDCWay.trim().length() != 0) {
                sResult = sResult + " and a.FDCWay like '" + // wdy modify 使用模糊查询
                    filterType.sDCWay.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sAccountingType.trim().length() != 0) {
                sResult = sResult + " and a.FAccountingType like '" + // wdy modify 使用模糊查询
                    filterType.sAccountingType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sSubTsfTypeCode.trim().length() != 0) {
                sResult = sResult + " and a.FSubTsfTypeCode like '" + // wdy modify 使用模糊查询
                    filterType.sSubTsfTypeCode.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }
	
	
	
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		
		buf.append(sTplNum).append("\t");
		buf.append(sTplName).append("\t");
		buf.append(sResume).append("\t");
		buf.append(sAcctCode).append("\t");
		buf.append(sDCWay).append("\t");
		buf.append(sAccountingType).append("\t");
		buf.append(sSubTsfTypeCode).append("\t");
		buf.append(sEntityCode).append("\t");
		buf.append(sDesc).append("\t");
        buf.append(super.buildRecLog());
		
		return buf.toString();
	}
	public String getOperValue(String sType) throws YssException {
		
		String sReturn = "";
		
		try
		{
			String[] sTypeList = sType.split(">>>");
			if (sTypeList.length > 1)
			{
				if (sTypeList[0].equals("getAcctList"))
				{
					String[] sRequestList = sTypeList[1].split(",");
					sPortCode = sRequestList[0];
					sSelectedYear = sRequestList[1];
					sAcctCode = sRequestList[2];
					
					sReturn = this.getListViewData3();
					
				}
			}
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		
		return sReturn;
	}
	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
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
                sTmpStr = sRowStr;
                sRecycled = sTmpStr; //增加对回收站的处理功能 by leeyu 2008-10-21 BUG:0000491
                reqAry = sTmpStr.split("\t");
                this.sTplNum = reqAry[0];
                this.sTplName = reqAry[1];
                this.sResume = reqAry[2];
                this.sAcctCode = reqAry[3];
                this.sDCWay = reqAry[4];

                this.sAccountingType = reqAry[5];
                this.sSubTsfTypeCode = reqAry[6];
                this.sEntityCode = reqAry[7];
                this.sDesc = reqAry[8];
                this.sOldTplNum = reqAry[9];
                this.sOldAcctCode = reqAry[10];
                this.checkStateId = Integer.parseInt(reqAry[11]);

                super.parseRecLog();
                if (sRowStr.indexOf("\r\t") >= 0) {
                    if (this.filterType  == null) {
                        this.filterType = new MaintenanceTemplateBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);

            }
        } catch (Exception e) {
            throw new YssException("解析维护模板设置信息出错!");
        }
		
	}
	public String getListViewData1() throws YssException {
        String strSql = "";
        try
        {
        	strSql = "Select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName" + 
        			" from " + pub.yssGetTableName("Tb_Data_MaintenanceTpl") + " a " +
        			" left join (select FUserCode,FUserName from tb_sys_userlist) b on a.FCreator = b.FUserCode " + 
        			" left join (select FUserCode,FUserName from tb_sys_userlist) c on a.FCHECKUSER = c.FUserCode " + 
        			buildFilterSql() + 
        			" order by a.FCreatetime,a.FCHECKTIME";
    		return builderListViewData(strSql);
        }
        catch(Exception e)
        {
        	throw new YssException("获取维护模板设置信息出错!");
        }
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
                setListViewData(rs);
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
	
	public void setListViewData(ResultSet rs) throws SQLException
	{
		this.sTplNum = rs.getString("FTplNum");
		this.sTplName = rs.getString("FTplName");
		this.sResume = rs.getString("FTplResume");
//		this.sAcctCode = rs.getString("FAcctCode");
//		this.sDCWay = rs.getString("FDCWay");
//		this.sAccountingType = rs.getString("FAccountingType");
//		this.sSubTsfTypeCode = rs.getString("FSubTsfTypeCode");
		this.sDesc = rs.getString("FDesc");

        super.setRecLog(rs);
		
	}
	
	public String getListViewData2() throws YssException {
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "模板代码\t模板名称";
        	strSql = "Select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName" + 
			" from " + pub.yssGetTableName("Tb_Data_MaintenanceTpl") + " a " +
			" left join (select FUserCode,FUserName from tb_sys_userlist) b on a.FCreator = b.FUserCode " + 
			" left join (select FUserCode,FUserName from tb_sys_userlist) c on a.FCHECKUSER = c.FUserCode " + 
			buildFilterSql() + 
			" where a.FCHECKSTATE = '1' " +
			"order by a.FCreatetime,a.FCHECKTIME";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FTplNum") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FTplName") + "").trim()).append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.setListViewData(rs);
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
	public String getListViewData3() throws YssException 
	{
		String strSql = "";
		ResultSet rs = null;
		ResultSet rsAuxiaLevel1 = null;
		ResultSet rsAuxiaList = null;
		String sReturn = "";
		String sAuxiaList = "";
		
		try
		{
			String sTableName = "a" + sSelectedYear + getBookSetCode(this.sPortCode) + "laccount ";
			try
			{
				
				strSql = "select b.*,substr(facctcode,0,4) as a ,facctlevel,b.facctparent from " + sTableName + " b";
				
				if (!"".equals(sAcctCode.trim()))
				{
					strSql = strSql + " where b.FAcctCode like '%" + sAcctCode + "%' ";
				}
				
				strSql = strSql + " order by b.facctclass, a, b.facctlevel desc, b.facctcode ";
				
				rs = dbl.openResultSet(strSql);
				
				while(rs.next())
				{
					sReturn = sReturn + rs.getString("FAcctCode") + "\t" + rs.getString("FAcctName") + "\t" + 
					rs.getString("FAuxiAcc") + "\t" + rs.getString("FAcctParent") + "\t" + 
					rs.getString("FCurCode") + "\t" + rs.getString("FAcctDetail") + "\t" + 
					//--- edit by songjie 2013.07.08 STORY #4143 需求深圳-(深圳赢时胜)QDII估值系统V4.0(高)20130702001 start---//
					//返回值添加 科目级别数据
					rs.getString("FAcctClass") + "\t" + rs.getString("FAcctLevel") + "\r\n";
					//--- edit by songjie 2013.07.08 STORY #4143 需求深圳-(深圳赢时胜)QDII估值系统V4.0(高)20130702001 end---//
				}
			}
			catch(Exception xe)
			{
				throw new YssException("以年份【" + sSelectedYear + "】和组合代码【" + sPortCode + "】获取科目表失败。");
			}
			
			if (sReturn.length() > 2)
			{
				sReturn = sReturn.substring(0,sReturn.length() - 2);
			}
			sReturn = sReturn + "\f\f";
			
			sTableName = "a" + sSelectedYear + getBookSetCode(this.sPortCode) + "auxiaccset";
			
			strSql = "select * from " + sTableName + " where length(auxiaccid) = 2 order by AUXIACCID";
			rsAuxiaLevel1 = dbl.openResultSet(strSql);
			
			while(rsAuxiaLevel1.next())
			{
				sAuxiaList = sAuxiaList + rsAuxiaLevel1.getString("AUXIACCID") + ">>" + rsAuxiaLevel1.getString("AUXIACCName") + "\t";
			}
			
			if(sAuxiaList.length() > 2)
			{
				sAuxiaList = sAuxiaList.substring(0,sAuxiaList.length() - 2);
			}
			
			sAuxiaList = sAuxiaList + "\r\n";
			

			strSql = "select * from " + sTableName + " where length(auxiaccid) > 2 order by AUXIACCID";
			
			rsAuxiaList = dbl.openResultSet(strSql);
			
			while(rsAuxiaList.next())
			{
				sAuxiaList = sAuxiaList + rsAuxiaList.getString("AUXIACCID") + ">>" + rsAuxiaList.getString("AUXIACCName") + "\t";
			}
			
			if(sAuxiaList.length() > 2)
			{
				sAuxiaList = sAuxiaList.substring(0,sAuxiaList.length() - 2);
			}
			
			sReturn = sReturn + sAuxiaList;
			
		}
		catch(Exception e)
		{
			throw new YssException("获取科目信息明细出错：" + e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs,rsAuxiaLevel1,rsAuxiaList);
		}
		
		return sReturn;
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
	
	/**
	 * modify by huangqirong 2013-04-24 bug #7486 调整组合套帐链接相关代码
	 * */
	public String getBookSetCode(String sPortCode) throws YssException
	{
		String sReturn = "";
		//String strSql = "";
		ResultSet rs = null;
		YssFinance finace = new YssFinance(); 
		try
		{
			finace.setYssPub(this.pub);
			String tmpSetId = finace.getBookSetId(pub.getAssetGroupCode() , sPortCode);
			if(tmpSetId != null && tmpSetId.trim().length() > 0 )
				sReturn = YssFun.formatNumber(YssFun.toNumber(tmpSetId) ,"000");
			
			//strSql = "select * from " + pub.yssGetTableName("tb_vch_portsetlink") + " where FPortCode = " +dbl.sqlString(sPortCode);
			//rs = dbl.openResultSet(strSql);
			
			//while(rs.next())
			//{
			//	sReturn = rs.getString("FBookSetCode");
			//}			
			if ("".equals(sReturn.trim()))
			{
				//--- edit by songjie 2013.07.05 STORY #4143 需求深圳-(深圳赢时胜)QDII估值系统V4.0(高)20130702001 start---//
				throw new YssException("请检查组合代码【" + sPortCode + "】的资产代码是否有对应的套帐！");
				//--- edit by songjie 2013.07.05 STORY #4143 需求深圳-(深圳赢时胜)QDII估值系统V4.0(高)20130702001 end---//
			}
		}
		catch(Exception e)
		{
			throw new YssException("通过组合代码获取套账号出错：" + e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return sReturn;
	}
	
	public String getTimeInterval() throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			strSql = "select max(fyear) as maxYear,min(fyear) as minYear from lsetlist";
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				sReturn = rs.getString("minYear") + "\t" + rs.getString("maxYear");
			}
		}
		catch(Exception e)
		{
			throw new YssException("获取年份区间出现错误：" + e.getMessage());
		}
				
		return sReturn;
	}


	
	

}
