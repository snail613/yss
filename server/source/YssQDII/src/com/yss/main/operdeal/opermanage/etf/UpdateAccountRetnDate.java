package com.yss.main.operdeal.opermanage.etf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 根据交收日而非交易日来设定台帐的退款日期（华夏恒指ETF）
 * 由于在台帐生成时，退款日期是通过判断节假日群的设置来生成的，
 * 而节假日群中只能进行交易日的设定，当交易日为非交收日时，需通过此类来更新台帐的退款日期
 * @author Administrator
 *
 */
public class UpdateAccountRetnDate extends BaseOperManage {

	private HashMap holidays = null;//保存节假日群代码
	private ETFParamSetAdmin etfParamAdmin = null;
	private HashMap etfParam = null;
	private ETFParamSetBean paramSet = null;
	
	public void doOpertion() throws YssException {
		if(paramSet == null){
			throw new YssException("组合【" + sPortCode + "】对应的ETF参数设置不存在或未审核！");
		}
		
		/**shashijie 2011-12-16 STORY 1434 */
		if(paramSet.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)){//华夏
			doUpdate();
		} else if (paramSet.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_ONE)) {//易方达
			if (isFUnsettleDate(this.dDate,this.sPortCode)) {
				updateFRefundDate();
			}
		}
		/**end*/
		
	}

	private void doUpdate() throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
		HashMap hMap= new HashMap();
		try{
			buff = new StringBuffer(200);
			buff.append("select * from ").append(pub.yssGetTableName("Tb_ETF_UnSettleDateSet"));
			buff.append(" where fportcode = ").append(dbl.sqlString(sPortCode));
			buff.append(" and FUnsettleDate = ").append(dbl.sqlDate(dDate));
			
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0, buff.length());
			if(rs.next()){
				hMap = getReturnDate();
				updateReturnDate(hMap);
			}
		}catch(Exception e){
			throw new YssException("更新台帐退款日期出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 更新台帐退款日期
	 * @param hMap
	 * @throws YssException
	 */
	private void updateReturnDate(HashMap hMap) throws YssException {
		String strSql = "";
		String strKey = "";
		java.sql.Date dTempDate = null;
		StringBuffer buff = null;
		Connection conn = null;
		boolean bTrans = true;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try{
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			
			strSql = "update " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
					" set FRefundDate = ? where FNum = ?";
			pst = dbl.getPreparedStatement(strSql);
			
			buff = new StringBuffer(1000);
			buff.append("select FNum,FMustMkUpDate,'M' as FRefMark,FRefundDate from ")
				.append(pub.yssGetTableName("tb_etf_standingbook"))
				.append(" where FMustMkUpDate < ").append(dbl.sqlDate(dDate))
				.append(" and FRefundDate > ").append(dbl.sqlDate(dDate))
				.append(" and FMustMkUpDate <> to_date('9998-12-31','yyyy-mm-dd') and FBS = 'S' and FRemaindAmount = 0")
				.append(" and FPortCode = ").append(dbl.sqlString(sPortCode))
				.append(" union select FNum,FMakeUpDate2 as FMustMkUpDate,'M' as FRefMark,FRefundDate from ")
				.append(pub.yssGetTableName("tb_etf_standingbook"))
				.append(" where FMakeUpDate2 < ").append(dbl.sqlDate(dDate))
				.append(" and FRefundDate > ").append(dbl.sqlDate(dDate))
				.append(" and FMakeUpDate2 <> to_date('9998-12-31','yyyy-mm-dd') and FBS = 'S' and FRemaindAmount = 0 and FMakeUpAmount <> FMakeUpAmount1")
				.append(" and FPortCode = ").append(dbl.sqlString(sPortCode))
				.append(" union select FNum,FMakeUpDate1 as FMustMkUpDate, 'F' as FRefMark,FRefundDate from ")
				.append(pub.yssGetTableName("tb_etf_standingbook"))
				.append(" where FMakeUpDate1 < ").append(dbl.sqlDate(dDate))
				.append(" and FRefundDate > ").append(dbl.sqlDate(dDate))
				.append(" and FMakeUpDate1 <> to_date('9998-12-31','yyyy-mm-dd') and FBS = 'S' and FRemaindAmount = 0 and FMakeUpAmount = FMakeUpAmount1")
				.append(" and FPortCode = ").append(dbl.sqlString(sPortCode));
			
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0, buff.length());
			while(rs.next()){
				strKey = rs.getDate("FMustMkUpDate") + "\t" + rs.getString("FRefMark");
				dTempDate = (java.sql.Date)hMap.get(strKey);
				if(YssFun.dateDiff(rs.getDate("FRefundDate"), dTempDate) == 0){
					continue;
				}
				pst.setDate(1, dTempDate);
				pst.setString(2, rs.getString("FNum"));		
				pst.addBatch();		
			}
			
			pst.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("根据交收日更新台帐退款日期出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 根据交收日获取退款日期
	 * @throws YssException
	 */
	private HashMap getReturnDate() throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
		Date dTempDate = null;
		Date dSHDuesDate = null;
		EachExchangeHolidays holiday = null;//节假日代码类
		String sRowStr ="";
		HashMap hMap = new HashMap();
		String strKey = "";
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			
			buff = new StringBuffer(1000);
			buff.append("select distinct FMustMkUpDate,FRefMark from (select FNum,FMustMkUpDate,'M' as FRefMark from ")
				.append(pub.yssGetTableName("tb_etf_standingbook"))
				.append(" where FMustMkUpDate < ").append(dbl.sqlDate(dDate))
				.append(" and FRefundDate > ").append(dbl.sqlDate(dDate))
				.append(" and FMustMkUpDate <> to_date('9998-12-31','yyyy-mm-dd') and FBS = 'S' and FRemaindAmount = 0")
				.append(" and FPortCode = ").append(dbl.sqlString(sPortCode))
				.append(" union select FNum,FMakeUpDate2 as FMustMkUpDate,'M' as FRefMark from ")
				.append(pub.yssGetTableName("tb_etf_standingbook"))
				.append(" where FMakeUpDate2 < ").append(dbl.sqlDate(dDate))
				.append(" and FRefundDate > ").append(dbl.sqlDate(dDate))
				.append(" and FMakeUpDate2 <> to_date('9998-12-31','yyyy-mm-dd') and FBS = 'S' and FRemaindAmount = 0 and FMakeUpAmount <> FMakeUpAmount1")
				.append(" and FPortCode = ").append(dbl.sqlString(sPortCode))
				.append(" union select FNum,FMakeUpDate1 as FMustMkUpDate, 'F' as FRefMark from ")
				.append(pub.yssGetTableName("tb_etf_standingbook"))
				.append(" where FMakeUpDate1 < ").append(dbl.sqlDate(dDate))
				.append(" and FRefundDate > ").append(dbl.sqlDate(dDate))
				.append(" and FMakeUpDate1 <> to_date('9998-12-31','yyyy-mm-dd') and FBS = 'S' and FRemaindAmount = 0 and FMakeUpAmount = FMakeUpAmount1)");
			
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0, buff.length());
			while(rs.next()){
				dTempDate = rs.getDate("FMustMkUpDate");//获取强制处理日期
				if(rs.getString("FRefMark").equals("F")){//第一次补票补完的情况，默认为第一次补票日期的下一个香港交易日为其强制处理日
					sRowStr = (holidays.containsKey(YssOperCons.YSS_ETF_OVERTYPE_LASTDEALDAYNUM) ? 
									(String) holidays.get(YssOperCons.YSS_ETF_OVERTYPE_LASTDEALDAYNUM) : 
										paramSet.getSHolidayCode()) + "\t" + 1 + "\t" + YssFun.formatDate(dTempDate);
					holiday.parseRowStr(sRowStr);
					dTempDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));
				}
				for(int i =0;i<2;i++){//2代表香港交易日
					//赎回退款日期规则：强制处理日+2日+1日，其中2按香港交易日处理，1按深圳交易日处理，如强制处理日+2日为非深圳交易日，则顺延至下个深圳交易日再+1
					sRowStr = (holidays.containsKey(YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE) ? 
									(String)holidays.get(YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE) : 
										paramSet.getSHolidayCode()) + "\t" + 1 + "\t" + YssFun.formatDate(dTempDate);
					holiday.parseRowStr(sRowStr);//解析参数
					dTempDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));	
					while(judgeBeUnsettleDate(dTempDate)){//如果为非交收日
						sRowStr = (holidays.containsKey(YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE) ? 
									(String)holidays.get(YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE) : 
										paramSet.getSHolidayCode()) + "\t" + 1 + "\t" + YssFun.formatDate(dTempDate);
						holiday.parseRowStr(sRowStr);//解析参数
						dTempDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));
					}	
				}
				//再按深圳交易日处理+1
				sRowStr = paramSet.getSHolidayCode() + "\t" + 0 + "\t" + YssFun.formatDate(dTempDate);
				holiday.parseRowStr(sRowStr);//解析参数
				dTempDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取当天的最近一个工作日
				sRowStr = paramSet.getSHolidayCode() + "\t" + 1 + "\t" + YssFun.formatDate(dTempDate);
				holiday.parseRowStr(sRowStr);//解析参数
				dSHDuesDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取赎回退款日期
				
				strKey = rs.getDate("FMustMkUpDate") + "\t" + rs.getString("FRefMark");
				hMap.put(strKey, dSHDuesDate);
			}
			return hMap;
		}catch(Exception e){
			throw new YssException("根据交收日获取台帐退款日期出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**判断是否为交收日,是返回true
	 * @param date
	 * @throws YssException
	 * @author shashijie ,2011-12-16 , STORY 1434
	 */
	private boolean judgeBeUnsettleDate(Date date) throws YssException {
		boolean bUnsettleDate = false;
		StringBuffer buff = null;
		ResultSet rs = null;
		String strHolidayCode = "";
		try{
			strHolidayCode = paramSet.getsCrossHolidayCode();
			if(strHolidayCode == null){
				strHolidayCode = " ";
			}
			buff = new StringBuffer(200);
			buff.append("select * from ").append(pub.yssGetTableName("Tb_ETF_UnSettleDateSet"));
			buff.append(" where fportcode = ").append(dbl.sqlString(sPortCode));
			buff.append(" and FUnsettleDate = ").append(dbl.sqlDate(date));
			buff.append(" and FCheckState = 1 and FHolidayCode = (select FCrossHolidaysCode from ");
			buff.append(pub.yssGetTableName("Tb_ETF_Param"));
			buff.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(sPortCode)).append(")");
			
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0, buff.length());
			if(rs.next()){
				bUnsettleDate = true;
			}
		}catch(Exception e){
			throw new YssException("判断指定日期是否为非交收日出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return bUnsettleDate;
	}

	public void initOperManageInfo(Date dDate, String portCode) throws YssException {
		this.sPortCode = portCode;
		this.dDate = dDate;
		
		etfParamAdmin = new ETFParamSetAdmin();
		etfParamAdmin.setYssPub(pub);
		etfParam = etfParamAdmin.getETFParamInfo(sPortCode);
		paramSet = (ETFParamSetBean)etfParam.get(sPortCode);
		holidays = paramSet.getHoildaysRela();//获取保存节假日代码的hash表
	}

	/**更新退款日期,更具非指定交收日
	 * @param dDate
	 * @param portCodes
	 * @author shashijie ,2011-12-16 , STORY 1434
	 */
	public void updateFRefundDate() throws YssException {
		ResultSet rs = null;
		boolean bTrans = true;
		Connection conn = null;
		Statement st = null;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			st = dbl.openStatement();
			
			//获取补票起始日到退款日期之间的天数
			HashMap mapDay = getFRefundDateMun();
			
			//获取退款日期对应的补票日
			String strSql = getFRefundDate(dDate,sPortCode);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				//获取补票日期,重新计算退款日期
				doOpertionFRefundDate(rs,mapDay,st);
			}
			
			st.executeBatch();
			conn.commit();
			bTrans =false;
			conn.setAutoCommit(true);
			
		} catch (Exception e) {
			throw new YssException("更新退款日期,更具非指定交收日出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
			dbl.closeStatementFinal(st);
		}
		
	}

	/**重新计算退款日期,并更新入台长表
	 * @param rs
	 * @param mapDay
	 * @author shashijie ,2011-12-16 , STORY 1434
	 */
	private void doOpertionFRefundDate(ResultSet rs, HashMap mapDay,Statement st) throws Exception {
		Date dMakeupDate = rs.getDate("FMakeupDate1");//获取补票日期
		String FBs = rs.getString("FBs");//申赎标示
		String FPortCode = rs.getString("FPortCode");//组合
		
		//退款日期推算天数
		int day = Double.valueOf(mapDay.get(FBs).toString()).intValue();//补票日期推算几个工作日的天数
		//退款日期
		Date FRefundDate = getWorkDayMakeBack(dMakeupDate, day,FPortCode);
		//更新退款日期
		updateBookFRefundDate(FPortCode,FBs,dMakeupDate,FRefundDate,st);
	}
	
	/**更新退款日期
	 * @param fPortCode 组合
	 * @param fBs 申赎
	 * @param dMakeupDate 补票日期
	 * @param fRefundDate 退款日期
	 * @param st 
	 * @author shashijie ,2011-12-16 , STORY 1434
	 * @modified 
	 */
	private void updateBookFRefundDate(String fPortCode, String fBs,
			Date dMakeupDate, Date fRefundDate,Statement st) throws SQLException {
		String sql = "update "+pub.yssGetTableName("Tb_ETF_StandingBook")+
			" set FRefundDate ="+dbl.sqlDate(fRefundDate)+" where FPortCode ="+dbl.sqlString(fPortCode)+
			" and FMakeupDate1 ="+dbl.sqlDate(dMakeupDate)+" and FBs ="+dbl.sqlString(fBs);
		st.addBatch(sql);
	}

	/**向后推出N个工作日(考虑国内国外与交收日)
	 * @param dDate 日期
	 * @param beginSupply 天数
	 * @author shashijie ,2011-12-16 , STORY 1434
	 */
	private Date getWorkDayMakeBack(Date dDate, int beginSupply,String FPortCode) throws YssException {
		BaseOperDeal operDeal = new BaseOperDeal();
		Date makeDate = dDate;//国内
		Date workDate = dDate;//国外
        operDeal.setYssPub(pub);
        for(int i = 0 ; i < beginSupply ; i++){
    		makeDate = operDeal.getWorkDay(paramSet.getSHolidayCode(), makeDate, 1);//国内
    		workDate = operDeal.getWorkDay(paramSet.getsCrossHolidayCode(), workDate, 1);//国外
    		//存入国内国外同一天工作日
    		setWordDateDiff(makeDate,workDate);
    		
    		//如果是非指定交收日则再加一天
    		while(isFUnsettleDate(workDate,FPortCode)) {
    			makeDate = operDeal.getWorkDay(paramSet.getSHolidayCode(), makeDate, 1);//国内
    			workDate = operDeal.getWorkDay(paramSet.getsCrossHolidayCode(), workDate, 1);//国外
			}
    		//存入国内国外同一天工作日
    		setWordDateDiff(makeDate,workDate);
        }
		return makeDate;
	}

	/**存入国内国外同一天工作日
	 * @param makeDate
	 * @param workDate
	 * @author shashijie ,2011-12-19 , STORY 1434
	 */
	private void setWordDateDiff(Date makeDate, Date workDate) throws YssException {
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
		while (YssFun.dateDiff(makeDate,workDate)!=0) {
			if(YssFun.dateDiff(makeDate,workDate) > 0){
				makeDate = operDeal.getWorkDay(paramSet.getSHolidayCode(), makeDate, 1);//国内
			}else{
				workDate = operDeal.getWorkDay(paramSet.getsCrossHolidayCode(), workDate, 1);//国外
			}
		}
	}

	/**判断是否是交收日,是则返回true
	 * @param dDate
	 * @param fPortCode
	 * @author shashijie ,2011-12-16 , STORY 1434
	 */
	private boolean isFUnsettleDate(Date dDate, String fPortCode) throws YssException {
		boolean bUnsettleDate = false;
		ResultSet rs = null;
		try{
			String sql = "select FHolidayCode From "+
				pub.yssGetTableName("Tb_ETF_UnSettleDateSet")+
				" where fportcode = "+dbl.sqlString(fPortCode)+
				" and FUnsettleDate = "+dbl.sqlDate(dDate)+
				" and FCheckState = 1 and FHolidayCode = (select FCrossHolidaysCode from "+
				pub.yssGetTableName("Tb_ETF_Param")+" where FCheckState = 1 and FPortCode = "+
				dbl.sqlString(fPortCode)+")";
			
			rs = dbl.openResultSet(sql);
			
			if(rs.next()){
				bUnsettleDate = true;
			}
		}catch(Exception e){
			throw new YssException("判断指定日期是否为非交收日出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return bUnsettleDate;
	}

	/**获取退款日期对应的补票日
	 * @param dDate
	 * @param sPortCode
	 * @author shashijie ,2011-12-16 , STORY 1434
	 */
	private String getFRefundDate(Date dDate, String sPortCode) {
		String sqlString = "Select a.FBs,a.Fmakeupdate1,Sum(1) As counts,a.FPortCode From " +
			pub.yssGetTableName("Tb_Etf_Standingbook")+" a Where a.Fmakeupdate1 < "+dbl.sqlDate(dDate)+
			" And a.Frefunddate >= "+dbl.sqlDate(dDate)+" And a.FPortCode In ("+operSql.sqlCodes(sPortCode)+" ) "+
			" Group By a.FBs,a.Fmakeupdate1,a.FPortCode ";
		return sqlString;
	}

	/**获取补票起始日到退款日期之间的天数
	 * @author shashijie ,2011-12-16 , STORY 1434
	 */
	private HashMap getFRefundDateMun() throws YssException {
		HashMap dayMap = new HashMap();
		double B = 0;//申购
		double S = 0;//赎回
		try {
			B = YssD.sub(paramSet.getISGDealReplace(), paramSet.getBeginSupply());
			S = YssD.sub(paramSet.getISHDealReplace(), paramSet.getBeginSupply());
			dayMap.put("B", B);
			dayMap.put("S", S);
		} catch (Exception e) {
			throw new YssException("获取补票起始日到退款日期之间的天数出错！",e);
		}
		return dayMap;
	}

}
