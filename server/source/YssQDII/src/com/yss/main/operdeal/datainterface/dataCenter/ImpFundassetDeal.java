package com.yss.main.operdeal.datainterface.dataCenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/********************************************************
 * 数据中心接口： 基金资产表（fundasset）
 *  MS01541 QDV4赢时胜上海2010年08月4日01_AB  数据中心
 * @author jiangshichao
 * @date   2010.07.22
 *
 */
public class ImpFundassetDeal extends BaseDataCenter {
    
	private HashMap fundCodeMap = new HashMap();
	private String msg = "";
	HashMap assetMap = null;
	
	private void initFundCodeMap ()throws YssException {
		ResultSet rs = null;
		ResultSet rs1 = null;
		try{
			//start modified by yeshenghong story3702 20130415
			rs = dbl.openResultSet(" select fassetgroupcode from TB_SYS_ASSETGROUP ");
			
			while(rs.next()){
				String query = " select a.fassetcode,b.fsetcode from tb_" + rs.getString("fassetgroupcode") + "_para_portfolio a"+
		           " join lsetlist b on a.fassetcode = b.fsetid where a.fcheckstate=1";
				rs1 = dbl.openResultSet(query);
				while(rs1.next()){
					fundCodeMap.put(rs1.getString("fassetcode"),rs1.getString("fsetcode"));
				}
				dbl.closeResultSetFinal(rs1);//modified by yeshenghong story3702 20130415
			}
			//-------end modified by yeshenghong story3702 20130415
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【基金资产数据】失败 ☆☆☆☆☆ \r\n";	
			throw new YssException("【获取基金代码出错.....】\t"+msg);
		}finally{
			dbl.closeResultSetFinal(rs1);
			dbl.closeResultSetFinal(rs);
		}
	}
	
	public void delData() throws YssException {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean bTrans = true;
		
		String delSql = "";
		conn = loadConnection();
		ResultSet rs = null;
		StringBuffer assetBuf = new StringBuffer();
		try {
			//start modified by yeshenghong  20130415 story3702
			for(int i=0;i<tmpPortCodes.length;i++){
				if(i>0){
					assetBuf.append(",");//modified by yeshenghong  20130415 story3702
				}
				if(tmpPortCodes[i].indexOf("-")>0)
				{
					assetBuf.append(dbl.sqlString((String)this.assetMap.get(tmpPortCodes[i].split("-")[1])));//modified by yeshenghong story3702 20130412
				}else
				{
					assetBuf.append(dbl.sqlString((String)this.assetMap.get(tmpPortCodes[i])));
				}
			}
			
			delSql = "delete from fundasset where financedate between "+dbl.sqlDate(sStartDate)+
	         " and "+ dbl.sqlDate(sEndDate)+" and fundcode in("+assetBuf.toString()+")";
			pst = openPreparedStatement(delSql);
			//end modified by yeshenghong  20130415 story3702
			pst.execute();
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
			
		} catch (Exception e) {
			msg = "☆☆☆☆☆ 导入【基金资产数据】失败 ☆☆☆☆☆ \r\n";	
			throw new YssException ("【数据中心——基金资产接口：删除基金资产数据出错......】\t"+msg);
		}finally{
			dbl.closeResultSetFinal(rs);
			closeStatementFinal(pst);
			endTransFinal(conn, bTrans);
		}
		
	}

