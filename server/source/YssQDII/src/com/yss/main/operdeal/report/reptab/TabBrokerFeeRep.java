package com.yss.main.operdeal.report.reptab;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.BrokerFeeRepBean;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssReflection;

/**券商股票佣金跨组合群佣金统计 add by licai MS01668
 * @author lc
 *
 */
public class TabBrokerFeeRep extends BaseBuildCommonRep {
	private Date startDate;
	private Date endDate;
	private double totaltradefee1Hj;
	private double totaltrademoneyHj;
	private HashMap brokerTradeMoneyAndFee=new HashMap();//总佣金和总交易量
	private HashMap portCodePerAssetGroup=new HashMap();
	private HashMap tradeMoneyAndTradeFeePerBroker=new HashMap();
	
	public  void initBuildReport(BaseBean bean) throws YssException {
		String[] sAsset_portCodes =null;
		
        CommonRepBean repBean=(CommonRepBean) bean;
		String[] reqAry = null;
		reqAry = repBean.getRepCtlParam().split("\n");
		
		for(int i=0;i<reqAry.length;i++){
			int paramIndex=Integer.parseInt(reqAry[i].split("\r")[0]);
			switch(paramIndex){
			case 1:{
				this.startDate=YssFun.toSqlDate(reqAry[i].split("\r")[1]);
//				this.startDate=YssFun.toDate(reqAry[i].split("\r")[1]);
				break;
			}
			case 2:{
				this.endDate=YssFun.toSqlDate(reqAry[i].split("\r")[1]);
//				this.endDate=YssFun.toDate(reqAry[i].split("\r")[1]);
				break;
			}
			//所有的组合群组合串
			case 3:{
				//将串按照 组合群组合分拆
				sAsset_portCodes=reqAry[i].split("\r");	
				//取出需要的"组合群/t组合"串组
				String[]assetGrpCodes=sAsset_portCodes[1].split(",");
				//遍历数组，解析组合群组合对
				for (int j = 0; j < assetGrpCodes.length; j++) {
					// 拆解组合群组合对，取出组合群和组合
					String[] assetGrpCodePortCodePair = assetGrpCodes[j]
							.split("\t");
					//组合群代码
					String assetGrpCode=assetGrpCodePortCodePair[0];
					//组合代码
					String portCode=assetGrpCodePortCodePair[1];
					StringBuffer sb=new StringBuffer();
					if(portCodePerAssetGroup.containsKey(assetGrpCode)){
						sb=(StringBuffer) portCodePerAssetGroup.get(assetGrpCode);
						sb.append(portCode+"\t");
					}else{
						portCodePerAssetGroup.put(assetGrpCode,sb.append(portCode).append("\t"));
					}
				}
		  }
		}
		}
		getBrokerTradeMoneyAndFee();
		getTradeMoneyAndFeePerBroker();
	}

	public String buildReport(String sType) throws YssException {
		String sResult = "";
        ArrayList arrResult = null;
        try {
            arrResult = getBrokerFeeRepDataResultList(); // 得到数据
            sResult = buildRowCompResult(arrResult); //得到含格式的数据  父类中
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        }		

	}

	/**获取报表字段bean列表
	 * @return
	 * @throws YssException
	 */
	protected ArrayList getBrokerFeeRepDataResultList()throws YssException{
		ArrayList repResult=new ArrayList();
		Iterator it=tradeMoneyAndTradeFeePerBroker.keySet().iterator();
		while(it.hasNext()){
			BrokerFeeRepBean bfBean=new BrokerFeeRepBean();
			//券商名
			String brokerName=(String) it.next();
			//券商交易量和佣金
			String tradeMoneyAndFee=(String)tradeMoneyAndTradeFeePerBroker.get(brokerName);
			//拆分交易量和佣金
			String[]tradeMoneyAndFeeArr=tradeMoneyAndFee.split("\t");
			//交易量
			double tradeMoney=Double.parseDouble(tradeMoneyAndFeeArr[0]);
			//佣金
			double tradeFee=Double.parseDouble(tradeMoneyAndFeeArr[1]);
			//设置报表bean属性值
			bfBean.setFbrokershortname(brokerName);
			bfBean.setFtrademoneyHj(YssFun.formatNumber(tradeMoney, "###,###.00"));
			bfBean.setFtradefee1Hj(YssFun.formatNumber(tradeFee, "###,###.00"));
			if (this.totaltrademoneyHj > 0) {
				bfBean.setTrademoneyBl(YssFun.roundIt(
						(tradeMoney / this.totaltrademoneyHj) * 100, 2));
			} else {
				bfBean.setTrademoneyBl(0);
			}
			if (this.totaltradefee1Hj > 0) {
				bfBean.setFYjBl(YssFun.roundIt(
						(tradeFee / this.totaltradefee1Hj) * 100, 2));
			} else {
				bfBean.setFYjBl(0);
			}

			repResult.add(bfBean);
	}
   //add by licai 20101105 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）------
	BrokerFeeRepBean bfTotalBean=new BrokerFeeRepBean();
	bfTotalBean.setFbrokershortname("总计：");
	bfTotalBean.setFtrademoneyHj(YssFun.formatNumber(totaltrademoneyHj, "###,###.00"));
	bfTotalBean.setFtradefee1Hj(YssFun.formatNumber(totaltradefee1Hj, "###,###.00"));
	bfTotalBean.setTrademoneyBl(totaltrademoneyHj>0?100:0);
	bfTotalBean.setFYjBl(totaltradefee1Hj>0?100:0);
	repResult.add(bfTotalBean);
	//add by licai 20101105 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）--end----
	
	return repResult;
 }
	
