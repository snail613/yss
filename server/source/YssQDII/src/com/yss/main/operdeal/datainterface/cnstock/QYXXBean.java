package com.yss.main.operdeal.datainterface.cnstock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 财汇权益信息（B股）
 * created by yanghaiming
 * 2009-06-04
 */
public class QYXXBean extends DataBase {
	
	private String currencyCodeA = "";
	private String currencyCodeSHB = "";
	private String currencyCodeSZB = "";
	private String roundCode = "";//默认舍入设置代码
	
	public QYXXBean(){
		
	}
	
	public void inertData() throws YssException {
		this.doCheck();
		this.isDoInsert();
		this.beforeInsert();
		this.makeEquity();
	}
	
	private void doCheck() throws YssException {
		String strSql = "";
    	ResultSet rs = null;
    	StringBuffer strResult = new StringBuffer();
    	try{
    		strSql = "select * from TMP_QY_INFO where zqdm not in (select substr(fsecuritycode, 0, instr(fsecuritycode, ' ') - 1) as fsecuritycode from " + 
					pub.yssGetTableName("tb_para_security") + " where substr(fsecuritycode, 0, instr(fsecuritycode, ' ') - 1) <> ' ') and (qykind = 'S' or qykind = 'HL')";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()) {
    			strResult.append(rs.getString("zqdm")).append(";");
    		}
    		if(strResult != null && strResult.length()>1){
    			throw new YssException("上市代码为："+strResult.substring(0,strResult.length()-1)+"的数据没有相关证券信息，请在证券信息设置界面维护后再导入！"); 
    		}
    	}catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	
	private void isDoInsert() throws YssException {
		String strSql = "";
    	ResultSet rs = null;
    	try{
    		String[] portCodes = this.sPort.split(",");//拆分已选组合代码
    		for(int i = 0; i<portCodes.length; i++){
    			strSql = "select * from " + pub.yssGetTableName("TB_DAO_ReadType") + " where fassetgroupcode = " + 
				dbl.sqlString(pub.getPrefixTB()) + " and fportcode = " + dbl.sqlString(portCodes[i]);
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if (rs.getString("FCURRENCYCODEA") == null
							|| rs.getString("FCURRENCYCODESHB") == null
							|| rs.getString("FCURRENCYCODESZB") == null){
						throw new YssException("请先完善" + portCodes[i] + "组合的数据接口参数设置再执行导入！");
					}else{
						currencyCodeA = rs.getString("FCURRENCYCODEA");
						currencyCodeSHB = rs.getString("FCURRENCYCODESHB");
						currencyCodeSZB = rs.getString("FCURRENCYCODESZB");
					}
				}else{
					throw new YssException("请先完善" + portCodes[i] + "组合的数据接口参数设置再执行导入！");
				}
				dbl.closeResultSetFinal(rs);
    		}	
    	}catch (Exception e) {
            throw new YssException(e);
        }
	}
	
	private void beforeInsert() throws YssException {
		String strSql = "";
    	ResultSet rs = null;
    	ResultSet rs1 = null;
    	Connection conn = dbl.loadConnection();
    	boolean bTrans = false;
    	try{
    		conn.setAutoCommit(false);//设置手动提事务
            bTrans = true;
    		strSql = "select * from " + pub.yssGetTableName("Tb_Para_Rounding") +
    				" where FRoundSymbol = 0 and FRoundRange = 0 and FRoundDigit = 2 and FRoundWay = 0";
    		rs = dbl.openResultSet(strSql);
    		if(rs.next()){
    			roundCode = rs.getString("FRoundCode");
    		}else{
    			strSql = "select max(froundcode) as froundcode from " + pub.yssGetTableName("Tb_Para_Rounding") + " where froundcode like 'R0%'";
    			rs1 = dbl.openResultSet(strSql);
    			if(rs.next()){
    				roundCode = rs1.getString("FRoundCode");
    				roundCode = "R" + YssFun.formatNumber(YssFun.toInt(YssFun.right(roundCode, roundCode.length() - 1)) + 1, "000");
    			}else{
    				roundCode = "R001";
    			}
    			strSql = "insert into " + pub.yssGetTableName("Tb_Para_Rounding") +
    					" (FRoundCode,FRoundName,FRoundSymbol,FRoundRange,FRoundDigit,FRoundWay,FCheckState,FCreator,FCreateTime)" +
    					" values(" + dbl.sqlString(roundCode) + " ,'四舍五入保留2位小数位',0,0,2,0,1,"+
    					dbl.sqlString(pub.getUserCode()) + "," + dbl.sqlString(YssFun.formatDate(pub.getUserDate(), "yyyy-MM-dd"))+ ")";
    			dbl.executeSql(strSql);
    		}
    		conn.commit();//提交事务
            bTrans = false;
            conn.setAutoCommit(true);//设置为自动提交事务
    	}catch (Exception e) {
            throw new YssException("导入权益信息时获取默认舍入位数出错！",e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	private void makeEquity() throws YssException {
		String strSql = "";
    	ResultSet rs = null;
    	Connection conn = dbl.loadConnection();//新建连接
        boolean bTrans = false;//用于判断事务是否开启
        PreparedStatement stm = null;//声明PreparedStatement
        PreparedStatement stm1 = null;//声明PreparedStatement
        String djDate = "";//确认日
        String cqDate = "";//除权日
        String dzDate = "";//到帐日
        try{
        	conn.setAutoCommit(false);//设置手动提事务
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_BonusShare") + " where substr(FTSecurityCode, 0, instr(FTSecurityCode, ' ') - 1) in " +
            		" (select zqdm from tmp_qy_info where QyKind = 'S') and FRecordDate in (select to_date(djdate,'yyyyMMdd') as djdate from tmp_qy_info where QyKind = 'S')";
            dbl.executeSql(strSql);//删除送股权益相关数据
            
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_Dividend") + " where substr(FSecurityCode, 0, instr(FSecurityCode, ' ') - 1) in " +
    				" (select zqdm from tmp_qy_info where QyKind = 'HL') and FRecordDate in (select to_date(djdate,'yyyyMMdd') as djdate from tmp_qy_info where QyKind = 'HL')";
            dbl.executeSql(strSql);//删除股票分红相关数据
            
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_BonusShare") +
            		" (FTSecurityCode,FRecordDate,FExRightDate,FAfficheDate,FPayDate,FPreTaxRatio," +
            		" FAfterTaxRatio,FPortCode,FAssetGroupCode,FRoundCode,FCheckState,FCreator,FCreateTime,FSSECURITYCODE)" +
            		" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//送股权益
            stm = dbl.openPreparedStatement(strSql);
            
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_Dividend") +
    		" (FSecurityCode,FCuryCode,FAfficheDate,FRecordDate,FDividendDate,FDistributeDate," +
    		" FDivdendType,FPreTaxRatio,FAfterTaxRatio,FPortCode,FAssetGroupCode,FRoundCode,FCheckState,FCreator,FCreateTime)" +
    		" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//股票分红
            stm1 = dbl.openPreparedStatement(strSql);
            
            strSql = "select * from tmp_qy_info";
            rs = dbl.openResultSet(strSql);
            while(rs.next()){
            	djDate = rs.getString("DjDate");
            	cqDate = rs.getString("CqDate");
        		dzDate = rs.getString("dzdate");
            	djDate = YssFun.left(djDate, 4) +
                		"-" + YssFun.mid(djDate, 4, 2) + "-" +
                			YssFun.right(djDate, 2);
        		cqDate = YssFun.left(cqDate, 4) +
		        		"-" + YssFun.mid(cqDate, 4, 2) + "-" +
		    			YssFun.right(cqDate, 2);
        		dzDate = YssFun.left(dzDate, 4) +
		        		"-" + YssFun.mid(dzDate, 4, 2) + "-" +
		    			YssFun.right(dzDate, 2);
            	if(rs.getString("QYKIND").equalsIgnoreCase("S")){//送股权益
            		if(rs.getString("scdm").equalsIgnoreCase("1")){
            			stm.setString(1, rs.getString("zqdm") + " CG");
            			stm.setString(14, rs.getString("zqdm") + " CG");
            		}else if (rs.getString("scdm").equalsIgnoreCase("2")){
            			stm.setString(1, rs.getString("zqdm") + " CS");
            			stm.setString(14, rs.getString("zqdm") + " CS");
            		}else if (rs.getString("scdm").equalsIgnoreCase("3")){
            			stm.setString(1, rs.getString("zqdm") + " CY");
            			stm.setString(14, rs.getString("zqdm") + " CY");
            		}else if (rs.getString("scdm").equalsIgnoreCase("4")){
            			stm.setString(1, rs.getString("zqdm") + " CO");
            			stm.setString(14, rs.getString("zqdm") + " CO");
            		}else {
            			stm.setString(1, " ");
            			stm.setString(14, " ");
            		}
            		
            		stm.setDate(2, YssFun.toSqlDate(djDate));//确认日
            		stm.setDate(3, YssFun.toSqlDate(cqDate));//除权日
            		stm.setDate(4, YssFun.toSqlDate(YssFun.addDay(YssFun.toDate(djDate), -3)));//公告日
            		stm.setDate(5, YssFun.toSqlDate(dzDate));//到帐日
            		stm.setDouble(6, rs.getDouble("beforerate"));
            		stm.setDouble(7, rs.getDouble("afterrate"));
            		stm.setString(8, " ");
            		stm.setString(9, " ");
            		stm.setString(10, roundCode);//舍入设置
            		stm.setInt(11, 1);
	            	stm.setString(12, pub.getUserCode());
	            	stm.setString(13, YssFun.formatDate(pub.getUserDate(), "yyyy-MM-dd"));
	            	
	            	stm.addBatch();
            	}else if(rs.getString("QYKIND").equalsIgnoreCase("HL")){//股票分红
            		if(rs.getString("scdm").equalsIgnoreCase("1")){
            			stm1.setString(1, rs.getString("zqdm") + " CG");
            		}else if (rs.getString("scdm").equalsIgnoreCase("2")){
            			stm1.setString(1, rs.getString("zqdm") + " CS");
            		}else if (rs.getString("scdm").equalsIgnoreCase("3")){
            			stm1.setString(1, rs.getString("zqdm") + " CY");
            		}else if (rs.getString("scdm").equalsIgnoreCase("4")){
            			stm1.setString(1, rs.getString("zqdm") + " CO");
            		}else {
            			stm1.setString(1, " ");
            		}
            		if(YssFun.left(rs.getString("zqdm"), 2).equals("20")){
            			stm1.setString(2, currencyCodeSZB);
            		}else if(YssFun.left(rs.getString("zqdm"), 3).equals("900")){
            			stm1.setString(2, currencyCodeSHB);
            		}else{
            			stm1.setString(2, currencyCodeA);
            		}
            		stm1.setDate(3, YssFun.toSqlDate(YssFun.addDay(YssFun.toDate(djDate), -3)));//公告日
            		stm1.setDate(4, YssFun.toSqlDate(djDate));//确认日
            		stm1.setDate(5, YssFun.toSqlDate(cqDate));//除息日
            		stm1.setDate(6, YssFun.toSqlDate(dzDate));//到息日
            		stm1.setInt(7, 0);
            		stm1.setDouble(8, rs.getDouble("beforerate"));
            		stm1.setDouble(9, rs.getDouble("afterrate"));
            		stm1.setString(10, " ");
            		stm1.setString(11, " ");
            		stm1.setString(12, roundCode);//舍入设置
            		stm1.setInt(13, 1);
	            	stm1.setString(14, pub.getUserCode());
	            	stm1.setString(15, YssFun.formatDate(pub.getUserDate(), "yyyy-MM-dd"));
	            	
	            	stm1.addBatch();
            	}
            }
            
            stm.executeBatch();
        	stm1.executeBatch();

            conn.commit();//提交事务
            bTrans = false;
            conn.setAutoCommit(true);//设置为自动提交事务
        }catch (Exception e) {
            throw new YssException("处理财汇权益信息(B股)出错！", e);
        } finally {
            dbl.closeStatementFinal(stm);
            dbl.closeStatementFinal(stm1);
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
	}
}
