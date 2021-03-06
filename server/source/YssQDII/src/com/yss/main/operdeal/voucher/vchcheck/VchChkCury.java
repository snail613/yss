package com.yss.main.operdeal.voucher.vchcheck;

import java.sql.*;

import com.yss.log.SingleLogOper;
import com.yss.util.*;

public class VchChkCury
    extends BaseVchCheck {
    public VchChkCury() {
    }

    /**
     * MS00332
     * QDV4赢时胜（上海）2009年3月20日01_AB
     * by leeyu 20090325
     * @throws YssException
     */
    public String doCheck() throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
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
            bookSets = getBookSet(sportCode);
            //add by licai 20101210 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
            if(bookSets==null){
            	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	if(logSumCode.trim().length() > 0){
            		logInfo += "        组合【"+sportCode+"】的套帐代码不存在！\r\n";
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查财务系统科目的币种是否设置为明细币种", pub, false, sportCode, 
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
            
        	//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        	logInfo += "      开始检查财务系统科目的币种是否设置为明细币种... ...\r\n";
        	
            runStatus.appendRunDesc("VchRun", "      开始检查财务系统科目的币种是否设置为明细币种... ...");
            for (int booksets = 0; booksets < bookSets.length; booksets++) {

            	//20120401 modified by liubo.Bug #4109
            	//要求在显示检查时将检查到的错误数据明显到分录
            	//======================================
                sqlStr = "select distinct acc.FCurCode, acc.FAcctCode, acc.FAcctName,vch.fEntityNum,vch.FVchTplCode,vch.FVchNum from (select a.FVchTplCode,a.FPortCode," +
                    " a.FBookSetCode,b.FSubJectCode,b.fEntityNum as fEntityNum,b.fVchNum as fVchNum,a.fvchdate from " + pub.yssGetTableName("tb_vch_data") + " a" +
                    //edit by qiuxufeng 20100105 428 QDV4深圳赢时胜2010年12月18日02_A 凭证检查时的优化，join改为left join
                    " left join " + pub.yssGetTableName("tb_vch_dataentity") + " b " +
                    
                    //modify by zhangfa 20101221 BUG #671 凭证生成时，如果有凭证科目未做到最明细，单独勾选该凭证代码生成凭证时系统不会检查。
                    " on a.FVchNum = b.FVchNum where a.FVchTplCode in (" + operSql.sqlCodes(this.isInData ? this.vchTypes : getVchTpls()  ) + ") " +
                    //----------------------------end 20101221 ------------------------------------------------------------------------
                    " and a.fvchdate between " + dbl.sqlDate(this.beginDate) + " and " + dbl.sqlDate(this.endDate) + " " +
                    " and a.FPortCode = " + dbl.sqlString(sportCode) + " and b.Fcheckstate=1 ) vch" +
                    //edit by qiuxufeng 20100105 428 QDV4深圳赢时胜2010年12月18日02_A 凭证检查时的优化，join改为left join
                    " left join A" + YssFun.formatDate(YssFun.parseDate(this.beginDate), "yyyy") +
                    bookSets[booksets] + "laccount" + " acc " +
                    " on vch.FSubJectCode = acc.FAcctCode  where acc.Fcurcode = '***'";
                rs = dbl.queryByPreparedStatement(sqlStr);  //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                	//---edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                	showInfo = "        凭证编号为【" + rs.getString("FVchNum") + 
    							"】、模板代码为【" + rs.getString("FVchTplCode") + 
    							"】、分录代码为【" + rs.getString("fEntityNum") + 
    							"】、科目代码为【" + rs.getString("FAcctCode") + 
    							"】、科目名称为【" + rs.getString("FAcctName") + 
    							"】的币种为“***”，没有设置为最明细币种！\r\n";
                	
                	logInfo += showInfo;
                	
                    runStatus.appendRunDesc("VchRun", showInfo);
                    //---edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    
                    reStr = "false";

                }
            	//===================end===================
                //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
                dbl.closeResultSetFinal(rs);
            }
            
        	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            if(logSumCode.trim().length() > 0){
            	logInfo +=  "      财务系统科目的币种是否设置为明细币种检查完成！\r\n";
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, 24, "检查财务系统科目的币种是否设置为明细币种", pub, false, sportCode, 
        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
					    	YssFun.toDate(this.endDate), logInfo, 
					    	logStartTime, logSumCode, new java.util.Date());
        		}
            }
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			
            runStatus.appendRunDesc("VchRun", "      财务系统科目的币种是否设置为明细币种检查完成！\r\n");
            dbl.closeResultSetFinal(rs);
            return reStr;
        } catch (Exception e) {
        	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		if(logSumCode.trim().length() > 0){
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查财务系统科目的币种是否设置为明细币种",  
            					pub, true, sportCode, YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
    							YssFun.toDate(this.endDate), 
    							//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
    							(logInfo + "\r\n检查财务系统科目的币种是否设置为明细币种出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
    							.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""), 
    							//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
    							logStartTime, logSumCode, new java.util.Date());
            		}
        		}
        		
        		runStatus.appendValRunDesc("检查失败！" + e.getMessage());
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
}
