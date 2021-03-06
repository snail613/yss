package com.yss.main.operdeal.report.repsafe;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * add by huangqirong 2012-03-02 story #1964
 * 中国人寿保险股份有限公司 - 应收利息情况表
 * */
public class SafeAccBean extends BaseBuildCommonRep {
	protected CommonRepBean repBean;
	private String startDate;
	private String endDate;
	private String portCodes;
	private String currency;
	
	
	/*
	 * 合计
	 * */
	private double firstBalances = 0;	
	private double lastBalances =0;
	private double adds = 0;
	private double minuses = 0;
	
	
	/*
	 * 科目代码
	 * */
	private String [] acctCode1= new String[]{"11320101","11320102","1132020101","113206"}; 
	private String [] acctCode2 = new String[]{"113203"};
	
	
	/*
	 * distinctPortcode 去除重复组合代码
	 * */
	private Hashtable distinctPortcode = new Hashtable();
	
	/*
	 * 已统计套帐表
	 * groupcode - setcode
	 * */
	private Hashtable alreadySetCode = new Hashtable();
	
	/*
	 * groupcode - portcode - assetcode - setCode
	 * */
	private Hashtable groupPortAssetCodeSetCode = new Hashtable();
	
	
	
	/**完成初始化
     * initBuildReport
     *
     * @param bean BaseBean: 通用报表类
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        repBean = (CommonRepBean) bean;
        //解析前台传入的条件字符串
        this.parseRowStr(this.repBean.getRepCtlParam());
    }
    
    
    /**
     * 将前台传入的条件进行解析
     * @param sRowStr String: 使用协议包装的条件字符串
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        try {
            if (sRowStr.equals("")) {
                return;
            }
            String reqAry[] = null;
            reqAry = sRowStr.split("\n"); //这里是要获得参数
            this.startDate = reqAry[0].split("\r")[1];
            this.endDate = reqAry[1].split("\r")[1];
            this.portCodes = reqAry[2].split("\r")[1];
            this.currency = reqAry[3].split("\r")[1];            
        } catch (Exception e) {
            throw new YssException("解析报表创建条件出错！", e);
        }
    }
    
    /**
     * buildReport
     *
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        sResult = buildShowData();
        return sResult;
    }
    
    /*
	 * 日期转字符串yyyy-MM-dd格式
	 * */
	private String getStringByDate(Date date,String format) throws YssException {
		String sDate = new SimpleDateFormat(format).format(date);
		return sDate;
	}
    
