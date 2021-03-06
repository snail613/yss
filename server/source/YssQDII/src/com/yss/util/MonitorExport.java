package com.yss.util;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.dsub.DbBase;

/**
 * add by songjie 2013.01.09
 * STORY #2343 QDV4建行2012年3月2日04_A
 * @author 宋洁
 *
 */
public class MonitorExport extends BaseDataSettingBean {
	//--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
	public static int loginNumber = 0;
	public static int errorInputNumber = 0;
	//--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 end---//
	
	//写监控数据到导出文件
	public void writeMonitorFile(PrintWriter pw) throws YssException{
		Connection conn = null;
		boolean bTrans = false;
		try{
			if(dbl == null){
				dbl = new DbBase();
				conn = dbl.loadConnection("[db_ysslog]");
				if(conn == null){
					conn = dbl.loadConnection(YssCons.DB_CONNECTTION_RECENTLY);
				}
			}
			conn.setAutoCommit(false);
			bTrans = true;
			
			getWriteFile(pw);
			
			//--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
			this.loginNumber = 0;
			this.errorInputNumber = 0;
			//--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 end---//
			
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("生成监控日志文件出错", e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * 获取登录信息
	 * @param sb
	 * @param assetGroupCode
	 * @return
	 * @throws YssException
	 */
	private void getWriteFile(PrintWriter pw) throws YssException{
		ResultSet rs = null;
		String strSql2 = "";
		String logInfo = "";
		StringBuffer strSqlSB = new StringBuffer();
		java.util.Date sysDate = new java.util.Date();//本次导出时间
		String operDate = "";
		String rbOperDate = "";
		String[] dates = null;
		String finishTime = "";
		String finishDate = "";
		String executeTime = "";
		String executeResult = "";
		String businessModule = "";
		HashMap hmValue = new HashMap();
		String key = "";
		String value = "";
		try{
			addInfoTohm(hmValue);
			
	        GregorianCalendar cl = new GregorianCalendar();
	        cl.setTime(sysDate);
	        long latestExpTime = cl.getTimeInMillis() - 30*60*1000;
	        cl.setTimeInMillis(latestExpTime);
	        Date latestExpDate =cl.getTime();//上次导出时间
			
			strSqlSB.append(" select a.D_OPER_DATE, ");//操作日期
			strSqlSB.append(" a.C_Creator_Code, ");//用户代码
			strSqlSB.append(" a.D_Execute_Start, ");//开始时间
			strSqlSB.append(" a.D_Execute_End, ");//结束时间
			strSqlSB.append(" a.C_Execute_Time, ");//执行时长
			strSqlSB.append(" case when a.C_Result_Typename = ' '  ");
			strSqlSB.append(" then 0 else 1 end as C_Result_Typename, ");//执行结果
			strSqlSB.append(" substr(b.C_Port_Code, 0, instr(b.C_Port_Code, '-') - 1) as FAssetGroupCode, ");//组合群代码
			strSqlSB.append(" substr(b.C_Port_Code, ");
			strSqlSB.append(" instr(b.C_Port_Code, '-') + 1, ");
			strSqlSB.append(" LENGTH(b.C_Port_Code) - instr(b.C_Port_Code, '-')) as FPortCode, ");//组合代码
			strSqlSB.append(" b.C_Bussiness_Module, ");//业务模块
			strSqlSB.append(" b.C_Bussiness_Sub_Module ");//业务子模块
			strSqlSB.append(" from (select D_OPER_DATE, ");
			strSqlSB.append(" C_Creator_Code, ");
			strSqlSB.append(" D_Execute_Start, ");
			strSqlSB.append(" D_Execute_End, ");
			strSqlSB.append(" C_Execute_Time, ");
			strSqlSB.append(" C_Result_Typename, ");
			strSqlSB.append(" FLogSumCode ");
			strSqlSB.append(" from T_Plugin_Log ");
			//add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			strSqlSB.append(" where D_EXECUTE_END <= " + dbl.sqlDate(sysDate,true) + " and D_OPER_DATE > " + dbl.sqlDate(latestExpDate,true));
			//delete by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			//strSqlSB.append(" where to_char(D_OPER_DATE, 'yyyyMMdd') = " + dbl.sqlString(YssFun.formatDate(sysDate,"yyyyMMdd")));
			strSqlSB.append(" and C_REF_NUM = '[root]' and C_Bussiness_Module <> '调度方案执行') a ");
			strSqlSB.append(" left join (select distinct C_Port_Code, ");
			strSqlSB.append(" C_Bussiness_Module, ");
			strSqlSB.append(" ' ' as C_Bussiness_Sub_Module, ");
			strSqlSB.append(" C_REF_NUM ");
			strSqlSB.append(" from T_Plugin_Log ");
			//add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			strSqlSB.append(" where D_EXECUTE_END <= " + dbl.sqlDate(sysDate,true) + " and D_OPER_DATE > " + dbl.sqlDate(latestExpDate,true));
			//delete by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			//strSqlSB.append(" where to_char(D_OPER_DATE, 'yyyyMMdd') = " + dbl.sqlString(YssFun.formatDate(sysDate,"yyyyMMdd")));
			strSqlSB.append(" and C_REF_NUM <> '[root]' ");
			strSqlSB.append(" and C_Bussiness_Module <> '收益计提' ");
			strSqlSB.append(" and C_Bussiness_Module <> '收益支付' ");
			strSqlSB.append(" union all ");
			strSqlSB.append(" select distinct C_Port_Code, ");
			strSqlSB.append(" C_Bussiness_Module, ");
			strSqlSB.append(" C_Bussiness_Sub_Module, ");
			strSqlSB.append(" C_REF_NUM ");
			strSqlSB.append(" from T_Plugin_Log ");
			//add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			strSqlSB.append(" where D_EXECUTE_END <= " + dbl.sqlDate(sysDate,true) + " and D_OPER_DATE > " + dbl.sqlDate(latestExpDate,true));
			//delete by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			//strSqlSB.append(" where to_char(D_OPER_DATE, 'yyyyMMdd') = " + dbl.sqlString(YssFun.formatDate(sysDate,"yyyyMMdd")));
			strSqlSB.append(" and C_REF_NUM <> '[root]' ");
			strSqlSB.append(" and C_Bussiness_Module in ('收益计提', '收益支付')) b on a.FLogSumCode = b.C_REF_NUM ");
			strSqlSB.append(" union all ");
			strSqlSB.append(" select a.D_OPER_DATE, ");//操作日期
			strSqlSB.append(" a.C_Creator_Code, ");//用户代码
			strSqlSB.append(" a.D_Execute_Start, ");//开始时间
			strSqlSB.append(" a.D_Execute_End, ");//结束时间
			strSqlSB.append(" a.C_Execute_Time, ");//执行时长
			strSqlSB.append(" case when a.C_Result_Typename = ' '  ");
			strSqlSB.append(" then 0 else 1 end as C_Result_Typename, ");//执行结果
			strSqlSB.append(" substr(b.C_Port_Code, 0, instr(b.C_Port_Code, '-') - 1) as FAssetGroupCode, ");//组合群代码
			strSqlSB.append(" substr(b.C_Port_Code, ");
			strSqlSB.append(" instr(b.C_Port_Code, '-') + 1, ");
			strSqlSB.append(" LENGTH(b.C_Port_Code) - instr(b.C_Port_Code, '-')) as FPortCode, ");//组合代码
			strSqlSB.append(" b.C_Bussiness_Module, ");//业务模块
			strSqlSB.append(" b.C_Bussiness_Sub_Module ");//业务子模块
			strSqlSB.append(" from (select D_OPER_DATE, ");
			strSqlSB.append(" C_Creator_Code, ");
			strSqlSB.append(" D_Execute_Start, ");
			strSqlSB.append(" D_Execute_End, ");
			strSqlSB.append(" C_Execute_Time, ");
			strSqlSB.append(" C_Result_Typename, ");
			strSqlSB.append(" FLogSumCode ");
			strSqlSB.append(" from T_Plugin_Log ");
			//add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			strSqlSB.append(" where D_EXECUTE_END <= " + dbl.sqlDate(sysDate,true) + " and D_OPER_DATE > " + dbl.sqlDate(latestExpDate,true));
			//delete by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			//strSqlSB.append(" where to_char(D_OPER_DATE, 'yyyyMMdd') = " + dbl.sqlString(YssFun.formatDate(sysDate,"yyyyMMdd")));
			strSqlSB.append(" and C_REF_NUM = '[root]' and C_Bussiness_Module = '调度方案执行') a ");
			strSqlSB.append(" left join (select distinct C_Port_Code, ");
			strSqlSB.append(" '调度方案执行' as C_Bussiness_Module, ");
			strSqlSB.append(" ' ' as C_Bussiness_Sub_Module, ");
			strSqlSB.append(" C_REF_NUM ");
			strSqlSB.append(" from T_Plugin_Log ");
			//add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			strSqlSB.append(" where D_EXECUTE_END <= " + dbl.sqlDate(sysDate,true) + " and D_OPER_DATE > " + dbl.sqlDate(latestExpDate,true));
			//delete by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			//strSqlSB.append(" where to_char(D_OPER_DATE, 'yyyyMMdd') = " + dbl.sqlString(YssFun.formatDate(sysDate,"yyyyMMdd")));
			strSqlSB.append(" and C_REF_NUM <> '[root]') b on a.FLogSumCode = b.C_REF_NUM ");
			strSqlSB.append(" union all ");
			//--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
			strSqlSB.append(" select sysdate as D_OPER_DATE, ' ' as C_Creator_Code, sysdate as D_Execute_Start, ");
			strSqlSB.append(" sysdate as D_Execute_End, ' ' as C_Execute_Time, 0 as C_Result_TypeName, ");
			strSqlSB.append(" ' ' as FAssetGroupCode, ' ' as FPortCode, '用户登录' as C_Bussiness_Module, ");
			strSqlSB.append(" ' ' as C_Bussiness_Sub_Module from dual a ");
			//--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 end---//
			strSql2 = strSqlSB.toString();
			rs = dbl.openResultSet(strSql2);
			while(rs.next()){
				if(rs.getString("C_Bussiness_Module").equals("用户登录")){
					operDate = rs.getString("D_Execute_Start");
					operDate = operDate.substring(0, operDate.indexOf("."));
					dates = operDate.split(" ");
					rbOperDate = dates[0] + "_" + dates[1].replaceAll(":", "-");
					
					logInfo = 
					//edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
					rbOperDate + "|LOGINOPERATOR|ITIS_QDII_USERLOG|LogInNumber|" + this.loginNumber + 
					"\r\n" +
					//edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
					rbOperDate + "|OPRPASSWORD|ITIS_QDII_PWERROR|ErrorTime|" + this.errorInputNumber;
				}else if(rs.getString("C_Bussiness_Module").equals("收益计提") ||
						 rs.getString("C_Bussiness_Module").equals("收益支付")){
					operDate = rs.getString("D_Execute_Start");
					operDate = operDate.substring(0, operDate.indexOf("."));
					dates = operDate.split(" ");
					rbOperDate = dates[0] + "_" + dates[1].replaceAll(":", "-");
					
					finishTime = rs.getString("D_Execute_End");
					finishTime = finishTime.substring(0, finishTime.indexOf("."));
					dates = finishTime.split(" ");
					finishDate = dates[0].replaceAll("-", "") + dates[1].replaceAll(":", "");
					
					executeTime = rs.getString("C_EXECUTE_TIME").replaceAll(":", "");
					
					executeResult = rs.getString("C_Result_TypeName");
					
					//--- edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
					key = rs.getString("C_Bussiness_Module") + "_" + rs.getString("C_Bussiness_Sub_Module");
					value = (String)hmValue.get(key);
					logInfo = 
					rbOperDate + "|DAYENDBATCH|ITIS_QDII_" + value + "|RunTime|" + executeTime +
					"\r\n" + 
					rbOperDate + "|DAYENDBATCH|ITIS_QDII_" + value + "|FinishTime|" + finishDate +
					"\r\n" + 
					rbOperDate + "|DAYENDBATCH|ITIS_QDII_" + value + "|ExecResult|" + executeResult;
					//--- edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 end---//
				} else{
					businessModule = rs.getString("C_Bussiness_Module");
					
					//--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
					key = rs.getString("C_Bussiness_Module");
					value = (String)hmValue.get(key);
					//--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
					
					operDate = rs.getString("D_Execute_Start");
					operDate = operDate.substring(0, operDate.indexOf("."));
					dates = operDate.split(" ");
					rbOperDate = dates[0] + "_" + dates[1].replaceAll(":", "-");
					
					finishTime = rs.getString("D_Execute_End");
					finishTime = finishTime.substring(0, finishTime.indexOf("."));
					dates = finishTime.split(" ");
					finishDate = dates[0].replaceAll("-", "") + dates[1].replaceAll(":", "");
					
					executeTime = rs.getString("C_EXECUTE_TIME").replaceAll(":", "");
					
					executeResult = rs.getString("C_Result_TypeName");
					
					if(businessModule.equals("财务系统")){
						businessModule = "余额表生成";
					}else if(businessModule.equals("净值统计表")){
						businessModule = "净值统计表生成";
					}else if(businessModule.equals("财务估值表")){
						businessModule = "财务估值表生成";
					}
					
					logInfo = 
					//--- edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
					rbOperDate + "|DAYENDBATCH|ITIS_QDII_" + value + "|RunTime|" + executeTime +
					"\r\n" + 
					rbOperDate + "|DAYENDBATCH|ITIS_QDII_" + value + "|FinishTime|" + finishDate +
					"\r\n" + 
					rbOperDate + "|DAYENDBATCH|ITIS_QDII_" + value + "|ExecResult|" + executeResult;
					//--- edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 end---//
				}
				
				pw.println(logInfo);
			}
		}catch(Exception e){
			throw new YssException("获取监控日志数据出错");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2013.03.05
	 * STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
	 * @param hmValue
	 * @throws YssException
	 */
	private void addInfoTohm(HashMap hmValue) throws YssException{
		hmValue.put("接口处理", "ITFDEAL");
		hmValue.put("交易结算", "TATRADESETTLE");
		hmValue.put("TA交易结算", "TATRADESETTLE");
		hmValue.put("净值统计表", "NAVREPCREATE");
		hmValue.put("财务估值表", "FINCREPCREATE");
		hmValue.put("财务系统", "BLCREPCREATE");
		hmValue.put("资产估值", "ASSETCAL");
		hmValue.put("业务处理", "BUSINESSDEAL");
		hmValue.put("权益处理", "RIGHTDEAL");
		hmValue.put("收益计提_债券计息", "BONDCALC");
		hmValue.put("收益计提_现金计息", "CASHCALC");
		hmValue.put("收益计提_两费计提", "FEECALC");
		hmValue.put("收益计提_回购计息", "PURCHASECALC");
		hmValue.put("收益计提_基金万分收益计提", "FUNDCALC");
		hmValue.put("收益计提_证券借贷计息", "SECLENDCALC");
		hmValue.put("收益支付_债券利息收支", "BONDITRSTPAY");
		hmValue.put("收益支付_现金利息收支", "CASHITRSTPAY");
		hmValue.put("收益支付_两费收支", "FEEPAY");
		hmValue.put("收益支付_证券借贷收益支付", "SECLENDPAY");
		hmValue.put("收益支付_送股税金支付", "SECTAXPAY");
		hmValue.put("库存统计","STOCKSTATIC");
		hmValue.put("凭证生成", "VCHCREATE");
		hmValue.put("凭证检查","VCHCHECK");
		hmValue.put("凭证方案执行", "VCHPROIMP");
		hmValue.put("调度方案执行", "SCHPROIMP");
		hmValue.put("预警检查", "PRECHECK");
	}
}
