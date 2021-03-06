package com.yss.main.operdeal.voucher.vchcheck;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

import com.yss.log.SingleLogOper;
import com.yss.main.operdeal.voucher.vchbuild.BaseVchBuild;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
*
* <p>Title: VchChkDicSet</p>
* <p>Description: 390 QDV4赢时胜（上海）2010年12月08日02_A 检查凭证字典对应关系是否设置</p>
* <p>Copyright: Copyright (c) 2006</p>
* <p>Company: </p>
* @author qiuxufeng
* @version 1.0
*/
public class VchChkDicSet
	extends BaseVchCheck {

    HashMap hmDsData = null; //数据源字段数据
    HashMap hmDictData = null; //字典所有数据
    
	public VchChkDicSet() {
	}
	
	public String doCheck() throws YssException {
        ResultSet rs = null;
        ResultSet vchrs = null; 
        String sqlStr = "";
        String[] bookSets = null;//套账代码
        String reStr = "true";
        
        //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        java.util.Date logStartTime = new java.util.Date();
		if(logOper == null){//添加非空判断
			logOper = SingleLogOper.getInstance();
		}
        String logInfo = "";
        //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
			//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
			logInfo += "      开始检查凭证字典对应关系是否设置... ...\r\n";
        	
            runStatus.appendRunDesc("VchRun", "      开始检查凭证字典对应关系是否设置... ...");
            bookSets = this.getBookSet(sportCode);//通过组合获取套账代码
            if(bookSets==null){
    			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	if(logSumCode.trim().length() > 0){
            		logInfo += "        组合【"+sportCode+"】的套帐代码不存在！\r\n";
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查凭证字典对应关系是否设置", pub, false, this.sportCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), logInfo, 
            					logStartTime, logSumCode, new java.util.Date());
            		}
            	}
				//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				
            	runStatus.appendRunDesc("VchRun",  "        组合【"+sportCode+"】的套帐代码不存在！\r\n");
            	reStr = "false";
            	return reStr;
            }
            
            getDsData(); // 获取要处理的所有模板数据源数据
            getDictData(); // 获取字典设置数据
            
//        	for (int booksets = 0; booksets < bookSets.length; booksets++) {
                
            sqlStr = "select dat.FVchNum, dat.FVchTplCode, tpl.FVchTplName, tpl.fDsCode, ent.FEntityCode,ent.FEntityName," +
            		 //edit by songjie 2013.01.21 添加字段 FResumeDesc,FAssistantDesc,FResumeDDesc,FAssistantDDesc
                	 " ent.FSubjectCode, ent.fresumedesc, ent.fassistantdesc, dstf.FDesc, dstf2.Fdesc as FResumeDDesc, " + 
                	 " dstf3.Fdesc as FAssistantDDesc, entsub.FSubjectField, entsub.FSubjectDict, " + 
                	 //---edit by songjie 2013.01.21 修改凭证字典检查逻辑 start---//
                	 " entsub2.FResumeField, entsub2.FResumeDict, entsub3.FAssistantField, entsub3.FAssistantDict, " +
                	 " dic.FDictName as SubjectDictName, dic2.FDictName as ResumeDictName, dic3.FDictName as assistantDictName from" +
                	 //---edit by songjie 2013.01.21 修改凭证字典检查逻辑 start---//
                	 " (select FVchNum, FVchTplCode from " + pub.yssGetTableName("Tb_Vch_Data") +
                	 " where FVchDate between " + dbl.sqlDate(this.beginDate) +
                	 " and " + dbl.sqlDate(this.endDate) +
                	 " and FPortCode = " + dbl.sqlString(sportCode) + // add by qiuxufeng 20110309 增加组合代码条件
                	 " and FVchTplCode in (" + operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes) + ")) dat" +
                	 " left join (select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") + 
                	 " where FCheckState = 1) tpl" +
                	 " on dat.FVchTplCode = tpl.FVchTplCode" +
                	 " left join (select * from " + pub.yssGetTableName("Tb_Vch_Entity") + 
                	 " where FCheckState = 1) ent" +
                	 " on tpl.FVchTplCode = ent.FVchTplCode" +
                	 " join (select * from " + pub.yssGetTableName("Tb_Vch_EntitySubject") + 
                	 " where FValueType = 0 and FCheckState = 1 and FSubjectDict <> ' ') entsub" +
                	 " on ent.FVchTplCode = entsub.FVchTplCode and ent.FEntityCode = entsub.FEntityCode" +
                	 //---add by songjie 2013.01.21 修改凭证字典检查逻辑 start---//
                	 " left join (select * from " + pub.yssGetTableName("Tb_Vch_EntityResume") + 
                	 " where FValueType = 0 and FCheckState = 1 and Fresumedict <> ' ') entsub2 " + 
                	 " on ent.FVchTplCode = entsub2.FVchTplCode and ent.FEntityCode = entsub2.FEntityCode " + 
                	 " left join (select * from " + pub.yssGetTableName("Tb_Vch_Assistant") + 
                	 " where FValueType = 0 and FCheckState = 1 and FASSISTANTDICT <> ' ') entsub3 " +
                	 " on ent.FVchTplCode = entsub3.FVchTplCode and ent.FEntityCode = entsub3.FEntityCode " + 
                	 //---add by songjie 2013.01.21 修改凭证字典检查逻辑 end---//
