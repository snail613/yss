package com.yss.main.parasetting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;

import com.yss.dsub.YssPub;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class BondInterTaxBean extends BaseDataSettingBean implements
		IDataSetting {

	//======================================================================
	private String RateCode;
	private String RateType;
	private String exchangeCode = ""; //交易所代码
	private String bondCategoryCode = "";   //债券品种代码
    private String securityCode = ""; //证券代码
    private String isOnlyColumns = "0";
	private int checkStateId ;
    private String multAuditString = "";    //批量处理数据
    //=======================================================================
    
    //=======================================================================
	public BondInterTaxBean(){}

    public int getCheckStateId() {
        return this.checkStateId;
    }

    public void setCheckStateId(int checkStateId) {
        this.checkStateId = checkStateId;
    }
	public String getRateCode() {
		return RateCode;
	}

	public void setRateCode(String rateCode) {
		RateCode = rateCode;
	}

	public String getRateType() {
		return RateType;
	}

	public void setRateType(String rateType) {
		RateType = rateType;
	} 
	
    public void setIsOnlyColumns(String isOnlyColumns) {
        this.isOnlyColumns = isOnlyColumns;
    }
    
    public String getIsOnlyColumns() {
        return isOnlyColumns;
    }
    
	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}

	public String getBondCategoryCode() {
		return bondCategoryCode;
	}

	public void setBondCategoryCode(String bondCategoryCode) {
		this.bondCategoryCode = bondCategoryCode;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
   
	public void setBondInterestTaxAttr(ResultSet rs) throws SQLException {
        this.RateType = rs.getString("ftype") + "";    
        super.setRecLog(rs);
    }
	//======================================================================
	
	//======================================================================
	public String getOperValue(String sType) throws YssException {
		if (sType.trim().toLowerCase().equals("search")) {
            return searchBondInfo(); 
        }
		 if (sType.equalsIgnoreCase("MultaddBondInterTax")) {
             if (multAuditString.length() > 0) {
            	 this.deleteMutli(this.multAuditString);
                 this.addMutli(this.multAuditString); //执行批量审核/反审核/删除
             }
         }
         if (sType.equalsIgnoreCase("MultdelBondInterTax")) {
             if (multAuditString.length() > 0) {
            	 this.deleteMutli(this.multAuditString);
             }
         }if(sType.equalsIgnoreCase("MultaudiBondInterTax")){
        	
                 this.autiMutil();
        	
         }else if (sType.equalsIgnoreCase("EditOperation")){
        	 this.EditOperation (this.multAuditString);
         }
		return this.getListViewData1();
	}
	//-----------------------------------------------------------------------
	
	
	public  String addMutli(String sMutilRowStr) throws YssException{
		    Connection conn = null;
	        String sqlStr = "";
	        java.sql.PreparedStatement psmt = null;
	        boolean bTrans = false;
	        BondInterTaxBean data = null,data1=null;
	        String[] multAudit = null,multAudit1=null;
	        try {
	            conn = dbl.loadConnection();
//	            String Temp = sMutilRowStr;
//	            multAudit1 = Temp.split("\f\f\f\f");
//	            if (multAudit1.length > 0) {
//	            	data1 = new BondInterTaxBean();
//                    data1.setYssPub(pub);
//	            	data1.parseRowStr1(multAudit1[0]);
//	            }
//	            sqlStr = "delete from " + pub.yssGetTableName("Tb_Para_BONDINTERTAX") +
//                " where Ftype = '" + data1.RateType+"'";
//
//                if (!bTrans) {
//                   conn.setAutoCommit(false);
//                   bTrans = true;
//                }
//               dbl.executeSql(sqlStr);
	            
	            sqlStr = "insert into " + pub.yssGetTableName("Tb_Para_BONDINTERTAX") +" (FINTERESTTAXCODE,FTYPE,FSECURITYCODE,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)values(?,?,?,?,?,?,?,? )";
                psmt = conn.prepareStatement(sqlStr);
	            if (multAuditString.length() > 0) {
	                multAudit = sMutilRowStr.split("\f\f\f\f");
	                if (multAudit.length > 0) {
	                    for (int i = 0; i < multAudit.length; i++) {
	                        data = new BondInterTaxBean();
	                        data.setYssPub(pub);
	                        data.parseRowStr1(multAudit[i]);
	                        psmt.setString(1, data.RateCode);
	                        psmt.setString(2, data.RateType);
	                        psmt.setString(3, data.bondCategoryCode);
	                        //psmt.setString(4,"");
	                        psmt.setInt(4, data.checkStateId);
	                        psmt.setString(5, data.creatorName);
	                        psmt.setString(6, data.creatorTime);
	                        psmt.setString(7, data.checkUserName);
	                        psmt.setString(8, ""); //modify by fangjiang 2010.10.20 MS01813
	                        
	                        psmt.addBatch();
	                    }
	                }
	                conn.setAutoCommit(false);
	                bTrans = true;
	                psmt.executeBatch();
	                conn.commit();
	                bTrans = false;
	                conn.setAutoCommit(true);
	            }
	        } catch (Exception e) {
	            throw new YssException("批量添加债券利率数据表出错!");
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }
	        return "";
	}
	
	public String deleteMutli(String sMutilRowStr)throws YssException {

		    Connection conn = null;
	        String sqlStr = "";
	        java.sql.PreparedStatement psmt = null;
	        boolean bTrans = false;
	        BondInterTaxBean data = null;
	        String[] multAudit = null;
	        try {
	            conn = dbl.loadConnection();
	            
	            sqlStr = "delete from " + pub.yssGetTableName("Tb_Para_BONDINTERTAX") +" where FTYPE=? and FSECURITYCODE=?";
	               psmt = conn.prepareStatement(sqlStr);
		            if (multAuditString.length() > 0) {
		                multAudit = sMutilRowStr.split("\f\f\f\f");
		                if (multAudit.length > 0) {
		                    for (int i = 0; i < multAudit.length; i++) {
		                        data = new BondInterTaxBean();
		                        data.setYssPub(pub);
		                        data.parseRowStr1(multAudit[i]);
		                        psmt.setString(1, data.RateType);
		                        psmt.setString(2, data.bondCategoryCode);  
		                        psmt.addBatch();
		                    }
		                }
		                conn.setAutoCommit(false);
		                bTrans = true;
		                psmt.executeBatch();
		                conn.commit();
		                bTrans = false;
		                conn.setAutoCommit(true);
		            }
		        } catch (Exception e) {
		            throw new YssException("批量删除债券利率数据表出错!");
		        } finally {
		            dbl.endTransFinal(conn, bTrans);
		        }
		        return "";
	}
	
	public String EditOperation (String sMutilRowStr)throws YssException {

	    Connection conn = null;
        String sqlStr = "";
        java.sql.PreparedStatement psmt = null;
        boolean bTrans = false;
        BondInterTaxBean data = null;
        String[] multAudit = null;

        try {
            conn = dbl.loadConnection();
            
            sqlStr = "update " + pub.yssGetTableName("Tb_Para_BONDINTERTAX") +" set finteresttaxcode=?,ftype=?,fcreator=?,fcreatetime=? where FSECURITYCODE=?";
               psmt = conn.prepareStatement(sqlStr);
	            if (multAuditString.length() > 0) {
	                multAudit = sMutilRowStr.split("\f\f\f\f");
	                if (multAudit.length > 0) {
	                    for (int i = 0; i < multAudit.length; i++) {
	                        data = new BondInterTaxBean();
	                        data.setYssPub(pub);
	                        data.parseRowStr1(multAudit[i]);
	                        psmt.setString(1,data.getRateCode());
	                        psmt.setString(2, data.getRateType());
	                        psmt.setString(3, pub.getUserCode());
	                        psmt.setString(4, YssFun.formatDatetime(new java.util.Date()));
	                        psmt.setString(5, data.getBondCategoryCode());  
	                        psmt.execute();
	                    }
	                }
	                conn.setAutoCommit(false);
	                bTrans = true;
	                conn.commit();
	                bTrans = false;
	                conn.setAutoCommit(true);
	            }
	        } catch (Exception e) {
	            throw new YssException("修改债券利率数据出错!");
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }
	        return "";
}
	public String autiMutil()throws YssException{
		 Connection conn = null;
	        String sqlStr = "";
	        java.sql.PreparedStatement psmt = null;
	        boolean bTrans = false;
	        BondInterTaxBean data = null;
	        String[] multAudit = null;
	        try {
	            conn = dbl.loadConnection();
	            
	            sqlStr = "update " + pub.yssGetTableName("Tb_Para_BONDINTERTAX") +" set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date())+"' where FTYPE=? ";
	               psmt = conn.prepareStatement(sqlStr);
		           psmt.setString(1, this.RateType);
                   conn.setAutoCommit(false);
		           bTrans = true;
		           psmt.execute();
		           conn.commit();
		           bTrans = false;
		           conn.setAutoCommit(true);

		        } catch (Exception e) {
		        	
		            throw new YssException("批量审核债券利率数据表出错!");
		        } finally {
		            dbl.endTransFinal(conn, bTrans);
		        }
		        return "";
	}
	public String addSetting() throws YssException {

		 if (multAuditString.length() > 0) {
        	 this.deleteMutli(this.multAuditString);
             this.addMutli(this.multAuditString); //执行批量审核/反审核/删除
         }
		 return "";
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub

	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub

	}

	public void delSetting() throws YssException {
		 if (multAuditString.length() > 0) {
        	 this.deleteMutli(this.multAuditString);
         }

	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub

	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
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
		return this.autoBuildRowStr(this.getBuilderRowFields1());
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String sMutiAudit = ""; 
		if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) { //提取批量处理数据
			 sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];
             multAuditString = sMutiAudit;
             sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];
        }
		if((!sRowStr.equalsIgnoreCase(""))&& sRowStr.indexOf("\f\n\f\n\f\n") < 0){
			this.autoParseRowStr(sRowStr);
	        super.parseRecLog();
		}
	}

	public void parseRowStr1(String sRowStr) throws YssException {
		String reqAry[] = null;
		reqAry = sRowStr.split("\t");
		if (sRowStr.split("<Logging>").length >= 2)
        {
        	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
        }
        sRowStr = sRowStr.split("<Logging>")[0];
		if(reqAry[0].equalsIgnoreCase("税前利率")){
			RateCode="0";
		}else{
			RateCode="1";
		}
		RateType=reqAry[0];
        bondCategoryCode=reqAry[1];
        super.parseRecLog();
	}
	public String buildRowStr1() throws YssException {
		StringBuffer buf = new StringBuffer();
        buf.append(this.RateType).append("\t");
      
        buf.append(super.buildRecLog());
        return buf.toString();
	}
	
	
	//--------------- 查询-------------------------------------------
	private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.securityCode != null && this.securityCode.trim().length() > 0) {
                sResult = sResult + "AND FSecurityCode = " +
                    dbl.sqlString(this.securityCode);
            }
            if (this.exchangeCode != null && this.exchangeCode.trim().length() > 0) {
                sResult = sResult + "AND FExchangeCode = " +
                    dbl.sqlString(this.exchangeCode);
            }
            if (this.bondCategoryCode != null && this.bondCategoryCode.trim().length()>0){
            	sResult = sResult + "AND FSubcatcode = " +
                dbl.sqlString(this.bondCategoryCode);
            }
