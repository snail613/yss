package com.yss.main.operdeal.datainterface.dataCenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.yss.util.YssException;
import com.yss.util.YssFun;

/*****************************************************
 * 交易表（QDII_TradeInfo）
 *  MS01541 QDV4赢时胜上海2010年08月4日01_AB  数据中心
 * @author jiangshichao
 * @date   2010.07.22
 */
public class ImpTradeInfoDeal extends BaseDataCenter{

    private String msg = "";
    HashMap assetMap = null;
	public void delData() throws YssException {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean bTrans = true;
		String delSql = "";//modified by yeshenghong story3702 20130412
		StringBuffer assetBuf = new StringBuffer();
		try {
			conn = loadConnection();
			//modified by yeshenghong story3702 20130412 数据中心接口增加跨组合群导出功能
			for(int i=0;i<tmpPortCodes.length;i++){
				if(i>0){
					assetBuf.append(",");
				}
				if(tmpPortCodes[i].indexOf("-")>0)
				{
					assetBuf.append(dbl.sqlString((String)assetMap.get(tmpPortCodes[i].split("-")[1])));//modified by yeshenghong story3702 20130412
				}else
				{
					assetBuf.append(dbl.sqlString((String)assetMap.get(tmpPortCodes[i])));
				}
			}
//			query = "select fassetcode from "+pub.yssGetTableName("tb_para_portfolio")+" where fcheckstate=1  and fportcode in ("+sportCodes+")";
//			rs = dbl.openResultSet(query);
			
			
			delSql = "delete from QDII_TradeInfo where FBargainDate between "+dbl.sqlDate(sStartDate)+
	         " and "+ dbl.sqlDate(sEndDate)+" and FfundNO in (" + assetBuf.toString() + ")";
			pst = openPreparedStatement(delSql);
			//----------end  modified by yeshenghong story3702 20130412 数据中心接口增加跨组合群导出功能
//			while(rs.next()){
//				pst.setString(1, rs.getString("fassetcode"));
//				pst.addBatch();
//			}
			pst.execute();//modified by yeshenghong story3702 20130412
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
			
		} catch (Exception e) {
			msg = "☆☆☆☆☆ 导入【交易数据】失败 ☆☆☆☆☆ \r\n ";
			throw new YssException ("【数据中心 ——交易数据接口：删除交易数据报错！！！】\t"+msg);
		}finally{
			closeStatementFinal(pst);
			endTransFinal(conn, bTrans);
		}
		
	}

	/*
	 * modified by yeshenghong story3702 20130412 数据中心接口增加跨组合群导出功能
	 * */
	public ResultSet getQDIIData(String portCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		try{
			strSql =" select a.*,d.fcatcode , d.fexchangecode,d.fexchangename,d.fregioncode,d.fcountrycode ,d.fcountryname, d.fregionname,e.fbrokername,f.fassetcode"+
			        " from "+
			        //-------------------------------------------------------------------------------------------------------------------------------------------//
			             " (select FNum,FSecurityCode,FBrokerCode,FTradeTypeCode,FCashAccCode,FSeatCode,FStockholderCode,FRateDate,FBargainDate,FBargainTime,FSettleDate," +
			                " FSettleTime,FMatureDate,FMatureSettleDate,FPortCuryRate,FBaseCuryRate,FTradeAmount,FTradePrice,FTradeMoney,FAccruedinterest,FBailMoney,"+
			                " FFeeCode1,FTradeFee1,FFeeCode2,FTradeFee2,FFeeCode3,FTradeFee3,FFeeCode4,FTradeFee4,FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6,"+
			                " FFeeCode7,FTradeFee7,FFeeCode8,FTradeFee8,FTotalCost,FInvMgrCode,FPORTCODE "+
			            " from "+pub.yssGetTableName("tb_data_subtrade")+
			            " where fcheckstate=1 and FBargainDate between "+dbl.sqlDate(sStartDate)+" and "+dbl.sqlDate(sEndDate) + " and FPortCode in (" + dbl.sqlString(portCode) + "))a"+
			      //-------------------------------------------------------------------------------------------------------------------------------------------//
				  " left join "+
				        " (select b.fsecuritycode,b.fcatcode,c.* from "+
				             " (select fsecuritycode,fexchangecode,fcatcode from " + pub.yssGetTableName("tb_para_security")+" b1"+
				             " where fcheckstate=1 and exists (select * from (select distinct fsecuritycode from "+ pub.yssGetTableName("tb_data_subtrade")+
				             " where fcheckstate=1 and FBargainDate between "+dbl.sqlDate(sStartDate)+" and "+dbl.sqlDate(sEndDate) + " and FPortCode in ("+
				             dbl.sqlString(portCode) + ")) b2 where b1.fsecuritycode =b2.fsecuritycode))b"+
				             " left join "+
				             " (select c1.*, c2.fcountryname, c3.fregionname from " +
				                " (select fexchangecode,fexchangename,fregioncode,fcountrycode from tb_base_exchange where fcheckstate = 1) c1 " +
				                " left join " +
				                " (select fcountrycode, fcountryname from tb_base_country where fcheckstate = 1) c2"+
				                " on c1.fcountrycode = c2.fcountrycode " + 
				                " left join " +
				                " (select fregioncode, fregionname from tb_base_region where fcheckstate = 1) c3 " +
				                " on c1.fregioncode = c3.fregioncode ) c" +
				            " on b.fexchangecode = c.fexchangecode )d" +     
				  " on a.FSecurityCode = d.fsecuritycode "+
				  //-------------------------------------------------------------------------------------------------------------------------------------------//   
				  " left join " +
				        " (select fbrokercode,fbrokername from " + pub.yssGetTableName("tb_para_broker") + " where fcheckstate=1 )e" +
				  "  on a.fbrokercode = e.fbrokercode"+
				  //-------------------------------------------------------------------------------------------------------------------------------------------//   
				  " left join " + 
				        " (select fportcode,fassetcode from " + pub.yssGetTableName("tb_para_portfolio") + " where fcheckstate=1)f " +
				  " on a.fportcode = f.fportcode"+
				  " order by a.FBargainDate ";//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02
				
			
			
			rs = dbl.openResultSet(strSql);
			return rs;
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【交易数据】失败 ☆☆☆☆☆ \r\n ";
			throw new YssException("【数据中心 ——交易数据接口：获取交易数据出错......】\t"+msg);
		}

	}


