package com.yss.main.operdeal.voucher.vchcheck;

import java.sql.*;

import com.yss.log.SingleLogOper;
import com.yss.util.*;

/**
 *
 * <p>Title: VchChkLaccCuy</p>
 * <p>Description: 检查凭证中科目币种与系统币种设置的匹配性</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author sj
 * @version 1.0
 */
public class VchChkLaccCuy
    extends BaseVchCheck {
    public VchChkLaccCuy() {
    }

    public String doCheck() throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        //String[] aryPorts = null;
        String[] bookSets = null;
        String reStr = "true";
        
		//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
		java.util.Date logStartTime = new java.util.Date();
		if(logOper == null){//添加非空判断
			logOper = SingleLogOper.getInstance();
		}
		String logInfo = "";
		String showInfo = "";
		//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            //aryPorts = this.portCodes.split(",");
            //for (int ports = 0; ports < aryPorts.length; ports++) {
        	
        	//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        	logInfo += "      开始检查科目币种设置... ...\r\n";
        	
            runStatus.appendRunDesc("VchRun", "      开始检查科目币种设置... ..."); //修改提示信息 by leeyu 20090219 QDV4赢时胜（上海）2009年02月16日01_B  MS00247
            bookSets = this.getBookSet(sportCode);
            //add by licai 20101210 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
            if(bookSets==null){
            	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	if(logSumCode.trim().length() > 0){
            		logInfo += "        组合【"+sportCode+"】的套帐代码不存在！\r\n";
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查科目币种设置", pub, false, this.sportCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), logInfo, 
            					logStartTime, logSumCode, new java.util.Date());
            		}
            	}
    			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            	
            	runStatus.appendRunDesc("VchRun",  "        组合【"+sportCode+"】的套帐代码不存在！\r\n");
            	reStr="false";
            	return reStr;
            }
            //add by licai 20101210 BUG #541======================================================================end            
            for (int booksets = 0; booksets < bookSets.length; booksets++) {

            	//20120401 modified by liubo.Bug #4109
            	//要求在显示检查时将检查到的错误数据明显到分录
            	//======================================
                sqlStr = "select   Vch.*, n.FCuryCode as nFCuryCode,Tpl.FVchTplName " +
                    " from (select a.FVchNum      as FVchNum, " +
                    " a.FVchTplCode  as FVchTplCode," +
                    " a.FBookSetCode as FBookSetCode," +
                    " a.FCuryCode as FCuryCode," +
                    " b.FSubjectCode as FSubjectCode," +
                    " b.fEntityNum as fEntityNum" +
                    " from (select * " +
                    " from " + pub.yssGetTableName("Tb_vch_data") +
                    //" where FCheckState <> 2 and " +
                    //edit by licai 20101210 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示
                    " where " + //这里只算已审核的数据 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
                    //edit by licai 20101210 BUG #541 ==============================end
                    " FPortCode = " + dbl.sqlString(sportCode) + " and " +
                    " FVchDate between " + dbl.sqlDate(this.beginDate) +
                    " and " + dbl.sqlDate(this.beginDate) +
                    " and FVchTplCode in (" +
                    operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes) + //function
                    ")) a left join (select * from " +
                    pub.yssGetTableName("Tb_Vch_DataEntity") +
//                    " where FCheckState <> 2) b on a.FVchNum = b.FVchNum) Vch left join " +
                    //" where FCheckState <> 2) b on a.FVchNum = b.FVchNum) Vch  join " +//by leeyu 去掉left　本次只检查财务与估值系统中科目相同的那一部分　MS00076BUG中的第二个问题 by leeyu 20090213
                  //edit by licai 20101210 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示
                    " ) b on a.FVchNum = b.FVchNum) Vch  join " + //这里只算已审核的数据 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
                  //edit by licai 20101210 BUG #541 ======================================end
                    "(select * from (select FAcctCode,FCurCode  from " +
                    "A" +
                    YssFun.formatDate(YssFun.parseDate(this.beginDate), "yyyy") +
                    bookSets[booksets] + "laccount" +
//                    " where FAcctDetail = 1) Acc left join (select FCuryCode from " + pub.yssGetTableName("Tb_Para_Currency") +
                    " ) Acc left join (select FCuryCode from " + pub.yssGetTableName("Tb_Para_Currency") + //by leeyu　去掉只检查最明细的科目条件　MS00076BUG中的第二个问题 by leeyu 20090213
//                    " where FCheckState = 1) Cury on Acc.FCurCode = Cury.FCuryCode) n on Vch.FSubjectCode = n.FAcctCode " +
                    " where FCheckState = 1) Cury on Acc.FCurCode = Cury.FCuryCode or Acc.FCurCode='***') n on Vch.FSubjectCode = n.FAcctCode " + //by leeyu 添加上一级与二级科目的　MS00076BUG中的第二个问题 by leeyu 20090213
                    " left join (select FVchTplCode,FVchTplName from " + pub.yssGetTableName("Tb_vch_VchTpl") +
                    //" where FCheckState = 1 and ((FportCode is null or FPortCode='') or FPortCode='"+sportCode+
                    " where FCheckState = 1 and ((FportCode is null or FPortCode='' or FPortCode=' ') or FPortCode='" + sportCode +
                    "')) tpl on Vch.FVchTplCode = tpl.FVchTplCode"; //增加对专用组合的处理 by ly 080326 BUG:0000349
                rs = dbl.queryByPreparedStatement(sqlStr);  //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    if (rs.getString("nFCuryCode") == null ||
                        rs.getString("nFCuryCode").length() == 0) {
                    	
                    	//---edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    	showInfo =  "        凭证编号为【" + rs.getString("FVCHNUM") +
                    				"】、凭证模版为【" + rs.getString("FVchTplName") +
                    				"】、分录编号为【" + rs.getString("fEntityNum") + 
                    				"】、科目代码为【" + rs.getString("FSubjectCode") +
                    				"】的凭证科目设置币种出错！\r\n";
                    	
                    	logInfo += showInfo;
                    	
                        runStatus.appendRunDesc("VchRun", showInfo);
                        //---edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                        
                        reStr = "false";
                    }
                	//===============end=======================
                }
                //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
                dbl.closeResultSetFinal(rs);
            }
            
            //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            if(logSumCode.trim().length() > 0){
            	logInfo += "      科目币种设置检查完成！\r\n";
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, 24, "检查科目币种设置", pub, false, this.sportCode, 
        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
        					YssFun.toDate(this.endDate), logInfo, 
        					logStartTime, logSumCode, new java.util.Date());
        		}
            }
            //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            
            runStatus.appendRunDesc("VchRun", "      科目币种设置检查完成！\r\n"); //修改提示信息 by leeyu 20090219 QDV4赢时胜（上海）2009年02月16日01_B  MS00247
            dbl.closeResultSetFinal(rs);
            return reStr;
        } catch (Exception e) {
        	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		if(logSumCode.trim().length() > 0){
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查科目币种设置", pub, true, this.sportCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), 
            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
            					(logInfo + "\r\n检查科目币种设置出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
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
            	throw new YssException("检查科目币种出错！", e);
            }
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
