package com.yss.webServices.operdeal.plugin.valuation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

import com.yss.core.util.YssCons;
import com.yss.projects.para.set.pojo.BEN_PLUGIN_PRODUCE;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.webServices.operdeal.WarnPluginBaseBean;

//story 1924 create by zhouwei 20120214  检查TA交易数据已结算	QDV4赢时胜(上海开发部)2011年11月25日01_A
public class WpTaSettleDataBean extends WarnPluginBaseBean {

	//预警操作
	public void doOperation(){
		ResultSet rs=null;
		String group="";
		String port="";
		String sql="";
		StringBuffer sb=new StringBuffer();
		try{
			setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_NORMAL, "正常");
//			int days=YssFun.dateDiff(plugPro.getOperDate_Begin(), plugPro.getOperDate_End());
			Date date=plugPro.getOperDate_End();
			curDate=YssFun.formatDate(date);
			for(int j=0;j<groupPorts.length;j++){
				group=groupPorts[j].split("-")[0];//组合群
				port=groupPorts[j].split("-")[1];//组合信息
				pub.setPrefixTB(group);
				//ta未结算数据
			    sql="select * from "+pub.yssGetTableName("Tb_TA_Trade")+" where FPORTCODE="
				   +dbl.sqlString(port)+" and fcheckstate=1 and FSettleDate <="+dbl.sqlDate(date)
				   +" and FSETTLESTATE=0";
				rs=dbl.openResultSet(sql);
				if(!rs.next()){
					sb.append(curDate+"日,"+port+"组合,TA交易数据尚未结算，请注意！\n");
					plugPro.setC_PRODUCE_STATE("已完成");
					plugPro.setC_RESULT_STATE(YssCons.RESULT_STATE_WARN);
					plugPro.setC_RESULT_INFO(sb.toString());
				}							    
				dbl.closeResultSetFinal(rs);
				this.getPortRelaInfo(group, port);
			}			
		
		}catch (Exception e) {
			log.error(" 检查TA交易数据已结算预警失败");
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
