package com.yss.main.operdeal.report.navrep;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import com.yss.base.*;
import com.yss.log.DayFinishLogBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.derivative.BaseDerivativeOper;
import com.yss.main.operdeal.invest.InvestCfgFormula;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.operdeal.report.navrep.pojo.*;
import com.yss.main.operdeal.valuation.LeverGradeFundCfg;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.pojo.cache.*;
import com.yss.util.*;
import com.yss.vsub.YssDbFun;
import com.yss.vsub.YssFinance;
import com.yss.main.etfoperation.etfshareconvert.ETFShareConvertAdmin;//xuqiji 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A

public class CtlNavRep
    extends BaseAPOperValue implements IClientOperRequest { //为了使净值表可以确认，增加的接口。BugId:MS00184 QDV4交银施罗德2009年01月09日01_B sj modified 20090120
    private Object obj = null;
    //--- add by songjie 2012.11.22 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
    //private edit to public
    public java.util.Date dDate = null;
    public String portCode = "";
    //--- add by songjie 2012.11.22 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 end---//
    private String invMgrCode = "";
    private boolean isSelect = false;
    private boolean bTAConvert=false;//xuqiji 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
    private NavRepBean navRep = null;//xuqiji 20091020 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
	private int operType;
	//edit by songjie 2012.10.29 STORY #2344 QDV4建行2012年3月2日05_A
	public SingleLogOper logOper;//private 改为 public 
	private int flag = 0; 
	//add by huangqirong 2013-01-31 Story #3433 , bug #6975
    public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	//---end---
	//---add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
	public String logSumCode = "";//汇总日志编号
	public boolean comeFromDD = false;//是否通过调度方案调用
	//---add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
	
    //---------在资产估值时，在后台直接赋值时用 sj add 20080401 ------
    public String getPortCode() {
        return portCode;
    }

    public boolean isIsSelect() {
        return isSelect;
    }

    public Object getObj() {
        return obj;
    }

    public Date getDDate() {
        return dDate;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public void setDDate(Date dDate) {
        this.dDate = dDate;
    }
	
    public void setBTAConvert(boolean bTAConvert) {
        this.bTAConvert = bTAConvert;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public boolean isBTAConvert() {
        return bTAConvert;
    }

    //-----------------------------------------------------------

    public CtlNavRep() {
    }

    public void init(Object bean) throws YssException {
        String reqAry[] = null;
        String reqAry1[] = null;
        String sRowStr = (String) bean;
        if (sRowStr.trim().length() == 0) {
            return;
        }
        reqAry = sRowStr.split("\n");
        reqAry1 = reqAry[0].split("\r");
        this.dDate = YssFun.toDate(reqAry1[1]);
        reqAry1 = reqAry[1].split("\r");
        this.portCode = reqAry1[1];
        reqAry1 = reqAry[2].split("\r");
        this.isSelect = new Boolean(reqAry1[1]).booleanValue();
        reqAry1 = reqAry[3].split("\r");
        this.invMgrCode = reqAry1[1];
        //xuqiji 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A--//
        if(reqAry.length>4){
	        reqAry1 = reqAry[4].split("\r");
	        this.bTAConvert=new Boolean(reqAry1[1]).booleanValue();
        }
        //--------------------------end--------------------------//
    }

    public Object invokeOperMothed() throws YssException {
        //---2009.04.18 蒋锦 添加 流程控制中适用组合的处理---//
        //参数布局散乱不便操作 MS00003
        //判断是否在组合中执行
        if (pub.getFlow() != null && pub.getFlow().keySet().contains(pub.getUserCode())) {
            //插入已执行组合
            ( (FlowBean) pub.getFlow().get(pub.getUserCode())).setFPortCodes(
                portCode);
        }
        //-----------------------------------------------//
        
		//---add by songjie 2012.09.21 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
  	    Date logStartTime = null;//业务子项开始时间
  	    if(logOper == null){//添加空指针判断
  	    	logOper = SingleLogOper.getInstance();
  	    }
        DayFinishLogBean df = new DayFinishLogBean();
        //---add by songjie 2012.09.21 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        
        //-------此处增加通用参数控制室为了在插入资产净值表数据时，若为中保则安之不同的投资经理插入不同的值。sj edit 20081120
        String netValueType = "";
        CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        netValueType = pubpara.getNavType();
        //-------------xuqiji 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A---//
        
		//---add by songjie 2012.09.21 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
		//this.setFunName("navdata");
    	logStartTime = new Date();//开始时间
		ResultSet rs = null;
		boolean isError = false;//是否为异常操作

		//---add by songjie 2012.09.21 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    	try {
    		//---add by songjie 2012.09.21 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    		if(logSumCode.length() == 0){
    			df.setYssPub(pub);
    			logSumCode = df.getLogSumCodes();
    		}else{
    			comeFromDD = true;
    		}
    		//---add by songjie 2012.09.21 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    		
    		if(bTAConvert){
            
    			ETFShareConvertAdmin etf = new ETFShareConvertAdmin();
    			etf.setYssPub(pub);
    			etf.doOperation(this.portCode, this.dDate, this.invMgrCode);
    			navRep=etf.getOldNavValueData(this.portCode, this.dDate, this.invMgrCode);
    		}
    		// -----------------------------------------end--------------------------------//
       	 	if (!isSelect) {
       	 		doNAVDeal("NavSecurity", "Security");
       	 		doNAVDeal("NavCash", "Cash");
       	 		doNAVDeal("NavInvest", "Invest");
       	 		doNAVDeal("NavTotal", "Total,UseCash,subTotal");//QDV4华夏2009年8月24日03_A MS00652 添加subTotal类型 by leeyu 20090905
       	 		updateNavRatio(this.dDate, this.portCode, this.invMgrCode); //更新占净值比例。sj edit 20080820
       	 		//--- MS00570 QDV4华安2009年07月16日01_AB -----------------------------------------------------
       	 		updateCostAndChangeWithCost(this.dDate, this.portCode, this.invMgrCode);//计算单位成本和涨跌
       	 		//--------------------------------------------------------------------------------------------
       	 		//====add by yanghaiming 20091124 MS00824 QDV4赢时胜海富通2009年11月20日01_B===========
       	 		updateNewSharePrice(this.dDate, this.portCode, this.invMgrCode); //计算新股的行情价格，以本位币成本/数量 获得

       	 		if (netValueType.trim().equalsIgnoreCase("new")) { //国内的方式
       	 			insertNetValue(this.dDate, this.portCode, this.invMgrCode); //插入资产净值表。sj add 20080626
       	 		} else { //中保的方式。
       	 			BaseBuildValuationRep valNet = (BaseBuildValuationRep) pub.
       	 				getOperDealCtx().getBean("valuationrep");
       	 			valNet.setYssPub(pub);
       	 			valNet.saveNetValue(portCode, dDate);
       	 		}
       	 		//add by yanghaiming MS01228 QDV4赢时胜(上海)2010年06月02日01_A
        		String sqlStr = "";

        		CtlPubPara ctlPubPara = new CtlPubPara();
        		ctlPubPara.setYssPub(pub);
        		if(ctlPubPara.getPerInterface().equalsIgnoreCase("0") && this.invMgrCode.equalsIgnoreCase("total")){//B股业务处理方式，循环投资经理生成净值
        		
	        		sqlStr = "SELECT b.FSubCode" +
	                " FROM " + pub.yssGetTableName("Tb_Para_Portfolio") + " a" +
	                " JOIN " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " b ON a.fportcode = b.fportcode" +
	                " WHERE a.FPortCode = " + dbl.sqlString(portCode) + " AND FRelaType = 'InvestManager'" +
	                " ORDER BY FSubCode";
	        		rs = dbl.openResultSet(sqlStr);
	        		while (rs.next()){
	        			this.invMgrCode = rs.getString("FSubCode");
	        			doNAVDeal("NavSecurity", "Security");
	                    doNAVDeal("NavCash", "Cash");
	                    doNAVDeal("NavInvest", "Invest");
	                    doNAVDeal("NavTotal", "Total,UseCash,subTotal");
	                    updateNavRatio(this.dDate, this.portCode, this.invMgrCode);
	                    updateCostAndChangeWithCost(this.dDate, this.portCode, this.invMgrCode);
	                    updateNewSharePrice(this.dDate, this.portCode, this.invMgrCode);
	                    if (netValueType.trim().equalsIgnoreCase("new")) { //国内的方式
	                        insertNetValue(this.dDate, this.portCode, this.invMgrCode);
	                    }
	        		}
        		}
        	} 
       	 	
			//--- add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
       	 	if(this.getFunName() != null){//若功能调用代码不为空，则插入业务日志数据
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
       	 			logOper.setDayFinishIData(this, 19,"净值统计表生成", 
            		pub, false, portCode,  this.dDate,this.dDate,
            		this.dDate,"净值统计表生成成功",logStartTime,
            		logSumCode, new java.util.Date());
        		}
       	 	}
            //--- add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
    		
    		//---------add by guojianhua 2010 09 15 增加日志记录---------------
   		    operType=19;
            this.setFunName("navdata");
            this.setModuleName("dayfinish");
            this.setRefName("000499");
            logOper.setIData(this, operType, pub);
            //-------------end------------------------------
        }catch (Exception e) {
  			//---add by songjie 2012.09.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
  			 try{
  				isError= true;//设置为异常状态
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, 19,"净值统计表生成", pub, 
 	            		true, portCode, this.dDate,this.dDate,this.dDate,
 	            		"净值统计表生成失败\r\n" + 
 	            		//edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A 处理日志信息 除去特殊符号
 	                    e.getMessage().replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", ""),
 	                    logStartTime,logSumCode, new java.util.Date());
        		}
  			 }catch(Exception ex){
  			    ex.printStackTrace();
  			 }
  			 //---add by songjie 2012.09.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
             //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
  			 finally{//添加 finally 保证可以抛出异常
            	 throw new YssException("生成资产净值出错！", e);
             }
  			 //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
             dbl.closeResultSetFinal(rs);
             //---add by songjie 2012.09.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
             if(!comeFromDD){//如果不是通过调度方案执行生成日志，则直接保存日志数据
         		//edit by songjie 2012.11.20 添加非空判断
         		if(logOper != null){
         			logOper.setDayFinishIData(this, 19,"sum", pub,
            			 isError, portCode,this.dDate,this.dDate,
            			 this.dDate," ",logStartTime,
            			 logSumCode, new java.util.Date());
         		}
             }
             //---add by songjie 2012.09.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        }
        return null;
    }
    /**
     * 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     */
    public void invokeETFOperMethod() throws YssException {
        if (pub.getFlow() != null && pub.getFlow().keySet().contains(pub.getUserCode())) {
            //插入已执行组合
            ( (FlowBean) pub.getFlow().get(pub.getUserCode())).setFPortCodes(
                portCode);
        }
        CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        if (!isSelect) {
            doETFNAVDeal("NavSecurity", "Security");
            doETFNAVDeal("NavCash", "Cash");
            doETFNAVDeal("NavInvest", "Invest");
            doETFNAVDeal("NavTotal", "Total,UseCash,subTotal");//QDV4华夏2009年8月24日03_A MS00652 添加subTotal类型 by leeyu 20090905
        }
    }
    
    private void doNAVDeal(String sBeanId, String Type) throws YssException {
        ArrayList valData = null;
        BaseNavRep val = (BaseNavRep) pub.getOperDealCtx().getBean(sBeanId); //获取子类
        val.setYssPub(pub);
        valData = val.buildRepData(dDate, portCode, invMgrCode); //执行子类方法*/
        //-----------xuqiji 20091020 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A------------------//
        addBeforeTAShareConvertNavData(sBeanId,valData);//此方法增加TA份额折算前的基金单位净值数据
        //--------------------------------------end 20091020-----------------------------------//
        val.deleteData(Type);
        val.insertTable(valData);
    }
    /**
     * 此方法增加TA份额折算前的基金单位净值数据
     * xuqiji 20091020 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @param sbeanId 子类标识
     * @param valData 保存数据的集合
     */
    private void addBeforeTAShareConvertNavData(String sbeanId,ArrayList valData) throws YssException{
    	try{
	    	if(this.bTAConvert){
	        	if(sbeanId.equalsIgnoreCase("NavTotal")){
		        	if(this.navRep.getKeyCode().trim().length()>0&&this.navRep.getOrderKeyCode().trim().length()>0){
		        		valData.add(this.navRep);
		        	}
	        	}
	        }
    	}catch(Exception e){
    		throw new YssException("增加TA份额折算前的基金单位净值数据出错！",e);
    	}
	}
    /**
     * 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     */
	private void doETFNAVDeal(String sBeanId, String Type) throws YssException {
        ArrayList valData = null;
        BaseNavRep val = (BaseNavRep) pub.getOperDealCtx()
            .getBean(sBeanId); //获取子类
        val.setYssPub(pub);
        val.bETFVal = true;
        valData = val.buildRepData(dDate, portCode, invMgrCode); //执行子类方法*/
        val.deleteETFData(Type);
        val.insertETFTable(valData);
    }

    /**
     * 在完成净值统计之后将相关数值录入资产净值表中.sj edit 20080627
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @throws YssException
     */
    public void insertNetValue(Date dDate, String portCode, String invMgrCode) throws
        YssException {
        String sqlStr = "";
        ResultSet rs = null;
        boolean analy1 = false;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "InvestPayRec");
            Hashtable hmNetValue = null;
            hmNetValue = getReport(dDate, portCode, invMgrCode);
            //edit by yanghaiming 20100624 MS01228 QDV4赢时胜(上海)2010年06月02日01_A
            if(invMgrCode.equalsIgnoreCase("total")){
            	saveReport(hmNetValue, " "); //这里需要一个空格。sj edit 20080716
            }else{
            	saveReport(hmNetValue,invMgrCode);
            }
//            if (analy1) { //如果有投资经理设置，则设置此投资经理的净值数据。sj edit 20080714
//                //----------2009-02-04 蒋锦 修改 MS00228 QDV4华夏2009年2月04日03_B  投资经理 取数错误 ---------------------//
//                //2009-02-04 蒋锦 删除 代码错误
//                //需要取与组合相关连的投资经理
//                //不能直接从投资经理表中取数，否则在多组合多投资经理的情况下，不可能得到正确的投资经理
////            sqlStr = "select FInvMgrCode,FInvMgrName from " +
////                  pub.yssGetTableName("Tb_Para_InvestManager") +
////                  " where FCheckState = 1 " +
////                  " order by FInvmgrCode";
////            rs = dbl.openResultSet(sqlStr);
////            if (rs.next()) { //只保存第一个经排序的投资经理的值。sj edit 20080722
////               saveReport(hmNetValue, rs.getString("FInvMgrCode"));
////            }
//                //在组合关联表中取于组合相关联的投资经理
//                sqlStr = "SELECT b.FSubCode" +
//                    " FROM " + pub.yssGetTableName("Tb_Para_Portfolio") + " a" +
//                    " JOIN " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " b ON a.fportcode = b.fportcode" +
//                    " WHERE a.FPortCode = " + dbl.sqlString(portCode) + " AND FRelaType = 'InvestManager'" +
//                    " ORDER BY FSubCode";
//                rs = dbl.openResultSet(sqlStr);
//                if (rs.next()) {
//                    saveReport(hmNetValue, rs.getString("FSubCode"));
//                } else {
//                    throw new YssException("组合：" + portCode + "缺少关联投资经理！");
//                }
//                //-----------------------------------------------------------------------------------------//
//            }
        } catch (Exception e) {
            throw new YssException("录入资产净值表出错！", e);
        } finally {
            //2009-02-05 蒋锦 添加 关闭记录集
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 从净值数据表中获取相关汇总数据。sj edit 20080627
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @return Hashtable
     * @throws YssException
     */
    private Hashtable getReport(Date dDate, String portCode, String invMgrCode) throws
        YssException {
        Hashtable hmNetValue = null;
        String sqlStr = "";
        ResultSet rs = null;
        YssNetValue netValue = null;
        String key = "";
        try {
            hmNetValue = new Hashtable();
            sqlStr = "select FKeyCode,FPrice,FPortMarketValue from " +
                pub.yssGetTableName("Tb_Data_NavData") +
                " where FNavDate = " + dbl.sqlDate(dDate) + " and FPortCode = " +
                dbl.sqlString(portCode) +
                " and FInvMgrCode = " + dbl.sqlString(invMgrCode) +
                " and FReTypeCode in ('Total')";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                netValue = new YssNetValue();
                netValue.setInvMgrCode(this.invMgrCode);
                key = rs.getString("FKeyCode");
                if (key.equalsIgnoreCase("TotalValue")) { //资产净值
                    netValue.setPortNetValue(rs.getDouble("FPortMarketValue"));
                } else if (key.equalsIgnoreCase("Unit")) { //单位净值
                    if (rs.getDouble("FPortMarketValue") == 0.0) {
                        netValue.setUnitPortNetValue(rs.getDouble("FPrice"));
                    } else {
                        netValue.setUnitPortNetValue(rs.getDouble("FPortMarketValue"));
                    }
                } else if (key.equalsIgnoreCase("MV")) { //估值增值
                    netValue.setIncPortMV(rs.getDouble("FPortMarketValue"));
                } else if (key.equalsIgnoreCase("FX")) { //汇兑损益
                    netValue.setExPortFX(rs.getDouble("FPortMarketValue"));

                } else if (key.equalsIgnoreCase("TotalAmount")) { //实收资本
                    netValue.setCapitalPortCuryCost(rs.getDouble("FPortMarketValue"));
                } else if (key.equalsIgnoreCase("UnPL")) { //损益平准金（未实现）
                    netValue.setPortUnPl(rs.getDouble("FPortMarketValue"));
                } else if (key.equalsIgnoreCase("PL")) { //损益平准金（已实现）
                    netValue.setPortPl(rs.getDouble("FPortMarketValue"));
                }
                hmNetValue.put(key, netValue);
            }
        } catch (Exception e) {
            throw new YssException("");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmNetValue;
    }

    /**
     * 录入资产净值表。sj edit 20080627
     * @param hmNetValue Hashtable
     * @return String
     * @throws YssException
     */
    public String saveReport(Hashtable hmNetValue, String InvMgrCode) throws
        YssException {
        String strSql = "";
        PreparedStatement pst = null;
        YssNetValue netValue = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        Iterator iter = null;
        String key = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
         	dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_Data_NetValue"));//添加行级锁 合并太平版本代码
            //删除原有记录
            if (InvMgrCode == null || InvMgrCode.trim().length() == 0) { //如果投资经理为空格.
                strSql = "delete from " + pub.yssGetTableName("Tb_Data_NetValue") +
//                  " where FInvMgrCode = " + dbl.sqlString(InvMgrCode) + //按投资经理为空格的纪录删除。
//                  " and FPortCode = " + dbl.sqlString(this.portCode) +
//                  " and FNAVDate = " + dbl.sqlDate(dDate);              //投资经理为空的话全删除
                    " where FPortCode = " + dbl.sqlString(this.portCode) +
                  //edit by yanghaiming 20100624 MS01228 QDV4赢时胜(上海)2010年06月02日01_A
                    " and FInvMgrCode = ' ' and FNAVDate = " + dbl.sqlDate(dDate);
            } else {
                strSql = "delete from " + pub.yssGetTableName("Tb_Data_NetValue") +
                //edit by yanghaiming 20100624 MS01228 QDV4赢时胜(上海)2010年06月02日01_A
                    //" where FInvMgrCode <> " + dbl.sqlString(" ") + //将投资经理不是空格的纪录都删除。
                	" where FInvMgrCode = " + dbl.sqlString(InvMgrCode) +
                    " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " and FNAVDate = " + dbl.sqlDate(dDate);
            }
            dbl.executeSql(strSql);

            //向资产净值表中插入基础货币资产净值
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_NetValue") +
                " (FNAVDate,FPortCode,FInvMgrCode,FBaseNetValue,FPortNetValue,FAmount,FType,FCheckState,FCreator,FCreateTime)" +
                " values(?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            Iterator it = hmNetValue.keySet().iterator();
            while (it.hasNext()) {
                key = (String) it.next();
                netValue = (YssNetValue) hmNetValue.get(key);
                if (InvMgrCode != null && InvMgrCode.trim().length() > 0) { //若参数InvMgrCode有值，则设置此投资经理的值。sj edit 20080714
                    netValue.setInvMgrCode(InvMgrCode);
                }
                if (key.equalsIgnoreCase("TotalValue")) { //插入资产净值
//               netValue = (YssNetValue) hmNetValue.get(key);
                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) { //若为total,则设置为空格。sj edit 20080707.
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4, netValue.getBaseNetValue());
                    pst.setDouble(5, netValue.getPortNetValue());
                    pst.setDouble(6, 0);
                    pst.setString(7, "01"); //标识－资产净值
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();

                } else if (key.equalsIgnoreCase("Unit")) { //插入单位净值
//               netValue = (YssNetValue) hmNetValue.get(key);
                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4,
                                  netValue.getUnitBaseNetValue());
                    pst.setDouble(5,
                                  netValue.getUnitPortNetValue());
                    pst.setDouble(6, 0);
                    pst.setString(7, "02"); //标识－单位净值
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();
                } else if (key.equalsIgnoreCase("MV")) { //插入估值增值
//               netValue = (YssNetValue) hmNetValue.get(key);
                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }

                    pst.setDouble(4, netValue.getIncBaseMV());
                    pst.setDouble(5, netValue.getIncPortMV());
                    pst.setDouble(6, 0);
                    pst.setString(7, "03"); //标识－估值增值
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();
                } else if (key.equalsIgnoreCase("FX")) { //插入汇兑损益
//               netValue = (YssNetValue) hmNetValue.get(key);
                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4, netValue.getExBaseFX());
                    pst.setDouble(5, netValue.getExPortFX());
                    pst.setDouble(6, 0);
                    pst.setString(7, "04"); //标识－汇兑损益
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();
                } else if (key.equalsIgnoreCase("TotalAmount")) { //插入实收资本
//               netValue = (YssNetValue) hmNetValue.get(key);
                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4, netValue.getCapitalBaseCuryCost());
                    pst.setDouble(5, netValue.getCapital());
                    pst.setDouble(6, netValue.getCapitalPortCuryCost());
                    pst.setString(7, "05"); //标识－实收资本
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();
                } else if (key.equalsIgnoreCase("UnPL")) { //插入损益平准金（未实现）
//               netValue = (YssNetValue) hmNetValue.get(key);

                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4, netValue.getBaseUnPl());
                    pst.setDouble(5, netValue.getPortUnPl());
                    pst.setDouble(6, 0); //实收资本插入数量
                    pst.setString(7, "06"); //标识－损益平准金（未实现）
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();

                } else if (key.equalsIgnoreCase("PL")) { //插入损益平准金（已实现）
//               netValue = (YssNetValue) hmNetValue.get(key);

                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4, netValue.getBasePl());
                    pst.setDouble(5, netValue.getPortPl());
                    pst.setDouble(6, 0);
                    pst.setString(7, "07"); //标识－损益平准金（已实现）
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();

                } 
