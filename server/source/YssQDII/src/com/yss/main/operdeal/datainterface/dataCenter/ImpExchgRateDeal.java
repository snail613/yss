package com.yss.main.operdeal.datainterface.dataCenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.main.operdata.ExchangeRateBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/******************************************************
 * 数据中心接口：汇率表（QDII_ExchgRate）
 *  MS01541 QDV4赢时胜上海2010年08月4日01_AB  数据中心
 * @author jiangshichao
 * @date   2010.07.22
 *
 */
public class ImpExchgRateDeal extends BaseDataCenter {

    private String msg = "";
    HashMap assetMap = null;//add by jsc 20120608  fundNo存入的改为资产代码
	public ImpExchgRateDeal() {
		
	}
	
	
	public String impData() throws YssException {			
		
		ResultSet rs = null;
		HashMap persistentMap = new HashMap(); //保存已经持久化的公用汇率
		HashMap commonRateMap = null;
		HashMap tmpRateMap = null;
		HashMap portCountMap = new HashMap();
		String sBeginDate = "";
		String sFinishDate = "";
		String sKey = "";
		PreparedStatement pst = null,pst1=null;
		String insertSql = "",delSql="";
		String nowTime = "";
		int count = 0;
		boolean bTrans = true;
	    //insertData(rs);
		ExchangeRateBean rateBean = null;
		//String[] tmpPortCodes=null;
		StringBuffer buff = new StringBuffer();
		StringBuffer buff1 = null;
		String curGroup = "";
		String curPort = "";
		String preTb = pub.getPrefixTB();
		msg = "☆☆☆☆☆  所选日期没有【汇率数据】 ，请核对后再重新导入 ☆☆☆☆☆ \r\n";//返回导入是否成功的提示  FEXRATESRCCODE, FCURYCODE, FEXRATEDATE, FFUNDNO
	    try {
	    	
	    	assetMap = initAssetMap();
	    	delData();
			
			insertSql = " insert into QDII_ExchgRate (FExRateSrcCode,FCuryCode,FMarkCury,FExRateDate,FExRateTime,FfundNO,FAssetGroupCode,FExRate1,"+
                        " FDataSource,FAuto_Flag,FImp_By,FImp_DateTime,FReview_By,FReview_DateTime) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pst = openPreparedStatement(insertSql);
			
			delSql = " delete from QDII_ExchgRate where FEXRATESRCCODE=? and FCURYCODE=?  and FEXRATEDATE=?  and FFUNDNO=' ' ";
			pst1 = openPreparedStatement(delSql);
			
			rateBean = new ExchangeRateBean();
			rateBean.setYssPub(pub);
			commonRateMap = rateBean.getCommonRate(sStartDate, sEndDate);
			//1. 组合代码排序方式为降序
			//2. 判断组合代码是否为空，
			//     如果组合代码为空，保存公共汇率，同时也把公用汇率处理成所勾选的投资组合的专用汇率，
			//     如果组合代码不为空，保存当前专用汇率，同时公共汇率从commonRateMap获取。
			//3. 如果当前的公用汇率已经保存，continue;
			//add  yeshenghong to support mutiple groups 20130412 story 3702
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
				rateBean = new ExchangeRateBean();
				rateBean.setYssPub(pub);
				tmpRateMap = rateBean.getCommonRate(sStartDate, sEndDate);
				Iterator it = tmpRateMap.keySet().iterator();
				while(it.hasNext())
				{
					String key = (String)it.next();
					if(!commonRateMap.containsKey(key))
					{
						commonRateMap.put(key , tmpRateMap.get(key));//add  yeshenghong to support mutiple groups 20130412
					}
				}
				rs = getQDIIData(curPort);
		    	while (rs.next()){
		    		if (count==0){
		    			sBeginDate = YssFun.formatDate(rs.getDate("FEXRATEDATE"));
		    		}
		    		rateBean = null;
		    		sKey = rs.getString("FEXRATESRCCODE") + "\t" + rs.getString("FCURYCODE") + "\t" + rs.getDate("FEXRATEDATE") + "\t" + rs.getString("fassetcode");
		    		nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date());
		    		//1. 公用的汇率已经保存，就不再插入
		    		if(persistentMap.containsKey(sKey)&&rs.getString("Fportcode").trim().length()==0){
		    			if(portCountMap.get(sKey)==null)
		    			{
		    				continue;
		    			}
		    			buff1 = (StringBuffer)portCountMap.get(sKey);
		    			//for (int i=0;i<super.tmpPortCodes.length;i++){
		    				if(buff1.toString().indexOf(curPort)>=0){
		    					if(i==super.tmpPortCodes.length-1){
									portCountMap.remove(sKey);
									buff1=null;
								}
		    					continue;
		    				}
		    				if(i==super.tmpPortCodes.length-1){
								portCountMap.remove(sKey);
								buff1=null;
							}
							pst.setString(1,rs.getString("FExRateSrcCode"));
							pst.setString(2,rs.getString("FCuryCode"));
							pst.setString(3,rs.getString("FMarkCury"));
							pst.setDate(4,rs.getDate("FExRateDate"));
							pst.setString(5,rs.getString("FExRateTime"));
							//~~~ modify by jscf 20120608  由原先的组合代码调整为资产代码		start
							//pst.setString(6,super.tmpPortCodes[i]);	delete by jsc 
							pst.setString(6, (String)assetMap.get(curPort));//modified by yeshenghong 20130412 3702
							//~~~ modify by jscf 20120608  由原先的组合代码调整为资产代码		end
							pst.setString(7,curGroup);
							pst.setDouble(8,rs.getDouble("FExRate1"));
							pst.setString(9,rs.getString("FDataSource"));
							pst.setString(10,"A");//手工/自动标志 默认为A
							pst.setString(11,pub.getUserName());
							pst.setString(12,nowTime);
							pst.setString(13,null);  
							pst.setString(14,null);
							
							count++;
							pst.addBatch();
							
							
						//}
		    			if(!portCountMap.containsKey(sKey)){
		    				continue;
		    			}
		    			 
		    		}
		    		
		    		//2. 判断组合代码是否为空
		    		if(rs.getString("fportcode")!=null && rs.getString("fportcode").trim().length()!=0){
		    			//2.1 组合代码不为空，插入专用汇率，同时插入公共汇率
		    			
						pst.setString(1,rs.getString("FExRateSrcCode"));
						pst.setString(2,rs.getString("FCuryCode"));
						pst.setString(3,rs.getString("FMarkCury"));
						pst.setDate(4,rs.getDate("FExRateDate"));
						pst.setString(5,rs.getString("FExRateTime"));
						
						//~~~ modify by jscf 20120608  由原先的组合代码调整为资产代码		start
						//pst.setString(6,rs.getString("Fportcode"));		delete by jsc 
						pst.setString(6, (String)assetMap.get(rs.getString("Fportcode").trim())==null?" ":(String)assetMap.get(rs.getString("Fportcode").trim()));
						//~~~ modify by jscf 20120608  由原先的组合代码调整为资产代码		end
						pst.setString(7,curGroup);
						pst.setDouble(8,rs.getDouble("FExRate1"));
						pst.setString(9,rs.getString("FDataSource"));
						pst.setString(10,"A");//手工/自动标志 默认为A
						pst.setString(11,pub.getUserName());
						pst.setString(12,nowTime);
						//复核人,复核时间为 默认修改为null
						pst.setString(13,null);  
						pst.setString(14,null);
						
						count++;
						pst.addBatch();
						
						if(portCountMap.containsKey(sKey)){
							buff = (StringBuffer)portCountMap.get(sKey);
							buff.append("\t").append(rs.getString("Fportcode"));
							portCountMap.put(sKey, buff);
						}else{
							buff.append(rs.getString("Fportcode"));
							portCountMap.put(sKey, buff);
						}
						if(!persistentMap.containsKey(sKey)&&commonRateMap.get(sKey)!=null){
							rateBean = (ExchangeRateBean)commonRateMap.get(sKey);
							pst.setString(1,rateBean.getStrExRateSrcCode());
							pst.setString(2,rateBean.getStrCuryCode());
							pst.setString(3,rateBean.getStrMarkCuryCode());
							pst.setDate(4,YssFun.toSqlDate(rateBean.getStrExRateDate()));
							pst.setString(5,rateBean.getStrExRateTime());
							
								
							//~~~ modify by jscf 20120608  由原先的组合代码调整为资产代码		start
							//pst.setString(6,rateBean.getStrPortCode());		delete by jsc 
							pst.setString(6, (String)assetMap.get(rateBean.getStrPortCode().trim())==null?" ":(String)assetMap.get(rateBean.getStrPortCode().trim()));
							//~~~ modify by jscf 20120608  由原先的组合代码调整为资产代码		end
							pst.setString(7,curGroup);//modified by yeshenghong 20130412 3702
							pst.setDouble(8,rateBean.getStrExRate1());
							pst.setString(9,rateBean.getStrDataSource());
							pst.setString(10,"A");//手工/自动标志 默认为A
							pst.setString(11,pub.getUserName());
							pst.setString(12,nowTime);
							pst.setString(13,null);  
							pst.setString(14,null);
							count++;
							pst.addBatch();
							
							persistentMap.put(sKey, rateBean);//保存到持久化HashMap中
							pst1.setString(1,rateBean.getStrExRateSrcCode());
							pst1.setString(2,rateBean.getStrCuryCode());
							//pst1.setString(3,rateBean.getStrMarkCuryCode());
							pst1.setDate(3,YssFun.toSqlDate(rateBean.getStrExRateDate()));
							pst1.addBatch();
						}
						
		    		}else{
		    			//2.2 组合代码为空，插入公共汇率，同时把公共汇率处理成专用汇率
		    			
						pst.setString(1,rs.getString("FExRateSrcCode"));
						pst.setString(2,rs.getString("FCuryCode"));
						pst.setString(3,rs.getString("FMarkCury"));
						pst.setDate(4,rs.getDate("FExRateDate"));
						pst.setString(5,rs.getString("FExRateTime"));
						pst.setString(6,rs.getString("Fportcode"));			
						pst.setString(7,curGroup);//modified by yeshenghong 20130412 3702
						pst.setDouble(8,rs.getDouble("FExRate1"));
						pst.setString(9,rs.getString("FDataSource"));
						pst.setString(10,"A");//手工/自动标志 默认为A
						pst.setString(11,pub.getUserName());
						pst.setString(12,nowTime);
						//复核人,复核时间为 默认修改为null
						pst.setString(13,null);  
						pst.setString(14,null);
						
						count++;
						pst.addBatch();
						
						persistentMap.put(sKey, rateBean);//保存到持久化HashMap中
						pst1.setString(1,rs.getString("FExRateSrcCode"));
						pst1.setString(2,rs.getString("FCuryCode"));
						//pst1.setString(3,rs.getString("FMarkCury"));
						pst1.setDate(3,rs.getDate("FExRateDate"));
						pst1.addBatch();
						
						//for (int i=0;i<super.tmpPortCodes.length;i++){
							pst.setString(1,rs.getString("FExRateSrcCode"));
							pst.setString(2,rs.getString("FCuryCode"));
							pst.setString(3,rs.getString("FMarkCury"));
							pst.setDate(4,rs.getDate("FExRateDate"));
							pst.setString(5,rs.getString("FExRateTime"));
							//~~~ modify by jscf 20120608  由原先的组合代码调整为资产代码		start
							//pst.setString(6,super.tmpPortCodes[i]);	delete by jsc 
							pst.setString(6, (String)assetMap.get(curPort));//modified by yeshenghong 20130412 3702
							//~~~ modify by jscf 20120608  由原先的组合代码调整为资产代码		end		
							pst.setString(7,curGroup);
							pst.setDouble(8,rs.getDouble("FExRate1"));
							pst.setString(9,rs.getString("FDataSource"));
							pst.setString(10,"A");//手工/自动标志 默认为A
							pst.setString(11,pub.getUserName());
							pst.setString(12,nowTime);
							//复核人,复核时间为 默认修改为null
							pst.setString(13,null);  
							pst.setString(14,null);
							
							count++;
							pst.addBatch();
						//}
						
		    		}
		    		
		    		sFinishDate = YssFun.formatDate(rs.getDate("FEXRATEDATE"));
		    		
		    	}
		    	dbl.closeResultSetFinal(rs);
			}
			//----------end  yeshenghong to support mutiple groups 20130412 story 3702
			if(!curGroup.equals(""))
			{
				pub.setPrefixTB(preTb);
			}
	    	
