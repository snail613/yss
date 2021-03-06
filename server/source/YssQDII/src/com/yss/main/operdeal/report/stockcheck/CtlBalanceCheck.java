package com.yss.main.operdeal.report.stockcheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;

import com.yss.dsub.BaseBean;
import com.yss.dsub.DbBase;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssDbOperSql;

/**
 * modify huangqirong 2012-11-15 story #3272 余额报表核对
 * */
// public class CtlBalanceCheck extends BaseAPOperValue implements
// IClientOperRequest {
public class CtlBalanceCheck extends BaseBuildCommonRep {
	
	protected CommonRepBean repBean;
	private String reportType = "";
	private String showType = "";
	private String checkDate = "";
	private String portCode = "";
	private String setId = "";
	private String showLevel = "";
	
	public CtlBalanceCheck() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 管理人  展示有序的字段    管理人字段数量和托管人字段数量必须一致
	 * */
	private String [] checkManagerFields = new String []{			
			"FACCTCODEA".toUpperCase(),
			"FACCTNAMEA".toUpperCase(),			
			"FSTARTBALA".toUpperCase(),
			"FDEBITA".toUpperCase(),
			"FCREDITA".toUpperCase(),
		    "FACCDEBITA".toUpperCase(),
		    "FACCCREDITA".toUpperCase(),
		    "FENDBALA".toUpperCase(),
		    "FBSTARTBALA".toUpperCase(),
		    "FBDEBITA".toUpperCase(),
		    "FBCREDITA".toUpperCase(),
		    "FBACCDEBITA".toUpperCase(),
		    "FBACCREDITA".toUpperCase(),
		    "FBENDBALA".toUpperCase(),
		    "FASTARTBALA".toUpperCase(),
		    "FADEBITA".toUpperCase(),
		    "FACREDITA".toUpperCase(),
		    "FAACCDEBITA".toUpperCase(),
		    "FAACCREDITA".toUpperCase(),
		    "FAENDBALA".toUpperCase()
			};	
	
	
	/**
	 * 托管人  展示有序的字段   管理人字段数量和托管人字段数量必须一致
	 * */
	private String [] checkTrusteeFields = new String []{			
			"FACCTCODEB".toUpperCase(),
			"FACCTNAMEB".toUpperCase(),
			"FSTARTBALB".toUpperCase(),
			"FDEBITB".toUpperCase(),
			"FCREDITB".toUpperCase(),
			"FACCDEBITB".toUpperCase(),
			"FACCCREDITB".toUpperCase(),	
			"FENDBALB".toUpperCase(),
			"FBSTARTBALB".toUpperCase(),
			"FBDEBITB".toUpperCase(),
			"FBCREDITB".toUpperCase(),
			"FBACCDEBITB".toUpperCase(),
			"FBACCREDITB".toUpperCase(),
			"FBENDBALB".toUpperCase(),
			"FASTARTBALB".toUpperCase(),
			"FADEBITB".toUpperCase(),
			"FACREDITB".toUpperCase(),
			"FAACCDEBITB".toUpperCase(),
			"FAACCCREDITB".toUpperCase(),
			"FAENDBALB".toUpperCase()
			};
	
	/**
	 * 字段类型 和上面字段 的个数是一致的
	 * */
	private String [] fieldsType = new String []{"String","String","Number",
										 		 "Number","Number","Number","Number","Number",
										 		"Number","Number","Number","Number","Number",
										 		"Number","Number","Number","Number","Number",
										 		"Number","Number"
										 		 };
    
	/**原币特殊字段获取*/
	private Hashtable<String, String> currencyField = new Hashtable<String, String>();
	
	public void initBuildReport(BaseBean bean) throws YssException {
		repBean = (CommonRepBean) bean;
        //解析前台传入的条件字符串
        this.parseRowStr(this.repBean.getRepCtlParam()); 
        
        this.currencyField.put("FSTARTBALB".toUpperCase(),"FBSTARTBALB".toUpperCase());
        this.currencyField.put("FDEBITB".toUpperCase(),"FBDEBITB".toUpperCase());
        this.currencyField.put("FCREDITB".toUpperCase(),"FBCREDITB".toUpperCase());
        this.currencyField.put("FACCDEBITB".toUpperCase(),"FBACCDEBITB".toUpperCase());
        this.currencyField.put("FACCCREDITB".toUpperCase(),"FBACCREDITB".toUpperCase());
        this.currencyField.put("FENDBALB".toUpperCase(),"FBENDBALB".toUpperCase());
	}	
	
	public void parseRowStr(String sRowStr) throws YssException {
		try {
            if (sRowStr.equals("")) {
                return;
            }
            String reqAry[] = null;
            reqAry = sRowStr.split("\n"); //这里是要获得参数
            this.reportType = reqAry[0].split("\r")[1];
            this.showType = reqAry[1].split("\r")[1];
            this.checkDate =reqAry[2].split("\r")[1];
            this.portCode = reqAry[3].split("\r")[1];
            this.setId = reqAry[4].split("\r")[1];
            this.showLevel = reqAry[5].split("\r")[1];
        } catch (Exception e) {
            throw new YssException("解析报表创建条件出错！", e);
        }
	}
	
