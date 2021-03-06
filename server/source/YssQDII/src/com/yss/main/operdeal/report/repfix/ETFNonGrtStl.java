package com.yss.main.operdeal.report.repfix;

import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.dsub.BaseBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.util.YssD;

/**
 * add by songjie 2013.04.25
 * STORY #3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001
 * 查询 和 生成  ETF非担保交收指令生成表 
 * @author 宋洁
 *
 */
public class ETFNonGrtStl  extends BaseBuildCommonRep{
	private String portCode = "";//组合代码
	private java.util.Date settleDate = null; //交收日期
	private String sOperType = "Search";//操作类型：查询 或 生成
	
	protected CommonRepBean repBean;
	
	public ETFNonGrtStl(){
		
	}
	
	/**
	 * add by songjie 2013.04.25
	 * STORY #3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001
	 */
	public void initBuildReport(BaseBean bean) throws YssException {
		repBean = (CommonRepBean) bean;
		// 解析前台传入的条件字符串
		this.parseRowStr(this.repBean.getRepCtlParam());
	}

	/**
	 * add by songjie 2013.04.25
	 * STORY #3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001
	 * @param sRowStr
	 * @throws YssException
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String[] sReq = sRowStr.split("\n");
		try {
			this.settleDate = YssFun.toDate(sReq[0].split("\r")[1]) ;
			this.portCode = sReq[1].split("\r")[1];
			this.sOperType = sReq[2].split("\r")[1];
		} catch (Exception e) {
			throw new YssException("解析参数出错", e);
		}
	}
	
	/**
	 * 生成报表
	 * add by songjie 2013.04.25
	 * STORY #3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001
	 * */
	public String buildReport(String sType) throws YssException {
		// 从前台传入的操作请求为查询时，调用查询数据的方法
		if (sOperType.trim().equals("Search")) {
			return this.searchETFNonGrtStlTable();
		} else if (sOperType.trim().equals("Build")) {
			return this.buildETFNonGrtStlTable();
		}else{
			return "";
		}
	}
	
