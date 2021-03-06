package com.yss.main.operdeal.datainterface;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.yss.log.SingleLogOper;
import com.yss.main.dao.IDataSetting;
import com.yss.main.dao.IOperValue;
import com.yss.main.datainterface.DaoCusConfigureBean;
import com.yss.main.datainterface.DaoFileNameBean;
import com.yss.main.datainterface.DaoPretreatBean;
import com.yss.main.funsetting.SpringInvokeBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.function.CtlFunction;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.syssetting.DataDictBean;
import com.yss.pojo.param.dao.YssImpData;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

public class ImpCusInterface extends BaseDaoOperDeal implements IDataSetting{
	//-------------modify by guojianhua 2010 09 15增加了IDataSetting接口和其方法------------
	ArrayList alDaoParam = new ArrayList();
	DataDictBean dictBean = null;
	Hashtable hashTable = new Hashtable();

	Hashtable htPortPercent = null; // 用来存储按照比例读数的组合 sunkey
	String portCode = ""; // 当前处理的组合
	private int operType;
	private SingleLogOper logOper;
	private String tableName = "";//未解析表明(数据库字典中表明)

	public String getNum(String key, String kValue) throws YssException {
		String value = "";
		try {
			if (!hashTable.containsKey(key)) {
				value = kValue;
				hashTable.put(key, kValue);
				return kValue;
			} else {
				value = (String) hashTable.get(key);
				value = String.valueOf(Integer.parseInt(value) + 1);
				hashTable.put(key, value);
				return value;
			}
		} catch (Exception e) {
			throw new YssException("获取子编号出错!");
		}

	}

	/**
	 * 
	 * @param key
	 * @param kValue
	 * @return
	 * @throws YssException
	 */
	public String getNumForStep(String key, String kValue, int iStep)
			throws YssException {
		String value = "";
		try {
			if (!hashTable.containsKey(key)) {
				value = kValue;
				hashTable.put(key, kValue);
				return kValue;
			} else {
				value = (String) hashTable.get(key);
				value = String.valueOf(Integer.parseInt(value) + iStep);
				hashTable.put(key, value);
				return value;
			}
		} catch (Exception e) {
			throw new YssException("获取子编号出错!");
		}

	}

	public ImpCusInterface() {
	}

	/**
	 * 导入操作时获取文件名
	 */
	public String getImpPathFileName() throws YssException {
		ArrayList alFileNameSet = null;
		StringBuffer buf = new StringBuffer();
		try {
			DaoCusConfigureBean cusCfg = new DaoCusConfigureBean();
			cusCfg.setYssPub(pub);
			cusCfg.setCusCfgCode(cusCfgCode);
			cusCfg.getSetting();
			alFileNameSet = this.getFileNameSet(cusCfg);
			if (alFileNameSet.size() > 0) {
				/**shashijie 2012-10-22 STORY 2978 增加日期段数据导入功能*/
				//判断文件路径中是否存在"日期段目录"文件名类型,有则返回true
				if (isListHaveKey(alFileNameSet,"dateparagraph")) {
					this.recuFileNames(alFileNameSet,cusCfg,buf);
				} else {
					if(!this.beginDate.equals(this.endDate)){
						BeforGetName(alFileNameSet);
					}
					this.recuFileNames(alFileNameSet, 0, "", buf, cusCfg);
				}
				/**end shashijie 2012-10-22 STORY */
			}
			return buf.toString();
		} catch (Exception e) {
			throw new YssException("得到文件名出错!", e);
		}
	}

	/**shashijie 2012-10-22 STORY 2978 解析文件名路径 */
	private void recuFileNames(ArrayList alFileNameSet,
			DaoCusConfigureBean cusCfg, StringBuffer buf) throws YssException {
		//先循环组合
		String[] array = this.portCodes.split(",");
		for (int i = 0; i < array.length; i++) {
			String portcode = array[i];//组合代码
			
			//再循环日期
			Date day = this.beginDate;
			for (int j = 0; j <= YssFun.dateDiff(this.beginDate,this.endDate); j++) {
				
				//最后循环文件名集合
				for (int k = 0; k < alFileNameSet.size(); k++) {
					DaoFileNameBean fileName = (DaoFileNameBean) alFileNameSet.get(k);
					//解析文件名路径
					doResolveFileNames(portcode,day,fileName,buf);
					//最后一条
					if (k==alFileNameSet.size()-1) {
						//必须是在设置了自动匹配后缀的情况下才添加文件类型后缀
						if (cusCfg.getAutoFix()) {
							buf.append(".").append(cusCfg.getFileType());
						}
						buf.append("\r\n");
					}
				}
				
				day = YssFun.addDay(day, 1);
			}
		}
	}