	public String insertData() throws YssException {
		
		PreparedStatement pst = null,pst1=null;
		String setId = "";
		String fundCode = "";
		String query = "";
		ResultSet rs = null;
		Connection conn = null;
		String insertSql = "";
		boolean bTrans = true;
		java.util.Date StartDate = YssFun.parseDate(sStartDate);
		java.util.Date temDate = null;
		int  iDays = 0;
		int num = 0;
		double total_nav = 0; //累计单位净值 
		double unit_nav = 0; //单位净值
		double manage_fee = 0;//管理费
		//---add by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
		double customer_fee = 0;//IV006-客户服务费
		//---end by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
		boolean flag = false;
		msg = "☆☆☆☆☆  所选组合下没有【基金资产数据】，请核对后再重新导入 ☆☆☆☆☆ \r\n";//返回导入是否成功的提示
		
		//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02 ------
		String sBeginDate = "";
		String sFinishDate = "";
		//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  end ----------------------------------//
		String curGroup = "";
		String curPort = "";
		String preTb = pub.getPrefixTB();
		int id = 0;
		try{
			insertSql = " insert into fundasset (id,financedate,fundcode,Fundnav,Fundqty,unit_nav,management_fee,total_nav,fundvalue,fundmtkvalue,"+
			            " moneyamt,positionamt1,positionamt2,positionamt3,positionamt4,stkcost,stkmtkvalue,bondcost,bondmtkvalue,bond_buyback_asset "+
			            " ,non_bond_buyback_asset,othercost,othermtkvalue,purchaseamount,purchaseqty,redemptionamount,redemptionqty,paidsum,alrdyearn "+
			            " ,stkearn,bondearn,otherbondearn,floatearn,balance,flowstkcost,unflowstkcost,flowstkmtkvalue,unflowstkmtkvalue,national_bond_cost "+
			            " ,national_bond_value,non_national_bond_cost,non_national_bond_value,reconciledby,bouns,proclaim_bouns,auto_flag,imp_date, "+
			            " bond_buyback_asset_1,non_bond_buyback_asset_1,imp_by,system_date,reconcileddate,totalbenefit,sale_fee,fundhldcost,fundhldmktvalue)"+
			            " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			pst = openPreparedStatement(insertSql);
			
			conn =  loadConnection();
			//---start  add  yeshenghong to support mutiple groups 20130412
			for(int j=0;j<tmpPortCodes.length;j++){
				if(tmpPortCodes[j].indexOf("-")>0)
				{
					curGroup = tmpPortCodes[j].split("-")[0];
				    curPort = tmpPortCodes[j].split("-")[1];//add  yeshenghong to support mutiple groups 20130412
				    pub.setPrefixTB(curGroup);
				}
				else
				{
					curGroup = pub.getAssetGroupCode();
				    curPort = tmpPortCodes[j];//add  yeshenghong to support mutiple groups 20130412
				}
				query = " select fportcode,fdate,fcurcode,facctcode,facctname,facctattr,famount,FStandardMoneyMarketValue from "+
				//需求变更 modify by jiangshichao 2010.08.16  管理费填每日发生额，而不是累计金额 去掉科目代码220601
		        //pub.yssGetTableName("tb_rep_guessvalue")+" where fdate = ? and fportcode = ? and  (facctcode in ('9600', '9612', '220601')"+
				pub.yssGetTableName("tb_rep_guessvalue")+" where fdate = ? and fportcode = ? and  (facctcode in ('9600', '9612')"+
		        " or (facctcode = '9000' and fcurcode = ' ') or (facctcode = '8700' and fcurcode = ' ')" +
		        //--- 数据中心需将资产和负债合计数据存入到基金资产表中  add by jiangshichao 2010.11.04 ---------- 
		        //负债类 8801，资产类 8800
		        " or (facctcode = '8801' and fcurcode = ' ') or (facctcode = '8800' and fcurcode = ' '))";
				//--- 数据中心需将资产和负债合计数据存入到基金资产表中  add by jiangshichao 2010.11.04 end ------
				
				iDays = YssFun.dateDiff(YssFun.parseDate(sStartDate), YssFun.parseDate(sEndDate));
				
				fundCode = (String)this.assetMap.get(curPort); //资产代码
				setId = (String)fundCodeMap.get(fundCode);//套账号码
				
				//Iterator it = fundCodeMap.entrySet().iterator();//
//				//按套帐号循环
//				while(it.hasNext()){
//					Map.Entry entry= (Map.Entry)it.next();
//					setId = (String)entry.getKey();
//					fundCode = (String)entry.getValue();
				
				//按日循环
				for(int i=0;i<=iDays; i++){
					temDate = YssFun.addDay(StartDate, i);
					//需求变更 modify by jiangshichao 2010.08.16  管理费填每日发生额，而不是累计金额
					manage_fee = getManagerFee(fundCode,temDate);
					
					//---add by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
					customer_fee = getCustomerFee(fundCode, temDate);					
					//---end by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
					
					pst1 = dbl.openPreparedStatement(query);
					pst1.setDate(1, YssFun.toSqlDate(temDate));
					pst1.setString(2, setId);
					rs = pst1.executeQuery();
					int count =0;
					while(rs.next()){
						//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02----------//
						if(sBeginDate.equalsIgnoreCase("")||YssFun.dateDiff(YssFun.parseDate(sBeginDate), rs.getDate("FDate"))<0){
							sBeginDate = YssFun.formatDate(rs.getDate("FDate"));
						}
						id++;
						sFinishDate = YssFun.formatDate(rs.getDate("FDate"));
						//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    end ------------------------------------//
						if(count == 1){
							flag = true;
							pst.setLong(1, Long.parseLong(createNum(fundCode,temDate) + id));//modified byyeshenghong 防止主键异常
							pst.setDate(2, rs.getDate("fdate"));
						}
						
						if(rs.getString("facctcode").equalsIgnoreCase("9000")){
							pst.setDouble(4, rs.getDouble("FStandardMoneyMarketValue")); //基金净资产
						}else if(rs.getString("facctcode").equalsIgnoreCase("8700")){
							pst.setDouble(5, rs.getDouble("famount"));//实收份额
						}else if(rs.getString("facctcode").equalsIgnoreCase("9600")){
							pst.setDouble(6, rs.getDouble("FStandardMoneyMarketValue"));//单位净值
							unit_nav = rs.getDouble("FStandardMoneyMarketValue");
						}
						//需求变更 modify by jiangshichao 2010.08.16  管理费填每日发生额，而不是累计金额
//						else if(rs.getString("facctcode").equalsIgnoreCase("220601")){
//							pst.setDouble(7, rs.getDouble("FStandardMoneyMarketValue"));//管理费
//						}

						if(rs.getString("facctcode").equalsIgnoreCase("9612")){
							total_nav = rs.getDouble("FStandardMoneyMarketValue");//累计单位净值
						}
						//--- 数据中心需将资产和负债合计数据存入到基金资产表中  add by jiangshichao 2010.11.04 --------
						if(rs.getString("facctcode").equalsIgnoreCase("8801")){
							pst.setDouble(28, YssD.mul(rs.getDouble("FStandardMoneyMarketValue"),-1));//负债以正值显示
						}
						if(rs.getString("facctcode").equalsIgnoreCase("8800")){
							pst.setDouble(10, rs.getDouble("FStandardMoneyMarketValue"));
						}
						//--- 数据中心需将资产和负债合计数据存入到基金资产表中  add by jiangshichao 2010.11.04 end ----
						pst.setString(47, (new SimpleDateFormat("yyyyMMdd")).format(rs.getDate("fdate")));//zhouss 时间变更等于 FUNDASSET.FINANCEDATE
						count++;
					}
					dbl.closeResultSetFinal(rs);
					/*******************************************************************
					 * 累计单位净值是后来才添加的，而该字段又是not null ,
					 * 考虑查询不到累计单位净值的情况，所以如果没有的话就赋单位净值.
					 */
					//if(count == 4){
					if(count ==6){ //数据中心需将资产和负债合计数据存入到基金资产表中
						pst.setDouble(8,total_nav );
						pst.setString(3, fundCode);
						pst.setDouble(7, manage_fee);
						//---add by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
						pst.setDouble(54, customer_fee);
						//---end by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
						SetPstValue(pst);
						pst.addBatch();
					}else if(count>0){
						pst.setDouble(8, unit_nav);
						pst.setString(3, fundCode);
						pst.setDouble(7, manage_fee);
						//---add by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
						pst.setDouble(54, customer_fee);
						//---end by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
						SetPstValue(pst);
						pst.addBatch();
					}
				}
			}
			//---end  add  yeshenghong to support mutiple groups 20130412
			if(!curGroup.equals(""))
			{
				pub.setPrefixTB(preTb);
			}
			if(fundCodeMap.size()>0&& flag ){
				num = pst.executeBatch().length;
				conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
			}
			if(num>0){
				//--- MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02 ------//
				if (sBeginDate.equalsIgnoreCase(sFinishDate)) {
					msg = "★★★★★ 导入【"+sBeginDate+"日 基金资产数据】成功 ★★★★★ \r\n";
				} else {
					msg = "★★★★★ 导入【"+sBeginDate+" 至 "+sFinishDate+"日 基金资产数据】成功 ★★★★★ \r\n";
				}
				//--- MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    end -------------------------------//
			}
			return msg;
		}catch(Exception e){
			  msg = "☆☆☆☆☆ 导入【基金资产数据】失败 ☆☆☆☆☆ \r\n";	
			throw new YssException ("【数据中心——基金资产接口：插入基金资产数据出错......】\t"+msg);
		}finally{
			/*added by yeshenghong 2013-5-15 Story 3702 */
			pub.setPrefixTB(preTb);
			/*end by yeshenghong 2013-5-15 Story 3702 */
			dbl.closeResultSetFinal(rs);
			closeStatementFinal(pst);
			closeStatementFinal(pst1);
			endTransFinal(conn, bTrans);
		}
	}

