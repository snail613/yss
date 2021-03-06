package com.yss.main.operdeal.datainterface.cnstock;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 财汇股本信息（B股）
 * created by yanghaiming
 * 2009-06-04
 */
public class GBXXBean extends DataBase {
	public GBXXBean(){
		
	}
	
	/**
     * 将股本信息表中的数据存储到板块设置，板块分类设置，证券信息设置表中
     * @throws YssException
     */
	private String holidaysCode = "";
	private String holidaysCodeSH = "";
	private String holidaysCodeSZ = "";
	private String currencyCodeA = "";
	private String currencyCodeSHB = "";
	private String currencyCodeSZB = "";
	private int delayDateA = 0;
	private int delayDateB = 0;
    public void inertData() throws YssException {
    	this.isDoInsert();
    	this.makeSector();
    	this.makeSecurity();
    }
    
    //判断是否已经设置B股节假日群
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
					if (rs.getString("fholidayscodesh") == null
							|| rs.getString("fholidayscodesz") == null
							|| rs.getString("FCURRENCYCODEA") == null
							|| rs.getString("FCURRENCYCODESHB") == null
							|| rs.getString("FCURRENCYCODESZB") == null
							|| rs.getString("FHOLIDAYSCODE") == null){
						throw new YssException("请先完善" + portCodes[i] + "组合的数据接口参数设置再执行导入！");
					}else{
						holidaysCode = rs.getString("FHOLIDAYSCODE");
						holidaysCodeSH = rs.getString("fholidayscodesh");
						holidaysCodeSZ = rs.getString("fholidayscodesz");
						currencyCodeA = rs.getString("FCURRENCYCODEA");
						currencyCodeSHB = rs.getString("FCURRENCYCODESHB");
						currencyCodeSZB = rs.getString("FCURRENCYCODESZB");
						delayDateA = rs.getInt("FDELAYDATEA");
						delayDateB = rs.getInt("FDELAYDATEB");
					}
				}else{
					throw new YssException("请先完善" + portCodes[i] + "组合的数据接口参数设置再执行导入！");
				}
    		}
    		
    	}catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    private void makeSector() throws YssException {
    	String strSql = "";//储存sql语句
    	String strSql1 = "";
        ResultSet rs = null;//声明结果集
        ResultSet rs1 = null;
        PreparedStatement stm = null;//声明PreparedStatement
        PreparedStatement stm1 = null;//声明PreparedStatement
        String flbkCode = "";
        String strOrderCode = "";//板块分类排序编号
        Connection conn = dbl.loadConnection();//新建连接
        boolean bTrans = false;
        int i = 1;
        try{
        	conn.setAutoCommit(false);//设置手动提交事务
            bTrans = true;
        	strSql = "insert into " + pub.yssGetTableName("Tb_Para_Sector") + " (FSectorCode,FSectorName,FSectorType,FStartDate,FCheckState,FCreator,FCreateTime)" +
        			" values(?,?,?,?,?,?,?)";
        	stm = dbl.openPreparedStatement(strSql);
        	strSql1 = "insert into " + pub.yssGetTableName("Tb_Para_SectorClass") + " (FSecClsCode,FSecClsName,FParentCode,FSectorCode,FOrderCode,FStartDate," +
        			"FCheckState,FCreator,FCreateTime) values(?,?,?,?,?,?,?,?,?)";
        	stm1 = dbl.openPreparedStatement(strSql1);
        	strSql = "select substr(flbk, 0, instr(flbk, ' ') - 1) as flbkcode,substr(flbk, instr(flbk, ' ')+1) as flbkname," + 
			" substr(zhbk, 0, instr(zhbk, ' ') - 1) as zhbkcode, substr(zhbk, instr(zhbk, ' ')+1) as zhbkname from tmp_GBXX" + 
			" where substr(zhbk, 0, instr(zhbk, ' ') - 1) not in (select FSecClsCode from " + pub.yssGetTableName("Tb_Para_SectorClass") +
			" ) and isDel ='False' group by zhbk, flbk order by flbk";
        	rs = dbl.openResultSet(strSql);
        	while (rs.next()){
        		if(rs.getString("flbkcode") != null){//接口文件中分类板块不会null时才进行导入
	        		if(!flbkCode.equals(rs.getString("flbkcode"))){
	        			strSql1 = "select * from " + pub.yssGetTableName("Tb_Para_Sector") + " where fsectorcode = " + dbl.sqlString(rs.getString("flbkcode"));
	        			rs1 = dbl.openResultSet(strSql1);
	        			if(rs1.next()){
	        				i = 1;
	        			}else{
			        		flbkCode = rs.getString("flbkCode");
			        		stm.setString(1, rs.getString("flbkCode"));
			        		stm.setString(2, rs.getString("flbkname"));
			        		stm.setString(3, "0");
			        		stm.setDate(4, YssFun.toSqlDate(this.sDate));//启用日期取接口导入的起始日期
			        		stm.setInt(5, 1);
			        		stm.setString(6, pub.getUserCode());
			        		stm.setString(7, YssFun.formatDate(pub.getUserDate(), "yyyy-MM-dd"));
			        		stm.addBatch();
			        		i = 1;
	        			}
	        		}
	        		if(!rs.getString("zhbkcode").equals(rs.getString("flbkcode"))){
		        		strSql1 = "select max(fordercode) as fordercode from " + pub.yssGetTableName("Tb_Para_SectorClass") + " where fsectorcode = " + dbl.sqlString(rs.getString("zhbkcode"));
		        		rs1 = dbl.openResultSet(strSql1);
		        		if(rs1.next()){
		        			if (rs1.getString("fordercode") == null) {
		        				strOrderCode = YssFun.formatNumber(i-1, "000");
		        				i++;
		        			}else{
		        				strOrderCode = YssFun.formatNumber(YssFun.toInt(rs1.getString("fordercode") == null ? "0" : rs1.getString("fordercode")) + i, "000");
		        				i++;
		        			}
		        			//strOrderCode = YssFun.formatNumber(YssFun.toInt(rs1.getString("fordercode") == null ? "0" : rs1.getString("fordercode")) + 1, "000");
		        		}
		        		stm1.setString(1, rs.getString("zhbkcode"));
		        		stm1.setString(2, rs.getString("zhbkname"));
		        		stm1.setString(3, rs.getString("flbkCode"));
		        		stm1.setString(4, rs.getString("flbkCode"));
		        		stm1.setString(5, dbl.sqlString(strOrderCode));
		        		stm1.setDate(6, YssFun.toSqlDate(this.sDate));
		        		stm1.setInt(7, 1);
		            	stm1.setString(8, pub.getUserCode());
		            	stm1.setString(9, YssFun.formatDate(pub.getUserDate(), "yyyy-MM-dd"));
		        		
		        		stm1.addBatch();
	        		}
        		}
        	}

        	stm.executeBatch();
        	stm1.executeBatch();

            conn.commit();//提交事务
            bTrans = false;
            conn.setAutoCommit(true);//设置为自动提交事务
        }catch (Exception e) {
            throw new YssException("处理财汇股本信息(B股)出错！", e);
        } finally {
            dbl.closeStatementFinal(stm);
            dbl.closeStatementFinal(stm1);
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs1);
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    private void makeSecurity() throws YssException {
    	String strSql = "";//储存sql语句
    	String strSql1 = "";
        ResultSet rs = null;//声明结果集
        ResultSet rs1 = null;
        PreparedStatement stm = null;//声明PreparedStatement
        PreparedStatement stm1 = null;
        Connection conn = dbl.loadConnection();//新建连接
        boolean bTrans = false;
        try{
        	conn.setAutoCommit(false);//设置手动提交事务
            bTrans = true;
        	strSql = "update " + pub.yssGetTableName("tb_para_security") + " set FSecurityName = ?, ftotalshare = ?," +
        			" fcurrentshare = ?, FSecurityCorpName = ?, FSectorCode = ?, fsyntheticcode = ?" + 
        			" where fsecuritycode = ?" ;
        	stm = dbl.openPreparedStatement(strSql);
        	strSql1 = "insert into " + pub.yssGetTableName("tb_para_security") + "(FSECURITYCODE,FSTARTDATE,FSECURITYNAME,FCATCODE,FSubCatCode,FEXCHANGECODE," +
        			" FMARKETCODE,FTRADECURY,FHOLIDAYSCODE,FSETTLEDAYTYPE,FSETTLEDAYS,FTOTALSHARE,FHANDAMOUNT,FFACTOR,FCURRENTSHARE," +
        			" FSectorCode,FSYNTHETICCODE,FCHECKSTATE,FCREATOR,FCREATETIME,FSECURITYCORPNAME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        	stm1 = dbl.openPreparedStatement(strSql1);
        	strSql = "select * from tmp_GBXX where zqdm in (select substr(fsecuritycode, 0, instr(fsecuritycode, ' ') - 1) as fsecuritycode from " + 
        			pub.yssGetTableName("tb_para_security") + " where substr(fsecuritycode, 0, instr(fsecuritycode, ' ') - 1) <> ' ') and isDel ='false'";
        	rs = dbl.openResultSet(strSql);
        	while(rs.next()){
        		if(rs.getString("flbk") != null && rs.getString("zhbk") != null && rs.getString("scdm") != null){
	        		stm.setString(1, rs.getString("zqjc") == null ? rs.getString("zqdm") : rs.getString("zqjc"));
	        		stm.setDouble(2, YssFun.roundIt(rs.getDouble("zgb"),4));
	        		if(rs.getString("scdm").equals("1") && YssFun.left(rs.getString("zqdm"), 3).equals("900")){
	        			stm.setDouble(3, YssFun.roundIt(rs.getDouble("ltbg"),4));
	        		}else if (rs.getString("scdm").equals("2") && YssFun.left(rs.getString("zqdm"), 2).equals("20")){
	        			stm.setDouble(3, YssFun.roundIt(rs.getDouble("ltbg"),4));
	        		}else {
	        			stm.setDouble(3, YssFun.roundIt(rs.getDouble("ltag"),4));
	        		}
	        		stm.setString(4, rs.getString("ywmc")== null ? "" : rs.getString("ywmc"));
	        		stm.setString(5, YssFun.left(rs.getString("flbk"), rs.getString("flbk").indexOf(" ")));
	        		stm.setString(6, YssFun.left(rs.getString("zhbk"), rs.getString("zhbk").indexOf(" ")));
	        		if(rs.getString("scdm").equals("1")){
	        			stm.setString(8, rs.getString("zqdm")+" CG");
	        		}
	        		if(rs.getString("scdm").equals("2")){
	        			stm.setString(8, rs.getString("zqdm")+" CS");
	        		}
	        		stm.addBatch();
        		}
        	}
        	strSql1 = "select * from tmp_GBXX where zqdm not in (select substr(fsecuritycode, 0, instr(fsecuritycode, ' ') - 1) as fsecuritycode from " + 
					pub.yssGetTableName("tb_para_security") + " where substr(fsecuritycode, 0, instr(fsecuritycode, ' ') - 1) <> ' ')";
        	rs1 = dbl.openResultSet(strSql1);
        	while(rs1.next()){
        		if(!(rs1.getString("flbk").equals(" ") && rs1.getString("zhbk").equals(" ")) && rs1.getString("scdm") != null){
	        		if(rs1.getString("scdm").equals("1") && (!YssFun.left(rs1.getString("zqdm"), 3).equals("000"))){
	        			stm1.setString(1, rs1.getString("zqdm") + " CG");
	        			stm1.setString(6, "CG");
	        			if(YssFun.left(rs1.getString("zqdm"), 3).equals("900")){
	        				stm1.setString(4, "EQ");//B股
	    	        		stm1.setString(5, "EQ01");//普通股
	        				stm1.setString(8, currencyCodeSHB);
	            			stm1.setString(9, holidaysCodeSH);
	            			stm1.setInt(11, delayDateB);
	            			stm1.setDouble(15, rs1.getDouble("ltbg"));
	        			}else{
	        				if(YssFun.left(rs1.getString("zqdm"), 1).equals("6")){
	        					stm1.setString(4, "EQ");//A股
		    	        		stm1.setString(5, "EQ01");//普通股
	        				}else if (YssFun.left(rs1.getString("zqdm"), 1).equals("5")){
	        					if(YssFun.left(rs1.getString("zqdm"), 3).equals("580") || YssFun.left(rs1.getString("zqdm"), 3).equals("582")){//权证
	        						stm1.setString(4, "OP");
			    	        		stm1.setString(5, "OP01");
	        					}else{//基金
	        						stm1.setString(4, "TR");
			    	        		stm1.setString(5, "TR01");
	        					}
	        				}else if (YssFun.left(rs1.getString("zqdm"), 1).equals("2")){//回购
	        					stm1.setString(4, rs1.getString("zqdm"));
		    	        		stm1.setString(5, "RE01");
	        				}else if (YssFun.left(rs1.getString("zqdm"), 1).equals("1") || YssFun.left(rs1.getString("zqdm"), 1).equals("0")){//债券
	        					stm1.setString(4, "FI");
		    	        		stm1.setString(5, "FI12");
	        				}
	        				stm1.setString(8, currencyCodeA);
	            			stm1.setString(9, holidaysCode);
	            			stm1.setInt(11, delayDateA);
	            			stm1.setDouble(15, rs1.getDouble("ltag"));
	        			}	
	        		}
	        		if(rs1.getString("scdm").equals("2")){
	        			stm1.setString(1, rs1.getString("zqdm") + " CS");
	        			stm1.setString(6, "CS");
	        			if(YssFun.left(rs1.getString("zqdm"), 2).equals("20")){
	        				stm1.setString(4, "EQ");//股票
	    	        		stm1.setString(5, "EQ01");//普通股
	        				stm1.setString(8, currencyCodeSZB);
	            			stm1.setString(9, holidaysCodeSZ);
	            			stm1.setInt(11, delayDateB);
	            			stm1.setDouble(15, rs1.getDouble("ltbg"));
	        			}else{
	        				if(YssFun.left(rs1.getString("zqdm"), 2).equals("00") || YssFun.left(rs1.getString("zqdm"), 2).equals("30")){//股票
	        					stm1.setString(4, "EQ");
		    	        		stm1.setString(5, "EQ01");
	        				}else if(YssFun.left(rs1.getString("zqdm"), 2).equals("03") || YssFun.left(rs1.getString("zqdm"), 2).equals("28")){//权证
	        					stm1.setString(4, "OP");
		    	        		stm1.setString(5, "OP02");
							} else if (YssFun.left(rs1.getString("zqdm"), 1).equals("1")){
		        				if (YssFun.left(rs1.getString("zqdm"), 2)
										.equals("16")
										|| YssFun.left(rs1.getString("zqdm"), 2)
												.equals("15")
										|| YssFun.left(rs1.getString("zqdm"), 2)
												.equals("18")) {// 基金
									stm1.setString(4, "TR");
									stm1.setString(5, "TR01");
								}else if(YssFun.left(rs1.getString("zqdm"), 2).equals("13")){//回购
									stm1.setString(4, "RE");
									stm1.setString(5, "RE01");
								}else{//债券
									stm1.setString(4, "FI");
									stm1.setString(5, "FI12");
								}
							}
	        				stm1.setString(8, currencyCodeA);
	            			stm1.setString(9, holidaysCode);
	            			stm1.setInt(11, delayDateA);
	            			stm1.setDouble(15, rs1.getDouble("ltag"));
	        			}
	        			
	        		}
	        		stm1.setDate(2, YssFun.toSqlDate("1900-01-01"));
	        		stm1.setString(3, rs1.getString("zqjc") == null ? rs1.getString("zqdm") : rs1.getString("zqjc"));
	        		
	        		stm1.setString(7, rs1.getString("zqdm"));
	        		stm1.setInt(10, 0);
	        		stm1.setDouble(12, rs1.getDouble("zgb"));
	        		stm1.setDouble(13, 100);
	        		stm1.setDouble(14, 1);
	        		stm1.setString(16, YssFun.left(rs1.getString("flbk"), rs1.getString("flbk").indexOf(" ")));
	        		stm1.setString(17, YssFun.left(rs1.getString("zhbk"), rs1.getString("zhbk").indexOf(" ")));
	        		stm1.setInt(18, 1);
	            	stm1.setString(19, pub.getUserCode());
	            	stm1.setString(20, YssFun.formatDate(pub.getUserDate(), "yyyy-MM-dd"));
	            	stm1.setString(21, rs1.getString("ywmc")== null ? "" : rs1.getString("ywmc")); 
	            	
	            	stm1.addBatch();
	        	}
        	}
        	
        	stm.executeBatch();
        	stm1.executeBatch();

            conn.commit();//提交事务
            bTrans = false;
            conn.setAutoCommit(true);//设置为自动提交事务
        }catch (Exception e) {
            throw new YssException("处理财汇股本信息(B股)出错！", e);
        } finally {
            dbl.closeStatementFinal(stm);
            dbl.closeStatementFinal(stm1);
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs1);
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
