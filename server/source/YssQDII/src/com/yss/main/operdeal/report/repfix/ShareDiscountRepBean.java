package com.yss.main.operdeal.report.repfix;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import com.yss.core.util.YssD;
import com.yss.dsub.BaseBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.valuation.LeverGradeFundCfg;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**liubo 20130531 STORY 3759 杠杆分级份额转算底稿的生成类 */
/**此报表生成逻辑非常复杂，且基本无文档描述单元格取数规则和生成逻辑，只有一份由QD需求组张培自己整理的大概取数逻辑*/
/**如有需要，可找我或者找QD需求组张培索取
 * 另报表生成的主方法中，如果对于hashtable中的key值的含义不理解，可以查看这个类中，initArrayList方法的注释
 * */

public class ShareDiscountRepBean extends BaseBuildCommonRep
{
	
	protected CommonRepBean repBean;		//报表填充基础类
	
	protected java.util.Date dDate = null;	//估值日期
	protected String sPortCode = "";		//组合代码
	protected String sInvokeType = "0";		//操作类型。0为报表浏览界面查询，1为按模板导出。默认为0
	
	/**
	 * 初始化报表
	 */
    public void initBuildReport(BaseBean bean) throws YssException {
        repBean = (CommonRepBean) bean;
        //解析前台传入的条件字符串
        this.parseRowStr(this.repBean.getRepCtlParam());
    }
	
    /**
     * 解析报表请求字符串，并给各控件赋值
     * @param sRowStr
     * @throws YssException
     */
	public void parseRowStr(String sRowStr) throws YssException {
		try {
            if (sRowStr.equals("")) {
                return;
            }
            String reqAry[] = null;
            reqAry = sRowStr.split("\n"); 
            this.dDate = (YssFun.isDate(reqAry[0].split("\r")[1]) ? YssFun.toDate(reqAry[0].split("\r")[1]) 
            		: new java.util.Date());			//估值日期
            this.sPortCode = reqAry[1].split("\r")[1];	//组合代码
            if(reqAry.length >= 3)
            {
            	sInvokeType = reqAry[2].split("\r")[1];	//操作类型
            }
        } catch (Exception e) {
            throw new YssException("解析报表创建条件出错！", e);
        }
	}
	
	/**
	 * 检查报表查询日期是否是所选组合的折算日。若不是折算日，则不能查询折算底稿报表
	 * @return	返回是，则可以正常查询报表；返回否，则日期不是折算日，不能查询
	 * @throws YssException
	 */
	private boolean checkDiscountDate() throws YssException
	{
		boolean bReturn = false;
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			strSql = "select count(*) as cnt from " + pub.yssGetTableName("tb_ta_levershare") + " where FCONVERSIONDATE = " +
					  dbl.sqlDate(this.dDate) + " and FPortCode = " + dbl.sqlString(this.sPortCode);
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				if (rs.getInt("cnt") > 0)
				bReturn = true;
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
		
		
		return bReturn;
	}
	
	
	
	/**
	 * 报表生成的主方法。
     * @throws YssException
	 */
	public String buildReport(String sType) throws YssException
	{
		StringBuffer bufRep = new StringBuffer();		//存储报表生成字符串
		String strSql = "";
		ResultSet rs = null;
		
		Hashtable<String, String> hsResults = new Hashtable<String, String>();	//存储查询时，报表的各个单元格数据
		Hashtable<String, String> hsRepExp = new Hashtable<String, String>();	//存储导出时，报表的各个单元格数据
		
		ArrayList<String> arControlList = new ArrayList<String>();	//存储报表所有单元格的集合

		//杠杆分级基金算法配置。此类用于计算A类的折算前后的单位净值
        LeverGradeFundCfg leverCfg = new LeverGradeFundCfg();		
		
		try
		{
			//查询报表日期是否为所选组合的折算日，若不是，则不能查询报表
			//=======================================
			if (!checkDiscountDate())
			{
				throw new YssException("对不起，日期【" + YssFun.formatDate(this.dDate) + "】不是" +
						"组合【" + this.sPortCode + "】的折算日！请核实后再查询报表！");
			}
			//==============end=========================
			
			initHashTable(hsResults);			//初始化查询报表的Hashtable
			initHashTable(hsRepExp);		//初始化导出报表的Hashtable
			initArrayList(arControlList);		//初始化单元格集合的Arraylist

			leverCfg.setYssPub(pub);
			
			//报表的头两行的固定信息
			//============================
			bufRep.append(YssFun.formatDate(this.dDate)).append("定期折算前信息").append("\r\n");
			bufRep.append("科    目").append("\t").append("数    量").append("\t");
			bufRep.append("净    值").append("\t").append("单位净值").append("\r\n");
			bufRep.append("实收基金").append("\t");
			//===========end=================
			
			//获取四大类的折算前数量
			//母基金的数量 = 净值数据表的实收基金 - TA交易拆分数据
			//基础类的数量 = 基础类TA库存数量- TA交易拆分数据
			//A类的数量 = A类的TA库存数量
			//B类的数量 = B类的TA库存数量
			//==============================================
			strSql = "select 'PortAmount' as ftype, " +
					 " Sum(FStorageAmount) - sum(NVl(b.FSellAmount, 0)) as FStorageAmount" +
					 " from (select Sum(FStorageAmount) as FStorageAmount from " + pub.yssGetTableName("tb_stock_ta") +
					 " where fstoragedate = " + dbl.sqlDate(dDate) +
					 " and FPortCode = " + dbl.sqlString(this.sPortCode) +
					 " and FCheckState = 1) a " +
					 " left join (select sum(FSellAmount) as FSellAmount " +
					 " from  " + pub.yssGetTableName("tb_ta_trade") + 
					 " where FSellType = '09' " +
					 " and FTradeDate = " + dbl.sqlDate(dDate) + ") b on 1 = 1" +
					 " union all " +
					 " select 'BaseClassAmount' as ftype," +
					 " (Sum(FStorageAmount) - sum(NVl(b.FSellAmount,0))) as FStorageAmount from " + 
					 pub.yssGetTableName("tb_stock_ta") + " a " +
					 " left join (select  FSellAmount from " + pub.yssGetTableName("tb_ta_trade") +
					 " where FSellType = '09' and FTradeDate = " + dbl.sqlDate(this.dDate) + ") b " +
					 " on 1 = 1 " + 
					 " where fstoragedate = " + dbl.sqlDate(this.dDate) +
					 " and FPortCode = " + dbl.sqlString(this.sPortCode) +
					 " and FPortClsCode in (select FPORTCLSCODE from " + pub.yssGetTableName("tb_ta_portcls") +
					 " where FPortCode = " + dbl.sqlString(sPortCode) + " and FShareCategory = '3' and FCheckState = 1) " +
					 " and FCheckState = 1 " +
					 " union all  " +
					 " select 'AClassAmount' as ftype,Sum(FStorageAmount) as FStorageAmount  from " + 
					 pub.yssGetTableName("tb_stock_ta") + 
					 " where fstoragedate = " + dbl.sqlDate(this.dDate) +
					 " and FPortCode = " + dbl.sqlString(this.sPortCode) +
					 " and FPortClsCode in (select FPORTCLSCODE from " + pub.yssGetTableName("tb_ta_portcls") +
					 " where FPortCode = " + dbl.sqlString(sPortCode) + " and FShareCategory = '1' and FCheckState = 1) " +
					 " and FCheckState = 1 " +
					 " union all " +
					 " select 'BClassAmount' as ftype,Sum(FStorageAmount) as FStorageAmount from " + 
					 pub.yssGetTableName("tb_stock_ta") + 
					 " where fstoragedate = " + dbl.sqlDate(this.dDate) +
					 " and FPortCode = " + dbl.sqlString(this.sPortCode) +
					 " and FPortClsCode in (select FPORTCLSCODE from " + pub.yssGetTableName("tb_ta_portcls") +
					 " where FPortCode = " + dbl.sqlString(sPortCode) + " and FShareCategory = '2' and FCheckState = 1) " +
					 " and FCheckState = 1";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				//获取母基金的数量
				if(rs.getString("FType").equals("PortAmount"))
				{
					hsResults.put("Received_Amount_Before", 
							YssFun.formatNumber(rs.getDouble("FStorageAmount"), "#,##0.00"));
					hsRepExp.put("Received_Amount_Before", 
							YssFun.formatNumber(rs.getDouble("FStorageAmount"), "#,##0.00"));
				}
				//获取基础类的数量
				else if(rs.getString("FType").equals("BaseClassAmount"))
				{
					hsResults.put("Class_Base_Amount_Before", 
							YssFun.formatNumber(rs.getDouble("FStorageAmount"), "#,##0.00"));
					hsRepExp.put("Class_Base_Amount_Before", 
							YssFun.formatNumber(rs.getDouble("FStorageAmount"), "#,##0.00"));
				}
				//获取A类的数量
				else if(rs.getString("FType").equals("AClassAmount"))
				{
					hsResults.put("Class_A_Amount_Before", 
							YssFun.formatNumber(rs.getDouble("FStorageAmount"), "#,##0.00"));
					hsRepExp.put("Class_A_Amount_Before", 
							YssFun.formatNumber(rs.getDouble("FStorageAmount"), "#,##0.00"));
				}
				//获取B类的数量
				else if(rs.getString("FType").equals("BClassAmount"))
				{
					hsResults.put("Class_B_Amount_Before", 
							YssFun.formatNumber(rs.getDouble("FStorageAmount"), "#,##0.00"));
					hsRepExp.put("Class_B_Amount_Before", 
							YssFun.formatNumber(rs.getDouble("FStorageAmount"), "#,##0.00"));
				}
			}
			//====================end==========================
			
			dbl.closeResultSetFinal(rs);
			
			//获取母基金折算前的资产净值
			//母基金折算前资产净值 = 净值表当天的净值
			//===============================
			strSql = "select FPortMarketValue from " + pub.yssGetTableName("tb_data_navdata") +
					 " where FPortCode = " + dbl.sqlString(this.sPortCode) + 
					 " and FNavDate = " + dbl.sqlDate(this.dDate) + 
					 " and FKeyCode = 'TotalValue'";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				hsResults.put("Received_NetValue_Before", 
						YssFun.formatNumber(rs.getDouble("FPortMarketValue"), "#,##0.00"));
				hsRepExp.put("Received_NetValue_Before", 
						YssFun.formatNumber(rs.getDouble("FPortMarketValue"), "#,##0.00"));
			}
			//================end===============
			
