package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.dsub.YssPreparedStatement;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdata.CGTBean;
import com.yss.main.operdata.SecLendFIFOBean;
import com.yss.main.operdata.SecurityOverSell;
import com.yss.pojo.cache.YssTradeAcc;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
//import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.YssOperCons;


/**
 * @author zhangjun ,2011-11-10  STORY #1433 证券卖空业务处理
 */
public class OperSecurityOverSell  extends BaseOperManage{ // BaseStgStatDeal
	
	
	public OperSecurityOverSell() {
    }
	
	public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate; 
        this.sPortCode = portCode; //组合
    }
	
	/**
     * 执行证券卖空业务处理
     */
	public void doOpertion() throws YssException {

        try {
        	securityOverSell(dDate, sPortCode);           
        } catch (Exception ex) {
            throw new YssException("处理证券卖空业务时出现异常！\n", ex);
        }
    }
	
	/**
     * 借入归还和借出召回时按先入先出法计算证券数量      
     */
	public void securityOverSell(java.util.Date dDate, String sPortCode) throws
    		YssException, SQLException {
		//String strSql = "";
		//ResultSet rs = null;
		//boolean fAnalysisCode1 = false;
		//boolean fAnalysisCode2 = false;
		HashMap secOverSellDetailMap = new HashMap(4096);
		try{
			/*strSql = "select * from " + pub.yssGetTableName("Tb_Para_StorageCfg") +
  				" where FCHECKSTATE = 1 and FSTORAGETYPE = 'Security'";
			rs = dbl.queryByPreparedStatement(strSql);
			if(rs.next()){
				if(rs.getString("FANALYSISCODE1") != null && rs.getString("FANALYSISCODE1").length() > 0){
					fAnalysisCode1 = true;
				}
				if(rs.getString("FANALYSISCODE2") != null && rs.getString("FANALYSISCODE2").length() > 0){
					fAnalysisCode2 = true;
				}
			}*/
			insertSecOverSellDetail(dDate, sPortCode);//插入当日借入借出数据至卖空先入先出明细表
			secOverSellDetailMap = getOverSellDetail(dDate, sPortCode);//获取卖空明细库存hash表
			updateOverSellDetailMap(dDate, sPortCode,secOverSellDetailMap);//通过归还、召回的数量更新HashMap的值			
			insertStockDetail(dDate, sPortCode,secOverSellDetailMap);//插入明细库存
			insertOverSellBook(dDate, sPortCode);//插入证券卖空台账表			
			
			//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
		    /**shashijie 2012-9-29 BUG 5859 若当天有卖空的业务进行处理时仍然提示当天无业务*/
			if(secOverSellDetailMap.size() < 2){
				this.sMsg="        当日无业务";
			}
			/**end shashijie 2012-9-29 BUG */
			/**【证券卖空业务处理】项调整
			 *  add by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
			createTransfer();  //资金调拨处理
			delTransfer();//交易数据反审核后删除产生的资金调拨数据
			/** -----end----- */
			
		}catch (Exception e) {
			throw new YssException("先入先出计算证券数量出错！\n", e);			
		}finally {
			//dbl.closeResultSetFinal(rs);			
		}		
	}
	/**
	 * 获取证券交易数据
	 * @throws YssException
	 */
	public void createTransfer() throws YssException{
		String strSql = "";
		ResultSet rs = null;
		try{
		     strSql = " select a.*,b.FTradeCury  from "+pub.yssGetTableName("Tb_DATA_SecLendTRADE")+ " a  join " +
		     		" (select * from "+pub.yssGetTableName("Tb_Para_Security")+" where FcheckState = 1) b on  a.fsecuritycode =" +
		     				"b.fsecuritycode and a.FCheckState = 1 and a.FBargainDate ="+dbl.sqlDate(dDate);
		     rs = dbl.openResultSet(strSql);
		     while(rs.next()){
		    	 if(rs.getString("FbailAccCode")==null||rs.getDouble("FbailMoney")==0){
		    	//保证金账户为空或者保证金金额为0，不产生资金调拨
		    		 continue;
		    	 }
		    	 else{//当界面所选择日期等于证券借贷交易数据成交日期，且保证金账户不为空、保证金金额不为0时，产生资金调拨
		    		 if((rs.getString("FbailAccCode").length()!=0&&rs.getDouble("FbailMoney")!=0)){//
			    		     doCashTransfer(rs);
			    	 }
		    	 }
		     }
		}catch(Exception e){
			throw new YssException("获取证券交易数据出错！\n", e);
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
			 strSql = " select a.*,b.FNum as FCashNum from "+pub.yssGetTableName("Tb_DATA_SecLendTRADE")+ " a join (select * from " 
			           +pub.yssGetTableName("Tb_Cash_Transfer")+" where FCheckState = 1) b on a.FBargainDate = b.FTransferDate " +
			           "and a.fsecuritycode=b.fsecuritycode and a.FBargainDate="+dbl.sqlDate(dDate)+" and a.FCheckState = 0";
		     rs = dbl.openResultSet(strSql);
		     while(rs.next()){
		    	  sNum = rs.getString("FCashNum");
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
			  strSql = " select * from "+pub.yssGetTableName("Tb_Cash_Transfer")+ " where FCheckState=1 and FTsfTypeCode = '01'" +
			  "and FSubTsfTypeCode='0001' and FTransferDate="+dbl.sqlDate(dDate)+" and FSecurityCode = "+dbl.sqlString(rs.getString("FSecurityCode"))+
			  " and FAttrClsCode ="+dbl.sqlString(rs.getString("FAttrClsCode"));
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
		}		
}
	/**
	 * 当交易方式为“借入”时一条从【证券借贷交易数据】界面的现金账户流出；
	 * 一条从【证券借贷交易数据】界面的保证金账户流入，金额为【证券借贷交易数据】界面的“保证金金额”
	 * 借入归还时，相反操作。
	 * 生成资金调拨
	 * @param rs
	 * @throws YssException
	 */
	public void doCashTransfer(ResultSet rs) throws YssException{
		String strSql = "";
		String sNum = "";  //编号
		Boolean bTrans = false ;
		Connection conn= null;
	    Boolean flag = true; //默认为借入 1 借入、2 借入归还 
	    int   i = 0;
	    double baseRate=0;
	    double portRate=0;
		try{
			 baseRate = this.getSettingOper().getCuryRate(
					  rs.getDate("FBargainDate"), 
					  rs.getString("FTradeCury"), 
					  rs.getString("FportCode"), 
					  YssOperCons.YSS_RATE_BASE);
		     portRate = this.getSettingOper().getCuryRate(
		    		  rs.getDate("FBargainDate"), 
		 		      "", 
		 		      rs.getString("FportCode"),
				      YssOperCons.YSS_RATE_PORT);
			  deleteTransfer(rs);
			  if(rs.getString("FTradeTypeCode").equals("Rcb")){
				  flag = false;
			  }
			  conn = dbl.loadConnection();
			  conn.setAutoCommit(false);
			  bTrans = true;
			  sNum = "C" + YssFun.formatDate(rs.getDate("FBargainDate"), "yyyyMMdd") +
	            dbFun.getNextDataInnerCode();
			  strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
              "(FNum,FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransferTime,FTransDate," +
              "FSecurityCode,FAttrClsCode,FDATASOURCE,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" +
              " values(" + dbl.sqlString(sNum) + "," +
              dbl.sqlString("01") + ","+  //调拨类型  ,FSrcCashAcc
              dbl.sqlString("0001") + ","+  //调拨子类型
              dbl.sqlDate(this.dDate) + "," +   //调拨日期
              dbl.sqlString("00:00:00") + "," +  //调拨时间
              dbl.sqlDate(this.dDate) + "," +    //业务日期
              dbl.sqlString(rs.getString("FSecurityCode")) + "," + 
              dbl.sqlString(rs.getString("FAttrclsCode")) + ","+
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
              "(FNum,FSubNum,FInOut,FPortCode,FCashAccCode,FMoney,FBaseCuryRate,FPortCuryRate,FAttrclsCode," +
              "FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
              dbl.sqlString(sNum) + "," +
              dbl.sqlString(YssFun.formatNumber(i + 1, "00000")) + "," +
              (flag?(rs.getString("FTradeTypeCode").equals("borrow")?"1":"-1"):(rs.getString("FTradeTypeCode").equals("borrow")?"-1":"1")) + ","+
              //1代表流入;-1代表流出
              dbl.sqlString(rs.getString("FPortCode")) + "," +
              dbl.sqlString((flag?rs.getString("FTradeTypeCode").equals("borrow")?rs.getString("FBailAccCode"):rs.getString("FBailAccCode"):
            	  rs.getString("FTradeTypeCode").equals("borrow")?rs.getString("FCashAccCode"):rs.getString("FCashAccCode"))) + ","+
              rs.getDouble("FbailMoney") + "," +
              baseRate+ "," +
              portRate + "," +
              dbl.sqlString(rs.getString("FAttrclsCode")) + ","+
              "1," +  //审核状态
              dbl.sqlString(rs.getString("FCreator")) + "," +
              dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
              dbl.sqlString(pub.getUserCode()) + "," +  
              dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
              ")";
          	  dbl.executeSql(strSql);//插入资金调拨子表
          	  
          	  i++;
		      strSql = "insert into " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
              "(FNum,FSubNum,FInOut,FPortCode,FCashAccCode,FMoney,FBaseCuryRate,FPortCuryRate,FAttrclsCode," +
              "FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
              dbl.sqlString(sNum) + "," +
              dbl.sqlString(YssFun.formatNumber(i + 1, "00000")) + "," +
              dbl.sqlString((flag?(rs.getString("FTradeTypeCode").equals("borrow")?"-1":"1"):(rs.getString("FTradeTypeCode").equals("borrow")?"1":"-1"))) + ","+
              //1代表流入;-1代表流出
              dbl.sqlString(rs.getString("FPortCode")) + "," +
              dbl.sqlString((flag?rs.getString("FTradeTypeCode").equals("borrow")?rs.getString("FCashAccCode"):rs.getString("FCashAccCode"):
            	  rs.getString("FTradeTypeCode").equals("borrow")?rs.getString("FBailAccCode"):rs.getString("FBailAccCode"))) + ","+
              rs.getDouble("FbailMoney") + "," +
              baseRate+ "," +
              portRate + "," +
              dbl.sqlString(rs.getString("FAttrclsCode")) + ","+
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
	
	
	//插入当日借入借出数据至卖空先入先出明细表
	public void insertSecOverSellDetail(java.util.Date dDate, String sPortCode) throws
			YssException, SQLException {
		String strSql = "";
		ResultSet rs = null;
		boolean bTrans = false; //代表是否开始了事务
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pst = null;
		YssPreparedStatement  pst = null;
        //=============end====================
		Connection conn = dbl.loadConnection();
		try{
			bTrans = true;
			strSql = "delete from " + pub.yssGetTableName("Tb_Stock_SecOverSell") +
			   		 " where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FLendDate = " + dbl.sqlDate(dDate);
			dbl.executeSql(strSql);
			strSql = "insert into " + pub.yssGetTableName("TB_Stock_SecOverSell") +
   					 " (FNUM,FLENDDATE,FBROKERCODE,FSECURITYCODE,FSECURITYSTATE,FAMOUNT,FPORTCODE,FLendRatio,FBailMoney,FCuryCode)" +   					 
   					 " values(?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//			pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
			strSql = "select * from " + pub.yssGetTableName("TB_DATA_SecLendTRADE") + 
					 " a left join ( select FNum as FNum_b,FBailMoney from " + pub.yssGetTableName("Tb_Data_SubTrade") + //关联交易子表取保证金
					 " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(sPortCode)+ " and FBargainDate = " + dbl.sqlDate(dDate) + 
   					 " ) b on a.FRELANUM = b.FNum_b " +
   					 " left join ( select FCashAccCode as FCashAccCode_c, FCuryCode from " + pub.yssGetTableName("Tb_para_cashaccount") +
   					 " where FCHECKSTATE = 1 ) c on a.FCashAccCode = c.FCashAccCode_c " +
   					 " where a.FCHECKSTATE = 1 and a.FPORTCODE = " + dbl.sqlString(sPortCode) +
   					 " and a.FBARGAINDATE = " + dbl.sqlDate(dDate) + " and a.FTRADETYPECODE in ('borrow','Loan')"; 
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next()){
				pst.setString(1, rs.getString("FNUM"));
				pst.setDate(2, rs.getDate("FBargainDate"));
				pst.setString(3, rs.getString("FBrokerCode"));//券商代码不能为空
				pst.setString(4, rs.getString("FSecurityCode"));
				pst.setString(5, rs.getString("FTradeTypeCode"));
				pst.setDouble(6, rs.getDouble("FTradeAmount"));
				pst.setString(7, rs.getString("FPortCode"));
				pst.setDouble(8, rs.getDouble("FLENDRATIO"));//借贷利率
				pst.setDouble(9, rs.getDouble("FBailMoney"));//保证金
				pst.setString(10, rs.getString("FCuryCode"));
				pst.addBatch();
			}
			pst.executeBatch();
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);			
		}catch(Exception e){
			throw new YssException("插入当日借入和借出数据至卖空先进先出明细库存出错！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
	    	dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans);
		}		
	}
	
	//add by zhangjun  
	//获取卖空先入先出明细表组合代码和证券代码作为Key值    向HashMap表填数据
	public HashMap getOverSellDetail(java.util.Date dDate, String sPortCode) throws
			YssException, SQLException {
		HashMap tempHash = new HashMap(4096);
		String strSecCode = "";
		ArrayList tempAry = new ArrayList();
		String strSql = "";
		ResultSet rs = null;
		try{
			strSql = "select * from (select * from " + pub.yssGetTableName("Tb_Stock_SecOverSell") + " where FLendDate = " +
   					dbl.sqlDate(dDate) + " and FPORTCODE = " + dbl.sqlString(sPortCode) + " union all (select * from " +
   					pub.yssGetTableName("Tb_Stock_SecOverSell") + " where FLendDate = (select max(FLendDate) from " + 
   					pub.yssGetTableName("Tb_Stock_SecOverSell") + " where FLendDate < " + dbl.sqlDate(dDate) + 
   					" and FPORTCODE = " + dbl.sqlString(sPortCode) + ") and (FAmount > 0 or FJSAmount > 0))) order by FSECURITYCODE,FNUM";
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next()){
				if(!strSecCode.equalsIgnoreCase(rs.getString("FPortCode") + rs.getString("FSECURITYCODE"))){//如果Key为初始值或者Key与之前一条数据的KeY不一致
					tempHash.put(strSecCode, tempAry);
					tempAry = new ArrayList();
				}
				SecurityOverSell secOverSellDetail = new SecurityOverSell();
				strSecCode = rs.getString("FPortCode") + rs.getString("FSECURITYCODE");
				
				secOverSellDetail.setfNum(rs.getString("FNum"));
				secOverSellDetail.setFAmount(rs.getDouble("FAmount"));
				secOverSellDetail.setFLendDate(YssFun.toSqlDate(this.dDate));
				secOverSellDetail.setFBrokerCode(rs.getString("FBrokerCode"));
				secOverSellDetail.setFPortCode(rs.getString("FPortCode"));
				secOverSellDetail.setFSecurityCode(rs.getString("FSecurityCode"));
				secOverSellDetail.setFSecurityState(rs.getString("FSecurityState"));	
				secOverSellDetail.setFLendRatio(rs.getDouble("FLendRatio"));
				secOverSellDetail.setFBailMoney(rs.getDouble("FBailMoney"));//保证金
				secOverSellDetail.setCuryCode(rs.getString("FCuryCode"));
				tempAry.add(secOverSellDetail);

			}			
			tempHash.put(strSecCode, tempAry);//最后一只证券的集合添加到HashMap中
			return tempHash;
		}catch(Exception e){
			throw new YssException("获取卖空先入先出明细库存出错！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}
	
	
	//通过借入归还、借出召回和借入送股的数量更新HashMap的值
	public void updateOverSellDetailMap(java.util.Date dDate, String sPortCode, HashMap tempHash) throws
			YssException, SQLException {		
	    String strSql = "";
	    String sql ="";
	    ResultSet rs = null;
	    ResultSet rSet = null;
	    ArrayList secAry = null;
	    SecurityOverSell securityOverSell = new SecurityOverSell();
	    //BaseOperDeal bsOperDeal=new BaseOperDeal();
	    double tAmount = 0;//存放借入归还或借出召回数量    
	    double sumAmount = 0;//用于存放同一支证券总的借入数量
	    double pAmount = 0;//存放每次借入应得的送股数量
	    double bailMoney = 0 ; //保证金
	    
	    SecLendFIFOBean fifoBean = null;
	    ArrayList fifoList = new ArrayList();
	    
	    //处理逻辑：先将当天有借入送股的送股数量更新当天的HashMap，再处理当天有借入归还和借出召回的证券。
	    //注：无需考虑借入归还送股和借出召回送股，因为借入送股的送股数量和借出送股的数量直接入该只证券的借入和借出数量了。
	    try{
	    	//借入送股 ,借出送股数据更新HashMap
	    	sql = "select * from " + pub.yssGetTableName("TB_DATA_SecLendTRADE") +    			  
			      " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(sPortCode) +
			      " and FBARGAINDATE = " + dbl.sqlDate(dDate) + 
			      " and FTRADETYPECODE in ('BInPaySec','BOutRecSec') " ;  //BInPaySec:借入送股   BOutRecSec: 借出送股          FTradeAmount 
	    	rs = dbl.queryByPreparedStatement(sql);
	    	while(rs.next()){ 
	    		secAry = (ArrayList)tempHash.get(rs.getString("FPortCode") + rs.getString("FSECURITYCODE"));
	    		tAmount = rs.getDouble("FTRADEAMOUNT");//借入送股  借出送股数量	    		
	    		if(secAry !=null){
	    			for(int i = 0; i<secAry.size(); i++){
		    			securityOverSell = (SecurityOverSell)secAry.get(i);
		    			sumAmount = sumAmount + securityOverSell.getFAmount();
		    		}
		    		if(sumAmount == 0 ){
		    			break;
		    		}
		    		for(int i = 0; i < secAry.size(); i++ ){
		    			securityOverSell = (SecurityOverSell)secAry.get(i);
		    			pAmount =  securityOverSell.getFAmount() / sumAmount * tAmount;
		    			securityOverSell.setFAmount(securityOverSell.getFAmount() + pAmount);
		    		}
		    		sumAmount = 0;	
	    		}	    		    		
	    	}
	    	//借入归还和借出召回数据更新HashMap
	    	strSql = "select * from " + pub.yssGetTableName("TB_DATA_SecLendTRADE") + 
	    	         " a left join ( select FNum as FNum_b,FBailMoney from " + pub.yssGetTableName("Tb_Data_SubTrade") +
	    	         " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(sPortCode) + "and FBargainDate = " + dbl.sqlDate(dDate) +
	    	         " and FTRADETYPECODE in ('01SS')) b on a.FRELANUM = b.FNum_b " +
	    	         " left join ( select FSecuritycode as FSecuritycode_c, FStartDate, FBrokerCode as FBrokerCode_c from " + pub.yssGetTableName("Tb_Para_SecurityLend") +
	    	         " where FCHECKSTATE = 1 ) c on a.FSecuritycode = c.FSecuritycode_c and a.FBrokerCode = c.FBrokerCode_c " +
		 	 		 " where a.FCHECKSTATE = 1 and a.FPORTCODE = " + dbl.sqlString(sPortCode) +
		             " and a.FBARGAINDATE = " + dbl.sqlDate(dDate) + " and a.FTRADETYPECODE in ('Rcb','Lr')" ; //借入归还（买入卖空）和借出召回数据
	        rSet = dbl.queryByPreparedStatement(strSql);
	        while(rSet.next()){       	
		        secAry = (ArrayList)tempHash.get(rSet.getString("FPortCode") + rSet.getString("FSECURITYCODE")); 				
		        tAmount = rSet.getDouble("FTRADEAMOUNT");
		        bailMoney = rSet.getDouble("FBailMoney");
		        if(secAry !=null){
		        	for(int i = 0; i<secAry.size(); i++){
		        		
		        		fifoBean = new SecLendFIFOBean();
		        		fifoBean.setNum(rSet.getString("FNum"));
		        		fifoBean.setRelaNum(((SecurityOverSell)secAry.get(i)).getfNum());
		        		fifoBean.setFLendDate(this.dDate);
		        		fifoBean.setFSettleDate(rSet.getDate("FSettleDate"));
		        		
			        	securityOverSell = (SecurityOverSell)secAry.get(i);					
			        	if(tAmount == 0){
			        		break;
			        	}
			        	if(tAmount < securityOverSell.getFAmount()){//借入归还或借出召回数量小于先入的当比库存
			        		securityOverSell.setFAmount(securityOverSell.getFAmount() - tAmount);
			        		fifoBean.setFAmount(tAmount);
			        		tAmount = 0;						
			        	}else if(tAmount >= securityOverSell.getFAmount()){
			        		fifoBean.setFAmount(securityOverSell.getFAmount());
			        		//securityOverSell.setFAmount(0);
			        		securityOverSell.setFAmount(YssD.sub(tAmount, securityOverSell.getFAmount())); //modified by zhaoxianlin #story 3208
			        		//tAmount -= securityOverSell.getFAmount();
			        		tAmount = securityOverSell.getFAmount();//modified by zhaoxianlin #story 3208
			        		
			        	}
			        	fifoList.add(fifoBean);
			        	//保证金处理
			        	if (securityOverSell.getFBailMoney() != 0 && bailMoney < securityOverSell.getFBailMoney()){
			        		securityOverSell.setFBailMoney(securityOverSell.getFBailMoney()- bailMoney);
			        	}else if (securityOverSell.getFBailMoney() != 0 && bailMoney >= securityOverSell.getFBailMoney() ){
			        		securityOverSell.setFBailMoney(0);
			        		bailMoney -= securityOverSell.getFBailMoney();
			        	}
			        }	    
		        }		        		
	        }
	        insertFIFOData(fifoList);
	    }catch(Exception e){
	    	throw new YssException("在更新HashMap时出现异常!\n", e);
	    }finally{
	    	dbl.closeResultSetFinal(rs);
	    	dbl.closeResultSetFinal(rSet);
	    }	   	
	}
	
	//根据HashMap的数据向FIFO先入先出明细库插入数据
	public void insertStockDetail(java.util.Date dDate, String sPortCode, HashMap tempHash) throws 
			YssException, SQLException {
		Connection conn = dbl.loadConnection();
	    boolean bTrans = false; //代表是否开始了事务
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//	    PreparedStatement pst = null;
	    YssPreparedStatement  pst = null;
        //=============end====================
	    String strSql = "";
	    ArrayList tempAry = null;
	    SecurityOverSell securityOverSell = new SecurityOverSell();	    
	    
	    double jsAmount = 0.0;
	    double bqIncome = 0.0;
	    double ljIncome = 0.0;
	    double baseRate = 0.0;
  	    double portRate = 0.0;
  	    double bbqIncome = 0.0;
  	    double bljIncome = 0.0;
  	    double marketPrice = 0.0;
	    try{
	    	 bTrans = true;
	    	 strSql = "delete from " + pub.yssGetTableName("Tb_Stock_SecOverSell") +
	   		 		  " where FPORTCODE = " + dbl.sqlString(sPortCode)  +  " and FLendDate = " + dbl.sqlDate(dDate);
	    	 dbl.executeSql(strSql);

	    	 strSql = "insert into " + pub.yssGetTableName("TB_Stock_SecOverSell") +
				      " (FNUM,FLENDDATE,FBROKERCODE,FSECURITYCODE,FSECURITYSTATE,FAMOUNT,FPORTCODE,FLENDRATIO,FBailMoney,FJSAmount,FBQIncome,FLJIncome,FCuryCode,FBBQIncome,FBLJIncome)" +   					 
				      " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				//modified by liubo.Story #2145
				//==============================
//		     pst = conn.prepareStatement(strSql);	  
	    	 pst = dbl.getYssPreparedStatement(strSql);
				//==============end================ 
		     Iterator it = tempHash.values().iterator();
		     while(it.hasNext()){
		    	 tempAry = (ArrayList)it.next();
		    	 for (int i = 0; i < tempAry.size(); i++){
		    		 securityOverSell = (SecurityOverSell)tempAry.get(i);
		    		 pst.setString(1, securityOverSell.getfNum());		    		 
		    		 pst.setDate(2, YssFun.toSqlDate(securityOverSell.getFLendDate())); 
		    		 pst.setString(3, securityOverSell.getFBrokerCode());
		    		 pst.setString(4, securityOverSell.getFSecurityCode());
		    		 pst.setString(5, securityOverSell.getFSecurityState());
		    		 pst.setDouble(6, securityOverSell.getFAmount());
		    		 pst.setString(7, securityOverSell.getFPortCode());
		    		 pst.setDouble(8, securityOverSell.getFLendRatio());
		    		 pst.setDouble(9, securityOverSell.getFBailMoney());
		    		 jsAmount = calJSAmont(securityOverSell.getfNum(), securityOverSell.getFAmount());
		    		 marketPrice = calMarketPrice(securityOverSell.getFSecurityCode());
		    		 bqIncome = calBQIncome(securityOverSell.getFAmount(), jsAmount, securityOverSell.getFLendRatio(), securityOverSell.getFSecurityCode(), securityOverSell.getFBrokerCode(), marketPrice);
		    		 ljIncome = calLJIncome(bqIncome, securityOverSell.getfNum());
		    		 baseRate = this.getSettingOper().getCuryRate(
							  this.dDate, 
							  securityOverSell.getCuryCode(), 
							  this.sPortCode, 
							  YssOperCons.YSS_RATE_BASE);
				     portRate = this.getSettingOper().getCuryRate(
				 		      this.dDate, 
				 		      "", 
						      this.sPortCode,
						      YssOperCons.YSS_RATE_PORT);
				     bbqIncome = this.getSettingOper().calPortMoney(
				    		  bqIncome,
							  baseRate, 
							  portRate,
							  securityOverSell.getCuryCode(), 
	                          this.dDate,
	                          this.sPortCode); 
				     bljIncome = this.getSettingOper().calPortMoney(
				    		  ljIncome,
							  baseRate, 
							  portRate,
							  securityOverSell.getCuryCode(), 
	                          this.dDate,
	                          this.sPortCode); 
		    		 pst.setDouble(10, jsAmount);
		    		 pst.setDouble(11, bqIncome);
		    		 pst.setDouble(12, ljIncome);
		    		 pst.setString(13, securityOverSell.getCuryCode());
		    		 pst.setDouble(14, bbqIncome);
		    		 pst.setDouble(15, bljIncome);

		    		 pst.addBatch();
		    	 }
		     }
		     pst.executeBatch();
			 conn.commit();
	         bTrans = false;
	         conn.setAutoCommit(true);
	    }catch(Exception e){
	    	throw new YssException("插入扣除借入归还和借出召回数据时，卖空先入先出明细库存出错！\n ", e);
	    }finally{
	    	dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans);
	    }
	}		
	
	//插入证券卖空台账表
	public void insertOverSellBook(java.util.Date dDate, String sPortCode) throws
			YssException, SQLException{
		Connection conn = dbl.loadConnection();
	    boolean bTrans = false; 	    
	    String strSql = "";	    
	    ResultSet rSet = null;
	    ResultSet rs = null;	    
	    ResultSet resultSet = null;
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//	    PreparedStatement pst = null;
	    YssPreparedStatement  pst = null;
        //=============end====================
	    double marketValue = 0;		 
		try{
			bTrans = true;
			strSql = " delete from " + pub.yssGetTableName("Tb_Data_SecOverSellBook") +
			         " where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FLendDate = " + dbl.sqlDate(dDate);
			dbl.executeSql(strSql);
			strSql = "insert into " + pub.yssGetTableName("Tb_Data_SecOverSellBook") +
				     " (FNUM,FPORTCODE,FLENDDATE,FAdversary,FSECURITYCODE,FSecurityName,FSECURITYSTATE,FAMOUNT," +   
				     "  FClosingPrice,FCurCode,FMarketValue,FBailMoney,FBailScale,FLendRatio,FOCurInterest,FOTotalInterest,FCurInterest,FTotalInterest,Flag)" +
				     " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//			pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
			//借入 、借出			
			strSql = " select *  from " + pub.yssGetTableName("Tb_Stock_SecOverSell") + " a "  +					 
					 /*" a left join ( select FNum as FNum_b,FRELANUM from " + 
					 pub.yssGetTableName("TB_DATA_SecLendTRADE") + " where FPORTCODE = " + dbl.sqlString(sPortCode) + 
					 " and FBargainDate = " + dbl.sqlDate(dDate)+ " ) b on a.FNum = b.FNum_b " + //借入借出数据  取借贷利率	
					 */				 
					 " left join ( select aa.fsecuritycode as fsecuritycode_c , fclosingprice from " + 
					 pub.yssGetTableName("Tb_Data_MarketValue") + " aa  join (select fsecuritycode, max(FMktValueDate) as FMktValueDate  from "  +					 
					 pub.yssGetTableName("Tb_Data_MarketValue") + " where FCheckState = 1 and FMktValueDate <= " + dbl.sqlDate(dDate) + 
					 " group by fsecuritycode ) bb on aa.fmktvaluedate = bb.fmktvaluedate  and aa.fsecuritycode = bb.fsecuritycode " + 					 
					 " ) c on a.FSecurityCode = c.FSecurityCode_c " + //取行情收盘价					 
					/* " left join ( select FSecurityCode as FSecurityCode_d,FPortCode as FPortCode_d,FMoney,FPortCuryMoney from " +
					 pub.yssGetTableName("Tb_Data_SecRecPay") + " where FSubTsfTypeCode in ('07PLI') and FTransDate = " + dbl.sqlDate(dDate) +
					 " ) d on a.FPortCode = d.FPortCode_d and a.FSecurityCode = d.FSecurityCode_d " + //取本期利息（币种代码,原币金额：FMoney，组合货币金额:FPortCuryMoney）
					 " left join ( select FSecurityCode as FSecurityCode_e,FPortCode as FPortCode_e,FBal,FPortCuryBal from " +
					 pub.yssGetTableName("Tb_Stock_SecRecPay") + " where FSubTsfTypeCode in ('07PLI') and FStorageDate =  " + dbl.sqlDate(dDate) + 
					 " ) e on a.FPortCode = e.FPortCode_e and a.FSecurityCode = e.FSecurityCode_e " + //取累计利息 ，原币金额：FBal，组合货币金额:FPortCuryBal
					 */
					 " left join ( select FSECURITYCODE as FSECURITYCODE_f,FSecurityName from " + 
					 pub.yssGetTableName("Tb_Para_Security") +
					 " ) f on a.FSECURITYCODE = f.FSECURITYCODE_f" +  //取证券名称,交易货币
					 " left join ( select FBrokerCode as FBrokerCode_h,FBrokerName from " + 
					 pub.yssGetTableName("Tb_Para_Broker") +
					 " ) h on a.FBrokerCode = h.FBrokerCode_h " +//取券商名称
					 //" left join ( select FNum as FNum_i, FBailMoney  from " + pub.yssGetTableName("Tb_Data_SubTrade") + 
					 //"  where  FPortCode = "  +  dbl.sqlString(sPortCode) + 
					 //" ) i on b.FRELANUM = i.FNum_i " +//取保证金	
					 " where a.FLendDate = " + dbl.sqlDate(dDate) +  "and a.FPortCode =  "  + dbl.sqlString(sPortCode);			
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next()){
				marketValue = YssD.round(YssD.mul(rs.getDouble("FAMOUNT"), rs.getDouble("FClosingPrice")), 2)  ; //市值
				pst.setString(1, rs.getString("FNum"));
				pst.setString(2, rs.getString("FPORTCODE"));
				pst.setDate(3, rs.getDate("FLENDDATE"));
				pst.setString(4, rs.getString("FBrokerName")); //券商（对手方）
				pst.setString(5, rs.getString("FSECURITYCODE"));
				pst.setString(6, rs.getString("FSecurityName")); 
				pst.setString(7, rs.getString("FSECURITYSTATE"));
				pst.setDouble(8, rs.getDouble("FAMOUNT"));				
				pst.setDouble(9, rs.getDouble("FClosingPrice"));
				pst.setString(10, rs.getString("FCuryCode"));
				pst.setDouble(11, marketValue); //市值 保留两位小数				
				pst.setDouble(12, rs.getDouble("FBailMoney")); //保证金				
				pst.setDouble(13, YssD.round(YssD.div(rs.getDouble("FBailMoney"),marketValue),2)); //保证金比例					
				pst.setDouble(14, rs.getDouble("FLendRatio")); //借贷利率
				pst.setDouble(15, rs.getDouble("FBQIncome"));//原币本期利息
				pst.setDouble(16, rs.getDouble("FLJIncome"));//原币累计利息
				pst.setDouble(17, rs.getDouble("FBBQIncome")); //本期利息
				pst.setDouble(18, rs.getDouble("FBLJIncome")); //累计利息
				pst.setString(19, "0");
				pst.addBatch();
			}			
			//借入归还、借出召回   Rcb,Lr
			strSql = "select * from " + pub.yssGetTableName("Tb_DATA_SecLendTRADE") +  
                     " a left join ( select aa.fsecuritycode as fsecuritycode_c , fclosingprice from  " + 
					 pub.yssGetTableName("Tb_Data_MarketValue") + " aa  join (select fsecuritycode, max(FMktValueDate) as FMktValueDate  from "  +					 
					 pub.yssGetTableName("Tb_Data_MarketValue") + " where FCheckState = 1 and FMktValueDate <= " + dbl.sqlDate(dDate) + 
					 " group by fsecuritycode ) bb on aa.fmktvaluedate = bb.fmktvaluedate and aa.fsecuritycode = bb.fsecuritycode " +
					 " ) c on a.FSecurityCode = c.FSecurityCode_c " + //取行情收盘价 			         
				     /*" left join ( select FSecurityCode as FSecurityCode_d,FPortCode as FPortCode_d,FMoney,FPortCuryMoney from " + 
				     pub.yssGetTableName("Tb_Data_SecRecPay") + " where FSubTsfTypeCode in ('07PLI') and FTransDate = " + dbl.sqlDate(dDate) +
				     " ) d on a.FPortCode = d.FPortCode_d and a.FSecurityCode = d.FSecurityCode_d " + //取本期利息（币种代码,基础汇率,组合汇率,组合货币金额）
				     " left join ( select FSecurityCode as FSecurityCode_e,FPortCode as FPortCode_e,FBal,FPortCuryBal from " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
				     " where FSubTsfTypeCode in ('07PLI') and FStorageDate = " + dbl.sqlDate(dDate) + 
				     " ) e on a.FPortCode = e.FPortCode_e and a.FSecurityCode = e.FSecurityCode_e " + //取累计利息 
*/				     " left join ( select FSECURITYCODE as FSECURITYCODE_f,FSecurityName,FTradeCury from " + pub.yssGetTableName("Tb_Para_Security") + 
				     " ) f on a.FSECURITYCODE = f.FSECURITYCODE_f" + //取证券名称
				     " left join ( select FBrokerCode as FBrokerCode_h,FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker") + 
				     " ) h on a.FBrokerCode = h.FBrokerCode_h " +//取券商名称
				     " left join ( select FNum as FNum_i,FBailMoney from " + pub.yssGetTableName("Tb_Data_SubTrade") + 
				     " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(sPortCode) + "and FBargainDate = " + dbl.sqlDate(dDate) + 
				     " and FTRADETYPECODE in ('01SS')) i on a.FRELANUM = i.FNum_i " + //保证金
				     " where a.FCHECKSTATE = 1 and a.FPORTCODE = " + dbl.sqlString(sPortCode) + " and a.FBARGAINDATE = " + dbl.sqlDate(dDate) + 
                     " and a.FTRADETYPECODE in ('Rcb','Lr') " ;
			rSet = dbl.queryByPreparedStatement(strSql);
			while(rSet.next()){
				marketValue = rSet.getDouble("FTradeAmount") * rSet.getDouble("FClosingPrice"); //市值
				pst.setString(1, rSet.getString("FNum"));
				pst.setString(2, rSet.getString("FPORTCODE"));
				pst.setDate(3, rSet.getDate("FBargainDate"));
				pst.setString(4, rSet.getString("FBrokerName")); //券商代码（对手方）
				pst.setString(5, rSet.getString("FSECURITYCODE"));
				pst.setString(6, rSet.getString("FSecurityName")); 
				pst.setString(7, rSet.getString("FTRADETYPECODE"));//证券状态
				pst.setDouble(8, rSet.getDouble("FTradeAmount"));				
				pst.setDouble(9, rSet.getDouble("FClosingPrice"));
				pst.setString(10, rSet.getString("FTradeCury"));
				pst.setDouble(11,YssD.round(marketValue,2)); //市值 保留两位小数				
				pst.setDouble(12, rSet.getDouble("FBailMoney")); //保证金 
				pst.setDouble(13, YssD.div(rSet.getDouble("FBailMoney"), marketValue, 2)); //保证金比例
				pst.setDouble(14, rSet.getDouble("FLendRatio")); //借贷利率
				pst.setDouble(15, 0);//原币本期利息
				pst.setDouble(16, 0);//原币累计利息
				pst.setDouble(17, 0); //本期利息
				pst.setDouble(18, 0); //累计利息
				pst.setString(19, "0");
				pst.addBatch();
			}
			
			//借入股利、借入归还股利、借出股利、借出召回股利、借入配股权证、借入归还配股权证、借出配股权证、借出召回配股权证
			//'BInPayDid','Drb','LOutRecDid','RLr', 'BInPayOP','Awrb','BOutRecOP','Lpwr'
			strSql = "select * from " + pub.yssGetTableName("Tb_DATA_SecLendTRADE")  + 
			         " a left join ( select aa.fsecuritycode as fsecuritycode_c , fclosingprice from  " + 
			         pub.yssGetTableName("Tb_Data_MarketValue") + " aa  join (select fsecuritycode, max(FMktValueDate) as FMktValueDate  from "  +
			         pub.yssGetTableName("Tb_Data_MarketValue") + " where FCheckState = 1 and FMktValueDate <= " + dbl.sqlDate(dDate) + 
			         " group by fsecuritycode ) bb on aa.fmktvaluedate = bb.fmktvaluedate and aa.fsecuritycode = bb.fsecuritycode " +
		             " ) c on a.FSecurityCode = c.FSecurityCode_c " + //取行情收盘价			         
			         " left join ( select FSECURITYCODE as FSECURITYCODE_f,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") + 
			         " ) f on a.FSECURITYCODE = f.FSECURITYCODE_f" + //取证券名称
			         " left join ( select FBrokerCode as FBrokerCode_h,FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker") + 
			         " ) h on a.FBrokerCode = h.FBrokerCode_h " + //取券商名称	
			         " left join ( select FCashAccCode as FCashAccCode_i,FCuryCode from " + pub.yssGetTableName("Tb_Para_CashAccount") + 
			         " where FStartDate <= " + dbl.sqlDate(dDate) + " and  FMatureDate >= " + dbl.sqlDate(dDate)+ 
			         " ) i on a.FCashAccCode = i.FCashAccCode_i " + // 取币种
			         " where a.FCHECKSTATE = 1 and a.FPORTCODE = " + dbl.sqlString(sPortCode) + " and a.FBARGAINDATE = " + dbl.sqlDate(dDate) + 
			         " and a.FTRADETYPECODE in ('BInPayDid','Drb','LOutRecDid','RLr', 'BInPayOP','Awrb','BOutRecOP','Lpwr') ";
			resultSet = dbl.queryByPreparedStatement(strSql); 
			
			//借入股利、借入归还股利、借出股利、借出召回股利、
			/*strSql= " select * from (select * from " + pub.yssGetTableName("Tb_Stock_SecOverSell")  + 
			        " a left join ( select FSECURITYCODE as FSECURITYCODE_b ,FTRADETYPECODE ,FTradePrice from " + pub.yssGetTableName("Tb_DATA_SecLendTRADE") + 
			        " where FCHECKSTATE = 1 and  FPORTCODE = " + dbl.sqlString(sPortCode) + " and FBARGAINDATE <= " + dbl.sqlDate(dDate) +
			        " and FTRADETYPECODE in ('BInPayDid','Drb','LOutRecDid','RLr')) b on a.FSECURITYCODE = b.FSECURITYCODE_b " +
			        " where a.FPORTCODE = " + dbl.sqlString(sPortCode) + " and a.FLendDate = " + dbl.sqlDate(dDate) +
			        " ) group by a.FSECURITYCODE ,FTRADETYPECODE_b";
			resultSet = dbl.queryByPreparedStatement(strSql);*/
			
			//借入配股权证、借入归还配股权证、借出配股权证、借出召回配股权证
			
			
			
			while(resultSet.next()){
				marketValue = resultSet.getDouble("FTradeAmount") * resultSet.getDouble("FClosingPrice"); //市值
				pst.setString(1, resultSet.getString("FNum"));
				pst.setString(2, resultSet.getString("FPORTCODE"));
				pst.setDate(3, resultSet.getDate("FBargainDate"));
				pst.setString(4, resultSet.getString("FBrokerName")); //券商代码（对手方）
				pst.setString(5, resultSet.getString("FSECURITYCODE"));
				pst.setString(6, resultSet.getString("FSecurityName")); 
				pst.setString(7, resultSet.getString("FTRADETYPECODE"));//证券状态
				pst.setDouble(8, resultSet.getDouble("FTradeAmount"));				
				pst.setDouble(9, resultSet.getDouble("FClosingPrice"));
				pst.setString(10, resultSet.getString("FCuryCode"));
				if (resultSet.getString("FTRADETYPECODE").equals("BInPayDid") ||  resultSet.getString("FTRADETYPECODE").equals("Drb") 
					 || resultSet.getString("FTRADETYPECODE").equals("LOutRecDid") || resultSet.getString("FTRADETYPECODE").equals("RLr")){
					pst.setDouble(11, resultSet.getDouble("FTradePrice") ); // 分红金额取借贷交易数据的交易价格
				}else{
					pst.setDouble(11, YssD.round(marketValue,2)); //市值 保留两位小数	
				}							
				pst.setDouble(12, 0); //保证金
				pst.setDouble(13, 0); //保证金比例
				pst.setDouble(14, 0); //借贷利率
				pst.setDouble(15, 0);//原币本期利息
				pst.setDouble(16, 0);//原币累计利息				
				pst.setDouble(17, 0); //本位币本期利息
				pst.setDouble(18, 0); //本位币累计利息
				pst.setString(19, "0");
				pst.addBatch();
			}			
			pst.executeBatch();
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);	        
	        //合计项处理
	        insertTotal(dDate, sPortCode);	               
		}catch(Exception e){
			throw new YssException("向卖空台账表插入数据时出现异常！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rSet);
			dbl.closeResultSetFinal(resultSet);			
			dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans);       
		}		
	}
	
	//合计项处理
	public void insertTotal(java.util.Date dDate, String sPortCode) throws
			YssException, SQLException{
		Connection conn = dbl.loadConnection();
	    boolean bTrans = false; 	    
	    String strSql = "";	    
	    ResultSet rSet = null;
	    ResultSet rs = null;	    
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//	    PreparedStatement pst = null;
	    YssPreparedStatement pst = null;
        //=============end====================
	    try{	    	   
	        bTrans = true;
	        strSql = " delete from " + pub.yssGetTableName("Tb_Data_SecOverSellBook") +
	                 " where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FLendDate = " + dbl.sqlDate(dDate) + "and Flag = 1 " ;
	        dbl.executeSql(strSql);
	        strSql = "insert into " + pub.yssGetTableName("Tb_Data_SecOverSellBook") +
		             " (FNUM,FPORTCODE,FLENDDATE,FAdversary,FSECURITYCODE,FSecurityName,FSECURITYSTATE,FAMOUNT," +   
		             "  FClosingPrice,FCurCode,FMarketValue,FBailMoney,FBailScale,FLendRatio,FOCurInterest,FOTotalInterest,FCurInterest,FTotalInterest,Flag)" +
		             " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//	        pst = conn.prepareStatement(strSql);
	        pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
	        //借入，借入送股，借入配股权证	     
	        strSql= "select FAdversary,sum(FBailMoney) as FBailMoney,sum(FMarketValue) as FMarketValue, sum(FCurInterest) as FCurInterest," +
	                " sum(FTotalInterest) as FTotalInterest ,sum(FOCurInterest) as FOCurInterest ,sum(FOTotalInterest) as FOTotalInterest  from " +
	                pub.yssGetTableName("Tb_Data_SecOverSellBook") +
	                " where FLendDate =" + dbl.sqlDate(dDate) + "and FSecurityState in ('borrow', 'BInPaySec', 'BInPayOP') and flag = 0 group by FAdversary ";
	        rs = dbl.queryByPreparedStatement(strSql);
	        while(rs.next()){							
	        	//modified by liubo.Bug #3560.此处不是BUG单的原始内容，由修改此BUG牵扯出的这个问题
	        	//一个对手方在某一天同时做借入和借出操作,INSERT操作就会报主键冲突。在原始的FNUM字段由对手方+组合代码的基础上加一个市值的值，避免出现主键冲突
	        	//============================================		
				pst.setString(1, rs.getString("FAdversary")+this.sPortCode+rs.getString("FMarketValue"));
				//===================end======================
				pst.setString(2, this.sPortCode);
				pst.setDate(3, YssFun.toSqlDate(this.dDate)); 
				pst.setString(4, rs.getString("FAdversary")); //券商代码（对手方）
				pst.setString(5, " ");
				pst.setString(6, " "); 
				pst.setString(7, "borrow");
				pst.setDouble(8, 0); //数量				
				pst.setDouble(9, 0); //收盘价
				pst.setString(10, " ");//币种
				pst.setDouble(11, rs.getDouble("FMarketValue")); //市值 保留两位小数				
				pst.setDouble(12, rs.getDouble("FBailMoney")); //保证金（未处理）
				pst.setDouble(13, YssD.div(rs.getDouble("FBailMoney"),rs.getDouble("FMarketValue"),2)); //保证金比例（未处理）
				pst.setDouble(14, 0); //借贷利率
				pst.setDouble(15, rs.getDouble("FOCurInterest"));//原币本期利息
				pst.setDouble(16, rs.getDouble("FOTotalInterest"));//原币累计利息				
				pst.setDouble(17, rs.getDouble("FCurInterest")); //本期利息
				pst.setDouble(18, rs.getDouble("FTotalInterest")); //累计利息
				pst.setString(19, "1");
				pst.addBatch();
			}	
	        //借出，借出送股，借出配股权证 
	        strSql= "select FAdversary,sum(FBailMoney) as FBailMoney,sum(FMarketValue) as FMarketValue, sum(FCurInterest) as FCurInterest," +
	                " sum(FTotalInterest) as FTotalInterest ,sum(FOCurInterest) as FOCurInterest ,sum(FOTotalInterest) as FOTotalInterest  from " +
	                pub.yssGetTableName("Tb_Data_SecOverSellBook") +
                    " where FLendDate =" + dbl.sqlDate(dDate) + "and FSecurityState in ('Loan', 'BOutRecSec', 'BOutRecOP') and flag = 0 group by FAdversary ";
	        rSet = dbl.queryByPreparedStatement(strSql);
	        while(rSet.next()){					
	        	//modified by liubo.Bug #3560.此处不是BUG单的原始内容，由修改此BUG牵扯出的这个问题
	        	//一个对手方在某一天同时做借入和借出操作,INSERT操作就会报主键冲突。在原始的FNUM字段由对手方+组合代码的基础上加一个市值的值，避免出现主键冲突
	        	//============================================
	        	pst.setString(1, rSet.getString("FAdversary")+this.sPortCode+rSet.getString("FMarketValue"));
	        	//=====================end========================
	        	pst.setString(2, this.sPortCode);
			    pst.setDate(3, YssFun.toSqlDate(this.dDate)); 
			    pst.setString(4, rSet.getString("FAdversary")); //券商代码（对手方）
			    pst.setString(5, " ");
			    pst.setString(6, " "); 
			    pst.setString(7, "Loan");
			    pst.setDouble(8, 0); //数量				
			    pst.setDouble(9, 0); //收盘价
			    pst.setString(10, " ");//币种
			    pst.setDouble(11, rSet.getDouble("FMarketValue")); //市值 保留两位小数				
			    pst.setDouble(12,rSet.getDouble("FBailMoney")); //保证金（未处理）
			    
			    //20120106 modified by liubo.Bug 3560
			    //===================================
				//pst.setDouble(13, YssD.div(rs.getDouble("FBailMoney"),rs.getDouble("FMarketValue"),2));
				pst.setDouble(13, YssD.div(rSet.getDouble("FBailMoney"),rSet.getDouble("FMarketValue"),2)); //保证金比例（未处理）
			    //=============end======================
				
			    pst.setDouble(14, 0); //借贷利率
			    pst.setDouble(15, rSet.getDouble("FOCurInterest"));//原币本期利息
				pst.setDouble(16, rSet.getDouble("FOTotalInterest"));//原币累计利息	
			    pst.setDouble(17, rSet.getDouble("FCurInterest")); //本期利息
			    pst.setDouble(18, rSet.getDouble("FTotalInterest")); //累计利息
			    pst.setString(19, "1");
			    pst.addBatch();
	        }	
	        pst.executeBatch();
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);	 
	    }catch(Exception e){
	    	throw new YssException("向卖空台账表插入合计项时出现异常！\n", e);
	    }finally{
	    	dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rSet);			
			dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans);	    	
	    }		
	}
	
	private void insertFIFOData(ArrayList list) throws YssException {
    	String strSql = "";
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pst = null;
    	YssPreparedStatement pst = null; 
        //=============end====================
		Connection conn = dbl.loadConnection();
		SecLendFIFOBean bean = null;
		boolean bTrans = false;
		try {			
			conn.setAutoCommit(false);
            bTrans = true;
            
            strSql = " delete from " + pub.yssGetTableName("Tb_Data_SECLENDFIFO") +
   			         " where FLENDDATE = " + dbl.sqlDate(dDate);
            dbl.executeSql(strSql);
            
			strSql = " insert into " + pub.yssGetTableName("Tb_Data_SECLENDFIFO")
				   + " (FNum, FRELANUM, FLENDDATE, FSETTLEDATE, FAMOUNT) "
				   + " values(?,?,?,?,?) ";
			//modified by liubo.Story #2145
			//==============================
//			pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
			for (int i = 0; i < list.size(); i++) {
				bean = (SecLendFIFOBean)list.get(i);
                pst.setString(1, bean.getNum());
                pst.setString(2, bean.getRelaNum());
                pst.setDate(3, YssFun.toSqlDate(this.dDate));
                pst.setDate(4, YssFun.toSqlDate(bean.getFSettleDate()));
                pst.setDouble(5, bean.getFAmount());
				pst.executeUpdate();
            }						
            conn.commit();
            bTrans = false;
		} catch(Exception e) {
			throw new YssException("插入证券借贷先入先出数据出错！", e);
		} finally {
			dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
		}
    } 
	
	private double calJSAmont(String num, double amount) throws YssException {
		double result = amount;
		String strSql = "";	    
	    ResultSet rs = null;	
		try {			
			strSql = " select sum(FTradeAmount)*(-1) as FAmount from " + pub.yssGetTableName("Tb_DATA_SecLendTRADE") +
			         " where FCheckState = 1 and FTRADETYPECODE in ('borrow','Loan') and FNum = " + dbl.sqlString(num) + 
			         " and FSettleDate > " + dbl.sqlDate(this.dDate) +
			         " union all " +
			         " select sum(FAmount) as FAmount from " + pub.yssGetTableName("Tb_DATA_SECLENDFIFO") + 
			         " where FRelaNum = " + dbl.sqlString(num) + " and FLendDate <= " + dbl.sqlDate(this.dDate) +
			         " and FSettleDate > " + dbl.sqlDate(this.dDate);
			rs = dbl.queryByPreparedStatement(strSql);
	        while(rs.next()){
	        	result += rs.getDouble("FAmount");
	        }
		} catch(Exception e) {
			throw new YssException("计算实际库存数量出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return result;
	}
	
	private double calBQIncome(double amount, double jsAmount, double rate, String securityCode, String brokerCode, double marketPrice) throws YssException {
		double result = 0.0;
		String strSql = "";	    
	    ResultSet rs = null;
		try {			
			strSql = " select FStartDate from " + pub.yssGetTableName("Tb_Para_SecurityLend") +
	                 " where FCHECKSTATE = 1 and FSecurityCode = " + dbl.sqlString(securityCode) +
	                 " and FBrokerCode = " + dbl.sqlString(brokerCode);
			rs = dbl.queryByPreparedStatement(strSql);
	        while(rs.next()){
	        	if("fsettledate".equalsIgnoreCase(rs.getString("FStartDate"))){
	        		result = YssD.round(YssD.mul(jsAmount, marketPrice), 2);	        		
	        	}else{
	        		result = YssD.round(YssD.mul(amount, marketPrice), 2);
	        	}
	        	result = YssD.round(YssD.div(YssD.mul(result, rate), 365), 2);
	        }
         } catch(Exception e) {
			throw new YssException("计算本期利息出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return result;
	}
	
	private double calLJIncome(double bqIncome, String num) throws YssException {
		double result = 0.0;
		String strSql = "";	    
	    ResultSet rs = null;
		try {			
			strSql = " select sum(FBQIncome) as FBQIncome from " + pub.yssGetTableName("Tb_Stock_SecOverSell") +
			         " where Fnum = " + dbl.sqlString(num) + " and FLendDate < " + dbl.sqlDate(this.dDate);
			rs = dbl.queryByPreparedStatement(strSql);
	        while(rs.next()){
        		result = YssD.add(rs.getDouble("FBQIncome"), bqIncome);

	        }
		} catch(Exception e) {
			throw new YssException("计算累计利息出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return result;
	}
	
	private double calMarketPrice(String securityCode) throws YssException {
		double result = 0.0;
		String strSql = "";	    
	    ResultSet rs = null;
		try {			
			strSql = " select aa.fsecuritycode, fclosingprice from  " + 
					 pub.yssGetTableName("Tb_Data_MarketValue") + " aa join (select fsecuritycode, max(FMktValueDate) as FMktValueDate  from "  +					 
					 pub.yssGetTableName("Tb_Data_MarketValue") + " where FCheckState = 1 and FMktValueDate <= " + dbl.sqlDate(dDate) + 
					 " group by fsecuritycode ) bb on aa.fmktvaluedate = bb.fmktvaluedate and aa.fsecuritycode = bb.fsecuritycode" +
					 " where aa.fsecuritycode = " + dbl.sqlString(securityCode); 
			rs = dbl.queryByPreparedStatement(strSql);
	        while(rs.next()){
        		result = rs.getDouble("fclosingprice");
	        }
		} catch(Exception e) {
			throw new YssException("计算累计利息出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return result;
	}
}
