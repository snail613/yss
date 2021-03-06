package com.yss.main.operdeal.opermanage.etflink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Hashtable;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * add by huangqirong 2012-07-07 story #2727 ETF联接基金 业务处理
 * */
public class ETFLinkDVPBaseOperManage extends BaseOperManage {
    //add by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001
	public int insertNum = 0;
	public ETFLinkDVPBaseOperManage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doOpertion() throws YssException {
		// TODO Auto-generated method stub
		this.getDelayDay(this.sPortCode , this.dDate);
	}

	@Override
	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		// TODO Auto-generated method stub
		this.sPortCode = portCode;
		this.dDate = dDate;
	}
	
	
	private void getDelayDay(String portCode , java.util.Date date){
		ResultSet rs = null;
		// key : Date(yyyy-MM-dd)   value : Date(yyyy-MM-dd)
		Hashtable<String, String> htHolidayCodes = new Hashtable<String, String>();
		String sql = "select FHolidaysCode,FDVPSETTLEOVER from " + pub.yssGetTableName("Tb_para_DVPBusSet") +
						" where FPortCode = " + dbl.sqlString(portCode)+ " group by FHolidaysCode ,FDVPSETTLEOVER ";
		//---add by songjie 2012.11.20 STORY 3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
		String delSql = "";
		boolean bTrans = false;
		Connection conn = null;
		//---add by songjie 2012.11.20 STORY 3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
		try {
			//---add by songjie 2012.11.20 STORY 3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
			conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;

			delSql = " delete from " + pub.yssGetTableName("Tb_Cash_Subtransfer") + 
			         " a where exists (select b.FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
			         " b where b.Fsubtsftypecode = '0107' and b.FCheckState = 1 and a.Fnum = b.FNum " +
			         " and b.Ftransferdate = " + dbl.sqlDate(this.dDate) +" and b.Ftransdate = " + 
			         dbl.sqlDate(this.dDate) + " ) and a.FCheckState = 1 ";
			
			dbl.executeSql(delSql);
			
			delSql = " delete from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
			" b where b.Fsubtsftypecode = '0107' and b.FCheckState = 1 " +
			" and b.Ftransferdate = " + dbl.sqlDate(this.dDate) + 
			" and b.Ftransdate = " + dbl.sqlDate(this.dDate);
			
			dbl.executeSql(delSql);
			//---add by songjie 2012.11.20 STORY 3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
			
			BaseOperDeal operDeal = new BaseOperDeal();
			operDeal.setYssPub(this.pub);
			
			rs = dbl.openResultSet(sql);
			while(rs.next()){
				String holidayCode = rs.getString("FHolidaysCode");
				int delayDay = rs.getInt("FDVPSETTLEOVER");
				String strDate = YssFun.formatDate(operDeal.getWorkDay(holidayCode , date , -delayDay), "yyyy-MM-dd"); //对应工作日
				
				if(!htHolidayCodes.containsKey(strDate)){
					htHolidayCodes.put(strDate, strDate);
				}
			}
			java.util.Enumeration<String> e = htHolidayCodes.elements();			
			while(e.hasMoreElements()){
				String keyDate = e.nextElement();		
				//edit by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 
				this.createCashTranData(portCode , keyDate, conn);
			}
			
			//---add by songjie 2012.11.20 STORY 3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //---add by songjie 2012.11.20 STORY 3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
		} catch (Exception e) {
			System.out.println("执行联接基金DVP业务处理出错：\n"+e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
			//add by songjie 2012.11.20 STORY 3184 需求北京-[建信基金]QDV4.0[中]20121023001
			dbl.endTransFinal(conn, bTrans);
		}
	}
	//add by songjie 2012.11.20 STORY 3184 需求北京-[建信基金]QDV4.0[中]20121023001
	private void createCashTranData(String portCode , String date, Connection conn){
		  	String strSql = "";
	        String strSqlSub = "";
	        String tmpSql = "";
	        ResultSet rs = null;
	        ResultSet rsTmp = null;
	        PreparedStatement pst = null; //资金调拨主表
	        PreparedStatement pstSub = null; //资金调拨子表
	        String sTransferType = "01"; //调拨类型
	        String sFNum = ""; //调拨编号
	        String sFTmpNum = ""; //存储已删除的主表记录的FNum，便于删除子表中相同FNum的记录
	        //---delete by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
	        //Connection conn = null ;
	        //boolean bTrans = false; //代表是否开始了事务
	        //---delete by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
	        String sDesc = "";
	        String sTmpCusCat = "";
	        boolean analy1;
	        boolean analy2;
	        try {
	        	//delete by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001
	        	//conn = dbl.loadConnection();
	        	boolean bTPVer =false;//区分是太平资产版本还是QDII的版本,合并版本时调整
	        	CtlPubPara pubPara = null; //区分太平资产与QD参数，合并版本时调整 
	        	pubPara =new CtlPubPara();
	        	pubPara.setYssPub(pub);
	        	String sPara =pubPara.getNavType();//通过净值表类型来判断
	        	if(sPara!=null && sPara.trim().equalsIgnoreCase("new")){
	        		bTPVer=false;//国内QDII模式
	        	}else{
	        		bTPVer=true;//太平资产模式
	        	}
	            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
	            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
	
	            BaseOperDeal baseOperBean = (BaseOperDeal) pub.getOperDealCtx().
	                getBean("baseoper");
	            baseOperBean.setYssPub(pub);
	            
	            //向调拨表插入数据
	            strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
	                " (FNum,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode,FTransferDate,FTransferTime,FTransDate,FTradeNum," +
	                "FSecurityCode,FDataSource,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" +
	                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	            pst = conn.prepareStatement(strSql);
	
	            //向调拨子表插入数据
	            strSqlSub = "insert into " + pub.yssGetTableName("Tb_Cash_SubTransfer") +	               
	                " (FNum,FSubNum,FInOut,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAttrClsCode,FCashAccCode,FMoney," +
	                "FBaseCuryRate,FPortCuryRate,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDesc)" +
	                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	            pstSub = conn.prepareStatement(strSqlSub);	   
	
	            //---delete by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
//	            conn.setAutoCommit(false);
//	            bTrans = true;
	            //---delete by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
	            
	            //取交易记录
	            strSql = " select distinct a.*, " +
				       " b.FTradeCury as sTradeCuryCode, " +
				       " b.FSecurityName, " +
				       " c.FPortCury as sPortCuryCode," +
				       " b.FCusCatCode," +
				       " b.FHolidaysCode," +
				       " b.FCatCode," +
				       " b.FSubCatCode," + 
				       " e.FTradeTypeName," +
				       " o.FInvMgrName as FInvMgrName," +
				       " q.FBrokerName as FBrokerName," +
				       " d.FCatCode as FCatCode," +
				       " e.FCashInd as FCashInd," +
				       " dvp.FCashCode as FCashCode" +
				       " from " + pub.yssGetTableName("Tb_Data_SubTrade") +" a " +
				       " left join (select pca1.FCashAcccode as FCashAcccode ,pca1.fcurycode as fcurycode from (" +
				       " select FCashAcccode , max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_CashAccount") + 
				       " where Fcheckstate = 1 group by FCashAcccode " +
				       " ) pca0 "+
				       " left join " + pub.yssGetTableName("Tb_Para_CashAccount") + " pca1 " +
				       " on pca0.FCashAcccode = pca1.FCashAcccode and "+
				       " pca0.FStartDate = pca1.FStartDate) fc on a.FCashAccCode = fc.FCashAccCode " +				       
				       " left join (select h.FSecurityCode as FSecurityCode," +
				       " h.FSecurityName, " +
				       " h.FTradeCury as FTradeCury, " +
				       " FCusCatCode, FCatCode, FSubCatCode, FExchangeCode, FHolidaysCode " +
				       " from " + pub.yssGetTableName("Tb_Para_Security") + " h join (select FSecurityCode, max(FStartDate) as FStartDate " +
				       " from " + pub.yssGetTableName("Tb_Para_Security") + " where FCheckState = 1 group by FSecurityCode) i on h.FSecurityCode = " +
				       " i.FSecurityCode and h.FStartDate = i.FStartDate) b on a.FSecurityCode = b.FSecurityCode " +
				       " left join (select FPortCode, FPortCury from " + pub.yssGetTableName("Tb_Para_Portfolio") +
				       " where FCheckState = 1) c on a.FPortCode = c.FPortCode " + 
				       " left join (select l.FCatCode as FCatCode, l.FSecurityCode as FSecurityCode " +
				       " from " + pub.yssGetTableName("Tb_Para_Security") + " l " +
				       " join (select FSecurityCode, max(FStartDate) as FStartDate " +
				       " from " + pub.yssGetTableName("Tb_Para_Security") + " where FCheckState = 1 group by FSecurityCode) m on l.FSecurityCode = m.FSecurityCode " +
				       " and l.FStartDate = m.FStartDate) d on a.FSecurityCode = d.FSecurityCode left join (select FTradeTypeCode, FTradeTypeName, FCashInd " +
				       " from Tb_Base_TradeType where FCheckState = 1) e on a.FTradeTypeCode = e.FTradeTypeCode " +
				       " left join (select FInvMgrCode, FInvMgrName from " + pub.yssGetTableName("Tb_Para_InvestManager")+ " where FCheckState = 1) o " +
				       " on a.FInvMgrCode = o.FInvMgrCode left join (select FBrokerCode, FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker") +
				       " where FCheckState = 1) q on a.FBrokerCode = q.FBrokerCode " +
				       " join " +
				       " (select dvp1.* from (select max(FStartDate) as FStartDate, " +
				       " FPortCode, FTradeTypeCode, FExchangeCode, FCategoryCode, FSubCatCode from " + pub.yssGetTableName("Tb_Para_DVPBusSet") +            
				       " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode)+ 
				       " group by FPortCode, FTradeTypeCode, FExchangeCode, FCategoryCode, FSubCatCode) dvp0 " +
				       " left join " + pub.yssGetTableName("Tb_para_DVPBusSet") + " dvp1 on dvp0.FPortCode = dvp1.FPortCode " +
				       " and dvp0.FTradeTypeCode = dvp1.FTradeTypeCode and dvp0.FExchangeCode = dvp1.FExchangeCode " +
				       " and dvp0.FCategoryCode = dvp1.FCategoryCode and dvp0.FSubCatCode = dvp1.FSubCatCode " +
				       " and dvp0.FStartDate = dvp1.FStartDate " +
				       " ) dvp " +
				       " on dvp.Fportcode =a.Fportcode " +
                      " and a.fsettlestate = 1 " +
                      " and dvp.FStartDate <= a.fbargaindate " +                            
                      " and dvp.FExchangeCode = (case when ( " +
                      							" select count(*) from " + pub.yssGetTableName("Tb_para_DVPBusSet")+ " tpdb " +
                      							" where FCheckState = 1 " +
                                                " and tpdb.Fportcode =a.Fportcode " +
                                                " and tpdb.FExchangeCode =b.FExchangeCode " +
                                                " and tpdb.FStartDate<= a.fbargaindate " +
                                                " )>0 " +
                                                " then b.FExchangeCode else ' ' end ) " +                                
                      " and dvp.FCategoryCode = ( case when (select count(*) from " + pub.yssGetTableName("Tb_para_DVPBusSet")+ " tpdb " +
                                                               " where FCheckState = 1 " +
                                                               " and tpdb.FStartDate<= a.fbargaindate " +
                                                               " and tpdb.Fportcode =a.Fportcode " +
                                                               " and tpdb.fcategorycode = b.FCatCode " +
                                                               " )>0 " +
                                                 " then b.FCatCode else ' ' end) " +                      
                      " and dvp.FSubCatCode = ( case when (select count(*) from " + pub.yssGetTableName("Tb_para_DVPBusSet")+ " tpdb " +
                                                 " where FCheckState = 1 " +
                                                 " and tpdb.FStartDate<= a.fbargaindate " +
                                                 " and tpdb.Fportcode =a.Fportcode " +
                                                 " and tpdb.fsubcatcode = b.FSubCatCode " +
                                                 " )>0 " +
                                                 " then b.FSubCatCode else ' ' end) " +
                      " and dvp.Ftradetypecode = ( case when (select count(*) from " + pub.yssGetTableName("Tb_para_DVPBusSet")+ " tpdb "+ 
                                                 " where FCheckState = 1 " +
                                                 " and tpdb.FStartDate<= a.fbargaindate " +
                                                 " and tpdb.Fportcode =a.Fportcode " +
                                                 " and tpdb.Ftradetypecode = a.Ftradetypecode " +
                                                 " )>0 " +
                                                 " then a.Ftradetypecode else ' ' end) " +
                      " and b.FHolidaysCode = dvp.FHolidaysCode " +
                      " and a.fbargaindate = " +dbl.sqlDate(date);
	            
	            rs = dbl.openResultSet(strSql);
	
	            while (rs.next()) {
	            	int cashInt = rs.getInt("FCashInd");	
	            	
	                sFNum = "C" +
	                    YssFun.formatDatetime(this.dDate).substring(0, 8) +
	                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"),
	                                           dbl.sqlRight("FNUM", 6), "000001");
	
	                //获取将要被删除的那条资金调拨数据中的FNum，以便删除子表中相同的FNum的记录
					//edit by songjie 2012.11.13 STORY #3214 需求深圳-[易方达基金]QDV4.0[紧急]20121030001 修改获取资金调拨数据编号的逻辑
	                tmpSql = "select FNum from " +
	                    pub.yssGetTableName("Tb_Cash_Transfer") +
	                    " where FTsfTypeCode='" + sTransferType +
	                    "' and FTransferDate=" +
	                    dbl.sqlDate(this.dDate) +
	                    " and FSecurityCode='" + rs.getString("FSecurityCode") +
	                    "' and FTradeNum = '" + rs.getString("FNum") +
	                    "' and FCheckState=1";
	                rsTmp = dbl.openResultSet(tmpSql);
	                //--- edit by songjie 2012.11.13 STORY #3214 需求深圳-[易方达基金]QDV4.0[紧急]20121030001 start---//
	                String nums = "";//获取资金调拨数据编号
	                while(rsTmp.next()){
	                	nums += rsTmp.getString("FNum") +",";
	                }
	                if(nums.length() > 1){
	                	nums = nums.substring(0,nums.length() - 1);
	                }
	                if (nums.length() > 0) {	                	
	                	sFTmpNum = operSql.sqlCodes(nums);
	                }
	                //--- edit by songjie 2012.11.13 STORY #3214 需求深圳-[易方达基金]QDV4.0[紧急]20121030001 end---//
	                if (rsTmp != null) {
	                    dbl.closeResultSetFinal(rsTmp);
	                    //删除子表中原有的相同的调拨数据
	                }
	                if(sFTmpNum.trim().length() > 0){
		                strSqlSub = "delete from " +
		                    pub.yssGetTableName("Tb_Cash_SubTransfer") +
		                    " where FNum in ( " +sFTmpNum +")";
		                dbl.executeSql(strSqlSub);
	
		                //删除主表中原有的相同的调拨数据
		                strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
		                    " where FTsfTypeCode='" + sTransferType +
		                    "' and FTransferDate=" +
		                    dbl.sqlDate(this.dDate) +
		                    " and FTransDate=" + dbl.sqlDate(this.dDate) +
		                    " and FSecurityCode='" + rs.getString("FSecurityCode") +
		                    "' and FTradeNum = '" + rs.getString("FNum") +
		                    "' and FCheckState=1 ";
		                dbl.executeSql(strSql);
	                }
	                
                	pst.setString(1, sFNum);
                	pst.setString(2, "01");//调拨类型 內部現金賬戶調撥
                	pst.setString(3, "0107"); //DVP内部现金账户调拨
                	
                	pst.setString(4, rs.getString("fattrclscode") != null ? rs.getString("fattrclscode"):" ");
                	pst.setDate(5, YssFun.toSqlDate(this.dDate));
                	pst.setString(6, "00:00:00");
                	pst.setDate(7, YssFun.toSqlDate(this.dDate));
                	pst.setString(8, rs.getString("FNum"));
                	pst.setString(9, rs.getString("FSecurityCode"));
                	pst.setInt(10, 1); //数据来源
                	pst.setString(11, " ");//fdesc
                	pst.setInt(12, 1); //FCheckState=3表示是用于监控临时存储的状态
                	pst.setString(13, pub.getUserCode());
                	pst.setString(14, YssFun.formatDatetime(new java.util.Date()));
                	pst.setString(15, pub.getUserCode());
                	pst.setString(16, YssFun.formatDatetime(new java.util.Date()));
                	
                	if(cashInt!=0)  //交易类型为为无 不产生资金调拨
                	{
				    	//add by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001
                		insertNum++;
                		pst.executeUpdate();
                	}
	
		        	//插入数据到调拨子表
		        	pstSub.setString(1, sFNum);
		        	pstSub.setString(2, "00001");
		        	pstSub.setInt(3, cashInt * -1); //反向
		        	pstSub.setString(4, rs.getString("FPortCode"));
		        	pstSub.setString(5, (analy1 ? rs.getString("FInvMgrCode") : " "));
		        	if (analy2) {
		        		if(bTPVer){//当为太平版本时，将分析代码设置为：所属分类 合并太平版本代码
		        			//   交易结算生成的资金调拨的所属分类未使用交易所属分类   
		        			pstSub.setString(6, 
		        					rs.getString("FAttrClsCode").trim().length() > 0 ? rs.getString("FAttrClsCode"): rs.getString("FCatCode")); 
		        		}else{
		        			sTmpCusCat = rs.getString("FCusCatCode") + "";
		        			if (sTmpCusCat.equalsIgnoreCase("null") ||
		        					sTmpCusCat.trim().length() == 0) {
		        				pstSub.setString(6, rs.getString("FCatCode"));
		        			} else {
		        				pstSub.setString(6, YssFun.right(sTmpCusCat, 2));
		        			}
		        		}
		        	} else {
		        		pstSub.setString(6, " ");
		        	}
		        	pstSub.setString(7, " ");			        	
		        	if(rs.getString("FAttrClsCode") == null || rs.getString("FAttrClsCode").trim().length() == 0)
		        		pstSub.setString(8," ");
			        else		                		
			            pstSub.setString(8, rs.getString("FAttrClsCode"));
			        
                	pstSub.setString(9, rs.getString("FFactCashAccCode")); //结算账户
                	pstSub.setDouble(10, rs.getDouble("FFactSettleMoney"));
                	pstSub.setDouble(11, rs.getDouble("FFactBaseRate"));
                	pstSub.setDouble(12, rs.getDouble("FFactPortRate"));
                	pstSub.setInt(13, 1); //FCheckState=3表示是用于监控临时存储的状态
                	pstSub.setString(14, pub.getUserCode());
                	pstSub.setString(15, YssFun.formatDatetime(new java.util.Date()));
                	pstSub.setString(16, pub.getUserCode());
                	pstSub.setString(17, YssFun.formatDatetime(new java.util.Date()));
                	sDesc = "[" + YssFun.formatDate(YssFun.toSqlDate(this.dDate)) +"]" + rs.getString("FTradeTypeName") + rs.getString("FSecurityName") +
                			"[" + rs.getString("FSecurityCode") + "]";	                	
                	pstSub.setString(18, sDesc);                	
                	
                	if(cashInt!=0) //交易类型为为无 不产生资金调拨
                	{
                		pstSub.executeUpdate();
                	}
                	
                	/*  第二笔 */
                	
                	//插入数据到调拨子表
		        	pstSub.setString(1, sFNum);
		        	pstSub.setString(2, "00002");
		        	pstSub.setInt(3, cashInt); //资金方向
		        	pstSub.setString(4, rs.getString("FPortCode"));
		        	pstSub.setString(5, (analy1 ? rs.getString("FInvMgrCode") : " "));
		        	if (analy2) {
		        		if(bTPVer){//当为太平版本时，将分析代码设置为：所属分类 合并太平版本代码
		        			//   交易结算生成的资金调拨的所属分类未使用交易所属分类   
		        			pstSub.setString(6, 
		        					rs.getString("FAttrClsCode").trim().length() > 0 ? rs.getString("FAttrClsCode"): rs.getString("FCatCode")); 
		        		}else{
		        			sTmpCusCat = rs.getString("FCusCatCode") + "";
		        			if (sTmpCusCat.equalsIgnoreCase("null") ||
		        					sTmpCusCat.trim().length() == 0) {
		        				pstSub.setString(6, rs.getString("FCatCode"));
		        			} else {
		        				pstSub.setString(6, YssFun.right(sTmpCusCat, 2));
		        			}
		        		}
		        	} else {
		        		pstSub.setString(6, " ");
		        	}
		        	pstSub.setString(7, " ");			        	
		        	if(rs.getString("FAttrClsCode") == null || rs.getString("FAttrClsCode").trim().length() == 0)
		        		pstSub.setString(8," ");
			        else		                		
			            pstSub.setString(8, rs.getString("FAttrClsCode"));
			                	
                	pstSub.setString(9, rs.getString("FCashCode")); //现金账户
                	pstSub.setDouble(10, rs.getDouble("FFactSettleMoney"));
                	pstSub.setDouble(11, rs.getDouble("FFactBaseRate"));
                	pstSub.setDouble(12, rs.getDouble("FFactPortRate"));
                	pstSub.setInt(13, 1); //FCheckState=3表示是用于监控临时存储的状态
                	pstSub.setString(14, pub.getUserCode());
                	pstSub.setString(15, YssFun.formatDatetime(new java.util.Date()));
                	pstSub.setString(16, pub.getUserCode());
                	pstSub.setString(17, YssFun.formatDatetime(new java.util.Date()));
                	sDesc = "[" + YssFun.formatDate(YssFun.toSqlDate(this.dDate)) +"]" + rs.getString("FTradeTypeName") + rs.getString("FSecurityName") +
                			"[" + rs.getString("FSecurityCode") + "]";	                	
                	pstSub.setString(18, sDesc);                	
                	
                	if(cashInt!=0) //交易类型为为无 不产生资金调拨
                	{
                		pstSub.executeUpdate();
                	}
		        }
	            //---add by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
	            if(insertNum == 0){
	            	this.sMsg="        当日无业务";
	            }
	            //---add by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
	            //---delete by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
//	            conn.commit();
//	            bTrans = false;
//	            conn.setAutoCommit(true);
	            //---delete by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        } finally {
	            dbl.closeStatementFinal(pst);
	            dbl.closeStatementFinal(pstSub);
	            //delete by songjie 2012.11.20 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 
	            //dbl.endTransFinal(conn, bTrans);
	            dbl.closeResultSetFinal(rs);
	            dbl.closeResultSetFinal(rsTmp);
	        }
  	}
}
