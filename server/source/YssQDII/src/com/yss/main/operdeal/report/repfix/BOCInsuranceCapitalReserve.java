package com.yss.main.operdeal.report.repfix;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.yss.dsub.*;
import com.yss.main.cusreport.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.*;
import com.yss.main.operdeal.report.netvalueviewpl.*;
import com.yss.main.report.*;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 * <p>Date 2012.2.20</p>
 * <p>Author mengweilong</P>
 * <p>Description 中行人寿资本公积情况表</p>
 * story 1967 QD中国银行2011年11月29日19_A
 */
public class BOCInsuranceCapitalReserve extends BaseBuildCommonRep {
    
	private String startDate = "";//起始日期
	private String endDate = "";//截止日期
	private String portCode = ""; //组合
	private String currencyNo = "";//币种
	//控件中指定的年、月
	private int startYear = 0;
	private int startMonth = 0;
	private int startDay = 0;
	private int endYear = 0;
	private int endMonth = 0;
	private int endDay = 0;
	private int lastYear = 0;//起始日期的上一个年份
	private int lastMonth = 0;//起始日期的上一个月的月份
	private String lastDate = "";//起始日期的上一个月月末
	private String portArray[] = null;//组合
	private String groupArray[] = null;//组合群
	private String groupPortArray[] = null;//组合群-组合
	private String account[]=null;//科目
	private double startBalancedouble[] = null;//期初余额
	private double endBalancedouble[] = null;//期末余额
	private BigDecimal startBalance[] =null;
	private BigDecimal endBalance[] = null;
//	private double startBaseRate[] = null;//起始日期基础汇率
//	private double startPortRate[] = null;//起始日期组合汇率
	private double endBaseRate[] = null;//截止日期基础汇率
	private double endPortRate[] = null;//截止日期组合汇率
	private double theYearIncreasedouble[] = null;//本期增加
	private double theYearDecreasedouble[] = null;//本期减少
	private double balanceTotaldouble[] =null;
	private BigDecimal theYearIncrease[] = null;
	private BigDecimal theYearDecrease[] = null;
	private BigDecimal balanceTotal[] =null;//合计
	private String groupPort = ""; //组合群-组合
	private String errorStr = "";//错误提示
	private CommonRepBean repBean;
    private YssFinance fc = null;
    private FixPub fixPub = null;
    public BOCInsuranceCapitalReserve() {
    }   
    /**
     * 程序入口
     * buildReport
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        sResult = buildShowData();
        return sResult;
    }
    /**
     * 初始化变量
     * initBuildReport
     * @param bean BaseBean
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        
        startDate = reqAry[0].split("\r")[1];//起始日期
        endDate = reqAry[1].split("\r")[1];//截止日期
        groupPort = reqAry[2].split("\r")[1];//组合群-组合
        currencyNo = reqAry[3].split("\r")[1];//币种
        //起始日期
        String dateArray[] = startDate.split("-");
        startYear = Integer.parseInt(dateArray[0]);
        startMonth = Integer.parseInt(dateArray[1]);
        startDay = Integer.parseInt(dateArray[2]);
        //截止日期
        String dateArray2[] = endDate.split("-");
        endYear = Integer.parseInt(dateArray2[0]);
        endMonth = Integer.parseInt(dateArray2[1]);
        endDay = Integer.parseInt(dateArray[2]);
        
        groupPortArray = groupPort.split(","); //组合群-组合
        portArray = new String[groupPortArray.length];//组合
        groupArray = new String[groupPortArray.length];//组合群
        //汇率
//        startBaseRate = new double[portArray.length];
//        startPortRate = new double[portArray.length];
        endBaseRate = new double[portArray.length];
        endPortRate = new double[portArray.length];
        //科目代码 
        account =new String[]{"4002990301","4002990302"};
        startBalance = new BigDecimal[account.length];
        endBalance = new BigDecimal[account.length];
        theYearIncrease = new BigDecimal[account.length];
        theYearDecrease = new BigDecimal[account.length];
        balanceTotal = new BigDecimal[4];
        startBalancedouble = new double[account.length];
        endBalancedouble = new double[account.length];
        theYearIncreasedouble = new double[account.length];
        theYearDecreasedouble = new double[account.length];
        balanceTotaldouble = new double[4];
        
        fixPub = new FixPub();
        fixPub.setYssPub(pub);
        //判断用户所设置的日期是否正确
        if(!startDate.equals(getFirstDayOfMonth(startYear,startMonth)) || !endDate.equals(getLastDayOfMonth(endYear,endMonth)))
        {
        	throw new YssException("请设置完整的会计期间！");
        }
        if(startYear != endYear)
        {
        	throw new YssException("报表查询不支持跨年操作！");
        }
        if((endYear < startYear) || (startYear <= endYear && startMonth > endMonth) || (startYear <= endYear && startMonth <= endMonth && startDay > endDay))
        {
        	throw new YssException("截止日期要大于起始日期！");
        }
        for(int i=0;i<groupPortArray.length;i++)
  	    {
  		   groupArray[i] = groupPortArray[i].split("-")[0];
  		   portArray[i] = groupPortArray[i].split("-")[1];
  		   if(!isOverAccountPeriod(groupArray[i],portArray[i]))
     	   {
  			   errorStr += ( groupPortArray[i] + "," );
     	   }
  	    }
 		if(errorStr != "")
 		{
 			errorStr = errorStr.substring(0,errorStr.length()-1);
 			//throw new YssException("所选择的组合群-组合【" + errorStr + "】无对应的会计期间！");
 			throw new YssException("查询日期超出当前会计期间。");
 		}
    }
    /**
     * saveReport
     * @param sReport String
     * @return String
     */
    public String saveReport(String sReport) {
        return "";
    }   
    /**
     * 拼接数据字符串和格式字符串
     * @return String
     * @throws YssException
     */
    protected String buildShowData() throws YssException { //报表的显示数据
    	
    	StringBuffer buf = new StringBuffer();
    	StringBuffer strResult = new StringBuffer();
    	String str = "";
    	fc = new YssFinance(); //可以调用里面的函数按其年份来生成财务里的表
    	fc.setYssPub(pub);
    	try {
    		//getPortGroupCode();
    		getLastDate();
    		getExchangeRate();
    		getStartEndBalance();

            buf.append("项目").append("\t").append("行次").append("\t").append("期初余额").append("\t");
            buf.append("本期增加").append("\t").append("本期减少").append("\t").append("期末余额").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("股本溢价").append("\t").append("1").append("\t");
            buf.append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("投资性房地产重估").append("\t").append("2").append("\t");
            buf.append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("套期公允价值变动").append("\t").append("3").append("\t");
            buf.append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空s
            buf.append("持有的可供出售金融资产公允价值变动").append("\t");
            buf.append("4").append("\t").append(startBalance[0]).append("\t");
            buf.append(theYearIncrease[0]).append("\t").append(theYearDecrease[0]).append("\t").append(endBalance[0]).append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("外币折算差额").append("\t").append("5").append("\t");
            buf.append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("分红特别储备影响").append("\t").append("6").append("\t");
            buf.append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("递延所得税影响").append("\t").append("7").append("\t");
            buf.append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("权益法下被投资单位其他所有者权益变动的影响").append("\t");
            buf.append("8").append("\t").append(startBalance[1]).append("\t");
            buf.append(theYearIncrease[1]).append("\t").append(theYearDecrease[1]).append("\t").append(endBalance[1]).append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
 
            buf.setLength(0); //给buf清空
            buf.append(" ").append("\t").append("9").append("\t").append(" ").append("\t");
            buf.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append(" ").append("\t").append("10").append("\t").append(" ").append("\t");
            buf.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("合计").append("\t").append("11").append("\t").append(balanceTotal[0]).append("\t");
            buf.append(balanceTotal[2]).append("\t").append(balanceTotal[3]).append("\t").append(balanceTotal[1]).append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            buf.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("主管会计工作的负责人:").append("\t").append(" ").append("\t");
            buf.append("会计机构负责人:").append("\t").append(" ").append("\t");
            buf.append(" ").append("\t").append(" ").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            buf.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("会计主管:").append("\t").append(" ").append("\t").append("制表人:").append("\t");
            buf.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
    		return strResult.toString();
    	} 
    	catch (Exception e) {
    		throw new YssException("获取资本公积情况表出错！ \n" + e.getMessage());
    	}
    	finally {
    	}
    }
    