//                				" on ent.FVchTplCode = entsub.FVchTplCode " +
                	 " left join (select * from " + pub.yssGetTableName("Tb_Vch_DsTabField") + 
                	 " where FCheckState = 1) dstf" +
                	 " on tpl.FDsCode = dstf.FVchDsCode and dstf.FAliasName = entsub.FSubjectField" +
                	 //---add by songjie 2013.01.21 修改凭证字典检查逻辑 start---//
                	 " left join (select * from " + pub.yssGetTableName("Tb_Vch_DsTabField") + 
                	 " where FCheckState = 1) dstf2 " + 
                	 " on tpl.FDsCode = dstf2.FVchDsCode and dstf2.FAliasName = entsub2.FResumeField " +
                	 " left join (select * from " + pub.yssGetTableName("Tb_Vch_DsTabField") + 
                	 " where FCheckState = 1) dstf3 " +
                	 " on tpl.FDsCode = dstf3.FVchDsCode and dstf3.FAliasName = entsub3.fassistantfield " + 
                	 //---add by songjie 2013.01.21 修改凭证字典检查逻辑 end---//
                	 " left join (select distinct FDictCode, FDictName from " + pub.yssGetTableName("Tb_Vch_Dict") + 
                	 " where FCheckState = 1) dic " +
                	 " on entsub.FSubjectDict = dic.FDictCode" +
                	 //---add by songjie 2013.01.21 修改凭证字典检查逻辑 start---//
                	 " left join (select distinct FDictCode, FDictName from " + pub.yssGetTableName("Tb_Vch_Dict") + 
                	 " where FCheckState = 1) dic2 " + 
                	 " on entsub2.FResumeDict = dic2.FDictCode " + 
                	 " left join (select distinct FDictCode, FDictName from " + pub.yssGetTableName("Tb_Vch_Dict") + 
                	 " where FCheckState = 1) dic3 " + 
                	 " on entsub3.fassistantdict = dic3.FDictCode " + 
                	 //---add by songjie 2013.01.21 修改凭证字典检查逻辑 end---//
                	 " order by FVchNum, FVchTplCode, FEntityCode";
            rs = dbl.queryByPreparedStatement(sqlStr);//modify by fangjiang 2011.08.14 STORY #788
                
            int iErr = 0;
            String strSubField = "";
            //---add by songjie 2013.01.21 修改凭证字典检查逻辑 start---//
            String strResumeField = "";
            String strAssistantField = "";
            String[] resumeAry = null;
            String[] assistantAry = null;
            //---add by songjie 2013.01.21 修改凭证字典检查逻辑 end---//
            String sRunStatus = "";
            String[] subFieldAry = null;
                //HashSet hs = new HashSet();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            while(rs.next()) {

				//BUG3313建行年终结转，在生成分红业务凭证时，提示未在凭证字典设置对应关系  add by jiangshichao 2011.12.05
            	strSubField = getSubjectField(rs.getString("FVchTplCode").trim(), rs.getString("FSubjectField").trim()); // 获取标识代码
            	//---add by songjie 2013.01.21 修改凭证字典检查逻辑 start---//
            	if(rs.getString("FResumeField") != null){
            		strResumeField = getSubjectField(rs.getString("FVchTplCode").trim(), rs.getString("FResumeField").trim());
            	}
            	if(rs.getString("FAssistantField") != null){
            		strAssistantField = getSubjectField(rs.getString("FVchTplCode").trim(), rs.getString("FAssistantField").trim());
            	}
            	//---add by songjie 2013.01.21 修改凭证字典检查逻辑 end---//