			dbl.closeResultSetFinal(rs);
			
			//获取母基金折算前的单位净值
			//母基金折算前单位净值  = 母基金折算前净值/母基金折算前数量
			//===================================
			hsResults.put("Received_UnitValue_Before", 
					YssFun.formatNumber(
					YssFun.roundIt(
					YssD.div(YssFun.toDouble(hsResults.get("Received_NetValue_Before")),
							YssFun.toDouble(hsResults.get("Received_Amount_Before"))), 14),"#,##0.00000000000000"));
			hsRepExp.put("Received_UnitValue_Before", "=ROUND(C3/B3,14)");
			//==============end=====================

			//获取折算前A类单位净值
			//累计收益起始日：max{基金成立日，最近一次折算基准日}(需要排除本日09拆分TA数据)
			//累计收益起始日～估值日中的 收益率变化、年天数变化
			//==============================
            strSql = " select distinct t.FPortClsCode,t.FShareCategory,t.FConvention,t.FPeriod,t.FDailyNav," +
            		" t.FAfterDiscountNav, t.FAfterDiscountAmount, s.FBeanId, t.fshowitem,t.FOFFSET,c.FBaseDate  " +
            		" from " + pub.yssGetTableName("tb_ta_portcls") +  
            		" t join Tb_Base_CalcInsMetic b on t.FDailyNav = b.FCIMCode " +
            		" join  tb_fun_spinginvoke s on b.fspicode = s.fsicode"+
            		" left join (select FPortCode,max(FBaseDate) as FBaseDate from tb_001_ta_levershare " +
            		" where FCONVERSIONDATE <= to_date('20130203','yyyymmdd') " +
            		" group by FPortCode) c  " +
            		" on t.fportcode = c.fportcode " + 
            		" where t.fportcode  = " + dbl.sqlString(this.sPortCode) + " and t.FCheckState = 1 " +
            		" and t.FShareCategory = 1" +
            		" order by FOFFSET, fportclscode ";
            rs = dbl.openResultSet(strSql);
            
            while(rs.next())
            {
	            leverCfg.init(rs.getString("FAfterDiscountAmount"),
	            		this.sPortCode,rs.getString("FPortClsCode"),dDate,"rep");
	            
            	hsResults.put("Class_A_UnitValue_Before", 
            			YssFun.formatNumber(leverCfg.getPriorClassNetValue(rs.getString("FPortClsCode")),
            			"#,##0.00000000000000"));

            	hsRepExp.put("Class_A_UnitValue_Before", 
            			leverCfg.getPriorClassNetValueForRepExp(rs.getString("FPortClsCode")));
            }
			//============end=================
            
            dbl.closeResultSetFinal(rs);

            //获取折算前B类单位净值
            //折算前B类单位净值 = （折算前母基金单位净值 -0.5*折算前A类单位净值）/0.5
            //=================================
			hsResults.put("Class_B_UnitValue_Before", 
					YssFun.formatNumber(
					YssFun.roundIt(YssD.div(YssFun.toDouble(hsResults.get("Received_UnitValue_Before")) - 
							YssD.mul(0.5, YssFun.toDouble(hsResults.get("Class_A_UnitValue_Before"))), 0.5), 14),
							"#,##0.00000000000000"));
			hsRepExp.put("Class_B_UnitValue_Before", "=ROUND((D3-0.5*D5)/0.5,14)");
            //================end=================
            
			//获取折算前A类净值
			//折算前A类净值=折算前A类单位净值*折算前A类数量
			//===========================
            hsResults.put("Class_A_NetValue_Before", YssFun.formatNumber(
            		YssFun.roundIt(
            		YssD.mul(YssFun.toDouble(hsResults.get("Class_A_Amount_Before")), 
            				YssFun.toDouble(hsResults.get("Class_A_UnitValue_Before"))),14),"#,##0.00"));
			hsRepExp.put("Class_A_NetValue_Before", "=ROUND(B5*D5,2)");
			//=============end==============

			//获取折算前B类净值
			//折算前B类净值=折算前B类单位净值*折算前B类数量
			//===========================
            hsResults.put("Class_B_NetValue_Before", YssFun.formatNumber(
            		YssFun.roundIt(YssD.mul(
            				YssFun.toDouble(hsResults.get("Class_B_Amount_Before")), 
            				YssFun.toDouble(hsResults.get("Class_B_UnitValue_Before"))), 14),"#,##0.00"));
            hsRepExp.put("Class_B_NetValue_Before", "=ROUND(B6*D6,2)");
			//==============end=============
            
