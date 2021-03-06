package com.yss.main.etfoperation.etfaccbook.easySquareReach;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.StandingBookBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 此类事根据明细表和明细关联表中的数据汇总到台账表中（tb_etf_standingbook）
 * shashijie 2011.07.28 STORY 1434 
 *
 */
public class CreateStandingBook extends CtlETFAccBook{
	private ETFParamSetBean paramSet = null;// ETF参数的实体类
	private ETFParamSetAdmin paramSetAdmin = null;//参数设置操作类
	private HashMap etfParam = null;//保存参数设置
	private String sMaxTradeNum= "00000000000000";//保存最大的当日明细表中的申请编号
	private ArrayList standBookData = new ArrayList();//保存台账bean 数据
	private String securityCodes = "";//证券代码
	private java.util.Date startDate = null;//开始的申赎日期
	private java.util.Date endDate = null;//结束的申赎日期	
	private java.util.Date tradeDate = null;//估值日期
	//private String standingBookType = "";//台账类型
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
	public ArrayList getStandBookData() {
		return standBookData;
	}
	public void setStandBookData(ArrayList standBookData) {
		this.standBookData = standBookData;
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
	public CreateStandingBook(){
		super();
	}
	
	/**
	 * 处理业务的入口方法
	 * @throws YssException
	 */
	public void doManageAll() throws YssException{
		int days = 0;//保存起始日期与截止日期之差
		Date dDate = null;//起始日期
		EachExchangeHolidays holiday = null;//节假日代码
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			days = YssFun.dateDiff(this.startDate,this.endDate);//赋值
			dDate = this.startDate;//开始日期
			for(int i=0;i<=days;i++){//循环日期
				sMaxTradeNum ="00000000000000";//保存最大的当日明细表中的申请编号,每次循环时都要先赋初始值
				doSelectStandindBookData(dDate);//此方法处理明细数据和明细关联数据汇总到台账bean中保存，并放到集合中
				insertStandingBook(dDate);//此方法汇总明细表和明细关联表数据插入到台帐表中
				dDate = YssFun.addDay(dDate,1);//每次循环日期加1
			}
			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	
	/**
	 * 此方法做解析前台传来数据
	 */
	public void initData(Date startDate,Date endDate,Date tradeDate ,
			String portCodes,String standingBookType) throws YssException{
		try{
			this.startDate = startDate;//起始日期
			this.endDate = endDate;//截止日期
			this.tradeDate = tradeDate;//业务日期
			this.portCodes = portCodes;//组合代码
			this.standingBookType = standingBookType;//台账类型
			paramSetAdmin = new ETFParamSetAdmin();//实例化
			paramSetAdmin.setYssPub(pub);//设置pub
			etfParam = paramSetAdmin.getETFParamInfo(this.portCodes); // 根据已选组合代码用于获取相关ETF参数数据
			paramSet = (ETFParamSetBean) etfParam.get(this.portCodes);//参数设置实体bean
		}catch (Exception e) {
			throw new YssException("做解析前台传来数据出错！",e);
		}
	}
	
	/**
	 * 此方法处理明细数据和明细关联数据汇总到台账bean中保存，并放到集合中
	 * @param dDate
	 * @throws YssException
	 */
	private void doSelectStandindBookData(Date dDate) throws YssException{
		StringBuffer buff = null;//拼接SQL语句
		ResultSet rs =null;//结果集
		int DataDirection = 1; //数据方向
		StandingBookBean book = null;//台账实体bean
		HashMap mapBookData = null;//保存台账数据
		String sNum = "";
		try{
			if(standBookData.size()!=0){//集合不为空时，先清空数据
				standBookData.clear();
			}
			Date sDate = getBuyDate(dDate);//选择业务日对应的确认日
			
			mapBookData = new HashMap();//实例化
			
			buff = new StringBuffer(1000);
			//从明细表关联明细关联表中取数据
			/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
			buff.append("select * from (select t1.*,t2.* from (")
				.append("select FNUM as supplyNum,FPORTCODE,FSECURITYCODE,FSTOCKHOLDERCODE, FBROKERCODE,FSEATCODE,")
				.append("FBS,FBUYDATE,FREPLACEAMOUNT,FBRAKETNUM,FUNITCOST,FOREPLACECASH,FHREPLACECASH,FOCREPLACECASH,")
				.append("FHCREPLACECASH,FEXCHANGERATE  as FSHRate,FTRADENUM,FMARKTYPE from ")
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(") t1 left join (select FNUM,FMAKEUPDATE,FUNITCOST as FSupplyUnitCost,")
				.append("FMAKEUPAMOUNT,FOPREPLACECASH,FHPREPLACECASH,FOCPREPLACECASH,FHCPREPLACECASH,FEXRIGHTDATE,")
				.append("FSUMAMOUNT,FREALAMOUNT,FINTEREST,FWARRANTCOST,FREMAINDAMOUNT,FOCREFUNDSUM,FHCREFUNDSUM,FOCANREPCASH,")
				.append("FHCANREPCASH,FREFUNDDATE,FDATAMARK,FDATADIRECTION,FREFNUM,FBBINTEREST,FBBWARRANTCOST,FRIGHTRATE,")
				.append("FEXCHANGERATE as FSupplyRate,FHMAKEUPCOST,FOMAKEUPCOST,FDEFLATIONAMOUNT,FOTHERRIGHT,FBBOTHERRIGHT from ")
				.append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(") t2 on t1.supplyNum = t2.fnum where t1.fportcode in(")
				.append(this.operSql.sqlCodes(this.portCodes)).append(")")
				.append(" and t1.fbuydate = ").append(dbl.sqlDate(sDate))
				.append(" union all ")
				.append(" select t3.*,t4.* from (")
				.append("select FNUM as supplyNum,FPORTCODE,FSECURITYCODE,FSTOCKHOLDERCODE, FBROKERCODE,FSEATCODE,")
				.append("FBS,FBUYDATE,FREPLACEAMOUNT,FBRAKETNUM,FUNITCOST,FOREPLACECASH,FHREPLACECASH,")
				.append("FOCREPLACECASH,FHCREPLACECASH,FEXCHANGERATE  as FSHRate,FTRADENUM,FMARKTYPE from ")
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" t6 where t6.fportcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")")
				.append(" and fbuydate <> ").append(dbl.sqlDate(sDate))
				.append(" and exists (select t5.fnum from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" t5 where (t5.fmakeupdate = ").append(dbl.sqlDate(dDate))
				.append(" or t5.fexrightdate = ").append(dbl.sqlDate(dDate))
				.append(") and t6.fbuydate = to_date(substr(t5.fnum,2,8),'yyyymmdd'))) t3")
				.append(" left join (select FNUM,FMAKEUPDATE,FUNITCOST as FSupplyUnitCost,FMAKEUPAMOUNT,")
				.append("FOPREPLACECASH,FHPREPLACECASH,FOCPREPLACECASH,FHCPREPLACECASH,FEXRIGHTDATE,")
				.append("FSUMAMOUNT,FREALAMOUNT,FINTEREST,FWARRANTCOST,FREMAINDAMOUNT,FOCREFUNDSUM,")
				.append("FHCREFUNDSUM,FOCANREPCASH,FHCANREPCASH,FREFUNDDATE,FDATAMARK,FDATADIRECTION,FREFNUM,")
				.append("FBBINTEREST,FBBWARRANTCOST,FRIGHTRATE,FEXCHANGERATE as FSupplyRate,FHMAKEUPCOST,")
				.append("FOMAKEUPCOST,FDEFLATIONAMOUNT,FOTHERRIGHT,FBBOTHERRIGHT from ")
				.append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" where fmakeupdate = ").append(dbl.sqlDate(dDate))
				.append(" or fexrightdate = ").append(dbl.sqlDate(dDate))
				.append(")t4 on t3.supplyNum = t4.fnum )")
				.append(" order by supplyNum,fportcode,fstockholdercode,fsecuritycode,fbs,fbuydate,FDataMark,fmakeupdate");
			/**end---huhuichao 2013-8-8 STORY  4276*/
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			while(rs.next()){

				book = new StandingBookBean();//实例化
				if(rs.getString("FBS").equals("S")){
					DataDirection = -1;
				}else{
					DataDirection = 1;
				}
				book.setNum(rs.getString("FNum"));//申请编号
				book.setTradeNum(rs.getString("FTradeNum"));//成交编号
				book.setBuyDate(rs.getDate("FBuyDate"));//申赎日期
				book.setBs(rs.getString("FBs"));//台账类型
				book.setPortCode(rs.getString("FPortCode"));//组合代码
				book.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
				book.setStockHolderCode(rs.getString("FStockHolderCode"));//投资者
				book.setBrokerCode(rs.getString("FBrokerCode"));//券商
				book.setSeatCode(rs.getString("FSeatCode"));//交易席位
				book.setMakeUpAmount(YssD.mul(rs.getDouble("FReplaceAmount"),DataDirection));//申赎数量
				book.setUnitCost(rs.getDouble("FUnitCost"));//单位成本
				book.setReplaceCash(rs.getDouble("FHReplaceCash"));//替代金额
				book.setCanReplaceCash(YssD.mul(rs.getDouble("FHCReplaceCash"),DataDirection));//可退替代款
				book.setExRightDate(rs.getDate("FExRightDate"));//权益日期
				book.setSumAmount(YssD.mul(rs.getDouble("FSumAmount"),DataDirection));//总数量
				book.setRealAmount(YssD.mul(rs.getDouble("FRealAmount"),DataDirection));//实际数量
				book.setTotalInterest(YssD.mul(rs.getDouble("FInterest"),DataDirection));//总派息原币
				book.setWarrantCost(YssD.mul(rs.getDouble("FWarrantCost"),DataDirection));//权证价值原币
				book.setBBInterest(YssD.mul(rs.getDouble("FBBInterest"),DataDirection));//总派息本币
				book.setBBWarrantCost(YssD.mul(rs.getDouble("FBBWarrantCost"),DataDirection));//权证价值本币
				book.setRightRate(rs.getDouble("FRightRate"));//权益汇率
				
				//-------第一次补票数据（易方达只有一次补票）------------
				book.setMakeUpDate1(rs.getDate("FMakeUpDate"));//第一次补票日期
				book.setMakeUpAmount1(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));//第一次补票数量
				book.setMakeUpUnitCost1(rs.getDouble("FSupplyUnitCost"));//第一次补票单位成本
				book.setoMakeUpCost1(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));//第一次补票总成本原币
				book.sethMakeUpCost1(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));//第一次补票总成本本币
				book.setMakeUpRepCash1(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));//第一次补票可退替代款原币
				book.setCanMkUpRepCash1(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));//第一次补票可退替代款本币
				book.setExRate1(rs.getDouble("FSupplyRate"));//第一次补票汇率

