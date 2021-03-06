package com.yss.main.operdeal.opermanage.etf;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.util.YssException;

/**
 * ETF业务在此进行统一处理，包括：
 * 现金差额和现金替代的结转、生成可退替代款的应收应付、生成应付替代款的现金应收应付、应付替代款的结转、备付金结转等
 * @author Administrator
 *
 */
public class ETFBaseOperManage extends BaseOperManage {

	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.sPortCode = portCode;
		this.dDate = dDate;
	}

	public void doOpertion() throws YssException {
		BaseOperManage opermanage = null;
		String[] sType = null;
		try{
			/**shashijie 2011-10-27 BUG 2979 */
			//判断是否是ETF组合
			if (isHaveETFPortCode(this.sPortCode)) {
				sType = new String[]{"standingbook",//生成台帐数据
									"updateRetnDate",//根据非交收日更新台帐退款日期
									"insBalTrans",//结转现金替代和现金差额
									"insteadDues",//生成、结转应付替代款
									"insRetnable",//生成可退替代款应收应付
									"supplyorinvest",//拆分主动投资和补票数据
									"etfvaluation",//计算ETF基金估值增值
									"pretrademoney",//产生预提交易收入的现金应收应付数据
									"clearAccTrans"};//结转备付金
				
				for (int i = 0; i < sType.length; i++) {
					opermanage = (BaseOperManage) pub.getOperDealCtx().getBean(sType[i]);
					opermanage.setYssPub(pub);
					opermanage.initOperManageInfo(dDate, sPortCode);
					opermanage.doOpertion();
				}
				//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
				//当日产生数据，则认为有业务。
			}else{
				this.sMsg="        当日无业务";
			}
		}catch(Exception e){
			throw new YssException("ETF业务处理失败！",e);
		}
	}

	/**判断是否是ETF组合
	 * @return
	 * @author shashijie ,2011-10-27 , BUG 2972 业务处理时如果查询到没有相关参数，系统报错 
	 * @modified
	 */
	private boolean isHaveETFPortCode(String PortCode) throws YssException {
		boolean falg = false;
		StringBuffer bufSql = new StringBuffer();
        ResultSet rs = null;
        try{
        	if (PortCode==null) {
				return falg;
			}
            bufSql.append("SELECT FPortCode FROM ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
            		.append(" WHERE FPortCode IN ( " ).append(operSql.sqlCodes(PortCode))
            		.append(") AND FAssetType = '01' AND FSubAssetType = '0106' ");
            rs = dbl.queryByPreparedStatement(bufSql.toString());
            if (rs.next()){
            	falg = true;
            }
            return falg;
        }catch(Exception e){
            throw new YssException("获取ETF组合出错！", e);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
	}

}
