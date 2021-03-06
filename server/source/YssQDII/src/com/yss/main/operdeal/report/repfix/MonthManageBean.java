package com.yss.main.operdeal.report.repfix;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.exceptionMappingType;
import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 *add by huangqirong 2012-03-07 story #2116 
 *合格境内机构投资者境外证券投资月报表(一) 
 * modify by huangqirong 2012-10-08 story #3120
  */
public class MonthManageBean extends BaseBuildCommonRep{
	
	protected CommonRepBean repBean;
	private String startDate = "";	//开始日期 
	private String endDate = "";		//结束日期
	private String portCode = "";	//组合
	private String inAcctCodes = "";	//境内账户	
	private String outAcctCodes = "";	//境外账户
	private String balanceAcctCodes = "";//境外结算账户
	private String rateSource  = "";//汇率来源
	
	private String sRepCode = "";	//20121109 added by liubo.Story #3144.报表代码
	private String sOperType = "Search";		//20121113 added by liubo.Story #3144.操作类型（“生成”或是“查询”）
	
	private String sCurShowing = "0";			//20130220 added by liubo.Story #3517.QDII境内外币托管账户情况表，取哪些币种的账户。0表示所有账户，1表示人民币，2表示外币
	
	private String sPortCuryCode = "CNY";
	
	private String date = "";
    //------add add by zhaoxianlin 20121225 STORY #3383 外管局报表
	private String orgCode = " ";  //机构代码
	private String orgName = " ";  //机构名称
	private String productName = " "; //产品/客户名称
	private String curyCode = " ";  //币种
	double subDeposit = 0; //存款投资_定期存款
	double subFundCost = 0; //基金投资_开发式_货币成本
	double subFundMV = 0; //基金投资_开发式_货币成本
	//-----end----------
	

	/**
	 * 科目代码是根据科目性质得到
	 * 后续是根据科目性质取数 
	 * */
	
	/*投资情况*/	
	//private String [] acctCode1= new String[]{"1002","1202\t存款投资_定期存款(<1年)\t债券投资（短期债券：计息截止日-计息起始日<1年）","1103","1102,1107","1105","3101,1106\t其他衍生工具","1104,1108,1109"};
	private String [] acctCode1= new String[]{
			"银行存款",
			"买入返售证券\t存款投资_定期存款(<1年)\t债券投资（短期债券：计息截止日-计息起始日<1年）",
			"债券投资",
			"股票投资,存托凭证",
			"基金投资",
			"远期投资,权证投资\t其他衍生工具",
			"债券投资_资产证券,房地产信托凭证,信托产品投资"
			};
	
	/*银行存款*/
	//private String [] acctCode2 = new String[]{"10020101,1021","100202"};
	private String [] acctCode2 = new String[]{
			"银行存款-w- and FCurCode = 'CNY'",
			"银行存款-w- and FCurCode <> 'CNY'"
			};	
	
	/*收入情况 -- 利息收入*/
	//private String [] acctCode3= new String []{"60110301,60110351,601104,60110311","6302"};
	private String [] acctCode3= new String []{
			"存款利息收入_活期存款,存款利息收入_申购款,买入返售证券收入,存款利息收入_清算备付金",
			"其他收入"
			};
	
	/*支出*/
	//private String [] acctCode4 = new String []{"2203,2204","410401-m-223201","2207","220601"};
	private String [] acctCode4 = new String []{
			"应付赎回款,应付赎回费",
			"收益分配_应付收益-m-应付收益",
			"应付托管费",
			"应付管理人报酬_管理费"
			};
	
	/*各类手续费 - 其他支出*/
	//private String [] acctCode5 =new String []{"660501","660502,660503,660504,660505"};
	private String [] acctCode5 =new String []{
			"其他费用_银行费用",
			"其他费用_信息披露费,其他费用_审计费,其他费用_维护费,其他费用_税务顾问费"
			};
	
	
	private double dAccrual = 0;				//本月发生额折美元
	private double dOriginalAccrual = 0;		//本月原币发生额
	private double dLastBalances =0;			//历年合计折美元
	private double dOriginalLastBalances =0;	//历年本位币合计
	
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
	
	public void parseRowStr(String sRowStr) throws YssException {
		try {
            if (sRowStr.equals("")) {
                return;
            }
            String reqAry[] = null;
            reqAry = sRowStr.split("\n"); //这里是要获得参数
            this.startDate = reqAry[0].split("\r")[1];
            this.endDate = reqAry[1].split("\r")[1];
            this.inAcctCodes =reqAry[2].split("\r")[1];
            this.portCode = reqAry[3].split("\r")[1];
            this.outAcctCodes = reqAry[4].split("\r")[1];
            this.balanceAcctCodes = reqAry[5].split("\r")[1];
            this.rateSource = reqAry[6].split("\r")[1];
            //20121107 added by liubo.Story #3144
            //============================
            
            //报表类型
            if (reqAry.length >= 8)
            {
            	this.sRepCode = reqAry[7].split("\r")[1];
            }
            //报表操作方式
            if (reqAry.length >= 9)
            {
            	this.sOperType = reqAry[8].split("\r")[1];
            }
            //============end================

            if (reqAry.length >= 10)
            {
            	this.sCurShowing = reqAry[9].split("\r")[1];
            }
        } catch (Exception e) {
            throw new YssException("解析报表创建条件出错！", e);
        }
	}
	
	/**
	 * 生成报表
	 * */
	public String buildReport(String sType) throws YssException {
		StringBuffer sbf = new StringBuffer();
		if(this.portCode.contains("-"))
		{
			String[] portCodes = this.portCode.split(",");
			String curAssetGroup = pub.getAssetGroupCode();
			for(int i=0;i<portCodes.length;i++)
			{
				pub.setPrefixTB(portCodes[i].split("-")[0]);
				pub.setAssetGroupCode(portCodes[i].split("-")[0]);
				this.portCode = portCodes[i].split("-")[1];
				sbf.append(this.buildShowData());//modified by yeshenghong 20130410  story3751
			}
			pub.setPrefixTB(curAssetGroup);
			pub.setAssetGroupCode(curAssetGroup);
			return sbf.toString();
		}else
		{
			return this.buildShowData();
		}
	}

	//20121110 modified by liubo.Story #3144
	private String buildShowData() throws YssException 
	{
		//从前台传入的操作请求为查询时，调用查询数据的方法
		if (sOperType.trim().equals("Search"))
		{
			return searchWGJRep();
		}
		//从前台传入的操作请求为 生成时，根据不同的报表代码，分别调用不同的报表生成方法进行生成
		else
		{
	/**
			if (this.sRepCode.equals("REP_HG_WGJ_TZYB1"))
			if (this.sRepCode.equals("REP_HG_WGJ_JNWBTGZH"))
			{
				return getThe1stWGJReportOper();
			}
			else if (this.sRepCode.equals("REP_HG_WGJ_TZYB2"))
			{
				return getThe2ndWGJRep();
			}
			else if (this.sRepCode.equals("REP_HG_WGJ_TZYB3"))
			{
				return getThe3rdWGJRep();
			}
			*/
			if (this.sRepCode.equals("REP_HG_WGJ_JNWBTGZH"))
			{
				return getThe1stWGJReportOper();  //QDII境内外币托管账户情况
			}
			else if (this.sRepCode.equals("REP_HG_WGJ_JWZQTZ"))
			{
				return getSecInvestWGJRep(); //QDII境外证券投资信息
			}
			else if (this.sRepCode.equals("REP_HG_WGJ_ZJHCRJGH"))
			{
				return getCashFlow2WGJRep();  //资金汇出入及结购汇明细信息
			}
			else if(this.sRepCode.equals("REP_HG_WGJ_ZHXX")){
				
				return getAcctInfoWGJRep(); //STORY #3384 外管局报表——QDII账户信息
			}
			/**Start 20130620 added by liubo.Story #4000 旧版外管局月报——境内机构投资者境外证券投资月报表*/
			else if(this.sRepCode.equals("RepManagerMonth"))
			{
				return getRepManagerMonth();
			}
			/**End 20130620 added by liubo.Story #4000 旧版外管局月报——境内机构投资者境外证券投资月报表*/
			else 
			{
				return "";
			}
		}		
	}
	
	private double getBOMNumber(String sSqlField) throws YssException
	{
		ResultSet rs = null;
		StringBuffer bufSql = new StringBuffer();
		double dReturn = 0.0;
		
		try
		{
			bufSql.append("select Nvl(" + sSqlField + ",0) as " + sSqlField);
			bufSql.append(" from " + pub.yssGetTableName("Tb_Rep_OverseasSecRep"));
			bufSql.append(" where FPortCode = " + dbl.sqlString(this.portCode));
			bufSql.append(" and FYearMonth = " + YssFun.formatDate(YssFun.addMonth(YssFun.toDate(this.endDate), -1),"yyyyMM"));
			
			rs = dbl.queryByPreparedStatement(bufSql.toString());
			
			while(rs.next())
			{
				dReturn = rs.getDouble(sSqlField);
			}
		}
		catch(Exception ye)
		{
			throw new YssException(ye);
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return dReturn;
	}
	
	/**
	 * 20130623 added by liubo.Story #4000.需求北京-(农业银行)QDIIV4(高)20130527001
	 * 此方法用于将旧版外管局月报的每个项目的月初数、发生额、月末数三个字段进行拼接
	 * @param dFirstCount	某个项目的月初数
	 * @param dCurrentCount 本月发生额
	 * @param dLastCount	月末数
	 * @return	返回拼接好的字符串
	 */
	private String formatRepState(double dFirstCount,double dCurrentCount,double dLastCount)
	{
		StringBuffer sReturn = new StringBuffer();
		
		sReturn.append(YssFun.formatNumber(dFirstCount, "#,##0.00")).append("\t"); 
		sReturn.append(YssFun.formatNumber(dCurrentCount, "#,##0.00")).append("\t"); 
		sReturn.append(YssFun.formatNumber(dLastCount, "#,##0.00")).append("\r\n"); 
			
		return sReturn.toString();
	}

	/**
	 * 20130623 added by liubo.Story #4000.需求北京-(农业银行)QDIIV4(高)20130527001
	 * 此方法用于将旧版外管局月报的每个项目的月初数、发生额、月末数三个字段，简单拼接成类似“字段1,字段2,字段3”这种形式的语句
	 * 报表生成的主方法中直接调用可以比较简单的生成insert语句
	 * @param dFirstCount
	 * @param dCurrentCount
	 * @param dLastCount
	 * @return
	 */
	private String formatSqlStatement(double dFirstCount,double dCurrentCount,double dLastCount)
	{
		StringBuffer sReturn = new StringBuffer();
		
		sReturn.append(YssFun.formatNumber(dFirstCount, "###0.00")).append(","); 
		sReturn.append(YssFun.formatNumber(dCurrentCount, "###0.00")).append(","); 
		sReturn.append(YssFun.formatNumber(dLastCount, "###0.00")).append(","); 
			
		return sReturn.toString();
	}
	
	/**
	 * 20130623 added by liubo.Story #4000.需求北京-(农业银行)QDIIV4(高)20130527001
	 * 此方法用于获取指定投资组合的组合货币代码
	 * @param FPortCode	指定的投资组合
	 * @return	该组合的组合货币代码
	 * @throws YssException
	 */
	private String getPortCury(String FPortCode) throws YssException
	{
		String sReturn = "CNY";
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			strSql = "select * from " + pub.yssGetTableName("tb_para_portfolio") +
					 " where FPortCode = " + dbl.sqlString(FPortCode);
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				sReturn = rs.getString("FPortCury");
			}
		}
		catch(Exception ye)
		{
			throw new YssException();
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return sReturn;
	}
	
	
	/**
	 * 20130620 added by liubo.Story #4000.需求北京-(农业银行)QDIIV4(高)20130527001
	 * 旧版外管局月报的合计项
	 */
	private double firstBalances = 0;		//月初数合计
	private double lastBalances =0;			//月末数合计
	private double adds = 0;				//本月支出发生额合计
	private double minuses = 0;				//本月收入发生额合计
	
	/**
	 * 20130620 added by liubo.Story #4000.需求北京-(农业银行)QDIIV4(高)20130527001
	 * 旧版外管局月报的生成方法
	 * @return
	 * @throws YssException
	 */
	private String getRepManagerMonth() throws YssException
	{
    	StringBuffer result=new StringBuffer();
    	StringBuffer bufSql = new StringBuffer();
		String sql = "";

		String tempRw = "";
		double firstCount = 0;		//月初数
		double currentCount = 0;	//本月发生额
		double lastCount = 0;		//月末数

        boolean bTrans = false; 
        Connection conn = dbl.loadConnection();
    	
    	String nowDate = (new SimpleDateFormat("yyyy-MM-dd")).format(new java.util.Date());
    	String [] ndate = nowDate.split("-");
    	String [] sdate =this.startDate.split("-");
    	String [] edate = this.endDate.split("-");	    	    	
    	
    	String emaxDay = YssFun.formatDate(this.getStringData("select last_day(to_date("+dbl.sqlString(this.endDate)+",'yyyy-MM-dd')) as FlastDay from dual","FlastDay"),"yyyy-MM-dd");
    	
    	if(Integer.parseInt(edate[0]) > Integer.parseInt(ndate[0]))
    		throw new YssException("请设置完整的会计期间！");
    	
    	if( Integer.parseInt(edate[0]) >= Integer.parseInt(ndate[0]) && Integer.parseInt(edate[1]) > Integer.parseInt(ndate[1]))
    		throw new YssException("请设置完整的会计期间！");
    	
    	if( Integer.parseInt(edate[0]) >= Integer.parseInt(ndate[0]) && 
    			Integer.parseInt(edate[1]) >= Integer.parseInt(ndate[1]) && 
    			Integer.parseInt(edate[2]) > Integer.parseInt(ndate[2]))
    			throw new YssException("查询日期超出当前会计期间！");
    	
    	
    	if( !sdate[0].equalsIgnoreCase(edate[0]))
    		throw new YssException("报表查询不支持跨年操作！");	    	
    	
    	if(Integer.parseInt(sdate[0]) >= Integer.parseInt(edate[0]) && Integer.parseInt(sdate[1]) > Integer.parseInt(edate[1]))
    		throw new YssException("请设置完整的会计期间！"); 
    	
    	if(Integer.parseInt(sdate[1]) != Integer.parseInt(edate[1]))
    		throw new YssException("请设置完整的会计期间！"); 
    	
    	if(!this.startDate.endsWith("-01"))
    		throw new YssException("请设置完整的会计期间！");
    	
    	if(!this.endDate.endsWith(emaxDay.split("-")[2]))
    		throw new YssException("请设置完整的会计期间！");
    	
    	
    	String tempTotal = "";
		try {
			
			sql = " delete from " + pub.yssGetTableName("Tb_Rep_OverseasSecRep") +
				  " where FPortCode = " + dbl.sqlString(this.portCode) +
				  " and FYearMonth = " + dbl.sqlString(YssFun.formatDate(this.endDate,"yyyyMM"));
			
			dbl.executeSql(sql);
			
			this.sPortCuryCode = getPortCury(this.portCode);
			
			bufSql.append("insert into " + pub.yssGetTableName("Tb_Rep_OverseasSecRep "));
			bufSql.append(" values(" + dbl.sqlString(this.portCode) + ",");
			bufSql.append(dbl.sqlString(YssFun.formatDate(this.endDate, "yyyyMM"))).append(",");
			
			/*投资情况*/
			
			//银行存款
			sql = getFinaSysStatement("b.FAcctAttr like '银行存款%' and a.FisDetail = 1", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FYHCKYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));

			this.firstBalances += firstCount; 
			this.minuses += (lastCount - firstCount);
			this.lastBalances += lastCount;

			//货币市场工具
			sql = getFinaSysStatement("(b.FAcctAttr like '买入返售证券%' or b.FAcctAttr like '存款投资_定期存款(<1年)%' " +
					"or b.FAcctAttr like '债券投资（短期债券：计息截止日-计息起始日<1年）%') and a.FisDetail = 1",
					null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount += getBOMNumber("FHBSCYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));
			
			this.firstBalances += firstCount; 
			this.minuses += (lastCount - firstCount);
			this.lastBalances += lastCount;

			//债券
			sql = getFinaSysStatement("b.FAcctAttr like '债券投资%' and a.FisDetail = 1", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FZQYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));
			
			this.firstBalances += firstCount; 
			this.minuses += (lastCount - firstCount);
			this.lastBalances += lastCount;
			
			//股票
			sql = getFinaSysStatement("(b.FAcctAttr like '股票投资%' or b.FAcctAttr like '存托凭证%') and a.FisDetail = 1",
					null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FGPYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));
			
			this.firstBalances += firstCount; 
			this.adds = 0;
			this.minuses += (lastCount - firstCount);
			this.lastBalances += lastCount;

			//基金
			sql = getFinaSysStatement("b.FAcctAttr like '基金投资%' and a.FisDetail = 1", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FJJYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));
			
			this.firstBalances += firstCount; 
			this.minuses += (lastCount - firstCount);
			this.lastBalances += lastCount;

