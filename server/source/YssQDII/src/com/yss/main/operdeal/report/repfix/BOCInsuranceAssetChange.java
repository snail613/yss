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
 * <p>Date 2012.2.23</p>
 * <p>Author mengweilong</P>
 * <p>Description 中行人寿快报（资产变动表）</p>
 * story 1968 QD中国银行2011年11月29日20_A
 */
public class BOCInsuranceAssetChange extends BaseBuildCommonRep {
    
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
	private String groupPort = ""; //组合群-组合
	private String account[]=null;//科目
	private BigDecimal startBalance[] = null;//期初余额
	private BigDecimal endBalance[] = null;//期末余额
	private double endBalancedouble[]= null;
	private double startBalancedouble[] = null;
	private double startBalanceTotaldouble[]= null;
	private double endBalanceTotaldouble[] = null;
//	private double startBaseRate[] = null;//起始日期基础汇率
//	private double startPortRate[] = null;//起始日期组合汇率
	private double endBaseRate[] = null;//截止日期基础汇率
	private double endPortRate[] = null;//截止日期组合汇率
	private BigDecimal startBalanceTotal[] = null;//期初合计值
	private BigDecimal endBalanceTotal[] = null;//期末合计值
	private String errorStr = "";//错误提示
	private CommonRepBean repBean;
    private YssFinance fc = null;
    private FixPub fixPub = null;
    public BOCInsuranceAssetChange() {
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
        account =new String[]{"1002","1133","1155","1131","1132","1122","1503","1511","2202","2161","4002","410407"};
        startBalance = new BigDecimal[account.length];
        endBalance = new BigDecimal[account.length];
        startBalanceTotal = new BigDecimal[4];
        endBalanceTotal = new BigDecimal[4];
        endBalancedouble = new double[account.length];
    	startBalancedouble = new double[account.length];
    	startBalanceTotaldouble = new double[4];
    	endBalanceTotaldouble  = new double[4];
        
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
 			throw new YssException("查询日期超出当前会计期间。");  //modify by zhangjun 2012-03-22 
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
     * 拼接数据及格式
     * buildShowData
     * @return String
     */
    protected String buildShowData() throws YssException {
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
    		
    		buf.append("资    产").append("\t").append("序号").append("\t").append("月末余额").append("\t").append("月初余额").append("\t").append("本月变化").append("\t");
            buf.append("负债和所有者权益（或股东权益）").append("\t").append("序号").append("\t").append("月末余额").append("\t").append("月初余额").append("\t").append("本月变化").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
    		buf.append("资产：").append("\t").append("1").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            buf.append("负债：").append("\t").append("41").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("货币资金").append("\t").append("2").append("\t").append(endBalance[0].add(endBalance[1])).append("\t");
            buf.append(startBalance[0].add(startBalance[1])).append("\t").append(endBalance[0].add(endBalance[1]).subtract(startBalance[0].add(startBalance[1]))).append("\t");
            buf.append("短期借款").append("\t").append("42").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append("拆出资金").append("\t").append("3").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("存入保证金").append("\t").append("43").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("交易性金融资产").append("\t").append("4").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append(" 拆入资金：").append("\t").append("44").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append(" 衍生金融资产").append("\t").append("5").append("\t").append(endBalance[2]).append("\t");
            buf.append(startBalance[2]).append("\t").append(endBalance[2].subtract(startBalance[2])).append("\t");
            buf.append("交易性金融负债").append("\t").append("45").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("买入返售金融资产").append("\t").append("6").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("衍生金融负债").append("\t").append("46").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("应收股利").append("\t").append("7").append("\t").append(endBalance[3]).append("\t");
            buf.append(startBalance[3]).append("\t").append(endBalance[3].subtract(startBalance[3])).append("\t");
            buf.append("卖出回购金融资产款").append("\t").append("47").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("应收利息").append("\t").append("8").append("\t").append(endBalance[4]).append("\t").append(startBalance[4]).append("\t");
            buf.append(endBalance[4].subtract(startBalance[4])).append("\t");
            buf.append("预收保费").append("\t").append("48").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("应收保费").append("\t").append("9").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("应付手续费及佣金").append("\t").append("49").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append(" 应收代位追偿款").append("\t").append("10").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("应付分保账款").append("\t").append("50").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("应收分保账款").append("\t").append("11").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("应付职工薪酬").append("\t").append("51").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("应收分保未到期责任准备金").append("\t").append("12").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("应交税费").append("\t").append("52").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("应收分保未决赔款准备金").append("\t").append("13").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("应付股利").append("\t").append("53").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("应收分保寿险责任准备金").append("\t").append("14").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("其他应付款").append("\t").append("54").append("\t").append(endBalance[8]).append("\t").append(startBalance[8]).append("\t");
            buf.append(endBalance[8].subtract(startBalance[8]));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("  应收分保长期健康险责任准备金").append("\t").append("15").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("应付赔付款").append("\t").append("55").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("预付赔付款").append("\t").append("16").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("应付保单红利").append("\t").append("56").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append(" 待摊费用").append("\t").append("17").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("保护储金及投资款").append("\t").append("57").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("其他应收款").append("\t").append("18").append("\t").append(endBalance[5]).append("\t");
            buf.append(startBalance[5]).append("\t").append(endBalance[5].subtract(startBalance[5])).append("\t");
            buf.append("未到期责任准备金").append("\t").append("58").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("贷款").append("\t").append("19").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("未决赔款准备金").append("\t").append("59").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("其中：保户质押贷款").append("\t").append("20").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("寿险责任准备金").append("\t").append("60").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("系统往来").append("\t").append("21").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("长期健康险责任准备金").append("\t").append("61").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("定期存款").append("\t").append("22").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("代理业务负债").append("\t").append("62").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("代理业务资产").append("\t").append("23").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("长期借款").append("\t").append("63").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("可供出售金融资产").append("\t").append("24").append("\t").append(endBalance[6]).append("\t").append(startBalance[6]).append("\t");
            buf.append(endBalance[6].subtract(startBalance[6])).append("\t");
            buf.append("应付债券").append("\t").append("64").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("持有至到期投资").append("\t").append("25").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("内部往来").append("\t").append("65").append("\t").append(endBalance[9]).append("\t").append(startBalance[9]).append("\t");
            buf.append(endBalance[9].subtract(startBalance[9])).append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("长期股权投资").append("\t").append("26").append("\t").append(endBalance[7]).append("\t").append(startBalance[7]).append("\t");
            buf.append(endBalance[7].subtract(startBalance[7])).append("\t");
            buf.append("独立账户负债").append("\t").append("66").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("存出资本保证金").append("\t").append("27").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("货币兑换").append("\t").append("67").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("投资性房地产").append("\t").append("28").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("递延所得税负债").append("\t").append("68").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("固定资产").append("\t").append("29").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("其他负债").append("\t").append("69").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("在建工程").append("\t").append("30").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("负债合计").append("\t").append("70").append("\t").append(endBalanceTotal[1]).append("\t").append(startBalanceTotal[1]).append("\t");
            buf.append(endBalanceTotal[1].subtract(startBalanceTotal[1])).append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("无形资产").append("\t").append("31").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("所有者权益（或股东权益）").append("\t").append("71").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("长期待摊费用").append("\t").append("32").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("实收资本（或股本）").append("\t").append("72").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("独立帐户资产").append("\t").append("33").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("资本公积").append("\t").append("73").append("\t").append(endBalance[10]).append("\t").append(startBalance[10]).append("\t");
            buf.append(endBalance[10].subtract(startBalance[10])).append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("递延所得税资产").append("\t").append("34").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append("减：库存股").append("\t").append("74").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("其他资产").append("\t").append("35").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            buf.append(" 盈余公积").append("\t").append("75").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append(" ").append("\t").append("36").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            buf.append("一般风险准备").append("\t").append("76").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append(" ").append("\t").append("37").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            buf.append("未分配利润").append("\t").append("77").append("\t").append(endBalance[11]).append("\t").append(startBalance[11]).append("\t");
            buf.append(endBalance[11].subtract(startBalance[11])).append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            buf.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
            buf.setLength(0); //给buf清空
            buf.append(" ").append("\t").append("38").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            buf.append("少数股东权益").append("\t").append("78").append("\t").append("0.00").append("\t").append("0.00").append("\t").append("0.00").append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append(" ").append("\t").append("39").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
            buf.append("所有者权益（或股东权益）合计").append("\t").append("79").append("\t").append(endBalanceTotal[2]).append("\t").append(startBalanceTotal[2]).append("\t");
            buf.append(endBalanceTotal[2].subtract(startBalanceTotal[2])).append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("资产总计").append("\t").append("40").append("\t").append(endBalanceTotal[0]).append("\t");
            buf.append(startBalanceTotal[0]).append("\t").append(endBalanceTotal[0].subtract(startBalanceTotal[0])).append("\t");
            buf.append("负债和所有者权益（或股东权益）总计").append("\t").append("80").append("\t").append(endBalanceTotal[3]).append("\t");
            buf.append(startBalanceTotal[3]).append("\t").append(endBalanceTotal[3].subtract(startBalanceTotal[3])).append("\t");
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");
            
    		return strResult.toString();
    	} catch (Exception e) {
        throw new YssException("获取中行人寿快报（资产变动表）出错： \n" + e.getMessage());
    	} finally {
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
     * 获取各项目的期初余额和期末余额
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
    		for(int i=0;i<8;i++)
        	{
    			//资产总计
    			//资产总计
        		startBalanceTotaldouble[0] += startBalancedouble[i];
        		startBalanceTotal[0]= new BigDecimal(startBalanceTotaldouble[0]).setScale(2,BigDecimal.ROUND_HALF_UP);
        		endBalanceTotaldouble[0] += endBalancedouble[i];
        		endBalanceTotal[0]= new BigDecimal(endBalanceTotaldouble[0]).setScale(2,BigDecimal.ROUND_HALF_UP);
        	}		
    		//负债合计
    		startBalanceTotaldouble[1] = startBalancedouble[8]+ startBalancedouble[9];
    		endBalanceTotaldouble[1] = endBalancedouble[8]+ endBalancedouble[9];
    		//所有者权益（或股东权益）合计
    		startBalanceTotaldouble[2] = startBalancedouble[10]+ startBalancedouble[11];
    		endBalanceTotaldouble[2] = endBalancedouble[10]+ endBalancedouble[11];
    		//负债和所有者权益（或股东权益）总计
    		startBalanceTotaldouble[3] = startBalanceTotaldouble[1]+ startBalanceTotaldouble[2];
    		endBalanceTotaldouble[3] = endBalanceTotaldouble[1]+ endBalanceTotaldouble[2];
    		
    		startBalanceTotal[1] = new BigDecimal(startBalanceTotaldouble[1]).setScale(2,BigDecimal.ROUND_HALF_UP);
    		endBalanceTotal[1] = new BigDecimal(endBalanceTotaldouble[1]).setScale(2,BigDecimal.ROUND_HALF_UP);
    		endBalanceTotal[2] = new BigDecimal(endBalanceTotaldouble[2]).setScale(2,BigDecimal.ROUND_HALF_UP);
    		startBalanceTotal[2] = new BigDecimal(startBalanceTotaldouble[2]).setScale(2,BigDecimal.ROUND_HALF_UP);
    		startBalanceTotal[3] = new BigDecimal(startBalanceTotaldouble[3]).setScale(2,BigDecimal.ROUND_HALF_UP);
    		endBalanceTotal[3] = new BigDecimal(endBalanceTotaldouble[3]).setScale(2,BigDecimal.ROUND_HALF_UP);
    		for(int i=0;i<4;i++)
    		{
    			if(startBalanceTotaldouble[i]==0)
    			{
    				startBalanceTotal[i]=new BigDecimal(0).setScale(2,BigDecimal.ROUND_HALF_UP);
    			}
    			if(endBalanceTotaldouble[i]==0)
    			{
    				endBalanceTotal[i]=new BigDecimal(0).setScale(2,BigDecimal.ROUND_HALF_UP);
    			}
    		}
    	}
    	catch (Exception e) {
    		throw new YssException("获取各项目的期初余额和期末余额出错！ \n" + e.getMessage());
        }
    }   
    /**
     * 拼接格式
     * buildRowCompResult
     * @param str String
     * @return String
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
            hmCellStyle = getCellStyles("DSBKJ01");
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("DSBKJ01") +
                " and FCheckState = 1 order by FOrderIndex";
            rs = dbl.openResultSet(strSql);
            for (int i = 0; i < sArry.length; i++) {
                sKey = "DSBKJ01" + "\tDSF\t-1\t" + i;
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
            throw new YssException("查询日期超出当前会计期间。");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return flag;
    }
}
