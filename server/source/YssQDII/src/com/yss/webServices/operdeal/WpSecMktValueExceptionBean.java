package com.yss.webServices.operdeal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import com.yss.projects.para.set.pojo.BEN_PLUGIN_PRODUCE;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
//story 1925 by zhouwei行情异常提示 
public class WpSecMktValueExceptionBean extends WarnPluginBaseBean {

	public void doOperation(){

		this.plugPro=(BEN_PLUGIN_PRODUCE) list.get(0);
		ResultSet rs=null;
		String curDate=YssFun.formatDate(plugPro.getOperDate_Begin(),"yyyy-MM-dd");
		String[] groupPorts=plugPro.getC_PORT_CODE().split("-");//组合-组合群
		String group=groupPorts[0];//组合群
		String port=groupPorts[1];//组合信息
		StringBuffer sb=new StringBuffer();
		BEN_PLUGIN_PRODUCE pp=null;
		try{
			setPlugProInfoToClient("已完成", "正常", " ");
			int days=YssFun.dateDiff(plugPro.getOperDate_Begin(), plugPro.getOperDate_End());
			for(int i=0;i<=days;i++){
				Date date=YssFun.addDay(plugPro.getOperDate_Begin(), i);
				//在交易日无行情数据提醒或在节假日有行情数据提醒： 
				String sql="select * from (select a.fholidayscode,a.fsecuritycode,b.fcatname,d.fexchangename,(case when c.ftype is null"
				+" then '-1' when c.ftype=0 then (case when e.FClosingPrice is null then '0' else '2' end) else"
				+" (case when e.FClosingPrice is not null then '1' else '2' end) end) as fmark from "
				+yssGetTableName("Tb_Para_Security", group)+" a"
				+" left join (select * from Tb_Base_Category where fcheckstate=1) b on a.FCatCode=b.fcatcode"
				+" left join (select * from Tb_Base_ChildHoliday where FCheckState=1 and fdate="+dbl.sqlDate(date)+") c on a.FHolidaysCode=c.fholidayscode"
				+" left join (select * from Tb_Base_Exchange where fcheckstate=1) d on a.fexchangecode=d.fexchangecode"
				+" left join (select * from "+yssGetTableName("Tb_Data_MarketValue", group)
				+"  where fcheckstate=1 and FMktValueDate= "+dbl.sqlDate(date)+") e on a.fsecuritycode=e.fsecuritycode"
				+" where a.fstartdate<"+dbl.sqlDate(date)+" and a.fcheckstate=1) aa where aa.fmark<>'2' order by fmark";
				rs=dbl.openResultSet(sql);
				while(rs.next()){
					//符合预警的条件
					String securityCode=rs.getString("FSECURITYCODE");
					String exchangeName=rs.getString("fexchangename");
					String catName=rs.getString("fcatname");
					String mark=rs.getString("fmark");
					String holidyCode=rs.getString("fholidayscode");
					curDate=YssFun.formatDate(date);
					if(mark.equals("-1")){//无该年份的节假日群
						sb.append("节假日群"+holidyCode+"没有"+curDate.substring(0, 3)+"年的设置\n");	
					}else if(mark.equals("0")){//在交易日无行情数据提醒
						sb.append(curDate+"日，在"+exchangeName+"交易所，"+catName+"品种,"+securityCode+"证券交易日无行情数据，请注意！\n");
					}else{//在节假日有行情数据提醒
						sb.append(curDate+"日，在"+exchangeName+"交易所，"+catName+"品种,"+securityCode+"证券节假日有行情数据，请注意！\n");
					}	
					setPlugProInfoToClient("已完成", "提醒",sb.toString());										
				}													
				dbl.closeResultSetFinal(rs);					
			}						
		}catch (Exception e) {
			log.error("行情异常提示失败");
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