			//衍生产品
			sql = getFinaSysStatement("(b.FAcctAttr like '远期投资%' or b.FAcctAttr like '权证投资%' " +
					"or b.FAcctAttr like '其他衍生工具%') and a.FisDetail = 1",
					null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FYSCPYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));
			
			this.firstBalances += firstCount; 
			this.minuses += (lastCount - firstCount);
			this.lastBalances += lastCount;

			//其他投资
			sql = getFinaSysStatement("(b.FAcctAttr like '资产支持证券%' or b.FAcctAttr like '房地产信托%' " +
					"or b.FAcctAttr like '1109%') and a.FisDetail = 1",
					null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FQTTZYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));
			
			this.firstBalances += firstCount; 
			this.minuses += (lastCount - firstCount);
			this.lastBalances += lastCount;

			//投资情况合计
			result.append(formatRepState(getBOMNumber("FTZQKHJYueMo"),(this.adds + this.minuses),this.lastBalances));
			bufSql.append(formatSqlStatement(getBOMNumber("FTZQKHJYueMo"),(this.adds + this.minuses),this.lastBalances));	
			
			this.firstBalances = 0; 
			this.adds = 0;
			this.minuses  = 0; 
			this.lastBalances  = 0; 
							
			/*境内托管账户情况*/
			
			//人民币存款
			sql = getFinaSysStatement("a.FacctCode in ('1002','1021') ", "CNY", "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FCNYCKYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , lastCount - firstCount); 

			//外币存款
			sql = getFinaSysStatement("a.FacctCode in ('1002','1021') and a.FCurCode <> 'CNY'", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FWHCNYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , lastCount - firstCount); 
			
			//购汇
			firstCount = getBOMNumber("FGHYueMo");
			tempRw =firstCount+"\t";
			
			sql =  this.getForeignRateData1(this.startDate , this.endDate, this.inAcctCodes, this.portCode , "1",
					this.rateSource, this.rateSource);
			currentCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"FBMoney","FBCuryCode",this.portCode);
			tempRw +=currentCount+"\t";
			
			sql =  this.getForeignRateData1(this.startDate , this.endDate, this.inAcctCodes, this.portCode , "2",
					this.rateSource, this.rateSource);
			lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"FBMoney","FBCuryCode",this.portCode);
			tempRw +=lastCount;
			
			result.append(tempRw + "\r\n");
			bufSql.append(formatSqlStatement(firstCount,currentCount,lastCount));
			
			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , currentCount); 
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			//境外证券投资外汇账户划入
			firstCount = getBOMNumber("FJWTGZHHRYueMo");
			tempRw = firstCount + "\t";
			
			sql = this.getForeignRateData2(this.startDate ,  this.endDate, 
					this.inAcctCodes, this.outAcctCodes, this.portCode, "1");
			currentCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate),"FBMoney","FBCuryCode",this.portCode);
			tempRw += currentCount+"\t";
			
			sql = this.getForeignRateData2(this.startDate ,  this.endDate, 
					this.inAcctCodes, this.outAcctCodes, this.portCode, "2");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate),"FBMoney","FBCuryCode",this.portCode);
			tempRw += lastCount;
			
			result.append(tempRw + "\r\n");
			bufSql.append(formatSqlStatement(firstCount,currentCount,lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , currentCount); 
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			
			//申购款汇入

			sql = getFinaSysStatement("a.FacctCode = '1207' ", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FSGKHRYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , lastCount - firstCount); 
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			//利息收入 

			sql = getFinaSysStatement("a.FacctCode = '1204' ", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FLXSRYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));
			
			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , lastCount - firstCount); 

			//其他收入
			
			result.append("0\t0\t0\r\n");
			bufSql.append("0,0,0,");

			//账户收入情况合计
			result.append(formatRepState(getBOMNumber("FZHSRHJYueMo"),(this.adds + this.minuses),this.lastBalances));
			bufSql.append(formatSqlStatement(getBOMNumber("FZHSRHJYueMo"),(this.adds + this.minuses),this.lastBalances));	
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			
			this.firstBalances = 0; 
			this.adds = 0;
			this.minuses = 0;
			this.lastBalances = 0;
			
			//结汇
			firstCount = getBOMNumber("FJHYueMo");
			tempRw = firstCount+"\t";
			
			sql =  this.getForeignRateData4(this.startDate , this.endDate, this.inAcctCodes, this.portCode , "1",
					this.rateSource, this.rateSource);
			currentCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"FSMoney","FSCuryCode",this.portCode);
			tempRw += currentCount +"\t";
			
			sql =  this.getForeignRateData4(this.startDate , this.endDate, this.inAcctCodes, this.portCode , "2",
					this.rateSource, this.rateSource);
			lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"FSMoney","FSCuryCode",this.portCode);
			tempRw +=lastCount;
			
			result.append(tempRw + "\r\n");
			bufSql.append(formatSqlStatement(firstCount,currentCount,lastCount));
			
			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			//this.adds += currentCount ;
			this.minuses = YssD.add(this.minuses , currentCount); 
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			
			//划往境外证券投资外汇账户
			firstCount = getBOMNumber("FHWJWTGYueMo");
			tempRw = firstCount +"\t";

			sql = this.getForeignRateData5(this.startDate ,  this.endDate, this.inAcctCodes, this.outAcctCodes, this.portCode, "1");
			currentCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "FSMoney", "FSCuryCode", this.portCode);
			tempRw += currentCount +"\t";

			sql = this.getForeignRateData5(this.startDate ,  this.endDate, this.inAcctCodes, this.outAcctCodes, this.portCode, "2");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "FSMoney", "FSCuryCode", this.portCode);
			tempRw += lastCount;

			result.append(tempRw + "\r\n");
			bufSql.append(formatSqlStatement(firstCount,currentCount,lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , currentCount); 
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			//支付赎回款
			sql = getFinaSysStatement("a.FacctCode in ('2203','2204') ", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FZFSHKYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , lastCount - firstCount); 
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			//分红
			sql = getFinaSysStatement("a.FacctCode in ('410401,223201') ", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FFHYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , lastCount - firstCount); 
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			//托管费
			sql = getFinaSysStatement("a.FacctCode in ('2207') ", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FTGFYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , lastCount - firstCount); 
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			//管理费
			sql = getFinaSysStatement("a.FacctCode in ('220601') ", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FGLFYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , lastCount - firstCount); 
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			//各类手续费
			sql = getFinaSysStatement("a.FacctCode in ('66050101') ", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FSXFYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , lastCount - firstCount); 
			
			firstCount = 0;
			currentCount = 0;
			lastCount = 0;
			
			//其他支出
			sql = getFinaSysStatement("a.FacctCode in ('2501') ", null, "FBEndBal");
			lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "Cnt", null, this.portCode);
			firstCount = getBOMNumber("FQTZCYueMo");
			
			result.append(formatRepState(firstCount,(lastCount - firstCount),lastCount));
			bufSql.append(formatSqlStatement(firstCount,(lastCount - firstCount),lastCount));

			this.firstBalances = YssD.add(this.firstBalances, firstCount );
			this.lastBalances = YssD.add(this.lastBalances, lastCount );
			this.minuses = YssD.add(this.minuses , lastCount - firstCount); 
			
			//账户支出情况合计
			tempTotal = getBOMNumber("FZHZCHJYueMo") + "\t" + (this.adds + this.minuses )+ "\t" + this.lastBalances;  //合计
			tempTotal = this.buildRowCompResult(tempTotal)+"\r\n";
			result.append(tempTotal);
			
			bufSql.append(formatSqlStatement(firstCount,(this.adds + this.minuses),lastCount));
			bufSql.append("' ',1," + dbl.sqlString(pub.getUserCode()) + ",'" + YssFun.formatDate(new java.util.Date()) + "',");
			bufSql.append(dbl.sqlString(pub.getUserCode()) + ",'" + YssFun.formatDate(new java.util.Date()) + "')");
			
			this.firstBalances = 0; 
			this.adds = 0;
			this.minuses = 0;
			this.lastBalances = 0;
			
			dbl.executeSql(bufSql.toString());

	    	conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);   
			
		} catch (Exception e) {
			throw new YssException("获取中行人寿应收利息出错： \n" + e.getMessage());			
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}    	
		return result.toString();
	}
	
	
	
	/**
	 * add by zhaoxianlin 20121225 STORY #3384 外管局报表——QDII账户信息
	 */
	public String getAcctInfoWGJRep() throws YssException{
		String strSql = "";
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		String opendate = null;
		String maturedate = null;
		try{
			 strSql = " select m.fportName as FproductName,a1.FmanagerCode as FQDorgCode,a1.FmanagerName as FQDorgName," +
			 		"a3.FTrusteeCode as FQDoutTruCode,a3.FTrusteeName as FQDoutTruName,' ' as FQDoutTruEName,a4.FAccProp," +
			 		"a4.FBankAccount,a4.FstartDate,a4.FMatureDate,a4.FCuryCode,a4.Fdesc from "+pub.yssGetTableName("Tb_Para_Portfolio")+ 
			 		" m left join (select b.fportCode, e.FmanagerCode, e.FmanagerName from "+pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")+
			 		" b join (select d.* from "+pub.yssGetTableName("Tb_Para_Manager")+" d) e on b.FSubCode =e.fmanagerCode "+
			 		" where b.FRelaType = 'Manager') a1 on m.fportcode =a1.fportcode "+
			 		" left join (select b.fportCode, e.FTrusteeCode, e.FTrusteeName from "+pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")+
			 		" b join (select d.* from "+pub.yssGetTableName("Tb_Para_Trustee")+" d) e on b.FSubCode = e.FTrusteeCode where b.FRelaType = 'Trustee' "+
			 		" and b.FRelaGrade = 'secondary') a3 on m.fportcode =a3.fportcode"+
			 		" left join (select case FCuryCode when 'CNY' THEN '12' else '11' end as FAccProp,FBankAccount, FstartDate,FMatureDate,"+ //modified by yeshenghong story3751
			 		" FCuryCode,FportCode,FDesc from "+pub.yssGetTableName("Tb_Para_CashAccount")+" m where FportCode ="+dbl.sqlString(this.portCode)+
			 		" and FAccSort = 1 and FcheckState =1 and FBankCode in (select c1.ftrusteecode from "+pub.yssGetTableName("Tb_Para_Trustee")+
			 		" c1 where c1.FTrusteeCode in (select b.fsubcode from "+pub.yssGetTableName("Tb_Para_Portfolio")+" a left join (select * from "
			 		+pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")+ " where Frelatype = 'Trustee' and fcheckstate = 1) b on a.fportcode = "+
			 		" b.fportcode where a.fportCode = "+dbl.sqlString(this.portCode)+" ) and c1.fcheckstate = 1)) a4 on m.fportcode = a4.fportcode"+
			 		"  where m.fportCode = "+dbl.sqlString(this.portCode);
		     rs = dbl.openResultSet(strSql);
		     while(rs.next()){
		    	 buf.append(rs.getString("FQDOrgCode")).append("\t");
		    	 buf.append(rs.getString("FQDOrgName")).append("\t");
		    	 buf.append(rs.getString("FQDouttruCode")).append("\t");
		    	 buf.append(rs.getString("FQDouttruName")).append("\t");
		    	 buf.append(rs.getString("FQDouttruEName")).append("\t");
		    	 buf.append(rs.getString("FproductName")).append("\t");
		    	 buf.append(rs.getString("FaccProp")).append("\t");
		    	 buf.append(("".equals(rs.getString("FBankAccount"))||null==rs.getString("FBankAccount") ? " ":rs.getString("FBankAccount"))).append("\t");
		    	 buf.append(rs.getString("FcuryCode")).append("\t");
		    	 buf.append(" ").append("\t");	//20130305 added by liubo.Story #3517.与模板中的“币种名称”的隐藏列对应
		    	 opendate = rs.getDate("FStartDate").toString();
		    	 maturedate = rs.getDate("FMatureDate").toString();
		    	 opendate = opendate.substring(0,4)+opendate.substring(5, 7)+opendate.substring(8);
		    	 maturedate = maturedate.substring(0,4)+maturedate.substring(5, 7)+maturedate.substring(8);
		    	 buf.append(("".equals(opendate)||null==opendate ? " " :opendate)).append("\t");
		    	 buf.append(("".equals(maturedate)||null==maturedate ? " " :maturedate)).append("\t");
		    	 buf.append(" ").append("\r\n");
		     }
		     return buf.toString();
		}catch(Exception e){
			throw new YssException("生成QDII账户信息出错！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}
	
	//add by zhaoxianlin 20121218 STORY #3381 外管局报表——QDII境外证券投资信息
	private String getSecInvestWGJRep() throws YssException
	{
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		StringBuffer buff = new StringBuffer();    	
		ResultSet rs = null;
		String sSetCode = "";
		
		ArrayList arrWhereSql = new ArrayList();	//存储科目性质和科目类别的SQL查询语句
		ArrayList arrProjName = new ArrayList();	//存储插入外管局月报数据表中的生成项目名
		ArrayList arrProJType = new ArrayList();	//存储科目分类，包括Assets（资产）、TotalAssets（资产合计）、Debts（负债）、TotalDebts（负债合计）、NetAssets（净资产）
		
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
		try
		{
			conn.setAutoCommit(false);
			bTrans = true;
			
			//根据前台传入的科目代码，获取估值表中的套账
			//===========================
			strSql = " select FSetCode from LSetList where FYear = to_char(" + dbl.sqlDate(this.startDate) + ",'yyyy') " + 
					 " and FSetID in (select FAssetCode from " + pub.yssGetTableName("tb_para_portfolio") + 
					 " where FPortCode = " + dbl.sqlString(this.portCode) + ")";
			rs = dbl.queryByPreparedStatement(strSql);
			
			if (rs.next())
			{
				sSetCode = rs.getString("FSetCode");
			}
			//============end===============
			
			//对报表要求进行生成的21个项目进行分类
			//根据arrProJType存储的值，Assets（资产）、Debts（负债）两类的成本和市值分别进行存储
			//TotalAssets（资产合计）、TotalDebts（负债合计）两类的成本和市值，分别由资产、负债两类的成本与市值累加而来
			//NetAssets（净资产）有资产合计-负债合计而来
			//=======================================		
			
			//20130327 deleted by liubo.Bug #7392
			//统计这个类的金额的意图在于剔除在于剔除银行存款中的科目性质like '存款投资_定期存款%'部分的金额。这个剔除过程在BUG 7076的部分中已经做到，这个类的统计已经没有实际作用
			//而且这个类被设置为了资产类，那么在统计资产合计时，这个类的金额将会被统计进去。导致科目性质like '存款投资_定期存款%'部分的金额被重复统计
			//因此删除这个类的统计逻辑
			//======================================
//			arrProjName.add("存款投资_定期存款");
//			arrWhereSql.add("FAcctAttr like '存款投资_定期存款%'  and FAcctClass = '资产类' ");
//			arrProJType.add("Assets");
			//=================end=====================
			
			arrProjName.add("基金投资_开发式_货币");
			arrWhereSql.add("FAcctAttr like '基金投资_开发式_货币%'  and FAcctClass = '资产类' ");
			arrProJType.add("Assets");
			
			//20130206 modified by liubo.Bug #7076
			//“银行存款”部分，需要扣除科目性质like '存款投资_定期存款%'的部分
			//===================================
			arrProjName.add("银行存款");
//			arrWhereSql.add("(FAcctAttr like '银行存款%' or FAcctAttr like '存款投资%') and FAcctClass = '资产类' ");
			
			//20130527 modified  by liubo.Story #3982
			//"银行存款"项目需要加入103102外汇远期存出保证金的市值数据
			//******************************
		    arrWhereSql.add("(FAcctAttr like '银行存款%' or FAcctAttr like '存款投资%' or FAcctCode like '103102%') " + 
		    		" and FAcctAttr not like '存款投资_定期存款%' and FAcctClass = '资产类' ");
			//*************end*****************
			arrProJType.add("Assets");
			//=================end==================

			arrProjName.add("货币市场工具");
			arrWhereSql.add("(FAcctAttr like '买入返售证券%' or FAcctAttr like '基金投资_开发式_货币%' or FAcctAttr like '存款投资_定期存款%') and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("债券投资");
			arrWhereSql.add("FAcctAttr like '债券投资%' and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("公司股票");
			arrWhereSql.add("substr(facctattr,0,4) in ('股票投资','存托凭证') and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("基金");
			arrWhereSql.add("FAcctAttr like '基金投资%' and FAcctClass = '资产类' ");
			arrProJType.add("Assets");
            
			//modify by fangjiang BUG 7268 2013.03.07
			
			arrProjName.add("衍生产品");
			//20130527 modified  by liubo.Story #3982
			//“衍生产品”项目需要加入310101外汇远期的市值数据
			//======================================
			//--- edit by songjie 2013.07.05 BUG 8551 QDV4建行2013年07月03日04_B start---//
			//修改sql查询逻辑，在 or 逻辑外添加括号，以防查询到其他月份的数据
			arrWhereSql.add("((substr(facctattr,0,4) in ('远期投资','权证投资') or facctattr like '其他衍生工具%') " + 
							" or (FAcctCode like '310101%'))");
			//--- edit by songjie 2013.07.05 BUG 8551 QDV4建行2013年07月03日04_B end---//
			//=====================end=================
			arrProJType.add("Assets");

			arrProjName.add("其他投资");
			arrWhereSql.add("((facctattr like '债券投资_资产证券%') or (facctattr like '房地产信托凭证%') or (facctattr like '信托产品投资%')) and FAcctClass = '资产类' ");
			arrProJType.add("Assets");
			
			arrProjName.add("投资市值合计");
			arrWhereSql.add("");
			arrProJType.add("TotalInvestMV");
			
			arrProjName.add("预付投资款");
			arrWhereSql.add(" 1 = 2 ");
			arrProJType.add("Assets");

			arrProjName.add("应收投资款");
			arrWhereSql.add("FAcctAttr like '证券清算款%' and FCost > 0 and FMarketValue > 0");
			arrProJType.add("Assets");

			arrProjName.add("应收股利");
			arrWhereSql.add("FAcctAttr like '应收股利%' and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("应收利息");
			//20130527 modified  by liubo.Story #3982
			//"应收利息"项目应加入120403远期保证金应收利息的市值数据
			//======================================
			arrWhereSql.add("(FAcctAttr like '应收利息%' or FAcctCode like '120403%') and FAcctClass = '资产类' ");
			//================end======================
			arrProJType.add("Assets");

			arrProjName.add("其他应收款");
			arrWhereSql.add("(FAcctAttr like '其他应收款%' or FAcctAttr like '应收申购款%' or FAcctAttr like '待摊费用%') and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("资产合计");
			arrWhereSql.add("");
			arrProJType.add("TotalAssets");
			
			arrProjName.add("应付投资款");
			arrWhereSql.add("FAcctAttr like '证券清算款%' and FCost < 0 and FMarketValue < 0");
			arrProJType.add("Debts");

			arrProjName.add("应付托管费");
			arrWhereSql.add("FAcctAttr like '应付托管费%' and FAcctClass = '负债类'");
			arrProJType.add("Debts");
			
			arrProjName.add("应付管理费");
			arrWhereSql.add("FAcctAttr like '应付管理人报酬_管理费%' and FAcctClass = '负债类'");
			arrProJType.add("Debts");

			arrProjName.add("应付佣金");
			arrWhereSql.add("FAcctAttr like '应付佣金%' and FAcctClass = '负债类'");
			arrProJType.add("Debts");

			arrProjName.add("应交税金");
			arrWhereSql.add("FAcctAttr like '应交税金%' and FAcctClass = '负债类'");
			arrProJType.add("Debts");
			
			//modify by fangjiang bug 7268 2013.03.07
			arrProjName.add("其他应付款");
			arrWhereSql.add("(FAcctAttr like '其他应付款%' or FAcctAttr like '预提费用%' or FAcctAttr like '应付赎回款%' or FAcctAttr like '应付赎回费%' or FAcctAttr like '应付利润%' or FAcctAttr like '应付交易费用%') and FAcctClass = '负债类'");
			arrProJType.add("Debts");
			
			arrProjName.add("负债合计");
			arrWhereSql.add("");
			arrProJType.add("TotalDebts");
			
			arrProjName.add("净资产");
			arrWhereSql.add("");
			arrProJType.add("NetAssets");
			
			arrProjName.add("所托管人民币资金存款余额");
			//20130527 modified  by liubo.Story #3982
			//"所托管人民币资金存款余额"项目应加入103102外汇远期存出保证金和120403远期保证金应收利息的市值数据
			//*********************************
			arrWhereSql.add("(FAcctAttr like '银行存款%' or FAcctCode like '120403%' or FAcctCode like '103102%') " +
					" and FAcctClass = '资产类' and FCurCode ='CNY' ");
			//***************end******************
			arrProJType.add("Assets");//

			//==================end=====================
			
			strSql = "delete from " + pub.yssGetTableName("tb_rep_WGJRep") 
					 + " where FRepCode = 'REP_HG_WGJ_JWZQTZ' and FPortCode = " + dbl.sqlString(this.portCode) + " and FStartDate = " + dbl.sqlDate(this.startDate);
			dbl.executeSql(strSql);
			
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
			buff = getPubData(buff);
			
			//调用secInvestWGJReportOper方法，往外管局月报数据表中插入数据，并将报表数据拼接，返回给前台
			//====================================
			for (int i = 0; i < arrProjName.size(); i++)
			{
                if(arrProjName.get(i).toString().equals("存款投资_定期存款")){
					secInvestWGJReportOper(arrWhereSql.get(i).toString(),arrProjName.get(i).toString(),String.valueOf(i+1),sSetCode,arrProJType.get(i).toString());
				}
				else if(arrProjName.get(i).toString().equals("基金投资_开发式_货币")){
					secInvestWGJReportOper(arrWhereSql.get(i).toString(),arrProjName.get(i).toString(),String.valueOf(i+1),sSetCode,arrProJType.get(i).toString());
				}else{
					buff.append(secInvestWGJReportOper(arrWhereSql.get(i).toString(),arrProjName.get(i).toString(),String.valueOf(i+1),sSetCode,arrProJType.get(i).toString()));
				}
			}
			//=================end===================
			buff.append(" ").append("\r\n");
//			conn.commit();
//            bTrans = false;
//            conn.setAutoCommit(true);
		}
		catch(Exception ye)
		{
			throw new YssException("外管局报表——QDII境外证券投资信息出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
		return buff.toString();
	}
    /**
     * add by zhaoxianlin 20121218 STORY #3381 获取报表公共部分信息
     * @param buff
     * @return
     * @throws YssException
     */
	public StringBuffer getPubData(StringBuffer buff) throws YssException{
		ResultSet rs = null;
		StringBuffer  buf = new StringBuffer();
		try{
			buf = buf.append("select m.fportName as productName,")
				   .append(" a1.Fmanagercode,")
				   .append(" a1.FmanagerName,")
				   .append(dbl.sqlDate(this.startDate)).append(" as FDate,")
				   .append(" 'USD' as FcuryCode from ")
				   .append( pub.yssGetTableName("Tb_Para_Portfolio")).append(" m left join (select b.fportCode,e.Fmanagercode,e.fmanagername from ")
				   .append( pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				   .append(" b join (select d.* from ").append(pub.yssGetTableName("Tb_Para_Manager"))
				   .append(" d) e on b.FSubCode =  e.fmanagerCode ")
				   .append(" where b.fportCode = ").append(dbl.sqlString(portCode))    
				   .append(" and b.FRelaType = 'Manager') a1 on m.fportcode = a1.fportcode")
				   .append(" where m.fportCode =").append(dbl.sqlString(portCode));
			
		     rs = dbl.openResultSet(buf.toString());
		     if(rs.next()){
		    	 orgCode  = rs.getString("Fmanagercode");
		    	 orgName  = rs.getString("FmanagerName");
		    	 productName  = rs.getString("productName");
		    	 curyCode  = rs.getString("FcuryCode");
		    	 buff.append(orgCode).append("\t")
		    	     .append(orgName).append("\t")
		    	     .append(productName).append("\t")
		    	     .append(YssFun.formatDate(rs.getDate("FDate"),"yyyy年MM月")).append("\t")
		    	     .append(curyCode).append("\t")
		    	     .append(" ").append("\t");		//20130304 added by liubo.Story #3517.与模板中的“币种名称”的隐藏列对应
		     }
		     return buff;
		}catch(Exception e){
			throw new YssException("拼接境外投资信息公共信息出错！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}
    private double dOriginalTotal = 0;
    /**
     * add by huangqirong 2012-10-11 story #3120
     * 查询科目对应的余额再根据外管局汇率转换
     * sql
     * field1 要取的单个字段
     * field2 币种字段
     * */
    private double getTotalBlance(String sql ,java.util.Date date , String field1, String field2 , String portCode ) throws YssException{
    	dOriginalTotal = 0;
    	double total = 0 ;
    	double rate = 1 ;
    	double curyMoney = 0 ;
        ResultSet rs = null; 
        BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
        try {           
            rs = dbl.openResultSet(sql);
			while (rs.next()) {
				rate = operDeal.getCuryRate(date, this.rateSource, this.rateSource, 
					/**Start 20130624 modified by liubo.Story #4000.需求北京-(农业银行)QDIIV4(高)20130527001
					 * 当传入的币种参数为null时，表示直接取本位币*/
						(field2 == null ? this.sPortCuryCode : rs.getString(field2)),
						this.portCode,  "Base"); //外管局汇率
				
				curyMoney = rs.getDouble(field1) ;	
				
				if (field2 == null)
				{
					curyMoney = YssD.mul(curyMoney, rate) ;
				}
				/**End 20130624 modified by liubo.Story #4000.需求北京-(农业银行)QDIIV4(高)20130527001*/
				else if(!"USD".equalsIgnoreCase(rs.getString(field2)))
				{
					curyMoney = YssD.mul(curyMoney, rate) ;
				}
				total = YssD.add(total, curyMoney);			
				dOriginalTotal = YssD.add(dOriginalTotal, rs.getDouble(field1));		
			}
        } catch (Exception e) {
            throw new YssException("获取余额表数据出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    	return total ;
    }
    
    /*
     * 购汇	拿境内人民币去买境内外币    月初数
     * */
    private String getForeignRateData1 (String date1 , String date2 ,String account1,String portcode , String sqlType
    		, String sBaseSrcRate , String sPortSrcRate ) throws YssException{
    	String sql = "";
    	
    	BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);

//		double baseCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "base");
//		double portCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "port");

//		double baseUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "base");
//		double portUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "port");
		
		double baseCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, sBaseSrcRate , sPortSrcRate , "base");
		    	
    	if(sqlType.equalsIgnoreCase("0")){
    		sql = " select FBCuryCode, FBMoney from ( " +
    				"select FBCuryCode , (sum(FBMoney) * " + baseCny + " ) as FBMoney from " + 
    					pub.yssGetTableName("Tb_Data_RateTrade") + " where FCheckState = 1 " +
    				" and FPortCode= " + dbl.sqlString(portcode) +
    				" and FBPortCode = " + dbl.sqlString(portcode) +
    				" and (FSettleDate < to_date(" + dbl.sqlString(date1) + ",'yyyy-MM-dd') " +
    				" or FBSettleDate < to_date(" + dbl.sqlString(date1) + ",'yyyy-MM-dd') ) " +
    				" and FSCuryCode in ('CNY') " +	//境内人民币
    				" and FBCuryCode <>'CNY' " +	//境内外币
    				" and FBCashAccCode in (" +operSql.sqlCodes(account1)+ ")" +
    				" group by FBCuryCode )" ;
    	} else if(sqlType.equalsIgnoreCase("1")){
    		sql=" select FBCuryCode , FBMoney from ( " +
    				" select FBCuryCode ,(sum(FBMoney) * "+ baseCny +" ) as FBMoney from " + 
    				pub.yssGetTableName("Tb_Data_RateTrade") + " where FCheckState = 1 "+
    			" and FPortCode = " + dbl.sqlString(portcode) +
    			" and FBPortCode = " + dbl.sqlString(portcode) +
    			" and (FSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')"+
    			" or FBSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')) "+
    			" and " +
    			" (FSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd') " +
    			" or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd')) "+
    			" and FSCuryCode in ('CNY') " +	//境内人民币
    			" and FBCuryCode <> 'CNY' " +	//境内外币
    			" and FBCashAccCode in (" +operSql.sqlCodes(account1)+ ")" +
    			" group by FBCuryCode )" ;
    	} else if(sqlType.equalsIgnoreCase("2")){
    		sql = " select FBCuryCode , FBMoney from ( " +
    				" select FBCuryCode ,(sum(FBMoney) * "+ baseCny + " ) as FBMoney from " + 
    				pub.yssGetTableName("Tb_Data_RateTrade") + " where FCheckState = 1 " +
				" and FPortCode=" + dbl.sqlString(portcode) + 
				" and FBPortCode = " + dbl.sqlString(portcode) +
				" and (FSettleDate <= to_date(" + dbl.sqlString(date2) + ",'yyyy-MM-dd') " +
				" or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ",'yyyy-MM-dd') ) " +
				" and FSCuryCode in ('CNY') " +		//境内人民币
				" and FBCuryCode <>'CNY' " +		//境内外币
				" and FBCashAccCode in (" +operSql.sqlCodes(account1)+ ")" +
				" group by FBCuryCode )" ;
    	}
    	return sql;
    }
        
    /*
     * 境外证券投资外汇账户划入	境外外币账户划入到境内外币账户
     * */
    private String getForeignRateData2 (String date1 , String date2 , String acount1,String acount2 ,String portcode , String sqlType) throws YssException{
    	String sql ="";
    	
//    	BaseOperDeal operDeal = new BaseOperDeal();
//		operDeal.setYssPub(pub);

//		double baseCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "base");
//		double portCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "port");
//		
//		double baseUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "base");
//		double portUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "port");
		
    	if(sqlType.equalsIgnoreCase("0")){
	    	sql= " select FBCuryCode,FBMoney from ( " +
	    			"select FBCuryCode , sum(FBMoney) as FBMoney from " + 
	    			pub.yssGetTableName("Tb_Data_RateTrade") + 
	    		" where FCheckState = 1 " +
	    		" and FPortCode = " + dbl.sqlString(portcode) + 
	    		" and FBPortCode = " + dbl.sqlString(portcode) + 
	    		" and (FSettleDate < to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd') "+
	    		" or FBSettleDate < to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')) " +  
	    		" and FBCashAccCode in ("+ operSql.sqlCodes(acount1)+") and FBCuryCode <> 'CNY' " + //境内外币
	    		" and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where " +
				" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) " +
				" and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where " +
				" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) " +
	    		" group by FBCuryCode )";
    	} else if(sqlType.equalsIgnoreCase("1")){
    		sql= " select FBCuryCode, FBMoney from ( " +
    				"select FBCuryCode ,sum(FBMoney) as FBMoney from " + 
    				pub.yssGetTableName("Tb_Data_RateTrade") + 
	    		" where FCheckState = 1 " +
	    		" and FPortCode = " + dbl.sqlString(portcode) + 
	    		" and FBPortCode = " + dbl.sqlString(portcode) + 
	    		" and (FSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd') "+
	    		" or FBSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')) " +
	    		" and (FSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd') "+
	    		" or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd')) " +  
	    		" and FBCashAccCode in ("+ operSql.sqlCodes(acount1)+") and FBCuryCode <> 'CNY' " + //境内外币
	    		" and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where " +
				" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) " +
				" and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where " +
				" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) " +
	    		" group by FBCuryCode )";
    		
    	} else  if(sqlType.equalsIgnoreCase("2")){
    		sql= " select FBCuryCode, FBMoney from ( " +
    				"select FBCuryCode , sum(FBMoney) as FBMoney from " + 
    				pub.yssGetTableName("Tb_Data_RateTrade") + 
    		" where FCheckState = 1 " +
    		" and FPortCode = " + dbl.sqlString(portcode) + 
    		" and FBPortCode = " + dbl.sqlString(portcode) + 
    		" and (FSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd') "+
    		" or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd')) " +  
    		" and FBCashAccCode in ("+ operSql.sqlCodes(acount1)+") and FBCuryCode <> 'CNY' " + //境内外币
    		" and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where " +
			" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) " +
			" and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where " +
			" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) " +
    		" group by FBCuryCode )";
    	}
    	return sql;
    }
    
    
    /*
     * 境外结算账户划入	从境外结算账户划入境内账户
     * */
    private String getForeignRateData3 (String date1 ,String date2 , String acount1 ,String acount3, String portcode , String sqlType) throws YssException{
    	String sql ="" ;
    	
//    	BaseOperDeal operDeal = new BaseOperDeal();
//		operDeal.setYssPub(pub);

//		double baseCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "base");
//		double portCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "port");
//		
//		double baseUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "base");
//		double portUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "port");
		
    	if(sqlType.equalsIgnoreCase("0")){
    		sql= " select FSCuryCode ,FSMoney from ( " +
    				" select FSCuryCode ,sum(FSMoney) as FSMoney from "+ 
    				pub.yssGetTableName("Tb_Data_RateTrade") +
				 	" where FCheckState = 1 " +
				    " and FPortCode = " + dbl.sqlString(portcode) + 
				    " and FBPortCode = " + dbl.sqlString(portcode) + 
				    " and (FSettleDate < to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd') " +
				    "  or FBSettleDate < to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')) " +
				    " and FSCashAccCode in ("+ operSql.sqlCodes(acount1)+") " +	//境内账户  //modifymodify by huangqirong 2012-10-08 story #3120
				    " group by FSCuryCode )" ;
    	
    	} else if(sqlType.equalsIgnoreCase("1")){
    		sql= " select FSCuryCode , FSMoney from ( " +
    				"select FSCuryCode , sum(FSMoney) as FSMoney from "+ 
    					pub.yssGetTableName("Tb_Data_RateTrade") +
				 	" where FCheckState = 1 " +
				    " and FPortCode = " + dbl.sqlString(portcode) + 
				    " and FBPortCode = " + dbl.sqlString(portcode) + 
				    " and (FSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd') " +
				    "  or FBSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')) " +
				    " and (FSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd') " +
				    "  or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd')) " +		    
				    " and FSCashAccCode in ("+ operSql.sqlCodes(acount1)+") " +	//境内账户
				    " group by FSCuryCode )" ;
    		
    	} else if(sqlType.equalsIgnoreCase("2")){
    		sql= " select FSCuryCode , FSMoney from ( " +
    				" select FSCuryCode ,sum(FSMoney) as FSMoney from "+ 
    				pub.yssGetTableName("Tb_Data_RateTrade") +
				 	" where FCheckState = 1 " +
				    " and FPortCode = " + dbl.sqlString(portcode) + 
				    " and FBPortCode = " + dbl.sqlString(portcode) + 
				    " and (FSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd') " +  //modify by huangqirong 2012-10-08 story #3120
				    "  or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd')) " +
				    " and FSCashAccCode in ("+ operSql.sqlCodes(acount1)+") " + //境内账户
				    " group by FSCuryCode )" ;
    	}
    	return sql;
    }
    
    
    /*
     * 结汇	境内外币划到境内人民币
     * */
    private String getForeignRateData4 (String date1 , String date2, String account1 ,String portcode , String sqlType
    		, String sBaseSrcRate , String sPortSrcRate) throws YssException{
    	String sql = "";
    	BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);

//		double baseCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "base");
//		double portCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "port");
//		
//		double baseUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "base");
//		double portUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "port");
		
		double baseCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, sBaseSrcRate , sPortSrcRate , "base");		
    	
    	if(sqlType.equalsIgnoreCase("0")){    		 
    		sql = "select FSCuryCode , FSMoney  from ( " +
    				" select FSCuryCode , (sum(FSMoney) * " + baseCny + ") as FSMoney from " + 
    					pub.yssGetTableName("Tb_Data_RateTrade") + " where FCheckState = 1 " +
    				" and FPortCode= " + dbl.sqlString(portcode) +
    				" and FBPortCode = " + dbl.sqlString(portcode) +
    				" and (FSettleDate < to_date(" + dbl.sqlString(date1) + ",'yyyy-MM-dd') " +
    				" or FBSettleDate < to_date(" + dbl.sqlString(date1) + ",'yyyy-MM-dd') ) " +
    				" and FBCuryCode in ('CNY') " +	//境内人民币
    				" and FSCuryCode <>'CNY' " +	//境内外币
    				" and FSCashAccCode in (" +operSql.sqlCodes(account1)+ ")" +
    				" group by FSCuryCode )" ;    		
    	} else if(sqlType.equalsIgnoreCase("1")){
    		sql="select FSCuryCode, FSMoney from ( " +
    				"select FSCuryCode , (sum(FSMoney) * " + baseCny + ") as FSMoney from " + 
    				pub.yssGetTableName("Tb_Data_RateTrade") + " where FCheckState = 1 "+
    			" and FPortCode = " + dbl.sqlString(portcode) +
    			" and FBPortCode = " + dbl.sqlString(portcode) +
    			" and (FSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')"+
    			" or FBSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')) "+
    			" and " +
    			" (FSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd') " +
    			" or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd')) "+
    			" and FBCuryCode in ('CNY') " +	//境内人民币
    			" and FSCuryCode <> 'CNY' " +	//境内外币
				" and FSCashAccCode in (" +operSql.sqlCodes(account1)+ ")" +
    			" group by FSCuryCode )" ;    		
    	} else if(sqlType.equalsIgnoreCase("2")){
    		sql = "select FSCuryCode,FSMoney from ( " +
    				"select FSCuryCode , (sum(FSMoney) * " + baseCny + ") as FSMoney from " + 
    				pub.yssGetTableName("Tb_Data_RateTrade") + " where FCheckState = 1 " +
				" and FPortCode=" + dbl.sqlString(portcode) + 
				" and FBPortCode = " + dbl.sqlString(portcode) +
				" and (FSettleDate <= to_date(" + dbl.sqlString(date2) + ",'yyyy-MM-dd') " +
				" or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ",'yyyy-MM-dd') ) " +
				" and FBCuryCode in ('CNY') " +		//境内人民币
				" and FSCuryCode <>'CNY' " +		//境内外币
				" and FSCashAccCode in (" +operSql.sqlCodes(account1)+ ")" +
				" group by FSCuryCode )" ;    		
    	}
    	return sql;
    }
    
    
    /*
     * 划往境外证券投资外汇账户	由建行境内除人民币账户划往境外账户
     * */
    private String getForeignRateData5 (String date1 , String date2 , String acount1,String acount2 ,String portcode , String sqlType) throws YssException{
    	
    	String sql = "";
    	
//    	BaseOperDeal operDeal = new BaseOperDeal();
//		operDeal.setYssPub(pub);

//		double baseCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "base");
//		double portCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "port");
//		
//		double baseUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "base");
//		double portUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "port");
		
    	if(sqlType.equalsIgnoreCase("0")){
	    	sql= "select FSCuryCode ,FSMoney from ( " +
	    			" select FSCuryCode , sum(FSMoney) as FSMoney from " + 
	    			pub.yssGetTableName("Tb_Data_RateTrade") + 
	    		" where FCheckState = 1 " +
	    		" and FPortCode = " + dbl.sqlString(portcode) + 
	    		" and FBPortCode = " + dbl.sqlString(portcode) + 
	    		" and (FSettleDate < to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd') "+
	    		" or FBSettleDate < to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')) " +  
	    		" and FSCashAccCode in ("+ operSql.sqlCodes(acount1)+") and FBCuryCode <> 'CNY' "+ //境外外币
				" and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  " +
				" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) " +
				" and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  " +
				" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) " +
	    		" group by FSCuryCode )";
    	} else if(sqlType.equalsIgnoreCase("1")){
    		sql= "select FSCuryCode, FSMoney from ( " +
    				" select FSCuryCode , sum(FSMoney) as FSMoney from " + 
    				pub.yssGetTableName("Tb_Data_RateTrade") + 
	    		" where FCheckState = 1 " +
	    		" and FPortCode = " + dbl.sqlString(portcode) + 
	    		" and FBPortCode = " + dbl.sqlString(portcode) + 
	    		" and (FSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd') "+
	    		" or FBSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')) " + 
	    		" and (FSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd') "+
	    		" or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd')) " +  
	    		" and FSCashAccCode in ("+ operSql.sqlCodes(acount1)+") and FBCuryCode <> 'CNY' "+ //境外外币
				" and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  " +
				" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) " +
				" and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  " +
				" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) " +
	    		" group by FSCuryCode)";
    	} else  if(sqlType.equalsIgnoreCase("2")){
    		sql= "select FSCuryCode ,FSMoney from ( " +
    				"select FSCuryCode , sum(FSMoney) as FSMoney from " + 
    				pub.yssGetTableName("Tb_Data_RateTrade") + 
    		" where FCheckState = 1 " +
    		" and FPortCode = " + dbl.sqlString(portcode) + 
    		" and FBPortCode = " + dbl.sqlString(portcode) + 
    		" and (FSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd') "+
    		" or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd')) " +  
    		" and FSCashAccCode in ("+ operSql.sqlCodes(acount1)+") and FBCuryCode <> 'CNY' "+ //境外外币
			" and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  " +
			" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) " +
			" and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  " +
			" FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) " +
    		" group by FSCuryCode )";
    	}
    	return sql;
    }
    
    
    /*
     * 划往境外结算账户	从境内账户划入境外结算账户
     * */
    private String getForeignRateData6 (String date1 ,String date2 , String acount1,String acount3 ,String portcode ,String sqlType) throws YssException{
    	String sql ="" ;
    	
//    	BaseOperDeal operDeal = new BaseOperDeal();
//		operDeal.setYssPub(pub);

		//double baseCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "base");
		//double portCny = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "CNY", portcode, "port");
		
		//double baseUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "base");
		//double portUSD = operDeal.getCuryRate(YssFun.parseDate(this.endDate), "USD", portcode, "port");
    	
    	if(sqlType.equalsIgnoreCase("0")){
    		sql= "select FBCuryCode, FBMoney from ( " +
    				" select FBCuryCode ,sum(FBMoney) as FBMoney from "+ pub.yssGetTableName("Tb_Data_RateTrade") +
				 	" where FCheckState = 1 " +
				    " and FPortCode = " + dbl.sqlString(portcode) + 
				    " and FBPortCode = " + dbl.sqlString(portcode) + 
				    " and (FSettleDate < to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd') " +
				    "  or FBSettleDate < to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')) " +
				    " and FSCashAccCode in ("+ operSql.sqlCodes(acount1)+")" +   //境内
				    " group by FBCuryCode )" ;
    	
    	} else if(sqlType.equalsIgnoreCase("1")){
    		sql= "select FBCuryCode,FBMoney from ( " +
    				" select FBCuryCode , sum(FBMoney) as FBMoney from "+ 
    				pub.yssGetTableName("Tb_Data_RateTrade") +
				 	" where FCheckState = 1 " +
				    " and FPortCode = " + dbl.sqlString(portcode) + 
				    " and FBPortCode = " + dbl.sqlString(portcode) + 
				    " and (FSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd') " +
				    "  or FBSettleDate >= to_date(" + dbl.sqlString(date1) + ", 'yyyy-MM-dd')) " +
				    " and (FSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd') " +
				    "  or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd')) " +		
				    " and FSCashAccCode in ("+ operSql.sqlCodes(acount1)+")" +  //境内
				    " group by FBCuryCode )" ;
    		
    	} else if(sqlType.equalsIgnoreCase("2")){
    		sql= "select FBCuryCode, FBMoney from ( " +
    				"select FBCuryCode , sum(FBMoney) as FBMoney from "+ 
    				pub.yssGetTableName("Tb_Data_RateTrade") +
				 	" where FCheckState = 1 " +
				    " and FPortCode = " + dbl.sqlString(portcode) + 
				    " and FBPortCode = " + dbl.sqlString(portcode) + 
				    " and (FSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd') " +
				    "  or FBSettleDate <= to_date(" + dbl.sqlString(date2) + ", 'yyyy-MM-dd')) " +
				    " and FSCashAccCode in ("+ operSql.sqlCodes(acount1)+")" +  //境内
				    " group by FBCuryCode )" ;    		
    	}    	
    	return sql;
    }
    
    private String getForeignRateData7(String sCashAccount,String sCuryCode)
    {
    	String strSql = " select Round(Nvl(sum(FMoney),0),2) as FMoney, '" + sCuryCode + "' as FCuryCode " +
    					" from " + pub.yssGetTableName("tb_cash_subtransfer") + " where fnum in " +
				        " (select fnum from " + pub.yssGetTableName("tb_cash_transfer") + " where FSubTSFTypeCode = '03OPE'  " +
				        " and FTransferDate between " + dbl.sqlDate(this.startDate) + 
				        " and " + dbl.sqlDate(this.endDate) + ") and FCashAccCode in ("+ operSql.sqlCodes(sCashAccount)+")";
    	
    	
    	return strSql;
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
				result = rs.getString(field) == null ? "0" :rs.getString(field);
			}
        } catch (Exception e) {
            throw new YssException("获取期初或期末数据出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    	return result;
    }  
    
    /*
     * 查询数据
     * 
     * */
    private String getMulStringData(String sql , String field ) throws YssException{    	
        ResultSet rs = null; 
        String result = "";
        try {           
            rs = dbl.openResultSet(sql);
			while (rs.next()) {
				result += rs.getString(field)+",";
			}
			//if(result.length() > 0)
			//	result = result.substring(0 , result.length() -1);
        } catch (Exception e) {
            throw new YssException("获取期初或期末数据出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    	return result;
    } 
    
    /*
     * 设置组合群 组合 资产代码 等
     * */
    private String getGroupPortAssetSetCodes(String groupCode,String portCode) throws YssException{
        String strSql = "";
        ResultSet rs = null;
        String assetCode="";
        String setId ="";
        try {
            	String groupcode= groupCode;
            	String portcode= portCode;
            	
            	strSql = " select * from Tb_" + groupcode + "_Para_Portfolio where FCheckState = 1 and FEnabled =1 and FPORTCODE = "+ dbl.sqlString(portcode) ;
            	rs = dbl.openResultSet(strSql);
            	if (rs.next()) {
            		assetCode = rs.getString("FASSETCODE");
            		setId = this.getSetCode(assetCode);
            	}
        } catch (Exception e) {
            throw new YssException("获取资产代码出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return setId;
    }
    
    /*
     * 
     * 获取套帐代码
     * */
    private String getSetCode(String assetCode) throws YssException{    	
    	 String strSql = "";
         ResultSet rs = null;
         String setId="";
         
         try {
            
             strSql = "select * from LSetList where FsetId="+dbl.sqlString(assetCode)+" order by Fyear desc";
             rs = dbl.openResultSet(strSql);
             if(rs.next()) {
            	 setId = YssFun.formatNumber(rs.getInt("FSETCODE") ,"000");
             }             
         } catch (Exception e) {
             throw new YssException("获取套帐代码出错： \n" + e.getMessage());
         } finally {
             dbl.closeResultSetFinal(rs);
         }    	
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
            hmCellStyle = getCellStyles("REP_HG_WGJ_TZYB1");
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("REP_HG_WGJ_TZYB1") +
                " and FCheckState = 1 order by FOrderIndex";
            rs = dbl.openResultSet(strSql);
            for (int i = 0; i < sArry.length; i++) {
                sKey = "REP_HG_WGJ_TZYB1" + "\tDSF\t-1\t" + i;
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
            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取建行外管局月报表格式出错：\n"+e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	
	private String getDepositData(String dStartDate ,String dEndDate , String strCashAcc ,String sPortCode ,String sqlType)
	{
		String strSql = "";
		
		if (sqlType.equals("0"))
		{
			strSql = "";
		}
		
		return strSql;
	}
	
	//20130204 added by liubo.Story #3517
	//外管局月报——QDII境内外币托管账户情况表，通过此方法取某个账户某个项目的累计值。即取这个月往前数一个月的某个项目的累计值
	//modify by fangjiang BUG 7268 2013.03.07
	private Double getAccumulatedValue(String sBankCode,String sProductName,String sCuryCode,String sOriginalFieldName,String sFieldName) throws YssException
	{
		String strSql = "";
		
		//20130311 added by liubo.Bug #7287
		//客户要求历年累计的折美元数，不要使用历年累计的原币金额来乘当月的汇率，而是直接取历年累计的折美元数
		//==========================================
		ResultSet rs = null;
		double dReturn = 0;
		
		dOriginalTotal = 0;
		
		try
		{
			//20130325 modified by liubo.Bug #7367
			//目前境内外托管账户情况表的报告期可能会有yyyy年MM月、yyyyMM两种格式，需要都考虑到，避免出现娶不到数的情况
			//====================================
			strSql = " select Nvl(Sum(" + sOriginalFieldName + "),0) as " + sOriginalFieldName + ", " +
					 " Nvl(Sum(" + sFieldName + "),0) as " + sFieldName + ",FCuryCode " +
					 " from " + pub.yssGetTableName("TB_REP_WGJREP2") + 
					 " where FACCCODE = " + dbl.sqlString(sBankCode) + " and FPRODUCTNAME = " + dbl.sqlString(sProductName)+
					 " and FCuryCode = " + dbl.sqlString(sCuryCode) + 
					 " and FREPORTDATE in ('" + YssFun.formatDate(YssFun.addMonth(YssFun.toDate(this.endDate), -1),"yyyy年MM月") + "','" + YssFun.formatDate(YssFun.addMonth(YssFun.toDate(this.endDate), -1),"yyyyMM") + "')" +
					 " group by FCuryCode";
			//================end====================
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				dReturn = rs.getDouble(sFieldName);
				dOriginalTotal = rs.getDouble(sOriginalFieldName);
			}
		}
		catch(Exception ye)
		{
			throw new YssException("获取《境内外币托管账户情况表》累计值出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		//====================end======================
		
		return dReturn;
	}
	
	//20130204 added by liubo.Story #3517
	//外管局月报——QDII境内外币托管账户情况表，通过此方法取财务系统余额表的数据
	//sConditions为取数逻辑，FCuryCode参数为币种，sAccessMode为取哪个字段
	private String getFinaSysStatement(String sConditions,String FCuryCode,String sAccessMode) throws YssException
	{
		String strSql = "";
		String sTablePrefix = "";
		
		try
		{
			sTablePrefix = "A" + YssFun.formatDate(this.endDate,"yyyy") + 
							this.getGroupPortAssetSetCodes(pub.getAssetGroupCode(), this.portCode);
			
			strSql = " Select sum(a." + sAccessMode + ") as Cnt,a.FCurCode " +
					 " from " + sTablePrefix + "lbalance a " +
					 " left join " + sTablePrefix + "laccount b on a.Facctcode = b.facctcode " +
					 " where " + sConditions +
					 (this.sRepCode.equals("RepManagerMonth") ? " " : " and a.fisdetail = 1") +
					 (FCuryCode == null ? " " : " and a.FCurCode = " + dbl.sqlString(FCuryCode)) +
					 " and a.FMonth = " + YssFun.formatDate(this.endDate,"MM") + 
					 " group by a.FCurCode";
		}
		catch(Exception ye)
		{
			throw new YssException("获取财务系统【科目余额表】出错：" + ye.getMessage());
		}
		
		
		return strSql;
	}
	
	//20121110 added by liubo.Story #3144
	//将3144需求实现之前，已经存在的QDII境外投资月报表（一）的查询逻辑全部搬到这个类中
	//modify by fangjiang bug 7268 2013.03.07
	private String getThe1stWGJReportOper() throws YssException
	{
		StringBuffer result=new StringBuffer();
    	
    	String nowDate = (new SimpleDateFormat("yyyy-MM-dd")).format(new java.util.Date());
    	String [] ndate = nowDate.split("-");
    	String [] sdate =this.startDate.split("-");
    	String [] edate = this.endDate.split("-");	    	    	
    	
    	String emaxDay = YssFun.formatDate(this.getStringData("select last_day(to_date("+dbl.sqlString(this.endDate)+",'yyyy-MM-dd')) as FlastDay from dual","FlastDay"),"yyyy-MM-dd");
    	
    	if(Integer.parseInt(edate[0]) > Integer.parseInt(ndate[0]))
    		throw new YssException("请设置完整的会计期间！");
    	
    	if( Integer.parseInt(edate[0]) >= Integer.parseInt(ndate[0]) && Integer.parseInt(edate[1]) > Integer.parseInt(ndate[1]))
    		throw new YssException("请设置完整的会计期间！");
    	
    	if( Integer.parseInt(edate[0]) >= Integer.parseInt(ndate[0]) && 
    			Integer.parseInt(edate[1]) >= Integer.parseInt(ndate[1]) && 
    			Integer.parseInt(edate[2]) > Integer.parseInt(ndate[2]))
    			throw new YssException("查询日期超出当前会计期间！");
    	
    	
    	if( !sdate[0].equalsIgnoreCase(edate[0]))
    		throw new YssException("报表查询不支持跨年操作！");	    	
    	
    	if(Integer.parseInt(sdate[0]) >= Integer.parseInt(edate[0]) && Integer.parseInt(sdate[1]) > Integer.parseInt(edate[1]))
    		throw new YssException("请设置完整的会计期间！"); 
    	
    	if(Integer.parseInt(sdate[1]) != Integer.parseInt(edate[1]))
    		throw new YssException("请设置完整的会计期间！"); 
    	
    	if(!this.startDate.endsWith("-01"))
    		throw new YssException("请设置完整的会计期间！");
    	
    	if(!this.endDate.endsWith(emaxDay.split("-")[2]))
    		throw new YssException("请设置完整的会计期间！");
    	
    	//20121219 modified by liubo.Story #3380
    	//根据“QDII境内外币托管账户情况表”的要求，修改原外管局报表（一）的代码逻辑
    	//==========================================
    	String tempTotal = "";	
    	String strSql = "";
    	ResultSet rs = null;
    	ResultSet rsAcc = null;
    	StringBuffer buff = new StringBuffer();
        boolean bTrans  = false ;
        Connection conn = dbl.loadConnection();
		try 
		{
			if (this.inAcctCodes == null)
			{
				return "";
			}

	        conn.setAutoCommit(false);
	        bTrans = true;
	         
			
			//取出所有境内外币账户，即账户分类为境内账户，货币代码非人民币的账户
			//然后以账户为单位逐个生成数据
			strSql = "select * from " + pub.yssGetTableName("tb_para_cashaccount") +
					 " where FPortCode = " + dbl.sqlString(this.portCode) + 
					 " and FACCSORT = '1' and FCheckState = 1 and facctype = '01'" ;
			//and FCuryCode <> 'CNY'  条件去除 modified by zhaoxianlin 20130122 STORY #3479 QDII境内外币托管账户情况表显示人民币账户信息
			
			//20130220 added by liubo.Story #3517
			//sCurShowing值为0，表示取所有币种的账户，为1表示取人民币账户，为2表示取外币账户
			if (this.sCurShowing.equals("0"))
			{
				
			}
			else if (this.sCurShowing.equals("1"))
			{
				strSql = strSql + " and FCuryCode = 'CNY'";
			}
			else if (this.sCurShowing.equals("2"))
			{
				strSql = strSql + " and FCuryCode <> 'CNY'";
			}
			
			rsAcc = dbl.queryByPreparedStatement(strSql);
			
			while(rsAcc.next())
			{
				
				if (rsAcc.getString("FBankAcCount") == null || rsAcc.getString("FBankAcCount").trim().equals(""))
				{
					continue;
				}
				
				buff = new StringBuffer();
				buff.append("insert into  " + pub.yssGetTableName("TB_REP_WGJREP2") + "  values(");
				
				strSql = "select e.FManagerCode,e.FManagerName,a.fportname,Nvl(c.fbankaccount,' ') as fbankaccount,c.fcurycode " +
						 " from " + pub.yssGetTableName("tb_para_portfolio") + " a " +
						 " left join (select * from tb_sys_assetgroup where FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ") b on 1 = 1 " +
						 " left join (select * from " + pub.yssGetTableName("tb_para_cashaccount") + " where FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + ") c on 1 = 1 " +
						 " left join (select m.fportName as productName, " +
						 " a1.FManagerCode as FManagerCode, " +
						 " a1.FManagerName as FManagerName " +
						 " from " + pub.yssGetTableName("Tb_Para_Portfolio") + " m " +
						 " left join (select b.fportCode, e.FManagerCode, e.FManagerName " +
						 " from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " b " +
						 " join (select d.*, forgcode, FAffCorpName " +
						 " from " + pub.yssGetTableName("Tb_Para_Manager") + " d " +
						 " join (select faffcorpcode, forgcode, FAffCorpName " +
                         " from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                         " where Fcheckstate = 1) c on c.faffcorpcode =d.FManagerCode) e on b.FSubCode = e.fmanagerCode " +
                         " where b.fportCode = " + dbl.sqlString(this.portCode) + " and b.FRelaType = 'Manager') a1 on m.fportcode = a1.fportcode " +
                         " where m.fportCode = " + dbl.sqlString(this.portCode) + ") e on 1 = 1 " +
						 " where a.Fportcode = " + dbl.sqlString(this.portCode); 
				
				rs = dbl.queryByPreparedStatement(strSql);
				
				if(rs.next())
				{
					result.append(rs.getString("FManagerCode")).append("\t");	//管理人代码
					result.append(rs.getString("FManagerName")).append("\t");	//管理人名称
					result.append(rs.getString("FPortName")).append("\t");		//产品名称
					result.append(rs.getString("FBankAcCount")).append("\t");	//银行账号
					result.append(YssFun.formatDate(this.endDate,"yyyy年MM月")).append("\t");	//报告期
					result.append(rs.getString("FCuryCode")).append("\t");		//币种
					result.append(" ").append("\t");		//币种名称
				}
				
				String sBankCode = rs.getString("FBankAcCount");
				String sProductName = rs.getString("FPortName");

				strSql = "Delete from " + pub.yssGetTableName("TB_REP_WGJREP2") + " where FPRODUCTNAME = " + dbl.sqlString(rs.getString("FPortName")) +
						 " and FACCCODE = " + dbl.sqlString(rs.getString("FBankAcCount")) + " and FREPORTDATE = " + dbl.sqlString(YssFun.formatDate(this.endDate,"yyyy年MM月"));
				dbl.executeSql(strSql);				

				buff.append(dbl.sqlString(rs.getString("FManagerCode"))).append(",");
				buff.append(dbl.sqlString(rs.getString("FManagerName"))).append(",");
				buff.append(dbl.sqlString(rs.getString("FPortName"))).append(",");
				buff.append(dbl.sqlString(rs.getString("FBankAcCount"))).append(",");
				buff.append(dbl.sqlString(YssFun.formatDate(this.endDate,"yyyy年MM月"))).append(",");
				buff.append(dbl.sqlString(rs.getString("FCuryCode"))).append(",");
				
				dbl.closeResultSetFinal(rs);
				
				this.dAccrual = 0;					//本月收入\支出折发生额折美元
				this.dOriginalAccrual = 0;			//本月收入\支出折发生额原币金额
				this.dLastBalances = 0;				//历年收入\支出折发生额折美元
				this.dOriginalLastBalances = 0;		//历年收入\支出折发生额原币金额
				
	//			/*银行存款*/
				
				//20130206 modified by liubo.Bug #7077
				//客户要求对托管行境内人民币账户银行存款实行汇总显示。
				//境内人民币账户可能分为存款账户，保证金账户，和备付金账户，而后两种账户除了有银行存款业务和利息收入以外，一般不会有其他业务
				//而托管行境内外币账户，一个币种一般只会有一个，可以直接进行汇总。
				//所有在银行存款这里直接针对现金库存中某个币种的所有境内账户进行汇总
				//====================================
				//modify by fangjiang 2013.03.08 story 3710
				strSql = "select sum(FAccBalance) as FAccBalance,FCuryCode from " + pub.yssGetTableName("tb_stock_cash") +
						 " where FCheckState = 1 " +
						 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + "" +
						 //" and FCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") +
						 //" where FAccSort = '1' and FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portCode) + " and FCuryCode = " + dbl.sqlString(rsAcc.getString("FCuryCode")) + ")" +
						 " and FStorageDate = " + dbl.sqlDate(this.endDate) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FCuryCode = " + dbl.sqlString(rsAcc.getString("FCuryCode")) +
						 " group by FCuryCode ";
				double dDeposit = this.getTotalBlance(strSql,YssFun.parseDate(this.endDate),"FAccBalance","FCuryCode",this.portCode);
				//=================end===================
				
				result.append(YssFun.formatNumber(this.dOriginalTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(dDeposit,"#,##0.00")).append("\t");
				
				buff.append(YssFun.formatNumber(this.dOriginalTotal,"0.00")).append(",").append(YssFun.formatNumber(dDeposit,"0.00")).append(",");
				
				String tempRw = "";
				double firstCount = 0;
				double currentCount = 0;
				double lastCount = 0;
				
				double dTempTotal = 0;
								
				
				/*购汇*/
				String sql = "";
				
				sql =  this.getForeignRateData1(this.startDate , this.endDate, dbl.sqlString(rsAcc.getString("FCashAccCode")), this.portCode , "1",
						this.rateSource, this.rateSource);
							
				currentCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"FBMoney","FBCuryCode",this.portCode);

				dTempTotal = this.dOriginalTotal;
							
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");
				
				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
	
				dAccrual += currentCount;
				dOriginalAccrual += dOriginalTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
				
//				sql =  this.getForeignRateData1(this.startDate , this.endDate, dbl.sqlString(rsAcc.getString("FCashAccCode")), this.portCode , "2",
//						this.rateSource, this.rateSource);
				
				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMGH_RMB","FSUMGH_USD");
								
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);
				
				lastCount += currentCount;
				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
							
				/*境外证券投资外汇账户划入*/
				
				/**
				 * 境外证券投资外汇账户划入
				 * 境外结算账户划入
				 * 这俩个合为一项
				 * */
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
				
				
				/*境外结算账户划入*/
				
	
				sql = this.getForeignRateData2(this.startDate ,  this.endDate, dbl.sqlString(rsAcc.getString("FCashAccCode")), this.outAcctCodes, this.portCode, "1");
				currentCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate) , "FBMoney", "FBCuryCode", this.portCode );
				dTempTotal = this.dOriginalTotal;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
	
//				sql = this.getForeignRateData2(this.startDate ,  this.endDate, dbl.sqlString(rsAcc.getString("FCashAccCode")), this.outAcctCodes,this.portCode, "2");
//				lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate) , "FBMoney", "FBCuryCode", this.portCode );
//				dTempTotal = this.dOriginalTotal;

				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMIJWTGZH_RMB","FSUMIJWTGZH_USD");
												
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);
				
				lastCount += currentCount;
				dTempTotal += this.dOriginalTotal ;
								
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
				
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
				
				/*申购款汇入   默认为零 */
				
				strSql = //--- delete by songjie 2013.09.04 BUG 9362 QDV4建行2013年09月03日01_B start---// 
					     //"select Nvl(sum(a.Fsellmoney),0) + Nvl(sum(b.FSellMoney),0) as Cnt," + dbl.sqlString(rsAcc.getString("FCuryCode")) + " as FCuryCode " +
				         //--- delete by songjie 2013.09.04 BUG 9362 QDV4建行2013年09月03日01_B end---// 
					     //--- add by songjie 2013.09.04 BUG 9362 QDV4建行2013年09月03日01_B start---// 
					     "select Nvl(sum(a.Fsellmoney),0) as Cnt," + dbl.sqlString(rsAcc.getString("FCuryCode")) + " as FCuryCode " +
					     //--- add by songjie 2013.09.04 BUG 9362 QDV4建行2013年09月03日01_B end---// 
				         " from (select sum(Fsellmoney) as Fsellmoney from " + pub.yssGetTableName("tb_ta_trade") + 
						 " where FCheckState = 1 " +
						 " and fselltype = '01' " +
						 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) +
						 " and FPortCode = " + dbl.sqlString(this.portCode) +
						 " and FSettleDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) + ") a ";
				         //--- delete by songjie 2013.09.04 BUG 9362 QDV4建行2013年09月03日01_B start---// 
//						 " left join (select sum(Fsellmoney) as Fsellmoney from " + pub.yssGetTableName("tb_ta_trade") + " where " +
//						 " FCheckState = 1 and fselltype = '00' and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and FSettleDate between " + dbl.sqlDate(this.startDate) + " and " +
//						 dbl.sqlDate(this.endDate) + " and FPortCode = " + dbl.sqlString(this.portCode) + ") b on 1 = 1 "
						 //--- delete by songjie 2013.09.04 BUG 9362 QDV4建行2013年09月03日01_B end---// 
				
				currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCuryCode", this.portCode);
				
				dTempTotal = this.dOriginalTotal;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
				
//				strSql = "select * from " + pub.yssGetTableName("tb_ta_trade") + " a " +
//				 " where FCheckState = 1 and a.fselltype = '01'  " +
//				 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and FPortCode = " + dbl.sqlString(this.portCode) +
//				 " and FConfimDate <= " + dbl.sqlDate(this.endDate);
//	
//				lastCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FSellMoney", "FCuryCode", this.portCode);
				
				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMSGHR_RMB","FSUMSGHR_USD");
												
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);
				
				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
				
				
				/* 利息收入 */
				
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
				
				//20130220 modified by liubo.Story #3517
				//当生成模式为农行模式，当月应收利息取财务系统应收利息科目贷方金额
				if (YssCons.YSS_WGJRep_BuldingMode.equalsIgnoreCase("JH"))
				{
					//20130313 modified by liubo.Story #3717
					//客户要求利息收入部分，只取银行存款利息的部分，即在估值系统中，06DE的现金应收应付+02DE的资金调拨-02DE的现金应收应付
					//即人民币账户使用原外币账户取利息收入的逻辑
					//++++++++++++++++++++++++++++++++++++++++++
					
//					if (rsAcc.getString("FCuryCode").equalsIgnoreCase("CNY"))
//					{
//						strSql = getFinaSysStatement("a.FAcctCode like '6011%' and b.Facctname not like '%债%' ",rsAcc.getString("FCuryCode"),"Fdebit");
//						
//						currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
//						
//						dTempTotal = this.dOriginalTotal;
//					}
//					else
//					{
						//strSql 更改  add by  zhaoxianlin 20130122 BUG 6931 QDII境内外币托管账户情况表中本月利息收入取数不正确---start--//
						strSql = "select Round(Nvl((Fmoney+Fmoney2-Fmoney3),0),2) as Fmoney,fcurycode from (select Nvl(sum(a.Fmoney),0) as Fmoney, " +
								"Nvl(sum(b.fmoney),0) as Fmoney2,Nvl(sum(c.fmoney),0) as Fmoney3 from " +
								//20130129 modified by liubo.Bug #7015
								//修改06DE的取数方法，改为直接取汇总值。避免出现 b表和c表的数据随着a表记录的条数而重复的情况
								//=====================================
		//						" from "+pub.yssGetTableName("tb_data_cashpayrec")+
								" (select sum(FMoney) as FMoney from " + pub.yssGetTableName("tb_data_cashpayrec") +
								" where FCheckState = 1 and FSubTSFTypeCode = '06DE' and FTransDate between " + dbl.sqlDate(this.startDate) + " and " +
				                dbl.sqlDate(this.endDate) + " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and FPortCode = " + dbl.sqlString(this.portCode) + ") a " +
								" left join (select a.*, b.fmoney from "+pub.yssGetTableName("Tb_Cash_Transfer")+" a join (select * from "+
								pub.yssGetTableName("Tb_Cash_SubTransfer")+" where Fcheckstate = 1 and FCashAccCode = "+dbl.sqlString(rsAcc.getString("FCashAccCode"))+
								" ) b on a.fnum = b.fnum where a.FTsfTypeCode = '02' and a.FSubTsfTypeCode = '02DE' and a.fcheckstate = 1 and FTransferDate between "+
							    dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +" ) b on 1 = 1 left join (select Fmoney,FTransDate from "+
							    pub.yssGetTableName("tb_data_cashpayrec")+" where FCheckState = 1 and FSubTSFTypeCode = '02DE' and FTransDate between "+
							    dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +" and FCashAccCode = "+dbl.sqlString(rsAcc.getString("FCashAccCode"))+
							    " and FPortCode = "+dbl.sqlString(this.portCode)+" )c on 1=1) x "+
								//==============end=======================
							    " left join (select FcuryCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+" where FcheckState =1 and FcashACCcode = "+
							    dbl.sqlString(rsAcc.getString("FCashAccCode"))+" ) y on 1=1 ";
						//strSql 更改  add by  zhaoxianlin 20130122 BUG 6931 QDII境内外币托管账户情况表中本月利息收入取数不正确---end--//
						
						currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
						dTempTotal = this.dOriginalTotal;
//					}

					//++++++++++++++++++++end++++++++++++++++++++++

				}
				else
				{
					strSql = getFinaSysStatement("a.FAcctCode like '1204%' ",rsAcc.getString("FCuryCode"),"Fcredit");
					
					currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
					
					dTempTotal = this.dOriginalTotal;
				}
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
	
//				strSql = "select * from " + pub.yssGetTableName("tb_data_cashpayrec") +
//					 	 " where FCheckState = 1 and FSubTSFTypeCode = '06DE' " +
//					 	 " and FTransDate <= " + dbl.sqlDate(this.endDate) +
//					 	 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and FPortCode = " + dbl.sqlString(this.portCode);
				//strSql 更改  add by  zhaoxianlin 20130122 BUG 6931 QDII境内外币托管账户情况表中本月利息收入取数不正确---start--//
//				strSql = "select Round(Nvl((Fmoney+Fmoney2-Fmoney3),0),1) as Fmoney,fcurycode from (select Nvl(sum(a.Fmoney),0) as Fmoney, " +
//						"Nvl(sum(b.fmoney),0) as Fmoney2,Nvl(sum(c.fmoney),0) as Fmoney3 from "+pub.yssGetTableName("tb_data_cashpayrec")+
//						" a left join (select a.*, b.fmoney from "+pub.yssGetTableName("Tb_Cash_Transfer")+" a join (select * from "+
//						pub.yssGetTableName("Tb_Cash_SubTransfer")+" where Fcheckstate = 1 and FCashAccCode = "+dbl.sqlString(rsAcc.getString("FCashAccCode"))+
//						" ) b on a.fnum = b.fnum where a.FTsfTypeCode = '02' and a.FSubTsfTypeCode = '02DE' and a.fcheckstate = 1 and FTransferDate <= "+
//					    dbl.sqlDate(this.endDate) +" ) b on 1 = 1 left join (select Fmoney,FTransDate from "+
//					    pub.yssGetTableName("tb_data_cashpayrec")+" where FCheckState = 1 and FSubTSFTypeCode = '02DE' and FTransDate <= "+
//					    dbl.sqlDate(this.endDate) +" and FCashAccCode = "+dbl.sqlString(rsAcc.getString("FCashAccCode"))+
//					    " and FPortCode = "+dbl.sqlString(this.portCode)+" )c on 1=1 where a.FCheckState = 1 and a.FSubTSFTypeCode = '06DE' "+
//					    " and a.FTransDate <= "+ dbl.sqlDate(this.endDate) +" and a.FCashAccCode = "+
//					    dbl.sqlString(rsAcc.getString("FCashAccCode"))+" and a.FPortCode = "+dbl.sqlString(this.portCode)+" ) x "+
//					    " left join (select FcuryCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+" where FcheckState =1 and FcashACCcode = "+
//					    dbl.sqlString(rsAcc.getString("FCashAccCode"))+" ) y on 1=1 ";
				//strSql 更改  add by  zhaoxianlin 20130122 BUG 6931 QDII境内外币托管账户情况表中本月利息收入取数不正确---end--//
//				lastCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				
//				dTempTotal = this.dOriginalTotal;

				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMINTEREST_RMB","FSUMINTEREST_USD");
												
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);
				
				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
	
				/* 其他收入 */
				
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
				
//				strSql = "select * from " + pub.yssGetTableName("tb_data_cashpayrec") +
//					 	 " where FCheckState = 1 and FSubTSFTypeCode = '06OT' " +
//					 	 " and FTransDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +
//					 	 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and FPortCode = " + dbl.sqlString(this.portCode);
//				
//				currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				
//				dTempTotal = this.dOriginalTotal;
				

				if (YssCons.YSS_WGJRep_BuldingMode.equalsIgnoreCase("JH"))
				{
					//modify by fangjiang STORY #3703 2013.03.07
					/*if (rsAcc.getString("FCuryCode").equalsIgnoreCase("CNY"))
					{
						strSql = getFinaSysStatement("a.FAcctCode like '6302%' ",rsAcc.getString("FCuryCode"),"Fdebit");
						
						currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
						
						dTempTotal = this.dOriginalTotal;
					}
					else
					{
						dTempTotal = 0;
						currentCount = 0;
					}*/
					
					//20130326 modified by liubo.Bug #7387
					//======================================
//					strSql = getFinaSysStatement("a.FAcctCode like '6302%' ",rsAcc.getString("FCuryCode"),"Fdebit");
					strSql = getFinaSysStatement("(a.FAcctCode like '630201%' or a.FAcctCode like '630205%') ",rsAcc.getString("FCuryCode"),"Fdebit");
					//===================end===================
					
					currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
					
					dTempTotal = this.dOriginalTotal;
				}
				else
				{
					dTempTotal = 0;
					currentCount = 0;
					
				}
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
	
//				strSql = "select * from " + pub.yssGetTableName("tb_data_cashpayrec") +
//					 	 " where FCheckState = 1 and FSubTSFTypeCode = '06OT' " +
//					 	 " and FTransDate <= " + dbl.sqlDate(this.endDate) +
//					 	 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and FPortCode = " + dbl.sqlString(this.portCode);
//				
//				lastCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
				
				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMIOTHER_RMB","FSUMIOTHER_USD");
				
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);
				
				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);

				//本月收入合计
				result.append(YssFun.formatNumber(dOriginalAccrual,"#,##0.00")).append("\t").append(YssFun.formatNumber(dAccrual,"#,##0.00")).append("\t");
				//历年累计收入合计
				result.append(YssFun.formatNumber(dOriginalLastBalances,"#,##0.00")).append("\t").append(YssFun.formatNumber(dLastBalances,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dOriginalAccrual,"0.00")).append(",").append(YssFun.formatNumber(dAccrual,"0.00")).append(",");

				buff.append(YssFun.formatNumber(dOriginalLastBalances,"0.00")).append(",").append(YssFun.formatNumber(dLastBalances,"0.00")).append(",");
				
				
				this.dAccrual = 0; 
				this.dOriginalAccrual = 0;
				this.dLastBalances = 0;
				this.dOriginalLastBalances = 0;
	
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
				
				/*结汇*/
				
				sql =  this.getForeignRateData4(this.startDate , this.endDate, dbl.sqlString(rsAcc.getString("FCashAccCode")), this.portCode , "1",
						this.rateSource, this.rateSource);
				currentCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"FSMoney","FSCuryCode",this.portCode);
				dTempTotal = this.dOriginalTotal;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
				
//				sql =  this.getForeignRateData4(this.startDate , this.endDate, dbl.sqlString(rsAcc.getString("FCashAccCode")), this.portCode , "2",
//						this.rateSource, this.rateSource);
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"FSMoney","FSCuryCode",this.portCode);
//				dTempTotal = this.dOriginalTotal;
				
				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMJH_RMB","FSUMJH_USD");
				
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);
				
				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
	
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
				
				
				/*划往境外证券投资外汇账户*/
				/*划往境外结算账户*/
				
				sql = this.getForeignRateData5(this.startDate ,  this.endDate, dbl.sqlString(rsAcc.getString("FCashAccCode")), this.outAcctCodes, this.portCode, "1");
				currentCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "FSMoney", "FSCuryCode", this.portCode);
				dTempTotal = this.dOriginalTotal;
				
				//20130227 deleted by liubo.Story #3681.QDII境内外币托管账户情况表本月划往境外托管账户金额去掉OPE费用金额
				//=============================================
