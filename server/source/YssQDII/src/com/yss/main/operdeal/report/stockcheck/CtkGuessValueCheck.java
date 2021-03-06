package com.yss.main.operdeal.report.stockcheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

/**
 * add huangqirong 2012-11-15 story #3272 估值报表核对
 * */
public class CtkGuessValueCheck extends BaseBuildCommonRep {
	protected CommonRepBean repBean;
	private String reportType = "";
	private String showType = "";
	private String checkDate = "";
	private String portCode = "";
	private String setId = "";
	private String showLevel = "";
	
	public CtkGuessValueCheck() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 管理人  展示有序的字段    管理人字段数量和托管人字段数量必须一致
	 * */
	private String [] checkManagerFields = new String []{
			"FSrcAcctCode".toUpperCase(),
			"FSrcAcctName".toUpperCase(),
			"FSrcAmount".toUpperCase(),
			"FSrcPortUnitCost".toUpperCase(),
			"FSrcPortCost".toUpperCase(),
			"FSrcPortCostRatio".toUpperCase(),
			"FSrcPortMarketPrice".toUpperCase(),
		    "FSrcPortMarketValue".toUpperCase(),
		    "FSrcPortMarketValueRatio".toUpperCase(),
		    "FSrcPortAppreciation".toUpperCase()
			};	
	/**
	 * 托管人  展示有序的字段   管理人字段数量和托管人字段数量必须一致
	 * */
	private String [] checkTrusteeFields = new String []{
			"FACCTCODE".toUpperCase(),
			"FACCTNAME".toUpperCase(),
			"FAMOUNT".toUpperCase(),
			"FUNITNVAMONEY".toUpperCase(),
			"FSTANDARDMONEYCOST".toUpperCase(),
			"FSTANDARDMONEYCOSTTONETRATIO".toUpperCase(),
			"FMARKETPRICE".toUpperCase(),
			"FSTANDARDMONEYMARKETVALUE".toUpperCase(),
			"FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(),
			"FSTANDARDMONEYAPPRECIATION".toUpperCase()
			};
	/**
	 * 字段类型 和上面字段 的个数是一致的
	 * */
	private String [] fieldsType = new String []{"String","String","Number","Number","Number",
										 		 "Number","Number","Number","Number","Number"
										 		};
	
	/**
	 * 字段类型 和上面字段 的个数是一致的
	 * */
	private Hashtable< String , String > fieldsTypes = new Hashtable< String , String >();
	
	
	/**
	 * 额外字段
	 * */
	private String [] otherField = new String []{"FCreateTime".toUpperCase(),"FCHECKTIME".toUpperCase(),"FacctLevel".toUpperCase()};
	/**
	 * 合计项取数规则 某一字段对应取令一字段的值
	 * 如：
	 * 8600     显示的字段		实际取的字段
	 * 8600 == {FAcctCode == FAcctName}
	 * totalValueField 数组的键不能重复
	 * */
	private Hashtable<String , Hashtable<String , String > > totalValueField = new Hashtable<String, Hashtable<String , String > >();
	
