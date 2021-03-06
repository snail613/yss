package com.yss.main.operdeal.datainterface.dataCenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.yss.util.YssException;
import com.yss.util.YssFun;

/**********************************************************
 * 数据中心接口： 基金净值表（QDII_FundNetValue） 
 *  MS01541 QDV4赢时胜上海2010年08月4日01_AB  数据中心
 * @author jiangshichao
 * @date   2010.07.22
 */
public class ImpFundNetValueDeal extends BaseDataCenter {
   
	private String msg = "";
	public ImpFundNetValueDeal() throws YssException{
		
		con = loadConnection();
	}
	
	HashMap assetMap = null;
	
	/*
	 * story3702 添加对多组合群的支持  20130515  yeshenghong
	 * */
	public  ResultSet getQDIIData(String portCode)throws YssException{
		String query = "";
		ResultSet rs = null;
		try{
			query = " select a.*,d.*,e.fassetcode from "+
	        " (select fordercode,FNAVDate,FPortCode,FKeyCode,FKeyName,FReTypeCode,FCuryCode,FPrice,FOTPrice1,FOTPrice2,FOTPrice3,"+
	        " FSEDOLCode,FISINCode,FSParAmt,FBaseCuryRate,FPortCuryRate,FCost,FPortCost,FMarketValue,FPortMarketValue,"+
	        " FMVValue,FPortMVValue,FFXValue,FPortMarketValueRatio,FUnitCost,FPortUnitCost  "+
	        " from " + pub.yssGetTableName("tb_data_navdata")+                      //modified by yeshenghong  story3702 数据中心接口增加跨组合群导出功能  20130413 
	        " where fnavdate between "+dbl.sqlDate(sStartDate)+" and "+dbl.sqlDate(sEndDate) + " and FPortCode in ('"+portCode+"') ) a"+
	        " left join "+									                        //end by yeshenghong  story3702 20130413
	        //-----------------------------------------------------------------------------------------------------------------------------//
	        " ( select b.fsecuritycode,b.fexchangecode,c.fregioncode,c.fregionname,c.fexchangename,c.fcountrycode,c.fcountryname from " +
	            " (select fsecuritycode,fexchangecode from " + pub.yssGetTableName("tb_para_security") + " b1 where fcheckstate=1  and exists (select * from (select distinct fkeycode"+
	            " from "+pub.yssGetTableName("tb_data_navdata")+" where fretypecode='Security' and fdetail= 0 and fnavdate between "+dbl.sqlDate(sStartDate)+
	            " and "+dbl.sqlDate(sEndDate) +" and FPortCode in ('"+portCode+"'))b2 where b1.fsecuritycode =b2.fkeycode))b "+
	            " left join" +
	            " (select c1.*,c2.fcountryname,c3.fregionname from "+
	                  " (select fexchangecode,fexchangename,fregioncode,fcountrycode from tb_base_exchange where fcheckstate=1)c1"+
	                  " left join "+
	                  " (select fcountrycode,fcountryname from tb_base_country  where fcheckstate=1) c2  on c1.fcountrycode = c2.fcountrycode "+
	                  " left join "+
	                  " (select fregioncode,fregionname from tb_base_region where fcheckstate=1)c3 on c1.fregioncode = c3.fregioncode)c"+
	            " on b.fexchangecode = c.fexchangecode )d"+
	       //-----------------------------------------------------------------------------------------------------------------------------// 
	        " on a.FKeyCode = d.fsecuritycode"+
	       //-----------------------------------------------------------------------------------------------------------------------------// 
	        " left join "+
	        " (select fportcode,fassetcode from " + pub.yssGetTableName("tb_para_portfolio") + " where fcheckstate=1 ) e on a.fportcode = e.fportcode" +
	      //MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  add by jiangshichao 2010.09.02 -----//
	        " order by fnavdate ";
	      //MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  end --------------------------------//		
				
			rs = dbl.openResultSet(query);
			return rs;
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【基金净值数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心接口 ——基金净值接口：获取基金净值数据出错......】\t"+msg);
		}
}
	

