package com.yss.webServices.operdeal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.yss.core.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

//story 1925 create by zhouwei 20120215   债券到期日提醒	QDV4赢时胜(上海开发部)2011年11月25日02_A
public class WpDueFixInterestBean extends WarnPluginBaseBean {

	//预警操作
	public void doOperation(){
		ResultSet rs=null;
		String group="";
		String port="";
		String sql="";
		StringBuffer sb=new StringBuffer();
		Map assetGroup=new HashMap();
		try{
			setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_NORMAL, "正常");
			int days=YssFun.dateDiff(plugPro.getOperDate_Begin(), plugPro.getOperDate_End());
			for(int i=0;i<=days;i++){
				Date date=YssFun.addDay(plugPro.getOperDate_Begin(), i);
				for(int j=0;j<groupPorts.length;j++){
					group=groupPorts[j].split("-")[0];//组合群
					port=groupPorts[j].split("-")[1];//组合信息
					if(assetGroup.containsKey(group)){
						continue;
					}
					//计息截至日字段（FInsEndDate）=检查日期-提前天数的证券
				    sql="select select (FInsEndDate-"+dbl.sqlDate(date)+") as difdays,FSecurityCode from "
				       +yssGetTableName("Tb_Para_FixInterest", group)
				       +" where fcheckstate=1 and abs(FInsEndDate-"+dbl.sqlDate(date)+")="+plugPro.getC_PLUGIN_VALUE();
					rs=dbl.openResultSet(sql);
					while(rs.next()){
						plugPro.setC_PRODUCE_STATE("已完成");
						plugPro.setC_RESULT_STATE(YssCons.RESULT_STATE_WARN);
						curDate=YssFun.formatDate(date);
						if(rs.getInt("difdays")>=0){
							sb.append(curDate+"日,"+rs.getString("FSecurityCode")+"证券距到期日尚有"+plugPro.getC_PLUGIN_VALUE()+"日，请注意！\n");
						}else{
							sb.append(curDate+"日,"+rs.getString("FSecurityCode")+"证券距到期日已过"+plugPro.getC_PLUGIN_VALUE()+"日，请注意！\n");
						}						
						plugPro.setC_RESULT_INFO(sb.toString());
					}							    
					dbl.closeResultSetFinal(rs);
				}	
			}		
		
		}catch (Exception e) {
			log.error("交易日TA数据预警失败");
			e.printStackTrace();
		}finally{
			dbl.closeResultSetFinal(rs);
			if(this.sessionId.equals("")){
				try{
					dbl.closeConnection();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}	
		}
	}

}
