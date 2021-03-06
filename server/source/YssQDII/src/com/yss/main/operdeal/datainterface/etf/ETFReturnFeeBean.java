package com.yss.main.operdeal.datainterface.etf;

import java.sql.*;
import java.util.*;
import com.yss.util.*;
import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.operdeal.datainterface.pojo.ETFReturnFee;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;

public class ETFReturnFeeBean extends DataBase{

	/**
	 * 构造函数
	 */
	public ETFReturnFeeBean() {
		
	}

	/**
     * 退补款接口数据导出方法
     * @throws YssException
     */
    public void inertData() throws YssException {
    	Connection conn = dbl.loadConnection(); // 新建连接
        boolean bTrans = true;//事务控制标识
        ResultSet rs = null;//结果集声明
        String strSql = "";
        String zqdm = "";//证券代码
        String zqzh = "";//股东代码
        int i = 0;
        HashMap hmNum = new HashMap();
        ETFReturnFee etfReturn = null;
        EachExchangeHolidays holiday = null;//节假日获取类
        java.util.Date buyBDate = null;
        java.util.Date buySDate = null;
        ETFParamSetBean paramSet = null;
        ETFParamSetAdmin paramSetAdmin = null;
        HashMap etfParam = null;
        String holidayCode = "";
        ArrayList alReturnFee = new ArrayList();
        PreparedStatement pst = null; // 声明PreparedStatement
    	try{
    		paramSetAdmin = new ETFParamSetAdmin();
			paramSetAdmin.setYssPub(pub);
    		
    		holiday = new EachExchangeHolidays();
			holiday.setYssPub(pub);
    		
			etfParam = paramSetAdmin.getETFParamInfo(this.sPort); // 根据已选组合代码
			
			paramSet = (ETFParamSetBean) etfParam.get(this.sPort);// 根据组合代码获取ETF参数设置
			
			if(paramSet != null){
				holidayCode = paramSet.getSHolidayCode();
			}
			
			//若为申购，则查询申购赎回的申请日期为T-1的数据
			holiday.parseRowStr(holidayCode+"\t-1\t" + YssFun.formatDate(this.sDate, "yyyy-MM-dd"));
			buyBDate = YssFun.parseDate(holiday.getOperValue("getWorkDate"),"yyyy-MM-dd");
			
			//若为赎回，则查询申购赎回的申请日期为T-4的数据
			holiday.parseRowStr(holidayCode+"\t-4\t" + YssFun.formatDate(this.sDate, "yyyy-MM-dd"));
			buySDate = YssFun.parseDate(holiday.getOperValue("getWorkDate"),"yyyy-MM-dd");// 格式化
    		
			strSql = 
		    " select c.JJDM, c.ZQZH, c.JYXW, c.XWHY, c.TKJE, c.BKJE, c.JSRQ, c.JYRQ, c.ZQDM from ( " +
			" select pa.fonegrademktcode as JJDM, book.FStockHolderCode as ZQZH, " +
			" book.FSeatCode as JYXW, book.FBrokerCode as XWHY, (case when book.FSumReturn > 0 " +
			" then book.FSumReturn else 0 end) as TKJE, " +
			" (case when book.FSumReturn < 0 then -book.FSumReturn else 0 end) as BKJE, " +
			" SUBSTR(to_char(book.FRefundDate, 'yyyyMMdd'), 0, 8) as JSRQ, " +
			" SUBSTR(to_char(book.FBuyDate, 'yyyyMMdd'), 0, 8) as JYRQ, 'C' as ZQDM, " +
			" book.FBuyDate, book.Ftradenum from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			" book left join (select * from " + pub.yssGetTableName("TB_ETF_Param") + 
			" where FCheckState = 1) pa on book.fportcode = pa.fportcode " +
			" where book.FStockHolderCode <> ' ' and book.FSecurityCode = ' ' and  " +
			" book.FBuyDate = " + dbl.sqlDate(buyBDate) +
			" and book.Fportcode = " + dbl.sqlString(this.sPort) + 
			" and FBS = 'B') c " + 
			" union all " + 
			" select b.JJDM, b.ZQZH, b.JYXW, " +
			" b.XWHY, b.TKJE, b.BKJE, b.JSRQ, b.JYRQ, b.ZQDM from ( " + 
			" select pa.fonegrademktcode as JJDM, " +
			" book.FStockHolderCode as ZQZH, book.FSeatCode as JYXW, book.FBrokerCode as XWHY, " +
			" (case when book.FSumReturn < 0 then -book.FSumReturn else 0 end) as TKJE, " +
			" 0 as BKJE, " + 
			" SUBSTR(to_char(book.FRefundDate, 'yyyyMMdd'), 0, 8) as JSRQ, " +
			" SUBSTR(to_char(book.FBuyDate, 'yyyyMMdd'), 0, 8) as JYRQ, 'D' as ZQDM, " +
			" book.FBuyDate, book.Ftradenum from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			" book left join (select * from " + pub.yssGetTableName("TB_ETF_Param") + 
			" where FCheckState = 1) pa on book.fportcode = pa.fportcode " +
			" where book.FStockHolderCode <> ' ' and book.FSecurityCode = ' ' " +
			" and book.FBuyDate = " + dbl.sqlDate(buySDate) + 
			" and book.Fportcode = " + dbl.sqlString(this.sPort) +
			" and FBs = 'S' order by book.FBuyDate, book.Ftradenum) b " +
			" union all " + 
			" (select distinct a.JJDM, a.ZQZH, a.JYXW, a.XWHY, (case when  " +
			" tradestl.fbraketnum * navdata.FPortMarketValue > 0 then " +
			" tradestl.fbraketnum * navdata.FPortMarketValue else 0 end) as TKJE, " +
			" (case when tradestl.fbraketnum * navdata.FPortMarketValue < 0 then " +
			" -tradestl.fbraketnum * navdata.FPortMarketValue else 0 end) as BKJE, " +
			" a.JSRQ, a.JYRQ, a.ZQDM from ( " +
			" select pa.fonegrademktcode as JJDM, " +
			" book.FStockHolderCode as ZQZH, book.FSeatCode as JYXW, book.FBrokerCode as XWHY, " +
			" SUBSTR(to_char(book.FRefundDate, 'yyyyMMdd'), 0, 8) as JSRQ, " +
			" SUBSTR(to_char(book.FBuyDate, 'yyyyMMdd'), 0, 8) as JYRQ, " + 
			" 'B' as ZQDM, book.ftradenum, book.Fbuydate, book.Fportcode from " + 
			pub.yssGetTableName("Tb_ETF_StandingBook") + 
			" book left join (select * from " + pub.yssGetTableName("TB_ETF_PARAM") +
			" where FCheckState = 1) pa on book.fportcode = pa.fportcode " + 
			" where book.FStockHolderCode <> ' ' and book.FSecurityCode = ' ' " + 
			" and book.FBuyDate = " + dbl.sqlDate(buySDate) + 
			" and book.Fportcode = " + dbl.sqlString(this.sPort) + 
			" and FBs = 'S' order by FBuyDate, FTradeNum) a " + 
			" left join " + 
			" (select * from " + pub.yssGetTableName("Tb_ETF_Tradestldtl") + 
			" ) tradestl on a.ftradenum = tradestl.ftradenum " + 
			" and a.Fbuydate = tradestl.fbuydate " + 
			" left join " + 
			" (select FPortMarketValue, FPortCode from " + pub.yssGetTableName("Tb_ETF_NavData") + 
			" where FNavDate = " + dbl.sqlDate(this.sDate) +
			" and FKeyCode = 'UnitCashBal') navdata on navdata.FPortCode = a.FPortCode) " ;

			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				zqdm = rs.getString("ZQDM");
				zqzh = rs.getString("ZQZH");
				
				if(hmNum.get(zqzh + "," + zqdm) == null){
					i= 1;
				}
				else{
					i = Integer.parseInt((String)hmNum.get(zqzh + "," + zqdm));
					i += 1;
				}
				
				hmNum.put(zqzh + "," + zqdm, String.valueOf(i));

				zqdm += YssFun.formatNumber(i, "00000");

				etfReturn = new ETFReturnFee();
				
				etfReturn.setJJDM(rs.getString("JJDM"));
				etfReturn.setZQZH(rs.getString("ZQZH"));
				etfReturn.setJYXW(rs.getString("JYXW"));
				etfReturn.setXWHY(rs.getString("XWHY"));
				etfReturn.setTKJE(rs.getDouble("TKJE"));
				etfReturn.setBKJE(rs.getDouble("BKJE"));
				etfReturn.setJSRQ(rs.getString("JSRQ"));
				etfReturn.setJYRQ(rs.getString("JYRQ"));
				etfReturn.setZQDM(zqdm);
				
				alReturnFee.add(etfReturn);
			}
			
			if(alReturnFee.size() != 0){
				conn.setAutoCommit(false);
	    		
				strSql = "delete from tmp_etf_returnAdd";
				dbl.executeSql(strSql);
				
				strSql = " insert into tmp_etf_returnAdd(JJDM,ZQZH,JYXW,XWHY,TKJE,BKJE,JSRQ,JYRQ,ZQDM) " +
				" values(?,?,?,?,?,?,?,?,?) ";
				
				pst = dbl.openPreparedStatement(strSql);
				
				for(int j = 0; j < alReturnFee.size(); j++){
					etfReturn = (ETFReturnFee)alReturnFee.get(j);

					pst.setString(1, etfReturn.getJJDM());
					pst.setString(2, etfReturn.getZQZH());
					pst.setString(3, etfReturn.getJYXW());
					pst.setString(4, etfReturn.getXWHY());
					pst.setString(5, etfReturn.getTKJE() + "");
					pst.setString(6, etfReturn.getBKJE() + "");
					pst.setString(7, etfReturn.getJSRQ());
					pst.setString(8, etfReturn.getJYRQ());
					pst.setString(9, etfReturn.getZQDM());
					
					pst.addBatch();
				}
				
				pst.executeBatch();
				
	    		conn.commit();
	    		bTrans = false;
	    		conn.setAutoCommit(true);
			}
    	}
    	catch(Exception e){
    		throw new YssException("退补款接口数据导出出错！",e);
    	}
    	finally{
    		dbl.closeResultSetFinal(rs);
    		dbl.closeStatementFinal(pst);
    		dbl.endTransFinal(conn,bTrans);
    	}
    }
}
