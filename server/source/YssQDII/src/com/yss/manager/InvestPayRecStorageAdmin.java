package com.yss.manager;

import java.sql.*;
import java.util.*;

import com.yss.commeach.EachRateOper;
import com.yss.dsub.*;
import com.yss.main.storagemanage.*;
import com.yss.util.*;

public class InvestPayRecStorageAdmin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    public InvestPayRecStorageAdmin() {
    }

    public void addList(InvestPayRecBean investpayrec) {
        this.addList.add(investpayrec);
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports
        ) throws
        YssException {
        insert(dStartDate, dEndDate, ports, "", "", "", "");

    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, String invMgr,
                       String broker, String ivpaycatcode, String cury
        ) throws
        YssException {
        String strSql = "";
        InvestPayRecBean investpayrec = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
//      boolean bTrans=false;
        int i = 0;
        try {
//         conn.setAutoCommit(false);
//         bTrans=true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                " where FStorageDate between " + dbl.sqlDate(dStartDate) +
                " and " + dbl.sqlDate(dEndDate) +
                ( (ports == null || ports.length() == 0) ? " " :
                 " and FPortCode in (" + ports + ")") +
                ( (invMgr == null || invMgr.length() == 0) ?
                 " " :
                 " and FAnalysisCode1 = " + dbl.sqlString(invMgr)) +
                ( (broker == null || broker.length() == 0) ?
                 " " :
                 " and FAnalysisCode2 = " +
                 dbl.sqlString(broker)) +
                ( (ivpaycatcode == null ||
                   ivpaycatcode.length() == 0) ? " " :
                 " and FIVPayCatCode = " +
                 dbl.sqlString(ivpaycatcode)) +
                ( (cury == null || cury.length() == 0) ? " " :
                 " and FCuryCode = " + dbl.sqlString(cury));
//               " and FStorageInd <> 2 ";//MS00308 QDV4赢时胜上海2009年3月11日01_B 将所有状态的库存数据都删除 modify by shenjie
            if (dStartDate != null && dEndDate != null && dStartDate.equals(dEndDate)) {
                strSql = strSql + " and FYearMonth<>'" +
                    new Integer(YssFun.getYear(dStartDate)).toString() + "00'";
            }
            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                "(FYearMonth, FStorageDate, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3," +
                " FIVPayCatCode, FTsfTypeCode, FSubTsfTypeCode, FCuryCode, FBal, FBaseCuryBal, FPortCuryBal, " +
                " FStorageInd, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,fattrclscode)" +//--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 
//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            for (i = 0; i < this.addList.size(); i++) {
                investpayrec = (InvestPayRecBean) addList.get(i);
                yssPst.setString(1,
                              YssFun.formatDate(investpayrec.getDtStorageDate(), "yyyyMM"));
                yssPst.setDate(2, YssFun.toSqlDate(investpayrec.getDtStorageDate()));
                yssPst.setString(3, investpayrec.getSPortCode());
                yssPst.setString(4, investpayrec.getSAnalysisCode1());
                yssPst.setString(5, investpayrec.getSAnalysisCode2());
                yssPst.setString(6, investpayrec.getSAnalysisCode3());
                yssPst.setString(7, investpayrec.getSIvPayCatCode());
                yssPst.setString(8, investpayrec.getSTsfTypeCode());
                yssPst.setString(9, investpayrec.getSSubTsfTypeCode());
                yssPst.setString(10, investpayrec.getSCuryCode());
                yssPst.setDouble(11, investpayrec.getDBal());
                yssPst.setDouble(12, investpayrec.getDBaseBal());
                yssPst.setDouble(13, investpayrec.getDPortBal());
                yssPst.setInt(14, 0);
                yssPst.setInt(15, 1);
                yssPst.setString(16, pub.getUserCode());
                yssPst.setString(17, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(18, pub.getUserCode());
                yssPst.setString(19, YssFun.formatDatetime(new java.util.Date()));
              //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 ----//
                yssPst.setString(20, (investpayrec.getAttrClsCode().trim().length())!=0?investpayrec.getAttrClsCode():" ");
              //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao end------------//  
                yssPst.executeUpdate();

            }
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("系统保存应收应付库存金额时出现异常!" + "\n", e); //by 曹丞 2009.02.01 保存运营应收应付库存金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeStatementFinal(yssPst);
        }
    }
	
	// ----- QDV4中保2010年1月4日01_B add by jiangshichao 2010.01.06---------
	//合并太平版本代码
	public void updateAvgRate(InvestPayRecBean investpayrec,double basemoney,double portmoney)
			throws YssException {
		Connection conn = dbl.loadConnection();
		String strSql = "";
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
		try {
			strSql = "update "
					+ pub.yssGetTableName("tb_data_investpayrec")
					+ " set fbasecuryrate=?,fportcuryrate=? , fbasecurymoney=?, fportcurymoney=?"
					+ " where fportcode=? and fanalysiscode1=? and Fivpaycatcode=?  and FTsfTypeCode='03'  and ftransdate =? and fcheckstate=1";
//			pst = conn.prepareStatement(strSql);
			yssPst = dbl.getYssPreparedStatement(strSql);
			yssPst.setDouble(1, investpayrec.getDBaseRate());
			yssPst.setDouble(2, investpayrec.getDPortRate());
			yssPst.setDouble(3, basemoney);
			yssPst.setDouble(4, portmoney);
			
			yssPst.setString(5, investpayrec.getSPortCode());
			yssPst.setString(6, investpayrec.getSAnalysisCode1());
			yssPst.setString(7, investpayrec.getSIvPayCatCode());
			yssPst.setDate(8, YssFun.toSqlDate(investpayrec.getDtStorageDate()));

			yssPst.executeUpdate();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("【更新運營應收應付匯率出錯】！" + "\n", e);
		} finally {
			dbl.closeStatementFinal(yssPst);
			conn = null;
		}
	}


	/**
	 * 生成库存统计时的汇兑损益
	 * add by lidaolong 20110314 #386 增加一个功能，能够自动支付管理费和托管费
	 * @throws YssException 
	 */
	public void insertHDSY(java.util.Date dStartDate, java.util.Date dEndDate,
            String ports) throws YssException{
		  InvestPayRecBean investrecpaybal = null;
		ResultSet rs =null;
		
		   EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
           double BaseCuryRate = 0;
	        double PortCuryRate = 0;
		
		 boolean analy1; //判断是否需要用分析代码；杨
	     boolean analy2;
	     boolean analy3;
	        
		String strSql = "select * from " + pub.yssGetTableName("Tb_Stock_Invest")
				+" where  fstoragedate="+ dbl.sqlDate(dStartDate)+" and FPortCode in (" + ports + ")" +
		  		" and FCheckState = 1" ;
		try{
		     analy1 = operSql.storageAnalysis("FAnalysisCode1", "InvestPayRec");
	         analy2 = operSql.storageAnalysis("FAnalysisCode2", "InvestPayRec");
	         analy3 = operSql.storageAnalysis("FAnalysisCode3", "InvestPayRec");
	         
		 rs = dbl.openResultSet(strSql);
		 addList.clear();
		 while (rs.next()){
			   investrecpaybal = new InvestPayRecBean();
               investrecpaybal.setDtStorageDate(rs.getDate("fstoragedate"));
               
			 investrecpaybal.setSPortCode(rs.getString("FPortCode"));
             investrecpaybal.setSIvPayCatCode(rs.getString("FIVPayCatCode"));
             investrecpaybal.setAttrClsCode(rs.getString("Fattrclscode"));
             investrecpaybal.setSAnalysisCode1(analy1 ? rs.getString(
                 "FAnalysisCode1") : " ");
             investrecpaybal.setSAnalysisCode2(analy2 ? rs.getString(
                 "FAnalysisCode2") : " ");
             investrecpaybal.setSAnalysisCode3(analy3 ? rs.getString(
                 "FAnalysisCode3") : " ");
             investrecpaybal.setSTsfTypeCode("99");
             investrecpaybal.setSSubTsfTypeCode("99IP");
             investrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
             
             investrecpaybal.setDBal(0.0);
         
             
 	        BaseCuryRate = this.getSettingOper().getCuryRate(dStartDate, //获取两费用支付当日的基础汇率
	                rs.getString("FCuryCode"), rs.getString("FPortCode"),
	                YssOperCons.YSS_RATE_BASE);

	            rateOper.setYssPub(pub);
	            rateOper.getInnerPortRate(dStartDate, rs.getString("FCuryCode"),
	                                      rs.getString("FPortCode"));
	            PortCuryRate = rateOper.getDPortRate(); 
	            // 基础货币汇兑损益=今日库存原币金额 * 今日基础汇率 – 今日库存基础货币金额；
	           
         	investrecpaybal.setDBaseBal(YssD.round(YssD.sub(YssD.mul(rs.getDouble("FBal"), 
         																BaseCuryRate),
         										  rs.getDouble("FBaseCuryBal")),
         								2));
         	 //组合货币汇兑损益=今日库存原币金额 * 今日基础汇率 / 今日组合汇率 – 今日库存组合货币金额。
         	investrecpaybal.setDPortBal(YssD.round(YssD.sub(YssD.div(YssD.mul(rs.getDouble("FBal"),BaseCuryRate)
																	,PortCuryRate),
															rs.getDouble("FPortCuryBal")),
													2));
         	addList.add(investrecpaybal);
		 }
		 saveInvestPayRec(dStartDate,dEndDate,ports);//保存库存数据的汇兑损益
		}catch(Exception ex){
			throw new YssException("生成运营收支库存汇兑损益出错");
		}
		
	}
	
	/**
	 * 保存库存数据的汇兑损益.
	 * add by lidaolong 20110314 #386 增加一个功能，能够自动支付管理费和托管费
	 * @param dStartDate
	 * @param dEndDate
	 * @param ports
	 * @throws YssException
	 */
	private void saveInvestPayRec(java.util.Date dStartDate,java.util.Date dEndDate,String ports) throws YssException{
			String strSql = "";
	        InvestPayRecBean investpayrec = null;
	        //modified by liubo.Story #1757
	        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
	        //=================================
//	        PreparedStatement pst = null;
	        YssPreparedStatement yssPst = null;
	        //===============end==================
	        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		
		try{
			//为了防止重复操作，先删除数据
			strSql ="delete from "+ pub.yssGetTableName("Tb_Stock_InvestPayRec")+" where FTsfTypeCode='99' and FSubTsfTypeCode='99IP' AND FStorageDate between " + dbl.sqlDate(dStartDate) +
					" and " + dbl.sqlDate(dEndDate) +
						( (ports == null || ports.length() == 0) ? " " :
						" and FPortCode in (" + ports + ")");
			
			
			 strSql = "insert into " + pub.yssGetTableName("Tb_Stock_InvestPayRec") +
             "(FYearMonth, FStorageDate, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3," +
             " FIVPayCatCode, FTsfTypeCode, FSubTsfTypeCode, FCuryCode, FBal, FBaseCuryBal, FPortCuryBal, " +
             " FStorageInd, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,fattrclscode)" +//--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 
             " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 
//         pst = conn.prepareStatement(strSql);
			 yssPst = dbl.getYssPreparedStatement(strSql);

         for (int i = 0; i < this.addList.size(); i++) {
             investpayrec = (InvestPayRecBean) addList.get(i);
             yssPst.setString(1,
                           YssFun.formatDate(investpayrec.getDtStorageDate(), "yyyyMM"));
             yssPst.setDate(2, YssFun.toSqlDate(investpayrec.getDtStorageDate()));
             yssPst.setString(3, investpayrec.getSPortCode());
             yssPst.setString(4, investpayrec.getSAnalysisCode1());
             yssPst.setString(5, investpayrec.getSAnalysisCode2());
             yssPst.setString(6, investpayrec.getSAnalysisCode3());
             yssPst.setString(7, investpayrec.getSIvPayCatCode());
             yssPst.setString(8, investpayrec.getSTsfTypeCode());
             yssPst.setString(9, investpayrec.getSSubTsfTypeCode());
             yssPst.setString(10, investpayrec.getSCuryCode());
             yssPst.setDouble(11, investpayrec.getDBal());
             yssPst.setDouble(12, investpayrec.getDBaseBal());
             yssPst.setDouble(13, investpayrec.getDPortBal());
             yssPst.setInt(14, 0);
             yssPst.setInt(15, 1);
             yssPst.setString(16, pub.getUserCode());
             yssPst.setString(17, YssFun.formatDatetime(new java.util.Date()));
             yssPst.setString(18, pub.getUserCode());
             yssPst.setString(19, YssFun.formatDatetime(new java.util.Date()));

             yssPst.setString(20, (investpayrec.getAttrClsCode().trim().length())!=0?investpayrec.getAttrClsCode():" ");

             yssPst.executeUpdate();

         }

     } catch (Exception e) {
         throw new YssException("系统保存应收应付库存金额时出现异常!" + "\n", e); 
     } finally {
         dbl.closeStatementFinal(yssPst);
     }
		
	}
}
