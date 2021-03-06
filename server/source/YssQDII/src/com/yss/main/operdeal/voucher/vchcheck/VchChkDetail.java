package com.yss.main.operdeal.voucher.vchcheck;

import java.sql.*;

import com.yss.log.SingleLogOper;
import com.yss.util.*;

/**
 *
 * <p>Title: VchChkDetail</p>
 * <p>Description: 检查科目是否为明细科目</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author sj
 * @version 1.0
 */
public class VchChkDetail
    extends BaseVchCheck {
    public VchChkDetail() {
    }

    /**
     * 检查科目是否为明细科目
     * @throws YssException
     */
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
        	logInfo += "      开始检查生成的凭证科目是否为非明细科目... ...\r\n";
        	
            runStatus.appendRunDesc("VchRun", "      开始检查生成的凭证科目是否为非明细科目... ...");
            bookSets = getBookSet(this.sportCode);
            //add by licai 20101210 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
            if(bookSets==null){
            	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	if(logSumCode.trim().length() > 0){
            		logInfo += "        组合【"+sportCode+"】的套帐代码不存在！\r\n";
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查凭证科目是否为明细", pub, false, this.sportCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), logInfo, 
            					logStartTime, logSumCode, new java.util.Date());
            		}
            	}
    			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            	
            	runStatus.appendRunDesc("VchRun", "        组合【"+sportCode+"】的套帐代码不存在！\r\n");
            	reStr="false";
            	return reStr;
            }
          //add by licai 20101210 BUG #541==========================================================================end
            for (int booksets = 0; booksets < bookSets.length; booksets++) {

            	//20120401 modified by liubo.Bug #4109
            	//要求在显示检查时将检查到的错误数据明显到分录
            	//======================================
                sqlStr = "select D.*,tpl.FVchTplName from (select   Vch.* " +
                    " from (select a.FVchNum      as FVchNum, " +
                    " a.FVchTplCode  as FVchTplCode," +
                    " a.FBookSetCode as FBookSetCode," +
                    " b.FEntityNum as FEntityNum," +
                    " b.FSubjectCode as FSubjectCode" +
                    " from (select * " +
                    " from " + pub.yssGetTableName("Tb_vch_data") +
                    //" where FCheckState <> 2 and " +
                   //edit by licai 20101210 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示
                    " where " + //这里只算已审核的数据 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
                   //edit by licai 20101210 BUG #541 ==========================================end
                    //--------------------------------------------------
                    " FPortCode = " + dbl.sqlString(this.sportCode) + " and " +
                    //--------------------------------------------------
                    " FVchDate between " + dbl.sqlDate(this.beginDate) +
                    " and " + dbl.sqlDate(this.beginDate) +
                    " and FVchTplCode in (" +
                    //modify by zhangfa 20101221 BUG #671 凭证生成时，如果有凭证科目未做到最明细，单独勾选该凭证代码生成凭证时系统不会检查。
                    
                    //20120509 modified by liubo.Bug #4400
                    //凭证接口中向财务系统中导入凭证时，无法检查出不为明细科目的凭证
                    //==============================
//                    operSql.sqlCodes(this.isInData ?  this.vchTypes : getVchTpls() ) + //function
                    operSql.sqlCodes(this.isInData ?  getVchTpls() : this.vchTypes) + //function
                    //==============end================
                    
                    //--------------------------end 20101221-------------------------------------------------------------------------
                    ")) a left join (select * from " +
                    pub.yssGetTableName("Tb_Vch_DataEntity") +
                    //" where FCheckState <> 2) b on a.FVchNum = b.FVchNum) Vch where exists (select 1  from " +
                   //edit by licai 20101210 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示
                    " ) b on a.FVchNum = b.FVchNum) Vch where exists (select 1  from " + //这里只算已审核的数据 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
                  //edit by licai 20101210 BUG #541 ===================================end
                    "A" +
                    YssFun.formatDate(YssFun.parseDate(this.beginDate), "yyyy") +
                    bookSets[booksets] + "laccount" +
                    " Acc where FAcctDetail = 0 and acc.Facctcode = Vch.FSubjectCode)) D" +
                    " left join (select FVchTplCode,FVchTplName from " + pub.yssGetTableName("Tb_vch_VchTpl") +
                    //" where FCheckState = 1 and ((FPortCode is null or FPortCode='')or FPortCode='"+sportCode+
                    " where FCheckState = 1 and ((FPortCode is null or FPortCode='' or FPortCode=' ')or FPortCode='" + sportCode +
                    "')) tpl on D.FVchTplCode = tpl.FVchTplCode"; ; //增加对专用组合的处理 by ly 080326 BUG:0000349
                rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                	//---edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                	showInfo = "        凭证编号为【" + rs.getString("FVCHNUM") +
        						"】、凭证模版为【" + rs.getString("FVchTplName") +
        						"】、分录编号为【" + rs.getString("FEntityNum") + 
        						"】、科目代码为【" + rs.getString("FSubjectCode") +
        						"】的凭证科目为非明细科目！\r\n";
                	
                	logInfo += showInfo;
                	
                    runStatus.appendRunDesc("VchRun",showInfo);
                    //---edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    /**add---shashijie 2013-3-30 BUG 7343 调度方案执行时也需要提示*/
                    runStatus.appendSchRunDesc("        凭证编号为【" +
                            rs.getString("FVCHNUM") +
                            "】，" +
                            "凭证模版为【" +
                            rs.getString("FVchTplName") +
                            "】,分录编号为【" + rs.getString("FEntityNum") + "】" + "，科目代码为【" +
                            rs.getString("FSubjectCode") +
                            "】的凭证科目为非明细科目！" + "\r\n");
					/**end---shashijie 2013-3-30 BUG 7343*/
                    reStr = "false";
                }
            	//====================end==================
            }
        	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            if(logSumCode.trim().length() > 0){
            	logInfo += "      凭证科目是否为明细检查完成！\r\n";
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, 24, "检查凭证科目是否为明细", pub, false, this.sportCode, 
        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
        					YssFun.toDate(this.endDate), logInfo, 
        					logStartTime, logSumCode, new java.util.Date());
        		}
            }
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	
            runStatus.appendRunDesc("VchRun", "      凭证科目是否为明细检查完成！\r\n"); //添加回车换行符 by leeyu 20090219 QDV4赢时胜（上海）2009年02月16日01_B  MS00247
            dbl.closeResultSetFinal(rs);
            return reStr;
        } catch (Exception e) {
        	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		if(logSumCode.trim().length() > 0){
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查凭证科目是否为明细", pub, true, this.sportCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), 
            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
            					(logInfo + "\r\n检查凭证科目是否为明细出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
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
            	throw new YssException("检查科目是否为明细出错！", e);
            }
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
