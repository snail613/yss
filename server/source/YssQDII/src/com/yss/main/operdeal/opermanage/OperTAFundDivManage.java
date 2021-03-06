package com.yss.main.operdeal.opermanage;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.cashmanage.*;
import com.yss.main.operdata.*;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.main.taoperation.*;
import com.yss.vsub.YssDbFun;

/**
 * <p>Title: OperTAFundDividend</p>
 *
 * <p>Description: TA基金分红的业务处理</p>
 *
 * add by wangzuochun 2009.07.02
 * MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A
 */
public class OperTAFundDivManage
    extends BaseOperManage {

	
	boolean bstate = true;//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409  改状态值用于判断当天是否有业务
    public OperTAFundDivManage() {
    }

    /**
     * 执行TA基金分红业务处理
     * @throws YssException
     */
    public void doOpertion() throws YssException {
    	//---add by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B start---//
    	CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
    	cashPayRecAdmin.setYssPub(pub);
    	//---add by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B end---//
    	
    	/*added by yeshenghong 2013-5-20 Story 3759 */
//    	//执行TA基金拆分，在TA交易数据表中产生TA基金拆分数据
//        createFundDivident();
        //在TA交易数据表中产生基金分红数据
		/*end by yeshenghong 2013-5-20 Story 3759 */
    	
        //edit by songjie 2013.02.28 STORY #3413 需求上海-[YSS_SH]QDIIV4.0[中]20121214002
        //TA分红权益除权日，做业务处理，不生成TA分红交易数据，TA分红交易数据只通过接口导入。
        //createDividendData();
        
        //产生TA基金分红除权日的现金应收应付   
        //modify by guyichuan 20110325 #3586 增加资金调拨
        //edit by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B 添加参数 cashPayRecAdmin
        createRightDateCash(cashPayRecAdmin);
        
   	 	//产生TA基金转投日的现金应收应付
    	//edit by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B 添加参数 cashPayRecAdmin
        createFundInvestHD(cashPayRecAdmin);
        
        //TA分红结算日生成分红到账TA交易数据 以及 现金应收应付数据
        createTADividendPaid(cashPayRecAdmin);
                
        //产生TA基金分红结算日的现金应收应付      modify by guyichuan 20110325 不须产生资金调拨
        //edit by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B 添加参数 cashPayRecAdmin
        createSettleDateCash(cashPayRecAdmin);
    	
        //add by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B
        insertCashRecPay(cashPayRecAdmin);
    	
      //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
		//当日产生数据，则认为有业务。
        if(bstate){
        	this.sMsg="        当日无业务";
        }
    }

    /**
     * add by songjie 2013.02.28
     * STORY #3413 需求上海-[YSS_SH]QDIIV4.0[中]20121214002
     * TA分红结算日生成分红到账TA交易数据 以及 现金应收应付数据
     * @throws YssException
     */
    private void createTADividendPaid(CashPayRecAdmin cashPayRecAdmin) throws YssException{
    	String strSql = "",strSql2 = "",strSqlInsert = "",strSqlDelete = "",sFNum = "";
    	ResultSet rs = null,rs2 = null;
    	double settleMoney = 0;
        TaTradeBean tradeBean = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        ArrayList list = new ArrayList();
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        boolean bTrans = false;
        CashPecPayBean cashPecPayBean = null;
    	try{
    		conn.setAutoCommit(false);
    		bTrans = true;
    		
            strSqlDelete = "delete from " + pub.yssGetTableName("Tb_TA_Trade") +
            " where FTradeDate = " + dbl.sqlDate(dDate) +
            " and FPortCode = " + dbl.sqlString(sPortCode) +
            " and FSellType = '10' and FDATASOURCE = 1";//添加数据来源标识 add by jsc 20120621 
            dbl.executeSql(strSqlDelete); // 执行删除语句
    		
    		strSql = " select * from " + pub.yssGetTableName("Tb_TA_Trade") + 
    		 " where FSellType = '03' and FSettleDate = " + dbl.sqlDate(this.dDate) + 
    		 " and FPortCode = " + dbl.sqlString(this.sPortCode) + " and FCheckState = 1 ";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			settleMoney = 0;
    			
    			strSql2 = " select * from " + pub.yssGetTableName("Tb_TA_Trade") +
    			" where FSellType = '08' and FConfimDate between " + dbl.sqlDate(rs.getDate("FConfimDate")) + 
    			" and " + dbl.sqlDate(rs.getDate("FSettleDate")) + " and FCheckState = 1 and FPortCode = " + 
    			dbl.sqlString(this.sPortCode) + " and FCashAccCode = " + dbl.sqlString(rs.getString("FCashAccCode")) +
    			" and FPortClsCode = " + dbl.sqlString(rs.getString("FPortClsCode")) +
    			" and FCuryCode = " + dbl.sqlString(rs.getString("FCuryCode"));
    			rs2 = dbl.openResultSet(strSql2);
    			while(rs2.next()){
    				settleMoney += rs2.getDouble("FSettleMoney");//实际结算金额
    			}
    			
    			dbl.closeResultSetFinal(rs2);
                
                BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取当日的基础汇率
                        rs.getString("FCuryCode"), rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

				rateOper.setYssPub(pub);
				rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
				PortCuryRate = rateOper.getDPortRate(); // 获取当日的组合汇率

				tradeBean = new TaTradeBean();

				tradeBean.setDMarkDate(rs.getDate("FSettleDate")); // 设置基准日期
				tradeBean.setDTradeDate(rs.getDate("FSettleDate")); // 设置交易日期
				tradeBean.setDConfimDate(rs.getDate("FSettleDate")); // 设置确认日期
				tradeBean.setDSettleDate(rs.getDate("FSettleDate")); // 设置结算日期
				tradeBean.setStrSellTypeCode("10"); // 设置销售类型:09(分红拆分)
				tradeBean.setStrCuryCode(rs.getString("FCuryCode"));
				tradeBean.setStrPortCode(rs.getString("FPortCode"));
				tradeBean.setSPortClsCode(rs.getString("FPortClsCode"));
				tradeBean.setStrCashAcctCode(rs.getString("FCashAccCode"));
				tradeBean.setDBaseCuryRate(BaseCuryRate);
				tradeBean.setDPortCuryRate(PortCuryRate);
				tradeBean.setDSellAmount(0); // 设置销售份额
				tradeBean.setDSellPrice(0);// 权益价格
				tradeBean.setStrSellNetCode(rs.getString("FSellNetCode"));//销售网点
				tradeBean.setDSellMoney(rs.getDouble("FSellMoney") - settleMoney);// TA分红销售金额 - TA分红转投实际结算金额
                list.add(tradeBean);
                
                cashPecPayBean = setSettleDateCash(rs,rs.getDouble("FSellMoney") - settleMoney);
                cashPecPayBean.setRelaNum(rs.getString("fnum"));              //设置红利分发关联编号
                cashPayRecAdmin.addList(cashPecPayBean);
    		}
    		
            strSqlInsert = "insert into " + pub.yssGetTableName("Tb_TA_Trade") +
            "(FNUM,FMarkDate,FTradeDate,FPORTCODE,FSellNetCode,FSellType,FCuryCode," +
            " FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FSellMoney," +
            " FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FConfimDate,FSettleDate,FBASECURYRATE,FPortCuryRate," +
            " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
            " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8" +
            ", FDesc, FCheckState, FCreator, FCreateTime,FCheckUser,FCheckTime,FSettleState,FPortClsCode,FSettleMoney" +
            ",FBeMarkMoney,FDATASOURCE" + //添加数据来源标识 add by jsc 20120621 
            ")" + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?,?)";
			
			pst = conn.prepareStatement(strSqlInsert);
			// 循环向TA交易数据表中插入数据
			for (int i = 0; i < list.size(); i++) {
				tradeBean = (TaTradeBean) list.get(i);
				sFNum = getNum(tradeBean);

				pst.setString(1, sFNum); // 将多次获取编号的方法改为只取一次,以提高效率
				// 基准日期、交易日期都为除权日
				pst.setDate(2, YssFun.toSqlDate(tradeBean.getDTradeDate()));
				pst.setDate(3, YssFun.toSqlDate(tradeBean.getDTradeDate()));
				pst.setString(4, tradeBean.getStrPortCode());
				pst.setString(5, tradeBean.getStrSellNetCode().trim().length() == 0 ? " " : tradeBean
						.getStrSellNetCode());
				pst.setString(6, "10");
				pst.setString(7, tradeBean.getStrCuryCode());
				pst.setString(8, tradeBean.getStrAnalysisCode1().trim().length() == 0 ? " " : tradeBean
						.getStrAnalysisCode1());
				pst.setString(9, tradeBean.getStrAnalysisCode2().trim().length() == 0 ? " " : tradeBean
						.getStrAnalysisCode2());
				pst.setString(10, tradeBean.getStrAnalysisCode3().trim().length() == 0 ? " " : tradeBean
						.getStrAnalysisCode3());
				pst.setString(11, tradeBean.getStrCashAcctCode());
				pst.setDouble(12, tradeBean.getDSellMoney());
				pst.setDouble(13, 0);
				pst.setDouble(14, tradeBean.getDSellPrice());
				pst.setDouble(15, tradeBean.getDIncomeNotBal());
				pst.setDouble(16, tradeBean.getDIncomeBal());
				pst.setDate(17, YssFun.toSqlDate(tradeBean.getDTradeDate()));
				pst.setDate(18, YssFun.toSqlDate(tradeBean.getDSettleDate()));
				pst.setDouble(19, tradeBean.getDBaseCuryRate());
				pst.setDouble(20, tradeBean.getDPortCuryRate());
				pst.setString(21, " ");
				pst.setDouble(22, 0);
				pst.setString(23, " ");
				pst.setDouble(24, 0);
				pst.setString(25, " ");
				pst.setDouble(26, 0);
				pst.setString(27, " ");
				pst.setDouble(28, 0);
				pst.setString(29, " ");
				pst.setDouble(30, 0);
				pst.setString(31, " ");
				pst.setDouble(32, 0);
				pst.setString(33, " ");
				pst.setDouble(34, 0);
				pst.setString(35, " ");
				pst.setDouble(36, 0);
				pst.setString(37, " "); // 描述
				pst.setInt(38, 1); // 设置审核状态为已审核
				pst.setString(39, pub.getUserCode());
				pst.setString(40, YssFun.formatDatetime(new java.util.Date()));
				pst.setString(41, pub.getUserCode());
				pst.setString(42, YssFun.formatDatetime(new java.util.Date()));
				pst.setInt(43, tradeBean.getSettleState()); // 结算方式
				pst.setString(44, tradeBean.getSPortClsCode()); // 分级组合代码
				pst.setDouble(45, tradeBean.getDSettleMoney() == 0 ? tradeBean.getDSellMoney() : tradeBean
						.getDSettleMoney());
				pst.setDouble(46, tradeBean.getBeMarkMoney() == 0 ? tradeBean.getDSellMoney() : tradeBean
						.getBeMarkMoney());
				pst.setInt(47, 1);
				
				pst.executeUpdate();
			}
			
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			
			// 当日产生数据，则认为有业务。
			if (list != null && list.size() > 0) {
				this.bstate = false;
			}
    	}catch(Exception e){
    		throw new YssException("生成TA分红到账交易数据出错！");
    	}finally{
    		dbl.closeResultSetFinal(rs,rs2);
    		dbl.closeStatementFinal(pst);
    		dbl.endTransFinal(conn, bTrans);
    	}
    }
    
    /**
     * add by songjie 2012.12.26 
     * BUG 6701 QDV4南方2012年12月19日01_B
     * @param cashPayRecAdmin
     */
    private void insertCashRecPay(CashPayRecAdmin cashPayRecAdmin) throws YssException{
    	String strSql = "";
    	Connection conn = dbl.loadConnection();
    	boolean trans = false;
    	try{
    		conn.setAutoCommit(false);
    		trans = true;
    		
    		strSql = " delete from " + pub.yssGetTableName("Tb_Data_Cashpayrec") + 
    		" where FDataOrigin = 0 and FTransDate between " + dbl.sqlDate(dDate) + 
    		" and " + dbl.sqlDate(dDate) + " and FTsfTypeCode = '03' and FSubTsfTypeCode = '03FD' " +
    		" and FDataSource = 0 and FPortCode in (" + operSql.sqlCodes(sPortCode) + ")";
    		
    		dbl.executeSql(strSql);
    		
            if (cashPayRecAdmin.getList().size() > 0) {
            	cashPayRecAdmin.delete("", dDate, dDate, "07", "07FD", "", "", sPortCode, "", "", "", 0, 0, "", "FundDividend", " ");
            	cashPayRecAdmin.insert("",dDate,dDate,"03","03FD","","",sPortCode,"","","",0,true,false,false,0," ","");
            }
            
            conn.commit();
            conn.setAutoCommit(true);
            trans = false;
    	}catch(Exception e){
    		throw new YssException("插入现金应收应付数据出错！", e);
    	} finally{
    		dbl.endTransFinal(conn, trans);
    	}
    }

	/**********************************************************
     * MS01170 QDV4国内（测试）2010年05月14日01_A  
     *  add by jiangshichao 2010.06.24
     * 处理Ta分红业务时，产生分红数据
     * @throws YssException
     */
    private void createDividendData() throws YssException {
        String sFNum = "";
        String strSql = "";
        String strSqlInsert = "";
        String strSqlDelete = "";

        ResultSet rs = null;
        TaTradeBean tradeBean = null;
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
        PreparedStatement pst = null;
        //YssPreparedStatement pst = null;
        //=============end====================
        ArrayList list = new ArrayList();
//        HashMap htDiffDate = new HashMap(); // 存放不同日期的 Max FNum 值
        Connection conn = dbl.loadConnection();

        try {
            // 查询语句：在估值日当天：查询TA基金权益信息表中权益类型为基金分红的记录  
            strSql = "SELECT FR.*,tpca.FCuryCode FROM " +
                "(SELECT * FROM " + pub.yssGetTableName("Tb_TA_FundRight") +
                " WHERE FCHECKSTATE = 1 AND FRightType= 'Dividend' AND FRightDate = " +
                dbl.sqlDate(dDate) + " AND FPortCode = " + dbl.sqlString(sPortCode) + ") FR " +
                " left join (select tpca2.* " +
                " from (select FCASHACCCODE, max(FSTARTDATE) as FSTARTDATE "+
			  	" from "+pub.yssGetTableName("Tb_Para_CashAccount")+" where Fcheckstate = 1 "+
			  	" group by FCASHACCCODE) tpca1 "+
			  	" left join "+pub.yssGetTableName("Tb_Para_CashAccount")+" tpca2 on tpca1.FCASHACCCODE = " +
			  	" tpca2.fcashacccode  and tpca1.FSTARTDATE = tpca2.FSTARTDATE) tpca "+
			  	" on FR.FCASHACCOUNT = tpca.FCASHACCCODE " + 
			  	" ";
            
            // 插入语句：向TA交易数据表中插入数据
            strSqlInsert = "insert into " + pub.yssGetTableName("Tb_TA_Trade") +
                "(FNUM,FMarkDate,FTradeDate,FPORTCODE,FSellNetCode,FSellType,FCuryCode," +
                " FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FSellMoney," +
                " FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FConfimDate,FSettleDate,FBASECURYRATE,FPortCuryRate," +
                " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8" +
                ", FDesc, FCheckState, FCreator, FCreateTime,FCheckUser,FCheckTime,FSettleState,FPortClsCode,FSettleMoney" +
                ",FBeMarkMoney,FDATASOURCE" + //添加数据来源标识 add by jsc 20120621 
                ")" + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?)";
            rs = dbl.queryByPreparedStatement(strSql);
            // 循环记录集，将tradeBean对象添加到list中
            while (rs.next()) {
                tradeBean = setTaTradeBean(rs);
                list.add(tradeBean);
            }
            // 在向TA交易数据表中插入数据之前先删除相应的记录
            /**shashijie 2012-5-28 BUG 4652 不需要判断,无论是否有数据业务处理始终先删除旧数据 */
            //if (list.size() > 0) {
                strSqlDelete = "delete from " + pub.yssGetTableName("Tb_TA_Trade") +
                    " where FTradeDate = " + dbl.sqlDate(dDate) +
                    " and fportcode = " + dbl.sqlString(sPortCode) +
                    " and FSellType = '03' and FDATASOURCE=1";//添加数据来源标识 add by jsc 20120621 
                dbl.executeSql(strSqlDelete); // 执行删除语句
            //}
			/**end*/
			//modified by liubo.Story #2145
			//==============================
            pst = conn.prepareStatement(strSqlInsert);
            //pst = dbl.getYssPreparedStatement(strSqlInsert);
			//==============end================

            // 循环向TA交易数据表中插入数据
            for (int i = 0; i < list.size(); i++) {
                tradeBean = (TaTradeBean) list.get(i);
                sFNum = getNum(tradeBean);
                /*if (i == 0) {
                    sFNum = getNum(tradeBean); //只取一次
                    htDiffDate.put(tradeBean.getDSettleDate(), sFNum);
                }
                if (htDiffDate.get(tradeBean.getDSettleDate()) == null) {
                    //如果本次的日期与上一次的日期不同的话就得再取一次编号
                    sFNum = getNum(tradeBean);
                    htDiffDate.put(tradeBean.getDSettleDate(), sFNum);
                }*/
                pst.setString(1, sFNum); //将多次获取编号的方法改为只取一次,以提高效率
                //基准日期、交易日期都为除权日
                pst.setDate(2, YssFun.toSqlDate(tradeBean.getDTradeDate()));
                pst.setDate(3, YssFun.toSqlDate(tradeBean.getDTradeDate()));
                pst.setString(4, tradeBean.getStrPortCode());
                pst.setString(5, tradeBean.getStrSellNetCode().trim().length() == 0 ? " " : tradeBean.getStrSellNetCode());
                pst.setString(6, "03");
                pst.setString(7, tradeBean.getStrCuryCode());
                pst.setString(8, tradeBean.getStrAnalysisCode1().trim().length() == 0 ? " " : tradeBean.getStrAnalysisCode1());
                pst.setString(9, tradeBean.getStrAnalysisCode2().trim().length() == 0 ? " " : tradeBean.getStrAnalysisCode2());
                pst.setString(10, tradeBean.getStrAnalysisCode3().trim().length() == 0 ? " " : tradeBean.getStrAnalysisCode3());
                pst.setString(11, tradeBean.getStrCashAcctCode());
                pst.setDouble(12, tradeBean.getDSellMoney());
                pst.setDouble(13, 0);
                pst.setDouble(14, tradeBean.getDSellPrice());
                pst.setDouble(15, tradeBean.getDIncomeNotBal());
                pst.setDouble(16, tradeBean.getDIncomeBal());
                pst.setDate(17, YssFun.toSqlDate(tradeBean.getDTradeDate()));
                pst.setDate(18, YssFun.toSqlDate(tradeBean.getDSettleDate()));
                pst.setDouble(19, tradeBean.getDBaseCuryRate());
                pst.setDouble(20, tradeBean.getDPortCuryRate());
                pst.setString(21, " ");
                pst.setDouble(22, 0);
                pst.setString(23, " ");
                pst.setDouble(24, 0);
                pst.setString(25, " ");
                pst.setDouble(26, 0);
                pst.setString(27, " ");
                pst.setDouble(28, 0);
                pst.setString(29, " ");
                pst.setDouble(30, 0);
                pst.setString(31, " ");
                pst.setDouble(32, 0);
                pst.setString(33, " ");
                pst.setDouble(34, 0);
                pst.setString(35, " ");
                pst.setDouble(36, 0);
                pst.setString(37, " "); // 描述
                pst.setInt(38, 1); // 设置审核状态为已审核
                pst.setString(39, pub.getUserCode());
                pst.setString(40, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(41, pub.getUserCode());
                pst.setString(42, YssFun.formatDatetime(new java.util.Date()));
                pst.setInt(43, tradeBean.getSettleState()); // 结算方式
                pst.setString(44, tradeBean.getSPortClsCode()); // 分级组合代码
                pst.setDouble(45, tradeBean.getDSettleMoney()==0?tradeBean.getDSellMoney():tradeBean.getDSettleMoney());
                pst.setDouble(46, tradeBean.getBeMarkMoney()==0?tradeBean.getDSellMoney():tradeBean.getBeMarkMoney());
                pst.setInt(47, 1);
                pst.executeUpdate();
                //pst.addBatch();
            }
                  
          //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(list!=null && list.size()>0){
    			this.bstate = false;
    		}
    		
        } catch (Exception ex) {
            throw new YssException("在TA交易数据表中产生TA分红数据出现异常！", ex);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.closeResultSetFinal(rs);
        }
    }
    
    
    /**
     * 根据TA基金权益信息表中的信息，执行TA基金拆分，在TA交易数据表中产生TA基金拆分数据
     * @throws YssException
     */
    private void createFundDivident() throws YssException {
        String sFNum = "";
        String strSql = "";
        String strSqlInsert = "";
        String strSqlDelete = "";

        ResultSet rs = null;
        TaTradeBean tradeBean = null;
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement pst = null;
        //=============end====================
        ArrayList list = new ArrayList();
        HashMap htDiffDate = new HashMap(); // 存放不同日期的 Max FNum 值
//        Connection conn = dbl.loadConnection();

        try {
            // 查询语句：在估值日当天：查询TA基金权益信息表中权益类型为基金拆分的记录
        	
        	//20120605 modified by liubo.Story #2683
        	//在获取TA权益信息的同时，查询当天的TA库存，以TA库存当天库存数量加上权益份额，得到拆分后基金份额
        	//用得到的拆分后基金份额，除以拆分前基金份额（即当天的TA库存数量），得到拆分比例
        	//=========================================
//            strSql = "SELECT FR.*,CA.FCuryCode,round((NVL(ta.fstorageamount,0) + FR.FRIGHTAMOUNT)/(NVL(ta.fstorageamount,FR.FRIGHTAMOUNT)),9) as sss FROM " +
//                "(SELECT * FROM " + pub.yssGetTableName("Tb_TA_FundRight") +
//                " WHERE FCHECKSTATE = 1 AND FRightType= 'Reinvest' AND FRightDate = " +
//                dbl.sqlDate(dDate) + " AND FPortCode = " + dbl.sqlString(sPortCode) + ") FR " +
//                " left join (select FPortCode,FPortCury As FCuryCode from " +
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FCheckState = 1) CA on CA.FPortCode = FR.FPortCode " +
//                " left join " + pub.yssGetTableName("tb_stock_ta") + " ta on FR.FRIGHTDATE = ta.fstoragedate and FR.FPORTCODE = ta.fportcode" ;
        	
            strSql = "SELECT FR.*,CA.FCuryCode,round(NVl(ta.FPrice,1)/FR.FRightUnitMoney,9) as sss FROM " +
            "(SELECT * FROM " + pub.yssGetTableName("Tb_TA_FundRight") +
            " WHERE FCHECKSTATE = 1 AND FRightType= 'Reinvest' AND FRightDate = " +
            dbl.sqlDate(dDate) + " AND FPortCode = " + dbl.sqlString(sPortCode) + ") FR " +
            " left join (select FPortCode,FPortCury As FCuryCode from " +
            pub.yssGetTableName("Tb_Para_Portfolio") +
            " where FCheckState = 1) CA on CA.FPortCode = FR.FPortCode " +
            " left join (select * from " + pub.yssGetTableName("tb_data_navdata") + " where FReTypeCode = 'Total' and FKeyCode = 'Unit') ta on FR.FRIGHTDATE = ta.FNavDate and FR.FPORTCODE = ta.fportcode" ;
        	//====================end=====================
            // 插入语句：向TA交易数据表中插入数据
            strSqlInsert = "insert into " + pub.yssGetTableName("Tb_TA_Trade") +
                "(FNUM,FMarkDate,FTradeDate,FPORTCODE,FSellNetCode,FSellType,FCuryCode," +
                " FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FSellMoney," +
                " FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FConfimDate,FSettleDate,FBASECURYRATE,FPortCuryRate," +
                " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8" +
                ", FDesc, FCheckState, FCreator, FCreateTime,FCheckUser,FCheckTime,FSettleState,FPortClsCode,FSettleMoney" +
                ",FBeMarkMoney" +
                ", FSPLITRATIO" +	//20120607 added by liubo.Story #2683.拆分比例
                ")" + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?)";
            rs = dbl.queryByPreparedStatement(strSql);
            // 循环记录集，将tradeBean对象添加到list中
            while (rs.next()) {
            	
            	if (rs.getDouble("sss") == 1)
            	{
            		continue;
            	}
            	
                tradeBean = setTaTradeBean(rs);

            	//20120605 added by liubo.Story #2683
                //===========================
                tradeBean.setdSplitRatio(rs.getDouble("sss"));
                //============end===============
                
                list.add(tradeBean);
            }
            // 在向TA交易数据表中插入数据之前先删除相应的记录
            //if (list.size() > 0) {
                strSqlDelete = "delete from " + pub.yssGetTableName("Tb_TA_Trade") +
                    " where FTradeDate = " + dbl.sqlDate(dDate) +
                    " and FConfimDate = " + dbl.sqlDate(dDate) +
                    " and FSettleDate = " + dbl.sqlDate(dDate) +
                    " and FMarkDate = " + dbl.sqlDate(dDate) +
                    " and FSellType = '09' and FSPLITRATIO <> 1"+
                    " and FDATASOURCE=1";
                dbl.executeSql(strSqlDelete); // 执行删除语句
           // }
			//modified by liubo.Story #2145
			//==============================
