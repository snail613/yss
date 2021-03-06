package com.yss.main.operdata;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.cashmanage.PrincipalExtBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.*;

public class ForwardSettleBean extends BaseDataSettingBean implements
	IDataSetting  {
	
	private ForwardSettleBean FilterType = null;
	private String sRecycled = ""; //保存未解析前的字符串
	
	private String num = "";
    private String tradeNum = "";
    private java.util.Date settleDate = null;//提取日期
    private double sCapMoney;
    private double bCapMoney;
    private String desc;
  
	private String oldNum;

    private boolean BShow = false;
    
    private ForwardSettleBean forward = null;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 

    public ForwardSettleBean getFilterType() {
		return FilterType;
	}

	public void ForwardSettleBean(ForwardSettleBean filterType) {
		FilterType = filterType;
	}

	public String getSRecycled() {
		return sRecycled;
	}

	public void setSRecycled(String recycled) {
		sRecycled = recycled;
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

	public void setBShow(boolean show) {
		BShow = show;
	}

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

	public java.util.Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(java.util.Date settleDate) {
		this.settleDate = settleDate;
	}

	public double getSCapMoney() {
		return sCapMoney;
	}

	public void setSCapMoney(double capMoney) {
		sCapMoney = capMoney;
	}

	public double getBCapMoney() {
		return bCapMoney;
	}

	public void setBCapMoney(double capMoney) {
		bCapMoney = capMoney;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String buildRowStr() throws YssException {
		
		StringBuffer buf = new StringBuffer();
        
        buf.append(this.num).append("\t");
        buf.append(this.tradeNum).append("\t");
        buf.append(format.format(this.settleDate)).append("\t");
        buf.append(this.bCapMoney).append("\t");
        buf.append(this.sCapMoney).append("\t");
        buf.append(this.desc).append("\t");
        
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (forward == null) {
			forward = new ForwardSettleBean();
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
            this.settleDate =  YssFun.parseDate(reqAry[2].trim().length()==0?"9998-12-31":reqAry[2]);
            this.bCapMoney = (reqAry[3].trim().length()==0?0:Double.parseDouble(reqAry[3]));
            this.sCapMoney = (reqAry[4].trim().length()==0?0:Double.parseDouble(reqAry[4]));          
            if (reqAry[5] != null ){
                if (reqAry[5].indexOf("【Enter】") >= 0){
                     this.desc = reqAry[5].replaceAll("【Enter】", "\r\n");
                }
                else {
                   this.desc = reqAry[5];
                }
            }
            
            this.oldNum = reqAry[6];

            this.checkStateId = Integer.parseInt(reqAry[7]);
            
            if (reqAry[8].equalsIgnoreCase("true")) {
                this.BShow = true;
            } else {
                this.BShow = false;
            } 
            
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new ForwardSettleBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析远期交割数据出错！", e);
        }

	}

	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            String nowDate=YssFun.formatDate(new java.util.Date(),
                    YssCons.YSS_DATETIMEFORMAT).
                    substring(0, 8);
            this.num = "ST" + nowDate +
            dbFun.getNextInnerCode(pub.yssGetTableName("TB_Data_FwTradeSettle"),
                                   dbl.sqlRight("FNum", 6), "000001",
                                   " where FNum like 'ST"
                                   + nowDate + "%'", 1);
            strSql = "insert into " + pub.yssGetTableName("TB_Data_FwTradeSettle")
            	+ " (Fnum, FTradeNum, FSETTLEDATE, FBCAPMONEY, FSCAPMONEY, FDESC,"
            	+ " fcheckstate, fcreator, fcreatetime) "
            	+ " values ("
            	+ dbl.sqlString(this.num) + ","
            	+ dbl.sqlString(this.tradeNum) + "," 
            	+ dbl.sqlDate(this.settleDate) + "," 
            	+ this.bCapMoney + ","
            	+ this.sCapMoney + ","
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
            throw new YssException("新增远期交割信息出错", e);
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
            
            strSql = "update " + pub.yssGetTableName("TB_Data_FwTradeSettle")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where FNum = " + dbl.sqlString(this.num);
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除远期交割信息出错", e);
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
					strSql = "update " + pub.yssGetTableName("TB_Data_FwTradeSettle")
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
			throw new YssException("审核远期交割信息出错", e);
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
                    strSql = "delete from " + pub.yssGetTableName("TB_Data_FwTradeSettle") 
	                	+ " where FNum = " + dbl.sqlString(this.num);
                    
                    dbl.executeSql(strSql);
                }
            }
            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除远期交割信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("TB_Data_FwTradeSettle")
	            	+ " set FTRADENUM = " + dbl.sqlString(this.tradeNum)
	            	+ " , FBCAPMONEY = " + this.bCapMoney
	            	+ " , FSCAPMONEY = " + this.sCapMoney
	            	+ " , FSettleDATE = " + dbl.sqlDate(this.settleDate)
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
            throw new YssException("修改远期交割信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public String getOperValue(String sType) throws YssException {
        if(sType.equals("Info")){
        	return getInfo();
        }
		return "";

    }
	
	public String getInfo() throws YssException {
		String strSql = ""; // 定义一个存放sql语句的字符串
        ResultSet rs = null;
        double sCapMoney = 0.00;
        String matureDate = "";
        double tradeAmount = 0.00;
        /**BigDecimal price = null;//shashijie 2011.03.10 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数*/
        BigDecimal matureMoney = null;
        BigDecimal price = null;
        BigDecimal nOutMoney = null;
        BigDecimal yOutMoney = null;
        BigDecimal tradeAmount1 = null;
        BigDecimal matureMoney1 = null;
        BigDecimal offMoney = null;
        /**~~~~~~~~~~~~~~~~~~~~~~~~~~end~~~~~~~~~~~~~~~~~~~~~~~~~*/
        /*double nOutMoney = 0.00;
        double yOutMoney = 0.00;
        double matureMoney = 0.00;
        double price = 0.0;*/
        
        try {
        	strSql = " select distinct * from " +
        			 " (select FNum, FTRADEAMOUNT, FMATUREMONEY, FTrustPrice, FMATUREDATE, FSECURITYCODE from " + pub.yssGetTableName("Tb_Data_ForwardTrade")
        			 + " where FCheckState = 1 ) a" 
        			 + " left join (select FOffNum, sum(FTRADEAMOUNT) as FTRADEAMOUNT1, sum(FMATUREMONEY) as FMATUREMONEY1 from " + pub.yssGetTableName("Tb_Data_ForwardTrade") 
        			 + "  where FCheckState = 1 and FTradeType = '21' group by FOffNum) e on a.Fnum = e.FOffnum "      			 
        			 + " left join (select FTradeNum, sum(FSCapMoney) as FSCapMoney from " + pub.yssGetTableName("Tb_Data_fwtradesettle")
        			 + " where FCheckState = 1 group by FTradeNum  ) b on a.FNum= b.FTradeNum left join (select FSecurityCode, FBuyCury, FSaleCury from " + pub.yssGetTableName("Tb_Para_Forward")
                     + " where FCheckState = 1 ) c on a.FSECURITYCODE = c.FSecurityCode left join (select FSecurityCode, FTradeCury from " +  pub.yssGetTableName("Tb_Para_Security")
                     + " where FCheckState = 1 ) d on a.FSECURITYCODE = d.FSecurityCode " + " where a.FNum = " + dbl.sqlString(this.tradeNum);  
			rs = dbl.openResultSet(strSql);
            while(rs.next()){
            	price = rs.getBigDecimal("FTrustPrice");//shashijie 2011.03.10 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数
            	matureDate = format.format(rs.getDate("FMATUREDATE"));
            	tradeAmount = rs.getDouble("FTRADEAMOUNT");
            	matureMoney = rs.getBigDecimal("FMATUREMONEY");//shashijie 2011.03.10 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数
            	sCapMoney = rs.getDouble("FSCapMoney");
            	yOutMoney = new BigDecimal(sCapMoney);//shashijie 2011.03.10 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数
            	tradeAmount1 = new BigDecimal(rs.getDouble("FTRADEAMOUNT1"));
            	matureMoney1 = new BigDecimal(rs.getDouble("FMATUREMONEY1"));
            	if (rs.getString("FTradeCury").equalsIgnoreCase(rs.getString("FBuyCury"))) {
            		nOutMoney = YssD.subD(new BigDecimal(tradeAmount), tradeAmount1, yOutMoney);//shashijie 2011.03.10 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数
            		offMoney = tradeAmount1;
            	} else if (rs.getString("FTradeCury").equalsIgnoreCase(rs.getString("FSaleCury"))) {
            		nOutMoney = YssD.subD(matureMoney, matureMoney1, yOutMoney);//shashijie 2011.03.10 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数
            		offMoney = matureMoney1;
            	}
            }
            return matureDate + "\t" + price + "\t" + yOutMoney + "\t" + nOutMoney + "\t" + offMoney;
        }
        catch(Exception e){
        	throw new YssException("获取远期交割数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
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
        	
        	strSql = " select distinct * from " + pub.yssGetTableName("TB_Data_FwTradeSettle")        
				    + " a left join (select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" 
				    + " left join (select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode where"
				    + buildFilterStr("a")
				    + " order by a.FCheckState, a.FCreateTime desc";
				
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
        	throw new YssException("获取远期交割数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	
	public String buildFilterStr(String prefix) throws YssException {
        String str="";
    	
    	try {
 			ArrayList alCon=new ArrayList();
			
			alCon.add(" 1=1 ");
			
    		if (this.FilterType!=null) {
    			ForwardSettleBean filter = this.FilterType;
    			
    			if (filter.isBShow() == false) {
    				alCon.add(" 1=2 ");
                }
    			
    			if (prefix==null) {
    				prefix="";
    			} else if (!prefix.trim().endsWith(".")) {
    				prefix+=".";
    			}
    			
    			if(!YssFun.formatDate(filter.getSettleDate()).equalsIgnoreCase("9998-12-31")) {
    				alCon.add(prefix+"FSETTLEDATE = "+dbl.sqlDate(filter.getSettleDate()));
    			}
    			
    			if(filter.getTradeNum() !=null && filter.getTradeNum().trim().length()>0) {
    				alCon.add(prefix+"FTRADENUM = "+dbl.sqlString(filter.getTradeNum()));
    			}
    			
    			if (filter.getBCapMoney()>0) { 
    				alCon.add(prefix+"FBCAPMONEY = " + filter.getBCapMoney());
                }
    			
    			if (filter.getSCapMoney()>0) { 
    				alCon.add(prefix+"FSCAPMONEY = " + filter.getSCapMoney());
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
        this.settleDate = rs.getDate("FSETTLEDATE");
        this.bCapMoney = rs.getDouble("FBCAPMONEY");
        this.sCapMoney = rs.getDouble("FSCAPMONEY");
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
}