    	    if(count>0){
				//--- MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02 ------//
				if (sBeginDate.equalsIgnoreCase(sFinishDate)) {
					msg = "★★★★★ 导入【"+sBeginDate+"日 汇率数据】成功 ★★★★★ \r\n";
				} else {
					msg = "★★★★★ 导入【"+sBeginDate+" 至 "+sFinishDate+"日 汇率数据】成功 ★★★★★ \r\n";
				}
				//--- MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    end -------------------------------//
				pst1.executeBatch();
				pst.executeBatch();
				con.commit();
	            bTrans = false;
	            con.setAutoCommit(true);
			   }

				   return msg;
	        	       
	    } catch (Exception e) {
			msg = "☆☆☆☆☆ 导入【汇率数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心——汇率数据接口：获取汇率数据出错！！！】\t"+msg);
		}finally{
			dbl.closeResultSetFinal(rs);
			closeStatementFinal(pst);
			closeStatementFinal(pst1);
			endTransFinal(con, bTrans);
			assetMap.clear();
		}

	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	public ResultSet getQDIIData(String portCode)throws YssException{
		
		String strSql = "";
		ResultSet rs = null;
		try{		
			strSql = " select a.*,b.fassetcode "+
			         " from "+  
				        " (select Fportcode,FExRateSrcCode,FCuryCode,FMarkCury,FExRateDate,FExRateTime,FExRate1,FDataSource" +
				        " from "+ pub.yssGetTableName("tb_data_exchangeRate") +
				        " where fcheckstate=1  and fexratedate between "+dbl.sqlDate(sStartDate)+ " and "+ dbl.sqlDate(sEndDate)+
				        " and fportcode in ('"+portCode+"',' ')"+" )a "+
			         " left join "+ 
			            " (select fportcode,fassetcode "+
			            " from "+ pub.yssGetTableName("tb_para_portfolio") +
			            " where fcheckstate=1)b"+
			         //MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  add by jiangshichao 2010.09.02 -----//
			         " on a.Fportcode = b.fportcode order by FExRateDate,a.fportcode desc "; //查询结果按日期升序来排序
			         //MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  end --------------------------------//
			rs = dbl.openResultSet(strSql);					
			return rs;			
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【汇率数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心——汇率数据接口：获取汇率数据出错！！！】\t"+msg);
		}
	}
	
	
	
	
	
	public void delData()throws YssException{
		Connection conn = null;
		PreparedStatement pst = null;
		boolean bTrans = true;
		String strSql = "";
		StringBuffer assetBuf = new StringBuffer();
		try{
			conn = loadConnection();
			
			//String[] tmpPorts = tmpPortCodes.split(",");
			//modified by yeshenghong story3702 20130412  story 3702
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
			//end by yeshenghong story3702 20130412 story 3702
			
			strSql = " delete from QDII_ExchgRate where FExRateDate between "+dbl.sqlDate(sStartDate)+
			         " and "+dbl.sqlDate(sEndDate)+" and FFUNDNO in("+assetBuf.toString()+")";//删除选择的组合和组合代码为空的汇率 add by jiangshichao 
			pst = openPreparedStatement(strSql);
			pst.execute();
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);     
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【汇率数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心——汇率数据接口：删除汇率数据出错！！】\t"+msg);
		}finally{
			endTransFinal(conn, bTrans);
			closeStatementFinal(pst);
		}
	}

	public String insertData(ResultSet rsSrc) throws YssException {
		PreparedStatement pst = null;
		String insertSql = "";
		boolean flag = false;//用于判断返回的结果集是否为空，如果为空的时候执行插入会报错
		boolean bTrans = true;
		String nowTime = "";
		//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02 ------
		String sBeginDate = "";
		String sFinishDate = "";
		//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  end ----------------------------------//
		msg = "☆☆☆☆☆  所选日期没有【汇率数据】 ，请核对后再重新导入 ☆☆☆☆☆ \r\n";//返回导入是否成功的提示
		
		try{	

			insertSql = " insert into QDII_ExchgRate (FExRateSrcCode,FCuryCode,FMarkCury,FExRateDate,FExRateTime,FfundNO,FAssetGroupCode,FExRate1,"+
			            " FDataSource,FAuto_Flag,FImp_By,FImp_DateTime,FReview_By,FReview_DateTime) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			pst = openPreparedStatement(insertSql);
			
			int count = 0;
			while(rsSrc.next()){
				if(count ==0){
					flag = true;
					
					sBeginDate= YssFun.formatDate(rsSrc.getDate("FExRateDate"));//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02
				}
				nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date());
				pst.setString(1,rsSrc.getString("FExRateSrcCode"));
				pst.setString(2,rsSrc.getString("FCuryCode"));
				pst.setString(3,rsSrc.getString("FMarkCury"));
				pst.setDate(4,rsSrc.getDate("FExRateDate"));
				pst.setString(5,rsSrc.getString("FExRateTime"));
				//pst.setString(6,rsSrc.getString("fassetcode")==null?" ":rsSrc.getString("fassetcode"));
				pst.setString(6,rsSrc.getString("Fportcode"));			
				pst.setString(7,pub.getAssetGroupCode());
				pst.setDouble(8,rsSrc.getDouble("FExRate1"));
				pst.setString(9,rsSrc.getString("FDataSource"));
				pst.setString(10,"A");//手工/自动标志 默认为A
				pst.setString(11,pub.getUserName());
				pst.setString(12,nowTime);
				//复核人,复核时间为 默认修改为null
				pst.setString(13,null);  
				pst.setString(14,null);
				
				count++;
				pst.addBatch();
				
				sFinishDate = YssFun.formatDate(rsSrc.getDate("FExRateDate"));//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02
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
					msg = "★★★★★ 导入【"+sBeginDate+"日 汇率数据】成功 ★★★★★ \r\n";
				} else {
					msg = "★★★★★ 导入【"+sBeginDate+" 至 "+sFinishDate+"日 汇率数据】成功 ★★★★★ \r\n";
				}
				//--- MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    end -------------------------------//
			   }

			   return msg;
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【汇率数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心——汇率数据接口：导入汇率数据出错！！！】\t"+msg);
		}finally{
			dbl.closeResultSetFinal(rsSrc);
			closeStatementFinal(pst);
			endTransFinal(con, bTrans);
			
		}
		
	}
	
}