    private String buildShowData() throws YssException {
		// TODO Auto-generated method stub
    	StringBuffer result=new StringBuffer();
    	//this.discticePortCode(this.portCodes,",");
    	this.getGroupPortAssetSetCodes();
    	String [] sdate = this.startDate.split("-");    	
    	String [] edate = this.endDate.split("-");
    	
    	String nowYear = getStringByDate(new java.util.Date(),"yyyy-MM-dd");
    	
    	int year1 = Integer.parseInt(sdate[0]); 
    	int year2 = Integer.parseInt(nowYear.split("-")[0]);
    	
    	String emaxDay = YssFun.formatDate(this.getStringData("select last_day(to_date("+dbl.sqlString(this.endDate)+",'yyyy-MM-dd')) as FlastDay from dual","FlastDay"), "yyyy-MM-dd");
    	
    	if(year1 > year2)
    		throw new YssException("请设置完整的会计期间！");
    	
    	if(year1 == year2 && Integer.parseInt(edate[1]) > Integer.parseInt(nowYear.split("-")[1]))
    		throw new YssException("请设置完整的会计期间！");
    	
    	if( !sdate[0].equalsIgnoreCase(edate[0]))
    		throw new YssException("报表查询不支持跨年操作！");
    	
    	if(!this.startDate.endsWith("-01"))
    		throw new YssException("请设置完整的会计期间！");
    	
    	if(!this.endDate.endsWith(emaxDay.split("-")[2]))
    		throw new YssException("请设置完整的会计期间！");
    	
    	if(Integer.parseInt(sdate[0]) > Integer.parseInt(edate[0]))
    		throw new YssException("请设置完整的会计期间！");
    		
    	if(Integer.parseInt(sdate[0]) > Integer.parseInt(edate[0]) && Integer.parseInt(sdate[1]) > Integer.parseInt(edate[1]))
    		throw new YssException("请设置完整的会计期间！");
    	
		try {			
			for (int i = 0; i < acctCode1.length; i++) {
				result.append(this.getAcctData(acctCode1[i])); //获取某一科目数据
			}
			
			String tempNumber =this.buildRowCompResult("0\t0\t0\t0")+"\r\n";
			for (int i = 0; i < 10; i++) {
				
				result.append(tempNumber);
			}
			
			if(result.length() > 0)
				result.setLength(result.length() - 2);			
			
			result.append("\f\n18,-1,1,-1\f\f");
			
			for (int i = 0; i < acctCode2.length; i++) {
				result.append(this.getAcctData(acctCode2[i])); //获取某一科目数据
			}
			
			
			for (int i = 0; i < 2; i++) {
				
				result.append(tempNumber);
			}
			
			if(result.length() > 0)
				result.setLength(result.length() - 2);
			
			
			String tempTotal = "";
			tempTotal = this.firstBalances + "\t" + this.adds+ "\t" + this.minuses + "\t" + this.lastBalances;
			tempTotal = this.buildRowCompResult(tempTotal)+"\r\n";
			result.append("\f\n25,-1,1,-1\f\f").append(tempTotal);
			this.firstBalances = 0; 
			this.adds = 0;
			this.minuses = 0;
			this.lastBalances = 0;
			
		} catch (Exception e) {
			throw new YssException("获取中行人寿应收利息出错： \n" + e.getMessage());			
		} finally {
			
		}    	
		return result.toString();
	}
    
    private String oldPrefix ="";
    
    /*
	 * 修改表前缀
	 * */
	private void setPreFixTb(String tbFix){
		this.oldPrefix = pub.getPrefixTB();			
		if(!tbFix.equalsIgnoreCase(this.oldPrefix)){			
			pub.setPrefixTB(tbFix);
			pub.setAssetGroupCode(tbFix);
		}
	}
    