	/**
     * 将数据与报表数据源传入得到相应格式数据
     * @throws YssException
     */
    protected String buildRowCompResult(ArrayList arrResult) throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";
        RepTabCellBean rtc = null;
        BrokerFeeRepBean bfBean;
        try {
            for (int i = 0; i < arrResult.size(); i++) {
                bfBean = (BrokerFeeRepBean) arrResult.get(i);
                hmCellStyle = getCellStyles("DSBrokerFee");
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                    " where FRepDsCode = " + dbl.sqlString("DSBrokerFee") +
                    " and FCheckState = 1 order by FOrderIndex";
                rs = dbl.openResultSet(strSql);

                while (rs.next()) {
                    //获得样式
                    sKey = "DSBrokerFee" + "\tDSF\t-1\t" +
                        rs.getString("FOrderIndex");

                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");

                    buf.append(YssReflection.getPropertyValue(bfBean,
                        rs.getString("FDsField")) +
                               "\t");
                }
                dbl.closeResultSetFinal(rs);
                if (buf.toString().trim().length() > 1) {
                    strReturn = strReturn + buf.toString().substring(0,
                        buf.toString().length() - 1);
                    buf.delete(0, buf.toString().length());
                    strReturn = strReturn + "\r\n"; //每一行用\r\n隔开
                }

            }
        } catch (Exception e) {
            throw new YssException("获取格式出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return strReturn;
    }
	
	
	/**计算跨组合群券商总交易量和总佣金
	 * @throws YssException
	 */
	protected void getBrokerTradeMoneyAndFee()throws YssException{
//		StringBuffer sb=new StringBuffer();//edit by licai 20101105 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）------
		Iterator it=portCodePerAssetGroup.keySet().iterator();
		while(it.hasNext()){
			ResultSet rs=null;
			String assetGrpCode=(String) it.next();
			StringBuffer portCodes=(StringBuffer) portCodePerAssetGroup.get(assetGrpCode);
			String[]portCodeArr=portCodes.toString().split("\t");
			String portCodeStr="";
			for(int j=0;j<portCodeArr.length;j++){
				portCodeStr+="'"+portCodeArr[j]+"',";
			}			
			portCodeStr="("+portCodeStr.substring(0,portCodeStr.lastIndexOf(","))+")";
			
			StringBuffer sb=new StringBuffer();//add by licai 20101105 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）------
			sb.append("select round(sum(ftrademoney * fexrate1), 2) as ftrademoneyHj,round(sum(round(ftradefee1 * fexrate1, 2)), 2) as ftradefee1Hj");
			sb.append(" from (");
			sb.append(" select a.ftrademoney,a.ftradefee1,b.fbrokershortname,c.fcurycode,a.FBaseCuryRate as fexrate1");
			sb.append(" from tb_");
			sb.append(assetGrpCode);
			sb.append("_data_subtrade a ");
			sb.append(" left join tb_");
			sb.append(assetGrpCode);
			sb.append("_Para_Broker b on a.fbrokercode = b.fbrokercode ");
			sb.append(" left join (select fcashacccode, fcurycode");
			sb.append(" from tb_");
			sb.append(assetGrpCode);//edit by licai 20101109 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）------
			sb.append("_Para_CashAccount");
			sb.append(") c on a.fcashacccode = c.fcashacccode");
			sb.append(" left join (");
			sb.append(" select fcurycode, fexratedate, fexrate1");
			sb.append(" from tb_"+assetGrpCode+"_Data_ExchangeRate");
			sb.append(")d on a.fbargaindate =d.fexratedate and c.fcurycode =d.fcurycode");
			sb.append(" where a.ftradetypecode in('01', '02')and a.fbargaindate");
			sb.append(" between ");
			
			sb.append("to_date('");
			sb.append(this.startDate);
			sb.append("', 'yyyy-MM-dd')");
			sb.append(" and ");
			sb.append("to_date('");
			sb.append(this.endDate);
			sb.append("', 'yyyy-MM-dd')");
			
			sb.append(" and a.FCHECKSTATE = '1'and b.FCHECKSTATE = '1'");
			sb.append(" and a.fportcode in ");
			sb.append(portCodeStr);
			sb.append(")");
			try {
				rs = dbl.openResultSet(sb.toString());
				while (rs.next()) {
					
					this.totaltrademoneyHj +=YssFun.roundIt(rs.getDouble("ftrademoneyHj"), 2);
					this.totaltradefee1Hj += YssFun.roundIt(rs.getDouble("ftradefee1Hj"), 2);
				}
			} catch (SQLException e) {
				throw new YssException(e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
			
			
		}
	}
	
	/**计算每个券商的交易量和佣金
	 * @throws YssException
	 */
	protected void getTradeMoneyAndFeePerBroker()throws YssException{		
//			StringBuffer sb=new StringBuffer();////edit by licai 20101105 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）------
			double ftrademoneyhj=0.0;
			double ftradefee1Hj=0.0;
			Iterator it=portCodePerAssetGroup.keySet().iterator();
			//遍历组合群代码
			while(it.hasNext()){
				ResultSet rs=null;
				String assetGrpCode=(String) it.next();
				StringBuffer portCodes=(StringBuffer) portCodePerAssetGroup.get(assetGrpCode);
				String[]portCodeArr=portCodes.toString().split("\t");
				String portCodeStr="";
				for(int j=0;j<portCodeArr.length;j++){
					portCodeStr+="'"+portCodeArr[j]+"',";
				}
				//构造（‘组合代码’，....）串
				portCodeStr="("+portCodeStr.substring(0,portCodeStr.lastIndexOf(","))+")";
				StringBuffer sb=new StringBuffer();//add by licai 20101105 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）------
				sb.append("select aa.fbrokershortname,aa.ftrademoneyHj,aa.ftradefee1Hj");
				sb.append(" from(");
				//分券商名统计交易量和佣金
				sb.append(" select  fbrokershortname ||'(券商编码:'||fbrokercode||')' || ' 合计' as fbrokershortname,round(sum(ftrademoney * fexrate1),2)as ftrademoneyhj,");//edit by licai 20101109 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）
				sb.append(" round(sum(ftradefee1 * fexrate1),2) as ftradefee1Hj");
				sb.append(" from(");
				sb.append(" select  b.fbrokercode,a.ftrademoney,a.ftradefee1,b.fbrokershortname,c.fcurycode,a.FBaseCuryRate as fexrate1");//edit by licai 20101109 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）
				sb.append(" from tb_");
				sb.append(assetGrpCode);
				sb.append("_data_subtrade a");
				sb.append(" left join tb_");
				sb.append(assetGrpCode);
				sb.append("_Para_Broker b");
				sb.append(" on a.fbrokercode =b.fbrokercode");
				sb.append(" left join (select fcashacccode, fcurycode");
				sb.append(" from tb_");
				sb.append(assetGrpCode);
				sb.append("_Para_CashAccount");
				sb.append(")c ");
				sb.append(" on a.fcashacccode=c.fcashacccode");
				sb.append(" left join (");
				sb.append(" select fcurycode, fexratedate, fexrate1");
				sb.append(" from tb_");
				sb.append(assetGrpCode);
				sb.append("_Data_ExchangeRate");
				sb.append(")d ");
				sb.append(" on a.fbargaindate =d.fexratedate");
				sb.append(" and c.fcurycode =d.fcurycode");
				sb.append(" where a.ftradetypecode");
				sb.append(" in ('01', '02')");
				sb.append(" and a.fbargaindate");
				sb.append(" between ");
				sb.append(" to_date('");
				sb.append(this.startDate);
				sb.append("', 'yyyy-MM-dd')");
				sb.append(" and ");
				sb.append(" to_date('");
				sb.append(this.endDate);
				sb.append("', 'yyyy-MM-dd')");
				sb.append(" and a.FCHECKSTATE = '1'");
				sb.append(" and b.FCHECKSTATE = '1'");
				sb.append(" and a.fportcode in");
				sb.append(portCodeStr);
				sb.append(")ccc");
				sb.append(" group by fbrokercode,fbrokershortname");//edit by licai 20101109 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）
				
				//edit by licai 20101105 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）------
				/*//合计统计
				sb.append(" union ");
				sb.append(" select '总计' as fbrokershortname,sum(round(sum(ftrademoney * fexrate1),2))as ftrademoneyhj,");
				sb.append(" sum(round(sum(ftradefee1 * fexrate1),2))as ftradefee1Hj");
				sb.append(" from(");
				sb.append(" select a.ftrademoney,a.ftradefee1,b.fbrokershortname,c.fcurycode,a.FBaseCuryRate as fexrate1");
				sb.append(" from tb_");
				sb.append(assetGrpCode);
				sb.append("_data_subtrade a");
				sb.append(" left join tb_");
				sb.append(assetGrpCode);
				sb.append("_Para_Broker b");
				sb.append(" on a.fbrokercode =b.fbrokercode");
				sb.append(" left join (select fcashacccode, fcurycode");
				sb.append(" from tb_");
				sb.append(assetGrpCode);
				sb.append("_Para_CashAccount");
				sb.append(")c ");
				sb.append(" on a.fcashacccode=c.fcashacccode");
				sb.append(" left join (");
				sb.append(" select fcurycode, fexratedate, fexrate1");
				sb.append(" from tb_");
				sb.append(assetGrpCode);
				sb.append("_Data_ExchangeRate");
				sb.append(")d ");
				sb.append(" on a.fbargaindate =d.fexratedate");
				sb.append(" and c.fcurycode =d.fcurycode");
				sb.append(" where a.ftradetypecode");
				sb.append(" in ('01', '02')");
				sb.append(" and a.fbargaindate");						
				
				sb.append(" between ");
				sb.append(" to_date('");
				sb.append(this.startDate);
				sb.append("', 'yyyy-MM-dd')");
				sb.append(" and ");
				sb.append(" to_date('");
				sb.append(this.endDate);
				sb.append("', 'yyyy-MM-dd')");
				
				sb.append(" and a.FCHECKSTATE = '1'");
				sb.append(" and b.FCHECKSTATE = '1'");
				sb.append(" and a.fportcode in");
				sb.append(portCodeStr);
				sb.append(")ddd");
				sb.append(" group by fbrokershortname");*/
				//edit by licai 20101105 BUG #325 工银佣金表需求未能按照客户要求实现跨足合群查询功能（10号发布）---end---
				sb.append(")aa");
				try {
					rs=dbl.openResultSet(sb.toString());
					while(rs.next()){
						if(brokerTradeMoneyAndFee==null)
							brokerTradeMoneyAndFee=new HashMap();
						//券商名
						String fbrokershortname=rs.getString("fbrokershortname");
						//交易量
						double tradeMoney = YssFun.roundIt(rs.getDouble("ftrademoneyhj"), 2);
						double tradeFee = YssFun.roundIt(rs.getDouble("ftradefee1Hj"), 2);
						//如果券商名存在,那么取出map里相应的交易量和费用，并累加
						if(tradeMoneyAndTradeFeePerBroker.containsKey(fbrokershortname)){
							String tradeMoneyAndFeeStr=(String) tradeMoneyAndTradeFeePerBroker.get(fbrokershortname);
							String[]tradeMoneyAndFeeArr=tradeMoneyAndFeeStr.split("\t");
							ftrademoneyhj=Double.parseDouble(tradeMoneyAndFeeArr[0])+tradeMoney;
							ftradefee1Hj=Double.parseDouble(tradeMoneyAndFeeArr[1])+tradeFee;
						}else{//如果券商名不存在，那么直接将交易量和费用放进map
							ftrademoneyhj=tradeMoney;
							ftradefee1Hj=tradeFee;
						}
						tradeMoneyAndTradeFeePerBroker.put(fbrokershortname, ftrademoneyhj+"\t"+ftradefee1Hj);
					}
				} catch (SQLException e) {
					throw new YssException(e);
				}finally{
					dbl.closeResultSetFinal(rs);
				}
		   }
		
	}

}
