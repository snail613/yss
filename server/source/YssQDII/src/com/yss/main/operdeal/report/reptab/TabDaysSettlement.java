package com.yss.main.operdeal.report.reptab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.base.BaseAPOperValue;
import com.yss.main.dayfinish.OffAcctBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/**************************************************
 * 太平资产用表     02當日買賣清算
 * MS01748 QDV4太平2010年09月16日02_A  
 * 太平标准月报18张报表，需要保存，并能批量导出 
 * @author benson 2010.10.14
 *
 */
public class TabDaysSettlement extends BaseAPOperValue {

    //=====================================================================================//
	
	//~ 前台传过来的参数
	private java.util.Date dBeginDate;
    private java.util.Date dEndDate;
    private String portCode;
    private String invmgrCode;
    private String sortCol;                //排序列
    private String sortStyle;              //排序方式
    private boolean isCreate;              //是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询
	
    
    /**
    * <p>Title: </p>
    * 内部类
    */
   private class DaysSettlementBean {
	   //~ 报表属性
	    private String sSecurityCode = "";
	    private String sCatCode = "";
	    private String sportCode="";
	    private java.sql.Date  dBargainDate = null;
	    private String sInvmgrCode = "";
	    private String sBrokerCode = "";
	    private String sCuryCode = "";
	    private String sTradeTypeCode = "";
	    private double dTradePrice = 0;
	    private double dTradeAmount = 0;
	    private double dTradeMoney = 0;
	    private double dFee = 0;
	    private double dYslx = 0;
	    private double dYflx = 0;
	    private double dByslx = 0;
	    private double dByflx = 0;
	    private double dMrQsk = 0;
	    private double dMcQsk = 0;
	    private double dMrQsk_HKD = 0;
	    private double dMcQsk_HKD = 0;
	    private double dCost = 0;
	    private double dBasecuryCost = 0;
	    private double dGainlose = 0;       //盈亏金额
	    private double dGainLose_HKD = 0;   //盈亏金额(港币)
	    private double dRate = 0;
	    
      public void DaysSettlementBean() {
      }
      
   }
	//=====================================================================================//
	public TabDaysSettlement(){
		
	}
	
	
	//=====================================================================================//
	
