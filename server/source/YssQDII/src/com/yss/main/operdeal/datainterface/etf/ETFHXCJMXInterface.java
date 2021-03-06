package com.yss.main.operdeal.datainterface.etf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.GregorianCalendar;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 华夏接口导入香港代办券商成交明细回报数据，先存放到临时表（tmp_etf_CJMXInterface）,然后导入系统表（Tb_ETF_CJMXInterface）
 * @author xuqiji 20091217
 *
 */
public class ETFHXCJMXInterface extends DataBase{
	
	public ETFHXCJMXInterface() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 接口导入入口方法
	 */
	public void inertData() throws YssException {
		try{
			this.doInsertData();//接口导入数据
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	/**
	 * 接口导入数据
	 * @throws YssException 
	 *
	 */
	private void doInsertData() throws YssException {
		Connection conn = dbl.loadConnection(); // 新建连接
		boolean bTrans = true;//事物控制标识
		ResultSet rs = null;//结果集
		PreparedStatement pst = null; // 声明PreparedStatement
		StringBuffer buff = null;
		String [] sportCode = null;
		String securityCode = "";
		
		try{
			buff = new StringBuffer(500);

			sportCode = this.sPort.split(",");
			for(int i =0; i< sportCode.length; i++){
				conn.setAutoCommit(false);//设置为手动提交事物
				dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_ETF_CJMXInterface"));
				
				/**shashijie 2011-12-23 STORY 1789 华夏导入的文件里每个字段都会有""号,所以这里需要去掉 */
				buff.append(" Select Max(Replace(a.Side, '\"', '')) As Side "+
						" ,Sum(Replace(a.Quantity, '\"', '')) As Quantity"+
						" ,Sum(Replace(a.Price, '\"', '')) As Price"+
						" ,Sum(Replace(a.Money, '\"', '')) As Money"+
						" ,Sum(Replace(a.Fee, '\"', '')) As Fee"+
						" ,Max(Replace(a.Broker, '\"', '')) As Broker"+
						" ,Replace(a.TradeDate, '\"', '') As TradeDate"+
						" ,Max(Replace(a.SettlementDate, '\"', '')) As SettlementDate"+
						" ,Max(Replace(a.Account, '\"', '')) As Account"+
						" ,Max(Replace(a.TIF, '\"', '')) As TIF"+
						" ,Replace(a.Hbhtxh, '\"', '') As Hbhtxh"+
						" ,Replace(a.Security, '\"', '') As Security"+
						" ,Max(Replace(a.FStockerCode, '\"', '')) As FStockerCode"+
						" From tmp_etf_CJMXInterface a"+
						" Group By a.TradeDate,a.Security,a.Hbhtxh ");
				/**end*/
				
				rs = dbl.openResultSet_antReadonly(buff.toString());
				buff.delete(0,buff.length());
				
				buff.append(" insert into ").append(pub.yssGetTableName("Tb_ETF_CJMXInterface"));
				buff.append("(FPortCode,FBS,FTradeAmount,FTradePrice,FTradeMoney,FTradeFee,FBrokerCode,FBargainDate,");
				buff.append(" FSettleDate,FCashAccCode,FTIF,FContractNum,FSecurityCode,FCheckState,");
				buff.append(" FCreator,FCreateTime,FCheckUser,FCheckTime,FStockerCode)").append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				pst = dbl.openPreparedStatement(buff.toString());
				buff.delete(0,buff.length());
				if(rs.next()){
					//1.删除表Tb_XXX_ETF_HBInterface相关交易日期和组合代码的数据
					buff.append(" delete from ").append(pub.yssGetTableName("Tb_ETF_CJMXInterface"));
					buff.append(" where FBargainDate = ").append(dbl.sqlDate(this.sDate));
					buff.append(" and FPortCode in(").append(operSql.sqlCodes(this.sPort)).append(")");
					buff.append(" and FBrokerCode =").append(dbl.sqlString(rs.getString("Broker")));
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
					
					rs.beforeFirst();
					
					while(rs.next()){
						if(rs.getString("Side").equalsIgnoreCase("Side")&&rs.getString("Quantity").equalsIgnoreCase("Quantity")
								&&rs.getString("Security").equalsIgnoreCase("Security")){
							continue;
						}
						
						//判断文件中的成交日期是否与文件日期（接口导入日期）一致
						boolean bChecked = checkDate(rs.getString("TradeDate"));
						if(bChecked){
							continue;//成交日期与导入日期不一致，不执行导入
						}
						/**shashijie 2011-12-23 STORY 1789 华夏导入的文件里每个字段都会有""号,所以这里需要去掉 */
						pst.setString(1,sportCode[i]);//组合代码
						pst.setString(2,rs.getString("Side").replaceAll("\"", ""));//买卖方向
						pst.setDouble(3,Double.parseDouble(rs.getString("Quantity").replaceAll("\"", "")));//成交数量
						pst.setDouble(4,Double.parseDouble(rs.getString("Price").replaceAll("\"", "")));//成交价格
						pst.setDouble(5,Double.parseDouble(rs.getString("Money").replaceAll("\"", "")));//成交金额
						pst.setDouble(6,Double.parseDouble(rs.getString("Fee").replaceAll("\"", "")));//成交费用
						pst.setString(7,rs.getString("Broker").replaceAll("\"", ""));//券商代码
						pst.setDate(8,YssFun.toSqlDate(this.sDate));//成交日期
						pst.setDate(9,YssFun.toSqlDate(rs.getString("SettlementDate").replaceAll("\"", "")));//结算日期
						pst.setString(10,rs.getString("Account").replaceAll("\"", ""));//现金账户
						pst.setString(11,rs.getString("TIF").replaceAll("\"", ""));//有效日期
						
						/**shashijie 2011-12-29 STORY 1789 合同序号只需要截取前6位（席位号） +　后8位（合同序号）即可 */
						pst.setString(12,YssFun.left(rs.getString("Hbhtxh").replaceAll("\"", ""),6) + "-" + 
								YssFun.right(rs.getString("Hbhtxh").replaceAll("\"", ""),8));//合同序号
						/**end*/
						
						if(rs.getString("Security").indexOf(" Equity") > 0){
							//如果证券代码后面带 Equity，则将其去掉
							securityCode = (rs.getString("Security").trim().substring(0,
									rs.getString("Security").length()-6)).replaceAll("\"", "").trim();
						}else{
							securityCode = rs.getString("Security").trim();
						}
						pst.setString(13,securityCode);//证券代码
						pst.setInt(14,1);
						pst.setString(15,pub.getUserCode());
						pst.setString(16,YssFun.formatDatetime(new java.util.Date()));
						pst.setString(17,pub.getUserCode());
						pst.setString(18,YssFun.formatDatetime(new java.util.Date()));
						//股东代码,不满10位的自定在前面补零
						pst.setString(19,getFormatFStockerCode(rs.getString("FStockerCode").replaceAll("\"", "")));//股东代码
						/**end*/
						
						pst.addBatch();
					}
					pst.executeBatch();
				}
				conn.commit();
				conn.setAutoCommit(true);
				bTrans = false;
				dbl.closeResultSetFinal(rs);
				dbl.closeStatementFinal(pst);
			}
		}catch (Exception e) {
			throw new YssException("接口导入数据出错！",e);
		}
		
	}

	/**
	 * 判断文件中的成交日期是否与文件日期（接口导入日期）一致
	 * 由于日期传入的格式不固定，可能存在YYYYMMDD，MMDDYY,MMDDYYYY等格式
	 * 只要传入的年月日对应的数字能与当前接口处理日期的年月日数字匹配上就认为一致
	 * @param string
	 * @return
	 * @throws YssException 
	 */
	private boolean checkDate(String contentDate) throws YssException {
		boolean bResult = false;
/*      int jj;
        char ss, cc;
        String[] sss = {
            "-", "/", "."};
        String[] result;

        //检查分隔符
        for (jj = 0; jj < sss.length; jj++) {
            if (contentDate.indexOf(sss[jj]) >= 0) {
                break;
            }
        }
        if (jj >= sss.length) {
        	//非法日期格式
        	return true;
        }

        ss = sss[jj].charAt(0);
        //检查数字有效性即除了数字和分隔符，不应该再包括其它字符
        for (int i = 0; i < contentDate.length(); i++) {
            cc = contentDate.charAt(i);
            if (cc != ss && (cc < '0' || cc > '9')) {
            	//非法日期格式
            	return true;
            }
        }

        //劈开，获取3个数字
        result = contentDate.split(sss[jj], -1); //检查全部，包括空的元素，用0会忽略空
        if (result.length != 3) {
        	//非法日期格式
        	return true;
        }
        
        String year = String.valueOf(YssFun.getYear(this.sDate));
        String month = String.valueOf(YssFun.getMonth(this.sDate));
        String day = String.valueOf(YssFun.getDay(this.sDate));
        
        String sContentOne = result[0].length() < 2 ? "0" + result[0] : result[0];
        String sContentTwo = result[1].length() < 2 ? "0" + result[1] : result[1];
        String sContentThree = result[2].length() < 2 ? "0" + result[2] : result[2];
        
        if(day.equals(sContentOne))
        {
        	if(month.equals(sContentTwo)){
        		bResult = year.indexOf(sContentThree) > -1 ? false : true;
        	}else if(month.equals(sContentThree)){
        		bResult = year.indexOf(sContentTwo) > -1 ? false : true;
        	}
        }else if(day.equals(sContentTwo)){
        	if(month.equals(sContentOne)){
        		bResult = year.indexOf(sContentThree) > -1 ? false : true;
        	}else if(month.equals(sContentThree)){
        		bResult = year.indexOf(sContentOne) > -1 ? false : true;
        	}
        }else if(day.equals(sContentThree)){
        	if(month.equals(sContentTwo)){
        		bResult = year.indexOf(sContentOne) > -1 ? false : true;
        	}else if(month.equals(sContentOne)){
        		bResult = year.indexOf(sContentTwo) > -1 ? false : true;
        	}
        }else{
        	bResult = true;
        }*/
		
		if(!YssFun.isDate(contentDate)){
			return true;
		}
		
        int jj;
        String[] sss = {
            "-", "/", "."};
        String[] result;
        int kk, mm;
        GregorianCalendar cl = null;

        //检查分隔符
        for (jj = 0; jj < sss.length; jj++) {
            if (contentDate.indexOf(sss[jj]) >= 0) {
                break;
            }
        }

        //劈开，获取3个数字
        result = contentDate.split(sss[jj], -1); //检查全部，包括空的元素，用0会忽略空
        jj = Integer.parseInt(result[0]);
        kk = Integer.parseInt(result[1]);
        mm = Integer.parseInt(result[2]);

        //判断是否符合一种日期格式
        //1、yyyy/MM/dd格式
        if (YssFun.isValidDate(jj, kk, mm)) {
            cl = new GregorianCalendar(jj < 30 ? jj + 2000 :
                                       (jj <= 99 ? jj + 1900 : jj), kk - 1, mm);
            
            if(YssFun.dateDiff(cl.getTime(),this.sDate) == 0){
            	return false;
            }
        }
        //2、MM/dd/yyyy、MM/dd/yy格式
        if (YssFun.isValidDate(mm < 30 ? mm + 2000 :
                					(mm <= 99 ? mm + 1900 : mm), jj, kk)) {
        	cl = new GregorianCalendar(mm < 30 ? mm + 2000 :
									(mm <= 99 ? mm + 1900 : mm), jj - 1, kk);
            if(YssFun.dateDiff(cl.getTime(),this.sDate) == 0){
            	return false;
            }
        }
        //3、dd/MM/yyyy格式
        if (YssFun.isValidDate(mm < 30 ? mm + 2000 :
								(mm <= 99 ? mm + 1900 : mm), kk, jj)) {
            cl = new GregorianCalendar(mm < 30 ? mm + 2000 :
								(mm <= 99 ? mm + 1900 : mm), kk - 1, jj);
            if(YssFun.dateDiff(cl.getTime(),this.sDate) == 0){
            	return false;
            }
        } 
        
        bResult = true;//上述格式都不符合
        
		return bResult;
	}
	
	/**格式化字符串,不满10位的自定在前面补零(最早处理股东代码)
	 * @param value 需要格式化的值
	 * @return formatValue 格式化过后的值
	 * @author shashijie ,2011-12-27 , STORY 1789 
	 */
	private static String getFormatFStockerCode(String value) {
		String formatValue = value;
		int todo = 10 - value.trim().length();
		if (todo <= 0) {
			return formatValue;
		}
		for (int i = 0; i < todo; i++) {
			formatValue = "0"+formatValue;
		}
		return formatValue;
	}  
}