//                if(strSubField.trim().length() == 0) {
                		//获取标识代码为空
//                		if(!hs.contains(rs.getString("FVchTplCode"))) {
//                			runStatus.appendRunDesc("VchRun", "        `模版" + rs.getString("FVchTplCode") + "的数据源获取记录为空\r\n");
//                			hs.add(rs.getString("FVchTplCode"));
//                		}
//                		reStr = "false";
//                		continue;
//                }
                subFieldAry = strSubField.split(",");
                for (int i = 0; i < subFieldAry.length; i++) {
	                //20111026 modified by liubo.Story #1456.当该凭证分录在生成的分录数据表中不存在时，该分录不进行提示
	                if(!existCnvContent(rs.getString("FSubjectDict"), subFieldAry[i]) && 
	                	existDataEntity(rs.getString("FVchNum"), rs.getString("fDsCode"), rs.getString("FEntityCode"), rs.getString("FVchTplCode"))) {
	                    if(iErr == 0) {
	                    	logInfo += "         发现未设置字典对应关系：\r\n";
	                    	
	                    	runStatus.appendRunDesc("VchRun", "        发现未设置字典对应关系："); //在凭证方案执行显示状态
	                    	runStatus.appendRunDesc("SchRun", "\r\n        发现未设置字典对应关系："); //在调度方案显示状态
	                    }
	                    
		                sRunStatus = "        凭证编号" + rs.getString("FVchNum") +
				                     " 模版（" + rs.getString("FVchTplCode") + "-" +  rs.getString("FVchTplName") + // 凭证模板代码-凭证模板名称
				                     "）、分录（" + rs.getString("FEntityCode") + "-" + rs.getString("FEntityName") +  // 分录代码-分录名称
				                     "）、科目(" +  rs.getString("FSubjectCode").replaceAll("\r\n", "") + ")中" + // 科目内容__<交易所币种>__科目字段
				                     (null == rs.getString("FDesc") ? "_" : "<" + rs.getString("FDesc").trim() + ">" + subFieldAry[i]) + // <交易所币种>US||USD
				                     //edit by songjie 2013.01.21 SubjectDict 改为 SubjectDictName
				                     "未在凭证字典（" + rs.getString("FSubjectDict") + "-" + rs.getString("SubjectDictName") + // 凭证字典代码-凭证字典名称
				                     "）中设置对应关系 ！";
		                	
		                //add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
		                logInfo += sRunStatus + "\r\n";
		                	
		            	runStatus.appendRunDesc("VchRun", sRunStatus + "\r\n"); //在凭证方案执行显示状态
		            	runStatus.appendRunDesc("SchRun", "\r\n" + sRunStatus); //在调度方案显示状态
		            	
		            	iErr++;
		            	reStr = "false";
	                }
                }
                
                //--- add by songjie 2013.01.21 修改凭证字典检查逻辑 start---//
                if(strResumeField != null && strResumeField.trim().length() > 0){
                    resumeAry = strResumeField.split(",");
                    for (int i = 0; i < resumeAry.length; i++) {
    	                //20111026 modified by liubo.Story #1456.当该凭证分录在生成的分录数据表中不存在时，该分录不进行提示
    	                if(rs.getString("FResumeDict") != null && !rs.getString("FResumeDict").equals("null") &&
    	                	!existCnvContent(rs.getString("FResumeDict"), resumeAry[i]) && 
    	                	existDataResume(rs.getString("FVchNum"), rs.getString("fDsCode"), rs.getString("FEntityCode"), rs.getString("FVchTplCode"))) {

    	                    if(iErr == 0) {
    	                    	logInfo += "         发现未设置字典对应关系：\r\n";
    	                    	
    	                    	runStatus.appendRunDesc("VchRun", "        发现未设置字典对应关系："); //在凭证方案执行显示状态
    	                    	runStatus.appendRunDesc("SchRun", "\r\n        发现未设置字典对应关系："); //在调度方案显示状态
    	                    }
    	                	
    		                sRunStatus = "        凭证编号" + rs.getString("FVchNum") +
    				                     " 模版（" + rs.getString("FVchTplCode") + "-" +  rs.getString("FVchTplName") + // 凭证模板代码-凭证模板名称
    				                     "）、分录（" + rs.getString("FEntityCode") + "-" + rs.getString("FEntityName") +  // 分录代码-分录名称
    				                     "）、摘要(" +  rs.getString("FResumeDesc").replaceAll("\r\n", "") + ")中" + // 摘要内容
    				                     (null == rs.getString("FResumeDDesc") ? "_" : "<" + rs.getString("FResumeDDesc").trim() + ">" + resumeAry[i]) + // <交易所币种>US||USD
    				                     "未在凭证字典（" + rs.getString("FResumeDict") + 
    				                     (rs.getString("ResumeDictName") == null ? "" : ("-" + rs.getString("ResumeDictName")))
    				                      + // 凭证字典代码-凭证字典名称
    				                     "）中设置对应关系 ！";
    		                	
    		                //add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    		                logInfo += sRunStatus + "\r\n";
    		                	
    		            	runStatus.appendRunDesc("VchRun", sRunStatus + "\r\n"); //在凭证方案执行显示状态
    		            	runStatus.appendRunDesc("SchRun", "\r\n" + sRunStatus); //在调度方案显示状态
    		            	
    		            	iErr++;
    		            	reStr = "false";
    	                }
                    }
                }
                
                if(strAssistantField != null && strAssistantField.trim().length() > 0){
                    assistantAry = strAssistantField.split(",");
                    for (int i = 0; i < assistantAry.length; i++) {
    	                //20111026 modified by liubo.Story #1456.当该凭证分录在生成的分录数据表中不存在时，该分录不进行提示
    	                if(rs.getString("FAssistantDict") != null && !rs.getString("FAssistantDict").equals("null") &&
    	                   !existCnvContent(rs.getString("FAssistantDict"), assistantAry[i]) && 
    	                    existDataAssistant(rs.getString("FVchNum"), rs.getString("fDsCode"), 
    	                    rs.getString("FEntityCode"), rs.getString("FVchTplCode"))) {

    	                    if(iErr == 0) {
    	                    	logInfo += "         发现未设置字典对应关系：\r\n";
    	                    	
    	                    	runStatus.appendRunDesc("VchRun", "        发现未设置字典对应关系："); //在凭证方案执行显示状态
    	                    	runStatus.appendRunDesc("SchRun", "\r\n        发现未设置字典对应关系："); //在调度方案显示状态
    	                    }    	                	
    	                	
    		                sRunStatus = "        凭证编号" + rs.getString("FVchNum") +
    				                     " 模版（" + rs.getString("FVchTplCode") + "-" +  rs.getString("FVchTplName") + // 凭证模板代码-凭证模板名称
    				                     "）、分录（" + rs.getString("FEntityCode") + "-" + rs.getString("FEntityName") +  // 分录代码-分录名称
    				                     "）、辅助核算(" +  rs.getString("FAssistantDesc").replaceAll("\r\n", "") + ")中" + // 科目内容__<交易所币种>__科目字段
    				                     (null == rs.getString("FAssistantDDesc") ? "_" : "<" + rs.getString("FAssistantDDesc").trim() + ">" + assistantAry[i]) + // <交易所币种>US||USD
    				                     "未在凭证字典（" + rs.getString("FAssistantDict") + 
    				                     (rs.getString("AssistantDictName") == null ? "" : "-" + rs.getString("AssistantDictName") ) +
    				                     // 凭证字典代码-凭证字典名称
    				                     "）中设置对应关系 ！";
    		                	
    		                //add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    		                logInfo += sRunStatus + "\r\n";
    		                	
    		            	runStatus.appendRunDesc("VchRun", sRunStatus + "\r\n"); //在凭证方案执行显示状态
    		            	runStatus.appendRunDesc("SchRun", "\r\n" + sRunStatus); //在调度方案显示状态
    		            	
    		            	iErr++;
    		            	reStr = "false";
    	                }
                    }
                }

                //--- add by songjie 2013.01.21 修改凭证字典检查逻辑 start---//
                
                
            }
