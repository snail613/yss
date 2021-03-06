package com.yss.main.operdeal.datainterface.dataCenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**************************************************************
 * 数据中心接口：财务估值表（QDII_FundFinanceValue）
 *  MS01541 QDV4赢时胜上海2010年08月4日01_AB  数据中心
 * @author jiangshichao
 * @date   2010.07.22
 */
public class ImpFundFinanceValueDeal extends BaseDataCenter {
    HashMap fundMap = null;
    private String msg = "";
    HashMap assetMap = null;
	public void delData() throws YssException {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean bTrans = true;
		ResultSet rs = null;
		String delSql = "";
		StringBuffer assetBuf = new StringBuffer();
		try {
			
			conn = loadConnection();
			
			//modified by yeshenghong story3702 20130412
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
			delSql = "delete from QDII_FundFinanceValue where FDate between "+dbl.sqlDate(sStartDate)+
	         " and "+ dbl.sqlDate(sEndDate)+" and FPortCode in(" + assetBuf.toString() + ")";
			
			pst = openPreparedStatement(delSql);
			pst.execute();
			//----------end  modified by yeshenghong story3702 20130415
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
		} catch (Exception e) {
			msg = "☆☆☆☆☆ 导入【财务估值数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException ("【数据中心——财务估值数据接口：删除财务估值数据报错！！！】\t"+msg);
		}finally{
			dbl.closeResultSetFinal(rs);
			closeStatementFinal(pst);
			endTransFinal(conn, bTrans);
		}
		
	}
	/*
	 * story3702 添加对多组合群的支持  20130515
	 * yeshenghong
	 * */
	public ResultSet getQDIIData(String portCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String setCodes = "";
		try{
			setCodes = getSetCode(portCode);
			strSql =" select FPortCode,FDate,FAcctCode,FCurCode,FAcctName,FAcctAttr,FAcctClass,FExchangeRate,FAmount,FCost,FStandardMoneyCost,FCostToNetRatio,"+
			        " FStandardMoneyCostToNetRatio,FMarketPrice,FOTPrice1,FOTPrice2,FOTPrice3,FMarketValue,FStandardMoneyMarketValue,FMarketValueToRatio,"+
			        " FStandardMoneyMarketValueToRat,FAppreciation,FStandardMoneyAppreciation,FMarketDescribe,FAcctLevel,FAcctDetail,FDesc from "+ 
			        pub.yssGetTableName("Tb_Rep_GuessValue")+" where fdate between "+dbl.sqlDate(sStartDate)+" and "+dbl.sqlDate(sEndDate) +
			        " and fportcode in ("+setCodes+") And facctcode <> 'D200' "+ //modify huangqirong 2013-07-24 bug #8750
			      //MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  add by jiangshichao 2010.09.02 -----//
			        " order by fdate ";
			      //MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  end --------------------------------//
				
			rs = dbl.openResultSet(strSql);
			return rs;
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【财务估值数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心接口 ——财务估值数据接口：获取财务估值数据出错......】\t"+msg);
		}

	}

	public String insertData( ) throws YssException {
		PreparedStatement pst = null;
		String insertSql = "";
		boolean flag = false;
		boolean bTrans = true;
		String nowTime = "";
		ResultSet rsSrc = null;
		msg =  "☆☆☆☆☆ 所选组合下没有【财务估值数据】，请核对后再重新导入 ☆☆☆☆☆ \r\n";//返回导入是否成功的提示;
		
		//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02 ------
		String sBeginDate = "";
		String sFinishDate = "";
		String strSql = "";
		//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功  end ----------------------------------//
		String curGroup = "";
		String curPort = "";
		String preTb = pub.getPrefixTB();
		try{
			insertSql = " insert into QDII_FundFinanceValue (FPortCode,FDate,FAcctCode,FCurCode,FAcctName,FAcctAttr,FAcctClass,FExchangeRate,FAmount, "+
            " FCost,FStandardMoneyCost,FCostToNetRatio,FStandardMoneyCostToNetRatio,FMarketPrice,"+
            " FOTPrice1,FOTPrice2,FOTPrice3,FMarketValue,FStandardMoneyMarketValue,FMarketValueToRatio,"+
            " FStdMoneyMktValueToRatio,FAppreciation,FStandardMoneyAppreciation,FMarketDescribe,FAcctLevel,"+
            " FAcctDetail,FDesc,FAuto_Flag,FImp_By,FImp_DateTime,FReview_By,FReview_DateTime) values"+
            " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
						sBeginDate = YssFun.formatDate(rsSrc.getDate("FDate"));//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02
					}
					nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date());
					//pst.setString(1,(String)fundMap.get(rsSrc.getString("FPortCode")));//财务估值表中的组合代码修改为基金代码
					pst.setString(1,(String)assetMap.get(curPort));//财务估值表中的组合代码修改为基金代码
					pst.setDate(2,rsSrc.getDate("FDate"));
					pst.setString(3,rsSrc.getString("FAcctCode"));
					pst.setString(4,rsSrc.getString("FCurCode"));
					pst.setString(5,rsSrc.getString("FAcctName"));
					pst.setString(6,rsSrc.getString("FAcctAttr"));
					pst.setString(7,rsSrc.getString("FAcctClass"));
					pst.setDouble(8,rsSrc.getDouble("FExchangeRate"));
					pst.setDouble(9,rsSrc.getDouble("FAmount"));
					
