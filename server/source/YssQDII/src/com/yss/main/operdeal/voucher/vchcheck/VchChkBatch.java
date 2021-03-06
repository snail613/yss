package com.yss.main.operdeal.voucher.vchcheck;

import java.sql.ResultSet;

import com.yss.log.SingleLogOper;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class VchChkBatch extends BaseVchCheck {

	
	public VchChkBatch(){}
	
	
	 public String doCheck() throws YssException {
		 ResultSet rs = null;
	        StringBuffer queryBuf = new StringBuffer();
	        String[] bookSets = null;
	        String reStr = "true";
	        StringBuffer chkInfo = new StringBuffer();
	        boolean startFlag = true;
	        boolean endFlag = false;
	        StringBuffer oldMsgType = new StringBuffer();
	        
	        //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	        java.util.Date logStartTime = new java.util.Date();
			if(logOper == null){//添加非空判断
				logOper = SingleLogOper.getInstance();
			}
	        String logInfo = "";
	        //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			try {
	            bookSets = getBookSet(sportCode); 
	            if(bookSets==null){
	            	//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	            	logInfo += "        组合【"+sportCode+"】的套帐代码不存在！\r\n";
	            	
	            	runStatus.appendRunDesc("VchRun",  "        组合【"+sportCode+"】的套帐代码不存在！\r\n");
	            	reStr="false";
	            	return reStr;
	            }
	            
	            for (int booksets = 0; booksets < bookSets.length; booksets++) {
	            	queryBuf.setLength(0);
	            	queryBuf = getSubjectChkSql(bookSets[booksets]);
	                rs = dbl.openResultSet(queryBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE);  
	                
                	//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                	logInfo += "      开始检查套帐【" + bookSets[booksets] + "】科目\r\n";
                	
	                if(!rs.next()){
	                	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	                	logInfo += "      开始检查科目是否在财务中存在... ...\r\n";
	                	logInfo += "      科目是否在财务中存在检查完成！\r\n";
	                	logInfo += "      开始检查凭证科目为非明细科目... ...\r\n";
	                	logInfo += "      凭证科目为非明细科目检查完成！\r\n";
	                	logInfo += "      开始检查财务系统科目的币种是否设置为明细币种... ...\r\n";
	                	logInfo += "      财务系统科目的币种是否设置为明细币种检查完成！\r\n";
	                	logInfo += "      开始检查凭证中科目币种与系统币种设置的匹配性... ...\r\n";
	                	logInfo += "      凭证中科目币种与系统币种设置的匹配性检查完成！\r\n";
	                	
	                	logInfo += "      套帐【" + bookSets[booksets] + "】科目检查完成\r\n";
	                	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	                	
	                	runStatus.appendRunDesc("VchRun", "      开始检查科目是否在财务中存在... ...");
	                	runStatus.appendRunDesc("VchRun", "      科目是否在财务中存在检查完成！\r\n");
	                	runStatus.appendRunDesc("VchRun", "      开始检查凭证科目为非明细科目... ...");
	                	runStatus.appendRunDesc("VchRun", "      凭证科目为非明细科目检查完成！\r\n");
	                	runStatus.appendRunDesc("VchRun", "      开始检查财务系统科目的币种是否设置为明细币种... ...");
	                	runStatus.appendRunDesc("VchRun", "      财务系统科目的币种是否设置为明细币种检查完成！\r\n");
	                	runStatus.appendRunDesc("VchRun", "      开始检查凭证中科目币种与系统币种设置的匹配性... ...");
	                	runStatus.appendRunDesc("VchRun", "      凭证中科目币种与系统币种设置的匹配性检查完成！\r\n");
	                	
	                	//---edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	                	continue;
	                	//return reStr;
	                	//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	                }
	                rs.beforeFirst();
	                while (rs.next()) {
	                	if(!rs.getString("fMsgtype").equalsIgnoreCase(oldMsgType.toString())&&oldMsgType.length()>0){
	                		endFlag = true;
	                	}
	                	
	                	chkInfo.setLength(0);
	                	if(rs.getString("fMsgtype").equalsIgnoreCase("1")&& startFlag){
	                		startFlag = false;
	                		//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	                		logInfo += "      开始检查科目是否在财务中存在... ...\r\n";
	                		
	                		runStatus.appendRunDesc("VchRun", "      开始检查科目是否在财务中存在... ...");
	                	}else if(rs.getString("fMsgtype").equalsIgnoreCase("2")&& startFlag){
	                		startFlag = false;
	                		//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	                		logInfo += "      开始检查凭证科目为非明细科目... ...\r\n";
	                		
	                		runStatus.appendRunDesc("VchRun", "      开始检查凭证科目为非明细科目... ...");
	                	}else if(rs.getString("fMsgtype").equalsIgnoreCase("3")&& startFlag){
	                		startFlag = false;
	                		//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	                		logInfo += "      开始检查财务系统科目的币种是否设置为明细币种... ...\r\n";
	                		
	                		runStatus.appendRunDesc("VchRun", "      开始检查财务系统科目的币种是否设置为明细币种... ...");
	                	}else if(rs.getString("fMsgtype").equalsIgnoreCase("4")&& startFlag){
	                		startFlag = false;
	                		//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	                		logInfo += "      开始检查凭证中科目币种与系统币种设置的匹配性... ...\r\n";
	                		
	                		runStatus.appendRunDesc("VchRun", "      开始检查凭证中科目币种与系统币种设置的匹配性... ...");
	                	}
	                	
	                	chkInfo.append("\t").append(rs.getString("fMsg")).append("\r\n");
	                    runStatus.appendRunDesc("VchRun", chkInfo.toString());
	                    reStr = "false";

	                    if(oldMsgType.toString().equalsIgnoreCase("1")&& endFlag){
	                		startFlag = true;
	                		//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	                		logInfo += "      科目是否在财务中存在检查完成！\r\n";
	                		
	                		runStatus.appendRunDesc("VchRun", "      科目是否在财务中存在检查完成！\r\n");
	                	}else if(oldMsgType.toString().equalsIgnoreCase("2")&& endFlag){
	                		startFlag = true;
	                		//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	                		logInfo += "      凭证科目为非明细科目检查完成！\r\n";
	                		
	                		runStatus.appendRunDesc("VchRun", "      凭证科目为非明细科目检查完成！\r\n");
	                	}else if(oldMsgType.toString().equalsIgnoreCase("3")&& endFlag){
	                		startFlag = true;
	                		//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	                		logInfo += "      财务系统科目的币种是否设置为明细币种检查完成！\r\n";
	                		
	                		runStatus.appendRunDesc("VchRun", "      财务系统科目的币种是否设置为明细币种检查完成！\r\n");
	                	}else if(oldMsgType.toString().equalsIgnoreCase("4")&& endFlag){
	                		startFlag = true;
	                		//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	                		logInfo += "      凭证中科目币种与系统币种设置的匹配性检查完成！\r\n";
	                		
	                		runStatus.appendRunDesc("VchRun", "      凭证中科目币种与系统币种设置的匹配性检查完成！\r\n");
	                	}
	                    oldMsgType.setLength(0);
	                    oldMsgType.append(rs.getString("fMsgtype"));
	                }
	                dbl.closeResultSetFinal(rs);
	                
                    //-add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    logInfo += "      套帐【" + bookSets[booksets] + "】科目检查完成\r\n";
	            }
	            
	            //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	            if(logSumCode.trim().length() > 0){
	        		//edit by songjie 2012.11.20 添加非空判断
	        		if(logOper != null){
	        			logOper.setDayFinishIData(this, 24, "凭证科目检查", pub, false, sportCode, 
	        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
	        					YssFun.toDate(this.endDate), logInfo, logStartTime, 
	        					logSumCode,new java.util.Date());
	        		}
	            }
				//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				
	            dbl.closeResultSetFinal(rs);
	            
	            return reStr;
	        } catch (Exception e) {
	        	//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	            runStatus.appendValRunDesc("凭证科目检查失败！" + e.getMessage());
	        	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	        	try{
	        		if(logSumCode.trim().length() > 0){
	            		//edit by songjie 2012.11.20 添加非空判断
	            		if(logOper != null){
	            			logOper.setDayFinishIData(this, 24, "凭证科目检查",pub, true, sportCode, 
	            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
	            					YssFun.toDate(this.endDate), 
	            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
	            					(logInfo + "\r\n凭证科目检查失败\r\n" + e.getMessage())//处理日志信息 除去特殊符号
	            					.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""), 
	            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
	            					logStartTime, logSumCode,new java.util.Date());
	            		}
	        		}
	        	}catch(Exception ex){
	        		ex.printStackTrace();
	        	}
	        	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
	        	finally{//添加 finally 保证可以抛出异常
	        		throw new YssException("检查财务系统科目的币种是否设置为明细币种！", e);
	        	}
	            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
	    }
	
	
	 
	/**
	 * 合并财务系统科目设置找不到对应科目检查、
     * 凭证科目为最明细科目检查、
     * 财务系统科目的币种是否设置为明细币种检查、
     * 科目币种与系统币种设置的匹配性检查
	 * @param bookSet
	 * @return
	 * @throws YssException
	 */
	private StringBuffer getSubjectChkSql(String bookSet) throws YssException{
		
		    StringBuffer chkQueryBuf = new StringBuffer();

		    
			chkQueryBuf.append(" select * from ");
			chkQueryBuf.append(" (select case  ");
			chkQueryBuf.append(" when acc.facctcode is null then ");
			chkQueryBuf.append(" '凭证编号为【'||FVchNum||'】,凭证模板名称为【'||tpl.fvchtplname||'】,凭证分录编号为【'||fentitynum||'】的科目代码");
			chkQueryBuf.append("【'||fsubjectcode||'】在财务系统科目设置中找不到对应的科目,请设置相应的科目!' ");
			chkQueryBuf.append(" when acc.facctdetail <>'1' then ");
			chkQueryBuf.append(" '凭证编号为【'||FVchNum||'】,凭证模板名称为【'||tpl.fvchtplname||'】,凭证分录编号为【'||fentitynum||'】的科目代码");
			chkQueryBuf.append("【'||fsubjectcode||'】的凭证科目为非明细科目！' ");
			chkQueryBuf.append(" when acc.fcurcode='***' then ");
			chkQueryBuf.append(" '凭证编号为【'||FVchNum||'】,凭证模板名称为【'||tpl.fvchtplname||'】,凭证分录编号为【'||fentitynum||'】的科目代码");
			chkQueryBuf.append("【'||fsubjectcode||'】的币种为“***”，没有设置为最明细币种！' ");
			chkQueryBuf.append(" when cur.fcurycode is null then ");
			chkQueryBuf.append(" '凭证编号为【'||FVchNum||'】,凭证模板名称为【'||tpl.fvchtplname||'】,凭证分录编号为【'||fentitynum||'】的科目代码");
			chkQueryBuf.append("【'||fsubjectcode||'】的凭证科目设置币种出错！' ");
			chkQueryBuf.append(" else ' ' end as fMsg ,");
			chkQueryBuf.append(" case when acc.facctcode is null then '1' ");
			chkQueryBuf.append(" when acc.facctdetail <>'1' then '2' ");
			chkQueryBuf.append(" when acc.fcurcode='***' then '3' ");
			chkQueryBuf.append(" when cur.fcurycode is null then '4' ");
			chkQueryBuf.append(" else '0' end as fMsgtype from ");
			/**
		     * 以估值系统凭证数据为主表关联财务系统科目表。
             * 分别对科目匹配情况、科目最明细字段，科目币种字段、
             * 科目币种字段的匹配情况来做检查。
		     */
			chkQueryBuf.append(" (select a2.* ,a1.FVCHTPLCODE from  ");
			chkQueryBuf.append(" (select FVchNum,FVCHTPLCODE from ").append(pub.yssGetTableName("Tb_vch_data"));
			chkQueryBuf.append(" where fportcode=").append(dbl.sqlString(sportCode)).append(" and fvchdate between ").append(dbl.sqlDate(this.beginDate));
			chkQueryBuf.append(" and ").append(dbl.sqlDate(this.endDate));
			chkQueryBuf.append(" and fvchtplcode in(").append(operSql.sqlCodes(this.isInData ?  getVchTpls() : this.vchTypes)).append(" ))a1 ");
			chkQueryBuf.append(" join ");
			chkQueryBuf.append(" (select FVchNum,fentitynum,fsubjectcode from ").append(pub.yssGetTableName("tb_vch_dataentity")).append(")a2 ");
			chkQueryBuf.append(" on a1.FVchNum = a2.FVchNum )vch ");
			chkQueryBuf.append(" left join  ");
			chkQueryBuf.append(" (select facctcode,facctdetail,fcurcode from ").append(getCwTab(bookSet)).append(" )acc ");
			chkQueryBuf.append(" on vch.fsubjectcode = acc.facctcode ");
			chkQueryBuf.append(" left join ");
			chkQueryBuf.append(" (select fcurycode from ").append(pub.yssGetTableName("tb_para_currency")).append(" where fcheckstate=1)cur ");
			chkQueryBuf.append(" on acc.fcurcode = cur.fcurycode ");
			chkQueryBuf.append(" left join ");
			chkQueryBuf.append(" (select fvchtplcode,fvchtplname from ").append(pub.yssGetTableName("tb_vch_vchtpl")).append(" where fcheckstate=1)tpl ");
			chkQueryBuf.append(" on vch.fvchtplcode = tpl.fvchtplcode) chk ");
			chkQueryBuf.append(" where fMsg<>' ' order by fMsgtype");
			
			return chkQueryBuf;
		
		
	}
	
	private String getCwTab(String sSetCode) throws YssException {

		StringBuffer cwTabBuf = new StringBuffer();
		int len = 0;
		cwTabBuf.append("A");
		cwTabBuf.append(YssFun.formatDate(YssFun.parseDate(this.beginDate),
				"yyyy"));

		len = 3 - sSetCode.length();

		for (int i = 0; i < len; i++) {
			cwTabBuf.append("0");
		}
		cwTabBuf.append(sSetCode).append("laccount");

		return cwTabBuf.toString();
	}
	
	
}
