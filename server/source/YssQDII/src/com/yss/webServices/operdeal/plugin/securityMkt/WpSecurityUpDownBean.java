package com.yss.webServices.operdeal.plugin.securityMkt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import com.yss.core.util.YssCons;
import com.yss.projects.para.set.pojo.BEN_PLUGIN_PRODUCE;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.webServices.operdeal.WarnPluginBaseBean;
//story 1925 by zhouwei 投资不同类型证券的行情涨跌幅超出范围时给予提示； 
public class WpSecurityUpDownBean extends WarnPluginBaseBean {

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
			setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_NORMAL, " ");
//			int days=YssFun.dateDiff(plugPro.getOperDate_Begin(), plugPro.getOperDate_End());
//			for(int i=0;i<=days;i++){}	
			Date date=plugPro.getOperDate_Begin();
			curDate=YssFun.formatDate(date);
			pub.setPrefixTB(group);
			int len=list.size();
			for(int j=0;j<len;j++){
				//遍历项目：股票、债券、非货币基金、权证、期货、期权；
				pp=(BEN_PLUGIN_PRODUCE)list.get(j);
				String sql="select b.FPRICE as FYPRICE,a.FPRICE as FPRICE,c.FSecurityCode,c.FSecurityName,c.FCatCode,c.FSubCatCode,d.FCatName,"
						+"(round((a.FPRICE-b.FPRICE)/b.FPRICE,4)*100) as ratio from "+pub.yssGetTableName("Tb_Data_ValMktPrice")
						+" a left join "+pub.yssGetTableName("Tb_Data_ValMktPrice")+" b on a.FPORTCODE=b.FPORTCODE and a.FSECURITYCODE=b.FSECURITYCODE"
						//edit by songjie 2012.08.15 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加 and 
						+" and a.FVALDATE=b.FVALDATE+1"
						+" left join "+pub.yssGetTableName("Tb_Para_Security")
						+" c on a.FSecurityCode=c.FSecurityCode left join Tb_Base_Category d"
						+" on c.FCatCode=d.FCatCode"
						+" where a.FPORTCODE="+dbl.sqlString(port)
						+" and a.FVALDATE="+dbl.sqlDate(date)
						+" and b.FPRICE>0"
						+" and "+this.buildFilterSql("(round((a.FPRICE-b.FPRICE)/b.FPRICE,4)*100)", pp)
						+" and c.FCatCode="+dbl.sqlString(pp.getC_PLUGIN_ITEM().toUpperCase())
					    +" and c.FSubCatCode<>'TR03'";
				rs=dbl.openResultSet(sql);
				while(rs.next()){
					//符合预警的条件
					double ratio=rs.getDouble("ratio");
					if(ratio>=0){//涨幅
						sb.append(curDate+"日,"+port+"组合,"+rs.getString("FCatName")+"类型资产中"+rs.getString("FSecurityCode")+"证券，涨跌幅为"+ratio+"%,超过预警值！\n");
						plugPro.setC_PRODUCE_STATE("已完成");
						plugPro.setC_RESULT_STATE(YssCons.RESULT_STATE_WARN);
						plugPro.setC_RESULT_INFO(sb.toString());
						setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_WARN,sb.toString());
					}						
				}													
				dbl.closeResultSetFinal(rs);				
			}
			this.getPortRelaInfo(group, port);
		}catch (Exception e) {
			log.error("证券涨跌幅预警失败");
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
