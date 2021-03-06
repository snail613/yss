package com.yss.manager;

import com.yss.commeach.EachRateOper;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.storagemanage.*;
import com.yss.util.*;

public class CashStorageAdmin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    public CashStorageAdmin() {
    }

    public void addList(CashStorageBean cashstorage) {
        this.addList.add(cashstorage);
    }

    public void insert() throws YssException {
        insert(null, null, "", "", "", "", "");
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports
        ) throws YssException {
        insert(dStartDate, dEndDate, ports, "", "", "", "");
    }
    /**
     * add by huangqirong 2013-04-15 bug #7545 增加选中的现金账户
     * */
    public void insert(java.util.Date dStartDate, java.util.Date dEndDate, String ports ,String cashAccs) throws YssException {
    	insert(dStartDate, dEndDate, ports, "", "", cashAccs, "");
	}

    private String buildWhereSql(java.util.Date dStartDate,
                                 java.util.Date dEndDate,
                                 String ports,
                                 String invMgr,
                                 String cat,
                                 String cashacc,
                                 String cury
        ) throws YssException {

        String sResult = " where 1=1 ";
        if (dStartDate != null && dEndDate != null && dStartDate.equals(dEndDate)) {
            sResult = sResult + " and FYearMonth<>" +
                dbl.sqlString(new Integer(YssFun.getYear(dStartDate)).toString() + "00"); // lzp modify 20080123
        }

        if (dStartDate != null && dEndDate != null) {
            sResult += " and FStorageDate  between " +
                dbl.sqlDate(dStartDate) + " and " + dbl.sqlDate(dEndDate);
        }
		/**shashijie 2012-7-2 STORY 2475 */
        if (ports != null && ports.length() > 0) {
            sResult += " and FPortCode in (" + ports + ")";
        }
        if (invMgr != null && invMgr.length() > 0) {
            sResult += " and FAnalysisCode1 =" + dbl.sqlString(invMgr);
        }
        if (cat != null && cat.length() > 0) {
            sResult += " and FAnalysisCode2 =" + dbl.sqlString(cat);
        }
        if (cashacc != null && cashacc.length() > 0) {
            sResult += " and FCashAccCode in ( " + operSql.sqlCodes(cashacc) + " )"; //add by huangqirong 2013-04-15 bug #7545 选中的现金账户
        }
        if (cury != null && cury.length() > 0) {
            sResult += " and FCuryCode =" + dbl.sqlString(cury);
        }
		/**end*/
        return sResult;
    }

