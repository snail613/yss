package com.yss.main.operdeal.voucher.vchcheck;

import java.sql.*;

import com.yss.log.SingleLogOper;
import com.yss.util.*;

/**
 *
 * <p>Title: VchChkBalance</p>
 * <p>Description: 检查凭证的借贷是否平衡</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author sj
 * @version 1.0
 */
public class VchChkBalance
    extends BaseVchCheck {
    public VchChkBalance() {
    }

    /**
     * 检查凭证的借贷是否平衡
     * @throws YssException
     */
    public String doCheck() throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String reStr = "true";
        
        //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        java.util.Date logStartTime = new java.util.Date(); 
		if(logOper == null){//添加非空判断
			logOper = SingleLogOper.getInstance();
		}
        String logInfo = "";
        //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            //aryPorts = this.portCodes.split(",");
            //for (int ports = 0;ports < aryPorts.length;ports++)
            //{
        	
        	//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        	logInfo += "      开始检查借贷平衡... ...\r\n";
        	
            runStatus.appendRunDesc("VchRun", "      开始检查借贷平衡... ...");
            sqlStr = "select VchData.*,Tpl.FVchTplName from " +
                "(select FVchNum, FVchTplCode,FVChDate,FPortCode,FBookSetCode, " +
                " FCuryCode, FSrcCury," + //sum(SFBal) as FSumData,
                " sum(SFSetBal) as FSetSumData " +		
                " from (select FVchNum,FVchTplCode,FVChDate,FPortCode, FBookSetCode, " +
                " FCuryCode, FSrcCury, EFDCWay," +		
                //" (case when EFDCWay = 0 then sum(EFBal) else sum(EFBal) * -1 end) as SFBal," +
                " (case when EFDCWay = '0' then sum(EFSetBal) else sum(EFSetBal) * -1  end) as SFSetBal " +
                " from (select Vch.*,  Entity.FCuryRate as EFCuryRate, " +
                " Entity.FBookSetCode as EFBookSetCode, Entity.FBal as EFBal, Entity.FSetBal as EFSetBal, " +
                " Entity.FAmount  as EFAmount, Entity.FAssistant   as EFAssistant, Entity.FPrice as EFPrice," +
                " Entity.FSubjectCode as EFSubjectCode, Entity.FResume as EFResume,Entity.FDCWay as EFDCWay " +
                "  from (select * from " + pub.yssGetTableName("Tb_Vch_Data") +
                " where FVchDate between " + dbl.sqlDate(this.beginDate) + " and " + dbl.sqlDate(this.endDate) +
                " and FPortCode = " + dbl.sqlString(this.sportCode) +
                " and FVchTplCode in (" + operSql.sqlCodes(this.isInData ? getVchTpls() : this.vchTypes) + ")" +
                " and FCheckState <> 2 ) " +//edit by songjie 2011.03.30 BUG:QDV4中银基金2011年03月25日01_B
                //delete by songjie 2011.03.30 BUG:QDV4中银基金2011年03月25日01_B
//                " and FCheckState =1 ) " + //这里只算已审核的数据　QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
                " Vch left join (select * from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                //" where FCheckState <> 2) Entity on Vch.FVchNum = Entity.FVchNum) Balance " +
                " where FCheckState =1) Entity on Vch.FVchNum = Entity.FVchNum) Balance " + //这里只算已审核的数据 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
                " group by FVchNum, FVchTplCode, FVChDate, FPortCode,FBookSetCode, FCuryCode, FSrcCury,EFDCWay) SData " + 	//20120401 modified by liubo.Bug #4109
                " group by FVchNum, FVchTplCode, FVChDate, FPortCode, FBookSetCode, FCuryCode,FSrcCury" +		//20120401 modified by liubo.Bug #4109
                " having " + //sum(SFBal) <> 0 and
                " sum(SFSetBal) <> 0 ) VchData " +
                " left join (select FVchTplCode,FVchTplName from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                //" where FCheckState = 1 and ((FPortCode is null or FPortCode ='')or FPortCode='"+sportCode+
                " where FCheckState = 1 and ((FPortCode is null or FPortCode ='' or FPortCode=' ')or FPortCode='" + sportCode +
                "')) Tpl on VchData.FVchTplCode = Tpl.FVchTplCode"; //增加对专用组合的处理 BUG:0000349
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	logInfo += "        凭证编号为【" + rs.getString("FVCHNUM") + "】，" +
                "凭证模版为【" + rs.getString("FVchTplName") + "】的借贷平衡出错！\r\n";
            	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            	
                runStatus.appendRunDesc("VchRun", "        凭证编号为【" + rs.getString("FVCHNUM") + "】，" +
                                        "凭证模版为【" + rs.getString("FVchTplName") + "】的借贷平衡出错！\r\n");
                reStr = "false";
            }
            
            //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            if(logSumCode.trim().length() > 0){
            	logInfo += "      借贷平衡检查完成！\r\n";
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, 24, "检查凭证借贷平衡",pub, false, this.sportCode, 
        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
        					YssFun.toDate(this.endDate), logInfo, logStartTime, 
        					logSumCode, new java.util.Date());
        		}
            }
        	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			
            runStatus.appendRunDesc("VchRun", "      借贷平衡检查完成！\r\n");
            dbl.closeResultSetFinal(rs);
            return reStr;
            //}
        } catch (Exception e) {
        	//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		if(logSumCode.trim().length() > 0){
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 24, "检查凭证借贷平衡",
            					pub, true, this.sportCode, YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.endDate), 
            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
            					(logInfo + "\r\n检查借贷平衡出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
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
            	throw new YssException("检查借贷平衡出错！", e);
            }
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