    /**
     * 某一个科目的数据
     * */
    private String getAcctData(String acctCode  )  throws YssException{
    	Enumeration e = this.groupPortAssetCodeSetCode.keys();
    	this.alreadySetCode.clear(); //清空某科目已执行记录
    	StringBuffer result = new StringBuffer();
    	StringBuffer tempData = new StringBuffer();
    	
    	String [] date = this.startDate.split("-");
    	String [] edate = this.endDate.split("-");
    			
		double firstBalance = 0 ;
		double lastBalance = 0 ;
		double add = 0 ;
		double minus = 0;
		
		while(e.hasMoreElements()){
			String key = (String) e.nextElement();
			String [] groupPortAssetSetCodes = key.split("-");
			
			
			/*已经统计过不再进行统计*/
			if(groupPortAssetSetCodes.length <= 3 )
				continue;
			if(this.alreadySetCode.contains(groupPortAssetSetCodes[3]))
				continue;
			
			this.setPreFixTb(groupPortAssetSetCodes[0]);
			
			BaseOperDeal operDeal = new BaseOperDeal();
			operDeal.setYssPub(pub);			
			double baseRate = operDeal.getCuryRate(YssFun.parseDate(this.endDate), this.currency, groupPortAssetSetCodes[1], "base");
			double portRate = operDeal.getCuryRate(YssFun.parseDate(this.endDate), this.currency, groupPortAssetSetCodes[1], "port");	/*期初汇率*/
			
			this.setPreFixTb(this.oldPrefix);
			
			baseRate = baseRate == 0 ? 1 : baseRate ;
			
			portRate = portRate == 0 ? 1 : portRate ;
			
			/* 期初 */
			String sql1 = " select sum(FBStartBal * " +portRate+ " / "+ baseRate +") as total from (" +
						"select Fcurcode,FBStartBal " +" from a"+date[0]+groupPortAssetSetCodes[3]+"Lbalance al  where Facctcode="+dbl.sqlString(acctCode)+" and Fmonth= "+Integer.parseInt(edate[1])+" ) alb " ;
			
			/* 期末 */
			String sql2 = " select sum(alb.FBEndBal * " + portRate + "/"+baseRate+") as total from (" +
						"select * from a"+edate[0]+groupPortAssetSetCodes[3]+"Lbalance al  where Facctcode="+dbl.sqlString(acctCode)+" and Fmonth="+Integer.parseInt(edate[1])+" ) alb " ;
			
			/* 本年增加 */
			String sql3 = " select sum(alb.FBAccDebit * " + portRate + "/"+baseRate+") as total from (" +
						"select * from a"+edate[0]+groupPortAssetSetCodes[3]+"Lbalance al  where Facctcode="+dbl.sqlString(acctCode)+" and Fmonth="+Integer.parseInt(edate[1])+" ) alb " ;
			
			/* 本年减少*/
			String sql4 = " select sum(alb.FBAccCredit * " + portRate + "/"+baseRate+") as total from (" +
						"select * from a"+edate[0]+groupPortAssetSetCodes[3]+"Lbalance al  where Facctcode="+dbl.sqlString(acctCode)+" and Fmonth="+Integer.parseInt(edate[1])+" ) alb " ;
			
			String maxMonth = this.getStringData("select max(Fmonth) as Fmonth from a"+date[0]+groupPortAssetSetCodes[3]+"Lbalance","Fmonth");
			
			if(maxMonth.trim().length() == 0 || Integer.parseInt(maxMonth) ==0)
				throw new YssException("查询【"+this.portCodes+"】组合无对应的会计期间！");
			
			if(Integer.parseInt(edate[1]) > Integer.parseInt(maxMonth))
				throw new YssException("查询【"+this.portCodes+"】组合无对应的会计期间！");
			
			firstBalance += this.getNumberData(sql1,"total",0);
			
			lastBalance += this.getNumberData(sql2,"total",0);
			
			add += this.getNumberData(sql3,"total",0);
			minus += this.getNumberData(sql4,"total",0);
			
			
			
			this.alreadySetCode.put(groupPortAssetSetCodes[3], groupPortAssetSetCodes[3]); //标识 已统计
			
		}
		tempData.append(firstBalance).append("\t").append(add).append("\t").append(minus).append("\t").append(lastBalance);	
		this.firstBalances += firstBalance;
		this.lastBalances += lastBalance;
		
		this.adds += add;
		this.minuses += minus;
		result.append(this.buildRowCompResult(tempData.toString())).append("\r\n");
		
    	return result.toString();
    }
    
