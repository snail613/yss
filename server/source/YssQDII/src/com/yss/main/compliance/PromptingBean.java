package com.yss.main.compliance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class PromptingBean extends BaseDataSettingBean implements IDataSetting{
	
	private String strPortCode = "";
	private String strPortName = "";
	private String strUserCode = "";
	private String strUserName = "";
	private String strTransferPath = "";
	private String strTrusteeCode = "";
	private String strTrusteeName = "";
	private String strLinkManCode = "";
	private String strLinkManName = "";
	private String strDateFormat = "";
	
	private PromptingBean filterType = null;
	
    String[] allReqAry = null;
    String[] oneReqAry = null;
    
    private String sRecycled = "";

    /**add---shashijie 2013-2-25 BUG 7145 增加判断字段 */
    private String oldPortCode = "";
	/**end---shashijie 2013-2-25 BUG 7145*/
    
	public String addSetting() throws YssException {
		String strSql = "";
        ResultSet rs = null;
        //String sRecord = "";
        String sResult = "";
        try 
        {
        	strSql = "Select FPortCode,FCheckState from " + pub.yssGetTableName("Tb_comp_prompting") + 
        	" where FPortCode = " + dbl.sqlString(this.strPortCode) + "" ;

        	rs = dbl.openResultSet(strSql);
	
			while(rs.next())
			{
		        	if (rs.getInt("FCheckState") == 0)
		        	{
		        		sResult = "未审核";
		        	}
		        	else if (rs.getInt("FCheckState") == 1)
		        	{
		        		sResult = "已审核";
		        	}
		        	else if (rs.getInt("FCheckState") == 2)
		        	{
		        		sResult = "回收站";
		        	}
		        	throw new YssException("【" + sResult + "】中已存在组合代码为【" + this.strPortCode + "】的设置信息，请重新输入");
			}
			addPropmting();
	    }
        catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return "";
	}
	
	public String addPropmting() throws YssException {
		Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        ResultSet rs = null;
        //String sRecord = "";
        
        try {
        	
            strSql = "insert into " + pub.yssGetTableName("Tb_comp_prompting") +
                "(FPortCode,FDateFormat,FTransferPath,FSupervisionUserCode,FTrusteeCode," +
                " FLinkManCode" +
                ",FCheckState,FCreator,FCreateTime,FCheckUser )" +
                " values(" + dbl.sqlString(this.strPortCode) + "," +
                dbl.sqlString(this.strDateFormat) + "," +
                dbl.sqlString(this.strTransferPath) + "," +
                dbl.sqlString(this.strUserCode) + "," +
                dbl.sqlString(this.strTrusteeCode) + "," +
                
                dbl.sqlString(this.strLinkManCode) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
        	/**add---shashijie 2013-2-25 BUG 7144 修改提示语句 */
        	throw new YssException("新增提示函设置出错!");
			/**end---shashijie 2013-2-25 BUG 7144*/
        } finally {
            dbl.endTransFinal(con, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return "";
	}

	public void checkInput(byte btOper) throws YssException {
		/**add---shashijie 2013-2-25 BUG 7145 增加判断 */
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("Tb_comp_prompting"),
                "FPortCode",
                this.strPortCode,
                this.oldPortCode);
		/**end---shashijie 2013-2-25 BUG 7145*/
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
                sql = "update " + pub.yssGetTableName("Tb_comp_prompting") +
                    " set FCheckState=" + this.checkStateId + ",FCheckUser=" +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime=" +
                    dbl.sqlString(this.checkTime) + " where FPortCode=" +
                    dbl.sqlString(this.strPortCode);
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
            throw new YssException("审核提示函设置信息出错!");//findbugs风险调整，异常没有被抛出 胡坤 20120626
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
            strSql = "update " + pub.yssGetTableName("Tb_comp_prompting") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FPortCode = " + dbl.sqlString(this.strPortCode);
            dbl.executeSql(strSql);

            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("删除提示函设置信息出错!");
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
                strSql = "delete " + pub.yssGetTableName("Tb_comp_prompting") +
                    " where FPortCode=" + dbl.sqlString(this.strPortCode);
                dbl.executeSql(strSql);
            }
            con.commit();
            con.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("清除提示函设置信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

	}

	public String editSetting() throws YssException {
		 Connection con = dbl.loadConnection();
	        boolean bTrans = false;
	        //ResultSet rs = null;
	        String strSql = "";
	        try {
	            con.setAutoCommit(false);
	            bTrans = true;
	            strSql = "update " + pub.yssGetTableName("Tb_comp_prompting") +
	                " set  "  +
	                /**add---shashijie 2013-2-25 BUG 7145 修改判断字段 */
	                " FPortCode = " + dbl.sqlString(this.strPortCode) +
	                /**end---shashijie 2013-2-25 BUG 7145*/
	                ",FDateFormat =" + dbl.sqlString(this.strDateFormat) +
	                ",FTransferPath =" + dbl.sqlString(this.strTransferPath) +
	                ",FSupervisionUserCode =" + dbl.sqlString(this.strUserCode) +
	                ",FTrusteeCode =" + dbl.sqlString(this.strTrusteeCode) +
	                ",FLinkManCode =" + dbl.sqlString(this.strLinkManCode) +
	                ",FCheckstate= " +
	                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
	                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
	                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
	                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
	                /**add---shashijie 2013-2-25 BUG 7145 修改判断字段 */
	                //" where FPortCode = " + dbl.sqlString(this.strPortCode);
	                " where FPortCode = " + dbl.sqlString(this.oldPortCode);
	            	/**end---shashijie 2013-2-25 BUG 7145*/
	                
	            dbl.executeSql(strSql);
	            con.commit();
	            bTrans = false;
	            con.setAutoCommit(true);
	        } catch (Exception ex) {
	            throw new YssException(ex.getMessage());
	        } finally {
	            dbl.endTransFinal(con, bTrans);
	        }
	        return "";
	}

	public String getAllSetting() throws YssException {
		
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_comp_prompting") +
                " where FPortCode = " + dbl.sqlString(this.strPortCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
            	this.strPortCode = rs.getString("FPortCode");
                this.strDateFormat = rs.getString("FDateFormat");
                this.strTransferPath = rs.getString("FTransferPath");
                this.strUserCode = rs.getString("FSupervisionUserCode");
                this.strTrusteeCode = rs.getString("FTrusteeCode");
                this.strLinkManCode = rs.getString("FLinkManCode");
            }
            return null;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		
		return null;
	}

	public String getBeforeEditData() throws YssException {
		
		return null;
	}

	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		
		buf.append(this.strPortCode).append("\t");
		buf.append(this.strPortName).append("\t");
		buf.append(this.strDateFormat).append("\t");
		buf.append(this.strTransferPath).append("\t");
		buf.append(this.strUserCode).append("\t");
		buf.append(this.strUserName).append("\t");
		buf.append(this.strTrusteeCode).append("\t");
		buf.append(this.strTrusteeName).append("\t");
		buf.append(this.strLinkManCode).append("\t");
		buf.append(this.strLinkManName).append("\t");

        buf.append(super.buildRecLog());
		
		return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		try {
            if (sType != null && sType.equalsIgnoreCase("copy")) {
            	/**add---shashijie 2013-2-25 BUG 7145 无用删除*/
            	/*String sOldSettingCode = "";
                int iCheckState = 0;
                iCheckState = (pub.getSysCheckState() ? 0 : 1);
                addSetting();
                return this.getListViewData1();*/
                /**end---shashijie 2013-2-25 BUG 7145*/
            }
        } catch (Exception ex) {
            throw new YssException(ex.toString());
        }
        return "";
	}

	public void parseRowStr(String sRowStr) throws YssException {
		
		String reqAry[] = null;
		String sTmpStr = "";
		try
		{
            if (sRowStr.trim().length() == 0) {
                return;
            }
            
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\f") >= 0) {
                allReqAry = sRowStr.split("\r\f");
                this.strPortCode = allReqAry[0].split("\t")[0];
            } else {
                sTmpStr = sRowStr;
                sRecycled = sTmpStr; //增加对回收站的处理功能 by leeyu 2008-10-21 BUG:0000491
                reqAry = sTmpStr.split("\t");
                this.strPortCode = reqAry[0];
                this.strDateFormat = reqAry[1];
                this.strTransferPath = reqAry[2];
                this.strUserCode = reqAry[3];
                this.strTrusteeCode = reqAry[4];
                this.strLinkManCode = reqAry[5];
                this.checkStateId = Integer.parseInt(reqAry[6]);
                
                /**add---shashijie 2013-2-25 BUG 7145 增加判断字段 */
                this.oldPortCode = reqAry[7];
            	/**end---shashijie 2013-2-25 BUG 7145*/
                
                super.parseRecLog();
                if (sRowStr.indexOf("\r\t") >= 0) {
                    if (this.filterType  == null) {
                        this.filterType = new PromptingBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
	}

	public String getListViewData1() throws YssException {
		String strSql = "";
        strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTrusteeName as FTrusteeName " +
    		" , f.FPortName as FPortName,g.FUserName as UserName," +
    		" e.FLinkManName as FLinkManName from " + pub.yssGetTableName("Tb_comp_prompting") + " a " +
    		" left join (select FUserCode,FUserName from " + pub.yssGetTableName("Tb_Sys_UserList") + ") b on a.FCreator = b.FUserCode " +
    		" left join (select FUserCode,FUserName from " + pub.yssGetTableName("Tb_Sys_UserList") + ") c on a.FCheckUser = c.FUserCode " +
    		" left join (select FTrusteeCode,FTrusteeName from " + pub.yssGetTableName("Tb_Para_Trustee") + 
    		" where fCheckState = '1') d on a.FTrusteeCode=d.FTrusteeCode " +
    		" left join (select * from " + pub.yssGetTableName("Tb_Para_Linkman") + " where fCheckState = '1'" +
			"  and FRelaType = 'Trustee') e on a.FLinkManCode=e.FLinkManCode " +
    		" left join (select FPortCode,FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
    		" where FCheckState = '1') f on a.FPortCode=f.FPortCode " +
    		" left join (select FUserCode,FUserName from " + pub.yssGetTableName("Tb_Sys_UserList") + 
    		" ) g on a.FSupervisionUserCode = g.FUserCode " +
    		buildFilterSql() +
    		//edit by songjie 2012.02.03 报未明确到列
    		" order by a.FCheckTime ";
            
        return this.builderListViewData(strSql);
	}

	public String getListViewData2() throws YssException {
		
		return null;
	}

	public String getListViewData3() throws YssException {
		
		return null;
	}

	public String getListViewData4() throws YssException {
		
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		
		return null;
	}

	public String getTreeViewData1() throws YssException {
		
		return null;
	}

	public String getTreeViewData2() throws YssException {
		
		return null;
	}

	public String getTreeViewData3() throws YssException {
		
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		
		return null;
	}
	
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '%" +
                    filterType.strPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strDateFormat.length() != 0) {
                sResult = sResult + " and a.FDateFormat like '%" +
                    filterType.strDateFormat.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strTransferPath.length() != 0) {
                sResult = sResult + " and a.FTransferPath like '%" + // wdy modify 使用模糊查询
                    filterType.strTransferPath.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strUserCode.length() != 0) {
                sResult = sResult + " and a.FSupervisionUserCode like '" + // wdy modify 使用模糊查询
                    filterType.strUserCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strTrusteeCode.length() != 0) {
                sResult = sResult + " and a.FTrusteeCode like '%" + // wdy modify 使用模糊查询并把模糊查询修改为:like '%XX%'
                    filterType.strTrusteeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strLinkManCode.length() != 0) {
                sResult = sResult + " and a.FLinkManCode like '%" + // wdy modify 使用模糊查询并把模糊查询修改为:like '%XX%'
                    filterType.strLinkManCode.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
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
            throw new YssException("获取提示函设置信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

	public void setListViewData(ResultSet rs) throws SQLException
	{
    	this.strPortCode = rs.getString("FPortCode");
        this.strPortName = rs.getString("FPortName");
        this.strDateFormat = rs.getString("FDateFormat");
        this.strTransferPath = rs.getString("FTransferPath");
        this.strUserCode = rs.getString("FSupervisionUserCode");
        this.strUserName = rs.getString("UserName");
        this.strTrusteeCode = rs.getString("FTrusteeCode");
        this.strTrusteeName = rs.getString("FTrusteeName");
        this.strLinkManCode = rs.getString("FLinkManCode");
        this.strLinkManName = rs.getString("FLinkManName");
        super.setRecLog(rs);
		
	}
	
	public String getPortCode() {
		return strPortCode;
	}

	public void setPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}

	public String getPortName() {
		return strPortName;
	}

	public void setPortName(String strPortName) {
		this.strPortName = strPortName;
	}

	public String getUserCode() {
		return strUserCode;
	}

	public void setUserCode(String strUserCode) {
		this.strUserCode = strUserCode;
	}

	public String getStrUserName() {
		return strUserName;
	}

	public void setUserName(String strUserName) {
		this.strUserName = strUserName;
	}

	public String getTransferPath() {
		return strTransferPath;
	}

	public void setTransferPath(String transferPath) {
		strTransferPath = transferPath;
	}

	public String getTrusteeCode() {
		return strTrusteeCode;
	}

	public void setTrusteeCode(String trusteeCode) {
		strTrusteeCode = trusteeCode;
	}

	public String getTrusteeName() {
		return strTrusteeName;
	}

	public void setTrusteeName(String trusteeName) {
		strTrusteeName = trusteeName;
	}

	public String getLinkManCode() {
		return strLinkManCode;
	}

	public void setLinkManCode(String linkManCode) {
		strLinkManCode = linkManCode;
	}

	public String getLinkManName() {
		return strLinkManName;
	}

	public void setLinkManName(String linkManName) {
		strLinkManName = linkManName;
	}

	public String getDateFormat() {
		return strDateFormat;
	}

	public void setDateFormat(String dateFormat) {
		strDateFormat = dateFormat;
	}


	public PromptingBean getFilterType() {
		return filterType;
	}

	public void setFilterType(PromptingBean filterType) {
		this.filterType = filterType;
	}

	/**add---shashijie 2013-2-25 返回 oldPortCode 的值*/
	public String getOldPortCode() {
		return oldPortCode;
	}

	/**add---shashijie 2013-2-25 传入oldPortCode 设置  oldPortCode 的值*/
	public void setOldPortCode(String oldPortCode) {
		this.oldPortCode = oldPortCode;
	}


}
