package com.yss.webServices.operdeal.plugin.valCheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.core.util.YssCons;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.webServices.operdeal.WarnPluginBaseBean;


/**shashijie 2012-12-26 STORY 3254 交易数据的成交金额+-费用后，要等于该笔交易的实际结算金额
 * @author shashijie 
 *
 */
public class TotalCostMoney extends WarnPluginBaseBean {
	//预警执行
	public void doOperation() {
		String group = "";
		String port = "";
		
		try {
			setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_NORMAL, "正常");
			int days = YssFun.dateDiff(plugPro.getOperDate_Begin(), plugPro.getOperDate_End());
			//日期
			for (int i = 0; i <= days; i++) {
				Date date = YssFun.addDay(plugPro.getOperDate_Begin(), i);
				//当前处理日期
				curDate = YssFun.formatDate(date);
				//组合
				for (int j = 0; j < groupPorts.length; j++) {
					group = groupPorts[j].split("-")[0];// 组合群
					port = groupPorts[j].split("-")[1];// 组合信息
					pub.setPrefixTB(group);
					//查询当前日期的交易数据
					doOperion(port,YssFun.toDate(curDate));
					//获取组合的相关信息，如 资产类型，套账号等
					this.getPortRelaInfo(group, port);
				}
			}
		} catch (Exception e) {
			log.error("交易数据预警失败");
			e.printStackTrace();
		} finally {
			pub.setPrefixTB(this.prefixTag);
			if (this.sessionId.equals("")) {
				try {
					dbl.closeConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**shashijie 2012-12-26 STORY 3254 查询当前日期的交易数据*/
	private void doOperion(String port, Date date) {
		String info = "";//提示信息
		ResultSet rs = null; 
		try {
			String query = getQueryTrade(port,date);
			rs = dbl.openResultSet(query);
			while(rs.next()) {
				info += curDate + "日," + port + "组合,交易编号为"+rs.getString("Fnum")+",交易证券为"+
					rs.getString("Fsecuritycode")+",交易方式为"+rs.getString("Ftradetypecode")+
					",的交易数据:成交金额+应计利息+-费用 不等于 实收实付金额，请注意!\n";
			}
			//提示
			if (info.trim().equals("")) {
				setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_NORMAL, "正常");
			} else {
				setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_WARN, info);
				/*plugPro.setC_PRODUCE_STATE("已完成");
				plugPro.setC_RESULT_STATE(YssCons.RESULT_STATE_WARN);
				plugPro.setC_RESULT_INFO(info);*/
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2012-12-26 STORY 3254 获取查询当前日期的交易数据的SQL */
	private String getQueryTrade(String port, Date dDate) {
		String sqlString = "Select a.Fbargaindate, /*交易日期*/" +
				" a.Ffactsettlemoney, /*实际结算金额*/" +
				" a.Ftotalcost, /*实收实付金额*/" +
				" a.Ftrademoney, /*成交金额*/" +
				" a.Faccruedinterest, /*应计利息*/" +
				" a.Ftradefee1," +
				" a.Ftradefee2," +
				" a.Ftradefee3," +
				" a.Ftradefee4," +
				" a.Ftradefee5," +
				" a.Ftradefee6," +
				" a.Ftradefee7," +
				" a.Ftradefee8," +
				" a.Fnum,a.Fsecuritycode, Case When a.Ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_JYLX_Buy)+
				" Then '买入' Else '卖出' " +
				" End As Ftradetypecode From " +
				pub.yssGetTableName("Tb_Data_Subtrade")+" a Where a.Fcheckstate = 1" +
				" And a.fportcode = "+dbl.sqlString(port)+
				" And a.Fbargaindate = "+dbl.sqlDate(dDate)+
				" And a.Ftradetypecode In ("+
				operSql.sqlCodes(YssOperCons.YSS_JYLX_Buy+","+YssOperCons.YSS_JYLX_Sale)+")"+//买入卖出
				" And a.Ftotalcost <> Case When a.Ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_JYLX_Buy)+" Then"+
				//若是买入则+费用
				" a.Ftrademoney + a.Faccruedinterest + a.Ftradefee1 + a.Ftradefee2 + a.Ftradefee3 + " +
				" a.Ftradefee4 + a.Ftradefee5 + a.Ftradefee6 + a.Ftradefee7 + a.Ftradefee8 Else"+
				//卖出则-费用
				" a.Ftrademoney + a.Faccruedinterest - a.Ftradefee1 - a.Ftradefee2 - a.Ftradefee3 - " +
				" a.Ftradefee4 - a.Ftradefee5 - a.Ftradefee6 + a.Ftradefee7 - a.Ftradefee8 End";
		return sqlString;
	}

}