//            if (sResult.length() == 0) {
//                sResult += " AND 1= 2";
//            }

        } catch (Exception e) {
            throw new YssException("前台参数获取出错!", e);
        }
        return sResult;
    }

	
	public String searchBondInfo()throws YssException{
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        StringBuffer bufState = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        boolean bTrans = false;
        boolean bIsSecurityPub = false;
        String filterSql = "";
        Connection conn = dbl.loadConnection();
        SecurityBean securityOper = (SecurityBean) pub.getParaSettingCtx().
            getBean("security");
        try {
            securityOper.setYssPub(pub);
            sHeader = securityOper.getListView1Headers();
            if (isOnlyColumns.equalsIgnoreCase("1")) {
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            } 
            
            //2009.06.04 蒋锦 修改 操作组合 《QDV4赢时胜（上海）2009年4月20日02_A》
            //直接使用 Tb_Pub_Para_Security 表
            StringBuffer bufSql = new StringBuffer();
            bufSql.append(" SELECT  case when k.ftype = '税前利率' then 1 when k.ftype = '税后利率' then 2 else 0 end as state, sec.*,d.FCatName,e.FSubCatName,f.FExchangeName, f.FRegionCode,fa.FRegionName,fb.Fcountrycode,fb.FCountryName,");
            bufSql.append("fc.FAreaCode,fc.FAreaName,g.FCurrencyName,h.FSectorName,i.FHolidaysName,j.FCusCatName,k.FAffCorpName as FIssueCorpName,m.FVocName as FSettleDayTypeValue,");
            bufSql.append("b.fusername as fcreatorname,c.fusername as fcheckusername,l.fsecclsname as FSYNTHETICNAME ");//板块分类名称add by yanghaiming 20100426 
            bufSql.append(",ass.FAssetGroupName as FAssetGroupName");		//added by liubo.Story #1770
            bufSql.append(" FROM (SELECT * from ").append(pub.yssGetTableName("Tb_Para_Security"));
            bufSql.append(" a WHERE FCheckState = 1 and fcatcode='FI' ");
            bufSql.append(buildFilterSql()).append(") sec ");
            bufSql.append(" left join (select FSecClsCode,FSecClsName from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_SectorClass"));
            bufSql.append(" where FCheckState = 1) l on sec.fsyntheticcode = l.FSecClsCode");//板块分类名称add by yanghaiming 20100426
            bufSql.append(" LEFT JOIN (select FUserCode, FUserName from Tb_Sys_UserList) b on sec.FCreator = ");
            bufSql.append(" b.FUserCode ");
            bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) c on sec.FCheckUser = ");
            bufSql.append(" c.FUserCode ");
            bufSql.append(" left join (select FCatCode, FCatName ");
            bufSql.append(" from Tb_Base_Category ");
            bufSql.append(
                " where FCheckState = 1) d on sec.FCatCode = d.FCatCode ");
            bufSql.append(" left join (select FSubCatCode, FSubCatName ");
            bufSql.append(" from Tb_Base_SubCategory ");
            bufSql.append(
                " where FCheckState = 1) e on sec.FSubCatCode = e.FSubCatCode ");
            bufSql.append(" left join (select FExchangeCode, ");
            bufSql.append(" FExchangeName, ");
            bufSql.append(" FRegionCode, ");
            bufSql.append(" FCountryCode, ");
            bufSql.append(" FAreaCode ");
            bufSql.append(" from Tb_Base_Exchange ");
            bufSql.append(
                " where FCheckState = 1) f on sec.FExchangeCode = f.FExchangeCode ");
            bufSql.append(" left join (select FRegionCode, FRegionName ");
            bufSql.append(" from Tb_Base_Region ");
            bufSql.append(
                " where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode ");
            bufSql.append(" left join (select FCountryCode, FCountryName ");
            bufSql.append(" from Tb_Base_Country ");
            bufSql.append(
                " where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode ");
            bufSql.append(" left join (select FAreaCode, FAreaName ");
            bufSql.append(" from Tb_Base_Area ");
            bufSql.append(
                " where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode ");
            bufSql.append(
                " left join (select FCuryCode, FCuryName as FCurrencyName ");
            bufSql.append(" from ").append(pub.yssGetTableName(
                "Tb_Para_Currency")).append(" ");
            bufSql.append(
                " where FCheckState = 1) g on sec.FTradeCury = g.FCuryCode ");
            bufSql.append(" left join (select FSectorCode as FSectorCode, ");
            bufSql.append(" FSectorName as FSectorName from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_Sector"));
            bufSql.append(" p ) h on sec.FSectorCode = h.FSectorCode ");
            bufSql.append(" left join (select FHolidaysCode, FHolidaysName ");
            bufSql.append(" from Tb_Base_Holidays ");
            bufSql.append(
                " where FCheckState = 1) i on sec.FHolidaysCode = i.FHolidaysCode ");
            bufSql.append(" left join (select FAssetGroupCode,FAssetGroupName from tb_sys_AssetGroup) ass on sec.FAssetGroupCode = ass.FAssetGroupCode ");	//added by liubo.Story #1770
            bufSql.append(" left join (select FCusCatCode, FCusCatName from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_CustomCategory"));
            bufSql.append(
                " where FCheckState = 1) j on sec.FCusCatCode = j.FCusCatCode ");
            bufSql.append(" left join (select FAffCorpCode, FAffCorpName from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_AffiliatedCorp"));
            bufSql.append(
                " where FCheckState = 1) k on sec.FIssueCorpCode = k.FAffCorpCode ");
            bufSql.append(
                " left join Tb_Fun_Vocabulary m on ").append(dbl.sqlToChar("sec.FSettleDayType")).append(" = m.FVocCode ");
            bufSql.append(" and m.FVocTypeCode = 'scy_sdaytype' ");
            //------------------------------
            bufSql.append(" left join (select * from ").append(pub.yssGetTableName("tb_para_bondintertax")).append(" )k on sec.fsecuritycode=k.fsecuritycode ");
            strSql = bufSql.toString();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	if(rs.getString("FSecuritycode").equalsIgnoreCase("126001 CG")){
            		System.out.println();
            	}
            	//如果没有选择利率类型，就只显示没有进行利率设置的债券
            	if(this.RateType.equalsIgnoreCase(" ")){
            		if(rs.getString("state").equalsIgnoreCase("1")||rs.getString("state").equalsIgnoreCase("2")){
            			continue;
            		}
            	}
            	//如果查询税后利率的债券则应把税前利率的债券过滤；
            	else if(rs.getString("state").equalsIgnoreCase("1")&&this.RateType.equalsIgnoreCase("税后利率")){
            		continue;
            	}else if (rs.getString("state").equalsIgnoreCase("2")&&this.RateType.equalsIgnoreCase("税前利率")){
            		continue;
            	}
            	bufState.append(rs.getString("state")).append("\t");
                bufShow.append(securityOper.buildRowShowStr(rs,
                    securityOper.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                securityOper.setSecurityAttr(rs);
                bufAll.append(securityOper.buildRowStr()).
                    append(YssCons.YSS_LINESPLITMARK);
            }

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return bufState+ "\f\f\f\f" + sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

        } catch (Exception e) {
            throw new YssException("加载证券信息出错\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
	}
   	

	public String getListViewData1() throws YssException {
		String sHeader = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        ResultSet rs = null;
        String strSql = "";

        BondInterTaxBean bondInterTaxBean = (BondInterTaxBean) pub.getParaSettingCtx().getBean("bondrateset");
        bondInterTaxBean.setYssPub(pub);
        sHeader = bondInterTaxBean.getListView1Headers();
        if (isOnlyColumns.equalsIgnoreCase("1")) {
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        }
        
        strSql="select distinct a.ftype,a.fchecktime,b.FCHECKUSERNAME,b.fcheckuser,a.fcreatetime,b.FCreatorName,b.fcreator,b.FCheckState from "+
                "(select max(fcreatetime) fcreatetime,max(fchecktime) fchecktime,ftype from "+
                pub.yssGetTableName("Tb_Para_BONDINTERTAX")+" group by ftype) a "+
                "left join (select * from "+pub.yssGetTableName("Tb_Para_BONDINTERTAX")+" ) b on b.fcreatetime =a.fcreatetime";
        try {
			rs = dbl.openResultSet(strSql);
			 while (rs.next()) {
		            bufShow.append(bondInterTaxBean.buildRowShowStr(rs,
		            		bondInterTaxBean.getListView1ShowCols())).
		                append(YssCons.YSS_LINESPLITMARK);

		            bondInterTaxBean.setBondInterestTaxAttr(rs);
		            bufAll.append(bondInterTaxBean.buildRowStr1()).
		                append(YssCons.YSS_LINESPLITMARK);
		        }

		        if (bufShow.toString().length() > 2) {
		            sShowDataStr = bufShow.toString().substring(0,
		                bufShow.toString().length() - 2);
		        }

		        if (bufAll.toString().length() > 2) {
		            sAllDataStr = bufAll.toString().substring(0,
		                bufAll.toString().length() - 2);
		        }
				return this.getListView1Headers() + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
		        "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc" +
		        sVocStr;
		} catch (Exception e) {
            throw new YssException("获取债券计息利率数据出错", e);
        } finally {
        	dbl.closeResultSetFinal(rs);
        }
       
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