	/**
	 * 查询报表数据
	 * add by songjie 2013.04.25
	 * STORY #3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001
	 * @return
	 */
	private String searchETFNonGrtStlTable() throws YssException{
		String strSql = "";
		ResultSet rs = null;
		StringBuffer buff = new StringBuffer();
		boolean seeSum = false;
		boolean haveDetail = false;
		try{
			strSql = "select * from " + pub.yssGetTableName("Tb_ETF_NoGrtRep") + 
			" where FSettleDate = " + dbl.sqlDate(settleDate) + 
			" and FFundCode in(select FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
			" where FPortCode = " + dbl.sqlString(portCode) + ") and FCheckTime is null ";
			rs = dbl.openResultSet(strSql);
			
			if(rs.next()){
				haveDetail = true;
			}
			
			dbl.closeResultSetFinal(rs);
			
			if(!haveDetail){
				return "";
			}
			
			strSql = " select * from (select * from " + pub.yssGetTableName("Tb_ETF_NoGrtRep") + 
			" where FSettleDate = " + dbl.sqlDate(settleDate) +
			" and FFundCode in(select FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
			" where FPortCode = " + dbl.sqlString(portCode) + ") " + 
			" and FOrderCode = 'detail' order by FDclNum) a " +
			" union all " + 
			" select * from (select * from " + pub.yssGetTableName("Tb_ETF_NoGrtRep") + 
			" where FSettleDate = " + dbl.sqlDate(settleDate) +
			" and FFundCode in(select FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
			" where FPortCode = " + dbl.sqlString(portCode) + ") " +
			" and FOrderCode <> 'detail' order by fordercode) b ";
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				if(!seeSum && rs.getString("FOrderCode") != null && rs.getString("FOrderCode").indexOf("sum") != -1){
					seeSum = true;
					buff.append("\r\n");
				}
				
				if(rs.getString("FDclNum").equals("ETF资金交收汇总")){
					buff.append("\r\n");
				}
				
				buff.append(rs.getString("FDclNum") + "").append("\t");
				buff.append(YssFun.formatDate(rs.getDate("FSettleDate"),"yyyyMMdd") + "").append("\t");
				buff.append(rs.getString("FPayType") + "").append("\t");
				buff.append(rs.getString("FReceiveType") + "").append("\t");
				buff.append(rs.getString("FPayerNum") + "").append("\t");
				buff.append(rs.getString("FPayerAccCode") + "").append("\t");
				buff.append(rs.getString("FPayerName") + "").append("\t");
				buff.append(rs.getString("FPayerEcnmType") + "").append("\t");
				buff.append(rs.getString("FReceiverNum") + "").append("\t");
				buff.append(rs.getString("FReceiverAccCode") + "").append("\t");
				buff.append(rs.getString("FReceiverName") + "").append("\t");
				buff.append(rs.getString("FReceiverEcnmType") + "").append("\t");
				buff.append(rs.getString("FCuryCode") + "").append("\t");
				//--- edit by songjie 2013.05.20 数据显示为科学计数法  应改为常规格式 start---//
				buff.append(YssFun.formatNumber(rs.getDouble("FCommandMoney"), "#,##0.##")).append("\t");
				//--- edit by songjie 2013.05.20 数据显示为科学计数法  应改为常规格式 end---//
				
				
				
				
				if(rs.getString("FOrderCode") != null && rs.getString("FOrderCode").indexOf("sum") != -1){
					buff.append("").append("\t");
					buff.append("").append("\t");
					
					if(!(rs.getString("FDclNum").equals("应收款合计") ||
					   rs.getString("FDclNum").equals("应付款合计")	||
					   rs.getString("FDclNum").equals("银行存款账户应付款合计"))){
						buff.append("").append("\t");
					}else{
						buff.append(rs.getString("FFundCode") + "").append("\t");
					}
				}else{
					if(YssFun.formatDate(rs.getDate("FBargainDate"),"yyyyMMdd").equals("19000101")){
						buff.append("").append("\t");
					}else{
						buff.append(YssFun.formatDate(rs.getDate("FBargainDate"),"yyyyMMdd")).append("\t");
					}
					
					buff.append(YssFun.formatDate(rs.getDate("FPreSettleDate"),"yyyyMMdd")).append("\t");
					buff.append(rs.getString("FFundCode") + "").append("\t");
				}
				
				buff.append(rs.getString("FCapitalType") + "").append("\t");
				buff.append(rs.getString("FDesc") + "").append("\t");
				if(rs.getString("FOrderCode") != null && rs.getString("FOrderCode").indexOf("sum") != -1){
					buff.append("").append("\t");
				}else{
				    //--- edit by songjie 2013.05.20 数据显示为科学计数法  应改为常规格式 start---//
					buff.append(YssFun.formatNumber(rs.getDouble("FZDCommandMoney"), "#,##0.##")).append("\t");
				    //--- edit by songjie 2013.05.20 数据显示为科学计数法  应改为常规格式 end---//
				}
				
				buff.append(rs.getString("FZDChecked") + "").append("\t");
				buff.append(rs.getString("FSeatMember") + "").append("\t");
				buff.append(rs.getString("FCreater") + "").append("\t");
				buff.append("\r\n");
			}
			
			buff.append("\r\f");
			
			return buff.toString();
		}catch(Exception e){
			throw new YssException("查询ETF非担保交收指令生成表出错");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 生成报表数据
	 * add by songjie 2013.04.25
	 * STORY #3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001
	 * @return
	 */
	private String buildETFNonGrtStlTable() throws YssException{
		String strSql = "";
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		Connection conn = dbl.getConnection();
		boolean trans = false;
		PreparedStatement pst = null;
		String assetCode = "";
		boolean haveDetail = false;
		//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
		String clearNo = " ";//资金账户
		String clearName = " ";//付款人、收款人名称
		String clearNum = "";//ETF参数设置_清算编号
		//edit by songjie 2013.05.09 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B
		double zdCommandMoney = 0;//中登划款金额
		HashMap hmMoney = null;
		String payerNum = "";//付方清算编号
		/**add---huhuichao 2013-7-24 STORY  4192 博时：跨境ETF补充*/
		String supplymode = "";//补票方式
		/**end---huhuichao 2013-7-24 STORY  4192*/
		//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
		try{
			conn.setAutoCommit(false);
			trans = true;
			
			//--- add by songjie 2013.05.09 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B start---//
			deleteReportData();//根据查询条件删除ETF非担保交收指令表数据
			//--- add by songjie 2013.05.09 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B end---//
			
			//--- add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
			strSql = " select * from " + pub.yssGetTableName("Tb_ETF_Param") + 
			" where FPortCode = " + dbl.sqlString(portCode) + " and FCheckState = 1 ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				/**add---huhuichao 2013-7-24 STORY  4192 博时：跨境ETF补充*/
				clearNum = rs.getString("FClearNum");
				supplymode = rs.getString("FSupplyMode");
				/**end---huhuichao 2013-7-24 STORY  4192*/
			}
			
			dbl.closeResultSetFinal(rs);
			//--- add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B end---//
			
			strSql = " select FSeatCode, FACCOUNTNO, FBROKERNAME from " + pub.yssGetTableName("Tb_ETF_Broker") + 
			//edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B 清算编号 通过参数获取
			" where FSeatCode = " + dbl.sqlString(clearNum) + " and FCheckState = 1 ";
			rs = dbl.openResultSet(strSql) ;
			while(rs.next()){
				//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
				clearNo = rs.getString("FACCOUNTNO");//资金账户
				clearName = rs.getString("FBROKERNAME");//付款人、收款人名称
				//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B end---//
			}
			dbl.closeResultSetFinal(rs);
			
			strSql = " select FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
			//edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B 添加已审核查询条件
			" where FPortCode = " + dbl.sqlString(portCode) + " and FCheckState = 1 ";
			rs = dbl.openResultSet(strSql) ;
			while(rs.next()){
				assetCode = rs.getString("FAssetCode");
			}
			dbl.closeResultSetFinal(rs);
			
			hmMoney = getZDCommandMoney();
			
			strSql = buildInsertSql();
			pst = dbl.getPreparedStatement(strSql);
			//edit by songjie 2013.05.09 STORY 3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 拼接明细项数据
			/**add---huhuichao 2013-7-24 STORY  4192 博时：跨境ETF补充*/
			/**生成“ETF非担保交收指令生成表”时，区分补票方式*/
			if (supplymode.equalsIgnoreCase("10"))//轧差+加权平均  补票方式
				rs = dbl.openResultSet(buildReportSql(sb));
			else if (supplymode.equalsIgnoreCase("9"))
				rs = dbl.openResultSet(createReportSql(sb));//单位篮子补票  补票方式
			/**end---huhuichao 2013-7-24 STORY  4192*/
			while(rs.next()){
				if(!haveDetail){
					haveDetail = true;
				}
				
				setPst(pst, rs, clearNum, clearNo, clearName, hmMoney);
			}
			
			dbl.closeResultSetFinal(rs);
			
			pst.executeBatch();
			
			//如果未生成明细数据，则不生成汇总数据，直接返回查询结果
			if(!haveDetail){
				return this.searchETFNonGrtStlTable();
			}
		
			//--- add by songjie 2013.05.09 STORY 3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 start---//
			createSumData(pst, assetCode);//生成汇总报表数据
			//--- add by songjie 2013.05.09 STORY 3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 end---//
			
			pst.executeBatch();
			
			conn.commit();
			trans = false;
			
			return this.searchETFNonGrtStlTable();
		}catch(Exception e){
			throw new YssException("生成ETF非担保交收指令生成表报错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, trans);
			dbl.closeStatementFinal(pst);
		}
	}
	
	/**
	 * add by songjie 2013.04.25
	 * STORY #3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001
	 * 拼接 insert 语句
	 * @return
	 * @throws YssException
	 */
	private String buildInsertSql()throws YssException{
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("insert into " + pub.yssGetTableName("Tb_ETF_NoGrtRep"))
		     .append("(FDclNum,FSettleDate,FPayType,FReceiveType,FPayerNum,FPayerAccCode,FPayerName,")
		     .append("FPayerEcnmType,FReceiverNum,FReceiverAccCode,FReceiverName,FReceiverEcnmType,")
		     .append("FCuryCode,FCommandMoney,FBargainDate,FPreSettleDate,FFundCode,FCapitalType,")
		     .append("FDesc,FZDCommandMoney,FZDChecked,FSeatMember,FCreater,FCreateTime,FCheckState,")
		     .append("FChecker,FCheckTime,FOrderCode)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		return sbSql.toString();
	}
	
	/**
	 * add by songjie 2013.04.25
	 * STORY #3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001
	 * 给明细数据赋值并插入报表
	 * @param pst
	 * @param rs
	 * @param clearNum
	 * @param clearNo
	 * @param clearName
	 * @param hmMoney
	 * @throws YssException
	 */
	private void setPst(PreparedStatement pst, ResultSet rs, 
			            String clearNum, String clearNo, 
			            String clearName, HashMap hmMoney) throws YssException{
		String payerNum = "";
		double zdCommandMoney = 0;
		try{
			pst.setString(1, rs.getString("FDclNum") + "");
			pst.setDate(2, YssFun.toSqlDate(rs.getDate("FSettleDate")));
			pst.setString(3, rs.getString("FPayType") + "");
			pst.setString(4, rs.getString("FReceiveType") + "");
			
			//若资金类型 为 205：ETF申购赎回现金差额净额
			if(rs.getString("FCapitalType").equals("205")){
				//--- add by songjie 2013.05.20 席位会员取数错误 start---//
				payerNum = rs.getString("FPayerNum") + "";//席位会员 = ETF台账_参与券商代码
				//--- add by songjie 2013.05.20 席位会员取数错误 end---//
				
				//若 划款金额 >= 0,则 付方清算编号 = ETF台账_参与券商代码
				if (rs.getDouble("FCommandMoney") >= 0) {
					pst.setString(5, rs.getString("FPayerNum") + "");// 付方清算编号
					pst.setString(6, rs.getString("FPayerAccCode") == null ? " " : rs.getString("FPayerAccCode"));//付方资金帐户
					pst.setString(7, rs.getString("FPayerName")== null ? " " : rs.getString("FPayerName"));//付款人名称
				} else {//否则，付方清算编号 = ETF参数设置_清算编号
					//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
					pst.setString(5, clearNum);// 付方清算编号
					pst.setString(6, clearNo);//付方资金账户
					pst.setString(7, clearName);//付款人名称
					//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B end---//
				}
			}
			
			//若资金类型 为 203：ETF申购赎回现金替代退补
			if(rs.getString("FCapitalType").equals("203")){
				//若 划款金额 >= 0,则 付方清算编号 = ETF参数设置_清算编号
				if (rs.getDouble("FCommandMoney") >= 0) {
					//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
					//---add by songjie 2013.05.10 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B start---//
					payerNum = clearNum;
					//---add by songjie 2013.05.10 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B end---//
					pst.setString(5, clearNum);// 付方清算编号
					pst.setString(6, clearNo);//付方资金账户
					pst.setString(7, clearName);//付款人名称
					//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B end---//
				} else {//否则，付方清算编号 = ETF台账_参与券商代码
					//---add by songjie 2013.05.10 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B start---//
					payerNum = rs.getString("FPayerNum") + "";
					//---add by songjie 2013.05.10 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B end---//
					pst.setString(5, rs.getString("FPayerNum") + "");// 付方清算编号
					pst.setString(6, rs.getString("FPayerAccCode") == null ? " " : rs.getString("FPayerAccCode"));//付方资金帐户
					pst.setString(7, rs.getString("FPayerName")== null ? " " : rs.getString("FPayerName"));//付款人名称
				}
			}
			
			//若资金类型 为202：ETF赎回现金替代，则 付方清算编号 = ETF参数设置_清算编号
			if(rs.getString("FCapitalType").equals("202")){
				//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
				//---add by songjie 2013.05.10 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B start---//
				payerNum = clearNum;
				//---add by songjie 2013.05.10 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B end---//
				pst.setString(5, clearNum);//付方清算编号
				pst.setString(6, clearNo);//付方资金账户
				pst.setString(7, clearName);//付款人名称
				//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B end---//
			}
	
			pst.setString(8, rs.getString("FPayerEcnmType") + "");
			
			//若资金类型 为 205：ETF申购赎回现金差额净额
			if(rs.getString("FCapitalType").equals("205")){
				//若 划款金额 >= 0,则 收方清算编号 = ETF参数设置_清算编号
				if (rs.getDouble("FCommandMoney") >= 0) {
					//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
					pst.setString(9, clearNum);//收方清算编号
					pst.setString(10, clearNo);//收方资金帐户
					pst.setString(11, clearName);//收款人名称
					//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B end---//
				}else{//否则，付方清算编号 = ETF台账_参与券商代码
					pst.setString(9, rs.getString("FPayerNum") + "");//收方清算编号
					pst.setString(10, rs.getString("FPayerAccCode") == null ? " " : rs.getString("FPayerAccCode"));//收方资金帐户
					pst.setString(11, rs.getString("FPayerName")== null ? " " : rs.getString("FPayerName"));//收款人名称
				}
			}
			
			//若资金类型 为 203：ETF申购赎回现金替代退补
			if(rs.getString("FCapitalType").equals("203")){
				//若 划款金额 >= 0,则 收方清算编号 = ETF台账_参与券商代码
				if (rs.getDouble("FCommandMoney") >= 0) {
					pst.setString(9, rs.getString("FPayerNum") + "");//收方清算编号
					pst.setString(10, rs.getString("FPayerAccCode") == null ? " " : rs.getString("FPayerAccCode"));//收方资金帐户
					pst.setString(11, rs.getString("FPayerName")== null ? " " : rs.getString("FPayerName"));//收款人名称
				}else{//否则，付方清算编号 = ETF参数设置_清算编号
					//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
					pst.setString(9, clearNum);//收方清算编号
					pst.setString(10, clearNo);//收方资金帐户
					pst.setString(11, clearName);//收款人名称
					//--- edit by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B end---//
				}
			}
			
			//若资金类型 为202：ETF赎回现金替代，则 收方清算编号 = ETF台账_参与券商代码
			if(rs.getString("FCapitalType").equals("202")){
				pst.setString(9, rs.getString("FPayerNum") + "");//收方清算编号
				pst.setString(10, rs.getString("FPayerAccCode") == null ? " " : rs.getString("FPayerAccCode"));//收方资金帐户
				pst.setString(11, rs.getString("FPayerName")== null ? " " : rs.getString("FPayerName"));//收款人名称
			}
			
			pst.setString(12, rs.getString("FReceiverEcnmType") + "");
			pst.setString(13, rs.getString("FCuryCode") + "");
			pst.setDouble(14, Math.abs(rs.getDouble("FCommandMoney")));
			pst.setDate(15, rs.getDate("FBargainDate") == null ? YssFun.toSqlDate("1900-01-01") : YssFun.toSqlDate(rs.getDate("FBargainDate")));
			pst.setDate(16, YssFun.toSqlDate(rs.getDate("FPreSettleDate")));
			pst.setString(17, rs.getString("FFundCode") + "");
			pst.setString(18, rs.getString("FCapitalType") + "");//资金类型
			pst.setString(19, rs.getString("FDesc") + "");
			
			//--- edit by songjie 2013.05.09 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B start---//
			if(rs.getString("FCapitalType").equals("205")){
				if(hmMoney.get(payerNum) != null){
					zdCommandMoney = ((Double)hmMoney.get(payerNum)).doubleValue();
				}else{
					zdCommandMoney = 0;
				}
				pst.setDouble(20, Math.abs(zdCommandMoney));
				//如果划款金额 - 中登划款金额 == 0，则 核对结果为 一致，否则为 不一致
				if(YssD.sub(rs.getDouble("FCommandMoney"), zdCommandMoney) == 0){
					pst.setString(21, "一致");
				}else{
					pst.setString(21, "不一致");
				}
			}else{
				pst.setDouble(20, Math.abs(rs.getDouble("FZDCommandMoney")));
				pst.setString(21, rs.getString("FZDChecked") + "");
			}
			pst.setString(22, payerNum);
			//---edit by songjie 2013.05.09 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B end---//
			pst.setString(23, pub.getUserCode());
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "detail");
			
			pst.addBatch();
		}catch(Exception e){
			throw new YssException(" pst赋值出错 ",e);
		}
	}
	
	/**
	 * add by songjie 2013.05.09
	 * BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B
	 * 计算中登划款金额
	 * @param receiverNum
	 * @return
	 * @throws YssException
	 */
	private HashMap getZDCommandMoney() throws YssException{
		String strSql = "";
		ResultSet rs = null;
		double zdCommandMoney = 0;
		HashMap hmMoney = new HashMap();
		String clearCode = "";
		try{
			strSql = " select sum(jsmx.FinalMoney) as FinalMoney, fportcode,FClearCode  from (select case " +
			" when jsitf.FTradeTypeCode = '103' then -jsitf.FTotalMoney else jsitf.FTotalMoney " +
			" end as FinalMoney,jsitf.FTotalMoney,jsitf.FTotalMoney,jsitf.fportcode,jsitf.FClearCode " +
			" from " + pub.yssGetTableName("Tb_ETF_JSMXInterface") +
			" jsitf where jsitf.FCheckState = 1 and jsitf.FClearMark = '279' " +
			" and jsitf.FPortCode = " + dbl.sqlString(portCode) + " and exists (select FTradeDate, fbs " +
			" from (select FTradeDate, case when fselltype = '01' then '102' else '103' end as fbs from " + 
			pub.yssGetTableName("Tb_Ta_Trade") +
			" where FCheckState = 1 and fselltype in ('01', '02') and fportcode = " +
			dbl.sqlString(portCode) + " and FCashBalanceDate = " + dbl.sqlDate(settleDate) +
			" ) ta where ta.FTradeDate = jsitf.FBargainDate and ta.fbs = jsitf.FTradeTypeCode)) jsmx " +
			" group by fportcode, FClearCode ";
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				clearCode = rs.getString("FClearCode");
				zdCommandMoney = rs.getDouble("FinalMoney");
				hmMoney.put(clearCode, zdCommandMoney);
			}
			
			return hmMoney;
		}catch(Exception e){
			throw new YssException("计算中登划款金额出错",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2013.05.09 
	 * BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B
	 * 删除ETF非担保交收指令表数据
	 * @throws YssException
	 */
	private void deleteReportData() throws YssException{
		String strSql = "";
		try{
			strSql = " delete from " + pub.yssGetTableName("Tb_ETF_NoGrtRep") + 
			" where FSettleDate = " + dbl.sqlDate(settleDate) +
			" and FFundCode in (select FAssetCode from " + pub.yssGetTableName("Tb_Para_portfolio") + 
			" where FPortCode = " + dbl.sqlString(portCode) + ") ";
			
			dbl.executeSql(strSql);
		}catch(Exception e){
			throw new YssException("删除ETF非担保交收指令表数据出错",e);
		}
	}
	/**
	 * add by huhuichao 2013-7-24 STORY  4192 博时：跨境ETF补充
	 * 拼接报表数据sql
	 * @param sb
	 * @return String
	 * @throws YssException
	 */
	private String createReportSql(StringBuffer sb) throws YssException{ 
		try{
			sb.append(" select REPLACE(TO_CHAR(FSettleDate, 'yyMMdd') || to_char(rownum, '0000'), ' ','') as FDclNum, ")
            .append(" FSettleDate, '002' as FPayType,'F' as FReceiveType,FPayerNum,FPayerAccCode, ")
            .append(" FPayerName,' ' as FPayerEcnmType,' ' as FReceiverNum,' ' as FReceiverAccCode, ")
            .append(" ' ' as FReceiverName,' ' as FReceiverEcnmType,'RMB' as FCuryCode, ")
            .append(" FCommandMoney,FBargainDate,FPreSettleDate,FFundCode,FCapitalType, ")
            .append(" FDesc,FZDCommandMoney,FZDChecked,' ' as FSeatMember,'admin' as FCreater from (")
			//ETF申购赎回现金差额净额
            .append(" select ").append(dbl.sqlDate(settleDate)).append(" as FSettleDate,")
            .append(" detail.FBrokerCode as FPayerNum, ")
            .append(" broker.FAccountNo as FPayerAccCode,broker.FBROKERNAME as FPayerName, ")
            .append(" nvl((detail.ftradeamount/para.fnormscale*cashbal.fstandardmoneymarketvalue), 0) as FCommandMoney, ")
            .append(" detail.fbuydate as FBargainDate,").append(dbl.sqlDate(settleDate)).append(" as FPreSettleDate, ")
            .append(" port.FAssetCode as FFundCode,'205' as FCapitalType, ")
            .append(" 'ETF申购赎回现金差额净额' as FDesc, ")
            .append(" 0 as FZDCommandMoney, ")
            .append(" ' ' as FZDChecked ")
            .append(" from (select FPortCode,FClearCode as FBrokerCode,fbargaindate as fbuydate,")
            .append("sum(case when FTradeTypeCode = '103' and jsmx.fclearmark = ' ' then -ftradeamount when" +
            		" FTradeTypeCode = '102' and jsmx.fclearmark = '276' then ftradeamount end ) as ftradeamount from " )
            .append(pub.yssGetTableName("Tb_ETF_JSMXInterface"))
            .append(" jsmx  where fportcode = ").append(dbl.sqlString(portCode))
            .append(" and exists  (select FTradeDate, FTradeTypeCode from (select FTradeDate,case when fselltype =" +
            		" '01' then '102' else '103' end as FTradeTypeCode from ")
            .append(pub.yssGetTableName("Tb_Ta_Trade")).append(" where FCheckState = 1 ")
            .append("and fselltype in ('01', '02')")
            .append("and fportcode = ").append(dbl.sqlString(portCode))
            .append("and FCashBalanceDate = ").append(dbl.sqlDate(settleDate))
            .append(" ) ta where ta.ftradedate = jsmx.fbargaindate and ta.FTradeTypeCode =" +
            		" jsmx.FTradeTypeCode)")
            .append(" and jsmx.frecordtype = '003'")
            .append(" group by FClearCode, fportcode, fbargaindate ) detail ")
            .append(" left join (select FSeatCode, FACCOUNTNO, FBROKERNAME ")
            .append(" from " + pub.yssGetTableName("Tb_ETF_BROKER") + " where FCheckState = 1) broker ")
            .append(" on detail.FBrokerCode = broker.FSeatCode ")
            .append(" left join (select fstandardmoneymarketvalue, fportcode from (select * from")
            .append(" (select gv.fstandardmoneymarketvalue as fstandardmoneymarketvalue,lpad(gv.fportcode, 3, '0')" +
            		" as fsetcode,fdate from ")
            .append(pub.yssGetTableName("Tb_Rep_GuessValue")).append(" gv where gv.fdate =")
            .append(" (select distinct FTradeDate from ")
            .append(pub.yssGetTableName("Tb_Ta_Trade")).append(" ta where FCheckState = 1 ")
            .append("and FCashBalanceDate = ").append(dbl.sqlDate(settleDate))
            .append("and ta.fselltype in ('01', '02'))").append(" and gv.facctcode = '9802') mm")
            .append(" left join (select fsetid, fsetcode,fyear from lsetlist ) nn ")
            .append(" on mm.fsetcode = nn.fsetcode and nn.fyear = to_number(to_char(mm.fdate,'yyyy'))")
            /**add---huhuichao 2013-11-15 STORY  13644 */
            .append(" left join (select fportcode, fassetcode from ")
            .append(pub.yssGetTableName("Tb_Para_Portfolio"))
            .append(" ) kk  on kk.fassetcode = nn.fsetid)) cashbal ")
            /**end---huhuichao 2013-11-15 STORY  13644*/
            .append(" on cashbal.fportcode = detail.fportcode ")
            .append(" left join (select FAssetCode, FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio"))
            .append(" where FcheckState = 1) port on port.FPortCode = detail.fportcode ")
            .append(" left join (select etfp.fnormscale, etfp.fportcode from ")
            .append(pub.yssGetTableName("Tb_ETF_Param")).append(" etfp where FPortCode = ").append(dbl.sqlString(portCode))
            .append(" and FCheckState = 1) para on para.fportcode = detail.fportcode ")
            //ETF申购赎回现金替代退补
            .append(" union all ")
            .append(" select a.FRefundDate as FSettleDate, a.FBrokerCode as FPayerNum, ")
            .append(" broker.FAccountNo as FPayerAccCode, broker.FBROKERNAME as FPayerName, ")
            .append(" nvl(a.ftradeamount / para.fnormscale*m.FSumReturn, 0) as FCommandMoney, ")
            .append(" a.FBuydate as FBargainDate,a.FRefundDate as FPreSettleDate, port.FAssetCode as FFundCode, ")
            .append(" '203' as FCapitalType, 'ETF申购赎回现金替代退补' as FDesc, ")
            .append(" 0 as FZDCommandMoney, ")
            .append(" ' ' as FZDChecked ")
            .append(" from (select ").append(dbl.sqlDate(settleDate)).append(" as FRefundDate,fclearcode as FBrokerCode,")
            .append(" fportcode,fbargaindate as FBuydate,ftradetypecode,sum(jsmx.ftradeamount) as ftradeamount from ")
            .append(pub.yssGetTableName("Tb_ETF_JSMXInterface"))
            .append(" jsmx  where jsmx.fbargaindate = (select fbuydate from ")
            .append(pub.yssGetTableName("tb_etf_standingbook")).append(" where frefunddate = ")
            .append(dbl.sqlDate(settleDate)).append(" and fportcode = ").append(dbl.sqlString(portCode))
            .append(" and fbs = 'B' group by fbuydate)")
            .append(" and jsmx.ftradetypecode = '102' and jsmx.fclearmark = '276' and jsmx.frecordtype = '003'")
            .append(" group by fsettledate, fportcode,fclearcode,fbargaindate,ftradetypecode) a")
            .append(" left join (select FSeatCode, FACCOUNTNO, FBROKERNAME ")
            .append(" from " + pub.yssGetTableName("Tb_ETF_BROKER") + " where FCheckState = 1) broker ")
            .append(" on a.FBrokerCode = broker.FSeatCode ")
            .append(" left join (select FAssetCode, FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio"))
            .append(" where FcheckState = 1) port on port.FPortCode = a.fportcode ")
            .append(" left join (select etfp.fnormscale, etfp.fportcode from ")
            .append(pub.yssGetTableName("Tb_ETF_Param")).append(" etfp where FPortCode = ").append(dbl.sqlString(portCode))
            .append(" and FCheckState = 1) para on para.fportcode = a.fportcode ")
            .append(" left join (select FSumReturn, fportcode from ").append(pub.yssGetTableName("tb_etf_standingbook"))
            .append("  where frefunddate = ").append(dbl.sqlDate(settleDate)).append(" and fportcode = ")
            .append(dbl.sqlString(portCode)).append(" and fbs = 'B' and FSecurityCode = ' ') m" +
            		" on m.fportcode = a.fportcode")
            .append("  where a.FRefundDate = ").append(dbl.sqlDate(settleDate))
            .append(" and a.fportcode = ").append(dbl.sqlString(portCode))
            .append(" and a. ftradetypecode = '102' and a.Fbrokercode <> ' '")
            //ETF赎回现金替代（包含必须现金替代赎回款和可以现金替代赎回款）
            .append(" union all ")
            .append(" select ").append(dbl.sqlDate(settleDate)).append(" as FSettleDate,")
            .append(" a.FBrokerCode as FPayerNum, ")
            .append(" broker.FAccountNo as FPayerAccCode, broker.FBROKERNAME as FPayerName, ")
            .append(" nvl(a.ftradeamount/para.fnormscale*m.FSumReturn, 0) +")
            .append(" nvl(a.ftradeamount/para.fnormscale*n.freplacemoney, 0) as FCommandMoney,")
            .append(" a.fbuydate as FBargainDate,")
            .append(dbl.sqlDate(settleDate)).append(" as FPreSettleDate,")
            .append(" port.FAssetCode as FFundCode,'202' as FCapitalType,'ETF赎回现金替代' as FDesc, ")
            .append(" 0 as FZDCommandMoney, ' ' as FZDChecked ")
            .append(" from (select ").append(dbl.sqlDate(settleDate)).append(" as FRefundDate,")
            .append(" fclearcode as FBrokerCode,fportcode,fbargaindate as FBuydate, ftradetypecode,")
            .append(" sum(jsmx.ftradeamount) as ftradeamount from ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface"))
            .append(" jsmx where jsmx.fbargaindate = (select fbuydate from ").append(pub.yssGetTableName("tb_etf_standingbook"))
            .append(" where frefunddate = ").append(dbl.sqlDate(settleDate))
            .append(" and fportcode = ").append(dbl.sqlString(portCode)).append(" and fbs = 'S' group by fbuydate)")
            .append(" and jsmx.ftradetypecode = '103' and jsmx.fclearmark = ' ' and jsmx.frecordtype = '003'")
            .append(" group by fsettledate,fportcode,fclearcode,fbargaindate,ftradetypecode) a")
            .append(" left join (select FSeatCode, FACCOUNTNO, FBROKERNAME from ")
            .append(pub.yssGetTableName("Tb_ETF_BROKER"))
            .append(" where FCheckState = 1) broker on a.FBrokerCode = broker.FSeatCode")
            .append(" left join (select FAssetCode, FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio"))
            .append(" where FcheckState = 1) port on port.FPortCode = a.fportcode ")
            .append(" left join (select etfp.fnormscale, etfp.fportcode from ")
            .append(pub.yssGetTableName("Tb_ETF_Param")).append(" etfp where FPortCode = ").append(dbl.sqlString(portCode))
            .append(" and FCheckState = 1) para on para.fportcode = a.fportcode ")
            .append(" left join (select FSumReturn * -1 as FSumReturn, fportcode from ")
            .append(pub.yssGetTableName("tb_etf_standingbook")).append(" where frefunddate = ")
            .append(dbl.sqlDate(settleDate)).append(" and fportcode = ").append(dbl.sqlString(portCode))
            .append(" and fbs = 'S' and FSecurityCode = ' ') m on m.fportcode = a.fportcode")
            .append(" left join (select freplacemoney, fportcode from ").append(pub.yssGetTableName("Tb_ETF_StockList"))
            .append(" where FDate = (select ftradedate from ").append(pub.yssGetTableName("Tb_Ta_Trade"))
            .append(" where fselltype = '02' and FCashReplaceDate = ").append(dbl.sqlDate(settleDate))
            .append(" )and FReplaceMark = '6') n on n.fportcode = a.fportcode )");
			return sb.toString();
		}catch(Exception e){
			throw new YssException(e.getMessage());
		}
	}
	
	/**
	 * add by songjie 2013.05.09 
	 * BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B
	 * 拼接报表数据sql
	 * @param sb
	 * @return
	 * @throws YssException
	 */
	private String buildReportSql(StringBuffer sb) throws YssException{ 
		try{
			sb.append(" select REPLACE(TO_CHAR(FSettleDate, 'yyMMdd') || to_char(rownum, '0000'), ' ','') as FDclNum, ")
            .append(" FSettleDate, '002' as FPayType,'F' as FReceiveType,FPayerNum,FPayerAccCode, ")
            .append(" FPayerName,' ' as FPayerEcnmType,' ' as FReceiverNum,' ' as FReceiverAccCode, ")
            .append(" ' ' as FReceiverName,' ' as FReceiverEcnmType,'RMB' as FCuryCode, ")
            .append(" FCommandMoney,FBargainDate,FPreSettleDate,FFundCode,FCapitalType, ")
            .append(" FDesc,FZDCommandMoney,FZDChecked,' ' as FSeatMember,'admin' as FCreater from (")
            
            //ETF申购赎回现金差额净额
            .append(" select ").append(dbl.sqlDate(settleDate)).append(" as FSettleDate,")
            .append(" detail.FBrokerCode as FPayerNum, ")
            .append(" broker.FAccountNo as FPayerAccCode,broker.FBROKERNAME as FPayerName, ")
            .append(" nvl((detail.FinalNum / stocl.secnums * cashbal.fportmarketvalue), 0) as FCommandMoney, ")
            .append(" detail.fbuydate as FBargainDate,").append(dbl.sqlDate(settleDate)).append(" as FPreSettleDate, ")
            .append(" port.FAssetCode as FFundCode,'205' as FCapitalType, ")
            .append(" 'ETF申购赎回现金差额净额' as FDesc, ")
//            .append(" nvl(jsmx1.FinalMoney, 0) as FZDCommandMoney, ")
//            //edit by songjie 2013.05.09 STORY 3869 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 用于判断核对项 的划款金额 计算错误
//            .append(" case when (abs(nvl((detail.FinalNum / stocl.secnums * cashbal.fportmarketvalue), 0)) - abs(nvl(jsmx1.FinalMoney, 0))) = 0 ")
//            .append(" then '一致' else '不一致' end as FZDChecked ")
            .append(" 0 as FZDCommandMoney, ")
            .append(" ' ' as FZDChecked ")
            .append(" from (select FPortCode,FBrokerCode,FBuydate,")
            .append("sum(case when FBS = 'S' then -FBraketNum else FBraketNum end ) as FinalNum from " )
            .append(pub.yssGetTableName("Tb_ETF_TradeStlDtl")).append(" tsd1  where FBrokerCode <> ' ' ")
            .append(" and fportcode = ").append(dbl.sqlString(portCode))
            .append(" and exists  (select FTradeDate, fbs from (select FTradeDate,case when fselltype = '01' then 'B' else 'S' end as fbs from ")
            .append(pub.yssGetTableName("Tb_Ta_Trade")).append(" where FCheckState = 1 ")
            .append("and fselltype in ('01', '02')")
            .append("and fportcode = ").append(dbl.sqlString(portCode))
            .append("and FCashBalanceDate = ").append(dbl.sqlDate(settleDate))
            .append(" ) ta where ta.FTradeDate = tsd1.FBuyDate and ta.fbs = tsd1.fbs)")
            .append(" group by fportcode ,fbuydate,FBrokerCode ) detail ")
            .append(" left join (select FSeatCode, FACCOUNTNO, FBROKERNAME ")
            .append(" from " + pub.yssGetTableName("Tb_ETF_BROKER") + " where FCheckState = 1) broker ")
            .append(" on detail.FBrokerCode = broker.FSeatCode ")
            .append(" left join (select nav.fportmarketvalue,nav.fportcode from ")
            .append(pub.yssGetTableName("Tb_Etf_Navdata"))
            .append(" nav where nav.fkeycode = 'UnitCashBal' and exists (select FTradeDate from ")
            .append(pub.yssGetTableName("Tb_Ta_Trade"))
            .append(" ta where FCheckState = 1 and FCashBalanceDate = ").append(dbl.sqlDate(settleDate))
            .append(" and ta.fselltype in ('01','02') and nav.fnavdate = ta.FTradeDate)) cashbal ")
            .append(" on cashbal.fportcode = detail.fportcode ")
            .append(" left join (select stoc.fportcode,stoc.fdate, count(stoc.fsecuritycode) as secnums from ")
            .append(pub.yssGetTableName("Tb_Etf_StockList"))
            .append(" stoc where stoc.fportcode = ").append(dbl.sqlString(portCode))
            .append(" and stoc.freplacemark = '5'")
            .append(" group by stoc.fportcode,stoc.fdate) stocl on stocl.fdate = detail.fbuydate ")
            .append(" left join (select FAssetCode, FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio"))
            .append(" where FcheckState = 1) port on port.FPortCode = detail.fportcode ")
//            .append(" left join (select sum(jsmx.FinalMoney) as FinalMoney, fportcode ,FClearCode ")
//            .append(" from (select case when jsitf.FTradeTypeCode = '103' then -jsitf.FTotalMoney ")
//            //--- edit by songjie 2013.05.09 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B start---//
//            //中登划款金额  的 查询条件 改为  通过 Tb_ETF_JSMXInterface.FBargainDate = ETF申赎对应的交易日期 来关联数据
//            //ETF申赎对应的交易日期  = TA交易数据 的 交易日期，通过 现金差额结转日期=ETF非担保交收指令表的交收日期 且 交易类型相同来关联数据
//            .append(" else jsitf.FTotalMoney end as FinalMoney, jsitf.FTotalMoney ,jsitf.FTotalMoney, ")
//            .append(" jsitf.fportcode, jsitf.FClearCode from ")
//            .append(pub.yssGetTableName("Tb_ETF_JSMXInterface"))
//            .append(" jsitf where jsitf.FCheckState = 1 and jsitf.FClearMark = '279' ")
//            .append(" and jsitf.FPortCode = " + dbl.sqlString(portCode))
//            .append(" and exists (select FTradeDate, FBS from (select FTradeDate, case when FSellType = '01' ")
//            .append(" then '102' else '103' end as FBS from " + pub.yssGetTableName("Tb_TA_Trade"))
//            .append(" where FCheckState = 1 and FSellType in ('01', '02') and FPortCode = " + dbl.sqlString(portCode))
//            .append(" and FCashBalanceDate = " + dbl.sqlDate(settleDate))
//            .append(" ) ta where ta.FTradeDate = jsitf.FBargainDate and ta.fbs = jsitf.FTradeTypeCode) ")
//            //--- edit by songjie 2013.05.09 BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B end---//
//            .append(" ) jsmx  group by fportcode ,FClearCode) jsmx1 on jsmx1.fportcode = detail.fportcode ")
            
            //ETF申购赎回现金替代退补
            .append(" union all ")
            .append(" select a.FRefundDate as FSettleDate, a.FBrokerCode as FPayerNum, ")
            .append(" broker.FAccountNo as FPayerAccCode, broker.FBROKERNAME as FPayerName, ")
            .append(" nvl(a.FSumReturn, 0) as FCommandMoney, a.FBuydate as FBargainDate, ")
            .append(" a.FRefundDate as FPreSettleDate, port.FAssetCode as FFundCode, ")
            .append(" '203' as FCapitalType, 'ETF申购赎回现金替代退补' as FDesc, ")
            .append(" 0 as FZDCommandMoney, ")
            .append(" ' ' as FZDChecked ")
            .append(" from (select distinct sb.FRefundDate, sb.FBrokerCode, sb.FPortCode, sb.FBS, sb.FBuyDate, ")
            .append(" sb.fdate,sum(sb.FSumReturn) as FSumReturn ")
            .append(" from " + pub.yssGetTableName("Tb_Etf_Standingbook") + " sb ")
            .append(" group by sb.FRefundDate, sb.FBrokerCode, sb.FPortCode, sb.FBS, sb.FBuyDate ,sb.fdate) a ")
            .append(" join ( select max(fdate) as fdate ,fbuydate,fportcode from ")
            .append(pub.yssGetTableName("tb_etf_standingbook")) 
            .append(" where frefunddate = " + dbl.sqlDate(settleDate))
            .append(" and fportcode = " + dbl.sqlString(portCode))
            .append(" and fbs = 'B' group by  fportcode,fbuydate )")
            .append(" mk on mk.fdate = a.fdate and mk.fbuydate = a.fbuydate and mk.fportcode = a.fportcode")
            .append(" left join (select FSeatCode, FACCOUNTNO, FBROKERNAME from ")
            .append(pub.yssGetTableName("Tb_ETF_BROKER"))
            .append(" where FCheckState = 1) broker on a.FBrokerCode = broker.FSeatCode ")
            .append(" left join (select FAssetCode, FPortCode from ")
            .append(pub.yssGetTableName("Tb_Para_Portfolio"))
            .append(" where FcheckState = 1) port on port.FPortCode = a.FPortCode ")
            .append(" where a.FRefundDate = " + dbl.sqlDate(settleDate))
            .append(" and a.FPortCode = " + dbl.sqlString(portCode) + " and a.FBS = 'B' and a.Fbrokercode <> ' ' ")
            
            //ETF赎回现金替代（包含必须现金替代赎回款和可以现金替代赎回款）
            .append(" union all ")
            .append(" select ").append(dbl.sqlDate(settleDate)).append(" as FSettleDate,")
            .append(" a.FBrokerCode as FPayerNum, ")
            .append(" broker.FAccountNo as FPayerAccCode, broker.FBROKERNAME as FPayerName, ")
            .append(" nvl(freturnmoney, 0) as FCommandMoney,")
            .append(" a.fbuydate as FBargainDate,")
            .append(dbl.sqlDate(settleDate)).append(" as FPreSettleDate,")
            .append(" port.FAssetCode as FFundCode,'202' as FCapitalType,'ETF赎回现金替代' as FDesc, ")
            .append(" 0 as FZDCommandMoney, ' ' as FZDChecked ")
            .append(" from (select fportcode,fbuydate,FBrokerCode,sum(freturnmoney) as freturnmoney from (")
            //必须现金替代赎回款
            .append(" select detail.fportcode,detail.fbuydate, detail.FBrokerCode,")
            .append(" detail.FinalNum /stocl.secnums * gus1.FReplaceMoney as freturnmoney")
            .append(" from (select fportcode,fbuydate,FBrokerCode, sum(FBraketNum) as FinalNum from ")
            .append(pub.yssGetTableName("Tb_ETF_TradeStlDtl"))
            .append(" tsd1 where exists (select FTradeDate from ")
            .append(pub.yssGetTableName("Tb_Ta_Trade"))
            .append(" ta where FCheckState = 1 and fselltype = '02'")
            .append(" and FPortCode = ").append(dbl.sqlString(portCode))
            .append(" and FCashReplaceDate = ").append(dbl.sqlDate(settleDate))
            .append(" and ta.FTradeDate = tsd1.FBuyDate) and FBS = 'S' ")
            .append(" and FPortCode = ").append(dbl.sqlString(portCode))
            .append(" and fbrokercode <> ' ' group by fportcode,fbuydate,FBrokerCode) detail")
            .append(" left join (select sum(FReplaceMoney) as FReplaceMoney, fportcode,fdate from ")              
            .append(pub.yssGetTableName("Tb_ETF_StockList"))
            .append(" where FReplaceMark = '6'")
            .append(" and FPortCode = ").append(dbl.sqlString(portCode))
            .append(" group by fportcode,fdate) gus1 on gus1.fportcode = detail.fportcode and gus1.fdate = detail.fbuydate")
            .append(" left join (select stoc.fportcode,stoc.fdate, count(stoc.fsecuritycode) as secnums from ")
            .append(pub.yssGetTableName("Tb_Etf_StockList"))
            .append(" stoc where stoc.fportcode = ").append(dbl.sqlString(portCode))
            .append(" and stoc.freplacemark = '5'")
            .append(" group by stoc.fportcode,stoc.fdate) stocl on stocl.fdate = detail.fbuydate ")
            .append(" union all ")
            //可以现金替代赎回款
            .append(" select s.FPortCode, s.fbuydate,s.FBrokerCode,sum(s.FSumReturn * -1) as freturnmoney from ")
            .append(pub.yssGetTableName("Tb_Etf_Standingbook"))
            .append(" s join ( select max(fdate) as fdate ,fbuydate,fportcode from ")
            .append(pub.yssGetTableName("tb_etf_standingbook")) 
            .append(" where frefunddate = " + dbl.sqlDate(settleDate))
            .append(" and fportcode = " + dbl.sqlString(portCode))
            .append(" and fbs = 'S' group by  fportcode,fbuydate)")
            .append(" mk on mk.fdate = s.fdate and mk.fbuydate = s.fbuydate and mk.fportcode = s.fportcode")
            .append(" where s.FBS = 'S' and s.fbrokercode <> ' ' ")
            .append(" and s.FPortCode = ").append(dbl.sqlString(portCode))
            .append(" and s.FReFundDate = ").append(dbl.sqlDate(settleDate))
            .append(" group by s.FPortCode, s.fbuydate,s.FBrokerCode ) group by FPortCode, fbuydate,FBrokerCode) a ")
            .append(" left join (select FSeatCode, FACCOUNTNO, FBROKERNAME from ")
            .append(pub.yssGetTableName("Tb_ETF_BROKER"))
            .append(" where FCheckState = 1) broker on a.FBrokerCode = broker.FSeatCode ")
            .append(" left join (select FAssetCode, FPortCode from ")
            .append(pub.yssGetTableName("Tb_Para_Portfolio"))
            .append(" where FcheckState = 1) port on port.FPortCode = a.FPortCode )");
			
			return sb.toString();
		}catch(Exception e){
			throw new YssException(e.getMessage());
		}
	}
	
	/**
	 * add by songjie 2013.05.09
	 * BUG 7773 QDV4赢时胜(上海)2013年05月08日01_B
	 * 生成汇总项报表数据
	 */
	private void createSumData(PreparedStatement pst, String assetCode) throws YssException{
		String strSql = "";
		ResultSet rs = null;
		double preReceiveMSum = 0;//应收款合计
		double prePaySum = 0;//应付款合计
		double paySum = 0;//银行存款账户应付款合计
		
		double etfCptStlSum = 0;//ETF资金交收汇总
		double etfCashBalance = 0;//ETF现金差额
		double etfRefundMoney = 0;//ETF退补款
		double etfRedeemMoney = 0;//ETF赎回款（赎回现金替代）
		double etfRplcMoney = 0;//ETF现金替代
		
		double etfLinkCptStlSum = 0;//联接资金交收汇总
		double etfLinkCashBalance = 0;//联接基金现金差额
		double etfLinkRefundM = 0;//联接基金退补款
		double etfLinkRedeemM = 0;//联接基金赎回款（赎回现金替代）
		double etfLinkRplcM = 0;//联接基金现金替代
		try{
			strSql = "  select sum(FCommandMoney) as FCommandMoney,FFundCode,FCuryCode from " + 
			pub.yssGetTableName("Tb_ETF_NoGrtRep") + 
			" where FReceiverNum in (select FClearNum from " + pub.yssGetTableName("Tb_ETF_Param") + 
			" where FPortCode = " + dbl.sqlString(portCode) + ") and FSettleDate = " + dbl.sqlDate(settleDate) + 
			" and FFundCode in(select FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
			" where FPortCode = " + dbl.sqlString(portCode) + ") group by  FFundCode, FCuryCode ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				preReceiveMSum = rs.getDouble("FCommandMoney");
			}
			
			dbl.closeResultSetFinal(rs);
			
			//合计项：应收款合计
			pst.setString(1, "应收款合计");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, "RMB");
			pst.setDouble(14, preReceiveMSum);
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum01");
			
			pst.addBatch();
			
			strSql = "  select sum(rep.FCommandMoney) as FCommandMoney,rep.FFundCode,rep.FCuryCode from " + 
			pub.yssGetTableName("Tb_ETF_NoGrtRep") + 
			" rep where not exists (select FClearNum from " + pub.yssGetTableName("Tb_ETF_Param") + 
			" para where para.FPortCode = " + dbl.sqlString(portCode) + 
			" and para.FClearNum = rep.FReceiverNum) and FSettleDate = " + dbl.sqlDate(settleDate) + 
			" and FFundCode in(select FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
			" where FPortCode = " + dbl.sqlString(portCode) + ") group by rep.FFundCode,rep.FCuryCode ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				prePaySum = rs.getDouble("FCommandMoney");
				//合计项：应付款合计
				pst.setString(1, "应付款合计");
				pst.setDate(2, YssFun.toSqlDate(settleDate));
				pst.setString(3, " ");
				pst.setString(4, " ");
				pst.setString(5, " ");
				pst.setString(6, " ");
				pst.setString(7, " ");
				pst.setString(8, " ");
				pst.setString(9, " ");
				pst.setString(10, " ");
				pst.setString(11, " ");
				pst.setString(12, " ");
				pst.setString(13, rs.getString("FCuryCode") + "");
				pst.setDouble(14, rs.getDouble("FCommandMoney"));
				pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
				pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
				pst.setString(17, rs.getString("FFundCode") + "");
				pst.setString(18, " ");
				pst.setString(19, " ");
				pst.setDouble(20, 0);
				pst.setString(21, " ");
				pst.setString(22, " ");
				pst.setString(23, " ");
				pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
				pst.setString(25, "1");
				pst.setString(26, "");
				pst.setString(27, "");
				pst.setString(28, "sum02");
				
				pst.addBatch();
			}
			
			dbl.closeResultSetFinal(rs);
			
			paySum = YssD.sub(prePaySum, preReceiveMSum); //银行存款账户应付款合计 = 应付款合计 - 应收款合计
			
			strSql = " select para.FCashAccCode, cash.FBankAccount as CashBankNum, cash.FBankAccName as CashBankName, " + 
			" para.FClearAccCode, clear.FBankAccount as ClearBankNum, clear.FBankAccName as ClearBankName from " + 
			pub.yssGetTableName("Tb_ETF_Param") + " para " + 
			" left join " + 
			" (select FCashAccCode,FBankAccount,FBankAccName from " + pub.yssGetTableName("Tb_Para_Cashaccount") + 
			") cash on cash.FCashAccCode = para.FCashAccCode " + 
			" left join " +
			" (select FCashAccCode,FBankAccount,FBankAccName from " + pub.yssGetTableName("Tb_Para_Cashaccount") + 
			") clear on clear.FCashAccCode = para.FClearAccCode " + 
			" where para.FPortCode = " + dbl.sqlString(portCode);
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				//合计项：银行存款账户应付款合计
				//--- edit by songjie 2013.05.28 STORY #3995 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 start---//
				if(paySum <= 0){//如果应收款合计 >= 应付款合计，则  该合计项显示为"银行存款账户应收款合计"
					pst.setString(1, "银行存款账户应收款合计");
				}else{//如果应收款合计 < 应付款合计，则  该合计项显示为"银行存款账户应付款合计"
					pst.setString(1, "银行存款账户应付款合计");
				}
				//--- edit by songjie 2013.05.28 STORY #3995 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 end---//
				pst.setDate(2, YssFun.toSqlDate(settleDate));
				pst.setString(3, " ");
				pst.setString(4, " ");
				pst.setString(5, " ");
				//--- edit by songjie 2013.05.28 STORY #3995 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 start---//
				//如果应收款合计 >= 应付款合计,则 
				if(paySum <= 0){
					//付方资金账户 = ETF参数设置界面的清算备付金账户对应的现金账户设置界面的银行账号
					//付款人名称 = ETF参数设置界面的清算备付金账户对应的现金账户设置界面的银行账户名称
					pst.setString(6, rs.getString("ClearBankNum") == null ? " " : rs.getString("ClearBankNum"));//付方资金帐户
					pst.setString(7, rs.getString("ClearBankName") == null ? " " : rs.getString("ClearBankName"));//付款人名称
				}else{//如果应收款合计 < 应付款合计，则
					//付方资金账户 = ETF参数设置界面的存款账户对应的现金账户设置界面的银行账号
					//付款人名称 = ETF参数设置界面的存款账户对应的现金账户设置界面的银行账户名称
					pst.setString(6, rs.getString("CashBankNum") == null ? " " : rs.getString("CashBankNum"));//付方资金帐户
					pst.setString(7, rs.getString("CashBankName") == null ? " " : rs.getString("CashBankName"));//付款人名称
				}
				//--- edit by songjie 2013.05.28 STORY #3995 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 end---//
				pst.setString(8, " ");
				pst.setString(9, " ");
				//--- edit by songjie 2013.05.28 STORY #3995 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 start---//
				if(paySum <= 0){//如果应收款合计 >= 应付款合计,则
					//收方资金账户 = ETF参数设置界面的存款账户对应的现金账户设置界面的银行账号
					//收款人名称 = ETF参数设置界面的存款账户对应的现金账户设置界面的银行账户名称
					pst.setString(10, rs.getString("CashBankNum") == null ? " " : rs.getString("CashBankNum"));//收方资金帐户
					pst.setString(11, rs.getString("CashBankName") == null ? " " : rs.getString("CashBankName"));//收款人名称
				}else{//如果应收款合计 < 应付款合计，则
					//收方资金账户 = ETF参数设置界面的清算备付金账户对应的现金账户设置界面的银行账号；
					//收款人名称 = ETF参数设置界面的清算备付金账户对应的现金账户设置界面的银行账户名称；
					pst.setString(10, rs.getString("ClearBankNum") == null ? " " : rs.getString("ClearBankNum"));//收方资金帐户
					pst.setString(11, rs.getString("ClearBankName") == null ? " " : rs.getString("ClearBankName"));//收款人名称
				}
				//--- edit by songjie 2013.05.28 STORY #3995 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 end---//
				pst.setString(12, " ");
				pst.setString(13, "RMB");
				//--- edit by songjie 2013.05.28 STORY #3995 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 start---//
				pst.setDouble(14, Math.abs(paySum));//改为获取绝对值
				//--- edit by songjie 2013.05.28 STORY #3995 需求上海-(国泰基金)QDIIV4.0(紧急)20130418001 end---//
				pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
				pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
				pst.setString(17, assetCode);//资金代码
				pst.setString(18, " ");
				pst.setString(19, " ");
				pst.setDouble(20, 0);//中登划款金额    需确定取数逻辑  
				pst.setString(21, " ");
				pst.setString(22, " ");
				pst.setString(23, " ");
				pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
				pst.setString(25, "1");
				pst.setString(26, "");
				pst.setString(27, "");
				pst.setString(28, "sum03");
				
				pst.addBatch();
			}
			
			dbl.closeResultSetFinal(rs);
			
			etfCptStlSum = -paySum;//ETF资金交收汇总 = - 银行存款账户应付款合计
			
			//合计项：ETF资金交收汇总
			pst.setString(1, "ETF资金交收汇总");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");//FPayerAccCode 付方资金帐户    需确定取数逻辑
			pst.setString(7, " ");//FPayerName  付款人名称    需确定取数逻辑
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");//FReceiverAccCode  收方资金帐户    需确定取数逻辑
			pst.setString(11, " ");//FReceiverName  收款人名称    需确定取数逻辑
			pst.setString(12, " ");
			pst.setString(13, "RMB");
			pst.setDouble(14, etfCptStlSum);//等于负“银行存款账户应付款合计”
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);//资金代码
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum04");
			
			pst.addBatch();
			
			//ETF现金差额 205
			etfCashBalance = getTotalValue(pst,"205",assetCode);
			
			//ETF退补款 203
			etfRefundMoney = getTotalValue(pst,"203",assetCode);
			
			//ETF赎回款（赎回现金替代） 202
			etfRedeemMoney = getTotalValue(pst,"202",assetCode);
			
			strSql = " select sum(FCashRepAmount) as FCashRepAmount from " + pub.yssGetTableName("Tb_TA_Trade") + 
			" where FSellType = '01' and FPortCode = " + dbl.sqlString(portCode) + 
			" and FCashReplaceDate = " + dbl.sqlDate(settleDate);
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				pst.setString(1, "ETF现金替代");
				pst.setDate(2, YssFun.toSqlDate(settleDate));
				pst.setString(3, " ");
				pst.setString(4, " ");
				pst.setString(5, " ");
				pst.setString(6, " ");
				pst.setString(7, " ");
				pst.setString(8, " ");
				pst.setString(9, " ");
				pst.setString(10, " ");
				pst.setString(11, " ");
				pst.setString(12, " ");
				pst.setString(13, " ");
				
				etfRplcMoney = rs.getDouble("FCashRepAmount");
				pst.setDouble(14, etfRplcMoney);
				
				pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
				pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
				pst.setString(17, assetCode);
				pst.setString(18, " ");
				pst.setString(19, " ");
				pst.setDouble(20, 0);
				pst.setString(21, " ");
				pst.setString(22, " ");
				pst.setString(23, " ");
				pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
				pst.setString(25, "1");
				pst.setString(26, "");
				pst.setString(27, "");
				pst.setString(28, "sum08");
				
				pst.addBatch();
			}
			
			dbl.closeResultSetFinal(rs);
			
			pst.setString(1, "联接资金交收汇总");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, " ");
			pst.setDouble(14, etfLinkCptStlSum);
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum09");
			
			pst.addBatch();
			
			pst.setString(1, "联接基金现金差额");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, " ");
			pst.setDouble(14, etfLinkCashBalance);
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum10");
			
			pst.addBatch();
			
			pst.setString(1, "联接基金退补款");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, " ");
			pst.setDouble(14, etfLinkRefundM);
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum11");
			
			pst.addBatch();
			
			pst.setString(1, "联接基金赎回款（赎回现金替代）");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, " ");
			pst.setDouble(14, etfLinkRedeemM);
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum12");
			
			pst.addBatch();
			
			pst.setString(1, "联接基金现金替代");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, " ");
			pst.setDouble(14, etfLinkRplcM);
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum13");
			
			pst.addBatch();
				
			//非联接资金交收汇总 = ETF资金交收汇总 - 联接资金交收汇总
			pst.setString(1, "非联接资金交收汇总");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, " ");
			pst.setDouble(14, YssD.sub(etfCptStlSum, etfLinkCptStlSum));
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum14");
			
			pst.addBatch();
			
			//非联接基金现金差额 = ETF现金差额 - 联接基金现金差额
			pst.setString(1, "非联接基金现金差额");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, " ");
			pst.setDouble(14, YssD.sub(etfCashBalance, etfLinkCashBalance));
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum15");
			
			pst.addBatch();
			
			pst.setString(1, "非联接基金退补款");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, " ");
			pst.setDouble(14, YssD.sub(etfRefundMoney, etfLinkRefundM));
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum16");
			
			pst.addBatch();
			
			//非联接基金赎回款（赎回现金替代）  = ETF赎回款（赎回现金替代）-联接基金赎回款（赎回现金替代）
			pst.setString(1, "非联接基金赎回款（赎回现金替代）");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, " ");
			pst.setDouble(14, YssD.sub(etfRedeemMoney, etfLinkRedeemM));
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum17");
			
			pst.addBatch();
			
			//非联接基金现金替代  = ETF现金替代-联接基金现金替代
			pst.setString(1, "非联接基金现金替代");
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");
			pst.setString(7, " ");
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");
			pst.setString(12, " ");
			pst.setString(13, " ");
			pst.setDouble(14, YssD.sub(etfRplcMoney, etfLinkRplcM));
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, "sum18");
			
			pst.addBatch();
		}catch(Exception e){
			throw new YssException("ETF非担保交收指令表生成汇总数据出错",e);
		}
	}
	
	private double getTotalValue(PreparedStatement pst ,String capitalType ,String assetCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		double dTotalValue = 0;
		String orderCode = "";
		
		try{
			strSql = "select sum(FCommandMoney) as FCommandMoney,FCapitalType from " + pub.yssGetTableName("Tb_ETF_NoGrtRep") + 
			" where FSettleDate = " + dbl.sqlDate(settleDate) + 
			" and FCapitalType = " + dbl.sqlString(capitalType) + 
			" and FFundCode in(select FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
			" where FPortCode = " + dbl.sqlString(portCode) + ") group by FCapitalType  order by FCapitalType desc ";
			
			rs = dbl.openResultSet(strSql);
			
			if(rs.next()){
				dTotalValue = rs.getDouble("FCommandMoney");
			} 
			dbl.closeResultSetFinal(rs);
			
			if(capitalType.equals("205")){
				pst.setString(1, "ETF现金差额");
				orderCode = "sum05";
			}else if(capitalType.equals("203")){
				pst.setString(1, "ETF退补款");
				orderCode = "sum06";
			}else{
				pst.setString(1, "ETF赎回款（赎回现金替代）");
				orderCode = "sum07";
			}
			pst.setDate(2, YssFun.toSqlDate(settleDate));
			pst.setString(3, " ");
			pst.setString(4, " ");
			pst.setString(5, " ");
			pst.setString(6, " ");//FPayerAccCode 付方资金帐户    需确定取数逻辑
			pst.setString(7, " ");//FPayerName  付款人名称    需确定取数逻辑
			pst.setString(8, " ");
			pst.setString(9, " ");
			pst.setString(10, " ");//FReceiverAccCode  收方资金帐户    需确定取数逻辑
			pst.setString(11, " ");//FReceiverName  收款人名称    需确定取数逻辑
			pst.setString(12, " ");
			pst.setString(13, " ");
			
			pst.setDouble(14, dTotalValue);
			pst.setDate(15, YssFun.toSqlDate("1900-01-01"));
			pst.setDate(16, YssFun.toSqlDate("1900-01-01"));
			pst.setString(17, assetCode);
			pst.setString(18, " ");
			pst.setString(19, " ");
			pst.setDouble(20, 0);
			pst.setString(21, " ");
			pst.setString(22, " ");
			pst.setString(23, " ");
			pst.setString(24,YssFun.formatDatetime(new java.util.Date()) + "");
			pst.setString(25, "1");
			pst.setString(26, "");
			pst.setString(27, "");
			pst.setString(28, orderCode);
			
			pst.addBatch();
			
			return dTotalValue;
		}catch(Exception e){
			throw new YssException("生成ETF非担保交收指令汇总数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
