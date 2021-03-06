package com.yss.main.operdeal.datainterface.etf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**shashijie 2012-06-18 开发华夏ETF联接基金时修改此类 */
public class ETFStockListBean extends DataBase {
	
    public ETFStockListBean() {
    	
    }
    
    /** 导入数据的入口方法 */
    public void inertData() throws YssException {
    	//判断是否是ETF组合或ETF联接组合
    	if (isETFPortCode(this.sPort)) {
    		//获取数据集合
    		List subBeanList = getBeanList(this.sDate,this.sPort);
    		
    		//存入数据
    		insertSubBeanList(subBeanList,this.sDate,this.sPort);
    		
    		//获取现金差额数据集合
    		String ETF_Difference = getETF_Difference();
    		
    		//存入数据
    		insertETF_DifferenceList(ETF_Difference);
		}
    }

	/**shashijie 2012-6-18 STORY 2727 */
	private void insertETF_DifferenceList(String eTFDifference) throws YssException {
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			//先删除
			String strSql = getDeleteDif(eTFDifference);
			dbl.executeSql(strSql);
			
			strSql = getInsertDif();//新增SQL
			ps = conn.prepareStatement(strSql);
			//赋值
			setPreparedStatement(ps,eTFDifference);
			ps.executeUpdate();
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("保存数据出错!",e);
		} finally {
			dbl.closeStatementFinal(ps);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**shashijie 2012-6-18 STORY 2727 */
	private void setPreparedStatement(PreparedStatement ps, String eTFDifference) throws Exception{
		ps.setString(1, getStocklistfileValue(eTFDifference,2,","));//证券代码
		String dDate = getStocklistfileValue(eTFDifference,0,",");
		ps.setDate(2, YssFun.toSqlDate(dDate));//交易日期
		String FCashBal = getStocklistfileValue(eTFDifference,1,",").equals("")?
				"0" : getStocklistfileValue(eTFDifference,1,",");
		ps.setDouble(3, Double.valueOf(FCashBal));//现金差额
	}

	/**shashijie 2012-6-18 STORY 2727 */
	private String getInsertDif() {
		String query = " insert into "+
			pub.yssGetTableName("Tb_ETF_Difference")+
			"(" +
			" FSecurityCode ,"+//证券代码
			" FBargainDate ,"+//交易日期
			" FCashBal "+//现金差额
			
			")"+
			" Values ( " +
			" ?,?,?"+
			" ) ";
		return query;
	}

	/**shashijie 2012-6-18 STORY 2727 */
	private String getDeleteDif(String Stocklistfile) {
		String query = 
			" delete From "+pub.yssGetTableName("Tb_ETF_Difference")+
	        " where FBargainDate = "+dbl.sqlDate(getStocklistfileValue(Stocklistfile, 0, ","));
		return query;
	}

	/**shashijie 2012-6-18 STORY 2727 获取现金差额与日期*/
	private String getETF_Difference() throws YssException {
		ResultSet rs = null;
		String Date = "";//现金差额日期
		String FCashBal = "";//现金差额
		String FundID = "";//目标ETF代码(二级市场代码)
		try {
			String query = "Select * From (Select A1.Stocklistfile," +
					" Substr(A1.Stocklistfile, 1, Instr(A1.Stocklistfile, '=', 1, 1) - 1) As FSecurityCode," +
					" Substr(A1.Stocklistfile,Instr(A1.Stocklistfile, '=', 1, 1) + 1) As val "+
					" From Tmp_Etf_Stocklist A1 Order By A1.Stocklistfile) a" +
					" Where a.Fsecuritycode In ('PreTradingDay','CashComponent','FundID')";
	        rs = dbl.openResultSet(query);
	        while (rs.next()) {
	        	if (rs.getString("FSecurityCode").equals("PreTradingDay")) {//日期
	        		Date = YssFun.left(rs.getString("val").trim(), 4)
						+ "-"
						+ YssFun.mid(rs.getString("val").trim(), 4, 2)
						+ "-"
						+ YssFun.right(rs.getString("val").trim(), 2);
				}else if (rs.getString("FSecurityCode").equals("CashComponent")) {//现金差额
					FCashBal = rs.getString("val");
				}else if (rs.getString("FSecurityCode").equals("FundID")) {//目标ETF代码(二级市场代码)
					//edit by songjie 2012.07.27 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
					FundID = rs.getString("val") + " CS";
				}
	        }
		} catch (Exception e) {
			throw new YssException("查询现金差额出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return Date + ","+FCashBal+","+FundID;
	}

	/**shashijie 2012-6-18 STORY 2727 */
	private void insertSubBeanList(List subBeanList, Date sDate, String sPort) throws YssException {
		if (subBeanList.isEmpty()) {
			return;
		}
		
		Connection conn = dbl.loadConnection(); // 新建连接
        boolean bTrans = true;//事务控制标识
        ResultSet rs = null;//结果集声明
        PreparedStatement ps = null; // 声明PreparedStatement
		
        try {
        	conn.setAutoCommit(false);
            // 1.删除股票篮表Tb_ETF_StockList相关导入日期和组合代码的数据
            String delete = getDelete(sDate,sPort);
            dbl.executeSql(delete);
            
            String strSql = getInsert();//新增SQL
			ps = conn.prepareStatement(strSql);
			//批量增加
			for (int i = 0; i < subBeanList.size(); i++) {
				Map<String, Object> map = (Map<String, Object>)subBeanList.get(i);
				//赋值
				setPreparedStatement(ps,map);
				ps.executeUpdate();
			}
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("保存股票蓝出错!",e);
		} finally {
			dbl.endTransFinal(conn,bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(ps);
		}
	}

	/**shashijie 2012-6-18 STORY 2727 */
	private void setPreparedStatement(PreparedStatement ps,
			Map<String, Object> map) throws Exception {
		if (map == null) {
			throw new YssException("赋值数据表对象出错!");
		}
		ps.setString(1,map.get("FPortCode").toString());//组合代码
		ps.setString(2,map.get("FSecurityCode").toString());//证券代码
		ps.setDouble(3,Double.valueOf(map.get("FAmount").toString()));//证券数量
		ps.setString(4,map.get("FReplaceMark").toString());//替代标志
		ps.setDouble(5,Double.valueOf(map.get("FPremiumScale").toString()));//溢价比例
		ps.setDouble(6,Double.valueOf(map.get("FReplaceMoney").toString()));//替代金额
		ps.setDouble(7,Double.valueOf(map.get("FTotalMoney").toString()));//总金额
		ps.setInt(8,this.checkState.equalsIgnoreCase("true")?1:0);//审核状态审核状态
		ps.setString(9,pub.getUserCode());//创建人、修改人
        ps.setString(10,YssFun.formatDate(new Date(), "yyyy-MM-dd"));//创建、修改时间
		ps.setString(11,pub.getUserCode());//复核人
		ps.setString(12,YssFun.formatDate(new Date(), "yyyy-MM-dd"));//复核时间
		ps.setString(13,map.get("FDesc").toString());//描述
		ps.setDate(14,YssFun.toSqlDate(map.get("FDate").toString()));//导入日期
		ps.setString(15,map.get("FISINCode").toString());//国际产品代码
		ps.setString(16,map.get("FListedMarket").toString());//挂牌市场
		ps.setDouble(17,Double.valueOf(map.get("FSHReplaceMoney").toString()));//赎回替代金额

	}

	/**shashijie 2012-6-18 STORY 2727 */
	private String getInsert() {
		String value = " insert into "+pub.yssGetTableName("Tb_ETF_StockList")+
			" ( " +
			" FPortCode," +//组合代码
			" FSecurityCode," +//证券代码
			" FAmount," +//证券数量
			" FReplaceMark," +//替代标志
			" FPremiumScale," +//溢价比例
			" FReplaceMoney," +//替代金额
			" FTotalMoney," +//总金额
			" FCheckState," +//审核状态
			" FCreator," +//创建人、修改人
			" FCreateTime," +//创建、修改时间
			" FCheckUser," +//复核人
			" FCheckTime," +//复核时间
			" FDesc," +//描述
			" FDate," +//导入日期
			" FISINCode," +//国际产品代码
			" FListedMarket," +//挂牌市场
			" FSHReplaceMoney" +//赎回替代金额
			" ) " +
			" values(?,?,?,?,?,?,?,?,?,?," +//10
			"?,?,?,?,?,?,?)";
		return value;
	}

	/**shashijie 2012-6-18 STORY 2727 */
	private String getDelete(Date sDate, String sPort) {
		String value = " delete from "+pub.yssGetTableName("Tb_ETF_StockList")+
    	" where FDate ="+dbl.sqlDate(sDate)+
    	" and FPortCode in("+operSql.sqlCodes(sPort)+")";
		return value;
	}

	/**shashijie 2012-6-18 STORY 2727 */
	private List getBeanList(Date sDate, String sPort) throws YssException {
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			//edit by songjie 2012.10.24 修改获取证券代码的逻辑
			String query = "Select Stocklistfile, (LTRIM(FSecurityCode,'0') || ' HK') as FSecurityCode From (Select A1.Stocklistfile," +
					" Substr(A1.Stocklistfile," +
					" 1, Instr(A1.Stocklistfile, '|', 1, 1) - 1) As Fsecuritycode" +
					" From Tmp_Etf_Stocklist A1" +
					" Order By A1.Stocklistfile) a" +
					" Where a.Fsecuritycode Is Not Null and StockListFile not like '%XSHE%'";
			//查询出股票篮临时表tmp_etf_stocklist中的数据
	        rs = dbl.openResultSet(query);
	        while (rs.next()) {
	            Map<String, String> map = new HashMap<String, String>();
	            map.put("FPortCode", sPort);//组合代码
	            //获取证券代码
	            String FSecurityCode = getFSecurityCode(rs.getString("Fsecuritycode"));
	            map.put("FSecurityCode",FSecurityCode);//证券代码
	            String FAmount = getStocklistfileValue(rs.getString("Stocklistfile"),2,"[|]").equals("") ? 
	            		"0" : getStocklistfileValue(rs.getString("Stocklistfile"),2,"[|]");
	            map.put("FAmount",FAmount);//证券数量
	            map.put("FReplaceMark",getStocklistfileValue(rs.getString("Stocklistfile"),3,"[|]"));//替代标志
	            String FPremiumScale = getStocklistfileValue(rs.getString("Stocklistfile"),4,"[|]").equals("") ? 
	            		"0" : getStocklistfileValue(rs.getString("Stocklistfile"),4,"[|]");
	            map.put("FPremiumScale",FPremiumScale);//溢价比例
	            String money = getStocklistfileValue(rs.getString("Stocklistfile"),5,"[|]").equals("") ? 
	            		"0" : getStocklistfileValue(rs.getString("Stocklistfile"),5,"[|]");
	            map.put("FReplaceMoney",money);//替代金额
	            map.put("FTotalMoney",money);//总金额
	            String FSHReplaceMoney = getStocklistfileValue(rs.getString("Stocklistfile"),6,"[|]").equals("") ? 
	            		"0" : getStocklistfileValue(rs.getString("Stocklistfile"),6,"[|]");
	            map.put("FSHReplaceMoney", FSHReplaceMoney);//赎回替代金额
	            map.put("FDesc","");//描述
	            map.put("FDate",YssFun.formatDate(sDate));//导入日期
	            map.put("FISINCode","");//国际产品代码
	            map.put("FListedMarket",getStocklistfileValue(rs.getString("Stocklistfile"),7,"[|]"));//挂牌市场
	            list.add(map);
	        }
		} catch (Exception e) {
			throw new YssException("查询出股票篮临时表出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return list;
	}

	/**shashijie 2012-6-18 STORY 2727 根据传入的标示与下标来取出数组的值 
	* @param Stocklistfile 数组
	* @param id 下标
	* @param split 标识
	* @return*/
	private String getStocklistfileValue(String Stocklistfile, int id,String split) {
		String value = "";
		String[] sp = Stocklistfile.split(split);
		if(sp[id].trim().length()>0){
			value = sp[id].trim();
		}
		return value;
	}

	/**shashijie 2012-6-18 STORY 2727 获取证券代码 */
	private String getFSecurityCode(String Fsecuritycode) {
		ResultSet rs = null;
		String value = Fsecuritycode.trim();
		try {
			//---add by songjie 2012.10.24 修改获取证券代码的逻辑  start---//
			String query = " select FSecurityCode from " + pub.yssGetTableName("Tb_Para_Security") + 
			" where FSecurityCode = " + dbl.sqlString(Fsecuritycode.trim());
			//---add by songjie 2012.10.24 修改获取证券代码的逻辑  end---//
			//---delete by songjie 2012.10.24 修改获取证券代码的逻辑  start---//
//			String query = " Select s.FSecurityCode From "+pub.yssGetTableName("tb_para_security")+
//				" s Where s.FCheckState = 1 And Lpad(Substr(s.FSecuritycode,1,length(s.FSecuritycode)-3),5,'0') = "+
//				dbl.sqlString(Fsecuritycode.trim());
			//---delete by songjie 2012.10.24 修改获取证券代码的逻辑  end---//
            rs = dbl.openResultSet(query);
            if(rs.next()){
            	value = rs.getString("FSecurityCode"); //证券代码
            }else{
                //throw new YssException("请检查系统证券信息设置中是否有股票篮中的证券信息【"+Fsecuritycode.trim()+"】！");
            }
		} catch (Exception e) {

		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}

	/**shashijie 2012-6-18 STORY 2727 */
	private boolean isETFPortCode(String sPort) throws YssException {
		boolean falg = false;
		ResultSet rs = null;
		try {
			rs = dbl.openResultSet("Select FSubAssetType From "+pub.yssGetTableName("Tb_Para_Portfolio")+
					" where FPortCode = "+dbl.sqlString(this.sPort));
			if (rs.next()) {
				String type = rs.getString("FSubAssetType");
				if (type.equals("0106") || type.equals("0108")) {
					falg = true;
				}
			}
		} catch (Exception e) {
			throw new YssException("判断是否是ETF(联接)组合出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return falg;
	}

}