//				sql = getForeignRateData7(dbl.sqlString(rsAcc.getString("FCashAccCode")),rsAcc.getString("FCuryCode"));
//				
//				currentCount += this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				dTempTotal += this.dOriginalTotal;
				//====================end=========================
	
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
				
//				sql = this.getForeignRateData5(this.startDate ,  this.endDate, dbl.sqlString(rsAcc.getString("FCashAccCode")), this.outAcctCodes, this.portCode, "2");
//				lastCount = this.getTotalBlance(sql, YssFun.parseDate(this.endDate), "FSMoney", "FSCuryCode", this.portCode);
//				dTempTotal = this.dOriginalTotal;
				
				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMOJWTGZH_RMB","FSUMOJWTGZH_USD");
				
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);
				
				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
				
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
								
				/*支付赎回款金额*/
				
				strSql = "select * from " + pub.yssGetTableName("tb_ta_trade") +
						 " where FCheckState = 1 and fselltype = '02' and FPortCode = " + dbl.sqlString(this.portCode) + 
						 " and FSettleDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) + 
						 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode"));
				currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "fsettlemoney", "FCuryCode", this.portCode);
				dTempTotal = this.dOriginalTotal;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
	
//				strSql = "select * from " + pub.yssGetTableName("tb_ta_trade") +
//						 " where FCheckState = 1 and fselltype = '02' and FPortCode = " + dbl.sqlString(this.portCode) + 
//						 " and FTradeDate <= " +  dbl.sqlDate(this.endDate) + 
//						 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode"));
//				lastCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FSellMoney", "FCuryCode", this.portCode);
//				dTempTotal = this.dOriginalTotal;
				
				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMSH_RMB","FSUMSH_USD");
				
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);

				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
				
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
	
				/*分红金额*/
	
				if (YssCons.YSS_WGJRep_BuldingMode.equalsIgnoreCase("JH"))
				{
					strSql = "select * from " + pub.yssGetTableName("tb_ta_trade") +
							 " where FCheckState = 1 and fselltype = '03' and FPortCode = " + dbl.sqlString(this.portCode) + 
							 " and FSettleDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) + 
							 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode"));
					currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "fsettlemoney", "FCuryCode", this.portCode);
					dTempTotal = this.dOriginalTotal;
				}
				else
				{
					strSql = "select Nvl(sum(a.fsettlemoney),0) - Nvl(sum(b.fsettlemoney),0) as Cnt," + dbl.sqlString(rsAcc.getString("FCuryCode")) + " as FCuryCode " +
							 " from " + pub.yssGetTableName("tb_ta_trade") + " a " +
							 " left join (select * from " + pub.yssGetTableName("tb_ta_trade") + " where FSellType = '08' and FSettleDate between " + dbl.sqlDate(this.startDate) + " and " +
							 dbl.sqlDate(this.endDate) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + ") b on 1 = 1 " +
							 " where a.FCheckState = 1 " +
							 " and a.fselltype = '03' " +
							 " and a.FPortCode = " + dbl.sqlString(this.portCode) +
							 " and a.FSettleDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +
							 " and a.FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode"));

					currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCuryCode", this.portCode);
					dTempTotal = this.dOriginalTotal;
				}
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
	
