package com.yss.main.operdeal.voucher.vchcheck;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.yss.util.*;
import com.yss.vsub.YssFinance;
import com.yss.log.SingleLogOper;
import com.yss.main.operdeal.voucher.vchbuild.*;
import com.yss.main.voucher.VchDataBean;
import com.yss.main.voucher.VchDataEntityBean;
import com.yss.manager.VoucherAdmin;
import com.yss.pojo.cache.YssWhereCond;

/**
 * 20120401 added by liubo.Story #2192
 * 检查科目分录辅助核算项设置
 * 
 */
public class VchChkAuxiAcc
    extends BaseVchCheck {

    
    public VchChkAuxiAcc() {
    	
    }

    String reStr = "true";


	public String doCheck() throws YssException {
		String[] bookSets = null;

		//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
		java.util.Date logStartTime = new java.util.Date();
		if(logOper == null){//添加非空判断
			logOper = SingleLogOper.getInstance();
		}
		String logInfo = "";
		//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		try {
			//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
			logInfo += "      开始检查科目辅助核算信息设置... ...\r\n";
			
			runStatus.appendRunDesc("VchRun", "      开始检查科目辅助核算信息设置... ...");
			bookSets = getBookSet(sportCode);
			if (bookSets == null) {
				runStatus.appendRunDesc("VchRun", "        组合【" + sportCode + "】的套帐代码不存在！\r\n");
				reStr = "false";
				if(logSumCode.trim().length() > 0){
				//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					logInfo += "        组合【" + sportCode + "】的套帐代码不存在！\r\n";
	        		//edit by songjie 2012.11.20 添加非空判断
	        		if(logOper != null){
	        			logOper.setDayFinishIData(this, 24, "检查科目辅助核算信息设置", pub, false, " ", 
	        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
	        					YssFun.toDate(this.endDate), logInfo, logStartTime, 
								logSumCode, new java.util.Date());
	        		}
				}
				//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				
				return reStr;
			}
			for (int booksets = 0; booksets < bookSets.length; booksets++) {
				//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
				check(bookSets[booksets], logInfo);
			}

			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			if(logSumCode.trim().length() > 0){
				logInfo += "      检查科目辅助核算信息设置完成\r\n";
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, 24, "检查科目辅助核算信息设置", pub, false, sportCode, 
        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
        					YssFun.toDate(this.endDate), logInfo, logStartTime, 
        					logSumCode, new java.util.Date());
        		}
			}
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			
			return reStr;
		} catch (Exception e) {
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			try{
				if(logSumCode.trim().length() > 0){
	        		//edit by songjie 2012.11.20 添加非空判断
	        		if(logOper != null){
	        			logOper.setDayFinishIData(this, 24, "检查科目辅助核算信息设置", pub, true, sportCode, 
	        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
	        					YssFun.toDate(this.endDate), 
	        					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
	        					(logInfo + "\r\n检查科目辅助核算信息设置出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
	        					.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""), 
	        					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
	        					logStartTime, logSumCode, new java.util.Date());
	        		}
				}
				runStatus.appendRunDesc("VchRun", "检查失败！" + e.getMessage());
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
			finally{//添加 finally 保证可以抛出异常
				throw new YssException("检查凭证辅助核算项出错！", e);
			}
			//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
		}
	}
    
    
    
    /**
     *  生成辅助核算项检查的SQL语句
     *  @author jsc 20120713
     * @throws YssException 
     */
    private  String buildCheckSql(String sSetCode) throws YssException{
    	
    	StringBuffer chkBuf = new StringBuffer();
    
    	// 凭证模板  ---   join ---凭证 主表  --- join ---  凭证数据子表  ---join --- 科目表
    	
    	chkBuf.append(" select case  ");
    	chkBuf.append(" when c.fassistant is null then '【凭证模板:'||a.fvchtplcode||'-'||a.fvchtplname||'】,【分录：'||c.fentitynum||'】,【科目：'||c.fsubjectcode||");
    	chkBuf.append("'】中未设置辅助核算项;财务系统中所对应的【科目代码：'||d.facctcode||'】设置有【辅助核算项：'||d.fauxiacc||'】'");
    	chkBuf.append(" when d.fauxiacc is null then '【凭证模板:'||a.fvchtplcode||'-'||a.fvchtplname||'】,【【分录：'||c.fentitynum||'】,【科目：'||c.fsubjectcode||");
    	chkBuf.append("'】中设置有【辅助核算项'||c.fassistant||';财务系统中所对应的【科目代码：'||d.facctcode||'】未设置辅助核算项'");
    	chkBuf.append("else ");
    	chkBuf.append(" '【凭证模板:' || a.fvchtplcode || '-' || a.fvchtplname || '】,【【分录：' ||");
    	chkBuf.append( " c.fentitynum  || '】,【科目：' || c.fsubjectcode || '】中设置有【辅助核算项' ||");
    	chkBuf.append(" c.fassistant || ';财务系统中所对应的【科目代码：' || d.facctcode || '】辅助核算项不一致'");//add dongqingsong bug 83018
    	chkBuf.append(" end as chkMsg ");
    	chkBuf.append(" from ");
    	chkBuf.append(" (select fvchtplcode,fvchtplname,fattrcode from ").append(pub.yssGetTableName("tb_vch_vchtpl"));
        chkBuf.append(" where FCheckState = 1 and (FPortCode=").append(dbl.sqlString(sportCode));
        chkBuf.append("or (FPortCode is null or FPortCode='' or FPortCode=' '))");
        if (!this.strInvokeType.equals("FrmVchCheck")){
        	//凭证模板代码
        	chkBuf.append(" and FVchTplCode in (").append(operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes)).append(")) a");
        }else{
        	//凭证属性 or 凭证模板代码
        	chkBuf.append(" and (FATTRCODE in (").append(operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes)).append(")");
        	chkBuf.append(" or FVchTplCode in (").append(operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes)).append("))) a");
        }
        chkBuf.append(" join ");
        chkBuf.append(" ( select fvchnum,fvchtplcode from  ").append(pub.yssGetTableName("tb_vch_data"));
        chkBuf.append(" where fvchdate between ").append(dbl.sqlDate(beginDate)).append(" and ").append(dbl.sqlDate(endDate));
        chkBuf.append(" and fportcode = ").append(dbl.sqlString(sportCode)).append(")b");
        chkBuf.append(" on a.fvchtplcode = b.fvchtplcode");
        chkBuf.append(" join");
        chkBuf.append(" ( select fvchnum, fsubjectcode,trim(fassistant) as fassistant, case  when length(fentitynum)>5 then substr(fentitynum, 5, 2)   else fentitynum    end as fentitynum from ").append(pub.yssGetTableName("tb_vch_dataentity")).append(")c");//add dongqingsong bug 83018
        chkBuf.append(" on b.fvchnum = c.fvchnum");
        chkBuf.append(" join ");
        chkBuf.append(" (select facctcode,trim(fauxiacc) as fauxiacc from ").append(getCwTab(sSetCode));
        chkBuf.append(" where facctdetail=1 )d");
        chkBuf.append(" on c.fsubjectcode= d.facctcode ");
        chkBuf.append(" where ((c.fassistant is null and d.fauxiacc is not null) or (d.fauxiacc is null and c.fassistant is not null) or substr(c.fassistant,0,2) <> d.fauxiacc) "); //add dongqingsong bug 83018
    	return chkBuf.toString();
    }
    
    private  String getCwTab(String sSetCode) throws YssException {
    	
    	StringBuffer cwTabBuf = new StringBuffer();
    	int len =0;
    	cwTabBuf.append("A");
    	cwTabBuf.append(YssFun.formatDate(YssFun.parseDate(this.beginDate), "yyyy"));
    	
    	len = 3-sSetCode.length();
    	
    	for(int i=0;i<len;i++){
    		cwTabBuf.append("0");
    	}
    	cwTabBuf.append(sSetCode).append("laccount");
    	
    	return cwTabBuf.toString();
    }
    
	//edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logInfo
    private  void check(String sSetCode, String logInfo)throws YssException{
    	
    	String chkQuery = "";
    	ResultSet rs = null;
    	try{
    		
    		chkQuery = buildCheckSql(sSetCode);
    		rs = dbl.openResultSet(chkQuery);
    		
    		while(rs.next()){
    			reStr = "false";
    			
    			//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    			logInfo += rs.getString("chkMsg") + "\r\n";
    			
    			runStatus.appendRunDesc("VchRun", rs.getString("chkMsg"));
    			/**add---shashijie 2013-3-30 BUG 7343 调度方案也需要提示错误状态信息*/
    			runStatus.appendSchRunDesc(rs.getString("chkMsg") + "\r\n");
				/**end---shashijie 2013-3-30 BUG 7343*/
    		}
    		
    	}catch(Exception e){
    		throw new YssException(e.getMessage());
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /*  delete by jsc 20120713
    public String doCheck() throws YssException {
        ResultSet rs = null;
        ResultSet rsDs = null;
        String sqlStr = "";
        String[] bookSets = null;
        try {
            runStatus.appendRunDesc("VchRun", "      开始检查科目辅助核算信息设置... ...");
            bookSets = getBookSet(sportCode);
            if(bookSets==null){
            	runStatus.appendRunDesc("VchRun",  "        组合【"+sportCode+"】的套帐代码不存在！"+"\r\n");
            	reStr="false";
            	return reStr;
            }
            for (int booksets = 0; booksets < bookSets.length; booksets++) {
            	if (!this.strInvokeType.equals("FrmVchCheck"))
            	{
	            	sqlStr =
	                    " select a.* from " +
	                    pub.yssGetTableName("Tb_Vch_VchTpl") + " a" +
	                    " where a.FCheckState = 1 " +
	                    " and a.FVchTplCode in (" + operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes) + ")" +
	                    " and (a.FPortCode=" + dbl.sqlString(sportCode) +
	                    " or (a.FPortCode is null or a.FPortCode='' or a.FPortCode=' '))" +
	                    " order by a.FVCHTPLCODE";
            	}
            	else
            	{
            		sqlStr =
                    " select a.* from " +
                    pub.yssGetTableName("Tb_Vch_VchTpl") + " a" +
                    " where a.FCheckState = 1 " +
                    " and (a.FATTRCODE in (" + operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes) + ") or a.FVchTplCode in (" + operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes) + "))" +
                    " and (a.FPortCode=" + dbl.sqlString(sportCode) +
                    " or (a.FPortCode is null or a.FPortCode='' or a.FPortCode=' '))" +
                    " order by a.FVCHTPLCODE";
            		
            	}
            	rs = dbl.queryByPreparedStatement(sqlStr);
                
                while (rs.next()) { 
                    doMultiVch(rs.getString("FVCHTPLCODE"), bookSets[booksets]);
                }
                dbl.closeResultSetFinal(rs);
            }
            runStatus.appendRunDesc("VchRun", "      科目辅助核算信息设置检查完成！\r\n");
            return reStr;
        } catch (Exception e) {
            runStatus.appendRunDesc("VchRun", "检查失败！" + e.getMessage());
            throw new YssException("检查凭证辅助核算项出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs,rsDs);
        }
    }
    private void doMultiVch(String sVchTplCode,  String sBookSetCode) throws YssException
    {
        ResultSet rs = null;
        String strSql = "";
        String sAcctCode = "";
        String sAuxiAccCode = "";
        try {
	            strSql = "select  x.*, y.fentityname from " +
						 " (select b.fvchtplcode, a.* " +
						 " from " + pub.yssGetTableName("tb_vch_dataentity") + " a " +
						 " left join " + pub.yssGetTableName("tb_vch_data") + " b on a.fvchnum = b.fvchnum " +
						 " where b.fvchdate between " + dbl.sqlDate(beginDate) + " and " + dbl.sqlDate(endDate) +
						 " and b.fvchtplcode = " + dbl.sqlString(sVchTplCode) + " and b.fportcode = " + dbl.sqlString(sportCode) + ") x " +
						 " left join " + pub.yssGetTableName("tb_vch_entity") + " y on x.fvchtplcode = y.fvchtplcode and x.fentitynum = trim(to_char(y.fentitycode,'000000')) " +
						 " order by x.fvchnum, x.fvchtplcode, x.fentitynum";
	            rs = dbl.queryByPreparedStatement(strSql); 

             		while (rs.next())
             		{
		                sAcctCode = rs.getString("FSUBJECTCODE");
		                sAuxiAccCode = getAuxiAccFromFundacc(sBookSetCode,sAcctCode);
		                if((rs.getString("FASSISTANT") == null || rs.getString("FASSISTANT").trim().equals("")) && sAuxiAccCode.trim().equals(""))
		                {
		                            		
		                }
		                else if((rs.getString("FASSISTANT") != null && !rs.getString("FASSISTANT").trim().equals("")) && !sAuxiAccCode.trim().equals(""))
		                {
		                            		
		                }
		                else if((rs.getString("FASSISTANT") == null || rs.getString("FASSISTANT").trim().equals("")) && !sAuxiAccCode.trim().equals(""))
		                {
		                	runStatus.appendRunDesc("VchRun", "发现未设置辅助核算信息：" +
			                    "凭证模板（" + sVchTplCode + "），分录（"+ rs.getString("FENTITYNUM") +"-" + rs.getString("FENTITYNAME") + "），" +
			                    "科目（" + rs.getString("FSUBJECTCODE") + "）中未设置辅助核算信息；" +
			                    "财务系统中所对应的科目代码（" + sAcctCode + "）设置有辅助核算项（" + sAuxiAccCode + "）\r\n");
		                	reStr = "false";
		                }
		                else if((rs.getString("FASSISTANT") != null && !rs.getString("FASSISTANT").trim().equals("")) && sAuxiAccCode.trim().equals(""))
		                {
		                    runStatus.appendRunDesc("VchRun", "发现未设置辅助核算信息：" +
		                        "凭证模板（" + sVchTplCode + "），分录（"+ rs.getString("FENTITYNUM") +"-" + rs.getString("FENTITYNAME") + "），" +
		                        "科目（" + rs.getString("FSUBJECTCODE") + "）中设置有辅助核算信息（" + rs.getString("FASSISTANT") + "）" +
		                        "财务系统中所对应的科目代码（" + sAcctCode + "）未设置辅助核算项\r\n");
		                	reStr = "false";
		                }
                            
            }
        }catch (Exception e) {
            throw new YssException("检查科目辅助核算项设置出现错误：" + "\n", e); 
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    
    }

    
    private String getAuxiAccFromFundacc(String sBookSet, String sAcctCode) throws YssException
    {
    	String sReturn = "";
    	String strSql = "";
    	ResultSet rs = null;
    	
    	try
    	{
	    	strSql = "select * from A" + YssFun.formatDate(YssFun.parseDate(this.beginDate), "yyyy") + sBookSet +"LAccount where FAcctCode = " + dbl.sqlString(sAcctCode);
	    	rs = dbl.queryByPreparedStatement(strSql);
	    	
	    	while(rs.next())
	    	{
	    		sReturn = rs.getString("FAuxiAcc");
	    	}
    	}
    	catch(Exception ye)
    	{
    		throw new YssException("获取财务系统的科目表明细出错：" + ye.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    	return sReturn;
    }
    */
}