				book.setRemaindAmount(YssD.mul(rs.getDouble("FRemaindAmount"),DataDirection));//剩余数量
				book.setSumReturn(YssD.mul(rs.getDouble("FHCRefundSum"),DataDirection));//应退合计
				book.setRefundDate(rs.getDate("FRefundDate"));//退款日期
				book.setExchangeRate(rs.getDouble("FSHRate"));//换汇日期
				
				book.setGradeType1(book.getTradeNum());//排序编号1
				book.setGradeType2("T");//排序编号2      空表示汇总项 ,否则表示明细项
				book.setGradeType3(book.getSecurityCode());//排序编号3
				book.setOrderCode(book.getGradeType1()+"##"+book.getGradeType2()+"##"+rs.getString("FMarkType")+
						"##"+book.getGradeType3());//排序编号
				book.setMarkType(rs.getString("FMarkType"));//标志类型 ：实时time或者钆差difference
				sNum = rs.getString("supplyNum");//申请编号
				book.setRateType(" ");
				/**shashijie 2011-08-07*/
				book.setDeflationAmount(rs.getDouble("FDeflationAmount"));
				/**end*/
				/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
				book.setOtherRight(YssD.mul(rs.getDouble("FOTHERRIGHT"),DataDirection));//其他权益原币
				book.setbBOtherRight(YssD.mul(rs.getDouble("FBBOTHERRIGHT"),DataDirection));//其他权益本币
				/**end---huhuichao 2013-8-8 STORY  4276  */
				standBookData.add(book);//保存台账数据到集合中
				mapBookData.put(sNum,book);//保存台账数据到hash表中
			}
			
		}catch (Exception e) {
			throw new YssException("处理明细数据和明细关联数据汇总到台账bean中保存，并放到集合中出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/** shashijie ,2011-7-12 此方法汇总明细表和明细关联表数据插入到台帐表中 */
	private void insertStandingBook(Date dDate) throws YssException{
		StringBuffer buff = null;//拼接SQL语句
 		Connection conn =null;//数据库连接
		boolean bTrans = true;//事物控制标识
		PreparedStatement pst = null;//预处理
		StandingBookBean book = null;//台账实体bean
		ResultSet rs =null;//结果集
		HashMap changeRateData = new HashMap();//保存换汇汇率
		HashMap changeRateDate = new HashMap();//保存换汇日期
		
		long sNum=0;//为了产生的编号不重复
		String strNumDate = ""; //保存交易编号
		try{
			Date sDate = getBuyDate(dDate);//选择业务日对应的确认日
			
			buff = new StringBuffer(500);
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);//设置为事物手动提交
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_etf_standingbook"));//锁定表
			//根据组合代码，申赎日期删除台账数据
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_standingbook"))
				.append(" s where FportCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")")
				.append(" and ( FBuyDate = ").append(dbl.sqlDate(sDate)).append(" or ")
				.append(" exists (select ffdate from (select distinct to_date(substr(fnum,2,8),'yyyymmdd') as ffdate from ")
				.append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" where fmakeupdate = ").append(dbl.sqlDate(dDate))
				.append(" or fexrightdate = ").append(dbl.sqlDate(dDate))
				.append(") t5 where s.fbuydate = t5.ffdate))");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			//获取当天的实际汇率
			buff.append(" select * from ").append(pub.yssGetTableName("tb_etf_bookexratedata"))
				.append(" where FCheckState = 1 and FExRateDate =").append(dbl.sqlDate(sDate));
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			String sKey="";
			while(rs.next()){
				//shashijie 2011.07.12 修改原有汇率KEY的拼接
				if(rs.getString("FBookType").equalsIgnoreCase("B")){
					sKey = rs.getString("FPortCode")+"\t"+rs.getString("FBookType") + "\t" + 
							rs.getDate("FBuyDate");// + "\t" + "T+1";
				}else{//对于赎回的实际汇率都是t+4的实际汇率
					sKey = rs.getString("FPortCode")+"\t"+rs.getString("FBookType") + "\t" + 
							rs.getDate("FBuyDate");// + "\t" + "T+4";
				}
				//~~~~~~end
				changeRateData.put(sKey,new java.lang.Double(rs.getDouble("FExRateValue")));
				changeRateDate.put(sKey,rs.getDate("FExRateDate"));
			}
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_standingbook"));
			buff.append("(FNum,FBuyDate,FBs,FPortCode,FSecurityCode,FStockHolderCode,FBrokerCode,FSeatCode,");
			buff.append(" FMakeUpAmount,FUnitCost,FReplaceCash,FCanReplaceCash,FExRightDate,FSumAmount, " +
					" FRealAmount,FTotalInterest,");
			buff.append(" FWarrantCost,FBBInterest,FBBWarrantCost,FRightRate,FMakeUpDate1,FMakeUpAmount1, " +
					" FMakeUpUnitCost1,FOMakeUpCost1,FHMakeUpCost1,");
			buff.append(" FMakeUpRepCash1,FCanMkUpRepCash1,FMakeUpDate2,FMakeUpAmount2,FMakeUpUnitCost2, " +
					" FOMakeUpCost2,FHMakeUpCost2,FMakeUpRepCash2,");
			buff.append(" FCanMkUpRepCash2,FMakeUpDate3,FMakeUpAmount3,FMakeUpUnitCost3,FOMakeUpCost3, " +
					" FHMakeUpCost3,FMakeUpRepCash3,FCanMkUpRepCash3,");
			buff.append(" FMakeUpDate4,FMakeUpAmount4,FMakeUpUnitCost4,FOMakeUpCost4,FHMakeUpCost4, " +
					" FMakeUpRepCash4,FCanMkUpRepCash4,FMakeUpDate5,");
			buff.append(" FMakeUpAmount5,FMakeUpUnitCost5,FOMakeUpCost5,FHMakeUpCost5,FMakeUpRepCash5, " +
					" FCanMkUpRepCash5,FMustMkUpDate,FMustMkUpAmount,");
			buff.append(" FMustMkUpUnitCost,FOMustMkUpCost,FHMustMkUpCost,FMustMkUpRepCash, " +
					" FMustCMkUpRepCash,FRemaindAmount,FSumReturn,FRefundDate,");
			buff.append(" FExchangeRate,FOrderCode,FGradeType1,FGradeType2,FGradeType3,FExRate1,FExRate2,FExRate3,");
			buff.append(" FExRate4,FExRate5,FMustExRate,FFactExRate,FExRateDate,FMarkType,FRateType, " +
					" FTradeNum,FCreator,FCreateTime ,FDeflationAmount, ");
			/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
			buff.append(" FOTHERRIGHT,FBBOTHERRIGHT )");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");//35
			buff.append(" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");//40
			buff.append(" ?,?,?,?,?,?,?,?,?,?,?)");
			/**end---huhuichao 2013-8-8 STORY  4276*/
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			

			//--------------------拼接交易编号---------------------
			strNumDate = YssFun.formatDatetime(sDate).substring(0, 8);
            strNumDate = strNumDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("tb_etf_standingbook"),
                                       dbl.sqlRight("FNUM", 6), "000000",
                                       " where FNum like 'T" + strNumDate + "%'", 1);
            strNumDate = "T" + strNumDate;
            String s = strNumDate.substring(9, strNumDate.length());
            sNum = Long.parseLong(s);
            //--------------------------------end--------------------------//
			
			for(int i = 0;i < standBookData.size(); i++){//循环保存台账数据的集合
				//--------------------拼接交易编号---------------------
                sNum++;
                String tmp = "";
                for (int j = 0; j < s.length() - String.valueOf(sNum).length(); j++) {
                    tmp += "0";
                }
                strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                if(Double.parseDouble(strNumDate.substring(9,strNumDate.length())) > 
                	Double.parseDouble(sMaxTradeNum.substring(9,sMaxTradeNum.length()))){
					sMaxTradeNum = strNumDate;
				}
                // ------------------------end--------------------------//
				book = (StandingBookBean) standBookData.get(i);
				//以下为预处理赋值
				pst.setString(1,strNumDate);
				pst.setDate(2,YssFun.toSqlDate(book.getBuyDate()));
				pst.setString(3,book.getBs());
				pst.setString(4,book.getPortCode());
				pst.setString(5,book.getSecurityCode());
				pst.setString(6,book.getStockHolderCode());
				pst.setString(7,book.getBrokerCode());
				pst.setString(8,book.getSeatCode());
				pst.setDouble(9,book.getMakeUpAmount());
				pst.setDouble(10,book.getUnitCost());
				pst.setDouble(11,book.getReplaceCash());
				pst.setDouble(12,book.getCanReplaceCash());
				pst.setDate(13,YssFun.toSqlDate(book.getExRightDate()!=null?
						book.getExRightDate():YssFun.toDate("9998-12-31")));
				pst.setDouble(14,book.getSumAmount());
				pst.setDouble(15,book.getRealAmount());
				pst.setDouble(16,book.getTotalInterest());
				pst.setDouble(17,book.getWarrantCost());
				pst.setDouble(18,book.getBBInterest());
				pst.setDouble(19,book.getBBWarrantCost());
				pst.setDouble(20,book.getRightRate());
				
				pst.setDate(21,YssFun.toSqlDate(book.getMakeUpDate1()!=null?
						book.getMakeUpDate1():YssFun.toDate("9998-12-31")));
				pst.setDouble(22,book.getMakeUpAmount1());
				pst.setDouble(23,book.getMakeUpUnitCost1());
				pst.setDouble(24,book.getoMakeUpCost1());
				pst.setDouble(25,book.gethMakeUpCost1());
				pst.setDouble(26,book.getMakeUpRepCash1());
				pst.setDouble(27,book.getCanMkUpRepCash1());
				
				pst.setDate(28,YssFun.toSqlDate(book.getMakeUpDate2()!=null?
						book.getMakeUpDate2():YssFun.toDate("9998-12-31")));
				pst.setDouble(29,book.getMakeUpAmount2());
				pst.setDouble(30,book.getMakeUpUnitCost2());
				pst.setDouble(31,book.getoMakeUpCost2());
				pst.setDouble(32,book.gethMakeUpCost2());
				pst.setDouble(33,book.getMakeUpRepCash2());
				pst.setDouble(34,book.getCanMkUpRepCash2());
				
				pst.setDate(35,YssFun.toSqlDate(book.getMakeUpDate3()!=null?
						book.getMakeUpDate3():YssFun.toDate("9998-12-31")));
				pst.setDouble(36,book.getMakeUpAmount3());
				pst.setDouble(37,book.getMakeUpUnitCost3());
				pst.setDouble(38,book.getoMakeUpCost3());
				pst.setDouble(39,book.gethMakeUpCost3());
				pst.setDouble(40,book.getMakeUpRepCash3());
				pst.setDouble(41,book.getCanMkUpRepCash3());
				
				pst.setDate(42,YssFun.toSqlDate(book.getMakeUpDate4()!=null?
						book.getMakeUpDate4():YssFun.toDate("9998-12-31")));
				pst.setDouble(43,book.getMakeUpAmount4());
				pst.setDouble(44,book.getMakeUpUnitCost4());
				pst.setDouble(45,book.getoMakeUpCost4());
				pst.setDouble(46,book.gethMakeUpCost4());
				pst.setDouble(47,book.getMakeUpRepCash4());
				pst.setDouble(48,book.getCanMkUpRepCash4());
				
				pst.setDate(49,YssFun.toSqlDate(book.getMakeUpDate5()!=null?
						book.getMakeUpDate5():YssFun.toDate("9998-12-31")));
				pst.setDouble(50,book.getMakeUpAmount5());
				pst.setDouble(51,book.getMakeUpUnitCost5());
				pst.setDouble(52,book.getoMakeUpCost5());
				pst.setDouble(53,book.gethMakeUpCost5());
				pst.setDouble(54,book.getMakeUpRepCash5());
				pst.setDouble(55,book.getCanMkUpRepCash5());
				
				pst.setDate(56,YssFun.toSqlDate(book.getMustMkUpDate()!=null?
						book.getMustMkUpDate():YssFun.toDate("9998-12-31")));
				pst.setDouble(57,book.getMustMkUpAmount());
				pst.setDouble(58,book.getMustMkUpUnitCost());
				pst.setDouble(59,book.getoMustMkUpCost());
				pst.setDouble(60,book.gethMustMkUpCost());
				pst.setDouble(61,book.getMustMkUpRepCash());
				pst.setDouble(62,book.getMustCMkUpRepCash());
				
				pst.setDouble(63,book.getRemaindAmount());
				//拼接 汇率日期,换汇汇率 的KEY
				if(book.getBs().equalsIgnoreCase("S")){
					sKey = book.getPortCode()+"\t"+ "B" +"\t"+ book.getBuyDate();
				}else{
					sKey = book.getPortCode()+"\t"+ book.getBs() +"\t"+ book.getBuyDate();
				}
				
				//应退合计
				pst.setDouble(64,book.getSumReturn());
				
				pst.setDate(65,YssFun.toSqlDate(book.getRefundDate()!=null?
						book.getRefundDate():YssFun.toDate("9998-12-31")));
				pst.setDouble(66,book.getExchangeRate());
				pst.setString(67,book.getOrderCode());
				pst.setString(68,book.getGradeType1());
				pst.setString(69,book.getGradeType2());
				pst.setString(70,book.getGradeType3());
				pst.setDouble(71,book.getExRate1());
				pst.setDouble(72,book.getExRate2());
				pst.setDouble(73,book.getExRate3());
				pst.setDouble(74,book.getExRate4());
				pst.setDouble(75,book.getExRate5());
				pst.setDouble(76,book.getMustExRate());
				if(changeRateData.containsKey(sKey)){//实际汇率
					pst.setDouble(77, java.lang.Double.parseDouble(changeRateData.get(sKey).toString()));
				}else{
					pst.setDouble(77,book.getFactExRate());
				}
				if(changeRateDate.containsKey(sKey)){//换汇日期
					pst.setDate(78, YssFun.toSqlDate(changeRateDate.get(sKey).toString()));
				}else{
					pst.setDate(78,YssFun.toSqlDate(book.getExRateDate()!=null?
							book.getExRateDate():YssFun.toDate("9998-12-31")));
				}
				
				pst.setString(79,book.getMarkType());
				pst.setString(80,book.getRateType());
				pst.setString(81,book.getTradeNum());
				pst.setString(82,pub.getUserCode());
				pst.setString(83,YssFun.formatDatetime(new Date()));
				pst.setDouble(84,book.getDeflationAmount());//缩股数量
				/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
				pst.setDouble(85,book.getOtherRight());
				pst.setDouble(86,book.getbBOtherRight());
				/**end---huhuichao 2013-8-8 STORY  4276  */
				pst.addBatch();//增加批处理
			}
			pst.executeBatch();//执行批处理

			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
			
			setStandingBookData(dDate);//处理台账汇总数据
			
		}catch (Exception e) {
			throw new YssException("汇总明细表和明细关联表数据插入到台帐表中出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 此方法处理台账汇总数据
	 * @param pst
	 * @throws YssException
	 */
	private void setStandingBookData(Date dDate) throws YssException{
		StringBuffer buff = null;//做拼接SQL语句
		Connection conn =null;//数据库连接
		boolean bTrans = true;//事物控制标识
		PreparedStatement pst = null;//预处理
		String sRowStr ="";
		Date yesDate = null;//获取倒推出的补票工作日
		EachExchangeHolidays holiday = null;//节假日代码
		ResultSet rs = null;//结果集
		long sNum=0;//为了产生的编号不重复
		String strNumDate = ""; //保存交易编号
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = paramSet.getSHolidayCode() + "\t" + -1 + "\t" + YssFun.formatDate(dDate);
			//解析数据
			holiday.parseRowStr(sRowStr);
			yesDate = getWorkDayMake(dDate,paramSet.getBeginSupply());//获取倒推出的补票工作日
			
			buff = new StringBuffer(500);
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置为手动提交事物
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_etf_standingbook"));//给表加锁
			
			buff.append(" select sum(FMakeUpAmount) as FMakeUpAmount,sum(FReplaceCash) as FReplaceCash,");
			/**add---huhuichao 2013-8-8 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
			buff.append(" sum(FOtherRight) as FOtherRight,sum(FBBOtherRight) as FBBOtherRight,");//汇总其他权益
			/**end---huhuichao 2013-8-8 STORY  4276*/
			buff.append(" sum(FCanReplaceCash) as FCanReplaceCash,max(FExRightDate) as FExRightDate,");
			buff.append(" sum(FSumAmount) as FSumAmount,sum(FRealAmount) as FRealAmount,");
			buff.append(" sum(FTotalInterest) as FTotalInterest,sum(FWarrantCost) as FWarrantCost,");
			buff.append(" sum(FBBInterest) as FBBInterest,sum(FBBWarrantCost) as FBBWarrantCost,");
			buff.append(" max(FRightRate) as FRightRate,max(FMakeUpDate1) as FMakeUpDate1,");
			buff.append(" sum(FMakeUpAmount1) as FMakeUpAmount1,sum(FOMakeUpCost1) as FOMakeUpCost1,");
			buff.append(" sum(FHMakeUpCost1) as FHMakeUpCost1,sum(FMakeUpRepCash1) as FMakeUpRepCash1,");
			buff.append(" sum(FCanMkUpRepCash1) as FCanMkUpRepCash1,max(FMakeUpDate2) as FMakeUpDate2,");
			buff.append(" sum(FMakeUpAmount2) as FMakeUpAmount2,sum(FOMakeUpCost2) as FOMakeUpCost2,");
			buff.append(" sum(FHMakeUpCost2) as FHMakeUpCost2,sum(FMakeUpRepCash2) as FMakeUpRepCash2,");
			buff.append(" sum(FCanMkUpRepCash2) as FCanMkUpRepCash2,max(FMakeUpDate3) as FMakeUpDate3,");
			buff.append(" sum(FMakeUpAmount3) as FMakeUpAmount3,sum(FOMakeUpCost3) as FOMakeUpCost3,");
			buff.append(" sum(FHMakeUpCost3) as FHMakeUpCost3,sum(FMakeUpRepCash3) as FMakeUpRepCash3,");
			buff.append(" sum(FCanMkUpRepCash3) as FCanMkUpRepCash3,max(FMakeUpDate4) as FMakeUpDate4,");
			buff.append(" sum(FMakeUpAmount4) as FMakeUpAmount4,sum(FOMakeUpCost4) as FOMakeUpCost4,");
			buff.append(" sum(FHMakeUpCost4) as FHMakeUpCost4,sum(FMakeUpRepCash4) as FMakeUpRepCash4,");
			buff.append(" sum(FCanMkUpRepCash4) as FCanMkUpRepCash4,max(FMakeUpDate5) as FMakeUpDate5,");
			buff.append(" sum(FMakeUpAmount5) as FMakeUpAmount5,sum(FOMakeUpCost5) as FOMakeUpCost5,");
			buff.append(" sum(FHMakeUpCost5) as FHMakeUpCost5,sum(FMakeUpRepCash5) as FMakeUpRepCash5,");
			buff.append(" sum(FCanMkUpRepCash5) as FCanMkUpRepCash5,max(FMustMkUpDate) as FMustMkUpDate,");
			buff.append(" sum(FMustMkUpAmount) as FMustMkUpAmount,sum(FMustMkUpUnitCost) as FMustMkUpUnitCost,");
			buff.append(" sum(FOMustMkUpCost) as FOMustMkUpCost,sum(FHMustMkUpCost) as FHMustMkUpCost,");
			buff.append(" sum(FMustMkUpRepCash) as FMustMkUpRepCash,sum(FMustCMkUpRepCash) as FMustCMkUpRepCash,");
			buff.append(" sum(FRemaindAmount) as FRemaindAmount,sum(FSumReturn) as FSumReturn,");
			buff.append(" max(FRefundDate) as FRefundDate,max(FExchangeRate) as FExchangeRate,");
			buff.append(" max(FExRate1) as FExRate1,max(FExRate2) as FExRate2,max(FExRate3) as FExRate3,");
			buff.append(" max(FExRate4) as FExRate4,max(FExRate5) as FExRate5,max(FMustExRate) as FMustExRate,");
			buff.append(" max(FFactExRate) as FFactExRate,max(FExRateDate) as FExRateDate,");
			buff.append(" FGradeType1,FStockHolderCode,FTradeNum,FPortcode,FBuyDate,FBs,FBrokerCode,FSeatCode,' ' " +
					" as FSecurityCode from ");
			buff.append(pub.yssGetTableName("tb_etf_standingbook"));
			buff.append(" where FPortcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and ( FBuyDate = ").append(dbl.sqlDate(yesDate)).append(" or ");
			buff.append(" FBuyDate = "+dbl.sqlDate(dDate)+" ) ").append(
					" group by FGradeType1,FStockHolderCode, FTradeNum, FPortcode, " +
					" FBuyDate, FBs,FBrokerCode,FSeatCode");
			
			rs =dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_standingbook"));
			buff.append("(FNum,FBuyDate,FBs,FPortCode,FSecurityCode,FStockHolderCode,FBrokerCode,FSeatCode,");
			buff.append(" FMakeUpAmount,FUnitCost,FReplaceCash,FCanReplaceCash,FExRightDate,FSumAmount, " +
					" FRealAmount,FTotalInterest,");
			buff.append(" FWarrantCost,FBBInterest,FBBWarrantCost,FRightRate,FMakeUpDate1,FMakeUpAmount1, " +
					" FMakeUpUnitCost1,FOMakeUpCost1,FHMakeUpCost1,");
			buff.append(" FMakeUpRepCash1,FCanMkUpRepCash1,FMakeUpDate2,FMakeUpAmount2,FMakeUpUnitCost2, " +
					" FOMakeUpCost2,FHMakeUpCost2,FMakeUpRepCash2,");
			buff.append(" FCanMkUpRepCash2,FMakeUpDate3,FMakeUpAmount3,FMakeUpUnitCost3,FOMakeUpCost3, " +
					" FHMakeUpCost3,FMakeUpRepCash3,FCanMkUpRepCash3,");
			buff.append(" FMakeUpDate4,FMakeUpAmount4,FMakeUpUnitCost4,FOMakeUpCost4,FHMakeUpCost4, " +
					" FMakeUpRepCash4,FCanMkUpRepCash4,FMakeUpDate5,");
			buff.append(" FMakeUpAmount5,FMakeUpUnitCost5,FOMakeUpCost5,FHMakeUpCost5,FMakeUpRepCash5, " +
					" FCanMkUpRepCash5,FMustMkUpDate,FMustMkUpAmount,");
			buff.append(" FMustMkUpUnitCost,FOMustMkUpCost,FHMustMkUpCost,FMustMkUpRepCash,FMustCMkUpRepCash, " +
					" FRemaindAmount,FSumReturn,FRefundDate,");
			buff.append(" FExchangeRate,FOrderCode,FGradeType1,FGradeType2,FGradeType3,FExRate1,FExRate2,FExRate3,");
			/**add---huhuichao 2013-8-8 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
			buff.append(" FExRate4,FExRate5,FMustExRate,FFactExRate,FExRateDate,FMarkType,FRateType, " +
					" FTradeNum,FCreator,FCreateTime,FOtherRight,FBBOtherRight)");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");//35
			buff.append(" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");//40
			buff.append(" ?,?,?,?,?,?,?,?,?,?)");
			/**end---huhuichao 2013-8-8 STORY  4276*/
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			//--------------------拼接交易编号---------------------
			strNumDate = YssFun.formatDatetime(dDate).substring(0, 8);
            strNumDate = strNumDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("tb_etf_standingbook"),
                                       dbl.sqlRight("FNUM", 6), "000000",
                                       " where FNum like 'T" + strNumDate + "%'", 1);
            strNumDate = "T" + strNumDate;
            String s = strNumDate.substring(9, strNumDate.length());
            sNum = Long.parseLong(s);
            //--------------------------------end--------------------------//
			
			while(rs.next()){
				//--------------------拼接交易编号---------------------
                sNum++;
                String tmp = "";
                for (int j = 0; j < s.length() - String.valueOf(sNum).length(); j++) {
                    tmp += "0";
                }
                strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                // ------------------------end--------------------------//
				pst.setString(1,strNumDate);
				pst.setDate(2,YssFun.toSqlDate(rs.getDate("FBuyDate")));
				pst.setString(3,rs.getString("FBs"));
				pst.setString(4,rs.getString("FPortCode"));
				pst.setString(5,rs.getString("FSecurityCode"));
				pst.setString(6,rs.getString("FStockHolderCode"));
				pst.setString(7,rs.getString("FBrokerCode"));
				pst.setString(8,rs.getString("FSeatCode"));
				pst.setDouble(9,rs.getDouble("FMakeUpAmount"));
				pst.setDouble(10,0);
				pst.setDouble(11,rs.getDouble("FReplaceCash"));
				pst.setDouble(12,rs.getDouble("FCanReplaceCash"));
				pst.setDate(13,YssFun.toSqlDate(rs.getDate("FExRightDate")!=null?
						rs.getDate("FExRightDate"):YssFun.toDate("9998-12-31")));
				pst.setDouble(14,rs.getDouble("FSumAmount"));
				pst.setDouble(15,rs.getDouble("FRealAmount"));
				pst.setDouble(16,rs.getDouble("FTotalInterest"));
				pst.setDouble(17,rs.getDouble("FWarrantCost"));
				pst.setDouble(18,rs.getDouble("FBBInterest"));
				pst.setDouble(19,rs.getDouble("FBBWarrantCost"));
				pst.setDouble(20,rs.getDouble("FRightRate"));
				
				pst.setDate(21,YssFun.toSqlDate(rs.getDate("FMakeUpDate1")!=null?
						rs.getDate("FMakeUpDate1"):YssFun.toDate("9998-12-31")));
				pst.setDouble(22,rs.getDouble("FMakeUpAmount1"));
				pst.setDouble(23,0);
				pst.setDouble(24,rs.getDouble("FOMakeUpCost1"));
				pst.setDouble(25,rs.getDouble("FHMakeUpCost1"));
				pst.setDouble(26,rs.getDouble("FMakeUpRepCash1"));
				pst.setDouble(27,rs.getDouble("FCanMkUpRepCash1"));
				
				pst.setDate(28,YssFun.toSqlDate(rs.getDate("FMakeUpDate2")!=null?
						rs.getDate("FMakeUpDate2"):YssFun.toDate("9998-12-31")));
				pst.setDouble(29,rs.getDouble("FMakeUpAmount2"));
				pst.setDouble(30,0);
				pst.setDouble(31,rs.getDouble("FOMakeUpCost2"));
				pst.setDouble(32,rs.getDouble("FHMakeUpCost2"));
				pst.setDouble(33,rs.getDouble("FMakeUpRepCash2"));
				pst.setDouble(34,rs.getDouble("FCanMkUpRepCash2"));
				
				pst.setDate(35,YssFun.toSqlDate(rs.getDate("FMakeUpDate3")!=null?
						rs.getDate("FMakeUpDate3"):YssFun.toDate("9998-12-31")));
				pst.setDouble(36,rs.getDouble("FMakeUpAmount3"));
				pst.setDouble(37,0);
				pst.setDouble(38,rs.getDouble("FOMakeUpCost3"));
				pst.setDouble(39,rs.getDouble("FHMakeUpCost3"));
				pst.setDouble(40,rs.getDouble("FMakeUpRepCash3"));
				pst.setDouble(41,rs.getDouble("FCanMkUpRepCash3"));
				
				pst.setDate(42,YssFun.toSqlDate(rs.getDate("FMakeUpDate4")!=null?
						rs.getDate("FMakeUpDate4"):YssFun.toDate("9998-12-31")));
				pst.setDouble(43,rs.getDouble("FMakeUpAmount4"));
				pst.setDouble(44,0);
				pst.setDouble(45,rs.getDouble("FOMakeUpCost4"));
				pst.setDouble(46,rs.getDouble("FHMakeUpCost4"));
				pst.setDouble(47,rs.getDouble("FMakeUpRepCash4"));
				pst.setDouble(48,rs.getDouble("FCanMkUpRepCash4"));
				
				pst.setDate(49,YssFun.toSqlDate(rs.getDate("FMakeUpDate5")!=null?
						rs.getDate("FMakeUpDate5"):YssFun.toDate("9998-12-31")));
				pst.setDouble(50,rs.getDouble("FMakeUpAmount5"));
				pst.setDouble(51,0);
				pst.setDouble(52,rs.getDouble("FOMakeUpCost5"));
				pst.setDouble(53,rs.getDouble("FHMakeUpCost5"));
				pst.setDouble(54,rs.getDouble("FMakeUpRepCash5"));
				pst.setDouble(55,rs.getDouble("FCanMkUpRepCash5"));
				
				pst.setDate(56,YssFun.toSqlDate(rs.getDate("FMustMkUpDate")!=null?
						rs.getDate("FMustMkUpDate"):YssFun.toDate("9998-12-31")));
				pst.setDouble(57,rs.getDouble("FMustMkUpAmount"));
				pst.setDouble(58,0);
				pst.setDouble(59,rs.getDouble("FOMustMkUpCost"));
				pst.setDouble(60,rs.getDouble("FHMustMkUpCost"));
				pst.setDouble(61,rs.getDouble("FMustMkUpRepCash"));
				pst.setDouble(62,rs.getDouble("FMustCMkUpRepCash"));
				
				pst.setDouble(63,rs.getDouble("FRemaindAmount"));
				pst.setDouble(64,rs.getDouble("FSumReturn"));
				pst.setDate(65,YssFun.toSqlDate(rs.getDate("FRefundDate")!=null?
						rs.getDate("FRefundDate"):YssFun.toDate("9998-12-31")));
				pst.setDouble(66,rs.getDouble("FExchangeRate"));
				pst.setString(67,rs.getString("FGradeType1"));
				pst.setString(68,rs.getString("FGradeType1"));
				pst.setString(69,"");
				pst.setString(70,"");
				pst.setDouble(71,rs.getDouble("FExRate1"));
				pst.setDouble(72,rs.getDouble("FExRate2"));
				pst.setDouble(73,rs.getDouble("FExRate3"));
				pst.setDouble(74,rs.getDouble("FExRate4"));
				pst.setDouble(75,rs.getDouble("FExRate5"));
				pst.setDouble(76,rs.getDouble("FMustExRate"));
				pst.setDouble(77,rs.getDouble("FFactExRate"));
				pst.setDate(78,YssFun.toSqlDate(rs.getDate("FExRateDate")!=null?
						rs.getDate("FExRateDate"):YssFun.toDate("9998-12-31")));
				pst.setString(79,"");
				pst.setString(80," ");
				pst.setString(81,rs.getString("FTradeNum"));
				pst.setString(82,pub.getUserCode());
				pst.setString(83,YssFun.formatDatetime(new Date()));
				/**add---huhuichao 2013-8-8 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
				pst.setDouble(84,rs.getDouble("FOtherRight"));
				pst.setDouble(85,rs.getDouble("FBBOtherRight"));
				/**add---huhuichao 2013-8-8 STORY  4276 */
				pst.addBatch();//增加批处理
			}
			pst.executeBatch();//执行批处理
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("处理台账汇总数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
		}
	}
	
	/**shashijie,2011-7-8 向前推出int BeginSupply个工作日*/
	private Date getWorkDayMake(Date dDate, int beginSupply) throws YssException {
		BaseOperDeal operDeal = new BaseOperDeal();
        operDeal.setYssPub(pub);
        //根据补票日期倒推出申赎日期 
        //因为ETF参数设置调整，相应获取节假日的方式同步调整
		Date mDate = operDeal.getWorkDay((String) paramSet.getHoildaysRela()
				.get("beginsupply"), dDate, beginSupply * -1);
		return mDate;
	}
	
	/**shashijie ,2011-8-12,STORY 1434 ,根据TA交易数据的确认日查询出交易日期,这里的交易日期就是补票的申赎日期*/
	public Date getBuyDate(Date dDate) throws YssException {
		Date dBuyDate = dDate;//交易日,即申赎日
		StringBuffer buffer = new StringBuffer(100);
		ResultSet rs = null;
		try{
			buffer.append("SELECT  FTradeDate From ").append(pub.yssGetTableName("Tb_TA_Trade"))
				.append(" WHERE FConfimDate = ").append(dbl.sqlDate(dDate))//确认日
				.append(" AND FPortCode in ( ").append(operSql.sqlCodes(portCodes) + " ) ")//组合
				.append(" AND FcheckState = 1");
			rs = dbl.openResultSet(buffer.toString());
			if (rs.next()){
				dBuyDate = rs.getDate("FTRADEDATE");
			}
		}catch (Exception e) {
			throw new YssException("获取申赎日期出错 ！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dBuyDate;
	}
		
}
