package com.yss.main.operdeal.income.stat;

import java.sql.ResultSet;
import java.util.*;

import com.yss.dsub.*;
import com.yss.log.DayFinishLogBean;
import com.yss.log.SingleLogOper;
import com.yss.util.*;

public class BaseIncomeStatDeal
    extends BaseBean {
    protected java.util.Date beginDate;
    protected java.util.Date endDate;
    protected String portCodes;
    protected String selCodes;
    protected String invmgrField = "";
    protected String brokerField = "";
    protected String catField;
    protected String modeCode = ""; // add by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A
	protected String otherParam="";//QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
   	protected String resultMes="";//QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
    //------- MS00270 QDV4赢时胜（上海）2009年2月25日01_B  已计提的数据将放入其中,以便在日期循环中获取之前的日期中计提的数据
    private ArrayList alResult = null;
    //------------------------------------------------------------------------
    protected int dayCount = 0;//add by yeshenghong 2441 积数法  时间段时的天数 
    
	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	public String logSumCode = "";//汇总日志编号
	public boolean comeFromDD = false;//通过调度方案调用
	public String logInfo = "";
	public SingleLogOper logOper = null;//日志实例
	String operType = "";//操作类型
	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	
    public BaseIncomeStatDeal() {
    }
	/**
    * getting()... QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
    * @return
    */
   public String getResultMes(){
	   return resultMes;
   }
    
    //------ add by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A
    public void initIncomeStat(java.util.Date dBeginDate,
                               java.util.Date dEndDate, String sPortCodes,
                               String sSelCodes, String modeCode) throws YssException {
    	
    	initIncomeStat(dBeginDate,dEndDate,sPortCodes,sSelCodes);
        this.modeCode = modeCode;
    }
    //------------------------------ MS00895 --------------------------------//
    
    //为统计时计算利息设置初始信息
    public void initIncomeStat(java.util.Date dBeginDate,
                               java.util.Date dEndDate, String sPortCodes,
                               String sSelCodes) throws YssException {
        this.beginDate = dBeginDate;
        this.endDate = dEndDate;
        this.portCodes = sPortCodes;
        this.selCodes = sSelCodes;
        invmgrField = this.getSettingOper().getStorageAnalysisField(YssOperCons.
            YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
        brokerField = this.getSettingOper().getStorageAnalysisField(YssOperCons.
            YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
        catField = this.getSettingOper().getStorageAnalysisField(YssOperCons.
            YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_CatType);
    }
    /**
    * 重载此方法
    * 添加otherparam参数
    * QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
    */
   public void initIncomeStat(java.util.Date dBeginDate,
                               java.util.Date dEndDate, String sPortCodes,
                               String sSelCodes, String modeCode,String otherParam) throws YssException {
	   initIncomeStat(dBeginDate,dEndDate,sPortCodes,sSelCodes,modeCode);
	   this.otherParam = otherParam;
	   
   }

    //获取计算收益的证券、存款等
    public ArrayList getIncomes() throws YssException {
        java.util.Date operDate = null;
        int iDays = 0;
        ArrayList alResult = new ArrayList();
        //----------add by zhouxiang   2010.11.24-----
        String accountType="";
//        accountType=getAccTypeByAccount(this.selCodes);
        operType=getOperType(this);
//        String subItem="";//收益计提处理子项目
//        subItem=getSubItemBySelCodes(this,selCodes);
		//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        if(logOper == null){
        	logOper = SingleLogOper.getInstance();
        }
		//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        //----------add by zhouxiang   2010.11.24-----
        //---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        Date logBeginDate = null;
 	    DayFinishLogBean df = new DayFinishLogBean();
 	    boolean isError = false;//是否报错
 	    String errorInfo = " ";//报错信息
 	    //---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            iDays = YssFun.dateDiff(beginDate, endDate);
            operDate = beginDate;
            
            //---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            df.setYssPub(pub);
            
            if(!this.comeFromDD){
            	logSumCode = df.getLogSumCodes();
            	logBeginDate = new Date();
            }
        	//---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	
            for (int i = 0; i <= iDays; i++) {
            	//add by songjie 2012.08.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
            	logBeginDate = new Date();
            	
            	this.dayCount = i;
                alResult.addAll(getDayIncomes(operDate));
                //--- MS00270 QDV4赢时胜（上海）2009年2月25日01_B 将每日计提的数据放入公共容器,以便在子类中获取--------
                setAlResult(alResult);
                //---------------------------------------------------------
                operDate = YssFun.addDay(operDate, 1);
            }
            
            //===MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化  sunkey 20090425
            //收益计提成功后，将全局变量中债券计息相关的数据删除，避免第二次计提出现错误
            if (YssGlobal.hmSecRecBeans.containsKey(pub.getUserCode() + pub.getAssetGroupCode())) {
                YssGlobal.hmSecRecBeans.remove(pub.getUserCode() + pub.getAssetGroupCode());
            }
            //=========================End MS00006=================================
            //add by songjie 2012.09.26 STORY #2344 QDV4建行2012年3月2日05_A
            this.saveIncomes(alResult);
            return alResult;
        }catch(YssException e){
        	try{
            	//--- edit by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        		isError = true;//设置为 报错状态
                //获取报错信息
        		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
        		errorInfo = " \r\n收益计提出错\r\n" + //处理日志信息 除去特殊符号
        		e.getMessage().replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", "");
        		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
        		//--- edit by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	
        	throw new YssException(e.getMessage());
        }
        catch (Exception e) {
        	throw new YssException("计提收益出错!\n", e); //改了一下报错信息，之前的提示不准确。sj modified 20081209 | 添加换行符，用以异常解析 sunkey 20090204 QDV4.1-BugNO:MS00004 指示信息的解析处理
        }
        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        finally{
		    //报错业务日志数据
        	if(!this.comeFromDD){
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
        			if(logOper.getDayFinishLog().getAlPojo().size() == 0){
                		logOper.setDayFinishIData(this,7," ", pub, isError, this.portCodes, 
                    			this.beginDate,operDate,this.endDate,errorInfo," ",
                    			logBeginDate,logSumCode,new java.util.Date());
        			}
        			//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
            		logOper.setDayFinishIData(this,7,"sum", pub, isError, " ", 
                			this.beginDate,operDate,this.endDate,errorInfo," ",
                			logBeginDate,logSumCode,new java.util.Date());
        		}
        	}
        	this.comeFromDD = false;
        }
        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    }

    
	
	public ArrayList getDayIncomes(java.util.Date dDate) throws YssException {
        return null;
    }

    //保存收益
    public void saveIncomes(ArrayList alIncome) throws YssException {

    }

    //--------MS00270 QDV4赢时胜（上海）2009年2月25日01_B  sj modified---------------------------
    protected void setAlResult(ArrayList alResult) { //将每日计提的数据,放置入公共容器
        this.alResult = alResult;
    }

    protected ArrayList getAlResult() { //在子类中获取每日计提的数据.
        return this.alResult;
    }
    //-----------------------------------------------------------------------------------------

	
	//获取操作类型edited by zhouxiang 2011.01.07 日终处理日志中的操作项目
	private String getOperType(BaseIncomeStatDeal baseIncomeStatDeal) {
		String sClassName=baseIncomeStatDeal.getClass().getName().toString();
		if(sClassName.indexOf("StatAccInterest")>1){//stataccinterest 第二 现金计息
			return "StatAccInterest";
		}else if(sClassName.indexOf("StatInvestFee")>1){//statinvestfee 第三 两费计息
			//edit by songjie 2012.08.20 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 
			//StatAccInterest 改为 StatInvestFee
			return "StatInvestFee";
		}else if(sClassName.indexOf("StatPurchaseIns")>1){//stataccpurchase 第四 回购计息
			return "StatPurchaseIns";
		}else if(sClassName.indexOf("StatMonetaryFundIns")>1){//statmonetaryfundins 第五 基金万份
			return "StatMonetaryFundIns";
		}else if(sClassName.indexOf("StatSecLendInterest")>1){
			return "statseclendinterest";
		}else if(sClassName.indexOf("StatBondInterest")>1){//statbondinterest 第一 债券计息
			return "StatBondInterest";
		}
		return "";
	}
	//获取操作项目下的明细数据
//	private String getSubItemBySelCodes(BaseIncomeStatDeal baseIncomeStatDeal,
//			String selCodes2) throws YssException {
//		String sClassName=baseIncomeStatDeal.getClass().getName().toString();
//		if(sClassName.indexOf("StatAccInterest")>1){//stataccinterest 第二 现金计息
//			return getSubItemAcc(selCodes2);
//		}else if(sClassName.indexOf("StatInvestFee")>1){//statinvestfee 第三 两费计息
//			return getSubItemFee(selCodes2);
//		}else if(sClassName.indexOf("StatPurchaseIns")>1){//stataccpurchase 第四 回购计息
//			return getSubItemBond(selCodes2);
//		}else if(sClassName.indexOf("StatMonetaryFundIns")>1){//statmonetaryfundins 第五 基金万份
//			return getSubItemBond(selCodes2);
//		}else if(sClassName.indexOf("StatSecLendInterest")>1){
//			return getSubItemBond(selCodes2);
//		}else if(sClassName.indexOf("StatBondInterest")>1){//statbondinterest 第一 债券计息
//			return getSubItemBond(selCodes2);
//		}
//		
//		return null;
//	}
	//使用证券代码获取：证券代码-证券名称
//	public String getSubItemBond(String selCodes2) throws YssException {
//		ResultSet rs=null;
//		String sqlStr="";
//		String sReturn=" ";
//		try{
//			sqlStr="select distinct a.fsecuritycode||'-'||a.fsecurityname as fsecuritycode  from "+pub.yssGetTableName("Tb_Para_Security")
//					+" a where a.fcheckstate=1 and a.fsecuritycode in ("+operSql.sqlCodes(selCodes2)+")";
//			rs=dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
//			while(rs.next()){
//				//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
//				sReturn += rs.getString("fsecuritycode") + YssCons.YSS_LINESPLITMARK;
//			}
//			dbl.closeResultSetFinal(rs);
//		}catch(Exception e){
//			throw new YssException("使用证券代码获取证券代码-证券名称出错");
//		}
//		//--- edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start ---//
//		if(sReturn.length() > 2){
//			sReturn = sReturn.substring(0, sReturn.length() - 2);
//		}
//		//--- edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end ---//
//		
//		return sReturn;
//	}
	//使用运营收支获取：运营收支代码-名称
	public String getSubItemFee(String selCodes2) throws YssException {
		ResultSet rs=null;
		String sqlStr="";
		String sReturn="";
		try{
			sqlStr="select a.fivpaycatcode||'-'||a.fivpaycatname as fivpaycatcode  from " +pub.yssGetTableName("Tb_Base_InvestPayCat")
					+" a where  a.fcheckstate=1 and a.fivpaycatcode in ("+operSql.sqlCodes(selCodes2)+")";
			rs=dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while(rs.next()){
				//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
				sReturn += rs.getString("fivpaycatcode") + YssCons.YSS_LINESPLITMARK;
			}
			
			dbl.closeResultSetFinal(rs);
		}catch(Exception e){
			throw new YssException("使用运营收支代码获取运营收支代码-名称出错");
		}
		
		//--- edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start ---//
		if(sReturn.length() > 2){
			sReturn = sReturn.substring(0, sReturn.length() - 2);
		}
		//--- edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end ---//
		return sReturn;
	}
	//使用现金账户获取：现金账户代码-名称
	public String getSubItemAcc(String selCodes2) throws YssException {
		ResultSet rs=null;
		String sqlStr="";
		String sReturn="";
		try{
			sqlStr="select distinct a.fcashacccode||'-'||a.fcashaccname as fcashacccode from " +pub.yssGetTableName("tb_para_cashaccount")
					+" a where a.fcheckstate=1 and a.fcashacccode in ("+operSql.sqlCodes(selCodes2)+")";
			rs=dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while(rs.next()){
				//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
				sReturn += rs.getString("fcashacccode") + YssCons.YSS_LINESPLITMARK;
			}
			dbl.closeResultSetFinal(rs);
		}catch(Exception e){
			throw new YssException("使用现金账户代码获取账户代码-名称出错");
		}
		
		//--- edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start ---//
		if(sReturn.length() > 2){
			sReturn = sReturn.substring(0, sReturn.length() - 2);
		}
		//--- edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end ---//
		return sReturn;
	}
}