    /*
     * 查询数据
     * 
     * */
    private double getNumberData(String sql , String field , double defaultValue) throws YssException{
    	double total = defaultValue;
        ResultSet rs = null; 
        try {           
            rs = dbl.openResultSet(sql);
			if (rs.next()) {
				total = rs.getDouble(field);
			}
        } catch (Exception e) {
            throw new YssException("获取期初或期末数据出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    	return total;
    }  
    
    /*
     * 查询数据
     * 
     * */
    private String getStringData(String sql , String field ) throws YssException{    	
        ResultSet rs = null; 
        String result = "";
        try {           
            rs = dbl.openResultSet(sql);
			if (rs.next()) {
				result = rs.getString(field);
			}
        } catch (Exception e) {
            throw new YssException("获取期初或期末数据出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    	return result;
    }  
    
    /*
     * 去除重复组合代码
     * */
    private void discticePortCode(String portCodes , String regex){
    	if(portCodes == null || portCodes.length() == 0)
    		return ;    	
    	if(regex == null || regex.length() == 0)
    		regex = ",";
    	
    	String [] arrPortCodes = portCodes.split(regex);
    	
    	for (int i = 0; i < arrPortCodes.length; i++) {
    		/**shashijie 2012-7-1 STORY 2475*/
    		if(!this.distinctPortcode.contains(arrPortCodes[i])){
				this.distinctPortcode.put(arrPortCodes[i], arrPortCodes[i]);
			}
			/**end*/
		}
    }
    
    /*
     * 设置组合群 组合 资产代码 等
     * */
    private void getGroupPortAssetSetCodes() throws YssException{
        String strSql = "";
        ResultSet rs = null;
        String [] arrayAssetGroups =null;        
        try {        	
            arrayAssetGroups = this.portCodes.split(",");
            
            for (int i = 0; i < arrayAssetGroups.length; i++) {
            	String groupcode=arrayAssetGroups[i].split("-")[0];
            	String portcode=arrayAssetGroups[i].split("-")[1];
            	
            	strSql = " select * from Tb_" + groupcode + "_Para_Portfolio where FCheckState = 1 and FEnabled =1 and FPORTCODE = "+ dbl.sqlString(portcode) ;
            	rs = dbl.openResultSet(strSql);
            	while (rs.next()) {
            		String portCode = rs.getString("FPORTCODE");
            		if(!portCode.equals(portcode))
            			continue;
            		String assetCode = rs.getString("FASSETCODE");            		
            		String setCode = this.getSetCode(assetCode);            		
            		String key = groupcode + "-" + portCode + "-" + assetCode + "-"+ setCode;
            		this.groupPortAssetCodeSetCode.put(key, key);
            	}
            }
        } catch (Exception e) {
            throw new YssException("获取组合群代码、组合代码、资产代码、套帐代码出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /*
     * 
     * 获取套帐代码
     * */
    private String getSetCode(String assetCode) throws YssException{
    	
    	 String strSql = "";
         ResultSet rs = null;
         String setCode="";
         
         try {
            
             strSql = "select * from LSetList where FsetId="+dbl.sqlString(assetCode)+" order by Fyear desc";
             rs = dbl.openResultSet(strSql);
             if(rs.next()) {
            	 setCode =rs.getString("FSETCODE");
             }
             
             if(setCode.length() > 0 && setCode.length() < 3)
            	 setCode = getFillDigit(setCode , 3 , "0" , true);
             
         } catch (Exception e) {
             throw new YssException("获取套帐代码出错： \n" + e.getMessage());
         } finally {
             dbl.closeResultSetFinal(rs);
         }    	
    	return setCode;
    }
    
    
    /*
     * 设置保留位数
     * 
     * fixChar :前后缀字符
     * preOrPostFix 是否前缀  : true 前缀  ，false 后缀 
     * 
     * */
    private String getFillDigit(String value , int tagerDigit , String fixChar , boolean preOrPostFix){
    	
    	if(value == null || value.length() == 0)
    		return "";
    	if(fixChar == null || fixChar.length() == 0 )
    		fixChar = "0";    	
    	
    	int count = value.length() < tagerDigit ? tagerDigit - value.length() : 0 ;    	
		String postfixs ="";    		
		for (int i = 0; i < count; i++) {
			postfixs += fixChar ;
		}

		if(postfixs.length() > 0)
			value = preOrPostFix ? postfixs + value  : value + postfixs;   
		
    	return value;
    }    
    

	protected String buildRowCompResult(String str) throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";
        RepTabCellBean rtc = null;
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("DSSafeAcc");
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("DSSafeAcc") +
                " and FCheckState = 1 order by FOrderIndex";
            rs = dbl.openResultSet(strSql);
            for (int i = 0; i < sArry.length; i++) {
                sKey = "DSSafeAcc" + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append(
                    "\t");
            }
            if (buf.toString().trim().length() > 1) {
                strReturn = buf.toString().substring(0,
                    buf.toString().length() - 1);
            }
            rs.close();
            return strReturn ;
        } catch (Exception e) {
            throw new YssException("获取中行人寿应收利息数据的格式出错：\n"+e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
}
