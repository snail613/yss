package com.yss.main.operdeal.report.repfix;

import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

public class FundNav extends BaseBuildCommonRep {

	private String searchDate = ""; // 日期
	private String strPortCode = ""; // 组合代码
	private String[] aryPortCode = null;
	
	protected String portName = "";// 组合名称
	protected String portCury = "";// 本位币
	protected double netValue = 0;// 资产总净值
	protected double paidCapital = 0;//实收资本
	protected double unitValue = 0;//单位净值
	protected double accumulateUnit = 0;//累计净值
	
	private CommonRepBean repBean;
	private FixPub fixPub = null;

	/**
	 * buildReport 获取报表数据入口 （基类方法的实现）
	 * 
	 * @param sType
	 *            
	 * @return String
	 */
	public String buildReport(String sType) throws YssException {
		String sResult = "";
		sResult = buildShowData();
		return sResult;
	}

	/**
	 * initBuildReport
	 * 
	 * @param bean
	 *            
	 */
	public void initBuildReport(BaseBean bean) throws YssException {
		String reqAry[] = null;
		repBean = (CommonRepBean) bean;
		reqAry = repBean.getRepCtlParam().split("\n"); // 这里是要获得参数
		searchDate = reqAry[0].split("\r")[1];
		strPortCode = reqAry[1].split("\r")[1];
		aryPortCode = strPortCode.split(",");

		fixPub = new FixPub();
		fixPub.setYssPub(pub);
	}

	/**
	 * buildShowData 报表显示的数据
	 * 
	 * @return String
	 */
	protected String buildShowData() throws YssException {

		StringBuffer buf = new StringBuffer();
		StringBuffer strResult = new StringBuffer();
		String str = "";
		try {
			for (int i = 0; i < aryPortCode.length; i++) {
				
				getPortName(aryPortCode[i]);//获取组合名称
				getPortCury(aryPortCode[i]);// 获取本位币
				getNetValue(YssFun.toSqlDate(searchDate), aryPortCode[i]);// 获取资产净值
				getPaidCapital(YssFun.toSqlDate(searchDate), aryPortCode[i]);//获取实收资本
				getUnitValue(YssFun.toSqlDate(searchDate), aryPortCode[i]);// 获取单位净值
				getAccumulateUnit(YssFun.toSqlDate(searchDate), aryPortCode[i]);// 获取资产净值
                
				buf.setLength(0); // 给buf清空
				buf.append(portName).append("\t");
				buf.append(portCury).append("\t");
				buf.append(YssFun.roundIt(netValue, 2)).append("\t");
				buf.append(YssFun.roundIt(paidCapital, 2)).append("\t");
				buf.append(YssFun.roundIt(unitValue, 3)).append("\t");
				buf.append(YssFun.roundIt(accumulateUnit, 3));								
				str = buf.toString();
				strResult.append(this.buildRowCompResult(str)).append("\r\n");
				
				//将这些全局变量设为空值或0，防止对上次循环对下次循环产生影响
				portName = ""; 
				portCury = "";
				netValue = 0;
				paidCapital = 0;
				unitValue = 0;
				accumulateUnit = 0;
			}
					
			return strResult.toString();
		} catch (Exception e) {
			throw new YssException("获取资产净值表： \n" + e.getMessage());
		} finally {
		}
	}
	
