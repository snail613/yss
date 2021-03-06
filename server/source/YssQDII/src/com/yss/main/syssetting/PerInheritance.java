package com.yss.main.syssetting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.OperationDataBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class PerInheritance extends BaseDataSettingBean implements
IDataSetting {

    private String sTrustor = "";     //委托人
    private String sTrustee = "";     //受托人
    private String sTrustorName = "";     //委托人名称
    private String sTrusteeName = "";     //受托人名称
    private java.util.Date dStartDate;//开始日期
    private java.util.Date dEndDate;//结束日期
    private String sPortCodeList = "";    //组合集合
    private String sOldTrustor = "";     //委托人
    private String sOldTrustee = "";     //受托人
    private java.util.Date dOldEndDate;//结束日期
    private java.util.Date dOldStartDate;//开始日期

    private PerInheritance filterType;
	private String sRecycled = "";


	public String getTrustor() {
		return sTrustor;
	}

	public void setTrustor(String sTrustor) {
		this.sTrustor = sTrustor;
	}

	public String getTrustee() {
		return sTrustee;
	}

	public void setTrustee(String sTrustee) {
		this.sTrustee = sTrustee;
	}

	public java.util.Date getStartDate() {
		return dStartDate;
	}

	public void setStartDate(java.util.Date dStartDate) {
		this.dStartDate = dStartDate;
	}

	public java.util.Date getEndDate() {
		return dEndDate;
	}

	public void setEndDate(java.util.Date dEndDate) {
		this.dEndDate = dEndDate;
	}

	public String getPortCodeList() {
		return sPortCodeList;
	}

	public void setPortCodeList(String sPortCodeList) {
		this.sPortCodeList = sPortCodeList;
	}

	public String getOldTrustor() {
		return sOldTrustor;
	}

	public void setOldTrustor(String sOldTrustor) {
		this.sOldTrustor = sOldTrustor;
	}

	public String getOldTrustee() {
		return sOldTrustee;
	}

	public void setOldTrustee(String sOldTrustee) {
		this.sOldTrustee = sOldTrustee;
	}

	public java.util.Date getOldEndDat() {
		return dOldEndDate;
	}

	public void setdOldEndDat(java.util.Date dOldEndDat) {
		this.dOldEndDate = dOldEndDat;
	}

	public java.util.Date getOldStartDate() {
		return dOldStartDate;
	}

	public void setOldStartDate(java.util.Date dOldStartDate) {
		this.dOldStartDate = dOldStartDate;
	}
	
	public String getAssetGroupTreeViewData(String sRequest) throws YssException
	{
		String sCurrentUser = "";
		String sReturn = "";
		
		sCurrentUser = pub.getUserCode();
		
		AssetGroupBean cgb = new AssetGroupBean();
		try
		{
			pub.setUserCode(sRequest);
			cgb.setYssPub(pub);
			
			sReturn = cgb.getTreeViewData();
			
			return sReturn;	
		}
		catch(Exception ye)
		{
			throw new YssException("获取委托人所有组合出错：" + ye.getMessage());
		}
		finally
		{
			pub.setUserCode(sCurrentUser);
		}	
	}

	public String addSetting() throws YssException {
		Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        ResultSet rs = null;
        String sRecord = "";
        
        try {
        	
            strSql = "insert into tb_sys_PerInheritance " +
                "(FTrustor,FTrustee,FStartDate,FEndDate,FPortCodeList," +
                "FCheckState,FCreator,FCreateTime)" +
                " values(" + 
                dbl.sqlString(this.sTrustor) + "," +
                dbl.sqlString(this.sTrustee) + "," +
                dbl.sqlDate(this.dStartDate) + "," +
                dbl.sqlDate(this.dEndDate) + "," +
                dbl.sqlString(this.sPortCodeList) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) +
                ")";
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (YssException ex) {
            throw new YssException("新增权限继承设置出错：" + ex.getMessage());
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("新增权限继承设置出错!" + e.getMessage());
		} finally {
            dbl.endTransFinal(con, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return "";
	}

	//受托人字段属于多选项，可能会有逗号的存在，导致dbFun.checkInputCommon无法正常进行解析。在这里手动进行是否重复数据的判断
	public void checkInput(byte btOper) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			if (this.sOldTrustor.equals(this.sTrustor) && this.sOldTrustee.equals(this.sTrustee) 
					&& YssFun.formatDate(this.dOldStartDate).equals(YssFun.formatDate(this.dStartDate )) && YssFun.formatDate(this.dOldEndDate).equals(YssFun.formatDate(this.dEndDate)))
			{
				return;
			}
			strSql = "select * from tb_sys_PerInheritance where FTrustor = " + dbl.sqlString(this.sTrustor) + " and FTrustee = " + dbl.sqlString(this.sTrustee) +
					 " and FStartDate = " + dbl.sqlDate(this.dStartDate) + " and FEndDate = " + dbl.sqlDate(dEndDate);
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next())
			{
				String sCheckState = "";
				int iCheckStateFlag = rs.getInt("FCHECKSTATE");
				if (iCheckStateFlag == 0)
				{
					sCheckState = "未审核";
				}
				if (iCheckStateFlag == 1)
				{
					sCheckState = "已审核";
				}
				if (iCheckStateFlag == 2)
				{
					sCheckState = "回收站";
				}
				throw new YssException(sCheckState + "中已存在【" + this.sTrustor + "，" + this.sTrustee + "，" + YssFun.formatDate(this.dStartDate) + "，" + YssFun.formatDate(this.dEndDate) + "】的数据！");
			}
		}
		catch(Exception ye)
		{
			throw new YssException(ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
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
                sql = "update tb_sys_PerInheritance" +
                    " set FCheckState=" + this.checkStateId + ",FCheckUser=" +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime=" +
                    dbl.sqlString(this.checkTime) + 
                    " where FTrustor = " + dbl.sqlString(this.sOldTrustor) + " and FTrustee = " + dbl.sqlString(this.sOldTrustee) +
                    " and FStartDate = " + dbl.sqlDate(this.dOldStartDate) + " and FEndDate = " + dbl.sqlDate(this.dOldEndDate);
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
            throw new YssException("审核权限继承设置出错：" + ex.getMessage());//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
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
            strSql = "update tb_sys_PerInheritance" +
                " set FCheckState = 2 " +
                " where FTrustor = " + dbl.sqlString(this.sOldTrustor) + " and FTrustee = " + dbl.sqlString(this.sOldTrustee) +
                " and FStartDate = " + dbl.sqlDate(this.dOldStartDate) + " and FEndDate = " + dbl.sqlDate(this.dOldEndDate);
            dbl.executeSql(strSql);

            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("删除权限继承设置出错：" + ex.getMessage());
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
                strSql = "delete tb_sys_PerInheritance" +
                " where FTrustor = " + dbl.sqlString(this.sOldTrustor) + " and FTrustee = " + dbl.sqlString(this.sOldTrustee) +
                " and FStartDate = " + dbl.sqlDate(this.dOldStartDate) + " and FEndDate = " + dbl.sqlDate(this.dOldEndDate);
                dbl.executeSql(strSql);
            }
            con.commit();
            con.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("清除权限继承设置出错：" + e.getMessage());
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
		
	}

	public String editSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        ResultSet rs = null;
        String strSql = "";
        try
        {
        con.setAutoCommit(false);
        bTrans = true;
        strSql = "update tb_sys_PerInheritance " +
            " set FTrustor = " + dbl.sqlString(this.sTrustor) +
            ",FTrustee=" + dbl.sqlString(this.sTrustee) +
            ",FStartDate=" + dbl.sqlDate(this.dStartDate) +
            ",FEndDate=" + dbl.sqlDate(this.dEndDate) +
            ",FPortCodeList=" + dbl.sqlString(this.sPortCodeList) +
            ",FCheckstate= " +
            (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
            dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
            dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
            (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
            " where FTrustor = " + dbl.sqlString(this.sOldTrustor) + " and FTrustee = " + dbl.sqlString(this.sOldTrustee) +
            " and FStartDate = " + dbl.sqlDate(this.dOldStartDate) + " and FEndDate = " + dbl.sqlDate(this.dOldEndDate);
        dbl.executeSql(strSql);
        con.commit();
        bTrans = false;
        con.setAutoCommit(true);
    } catch (Exception ex) {
        throw new YssException("修改权限继承设置出错：" + ex.getMessage());
    } finally {
        dbl.endTransFinal(con, bTrans);
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

	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		buf.append(this.sTrustor).append("\t");
		buf.append(this.sTrustorName).append("\t");
		buf.append(this.sTrustee).append("\t");
		buf.append(YssFun.formatDate(this.dStartDate, YssCons.YSS_DATEFORMAT)).append("\t");
		buf.append(YssFun.formatDate(this.dEndDate, YssCons.YSS_DATEFORMAT)).append("\t");
		buf.append(this.sPortCodeList).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
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
            sRecycled = sTmpStr;
            reqAry = sTmpStr.split("\t");
            if (reqAry.length < 9) {
                return;
            }
            
            this.sTrustor = reqAry[0];
            this.sTrustee = reqAry[1];
            this.dStartDate = YssFun.toDate(reqAry[2]);
            this.dEndDate = YssFun.toDate(reqAry[3]);
            this.sPortCodeList = reqAry[4];
            this.sOldTrustor = reqAry[5];
            this.sOldTrustee = reqAry[6];
            this.dOldStartDate = YssFun.toDate(reqAry[7]);
            this.dOldEndDate = YssFun.toDate(reqAry[8]);
            this.checkStateId = YssFun.toInt(reqAry[9]);
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new PerInheritance();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析业务数据设置请求出错", e);
        }

		
	}
	
	private String buildFilterSql() throws YssException 
	{
		String sReturn = "";
		
		if (this.filterType != null) {
			sReturn = " where 1=1 ";
            if (this.filterType.sTrustor.length() != 0) {
            	sReturn = sReturn + " and a.FTrustor like '%" + 
                    filterType.sTrustor + "%'";
            }
            if (this.filterType.sTrustee.length() != 0) {
            	sReturn = sReturn + " and FTrustee like '%" +
                    filterType.sTrustee + "%'";
            }
            if (this.filterType.dStartDate != null &&
                    !this.filterType.dStartDate.equals(YssFun.toDate("9998-12-31"))) {
            	sReturn = sReturn + " and a.FStartDate = " +
                        dbl.sqlDate(filterType.dStartDate);
            }
            if (this.filterType.dEndDate != null &&
                    !this.filterType.dEndDate.equals(YssFun.toDate("9998-12-31"))) {
            	sReturn = sReturn + " and a.FEndDate = " +
                        dbl.sqlDate(filterType.dEndDate);
            }

        }
		
		return sReturn;
	}

	public String getListViewData1() throws YssException {
		String strSql = "";
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
        	
        	strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FUserName as FTrustorName,e.FUserName as FTrusteeName from tb_sys_PerInheritance a " +
			   		 " left join (select FUserCode,FUserName from " + pub.yssGetTableName("Tb_Sys_UserList") + ") b on a.FCreator = b.FUserCode " +
					 " left join (select FUserCode,FUserName from " + pub.yssGetTableName("Tb_Sys_UserList") + ") c on a.FCheckUser = c.FUserCode " +
			   		 " left join (select FUserCode,FUserName from " + pub.yssGetTableName("Tb_Sys_UserList") + ") d on a.FTrustor = d.FUserCode " +
			   		 " left join (select FUserCode,FUserName from " + pub.yssGetTableName("Tb_Sys_UserList") + ") e on a.FTrustee = e.FUserCode " +
			   		buildFilterSql() +
			   		 " order by FCheckTime ";
        	
            sHeader = "委托人代码\t委托人名称\t受托人代码\t开始时间\t结束时间\t制作人\t制作时间\t审核人\t审核时间";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	bufShow.append(rs.getString("FTrustor")).append("\t");
            	bufShow.append(rs.getString("FTrustorName")).append("\t");
            	bufShow.append(rs.getString("FTrustee")).append("\t");
            	bufShow.append(rs.getString("FStartDate")).append("\t");
            	bufShow.append(rs.getString("FEndDate")).append("\t");
            	bufShow.append(rs.getString("FCREATOR")).append("\t");
            	bufShow.append(rs.getString("FCREATETIME")).append("\t");
            	bufShow.append(rs.getString("FCHECKUSER")).append("\t");
            	bufShow.append(rs.getString("FCHECKTIME")).append("\t").append(YssCons.
                        YSS_LINESPLITMARK);
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
                "FTrustor	FTrustorName	FTrustee	FStartDate	FEndDate	FCREATOR	FCREATETIME	FCHECKUSER	FCHECKTIME";
        } catch (Exception e) {
            throw new YssException("获取套账信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	
	private void setListViewData(ResultSet rs) throws SQLException
	{
		this.sTrustor = rs.getString("FTrustor");
		this.sTrustorName = rs.getString("FTrustorName");
		this.sTrustee = rs.getString("FTrustee");
		this.sTrusteeName = rs.getString("FTrusteeName");
		this.dStartDate = rs.getDate("FStartDate");
		this.dEndDate = rs.getDate("FEndDate");
		this.sPortCodeList = rs.getString("FPortCodeList");
		
		super.setRecLog(rs);
	}
	
	public String ConvertTrusteeNames(String sRequest) throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		String[] sUserCodeList;
		String sUserCode = "";
		
		try
		{
			if (sRequest == null || sRequest.trim().equals(""))
			{
				return "";
			}
			sUserCodeList = sRequest.split(",");
			for (int i = 0; i < sUserCodeList.length; i++)
			{
				sUserCode = sUserCode + "'" + sUserCodeList[i] + "',";
			}
			if (sUserCode.trim().length() > 3)
			{
				sUserCode = sUserCode.substring(0,sUserCode.length() - 1);
				strSql = "Select * from Tb_Sys_UserList where FUserCode in (" + sUserCode +")";
				rs = dbl.queryByPreparedStatement(strSql);
				while(rs.next())
				{
					sReturn = sReturn + rs.getString("FUserName") + ",";
				}
				if (sReturn.trim().length() > 2)
				{
					sReturn = sReturn.substring(0,sReturn.length() - 1);
				}
			}
		}
		catch(Exception ye)
		{
			
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return sReturn;
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

}