	public void initBuildReport(BaseBean bean) throws YssException {
		repBean = (CommonRepBean) bean;
        //解析前台传入的条件字符串
        this.parseRowStr(this.repBean.getRepCtlParam());  
        
        /**
         * 设置字段名称 == 字段类型
         * */
        this.fieldsTypes.put("FSrcAcctCode".toUpperCase(),"String");
        this.fieldsTypes.put("FSrcAcctName".toUpperCase(),"String");
        this.fieldsTypes.put("FSrcAmount".toUpperCase(),"Number");
        this.fieldsTypes.put("FSrcPortUnitCost".toUpperCase(),"Number");
        this.fieldsTypes.put("FSrcPortCost".toUpperCase(),"Number");
        this.fieldsTypes.put("FSrcPortCostRatio".toUpperCase(),"Number");
        this.fieldsTypes.put("FSrcPortMarketPrice".toUpperCase(),"Number");
        this.fieldsTypes.put("FSrcPortMarketValue".toUpperCase(),"Number");
        this.fieldsTypes.put("FSrcPortMarketValueRatio".toUpperCase(),"Number");
        this.fieldsTypes.put("FSrcPortAppreciation".toUpperCase(),"Number");
        this.fieldsTypes.put("FACCTCODE".toUpperCase(),"String");
        this.fieldsTypes.put("FACCTNAME".toUpperCase(),"String");
        this.fieldsTypes.put("FAMOUNT".toUpperCase(),"Number");
        this.fieldsTypes.put("FUNITNVAMONEY".toUpperCase(),"Number");
        this.fieldsTypes.put("FSTANDARDMONEYCOST".toUpperCase(),"Number");
        this.fieldsTypes.put("FSTANDARDMONEYCOSTTONETRATIO".toUpperCase(),"Number");
        this.fieldsTypes.put("FMARKETPRICE".toUpperCase(),"Number");
        this.fieldsTypes.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(),"Number");
        this.fieldsTypes.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(),"Number");
        this.fieldsTypes.put("FSTANDARDMONEYAPPRECIATION".toUpperCase(),"Number");
        
        /**
         * 设置合计项需要取值的字段
         * 管理人数据
         * 托管人数据
         * */
        Hashtable<String ,String > acct_8600 = new Hashtable<String ,String >();
        acct_8600.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_8600.put("FSrcAcctName", "FSrcAcctName");
        acct_8600.put("FSrcPortCost".toUpperCase(), "FSrcPortCost".toUpperCase());
        acct_8600.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        acct_8600.put("FSrcPortMarketValueRatio".toUpperCase(), "FSrcPortMarketValueRatio".toUpperCase());
        
        acct_8600.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());        
        acct_8600.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_8600.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        acct_8600.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(), "FSTANDARDMONEYMARKETVALUETORAT".toUpperCase()); 
        this.totalValueField.put("8600", acct_8600);
        
        Hashtable<String ,String > acct_8700 = new Hashtable<String ,String >();
        acct_8700.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_8700.put("FSrcAcctName", "FSrcAcctName");
        acct_8700.put("FSrcAmount".toUpperCase(), "FSrcAmount".toUpperCase());
        acct_8700.put("FSrcPortUnitCost".toUpperCase(), "FSrcPortUnitCost".toUpperCase());
        acct_8700.put("FSrcPortCost".toUpperCase() , "FSrcPortCost".toUpperCase());
        acct_8700.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        
        acct_8700.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_8700.put("FAMOUNT".toUpperCase(), "FAMOUNT".toUpperCase());
        acct_8700.put("FUNITNVAMONEY".toUpperCase(), "FUNITNVAMONEY".toUpperCase());
        acct_8700.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_8700.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());        
        this.totalValueField.put("8700", acct_8700);
        
        Hashtable<String ,String > acct_8800 = new Hashtable<String ,String >();
        acct_8800.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_8800.put("FSrcAcctName", "FSrcAcctName");
        acct_8800.put("FSrcPortCost".toUpperCase() , "FSrcPortCost".toUpperCase());
        acct_8800.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        acct_8800.put("FSrcPortMarketValueRatio".toUpperCase(), "FSrcPortMarketValueRatio".toUpperCase());
        
        acct_8800.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_8800.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_8800.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        acct_8800.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(), "FSTANDARDMONEYMARKETVALUETORAT".toUpperCase());        
        this.totalValueField.put("8800", acct_8800);
        
        Hashtable<String ,String > acct_8801 = new Hashtable<String ,String >();
        acct_8801.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_8801.put("FSrcAcctName", "FSrcAcctName");
        acct_8801.put("FSrcPortCost".toUpperCase() , "FSrcPortCost".toUpperCase());
        acct_8801.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        acct_8801.put("FSrcPortMarketValueRatio".toUpperCase(), "FSrcPortMarketValueRatio".toUpperCase());
        
        acct_8801.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_8801.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_8801.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        acct_8801.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(), "FSTANDARDMONEYMARKETVALUETORAT".toUpperCase());        
        this.totalValueField.put("8801", acct_8801);
        
        Hashtable<String ,String > acct_8803 = new Hashtable<String ,String >();
        acct_8803.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_8803.put("FSrcAcctName", "FSrcAcctName");
        acct_8803.put("FSrcPortCost".toUpperCase() , "FSrcPortCost".toUpperCase());
        acct_8803.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        acct_8803.put("FSrcPortMarketValueRatio".toUpperCase(), "FSrcPortMarketValueRatio".toUpperCase());
        
        acct_8803.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_8803.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_8803.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        acct_8803.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(), "FSTANDARDMONEYMARKETVALUETORAT".toUpperCase());        
        this.totalValueField.put("8803", acct_8803);
        
        Hashtable<String ,String > acct_8804 = new Hashtable<String ,String >();
        acct_8804.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_8804.put("FSrcAcctName", "FSrcAcctName");
        acct_8804.put("FSrcPortCost".toUpperCase() , "FSrcPortCost".toUpperCase());
        acct_8804.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        acct_8804.put("FSrcPortMarketValueRatio".toUpperCase(), "FSrcPortMarketValueRatio".toUpperCase());
        
        acct_8804.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_8804.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_8804.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        acct_8804.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(), "FSTANDARDMONEYMARKETVALUETORAT".toUpperCase());        
        this.totalValueField.put("8804", acct_8804);        
        
        Hashtable<String ,String > acct_8805 = new Hashtable<String ,String >();
        acct_8805.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_8805.put("FSrcAcctName", "FSrcAcctName");
        acct_8805.put("FSrcPortCost".toUpperCase() , "FSrcPortCost".toUpperCase());
        acct_8805.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        acct_8805.put("FSrcPortMarketValueRatio".toUpperCase(), "FSrcPortMarketValueRatio".toUpperCase());
        
        acct_8805.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_8805.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_8805.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        acct_8805.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(), "FSTANDARDMONEYMARKETVALUETORAT".toUpperCase());        
        this.totalValueField.put("8805", acct_8805);
        
        Hashtable<String ,String > acct_8806 = new Hashtable<String ,String >();
        acct_8806.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_8806.put("FSrcAcctName", "FSrcAcctName");
        acct_8806.put("FSrcPortCost".toUpperCase() , "FSrcPortCost".toUpperCase());
        acct_8806.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        acct_8806.put("FSrcPortMarketValueRatio".toUpperCase(), "FSrcPortMarketValueRatio".toUpperCase());
        
        acct_8806.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_8806.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_8806.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        acct_8806.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(), "FSTANDARDMONEYMARKETVALUETORAT".toUpperCase());        
        this.totalValueField.put("8806", acct_8806);
        
        Hashtable<String ,String > acct_8807 = new Hashtable<String ,String >();
        acct_8807.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_8807.put("FSrcAcctName", "FSrcAcctName");
        acct_8807.put("FSrcPortCost".toUpperCase() , "FSrcPortCost".toUpperCase());
        acct_8807.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        acct_8807.put("FSrcPortMarketValueRatio".toUpperCase(), "FSrcPortMarketValueRatio".toUpperCase());
        
        acct_8807.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_8807.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_8807.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        acct_8807.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(), "FSTANDARDMONEYMARKETVALUETORAT".toUpperCase());        
        this.totalValueField.put("8807", acct_8807);
        
        Hashtable<String ,String > acct_8808 = new Hashtable<String ,String >();
        acct_8808.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_8808.put("FSrcAcctName", "FSrcAcctName");
        acct_8808.put("FSrcPortCost".toUpperCase() , "FSrcPortCost".toUpperCase());
        acct_8808.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        acct_8808.put("FSrcPortMarketValueRatio".toUpperCase(), "FSrcPortMarketValueRatio".toUpperCase());
        
        acct_8808.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_8808.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_8808.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        acct_8808.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(), "FSTANDARDMONEYMARKETVALUETORAT".toUpperCase());        
        this.totalValueField.put("8808", acct_8808);
        
        Hashtable<String ,String > acct_9000 = new Hashtable<String ,String >();
        acct_9000.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        //acct_9000.put("FSrcAcctName", "FSrcAcctName");
        acct_9000.put("FSrcPortCost".toUpperCase() , "FSrcPortCost".toUpperCase());
        acct_9000.put("FSrcPortMarketValue".toUpperCase(), "FSrcPortMarketValue".toUpperCase());
        acct_9000.put("FSrcPortMarketValueRatio".toUpperCase(), "FSrcPortMarketValueRatio".toUpperCase());
        
        acct_9000.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9000.put("FSTANDARDMONEYCOST".toUpperCase(), "FSTANDARDMONEYCOST".toUpperCase());
        acct_9000.put("FSTANDARDMONEYMARKETVALUE".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        acct_9000.put("FSTANDARDMONEYMARKETVALUETORAT".toUpperCase(), "FSTANDARDMONEYMARKETVALUETORAT".toUpperCase());        
        this.totalValueField.put("9000", acct_9000);
        
        Hashtable<String ,String > acct_9600 = new Hashtable<String ,String >();
        acct_9600.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        acct_9600.put("FSrcAcctName".toUpperCase(), "FSrcAcctName".toUpperCase());
        //acct_9600.put("FSrcPortMarketValue".toUpperCase() , "FSrcPortMarketValue".toUpperCase());
        
        acct_9600.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9600.put("FAcctName".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());     
        this.totalValueField.put("9600", acct_9600);
        
        
        Hashtable<String ,String > acct_9603 = new Hashtable<String ,String >();
        acct_9603.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        acct_9603.put("FSrcAcctName".toUpperCase(), "FSrcAcctName".toUpperCase());
        //acct_9603.put("FSrcPortMarketValue".toUpperCase() , "FSrcPortMarketValue".toUpperCase());
        
        acct_9603.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9603.put("FAcctName".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());     
        this.totalValueField.put("9603", acct_9603);
        
        Hashtable<String ,String > acct_9604 = new Hashtable<String ,String >();
        acct_9604.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        acct_9604.put("FSrcAcctName".toUpperCase(), "FSrcAcctName".toUpperCase());
        //acct_9604.put("FSrcPortMarketValue".toUpperCase() , "FSrcPortMarketValue".toUpperCase());
        
        acct_9604.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9604.put("FAcctName".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        this.totalValueField.put("9604", acct_9604);
        
        
        Hashtable<String ,String > acct_9612 = new Hashtable<String ,String >();
        acct_9612.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        acct_9612.put("FSrcAcctName".toUpperCase(), "FSrcAcctName".toUpperCase());
        //acct_9612.put("FSrcPortMarketValue".toUpperCase() , "FSrcPortMarketValue".toUpperCase());
        
        acct_9612.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9612.put("FAcctName".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        this.totalValueField.put("9612", acct_9612);
                
        Hashtable<String ,String > acct_9800 = new Hashtable<String ,String >();
        acct_9800.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        acct_9800.put("FSrcAcctName".toUpperCase(), "FSrcAcctName".toUpperCase());
        //acct_9800.put("FSrcPortMarketValue".toUpperCase() , "FSrcPortMarketValue".toUpperCase());
        
        acct_9800.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9800.put("FAcctName".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        this.totalValueField.put("9800", acct_9800);
        
        Hashtable<String ,String > acct_9801 = new Hashtable<String ,String >();
        acct_9801.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        acct_9801.put("FSrcAcctName".toUpperCase(), "FSrcAcctName".toUpperCase());
        //acct_9801.put("FSrcPortMarketValue".toUpperCase() , "FSrcPortMarketValue".toUpperCase());
        
        acct_9801.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9801.put("FAcctName".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        this.totalValueField.put("9801", acct_9801);
        
        Hashtable<String ,String > acct_9802 = new Hashtable<String ,String >();
        acct_9802.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        acct_9802.put("FSrcAcctName".toUpperCase(), "FSrcAcctName".toUpperCase());
        //acct_9802.put("FSrcPortMarketValue".toUpperCase() , "FSrcPortMarketValue".toUpperCase());
        
        acct_9802.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9802.put("FAcctName".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        this.totalValueField.put("9802", acct_9802);
        
        
        Hashtable<String ,String > acct_9991 = new Hashtable<String ,String >();
        acct_9991.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        acct_9991.put("FSrcAcctName".toUpperCase(), "FSrcAcctName".toUpperCase());
        //acct_9991.put("FSrcPortMarketValue".toUpperCase() , "FSrcPortMarketValue".toUpperCase());
        //acct_9991.put("FSrcPortCostRatio", "FSrcPortCostRatio");
        
        acct_9991.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9991.put("FAcctName".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());        
        this.totalValueField.put("9991", acct_9991);        
        
        Hashtable<String ,String > acct_9992 = new Hashtable<String ,String >();
        acct_9992.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        acct_9992.put("FSrcAcctName".toUpperCase() , "FSrcAcctName".toUpperCase());
        
        acct_9992.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9992.put("FAcctName".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        this.totalValueField.put("9992", acct_9992);
        
        Hashtable<String ,String > acct_9993 = new Hashtable<String ,String >();
        acct_9993.put("FSrcAcctCode".toUpperCase(), "FSrcAcctCode".toUpperCase());
        acct_9993.put("FSrcAcctName".toUpperCase() , "FSrcAcctName".toUpperCase());
        
        acct_9993.put("FAcctCode".toUpperCase(), "FAcctName".toUpperCase());
        acct_9993.put("FAcctName".toUpperCase(), "FSTANDARDMONEYMARKETVALUE".toUpperCase());
        this.totalValueField.put("9993", acct_9993);
	}	
	
	private String percentageAcctCodes = new String ("9991,9992,9993,FSrcPortCostRatio,FSrcPortMarketValueRatio,FSTANDARDMONEYCOSTTONETRATIO,FSTANDARDMONEYMARKETVALUETORAT"); //需要转百分比科目代码
	
	public void parseRowStr(String sRowStr) throws YssException {
		try {
            if (sRowStr.equals("")) {
                return;
            }
            String reqAry[] = null;
            reqAry = sRowStr.split("\n"); //这里是要获得参数
            this.reportType = reqAry[0].split("\r")[1];
            this.showType = reqAry[1].split("\r")[1];
            this.checkDate =reqAry[2].split("\r")[1];
            this.portCode = reqAry[3].split("\r")[1];
            this.setId = reqAry[4].split("\r")[1];
            this.showLevel = reqAry[5].split("\r")[1];
        } catch (Exception e) {
            throw new YssException("解析报表创建条件出错！", e);
        }
	}
	
	
	/**
	 * 生成报表
	 * */
	public String buildReport(String sType) throws YssException {
		this.preGuessValuecheck();
		String reportData = "";
		reportData = this.getCheckGuessValue(this.checkDate , this.setId);
		return reportData;
	}
	
	/**
	 * 估值核对数据
	 * */
	private String getCheckGuessValue(String checkDate , String setIdInt) throws YssException{
		ResultSet rs = null;
		StringBuffer result = new StringBuffer();
		
		String strSetId = YssFun.formatNumber(Integer.parseInt(setIdInt),"000");
		String year = checkDate.split("-")[0];
		
		boolean isWar = false ; //是否已加汇总换行区分
		
		String sql =  " select trgv.*,trcgv.*,trgvaccname.FacctName,trgvaccname.FacctLevel,trgvaccname.FAcctDetail,alc.fauxiacc, " +
                        " (case when trgv.FHzMx = 'hz' then ' ' " +
                        " when trgvaccname.facctdetail = 0 then '-' " +         
                        " when trgvaccname.facctdetail = 1 " +  
                        " and length(Trim(alc.fauxiacc)) is not null " + 
                        " and instr(trgv.FAcctCode,'_',1) = 0 then  '-' " +              
                        " else ' ' end) as symbol ," +
                        " (case " +
                        " when trgv.FHzMx = 'mx' " + 
                        " and trgvaccname.facctdetail = 1 " +
                        " and trgvaccname.Facctlevel = 4 and get_val_facctcode(trgv.facctcode) != 0 then " +
                        " substr(trgv.facctcode, 0, INSTR(trgv.facctcode, '_', 1, 1) - 1) " +
                        " else " +
                        " trgv.Facctcode " +
                        " end) as facctcode1, " +
                        " (case " +
                        "   when  trgv.FHzMx = 'mx'  " +
                        " and trgvaccname.facctdetail = 1  " +
                        " and trgvaccname.Facctlevel = 4  " +
                        " and get_val_facctcode(trgv.facctcode) != 0 then " +
                        " get_val_facctcode(trgv.facctcode) " +
                        " else " +
                        " 0 " +
                        " end) as facctcode2 " +                                
                        " from ( " +
					/**非合计项*/
						" select * from ( " +						
						" select 'mx' as FHzMx,FAcctcode,FPortCode, FDate ,sum(FAmount) as FAmount,sum(FStandardmoneyCost) as FStandardmoneyCost , " +
					    " sum(FStandardmoneyCosttonetratio) as FStandardmoneyCosttonetratio ,sum(Fmarketprice) as Fmarketprice," +
					    " sum(FStandardmoneymarketvalue) as FStandardmoneymarketvalue, sum(Fstandardmoneymarketvaluetorat) as Fstandardmoneymarketvaluetorat, " +
					    " sum(Fstandardmoneyappreciation) as Fstandardmoneyappreciation ,sum(FunitNvaMoney) as FunitNvaMoney " +
					    " from (" +
					    " select trgv.FAcctcode, trgv.FPortCode, trgv.FDate, trgv.FAmount as FAmount," +
					    " (case when trgv.FAcctCode like '2%' then -trgv.FStandardmoneyCost else trgv.FStandardmoneyCost " +
					    "  end) as FStandardmoneyCost, (case when trgv.FAcctCode like '2%' then - trgv.FStandardmoneyCosttonetratio " +
					    " else trgv.FStandardmoneyCosttonetratio end) as FStandardmoneyCosttonetratio, " +
					    " (case when trgv.FAcctCode like '2%' then - trgv.Fmarketprice * trgv.FExchangeRate " +
					    " else trgv.Fmarketprice * trgv.FExchangeRate end) as Fmarketprice, (case " +
					    " when trgv.FAcctCode like '2%' then -trgv.FStandardmoneymarketvalue else " +
					    " trgv.FStandardmoneymarketvalue end) as FStandardmoneymarketvalue, " +
					    " (case when trgv.FAcctCode like '2%' then -trgv.Fstandardmoneymarketvaluetorat else " +
					    " trgv.Fstandardmoneymarketvaluetorat end) as Fstandardmoneymarketvaluetorat," +
					    " (case when trgv.FAcctCode like '2%' then -trgv.Fstandardmoneyappreciation else " +
					    " trgv.Fstandardmoneyappreciation end) as Fstandardmoneyappreciation, round((case " +					   
					    " when trgv.famount <> 0 then (case when trgv.FAcctCode like '2%' " +
					    " then -trgv.Fstandardmoneycost / trgv.famount else trgv.Fstandardmoneycost / trgv.famount " +
                        " end) else 0 end), 2) as FunitNvaMoney " +
					    " from (select * " +
					    " from " + pub.yssGetTableName("tb_rep_guessvalue") +
					    " where FDate = " + dbl.sqlDate(this.checkDate) + 
					    " and FPortCode = " + dbl.sqlString(this.setId) +
					    " and FAcctClass <> '合计' ) trgv " +
					    " ) tgv group by tgv.FDate, tgv.FPortCode, tgv.Facctcode) trgv1 " +
						" union " +
					/**合计项*/
						" select * from (  " +
						" select  " +
						" 'hz' as  FHxMx, " +
						" trgv.FAcctcode, " +
						" trgv.FPortCode,trgv.FDate,trgv.FAmount as FAmount, " +
						" (case when trgv.FAcctCode like '2%' then -trgv.FStandardmoneyCost else trgv.FStandardmoneyCost end) as FStandardmoneyCost, " +
						" (case when trgv.FAcctCode like '2%'then -trgv.FStandardmoneyCosttonetratio else trgv.FStandardmoneyCosttonetratio end) as FStandardmoneyCosttonetratio , " +
						" (case when trgv.FAcctCode like '2%' then -trgv.Fmarketprice else trgv.Fmarketprice end) as Fmarketprice, " +
						" (case when trgv.FAcctCode like '2%' then -trgv.FStandardmoneymarketvalue else trgv.FStandardmoneymarketvalue end) as FStandardmoneymarketvalue, " +
						" (case when trgv.FAcctCode like '2%' then -trgv.Fstandardmoneymarketvaluetorat else trgv.Fstandardmoneymarketvaluetorat end) as Fstandardmoneymarketvaluetorat, " +
						" (case when trgv.FAcctCode like '2%' then -trgv.Fstandardmoneyappreciation else trgv.Fstandardmoneyappreciation end) as Fstandardmoneyappreciation,           " +
						" round((case when trgv.famount <> 0 then (case when trgv.FAcctCode like '2%' then -trgv.Fstandardmoneycost/trgv.famount else trgv.Fstandardmoneycost/trgv.famount end) else 0 end),2) as FunitNvaMoney " +
						" from (" +
						" select * from " + pub.yssGetTableName("tb_rep_guessvalue") + " where FDate = " + dbl.sqlDate(checkDate) + 
						" and FPortCode = " + dbl.sqlString(setIdInt) + " and FAcctClass = '合计' and FCurCode = ' ' order by FAcctcode " +
						" ) trgv " +
						" )trgv2     " +
						" ) trgv   " +
						" full join  (" +
						" select trcgv.* from " + // 优化 sql 避免 oracle的bug 产生不支持
						" ( " +
						" select  " +
						" FDate ,FAssetcode , FSrcAcctCode,FSrcAcctName,FSrcAmount,FSrcPortUnitCost,FSrcPortCost,FSrcPortCostRatio,FSrcPortMarketPrice, " +
						" FSrcPortMarketValue,FSrcPortMarketValueRatio,FSrcPortAppreciation,FCreatetime,FCHECKTIME,FCONVERACCTCODE " +
						" from " + pub.yssGetTableName("Tb_Rep_CheckGuessValue") +
						" where Fdate = " + dbl.sqlDate(checkDate) + 
						" and FAssetCode = " + dbl.sqlString(setIdInt) +
						" ) trcgv " + // 优化 sql 避免 oracle的bug 产生不支持
						" join (select distinct FPORTCODE ,fdate,FACCTCODE from " + pub.yssGetTableName("tb_rep_guessvalue") +  // 优化 sql 避免 oracle的bug 产生不支持
			            " where FDate = " + dbl.sqlDate(checkDate) + 	// 优化 sql 避免 oracle的bug 产生不支持
			            " and FPortCode = " + dbl.sqlString(setIdInt) + " ) trgv " + // 优化 sql 避免 oracle的bug 产生不支持
			            " on trcgv.fdate = trgv.fdate and trcgv.fassetcode = trgv.FPORTCODE and trcgv.FCONVERACCTCODE = trgv.FACCTCODE " +	// 优化 sql 避免 oracle的bug 产生不支持		
						" ) trcgv " +
						" on trgv.FPortCode = trcgv.FAssetCode and trgv.FDate = trcgv.FDate and trgv.FAcctcode = trcgv.FCONVERACCTCODE " +
						" left join  " +
						" ( " +
						" select distinct FPortCode,FDate,FAcctCode,FacctName,FacctLevel,FAcctDetail from " + pub.yssGetTableName("tb_rep_guessvalue") + 
						" where FDate = " + dbl.sqlDate(checkDate) + " and FPortCode = " + dbl.sqlString(setIdInt) +
						" ) trgvaccname " +
						" on trgv.FPortCode = trgvaccname.FPortCode and trgv.FDate = trgvaccname.FDate and trgv.FAcctCode = trgvaccname.FAcctCode " +
						" left join  A" + year + strSetId + "laccount alc on case when instr(trgv.facctcode, " +
			            " '_') > 0 then substr(trgv.facctcode, 0, instr(trgv.facctcode, '_') - 1) else trgv.facctcode end = alc.facctcode " +
			            " inner join ( select * from " + pub.yssGetTableName("Tb_Dao_Dict") + " tdd where tdd.fdictcode = 'ZHACCTCODECONVERT' ) tddt " +
			            " on trcgv.FSrcAcctCode = tddt.fsrcconent " +
			            " and trgv.Facctcode = (case when instr(tddt.fcnvconent,'|') > 0 then substr(tddt.fcnvconent,instr(tddt.fcnvconent,'|')+1) else tddt.fcnvconent end) " +			            
			            " order by FAcctCode1, FAcctCode2 ,trgv.FStandardmoneymarketvalue desc, trgvaccname.facctlevel" ;
		
		try {
			rs = dbl.openResultSet(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			double assetNav = this.getMarketValue(); //资产净值
			
			while(rs.next()) {
				StringBuffer rowGuessData = new StringBuffer(); 
				boolean isEqual = true ;	//是否一致
				
				int level = rs.getInt("FacctLevel");	//级别
				//int isDetail = rs.getInt("FAcctDetail"); //是否明细项
				String hzmx = rs.getString("FHzMx");
				String symbol = rs.getString("symbol");			
				String tmpAcctCode = rs.getString("FAcctCode");
				
				if("hz".equalsIgnoreCase(hzmx) && !isWar){
					isWar = true;
					rowGuessData.append("sp\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
					result.append(this.buildRowCompResult(rowGuessData.toString())).append("\r\n"); //拼接行
					rowGuessData = new StringBuffer();
				}
				
				rowGuessData.append( (level == 0 ? "a" : level ) + " \t" + symbol).append("\t"); //伸缩标志
				
				
				
				for (int i = 0; i < this.checkManagerFields.length; i++) { //托管人数据相关字段					
					
					/**
					 * 非合计项科目数据
					 * */
					if("mx".equalsIgnoreCase(hzmx)){
						
						if("String".equalsIgnoreCase(this.fieldsType[i])){ //托管人数据相关字段对应类型							
							/**判断是否一致*/
							String manageData = rs.getString(this.checkManagerFields[i]) ;
							String trusteeData = rs.getString(this.checkTrusteeFields[i]);							
							
							rowGuessData.append(manageData == null ? " " : manageData ).append("\t");
							rowGuessData.append(trusteeData == null ? " " : trusteeData ).append("\t");
							
						}
						else if("Number".equalsIgnoreCase(this.fieldsType[i])){	//托管人数据相关字段对应类型
							//FSTANDARDMONEYMARKETVALUE ,FSTANDARDMONEYCOSTTONETRATIO ,FSTANDARDMONEYMARKETVALUETORAT
							
							/**判断是否一致*/
							double manageData = rs.getDouble(this.checkManagerFields[i]) ;
							double trusteeData = rs.getDouble(this.checkTrusteeFields[i]);
							
													
							rowGuessData.append(manageData).append("\t");
							
							//市值占净比重新手工算
							if("FSTANDARDMONEYMARKETVALUETORAT".equalsIgnoreCase(this.checkTrusteeFields[i])){
								double navToRat = YssD.round(YssD.div(rs.getDouble("FSTANDARDMONEYMARKETVALUE"),assetNav),6);								
								rowGuessData.append(navToRat).append("\t");
							}else							
								rowGuessData.append(trusteeData).append("\t");							
						}
						
					/**
					 * 合计项数据
					 * */
					}else if("hz".equalsIgnoreCase(hzmx)){
						
						String managerFieldName = this.checkManagerFields[i];
						String trusteeFieldName = this.checkTrusteeFields[i];
						//String fieldType = this.fieldsType[i];						
						
						if( this.totalValueField.containsKey(tmpAcctCode) ){
							
							if( this.totalValueField.get(tmpAcctCode) instanceof Hashtable ){ 	//是否为合计项字段
							
								Hashtable< String , String > fieldNames = this.totalValueField.get(tmpAcctCode);									
								
								String managerDataStr = "" ;
								String trusteeDataStr = "" ;
								double managerDataDou = 0 ;
								double trusteeDataDou = 0 ;
								
								if(fieldNames.containsKey(managerFieldName) && fieldNames.containsKey(trusteeFieldName)){
									
									/**判断合计项需要的字段类型*/
									if(this.fieldsTypes.containsKey(managerFieldName)){
										if("String".equalsIgnoreCase(this.fieldsTypes.get(fieldNames.get(managerFieldName)))){
											//获取合计项需要取且对应字段的值
											managerDataStr = rs.getString(fieldNames.get(managerFieldName));
											trusteeDataStr = rs.getString(fieldNames.get(trusteeFieldName));
												
											rowGuessData.append(managerDataStr == null ? " " : managerDataStr ).append("\t");
													
											if(this.percentageAcctCodes.contains(tmpAcctCode) && "Number".equalsIgnoreCase(this.fieldsTypes.get(fieldNames.get(trusteeFieldName)))){
												rowGuessData.append(trusteeDataStr == null ? " " : String.valueOf(YssD.mul(Double.parseDouble(trusteeDataStr),100))).append("%\t");
											}else{
												rowGuessData.append(trusteeDataStr == null ? " " : trusteeDataStr ).append("\t");
											}
										}else if("Number".equalsIgnoreCase(this.fieldsTypes.get(fieldNames.get(managerFieldName)))){
											//获取合计项需要取且对应字段的值
											managerDataDou = rs.getDouble(fieldNames.get(managerFieldName));
											trusteeDataDou = rs.getDouble(fieldNames.get(trusteeFieldName));
											
											
											rowGuessData.append(managerDataDou).append("\t");
											rowGuessData.append(trusteeDataDou).append("\t");												
										}
									}else{
										rowGuessData.append(" ").append("\t");
										rowGuessData.append(" ").append("\t");
									}
								}else{
									rowGuessData.append(" ").append("\t");
									rowGuessData.append(" ").append("\t");
								}								
							}else{
								rowGuessData.append(" ").append("\t");
								rowGuessData.append(" ").append("\t");
							}
							
						/**
						 * 不需要取值的合计项
						 * */
						}else{
							break;
						}
					}
				}
				
				
				/*for (int i = 0; i < this.otherField.length; i++) {
					if("mx".equalsIgnoreCase(hzmx) || ( "hz".equalsIgnoreCase(hzmx) && this.totalValueField.containsKey(tmpAcctCode) ) )
						rowGuessData.append(rs.getString(this.otherField[i]) == null ? " " : rs.getString(this.otherField[i])).append("\t");
					else if("hz".equalsIgnoreCase(hzmx) && !this.totalValueField.containsKey(tmpAcctCode))
						break;
				}*/
				
				if("mx".equalsIgnoreCase(hzmx) || ("hz".equalsIgnoreCase(hzmx) && this.totalValueField.containsKey(tmpAcctCode))){
					//rowGuessData.append(level).append("\t");  //级别
					//rowGuessData.append(isEqual ? "yz" : "byz" ).append("\t"); //显示类别				
					result.append(this.buildRowCompResult(rowGuessData.toString())).append("\r\n"); //拼接行
				}else if("hz".equalsIgnoreCase(hzmx) && !this.totalValueField.containsKey(tmpAcctCode))
					continue;				
								
				if(rs.isLast()){
					result.append(this.buildRowCompResult("sp\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ")).append("\r\n");
					result.append(this.buildRowCompResult("sp\t \t上传时间：\t" + rs.getString("FCreateTime") + " \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ")).append("\r\n");
					result.append(this.buildRowCompResult("sp\t \t核对时间：\t" + rs.getString("FCheckTime") + " \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ") ).append("\r\n");		
				}
			}
			
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("获取估值表数据出错：\n"+e.getMessage());
            throw new YssException("获取估值表数据出错：\n"+e.getMessage());
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("获取估值表数据出错：\n"+e.getMessage());
		            throw new YssException("获取估值表数据出错：\n"+e.getMessage());
				}
			}
		}
		return result.toString();
	}
	
	/**
	 * 获取资产净值
	 * */
	private double getMarketValue() throws YssException{
		
		double sResult = 0;
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = " select * from " + pub.yssGetTableName("tb_rep_guessvalue") + " g where FDate = " + dbl.sqlDate(this.checkDate) +
            		 " and FPortCode = " + dbl.sqlString(this.setId) + " and FAcctClass = '合计' and FAcctCode = '9000' and FCurCode = ' '";
                        
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
            	sResult = rs.getDouble("FSTANDARDMONEYMARKETVALUE");
            }            
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            throw new YssException(e);
        } finally {
        	dbl.closeResultSetFinal(rs);
        }

		return sResult;
	}
	
	/**
	 * 估值预处理
	 * */
	private void preGuessValuecheck() throws YssException{
		try {
			dbl.loadConnection().setAutoCommit(true);
		
			String sql = "update " + pub.yssGetTableName("Tb_Rep_CheckGuessValue") + " set FCHECKUSER = " + dbl.sqlString(pub.getUserCode())+
						 ",FCHECKTIME = TO_CHAR(SYSDATE, 'YYYYMMDD HH24:MI:SS') where Fdate = " + dbl.sqlDate(this.checkDate) + 
						 " and FAssetCode = " + dbl.sqlString(this.getSetIdByPortCode(this.portCode));
			dbl.executeSql(sql);
			dbl.loadConnection().setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("执行估值表预处理出错：\n"+e.getMessage());
            throw new YssException("执行估值表预处理出错：\n"+e.getMessage());
		}
	}	
	
	/**
	 * 获取套帐
	 * */
	private String getSetIdByPortCode(String portCode) throws YssException{
		String setId = "";
		YssFinance finace = new YssFinance();
		finace.setYssPub(this.pub);
		setId = finace.getBookSetId(portCode);
		return setId;
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
            hmCellStyle = getCellStyles("DS_CheckGuessValue");
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("DS_CheckGuessValue") +
                " and FCheckState = 1 order by FOrderIndex";
            rs = dbl.openResultSet(strSql);
            for (int i = 0; i < sArry.length; i++) {
                sKey = "DS_CheckGuessValue" + "\tDSF\t-1\t" + i;
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
            return strReturn;
        } catch (Exception e) {
        	System.out.println("获取估值表核对报表格式出错：\n"+e.getMessage());
            throw new YssException("获取估值表核对报表格式出错：\n"+e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
