package com.yss.main.etfoperation.etfaccbook.timeanddifference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.commeach.EachRateOper;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFSubTradeBean;
import com.yss.main.operdeal.businesswork.SellTradeRelaCal;
import com.yss.main.operdeal.stgstat.StgSecurity;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 拆分华宝交易数据为主动投资或者被动投资数据，插入到ETF交易子表（TB_ETF_subTrade）
 * @author xuqiji 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 *
 */
public class DivideETFReplaceOrAction extends CtlETFAccBook{
	private ETFParamSetBean paramSet = null;// ETF参数的实体类
	private ETFParamSetAdmin paramSetAdmin = null;//参数设置操作类
	private HashMap etfParam = null;//保存参数设置
	private ArrayList alETFSubTradeData = new ArrayList();//保存ETF交易子表数据
	private String securityCodes = "";//证券代码
	private java.util.Date startDate = null;//开始的申赎日期
	private java.util.Date endDate = null;//结束的申赎日期	
	private java.util.Date tradeDate = null;//估值日期
	private String standingBookType = "";//台账类型
	private String portCodes = "";//组合代码
	public java.util.Date getEndDate() {
		return endDate;
	}
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}
	public String getPortCodes() {
		return portCodes;
	}
	public void setPortCodes(String portCodes) {
		this.portCodes = portCodes;
	}
	public String getSecurityCodes() {
		return securityCodes;
	}
	public void setSecurityCodes(String securityCodes) {
		this.securityCodes = securityCodes;
	}
	public String getStandingBookType() {
		return standingBookType;
	}
	public void setStandingBookType(String standingBookType) {
		this.standingBookType = standingBookType;
	}
	public java.util.Date getStartDate() {
		return startDate;
	}
	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}
	public java.util.Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(java.util.Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public DivideETFReplaceOrAction() {
		super();
	}
	/**
	 * 此类的入口方法
	 */
	public void doManage()throws YssException{
		int days = 0;//保存两个日期之差
		Date dDate = null;
		try{
			days = YssFun.dateDiff(this.startDate,this.endDate);
			dDate = this.startDate;//起始日期
			for(int i=0;i<=days;i++){
				
				doTheDayTradeDataIsAction(dDate);//处理当天交易数据中没有关联到申赎数据的成交编号的主动投资数据
				
				doDivideTradeData(dDate);//处理当天交易数据中关联到申赎数据的成交编号的数据,并拆分为主动投资或者被动投资
				
				if(alETFSubTradeData.size()>0){
					insertETFSubTrade(dDate);//此方法插入数据到ETF交易子表中
				}
				
				dDate = YssFun.addDay(dDate,1);//每次循环时日期加1
			}
			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	
	/**
	 * 此方法插入数据到ETF交易子表中
	 * @param dDate
	 */
	private void insertETFSubTrade(Date dDate) throws YssException{
		StringBuffer buff =null;
		Connection conn =null;
		boolean bTrans = true;
		PreparedStatement pst = null;
		long sNum=0;//为了产生的编号不重复
		String strNumDate = ""; //保存交易编号
		ETFSubTradeBean subTrade = null;
		try{
			conn =dbl.loadConnection();
			conn.setAutoCommit(false);
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("TB_ETF_subTrade"));
			buff = new StringBuffer(500);
			buff.append(" delete from ").append(pub.yssGetTableName("TB_ETF_subTrade"));
			buff.append(" where FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and FBargainDate = ").append(dbl.sqlDate(dDate));
			
			dbl.executeSql(buff.toString());
			buff.delete(0, buff.length());
			
			buff.append(" insert into ").append(pub.yssGetTableName("Tb_ETF_SubTrade"));
			buff.append("(FNum,FSecurityCode,FPortCode,FTradeTypeCode,FCashAccCode,FBargainDate,FBargainTime,");
			buff.append(" FBaseCuryRate,FPortCuryRate,FTradeAmount,FTradePrice,FTradeMoney,FFeeCode1,FTradeFee1,FFeeCode2,FTradeFee2,");
			buff.append(" FFeeCode3,FTradeFee3,FFeeCode4,FTradeFee4,FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6,");
			buff.append(" FFeeCode7,FTradeFee7,FFeeCode8,FTradeFee8,");
			buff.append(" FTotalCost,FCost,FBaseCuryCost,FPortCuryCost,FETFTradeWayCode,FBBTotalCost,FBBTradeFee,FVMoney,FVBBMoney)");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			//--------------------拼接交易编号---------------------
			strNumDate = YssFun.formatDatetime(dDate).substring(0, 8);
            strNumDate = strNumDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_ETF_SubTrade"),
                                       dbl.sqlRight("FNUM", 6), "000000",
                                       " where FNum like 'T" + strNumDate + "%'", 1);
            strNumDate = "T" + strNumDate;
            String s = strNumDate.substring(9, strNumDate.length());
            sNum = Long.parseLong(s);
            //--------------------------------end--------------------------//
			
            for(int i = 0;i < alETFSubTradeData.size(); i++){
            	//--------------------拼接交易编号---------------------
                sNum++;
                String tmp = "";
                for (int j = 0; j < s.length() - String.valueOf(sNum).length(); j++) {
                    tmp += "0";
                }
                strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                // ------------------------end--------------------------//
            	subTrade = (ETFSubTradeBean) alETFSubTradeData.get(i);
            	
            	pst.setString(1,strNumDate);
				pst.setString(2,subTrade.getSecurityCode());
				pst.setString(3,subTrade.getPortCode());
				pst.setString(4,subTrade.getTradeCode());
				pst.setString(5,subTrade.getCashAcctCode());
				pst.setDate(6,YssFun.toSqlDate(subTrade.getBargainDate()));
				pst.setString(7,subTrade.getBargainTime());
				pst.setDouble(8,subTrade.getBaseCuryRate());
				pst.setDouble(9,subTrade.getPortCuryRate());
				pst.setDouble(10,subTrade.getTradeAmount());//数量
				pst.setDouble(11,subTrade.getTradePrice());
				pst.setDouble(12,subTrade.getTradeMoney());//成交金额
				pst.setString(13,subTrade.getFFeeCode1());
				pst.setDouble(14,subTrade.getFTradeFee1());
				pst.setString(15,subTrade.getFFeeCode2());
				pst.setDouble(16,subTrade.getFTradeFee2());
				pst.setString(17,subTrade.getFFeeCode3());
				pst.setDouble(18,subTrade.getFTradeFee3());
				pst.setString(19,subTrade.getFFeeCode4());
				pst.setDouble(20,subTrade.getFTradeFee4());
				pst.setString(21,subTrade.getFFeeCode5());
				pst.setDouble(22,subTrade.getFTradeFee5());
				pst.setString(23,subTrade.getFFeeCode6());
				pst.setDouble(24,subTrade.getFTradeFee6());
				pst.setString(25,subTrade.getFFeeCode7());
				pst.setDouble(26,subTrade.getFTradeFee7());
				pst.setString(27,subTrade.getFFeeCode8());
				pst.setDouble(28,subTrade.getFTradeFee8());
				
				pst.setDouble(29,subTrade.getTotalCost());//实际成交金额
				pst.setDouble(30,subTrade.getCost());//原币成本
				pst.setDouble(31,subTrade.getBaseCuryCost());//基础货币成本
				pst.setDouble(32,subTrade.getPortCuryCost());//组合货币成本
				pst.setString(33,subTrade.getETFTradeWayCode());//投资类型 ： 主动或被动
				pst.setDouble(34,subTrade.getBBTotalCost());
				pst.setDouble(35,subTrade.getBBTradeFee());
				pst.setDouble(36,subTrade.getVmoney());
				//本币估值增值
				pst.setDouble(37,subTrade.getVBBMoney());
				
				pst.addBatch();
            	
            }
            pst.executeBatch();
            
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
		}catch (Exception e) {
			throw new YssException("插入数据到ETF交易子表中出错！",e);
		}finally{
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
			
		}
		
	}
	/**
	 * 此方法做解析前台传来数据
	 */
	public void initData(Date startDate,Date endDate,Date tradeDate ,String portCodes,String standingBookType) throws YssException{
		try{
			this.startDate = startDate;//起始日期
			this.endDate = endDate;//截止日期
			this.tradeDate = tradeDate;//业务日期
			this.portCodes = portCodes;//组合代码
			this.standingBookType = standingBookType;//台账类型
			paramSetAdmin = new ETFParamSetAdmin();//参数设置操作类
			paramSetAdmin.setYssPub(pub);//设置pub
			etfParam = paramSetAdmin.getETFParamInfo(this.portCodes); // 根据已选组合代码用于获取相关ETF参数数据
			paramSet = (ETFParamSetBean) etfParam.get(this.portCodes);//根据组合代码获取参数设置实体bean
		}catch (Exception e) {
			throw new YssException("做解析前台传来数据出错！",e);
		}
	}
	
	/**
	 * 处理当天交易数据中没有关联到申赎数据的成交编号的主动投资数据
	 * @param dDate
	 * @throws YssException
	 */
	private void doTheDayTradeDataIsAction(Date dDate) throws YssException{
		ETFSubTradeBean subTrade = null;//ETF交易子表实体bean
		StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs = null;//结果集
		double baseRate = 1;//基础汇率
		double portRate = 1;//组合汇率
		StgSecurity securityCost = null;//证券库存声明
		SellTradeRelaCal tradeRealCal = null;//计算卖出交易中的关联数据类的声明
		String ETFTradeWayCode ="";//投资类型：主动投资或者被动投资
		try{
			if(alETFSubTradeData.size()!=0){//集合不为空时，要先清空
				alETFSubTradeData.clear();
			}
			
			//接口导入交易数据没有计算成本，以下是计算交易数据的成本----------------------//
			securityCost = new StgSecurity();
			securityCost.setYssPub(pub);
			securityCost.refreshTradeCost(dDate,dDate,this.getPortCodeSQL());//panjunfang modify 20101217 QDV4华宝2010年12月17日01_B
			//------------------------------end---------------------------------//
			
			//--------------以下计算当天交易的估值增值 ------------------------------//
			tradeRealCal = new SellTradeRelaCal(); 
			tradeRealCal.setYssPub(pub);
			tradeRealCal.setWorkDate(dDate);
			tradeRealCal.setPortCodes(this.portCodes);
			tradeRealCal.doOperation("do");
			//--------------------end -----------------------------------------//
			
			buff = new StringBuffer(1000);
			buff.append(" select sub.*, po.fportcury, se.ftradecury,re.FAppreciation,re.FPortAppreciation from ").append(pub.yssGetTableName("tb_data_subtrade"));//业务资料
			buff.append(" sub left join (select * from ").append(pub.yssGetTableName("tb_para_portfolio"));//组合设置表
			buff.append(" where FCheckState = 1 and Fsubassettype = '0106') po on sub.fportcode = po.fportcode");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_para_security"));//证券信息表
			buff.append(" where FCheckState = 1) se on sub.fsecuritycode = se.fsecuritycode");
			buff.append(" left join (select sum(FAppreciation) as FAppreciation,sum(FPortAppreciation) as FPortAppreciation,");
			buff.append(" fNum from ").append(pub.yssGetTableName("tb_data_tradesellrela"));//交易数据卖出估值增值表
			buff.append(" group by fNum) re on sub.fnum = re.fnum");
			buff.append(" where sub.fcheckstate = 1 and sub.fbargaindate =").append(dbl.sqlDate(dDate));
			buff.append(" and sub.fportcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and not exists (select * from ").append(pub.yssGetTableName("tb_etf_ghinterface")).append(" gh ");//过户库
			buff.append(" where fbargaindate <= ").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and FOperType = '2ndcode' and sub.fdealnum = gh.ftradenum )");
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			
			while(rs.next()){
				
				baseRate = this.getSettingOper().getCuryRate(// 基础汇率
						rs.getDate("FBargainDate"), rs.getString("ftradecury"),
						rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

				EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
				rateOper.setYssPub(pub);// 设置PUB
				rateOper.getInnerPortRate(rs.getDate("FBargainDate"),
						rs.getString("fportcury"), rs.getString("FPortCode"));
				portRate = rateOper.getDPortRate();// 组合汇率
				
				subTrade = new ETFSubTradeBean();//实例化
				
				ETFTradeWayCode = "ACTIVE";//主动投资
				
				setActionData(subTrade,rs,baseRate,portRate,ETFTradeWayCode,1);//设置拆分的ETF交易子表投资数据
				
				alETFSubTradeData.add(subTrade);//保存数据到集合中
			}
			
		}catch (Exception e) {
			throw new YssException("处理当天交易数据中没有关联到申赎数据的成交编号的主动投资数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 处理当天交易数据中关联到申赎数据的成交编号的数据,并拆分为主动投资或者被动投资
	 * @param dDate
	 * @throws YssException
	 */
	private void doDivideTradeData(Date dDate) throws YssException{
		StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs =null;//结果集
		ETFSubTradeBean subTrade = null;//ETF交易子表实体bean
		EachExchangeHolidays holiday = null;//节假日代码
		String sRowStr ="";
		Date yesDate = null;//操作日当天的钱一个工作日
		double replaceScale = 0;//被动投资比例
		double baseRate = 1;//基础汇率
		double portRate = 1;//组合汇率
		String ETFTradeWayCode="";//投资类型
		double supplyAmount =0;//补票数量
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = paramSet.getSHolidayCode() + "\t" + -1 + "\t" + YssFun.formatDate(dDate);
			//解析数据
			holiday.parseRowStr(sRowStr);
			yesDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取当天的最近一个工作日
			
			buff = new StringBuffer(2000);
			buff.append(" select t1.freplaceamount as FSupplyamount,t2.Fsumamount,sub.*,po.fportcury,se.ftradecury from ");
			buff.append(pub.yssGetTableName("tb_etf_tradestldtl")).append(" t1 ");//结算明细表
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));//结算明细关联表
			buff.append(" where FDataMark = '0' and fremaindamount <> 0) t2 on t1.fnum = t2.FNum");
			buff.append(" join (select s.*, re.FAppreciation, re.FPortAppreciation from ").append(pub.yssGetTableName("tb_data_subtrade"));//业务资料表
			buff.append(" s left join (select sum(FAppreciation) as FAppreciation,sum(FPortAppreciation) as FPortAppreciation,fNum from ");
			buff.append(pub.yssGetTableName("tb_data_tradesellrela")).append(" group by fNum) re on s.fnum = re.FNum");//交易数据卖出估值增值表
			buff.append(" where s.fcheckstate = 1 and s.fbargaindate = ").append(dbl.sqlDate(dDate));
			buff.append(" and s.fportcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" ) sub on t1.ftradenum = sub.fdealnum and sub.fappdate = t1.fbuydate and sub.FSecurityCode =  t1.FSecurityCode");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_para_portfolio"));//组合设置表
			buff.append(" where FCheckState = 1 and Fsubassettype = '0106') po on t1.fportcode = po.fportcode");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_para_security"));//证券信息表
			buff.append(" where FCheckState = 1) se on t1.fsecuritycode = se.fsecuritycode");
			buff.append(" where t1.fportcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and t1.fbuydate =").append(dbl.sqlDate(dDate));
			buff.append(" and t1.fmarktype = 'time'");
			buff.append(" union all ");
			buff.append(" select t2.fremaindamount as FSupplyamount,t3.Fsumamount,sub.*,po.fportcury,se.ftradecury from ");
			buff.append(pub.yssGetTableName("tb_etf_tradestldtl")).append(" t1 ");//结算明细表
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));//结算明细关联表
			buff.append(" where FDataMark = '0' and FRefNum = '1' and fremaindamount<>0 and FMakeUpDate = ").append(dbl.sqlDate(yesDate)).append(") t2 on t1.fnum = t2.FNum");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));//结算明细关联表
			buff.append(" where FDataMark = '0' and FRefNum = '2' and FMakeUpDate =").append(dbl.sqlDate(dDate));
			buff.append(" ) t3 on t1.fnum = t3.FNum");
			buff.append(" join (select s.*, re.FAppreciation, re.FPortAppreciation from ").append(pub.yssGetTableName("tb_data_subtrade"));//业务资料表
			buff.append(" s left join (select sum(FAppreciation) as FAppreciation,sum(FPortAppreciation) as FPortAppreciation,fNum from ");
			buff.append(pub.yssGetTableName("tb_data_tradesellrela")).append(" group by fNum) re on s.fnum = re.FNum");//交易数据卖出估值增值表
			buff.append(" where s.fcheckstate = 1 and s.fbargaindate = ").append(dbl.sqlDate(dDate));
			buff.append(" and s.fportcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" ) sub on t1.ftradenum = sub.fdealnum and sub.fappdate = t1.fbuydate and sub.FSecurityCode =  t1.FSecurityCode");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_para_portfolio"));//组合设置表
			buff.append(" where FCheckState = 1 and Fsubassettype = '0106') po on t1.fportcode = po.fportcode");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_para_security"));//证券信息表
			buff.append(" where FCheckState = 1) se on t1.fsecuritycode = se.fsecuritycode");
			buff.append(" where t1.fportcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and t1.fbuydate =").append(dbl.sqlDate(yesDate));
			buff.append(" and t1.fmarktype = 'time'");
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			while(rs.next()){
				
				baseRate = this.getSettingOper().getCuryRate(// 基础汇率
						rs.getDate("FBargainDate"), rs.getString("ftradecury"),
						rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

				EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
				rateOper.setYssPub(pub);// 设置PUB
				rateOper.getInnerPortRate(rs.getDate("FBargainDate"),
						rs.getString("fportcury"), rs.getString("FPortCode"));
				portRate = rateOper.getDPortRate();// 组合汇率
				
				subTrade = new ETFSubTradeBean();
				supplyAmount = YssD.add(rs.getDouble("FSupplyamount"),rs.getDouble("Fsumamount"));//要加上当天权益数量
				if(supplyAmount >= rs.getDouble("FTradeAmount")){//当天该支股票的申赎数量 大于或者等于当天的交易数量时，交易数据为被动投资数据
					
					ETFTradeWayCode = "REPLACE";//主动投资
					setActionData(subTrade,rs,baseRate,portRate,ETFTradeWayCode,1);//设置拆分的ETF交易子表投资数据
					
				}else{//要把交易数据拆分成主动投资数据 和被动投资数据
					replaceScale = YssD.div(rs.getDouble("FSupplyamount"), rs.getDouble("FTradeAmount"));
					ETFTradeWayCode = "REPLACE";//被动投资
					setActionData(subTrade,rs,baseRate,portRate,ETFTradeWayCode,replaceScale);//设置拆分的ETF交易子表投资数据
					
				}
				alETFSubTradeData.add(subTrade);//保存数据
			}
		}catch (Exception e) {
			throw new YssException("处理当天交易数据中关联到申赎数据的成交编号的数据,并拆分为主动投资或者被动投资出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 设置拆分的ETF交易子表投资数据
	 * @param subTrade ETF交易子表实体bean
	 * @param rs 结果集
 	 * @param baseRate 基础汇率
	 * @param portRate 组合汇率
	 * @param ETFTradeWayCode 交易类型：主动投资 --- ACTIVE ，补 票 ---- REPLACE
	 * @param replaceScale 被动投资比例
	 * @throws YssException
	 */
	private void setActionData(ETFSubTradeBean subTrade,ResultSet rs,double baseRate,double portRate,String ETFTradeWayCode,double replaceScale) throws YssException{
		ETFSubTradeBean subTradeActive = null;//主动投资bean
		try{
			subTrade.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
			subTrade.setPortCode(rs.getString("FPortCode"));//组合代码
			subTrade.setTradeCode(rs.getString("FTradeTypeCode"));//交易类型
			subTrade.setCashAcctCode(rs.getString("FCashAccCode"));//现金账户
			subTrade.setBargainDate(YssFun.formatDate(rs.getDate("FBargainDate")));//成交日期
			subTrade.setBargainTime(rs.getString("FBargainTime"));//成交时间
			subTrade.setBaseCuryRate(baseRate);//基础汇率
			subTrade.setPortCuryRate(portRate);//组合汇率
			subTrade.setTradeAmount(replaceScale==1?rs.getDouble("FTradeAmount"):YssD.add(rs.getDouble("FSupplyamount"),rs.getDouble("Fsumamount")));//成交数量
			subTrade.setTradeMoney(YssD.round(YssD.mul(rs.getDouble("FTradeMoney"), replaceScale), 2));//交易金额
			subTrade.setTradePrice(replaceScale==1?rs.getDouble("FTradePrice"):YssD.round(YssD.div(subTrade.getTradeMoney(), subTrade.getTradeAmount()), 2));//成交价格
			//费用（原币）=总费用（原币）* 比例 
			subTrade.setFFeeCode1(rs.getString("FFeeCode1"));//费用代码1
			subTrade.setFTradeFee1(YssD.round(YssD.mul(rs.getDouble("FTradeFee1"), replaceScale), 2));//费用1
			subTrade.setFFeeCode2(rs.getString("FFeeCode2"));
			subTrade.setFTradeFee2(YssD.round(YssD.mul(rs.getDouble("FTradeFee2"), replaceScale), 2));
			subTrade.setFFeeCode3(rs.getString("FFeeCode3"));
			subTrade.setFTradeFee3(YssD.round(YssD.mul(rs.getDouble("FTradeFee3"), replaceScale), 2));
			subTrade.setFFeeCode4(rs.getString("FFeeCode4"));
			subTrade.setFTradeFee4(YssD.round(YssD.mul(rs.getDouble("FTradeFee4"), replaceScale), 2));
			subTrade.setFFeeCode5(rs.getString("FFeeCode5"));
			subTrade.setFTradeFee5(YssD.round(YssD.mul(rs.getDouble("FTradeFee5"), replaceScale), 2));
			subTrade.setFFeeCode6(rs.getString("FFeeCode6"));
			subTrade.setFTradeFee6(YssD.round(YssD.mul(rs.getDouble("FTradeFee6"), replaceScale), 2));
			subTrade.setFFeeCode7(rs.getString("FFeeCode7"));
			subTrade.setFTradeFee7(YssD.round(YssD.mul(rs.getDouble("FTradeFee7"), replaceScale), 2));
			subTrade.setFFeeCode8(rs.getString("FFeeCode8"));
			subTrade.setFTradeFee8(YssD.round(YssD.mul(rs.getDouble("FTradeFee8"), replaceScale), 2));
			//成本（原币）=总成本（原币）* 比例 
			subTrade.setCost(YssD.round(YssD.mul(rs.getDouble("FCost"), replaceScale), 2));
			//清算款（原币） =  成本（原币） + 费用（原币）
			subTrade.setTotalCost(YssD.round(YssD.mul(rs.getDouble("ftotalcost"),replaceScale),2));
			//基础货币成本
			subTrade.setBaseCuryCost(YssD.round(YssD.mul(rs.getDouble("FBaseCuryCost"), replaceScale), 2));
			//组合货币成本
			subTrade.setPortCuryCost(YssD.round(YssD.mul(rs.getDouble("FPortCuryCost"), replaceScale), 2));
			//交易类型 
			subTrade.setETFTradeWayCode(ETFTradeWayCode);
			//费用本币 = 费用（原币）* 基础汇率/组合汇率
			subTrade.setBBTradeFee(YssD.round(
					YssD.div(
							YssD.mul(
									YssD.add(
											YssD.add(subTrade.getFTradeFee1(), subTrade.getFTradeFee2(),subTrade.getFTradeFee3()),YssD.add(subTrade.getFTradeFee4(),subTrade.getFTradeFee5(),subTrade.getFTradeFee6()),YssD.add(subTrade.getFTradeFee7(),subTrade.getFTradeFee8())),
											baseRate),
											portRate), 
											2));
			//清算款（本币） =  成本（本币） + 费用（本币）
			subTrade.setBBTotalCost(YssD.round(YssD.div(YssD.mul(subTrade.getTotalCost(), baseRate),portRate), 2));
		
			//原币估值增值 = 原币总估值增值 * 被动投资比例
			subTrade.setVmoney(YssD.round(YssD.mul(rs.getDouble("FAppreciation"), replaceScale), 2));
			//原币估值增值 = 本币总估值增值 * 被动投资比例
			subTrade.setVBBMoney(YssD.round(YssD.mul(rs.getDouble("FPortAppreciation"), replaceScale), 2));
			
			if(replaceScale!=1){
				subTradeActive = new ETFSubTradeBean();
				
				subTradeActive.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
				subTradeActive.setPortCode(rs.getString("FPortCode"));//组合代码
				subTradeActive.setTradeCode(rs.getString("FTradeTypeCode"));//交易类型
				subTradeActive.setCashAcctCode(rs.getString("FCashAccCode"));//现金账户
				subTradeActive.setBargainDate(YssFun.formatDate(rs.getDate("FBargainDate")));//成交日期
				subTradeActive.setBargainTime(rs.getString("FBargainTime"));//成交时间
				subTradeActive.setBaseCuryRate(baseRate);//基础汇率
				subTradeActive.setPortCuryRate(portRate);//组合汇率
				subTradeActive.setTradeAmount(YssD.sub(rs.getDouble("FTradeAmount"),subTrade.getTradeAmount()));//成交数量
				//成交金额 = 总金额 - （被动比例×总金额）
				subTradeActive.setTradeMoney(YssD.round(YssD.sub(rs.getDouble("FTradeMoney"), subTrade.getTradeMoney()), 2));
				//成交价格 = （总金额 - （被动比例×总金额））/（交易总数量 - 补票总数量）
				subTradeActive.setTradePrice(YssD.round(YssD.div(subTradeActive.getTradeMoney(), subTrade.getTradeAmount()), 2));
				//交易费用 = 交易总费用 - （补票费用（即 被动比例× 交易总费用））
				subTradeActive.setFFeeCode1(rs.getString("FFeeCode1"));//费用代码1
				subTradeActive.setFTradeFee1(YssD.round(YssD.sub(rs.getDouble("FTradeFee1"), subTrade.getFTradeFee1()), 2));//费用1
				subTradeActive.setFFeeCode2(rs.getString("FFeeCode2"));
				subTradeActive.setFTradeFee2(YssD.round(YssD.sub(rs.getDouble("FTradeFee2"), subTrade.getFTradeFee2()), 2));
				subTradeActive.setFFeeCode3(rs.getString("FFeeCode3"));
				subTradeActive.setFTradeFee3(YssD.round(YssD.sub(rs.getDouble("FTradeFee3"), subTrade.getFTradeFee3()), 2));
				subTradeActive.setFFeeCode4(rs.getString("FFeeCode4"));
				subTradeActive.setFTradeFee4(YssD.round(YssD.sub(rs.getDouble("FTradeFee4"), subTrade.getFTradeFee4()), 2));
				subTradeActive.setFFeeCode5(rs.getString("FFeeCode5"));
				subTradeActive.setFTradeFee5(YssD.round(YssD.sub(rs.getDouble("FTradeFee5"), subTrade.getFTradeFee5()), 2));
				subTradeActive.setFFeeCode6(rs.getString("FFeeCode6"));
				subTradeActive.setFTradeFee6(YssD.round(YssD.sub(rs.getDouble("FTradeFee6"), subTrade.getFTradeFee6()), 2));
				subTradeActive.setFFeeCode7(rs.getString("FFeeCode7"));
				subTradeActive.setFTradeFee7(YssD.round(YssD.sub(rs.getDouble("FTradeFee7"), subTrade.getFTradeFee7()), 2));
				subTradeActive.setFFeeCode8(rs.getString("FFeeCode8"));
				subTradeActive.setFTradeFee8(YssD.round(YssD.sub(rs.getDouble("FTradeFee8"), subTrade.getFTradeFee8()), 2));
				//成本 = 总成本 - 被动投资的成本
				subTradeActive.setCost(YssD.round(YssD.sub(rs.getDouble("FCost"), subTrade.getCost()), 2));
				//基础货币成本 = 总基础货币成本 - 被动投资的基础货币成本
				subTradeActive.setBaseCuryCost(YssD.round(YssD.sub(rs.getDouble("FBaseCuryCost"), subTrade.getBaseCuryCost()), 2));
				//组合货币成本 = 总组合货币成本 - 被动投资的组合货币成本
				subTradeActive.setPortCuryCost(YssD.round(YssD.sub(rs.getDouble("FPortCuryCost"), subTrade.getPortCuryCost()), 2));
				//实际成交金额 = 总实际成交金额 - 被动投资的实际成交金额
				subTradeActive.setTotalCost(YssD.round(YssD.sub(rs.getDouble("FTotalCost"), subTrade.getTotalCost()), 2));
				//本币实际成交金额 = 本币总实际成交金额 - 原币实际成交金额*基础汇率/组合汇率 * 被动投资比例 
				subTradeActive.setBBTotalCost(YssD.round(YssD.sub(YssD.div(YssD.mul(rs.getDouble("FTotalCost"), baseRate), portRate), subTrade.getBBTotalCost()), 2));
	
				//本币交易费用 = 本币总交易费用 - 原币实际成交金额*基础汇率/组合汇率 * 被动投资比例 
				subTradeActive.setBBTradeFee(YssD.sub(YssD.round(
						YssD.div(
								YssD.mul(
										YssD.add(
												YssD.add(rs.getDouble("FTradeFee1"), rs.getDouble("FTradeFee2"),
														rs.getDouble("FTradeFee3")),
												YssD.add(rs.getDouble("FTradeFee4"),
														rs.getDouble("FTradeFee5"),rs.getDouble("FTradeFee6")),
												YssD.add(rs.getDouble("FTradeFee7"),rs.getDouble("FTradeFee8"))
												), 
										baseRate), 
								portRate), 
								2),subTrade.getBBTradeFee()));
				//主动投资原币估值增值 = 原币估值增值 -被动投资原币估值增值(原币估值增值 * 被动投资比例)
				subTradeActive.setVmoney(YssD.round(YssD.mul(rs.getDouble("FAppreciation"), subTrade.getVmoney()), 2));
				//主动投资本币估值增值 = 本币估值增值-被动投资本币估值增值（本币估值增值 * 被动投资比例）
				subTradeActive.setVBBMoney(YssD.round(YssD.sub(rs.getDouble("FPortAppreciation"), subTrade.getVBBMoney()), 2));
				//交易类型  -主动投资
				subTradeActive.setETFTradeWayCode("ACTIVE");
				alETFSubTradeData.add(subTradeActive);//保存数据
			}
		}catch (Exception e) {
			throw new YssException("设置拆分的ETF交易子表投资数据出错！",e);
		}
	}
	
    //获取组合代码，转换后形如 '001','002'
    private String getPortCodeSQL() {
        String strReturn = "";
        String[] sPortAry;
        if (this.portCodes.trim().length() > 0) {
            sPortAry = this.portCodes.split(",");
            for (int i = 0; i < sPortAry.length; i++) {
                strReturn = strReturn + "'" + sPortAry[i] + "',";
            }
            if (strReturn.length() > 0) {
                strReturn = YssFun.left(strReturn, strReturn.length() - 1);
            }
        }
        return strReturn;
    }
}


























