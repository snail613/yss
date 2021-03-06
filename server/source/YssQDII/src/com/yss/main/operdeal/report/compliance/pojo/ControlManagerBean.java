package com.yss.main.operdeal.report.compliance.pojo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.operdata.TradeRelaBean;
import com.yss.main.operdata.TradeSubBean;
import com.yss.manager.TradeDataAdmin;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class ControlManagerBean extends BaseDataSettingBean implements IDataSetting {

	private java.util.Date compDate; //监控日期
	private java.util.Date sEndDate;
    private java.util.Date createDate; //
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String indexCfgCode = ""; //指标配置代码
    private String indexCfgName = ""; //指标配置名称
    private String compResult = ""; //监控结果
    private String Desc = ""; //描述

    private double numerator=0; //分子
    private double denominator=0; //分母
    private double factRatio=0; //实际比值
    private double dCompStandard=0;//阀值
    private String sState;
	private String sFReCheckstate;
    private ControlManagerBean filterType;
    private String sIsOnlyColumns;
    private String sRecycled="";
    private String oldPortCode = "";
    private String oldIndexCfgCode = "";
    private java.util.Date oldCompDate;
    //--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
    private String assetGroupCode = "";
    public String getAssetGroupCode(){
    	return this.assetGroupCode;
    }
    
    public void setAssetGroupCode(String assetGroupCode){
    	this.assetGroupCode = assetGroupCode;
    }
    //--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
    
	public void parseRowStr(String sRowStr) throws YssException {
		 String[] reqAry = null;
         String sTmpStr = "";
         String sMutiAudit = ""; 
         try {

             if (sRowStr.trim().length() == 0) {
                 return;
             }
             if (sRowStr.indexOf("\r\t") >= 0) {
                 sTmpStr = sRowStr.split("\r\t")[0];
             } else {
                 sTmpStr = sRowStr;
             }
             sRecycled = sRowStr;
             reqAry = sTmpStr.split("\t");
             if(reqAry[0].indexOf(",")>0){
            	 this.compDate = YssFun.parseDate(reqAry[0].split(",")[0]);
            	 this.sEndDate = YssFun.parseDate(reqAry[0].split(",")[1]);
             }else{
            	 this.compDate = YssFun.parseDate(reqAry[0]);
            	 this.sEndDate = this.compDate;
             }
             
             this.portCode = reqAry[1];
             this.indexCfgCode = reqAry[2];
             this.compResult = reqAry[3];
             this.numerator = YssFun.toDouble(reqAry[4]);
             this.denominator=YssFun.toDouble(reqAry[5]);
             this.factRatio = YssFun.toDouble(reqAry[6]);
             this.dCompStandard=YssFun.toDouble(reqAry[7]);
             this.sState = reqAry[8];
             this.sFReCheckstate = reqAry[9];
             this.sIsOnlyColumns = reqAry[10];
		 	 this.oldPortCode = reqAry[11];
             this.oldIndexCfgCode = reqAry[12];
             this.oldCompDate = YssFun.parseDate(reqAry[13]);
             this.checkStateId = Integer.parseInt(reqAry[14]);
             //add by songjie 2013.02.25 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001
             this.assetGroupCode = reqAry[15];
             super.parseRecLog();
             if (sRowStr.indexOf("\r\t") >= 0) {
                 if (this.filterType == null) {
                     this.filterType = new ControlManagerBean();
                     this.filterType.setYssPub(pub);
                 }
                 this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
             }
         } catch (Exception e) {
             throw new YssException("解析分红权益数据信息出错", e);
         }

		
	}
    
	public String buildRowStr() throws YssException {
		StringBuffer buff = new StringBuffer();
    	buff.append(this.compDate).append("\t");
    	buff.append(this.portCode).append("\t");
    	buff.append(this.portName).append("\t");
    	buff.append(this.indexCfgCode).append("\t");
    	buff.append(this.indexCfgName).append("\t");
    	buff.append(this.compResult).append("\t");
    	buff.append(this.numerator).append("\t");
    	buff.append(this.denominator).append("\t");
    	buff.append(this.factRatio).append("\t");
    	buff.append(this.dCompStandard).append("\t");
    	buff.append(this.sState).append("\t");
    	buff.append(super.buildRecLog());
        return buff.toString();
	}
	
	
	  public String getOperValue(String sType) throws YssException {
	    	
		  
	    	if(sType.equalsIgnoreCase("find")){
	    		return getListViewData1();
	    	}else if(sType.equalsIgnoreCase("add")){
	    		
	    	}else if(sType.equalsIgnoreCase("edit")){
	    		
	    	}else if(sType.equalsIgnoreCase("del")){
	    		
	    	}else if(sType.equalsIgnoreCase("audit")){
	    		
	    	}else if(sType.equalsIgnoreCase("recheck")){
	    		//--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
	    		String currentAssetGroup = pub.getPrefixTB();
	    		try{
	    			if(this.assetGroupCode != null && this.assetGroupCode .length() > 0 && 
	    					!pub.getPrefixTB().equals(this.assetGroupCode)){
	    				pub.setPrefixTB(this.assetGroupCode);
	    				pub.setAssetGroupCode(this.assetGroupCode);
	    			}
	    			//--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
	    		
	    			if(this.sFReCheckstate.equalsIgnoreCase("1")){
	    				isReCheck();
	    			}
	    			return reCheckSetting();
	    			//--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 start---//
	    		}catch(Exception e){
	    			//edit by songjie 2013.02.28 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 添加 ,e
	    			throw new YssException("确认、反确认监控数据出错！",e);
	    		}finally{
	    			pub.setPrefixTB(currentAssetGroup);
	    			pub.setAssetGroupCode(currentAssetGroup);
	    		}
	    		//--- add by songjie 2013.02.22 STORY #3648 需求深圳-(招商银行)QDII估值系统V4.0(中)20130219001 end---//
	    	}else if(sType.equalsIgnoreCase("listview1")){
	    		isReCheck();
	    	}
	        return "";
	    }
	
	
	public String getListViewData1() throws YssException {
		 String sHeader = "";
	        String sShowDataStr = "";
	        String sAllDataStr = "";
	        String sVocStr = "";
	        String strSql = "";
	        ResultSet rs = null;
	        StringBuffer queryBuff = new StringBuffer();
	        StringBuffer bufShow = new StringBuffer();
	        StringBuffer bufAll = new StringBuffer();
	        try {
	            sHeader = this.getListView1Headers();
	            
	            if (this.filterType.sIsOnlyColumns.equals("1")) {
	            	VocabularyBean vocabulary = new VocabularyBean();
	                vocabulary.setYssPub(pub);
	                sVocStr = vocabulary.getVoc(YssCons.YSS_GEN_DIVIDENDTYPE);
	            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
	                this.getListView1ShowCols();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
	            
	            }
//	            //---------------------------------------------------------------------------------------------------------
	            queryBuff.append(" select a.FCOMPDATE ,substr(a.fportcode,instr(a.fportcode,'-')+1) as fportcode,a.FINDEXCFGCODE,d.FUserName as FCreatorName,e.FUserName as FCheckUserName,");
	            queryBuff.append(" a.FCREATEDATE ,a.FCOMPRESULT ,a.FDESC,a.FCHECKSTATE,a.FCREATOR ,a.FCREATETIME ,a.FCHECKUSER, a.FCHECKTIME,");
	            queryBuff.append(" a.FNUMERATOR,a.FDENOMINATOR,a.FFACTRATIO,a.FSTATE,a.FCOMPSTANDARD,a.FRECHECKSTATE,b.fportname,c.findexcfgname ");
	            queryBuff.append(" from ").append(pub.yssGetTableName("Tb_Comp_ResultData")).append(" a ");
	            queryBuff.append(" left join ");
	            queryBuff.append(" (select fportcode,fportname from ").append(pub.yssGetTableName("tb_para_portfolio"));
	            queryBuff.append("  where fcheckstate=1) b on substr(a.fportcode,instr(a.fportcode,'-')+1) = b.fportcode");
	            queryBuff.append(" left join ");
	            queryBuff.append(" (select findexcfgcode,findexcfgname from ").append(pub.yssGetTableName("tb_comp_indexcfg"));
	            queryBuff.append("  where fcheckstate=1 )c on a.findexcfgcode=c.findexcfgcode ");
	            queryBuff.append(" left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode");
	            queryBuff.append(" left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCreator = e.FUserCode");
                queryBuff.append(buildFilterSql());
                rs = dbl.openResultSet(queryBuff.toString());
	            while (rs.next()) {
	                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
	                    append(YssCons.YSS_LINESPLITMARK);

                    setResultSetAttr(rs);
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
//	            //BugNo:0000328 edit by jc
//	            VocabularyBean vocabulary = new VocabularyBean();
//	            vocabulary.setYssPub(pub);
//	            sVocStr = vocabulary.getVoc(YssCons.YSS_GEN_DIVIDENDTYPE);
//
	            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
	                this.getListView1ShowCols();
	            //----------------------jc
	        } catch (Exception e) {
	            throw new YssException("获取监控结果信息出错！", e);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
	}
	
	private void setResultSetAttr( ResultSet rs) throws YssException{
		
		try {
			this.compDate = rs.getDate("FCOMPDATE");
			this.compResult = rs.getString("FCOMPRESULT");
			this.portCode = rs.getString("fportcode");
		    this.portName = rs.getString("fportname");
		    this.indexCfgCode = rs.getString("FINDEXCFGCODE");
		    this.indexCfgName = rs.getString("findexcfgname");
		    this.Desc = rs.getString("FDESC");
		    this.numerator= rs.getDouble("FNUMERATOR");
		    this.denominator= rs.getDouble("FDENOMINATOR");
		    this.factRatio= rs.getDouble("FFACTRATIO");
		    this.dCompStandard= rs.getDouble("FCOMPSTANDARD");
		    this.sState= rs.getString("FSTATE");
			this.sFReCheckstate= rs.getString("FRECHECKSTATE");
			this.checkStateId = rs.getInt("fcheckstate");
			super.setRecLog(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() throws YssException {
		String sResult = "";
		if (this.filterType != null) {
			sResult = " where 1=1";
			if (this.filterType.sIsOnlyColumns.equals("1")) {
				sResult = sResult + " and 1=2 ";
				return sResult;
			}
			if (this.filterType.portCode.length() != 0) {
				if (this.filterType.portCode.indexOf(",")>0){
					sResult = sResult + " and a.fportcode in("
					+ operSql.sqlCodes(this.filterType.portCode)+")";
				}else{
					sResult = sResult + " and a.fportcode = "
					+ dbl.sqlString(this.filterType.portCode);
				}
				
			}
		if (filterType.compDate !=null
//					&& !filterType.compDate.equals("1900-01-01")
				&& !YssFun.formatDate(filterType.compDate).equals("1900-01-01")
					) {
				sResult = sResult + " and a.FCOMPDATE  between  "
						+ dbl.sqlDate(filterType.compDate)+" and "+ dbl.sqlDate(filterType.sEndDate);
		}
		if(filterType.indexCfgCode.length()!=0){
			sResult = sResult + " and FINDEXCFGCODE ="
			+ dbl.sqlString(this.filterType.indexCfgCode);
		}
		if(filterType.numerator > 0){
			sResult = sResult + " and FNUMERATOR ="+ this.filterType.numerator;
		}
		if(filterType.denominator >0 ){
			sResult = sResult + " and FDENOMINATOR ="+ this.filterType.denominator;
		}		
		if(filterType.factRatio  >0){
			sResult = sResult + " and FFACTRATIO ="+ this.filterType.factRatio;
		}
		if(filterType.dCompStandard  >0){
			sResult = sResult + " and FCOMPSTANDARD ="+ this.filterType.dCompStandard;
		}
        if(filterType.compResult.length()!=0){
        	sResult = sResult + " and FCOMPRESULT ="+ dbl.sqlString(this.filterType.compResult);
        }
	  }
		return sResult;
	}
	
	
	
	
	public String addSetting() throws YssException {
		    String strSql = "";
	        String strSqlDel = "";
	        boolean bTrans = false;
	        PreparedStatement pstmt = null;
	        Connection conn = dbl.loadConnection();
	        try {

	            strSql = "INSERT INTO " + pub.yssGetTableName("Tb_Comp_ResultData") +
	                " (FCompDate, FCreateDate, FPortCode, FIndexCfgCode, FCompResult, FCheckState, FCreator, FCreateTime,FNUMERATOR,FDENOMINATOR,FFACTRATIO,FCOMPStandard,Fstate,fRecheckState)" +
	                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	            pstmt = conn.prepareStatement(strSql);
	            
	            conn.setAutoCommit(false);
	            bTrans = true;

	            pstmt.setDate(1, YssFun.toSqlDate(this.compDate));
                pstmt.setDate(2, YssFun.toSqlDate(YssFun.formatDate(new Date())));
                pstmt.setString(3, this.portCode);
                pstmt.setString(4, this.indexCfgCode);
                pstmt.setString(5, this.compResult);
                pstmt.setInt(6, 0);
                pstmt.setString(7, pub.getUserCode());
                pstmt.setString(8, YssFun.formatDate(new Date()));
                pstmt.setDouble(9, this.numerator);
                pstmt.setDouble(10, this.denominator);
                pstmt.setDouble(11, this.factRatio);
                pstmt.setDouble(12, this.dCompStandard);
                pstmt.setInt(13, 2);
                pstmt.setInt(14, 0);
                pstmt.executeUpdate();
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
                
                return "";
	        } catch (Exception e) {
	            throw new YssException("储存监控结果出错", e);
	        } finally {
	            dbl.closeStatementFinal(pstmt);
	            dbl.endTransFinal(conn, bTrans);
	        }
	}

	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper, pub.yssGetTableName("tb_comp_resultdata"),
                "FCOMPDATE,FPORTCODE,FINDEXCFGCODE", YssFun.formatDate(this.compDate)+","+this.portCode+","+this.indexCfgCode, YssFun.formatDate(this.oldCompDate)+","+this.oldPortCode+","+this.oldIndexCfgCode);
		
	}

	public void checkSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String strSql = "";
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			if (sRecycled.trim().length() == 0) {
				strSql = "update " + pub.yssGetTableName("TB_COMP_RESULTDATA")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date()) + "'"
						+ " where FCOMPDATE = " + dbl.sqlDate(this.compDate)
						+ " and fportcode=" + dbl.sqlString(this.portCode)
						+" and FINDEXCFGCODE ="+dbl.sqlString(this.indexCfgCode);
				dbl.executeSql(strSql);
				
			} else {
				String[] arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql = "update "
							+ pub.yssGetTableName("TB_COMP_RESULTDATA")
							+ " set FCheckState = " + this.checkStateId
							+ ", FCheckUser = "
							+ dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = '"
							+ YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FCOMPDATE = " + dbl.sqlDate(this.compDate)
							+ " and fportcode =" + dbl.sqlString(this.portCode)
							+" and FINDEXCFGCODE ="+dbl.sqlString(this.indexCfgCode);
					dbl.executeSql(strSql);
				}
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new YssException("审核监控结果数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		
	}

	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String strSql = "";
		try {
			strSql = "delete from " + pub.yssGetTableName("Tb_Comp_ResultData")
					+ " where FCOMPDATE = " + dbl.sqlDate(this.compDate)
					+ " and fportcode = " + dbl.sqlString(this.portCode)
					+" and FINDEXCFGCODE ="+dbl.sqlString(this.indexCfgCode);
			
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("修改监控结果出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		
	}

	public void deleteRecycleData() throws YssException {
		String strSql = "";
		Connection conn = null;
		boolean bTrans = false;
		String[] arrData = null;
		TradeSubBean data = null;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			arrData = sRecycled.split("\r\n");
			for (int i = 0; i < arrData.length; i++) {
				if (arrData[i].length() == 0) {
					continue;
				}
				this.parseRowStr(arrData[i]);
				strSql = "delete from "
						+ pub.yssGetTableName("TB_COMP_RESULTDATA")
						+ " where FCOMPDATE = " + dbl.sqlDate(this.compDate)
					    + " and substr(fportcode,instr(fportcode,'-')+1) =" + dbl.sqlString(this.portCode)
					    + " and FINDEXCFGCODE ="+dbl.sqlString(this.indexCfgCode);
				dbl.executeSql(strSql);
			}
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		} catch (Exception e) {
			throw new YssException("清除数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		
	}

	public String editSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = " update "
					+ pub.yssGetTableName("Tb_Comp_ResultData")
					+ " set FCOMPRESULT= "+dbl.sqlString(this.compResult)
					+ ",FCOMPDATE = " + dbl.sqlDate(this.compDate)
					+ ",FPORTCODE = " + dbl.sqlString(this.portCode)
					+ " ,FINDEXCFGCODE =  " + dbl.sqlString(this.indexCfgCode)
					+",FNUMERATOR = "+this.numerator
					+ ",FDENOMINATOR = "+ this.denominator
					+ ",FFACTRATIO = "+this.factRatio
					+ ",FCOMPSTANDARD = "+this.dCompStandard
					+ ",FSTATE = "+ (this.sState.equalsIgnoreCase("0")?dbl.sqlString(1+""):dbl.sqlString(this.sState))
					+ ",FCREATOR="+dbl.sqlString(pub.getUserCode())
					+ ",FCREATETIME="+dbl.sqlString(YssFun.formatDate(new Date()))
					+" where FCOMPDATE = " + dbl.sqlDate(this.oldCompDate)
					+ " and fportcode =" + dbl.sqlString(this.oldPortCode)
					+" and FINDEXCFGCODE = "+dbl.sqlString(this.oldIndexCfgCode);

			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return "";
		} catch (Exception e) {
			throw new YssException("修改监控结果出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public String reCheckSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String strSql = "";
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = "update "
							+ pub.yssGetTableName("TB_COMP_RESULTDATA")
							+ " set FReCheckState = " + this.sFReCheckstate
							+ ", FCheckUser = "
							+ dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = '"
							+ YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FCOMPDATE between " + dbl.sqlDate(this.compDate)
							+ " and "+dbl.sqlDate(this.sEndDate)
							+ " and substr(fportcode,instr(fportcode,'-')+1) in(" + operSql.sqlCodes(this.portCode)+")"
							
			                +" and fcheckstate=1";
					dbl.executeSql(strSql);
			
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
		
				return "确认监控结果";
		} catch (Exception e) {
			throw new YssException("复核监控结果数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		
	}
	
	public void isReCheck() throws YssException {
		StringBuffer buff = new StringBuffer();
    	ResultSet rs = null;
    	String flag = "false";
    	StringBuffer errorMsg = new StringBuffer();
    	java.sql.Date oldDate=null,startDate=null;
    	String oldPortCode="";
    	try{
    		
    		buff.append(" select substr(fportcode,instr(fportcode,'-')+1) as fportcode,FCOMPDATE  from ").append(pub.yssGetTableName("tb_comp_resultdata"));
    		buff.append(" where FCOMPDATE between ").append(dbl.sqlDate(this.compDate)).append(" and ").append(dbl.sqlDate(this.sEndDate)).append(" and FRECHECKSTATE=1");
    		buff.append(" and substr(fportcode,instr(fportcode,'-')+1) in(").append(operSql.sqlCodes(this.portCode)).append(")");
    		buff.append(" order by substr(fportcode,instr(fportcode,'-')+1),FCOMPDATE");
    		
    		rs = dbl.openResultSet(buff.toString());
    		while(rs.next()){
//    			if(rs.getRow()==1){
//    				errorMsg = new StringBuffer();
//    			}
    			if(rs.getString("fportcode").equalsIgnoreCase(oldPortCode)){
	    			  
	    			  if(YssFun.dateDiff(oldDate, rs.getDate("FCOMPDATE"))>1){
	    				  startDate = rs.getDate("FCOMPDATE"); 
	    				  errorMsg.append("】至【"+YssFun.formatDate(oldDate,"yyyy-MM-dd")).append("】已确认监控结果...\r\n");
	    				  errorMsg.append("组合【"+rs.getString("fportcode")+"】对应的业务日期【"+YssFun.formatDate(rs.getDate("FCOMPDATE"), "yyyy-MM-dd"));
	    			  }
	    			  oldDate = rs.getDate("FCOMPDATE"); 
	    			  
	    		  }else{
	    			  if(oldPortCode.length()==0 && oldDate==null){
	    				  if(startDate==null ){
  	    				  startDate = rs.getDate("FCOMPDATE"); 
  	    			  }
	    				  errorMsg.append("组合【"+rs.getString("fportcode")+"】对应的业务日期【"+YssFun.formatDate(rs.getDate("FCOMPDATE"), "yyyy-MM-dd"));
	        	    			  
	    			  }else{
	    				  errorMsg.append("】至【"+YssFun.formatDate(oldDate,"yyyy-MM-dd")).append("】已确认监控结果...\r\n");
	    				  errorMsg.append("组合【"+rs.getString("fportcode")+"】对应的业务日期【"+YssFun.formatDate(rs.getDate("FCOMPDATE"), "yyyy-MM-dd"));
	        	    	
	    			  }
	    			  oldPortCode = rs.getString("fportcode");
	    			  oldDate = rs.getDate("FCOMPDATE"); 
	    		  }
    		}
    		if(errorMsg.toString().length()>0){
    			if(YssFun.dateDiff(startDate,oldDate)>0){
    				errorMsg.append("】至【"+YssFun.formatDate(oldDate,"yyyy-MM-dd")).append("】已确认监控结果...\r\n");
    			}else if(YssFun.dateDiff(startDate,oldDate)==0){
    				errorMsg.append("】已确认监控结果...\r\n");
    			}
    			
        		errorMsg.append("请先反确认以上组合的监控数据===");
    			throw new YssException(errorMsg.toString());
    		}
    		
    	}catch(Exception e){
    		throw new YssException(errorMsg.toString().length()>0?errorMsg.toString():"监控结果检查报错......");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    	
	}
	
	
	public String getAllSetting() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getListViewData2() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// XXX Auto-generated method stub
		return null;
	}

}