            //获取折算前基础类净值
            //折算前基础类净值=折算前母基金净值-折算前A类净值-折算前B类净值
			//===========================
            hsResults.put("Class_Base_NetValue_Before",YssFun.formatNumber(
            		YssFun.roundIt(YssFun.toDouble(hsResults.get("Received_NetValue_Before")) - 
            				YssFun.toDouble(hsResults.get("Class_A_NetValue_Before")) - 
            				YssFun.toDouble(hsResults.get("Class_B_NetValue_Before")), 14),"#,##0.00"));
            hsRepExp.put("Class_Base_NetValue_Before", "=C3-C5-C6");
			//=============end==============
            
            //获取折算前基础类单位净值
            //折算前基础类单位净值=折算前基础类净值/折算前基础类数量
			//===========================
            hsResults.put("Class_Base_UnitValue_Before", YssFun.formatNumber(
            		YssFun.roundIt(
            		YssD.div(YssFun.toDouble(hsResults.get("Class_Base_NetValue_Before")), 
            				YssFun.toDouble(hsResults.get("Class_Base_Amount_Before"))), 14),"#,##0.00000000000000"));
            hsRepExp.put("Class_Base_UnitValue_Before", "=ROUND(C4/B4,14)");
			//=============end==============
            
            //获取折算后母基金数量
            //折算后母基金数量=折算前母基金数量
			//===========================
            hsResults.put("Received_Amount_Discount",YssFun.formatNumber(
            		YssFun.roundIt(YssFun.toDouble(hsResults.get("Received_Amount_Before")), 2),"#,##0.00"));
            hsRepExp.put("Received_Amount_Discount", YssFun.formatNumber(
            		YssFun.roundIt(YssFun.toDouble(hsResults.get("Received_Amount_Before")), 2),"#,##0.00"));
			//=============end==============

            //获取折算后母基金净值
            //折算后母基金净值=折算前母基金净值
			//===========================
            hsResults.put("Received_NetValue_Discount",YssFun.formatNumber(
            		YssFun.roundIt(YssFun.toDouble(hsResults.get("Received_NetValue_Before")), 2),"#,##0.00"));
            hsRepExp.put("Received_NetValue_Discount", YssFun.formatNumber(
            		YssFun.roundIt(YssFun.toDouble(hsResults.get("Received_NetValue_Before")), 2),"#,##0.00"));
			//==============end=============
            
            //获取折算后母基金单位净值
            //折算后母基金单位净值=折算前母基金单位净值
			//===========================
            hsResults.put("Received_UnitValue_Discount",YssFun.formatNumber(
            		YssFun.roundIt(
            				YssFun.toDouble(hsResults.get("Received_UnitValue_Before")), 14),"#,##0.00000000000000"));
            hsRepExp.put("Received_UnitValue_Discount", YssFun.formatNumber(
            		YssFun.roundIt(
            				YssFun.toDouble(hsResults.get("Received_UnitValue_Before")), 14),"#,##0.00000000000000"));
			//============end===============
            