//        	}
                
            //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            if(logSumCode.trim().length() > 0){
            	logInfo += "      检查凭证字典对应关系是否设置完成！\r\n";
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, 24, "检查凭证字典对应关系是否设置", pub, false, this.sportCode, 
        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
        					YssFun.toDate(this.endDate), logInfo, 
        					logStartTime, logSumCode, new java.util.Date());
        		}
            }
			//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//    
            
            runStatus.appendRunDesc("VchRun", "      检查凭证字典对应关系是否设置完成！\r\n");
            return reStr;
        } catch (Exception e) {
        	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		if(logSumCode.trim().length() > 0){
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查凭证字典对应关系是否设置", pub, true, this.sportCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), 
            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
            					(logInfo + "\r\n检查凭证字典对应关系是否设置出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
            					.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""), 
            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
            					logStartTime, logSumCode, new java.util.Date());
            		}
        		}
        		
        		runStatus.appendRunDesc("VchRun", "检查失败！" + e.getMessage());
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
            finally{//添加 finally 保证可以抛出异常
            	throw new YssException("检查凭证字典对应关系是否设置出错！", e);
            }
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeResultSetFinal(rs, vchrs);
        }
    }
	
	/**
	 * 将要处理的凭证模板数据源结果集放到一个HashMap中  by qiuxufeng 20110117 390 QDV4赢时胜（上海）2010年12月08日02_A
	 * @方法名：getDsData
	 * @返回类型：void
	 */
	private void getDsData() throws SQLException, YssException {
        ResultSet rs = null;
        ResultSet rsDs = null;
		String sqlStr = "";
        String strDataSource = "";
        ResultSetMetaData rsmd = null;
        
		sqlStr = "select dat.FVchTplCode, ds.FDataSource from" +
				" (select distinct FVchTplCode from " + pub.yssGetTableName("Tb_Vch_Data") +
				" where FVchTplCode in (" + operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes) + ")) dat" +
				" left join " + pub.yssGetTableName("Tb_Vch_VchTpl") + " tpl on dat.FVchTplCode = tpl.FVchTplCode" +
				" left join " + pub.yssGetTableName("Tb_Vch_DataSource") + " ds on tpl.FDsCode = ds.FVchDsCode" +
				" order by FVchTplCode";
		try {
			if(null == hmDsData) {
				hmDsData = new HashMap();
			}
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while(rs.next()) {
				if(!hmDsData.containsKey(rs.getString("FVchTplCode"))) {
					if (dbl.getDBType() == YssCons.DB_ORA) {
						strDataSource = dbl.clobStrValue(rs.getClob("FDataSource")).replaceAll("\t", "   ");
			        } else if (dbl.getDBType() == YssCons.DB_DB2) {
			        	strDataSource = rs.getString("FDataSource").replaceAll("\t", "   ");
			        }
			        HashMap hmDsData = new HashMap();
					sqlStr = buildDsSql(strDataSource, sportCode);
					rsDs = dbl.openResultSet(sqlStr);
					if(!hmDsData.containsKey(rs.getString("FVchTplCode"))) {
						rsmd = rsDs.getMetaData();
						while(rsDs.next()) {
							for (int i = 0; i < rsmd.getColumnCount(); i++) {
								if(hmDsData.containsKey(rsmd.getColumnName(i + 1))) {
									String sValue = (String)hmDsData.get(rsmd.getColumnName(i + 1)) + "," + rsDs.getString(i + 1);
									hmDsData.put(rsmd.getColumnName(i + 1), sValue);
								} else {
									hmDsData.put(rsmd.getColumnName(i + 1), rsDs.getString(i + 1));
								}
							}
						}
						this.hmDsData.put(rs.getString("FVchTplCode"), hmDsData);
					}
					dbl.closeResultSetFinal(rsDs);
				}
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		} finally {
            dbl.closeResultSetFinal(rs, rsDs);
        }
	}
	
	/**
	 * 将字典数据结果集放到一个HashMap中 by qiuxufeng 20110117 390 QDV4赢时胜（上海）2010年12月08日02_A
	 * @throws SQLException 
	 * @方法名：getDictData
	 * @参数：void
	 * @返回类型：void
	 */
	private void getDictData() throws SQLException, YssException {
		String strSql = "";
		ResultSet rs = null;
		if(null == hmDictData) {
			hmDictData = new HashMap();
		}
		strSql = "select * from " + pub.yssGetTableName("Tb_Vch_Dict") + " where FCheckState = 1";
		try {
			rs = dbl.queryByPreparedStatement(strSql);  //modify by fangjiang 2011.08.14 STORY #788
			while(rs.next()) {
				hmDictData.put(rs.getString("FDictCode") + rs.getString("FIndCode"), rs.getString("FCnvConent"));
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 获取凭证分录元素科目字段的值 by qiuxufeng 20110117 390 QDV4赢时胜（上海）2010年12月08日02_A
	 * @方法名：getSubjectField
	 * @参数：sTpl String 凭证模板代码
	 * @参数：sField String 科目对应字段
	 * @返回类型：String
	 */
	private String getSubjectField(String sTpl, String sField) {
		HashMap tmpHm = null;
		String reStr = "";
		if(hmDsData.containsKey(sTpl)) {
			tmpHm = (HashMap) hmDsData.get(sTpl);
			// edit by qiuxufeng 20110304 BUG #1185 QDV4上海(38测试)2011年3月02日01_B
			// 通过ResultSetMetaData获取的列名都为大写
			if(tmpHm.containsKey(sField.toUpperCase())) {
				reStr = (String) tmpHm.get(sField.toUpperCase());
			}
		}
		return reStr;
	}
	
	/**
	 * 判断该分录元素的科目字段是否存在字典设置  by qiuxufeng 20110118 390 QDV4赢时胜（上海）2010年12月08日02_A
	 * @方法名：existCnvContent
	 * @参数：sDictCode String 字典代码
	 * @参数：sIndCode String 标识代码
	 * @返回类型：boolean
	 */
	private boolean existCnvContent(String sDictCode, String sIndCode) {
		if(null == hmDictData || hmDictData.isEmpty()) {
			return false;
		}
		if(hmDictData.containsKey(sDictCode + sIndCode)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 20111026 added byliubo.Story #1456
	 * 通过此方法，获取生成的凭证分录数据的实际的科目，来判定该凭证分录是否有生成
	 * @参数：String sVchNum		凭证编号
	 * @参数：String sDsCode		数据源代号
	 * @参数：String sEntityCode		凭证分录编号
	 * @参数：String sVchTplCode		凭证模板编号
	 * @返回类型：boolean
	 */
	private boolean existDataEntity(String sVchNum,String sDsCode,String sEntityCode,String sVchTplCode) throws YssException
	{
		String strSql = "";
		String strDsCodeSql = "";
		ResultSet rsDsCode = null;
		ResultSet rs = null;
		HashMap hmDsFieldType = null;
		boolean bResult = false;
		try {
			BaseVchBuild vchBuild = new BaseVchBuild();
			vchBuild.setYssPub(pub);
			vchBuild.setBeginDate(beginDate);
			vchBuild.setEndDate(endDate);
			
			strDsCodeSql = vchBuild.buildVchDsSql(sDsCode,sportCode);
			rsDsCode = dbl.openResultSet(strDsCodeSql);
			hmDsFieldType = dbFun.getFieldsType(rsDsCode);
			while(rsDsCode.next())
			{
				strSql = "select * from " + pub.yssGetTableName("Tb_Vch_DataEntity") + " where FVchNum = '" + sVchNum + "' and FSubjectCode = '" + 
						vchBuild.getEntitySubject(sVchTplCode,sEntityCode,rsDsCode,hmDsFieldType,sportCode) + "'";
				rs = dbl.queryByPreparedStatement(strSql);
			
				if (rs.next())
				{
					bResult = true;
				}
				else
				{
					bResult = false;
				}
				
				dbl.closeResultSetFinal(rs);
				
			}
				
			return bResult;
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rsDsCode,rs);
		}
	}
	
	/**
	 * add by songjie 2013.01.21
	 * 获取摘要数据
	 * @param sVchNum
	 * @param sDsCode
	 * @param sEntityCode
	 * @param sVchTplCode
	 * @return
	 * @throws YssException
	 */
	private boolean existDataResume(String sVchNum,String sDsCode,String sEntityCode,String sVchTplCode) throws YssException
	{
		String strSql = "";
		String strDsCodeSql = "";
		ResultSet rsDsCode = null;
		ResultSet rs = null;
		HashMap hmDsFieldType = null;
		boolean bResult = false;
		try {
			BaseVchBuild vchBuild = new BaseVchBuild();
			vchBuild.setYssPub(pub);
			vchBuild.setBeginDate(beginDate);
			vchBuild.setEndDate(endDate);
			
			strDsCodeSql = vchBuild.buildVchDsSql(sDsCode,sportCode);
			rsDsCode = dbl.openResultSet(strDsCodeSql);
			hmDsFieldType = dbFun.getFieldsType(rsDsCode);
			while(rsDsCode.next())
			{
				strSql = "select * from " + pub.yssGetTableName("Tb_Vch_DataEntity") + 
				" where FVchNum = " + dbl.sqlString(sVchNum) + " and FResume = " + 
				dbl.sqlString(vchBuild.getEntityResume(sVchTplCode,sEntityCode,rsDsCode,hmDsFieldType,sportCode));
				rs = dbl.queryByPreparedStatement(strSql);
			
				if (rs.next())
				{
					bResult = true;
				}
				else
				{
					bResult = false;
				}
				
				dbl.closeResultSetFinal(rs);
				
			}
				
			return bResult;
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rsDsCode,rs);
		}
	}
	
	/**
	 * add by songjie 2013.01.21
	 * 获取辅助核算数据
	 * @param sVchNum
	 * @param sDsCode
	 * @param sEntityCode
	 * @param sVchTplCode
	 * @return
	 * @throws YssException
	 */
	private boolean existDataAssistant(String sVchNum,String sDsCode,String sEntityCode,String sVchTplCode) throws YssException
	{
		String strSql = "";
		String strDsCodeSql = "";
		ResultSet rsDsCode = null;
		ResultSet rs = null;
		HashMap hmDsFieldType = null;
		boolean bResult = false;
		try {
			BaseVchBuild vchBuild = new BaseVchBuild();
			vchBuild.setYssPub(pub);
			vchBuild.setBeginDate(beginDate);
			vchBuild.setEndDate(endDate);
			
			strDsCodeSql = vchBuild.buildVchDsSql(sDsCode,sportCode);
			rsDsCode = dbl.openResultSet(strDsCodeSql);
			hmDsFieldType = dbFun.getFieldsType(rsDsCode);
			while(rsDsCode.next())
			{
				strSql = "select * from " + pub.yssGetTableName("Tb_Vch_DataEntity") + 
				" where FVchNum = " + dbl.sqlString(sVchNum) + " and FAssistant = " + 
				dbl.sqlString(vchBuild.getAssistant(sVchTplCode,sEntityCode,rsDsCode,hmDsFieldType,sportCode));
				rs = dbl.queryByPreparedStatement(strSql);
			
				if (rs.next())
				{
					bResult = true;
				}
				else
				{
					bResult = false;
				}
				
				dbl.closeResultSetFinal(rs);
				
			}
				
			return bResult;
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rsDsCode,rs);
		}
	}
}