//                else if (key.equalsIgnoreCase("Cash")) { //插入现金头寸
//                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新资产净值表信息出错" + "\n" + e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(pst);
        }
        return "";
    }

    /**
     * 更新净值表的市值比例
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @throws YssException
     */
    private void updateNavRatio(Date dDate, String portCode,
                                String invMgrCode) throws YssException {
        String sqlStr = "";
        String tempViewName = "";
	    Connection conn =dbl.loadConnection();
	    boolean bTrans =false;
        try {
            tempViewName = "V_Temp_NavRatio_" + pub.getUserCode();
            createNavRatioView(dDate, portCode, invMgrCode, tempViewName);
        } catch (Exception e) {
            throw new YssException(e);
        }
     	dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_Data_NavData")); //合并太平版本代码 加锁
        sqlStr = "update " + pub.yssGetTableName("tb_Data_NavData") + " nav set nav.FPortMarketValueRatio = (select FPortMarketRatio from " +
            tempViewName + "  where  FKeyCode = nav.Fkeycode" +
            " and FNavDate = nav.fnavdate " +
            " and FReTypeCode = nav.FReTypeCode" +
            " and FDetail = nav.FDetail" +
            " and FOrderCode = nav.FOrderCode" +
            " and FInvestType = nav.FInvestType" +  //modify by fangjiang story 1156 2011.07.25
            ") where exists (select 'X' from " + tempViewName + " where nav.Fkeycode = " + tempViewName + ".FKeyCode" +
            " and nav.fnavdate = " + tempViewName + ".FNavDate and nav.FReTypeCode = " + tempViewName + ".FReTypeCode " +
            " and nav.FDetail = " + tempViewName + ".FDetail" +
            " and nav.FOrderCode = " + tempViewName + ".FOrderCode" +
            " and nav.FInvestType = " + tempViewName + ".FInvestType" + //modify by fangjiang story 1156 2011.07.25
            ")"+//fanghaoln 20100429 MS01103 QDV4华夏2010年4月20日01_B
            " and nav.FportCode = "  +dbl.sqlString(portCode);//QDII分盘在生成净值表要区分组合
            //------------------end ---MS01103----------------------
        try {
    	  	conn.setAutoCommit(bTrans);
          	bTrans =true;
            dbl.executeSql(sqlStr);
         	conn.commit();
         	conn.setAutoCommit(bTrans);
         	bTrans =false;
        } catch (Exception e) {
            throw new YssException("更新净值比例出错！", e);
	    }finally{
	      dbl.endTransFinal(conn, bTrans);
	    }
    }

    /**
     * 建立生成比例的视图
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @param tempViewName String
     * @throws YssException
     */
    private void createNavRatioView(Date dDate, String portCode,
                                    String invMgrCode, String tempViewName) throws YssException {
        String sqlStr = "";
        StringBuffer buf = null;
        try {
            if (tempViewName.trim().length() > 0 && dbl.yssViewExist(tempViewName)) {
                dbl.executeSql("drop view " + tempViewName);
            }
            //添加删除表的语句，系统优化，合并太平版本调整 by leeyu 20100824
            if (tempViewName.trim().length() > 0 && dbl.yssTableExist(tempViewName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " + tempViewName));
                /**end*/
            }
          //添加删除表的语句，系统优化，合并太平版本调整 by leeyu 20100824
        } catch (Exception e) {
            throw new YssException("建立比例试图出错！", e);
        }
//      try {
        buf = new StringBuffer(4096);
        //buf.append("create  view ").append(tempViewName);
        buf.append("create  table ").append(tempViewName);//将视图改为临时表，系统优化，合并太平版本调整 by leeyu 20100824
        buf.append(" as (");
        buf.append(" select data.FKeyCode,");
        buf.append(" data.FDetail,");
        buf.append(" data.FCuryCode,");
        buf.append(" data.FNavDate,");
        buf.append(" data.FCost,");
        buf.append(" data.FPortCost,");
        buf.append(" data.FMarketValue,");
        buf.append(" data.FPortMarketValue,");
        buf.append(" data.FMVValue,");
        buf.append(" data.FFXValue,");
        buf.append(" data.FPortMVValue,");
        buf.append(" data.FSParAmt,");
        buf.append(" round((data.FPortMarketValue / totalvalue.FPortMarketValue) * 100, 4) as FPortMarketRatio,");
        buf.append(" data.FOrderCode,");
        buf.append(" data.FReTypeCode,");
        buf.append(" data.FInvestType"); //modify by fangjiang story 1156 2011.07.25
        buf.append(" from (select FKeyCode,");
        buf.append(" FCuryCode,");
        buf.append(" FNavDate,");
        buf.append(" FDetail,");
        buf.append(" FCost,");
        buf.append(" FPortCost,");
        buf.append(" FMarketValue,");
        buf.append(" FPortMarketValue,");
        buf.append(" FFXValue,");
        buf.append(" FMVValue,");
        buf.append(" FPortMVValue,");
        buf.append(" round(FSParAmt, 2) as FSParAmt,");
        buf.append(" 'link' as FLink,");
        buf.append(" FGradeType2,");
        buf.append(" FGradeType6,");
        buf.append(" FOrderCode,");
        buf.append(" FReTypeCode,");
        buf.append(" FInvestType"); //modify by fangjiang story 1156 2011.07.25
        buf.append(" from ");
        buf.append(pub.yssGetTableName("tb_Data_NavData"));
        buf.append(" where  ");
//         buf.append(" and FDetail = 0");
        buf.append("  FInvMgrCode = ");
        buf.append(dbl.sqlString(invMgrCode));
//         buf.append(" and FGradeType2 = 'EQ01'");
        buf.append(" and ((FReTypeCode = 'Security' and (FGradeType6 is null or FGradeType6 = '')) or " +
        		//"(FReTypeCode = 'Cash' and (FGradeType5 is null or FGradeType5 = '')) " +
        		//"(FReTypeCode = 'Cash' and (FGradeType6 is null or FGradeType6 = '')) " +//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
        		//【STORY #2436 净值统计表中，原先的现金类的账户下一级要求改为相应数据 】add by jsc 20120401 
        		// 净值统计表中，原先的现金类的账户下一级，例如清算款，应收利息，应收股息的占净值比例都为0，现在要求改为相应数据
        		" (FReTypeCode = 'Cash' )"+
        		"or (FReTypeCode = 'Invest')) ");
        buf.append(" and FPortCode = ");
        buf.append(dbl.sqlString(portCode));
        buf.append(" and FNAVDate = ");
        buf.append(dbl.sqlDate(dDate));
        buf.append(" ) data");
        buf.append(" left join (select FPortMarketValue, 'link' as FLink");
        buf.append(" from ");
        buf.append(pub.yssGetTableName("tb_Data_NavData"));
        buf.append(" where FNAVDate = ");
        buf.append(dbl.sqlDate(dDate));
        buf.append(" and FPortCode =");
        buf.append(dbl.sqlString(portCode));
        buf.append(" and FInvMgrCode =");
        buf.append(dbl.sqlString(invMgrCode));
        buf.append(" and FReTypeCode = 'Total'");
        buf.append(" and FKeyCode = 'TotalValue') totalvalue on data.FLink = ");
        buf.append(" totalvalue.FLink)");
        sqlStr = buf.toString();
        buf.delete(0, buf.length());
        try {
            dbl.executeSql(sqlStr);
            //添加主键 ，系统优化，合并太平版本调整 by leeyu 20100824
            sqlStr="alter table "+tempViewName+" add constraint PK_"+tempViewName
            +" primary key(FKeyCode,FDetail,FCuryCode,FNavDate,FOrderCode,FRetypeCode, FInvestType)"; //modify by fangjiang story 1156 2011.07.25
            dbl.executeSql(sqlStr);
            //合并太平版本调整 by leeyu 20100824
        } catch (Exception e) {
            throw new YssException("生成净值比例视图出错！", e);
        }
    }

//-------为了使净值表可以确认，增加的接口。BugId:MS00184 QDV4交银施罗德2009年01月09日01_B sj modified 20090120
    public String checkRequest(String sType) throws YssException {
        return "";
    }

    public String doOperation(String sType) throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        String[] confirms = null;
        String resultStr = "";
        if (sType.indexOf("@confirm@") > 0) { //若有，则为对当日的净值确认或反确认。
            confirms = sType.split("@confirm@");
            resultStr = doConfirmed(confirms[0], confirms[1]); //0位操作类型,1为参数
        } else if (sType.indexOf("@navInfo@") > 0) { //查询当日的净值为确认或反确认
            confirms = sType.split("@navInfo@");
            resultStr = getConfirmInfo(confirms[1]); //1为参数
        } else if (sType.equalsIgnoreCase("checkPerInterface")){
        	resultStr = isPerInterface();
        }
        return resultStr;
    }

    /**
     * 执行确认及反确认
     * BugId:MS00184 QDV4交银施罗德2009年01月09日01_B
     * @param doType String
     * @param params String
     * @return String
     * @throws YssException
     */
    private String doConfirmed(String doType, String params) throws YssException {
        String resultStr = "";
        //====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能 QDV4华夏2009年12月29日01_A ===========  
        String[] reqDate = null;
        // BUG2998在【净值统计表】中选择组合时净值表不能确认 add by jiangshichao 2011.10.26 start
        //reqDate = params.split("\b\f\b")[0].split("~n~");//日期段的两个日期区间是用~n~分隔的
        reqDate = params.split("\b\\|\b")[0].split("~n~");//日期段的两个日期区间是用~n~分隔的
        // BUG2998在【净值统计表】中选择组合时净值表不能确认  add by jiangshichao 2011.10.26  end 
        String dNavDate = "";//净值日期
        dNavDate = reqDate[0];
        String dEndDate = "";//结束日期，如果是日期区间，此处才有值      
        boolean hasNavValue = true;
        if(reqDate!=null&&reqDate.length>1){
        	dEndDate = reqDate[1];
        }
        //edit by yanghaiming 20100408 MS01068 QDV4赢时胜上海2010年4月06日01_A 
        else{
        	dEndDate = dNavDate;
        }
        //===========end==================================================
//        String dNavDate = params.split("\b\f\b")[0]; //净值日期
     // BUG2998在【净值统计表】中选择组合时净值表不能确认 add by jiangshichao 2011.10.26 start
        //String sPortCode = params.split("\b\f\b")[1]; //组合
        //String sInvMgrCode = params.split("\b\f\b")[2]; //投资经理
        String sPortCode = params.split("\b\\|\b")[1]; //组合
        String sInvMgrCode = params.split("\b\\|\b")[2]; //投资经理
     // BUG2998在【净值统计表】中选择组合时净值表不能确认 add by jiangshichao 2011.10.26 end
        BaseNavRep navConfirm = null; //对净值表操作的类
        ArrayList valData = null; //植入BaseNavRep
        NavRepBean nav = null; //净值数据的VO
        if (doType.equalsIgnoreCase("confirm")) { //确认净值
        	hasNavValue = hasNavTotalValue(dNavDate, dEndDate, sPortCode, sInvMgrCode);
//            if (hasNavValue != null && hasNavValue.length() > 1) { //判断是否已生成资产净值数据，若尚未生成，则抛出提示信息。
//            	//edit by yanghaiming 20100408 MS01068 QDV4赢时胜上海2010年4月06日01_A
//                throw new YssException("对不起！以下日期" + hasNavValue +"尚未生成资产净值数据，请生成资产净值数据再进行确认操作！");
//            }
        	//edit by yanghaiming 20100513 MS01068 QDV4赢时胜上海2010年4月06日01_A
        	if(!hasNavValue){
        		throw new YssException("对不起！" + dNavDate + "至" + dEndDate +"无资产净值数据，请生成资产净值数据再进行确认操作！");
        	}
            navConfirm = new BaseNavRep();
            navConfirm.portCode = sPortCode;
            if (!YssFun.isDate(dNavDate)) {
                throw new YssException("系统在确认净值时获取净值日期出现异常!");
            }
            navConfirm.dDate = YssFun.toDate(dNavDate);
          //====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能
            if(dEndDate!=null&&dEndDate.trim().length()>0&&YssFun.isDate(dEndDate)){
            	navConfirm.dEndDate =YssFun.toDate(dEndDate);
            }
            //==================end==============================
            navConfirm.invMgrCode = sInvMgrCode;
            navConfirm.setYssPub(pub);
            try {
                navConfirm.deleteData("confirm"); //删除确认信息
            } catch (YssException ex) {
                throw new YssException("系统在确认净值前的删除操作出现异常", ex);
            }
            valData = new ArrayList();
          //====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能 QDV4华夏2009年12月29日01_A ===========  
            //循环日期区间，对其中的每个日期进行确认操作=============
            if (null == navConfirm.dEndDate){
            	navConfirm.dEndDate = navConfirm.dDate;
            }//若没有结束日期，不是区间段确认，就需要设置一个默认值。
            while(navConfirm.dDate.compareTo(navConfirm.dEndDate)<=0){//当dDate比dEndDate小的时候循环 
            	
            	nav = new NavRepBean(); //实例化净值数据的VO                
                nav.setReTypeCode("confirm"); //类型为净值确认数据
                nav.setInvMgrCode(sInvMgrCode);
                nav.setPortCode(sPortCode);
                nav.setNavDate(navConfirm.dDate);
                nav.setKeyCode("confirm");
                nav.setKeyName("确认净值表的锁定记录");
                nav.setOrderKeyCode("confirm" + YssFun.formatDate(navConfirm.dDate));
                nav.setCuryCode("confirmCury");
                valData.add(nav); //将净值数据放入ArrayList
                navConfirm.dDate=YssFun.addDay(navConfirm.dDate, 1);//加上一天，这样才能循环日期区间
            }
            //===========end====================================
            /*
            nav = new NavRepBean(); //实例化净值数据的VO
            //-----------------------------
            nav.setReTypeCode("confirm"); //类型为净值确认数据
            nav.setInvMgrCode(sInvMgrCode);
            nav.setPortCode(sPortCode);
            nav.setNavDate(YssFun.toDate(dNavDate));
            nav.setKeyCode("confirm");
            nav.setKeyName("确认净值表的锁定记录");
            nav.setOrderKeyCode("confirm" + dNavDate);
            nav.setCuryCode("confirmCury");
            valData.add(nav); //将净值数据放入ArrayList         */
            
            navConfirm.insertTable(valData); //将确认信息插入净值表
            
            //add by guojianhua  2010 09 13-------------------
            operType=17;
            logOper = SingleLogOper.getInstance();
            this.setFunName("navdata");
            this.setModuleName("dayfinish");
            this.setRefName("000499");
            logOper.setIData(this, operType, pub);
            //--------------end----------------------------------
            
            resultStr = "confirm"; //向前台返回当日以确认净值
        } else if (doType.equalsIgnoreCase("unconfirm")) { //反确认净值
            navConfirm = new BaseNavRep();
            navConfirm.portCode = sPortCode;
            if (!YssFun.isDate(dNavDate)) {
                throw new YssException("系统在反确认净值时获取净值日期出现异常!");
            }
            navConfirm.dDate = YssFun.toDate(dNavDate);
          //====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能
            if(dEndDate!=null&&dEndDate.trim().length()>0&&YssFun.isDate(dEndDate)){
            	navConfirm.dEndDate =YssFun.toDate(dEndDate);
            }
            //==================end==============================
            navConfirm.invMgrCode = sInvMgrCode;
            navConfirm.setYssPub(pub);
            //add by guojianhua  2010 09 13-------------------
            operType=18;
            logOper = SingleLogOper.getInstance();
            this.setFunName("navdata");
            this.setModuleName("dayfinish");
            this.setRefName("000499");
            logOper.setIData(this, operType, pub);
            //--------------end----------------------------------
            try {
                navConfirm.deleteData("confirm"); //删除确认信息
            } catch (YssException e) {
                throw new YssException("系统在反确认净值出现异常!", e);
            }
            resultStr = "unconfirm"; //向前台返回当日反确认净值
        }
        return resultStr;
    }

    /**
     * 获取当日的净值表确认情况
     * BugId:MS00184 QDV4交银施罗德2009年01月09日01_B
     * @param navInfo String
     * @return String
     * @throws YssException
     */
    private String getConfirmInfo(String navInfo) throws YssException {
        String sqlStr = "";
        String resultStr = "";
        ResultSet rs = null;
     // BUG2998在【净值统计表】中选择组合时净值表不能确认  add by jiangshichao 2011.10.26 start
        //String dNavDate = navInfo.split("\b\f\b")[0]; //净值日期
        //String sPortCode = navInfo.split("\b\f\b")[1]; //组合
        //String sInvMgrCode = navInfo.split("\b\f\b")[2]; //投资经理
        String dNavDate = navInfo.split("\b\\|\b")[0]; //净值日期
        String sPortCode = navInfo.split("\b\\|\b")[1]; //组合
        String sInvMgrCode = navInfo.split("\b\\|\b")[2]; //投资经理
     // BUG2998在【净值统计表】中选择组合时净值表不能确认  add by jiangshichao 2011.10.26  end 
        if (!hasNavTotalValue(dNavDate, sPortCode, sInvMgrCode)) { //若尚未生成净值数据，则向前台传递尚未生成的信息。
            resultStr = "ungenerate"; //未生成
            return resultStr; //返回此信息。
        }
        sqlStr = "select * from " + pub.yssGetTableName("tb_Data_NavData") +
            " where FInvMgrCode = " + dbl.sqlString(sInvMgrCode) + " and FReTypeCode = 'confirm' and FPortCode = " +
            dbl.sqlString(sPortCode) + " and FNavDate = " + dbl.sqlDate(dNavDate); //获取当日的净值确认情况
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                resultStr = "unconfirm"; //若为已确认，则向前台返回unconfirm。在按钮上显示为"反确认"的样式 。
            } else {
                resultStr = "confirm"; //若为未确认，则向前台返回confirm。在按钮上显示为"确认"的样式 。
            }
        } catch (Exception ex) {
            throw new YssException("获取净值确认信息出现异常!", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return resultStr;
    }

    /**
     * 判断当日的净值是否已生成
     * BugId:MS00184 QDV4交银施罗德2009年01月09日01_B 20090122
     * @param params String
     * @return boolean
     * @throws YssException
     */
    private boolean hasNavTotalValue(String dNavDate, String sPortCode, String sInvMgrCode) throws YssException {
        boolean hasNavUnit = false;
        String sqlStr = "";
        ResultSet rs = null;
        sqlStr = "select * from " + pub.yssGetTableName("tb_Data_NavData") +
            " where FInvMgrCode = " + dbl.sqlString(sInvMgrCode) + " and FReTypeCode = 'Total' and FKeyCode = 'TotalValue' and FPortCode = " +
            dbl.sqlString(sPortCode) + " and FNavDate = " + dbl.sqlDate(dNavDate); //获取当日的资产净值情况;
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                hasNavUnit = true;
            }
        } catch (Exception ex) {
            throw new YssException("系统在判断当日是否已生成净值时出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hasNavUnit;
    }
//-------------------------------------------------------------------------------
    /**
     * 增加单位成本和涨跌
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @throws YssException
     * MS00570 QDV4华安2009年07月16日01_AB
     */
    private void updateCostAndChangeWithCost(Date dDate, String portCode,
                                             String invMgrCode) throws YssException {
        String sqlStr = null;
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_Data_NavData"));//添加行级独占锁 合并太平版本调整添加 by leeyu 20100825
        sqlStr = "update " + pub.yssGetTableName("tb_Data_NavData") +
            " set FPortUnitCost = case FSPARAMT when 0 then  0 else FPortCost/FSPARAMT end," + //成本/股数
            " FPortChangeWithCost = case FPortCost when 0 then 0 else FPortMVValue/FPortCost end, " + //浮动盈亏/成本
            //=========================by xuxuming,20090818.MS00637 QDV4华安2009年08月14日01_AB=======================
            " FUnitCost = case FSPARAMT when 0 then  0 else FCost/FSPARAMT end," + //原币成本/股数
            " FChangeWithCost = case FCost when 0 then 0 else FMVValue/FCost end " + //原币浮动盈亏/原币成本
            //=======================================================================================================
            " where " +
            "(FReTypeCode = 'Security' and FDetail = 0 and (FGradeType6 is null or FGradeType6 = ''))" +//不获取应收应付的数据来进行计算。
            " and FinvMgrCode = " + dbl.sqlString(invMgrCode) +
            " and FPortCode = " + dbl.sqlString(portCode) +
            " and FNavDate = " + dbl.sqlDate(dDate);
        try {
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("更新单位成本和涨跌出错！", e);
        }
        finally{
            dbl.endTransFinal(con, bTrans);
        }

    }
    /**
     * 更新新股行情价格
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @throws YssException
     * add by yanghaiming 20091124 MS00824 QDV4赢时胜海富通2009年11月20日01_B
     */
    private void updateNewSharePrice(Date dDate, String portCode,
    									String invMgrdode) throws YssException {
    	String sqlStr = null;
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_Data_NavData"));//添加行级独占锁 合并太平版本调整添加 by leeyu 20100825
        sqlStr = "update " + pub.yssGetTableName("tb_Data_NavData") +
        	" set FPRICE = case FSPARAMT when 0 then 0 else ROUND(FCOST/FSPARAMT,2) end" +
        	" where " +
        	"FRETYPECODE = 'Security' and  FGradeType5 is not null  and FPRICE = 0" +//xuqiji 2010-02-09 MS00927 : QDV4赢时胜上海2010年01月12日01_AB 
        	" and FinvMgrCode = " + dbl.sqlString(invMgrdode) +
            " and FPortCode = " + dbl.sqlString(portCode) +
            " and FNavDate = " + dbl.sqlDate(dDate);
        try{
        	con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        }catch (Exception e) {
        	throw new YssException("更新新股行情价格出错！", e);
        }
        finally{
            dbl.endTransFinal(con, bTrans);
        }
    }
    
    /***
     * 获取是否显示B股个性化界面通用参数
     * @return String
     * @throws YssException 
     */
    private String isPerInterface() throws YssException{
    	CtlPubPara ctlPubPara = new CtlPubPara();
    	ctlPubPara.setYssPub(pub);
    	String result = ctlPubPara.getPerInterface();
    	return result;
    }
	/**
     * 判断日期段内净值是否已生成
     * BugId:MS01068 QDV4赢时胜上海2010年4月06日01_A  20100408 
     * @author yanghaiming
     * @param params String
     * @return String
     * @throws YssException
     */
    private boolean hasNavTotalValue(String dNavDate, String dEndDate, String sPortCode, String sInvMgrCode) throws YssException {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dDate = YssFun.toDate(dNavDate);
        boolean flag = false;
        while(dDate.compareTo(YssFun.toDate(dEndDate))<=0){
        	if(hasNavTotalValue(format.format(dDate), sPortCode, sInvMgrCode)){
        		flag = true;
        	}
        	dDate = YssFun.addDay(dDate, 1);
        }
		return flag;
    }
    
    //add by fangjiang 2011.10.26 STORY #1589  分级处理
    public void dealMultiClass() throws YssException, SQLException {
    	ParaWithPubBean pubBean = new ParaWithPubBean();
        pubBean.setYssPub(pub);
        ResultSet rs = null;
        String paraId = "";
        ArrayList curyCode = new ArrayList();
        ArrayList managelFormula = new ArrayList();
        ArrayList trusteeFormula = new ArrayList();
        //add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
        ArrayList faFormula = new ArrayList();
        ArrayList stFormula = new ArrayList();
        ArrayList fxjFormula = new ArrayList();
        double scale = 0.0;
        double manageFee = 0.0;
        double trusteeFee = 0.0;
		//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
        double faFee = 0.0;
        double stFee = 0.0;
        double fxjFee = 0.0;
        double netValueBefore = 0.0;
        double netValueTmp = 0.0;
        double netValue = 0.0;
        double baseRate = 0.0;
        double portRate = 0.0;
        double classNetValue = 0.0;
        double amount = 0.0;
        double unitNet = 0.0;
        double gzzz = 0.0;
        double gzzzTmp = 0.0;
        double classGzzz = 0.0;
        double hdsy = 0.0;
        double hdsyTmp = 0.0;
        double classHdsy = 0.0;
        String strSql = "";
        PreparedStatement pst = null;
        boolean bTrans = false; 
        Connection conn = dbl.loadConnection();
        TaTradeBean ta = new TaTradeBean();
        ta.setYssPub(this.pub);
        CtlPubPara pubPara=new CtlPubPara();
        pubPara.setYssPub(this.pub);
        String gzzzState="";//组合分级的估值增值的计算方式 add by zhouwei 20120313
        try {        
        	gzzzState=pubPara.getClsGzzzWay(this.portCode);
        	strSql = " delete from " + pub.yssGetTableName("Tb_Data_MultiClassNet") +
		             " where FPortCode = " + dbl.sqlString(this.portCode) +
		             " and FNAVDate = " + dbl.sqlDate(dDate);      
		    dbl.executeSql(strSql);
		   
		    if(!ta.isMultiClass(this.portCode)){
				return;
			}
        	
        	this.flag = ta.getAccWayState(this.portCode);
        	if(this.flag == 0){
        		rs = pubBean.getResultSetByLike("CtlFeeCalcClass", "SelPort", this.portCode+"%", null);
          	    while(rs.next()){
          	    	paraId += rs.getString("FParaId") + ",";
          	    }
          	    if(paraId.length() > 1){
          	    	paraId = paraId.substring(0, paraId.length()-1);
          	    }
          	    dbl.closeResultSetFinal(rs);
          	    rs = pubBean.getResultSetByLike("CtlFeeCalcClass", paraId);
          	    while(rs.next()){
          	    	if("CuryCode".equalsIgnoreCase(rs.getString("FCtlCode"))){
          	    		curyCode.add(rs.getString("FCtlValue").split("[|]")[0]);
          	    	}else if("ManagelFormula".equalsIgnoreCase(rs.getString("FCtlCode"))){
          	    		managelFormula.add(rs.getString("FCtlValue").split("[|]")[0]);
          	    	}else if("TrusteeFormula".equalsIgnoreCase(rs.getString("FCtlCode"))){
          	    		trusteeFormula.add(rs.getString("FCtlValue").split("[|]")[0]);
          	    	}
          	    	//---add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A start---//
          	    	else if("FA_Formula".equalsIgnoreCase(rs.getString("FCtlCode"))){
          	    		if(rs.getString("FCtlValue").split("[|]").length >= 2){
          	    			faFormula.add(rs.getString("FCtlValue").split("[|]")[0]);
          	    		}
          	    	}
          	    	//---add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A end---//
          	    }  
        	}else if(this.flag == 1){ //fj 按照基准资产份额分级
        		strSql = " select a.fivpaycatcode, a.FPortCode, a.FPortClsCode, a.fariexpcode from " +
        			     pub.yssGetTableName("tb_para_investpay") + " a join (select fivpaycatcode,FPortCode, FPortClsCode, " +
        			     " max(FStartDate) as FStartDate from " + pub.yssGetTableName("tb_para_investpay") +
        			     " where FCheckState = 1 and length(trim(FPortClsCode))>0 and FStartDate <= " + dbl.sqlDate(this.dDate) +
        			     " group by fivpaycatcode, FPortCode, FPortClsCode) b " +    			    
        			     " on a.fivpaycatcode = b.fivpaycatcode and a.FStartDate = b.FStartDate and a.FPortClsCode = b.FPortClsCode " +
        			     //edit by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A 添加 IV103
        			     " where a.FCheckState = 1 and a.fivpaycatcode in ('IV001','IV002','IV103','YSS_STF','YSS_FXJ')  order by a.FPortClsCode asc,a.Fivpaycatcode";       		
        		rs = dbl.openResultSet(strSql);
        		while(rs.next()){
        			curyCode.add(rs.getString("FPortClsCode"));
        			if("IV001".equalsIgnoreCase(rs.getString("fivpaycatcode"))){
        				managelFormula.add(rs.getString("fariexpcode"));
        			}else if("IV002".equalsIgnoreCase(rs.getString("fivpaycatcode"))){
        				trusteeFormula.add(rs.getString("fariexpcode"));
        			}else if("YSS_STF".equalsIgnoreCase(rs.getString("fivpaycatcode"))){
        				stFormula.add(rs.getString("fariexpcode"));
        			}
        			//---add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A start---//
        			else if("IV103".equalsIgnoreCase(rs.getString("fivpaycatcode"))){
        				faFormula.add(rs.getString("fariexpcode"));
        			}
        			//---add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A end---//
        			else if("YSS_FXJ".equalsIgnoreCase(rs.getString("fivpaycatcode"))){
        				fxjFormula.add(rs.getString("fariexpcode"));
        			}	
        		}
        		curyCode = this.removeDuplicateWithOrder(curyCode);
        	}else if(this.flag == 2){ //add by fangjiang 2012.05.02 stroy 2565
        		this.dealMClass();
        		return;
        	}        	
        	//add by huangqirong 2012-08-30 story #2782
        	else if(this.flag == 3 || this.flag == 4){
        		this.dealClassNetValue();
        		return;
        	}
        	//---end---
        	//杠杆分级份额折算，产生Ta交易数据  add by yeshenghong 20130509  story3759
        	else if(this.flag==5)
        	{
        		this.dealLeverGradeNetValue();
        		return;
        	}
        	//---end---   add by yeshenghong 20130509  story3759
      	    strSql = " insert into " + pub.yssGetTableName("Tb_Data_MultiClassNet") +
                     " (FNAVDate,FPortCode,FType,FPortCuryCode,FCuryCode,FNetValueBeforeFee,FManageFee,FTrusteeFee," +
                     //edit by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A 添加 FFEFEE
                     " FNetValue,FClassNetValue,FCheckState,FCreator,FCreateTime,FSTFee,FFXJFee,FFAFEE) " + //fj
                     //edit by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A 添加 FFEFEE
                     " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "; //fj
            pst = conn.prepareStatement(strSql);
      	    for(int i=0; i<curyCode.size(); i++){ //这里的curyCode是分级代码,目前博时的分级代码是币种，嘉实（RQFII）的分级代码是分级组合代码
  	    		//基础汇率
      	    	baseRate = this.getSettingOper().getCuryRate(
	  					  this.dDate, 
	  					  (String)curyCode.get(i), 
	  					  this.portCode, 
	  					  YssOperCons.YSS_RATE_BASE);
      	    	//组合汇率
      	    	portRate = this.getSettingOper().getCuryRate(
	  					  this.dDate, 
	  					  "", 
	  					  this.portCode, 
	  					  YssOperCons.YSS_RATE_PORT);  
      	    	if(i < curyCode.size()-1){    
      	    		//获取各Class的净值占比
  	    			scale = this.getScale((String)curyCode.get(i));		  	    		   
  	    			//各Class的费前资产净值（本外币）
	      	    	netValueBefore = YssD.round
	      	    	                 (
      	    	                		 YssD.mul
	   	      	    	                 (   //【STORY #2686 要求系统能做针对RQFII的分红，以及红利转投 】 add by jsc 20120618
	   	      	    	                     //分级的分红金额由各个分级的费前净值中刨去，而不应该在费前总净值中刨去。所以费前总净值这边把多减的分级分红金额加回去。【针对份额拆分】
	   	  	    	                		 flag==1?YssD.add(getNetValue(this.dDate, "01"), getClassDiviendMoney("")):getNetValue(this.dDate, "01"),
	   	  	    	                		 scale
	   	    	                		 ) , 
      	    	                		 12
  	    	                		 );
	      	        //把各个分级的费前净值中刨去各个分级分红金额       		 
	      	    	netValueBefore =  flag==1?YssD.sub(netValueBefore, getClassDiviendMoney((String)curyCode.get(i))):netValueBefore; 
	      	    	netValueTmp += netValueBefore;
	      	    	//各Class的估值增值（本外币）
	      	    	if(gzzzState.equalsIgnoreCase("values")){//估值系统的算法---中银宝成   edit by zhouwei 20120313
	      	    		gzzz = YssD.round
	 	      	    		   (
	 	      	    			   YssD.mul
	 	      	    			   (
	       	    					   getNetValue(this.dDate, "03"), 
	       	    					   scale
	   	    					   ), 
	       	    				   12
	   	    				   );
	      	    	}else{//按照财务系统的算法
	      	    	//获取组合分级的前一天的余额  原币转成本位币
	      	    		double clsGzzz=YssD.round(
	      	    				YssD.div(
		      	    					 YssD.mul(
		      	    					     getClsGzzzValues((String)curyCode.get(i),YssFun.addDay(this.dDate, -1)),
		      	    					     baseRate
		    	    					   ),
		    	    					   portRate
	      	    						),
	      	    					12);
	      	    		gzzz = YssD.round
		 	      	    		   (
		 	      	    			   YssD.add(
			 	      	    			   YssD.mul
			 	      	    			   (
			 	      	    				  //获取当天的估值增值的发生额	=当天的余额-前一天的余额	 	   
			       	    					   YssD.sub(getNetValue(this.dDate, "03"),getNetValue(YssFun.addDay(this.dDate,-1), "03")), 
			       	    					   scale
			   	    					   ), 
			   	    					  clsGzzz
		       	    					  ),
		       	    				   12
		   	    				   );
	      	    	}	      	    	
	      	    	gzzzTmp += gzzz;
	      	        //各Class的汇兑损益（本外币）
	      	    	hdsy = YssD.round
		   	    		   (
		   	    			   YssD.mul
		   	    			   (
		    					   getNetValue(this.dDate, "04"), 
		    					   scale
		   					   ), 
		    				   12
		   				   );
	      	    	hdsyTmp += hdsy;
      	    	}else{ //最后一个Class钆差
      	    		//最后一个Class的费前资产净值（本外币）
      	    		netValueBefore = YssD.sub
      	    		                 (
  	    		                		 getNetValue(this.dDate, "01"),  
  	    		                		 netValueTmp
    		                		 );
      	    		//最后一个Class的估值增值（本外币）
      	    		gzzz = YssD.sub
		                   (
		                       getNetValue(this.dDate, "03"),  
                		       gzzzTmp
	                	   );
      	    		//最后一个Class的汇兑损益（本外币）
      	    		hdsy = YssD.sub
	                       (
	                           getNetValue(this.dDate, "04"),  
         		               hdsyTmp
             	           );
      	    	}
      	    	//各Class的管理费（本外币）
      	    	if(managelFormula.size() > 1){
      	    		manageFee = this.getInvestFee(netValueBefore, (String)managelFormula.get(i), "IV001", (String)curyCode.get(i));  
      	    	}
      	    	//各Class的托管费（本外币）
      	    	if(trusteeFormula.size() > 1){
      	    		trusteeFee = this.getInvestFee(netValueBefore, (String)trusteeFormula.get(i), "IV002", (String)curyCode.get(i));
      	    	}
      	    	//---add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A start---//
      	    	//各Class的FA_Admin费（本外币）
      	    	if(faFormula.size() > 1){
      	    		faFee = this.getInvestFee(netValueBefore, (String)faFormula.get(i), "IV103", (String)curyCode.get(i));
      	    	}
      	    	//---add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A end---//
      	    	
      	    	//各Class的受托费（信托费），RQFII（本外币）
      	    	if(this.flag == 1){ //fj 
      	    		if(stFormula.size() > 1){
      	    			stFee = this.getInvestFee(netValueBefore, (String)stFormula.get(i), "YSS_STF", (String)curyCode.get(i));
      	    		}
      	    	}
      	    	if(fxjFormula.size() > 1){
      	    		fxjFee = this.getInvestFee(netValueBefore, (String)fxjFormula.get(i), "YSS_FXJ", (String)curyCode.get(i));
  	    		}
      	    	//各Class的费后资产净值（本外币）
      	    	//edit by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A 添加 faFee
      	    	netValue = YssD.sub(netValueBefore, manageFee, trusteeFee, stFee, fxjFee, faFee);      	    	
      	    	//各Class的费后资产净值
      	    	classNetValue = YssD.round
      	    					(
  	    							YssD.div
  				                	(
  				                		YssD.mul
  				                		(
  				                			netValue, 
  				                			portRate
  				                		), 
  				                		baseRate
  				            		), 
  	    							12
    							);              	               
      	    	pst.setDate(1, YssFun.toSqlDate(dDate));
                pst.setString(2, this.portCode);
                pst.setString(3, "01"); //标识－资产净值
                pst.setString(4, getPortCuryCode());
                pst.setString(5, (String)curyCode.get(i));
                pst.setDouble(6, netValueBefore);
                pst.setDouble(7, manageFee);
                pst.setDouble(8, trusteeFee);
                pst.setDouble(9, netValue);
                pst.setDouble(10, classNetValue);
                pst.setInt(11, 1);
                pst.setString(12, pub.getUserCode());
                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
                pst.setDouble(14, stFee); //fj
                pst.setDouble(15, fxjFee); //fj
                pst.setDouble(16, faFee);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
                pst.executeUpdate();
                
                //各Class的库存数量
                amount = getTaStock((String)curyCode.get(i), this.dDate, 0);
                //各Class的单位净值
                unitNet = YssD.round(YssD.div(YssD.round(classNetValue, 2), amount), 12);
                
                pst.setDate(1, YssFun.toSqlDate(dDate));
                pst.setString(2, this.portCode);
                pst.setString(3, "05"); //标识－实收资本
                pst.setString(4, getPortCuryCode());
                pst.setString(5, (String)curyCode.get(i));
                pst.setDouble(6, 0.0);
                pst.setDouble(7, 0.0);
                pst.setDouble(8, 0.0);
                pst.setDouble(9, 0.0);
                pst.setDouble(10, amount);
                pst.setInt(11, 1);
                pst.setString(12, pub.getUserCode());
                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
                pst.setDouble(14, 0.0); //fj
                pst.setDouble(15, 0.0); //fj
                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
                
                pst.executeUpdate();
                
                pst.setDate(1, YssFun.toSqlDate(dDate));
                pst.setString(2, this.portCode);
                pst.setString(3, "02"); //标识－单位净值
                pst.setString(4, getPortCuryCode());
                pst.setString(5, (String)curyCode.get(i));
                pst.setDouble(6, 0.0);
                pst.setDouble(7, 0.0);
                pst.setDouble(8, 0.0);
                pst.setDouble(9, 0.0);
                pst.setDouble(10, unitNet);
                pst.setInt(11, 1);
                pst.setString(12, pub.getUserCode());
                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
                pst.setDouble(14, 0.0); //fj
                pst.setDouble(15, 0.0); //fj
                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
                pst.executeUpdate();
                
                //modify by fangjiang 2011.12.27 BUG 3490
                //各Class的估值增值
                classGzzz = YssD.round
                            (
                        		YssD.div
			            		(
			                		YssD.mul
			                		(
		                				gzzz, 
		                				portRate
			                		), 
			                		baseRate
			            		),
			            		12
		            		); 
                //各Class的汇兑损益
                classHdsy = YssD.round
                			(
            					YssD.div
    		            		(
    		                		YssD.mul
    		                		(
    	                				hdsy, 
    	                				portRate
    		                		), 
    		                		baseRate
    		            		), 
    		            		12
		            		); 
                //-------------end BUG 3490--------------
                
                pst.setDate(1, YssFun.toSqlDate(dDate));
                pst.setString(2, this.portCode);
                pst.setString(3, "03"); //标识－估值增值
                pst.setString(4, getPortCuryCode());
                pst.setString(5, (String)curyCode.get(i));
                pst.setDouble(6, 0.0);
                pst.setDouble(7, 0.0);
                pst.setDouble(8, 0.0);
                pst.setDouble(9, gzzz);
                pst.setDouble(10, classGzzz);
                pst.setInt(11, 1);
                pst.setString(12, pub.getUserCode());
                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
                pst.setDouble(14, 0.0); //fj
                pst.setDouble(15, 0.0); //fj
                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
                pst.executeUpdate();
                
                pst.setDate(1, YssFun.toSqlDate(dDate));
                pst.setString(2, this.portCode);
                pst.setString(3, "04"); //标识－汇兑损益
                pst.setString(4, getPortCuryCode());
                pst.setString(5, (String)curyCode.get(i));
                pst.setDouble(6, 0.0);
                pst.setDouble(7, 0.0);
                pst.setDouble(8, 0.0);
                pst.setDouble(9, hdsy);
                pst.setDouble(10, classHdsy);
                pst.setInt(11, 1);
                pst.setString(12, pub.getUserCode());
                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
                pst.setDouble(14, 0.0); //fj
                pst.setDouble(15, 0.0); //fj
                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
                pst.executeUpdate();
                
                pst.setDate(1, YssFun.toSqlDate(dDate));
                pst.setString(2, this.portCode);
                pst.setString(3, "06"); //标识－损益平准金未实现
                pst.setString(4, getPortCuryCode());
                pst.setString(5, (String)curyCode.get(i));
                pst.setDouble(6, 0.0);
                pst.setDouble(7, 0.0);
                pst.setDouble(8, 0.0);
                pst.setDouble(9, 0.0);
                pst.setDouble(10, getTaStock((String)curyCode.get(i), this.dDate, 1));
                pst.setInt(11, 1);
                pst.setString(12, pub.getUserCode());
                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
                pst.setDouble(14, 0.0); //fj
                pst.setDouble(15, 0.0); //fj
                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
                pst.executeUpdate();
                
                pst.setDate(1, YssFun.toSqlDate(dDate));
                pst.setString(2, this.portCode);
                pst.setString(3, "07"); //标识－损益平准金已实现
                pst.setString(4, getPortCuryCode());
                pst.setString(5, (String)curyCode.get(i));
                pst.setDouble(6, 0.0);
                pst.setDouble(7, 0.0);
                pst.setDouble(8, 0.0);
                pst.setDouble(9, 0.0);
                pst.setDouble(10, getTaStock((String)curyCode.get(i), this.dDate, 2));
                pst.setInt(11, 1);
                pst.setString(12, pub.getUserCode());
                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
                pst.setDouble(14, 0.0); //fj
                pst.setDouble(15, 0.0); //fj
                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
                pst.executeUpdate();
                
                if(this.flag == 0){
	                pst.setDate(1, YssFun.toSqlDate(dDate));
	                pst.setString(2, this.portCode);
	                pst.setString(3, "08"); //累计净值
	                pst.setString(4, getPortCuryCode());
	                pst.setString(5, (String)curyCode.get(i));
	                pst.setDouble(6, 0.0);
	                pst.setDouble(7, 0.0);
	                pst.setDouble(8, 0.0);
	                pst.setDouble(9, 0.0);
	                pst.setDouble(10, YssD.add(unitNet, getAccumulateDivided(this.dDate, (String)curyCode.get(i))));
	                pst.setInt(11, 1);
	                pst.setString(12, pub.getUserCode());
	                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
	                pst.setDouble(14, 0.0); //fj
	                pst.setDouble(15, 0.0); //fj
	                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
	                pst.executeUpdate();
	                
	                pst.setDate(1, YssFun.toSqlDate(dDate));
	                pst.setString(2, this.portCode);
	                pst.setString(3, "09"); //日净值增长率
	                pst.setString(4, getPortCuryCode());
	                pst.setString(5, (String)curyCode.get(i));
	                pst.setDouble(6, 0.0);
	                pst.setDouble(7, 0.0);
	                pst.setDouble(8, 0.0);
	                pst.setDouble(9, 0.0);
	                pst.setDouble(10, getDayScale(this.dDate, (String)curyCode.get(i), unitNet));
	                pst.setInt(11, 1);
	                pst.setString(12, pub.getUserCode());
	                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
	                pst.setDouble(14, 0.0); //fj
	                pst.setDouble(15, 0.0); //fj
	                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
	                pst.executeUpdate();
	                
	                pst.setDate(1, YssFun.toSqlDate(dDate));
	                pst.setString(2, this.portCode);
	                pst.setString(3, "10"); //本期净值增长率
	                pst.setString(4, getPortCuryCode());
	                pst.setString(5, (String)curyCode.get(i));
	                pst.setDouble(6, 0.0);
	                pst.setDouble(7, 0.0);
	                pst.setDouble(8, 0.0);
	                pst.setDouble(9, 0.0);
	                pst.setDouble(10, getThisPeriodScale(this.dDate, (String)curyCode.get(i), unitNet));
	                pst.setInt(11, 1);
	                pst.setString(12, pub.getUserCode());
	                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
	                pst.setDouble(14, 0.0); //fj
	                pst.setDouble(15, 0.0); //fj
	                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
	                pst.executeUpdate();
	                
	                pst.setDate(1, YssFun.toSqlDate(dDate));
	                pst.setString(2, this.portCode);
	                pst.setString(3, "11"); //累计净值增长率
	                pst.setString(4, getPortCuryCode());
	                pst.setString(5, (String)curyCode.get(i));
	                pst.setDouble(6, 0.0);
	                pst.setDouble(7, 0.0);
	                pst.setDouble(8, 0.0);
	                pst.setDouble(9, 0.0);
	                pst.setDouble(10, getAccumulateScale(this.dDate, (String)curyCode.get(i), unitNet, 3, true));
	                pst.setInt(11, 1);
	                pst.setString(12, pub.getUserCode());
	                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
	                pst.setDouble(14, 0.0); //fj
	                pst.setDouble(15, 0.0); //fj
	                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
	                pst.executeUpdate();    
                }else if(this.flag == 1){
                	
                	pst.setDate(1, YssFun.toSqlDate(dDate));
	                pst.setString(2, this.portCode);
	                pst.setString(3, "08"); //累计净值
	                pst.setString(4, getPortCuryCode());
	                pst.setString(5, (String)curyCode.get(i));
	                pst.setDouble(6, 0.0);
	                pst.setDouble(7, 0.0);
	                pst.setDouble(8, 0.0);
	                pst.setDouble(9, 0.0);
	                pst.setDouble(10, YssD.add(unitNet, getSumDivided(this.dDate, (String)curyCode.get(i))));
	                pst.setInt(11, 1);
	                pst.setString(12, pub.getUserCode());
	                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
	                pst.setDouble(14, 0.0); //fj
	                pst.setDouble(15, 0.0); //fj
	                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
	                pst.executeUpdate();
                	
                	
                	
                	pst.setDate(1, YssFun.toSqlDate(dDate));
	                pst.setString(2, this.portCode);
	                pst.setString(3, "12"); //分级组合折算后的库存数量
	                pst.setString(4, getPortCuryCode());
	                pst.setString(5, (String)curyCode.get(i));
	                pst.setDouble(6, 0.0);
	                pst.setDouble(7, 0.0);
	                pst.setDouble(8, 0.0);
	                pst.setDouble(9, 0.0);
	                pst.setDouble(10, getTaStock((String)curyCode.get(i), this.dDate, 3));
	                pst.setInt(11, 1);
	                pst.setString(12, pub.getUserCode());
	                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
	                pst.setDouble(14, 0.0); //fj
	                pst.setDouble(15, 0.0); //fj
	                pst.setDouble(16, 0.0);//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
	                pst.executeUpdate();
                }
      	    }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);   	  
	    } catch (YssException e) {
	    	throw new YssException(e.getMessage());
	    } finally{
		    dbl.closeResultSetFinal(rs);
		    dbl.endTransFinal(conn, bTrans);
	    }	   
    }
    
    
    /**
     *  add by jsc 20120618
     *  获取各级分级分红金额
     * @param portClsCode 分级代码
     * @return
     * @throws YssException
     */
    private double getClassDiviendMoney(String FPORTCLSCODE)throws YssException{
    	
    	double dDiviedMoney=0;
    	ResultSet rs = null;
    	StringBuffer queryBuf = new StringBuffer();
    	try{
//    		queryBuf.append(" select sum(fportcurymoney) as fportcurymoney from  ");
//    		queryBuf.append(" (select frelanum,fsubtsftypecode, case when  ").append(" FTRANSDATE !=").append(dbl.sqlDate(this.dDate)).append(" and fsubtsftypecode='03FD' then - fportcurymoney * finout else fportcurymoney * finout end as fportcurymoney ");
//    		queryBuf.append(" from  ").append(pub.yssGetTableName("tb_data_cashpayrec")).append(" where fcheckstate=1 and frelatype='FundDividend' and fportcode=").append(dbl.sqlString(this.portCode)).append(" ) a  ");
//    		queryBuf.append(" (select * ");
//    		queryBuf.append(" from ").append(pub.yssGetTableName("tb_ta_trade")).append(" b ");
//    		queryBuf.append(" where fportcode = ").append(dbl.sqlString(this.portCode));
//    		queryBuf.append(" and fselltype ='03' and fcheckstate = 1 ");
//    		queryBuf.append(" and ftradedate = ").append(dbl.sqlDate(this.dDate));
//    		if(FPORTCLSCODE.length()!=0){
//    			queryBuf.append(" and fportclscode=").append(dbl.sqlString(FPORTCLSCODE));
//    		}
//    		queryBuf.append(" ) ");
    		
    		/**
    		 * 获取分红确认日期的各分级分红数据
    		 */
    		queryBuf.append(" select nvl(sum(ta.FSELLMONEY*rate.fbaserate),0) as FSELLMONEY from ");
    		queryBuf.append(" (select a.FSELLMONEY,a.fcashacccode,ca.fcurycode from  ");
    		//queryBuf.append(" (select case when FSELLTYPE='08' ").append(" and nvl(length(").append(dbl.sqlString(FPORTCLSCODE.trim())).append("),0)=0");
    		//queryBuf.append(" then -FSELLMONEY else FSELLMONEY end as FSELLMONEY,fcashacccode from  ");
    		queryBuf.append(" (select  FSELLMONEY,fcashacccode from ");
    		queryBuf.append(pub.yssGetTableName("tb_ta_trade")).append(" where fcheckstate=1 and ");
    		//取分红确认日
    		queryBuf.append(" fportcode= ").append(dbl.sqlString(this.portCode)).append(" and FCONFIMDATE= ").append(dbl.sqlDate(this.dDate));
    		//如果有分级代码则获取各分级分红数据，如果没有分级代码则获取各分级总金额
    		if(FPORTCLSCODE.trim().length()>0){
    			queryBuf.append(" and fportclscode= ").append(dbl.sqlString(FPORTCLSCODE));
    		}
    		//类型为分红
    		queryBuf.append(" and FSELLTYPE in('03') ) a left join ");
    		queryBuf.append(" (select ca1.fcurycode,ca1.fcashacccode from  ");
    		queryBuf.append(" ((select fcashacccode,fcurycode,FSTARTDATE from ").append(pub.yssGetTableName("tb_para_cashaccount")).append(" where fcheckstate=1)ca1");
    		queryBuf.append(" join ");
    		queryBuf.append(" (select fcashacccode,max(FSTARTDATE) as FSTARTDATE from ").append(pub.yssGetTableName("tb_para_cashaccount")).append(" where fcheckstate=1 group by fcashacccode,FSTARTDATE)ca2 ");
    		queryBuf.append(" on ca1.fcashacccode = ca2.fcashacccode and ca1.FSTARTDATE=ca2.FSTARTDATE))ca ");
    		queryBuf.append(" on a.fcashacccode =ca.fcashacccode)ta ");
    		queryBuf.append(" left join  ");
    		queryBuf.append(" (select fcurycode,fbaserate from ").append(pub.yssGetTableName("tb_data_valrate ")).append(" where fportcode=").append(dbl.sqlString(this.portCode));
    		queryBuf.append(" and fvaldate=").append(dbl.sqlDate(this.dDate)).append(" and fcheckstate=1)rate  ");
    		queryBuf.append(" on ta.fcurycode = rate.fcurycode ");
    		
    		rs = dbl.openResultSet(queryBuf.toString());
    		while(rs.next()){
    			dDiviedMoney = rs.getDouble("FSELLMONEY");
    		}
    		return dDiviedMoney;
    	}catch(Exception e ){
    		throw new YssException("获取分级基金分红数据出错... ...");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		queryBuf.setLength(0);
    	}
    }
    /**add by zhouwei 20120313 
     * 获取组合分级估值增值的余额
     * @param portClsCode
     * @param date
     * @param scale
     * @return
     * @throws YssException
     */
    private double getClsGzzzValues(String portClsCode,Date date) throws YssException{
    	double gzzz=0;
    	ResultSet rs=null;
    	String sql="";
    	try{
    		//获取组合分级估值增值的余额
    		sql="select * from "+pub.yssGetTableName("TB_DATA_MULTICLASSNET")
    		   +" where FTYPE='03' and FCURYCODE="+dbl.sqlString(portClsCode)
    		   +" and FPORTCODE="+dbl.sqlString(this.portCode)+" and FNAVDATE="+dbl.sqlDate(date);
    		rs=dbl.openResultSet(sql);
    		if(rs.next()){
    			gzzz=rs.getDouble("FCLASSNETVALUE");
    		}
    	}catch (Exception e) {
			throw new YssException("获取组合分级"+portClsCode+"的估值增值余额出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return gzzz;
    }
    //获得资产净值、估值增值、 汇兑损益
    private double getNetValue(Date d, String type) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	double result = 0.0;
        sqlStr = " select FPortNetValue from " + pub.yssGetTableName("tb_data_netValue") +
                 " where FType = " + dbl.sqlString(type) + " and FPortCode = " + 
                 dbl.sqlString(this.portCode) + " and FNavDate = " + dbl.sqlDate(d);       
        try{
        	rs = dbl.queryByPreparedStatement(sqlStr);
        	while(rs.next()){
        		result = rs.getDouble("FPortNetValue");
        	}          	
        }catch (Exception e) {
        	throw new YssException(e.getMessage());
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        return result;
    }
    
    //获得各Class的资产净值
    private double getNetValueForClass(String curyCode, Date d) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	double result = 0.0;
        sqlStr = " select FNetValue from " + pub.yssGetTableName("tb_data_MultiClassNet") +
                 " where FType = '01' and FCuryCode = " + dbl.sqlString(curyCode) + 
                 " and FPortCode = " + dbl.sqlString(this.portCode) + " and FNavDate = " + dbl.sqlDate(d);       
        try{
        	rs = dbl.queryByPreparedStatement(sqlStr);
        	while(rs.next()){
        		result = rs.getDouble("FNetValue");
        	}        	
        }catch (Exception e) {
        	throw new YssException(e.getMessage());
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        return result;
    }
    
    //获得申购的本位币金额
    private double getBSG(String curyCode, Date d) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	double result = 0.0;
        sqlStr = " select sum((FSettleMoney * FBaseCuryRate)/FPortCuryRate) as money from " + pub.yssGetTableName("Tb_TA_Trade") +
                 " where FCheckState = 1 and FSellType in ('00','01') and FPortCode = " + dbl.sqlString(this.portCode) + " and FConfimDate = " + dbl.sqlDate(d);
        if(curyCode.length() > 1){
        	sqlStr = sqlStr + " and FCuryCode = " + dbl.sqlString(curyCode);
        }
        try{
        	rs = dbl.queryByPreparedStatement(sqlStr);
        	while(rs.next()){
        		result = rs.getDouble("money");
        	}           	
        }catch (Exception e) {
        	throw new YssException(e.getMessage());
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        return result;
    }
        
    //获得赎回的本位币金额
    private double getBSH(String curyCode, Date d) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	double result = 0.0;
    	sqlStr = " select sum((FSettleMoney * FBaseCuryRate)/FPortCuryRate) as money from " + pub.yssGetTableName("Tb_TA_Trade") +
    			 " where FCheckState = 1 and FSellType = '02' and FPortCode = " + dbl.sqlString(this.portCode) + " and FConfimDate = " + dbl.sqlDate(d);
		if(curyCode.length() > 1){
			sqlStr = sqlStr + " and FCuryCode = " + dbl.sqlString(curyCode);
		}
		try{
			rs = dbl.queryByPreparedStatement(sqlStr);
			while(rs.next()){
				result = rs.getDouble("money");
        	}  			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
        return result;
    }
    
    //获得各Class的实收资本、损益平准金未实现、损益平准金已实现 modify huangqirong 2012-07-09 story #2727
    public double getTaStock(String curyCode, Date d, int flag) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	double result = 0.0;
    	sqlStr = " select * from " + pub.yssGetTableName("Tb_Stock_Ta") +
    			 " where FCheckState = 1 and (case when " + this.flag + "=0 then FCuryCode else FPortClsCode end) "+ 
    			 " = " + dbl.sqlString(curyCode) + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FStorageDate = " + dbl.sqlDate(d);    	
		try{
			rs = dbl.queryByPreparedStatement(sqlStr);
			while(rs.next()){
				if(flag == 0){
					result = rs.getDouble("FStorageAmount");
				}else if(flag == 1){
					result = rs.getDouble("FCuryUnPl");
				}else if(flag == 2){
					result = rs.getDouble("FCuryPl");
				}else if(flag == 3){	//fj
					result = rs.getDouble("FfjzhzsStorageAmount");
				}
        	}    					
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
        return result;
    }
    
    //获得各分级折算后的库存数量之和
    private double sumTaZSStock(Date d) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	double result = 0.0;
    	sqlStr = " select sum(FfjzhzsStorageAmount) as FfjzhzsStorageAmount from " + pub.yssGetTableName("Tb_Stock_Ta") +
    			 " where FCheckState = 1 " + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FStorageDate = " + dbl.sqlDate(d);    	
		try{
			rs = dbl.queryByPreparedStatement(sqlStr);
			while(rs.next()){
				result = rs.getDouble("FfjzhzsStorageAmount");
        	}    					
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
        return result;
    }
    
    //根据spring调用代码返回beanid
    private String getBeanIdBySpring(String id) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	String result = "";
    	sqlStr = " select FBeanId from TB_FUN_SPINGINVOKE where FSICode = " + dbl.sqlString(id);  	
		try{
			rs = dbl.queryByPreparedStatement(sqlStr);
			while(rs.next()){
				result = rs.getString("FBeanId");	
        	} 								
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
        return result;
    }
    
    //根据组合获得本位币
    private String getPortCuryCode() throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	String result = "";
    	sqlStr = " select FPortCury from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portCode);  	
		try{
			rs = dbl.queryByPreparedStatement(sqlStr);
			while(rs.next()){
				result = rs.getString("FPortCury");	
        	} 								
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
        return result;
    }
    
    //累计分红
    private double getAccumulateDivided(Date dEndDate, String curyCode) throws YssException {
    	String sqlStr = null;
		ResultSet rs = null;
		double result = 0.0;
		try {
			sqlStr = "select sum(FSellPrice) as FSellPrice from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 and FCuryCode = " + dbl.sqlString(curyCode)
					+ " and FPortCode = " + dbl.sqlString(this.portCode) + " and FSellType = '03'"; 
			if (dEndDate != null&&!YssFun.formatDate(dEndDate).equals("9998-12-31")) {
				sqlStr += " and FConfimDate <= " + dbl.sqlDate(dEndDate);
			}
			rs = dbl.queryByPreparedStatement(sqlStr);
			if (rs.next()) {
				result = rs.getDouble("FSellPrice");
			}
		} catch (Exception e) {
			throw new YssException("获取累计净值出现异常！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return result;
    }
    
    //累计分红 modify huangqirong 2012-07-09 story #2727
    public double getSumDivided(Date dEndDate, String clsPortCode) throws YssException {
    	String sqlStr = null;
		ResultSet rs = null;
		double result = 0.0;
		try {
			sqlStr = "select sum(FSellPrice) as FSellPrice from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 and FPortClsCode = " + dbl.sqlString(clsPortCode)
					+ " and FPortCode = " + dbl.sqlString(this.portCode) + " and FSellType = '03'"; 
			if (dEndDate != null&&!YssFun.formatDate(dEndDate).equals("9998-12-31")) {
				sqlStr += " and FConfimDate <= " + dbl.sqlDate(dEndDate);
			}
			rs = dbl.queryByPreparedStatement(sqlStr);
			if (rs.next()) {
				result = rs.getDouble("FSellPrice");
			}
		} catch (Exception e) {
			throw new YssException("获取累计净值出现异常！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return result;
    }
    
    //日净值增长率
    private double getDayScale(Date d, String curyCode, double unitValue) throws YssException {
    	String sqlStr = null;
		ResultSet rs = null;
		double today = 0.0;
		double yesterday = 0.0;
		double result = 0.0;
		double price = 0.0;
		try {
			sqlStr = " select FSellPrice, FConfimDate, FPortCode from " + pub.yssGetTableName("TB_TA_TRADE") +
		             " where FCheckState = 1 and FConfimDate = " + dbl.sqlDate(d) + " and FSellType = '03' and FCuryCode = " + dbl.sqlString(curyCode);
			rs = dbl.queryByPreparedStatement(sqlStr);
			if (rs.next()) {
				price = rs.getDouble("FSellPrice");
			}
			today = YssD.round(unitValue, 3);
			yesterday = getUnitValue(YssFun.addDay(d, -1), curyCode);
			if(yesterday == 0){
				yesterday = today;
			}
			result = YssD.mul
			         (
						 YssD.div
						 (
							 YssD.add
							 (
								 YssD.sub
								 (
									 today, 
									 yesterday
								 ), 
								 price
						     ),
							 YssD.sub
							 (
								 yesterday,
								 price
							 )
						 ),
						 100
					 );
			result = YssD.round(result, 12);
		} catch (Exception e) {
			throw new YssException("获取日净值增长率出现异常！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return result;
    }
    
    //本期净值增长率
    private double getThisPeriodScale(Date d, String curyCode, double unitValue) throws YssException {
    	ArrayList accummulate =new ArrayList();
        ArrayList yesdayRate = new ArrayList();
        int year = 0;
        String sql = "";
        String holiday = "";
        java.util.Date startDate = new java.util.Date(); //存储期初日期
        int i = 0;
        String sqlStr = null;
        ResultSet tmpRs = null;
        ResultSet rsRateDate = null;
        ResultSet rs = null;
        Date RateDate;
        double firstRate = 1.0; //保存第一次计算
        double centerRate = 1.0; //保存从第二次到第倒数第二次
        double lastRate = 1.0; //保存最后一次计算
        double lastDateRate = 0.0; //期末单位基金资产净值
        try {
        	//若为期内成立基金，则期初单位基金资产净值为基金成立日的单位净值，其中基金成立日指该组合TA交易数据中销售类型为“00”（基金成立）对应的交易日期。
        	year = YssFun.getYear(d);
        	sql = " select FTradeDate from " + pub.yssGetTableName("Tb_TA_Trade")
				  + " where FPortCode = " + dbl.sqlString(portCode) + " and FSellType = '00'" 
				  + " and FCheckState = '1'" + " and FCuryCode = " + dbl.sqlString(curyCode);
            rs = dbl.queryByPreparedStatement(sql); 
            while (rs.next()) {
                if (YssFun.getYear(rs.getDate("FTradeDate")) == year) { //如果基金是今年成立的则取成立日期为期初日期
                    startDate = rs.getDate("FTradeDate");
                } else { // 如果基金不是今年成立的则取去年的最后一天的日期为期初日期
                    sql = " select max(fnavdate) as fnavdate from " + pub.yssGetTableName("Tb_Data_Multiclassnet") +
                          " where fnavdate between "+dbl.sqlDate((year - 1)+"-01-01")+" and "+dbl.sqlDate((year - 1)+"-12-31")+
                          " and FPortCode = " + dbl.sqlString(portCode) + " and FCuryCode = " + dbl.sqlString(curyCode);
                    tmpRs = dbl.queryByPreparedStatement(sql); 
                    while (tmpRs.next()) {
                        startDate = tmpRs.getDate("fnavdate"); //调用主要财务指标中的计算本期基金净值增长率的方法时传入的期初日期会减去一天，是取期初前一天的数据计算的
                    } //这里传入的期初日期其实就是要取数据的日期，故要加上一天带入计算
                    dbl.closeResultSetFinal(tmpRs);
                }
            }
            dbl.closeResultSetFinal(rs);
            
            sqlStr = " select fholidayscode,fportcode from " + pub.yssGetTableName("Tb_TA_CashSettle ")+
			         " where fcheckstate=1  and FSellTypeCode = '03' and FCuryCode = " + dbl.sqlString(curyCode) +
			         " and (fportcode = ' ' or fportcode = " + dbl.sqlString(portCode) + ")";
            rsRateDate = dbl.queryByPreparedStatement(sqlStr); 
    		while(rsRateDate.next()) {
    			if(rsRateDate.getString("fportcode").length() > 0){
    				holiday=rsRateDate.getString("fholidayscode");
    				break;
    			}
    			holiday=rsRateDate.getString("fholidayscode");
    		}
    		if (holiday.equals("")) {
    			throw new YssException("获取节假日群错误！请在“TA业务模块“>>”TA现金结算链接设置”中为分红交易类型设置对应节假日！");
    		}

            lastDateRate = YssD.round(unitValue, 3); //期末单位基金资产净值
            
            BaseOperDeal Bdeal = new BaseOperDeal();
            Bdeal.setYssPub(pub);
            //得到期内的分红数据
            sqlStr = "select FSellPrice as FAccumulateDivided,FConfimDate from " + pub.yssGetTableName("TB_TA_TRADE") +
                " where FCheckState = 1 and FConfimDate <=" + dbl.sqlDate(d) + "  and FConfimDate >= " + dbl.sqlDate(startDate) +" and FSellType = '03' " +
                " and FPortCode = " + dbl.sqlString(portCode) + " and FCuryCode = " + dbl.sqlString(curyCode) + " order by FConfimDate "; //获取累计分红价格，数据为小于等于当前日期
            rs = dbl.queryByPreparedStatement(sqlStr); 
            while (rs.next()) {
                accummulate.add(new Double(rs.getDouble("FAccumulateDivided"))); //获取第N次分红的金额。
                RateDate = Bdeal.getWorkDay(holiday, rs.getDate("FConfimDate"),-1);
                yesdayRate.add(new Double(getUnitValue(RateDate, curyCode))); //获取分红前的的单位净值
                i++;
            }
            dbl.closeResultSetFinal(rs);
            if (i >= 1) {
            	double douStartUnitValue = getUnitValue(startDate, curyCode);
                firstRate =((Double)yesdayRate.get(0)).doubleValue()/(douStartUnitValue == 0 ? 1:douStartUnitValue); //保存第一次计算
                for (int j = 1; j < i; j++) {
                    centerRate = ((Double)yesdayRate.get(j)).doubleValue()/ (((Double)yesdayRate.get(j - 1)).doubleValue() - ((Double)accummulate.get(j - 1)).doubleValue()) * centerRate; //从第二次到最后一次的前一次
                }
                lastRate = lastDateRate /(((Double)yesdayRate.get(i - 1)).doubleValue() - ((Double)accummulate.get(i - 1)).doubleValue()); //保存最后一次计算
                firstRate = (firstRate * centerRate * lastRate - 1) * 100; //把第一到最后乘起来就得到了本期净值增长率
            } else {
            	double douStartUnitValue = getUnitValue(startDate, curyCode);            	
                firstRate = (lastDateRate / (douStartUnitValue == 0 ? 1:douStartUnitValue) - 1) * 100 ;
            }//期内无分红数据，公式可简化为（期末÷期初单位净值）－１
            firstRate = YssD.round(firstRate, 12);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsRateDate);
            dbl.closeResultSetFinal(tmpRs);
        }
        if(lastDateRate==0) {
        	firstRate=0;
        }
        return firstRate;
    }
    
    //累计净值增长率
    //edit by songjie 2012.11.24 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 private edit to public
    public double getAccumulateScale(Date d, String curyCode, double unitValue, int digit, boolean flag) throws YssException {
    	ArrayList accummulate =new ArrayList();
        ArrayList yesdayRate = new ArrayList();
        int i = 0;
        String sqlStr = null;
        ResultSet rs = null;
        Date RateDate;
        double firstRate = 1.0; //保存第一次计算
        double centerRate = 1.0; //保存从第二次到第倒数第二次
        double lastRate = 1.0; //保存最后一次计算
        double lastDateRate = 0.0; //期末单位基金资产净值
        double unitCostByCury = 0.0;
        TaTradeBean ta = new TaTradeBean();
        ta.setYssPub(pub);
        try {        	
        	lastDateRate = YssD.round(unitValue, digit);; //期末单位基金资产净值    
        	
            //得到当天的累计分红
            sqlStr = " select FSellPrice as FAccumulateDivided,FConfimDate from " + pub.yssGetTableName("TB_TA_TRADE") +
                     " where FCheckState = 1 and FConfimDate <=" + dbl.sqlDate(dDate) + " and FSellType = '03' " +
                     " and FPortCode = " + dbl.sqlString(portCode) ;  //获取累计分红价格，数据为小于等于当前日期
            if(flag){
            	sqlStr += " and FCuryCode = " + dbl.sqlString(curyCode) + " order by FConfimDate";
            }else{
            	sqlStr += " and Fportclscode = " + dbl.sqlString(curyCode) + " order by FConfimDate";
            }           
            rs = dbl.queryByPreparedStatement(sqlStr); 
            while (rs.next()) {
                accummulate.add(new Double(rs.getDouble("FAccumulateDivided"))); //获取第N次分红的金额。
                RateDate = rs.getDate("FConfimDate");
                if(flag){
                	yesdayRate.add(new Double(getUnitValue(YssFun.addDay(RateDate, -1), curyCode))); //获取分红前的的单位净值
                }else{
                	yesdayRate.add(new Double(getUnitValueByFJCode(YssFun.addDay(RateDate, -1), curyCode, digit, "22"))); //获取分红前的的单位净值
                }    
                i++;
            }
            dbl.closeResultSetFinal(rs);
            if(flag){
            	unitCostByCury = ta.getUnitCostByCury(this.portCode,curyCode);
            }else{
            	unitCostByCury = ta.getUnitCostByFJCode(this.portCode,curyCode);
            }    
            if (i >= 1) {
                firstRate =((Double)yesdayRate.get(0)).doubleValue()/unitCostByCury; //保存第一次计算
                for (int j = 1; j < i; j++) {
                    centerRate = ((Double)yesdayRate.get(j)).doubleValue()/ (((Double)yesdayRate.get(j - 1)).doubleValue() - ((Double)accummulate.get(j - 1)).doubleValue()) * centerRate; //从第二次到最后一次的前一次
                }
                lastRate = lastDateRate /(((Double)yesdayRate.get(i - 1)).doubleValue() - ((Double)accummulate.get(i - 1)).doubleValue()); //保存最后一次计算
                firstRate = (firstRate * centerRate * lastRate - 1);//把第一到最后乘起来就得到了累计净值增长率                
            } else {            	
                firstRate = (lastDateRate/unitCostByCury - 1); //没有分红计处累计净值增长率
            }
            if(flag){
            	firstRate = firstRate * 100; 
            }
            firstRate = YssD.round(firstRate, 12);
        } catch (Exception e) {
            throw new YssException("获取累计分红数据出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        if(lastDateRate==0){
        	firstRate=0;
        }
        return firstRate;
    }
    
    //各Class的单位净值
    private double getUnitValue(Date dDate, String curyCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            strSql = "select round(FClassNetValue, 3) as FPrice from " + pub.yssGetTableName("Tb_Data_Multiclassnet") +
                " where FType = '02' and FCuryCode = " + dbl.sqlString(curyCode) + " and FNavDate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(portCode) ;
            rs = dbl.queryByPreparedStatement(strSql); 
            while (rs.next()) {
                dReturn = rs.getDouble("FPrice");
            }
            return dReturn; 
        } catch (Exception e) {
            throw new YssException("获取单位净值出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    private double getUnitValueByFJCode(Date dDate, String portclscode, int digit, String type) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            strSql = "select round(FClassNetValue, " + digit + ") as FPrice from " + pub.yssGetTableName("Tb_Data_Multiclassnet") +
                " where FType = " + dbl.sqlString(type) + " and FCuryCode = " + dbl.sqlString(portclscode) + " and FNavDate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(portCode) ;
            rs = dbl.queryByPreparedStatement(strSql); 
            while (rs.next()) {
                dReturn = rs.getDouble("FPrice");
            }
            return dReturn; 
        } catch (Exception e) {
            throw new YssException("获取单位净值出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    public ArrayList removeDuplicateWithOrder(ArrayList list) {  
        Set set = new HashSet();  
        ArrayList newList = new ArrayList();  
        for (Iterator iter = list.iterator(); iter.hasNext();) {  
        	Object element = iter.next();  
            if (set.add(element)){   
                newList.add(element);  
            }   
        } 
        return newList;  
    }
    
    public String getInvestTZInfo(String portCode, String clsPortCode, String catCode) throws YssException {    	
    	String strSql = "";
        ResultSet rs = null;
        String result = "null";
        try {
            strSql = " select FMoney from " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                     " where Fcheckstate =1 and FPortCode = " + dbl.sqlString(portCode) + " and FPortClsCode = " + dbl.sqlString(clsPortCode) +
                     " and FIVPayCatCode = " + dbl.sqlString(catCode) + " and FTransDate = " + dbl.sqlDate(this.dDate)+
                     " and FTsfTypeCode = '98' and FSubTsfTypeCode = '9804IV' ";                       
            rs = dbl.queryByPreparedStatement(strSql); 
            while (rs.next()) {
            	result = Double.toString(rs.getDouble("FMoney"));
            }
            return result; 
        } catch (Exception e) {
            throw new YssException("获取单位净值出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    private double getInvestFee(Double netValueBefore, String formula, String iVCatCode, String curyCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        InvestCfgFormula investOper = null;
        try {        	
        	investOper = (InvestCfgFormula) pub.getOperDealCtx().getBean(
		 			     	 getBeanIdBySpring(formula)
				     	 );
		 	investOper.setYssPub(pub);
		 	if(this.flag == 0){
		 		investOper.init(netValueBefore, formula, this.portCode, this.dDate);
		 	}else if(this.flag == 1){
		 		investOper.init(netValueBefore, formula, iVCatCode, this.portCode, curyCode, this.dDate);
		 	}
		 	 //如果当天没有人为调整，则通过利息算法计算，否则直接取调整金额
		 	if("null".equalsIgnoreCase(getInvestTZInfo(this.portCode, curyCode, iVCatCode))){
		 		dReturn = investOper.calcFormulaDouble();
		 	}else{
		 		dReturn = Double.parseDouble(getInvestTZInfo(this.portCode, curyCode, iVCatCode));
		 	} 
            return dReturn; 
        } catch (Exception e) {
            throw new YssException("获取运营费用出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    private double getScale(String curyCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
        	if(this.flag == 0){
        		dReturn  =  YssD.div
	      	    	        (
	  	    	        		YssD.sub
	  	    	        		(
	    	        				YssD.add
	    	        				(
		        						getNetValueForClass(curyCode, YssFun.addDay(this.dDate, -1)),    	        					 
		        						getBSG(curyCode, this.dDate)
	        						), 
	        						getBSH(curyCode, this.dDate)
		        				),
		        				YssD.sub
	  	    	        		(
	    	        				YssD.add
	    	        				(
		        						getNetValue(YssFun.addDay(this.dDate, -1), "01"), 
		        						getBSG("", this.dDate)
	        						), 
	        						getBSH("", this.dDate)
		        				)
	    	        		);
  			} else if(this.flag == 1){ //fj
  				dReturn  =  YssD.div
	  				        (
				        		getTaStock(curyCode, this.dDate, 3),
				        		sumTaZSStock(this.dDate)
			        		);  	    				        
    		}
        	return dReturn;
        } catch (Exception e) {
            throw new YssException("获取单位净值出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }    
    
    //单位净值
    private double getUnit(Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            strSql = " select round(FPrice,fisincode) as FPrice from " + pub.yssGetTableName("tb_data_navdata") +
                     " where fretypecode = 'Total' and FPortCode = " + dbl.sqlString(this.portCode) +
                     " and fnavdate = " + dbl.sqlDate(dDate) + " and FkeyCode = 'Unit' ";
            rs = dbl.queryByPreparedStatement(strSql); 
            while (rs.next()) {
                dReturn = rs.getDouble("FPrice");
            }
            return dReturn; 
        } catch (Exception e) {
            throw new YssException("获取单位净值出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //累计单位净值
    private double getAccumulateUnit(Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            strSql = " select round(FPrice,fisincode) as FPrice from " + pub.yssGetTableName("tb_data_navdata") +
                     " where fretypecode = 'Total' and FPortCode = " + dbl.sqlString(this.portCode) +
                     " and fnavdate = " + dbl.sqlDate(dDate) + " and FkeyCode = 'AccumulateUnit' ";
            rs = dbl.queryByPreparedStatement(strSql); 
            while (rs.next()) {
                dReturn = rs.getDouble("FPrice");
            }
            return dReturn; 
        } catch (Exception e) {
            throw new YssException("获取累计单位净值出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //资产净值 add by zhouwei 20120620 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
    public double getTotalValue(Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            strSql = " select  fportmarketvalue from " + pub.yssGetTableName("tb_data_navdata") +
                     " where fretypecode = 'Total' and FPortCode = " + dbl.sqlString(this.portCode) +
                     " and fnavdate = " + dbl.sqlDate(dDate) + " and FkeyCode = 'TotalValue' ";
            rs = dbl.queryByPreparedStatement(strSql); 
            while (rs.next()) {
                dReturn = rs.getDouble("fportmarketvalue");
            }
            return dReturn; 
        } catch (Exception e) {
            throw new YssException("获取资产净值出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //add by fangjiang 2012.05.02 stroy 2565
    private void dealMClass() throws YssException, SQLException {
    	
    	String strSql = "";
    	String sql = "";
    	String curyCode = "";
    	String clsPortCode = "";
    	ResultSet rs = null;
    	double baseRate = 0;
    	double portRate = 0;
    	double unitNet = 0;
    	double amount = 0;
    	double classNetValue = 0;
    	double accumulateUnit = 0;
    	double scale = 0; 
    	PreparedStatement pst = null;
        boolean bTrans = false; 
        Connection conn = dbl.loadConnection();
		//add by zhouwei 2012.06.20 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
        boolean inShowItem=false;//是否按照分级的显示项来生成
        strSql = " insert into " + pub.yssGetTableName("Tb_Data_MultiClassNet") +
		         " (FNAVDate,FPortCode,FType,FPortCuryCode,FCuryCode,FNetValueBeforeFee,FManageFee,FTrusteeFee," +
		         " FNetValue,FClassNetValue,FCheckState,FCreator,FCreateTime,FSTFee,FFXJFee) " + 
		         " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "; 

        pst = conn.prepareStatement(strSql);        
        
        String PortCodeCuryCode = "";
        rs = dbl.openResultSet("select FPortCury from "+pub.yssGetTableName("Tb_Para_Portfolio")+" where FPortCode = "+dbl.sqlString(this.portCode)+" and FCheckState= 1");
        if(rs.next()){
        	PortCodeCuryCode = rs.getString("FPortCury")+"";
        }
        rs.close();
            
    	try{
		    //--- edit by zhouwei 2012.06.20 STORY #2727 start---//
    		//如果所有组合分级的显示项都没有设置，则认为采用之前易方达ETF的计算方法，否则就根据显示项来进行计算  story 2727 by zhouwei 20120620
    		sql="select  FPORTCLSCODE from "+pub.yssGetTableName("tb_ta_portcls")+" where fshowitem is not null"
 		   		+" and fcheckstate=1 and Fportcode = " + dbl.sqlString(this.portCode);
    		rs=dbl.openResultSet(sql);
    		if(rs.next()){
    			inShowItem=true;
    		}
    		dbl.closeResultSetFinal(rs);
	    	sql =  " select a.*,b.fshowitem,b.FOFFSET From " + pub.yssGetTableName("Tb_TA_ClassFundDegree") +
	    		   " a join (select fshowitem,FPORTCLSCODE,FPortCode,FOFFSET from "+pub.yssGetTableName("tb_ta_portcls")
	    		 + " where fcheckstate=1 and (fportclsschema = 'abLimited' or fportclsschema = 'inExRate')) b"
	    		   //modifiec by  yeshenghong  story4151  添加分级模式的筛选  
	    		 + " on a.FPortCode=b.FPortCode and a.FPORTCLSCODE=b.FPORTCLSCODE"
	    	     + " where a.FPortCode = " + dbl.sqlString(this.portCode) + " and a.FCheckState = 1 "
	    		 + " and a.FPORTCLSCODE<>' ' and a.FCURYCODE<>' '"   	
	    		 + " order by FOFFSET";	//20120709 added by liubo.Story #2719.根据“是否轧差计算份额总净值字段”进行排序，每个投资组合有且只有一个选“是”（即该字段值为1）的分级组合，这个分级组合将会在最后进行轧差计算
	    	//--- edit by zhouwei 2012.06.20 STORY #2727 end---//
			rs = dbl.openResultSet(sql);
	    	while(rs.next()){
	    		//币种
	    		curyCode = rs.getString("FCuryCode"); 
	    		//分级组合代码
	    		clsPortCode = rs.getString("FPortClsCode");
	    		//基础汇率
	    		baseRate = this.getSettingOper().getCuryRate(
			  				  this.dDate, 
			  				  curyCode, 
			  				  this.portCode, 
			  				  YssOperCons.YSS_RATE_BASE);
			   	//组合汇率
			   	portRate = this.getSettingOper().getCuryRate(
			  				  this.dDate, 
			  				  "", 
			  				  this.portCode, 
			  				  YssOperCons.YSS_RATE_PORT); 
			   	//分级组合的单位净值
			   	unitNet = YssD.div
			   	          (
			   	              YssD.mul
			   	              (
		   	            		  this.getUnit(this.dDate), 
		   	            		  portRate
	   	            		  ),
	   	        		      baseRate
	        		      );
			   	//根据通用参数保留unitNet的位数 
			   	//add by huangqirong 2012-05-09 story #2565
			   	CtlPubPara pubPara = new CtlPubPara();
			   	pubPara.setYssPub(this.pub);
			   	String digits = pubPara.getDigitsPortMethod("PubParaUnitCls","dayfinish","CtlPubParaUnitCls","portClsSel","txtdigit",clsPortCode,"3");			   	
			   	int digt = YssFun.toInt(digits);

				if(!PortCodeCuryCode.equals(curyCode)){
			   		unitNet = YssD.round(unitNet, digt);
			   	}
				//---edit by zhouwei 2012.06.20 STORY #2727 start---//
				String showItems=rs.getString("fshowitem");
			   	if(showItems != null && showItems.indexOf("23")>-1){//显示项包含每十份单位净值 story 2727 modify by zhouwei 20120620
			   		double tenUnitNet=YssD.mul(unitNet,10);
//			   		if(!PortCodeCuryCode.equals(curyCode)){
//			   			tenUnitNet= YssD.round(
//			   					tenUnitNet, digt);
//				   	}
			   		pst.setDate(1, YssFun.toSqlDate(this.dDate));
				    pst.setString(2, this.portCode);
				    pst.setString(3, "23"); //标识－每十份单位净值
				    pst.setString(4, getPortCuryCode());
				    pst.setString(5, clsPortCode);
				    pst.setDouble(6, 0.0);
				    pst.setDouble(7, 0.0);
				    pst.setDouble(8, 0.0);
				    pst.setDouble(9, 0.0);
				    pst.setDouble(10, tenUnitNet);
				    pst.setInt(11, 1);
				    pst.setString(12, pub.getUserCode());
				    pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				    pst.setDouble(14, 0.0); 
				    pst.setDouble(15, 0.0); 
				    pst.executeUpdate();
				}
	
			   	//---end---		
			   	if(!inShowItem || (showItems != null && showItems.indexOf("02")>-1)){//不按照显示项显示 或者 按照显示项并且显示项包含单位净值 story 2727 modify by zhouwei 20120620
			   		pst.setDate(1, YssFun.toSqlDate(this.dDate));
				    pst.setString(2, this.portCode);
				    pst.setString(3, "02"); //标识－单位净值
				    pst.setString(4, getPortCuryCode());
				    pst.setString(5, clsPortCode);
				    pst.setDouble(6, 0.0);
				    pst.setDouble(7, 0.0);
				    pst.setDouble(8, 0.0);
				    pst.setDouble(9, 0.0);
				    pst.setDouble(10, unitNet);
				    pst.setInt(11, 1);
				    pst.setString(12, pub.getUserCode());
				    pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				    pst.setDouble(14, 0.0); 
				    pst.setDouble(15, 0.0); 
				    pst.executeUpdate();
			   	}
			   	
			   	if(showItems != null && showItems.indexOf("22")>-1){//显示项包含今日单位净值 story 2727 modify by zhouwei 20120620
			   		pst.setDate(1, YssFun.toSqlDate(this.dDate));
				    pst.setString(2, this.portCode);
				    pst.setString(3, "22"); //标识－今日单位净值
				    pst.setString(4, getPortCuryCode());
				    pst.setString(5, clsPortCode);
				    pst.setDouble(6, 0.0);
				    pst.setDouble(7, 0.0);
				    pst.setDouble(8, 0.0);
				    pst.setDouble(9, 0.0);
				    pst.setDouble(10, unitNet);
				    pst.setInt(11, 1);
				    pst.setString(12, pub.getUserCode());
				    pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				    pst.setDouble(14, 0.0); 
				    pst.setDouble(15, 0.0); 
				    pst.executeUpdate();
				}

			    amount = getTaStock(clsPortCode, this.dDate, 0);				
			   	if(!inShowItem || (showItems != null && showItems.indexOf("05")>-1)){//不按照显示项显示 或者 按照显示项并且显示项包含实收资本 story 2727 modify by zhouwei 20120620
			    //各分级的总份额				    
				    pst.setDate(1, YssFun.toSqlDate(this.dDate));
				    pst.setString(2, this.portCode);
				    pst.setString(3, "05"); //标识－实收资本
				    pst.setString(4, getPortCuryCode());
				    pst.setString(5, clsPortCode);
				    pst.setDouble(6, 0.0);
				    pst.setDouble(7, 0.0);
				    pst.setDouble(8, 0.0);
				    pst.setDouble(9, 0.0);
				    pst.setDouble(10, amount);
				    pst.setInt(11, 1);
				    pst.setString(12, pub.getUserCode());
				    pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				    pst.setDouble(14, 0.0); 
				    pst.setDouble(15, 0.0); 
				    pst.executeUpdate();
			   	}
			   	
			  if(!inShowItem){//不按照显示项显示  story 2727 modify by zhouwei 20120620
				  //各分级的资产净值，要不要保留位数待确认
				    classNetValue = YssD.mul(amount, unitNet);		    
				   	pst.setDate(1, YssFun.toSqlDate(dDate));
				    pst.setString(2, this.portCode);
				    pst.setString(3, "01"); //标识－资产净值
				    pst.setString(4, getPortCuryCode());
				    pst.setString(5, clsPortCode);
				    pst.setDouble(6, 0.0);
				    pst.setDouble(7, 0.0);
				    pst.setDouble(8, 0.0);
				    pst.setDouble(9, 0.0);
				    pst.setDouble(10, classNetValue);
				    pst.setInt(11, 1);
				    pst.setString(12, pub.getUserCode());
				    pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				    pst.setDouble(14, 0.0);
				    pst.setDouble(15, 0.0); 
				    pst.executeUpdate(); 
			  }else if(showItems != null && showItems.indexOf("01")>-1){//按照显示项并且显示项包含资产净值 story 2727 modify by zhouwei 20120620
				  	// 人民币资产净值 / 美元兑人民币汇率= 美元资产净值
				  classNetValue = YssD.div
	   	             (
	   	                YssD.mul
	   	                (
   	            		  this.getTotalValue(this.dDate), 
   	            		  portRate
	            		  ),
	        		      baseRate
    		        );		    
				   	pst.setDate(1, YssFun.toSqlDate(dDate));
				    pst.setString(2, this.portCode);
				    pst.setString(3, "01"); //标识－资产净值
				    pst.setString(4, getPortCuryCode());
				    pst.setString(5, clsPortCode);
				    pst.setDouble(6, 0.0);
				    pst.setDouble(7, 0.0);
				    pst.setDouble(8, 0.0);
				    pst.setDouble(9, 0.0);
				    pst.setDouble(10, classNetValue);
				    pst.setInt(11, 1);
				    pst.setString(12, pub.getUserCode());
				    pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				    pst.setDouble(14, 0.0);
				    pst.setDouble(15, 0.0); 
				    pst.executeUpdate();
			  }

			   	//20120709 added by liubo.Story #2719
			   	//生成份额总净值
			   	//=============================
			   	if (!inShowItem || (showItems != null && showItems.indexOf("06")>-1))
			   	{
				   	pst.setDate(1, YssFun.toSqlDate(dDate));
				    pst.setString(2, this.portCode);
				    pst.setString(3, "06"); //标识－份额总净值
				    pst.setString(4, getPortCuryCode());
				    pst.setString(5, clsPortCode);
				    pst.setDouble(6, 0.0);
				    pst.setDouble(7, 0.0);
				    pst.setDouble(8, 0.0);
				    pst.setDouble(9, 0.0);
				    //某个组合的FOFFSET字段（轧差计算份额总净值）值为0时，表示按正常计算份额总净值，公式为“本位币基金净值×该币种份额 / 基金总份额”
				    //若值为1，表示进行轧差计算，公式为“本位币基金净值 – 所有其他币种的份额总净值之和”
				    //一个投资组合只能有且只有一个分级组合的FOFFSET字段值能为1
				    //=================================
				    if (rs.getString("FOFFSET").trim().equals("0"))
				    {
				    	pst.setDouble(10, YssD.div(YssD.mul(getTotalValue(this.dDate), amount), getTAStockAmount(this.dDate,this.portCode)));
				    }
				    else
				    {
				    	pst.setDouble(10, YssD.sub(getTotalValue(this.dDate), getNetValueOffset(this.dDate,this.portCode)));
				    }
				    //=================end================
				    pst.setInt(11, 1);
				    pst.setString(12, pub.getUserCode());
				    pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				    pst.setDouble(14, 0.0);
				    pst.setDouble(15, 0.0); 
				    pst.executeUpdate();
			   	}
			   	//==============end===============
			   	
			   	//20120709 added by liubo.Story #2719
			   	//生成单位净值（币种净值/总份额）
			   	//+++++++++++++++++++++++++++++
			   	if (!inShowItem || (showItems != null && showItems.indexOf("07")>-1))
			   	{
				   	pst.setDate(1, YssFun.toSqlDate(dDate));
				    pst.setString(2, this.portCode);
				    pst.setString(3, "07"); //标识－份额总净值
				    pst.setString(4, getPortCuryCode());
				    pst.setString(5, clsPortCode);
				    pst.setDouble(6, 0.0);
				    pst.setDouble(7, 0.0);
				    pst.setDouble(8, 0.0);
				    pst.setDouble(9, 0.0);
				    pst.setDouble(10, YssD.round(YssD.div(classNetValue, getTAStockAmount(this.dDate,this.portCode)),digt)); //modify huangqirong 2013-01-10 STORY #3433 多币种财务估值表对美元单位净值保留4位小数位
				    pst.setInt(11, 1);
				    pst.setString(12, pub.getUserCode());
				    pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				    pst.setDouble(14, 0.0);
				    pst.setDouble(15, 0.0); 
				    pst.executeUpdate();
			   	}
				//---edit by zhouwei 2012.06.20 STORY #2727 end---//
			   	//+++++++++++end++++++++++++++++++
			   	
			   
			    
			    //分级组合的累计单位净值
			    accumulateUnit = YssD.add
			                     (
		                    		 unitNet, 
		                    		 this.getSumDivided(this.dDate,clsPortCode)
	                    		 );
/*			    accumulateUnit = YssD.div
					   	         (
					   	             YssD.mul
					   	             (
				   	            		 this.getAccumulateUnit(this.dDate), 
				   	            		 portRate
			   	            		 ),
			   	        		     baseRate
			        		     );*/
			    //根据通用参数保留unitNet的位数 
			    //add by huangqirong 2012-05-09 story #2565
				if(!PortCodeCuryCode.equals(curyCode)){
					accumulateUnit = YssD.round(accumulateUnit, digt);
			   	}
				//---end---
				//edit by zhouwei 2012.06.20 STORY #2727
				if(!inShowItem || (showItems != null && showItems.indexOf("08")>-1)){//不按照显示项显示 或者 按照显示项并且显示项包含累计单位净值 story 2727 modify by zhouwei 20120620
				    pst.setDate(1, YssFun.toSqlDate(dDate));
			        pst.setString(2, this.portCode);
			        pst.setString(3, "08"); //累计单位净值
			        pst.setString(4, getPortCuryCode());
			        pst.setString(5, clsPortCode);
			        pst.setDouble(6, 0.0);
			        pst.setDouble(7, 0.0);
			        pst.setDouble(8, 0.0);
			        pst.setDouble(9, 0.0);
			        pst.setDouble(10, accumulateUnit);
			        pst.setInt(11, 1);
			        pst.setString(12, pub.getUserCode());
			        pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
			        pst.setDouble(14, 0.0); 
			        pst.setDouble(15, 0.0);
			        pst.executeUpdate();
				}								
				//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
				double clsNavofYB = 0;//分级基金资产净值（原币）
				double clsUnitNetOfYB = 0;//分级基金单位净值（原币）
				String portCuryCode = getPortCuryCode();//组合货币币种
				double totalNetValue = getTotalValue(this.dDate);//总的本位币资产净值
				double totalTAAmount = getTAStockAmount(this.dDate,this.portCode);//总的TA库存数量
				//如果显示项包含 资产总净值（原币）
				if(!inShowItem || (showItems != null && showItems.indexOf("38")>-1)){
					//非钆差分级组合的资产净值_原币 
					//= 基金总资产净值_本位币 * （非钆差分级组合的的份额数量 / 总的份额数量）* 组合汇率 / 基础汇率；
					if (rs.getString("FOFFSET").trim().equals("0"))//如果是非钆差分级组合
				    {
						clsNavofYB = YssD.div(
								              YssD.mul(
								            		   YssD.div(
	                                                            YssD.mul(getTotalValue(this.dDate), 
	                            		                                 amount), 
	                                                            getTAStockAmount(this.dDate,this.portCode)
	                                                            ),
	                                                   portRate),
	                                          baseRate);
				    }
				    else//如果是钆差分级组合，需钆差计算该分级资产净值
				    {
						//分级组合总净值_原币（轧差） 
				    	// =（基金总资产净值_本位币 – ∑（其他非钆差分级组合的份额总净值_本位币））* 组合汇率 / 基础汇率；
						//其他非钆差分级组合的份额总净值_本位币
				    	// = ∑（基金总资产净值_本位币 * （各分级的份额数量 / 总的份额数量））；
				    	
				    	clsNavofYB = YssD.div(
				    			              YssD.mul(
				    			            		   YssD.sub(getTotalValue(this.dDate), 
				    			            				    calUnSpecialSumNet(totalNetValue,totalTAAmount)),
                                                       portRate),
                                              baseRate);
				    }
					
					setPst(pst, "38", clsPortCode, portCuryCode, YssFun.roundIt(clsNavofYB, 2));//modified by yeshenghong story4151 20130816
				}
				//如果显示项包含 单位净值（原币）
				if(!inShowItem || (showItems != null && showItems.indexOf("39")>-1)){
					//各分级单位净值_原币 = 各分级资产净值_原币 / 各分级的份额数量；
					clsUnitNetOfYB = YssD.div(clsNavofYB,amount);
					
					setPst(pst, "39", clsPortCode, portCuryCode, clsUnitNetOfYB);
				}
				//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
	    	}
	    	conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);   	
    	} catch (YssException e) {
    		throw new YssException(e.getMessage());
		} finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
    }
	
	
	 /**
     * add by songjie 2013.05.31 
     * STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001
     * 计算非钆差分级资产净值之和
     * @param totalNetValue 总的本位币资产净值
     * @param totalTAAmount 总的TA库存数量
     * @return 
     * @throws YssException
     */
    public double calUnSpecialSumNet(double totalNetValue, double totalTAAmount) throws YssException{
    	double unSpecialSumNet = 0;
    	ResultSet rs = null;
    	StringBuffer sb = new StringBuffer();
    	try{
    		if(totalNetValue != 0){
    			sb.append(" select sum(").append(totalNetValue).append(" * a.FStorageAmount / ").append(totalTAAmount)
      		  		.append(") as unSpecialSumNet from ").append(pub.yssGetTableName("Tb_Stock_TA")).append(" a ")
      		  		.append(" join ")
      		  		.append(" (select FPortCode,FPortClsCode from ").append(pub.yssGetTableName("Tb_TA_PortCls"))
      		  		.append(" where FOffSet = 0 and FCheckState = 1) b ")
      		  		.append(" on a.FPortCode = b.FPortCode and a.FPortClsCode = b.FPortClsCode ")
      		  		.append(" where a.FStorageDate = ").append(dbl.sqlDate(this.dDate))
      		  		.append(" and a.FPortCode = ").append(dbl.sqlString(this.portCode))
      		  		.append(" and a.FCheckState = 1 ");
      		
    			rs = dbl.openResultSet(sb.toString());
    			while(rs.next()){
    				unSpecialSumNet = rs.getDouble("unSpecialSumNet");
    			}
    		}
    		
    		return unSpecialSumNet;
    	}catch(Exception e){
    		throw new YssException("计算非钆差分级资产净值之和出错",e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
	
    
    /**
     * add by yeshenghong 2013.08.06 
     * STORY #4151
     * 计算非钆差分级资产净值之和
     * @param totalNetValue 总的本位币资产净值
     * @return 
     * @throws YssException
     */
    //edit by songjie 2014.05.23 STORY #16993 需求上海-[海富通]QDIIV4.0[紧急]201405022001 
    //添加参数 sumItemType 用于区分合计项代码统计非钆差组合对应的合计值
    public double calUnSpecialSumNet(double totalNetValue,String sumItemType) throws YssException{
    	double unSpecialSumNet = 0;
    	ResultSet rs = null;
    	String  strSql = "";
    	BaseOperDeal obj = new BaseOperDeal();
        obj.setYssPub(pub);
        double sPortRate = 0;
        double sBaseRate = 0;
    	try{
    		if(totalNetValue != 0){
      		    strSql = " select m.fclassnetvalue,c.fcurycode from " + pub.yssGetTableName("tb_data_multiclassnet") + " m " +
      			   		" join " + pub.yssGetTableName("Tb_TA_PortCls")  + " p on m.fcurycode = p.fportclscode " +
      			   		" join tb_001_ta_classfunddegree c on m.fcurycode = c.fportclscode " +
      			   		//edit by songjie 2014.05.23 STORY #16993 需求上海-[海富通]QDIIV4.0[紧急]201405022001 合计项代码通过参数控制
      			   		" where m.ftype = " + dbl.sqlString(sumItemType) + " and m.fportcode = " + dbl.sqlString(portCode) +
      			   		" and m.fnavdate = " +dbl.sqlDate(this.dDate) + 
      			   		" and p.fcheckstate = 1 and p.foffset = 0 ";	
    			rs = dbl.openResultSet(strSql);
    			while(rs.next()){
    				sPortRate = obj.getCuryRate(this.dDate, rs.getString("fcuryCode"),
                            this.portCode,
                            YssOperCons.YSS_RATE_PORT);
    				sBaseRate = obj.getCuryRate(dDate, rs.getString("fcuryCode"),
    		                this.portCode,
    		                YssOperCons.YSS_RATE_BASE);
    				//edit by songjie 2044.05.23 BUG #94222 QDV4海富通基金2014年05月21日01_B 添加保留两位的逻辑
    				//--- edit by songjie 2014.05.23 STORY #16993 需求上海-[海富通]QDIIV4.0[紧急]201405022001 start---//
    				if(sumItemType.equals("38")){
    					unSpecialSumNet += YssD.round(YssD.div(YssD.mul(rs.getDouble("fclassnetvalue") , sBaseRate),sPortRate), 2);
    				}else if(sumItemType.equals("011")){
    					unSpecialSumNet += rs.getDouble("fclassnetvalue");
    				}
    				//--- edit by songjie 2014.05.23 STORY #16993 需求上海-[海富通]QDIIV4.0[紧急]201405022001 end---//
    			}
    		}
    		
    		return totalNetValue-unSpecialSumNet;
    	}catch(Exception e){
    		throw new YssException("计算非钆差分级资产净值之和出错",e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
	/**
	 * add by songjie 2013.05.31 STORY #3965
	 * 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 设置 PreparedStatement
	 * 设置 pst 并插入分级资产净值表
	 * @param pst PreparedStatement
	 * @param type 合计项代码
	 * @param clsPortCode 分级组合代码
	 * @param portCuryCode 组合货币币种
	 * @param showValue 合计项金额
	 * @throws YssException
	 */
	private void setPst(PreparedStatement pst, String type, String clsPortCode, String portCuryCode, double showValue) 
	throws YssException {
		try {
			pst.setDate(1, YssFun.toSqlDate(dDate));
			pst.setString(2, this.portCode);
			pst.setString(3, type); // 合计项代码
			pst.setString(4, getPortCuryCode());
			pst.setString(5, clsPortCode);
			pst.setDouble(6, 0.0);
			pst.setDouble(7, 0.0);
			pst.setDouble(8, 0.0);
			pst.setDouble(9, 0.0);
			pst.setDouble(10, showValue);
			pst.setInt(11, 1);
			pst.setString(12, pub.getUserCode());
			pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
			pst.setDouble(14, 0.0);
			pst.setDouble(15, 0.0);
			pst.executeUpdate();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
    
    /**
     * 此方法用于获取TA库存的总数量
     * 20120709 added by liubo.Story #2719
     * @param dDate 估值日期
     * @param sPortCode 投资组合代码
     */
    public double getTAStockAmount(Date dDate,String sPortCode) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;
    	double dReturn = 1;
    	
    	try
    	{
		    //--- edit by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
			//换行
	    	strSql = "select sum(FSTORAGEAMOUNT) as Amount from " + pub.yssGetTableName("Tb_Stock_TA") + 
	    	" where FStorageDate = " + dbl.sqlDate(dDate) + " and FPortCode = " + dbl.sqlString(sPortCode);
			//--- edit by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
	    	rs = dbl.queryByPreparedStatement(strSql);
	    	
	    	while(rs.next())
	    	{
	    		dReturn = rs.getDouble("Amount");
	    	}
    	}
    	catch(Exception ye)
    	{
    		throw new YssException("计算份额总净值时获取TA库存数量出错：" + ye.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    	return dReturn;
    }
    
    /**
     * 此方法用于获取某个组合在某天的基金总净值
     * 20120709 added by liubo.Story #2719
     * @param dDate 估值日期
     * @param sPortCode 投资组合代码
     */
    public double getNetValueOffset(Date dDate,String sPortCode) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;
    	double dReturn = 0;
    	
    	try
    	{
    		strSql = "select sum(FCLASSNETVALUE) as Total from " + pub.yssGetTableName("tb_data_multiclassnet") +
    				 " where FNAVDATE = " + dbl.sqlDate(dDate) + " and FPORTCODE = " + dbl.sqlString(sPortCode) +
    				 " and FCuryCode in (select FPORTCLSCODE from " + pub.yssGetTableName("tb_ta_portcls") + " where FPORTCODE = " + dbl.sqlString(sPortCode)  +
    				 " and FOFFSET = 0) and FType = '06'";
    		
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
    			dReturn = rs.getDouble("Total");
    		}
    	}
    	catch(Exception ye)
    	{
    		throw new YssException("轧差计算份额总净值时出错：" + ye.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    	return dReturn;
    }
    
    /**
     * add by yeshenghong 2013-05-09 story #3759
     * 生成杠杆分级基金各份额资产净值
     * */
    private void dealLeverGradeNetValue()  throws YssException, SQLException  {
    	String strSql = "";
    	String sql = "";
    	String clsPortCode = "";
    	ResultSet rs = null;  
    	ResultSet rsInner = null;
    	PreparedStatement pst = null;
//        boolean bTrans = true; 
        Connection conn = dbl.loadConnection();
//        conn.setAutoCommit(false);
        double yesNetValue = 0 ;//昨日单位净值
        double curNetValue = 0;//今日单位净值
        double accumNetValue = 0;//累计单位净值
        double growthRate = 0;
        double accGrowthRate = 0;
        //02  单位净值  08累计净值  09 日净值增长率 022高精度单位净值
        boolean inShowItem = false;//是否按照分级的显示项来生成
        String showItems = "";
        ArrayList alShowItems = null;//add by songjie 2014.05.24 用于保存明细的显示项代码 BUG #94385 QDV4海富通基金2014年05月26日01_B
        double amount = 0;
    	try{  
    		sql="select  FPORTCLSCODE from "+pub.yssGetTableName("tb_ta_portcls")+" where fshowitem is not null"
		   		+" and fcheckstate=1 and Fportcode = " + dbl.sqlString(this.portCode);
			rs=dbl.openResultSet(sql);
			if(rs.next()){
				inShowItem = true;
			}
			dbl.closeResultSetFinal(rs);
			String portCuryCode = getPortCuryCode();
			
    	    strSql = " insert into " + pub.yssGetTableName("Tb_Data_MultiClassNet") +
		       " (FNAVDate,FPortCode,FType,FPortCuryCode,FCuryCode,FNetValueBeforeFee,FManageFee,FTrusteeFee," +
		       " FNetValue,FClassNetValue,FCheckState,FCreator,FCreateTime,FSTFee,FFXJFee) " + 
		       " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "; 

    	    pst = conn.prepareStatement(strSql);
    		//根据通用参数保留unitNet的位数 
		   	//add by huangqirong 2012-05-09 story #2565
		   	CtlPubPara pubPara = new CtlPubPara();
		   	pubPara.setYssPub(this.pub);
//		   	String digits = pubPara.getDigitsPortMethod("PubParaUnitCls","dayfinish","CtlPubParaUnitCls","portClsSel","txtdigit",clsPortCode,"3");			   	
//		   	int digt = YssFun.toInt(digits);

    		
	    	 //查询获取基金份额的份额类别   FNAVFormula 净值公式, FDiscountFormula 折算公式
            sql = " select distinct t.FPortClsCode,t.FShareCategory,t.FConvention,t.FPeriod,t.FDailyNav,t.FAfterDiscountNav," +
            		" t.FAfterDiscountAmount, s.FBeanId, t.fshowitem,t.FOFFSET,t.fportclsnav,f.fcurycode  from " + pub.yssGetTableName("tb_ta_portcls") +  
            		" t join Tb_Base_CalcInsMetic b on t.FDailyNav = b.FCIMCode " +
            		" join  tb_fun_spinginvoke s on b.fspicode = s.fsicode"+
            		" left join " + pub.yssGetTableName("Tb_TA_ClassFundDegree") + " f on t.FPortClsCode = f.FPortClsCode " + 
            		" where t.fportcode  = " + dbl.sqlString(this.portCode) + " and t.FCheckState = 1 " +
            		" and ( t.fportclsschema = 'abLimited' or t.fportclsschema = 'inNetValue_chinaL' )" + //add by yeshenghong story4151 20130813
            		" order by FOFFSET, fportclscode ";
            rs = dbl.openResultSet(sql);
            
            LeverGradeFundCfg leverCfg = null;
           
	    	while(rs.next()){
	    		//分级组合代码
	    		clsPortCode = rs.getString("FPortClsCode");
	    		//根据通用参数保留unitNet的位数 
			   	//add by huangqirong 2012-05-09 story #2565
	    	 	
			   	String digits = pubPara.getDigitsPortMethod("PubParaUnitCls","dayfinish","CtlPubParaUnitCls","portClsSel","txtdigit",clsPortCode,"3");	
				int digt = YssFun.toInt(digits);
	            if (rs.getString("FBeanId") != null && rs.getString("FBeanId").length() > 0) {
	            	 leverCfg = (LeverGradeFundCfg) pub.getOperDealCtx().getBean(
	                         rs.
	                         getString("FBeanId"));
	            	 leverCfg.setYssPub(pub);
	 	    		 leverCfg.init(rs.getString("FDailyNav"),portCode,clsPortCode,dDate);
	            }else
	            {
	            	throw new YssException("请设置【基础参数模块的Spring调用】,引用BeanId: LeverGradeFundCfg!");
	            }
	            
	            curNetValue = leverCfg.calcGradeFundNetValue();
	            accumNetValue = curNetValue;
	            showItems = rs.getString("fshowitem");
	            //--- edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B start---//
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
	            alShowItems = new ArrayList();
	            String[] showIts = showItems.split(",");
	            for(int i = 0; i < showIts.length; i++){
	            	alShowItems.add(showIts[i]);
	            }
	            //--- edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B end---//
	    		if(rs.getString("FShareCategory").equals("1"))//A份额 累计单位净值计算  优先类
	    		{
	    			accumNetValue = curNetValue;
	    			sql = " select * from " + pub.yssGetTableName("tb_data_MultiClassNet") + " where fnavdate in " +
	    				  " (select distinct FBaseDate from " + pub.yssGetTableName("tb_ta_LeverShare") + "  where FConversionDate <=" +
	    				  dbl.sqlDate(this.dDate) + " and fportcode = " + dbl.sqlString(portCode) + " and fconversiontype <> 3) " +
	    				  " and  FCurycode = " + dbl.sqlString(clsPortCode) + " and ftype = '02'  order by fnavdate ";
	    			rsInner = dbl.openResultSet(sql);
	    			while(rsInner.next())
	    			{
	    				accumNetValue += rsInner.getDouble("FClassNetValue") - 1;
	    			}
	    			dbl.closeResultSetFinal(rsInner);
	    			sql = " select FSplitNetValue from " + pub.yssGetTableName("tb_ta_trade") + " where FPortClsCode = " + dbl.sqlString(clsPortCode) +
					" and fportcode = " + dbl.sqlString(portCode) + " and FConvertType = '3' and FTradeDate <= " + dbl.sqlDate(this.dDate);
					rsInner = dbl.openResultSet(sql);
					while(rsInner.next())
					{
						accumNetValue += rsInner.getDouble("FSplitNetValue") - 1;
					}
					dbl.closeResultSetFinal(rsInner);
	    		}
	    		
	    		if(rs.getString("FShareCategory").equals("2"))//B份额 累计单位净值计算  进取类
	    		{
	    			accumNetValue = curNetValue;
	    			sql = " select FSplitNetValue from " + pub.yssGetTableName("tb_ta_trade") + " where FPortClsCode = " + dbl.sqlString(clsPortCode) +
	    					" and fportcode = " + dbl.sqlString(portCode) + " and FConvertType = '3' and FTradeDate <= " + dbl.sqlDate(this.dDate);
	    			rsInner = dbl.openResultSet(sql);
	    			while(rsInner.next())
	    			{
	    				accumNetValue *= rsInner.getDouble("FSplitNetValue");
	    			}
	    			dbl.closeResultSetFinal(rsInner);
	    		}
	    		
	    		if(rs.getString("FShareCategory").equals("3"))//基础份额 累计单位净值计算
	    		{
	    			accumNetValue = curNetValue;
	    			sql = " select FSplitNetValue,FTradeDate from " + pub.yssGetTableName("tb_ta_trade") + " where FPortClsCode = " + dbl.sqlString(clsPortCode) +
	    					" and fportcode = " + dbl.sqlString(portCode) + " and FConvertType = '3' and FTradeDate <= " + dbl.sqlDate(this.dDate);
	    			rsInner = dbl.openResultSet(sql);
	    			Date xiachaiDate = YssFun.parseDate("1901-01-01");
	    			double discountRatio = 1;
	    			double preDiscount = 0;
	    			double afterDiscount = 0;
	    			if(rsInner.next())
	    			{
	    				discountRatio = rsInner.getDouble("FSplitNetValue");
	    				xiachaiDate = rsInner.getDate("FTradeDate");
	    			}
	    			dbl.closeResultSetFinal(rsInner);
	    			//NAV_基础类累计 = ｛NAV_基础类今日+［(DX1-1)+(DX2-1)+...(DXn-1)］*0.5｝* L1 + ［(DZ1-1)+(DZ2-1)+(DZm-1)］*0.5
	    			//下拆日之前计算
	    			sql = " select * from " + pub.yssGetTableName("tb_data_MultiClassNet") + " where fnavdate in" +
	    					"(select distinct FBaseDate from " + pub.yssGetTableName("tb_ta_LeverShare") + " where FConversionDate <" +
  				  			dbl.sqlDate(xiachaiDate) + " and fportcode = " + dbl.sqlString(portCode) + "and fconversiontype='1')" +
	    					" and fportcode = " + dbl.sqlString(portCode) + " and  FCurycode in " + 
	    					" (select fportclscode from " + pub.yssGetTableName("tb_ta_portcls") + " where fsharecategory = 1 " +
	    				    " and fportcode = " + dbl.sqlString(portCode) + " and fcheckstate = 1)" + 
  				  			" and ftype = '02' order by fnavdate ";
		  			rsInner = dbl.openResultSet(sql);
		  			while(rsInner.next())
		  			{
		  				preDiscount += rsInner.getDouble("FClassNetValue") - 1;
		  			}
		  			dbl.closeResultSetFinal(rsInner);
		  			//下拆日之后计算
		  			sql = " select * from " + pub.yssGetTableName("tb_data_MultiClassNet") + " where fnavdate in "  +
		  				"(select distinct FBaseDate from " + pub.yssGetTableName("tb_ta_LeverShare") + " where FConversionDate > " +
			  			dbl.sqlDate(xiachaiDate) + " and FBaseDate<= " + dbl.sqlDate(this.dDate) +   " and fportcode = " + dbl.sqlString(portCode) + 
			  			" and fconversiontype='1') and fportcode = " + dbl.sqlString(portCode) + " and  FCurycode in " + 
			  			" (select fportclscode from " + pub.yssGetTableName("tb_ta_portcls") + " where fsharecategory = 1 " +
    				    " and fportcode = " + dbl.sqlString(portCode) + " and fcheckstate = 1)" +  
				  		" and ftype = '02'  order by fnavdate ";
		  			rsInner = dbl.openResultSet(sql);
		  			while(rsInner.next())
		  			{
		  				afterDiscount += rsInner.getDouble("FClassNetValue") - 1;
		  			}
		  			dbl.closeResultSetFinal(rsInner);
		  			accumNetValue = (accumNetValue +  afterDiscount * 0.5) * discountRatio + preDiscount * 0.5;
		  			
	    		}
	    		
	            //昨日单位净值
	            yesNetValue = this.getClassNetValue(clsPortCode, YssFun.addDay(this.dDate, -1), "02", "FClassNetValue");
	            if(yesNetValue==0)
	            {
	            	yesNetValue = 1;
	            }
	            //单位净值增长率
	            growthRate = YssD.sub(YssFun.roundIt(curNetValue, digt),YssFun.roundIt(yesNetValue, digt))/YssFun.roundIt(yesNetValue, digt);
	            
	            sql = " select * from " + pub.yssGetTableName("tb_data_MultiClassNet") + " where fnavdate in (select Ftradedate from " +
	            	  pub.yssGetTableName("tb_ta_trade") + " where  FPortClsCode = " + dbl.sqlString(clsPortCode) + " and FPortCode =  " + dbl.sqlString(portCode) +
	            	  " and fselltype = '00') and FCurycode = " + dbl.sqlString(clsPortCode) + " and ftype = '02' and FPortCode = " + dbl.sqlString(portCode);
	            rsInner = dbl.openResultSet(sql);
	            if(rsInner.next())
	            {
	            	 //累计净值增长率
		            accGrowthRate = (YssFun.roundIt(curNetValue, digt) - YssFun.roundIt(rsInner.getDouble("FClassNetValue"), digt))/YssFun.roundIt(rsInner.getDouble("FClassNetValue"), digt);
	            }
	            dbl.closeResultSetFinal(rsInner);
	            
	            curNetValue = YssFun.roundIt(curNetValue, 24);//按要求保留净值位数 
	            //edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
	            if(!inShowItem || alShowItems.contains("022")){
		            //高精度单位净值
				    setPreparedStatement(pst,clsPortCode, "022",YssFun.roundIt(curNetValue, 14), portCuryCode);
				    pst.executeUpdate();
	            }
			    
//	            curNetValue = YssFun.roundIt(curNetValue, digt);//按要求保留净值位数 
	            //edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
	            if(!inShowItem || alShowItems.contains("02")){
		            //单位净值
	            	setPreparedStatement(pst,clsPortCode, "02",YssFun.roundIt(curNetValue, digt), portCuryCode);
				    pst.executeUpdate();
	            }
			    
//			    accumNetValue = YssFun.roundIt(accumNetValue, digt);
	            //edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
			    if(!inShowItem || alShowItems.contains("36")){
				    //累计单位净值
			    	setPreparedStatement(pst,clsPortCode, "36",YssFun.roundIt(accumNetValue, digt), portCuryCode);
				    pst.executeUpdate();
			    }
			    
	            //edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
			    if(!inShowItem || alShowItems.contains("09")){
				    //单位净值增长率
			    	setPreparedStatement(pst,clsPortCode, "09",growthRate, portCuryCode);
				    pst.executeUpdate();
			    }
			    
	            //edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
			    if(!inShowItem || alShowItems.contains("11")){
				    //累计单位净值增长率
			    	setPreparedStatement(pst,clsPortCode, "11",accGrowthRate, portCuryCode);
				    pst.executeUpdate();
			    }
			    
			    amount = getTaStock(clsPortCode, this.dDate, 0);				
	            //edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
			   	if(!inShowItem || alShowItems.contains("05")){//不按照显示项显示 或者 按照显示项并且显示项包含实收资本 story 2727 modify by zhouwei 20120620
			   		//各分级的总份额	
			   		setPreparedStatement(pst,clsPortCode, "05",amount, portCuryCode);
				    pst.executeUpdate();
			   	}
	            //edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
				if (!inShowItem || alShowItems.contains("06"))
			   	{
				   	//标识－份额总净值
				    //某个组合的FOFFSET字段（轧差计算份额总净值）值为0时，表示按正常计算份额总净值，公式为“本位币基金净值×该币种份额 / 基金总份额”
				    //若值为1，表示进行轧差计算，公式为“本位币基金净值 – 所有其他币种的份额总净值之和”
				    //一个投资组合只能有且只有一个分级组合的FOFFSET字段值能为1
				    //=================================
				    if (rs.getString("FOFFSET").trim().equals("0"))
				    {
				    	setPreparedStatement(pst,clsPortCode, "06",YssFun.roundIt(amount * YssFun.roundIt(curNetValue, digt), 2), portCuryCode);
				    }
				    else
				    {
				    	setPreparedStatement(pst,clsPortCode, "06",YssD.sub(getTotalValue(this.dDate), getNetValueOffset(this.dDate,this.portCode)), portCuryCode);
				    }
				    //=================end================
				    pst.executeUpdate();
			   	}
	            //edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
				if (!inShowItem || alShowItems.contains("37"))
			   	{
				 
				    //标识－份额总净值
				    //某个组合的FOFFSET字段（轧差计算份额总净值）值为0时，表示按正常计算份额总净值，公式为“本位币基金净值×该币种份额 / 基金总份额”
				    //若值为1，表示进行轧差计算，公式为“本位币基金净值 – 所有其他币种的份额总净值之和”
				    //一个投资组合只能有且只有一个分级组合的FOFFSET字段值能为1
				    //=================================
				    if (rs.getString("FOFFSET").trim().equals("0"))
				    {
				    	setPreparedStatement(pst,clsPortCode, "37",YssFun.roundIt(amount * curNetValue, 2), portCuryCode);
				    }
				    else
				    {
				    	setPreparedStatement(pst,clsPortCode, "37",YssD.sub(getTotalValue(this.dDate), 
				    						getNetValueOffset(this.dDate,this.portCode)), portCuryCode);
				    }
				    //=================end================
				    pst.executeUpdate();
			   	}
                 //edit by songjie 2014.05.23 STORY #16993 需求上海-[海富通]QDIIV4.0[紧急]201405022001
                 double totalNetValue = getTotalValue(this.dDate);//总的本位币资产净值
				//如果显示项包含 资产总净值（原币）  add by yeshenghong 20130802  story4151  资产净值通过公式计算
				
                 double clsNavofYB = 0;//分级基金资产净值（原币）
					
                 double totalTAAmount = getTAStockAmount(this.dDate,this.portCode);//总的TA库存数量
                 double baseRate = this.getSettingOper().getCuryRate(this.dDate, rs.getString("fcurycode"), this.portCode, YssOperCons.YSS_RATE_BASE);
                 //组合汇率
                 double portRate = this.getSettingOper().getCuryRate(this.dDate, "", this.portCode, YssOperCons.YSS_RATE_PORT); 
					
                 String clsNavFormula = rs.getString("fportclsnav");//资产净值公式
                 if(clsNavFormula!=null&&!clsNavFormula.equals(""))
                 {
                	 if (rs.getString("FBeanId") != null && rs.getString("FBeanId").length() > 0) {
                		 leverCfg = (LeverGradeFundCfg) pub.getOperDealCtx().getBean(
                				 rs.
                				 getString("FBeanId"));
                		 leverCfg.setYssPub(pub);
                		 leverCfg.init(clsNavFormula,portCode,clsPortCode,dDate);
                		 clsNavofYB = YssFun.roundIt(leverCfg.calcGradeFundNetValue(), 2);
                	 }else
                	 {
                		 throw new YssException("请设置【基础参数模块的Spring调用】,引用BeanId: LeverGradeFundCfg!");
                	 }
					
                 }else if(curNetValue==0||amount==0){
                	 clsNavofYB = 0;
                 }else{
                	 //非钆差分级组合的资产净值_原币   Round(“02基金单位净值”  * “05实收资本” ,2)
                	 //= 基金总资产净值_本位币 * （非钆差分级组合的的份额数量 / 总的份额数量）* 组合汇率 / 基础汇率；
                	 if (rs.getString("FOFFSET").trim().equals("0"))//如果是非钆差分级组合
                	 {
                		 clsNavofYB = YssFun.roundIt(curNetValue * amount, 2);
                	 }
                	 else//如果是钆差分级组合，需钆差计算该分级资产净值
                	 {
							//分级组合总净值_原币（轧差） 
					    	// =（基金总资产净值_本位币 – ∑（其他非钆差分级组合的份额总净值_本位币））* 组合汇率 / 基础汇率；
							//其他非钆差分级组合的份额总净值_本位币
					    	// = ∑（基金总资产净值_本位币 * （各分级的份额数量 / 总的份额数量））；
					    	//edit by songjie 2014.05.23 BUG #94184 QDV4赢时胜上海(开发)2014年5月20日01_B * 组合汇率 / 基础汇率
							//edit by songjie 2014.05.23 STORY #16993 需求上海-[海富通]QDIIV4.0[紧急]201405022001 添加 calUnSpecialSumNet 参数
                		 clsNavofYB = YssFun.roundIt(YssD.div(YssD.mul(calUnSpecialSumNet(totalNetValue,"38"),portRate),baseRate),2);
                	 }
                 }
	            //edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
				if(!inShowItem || alShowItems.contains("38")){
					setPreparedStatement(pst,clsPortCode, "38",clsNavofYB, portCuryCode);
					pst.executeUpdate();
				}
			    
				/**Start 20140320 modified by liubo.STORY #15862 需求深圳-(招商基金)QDII估值系统V4.0(紧急)20140318*/
				//计算011资产净值(本币计价)
				//大致计算公式为： Round（“38 资产净值(原币)” *分级币种基础汇率/分级币种组合汇率， 本分级单位净值保留位数）
	            //edit by songjie 2014.05.24 BUG #94385 QDV4海富通基金2014年05月26日01_B
				//修改显示项判断逻辑 由indexOf 改为 ArrayList.contains
				if (!inShowItem || alShowItems.contains("011"))
				{
					double dStdclsNavofYB = 0;
					//基础汇率
					baseRate = this.getSettingOper().getCuryRate(this.dDate, rs.getString("fcurycode"), this.portCode, YssOperCons.YSS_RATE_BASE);
				   	//组合汇率
					portRate = this.getSettingOper().getCuryRate(this.dDate, "", this.portCode, YssOperCons.YSS_RATE_PORT); 
					
					//--- add by songjie 2014.05.23 STORY #16993 需求上海-[海富通]QDIIV4.0[紧急]201405022001 start---//
					//如 该分级的份额数量为0 或 单位净值为0, 则 直接取0
					if(curNetValue==0||amount==0){
						dStdclsNavofYB = 0;
					}else{
						if(rs.getString("FOFFSET").trim().equals("0")){//如果是非钆差分级组合
							//--- add by songjie 2014.05.23 STORY #16993 需求上海-[海富通]QDIIV4.0[紧急]201405022001 end---//
							dStdclsNavofYB = YssD.div(YssD.mul(clsNavofYB, baseRate), portRate);
							dStdclsNavofYB = YssFun.roundIt(dStdclsNavofYB, 2);
							//--- add by songjie 2014.05.23 STORY #16993 需求上海-[海富通]QDIIV4.0[紧急]201405022001 start---//
						}else{//如果是钆差组合，则
							//“011资产净值(本币计价)”=母基金本币资产净值 - ∑（其它分级的"011资产净值(本币计价)"）
							dStdclsNavofYB = calUnSpecialSumNet(totalNetValue,"011");
						}
					}
					//--- add by songjie 2014.05.23 STORY #16993 需求上海-[海富通]QDIIV4.0[紧急]201405022001 end---//

					setPreparedStatement(pst,clsPortCode, "011",dStdclsNavofYB, portCuryCode);
					
					pst.executeUpdate();
				}
				/**End 20140320 modified by liubo.STORY #15862 需求深圳-(招商基金)QDII估值系统V4.0(紧急)20140318*/
	    	}
//	    	conn.commit();
//            bTrans = false;
//            conn.setAutoCommit(true);
    	} catch (YssException e) {
    		throw new YssException(e.getMessage());
		} finally{
			dbl.closeResultSetFinal(rs);
//			dbl.endTransFinal(conn, bTrans);
		}
    }
    
    /*
     * 设置多CLASS净值数据 
     * add by yeshenghong story 3759 20130609
     * */
	private void setPreparedStatement(PreparedStatement pst,
			String clsPortCode, String ftype, double netValue, String curyCode)
			throws YssException  {
   		try {
			pst.setDate(1, YssFun.toSqlDate(this.dDate));
			pst.setString(2, this.portCode);
		    pst.setString(3, ftype); 
		    pst.setString(4, curyCode);
		    pst.setString(5, clsPortCode);
		    pst.setDouble(6, 0.0);
		    pst.setDouble(7, 0.0);
		    pst.setDouble(8, 0.0);
		    pst.setDouble(9, 0);
		    pst.setDouble(10, YssFun.roundIt(netValue, 14));
		    pst.setInt(11, 1);
		    pst.setString(12, pub.getUserCode());
		    pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
		    pst.setDouble(14, 0.0); 
		    pst.setDouble(15, 0.0); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new YssException(e.getMessage());
		}
	   
    	
    }
        
    
    /**
     * add by huangqirong 2012-08-30 story #2782
     * 生成多class组合分级估值占比和估值增值
     * modify by fangjiang story 3264 2012.11.19
     * modify by fangjiang 2012.12.05 story 3264
     * */
    private void dealClassNetValue()  throws YssException, SQLException  {
    	String strSql = "";
    	String sql = "";
    	String curyCode = "";
    	String clsPortCode = "";
    	ResultSet rs = null;    	
    	PreparedStatement pst = null;
        boolean bTrans = true; 
        Connection conn = dbl.loadConnection();
        conn.setAutoCommit(false);
        double totalNetValues = 0 ;
        double curyNetValue = 0;
        double upNetValue = 0 ; //分子
        double downNetValue = 0; //分母
        double lastNetValue = 0; //最后一个分子
        strSql = " insert into " + pub.yssGetTableName("Tb_Data_MultiClassNet") +
		         " (FNAVDate,FPortCode,FType,FPortCuryCode,FCuryCode,FNetValueBeforeFee,FManageFee,FTrusteeFee," +
		         " FNetValue,FClassNetValue,FCheckState,FCreator,FCreateTime,FSTFee,FFXJFee) " + 
		         " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "; 

        pst = conn.prepareStatement(strSql);
        //昨日总的资产净值
        totalNetValues = getNetValue(YssFun.addDay(this.dDate, -1), "01");
        double inTotal = 0;
        double outTotal = 0;
        if(this.flag == 3){
        	inTotal = this.getTaTradeData(this.dDate, this.portCode, "", "00,01,04,08", "", 
        			                      "sum(FSettleMoney * FBaseCuryRate/FPortCuryRate)", "TA发生额");
        	
        }else if(this.flag == 4){
        	inTotal = this.getTaTradeData(this.dDate, this.portCode, "", "01,04,08", "", 
                                          "sum(FSettleMoney * FBaseCuryRate/FPortCuryRate)", "TA发生额");
        	inTotal += this.getTaTradeData(this.dDate, this.portCode, "", "00", "", 
                    					  "sum(fsellamount)", "TA发生额");
        	
        }
        outTotal = this.getTaTradeData(this.dDate, this.portCode, "", "03", "", 
				   					   "sum(FSettleMoney * FBaseCuryRate/FPortCuryRate)", "TA发生额");
        outTotal += this.getTaShData(this.dDate, this.portCode, "", "02,05", "", "TA发生额");
        //分母
        downNetValue = YssD.sub(YssD.add(totalNetValues,
        								 inTotal
        								),
        								outTotal);
        lastNetValue = downNetValue; //初始最后一个分级等于 分母
    	try{    		
    	
	    	sql =  " select a.*,b.fshowitem,b.FOFFSET From " + pub.yssGetTableName("Tb_TA_ClassFundDegree") +
	    		   " a join (select fshowitem,FPORTCLSCODE,FPortCode,FOFFSET,fparrentcode from "+pub.yssGetTableName("tb_ta_portcls")
	    		 + " where fcheckstate=1 and (fportclsschema = 'abLimited' or fportclsschema = " 
	    		 + dbl.sqlString(this.flag==4?"inNetValue_chinaM":"inNetValue_china")//modified by yeshenghong 20130813 story4151
	    		 + " )) b on a.FPortCode=b.FPortCode and a.FPORTCLSCODE=b.FPORTCLSCODE"
	    	     + " where a.FPortCode = " + dbl.sqlString(this.portCode) + " and a.FCheckState = 1 "
	    		 + " and a.FPORTCLSCODE<>' ' and a.FCURYCODE<>' ' and b.fparrentcode = ' '"   	
	    		 + " order by FOFFSET asc ";
			rs = dbl.openResultSet(sql);
	    	while(rs.next()){
	    		//币种
	    		curyCode = rs.getString("FCuryCode"); 
	    		//分级组合代码
	    		clsPortCode = rs.getString("FPortClsCode");
			   	
				if(rs.getString("FOFFSET").equals("0")){
					//昨日分级净值
				   	curyNetValue = this.getClassNetValue(clsPortCode, YssFun.addDay(this.dDate, -1), "01", "FNetValue");	
				   	if(this.flag == 3){
			        	inTotal = this.getTaTradeData(this.dDate, this.portCode, clsPortCode, "00,01,04,08", "",
			        			                      "sum(FSettleMoney * FBaseCuryRate/FPortCuryRate)", "TA发生额");
			        	outTotal = this.getTaTradeData(this.dDate, this.portCode, clsPortCode, "03","",
		                           					   "sum(FSettleMoney * FBaseCuryRate/FPortCuryRate)", "TA发生额");
			        	outTotal += this.getTaShData(this.dDate, this.portCode, clsPortCode, "02,05", "","TA发生额");
			        	
			        }else if(this.flag == 4){
			        	inTotal = this.getTaByParrentCode(this.dDate, this.portCode, clsPortCode, "01,04,08", 
			                                              "sum(FSettleMoney * FBaseCuryRate/FPortCuryRate)","TA发生额");
			        	inTotal += this.getTaByParrentCode(this.dDate, this.portCode, clsPortCode, "00",
			                    					       "sum(fsellamount)", "TA发生额");
					   	outTotal = this.getTaByParrentCode(this.dDate, this.portCode, clsPortCode, "03",
			   					                           "sum(FSettleMoney * FBaseCuryRate/FPortCuryRate)", "TA发生额");
				   		outTotal += this.getTaShByParrentCode(this.dDate, this.portCode, clsPortCode, "02,05", "TA发生额");
			        }
				    //分子		   	    
					upNetValue = YssD.sub(YssD.add(curyNetValue,inTotal),
											outTotal);			   	
					lastNetValue = YssD.sub(lastNetValue, upNetValue);
				}else{
					//最后一个分级则为这个
					upNetValue = lastNetValue ;
				}
				
		   		pst.setDate(1, YssFun.toSqlDate(this.dDate));
			    pst.setString(2, this.portCode);
			    pst.setString(3, "zb"); 
			    pst.setString(4, getPortCuryCode());
			    pst.setString(5, clsPortCode);
			    pst.setDouble(6, 0.0);
			    pst.setDouble(7, 0.0);
			    pst.setDouble(8, 0.0);
			    pst.setDouble(9, upNetValue);
			    pst.setDouble(10, downNetValue);
			    pst.setInt(11, 1);
			    pst.setString(12, pub.getUserCode());
			    pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
			    pst.setDouble(14, 0.0); 
			    pst.setDouble(15, 0.0); 
			    pst.executeUpdate();
	    	}
	    	conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
    	} catch (YssException e) {
    		throw new YssException(e.getMessage());
		} finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
    }
        
    /**
     * 获取资产净值 或分级资产净值
     * add by huangqirong 2012-08-31 story #2782
     * */
    //获得各Class的资产净值
    public double getClassNetValue(String clsPortCode, Date d , String type , String fieldName) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	double result = 0.0;
        sqlStr = " select "+ fieldName +" from " + pub.yssGetTableName("tb_data_MultiClassNet") +
                 " where FType = " + dbl.sqlString(type) + " and FCuryCode = " + dbl.sqlString(clsPortCode) + 
                 " and FPortCode = " + dbl.sqlString(this.portCode) + " and FNavDate = " + dbl.sqlDate(d);       
        try{
        	rs = dbl.queryByPreparedStatement(sqlStr);
        	while(rs.next()){
        		result = rs.getDouble(fieldName);
        	}        	
        }catch (Exception e) {
        	throw new YssException(e.getMessage());
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        return result;
    }
    
    /**
     * 分级发生额
     * add by huangqirong 2012-08-31 story #2782
     * modify by fangjiang 2012.12.05 story 3264
     * */
    private double getTaTradeData(java.util.Date date , String portCode , String portClsCode , String selltype ,
    		                      String curyCode , String singleField , String msg)  throws YssException{
    	String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;  
        try {        	
            strSql = " select " + singleField + " as fmoney from " + pub.yssGetTableName("tb_ta_trade") + " ta " +
            		 " where ta.FCheckState = 1 and ta.fconfimdate =  " +dbl.sqlDate(date) +
            		 " and ta.fportcode =  " + dbl.sqlString(portCode) +
            		 (portClsCode.length() > 0 ? " and ta.fportclscode = " + dbl.sqlString(portClsCode) : "" )+
            		 " and ta.fselltype in(" + operSql.sqlCodes(selltype) + ")"+
            		 (curyCode.length() > 0 ? " and ta.fcurycode = " + dbl.sqlString(curyCode) : "" );
            rs = dbl.queryByPreparedStatement(strSql); 
            if (rs.next()) {
                dReturn = rs.getDouble("fmoney");
                dReturn = YssD.round(dReturn, 2);
            } 
            return dReturn; 
        } catch (Exception e) {
            throw new YssException("获取" + msg + "出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //add by fangjiang 2012.12.05 story 3264
    private double getTaShData(java.util.Date date, String portCode, String portClsCode, String selltype, 
    		                   String curyCode, String msg)  throws YssException{
    	String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;  
        try {        	
            strSql = " select sum((fsellmoney-ftradefee1-ftradefee2)*FBaseCuryRate/FPortCuryRate) as fyfshk, " +
            		 " sum(ftradefee1*FBaseCuryRate/FPortCuryRate) as fyfshf, sum(ftradefee2*FBaseCuryRate/FPortCuryRate) as fshfsr from "  + pub.yssGetTableName("tb_ta_trade") + " ta " +
            		 " where ta.FCheckState = 1 and ta.fconfimdate =  " +dbl.sqlDate(date) +
            		 " and ta.fportcode =  " + dbl.sqlString(portCode) +
            		 (portClsCode.length() > 0 ? " and ta.fportclscode = " + dbl.sqlString(portClsCode) : "" )+
            		 " and ta.fselltype in(" + operSql.sqlCodes(selltype) + ")"+
            		 (curyCode.length() > 0 ? " and ta.fcurycode = " + dbl.sqlString(curyCode) : "" );
            rs = dbl.queryByPreparedStatement(strSql); 
            if (rs.next()) {
                dReturn = YssD.add(rs.getDouble("fyfshk"),rs.getDouble("fyfshf"),rs.getDouble("fshfsr"));
                dReturn = YssD.round(dReturn, 2);
            } 
            return dReturn; 
        } catch (Exception e) {
            throw new YssException("获取" + msg + "出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * 根据父级分组合代码获得Ta数据
     * add by fangjiang story 3264 2012.11.19
     * modify by fangjiang 2012.12.05 story 3264
     */
    private double getTaByParrentCode(java.util.Date date, String portCode, String portClsCode, String selltype, 
    								  String singleField, String msg) throws YssException{
    	String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;  
        try {        	
            strSql = " select " + singleField + " as fmoney from " + pub.yssGetTableName("tb_ta_trade") + " ta " +
                     " join " + pub.yssGetTableName("tb_ta_portcls") + " b on ta.fportclscode = b.fportclscode " +
            		 " where ta.FCheckState = 1 and ta.fconfimdate =  " +dbl.sqlDate(date) +
            		 " and ta.fportcode =  " + dbl.sqlString(portCode) +           		 
            		 " and ta.fselltype in(" + operSql.sqlCodes(selltype) + ")"+
            		 " and b.FCheckState = 1 " +
            		 (portClsCode.length() > 0 ? " and b.fparrentcode = " + dbl.sqlString(portClsCode) : "" );
            rs = dbl.queryByPreparedStatement(strSql); 
            if (rs.next()) {
            	dReturn = rs.getDouble("fmoney");
                dReturn = YssD.round(dReturn, 2);
            }
            return dReturn; 
        } catch (Exception e) {
            throw new YssException("获取" + msg + "出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //add by fangjiang 2012.12.05 story 3264
    private double getTaShByParrentCode(java.util.Date date, String portCode, String portClsCode, String selltype, 
            							String msg)  throws YssException{
		String strSql = "";
		ResultSet rs = null;
		double dReturn = 0;  
		try {        	
			strSql = " select sum((fsellmoney-ftradefee1-ftradefee2)*FBaseCuryRate/FPortCuryRate) as fyfshk, " +
				     " sum(ftradefee1*FBaseCuryRate/FPortCuryRate) as fyfshf, sum(ftradefee2*FBaseCuryRate/FPortCuryRate) as fshfsr from "  + pub.yssGetTableName("tb_ta_trade") + " ta " +
			         " join " + pub.yssGetTableName("tb_ta_portcls") + " b on ta.fportclscode = b.fportclscode " +
			   		 " where ta.FCheckState = 1 and ta.fconfimdate =  " +dbl.sqlDate(date) +
			   		 " and ta.fportcode =  " + dbl.sqlString(portCode) +           		 
			   		 " and ta.fselltype in(" + operSql.sqlCodes(selltype) + ")"+
			   		 " and b.FCheckState = 1 " +
			   		 (portClsCode.length() > 0 ? " and b.fparrentcode = " + dbl.sqlString(portClsCode) : "" );
			rs = dbl.queryByPreparedStatement(strSql); 
			if (rs.next()) {
				dReturn = YssD.add(rs.getDouble("fyfshk"),rs.getDouble("fyfshf"),rs.getDouble("fshfsr"));
				dReturn = YssD.round(dReturn, 2);
			} 
			return dReturn; 
		} catch (Exception e) {
			throw new YssException("获取" + msg + "出现异常！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    
}
