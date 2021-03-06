package com.yss.main.operdata;

import java.sql.*; //MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009.04.11
import java.util.*;

import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.util.*;

public class ExchangeRateBean extends BaseDataSettingBean implements
		IDataSetting {
	public ExchangeRateBean() {
	}

	private String strExRateSrcCode = ""; // 汇率行情代码
	private String strExRateSrcName = ""; // 汇率行情名称
	private String strCuryCode = ""; // 货币代码
	private String strCuryName = ""; // 货币名称
	private String strPortCode = ""; // 组合代码
	private String strPortName = ""; // 组合名称
	private String strMarkCuryCode = ""; // 基准货币代码 linjunyun 2008-11-20
											// bug:MS00011
	private String strMarkCuryName = ""; // 基准货币名称 linjunyun 2008-11-20
											// bug:MS00011
	private double strExRate1; //
	private double strExRate2; //
	private double strExRate3; //
	private double strExRate4; //
	private double strExRate5; //
	private double strExRate6; //
	private double strExRate7; //
	private double strExRate8; //
	private String strOldExRateSrcCode = "";
	private String strExRateDate = ""; //
	private String strExRateTime = "00:00:00"; //
	private String strOldExRateDate = "";
	private String strOldPortCode = "";
	private String strOldCuryCode = "";
	private String strOldExRateTime = "00:00:00";
	private String strOldMarkCuryCode = ""; // 原基准货币代码 linjunyun 2008-11-20
											// bug:MS00011
	private String strDesc = "";
	private String sRecycled = ""; // 保存未解析前的字符串
	private String sAssetGroupCode = ""; // QDV4建行2008年12月25日01_A MS00131
											// byleeyu 20090204
	private String sAssetGroupName = "";	//added by liubo.Story #1770
	private String sOldAssetGroupCode = ""; // QDV4建行2008年12月25日01_A MS00131
											// byleeyu 20090204

	private ExchangeRateBean filterType;
    private String strDataSource = "";//add by jiangshichao STORY #1244 导入汇率时多组合情况下，以组合代码为空的汇率为默认的汇率，导入数据中心相关需求
	
	//private String strIsOnlyColumns = "0"; // 在初始登陆时是否只显示列，不查询数据\

	// ------MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie
	// 2009.04.11---------------------------//
	private HashMap rate; // 用于储存汇率数据表中所有的利率字段对应的数据
	private String Group;//add baopingping #story 1167 20110719增加前台是否选择了跨组合群操作
	private String Iscopy;//add baopingping #story 1167 20110719增加前台是否覆盖
	
	//add by songjie 2011.09.15 #1492 QDV4华安基金2011年8月10日01_A
	private String multAuditString = "";//储存批量审核、反审核的汇率数据
	private SingleLogOper logOper;//add by songjie 2011.11.22 BUG 2953 QDV4赢时胜(测试)2011年10月14日01_B
	
	//add by jiangshichao STORY #1244 导入汇率时多组合情况下，以组合代码为空的汇率为默认的汇率，导入数据中心相关需求  start ---
	public String getStrDataSource() {
		return strDataSource;
	}

	public void setStrDataSource(String strDataSource) {
		this.strDataSource = strDataSource;
	}
   // STORY #1244 导入汇率时多组合情况下，以组合代码为空的汇率为默认的汇率，导入数据中心相关需求  end -------------------------
	public String getIscopy() {
		return Iscopy;
	}

	public void setIscopy(String iscopy) {
		Iscopy = iscopy;
	}

	public String getGroup() {
		return Group;
	}

	public void setGroup(String group) {
		Group = group;
	}

	/**
	 * rate的get方法
	 * 
	 * @return HashMap
	 */
	public HashMap getRate() {
		return rate;
	}

	/**
	 * rate的set方法
	 * 
	 * @param rate
	 *            HashMap
	 */
	public void setRate(HashMap rate) {
		this.rate = rate;
	}

	// ------MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie
	// 2009.04.11---------------------------//

	public void setFilterType(ExchangeRateBean filterType) {
		this.filterType = filterType;
	}

	/**
	 * addOperData 增加汇率行情设置
	 * 
	 * @return String
	 */
	public String addSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		boolean bIsExchangeRatePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
		String strSql = "";
		try {
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			strSql =
			// "insert into " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
			// "(FExRateSrcCode, FCuryCode,FMarkCury,FExRateDate,FExRateTime,FPortCode,FExRate1,FExRate2, FExRate3,FExRate4,"
			// +
			// " FExRate5, FExRate6,FExRate7,FExRate8,FDesc,FDataSource,FCheckState, FCreator, FCreateTime,FCheckUser) "
			// +
			"insert into "
					+ (bIsExchangeRatePub ? "Tb_Base_ExchangeRate" : pub
							.yssGetTableName("Tb_Data_ExchangeRate"))
					+ // 根据参数决定是用哪张表 MS00131 by leeyu
					"(FExRateSrcCode, FCuryCode,FMarkCury,FExRateDate,FExRateTime,FPortCode,FExRate1,FExRate2, FExRate3,FExRate4,"
					+ " FExRate5, FExRate6,FExRate7,FExRate8,FDesc,FDataSource,FCheckState, FCreator, FCreateTime,FCheckUser"
					+ (bIsExchangeRatePub ? ",FASSETGROUPCODE" : "")
					+ ") "
					+ // 若表为新表则添加上FASSETGROUPCODE字段
					" values("
					+ dbl.sqlString(this.strExRateSrcCode)
					+ ","
					+ dbl.sqlString(this.strCuryCode)
					+ ","
					+ dbl.sqlString(this.strMarkCuryCode)
					+ ","
					+ // 插入基准货币代码 linjunyun 2008-11-20 bug:MS00011
					dbl.sqlDate(this.strExRateDate)
					+ ","
					+ dbl.sqlString(this.strExRateTime)
					+ ","
					+ dbl.sqlString(this.strPortCode.length() == 0 ? " "
							: this.strPortCode)
					+ ","
					+
					// lzp modify 2007 12.7
					this.strExRate1
					+ ","
					+ this.strExRate2
					+ ","
					+ this.strExRate3
					+ ","
					+ this.strExRate4
					+ ","
					+ this.strExRate5
					+ ","
					+ this.strExRate6
					+ ","
					+ this.strExRate7
					+ ","
					+ this.strExRate8
					+ ","
					+
					// -------------
					dbl.sqlString(this.strDesc)
					+ ","
					+ 0
					+ ","
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ","
					+ dbl.sqlString(this.creatorCode)
					+ ", "
					+ dbl.sqlString(this.creatorTime)
					+ ","
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode))
					+ (bIsExchangeRatePub ? ("," + dbl
							.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode
									: " "))
							: "") + // 若是新表则需给这个字段赋值 QDV4建行2008年12月25日01_A
									// MS00131 byleeyu 20090204
					")";
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return buildRowStr();
		} catch (Exception e) {
			throw new YssException("新增汇率行情设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	/**
	 * buildRowStr 获取数据字符串
	 * 
	 * @return String
	 */
	public String buildRowStr() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.strExRateSrcCode).append("\t");
		buf.append(this.strExRateSrcName).append("\t");
		buf.append(this.strExRateDate).append("\t");
		buf.append(this.strExRateTime).append("\t");
		buf.append(this.strPortCode).append("\t");
		buf.append(this.strPortName).append("\t");
		buf.append(this.strCuryCode).append("\t");
		buf.append(this.strCuryName).append("\t");
		buf.append(this.strExRate1).append("\t");
		buf.append(this.strExRate2).append("\t");
		buf.append(this.strExRate3).append("\t");
		buf.append(this.strExRate4).append("\t");
		buf.append(this.strExRate5).append("\t");
		buf.append(this.strExRate6).append("\t");
		buf.append(this.strExRate7).append("\t");
		buf.append(this.strExRate8).append("\t");
		buf.append(this.strDesc).append("\t");
		buf.append(this.strMarkCuryCode).append("\t"); // 获得基准货币代码 linjunyun
														// 2008-11-20
														// bug:MS00011
		buf.append(this.strMarkCuryName).append("\t"); // 获得基准货币名称 linjunyun
														// 2008-11-20
														// bug:MS00011
		buf.append(this.sAssetGroupCode).append("\t"); // QDV4建行2008年12月25日01_A
														// MS00131 byleeyu
														// 20090204 新增字段
		buf.append(this.sAssetGroupName).append("\t");	//added by liubo.Story #1770
		buf.append(super.buildRecLog());
		return buf.toString();
	}

	/**
	 * checkInput 验证数据
	 * 
	 * @param btOper
	 *            byte
	 */
	public void checkInput(byte btOper) throws YssException {
		
		
		
		if (((String) pub.getHtPubParams().get("exchangerate"))
				.equalsIgnoreCase("true")) { // 当通用参数传过来的值为真时再给这个变量赋值 MS00131
			dbFun.checkInputCommon(
							btOper, // 通过通用查询得到的结果为真时，则要用到基础级表
									// QDV4建行2008年12月25日01_A MS00131 by leeyu
									// 20090205
							"Tb_Base_ExchangeRate",
							"FExRateSrcCode,FPortCode,FCuryCode,FMarkCury,FExRateDate,FExRateTime,FASSETGROUPCODE",
							this.strExRateSrcCode
									+ ","
									+ (this.strPortCode.length() == 0 ? " "
											: this.strPortCode) + ","
									+ this.strCuryCode + ","
									+ this.strMarkCuryCode
									+ ","
									+ // 验证基准货币代码 linjunyun 2008-11-20
										// bug:MS00011
									this.strExRateDate + ","
									+ this.strExRateTime + ","
									+ sAssetGroupCode, this.strOldExRateSrcCode
									+ ","
									+ (this.strOldPortCode.length() == 0 ? " "
											: this.strOldPortCode) + ","
									+ this.strOldCuryCode
									+ ","
									+ this.strOldMarkCuryCode
									+ // 验证原基准货币代码 linjunyun 2008-11-20
										// bug:MS00011
									"," + this.strOldExRateDate + ","
									+ this.strOldExRateTime + ","
									+ sOldAssetGroupCode);
		} else { // 若不是跨组合群处理，则还是用原来的方法来处理 by leeyu 20090205 MS00131
			String[] sAssetGroup = null; 
			if("".equals(sAssetGroupCode.trim())){ 
				dbFun.checkInputCommon(
						btOper,
						pub.yssGetTableName("Tb_Data_ExchangeRate"),
						"FExRateSrcCode,FPortCode,FCuryCode,FMarkCury,FExRateDate,FExRateTime",
						this.strExRateSrcCode
								+ ","
								+ (this.strPortCode.length() == 0 ? " "
										: this.strPortCode) + ","
								+ this.strCuryCode + ","
								+ this.strMarkCuryCode + ","
								+ // 验证基准货币代码 linjunyun 2008-11-20
									// bug:MS00011
								this.strExRateDate + ","
								+ this.strExRateTime,
						this.strOldExRateSrcCode
								+ ","
								+ (this.strOldPortCode.length() == 0 ? " "
										: this.strOldPortCode) + ","
								+ this.strOldCuryCode + ","
								+ this.strOldMarkCuryCode
								+ // 验证原基准货币代码 linjunyun 2008-11-20
									// bug:MS00011
								"," + this.strOldExRateDate + ","
								+ this.strOldExRateTime);
			}else{
			sAssetGroup = sAssetGroupCode.split(","); 
			for (int i = 0;i < sAssetGroup.length;i++) 
			{ 
			try 
			{ 

			dbFun.checkInputCommon(
							btOper,
							"Tb_"+sAssetGroup[i]+"_Data_ExchangeRate",
							"FExRateSrcCode,FPortCode,FCuryCode,FMarkCury,FExRateDate,FExRateTime",
							this.strExRateSrcCode
									+ ","
									+ (this.strPortCode.length() == 0 ? " "
											: this.strPortCode) + ","
									+ this.strCuryCode + ","
									+ this.strMarkCuryCode + ","
									+ // 验证基准货币代码 linjunyun 2008-11-20
										// bug:MS00011
									this.strExRateDate + ","
									+ this.strExRateTime,
							this.strOldExRateSrcCode
									+ ","
									+ (this.strOldPortCode.length() == 0 ? " "
											: this.strOldPortCode) + ","
									+ this.strOldCuryCode + ","
									+ this.strOldMarkCuryCode
									+ // 验证原基准货币代码 linjunyun 2008-11-20
										// bug:MS00011
									"," + this.strOldExRateDate + ","
									+ this.strOldExRateTime);
			} catch(Exception e) 
			{ 
			throw new YssException("组合群【" + sAssetGroup[i] + "】已存在投资组合为	【" + (this.strPortCode.length() == 0 ? " ": this.strPortCode) + "】,投资货币为【"
					+ this.strCuryCode + "】,基准货币为【"
					+ this.strMarkCuryCode
					+ // 验证原基准货币代码 linjunyun 2008-11-20
						// bug:MS00011
					"】,汇率日期为【" + this.strExRateDate + "】,汇率时间为【"
					+ this.strExRateTime + "】的数据！"); 
			} 
			} 
			}
		}
	}

	/**
	 * 修改时间：2008年3月27号 修改人：单亮 原方法功能：只能处理汇率行情设置的审核和未审核的单条信息。
	 * 新方法功能：可以处理汇率行情设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
	 * 新方法功能：可以处理汇率行情设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
	 * 
	 * @throws YssException
	 */
	public void checkSetting() throws YssException {
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		boolean bIsExchangeRatePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
		Connection conn = dbl.loadConnection();
		//add by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A
		String[] assetGroupCodes = null;//保存组合群数组
		try {
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			conn.setAutoCommit(false);
			bTrans = true;
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) {
				arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
					if(this.sAssetGroupCode.trim().length() > 0){//添加跨组合群还原功能
						assetGroupCodes = this.sAssetGroupCode.trim().split(",");
						for(int j = 0; j < assetGroupCodes.length; j++){
							strSql = "update " + (bIsExchangeRatePub ? "Tb_Base_ExchangeRate"
									: "Tb_" + assetGroupCodes[j] + "_Data_ExchangeRate")
									+ // 根据参数决定用哪张表 MS00131
									" set FCheckState = " + this.checkStateId
									+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
									+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) 
									+ " where FExRateSrcCode = " + dbl.sqlString(this.strExRateSrcCode)
									+ " and FPortCode = " 
									+ dbl.sqlString(this.strPortCode.length() == 0 ? " ": this.strPortCode)
									+ " and FCuryCode = " + dbl.sqlString(this.strCuryCode) + " and FMarkCury = "
									+ dbl.sqlString("null".equalsIgnoreCase(this.strMarkCuryCode) ? " ": this.strMarkCuryCode)
									// 增加基准货币代码主键条件 linjunyun 2008-11-20 bug:MS00011
									+ " and FExRateTime = " + dbl.sqlString(this.strExRateTime)
									+ " and FExRateDate = " + dbl.sqlDate(this.strExRateDate)
									+ (bIsExchangeRatePub ? (" and FASSETGROUPCODE=" 
									// 添加上FASSETGROUPCODE主键字段  QDV4建行2008年12月25日01_A MS00131 by leeyu
									+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode: " ")): ""); 
							
							dbl.executeSql(strSql);							
						}
					}else{
						strSql = "update " + (bIsExchangeRatePub ? "Tb_Base_ExchangeRate"
								: pub.yssGetTableName("Tb_Data_ExchangeRate"))
								+ // 根据参数决定用哪张表 MS00131
								" set FCheckState = " + this.checkStateId
								+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
								+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) 
								+ " where FExRateSrcCode = " + dbl.sqlString(this.strExRateSrcCode)
								+ " and FPortCode = " 
								+ dbl.sqlString(this.strPortCode.length() == 0 ? " ": this.strPortCode)
								+ " and FCuryCode = " + dbl.sqlString(this.strCuryCode) + " and FMarkCury = "
								+ dbl.sqlString("null".equalsIgnoreCase(this.strMarkCuryCode) ? " ": this.strMarkCuryCode)
								// 增加基准货币代码主键条件 linjunyun 2008-11-20 bug:MS00011
								+ " and FExRateTime = " + dbl.sqlString(this.strExRateTime)
								+ " and FExRateDate = " + dbl.sqlDate(this.strExRateDate)
								+ (bIsExchangeRatePub ? (" and FASSETGROUPCODE=" 
								// 添加上FASSETGROUPCODE主键字段  QDV4建行2008年12月25日01_A MS00131 by leeyu
								+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode: " ")): ""); 
						
						dbl.executeSql(strSql);						
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
				}
			}
			// 如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
			else if (strExRateSrcCode != null&&(!strExRateSrcCode.equalsIgnoreCase("")) ) {
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
				if(this.sAssetGroupCode.trim().length() > 0){//添加跨组合群还原功能
					assetGroupCodes = this.sAssetGroupCode.trim().split(",");
					for(int j = 0; j < assetGroupCodes.length; j++){
						strSql = "update "+ (bIsExchangeRatePub ? "Tb_Base_ExchangeRate" : 
							"Tb_" + assetGroupCodes[j] + "_Data_ExchangeRate")
							// 根据参数决定用哪张表 MS00131
							+ " set FCheckState = " + this.checkStateId
							+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))
							+ " where FExRateSrcCode = "+ dbl.sqlString(this.strExRateSrcCode)
							+ " and FPortCode = "+ dbl.sqlString(this.strPortCode.length() == 0 ? " ": this.strPortCode)
							+ " and FCuryCode = "+ dbl.sqlString(this.strCuryCode) + " and FMarkCury="
							+ dbl.sqlString("null".equalsIgnoreCase(this.strMarkCuryCode) ? " ": this.strMarkCuryCode)
							// 增加基准货币代码主键条件 linjunyun 2008-11-20 bug:MS00011
							+ " and FExRateTime = "+ dbl.sqlString(this.strExRateTime)
							+ " and FExRateDate = "+ dbl.sqlDate(this.strExRateDate)
							+ (bIsExchangeRatePub ? (" and FASSETGROUPCODE=" 
							// 添加上FASSETGROUPCODE主键字段 QDV4建行2008年12月25日01_A MS00131 by leeyu
							+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode: " ")): ""); 
						
						dbl.executeSql(strSql);						
					}
				}else{
					strSql = "update "+ (bIsExchangeRatePub ? "Tb_Base_ExchangeRate" : 
						pub.yssGetTableName("Tb_Data_ExchangeRate"))
						// 根据参数决定用哪张表 MS00131
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))
						+ " where FExRateSrcCode = "+ dbl.sqlString(this.strExRateSrcCode)
						+ " and FPortCode = "+ dbl.sqlString(this.strPortCode.length() == 0 ? " ": this.strPortCode)
						+ " and FCuryCode = "+ dbl.sqlString(this.strCuryCode) + " and FMarkCury="
						+ dbl.sqlString("null".equalsIgnoreCase(this.strMarkCuryCode) ? " ": this.strMarkCuryCode)
						// 增加基准货币代码主键条件 linjunyun 2008-11-20 bug:MS00011
						+ " and FExRateTime = "+ dbl.sqlString(this.strExRateTime)
						+ " and FExRateDate = "+ dbl.sqlDate(this.strExRateDate)
						+ (bIsExchangeRatePub ? (" and FASSETGROUPCODE=" 
						// 添加上FASSETGROUPCODE主键字段 QDV4建行2008年12月25日01_A MS00131 by leeyu
						+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode: " ")): ""); 
					
					dbl.executeSql(strSql);					
				}
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
			}
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核汇率行情设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		// ----------end

	}

	/**
	 * 删除数据即放入回收站
	 * 
	 * @throws YssException
	 */
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		boolean bIsExchangeRatePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
		String strSql = "";
		try {
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			// strSql = "update " + pub.yssGetTableName("Tb_Data_ExchangeRate")
			// +
			strSql = "update "
					+ (bIsExchangeRatePub ? "Tb_Base_ExchangeRate" : pub
							.yssGetTableName("Tb_Data_ExchangeRate"))
					+ // 根据参数决定用哪张表 MS00131
					" set FCheckState = "
					+ this.checkStateId
					+ ", FCheckUser = "
					+ dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FExRateSrcCode = "
					+ dbl.sqlString(this.strExRateSrcCode)
					+ " and FPortCode="
					+ dbl.sqlString(this.strPortCode.length() == 0 ? " "
							: this.strPortCode)
					+ " and FCuryCode="
					+ dbl.sqlString(this.strCuryCode)
					+ " and FMarkCury="
					+ dbl.sqlString(this.strMarkCuryCode)
					+ // 增加基准货币代码主键条件 linjunyun 2008-11-20 bug:MS00011
					" and FExRateTime="
					+ dbl.sqlString(this.strExRateTime)
					+ " and FExRateDate="
					+ dbl.sqlDate(this.strExRateDate)
					+ (bIsExchangeRatePub ? (" and FASSETGROUPCODE=" + dbl
							.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode
									: " "))
							: ""); // 添加上FASSETGROUPCODE主键字段
									// QDV4建行2008年12月25日01_A MS00131 by leeyu

			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除汇率行情设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	/**
	 * editOperData 修改汇率行情设置
	 * 
	 * @return String
	 */
	public String editSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		boolean bIsExchangeRatePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
		String strSql = "";
		try {
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			// strSql = "update " + pub.yssGetTableName("Tb_Data_ExchangeRate")
			// +
			strSql = "update "
					+ (bIsExchangeRatePub ? "Tb_Base_ExchangeRate" : pub
							.yssGetTableName("Tb_Data_ExchangeRate"))
					+ // 根据参数决定用哪张表 MS00131
					" set FExRateSrcCode = "
					+ dbl.sqlString(this.strExRateSrcCode)
					+ ", FExRateDate = "
					+ dbl.sqlDate(this.strExRateDate)
					+ ", FExRateTime = "
					+ dbl.sqlString(this.strExRateTime)
					+ ",FPortCode = "
					+ dbl.sqlString(this.strPortCode.length() == 0 ? " "
							: this.strPortCode)
					+ ", FCuryCode = "
					+ dbl.sqlString(this.strCuryCode)
					+ ", FMarkCury="
					+ dbl.sqlString(this.strMarkCuryCode)
					+ // 更新基准货币代码 linjunyun 2008-11-20 bug:MS00011
					// --------------lzp modify 2007 12.7
					", FExRate1 ="
					+ this.strExRate1
					+ ", FExRate2 ="
					+ this.strExRate2
					+ ",FExRate3 ="
					+ this.strExRate3
					+ ",FExRate4 ="
					+ this.strExRate4
					+ ",FExRate5 ="
					+ this.strExRate5
					+ ",FExRate6 ="
					+ this.strExRate6
					+ ",FExRate7 ="
					+ this.strExRate7
					+ ",FExRate8 ="
					+ this.strExRate8
					+
					// ------------------
					",FDesc ="
					+ dbl.sqlString(this.strDesc)
					+ (bIsExchangeRatePub ? (",FASSETGROUPCODE=" + dbl
							.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode
									: " "))
							: "")
					+ // 需为FASSETGROUPCODE主键字段赋值 QDV4建行2008年12月25日01_A MS00131
						// by leeyu
					",FCheckstate = "
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ",FCreator = "
					+ dbl.sqlString(this.creatorCode)
					+ " , FCreateTime = "
					+ dbl.sqlString(this.creatorTime)
					+ ",FCheckUser = "
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode))
					+ " where FExRateSrcCode = "
					+ dbl.sqlString(this.strOldExRateSrcCode)
					+ " and FPortCode="
					+ dbl.sqlString(this.strOldPortCode.length() == 0 ? " "
							: this.strOldPortCode)
					+ " and FCuryCode="
					+ dbl.sqlString(this.strOldCuryCode)
					+ " and FMarkCury="
					+ dbl.sqlString(this.strOldMarkCuryCode)
					+ // 原基准货币代码作为主键条件查询 linjunyun 2008-11-20 bug:MS00011
					" and FExRateTime="
					+ dbl.sqlString(this.strOldExRateTime)
					+ " and FExRateDate="
					+ dbl.sqlDate(this.strOldExRateDate)
					+ (bIsExchangeRatePub ? (" and FASSETGROUPCODE=" + dbl
							.sqlString(sOldAssetGroupCode.trim().length() > 0 ? sOldAssetGroupCode
									: " "))
							: ""); // 添加上FASSETGROUPCODE主键字段
									// QDV4建行2008年12月25日01_A MS00131 by leeyu
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return buildRowStr();
		} catch (Exception e) {
			throw new YssException("修改汇率行情设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	/**
	 * getListViewData1 获取汇率行情数据
	 * 
	 * @return String
	 */
	public String getListViewData1() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sDateStr = "";
		String strSql = "";
		ResultSet rs = null;
		boolean bTrans = false;
		boolean bIsExchangeRatePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
		String strSql1 = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try {
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			sHeader = this.getListView1Headers();
			// fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A 20100708
			// 优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
			if (this.filterType.isOnlyColumns.equals("1")&&!(pub.isBrown())) {// add by ysh 20111025 story 1285
				return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
						+ "\r\f" + this.getListView1ShowCols() + "\r\f"
						+ yssPageInationBean.buildRowStr();// QDV4赢时胜上海2010年03月16日06_B
															// MS00884 by xuqiji
			}
			// --------------------------------------end
			// MS01310--------------------------------------------------------
			strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, "
					+ " g.FCuryName,f.FExRateSrcName,h.FPortName as FPortName,h.FStartDate as FStartDate,"
					+
					// 设置基准货币的货币代码别名FMarkCuryCode，货币名称的别名FMarkCuryName linjunyun
					// 2008-11-20 bug:MS00011
					" m.FCuryCode as FMarkCuryCode, m.FCuryName as FMarkCuryName, ass.FAssetGroupName as FAssetGroupName "
					+
					// " from " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
					// " a " +
					" from "
					+ (bIsExchangeRatePub ? "Tb_Base_ExchangeRate" : pub
							.yssGetTableName("Tb_Data_ExchangeRate"))
					+ " a "
					+ // 根据参数决定用哪张表 MS00131 QDV4建行2008年12月25日01_A by leeyu
						// 20090205
					" left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
					+ " left join (select FCuryCode,FCuryName from "
					+ pub.yssGetTableName("tb_para_currency")
					+ " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode"
					+
					// 获取基准货币的货币代码和货币名称 linjunyun 2008-11-20 bug:MS00011
					" left join (select FCuryCode,FCuryName from "
					+ pub.yssGetTableName("tb_para_currency")
					+ " where FCheckState = 1) m on a.FMarkCury = m.FCuryCode"
					+
					// ************************//
	                " left join (select FAssetGroupCode,FAssetGroupName from tb_sys_AssetGroup) ass on a.FAssetGroupCode = ass.FAssetGroupCode" +	//added by liubo.Story #1770
					" left join (select FExRateSrcCode,FExRateSrcName from "
					+ pub.yssGetTableName("Tb_Para_ExRateSource")
					+ " ) f on a.FExRateSrcCode = f.FExRateSrcCode "
					+ " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
					//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//					+ pub.yssGetTableName("tb_para_portfolio")
//					+ " "
//					+ " where FStartDate <= "
//					+ dbl.sqlDate(new java.util.Date())
//					+ " and FCheckState = 1 group by FPortCode )u "
//					+ " join (select * from "
					//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
					+ pub.yssGetTableName("tb_para_portfolio")
					+ " v where FCheckState = 1) h on a.FPortCode = h.FPortCode"//edit by songjie 2011.03.16 不以最大的启用日期查询数据
					+
					// buildFilterSql() +
					// " order by a.FCheckState, a.FCreateTime desc";
					buildFilterSql()
					+ (bIsExchangeRatePub ? (" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like '%"	//modified by liubo.Story #1770
							+ pub.getAssetGroupCode() + "%')")
							: "") + // 若为新表还要添加上查询条件 MS00131
									// QDV4建行2008年12月25日01_A
					" order by a.FCheckState, a.FCreateTime desc";
			// QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("ExchangeRate");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
			while (rs.next()) {
				//modified by liubo.Story #1770.
				//用tmpAssetGroupName变量保存组合群名称，然后使用基类重载的buildRowShowStr方法将组合群名称插入ListView的数据中
				//================================
				String tmpAssetGroupName = this.getGroupNameFromGroupCode(rs.getString("FASSETGROUPCODE"));	
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols(),tmpAssetGroupName))
						.append(YssCons.YSS_LINESPLITMARK);

				//============end====================

				setResultSetAttr(rs);
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}
			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f"
					+ yssPageInationBean.buildRowStr();// QDV4赢时胜上海2010年03月16日06_B
														// MS00884 by xuqiji
		} catch (Exception e) {
			throw new YssException("获取汇率行情信息出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(dbl.getProcStmt());
		}

	}

	/**
	 * getListViewData2
	 * 
	 * @return String
	 */
	public String getListViewData2() {
		return "";
	}

	/**
	 * getListViewData3
	 * 
	 * @return String
	 */
	public String getListViewData3() {
		return "";
	}

	/**
	 * getOperData
	 */
	public void getOperData() {
	}

	/**
	 * getTreeViewData1
	 * 
	 * @return String
	 */
	public String getTreeViewData1() {
		return "";
	}

	/**
	 * getTreeViewData2
	 * 
	 * @return String
	 */
	public String getTreeViewData2() {
		return "";
	}

	/**
	 * getTreeViewData3
	 * 
	 * @return String
	 */
	public String getTreeViewData3() {
		return "";
	}

	/**
	 * parseRowStr 解析汇率行情数据
	 * 
	 * @param sRowStr
	 *            String
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
		String sTmpStr = "";
		String sMutiAudit = "";//add by songjie 2011.09.15 # 1492 QDV4华安基金2011年8月10日01_A
		try {
			if (sRowStr.trim().length() == 0) {
				return;
			}
			
			//---edit by songjie 2011.09.15 # 1492 QDV4华安基金2011年8月10日01_A start---//
			if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
				sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];
				multAuditString = sMutiAudit;
				sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];
				
				if (sRowStr.indexOf("\r\t") >= 0) {
					sTmpStr = sRowStr.split("\r\t")[0];
				} else {
					sTmpStr = sRowStr;
				}
				sRecycled = sRowStr; // 把未解析的字符串先赋给sRecycled
				reqAry = sTmpStr.split("\t");
				this.strExRateSrcCode = reqAry[0];
				this.strExRateSrcName = reqAry[1];
				this.strExRateDate = reqAry[2];
				this.strExRateTime = reqAry[3];
				this.strPortCode = reqAry[4];
				this.strPortName = reqAry[5];
				this.strCuryCode = reqAry[6];
				this.strCuryName = reqAry[7];
				if (YssFun.isNumeric(reqAry[8])) {
					this.strExRate1 = Double.parseDouble(reqAry[8]);
				}
				if (YssFun.isNumeric(reqAry[9])) {
					this.strExRate2 = Double.parseDouble(reqAry[9]);
				}
				if (YssFun.isNumeric(reqAry[10])) {
					this.strExRate3 = Double.parseDouble(reqAry[10]);
				}
				if (YssFun.isNumeric(reqAry[11])) {
					this.strExRate4 = Double.parseDouble(reqAry[11]);
				}
				if (YssFun.isNumeric(reqAry[12])) {
					this.strExRate5 = Double.parseDouble(reqAry[12]);
				}
				if (YssFun.isNumeric(reqAry[13])) {
					this.strExRate6 = Double.parseDouble(reqAry[13]);
				}
				if (YssFun.isNumeric(reqAry[14])) {
					this.strExRate7 = Double.parseDouble(reqAry[14]);
				}
				if (YssFun.isNumeric(reqAry[15])) {
					this.strExRate8 = Double.parseDouble(reqAry[15]);
				}

				// ------ modify by nimengjing 2010.12.02 BUG #535
				// 指数行情设置界面描述字段中存在回车符时，清除/还原报错
				if (reqAry[16] != null) {
					if (reqAry[16].indexOf("【Enter】") >= 0) {
						this.strDesc = reqAry[16].replaceAll("【Enter】", "\r\n");
					} else {
						this.strDesc = reqAry[16];
					}
				}
				// ----------------- BUG #533 ----------------//
				this.checkStateId = Integer.parseInt(reqAry[17]);
				this.strOldExRateSrcCode = reqAry[18];
				this.strOldExRateDate = reqAry[19];
				this.strOldExRateTime = reqAry[20];
				this.strOldPortCode = reqAry[21];
				this.strOldCuryCode = reqAry[22];
				this.strMarkCuryCode = reqAry[23]; // 赋值基准货币代码 linjunyun 2008-11-20
													// bug:MS00011
				this.strMarkCuryName = reqAry[24]; // 赋值基准货币名称 linjunyun 2008-11-20
													// bug:MS00011
				this.strOldMarkCuryCode = reqAry[25]; // 赋值原基准货币名称 linjunyun
														// 2008-11-20 bug:MS00011
				this.isOnlyColumns = reqAry[26];
				this.sAssetGroupCode = reqAry[27]; // MS00131 新增字段
													// QDV4建行2008年12月25日01_A by
													// leeyu 20090205
				this.sOldAssetGroupCode = reqAry[28]; // MS00131 新增字段
														// QDV4建行2008年12月25日01_A by
														// leeyu 20090205
				this.sOldAssetGroupCode = reqAry[29];	//added by liubo.Story #1770
				this.Group=reqAry[30];//add baopingping #story 1167 接收前台传来是否进行跨组合操作
				this.Iscopy=reqAry[31];//add baopingping #story 1167 接收前台传来是否覆盖
				super.parseRecLog();
				if (sRowStr.indexOf("\r\t") >= 0) {
					if (this.filterType == null) {
						this.filterType = new ExchangeRateBean();
						this.filterType.setYssPub(pub);
					}
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			}else{
				if (sRowStr.indexOf("\r\t") >= 0) {
					sTmpStr = sRowStr.split("\r\t")[0];
				} else {
					sTmpStr = sRowStr;
				}
				sRecycled = sRowStr; // 把未解析的字符串先赋给sRecycled
				reqAry = sTmpStr.split("\t");
				this.strExRateSrcCode = reqAry[0];
				this.strExRateSrcName = reqAry[1];
				this.strExRateDate = reqAry[2];
				this.strExRateTime = reqAry[3];
				this.strPortCode = reqAry[4];
				this.strPortName = reqAry[5];
				this.strCuryCode = reqAry[6];
				this.strCuryName = reqAry[7];
				if (YssFun.isNumeric(reqAry[8])) {
					this.strExRate1 = Double.parseDouble(reqAry[8]);
				}
				if (YssFun.isNumeric(reqAry[9])) {
					this.strExRate2 = Double.parseDouble(reqAry[9]);
				}
				if (YssFun.isNumeric(reqAry[10])) {
					this.strExRate3 = Double.parseDouble(reqAry[10]);
				}
				if (YssFun.isNumeric(reqAry[11])) {
					this.strExRate4 = Double.parseDouble(reqAry[11]);
				}
				if (YssFun.isNumeric(reqAry[12])) {
					this.strExRate5 = Double.parseDouble(reqAry[12]);
				}
				if (YssFun.isNumeric(reqAry[13])) {
					this.strExRate6 = Double.parseDouble(reqAry[13]);
				}
				if (YssFun.isNumeric(reqAry[14])) {
					this.strExRate7 = Double.parseDouble(reqAry[14]);
				}
				if (YssFun.isNumeric(reqAry[15])) {
					this.strExRate8 = Double.parseDouble(reqAry[15]);
				}

				// ------ modify by nimengjing 2010.12.02 BUG #535
				// 指数行情设置界面描述字段中存在回车符时，清除/还原报错
				//edit by songjie 2011.09.23 需求 1492 QDV4华安基金2011年8月10日01_A
				if (reqAry[16] != null && !reqAry[16].equalsIgnoreCase("null")) {
					if (reqAry[16].indexOf("【Enter】") >= 0) {
						this.strDesc = reqAry[16].replaceAll("【Enter】", "\r\n");
					} else {
						this.strDesc = reqAry[16];
					}
				}
				// ----------------- BUG #533 ----------------//
				this.checkStateId = Integer.parseInt(reqAry[17]);
				this.strOldExRateSrcCode = reqAry[18];
				this.strOldExRateDate = reqAry[19];
				this.strOldExRateTime = reqAry[20];
				this.strOldPortCode = reqAry[21];
				this.strOldCuryCode = reqAry[22];
				//edit by songjie 2011.09.23 需求 1492 QDV4华安基金2011年8月10日01_A
				this.strMarkCuryCode = reqAry[23].equals("null") ? " " : reqAry[23]; // 赋值基准货币代码 linjunyun 2008-11-20
													// bug:MS00011
				//edit by songjie 2011.09.23 需求 1492 QDV4华安基金2011年8月10日01_A
				this.strMarkCuryName = reqAry[24].equals("null") ? " " : reqAry[24]; // 赋值基准货币名称 linjunyun 2008-11-20
													// bug:MS00011
				this.strOldMarkCuryCode = reqAry[25]; // 赋值原基准货币名称 linjunyun
														// 2008-11-20 bug:MS00011
				this.isOnlyColumns = reqAry[26];
				this.sAssetGroupCode = reqAry[27]; // MS00131 新增字段
													// QDV4建行2008年12月25日01_A by
													// leeyu 20090205
				this.sOldAssetGroupCode = reqAry[28]; // MS00131 新增字段
														// QDV4建行2008年12月25日01_A by
														// leeyu 20090205
				this.sOldAssetGroupCode = reqAry[29];	//added by liubo.Story #1770
				this.Group=reqAry[30];//add baopingping #story 1167 接收前台传来是否进行跨组合操作
				this.Iscopy=reqAry[31];//add baopingping #story 1167 接收前台传来是否覆盖
				super.parseRecLog();
				if (sRowStr.indexOf("\r\t") >= 0) {
					if (this.filterType == null) {
						this.filterType = new ExchangeRateBean();
						this.filterType.setYssPub(pub);
					}
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			}
			//---edit by songjie 2011.09.15 # 1492 QDV4华安基金2011年8月10日01_A end---//


		} catch (Exception e) {
			throw new YssException("解析汇率行情设置请求出错", e);
		}
	}

	/**
	 * 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() throws YssException {
		String sResult = "";
		if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
				return " where 1=1";
		if (this.filterType != null) {
			sResult = " where 1=1 ";
			if (this.filterType.isOnlyColumns.equals("1")) {
				sResult = sResult + " and 1 = 2 ";
				return sResult;
			}
			if (this.filterType.strPortCode.length() != 0) {
				sResult = sResult + " and a.FPortCode like '"
						+ filterType.strPortCode.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.strExRateSrcCode.length() != 0) {
				sResult = sResult + " and a.FExRateSrcCode like '"
						+ filterType.strExRateSrcCode.replaceAll("'", "''")
						+ "%'";
			}
			if (this.filterType.strCuryCode.length() != 0) {
				sResult = sResult + " and a.FCuryCode like '"
						+ filterType.strCuryCode.replaceAll("'", "''") + "%'";
			}
			// 筛选基准货币代码 linjunyun 2008-11-20 bug:MS00011
			if (this.filterType.strMarkCuryCode.length() != 0) {
				sResult = sResult + " and a.FMarkCury like '"
						+ filterType.strMarkCuryCode.replaceAll("'", "''")
						+ "%'";
			}
			// -------------------lzp modify 2007 12.7
			if (!(this.filterType.strExRate1 == 0)) {
				sResult = sResult + " and a.FExRate1 = "
						+ filterType.strExRate1;
			}
			if (!(this.filterType.strExRate2 == 0)) {
				sResult = sResult + " and a.FExRate2 = "
						+ filterType.strExRate2;
			}

			if (!(this.filterType.strExRate3 == 0)) {
				sResult = sResult + " and a.FExRate3 = "
						+ filterType.strExRate3;
			}
			if (!(this.filterType.strExRate4 == 0)) {
				sResult = sResult + " and a.FExRate4 = "
						+ filterType.strExRate4;
			}
			if (!(this.filterType.strExRate5 == 0)) {
				sResult = sResult + " and a.FExRate5 = "
						+ filterType.strExRate5;
			}
			if (!(this.filterType.strExRate6 == 0)) {
				sResult = sResult + " and a.FExRate6 = "
						+ filterType.strExRate6;
			}
			if (!(this.filterType.strExRate7 == 0)) {
				sResult = sResult + " and a.FExRate7 = "
						+ filterType.strExRate7;
			}
			if (!(this.filterType.strExRate8 == 0)) {
				sResult = sResult + " and a.FExRate8 = "
						+ filterType.strExRate8;
			}
			// ---------------------------
			if (this.filterType.strExRateDate.length() != 0
					&& !this.filterType.strExRateDate.equals("9998-12-31")) {
				sResult = sResult + " and FExRateDate = "
						+ dbl.sqlDate(filterType.strExRateDate);
			}
			if (this.filterType.strExRateTime.length() != 0
					&& !this.filterType.strExRateTime.equals("00:00:00")) {
				sResult = sResult + " and FExRateTime = "
						+ dbl.sqlString(filterType.strExRateTime);
			}
			if (((String) pub.getHtPubParams().get("exchangerate"))
					.equalsIgnoreCase("true")) { // 当通用参数传过来的值为真时再给这个变量赋值
				if (this.filterType.sAssetGroupCode.trim().length() != 0) {
					sResult = sResult + " and a.FASSETGROUPCODE ='"
							+ filterType.sAssetGroupCode.replaceAll("'", "''")
							+ "'";
				}
			} // QDV4建行2008年12月25日01_A MS00131 by leeyu 20090204

		}
		return sResult;
	}

	/**
	 * saveMutliOperData
	 * 
	 * @param sMutilRowStr
	 *            String
	 * @return String
	 */
	public String saveMutliOperData(String sMutilRowStr) {
		return "";
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException,
			YssException {
		this.strExRateSrcCode = rs.getString("FExRateSrcCode") + "";
		this.strExRateSrcName = rs.getString("FExRateSrcName") + "";
		this.strPortCode = rs.getString("FPortCode") + "";
		this.strPortName = rs.getString("FPortName") + "";
		this.strCuryCode = rs.getString("FCuryCode") + "";
		this.strCuryName = rs.getString("FCuryName") + "";
		this.strMarkCuryCode = rs.getString("FMarkCury") + ""; // 获取基准货币代码
																	// linjunyun
																	// 2008-11-20
																	// bug:MS00011
		this.strMarkCuryName = rs.getString("FMarkCuryName") + ""; // 获取基准货币名称
																	// linjunyun
																	// 2008-11-20
																	// bug:MS00011
		this.strExRateDate = rs.getDate("FExRateDate") + "";
		this.strExRateTime = rs.getString("FExRateTime") + "";
		this.strExRate1 = rs.getDouble("FExRate1");
		this.strExRate2 = rs.getDouble("FExRate2");
		this.strExRate3 = rs.getDouble("FExRate3");
		this.strExRate4 = rs.getDouble("FExRate4");
		this.strExRate5 = rs.getDouble("FExRate5");
		this.strExRate6 = rs.getDouble("FExRate6");
		this.strExRate7 = rs.getDouble("FExRate7");
		this.strExRate8 = rs.getDouble("FExRate8");
		this.strDesc = rs.getString("FDesc") + "";
		this.strDataSource = rs.getString("FDATASOURCE") +""; //add by jiangshichao STORY #1244 导入汇率时多组合情况下，以组合代码为空的汇率为默认的汇率，导入数据中心相关需求
		
		//modified by liubo.Story #1770
		//==============================
//		if (((String) pub.getHtPubParams().get("exchangerate"))
//				.equalsIgnoreCase("true")) { // 当通用参数传过来的值为真时再给这个变量赋值
//			this.sAssetGroupCode = rs.getString("FASSETGROUPCODE"); // QDV4建行2008年12月25日01_A
//																	// MS00131
//																	// by leeyu
//																	// 20090205
//		}
		this.sAssetGroupCode = rs.getString("FASSETGROUPCODE");
		//==============================
		super.setRecLog(rs);

	}

	/**
	 * getOperValue
	 * 
	 * @param sType
	 *            String
	 * @return String
	 * @throws YssException 
	 */
	public String getOperValue(String sType) throws YssException {
		if (sType != null && sType.equalsIgnoreCase("getPubParamExchangeRate")) {
			return (String) pub.getHtPubParams().get("exchangerate"); // 获取行情跨组合群处理的通用参数的数据
		}
		//---add by songjie 2011.09.15 #1492 QDV4华安基金2011年8月10日01_A start---//
		if (sType != null && sType.equalsIgnoreCase("multauditExchgRate")){
			
			if (multAuditString.length() > 0) {
				//---edit by songjie 2011.11.22 BUG 2953 QDV4赢时胜(测试)2011年10月14日01_B 实现跨组合群批量审核反审核删除汇率数据功能 start---//
				if(this.Group.equalsIgnoreCase("ok")){
					String assetGroupCode=this.getAssdeGroup();
			    	String[] GroupCode=null;
					if(assetGroupCode!=null)
					{
					   GroupCode=assetGroupCode.split("\t");
					   for(int i=0;i<GroupCode.length;i++){
						   this.auditData(this.multAuditString,GroupCode[i]);// 跨组合执行批量审核/反审核
					   }
					}
				}else{
					return this.auditMutli(this.multAuditString);
				}
				//---edit by songjie 2011.11.22 BUG 2953 QDV4赢时胜(测试)2011年10月14日01_B end---//
			}
		}
		//---add by songjie 2011.09.15 #1492 QDV4华安基金2011年8月10日01_A end---//
		return "";
	}
	
	/**
	 * add by songjie 2011.11.22
	 * BUG 2953 QDV4赢时胜(测试)2011年10月14日01_B
	 * 实现跨组合群批量审核反审核汇率数据的功能
	 * @param sMutilRowStr
	 * @param assetGroupCode
	 * @return
	 * @throws YssException
	 */
	public void  auditData(String sMutilRowStr,String assetGroupCode) throws YssException{
		Connection conn = dbl.loadConnection();
		ExchangeRateBean data = null;
		boolean bTrans = false;
		String[] multAudit = null;
		String strSql = "";
		PreparedStatement psmt = null;
		try {
			if (multAuditString.length() > 0) {
				multAudit = sMutilRowStr.split("\f\f\f\f");
				if (multAudit.length > 0) {
					if(!dbl.yssTableExist("Tb_" + assetGroupCode + "_Data_Exchangerate")){
						return;
					}
					strSql = "update Tb_" + assetGroupCode + "_Data_Exchangerate" + 
					//edit by songjie 2011.12.19 BUG 3416 QDV4华宝兴业2011年12月15日01_B 设置审核人 和 审核时间
					" set FCheckState = " + this.checkStateId + ", FCHECKUSER = ? , FCHECKTIME = ?" +
					" where FExRateSrcCode = ? and FCuryCode = ? and FMarkCury = ? and FExRateDate = ? " +
					" and FExRateTime = ? and FPortCode = ? ";
					
					psmt = conn.prepareStatement(strSql);
					
					for (int i = 0; i < multAudit.length; i++) {
						data = new ExchangeRateBean();
						data.setYssPub(pub);
						data.parseRowStr(multAudit[i]);
						//---edit by songjie 2011.12.19 BUG 3416 QDV4华宝兴业2011年12月15日01_B 设置审核人 和 审核时间 start---//
						psmt.setString(1, this.creatorCode);
						psmt.setString(2, this.creatorTime);
						psmt.setString(3, data.strExRateSrcCode);
						psmt.setString(4, data.strCuryCode);
						psmt.setString(5, data.strMarkCuryCode);
						psmt.setDate(6, YssFun.toSqlDate(data.strExRateDate));
						psmt.setString(7, data.strExRateTime);
						psmt.setString(8, data.strPortCode.equals("")? " " : data.strPortCode);
						//---edit by songjie 2011.12.19 BUG 3416 QDV4华宝兴业2011年12月15日01_B end---//
						
						psmt.addBatch();
						
						logOper = SingleLogOper.getInstance();
						data = this;
						if (this.checkStateId == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (this.checkStateId == 1) {
							data.checkStateId = 1;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						} else if (this.checkStateId == 0) {
							data.checkStateId = 0;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						}
					}
					
					conn.setAutoCommit(false);
					bTrans = true;
					
					psmt.executeBatch();
					
					conn.commit();
					bTrans = false;
					conn.setAutoCommit(true);
				}
			}
		} catch (Exception e) {
			throw new YssException("跨组合群批量审核反审核汇率数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeStatementFinal(psmt);
		}
	}
	
	/**
	 * add by songjie 2011.11.22
	 * BUG 2953 QDV4赢时胜(测试)2011年10月14日01_B
	 * 获取所有组合群代码
	 * @return
	 * @throws YssException
	 */
	public String getAssdeGroup() throws YssException{
		ResultSet rs=null;
		String sql=null;
		String assetGroupCode="";
		try{
			sql="select * from Tb_Sys_AssetGroup order by FAssetGroupCode";
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				assetGroupCode+=rs.getString("FAssetGroupCode")+"\t";
			}
			if(assetGroupCode.length() > 2){
				assetGroupCode = assetGroupCode.substring(0, assetGroupCode.length() - 1);
			}
			return assetGroupCode;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2011.09.15 
	 * #1492 QDV4华安基金2011年8月10日01_A
	 * 批量审核、反审核汇率数据
	 * @param sMutilRowStr
	 * @return
	 * @throws YssException
	 */
	public String auditMutli(String sMutilRowStr) throws YssException {
		Connection conn = null;
		boolean bTrans = false;
		String strSql = "";
//		PreparedStatement psmt = null;
		String[] multAudit = null;
		ExchangeRateBean data = null;
		try{
//			conn = dbl.loadConnection();
//			strSql = "update " + pub.yssGetTableName("Tb_Data_Exchangerate") + 
//			" set FCheckState = " + this.checkStateId + 
//			" where FExRateSrcCode = ? and FCuryCode = ? and FMarkCury = ? and FExRateDate = ? " +
//			" and FExRateTime = ? and FPortCode = ? ";
			
//			psmt = conn.prepareStatement(strSql);
			if (multAuditString.length() > 0) {
				multAudit = sMutilRowStr.split("\f\f\f\f");
				if (multAudit.length > 0) {
					for (int i = 0; i < multAudit.length; i++) {
						data = new ExchangeRateBean();
						data = this;
						data.setYssPub(pub);
						data.parseRowStr(multAudit[i]);
						String[] sGroupCode = ("".equals(data.getSAssetGroupCode().trim()) ? pub.getAssetGroupCode() : data.getSAssetGroupCode()).split(",");
						for (int j = 0;j<sGroupCode.length;j++)
						{
							strSql = "update " + "Tb_" + sGroupCode[j] + "_Data_Exchangerate " + 
							" set FCheckState = " + this.checkStateId + 
							//---add by songjie 2011.12.19 BUG 3416 QDV4华宝兴业2011年12月15日01_B 设置审核人 和 审核时间 start---//
							" , FCHECKUSER = " + dbl.sqlString(this.creatorCode) + 
							" , FCHECKTIME = " + dbl.sqlString(this.creatorTime) +
							//---add by songjie 2011.12.19 BUG 3416 QDV4华宝兴业2011年12月15日01_B end---//
							" where FExRateSrcCode = " + dbl.sqlString(data.strExRateSrcCode) +
							" and FCuryCode = " + dbl.sqlString(data.strCuryCode) + 
							" and FMarkCury = " + dbl.sqlString(data.strMarkCuryCode) + 
							" and FExRateDate = " + dbl.sqlDate(data.strExRateDate) + 
							" and FExRateTime = " + dbl.sqlString(data.strExRateTime) + 
							" and FPortCode = " + dbl.sqlString(data.strPortCode.equals("")? " " : data.strPortCode);
							
							dbl.executeSql(strSql);
							
//						psmt.setString(1, data.strExRateSrcCode);
//						psmt.setString(2, data.strCuryCode);
//						psmt.setString(3, data.strMarkCuryCode);
//						psmt.setDate(4, YssFun.toSqlDate(data.strExRateDate));
//						psmt.setString(5, data.strExRateTime);
//						psmt.setString(6, data.strPortCode.equals("")? " " : data.strPortCode);
						}
						
//						psmt.addBatch();
					}
				}
			}
			
//			conn.setAutoCommit(false);
//			bTrans = true;
			
//			psmt.executeBatch();
			
//			conn.commit();
//			bTrans = false;
//			conn.setAutoCommit(true);
			
		}catch(YssException e){
			throw new YssException("批量审核或反审核汇率数据出错!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new YssException("批量审核或反审核汇率数据出错!");
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
		return "";
	}

	/**
	 * getBeforeEditData
	 * 
	 * @return String
	 */
	public String getBeforeEditData() throws YssException {
		ExchangeRateBean befEditBean = new ExchangeRateBean();
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, "
					+ " g.FCuryName,f.FExRateSrcName,h.FPortName as FPortName,h.FStartDate as FStartDate,"
					+
					// 设置基准货币的货币代码别名FMarkCuryCode，货币名称的别名FMarkCuryName linjunyun
					// 2008-11-20 bug:MS00011
					" m.FCuryCode as FMarkCuryCode, m.FCuryName as FMarkCuryName"
					+ " from "
					+ pub.yssGetTableName("Tb_Data_ExchangeRate")
					+ " a "
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
					+ " left join (select FCuryCode,FCuryName from "
					+ pub.yssGetTableName("tb_para_currency")
					+ " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode"
					+
					// 获取基准货币的货币代码和货币名称 linjunyun 2008-11-20 bug:MS00011
					" left join (select FCuryCode,FCuryName from "
					+ pub.yssGetTableName("tb_para_currency")
					+ " where FCheckState = 1) m on a.FMarkCury = m.FCuryCode"
					+
					// ************************//
					" left join (select FExRateSrcCode,FExRateSrcName from "
					+ pub.yssGetTableName("Tb_Para_ExRateSource")
					+ " ) f on a.FExRateSrcCode = f.FExRateSrcCode "
					+ " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
					//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//					+ pub.yssGetTableName("tb_para_portfolio")
//					+ " "
//					+ " where FStartDate <= "
//					+ dbl.sqlDate(new java.util.Date())
//					+ " and FCheckState = 1 group by FPortCode )u "
//					+ " join (select * from "
					//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
					+ pub.yssGetTableName("tb_para_portfolio")
					+ " v where FCheckState = 1) h on a.FPortCode = h.FPortCode"//edit by songjie 2011.03.16 不以最大的启用日期查询数据
					+ " where a.FExRateSrcCode ="
					+ dbl.sqlString(this.strOldExRateSrcCode)
					+ " and  a.FCuryCode="
					+ dbl.sqlString(this.strOldCuryCode)
					+ " and  a.FMarkCury="
					+ dbl.sqlString(this.strOldMarkCuryCode)
					+ // 原基准货币代码作为主键条件查询 linjunyun 2008-11-20 bug:MS00011
					" and  a.FExRateDate="
					+ dbl.sqlDate(this.strOldExRateDate)
					+ " and  a.FExRateTime="
					+ dbl.sqlString(this.strOldExRateTime)
					+ (this.strOldPortCode.length() > 0 ? "and a.FPortCode="
							+ dbl.sqlString(this.strOldPortCode) : "and 1=1")
					+ " order by a.FCheckState, a.FCreateTime desc";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				befEditBean.strExRateSrcCode = rs.getString("FExRateSrcCode")
						+ "";
				befEditBean.strExRateSrcName = rs.getString("FExRateSrcName")
						+ "";
				befEditBean.strPortCode = rs.getString("FPortCode") + "";
				befEditBean.strPortName = rs.getString("FPortName") + "";
				befEditBean.strCuryCode = rs.getString("FCuryCode") + "";
				befEditBean.strCuryName = rs.getString("FCuryName") + "";
				befEditBean.strMarkCuryCode = rs.getString("FMarkCuryCode")
						+ ""; // 获取基准货币代码 linjunyun 2008-11-20 bug:MS00011
				befEditBean.strMarkCuryName = rs.getString("FMarkCuryName")
						+ ""; // 获取基准货币名称 linjunyun 2008-11-20 bug:MS00011
				befEditBean.strExRateDate = rs.getDate("FExRateDate") + "";
				befEditBean.strExRateTime = rs.getString("FExRateTime") + "";
				befEditBean.strExRate1 = rs.getDouble("FExRate1");
				befEditBean.strExRate2 = rs.getDouble("FExRate2");
				befEditBean.strExRate3 = rs.getDouble("FExRate3");
				befEditBean.strExRate4 = rs.getDouble("FExRate4");
				befEditBean.strExRate5 = rs.getDouble("FExRate5");
				befEditBean.strExRate6 = rs.getDouble("FExRate6");
				befEditBean.strExRate7 = rs.getDouble("FExRate7");
				befEditBean.strExRate8 = rs.getDouble("FExRate8");
				befEditBean.strDesc = rs.getString("FDesc") + "";

			}
			return befEditBean.buildRowStr();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	/**
	 * getListViewData4
	 * 
	 * @return String
	 */
	public String getListViewData4() {
		return "";
	}

	/**
	 * saveMutliSetting
	 * add baopingping 20110715 #story 1167
	 * 跨组合 添加，删除，修改，复制，审核的入口
	 * @param sMutilRowStr
	 * @return String
	 * @throws YssException
	 */
	public String saveMutliSetting(String flag) throws YssException {
		MarketValueBean Mark =new MarketValueBean();
		Mark.setYssPub(pub);
		String msg="";
		//modified by liubo.Story #1770
		//================================
//		String FAssetGroupCode=Mark.getAssdeGroup();
    	String[] GroupCode=null;
//		if(FAssetGroupCode!=null)
//		{
//		   GroupCode=FAssetGroupCode.split("\t");
//		}
    	
    	if (flag.equalsIgnoreCase("copy")||flag.equalsIgnoreCase("add")){
    		checkInput("".equals(this.sAssetGroupCode.trim()) ? YssCons.OP_ADD : YssCons.OP_MutliAdd);
    	}else if(flag.equalsIgnoreCase("edit")){
    		checkInput("".equals(this.sAssetGroupCode.trim()) ? YssCons.OP_EDIT : YssCons.OP_MutliEdit);
    	}
    	GroupCode = ("".equals(this.sAssetGroupCode.trim()) ? pub.getAssetGroupCode() : this.sAssetGroupCode).split(",");
    	//===============end==================
		if(GroupCode == null){
			this.addData(pub.getAssetGroupCode());
		}else{
			for(int i=0;i<GroupCode.length;i++)
			{
				String tableName=this.getTable(GroupCode[i]);
				if(flag.equalsIgnoreCase("add")){
					if(this.Iscopy.equalsIgnoreCase("yes")){
					if(tableName.equalsIgnoreCase("none")){
						this.addData(GroupCode[i]);
					}else{
						this.editData(GroupCode[i]);
					}
					}else{
						if(tableName.equalsIgnoreCase("none")){
						      this.addData(GroupCode[i]);
							}
					}
				}else if(flag.equalsIgnoreCase("edit")){
					this.editData(GroupCode[i]);
			    }else if(flag.equalsIgnoreCase("del")){
			    	this.delData(GroupCode[i]);
			    }else if(flag.equalsIgnoreCase("audit")){
			    	this.checkData(GroupCode[i]);
			    }else if(flag.equalsIgnoreCase("copy")){
					if(this.Iscopy.equalsIgnoreCase("yes")){
						if(tableName.equalsIgnoreCase("none")){
							this.copyData(GroupCode[i]);
						
						}else{
							this.editData(GroupCode[i]);
						}
					}else{
						if(tableName.equalsIgnoreCase("none")){
					      this.copyData(GroupCode[i]);
						}else{
							//this.editData(GroupCode[i]);
						}
					}
				}else if(flag.equalsIgnoreCase("checked")){
					
					msg+=this.getGroup(GroupCode[i]);
				}
			}
		}
		
		if(msg.endsWith(",")){
			msg = msg.substring(0, msg.length()-1);
		}
		return msg;
	}

	/**
	 * 返回汇率数据实体 getSetting MS00709,交易数据接口汇率提示,QDV4赢时胜上海2009年9月23日01_A by
	 * xuxuming,20090924
	 * 
	 * @return IDataSetting
	 */
	public IDataSetting getSetting() throws YssException {
		ResultSet rs = null;
		String strSql = "";
		try {
			strSql = "SELECT * FROM "
					+ pub.yssGetTableName("Tb_Data_ExchangeRate") + " a "
					+ buildFilterSql() + " and a.FCheckState = 1";// 查询 已审核 的记录。
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				this.strExRateSrcCode = rs.getString("FExRateSrcCode") + "";
				this.strCuryCode = rs.getString("FCuryCode") + "";
				this.strPortCode = rs.getString("FPortCode") + "";
			}
		} catch (Exception e) {
			throw new YssException("获取汇率数据信息出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return this;

	}

	/**
	 * getAllSetting
	 * 
	 * @return String
	 */
	public String getAllSetting() {
		return "";
	}

	/**
	 * 从回收站删除数据 即从数据库彻底删除数据
	 * 
	 * @throws YssException
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		boolean bIsExchangeRatePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
		// 获取一个连接
		Connection conn = dbl.loadConnection();
		//add by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A
		String[] assetGroupCodes = null;//保存组合群数组
		try {
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != "" && sRecycled != null) {
				// 根据规定的符号，把多个sql语句分别放入数组
				arrData = sRecycled.split("\r\n");
				conn.setAutoCommit(false);
				bTrans = true;
				// 循环执行这些删除语句
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
					if(this.sAssetGroupCode.trim().length() > 0){//添加跨组合群清除功能
						assetGroupCodes = this.sAssetGroupCode.trim().split(",");
						for(int j = 0; j < assetGroupCodes.length; j++){
							strSql = "delete from "+ (bIsExchangeRatePub ? "Tb_Base_ExchangeRate" : 
								"Tb_" + assetGroupCodes[j] + "_Data_ExchangeRate")
								+ // 根据参数决定到底用哪张表 MS00131 QDV4建行2008年12月25日01_A by leeyu 20090205
								" where FExRateSrcCode = " + dbl.sqlString(this.strExRateSrcCode)
								+ " and FPortCode = " + dbl.sqlString(this.strPortCode.length() == 0 ? " ": this.strPortCode)
								+ " and FCuryCode = " + dbl.sqlString(this.strCuryCode)
								+ " and FMarkCury = " + dbl.sqlString("null".equalsIgnoreCase(this.strMarkCuryCode) ? " ": this.strMarkCuryCode)
								// 添加基准货币代码的主键判断 linjunyun 2008-11-20 bug:MS00011
								+ " and FExRateTime = " + dbl.sqlString(this.strExRateTime)
								+ " and FExRateDate = " + dbl.sqlDate(this.strExRateDate)
								+ (bIsExchangeRatePub ? (" and FASSETGROUPCODE = " 
								// 添加上FASSETGROUPCODE主键字段 QDV4建行2008年12月25日01_A MS00131 by leeyu
								+ dbl.sqlString(assetGroupCodes[j].trim().length() > 0 ? assetGroupCodes[j]: " ")): " "); 
												
							// 执行sql语句
							dbl.executeSql(strSql);	
						}
					}else{
						strSql = "delete from "+ (bIsExchangeRatePub ? "Tb_Base_ExchangeRate" : 
							pub.yssGetTableName("Tb_Data_ExchangeRate"))
							+ // 根据参数决定到底用哪张表 MS00131 QDV4建行2008年12月25日01_A by leeyu 20090205
							" where FExRateSrcCode = " + dbl.sqlString(this.strExRateSrcCode)
							+ " and FPortCode = " + dbl.sqlString(this.strPortCode.length() == 0 ? " ": this.strPortCode)
							+ " and FCuryCode = " + dbl.sqlString(this.strCuryCode)
							+ " and FMarkCury = " + dbl.sqlString("null".equalsIgnoreCase(this.strMarkCuryCode) ? " ": this.strMarkCuryCode)
							// 添加基准货币代码的主键判断 linjunyun 2008-11-20 bug:MS00011
							+ " and FExRateTime = " + dbl.sqlString(this.strExRateTime)
							+ " and FExRateDate = " + dbl.sqlDate(this.strExRateDate)
							+ (bIsExchangeRatePub ? (" and FASSETGROUPCODE = " 
							// 添加上FASSETGROUPCODE主键字段 QDV4建行2008年12月25日01_A MS00131 by leeyu
							+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode: " ")): " "); 
											
						// 执行sql语句
						dbl.executeSql(strSql);	
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
				}
			}
			// sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
			else if (strExRateSrcCode != "" && strExRateSrcCode != null) {
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
				if(this.sAssetGroupCode.trim().length() > 0){//添加跨组合群清除功能
					assetGroupCodes = this.sAssetGroupCode.trim().split(",");
					for(int j = 0; j < assetGroupCodes.length; j++){
						strSql = "delete from " + (bIsExchangeRatePub ? "Tb_Base_ExchangeRate" : 
						    "Tb_" + assetGroupCodes[j] + "_Data_ExchangeRate")
							// 根据参数决定到底用哪张表 MS00131 QDV4建行2008年12月25日01_A by leeyu 20090205
							+ " where FExRateSrcCode = " + dbl.sqlString(this.strExRateSrcCode)
							+ " and FPortCode = " + dbl.sqlString(this.strPortCode.length() == 0 ? " ": this.strPortCode)
							+ " and FCuryCode = " + dbl.sqlString(this.strCuryCode)
							+ " and FMarkCury = " + dbl.sqlString("null".equalsIgnoreCase(this.strMarkCuryCode) ? " ": this.strMarkCuryCode)
							// 添加基准货币代码的主键判断 linjunyun 2008-11-20 bug:MS00011
							+ " and FExRateTime = " + dbl.sqlString(this.strExRateTime)
							+ " and FExRateDate = " + dbl.sqlDate(this.strExRateDate)
							+ (bIsExchangeRatePub ? (" and FASSETGROUPCODE=" 
							// 添加上FASSETGROUPCODE主键字段 QDV4建行2008年12月25日01_A MS00131 by leeyu
							+ dbl.sqlString(assetGroupCodes[j].trim().length() > 0 ? assetGroupCodes[j]: " ")): " "); 				
											
						// 执行sql语句
						dbl.executeSql(strSql);						
					}
				}else{
					strSql = "delete from " + (bIsExchangeRatePub ? "Tb_Base_ExchangeRate" : 
					    pub.yssGetTableName("Tb_Data_ExchangeRate"))
						// 根据参数决定到底用哪张表 MS00131 QDV4建行2008年12月25日01_A by leeyu 20090205
						+ " where FExRateSrcCode = " + dbl.sqlString(this.strExRateSrcCode)
						+ " and FPortCode = " + dbl.sqlString(this.strPortCode.length() == 0 ? " ": this.strPortCode)
						+ " and FCuryCode = " + dbl.sqlString(this.strCuryCode)
						+ " and FMarkCury = " + dbl.sqlString("null".equalsIgnoreCase(this.strMarkCuryCode) ? " ": this.strMarkCuryCode)
						// 添加基准货币代码的主键判断 linjunyun 2008-11-20 bug:MS00011
						+ " and FExRateTime = " + dbl.sqlString(this.strExRateTime)
						+ " and FExRateDate = " + dbl.sqlDate(this.strExRateDate)
						+ (bIsExchangeRatePub ? (" and FASSETGROUPCODE=" 
						// 添加上FASSETGROUPCODE主键字段 QDV4建行2008年12月25日01_A MS00131 by leeyu
						+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode: " ")): " "); 				
										
					// 执行sql语句
					dbl.executeSql(strSql);					
				}
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("清除数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
	 * 
	 * @param sAssetGroupCode
	 *            String
	 */
	public void setSAssetGroupCode(String sAssetGroupCode) {
		this.sAssetGroupCode = sAssetGroupCode;
	}

	/**
	 * //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
	 * 
	 * @param sOldAssetGroupCode
	 *            String
	 */
	public void setSOldAssetGroupCode(String sOldAssetGroupCode) {
		this.sOldAssetGroupCode = sOldAssetGroupCode;
	}

	// ---------MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie
	// 2009.04.11-------------//
	/**
	 * strPortCode的set方法
	 * 
	 * @param strPortCode
	 *            String
	 */
	public void setStrPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}

	/**
	 * strMarkCuryCode的set方法
	 * 
	 * @param strMarkCuryCode
	 *            String
	 */
	public void setStrMarkCuryCode(String strMarkCuryCode) {
		this.strMarkCuryCode = strMarkCuryCode;
	}

	/**
	 * strExRateTime的set方法
	 * 
	 * @param strExRateTime
	 *            String
	 */
	public void setStrExRateTime(String strExRateTime) {
		this.strExRateTime = strExRateTime;
	}

	/**
	 * strExRateSrcCode的set方法
	 * 
	 * @param strExRateSrcCode
	 *            String
	 */
	public void setStrExRateSrcCode(String strExRateSrcCode) {
		this.strExRateSrcCode = strExRateSrcCode;
	}

	/**
	 * strExRateDate的set方法
	 * 
	 * @param strExRateDate
	 *            String
	 */
	public void setStrExRateDate(String strExRateDate) {
		this.strExRateDate = strExRateDate;
	}

	/**
	 * strExRate8的set方法
	 * 
	 * @param strExRate8
	 *            double
	 */
	public void setStrExRate8(double strExRate8) {
		this.strExRate8 = strExRate8;
	}

	/**
	 * strExRate7的set方法
	 * 
	 * @param strExRate7
	 *            double
	 */
	public void setStrExRate7(double strExRate7) {
		this.strExRate7 = strExRate7;
	}

	/**
	 * strExRate6的set方法
	 * 
	 * @param strExRate6
	 *            double
	 */
	public void setStrExRate6(double strExRate6) {
		this.strExRate6 = strExRate6;
	}

	/**
	 * strExRate5的set方法
	 * 
	 * @param strExRate5
	 *            double
	 */
	public void setStrExRate5(double strExRate5) {
		this.strExRate5 = strExRate5;
	}

	/**
	 * strExRate4的set方法
	 * 
	 * @param strExRate4
	 *            double
	 */
	public void setStrExRate4(double strExRate4) {
		this.strExRate4 = strExRate4;
	}

	/**
	 * strExRate3的set方法
	 * 
	 * @param strExRate3
	 *            double
	 */
	public void setStrExRate3(double strExRate3) {
		this.strExRate3 = strExRate3;
	}

	/**
	 * strExRate2的set方法
	 * 
	 * @param strExRate2
	 *            double
	 */
	public void setStrExRate2(double strExRate2) {
		this.strExRate2 = strExRate2;
	}

	/**
	 * strExRate1的set方法
	 * 
	 * @param strExRate1
	 *            double
	 */
	public void setStrExRate1(double strExRate1) {
		this.strExRate1 = strExRate1;
	}

	/**
	 * strDesc的set方法
	 * 
	 * @param strDesc
	 *            String
	 */
	public void setStrDesc(String strDesc) {
		this.strDesc = strDesc;
	}

	/**
	 * strCuryCode的set方法
	 * 
	 * @param strCuryCode
	 *            String
	 */
	public void setStrCuryCode(String strCuryCode) {
		this.strCuryCode = strCuryCode;
	}

	// ---------MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie
	// 2009.04.11-------------//

	/**
	 * //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
	 * 
	 * @return String
	 */
	public String getSAssetGroupCode() {
		return sAssetGroupCode;
	}

	/**
	 * //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
	 * 
	 * @return String
	 */
	public String getSOldAssetGroupCode() {
		return sOldAssetGroupCode;
	}

	// ---------MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie
	// 2009.04.11-------------//
	/**
	 * strPortCode的get方法
	 * 
	 * @return String
	 */
	public String getStrPortCode() {
		return strPortCode;
	}

	/**
	 * strMarkCuryCode的get方法
	 * 
	 * @return String
	 */
	public String getStrMarkCuryCode() {
		return strMarkCuryCode;
	}

	/**
	 * strExRateTime的get方法
	 * 
	 * @return String
	 */
	public String getStrExRateTime() {
		return strExRateTime;
	}

	/**
	 * strExRateSrcCode的get方法
	 * 
	 * @return String
	 */
	public String getStrExRateSrcCode() {
		return strExRateSrcCode;
	}

	/**
	 * strExRateDate的get方法
	 * 
	 * @return String
	 */
	public String getStrExRateDate() {
		return strExRateDate;
	}

	/**
	 * strExRate8的get方法
	 * 
	 * @return double
	 */
	public double getStrExRate8() {
		return strExRate8;
	}

	/**
	 * strExRate7的get方法
	 * 
	 * @return double
	 */
	public double getStrExRate7() {
		return strExRate7;
	}

	/**
	 * strExRate6的get方法
	 * 
	 * @return double
	 */
	public double getStrExRate6() {
		return strExRate6;
	}

	/**
	 * strExRate5的get方法
	 * 
	 * @return double
	 */
	public double getStrExRate5() {
		return strExRate5;
	}

	/**
	 * strExRate4的get方法
	 * 
	 * @return double
	 */
	public double getStrExRate4() {
		return strExRate4;
	}

	/**
	 * strExRate3的get方法
	 * 
	 * @return double
	 */
	public double getStrExRate3() {
		return strExRate3;
	}

	/**
	 * strExRate2的get方法
	 * 
	 * @return double
	 */
	public double getStrExRate2() {
		return strExRate2;
	}

	/**
	 * strExRate1的get方法
	 * 
	 * @return double
	 */
	public double getStrExRate1() {
		return strExRate1;
	}

	/**
	 * strDesc的get方法
	 * 
	 * @return String
	 */
	public String getStrDesc() {
		return strDesc;
	}

	/**
	 * strCuryCode的get方法
	 * 
	 * @return String
	 */
	public String getStrCuryCode() {
		return strCuryCode;
	}

	public String getTreeViewGroupData1() throws YssException {
		return "";
	}

	public String getTreeViewGroupData2() throws YssException {
		return "";
	}

	public String getTreeViewGroupData3() throws YssException {
		return "";
	}

	public String getListViewGroupData1() throws YssException {
		return "";
	}

	public String getListViewGroupData2() throws YssException {
		return "";
	}

	public String getListViewGroupData3() throws YssException {
		return "";
	}

	public String getListViewGroupData4() throws YssException {
		return "";
	}

	public String getListViewGroupData5() throws YssException {
		return "";
	}
	// ---------MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie
	// 2009.04.11-------------//
	/**
	 * add baopingping #story 1167 20110717
	 * 给所有组合群下添加一条行情数据
	 * return ResultSet
	 * @throws YssException 
	 */
	private String addData(String FAssetGroupCode) throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		boolean bIsExchangeRatePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
		String strSql = "";
		try {
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			strSql =
			// "insert into " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
			// "(FExRateSrcCode, FCuryCode,FMarkCury,FExRateDate,FExRateTime,FPortCode,FExRate1,FExRate2, FExRate3,FExRate4,"
			// +
			// " FExRate5, FExRate6,FExRate7,FExRate8,FDesc,FDataSource,FCheckState, FCreator, FCreateTime,FCheckUser) "
			// +
				"insert into "
				+ ("Tb_"+FAssetGroupCode+"_Data_ExchangeRate")
				+ // 根据参数决定是用哪张表 MS00131 by leeyu
				"(FExRateSrcCode, FCuryCode,FMarkCury,FExRateDate,FExRateTime,FPortCode,FExRate1,FExRate2, FExRate3,FExRate4,"
				+ " FExRate5, FExRate6,FExRate7,FExRate8,FDesc,FDataSource,FCheckState, FCreator, FCreateTime,FCheckUser"
				+ ", FAssetGroupCode" //added by liubo.Story #1770
				
				+ ") "
				+ // 若表为新表则添加上FASSETGROUPCODE字段
				" values("
				+ dbl.sqlString(this.strExRateSrcCode)
				+ ","
				+ dbl.sqlString(this.strCuryCode)
				+ ","
				+ dbl.sqlString(this.strMarkCuryCode)
				+ ","
				+ // 插入基准货币代码 linjunyun 2008-11-20 bug:MS00011
				dbl.sqlDate(this.strExRateDate)
				+ ","
				+ dbl.sqlString(this.strExRateTime)
				+ ","
				+ dbl.sqlString(this.strPortCode.length() == 0 ? " "
						: this.strPortCode)
				+ ","
				+
				// lzp modify 2007 12.7
				this.strExRate1
				+ ","
				+ this.strExRate2
				+ ","
				+ this.strExRate3
				+ ","
				+ this.strExRate4
				+ ","
				+ this.strExRate5
				+ ","
				+ this.strExRate6
				+ ","
				+ this.strExRate7
				+ ","
				+ this.strExRate8
				+ ","
				+
				// -------------
				dbl.sqlString(this.strDesc)
				+ ","
				+ 0
				+ ","
				+ (pub.getSysCheckState() ? "0" : "1")
				+ ","
				+ dbl.sqlString(this.creatorCode)
				+ ", "
				+ dbl.sqlString(this.creatorTime)
				+ ","
				+ (pub.getSysCheckState() ? "' '" : dbl
						.sqlString(this.creatorCode))
				+ "," + dbl.sqlString(this.sAssetGroupCode) +	//added by liubo.Story #1770
			
				")";
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return buildRowStr();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new YssException("新增汇率行情设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

		
	}
	
	
	/**
	 * add baopingping #story 1167 20110717
	 * 修改所有组合群下所选数据主键相同的行情数据
	 * return ResultSet
	 * @throws YssException 
	 */
	private String editData(String FAssetGroupCode) throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		boolean bIsExchangeRatePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
		String[] sOldAssetGroup = null;
        String[] sNewAssetGroup = null;
		String strSql = "";
		String sTmpAssetGroupCode = "";
		try {
			if (!sOldAssetGroupCode.equals(sAssetGroupCode))
       	 {
       		 sOldAssetGroup = ("".equals(sOldAssetGroupCode.trim()) ? pub.getAssetGroupCode() : sOldAssetGroupCode).split(",");
       		 sNewAssetGroup = ("".equals(sAssetGroupCode.trim()) ? pub.getAssetGroupCode() : sAssetGroupCode).split(",");
       		 
       		 for (int i = 0;i < sOldAssetGroup.length;i++)
       		 {
				strSql = "delete from "
					+ (bIsExchangeRatePub ? "Tb_Base_ExchangeRate"
							: ("tb_"+sOldAssetGroup[i]+"_data_ExchangeRate"))
					+
					" where FExRateSrcCode = "
					+ dbl.sqlString(this.strOldExRateSrcCode)
					+ " and FPortCode="
					+ dbl
							.sqlString(this.strOldPortCode.length() == 0 ? " "
									: this.strOldPortCode)
					+ " and FCuryCode="
					+ dbl.sqlString(this.strOldCuryCode)
					+ " and FMarkCury="
					+ dbl
							.sqlString("null"
									.equalsIgnoreCase(this.strOldMarkCuryCode) ? " "
									: this.strOldMarkCuryCode)
					+ 
					" and FExRateTime="
					+ dbl.sqlString(this.strOldExRateTime)
					+ " and FExRateDate="
					+ dbl.sqlDate(this.strOldExRateDate)
					+ " and FASSETGROUPCODE=" + dbl.sqlString(sOldAssetGroupCode.trim().length() > 0 ? sOldAssetGroupCode: " "); 
					dbl.executeSql(strSql);
       		 	}
		       	for (int i = 0;i < sNewAssetGroup.length;i++)
			   		{
			       		if (!FAssetGroupCode.equals(sTmpAssetGroupCode))
			       		{
			       			addData(FAssetGroupCode);
			       			sTmpAssetGroupCode = FAssetGroupCode;
			       		}
			   		}
       		 	}
			else
			{
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			// strSql = "update " + pub.yssGetTableName("Tb_Data_ExchangeRate")
			// +
			strSql = "update "
				    + ("Tb_"+FAssetGroupCode+"_Data_ExchangeRate")
					+ // 根据参数决定用哪张表 MS00131
					" set FExRateSrcCode = "
					+ dbl.sqlString(this.strExRateSrcCode)
					+ ", FExRateDate = "
					+ dbl.sqlDate(this.strExRateDate)
					+ ", FExRateTime = "
					+ dbl.sqlString(this.strExRateTime)
					+ ",FPortCode = "
					+ dbl.sqlString(this.strPortCode.length() == 0 ? " "
							: this.strPortCode)
					+ ", FCuryCode = "
					+ dbl.sqlString(this.strCuryCode)
					+ ", FMarkCury="
					+ dbl.sqlString(this.strMarkCuryCode)
					+ // 更新基准货币代码 linjunyun 2008-11-20 bug:MS00011
					// --------------lzp modify 2007 12.7
					", FExRate1 ="
					+ this.strExRate1
					+ ", FExRate2 ="
					+ this.strExRate2
					+ ",FExRate3 ="
					+ this.strExRate3
					+ ",FExRate4 ="
					+ this.strExRate4
					+ ",FExRate5 ="
					+ this.strExRate5
					+ ",FExRate6 ="
					+ this.strExRate6
					+ ",FExRate7 ="
					+ this.strExRate7
					+ ",FExRate8 ="
					+ this.strExRate8
					+
					// ------------------
					",FDesc ="
					+ dbl.sqlString(this.strDesc)
					+ // 需为FASSETGROUPCODE主键字段赋值 QDV4建行2008年12月25日01_A MS00131
						// by leeyu
					",FCheckstate = "
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ",FCreator = "
					+ dbl.sqlString(this.creatorCode)
					+ " , FCreateTime = "
					+ dbl.sqlString(this.creatorTime)
					+ ",FCheckUser = "
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode))
					+ " where FExRateSrcCode = "
					+ dbl.sqlString(this.strOldExRateSrcCode)
					+ " and FPortCode="
					+ dbl.sqlString(this.strOldPortCode.length() == 0 ? " "
							: this.strOldPortCode)
					+ " and FCuryCode="
					+ dbl.sqlString(this.strOldCuryCode)
					+ " and FMarkCury="
					+ dbl.sqlString(this.strOldMarkCuryCode)
					+ // 原基准货币代码作为主键条件查询 linjunyun 2008-11-20 bug:MS00011
					" and FExRateTime="
					+ dbl.sqlString(this.strOldExRateTime)
					+ " and FExRateDate="
					+ dbl.sqlDate(this.strOldExRateDate) +" and FCheckState='0'";
					 // 添加上FASSETGROUPCODE主键字段
									// QDV4建行2008年12月25日01_A MS00131 by leeyu
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			}
			return buildRowStr();
		} catch (Exception e) {
			throw new YssException("修改汇率行情设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		
	}
	
	/**
	 * add baopingping #story 1167 20110717
	 * 删除 ，审核，反审核 所有组合群下所选数据主键相同的行情数据
	 * return ResultSet
	 * @throws YssException 
	 */
	public void delData(String FAssetGroupCode) throws YssException{
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		boolean bIsExchangeRatePub = false; // 定义变量存放通?貌问? QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
		String strSql = "";
		try {
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			// strSql = "update " + pub.yssGetTableName("Tb_Data_ExchangeRate")
			// +
			strSql = "update "
				+ ("Tb_"+FAssetGroupCode+"_Data_ExchangeRate")
					+ // 根据参数决定用哪张表 MS00131
					" set FCheckState = "
					+ this.checkStateId
					+ ", FCheckUser = "
					+ dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FExRateSrcCode = "
					+ dbl.sqlString(this.strExRateSrcCode)
					+ " and FPortCode="
					+ dbl.sqlString(this.strPortCode.length() == 0 ? " "
							: this.strPortCode)
					+ " and FCuryCode="
					+ dbl.sqlString(this.strCuryCode)
					+ " and FMarkCury="
					+ dbl.sqlString(this.strMarkCuryCode)
					+ // 增加基准货币代码主键条件 linjunyun 2008-11-20 bug:MS00011
					" and FExRateTime="
					+ dbl.sqlString(this.strExRateTime)
					+ " and FExRateDate="
					+ dbl.sqlDate(this.strExRateDate)+" and FCheckState='0'"
					; 

			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除汇率行情设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

		
	}
	
	/**
	 * add baopingping #story 1167 20110717
	 * 审核，反审核 所有组合群下所选数据主键相同的行情数据
	 * return ResultSet
	 * @throws YssException 
	 */
	public void checkData(String FAssetGroupCode) throws YssException{
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		boolean bIsExchangeRatePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
		Connection conn = dbl.loadConnection();
		try {
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			conn.setAutoCommit(false);
			bTrans = true;
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != null&&(!sRecycled.equalsIgnoreCase("")) ) {
				arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					// strSql = "update " +
					// pub.yssGetTableName("Tb_Data_ExchangeRate") +
					strSql = "update "
						+ ("Tb_"+FAssetGroupCode+"_Data_ExchangeRate")
							+ // 根据参数决定用哪张表 MS00131
							" set FCheckState = "
							+ this.checkStateId
							+ ", FCheckUser = "
							+ dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = '"
							+ YssFun.formatDatetime(new java.util.Date())
							+ "'"
							+ " where FExRateSrcCode = "
							+ dbl.sqlString(this.strExRateSrcCode)
							+ " and FPortCode="
							+ dbl
									.sqlString(this.strPortCode.length() == 0 ? " "
											: this.strPortCode)
							+ " and FCuryCode="
							+ dbl.sqlString(this.strCuryCode)
							+ " and FMarkCury="
							+ dbl
									.sqlString("null"
											.equalsIgnoreCase(this.strMarkCuryCode) ? " "
											: this.strMarkCuryCode)
							+ // 增加基准货币代码主键条件 linjunyun 2008-11-20 bug:MS00011
							" and FExRateTime="
							+ dbl.sqlString(this.strExRateTime)
							+ " and FExRateDate="
							+ dbl.sqlDate(this.strExRateDate);
							// 添加上FASSETGROUPCODE主键字段
											// QDV4建行2008年12月25日01_A MS00131 by
											// leeyu
					dbl.executeSql(strSql);
				}
			}
			// 如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
			else if (strExRateSrcCode != null&&(!strExRateSrcCode.equalsIgnoreCase(""))) {
				// strSql = "update " +
				// pub.yssGetTableName("Tb_Data_ExchangeRate") +
				strSql = "update "
					+ ("Tb_"+FAssetGroupCode+"_Data_ExchangeRate")
						+ // 根据参数决定用哪张表 MS00131
						" set FCheckState = "
						+ this.checkStateId
						+ ", FCheckUser = "
						+ dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date())
						+ "'"
						+ " where FExRateSrcCode = "
						+ dbl.sqlString(this.strExRateSrcCode)
						+ " and FPortCode="
						+ dbl.sqlString(this.strPortCode.length() == 0 ? " "
								: this.strPortCode)
						+ " and FCuryCode="
						+ dbl.sqlString(this.strCuryCode)
						+ " and FMarkCury="
						+ dbl.sqlString("null"
								.equalsIgnoreCase(this.strMarkCuryCode) ? " "
								: this.strMarkCuryCode)
						+ // 增加基准货币代码主键条件 linjunyun 2008-11-20 bug:MS00011
						" and FExRateTime="
						+ dbl.sqlString(this.strExRateTime)
						+ " and FExRateDate="
						+ dbl.sqlDate(this.strExRateDate);
						
				dbl.executeSql(strSql);
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核汇率行情设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		// ----------end
	}
	
	
	/**
	 * add baopingping #story 1167 20110717
	 * 给所有组合群下添加一条行情数据
	 * return ResultSet
	 */
    private String copyData(String FAssetGroupCode) throws YssException {
    	Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		boolean bIsExchangeRatePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
		String strSql = "";
		try {
			bIsExchangeRatePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("exchangerate"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			strSql =
			// "insert into " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
			// "(FExRateSrcCode, FCuryCode,FMarkCury,FExRateDate,FExRateTime,FPortCode,FExRate1,FExRate2, FExRate3,FExRate4,"
			// +
			// " FExRate5, FExRate6,FExRate7,FExRate8,FDesc,FDataSource,FCheckState, FCreator, FCreateTime,FCheckUser) "
			// +
				"insert into "
				+ ("Tb_"+FAssetGroupCode+"_Data_ExchangeRate")
				+ // 根据参数决定是用哪张表 MS00131 by leeyu
				"(FExRateSrcCode, FCuryCode,FMarkCury,FExRateDate,FExRateTime,FPortCode,FExRate1,FExRate2, FExRate3,FExRate4,"
				+ " FExRate5, FExRate6,FExRate7,FExRate8,FDesc,FDataSource,FCheckState, FCreator, FCreateTime,FCheckUser"
				
				+ ") "
				+ // 若表为新表则添加上FASSETGROUPCODE字段
				" values("
				+ dbl.sqlString(this.strExRateSrcCode)
				+ ","
				+ dbl.sqlString(this.strCuryCode)
				+ ","
				+ dbl.sqlString(this.strMarkCuryCode)
				+ ","
				+ // 插入基准货币代码 linjunyun 2008-11-20 bug:MS00011
				dbl.sqlDate(this.strExRateDate)
				+ ","
				+ dbl.sqlString(this.strExRateTime)
				+ ","
				+ dbl.sqlString(this.strPortCode.length() == 0 ? " "
						: this.strPortCode)
				+ ","
				+
				// lzp modify 2007 12.7
				this.strExRate1
				+ ","
				+ this.strExRate2
				+ ","
				+ this.strExRate3
				+ ","
				+ this.strExRate4
				+ ","
				+ this.strExRate5
				+ ","
				+ this.strExRate6
				+ ","
				+ this.strExRate7
				+ ","
				+ this.strExRate8
				+ ","
				+
				// -------------
				dbl.sqlString(this.strDesc)
				+ ","
				+ 0
				+ ","
				+ (pub.getSysCheckState() ? "0" : "1")
				+ ","
				+ dbl.sqlString(this.creatorCode)
				+ ", "
				+ dbl.sqlString(this.creatorTime)
				+ ","
				+ (pub.getSysCheckState() ? "' '" : dbl
						.sqlString(this.creatorCode))+
			
				")";
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return buildRowStr();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new YssException("复制汇率行情设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
    }
    
    
    /**
	 * add baopingping #story 1167 20110717
	 * 查看数据库中是否存这个组合群的数据
	 * TableName 组合群代码
	 * return ResultSet
	 * @throws YssException 
	 */
	
	public String getTable(String TableName) throws YssException{
		ResultSet rs=null;
		String sql=null;
		String  name="none";
		try{
			sql="select * from TB_"+TableName+"_Data_ExchangeRate where " +
			"FExRateSrcCode='"+this.strExRateSrcCode+"' and FCuryCode='"+this.strCuryCode
			+"'and FMarkCury='"+this.strMarkCuryCode+"' and FExRateDate="+dbl.sqlDate(this.strExRateDate)
			+" and " +" FPortCode="+ dbl.sqlString((this.strPortCode.length() == 0) ? " "
			: this.strPortCode);
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				name="full";
			}
			return name;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	
	/**
	 * add baopingping #story 1167 20110717
	 * 查看数据库中是否存这个组合群的数据
	 * TableName 组合群代码
	 * return ResultSet
	 * @throws YssException 
	 */
	
	public String getGroup(String TableName) throws YssException{
		ResultSet rs=null;
		String sql=null;
		String  name="";
		try{
			sql="select * from TB_"+TableName+"_Data_ExchangeRate where Fcheckstate = 0 " +
			" and FExRateSrcCode='"+this.strExRateSrcCode+"' and FCuryCode='"+this.strCuryCode
			+"'and FMarkCury='"+this.strMarkCuryCode+"' and FExRateDate="+dbl.sqlDate(this.strExRateDate)
			+" and " +" FPortCode="+ dbl.sqlString((this.strPortCode.length() == 0) ? " "
			: this.strPortCode);
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				name=TableName;
			}
			return (name.length() > 0 ? name + "," : name);
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	

	/**
	 * added by liubo. #story 1770.20111123
	 * 通过此方法，查询出类似“001,002”以逗号分隔开的组合群代号所代表的组合群代码，同样以逗号分隔开
	 * FAssetGroupCode 组合群代码
	 * return String
	 * @throws YssException 
	 */
	
	private String getGroupNameFromGroupCode(String FAssetGroupCode) throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		String[] groupCode = null;
		String requestGroupCode = "";
		try
		{
//			groupCode = ("".equals(FAssetGroupCode.trim()) ? pub.getAssetGroupCode() : FAssetGroupCode).split(",");
			groupCode = FAssetGroupCode.split(",");
			for (int i = 0;i<groupCode.length;i++)
			{
				requestGroupCode = requestGroupCode +"'" + groupCode[i] + "',";
			}
			
			strSql = "select * from tb_sys_AssetGroup where FAssetGroupCode in (" + requestGroupCode.substring(0,requestGroupCode.length() - 1) + ")";
			rs = dbl.openResultSet(strSql);
			while(rs.next())
			{
				sReturn = sReturn + rs.getString("FAssetGroupName") + ",";
			}
			
			return ("".equals(sReturn.trim()) ? "" : sReturn.substring(0, sReturn.length() - 1));
		
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
	}

	/***********************************************************
	 * STORY #1244 导入汇率时多组合情况下，以组合代码为空的汇率为默认的汇率，导入数据中心相关需求
	 * 获取公用汇率
	 * @return
	 * @throws YssException
	 */
	public HashMap getCommonRate(String sStartDate,String sEndDate)throws YssException{
		
		HashMap commonRateMap = new HashMap();
		StringBuffer buff = new StringBuffer();
		ResultSet rs = null;
		String sKey = "";
		ExchangeRateBean rateBean = null;
		try{ 
			//1. 获取当天所有组合代码为空的汇率     FEXRATESRCCODE, FCURYCODE, FMARKCURY, FEXRATEDATE, FEXRATETIME
			buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_ExchangeRate"));
			buff.append(" where fcheckstate=1 and fexratedate between ").append(dbl.sqlDate(sStartDate));
			buff.append(" and ").append(dbl.sqlDate(sEndDate));
			buff.append(" and fportcode =' '"); 
			
			rs = dbl.openResultSet(buff.toString());
			while(rs.next()){
				
				sKey = rs.getString("FEXRATESRCCODE")+"\t"+rs.getString("FCURYCODE")+"\t"+
				       rs.getString("FMARKCURY")+"\t"+rs.getDate("FEXRATEDATE")+"\t"+rs.getString("FEXRATETIME");
				rateBean = new ExchangeRateBean();
				
				rateBean.setStrExRateSrcCode(rs.getString("FEXRATESRCCODE"));
				rateBean.setStrCuryCode(rs.getString("FCURYCODE"));
				rateBean.setStrMarkCuryCode(rs.getString("FMARKCURY"));
				rateBean.setStrExRateDate(YssFun.formatDate(rs.getDate("FEXRATEDATE")));
				rateBean.setStrExRateTime(rs.getString("FEXRATETIME"));
				rateBean.setStrPortCode(rs.getString("fportcode"));			
				rateBean.setStrExRate1(rs.getDouble("FExRate1"));
				rateBean.setStrDataSource(rs.getString("FDATASOURCE"));
				
				commonRateMap.put(sKey, rateBean);
			}
			dbl.closeResultSetFinal(rs);
			buff.setLength(0);
			
			//2. 如果汇率表中没有公用汇率而只有专用的汇率，则根据组合代码优先规则。
			// 即组合代码中只有001001，001002的专用汇率，那么数据中心汇率表的公用汇率就是以001001的专用汇率作为公用汇率
			
			buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_ExchangeRate"));
			buff.append(" where fcheckstate=1 and fexratedate between ").append(dbl.sqlDate(sStartDate));
			buff.append(" and ").append(dbl.sqlDate(sEndDate));
			buff.append(" and fportcode <>' ' order by fportcode "); 
			
			rs = dbl.openResultSet(buff.toString());
			
			while(rs.next()){
				
				sKey = rs.getString("FEXRATESRCCODE")+"\t"+rs.getString("FCURYCODE")+"\t"+
				       rs.getString("FMARKCURY")+"\t"+rs.getDate("FEXRATEDATE")+"\t"+rs.getString("FEXRATETIME");
				
				if(!commonRateMap.containsKey(sKey)){
					rateBean = new ExchangeRateBean();
					
					rateBean.setStrExRateSrcCode(rs.getString("FEXRATESRCCODE"));
					rateBean.setStrCuryCode(rs.getString("FCURYCODE"));
					rateBean.setStrMarkCuryCode(rs.getString("FMARKCURY"));
					rateBean.setStrExRateDate(YssFun.formatDate(rs.getDate("FEXRATEDATE")));
					rateBean.setStrExRateTime(rs.getString("FEXRATETIME"));
					rateBean.setStrPortCode(" ");			
					rateBean.setStrExRate1(rs.getDouble("FExRate1"));
					rateBean.setStrDataSource(rs.getString("FDATASOURCE"));
					commonRateMap.put(sKey, rateBean);
				}
				
			}
			return commonRateMap ;
		}catch(YssException e){
			throw new YssException("【数据中心——汇率数据接口：获取公用汇率出错......】");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new YssException("【数据中心——汇率数据接口：获取公用汇率出错......】");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		
		
		
		
	}
	
	
}
