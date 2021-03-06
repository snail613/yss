package com.yss.webServices.operdeal.plugin.ta;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

import com.yss.core.util.YssCons;
import com.yss.projects.para.set.pojo.BEN_PLUGIN_PRODUCE;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.webServices.operdeal.WarnPluginBaseBean;

//story 1924 create by zhouwei 交易日无 TA数据提醒： 	QDV4赢时胜(上海开发部)2011年11月25日01_A
public class WpTaTradeDataBean extends WarnPluginBaseBean {

	//预警操作
	public void doOperation(){
		ResultSet rs=null;
		String group="";
		String port="";
		StringBuffer sb=new StringBuffer();
		try{
			setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_NORMAL, "正常");
			int days=YssFun.dateDiff(plugPro.getOperDate_Begin(), plugPro.getOperDate_End());
			for(int i=0;i<=days;i++){
				//edit by hongqingbing 20130827 BUG 9145  判断有没有TA数据，应该拿该组合的确认日期作为查询条件
				Date date=YssFun.addDay(plugPro.getOperDate_Begin(), i-1);
				curDate=YssFun.formatDate(date);
				for(int j=0;j<groupPorts.length;j++){
					group=groupPorts[j].split("-")[0];//组合群
					port=groupPorts[j].split("-")[1];//组合信息
					pub.setPrefixTB(group);
					//查询当前日期是否为节假日 
					String sql="select  a.* from "+pub.yssGetTableName("Tb_TA_CashSettle")+" a left join Tb_Base_ChildHoliday b"
							  +" on a.fholidayscode=b.fholidayscode where a.fcheckstate=1 and b.fcheckstate=1"
							  +" and a.fportcode="+dbl.sqlString(port)+" and b.fdate="+dbl.sqlDate(date);
					rs=dbl.openResultSet(sql);
					if(!rs.next()){//当前日期是工作日
						dbl.closeResultSetFinal(rs);
						sql="select FPORTCODE  from "+pub.yssGetTableName("Tb_TA_Trade")
						    +" where FPORTCODE="+dbl.sqlString(port)
						    +" and fcheckstate=1 and FTRADEDATE ="+dbl.sqlDate(date);
						rs=dbl.openResultSet(sql);
						if(!rs.next()){
							sb.append(curDate+"日,"+port+"组合无TA数据，请注意\n");
							plugPro.setC_PRODUCE_STATE("已完成");
							plugPro.setC_RESULT_STATE(YssCons.RESULT_STATE_WARN);
							plugPro.setC_RESULT_INFO(sb.toString());							
						}
					}				    
					dbl.closeResultSetFinal(rs);
					this.getPortRelaInfo(group, port);
				}			
			}			
		}catch (Exception e) {
			log.error("交易日TA数据预警失败");
			e.printStackTrace();
		}finally{
			dbl.closeResultSetFinal(rs);
			pub.setPrefixTB(this.prefixTag);
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
