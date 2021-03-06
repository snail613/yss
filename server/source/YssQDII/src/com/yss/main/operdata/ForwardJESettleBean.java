package com.yss.main.operdata;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.*;

public class ForwardJESettleBean extends BaseDataSettingBean implements
	IDataSetting  {
	
	private ForwardJESettleBean FilterType = null;
	private String sRecycled = ""; //保存未解析前的字符串
	
	private String num = "";	
	private String tradeNum = "";
    private String settleType = "";
    private java.util.Date settleDate = null;//交割日期
    private String cashAccCode = "";
    private String cashAccName = "";//现金账户名称
    
	private double money;
    private String inOut;
    private String desc;
  
	private String oldNum;

    private boolean BShow = false;
    
    private ForwardJESettleBean forward = null;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getTradeNum() {
		return tradeNum;
	}

	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}

	public String getSettleType() {
		return settleType;
	}

	public void setSettleType(String settleType) {
		this.settleType = settleType;
	}

	public java.util.Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(java.util.Date settleDate) {
		this.settleDate = settleDate;
	}

	public String getCashAccCode() {
		return cashAccCode;
	}

	public void setCashAccCode(String cashAccCode) {
		this.cashAccCode = cashAccCode;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getInOut() {
		return inOut;
	}

	public void setInOut(String inOut) {
		this.inOut = inOut;
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

	public boolean isBShow() {
		return BShow;
	}

	public void setBShow(boolean bShow) {
		BShow = bShow;
	}	
	
	public String getCashAccName() {
		return cashAccName;
	}

	public void setCashAccName(String cashAccName) {
		this.cashAccName = cashAccName;
	}

	
    public ForwardJESettleBean getFilterType() {
		return FilterType;
	}

	public void ForwardJESettleBean(ForwardJESettleBean filterType) {
		FilterType = filterType;
	}

	public String getSRecycled() {
		return sRecycled;
	}

	public void setSRecycled(String recycled) {
		sRecycled = recycled;
	}
	
	public String buildRowStr() throws YssException {
		
		StringBuffer buf = new StringBuffer();
        
        buf.append(this.num).append("\t");
        buf.append(this.tradeNum).append("\t");
        buf.append(this.settleType).append("\t");
        buf.append(format.format(this.settleDate)).append("\t");
        buf.append(this.cashAccCode).append("\t");
        buf.append(this.cashAccName).append("\t");
        buf.append(this.money).append("\t");
        buf.append(this.inOut).append("\t");
        buf.append(this.desc).append("\t");
        
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (forward == null) {
			forward = new ForwardJESettleBean();
			forward.setYssPub(pub);
        }
		String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.num = reqAry[0];
            this.tradeNum = reqAry[1];
            this.settleType = reqAry[2];
            this.settleDate =  YssFun.parseDate(reqAry[3].trim().length()==0?"9998-12-31":reqAry[3]);
            this.cashAccCode = reqAry[4];
            this.money = (reqAry[5].trim().length()==0?0:Double.parseDouble(reqAry[5]));
            this.inOut = reqAry[6];
            if (reqAry[7] != null ){
                if (reqAry[7].indexOf("【Enter】") >= 0){
                     this.desc = reqAry[7].replaceAll("【Enter】", "\r\n");
                }
                else {
                   this.desc = reqAry[7];
                }
            }
            
            this.oldNum = reqAry[8];

            this.checkStateId = Integer.parseInt(reqAry[9]);
            
            if (reqAry[10].equalsIgnoreCase("true")) {
                this.BShow = true;
            } else {
                this.BShow = false;
            } 
            
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new ForwardJESettleBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析远期净额交割数据出错！", e);
        }
	}

	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            String nowDate = YssFun.formatDate(new java.util.Date(), YssCons.YSS_DATETIMEFORMAT).substring(0, 8);
            this.num = "JT" + nowDate +
            dbFun.getNextInnerCode(pub.yssGetTableName("TB_Data_FwTradeJESettle"),
                                   dbl.sqlRight("FNum", 6), "000001",
                                   " where FNum like 'JT"
                                   + nowDate + "%'", 1);
            strSql = "insert into " + pub.yssGetTableName("TB_Data_FwTradeJESettle")
            	+ " (Fnum, FTradeNum, FSettleType, FSettleDate, FCashAccCode, FMoney, FInOut, FDesc,"
            	+ " fcheckstate, fcreator, fcreatetime) "
            	+ " values ("
            	+ dbl.sqlString(this.num) + ","
            	+ dbl.sqlString(this.tradeNum) + "," 
            	+ dbl.sqlString(this.settleType) + "," 
            	+ dbl.sqlDate(this.settleDate) + "," 
            	+ dbl.sqlString(this.cashAccCode) + "," 
            	+ this.money + ","
            	+ dbl.sqlString(this.inOut) + "," 
            	+ dbl.sqlString(this.desc) + "," 
            	+ "0," 
            	+ dbl.sqlString(this.creatorCode) + ","
            	+ dbl.sqlString(this.creatorTime) + ")";

            dbl.executeSql(strSql);
        
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增远期净额交割信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public void checkInput(byte btOper) throws YssException {	
		 
	}
	
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
            strSql = "update " + pub.yssGetTableName("TB_Data_FwTradeJESettle")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where FNum = " + dbl.sqlString(this.num);
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除远期净额交割信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public void checkSetting() throws YssException {
		
		String strSql = ""; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		try {
			conn.setAutoCommit(false); 
			bTrans = true; 
			if (sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) { // 判断传来的内容是否为空
				arrData = sRecycled.split("\r\n"); 
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]); 
					strSql = "update " + pub.yssGetTableName("TB_Data_FwTradeJESettle")
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
			throw new YssException("审核远期净额交割信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
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
            if (sRecycled != null&&!sRecycled.equalsIgnoreCase("")) {
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
                    strSql = "delete from " + pub.yssGetTableName("TB_Data_FwTradeJESettle") 
	                	+ " where FNum = " + dbl.sqlString(this.num);                    
                    dbl.executeSql(strSql);
                }
            }
            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除远期净额交割信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("TB_Data_FwTradeJESettle")
	            	+ " set FTradeNum = " + dbl.sqlString(this.tradeNum)
	            	+ " , FSettleType = " + dbl.sqlString(this.settleType)
	            	+ " , FSettleDATE = " + dbl.sqlDate(this.settleDate)
	            	+ " , FCashAccCode = " + dbl.sqlString(this.cashAccCode)
	            	+ " , FMoney = " + this.money
	            	+ " , FInOut = " + dbl.sqlString(this.inOut)
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
            throw new YssException("修改远期净额交割信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public String getOperValue(String sType) throws YssException {
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
        	
        	strSql = " select a.Fnum, a.FTRADENUM, a.FSettleType, a.FSETTLEDATE, a.FCashAccCode, a.FMONEY, " +
        			" (case when a.FInOut = '1' then '流入' else '流出' end) as FInOut, a.FDesc, a.FCheckstate," +
        			" a.fcreator, a.fcreatetime, a.fcheckuser, a.fchecktime, b.FCreatorName, c.FCheckUserName, d.FCashaccName" +
        			" from " + pub.yssGetTableName("TB_Data_FwTradeJESettle")        
				    + " a left join (select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" 
				    + " left join (select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " 
				    + " left join " + pub.yssGetTableName("Tb_Para_CashAccount") + " d on a.fcashacccode = d.fcashacccode where "
				    + buildFilterStr("a") + " order by a.FCheckState, a.FCreateTime desc";				
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
        	throw new YssException("获取远期净额交割数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	
	public String getListViewData2() throws YssException {
    	String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		String strSql = "";
		try {
			sHeader = "编号";
	        if(this.settleType.equals("0")){
	        	strSql = "select fnum from " + pub.yssGetTableName("Tb_Data_ForwardTrade")
                		 + " where FCheckState = 1 and FTradeType = '20' and FSettleDate = " + dbl.sqlDate(this.settleDate) + " order by FNum ";	        	
	        }else if(this.settleType.equals("1")){
	        	strSql = "select a.FNum as FNum from " + pub.yssGetTableName("TB_Data_FwTradeSettle")
   	         			 + " a left join " + pub.yssGetTableName("Tb_Data_ForwardTrade")
   	         			 + " b on a.ftradenum = b.fnum where a.FCheckState = 1 and a.FSettleDate = "
   	         			 + dbl.sqlDate(this.settleDate) + " and a.FSettleDate < b.FMatureDate order by a.FNum ";
	        }else if(this.settleType.equals("2")){
	        	strSql = "select a.FNum as FNum from " + pub.yssGetTableName("TB_Data_FwTradeSettle")
   	         			 + " a left join " + pub.yssGetTableName("Tb_Data_ForwardTrade")
   	         			 + " b on a.ftradenum = b.fnum where a.FCheckState = 1 and a.FSettleDate = "
   	         			 + dbl.sqlDate(this.settleDate) + " and a.FSettleDate >= b.FMatureDate order by a.FNum ";
	        }else{
	        	strSql = "select fnum from " + pub.yssGetTableName("Tb_Data_ForwardTrade")
       		             + " where FCheckState = 1 and FTradeType = '20' and FMatureDate = " + dbl.sqlDate(this.settleDate) 
	        	         + " union "
	        	         + "select a.FNum as FNum from " + pub.yssGetTableName("TB_Data_FwTradeSettle")
   	         			 + " a left join " + pub.yssGetTableName("Tb_Data_ForwardTrade")
   	         			 + " b on a.ftradenum = b.fnum where a.FCheckState = 1 and a.FSettleDate = "
   	         			 + dbl.sqlDate(this.settleDate) + " and a.FSettleDate < b.FMatureDate "
   	         			 + " union "
   	         			 + "select a.FNum as FNum from " + pub.yssGetTableName("TB_Data_FwTradeSettle")
  	         			 + " a left join " + pub.yssGetTableName("Tb_Data_ForwardTrade")
  	         			 + " b on a.ftradenum = b.fnum where a.FCheckState = 1 and a.FSettleDate = "
  	         			 + dbl.sqlDate(this.settleDate) + " and a.FSettleDate >= b.FMatureDate ";
	        }
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				
				bufShow.append((rs.getString("FNum") + "").trim())
						.append("\t");			
				
				bufShow.append(YssCons.YSS_LINESPLITMARK);		
				
				setAttr(rs);				
				
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
			}
		
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}
	
			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
	
		    return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
		    
		} catch (Exception e) {
			throw new YssException("获取远期外汇信息出错" + "\r\n" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
	
	public void setAttr(ResultSet rs) throws SQLException, SQLException,YssException {
	    this.num = rs.getString("FNum");
	}
	
	public String buildFilterStr(String prefix) throws YssException {
        String str="";
    	
    	try {
 			ArrayList alCon=new ArrayList();
			
			alCon.add(" 1=1 ");
			
    		if (this.FilterType!=null) {
    			ForwardJESettleBean filter = this.FilterType;
    			
    			if (filter.isBShow() == false) {
    				alCon.add(" 1=2 ");
                }
    			
    			if (prefix==null) {
    				prefix="";
    			} else if (!prefix.trim().endsWith(".")) {
    				prefix+=".";
    			}
    			
    			if (filter.getSettleType() != null && filter.getSettleType().trim().length()>0 ) { 
    				if (!filter.getSettleType().equalsIgnoreCase("All")) { 
        				alCon.add(prefix+"FSettleType = " + dbl.sqlString(filter.getSettleType().trim()));
                    }
                }
    			
    			if (filter.getCashAccCode() != null && filter.getCashAccCode().trim().length()>0 ) { 
    				if (!filter.getCashAccCode().equalsIgnoreCase("All")) { 
        				alCon.add(prefix+"FCashAccCode = " + dbl.sqlString(filter.getCashAccCode().trim()));
                    }
                }
                
    			if (filter.getInOut() != null && filter.getInOut().trim().length()>0 ) { 
    				if (!filter.getInOut().equalsIgnoreCase("All")) { 
        				alCon.add(prefix+"FInOut = " + dbl.sqlString(filter.getInOut().trim()));
                    }
                }
    			
    			if(!YssFun.formatDate(filter.getSettleDate()).equalsIgnoreCase("9998-12-31")) {
    				alCon.add(prefix+"FSETTLEDATE = "+dbl.sqlDate(filter.getSettleDate()));
    			}
    			
    			if(filter.getTradeNum() !=null && filter.getTradeNum().trim().length()>0) {
    				alCon.add(prefix+"FTRADENUM = "+dbl.sqlString(filter.getTradeNum()));
    			}
    			
    			if (filter.getMoney()>0) { 
    				alCon.add(prefix+"FMONEY = " + filter.getMoney());
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
    	this.tradeNum = rs.getString("FTRADENUM");
    	this.settleType = rs.getString("FSettleType");
        this.settleDate = rs.getDate("FSETTLEDATE");
        this.cashAccCode = rs.getString("FCashAccCode");
        this.cashAccName = rs.getString("FcashAccName");
        this.money = rs.getDouble("FMONEY");
        this.inOut = rs.getString("FInOut");
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
	
}