    /**
	* 得到某年某月的第一天
	* @param year
	* @param month
	*/
   protected String getFirstDayOfMonth(int year, int month) {
   	  Calendar cal = Calendar.getInstance();
   	  cal.set(Calendar.YEAR, year);
   	  cal.set(Calendar.MONTH, month-1);
   	  cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
   	  return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
   }
   /**
   * 得到某年某月的最后一天
   * @param year
   * @param month
   * @return
   */
   protected String getLastDayOfMonth(int year, int month) {
   	  Calendar cal = Calendar.getInstance();
   	  cal.set(Calendar.YEAR, year);
   	  cal.set(Calendar.MONTH, month-1);
   	  cal.set(Calendar.DAY_OF_MONTH, 1);
   	  int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
   	  cal.set(Calendar.DAY_OF_MONTH, value);
   	  return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
   }
   /**
   * 得到起始日期的期末信息
   */
   protected void getLastDate() throws ParseException
   {
	   SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	   Calendar cal=Calendar.getInstance();
	   try 
	   {
		    Date date = sf.parse(startDate);
		    cal.setTime(date);
		    if(cal.get(Calendar.MONTH) == 0)
		    {
		    	lastDate = getLastDayOfMonth(cal.get(Calendar.YEAR)-1,12);
			    lastMonth = 12;
				lastYear = cal.get(Calendar.YEAR) - 1;
		    }
		    else
		    {
			    lastDate = getLastDayOfMonth(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH));
			    lastMonth = cal.get(Calendar.MONTH);
				lastYear = cal.get(Calendar.YEAR);
		    }
	   } 
	   catch (ParseException e) 
	   {
		    e.printStackTrace();
	   }
   }