	//自动生成Id号： 日期+基金代码
	private String createNum(String fundCode, java.util.Date temDate ){
		String sDate = YssFun.formatDate(temDate, "yyyyMMdd");
		/*added by yeshenghong 2013-5-15 Story 3702 */
		Pattern pattern = Pattern.compile("[0-9]*"); 
        if(pattern.matcher(sDate + fundCode).matches())
        {
        	return sDate + fundCode;
        }else
        {
        	return sDate + (System.currentTimeMillis() + "").toString().substring(8);
        }
        /*end by yeshenghong 2013-5-15 Story 3702 */
	}
	
	/*********************************************
	 * 2010.07.22  21：00  add by jiangshichao 
	 * @throws SQLException 
	 */
	private void SetPstValue(PreparedStatement pst) throws SQLException{
		String nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date());

		pst.setDouble(9, 0);
		//pst.setDouble(10, 0); 数据中心需将资产和负债合计数据存入到基金资产表中  add by jiangshichao 2010.11.04
		pst.setDouble(11, 0);
		pst.setDouble(12, 0);
		pst.setDouble(13, 0);
		pst.setDouble(14, 0);
		pst.setDouble(15, 0);
		pst.setDouble(16, 0);
		pst.setDouble(17, 0);
		pst.setDouble(18, 0);
		pst.setDouble(19, 0);
		pst.setDouble(20, 0);
		pst.setDouble(21, 0);
		pst.setDouble(22, 0);
		pst.setDouble(23, 0);
		pst.setDouble(24, 0);
		pst.setDouble(25, 0);
		pst.setDouble(26, 0);
		pst.setDouble(27, 0);
		//pst.setDouble(28, 0); 数据中心需将资产和负债合计数据存入到基金资产表中  add by jiangshichao 2010.11.04
		pst.setDouble(29, 0);
		pst.setDouble(30, 0);
		pst.setDouble(31, 0);
		pst.setDouble(32, 0);
		pst.setDouble(33, 0);
		pst.setDouble(34, 0);
		pst.setDouble(35, 0);
		pst.setDouble(36, 0);
		pst.setDouble(37, 0);
		pst.setDouble(38, 0);
		pst.setDouble(39, 0);
		pst.setDouble(40, 0);
		pst.setDouble(41, 0);
		pst.setDouble(42, 0);
		pst.setString(43, " ");//复核人,复核时间为 默认修改为null
		pst.setDouble(44, 0);
		pst.setDouble(45, 0);
		pst.setString(46, "A");//手工/自动标志 默认修改为A
		//pst.setString(47, nowTime.substring(0,4)+nowTime.substring(5,7)+nowTime.substring(8,10));
		pst.setDouble(48, 0);
		pst.setDouble(49, 0);
		pst.setString(50, pub.getUserName());
		pst.setString(51, nowTime);
		pst.setString(52, " ");//复核人,复核时间为 默认修改为null
		pst.setDouble(53, 0);
		//---edit by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
		//pst.setDouble(54, 0);
		//---end by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
		pst.setDouble(55, 0);
		pst.setDouble(56, 0);
	}

	
	/*************************************************************************
	 * 获取当日基金管理费用
	 * @param fundNo 基金代码
	 * @param date 日期
	 * @return
	 * @throws YssException
	 */
	private double getManagerFee(String fundNo,java.util.Date date)throws YssException{
		
		ResultSet rs = null;
		String sql = "";
		double fee = 0;
		try{
			// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
		/*	sql = " select sum(fportcurymoney) as fportcurymoney from " + pub.yssGetTableName("tb_data_investpayrec")+ 
			      " where fcheckstate=1 and fivpaycatcode = 'IV001' and ftransdate ="+dbl.sqlDate(date)+ 
			      " and FTsfTypeCode = '07' and FSubTsfTypeCode='07IV'"+//add by licai 20101124 BUG #469 数据中心基金资产表中的管理费应该取每天计提的管理费费用
			      " and fportcode = (select fportcode from "+pub.yssGetTableName("tb_para_portfolio")+
			      " where fassetcode="+dbl.sqlString(fundNo)+" and fcheckstate=1 and fstartdate =(select  max(fstartdate) from "+
			      pub.yssGetTableName("tb_para_portfolio")+" where fassetcode="+dbl.sqlString(fundNo)+" and fcheckstate=1))";
		 */
			sql = " select sum(fportcurymoney) as fportcurymoney from " + pub.yssGetTableName("tb_data_investpayrec")+ 
		      " where fcheckstate=1 and fivpaycatcode = 'IV001' and ftransdate ="+dbl.sqlDate(date)+ 
		      " and FTsfTypeCode = '07' and FSubTsfTypeCode='07IV'"+//add by licai 20101124 BUG #469 数据中心基金资产表中的管理费应该取每天计提的管理费费用
		      " and fportcode = (select fportcode from "+pub.yssGetTableName("tb_para_portfolio")+
		      " where fassetcode="+dbl.sqlString(fundNo)+" and fcheckstate=1 )";
	 
			
			//end by lidaolong 
			rs = dbl.openResultSet(sql);
			while(rs.next()){
				fee = rs.getDouble("fportcurymoney");
			}
			return fee;
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【基金资产数据】失败 ☆☆☆☆☆ \r\n";	
			throw new YssException("【获取当日管理费用出错.....】\t"+msg);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	/* 获取当日客户服务费（IV006）
	 * add by hongqingbing 2014-02-17 Story_15218_数据中心接口_基金资产表
	 * @param fundNo 基金代码
	 * @param date 日期
	 * @return
	 * @throws YssException
	 */
	private double getCustomerFee(String fundNo,java.util.Date date)throws YssException{
		
		ResultSet rs = null;
		String sql = "";
		double fee = 0;
		try{		
			sql = " select sum(fportcurymoney) as CustomerFee from " + pub.yssGetTableName("tb_data_investpayrec")+ 
		      " where fcheckstate=1 and fivpaycatcode = 'IV006' and ftransdate ="+dbl.sqlDate(date)+ 
		      " and FTsfTypeCode = '07' and FSubTsfTypeCode='07IV'"+
		      " and fportcode = (select fportcode from "+pub.yssGetTableName("tb_para_portfolio")+
		      " where fassetcode="+dbl.sqlString(fundNo)+" and fcheckstate=1 )";	 			
			rs = dbl.openResultSet(sql);
			while(rs.next()){
				fee = rs.getDouble("CustomerFee");
			}
			return fee;
		}catch(Exception e){				
			throw new YssException("【获取当日管理费用出错.....】\t" + e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	public String impData() throws YssException {
		initFundCodeMap ();//modified by yeshenghong story3702  20130415 
		assetMap = initAssetMap();
		delData();
		return insertData();
	}
	
}