	/**shashijie 2012-10-23 STORY 2978 解析文件名路径
	* @param portcode 当前处理组合
	* @param day 当前处理日期
	* @param fileName DaoFileNameBean对象
	* @param buf 返回结果的StringBuffer */
	private void doResolveFileNames(String portcode, Date day,
			DaoFileNameBean fileName,
			StringBuffer buf) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		try {
			if(fileName.getFileNameCls() == null){
				return ;
			}
			//组合
			if (fileName.getFileNameCls().equalsIgnoreCase("Port")) {
				buf.append(portcode);
			} 
			//券商
			else if (fileName.getFileNameCls().equalsIgnoreCase("Broker")) {
				strSql = "select FBrokerCode from "
						+ pub.yssGetTableName("Tb_Para_Broker")
						+ " where FCheckState = 1";
			}
			//销售网点
			else if (fileName.getFileNameCls().equalsIgnoreCase("Net")) {
				strSql = "select FSellNetCode from "
						+ pub.yssGetTableName("Tb_TA_SellNet")
						+ " where FCheckState = 1";
			}
			//交易类型
			else if (fileName.getFileNameCls().equalsIgnoreCase("TradeType")) {
				strSql = "select FTradeTypeCode from Tb_Base_TradeType "
						+ " where FCheckState = 1";
			}
			//交易席位
			else if (fileName.getFileNameCls().equalsIgnoreCase("TradeSeat")) {
				strSql = " select b.fseatnum as fsubcode from "
						+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
						+ // 根据在组合中设置的交易席位带出来
						" a left join (select * from "
						+ pub.yssGetTableName("tb_para_tradeseat")
						+ ") b on a.fsubcode = b.fseatcode"
						+ "  where  a.FPortCode in ("
						+ operSql.sqlCodes(super.portCodes)
						+ " ) and a.FRelaType=" + dbl.sqlString("TradeSeat");
			}
			//组合群代码
			else if (fileName.getFileNameCls().equalsIgnoreCase("AssetGroup")) {
				buf.append(pub.getPrefixTB());
			}
			//查询解析
			if (strSql.length() > 0) {
				rs = dbl.openResultSet_antReadonly(strSql);
				if (rs.next()){
					rs.beforeFirst();
					while (rs.next()) {
						//判断是否设置了数据字典
						if (fileName.getFileNameDictCode() != null && 
								!fileName.getFileNameDictCode().equalsIgnoreCase("null")) {
							buf.append(this.doConvertData(fileName.getFileNameDictCode(), rs.getString(1)));
						} else {
							buf.append(rs.getString(1));
						}
					}
				}
				// 虽然finally中进行了游标关闭，但为了避免递归游标打开过多,因此关闭游标
				dbl.closeResultSetFinal(rs);
			}
			//固定
			if (fileName.getFileNameCls().equalsIgnoreCase("No")) {
				buf.append(fileName.getFileNameConent());
			}
			//日期
			else if (fileName.getFileNameCls().equalsIgnoreCase("Date")) {
				Date bufDate = day;
				//自定义接口配置需支持文件夹日期和数据日期不一致的情况
				BaseOperDeal dateOperDeal = new BaseOperDeal();
				dateOperDeal.setYssPub(this.pub);
				//计算日期，如果设置了节假日则使用节假日计算文件名
				if (!(("null".equals(fileName.getHolidaycode()) || " ".equals(fileName.getHolidaycode())))
						&&fileName.getHolidaycode().length()>0) {
					bufDate = dateOperDeal.getWorkDay(
							fileName.getHolidaycode(), bufDate, fileName
									.getDelayDay());
				} else {
					bufDate = YssFun.addDay(bufDate, fileName.getDelayDay());
				}
				//日期控制参数，如果选择的日期是节假日则不计算他的延迟天数
				Date dsynchro=new java.util.Date();
				dsynchro=YssFun.addDay(this.beginDate, -1);
				/*for (int j = 0; j <= YssFun.dateDiff(this.beginDate,
						this.endDate); j++) {*/
				dsynchro=YssFun.addDay(dsynchro, 1);//同步日期
				if (!(("null".equals(fileName.getHolidaycode()) || " ".equals(fileName.getHolidaycode())))&&fileName.getHolidaycode().length()>0){
					java.util.Date dtemp=new java.util.Date();
					dtemp=dateOperDeal.getWorkDay(fileName.getHolidaycode(), dsynchro,-1);//昨日工作日的下一个工作日就是自身工作日
					dtemp=dateOperDeal.getWorkDay(fileName.getHolidaycode(), dtemp,1);
					if(!dsynchro.equals(dtemp)){//如果当前同步日期不是工作日则不需要处理
						return;
					}
				}
				
				SimpleDateFormat format = null;
				String sDateStr = "";
				if (fileName.getFormat() != null) {
					if (fileName.getFormat().length() > 0) {
						// 若日期格式录入'YYYYMMDD'或'YYYYmmdd'...则默认为合法的日期格式'yyyyMMdd'
						if (fileName.getFormat().equalsIgnoreCase("yyyyMMdd") &&
								fileName.getFormat().split(",").length==1) {
							format = new SimpleDateFormat("yyyyMMdd");
						} else {
							//支持英文的日期格式
							if(fileName.getFormat().split(",").length>=2)
							{
								if(fileName.getFormat().split(",")[1].toUpperCase().equals("ENGLISH"))
								{
									format = new SimpleDateFormat(fileName.getFormat().split(",")[0],Locale.ENGLISH);
								}
								else
								{
									format = new SimpleDateFormat(fileName.getFormat().split(",")[0]);	
								}
							}
							else
							{
								format = new SimpleDateFormat(fileName.getFormat());	
							}
						}
						sDateStr = format.format(bufDate);
					}
				} else {
					sDateStr = YssFun.formatDate(bufDate);
				}
				//日期型的文件名的数据字典转换功能
				if (fileName.getFileNameDictCode() != null
						&& !fileName.getFileNameDictCode().equalsIgnoreCase("null")) {
					buf.append(this.doConvertData(fileName.getFileNameDictCode(), sDateStr));
				} else {
					buf.append(sDateStr);
				}
			}
			//}
			//日期段目录
			else if (fileName.getFileNameCls().equalsIgnoreCase("dateparagraph")) {
				if (fileName.getFormat().equalsIgnoreCase("yyyyMMdd") &&
						fileName.getFormat().split(",").length==1) {
					buf.append(YssFun.formatDate(day, "yyyyMMdd"));
				} else {
					buf.append(YssFun.formatDate(day));
				}
			}
		} catch (Exception e) {
			throw new YssException("解析文件名出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2012-10-22 STORY 2978 判断文件路径中是否存在"日期段目录"文件名类型,有则返回true
	* @param Key 文件名类型标示,dateparagraph表示"日期段目录"
	* @return*/
	private boolean isListHaveKey(List alFileNameSet,String key) {
		boolean falg = false;
		for (int i = 0; i < alFileNameSet.size(); i++) {
			DaoFileNameBean fileName = (DaoFileNameBean) alFileNameSet.get(i);
			//文件名类型存在改标示,则返回true
			if(fileName.getFileNameCls() != null && fileName.getFileNameCls().equalsIgnoreCase(key)){
				falg = true;
				return falg;
			}
		}
		return falg;
	}

	private void BeforGetName(ArrayList alFileNameSet) throws YssException {
		int sum=0;
		for(int i=0;i<alFileNameSet.size();i++){
			if(((DaoFileNameBean)alFileNameSet.get(i)).getFileNameCls().equals("Date")){
				sum++;
			}
		}
		if(sum>=2){
			throw new YssException("对不起，该业务不支持在文件名中存在多个日期类型的跨日期段导入操作!");
		}
	}
	

	/**
	 * 得到文件名
	 * 
	 * @param cusCfg
	 *            DaoCusConfigureBean
	 * @throws YssException
	 * @return String
	 */
	public String getFileName(DaoCusConfigureBean cusCfg) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		try {
			strSql = " select * from " + pub.yssGetTableName("Tb_Dao_FileName")
					+ " where FCusCfgCode="
					+ dbl.sqlString(cusCfg.getCusCfgCode())
					+ " and FCheckState=1 order by FOrderNum";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if (rs.getString("FFIleNameConent") != null) {
					buf.append(rs.getString("FFIleNameConent"));
				}
			}
			return buf.toString();
		} catch (Exception e) {
			throw new YssException("得到文件名出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 用递归法获取文件名 胡昆 20070923
	 * 
	 * @param alFileNameSet
	 * @param index
	 * @param sSuperPath
	 * @param bufResult
	 * @param cusCfg
	 * @throws YssException
	 */
	public void recuFileNames(ArrayList alFileNameSet, int index,
			String sSuperPath, StringBuffer bufResult,
			DaoCusConfigureBean cusCfg) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		DaoFileNameBean fileName = null;
		ArrayList alBuf = new ArrayList();
		java.util.Date bufDate = null;
		String sCurPath = "";
		SimpleDateFormat format = null;
		try {

			// 以下根据文件类型取数据
			fileName = (DaoFileNameBean) alFileNameSet.get(index);
			//---add by yangshaokai 2012.1.4 BUG3504 QDV4交银施罗德2011年12月23日01_B start---//
			if(fileName.getFileNameCls() == null){
				return ;
			}
			//---add by yangshaokai 2012.1.4 BUG3504 QDV4交银施罗德2011年12月23日01_B end---//
			if (fileName.getFileNameCls().equalsIgnoreCase("Port")) {
				strSql = "select FPortCode from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						+ " where FCheckState = 1" + " and FPortCode in ("
						+ operSql.sqlCodes(super.portCodes) + // 20071204
																// chenyibo
																// 根据前台传过来的组合作为查询条件
						" )";
			} else if (fileName.getFileNameCls().equalsIgnoreCase("Broker")) {
				strSql = "select FBrokerCode from "
						+ pub.yssGetTableName("Tb_Para_Broker")
						+ " where FCheckState = 1";
			} else if (fileName.getFileNameCls().equalsIgnoreCase("Net")) {
				strSql = "select FSellNetCode from "
						+ pub.yssGetTableName("Tb_TA_SellNet")
						+ " where FCheckState = 1";
			} else if (fileName.getFileNameCls().equalsIgnoreCase("TradeType")) {
				strSql = "select FTradeTypeCode from Tb_Base_TradeType "
						+ " where FCheckState = 1";
			} else if (fileName.getFileNameCls().equalsIgnoreCase("TradeSeat")) { // 20071204
																					// chenyibo
																					// 文件名设置中新增交易席位
				// edit by yanghaiming 20100610 MS01257
				// QDV4赢时胜(上海)2010年5月26日05_B 这里改取席位号
				strSql = " select b.fseatnum as fsubcode from "
						+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
						+ // 根据在组合中设置的交易席位带出来
						" a left join (select * from "
						+ pub.yssGetTableName("tb_para_tradeseat")
						+ ") b on a.fsubcode = b.fseatcode"
						+ "  where  a.FPortCode in ("
						+ operSql.sqlCodes(super.portCodes)
						+ " ) and a.FRelaType=" + dbl.sqlString("TradeSeat");
				// -----------------------------------
			//---add by songjie 2011.09.27 需求 #1385 QDV4赢时胜上海2011年07月19日01_A start---//
			} else if (fileName.getFileNameCls().equalsIgnoreCase("AssetGroup")) {
				alBuf.add(pub.getPrefixTB());
			}
			//---add by songjie 2011.09.27 需求 #1385 QDV4赢时胜上海2011年07月19日01_A end---//
			if (strSql.length() > 0) {
				//------ modify by wangzuochun 2010.07.16 QDV4国内(测试)2010年07月16日01_B
				rs = dbl.openResultSet_antReadonly(strSql);
				if (rs.next()){
					rs.beforeFirst();
					while (rs.next()) {
						if (fileName.getFileNameDictCode() != null && ! // 20071204
																		// chenyibo
																		// 判断是否设置了数据字典
								fileName.getFileNameDictCode().equalsIgnoreCase(
										"null")) {
							alBuf.add(this.doConvertData(fileName
									.getFileNameDictCode(), rs.getString(1)));
						} else {
							alBuf.add(rs.getString(1));
						}
					}
				}
				else {
					throw new YssException(" 请在组合设置中设置组合代码【" + this.portCodes + "】对应的席位代码！");
				}
				//---------------------QDV4国内(测试)2010年07月16日01_B-------------------//
				// 虽然finally中进行了游标关闭，但为了避免递归游标打开过多,因此关闭游标
				// MS00006:QDV4.1赢时胜上海2009年2月1日05_A 多用户并发关联 add by sunkey
				// 20090602
				dbl.closeResultSetFinal(rs);
			}
			if (fileName.getFileNameCls().equalsIgnoreCase("No")) {
				alBuf.add(fileName.getFileNameConent());
			} else if (fileName.getFileNameCls().equalsIgnoreCase("Date")) {
				bufDate = beginDate;
				// add by zhouxiang 850 2010.11.16 自定义接口配置需支持文件夹日期和数据日期不一致的情况
				// edit by songjie 850 2010.12.10 修改接口处理提示设置节假日群的提示错误
				BaseOperDeal dateOperDeal = new BaseOperDeal();
				dateOperDeal.setYssPub(this.pub);
				if (!(("null".equals(fileName.getHolidaycode()) || " ".equals(fileName.getHolidaycode())))
						//edit by licai 20101208 BUG #530 swift报文接口的问题 
						&&fileName.getHolidaycode().length()>0) {// 计算日期，如果设置了节假日则使用节假日计算文件名
					//edit by licai 20101208 BUG #530=====================end
					bufDate = dateOperDeal.getWorkDay(
							fileName.getHolidaycode(), bufDate, fileName
									.getDelayDay());
				} else {
					bufDate = YssFun.addDay(bufDate, fileName.getDelayDay());
				}
				java.util.Date dsynchro=new java.util.Date();//日期控制参数，如果选择的日期是节假日则不计算他的延迟天数 zhouxiang 2010.12.13
				dsynchro=YssFun.addDay(this.beginDate, -1);
				for (int j = 0; j <= YssFun.dateDiff(this.beginDate,
						this.endDate); j++) {
					dsynchro=YssFun.addDay(dsynchro, 1);//同步日期
					if (!(("null".equals(fileName.getHolidaycode()) || " ".equals(fileName.getHolidaycode())))&&fileName.getHolidaycode().length()>0){
						java.util.Date dtemp=new java.util.Date();
						dtemp=dateOperDeal.getWorkDay(fileName.getHolidaycode(), dsynchro,-1);//昨日工作日的下一个工作日就是自身工作日
						dtemp=dateOperDeal.getWorkDay(fileName.getHolidaycode(), dtemp,1);
						/**yeshenghong 2013-6-19 BUG 8221*/
//						if(!dsynchro.equals(dtemp)){//如果当前同步日期不是工作日则不需要处理
//							continue;
//						}
						/**yeshenghong 2013-6-19 BUG 8221 end*/
					}
					//end by zhouxiang 850 2010.12.13自定义接口配置需支持文件夹日期和数据日期不一致的情况 
					String sDateStr = "";
					if (fileName.getFormat() != null) { // 20071106 chenyibo
						if (fileName.getFormat().length() > 0) {
							// add by songjie 2010.02.04 MS00877
							// QDV4赢时胜（上海）2010年02月03日01_B
							// 若日期格式录入'YYYYMMDD'或'YYYYmmdd'...则默认为合法的日期格式'yyyyMMdd'
							if (fileName.getFormat().equalsIgnoreCase("yyyyMMdd")&&fileName.getFormat().split(",").length==1) {
								format = new SimpleDateFormat("yyyyMMdd");
							} else {
								// add by songjie 2010.02.04 MS00877
								// QDV4赢时胜（上海）2010年02月03日01_B
								//format = new SimpleDateFormat(fileName.getFormat());								
								// add by songjie 2010.02.04 MS00877
								// QDV4赢时胜（上海）2010年02月03日01_B
								//update by guolongchao STORY 2210     支持英文的日期格式--------start
								if(fileName.getFormat().split(",").length>=2)
								{
									if(fileName.getFormat().split(",")[1].toUpperCase().equals("ENGLISH"))
									{
										format = new SimpleDateFormat(fileName.getFormat().split(",")[0],Locale.ENGLISH);
									}
									else
									{
										format = new SimpleDateFormat(fileName.getFormat().split(",")[0]);	
									}
								}
								else
								{
									format = new SimpleDateFormat(fileName.getFormat());	
								}
								//update by guolongchao STORY 2210     支持英文的日期格式--------end
							}
							// add by songjie 2010.02.04 MS00877
							// QDV4赢时胜（上海）2010年02月03日01_B
							// 字符串不能直接加入 Buf，先放在临时变量中然后再作数据字典转换。
							sDateStr = format.format(bufDate);
							// alBuf.add(format.format(bufDate));
						}
					} else {
						sDateStr = YssFun.formatDate(bufDate);
						// alBuf.add(YssFun.formatDate(bufDate)); //20071107
						// chenyibo
					}
					// 2009.10.27 蒋锦 添加 日期型的文件名的数据字典转换功能 MS00005
					// QDV4.1赢时胜（上海）2009年9月28日04_A
					if (fileName.getFileNameDictCode() != null
							&& !fileName.getFileNameDictCode()
									.equalsIgnoreCase("null")) {
						alBuf.add(this.doConvertData(fileName
								.getFileNameDictCode(), sDateStr));
					} else {
						alBuf.add(sDateStr);
					}
					
					// add by zhouxiang 850 2010.12.13 自定义接口配置需支持文件夹日期和数据日期不一致的情况
					
					if (!(("null".equals(fileName.getHolidaycode()) || " ".equals(fileName.getHolidaycode())))&&fileName.getHolidaycode().length()>0){ 
						bufDate = dateOperDeal.getWorkDay(
							fileName.getHolidaycode(), bufDate, 1);//取+1工作日
					}else {bufDate = YssFun.addDay(bufDate, 1);}
					// add by zhouxiang 850 2010.12.13 自定义接口配置需支持文件夹日期和数据日期不一致的情况
					
				}
			}
			// --------------------------------------------------
			// 开始用递归法取数
			index++;
			for (int j = 0; j < alBuf.size(); j++) {
	/*			boolean flag = false;
				for (int k = index; k>=0;k--){
					
				}
				fileName.getFileNameCls().equals("date");
				DaoFileNameBean fileTest =  (DaoFileNameBean) alFileNameSet.get(index + 1);
				if(fileName.getFileNameCls().equals("date") && fileTest.getFileNameCls().equals("date")){
					index ++;
					(String) alBuf.get(j)转成日期
					sCurPath = sSuperPath + (String) alBuf.get(j) + str;
					recuFileNames(alFileNameSet, index, sCurPath, bufResult,
							cusCfg);
				}*/
				if (index <= alFileNameSet.size() - 1) {
					sCurPath = sSuperPath + (String) alBuf.get(j);
					recuFileNames(alFileNameSet, index, sCurPath, bufResult,
							cusCfg);
				} else {
					bufResult.append(sSuperPath);
					bufResult.append((String) alBuf.get(j));
					// 2009.10.27 蒋锦 添加 必须是在设置了自动匹配后缀的情况下才添加文件类型后缀
					// MS00005 QDV4.1赢时胜（上海）2009年9月28日04_A
					if (cusCfg.getAutoFix()) {
						bufResult.append(".").append(cusCfg.getFileType());
					}
					bufResult.append("\r\n");
				}
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取指定接口配置的文件名信息
	 * 
	 * @param cusCfg
	 *            接口
	 * @return Arraylist 封装了所有文件名对象的集合
	 * @throws YssException
	 */
	private ArrayList getFileNameSet(DaoCusConfigureBean cusCfg)
			throws YssException {
		String strSql = "";
		ResultSet rs = null;
		ArrayList alFileName = new ArrayList();
		DaoFileNameBean fileName = null;
		try {
			strSql = " select  FFormat,FFIleNameConent,FFileNameCls,FFileNameDict,delaydays, nvl(holidaycode,'null') as holidaycode from " 
					+ pub.yssGetTableName("Tb_Dao_FileName")//edited by zhouxiang 2010.11.6 只加载相应的字段
					+ " where FCusCfgCode="
					+ dbl.sqlString(cusCfg.getCusCfgCode())
					+ " and FCheckState=1 order by FOrderNum";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				fileName = new DaoFileNameBean();
				fileName.setCusCfgCode(cusCfg.getCusCfgCode());
				fileName.setFormat(rs.getString("FFormat"));
				fileName.setFileNameConent(rs.getString("FFIleNameConent"));
				fileName.setFileNameCls(rs.getString("FFileNameCls"));
				fileName.setFileNameDictCode(rs.getString("FFileNameDict"));
				//add by zhouxiang 850 2010.11.16 自定义接口配置需支持文件夹日期和数据日期不一致的情况 
				fileName.setDelayDay(rs.getInt("delaydays"));
				fileName.setHolidaycode(rs.getString("holidaycode"));
				//add by zhouxiang 850 2010.11.16 自定义接口配置需支持文件夹日期和数据日期不一致的情况 
				alFileName.add(fileName);
			}
			return alFileName;
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取接口文件头信息
	 * 
	 * @return
	 * @throws YssException
	 */
	public String getFileInfo() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		String fileInfo = "";
		String loadRow = "";
		String loadIndex = "";
		String loadLen = "";
		try {
			strSql = " select * from " + pub.yssGetTableName("Tb_Dao_FileInfo")
					+ " where FCusCfgCode=" + dbl.sqlString(this.cusCfgCode)
					+ " and FCheckState=1 order by FOrderNum";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				loadRow = String.valueOf(rs.getInt("FLoadRow")); // 读取行
				loadIndex = String.valueOf(rs.getInt("FLoadIndex"));// 读取位置
				loadLen = String.valueOf(rs.getInt("FLoadLen")); // 读取长度

				buf.append(loadRow).append(",");
				buf.append(loadIndex).append(",");
				buf.append(loadLen).append("\r\t");
			}
			if (buf.length() > 2) {
				fileInfo = buf.toString().substring(0,
						buf.toString().length() - 2);
			}
			return fileInfo;
		} catch (Exception e) {
			throw new YssException("获取文件头数据出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 开始操作(插入数据+预处理)
	 * 
	 * @throws YssException
	 * @return String
	 */
	public String doInterface() throws YssException {
		try {
			initParams();
			DaoCusConfigureBean cusCfg = new DaoCusConfigureBean();
			cusCfg.setYssPub(pub);
			cusCfg.setCusCfgCode(cusCfgCode);
			cusCfg.getSetting();

			// 如果是XML文件则处理XML文件
			if (cusCfg.getFileType().equalsIgnoreCase("xml")) {
				ImpCusXMLInterface xmlInter = new ImpCusXMLInterface();
				xmlInter.setYssPub(pub);
				xmlInter.insertData(cusCfg, allData);
			} else {
				insertData(cusCfg); // 插入数据
			}

         // ---增加日志记录功能----guojianhua add 20100915-------//
            operType=9;
            logOper = SingleLogOper.getInstance();
            this.setFunName("interfacedeal");
            this.setModuleName("interface");
            this.setRefName("000222");
			logOper.setIData(this,operType, pub);
        // ------------------end-----------------------//

			// 导入时，先将数据插入到临时表，等待前台传预处理代码时再做预处理
			return cusCfg.getDPCodes() + "";
		} catch (Exception e) {
			throw new YssException("处理数据出错", e);
		}
	}

	/**
	 * 把数据插入到对应的表中
	 * 
	 * @param cusCfg
	 *            接口配置对象
	 * @throws YssException
	 *             自定义异常
	 */
	private void insertData(DaoCusConfigureBean cusCfg) throws YssException {

		int count = 0; // 计数器
		String strSql = "";
		int iPstIndex = 1;
		boolean bTrans = true;
		String[] allDataAry = null;
		HashMap hmFieldType = null;
		String[] contentAry = null;

		PreparedStatement pst = null;
		Connection conn = dbl.loadConnection();
		YssImpData impData = new YssImpData();

		// 若自定义接口中的文件名为空条件满足时才能认为是不读外部文件 by leeyu 080717
		if (cusCfg.getTabName().equalsIgnoreCase("null")) {
			return;
		}

		try {
			// 如果临时表不存在，就根据数据字典中的设置，动态建临时表 20071124 chenyibo
			if (!dbl.yssTableExist(cusCfg.getTabName())) {
				DataDictBean dataDict = new DataDictBean();
				dataDict.setYssPub(pub);
				dataDict.getTableInfo(cusCfg.getTabName().trim());
				dataDict.createTab(cusCfg.getTabName().trim());
			}

			// 开始进行事物控制
			conn.setAutoCommit(false);

			// 把临时表中的数据全部删除
			delData(cusCfg);

			// 把同一类的多个文件的数据进行拆分
			allDataAry = allData.split("\f\f");
			// 获取对应表的字段类型
			hmFieldType = dbFun.getFieldsType(cusCfg.getTabName());

			// 生成sql语句将数据插入到对应的表
			strSql = buildInsertSql(cusCfg);
			pst = conn.prepareStatement(strSql);
			for (int i = 0; i < allDataAry.length; i++) {
				//edit by songjie 2012.02.09 BUG 3732 QDV4农业银行2012年01月18日01_B
				impData.parseRowStr(allDataAry[i],cusCfg);

				// 设置文件头信息
				// setFileInfo(cusCfg, impData); --无实际用途 sunkey 注

				// 把文件内容拆分成数组
				//edit by licai 20101213 BUG #632 在QD估值系统中，读交易数据，如果交易数据为空就会报错
				if(!impData.getFileContent().equals("")){
					contentAry = impData.getFileContent().split("\r\t");
					
					for (int j = 0; j < contentAry.length; j++) {
						count++;
						iPstIndex = setFileNamePst(cusCfg, impData.getFileName(),
								pst, hmFieldType);
						iPstIndex = setFileInfoPst(cusCfg, impData.getFileInfo(),
								pst, hmFieldType, iPstIndex);
						//20110721 modified by liubo.Story #1167
						//定义iResult变量，获取setFileContentPst返回的游标，若为9999999，表示该条数据不在当前组合群的需要导入的组合代码中，不进入批处理。
						//******************************
						int iResult = setFileContentPst(cusCfg, contentAry[j], pst, hmFieldType,
								iPstIndex);
						if (iResult != 9999999)
						{
							pst.addBatch();
						}
						//***********end****************
						
						// 1W条执行一次批处理
						if (count == 10000) {
							pst.executeBatch();
							count = 0;
						}
					}
				}
			}
			//edit by licai 20101213 BUG #632===============================================end
			if (count != 0) {
				pst.executeBatch();
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("插入到对应的表中出错!", e);
		} finally {
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 删除对应表的所有数据
	 * 
	 * @param cusCfg
	 * @throws YssException
	 */
	private void delData(DaoCusConfigureBean cusCfg) throws YssException {
		String strSql = "";
		Connection conn = null;
		boolean bTrans = true;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			strSql = "delete from " + cusCfg.getTabName();
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 生成插入到对应表的SQL语句
	 * 
	 * @param cusCfg
	 * @return
	 * @throws YssException
	 */
	private String buildInsertSql(DaoCusConfigureBean cusCfg)
			throws YssException {
		String sResult = "";
		String sFileNameFields = ""; // 文件名字段
		String sFileInfoFields = ""; // 文件头字段
		String sFileContentFields = ""; // 文件内容字段
		String sParam = ""; // 字段的参数,如:?,?,?等

		String finalParam = "";
		String[] arrParam = null; // 用,分割把参数寸入数组中
		StringBuffer finalParamBuf = new StringBuffer();

		String[] arrFields = null; // 用,分割把字段寸入数组中
		String finalFields = ""; // 最终所有字段
		StringBuffer bufFields = new StringBuffer(); // 所有字段的buf
		StringBuffer finalFieldsBuf = new StringBuffer(); // 最终所有字段的buf

		sFileNameFields = sqlfileNameFields(cusCfg); // 生成文件名的字段
		sFileInfoFields = sqlfileInfoFields(cusCfg); // 生成文件头的字段
		sFileContentFields = sqlfileContentFields(cusCfg); // 生成文件内容的字段

		bufFields.append(sFileNameFields).append(",");
		bufFields.append(sFileInfoFields).append(",");
		bufFields.append(sFileContentFields);

		arrFields = bufFields.toString().split(",");
		for (int i = 0; i < arrFields.length; i++) {
			if (arrFields[i].length() > 0) {
				finalFieldsBuf.append(arrFields[i]).append(",");
			}
		}
		if (bufFields.length() > 0) {
			finalFields = finalFieldsBuf.toString().substring(0,
					finalFieldsBuf.toString().length() - 1);
		}
		sParam = sqlParam(sFileNameFields) + ",";
		sParam += sqlParam(sFileInfoFields) + ",";
		sParam += sqlParam(sFileContentFields);
		arrParam = sParam.split(",");
		for (int j = 0; j < arrParam.length; j++) {
			if (arrParam[j].length() > 0) {
				finalParamBuf.append(arrParam[j]).append(",");
			}
		}
		if (finalParamBuf.length() > 0) {
			finalParam = finalParamBuf.toString().substring(0,
					finalParamBuf.toString().length() - 1);
		}
		sResult = "insert into " + cusCfg.getTabName() + " ( " + finalFields
				+ " ) values (" + finalParam + ")";
		return sResult;
	}

	/**
	 * 生成文件名设置对应的插入SQL语句
	 * 
	 * @param cusCfg
	 * @return
	 * @throws YssException
	 */
	private String sqlfileNameFields(DaoCusConfigureBean cusCfg)
			throws YssException {
		// 实现方法：取文件名设置中对应表的字段不为NULL的，并且按照排序号order by
		// 1.把对应表的字段拼成一个字符串，中间用","分割
		String strSql = "";
		ResultSet rs = null;
		String sResult = "";
		StringBuffer buf = new StringBuffer();
		try {
			strSql = " select FTabFeild from "
					+ pub.yssGetTableName("Tb_Dao_FileName")
					+ " where FCusCfgCode="
					+ dbl.sqlString(cusCfg.getCusCfgCode())
					+ " and FTabFeild<>" + dbl.sqlString("null")
					+ " and FCheckState=1" + " order by FOrderNum";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				buf.append(rs.getString("FTabFeild")).append(",");
			}
			if (buf.length() > 1) {
				sResult = buf.toString().substring(0,
						buf.toString().length() - 1);
			}
			return sResult;
		} catch (Exception e) {
			throw new YssException("获取文件名字段信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 生成文件头设置对应的插入SQL语句
	 * 
	 * @param cusCfg
	 * @return
	 * @throws YssException
	 */
	private String sqlfileInfoFields(DaoCusConfigureBean cusCfg)
			throws YssException {
		// 实现方法：取文件头设置中对应表的字段不为NULL的，并且按照排序号order by
		// 1.把对应表的字段拼成一个字符串，中间用","分割
		String sResult = "";
		String strSql = "";
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		try {
			strSql = " select FTabFeild from "
					+ pub.yssGetTableName("Tb_Dao_FileInfo")
					+ " where FCusCfgCode="
					+ dbl.sqlString(cusCfg.getCusCfgCode())
					+ " and FTabFeild<>" + dbl.sqlString("null")
					+ " and FCheckState=1" + " order by FOrderNum";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				buf.append(rs.getString("FTabFeild")).append(",");
			}
			if (buf.length() > 1) {
				sResult = buf.toString().substring(0,
						buf.toString().length() - 1);
			}
			return sResult;
		} catch (Exception e) {
			throw new YssException("获取文件头字段信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 生成文件内容设置对应的插入SQL语句
	 * 
	 * @param cusCfg
	 * @return
	 * @throws YssException
	 */
	private String sqlfileContentFields(DaoCusConfigureBean cusCfg)
			throws YssException {
		// 实现方法：取文件头设置中对应表的字段不为NULL的，并且按照排序号order by
		// 1.把对应表的字段拼成一个字符串，中间用","分割
		String sResult = "";
		String strSql = "";
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		try {
			strSql = " select FTabFeild from "
					+ pub.yssGetTableName("Tb_Dao_FileContent")
					+ " where FCusCfgCode="
					+ dbl.sqlString(cusCfg.getCusCfgCode())
					+ " and FTabFeild<>" + dbl.sqlString("null")
					+ " and FCheckState=1" + " order by FOrderNum";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				buf.append(rs.getString("FTabFeild")).append(",");
			}
			if (buf.length() > 1) {
				sResult = buf.toString().substring(0,
						buf.toString().length() - 1);
			}
			return sResult;
		} catch (Exception e) {
			throw new YssException("获取文件内容字段信息出错", e);
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
	 * 设置文件头信息 -- 目前该方法未实现任何内容
	 * 
	 * @param cusCfg
	 * @param impData
	 * @throws YssException
	 * 
	 *             private void setFileInfo(DaoCusConfigureBean cusCfg,
	 *             YssImpData impData) throws YssException {
	 *             //实现方法：取文件名设置中对应表的字段不为NULL的，并且按照排序号order by
	 *             //1.根据文件头设置到文件内容中取出文件头信息并设置到impData对象的文件头信息属性中
	 *             //2.文件头信息每一节以一种特定的标识符分开，以便拆分成数组 String strSql = ""; ResultSet
	 *             rs = null; try { strSql = " select FTabFeild from " +
	 *             pub.yssGetTableName("Tb_Dao_FileInfo") +
	 *             " where FCusCfgCode=" + dbl.sqlString(cusCfg.getCusCfgCode())
	 *             + " and FTabFeild<>" + dbl.sqlString("null") +
	 *             " order by FOrderNum"; rs = dbl.openResultSet(strSql); while
	 *             (rs.next()) { } } catch (Exception e) { throw new
	 *             YssException("设置文件头信息", e); } //---- MS00449
	 *             QDV4赢时胜（上海）2009年5月13日03_B 关闭游标 sj --- finally {
	 *             dbl.closeResultSetFinal(rs); }
	 *             //------------------------------
	 *             ------------------------------ }
	 */

	/**
	 * 给文件名信息pst付值,返回最后一个标号
	 * 
	 * @param cusCfg
	 * @param sFileNameData
	 * @param pst
	 * @param hmFieldType
	 * @return
	 * @throws YssException
	 */
	private int setFileNamePst(DaoCusConfigureBean cusCfg,
			String sFileNameData, PreparedStatement pst, HashMap hmFieldType)
			throws YssException {
		// 实现方法：取文件名设置中对应表的字段不为NULL的，并且按照排序号order by
		// 1.把sFileNameData拆分成数组
		// 2.根据文件名设置中排序号到数组中取数
		// 3.根据对应表的字段到字段类型中找到对应的类型，并根据类型设置pst
		// 4.设置pst的标号是从1开始顺序往下，返回最后一个标号

		int i = 0; // arrFileNameData中的编号
		int pstId = 1; // pst中的编号
		ResultSet rs = null;
		String strSql = "";
		String fieldType = ""; // 字段类型
		String[] arrFileNameData = null;
		try {
			strSql = " select FTabFeild from "
					+ pub.yssGetTableName("Tb_Dao_FileName")
					+ " where FCusCfgCode="
					+ dbl.sqlString(cusCfg.getCusCfgCode())
					+ " and FTabFeild<>" + dbl.sqlString("null")
					+ " and FCheckState=1" + " order by FOrderNum";
			if (cusCfg.getSplitMark() != null) {
				if (cusCfg.getSplitType().equalsIgnoreCase("1")) { // 20071104
																	// chenyibo
																	// 如果是符号分割
					if (cusCfg.getSplitMark().trim().equalsIgnoreCase("Tab")) {
						arrFileNameData = sFileNameData.split("\t");
					} else { // 20071104 chenyibo 固定方式
						//---edit by songjie 2011.08.22 BUG 2496 QDV4农业银行2011年8月22日01_B start---//
						if(cusCfg.getSplitMark().trim().equalsIgnoreCase("|"))
						{
							arrFileNameData = sFileNameData.split(",");
						}else{
							arrFileNameData = sFileNameData.split(cusCfg.getSplitMark());
						}
						//---edit by songjie 2011.08.22 BUG 2496 QDV4农业银行2011年8月22日01_B end---//
					}
				}
			} else {
				arrFileNameData = sFileNameData.split("");
			}
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				fieldType = (String) hmFieldType.get(rs.getString("FTabFeild")
						.trim().toUpperCase()); // 20071107 chenyibo
				if (fieldType.indexOf("VARCHAR") > -1) {
					if (arrFileNameData[i].length() > 0
							&& arrFileNameData[i] != "null") {
						pst.setString(pstId, arrFileNameData[i]);
					} else {
						pst.setString(pstId, " ");
					}
				} else if (fieldType.indexOf("NUMBER") > -1) {
					if (arrFileNameData[i].length() > 0
							&& arrFileNameData[i] != "null") { // 如果不为空
						if (YssFun.isNumeric(arrFileNameData[i])) { // 如果是double型的数据
							pst.setDouble(pstId, YssFun
									.toDouble(arrFileNameData[i]));
						} else {
							pst.setInt(pstId, YssFun.toInt(arrFileNameData[i]));
						}
					} else {
						pst.setInt(pstId, 0); // 如果是空就存入一个0
					}
				} else if (fieldType.indexOf("DATE") > -1) {
					if (arrFileNameData[i].length() > 0
							&& arrFileNameData[i] != "null") { // 如果日期内容不为空
						if (YssFun.isDate(arrFileNameData[i])) { // 如果是日期
							pst.setDate(pstId, YssFun.toSqlDate(YssFun
									.toDate(arrFileNameData[i])));
						} else { // 转化成日期格式
							pst.setDate(pstId, YssFun.toSqlDate(YssFun
									.toDate(YssFun.left(arrFileNameData[i], 4)
											+ "-"
											+ YssFun.mid(arrFileNameData[i], 4,
													2)
											+ "-"
											+ YssFun.right(arrFileNameData[i],
													2))));
						}
					} else {
						pst.setDate(pstId, YssFun.toSqlDate(YssFun
								.toDate("1900-01-01"))); // 存入一个默认值
					}
				}
				pstId = pstId + 1;
				i = i + 1;
			}
			return pstId;

		} catch (Exception e) {
			throw new YssException("给文件名信息pst付值出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 给文件头信息pst付值,返回最后一个标号
	 * 
	 * @param cusCfg
	 * @param sFileInfoData
	 * @param pst
	 * @param hmFieldType
	 * @param iBeginIndex
	 * @return
	 * @throws YssException
	 */
	private int setFileInfoPst(DaoCusConfigureBean cusCfg,
			String sFileInfoData, PreparedStatement pst, HashMap hmFieldType,
			int iBeginIndex) throws YssException {
		// 实现方法：取文件头设置中对应表的字段不为NULL的，并且按照排序号order by
		// 1.把sFileInfoData数据拆分成数组
		// 2.根据文件头设置中排序号到数组中取数
		// 3.根据对应表的字段到字段类型中找到对应的类型，并根据类型设置pst
		// 4.设置pst的标号是从iBeginIndex开始顺序往下，返回最后一个标号
		int i = 0; // arrFileInfoData中的编号
		int pstId = 1; // pst的编号
		ResultSet rs = null;
		String strSql = "";
		String fieldType = ""; // 字段类型
		String[] arrFileInfoData = null;
		try {
			pstId = iBeginIndex;
			// sql语句:从Tb_Dao_FileInfo中根据自定义代码获取表头字段不等于null的数据
			strSql = " select FTabFeild from "
					+ pub.yssGetTableName("Tb_Dao_FileInfo")
					+ " where FCusCfgCode="
					+ dbl.sqlString(cusCfg.getCusCfgCode())
					+ " and FTabFeild<>" + dbl.sqlString("null")
					+ " and FCheckState=1" + " order by FOrderNum";
			if (cusCfg.getSplitMark() != null) {
				if (cusCfg.getSplitType().equalsIgnoreCase("1")) { // 如果是符号分割
																	// 20071104
																	// chenyibo
					if (cusCfg.getSplitMark().trim().equalsIgnoreCase("tab")) {
						arrFileInfoData = sFileInfoData.split("\t");
					} else { // 20071104 chenyibo 固定方式
						arrFileInfoData = sFileInfoData.split(cusCfg
								.getSplitMark().trim());
					}
				}
			} else {
				arrFileInfoData = sFileInfoData.split("");
			}
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				fieldType = (String) hmFieldType.get(rs.getString("FTabFeild"));
				if (fieldType.indexOf("VARCHAR") > -1) {
					if (arrFileInfoData[i].length() > 0
							&& arrFileInfoData[i] != "null") {
						pst.setString(pstId, arrFileInfoData[i]);
					} else {
						pst.setString(pstId, " ");
					}
				} else if (fieldType.indexOf("NUMBER") > -1) {
					if (arrFileInfoData[i].length() > 0
							&& arrFileInfoData[i] != "null") { // 如果不为空
						if (YssFun.isNumeric(arrFileInfoData[i])) { // 如果是double型的数据
							pst.setDouble(pstId, YssFun
									.toDouble(arrFileInfoData[i]));
						} else {
							pst.setInt(pstId, YssFun.toInt(arrFileInfoData[i]));
						}
					} else {
						pst.setInt(pstId, 0); // 如果是空就存入一个0
					}
				} else if (fieldType.indexOf("DATE") > -1) {
					if (arrFileInfoData[i].length() > 0
							&& arrFileInfoData[i] != "null") { // 如果日期内容不为空
						if (YssFun.isDate(arrFileInfoData[i])) { // 如果是日期
							pst.setDate(pstId, YssFun.toSqlDate(YssFun
									.toDate(arrFileInfoData[i])));
						} else { // 转化成日期格式
							pst.setDate(pstId, YssFun.toSqlDate(YssFun
									.toDate(YssFun.left(arrFileInfoData[i], 4)
											+ "-"
											+ YssFun.mid(arrFileInfoData[i], 4,
													2)
											+ "-"
											+ YssFun.right(arrFileInfoData[i],
													2))));
						}
					} else {
						pst.setDate(pstId, YssFun.toSqlDate(YssFun
								.toDate("1900-01-01"))); // 存入一个默认值
					}
				}
				pstId = pstId + 1;
				i = i + 1;
			}
			return pstId;
		} catch (Exception e) {
			throw new YssException("给文件头信息pst付值出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 给文件头信息pst付值,返回最后一个标号
	 * 
	 * @param cusCfg
	 * @param sRowData
	 * @param pst
	 * @param hmFieldType
	 * @param iBeginIndex
	 * @return
	 * @throws YssException
	 */
	private int setFileContentPst(DaoCusConfigureBean cusCfg, String sRowData,
			PreparedStatement pst, HashMap hmFieldType, int iBeginIndex)
			throws YssException {
		// 实现方法：取文件内容设置中对应表的字段不为NULL的，并且按照排序号order by
		// 1.把sRowData数据拆分成数组
		// 2.根据文件头设置中排序号到数组中取数
		// 3.根据对应表的字段到字段类型中找到对应的类型，并根据类型设置pst
		// 4.设置pst的标号是从iBeginIndex开始顺序往下，返回最后一个标号

		ResultSet rs = null;
		String strSql = "";
		String reStr = ""; // chenyibo 20071104
		String fieldType = ""; // 字段类型
		String[] assetGroupForCompare = null;
		int i = 0; // arrFileContentData数组编号
		int tmp = 0; // 临时为了调试用
		int pstId = 1; // pst中的编号
		int loadLen = 0; // 读取长度
		int loadIndex = 0; // 读取位置

		String tmpDate = ""; // 不是日期格式的先转化成日期格式 如:20070905 就要转成 2007-09-05 临时的格式
								// 然后存入到这个临时变量中去
		String tmpValue = ""; // 通过字典中转化的数据
		String fileContentValue = ""; // 一个字段的对应的值
		String[] arrFileContentData = null;

		try {
			pstId = iBeginIndex;
			// sql语句:从文件内容表中根据自定义接口代码获取字段类型不等于null的数据
			strSql = " select FTabFeild,FFormat,FFileContentDict,FLoadIndex,FLoadLen from "
					+ pub.yssGetTableName("Tb_Dao_FileContent")
					+ " where FCusCfgCode="
					+ dbl.sqlString(cusCfg.getCusCfgCode())
					+ " and FTabFeild<>"
					+ dbl.sqlString("null")
					+ " and FCheckState=1" + " order by FOrderNum";
			// 用标记号把数据分割
			if (cusCfg.getSplitMark() != null) {
				if (cusCfg.getSplitType().equalsIgnoreCase("1")) { // 20071104
																	// chenyibo
																	// 如果是符号分割
					if (cusCfg.getSplitMark().trim().equalsIgnoreCase("tab")) {
						arrFileContentData = sRowData.split("\t");
					} 
					//add by zhangfa 20101013 MS01768    自定义接口配置采用"|"作为分割符存在问题    QDV4华夏2010年09月20日01_B
					else if(cusCfg.getSplitMark().trim().equalsIgnoreCase("|")){
						arrFileContentData = sRowData.split("\\|");
					}
					//------------------------------------------------------------------------------------------------
					else { // 20071104 chenyibo 固定方式
						arrFileContentData = sRowData.split(cusCfg
								.getSplitMark());
					}
				}
			} else {
				arrFileContentData = sRowData.split("");
			}
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				tmp = tmp + 1;
				if (cusCfg.getSplitType().equalsIgnoreCase("0")) { // 20071102
																	// chenyibo
																	// 读取恒生的TA交易数据
																	// //用固定方式
					loadLen = rs.getInt("FLoadLen");
					loadIndex = rs.getInt("FLoadIndex");
					reStr = getStrByByte(sRowData, loadIndex, loadLen);
					fileContentValue = reStr.split("\r\t")[0]; // 以字节方式读取字符串数据
				} else { // 分割类型：符号分割
					// 2008.09.18 陈土强 修改
					// 判断读取位置的设置是否为零，是零就采取原先的按顺序读取方法，不是零就按照设置的读取位置来读取
					// BUG: 0000474
					loadIndex = rs.getInt("FLoadIndex");
					if (loadIndex == 0) {
						loadIndex = i; // 这里做判断，若读取位置没有设置的话，应该用按以前的顺序法读取数据 by
										// leeyu 081014
					}
					fileContentValue = arrFileContentData[loadIndex]; // 这里用loadIndex读取数据
																		// by
																		// leeyu
				}
				fieldType = (String) hmFieldType.get(rs.getString("FTabFeild")
						.trim().toUpperCase());
				if (fieldType.indexOf("VARCHAR") > -1
						|| fieldType.indexOf("CHAR") > -1) {
					if (fileContentValue.trim().length() > 0
							&& !fileContentValue.trim().equals("null")) {
						// 如果接口字典中设置了数据就要通过接口字典转换内容
						if (rs.getString("FFileContentDict") != null
								&& !rs.getString("FFileContentDict")
										.equalsIgnoreCase("null")) {
							tmpValue = doConvertData(rs
									.getString("FFileContentDict"),
									fileContentValue.trim()); // 去掉空格 20071022
																// chenyibo
//							pst.setString(pstId, tmpValue);			//20110721 modified by liub.Story #1167.
						} else {
//							pst.setString(pstId, fileContentValue.trim()); 	//20110721 modified by liub.Story #1167.
							tmpValue = fileContentValue.trim();				// 20071106
																			// chenyibo
						}
					} else {
//						pst.setString(pstId, " ");					//20110721 modified by liub.Story #1167.
						tmpValue = " ";
					}
					//20110721 modified by liub.Story #1167
					//判断当前进行赋值的是否是OMGEO_TRADE接口的第三个字段，也就是组合代码字段
					//若不是，则直接执行；若是，则需要将组合代码截取出来（OMGEO_TRADE接口的组合代码格式是"组合群代码-组合代码"）
					//*******************************
					if (!(this.cusCfgCode.equals("OMGEO_TRADE") && pstId == 3))
					{
						pst.setString(pstId, tmpValue);
					}
					else
					{
						assetGroupForCompare = tmpValue.split("-");
						pst.setString(pstId, (assetGroupForCompare.length > 1?assetGroupForCompare[1]:tmpValue));
					}
					//***************end******************

					//20110721 modified by liub.Story #1167
					//判断当前进行赋值的是否是OMGEO_TRADE接口的第三个字段，也就是组合代码字段
					//若是，则需要对接口文件的代码进行解析，解析出组合群代码和组合代码。
					//若接口文件记载的组合群代码和组合代码与系统目前记载的相同，则表明该条符合条件，可以进行导入。若不同，则不进行导入
					//********************************
					if (this.cusCfgCode.equals("OMGEO_TRADE") && pstId == 3)
					{
						//系统当前的代码是前台是以"组合1,组合2,组合3,...组合N-1,组合N"进行排列，通过","截取出明细，并存入portForCompare变量
						String[] portForCompare = portCodes.split(",");
						if (portForCompare.length > 0)
						{
							int iNumOfMiss = 0;
							for (int in = 0;in < portForCompare.length;in++)
							{
								//若接口文件的组合群和组合代码与系统的符合，则跳出该循环，继续进行下面的赋值，最后导入数据库
								//若不符合，则将iNumOfMiss进行自增加。
								//--------------------------------
								if ((assetGroupForCompare.length > 1?assetGroupForCompare[1]:tmpValue).equals(portForCompare[in]) && pub.getPrefixTB().equals(assetGroupForCompare[0]))
								{
									break;
								}
								else
								{
									iNumOfMiss = iNumOfMiss + 1;
								}
								//----------------end--------------------
							}
							//当portForCompare所有的条目遍历完成，iNumOfMiss等于portForCompare里面的明细数量，
							//表明该条数据记载的组合代码，与前台传入的需要进行导入的组合代码不符，返回9999999这个游标，表示该条数据在当前组合群不进行导入
							//==================================
							if (iNumOfMiss == portForCompare.length)
							{
								return 9999999;
							}
							//===============end===================
						}
					}
					//****************end****************
					
				} else if (fieldType.indexOf("NUMBER") > -1) {
					if (fileContentValue.trim().length() > 0
							&& !fileContentValue.trim().equals("null")) { // 如果不为空
						if (YssFun.isNumeric(fileContentValue)) { // 如果是double型的数据
							if (rs.getString("FFileContentDict") != null
									&& !rs.getString("FFileContentDict")
											.equalsIgnoreCase("null")) {
								tmpValue = this.doConvertData(rs
										.getString("FFileContentDict"),
										fileContentValue.trim()); // 去掉空格
																	// 20071022
																	// chenyibo
								pst.setDouble(pstId, YssFun.toDouble(tmpValue));
							} else {
								pst.setDouble(pstId, YssFun
										.toDouble(fileContentValue));
							}
						} else {
							if (rs.getString("FFileContentDict") != null
									&& !rs.getString("FFileContentDict")
											.equalsIgnoreCase("null")) {
								tmpValue = this.doConvertData(rs
										.getString("FFileContentDict"),
										fileContentValue.trim()); // 去掉空格
																	// 20071022
																	// chenyibo
								pst.setInt(pstId, YssFun.toInt(tmpValue));
							} else {
								pst.setInt(pstId, YssFun
										.toInt(fileContentValue));
							}
						}
					} else {
						pst.setInt(pstId, 0); // 如果是空就存入一个0
					}
				}
				// 20080102 chenyibo 2008-1-4这种形式的数据,不需要处理
				else if (fieldType.indexOf("DATE") > -1) {
					if (fileContentValue.trim().length() > 0
							&& !fileContentValue.trim().equals("null")) { // 如果日期内容不为空
						if (YssFun.isDate(fileContentValue.trim())) { // 如果是日期,fileContentValue.trim()，杨文奇20080124
							if (rs.getString("FFormat") != null
									&& !rs.getString("FFormat")
											.equalsIgnoreCase("null")) { // 如果是需要转化成在前台设置的格式
																			// 如:yyyy-MM-dd或者
																			// yyyyMMdd
								pst.setDate(pstId, YssFun.toSqlDate(YssFun
										.formatDate(fileContentValue.trim(), rs
												.getString("FFormat"))));
							} else {
								// 用默认的日期格式
								pst.setDate(pstId, YssFun.toSqlDate(YssFun
										.toDate(fileContentValue.trim())));
							}
						} else { // 不是日期格式的先转化成日期格式 如:20070905 就要转成 2007-09-05
							tmpDate = YssFun.left(fileContentValue.trim(), 4)
									+ "-"
									+ YssFun.mid(fileContentValue.trim(), 4, 2)
									+ "-"
									+ YssFun.right(fileContentValue.trim(), 2);
							if (rs.getString("FFormat") != null
									&& !rs.getString("FFormat")
											.equalsIgnoreCase("null")) {
								pst.setDate(pstId, YssFun.toSqlDate(YssFun
										.formatDate(tmpDate, rs
												.getString("FFormat"))));
							} else {
								tmpValue = this.doConvertData(rs
										.getString("FFileContentDict"),
										fileContentValue.trim()); // 这里主要考虑有非日期格式
																	// 20080321
																	// chenjia
								// 如#NA.NA这种数据的话 读到临时表里面会出错 通过字典转换成一个日期
								// 如：9999-12-31 然后再做其它的处理
								if (!tmpValue.trim().equals("")) {
									// 当没有字典转换的时候 得到的值是原始字段 如20081231 得转换成日期格式
									// 2008-12-31
									tmpValue = YssFun.left(tmpValue.trim(), 4)
											+ "-"
											+ YssFun.mid(tmpValue.trim(), 4, 2)
											+ "-"
											+ YssFun.right(tmpValue.trim(), 2);
									pst.setDate(pstId, YssFun.toSqlDate(YssFun
											.toDate(tmpValue)));
								} else {
									pst.setDate(pstId, YssFun.toSqlDate(YssFun
											.toDate(tmpDate)));
								}
							}
						}
					} else { // 如果是日期是空的话就存入"1900-01-01" 默认值
						pst.setDate(pstId, YssFun.toSqlDate(YssFun
								.toDate("1900-01-01"))); // 存入一个默认值
					}
				}
				pstId = pstId + 1;
				i = i + 1;
			}
			return pstId;
		} catch (Exception e) {
			throw new YssException("给文件内容信息pst付值出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 通过接口字典把数据转成系统中需要的数据
	 * 
	 * @param fileContentDict
	 *            String
	 * @return Object
	 * @throws YssException
	 */
	private String doConvertData(String fileContentDict, String srcConent)
			throws YssException {
		ResultSet rs = null;
		String strSql = "";
		String cnvConent = "  "; // 通过接口字典把原数据转成目标数据
		try {
			if (fileContentDict != null) {
				strSql = " select FCnvConent from "
						+ pub.yssGetTableName("Tb_Dao_Dict")
						+ " where FDictCode=" + dbl.sqlString(fileContentDict)
						+ " and FSrcConent=" + dbl.sqlString(srcConent)
						+ " and FCheckState=1";
				rs = dbl.openResultSet(strSql);
				if (rs.getRow() == 0) { // 判断 如果接口字典中没有值时就返回 原值 by ly 080218
					cnvConent = srcConent;
				}

				while (rs.next()) {
					cnvConent = rs.getString("FCnvConent");
				}

				return cnvConent;
			} else {
				return cnvConent;
			}
		} catch (Exception e) {
			throw new YssException("通过接口字典转换内容出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	protected String wipeSqlCond(String strSql) throws YssException {
		int iBPos = 0;
		int iEPos = 0;
		String sCond = "";
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
		return strSql;
	}

	/**
	 * 开始预处理 -- 本方法未被使用过
	 * 
	 * @param cusCfg
	 * @throws YssException
	 */
	// private void doPretreat(DaoCusConfigureBean cusCfg) throws
	// YssException {
	// String strSql = "";
	// ResultSet rs = null;
	// String dPCodes = "";
	// String[] arrDPCodes = null; //预处理代码组，
	// //一个接口中可能会有几个预处理代码
	// //通过几次运算后得到最终的数据
	// try {
	// dPCodes = cusCfg.getDPCodes();
	// if (dPCodes == null || dPCodes.length() == 0) {
	// //如果接口没有预处理代码，要让前台提示 edit by jc
	// //return; //如果接口没有预处理代码时返回 李钰添加 1019
	// throw new YssException(cusCfg.getCusCfgCode() +
	// "接口没有预处理代码，请先设置预处理再进行操作！");
	// //---------------------------------------jc
	// }
	// arrDPCodes = dPCodes.split(",");
	// for (int i = 0; i < arrDPCodes.length; i++) {
	// doOnePretreat(arrDPCodes[i]); //开始预处理
	// }
	// } catch (Exception e) {
	// throw new YssException("预处理数据出错", e);
	// }
	// }

	/**
	 * 重载此方法，用于将DaoPretreatBean中的数据先进行处理 by leeyu QDV4深圳2009年01月13日01_RA MS00192
	 * by leeyu 20090303
	 * 
	 * @param String
	 *            sPretCode 预处理代码
	 * @return YssException 自定义异常
	 */
	public String doOnePretreat(String sPretCode) throws YssException {
		DaoPretreatBean pret = new DaoPretreatBean();
		pret.setYssPub(pub);
		pret.setDPDsCode(sPretCode);
		pret.getSetting();
		return doOnePretreat(pret);
	}

	/**
	 * 传入预处理代码开始一个预处理 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
	 */
	public String doOnePretreat(DaoPretreatBean pret) throws YssException { // 更改为用DaoPretreatBean操作,并改为public类型
		initParams(); // 2009-1-9添加公共变量的初始化 leeyu
		String sResult = ""; // 定义预处理返回的数据的变量 MS00032

		/**shashijie 2011.06.07 STORY #970 目前接口预处理支持财务表的 A形式，但在解析时未能正确解析 */
		this.tableName = pret.getTargetTabCode();//未解析表名
		String[] sPortArr = portCodes.split(",");
		String targetTabName = "";//add by songjie 2012.04.27 BUG 4329 QDV4海富通2012年04月19日01_B
		for (int i = 0; i < sPortArr.length; i++) {
			this.portCode=sPortArr[i];//add by zhouwei 20120619  组合信息 story 2727
			//若无目标表(提示数据源)这里不做处理 shashijie 2011.06.20 
			if (this.tableName!=null && this.tableName.trim().length()>0) {
				//add by songjie 2012.04.27 BUG 4329 QDV4海富通2012年04月19日01_B
				targetTabName = pret.getTargetTabCode();
				String newTableName = buildTableName(pret.getTargetTabCode(),sPortArr[i]);//解析目标表中的<Set><year>表明
				pret.setTargetTabCode(newTableName);
			}
			
			// ----当预处理中的目标表不存在，则在此创建 sj modify 20081124 bug MS00037
			// -------------------------------------
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
				//update by guolongchao 20120310 STORY 2210   添加导入存储表类型-------start
				if (subDict.getStrTableType().equalsIgnoreCase("2")) { // 若为导入存储表,则建表.
					dataDict.createTab(pret.getTargetTabCode().trim());
				}
				//update by guolongchao 20120310 STORY 2210 添加导入存储表类型-------end
			}
			// --------------------------------------------------------------------------------------------------------

			if (pret.getDsType() == 2) { // 固定数据源 执行单独的一个javaBean 目前处理的是国内的数据接口
				DataBase dtatBase = (DataBase) pub.getPretFunCtx().getBean(
						pret.getBeanId()); // 调用相应的 javaBean
				dtatBase.setYssPub(pub);
	       //MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据 add by jiangshichao  2010.09.10 ----
	            dtatBase.setDealDate(dealDate);
	            dtatBase.setEndDate(endDate);
		    //MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据 end --------------------------------		
				dtatBase.setCheckState(check);// 国内：MS00012
												// QDV4.1赢时胜（上海）2009年4月20日12_A add
												// by songjie 2009-06-15 用于设置审核状态
				// 国内：MS00012 QDV4.1赢时胜（上海）2009年4月20日12_A add by songjie 2009-07-18
				// 用于设置自定义接口代码
				dtatBase.setCusCfgCode(this.cusCfgCode);
				//dtatBase.initDate(this.beginDate, "", this.portCodes); //modify huangqirong 2013-05-30 story #3871 单组合
				dtatBase.initDate(this.beginDate, "", sPortArr[i]); //add huangqirong 2013-05-30 story #3871 单组合
				dtatBase.inertData(); // 调用 javaBean的插入方法
				sResult = YssCons.YSS_BAIL_ACC_SETTINGSTATE;  ////add by zhaoxianlin 20121105 #story 3159 
			} else if (pret.getDsType() == 0) { // 静态数据源 执行update操作
				this.exeDataSource(pret);
			} else if (pret.getDsType() == 1) { // 动态数据源 执行insert操作
				// MS0 add by zhangfa 2010.06.30 QDV4海富通2010年06月22日02_AB
				//edit by yangshaokai 2011.12.29 STORY 2007
				/**shashijie 2012-3-23 BUG 4119 为空,false,空格不支持跨组合*/
				if (pret.ismGroupshare() == null ||
						(pret.ismGroupshare() != null && pret.ismGroupshare().trim().length() == 0)
						|| pret.ismGroupshare().trim().equals("false")) {
				/**end*/
					delTgtTabData(pret); // step1 根据目标表删除条件删除目标表中的数据
					sResult = insertTgtTabData(pret); // step2 根据预处理中的数据源插入到目标表中
					//delete by songjie 2011.09.08 BUG 2599 QDV4海富通2011年08月30日01_B
					//updateSubTradeNum(); //------ 还原删除的交易子表编号 add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 
				} else {

					// 1.获取所有的组合群代码
					//edit by yangshaokai 2011.12.29 STORY 2007
					String[] group;
					/**shashijie 2012-3-23 BUG 4119  如果为true则获取所有组合群,否则获取字段mGroupshare里的组合群代码*/
					if(pret.ismGroupshare().trim().equals("true")) {
						group = getAllGroup().split(",");
					} else {
						group = pret.ismGroupshare().split(",");
					}
					/**end*/
					
					//---------end------------
					
					// 2.获取目标表
					if (group != null && group.length > 0) {
						for (int j = 0; j < group.length; j++) {
							if (group[j].length() > 0) {
								// 3.pub.setPrefixTB("组合群代码")即给表加前缀
								pub.setPrefixTB(group[j]);
								//---add by songjie 2012.04.27 BUG 4329 QDV4海富通2012年04月19日01_B start---//
								if(targetTabName.indexOf("<Set>") != -1 ||
								   targetTabName.indexOf("< Set >") != -1){
									//若目标表为财务系统的套帐表，若设置了多组合群共享，则把数据导入到多组合群下所有组合对应的套帐表中
									String[] portcodes = getPortCodeOfAsset().split(",");
									for(int k = 0; k < portcodes.length; k++){
										String targetTabNm = buildTableName(targetTabName,portcodes[k]);
										pret.setTargetTabCode(targetTabNm);
										
										// 4.判断含有组合群号的表是否存在
										if (dbl.yssTableExist(pub.yssGetTableName(pret.getTargetTabCode()))) {
											delMTgtTabData(pret,group[j]); // step1 根据目标表删除条件删除目标表中的数据
											insertMTgtTabData(pret,group[j]); // step2根据预处理中的数据源插入到目标表中
											//delete by songjie 2013.01.30 BUG 5333 QDV4海富通2012年08月20日01_B
											//updateSubTradeNum(); //------ 还原删除的交易子表编号 add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 					 
										}
									}
								}else{
									String targetTabNm = buildTableName(targetTabName,sPortArr[i]);
									pret.setTargetTabCode(targetTabNm);
									
									// 4.判断含有组合群号的表是否存在
									if (dbl.yssTableExist(pub.yssGetTableName(pret.getTargetTabCode()))) {
										delMTgtTabData(pret,group[j]); // step1 根据目标表删除条件删除目标表中的数据
										insertMTgtTabData(pret,group[j]); // step2根据预处理中的数据源插入到目标表中
										//delete by songjie 2013.01.30 BUG 5333 QDV4海富通2012年08月20日01_B
										//updateSubTradeNum(); //------ 还原删除的交易子表编号 add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 					 
									}
								}
							}
						}
						if(targetTabName.indexOf("<Set>") != -1 ||
								   targetTabName.indexOf("< Set >") != -1){
							break;
						}
						//---add by songjie 2012.04.27 BUG 4329 QDV4海富通2012年04月19日01_B end---//
					}
					

				}
				// -------------------------------------------------------------------

				// afterInsertDeal(pret); //step3 当数据插入到了目标表后有可能还要对数据进行相应的处理
				// 注:但不是每个接口都是必须的 chenyibo 20071108
			}
			// ==================//
			else if (pret.getDsType() == 4) { // 提示类型预处理数据源 MS00032
				this.portCode=sPortArr[i];//story 1536 add by zhouwei 20111013 取得组合号，对于SQL的解析起作用
				sResult = promptPret(pret);
			}
			// ==================//
			else { // 参数数据源 从sql语句中的的到的数据作为参数
				this.exeParamDataSource(pret);
			}
			
			/**shashijie 2011.06.07 STORY #970 目前接口预处理支持财务表的 A形式，但在解析时未能正确解析 */
			pret.setTargetTabCode(this.tableName);//未解析表名
		}
		/**end*/
		return sResult; // 返回值MS00032
	}

	/**
	 * add by songjie 2012.05.02 
	 * BUG 4329 QDV4海富通2012年04月19日01_B
	 * @return
	 * @throws YssException
	 */
	private String getPortCodeOfAsset()throws YssException{
		String strSql = "";
		ResultSet rs = null;
		String portcodes = "";
		try{
			strSql = " select FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState = 1";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				portcodes += rs.getString("FPortCode") + ",";
			}
			
			if(portcodes.length() > 0){
				portcodes = portcodes.substring(0,portcodes.length() - 1);
			}
			return portcodes;
		}catch(Exception e){
			throw new YssException("获取组合数据出错", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 查找系统所有的组合群号码 MS01337 add by zhangfa 2010.07.01 QDV4海富通2010年06月22日02_AB
	 * 
	 * @return String
	 * @throws YssException
	 * 
	 */
	public String getAllGroup() throws YssException {
		StringBuffer group = new StringBuffer();
		Connection conn = null;
		String strSql = "";// 查找系统所有的组合群号码的sql语句
		ResultSet rs = null;
		boolean bTrans = true;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			strSql = "select fassetgroupcode from TB_SYS_ASSETGROUP";
			rs = dbl.openResultSet(strSql);

			while (rs.next()) {
				group.append(rs.getString("fassetgroupcode")).append(",");
			}
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("查询目标表出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
		return group.toString();
	}

	/**
	 * 处理提示类型的预处理方法 MS00032
	 * 
	 * @param pret
	 *            DaoPretreatBean
	 * @return String
	 * @throws YssException
	 */
	private String promptPret(DaoPretreatBean pret) throws YssException {
		String sResult = "";
		CtlFunction funCtl = new CtlFunction();
		funCtl.setYssPub(pub);
		funCtl.init(pret.getDataSource());
		funCtl.setBaseOper(this);
		sResult = funCtl.doFunctions();
		return sResult;
	}

	// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB 蒋世超 添加 2009.12.24 --------------
	private void SetTabYearPre() throws YssException {
		int beginYear;
		int endYear;
		beginYear = YssFun.getYear(super.beginDate);
		endYear = YssFun.getYear(super.endDate);
		if (beginYear == endYear) {
			pub.setPrefixYear(beginYear);
		} else {
			throw new YssException("财务系统表不支持跨年份操作！");
		}
	}

	// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB end 2009.12.24 --------------
	/**
	 * 删除目标表数据
	 * 
	 * @param pret
	 * @throws YssException
	 */
	private void delTgtTabData(DaoPretreatBean pret) throws YssException {
		// 实现方法：取目标表删除条件设置，并且按照排序号order，并结合pret中的目标表，生成类似以下SQL语句
		// delete from tb_001_data_subtrade a where exists
		// (select * from tb_001_data_subtrade_blm b
		// where a.FPortCode = b.FPortCode and
		// a.FBrokerCode = b.FBrokerCode and a.FInvmgrCode = b.FInvmgrCode)
		String strSql = "";
		ResultSet rs = null;
		ResultSet rsTemp = null; //------ add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 
		String selSql = ""; //------ add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 
		String delSql = ""; // 删除目标表的sql语句
		List dsTabSql = null; // 原表
		String targetTab = ""; // 目标表
		StringBuffer whereSqlBuf = new StringBuffer();
		String whereSql = "";
		Connection conn = null;
		boolean bTrans = true;
		int iTabType = 0;
		try {
			dictBean = new DataDictBean(pub);
			iTabType = dictBean.getTabType(tableName);/**shashijie 2011.06.07 STORY #970*/
			if (iTabType == 1||iTabType == 2) {//add by guolongchao 20111228  STORY 1499 添加导入存储表类型
				targetTab = pret.getTargetTabCode();
			} else if (iTabType == 0) {
				// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB 蒋世超 添加 2009.12.24
				// --------------
				if (pret.getTargetTabCode().toUpperCase().startsWith("A")) {
					// 财务报表向后台传递年份参数
					SetTabYearPre();
				}
				// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB end
				// ---------------------------------
				targetTab = pub.yssGetTableName(pret.getTargetTabCode());
			} // 根据表名判断表是否是临时表或是系统表 by liyu
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			dsTabSql = getDsSqlList(pret.getDataSource());
			strSql = " select * from "
					+ pub.yssGetTableName("Tb_Dao_TgtTabCond")
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
			if (whereSql.length() > 0) {
				// dsTabSql里面含有类似select '' as FSECURITYCODE 的代码，
				// 当在whereSql中用到a.FSECURITYCODE=b.FSECURITYCODE时报错
				// delSql = " delete from " + pub.yssGetTableName(targetTab) +
				// " a " +// by liyu
				for (int i = 0; i < dsTabSql.size(); i++) {
					//---delete by songjie 2013.01.30 BUG 5333 QDV4海富通2012年08月20日01_B start---//
//					//------ add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 
//					if ("tb_data_subtrade".equalsIgnoreCase(pret.getTargetTabCode())) {
//						
//						selSql = " select * from " + targetTab + " a "
//							+ " where exists (select * from ("
//							+ dsTabSql.get(i).toString().split("\f\f")[1]
//							+ ") b " + " where " + whereSql + ")"; 
//						
//						rsTemp = dbl.openResultSet(selSql);
//						
//						if (rsTemp.next()){
//							if (dbl.yssTableExist("Temp_settleSubTrade")){
//								//add by songjie 2011.12.26 BUG 3486 QDV4嘉实2011年12月21日01_B
//								dbl.executeSql("drop table Temp_settleSubTrade"); //删除表
//								//---delete by songjie 2011.12.26 BUG 3486 QDV4嘉实2011年12月21日01_B start---//
////								delSql = " insert into Temp_settleSubTrade (" + selSql + ")";
////								dbl.executeSql(delSql); //插入数据
//								//---delete by songjie 2011.12.26 BUG 3486 QDV4嘉实2011年12月21日01_B end---//
//							}
//							
////							else{//delete by songjie 2011.12.26 BUG 3486 QDV4嘉实2011年12月21日01_B
//								delSql = " create table Temp_settleSubTrade as (" + selSql + ")";
//								dbl.executeSql(delSql); //创建表
////							}//delete by songjie 2011.12.26 BUG 3486 QDV4嘉实2011年12月21日01_B
//							
//						}
//						dbl.closeResultSetFinal(rsTemp);
//					}
//					//------------------------ BUG #1511 ------------------------//
					//---delete by songjie 2013.01.30 BUG 5333 QDV4海富通2012年08月20日01_B end---//
					
					//---add by songjie 2011.09.08 BUG 2599 QDV4海富通2011年08月30日01_B start---//
					if ("tb_data_subtrade".equalsIgnoreCase(pret.getTargetTabCode())) {
						delSql = " delete from " + pub.yssGetTableName("Tb_Cash_subTransfer") + 
						" where FNum in(select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
						" where FTradeNum in( select FNum from " + targetTab + " a where exists (select * from (" + 
						dsTabSql.get(i).toString().split("\f\f")[1] + ") b where " + whereSql + " ))) ";
						
						dbl.executeSql(delSql);
				    
						delSql = " delete from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
						" where FTradeNum in( select FNum from " + targetTab + 
						" a where exists (select * from (" + 
						dsTabSql.get(i).toString().split("\f\f")[1] + ") b where " + 
						whereSql + " ))";
						
						dbl.executeSql(delSql);
						
						//---add by songjie 2012.09.10 BUG 5333 QDV4海富通2012年08月20日01_B start---//
						//删除 资金调拨子数据中  交易编号(FTradeNum)、证券代码(FSecurityCode)不为空 
						//且 交易编号(FTradeNum) 不包含在 已审核的 交易数据的成交编号 中的数据
						delSql = " delete from " + pub.yssGetTableName("Tb_Cash_subTransfer") + 
						" where FNum in (select FNum from " + 
						pub.yssGetTableName("Tb_Cash_Transfer") +
						" where FTradeNum not in (select FNum from " + 
						pub.yssGetTableName("Tb_Data_SubTrade") + 
						" b where FCheckState = 1) and FSecurityCode is not null " +
						//edit by songjie 2013.01.30 BUG 5333 QDV4海富通2012年08月20日01_B  添加 调拨类型为 成本 的删除条件
						" and FTradeNum is not null and FCheckState = 1 and FTsfTypeCode = '05' " + 
						//add by songjie 2013.01.24 添加业务日期 做为 删除条件
						" and FTransDate between " + dbl.sqlDate(this.beginDate) + " and " + dbl.sqlDate(this.endDate) + ") ";
						
						dbl.executeSql(delSql);
						
						//删除 资金调拨数据中  交易编号(FTradeNum)、证券代码(FSecurityCode)不为空 
						//且 交易编号(FTradeNum) 不包含在 已审核的 交易数据的成交编号 中的数据
						delSql = " delete from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
						" where FTradeNum not in (select FNum from " + 
						pub.yssGetTableName("Tb_Data_SubTrade") + 
						" where FCheckState = 1) and FSecurityCode is not null " + 
						//edit by songjie 2013.01.30 BUG 5333 QDV4海富通2012年08月20日01_B 添加 调拨类型为 成本 的删除条件
						" and FTradeNum is not null and FCheckState = 1 and FTsfTypeCode = '05' " +
						//add by songjie 2013.01.24 添加业务日期 做为 删除条件
						" and FTransDate between " + dbl.sqlDate(this.beginDate) + " and " + dbl.sqlDate(this.endDate);
						
						dbl.executeSql(delSql);
						//---add by songjie 2012.09.10 BUG 5333 QDV4海富通2012年08月20日01_B end---//
					}
					//---add by songjie 2011.09.08 BUG 2599 QDV4海富通2011年08月30日01_B end---//
					
					delSql = " delete from " + targetTab + " a "
							+ " where exists (select * from ("
							+ dsTabSql.get(i).toString().split("\f\f")[1]
							+ ") b " + " where " + whereSql + ")";
					delSql = this.buildDsSql(delSql);//add by huangqirong 2012-05-08 story #2565 之前sql未 解析
					dbl.executeSql(delSql);
				}
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new YssException("删除目标表数据出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);//------ add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 
			dbl.closeResultSetFinal(rsTemp);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 *  add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 
	 *  更新资金调拨关联编号
	 * @throws YssException
	 */
	private void updateSubTradeNum() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		ResultSet rsTemp = null;
		HashMap hashNum = new HashMap();
		
		try {
			if (dbl.yssTableExist("Temp_settleSubTrade")) {
				
				strSql = " select a.* from " + pub.yssGetTableName("TB_data_subTrade") 
						+ " a where exists (select * from Temp_settleSubTrade b  "
						+ " where a.FBARGAINDATE = b.FBARGAINDATE and a.FSECURITYCODE = b.FSECURITYCODE"
						//------ modify by wangzuochun 2011.05.24 BUG1856系统提示“更新交易数据编号出错”，此错误提示只会出现一次
						+ " and a.FBROKERCODE = b.FBROKERCODE and a.FPortCode = b.FPORTCODE) order by a.fnum ";
		
				rs = dbl.openResultSet(strSql);
				
				while(rs.next()){
					//------ modify by wangzuochun 2011.05.24 BUG1856系统提示“更新交易数据编号出错”，此错误提示只会出现一次
					strSql = " select * from Temp_settleSubTrade order by fnum";
					
					rsTemp = dbl.openResultSet(strSql);
					
					while(rsTemp.next()){
						if (rs.getString("FSECURITYCODE").equals(rsTemp.getString("FSECURITYCODE"))
								&& rs.getString("FBROKERCODE").equals(rsTemp.getString("FBROKERCODE"))
								&& rs.getString("FPortCode").equals(rsTemp.getString("FPortCode"))
								&& rs.getDate("FBARGAINDATE").equals(rsTemp.getDate("FBARGAINDATE"))
								&& rs.getString("FNum").substring(9,10).equals(rsTemp.getString("FNum").substring(9,10))){
						
							hashNum.put(rs.getString("FNum"), rsTemp.getString("FNum"));
							strSql = " delete from Temp_settleSubTrade where FNum = " + dbl.sqlString(rsTemp.getString("FNum"));
							dbl.executeSql(strSql);
							break;
						}
					}
					dbl.closeResultSetFinal(rsTemp);
				}
				dbl.closeResultSetFinal(rs);
				
				
			    Iterator iterator = hashNum.keySet().iterator(); //循环取出当前对象
				while (iterator.hasNext()) {
					
					String strNum = (String)iterator.next();
					strSql = "update " + pub.yssGetTableName("TB_data_subTrade") 
						+ " set FNum = " + dbl.sqlString((String)hashNum.get(strNum))
		                + " where FNum = " + dbl.sqlString(strNum);
					
					dbl.executeSql(strSql);
				}
							
				strSql = " drop table Temp_settleSubTrade ";
				dbl.executeSql(strSql);
			}
			
		} catch (Exception e) {
			throw new YssException("更新交易数据编号出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsTemp);
		}
	}
	
	
	
	/**
	 * MS01337 add by zhangfa 2010.07.06 QDV4海富通2010年06月22日02_AB
	 * 
	 * @方法名：delMTgtTabData
	 * @参数：
	 * @返回类型：void
	 * @说明：多组合群共享时,删除目标表数据
	 */
	private void delMTgtTabData(DaoPretreatBean pret,String number) throws YssException {
		// 实现方法：取目标表删除条件设置，并且按照排序号order，并结合pret中的目标表，生成类似以下SQL语句
		// delete from tb_001_data_subtrade a where exists
		// (select * from tb_001_data_subtrade_blm b
		// where a.FPortCode = b.FPortCode and
		// a.FBrokerCode = b.FBrokerCode and a.FInvmgrCode = b.FInvmgrCode)
		String strSql = "";
		ResultSet rs = null;
		String delSql = ""; // 删除目标表的sql语句
		ResultSet rsTemp = null; //------ add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 
		String selSql = ""; //------ add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 
		List dsTabSql = null; // 原表
		String targetTab = ""; // 目标表
		StringBuffer whereSqlBuf = new StringBuffer();
		String whereSql = "";
		Connection conn = null;
		boolean bTrans = true;
		int iTabType = 0;
		try {
			dictBean = new DataDictBean(pub);
			iTabType = dictBean.getTabType(tableName);/**shashijie 2011.06.07 STORY #970*/
			if (iTabType == 1||iTabType == 2) {//add by guolongchao 20111228 STORY 1499
				targetTab = pret.getTargetTabCode();
			} else if (iTabType == 0) {
				// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB 蒋世超 添加 2009.12.24
				// --------------
				if (pret.getTargetTabCode().toUpperCase().startsWith("A")) {
					// 财务报表向后台传递年份参数
					SetTabYearPre();
				}
				// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB end
				// ---------------------------------
				targetTab = pub.yssGetTableName(pret.getTargetTabCode());
			} // 根据表名判断表是否是临时表或是系统表 by liyu
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			dsTabSql = getDsSqlList(pret.getDataSource());
			
			pub.setPrefixTB(pub.getAssetGroupCode());
			strSql = " select * from "
					+ pub.yssGetTableName("Tb_Dao_TgtTabCond")
					+ " where FDPDsCode=" + dbl.sqlString(pret.getDPDsCode())
					+ " and FCheckState=1" + " order by FOrderIndex ";
			rs = dbl.openResultSet(strSql);
			pub.setPrefixTB(number);
			
			while (rs.next()) {
				whereSqlBuf.append("a.").append(rs.getString("FTargetField"))
						.append("=").append("b.").append(
								rs.getString("FDsField")).append(" and ");
			}
			if (whereSqlBuf.length() > 5) {
				whereSql = whereSqlBuf.toString().substring(0,
						whereSqlBuf.toString().length() - 5);

			}
			if (whereSql.length() > 0) {
				// dsTabSql里面含有类似select '' as FSECURITYCODE 的代码，
				// 当在whereSql中用到a.FSECURITYCODE=b.FSECURITYCODE时报错
				// delSql = " delete from " + pub.yssGetTableName(targetTab) +
				// " a " +// by liyu
				for (int i = 0; i < dsTabSql.size(); i++) {
					//------ add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 
					if ("tb_data_subtrade".equalsIgnoreCase(pret.getTargetTabCode())) {
						//---delete by songjie 2013.01.30 BUG 5333 QDV4海富通2012年08月20日01_B start---//
//						selSql = " select * from " + targetTab + " a "
//							+ " where exists (select * from ("
//							+ dsTabSql.get(i).toString().split("\f\f")[1]
//							+ ") b " + " where " + whereSql + ")"; 
//						
//						rsTemp = dbl.openResultSet(selSql);
//						
//						if (rsTemp.next()){
//							if (dbl.yssTableExist("Temp_settleSubTrade")){
//								//add by songjie 2011.12.26 BUG 3486 QDV4嘉实2011年12月21日01_B
//								dbl.executeSql("drop table Temp_settleSubTrade"); //删除表
//								//---delete by songjie 2011.12.26 BUG 3486 QDV4嘉实2011年12月21日01_B start---//
////								delSql = " insert into Temp_settleSubTrade (" + selSql + ")";
////								dbl.executeSql(delSql); //插入数据
//								//---delete by songjie 2011.12.26 BUG 3486 QDV4嘉实2011年12月21日01_B end---//
//							}
////							else{//delete by songjie 2011.12.26 BUG 3486 QDV4嘉实2011年12月21日01_B
//								delSql = " create table Temp_settleSubTrade as (" + selSql + ")";
//								dbl.executeSql(delSql); //创建表
////							}//delete by songjie 2011.12.26 BUG 3486 QDV4嘉实2011年12月21日01_B
//						}
//						dbl.closeResultSetFinal(rsTemp);
						//---delete by songjie 2013.01.30 BUG 5333 QDV4海富通2012年08月20日01_B end---//
						
						//---add by songjie 2012.09.10 BUG 5333 QDV4海富通2012年08月20日01_B start---//
						delSql = " delete from " + pub.yssGetTableName("Tb_Cash_subTransfer") + 
						" where FNum in(select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
						" where FTradeNum in( select FNum from " + targetTab + " a where exists (select * from (" + 
						dsTabSql.get(i).toString().split("\f\f")[1] + ") b where " + whereSql + " ))) ";
						
						dbl.executeSql(delSql);
				    
						delSql = " delete from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
						" where FTradeNum in( select FNum from " + targetTab + 
						" a where exists (select * from (" + 
						dsTabSql.get(i).toString().split("\f\f")[1] + ") b where " + 
						whereSql + " ))";
						
						dbl.executeSql(delSql);
						
						//删除 资金调拨子数据中  交易编号(FTradeNum)、证券代码(FSecurityCode)不为空 
						//且 交易编号(FTradeNum) 不包含在 已审核的 交易数据的成交编号 中的数据
						delSql = " delete from " + pub.yssGetTableName("Tb_Cash_subTransfer") + 
						" where FNum in (select FNum from " + 
						pub.yssGetTableName("Tb_Cash_Transfer") +
						" where FTradeNum not in (select FNum from " + 
						pub.yssGetTableName("Tb_Data_SubTrade") + 
						" b where FCheckState = 1) and FSecurityCode is not null " +
						//edit by songjie 2013.01.30 BUG 5333 QDV4海富通2012年08月20日01_B  添加 调拨类型为 成本 的删除条件
						" and FTradeNum is not null and FCheckState = 1 and FTsfTypeCode = '05' " + 
						//add by songjie 2013.01.24 添加业务日期 做为 删除条件
						" and FTransDate between " + dbl.sqlDate(this.beginDate) + " and " + dbl.sqlDate(this.endDate) + ") ";
						
						dbl.executeSql(delSql);
						
						//删除 资金调拨数据中  交易编号(FTradeNum)、证券代码(FSecurityCode)不为空 
						//且 交易编号(FTradeNum) 不包含在 已审核的 交易数据的成交编号 中的数据
						delSql = " delete from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
						" where FTradeNum not in (select FNum from " + 
						pub.yssGetTableName("Tb_Data_SubTrade") + 
						" where FCheckState = 1) and FSecurityCode is not null " + 
						//edit by songjie 2013.01.30 BUG 5333 QDV4海富通2012年08月20日01_B 添加 调拨类型为 成本 的删除条件
						" and FTradeNum is not null and FCheckState = 1 and FTsfTypeCode = '05' " +
						//add by songjie 2013.01.24 添加业务日期 做为 删除条件
						" and FTransDate between " + dbl.sqlDate(this.beginDate) + " and " + dbl.sqlDate(this.endDate);
						
						dbl.executeSql(delSql);
						//---add by songjie 2012.09.10 BUG 5333 QDV4海富通2012年08月20日01_B end---//
					}
					//------------------------ BUG #1511 ------------------------//
					
					delSql = " delete from " + targetTab + " a "
							+ " where exists (select * from ("
							+ dsTabSql.get(i).toString().split("\f\f")[1]
							+ ") b " + " where " + whereSql + ")";
					dbl.executeSql(delSql);
				}
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new YssException("删除目标表数据出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsTemp); //------ add by wangzuochun 2011.03.19 BUG #1511 资金调拨记录发生错误 
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 根据表名从数据字典设置中获取系统表描述
	 * //add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
	 * @param tbName
	 * @return
	 * @throws YssException
	 */
	private String getDicTbDesc(String tbName) throws YssException{
		String strTbDesc="";
		ResultSet rs =null;
		try{
			String strSql="select distinct (a.FTabName), a.FTableDesc, a.FTableType"
							+" from Tb_Fun_DataDict a"
							+" where  a.ftabname ="+dbl.sqlString(tbName)
							+" order by FTabName";
			rs = dbl.openResultSet(strSql);
			
			while(rs.next()){
				strTbDesc = rs.getString("FTableDesc");
			}
		}catch(Exception ex){
			throw new YssException("根据表名从数据字典设置中获取系统表描述出错!");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return strTbDesc;
	}
	
	/**
	 * 根据预处理的数据源插入数据到目标表
	 * 
	 * @param pret
	 * @throws YssException
	 */
	private String insertTgtTabData(DaoPretreatBean pret) throws YssException {
		Connection conn = dbl.loadConnection();
		PreparedStatement pst = null;
		String strInfo ="";//add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
		String strSql = "";
		String nameAndNum="";//目标表名和记录数 //add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
		ResultSet rsDs = null; // 数据源的记录集
		ResultSet rs = null; // 该记录集用来打开接口预处理字段设置表
		String strPretSql = "";
		IOperValue operValue = null;
		HashMap hmFieldType = null; // 字段的字段名:字段的类型
		int iPstOrder = 1; // pst的编号
		Object sData = ""; // 通过函数获取的数据处理
		int tmpNum = 1; // 暂时为了调试用的
		String sDsFields = ""; // 通过spring 的方式，去调用某个涵数时，
		// 需要从数据源中取的一些字段做为参数用
		String[] arrDsFields = null;
		ArrayList alDsFieldValue = null; // 通过spring调用的方式，把要传入的参数放入ArrayList中
		boolean bTrans = true;
		int iTabType = 0;
		String sTabName = "";

		List sqlList = null; // 用于存储解析数据源获取到得sql组 sunkey@Modify 20091121

		try {
			dictBean = new DataDictBean(pub);
			iTabType = dictBean.getTabType(tableName);/**shashijie 2011.06.07 STORY #970*/
			if (iTabType == 0) {
				// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB 蒋世超 添加 2009.12.24
				// --------------
				if (pret.getTargetTabCode().toUpperCase().startsWith("A")) {
					// 财务报表向后台传递年份参数
					SetTabYearPre();
				}
				// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB end
				// ---------------------------------
				sTabName = pub.yssGetTableName(pret.getTargetTabCode());
			} else if (iTabType == 1||iTabType == 2) {//add by guolongchao 20111228 STORY 1499
				sTabName = pret.getTargetTabCode();
			} // 判断表是临时表还是系统表 by liyu 080324
			conn.setAutoCommit(false); // chenyibo 20071002
			strSql = buildInsertTgtSql(pret); // 生成插入到目标表的sql语句
			pst = conn.prepareStatement(strSql);
			// 获取目标表的字段类型
			hmFieldType = dbFun.getFieldsType(sTabName); // by liyu 080324

			// 根据预处理代码打开接口预处理字段设置，并按照顺序号 order by，
			// 用可滚动的游标打开
			strPretSql = " select * " + " from "
					+ pub.yssGetTableName("Tb_Dao_PretreatField")
					+ " where FDPDsCode=" + dbl.sqlString(pret.getDPDsCode())
					+ " and FCheckState=1" + " order by FOrderIndex ";
			rs = dbl.openResultSet_antReadonly(strPretSql);

			// 通过预处理中的数据源获得一个sql语句集合，如果数据源中不包含参数<PP>则一次性处理所有组合，否则将按组合逐个处理
			sqlList = getDsSqlList(pret.getDataSource());

			// 循环处理每个组合群产生的数据源
			for (int j = 0; j < sqlList.size(); j++) {
				String[] portSql = sqlList.get(j).toString().split("\f\f");
				portCode = portSql[0];
				strSql = portSql[1];
				rsDs = dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE); // 数据源的记录集
				
				//判断是否要返回 add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
				if ("true".equals(pret.getShowImpNum()) && j==0){
					
					//获取结果集数量
					rsDs.last();
					/**shashijie 2012-10-24 STORY 3109 支持跨日期段导入数据,提示也得加上日期 */
					strInfo = "~~n~~导入【"+
					"日期:"+YssFun.formatDate(this.dealDate) + "  接口:"+//处理日期
					getDicTbDesc(pret.getTargetTabCode())+"】表中成功导入"+rsDs.getRow()+"条!"+"\r\n    ";
					/**end shashijie 2012-10-24 STORY */
					rsDs.beforeFirst();
				
				}
			//end by lidaolong
				
				while (rsDs.next()) { // 循环数据源的记录集
					tmpNum = tmpNum + 1; // 为了调试用
					rs.beforeFirst();
					iPstOrder = 1;
					while (rs.next()) {
						if (rs.getInt("FPretType") == 0) { // 数据源获取
							Object objRes = "";
							// 这里增加功能：导入预处理 数据源获取时多个字段值往一个字段里插时，中间用逗号分隔 by leeyu
							// 20091111
							if (rs.getString("FDsField").indexOf(",") > 1) {
								String[] arrField = rs.getString("FDsField")
										.split(",");
								for (int i = 0; i < arrField.length; i++) {
									objRes = objRes
											+ (rsDs.getObject(arrField[i]) + ",");
								}
								if (String.valueOf(objRes).endsWith(",")) {
									objRes = String.valueOf(objRes)
											.substring(
													0,
													String.valueOf(objRes)
															.length() - 1);
								}
							} else {
								objRes = rsDs.getObject(rs
										.getString("FDsField"));
							}
							// 直接通过rsDs记录集和数据源字段取数,并做插入操作
							// setPretPstValue(pst, iPstOrder,
							// hmFieldType,
							// rsDs.getObject(rs.getString("FDsField")),
							// rs.getString("FTargetField"));
							setPretPstValue(pst, iPstOrder, hmFieldType,
									objRes, rs.getString("FTargetField"));
							// by leeyu 20091111
						} else if (rs.getInt("FPretType") == 1) { // 函数获取(通过spring的方式)
							alDsFieldValue = new ArrayList();
							SpringInvokeBean springInvoke = new SpringInvokeBean();
							springInvoke.setYssPub(pub);
							springInvoke.setSICode(rs.getString("FSICode")); // 设置Spring的调用代码
							springInvoke.getSetting();
							operValue = (IOperValue) pub.getDataInterfaceCtx()
									.getBean(springInvoke.getBeanID()); // 通过beanId得到对象

							// 得到做为参数用的字段,用","分割 但是在配置的时候这里的参数要根据
							// 具体涵数中的参数的顺序来进行配置
							// springInvoke.getParams()得到spring的参数如:calFee,getCashAcc等
							sDsFields = rs.getString("FDsField");

							arrDsFields = sDsFields.split(",");
							for (int i = 0; i < arrDsFields.length; i++) {
								alDsFieldValue.add(rsDs
										.getObject(arrDsFields[i]));
							}
							if (rs.getString("FTargetField").equalsIgnoreCase(
									"FNum")) {
								alDsFieldValue.add(this); // 20071114 chenyibo
															// 处理交易编号的问题
							}
							alDsFieldValue.add(beginDate); // 将起始日期添加到list中,by
															// liyu 080120
							alDsFieldValue.add(endDate); // 将终止日期添加到list中,by
															// liyu 080120
							alDsFieldValue.add(portCode); // 将组合集添加到list中,by
															// liyu 080120

							// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
							prepFunBean.setObj(alDsFieldValue);
							operValue.init(prepFunBean);
							operValue.setYssPub(pub);
							sData = operValue.getTypeValue(springInvoke
									.getParams());
							iPstOrder = setFunPstValue(pst, iPstOrder,
									hmFieldType, sData, rs
											.getString("FTargetField"));
						}
						iPstOrder++;
					}
					// 判断是否是a开头的表,另加上判断表是否为临时表,从数据字典里取的表类型 liyu 1218
					if (!pret.getTargetTabCode().toLowerCase().startsWith("a")
							&& iTabType == 0) {
						setPstCommonValue(pst, iPstOrder);
					}
					pst.executeUpdate();
				}
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true); // chenyibo 20071002
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rsDs, rs);
			dbl.endTransFinal(conn, bTrans);			
		}
		return strInfo; //add by lidaolong 20110411 #813  QDV4华泰柏瑞2011年3月16日01_A 
	}
	/**
	 * MS01337 add by zhangfa 2010.07.06 QDV4海富通2010年06月22日02_AB
	 * @方法名：insertMTgtTabData
	 * @参数：
	 * @返回类型：void
	 * @说明：多组合群共享时,数据源插入数据到目标表
	 */
	private void  insertMTgtTabData(DaoPretreatBean pret,String groupNumber ) throws YssException {
		Connection conn = dbl.loadConnection();
		PreparedStatement pst = null; 
		String strSql = "";
		ResultSet rsDs = null; // 数据源的记录集
		ResultSet rs = null; // 该记录集用来打开接口预处理字段设置表
		String strPretSql = "";
		IOperValue operValue = null;
		HashMap hmFieldType = null; // 字段的字段名:字段的类型
		int iPstOrder = 1; // pst的编号
		Object sData = ""; // 通过函数获取的数据处理
		int tmpNum = 1; // 暂时为了调试用的
		String sDsFields = ""; // 通过spring 的方式，去调用某个涵数时，
		// 需要从数据源中取的一些字段做为参数用
		String[] arrDsFields = null;
		ArrayList alDsFieldValue = null; // 通过spring调用的方式，把要传入的参数放入ArrayList中
		boolean bTrans = true;
		int iTabType = 0;
		String sTabName = "";

		List sqlList = null; // 用于存储解析数据源获取到得sql组 sunkey@Modify 20091121

		try {
			dictBean = new DataDictBean(pub);
			iTabType = dictBean.getTabType(tableName);/**shashijie 2011.06.07 STORY #970*/
			if (iTabType == 0) {
				// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB 蒋世超 添加 2009.12.24
				// --------------
				if (pret.getTargetTabCode().toUpperCase().startsWith("A")) {
					// 财务报表向后台传递年份参数
					SetTabYearPre();
				}
				// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB end
				// ---------------------------------
				sTabName = pub.yssGetTableName(pret.getTargetTabCode());
			} else if (iTabType == 1||iTabType == 2) {//add by guolongchao 20111228 STORY 1499
				sTabName = pret.getTargetTabCode();
			} // 判断表是临时表还是系统表 by liyu 080324
			conn.setAutoCommit(false); // chenyibo 20071002
			
			//pub.setPrefixTB(pub.getAssetGroupCode());
			strSql = buildMInsertTgtSql(pret,groupNumber); // 生成插入到目标表的sql语句
			pst = conn.prepareStatement(strSql);
			// 获取目标表的字段类型
			hmFieldType = dbFun.getFieldsType(sTabName); // by liyu 080324

			// 根据预处理代码打开接口预处理字段设置，并按照顺序号 order by，
			// 用可滚动的游标打开
			pub.setPrefixTB(pub.getAssetGroupCode());
			
			strPretSql = " select * " + " from "
					+ pub.yssGetTableName("Tb_Dao_PretreatField")
					+ " where FDPDsCode=" + dbl.sqlString(pret.getDPDsCode())
					+ " and FCheckState=1" + " order by FOrderIndex ";
			rs = dbl.openResultSet_antReadonly(strPretSql);
			
            pub.setPrefixTB(groupNumber);
			// 通过预处理中的数据源获得一个sql语句集合，如果数据源中不包含参数<PP>则一次性处理所有组合，否则将按组合逐个处理
			sqlList = getDsSqlList(pret.getDataSource());

			// 循环处理每个组合群产生的数据源
			for (int j = 0; j < sqlList.size(); j++) {
				String[] portSql = sqlList.get(j).toString().split("\f\f");
				portCode = portSql[0];
				strSql = portSql[1];
				rsDs = dbl.openResultSet(strSql); // 数据源的记录集
					
				while (rsDs.next()) { // 循环数据源的记录集
					tmpNum = tmpNum + 1; // 为了调试用
					rs.beforeFirst();
					iPstOrder = 1;
					while (rs.next()) {
						if (rs.getInt("FPretType") == 0) { // 数据源获取
							Object objRes = "";
							// 这里增加功能：导入预处理 数据源获取时多个字段值往一个字段里插时，中间用逗号分隔 by leeyu
							// 20091111
							if (rs.getString("FDsField").indexOf(",") > 1) {
								String[] arrField = rs.getString("FDsField")
										.split(",");
								for (int i = 0; i < arrField.length; i++) {
									objRes = objRes
											+ (rsDs.getObject(arrField[i]) + ",");
								}
								if (String.valueOf(objRes).endsWith(",")) {
									objRes = String.valueOf(objRes)
											.substring(
													0,
													String.valueOf(objRes)
															.length() - 1);
								}
							} else {
								objRes = rsDs.getObject(rs
										.getString("FDsField"));
							}
							// 直接通过rsDs记录集和数据源字段取数,并做插入操作
							// setPretPstValue(pst, iPstOrder,
							// hmFieldType,
							// rsDs.getObject(rs.getString("FDsField")),
							// rs.getString("FTargetField"));
							setPretPstValue(pst, iPstOrder, hmFieldType,
									objRes, rs.getString("FTargetField"));
							// by leeyu 20091111
						} else if (rs.getInt("FPretType") == 1) { // 函数获取(通过spring的方式)
							alDsFieldValue = new ArrayList();
							SpringInvokeBean springInvoke = new SpringInvokeBean();
							springInvoke.setYssPub(pub);
							springInvoke.setSICode(rs.getString("FSICode")); // 设置Spring的调用代码
							springInvoke.getSetting();
							operValue = (IOperValue) pub.getDataInterfaceCtx()
									.getBean(springInvoke.getBeanID()); // 通过beanId得到对象

							// 得到做为参数用的字段,用","分割 但是在配置的时候这里的参数要根据
							// 具体涵数中的参数的顺序来进行配置
							// springInvoke.getParams()得到spring的参数如:calFee,getCashAcc等
							sDsFields = rs.getString("FDsField");

							arrDsFields = sDsFields.split(",");
							for (int i = 0; i < arrDsFields.length; i++) {
								alDsFieldValue.add(rsDs
										.getObject(arrDsFields[i]));
							}
							if (rs.getString("FTargetField").equalsIgnoreCase(
									"FNum")) {
								alDsFieldValue.add(this); // 20071114 chenyibo
															// 处理交易编号的问题
							}
							alDsFieldValue.add(beginDate); // 将起始日期添加到list中,by
															// liyu 080120
							alDsFieldValue.add(endDate); // 将终止日期添加到list中,by
															// liyu 080120
							alDsFieldValue.add(portCode); // 将组合集添加到list中,by
															// liyu 080120

							// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
							prepFunBean.setObj(alDsFieldValue);
							operValue.init(prepFunBean);
							operValue.setYssPub(pub);
							sData = operValue.getTypeValue(springInvoke
									.getParams());
							iPstOrder = setFunPstValue(pst, iPstOrder,
									hmFieldType, sData, rs
											.getString("FTargetField"));
						}
						iPstOrder++;
					}
					// 判断是否是a开头的表,另加上判断表是否为临时表,从数据字典里取的表类型 liyu 1218
					if (!pret.getTargetTabCode().toLowerCase().startsWith("a")
							&& iTabType == 0) {
						setPstCommonValue(pst, iPstOrder);
					}
					pst.executeUpdate();
				}
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true); // chenyibo 20071002
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rsDs, rs);
			dbl.endTransFinal(conn, bTrans);
		} 
	}

	// --------------------------------------------------------------------
	// 生成插入目标表的SQL语句
	private String buildInsertTgtSql(DaoPretreatBean pret) throws YssException {
		// 实现方法：取接口预处理字段设置中的目标表字段，并且按照排序号order，并结合pret中的目标表。
		// 把目标表字段组合成FSecurityCode,FPortCode,FBrokerCode字符，再生成和目标表字段同样数量的"?",作为参数
		String sResult = "";
		ResultSet rs = null;
		String strSql = "";
		String targetTab = ""; // 目标表
		String targetTabField = ""; // 目标表字段
		String targetTabFieldParam = ""; // 目标表字段参数 如:?,?,?
		StringBuffer targetTabFieldBuf = new StringBuffer();
		int iTabType = 0;
		try {
			dictBean = new DataDictBean(pub);
			iTabType = dictBean.getTabType(tableName);/**shashijie 2011.06.07 STORY #970*/
			if (iTabType == 0) {
				targetTab = pub.yssGetTableName(pret.getTargetTabCode());
			} else if (iTabType == 1||iTabType == 2) {//add by guolongchao 20111228 STORY 1499
				targetTab = pret.getTargetTabCode();
			} // 增加对目标表类型的判断与处理. by liyu 080324
			// targetTab = pub.yssGetTableName(pret.getTargetTabCode()); //得到目标表
			strSql = " select FDsField,FTargetField from "
					+ pub.yssGetTableName("Tb_Dao_PretreatField")
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
	 * MS01337 add by zhangfa 2010.07.06 QDV4海富通2010年06月22日02_AB
	 * @方法名：buildMInsertTgtSql
	 * @参数：
	 * @返回类型：String
	 * @说明：多组合群共享时,生成插入目标表的SQL语句
	 */
	private String buildMInsertTgtSql(DaoPretreatBean pret,String number) throws YssException {
		// 实现方法：取接口预处理字段设置中的目标表字段，并且按照排序号order，并结合pret中的目标表。
		// 把目标表字段组合成FSecurityCode,FPortCode,FBrokerCode字符，再生成和目标表字段同样数量的"?",作为参数
		String sResult = "";
		ResultSet rs = null;
		String strSql = "";
		String targetTab = ""; // 目标表
		String targetTabField = ""; // 目标表字段
		String targetTabFieldParam = ""; // 目标表字段参数 如:?,?,?
		StringBuffer targetTabFieldBuf = new StringBuffer();
		int iTabType = 0;
		try {
			dictBean = new DataDictBean(pub);
			iTabType = dictBean.getTabType(tableName);/**shashijie 2011.06.07 STORY #970*/
			if (iTabType == 0) {
				targetTab = pub.yssGetTableName(pret.getTargetTabCode());
			} else if (iTabType == 1||iTabType == 2) {//add by guolongchao 20111228 STORY 1499
				targetTab = pret.getTargetTabCode();
			} // 增加对目标表类型的判断与处理. by liyu 080324
			// targetTab = pub.yssGetTableName(pret.getTargetTabCode()); //得到目标表
			pub.setPrefixTB(pub.getAssetGroupCode());
			strSql = " select FDsField,FTargetField from "
					+ pub.yssGetTableName("Tb_Dao_PretreatField")
					+ " where FDPDsCode=" + dbl.sqlString(pret.getDPDsCode())
					+ " and FCheckState=1" + " order by FOrderIndex";
			rs = dbl.openResultSet(strSql);
			pub.setPrefixTB(number);
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
	 * 将自定义的数据源解析成标准的sql语句
	 * 
	 * @param sDs
	 * @return
	 * @throws YssException
	 */
	public String buildDsSql(String sDs) throws YssException {
		YssFinance cw = null;

		// 节假日处理
		sDs = pretSqlIns(sDs, null, "");

		sDs = sDs.replaceAll("<S1>", "<S>");
		sDs = sDs.replaceAll("<S2>", "<S>");
		sDs = sDs.replaceAll("<S>", "(" + operSql.sqlCodes(portCodes) + ")");
		sDs = sDs.replaceAll("<D>", "<D1>");
		sDs = sDs.replaceAll("<D1>", dbl.sqlDate(beginDate));
		sDs = sDs.replaceAll("<D2>", dbl.sqlDate(endDate));
		// add by leeyu 080729
		if (sDs.indexOf("<U>") > 0) {
			sDs = sDs.replaceAll("<U>", pub.getUserCode());
		} else if (sDs.indexOf("< U >") > 0) {
			sDs = sDs.replaceAll("< U >", pub.getUserCode());
		}

		if (sDs.indexOf("<Year>") > 0) { // 把"<Year>"的标识替换成结束日期的年份
			sDs = sDs.replaceAll("<Year>", YssFun.formatDate(this.beginDate,
					"yyyy"));
		} else if (sDs.indexOf("< Year >") > 0) { // add by leeyu 080729
			sDs = sDs.replaceAll("< Year >", YssFun.formatDate(this.beginDate,
					"yyyy"));
		}
		if (sDs.indexOf("<Set>") > 0) { // 把"<Year>"的标识替换成套帐号
			cw = new YssFinance();
			cw.setYssPub(pub);
			sDs = sDs.replaceAll("<Set>", cw.getCWSetCode(portCodes));
		} else if (sDs.indexOf("< Set >") > 0) { // add by leeyu 080729
			cw = new YssFinance();
			cw.setYssPub(pub);
			sDs = sDs.replaceAll("< Set >", cw.getCWSetCode(portCodes));
		}
		if (sDs.indexOf("<Group>") > 0) { // 把"<Group>"的标识替换成群
			// add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
			// 若为跨组合群操作

			if (pub.getPrefixTB() != null
					&& !pub.getPrefixTB().equals(pub.getAssetGroupCode())) {
				// 则<Group>替换为跨组合群操作的已选组合群代码
				sDs = sDs.replaceAll("<Group>", pub.getPrefixTB());
			} else {
				// add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
				sDs = sDs.replaceAll("<Group>", pub.getAssetGroupCode());
				// add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
			}
			// add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
		} else if (sDs.indexOf("< Group >") > 0) {
			// add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
			// 若为跨组合群操作
			if (pub.getPrefixTB() != null
					&& !pub.getPrefixTB().equals(pub.getAssetGroupCode())) {
				// 则<Group>替换为跨组合群操作的已选组合群代码
				sDs = sDs.replaceAll("< Group >", pub.getPrefixTB());
			} else {
				// add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
				sDs = sDs.replaceAll("< Group >", pub.getAssetGroupCode());
				// add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
			}
			// add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
		}
		// 组合代码 MS00817:QDV4工银2009年11月17日01_A sunkey@Add
		if (sDs.indexOf("<Port>") != -1) {
			sDs = sDs.replaceAll("<Port>", dbl.sqlString(portCode));
		} else if (sDs.indexOf("< Port >") != -1) {
			sDs = sDs.replaceAll("< Port >", dbl.sqlString(portCode));
		}

		// 资产代码 MS00817:QDV4工银2009年11月17日01_A sunkey@Modify
		if (sDs.indexOf("<Asset>") != -1) {
			PortfolioBean port = new PortfolioBean();
			port.setYssPub(pub);
			port.setPortCode(portCode);
			port.getSetting();
			sDs = sDs.replaceAll("<Asset>", dbl.sqlString(port.getAssetCode()));
		}

		// xuqiji 20091014----MS00005
		// QDV4.1赢时胜（上海）2009年9月28日04_A------------------------------
		if (sDs.indexOf("<Link>") > 0) { // 把"<Link>"的标识替换成连接符
											// oralce用“||”,db2用“+”
			if (dbl.getDBType() == YssCons.DB_ORA) {
				sDs = sDs.replaceAll("<Link>", "||");
			} else {
				sDs = sDs.replaceAll("<Link>", "+");
			}
		}
		// -----------------------end-------------------------//
		sDs = sDs.replaceAll("~Base", "base");

		return sDs;
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
				if (sValue.trim().length() > 0 && !sValue.trim().equals("null")) {
					pst.setString(pstId, sValue);
				} else {
					pst.setString(pstId, " ");
				}
			} else if (fieldType.indexOf("NUMBER") > -1) {
				if (sValue.trim().length() > 0 && !sValue.equals("null")) { // 如果不为空
					if (YssFun.isNumeric(sValue)) { // 如果是double型的数据
						//pst.setDouble(pstId, YssFun.toDouble(sValue));
						//panjunfang modify 20111025 BUG2999 QDV4工银2011年10月21日01_B
						pst.setBigDecimal(pstId, new BigDecimal(
								new DecimalFormat(
										"#.##################")
										.format(YssFun
												.toDouble(sValue))));
					} else {
						pst.setInt(pstId, YssFun.toInt(sValue));
					}
				} else {
					pst.setInt(pstId, 0); // 如果是空就存入一个0
				}
			} else if (fieldType.indexOf("DATE") > -1) {
				if (sValue.trim().length() > 0 && !sValue.trim().equals("null")) { // 如果日期内容不为空
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
								&& !arrTargetTabFieldVal[i].trim().equals("null")) {
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

	/**
	 * 五个公共的字段,比如创建人,创建时间，等等
	 * 
	 * @param pst
	 *            PreparedStatement
	 * @param pstId
	 *            int
	 * @throws YssException
	 */
	public void setPstCommonValue(PreparedStatement pst, int pstId)
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

	/**
	 * 计算子字符串 chenyibo 20071102
	 * 
	 * @param str
	 *            String
	 * @param begin
	 *            int
	 * @param count
	 *            int
	 * @return String
	 * @throws YssException
	 */
	public static String getStrByByte(String str, int begin, int count)
			throws YssException {
		String reStr = "";
		byte[] temp = null;
		StringBuffer buf = new StringBuffer();

		try {
			int reInt = 0;
			byte[] tempByte = str.getBytes();
			while (begin < tempByte.length && count > reInt) {
				if (tempByte[begin] < 0) {
					temp = new byte[] { tempByte[begin], tempByte[begin + 1] };
					reStr = new String(temp, "GB2312");
					buf.append(reStr);
					begin = begin + 2;
					reInt = reInt + 2;
				} else {
					temp = new byte[] { tempByte[begin] };
					reStr = new String(temp, "GB2312");
					buf.append(reStr);
					begin = begin + 1;
					reInt = reInt + 1;
				}
			}
			reStr = buf.toString().trim();
			buf = null;
		} catch (Exception e) {
			throw new YssException("计算子字符串出错!");
		}
		return reStr + "\r\t" + begin; // 20071104 chenyibo 返回一个开始位置
	}

	/**
	 * 插入到目标表后的处理 但不是每个接口都是必须做这一步的 该方法目前无用 -- sunkey(注)
	 * 
	 * @param pret
	 *            DaoPretreatBean
	 * @throws YssException
	 */
	// private void afterInsertDeal(DaoPretreatBean pret) throws YssException {
	// CommonPretFun pretFun = new CommonPretFun();
	// ArrayList list = new ArrayList();
	// if (pret.getDsType() == 1) {
	// list.add(pret.getTargetTabCode());
	// pretFun.setYssPub(pub);
	// pretFun.init(list);
	// pretFun.getTypeValue(pret.getBeanId());
	// }
	// }

	/**
	 * chenyibo 20071124 固定数据源
	 */
	private void exeDataSource(DaoPretreatBean pret) throws YssException {
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

	public void exeParamDataSource(DaoPretreatBean pret) throws YssException {
		IOperValue operValue = null;
		String strSql = "";
		ResultSet rs = null;
		String[] arrDsFields = null;
		ResultSet rsDs = null;
		StringBuffer buf = null;
		String param = "";
		ArrayList list = new ArrayList();
		try {
			strSql = buildDsSql(pret.getDataSource()); // 生成数据源的sql语句
			rsDs = dbl.openResultSet(strSql); // 数据源的记录集
			strSql = " select * " + " from "
					+ pub.yssGetTableName("Tb_Dao_PretreatField")
					+ " where FDPDsCode=" + dbl.sqlString(pret.getDPDsCode())
					+ " and FCheckState=1" + " order by FOrderIndex ";
			rs = dbl.openResultSet_antReadonly(strSql);
			while (rs.next()) { // 加循环,因为结果集不只是一条 by ly 0129
				SpringInvokeBean springInvoke = new SpringInvokeBean();
				springInvoke.setYssPub(pub);
				springInvoke.setSICode(rs.getString("FSICode")); // 设置Spring的调用代码
				springInvoke.getSetting();
				operValue = (IOperValue) pub.getDataInterfaceCtx().getBean(
						springInvoke.getBeanID()); // 将operValue在这里实例化 by liyu
													// 080429
				while (rsDs.next()) {
					buf = new StringBuffer();
					arrDsFields = rs.getString("FDsField").split(","); // 得到做为参数用的字段,用","分割
					for (int i = 0; i < arrDsFields.length; i++) {
						buf.append(rsDs.getObject(arrDsFields[i]).toString())
								.append(",");
					}
					if (buf.toString().length() > 1) {
						param = buf.toString().substring(0,
								buf.toString().length() - 1);
					}
					list.add(param);
				}
				// // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
				prepFunBean.setObj(list);
				operValue.init(prepFunBean);
				// // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
				operValue.setYssPub(pub);
				operValue.getTypeValue(springInvoke.getParams());
			}
		} catch (Exception e) {
			throw new YssException("操作失败", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsDs);
		}
	}

	// ============MS00817:QDV4工银2009年11月17日01_A sunkey@Modify ==
	/**
	 * 根据通用参数获取组合所占的百分比，从而计算出正确的数据值
	 * 
	 * @param String
	 *            sDs 要进行转换的数据源
	 * @author sunkey add at 20091120
	 * @throws YssException
	 */
	private void getPortPercent() throws YssException {

		// 通过通用参数获取组合对应的比例
		CtlPubPara pubPara = new CtlPubPara();
		pubPara.setYssPub(pub);
		htPortPercent = pubPara.getPortPercent();

	}

	/**
	 * 按照组合的个数将数据源解析为对应个数的数据源
	 * 
	 * @param sDs
	 * @return
	 * @throws YssException
	 */
	private List getDsSqlList(String sDs) throws YssException {
		String sTmpDs = "";
		String tmpOldFun = "";
		ArrayList listPPorts = null;
		ArrayList tmpList = null;
		ArrayList listPortDs = new ArrayList();

		// 需要独立组合的情况主要是使用<PP>函数的，因为这个函数是需要根据组合获取相应比例的
		if (sDs.indexOf("<PP>") != -1 || sDs.indexOf("TOP[") != -1) {

			// <PP> port percent，通过通用参数取值 sunkey@Modify 20091120
			if (htPortPercent == null) {
				getPortPercent();
			}

			// 获取设置了通用参数又非当前处理的其他组合
			listPPorts = getPercentPorts();
			tmpList = (ArrayList) listPPorts.clone();

			// 将组合截取成独立的
			String[] sPortArr = portCodes.split(",");

			// 循环组合产生对应的数据源sql
			for (int i = 0; i < sPortArr.length; i++) {

				// 先将特殊字符进行转换，主要是将[]转换成【】，将()替换（）,*换成#
				sTmpDs = sDs.replaceAll("\\(", "（").replaceAll("\\)", "）")
						.replaceAll("\\*", "#").replaceAll("\\[", "【")
						.replaceAll("\\]", "】");

				// 设置当前处理的组合
				portCode = sPortArr[i];

				// 如果是最后一个并且是设置了比例的，要进行钆差运算 *****
				if (sTmpDs.indexOf("TOB【") != -1 && listPPorts.size() == 1
						&& listPPorts.get(0).equals(portCode)) {

					// Gd[]中的数据要进行循环并进行减法操作
					while (sTmpDs.indexOf("TOB【") != -1) {
						// 1.获取数据源中源函数
						tmpOldFun = getTOBFunInfo(sTmpDs);

						// //取得被减数.将原函数中的比例参数替换成1，作为被减数
						// String tmpPartFun = tmpOldFun.replaceAll("<PP>",
						// "1");
						// 函数有两个参数，第一位为被减数，第二位才是要进行轧差的数据
						String tmpPartFun = tmpOldFun.split(";")[0];

						// 3.循环所有设置了比例的组合，排除自己，进行钆差计算
						for (int j = 0; j < tmpList.size(); j++) {
							if (tmpList.get(j).equals(portCode)) {
								continue;
							}
							// 将函数替换为对应的比例
							tmpPartFun += "-"
									+ tmpOldFun.split(";")[1].replaceAll(
											"<PP>", htPortPercent.get(
													tmpList.get(j)).toString());
						}
						// 4.将钆差计算后的sql替换原来的钆差函数
						sTmpDs = sTmpDs.replaceAll("TOB【" + tmpOldFun + "】",
								tmpPartFun);
					}
				} else {
					// 一般情况下不使用轧差函数，直接将函数去掉（替换成函数内容）
					while (sTmpDs.indexOf("TOB【") != -1) {
						tmpOldFun = getTOBFunInfo(sTmpDs);
						sTmpDs = sTmpDs.replaceAll("TOB【" + tmpOldFun + "】",
								tmpOldFun.split(";")[1]);
					}
				}

				if (htPortPercent.get(portCode) == null) {
					// 没有设置使用默认1
					sTmpDs = sTmpDs.replaceAll("<PP>", "1");
				} else {
					// 将函数替换为对应的比例
					sTmpDs = sTmpDs.replaceAll("<PP>", htPortPercent.get(
							portCode).toString());
				}

				if (listPPorts.contains(sPortArr[i])) {
					listPPorts.remove(sPortArr[i]);
				}
				// 还原成标准的sql
				sTmpDs = sTmpDs.replaceAll("（", "(").replaceAll("）", ")")
						.replaceAll("#", "*").replaceAll("【", "[").replaceAll(
								"】", "]");
				listPortDs.add(sPortArr[i] + "\f\f" + buildDsSql(sTmpDs));
			}

		} else {
			listPortDs.add(portCodes + "\f\f" + buildDsSql(sDs));
		}

		return listPortDs;
	}

	/**
	 * 获取所有设置了比例的组合
	 * 
	 * @return
	 */
	private ArrayList getPercentPorts() {
		ArrayList listPorts = new ArrayList();
		Enumeration e = htPortPercent.keys();
		while (e.hasMoreElements()) {
			listPorts.add(e.nextElement().toString());
		}
		return listPorts;
	}

	/**
	 * 解析钆差函数
	 * 
	 * @param sDs
	 * @return
	 */
	private String getTOBFunInfo(String sDs) {

		// 截取TOB[]中括号内的字段，因为可能存在嵌套的情况，因此要进行特别处理
		// 1.获取到TOB[后面的字符串
		String sTailDs = sDs.substring(sDs.indexOf("TOB【") + 4);
		// 2.根据[的个数判断从哪个]进行截断
		while (sTailDs.indexOf("【") != -1
				&& sTailDs.indexOf("【") < sTailDs.indexOf("】")) {
			sTailDs = sTailDs.replaceFirst("【", "[").replaceFirst("】", "]");
		}
		// 3.获取函数
		String sFunPara = sTailDs.substring(0, sTailDs.indexOf("】"));

		// 4.还原
		sFunPara = sFunPara.replaceAll("\\[", "【").replaceAll("\\]", "】");

		return sFunPara;
	}

	/**
	 * 处理函数 WDay[参数1,参数2,参数3] 取工作日前一天 参数1:传入的日期 参数2: 节假日群 参数3:相差天数
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
		BaseOperDeal deal = this.getSettingOper();
		try {
			if (sSql.indexOf("[") > 0 && sSql.indexOf("]") > 0) {
				if (sSql.indexOf("]") > sSql.indexOf("[")) { // 确保"]" 在"[" 的后面
					sParams = sSql.substring(sSql.indexOf("[") + 1, sSql
							.indexOf("]"));
					arrParams = sParams.split(";");
					if (arrParams[0].equals("<D>")||arrParams[0].equals("<D1>")) {//modify by nimengjing 2011.2.15 BUG #1090 接口处理里导入和导出接口的处理时,因解析函数处理不一致导致报错 
						date = this.beginDate;
					}
					sFunCode = sSql.substring(sSql.indexOf("[") - 4, sSql
							.indexOf("["));
					if (sFunCode.equalsIgnoreCase("WDay")) {
						strReplace = "WDay" + "\\[" + sParams + "\\]";
						strCalc = dbl.sqlDate(deal.getWorkDay(
								(String) arrParams[1], date, YssFun
										.toInt(arrParams[2])));
					}
				}
			}
			sSql = sSql.replaceAll(strReplace, strCalc);
			if (sFunCode.equalsIgnoreCase("WDay")) { // 此处只对WDay做处理，若增加了其他的函数，需增加判断。
				if (sSql.indexOf("WDay[" + params) > 0) { // 判断是否还有WDay函数
					sSql = pretSqlIns(sSql, date, params); // 若还有函数，则做递归处理。
				}
			}
			return sSql;
		} catch (Exception e) {
			throw new YssException("解析SQL内部函数出错" + e.toString(), e);
		}
	}

	// ==============End MS00817 = ==========================
	/**
	 * 获取组合的方法 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
	 * 
	 * @return
	 * @throws YssException
	 */
	public String getPortCode() throws YssException {
		if (this.portCodes != null && this.portCodes.length() > 0)
			return this.portCodes;
		return "";
	}

	/**
	 * 获取日期 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
	 * 
	 * @return
	 * @throws YssException
	 */
	public String getDate() throws YssException {
		if (this.beginDate != null) {
			return YssFun.formatDate(this.beginDate, "yyyy-MM-dd");
		}
		return YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd");
	}

	/**
	 * 获取组合的交易席位代码 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
	 * 
	 * @return
	 * @throws YssException
	 */
	public String getTradeSeatCode() throws YssException {
		String tradeSeat = "";
		String sqlStr = "";
		ResultSet rs = null;
		if (this.tradeSeat != null && this.tradeSeat.trim().length() > 0)
			return this.tradeSeat;
		try {
			// edit by yanghaiming 20100610 MS01257 QDV4赢时胜(上海)2010年5月26日05_B
			// 这里改取席位号
			sqlStr = "select distinct b.fseatnum as fsubcode from "
					+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
					+ " a left join (select * from "
					+ pub.yssGetTableName("tb_para_tradeseat")
					+ ") b on a.fsubcode = b.fseatcode"
					+ "  where  a.FPortCode in ("
					+ operSql.sqlCodes(this.portCodes) + " ) and a.FRelaType="
					+ dbl.sqlString("TradeSeat");
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {
				tradeSeat += (rs.getString("FSubCode") + ",");
			}
			if (tradeSeat.endsWith(","))
				tradeSeat = tradeSeat.substring(0, tradeSeat.length() - 1);
		} catch (Exception ex) {
			throw new YssException("获取交易席位的筛选条件出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return tradeSeat;
	}

	/**
	 * 获取组合的股东代码 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
	 * 
	 * @return
	 * @throws YssException
	 */
	public String getHolderCode() throws YssException {
		String holder = "";
		String sqlStr = "";
		ResultSet rs = null;
		try {
			sqlStr = "select FSubCode from "
					+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
					+ "  where  FPortCode in ("
					+ operSql.sqlCodes(this.portCodes) + " ) and FRelaType="
					+ dbl.sqlString("Stockholder");
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {
				holder = holder + (rs.getString("FSubCode") + ",");
			}
			if (holder.endsWith(","))
				holder = holder.substring(0, holder.length() - 1);
		} catch (Exception ex) {
			throw new YssException("获取股东代码的筛选条件出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return holder;
	}

	/**
	 * 获取资产代码 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
	 * 
	 * @return
	 * @throws YssException
	 */
	public String getAssestCode() throws YssException {
		String assest = "";
		String sqlStr = "";
		ResultSet rs = null;
		try {
			sqlStr = "select FAssetCode from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ "  where  FPortCode in ("
					+ operSql.sqlCodes(this.portCodes) + " ) ";
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {
				assest += (rs.getString("FAssetCode") + ",");
			}
			if (assest.endsWith(","))
				assest = assest.substring(0, assest.length() - 1);
		} catch (Exception ex) {
			throw new YssException("获取组合资产代码的筛选条件出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return assest;
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 
	 * @param alFileNameSet
	 * 将文件名都传进去判断是否有两个都是日期的，如果有则给出提示
	 * @throws YssException 
	 */
	
	
	
	
	
	
	
	/** shashijie 2011.06.07 STORY #970 目前接口预处理支持财务表的 A形式，但在解析时未能正确解析 */
    private String buildTableName(String tableName,String portCode) throws YssException{
        try{
            if (tableName.indexOf("<U>") > 0) {
                tableName = tableName.replaceAll("<U>", pub.getUserCode());
            } else if (tableName.indexOf("< U >") > 0) {
                tableName = tableName.replaceAll("< U >", pub.getUserCode());
            }
            if (tableName.indexOf("<Year>") > 0) { //把"<Year>"的标识替换成结束日期的年份
                tableName = tableName.replaceAll("<Year>",
                                     YssFun.formatDate(this.beginDate, "yyyy"));
            } else if (tableName.indexOf("< Year >") > 0) { // add by leeyu 080729
                tableName = tableName.replaceAll("< Year >",
                                     YssFun.formatDate(this.beginDate, "yyyy"));
            }
            if (tableName.indexOf("<Set>") > 0) { //把"<Year>"的标识替换成套帐号
                YssFinance cw = new YssFinance();
                cw.setYssPub(pub);
                tableName = tableName.replaceAll("<Set>", cw.getCWSetCode(portCode));
            } else if (tableName.indexOf("< Set >") > 0) { // add by leeyu 080729
                YssFinance cw = new YssFinance();
                cw.setYssPub(pub);
                tableName = tableName.replaceAll("< Set >", cw.getCWSetCode(portCode));
            }
            if (tableName.indexOf("<Group>") > 0) { //把"<Group>"的标识替换成群
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	//若为跨组合群操作 
            	if(pub.getPrefixTB() != null && !pub.getPrefixTB().equals(pub.getAssetGroupCode())){
            		//则<Group>替换为跨组合群操作的已选组合群代码
            		tableName = tableName.replaceAll("<Group>", pub.getPrefixTB());
            	}else{
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            		tableName = tableName.replaceAll("<Group>", pub.getAssetGroupCode());
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	}
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            } else if (tableName.indexOf("< Group >") > 0) {
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	//若为跨组合群操作 
            	if(pub.getPrefixTB() != null && !pub.getPrefixTB().equals(pub.getAssetGroupCode())){
            		//则<Group>替换为跨组合群操作的已选组合群代码
            		tableName = tableName.replaceAll("< Group >", pub.getPrefixTB());
            	}else{
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            		tableName = tableName.replaceAll("< Group >", pub.getAssetGroupCode());
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	}
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            }
            tableName = tableName.replaceAll("~Base", "base");
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }
        return tableName;
    }
    
    
}
