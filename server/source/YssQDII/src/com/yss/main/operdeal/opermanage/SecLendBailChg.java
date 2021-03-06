package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

import com.yss.core.util.YssFun;
import com.yss.util.YssException;
import com.yss.util.YssOperCons;
//add by zhaoxianlin 20121107 STORY #3208   业务处理
public class SecLendBailChg extends BaseOperManage {

	@Override   
	public void doOpertion() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		Boolean flag = true;
          try{
        	  strSql = " select a.*,b.FTradeCury  from "+pub.yssGetTableName("TB_DATA_SECLENDBAILCHANGE")+ " a  join " +
	     		" (select * from "+pub.yssGetTableName("Tb_Para_Security")+" where FcheckState = 1) b on  a.fsecuritycode =" +
	     				"b.fsecuritycode and a.FCheckState = 1 and a.FCHANGEDATE ="+dbl.sqlDate(dDate);
        	  rs= dbl.openResultSet(strSql);
        	  while(rs.next()){
        		  if(rs.getString("FbailAccCode").length()!=0&&rs.getDouble("FCHANGEMONEY")!=0){
        			  createCashTrans(rs);
        			  flag=false;
        		  }
        	  }
        	  delTransfer();//保证金调整数据反审核后删除产生的资金调拨数据
        	  if(flag){
        		  this.sMsg = "       当日无业务";  
        		  return;
        	  }
          }catch(Exception e){
        	  throw new YssException(e.getMessage());
          }finally{
        	  dbl.closeResultSetFinal(rs);
          }
	}
	
	public void delTransfer() throws YssException{
		String strSql = "";
		ResultSet rs = null;
		Boolean bTrans = false ;
		Connection conn= null;
		String sNum="";
		try{
			  conn = dbl.loadConnection();
			  conn.setAutoCommit(false);
			  bTrans = true;
			 strSql = " select a.*,b.FNum from "+pub.yssGetTableName("TB_DATA_SECLENDBAILCHANGE")+ " a join (select * from " 
			           +pub.yssGetTableName("Tb_Cash_Transfer")+" where FTsfTypeCode = '01' and FSubTsfTypeCode = '0005' and " +
			           	" FCheckState = 1) b on a.FChangeDate = b.FTransferDate and a.fsecuritycode=b.fsecuritycode and a.FChangeDate="+dbl.sqlDate(dDate)+" and a.FCheckState = 0";
		     rs = dbl.openResultSet(strSql);
		     while(rs.next()){
		    	  sNum = rs.getString("FNUM");
		    	  strSql = "delete from " +
	              pub.yssGetTableName("tb_cash_transfer") + " where FNum = " +dbl.sqlString(sNum) ;  //" where FNum like '" + sNum + "%'";
		          dbl.executeSql(strSql); //删除资金调拨表
		          strSql = "delete from " +
	              pub.yssGetTableName("Tb_Cash_SubTransfer") + " where FNum =" + dbl.sqlString(sNum) ;
	              dbl.executeSql(strSql); //删除资金调拨子表
		     }
		      conn.commit();
         	  conn.setAutoCommit(true);
         	  bTrans=false;
		}catch(Exception e){
			throw new YssException("保证金调整数据反审核后删除资金调拨数据出错！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}		
	}
	/**
	 * 生成资金调拨
	 * @param rs
	 * @throws YssException
	 */
	public void createCashTrans(ResultSet rs) throws YssException{
		String strSql = "";
		String sNum = "";  //编号
		Boolean bTrans = false ;
		Connection conn= null;
	    int   i = 0;
	    double baseRate=0;
	    double portRate=0;
		try{
			baseRate = this.getSettingOper().getCuryRate(
					  rs.getDate("FCHANGEDATE"), 
					  rs.getString("FTradeCury"), 
					  rs.getString("FportCode"), 
					  YssOperCons.YSS_RATE_BASE);
		     portRate = this.getSettingOper().getCuryRate(
		    		  rs.getDate("FCHANGEDATE"), 
		 		      "", 
		 		      rs.getString("FportCode"),
				      YssOperCons.YSS_RATE_PORT);
			  deleteTransfer(rs);
			  conn = dbl.loadConnection();
			  conn.setAutoCommit(false);
			  bTrans = true;
			  sNum = "C" + YssFun.formatDate(rs.getDate("FCHANGEDATE"), "yyyyMMdd") +
	            dbFun.getNextDataInnerCode();
			  strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
              "(FNum,FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransferTime,FTransDate," +
              "FSecurityCode,FDATASOURCE,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" +
              " values(" + dbl.sqlString(sNum) + "," +
              dbl.sqlString("01") + ","+  //调拨类型  ,FSrcCashAcc
              dbl.sqlString("0005") + ","+  //调拨子类型
              dbl.sqlDate(this.dDate) + "," +   //调拨日期
              dbl.sqlString("00:00:00") + "," +  //调拨时间
              dbl.sqlDate(this.dDate) + "," +    //业务日期
              dbl.sqlString(rs.getString("FSecurityCode")) + "," + 
               "1," + //数据来源  0-手工；1－自动
              dbl.sqlString(" ") + "," +
               "1," +  //审核状态
               dbl.sqlString(rs.getString("FCreator")) + "," +
               dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
               dbl.sqlString(pub.getUserCode()) + "," +  
               dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
              ")";
		      dbl.executeSql(strSql);
		      
		      strSql = "insert into " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
              "(FNum,FSubNum,FInOut,FPortCode,FCashAccCode,FMoney,FBaseCuryRate,FPortCuryRate," +
              "FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
              dbl.sqlString(sNum) + "," +
              dbl.sqlString(YssFun.formatNumber(i + 1, "00000")) + "," +
              "-1"+","+  //1代表流入;-1代表流出
              dbl.sqlString(rs.getString("FPortCode")) + "," +
              dbl.sqlString(rs.getString("FCashAccCode"))+","+  //现金账户流出
              rs.getDouble("FCHANGEMONEY") + "," +   //调整金额
              baseRate+ "," +
              portRate + "," +
              "1," +  //审核状态
              dbl.sqlString(rs.getString("FCreator")) + "," +
              dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
              dbl.sqlString(pub.getUserCode()) + "," +  
              dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
              ")";
          	  dbl.executeSql(strSql);//插入资金调拨子表
          	  
          	  i++;
		      strSql = "insert into " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
              "(FNum,FSubNum,FInOut,FPortCode,FCashAccCode,FMoney,FBaseCuryRate,FPortCuryRate," +
              "FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
              dbl.sqlString(sNum) + "," +
              dbl.sqlString(YssFun.formatNumber(i + 1, "00000")) + "," +
              "1"+","+  //1代表流入;-1代表流出
              dbl.sqlString(rs.getString("FPortCode")) + "," +
              dbl.sqlString(rs.getString("FbailAccCode"))+","+  //保证金账户流入
              rs.getDouble("FCHANGEMONEY") + "," +  //调整金额
              baseRate+ "," +
              portRate + "," +
              "1," +  //审核状态
              dbl.sqlString(rs.getString("FCreator")) + "," +
              dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
              dbl.sqlString(pub.getUserCode()) + "," +  
              dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
              ")";
          	  dbl.executeSql(strSql);//插入资金调拨子表
          	  
          	  conn.commit();
          	  conn.setAutoCommit(true);
          	  bTrans=false;
			
		}catch(Exception e){
			throw new YssException("生成资金调拨数据出错！\n", e);
		}finally{
			 dbl.endTransFinal(conn, bTrans);
		}		
}
	
	/**
	 * 删除资金调拨数据
	 * @param rs
	 * @throws YssException
	 */
	public void deleteTransfer(ResultSet rs) throws YssException{
		String strSql = "";
		Boolean bTrans = false ;
		Connection conn= null;
		String sNum="";
		ResultSet rst = null;
		try{
			 //sNum = "C" + YssFun.formatDate(rs.getDate("FBargainDate"), "yyyyMMdd");
			  conn = dbl.loadConnection();
			  conn.setAutoCommit(false);
			  bTrans = true;
			  strSql = " select * from "+pub.yssGetTableName("Tb_Cash_Transfer")+ " where FTsfTypeCode = '01'" +
			  "and FSubTsfTypeCode='0005' and FTransferDate="+dbl.sqlDate(dDate)+" and FSecurityCode = "+dbl.sqlString(rs.getString("FSecurityCode"));
			  rst=dbl.openResultSet(strSql);
			  while(rst.next()){
				  sNum = rst.getString("FNum");
				  strSql = "delete from " +
	              pub.yssGetTableName("tb_cash_transfer") + " where FNum = " +dbl.sqlString(sNum) ;  //" where FNum like '" + sNum + "%'";
		          dbl.executeSql(strSql); //删除资金调拨表
		          strSql = "delete from " +
	              pub.yssGetTableName("Tb_Cash_SubTransfer") + " where FNum =" + dbl.sqlString(sNum) ;
	              dbl.executeSql(strSql); //删除资金调拨子表
			  }
          	  conn.commit();
          	  conn.setAutoCommit(true);
          	  bTrans=false;
		}catch(Exception e){
			throw new YssException("删除资金调拨数据出错！\n", e);
		}finally{
			 dbl.endTransFinal(conn, bTrans);
			 dbl.closeResultSetFinal(rst);
		}		
}
	@Override
	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		 this.dDate = dDate;//调拨日期
	        this.sPortCode = portCode;//组合
	}

}
