package com.yss.main.operdata.moneycontrol;

import java.sql.*;
import java.math.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: TradeBean </p>
 * <p>Description: 汇率数据 </p>
 * @author fangjiang
 * @date 20100728
 */
public class TradeBean extends BaseDataSettingBean implements
		IDataSetting {
	
	private boolean BShow = false;
	
	private TradeBean FilterType = null;
	private String sRecycled = ""; //保存未解析前的字符串
	private String multAuditString = ""; //批量处理数据
	
	private String num = "";//编号
    private String securityCode = "";//证券代码
    private String securityName = "";//证券名称
    private String tradeTypeCode = "";//交易方式代码
    private String tradeTypeName = "";//交易方式名称
    private String portCode = "";//组合代码
    private String portName = "";//组合名称
    private String cashAccCode = "";//现金账户代码
    private String cashAccName = "";//现金账户名称
    private Date bargainDate;//成交日期
    private Date settleDate;//结算日期
    private double settleMoney;//结算金额
    private String exchangeCode = "";//交易所代码
    private String exchangeName = "";//交易所名称
    private String securityType = "";//证券类型
    private String desc = "";//描述

    private String oldNum = "";//修改前的编号
	
	private TradeBean tradeBean = null;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	
	public String buildRowStr() throws YssException {
		
		StringBuffer buf = new StringBuffer();
        
        buf.append(this.num).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.tradeTypeCode).append("\t");
        buf.append(this.tradeTypeName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.cashAccCode).append("\t");
        buf.append(this.cashAccName).append("\t");
        buf.append(format.format(this.bargainDate)).append("\t");
        buf.append(format.format(this.settleDate)).append("\t");
        buf.append(this.settleMoney).append("\t");
        buf.append(this.exchangeCode).append("\t");
        buf.append(this.exchangeName).append("\t");
        buf.append(this.securityType).append("\t");
        buf.append(this.desc).append("\t");
        
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (tradeBean == null) {
			tradeBean = new TradeBean();
			tradeBean.setYssPub(pub);
        }
		String reqAry[] = null;
        String sTmpStr = "";
        
        String sMutiAudit = ""; //批量处理的数据
        try {
        	
        	if (sRowStr.trim().length() == 0) {
                return;
            }
        	
        	if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];  //得到的是从前台传来需要审核与反审核的批量数据
                multAuditString = sMutiAudit;                   //保存在全局变量中
                sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];     //前台传来的要更新的一些数据
            }
        	
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            
            this.num = reqAry[0];
            this.securityCode = reqAry[1];
            this.securityName = reqAry[2];
            this.tradeTypeCode = reqAry[3];
            this.tradeTypeName = reqAry[4]; 
            this.portCode = reqAry[5];
            this.portName = reqAry[6];
            this.exchangeCode = reqAry[7];
            this.exchangeName = reqAry[8];
            this.cashAccCode = reqAry[9];
            this.cashAccName = reqAry[10];
            this.bargainDate = YssFun.parseDate(reqAry[11].trim().length()==0?"9998-12-31":reqAry[11]);
            this.settleDate = YssFun.parseDate(reqAry[12].trim().length()==0?"9998-12-31":reqAry[12]);
            this.securityType = reqAry[13];
            this.settleMoney = (reqAry[14].trim().length()==0?0:Double.parseDouble(reqAry[14]));
            this.desc = reqAry[15];
            
            this.oldNum = reqAry[16];

            this.checkStateId = YssFun.toInt(reqAry[17]);

            if (reqAry[18].equalsIgnoreCase("true")) {
                this.BShow = true;
            } else {
                this.BShow = false;
            } 
            
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new TradeBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析交易数据出错！", e);
        }

	}
	
	//新增一条数据
	public String addSetting() throws YssException {
		String strSql="";
		boolean bTrans=false;//代表事务是否开始
		String nowDate="";
		Connection conn = dbl.loadConnection();
		try {
            conn.setAutoCommit(false);
            bTrans = true;
            nowDate=YssFun.formatDate(new java.util.Date(),
                    YssCons.YSS_DATETIMEFORMAT).
                    substring(0, 8);
            this.num = "T" + nowDate +"00000"+
            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_DivineTradeData"),
                                   dbl.sqlRight("FNum", 6), "000001",
                                   " where FNum like 'T"
                                   + nowDate + "%'", 1);
            strSql="insert into "+pub.yssGetTableName("Tb_Data_DivineTradeData")
            	+ " (FNUM,FSECURITYCODE,FTRADETYPECODE,FPORTCODE,FCASHACCCODE,FBARGAINDATE,FSETTLEDATE,FSETTLEMONEY,FEXCHANGECODE,FSECURITYTYPE,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME)"
            	+" values("
            	+dbl.sqlString(this.num)+ ","
            	+dbl.sqlString(this.securityCode)+ ","
            	+dbl.sqlString(this.tradeTypeCode)+ ","
            	+dbl.sqlString(this.portCode)+ ","
            	+dbl.sqlString(this.cashAccCode)+ ","
            	+dbl.sqlDate(this.bargainDate)+ ","
            	+dbl.sqlDate(this.settleDate)+ ","
            	+this.settleMoney+ ","
            	+dbl.sqlString(this.exchangeCode)+ ","
            	+dbl.sqlString(this.securityType)+ ","
            	+dbl.sqlString(this.desc)+ ","
            	+"0,"
            	+dbl.sqlString(this.creatorCode)+ ","
            	+dbl.sqlString(this.creatorTime)+")";
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            return "";        
        } catch (Exception e) {
            throw new YssException("新增交易数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void checkInput(byte btOper) throws YssException {	
		dbFun.checkInputCommon(btOper,pub.yssGetTableName("TB_DATA_DivineTradeData"),
                               "FNum",this.num,this.oldNum);
	}
	
	//审核
	public void checkSetting() throws YssException {
		String strSql = ""; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		try {
			conn.setAutoCommit(false); 
			bTrans = true; 
			if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { // 判断传来的内容是否为空
				arrData = sRecycled.split("\r\n"); 
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]); 
					
                    strSql = "update " + pub.yssGetTableName("TB_DATA_DivineTradeData")
                    	+ " set FCheckState = case fcheckstate when 0 then 1 else 0 end" 
                    	+ ", FCheckUser = " 
                    	+ dbl.sqlString(this.checkUserCode)
                    	+ ", FCheckTime = "
                    	+ dbl.sqlString(this.checkTime)
                    	+ " where FNum = " + dbl.sqlString(this.num);
					
					dbl.executeSql(strSql); // 执行更新操作
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核交易信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}

	}
	
	//删除
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
            strSql = "update " + pub.yssGetTableName("TB_DATA_DivineTradeData")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where FNum = " + dbl.sqlString(this.num);
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除交易信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	//清除
	public void deleteRecycleData() throws YssException {
		String strSql = ""; // 定义一个放SQL语句的字符串
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false; // 代表是否开始了事务
        // 获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            // 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                // 根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                // 循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("TB_DATA_DivineTradeData") 
	                	+ " where FNum = " + dbl.sqlString(this.num);
                    
                    dbl.executeSql(strSql);
                }
            }

            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除交易信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); // 释放资源
        }		

	}
	
	//修改
	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("TB_DATA_DivineTradeData")
            
            	+ " set FSECURITYCODE = " + dbl.sqlString(this.securityCode)
            	+ " , FTRADETYPECODE = " + dbl.sqlString(this.tradeTypeCode)
            	+ " , FPORTCODE = " + dbl.sqlString(this.portCode)
            	+ " , FCASHACCCODE = " + dbl.sqlString(this.cashAccCode)
            	+ " , FBARGAINDATE = " + dbl.sqlDate(this.bargainDate)
            	+ " , FSETTLEDATE = " + dbl.sqlDate(this.settleDate)
            	+ " , FSETTLEMONEY = " + this.settleMoney
            	+ " , FEXCHANGECODE = " + dbl.sqlString(this.exchangeCode)
            	+ " , FSECURITYTYPE = " + dbl.sqlString(this.securityType)
            	+ " , FDESC = " + dbl.sqlString(this.desc)
            	+ " , fcreator = " + dbl.sqlString(this.creatorCode)
            	+ " , fcreatetime = " + dbl.sqlString(this.creatorTime)

				+ " where FNum = " + dbl.sqlString(this.oldNum); 
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改交易信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public String getOperValue(String sType) throws YssException {
        String sResult = "";
        try {
            //批量审核/反审核/删除
            if (sType.equalsIgnoreCase("multauditTradeSub")) { //判断是否要进行批量审核与反审核
                if (multAuditString.length() > 0) { //判断批量审核与反审核的内容是否为空
                    return this.auditMutli(this.multAuditString); //执行批量审核/反审核
                }
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }
	
	public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null; //建立一个数据库连接
        String sqlStr = ""; //创建一个字符串
        PreparedStatement psmt1 = null;
        boolean bTrans = true; //建一个boolean变量，默认自动回滚
        TradeBean tradeBean = null; //创建一个pojo类
        String[] multAudit = null; //建一个字符串数组

        try {
            conn = dbl.loadConnection(); //和数据库进行连接
            //审核、反审核、删除交易数据
            sqlStr = "update " + pub.yssGetTableName("TB_DATA_DivineTradeData") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = ? "  ; //更新数据库审核与未审核的SQL语句
            
            psmt1 = conn.prepareStatement(sqlStr); //执行SQL语句


            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                    	tradeBean = new TradeBean(); //new 一个pojo类
                    	tradeBean.setYssPub(pub); //设置一些基础信息
                    	tradeBean.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                    	psmt1.setString(1,tradeBean.num);
                        psmt1.addBatch(); 
                    }
                }
                conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
                psmt1.executeBatch();
                conn.commit(); //提交事物
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("批量审核交易数据出错!");
        } finally
        {
        	dbl.closeStatementFinal(psmt1);
        }
        return "";
    }

	public String getListViewData1() throws YssException {
		String strSql = ""; // 定义一个存放sql语句的字符串
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
        	sHeader = this.getListView1Headers();
        	
        	strSql="select a.fnum as fnum," +
					"a.fportcode as fportcode," +
					"b.fportname as fportname," +
					"a.ftradetypecode as ftradetypecode," +
					"e.ftradetypename as ftradetypename, " +
					"a.fcashacccode as fcashacccode ," +
					"d.fcashaccname as fcashaccname," +
					"a.fexchangecode as fexchangecode," +
					"f.fexchangename as fexchangename," +
					"a.fsecuritycode as fsecuritycode," +
					"c.fsecurityname as fsecurityname," +
					"a.fbargaindate as fbargaindate," +
					"a.fsettledate as fsettledate," +
					"a.fsecuritytype as fsecuritytype, " +
					"a.fsettlemoney as fsettlemoney," +
					"a.fdesc as fdesc," +
					"a.fcheckstate as FCheckState, " +
					"a.fcreator as FCreator,"+
					"g.FUserName as FCreatorName, " +
					"a.fcreatetime as FCreateTime, " +
					"a.fcheckuser as FCheckUser,"+
					"h.fusername as FCheckUserName,"
				+   "a.fchecktime as FCheckTime from "
				+ pub.yssGetTableName("TB_DATA_DivineTradeData")+
				" a left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
//				+pub.yssGetTableName("tb_Para_Portfolio")+//delete by songjie 2011.03.16 不以最大的启用日期查询数据
				+" select FSTARTDATE, fportcode, FPORTNAME from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
				+pub.yssGetTableName("tb_Para_Portfolio")+
				" where FCheckState = 1"//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
				+") b "
				+"on a.fportcode = b.fportcode "
				+ " left join (select * from " + pub.yssGetTableName("Tb_Para_Security") + ") c on a.fsecuritycode = c.fsecuritycode "
				+ " left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
//				+pub.yssGetTableName("Tb_Para_CashAccount")+//delete by songjie 2011.03.16 不以最大的启用日期查询数据
				+" select FSTARTDATE, FCashAccCode, FCashAccName from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
				+pub.yssGetTableName("Tb_Para_CashAccount")+//edit by songjie 2011.03.16 不以最大的启用日期查询数据
				" where FCheckState = 1"
				+") d on a.fcashacccode = d.fcashacccode" 
		        + " left join (select * from " + pub.yssGetTableName("Tb_Base_TradeType") + ") e on a.ftradetypecode = e.ftradetypecode "
		        + " left join (select * from " + pub.yssGetTableName("Tb_Base_Exchange") + ") f on a.fexchangecode = f.fexchangecode "
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) g " 
		        + " on a.FCreator = g.FUserCode "
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) h " 
		        + " on a.FCheckUser = h.FUserCode " 
				+ " where " + buildFilterStr("a")
				+ " order by a.FCheckState, a.FCreateTime desc";
				;
            rs = dbl.openResultSet(strSql);
            while(rs.next()){
            	bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                append(YssCons.YSS_LINESPLITMARK);
            	
            	this.setResultSetAttr(rs);
            	
            	bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
            + "\r\f" + this.getListView1ShowCols();
        }
        catch(Exception e){
        	throw new YssException("获取交易数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	
	/**
     * 生成筛选条件子句
     * @param prefix
     * @return
     * @throws YssException
     */
    public String buildFilterStr(String prefix) throws YssException {
        String str="";
    	
    	try {
 			ArrayList alCon=new ArrayList();
			
			alCon.add(" 1=1 ");
			
    		if (this.FilterType!=null) {
    			TradeBean filter = this.FilterType;
    			
    			if (filter.isBShow() == false) {
    				alCon.add(" 1=2 ");
                }
    			
    			if (prefix==null) {
    				prefix="";
    			} else if (!prefix.trim().endsWith(".")) {
    				prefix+=".";
    			}
    			
    			if(!YssFun.formatDate(filter.getBargainDate()).equalsIgnoreCase("9998-12-31")) {
    				alCon.add(prefix+"FBargainDate = "+dbl.sqlDate(filter.getBargainDate()));
    			}
    			
    			if(!YssFun.formatDate(filter.getSettleDate()).equalsIgnoreCase("9998-12-31")) {
    				alCon.add(prefix+"FSettleDate = "+dbl.sqlDate(filter.getSettleDate()));
    			}
    			
    			if(filter.getPortCode()!=null&&filter.getPortCode().trim().length()!=0) {
    				alCon.add(prefix+"FPortCode in ("+dbl.sqlString(filter.getPortCode().trim())+")");
    			} 
    			
    			if(filter.getSecurityCode() !=null && filter.getSecurityCode().trim().length()>0) {
    				alCon.add(prefix+"FSecurityCode = "+dbl.sqlString(filter.getSecurityCode().trim()));
    			}  
    			
    			if (filter.getTradeTypeCode() != null && filter.getTradeTypeCode().trim().length()>0 ) { 
    				alCon.add(prefix+"FTradeTypeCode = " +dbl.sqlString(filter.getTradeTypeCode().trim()));
                }
    			
    			if(filter.getExchangeCode() !=null && filter.getExchangeCode().trim().length()>0) {
    				alCon.add(prefix+"FExchangeCode = "+dbl.sqlString(filter.getExchangeCode().trim()));
    			}  
    			
    			if (filter.getCashAccCode() != null && filter.getCashAccCode().trim().length()>0 ) { 
    				alCon.add(prefix+"FCashAccCode = " +dbl.sqlString(filter.getCashAccCode().trim()));
                }
    			
    			if (filter.getSecurityType() != null && filter.getSecurityType().trim().length()>0 ) { 
    				if (!filter.getSecurityType().equalsIgnoreCase("All")) { 
        				alCon.add(prefix+"FSecurityType = " + dbl.sqlString(filter.getSecurityType().trim()));
                    }
                }
    			
    			if (filter.getSettleMoney()>0) { 
    				alCon.add(prefix+"FSettleMoney = " + filter.getSettleMoney());
                }
    			
    			if(filter.getDesc() !=null && filter.getDesc().trim().length()>0) {
    				alCon.add(prefix+"FDesc = "+dbl.sqlString(filter.getDesc().trim()));
    			}   
    		}
    		
			str=YssFun.join((String[])alCon.toArray(new String[]{}), " and ");
        }
        catch(Exception e){
        	throw new YssException("生成筛选条件子句出错！", e);
        }
        
        return str;
    } 
    
    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
    	
    	this.num = rs.getString("Fnum");
        this.securityCode = rs.getString("FsecurityCode");
        this.securityName = rs.getString("FsecurityName");
        this.tradeTypeCode = rs.getString("FtradeTypeCode");
        this.tradeTypeName = rs.getString("FtradeTypeName");
        this.portCode = rs.getString("FportCode");
        this.portName = rs.getString("FportName");
        this.cashAccCode = rs.getString("FcashAccCode");
        this.cashAccName = rs.getString("FcashAccName");
        this.bargainDate = rs.getDate("FbargainDate");
        this.settleDate = rs.getDate("FsettleDate");
        this.settleMoney = rs.getDouble("FsettleMoney");
        this.exchangeCode = rs.getString("FexchangeCode");
        this.exchangeName = rs.getString("FexchangeName");
        this.securityType = rs.getString("FsecurityType");
        this.desc = rs.getString("FDesc");
        
        super.setRecLog(rs);
    }
    
    public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	public boolean isBShow() {
		return BShow;
	}

	public void setBShow(boolean show) {
		BShow = show;
	}

	public TradeBean getFilterType() {
		return FilterType;
	}

	public void setFilterType(TradeBean filterType) {
		FilterType = filterType;
	}

	public String getSRecycled() {
		return sRecycled;
	}

	public void setSRecycled(String recycled) {
		sRecycled = recycled;
	}

	public String getMultAuditString() {
		return multAuditString;
	}

	public void setMultAuditString(String multAuditString) {
		this.multAuditString = multAuditString;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public String getTradeTypeCode() {
		return tradeTypeCode;
	}

	public void setTradeTypeCode(String tradeTypeCode) {
		this.tradeTypeCode = tradeTypeCode;
	}

	public String getTradeTypeName() {
		return tradeTypeName;
	}

	public void setTradeTypeName(String tradeTypeName) {
		this.tradeTypeName = tradeTypeName;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getCashAccCode() {
		return cashAccCode;
	}

	public void setCashAccCode(String cashAccCode) {
		this.cashAccCode = cashAccCode;
	}

	public String getCashAccName() {
		return cashAccName;
	}

	public void setCashAccName(String cashAccName) {
		this.cashAccName = cashAccName;
	}

	public Date getBargainDate() {
		return bargainDate;
	}

	public void setBargainDate(Date bargainDate) {
		this.bargainDate = bargainDate;
	}

	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	public double getSettleMoney() {
		return settleMoney;
	}

	public void setSettleMoney(double settleMoney) {
		this.settleMoney = settleMoney;
	}

	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOldNum() {
		return oldNum;
	}

	public void setOldNum(String oldNum) {
		this.oldNum = oldNum;
	}

}