	public void delData()throws YssException{
		Connection conn = null;
		PreparedStatement pst = null;
		boolean bTrans = true;
		String delSql = "";
		ResultSet rs = null;
		StringBuffer assetBuf = new StringBuffer();
		try {
            
//			query = "select fassetcode from "+pub.yssGetTableName("tb_para_portfolio")+" where fcheckstate=1  and fportcode in ("+sportCodes+")";
//			rs = dbl.openResultSet(query);
//			
			conn = loadConnection();
			
			for(int i=0;i<tmpPortCodes.length;i++){
				if(i>0){
					assetBuf.append(",");
				}
				if(tmpPortCodes[i].indexOf("-")>0)
				{
					assetBuf.append(dbl.sqlString((String)assetMap.get(tmpPortCodes[i].split("-")[1])));//modified by yeshenghong story3702 20130412  数据中心接口增加跨组合群导出功能
				}else
				{
					assetBuf.append(dbl.sqlString((String)assetMap.get(tmpPortCodes[i])));
				}
			}
			delSql = "delete from QDII_FundNetValue where FNAVDate between "+dbl.sqlDate(sStartDate)+
	         " and "+ dbl.sqlDate(sEndDate)+" and FfundNO in (" + assetBuf.toString() + ")";//modified by yeshenghong story3702 20130415 数据中心接口增加跨组合群导出功能
			pst = openPreparedStatement(delSql);
			
//			while(rs.next()){
//				pst.setString(1, rs.getString("fassetcode"));
//				pst.addBatch();
//			}
			pst.execute();
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
			
		} catch (Exception e) {
			msg = "☆☆☆☆☆ 导入【基金净值数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心接口 ——基金净值接口：删除基金净值数据出错......】\t"+msg);
		}finally{
			dbl.closeResultSetFinal(rs);
			closeStatementFinal(pst);
			endTransFinal(conn, bTrans);
		}
		
		
	}
	
	public String insertData( ) throws YssException{
		ResultSet rsSrc = null;
		PreparedStatement pst = null;
		Connection con = null;
		String insertSql = "";
		boolean flag = false;
		boolean bTrans = true;
		String nowTime = "";
	    msg =  "☆☆☆☆☆ 所选组合下没有【基金净值数据】，请核对后再重新导入 ☆☆☆☆☆ \r\n";//返回导入是否成功的提示;
	    
	    //MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02 ------
		String sBeginDate = "";
		String sFinishDate = "";
		//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  end ----------------------------------//
		String curGroup = "";
		String curPort = "";
		String preTb = pub.getPrefixTB();
		try{
			con =  loadConnection();
			insertSql = " insert into QDII_FundNetValue (FNAVDate,FfundNO,FKeyCode,FKeyName,FReTypeCode,FCuryCode,FEXCHANGECODE,FEXCHANGENAME,FCOUNTRYCODE, "+
			            " FCOUNTRYNAME,FREGIONCODE,FREGIONNAME,FPrice,FOTPrice1,FOTPrice2,FOTPrice3,FSEDOLCode,FISINCode,FSParAmt,"+
			            " FBaseCuryRate,FPortCuryRate,FCost,FPortCost,FMarketValue,FPortMarketValue,FMVValue,FPortMVValue,FFXValue,"+
			            " FPortMarketValueRatio,FUnitCost,FPorUnitCost,FAuto_Flag,FImp_By,FImp_DateTime,FReview_By,FReview_DateTime,fordercode) values "+
			            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			pst = openPreparedStatement(insertSql);
			int count =0;
			
			//add  yeshenghong to support mutiple groups 20130412 story 3702 数据中心接口增加跨组合群导出功能
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
				rsSrc = getQDIIData(curPort);//add  yeshenghong to support mutiple groups 20130412
				while(rsSrc.next()){
					if(count ==0 ){
						flag = true;
						sBeginDate= YssFun.formatDate(rsSrc.getDate("FNAVDate"));//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02
					}
					nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date());
					pst.setDate(1,rsSrc.getDate("FNAVDate"));
					pst.setString(2,rsSrc.getString("fassetcode"));
					pst.setString(3,rsSrc.getString("FKeyCode"));
					pst.setString(4,rsSrc.getString("FKeyName"));
					pst.setString(5,rsSrc.getString("FReTypeCode"));
					pst.setString(6,rsSrc.getString("FCuryCode"));
					pst.setString(7,rsSrc.getString("fexchangecode"));
					pst.setString(8,rsSrc.getString("fexchangename"));
					pst.setString(9,rsSrc.getString("fcountrycode")==null?" ":rsSrc.getString("fcountrycode"));
					pst.setString(10,rsSrc.getString("fcountryname")==null?" ":rsSrc.getString("fcountryname"));
					pst.setString(11,rsSrc.getString("fregioncode")==null?" ":rsSrc.getString("fregioncode"));
					pst.setString(12,rsSrc.getString("fregionname")==null?" ":rsSrc.getString("fregionname"));
					pst.setDouble(13, rsSrc.getDouble("FPrice"));
					pst.setDouble(14, rsSrc.getDouble("FOTPrice1"));
					pst.setDouble(15, rsSrc.getDouble("FOTPrice2"));
					pst.setDouble(16, rsSrc.getDouble("FOTPrice3"));
					pst.setString(17, rsSrc.getString("FSEDOLCode"));
					pst.setString(18, rsSrc.getString("FISINCode"));
					pst.setDouble(19, rsSrc.getDouble("FSParAmt"));
					pst.setDouble(20, rsSrc.getDouble("FBaseCuryRate"));
					pst.setDouble(21, rsSrc.getDouble("FPortCuryRate"));
					pst.setDouble(22, rsSrc.getDouble("FCost"));
					pst.setDouble(23, rsSrc.getDouble("FPortCost"));
					pst.setDouble(24, rsSrc.getDouble("FMarketValue"));
					pst.setDouble(25, rsSrc.getDouble("FPortMarketValue"));
					pst.setDouble(26, rsSrc.getDouble("FMVValue"));
					pst.setDouble(27, rsSrc.getDouble("FPortMVValue"));
					pst.setDouble(28, rsSrc.getDouble("FFXValue"));
					pst.setDouble(29, rsSrc.getDouble("FPortMarketValueRatio"));
					pst.setDouble(30, rsSrc.getDouble("FUnitCost"));
					pst.setDouble(31, rsSrc.getDouble("FPortUnitCost"));
					pst.setString(32,"A");//手工/自动标志 默认修改为A
					pst.setString(33,pub.getUserName());
					pst.setString(34,nowTime);
					pst.setString(35,null);//复核人,复核时间为 默认修改为null
					pst.setString(36,null);//复核人,复核时间为 默认修改为null
					pst.setString(37,rsSrc.getString("fordercode"));
					pst.addBatch();
					count++;
					sFinishDate = YssFun.formatDate(rsSrc.getDate("FNAVDate"));//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02
				}
			}
			//------end  yeshenghong to support mutiple groups 20130412 story 3702
			if(!curGroup.equals(""))
			{
				pub.setPrefixTB(preTb);
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
					msg = "★★★★★ 导入【"+sBeginDate+"日 基金净值数据】成功 ★★★★★ \r\n";
				} else {
					msg = "★★★★★ 导入【"+sBeginDate+" 至 "+sFinishDate+"日 基金净值数据】成功 ★★★★★ \r\n";
				}
				//--- MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    end -------------------------------//
			}
			return msg;
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【基金净值数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心 ——基金净值接口：插入基金净值数据出错......】\t"+msg);
		}finally{
			/*added by yeshenghong 2013-5-15 Story 3702 */
			pub.setPrefixTB(preTb);
			/*end by yeshenghong 2013-5-15 Story 3702 */
			dbl.closeResultSetFinal(rsSrc);
			closeStatementFinal(pst);
			endTransFinal(con, bTrans);
		}
		
	}


	public String impData() throws YssException {
        assetMap = this.initAssetMap();
        /*added by yeshenghong 2013-5-15 Story 3702 数据中心接口增加跨组合群导出功能*/
        delData();//add  yeshenghong to support mutiple groups 20130412
		/*end by yeshenghong 2013-5-15 Story 3702 */
		
		return insertData();
	}
}