//删除操作
    public void delete(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, String invMgr,
                       String cat, String cashacc, String cury
        ) throws YssException {
        String strSql = "";
        String sWhereSql = "";
        try {
            sWhereSql = this.buildWhereSql(dStartDate, dEndDate, ports, invMgr,
                                           cat, cashacc, cury);
            if (sWhereSql.trim().length() == 0 ||
                sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
                return;
            }
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Cash") +
                sWhereSql;
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }

  //----- QDV4中保2010年1月4日01_B  add by jiangshichao  2010.01.06---------
   public void updateAvgRate() throws YssException{
	      
	      CashStorageBean cashstorage = null;
	      Connection conn = dbl.loadConnection();
	      try{
	    	  
	    	  for ( int i = 0; i < this.addList.size(); i++) {
	              cashstorage = (CashStorageBean) addList.get(i);
	            	  updateRate(conn,cashstorage);
	            	  updateRateFx(conn,cashstorage);   
	    	  }
	      }catch (Exception e) {
	          throw new YssException("统计现金库存时更新移动加权汇率出错!"+"\n", e);// by 曹丞 2009.02.02 保存现金库存金额异常信息 MS00004 QDV4.1-2009.2.1_09A
	       }
	       finally {
	          if(conn != null){
	        	  //conn.close();
	        	  conn=null;
	          }
	       }
   }
   
 //----- QDV4中保2010年1月4日01_B  add by jiangshichao  2010.01.06---------  更新资金调拨汇率
   public void updateRate(Connection conn,CashStorageBean cashStorage) throws YssException{
	      String strSql="";        
	      //modified by liubo.Story #1757
	        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
	        //=================================
//	        PreparedStatement pst = null;
	        YssPreparedStatement yssPst = null;
	        //===============end==================
	      try{
	    	  strSql = "update "+pub.yssGetTableName("tb_cash_subtransfer")+" set fbasecuryrate=?,fportcuryrate=? "
	    	           +" where fnum like 'C"+YssFun.formatDate(cashStorage.getStrStorageDate(), "yyyyMMdd")+"%' and fportcode='"+cashStorage.getStrPortCode()+"' and fanalysiscode1 ='"+cashStorage.getStrFAnalysisCode1()
                       +"' and fanalysiscode2='"+cashStorage.getStrFAnalysisCode2()+"' and fcashacccode='"+ cashStorage.getStrCashAccCode()+"' and finout='-1' and fcheckstate=1";
//	    	  pst = conn.prepareStatement(strSql);
	    	  yssPst = dbl.getYssPreparedStatement(strSql);
	    	  yssPst.setDouble(1, YssFun.toDouble(cashStorage.getStrBaseCuryRate()));
	    	  yssPst.setDouble(2, YssFun.toDouble(cashStorage.getStrPortCuryRate()));
	    	  
	    	  yssPst.executeUpdate();
	    	  conn.setAutoCommit(true);
	      }catch(Exception e){
	    	  throw new YssException("统计现金库存更新资金调拨汇率出错！"+"\n",e);
	      }finally {
	          dbl.closeStatementFinal(yssPst);
	      }
   }
   
 //----- QDV4中保2010年1月4日01_B  add by jiangshichao  2010.01.06  更新换汇业务的汇兑损益
   public void updateRateFx(Connection conn,CashStorageBean cashStorage) throws YssException{
	   String strSql="";
	   //modified by liubo.Story #1757
       //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
       //=================================
//       PreparedStatement pst = null;
       YssPreparedStatement yssPst = null;
       //===============end==================
	   ResultSet rs = null;
	   double dhdsy = 0;
	   double dSBaseRate = 0;
	   double dPortRate = 0;
	   try{ 
		   strSql = "select * from "+pub.yssGetTableName("tb_data_ratetrade")+" where fsettledate="+ dbl.sqlDate(YssFun.toSqlDate(cashStorage.getStrStorageDate()))
		            +" and fportcode='"+cashStorage.getStrPortCode()+"' and fanalysiscode1 ='"+cashStorage.getStrFAnalysisCode1()
                       +"' and fanalysiscode2='"+cashStorage.getStrFAnalysisCode2()+"' and fscashacccode='"+ cashStorage.getStrCashAccCode()+"'  and fcheckstate=1";
		   rs = dbl.openResultSet(strSql);
		   if(rs.next()){
			   //如果汇兑损益为0，则不做更新；否则使用统计库存后算出的移动加权汇率重新计算汇兑损益
			   if(rs.getDouble("fratefx")!=0){
				   Date tradeDate = rs.getDate("ftradedate");
				   String sCuryCode = rs.getString("fscurycode");
				   String bCuryCode = rs.getString("fbcurycode");
				   double dSMoney = rs.getDouble("fsmoney");
				   double dBMoney = rs.getDouble("fbmoney");
				   //获取换汇交易的汇率
				   dSBaseRate = this.getSettingOper().getCuryRate(tradeDate, bCuryCode,cashStorage.getStrPortCode(), YssOperCons.YSS_RATE_BASE);
		           EachRateOper ratePort =new EachRateOper();
		           ratePort.setYssPub(pub);
		           ratePort.getInnerPortRate(tradeDate,sCuryCode,cashStorage.getStrPortCode());
		           dPortRate = ratePort.getDPortRate();
		           double AvgBaseRate =  YssFun.toDouble(cashStorage.getStrBaseCuryRate()); 
		             dhdsy = YssD.sub(YssD.round(YssD.div(YssD.mul(dBMoney,  dSBaseRate),dPortRate ), 2),YssD.round(YssD.mul(AvgBaseRate, dSMoney), 2));
			   }
			   strSql = "update "+pub.yssGetTableName("tb_data_ratetrade")+" set fratefx = ? where fsettledate=? and fportcode=? and fanalysiscode1=?" 
			            +" and fanalysiscode2=? and fscashacccode=? and fcheckstate=1"; 
			   
//			   pst = conn.prepareStatement(strSql);
			   yssPst = dbl.getYssPreparedStatement(strSql);
			   yssPst.setDouble(1, dhdsy);
			   yssPst.setDate(2, YssFun.toSqlDate(cashStorage.getStrStorageDate()));
			   yssPst.setString(3, cashStorage.getStrPortCode());
			   yssPst.setString(4, cashStorage.getStrFAnalysisCode1());
		       yssPst.setString(5, cashStorage.getStrFAnalysisCode2());
		       yssPst.setString(6, cashStorage.getStrCashAccCode());
		       yssPst.executeUpdate();
		       conn.setAutoCommit(true);
		   }
	   }catch(Exception e){
	    	  throw new YssException("统计现金库存更新换汇业务的汇兑损益出错！"+"\n",e);
	      }finally {
	    	  if(rs != null){
	    			  dbl.closeResultSetFinal(rs);
	    			  dbl.closeStatementFinal(yssPst);
	    	  }
	          
	      }
   }
    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, String invMgr,
                       String cat, String cashacc, String cury
        ) throws YssException {
        String strSql = "";
        CashStorageBean cashstorage = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        int i = 0;
        try {
            //2008.07.30 蒋锦 修改 添加事务控制
            conn.setAutoCommit(false);
            bTrans = true;

            //执行删除操作
            this.delete(dStartDate, dEndDate, ports, invMgr, cat, cashacc, cury);

            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_Cash") +
                "(FCashAccCode,FYearMonth,FStorageDate,FPortCode,FCuryCode,FAccBalance,FPortCuryRate,FPortCuryBal,FBaseCuryRate,FBaseCuryBal,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3" +
                ",FStorageInd,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,fattrclscode)" +//--- add by jiangshichao NO.125 用户需要对组合按资本类别进行子组合的分类
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//--- add by jiangshichao NO.125 用户需要对组合按资本类别进行子组合的分类
//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            for (i = 0; i < this.addList.size(); i++) {
                cashstorage = (CashStorageBean) addList.get(i);
                yssPst.setString(1, cashstorage.getStrCashAccCode());
                yssPst.setString(2,
                              YssFun.formatDate(cashstorage.getStrStorageDate(),
                                                "yyyyMM"));
                yssPst.setDate(3,
                            YssFun.toSqlDate(YssFun.toDate(cashstorage.
                    getStrStorageDate())));
                yssPst.setString(4, cashstorage.getStrPortCode());
                yssPst.setString(5, cashstorage.getStrCuryCode());
                yssPst.setDouble(6, YssFun.toDouble(cashstorage.getStrAccBalance()));
                yssPst.setDouble(7, YssFun.toDouble(cashstorage.getStrPortCuryRate()));
                yssPst.setDouble(8, YssFun.toDouble(cashstorage.getStrPortCuryBal()));
                yssPst.setDouble(9, YssFun.toDouble(cashstorage.getStrBaseCuryRate()));
                yssPst.setDouble(10, YssFun.toDouble(cashstorage.getStrBaseCuryBal()));
                yssPst.setString(11,
                              cashstorage.getStrFAnalysisCode1().trim().length() ==
                              0 ?
                              " " : cashstorage.getStrFAnalysisCode1());
                yssPst.setString(12,
                              cashstorage.getStrFAnalysisCode2().trim().length() ==
                              0 ?
                              " " : cashstorage.getStrFAnalysisCode2());
                yssPst.setString(13,
                              cashstorage.getStrFAnalysisCode3().trim().length() ==
                              0 ?
                              " " : cashstorage.getStrFAnalysisCode3());
                // pst.setDouble(14, 0); //入库标识
                yssPst.setDouble(14, cashstorage.getIntStorageState()); //入库标识
                yssPst.setInt(15, cashstorage.checkStateId);
                yssPst.setString(16, pub.getUserCode());
                yssPst.setString(17, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(18, pub.getUserCode());
                yssPst.setString(19, YssFun.formatDatetime(new java.util.Date()));
				//--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 ----//
                yssPst.setString(20, (cashstorage.getStrAttrClsCode().trim().length())!=0?cashstorage.getStrAttrClsCode():" ");
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao end------------//  
                yssPst.executeUpdate();

            }
            //2008.07.30 蒋锦 修改 添加事务控制
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("系统保存现金库存金额时出现异常!" + "\n", e); // by 曹丞 2009.02.02 保存现金库存金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeStatementFinal(yssPst);
            //2008.07.30 蒋锦 添加事务控制
            dbl.endTransFinal(conn, bTrans);
        }

    }
}