//   /**
//    * 获取组合代码和组合群代码
//    */
//   protected void getPortGroupCode() throws YssException {
//
//       try {
//    	   for(int i=0;i<groupPortArray.length;i++)
//    	   {
//    		   groupArray[i] = groupPortArray[i].split("-")[0];
//    		   portArray[i] = groupPortArray[i].split("-")[1];
//    	   }
//    	   
//       } catch (Exception e) {
//           throw new YssException("获取组合代码和组合群代码出错！"+ "\r\n" + e.getMessage(), e);
//       } 
//   }  
   /**
    * 
    * 获取套账号
    */
   protected int getSetCode( String portCode,String groupCode) throws YssException {
       ResultSet rs = null;
       String sqlStr = "";
       int portInfo = 0;
       try {
           sqlStr = "select distinct l.fsetcode from lsetlist l join " + "Tb_" + groupCode + "_Para_Portfolio" + 
           		" t on l.fsetid = t.fassetcode  where t.fportcode = " + dbl.sqlString(portCode);
           rs = dbl.queryByPreparedStatement(sqlStr); 
           if (rs.next()) {
           	portInfo =Integer.parseInt( rs.getString("fsetcode"));
           }
           if(portInfo==0)
           {
        	   throw new YssException("所选组合【" + portCode +"】没有对应的套账号!");
           }
           return portInfo;
       } catch (Exception e) {
           throw new YssException("获取套账号出错！"+ "\r\n" + e.getMessage(), e);
       } finally {
           dbl.closeResultSetFinal(rs);
       }
   }  
   /**
    * 处理财务系统表前缀
    */
   protected String getTablePrefix(int lYear, int lnSet) {
		String stmp;
		if ((lYear > 999) && (lnSet != 0)) { //年份四位
			stmp = "A"  + lYear + new DecimalFormat("000").format(lnSet);
			return (stmp.length() == 1) ? "" : stmp;
			}
			return "";
	}
   /**
    * 获取基础汇率和组合汇率
     */
    protected void getExchangeRate() throws YssException{

        try {
       	 
       	 for(int i=0;i<portArray.length;i++)
       	 {	  		 
       		 endBaseRate[i] = this.getSettingOper().getCuryRate( //期末基础汇率
 						YssFun.parseDate(endDate), 
 						currencyNo,
 						portArray[i],
 						YssOperCons.YSS_RATE_BASE);
         	 
       		 endPortRate[i] = this.getSettingOper().getCuryRate( //期末组合汇率
 						YssFun.parseDate(endDate), 
 						"", 
 						portArray[i], 
 						YssOperCons.YSS_RATE_PORT);

       	 }
        } catch (Exception e) {
            throw new YssException("获取基础汇率和组合汇率出错！ \n" + e.getMessage());
        } 
    }
    /**
     * 获取期初余额
     * @return double
     * @throws YssException
     */
    protected double getStartBalance(String pAcctcode,String portCode,String groupCode)throws YssException
    {
    	double startBalance = 0;
    	String strSql = null;
        ResultSet rs = null;
        try {
        	strSql="select sum(FBStartBal) as fstartBalance from "
				  + getTablePrefix(startYear,getSetCode(portCode,groupCode)) + "lbalance"
				  + " where fmonth=" + startMonth
				  + " and facctcode=" + pAcctcode;
          	rs = dbl.openResultSet(strSql);
          	if(rs.next()) {
              	startBalance = YssD.round(rs.getDouble("fstartBalance"), 4);
              }
            rs.close();
        } catch (Exception e) {
            throw new YssException("获取期初余额出错！ \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return startBalance;
    }
    /**
     * 获取期末余额
     * @return double
     * @throws YssException
     */
    protected double getEndBalance(String pAcctcode,String portCode,String groupCode)throws YssException
    {
    	double endBalance = 0;
    	String strSql = null;
        ResultSet rs = null;
        try {
        	strSql="select sum(FBEndBal) as fendBalance from "
				  + getTablePrefix(endYear,getSetCode(portCode,groupCode)) + "lbalance"
//				  + " join " + "Tb_" + groupCode + "_Para_Portfolio" + " b"
//				  + " on a.fcurcode = b.fportcury"
				  + " where fmonth=" + endMonth
				  + " and facctcode=" + pAcctcode;
        	rs = dbl.openResultSet(strSql);
            if(rs.next()) {
            	endBalance = YssD.round(rs.getDouble("fendBalance"), 4);
            }
            rs.close();
        } catch (Exception e) {
            throw new YssException("获取期末余额出错！ \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return endBalance;
    }
    /**
     * 获取各项目的期初余额和期末余额、及合计
     */
    protected void getStartEndBalance()throws YssException
    {
    	try
    	{
    		for(int i=0;i<account.length;i++)//科目数
            {
            	for(int j=0;j<portArray.length;j++)//组合数
            	{
            		if(endBaseRate[j]==0)
            		{
            			continue;
            		}
            		else
            		{
            			endBalancedouble[i] += (getEndBalance(account[i],portArray[j],groupArray[j]) * endPortRate[j] / endBaseRate[j]);//期末余额
            			endBalance[i] = new BigDecimal(endBalancedouble[i]).setScale(2,BigDecimal.ROUND_HALF_UP);
            			startBalancedouble[i] += (getStartBalance(account[i],portArray[j],groupArray[j])* endPortRate[j] / endBaseRate[j]);//期初余额
            			startBalance[i] = new BigDecimal(startBalancedouble[i]).setScale(2,BigDecimal.ROUND_HALF_UP);
            		}
            	}
            	if(endBalancedouble[i]==0)
            	{
            		endBalance[i] = new BigDecimal(0).setScale(2,BigDecimal.ROUND_HALF_UP);
            	}
            	if(startBalancedouble[i]==0)
            	{
            		startBalance[i] = new BigDecimal(0).setScale(2,BigDecimal.ROUND_HALF_UP);
            	}
    		}
    		for(int i=0;i<account.length;i++)
    		{
        		if(startBalancedouble[i]<=endBalancedouble[i])
        		{
        			theYearIncreasedouble[i]=endBalancedouble[i]-startBalancedouble[i];
        			theYearIncrease[i] = new BigDecimal(theYearIncreasedouble[i]).setScale(2,BigDecimal.ROUND_HALF_UP);
        		}
        		else
        		{
        			theYearDecreasedouble[i]=startBalancedouble[i]-endBalancedouble[i];
        			theYearDecrease[i] = new BigDecimal(theYearDecreasedouble[i]).setScale(2,BigDecimal.ROUND_HALF_UP);
        		}
        		if(theYearIncreasedouble[i]==0)
        		{
        			theYearIncrease[i] = new BigDecimal(0).setScale(2,BigDecimal.ROUND_HALF_UP);
        		}
        		if(theYearDecreasedouble[i]==0)
        		{
        			theYearDecrease[i] = new BigDecimal(0).setScale(2,BigDecimal.ROUND_HALF_UP);
        		}
    		}
    		balanceTotaldouble[0]=startBalancedouble[0]+startBalancedouble[1];//期初余额总计
    		balanceTotaldouble[2]=theYearIncreasedouble[0]+theYearIncreasedouble[1];//本期增加总计
    		balanceTotaldouble[3]=theYearDecreasedouble[0]+theYearDecreasedouble[1];//本期减少总计
    		balanceTotaldouble[1]=endBalancedouble[0]+endBalancedouble[1];//期末余额总计
    		
    		balanceTotal[0] = new BigDecimal(balanceTotaldouble[0]).setScale(2,BigDecimal.ROUND_HALF_UP);
    		balanceTotal[2] = new BigDecimal(balanceTotaldouble[2]).setScale(2,BigDecimal.ROUND_HALF_UP);
    		balanceTotal[3] = new BigDecimal(balanceTotaldouble[3]).setScale(2,BigDecimal.ROUND_HALF_UP);
    		balanceTotal[1] = new BigDecimal(balanceTotaldouble[1]).setScale(2,BigDecimal.ROUND_HALF_UP);
    		
    		for(int i=0;i<4;i++)
    		{
    			if(balanceTotaldouble[i]==0)
    			{
    				balanceTotal[i]=new BigDecimal(0).setScale(2,BigDecimal.ROUND_HALF_UP);
    			}
    		}
    	}
    	catch (Exception e) {
    		throw new YssException("获取各项目的期初余额和期末余额、及合计出错！ \n" + e.getMessage());
        }
    } 
    /**
     * 拼接格式
     * @param str
     * @return String
     * @throws YssException
     */
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
            hmCellStyle = getCellStyles("DSBKJ1026");
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("DSBKJ1026") +
                " and FCheckState = 1 order by FOrderIndex";
            rs = dbl.openResultSet(strSql);
            for (int i = 0; i < sArry.length; i++) {
                sKey = "DSBKJ1026" + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append("\t");
            }
            if (buf.toString().trim().length() > 1) {
                strReturn = buf.toString().substring(0,buf.toString().length() - 1);
            }
            rs.close();
            return strReturn + "\t\t";
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    } 
    /**
     * 判断用户所设置的日期是否在会计期间内
     * @param groupCode
     * @param portCode
     * @return
     * @throws YssException
     */
    protected boolean isOverAccountPeriod(String groupCode,String portCode)throws YssException
    {
    	int year[] = new int[2];
    	boolean flag = true;
    	String strSql = null;
        ResultSet rs = null;
        try {
        	strSql = "select max(fyear) as maxYear,min(fyear) as minYear from lsetlist l join " + "Tb_" + groupCode + "_Para_Portfolio t " + 
        		"on l.fsetid = t.fassetcode  where t.fportcode = " + dbl.sqlString(portCode);
        	rs = dbl.openResultSet(strSql);
            if(rs.next()) {
            	year[0]=rs.getInt("maxYear");
            	year[1]=rs.getInt("minYear");
            }
            rs.close();
            if(endYear<year[1] || endYear>year[0])
            {
            	flag = false;
            }
        } catch (Exception e) {
            throw new YssException("查询日期超出当前会计期间");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return flag;
    }
}