	public void init(Object bean) throws YssException {
		String reqAry[] = null;
		String reqAry1[] = null;
		String sRowStr = (String) bean;
		if (sRowStr.trim().length() == 0) {
			return;
		}
		reqAry = sRowStr.split("\n");
//		reqAry1 = reqAry[0].split("\r");
//		this.dBeginDate = YssFun.toDate(reqAry1[1]);
//		reqAry1 = reqAry[1].split("\r");
//		this.dEndDate = YssFun.toDate(reqAry1[1]);
//		reqAry1 = reqAry[2].split("\r");
//		this.portCode = reqAry1[1];
//		reqAry1 = reqAry[3].split("\r");
//		this.invmgrCode = reqAry1[1];
//		reqAry1 = reqAry[4].split("\r");
//		this.sortCol = reqAry1[1];
//		reqAry1 = reqAry[5].split("\r");
//		this.sortStyle = reqAry1[1];
//		reqAry1 = reqAry[6].split("\r"); 
//		if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0，则只查询已生成的报表数据
//			this.isCreate = false;
//		} else { // 生成报表
//			this.isCreate = true;
//		}
		//==================修改解析控件的值，前台控件值为空时不传值导致解析出错  edit by qiuxufeng 20101109 
		for (int i = 0; i < reqAry.length; i++) {
			reqAry1 = reqAry[i].split("\r");
			if(reqAry1[0].equalsIgnoreCase("1")) {
				this.dBeginDate = YssFun.toDate(reqAry1[1]);
			} else if(reqAry1[0].equalsIgnoreCase("2")) {
				this.dEndDate = YssFun.toDate(reqAry1[1]);
			} else if(reqAry1[0].equalsIgnoreCase("3")) {
				this.portCode = reqAry1[1];
			} else if(reqAry1[2].equalsIgnoreCase("ComboBox")) {
				if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0，则只查询已生成的报表数据
					this.isCreate = false;
				} else { // 生成报表
					this.isCreate = true;
				}
			} else if(reqAry1[0].equalsIgnoreCase("4")) {
				this.invmgrCode = reqAry1[1];
			} else if(reqAry1[0].equalsIgnoreCase("5")) {
				this.sortCol = reqAry1[1];
			} else if(reqAry1[0].equalsIgnoreCase("6")) {
				this.sortStyle = reqAry1[1];
			}
		}
		//=========end=========
	}

	public Object invokeOperMothed() throws YssException {
		HashMap valueMap = null;
		try {
			valueMap = new HashMap();
			createTable();
			if (this.isCreate) {
				//===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
				OffAcctBean offAcct = new OffAcctBean();
				offAcct.setYssPub(this.pub);
				String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
				String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
				if(!tmpInfo.trim().equalsIgnoreCase("")) {
					return "<OFFACCT>" + tmpInfo;
				}
				//=================end=================
				processDealData(valueMap);
			}
			// ------------------------------------------------------------
		} catch (YssException ex) {
			throw new YssException(ex.getMessage());
		}
		return "";
	}
	//=====================================================================================//
	
	/**
     * 执行从各个报表获取数据的动作
     * @throws YssException
     */
    private void processDealData(HashMap valueMap) throws YssException {
        if (null == valueMap) {
            throw new YssException("未实例化Map！");
        }
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        
        //----------------------------------------------------------------------
        try {
        	conn.setAutoCommit(false);
        	bTrans = true;
        	
        	deleteDaysSettlementData();
        	dealStock(valueMap);
            //dealBond(valueMap);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
	
	
	//=====================================================================================//
    
    private void dealStock(HashMap valueMap)throws YssException{
    	
    	ResultSet rs = null;
    	String query = "";
    	
    	try{
    		//query = " select subTrade.Fsecuritycode,subTrade.Finvmgrcode,subTrade.Fbrokercode,subTrade.Ftradetypecode," +
    		//edit by qiuxufeng 20101117 增加查询所有字段，后面需要一些字段值处理
    		query = " select subTrade.*,subTrade.Fsecuritycode,subTrade.Finvmgrcode,subTrade.Fbrokercode,subTrade.Ftradetypecode," +
    				" sec.FCatCode,cury.ftradecury, subTrade.FTradePrice,subTrade.FTradeAmount,subTrade.FTradeMoney," +
    				//--- 费用 
    				" NVL(subTrade.FTradeFee1, 0) + NVL(subTrade.FTradeFee2, 0) + NVL(subTrade.FTradeFee3, 0) + NVL(subTrade.FTradeFee4, 0) +" +
    				" NVL(subTrade.FTradeFee5, 0) + NVL(subTrade.FTradeFee6, 0) + NVL(subTrade.FTradeFee7, 0) + NVL(subTrade.FTradeFee8, 0) as fee," +
    				" case when subTrade.FTradeTypeCode = '01' or subTrade.FTradeTypeCode = '08' then FAccruedinterest else 0 end as yslx," +
    				" case when subTrade.FTradeTypeCode = '02' then FAccruedinterest else 0 end as yflx," +
    				" case when subTrade.FTradeTypeCode = '01' or subTrade.FTradeTypeCode = '08' then FAccruedinterest * NVL(subTrade.FBaseCuryRate, 0) /" +
    				" NVL(subTrade.FPortCuryRate, 0) else 0 end as byslx," +
    				" case when subTrade.FTradeTypeCode = '02' then FAccruedinterest * NVL(subTrade.FBaseCuryRate, 0) /NVL(subTrade.FPortCuryRate, 0) " +
    				" else  0 end as byflx, " +
    				" case when subTrade.FTradeTypeCode = '01' or subTrade.FTradeTypeCode = '08' then subTrade.FfactSettleMoney else 0 end as mrqsk," +
    				" case when subTrade.FTradeTypeCode = '02' then subTrade.FfactSettleMoney else 0 end as mcqsk," +
    				" case when subTrade.FTradeTypeCode = '01' or subTrade.FTradeTypeCode = '08' then NVL(subTrade.FfactSettleMoney, 0) * " +
    				" NVL(subTrade.FBaseCuryRate, 0) /NVL(subTrade.FPortCuryRate, 0)  else 0 end as mrqskhkd," +
    				" case when subTrade.FTradeTypeCode = '02' then NVL(subTrade.FfactSettleMoney, 0) * NVL(subTrade.FBaseCuryRate, 0) /NVL(subTrade.FPortCuryRate, 0)" +
    				" else 0 end as mcqskhkd,subTrade.FVcost as Fcost,subtrade.fVbasecurycost as fbasecurycost,(subTrade.FfactSettleMoney - (subTrade.FVcost + " +
    				" subTrade.FAccruedinterest)) as gainlose, ((subTrade.FfactSettleMoney -(subTrade.FVcost + subTrade.FAccruedinterest)) * NVL(subTrade.FBaseCuryRate, 0)) as gainloseHKD," +
    				" NVL(subTrade.FBaseCuryRate, 0) / NVL(subTrade.FPortCuryRate, 0) as Rate"+
    		 //=================================
    				" from  " + pub.yssGetTableName("tb_data_subtrade")+" subTrade " +
    				" left join " +
    				" (select secb. * from (select FsecurityCode, Max(FStartDate) as FStartDate   from  " +pub.yssGetTableName("tb_para_security")+
    				" where Fcheckstate = 1  group by FsecurityCode) seca " +
    				" join (select x.FsecurityCode, x.FsecurityName,x.FstartDate,x.FCatCode,y.FCatName from "+pub.yssGetTableName("tb_para_security")+" x " +
    				" left join (select FCatCode, FCatName from Tb_Base_Category where FCheckState = 1) y on x.FCatCode = y.FCatCode) secb " +
    				" on secb.FsecurityCode = seca.FsecurityCode and secb.FStartDate = seca.FStartDate) sec " +
    				" on sec.fsecurityCode =  subTrade.fsecurityCode left join (select curyb. * from (select Fsecuritycode, MAX(FStartDate) as FStartDate" +
    				" from "+pub.yssGetTableName("tb_para_security")+" where FcheckState = 1 group by Fsecuritycode) curya join (select m.Fsecuritycode," +
    				//" m.FTradeCury,m.FStartDate,n.FCuryName,from "+pub.yssGetTableName("tb_para_security")+" m left join (select FCuryCode, FCuryName" +
    				//=======================================此处多一个逗号
    				" m.FTradeCury,m.FStartDate,n.FCuryName from "+pub.yssGetTableName("tb_para_security")+" m left join (select FCuryCode, FCuryName" +
    				" from "+pub.yssGetTableName("Tb_Para_Currency")+"  where FCheckState = 1) n on n.FCuryCode = m.FTradeCury) curyb on curyb.Fsecuritycode =" +
    				" curya.Fsecuritycode and curyb.FStartDate =curya.FStartDate) cury on cury.Fsecuritycode =subTrade.Fsecuritycode  where FBargainDate between " +
    				//============================================================================================================================== tradeType无效标识符
    				//dbl.sqlDate(dBeginDate)+" and "+dbl.sqlDate(dEndDate) +" and FCheckState = 1  and (sec.FCatCode = 'EQ' or sec.FCatCode='FI') and tradeType.Ftradetypecode in " +
    				//tradeType无效标识符 edit by qiuxufeng 20101109
    				dbl.sqlDate(dBeginDate)+" and "+dbl.sqlDate(dEndDate) +" and FCheckState = 1  and (sec.FCatCode = 'EQ' or sec.FCatCode='FI') and Ftradetypecode in " +
    				//" ('01', '02', '08') and FPortCode = "+dbl.sqlString(this.portCode)+" and FInvMgrCode ="+dbl.sqlString(this.invmgrCode);
    				//edit by qiuxufeng 20101118 sql语句未判断参数为空时不为筛选条件
    				" ('01', '02', '08') " + 
    				((null == this.portCode || this.portCode.trim().equalsIgnoreCase("")) ? "" : " and FPortCode = " + dbl.sqlString(this.portCode)) +
    				((null == this.invmgrCode || this.invmgrCode.trim().equalsIgnoreCase("")) ? "" : " and FInvMgrCode = " + dbl.sqlString(this.invmgrCode));
    		rs = dbl.openResultSet(query);
    		setResultValue(valueMap,rs);
    		insertToDaysSettlementData(valueMap);
    		
    	}catch(Exception e){
    		throw new YssException("【當日買賣清算表-处理股票数据出错】"+e.getMessage());
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		valueMap.clear();
    	}
    }
    
//    private void dealBond(HashMap valueMap)throws YssException{
//    	
//    	ResultSet rs = null;
//    	String query = "";
//    	
//    	try{
//    		query = "";
//    		
//    		rs = dbl.openResultSet(query);
//    		setResultValue(valueMap,rs);
//    		insertToDaysSettlementData(valueMap);
//    		
//    	}catch(Exception e){
//    		throw new YssException("【當日買賣清算表-处理股票数据出错】"+e.getMessage());
//    	}finally{
//    		dbl.closeResultSetFinal(rs);
//    		valueMap.clear();
//    	}
//    }
	/**
     * 生成数据表
     * @throws YssException
     */
    private void createTable() throws YssException {
        String strSql = "";
        try {
            if (dbl.yssTableExist(pub.yssGetTableName("Tb_rep_DaysSettlement"))) {
            	return;
            } else {
                strSql = "create table " +
                    pub.yssGetTableName("Tb_rep_DaysSettlement") +
                    " (FSECURITYCODE   VARCHAR2(40)," +
                    " FCatCode varchar2(100) not null," +
                    " FBARGAINDATE   DATE," +
                    " FPORTCODE   VARCHAR2(40)," +
                    " FINVMGRCODE   VARCHAR2(100)," +
                    " FBROKERCODE   VARCHAR2(400)," +
                    " FCURYCODE   VARCHAR2(100)," +
                    " FTRADETYPECODE   VARCHAR2(100)," +
                    " FTRADEPRICE   NUMBER(22,6)," +
                    " FTRADEAMOUNT   NUMBER(22,6)," +
                    " FTRADEMONEY   NUMBER(22,6)," +
                    " FFEE   NUMBER(22,6)," +
                    " FYSLX   NUMBER(22,6)," +
                    " FYFLX   NUMBER(22,6)," +
                    " FBYSLX   NUMBER(22,6)," +
                    " FBYFLX   NUMBER(22,6)," +
                    " FCOST   NUMBER(22,6)," +
                    " FBASECURYCOST   NUMBER(22,6)," +
                    " FMRQSK   NUMBER(22,6)," +
                    " FMCQSK   NUMBER(22,6)," +
                    " FMRQSKHKD   NUMBER(22,6)," +
                    " FMCQSKHKD   NUMBER(22,6)," +
                    " FGAINLOSE   NUMBER(22,6)," +
                    " FGAINLOSEHKD   NUMBER(22,6)," +
                    " FRATE   NUMBER(22,6))" ;
                
                dbl.executeSql(strSql);
            }
            
        } catch (Exception e) {
            throw new YssException("创建當日買賣清算表出错！"+e.getMessage());
        }
    }

    /**
     * 将数据封装放入HashMap中。
     * @param valueMap HashMap
     * @param rs ResultSet
     * @throws YssException
     */
    private void setResultValue(HashMap valueMap, ResultSet rs) throws
        YssException {
    	
    	DaysSettlementBean daysSettlement = null;
    	int count = 1;
        if (null == valueMap) {
            throw new YssException("未实例化Map！");
        }
        if (null == rs) {
            return;
        }
       
        try {
        	while(rs.next()){
        		daysSettlement = new DaysSettlementBean();
        		daysSettlement.sSecurityCode = rs.getString("FSECURITYCODE");
        		daysSettlement.sCatCode = rs.getString("FCatCode");
        		daysSettlement.sportCode= rs.getString("FPORTCODE");
        		daysSettlement.dBargainDate = rs.getDate("FBARGAINDATE");
        		daysSettlement.sInvmgrCode = rs.getString("FINVMGRCODE");
        		daysSettlement.sBrokerCode = rs.getString("FBROKERCODE");
        		//daysSettlement.sCuryCode = rs.getString("FCURYCODE");
        		//edit by qiuxufeng 20101118 取出币种字段名为ftradecury，其它修改字段与取出字段不符
        		daysSettlement.sCuryCode = rs.getString("ftradecury");
        		daysSettlement.sTradeTypeCode = rs.getString("FTRADETYPECODE");
        		daysSettlement.dTradePrice = rs.getDouble("FTRADEPRICE");
        		daysSettlement.dTradeAmount = rs.getDouble("FTRADEAMOUNT");
        		daysSettlement.dTradeMoney = rs.getDouble("FTRADEMONEY");
        		//daysSettlement.dFee = rs.getDouble("FFEE");
        		daysSettlement.dFee = rs.getDouble("FEE");
        		//daysSettlement.dYslx = rs.getDouble("FYSLX");
        		daysSettlement.dYslx = rs.getDouble("YSLX");
        		//daysSettlement.dYflx = rs.getDouble("FYFLX");
        		daysSettlement.dYflx = rs.getDouble("YFLX");
        		//daysSettlement.dByslx = rs.getDouble("FBYSLX");
        		daysSettlement.dByslx = rs.getDouble("BYSLX");
        		//daysSettlement.dByflx = rs.getDouble("FBYFLX");
        		daysSettlement.dByflx = rs.getDouble("BYFLX");
        		//daysSettlement.dMrQsk = rs.getDouble("FMRQSK");
        		daysSettlement.dMrQsk = rs.getDouble("MRQSK");
        		//daysSettlement.dMcQsk = rs.getDouble("FMCQSK");
        		daysSettlement.dMcQsk = rs.getDouble("MCQSK");
        		//daysSettlement.dMrQsk_HKD = rs.getDouble("FMRQSKHKD");
        		daysSettlement.dMrQsk_HKD = rs.getDouble("MRQSKHKD");
        		//daysSettlement.dMcQsk_HKD = rs.getDouble("FMCQSKHKD");
        		daysSettlement.dMcQsk_HKD = rs.getDouble("MCQSKHKD");
        		daysSettlement.dCost = rs.getDouble("FCOST");
        		daysSettlement.dBasecuryCost = rs.getDouble("FBASECURYCOST");
        		//daysSettlement.dGainlose = rs.getDouble("FGAINLOSE");           //盈亏金额
        		daysSettlement.dGainlose = rs.getDouble("GAINLOSE");           //盈亏金额
        		//daysSettlement.dGainLose_HKD = rs.getDouble("FGAINLOSEHKD");    //盈亏金额(港币)
        		daysSettlement.dGainLose_HKD = rs.getDouble("GAINLOSEHKD");    //盈亏金额(港币)
        		//daysSettlement.dRate = rs.getDouble("FRATE");
        		daysSettlement.dRate = rs.getDouble("RATE");
        		//=========end============
        		valueMap.put(count+"", daysSettlement);
        		count++;
        	}
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
    }

    /**
     * 将数据插入数据库
     * @param valueMap HashMap
     * @throws YssException
     */
    private void insertToDaysSettlementData(HashMap valueMap) throws YssException {
        if (null == valueMap || valueMap.isEmpty()) {
            return;
        }
        DaysSettlementBean daysSettlement = null;
        Object object = null;
        PreparedStatement prst = null;
        String sqlStr = "insert into " +
            pub.yssGetTableName("Tb_rep_DaysSettlement") +
            " (FSECURITYCODE,FCatCode,FBARGAINDATE,FPORTCODE,FINVMGRCODE,FBROKERCODE,FCURYCODE,FTRADETYPECODE, "+   
            " FTRADEPRICE,FTRADEAMOUNT,FTRADEMONEY,FFEE,FYSLX,FYFLX,FBYSLX,FBYFLX,FCOST,FBASECURYCOST,FMRQSK,"+    
            " FMCQSK,FMRQSKHKD,FMCQSKHKD,FGAINLOSE,FGAINLOSEHKD,FRATE )"+ 
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            prst = dbl.openPreparedStatement(sqlStr);
            Iterator it = valueMap.keySet().iterator();
            while (it.hasNext()) {
            	daysSettlement = (com.yss.main.operdeal.report.reptab.TabDaysSettlement.DaysSettlementBean) valueMap.get((String) it.next());
				prst.setString(1, daysSettlement.sSecurityCode);
				prst.setString(2, daysSettlement.sCatCode);
				prst.setDate(3, daysSettlement.dBargainDate);
				prst.setString(4, daysSettlement.sportCode);
				prst.setString(5, daysSettlement.sInvmgrCode);
				prst.setString(6, daysSettlement.sBrokerCode);
				prst.setString(7, daysSettlement.sCuryCode);
				prst.setString(8, daysSettlement.sTradeTypeCode);

//				prst.setDouble(9, YssFun.roundIt(daysSettlement.dTradePrice, 2));
//				prst.setDouble(10, YssFun.roundIt(daysSettlement.dTradeAmount, 2));
//				prst.setDouble(11, YssFun.roundIt(daysSettlement.dTradeMoney, 2));
//				prst.setDouble(12, YssFun.roundIt(daysSettlement.dFee, 2));
//				prst.setDouble(13, YssFun.roundIt(daysSettlement.dYslx, 2));
//				prst.setDouble(14, YssFun.roundIt(daysSettlement.dYflx, 2));
//				prst.setDouble(15, YssFun.roundIt(daysSettlement.dByslx, 2));
//				prst.setDouble(16, YssFun.roundIt(daysSettlement.dByflx, 2));
//				prst.setDouble(17, YssFun.roundIt(daysSettlement.dCost, 2));
//				prst.setDouble(18, YssFun.roundIt(daysSettlement.dBasecuryCost, 2));
//				prst.setDouble(19, YssFun.roundIt(daysSettlement.dMrQsk, 2));
//				prst.setDouble(20, YssFun.roundIt(daysSettlement.dMcQsk, 2));
//				prst.setDouble(21, YssFun.roundIt(daysSettlement.dMrQsk_HKD, 2));
//				prst.setDouble(22, YssFun.roundIt(daysSettlement.dMcQsk_HKD, 2));
//				prst.setDouble(23, YssFun.roundIt(daysSettlement.dGainlose, 2));
//				prst.setDouble(24, YssFun.roundIt(daysSettlement.dGainLose_HKD, 2));
//				prst.setDouble(25, YssFun.roundIt(daysSettlement.dRate, 2));
				//edit by qiuxufeng 20101118 值的精度修改
				prst.setDouble(9, YssFun.roundIt(daysSettlement.dTradePrice, 6));
				prst.setDouble(10, YssFun.roundIt(daysSettlement.dTradeAmount, 6));
				prst.setDouble(11, YssFun.roundIt(daysSettlement.dTradeMoney, 6));
				prst.setDouble(12, YssFun.roundIt(daysSettlement.dFee, 6));
				prst.setDouble(13, YssFun.roundIt(daysSettlement.dYslx, 6));
				prst.setDouble(14, YssFun.roundIt(daysSettlement.dYflx, 6));
				prst.setDouble(15, YssFun.roundIt(daysSettlement.dByslx, 6));
				prst.setDouble(16, YssFun.roundIt(daysSettlement.dByflx, 6));
				prst.setDouble(17, YssFun.roundIt(daysSettlement.dCost, 6));
				prst.setDouble(18, YssFun.roundIt(daysSettlement.dBasecuryCost, 6));
				prst.setDouble(19, YssFun.roundIt(daysSettlement.dMrQsk, 6));
				prst.setDouble(20, YssFun.roundIt(daysSettlement.dMcQsk, 6));
				prst.setDouble(21, YssFun.roundIt(daysSettlement.dMrQsk_HKD, 6));
				prst.setDouble(22, YssFun.roundIt(daysSettlement.dMcQsk_HKD, 6));
				prst.setDouble(23, YssFun.roundIt(daysSettlement.dGainlose, 6));
				prst.setDouble(24, YssFun.roundIt(daysSettlement.dGainLose_HKD, 6));
				prst.setDouble(25, YssFun.roundIt(daysSettlement.dRate, 6));
				
                prst.executeUpdate();
            }
        } catch (Exception e) {
        	 throw new YssException("插入當日買賣清算数据出错！"+e.getMessage());
        } finally {
            dbl.closeStatementFinal(prst);
        }
    }

    
    /**
     * 从tb_rep_DaysSettlement表中按要求删除相关数据
     * @throws YssException
     */
    private void deleteDaysSettlementData() throws YssException {
        String sqlStr = "Delete from " +
            pub.yssGetTableName("Tb_rep_DaysSettlement") +
            " where FBARGAINDATE between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + 
//            (this.portCode.equalsIgnoreCase("")?"":" and FPORTCODE ="+dbl.sqlString(this.portCode))+
//            (this.invmgrCode.equalsIgnoreCase("")?"":" and FINVMGRCODE ="+dbl.sqlString(this.invmgrCode));
	        //((null != this.portCode && this.portCode.equalsIgnoreCase(""))?"":" and FPORTCODE ="+dbl.sqlString(this.portCode))+
	        //((null != this.invmgrCode && this.invmgrCode.equalsIgnoreCase(""))?"":" and FINVMGRCODE ="+dbl.sqlString(this.invmgrCode));
            //edit by qiuxufeng 20101118 条件语句逻辑错误
	        ((null == this.portCode || this.portCode.equalsIgnoreCase(""))?"":" and FPORTCODE ="+dbl.sqlString(this.portCode))+
	        ((null == this.invmgrCode || this.invmgrCode.equalsIgnoreCase(""))?"":" and FINVMGRCODE ="+dbl.sqlString(this.invmgrCode));
        try {
            dbl.executeSql(sqlStr);
        } catch (Exception e) {
        	 throw new YssException("删除當日買賣清算数据出错！"+e.getMessage());
        }

    }
	
	
}