					//--- modify by jiangshichao 2010.08.16 负债类金额显示为正值
					if(rsSrc.getString("FAcctclass").equalsIgnoreCase("负债类")){
						pst.setDouble(10, YssD.mul(rsSrc.getDouble("FCost"), -1));
						pst.setDouble(11, YssD.mul(rsSrc.getDouble("FStandardMoneyCost"), -1));
					}else{
						pst.setDouble(10, rsSrc.getDouble("FCost"));
						pst.setDouble(11, rsSrc.getDouble("FStandardMoneyCost"));
					}
					
					pst.setDouble(12, rsSrc.getDouble("FCostToNetRatio"));
					pst.setDouble(13, rsSrc.getDouble("FStandardMoneyCostToNetRatio"));
					pst.setDouble(14, rsSrc.getDouble("FMarketPrice"));
					pst.setDouble(15, rsSrc.getDouble("FOTPrice1"));
					pst.setDouble(16, rsSrc.getDouble("FOTPrice2"));
					pst.setDouble(17, rsSrc.getDouble("FOTPrice3"));
					
					//--- modify by jiangshichao 2010.08.16 负债类金额显示为正值
					if(rsSrc.getString("FAcctclass").equalsIgnoreCase("负债类")){
					//MS01841 QDV4赢时胜(33上线测试)2010年10月12日01_B  modify by jiangshichao
						pst.setDouble(18, YssD.mul(rsSrc.getDouble("FMarketValue"), -1));
						pst.setDouble(19, YssD.mul(rsSrc.getDouble("FStandardMoneyMarketValue"), -1));
					}else{
						pst.setDouble(18, rsSrc.getDouble("FMarketValue"));
						pst.setDouble(19, rsSrc.getDouble("FStandardMoneyMarketValue"));
					}
					
					pst.setDouble(20, rsSrc.getDouble("FMarketValueToRatio"));
					pst.setDouble(21, rsSrc.getDouble("FStandardMoneyMarketValueToRat"));
					pst.setDouble(22, rsSrc.getDouble("FAppreciation"));
					pst.setDouble(23, rsSrc.getDouble("FStandardMoneyAppreciation"));
					pst.setString(24, rsSrc.getString("FMarketDescribe"));
					pst.setDouble(25, rsSrc.getDouble("FAcctLevel"));
					pst.setDouble(26, rsSrc.getDouble("FAcctDetail"));
					pst.setString(27, rsSrc.getString("FDesc"));
					pst.setString(28,"A");//手工/自动标志 默认修改为A
					pst.setString(29,pub.getUserName());
					pst.setString(30,nowTime);
					pst.setString(31,null);//复核人,复核时间为 默认修改为null
					pst.setString(32,null);//复核人,复核时间为 默认修改为null
					
					pst.addBatch();
					count++;
					sFinishDate = YssFun.formatDate(rsSrc.getDate("FDate"));//MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    add by jiangshichao 2010.09.02
				}
			}
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
					msg = "★★★★★ 导入【"+sBeginDate+"日 财务估值数据】成功 ★★★★★ \r\n";
				} else {
					msg = "★★★★★ 导入【"+sBeginDate+" 至 "+sFinishDate+"日 财务估值数据】成功 ★★★★★ \r\n";
				}
				//--- MS01631 QDV4赢时胜上海2010年08月23日01_AB 6.导出数据成功后，应提示某个日期的某张表导出成功    end -------------------------------//
			}
			return msg;
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【财务估值数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心接口 ——财务估值数据接口：插入财务估值数据出错......】\t"+msg);
		}finally{
			dbl.closeResultSetFinal(rsSrc);
			closeStatementFinal(pst);
			endTransFinal(con, bTrans);
		}	
	}

	/*
	 * story3702 添加对多组合群的支持  20130515
	 * yeshenghong
	 * */
	private String getSetCode(String portCode)throws YssException {
		ResultSet rs = null;
		StringBuffer buf = null;
		String setCodes = "";
		try{
			String syear = sEndDate.substring(0, 4);
			
			String query = " select a.fassetcode,b.fsetcode from "+
	           " (select fassetcode from " + pub.yssGetTableName("tb_para_portfolio") +" where fportcode in('"+ portCode + "') and fcheckstate=1 )a"+
	        " left join" +
	           " (select fsetcode,fsetid from lsetlist where fyear="+syear+" order by FSetCode desc) b"+
	        " on a.fassetcode = b.fsetid";
			
			rs = dbl.openResultSet(query);
			
			int count = 0;
			while(rs.next()){//codes modified by yeshenghong story3702 20130415
				if(count ==0 ){
					buf = new StringBuffer();
					fundMap = new HashMap();
				}
				buf.append("'"+rs.getString("fsetcode")+"'").append(",");
				if(!fundMap.containsKey(rs.getString("fassetcode")))
				{
					fundMap.put(rs.getString("fsetcode"),rs.getString("fassetcode"));
				}
			}
			
			if(buf.toString().length()>1){
				setCodes = buf.toString().substring(0,buf.toString().length()-1);
			}
			return setCodes;
		}catch(Exception e){
			msg = "☆☆☆☆☆ 导入【财务估值数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【获取基金代码出错......】\t"+msg);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	
	public String impData() throws YssException {
        assetMap = this.initAssetMap();
		delData();
		//rs = getQDIIData();
		return insertData();
		
	}
}