//            pst = conn.prepareStatement(strSqlInsert);
            pst = dbl.getYssPreparedStatement(strSqlInsert);
			//==============end================

            // 循环向TA交易数据表中插入数据
            for (int i = 0; i < list.size(); i++) {
                tradeBean = (TaTradeBean) list.get(i);
                if (i == 0) {
                    sFNum = getNum(tradeBean); //只取一次
                    htDiffDate.put(tradeBean.getDSettleDate(), sFNum);
                }
                if (htDiffDate.get(tradeBean.getDSettleDate()) == null) {
                    //如果本次的日期与上一次的日期不同的话就得再取一次编号
                    sFNum = getNum(tradeBean);
                    htDiffDate.put(tradeBean.getDSettleDate(), sFNum);
                }
                pst.setString(1, sFNum); //将多次获取编号的方法改为只取一次,以提高效率
                pst.setDate(2, YssFun.toSqlDate(tradeBean.getDMarkDate()));
                pst.setDate(3, YssFun.toSqlDate(tradeBean.getDTradeDate()));
                pst.setString(4, tradeBean.getStrPortCode());
                pst.setString(5, tradeBean.getStrSellNetCode().trim().length() == 0 ? " " : tradeBean.getStrSellNetCode());
                pst.setString(6, tradeBean.getStrSellTypeCode());
                pst.setString(7, tradeBean.getStrCuryCode());
                pst.setString(8, tradeBean.getStrAnalysisCode1().trim().length() == 0 ? " " : tradeBean.getStrAnalysisCode1());
                pst.setString(9, tradeBean.getStrAnalysisCode2().trim().length() == 0 ? " " : tradeBean.getStrAnalysisCode2());
                pst.setString(10, tradeBean.getStrAnalysisCode3().trim().length() == 0 ? " " : tradeBean.getStrAnalysisCode3());
                pst.setString(11, tradeBean.getStrCashAcctCode());
                pst.setDouble(12, tradeBean.getDSellMoney());
                pst.setDouble(13, tradeBean.getDSellAmount());
                pst.setDouble(14, tradeBean.getDSellPrice());
                pst.setDouble(15, tradeBean.getDIncomeNotBal());
                pst.setDouble(16, tradeBean.getDIncomeBal());
                pst.setDate(17, YssFun.toSqlDate(tradeBean.getDConfimDate()));
                pst.setDate(18, YssFun.toSqlDate(tradeBean.getDSettleDate()));
                pst.setDouble(19, tradeBean.getDBaseCuryRate());
                pst.setDouble(20, tradeBean.getDPortCuryRate());
                pst.setString(21, " ");
                pst.setDouble(22, 0);
                pst.setString(23, " ");
                pst.setDouble(24, 0);
                pst.setString(25, " ");
                pst.setDouble(26, 0);
                pst.setString(27, " ");
                pst.setDouble(28, 0);
                pst.setString(29, " ");
                pst.setDouble(30, 0);
                pst.setString(31, " ");
                pst.setDouble(32, 0);
                pst.setString(33, " ");
                pst.setDouble(34, 0);
                pst.setString(35, " ");
                pst.setDouble(36, 0);
                pst.setString(37, " "); // 描述
                pst.setInt(38, 1); // 设置审核状态为已审核
                pst.setString(39, pub.getUserCode());
                pst.setString(40, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(41, pub.getUserCode());
                pst.setString(42, YssFun.formatDatetime(new java.util.Date()));
                pst.setInt(43, tradeBean.getSettleState()); // 结算方式
                pst.setString(44, tradeBean.getSPortClsCode()); // 分级组合代码
                pst.setDouble(45, tradeBean.getDSettleMoney()==0?tradeBean.getDSellMoney():tradeBean.getDSettleMoney());
                pst.setDouble(46, tradeBean.getBeMarkMoney()==0?tradeBean.getDSellMoney():tradeBean.getBeMarkMoney());
                pst.setDouble(47, tradeBean.getdSplitRatio());
                pst.addBatch();
            }
            pst.executeBatch();
            
          //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(list!=null && list.size()>0){
    			this.bstate = false;
    		}
            
        } catch (Exception ex) {
            throw new YssException("在TA交易数据表中产生TA基金拆分数据出现异常！", ex);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 设置TA交易数据对象，并返回此对象
     * @param rs ResultSet
     * @return TaTradeBean
     * @throws YssException
     */
    private TaTradeBean setTaTradeBean(ResultSet rs) throws YssException {
        TaTradeBean taTradeBean = new TaTradeBean();
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        try {

            BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取当日的基础汇率
                rs.getString("FCuryCode"), rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
            PortCuryRate = rateOper.getDPortRate();                 //获取当日的组合汇率

            taTradeBean.setDMarkDate(rs.getDate("FRightDate"));     // 设置基准日期
            taTradeBean.setDTradeDate(rs.getDate("FRightDate"));    // 设置交易日期
            taTradeBean.setDConfimDate(rs.getDate("FRightDate"));   // 设置确认日期
            taTradeBean.setDSettleDate(rs.getDate("FSettledate"));   // 设置结算日期
            taTradeBean.setStrSellTypeCode("09");                   // 设置销售类型:09(分红拆分)
            taTradeBean.setStrCuryCode(rs.getString("FCuryCode"));
            taTradeBean.setStrPortCode(rs.getString("FPortCode"));
            taTradeBean.setSPortClsCode(rs.getString("FPortClsCode"));
            taTradeBean.setStrCashAcctCode(rs.getString("FCashAccount") == null ? " " : rs.getString("FCashAccount"));
            taTradeBean.setDBaseCuryRate(BaseCuryRate);
            taTradeBean.setDPortCuryRate(PortCuryRate);
            taTradeBean.setDSellAmount(rs.getDouble("FRightAmount")); // 设置销售份额
            taTradeBean.setDSellPrice(rs.getDouble("FRightUnitMoney"));//权益价格
            taTradeBean.setDSellMoney(rs.getDouble("FRightMoney"));//权益金额
            
        } catch (Exception e) {
            throw new YssException("设置除TA交易数据时出现异常！", e);
        }
        return taTradeBean;
    }

    /**
     * 若为除权日，则在现金应收应付产生一笔业务类型为07，业务子类型为07FD，方向为流入
     * modify by guyichuan 20110328 TASK #3586::TA分红业务处理模式需修改       出权日须产生资金调拨
     * @param rs ResultSet
     * @throws YssException
     */
    //edit by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B 添加参数 cashPayRecAdmin
    private void createRightDateCash(CashPayRecAdmin cashPayRecAdmin) throws YssException {
        CashPecPayBean cashPecPayBean = null;
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
        CashTransAdmin cashtransAdmin = null;

        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer strBuf = new StringBuffer();

        try {
            if (cashtransAdmin == null) {
                cashtransAdmin = new CashTransAdmin(); //新建资金调拨控制对象
                cashtransAdmin.setYssPub(pub);
            }
            // 查询语句：在除权日当天：查询TA基金权益信息表中权益类型为基金分红的记录
            strBuf.append(" SELECT ta.*,CA.FCuryCode FROM"); 
            //edit by songjie 2013.03.01 STORY #3413 需求上海-[YSS_SH]QDIIV4.0[中]20121214002 添加 FConfimDate
            strBuf.append(" (select fnum,ftradedate,FCASHACCCODE as FCashAccount  ,fsellmoney,FPortCode,FConfimDate from ").append(pub.yssGetTableName("tb_ta_trade")).append(" where fcheckstate=1 and fportcode= ");       
            //edit by songjie 2013.02.28 STORY #3413 需求上海-[YSS_SH]QDIIV4.0[中]20121214002 FTradeDate 改为 FConfimDate
            strBuf.append(dbl.sqlString(this.sPortCode)).append(" and fselltype='03'and FConfimDate=").append(dbl.sqlDate(this.dDate)).append(")ta");
            strBuf.append(" left join ");
            strBuf.append("(select FCashAccCode,FCuryCode from ").append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" where FCheckState = 1) CA ");
            strBuf.append("on CA.FCashAccCode = ta.FCashAccount");
            rs = dbl.queryByPreparedStatement(strBuf.toString());
            // 循环记录集，将cashPecPayBean对象添加到cashPayRecAdmin中的addList
            while (rs.next()) {
                cashPecPayBean = setRightDateCash(rs);
                cashPayRecAdmin.addList(cashPecPayBean);
            
                //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
                //当日产生数据，则认为有业务。
                if(cashPayRecAdmin.getList()!=null && cashPayRecAdmin.getList().size()>0){//modify by zhouwei 20120618 判断现金应收应付数据
                	this.bstate = false;
                }
    		}
            
        } catch (Exception ex) {
            throw new YssException("生成除权日现金应收应付款出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 若为基金转投日，则在现金应收应付产生一笔业务类型为03，业务子类型为03FD，方向为流入
     * @param rs ResultSet
     * @throws YssException
     */
    //edit by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B
    private void createFundInvest(CashPayRecAdmin cashPayRecAdmin) throws YssException {
        CashPecPayBean cashPecPayBean = null;
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
        CashTransAdmin cashtransAdmin = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String strSql = "";
        try {
            if (cashtransAdmin == null) {
                cashtransAdmin = new CashTransAdmin(); //新建资金调拨控制对象
                cashtransAdmin.setYssPub(pub);
            }
            // 查询语句：在确认日当天：查询TA交易数据表中权益类型为分红转投的记录
            strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Ta_Trade") +
                " WHERE FCHECKSTATE = 1 AND FSellType= '08' AND FConfimDate = " +
                dbl.sqlDate(dDate) + " AND FPortCode = " + dbl.sqlString(sPortCode);

            rs = dbl.queryByPreparedStatement(strSql);
            while (rs.next()) {
                cashPecPayBean = setFundInvest(rs);
                cashPayRecAdmin.addList(cashPecPayBean);
                
                //add by guyichuan 20110328 TASK #3586::分红转投确认日须产生资金调拨
                strSql = " select TD.FPortCode,TD.FCashAccCode,TD.FCuryCode,TD.FSettleMoney  As SumMoney,TD.fnum,FR.FSettleDate "
                    +" from" 
                    +" (select * from "
                    +pub.yssGetTableName("tb_ta_trade") 
                    +" where FSellType = '08'"
                    +" and FPortCode = " 
                    +dbl.sqlString(sPortCode)
                    +" and FConfimDate = "
                    +dbl.sqlDate(dDate)
                    +" and fportclscode ="+dbl.sqlString(rs.getString("fportclscode"))
                    +" and FCHECKSTATE = 1"
                    +"  ) TD"
            
             +" join" //modify by jsc 20120618 【STORY #2686 要求系统能做针对RQFII的分红，以及红利转投 】
                    +" (select FSettleDate,FCASHACCOUNT,fportclscode from "//modify by jsc 20120618 【STORY #2686 要求系统能做针对RQFII的分红，以及红利转投 】
                    +pub.yssGetTableName("Tb_TA_FundRight")
                    + " where Fportcode= " 
                    +dbl.sqlString(sPortCode)
                    +" and FCHECKSTATE = 1"
                    +" ) FR" 
                    +" on FR.FCASHACCOUNT=TD.FCashAccCode  and td.fportclscode = fr.fportclscode "//modify by jsc 20120618 【STORY #2686 要求系统能做针对RQFII的分红，以及红利转投 】
                    +" where TD.FSettleDate =FR.FSettleDate"; 	
                
                rs2 = dbl.queryByPreparedStatement(strSql); // 执行后得到一条记录集
                // 如果存在记录集，则生成现金应收应付对象和资金调拨对象
                if (rs2.next()) {        	
                    transfer = setTransfer(rs2,rs2.getDate("FSettleDate")); //获取资金调拨数据,调拨日期为结算日期
                    transfer.setFRelaNum(rs2.getString("fnum"));
                    transferSet = setTransferSet(rs2,1); //获取资金调拨子数据
                    subTransfer = new ArrayList(); //实例化放置资金调拨子数据的容器
                    subTransfer.add(transferSet); //将资金调拨子数据放入容器
                    transfer.setSubTrans(subTransfer); //将子数据放入资金调拨中
                    cashtransAdmin.addList(transfer);
                    
                    cashtransAdmin.insert(rs2.getDate("FSettleDate"), "FundDividend", 1, rs2.getString("fnum")); //插入资金调拨,以调拨日期和关联编号类型,自动生成的来删除已有资金调拨
                }
              //rs是在循环里打开的，应用完就关闭，否则会占用资源
                dbl.closeResultSetFinal(rs2);
                
              //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
        		//当日产生数据，则认为有业务。
        		if(subTransfer!=null && subTransfer.size()>0){
        			this.bstate = false;
        		}
            } 
        } catch (Exception ex) {
            throw new YssException("生成基金转投日现金应收应付款出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    
    /**
     * 若为基金转投日，则在现金应收应付产生一笔业务类型为03，业务子类型为03FD，方向为流入
     * @param rs ResultSet
     * @throws YssException
     */
    //edit by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B 添加参数 cashPayRecAdmin
    private void createFundInvestHD(CashPayRecAdmin cashPayRecAdmin) throws YssException {
        CashPecPayBean cashPecPayBean = null;
        ResultSet rs = null;
        String strSql = "";

        try {            
            // 查询语句：在确认日当天：查询TA交易数据表中权益类型为基金拆分的记录
            strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Ta_Trade") +
                " WHERE FCHECKSTATE = 1 AND FSellType= '08' AND FConfimDate = " +
                dbl.sqlDate(dDate) + " AND FPortCode = " + dbl.sqlString(sPortCode);

            rs = dbl.queryByPreparedStatement(strSql);
            while (rs.next()) {
                cashPecPayBean = setFundInvest(rs);
                cashPayRecAdmin.addList(cashPecPayBean);
            } 
            
          //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(cashPayRecAdmin.getAddList()!=null && cashPayRecAdmin.getAddList().size()>0){
    			this.bstate = false;
    		}
        } catch (Exception ex) {
            throw new YssException("生成基金转投日现金应收应付款出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    
    
    /**
     * 若为结算日，则在现金应收应付产生一笔业务类型为03，业务子类型为03FD，方向为流出;
     * 另外还产生一笔流出的资金调拨，调拨类型为04，调拨子类型为0403，调拨金额为：现金帐户下挂的应付TA基金分红支出款余额
     * modify by guyichuan 20110328 TASK #3586::TA分红业务处理模式需修改            不产生资金调拨
     * @param rs ResultSet
     * @throws YssException
     */
    //edit by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B
    private void createSettleDateCash(CashPayRecAdmin cashPayRecAdmin) throws YssException {
        CashPecPayBean cashPecPayBean = null;
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
        CashTransAdmin cashtransAdmin = null;

        ResultSet rs = null;
        ResultSet rs2 = null;
        String strSql = "";
        String strSqlSettle = "";
        
        TaTradeBean ta = new TaTradeBean();
        ta.setYssPub(this.pub);
        
        try {
        	//modify by fangjiang 2012.12.04 story 3264
        	if(2 == ta.getAccWayState(this.sPortCode) ||
        	   3 == ta.getAccWayState(this.sPortCode) ||
        	   4 == ta.getAccWayState(this.sPortCode)	){
        		this.getCJCashPayRec();
        		return;
        	}
            if (cashtransAdmin == null) {
                cashtransAdmin = new CashTransAdmin(); //新建资金调拨控制对象
                cashtransAdmin.setYssPub(pub);
            }
            // 查询语句：在结算日当天，查询TA基金权益信息表中权益类型为基金分红的记录
			strSqlSettle = "select a.*,b.fnum from "
					+ pub.yssGetTableName("Tb_TA_FundRight")
					// begin by zhouxiang MS01744 2010.10.18
					// 为TA分红结算日剩余的金额资金调拨和应收应付增加关联编号
					+" a left join (select * from "
					+ pub.yssGetTableName("tb_ta_trade")
					+ " where fcheckstate = 1 and Fsettledate = "
					+ dbl.sqlDate(dDate)
					+ " and fselltype = '03') b on a.Fcashaccount = b.Fcashacccode and a.fportclscode= b.fportclscode "
					// end-- by zhouxiang MS01744 2010.10.18
					// 为TA分红结算日剩余的金额资金调拨和应收应付增加关联编号
					+ " WHERE a.FCHECKSTATE = 1 AND a.FRightType= 'Dividend' AND a.FSettleDate = "
					+ dbl.sqlDate(dDate) + " AND a.FPortCode = "
					+ dbl.sqlString(sPortCode);
            rs2 = dbl.queryByPreparedStatement(strSqlSettle);
            // 循环记录集，产生现金应收应付和资金调拨
            while (rs2.next()) {
                // 查询语句： 从现金应收应付表和现金应收应付库存表中算出结算日的现金应收应付金额
                strSql = 
                	/*begin by zhouxiang MS01744 结算日的时候保留以前‘分红转投’结算日对剩余金额的处理，同时在原来的
                	基础之上增加新的功能， 如果权益结算日当天查询出有‘红利方法’的数据， 则将应收应付和资金调拨的结果清零，即不产生资金调拨和应收应付
                	以便在之后再做‘分红转投’的业务 2010.10.21
                	*/
                	"select a.Fportcode,a.FCashAccCode, a.FCuryCode, (case"
                     +" when (a.SumMoney * b.FSettleMoney) is null then "+
                     "  a.SumMoney  else (a.SumMoney * b.FSettleMoney) end) as SumMoney from ( "+
                     //end by zhouxiang  MS01744    系统需支持基金分红时，现金分红在分红转投日之前的情况           
                	"select y.Fportcode, y.FCashAccCode, y.FCuryCode,Sum(FSettleMoney) As SumMoney from " +
                    "((select FPortCode,FCashAccCode,FCuryCode,FMoney * FInOut As FSettleMoney from " +
                    pub.yssGetTableName("tb_data_cashpayrec") +
                    " where FTsfTypeCode = '07' and FSubTsfTypeCode = '07FD' and FPortCode = " +
                    dbl.sqlString(sPortCode) + " and FTransDate = " + dbl.sqlDate(dDate) + ")" +

                    //begin---zhouxiang MS01744   系统需支持基金分红时，现金分红在分红转投日之前的情况    2010.10.18------------
                    " union (select FPortCode,FCashAccCode,FCuryCode,sum(FSellMoney * (-1)) As FSettleMoney from " +//MS01744 将所有的红利发放和分红转投加起来
                    pub.yssGetTableName("tb_ta_trade") +
                    " where FSellType in ('08','10')  and FPortCode = " +                          					//结算日产生应收应付和资金调拨的时候将‘红利发放’的金额减掉
                    //end-----zhouxiang MS01744   系统需支持基金分红时，现金分红在分红转投日之前的情况    2010.10.18------------
                    dbl.sqlString(sPortCode) + " and FConfimDate = " +
                    dbl.sqlDate(dDate) + " and FSettleDate = " +
                    dbl.sqlDate(dDate) + 
                    //modify by jsc 20120618 【STORY #2686 要求系统能做针对RQFII的分红，以及红利转投 】
                    " and FPORTCLSCODE ="+dbl.sqlString(rs2.getString("FPORTCLSCODE"))+ 
                    " and FCHECKSTATE = 1 group by FPortCode,FPORTCLSCODE,FCashAccCode,FCuryCode)" +

                    " union (select FPortCode,FCashAccCode,FCuryCode,FBal As FSettleMoney from " + pub.yssGetTableName("tb_stock_cashpayrec") +
                    " where FTsfTypeCode = '07' and FSubTsfTypeCode = '07FD' and FPortCode = " +
                    dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                    ")) y group by Fportcode,FCashAccCode,FCuryCode"//取结算日前一天库存
                    
                    /*begin by zhouxiang MS01744 系统需支持基金分红的现金分红再分红转投日之前的情况 ,此处将红利发放的结果算出来，如果没有则返回1有的话返回0 */
                    +" ) a left join (select FPortCode,FPORTCLSCODE, FCashAccCode,FCuryCode, case when FSellMoney is null then"
                    +" 1  else   FSellMoney * 0 end As FSettleMoney  from "+ pub.yssGetTableName("tb_ta_trade")
                    +" where FSellType in ('10')  and FPortCode ="
                    +dbl.sqlString(sPortCode) + " and FConfimDate = " +
                    dbl.sqlDate(dDate) + " and FSettleDate = " + dbl.sqlDate(dDate) +
                    " and FPORTCLSCODE ="+dbl.sqlString(rs2.getString("FPORTCLSCODE"))+ //【STORY #2686 要求系统能做针对RQFII的分红，以及红利转投 】 add by jsc 20120618
                    " and FCHECKSTATE = 1) b on a.FCashAccCode = b.FCashAccCode";
                    //end by zhouxiang MS01744 系统需支持基金分红的现金分红再分红转投日之前的情况         
                
                rs = dbl.queryByPreparedStatement(strSql); // 执行后得到一条记录集
                // 如果存在记录集，则生成现金应收应付对象和资金调拨对象
                if (rs.next()) {          	
                    cashPecPayBean = setSettleDateCash(rs,rs.getDouble("SumMoney"));
                    cashPecPayBean.setRelaNum(rs2.getString("Fnum"));//edited by zhouxiang MS01744 增加关联编号    2010.10.18
                    cashPayRecAdmin.addList(cashPecPayBean);
                }
                
                //rs是在循环里打开的，应用完就关闭，否则会占用资源
                dbl.closeResultSetFinal(rs);
                
                //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
        		//当日产生数据，则认为有业务。
        		if(subTransfer!=null && subTransfer.size()>0){
        			this.bstate = false;
        		}
            }
        } catch (Exception ex) {
            throw new YssException("生成结算日现金应收应付款和资金调拨数据出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs2);
        }
    }

    
    
    /**
     * 设置除权日产生的现金应收应付对象，并返回此对象
     * @param rs ResultSet
     * @return CashPecPayBean
     * @throws YssException
     */
    private CashPecPayBean setRightDateCash(ResultSet rs) throws YssException {
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        double Money = 0;
        double BaseMoney = 0;
        double PortMoney = 0;
        EachRateOper rateOper = new EachRateOper();     //新建获取利率的通用类
        CashPecPayBean cashBean = new CashPecPayBean(); // 新建现金应收应付对象
        try {
            BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取当日的基础汇率
                rs.getString("FCuryCode"), rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
            PortCuryRate = rateOper.getDPortRate();     //获取当日的组合汇率

            Money = rs.getDouble("fsellmoney");        //原币金额
            BaseMoney = this.getSettingOper().calBaseMoney(Money, BaseCuryRate);    //计算基础货币金额
            PortMoney = this.getSettingOper().calPortMoney(Money, BaseCuryRate,
                PortCuryRate, rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")); //计算组合货币金额

            cashBean.setBaseCuryRate(BaseCuryRate);
            cashBean.setPortCuryRate(PortCuryRate);
            cashBean.setMoney(Money); // 设置原币金额
            cashBean.setBaseCuryMoney(BaseMoney);
            cashBean.setPortCuryMoney(PortMoney);
            cashBean.setPortCode(rs.getString("FPortCode"));
            cashBean.setCashAccCode(rs.getString("FCashAccount"));
            cashBean.setInOutType(1); // 流入
            cashBean.setCuryCode(rs.getString("FCuryCode"));
            //edit by songjie 2013.02.28 STORY #3413 需求上海-[YSS_SH]QDIIV4.0[中]20121214002
            //业务日期 由 成交日期 改为 确认日
            cashBean.setTradeDate(rs.getDate("FConfimDate"));        // 设置业务日期
            cashBean.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);    // 设置业务类型为07
            cashBean.setSubTsfTypeCode("07FD");                     // 设置业务子类型为07FD
            cashBean.checkStateId = 1;                              // 设置审核状态为已审核
          //【STORY #2686 要求系统能做针对RQFII的分红，以及红利转投】 为了区分现金应收应付，需要在应收应付数据插入Ta交易数据作为关联编号  add by jsc 
            cashBean.setRelaNum(rs.getString("fnum"));            
            cashBean.setRelaNumType("FundDividend");
        } catch (Exception e) {
            throw new YssException("设置除权日现金应收应付数据时出现异常！", e);
        }
        return cashBean;
    }

    /**
     * 设置转投日产生的现金应收应付对象，并返回此对象
     * @param rs ResultSet
     * @return CashPecPayBean
     * @throws YssException
     */
    private CashPecPayBean setFundInvest(ResultSet rs) throws YssException {
        CashPecPayBean cashBean = new CashPecPayBean();
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        double Money = 0;
        double BaseMoney = 0;
        double PortMoney = 0;
        EachRateOper rateOper = new EachRateOper();                 //新建获取利率的通用类
        try {
            BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取转投日的基础汇率
                rs.getString("FCuryCode"), rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
            PortCuryRate = rateOper.getDPortRate();                 //获取转投日的组合汇率

            Money = rs.getDouble("FSellMoney");                     //销售金额
            BaseMoney = this.getSettingOper().calBaseMoney(Money, BaseCuryRate);            //计算基础货币金额
            PortMoney = this.getSettingOper().calPortMoney(Money, BaseCuryRate,
                PortCuryRate, rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")); //计算组合货币金额

            cashBean.setBaseCuryRate(BaseCuryRate);
            cashBean.setPortCuryRate(PortCuryRate);
            cashBean.setMoney(Money);                           // 设置销售金额
            cashBean.setBaseCuryMoney(BaseMoney);
            cashBean.setPortCuryMoney(PortMoney);
            cashBean.setPortCode(rs.getString("FPortCode"));
            cashBean.setCashAccCode(rs.getString("FCashAccCode"));
            cashBean.setInOutType(1);                           // 流入
            cashBean.setCuryCode(rs.getString("FCuryCode"));
            cashBean.setTradeDate(rs.getDate("FConfimDate"));   // 设置确认日期
            cashBean.setTsfTypeCode("03");                      // 设置业务类型为03
            cashBean.setSubTsfTypeCode("03FD");                 // 设置业务子类型为03FD
            cashBean.checkStateId = 1;                          // 设置审核状态为已审核
        } catch (Exception e) {
            throw new YssException("设置基金转投日现金应收应付数据时出现异常！", e);
        }
        return cashBean;
    }

    /**
     * 设置结算日产生的现金应收应付对象，并返回此对象
     * @param rs ResultSet
     * @return CashPecPayBean
     * @throws YssException
     */
    private CashPecPayBean setSettleDateCash(ResultSet rs,double cashPecPayMoney) throws YssException {
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        double Money = 0;
        double BaseMoney = 0;
        double PortMoney = 0;
        EachRateOper rateOper = new EachRateOper();                 //新建获取利率的通用类
        CashPecPayBean cashBean = new CashPecPayBean();
        try {
            BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取结算日的基础汇率
                rs.getString("FCuryCode"), rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
            PortCuryRate = rateOper.getDPortRate();                 //获取结算日的组合汇率

            Money = cashPecPayMoney;                                //原币金额
            BaseMoney = this.getSettingOper().calBaseMoney(Money, BaseCuryRate); //计算基础货币金额
            PortMoney = this.getSettingOper().calPortMoney(Money, BaseCuryRate,
                PortCuryRate, rs.getString("FCuryCode"), dDate,
                rs.getString("FPortCode"));                         //计算组合货币金额

            cashBean.setBaseCuryRate(BaseCuryRate);
            cashBean.setPortCuryRate(PortCuryRate);
            cashBean.setMoney(Money);                               // 设置原币金额
            cashBean.setBaseCuryMoney(BaseMoney);
            cashBean.setPortCuryMoney(PortMoney);
            cashBean.setPortCode(rs.getString("FPortCode"));
            cashBean.setCashAccCode(rs.getString("FCashAccCode"));
            cashBean.setInOutType(1);                               //流入
            cashBean.setCuryCode(rs.getString("FCuryCode"));
            cashBean.setTradeDate(dDate);                           // 设置业务日期
            cashBean.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);    // 设置业务类型为03
            cashBean.setSubTsfTypeCode("03FD");                     // 设置业务子类型03FD
            cashBean.checkStateId = 1;                              // 设置审核状态为已审核
        } catch (Exception e) {
            throw new YssException("设置结算日现金应收应付数据时出现异常！", e);
        }
        return cashBean;
    }

     /**
     *  资金调拨数据子对象，并返回此对象  
     * @param rs ResultSet
     * @return TransferSetBean
     * @throws YssException
     */
    private TransferSetBean setTransferSet(ResultSet rs,int inOut) throws YssException {
        TransferSetBean transferSet = null;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        try {
            transferSet = new TransferSetBean();
            double dBaseRate = 1;
            double dPortRate = 1;

            dBaseRate = this.getSettingOper().getCuryRate(
            	dDate,
                rs.getString("FCuryCode"), 
                rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);         //获取业务当天的基础汇率

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(
            	dDate, 
            	rs.getString("FCuryCode"),
                rs.getString("FPortCode"));
            dPortRate = rateOper.getDPortRate();    //获取业务当天的组合汇率

            transferSet.setIInOut( inOut);                              //资金调拨流方向 -1:流出  1：流入
            transferSet.setSPortCode(rs.getString("FPortCode"));
            transferSet.setSCashAccCode(rs.getString("FCashAccCode"));  //设置现金账户
            transferSet.setDMoney(rs.getDouble("SumMoney"));            //设置金额
            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
        return transferSet; //返回资金调拨子数据
    }

    /**
     * modify 产生的资金调拨数据对象，并返回此对象
     * @param rs ResultSet
     * @return TransferSetBean
     * @throws YssException
     */
    private TransferBean setTransfer(ResultSet rs,Date date) throws YssException {
        TransferBean transfer = null;
        try {
            transfer = new TransferBean();
            transfer.setDtTransDate(dDate); //业务日期
            transfer.setDtTransferDate(date); //调拨日期为结算日期
            transfer.setStrTsfTypeCode("04");
            transfer.setStrSubTsfTypeCode("0403");
            transfer.setFNumType("FundDividend"); //设置关联编号类型为TA基金分红
            transfer.checkStateId = 1;
            transfer.setDataSource(1);
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
        return transfer; //返回资金调拨数据
    }

    /**
     * 产生编号，并返回此编号
     * @param taTrade TaTradeBean
     * @return String
     * @throws YssException
     */
    private String getNum(TaTradeBean taTrade) throws YssException {
        String sFNum = "";
        sFNum = "T" +
            YssFun.formatDatetime(taTrade.getDSettleDate()).
            substring(0, 8) +
            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_TA_Trade"),
                                   dbl.sqlRight("FNUM", 6), "000001",
                                   " where FSettleDate = " +
                                   dbl.sqlDate(taTrade.getDSettleDate()));
        return sFNum;
    }

    /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate;         //调拨日期
        this.sPortCode = portCode;  //组合
    }
    /**
     * @author zhouxiang
     * MS01744  2010.10.18 系统需支持基金分红时，现金分红在分红转投日之前的情况
     * modify by guyichuan 20110328 结算日红利发放产生资金调拨为 红利发放后的余额
     * @throws YssException 
     * @ 说明：处理TA交易类型为”10“的”红利发放“的交易数据，该数据的结算日期输入要求必须是红利权益的结算日期，此处产生红利发放的应收应付和资金调拨
     */
    //edit by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B
    private void createInvestCashTransfer(CashPayRecAdmin cashPayRecAdmin) throws YssException {
    	 CashPecPayBean cashPecPayBean = null;
         TransferBean transfer = null;
         TransferSetBean transferSet = null;
         ArrayList subTransfer = null;
         CashTransAdmin cashtransAdmin = null;

         ResultSet rs = null;
         ResultSet rs2 = null;
         String strSql = "";
         String strSqlSettle = "";
         try {
             if (cashtransAdmin == null) {
                 cashtransAdmin = new CashTransAdmin(); //新建资金调拨控制对象
                 cashtransAdmin.setYssPub(pub);
             }
             // 查询语句：在结算日当天，查询TA基金权益信息表中权益类型为基金分红的记录
             strSqlSettle = "select * from " + pub.yssGetTableName("Tb_TA_FundRight") +
                 " WHERE FCHECKSTATE = 1 AND FRightType= 'Dividend' AND FSettleDate = " +
                 dbl.sqlDate(dDate) + " AND FPortCode = " + dbl.sqlString(sPortCode);
             rs2 = dbl.queryByPreparedStatement(strSqlSettle);
             // 循环记录集，产生现金应收应付和资金调拨
             while (rs2.next()) {
            	 //add by guyichuan 20110328 TASK #3586:: 
            	 // 查询语句：红利发放的现金应收应付和产生资金调拨的金额     
            	 strSql = " select TD.FPortCode,TD.FCashAccCode,TD.FCuryCode,TD.FSettleMoney,(FR.FRightMoney-TD.FSettleMoney)  As SumMoney,TD.fnum "
                        +" from" 
                        +" (select * from "
                        +pub.yssGetTableName("tb_ta_trade") 
                        +" where FSellType = '10'"
                        +" and FPortCode = " 
                        +dbl.sqlString(sPortCode)
                        +" and FConfimDate = "
                        +dbl.sqlDate(dDate)
                        +" and FSettleDate = "+dbl.sqlDate(dDate)
                        +" and FCHECKSTATE = 1"
                        +" and fportclscode=" +dbl.sqlString(rs2.getString("fportclscode"))
                        +"  ) TD"
                
                 +" left join"
                        +" (select Frightmoney,FCASHACCOUNT ,fportclscode from "
                        +pub.yssGetTableName("Tb_TA_FundRight")
                        + " where Fportcode= " 
                        +dbl.sqlString(sPortCode)
                        +" and Fsettledate= "
                        +dbl.sqlDate(dDate)
                        +" and FCHECKSTATE = 1"
                        +" ) FR" 
                        +" on FR.FCASHACCOUNT=TD.FCashAccCode and td.fportclscode = fr.fportclscode ";  
 
                 rs = dbl.queryByPreparedStatement(strSql); // 执行后得到一条记录集
                 // 如果存在记录集，则生成现金应收应付对象和资金调拨对象
                 if (rs.next()) {    	 
                     cashPecPayBean = setSettleDateCash(rs,rs.getDouble("FSettleMoney"));
                     cashPecPayBean.setRelaNum(rs.getString("fnum"));              //设置红利分发关联编号
                     cashPayRecAdmin.addList(cashPecPayBean);

                     transfer = setTransfer(rs,dDate); //获取资金调拨数据,调拨日期为结算日期
                     transfer.setFRelaNum(rs.getString("fnum"));
                     transferSet = setTransferSet(rs,1); //获取资金调拨子数据
                     subTransfer = new ArrayList(); //实例化放置资金调拨子数据的容器
                     subTransfer.add(transferSet); //将资金调拨子数据放入容器
                     transfer.setSubTrans(subTransfer); //将子数据放入资金调拨中
                     cashtransAdmin.addList(transfer);
                 }
                 // 产生现金应收应付数据和资金调拨数据，插入相应的数据库表中
                 //edit by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B
                 if (cashtransAdmin.getAddList().size() > 0) {
                     cashtransAdmin.insert(dDate, "FundDividend", 1, rs.getString("fnum")); //插入资金调拨,以调拨日期和关联编号类型,自动生成的来删除已有资金调拨                     
                     cashtransAdmin.getAddList().clear();
                 }
                 //rs是在循环里打开的，应用完就关闭，否则会占用资源
                 dbl.closeResultSetFinal(rs);
                 
               //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
         		//当日产生数据，则认为有业务。
         		if(subTransfer!=null && subTransfer.size()>0){
         			this.bstate = false;
         		}
             }
         } catch (Exception ex) {
             throw new YssException("生成结算日现金应收应付款和资金调拨数据出现异常！", ex);
         } finally {
             dbl.closeResultSetFinal(rs);
             dbl.closeResultSetFinal(rs2);
         }
		
	}
    
    
    /**
     * @author JIANGSHICHAO
     * @throws YssException 
     * @ 说明：处理TA交易类型为”10“的”红利发放“的交易数据，该数据的结算日期输入要求必须是红利权益的结算日期，此处产生红利发放的应收应付
     */
    //edit by songjie 2012.12.26 BUG 6701 QDV4南方2012年12月19日01_B
    private void createInvestCashPayRecHD( CashPayRecAdmin cashPayRecAdmin) throws YssException {
    	 CashPecPayBean cashPecPayBean = null;
         ResultSet rs = null;
         String strSql = "";
         try {
            	 //add by guyichuan 20110328 TASK #3586:: 
            	 // 查询语句：红利发放的现金应收应付和产生资金调拨的金额     
            	 strSql = " select TD.FPortCode,TD.FCashAccCode,TD.FCuryCode,TD.FSettleMoney,TD.fnum "
                        +" from" 
                        +" (select * from "
                        +pub.yssGetTableName("tb_ta_trade") 
                        +" where FSellType = '10'"
                        +" and FPortCode = " 
                        +dbl.sqlString(sPortCode)
                        +" and FConfimDate = "
                        +dbl.sqlDate(dDate)
                        +" and FSettleDate = "+dbl.sqlDate(dDate)
                        +" and FCHECKSTATE = 1"
                        +"  ) TD";
                
                 rs = dbl.queryByPreparedStatement(strSql); // 执行后得到一条记录集
                 // 如果存在记录集，则生成现金应收应付对象和资金调拨对象
                 while (rs.next()) {    	 
                     cashPecPayBean = setSettleDateCash(rs,rs.getDouble("FSettleMoney"));
                     cashPecPayBean.setRelaNum(rs.getString("fnum"));              //设置红利分发关联编号
                     cashPayRecAdmin.addList(cashPecPayBean);
                 }
                 
               //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
         		//当日产生数据，则认为有业务。
         		if(cashPayRecAdmin.getList()!=null && cashPayRecAdmin.getList().size()>0){
         			this.bstate = false;
         		}
             
         } catch (Exception ex) {
             throw new YssException("生成结算日现金应收应付款和资金调拨数据出现异常！", ex);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
	}
    
    private void getCJCashPayRec() throws YssException {
    	
        ResultSet rs = null;
        double money;
        
        CashPayRecAdmin prAdmin = new CashPayRecAdmin();
        CashPecPayBean cashpecpay = null;

        double dBaseRate = 1;
        double dPortRate = 1;

        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        
        try {
	        String strSql =
		            " select a1.*," +
		            " FTradeFee1 + FTradeFee2 + FTradeFee3 + FTradeFee4 + FTradeFee5 +" +
		            " FTradeFee6 + FTradeFee7 + FTradeFee8 as FTotalFee," +
		            " a2.FCashInd" +
		            " from " + pub.yssGetTableName("Tb_Ta_Trade") + " a1" +
		            " left join (select * from " +
		            pub.yssGetTableName("Tb_Ta_Selltype") +
		            " where FCheckState = 1) a2" +
		            " on a1.FSellType = a2.FSellTypeCode" +
		            " where a1.FPortCode in (" + operSql.sqlCodes(this.sPortCode) + ")" + //modify huangqirong 2013-02-27 bug #7180 
		            " and a1.FCheckState = 1" +
		            //" and (FConfimDate = " + dbl.sqlDate(dDate) + " or "
		            " and FSettleDate = " + dbl.sqlDate(dDate) +
		            " and FConfimDate <> FSettleDate and a2.FCashInd <> '0'" +
		        	" and a2.FSellTypeCode in ('03')"; 
	        rs = dbl.queryByPreparedStatement(strSql); 
	        while (rs.next()) {
	            cashpecpay = new CashPecPayBean();
	            cashpecpay.setTradeDate(dDate);
	            cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
	            cashpecpay.setPortCode(rs.getString("FPortCode"));
	
	            dBaseRate = rs.getDouble("FBaseCuryRate");
	            dPortRate = rs.getDouble("FPortCuryRate");
	
	            cashpecpay.setDataSource(0); //自动
	            cashpecpay.setCuryCode(rs.getString("FCuryCode"));
	
	            if (rs.getInt("FCashInd") == -1) { //现金方向流出
	                if (rs.getDouble("FSettleMoney") != 0) {
	                    money = rs.getDouble("FSettleMoney");
	                } else {
	                    money = YssD.add(rs.getDouble("FSellMoney"),
	                                     rs.getDouble("FTotalFee"));
	                }
	                cashpecpay.setMoney(money);
	                cashpecpay.setBaseCuryMoney(this.getSettingOper().
	                                            calBaseMoney(money, dBaseRate));
	                cashpecpay.setPortCuryMoney(this.getSettingOper().
	                                            calPortMoney(money, dBaseRate,
	                    dPortRate,
	                    rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")));
	                if (rs.getDate("FConfimDate").equals(dDate)) {
	                    cashpecpay.setTsfTypeCode("07"); //应付
	                    cashpecpay.setSubTsfTypeCode("07FD"); //应付
	                } else {
	                    cashpecpay.setTsfTypeCode("03"); //费用
	                    cashpecpay.setSubTsfTypeCode("03FD"); //费用
	                }
	            } else { //现金方向流入
	                
	            }
	            cashpecpay.setBaseCuryRate(dBaseRate);
	            cashpecpay.setPortCuryRate(dPortRate);
	            cashpecpay.checkStateId = 1;
	            prAdmin.addList(cashpecpay);
	        }
	        bTrans = true;
            conn.setAutoCommit(false);
			//20091019- MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
            prAdmin.setYssPub(pub);
            prAdmin.insert(dDate, "03", "03FD",  
            		       this.sPortCode, 0, false);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("冲减TA分红出现异常!\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(bTrans);
        }
    }
 //--- 【STORY #2686 要求系统能做针对RQFII的分红，以及红利转投 】  add by jsc 20120620 start --- 
    
    
    /**
     * 通过判断交易类型来判断模式，
     * 分红类型 03  金额方向  无         数量方向 无   --- 手工
     * 分红类型 03  金额方向  流出    数量方向 无   --- 自动 
     * @return
     * @throws YssException
     */
    private boolean getDealMode()throws YssException{
    	
    	
    	StringBuffer queryBuf = new StringBuffer();
    	ResultSet rs = null;
      try{
    	  
    	  queryBuf.append(" select 1 from ").append(pub.yssGetTableName("tb_ta_selltype")).append(" where fcheckstate=1 and fselltypecode='03' and fcashind=0 and famountind=0 ");
    	  
    	  rs = dbl.openResultSet(queryBuf.toString());
    	  if(rs.next()){
    		  return true;
    	  }else{
    		  return false;
    	  }
      }catch(Exception e){
    	  throw new YssException("判断Ta分红处理方式出错... ...");
      }	finally{
    	  queryBuf.setLength(0);
    	  dbl.closeResultSetFinal(rs);
      }
    }
    //--- end --- 
}
