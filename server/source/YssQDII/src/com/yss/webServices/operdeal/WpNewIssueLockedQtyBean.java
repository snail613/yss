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
//story 1925 by zhouwei新股锁定期数量检查 ； 
public class WpNewIssueLockedQtyBean extends WarnPluginBaseBean {

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
				//判断新股锁定期数量是否超过持仓数量（中签数量）
				String sql="select a.* from (select * from "+yssGetTableName("Tb_Data_Newissuetrade", group)
				+" where fcheckstate=1 and ftradetypecode='44') a left join (select * from   "+yssGetTableName("Tb_Data_Newissuetrade", group)
				+" where fcheckstate=1 and ftradetypecode='45') b on a.fnum=b.fnum"
				+" where nvl(a.famount,0)<nvl(b.famount,0)"
				+" and a.FSECURITYTYPE='GP'"//股票
				+" and b.flockbegindate<="+dbl.sqlDate(date)
				+" and b.flockenddate>="+dbl.sqlDate(date);
				rs=dbl.openResultSet(sql);
				while(rs.next()){
					//符合预警的条件
					String securityCode=rs.getString("FSECURITYCODE");
					curDate=YssFun.formatDate(date);
					sb.append(curDate+"日,"+port+"组合,"+securityCode+"证券锁定数量与持仓数量异常，请注意！\n");
					setPlugProInfoToClient("已完成", "提醒",sb.toString());										
				}													
				dbl.closeResultSetFinal(rs);				
			
			}						
		}catch (Exception e) {
			log.error("新股锁定期数量检查失败");
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