	public String insertData() throws YssException {
		PreparedStatement pst = null;
		String insertSql = "";
		boolean flag = false;
		boolean bTrans = true;
		String nowTime = "";
		//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02 ------
		String sBeginDate = "";
		String sFinishDate = "";
		ResultSet rsSrc = null;
		//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  end ----------------------------------//
		msg =  "☆☆☆☆☆ 所选组合下没有【交易数据】，请核对后再重新导入 ☆☆☆☆☆ \r\n";//返回导入是否成功的提示;
		String curGroup = "";
		String curPort = "";
		String preTb = pub.getPrefixTB();
		int id = 0;
		try{
			insertSql = " insert into QDII_TradeInfo (FSecurityCode,FfundNO,FBrokerCode,FBrokerName,FEXCHANGECODE,FEXCHANGENAME,FCOUNTRYCODE,FCOUNTRYNAME,FREGIONCODE,FREGIONNAME, "+
			            " FSecTypeCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,FSeatCode,FStockholderCode,FRateDate,FBargainDate,FBargainTime,FSettleDate,FSettleTime,FMatureDate,"+
			            " FMatureSettleDate,FPortCuryRate,FBaseCuryRate,FTradeAmount,FTradePrice,FTradeMoney,FAccruedinterest,FFeeCode1,FTradeFee1,FFeeCode2,FTradeFee2,FFeeCode3," +
			            "FTradeFee3,FFeeCode4,FTradeFee4,FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6,FFeeCode7,FTradeFee7,FFeeCode8,FTradeFee8,FTotalCost,FAuto_Flag,FImp_By,FImp_DateTime" +
			            ",FReview_By,FReview_DateTime,Fnum) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			pst = openPreparedStatement(insertSql);
			int count =0;
			for(int i=0;i<tmpPortCodes.length;i++){
				if(tmpPortCodes[i].indexOf("-")>0)
				{
					curGroup = tmpPortCodes[i].split("-")[0];
				    curPort = tmpPortCodes[i].split("-")[1];//add  yeshenghong to support mutiple groups 20130412
				    pub.setPrefixTB(curGroup);
				}
				else
				{
					curGroup = pub.getAssetGroupCode();
				    curPort = tmpPortCodes[i];//add  yeshenghong to support mutiple groups 20130412
				}
				rsSrc = getQDIIData(curPort);
				while(rsSrc.next()){
					if(count ==0 ){
						flag = true;
						sBeginDate = YssFun.formatDate(rsSrc.getDate("FBargainDate"));//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02
					}
					nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date());
					pst.setString(1,rsSrc.getString("FSecurityCode"));
					pst.setString(2,rsSrc.getString("fassetcode"));
					pst.setString(3,rsSrc.getString("FBrokerCode"));
					pst.setString(4,rsSrc.getString("fbrokername"));
					pst.setString(5,rsSrc.getString("fexchangecode"));
					pst.setString(6,rsSrc.getString("fexchangename"));
					pst.setString(7,rsSrc.getString("fcountrycode"));
					pst.setString(8,rsSrc.getString("fcountryname"));
					pst.setString(9,rsSrc.getString("fregioncode"));
					pst.setString(10,rsSrc.getString("fregionname"));
					pst.setString(11,rsSrc.getString("fcatcode"));
					pst.setString(12,rsSrc.getString("FInvMgrCode"));
					pst.setString(13,rsSrc.getString("FTradeTypeCode"));
					pst.setString(14,rsSrc.getString("FCashAccCode"));
					pst.setString(15,rsSrc.getString("FSeatCode"));
					pst.setString(16,rsSrc.getString("FStockholderCode"));
					pst.setDate(17,rsSrc.getDate("FRateDate"));
					pst.setDate(18,rsSrc.getDate("FBargainDate"));
					pst.setString(19,rsSrc.getString("FBargainTime"));
					pst.setDate(20,rsSrc.getDate("FSettleDate"));
					pst.setString(21,rsSrc.getString("FSettleTime"));
					pst.setDate(22,rsSrc.getDate("FMatureDate"));
					pst.setDate(23,rsSrc.getDate("FMatureSettleDate"));
					pst.setDouble(24,rsSrc.getDouble("FPortCuryRate"));
					pst.setDouble(25,rsSrc.getDouble("FBaseCuryRate"));
					pst.setDouble(26,rsSrc.getDouble("FTradeAmount"));
					pst.setDouble(27,rsSrc.getDouble("FTradePrice"));
					pst.setDouble(28,rsSrc.getDouble("FTradeMoney"));
					pst.setDouble(29,rsSrc.getDouble("FAccruedinterest"));
					pst.setString(30,rsSrc.getString("FFeeCode1"));
					pst.setDouble(31,rsSrc.getDouble("FTradeFee1"));
					pst.setString(32,rsSrc.getString("FFeeCode2"));
					pst.setDouble(33,rsSrc.getDouble("FTradeFee2"));
					pst.setString(34,rsSrc.getString("FFeeCode3"));
					pst.setDouble(35,rsSrc.getDouble("FTradeFee3"));
					pst.setString(36,rsSrc.getString("FFeeCode4"));
					pst.setDouble(37,rsSrc.getDouble("FTradeFee4"));
					pst.setString(38,rsSrc.getString("FFeeCode5"));
					pst.setDouble(39,rsSrc.getDouble("FTradeFee5"));
					pst.setString(40,rsSrc.getString("FFeeCode6"));
					pst.setDouble(41,rsSrc.getDouble("FTradeFee6"));
					pst.setString(42,rsSrc.getString("FFeeCode7"));
					pst.setDouble(43,rsSrc.getDouble("FTradeFee7"));
					pst.setString(44,rsSrc.getString("FFeeCode8"));
					pst.setDouble(45,rsSrc.getDouble("FTradeFee8"));
					pst.setDouble(46,rsSrc.getDouble("FTotalCost"));
					pst.setString(47,"A");//手工/自动标志 默认修改为A
					pst.setString(48,pub.getUserName());
					pst.setString(49,nowTime);
					pst.setString(50,null);//复核人,复核时间为 默认修改为null
					pst.setString(51,null);//复核人,复核时间为 默认修改为null
					
					pst.setString(52,rsSrc.getString("Fnum"));
					count++;
					id++;
					pst.addBatch();
					sFinishDate = YssFun.formatDate(rsSrc.getDate("FBargainDate"));//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02
				}
			}
			if(!curGroup.equals(""))
			{
				pub.setPrefixTB(preTb);//add  yeshenghong to support mutiple groups 20130412
			}
			
			if(flag){
				pst.executeBatch();
				con.commit();
	            bTrans = false;
	            con.setAutoCommit(true);
			}
			if(count>0){
				//--- MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02 ------//
				if (sBeginDate.equalsIgnoreCase(sFinishDate)) {
					msg = "★★★★★ 导入【"+sBeginDate+"日交易数据】成功 ★★★★★ \r\n";
				} else {
					msg = "★★★★★ 导入【"+sBeginDate+" 至 "+sFinishDate+"日 交易数据】成功 ★★★★★ \r\n";
				}
				//--- MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    end -------------------------------//
			}
			return msg;
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【交易数据】失败 ☆☆☆☆☆ \r\n ";
			throw new YssException("【数据中心 ——交易数据接口：插入交易数据出错！！！】\t"+msg);
		}finally{
			dbl.closeResultSetFinal(rsSrc);
			closeStatementFinal(pst);
			endTransFinal(con, bTrans);
		}

	}

	
	public String impData() throws YssException {
		assetMap = initAssetMap();
		delData();
		return insertData( );//add  yeshenghong to support mutiple groups 20130412
	}

}