//				strSql = "select * from " + pub.yssGetTableName("tb_ta_trade") +
//						 " where FCheckState = 1 and fselltype = '03' and FPortCode = " + dbl.sqlString(this.portCode) + 
//						 " and FTradeDate <= " +  dbl.sqlDate(this.endDate) + 
//						 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode"));
//				lastCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FSellMoney", "FCuryCode", this.portCode);
//				dTempTotal = this.dOriginalTotal;
				
				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMFH_RMB","FSUMFH_USD");
				
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);

				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
	
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
				
				/*托管费支出金额*/
				
				//20130129 added by liubo.Bug #7003
				//判断托管费的条件，由写死IV002的运营收支代码，变更为“运营费用品种类型”表的FfeeType字段为“tuoguan”
				//============================================
//				strSql = "select a.* from " + pub.yssGetTableName("tb_data_investpayrec") + " a " +
//						 " left join " + pub.yssGetTableName("tb_para_investpay") + " b on a.FIVPayCatCode = b.fivpaycatcode " +
//						 " left join Tb_Base_InvestPayCat c on a.fivpaycatcode = c.fivpaycatcode " +
//			 	 		 " where a.FCheckState = 1 and a.FSubTSFTypeCode = '07IV' " +
//			 	 		 " and a.FTransDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +
//			 	 		 " and b.FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and a.FPortCode = " + dbl.sqlString(this.portCode) +
//			 	 		 " and c.ffeetype = 'tuoguan'";
//				//===================end=========================
//		
//				currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				
//				dTempTotal = this.dOriginalTotal;

				if (YssCons.YSS_WGJRep_BuldingMode.equalsIgnoreCase("JH"))
				{
					if (rsAcc.getString("FCuryCode").equalsIgnoreCase("CNY"))
					{
						strSql = getFinaSysStatement("a.FAcctCode like '6404%' ",rsAcc.getString("FCuryCode"),"Fdebit");
						
						currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
						
						dTempTotal = this.dOriginalTotal;
					}
					else
					{
						dTempTotal = 0;
						currentCount = 0;
					}
				}
				else
				{
					strSql = getFinaSysStatement("a.FAcctCode like '2207%' ",rsAcc.getString("FCuryCode"),"Fdebit");
					
					currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
					
					dTempTotal = this.dOriginalTotal;
					
				}
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);

				//20130129 added by liubo.Bug #7003
				//判断托管费的条件，由写死IV002的运营收支代码，变更为“运营费用品种类型”表的FfeeType字段为“tuoguan”
				//============================================