	/**
	 * 生成报表
	 * */
	public String buildReport(String sType) throws YssException {
		this.preBalanceCheck();
		String reportData = "";
		reportData = this.getBalanceCheck(this.checkDate , this.setId);
		return reportData;
	}
	
	
	/**
	 * 余额核对数据
	 * */
	private String getBalanceCheck(String checkDate , String setIdInt) throws YssException {
		ResultSet rs = null;
		StringBuffer result = new StringBuffer();		
		
		String strSetId = YssFun.formatNumber(Integer.parseInt(setIdInt),"000");
		String [] dates = checkDate.split("-");
		String asset = this.getAssetCodeByPortCode(this.portCode);
		
		String sql = " select (case when tvb.fisdetail = 1 then '是' else '否' end) as fisdetailB," +
						 " (case when tvb.fisdetail = 0 then '-' " +
						 " when tvb.fisdetail = 1 and length(Trim(tvb.fauxiacc)) is not null and instr(tmpBal.FAcctCodeB, '_', 1) = 0 then '-'" +
						 " else ' ' end) as symbol, (case when length(Trim( " +
	                   	 " case when instr(tvb.fauxiacc ,'|') > 0 then substr(tvb.fauxiacc,3,instr(tvb.fauxiacc,'|') - 3) " +
	                     " else substr(tvb.fauxiacc,3,length(tvb.fauxiacc)) end)) > 0 then ala.facctlevel + 1 " +
	                     " else ala.facctlevel end) as FacctLevel, 'CNY' as FCURCODEB, (case when length(Trim(tvb.fauxiacc)) is not null " +
	                     " and tvb.fauxiacc <> ' ' then ax.auxiaccname else ala.facctname end )as FAcctNameB,(case " +
						 " when tvb.fisdetail = 1 and ala.Facctlevel > 3 and get_val_facctcode(tmpBal.FACCTCODEB) != 0 " + 
	                     " then substr(tmpBal.FACCTCODEB, 0, INSTR(tmpBal.FACCTCODEB, '_', 1, 1) - 1) else tmpBal.FACCTCODEB end)" +
	                     " as facctcode1, (case when tvb.fisdetail = 1 and ala.Facctlevel > 3 " +
	                     " and get_val_facctcode(tmpBal.FACCTCODEB) != 0 then get_val_facctcode(tmpBal.FACCTCODEB) else 0 end) as " +
	                     " facctcode2,trcbal.*, tmpBal.* from (select " + dbl.sqlString(dates[1]) + " as FMonthB , " +
						 " FACCTCODE as FACCTCODEB, " +
						 " (case when (select count(*) from TMPVOUCHERBALBAL tvb where tvb.facctcode = FACCTCODE) > 1 then  null else " +
						 " sum(FSTARTBAL) " +
						 " end) as FSTARTBALB, " +
						 " (case when (select count(*) from TMPVOUCHERBALBAL tvb where tvb.facctcode = FACCTCODE) > 1 then null " +
						 " else " +
						 " sum(FDEBIT) " +
						 " end) as FDEBITB, " +
						 " (case when (select count(*) from TMPVOUCHERBALBAL tvb where tvb.facctcode = FACCTCODE) > 1 then null " +
						 " else " +
						 " sum(FCREDIT) " +
						 " end) as FCREDITB, " +
						 " (case when (select count(*) from TMPVOUCHERBALBAL tvb where tvb.facctcode = FACCTCODE) > 1 then null " +
						 " else " +
						 " sum(FACCDEBIT) " +
						 " end) as FACCDEBITB, " +
						 " (case when (select count(*) from TMPVOUCHERBALBAL tvb where tvb.facctcode = FACCTCODE) > 1 then null " +
						 " else " +
						 " sum(FACCCREDIT) " +
						 " end) as FACCCREDITB, " +
						 " (case when (select count(*) from TMPVOUCHERBALBAL tvb where tvb.facctcode = FACCTCODE) > 1 then null " +
						 " else sum(FENDBAL) end) as FENDBALB,sum(FBSTARTBAL) as FBSTARTBALB,sum(FBDEBIT) as FBDEBITB," +
						 " sum(FBCREDIT) as FBCREDITB, sum(FBACCDEBIT) as FBACCDEBITB,sum(FBACCREDIT) as FBACCREDITB," +
						 " sum(FBENDBAL) as FBENDBALB,sum(FASTARTBAL) as FASTARTBALB, sum(FADEBIT) as FADEBITB," +
						 " sum(FACREDIT) as FACREDITB,sum(FAACCDEBIT) as FAACCDEBITB, sum(FAACCREDIT) as FAACCCREDITB, " +
						 " sum(FAENDBAL) as FAENDBALB from TMPVOUCHERBALBAL where Fmonth = " + dbl.sqlString(dates[1]) + 
						 " group by facctcode) tmpBal " +
						 " full join (select FMonth as FmonthA, " +
						 " FACCTCODE as FACCTCODEA,FACCTNAME as FACCTNAMEA,'CNY' as FCURCODEA,FSTARTBAL as FSTARTBALA," +
						 " FDEBIT as FDEBITA,FCREDIT as FCREDITA,FACCDEBIT as FACCDEBITA,FACCCREDIT as FACCCREDITA," +
						 " FENDBAL as FENDBALA,FBSTARTBAL as FBSTARTBALA,FBDEBIT as FBDEBITA,FBCREDIT as FBCREDITA," +
						 " FBACCDEBIT as FBACCDEBITA,FBACCREDIT as FBACCREDITA,FBENDBAL as FBENDBALA,FASTARTBAL as FASTARTBALA," +
						 " FADEBIT as FADEBITA,FACREDIT as FACREDITA,FAACCDEBIT as FAACCDEBITA,FAACCREDIT as FAACCREDITA, " +
						 " FAENDBAL as FAENDBALA,(case when FISDETAIL = 1 then '是' else '否'end) as FISDETAILA, " +
						 " FCREATETIME,FCheckTime,FCONVERACCTCODE from " + pub.yssGetTableName("Tb_Rep_CheckBALANCE") + 
						 " where FYear = " + dbl.sqlString(dates[0]) + " and FAssetCode = " + dbl.sqlString(this.setId) + 
						 " and FMonth = " + dbl.sqlString(dates[1]) + ") trcbal on tmpBal.FacctcodeB = trcbal.fconveracctcode " +
	                     " and tmpBal.FMonthB = trcbal.fmonthA " +
						 " left join (select distinct tb.facctcode as facctcode , tb.fisdetail as fisdetail , tb.fauxiacc as fauxiacc from TMPVOUCHERBALBAL tb " +
						 " where Fmonth = " + dbl.sqlString(dates[1]) + " )tvb  " +
						 " on tmpBal.FACCTCODEB = tvb.facctcode " +
						 " inner join ( select * from " + pub.yssGetTableName("Tb_Dao_Dict") + " tdd where tdd.fdictcode = 'ZHACCTCODECONVERT') tddt " +
				         " on tmpBal.FACCTCODEB = (case when instr(tddt.fcnvconent,'|') > 0 then substr(tddt.fcnvconent,instr(tddt.fcnvconent,'|')+1) else tddt.fcnvconent end) " +
						 " and trcbal.FACCTCODEA = tddt.fsrcconent " +
						 " left join a" + dates[0] + strSetId + "LAccount ala on ala.facctcode = " +
	                     " (case when length(trim(tvb.Fauxiacc)) is not null then substr(tvb.facctcode,1,instr(tvb.facctcode,'_')-1) else tvb.facctcode end ) " +
						 " left join A" + dates[0]+ strSetId + "AUXIACCSET ax on tvb.fauxiacc = ax.auxiaccid " +
						 " order by FacctCode1,FacctCode2" ;
		            
		    try {
		    	rs = dbl.openResultSet(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		    	while(rs.next()){		    		
		    		StringBuffer rowGuessData = new StringBuffer();
					
					int level = rs.getInt("FAcctLevel");	//级别
					String symbol = rs.getString("symbol");	 //伸缩符
					
					rowGuessData.append(level).append("\t").append(symbol).append("\t"); //拼接级别和伸缩符
					
					//遍历设置好的字段 及判断各字段的类型
		    		for (int i = 0; i < this.checkManagerFields.length; i++) { 
						if("String".equalsIgnoreCase(this.fieldsType[i])){
							rowGuessData.append(rs.getString(this.checkManagerFields[i])).append("\t");
							rowGuessData.append(rs.getString(this.checkTrusteeFields[i])).append("\t");
						}else if("Number".equalsIgnoreCase(this.fieldsType[i])){
							
							rowGuessData.append(YssD.round(rs.getDouble(this.checkManagerFields[i]),2)).append("\t"); //数值 和 数据文件一致
							if("159920".equalsIgnoreCase(asset)){ //特殊资产处理
								if(this.currencyField.containsKey(this.checkTrusteeFields[i]))
									rowGuessData.append(YssD.round(rs.getDouble(this.currencyField.get(this.checkTrusteeFields[i])),2)).append("\t"); //数值 和 数据文件一致
								else
									rowGuessData.append(YssD.round(rs.getDouble(this.checkTrusteeFields[i]),2)).append("\t"); //数值 和 数据文件一致
							}else{
								rowGuessData.append(YssD.round(rs.getDouble(this.checkTrusteeFields[i]),2)).append("\t"); //数值 和 数据文件一致
							}
						}
					}
		    		
		    		result.append(this.buildRowCompResult(rowGuessData.toString())).append("\r\n"); //一行为止
		    		
		    		if(rs.isLast()){
		    			rowGuessData = new StringBuffer();
		    			rowGuessData.append("sp\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
		    			rowGuessData.append(" \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
		    			result.append(this.buildRowCompResult(rowGuessData.toString())).append("\r\n");
		    			
		    			rowGuessData = new StringBuffer();
		    			rowGuessData.append("sp\t \t上传时间：\t" + rs.getString("FCreateTime") + " \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
		    			rowGuessData.append(" \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
		    			result.append(this.buildRowCompResult(rowGuessData.toString())).append("\r\n");
		    			
		    			rowGuessData = new StringBuffer();
		    			rowGuessData.append("sp\t \t核对时间：\t" + rs.getString("FCheckTime") + " \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t  \t \t ");
		    			rowGuessData.append(" \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
		    			result.append(this.buildRowCompResult(rowGuessData.toString())).append("\r\n");		    			
					}
		    	}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("获取余额表数据出错：\n"+e.getMessage());
	            throw new YssException("获取余额表数据出错：\n"+e.getMessage());
			}finally{
				if(rs != null){
					try {
						rs.close();
					} catch (SQLException e2) {
						System.out.println("获取余额表数据出错：\n"+e2.getMessage());
			            throw new YssException("获取余额表数据出错：\n"+e2.getMessage());
					}
					
				}
				
			}
			
		return result.toString();
	}
	
	/**
	 * 获取资产代码
	 * */
	private String getAssetCodeByPortCode(String portCode) throws YssException{
		String sResult = "159920";
        ResultSet portRs = null;        
        String sql = "select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
        				" ppf where ppf.fportcode = " + dbl.sqlString(portCode) +
        				" and ppf.fcheckstate = 1 and ppf.fenabled = 1" ;
	    try{
	    	portRs = dbl.openResultSet(sql);
	        if(portRs.next()){
		        	sResult = portRs.getString("fassetcode");
		    }
        
        } catch (Exception e) {
        	System.out.println("获取余额表数据时的获取资产代码出错：\n"+e.getMessage());
            throw new YssException("获取余额表数据时的获取资产代码出错：\n"+e.getMessage());
        } finally {
        	dbl.closeResultSetFinal(portRs);
        }
        return sResult;
	}
	
	/**
	 * 余额预处理
	 * */
	private void preBalanceCheck() throws YssException{
		String sql = "" ;
		try {
			dbl.loadConnection().setAutoCommit(true); //
			String [] dates = this.checkDate.split("-");
			
			/**
			 * 记录核对时间
			 * */
			sql = "update " + pub.yssGetTableName("Tb_Rep_CheckBALANCE")+ " set FCHECKUSER = " + dbl.sqlString(pub.getUserCode()) + 
						 " , FCHECKTIME = TO_CHAR(SYSDATE, 'YYYYMMDD HH24:MI:SS') " +						 
						 " where FYear = " + dbl.sqlString(dates[0]) + 
						 " and FAssetCode = " + dbl.sqlString(this.setId) +
						 " and FMonth = " + dbl.sqlString(dates[1]) ;
			dbl.executeSql(sql);
			
			/**
			 * 此处创建余额数据临时表
			 * */
			if(!dbl.yssTableExist("TMPVOUCHERBALBAL")){
				String createSql = "" +
				" create table TMPVOUCHERBALBAL (" +
				" FAACCDEBIT NUMBER(19,4), " +
				" FAACCREDIT NUMBER(19,4), " +
				" FACCCREDIT NUMBER(19,4), " +
				" FACCDEBIT  NUMBER(19,4), " +
				" FACCTCODE  VARCHAR2(50) not null, " +
				" FACREDIT   NUMBER(19,4), " +
				" FADDR      VARCHAR2(30) not null, " +
				" FADEBIT    NUMBER(19,4), " +
				" FAENDBAL   NUMBER(19,4), " +
				" FASTARTBAL NUMBER(19,4), " +
				" FAUXIACC   VARCHAR2(100) not null, " +
				" FBACCDEBIT NUMBER(19,4), " +
				" FBACCREDIT NUMBER(19,4), " +
				" FBCREDIT   NUMBER(19,4), " +
				" FBDEBIT    NUMBER(19,4), " +
				" FBENDBAL   NUMBER(19,4), " +
				" FBSTARTBAL NUMBER(19,4), " +
				" FCREDIT    NUMBER(19,4), " +
				" FCURCODE   VARCHAR2(3) not null, " +
				" FDEBIT     NUMBER(19,5), " +
				" FENDBAL    NUMBER(19,4), " +
				" FISDETAIL  NUMBER(1), " +
				" FMONTH     NUMBER(3) not null, " +
				" FSTARTBAL  NUMBER(19,4) ) " ;
				
				dbl.executeSql(createSql);
				
				createSql = " alter table TMPVOUCHERBALBAL "+
				 			" add constraint PK_TMPVOUCHERBALBAL primary key (FACCTCODE, FADDR, FAUXIACC, FCURCODE, FMONTH)";
				dbl.executeSql(createSql);
			}
			
			sql = "delete from TMPVOUCHERBALBAL where 1=1 and FADDR = " + dbl.sqlString(pub.getUserCode());
			
			dbl.executeSql(sql);  //先删除所有数据
			
			/**
			 * 期初余额
			 * */
			sql = " insert into TMPVOUCHERBALBAL " + 
				  " select " + 
				  " FAACCDEBIT, " + 
				  " FAACCCREDIT as FAACCREDIT, " +
				  " FACCCREDIT, " + 
				  " FACCDEBIT," + 
				  " (case when (FAuxiAcc is not null and FAuxiAcc <> ' ') then facctcode||'_'||( " +
				  " 	  Case When instr(FAuxiAcc, '|') > 2 " +
				  			 		" Then SUBSTR(FAuxiAcc, 3, instr(FAuxiAcc, '|') - 3) " +
				  			 " Else SUBSTR(FAuxiAcc, 3, length(FAuxiAcc)) End ) " +
				  		" when (FauxiAcc is not null and FauxiAcc <> ' ') then facctcode||'_'||( " +
				  				" Case When instr(FAuxiAcc, '|') > 2 Then SUBSTR(FAuxiAcc, 3, instr(FAuxiAcc, '|') - 3) " +
				  				" Else SUBSTR(FAuxiAcc, 3, length(FAuxiAcc)) End ) " +
				  		" else facctcode end) as FACCTCODE , " +
				  " FACREDIT, " +
				   dbl.sqlString(pub.getUserCode()) + " as FADDR, " +
				  " FADEBIT, " +
				  " FAENDBAL, " + 
				  " FASTARTBAL, " + 
				  " FAUXIACC, " + 
				  " FBACCDEBIT, " + 
				  " FBACCCREDIT as FBACCREDIT, " + 
				  " FBCREDIT, " + 
				  " FBDEBIT, " + 
				  " FBENDBAL, " + 
				  " FBSTARTBAL, " + 
				  " FCREDIT, " + 
				  " FCURCODE, " +
				  " FDEBIT, " +
				  " FENDBAL, " +
				  " FISDETAIL, " +
				  " FMONTH, " +
				  " FSTARTBAL " +
				  " from A" + dates[0] + YssFun.formatNumber(Integer.parseInt(this.setId),"000") + 
				  "lbalance a where fmonth=0 or fmonth= (case when " + dates[1] + " > 1  " +
				  											" then (" + dates[1] + " -1) else 0 end) " ;
			dbl.executeSql(sql);
			
			
			/**
			 * 期初和明细科目
			 * */
			sql = " insert into TMPVOUCHERBALBAL " +
			  	  " select (NVL(fjsl, 0) + NVL(faaccdebit, 0)) as FAAccDebit, " +
			      " (NVL(fdsl, 0) + NVL(faacccredit, 0)) as FAAccCredit, " +
			      " (NVL(fdje, 0) + NVL(facccredit, 0)) as FaccCredit, " +
			      " (NVL(fjje, 0) + NVL(faccdebit, 0)) as FaccDebit, " +
			      " (case " +
			      " when (a.FAuxiAcc is not null and a.FAuxiAcc <> ' ') then " +
			      " (NVL(fkmh, facctcode) ||'_' || (Case " +
			      " When instr(a.FAuxiAcc, '|') > 2 Then " +
			      " SUBSTR(a.FAuxiAcc, 3, instr(a.FAuxiAcc, '|') - 3) " +
			      " Else " +
			      " SUBSTR(a.FAuxiAcc, 3, length(a.FAuxiAcc)) " +
			      " End)) when(b.FauxiAcc is not null and b.FauxiAcc <> ' ') then(NVL(fkmh, facctcode) || '_' || (" +
			      		     " Case When instr(b.FAuxiAcc, '|') > 2 " +
				      			 " Then SUBSTR(b.FAuxiAcc, 3, instr(b.FAuxiAcc, '|') - 3) " +
				      			 " Else SUBSTR(b.FAuxiAcc, 3, length(b.FAuxiAcc)) End)) " +
			      		" else NVL(fkmh, facctcode) end) as FACCTCODE, " +
			      " NVL(fdsl, 0) as FACredit, " +
				  dbl.sqlString(pub.getUserCode()) + " as FADDR, " +
				  " NVL(fjsl, 0) as FADebit, " +
			      " (NVL(faendbal, 0) + NVL(fjsl, 0) - NVL(fdsl, 0)) as FAEndBal, " +
			      " NVL(faendbal, 0) as FAStartBal, " +
			      " (case when a.FAuxiAcc is null then b.FauxiAcc " +
			      		" else " +
			            " a.FauxiAcc end) as FauxiAcc, " +
			      " (NVL(fbjje, 0) + NVL(fbaccdebit, 0)) as FBAccDebit, " +
			      " (NVL(fbdje, 0) + NVL(fbacccredit, 0)) as FBAcCredit, " +
			      " NVL(fbdje, 0) as FBCredit, " +
			      " NVL(fbjje, 0) as FBDebit, " +
			      " (NVL(fbendbal, 0) + NVL(fbjje, 0) - NVL(fbdje, 0)) as FBEndBal, " +
			      " NVL(fbendbal, 0) as FBStartBal, " +
			      " NVL(fdje, 0) as Fcredit, " +
			      " NVL(fcyid, fcurcode) as FCurcode, " +
			      " NVL(fjje, 0) as Fdebit, " +
			      "(NVL(fendbal, 0) + NVL(fjje, 0) - NVL(fdje, 0)) as FendBal,1, " + dates[1] + " as FMonth,  " +
			      " NVL(fendbal, 0) as FstartBal  " +
			      " from (select fkmh, " +
			      " fcyid, " +
			      " sum(case " +
			      " when fjd = 'J' then fbal else 0 end) as fjje, " +
			      " sum(case when fjd = 'D' then fbal else 0 end) as fdje, " +
			      " sum(case when fjd = 'J' then fsl else 0  end) as fjsl, " +
			      " sum(case when fjd = 'D' then fsl else 0 end) as fdsl, " +
			      " sum(case when fjd = 'J' then fbbal else 0 end) as fbjje, " +
			      " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje, " +
			      " FauxiAcc  " +
			      " from A" + dates[0] + YssFun.formatNumber(Integer.parseInt(this.setId),"000") + "fcwvch " +
			      " where fterm = " + dbl.sqlString(dates[1]) +
			      " and (fconfirmer <> ' ' or fconfirmer is null) " +
			      " and fdate <= " +
			      " (case when " + dbl.sqlDate(this.checkDate) + 
			      	" < (Select FEndDate from AccountingPeriod " +
			        " where fsetcode = " + this.setId +
			        " and fyear = " + dbl.sqlString(dates[0]) +
			        " and fterm = " + dbl.sqlString(dates[1]) + " ) then " + dbl.sqlDate(this.checkDate) +
			        " else " +
			        " (Select FEndDate from AccountingPeriod " +
			        " where fsetcode = " + dbl.sqlString(this.setId) + 
			        " and fyear = " + dbl.sqlString(dates[0]) + 
			        " and fterm = " + dbl.sqlString(dates[1]) + ") end) " +
			        " group by fkmh, fcyid, FauxiAcc) a " +
			        " full join (select c.facctcode, " +
			        " fmonth, " +
			        " c.fcurcode, " +
			        " faccdebit, " +
			        " facccredit, " +
			        " fendbal, " +
			        " fbaccdebit, " +
			        " fbacccredit, " +
			        " fbendbal, " +
			        " faaccdebit, " +
			        " faacccredit, " +
			        " faendbal, " + 
			        " c.fauxiacc " + 
			        " from (select *  " +
			        " from A" + dates[0] + YssFun.formatNumber(Integer.parseInt(this.setId),"000") + "LBalance " +
			        " where fmonth = (case when " + dates[1] + " <= " +
			                               " (select fstartmonth From lsetlist " +
			                               " where fsetcode = " + dbl.sqlString(this.setId) + 
			                               " and fyear = " + dbl.sqlString(dates[0]) + 
			                               " and rownum = 1) then 0 " +
			                               " else( " + dates[1] + " - 1) end)) c " +
			        " join (select facctcode, facctdetail " +
			        " from A" + dates[0] + YssFun.formatNumber(Integer.parseInt(this.setId),"000") + "laccount " +
			        		" where facctdetail = 1) d on c.facctcode = d.facctcode) b " +
			        		" on a.fkmh = b.facctcode and a.fcyid = b.fcurcode and a.fauxiacc = b.fauxiacc " ;
			
			dbl.executeSql(sql);
			
			/**
			 * 非明细科目
			 * */
			sql = "insert into TMPVOUCHERBALBAL " +
					" select * from (  " +
					" select " +					
					" sum(b.faaccdebit) as FAACCDEBIT, " +
				    " sum(b.faaccredit) as FAACCREDIT, " +
	                " sum(b.facccredit) as FACCCREDIT, " +
	                " sum(b.faccdebit) as FACCDEBIT, " +
	                " a.facctcode as FACCTCODE, " +
	                " sum(b.facredit) as FACREDIT, " +
	                 dbl.sqlString(pub.getUserCode()) + " as FADDR ," +
	                " sum(b.fadebit) as FADEBIT, " +
	                " sum(b.faendbal) as FAENDBAL, " +
	                " sum(b.fastartbal) as FASTARTBAL, " +
	                " ' ' as FAUXIACC, " +
	                " sum(b.fbaccdebit) as FBACCDEBIT, " +
	                "  sum(b.fbaccredit) as FBACCREDIT, " +
	                "  sum(b.fbcredit) as FBCREDIT, " +
	                "  sum(b.fbdebit) as FBDEBIT, " +
	                "  sum(b.fbendbal) as FBENDBAL, " +
	                "  sum(b.fbstartbal) as FBSTARTBAL, " +
	                "  sum(b.fcredit) as FCREDIT, " +
	                "  b.fcurcode as FCURCODE, " +
	                "  sum(b.fdebit) as FDEBIT, " +
	                "  sum(b.fendbal) as FENDBAL, " +
	                " 0 as FISDETAIL, " +
	                dbl.sqlString(dates[1]) + " as FMONTH, " +
	                " sum(b.fstartbal) as FSTARTBAL	" +
				    " from A" + dates[0] + YssFun.formatNumber(Integer.parseInt(this.setId),"000") + "laccount a " +
				    " join TMPVOUCHERBALBAL b on a.facctcode = SubStr(b.facctcode, 1, 4) and length(b.facctcode ) > 4 " +
				    " where b.fmonth = " + dbl.sqlString(dates[1]) + 
				    " and length(a.facctcode) = 4  " +
				    " and (a.facctdetail = 0 or (a.fauxiacc <> ' ' and a.facctdetail = 1)) " +
				    " and FAddr = " + dbl.sqlString(pub.getUserCode()) +
				    " group by a.facctcode, b.fcurcode " +
				    " order by a.facctcode) " +
				    " union  " +
				   " select * from ( " +
				   " select " +
				   " sum(b.faaccdebit) as FAACCDEBIT, " +
				    " sum(b.faaccredit) as FAACCREDIT, " +
	                " sum(b.facccredit) as FACCCREDIT, " +
	                " sum(b.faccdebit) as FACCDEBIT, " +
	                " a.facctcode as FACCTCODE, " +
	                " sum(b.facredit) as FACREDIT, " +
	                 dbl.sqlString(pub.getUserCode()) + " as FADDR ," +
	                " sum(b.fadebit) as FADEBIT, " +
	                " sum(b.faendbal) as FAENDBAL, " +
	                " sum(b.fastartbal) as FASTARTBAL, " +
	                " ' ' as FAUXIACC, " +
	                " sum(b.fbaccdebit) as FBACCDEBIT, " +
	                "  sum(b.fbaccredit) as FBACCREDIT, " +
	                "  sum(b.fbcredit) as FBCREDIT, " +
	                "  sum(b.fbdebit) as FBDEBIT, " +
	                "  sum(b.fbendbal) as FBENDBAL, " +
	                "  sum(b.fbstartbal) as FBSTARTBAL, " +
	                "  sum(b.fcredit) as FCREDIT, " +
	                "  b.fcurcode as FCURCODE, " +
	                "  sum(b.fdebit) as FDEBIT, " +
	                "  sum(b.fendbal) as FENDBAL, " +
	                " 0 as FISDETAIL, " +
	                dbl.sqlString(dates[1]) + " as FMONTH, " +
	                " sum(b.fstartbal) as FSTARTBAL	" +
				    " from A" + dates[0] + YssFun.formatNumber(Integer.parseInt(this.setId),"000") + "laccount a " +
				    " join TMPVOUCHERBALBAL b on a.facctcode = SubStr(b.facctcode, 1, 6) and length(b.facctcode ) > 6 " +
				    " where b.fmonth = " + dbl.sqlString(dates[1]) + 
				    " and length(a.facctcode) = 6  " +
				    " and (a.facctdetail = 0 or (a.fauxiacc <> ' ' and a.facctdetail = 1)) " +
				    " and FAddr = " + dbl.sqlString(pub.getUserCode()) +
				    " group by a.facctcode, b.fcurcode  " +
				    " order by a.facctcode) " +
				    " union " +
				    " select * from ( " +
				    " select " +
				    " sum(b.faaccdebit) as FAACCDEBIT, " +
				    " sum(b.faaccredit) as FAACCREDIT, " +
	                " sum(b.facccredit) as FACCCREDIT, " +
	                " sum(b.faccdebit) as FACCDEBIT, " +
	                " a.facctcode as FACCTCODE, " +
	                " sum(b.facredit) as FACREDIT, " +
	                 dbl.sqlString(pub.getUserCode()) + " as FADDR ," +
	                " sum(b.fadebit) as FADEBIT, " +
	                " sum(b.faendbal) as FAENDBAL, " +
	                " sum(b.fastartbal) as FASTARTBAL, " +
	                " ' ' as FAUXIACC, " +
	                " sum(b.fbaccdebit) as FBACCDEBIT, " +
	                "  sum(b.fbaccredit) as FBACCREDIT, " +
	                "  sum(b.fbcredit) as FBCREDIT, " +
	                "  sum(b.fbdebit) as FBDEBIT, " +
	                "  sum(b.fbendbal) as FBENDBAL, " +
	                "  sum(b.fbstartbal) as FBSTARTBAL, " +
	                "  sum(b.fcredit) as FCREDIT, " +
	                "  b.fcurcode as FCURCODE, " +
	                "  sum(b.fdebit) as FDEBIT, " +
	                "  sum(b.fendbal) as FENDBAL, " +
	                " 0 as FISDETAIL, " +
	                dbl.sqlString(dates[1]) + " as FMONTH, " +
	                " sum(b.fstartbal) as FSTARTBAL	" +
				    " from A" + dates[0] + YssFun.formatNumber(Integer.parseInt(this.setId),"000") + "laccount a  " +
				    " join TMPVOUCHERBALBAL b on a.facctcode = SubStr(b.facctcode, 1, 8) and length(b.facctcode ) > 8 " +
				    " where b.fmonth = " + dbl.sqlString(dates[1]) + 
				    " and length(a.facctcode) = 8  " +
				    " and (a.facctdetail = 0 or (a.fauxiacc <> ' ' and a.facctdetail = 1)) " +
				    " and FAddr = " + dbl.sqlString(pub.getUserCode()) + 
				    " group by a.facctcode, b.fcurcode  " +
				    " order by a.facctcode) " ;
			dbl.executeSql(sql);
			
			dbl.loadConnection().setAutoCommit(false);
		} catch (SQLException e) {			
			e.printStackTrace();
			System.out.println("执行余额表预处理出错：\n"+e.getMessage());
            throw new YssException("执行余额表预处理出错：\n"+e.getMessage());
		}
	}
	
	
	protected String buildRowCompResult(String str) throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";
        RepTabCellBean rtc = null;
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("DS_CheckYUE");
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("DS_CheckYUE") +
                " and FCheckState = 1 order by FOrderIndex";
            rs = dbl.openResultSet(strSql);
            for (int i = 0; i < sArry.length; i++) {
                sKey = "DS_CheckYUE" + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append("\t");
            }
            if (buf.toString().trim().length() > 1) {
                strReturn = buf.toString().substring(0,
                    buf.toString().length() - 1);
            }
            rs.close();
            return strReturn;
        } catch (Exception e) {
        	System.out.println("获取余额表核对报表格式出错：\n"+e.getMessage());
            throw new YssException("获取余额表核对报表格式出错：\n"+e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	
	/*
	 * public String checkRequest(String sType) throws YssException { // TODO
	 * Auto-generated method stub return null; }
	 * 
	 * public String doOperation(String sType) throws YssException { // TODO
	 * Auto-generated method stub return null; }
	 * 
	 * public String buildRowStr() throws YssException { // TODO Auto-generated
	 * method stub return null; }
	 * 
	 * public String getOperValue(String sType) throws YssException { String[]
	 * confirms = null; String resultStr = ""; confirms = sType.split("\b\f\b");
	 * if (confirms[3].equalsIgnoreCase("check")) { resultStr =
	 * doStockCheck(confirms[0],confirms[1],confirms[2]); } return resultStr; }
	 * 
	 * private String doStockCheck(String checkDate, String portCode , String
	 * curyName) throws YssException { String strSql = ""; ResultSet rs = null;
	 * PreparedStatement stm = null; Connection conn =
	 * dbl.loadConnection();//新建连接 boolean bTrans = false; int lMonth =
	 * Integer.parseInt
	 * (checkDate.substring(checkDate.indexOf("-")+1,checkDate.indexOf("-")+3));
	 * String year = checkDate.substring(0,checkDate.indexOf("-")); YssFinance
	 * cw = new YssFinance(); cw.setYssPub(pub); String YssTablePrefix = ""; int
	 * YssStartMonth = 0; int kmLength = 4; String set = ""; String acctCode =
	 * ""; String securityCode = ""; try{ conn.setAutoCommit(false);//设置手动提交事务
	 * bTrans = true; strSql = "select count(*) as cou from " +
	 * pub.yssGetTableName("TB_DATA_ZHBALANCE") + " where fportcode = " +
	 * dbl.sqlString(portCode) + " and FDATE = " + dbl.sqlDate(checkDate); rs =
	 * dbl.openResultSet(strSql); set = cw.getCWSetCode(portCode);
	 * YssTablePrefix = "A" + year + set; YssStartMonth =
	 * getThisSetAccLen(set,Integer.parseInt(year)); while (rs.next()){
	 * if(rs.getInt("cou") == 0){ throw new YssException("请先将组合"+ portCode +
	 * "余额日期为" + checkDate + "的余额数据导入至系统中！"); }else{ strSql =
	 * "Delete from tmpBalBal"; dbl.executeSql(strSql); //从余额表中导入年初数及上月数据到临时余额表中
	 * 
	 * strSql = "insert into tmpBalBal select '001', a.*  from " +
	 * YssTablePrefix + "lbalance a where fmonth=0" + ( (lMonth > 1) ?
	 * " or fmonth=" + (lMonth - 1) : ""); dbl.executeSql(strSql);
	 * 
	 * strSql = "insert into tmpBalBal select '001'," + lMonth + "," +
	 * dbl.sqlIsNull("fkmh", "facctcode") + "," + dbl.sqlIsNull("fcyid",
	 * "fcurcode") + "," + dbl.sqlIsNull("fendbal", "0") + "," +
	 * dbl.sqlIsNull("fjje", "0") + "," + dbl.sqlIsNull("fdje", "0") + "," +
	 * dbl.sqlIsNull("fjje", "0") + " + " + dbl.sqlIsNull("faccdebit", "0") +
	 * "," + dbl.sqlIsNull("fdje", "0") + " + " + dbl.sqlIsNull("facccredit",
	 * "0") + "," + dbl.sqlIsNull("fendbal", "0") + " + " +
	 * dbl.sqlIsNull("fjje", "0") + " - " + dbl.sqlIsNull("fdje", "0") + "," +
	 * dbl.sqlIsNull("fbendbal", "0") + "," + dbl.sqlIsNull("fbjje", "0") + ","
	 * + dbl.sqlIsNull("fbdje", "0") + "," + dbl.sqlIsNull("fbjje", "0") + " + "
	 * + dbl.sqlIsNull("fbaccdebit", "0") + "," + dbl.sqlIsNull("fbdje", "0") +
	 * " + " + dbl.sqlIsNull("fbacccredit", "0") + "," +
	 * dbl.sqlIsNull("fbendbal", "0") + " + " + dbl.sqlIsNull("fbjje", "0") +
	 * " - " + dbl.sqlIsNull("fbdje", "0") + "," + dbl.sqlIsNull("faendbal",
	 * "0") + "," + dbl.sqlIsNull("fjsl", "0") + "," + dbl.sqlIsNull("fdsl",
	 * "0") + "," + dbl.sqlIsNull("fjsl", "0") + " + " +
	 * dbl.sqlIsNull("faaccdebit", "0") + "," + dbl.sqlIsNull("fdsl", "0") +
	 * " + " + dbl.sqlIsNull("faacccredit", "0") + "," +
	 * dbl.sqlIsNull("faendbal", "0") + " + " + dbl.sqlIsNull("fjsl", "0") +
	 * " - " + dbl.sqlIsNull("fdsl", "0") +
	 * ",1 ,case when a.FAuxiAcc is null then b.FauxiAcc else a.FauxiAcc end as FauxiAcc "
	 * +
	 * "from (select fkmh,fcyid, sum(case when fjd='J' then fbal else 0 end) as fjje,"
	 * +
	 * "sum(case when fjd='D' then fbal else 0 end) as fdje,sum(case when fjd='J' then fsl else 0 end) as fjsl,"
	 * + "sum(case when fjd='D' then fsl else 0 end) as fdsl," +
	 * "sum(case when fjd='J' then fbbal else 0 end) as fbjje," +
	 * "sum(case when fjd='D' then fbbal else 0 end) as fbdje, " + "FauxiAcc "+
	 * "from " + YssTablePrefix + "fcwvch where fterm=" + lMonth + " and fdate"
	 * + "<=" + dbl.sqlDate(checkDate) + " group by fkmh,fcyid,FauxiAcc) a ";
	 * 
	 * //'考虑余额表临时登帐，上月数据固定从余额表获取 strSql = strSql +
	 * "full join (select c.facctcode,fmonth,c.fcurcode,faccdebit,facccredit," +
	 * "fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faacccredit,faendbal,c.fauxiacc from "
	 * + "(select * from " + YssTablePrefix + "LBalance where fmonth=" + (
	 * (lMonth <= YssStartMonth) ? 0 : lMonth - 1) +
	 * ") c join (select facctcode,facctdetail from " + YssTablePrefix +
	 * "laccount where facctdetail=1) d on c.facctcode=d.facctcode)";
	 * 
	 * strSql = strSql +
	 * " b on a.fkmh=b.facctcode and a.fcyid=b.fcurcode and a.fauxiacc=b.fauxiacc"
	 * ; dbl.executeSql(strSql); for (int i = 1; i <= 3; i++) { kmLength =
	 * kmLength + (i-1)*2; strSql = "insert into tmpBalBal select '001', " +
	 * lMonth + ",a.facctcode" +
	 * ",b.fcurcode,sum(b.fstartbal),sum(b.fdebit),sum(b.fcredit)," +
	 * "sum(b.faccdebit),sum(b.facccredit),sum(b.fendbal),sum(b.fbstartbal),sum(b.fbdebit),"
	 * +
	 * "sum(b.fbcredit),sum(b.fbaccdebit),sum(b.fbacccredit),sum(b.fbendbal),sum(b.fastartbal),"
	 * +
	 * "sum(b.fadebit),sum(b.facredit),sum(b.faaccdebit),sum(b.faacccredit),sum(b.faendbal),0 ,' ' from "
	 * + "A" + year + set + "laccount a join tmpBalBal b on a.facctcode =" +
	 * dbl.sqlLeft("b.facctcode", kmLength) + " where b.fmonth=" + lMonth +
	 * " and "+dbl.sqlLen("a.facctcode") + "=" + kmLength +
	 * " and a.facctdetail=0  " + " and FAddr='001'" +
	 * " group by a.facctcode,b.fcurcode order by a.facctcode";
	 * dbl.executeSql(strSql); } } } dbl.closeResultSetFinal(rs); strSql =
	 * "update " + pub.yssGetTableName("TB_DATA_ZHBALANCE") +
	 * " set FACCTCODE = ?,FACCTNAME = ?,FSTARTBALDC = ?,FSTARTBAL = ?,FDEBIT = ?,FCREDIT = ?,FENDBALDC = ?, FENDBAL = ?, FCURNAME = ? where "
	 * + " fportcode = " + dbl.sqlString(portCode) + " and FDATE = " +
	 * dbl.sqlDate(checkDate) + " and FZHACCTCODE = ? and FCURCODE = " +
	 * dbl.sqlString(curyName); stm = dbl.openPreparedStatement(strSql); strSql
	 * = "select c.*,d.fcurname from (select " +
	 * "a.FBalDC,a.FAcctLevel,a.FAcctName,a.FAcctDetail,a.fcurcode,a.FAuxiAcc,b.* from "
	 * + YssTablePrefix + "LAccount a ," + "  ( " +
	 * "select FAcctcode,fcurcode as bcurcode," +
	 * "sum(case when FMonth=0 then F" + "" + "EndBal else 0 end) as ncye," +
	 * "sum(case when FMonth=0 then 0 else FStartBal end) as StartBal," +
	 * "sum(case when FMonth=0 then 0 else FDebit end) as Debit," +
	 * "sum(case when FMonth=0 then 0 else FCredit end) as Credit," +
	 * "sum(case when FMonth=0 then 0 else FAccDebit end) as AccDebit," +
	 * "sum(case when FMonth=0 then 0 else FAccCredit end) as AccCredit," +
	 * "sum(case when FMonth=0 then 0 else FEndBal end) as EndBal ,"+
	 * "sum(case when FMonth=0 then FBEndBal else 0 end) as bncye," +
	 * "sum(case when FMonth=0 then 0 else FBStartBal end) as bStartBal," +
	 * "sum(case when FMonth=0 then 0 else FBDebit end) as bDebit," +
	 * "sum(case when FMonth=0 then 0 else FBCredit end) as bCredit," +
	 * "sum(case when FMonth=0 then 0 else FBAccDebit end) as bAccDebit," +
	 * "sum(case when FMonth=0 then 0 else FBAccCredit end) as bAccCredit," +
	 * "sum(case when FMonth=0 then 0 else FBEndBal end) as bEndBal" +
	 * ",' ' as Auxiaccname from tmpBalBal where " + "(FMonth=0 or FMonth=" +
	 * lMonth + ")" + " group by FAcctcode,fcurcode  " + " union all " +
	 * "select FAcctcode" + dbl.sqlJoinString() + "'_'" + dbl.sqlJoinString() +
	 * " Case When " + dbl.sqlInstr("FAuxiAcc", "'|'") + ">2 Then  " +
	 * dbl.sqlSubStr("FAuxiAcc", "3", dbl.sqlInstr("FAuxiAcc", "'|'")+ "-3") +
	 * " Else " + dbl.sqlSubStr("FAuxiAcc", "3", dbl.sqlLen("FAuxiAcc")) +
	 * " End as FAcctcode,fcurcode as bcurcode," +
	 * "sum(case when FMonth=0 then F" + "" + "EndBal else 0 end) as ncye," +
	 * "sum(case when FMonth=0 then 0 else FStartBal end) as StartBal," +
	 * "sum(case when FMonth=0 then 0 else FDebit end) as Debit," +
	 * "sum(case when FMonth=0 then 0 else FCredit end) as Credit," +
	 * "sum(case when FMonth=0 then 0 else FAccDebit end) as AccDebit," +
	 * "sum(case when FMonth=0 then 0 else FAccCredit end) as AccCredit," +
	 * "sum(case when FMonth=0 then 0 else FEndBal end) as EndBal ,"+
	 * "sum(case when FMonth=0 then FBEndBal else 0 end) as bncye," +
	 * "sum(case when FMonth=0 then 0 else FBStartBal end) as bStartBal," +
	 * "sum(case when FMonth=0 then 0 else FBDebit end) as bDebit," +
	 * "sum(case when FMonth=0 then 0 else FBCredit end) as bCredit," +
	 * "sum(case when FMonth=0 then 0 else FBAccDebit end) as bAccDebit," +
	 * "sum(case when FMonth=0 then 0 else FBAccCredit end) as bAccCredit," +
	 * "sum(case when FMonth=0 then 0 else FBEndBal end) as bEndBal" +
	 * ",Auxiaccname from tmpBalBal e inner join " + YssTablePrefix +
	 * "Auxiaccset f on (" + dbl.sqlSubStr("e.FAuxiAcc", "0", dbl.sqlInstr(
	 * "e.FAuxiAcc", "'|'") + "-1") +
	 * "=f.Auxiaccid or e.FAuxiAcc=f.Auxiaccid)where " + "(FMonth=0 or FMonth="
	 * + lMonth + ")" + " and " + dbl.sqlLen(dbl.sqlTrim("FAuxiAcc")) +
	 * ">2 group by facctcode,fcurcode,fauxiacc,f.auxiaccname " +
	 * " ) b where substr(b.FAcctCode,0,case when (" +
	 * dbl.sqlInstr("b.FAcctCode", "'_'") + "-1)>0 then " +
	 * dbl.sqlInstr("b.FAcctCode", "'_'") + "-1 else " +
	 * dbl.sqlLen("b.FAcctCode") +
	 * " end )=a.FAcctCode order by b.FAcctCode)c left join " + YssTablePrefix +
	 * "lcurrency d on c.bcurcode=d.fcurcode  order by facctcode,d.fcurcode"; rs
	 * = dbl.openResultSet(strSql); while (rs.next()){ acctCode =
	 * rs.getString("FAcctCode"); if(acctCode.indexOf("_")>0 &&
	 * acctCode.indexOf("HK") == acctCode.length() - 2){ securityCode =
	 * acctCode.substring(acctCode.indexOf("_") + 1,acctCode.indexOf(" "));
	 * acctCode = acctCode.substring(0,acctCode.indexOf("_")) + "H" +
	 * YssFun.formatNumber(Integer.parseInt(securityCode), "00000"); }
	 * stm.setString(1, rs.getString("FAcctCode"));
	 * stm.setString(2,(rs.getString("FAcctCode").indexOf("_")>0 &&
	 * rs.getString("AuxiAccName").trim().length()>0?
	 * rs.getString("AuxiAccName"): rs.getString("FAcctName")));
	 * stm.setString(9, rs.getString("fcurname")); stm.setString(10,acctCode);
	 * if(curyName.equalsIgnoreCase("人民币")){//本位币 if(rs.getDouble("BStartBal")
	 * == 0){ stm.setString(3, "平"); stm.setDouble(4, 0); }else{
	 * stm.setString(3, rs.getInt("FBalDC") == 1 ? "借" : "贷"); stm.setDouble(4,
	 * rs.getDouble("BStartBal") * rs.getDouble("FBalDC")); } stm.setDouble(5,
	 * rs.getDouble("BDebit")); stm.setDouble(6, rs.getDouble("BCredit"));
	 * if(rs.getDouble("BEndBal") == 0){ stm.setString(7, "平"); stm.setDouble(8,
	 * 0); }else{ stm.setString(7, rs.getInt("FBalDC") == 1 ? "借" : "贷");
	 * stm.setDouble(8, rs.getDouble("BEndBal") * rs.getDouble("FBalDC")); }
	 * }else{//原币 if(rs.getDouble("StartBal") == 0){ stm.setString(3, "平");
	 * stm.setDouble(4, 0); }else{ stm.setString(3, rs.getInt("FBalDC") == 1 ?
	 * "借" : "贷"); stm.setDouble(4, rs.getDouble("StartBal") *
	 * rs.getDouble("FBalDC")); } stm.setDouble(5, rs.getDouble("Debit"));
	 * stm.setDouble(6, rs.getDouble("Credit")); if(rs.getDouble("EndBal") ==
	 * 0){ stm.setString(7, "平"); stm.setDouble(8, 0); }else{ stm.setString(7,
	 * rs.getInt("FBalDC") == 1 ? "借" : "贷"); stm.setDouble(8,
	 * rs.getDouble("EndBal") * rs.getDouble("FBalDC")); } } stm.addBatch(); }
	 * stm.executeBatch(); conn.commit();//提交事务 bTrans = false;
	 * conn.setAutoCommit(true);//设置为自动提交事务 return "1"; }catch (Exception e) {
	 * throw new YssException("处理持仓核对出错！", e); } finally {
	 * dbl.closeStatementFinal(stm); dbl.closeResultSetFinal(rs);
	 * dbl.endTransFinal(conn, bTrans); } }
	 * 
	 * public void parseRowStr(String sRowStr) throws YssException { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * 
	 * private int getThisSetAccLen(String FSetCode,int FYear) throws
	 * YssException { String tmp = ""; ResultSet Rs = null; String sql = ""; int
	 * YssStartMonth = 0; try{ sql =
	 * "select facclen,fstartmonth From lsetlist where fsetcode=" + FSetCode +
	 * " and fyear=" + FYear; Rs = dbl.openResultSet(sql); if (Rs.next()){ tmp =
	 * Rs.getString("facclen"); YssStartMonth = Rs.getInt("fstartmonth"); }
	 * Rs.getStatement().close(); Rs = null; }catch (SQLException ee){ }finally{
	 * try{ if (Rs != null) Rs.getStatement().close(); } catch (SQLException
	 * ex){} } return YssStartMonth; }
	 */

}
