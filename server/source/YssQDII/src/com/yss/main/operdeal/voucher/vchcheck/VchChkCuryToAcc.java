package com.yss.main.operdeal.voucher.vchcheck;

import java.sql.ResultSet;

import com.yss.log.SingleLogOper;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.pojo.sys.YssStatus;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

/**
*
* <p>Title: VchChkCuryToAcc</p>
* <p>Description: 326 QDV4工银2010年11月23日01_A 同一凭证不能有多币种检查（结转损益、汇兑损益、外汇交易、分红业务除外）</p>
* <p>Copyright: Copyright (c) 2006</p>
* <p>Company: </p>
* @author qiuxufeng
* @version 1.0
*/
public class VchChkCuryToAcc 
	extends BaseVchCheck {

	public VchChkCuryToAcc() {
	}
	
	  //add by lidaolong 20110408 #665 凭证导入时检查凭证币种，弹出提示窗口
	private String strInfo="";//传到前台的提示信息
	private boolean isCuryCheck=false;
	public boolean comeFromDAO = false;//由凭证接口调用
	public void parseStr(String sRowStr) throws YssException{
	    String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\r\f")[0].split("\t");

            this.beginDate = reqAry[0];
            this.endDate = reqAry[1];
            this.sportCode = reqAry[2]; //由，间隔
            //this.beanId = reqAry[3];
            this.vchTypes = reqAry[3]; //由，间隔
        } catch (Exception e) {
            throw new YssException("凭证操作请求信息出错\r\n" + e.getMessage(), e);
        }
	}
	public String doCuryCheck() throws YssException{
		
		isCuryCheck=true;
		if(runStatus==null)
		{
			runStatus = new YssStatus();
		}
		String code =sportCode;
		for (int i = 0; i < code.split(",").length; i++) {
			sportCode = code.split(",")[i];
			doCheck();
			if (strInfo.equals("false")) {
				throw new YssException("  组合【" + sportCode + "】的套帐代码不存在！");
			} else if (!strInfo.equals("")) {
				return strInfo;
			}
		}
		return strInfo;
	}
	//end by lidaolong
	
	public String doCheck() throws YssException {
        ResultSet rs = null;
        ResultSet vchrs = null;
        String sqlStr = "";
        String[] bookSets = null;//套账代码
        String reStr = "true";
        String vchTypesCheck = "";//需要检查的凭证模板代码
        YssFinance cw = null;
        String curCode1 = "";
        String curCode2 = "";
        String vchNum = "";
        
        //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        java.util.Date logStartTime = new java.util.Date();
		if(logOper == null){//添加非空判断
			logOper = SingleLogOper.getInstance();
		}
        String logInfo = "";
        //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
        	// add by lidaolong #665 凭证导入时检查凭证币种，弹出提示窗口
			CtlPubPara para = new CtlPubPara();
			para.setYssPub(pub);
			String vchCodes = para.getCtlCode();    		
        	//end by lidaolong
        	
			//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
			logInfo += "      开始检查一张凭证是否有多币种... ...\r\n";
			
            runStatus.appendRunDesc("VchRun", "      开始检查一张凭证是否有多币种... ...");
            bookSets = this.getBookSet(sportCode);//通过组合获取套账代码
            if(bookSets==null){
            	//--- add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	if(logSumCode.trim().length() > 0){
            		logInfo += "        组合【"+sportCode+"】的套帐代码不存在！"+"\r\n";
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查一张凭证是否有多币种", pub, false, this.sportCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), logInfo, 
            					logStartTime, logSumCode, new java.util.Date());
            		}
            	}
            	//--- add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				
            	runStatus.appendRunDesc("VchRun",  "        组合【"+sportCode+"】的套帐代码不存在！"+"\r\n");
            	reStr="false";
            	strInfo="false";  //add by lidaolong 20110408 #665 凭证导入时检查凭证币种，弹出提示窗口
            	return reStr;
            }
        	//排除模板属性是 损益结转凭证、换汇业务凭证、汇兑损益凭证、分红业务凭证 的模板代码
            sqlStr = "select a.FVchTplCode from " + pub.yssGetTableName("Tb_Vch_VchTpl") + " a " +
	            		" left join " + pub.yssGetTableName("Tb_Vch_Attr") + " b on a.FAttrCode = b.Fattrcode " +
	            		" where a.FVchTplCode in (" +
	            		//operSql.sqlCodes(vchTypes) +
	            		//edit by qiuxufeng 20110119 凭证方案执行和调度方案执行需要内部通过属性获取模板代码
	            		operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes) +
	            		") " +
	            		//方法一：通过属性名称包含字符排除凭证
	            		/*" and b.fattrname not like '%损益%' " +
	            		" and b.fattrname not like '%换汇%' " +
	            		" and b.fattrname not like '%分红%'" +*/
	            		//方法二：通过完整属性名称排除凭证	暂用具体属性名称排除
	            	//	" and b.fattrname not in ('损益结转凭证', '换汇业务凭证', '汇兑损益凭证', '分红业务凭证')" +//del by lidaolong 20110414 #665 凭证导入时检查凭证币种，弹出提示窗口
	            		//方法三：通过属性代码排除凭证
	            		("".equals(vchCodes)?"":" and b.FAttrCode not in("+operSql.sqlCodes(vchCodes)+")") +// add by lidaolong 20110414 #665 凭证导入时检查凭证币种，弹出提示窗口
	            		
	            		/*" and b.FAttrCode not in ('017', " +				//损益结转凭证
	            								" '020', " +				//换汇业务凭证
	            								" '015', '099', '221', " +	//汇兑损益凭证
	            								" '011')" +	*/				//分红业务凭证
	            		" and a.FCheckState = 1 and b.FCheckState = 1";
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while(rs.next()) {
            	//排除不需要检查凭证 的模板代码，用","隔开
            	vchTypesCheck += rs.getString("FVchTplCode") + ",";
            }
            dbl.closeResultSetFinal(rs);
            if(vchTypesCheck.trim().length() == 0) {
            	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	if(logSumCode.trim().length() > 0){
            		logInfo += "      检查一张凭证是否有多币种完成！\r\n";
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查一张凭证是否有多币种", pub, false, this.sportCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), logInfo, 
            					logStartTime, logSumCode, new java.util.Date());
            		}
            	}
				//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				
            	runStatus.appendRunDesc("VchRun", "      检查一张凭证是否有多币种完成！\r\n");
            	//排除不必要检查的模板后，如没有要检查的模板则检查结束
            	return reStr;
            }
        	for (int booksets = 0; booksets < bookSets.length; booksets++) {
        		cw = new YssFinance();
                cw.setYssPub(pub);
                //edit by qiuxufeng 20110104 428 QDV4深圳赢时胜2010年12月18日02_A
               //modified by guyichuan 20110505 STORY #665 提示的显示内容须修改
                //优化凭证检查效率，将原每条分录的查询检查改为一条执行语句检查，减少访问数据库次数
                //String sTabName = cw.getCWTabName(sportCode, YssFun.toDate(this.beginDate), "LACCOUNT");
                String sTabName = getCwTab(bookSets[booksets]);//拼接财务科目表名

            	//20120401 modified by liubo.Bug #4109
            	//要求在显示检查时将检查到的错误数据明显到分录
            	//======================================
                sqlStr = "select FVchNum, FAcctName,FCurCode,FResume,FVchTplCode, FVchTplName,FEntityNum from (" +
	                	" select distinct vch.FVchNum, vch.FVchTplCode, vch.FVchTplName ," +
	                	" acc.FCurCode,vch.FCuryCode,ent.FResume,acc.FAcctName, ent.FEntityNum as FEntityNum from (" +
	                	" select FVchNum, a.FVchTplCode, b.FVchTplName,a.FCuryCode as FCuryCode" +
						" from " + pub.yssGetTableName("Tb_Vch_Data") + " a " +
						" left join " + pub.yssGetTableName("Tb_Vch_VchTpl") + " b " +
						" on a.FVchTplCode = b.FVchTplCode " +
	    				" where a.FVchDate between " + dbl.sqlDate(beginDate) +
	    				" and " + dbl.sqlDate(endDate) +
	    				" and a.FBookSetCode = " + dbl.sqlString(bookSets[booksets]) +
	    				" and a.FVchTplCode in (" +
	    				operSql.sqlCodes(vchTypesCheck) +
	    				")" +
	    				//" and a.FCheckState = 1" +
	    				// edit by qiuxufeng 20110323 检查已审核和未审核的凭证
	    				" and a.FCheckState in(0,1)" +
	    				" and b.FCheckState = 1) vch" +
	    				" left join " +
	    				" (select FVchNum, FSubjectCode,FResume,FEntityNum from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
	    				" where FCheckState = 1) ent" +
	    				" on vch.FVchNum = ent.FVchNum" +
	    				" left join " +
	    				" (select FAcctCode, FCurCode,FAcctName from " + sTabName + ") acc" +
	    				" on ent.FSubjectCode = acc.FAcctCode)" +
	    				" group by FVchNum, FVchTplCode, FVchTplName,FCurCode,FResume,FAcctName,FEntityNum order by FVchNum desc ";
                rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                while(rs.next()) {
                	// 560 QDV4工银2011年1月26日01_A edit by qiuxufeng 20110228
                	if(!vchNum.equals(rs.getString("FVchNum")))
                	{
                		vchNum = rs.getString("FVchNum");//不同凭证直接返回  同一凭证才继续
                		curCode1 = rs.getString("FCurCode");
                		continue;
                	}else if(curCode1.equals(rs.getString("FCurCode")))
                	{
                		continue;
                	}//modified by yeshenghong 20120417 BUG4089
            		if(rs.isFirst()) {
                    	//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
            			logInfo +=  "        `发现凭证出现多币种异常\r\n";
            			
            			runStatus.appendRunDesc("VchRun", "        `发现凭证出现多币种异常\r\n");
            			runStatus.appendSchRunDesc("\r\n        `发现凭证出现多币种异常");
            		}
            		
                	//--- add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        			logInfo +=  "凭证编号为【" + rs.getString("FVchNum") + 
    							"】、凭证模版编号为【" + rs.getString("FVchTplCode") +
    							"】、凭证模版名称为【" + rs.getString("FVchTplName") +
    							"】、分录编号为【" + rs.getString("FEntityNum") + 
    							"】出现一张凭证有多个币种！\r\n";
            		//--- add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        			
            		 //add by lidaolong 20110408 #665 凭证导入时检查凭证币种，弹出提示窗口
            		// 560 QDV4工银2011年1月26日01_A edit by qiuxufeng 20110228 end
            		runStatus.appendRunDesc("VchRun", "\r\n[CuryCheck_start]" + 
            				"凭证编号为【" + rs.getString("FVchNum") + 
            				"】、凭证模版编号为【" + rs.getString("FVchTplCode") +
                            "】、凭证模版名称为【" + rs.getString("FVchTplName") +
                            "】、分录编号为【" + rs.getString("FEntityNum") + 
                            "】出现一张凭证有多个币种！" + "[CuryCheck_end]\r\n");
            		// 560 QDV4工银2011年1月26日01_A edit by qiuxufeng 20110228
            		// 增加调度方案调用凭证方案执行时，出现多币种异常给出提示在状态栏
            		runStatus.appendSchRunDesc("\r\n[CuryCheck_start]" + 
            				"凭证编号为【" + rs.getString("FVchNum") + 
            				"】、凭证模版编号为【" + rs.getString("FVchTplCode") + 
            				"】、凭证模版名称为【" + rs.getString("FVchTplName") +
                            "】、分录编号为【" + rs.getString("FEntityNum") + 
                            "】出现一张凭证有多个币种！[CuryCheck_end]\r\n");
            		// 560 QDV4工银2011年1月26日01_A edit by qiuxufeng 20110228 end
            		  //add by lidaolong 20110408 #665 凭证导入时检查凭证币种，弹出提示窗口
            		//modify by guyichuan 20110505 STORY #665 提示的显示内容须修改
            		if(isCuryCheck){
            			strInfo ="\r\n  凭证编号为【" + rs.getString("FVchNum") +
        				"】、分录编号为【" + rs.getString("FEntityNum") + 
        				"】、 科目为【" + rs.getString("FAcctName") +
                        "】、币种为【" + rs.getString("FCurCode") +
                        "】、凭证摘要为【" + rs.getString("FResume") +
                        "】的凭证出现一张凭证有多个币种的情形！";
            			return "";
            		}//end by lidaolong
                }
        	}
        	//--- add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	if(logSumCode.trim().length() > 0){
        		logInfo +=  "      检查一张凭证是否有多币种完成！\r\n";
        		if(logSumCode.trim().length() >0 ){
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查一张凭证是否有多币种", pub, false, this.sportCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), logInfo, 
            					logStartTime, logSumCode, new java.util.Date());
            		}
        		}
        	}
			//--- add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			
            runStatus.appendRunDesc("VchRun", "      检查一张凭证是否有多币种完成！\r\n");
            runStatus.appendSchRunDesc("        `凭证多币种检查完成");// 560 QDV4工银2011年1月26日01_A by qiuxufeng 20110228
            return reStr;
        } catch (Exception e) {
        	//--- add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		if(logSumCode.trim().length() >0 ){
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查一张凭证是否有多币种", pub, true, this.sportCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), 
            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
            					(logInfo + "\r\n检查一张凭证是否有多币种出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
            					.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""), 
            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
            					logStartTime, logSumCode, new java.util.Date());
            			
            			//---add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A start---//
            			if(this.comeFromDAO){//如果是通过凭证接口调用
                			logOper.setDayFinishIData(this, 25, "sum", pub, true, this.sportCode, 
                					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
                					YssFun.toDate(this.endDate), 
                					(logInfo + "\r\n检查一张凭证是否有多币种出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
                					.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""), 
                					logStartTime, logSumCode, new java.util.Date());
            			}
            			//---add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A end---//
            		}
        		}
        		
        		runStatus.appendRunDesc("VchRun", "检查失败！" + e.getMessage());
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//--- add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
            finally{//添加 finally 保证可以抛出异常
            	throw new YssException("检查一张凭证是否有多币种出错！", e);
            }
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
        	//add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A
        	this.comeFromDAO = false;
            dbl.closeResultSetFinal(rs, vchrs);
        }
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
