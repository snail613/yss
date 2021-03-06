package com.yss.main.operdeal.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.yss.dsub.BaseBean;
import com.yss.dsub.BaseReportBean;
import com.yss.main.cusreport.RepDataSourceBean;
import com.yss.main.cusreport.RepFormatBean;
import com.yss.main.cusreport.RepPretreatBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.dao.IBuildReport;
import com.yss.main.dao.IOperValue;
import com.yss.main.dao.IYssConvert;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.report.navrep.CtlNavRep;
import com.yss.main.platform.pfsystem.facecfg.pojo.FaceCfgParamBean;
import com.yss.main.report.CommonRepBean;
import com.yss.main.syssetting.DataDictBean;
import com.yss.pojo.param.comp.YssCommonRepCtl;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

public class BaseBuildCommonRep extends BaseReportBean implements IBuildReport {
	private String repCode = "";
	private CommonRepBean repBean;
	private ArrayList alRepParam;
	private ArrayList alRepParamBak;
	private boolean colorFlag;
	private int rowCount = 0;
	// begin zhouxiang MS01487 在没有设置“亮色筛选条件”时，可以查询出多条记录 加了以个控制参数 countRule
	private int countRule = 0;
	private String strOffAcctInfo = "";// 封账返回封账信息 add by qiuxufeng 20101108
	//add by licai 20110130 STORY #441 需优化现在的报表自定义模板
	private boolean bIsDynTabHead=false;//动态表头标记
	private LinkedHashMap hmDynRowCodesPerDynColumnCode=new LinkedHashMap();//每一组合代码等动态列对应的券商代码代码等的动态行,例如：键值对（组合代码：券商代码,券商代码）<String,String>
	private int iCount=0;//组合等的个数
	private String strRptStyle="";//自定义报表（报表格式代码,数据源代码）
	private int iColumnQty=0;//表头列数
	private String strDynColumnCode="";//组合代码等动态列
	private int iDynHeadStartRow=0;//动态条件表头(如组合、投资经理等)开始行
	private int iDynHeadStartColumn=0;//动态条件表头(如组合、投资经理等)开始列	
	
	private int insertRows=0;	//插入行数	add by huangqirong 2011-10-22 story #1747
	private int fixRows=0;		//固定行数	add by huangqirong 2011-10-22 story #1747
	private int dsStarRows = 0;		//报表数据行数行数 ,起始索引行	add by huangqirong 2012-02-23 story #1284
	
	//add by huangqirong 2012-01-06  story #1284  数据源字段参数序号 
	private int dsFieldNum=1;	//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
	public int getDsFieldNum() {
		return dsFieldNum;
	}

	public void setDsFieldNum(int dsFieldNum) {
		this.dsFieldNum = dsFieldNum;
	}
	//---end---
	
	private Hashtable<String, Integer> htParamDSSynRow = new Hashtable<String, Integer>(); //add by huangqirong 2012-05-25 story #2473 
	private Hashtable<String, String> htParamDSSynCount = new Hashtable<String, String>(); //add by huangqirong 2012-05-25 story #2473 
	private Hashtable<String, Integer>  htCountRows = new Hashtable<String, Integer>();	//add by huangqirong 2012-05-25 story #2473 
	
	//add by licai 20110130 STORY #441======================end

	// QDV4太平2010年09月16日03_A

	public BaseBuildCommonRep() {
	}

	/**
	 * initBuildReport
	 * 
	 * @param bean
	 *            BaseBean
	 */
	public void initBuildReport(BaseBean bean) throws YssException {
		String[] sRepCtlParamAry = null;
		YssCommonRepCtl repParam = null;
		//add by licai 20110209 STORY #441 需优化现在的报表自定义模板
		ResultSet rsDynColumnCodeName=null;//组合代码名称结果集
		ResultSet rsRpt=null;//(报表格式代码,数据源代码)和动态表头信息结果集
		String strDynRowCodes="";
		String strDynColumnAndRowCondition="";
		//add by licai 20110209 STORY #441 ====================end
		repBean = (CommonRepBean) bean;
		repCode = repBean.getRepCode();
		try{
			//add by licai 20110209 STORY #441 需优化现在的报表自定义模板
			bIsDynTabHead = repCode.toLowerCase().indexOf("dyn") > 0;// 自定义报表代码中包含"dyn"则为动态表头(dynamic)
			if (bIsDynTabHead) {
				// 查询报表格式代码和数据源代码
				String strSql = "select case  when FSUBDSCODES <> ' ' then  FREPFORMATCODE || ',' || FSUBDSCODES else  FREPFORMATCODE end as FRptSyle"
						+ " from "
						+ pub.yssGetTableName("tb_rep_custom")
						+ " where FCUSREPCODE=" + dbl.sqlString(repCode);
				rsRpt = dbl.openResultSet(strSql);
				if (rsRpt.next())
					strRptStyle = rsRpt.getString("FRptSyle");
				rsRpt.getStatement().close();
				// 查询动态表头列和行条件
				strSql = "select ce.FCONTENT from "
						+ pub.yssGetTableName("tb_rep_cell")
						+ " ce,"
						+ pub.yssGetTableName("tb_rep_custom")
						+ " cu "
						+ "where ce.frelacode = cu.frepformatcode"
						+ " and (ce.FCONTENT like '动态表头:%' or FCONTENT like '动态表头：%')"
						+ " and  cu.fcusrepcode ="
						+ dbl.sqlString(this.repCode);
				rsRpt = dbl.openResultSet(strSql);
				if (rsRpt.next()) {
					strDynColumnAndRowCondition = rsRpt.getString("FCONTENT");// 动态列表头条件
				}
				rsRpt.getStatement().close();
			}
			//add by licai 20110209 STORY #441 ====================end
			alRepParam = new ArrayList();
			if (repBean.getRepCtlParam().length() > 0) {
				if (
				// repBean.getRepCtlParam().indexOf("\n") > 0 &&
				repBean.getRepCtlParam().indexOf("\r") > 0) { // 判断是否为动态数据源报表的协议，因为有的固定报表不使用该协议
					sRepCtlParamAry = repBean.getRepCtlParam().split("\n");
					for (int i = 0; i < sRepCtlParamAry.length; i++) {
						repParam = new YssCommonRepCtl();
						repParam.parseRowStr(sRepCtlParamAry[i]);
						alRepParam.add(repParam);
						// add by licai 20110209 STORY #441 需优化现在的报表自定义模板
						if (bIsDynTabHead) {// 取得组合或券商等名称，具体要根据动态列表头条件进行判断并取值，通过getDynColumnCodeName方法取得动态列名称
							String strCtlNameCode = repParam.getCtlNameCode();
							if (strCtlNameCode
									.equalsIgnoreCase("DynColumnCodeSelCtl")) {
								// int iPortIndex=0;
								this.strDynColumnCode = repParam.getCtlValue();
								StringBuffer sbDynColumnCodeName = new StringBuffer();
								String strDynColumnCodeName = "";
								strDynColumnCodeName = getDynColumnCodeName(strDynColumnAndRowCondition);
								rsDynColumnCodeName = dbl.openResultSet(
										strDynColumnCodeName,
										ResultSet.TYPE_SCROLL_INSENSITIVE);
								while (rsDynColumnCodeName.next()) {
									sbDynColumnCodeName
											.append(iCount++)
											.append("|")
											.append(
													rsDynColumnCodeName
															.getString("FportCodeName"))
											.append(
													rsDynColumnCodeName
															.isLast() ? ""
															: ",");
								}
								this.strDynColumnCode = sbDynColumnCodeName
										.toString();
							} else if (strCtlNameCode
									.equalsIgnoreCase("DynRowCodesSelCtl")) {
								strDynRowCodes = repParam.getCtlValue();
							}
						}
						// add by licai 20110209 STORY
						// #441=====================end
					}
				}
			}
			alRepParamBak = (ArrayList) alRepParam.clone();
			//add by licai 20110209 STORY #441 需优化现在的报表自定义模板
			if (!strDynRowCodes.equals("") && !strDynColumnCode.equals("")) {
				String[] aryDynColumnCodes = strDynColumnCode.split(",");
				for (int i = 0; i < aryDynColumnCodes.length; i++) {
					hmDynRowCodesPerDynColumnCode.put(aryDynColumnCodes[i],
							strDynRowCodes);// 暂定每个组合对应的券商代码都一样
				}
			}
			//add by licai 20110209 STORY #441 =====================end
		}catch(Exception e){
			throw new YssException(e);
		}finally{
			dbl.closeResultSetFinal(rsRpt);
			dbl.closeResultSetFinal(rsDynColumnCodeName);
		}
	}

	/**add by licai 20110223 STORY #441 需优化现在的报表自定义模板
	 * 取得动态表头列代码和名称字符串，可以根据动态列表头条件进行查询取值
	 * @param strDynColumnCondition
	 * @return
	 */
	private String getDynColumnCodeName(String strDynColumnCondition) {
		String strSql = "";
		String strDynColumnCodeName = strDynColumnCondition.split(":")[3]
				.split(",")[0].trim();
		if (strDynColumnCodeName.equals("投资经理")) {
			strSql = "select '['||FInvMgrCode ||']'||'|'||FInvMgrName as FportCodeName from " //modify by huangqirong 2011-10-10 story #1387
					+ pub.yssGetTableName("Tb_Para_InvestManager")
					+ " where FInvMgrCode in "
					+ "("
					+ operSql.sqlCodes(strDynColumnCode) + ")";
			;
		}
		if (strDynColumnCodeName.equals("投资组合")) {
			strSql = "select '['||fportcode ||']'||'|'||fportname as FportCodeName from "
					+ pub.yssGetTableName("tb_para_portfolio")
					+ " where fportcode in "
					+ "("
					+ operSql.sqlCodes(strDynColumnCode) + ")";
		}
		if (strDynColumnCodeName.equals("交易券商")) {
			strSql = "select '['||FBrokerCode ||']'||'|'||FBrokerName as FportCodeName from "
					+ pub.yssGetTableName("Tb_Para_Broker")
					+ " where FBrokerCode in "
					+ "("
					+ operSql.sqlCodes(strDynColumnCode) + ")";
		}
		//add by huangqirong 2011-10-10 story #1387
		if (strDynColumnCodeName.equals("基金管理人")) {
			strSql = "select '['||FManagerCode ||']'||'|'||FManagerName as FportCodeName from "
					+ pub.yssGetTableName("tb_para_manager")
					+ " where FManagerCode in "
					+ "("
					+ operSql.sqlCodes(strDynColumnCode) + ")";
		}
		if (strDynColumnCodeName.equals("基金托管人")) {
			strSql = "select '['||FTrusteeCode ||']'||'|'||FTrusteeName as FportCodeName from "
					+ pub.yssGetTableName("Tb_Para_Trustee")
					+ " where FTrusteeCode in "
					+ "("
					+ operSql.sqlCodes(strDynColumnCode) + ")";
		}
		//---end---
		return strSql;
	}

