package com.yss.main.operdata;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * add by 20110506 STORY #741 客户要求增加投资的基金做分红转投业务的菜单
 * @author guyichuan
 *
 */
public class DividendInvestBean extends BaseDataSettingBean implements
	IDataSetting{

	private DividendInvestBean FilterType = null;
	private String sRecycled = ""; 		//保存未解析前的字符串
	private String fNum="";				//股票分红转投信息编号
	
    private String securityCode = "";	//证券代码
    private String portCode = "";		//组合代码
    private Date dividendDate = null;	//除权日
    private String curyCode = "";		//分红币种
    private String divdendType = "";	//分红类型
    private Date recordDate = null;		//权益登记日
    private Date bargainDate = null;	//业务日期

    private Date investDate = null;		//转投日期
    private double investMoney ;		//转投金额
    private double changeMoney ;		//调整金额
    private double investNum ;			//转投份额
    private String broker = " ";			//交易券商
    private String brokerName="";       //交易券商名称
    private String status="";           //标记是否是筛选
    
    private String oldNum;
    private boolean BShow = false;
    
    public boolean isBShow() {
		return BShow;
	}

	public void setBShow(boolean bShow) {
		BShow = bShow;
	}

	public String getOldNum() {
		return oldNum;
	}

	public void setOldNum(String oldNum) {
		this.oldNum = oldNum;
	}

	private DividendInvestBean invest = null;
    
public String buildRowStr() throws YssException {
	StringBuffer buf = new StringBuffer();
		buf.append(this.fNum).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.dividendDate==null?" \t":YssFun.formatDate(this.dividendDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.curyCode).append("\t");
        buf.append(this.divdendType).append("\t");
        buf.append(this.recordDate==null?" \t":YssFun.formatDate(this.recordDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.bargainDate==null?" \t":YssFun.formatDate(this.bargainDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.investDate==null?" \t":YssFun.formatDate(this.investDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.investMoney).append("\t");
        buf.append(this.changeMoney).append("\t");
        buf.append(this.investNum).append("\t");
        buf.append(this.broker).append("\t");
        buf.append(this.brokerName).append("\t");
        
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (invest == null) {
			invest = new DividendInvestBean();
			invest.setYssPub(pub);
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
            
            this.fNum=reqAry[0];
            this.securityCode = reqAry[1];
            this.portCode = reqAry[2];
            this.dividendDate =  YssFun.parseDate(reqAry[3].trim().length()==0?"9998-12-31":reqAry[3]);
            this.curyCode = reqAry[4];
            this.divdendType = reqAry[5];
            this.recordDate =  YssFun.parseDate(reqAry[6].trim().length()==0?"9998-12-31":reqAry[6]);
            this.bargainDate =  YssFun.parseDate(reqAry[7].trim().length()==0?"9998-12-31":reqAry[7]);
            this.investDate =  YssFun.parseDate(reqAry[8].trim().length()==0?"9998-12-31":reqAry[8]);
            
            this.investMoney = (reqAry[9].trim().length()==0?0:Double.parseDouble(reqAry[9].replaceAll(",", "")));
            this.changeMoney = (reqAry[10].trim().length()==0?0:Double.parseDouble(reqAry[10].replaceAll(",", ""))); 
            this.investNum = (reqAry[11].trim().length()==0?0:Double.parseDouble(reqAry[11].replaceAll(",", ""))); 
            this.broker = (reqAry[12]==null?" ":reqAry[12]);
            this.oldNum=reqAry[13];
            this.status=reqAry[14];
            if (reqAry[15].equalsIgnoreCase("true")) {
                this.BShow = true;
            } else {
                this.BShow = false;
            }
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new DividendInvestBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析股票分红转投数据出错！", e);
        }

	}
	
	public DividendInvestBean getInvest() {
		return invest;
	}

	public void setInvest(DividendInvestBean invest) {
		this.invest = invest;
	}

	public DividendInvestBean getFilterType() {
		return FilterType;
	}

	public void setFilterType(DividendInvestBean filterType) {
		FilterType = filterType;
	}

	public String getsRecycled() {
		return sRecycled;
	}

	public void setsRecycled(String sRecycled) {
		this.sRecycled = sRecycled;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public Date getDividendDate() {
		return dividendDate;
	}

	public void setDividendDate(Date dividendDate) {
		this.dividendDate = dividendDate;
	}

	public String getCuryCode() {
		return curyCode;
	}

	public void setCuryCode(String curyCode) {
		this.curyCode = curyCode;
	}

	public String getDivdendType() {
		return divdendType;
	}

	public void setDivdendType(String divdendType) {
		this.divdendType = divdendType;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public Date getBargainDate() {
		return bargainDate;
	}

	public void setBargainDate(Date bargainDate) {
		this.bargainDate = bargainDate;
	}

	public Date getInvestDate() {
		return investDate;
	}

	public void setInvestDate(Date investDate) {
		this.investDate = investDate;
	}

	public double getInvestMoney() {
		return investMoney;
	}

	public void setInvestMoney(double investMoney) {
		this.investMoney = investMoney;
	}

	public double getChangeMoney() {
		return changeMoney;
	}

	public void setChangeMoney(double changeMoney) {
		this.changeMoney = changeMoney;
	}

	public double getInvestNum() {
		return investNum;
	}

	public String getfNum() {
		return fNum;
	}

	public void setfNum(String fNum) {
		this.fNum = fNum;
	}

	public void setInvestNum(double investNum) {
		this.investNum = investNum;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}
	//新增
	public String addSetting() throws YssException {
		StringBuffer bufSql = new StringBuffer();
		StringBuffer bufCheckSql=new StringBuffer();
		String strSql="";
		
        boolean bTrans = false; // 代表是否开始了事务
        ResultSet rs=null;
        //存在相同的信息就给出提示
        bufCheckSql.append(" select * from");
        bufCheckSql.append("(select * from "+ pub.yssGetTableName("tb_data_dividendinvest"));
        bufCheckSql.append(" where FSecurityCode="+dbl.sqlString(this.securityCode));
        bufCheckSql.append(" and FPortCode="+dbl.sqlString(this.portCode));
        bufCheckSql.append(" and FDividendDate="+dbl.sqlDate(this.dividendDate));
        bufCheckSql.append(" and FCuryCode="+dbl.sqlString(this.curyCode));
        bufCheckSql.append(" and FRecordDate="+dbl.sqlDate(this.recordDate));
        bufCheckSql.append(" )a");
        bufCheckSql.append(" inner join (select FVocCode,FVocName,FVocTypeCode from  Tb_Fun_Vocabulary where FVocName="+dbl.sqlString(this.divdendType)+") n");
        bufCheckSql.append(" on a.FDivdendType = n.FVocCode and n.FVocTypeCode = 'DivdendType' ");
        try{
        	rs=dbl.openResultSet(bufCheckSql.toString());
        	if(rs.next()){
        		throw new YssException("存在对相同股票分红权益数据的分红转投设置！");
        	}
        }catch(Exception e){
        	throw new YssException(e.getMessage());
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
        
        
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            String nowDate=YssFun.formatDate(new java.util.Date(),
                    "yyyyMMdd");
            //fNum主键
            this.fNum = "ST" + nowDate +
            dbFun.getNextInnerCode(pub.yssGetTableName("TB_Data_DividendInvest"),
                                   dbl.sqlRight("FNum", 6), "000001",
                                   " where FNum like 'ST"
                                   + nowDate + "%'", 1); 
            
            strSql="select FVocCode as DividendType,FVocName ,FVocTypeCode from Tb_Fun_Vocabulary"+
                      " where FVocTypeCode = 'DivdendType' and FVocName="+dbl.sqlString(this.divdendType);
            rs=dbl.openResultSet(strSql);
            if(rs.next()){
            	this.divdendType=rs.getString("DividendType");
            }
            
            bufSql.append("insert into "+pub.yssGetTableName("TB_Data_DividendInvest"));
            bufSql.append("(FNum,FSecurityCode,FPortCode,FDividendDate,FCuryCode,FDivdendType,");
            bufSql.append("FRecordDate,FBargainDate,FInvestDate,FInvestMoney,FChangeMoney,FInvestNum,");
            bufSql.append("FBroker,FCheckState,FCreator,FCreateTime)");
            bufSql.append(" values (");
            bufSql.append(dbl.sqlString(this.fNum)+",");
            bufSql.append(dbl.sqlString(this.securityCode)+",");
            bufSql.append(dbl.sqlString(this.portCode)+",");
            bufSql.append(dbl.sqlDate(this.dividendDate)+",");
            bufSql.append(dbl.sqlString(this.curyCode)+",");
            bufSql.append(Integer.parseInt(this.divdendType)+",");
            bufSql.append(dbl.sqlDate(this.recordDate)+",");
            bufSql.append(dbl.sqlDate(this.bargainDate)+",");
            bufSql.append(dbl.sqlDate(this.investDate)+",");
            bufSql.append(this.investMoney +",");
            bufSql.append(this.changeMoney+",");
            bufSql.append(this.investNum+",");
            bufSql.append(dbl.sqlString(this.broker)+",");
            bufSql.append(0+",");                             //默认未审核
            bufSql.append(dbl.sqlString(this.creatorCode)+",");
            bufSql.append(dbl.sqlString(this.creatorTime));    
            bufSql.append(")");
            
            dbl.executeSql(bufSql.toString());
        
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增股票分红转投信息出错", e);
        } finally {
        	dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
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
			if (sRecycled != null&&(!sRecycled.equalsIgnoreCase("")) ) { // 判断传来的内容是否为空
				arrData = sRecycled.split("\r\n"); 
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]); 
					strSql = "update " + pub.yssGetTableName("Tb_Data_DividendInvest")
                	+ " set FCheckState = case fcheckstate when 0 then 1 else 0 end" 
                	+ ", FCheckUser = " 
                	+ dbl.sqlString(this.checkUserCode)
                	+ ", FCheckTime = "
                	+ dbl.sqlString(this.checkTime)
                	+ " where FNum = " + dbl.sqlString(this.fNum);
	
					dbl.executeSql(strSql); // 执行更新操作
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核股票分红转投信息出错", e);
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
            
            strSql = "update " + pub.yssGetTableName("Tb_Data_DividendInvest")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where FNum = " + dbl.sqlString(this.fNum);
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除股票分红转投信息出错", e);
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
            if (sRecycled != null&&!sRecycled.equalsIgnoreCase("") ) {
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
                    strSql = "delete from " + pub.yssGetTableName("Tb_Data_DividendInvest") 
	                	+ " where FNum = " + dbl.sqlString(this.fNum);
                    
                    dbl.executeSql(strSql);
                }
            }
            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除股票分红转投信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Data_DividendInvest")
	            	+ " set FBargainDate = " + dbl.sqlDate(this.bargainDate)
	            	+ " , FInvestDate = " + dbl.sqlDate(this.investDate)
	            	+ " , FInvestMoney = " + this.investMoney
	            	+ " , FChangeMoney = " + this.changeMoney
	            	+ " , FInvestNum = " + this.investNum
	            	+ " , FBroker="+ dbl.sqlString(this.broker)
	            	+ " , fcreator = " + dbl.sqlString(this.creatorCode)
	            	+ " , fcreatetime = " + dbl.sqlString(this.creatorTime)  
					+ " where FNum = " + dbl.sqlString(this.oldNum);
            dbl.executeSql(strSql); 
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改股票分红转投信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
		return null;
	}


	public String getOperValue(String sType) throws YssException {
		if(sType.equals("Info")){
        	return getInfo();
        }
		return "";
	}
	public String getInfo() throws YssException {

      return null;
	}
	public String getListViewData1() throws YssException {
		// TODO Auto-generated method stub
		StringBuffer bufSql=new StringBuffer(); // 定义一个存放sql语句的字符串
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try { 
        	sHeader = this.getListView1Headers();
        	
        	bufSql.append(" select * from");
        	bufSql.append(" (select * from "+pub.yssGetTableName("tb_data_dividendinvest")+buildFilterSql()+" )a");
        	bufSql.append(" left join (select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) b");
        	bufSql.append(" on a.FCreator = b.FUserCode");
        	bufSql.append(" left join  (select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList) c ");
        	bufSql.append(" on a.FCheckUser = c.FUserCode");
        	bufSql.append(" left join (select FBrokerCode,FBrokerName from ");
        	bufSql.append( pub.yssGetTableName("Tb_Para_Broker")+" )d on d.FBrokerCode=a.fbroker");
        	bufSql.append(" left join (select FVocCode,FVocName,FVocTypeCode from Tb_Fun_Vocabulary)n");
        	bufSql.append(" on a.FDivdendType = n.FVocCode and n.FVocTypeCode = 'DivdendType'");
        	bufSql.append(" order by a.FCheckState, a.FCreateTime desc");
				
            rs = dbl.openResultSet(bufSql.toString());
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
        	throw new YssException("获取股票分红转投数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
    	this.fNum=rs.getString("FNum");
    	this.securityCode=rs.getString("FSecurityCode");
    	this.portCode=rs.getString("FPortCode");
    	this.dividendDate=rs.getDate("FDividendDate");
    	this.curyCode=rs.getString("FCuryCode");
    	this.divdendType=rs.getString("FVocName");//分红的类型名称
    	this.recordDate=rs.getDate("FRecordDate");
    	this.bargainDate=rs.getDate("FBargainDate");
    	this.investDate=rs.getDate("FInvestDate");
    	this.investMoney=rs.getDouble("FInvestMoney");
    	this.changeMoney=rs.getDouble("FChangeMoney");
    	this.investNum=rs.getDouble("FInvestNum");
    	this.broker=rs.getString("FBroker");
    	this.brokerName=rs.getString("FBrokerName");
    	
        super.setRecLog(rs);
    }
	 /**
     * 筛选条件
     * @throws YssException
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.status!=null&&this.status.trim().equals("YssFilter")) {
            sResult = " where 1=1";
            /*if (this.filterType.strIsOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }*/
            if (this.securityCode!=null&&this.securityCode.trim().length()>0) {
                sResult = sResult + " and FSecurityCode="+dbl.sqlString(this.securityCode);                
            }
            if (this.portCode!=null&&this.portCode.trim().length()>0) {
                sResult = sResult + " and FPortCode="+dbl.sqlString(this.portCode);                
            }
            if (this.dividendDate!=null&&!YssFun.formatDate(this.dividendDate).equals("9998-12-31")) {
                sResult = sResult + " and FDividendDate="+dbl.sqlDate(this.dividendDate);                
            }
            if (this.curyCode!=null&&this.curyCode.trim().length()>0) {
                sResult = sResult + " and FCuryCode="+dbl.sqlString(this.curyCode);                
            }
            if (this.recordDate!=null&&!YssFun.formatDate(this.recordDate).equals("9998-12-31")) {
                sResult = sResult + " and FRecordDate="+dbl.sqlDate(this.recordDate);                
            }
            if (this.bargainDate!=null&&!YssFun.formatDate(this.bargainDate).equals("9998-12-31")) {
                sResult = sResult + " and FBargainDate="+dbl.sqlDate(this.bargainDate);                
            }
            if (this.investDate!=null&&!YssFun.formatDate(this.investDate).equals("9998-12-31")) {
                sResult = sResult + " and FInvestDate="+dbl.sqlDate(this.investDate);                
            }
            if (this.broker!=null&&this.broker.trim().length()>0) {
                sResult = sResult + " and FBroker="+dbl.sqlString(this.broker);                
            }

       }
        return sResult;
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