//				strSql = "select a.* from " + pub.yssGetTableName("tb_data_investpayrec") + " a " +
//						 " left join " + pub.yssGetTableName("tb_para_investpay") + " b on a.FIVPayCatCode = b.fivpaycatcode " +
//						 " left join Tb_Base_InvestPayCat c on a.fivpaycatcode = c.fivpaycatcode " +
//			 	 		 " where a.FCheckState = 1 and a.FSubTSFTypeCode = '07IV' " +
//			 	 		 " and a.FTransDate = "  + dbl.sqlDate(this.endDate) +
//			 	 		 " and b.FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and a.FPortCode = " + dbl.sqlString(this.portCode) +
//			 	 		 " and c.ffeetype = 'tuoguan'";
//				//===================end=========================
//				lastCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				
//				dTempTotal = this.dOriginalTotal;
				
				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMTGF_RMB","FSUMTGF_USD");
				
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);

				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
				
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
				
				/*管理费支出金额*/

				//20130129 added by liubo.Bug #7003
				//判断管理费的条件，由写死IV001的运营收支代码，变更为“运营费用品种类型”表的FfeeType字段为“guanli”
				//============================================
//				strSql = "select a.* from " + pub.yssGetTableName("tb_data_investpayrec") + " a " +
//						 " left join " + pub.yssGetTableName("tb_para_investpay") + " b on a.FIVPayCatCode = b.fivpaycatcode " +
//						 " left join Tb_Base_InvestPayCat c on a.fivpaycatcode = c.fivpaycatcode " +
//			 	 		 " where a.FCheckState = 1 and a.FSubTSFTypeCode = '07IV' " +
//			 	 		 " and a.FTransDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +
//			 	 		 " and b.FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and a.FPortCode = " + dbl.sqlString(this.portCode) +
//			 	 		 " and c.ffeetype = 'guanli'";
//
//				//===================end=========================
//				currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				
//				dTempTotal = this.dOriginalTotal;
				

				if (YssCons.YSS_WGJRep_BuldingMode.equalsIgnoreCase("JH"))
				{
					if (rsAcc.getString("FCuryCode").equalsIgnoreCase("CNY"))
					{
						strSql = getFinaSysStatement("a.FAcctCode like '6403%' ",rsAcc.getString("FCuryCode"),"Fdebit");
						
						currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
						
						dTempTotal = this.dOriginalTotal;
					}
					else
					{
						dTempTotal = 0;
						currentCount = 0;
					}
				}
				else
				{
					strSql = getFinaSysStatement("a.FAcctCode like '2206%' ",rsAcc.getString("FCuryCode"),"Fdebit");
					
					currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
					
					dTempTotal = this.dOriginalTotal;
				}
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);

				//20130129 added by liubo.Bug #7003
				//判断管理费的条件，由写死IV001的运营收支代码，变更为“运营费用品种类型”表的FfeeType字段为“guanli”
				//============================================
//				strSql = "select a.* from " + pub.yssGetTableName("tb_data_investpayrec") + " a " +
//						 " left join " + pub.yssGetTableName("tb_para_investpay") + " b on a.FIVPayCatCode = b.fivpaycatcode " +
//						 " left join Tb_Base_InvestPayCat c on a.fivpaycatcode = c.fivpaycatcode " +
//			 	 		 " where a.FCheckState = 1 and a.FSubTSFTypeCode = '07IV' " +
//			 	 		 " and a.FTransDate = "  + dbl.sqlDate(this.endDate) +
//			 	 		 " and b.FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and a.FPortCode = " + dbl.sqlString(this.portCode) +
//			 	 		 " and c.ffeetype = 'guanli'";
//
//				//===================end=========================
//				lastCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				
//				dTempTotal = this.dOriginalTotal;
				
				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMGLF_RMB","FSUMGLF_USD");
				
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);

				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
	
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
				
				/*手续费支出金额*/
				//add by  zhaoxianlin BUG 6930 --start 手续费取数逻辑修改//
//				strSql = "select sum(FMoney) as Fmoney,FcuryCode  from (select a.*, b.fmoney, c.FcuryCode,c.fcashacccode from "+ pub.yssGetTableName("Tb_Cash_Transfer")
//		         +" a join (select * from "+ pub.yssGetTableName("Tb_Cash_SubTransfer") + " where fcashacccode = "+dbl.sqlString(rsAcc.getString("FCashAccCode"))+") b on a.fnum = b.fnum "
//		         +" left join (select FcuryCode,fcashacccode from " + pub.yssGetTableName("Tb_Para_CashAccount")
//		         +" where FcheckState = 1 and fcashacccode = "+dbl.sqlString(rsAcc.getString("FCashAccCode"))+") c on 1=1  where a.FTsfTypeCode = '03' and " 
//		         +" a.FSubTsfTypeCode = '0303' and a.fcheckstate =1 and FTransferDate between "+dbl.sqlDate(this.startDate)
//		         +" and "+dbl.sqlDate(this.endDate)+") group by FcuryCode";
//				//add by  zhaoxianlin BUG 6930 --end//
//				
////				strSql = "select * from " + pub.yssGetTableName("tb_data_cashpayrec") +
////					 	 " where FCheckState = 1 and FSubTSFTypeCode in ('07FE01','07FE02','07RE01','07RE02','07RE03') " +
////					 	 " and FTransDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +
////					 	 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and FPortCode = " + dbl.sqlString(this.portCode);
//	
//				currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				
//				dTempTotal = this.dOriginalTotal;
				
				double dSXF_USD = 0;
				double dSXF_Original = 0;

				strSql = getFinaSysStatement("b.FAcctAttr like '其他费用_银行费用%' ",rsAcc.getString("FCuryCode"),"Fdebit");
					
				currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
					
				dTempTotal = this.dOriginalTotal;
					
				dSXF_USD = currentCount;
				
				dSXF_Original = dTempTotal;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
				
				//add by  zhaoxianlin BUG 6930 --start 累计手续费取数逻辑修改//
//				strSql = "select sum(FMoney) as Fmoney,FcuryCode  from (select a.*, b.fmoney, c.FcuryCode,c.fcashacccode from "+ pub.yssGetTableName("Tb_Cash_Transfer")
//		          +" a join (select * from "+ pub.yssGetTableName("Tb_Cash_SubTransfer") + " where fcashacccode = "+dbl.sqlString(rsAcc.getString("FCashAccCode"))+") b on a.fnum = b.fnum "
//		         +" left join (select FcuryCode,fcashacccode from " + pub.yssGetTableName("Tb_Para_CashAccount")
//		         +" where FcheckState = 1 and fcashacccode = "+dbl.sqlString(rsAcc.getString("FCashAccCode"))+") c on 1=1  where a.FTsfTypeCode = '03' and " 
//		         +" a.FSubTsfTypeCode = '0303' and a.fcheckstate =1 and FTransferDate <= "+dbl.sqlDate(this.endDate)
//		         +") group by FcuryCode";
//				//add by  zhaoxianlin BUG 6930 --start 累计手续费取数逻辑修改//
//				
////				strSql = "select * from " + pub.yssGetTableName("tb_data_cashpayrec") +
////					 	 " where FCheckState = 1 and FSubTSFTypeCode in ('07FE01','07FE02','07RE01','07RE02','07RE03') " +
////					 	 " and FTransDate <= " + dbl.sqlDate(this.endDate) +
////					 	 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and FPortCode = " + dbl.sqlString(this.portCode);
//				lastCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				
//				dTempTotal = this.dOriginalTotal;
				
				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMSXF_RMB","FSUMSXF_USD");
				
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);

				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
				
				firstCount = 0;
				currentCount = 0;
				lastCount = 0;
				dTempTotal = 0;
				
				/*其他支出金额*/
				
//				strSql = "select * from " + pub.yssGetTableName("tb_data_cashpayrec") +
//					 	 " where FCheckState = 1 and FSubTSFTypeCode = '07OT' " +
//					 	 " and FTransDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +
//					 	 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and FPortCode = " + dbl.sqlString(this.portCode);
//		
//				currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				
//				dTempTotal = this.dOriginalTotal;
				

				if (YssCons.YSS_WGJRep_BuldingMode.equalsIgnoreCase("JH"))
				{
					//modify by fangjiang STORY #3703 2013.03.07
					strSql = getFinaSysStatement("(a.FAcctCode like '6605%' or a.FAcctCode like '6406%')",rsAcc.getString("FCuryCode"),"Fdebit");
						
					currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
						
					dTempTotal = this.dOriginalTotal;
					
					currentCount = currentCount - dSXF_USD;
					dTempTotal = dTempTotal - dSXF_Original;
				}
				else
				{
					strSql = getFinaSysStatement("a.FAcctCode like '2501%' ",rsAcc.getString("FCuryCode"),"Fdebit");
					
					currentCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "Cnt", "FCurCode", this.portCode);
					
					dTempTotal = this.dOriginalTotal;
					
				}
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(currentCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(currentCount,"0.00")).append(",");
				
				dAccrual += currentCount;
				dOriginalAccrual += dTempTotal;
				dAccrual = YssD.round(dAccrual, 2);
				dOriginalAccrual = YssD.round(dOriginalAccrual, 2);
			
//				strSql = "select * from " + pub.yssGetTableName("tb_data_cashpayrec") +
//					 	 " where FCheckState = 1 and FSubTSFTypeCode = '07OT' " +
//					 	 " and FTransDate <= " + dbl.sqlDate(this.endDate) +
//					 	 " and FCashAccCode = " + dbl.sqlString(rsAcc.getString("FCashAccCode")) + " and FPortCode = " + dbl.sqlString(this.portCode);
//				
//				lastCount = this.getTotalBlance(strSql, YssFun.parseDate(this.endDate), "FMoney", "FCuryCode", this.portCode);
//				
//				dTempTotal = this.dOriginalTotal;

				lastCount = getAccumulatedValue(sBankCode,sProductName,rsAcc.getString("FCuryCode"),"FSUMOOTHER_RMB","FSUMOOTHER_USD");
				