	protected void getPortName(String portCode) throws YssException{
		String strSql = "";
		StringBuffer strSqlBuf = new StringBuffer();
		
		String groupCode = "";
		String subPortCode = "";
	    groupCode = portCode.split("-")[0];
	    subPortCode = portCode.split("-")[1];
	    
		ResultSet rs = null;
		try {
			strSqlBuf.append("select FPORTNAME from ");
			strSqlBuf.append("tb_");
			strSqlBuf.append(groupCode);
			strSqlBuf.append("_para_portfolio");
			strSqlBuf.append(" where FportCode= ");
			strSqlBuf.append(dbl.sqlString(subPortCode));
			strSql = strSqlBuf.toString();
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				portName = rs.getString("FPORTNAME");
			}
			rs.close();
		} catch (Exception e) {
			throw new YssException("获取组合名称： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		
	} 
	
	
	protected void getPortCury(String portCode) throws YssException {
		String strSql = "";
		StringBuffer strSqlBuf = new StringBuffer();
		
		String groupCode = "";
		String subPortCode = "";
	    groupCode = portCode.split("-")[0];
	    subPortCode = portCode.split("-")[1];
	    
		ResultSet rs = null;
		try {

			strSqlBuf.append("select FPORTCURY from ");
			strSqlBuf.append("tb_");
			strSqlBuf.append(groupCode);
			strSqlBuf.append("_para_portfolio");
			strSqlBuf.append(" where FportCode= ");
			strSqlBuf.append(dbl.sqlString(subPortCode));
			strSql = strSqlBuf.toString();
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				portCury = rs.getString("FPORTCURY");
			}
			rs.close();
		} catch (Exception e) {
			throw new YssException("获取本位币： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	protected void getNetValue(java.sql.Date dDate, String portCode)
			throws YssException {
		String strSql = "";
		StringBuffer strSqlBuf = new StringBuffer();
		
		String groupCode = "";
		String subPortCode = "";
	    groupCode = portCode.split("-")[0];
	    subPortCode = portCode.split("-")[1];
	    
		ResultSet rs = null;
		try {
			strSqlBuf.append("select fportmarketvalue from ");
			strSqlBuf.append("tb_");
			strSqlBuf.append(groupCode);
			strSqlBuf.append("_data_navdata");
			strSqlBuf.append(" where fnavdate = ");
			strSqlBuf.append(dbl.sqlDate(dDate));
			strSqlBuf.append(" and FportCode = ");
			strSqlBuf.append(dbl.sqlString(subPortCode));
			strSqlBuf.append(" and fkeycode = 'TotalValue'");
			strSql = strSqlBuf.toString();

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				netValue = rs.getDouble("fportmarketvalue");
			}
			rs.close();
		} catch (Exception e) {
			throw new YssException("获取基金资产净值： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	protected void getPaidCapital(java.sql.Date dDate, String portCode)
			throws YssException {
		String strSql = "";
		StringBuffer strSqlBuf = new StringBuffer();
		
		String groupCode = "";
		String subPortCode = "";
	    groupCode = portCode.split("-")[0];
	    subPortCode = portCode.split("-")[1];
		
		ResultSet rs = null;
		try {
			strSqlBuf.append("select fportmarketvalue from ");
			strSqlBuf.append("tb_");
			strSqlBuf.append(groupCode);
			strSqlBuf.append("_data_navdata");
			strSqlBuf.append(" where fnavdate = ");
			strSqlBuf.append(dbl.sqlDate(dDate));
			strSqlBuf.append(" and FportCode = ");
			strSqlBuf.append(dbl.sqlString(subPortCode));
			strSqlBuf.append(" and fkeycode = 'TotalAmount'");
			strSql = strSqlBuf.toString();

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				paidCapital = rs.getDouble("fportmarketvalue");
			}
			rs.close();
		} catch (Exception e) {
			throw new YssException("获取实收资本： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	protected void getUnitValue(java.sql.Date dDate, String portCode)
			throws YssException {
		String strSql = "";
		StringBuffer strSqlBuf = new StringBuffer();
		
		String groupCode = "";
		String subPortCode = "";
	    groupCode = portCode.split("-")[0];
	    subPortCode = portCode.split("-")[1];
		
		ResultSet rs = null;
		double dreturn = 0;
		try {
			strSqlBuf.append("select FPrice from ");
			strSqlBuf.append("tb_");
			strSqlBuf.append(groupCode);
			strSqlBuf.append("_data_navdata");
			strSqlBuf.append(" where fnavdate = ");
			strSqlBuf.append(dbl.sqlDate(dDate));
			strSqlBuf.append(" and FportCode = ");
			strSqlBuf.append(dbl.sqlString(subPortCode));
			strSqlBuf.append(" and fkeycode = 'Unit'");
			strSql = strSqlBuf.toString();

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				unitValue = rs.getDouble("FPrice");
			}
			rs.close();
		} catch (Exception e) {
			throw new YssException("获取单位净值： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	protected void getAccumulateUnit(java.sql.Date dDate, String portCode)
			throws YssException {
		String strSql = "";
		StringBuffer strSqlBuf = new StringBuffer();
		
		String groupCode = "";
		String subPortCode = "";
	    groupCode = portCode.split("-")[0];
	    subPortCode = portCode.split("-")[1];
		
		ResultSet rs = null;
		double dreturn = 0;
		try {
			strSqlBuf.append("select FPrice from ");
			strSqlBuf.append("tb_");
			strSqlBuf.append(groupCode);
			strSqlBuf.append("_data_navdata");
			strSqlBuf.append(" where fnavdate = ");
			strSqlBuf.append(dbl.sqlDate(dDate));
			strSqlBuf.append(" and FportCode = ");
			strSqlBuf.append(dbl.sqlString(subPortCode));
			strSqlBuf.append(" and fkeycode = 'AccumulateUnit'");
			strSql = strSqlBuf.toString();

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				accumulateUnit = rs.getDouble("FPrice");
			}
			rs.close();
		} catch (Exception e) {
			throw new YssException("获取累计净值： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	protected String buildRowCompResult(String str) throws YssException {

		String strSql = "";
		String strReturn = "";
		ResultSet rs = null;
		HashMap hmCellStyle = null;
		StringBuffer buf = new StringBuffer();
		String sKey = "";
		RepTabCellBean rtc = null;
		String[] sArry = null;
		try {
			sArry = str.split("\t");
			hmCellStyle = getCellStyles("DSFundNav004");
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField")
					+ " where FRepDsCode = " + dbl.sqlString("DSFundNav004")
					+ " and FCheckState = 1 order by FOrderIndex";
			rs = dbl.openResultSet(strSql);
			for (int i = 0; i < sArry.length; i++) {
				sKey = "DSFundNav004" + "\tDSF\t-1\t" + i;
				if (hmCellStyle.containsKey(sKey)) {
					rtc = (RepTabCellBean) hmCellStyle.get(sKey);
					buf.append(rtc.buildRowStr()).append("\n");
				}
				buf.append(sArry[i]).append("\t");
			}
			if (buf.toString().trim().length() > 1) {
				strReturn = buf.toString().substring(0,
						buf.toString().length() - 1);
			}
			rs.close();
			return strReturn + "\t\t";
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
}
