//20110616 Added by liubo.Story #963 
//“辅助核算项对应关系设置”界面所使用的BEAN
package com.yss.main.voucher;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchAssistantSettingBean extends BaseDataSettingBean implements IDataSetting{
	private String SettingCode = "";      //项目编号
    private String OldSettingCode = "";   //待修改的项目编号
    private String AuxiAccTB = "";        //辅助核算项目所在的项目表
    private String AuxiAccID = "";        //辅助核算项目编号
    private String AuxiAccName = "";      //项目名称
    private String Exchange  = "";
    
    public VchAssistantSettingBean() //add by yeshenghong bug3107
    {
    }
    
    public String getExchange() {
		return Exchange;
	}

	public void setExchange(String exchange) {
		Exchange = exchange;
	}

	public String[] getAllReqAry() {
		return allReqAry;
	}

	public void setAllReqAry(String[] allReqAry) {
		this.allReqAry = allReqAry;
	}

	public String[] getOneReqAry() {
		return oneReqAry;
	}

	public void setOneReqAry(String[] oneReqAry) {
		this.oneReqAry = oneReqAry;
	}

	private String sRecycled = "";

    private String Desc = "";           //描述(预留字段)

    private String BookSetCode = "";      //套帐号
    private String BookSetName = "";	  //套帐名

    String[] allReqAry = null;
    String[] oneReqAry = null;

    public String getBookSetName() {
		return BookSetName;
	}

	public void setBookSetName(String bookSetName) {
		BookSetName = bookSetName;
	}

	public String getSettingCode() {
		return SettingCode;
	}

	public void setSettingCode(String settingCode) {
		SettingCode = settingCode;
	}


	public String getOldSettingCode() {
		return OldSettingCode;
	}

	public void setOldSettingCode(String oldSettingCode) {
		OldSettingCode = oldSettingCode;
	}

	public String getAuxiAccTB() {
		return AuxiAccTB;
	}

	public void setAuxiAccTB(String auxiAccTB) {
		AuxiAccTB = auxiAccTB;
	}

	public String getAuxiAccID() {
		return AuxiAccID;
	}

	public void setAuxiAccID(String auxiAccID) {
		AuxiAccID = auxiAccID;
	}

	public String getAuxiAccName() {
		return AuxiAccName;
	}

	public void setAuxiAccName(String auxiAccName) {
		AuxiAccName = auxiAccName;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public String getBookSetCode() {
		return BookSetCode;
	}

	public void setBookSetCode(String bookSetCode) {
		BookSetCode = bookSetCode;
	}

	public String getM_sBookSetName() {
		return m_sBookSetName;
	}

	public void setM_sBookSetName(String mSBookSetName) {
		m_sBookSetName = mSBookSetName;
	}

	public String getCuryCode() {
		return CuryCode;
	}

	public void setCuryCode(String fCuryCode) {
		CuryCode = fCuryCode;
	}

	public String getCuryName() {
		return CuryName;
	}

	public void setCuryName(String fCuryName) {
		CuryName = fCuryName;
	}

	public String getCatCode() {
		return CatCode;
	}

	public void setCatCode(String fCatCode) {
		CatCode = fCatCode;
	}

	public String getCatName() {
		return CatName;
	}

	public void setCatName(String fCatName) {
		CatName = fCatName;
	}

	private String m_sBookSetName = "";      //套帐名称

    private String CuryCode = "";        //币种代码

    private String CuryName = "";        //币种名称
    private String CatCode = "";         //证券品种代码
    private String CatName = "";         //证券品种名称
	private VchAssistantSettingBean filterType = null;
    
	//20110623 Added by liubo. Story #963
	//首先查询数据库中是否存在套账号、币种代码、证券代码、交易所4项信息与新录入的数据是否完全一致，若完全一致，需要告知用户并退出新增操作。
	public String addSetting() throws YssException
	{
		String strSql = "";
        ResultSet rs = null;
        String sRecord = "";
        try 
        {
        	strSql = "Select count(*) as CNT from " + pub.yssGetTableName("Tb_Vch_AssistantSetting") +
			" where FBookSetCode = " + dbl.sqlString(this.BookSetCode) + " and " +
			" FCuryCode = " + dbl.sqlString(this.CuryCode) + " and " +
			" FCatCode = " + dbl.sqlString(this.CatCode) + " and " +
			" FExchange = " + dbl.sqlString(this.Exchange);
        	rs = dbl.openResultSet(strSql);
	
			while(rs.next())
			{
		        if(rs.getInt("CNT") > 0)
		        {
		        	throw new YssException("系统中已存在【套账号： " + this.BookSetCode+ ",证券： " + this.CatCode + ",货币：" + this.CuryCode + ",交易所：" + getExchangeValue() + "】的设置信息");
		        }
			}
			addAuxiAccSetting();
	    }
        catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return "";
	}
	
    private String getExchangeValue()
    {

        if (this.Exchange.equals("1"))
        {
            return "交易所";
        }
        else if (this.Exchange.equals("2"))
        {
            return "银行间";
        }
        else
        {
            return "空";
        }
    }
	

	//20110623 Added by liubo. Story #963
	//通过此方法新增配置记录
	public String addAuxiAccSetting() throws YssException {
		Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        ResultSet rs = null;
        String sRecord = "";
        
        try {
        	
//        	checkInputForSettingAdd(YssCons.OP_ADD);
        	
        	
            strSql = "insert into " + pub.yssGetTableName("Tb_Vch_AssistantSetting") +
                "(FSettingCode,FBookSetCode,FCuryCode,FCatCode,FAuxiAccTb," +
                " FAuxiAccID,Fexchange" +
                ",FDesc,FCheckState,FCreator,FCreateTime,FCheckUser )" +
                " values(" + dbl.sqlString(this.SettingCode) + "," +
                dbl.sqlString(this.BookSetCode) + "," +
                dbl.sqlString(this.CuryCode) + "," +
                dbl.sqlString(this.CatCode) + "," +
                dbl.sqlString(this.AuxiAccTB) + "," +
                
                dbl.sqlString(this.AuxiAccID) + "," +
                dbl.sqlString(this.Exchange) + "," +
                dbl.sqlString(this.Desc) + "," +
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
        } catch (YssException ex) {
            throw new YssException("新增辅助核算项设置出错!");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("新增辅助核算项设置出错!");
		} finally {
            dbl.endTransFinal(con, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return "";
	}

	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Vch_AssistantSetting"),
                "FSettingCode", this.SettingCode,
                this.OldSettingCode);
		
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
	                sql = "update " + pub.yssGetTableName("Tb_Vch_assistantsetting") +
	                    " set FCheckState=" + this.checkStateId + ",FCheckUser=" +
	                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime=" +
	                    dbl.sqlString(this.checkTime) + " where FSettingCode=" +
	                    dbl.sqlString(this.SettingCode);
	                dbl.executeSql(sql);
	            }
//	         con.setAutoCommit(false);
//	         bTrans = true;
//	         dbl.executeSql(sql);
	            //===============2008-10-21
	            con.commit();
	            bTrans = false;
	            con.setAutoCommit(true);
	        } catch (Exception ex) {
			    //new YssException("审核辅助核算项设置信息出错!");
	            throw new YssException("审核辅助核算项设置信息出错!");//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
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
            strSql = "update " + pub.yssGetTableName("Tb_Vch_AssistantSetting") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSettingCode = " + dbl.sqlString(this.SettingCode);
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
                strSql = "delete " + pub.yssGetTableName("Tb_Vch_AssistantSetting") +
                    " where FSettingCode=" + dbl.sqlString(this.SettingCode);
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
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        ResultSet rs = null;
        String strSql = "";
        try {
        	strSql = "Select count(*) as CNT from " + pub.yssGetTableName("Tb_Vch_AssistantSetting") +
			" where FBookSetCode = " + dbl.sqlString(this.BookSetCode) + " and FSettingCode <> " + dbl.sqlString(this.OldSettingCode) + " and " +
			" FCuryCode = " + dbl.sqlString(this.CuryCode) + " and " +
			" FCatCode = " + dbl.sqlString(this.CatCode) + " and " +
			" FExchange = " + dbl.sqlString(this.Exchange);
        	rs = dbl.openResultSet(strSql);
	
			while(rs.next())
			{
		        if(rs.getInt("CNT") > 0)
		        {
		        	throw new YssException("系统中已存在【套账号： " + this.BookSetCode+ ",证券： " + this.CatCode + ",货币：" + this.CuryCode + ",交易所：" + getExchangeValue() + "】的设置信息!");
		        }
			}
            con.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Vch_AssistantSetting") +
                " set FSettingCode = " + dbl.sqlString(this.SettingCode) +
                ",FBookSetCode=" + dbl.sqlString(this.BookSetCode) +
                ",FCuryCode=" + dbl.sqlString(this.CuryCode) +
                ",FCatCode=" + dbl.sqlString(this.CatCode) +
                ",FAuxiAccTb=" + dbl.sqlString(this.AuxiAccTB) +
                ",FAuxiAccID=" + dbl.sqlString(this.AuxiAccID) +
                ",FExchange=" + dbl.sqlString(this.Exchange) +
                ",FDesc=" + dbl.sqlString(this.Desc) + ",FCheckstate= " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FSettingCode = " + dbl.sqlString(this.OldSettingCode);
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
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_AssistantSetting") +
                " where FSettingCode = " + dbl.sqlString(this.SettingCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
            	this.SettingCode = rs.getString(SettingCode);
                this.BookSetCode = rs.getString("FBookSetCode");
                this.CuryCode = rs.getString("FCuryCode");
                this.CatCode = rs.getString("FCatCode");
                this.AuxiAccTB = rs.getString("FAuxiAccTb");
                this.AuxiAccID = rs.getString("FAuxiAccID");
                this.Exchange = rs.getString("FExchange");
                this.Desc = rs.getString("FDESC");
            }
            return null;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
        buf.append(this.SettingCode).append("\t");

        buf.append(this.BookSetCode).append("\t");
        buf.append(this.CuryCode).append("\t");
        buf.append(this.CuryName).append("\t");
        buf.append(this.CatCode).append("\t");
        buf.append(this.CatName).append("\t");
        buf.append(this.AuxiAccID).append("\t");
        buf.append(this.AuxiAccName).append("\t");
        buf.append(this.AuxiAccTB).append("\t");

        buf.append(this.Desc).append("\t");
        buf.append(this.Exchange).append("\t");


        buf.append(super.buildRecLog());

        return buf.toString();
	}
	

	public String getOperValue(String sType) throws YssException {
		try {
            if (sType != null && sType.equalsIgnoreCase("copy")) {
                String sOldSettingCode = "";
                int iCheckState = 0;
                iCheckState = (pub.getSysCheckState() ? 0 : 1);
                sOldSettingCode = this.OldSettingCode;
                OldSettingCode = "";
                this.checkInput(YssCons.OP_ADD);
                copySettingData(sOldSettingCode, iCheckState);
                addSetting();
                return this.getListViewData1();
            }
        } catch (Exception ex) {
            throw new YssException(ex.toString());
        }
        return "";
	}
	
	////20110616 Added by liubo.Story #963 
	//此该方法将从前台接受一个由a+系统年份+套账号+AuxiAccSet组成的辅助核算项表名，查询出该表的一级项，并返回前台。若查无该表，则返回查无此表的EXCEPTION
	//系统年份默认为套账存在的最大年份
	public String AuxiaccsetCheck(String AuxiaccsetTBName) throws YssException
	{	
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
        	
        	String[] sBookSetCode  = AuxiaccsetTBName.split(",");

            sHeader = "辅助核算项目代码\t辅助核算项目名称";
        	if (sBookSetCode.length > 0) 
        	{ 
        		
        		for (int i = 0;i < sBookSetCode.length;i++ )
        		{	
            		String tablePrefix = this.getTablePrefix(sBookSetCode[i]);//modified by yeshenghong 20120213 BUG3724
        			String sTBName = tablePrefix + "auxiaccset";
        			if (dbl.yssTableExist(sTBName))
        			{
//			            strSql = "Select AUXIACCID,AUXIACCNAME from " + sTBName + " where auxiaccid in (select FTradeTypeCode from Tb_Base_TradeType)order by auxiaccid";
        				strSql = "Select AUXIACCID,AUXIACCNAME from " + sTBName + " where length(auxiaccid) = 2 order by auxiaccid";
        				//modified by yeshenghong 20120221 BUG3834
			            rs = dbl.openResultSet(strSql);
			            while (rs.next()) {
			                bufShow.append( (rs.getString("AUXIACCID") + "").trim()).append("\t");
			                bufShow.append( (rs.getString("AUXIACCNAME") + "").trim()).append("\t");
			                bufShow.append(YssCons.YSS_LINESPLITMARK);
			                this.setAssistantSetting(rs, sTBName);
			                this.checkStateId = 1;  //add by yeshenghong 否则无法自动查找
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
	        			break;
        			}
        		}
        			
	            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        	}
        	else
        	{
//        		String sTBName = sBookSetCode[0];
//        		if (dbl.yssTableExist(sTBName))
//    			{
//		            strSql = "Select AUXIACCID,AUXIACCNAME from " + sTBName + " where auxiaccid in (select FTradeTypeCode from Tb_Base_TradeType)order by auxiaccid";
//		            rs = dbl.openResultSet(strSql);
//		            while (rs.next()) {
//		                bufShow.append( (rs.getString("AUXIACCID") + "").trim()).append("\t");
//		                bufShow.append( (rs.getString("AUXIACCNAME") + "").trim()).append("\t");
//		                bufShow.append(YssCons.YSS_LINESPLITMARK);
//		                this.setAssistantSetting(rs, sTBName);
//		                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
//		            }
//		            if (bufShow.toString().length() > 2) {
//		                sShowDataStr = bufShow.toString().substring(0,
//		                    bufShow.toString().length() - 2);
//		            }
//		
//		            if (bufAll.toString().length() > 2) {
//		                sAllDataStr = bufAll.toString().substring(0,
//		                    bufAll.toString().length() - 2);
//		            }
//    			}
        		return sHeader + "\r\f"; 		
        	}
        } catch (YssException e) {
            throw new YssException("不存在以该套帐名和系统年份生成的辅助核算项表！");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("不存在以该套帐名和系统年份生成的辅助核算项表！");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
		
	}
	//add by yeshenghong 20120213 BUG3724
	private String getTablePrefix(String setCode) throws YssException
	{
		int cwSetCode = Integer.parseInt(setCode);
		String strSql = "select max(fyear) as fyear from lsetlist where fsetcode = " + cwSetCode;
		ResultSet rs = null;
		String yearStr = "";
		try {
			rs = dbl.openResultSet(strSql);
			if(rs.next())
			{
				yearStr = rs.getString("fyear");
			}

			return  "a" + yearStr + setCode; 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 throw new YssException("获取套账年份失败！");
		}finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	//20110616 Added by liubo.Story #963
	//该方法根据前台传入的组合代码，返回套账号和套帐名称
	public String BookSetCheck(String PortCode) throws YssException
	{	
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "套帐代号\t套帐名称";
            strSql = "Select a.FSETCODE as FBOOKSETCODE,a.FSETNAME as FBOOKSETNAME,p.FDesc as FDesc,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCuryName as FCuryName,d.FCuryCode as FCuryCode " +
            		" from lsetlist a join " + pub.yssGetTableName("Tb_Para_Portfolio") + " p on a.fsetid = p.fassetcode " + 
            		" left join (select FUserCode,FUserName from Tb_Sys_UserList)b on p.FCreator = b.FUserCode " +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList)c on p.FCheckUser = c.FUserCode" +//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
                    " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ") d on p.FPortCury=d.FCuryCode" +
//                    " where a.FBookSetCode in (Select FBookSetCode from " + pub.yssGetTableName("tb_vch_portsetlink") +
            		" where p.FPortCode = '" + PortCode + "' and p.FCHECKSTATE = 1 " +
                    " order by p.FCheckState, p.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FBOOKSETCODE") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FBOOKSETNAME") + "").trim()).append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.setVchBookSet(rs);
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
            throw new YssException(e.toString());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\f") >= 0) {
                allReqAry = sRowStr.split("\r\f");
                this.SettingCode = allReqAry[0].split("\t")[0];
            } else {
                sTmpStr = sRowStr;
                sRecycled = sTmpStr; //增加对回收站的处理功能 by leeyu 2008-10-21 BUG:0000491
                reqAry = sTmpStr.split("\t");
                this.SettingCode = reqAry[0];
                this.BookSetCode = reqAry[1];
                this.BookSetName = reqAry[2];
                this.CuryCode = reqAry[3];
                this.CatCode = reqAry[4];

                this.AuxiAccTB = reqAry[5];
                this.AuxiAccID = reqAry[6];
                this.Desc = reqAry[7];
                this.checkStateId = Integer.parseInt(reqAry[8]);
                this.Exchange = reqAry[9];
                this.OldSettingCode = reqAry[10];

                super.parseRecLog();
                if (sRowStr.indexOf("\r\t") >= 0) {
                    if (this.filterType  == null) {
                        this.filterType = new VchAssistantSettingBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }

            }
        } catch (Exception e) {
            throw new YssException("解析辅助核算项设置信息出错!");
        }
		
	}

	public String getListViewData1() throws YssException {
		String strSql = "";
        strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCuryName as FCuryName , " +
        		 " g.FCatName as FCatName from " + pub.yssGetTableName("Tb_vch_AssistantSetting") + " a " +
        		 " left join (select FUserCode,FUserName from " + pub.yssGetTableName("Tb_Sys_UserList") + ") b on a.FCreator = b.FUserCode " +
        		 " left join (select FUserCode,FUserName from " + pub.yssGetTableName("Tb_Sys_UserList") + ") c on a.FCheckUser = c.FUserCode " +
        		 " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ") d on a.FCuryCode=d.FCuryCode " +
        		 " left join (select FCatCode,FCatName from Tb_Base_Category) g on a.fcatcode = g.FCatCode " +
        		 buildFilterSql() +
        		 " order by FCheckTime ";
            
        return this.builderListViewData(strSql);
	}
	
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.BookSetCode.length() != 0) {
                sResult = sResult + " and a.FBookSetCode like '%" +
                    filterType.BookSetCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.CuryCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '%" +
                    filterType.CuryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.CatCode.length() != 0) {
                sResult = sResult + " and a.FCatCode like '%" + // wdy modify 使用模糊查询
                    filterType.CatCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.Exchange.length() != 0) {
                sResult = sResult + " and a.FExchange like '" + // wdy modify 使用模糊查询
                    filterType.Exchange.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.Desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '%" + // wdy modify 使用模糊查询并把模糊查询修改为:like '%XXX%'
                    filterType.Desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }
	
	

	public String getListViewData2() throws YssException {
		
		return "";
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


	private void copySettingData(String oldSettingCode, int iCheckState) throws YssException {
	    Connection conn = dbl.loadConnection();
	    boolean bTrans = false;
	    String strSql = "";
	    try {
//	        /onn = dbl.loadConnection();
	        conn.setAutoCommit(bTrans);
	        bTrans = true;
	        strSql = "insert into " + pub.yssGetTableName("Tb_Vch_AssistantSetting") + " " +
	            " (FSettingCode,FBookSetCode,FCuryCode,FCatCode, " +
	            " FAuxiAccTb,FAuxiAccID,FExchange,FDESC,FCreator,FCreatetime, " +
	            " FCHECKSTATE,FCHECKUSER,FCHECKTIME) " +
	            " select " +
	            dbl.sqlString(this.SettingCode) + ",FBookSetCode,FCuryCode,FCatCode," +
	            " FAuxiAccTb,FAuxiAccID,FExchange,FDESC,FCreator,FCreatetime," + iCheckState + ", " +
	            " FCHECKUSER,FCHECKTIME from " + pub.yssGetTableName("Tb_Vch_AssistantSetting") + " " +
	            " where FCheckState<>2 and FSettingCode=" + dbl.sqlString(this.OldSettingCode);
	        dbl.executeSql(strSql);
	        conn.commit();
	        conn.setAutoCommit(bTrans);
	        bTrans = false;
	    } catch (Exception ex) {
	        try {
	        	if(conn != null) //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
	        		conn.rollback();
	        } catch (SQLException sqle) {
	            throw new YssException("复制辅助核算项设置新增数据出错！");
	        }
	        throw new YssException("复制辅助核算项设置数据出错！");
	    } finally {
	        dbl.endTransFinal(conn, bTrans);
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
	//20110616 Added by liubo.Story #963
	//该方法根据前台传回的辅助核算项目代码和辅助核算项表名，查询辅助核算项名称
	public String ReturnAuxiAccName() throws YssException
	{
		String strSql = "";
		ResultSet rs = null;
		ResultSet rs1 = null;
		
		String sAuxiAccName = "";
		String sBookSetName = "";
        try {          
    		String sBookSetCode[] = this.BookSetCode.split(",");
			for (int i = 1;i <= sBookSetCode.length;i++ )
			{	
				String sTBName = "a" + AuxiAccTB + sBookSetCode[i-1] + "auxiaccset";
				if (dbl.yssTableExist(sTBName))
				{
		            strSql = "Select AUXIACCNAME from " + sTBName + " where AUXIACCID = '" + AuxiAccID + "'" +
		            		" And AuxiAccID in (select FTradeTypeCode from Tb_Base_TradeType)order by auxiaccid";
		            rs = dbl.openResultSet(strSql);
		            while (rs.next()) {
		            	sAuxiAccName = rs.getString("AUXIACCNAME");
		            }
	    			break;
				}
			}
			for (int i = 1;i <= sBookSetCode.length;i++ )
			{
				strSql = " SELECT FSETNAME FROM lsetlist where FSetCode = to_number('" + sBookSetCode[i-1] + "')";
				 //modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
				rs1 = dbl.openResultSet(strSql);
				while (rs1.next())
				{
					sBookSetName = sBookSetName + rs1.getString("FSETNAME") + ",";
				}
				
			}

			sBookSetName = sBookSetName.substring(0,sBookSetName.length()-1);
			
			return sAuxiAccName + "\t" + sBookSetName;
			
        }
        catch(YssException e)
        {
        	throw new YssException("获取辅助核算项出错!");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取辅助核算项出错!");
		}
        finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs1);
        }
	}

	//20110623 Added by liubo. Story #963
	//当“证券信息维护”模块进行反审核操作时，需要查询辅助核算项辅助配置表，查出是否存在对应的辅助核算项设置。
	//若存在，需要将所有配置数据中的套账号所对应的辅助核算项表中的数据删除
	public void checkAssSettingForUnAudit() throws YssException
	{
		String strSql = "";
		boolean bTrans = false;
		Connection conn = null;
		ResultSet rs = null;
		String auxiID = "";
		String secCode = "";
		HashMap secMap = new HashMap();
		try 
		{
			String[] auxiArray = this.AuxiAccID.split(",");
			String[] secArray = this.BookSetCode.split(",");//BUG5456 modified by yeshenghong improve efficiency of security info mainteinance
			for(int i=0;i<auxiArray.length;i++)
			{
//				auxiID += dbl.sqlString(auxiArray[i]) + ",";
				secCode = " AuxiAccID like '__" + secArray[i] + "%' ";
				if(secMap.containsKey(auxiArray[i]))
				{
					secCode = (String)secMap.get(auxiArray[i]);
					secCode += " or AuxiAccID like '__" + secArray[i] + "%' ";
				}
				secMap.put(auxiArray[i], secCode);
			}
			//change the sql so as to operate in batch modified by yeshenghong BUG5456 20120907
//			auxiID = " (" + auxiID.substring(0, auxiID.length()-1) + ") ";
			
//			secCode = " (" + secCode.substring(0, secCode.length()-1) + ") ";
			conn = dbl.getConnection();
			bTrans = true;
			conn.setAutoCommit(false);
			for(Iterator iter=secMap.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();
				auxiID = (String)entry.getKey();
				secCode = (String)entry.getValue();
//				secCode = " (" + secCode.substring(0, secCode.length()-1) + ") ";
				strSql = "Select * from " + pub.yssGetTableName("Tb_vch_AssistantSetting") +" where FCatCode = '" + auxiID + "'" +
						" and FCheckState = 1";
				rs = dbl.openResultSet(strSql);
				
				while(rs.next())
				{
					String sBookSet[] = rs.getString("FBookSetCode").split(",");
					for (int i = 0;i <= sBookSet.length - 1;i++)
					{
						String sTBName = "a" + this.AuxiAccTB + sBookSet[i] + "auxiaccset";
						if (dbl.yssTableExist(sTBName))
						{
//							strSql = "Delete from " + sTBName + " where AuxiAccID like '__" + this.BookSetCode + "'";
							strSql = "Delete from " + sTBName + " where " + secCode;
							dbl.executeSql(strSql);
							
						}
					}
				}
			}
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		}
		catch(Exception e)
		{
			throw new YssException("获取辅助核算项出错!");
		}finally
		{
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}
	

	//20110623 Added by liubo. Story #963
	//当“证券信息维护”模块进行审核操作时，需要查询辅助核算项辅助配置表，查出是否存在对应的辅助核算项设置。
	//若存在，需要将“证券代码加一级代码作为前缀”添加进对应的套帐的辅助核算项。若存在多条设置数据，则根据货币代码和交易所数据进行匹配
	public void checkAssSettingForAudit() throws YssException
	{
		String strSql = "";
		Connection conn = null;
		boolean bTrans = false;
		ResultSet rsCat = null;
		ResultSet rsBookSet = null;
		ResultSet rsAssSetting = null;
		try
		{
			strSql = "SELECT COUNT(FCATCODE) AS CNT,FCATCODE FROM " + pub.yssGetTableName("Tb_vch_AssistantSetting") +" GROUP BY FCATCODE" +
					" HAVING FCATCODE =" + dbl.sqlString(this.AuxiAccID);
			rsCat = dbl.openResultSet(strSql);
			
			while(rsCat.next())
			{
				
				if (rsCat.getInt("CNT") <= 0)
				{}
				else if (rsCat.getInt("CNT") == 1)
				{
					strSql = "Select * from " + pub.yssGetTableName("Tb_vch_AssistantSetting") + " where FCatCode = " + dbl.sqlString(rsCat.getString("FCATCODE")) +
							" and FCheckState = 1 ";
					rsBookSet = dbl.openResultSet(strSql);
					while (rsBookSet.next())
					{
						if (rsBookSet.getString("FCuryCode").equalsIgnoreCase(" ") || rsBookSet.getString("FCuryCode").equalsIgnoreCase(this.CuryCode))
						{
							if (ExchangeCode(rsBookSet.getString("FExchange")))
							{
								String sBookSet[] = rsBookSet.getString("FBookSetCode").split(",");
								for (int i = 0;i <= sBookSet.length - 1;i++)
								{
									String sTBName = "a" + this.AuxiAccTB + sBookSet[i] + "auxiaccset";
									if (dbl.yssTableExist(sTBName))
									{
										conn = dbl.loadConnection();
										conn.setAutoCommit(bTrans);
										strSql = "Delete from " + sTBName + " where AuxiAccID = '" + rsBookSet.getString("FAuxiAccID") + this.BookSetCode + "' and AuxiAccName = '" + this.BookSetName + "'";
										
										dbl.executeSql(strSql);
										
										strSql = "Insert into " + sTBName + " Values('" + rsBookSet.getString("FAuxiAccID") + this.BookSetCode + "','" + this.BookSetName + "','Added By System')";
										
							            dbl.executeSql(strSql);
							            
								        conn.commit();
								        conn.setAutoCommit(bTrans);
								        bTrans = false;
								        dbl.endTransFinal(conn, bTrans);
									}
								}
							}
						}
					}
				}
				else
				{	
					strSql = "Select * from " + pub.yssGetTableName("Tb_vch_AssistantSetting") + " where FCatCode = " + dbl.sqlString(rsCat.getString("FCATCODE")) +
							" and FCheckState = 1";
					rsBookSet = dbl.openResultSet(strSql);
					while (rsBookSet.next())
					{
						if (rsBookSet.getString("FCuryCode").equalsIgnoreCase(" ") || rsBookSet.getString("FCuryCode").equalsIgnoreCase(this.CuryCode))
						{
							if (ExchangeCode(rsBookSet.getString("FExchange")))
							{
									
									String sBookSet[] = rsBookSet.getString("FBookSetCode").split(",");
									for (int i = 0;i <= sBookSet.length - 1;i++)
									{
										String sTBName = "a" + this.AuxiAccTB + sBookSet[i] + "auxiaccset";
										if (dbl.yssTableExist(sTBName))
										{
											conn = dbl.loadConnection();
											conn.setAutoCommit(bTrans);
											strSql = "Delete from " + sTBName + " where AuxiAccID = '" + rsBookSet.getString("FAuxiAccID") + this.BookSetCode + "' and AuxiAccName = '" + this.BookSetName + "'";
											
											dbl.executeSql(strSql);
											
											strSql = "Insert into " + sTBName + " Values('" + rsBookSet.getString("FAuxiAccID") + this.BookSetCode + "','" + this.BookSetName + "','Added By System')";
											
								            dbl.executeSql(strSql);
								            
									        conn.commit();
									        conn.setAutoCommit(bTrans);
									        bTrans = false;
									        dbl.endTransFinal(conn, bTrans);
										}
									}
									break;								
							}
						}
					}
				}
			}
			
		}
		catch(YssException e)
        {
        	throw new YssException("获取辅助核算项出错!");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	throw new YssException("获取辅助核算项出错!");
		}
		finally
		{
            dbl.closeResultSetFinal(rsCat);
            dbl.closeResultSetFinal(rsBookSet);
		}
	}

	//20110623 Added by liubo. Story #963
	//此方法用于匹配数据库中记载的交易所数据与前台传回的交易所数据进行匹配。首先需要将前台传回的数据进行转换
	//转换规则为0>>空数据，1>>交易所,2>>银行间
	private boolean ExchangeCode(String sOldCode)
	{
		boolean blnResult = false;
		String sCodeForCompare = "";
		
		if (this.Desc.equals("CY"))
		{
			sCodeForCompare = "2";
		}
		else if (!this.Desc.equals(" ") && !this.Desc.equals("CY") && !this.Desc.equals(""))
		{
			sCodeForCompare = "1";
		}
		else
		{
			sCodeForCompare = "0";
		}
		
		if (sCodeForCompare.equals(sOldCode) || sOldCode.equals("0"))
		{
			blnResult = true;
		}
		else
		{
			blnResult = false;
		}
		
		return blnResult;
	}
	
	
	
	public void setAssistantSetting(ResultSet rs,String AuxiAccTB) throws SQLException {

	        this.AuxiAccID = rs.getString("AUXIACCID") + "";
	        this.AuxiAccName = rs.getString("AUXIACCNAME") + "";
	        this.BookSetCode = "";
	        this.SettingCode = "";
	
	        this.CuryCode = "";
	        this.CatCode = "";
	        this.BookSetCode = "";
	        this.BookSetName =  "";
	        this.AuxiAccTB = "";
	        this.Desc = "";
//        super.setRecLog(rs);

	}
	
	public void setListViewData(ResultSet rs) throws SQLException
	{
		String ExchangeValue = "";
		if (rs.getString("FExchange").equals("1"))
		{
			ExchangeValue = "交易所";
		}
		else if (rs.getString("FExchange").equals("2"))
		{
			ExchangeValue = "银行间";
		}

		else
		{
			ExchangeValue = "";
		}
    	this.SettingCode = rs.getString("FSettingCode");
        this.BookSetCode = rs.getString("FBookSetCode");
        this.CuryCode = rs.getString("FCuryCode");
        this.CuryName = rs.getString("FCuryName");
        this.CatCode = rs.getString("FCatCode");
        this.CatName = rs.getString("FCatName");
        this.AuxiAccTB = rs.getString("FAuxiAccTb");
        this.AuxiAccID = rs.getString("FAuxiAccID");
        this.Exchange = rs.getString("FExchange");
        this.Desc = rs.getString("FDESC");
        super.setRecLog(rs);
		
	}
    public void setVchBookSet(ResultSet rs) throws SQLException {
        this.CuryCode = rs.getString("FCuryCode");
        this.CuryName = rs.getString("FCuryName");
        this.BookSetCode = rs.getString("FBookSetCode");
        this.BookSetName = rs.getString("FBookSetName");
        this.Desc = rs.getString("FDesc");
//        super.setRecLog(rs);
    }
}