//				lastCount = getTotalBlance(sql,YssFun.parseDate(this.endDate),"Cnt","FCuryCode",this.portCode);

				lastCount += currentCount;

				dTempTotal += this.dOriginalTotal ;
				
				result.append(YssFun.formatNumber(dTempTotal,"#,##0.00")).append("\t").append(YssFun.formatNumber(lastCount,"#,##0.00")).append("\t");

				buff.append(YssFun.formatNumber(dTempTotal,"0.00")).append(",").append(YssFun.formatNumber(lastCount,"0.00")).append(",");
				
				dLastBalances += lastCount;
				dOriginalLastBalances += dTempTotal;
				dLastBalances = YssD.round(dLastBalances, 2);
				dOriginalLastBalances = YssD.round(dOriginalLastBalances, 2);
	
				//本月支出合计
				result.append(YssFun.formatNumber(dOriginalAccrual,"#,##0.00")).append("\t").append(YssFun.formatNumber(dAccrual,"#,##0.00")).append("\t");
				
				//历年累计支出合计
				result.append(YssFun.formatNumber(dOriginalLastBalances,"#,##0.00")).append("\t").append(YssFun.formatNumber(dLastBalances,"#,##0.00")).append("\r\n");

				buff.append(YssFun.formatNumber(dOriginalAccrual,"0.00")).append(",").append(YssFun.formatNumber(dAccrual,"0.00")).append(",");

				buff.append(YssFun.formatNumber(dOriginalLastBalances,"0.00")).append(",").append(YssFun.formatNumber(dLastBalances,"0.00")).append(",''");
				buff.append(",'1',").append(dbl.sqlString(pub.getUserCode())).append(",").append(dbl.sqlString(YssFun.formatDate(new Date())));
				buff.append(",").append(dbl.sqlString(pub.getUserCode())).append(",").append(dbl.sqlString(YssFun.formatDate(new Date()))).append(")");
				
				dbl.executeSql(buff.toString());
				
			}
			
	    	//====================end======================

			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
			
		} catch (Exception e) {
			throw new YssException("生成【QDII境内外币托管账户情况表】出错： \n" + e.getMessage());			
		} finally {
			dbl.closeResultSetFinal(rsAcc);
			dbl.endTransFinal(conn, bTrans);
		}    	
		return result.toString();
	}

	//201211109 added by liubo.Story #3144
	//QDII境外证券投资月报表（二）的报表生成逻辑
	private String getThe2ndWGJRep() throws YssException
	{
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		StringBuffer buff = new StringBuffer();    	
		ResultSet rs = null;
		String sSetCode = "";
		
		ArrayList arrWhereSql = new ArrayList();	//存储科目性质和科目类别的SQL查询语句
		ArrayList arrProjName = new ArrayList();	//存储插入外管局月报数据表中的生成项目名
		ArrayList arrProJType = new ArrayList();	//存储科目分类，包括Assets（资产）、TotalAssets（资产合计）、Debts（负债）、TotalDebts（负债合计）、NetAssets（净资产）
		
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
		try
		{
			conn.setAutoCommit(false);
			bTrans = true;
			
			//根据前台传入的科目代码，获取估值表中的套账
			//===========================
			strSql = " select FSetCode from LSetList where FYear = to_char(" + dbl.sqlDate(this.startDate) + ",'yyyy') " + 
					 " and FSetID in (select FAssetCode from " + pub.yssGetTableName("tb_para_portfolio") + 
					 " where FPortCode = " + dbl.sqlString(this.portCode) + ")";
			rs = dbl.queryByPreparedStatement(strSql);
			
			if (rs.next())
			{
				sSetCode = rs.getString("FSetCode");
			}
			//============end===============
			
			//对报表要求进行生成的21个项目进行分类
			//根据arrProJType存储的值，Assets（资产）、Debts（负债）两类的成本和市值分别进行存储
			//TotalAssets（资产合计）、TotalDebts（负债合计）两类的成本和市值，分别由资产、负债两类的成本与市值累加而来
			//NetAssets（净资产）有资产合计-负债合计而来
			//=======================================
			arrProjName.add("银行存款");
			arrWhereSql.add("FAcctAttr like '银行存款%' and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("货币市场工具");
			arrWhereSql.add("FAcctAttr like '买入返售证券%' and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("债券投资");
			arrWhereSql.add("FAcctAttr like '债券投资%' and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("公司股票");
			arrWhereSql.add("substr(facctattr,0,4) in ('股票投资','存托凭证') and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("基金");
			arrWhereSql.add("FAcctAttr like '基金投资%' and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("衍生产品");
			arrWhereSql.add("(substr(facctattr,0,4) in ('远期投资','权证投资') or facctattr like '其他衍生工具%') and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("其他投资");
			arrWhereSql.add("((facctattr like '债券投资_资产证券%') or (facctattr like '房地产信托凭证%') or (facctattr like '信托产品投资%')) and FAcctClass = '资产类' ");
			arrProJType.add("Assets");
			
			arrProjName.add("预付投资款");
			arrWhereSql.add(" 1 = 2 ");
			arrProJType.add("Assets");

			arrProjName.add("应收投资款");
			arrWhereSql.add("FAcctAttr like '证券清算款%' and FCost > 0 and FMarketValue > 0");
			arrProJType.add("Assets");

			arrProjName.add("应收股利");
			arrWhereSql.add("FAcctAttr like '应收股利%' and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("应收利息");
			arrWhereSql.add("FAcctAttr like '应收利息%' and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("其他应收款");
			arrWhereSql.add("FAcctAttr like '其他应收款%' and FAcctClass = '资产类' ");
			arrProJType.add("Assets");

			arrProjName.add("资产合计");
			arrWhereSql.add("");
			arrProJType.add("TotalAssets");
			
			arrProjName.add("应付投资款");
			arrWhereSql.add("FAcctAttr like '证券清算款%' and FCost < 0 and FMarketValue < 0");
			arrProJType.add("Debts");

			arrProjName.add("应付托管费");
			arrWhereSql.add("FAcctAttr like '应付托管费%' and FAcctClass = '负债类'");
			arrProJType.add("Debts");

			arrProjName.add("应付佣金");
			arrWhereSql.add("FAcctAttr like '应付佣金%' and FAcctClass = '负债类'");
			arrProJType.add("Debts");

			arrProjName.add("应付管理费");
			arrWhereSql.add("FAcctAttr like '应付管理人报酬_管理费%' and FAcctClass = '负债类'");
			arrProJType.add("Debts");

			arrProjName.add("应交税金");
			arrWhereSql.add("FAcctAttr like '应交税金%' and FAcctClass = '负债类'");
			arrProJType.add("Debts");

			arrProjName.add("其他应付款");
			arrWhereSql.add("FAcctAttr like '其他应付款%' and FAcctClass = '负债类'");
			arrProJType.add("Debts");
			
			arrProjName.add("负债合计");
			arrWhereSql.add("");
			arrProJType.add("TotalDebts");
			
			arrProjName.add("净资产");
			arrWhereSql.add("");
			arrProJType.add("NetAssets");

			//==================end=====================
			
			strSql = "delete from " + pub.yssGetTableName("tb_rep_WGJRep") 
					 + " where FRepCode = 'REP_HG_WGJ_TZYB2' and FPortCode = " + dbl.sqlString(this.portCode) + " and FStartDate = " + dbl.sqlDate(this.startDate);
			dbl.executeSql(strSql);
			
			//调用The2ndWGJReportOper方法，往外管局月报数据表中插入数据，并将报表数据拼接，返回给前台
			//====================================
			for (int i = 0; i < arrProjName.size(); i++)
			{
				buff.append(The2ndWGJReportOper(arrWhereSql.get(i).toString(),arrProjName.get(i).toString(),String.valueOf(i+1),sSetCode,arrProJType.get(i).toString()));
			}
			//=================end===================
			
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		}
		catch(Exception ye)
		{
			throw new YssException("处理QDII外管局月报（二）出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
		return buff.toString();
	}
	
	//20121113 added by liubo.Story #3144
	//生成QDII境外证券投资月报表（三）
	private String getThe3rdWGJRep() throws YssException
	{
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		StringBuffer buff = new StringBuffer();    	
		ResultSet rs = null;
		ResultSet rsDetail = null;
		ResultSet rsSettleDate = null;
		int iSerialNum = 1;		
		
		try
		{
			conn.setAutoCommit(false);
			bTrans = true;
			
			strSql = "delete from " + pub.yssGetTableName("tb_rep_WGJRep") 
					 + " where FRepCode = 'REP_HG_WGJ_TZYB3' and FPortCode = " + dbl.sqlString(this.portCode) 
					 + " and FStartDate = " + dbl.sqlDate(this.startDate) + " and FEndDate = " + dbl.sqlDate(this.endDate);
			dbl.executeSql(strSql);

			//首先获取某个日期、某个组合下有多少的外汇交易
			//获取完成之后，需要以外汇交易数据编号为条件进行汇总
			//==================================
			strSql = " select distinct FSettleDate from  " + pub.yssGetTableName("Tb_Data_RateTrade") + " where " +
					"FPortCode = " + dbl.sqlString(this.portCode) + " and FSettleDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate)
					 + " and FCheckState = 1 order by FSettleDate";
			rsSettleDate = dbl.queryByPreparedStatement(strSql);
			
			while(rsSettleDate.next())
			{
				strSql = " select distinct FSettleDate from  " + pub.yssGetTableName("Tb_Data_RateTrade") + " where " +
				"FPortCode = " + dbl.sqlString(this.portCode) + " and FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate"));
				
				rs = dbl.queryByPreparedStatement(strSql);
				
				while(rs.next())
				{
					strSql = " select round(Nvl(FSMoneyA,0),2) as FSMoneyA,round(Nvl(FBMoneyA,0) * Nvl(rateB.FExRate,1),2) as FBMoneyAUSD, "	//资金购汇数据人民币、等值美元
							+" Nvl(FCuryB,' ') as FCuryB,round(Nvl(FSMoneyB,0),2) as FSMoneyB,round(Nvl(FSMoneyB,0) * Nvl(rateS.FExRate,1),2) as FSMoneyBUSD, "	//资金汇出数据币种、金额、等值美元
							+" Nvl(FCuryC,' ') as FCuryC,round(Nvl(FBMoneyC,0),2) as FBMoneyC,round(Nvl(FBMoneyC,0) * Nvl(rateSC.FExRate,1),2) as FBMoneyCUSD, "	//资金汇入币种、金额、等值美元
							+" round(Nvl(FSMoneyD,0) * Nvl(rateSD.FExRate,1),2) as FSMoneyDUSD,round(Nvl(FBMoneyD,0),2) as FSMoneyD, "			//资金结汇等值美元、人民币
							+" round((Nvl(FSMoneyB,0) * Nvl(rateS.FExRate,1)) - (Nvl(FBMoneyC,0) * Nvl(rateSC.FExRate,1)),2) as jhcUSD, "		//净汇出金额
							+" round((Nvl(FBMoneyA,0) * Nvl(rateB.FExRate,1)) - (Nvl(FSMoneyD,0) * Nvl(rateSD.FExRate,1)),2) as jghUSD "			//净购汇金额
							+" from "
							+" ( "
							+"  select * from "
							+"  ( "		//获取资金购汇数据
							+"    select FSMoney as FSMoneyA,FBMoney as FBMoneyA,FBCuryCode as FCuryA from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
							+"    where FSettleDate = " + dbl.sqlDate(rs.getDate("FSettleDate")) + " and FBCuryCode <> 'CNY' and FSCuryCode = 'CNY' "
							+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
							+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
							+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
							+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
							+"  ) a "
							+"  full join  "
							+"  ( "		//资金汇出数据
							+"    select FSCuryCode FCuryB,FSMoney as FSMoneyB from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
							+"    where FSettleDate = " + dbl.sqlDate(rs.getDate("FSettleDate")) + " "
							+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
							+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) "
							+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
							+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
							+"  ) b on 1 = 1 "
							+"  full join "
							+"  ( "		//资金汇入数据
							+"    select FBCuryCode as FCuryC,FBMoney as FBMoneyC from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
							+"    where FSettleDate = " + dbl.sqlDate(rs.getDate("FSettleDate")) + " "
							+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
							+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
							+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
							+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) "
							+"  ) c on 1 = 1 "
							+"  full join "
							+"  ( "		//资金结汇数据
							+"    select FSMoney as FSMoneyD,FBMoney as FBMoneyD,FSCuryCode as FCuryD from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
							+"    where FSettleDate = " + dbl.sqlDate(rs.getDate("FSettleDate")) + " and FBCuryCode = 'CNY' and FSCuryCode <> 'CNY' "
							+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
							+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
							+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
							+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
							+"  ) d on 1 = 1 "
							+" ) trade "
							+" left join   "
							+" ( "		//买入货币的平均汇率
							+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeB from " + pub.yssGetTableName("tb_data_exchangerate") + " "
							+"  where FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FExRateDate in "
							+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where FExRateDate <= "
							+"  last_day(" + dbl.sqlDate(rs.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + ") "
							+" ) rateB on trade.FCuryA = rateB.FCuryCodeB "
							+" left join   "
							+" ( "		//卖出货币的平均汇率
							+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeS from " + pub.yssGetTableName("tb_data_exchangerate") + " "
							+"  where FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FExRateDate in "
							+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where FExRateDate <= "
							+"  last_day(" + dbl.sqlDate(rs.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + ") "
							+" ) rateS on trade.FCuryB = rateS.FCuryCodeS "
							+" left join   "
							+" ( "		//卖出货币的平均汇率
							+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeS from " + pub.yssGetTableName("tb_data_exchangerate") + " "
							+"  where FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FExRateDate in "
							+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where FExRateDate <= "
							+"  last_day(" + dbl.sqlDate(rs.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + ") "
							+" ) rateSC on trade.FCuryC = rateSC.FCuryCodeS "
							+" left join   "
							+" ( "		//卖出货币的平均汇率
							+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeS from " + pub.yssGetTableName("tb_data_exchangerate") + " "
							+"  where FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FExRateDate in "
							+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where FExRateDate <= "
							+"  last_day(" + dbl.sqlDate(rs.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + ") "
							+" ) rateSD on trade.FCuryD = rateSD.FCuryCodeS ";
	
					//===============end===================
					
					rsDetail = dbl.openResultSet(strSql);
					
					double dPurchase_RMB = 0;
					double dPurchase_USD = 0;
					double dSettlement_RMB = 0;
					double dSettlement_USD = 0;
					
					ArrayList arDetail = new ArrayList();
					Hashtable hsDxfOut = new Hashtable();
					Hashtable hsDxfIn = new Hashtable();

					Hashtable hsDxfOutCury = new Hashtable();
					Hashtable hsDxfInCury = new Hashtable();
					
					int iNum = 0;
					
					while(rsDetail.next())
					{
						
						dPurchase_RMB += rsDetail.getDouble("FSMoneyA");
						dPurchase_USD += rsDetail.getDouble("FBMoneyAUSD");
						
						arDetail = new ArrayList();
						
						if (hsDxfOut.get(rsDetail.getString("FCuryB")) == null)
						{
							arDetail.add(0,rsDetail.getDouble("FSMoneyB"));
							arDetail.add(1,rsDetail.getDouble("FSMoneyBUSD"));
							hsDxfOut.put(rsDetail.getString("FCuryB"), arDetail);
						}
						else
						{
							arDetail = (ArrayList)hsDxfOut.get(rsDetail.getString("FCuryB"));
							arDetail.set(0, Double.parseDouble(arDetail.get(0).toString()) + rsDetail.getDouble("FSMoneyB"));
							arDetail.set(1, Double.parseDouble(arDetail.get(1).toString()) + rsDetail.getDouble("FSMoneyBUSD"));
							hsDxfOut.put(rsDetail.getString("FCuryB"), arDetail);
						}
						hsDxfOutCury.put(iNum, rsDetail.getString("FCuryB"));
						
						arDetail = new ArrayList();
						
						if (hsDxfIn.get(rsDetail.getString("FCuryC")) == null)
						{
							arDetail.add(0,rsDetail.getDouble("FBMoneyC"));
							arDetail.add(1,rsDetail.getDouble("FBMoneyCUSD"));
							hsDxfIn.put(rsDetail.getString("FCuryC"), arDetail);
						}
						else
						{
							arDetail = (ArrayList)hsDxfIn.get(rsDetail.getString("FCuryC"));
							arDetail.set(0, Double.parseDouble(arDetail.get(0).toString()) + rsDetail.getDouble("FBMoneyC"));
							arDetail.set(1, Double.parseDouble(arDetail.get(1).toString()) + rsDetail.getDouble("FBMoneyCUSD"));
							hsDxfIn.put(rsDetail.getString("FCuryC"), arDetail);
						}
						
						hsDxfInCury.put(iNum, rsDetail.getString("FCuryC"));
						
						dSettlement_RMB += rsDetail.getDouble("FSMoneyD");
						dSettlement_USD += rsDetail.getDouble("FSMoneyDUSD");
						
						iNum ++;
						
					}
					
					double dDxfOut = 0;
					double dDxfOut_USD = 0;
					double dDxfIn = 0;
					double dDxfIn_USD = 0;
					double dNetExport = 0;
					double dNetPurchase = 0;
					ArrayList arCruyDetail = new ArrayList();
					
					int iRowNum = (hsDxfOutCury.size() > hsDxfInCury.size() ? hsDxfOutCury.size() : hsDxfInCury.size());
					
					for (int i = 0; i < iRowNum; i++)
					{
						if (i >= 1)
						{
							dPurchase_RMB = 0;
							dPurchase_USD = 0;
							dSettlement_RMB = 0;
							dSettlement_USD = 0;
						}
						
						arCruyDetail = new ArrayList();
						
						if (hsDxfOutCury.get(i) == null)
						{
							dDxfOut = 0;
							dDxfOut_USD = 0;
						}
						else
						{
							arCruyDetail = (ArrayList)hsDxfOut.get(hsDxfOutCury.get(i).toString());
							dDxfOut = Double.parseDouble(arCruyDetail.get(0).toString());
							dDxfOut_USD = Double.parseDouble(arCruyDetail.get(1).toString());
						}
						
						if (hsDxfInCury.get(i) == null)
						{
							dDxfIn = 0;
							dDxfIn_USD = 0;
						}
						else
						{
							arCruyDetail = (ArrayList)hsDxfIn.get(hsDxfInCury.get(i).toString());
							dDxfIn = Double.parseDouble(arCruyDetail.get(0).toString());
							dDxfIn_USD = Double.parseDouble(arCruyDetail.get(1).toString());
						}
						
						dNetExport = YssD.sub(dDxfOut_USD, dDxfIn_USD);
						dNetPurchase = YssD.sub(dPurchase_USD,dSettlement_USD);
						
						if (dPurchase_RMB == 0 && dPurchase_USD == 0 && dSettlement_RMB == 0 && dSettlement_USD == 0)
						{
							continue;
						}
						
						//将查询到的数据插入的外管局月报数据表中
						strSql = "insert into " + pub.yssGetTableName("tb_rep_WGJRep") 
						+ " Values(" + dbl.sqlString("REP_HG_WGJ_TZYB3") + ","	//报表代码
						+ dbl.sqlString(this.portCode) + ","	//组合代码
						+ dbl.sqlDate(this.startDate) + ","		//起始日期
						+ dbl.sqlDate(this.endDate) + ","		//结束日期
						+ String.valueOf(iSerialNum) + ","		//序号
						+ "' ',"								//项目编号
						+ dbl.sqlDate(rs.getDate("FSettleDate")) + ","		//汇入汇出日期
						+ dPurchase_RMB + ","	//资金购汇人民币
						+ dPurchase_USD + ","	//资金购汇等值美元
						+ dbl.sqlString(hsDxfOutCury.get(i).toString()) + ","	//资金汇出币种
						+ dDxfOut + ","	//资金汇出金额
						+ dDxfOut_USD + ","	//资金汇出等值美元
						+ dbl.sqlString(hsDxfInCury.get(i).toString()) + ","	//资金汇入币种
						+ dDxfIn + ","	//资金汇入金额
						+ dDxfIn_USD + ","	//资金汇入等值美元
						+ dSettlement_USD + ","	//资金结汇等值美元
						+ dSettlement_RMB + ","	//资金结汇人民币
						+ dNetExport + ","	//净汇出金额
						+ dNetPurchase + ","	//净购汇金额
						+ "' '" + ","	//备注
						+ dbl.sqlString(pub.getUserCode()) + ","	//创建人用户代码
						+ dbl.sqlDate(new java.util.Date()) + ")";	//创建时间
						
						dbl.executeSql(strSql);
						
						buff.append(iSerialNum).append("\t");
						buff.append(YssFun.formatDate(rs.getDate("FSettleDate"))).append("\t");
						buff.append(YssFun.formatNumber(dPurchase_RMB, "#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(dPurchase_USD, "#,##0.00")).append("\t");
						buff.append(hsDxfOutCury.get(i).toString()).append("\t");
						buff.append(YssFun.formatNumber(dDxfOut, "#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(dDxfOut_USD, "#,##0.00")).append("\t");
						buff.append(hsDxfInCury.get(i).toString()).append("\t");
						buff.append(YssFun.formatNumber(dDxfIn, "#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(dDxfIn_USD, "#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(dSettlement_USD, "#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(dSettlement_RMB, "#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(dNetExport, "#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(dNetPurchase, "#,##0.00")).append("\t");
						buff.append(" ").append("\r\n");
						
						iSerialNum ++;
					}
					
					dbl.closeResultSetFinal(rsDetail);
					
				}
				dbl.closeResultSetFinal(rs);
			}
									
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

    		return buff.toString();
		}
		catch(Exception ye)
		{
			throw new YssException("处理QDII外管局月报（三）出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs,rsDetail,rsSettleDate);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	//20121218 added by zhaoxianlin. STORY #3383
	//生成外管局报表——资金汇出入及结购汇明细信息
	private String getCashFlowWGJRep() throws YssException
	{
		String strSqlCash = "";
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		StringBuffer buff = new StringBuffer();    	
		ResultSet rs = null;
		ResultSet rsDetail = null;
		ResultSet rsSettleDate = null;
		int iSerialNum = 1;
		String tempRw = "";
	    String accCode = " ";
		try
		{
			conn.setAutoCommit(false);
			bTrans = true;
			
			strSql = "delete from " + pub.yssGetTableName("tb_rep_WGJRep") 
					  +" where FRepCode = 'REP_HG_WGJ_ZJHCRJGH' and FPortCode = " + dbl.sqlString(this.portCode)
					  +" and FInOutDate between "+ dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate);
			dbl.executeSql(strSql);
			//==================================
			strSql = " select distinct FSettleDate from  " + pub.yssGetTableName("Tb_Data_RateTrade") + " where " +
					"FPortCode = " + dbl.sqlString(this.portCode) + " and FSettleDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate)
					 + " and FCheckState = 1 order by FSettleDate";
			rsSettleDate = dbl.queryByPreparedStatement(strSql);
			
			while(rsSettleDate.next())
			{
					strSqlCash = "select round(Nvl(FSMoneyA,0),2) as FSMoneyA,round(Nvl(FBMoneyA,0) * Nvl(rateB.FExRate,1),2) as FBMoneyAUSD, "	//资金购汇数据人民币、等值美元
						+" Nvl(FCuryB,' ') as FCuryB,round(Nvl(FSMoneyB,0),2) as FSMoneyB,round(Nvl(FSMoneyB,0) * Nvl(rateS.FExRate,1),2) as FSMoneyBUSD, "	//资金汇出数据币种、金额、等值美元
						+" Nvl(FCuryC,' ') as FCuryC,round(Nvl(FBMoneyC,0),2) as FBMoneyC,round(Nvl(FBMoneyC,0) * Nvl(rateSC.FExRate,1),2) as FBMoneyCUSD, "	//资金汇入币种、金额、等值美元
						+" round(Nvl(FSMoneyD,0) * Nvl(rateSD.FExRate,1),2) as FSMoneyDUSD,round(Nvl(FBMoneyD,0),2) as FSMoneyD "			//资金结汇等值美元、人民币
						+" from "
						+" ( "
						+"  select * from "
						+"  ( "		//获取资金购汇数据
						+"    select FSMoney as FSMoneyA,FBMoney as FBMoneyA,FBCuryCode as FCuryA from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
						+"    where FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + " and FBCuryCode <> 'CNY' and FSCuryCode = 'CNY' "
						+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						+"  ) a "
						+"  full join  "
						+"  ( "		//资金汇出数据
						+"    select FSCuryCode FCuryB,FSMoney as FSMoneyB from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
						+"    where FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + " "
						+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) "
						+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						+"  ) b on 1 = 1 "
						+"  full join "
						+"  ( "		//资金汇入数据
						+"    select FBCuryCode as FCuryC,FBMoney as FBMoneyC from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
						+"    where FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + " "
						+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) "
						+"  ) c on 1 = 1 "
						+"  full join "
						+"  ( "		//资金结汇数据
						+"    select FSMoney as FSMoneyD,FBMoney as FBMoneyD,FSCuryCode as FCuryD from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
						+"    where FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + " and FBCuryCode = 'CNY' and FSCuryCode <> 'CNY' "
						+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						+"  ) d on 1 = 1 "
						+" ) trade "
						+" left join   "
						+" ( "		//买入货币的平均汇率
						+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeB from " + pub.yssGetTableName("tb_data_exchangerate") + " "
						+"  where FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FExRateDate in "
						+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where FExRateDate <= "
						+"  last_day(" + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + ") "
						+" ) rateB on trade.FCuryA = rateB.FCuryCodeB "
						+" left join   "
						+" ( "		//卖出货币的平均汇率
						+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeS from " + pub.yssGetTableName("tb_data_exchangerate") + " "
						+"  where FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FExRateDate in "
						+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where FExRateDate <= "
						+"  last_day(" + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + ") "
						+" ) rateS on trade.FCuryB = rateS.FCuryCodeS "
						+" left join   "
						+" ( "		//卖出货币的平均汇率
						+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeS from " + pub.yssGetTableName("tb_data_exchangerate") + " "
						+"  where FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FExRateDate in "
						+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where FExRateDate <= "
						+"  last_day(" + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + ") "
						+" ) rateSC on trade.FCuryC = rateSC.FCuryCodeS "
						+" left join   "
						+" ( "		//卖出货币的平均汇率
						+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeS from " + pub.yssGetTableName("tb_data_exchangerate") + " "
						+"  where FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FExRateDate in "
						+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where FExRateDate <= "
						+"  last_day(" + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FPortCode = " + dbl.sqlString(this.portCode) + ") "
						+" ) rateSD on trade.FCuryD = rateSD.FCuryCodeS ";
					
				      strSql = " select distinct  m.fsettledate,' ' as fratereason,' ' as FrateReason2,m.fportcode," 
				                +" a1.Fmanagercode,a1.FmanagerName,a2.fportname,b1.* from "
							    + pub.yssGetTableName("Tb_Data_RateTrade") + " m "
							    +" left join (select b.fportCode,e.Fmanagercode,e.fmanagername from " +pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " b "
							    +" join (select d.* from "+pub.yssGetTableName("Tb_Para_Manager") + " d "+" ) e on b.FSubCode = e.fmanagerCode"	    
							    +"  where b.FRelaType = 'Manager') a1 on m.fportcode = a1.fportcode left join (select * from "+pub.yssGetTableName("Tb_Para_Portfolio")
							    +" where Fcheckstate = 1) a2 on a2.fportcode = m.fportcode  left join ("+strSqlCash+ ") b1 on 1 = 1  " 
							    +" where m.FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) 
							    + " and m.fportcode = " + dbl.sqlString(this.portCode);
							    
				  	          rsDetail = dbl.openResultSet(strSql);
				  	          while(rsDetail.next()){
				  	        	 
				  	        	if (rsDetail.getDouble("FSMoneyA") == 0 && rsDetail.getDouble("FBMoneyAUSD") == 0 && rsDetail.getDouble("FSMoneyD") == 0 && rsDetail.getDouble("FSMoneyDUSD") == 0)
								{//当日无购汇
									continue;
								}
                                if(accCode.equals(" ")){  //账户为空时不显示
				  	        		continue;
				  	        	}
									//将查询到的数据插入的外管局月报数据表中
									strSql = "insert into " + pub.yssGetTableName("tb_rep_WGJRep") 
									+ " Values(" + dbl.sqlString("REP_HG_WGJ_ZJHCRJGH") + ","	//报表代码
									+ dbl.sqlString(this.portCode) + ","	//组合代码
									+ dbl.sqlDate(this.startDate) + ","		//起始日期
									+ dbl.sqlDate(this.endDate) + ","		//结束日期
									+ String.valueOf(iSerialNum) + ","		//序号
									+ "' ',"								//项目编号
									+ dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + ","		//汇入汇出日期
									+ rsDetail.getDouble("FSMoneyA") + ","	//资金购汇人民币
									+ rsDetail.getDouble("FBMoneyAUSD") + ","	//资金购汇等值美元
									+ dbl.sqlString(rsDetail.getString("FCuryB")) + ","	//资金汇出币种
									+ rsDetail.getDouble("FSMoneyB") + ","	//资金汇出金额
									+ rsDetail.getDouble("FSMoneyBUSD") + ","	//资金汇出等值美元
									+ dbl.sqlString(rsDetail.getString("FCuryC")) + ","	//资金汇入币种
									+ rsDetail.getDouble("FBMoneyC") + ","	//资金汇入金额
									+ rsDetail.getDouble("FBMoneyCUSD") + ","	//资金汇入等值美元
									+ rsDetail.getDouble("FSMoneyDUSD") + ","	//资金结汇等值美元
									+ rsDetail.getDouble("FSMoneyD") + ","	//资金结汇人民币
									+ 0 + ","	//净汇出金额
									+ 0 + ","	//净购汇金额
									+ "' '" + ","	//备注
									+ dbl.sqlString(pub.getUserCode()) + ","	//创建人用户代码
									+ dbl.sqlDate(new java.util.Date()) + ","	//创建时间
									+dbl.sqlString(rsDetail.getString("FmanagerCode")) + ","//QDII机构代码
									+dbl.sqlString(rsDetail.getString("FmanagerName")) + ","//机构名称
									+dbl.sqlString(rsDetail.getString("fportname")) + ","//客户/产品名称
									//+dbl.sqlString(rsDetail.getString("fratereason2"))+","//汇兑原因
									+ 0 +","//汇兑原因 暂时写死
									+dbl.sqlString(accCode)+"," //账户
									+"' '" +")";//币种代码
									
									dbl.executeSql(strSql);
									
									buff.append(rsDetail.getString("FmanagerCode")).append("\t");
									buff.append(rsDetail.getString("FmanagerName")).append("\t");
									buff.append(rsDetail.getString("fportname")).append("\t");
									buff.append(YssFun.formatDate(rsDetail.getDate("FSettleDate"))).append("\t");
									buff.append(rsDetail.getString("fratereason")).append("\t");
									buff.append(accCode).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FSMoneyA"), "#,##0.00")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FBMoneyAUSD"), "#,##0.00")).append("\t");
									buff.append(rsDetail.getString("FCuryB")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FSMoneyB"), "#,##0.00")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FSMoneyBUSD"), "#,##0.00")).append("\t");
									buff.append(rsDetail.getString("FCuryC")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FBMoneyC"), "#,##0.00")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FBMoneyCUSD"), "#,##0.00")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FSMoneyDUSD"), "#,##0.00")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FSMoneyD"), "#,##0.00")).append("\t");
									buff.append(" ").append("\r\n");
									
									iSerialNum ++;
				  	          }
				  	        dbl.closeResultSetFinal(rsDetail);
				          }
			            dbl.closeResultSetFinal(rsSettleDate);
						conn.commit();
			            bTrans = false;
			            conn.setAutoCommit(true);
			            return buff.toString();
				 }
				catch(Exception ye)
				{
					throw new YssException("外管局报表——资金汇出入及结购汇明细信息出错：" + ye.getMessage());
				}
				finally
				{
					dbl.closeResultSetFinal(rsDetail,rsSettleDate);
					dbl.endTransFinal(conn, bTrans);
				}
	}	
//20121218 added by zhaoxianlin. STORY #3383
	//生成外管局报表——资金汇出入及结购汇明细信息
	private String getCashFlow2WGJRep() throws YssException
	{
		String strSqlCash = "";
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		StringBuffer buff = new StringBuffer();    	
		ResultSet rs = null;
		ResultSet rsDetail = null;
		ResultSet rsSettleDate = null;
		int iSerialNum = 1;
		String tempRw = "";
		String accCode = " ";
		Boolean hasCashCode =false;  //判断账户是否为境内外币主账户
		try
		{
			conn.setAutoCommit(false);
			bTrans = true;
			
			strSql = "delete from " + pub.yssGetTableName("tb_rep_WGJRep") 
					  +" where FRepCode = 'REP_HG_WGJ_ZJHCRJGH' and FPortCode = " + dbl.sqlString(this.portCode)
					  +" and FInOutDate between "+ dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate);
			dbl.executeSql(strSql);
			//==================================
			strSql = " select distinct FSettleDate from  " + pub.yssGetTableName("Tb_Data_RateTrade") + " where " +
					"FPortCode = " + dbl.sqlString(this.portCode) + " and FSettleDate between " + dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate)
					 + " and FCheckState = 1 order by FSettleDate";
			rsSettleDate = dbl.queryByPreparedStatement(strSql);
			
			while(rsSettleDate.next())
			{
					strSqlCash = " select case when FGCashAccCodeA<>' ' then FGCashAccCodeA  when FHCsAccCodeB<>' ' then FHCsAccCodeB " 
						+" when FHRbAccCodeC<>' ' then FHRbAccCodeC  when FJHsAccCodeD<>' ' then FJHsAccCodeD else ' ' end as FaccCode,"
						+" round(Nvl(FSMoneyA,0),2) as FSMoneyA,round(Nvl(FSMoneyA,0) * Nvl(rateB.FExRate,1),2) as FBMoneyAUSD, "	//资金购汇数据人民币、等值美元
						+" Nvl(FCuryB,' ') as FCuryB,round(Nvl(FSMoneyB,0),2) as FSMoneyB,round(Nvl(FSMoneyB,0) * Nvl(rateS.FExRate,1),2) as FSMoneyBUSD, "	//资金汇出数据币种、金额、等值美元
						+" Nvl(FCuryC,' ') as FCuryC,round(Nvl(FBMoneyC,0),2) as FBMoneyC,round(Nvl(FBMoneyC,0) * Nvl(rateSC.FExRate,1),2) as FBMoneyCUSD, "	//资金汇入币种、金额、等值美元
						+" round(Nvl(FBMoneyD,0),2) as FSMoneyD,round(Nvl(FBMoneyD,0) * Nvl(rateSD.FExRate,1),2) as FSMoneyDUSD "			//资金结汇等值美元、人民币
						+" from "
						+" ( "
						+"  select * from "
						+"  ( "		//获取资金购汇数据 
	                    +"    select sum(Nvl(FSMoney,0)) as FSMoneyA,FBCuryCode as FCuryA,FBCashAccCode as FGCashAccCodeA from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
						+"    where FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + " and FBCuryCode <> 'CNY' and FSCuryCode = 'CNY' and FcheckState = 1 "
						+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						
						//--- add by songjie 2013.07.05 BUG 8552 QDV4招行2013年07月03日01_B start---//
						//根据 买入组合、卖出组合 等于 已选组合代码作为查询条件
						+"    and FPortCode = " + dbl.sqlString(this.portCode) 
						+"    and FBPortCode = " + dbl.sqlString(this.portCode)
						//--- add by songjie 2013.07.05 BUG 8552 QDV4招行2013年07月03日01_B end---//
						
						+"    Group by FBCashAccCode,FBCuryCode) a "
						+"  full join  "
						+"  ( "		//资金汇出数据
						+"    select FSCuryCode as FCuryB,sum(Nvl(FSMoney,0)) as FSMoneyB,FSCashAccCode as FHCsAccCodeB from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
						+"    where FcheckState = 1 and FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + " "
						+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) "
						+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						
						//--- add by songjie 2013.07.05 BUG 8552 QDV4招行2013年07月03日01_B start---//
						//根据 买入组合、卖出组合 等于 已选组合代码作为查询条件
						+"    and FPortCode = " + dbl.sqlString(this.portCode) 
						+"    and FBPortCode = " + dbl.sqlString(this.portCode)
						//--- add by songjie 2013.07.05 BUG 8552 QDV4招行2013年07月03日01_B end---//
						
						+"   Group by FSCashAccCode,FSCuryCode) b on a.FGCashAccCodeA =b.FHCsAccCodeB "
						+"  full join "
						+"  ( "		//资金汇入数据
						+"    select FBCuryCode as FCuryC,sum(Nvl(FBMoney,0)) as FBMoneyC,FBCashAccCode as FHRbAccCodeC from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
						+"    where FcheckState = 1 and FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + " "
						+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'secondary')) "
						
						//--- add by songjie 2013.07.05 BUG 8552 QDV4招行2013年07月03日01_B start---//
						//根据 买入组合、卖出组合 等于 已选组合代码作为查询条件
						+"    and FPortCode = " + dbl.sqlString(this.portCode) 
						+"    and FBPortCode = " + dbl.sqlString(this.portCode)
						//--- add by songjie 2013.07.05 BUG 8552 QDV4招行2013年07月03日01_B end---//
						
						+"    Group by FBCashAccCode,FBCuryCode) c on b.FHCsAccCodeB =c.FHRbAccCodeC "
						+"  full join "
						+"  ( "		//资金结汇数据
						+"    select sum(Nvl(FSMoney,0))  as FSMoneyD,sum(Nvl(FBMoney,0)) as FBMoneyD,FSCuryCode as FCuryD,FSCashAccCode as FJHsAccCodeD from " + pub.yssGetTableName("Tb_Data_RateTrade") + " "
						+"    where FcheckState = 1 and FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + " and FBCuryCode = 'CNY' and FSCuryCode <> 'CNY' "
						+"    and FBCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						+"    and FSCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") + " where  "
						+"    FBankCode in (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FRelatype = 'Trustee' and FRelaGrade = 'primary')) "
						
						//--- add by songjie 2013.07.05 BUG 8552 QDV4招行2013年07月03日01_B start---//
						//根据 买入组合、卖出组合 等于 已选组合代码作为查询条件
						+"    and FPortCode = " + dbl.sqlString(this.portCode) 
						+"    and FBPortCode = " + dbl.sqlString(this.portCode)
						//--- add by songjie 2013.07.05 BUG 8552 QDV4招行2013年07月03日01_B end---//
						
						+"    Group by FSCashAccCode,FSCuryCode) d on c.FHRbAccCodeC =d.FJHsAccCodeD  "
						+" ) trade "
						+" left join   "
						+" ( "		//买入货币的平均汇率
						+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeB from " + pub.yssGetTableName("tb_data_exchangerate") + " "
						+"  where fcurycode = 'CNY' and fmarkcury = 'USD' and FExRateSrcCode = " + dbl.sqlString(this.rateSource) +  " and FExRateDate in "
						+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where fcurycode = 'CNY' and fmarkcury = 'USD' and FExRateDate <= "
						+"  last_day(" + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) +  " and FcheckState  = 1) "
						+" ) rateB on 1=1 "
						+" left join   "
						+" ( "		//卖出货币的平均汇率
						+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeS from " + pub.yssGetTableName("tb_data_exchangerate") + " "
						+"  where fmarkcury = 'USD' and FExRateSrcCode = " + dbl.sqlString(this.rateSource) +  " and FExRateDate in "
						+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where fmarkcury = 'USD' and FExRateDate <= "
						+"  last_day(" + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) +  " and FcheckState  = 1) "
						+" ) rateS on trade.FCuryB = rateS.FCuryCodeS "
						+" left join   "
						+" ( "		//卖出货币的平均汇率
						+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeS from " + pub.yssGetTableName("tb_data_exchangerate") + " "
						+"  where fmarkcury = 'USD' and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FExRateDate in "
						+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where fmarkcury = 'USD' and FExRateDate <= "
						+"  last_day(" + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) +  " and FcheckState  = 1) "
						+" ) rateSC on trade.FCuryC = rateSC.FCuryCodeS "
						+" left join   "
						+" ( "		//卖出货币的平均汇率
						+"  select Nvl(FExRate1, 1) as FExRate, FCuryCode as FCuryCodeS from " + pub.yssGetTableName("tb_data_exchangerate") + " "
						+"  where fcurycode = 'CNY' and fmarkcury = 'USD' and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + " and FExRateDate in "
						+"  (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + " where fcurycode = 'CNY' and fmarkcury = 'USD' and FExRateDate <= "
						+"  last_day(" + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + ") and FExRateSrcCode = " + dbl.sqlString(this.rateSource) +  " and FcheckState  = 1) "
						+" ) rateSD on 1=1 ";
					
				      strSql = " select distinct  m.fsettledate,' ' as fratereason,' ' as FrateReason2,m.fportcode," 
				                +" a1.Fmanagercode,a1.FmanagerName,a2.fportname,b1.*,b2.FBankAccount  from "
							    + pub.yssGetTableName("Tb_Data_RateTrade") + " m "
							    +" left join (select b.fportCode,e.Fmanagercode,e.fmanagername from " +pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " b "
							    +" join (select d.* from "+pub.yssGetTableName("Tb_Para_Manager") + " d " +" ) e on b.FSubCode = e.fmanagerCode"
							    +"  where b.FRelaType = 'Manager') a1 on m.fportcode = a1.fportcode left join (select * from "+pub.yssGetTableName("Tb_Para_Portfolio")
							    +" where Fcheckstate = 1) a2 on a2.fportcode = m.fportcode  left join ("+strSqlCash+ ") b1 on 1 = 1  " 
							    +" left join (select FCashAccCode,FBankAccount from " +pub.yssGetTableName("Tb_Para_CashAccount")
							    +" where FcheckState =1 ) b2 on b2.FCashAccCode =b1.FaccCode "
							    +" where m.FSettleDate = " + dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) 
							    + " and m.fportcode = " + dbl.sqlString(this.portCode);
							    
				  	          rsDetail = dbl.openResultSet(strSql);
				  	          
				  	          while(rsDetail.next()){
				  	        	 
//				  	        	if (rsDetail.getDouble("FSMoneyA") == 0 && rsDetail.getDouble("FBMoneyAUSD") == 0 && rsDetail.getDouble("FSMoneyD") == 0 && rsDetail.getDouble("FSMoneyDUSD") == 0)
//								{//当日无购汇
//									continue;
//								}
				  	        	 //-----add by zhaoxianlin 20130109 BUG6825---start//
                                if (rsDetail.getString("FaccCode") == null || rsDetail.getString("FaccCode").equals("")) {
				                     continue;
				                 }
                                //-----add by zhaoxianlin 20130109 BUG6825---end//
				  	        	hasCashCode=getAcctCode(rsDetail.getString("FaccCode")); //判断账户是否是境内外币主账户
				  	        	if(!hasCashCode){  //账户不是境内外币主账户时不显示
				  	        		      continue;
				  	        	}
									//将查询到的数据插入的外管局月报数据表中
									strSql = "insert into " + pub.yssGetTableName("tb_rep_WGJRep") 
									+ " Values(" + dbl.sqlString("REP_HG_WGJ_ZJHCRJGH") + ","	//报表代码
									+ dbl.sqlString(this.portCode) + ","	//组合代码
									+ dbl.sqlDate(this.startDate) + ","		//起始日期
									+ dbl.sqlDate(this.endDate) + ","		//结束日期
									+ String.valueOf(iSerialNum) + ","		//序号
									+ "' ',"								//项目编号
									+ dbl.sqlDate(rsSettleDate.getDate("FSettleDate")) + ","		//汇入汇出日期
									+ rsDetail.getDouble("FSMoneyA") + ","	//资金购汇人民币
									+ rsDetail.getDouble("FBMoneyAUSD") + ","	//资金购汇等值美元
									+ dbl.sqlString(rsDetail.getString("FCuryB")) + ","	//资金汇出币种
									+ rsDetail.getDouble("FSMoneyB") + ","	//资金汇出金额
									+ rsDetail.getDouble("FSMoneyBUSD") + ","	//资金汇出等值美元
									+ dbl.sqlString(rsDetail.getString("FCuryC")) + ","	//资金汇入币种
									+ rsDetail.getDouble("FBMoneyC") + ","	//资金汇入金额
									+ rsDetail.getDouble("FBMoneyCUSD") + ","	//资金汇入等值美元
									+ rsDetail.getDouble("FSMoneyDUSD") + ","	//资金结汇等值美元
									+ rsDetail.getDouble("FSMoneyD") + ","	//资金结汇人民币
									+ 0 + ","	//净汇出金额
									+ 0 + ","	//净购汇金额
									+ "' '" + ","	//备注
									+ dbl.sqlString(pub.getUserCode()) + ","	//创建人用户代码
									+ dbl.sqlDate(new java.util.Date()) + ","	//创建时间
									+dbl.sqlString(rsDetail.getString("FmanagerCode")) + ","//QDII机构代码
									+dbl.sqlString(rsDetail.getString("FmanagerName")) + ","//机构名称
									+dbl.sqlString(rsDetail.getString("fportname")) + ","//客户/产品名称
									//+dbl.sqlString(rsDetail.getString("fratereason2"))+","//汇兑原因
									+ 0 +","//汇兑原因 暂时写死
									+dbl.sqlString(rsDetail.getString("FBankAccount"))+"," //账户
									+"' '" +")";//币种代码
									
									dbl.executeSql(strSql);
									
									buff.append(rsDetail.getString("FmanagerCode")).append("\t");
									buff.append(rsDetail.getString("FmanagerName")).append("\t");
									buff.append(rsDetail.getString("fportname")).append("\t");
									buff.append(" ").append("\t").append(" ").append("\t");	//20130305 added by liubo.Story #3517.与模板中的“符号”和“变动编号”两个隐藏列对应
									buff.append(YssFun.formatDate(rsDetail.getDate("FSettleDate"))).append("\t");
									buff.append(rsDetail.getString("fratereason")).append("\t");
									buff.append(rsDetail.getString("FBankAccount")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FSMoneyA"), "#,##0.00")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FBMoneyAUSD"), "#,##0.00")).append("\t");
									buff.append(rsDetail.getString("FCuryB")).append("\t");
									buff.append(" ").append("\t");	//20130305 added by liubo.Story #3517.与模板中的汇出一项的“币种名称”的隐藏列对应
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FSMoneyB"), "#,##0.00")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FSMoneyBUSD"), "#,##0.00")).append("\t");
									buff.append(rsDetail.getString("FCuryC")).append("\t");
									buff.append(" ").append("\t");	//20130305 added by liubo.Story #3517.与模板中的汇入一项的“币种名称”的隐藏列对应
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FBMoneyC"), "#,##0.00")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FBMoneyCUSD"), "#,##0.00")).append("\t");
                                	buff.append(YssFun.formatNumber(rsDetail.getDouble("FSMoneyD"), "#,##0.00")).append("\t");
									buff.append(YssFun.formatNumber(rsDetail.getDouble("FSMoneyDUSD"), "#,##0.00")).append("\t");
									buff.append(" ").append("\r\n");
									
									iSerialNum ++;
				  	          }
				  	        dbl.closeResultSetFinal(rsDetail);
				          }
			            dbl.closeResultSetFinal(rsSettleDate);
						conn.commit();
			            bTrans = false;
			            conn.setAutoCommit(true);
			            return buff.toString();
				 }
				catch(Exception ye)
				{
					throw new YssException("外管局报表——资金汇出入及结购汇明细信息出错：" + ye.getMessage());
				}
				finally
				{
					dbl.closeResultSetFinal(rsDetail,rsSettleDate);
					dbl.endTransFinal(conn, bTrans);
				}
	}
	/**
	 * add by zhaoxianlin 20121227 #story 3383 外管局报表——资金汇出入及结购汇明细信息
	 * @param settleDate
	 * @return
	 * @throws YssException
	 */
	private Boolean getAcctCode(String accCode) throws YssException{
		String strSql = " ";
		String strAcc = " ";
		
		ResultSet rsAcc = null;
        ResultSet rs = null;
		List primAcct = new ArrayList(); //境内外币主托管行集合
		Boolean flag = false;
		try{
			 strSql = " select FCashAccCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+ " where   FcheckState = 1 and FAccSort = 1" +
			 		" and FcashAccCode in (select FcashAccCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+" where FBankCode =(select c.FTrusteeCode from "+
			 		pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")+" b join (select d.* from "+pub.yssGetTableName("Tb_Para_Trustee")+
			 		" d) c on b.FSubCode =c.FTrusteeCode where b.FRelaType = 'Trustee' and b.FRelaGrade = 'primary' and b.fportcode = "+dbl.sqlString(portCode)+
			 		"))  and FCuryCode <> 'CNY'";
		     rs = dbl.openResultSet(strSql);  //所有主托管行结果集
		     while(rs.next()){
		    	 primAcct.add(rs.getString(1));
		     }		
             for(int i=0;i<primAcct.size();i++){ //循环遍历外汇交易数据现金账户，若存在境内外币主托管行，返回该账户，否则为空；
		    		 if(accCode.equals((String)primAcct.get(i))){
		    			 flag = true;
		    			 break;
		    		 }
		    	 }
		     return flag;
		}catch(Exception e){
			throw new YssException("判断资金汇出入及结购汇明细信息账号是否是境内外币主托管行账户出错！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs,rsAcc);
		}		
	}
	private double dTotalAssetsCost = 0;		//资产合计的成本
	private double dTotalAssetsMarketValue = 0;	//资产合计的市值
	private double dTotalDebtsCost = 0;			//负债合计的成本
	private double dTotalDebtsMarketValue = 0;	//负债合计的市值
    private double dTotalInvestMV = 0 ; //投资市值合计
	
	//20121110 added by liubo.Story #3144
	//往外管局月报数据表中插入数据，并将报表数据拼接，返回给前台
	private String The2ndWGJReportOper(String sProType,String sProName,String sSerialNo,String sSetCode,String sProjType) throws YssException
	{
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		ResultSet rs = null;
		double dCost = 0;
		double dMarketValue = 0;
		try
		{
			if (sSetCode.trim().equals(""))
			{
				return " ";
			}
			
			conn.setAutoCommit(false);
			bTrans = true;
			
			//需要生成资产合计的数据时，直接将统计好的资产类的成本和资产类的市值传回前台
			if (sProjType.equalsIgnoreCase("TotalDebts"))
			{
				dCost = dTotalDebtsCost;
				dMarketValue = dTotalDebtsMarketValue;
			}
			//需要生成负债合计的数据时，直接将统计好的负债类的成本和负债类的市值传回前台
			else if (sProjType.equalsIgnoreCase("TotalAssets"))
			{
				dCost = dTotalAssetsCost;
				dMarketValue = dTotalAssetsMarketValue;
			}
			//需要生成净资产时，直接将统计好的资产合计的成本-负债合计的成本，用资产合计的市值-负债合计的市值
			else if (sProjType.equalsIgnoreCase("NetAssets"))
			{
				dCost = dTotalAssetsCost - dTotalDebtsCost;
				dMarketValue = dTotalAssetsMarketValue - dTotalDebtsMarketValue;
			}
			
			//需要生成的是普通的资产类和负债类的时候，根据传入的科目性质和科目类别，在估值表中取出数据，并乘以外管局给予的平均汇率
			else
			{
				strSql = " select " + sSerialNo + " as FNum," + dbl.sqlString(sProName) + " as FProjectName," +
						 " Nvl(round(sum(FCost * FExRate),2),0) as FCost,Nvl(round(sum(FMarKetValue * FExRate),2),0) as FMarKetValue " +
						 " from " +
						 " (select (case when c.FAcctLevel = a.FAcctLevel then a.Fcost else 0 end) as Fcost," +
						 " (case when c.FAcctLevel = a.FAcctLevel then a.FMarKetValue else 0 end) as FMarKetValue,Nvl(b.FExRate,1) as FExRate " +
						 " from " +
						 " (" +
						 " select distinct Nvl(FCost,0) as FCost,Nvl(FMarKetValue,0) as FMarKetValue,FCurCode,FAcctCode,FAcctLevel " +
						 " from " + pub.yssGetTableName("tb_rep_guessvalue") + " where " + sProType + 
						 " and FAcctDetail = '1' and FPortCode = " + dbl.sqlString(sSetCode) +
						 " and FDate in (select max(FDate) as FDate from " + pub.yssGetTableName("tb_rep_guessvalue") + 
						 " where to_char(FDate,'mm') = to_char(" + dbl.sqlDate(this.startDate) + ",'mm') and FPortCode = " + dbl.sqlString(sSetCode) + ")" +
						 " ) a " +
						 //根据前台传入的汇率来源，获取平均汇率
						 " left join (select Nvl(FExRate1,1) as FExRate,FCuryCode from " + pub.yssGetTableName("tb_data_exchangerate") + 
						 " where FExRateSrcCode = " + dbl.sqlString(this.rateSource) + 
						 " and FExRateDate in (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + 
						 " where FExRateDate <= last_day(" + dbl.sqlDate(this.startDate) + ") " +
						 " and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + ")) b " +
						 " on a.FCurCode = b.FCuryCode " +
						 //截取明细科目号的前八位，即第三级科目
						 //以前八位科目号对所有的明细科目进行分组，并以前八位科目号为条件，查询出这些科目号的最高级别，统一进行分组
						 //然后以前八位科目号和最高科目级别来筛选a子表中的科目
						 //以上方法主要用来筛选出某个分类的最明细科目，如股票投资，第三级第四级都是设置成明细科目，但是带辅助核算项的第四级科目才是最明细的科目
						 " left join (select substr(FAcctCode,0,8) as cnt,max(FAcctLevel) as FAcctLevel from " + pub.yssGetTableName("tb_rep_guessvalue") + 
						 " group by substr(FacctCode,0,8) having substr(FAcctCode,0,8) in ( " +
						 " select distinct substr(FacctCode,0,8) as cnt " +
						 " from " + pub.yssGetTableName("tb_rep_guessvalue") + " " +
						 " where " + sProType + 
						 " and FAcctDetail = '1' and FPortCode = " + dbl.sqlString(sSetCode) +
						 " and FDate in (select max(FDate) as FDate from " + pub.yssGetTableName("tb_rep_guessvalue") + 
						 " where to_char(FDate,'mm') = to_char(" + dbl.sqlDate(this.startDate) + ",'mm') and FPortCode = " + dbl.sqlString(sSetCode) + "))" +
						 " ) c" +
                         " on substr(a.FAcctCode,0,8) = c.cnt)";
				
				rs = dbl.queryByPreparedStatement(strSql);
				
				if (rs.next())
				{
					dCost = rs.getDouble("FCost");
					dMarketValue = rs.getDouble("FMarketValue");

		            //前台显示数据时，要负债一栏显示绝对值，不要显示小数
		            	if (dCost < 0)
		            	{
		            		dCost = YssD.mul(dCost,-1);
		            	}
		            	if (dMarketValue < 0)
		            	{
		            		dMarketValue = YssD.mul(dMarketValue,-1);
		            	}
					
					if (sProjType.equalsIgnoreCase("Assets"))
					{
						dTotalAssetsCost += dCost;
						dTotalAssetsMarketValue += dMarketValue;
					}
					else if (sProjType.equalsIgnoreCase("Debts"))
					{
			            //前台显示数据时，要负债一栏显示绝对值，不要显示小数
						//==========================
		            	if (dCost < 0)
		            	{
		            		dCost = YssD.mul(dCost,-1);
		            	}
		            	if (dMarketValue < 0)
		            	{
		            		dMarketValue = YssD.mul(dMarketValue,-1);
		            	}
						//============end==============
		            	
						dTotalDebtsCost += dCost;
						dTotalDebtsMarketValue += dMarketValue;
					}
				}
			}
			
			//成本和市值计算完毕之后，将数据插入外管局月报数据表中
			strSql = "insert into " + pub.yssGetTableName("tb_rep_WGJRep") + "(FRepCode,FPortCode,FStartDate,FEndDate,FSerialNo,FKeyName,FDxfOut_USD,FDxfIn_USD,FCREATOR,FCREATETIME)"
					 + " values(" + dbl.sqlString(this.sRepCode) + "," 	//报表代码
					 + dbl.sqlString(portCode) + "," 					//组合代码
					 + dbl.sqlDate(this.startDate) + ","				//起始日期
					 + dbl.sqlDate(this.startDate) + ","				//结束日期，实际就是起始日期
					 + sSerialNo + ","									//序号
					 + dbl.sqlString(sProName) + ","					//项目名称
					 + "round(" + dCost + ",2),"						//成本
					 + "round(" + dMarketValue + ",2),"					//市值
					 + dbl.sqlString(pub.getUserCode()) + ","			//创建人的用户代码
					 + dbl.sqlDate(new java.util.Date()) + ")";			//创建时间
			dbl.executeSql(strSql);
			
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
                        
            //最后将计算好的成本和市值拼接、传回
    		return YssFun.formatNumber(dCost, "#,##0.00") + "\t" + YssFun.formatNumber(dMarketValue, "#,##0.00") + "\r\n";
		}
		catch(Exception ye)
		{
			throw new YssException(ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn,bTrans);
		}
		
	}
	//add by zhaoxianlin 20121218 STORY #3381 外管局报表——QDII境外证券投资信息
	//往外管局月报数据表中插入数据，并将报表数据拼接，返回给前台
	private String secInvestWGJReportOper(String sProType,String sProName,String sSerialNo,String sSetCode,String sProjType) throws YssException
	{
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		ResultSet rs = null;
		double dCost = 0;
		double dMarketValue = 0;
		try
		{
			if (sSetCode.trim().equals(""))
			{
				return " ";
			}
			
			conn.setAutoCommit(false);
			bTrans = true;
			//需要生成投资市值合计时，直接将统计好的 市值传回前台
			if(sProjType.equalsIgnoreCase("TotalInvestMV")){
				dMarketValue = this.dTotalInvestMV;
			}
			//需要生成资产合计的数据时，直接将统计好的资产类的成本和资产类的市值传回前台
			else if (sProjType.equalsIgnoreCase("TotalDebts"))
			{
				dCost = dTotalDebtsCost;
				dMarketValue = dTotalDebtsMarketValue;
			}
			//需要生成负债合计的数据时，直接将统计好的负债类的成本和负债类的市值传回前台
			else if (sProjType.equalsIgnoreCase("TotalAssets"))
			{
				dCost = dTotalAssetsCost;
				dMarketValue = dTotalAssetsMarketValue;
			}
			//需要生成净资产时，直接将统计好的资产合计的成本-负债合计的成本，用资产合计的市值-负债合计的市值
			else if (sProjType.equalsIgnoreCase("NetAssets"))
			{
				dCost = dTotalAssetsCost - dTotalDebtsCost;
				dMarketValue = dTotalAssetsMarketValue - dTotalDebtsMarketValue;
			}
			
			//需要生成的是普通的资产类和负债类的时候，根据传入的科目性质和科目类别，在估值表中取出数据，并乘以外管局给予的平均汇率
			else
			{
				if (YssCons.YSS_WGJRep_BuldingMode.equalsIgnoreCase("JH"))
				{
					strSql = " select " + sSerialNo + " as FNum," + dbl.sqlString(sProName) + " as FProjectName," +
							 " Nvl(round(sum(FCost * FExRate),2),0) as FCost,Nvl(round(sum(FMarKetValue * FExRate),2),0) as FMarKetValue " +
							 " from " +
							 " (select (case when c.FAcctLevel = a.FAcctLevel then a.Fcost else 0 end) as Fcost," +
							 " (case when c.FAcctLevel = a.FAcctLevel then a.FMarKetValue else 0 end) as FMarKetValue,Nvl(b.FExRate,1) as FExRate " +
							 " from " +
							 " (" +
							 " select distinct Nvl(FCost,0) as FCost,Nvl(FMarKetValue,0) as FMarKetValue,FCurCode,FAcctCode,FAcctLevel " +
							 " from " + pub.yssGetTableName("tb_rep_guessvalue") + " where " + sProType + 
							 " and FAcctDetail = '1' and FPortCode = " + dbl.sqlString(sSetCode) +
							 " and FDate in (select max(FDate) as FDate from " + pub.yssGetTableName("tb_rep_guessvalue") + 
							 " where to_char(FDate,'yyyymm') = to_char(" + dbl.sqlDate(this.startDate) + ",'yyyymm') and FPortCode = " + dbl.sqlString(sSetCode) + ")" +
							 " ) a " +
							 //根据前台传入的汇率来源，获取平均汇率
							 " left join (select Nvl(FExRate1,1) as FExRate,FCuryCode from " + pub.yssGetTableName("tb_data_exchangerate") + 
							 " where fmarkcury = 'USD' and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + 
							 " and FExRateDate in (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + 
							 " where fmarkcury = 'USD' and FExRateDate <= last_day(" + dbl.sqlDate(this.startDate) + ") " +
							 " and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + ")) b " +
							 " on a.FCurCode = b.FCuryCode " +
							 //截取明细科目号的前八位，即第三级科目
							 //以前八位科目号对所有的明细科目进行分组，并以前八位科目号为条件，查询出这些科目号的最高级别，统一进行分组
							 //然后以前八位科目号和最高科目级别来筛选a子表中的科目
							 //以上方法主要用来筛选出某个分类的最明细科目，如股票投资，第三级第四级都是设置成明细科目，但是带辅助核算项的第四级科目才是最明细的科目
							 " left join (select substr(FAcctCode,0,8) as cnt,max(FAcctLevel) as FAcctLevel from " + pub.yssGetTableName("tb_rep_guessvalue") + 
					/**Start 20130814 modified by liubo.Bug #9018.QDV4招行2013年08月08日01_B
					 * 在获取某个getCNTList方法计算出来的科目号所属最大的科目级别时，需要加上套账号和日期作为判断条件
					 * 避免出现某个科目代码在前面的某个日期，有最大科目级别为5的数据，而估值日当天最大科目级别只有4
					 * 从而导致关联不到正确的科目代码，取不出数据的问题*/
							 " group by substr(FacctCode,0,8),FPortCode,FDate " +
							 " having substr(FAcctCode,0,8) in (" +getCNTList(sProType,sSetCode)+   //这里将原先的sql脱离出去  modified  by zhaoxianlin BUG 6868系统在生产QDII境外证券投资信息表时要10分钟左右 
							 " ) and FPortCode = " + dbl.sqlString(sSetCode) + 
							 " and FDate = " + dbl.sqlDate(startDate) + ") c" +
		                    " on substr(a.FAcctCode,0,8) = c.cnt)";
					/**End 20130814 modified by liubo.Bug #9018.QDV4招行2013年08月08日01_B*/
				}
				else
				{
					strSql = " select " + sSerialNo + " as FNum," + dbl.sqlString(sProName) + " as FProjectName," +
					 " Nvl(round(sum(FCost * FExRate),2),0) as FCost,Nvl(round(sum(FMarKetValue * FExRate),2),0) as FMarKetValue " +
					 " from " +
					 " (select (case when c.FAcctLevel = a.FAcctLevel then a.Fcost else 0 end) as Fcost," +
					 " (case when c.FAcctLevel = a.FAcctLevel then a.FMarKetValue else 0 end) as FMarKetValue,Nvl(b.FExRate,1) as FExRate " +
					 " from " +
					 " (" +
					 " select distinct Nvl(fstandardmoneycost,0) as FCost,Nvl(fstandardmoneymarketvalue,0) as FMarKetValue,FCurCode,FAcctCode,FAcctLevel " +
					 " from " + pub.yssGetTableName("tb_rep_guessvalue") + " where " + sProType + 
					 " and FAcctDetail = '1' and FPortCode = " + dbl.sqlString(sSetCode) +
					 " and FDate in (select max(FDate) as FDate from " + pub.yssGetTableName("tb_rep_guessvalue") + 
					 " where to_char(FDate,'yyyymm') = to_char(" + dbl.sqlDate(this.startDate) + ",'yyyymm') and FPortCode = " + dbl.sqlString(sSetCode) + ")" +
					 " ) a " +
					 //根据前台传入的汇率来源，获取平均汇率
					 " left join (select Nvl(FExRate1,1) as FExRate,FCuryCode from " + pub.yssGetTableName("tb_data_exchangerate") + 
					 " where fcurycode = 'CNY' and fmarkcury = 'USD' and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + 
					 " and FExRateDate in (select max(FExRateDate) as FRateDate from " + pub.yssGetTableName("tb_data_exchangerate") + 
					 " where fcurycode = 'CNY' and fmarkcury = 'USD' and FExRateDate <= last_day(" + dbl.sqlDate(this.startDate) + ") " +
					 " and FExRateSrcCode = " + dbl.sqlString(this.rateSource) + ")) b " +
					 " on 1=1 " +
					 //截取明细科目号的前八位，即第三级科目
					 //以前八位科目号对所有的明细科目进行分组，并以前八位科目号为条件，查询出这些科目号的最高级别，统一进行分组
					 //然后以前八位科目号和最高科目级别来筛选a子表中的科目
					 //以上方法主要用来筛选出某个分类的最明细科目，如股票投资，第三级第四级都是设置成明细科目，但是带辅助核算项的第四级科目才是最明细的科目
					 " left join (select substr(FAcctCode,0,8) as cnt,max(FAcctLevel) as FAcctLevel from " + pub.yssGetTableName("tb_rep_guessvalue") + 
				/**Start 20130814 modified by liubo.Bug #9018.QDV4招行2013年08月08日01_B
				 * 在获取某个getCNTList方法计算出来的科目号所属最大的科目级别时，需要加上套账号和日期作为判断条件
				 * 避免出现某个科目代码在前面的某个日期，有最大科目级别为5的数据，而估值日当天最大科目级别只有4
				 * 从而导致关联不到正确的科目代码，取不出数据的问题*/
					 " group by substr(FacctCode,0,8),FPortCode,FDate " +
					 " having substr(FAcctCode,0,8) in (" +getCNTList(sProType,sSetCode)+   //这里将原先的sql脱离出去  modified  by zhaoxianlin BUG 6868系统在生产QDII境外证券投资信息表时要10分钟左右 
					 " ) and FPortCode = " + dbl.sqlString(sSetCode) + 
					 " and FDate = " + dbl.sqlDate(startDate) + ") c" +
                   " on substr(a.FAcctCode,0,8) = c.cnt)";
				/**End 20130814 modified by liubo.Bug #9018.QDV4招行2013年08月08日01_B*/
				}
				
				
				rs = dbl.queryByPreparedStatement(strSql);
				
				if (rs.next())
				{
					dCost = rs.getDouble("FCost");
					dMarketValue = rs.getDouble("FMarketValue");

		            //前台显示数据时，要负债一栏显示绝对值，不要显示小数
		            	if (dCost < 0)
		            	{
		            		dCost = YssD.mul(dCost,-1);
		            	}
		            	if (dMarketValue < 0)
		            	{
		            		dMarketValue = YssD.mul(dMarketValue,-1);
		            	}
		            if(sProName.equals("银行存款")||sProName.equals("货币市场工具")||sProName.equals("债券投资")||
		            		sProName.equals("公司股票")||sProName.equals("基金")||sProName.equals("衍生产品")||
		            		sProName.equals("其他投资")){
		            	       dTotalInvestMV += dMarketValue;
		            }
					if (sProjType.equalsIgnoreCase("Assets"))
					{ 
						if(!sProType.equals("FAcctAttr like '银行存款%' and FAcctClass = '资产类' and FCurCode ='CNY' ")){
							dTotalAssetsCost += dCost;
							dTotalAssetsMarketValue += dMarketValue;
					    }
					}
					else if (sProjType.equalsIgnoreCase("Debts"))
					{
			            //前台显示数据时，要负债一栏显示绝对值，不要显示小数
						//==========================
		            	if (dCost < 0)
		            	{
		            		dCost = YssD.mul(dCost,-1);
		            	}
		            	if (dMarketValue < 0)
		            	{
		            		dMarketValue = YssD.mul(dMarketValue,-1);
		            	}
						//============end==============
		            	
						dTotalDebtsCost += dCost;
						dTotalDebtsMarketValue += dMarketValue;
					}
				}
			}		
	 if(sProName.equals("存款投资_定期存款")){
				   subDeposit  = dMarketValue;
				   return "";
			}else if(sProName.equals("基金投资_开发式_货币")){
				subFundCost = dCost;
				subFundMV = dMarketValue;
				   return "";
			}
			//成本和市值计算完毕之后，将数据插入外管局月报数据表中
			strSql = "insert into " + pub.yssGetTableName("tb_rep_WGJRep") + "(FRepCode,FPortCode,FStartDate,FEndDate,FSerialNo,FKeyName,FDxfOut_USD,FDxfIn_USD,FCREATOR,FCREATETIME,ForgCode,ForgName,FProductName,FcuryCode)"
					 + " values(" + dbl.sqlString(this.sRepCode) + "," 	//报表代码
					 + dbl.sqlString(portCode) + "," 					//组合代码
					 + dbl.sqlDate(this.startDate) + ","				//起始日期
					 + dbl.sqlDate(this.startDate) + ","				//结束日期，实际就是起始日期
					 + sSerialNo + ","									//序号
					 + dbl.sqlString(sProName) + ","					//项目名称
					 + "round(" + dCost + ",2),"						//成本
					 + "round(" + dMarketValue + ",2),"					//市值
					 + dbl.sqlString(pub.getUserCode()) + ","			//创建人的用户代码
					 + dbl.sqlDate(new java.util.Date()) + "," 		//创建时间
			         +dbl.sqlString(orgCode)+ ","
			         +dbl.sqlString(orgName)+ ","
			         +dbl.sqlString(productName)+ ","
			         +dbl.sqlString(curyCode) + ")";
			dbl.executeSql(strSql);
			
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            //最后将计算好的成本和市值拼接、传回
            if(sProName.equals("银行存款")||sProName.equals("投资市值合计")||sProName.equals("预付投资款")
            		||sProName.equals("应收投资款")||sProName.equals("应收股利")||sProName.equals("应收利息")
            		||sProName.equals("其他应收款")||sProName.equals("资产合计")||sProName.equals("应付投资款")
            		||sProName.equals("应付托管费")||sProName.equals("应付佣金")||sProName.equals("应付管理费")
            		||sProName.equals("应交税金")||sProName.equals("其他应付款")||sProName.equals("负债合计")
            		||sProName.equals("净资产")||sProName.equals("所托管人民币资金存款余额")){
               if(sProName.equals("银行存款")){
            	   //20130313 deleted by liubo.Bug #7299
            	   //“银行存款”部分，剔除科目性质like '存款投资_定期存款%'的部分,在定义取数逻辑，为ArrayList赋值时即做过了
            	   //在这里再做剔除，就造成了重复剔除，银行存款的金额会出现问题
            	   //而且在向后台表保存数据时，存储的就是做过一次剔除的数据，即正确数据
            	   //所以删除这条剔除逻辑
            	   //=====================================
//            		dMarketValue = dMarketValue - this.subDeposit;
             	   //================end=====================
            	}
            	return YssFun.formatNumber(dMarketValue, "#,##0.00") + "\t";
            }else{
                    if(sProName.equals("基金")){
	            	dCost = YssD.sub(dCost, subFundCost);
            		dMarketValue = YssD.sub(dMarketValue, subFundMV);
            	}
            	return YssFun.formatNumber(dCost, "#,##0.00") + "\t" + YssFun.formatNumber(dMarketValue, "#,##0.00") + "\t";
            }
		}
		catch(Exception ye)
		{
			throw new YssException(ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn,bTrans);
		}
		
	}
	/**
	 * add by zhaoxianlin BUG 6868系统在生产QDII境外证券投资信息表时要10分钟左右 
	 * 这里将SQL脱离出来，提高执行速度
	 * @param sProType
	 * @param sSetCode
	 * @return
	 * @throws YssException
	 */
	public String getCNTList(String sProType,String sSetCode) throws YssException{
		String strSql = "";
		ResultSet rs = null;
		StringBuffer buff = new StringBuffer();
		String strCnt = "' '";
		try{
			 strSql = " select distinct substr(FacctCode,0,8) as cnt " +
			 " from " + pub.yssGetTableName("tb_rep_guessvalue") + " " +
			 " where " + sProType + 
			 " and FAcctDetail = '1' and FPortCode = " + dbl.sqlString(sSetCode) +
			 " and FDate in (select max(FDate) as FDate from " + pub.yssGetTableName("tb_rep_guessvalue") + 
			 " where to_char(FDate,'yyyymm') = to_char(" + dbl.sqlDate(this.startDate) + ",'yyyymm') and FPortCode = " + dbl.sqlString(sSetCode) + ")" ;
		     rs = dbl.openResultSet(strSql);
		     while(rs.next()){
		    	 buff.append("'")
			    	 .append(rs.getInt(1))
			    	 .append("'")
			    	 .append(",");
		     }
		     if(!buff.toString().equals("")){
		    	 strCnt = buff.toString().substring(0, buff.toString().length()-1);
		     }
		     return strCnt;
		}catch(Exception e){
			throw new YssException("获取cnt字段的值出错！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}
	
	//20121114 added by liubo.Story #3144
	//查询报表数据
	private String searchWGJRep() throws YssException
	{
		StringBuffer buff = new StringBuffer();
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			//20130325 modified by liubo.Bug #7368
			//为境内外币托管账户情况表增加查询功能
			//======================================
			if (this.sRepCode.equalsIgnoreCase("RepManagerMonth"))
			{
				buff.append(SearchRepManagerMonth());
				return buff.toString();
			}
			else if (!this.sRepCode.equalsIgnoreCase("REP_HG_WGJ_JNWBTGZH"))
			{
				strSql = "select * from " + pub.yssGetTableName("tb_rep_WGJRep")
						+" where FPortCode = " + dbl.sqlString(this.portCode) + " and FRepCode = " + dbl.sqlString(this.sRepCode)
						+" and FStartDate = " + dbl.sqlDate(this.startDate)
						+" and FEndDate = " + (this.sRepCode.trim().equalsIgnoreCase("REP_HG_WGJ_TZYB2") ? dbl.sqlDate(this.startDate) : dbl.sqlDate(this.endDate))
						+" order by FSerialNo";
			}
			else
			{

				strSql = " select *  from " + pub.yssGetTableName("TB_REP_WGJREP2") + 
						 " where FPRODUCTNAME in (select FPortName from " + pub.yssGetTableName("tb_para_portfolio") + " where FPortCode = " + dbl.sqlString(this.portCode) + ") " +
						 " and FREPORTDATE in ('" + YssFun.formatDate(YssFun.toDate(this.endDate),"yyyy年MM月") + "','" + YssFun.formatDate(YssFun.toDate(this.endDate),"yyyyMM") + "')";
			}
			//==================end====================
			
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{

				//20130325 added by liubo.Bug #7368
				//为境内外币托管账户情况表增加查询功能
				//======================================
				if (sRepCode.trim().equalsIgnoreCase("REP_HG_WGJ_JNWBTGZH"))
				{
//					return getThe1stWGJReportOper();
					
					buff.append(rs.getString("FORGCODE")).append("\t");
					buff.append(rs.getString("FORGNAME")).append("\t");
					buff.append(rs.getString("FPRODUCTNAME")).append("\t");
					buff.append(rs.getString("FACCCODE")).append("\t");
					buff.append(rs.getString("FREPORTDATE")).append("\t");
					buff.append(rs.getString("FCURYCODE")).append("\t");
					buff.append(" ").append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FZH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FZH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FGH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FGH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMGH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMGH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FIJWTGZH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FIJWTGZH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMIJWTGZH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMIJWTGZH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSGHR_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSGHR_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMSGHR_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMSGHR_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FINTEREST_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FINTEREST_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMINTEREST_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMINTEREST_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FIOTHER_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FIOTHER_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMIOTHER_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMIOTHER_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSRHJ_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSRHJ_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMSRHJ_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMSRHJ_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FJH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FJH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMJH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMJH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FOJWTGZH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FOJWTGZH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMOJWTGZH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMOJWTGZH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMSH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMSH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FFH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FFH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMFH_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMFH_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FTGF_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FTGF_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMTGF_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMTGF_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FGLF_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FGLF_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMGLF_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMGLF_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSXF_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSXF_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMSXF_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMSXF_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FOOTHER_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FOOTHER_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMOOTHER_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMOOTHER_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FOHJ_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FOHJ_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMOHJ_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSUMOHJ_USD"),"#,##0.00")).append("\t");
					buff.append(rs.getString("FREMARK")).append("\t");
					buff.append(rs.getString("FCHECKSTATE")).append("\t");
					buff.append(rs.getString("FCREATOR")).append("\t");
					buff.append(rs.getString("FCREATETIME")).append("\t");
					buff.append(rs.getString("FCHECKUSER")).append("\t");
					buff.append(rs.getString("FCHECKTIME")).append("\r\n");
					
				}
				else if (sRepCode.trim().equalsIgnoreCase("REP_HG_WGJ_TZYB2"))
				{
					buff.append(YssFun.formatNumber(rs.getDouble("FDxfOut_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FDxfIn_USD"),"#,##0.00")).append("\r\n");
					
				}
				else if (sRepCode.trim().equalsIgnoreCase("REP_HG_WGJ_TZYB3"))
				{
					buff.append(rs.getInt("FSerialNo")).append("\t");
					buff.append(YssFun.formatDate(rs.getDate("FInOutDate"))).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FPurchase_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FPurchase_USD"),"#,##0.00")).append("\t");
					buff.append(rs.getString("FDxfOut_Cury")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FDxfOut"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FDxfOut_USD"),"#,##0.00")).append("\t");
					buff.append(rs.getString("FDxfOut_USD")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FDxfIn"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FDxfIn_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSettlement_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSettlement_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FNetExport"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FNetPurchase"),"#,##0.00")).append("\t");
					buff.append(rs.getString("FRemark")).append("\r\n");
					
				}
				else if(sRepCode.trim().equalsIgnoreCase("REP_HG_WGJ_ZJHCRJGH")){
					
					buff.append(rs.getString("forgcode")).append("\t");
					buff.append(rs.getString("forgName")).append("\t");
					buff.append(rs.getString("FproductName")).append("\t");
					buff.append(" ").append("\t");	//20130308 added by liubo.Bug #7276.查询的时候需要给隐藏的行号列加上一个空格，避免报表后面的列的数据错列
					buff.append(" ").append("\t");	//20130308 added by liubo.Bug #7276.查询的时候需要给隐藏的变动编号列加上一个空格，避免报表后面的列的数据错列
					buff.append(YssFun.formatDate(rs.getDate("FInOutDate"))).append("\t");
					//buff.append(rs.getDouble("fratereason")).append("\t");
					buff.append(" ").append("\t");
					buff.append(rs.getString("faccCode")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FPurchase_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FPurchase_USD"),"#,##0.00")).append("\t");
					buff.append(rs.getString("FDxfOut_Cury")).append("\t");
					buff.append(" ").append("\t");	//20130308 added by liubo.Bug #7276.查询的时候需要给隐藏的币种名称列加上一个空格，避免报表后面的列的数据错列
					buff.append(YssFun.formatNumber(rs.getDouble("FDxfOut"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FDxfOut_USD"),"#,##0.00")).append("\t");
					buff.append(rs.getString("FDxfOut_USD")).append("\t");
					buff.append(" ").append("\t");	//20130308 added by liubo.Bug #7276.查询的时候需要给隐藏的币种名称列加上一个空格，避免报表后面的列的数据错列
					buff.append(YssFun.formatNumber(rs.getDouble("FDxfIn"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FDxfIn_USD"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSettlement_RMB"),"#,##0.00")).append("\t");
					buff.append(YssFun.formatNumber(rs.getDouble("FSettlement_USD"),"#,##0.00")).append("\t");
					buff.append(rs.getString("FRemark")).append("\r\n");
					
				}
				else if(sRepCode.trim().equalsIgnoreCase("REP_HG_WGJ_JWZQTZ")){
					
					StringBuffer buf =new StringBuffer();
					buf = buf.append(" select a1.*,a2.cost2, a2.Mvalue2, a3.cost3, a3.Mvalue3, ")
					          .append(" a4.cost4, a4.Mvalue4, a5.cost5, a5.Mvalue5, ")
					          .append(" a6.cost6, a6.Mvalue6, a7.cost7, a7.Mvalue7 , a8.Mvalue8, a9.Mvalue9,")
					          .append("  a10.Mvalue10,  a11.Mvalue11 , a12.Mvalue12,  a13.Mvalue13 ,")
					          .append("  a14.Mvalue14,  a15.Mvalue15 , a16.Mvalue16,  a17.Mvalue17,")
					          .append("  a18.Mvalue18,  a19.Mvalue19 , a20.Mvalue20,  a21.Mvalue21, ")
					          .append("  a22.Mvalue22,  a23.Mvalue23 ").append(" from ")
					         .append(pub.yssGetTableName("tb_rep_WGJRep")).append(" a1 ");
					String[] keyList = {"","","货币市场工具","债券投资","公司股票","基金","衍生产品","其他投资","投资市值合计",
							            "预付投资款","应收投资款","应收股利","应收利息","其他应收款","资产合计","应付投资款","应付托管费",
							            "应付管理费","应付佣金","应交税金","其他应付款","负债合计","净资产","所托管人民币资金存款余额"};
					for(int i=2;i<=keyList.length-1;i++){
						buf.append(" left join (select FDxfOut_USD as cost").append(i)
						    .append(", FDxfIn_USD as Mvalue").append(i).append(", FrepCode, FstartDate from ")
						    .append(pub.yssGetTableName("tb_rep_WGJRep")).append(" where FkeyName =").append(dbl.sqlString(keyList[i]))
						    .append(") a").append(i).append(" on a1.frepcode = a").append(i).append(".FrepCode and a1.fstartDate =a")
						    .append(i).append(".fstartDate ");
						    
					}
					buf.append(" where a1.FRepCode = ").append(dbl.sqlString(this.sRepCode)).append(" and a1.FstartDate =")
					    .append(dbl.sqlDate(this.startDate)).append(" and a1.fkeyname = '银行存款'");
					rs = dbl.queryByPreparedStatement(buf.toString());
					while(rs.next()){
						buff.append(rs.getString("forgcode")).append("\t");
						buff.append(rs.getString("forgName")).append("\t");
						buff.append(rs.getString("FproductName")).append("\t");
						buff.append(YssFun.formatDate(rs.getDate("FstartDate"),"yyyy年MM月")).append("\t");
						buff.append("USD").append("\t");
						buff.append(" ").append("\t");	//20130308 added by liubo.Bug #7276.查询的时候需要给隐藏的币种名称列加上一个空格，避免报表后面的列的数据错列
						buff.append(YssFun.formatNumber(rs.getDouble("FDxfOut_USD"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("COST2"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue2"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("COST3"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue3"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("COST4"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue4"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("COST5"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue5"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("COST6"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue6"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("COST7"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue7"),"#,##0.00")).append("\t");
						
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue8"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue9"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue10"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue11"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue12"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue13"),"#,##0.00")).append("\t");
						
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue14"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue15"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue16"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue17"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue18"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue19"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue20"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue21"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue22"),"#,##0.00")).append("\t");
						buff.append(YssFun.formatNumber(rs.getDouble("Mvalue23"),"#,##0.00")).append("\t");
						buff.append(" ").append("\r\t");
					}					
				}
			}			
		}
		catch(Exception ye)
		{
			throw new YssException("查询外管局预报出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
				
		return buff.toString();
	}
	
	/**
	 * 20130623 added by liubo.Story #4000.需求北京-(农业银行)QDIIV4(高)20130527001
	 * 旧版外管局月报的报表查询的方法
	 * @return
	 * @throws YssException
	 */
	private String SearchRepManagerMonth() throws YssException 
	{
		String strSql = "";
		StringBuffer bufRep = new StringBuffer();
		ResultSet rs = null;
		
		try
		{
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_OverseasSecRep") +
					 " where FPortCode = " + dbl.sqlString(this.portCode) + 
					 " and FYearMonth = " + dbl.sqlString(YssFun.formatDate(this.endDate,"yyyyMM"));
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				bufRep.append(YssFun.formatNumber(rs.getDouble("FYHCKYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FYHCKFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FYHCKYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FHBSCYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FHBSCFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FHBSCYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZQYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZQFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZQYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FGPYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FGPFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FGPYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FJJYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FJJFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FJJYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FYSCPYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FYSCPFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FYSCPYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FQTTZYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FQTTZFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FQTTZYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FTZQKHJYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FTZQKHJFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FTZQKHJYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FCNYCKYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FCNYCKFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FCNYCKYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FWHCNYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FWHCNFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FWHCNYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FGHYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FGHFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FGHYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FJWTGZHHRYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FJWTGZHHRFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FJWTGZHHRYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FSGKHRYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FSGKHRFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FSGKHRYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FLXSRYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FLXSRFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FLXSRYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FQTSRYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FQTSRFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FQTSRYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZHSRHJYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZHSRHJFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZHSRHJYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FJHYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FJHFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FJHYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FHWJWTGYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FHWJWTGFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FHWJWTGYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZFSHKYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZFSHKFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZFSHKYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FFHYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FFHFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FFHYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FTGFYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FTGFFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FTGFYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FGLFYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FGLFFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FGLFYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FSXFYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FSXFFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FSXFYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FQTZCYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FQTZCFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FQTZCYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZHZCHJYueChu"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZHZCHJFaSheng"),"#,##0.00")).append("\t");
				bufRep.append(YssFun.formatNumber(rs.getDouble("FZHZCHJYueMo"),"#,##0.00")).append("\r\n");
				bufRep.append(rs.getString("FDesc")).append("\r\n");
			}
		}
		catch(Exception ye)
		{
			throw new YssException("查询【境内机构投资者境外证券投资月报表】出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return bufRep.toString();
	}
	
	
	//20130222 added by liubo.Story #3517.
	//客户要求外管局月报按模板导出的文件的命名规则为：日期+“-”+托管行代码
	//这个文件名需要在后台生成，然后返回给前台
	public String getRepExpFileName() throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		
		sReturn = YssFun.formatDate(this.endDate, "yyyyMM") + "-";
		
		try
		{
			
			strSql = "select m.fportName as productName,a1.FTrusteeCode as FTrusteeCode,a1.FTrusteeName as FTrusteeName,a1.frelatype " +
               		 " from " + pub.yssGetTableName("Tb_Para_Portfolio") + " m " +
               		 " left join (select b.fportCode, e.FTrusteeCode, e.FTrusteeName,b.frelatype " +
                     " from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " b " +
                     " join (select d.*, forgcode, FAffCorpName " +
                     " from " + pub.yssGetTableName("Tb_Para_Trustee") + " d " +
                     " join (select faffcorpcode,forgcode,FAffCorpName " +
                     " from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                     " where Fcheckstate = 1) c on c.faffcorpcode = d.ftrusteecode) e on b.FSubCode =e.ftrusteecode " +
                     " where b.fportCode = " + dbl.sqlString(this.portCode) +
                     " and b.FRelaType = 'Trustee' and b.Frelagrade = 'primary') a1 on m.fportcode = a1.fportcode " +
                     " where m.fportCode = " + dbl.sqlString(this.portCode);
			rs = dbl.queryByPreparedStatement(strSql);
			
			if (rs.next())
			{
				sReturn += rs.getString("FTrusteeCode");
			}
			
		}
		catch(Exception ye)
		{
			throw new YssException("获取外管局月报导出文件名出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return sReturn;
	}
	
}