	protected void createTmpTable(String sBeans) throws YssException {
		String[] sBeanAry = null;
		IOperValue ov = null;
		if (sBeans == null || sBeans.trim().length() == 0) {
			return;
		}
		sBeanAry = sBeans.split(",");
		for (int i = 0; i < sBeanAry.length; i++) {
			ov = (IOperValue) pub.getOperDealCtx().getBean(sBeanAry[i]);
			ov.setYssPub(pub);
			if(sBeans.equals("SellInterest")){//add by zhaoxian 20120906 #2883 story 2012年8月/17日/QDV4南方基金2012年8月17日01_A.
				if(!dbl.yssTableExist("TMP_REP_XS_SGKLL")){
					createTmpTable();
				};
				deleteTmpInter();   //删除临时表历史数据（只针对申购计息查询明细报表）
			}
			// param.parseRowStr(repBean.getRepCtlParam());
			ov.init(repBean.getRepCtlParam());
			// ov.invokeOperMothed();
			// ============添加封账条件，如已封账，返回封账信息 edit by qiuxufeng 20101108
			// QDV4太平2010年09月16日03_A
			
			//---add by songjie 2012.10.17 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			if(ov instanceof CtlNavRep){
			    //设置模块代码 以便保存日志数据
				((CtlNavRep) ov).setFunName("navdata");
			}
			//---add by songjie 2012.10.17 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			
			Object tmpObj = ov.invokeOperMothed();
			if (null == tmpObj)
				strOffAcctInfo = "";// 有报表返回null，处理成空字符串
			if (tmpObj instanceof String) {
				strOffAcctInfo = (String) tmpObj;
			}
			// ============end============
		}
	}

	/**
	 * buildReport
	 * 
	 * @param sType
	 *            String
	 * @return String
	 */
	public String buildReport(String sType) throws YssException {
		ResultSet rs = null;
		ResultSet rsSub = null;
		String strSql = "";
		String sResult = "";
		// StringBuffer buf = new StringBuffer();
		// 为了避免类成员变量对程序其他功能造成影响，每次生成报表的时候清0 sunkey 20081222
		rowCount = 0;
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Custom")
					+ " where FCusRepCode = " + dbl.sqlString(repCode)
					+ " and FCheckState = 1";
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				createTmpTable(rs.getString("FTmpTables"));
				// 添加封账条件，如已封账，返回封账信息 add by qiuxufeng QDV4太平2010年09月16日03_A
				if (strOffAcctInfo != "")
					return strOffAcctInfo;

				//doPretreat(rs.getString("FSubDsCodes"));// 进行数据源预处理操作 add by jiangshichao  2010.07.08
				if (rs.getString("FParamSource") != null
						&& rs.getString("FParamSource").trim().length() > 0) { // 有参数来源的报表
					/**add---shashijie 2013-6-8 BUG 8200 重构调用统一方法*/
					strSql = getRepDatasourceSQL(rs.getString("FParamSource"), "");
					/**end---shashijie 2013-6-8 BUG 8200*/
					rsSub = dbl.openResultSet(strSql);
					if (rsSub.next()) {
						strSql = dbl.clobStrValue(rsSub.getClob("FDataSource"));
						strSql = this.buildSql(strSql);
					} else {
						strSql = "";
					}
					dbl.closeResultSetFinal(rsSub);
					if (strSql.length() > 0) {
						rsSub = dbl.openResultSet(strSql);
						// 设置行数参数，参数数据源是有多条记录的，为了避免存储表只保留最后一条记录，用此参数区分 sunkey
						// 20081222
						rowCount = 0;
						while (rsSub.next()) {
							setRepRsParam(rsSub);
							if(!sType.equalsIgnoreCase("getsearch"))//add by huangqirong 2011-07-20 story #1101
								doPretreat(rs.getString("FSubDsCodes"));//STORY #1173 希望报表预处理数据源能获取参数数据源传来的参数 add by jiangshichao
							if (rs.getString("FRepType").equalsIgnoreCase("0")) { // 明细组合
								sResult += buildAllDataSource(rs
										.getString("FSubDsCodes"));
							} else if (rs.getString("FRepType")
									.equalsIgnoreCase("1")) { // 汇总组合
								sResult += buildCusSumRep(rs
										.getString("FSubRepCodes"))
										+ "\n\n\n\n";
							}
							rowCount++;
						}
						if (rs.getString("FRepType").equalsIgnoreCase("0")
								&& sResult.length() >= 2) {
							sResult = sResult
									.substring(0, sResult.length() - 2);
						}
						if (rs.getString("FRepType").equalsIgnoreCase("1")
								&& sResult.length() >= 4) {
							sResult = sResult
									.substring(0, sResult.length() - 4);
						}
						this.htParamDSSynRow.clear();//add by huangqirong 2012-05-25 story #2473
						this.htParamDSSynCount.clear();//add by huangqirong 2012-05-25 story #2473
						this.htCountRows.clear();//add by huangqirong 2012-05-25 story #2473						
					}
					alRepParam = alRepParamBak;
				} else {
					// createTmpTable(rs.getString("FTmpTables"));
					if(!sType.equalsIgnoreCase("getsearch"))//add by huangqirong 2011-07-20 story #1101
						doPretreat(rs.getString("FSubDsCodes"));//STORY #1173 希望报表预处理数据源能获取参数数据源传来的参数 add by jiangshichao
					if (rs.getString("FRepType").equalsIgnoreCase("0")) { // 明细组合
						sResult = buildAllDataSource(rs
								.getString("FSubDsCodes"));
					} else if (rs.getString("FRepType").equalsIgnoreCase("1")) { // 汇总组合
						sResult += buildCusSumRep(rs.getString("FSubRepCodes"));
					}		
				}
			}
			return sResult;
		} catch (Exception e) {
			// throw new YssException(e.getMessage());
			throw new YssException(e); // by caocheng 2009.02.04 MS00004
			// QDV4.1-2009.2.1_09A
			// 新的异常处理机制无需使用e.getMessage
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsSub);
		}
	}
	//add by zhaoxian 20120906 #2883 story 2012年8月/17日/QDV4南方基金2012年8月17日01_A.
  //此方法只正对南方基金申购款计息明细查询报表使用，每次查询时先删除临时表记录，以免数据重复
	private void deleteTmpInter() throws YssException{
		String strSql="";
		Connection con=null;
		Boolean bTrans=false;
		try{
			con=dbl.loadConnection();
			con.setAutoCommit(false);  //设置不自动提交事务
			bTrans=true;   //开启事务
			strSql="delete from TMP_REP_XS_SGKLL";
			dbl.executeSql(strSql);
			con.commit();  //提交事务
			bTrans = false;   //关闭事务
			con.setAutoCommit(true);   //设置自动提交事务
		}catch(Exception e){
			throw new YssException("删除临时表出错！");
		}finally{
			dbl.endTransFinal(con, bTrans);
		}
	}
	/*
	 * add by zhaoxian 20120906 #2883 story 2012年8月/17日/QDV4南方基金2012年8月17日01_A.
	 */
	private void createTmpTable() throws YssException{
		String strSql="";
		Connection con=null;
		Boolean bTrans=false;
		try{
			con=dbl.loadConnection();
			con.setAutoCommit(false);  //设置不自动提交事务
			bTrans=true;   //开启事务
			strSql="create table TMP_REP_XS_SGKLL " 
				 +"( FINTERESTRATE  NUMBER(30,8) not null,"
				 +" FINTERESTTYPE  VARCHAR2(50) not null,"
				 +" FPORTCURYMONEY NUMBER(30,8) not null,"
				 +"FSELLMONEY     NUMBER(30,8) not null,"
				 +" FSELLNETCODE   VARCHAR2(50) not null,"
				 +"FTRANSDATE     VARCHAR2(50) not null"
                +")";
			dbl.executeSql(strSql);
			con.commit();  //提交事务
			bTrans = false;   //关闭事务
			con.setAutoCommit(true);   //设置自动提交事务
		}catch(Exception e){
			throw new YssException("创建临时表出错！");
		}finally{
			dbl.endTransFinal(con, bTrans);
		}
		
		
	}
	protected void setRepRsParam(ResultSet rs) throws YssException {
		ResultSetMetaData rsmd = null;
		YssCommonRepCtl repParam = null;
		try {
			rsmd = rs.getMetaData();
			/**shashijie 2012-11-16 STORY 3187 获得控件名称顺序和list中一致*/
			List ctlNameList = getCtlName(alRepParam);
			/**end shashijie 2012-11-16 STORY */
			alRepParam.clear();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				repParam = new YssCommonRepCtl();
				repParam.setCtlIndex(String.valueOf(i));
				repParam.setCtlValue(rs.getString(i));
				/**shashijie 2012-11-16 STORY 3187 增加控件名称,因为后台可能要根据控件名称取控件的相关参数
				 * 目前只能支持日期与组合选择框(批量报表导出时),而且是根据顺序直接复职不做判断,非常不安全耦合性太强,以后有待加强*/
				String ctlName = ctlNameList.size()>=i ? (String)ctlNameList.get(i-1) : "";
				repParam.setCtlNameCode(ctlName);
				/**end shashijie 2012-11-16 STORY 3187 */
				alRepParam.add(repParam);
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	/**shashijie 2012-11-16 STORY 3187 根据控件值来判断,增加控件名称,因为后台可能要根据控件名称取控件的相关参数 */
	private ArrayList getCtlName(ArrayList paramList) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < paramList.size(); i++) {
			YssCommonRepCtl ctl = (YssCommonRepCtl)paramList.get(i);
			list.add(ctl.getCtlNameCode());
		}
		return list;
	}

	protected String buildCusSumRep(String sSubRepCodes) throws YssException {
		String[] sSubRepAry = null;
		StringBuffer buf = new StringBuffer();
		RepFormatBean repFmt = new RepFormatBean();
		String strSql = "";
		ResultSet rs = null;
		String sResult = "";
		try {
			sSubRepAry = sSubRepCodes.split(",");
			repFmt.setYssPub(pub);
			for (int i = 0; i < sSubRepAry.length; i++) {
				strSql = "select * from "
						+ pub.yssGetTableName("Tb_Rep_Custom")
						+ " where FCusRepCode = "
						+ dbl.sqlString(sSubRepAry[i]) + " and FCheckState = 1";
				rs = dbl.openResultSet(strSql);
				if (rs.next()) {
					if (rs.getString("FRepType").equalsIgnoreCase("1")) { // 20071126
						// chenyibo
						// 实现汇总报表下能够嵌套汇总报表的功能
						this.repCode = sSubRepAry[i];
						sResult = this.buildReport("");
						buf.append(sResult).append("\n\n\n\n");
					} else {
						repFmt.setRepCode(rs.getString("FRepFormatCode"));
						buf.append(repFmt.getListViewData3());
						buf.append("\f\f\f\f");
						this.repCode = sSubRepAry[i];
						buf.append(this.buildReport(""));
						buf.append("\n\n\n\n");
					}
				}
				dbl.closeResultSetFinal(rs);
			}
			if (buf.length() >= 4) {
				buf.setLength(buf.length() - 4);
			}
			return buf.toString();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/*
	 * add by huangqirong 2012-01-30 story #1284
	 * 汇总报表调用预处理
	 * */
	private void pretreatSumRep(String sSubRepCodes) throws YssException {
		String[] sSubRepAry = null;
		RepFormatBean repFmt = new RepFormatBean();
		String strSql = "";
		ResultSet rs = null;
		ResultSet subRs = null;		
		try {
			sSubRepAry = sSubRepCodes.split(",");
			repFmt.setYssPub(pub);
			for (int i = 0; i < sSubRepAry.length; i++) {
				strSql = "select * from "
						+ pub.yssGetTableName("Tb_Rep_Custom")
						+ " where FCusRepCode = "
						+ dbl.sqlString(sSubRepAry[i]) + " and FCheckState = 1";
				rs = dbl.openResultSet(strSql);
				if (rs.next()) {
					if (rs.getString("FRepType").equalsIgnoreCase("1")) {
						// 实现预处理汇总报表下能够嵌套汇总报表的功能
						this.repCode = sSubRepAry[i];
						subRs=dbl.openResultSet(strSql);
						if(subRs.next()){
							this.pretreatSumRep(subRs.getString("FSUBREPCODES")); //汇总报表递归处理
						}
						dbl.closeResultSetFinal(subRs);						
						this.doPretreat(rs.getString("FSUBDSCODES"));						
					} else {
						this.repCode = sSubRepAry[i];
						this.doPretreat(rs.getString("FSUBDSCODES"));
					}
				}
				dbl.closeResultSetFinal(rs);
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	protected String buildAllDataSource(String sSubDsCodes) throws YssException {
		String[] sDsGrpAry = null;
		String sTmp = "";
		StringBuffer buf = new StringBuffer();
		try {
			sDsGrpAry = (sSubDsCodes + "").split(",");
			for (int i = 0; i < sDsGrpAry.length; i++) {
				sTmp = buildDataSource(sDsGrpAry[i]);
				if (sTmp.length() > 0) {
					buf.append(sTmp).append("\f\f\r\r"+this.insertRows).append("\f\n");	//modify by huangqirong 2011-10-22 story #1747				
				}				
			}
			// if (buf.length() >= 2) {
			// buf.setLength(buf.length() - 2);
			// }
			return buf.toString();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	protected String buildDataSource(String sDsCode) throws YssException {
		ResultSet rs = null;
		ResultSet rsSub = null;
		String strSql = "";
		StringBuffer buf = new StringBuffer();
		String sResult = "";
		String sDataStr = "";
		String sColorFilter = "";
		try {
			if(sDsCode.equals("DS_QsBalance"))// add by yeshenghong story2525 20120519
			{
				this.CreateClearBanlanceTmpTable();
			}
			
//			strSql = " select count(*) as sum from Tmp_QSBalance_" + pub.getUserCode();
//			rs = dbl.openResultSet(strSql);
//			if(rs.next())
//			{
//				System.out.println("****************:" + rs.getInt("sum"));
//			}
			/**add---shashijie 2013-6-8 BUG 8200 重构调用统一方法*/
			strSql = getRepDatasourceSQL(sDsCode, "");
			/**end---shashijie 2013-6-8 BUG 8200*/
			rs = dbl.openResultSet(strSql);
			if (sDsCode.equalsIgnoreCase("DS00072")) {
				//int t = 1;//无用注释
			}
			if (sDsCode.equalsIgnoreCase("DS00027")) {
				//int t = 1;//无用注释
			}
			if (rs.next()) {
				//modify by huangqirong 2012-05-25 story #2473
				String [] fillRange = rs.getString("FFillRange").split(",");				
				
				if("0".equalsIgnoreCase(rs.getString("FParamDSSynCount")) && this.htParamDSSynCount.containsKey(sDsCode)) //不需要执行多次
					return "";
				else if("0".equalsIgnoreCase(rs.getString("FParamDSSynCount")) && !this.htParamDSSynCount.containsKey(sDsCode))
					this.htParamDSSynCount.put(sDsCode, sDsCode);
				
				if("1".equalsIgnoreCase(rs.getString("FParamDSSynRow")) && "1".equalsIgnoreCase(rs.getString("FParamDSSynCount"))){					
					if(this.htParamDSSynRow.containsKey(sDsCode))
						fillRange[0] = this.htParamDSSynRow.get(sDsCode) + "";
					else
						htParamDSSynRow.put(sDsCode, Integer.parseInt(fillRange[0]));
					
					buf.append(fillRange[0] + "," + fillRange[1] + "," + fillRange[2] + "," + fillRange[3]).append("\f\f");
				}else {
					buf.append(rs.getString("FFillRange")).append("\f\f");
				}
				//---end---
				
				if (rs.getInt("FDsType") == 1) { // 动态数据源
					/*strSql = buildSql(dbl.clobStrValue(rs
							.getClob("FDataSource")));//为便于处理动态表头报表，将动态数据源的sql获取方法移至buildDsRowStr()方法内
					// rsSub = dbl.openResultSet(strSql);
					rsSub = dbl.openResultSet(strSql,
							ResultSet.TYPE_SCROLL_INSENSITIVE); // 在方法中需要这样的记录集，故修改.sj
					// 20080229
					sColorFilter = doColorFilter(rsSub, rs
							.getString("FRepDsCode"));
					//edit by licai 20110209 STORY #441 需优化现在的报表自定义模板
					/*sDataStr = this.buildDsRowStr(rsSub,rs
							.getString("FRepDsCode"), rs
							.getString("FFillRange").split(","), rs
							.getLong("FTRowColor"), rs.getLong("FBRowColor"),
							rs.getString("FStorageTab"));*/
					//add by songjie 2011.07.01 BUG 2168 QDV4长信2011年6月24日01_B 添加亮色筛选条件的获取代码
					sColorFilter = doColorFilter(rsSub, rs.getString("FRepDsCode"));
					
					//add by huangqirong 2011-10-22 story #1747					
					String tempFixRows=rs.getString("FFIXROWS");
					if(tempFixRows != null){
						if(tempFixRows.trim().length()==0)
							this.fixRows=0;
						else
							this.fixRows=Integer.parseInt(tempFixRows);						
					}else
						this.fixRows=0;
					//---end---
					
					//modify huangqirong 2012-01-19 story #1284 数据源为空则不执行
					if(dbl.clobStrValue(rs.getClob("FDataSource")).trim().length()==0){
						sDataStr="";
					}else {
						sDataStr = this.buildDsRowStr(dbl.clobStrValue(rs
								.getClob("FDataSource")),rs
								.getString("FRepDsCode"), rs
								.getString("FFillRange").split(","), rs
								.getLong("FTRowColor"), rs.getLong("FBRowColor"),
								rs.getString("FStorageTab"));
					}
					//---end---
					//edit by licai 20110209 STORY #441 ====================end
					if (sDataStr.length() == 0) {
						buf.setLength(0);
					}
					buf.append(sDataStr);
					dbl.closeResultSetFinal(rsSub);
				} else if (rs.getInt("FDsType") == 2) { // 固定数据源
					IBuildReport bRep = (IBuildReport) pub.getOperDealCtx()
							.getBean(rs.getString("FBeanId"));
					bRep.setYssPub(pub);
					bRep.initBuildReport(repBean);
					sDataStr = bRep.buildReport("");
					buf.append(sDataStr);
				} 
				/**shashijie 2012-10-16  做需求2935 发现这里"参数","提示","静态"数据源类型都走这个"静态"if,特此修复这个BUG*/
				  else if (rs.getInt("FDsType") == 0) { // 静态数据源
					buf.append(dbl.clobStrValue(rs.getClob("FDataSource")));
				}
				/**end shashijie 2012-10-16 STORY 2935*/
				
			}
			// add by jiangshichao
			if (!sColorFilter.equalsIgnoreCase("")) {
				sResult = sColorFilter + "####" + buf.toString();
			} else {
				sResult = buf.toString();
			}
			if (sResult.length() > 2) {
				sResult = sResult.substring(0, sResult.length() - 2);
			}
			return sResult;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsSub);
		}
	}
	
	//创建清算现金余额临时表
	private void CreateClearBanlanceTmpTable() throws YssException // add by yeshenghong story2525 20120519
	{
		YssCommonRepCtl repParam = null;
		Date dDate = null;
		String portCode = "";
		String strSql = "";
		String assetCode = "";
		ResultSet rs = null;
		ArrayList accNumList = new ArrayList();
		ArrayList dateList = new ArrayList();
		ArrayList balanList = new ArrayList();
		Connection qsConn = null;
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		PreparedStatement pst = null;
		if(alRepParam.size()==2)
		{
			repParam = (YssCommonRepCtl) alRepParam.get(0);
			dDate = YssFun.parseDate(repParam.getCtlValue(), "yyyy-MM-dd");
			repParam = (YssCommonRepCtl) alRepParam.get(1);
			portCode = repParam.getCtlValue();
		}
		
		try {
			strSql = " select FAssetCode from " + pub.yssGetTableName("tb_para_portfolio") + " where FPortCode = " + dbl.sqlString(portCode);
			rs = dbl.openResultSet(strSql);
			if(rs.next())
			{
				assetCode = rs.getString("FAssetCode");
			}
			dbl.closeResultSetFinal(rs);
			
			if (dDate != null) {
				strSql = " select FAccNum, to_date(fdate, 'yyyy-MM-dd') as FDate, FBalance from qsbalance where to_date(fdate, 'yyyy-MM-dd') between "
						+ dbl.sqlDate(YssFun.addDay(dDate, -1)) + " and " + dbl.sqlDate(dDate) + " and FFundCode = " + dbl.sqlString(assetCode);
				qsConn = dbl.loadQsConnection();
				rs = qsConn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY).executeQuery(
						strSql);
				while (rs.next()) {
					accNumList.add(rs.getInt("FAccNum"));
					dateList.add(rs.getDate("FDate"));
					balanList.add(rs.getDouble("FBalance"));
				}
				dbl.closeResultSetFinal(rs);
				if (!qsConn.isClosed()) {
					qsConn.close();
					qsConn = null;
				}
			}
			if(!dbl.yssTableExist("Tmp_QSBalance_" + pub.getUserCode()))//不存在才创建
			{
				//dbl.executeSql("drop table " + "TmpNH_QSBalance_" + pub.getUserCode());
				strSql = "create global temporary table Tmp_QSBalance_" + pub.getUserCode() + "(FACCNUM INTEGER,FDate DATE," +
						 " FBALANCE NUMBER(19,4)) on Commit Preserve Rows";
				dbl.executeSql(strSql);
			}
			conn.setAutoCommit(false);
	        bTrans = true;
	        strSql = " delete from Tmp_QSBalance_" + pub.getUserCode();//清空数据
	        dbl.executeSql(strSql);
			strSql = "insert into Tmp_QSBalance_" + pub.getUserCode() + " values(?,?,?)";
			pst = dbl.getPreparedStatement(strSql);
			for(int i=0;i<accNumList.size();i++)
			{
				pst.setInt(1,(Integer)accNumList.get(i));
				pst.setDate(2, (java.sql.Date)dateList.get(i));
				pst.setDouble(3, (Double)balanList.get(i));
				pst.addBatch();
			}
			pst.executeBatch();   //将符合条件的数据添加到临时表中
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally
		{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(bTrans);
		}
	}

	protected String buildSql(String sDs) throws YssException {
		YssCommonRepCtl repParam = null;
		FaceCfgParamBean param = null;
		String sInd = "";// 参数的标识
		//sInd2 = "";//无用注释
		String sDataType = ""; // 数据类型的标识 S:字符型,I:数字型,D:日期型
		int iPos = 0;
		String sSqlValue = "";
		//boolean bFlag = false;//无用注释
		String tableName = ""; // add by yanghaiming MS01171
		// QDV4上海2010年05月06日01_AB 数据源中的表名
		int year1 = 0;// add by yanghaiming MS01171 QDV4上海2010年05月06日01_AB
		int year2 = 0;// add by yanghaiming MS01171 QDV4上海2010年05月06日01_AB
		String[] tableYear = null;
		String sqlResult = "";
		String yearStr = "";
		for (int i = 0; i < alRepParam.size(); i++) {
			repParam = (YssCommonRepCtl) alRepParam.get(i);
			sInd = "<" + (repParam.getCtlIndex()) + ">";
			iPos = sDs.indexOf(sInd);
			if (iPos <= 0) {
				sInd = " < " + (repParam.getCtlIndex()) + " >";
				iPos = sDs.indexOf(sInd);
			}
			if (iPos > 1) {
				sDataType = sDs.substring(iPos - 1, iPos);
				if (repParam.getCtlValue() == null) {
					continue;
				}
				//---add by songjie 2011.04.21 BUG 1707 QDV4赢时胜上海2011年04月15日01_B---//
				//若以T打头的话，则返回不带单引号的数据，可用于套帐代码作为查询条件、拼接表名时，套帐代码的获取
				if (sDataType.equalsIgnoreCase("T")) {
					sSqlValue = repParam.getCtlValue();
				}
				//---add by songjie 2011.04.21 BUG 1707 QDV4赢时胜上海2011年04月15日01_B---//
				if (sDataType.equalsIgnoreCase("S")) {
					//delete by songjie 2011.03.24 需求：590 QDV4深圳赢时胜2011年2月10日03_A
//					sSqlValue = dbl.sqlString(repParam.getCtlValue());
					//add by songjie 2011.03.24 需求：590 QDV4深圳赢时胜2011年2月10日03_A
					sSqlValue = operSql.sqlCodes(repParam.getCtlValue());
				} else if (sDataType.equalsIgnoreCase("I")) {
					sSqlValue = repParam.getCtlValue();
				} else if (sDataType.equalsIgnoreCase("D")) {
					// 转换成日期 BugNo:0000253 2 edit by jc
					sSqlValue = dbl.sqlDate(YssFun.parseDate(repParam
							.getCtlValue(), "yyyy-MM-dd"));
					// --------add by wangzuochun 2009.10.22 MS00750
					// 现金头寸预测报表中，取数据时需要判断数据的节假日情况 -----//
					sDs = pretSqlIns(sDs, YssFun.parseDate(repParam
							.getCtlValue(), "yyyy-MM-dd"), sDataType + sInd);
					// ---------------------------- MS00750
					// -----------------------------//
				} else if (sDataType.equalsIgnoreCase("N")) {
					// 转换代码，例如 001,002转换成'001','002'
					sSqlValue = operSql.sqlCodes(repParam.getCtlValue());
				}
				/**shashijie 2012-4-20 STORY 2386 增加不一致,一致,条件,只正对工银核对报表*/
				 else if (sDataType.equalsIgnoreCase("a")) {
					sSqlValue = getSqlValue(repParam.getCtlValue());
				}
				/**end*/
				sDs = sDs.replaceAll(sDataType + sInd, sSqlValue);
			}
			// 取控件的相关参数
			param = this.getCtlParam(repParam.getCtlNameCode());
			if (param.getSCtlInd().trim().length() != 0
					&& param.getSCtlInd()
							.equalsIgnoreCase(YssCons.CTL_PORTTYPE)) {
				if (sDs.indexOf("<Set>") > 0) { // 把"<Set>"的标识替换成套帐号
					YssFinance cw = new YssFinance();
					cw.setYssPub(pub);
					sDs = sDs.replaceAll("<Set>", cw.getCWSetCode(repParam
							.getCtlValue()));
				} else if (sDs.indexOf("< Set >") > 0) { // 把"< Set >"的标识替换成套帐号
					YssFinance cw = new YssFinance();
					cw.setYssPub(pub);
					sDs = sDs.replaceAll("< Set >", cw.getCWSetCode(repParam
							.getCtlValue()));
				}
			} // 处理 财务中的组合套帐号问题 by leeyu 080729
			if (param.getSCtlInd().trim().length() != 0
					&& param.getSCtlInd()
							.equalsIgnoreCase(YssCons.CTL_DATETYPE)) {
				if (sDs.indexOf("<Year>") > 0) { // 把"<Year>"的标识替换成年份
					sDs = sDs.replaceAll("<Year>", YssFun.formatDate(repParam
							.getCtlValue(), "yyyy"));
				} else if (sDs.indexOf("< Year >") > 0) { // 把"< Year >"的标识替换成年份
					sDs = sDs.replaceAll("< Year >", YssFun.formatDate(repParam
							.getCtlValue(), "yyyy"));
				}
			} // 处理财务中的年份问题
		}
		// sDs = wipeSqlCond(sDs);
		if (sDs.indexOf("<U>") > 0) {
			sDs = sDs.replaceAll("<U>", pub.getUserCode());
		} else if (sDs.indexOf("< U >") > 0) {
			sDs = sDs.replaceAll("< U >", pub.getUserCode());
		}
		if (sDs.indexOf("<Year>") > 0) { // 把"<Year>"的标识替换成结束日期的年份,若控件组没有处理就取当前日期
			// by leeyu 080729
			sDs = sDs.replaceAll("<Year>", YssFun.formatDate(
					new java.util.Date(), "yyyy"));
		} else if (sDs.indexOf("< Year >") > 0) { // 把"< Year >"的标识替换成结束日期的年份
			sDs = sDs.replaceAll("< Year >", YssFun.formatDate(
					new java.util.Date(), "yyyy"));
		}
		/*
		 * if (sDs.indexOf("<Set>") > 0) { //把"<Year>"的标识替换成套帐号 YssFinance cw =
		 * new YssFinance(); cw.setYssPub(pub); sDs = sDs.replaceAll("<Set>",
		 * cw.getCWSetCode("001")); }
		 */
		// --------------------------------------------------//<Group>为了替换表中的组合群如"001"
		// sj edit 20080307
		if (sDs.indexOf("<Group>") > 0) {
			sDs = sDs.replaceAll("<Group>", pub.getAssetGroupCode());
			/**add---huhuichao 2013-5-28 BUG  7963 跨组合群查询监控结果提示缺失右括号*/
			sDs = sDs.replaceAll("< Group >", pub.getAssetGroupCode());
			/**end---huhuichao 2013-5-28 BUG  7963 跨组合群查询监控结果提示缺失右括号*/
		} else if (sDs.indexOf("< Group >") > 0) {
			sDs = sDs.replaceAll("< Group >", pub.getAssetGroupCode());
		}
		// --------------------------------------------------
		// --------------------------------------------------//<User>为了替换表中的用户
		// sj edit 20081107
		/**add---huhuichao 2013-9-6 STORY  3899 指定日期资产负债表和期间段利润表*/
		if (sDs.indexOf("<User>") > 0) {
			sDs = sDs.replaceAll("<User>", pub.getUserCode());
		} else if (sDs.indexOf("< User >") > 0) {
			sDs = sDs.replaceAll("< User >", pub.getUserCode());
		}
		/**end---huhuichao 2013-9-6 STORY  3899*/
		// --------------------------------------------------
		//add by licai 20110131 STORY #441 需优化现在的报表自定义模板
		if(bIsDynTabHead){//是动态表头才处理
			if(sDs.indexOf("<RptCode>")>0){//自定义报表代码
				sDs=sDs.replaceAll("<RptCode>", dbl.sqlString(this.repCode));
			}else if(sDs.indexOf("< RptCode >")>0){
				sDs=sDs.replaceAll("< RptCode >", dbl.sqlString(this.repCode));
			}
			if(sDs.indexOf("<RptStyle>")>0){//('报表格式代码','报表数据源')
				sDs=sDs.replaceAll("<RptStyle>", "("+operSql.sqlCodes(this.strRptStyle)+")");
			}else if(sDs.indexOf("< RptStyle >")>0){
				sDs=sDs.replaceAll("< RptStyle >", "("+operSql.sqlCodes(this.strRptStyle)+")");
			}
		}
		//add by licai 20110131 STORY #441 ====================end

		sDs = sDs.replaceAll("~Base", "base");
		sDs = pretExpress(sDs); // 最后来处理自定义函数部分 by leeyu 080601
		sDs = wipeSqlCond(sDs); // 将替换的放在后下面,原因是pretExpress(sDs)方法里有[]号, by
		// leeyu 080602
		// add by yanghaiming 20100528 MS01220 QDV4中金2010年05月05日01_A
		for (int j = 0; j < 20; j++) {// 这里暂时处理20张表以内的数据源
			sqlResult = "";
			if (sDs.indexOf("<TB>") > 0) {
				tableName = sDs.substring(sDs.indexOf("<TB>"));
				tableName = tableName.substring(0,
						tableName.indexOf("<TE>") + 4);
				if (tableName.indexOf("<D") > 0) {
					yearStr = tableName.substring(tableName.indexOf("<D"),
							tableName.indexOf("D>") + 2);
					tableYear = yearStr.substring(1, yearStr.length() - 1)
							.split(",");
					repParam = (YssCommonRepCtl) alRepParam.get(Integer
							.parseInt(tableYear[1]) - 1);
					year1 = Integer.parseInt(repParam.getCtlValue().substring(
							0, 4));
					repParam = (YssCommonRepCtl) alRepParam.get(Integer
							.parseInt(tableYear[2]) - 1);
					year2 = Integer.parseInt(repParam.getCtlValue().substring(
							0, 4));
					for (int i = year1; i <= year2; i++) {
						if (i == year1) {
							sqlResult += "( ";
						} else {
							sqlResult += " union all ";
						}
						sqlResult += " select a"
								+ String.valueOf(i)
								+ ".*,"
								+ i
								+ " as syear from "
								+ tableName
										.substring(4, tableName.length() - 4)
										.replaceAll(yearStr, String.valueOf(i))
								+ " a" + String.valueOf(i);
					}
					sqlResult += ")";
					sDs = sDs.replaceAll(tableName, sqlResult);
				}
			} else {
				break;
			}
		}
		
		/**shashijie 2012-4-20 STORY 2386 万一上面没有转换这里去除关键字 */
		sDs = sDs.replaceAll("a<RepStockCheckGY>", "");
		/**end*/
		
		return sDs;
	}

	/**shashijie 2012-4-20 STORY 2386 获取替换的值
	* @param ctlValue
	* @return 返回需要替换的值 */
	private String getSqlValue(String ctlValue) {
		String value = "";
		if (ctlValue.equals("0")) {
			value = " And (NVL(FKCSLCITI,0) <> NVL(FKCSLBB,0) Or NVL(FKCSLBB,0) <> NVL(FKCSLYSS,0) " +
					" Or NVL(FKCSLCITI,0) <> NVL(FKCSLYSS,0)) ";
		}else if (ctlValue.endsWith("1")) {
			value = " And (FKCSLCITI = FKCSLBB And FKCSLBB = FKCSLYSS And FKCSLCITI = FKCSLYSS) ";
		}else {
			value = "";
		}
		return value;
	}
	

	/**
	 * add by wangzuochun 2009.10.22 MS00750 现金头寸预测报表中，取数据时需要判断数据的节假日情况 处理函数
	 * WDay[参数1,参数2,参数3] 取工作日前一天 参数1:传入的日期 参数2: 节假日群 参数3:相差天数
	 * 
	 * @param sSql
	 *            String
	 * @return String (WDay){1}[\\[](.)*[;](.)*[;](.)*[\\]]
	 * @throws YssException
	 */
	private String pretSqlIns(String sSql, java.util.Date date, String params)
			throws YssException {
		String sFunCode = ""; // 函数名
		String strReplace = ""; // 要替代的字符串
		String strCalc = ""; // 通过计算得到的字符串
		String sParams = ""; // 相关参数字符串
		String[] arrParams = null; // 相关参数
		BaseOperDeal deal = new BaseOperDeal();
		try {
			deal.setYssPub(pub);
			if (sSql.indexOf("[") > 0 && sSql.indexOf("]") > 0) {
				if (sSql.indexOf("]") > sSql.indexOf("[")) { // 确保"]" 在"[" 的后面
					sParams = sSql.substring(sSql.indexOf("[") + 1, sSql
							.indexOf("]"));
					arrParams = sParams.split(";");
					sFunCode = sSql.substring(sSql.indexOf("[") - 4, sSql
							.indexOf("["));
					if (sFunCode.equalsIgnoreCase("WDay") ) {
						if((arrParams[0].toString().indexOf(params)) >=0){
							strReplace = "WDay" + "\\[" + sParams + "\\]";
							strCalc = dbl.sqlDate(deal.getWorkDay(
									(String) arrParams[1], date, YssFun
											.toInt(arrParams[2])));
						}

					}
					
				//add by luopc STORY #1434 
					if (sFunCode.equalsIgnoreCase("Wdab") ) {
						if((arrParams[0].toString().indexOf(params)) >=0){
							strReplace = "Wdab" + "\\[" + sParams + "\\]";
							strCalc = dbl.sqlDate(deal.getWorkday(
									(String) arrParams[1], date, YssFun
											.toInt(arrParams[2])));
						}

					}
				}
			}
			//modify by huangqirong 2011-07-28 story #1173
			//sSql = sSql.replaceAll(strReplace, strCalc);//如果存在多个WDAy函数就会产生所有的WDay 函数里的值都是解析成一样
			sSql = sSql.replaceFirst(strReplace, strCalc);//支持多个
			//---end---
			if (sFunCode.equalsIgnoreCase("WDay")) { // 此处只对WDay做处理，若增加了其他的函数，需增加判断。
				if (sSql.indexOf("WDay[" + params) > 0) { // 判断是否还有WDay函数
					sSql = pretSqlIns(sSql, date, params); // 若还有函数，则做递归处理。
				}
			}///add by luopc 
			if (sFunCode.equalsIgnoreCase("Wdab")) { // 此处只对WDay做处理，若增加了其他的函数，需增加判断。
				if (sSql.indexOf("Wdab[" + params) > 0) { // 判断是否还有WDay函数
					sSql = pretSqlIns(sSql, date, params); // 若还有函数，则做递归处理。
				}
			}
			return sSql;
		} catch (Exception e) {
			throw new YssException("解析SQL内部函数出错" + e.toString(), e);
		}
	}

	/**shashijie 2013-4-2 STORY 3368 将其变成共有的,可以在外部调用*/
	public String wipeSqlCond(String strSql) throws YssException {
		//无用注释
		/*int iBIndex = -1;
		int iEIndex = -1;
		int iLen = 0;*/
		int iBPos = 0;
		int iEPos = 0;
		String sCond = "";
		//boolean bFlag = false;
		String sTmp = "";
		char[] chrAry = strSql.toCharArray();
		while (strSql.indexOf("[", iBPos) > 0) {
			iBPos = strSql.indexOf("[", iBPos);
			iEPos = strSql.indexOf("]", iBPos);
			sTmp = strSql.substring(iBPos, iEPos);
			if (sTmp.indexOf("S<") > -1 || sTmp.indexOf("S <") > -1
					|| sTmp.indexOf("D<") > -1 || sTmp.indexOf("D <") > -1
					|| sTmp.indexOf("N<") > -1 || sTmp.indexOf("N <") > -1
					|| sTmp.indexOf("I<") > -1 || sTmp.indexOf("I <") > -1) {
				sCond = YssFun.getStrParams(sTmp);
			}
			if (sCond.length() > 0) {
				for (int i = iBPos; i <= iEPos; i++) {
					chrAry[i] = ' ';
				}
				sCond = "";
			} else {
				chrAry[iBPos] = ' ';
				chrAry[iEPos] = ' ';
			}
			strSql = String.valueOf(chrAry);

		}
		// for (int i = iBPos; i<iEPos; i++){
		// if ((chrAry[i] == '<' && chrAry[i + 2] == '>') ||
		// (chrAry[i] == '<' && chrAry[i + 3] == '>') ||
		// (chrAry[i] == '<' && chrAry[i + 4] == '>') ||
		// (chrAry[i] == '<' && chrAry[i + 5] == '>')){
		//
		// }
		// }

		// for (int i = 0; i < chrAry.length; i++) {
		// if (chrAry[i] == '[') {
		// iBIndex = i;
		// iLen++;
		// }
		// if (iBIndex > -1) {
		// iLen++;
		// }
		//
		// if (strSql.indexOf(" < ")>0){
		// if (iLen > 0 &&
		// ( (chrAry[i] == '<' && chrAry[i + 4] == '>') ||
		// (chrAry[i] == '<' && chrAry[i + 5] == '>'))) {
		// bFlag = true;
		// }
		// }else{
		// if (iLen > 0 &&
		// ( (chrAry[i] == '<' && chrAry[i + 2] == '>') ||
		// (chrAry[i] == '<' && chrAry[i + 3] == '>'))) {
		// bFlag = true;
		// }
		// }
		// if (chrAry[i] == ']') {
		// iLen++;
		// if (bFlag) {
		// for (int j = iBIndex; j < iBIndex + iLen - 2; j++) {
		// chrAry[j] = ' ';
		// }
		// }
		// else {
		// chrAry[iBIndex] = ' ';
		// chrAry[i] = ' ';
		// }
		// bFlag = false;
		// iBIndex = -1;
		// iLen = 0;
		// }
		// }
		return strSql;
	}

	protected HashMap getCellStyles(String sRelaCode) throws YssException {
		HashMap hmResult = new HashMap();
		ResultSet rs = null;
		String strSql = "";
		RepTabCellBean cell = null;
		String sKey = "";
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Cell")
					+ " where FRelaCode = " + dbl.sqlString(sRelaCode)
					+ " and " + " FRelaType = 'DSF'";
			//add by licai 20110128 STORY #441 需优化现在的报表自定义模板
			if(/*sRelaCode.toUpperCase().startsWith("DSDYN")*/bIsDynTabHead){//如果数据源代码以dynds开头,则是动态表头数据源
				strSql="select * from TMP_REP_CELL"+
				       " where FRelaCode = " + dbl.sqlString(sRelaCode)+
				       " and " + " FRelaType = 'DSF'";
			}
			//add by licai 20110128 STORY #441 ====================end
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				cell = new RepTabCellBean();
				sKey = rs.getString("FRelaCode") + "\t"
						+ rs.getString("FRelaType") + "\t"
						+ rs.getString("FRow") + "\t" + rs.getString("FCol");
				cell.setCol(rs.getString("FCol"));
				cell.setContent(rs.getString("FContent"));
				cell.setBLine(rs.getString("FBLine"));
				cell.setTLine(rs.getString("FTLine"));
				cell.setRLine(rs.getString("FRLine"));
				cell.setLLine(rs.getString("FLLine"));
				cell.setForeColor(rs.getString("FForeColor"));
				cell.setBackColor(rs.getString("FBackColor"));
				cell.setBColor(rs.getString("FBColor"));
				cell.setLColor(rs.getString("FLColor"));
				cell.setRColor(rs.getString("FRColor"));
				cell.setTColor(rs.getString("FTColor"));
				cell.setFontName(rs.getString("FFontName"));
				cell.setFontSize(rs.getString("FFontSize"));
				cell.setFontStyle(rs.getString("FFontStyle"));
				cell.setFormat(rs.getString("FFormat") == null ? "null" : rs
						.getString("FFormat")); // MS00026 by leeyu 2008-11-19
				cell.setDataType(rs.getString("FDataType"));
				cell.setIMerge(rs.getInt("FIsMergeCol"));
				hmResult.put(sKey, cell);
			}
			return hmResult;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**edit by licai 20110209 STORY #441 需优化现在的报表自定义模板 
	 * 在方法内取得动态数据源sql并打开结果集
	 * @param sRepDsCode
	 * @param sFillRangeAry
	 * @param lTRowColor
	 * @param lBRowColor
	 * @param storageTab
	 * @return
	 * @throws YssException
	 */
	protected String buildDsRowStr(String strClobValue,String sRepDsCode, String[] sFillRangeAry, long lTRowColor, long lBRowColor,
			String storageTab) throws YssException {
		//String sDsField = ""; // 获得数据//无用注释
		//double dNum = 0;//无用注释
		int dDecimal = 2; // 保存精确度
		ArrayList sShowFieldAry = new ArrayList();
		ArrayList sTotalFieldAry = new ArrayList();
		//double dTmpTotal = 0;
		HashMap hmRsField = new HashMap();
		HashMap hmTotal = new HashMap();
		HashMap hmCellStyle = null;
		HashMap hmExpDouble = new HashMap(); // 用于储存公式中计算出来的值
		ResultSetMetaData rsmd = null;
		StringBuffer buf = new StringBuffer();
		/*Object obj = null;//无用注释
		String beanId = ""; // 处理公式的beanid//无用注释
		String[] expAry = null;//无用注释
		String expParam = "";*///无用注释
		String strSql = "";
		ResultSet rs = null;//字段配置结果集
		//String sValueInd = "";//无用注释
		int nRow = -1, nCol = -1;
		int iCurRow = 1;
		//int iCurCol = 1;//无用注释
		//String sKey = "";//无用注释
		double dTmp = 0;
		RepTabCellBean rtcTmp = null;
		boolean flag = false;
		//add by licai 20110209 STORY #441 需优化现在的报表自定义模板
		ResultSet rsDs=null;//动态数据源结果集
		LinkedHashMap hmRsDs=new LinkedHashMap();//动态数据源Sql键值对 <组合代码,对应的SQL>
		TreeMap hmRepDataPerRow=new TreeMap();//每行对应的报表数据
		StringBuffer sbDynColumnCodes=new StringBuffer();//组合代码等动态列
		TreeMap hmDynColumnCodeRows=new TreeMap();//组合代码等动态列：对应的数据源记录数
		//add by licai 20110209 STORY #441======================end

		try {
			//add by licai 20110209 STORY #441 需优化现在的报表自定义模板	
			if(bIsDynTabHead){//动态表头
				strClobValue = buildAllRsDsSql(hmDynColumnCodeRows,hmRsDs,strClobValue,sbDynColumnCodes);
				updateDynHead();				
			}
		    strSql = buildSql(strClobValue);
			rsDs = dbl.openResultSet(strSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE);
			//add by licai 20110209 STORY #441 =====================end
			rsmd = rsDs.getMetaData();
			nRow = Integer.parseInt(sFillRangeAry[1]);
			nCol = Integer.parseInt(sFillRangeAry[3]);
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				hmRsField.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));//存放报表数据源 键值对 (字段名:字段类型名)
			}
			hmCellStyle = getCellStyles(sRepDsCode);//从tb_XXX_rep_cell表查询DSF数据构造单元格bean，存放键值对（主键：单元格bean）
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField")
					+ " where FRepDsCode = " + dbl.sqlString(sRepDsCode)
					+ " order by FOrderIndex";
			rs = dbl.openResultSet_antReadonly(strSql);
			// 可以在此处创建存储表，但必须是一次数据操作的第一次 否则将只能保留最后一次操作，like 参数数据源
			// by sunkey Bug:0000515
			if (rowCount == 0) {
				flag = this.buildStorageTab(storageTab, rsmd, sRepDsCode);
			}
			//edit by licai 20110209 STORY #441 需优化现在的报表自定义模板	
			if (bIsDynTabHead) {// 动态表头
				Iterator itRsDs = hmRsDs.keySet().iterator();
				while (itRsDs.hasNext()) {// 遍历所有的数据源结果集：开始
					String strDynColumnCode = (String) itRsDs.next();
					strSql = (String) hmRsDs.get(strDynColumnCode);
					rsDs = dbl.openResultSet(strSql,
							ResultSet.TYPE_SCROLL_INSENSITIVE);
					buildDynRepDataPerRow(strDynColumnCode, sbDynColumnCodes,
							hmRepDataPerRow, sRepDsCode, lTRowColor,
							lBRowColor, storageTab, dDecimal, sShowFieldAry,
							sTotalFieldAry, hmRsField, hmTotal, hmCellStyle,
							hmExpDouble, rs, nRow, nCol, iCurRow, dTmp, rtcTmp,
							flag, rsDs);
					//add by licai 20110308 动态表头报表字段配置合计功能实现
					appendDynRepSummaryData(sRepDsCode, sShowFieldAry, hmTotal,
							hmCellStyle, hmRepDataPerRow, rs,sbDynColumnCodes,strDynColumnCode);
					//add by licai 20110308 ===============end
					dbl.closeResultSetFinal(rsDs);// 关闭获取数据源结果集元数据的结果集
				}// 遍历所有的数据源结果集：结束
				// 取出每行报表的数据
				if (iCount >= 1) {
					Iterator it = hmRepDataPerRow.keySet().iterator();
					StringBuffer sb = new StringBuffer();
					String[] aryDynColumnCodes = sbDynColumnCodes.toString()
							.split("\t");
					while (it.hasNext()) {
						StringBuffer sbPerRow = (StringBuffer) hmRepDataPerRow
								.get((Integer) it.next());
						String strPerRow = sbPerRow.toString();
						for (int i = 0; i < aryDynColumnCodes.length; i++) {// 将组合代码替换成\t
							if (strPerRow.indexOf(aryDynColumnCodes[i]) >= 0) {
								strPerRow = strPerRow.replaceAll("\\["
										+ aryDynColumnCodes[i]
												.substring(1,
														aryDynColumnCodes[i]
																.length() - 1)
										+ "\\]", "");
							}
						}
						sb.append(strPerRow);
						// sb.setLength(sb.length()-"\t".length());
						sb.append("\r\n");
					}
					buf = sb;
				}
			} else {
				buildRepDataPerRow(sRepDsCode, lTRowColor, lBRowColor,
						storageTab, dDecimal, sShowFieldAry, sTotalFieldAry,
						hmRsField, hmTotal, hmCellStyle, hmExpDouble, buf, rs,
						nRow, nCol, iCurRow, dTmp, rtcTmp, flag, rsDs);// 遍历数据源结果集取得DSF数据和报表字段值
				//add by licai 20110308 非动态表头报表字段配置合计功能实现
				appendRepSummaryData(sRepDsCode, sShowFieldAry, hmTotal,
						hmCellStyle, buf, rs);
				//add by licai 20110308 非动态表头报表字段配置合计功能实现
			}
			//edit by licai 20110209 STORY #441 =====================end	
			return buf.toString();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			//关闭hmRsDs里的结果集
			dbl.closeResultSetFinal(rsDs);//add by licai 20110209 STORY #441 需优化现在的报表自定义模板
			dbl.closeResultSetFinal(rs);
			
		}
	}

	/**add by licai 20110308 
	 * 非动态表头报表字段配置合计功能实现
	 * @param sRepDsCode
	 * @param sShowFieldAry
	 * @param hmTotal
	 * @param hmCellStyle
	 * @param buf
	 * @param rs
	 * @throws SQLException
	 */
	private void appendRepSummaryData(String sRepDsCode,
			ArrayList sShowFieldAry, HashMap hmTotal, HashMap hmCellStyle,
			StringBuffer buf, ResultSet rs) throws SQLException {
		double dTmpTotal;
		String sKey;
		// 生成合计数据
		if (hmTotal.size() == 0 && buf.toString().length() > 0) {//如果合计hashMap没有合计字段保存
			rs.beforeFirst();//将字段配置结果集游标移至开始处
			while (rs.next()) {//遍历字段配置结果集
				sShowFieldAry.add(rs.getString("FDsField") + "|"//arrayList元素 (字段名|序号)
						+ rs.getString("FOrderIndex"));
				if (rs.getBoolean("FIsTotal")) {//如果为合计字段
					hmTotal.put(rs.getString("FDsField") + "|"//存放键值对 （字段名|序号:0  ）
							+ rs.getString("FOrderIndex"), new Double(0));
				}

				if (rs.getString("FTotalInd") != null//如果合计标识不为null
						&& rs.getString("FTotalInd").trim().length() > 0) {
					hmTotal.put(rs.getString("FDsField") + "|"//存放键值对 （字段名|序号:合计标识  ）
							+ rs.getString("FOrderIndex"), rs
							.getString("FTotalInd"));
				}
			}
		}

		if (hmTotal.size() > 0) {//如果有合计数据：开始
			for (int i = 0; i < sShowFieldAry.size(); i++) {
				// 数据源的单元格合计列FRow存-2
				sKey = sRepDsCode + "\tDSF\t-2\t" + i;
				if (hmCellStyle.containsKey(sKey)) {//如果包含
					buf.append(
							((RepTabCellBean) hmCellStyle.get(sKey))
									.buildRowStr()).append("\n");//合计单元格格式
				}
				if (hmTotal.containsKey((String) sShowFieldAry.get(i))) {//合计中包含
					if (hmTotal.get((String) sShowFieldAry.get(i)) instanceof Double) {
						dTmpTotal = ((Double) hmTotal
								.get((String) sShowFieldAry.get(i)))
								.doubleValue();
						buf.append(dTmpTotal);//合计值
					} else if (hmTotal.get((String) sShowFieldAry.get(i)) instanceof String) { // 字符型实际是合计的标识
						buf.append(hmTotal.get((String) sShowFieldAry
								.get(i)));//合计标识
					}
				} else {
					buf.append("");
				}
				if (i < sShowFieldAry.size() - 1) {
					buf.append("\t");
				}
			}
			buf.append("\r\n");
		}//如果有合计数据：结束
	}
	/**add by licai 20110308 
	 * 动态表头报表字段配置合计功能实现
	 * @param sRepDsCode
	 * @param sShowFieldAry
	 * @param hmTotal
	 * @param hmCellStyle
	 * @param buf
	 * @param rs
	 * @param sbDynColumnCodes
	 * @param strDynColumnCode
	 * @throws SQLException
	 */
	private void appendDynRepSummaryData(String sRepDsCode,
			ArrayList sShowFieldAry, HashMap hmTotal, HashMap hmCellStyle,
			TreeMap hmRepDataPerRow, ResultSet rs,StringBuffer sbDynColumnCodes,String strDynColumnCode) throws SQLException {
		double dTmpTotal;
		String sKey;
		// 生成合计数据
		if (hmTotal.size() == 0 && hmRepDataPerRow.size() > 0) {//如果合计hashMap没有合计字段保存
			rs.beforeFirst();//将字段配置结果集游标移至开始处
			while (rs.next()) {//遍历字段配置结果集
				sShowFieldAry.add(rs.getString("FDsField") + "|"//arrayList元素 (字段名|序号)
						+ rs.getString("FOrderIndex"));
				if (rs.getBoolean("FIsTotal")) {//如果为合计字段
					hmTotal.put(rs.getString("FDsField") + "|"//存放键值对 （字段名|序号:0  ）
							+ rs.getString("FOrderIndex"), new Double(0));
				}
				
				if (rs.getString("FTotalInd") != null//如果合计标识不为null
						&& rs.getString("FTotalInd").trim().length() > 0) {
					hmTotal.put(rs.getString("FDsField") + "|"//存放键值对 （字段名|序号:合计标识  ）
							+ rs.getString("FOrderIndex"), rs
							.getString("FTotalInd"));
				}
			}
			rs.beforeFirst();//将字段配置结果集游标移至开始处
		}
		StringBuffer buf=new StringBuffer();		
		if (hmTotal.size() > 0) {//如果有合计数据：开始
			for (int i = 0; i < sShowFieldAry.size(); i++) {
				// 数据源的单元格合计列FRow存-2
				sKey = sRepDsCode + "\tDSF\t-2\t" + i;
				if (hmCellStyle.containsKey(sKey)) {//如果包含
					buf.append(
							((RepTabCellBean) hmCellStyle.get(sKey))
							.buildRowStr()).append("\n");//合计单元格格式
				}
				if (hmTotal.containsKey((String) sShowFieldAry.get(i))) {//合计中包含
					if (hmTotal.get((String) sShowFieldAry.get(i)) instanceof Double) {
						dTmpTotal = ((Double) hmTotal
								.get((String) sShowFieldAry.get(i)))
								.doubleValue();
						buf.append(dTmpTotal);//合计值
					} else if (hmTotal.get((String) sShowFieldAry.get(i)) instanceof String) { // 字符型实际是合计的标识
						buf.append(hmTotal.get((String) sShowFieldAry
								.get(i)));//合计标识
					}
				} else {
					buf.append("");
				}
				if (i < sShowFieldAry.size() - 1) {
					buf.append("\t");
				}
			}
//			buf.append("\r\n");
			hmRepDataPerRow.put(new Integer(hmRepDataPerRow.size()),
					new StringBuffer(sbDynColumnCodes.toString().replaceAll(
							"\\[" + strDynColumnCode + "\\]", buf.toString())));
		}//如果有合计数据：结束
	}

	/**add by licai STORY #441 需优化现在的报表自定义模板
	 * 
	 * @param hmDynColumnCodeRows
	 * @param strClobValue
	 * @return 
	 * @throws YssException
	 */
	private String buildAllRsDsSql(TreeMap hmDynColumnCodeRows,LinkedHashMap hmRsDs,String strClobValue,StringBuffer sbDynColumnCodes)
			throws YssException {
		String strSql="";
		Iterator it=null;
		//ResultSet rs=null;//无用注释
		try {
			if (bIsDynTabHead) {// 动态表头：开始
				it=hmDynRowCodesPerDynColumnCode.keySet().iterator();
				while (it.hasNext()) {
					String strCv = strClobValue;
					String sDynColumnCodeName = (String) it.next();// 行号|[组合代码]|组合名称
					String sDynColumnCodeInBracket = sDynColumnCodeName
							.split("[|]")[1];// [组合代码]
					String sDynColumnCode = sDynColumnCodeInBracket.substring(
							1, sDynColumnCodeInBracket.length() - 1);// 组合代码
					String sDynRowCodes = (String) hmDynRowCodesPerDynColumnCode
							.get(sDynColumnCodeName);// 券商代码
					if (strCv.indexOf("<DynColumnCode>") > 0) {// 组合代码等动态列条件
						strCv = strCv.replaceAll("<DynColumnCode>", dbl
								.sqlString(sDynColumnCode));// 
					} else if (strCv.indexOf("< DynColumnCode >") > 0) {
						strCv = strCv.replaceAll("< DynColumnCode >", dbl
								.sqlString(sDynColumnCode));
					}
					if (strCv.indexOf("<DynRowCodes>") > 0) {// 多券商代码等动态行条件
						strCv = strCv.replaceAll("<DynRowCodes>", "("
								+ operSql.sqlCodes(sDynRowCodes) + ")");
					} else if (strClobValue.indexOf("< DynRowCodes >") > 0) {
						strCv = strCv.replaceAll("< DynRowCodes >", "("
								+ operSql.sqlCodes(sDynRowCodes) + ")");
					}
					strSql = buildSql(strCv);
					int iRsRows = getRsRows(strSql); // 取得记录数
					hmDynColumnCodeRows.put(sDynColumnCode, String
							.valueOf(iRsRows));// <组合代码,记录数>
					hmRsDs.put(sDynColumnCode, strSql);// 按存入顺序存放<组合代码,对应的sql>
				}
				//实现hmCodeRows按value值降序排列
				ArrayList arrDynColumnCodesRows = new ArrayList(
						hmDynColumnCodeRows.entrySet());
				Collections.sort(arrDynColumnCodesRows, new Comparator() {
					public int compare(Object mapping1, Object mapping2) {
						return (Integer.valueOf((String) ((Map.Entry) mapping2)
								.getValue())).compareTo(Integer
								.valueOf(((String) ((Map.Entry) mapping1)
										.getValue())));
					}
				});		
				//edit by licai 20110222 STORY #441 需优化现在的报表自定义模板				
				LinkedHashMap hmDynColumnCodeSql = new LinkedHashMap();// 按存入顺序存放<组合代码,对应的结果集sql>
				LinkedHashMap hmNewDynRowCodesPerDynColumnCode = new LinkedHashMap();// 按存入顺序存放<组合代码,对应的'券商代码,券商代码'>
				int iDynColumnNameIndex = 0;
				for (int i = 0; i < arrDynColumnCodesRows.size(); i++) {// 按记录数递减顺序重新构造hmRsDs
					Map.Entry mapping = (Entry) arrDynColumnCodesRows.get(i);// <key,value>
					String strDynColumnCode = (String) mapping.getKey();// 组合代码
					sbDynColumnCodes
							.append("[" + strDynColumnCode + "]")
							.append(
									(i == arrDynColumnCodesRows.size() - 1) ? ""
											: "\t");// 按数据源结果集记录数递减的顺序存放组合代码
					hmDynColumnCodeSql.put(strDynColumnCode, hmRsDs
							.get(strDynColumnCode));// 按数据源结果集记录数递减的顺序存放对应的sql
					Iterator itBCPP = hmDynRowCodesPerDynColumnCode.keySet()
							.iterator();
					while (itBCPP.hasNext()) {
						String strDynColumnCodeName = (String) itBCPP.next();// 组合名
						if (strDynColumnCodeName.indexOf("[" + strDynColumnCode
								+ "]") > 0) {// 找到对应的组合
							String[] aryDynColumnCodeName = strDynColumnCodeName
									.split("[|]");
							StringBuffer sbReplace = new StringBuffer();
							String strReplace = sbReplace.append(// 获得重新排序后的'行号|组合代码|组合名'串
									iDynColumnNameIndex++).append("|").append(
									aryDynColumnCodeName[1]).append("|")
									.append(aryDynColumnCodeName[2]).toString();
							hmNewDynRowCodesPerDynColumnCode.put(strReplace,
									hmDynRowCodesPerDynColumnCode
											.get(strDynColumnCodeName));// 按记录数递减的顺序存放
						}
					}
				}
				//edit by licai 20110222 STORY #441 =====================end
				hmRsDs=hmDynColumnCodeSql;
				hmDynRowCodesPerDynColumnCode=hmNewDynRowCodesPerDynColumnCode;

			}// 动态表头：结束
		} catch (Exception e) {
			throw new YssException(e);
		}
		return strSql;
	}

	/**add by licai add by licai 20110211 STORY #441 需优化现在的报表自定义模板
	 * 获取结果集记录数
	 * @param strSql
	 * @throws SQLException
	 * @throws YssException
	 */
	private int getRsRows(String strSql) throws YssException {
		ResultSet rs=null;
		int iRows=0;
		try {
			rs = dbl.openResultSet(strSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE);
		while(rs.next()){
			rs.last();
			iRows=rs.getRow();
		}
		} catch (Exception e) {
			throw new YssException(e);
		}finally{			
			dbl.closeResultSetFinal(rs);
		}
		return iRows;
	}


	 /**add by licai 20110211 STORY #441 需优化现在的报表自定义模板
	 *逐行建立动态表头报表数据
	 * @param sbDynColumnCodes
	 * @param strDynColumnCode
	 * @param sRepDsCode
	 * @param lTRowColor
	 * @param lBRowColor
	 * @param storageTab
	 * @param dDecimal
	 * @param sShowFieldAry
	 * @param sTotalFieldAry
	 * @param hmRsField
	 * @param hmTotal
	 * @param hmCellStyle
	 * @param hmExpDouble
	 * @param sbRsPerRow
	 * @param rs
	 * @param nRow
	 * @param nCol
	 * @param iCurRow
	 * @param dTmp
	 * @param rtcTmp
	 * @param flag
	 * @param rsDs
	 * @throws SQLException
	 * @throws YssException
	 */
	private void buildDynRepDataPerRow(String strDynColumnCode,StringBuffer sbDynColumnCodes,TreeMap hmRepDataPerRow,String sRepDsCode, long lTRowColor,
			long lBRowColor, String storageTab, int dDecimal,
			ArrayList sShowFieldAry, ArrayList sTotalFieldAry,
			HashMap hmRsField, HashMap hmTotal, HashMap hmCellStyle,
			HashMap hmExpDouble, ResultSet rs, int nRow,
			int nCol, int iCurRow, double dTmp, RepTabCellBean rtcTmp,
			boolean flag, ResultSet rsDs)
			throws SQLException, YssException {
		String sDsField;
		double dNum;
		double dTmpTotal;
		Object obj;
		String beanId;
		String[] expAry;
		String expParam;
		String sValueInd;
		int iCurCol;
		String sKey;
		colorFlag=false;//add by huangqirong 2011-10-27 story#1387	 控制行颜色
		int iRowCount=0;//数据源结果集记录行数add by licai 20110211 STORY #441 需优化现在的报表自定义模板
		hmTotal=new HashMap();//add by licai 20110308 动态报表字段配置合计功能实现
		while (rsDs.next()) {//遍历数据源结果集：开始  
			//add by licai 20110211 STORY #441 需优化现在的报表自定义模板
			if(!hmRepDataPerRow.containsKey(new Integer(iRowCount))){//每行报表数据用组合代码占据位置
				hmRepDataPerRow.put(new Integer(iRowCount), sbDynColumnCodes);
			}
			StringBuffer sbRsPerRow=new StringBuffer();
			//add by licai 20110211 STORY #441 ====================end
			boolean isFormula = false; // 标识是否为公式字段的变量 sunkey Bug:0000515
			if ((nRow > -1 ? iCurRow <= nRow : true)) {//如果在填充范围内：开始
				iCurCol = 1;
				colorFlag = !colorFlag;
				while (rs.next()) {//遍历rs(字段配置)：开始
					if ((nCol > -1 ? iCurCol <= nCol : true)) {//逐列处理：开始
						// 加入格式前缀。
						// 数据源的单元格FRow存-1
						sKey = sRepDsCode + "\tDSF\t-1\t"
								+ rs.getString("FOrderIndex");
						if (hmCellStyle.containsKey(sKey)) {//如果单元格格式中包含字段配置：开始
							rtcTmp = (RepTabCellBean) hmCellStyle.get(sKey);
							if (colorFlag) {
								rtcTmp.setBackColor(String.valueOf(Math
										.abs(lBRowColor))); // 设置下行色`
							} else {
								rtcTmp.setBackColor(String.valueOf(Math
										.abs(lTRowColor))); // 设置上行色
							}
							if (rtcTmp.getDataType().equals("1")//如果为数值类型，取得精度值
									&& !rtcTmp.getFormat().equals("null")) {
								if (rtcTmp.getFormat().indexOf("\t") <= -1) {
									if (YssFun.isNumeric(rtcTmp.getFormat()
											.split(",")[2])) {
										dDecimal = Integer.parseInt(rtcTmp
												.getFormat().split(",")[2]);
									}
								} else {
									if (YssFun.isNumeric(rtcTmp.getFormat()
											.split("\t")[2])) {
										dDecimal = Integer
												.parseInt(rtcTmp
														.getFormat().split(
																"\t")[2]);
									}
								}

							}
							sbRsPerRow.append(rtcTmp.buildRowStr()).append("\n");//单元格格式
						}//如果单元格格式中包含字段配置：结束
						if (!rs.getString("FDsField").substring(0, 1)
								.equalsIgnoreCase("#")) {//如果为非公式字段：开始
							if (hmRsField.containsKey(rs.getString(
									"FDsField").toUpperCase())) {//如果数据源字段中包含字段配置的字段：开始
								if (((String) hmRsField.get(rs.getString(
										"FDsField").toUpperCase()))
										.equalsIgnoreCase("DATE")) {//如果为日期类型则进行格式处理
									if (rsDs.getString(rs
											.getString("FDsField")) != null) {
										// 2008.03.07 蒋锦 修改
										// 2008.03.20
										// 把rsDs.getString(rs.getString("FDsField"))改为rsDs.getDate(rs.getString("FDsField"))
										// 欧阳华修改
										sDsField = YssFun
												.formatDate(
														rsDs
																.getDate(rs
																		.getString("FDsField")),
														"yyyy-MM-dd");//取得日期值
									} else {
										sDsField = "";
									}
								} else {//其他类型则直接取值
									if (rsDs.getString(rs
											.getString("FDsField")) != null) {
										sDsField = rsDs.getString(
												rs.getString("FDsField"))
												.toString();//取得报表数据，动态表头如何处理？
									} else {
										sDsField = "";
									}
									// 来格式化显示的数据
									if (rtcTmp != null) {
										if (rtcTmp.getDataType()
												.equals("1")) {//数值类型
											if (YssFun.isNumeric(sDsField)) {
												dNum = Double
														.parseDouble(sDsField);
												sDsField = YssD.round(dNum,
														dDecimal)
														+ "";
											} else {
												sDsField = "0";
											}
										}
										// else{
										// sDsField = "";
										// }
									}
								}
								if (rs.getString("FColKey") != null
										&& !rs.getString("FColKey")
												.equalsIgnoreCase("null")) {
									sbRsPerRow
											.append(sDsField)
											.append("@")
											.append(rs.getString("FColKey"))
											.append("\t");
								} else {
									sbRsPerRow.append(sDsField).append("\t");
								}

								if (rsDs.isFirst()) {//如果报表数据源结果集游标回到首行，将配置字段（ 字段|序号）存进 arrayList(sShowFieldAry)
									sShowFieldAry.add(rs
											.getString("FDsField")
											+ "|"
											+ rs.getString("FOrderIndex"));
								}
								// 计算合计字段值
								if (rs.getBoolean("FIsTotal")) {//如果字段配置的字段是合计字段：开始
									if (!(rs.getString("FDsField") + "")
											.equalsIgnoreCase("null")) {//字段不为"null"
										dTmp = YssFun
												.isNumeric((rsDs
														.getString(rs
																.getString("FDsField")) + "")
														.trim()) ? YssD
												.round(
														rsDs
																.getDouble(rs
																		.getString("FDsField")),
														dDecimal)
												: 0;
									} else {//字段为"null"设定值为0
										dTmp = 0;
									}

									if (hmTotal.containsKey(rs
											.getString("FDsField")
											+ "|"
											+ rs.getString("FOrderIndex"))) {//如果合计键值对里包含键(字段配置|序号)，则进行累加并保存
										if (hmTotal
												.get(rs
														.getString("FDsField")
														+ "|"
														+ rs
																.getString("FOrderIndex")) instanceof Double) {
											dTmpTotal = ((Double) hmTotal
													.get(rs
															.getString("FDsField")
															+ "|"
															+ rs
																	.getString("FOrderIndex")))
													.doubleValue();
											dTmpTotal += dTmp;
											hmTotal
													.put(
															rs
																	.getString("FDsField")
																	+ "|"
																	+ rs
																			.getString("FOrderIndex"),
															new Double(
																	dTmpTotal));//重新保存合计值
										}
									} else {//如果合计键值对里不包含键(字段配置|序号)，则将值作为合计值保存
										hmTotal
												.put(
														rs
																.getString("FDsField")
																+ "|"
																+ rs
																		.getString("FOrderIndex"),
														new Double(dTmp));
									}
									sTotalFieldAry.add(rs
											.getString("FDsField"));//将合计字段保存进arrayList(sTotalFieldAry)
								}//如果字段配置的字段是合计字段：结束
							}//如果数据源字段中包含字段配置的字段：结束
						} //如果为非公式字段：结束
						else { //公式字段处理：开始
							isFormula = true; // 标识为公式字段
							sValueInd = rs.getString("FDsField").substring(
									1, 2);
							beanId = rs.getString("FDsField").substring(2,
									rs.getString("FDsField").indexOf("("));//取得公式的beanId
							expAry = rs.getString("FDsField")
									.substring(
											rs.getString("FDsField")
													.indexOf("(") + 1,
											rs.getString("FDsField")
													.indexOf(")")).split(
											",");
							expParam = "";
							for (int j = 0; j < expAry.length; j++) {
								if (YssFun.left(expAry[j], 1)
										.equalsIgnoreCase(":")) { // ":"表示是hmExpDouble中的key
									sKey = YssFun.right(expAry[j],
											expAry[j].length() - 1);
									if (hmExpDouble.containsKey(sKey)) {
										expParam += String
												.valueOf(((Double) hmExpDouble
														.get(sKey))
														.doubleValue())
												+ "\t";
									}
								} else {
									expParam += rsDs.getString(expAry[j])
											+ "\t";
								}
							}
							obj = pub.getOperDealCtx().getBean(beanId);
							((IOperValue) obj).setYssPub(pub);
							((IYssConvert) obj).parseRowStr(expParam);
							// 获取返回类型为double数据
							if (sValueInd.equalsIgnoreCase("D")) {//处理公式字段beanId计算出的结果:开始
								dTmp = ((IOperValue) obj)
										.getOperDoubleValue();
								if (rs.getString("FDsField").indexOf("::") > 0) { // ":"后面的值表示给公式的值设置的key
									sKey = rs.getString("FDsField").split(
											"::")[1];//公式计算的字段名为key
									hmExpDouble.put(sKey, new Double(dTmp));
								}
								if (rs.getString("FColKey") != null
										&& !rs.getString("FColKey")
												.equalsIgnoreCase("null")) {
									sbRsPerRow.append(dTmp).append("@").append(
											rs.getString("FColKey"))
											.append("\t");
								} else {
									sbRsPerRow.append(dTmp).append("\t");
								}
								// 计算合计字段值
								if (rs.getBoolean("FIsTotal")) {//如果字段配置的字段是合计字段：开始
									if (hmTotal.containsKey(rs
											.getString("FDsField")
											+ "|"
											+ rs.getString("FOrderIndex"))) {
										if (hmTotal
												.get(rs
														.getString("FDsField")
														+ "|"
														+ rs
																.getString("FOrderIndex")) instanceof Double) {
											dTmpTotal = ((Double) hmTotal
													.get(rs
															.getString("FDsField")
															+ "|"
															+ rs
																	.getString("FOrderIndex")))
													.doubleValue();
											dTmpTotal += dTmp;
											hmTotal
													.put(
															rs
																	.getString("FDsField")
																	+ "|"
																	+ rs
																			.getString("FOrderIndex"),
															new Double(
																	dTmpTotal));
										}
									} else {
										hmTotal
												.put(
														rs
																.getString("FDsField")
																+ "|"
																+ rs
																		.getString("FOrderIndex"),
														new Double(dTmp));
									}
									sTotalFieldAry.add(rs
											.getString("FDsField"));
								}//如果字段配置的字段是合计字段：结束
							}//处理公式字段beanId计算出的结果:结束
							if (rsDs.isFirst()) {//数据源结果集开始行
								sShowFieldAry
										.add(rs.getString("FDsField")
												+ "|"
												+ rs
														.getString("FOrderIndex"));
							}
						}//公式字段处理：结束

						if (rs.getString("FTotalInd") != null
								&& rs.getString("FTotalInd").trim()
										.length() > 0) {//如果设置了字段配置的字段标识
							hmTotal.put(rs.getString("FDsField") + "|"
									+ rs.getString("FOrderIndex"), rs
									.getString("FTotalInd"));//将键值对保存进合计HashMap（字段名|序号：字段标记）
						}
					}//逐列处理：结束
					iCurCol++;//下一列
				}//遍历rs(字段配置)：结束
			}//如果在填充范围内：结束
			if (sbRsPerRow.length() > 1) {
				sbRsPerRow.setLength(sbRsPerRow.length() - 1);
			}
//			sbRsPerRow.append("\r\n");
			//add by licai 20110211 STORY #441 需优化现在的报表自定义模板
			if (iCount >= 1) {
				StringBuffer sb = (StringBuffer) hmRepDataPerRow
						.get(new Integer(iRowCount));
				String strNewRowData = sb.toString();
				// 用该组合对应的该行的数据源结果集的数据替换占位的（组合代码）
				String strRegular = "\\[" + strDynColumnCode + "\\]";
				strNewRowData = strNewRowData.replaceAll(strRegular, sbRsPerRow
						.toString());//
				StringBuffer sbNewRowData = new StringBuffer(strNewRowData);
				sb = sbNewRowData;// 某组合对应结果集该行的报表数据
				hmRepDataPerRow.put(new Integer(iRowCount), sb);
				iRowCount++;
			}
			//add by licai 20110211 STORY #441 ====================end
			iCurRow++;//一行数据处理完，换一行
			// 将数据插入到存储表 sunkey Bug:0000515
			if (flag || rowCount > 0) {
				this.setStorageTabValue(storageTab, rsDs, dTmp, isFormula);
			}
			rs.beforeFirst();//遍历数据源结果集下一行之前，将字段配置结果集游标移至开始处，这样可以重新开始遍历字段配置结果集
		}//遍历数据源结果集：结束		
	}
	/**add by licai 20110211 STORY #441 需优化现在的报表自定义模板
	 *逐行获取非动态表头报表数据
	 * @param sRepDsCode
	 * @param lTRowColor
	 * @param lBRowColor
	 * @param storageTab
	 * @param dDecimal
	 * @param sShowFieldAry
	 * @param sTotalFieldAry
	 * @param hmRsField
	 * @param hmTotal
	 * @param hmCellStyle
	 * @param hmExpDouble
	 * @param buf
	 * @param rs
	 * @param nRow
	 * @param nCol
	 * @param iCurRow
	 * @param dTmp
	 * @param rtcTmp
	 * @param flag
	 * @param rsDs
	 * @throws SQLException
	 * @throws YssException
	 */
	private void buildRepDataPerRow(String sRepDsCode, long lTRowColor,
			long lBRowColor, String storageTab, int dDecimal,
			ArrayList sShowFieldAry, ArrayList sTotalFieldAry,
			HashMap hmRsField, HashMap hmTotal, HashMap hmCellStyle,
			HashMap hmExpDouble, StringBuffer buf, ResultSet rs, int nRow,
			int nCol, int iCurRow, double dTmp, RepTabCellBean rtcTmp,
			boolean flag, ResultSet rsDs)
	throws SQLException, YssException {
		String sDsField;
		double dNum;
		double dTmpTotal;
		Object obj;
		String beanId;
		String[] expAry;
		String expParam;
		String sValueInd;
		int iCurCol;
		String sKey;		
		int rows=0;	//固定行数	add by huangqirong 2011-10-22 story #1747
		
//		hmTotal=new HashMap();//add by licai 20110308 动态报表字段配置合计功能实现
		while (rsDs.next()) {//遍历数据源结果集：开始  
			
			rows++;	//add by huangqirong 2011-10-22 story #1747
			
			boolean isFormula = false; // 标识是否为公式字段的变量 sunkey Bug:0000515
			if ((nRow > -1 ? iCurRow <= nRow : true)) {//如果在填充范围内：开始
				iCurCol = 1;
				colorFlag = !colorFlag;
				while (rs.next()) {//遍历rs(字段配置)：开始
					if ((nCol > -1 ? iCurCol <= nCol : true)) {//逐列处理：开始
						// 加入格式前缀。
						// 数据源的单元格FRow存-1
						sKey = sRepDsCode + "\tDSF\t-1\t"
						+ rs.getString("FOrderIndex");
						if (hmCellStyle.containsKey(sKey)) {//如果单元格格式中包含字段配置：开始
							rtcTmp = (RepTabCellBean) hmCellStyle.get(sKey);
							if (colorFlag) {
								rtcTmp.setBackColor(String.valueOf(Math
										.abs(lBRowColor))); // 设置下行色`
							} else {
								rtcTmp.setBackColor(String.valueOf(Math
										.abs(lTRowColor))); // 设置上行色
							}
							if (rtcTmp.getDataType().equals("1")//如果为数值类型，取得精度值
									&& !rtcTmp.getFormat().equals("null")) {
								if (rtcTmp.getFormat().indexOf("\t") <= -1) {
									if (YssFun.isNumeric(rtcTmp.getFormat()
											.split(",")[2])) {
										dDecimal = Integer.parseInt(rtcTmp
												.getFormat().split(",")[2]);
									}
								} else {
									if (YssFun.isNumeric(rtcTmp.getFormat()
											.split("\t")[2])) {
										dDecimal = Integer
										.parseInt(rtcTmp
												.getFormat().split(
														"\t")[2]);
									}
								}
								
							}
							buf.append(rtcTmp.buildRowStr()).append("\n");//单元格格式
						}//如果单元格格式中包含字段配置：结束
						if (!rs.getString("FDsField").substring(0, 1)
								.equalsIgnoreCase("#")) {//如果为非公式字段：开始
							if (hmRsField.containsKey(rs.getString(
							"FDsField").toUpperCase())) {//如果数据源字段中包含字段配置的字段：开始
								if (((String) hmRsField.get(rs.getString(
								"FDsField").toUpperCase()))
								.equalsIgnoreCase("DATE")) {//如果为日期类型则进行格式处理
									if (rsDs.getString(rs
											.getString("FDsField")) != null) {
										// 2008.03.07 蒋锦 修改
										// 2008.03.20
										// 把rsDs.getString(rs.getString("FDsField"))改为rsDs.getDate(rs.getString("FDsField"))
										// 欧阳华修改
										sDsField = YssFun
										.formatDate(
												rsDs
												.getDate(rs
														.getString("FDsField")),
										"yyyy-MM-dd");//取得日期值
									} else {
										sDsField = "";
									}
								} else {//其他类型则直接取值
									if (rsDs.getString(rs
											.getString("FDsField")) != null) {
										String valueStr = rs.getString("FDsField");  //modified by yeshenghong #1817
										sDsField = rsDs.getString(valueStr).toString();
										if(valueStr.equals("FKMMC"))
										{
											if(sDsField.startsWith("<!CDATA[")&&sDsField.endsWith("]]>"))
											{
											String tempStr = sDsField.substring(8,sDsField.length());
											sDsField = tempStr.substring(0,tempStr.length()-3);
											}
										}
										
//										sDsField = rsDs.getString(
//												rs.getString("FDsField"))
//												.toString();//取得报表数据，动态表头如何处理？
									} else {
										sDsField = "";
									}
									// 来格式化显示的数据
									if (rtcTmp != null) {
										if (rtcTmp.getDataType()
												.equals("1")) {//数值类型
											if (YssFun.isNumeric(sDsField)) {
												dNum = Double
												.parseDouble(sDsField);
												sDsField = YssD.round(dNum,
														dDecimal)
														+ "";
											} else {
												sDsField = "0";
											}
										}
										// else{
										// sDsField = "";
										// }
									}
								}
								if (rs.getString("FColKey") != null
										&& !rs.getString("FColKey")
										.equalsIgnoreCase("null")) {
									buf
									.append(sDsField)
									.append("@")
									.append(rs.getString("FColKey"))
									.append("\t");
								} else {
									buf.append(sDsField).append("\t");
								}
								
								if (rsDs.isFirst()) {//如果报表数据源结果集游标回到首行，将配置字段（ 字段|序号）存进 arrayList(sShowFieldAry)
									sShowFieldAry.add(rs
											.getString("FDsField")
											+ "|"
											+ rs.getString("FOrderIndex"));
								}
								// 计算合计字段值
								if (rs.getBoolean("FIsTotal")) {//如果字段配置的字段是合计字段：开始
									if (!(rs.getString("FDsField") + "")
											.equalsIgnoreCase("null")) {//字段不为"null"
										dTmp = YssFun
										.isNumeric((rsDs
												.getString(rs
														.getString("FDsField")) + "")
														.trim()) ? YssD
																.round(
																		rsDs
																		.getDouble(rs
																				.getString("FDsField")),
																				dDecimal)
																				: 0;
									} else {//字段为"null"设定值为0
										dTmp = 0;
									}
									
									if (hmTotal.containsKey(rs
											.getString("FDsField")
											+ "|"
											+ rs.getString("FOrderIndex"))) {//如果合计键值对里包含键(字段配置|序号)，则进行累加并保存
										if (hmTotal
												.get(rs
														.getString("FDsField")
														+ "|"
														+ rs
														.getString("FOrderIndex")) instanceof Double) {
											dTmpTotal = ((Double) hmTotal
													.get(rs
															.getString("FDsField")
															+ "|"
															+ rs
															.getString("FOrderIndex")))
															.doubleValue();
											dTmpTotal += dTmp;
											hmTotal
											.put(
													rs
													.getString("FDsField")
													+ "|"
													+ rs
													.getString("FOrderIndex"),
													new Double(
															dTmpTotal));//重新保存合计值
										}
									} else {//如果合计键值对里不包含键(字段配置|序号)，则将值作为合计值保存
										hmTotal
										.put(
												rs
												.getString("FDsField")
												+ "|"
												+ rs
												.getString("FOrderIndex"),
												new Double(dTmp));
									}
									sTotalFieldAry.add(rs
											.getString("FDsField"));//将合计字段保存进arrayList(sTotalFieldAry)
								}//如果字段配置的字段是合计字段：结束
							}//如果数据源字段中包含字段配置的字段：结束
						} //如果为非公式字段：结束
						else { //公式字段处理：开始
							isFormula = true; // 标识为公式字段
							sValueInd = rs.getString("FDsField").substring(
									1, 2);
							beanId = rs.getString("FDsField").substring(2,
									rs.getString("FDsField").indexOf("("));//取得公式的beanId
							expAry = rs.getString("FDsField")
							.substring(
									rs.getString("FDsField")
									.indexOf("(") + 1,
									rs.getString("FDsField")
									.indexOf(")")).split(
									",");
							expParam = "";
							for (int j = 0; j < expAry.length; j++) {
								if (YssFun.left(expAry[j], 1)
										.equalsIgnoreCase(":")) { // ":"表示是hmExpDouble中的key
									sKey = YssFun.right(expAry[j],
											expAry[j].length() - 1);
									if (hmExpDouble.containsKey(sKey)) {
										expParam += String
										.valueOf(((Double) hmExpDouble
												.get(sKey))
												.doubleValue())
												+ "\t";
									}
								} else {
									expParam += rsDs.getString(expAry[j])
									+ "\t";
								}
							}
							obj = pub.getOperDealCtx().getBean(beanId);
							((IOperValue) obj).setYssPub(pub);
							((IYssConvert) obj).parseRowStr(expParam);
							// 获取返回类型为double数据
							if (sValueInd.equalsIgnoreCase("D")) {//处理公式字段beanId计算出的结果:开始
								dTmp = ((IOperValue) obj)
								.getOperDoubleValue();
								if (rs.getString("FDsField").indexOf("::") > 0) { // ":"后面的值表示给公式的值设置的key
									sKey = rs.getString("FDsField").split(
									"::")[1];//公式计算的字段名为key
									hmExpDouble.put(sKey, new Double(dTmp));
								}
								if (rs.getString("FColKey") != null
										&& !rs.getString("FColKey")
										.equalsIgnoreCase("null")) {
									buf.append(dTmp).append("@").append(
											rs.getString("FColKey"))
											.append("\t");
								} else {
									buf.append(dTmp).append("\t");
								}
								// 计算合计字段值
								if (rs.getBoolean("FIsTotal")) {//如果字段配置的字段是合计字段：开始
									if (hmTotal.containsKey(rs
											.getString("FDsField")
											+ "|"
											+ rs.getString("FOrderIndex"))) {
										if (hmTotal
												.get(rs
														.getString("FDsField")
														+ "|"
														+ rs
														.getString("FOrderIndex")) instanceof Double) {
											dTmpTotal = ((Double) hmTotal
													.get(rs
															.getString("FDsField")
															+ "|"
															+ rs
															.getString("FOrderIndex")))
															.doubleValue();
											dTmpTotal += dTmp;
											hmTotal
											.put(
													rs
													.getString("FDsField")
													+ "|"
													+ rs
													.getString("FOrderIndex"),
													new Double(
															dTmpTotal));
										}
									} else {
										hmTotal
										.put(
												rs
												.getString("FDsField")
												+ "|"
												+ rs
												.getString("FOrderIndex"),
												new Double(dTmp));
									}
									sTotalFieldAry.add(rs
											.getString("FDsField"));
								}//如果字段配置的字段是合计字段：结束
							}//处理公式字段beanId计算出的结果:结束
							if (rsDs.isFirst()) {//数据源结果集开始行
								sShowFieldAry
								.add(rs.getString("FDsField")
										+ "|"
										+ rs
										.getString("FOrderIndex"));
							}
						}//公式字段处理：结束
						
						if (rs.getString("FTotalInd") != null
								&& rs.getString("FTotalInd").trim()
								.length() > 0) {//如果设置了字段配置的字段标识
							hmTotal.put(rs.getString("FDsField") + "|"
									+ rs.getString("FOrderIndex"), rs
									.getString("FTotalInd"));//将键值对保存进合计HashMap（字段名|序号：字段标记）
						}
					}//逐列处理：结束
					iCurCol++;//下一列
				}//遍历rs(字段配置)：结束
			}//如果在填充范围内：结束
			if (buf.length() > 1) {
				buf.setLength(buf.length() - 1);
			}
			buf.append("\r\n");//动态报表数据需要在此处理			
			iCurRow++;//一行数据处理完，换一行
			// 将数据插入到存储表 sunkey Bug:0000515
			if (flag || rowCount > 0) {
				this.setStorageTabValue(storageTab, rsDs, dTmp, isFormula);
			}
			rs.beforeFirst();//遍历数据源结果集下一行之前，将字段配置结果集游标移至开始处，这样可以重新开始遍历字段配置结果集
		}//遍历数据源结果集：结束		
		//modify by huangqirong 2012-05-25 story #2473
		this.countInserRows(rows);	//计算要插入的行数	 add by huangqirong 2011-10-22 story #1747
		if(this.htParamDSSynRow.containsKey(sRepDsCode)){
			
			if(this.htParamDSSynRow.get(sRepDsCode) == -1)
				this.htParamDSSynRow.put(sRepDsCode, 1);
			
			//插入行数
			if(this.htCountRows.containsKey(sRepDsCode)){
				if(this.htCountRows.get(sRepDsCode) > this.fixRows){
					this.insertRows = rows;						
				}else if((this.htCountRows.get(sRepDsCode)+ rows) > this.fixRows){
					this.insertRows = this.htCountRows.get(sRepDsCode)+ rows - this.fixRows;					
				}
				this.htCountRows.put(sRepDsCode,this.htCountRows.get(sRepDsCode)+ rows);
			}else {				
				this.htCountRows.put(sRepDsCode , rows);
			}
						
			this.htParamDSSynRow.put(sRepDsCode, iCurRow + this.htParamDSSynRow.get(sRepDsCode) -1);			
		}
		//---end---
	}

	/**
	 * saveReport
	 * 
	 * @param sReport
	 *            String
	 * @return String
	 */
	public String saveReport(String sReport) {
		return "";
	}

	/**
	 * 取控件的相关参数
	 * 
	 * @param sctlCode
	 *            String control name code
	 * @return FaceCfgParamBean
	 */
	protected FaceCfgParamBean getCtlParam(String sctlCode) throws YssException {
		FaceCfgParamBean param = new FaceCfgParamBean();
		ResultSet rs = null;
		String strSql = "";
		try {
			param.setYssPub(pub);
			strSql = "select FCtlGrpCode from "
					+ pub.yssGetTableName("tb_rep_custom")
					+ " where FCusRepCode=" + dbl.sqlString(repCode);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if (rs.getString("FCtlGrpCode") == null
						|| rs.getString("FCtlGrpCode").length() == 0) {
					continue;
				}
				param.setSCtlGrpCode(rs.getString("FCtlGrpCode"));
				param.setSCtlCode(sctlCode);
				param.getSetting();
			}
			return param;
		} catch (Exception ex) {
			throw new YssException("获取控件的参数出错", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 创建存储表的方法 Bug:0000515
	 * 
	 * @param tabName
	 *            String 表名
	 * @param rsmd
	 *            ResultSetMetaData 数据源表结构
	 * @param sDsCode
	 *            String 数据源编号
	 * @param isFirst
	 *            boolean 是否为同一表的第一次
	 * @throws YssException
	 * @author sunkey
	 * @date 20081106
	 */
	private boolean buildStorageTab(String tabName, ResultSetMetaData rsmd,
			String sDsCode) throws YssException {
		String strSql = null;
		StringBuffer bufSql = new StringBuffer();
		ResultSet rs = null;
		Statement st = null;
		boolean flag = false;
		HashMap hmField = new HashMap();// 有重复的列 by leeyu 20100702
		// 太平资产报表导出生成临时表报重复列错误 合并太平版本调整
		try {
			/**
			 * 根据存储表的数据源配置创建表,如果表存在 则先执行Delete 表的结构直接从数据源中取，考虑DB2和Oracle
			 * 考虑报表数据是否和数据源配置数据相同
			 */
			if (tabName == null || tabName.trim().equals("")
					|| tabName.trim().equalsIgnoreCase("null")) {
				return false;
			}
			//edit by songjie 2014.03.04 BUG #89975 QDV4赢时胜上海(开发)2014年3月4日01_B pub.getUSerCode() 改为 pub.getUserID()
			if (dbl.yssTableExist(tabName + "_" + pub.getUserID())) {
				/**shashijie ,2011-10-12 , STORY 1698*/
				dbl.executeSql(dbl.doOperSqlDrop("drop table " + tabName + "_"
				//edit by songjie 2014.03.04 BUG #89975 QDV4赢时胜上海(开发)2014年3月4日01_B pub.getUSerCode() 改为 pub.getUserID()
						+ pub.getUserID()));
				/**end*/
			}
			bufSql.append("create table ").append(tabName).append("_").append(
			//edit by songjie 2014.03.04 BUG #89975 QDV4赢时胜上海(开发)2014年3月4日01_B pub.getUSerCode() 改为 pub.getUserID()
					pub.getUserID()).append("(");
			// 改为从Cell取数据 取字段名称，数字的类型,类型通过getMetadata获取
			strSql = "select fcontent,fformat from "
					+ pub.yssGetTableName("TB_REP_CELL") + " where frelacode='"
					+ sDsCode
					+ "' and frelatype='DSF' and frow=-1 order by fcol";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				// 创建存储表的列 字段从tb_xxx_rep_cell中取，并取fformat作为精度
				if (rs.getString("fcontent") != null
						&& hmField.get(rs.getString("fcontent")) == null) {// 合并太平版本调整
					hmField.put(rs.getString("fcontent"), rs
							.getString("fcontent"));
					// 如果是公式 则字段名称要截取 公式字段名#D***()
					if (rs.getString("fcontent").startsWith("#")) {
						String formulaField = rs.getString("fcontent")
								.substring(2,
										rs.getString("Fcontent").indexOf("("));
						bufSql.append(formulaField); // 字段名
						bufSql.append(" "); // 字段和字段类型之间的“ ”
						if (dbl.dbType == YssCons.DB_ORA) {
							bufSql.append("number(18,2)");
						} else if (dbl.dbType == YssCons.DB_DB2) {
							bufSql.append("DECIMAL(18,2)");
						}
					} else {
						// 使用循环比对数据源的字段和tb_xxx_rep_cell中的字段，从而进行操作
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							if (rs.getString("fcontent").trim()
									.equalsIgnoreCase(rsmd.getColumnName(i))) {
								bufSql.append(rs.getString("fcontent")); // 字段名
								bufSql.append(" "); // 字段和字段类型之间的“ ”
								bufSql.append(rsmd.getColumnTypeName(i)); // 字段类型
								// 数字的要考虑小数位,平常的考虑长度
								// 数据类型，要考虑精度，精度从tb_xxx_rep_cell列取split("\t")[2]
								if (rs.getString("fformat") != null
										&& (rsmd.getColumnTypeName(i).indexOf(
												"NUMBER") != -1
												|| rsmd.getColumnTypeName(i)
														.indexOf("DECIMAL") != -1
												|| rsmd.getColumnTypeName(i)
														.indexOf("FLOAT") != -1 || rsmd
												.getColumnTypeName(i).indexOf(
														"DOUBLE") != -1)) {
									if (rs.getString("fformat").trim()
											.equalsIgnoreCase("null")) {
										bufSql.append("("
												+ rsmd.getColumnDisplaySize(i)
												+ ",4)"); // 默认4位小数
									} else {
										bufSql.append("("
												+ rsmd.getColumnDisplaySize(i)
												+ ","
												+ rs.getString("fformat")
														.split("\t")[2] + ")");
									}
								} else if (rsmd.getColumnTypeName(i).indexOf(
										"DATE") == -1) {
									// bufSql.append("(" +
									// rsmd.getColumnDisplaySize(i) + ")");
									// 此地方需再考虑，字符串型与数值型及默认值的处理 by leeyu 20100705
									// 合并太平版本代码
									//add by huangqirong 2012-02-29 story #2088 Varchar2 长度太大 报错
									if (rsmd.getColumnTypeName(i).indexOf("VARCHAR2") > -1)
										bufSql.append("("+ (rsmd.getColumnDisplaySize(i) == 0 ? 50: rsmd.getColumnDisplaySize(i))+ ")");// Varchar2类型默认50长度
									else 
									//---end---	
									if (rsmd.getColumnTypeName(i).indexOf(
											"CHAR") > -1)
										bufSql
												.append("("
														+ (rsmd
																.getColumnDisplaySize(i) == 0 ? 50
																: rsmd
																		.getColumnDisplaySize(i))
														* 2 + ")");// char类型默认50长度
									else
										bufSql
												.append("("
														+ (rsmd
																.getColumnDisplaySize(i) == 0 ? 24
																: rsmd
																		.getColumnDisplaySize(i))
														+ ")");// number类型默认24长度
								}
								break; // 如果匹配到了 跳出循环，提高效率
							}
						}
					}
					bufSql.append(","); // 字段间的","
				}
				flag = true;
			}
			rs.getStatement().close();
			// 如果关于此数据源没有列描述，则不创建表s
			if (!flag) {
				return false;
			}
			// 删除最后一个","号
			bufSql.deleteCharAt(bufSql.length() - 1);
			bufSql.append(")");
			dbl.executeSql(bufSql.toString());
			return true;
		} catch (SQLException ex) {
			throw new YssException("抱歉，创建存储表信息出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
		}
	}

	/**
	 * 填充存储表，将数据导入到存储表中 Bug:0000515
	 * 
	 * @param tabName
	 *            String 存储表名称
	 * @param dsrs
	 *            ResultSet 包含要插入到存储表中数据的数据集（一般是根据数据源获取的）
	 * @param formula
	 *            double 公式数据
	 * @param isFormula
	 *            boolean 是否为公式字段
	 * @throws YssException
	 * @author sunkey
	 * @date 20081106
	 */
	private void setStorageTabValue(String tabName, ResultSet dsrs,
			double formula, boolean isFormula) throws YssException {
		String strSql = null;
		ResultSet rs = null;
		Statement st = null;
		ResultSetMetaData rsmd = null;
		try {
			if (tabName == null || tabName.trim().equals("")) {
				return;
			}
			// 用来获取上面创建的存储表的结构
			rs = dbl.openResultSet("select * from " + tabName + "_"
			//edit by songjie 2014.03.04 BUG #89975 QDV4赢时胜上海(开发)2014年3月4日01_B pub.getUSerCode() 改为 pub.getUserID()
					+ pub.getUserID());// alter by liuwei 中保打开多张报表需重新登录
			// 20100604
			rsmd = rs.getMetaData();// alter by liuwei 中保打开多张报表需重新登录 20100604
			// 合并太平版本代码
			// 插入数据 ，数据在获取表结构的时候已经获取到了
			String strValue = ""; // 存放报表数据的变量
			// 插入语句
			//edit by songjie 2014.03.04 BUG #89975 QDV4赢时胜上海(开发)2014年3月4日01_B pub.getUSerCode() 改为 pub.getUserID()
			strSql = "insert into " + tabName + "_" + pub.getUserID() + "(";
			// 原理：根据存储表的结构获取字段，根据字段从报表中获取数据
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				strSql += rsmd.getColumnName(i);
				// 根据字段类型不同 要采用不同的格式
				if (rsmd.getColumnTypeName(i).indexOf("NUMBER") != -1
						|| rsmd.getColumnTypeName(i).indexOf("DECIMAL") != -1
						|| rsmd.getColumnTypeName(i).indexOf("FLOAT") != -1
						|| rsmd.getColumnTypeName(i).indexOf("DOUBLE") != -1
						|| rsmd.getColumnTypeName(i).indexOf("INT") != -1
						|| rsmd.getColumnTypeName(i).indexOf("BIT") != -1) {
					// 如果是公式字段，值采用计算获得
					if (isFormula) {
						strValue += formula;
					} else {
						strValue += dsrs.getDouble(rsmd.getColumnName(i));
					}
				} else if (rsmd.getColumnTypeName(i).indexOf("DATE") != -1) {
					strValue += dsrs.getString(rsmd.getColumnName(i)) == null ? dsrs
							.getString(rsmd.getColumnName(i))
							: dbl.sqlDate(YssFun.formatDate(dsrs.getDate(rsmd
									.getColumnName(i))));
				} else {
					strValue += dsrs.getString(rsmd.getColumnName(i)) == null ? dsrs
							.getString(rsmd.getColumnName(i))
							: dbl.sqlString(dsrs.getString(rsmd
									.getColumnName(i)));
				}
				if (i != rsmd.getColumnCount()) {
					strSql += ",";
					strValue += ",";
				}
			}
			strSql += ")";
			strSql += " values(" + strValue + ")";
			dbl.executeSql(strSql);
		} catch (SQLException ex) {
			throw new YssException("抱歉，报表数据导入存储表出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
		}
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	/*****************************************************************
	 * 说明：预处理代码执行顺序是存储时保持的次序
	 * 
	 * @param DsCodes
	 *            数据源代码组
	 * @return
	 * @throws YssException
	 */
	private void doPretreat(String DsCode) throws YssException {
		RepDataSourceBean repDataSourceBean = null;
		RepPretreatBean pret = null;
		String sPretCodes[] = null;
		String pretCode = "";
		if (DsCode == null || DsCode.equalsIgnoreCase("")) {
			return;
		}
		String DsCodes[] = DsCode.split(",");// 拆分数据源代码组
		Connection conn = dbl.loadConnection();
		String TabCode = "";// 目标表
		boolean bTrans = true;
		for (int i = 0; i < DsCodes.length; i++) {
			repDataSourceBean = new RepDataSourceBean();
			repDataSourceBean.setYssPub(pub);
			repDataSourceBean.setRepDsCode(DsCodes[i]);
			repDataSourceBean.getSetting();
			pretCode = repDataSourceBean.getDPCodes() == null ? ""
					: repDataSourceBean.getDPCodes();// edit by yanghaiming
			// 20100713 如果为null则赋空值
			if (pretCode.equalsIgnoreCase("")) {
				continue;
			} else {
				sPretCodes = pretCode.split(",");
				for (int j = 0; j < sPretCodes.length; j++) {
					pret = new RepPretreatBean();
					pret.setYssPub(pub);
					pret.setDPDsCode(sPretCodes[j]);
					pret.getSetting();	
					pret.setCurrentDataSourceCode(DsCodes[i]);	//add by huangqirong 2012-01-06 story #1284 设置当前预处理所在数据源
					/**add---huhuichao 2013-6-28 STORY 4046 解决同一个数据源包含两个预处理，两个预处理往两个不同临时表里存数，无法动态创建第二个临时表的问题*/
					DataDictBean dD = new DataDictBean();
					dD.setYssPub(pub);
					dD.getTableInfo(pret.getTargetTabCode().trim());
					DataDictBean sDict = new DataDictBean();
					sDict.setYssPub(pub);
					String[] lastInformantion = dD.getSsubData().split("\f\f");
					sDict
							.protocolParse(lastInformantion[lastInformantion.length - 1]);

					pret.setTabType(sDict.getStrTableType());
					//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
					ResultSet rs = null;
					//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
					
					/**end---huhuichao 2013-6-28 STORY  4046*/
					// 在数据预处理前清空临时表的数据
					if (j == 0) {
						try {
							DataDictBean dataDict = new DataDictBean();
							dataDict.setYssPub(pub);
							dataDict.getTableInfo(pret.getTargetTabCode()
									.trim());
							// -----为了获取此表的类型 -------//
							DataDictBean subDict = new DataDictBean();
							subDict.setYssPub(pub);
							String[] lastInfo = dataDict.getSsubData().split(
									"\f\f");
							subDict
									.protocolParse(lastInfo[lastInfo.length - 1]);
							
							pret.setTabType(subDict.getStrTableType());//add by huangqirong 2012-01-04 story #2108 目标表类型
							
							if (subDict.getStrTableType().equalsIgnoreCase("1")) {// 判断是否为临时表
							//modify by nimengjing 2011.1.21 BUG #872 报表数据源含报表预处理时，执行时无法自动创建报表预处理中的临时表 
								if ((pret.getTargetTabCode() != null && pret
										.getTargetTabCode().trim().length() > 0)
										&& dbl.yssTableExist(pret
												.getTargetTabCode())) {// 判断是否存在临时表
									// 目标表为临时表
									TabCode = pret.getTargetTabCode();
									//modify by nimengjing 2011.1.21 BUG #870 报表预处理，动态数据源，执行前时会删除临时表所有数据 
									String strSql = "";
									
									strSql = " select * from "
											+ pub.yssGetTableName("Tb_Rep_TgtTabCond")
											+ " where FDPDsCode="
											+ dbl.sqlString(pret.getDPDsCode())
											+ " and FCheckState=1"
											+ " order by FOrderIndex ";
									rs = dbl.openResultSet(strSql);
									if (rs.next()&&pret.getDsType() == 1) {
										delTgtTabData(pret);//对动态数据源 ，调用方法，按照删除条件删除目标表中的数据
									} else {
										conn.setAutoCommit(false);
										dbl.executeSql("delete from "+ TabCode);
										conn.commit();
										bTrans = false;
										conn.setAutoCommit(true);
									}
									
									//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
									dbl.closeResultSetFinal(rs);
									//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
                                   //--------------------------------------------end bug#870----------------------------------------------------------
								}
						//--------------------------------------------end bug#872---------------------------------------------------------------------
							//add by huangqirong 2012-01-04 story #2108 目标表类型
							}else if(subDict.getStrTableType().equalsIgnoreCase("0")){
								if ((pret.getTargetTabCode() != null && pret
										.getTargetTabCode().trim().length() > 0)
										&& dbl.yssTableExist(pub.yssGetTableName(pret.getTargetTabCode()))) {// 判断是否存在系统表								
									// 目标表为系统表
									TabCode = pub.yssGetTableName(pret.getTargetTabCode());							
									//pret.setTargetTabCode(TabCode);//目标表设置为此
									//报表预处理，动态数据源，执行前时会删除临时表所有数据 
									String strSql = "";
									strSql = " select * from "
											+ pub.yssGetTableName("Tb_Rep_TgtTabCond")
											+ " where FDPDsCode="
											+ dbl.sqlString(pret.getDPDsCode())
											+ " and FCheckState=1"
											+ " order by FOrderIndex ";
									rs = dbl.openResultSet(strSql);
									if (rs.next()&&pret.getDsType() == 1) {
										delTgtTabData(pret);//对动态数据源 ，调用方法，按照删除条件删除目标表中的数据
									} else {
										conn.setAutoCommit(false);
										dbl.executeSql("delete from "+ TabCode);
										conn.commit();
										bTrans = false;
										conn.setAutoCommit(true);
									}
									
									//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
									dbl.closeResultSetFinal(rs);
									//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
								}
							}
							//---end---
							//add by huangqirong 2012-07-story #2200之前无存储表判断的处理
							else if (subDict.getStrTableType().equalsIgnoreCase("2")) {// 判断是否为存储表							
								if ((pret.getTargetTabCode() != null && pret
										.getTargetTabCode().trim().length() > 0)
										&& dbl.yssTableExist(pret
												.getTargetTabCode())) {// 判断是否存在存储表	
									// 目标表为临时表
									TabCode = pret.getTargetTabCode();									
									String strSql = "";
									strSql = " select * from "
											+ pub.yssGetTableName("Tb_Rep_TgtTabCond")
											+ " where FDPDsCode="
											+ dbl.sqlString(pret.getDPDsCode())
											+ " and FCheckState=1"
											+ " order by FOrderIndex ";
									rs = dbl.openResultSet(strSql);
									if (rs.next()&&pret.getDsType() == 1) {
										delTgtTabData(pret);//对动态数据源 ，调用方法，按照删除条件删除目标表中的数据
									}     
									
									//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
									dbl.closeResultSetFinal(rs);
									//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
								}						
							}
							//--emd---
							else {
								break; // 不存在表或系统表就直接跳出，不执行是清空数据操作
							}

						} catch (SQLException e) {
							throw new YssException("删除临时表数据出错!!!");
						} finally {
							dbl.endTransFinal(conn, bTrans);
							//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
							dbl.closeResultSetFinal(rs);
							//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
						}
					}
					doOnePretreat(pret);
					// initPreParam(pret);//初始化用于处理报表预处理的参数
					// pret.doPretreat();
				}
			}

		}

	}

	public String doOnePretreat(RepPretreatBean pret) throws YssException { // 更改为用DaoPretreatBean操作,并改为public类型
		// initParams(); //2009-1-9添加公共变量的初始化 leeyu
		String sResult = ""; // 定义预处理返回的数据的变量 MS00032

		// ----当预处理中的目标表不存在，则在此创建 sj modify 20081124 bug MS00037
		// -------------------------------------
		
		//add by huangqirong 2012-01-06  story #2108  数据源字段参数序号 
		if(pret.getTabType().trim().length()>0 && pret.getTabType().equalsIgnoreCase("1")){
		
			if ((pret.getTargetTabCode() != null && pret.getTargetTabCode().trim()
					.length() > 0)
					&& !dbl.yssTableExist(pret.getTargetTabCode())) { // 如果临时表不存在，就根据数据字典中的设置，动态建临时表
				DataDictBean dataDict = new DataDictBean();
				dataDict.setYssPub(pub);
				dataDict.getTableInfo(pret.getTargetTabCode().trim());
				// -----为了获取此表的类型 -------//
				DataDictBean subDict = new DataDictBean();
				subDict.setYssPub(pub);
				String[] lastInfo = dataDict.getSsubData().split("\f\f");
				subDict.protocolParse(lastInfo[lastInfo.length - 1]);
				if (subDict.getStrTableType().equalsIgnoreCase("1")) { // 若为临时表,则建表.
					dataDict.createTab(pret.getTargetTabCode().trim());
				}
			}
			// --------------------------------------------------------------------------------------------------------
		}else if(pret.getTabType().trim().length()>0 && pret.getTabType().equalsIgnoreCase("0")){
			if ((pret.getTargetTabCode() != null && pret.getTargetTabCode().trim()
					.length() > 0)
					&& !dbl.yssTableExist(pub.yssGetTableName(pret.getTargetTabCode()))) { // 如果临时表不存在，就根据数据字典中的设置，动态建临时表
				DataDictBean dataDict = new DataDictBean();
				dataDict.setYssPub(pub);
				dataDict.getTableInfo(pret.getTargetTabCode().trim());
				//dataDict.setTabName(pub.yssGetTableName(pret.getTargetTabCode().trim()));
				// -----为了获取此表的类型 -------//
				DataDictBean subDict = new DataDictBean();
				subDict.setYssPub(pub);
				String[] lastInfo = dataDict.getSsubData().split("\f\f");
				subDict.protocolParse(lastInfo[lastInfo.length - 1]);
				if(subDict.getStrTableType().equalsIgnoreCase("0")){
					dataDict.createTab(pub.yssGetTableName(pret.getTargetTabCode().trim()));
				}				
			}
		}
		//---end---
		//add by huangqirong 2012-07-19 story #2200 存储表没做判断处理
		else if(pret.getTabType().trim().length()>0 && pret.getTabType().equalsIgnoreCase("2")){
			if ((pret.getTargetTabCode() != null && pret.getTargetTabCode().trim()
					.length() > 0)
					&& !dbl.yssTableExist(pret.getTargetTabCode())) { // 如果存储表不存在，就根据数据字典中的设置，动态建存储表
				DataDictBean dataDict = new DataDictBean();
				dataDict.setYssPub(pub);
				dataDict.getTableInfo(pret.getTargetTabCode().trim());
				//dataDict.setTabName(pub.yssGetTableName(pret.getTargetTabCode().trim()));
				// -----为了获取此表的类型 -------//
				DataDictBean subDict = new DataDictBean();
				subDict.setYssPub(pub);
				String[] lastInfo = dataDict.getSsubData().split("\f\f");
				subDict.protocolParse(lastInfo[lastInfo.length - 1]);
				if(subDict.getStrTableType().equalsIgnoreCase("0")){
					dataDict.createTab(pub.yssGetTableName(pret.getTargetTabCode().trim()));
				}				
			}
		}
		

		if (pret.getDsType() == 2) { // 固定数据源 执行单独的一个javaBean 目前处理的是国内的数据接口
			DataBase dtatBase = (DataBase) pub.getPretFunCtx().getBean(
					pret.getBeanId()); // 调用相应的 javaBean
			dtatBase.setYssPub(pub);
			// dtatBase.setCheckState(check);//国内：MS00012
			// QDV4.1赢时胜（上海）2009年4月20日12_A add by songjie 2009-06-15 用于设置审核状态
			// 国内：MS00012 QDV4.1赢时胜（上海）2009年4月20日12_A add by songjie 2009-07-18
			// 用于设置自定义接口代码
			// dtatBase.setCusCfgCode(this.cusCfgCode);
			// dtatBase.initDate(this.beginDate, "", this.portCodes);
			dtatBase.inertData(); // 调用 javaBean的插入方法
		} else if (pret.getDsType() == 0) { // 静态数据源 执行update操作
			 this.exeDataSource(pret); //modify huangqirong 2012-07-19 story #2200
		} else if (pret.getDsType() == 1) { // 动态数据源 执行insert操作
			delTgtTabData(pret); // step1 根据目标表删除条件删除目标表中的数据
			insertTgtTabData(pret); // step2 根据预处理中的数据源插入到目标表中
			// afterInsertDeal(pret); //step3 当数据插入到了目标表后有可能还要对数据进行相应的处理
			// 注:但不是每个接口都是必须的 chenyibo 20071108
		}
		// ==================//
		else if (pret.getDsType() == 4) { // 提示类型预处理数据源 MS00032
			// sResult = promptPret(pret);
		}
		// ==================//
		else { // 参数数据源 从sql语句中的的到的数据作为参数
			// this.exeParamDataSource(pret);
		}
		return sResult; // 返回值MS00032
	}

	/**
	 * add by huangqirong 2012-07-19 story #2200 对报表的静态预处理支持
	 * */
	private void exeDataSource(RepPretreatBean pret) throws YssException {
		String strSql = "";
		Connection conn = null;
		boolean bTrans = true;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			strSql = this.buildDsSql(pret.getDataSource());
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("执行出错!");
		} finally {
			dbl.endTransFinal(bTrans);
		}
	}
	
	/*****************************************************
	 * 动态数据源：删除目标表数据 注意：如果没有设置删除条件，就不进行删除操作。
	 * 
	 * @throws YssException
	 */
	private void delTgtTabData(RepPretreatBean pret) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String delSql = ""; // 删除目标表的sql语句
		String DsSql = "";
		String targetTab = ""; // 目标表
		StringBuffer whereSqlBuf = new StringBuffer();
		String whereSql = "";
		Connection conn = null;
		DataDictBean dictBean = null;
		boolean bTrans = true;
		int iTabType = 0;
		try {
			// 1. 获取目标表表名
			dictBean = new DataDictBean(pub);
			// 获取表类型
			// 0:系统表,1:临时表 -1:当前表不存在
			iTabType = dictBean.getTabType(pret.getTargetTabCode());
			if (iTabType == 1) {
				targetTab = pret.getTargetTabCode();
			} else if (iTabType == 0) {
				targetTab = pub.yssGetTableName(pret.getTargetTabCode());
			}
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			DsSql = buildSql(pret.getDataSource());
			// 2. 根据删除条件对目标表数据的删除。
			strSql = " select * from "
					+ pub.yssGetTableName("Tb_Rep_TgtTabCond")
					+ " where FDPDsCode=" + dbl.sqlString(pret.getDPDsCode())
					+ " and FCheckState=1" + " order by FOrderIndex ";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				whereSqlBuf.append("a.").append(rs.getString("FTargetField"))
						.append("=").append("b.").append(
								rs.getString("FDsField")).append(" and ");
			}
			if (whereSqlBuf.length() > 5) {
				whereSql = whereSqlBuf.toString().substring(0,
						whereSqlBuf.toString().length() - 5);
			}
			//add by huangqirong 2012-01-06 story #1284 添加隐式删除条件
			pret.getTgFields(pret.getTargetTabCode());		//获取字段
			Hashtable tgFileds=pret.getTargetTabFileds();
			if(tgFileds.containsKey(pret.getRootFDsCode())){				
				pret.addTgField(targetTab, pret.getRootFDsCode()," VARCHAR2(20)");				//添加数据源代码字段
				whereSql += " and a."+pret.getRootFDsCode()+" = " + dbl.sqlString(pret.getCurrentDataSourceCode());				
			}
			//---end---
			
			if (whereSql.length() > 7) {
				delSql = " delete from " + targetTab + " a "
						+ " where exists (select * from (" + DsSql + ") b "
						+ " where " + whereSql + ")";
				dbl.executeSql(delSql);
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new YssException("删除目标表数据出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 动态数据源：根据预处理的数据源插入数据到目标表
	 * 
	 * @throws YssException
	 */
	private void insertTgtTabData(RepPretreatBean pret) throws YssException {
		Connection conn = dbl.loadConnection();
		PreparedStatement pst = null;
		String strSql = "";
		ResultSet rsDs = null; // 数据源的记录集
		ResultSet rs = null; // 该记录集用来打开接口预处理字段设置表
		String strPretSql = "";
		//IOperValue operValue = null;//无用注释
		HashMap hmFieldType = null; // 字段的字段名:字段的类型
		int iPstOrder = 1; // pst的编号
		//Object sData = ""; // 通过函数获取的数据处理//无用注释
		int tmpNum = 1; // 暂时为了调试用的
		/*String sDsFields = ""; // 通过spring 的方式，去调用某个涵数时，//无用注释
		// 需要从数据源中取的一些字段做为参数用
		String[] arrDsFields = null;
		ArrayList alDsFieldValue = null;*/ // 通过spring调用的方式，把要传入的参数放入ArrayList中//无用注释
		DataDictBean dictBean = null;
		boolean bTrans = true;
		int iTabType = 0;
		String sTabName = "";

		//List sqlList = null; // 用于存储解析数据源获取到得sql组 sunkey@Modify 20091121//无用注释
		//add by licai 20110125 STORY #441 需优化现在的报表自定义模板
		String strDynSql="";//动态表头
		ResultSet rsDyn=null;//动态表头结果集		
		int iStartRow=0;//表头开始行
		int iStartColumn=0;//表头开始列
		//int iEndRow=0;//表头结束行//无用注释
		int iEndColumn=0;//表头结束列		
		
		//int iTotalRows=0;//报表总行数//无用注释
		//int iTotalColumns=0;//报表总列数//无用注释
		String strRcSize="";//单元格高度和宽度
		String strMerge="";//单元格合并数据
		//add by licai 20110125=================================end
		try {
			// 1. 获取预处理目标表
			dictBean = new DataDictBean(pub);
			iTabType = dictBean.getTabType(pret.getTargetTabCode());
			if (iTabType == 0) {
				sTabName = pub.yssGetTableName(pret.getTargetTabCode());
			} else if (iTabType == 1) {
				sTabName = pret.getTargetTabCode();
			}
			conn.setAutoCommit(false); // chenyibo 20071002
			// 2. 生成插入到目标表的sql语句
			strSql = buildInsertTgtSql(pret); //
			pst = conn.prepareStatement(strSql);

			// 3. 获取目标表的字段类型
			hmFieldType = dbFun.getFieldsType(sTabName); // by liyu 080324

			// 根据预处理代码打开接口预处理字段设置，并按照顺序号 order by，
			strPretSql = " select * " + " from "
					+ pub.yssGetTableName("Tb_Rep_PretreatField")
					+ " where FDPDsCode=" + dbl.sqlString(pret.getDPDsCode())
					+ " and FCheckState=1" + " order by FOrderIndex ";
			rs = dbl.openResultSet_antReadonly(strPretSql);

			// 通过预处理中的数据源获得一个sql语句集合，如果数据源中不包含参数<PP>则一次性处理所有组合，否则将按组合逐个处理
			// sqlList = getDsSqlList(pret.getDataSource());

			strSql = buildSql(pret.getDataSource());//
			rsDs = dbl.openResultSet(strSql); // 数据源的记录集
			while (rsDs.next()) { // 循环数据源的记录集
				tmpNum = tmpNum + 1; // 为了调试用
				rs.beforeFirst();
				iPstOrder = 1;
				while (rs.next()) {// 循环预处理字段设置
					if (rs.getInt("FPretType") == 0) { // 数据源获取
						Object objRes = "";
						// 这里增加功能：导入预处理 数据源获取时多个字段值往一个字段里插时，中间用逗号分隔 by leeyu
						// 20091111
						if (rs.getString("FDsField").indexOf(",") > 1) {
							String[] arrField = rs.getString("FDsField").split(
									",");
							for (int i = 0; i < arrField.length; i++) {
								objRes = objRes
										+ (rsDs.getObject(arrField[i]) + ",");
							}
							if (String.valueOf(objRes).endsWith(",")) {
								objRes = String.valueOf(objRes).substring(0,
										String.valueOf(objRes).length() - 1);
							}
						} else {
							objRes = rsDs.getObject(rs.getString("FDsField"));
						}
						setPretPstValue(pst, iPstOrder, hmFieldType, objRes, rs
								.getString("FTargetField"));
					}
					iPstOrder++;
				}
				// 判断是否是a开头的表,另加上判断表是否为临时表,从数据字典里取的表类型 liyu 1218
				if (!pret.getTargetTabCode().toLowerCase().startsWith("a")
						&& iTabType == 0) {
					setPstCommonValue(pst, iPstOrder, "true");
				}
				
				//add by huangqirong 2012-01-06 story #1284
				pret.getTgFields(pret.getTargetTabCode());		//获取字段
				Hashtable tgFileds=pret.getTargetTabFileds();
				if(tgFileds.containsKey(pret.getRootFDsCode())){
					pret.addTgField(sTabName, pret.getRootFDsCode()," VARCHAR2(20)");				//添加数据源代码字段
					pst.setString(this.dsFieldNum, pret.getCurrentDataSourceCode());
				}
				//---end---
				
				pst.executeUpdate();
			}
			//add by licai 20110125 STORY #441 需优化现在的报表自定义模板
			if (bIsDynTabHead) {
				if (sTabName.equals("TMP_REP_CELL")) {// 临时表TMP_REP_CELL
					strDynSql = "select a.FROW,a.FCOL,a.FCONTENT,b.FROWS as totalRows,b.FCOLS as totalColumns from "
							+ pub.yssGetTableName("tb_rep_cell")
							+ " a right join (select FROWS,FCOLS,FREPFORMATCODE from "
							+ pub.yssGetTableName("tb_rep_format")
							+ ")b on a.Frelacode=b.FREPFORMATCODE"
							+ " right join (select * from "
							+ pub.yssGetTableName("tb_rep_custom")
							+ " where FCUSREPCODE="
							+ dbl.sqlString(this.repCode)
							+ " )c on a.Frelacode=c.FREPFORMATCODE"
							+ " where a.fRELATYPE='FMT' and a.FCONTENT like '动态表头:%'";
					rsDyn = dbl.openResultSet(strDynSql);// 动态表头:开始行号,开始列号:结束行号,结束列号
					if (rsDyn.next()) {
						// 动态条件表头
						iDynHeadStartRow = rsDyn.getInt("FROW");
						iDynHeadStartColumn = rsDyn.getInt("FCOL");
						// 动态表头(固定列)
						String[] arrRowCol = rsDyn.getString("FCONTENT").split(
								":");
						iStartRow = Integer.parseInt(arrRowCol[1].split(",")[0]
								.trim());// 开始行
						iStartColumn = Integer
								.parseInt(arrRowCol[1].split(",")[1].trim());// 开始列
						//无用注释
						/*iEndRow = Integer.parseInt(arrRowCol[2].split(",")[0]
								.trim());*/// 结束行
						iEndColumn = Integer
								.parseInt(arrRowCol[2].split(",")[1].trim());// 结束列
						// tb_XXX_rep_format中的行数和列数
						//无用注释
						/*iTotalRows = rsDyn.getInt("totalRows");
						iTotalColumns = rsDyn.getInt("totalColumns");*/
					}
					iColumnQty = iEndColumn - iStartColumn + 1;// 表头列数

					if (iCount > 1) {
						for (int i = 1; i < iCount; i++) {
							for (int j = 0; j < iColumnQty; j++) {// 横向报表，复制数据源表头格式
								strDynSql = "insert into TMP_REP_CELL(FRELACODE,FRELATYPE,FROW,"
										+ " FCOL,FCONTENT,FLLINE,FTLINE,FRLINE,FBLINE,FLCOLOR,"
										+ " FTCOLOR,FRCOLOR,FBCOLOR,FBACKCOLOR,FFORECOLOR,FFONTNAME,"
										+ " FFONTSIZE,FFONTSTYLE,FDATATYPE,FISMERGECOL,FFORMAT,FOTHERPARAMS)"
										+ " select FRELACODE,FRELATYPE,FROW,FCOL+"
										+ iColumnQty
										* i
										+ ",FCONTENT,FLLINE,"
										+ " FTLINE,FRLINE,FBLINE,FLCOLOR,FTCOLOR,FRCOLOR,FBCOLOR,FBACKCOLOR,"
										+ " FFORECOLOR,FFONTNAME,FFONTSIZE,FFONTSTYLE,FDATATYPE,FISMERGECOL,"
										+ " FFORMAT,FOTHERPARAMS from TMP_REP_CELL where (FRow="
										+ iDynHeadStartRow
										+ " and FCol="
										+ (iDynHeadStartColumn + j)
										+ " and FRELATYPE='FMT'"	//modify by huangqirong 2011-10-12 story #1387
										+ " and FRELACODE in ("+operSql.sqlCodes(this.strRptStyle)+"))" //add by huangqirong 2011-10-12 story #1387 为了不影响其他报表的条件 加上报表格式和相关数据源
										+ // 动态表头
										" or(FRow="
										+ iStartRow
										+ " and FCol="
										+ (iStartColumn + j)
										+ " and FRELATYPE='FMT'"	//modify by huangqirong 2011-10-12 story #1387
										+ " and FRELACODE in ("+operSql.sqlCodes(this.strRptStyle)+"))" //add by huangqirong 2011-10-12 story #1387 为了不影响其他报表的条件 加上报表格式和相关数据源
										+ // 表头
										" or (FRow in(-1,-2) and FCol="
										+ (iStartColumn + j)
										+ " and FRELATYPE='DSF'"	// 数据源和合计数据  //modify by huangqirong 2011-10-12 story #1387
										+ " and FRELACODE in ("+operSql.sqlCodes(this.strRptStyle)+"))"; //add by huangqirong 2011-10-12 story #1387 为了不影响其他报表的条件 加上报表格式和相关数据源
								dbl.executeSql(strDynSql);
							}
						}
					}
					updateDynHead();
				}
				if (sTabName.equals("TMP_REP_FORMAT")) {// 临时表TMP_REP_FORMAT
					/*
					 * strDynSql="update TMP_REP_FORMAT set FCOLS+"+iColumnQty*(iCount
					 * -1);//更新总列数 dbl.executeSql(strDynSql);
					 */

					strDynSql = "select FRCSIZE,FMERGE from TMP_REP_FORMAT a right join ("
							+ " select * from "
							+ pub.yssGetTableName("tb_rep_custom")
							+ " where FCUSREPCODE="
							+ dbl.sqlString(this.repCode)
							+ " )b on a.frepformatcode=b.FREPFORMATCODE";
					rsDyn = dbl.openResultSet(strDynSql);
					if (rsDyn.next()) {
						strRcSize = rsDyn.getString("FRCSIZE");
						strMerge = rsDyn.getString("FMERGE");
					}
					dbl.closeResultSetFinal(rsDyn);
					if (iCount > 1) {// 组合数>1时才需要处理
						String strRowHeight = strRcSize.split("[|]")[0].trim();// 行高数据串
						/*
						 * String[] arrRowHeight = strRowHeight.split(";");
						 * StringBuffer sbRowHeight = new StringBuffer(); for
						 * (int i = 0; i < arrRowHeight.length; i++) { if (i ==
						 * iStartRow - 1) { sbRowHeight .append(arrRowHeight[i])
						 * .append(";") .append(arrRowHeight[i]) .append( i ==
						 * arrRowHeight.length - 1 ? "" : ";"); } else {
						 * sbRowHeight .append(arrRowHeight[i]) .append( i ==
						 * arrRowHeight.length - 1 ? "" : ";"); } }
						 */
						String strColWidth = strRcSize.split("[|]")[1].trim();// 列宽数据串
						StringBuffer sbColWidth = new StringBuffer(strColWidth);
						for (int i = 0; i < iCount - 1; i++) {
							sbColWidth.append(";").append(strColWidth);
						}
						strRcSize = strRowHeight + "|" + sbColWidth.toString();// 重新拼接行高和列宽串赋值给FRCSIZE

						String[] arrMerge = strMerge.split(";");// 拆解合并单元格数据
						List arMerge = new ArrayList();
						for (int i = 0; i < arrMerge.length; i++) {
							arMerge.add(i, arrMerge[i]);
						}
						StringBuffer sbMerge = new StringBuffer();
						for (int i = 0; i < iColumnQty; i++) {
							String strRowCol = (iStartRow - 1) + ","
									+ (iStartColumn + i);
							int iSize = arMerge.size();// 原始arrayList元素数量
							int iIndex = iSize - 1;// 原始arrayList最后一个元素序号
							for (int j = 0; j < iSize; j++) {
								if (((String) arMerge.get(j))
										.startsWith(strRowCol)) {// 如果表头单元格包含合并数据，则新增的动态表头单元格合并数据都必须写进FMERGE字段
									StringBuffer sbRowCol = new StringBuffer();
									for (int k = 1; k <= iCount; k++) {// 如果有合并数据，则需要增加iCount个列，arMerge里相应增加iCount个元素
										String[] arrRowCol = ((String) arMerge
												.get(j)).split(",");// 拆出行、列数据
										for (int m = 0; m < arrRowCol.length; m += 2) {
											sbRowCol
													.append(
															(Integer
																	.parseInt(arrRowCol[m]) + 1)
																	+ // 行数下移1行
																	","
																	+ (Integer
																			.parseInt(arrRowCol[m + 1]) + iColumnQty
																			* k))// 列数右移iColumnQty*i列
													.append(
															m == arrRowCol.length - 2 ? ","
																	: "");
										}
										arMerge.add(++iIndex, sbRowCol
												.toString()); // 在arrayList里增加单元格合并数据
									}
								}
							}
						}
						// 遍历arrayList,增加动态表头单元格的合并数据
						for (int i = 0; i < arMerge.size(); i++) {
							sbMerge.append(((String) arMerge.get(i))).append(
									i == arrMerge.length - 1 ? "" : ";");
						}
						strMerge = sbMerge.toString();// 新的合并单元格数据
						// 更新总行数和总列数
						// :列数增加iCount*iColumnQty,字段FRCSIZE和FMERGE插入新增的单元格尺寸和合并数据
						strDynSql = "UPDATE TMP_REP_FORMAT SET FCOLS=FCOLS+"
								+ (iCount - 1) * iColumnQty + ",FRCSIZE="
								+ dbl.sqlString(strRcSize) + ",FMERGE="
								+ dbl.sqlString(strMerge);
						dbl.executeSql(strDynSql);
					}
				}
			}
			//add by licai 20110125 STORY #441======================end
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true); // chenyibo 20071002
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rsDs, rs);
			dbl.closeResultSetFinal(rsDyn);//add by licai 20110125 STORY #441 需优化现在的报表自定义模板		
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**更新动态列表头
	 * @throws SQLException
	 * @throws YssException
	 */
	private void updateDynHead() throws YssException {
		String strDynSql;
		for (int i = 0; i < iCount; i++) {// 将动态列表头条件，例如组合等，插入表头
			Iterator it = null;
			it = hmDynRowCodesPerDynColumnCode.keySet().iterator();
			while (it.hasNext()) {
				String strDynColumnNameCode = (String) it.next();
				if (strDynColumnNameCode.startsWith(i + "")) {
					String strDynColumnName = strDynColumnNameCode.split("[|]")[2];// 组合名称从前台传还是从库中查呢?前台报表控件传到后台目前只有代码无名称
					strDynSql = "update TMP_REP_CELL set FCONTENT="
							+ dbl.sqlString(strDynColumnName)
							+ ",FFORECOLOR=0"
							+ // 将动态条件表头前景颜色改为0
							" where FROW=" + iDynHeadStartRow + " and FCOL="
							+ (iDynHeadStartColumn + iColumnQty * i);// 将动态条件表头更新为具体的组合名或投资经理等
					try {
						dbl.executeSql(strDynSql);
					} catch (Exception e) {
						throw new YssException(e);
					}
					/*
					 * if(i==0){
					 * strDynSql="update TMP_REP_CELL set FCONTENT="+dbl
					 * .sqlString(strPortName)+",FFORECOLOR=0"+//将动态条件表头前景颜色改为0
					 * " where FROW="+iDynHeadStartRow+
					 * " and (FCOL="+iDynHeadStartColumn+
					 * " or FCol="+iDynHeadStartColumn+iColumnQty*i+
					 * ")";//将动态条件表头更新为具体的组合名或投资经理等 dbl.executeSql(strDynSql);
					 * }else if(i==iCount-1){
					 * strDynSql="update TMP_REP_CELL set FCONTENT="
					 * +dbl.sqlString
					 * (strPortName)+",FFORECOLOR=0"+//将动态条件表头前景颜色改为0
					 * " where FROW="
					 * +iDynHeadStartRow+" and FCOL="+iDynHeadStartColumn
					 * +iColumnQty*i;//将动态条件表头更新为具体的组合名或投资经理等
					 * dbl.executeSql(strDynSql); }else{strDynSql=
					 * "insert into TMP_REP_CELL(FRELACODE,FRELATYPE,FROW,"+
					 * " FCOL,FCONTENT,FLLINE,FTLINE,FRLINE,FBLINE,FLCOLOR,"+
					 * " FTCOLOR,FRCOLOR,FBCOLOR,FBACKCOLOR,FFORECOLOR,FFONTNAME,"
					 * +
					 * " FFONTSIZE,FFONTSTYLE,FDATATYPE,FISMERGECOL,FFORMAT,FOTHERPARAMS)"
					 * +" select FRELACODE,FRELATYPE,"+iDynHeadStartRow+
					 * " as FROW,FCOL+"+iColumnQty*i+
					 * ","+dbl.sqlString(strPortName)+" as FCONTENT,FLLINE,"+
					 * " FTLINE,FRLINE,FBLINE,FLCOLOR,FTCOLOR,FRCOLOR,FBCOLOR,FBACKCOLOR,"
					 * +
					 * " 0 as FFORECOLOR,FFONTNAME,FFONTSIZE,FFONTSTYLE,FDATATYPE,FISMERGECOL,"
					 * +//将动态条件表头前景颜色改为0
					 * " FFORMAT,FOTHERPARAMS from TMP_REP_CELL"+
					 * " where FRow="+
					 * iStartRow+" and FCol="+(iStartColumn+iColumnQty
					 * *(i-1));//将第一列以外的动态表头插入表 dbl.executeSql(strDynSql); }
					 */
					break;
				}
			}
		}
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 分 割 线
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	private String buildInsertTgtSql(RepPretreatBean pret) throws YssException {
		// 实现方法：取接口预处理字段设置中的目标表字段，并且按照排序号order，并结合pret中的目标表。
		// 把目标表字段组合成FSecurityCode,FPortCode,FBrokerCode字符，再生成和目标表字段同样数量的"?",作为参数
		String sResult = "";
		ResultSet rs = null;
		String strSql = "";
		String targetTab = ""; // 目标表
		String targetTabField = ""; // 目标表字段
		String targetTabFieldParam = ""; // 目标表字段参数 如:?,?,?
		StringBuffer targetTabFieldBuf = new StringBuffer();
		DataDictBean dictBean = null;
		int iTabType = 0;
		try {
			// 1. 获取目标表表名
			dictBean = new DataDictBean(pub);
			iTabType = dictBean.getTabType(pret.getTargetTabCode());
			if (iTabType == 0) {
				targetTab = pub.yssGetTableName(pret.getTargetTabCode());
			} else if (iTabType == 1) {
				targetTab = pret.getTargetTabCode();
			} // 增加对目标表类型的判断与处理. by liyu 080324

			strSql = " select FDsField,FTargetField from "
					+ pub.yssGetTableName("Tb_Rep_PretreatField")
					+ " where FDPDsCode=" + dbl.sqlString(pret.getDPDsCode())
					+ " and FCheckState=1" + " order by FOrderIndex";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				targetTabFieldBuf.append(rs.getString("FTargetField")).append(
						",");
			}
			if (!pret.getTargetTabCode().toLowerCase().startsWith("a")
					&& iTabType == 0) { // by liyu 1218 财务表没有这几个字段
				// ,另根据数据字典对表的判断只有系统表才加
				targetTabFieldBuf.append("FCheckState").append(",");
				targetTabFieldBuf.append("FCreator").append(",");
				targetTabFieldBuf.append("FCreateTime").append(",");
				targetTabFieldBuf.append("FCheckUser").append(",");
				targetTabFieldBuf.append("FCheckTime").append(",");
			}
			if (targetTabFieldBuf.length() > 1) {
				targetTabField = targetTabFieldBuf.toString().substring(0,
						targetTabFieldBuf.toString().length() - 1);
			}
			targetTabFieldParam = this.sqlParam(targetTabField); // 得到字段对应的"?"
						
			//add by huangqirong 2012-01-06 story #1284 添加隐式插入语句的字段
			pret.getTgFields(pret.getTargetTabCode());		//获取字段
			Hashtable tgFileds=pret.getTargetTabFileds();
			if(tgFileds.containsKey(pret.getRootFDsCode())){
				pret.addTgField(targetTab, pret.getRootFDsCode()," VARCHAR2(20)");				//添加数据源代码字段
				targetTabField += ","+pret.getRootFDsCode();
				targetTabFieldParam +=",?";
				this.dsFieldNum=targetTabFieldParam.split(",").length;
			}
			//---end---
			sResult = "insert into " + targetTab + " ( " + targetTabField
					+ " ) values (" + targetTabFieldParam + ")";
			return sResult;
		} catch (Exception e) {
			throw new YssException("生成插入目标表的SQL语句出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 生成插入SQL中的参数 比如传入的sFields是FSecurityCode,FPortCode,FBrokerCode
	 * 那么返回的结果就是?,?,?
	 * 
	 * @param sFields
	 * @return
	 * @throws YssException
	 */
	private String sqlParam(String sFields) throws YssException {
		String sResult = "";
		StringBuffer buf = new StringBuffer();
		String[] arrFields = null;
		try {
			if (sFields.length() > 0) {
				arrFields = sFields.split(",");
				for (int i = 0; i < arrFields.length; i++) {
					buf.append("?").append(",");
				}
				if (buf.length() > 1) {
					sResult = buf.toString().substring(0,
							buf.toString().length() - 1);
				}
			}
			return sResult;
		} catch (Exception e) {
			throw new YssException("生成SQL中的参数信息出错", e);
		}
	}

	/**
	 * 给pst付值
	 * 
	 * @param pst
	 *            PreparedStatement
	 * @param iOrder
	 *            int
	 * @param hmFieldType
	 *            HashMap
	 * @param value
	 *            Object
	 * @throws YssException
	 */
	public void setPretPstValue(PreparedStatement pst, int iOrder,
			HashMap hmFieldType, Object value, String targetField)
			throws YssException {
		int pstId = iOrder; // pst中的编号
		String fieldType = ""; // 字段类型
		String sValue = ""; // 字段的值
		try {
			sValue = String.valueOf(value).trim();
			fieldType = (String) hmFieldType.get(targetField.trim()
					.toUpperCase());
			if (fieldType.indexOf("VARCHAR") > -1
					|| fieldType.indexOf("CHAR") > -1) {
//				if (sValue.trim().length() > 0 && sValue.trim() != "null") 
				if (sValue.trim().length() > 0 && !sValue.trim().equals("null")) 
				{
					pst.setString(pstId, sValue);
				} else {
					pst.setString(pstId, " ");
				}
			} else if (fieldType.indexOf("NUMBER") > -1) {
//				if (sValue.trim().length() > 0 && sValue.trim() != "null") 
				if (sValue.trim().length() > 0 && !sValue.trim().equals("null")) 
				{ // 如果不为空
					if (YssFun.isNumeric(sValue)) { // 如果是double型的数据
						pst.setDouble(pstId, YssFun.toDouble(sValue));
					} else {
						pst.setInt(pstId, YssFun.toInt(sValue));
					}
				} else {
					pst.setInt(pstId, 0); // 如果是空就存入一个0
				}
			} else if (fieldType.indexOf("DATE") > -1) {
//				if (sValue.trim().length() > 0 && sValue.trim() != "null") 
				if (sValue.trim().length() > 0 && !sValue.trim().equals("null")) 
				{ // 如果日期内容不为空
					if (sValue.length() > 10) {
						sValue = YssFun.left(sValue, 10);
					}
					if (YssFun.isDate(sValue)) { // 如果是日期
						pst.setDate(pstId, YssFun.toSqlDate(YssFun
								.toDate(sValue)));
					} else {
						// 转化成日期格式
						pst.setDate(pstId, YssFun.toSqlDate(YssFun
								.toDate(YssFun.left(sValue, 4) + "-"
										+ YssFun.mid(sValue, 4, 2) + "-"
										+ YssFun.right(sValue, 2))));
					}
				} else {
					pst.setDate(pstId, YssFun.toSqlDate(YssFun
							.toDate("1900-01-01"))); // 存入一个默认值
				}
			}
		} catch (Exception e) {
			throw new YssException("给pst付值出错", e);
		}
	}

	/**
	 * 五个公共的字段,比如创建人,创建时间，等等
	 * 
	 * @param pst
	 *            PreparedStatement
	 * @param pstId
	 *            int
	 * @throws YssException
	 */
	public void setPstCommonValue(PreparedStatement pst, int pstId, String check)
			throws YssException {
		try {
			// 增加导入数据时候是否审核的功能 20071107 chenyibo
			if (check.equalsIgnoreCase("true")) {
				pst.setInt(pstId++, 1);
				pst.setString(pstId++, pub.getUserCode());
				pst.setString(pstId++, YssFun
						.formatDatetime(new java.util.Date()));
				pst.setString(pstId++, pub.getUserCode());
				pst.setString(pstId++, YssFun
						.formatDatetime(new java.util.Date()));

			} else {
				pst.setInt(pstId++, 0);
				pst.setString(pstId++, pub.getUserCode());
				pst.setString(pstId++, YssFun
						.formatDatetime(new java.util.Date()));
				pst.setString(pstId++, "");
				pst.setString(pstId++, "");
			}
		} catch (Exception e) {
			throw new YssException("给pst付公共值出错");
		}
	}

	public int setFunPstValue(PreparedStatement pst, int iOrder,
			HashMap hmFieldType, Object value, String targetField)
			throws YssException {
		String[] arrTargetTabField = null;
		String[] arrTargetTabFieldVal = null;
		String targetTabField = "";
		String targetTabFieldVal = "";
		int pstId = iOrder; // pst中的编号
		String fieldType = ""; // 字段类型
		try {
			targetTabFieldVal = (String) value; // 目标表字段值
			targetTabField = targetField; // 目标表字段
			arrTargetTabField = targetTabField.split(",");
			arrTargetTabFieldVal = targetTabFieldVal.split(",");
			for (int i = 0; i < arrTargetTabField.length; i++) {
				fieldType = (String) hmFieldType.get(arrTargetTabField[i]
						.toUpperCase());
				if (fieldType.indexOf("VARCHAR") > -1) {

					if (i < arrTargetTabFieldVal.length) {
						if (arrTargetTabFieldVal[i].trim().length() > 0
//								&& arrTargetTabFieldVal[i].trim() != "null")
								&& !arrTargetTabFieldVal[i].trim().equals("null"))
						{
							pst.setString(pstId, arrTargetTabFieldVal[i]);
						} else {
							pst.setString(pstId, " ");
						}
					} else {
						pst.setString(pstId, " ");
					}
				} else if (fieldType.indexOf("NUMBER") > -1) {
					if (i < arrTargetTabFieldVal.length) {
						if (arrTargetTabFieldVal[i].length() > 0
								&& arrTargetTabFieldVal[i] != "null") { // 如果不为空
							if (YssFun.isNumeric(arrTargetTabFieldVal[i])) { // 如果是double型的数据
								pst.setDouble(pstId, YssFun
										.toDouble(arrTargetTabFieldVal[i]));
							} else {
								pst.setInt(pstId, YssFun
										.toInt(arrTargetTabFieldVal[i]));
							}
						} else {
							pst.setInt(pstId, 0); // 如果是空就存入一个0
						}
					} else {
						pst.setInt(pstId, 0); // 如果是空就存入一个0
					}
				} else if (fieldType.indexOf("DATE") > -1) {
					if (i < arrTargetTabFieldVal.length) {
						if (arrTargetTabFieldVal[i].length() > 0
								&& arrTargetTabFieldVal[i] != "null") { // 如果日期内容不为空
							if (YssFun.isDate(arrTargetTabFieldVal[i])) { // 如果是日期
								pst.setDate(pstId, YssFun.toSqlDate(YssFun
										.toDate(arrTargetTabFieldVal[i])));
							} else { // 转化成日期格式
								pst
										.setDate(
												pstId,
												YssFun
														.toSqlDate(YssFun
																.toDate(YssFun
																		.left(
																				arrTargetTabFieldVal[i],
																				4)
																		+ "-"
																		+ YssFun
																				.mid(
																						arrTargetTabFieldVal[i],
																						4,
																						2)
																		+ "-"
																		+ YssFun
																				.right(
																						arrTargetTabFieldVal[i],
																						2))));
							}
						} else {
							pst.setDate(pstId, YssFun.toSqlDate(YssFun
									.toDate("1900-01-01"))); // 存入一个默认值
						}
					} else {
						pst.setDate(pstId, YssFun.toSqlDate(YssFun
								.toDate("1900-01-01"))); // 存入一个默认值
					}
				}
				if (arrTargetTabField.length > 1) {
					if (i < arrTargetTabField.length - 1) {
						pstId++;
					}
				}
			}
			return pstId;
		} catch (Exception e) {
			throw new YssException("给pst付值出错");
		}
	}

	/************************************************************************************
	 * 处理报表颜色筛选功能 目前功能不能实现多字段过滤条件多种情况显示不同颜色的情况
	 * 
	 * 着色颜色种类只有一种的情况：列与列的关系是"and" 着色颜色种类多种的情况：一列的不同情况，做不同颜色，关系是"or"
	 * 
	 * @param rsDs
	 *            数据源结果集
	 * @param sRepDsCode
	 *            数据源代码
	 * @return 显示类型\r\f 着色类型 \r\f关系\r\f过滤条件 注意：过滤规则(列号\n条件1\n)
	 *         过滤内容(字段名\t字段类型\t颜色\t过滤规则\t关系\误差值)
	 * @throws YssException
	 */
	private String doColorFilter(ResultSet rsDs, String sRepDsCode)
			throws YssException {
		HashMap filterMap = null; // key：字段名 value:筛选条件\t关系\t误差值
		//HashMap colMap = null; // key：字段名 value:行数//无用注释
		String sShowStyle = ""; // 显示格式 (背景着色 ：0 字体着色： 1)
		String sColorStyle = ""; // 颜色类型 (单行亮色 ：0 单元格亮色：1)
		StringBuffer buffer = new StringBuffer(); // 记录亮色信息
		String oldValue = "";
		String value;
		ResultSet rs = null;
		String strSql = "";
		String sReturn = "";
		//String frelation = "";//无用注释
		try {
			/*
			 * begin zhouxiang MS01487 在没有设置“亮色筛选条件”时， 对于有参数的报表查询，只在第一条记录时加载规则，
			 * countRule初始为0时记载规则，否则返回
			 */
			if (countRule == 0) {
				countRule++;
			} else {
				return "";
			}
			// end zhouxiang MS01487 在没有设置“亮色筛选条件”时，可以查询出多条记录
			if (sRepDsCode == null || sRepDsCode.equalsIgnoreCase("")) {

				return "";
			}
			// 1. 获取报表颜色筛选条件
			strSql = "select ffielname,ffieldtype,fcolorstyle,fshowstyle,fcolor,fdiscrepancy,frelation,fcontent from "
					+ pub.yssGetTableName("tb_Rep_Colorfilter")
					+ " where FREPDSCODE="
					+ dbl.sqlString(sRepDsCode)
					+ " order by frelation desc";
			rs = dbl.openResultSet_antReadonly(strSql);
			if (!rs.next()) {
				return "";
			}
			rs.beforeFirst();

			while (rs.next()) {
				if (filterMap == null) {
					filterMap = new HashMap();
					sShowStyle = rs.getString("fshowstyle");
					sColorStyle = rs.getString("fcolorstyle");
				}
				if (filterMap.containsKey(rs.getString("ffielname"))) {
					oldValue = (String) filterMap
							.get(rs.getString("ffielname"));
					value = rs.getString("ffielname") + "\t"
							+ rs.getString("ffieldtype") + "\t"
							+ rs.getString("fcolor") + "\t"
							+ rs.getString("fcontent") + "\t"
							+ rs.getString("fdiscrepancy");
					filterMap.put(rs.getString("ffielname"), oldValue + "\n"
							+ value);
				} else {
					filterMap.put(rs.getString("ffielname"), rs
							.getString("ffielname")
							+ "\t"
							+ rs.getString("ffieldtype")
							+ "\t"
							+ rs.getString("fcolor")
							+ "\t"
							+ rs.getString("fcontent")
							+ "\t"
							+ rs.getString("fdiscrepancy"));
				}

			}

			dbl.closeResultSetFinal(rs);
			if (buffer.toString().length() > 0)
				return "";

			buffer.append(sShowStyle).append("\r\f");
			buffer.append(sColorStyle).append("\r\f");

			// 获取设置颜色字段的列号
			strSql = "select fdsfield,forderindex,frepdscode from "
					+ pub.yssGetTableName("tb_rep_dsfield")
					+ " where frepdscode=" + dbl.sqlString(sRepDsCode)
					+ " order by forderindex  ";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if (filterMap.containsKey(rs.getString("fdsfield"))) {
					buffer.append(
							rs.getInt("forderindex")
									+ "\n"
									+ (String) filterMap.get(rs
											.getString("fdsfield"))).append(
							"\r\f");
				}

			}
			if (buffer.toString().length() > 2) {
				sReturn = buffer.toString().substring(0,
						buffer.toString().length() - 2);
			}

			return sReturn;
		} catch (Exception e) {
			throw new YssException("执行报表着色出错!");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	public String buildDsSql(String strSql) throws YssException {
		return buildSql(strSql);
	}

	public void setRepParam(ResultSet rs) throws YssException {
		setRepRsParam(rs);
	}

	public void setAlRepParam(ArrayList aryAlRepParam) {
		this.alRepParam = aryAlRepParam;
	}

	public void setAlRepParamBak(ArrayList aryAlRepParamBak) {
		this.alRepParamBak = aryAlRepParamBak;
	}

	/**
	 * 增加把合计项插入到存储表中 add by qiuxufeng 20101208 QDV4太平2010年09月16日02_A
	 * 
	 * @方法名：setStorageTotalValue
	 * @参数：
	 * @返回类型：void
	 */
	public void setStorageTotalValue(String storageTab, HashMap totalValue)
			throws SQLException, YssException {
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String strSql = "";
		String strValue = "";
		String sKey = "";
		// Object objValue = null;
		try {
			rs = dbl.openResultSet("select * from " + storageTab + "_"
					+ pub.getUserCode());
			rsmd = rs.getMetaData();
			strSql = "insert into " + storageTab + "_" + pub.getUserCode()
					+ "(";
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				strSql += rsmd.getColumnName(i);
				sKey = rsmd.getColumnName(i) + "|" + (i - 1);
				// if(totalValue.containsKey(sKey)) {
				// objValue = totalValue.get(sKey);
				// if(objValue instanceof Double) {
				// strValue += (String)objValue;
				// } else {
				// strValue += dbl.sqlString((String)objValue);
				// }
				// } else {
				// strValue += "''";
				// }
				// 根据字段类型不同 要采用不同的格式
				if (rsmd.getColumnTypeName(i).indexOf("NUMBER") != -1
						|| rsmd.getColumnTypeName(i).indexOf("DECIMAL") != -1
						|| rsmd.getColumnTypeName(i).indexOf("FLOAT") != -1
						|| rsmd.getColumnTypeName(i).indexOf("DOUBLE") != -1
						|| rsmd.getColumnTypeName(i).indexOf("INT") != -1
						|| rsmd.getColumnTypeName(i).indexOf("BIT") != -1) {
					if (totalValue.containsKey(sKey)) {
						strValue += (Double) totalValue.get(sKey);
					} else {
						strValue += "''";
					}
				} else {
					if (totalValue.containsKey(sKey)) {
						strValue += dbl
								.sqlString((String) totalValue.get(sKey));
					} else {
						strValue += "''";
					}
				}
				if (i != rsmd.getColumnCount()) {
					strSql += ",";
					strValue += ",";
				}
			}
			strSql += ")";
			strSql += " values(" + strValue + ")";
			dbl.executeSql(strSql);
		} catch (SQLException ex) {
			throw new YssException("抱歉，报表合计数据导入存储表出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	// add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
	public String checkReportBeforeSearch(String para) throws YssException {
		return "";
	}
	// -----------------------------
	
	/*
	 * 查询报表纯数据（不含格式）
	 * add by huangqirong 2011-10-17 stroy #1747
	 * modify huangqirong 2012-06-20 story #2473
	 */
	public String getReportData(String repCode) throws YssException{
		ResultSet rs = null;
		ResultSet rsSub = null;
		String [] dsArr=null;
		String ds="";
		StringBuffer data=new StringBuffer();
		String strSql = "";
		//boolean isParaDs = false;  //modify huangqirong 2013-01-22 bug #6962 当时预留扩展 判断是否有参数数据源 控制，暂时不支持
		rowCount = 0;
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Custom")
					+ " where FCusRepCode = " + dbl.sqlString(repCode)
					+ " and FCheckState = 1";
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				createTmpTable(rs.getString("FTmpTables"));
				if (rs.getString("FParamSource") != null
						&& rs.getString("FParamSource").trim().length() > 0) {
					/**add---shashijie 2013-6-8 BUG 8200 重构调用统一方法*/
					strSql = getRepDatasourceSQL(rs.getString("FParamSource"), "");
					/**end---shashijie 2013-6-8 BUG 8200*/
					
					rsSub = dbl.openResultSet(strSql);
					if (rsSub.next()) {
						strSql = dbl.clobStrValue(rsSub.getClob("FDataSource"));
						strSql = this.buildSql(strSql);
					} else {
						strSql = "";
					}
					dbl.closeResultSetFinal(rsSub);
					if (strSql.length() > 0) {
						rsSub = dbl.openResultSet(strSql);
						ds = rs.getString("FSubDsCodes");
						if(ds.trim().length()> 0){
							while (rsSub.next()) {
								setRepRsParam(rsSub);									
								dsArr = ds.split(",");
								for (int i = 0; i < dsArr.length; i++) { 		//遍历数据源
									String subData=getDataSourceData(dsArr[i]);
									if(subData.length()!=0)
										data.append(subData+"\r\t");	//数据源
								}									
							}
							rsSub.close();
							this.htParamDSSynRow.clear();//add by huangqirong 2012-05-25 story #2473
							this.htParamDSSynCount.clear();//add by huangqirong 2012-05-25 story #2473
							this.htCountRows.clear();//add by huangqirong 2012-05-25 story #2473
						}
					}
				}else {
					if (rs.getString("FRepType").equalsIgnoreCase("0")){
						ds = rs.getString("FSubDsCodes")==null ? "" : rs.getString("FSubDsCodes").trim();
						this.doPretreat(rs.getString("FSubDsCodes"));
					}
					else if (rs.getString("FRepType").equalsIgnoreCase("1")){						
						ds = rs.getString("FSubDsCodes")==null ? rs.getString("FSubRepCodes") : (rs.getString("FSubDsCodes").trim().length()==0 ? rs.getString("FSubRepCodes"):rs.getString("FSubDsCodes") + "," + rs.getString("FSubRepCodes"));
						this.doPretreat(rs.getString("FSubDsCodes"));
					}
				}
			}
			dbl.closeResultSetFinal(rs);
			
			//modify huangqirong 2013-01-22 bug #6962 当时预留扩展 参数数据源 控制，暂时不支持
			//if(!isParaDs)   
			//	return data.toString();
			//---end---
			
			if(ds.length()==0)
				return "";
			
			dsArr=ds.split(",");
			for (int i = 0; i < dsArr.length; i++) { 		//遍历数据源
				String subData=getDataSourceData(dsArr[i]);
				if(subData.length()!=0)
					data.append(subData+"\r\t");	//数据源
			}
			if(data.length()>0)
				data.setLength(data.length()-2); //去掉最后的 \t
			if(data.length()==0)
				return "";
		}catch (Exception e) {
			throw new YssException("查询报表数据出错！", e);
		}finally {
			
		}
		return data.toString();
	}
	
	/*
	 * 查询数据源数据
	 * modify by huangqirong 2012-02-23  stroy #1284
	 * add by huangqirong 2011-10-17  stroy #1747
	 * modify huangqirong 2012-06-20 story #2473
	 * */
	public String getDataSourceData(String dataSourceCode) throws YssException{
		ResultSet rs = null;
		String dataSource="";	//数据源
		StringBuffer data=new StringBuffer();	//返回所有数据
		int dataRow=0;			//数据源查询行数
		int fixRow=0;			//固定行数
		int insertRow=0;		//需要插入excel行数
		//String  fillArea="";	//填充范围
		int starRow= 0;			// 下一数据源起始行
		
		String strSql="";
		try {
			/**add---shashijie 2013-6-8 STORY 8200 这里与报表查询一致都不判断数据源是否已审核*/
			strSql = getRepDatasourceSQL(dataSourceCode," And FIsexport = 1 ");
			/**end---shashijie 2013-6-8 STORY 8200*/
			rs = dbl.openResultSet(strSql);			
			if (rs.next()) {
				
				//fillArea = rs.getString("FFILLRANGE");
				//starRow = Integer.parseInt(fillArea.split(",")[0]); //起始行
				
				//modify by huangqirong 2012-05-25 story #2473
				String [] fillRange = rs.getString("FFillRange").split(",");				
				
				if("0".equalsIgnoreCase(rs.getString("FParamDSSynCount")) && this.htParamDSSynCount.containsKey(dataSourceCode)) //不需要执行多次
					return "";
				else if("0".equalsIgnoreCase(rs.getString("FParamDSSynCount")) && !this.htParamDSSynCount.containsKey(dataSourceCode))
					this.htParamDSSynCount.put(dataSourceCode, dataSourceCode);
				
				if("1".equalsIgnoreCase(rs.getString("FParamDSSynRow")) && "1".equalsIgnoreCase(rs.getString("FParamDSSynCount"))){					
					if(this.htParamDSSynRow.containsKey(dataSourceCode))
						fillRange[0] = this.htParamDSSynRow.get(dataSourceCode) + "";
					else
						this.htParamDSSynRow.put(dataSourceCode, Integer.parseInt(fillRange[0]));
					
					data.append(fillRange[0] + "," + fillRange[1] + "," + fillRange[2] + "," + fillRange[3]).append("\f");
				}else {
					data.append(rs.getString("FFillRange")).append("\f");
				}
				
				starRow = Integer.parseInt(fillRange[0]); //起始行
				//---end---
				
				if (rs.getString("FFIXROWS") != null && (rs.getString("FFIXROWS")+"").trim().length() > 0) // 数据源是否导出到Excel中
					fixRow = Integer.parseInt(rs.getString("FFIXROWS").trim());

				//data.append(rs.getString("FFILLRANGE")).append("\f"); // 填充范围

				dataSource = dbl.clobStrValue(rs.getClob("FDataSource")); // 获取数据源
			}
			rs.close();
			
			if(dataSource.length()==0)
				return "";
			dataSource=buildSql(dataSource);	//预处理处理数据源 (解析数据源)
			
			String fields = getFieldSet(dataSourceCode);	//获取数据源字段配置
			
			if(fields.length()==0)
				return "";
			
			String [] fieldss = fields.split(",");
			
			rs=dbl.openResultSet(dataSource);//执行数据源
			
			//modify by huangqirong 2012-02-03 story #1284
			StringBuffer dsData=new StringBuffer();
			
			while(rs.next()){
				for (int i = 0; i < fieldss.length; i++) {
					dsData.append(rs.getString(fieldss[i])).append("\t"); //列
				}
				if(dsData.length()>0)
					dsData.setLength(dsData.length()-1); //去掉最后的 \t 
				dsData.append("\r");	//行
				dataRow++;
			}
			
			if(dsData.length() > 0 )
				data.append(dsData);
			else
				return "";
			//---end---
			if(data.length()>0)
				data.setLength(data.length()-1); 	//去掉最后的 \r
						
			//add by huangqirong 2012-06-20 story #2473
			this.countInserRows(dataRow);
			if(this.htParamDSSynRow.containsKey(dataSourceCode)){
				
				if(this.htParamDSSynRow.get(dataSourceCode) == -1)
					this.htParamDSSynRow.put(dataSourceCode, 1);
				
				//插入行数
				if(this.htCountRows.containsKey(dataSourceCode)){
					if(this.htCountRows.get(dataSourceCode) > fixRow){
						this.insertRows = dataRow;						
					}else if((this.htCountRows.get(dataSourceCode)+ dataRow) > fixRow){
						this.insertRows = this.htCountRows.get(dataSourceCode)+ dataRow - fixRow;					
					}
					this.htCountRows.put(dataSourceCode,this.htCountRows.get(dataSourceCode)+ dataRow);
				}else {				
					this.htCountRows.put(dataSourceCode , dataRow);
				}
							
				this.htParamDSSynRow.put(dataSourceCode, dataRow + this.htParamDSSynRow.get(dataSourceCode));			
			}
			insertRow = this.insertRows;
			//---end---
			
			/*if(fixRow>0)
				insertRow = dataRow - fixRow;
			else
				insertRow = 0;
			
			if(insertRow < 0)
				insertRow = 0;
			*/
			
			if( this.dsStarRows == 0)
				this.dsStarRows = starRow == -1 ? 0 : starRow ; //初始索引行
			
			if( starRow != -1 )
				this.dsStarRows = starRow ;
			
			data.append("\n").append( insertRow + "," + dsStarRows);	//Excel要插入的行数
			
			if( fixRow == 0){
				this.dsStarRows += dataRow ;
			}else{
				this.dsStarRows = starRow + insertRow + fixRow;
			}
		}catch (Exception e) {
			throw new YssException("查询报表数据源出错！", e);
		}finally {
			dbl.closeResultSetFinal(rs);
		}
		return data.toString();
	}
	
	/**shashijie 2013-6-8 BUG 8200 获取报表数据源不判断是否已审核状态 */
	private String getRepDatasourceSQL(String dataSourceCode,String whereSQL) {
		String sql = " Select * From " + pub.yssGetTableName("Tb_Rep_Datasource")
			+ " Where FRepdsCode = " + dbl.sqlString(dataSourceCode)
			+ " "+whereSQL;
		return sql;
	}
	

	/*
	 * 查询字段配置
	 * add by huangqirong 2011-10-17  stroy #1747
	 * 
	 * */
	public String getFieldSet(String dataSourceCode) throws YssException{
		ResultSet rs = null;
		StringBuffer fields=new StringBuffer();
		
		String strSql="";
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Dsfield")
					+ " where FRepDsCode = " + dbl.sqlString(dataSourceCode)
					+ " order by FOrderIndex";
			rs = dbl.openResultSet(strSql);			
			while(rs.next()){
				fields.append(rs.getString("FDsField")).append(","); //数据源配置的字段 (此字符串就是排序 索引后的)
			}
			if(fields.length()>0)
				fields.substring(0,fields.length()-1);
		}catch (Exception e) {
			throw new YssException("查询字段配置出错！", e);
		}finally {
			dbl.closeResultSetFinal(rs);
		}
		return fields.toString();
	}
	
	//add by huangqirong 2011-10-20 story #1747
	public void setRepBean(CommonRepBean repBean){
		this.repBean=repBean;
	}
	
	
	/**
	 * add by huangqirong 2011-10-22 story #1747
	 * */
	public void countInserRows(int rows){
		if(this.fixRows > 0)
			this.insertRows = rows - this.fixRows;
		
		if(this.insertRows < 0)
			this.insertRows = 0;
	}
    
	/**
	 * add by guolongchao 20120106 story #1284
	 */
	public String buildReport2(String sType) throws YssException {
		ResultSet rs = null;
		ResultSet rsSub = null;
		String strSql = "";
		String sResult = "";	
		// 为了避免类成员变量对程序其他功能造成影响，每次生成报表的时候清0 sunkey 20081222
		rowCount = 0;
		try 
		{
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Custom")
					+ " where FCusRepCode = " + dbl.sqlString(this.repCode)
					+ " and FCheckState = 1";
			rs = dbl.openResultSet(strSql);
			if (rs.next()) 
			{
				createTmpTable(rs.getString("FTmpTables"));
				// 添加封账条件，如已封账，返回封账信息 
				if (strOffAcctInfo != "")
					return strOffAcctInfo;
				// 有参数来源的报表
				if (rs.getString("FParamSource") != null && rs.getString("FParamSource").trim().length() > 0) 
				{
					/**add---shashijie 2013-6-8 BUG 8200 重构调用统一方法*/
					strSql = getRepDatasourceSQL(rs.getString("FParamSource"), "");
					/**end---shashijie 2013-6-8 BUG 8200*/
					rsSub = dbl.openResultSet(strSql);
					if (rsSub.next()) {
						strSql = dbl.clobStrValue(rsSub.getClob("FDataSource"));
						strSql = this.buildSql(strSql);
					} else {
						strSql = "";
					}
					dbl.closeResultSetFinal(rsSub);
					if (strSql.length() > 0)
					{
						rsSub = dbl.openResultSet(strSql);
						// 设置行数参数，参数数据源是有多条记录的，为了避免存储表只保留最后一条记录，用此参数区分
						rowCount = 0;
						while (rsSub.next())
						{
							setRepRsParam(rsSub);
							//之后考虑汇总和明细报表
							if (rs.getString("FRepType").equalsIgnoreCase("0")) { // 明细组合
								this.doPretreat(rs.getString("FSubDsCodes"));
							} else if (rs.getString("FRepType").equalsIgnoreCase("1")) { // 汇总组合
								this.pretreatSumRep(rs.getString("FSUBREPCODES"));
							}
							rowCount++;
						}
					}
					alRepParam = alRepParamBak;
				}
				else 
				{
					if (rs.getString("FRepType").equalsIgnoreCase("0")) { // 明细组合
						this.doPretreat(rs.getString("FSubDsCodes"));
					} else if (rs.getString("FRepType").equalsIgnoreCase("1")) { // 汇总组合
						this.pretreatSumRep(rs.getString("FSUBREPCODES"));
					}
				}
			}
			return sResult;
		} catch (Exception e) {			
			throw new YssException(e); 			
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsSub);
		}
	}
	
	/** //无用注释
	 * add by guolongchao 20120106 story #1284
	 */
	/*private void buildAllDataSource2(String sSubDsCodes) throws YssException {
		String[] sDsGrpAry = null;		
		StringBuffer buf = new StringBuffer();
		try 
		{
			sDsGrpAry = (sSubDsCodes + "").split(",");
			for (int i = 0; i < sDsGrpAry.length; i++) 
				 buildDataSource(sDsGrpAry[i]);
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}*/
}