            //获取折算后基础类数量
            //折算后基础类数量=折算前基础类数量
			//===========================
    		hsResults.put("Class_Base_Amount_Discount", YssFun.formatNumber(
    				YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_Base_Amount_Before")), 2),"#,##0.00"));
            hsRepExp.put("Class_Base_Amount_Discount", YssFun.formatNumber(
    				YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_Base_Amount_Before")), 2),"#,##0.00"));
			//==============end=============
    		
            //获取折算后基础类场内、场外的数量（份额）
            //折算后场内、场外数量（份额）在【杠杆分级份额折算】界面进行设置
			//===========================
    		strSql = "select * from " + pub.yssGetTableName("tb_ta_levershare") +
    				 " where FPortCode = " + dbl.sqlString(this.sPortCode) +
    				 " and FConversionDate = " + dbl.sqlDate(this.dDate);
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
    			hsResults.put("Floor_Amount_Discount",YssFun.formatNumber(
    					YssFun.roundIt(rs.getDouble("FFLOORSHARE"), 2),"#,##0.00"));
    			hsResults.put("OTC_Amount_Discount",YssFun.formatNumber(
    					YssFun.roundIt(rs.getDouble("FOTCSHARE"), 2),"#,##0.00"));

                hsRepExp.put("Floor_Amount_Discount", YssFun.formatNumber(
    					YssFun.roundIt(rs.getDouble("FFLOORSHARE"), 2),"#,##0.00"));
                hsRepExp.put("OTC_Amount_Discount", YssFun.formatNumber(
    					YssFun.roundIt(rs.getDouble("FOTCSHARE"), 2),"#,##0.00"));
    		}
			//============end===============
    		
			dbl.closeResultSetFinal(rs);
			
			//获取折算后A类数量
			//折算后A类数量=折算前A类数量
			//===========================
			hsResults.put("Class_A_Amount_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_A_Amount_Before")), 2),"#,##0.00"));
			hsRepExp.put("Class_A_Amount_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_A_Amount_Before")), 2),"#,##0.00"));
			//=============end==============
			
			//获取折算后B类数量
			//折算后B类数量=折算前B类数量
			//===========================
			hsResults.put("Class_B_Amount_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_B_Amount_Before")), 2),"#,##0.00"));
			hsRepExp.put("Class_B_Amount_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_B_Amount_Before")), 2),"#,##0.00"));
			//===========end================

			//获取折算后基础类单位净值
			//折算后基础类单位净值=折算前基础类单位净值
			//===========================
			hsResults.put("Class_Base_UnitValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_Base_UnitValue_Before")), 14),
					"#,##0.00000000000000"));
			hsRepExp.put("Class_Base_UnitValue_Discount", "=D4");
			//===============end============

			//获取折算后场内单位净值
			//折算后场内单位净值=折算前基础类单位净值
			//===========================
			hsResults.put("Floor_UnitValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_Base_UnitValue_Before")), 14),
					"#,##0.00000000000000"));
			hsRepExp.put("Floor_UnitValue_Discount", "=D4");
			//==============end=============

			//获取折算后场外单位净值
			//折算后场外单位净值=折算前基础类单位净值
			//===========================
			hsResults.put("OTC_UnitValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_Base_UnitValue_Before")), 14),
					"#,##0.00000000000000"));
			hsRepExp.put("OTC_UnitValue_Discount", "=D4");
			//===========end================

			//获取折算后A类单位净值
			//折算后A类单位净值=折算前A类单位净值
			//===========================
			hsResults.put("Class_A_UnitValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_A_UnitValue_Before")), 14),
					"#,##0.00000000000000"));
			hsRepExp.put("Class_A_UnitValue_Discount", "=D5");
			//=============end==============

			//获取折算后B类单位净值
			//折算后B类单位净值=折算前B类单位净值
			//===========================
			hsResults.put("Class_B_UnitValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_B_UnitValue_Before")), 14),
					"#,##0.00000000000000"));
			hsRepExp.put("Class_B_UnitValue_Discount", "=D6");
			//===============end============

			//获取折算后基础类净值
			//折算后基础类净值=折算前基础类净值
			//===========================
			hsResults.put("Class_Base_NetValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_Base_NetValue_Before")), 14),
					"#,##0.00"));
			hsRepExp.put("Class_Base_NetValue_Discount", "=C9-C13-C14");
			//============end===============

			//获取折算后A类净值
			//折算后A类净值=折算后A类数量*折算后A类单位净值。
			//因为折算后A类数量=折算前A类数量，折算后A类单位净值=折算前A类单位净值。因此实际上折算后A类净值=折算前A类净值
			//===================================
			hsResults.put("Class_A_NetValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_A_NetValue_Before")), 2),
					"#,##0.00"));
			hsRepExp.put("Class_A_NetValue_Discount", "=ROUND(D13*B13,2)");
			//=================end==================

			//获取折算后B类净值
			//折算后B类净值=折算后B类数量*折算后B类单位净值。
			//因为折算后B类数量=折算前B类数量，折算后B类单位净值=折算前B类单位净值。因此实际上折算后B类净值=折算前B类净值
			//===================================
			hsResults.put("Class_B_NetValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_B_NetValue_Before")), 2),
					"#,##0.00"));
			hsRepExp.put("Class_B_NetValue_Discount", "=ROUND(B14*D14,2)");
			//================end===================

			//获取折算后场内净值
			//折算后场内净值=折算后场内数量/折算后基础类数量*折算后基础类净值
			//===================================
			hsResults.put("Floor_NetValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssD.mul(YssD.div(YssFun.toDouble(hsResults.get("Floor_Amount_Discount")), 
							YssFun.toDouble(hsResults.get("Class_Base_Amount_Discount"))),
							YssFun.toDouble(hsResults.get("Class_Base_NetValue_Discount"))), 2),"#,##0.00"));
			hsRepExp.put("Floor_NetValue_Discount", "=ROUND(B11/B10*C10,2)");
			//================end===================
			
			//获取折算后场外净值
			//折算后场内净值=折算后场外数量/折算后基础类数量*折算后基础类净值
			//===================================
			hsResults.put("OTC_NetValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssD.mul(YssD.div(YssFun.toDouble(hsResults.get("OTC_Amount_Discount")), 
							YssFun.toDouble(hsResults.get("Class_Base_Amount_Discount"))),
							YssFun.toDouble(hsResults.get("Class_Base_NetValue_Discount"))), 2),"#,##0.00"));
			hsRepExp.put("OTC_NetValue_Discount", "=ROUND(B12/B10*C10,2)");
			//==================end=================
			
			double dUnitValue_BaseDate = 0;	//在折算基准日，基础类的单位净值，从多CLASS净值表中获取
			
			//获取基础类除息后净值
			//基础类除息后净值=round(（基础类净值/（基准日A类单位净值-1）*折算后基础类数量/2）/折算后基础类数量)
			//===================================
			strSql = "select Nvl(sum(FClassNetValue),0) as FClassNetValue " +
					 " from " + pub.yssGetTableName("tb_data_multiclassnet") +
					 " where FNavDate in (select FBaseDate from " + pub.yssGetTableName("TB_ta_LeverShare") +
					 " where FConversionDate = " + dbl.sqlDate(this.dDate) + 
					 " and FPortCode = " + dbl.sqlString(this.sPortCode) + ")" +
					 " and FPortCode = " + dbl.sqlString(this.sPortCode) +
					 " and FCuryCode in (select FPortClsCode from  " + pub.yssGetTableName("tb_ta_portcls") +
					 " where FPortCode = " + dbl.sqlString(this.sPortCode) + " and FShareCategory = '1') " +
					 " and FType = '022'";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				dUnitValue_BaseDate = rs.getDouble("FClassNetValue");
				
				double dResult = rs.getDouble("FClassNetValue") - 1;
				
				dResult = YssD.div(YssD.mul(dResult, YssFun.toDouble(hsResults.get("Class_Base_Amount_Discount"))),
						2);
				
				dResult = YssD.sub(YssFun.toDouble(hsResults.get("Class_Base_NetValue_Discount")), dResult);
				
				hsResults.put("Class_Base_ExdividendUnit_Discount", YssFun.formatNumber(
						YssD.div(dResult, YssFun.toDouble(hsResults.get("Class_Base_Amount_Discount"))),
						"#,##0.00000000000000"));
			}
			hsRepExp.put("Class_Base_ExdividendUnit_Discount", 
					"=ROUND((C10-(" + YssFun.formatNumber(dUnitValue_BaseDate, "0.##############") + "-1)*B10/2)/B10,14)");
			//===============end===================
			
			dbl.closeResultSetFinal(rs);

			//获取母基金除息后净值
			//母基金出席后净值=基础类除息后净值
			//====================================
			hsResults.put("Received_ExdividendUnit_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_Base_ExdividendUnit_Discount")), 14),
					"#,##0.00000000000000"));
			hsRepExp.put("Received_ExdividendUnit_Discount", "=E10");
			//==================end==================

			//获取场内除息后净值
			//场内出席后净值=基础类除息后净值
			//====================================
			hsResults.put("Floor_ExdividendUnit_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_Base_ExdividendUnit_Discount")), 14)
					,"#,##0.00000000000000"));
			hsRepExp.put("Floor_ExdividendUnit_Discount", "=E10");
			//=================end===================

			//获取场外除息后净值
			//场外出席后净值=基础类除息后净值
			//====================================
			hsResults.put("OTC_ExdividendUnit_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_Base_ExdividendUnit_Discount")), 14)
					,"#,##0.00000000000000"));
			hsRepExp.put("OTC_ExdividendUnit_Discount", "=E10");
			//===================end=================

			//获取A类除息后净值
			//A类除息后净值=累计收益起始日：max{基金成立日，最近一次折算基准日}
			//累计收益起始日～估值日中的 收益率变化、年天数变化
			//====================================
            strSql = " select distinct t.FPortClsCode,t.FShareCategory,t.FConvention,t.FPeriod,t.FDailyNav," +
            		" t.FAfterDiscountNav, t.FAfterDiscountAmount, s.FBeanId, t.fshowitem,t.FOFFSET, " +
            		" lever.FBaseDate " +
            		" from " + pub.yssGetTableName("tb_ta_portcls") +  
            		" t join Tb_Base_CalcInsMetic b on t.FDailyNav = b.FCIMCode " +
            		" join  tb_fun_spinginvoke s on b.fspicode = s.fsicode"+
            		" left join (select * from " + pub.yssGetTableName("TB_ta_LeverShare") +
            		" where FPortCode = " + dbl.sqlString(this.sPortCode) + " " +
            		" and FConversionDate = " + dbl.sqlDate(dDate) + ") lever on 1 = 1" +
            		" where t.fportcode  = " + dbl.sqlString(this.sPortCode) + " and t.FCheckState = 1 " +
            		" and t.FShareCategory = 1" +
            		" order by FOFFSET, fportclscode ";
            rs = dbl.openResultSet(strSql);
            
            while(rs.next())
            {
	            leverCfg.init(rs.getString("FAfterDiscountAmount"),this.sPortCode,
	            			rs.getString("FPortClsCode"),dDate,"normal");

            	hsResults.put("Class_A_ExdividendUnit_Discount", 
            			YssFun.formatNumber(leverCfg.getPriorClassNetValue(rs.getString("FPortClsCode")),
            			"#,##0.00000000000000"));

            	hsRepExp.put("Class_A_ExdividendUnit_Discount", 
            			leverCfg.getPriorClassNetValueForRepExp(rs.getString("FPortClsCode")));
            	
            }
			//=================end===================
            
            dbl.closeResultSetFinal(rs);
            
            //获取B类除息后净值
            //B类除息后净值=B类折算后单位净值
			//====================================
			hsResults.put("Class_B_ExdividendUnit_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssFun.toDouble(hsResults.get("Class_B_UnitValue_Discount")), 14),
					"#,##0.00000000000000"));
			hsRepExp.put("Class_B_ExdividendUnit_Discount", "=D14");
			//================end====================
			
			double dTemp = 0.000000000;
			
			//获取母基金折算比例
			//母基金折算比例=0
			//=============================
			hsResults.put("Received_DiscountRatio_Discount", "0.000000000");
			hsRepExp.put("Received_DiscountRatio_Discount", "0.000000000");
			//=================end===================
			
			//获取基础类折算比例
			//基础类折算比例=rounddown((折算基准日A类单位净值-1)/(基础类除息后净值*2))
			//====================================
			dTemp = YssD.div(dUnitValue_BaseDate - 1, YssFun.toDouble(hsResults.get("Class_Base_ExdividendUnit_Discount")) * 2);
			
			dTemp = roundDown(dTemp,9);
			
			hsResults.put("Class_Base_DiscountRatio_Discount", YssFun.formatNumber(dTemp, "#,##0.000000000"));
			hsRepExp.put("Class_Base_DiscountRatio_Discount", 
					"=ROUNDDOWN((" + YssFun.formatNumber(dUnitValue_BaseDate, "0.##############") + "-1)/(2*E10),9)");
			//=================end===================

			//获取场内折算比例
			//场内折算比例=基础类折算比例
			//====================================
			hsResults.put("Floor_DiscountRatio_Discount", YssFun.formatNumber(dTemp, "#,##0.000000000"));
			hsRepExp.put("Floor_DiscountRatio_Discount", "=F10");
			//==================end==================

			//获取场外折算比例
			//场外折算比例=基础类折算比例
			//====================================
			hsResults.put("OTC_DiscountRatio_Discount", YssFun.formatNumber(dTemp, "#,##0.000000000"));
			hsRepExp.put("OTC_DiscountRatio_Discount", YssFun.formatNumber(dTemp, "=F10"));
			//================end====================
			
			//获取A类除息后净值
			//A类除息后净值=round((基础类净值-(基准日A类单位净值-1)*折算后A类数量/2)，14)
			//====================================
			dTemp = YssD.div(dUnitValue_BaseDate - 1, 
					YssFun.toDouble(hsResults.get("Class_Base_ExdividendUnit_Discount")));
			
			dTemp = roundDown(dTemp,9);

			hsResults.put("Class_A_DiscountRatio_Discount", YssFun.formatNumber(dTemp, "#,##0.000000000"));
			hsRepExp.put("Class_A_DiscountRatio_Discount", 
					"=ROUNDDOWN((" + YssFun.formatNumber(dUnitValue_BaseDate, "0.##############") + "-1)/E10,9)");
			//===============end=====================

			//获取B类折算比例
			//B类折算比例=0
			//====================================
			hsResults.put("Class_B_DiscountRatio_Discount", "0.000000000");
			hsRepExp.put("Class_B_DiscountRatio_Discount", "0.000000000");
			//===============end=====================
			
			//获取母基金新增分级份额
			//母基金新增分级份额=0
			//====================================
			hsResults.put("Received_TheNewShare_Discount", "0.00");
			hsRepExp.put("Received_TheNewShare_Discount", "0.00");
			//=================end===================
			
			//获取场内新增分级份额
			//场内新增分级份额=场内数量*场内折算比例
			//====================================
			hsResults.put("Floor_TheNewShare_Discount", YssFun.formatNumber(
					roundDown(
					YssD.mul(YssFun.toDouble(hsResults.get("Floor_NetValue_Discount")), 
							YssFun.toDouble(hsResults.get("Floor_DiscountRatio_Discount"))), 0),"#,##0.00"));
			hsRepExp.put("Floor_TheNewShare_Discount", "=ROUNDDOWN(F11*B11,0)");
			//===============end=====================
			
			//获取场外新增分级份额
			//场外新增分级份额=场外数量*场外折算比例
			//====================================
			hsResults.put("OTC_TheNewShare_Discount", YssFun.formatNumber(
					roundDown(
					YssD.mul(YssFun.toDouble(hsResults.get("OTC_NetValue_Discount")), 
							YssFun.toDouble(hsResults.get("OTC_DiscountRatio_Discount"))), 2),"#,##0.00"));
			hsRepExp.put("OTC_TheNewShare_Discount", "=ROUNDDOWN(F12*B12,2)");
			//==================end==================

			//获取基础类新增分级份额
			//基础类新增分级份额=场内新增分级份额+场外新增分级份额
			//====================================
			hsResults.put("Class_Base_TheNewShare_Discount", YssFun.formatNumber(
					YssD.add(YssFun.toDouble(hsResults.get("Floor_TheNewShare_Discount")),
							YssFun.toDouble(hsResults.get("OTC_TheNewShare_Discount"))),"#,##0.00"));
			hsRepExp.put("Class_Base_TheNewShare_Discount", "=G11+G12");
			//=================end===================

			//获取A类新增分级份额
			//A类新增分级份额=rounddown(A类数量*A类折算比例,0)
			//====================================
			hsResults.put("Class_A_TheNewShare_Discount", YssFun.formatNumber(
					roundDown(
					YssD.mul(YssFun.toDouble(hsResults.get("Class_A_Amount_Discount")), 
							YssFun.toDouble(hsResults.get("Class_A_DiscountRatio_Discount"))), 0),"#,##0.00"));
			hsRepExp.put("Class_A_TheNewShare_Discount", "=ROUNDDOWN(F13*B13,0)");
			//=================end===================

			//获取B类新增分级份额
			//B类新增分级份额=0
			//====================================
			hsResults.put("Class_B_TheNewShare_Discount", "0.00");
			hsRepExp.put("Class_B_TheNewShare_Discount", "0.00");
			//================end====================
			
			//获取场内折算后数量
			//场内折算后数量=场内数量+场内新增分级份额+A类新增分级份额
			//====================================
			hsResults.put("Floor_DiscountAmount_Discount", YssFun.formatNumber(
					YssD.add(YssD.add(YssFun.toDouble(hsResults.get("Floor_Amount_Discount")),
							YssFun.toDouble(hsResults.get("Floor_TheNewShare_Discount"))), 
							YssFun.toDouble(hsResults.get("Class_A_TheNewShare_Discount"))),"#,##0.00"));
			hsRepExp.put("Floor_DiscountAmount_Discount", "=B11+G11+G13");
			//===============end=====================

			//获取场外折算后数量
			//场外折算后数量=场外数量+场外新增折算份额
			//====================================
			hsResults.put("OTC_DiscountAmount_Discount", YssFun.formatNumber(
					YssD.add(YssFun.toDouble(hsResults.get("OTC_Amount_Discount")),
							YssFun.toDouble(hsResults.get("OTC_TheNewShare_Discount"))),"#,##0.00"));
			hsRepExp.put("OTC_DiscountAmount_Discount", "=G12+B12");
			//==================end==================

			//获取基础类折算后数量
			//基础类折算后数量=场内折算后数量+场外折算后数量
			//====================================
			hsResults.put("Class_Base_DiscountAmount_Discount", YssFun.formatNumber(
					YssD.add(YssFun.toDouble(hsResults.get("Floor_DiscountAmount_Discount")),
							YssFun.toDouble(hsResults.get("OTC_DiscountAmount_Discount"))),"#,##0.00"));
			hsRepExp.put("Class_Base_DiscountAmount_Discount", "=H11+H12");
			//==================end==================
			
			//获取A类折算后数量
			//A类折算后数量=A类数量
			//====================================
			hsResults.put("Class_A_DiscountAmount_Discount", YssFun.formatNumber(
					YssFun.toDouble(hsResults.get("Class_A_Amount_Discount")),"#,##0.00"));
			hsRepExp.put("Class_A_DiscountAmount_Discount", "=B13");
			//=================end===================

			//获取B类折算后数量
			//B类折算后数量=B类数量
			//====================================
			hsResults.put("Class_B_DiscountAmount_Discount", YssFun.formatNumber(
					YssFun.toDouble(hsResults.get("Class_B_Amount_Discount")),"#,##0.00"));
			hsRepExp.put("Class_B_DiscountAmount_Discount", "=B14");
			//================end====================

			//获取母基金折算后数量
			//母基金折算后数量=基础类折算后数量+A类折算后数量+B类折算后数量
			//====================================
			hsResults.put("Received_DiscountAmount_Discount", YssFun.formatNumber(
					YssD.add(YssD.add(YssFun.toDouble(hsResults.get("Class_Base_DiscountAmount_Discount")),
					YssFun.toDouble(hsResults.get("Class_A_DiscountAmount_Discount"))), 
					YssFun.toDouble(hsResults.get("Class_B_DiscountAmount_Discount"))),"#,##0.00"));
			hsRepExp.put("Received_DiscountAmount_Discount", "=H10+H13+H14");
			//==================end==================

			//获取母基金净值
			//母基金净值=母基金净值
			//====================================
			hsResults.put("Received_DiscountNetValue_Discount", YssFun.formatNumber(
					YssFun.toDouble(hsResults.get("Received_NetValue_Discount")),"#,##0.00"));
			hsRepExp.put("Received_DiscountNetValue_Discount", "=C3");
			//===================end=================

			//获取母基金折算后单位净值
			//母基金单位净值=母基金折算后净值/母基金折算后数量
			//====================================
			hsResults.put("Received_DiscountUnitValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(
					YssD.div(YssFun.toDouble(hsResults.get("Received_DiscountNetValue_Discount")), 
							YssFun.toDouble(hsResults.get("Received_DiscountAmount_Discount"))),14),
							"#,##0.00000000000000"));
			hsRepExp.put("Received_DiscountUnitValue_Discount", "=ROUND(I9/H9,14)");
			//===================end=================

			//获取A类折算后单位净值
			//A类折算后单位净值=A类除息后净值
			//====================================
			hsResults.put("Class_A_DiscountUnitValue_Discount", YssFun.formatNumber(
					YssFun.toDouble(hsResults.get("Class_A_ExdividendUnit_Discount")),"#,##0.00000000000000"));
			hsRepExp.put("Class_A_DiscountUnitValue_Discount", "=E13");
			//===============end=====================

			//获取B类折算后单位净值
			//B类折算后单位净值=(母基金折算后单位净值-A类折算后单位净值*0.5)/0.5
			//====================================
			hsResults.put("Class_B_DiscountUnitValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(YssD.div(YssD.sub(YssFun.toDouble(hsResults.get("Received_DiscountUnitValue_Discount")), 
							YssFun.toDouble(hsResults.get("Class_A_DiscountUnitValue_Discount")) * 0.5), 0.5),14),
					"#,##0.00000000000000"));
			hsRepExp.put("Class_B_DiscountUnitValue_Discount", "=ROUND((J9-J13 * 0.5)/0.5,14)");
			//=================end===================

			//获取A类折算后净值
			//A类折算后净值=A类数量*A类折算后单位净值
			//====================================
			hsResults.put("Class_A_DiscountNetValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(
					YssD.mul(YssFun.toDouble(hsResults.get("Class_A_DiscountAmount_Discount")), 
							YssFun.toDouble(hsResults.get("Class_A_DiscountUnitValue_Discount"))),2),
					"#,##0.00"));
			hsRepExp.put("Class_A_DiscountNetValue_Discount", "=ROUND(H13*J13,2)");
			//==================end==================

			//获取B类折算后净值
			//B类折算后净值=B类数量*B类折算后单位净值
			//====================================
			hsResults.put("Class_B_DiscountNetValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(
					YssD.mul(YssFun.toDouble(hsResults.get("Class_B_DiscountAmount_Discount")), 
							YssFun.toDouble(hsResults.get("Class_B_DiscountUnitValue_Discount"))),14),
					"#,##0.00"));
			hsRepExp.put("Class_B_DiscountNetValue_Discount", "=ROUND(H14*J14,2)");
			//=================end===================

			//获取基础类折算后净值
			//基础类折算后净值=母基金折算后净值-A类折算后净值-B类折算后净值
			//====================================
			hsResults.put("Class_Base_DiscountNetValue_Discount", YssFun.formatNumber(
					YssD.sub(YssD.sub(YssFun.toDouble(hsResults.get("Received_DiscountNetValue_Discount")),
							YssFun.toDouble(hsResults.get("Class_A_DiscountNetValue_Discount"))), 
							YssFun.toDouble(hsResults.get("Class_B_DiscountNetValue_Discount")))
					,"#,##0.00"));
			hsRepExp.put("Class_Base_DiscountNetValue_Discount", "=I9-I13-I14");
			//==================end==================

			//获取场内折算后净值
			//场内折算后净值=场内折算后数量/基础类折算后数量*场内折算后单位净值
			//====================================
			hsResults.put("Floor_DiscountNetValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(
					YssD.mul(YssD.div(YssFun.toDouble(hsResults.get("Floor_DiscountAmount_Discount")),
					YssFun.toDouble(hsResults.get("Class_Base_DiscountAmount_Discount"))), 
					YssFun.toDouble(hsResults.get("Class_Base_DiscountNetValue_Discount"))),2),"#,##0.00"));
			hsRepExp.put("Floor_DiscountNetValue_Discount", "=ROUND(H11/H10*I10,2)");
			//================end====================

			//获取场外折算后净值
			//场外折算后净值=场外折算后数量/基础类折算后数量*场外折算后单位净值
			//====================================
			hsResults.put("OTC_DiscountNetValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(
					YssD.mul(YssD.div(YssFun.toDouble(hsResults.get("OTC_DiscountAmount_Discount")),
					YssFun.toDouble(hsResults.get("Class_Base_DiscountAmount_Discount"))), 
					YssFun.toDouble(hsResults.get("Class_Base_DiscountNetValue_Discount"))),2),"#,##0.00"));
			hsRepExp.put("OTC_DiscountNetValue_Discount", "=ROUND(H12/H10*I10,2)");
			//===============end=====================

			//获取基础类折算后单位净值
			//基础类折算后单位净值=基础类折算后净值/基础类折算后数量
			//====================================
			hsResults.put("Class_Base_DiscountUnitValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(
					YssD.div(YssFun.toDouble(hsResults.get("Class_Base_DiscountNetValue_Discount")), 
							YssFun.toDouble(hsResults.get("Class_Base_DiscountAmount_Discount"))),14),
					"#,##0.00000000000000"));
			hsRepExp.put("Class_Base_DiscountUnitValue_Discount", "=ROUND(I10/H10,14)");
			//===================end=================

			//获取场内折算后单位净值
			//场内折算后单位净值=	场内折算后净值/场内折算后数量
			//====================================
			hsResults.put("Floor_DiscountUnitValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(
					YssD.div(YssFun.toDouble(hsResults.get("Floor_DiscountNetValue_Discount")), 
							YssFun.toDouble(hsResults.get("Floor_DiscountAmount_Discount"))),14),
					"#,##0.00000000000000"));
			hsRepExp.put("Floor_DiscountUnitValue_Discount", "=ROUND(I11/H11,14)");
			//================end====================

			//获取场外折算后单位净值
			//场外折算后单位净值=场外折算后净值/场外折算后数量
			//====================================
			hsResults.put("OTC_DiscountUnitValue_Discount", YssFun.formatNumber(
					YssFun.roundIt(
					YssD.div(YssFun.toDouble(hsResults.get("OTC_DiscountNetValue_Discount")), 
							YssFun.toDouble(hsResults.get("OTC_DiscountAmount_Discount"))),14),
					"#,##0.00000000000000"));
			hsRepExp.put("OTC_DiscountUnitValue_Discount", "=ROUND(I12/H12,14)");
			//==================end==================
			
			//根据操作类型,如果是查询报表,返回表的数据集合;如果是导出报表,返回表的公式集合
			//====================================
			if (this.sInvokeType.equals("0"))
			{
				bufRep.append(setRepViewData(arControlList,hsResults));
			}
			else if (this.sInvokeType.equals("1"))
			{
				bufRep.append(setRepViewData(arControlList,hsRepExp));
			}
			//==============end======================
			
		}
		catch(Exception ye)
		{
			throw new YssException(ye);
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return bufRep.toString();
	}
	
	/**
	 * 初始化hashtable
	 * @param hsResult 需要被初始化的hashtable对象
	 */
	private void initHashTable(Hashtable<String, String> hsResult)
	{
		hsResult.put("Received_Amount_Before", "0.00");
		hsResult.put("Received_NetValue_Before", "0.00");
		hsResult.put("Received_UnitValue_Before", "0.00000000000000");

		hsResult.put("Class_Base_Amount_Before", "0.00");
		hsResult.put("Class_Base_NetValue_Before", "0.00");
		hsResult.put("Class_Base_UnitValue_Before", "0.00000000000000");

		hsResult.put("Class_A_Amount_Before", "0.00");
		hsResult.put("Class_A_NetValue_Before", "0.00");
		hsResult.put("Class_A_UnitValue_Before", "0.00000000000000");

		hsResult.put("Class_B_Amount_Before", "0.00");
		hsResult.put("Class_B_NetValue_Before", "0.00");
		hsResult.put("Class_B_UnitValue_Before", "0.00000000000000");
		

		hsResult.put("Received_Amount_Discount", "0.00");
		hsResult.put("Received_NetValue_Discount", "0.00");
		hsResult.put("Received_UnitValue_Discount", "0.00000000000000");
		hsResult.put("Received_ExdividendUnit_Discount", "0.00000000000000");
		hsResult.put("Received_DiscountRatio_Discount", "0.000000000");
		hsResult.put("Received_TheNewShare_Discount", "0.00");
		hsResult.put("Received_DiscountAmount_Discount", "0.00");
		hsResult.put("Received_DiscountNetValue_Discount", "0.00");
		hsResult.put("Received_DiscountUnitValue_Discount", "0.00000000000000");
		hsResult.put("Received_Creator_Discount", "0.00");

		hsResult.put("Class_Base_Amount_Discount", "0.00");
		hsResult.put("Class_Base_NetValue_Discount", "0.00");
		hsResult.put("Class_Base_UnitValue_Discount", "0.00000000000000");
		hsResult.put("Class_Base_ExdividendUnit_Discount", "0.00000000000000");
		hsResult.put("Class_Base_DiscountRatio_Discount", "0.000000000");
		hsResult.put("Class_Base_TheNewShare_Discount", "0.00");
		hsResult.put("Class_Base_DiscountAmount_Discount", "0.00");
		hsResult.put("Class_Base_DiscountNetValue_Discount", "0.00");
		hsResult.put("Class_Base_DiscountUnitValue_Discount", "0.00000000000000");
		hsResult.put("Class_Base_Creator_Discount", "0.00");

		hsResult.put("Floor_Amount_Discount", "0.00");
		hsResult.put("Floor_NetValue_Discount", "0.00");
		hsResult.put("Floor_UnitValue_Discount", "0.00000000000000");
		hsResult.put("Floor_ExdividendUnit_Discount", "0.00000000000000");
		hsResult.put("Floor_DiscountRatio_Discount", "0.000000000");
		hsResult.put("Floor_TheNewShare_Discount", "0.00");
		hsResult.put("Floor_DiscountAmount_Discount", "0.00");
		hsResult.put("Floor_DiscountNetValue_Discount", "0.00");
		hsResult.put("Floor_DiscountUnitValue_Discount", "0.00000000000000");
		hsResult.put("Floor_Creator_Discount", "0.00");

		hsResult.put("OTC_Amount_Discount", "0.00");
		hsResult.put("OTC_NetValue_Discount", "0.00");
		hsResult.put("OTC_UnitValue_Discount", "0.00000000000000");
		hsResult.put("OTC_ExdividendUnit_Discount", "0.00000000000000");
		hsResult.put("OTC_DiscountRatio_Discount", "0.000000000");
		hsResult.put("OTC_TheNewShare_Discount", "0.00");
		hsResult.put("OTC_DiscountAmount_Discount", "0.00");
		hsResult.put("OTC_DiscountNetValue_Discount", "0.00");
		hsResult.put("OTC_DiscountUnitValue_Discount", "0.00000000000000");
		hsResult.put("OTC_Creator_Discount", "0.00");
		
		hsResult.put("Class_A_Amount_Discount", "0.00");
		hsResult.put("Class_A_NetValue_Discount", "0.00");
		hsResult.put("Class_A_UnitValue_Discount", "0.00000000000000");
		hsResult.put("Class_A_ExdividendUnit_Discount", "0.00000000000000");
		hsResult.put("Class_A_DiscountRatio_Discount", "0.000000000");
		hsResult.put("Class_A_TheNewShare_Discount", "0.00");
		hsResult.put("Class_A_DiscountAmount_Discount", "0.00");
		hsResult.put("Class_A_DiscountNetValue_Discount", "0.00");
		hsResult.put("Class_A_DiscountUnitValue_Discount", "0.00000000000000");
		hsResult.put("Class_A_Creator_Discount", "0.00");

		hsResult.put("Class_B_Amount_Discount", "0.00");
		hsResult.put("Class_B_NetValue_Discount", "0.00");
		hsResult.put("Class_B_UnitValue_Discount", "0.00000000000000");
		hsResult.put("Class_B_ExdividendUnit_Discount", "0.00000000000000");
		hsResult.put("Class_B_DiscountRatio_Discount", "0.000000000");
		hsResult.put("Class_B_TheNewShare_Discount", "0.00");
		hsResult.put("Class_B_DiscountAmount_Discount", "0.00");
		hsResult.put("Class_B_DiscountNetValue_Discount", "0.00");
		hsResult.put("Class_B_DiscountUnitValue_Discount", "0.00000000000000");
		hsResult.put("Class_B_Creator_Discount", "0.00");
	}

	/**
	 * 初始化arraylist
	 * @param arr 需要被初始化的arraylist
	 */
	private void initArrayList(ArrayList arr)
	{
		arr.add("Received_Amount_Before");			//折算前母基金数量
		arr.add("Received_NetValue_Before");		//折算前母基金净值
		arr.add("Received_UnitValue_Before");		//折算前母基金单位净值
		arr.add("<br>—银华深证100指数分级\t");

		arr.add("Class_Base_Amount_Before");		//折算前基础类数量
		arr.add("Class_Base_NetValue_Before");		//折算前基础类净值
		arr.add("Class_Base_UnitValue_Before");		//折算前基础类单位净值
		arr.add("<br>—银华稳进\t");

		arr.add("Class_A_Amount_Before");			//折算前A类数量
		arr.add("Class_A_NetValue_Before");			//折算前A类净值
		arr.add("Class_A_UnitValue_Before");		//折算前A类单位净值
		arr.add("<br>—银华锐进\t");

		arr.add("Class_B_Amount_Before");			//折算前B类数量
		arr.add("Class_B_NetValue_Before");			//折算前B类净值
		arr.add("Class_B_UnitValue_Before");		//折算前B类单位净值
		arr.add("<br>" + YssFun.formatDate(dDate) + "定期折算明细信息");
		arr.add("<br>科 目\t数 量\t净 值\t单位净值\t除息后净值\t折算比例\t新增银华深证100指数分级份额\t折算后数量\t折算后净值\t" +
				"折算后单位净值\t制作人");
		arr.add("<br>实收基金\t");
		

		arr.add("Received_Amount_Discount");		//母基金数量(与折算前一致)
		arr.add("Received_NetValue_Discount");		//母基金净值(与折算前一致)
		arr.add("Received_UnitValue_Discount");		//母基金单位净值(与折算前一致)
		arr.add("Received_ExdividendUnit_Discount");//母基金除息后净值
		arr.add("Received_DiscountRatio_Discount");	//母基金折算比例
		arr.add("Received_TheNewShare_Discount");	//母基金新增折算份额
		arr.add("Received_DiscountAmount_Discount");//母基金折算后数量
		arr.add("Received_DiscountNetValue_Discount");//母基金折算后净值
		arr.add("Received_DiscountUnitValue_Discount");//募集基金折算后单位净值
//		arr.add("Received_Creator_Discount");
		arr.add(pub.getUserName() + "<br>—银华深证100指数分级\t");

		arr.add("Class_Base_Amount_Discount");		//基础类数量(与折算前一致)
		arr.add("Class_Base_NetValue_Discount");	//基础类净值(与折算前一致)
		arr.add("Class_Base_UnitValue_Discount");	//基础类单位净值(折算前一致)
		arr.add("Class_Base_ExdividendUnit_Discount");//基础类除息后净值
		arr.add("Class_Base_DiscountRatio_Discount");//基础类折算比例
		arr.add("Class_Base_TheNewShare_Discount");	//基础类新增折算份额
		arr.add("Class_Base_DiscountAmount_Discount");//基础类折算后数量
		arr.add("Class_Base_DiscountNetValue_Discount");//基础类折算后净值
		arr.add("Class_Base_DiscountUnitValue_Discount");//基础类折算后单位净值
//		arr.add("Class_Base_Creator_Discount");
		arr.add(pub.getUserName() + "<br>场内\t");

		arr.add("Floor_Amount_Discount");			//场内数量(与折算前一致)
		arr.add("Floor_NetValue_Discount");			//场内净值(与折算前一致)
		arr.add("Floor_UnitValue_Discount");		//场内单位净值(与折算前一致)
		arr.add("Floor_ExdividendUnit_Discount");	//场内除息后净值
		arr.add("Floor_DiscountRatio_Discount");	//场内折算比例
		arr.add("Floor_TheNewShare_Discount");		//场内新增分级份额
		arr.add("Floor_DiscountAmount_Discount");	//场内折算后数量
		arr.add("Floor_DiscountNetValue_Discount");	//场内折算后净值
		arr.add("Floor_DiscountUnitValue_Discount");//场内折算后单位净值
//		arr.add("Floor_Creator_Discount");
		arr.add(pub.getUserName() + "<br>场外\t");

		arr.add("OTC_Amount_Discount");				//场外数量(与折算前一致)
		arr.add("OTC_NetValue_Discount");			//场外净值(与折算前一致)
		arr.add("OTC_UnitValue_Discount");			//场外单位净值(与折算前一致)
		arr.add("OTC_ExdividendUnit_Discount");		//场外除息后净值
		arr.add("OTC_DiscountRatio_Discount");		//场外折算比例
		arr.add("OTC_TheNewShare_Discount");		//场外新增分级份额
		arr.add("OTC_DiscountAmount_Discount");		//场外折算后数量
		arr.add("OTC_DiscountNetValue_Discount");	//场外折算后净值
		arr.add("OTC_DiscountUnitValue_Discount");	//场外折算后单位净值
//		arr.add("OTC_Creator_Discount");
		arr.add(pub.getUserName() + "<br>—银华稳进\t");
		
		arr.add("Class_A_Amount_Discount");			//A类数量(与折算前一致)
		arr.add("Class_A_NetValue_Discount");		//A类净值(与折算前一致)
		arr.add("Class_A_UnitValue_Discount");		//A类单位净值(与折算前一致)
		arr.add("Class_A_ExdividendUnit_Discount");	//A类除息后净值
		arr.add("Class_A_DiscountRatio_Discount");	//A类折算比例
		arr.add("Class_A_TheNewShare_Discount");	//A类新增分级份额
		arr.add("Class_A_DiscountAmount_Discount");	//A类折算后数量
		arr.add("Class_A_DiscountNetValue_Discount");//A类折算后净值
		arr.add("Class_A_DiscountUnitValue_Discount");//A类折算后单位净值
//		arr.add("Class_A_Creator_Discount");
		arr.add(pub.getUserName() + "<br>—银华锐进\t");

		arr.add("Class_B_Amount_Discount");			//B类数量(与折算前一致)
		arr.add("Class_B_NetValue_Discount");		//B类净值(与折算前一致)
		arr.add("Class_B_UnitValue_Discount");		//B类单位净值(与折算前一致)
		arr.add("Class_B_ExdividendUnit_Discount");	//B类除息后净值
		arr.add("Class_B_DiscountRatio_Discount");	//B类折算比例
		arr.add("Class_B_TheNewShare_Discount");	//B类新增分级份额
		arr.add("Class_B_DiscountAmount_Discount");	//B类折算后数量
		arr.add("Class_B_DiscountNetValue_Discount");//B类折算后净值
		arr.add("Class_B_DiscountUnitValue_Discount");//B类折算后单位净值
//		arr.add("Class_B_Creator_Discount");
		arr.add(pub.getUserName() + "<br>");
	}
	
	/**
	 * 根据顺序，将集合中的数据进行包装，返回一个能被前台解析的字符串。
	 * 这个字符串可能是包含表数据的字符串，也可能是包含表公式集合的字符串
	 * @param arr	控制顺序的arraylist
	 * @param hsResults	//数据集合
	 * @return	包装好的字符串
	 * @throws YssException
	 */
	private String setRepViewData(ArrayList arr, Hashtable<String, String> hsResults) throws YssException
	{
		StringBuffer buff = new StringBuffer();
		
		try
		{
			for (int i = 0; i < arr.size(); i++)
			{
				String strResult = (String)arr.get(i);
				
				if(strResult != null)
				{
					//碰到<br>标签，则将该标签转换为换行符
					//===============================
					if(strResult.indexOf("<br>") > -1)
					{
						strResult = strResult.replace("<br>", "\r\n");
						buff.append(strResult);
					}
					//============end===================
					else
					{
						if(hsResults.get(strResult) != null)
						{
							buff.append(hsResults.get(strResult)).append("\t");
						}
						else
						{
							buff.append("0").append("\t");
						}
					}
				}
				else
				{
					return "";
				}
			}
		}
		catch(Exception ye)
		{
			throw new YssException(ye);
		}
		
		return buff.toString();
	}
	
	/**
	 * 实现EXCEL中rounddown函数的作用，即截取一个数字的位数，不考虑四舍五入
	 * @param dTarget	目标数字
	 * @param iDigit	截取位数
	 * @return	截取完成的数字
	 * @throws YssException
	 */
	private double roundDown(double dTarget, int iDigit) throws YssException
	{
		double dTemp = 0.00000000;
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			strSql = "select Trunc(" + YssFun.formatNumber(dTarget, "0.###############") + "," + iDigit + ") as cnt " +
					"from dual";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			if (rs.next())
			{
				dTemp = rs.getDouble("cnt");
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
		
		return dTemp;
	}

}
