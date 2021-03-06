package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.yss.commeach.EachRateOper;
import com.yss.main.cashmanage.SavingOutAccBean;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.income.paid.PaidAccIncome;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.platform.pfoper.pubpara.ParaWithPubBean;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.parasetting.PeriodBean;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.pojo.dayfinish.AccPaid;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * <p>Title: </p>
 *
 * <p>Description: 对定存业务的业务处理</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj
 */
public class OperSavingManage
    extends BaseOperManage {
    EachRateOper rateOper = null;
    private String sPara =""; //合并太平版本调整 by leeyu 20100831
    
    public OperSavingManage() {
    }

    /**
     * 执行业务处理
     *
     * @throws YssException
     * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage
     *   method
     */
    public void doOpertion() throws YssException {
    	//添加参数用于控制到期时是否产生到期的本金调拨金额  合并太平版本调整 by leeyu 20100831
    	CtlPubPara pubPara = new CtlPubPara();
		pubPara.setYssPub(pub);
		sPara = pubPara.getNavType();// 通过净值表类型来判断    
		// 合并太平版本调整 by leeyu 20100831
        createCashTransfer(); //此处产生资金调拨，包括支付的应收应付数据
        createCashPayRecAdmin();//此处产生现金应收应付
        /**shashijie 2012-7-19 STORY 2796 提前支取利息产生现金应收应付*/
		doCashPayRecAdmin();
		/**end*/
    }

	/**
     * 初始化信息
     *
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage
     *   method
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate; //调拨日期
        this.sPortCode = portCode; //组合
    }

    /**
     * 生成定存的资金调拨
     */
    private void createCashTransfer() throws YssException {
        String createCashTransferSql = null;
        String createPaidSql = null;
        String createPrincipalExtSql = null; //add by fangjiang 2010.11.29 STORY #97 协议存款业务需支持提前提取本金的功能
//        ResultSet rs = null;//edit by licai 20101116 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。 
        CashTransAdmin cashtransAdmin = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;

        boolean haveFixData = false;

        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            cashtransAdmin = new CashTransAdmin(); //生成资金调拨控制类
            cashtransAdmin.setYssPub(pub);

            rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
            
            
            //add by fangjiang 2010.11.29 STORY #97 协议存款业务需支持提前提取本金的功能    
            createPrincipalExtSql = buildPrincipalExtSql(); //协议定存本金提取
            createPrincipalExtCashTransfer(cashtransAdmin, createPrincipalExtSql, analy1, analy2, analy3); //产生协议定存本金提取的资金调拨
            //----------------------------------
            if (isOverDayProfession()) {//通用业务参数判断是否启用新模式
            	/**shashijie 2011.04.25 STORY #815 在提前支取日产生一笔反向的到期日资金调拨以冲减开始日产生的资金调拨*/
            	createMatureCashTransferMaxDay(cashtransAdmin, createPrincipalExtSql,
            			analy1, analy2, analy3,2);//产生到期日的资金调拨
            	/**end*/
			}
            
            createCashTransferSql = buildFirstSavingInAccSql(); //首期(包括买入)            
            createFirstCashTransfer(cashtransAdmin, createCashTransferSql, analy1, analy2, analy3); //产生交易当天的资金调拨
            
            if (isOverDayProfession()) {//通用业务参数判断是否启用新模式
            	/**shashijie 2011.04.19 STORY #815   产生到期日的资金调拨*/
            	createMatureCashTransferMaxDay(cashtransAdmin, createCashTransferSql,
            			analy1, analy2, analy3,1);//产生到期日的资金调拨
            	/**end*/
			}
            
            createCashTransferSql = buildMatureSavingOutAccSql(); //到期 资金调拨.
            createMatureCashTransfer(cashtransAdmin, createCashTransferSql, analy1, analy2, analy3); //产生到期的自动资金调拨数据       
            
            createCashTransferSql = buildSellSavingInAccSql(); //转出
            haveFixData = createSellCashTransfer(cashtransAdmin, createCashTransferSql, analy1, analy2, analy3); //产生转出后的资金调拨

            if (haveFixData) { //判断是否存在转出数据,若存在，则收益支付
                createPaidSql = createSellPaidSql(analy1, analy2, analy3);
                createPaidOperation(createPaidSql, analy1, analy2, analy3); //获取转出收益支付的数据
            }

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer")); //lock table
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer")); //lock table
            
            //------ add by wangzuochun 2010.08.25  MS01606    定存业务处理后，不能删除历史资金调拨数据    QDV4赢时胜(测试)2010年08月12日07_B
            //modify by nimengjing 2011.1.5 BUG #737 存款利息的资金调拨数据被删除 
            if(haveFixData){
                delOldCashTransfer(cashtransAdmin);
            }
            //----------------------end BUG #737----------------------
            //---------------MS01606---------------//
            
            /*autoSaving类型的资金调拨，说明此资金调拨的生成是在业务处理时产生的。在手工做到期处理时产生的资金调拨不能将此类的资金调拨删除*/
            //cashtransAdmin.insert(dDate, "autoSaving,principalExt,SavMature,CashPay,Saving", -1, ""); //插入资金调拨,以调拨日期和关联编号类型,自动生成的来删除已有自动在业务处理时产生的资金调拨 modify by fangjiang 2010.11.29 STORY #97 协议存款业务需支持提前提取本金的功能 //modify huangqirong 2012-06-04 bug #4679 
            
            /**shashijie 2012-7-19 STORY 2796 增加TakeInterest编号类型*/
            //BUG4872执行调度方案后收益支付产生的那笔收入信息没有了  定存业务处理后，不能删除历史资金调拨数据
            cashtransAdmin.insert(dDate, "autoSaving,principalExt,SavMature,Saving,TakeInterest", -1, "");
			/**end*/
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
            if(cashtransAdmin.getAddList()==null || cashtransAdmin.getAddList().size()==0){
            	this.sMsg="        当日无业务";
            }
            
        } catch (Exception ex) {
            throw new YssException("生成定存的资金调拨出现异常！", ex);
        } finally {
//            dbl.closeResultSetFinal(rs);//edit by licai 20101116 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。 
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    /**判断通用业务参数是否启用新模式 shashijie,2011-4-25  */
    private boolean isOverDayProfession() throws YssException, SQLException {
    	ParaWithPubBean pubBean = new ParaWithPubBean();
        pubBean.setYssPub(pub);
        String FCtlGrpCode = "isBeforehandHandle";//控件组
        String FCtlCode =  "cboIsTrue";//控件
        String FCtlValue = "1,1";//控件值
        String FParaId = null;//排序编号
        ResultSet rs = null;
        try {
        	rs = pubBean.getResultSetByLike(FCtlGrpCode, FCtlCode, FCtlValue, FParaId);
        	if (rs.next()) {
				return true;
			}
		} catch (YssException e) {
			dbl.closeResultSetFinal(rs);
		} finally{
			dbl.closeResultSetFinal(rs);
		}
		return false;
	}

	/**预先产生结束日的资金调拨
	 *@param direction 方向,1为正常  2为反向 */
	private void createMatureCashTransferMaxDay(CashTransAdmin cashtransAdmin,
			String sql, boolean analy1, boolean analy2, boolean analy3,
			int direction) throws YssException {
		ResultSet rs = null;
        
        TransferBean transfer = null;//资金调拨
        TransferSetBean transferSet = null;//资金调拨子表
        
        ArrayList subTransfer = null; //实例化放置资金调拨子数据的容器
        double matureMoney = 0D; //到期金额
        double interestValue = 0D;//到期利息
        try {
        	rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
            	//产生资金调拨"利息"金额,并将计算后的利息返回
        		interestValue = setInterest(cashtransAdmin,subTransfer,transfer,transferSet,
        					analy1, analy2, analy3,rs,direction);
        		if (direction==1) {//开始日
        			//到期金额(本金+利息)
            		matureMoney = YssD.add(rs.getDouble("FInMoney"), interestValue);
				} else if (direction==2) {//提前支取日
					matureMoney = YssD.add(rs.getDouble("FOutMoney"), interestValue);
				}
        		
                //产生本金+利息的资金调拨
                setMoneyAndIntereset(matureMoney,cashtransAdmin,subTransfer,transfer,transferSet,
                		analy1,analy2,analy3,rs,direction);
            }
        } catch (Exception e) {
            throw new YssException("生成定存的资金调拨出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	
	/**shashijie ,2011-4-22 产生到期日本金+利息的资金调拨 */
	private void setMoneyAndIntereset(double matureMoney,
			CashTransAdmin cashtransAdmin, ArrayList subTransfer,
			TransferBean transfer, TransferSetBean transferSet, boolean analy1,
			boolean analy2, boolean analy3, ResultSet rs,int direction) throws YssException,SQLException {
		ResultSet subRs = null;
		String outTransferSql = "";//查询流入活期账户的SQL语句
		
		subTransfer = new ArrayList();//实例化放置资金调拨子数据的容器
		transfer = new TransferBean();//资金调拨主表实体Bean
		//设置资金调拨数据
    	setTransfer(rs,transfer,YssOperCons.YSS_ZJDBLX_InnerAccount,YssOperCons.YSS_ZJDBZLX_COST_SAVING);
		//到期定存账户,开始日流出提前支取流入
		transferSet = setMatureTransferSet(rs, analy1, analy2, analy3, false, matureMoney); //获取流入资金调拨子数据
		if (direction==2) {//如果是提前支取日
			transferSet.setSCashAccCode(rs.getString("FInCashAccCode"));
		}
        subTransfer.add(transferSet); //将资金调拨子数据放入容器
        //到期活期账户
        if (direction==1) {//开始日
        	outTransferSql = buildMatureSavingInAccSql(rs.getString("FNum"));
        	//活期账户
            subRs = dbl.queryByPreparedStatement(outTransferSql);
            if (subRs.next()) {
        		transferSet = setMatureTransferSet(subRs, analy1, analy2, analy3, true, matureMoney); //获取流出资金调拨子数据
                subTransfer.add(transferSet); //将资金调拨子数据放入容器
            }
            dbl.closeResultSetFinal(subRs);
		} else if(direction==2){//提前支取日
			transferSet = setMatureTransferSet(rs, analy1, analy2, analy3, true, matureMoney);
			//设定流入现金账户为提前支取的流出账户
			transferSet.setSCashAccCode(rs.getString("FOutCashAccountCode"));
			subTransfer.add(transferSet); //将资金调拨子数据放入容器
		}
        
        transfer.setSubTrans(subTransfer); //将子数据放入资金调拨中
        
        //这里调整，如果是国内主流版本则产生到期本金的资金调拨，如果是太平版本暂不产生本金的资金调拨数据 合并太平版本调整 by leeyu 20100831
        if (sPara != null && sPara.trim().equalsIgnoreCase("new")) {
        	CashTransAdmin ca = new CashTransAdmin();
        	ca.setYssPub(pub);
        	ca.addList(transfer);
        	ca.insert("", rs.getDate("FMatureDate"), rs.getDate("FMatureDate"), YssOperCons.YSS_ZJDBLX_InnerAccount,YssOperCons.YSS_ZJDBZLX_COST_SAVING
    				, "", rs.getString("FNum"), "", "", "", "autoSavingCom", "", 1, "", "", 0, "", "", "", true, rs.getString("FNum"), "");
        }
	}

	/**shashijie ,2011-4-22  生成到期日利息的资金调拨*/
	private double setInterest(CashTransAdmin cashtransAdmin,
			ArrayList subTransfer, TransferBean transfer, TransferSetBean transferSet,
			boolean analy1, boolean analy2, boolean analy3,ResultSet rs,
			int direction) throws YssException,SQLException {
        double interestValue = 0D;//到期利息
		subTransfer = new ArrayList();//实例化放置资金调拨子数据的容器
    	transfer = new TransferBean();//资金调拨主表实体Bean
    	//设置资金调拨数据
    	setTransfer(rs,transfer,YssOperCons.YSS_ZJDBLX_Income,YssOperCons.YSS_ZJDBZLX_DEPOSIT_INTEREST);
    	//产生利息的资金调拨
    	transferSet = setTransferSet(rs, analy1, analy2, analy3);
    	if (direction==1) {//开始日
    		transferSet.setIInOut(1);//流入
		} else if(direction==2){//提前支取日
			//流出账户为定存账户
			transferSet.setIInOut(-1);//流出
			transferSet.setSCashAccCode(rs.getString("FOutCashAccountCode"));
		}
    	
    	//普通定存
		if ("4".equals(rs.getString("FSavingType"))){
			//如果计息方式为固定利率，存入金额+收益支付利息（所含利息与计提利息）
            if("fixrate".equals(rs.getString("FCALCTYPE"))){
            	interestValue = getInMoneyInterest(rs);
         	}else{ //固定收益:存入金额+所含利息 + 固定收益
         		interestValue = YssD.add(rs.getDouble("FIncludeInterest") , rs.getDouble("FRecInterest"));
         	}
        } else if("2".equals(rs.getString("FSavingType"))){//协定定存
        	if (direction==1) {//开始日
        		interestValue = getInMoneyInterestSaving(rs);
    		} else if(direction==2){//提前支取日
    			if (rs.getDouble("FOutMoney")>rs.getDouble("FInMoney")) {
					//如果提前支取金额大于本金(业务不考虑这种情况,但是我这里还是判断了)
    				interestValue = getInMoneyInterestSaving(rs);
				} else {//提取部分(全部)本金
					interestValue = getInMoneyInterestSavingPrincapl(rs);
				}
    		}
        	
        } else {
			return 0.0 ;
		}
    	//资金调拨利息金额
		transferSet.setDMoney(interestValue);
		subTransfer.add(transferSet); //将资金调拨子数据放入容器
		transfer.setSubTrans(subTransfer); //将子数据放入资金调拨中
		//重新new以便删除之前创建的资金调拨:2011-05-10修改
		CashTransAdmin ca = new CashTransAdmin();
		ca.setYssPub(pub);
		ca.addList(transfer);
		ca.insert("", rs.getDate("FMatureDate"), rs.getDate("FMatureDate"), YssOperCons.YSS_ZJDBLX_Income,YssOperCons.YSS_ZJDBZLX_DEPOSIT_INTEREST
				, "", rs.getString("FNum"), "", "", "", "autoSavingCom", "", 1, "", "", 0, "", "", "", true, rs.getString("FNum"), "");
        return interestValue;
	}

	/** shashijie,2011-4-26,获取协定定存到期日(提前支取后余下的本金)的利息*/
	private double getInMoneyInterestSavingPrincapl(ResultSet rs) throws YssException,SQLException {
		double result = 0D;//应得所有利息
        double oldInterest = 0D; //已经计提的利息
        double conventionInterest = 0D; //被提取后本金计算的利息
        double allResult = 0D;//总利息
        
        //存入金额*计息公式*期限/年天数
        allResult = getInMoneyInterest(rs);
        //存入金额*计息公式*已提前支取天数/年天数
        oldInterest = getOldInMoneyInterest(rs);
        //(存入金额-提取本金)*计息公式*剩余天数/年天数
        conventionInterest = getNewInMoneyInterest(rs);
        
        result = YssD.sub(allResult, oldInterest,conventionInterest);
		return result;
	}

	/** shashijie ,2011-4-26 计算出剩余本金可得利息 */
	private double getNewInMoneyInterest(ResultSet rs) throws YssException,SQLException {
		double subMoney = YssD.sub(rs.getDouble("FInMoney"), rs.getDouble("FOutMoney"));
		//总利息
		double allResult = calcCommSavingWithFixRate(rs, dDate,subMoney);
		//每日利息
		double everyResult = getEveryResult(rs,allResult);
		double dateNum = 0D;//天数
		
		//初始期间设置,不为空时才处理
		PeriodBean period = new PeriodBean();
		getPeriodBean(period,rs);
    	if(period.getDayInd().equalsIgnoreCase("0")){//计头不计尾
    		dateNum = YssFun.dateDiff(dDate,rs.getDate("FMatureDate"));
		} else {
			dateNum = YssD.add(YssFun.dateDiff(dDate,rs.getDate("FMatureDate")),1);
		}
    	//因得利息
		double interestValue = getInterest(rs, everyResult,dateNum);
		return interestValue;
	}

	/**shashijie ,2011-4-26,计算已经计提的利息*/
	private double getOldInMoneyInterest(ResultSet rs) throws YssException , SQLException {
		//总利息
		double allResult = calcCommSavingWithFixRate(rs, dDate,rs.getDouble("FInMoney"));
		//每日利息
		double everyResult = getEveryResult(rs,allResult);
		double dateNum = 0D;//天数
		
		//初始期间设置,不为空时才处理
		PeriodBean period = new PeriodBean();
		getPeriodBean(period,rs);
    	if (period.getDayInd().equalsIgnoreCase("1")){//计尾不计头
    		dateNum = YssD.sub(YssFun.dateDiff(rs.getDate("FSavingDate"),dDate),1);
    	} else {
    		dateNum = YssFun.dateDiff(rs.getDate("FSavingDate"),dDate);
		}
    	//因得利息
		double interestValue = getInterest(rs, everyResult,dateNum);
		return interestValue;
	}

	/**shashijie ,2011-4-26 重载计算应得利息方法(天利息*实际计提天数)*/
	private double getInterest(ResultSet rs, double result, double dateNum) throws YssException,SQLException {
		double allInterest;//总利息
    	//(每日利息*总期限天数)保留位数
    	allInterest = YssD.mul(result, dateNum);
    	allInterest = this.getSettingOper().reckonRoundMoney(rs.getString("FRoundCode"), allInterest);
		return allInterest;
	}

	/**协定定存利息 shashijie ,2011-4-22 */
	private double getInMoneyInterestSaving(ResultSet rs) throws YssException,SQLException {
		double result = 0D;//应得所有利息
        double basicInterest = 0D; //基本金额利息
        double conventionInterest = 0D; //协议金额利息
        double allResult = 0D;//总利息
        double everyResult = 0D;//每日利息
        
        //基本金额×基本汇率
        basicInterest = YssD.mul(rs.getDouble("FBasicMoney"), rs.getDouble("FBasicRate")); 
        //存入金额-基本额度
        double subValue = YssD.sub(rs.getDouble("FInMoney"), rs.getDouble("FBasicMoney"));
        //总利息(年利息,算的是一年的利息)
        conventionInterest = calcCommSavingWithFixRate(rs, dDate, subValue);
        allResult = YssD.add(basicInterest, conventionInterest); //基本金额利息 + 协议金额利息
		everyResult = getEveryResult(rs,allResult);//每日利息(除以设置的年天数)
		result = getInterest(rs, everyResult);//应得所有利息(乘以实际计息天数)
		return result;
	}

	/**获取固定利率的利息 shashijie ,2011-4-22 */
	private double getInMoneyInterest(ResultSet rs) throws YssException , SQLException {
    	double allResult = 0D;//总利息(年利息,算的是一年的利息)
		double everyResult = 0D;//每日利息(除以设置的年天数)
		double interestValue = 0D;//应得所有利息(乘以实际计息天数)
		
		allResult = calcCommSavingWithFixRate(rs, dDate,rs.getDouble("FInMoney"));
		everyResult = getEveryResult(rs,allResult);
		interestValue = getInterest(rs, everyResult);
		
		return interestValue;
	}

	/** 设置资金调拨主表类对象 shashijie ,2011-4-20 */
	private void setTransfer(ResultSet rs, TransferBean transfer,String type,String sonType ) throws YssException {
		if (transfer==null) {
			return;
		}
        try {
            transfer.setDtTransDate(rs.getDate("FMatureDate")); //业务日期
            transfer.setDtTransferDate(rs.getDate("FMatureDate")); //调拨日期
            transfer.setStrTsfTypeCode(type);//资金调拨类型
            transfer.setStrSubTsfTypeCode(sonType);//资金调拨子类型
            transfer.setFNumType("autoSavingCom"); //编号类型
            transfer.setFRelaNum(rs.getString("FNum")); //关联编号
            transfer.setSavingNum(rs.getString("FNum")); //定存编号
            transfer.setSrcCashAccCode("FCashAccCode");//来源帐户代码
            transfer.checkStateId = 1;//状态
            transfer.setDataSource(1);
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
	}
	

	/**
	 * MS01164   QDV4赢时胜（测试）2010年05月26日02_B 
	 * 产生定存现金应收应付     
	 */
	private void createCashPayRecAdmin() throws YssException{
		String createCashPayRecAdminSql = null;
		ResultSet rs = null;
		boolean bTrans = false;
		CashPayRecAdmin cashpayrecadmin = null;
		Connection conn = dbl.loadConnection();
		String DigitType = "";

		try {
			// 添加参数控制保留小数位数，合并太平版本调整 byleeyu 20100827
			CtlPubPara pubpara = new CtlPubPara();
			pubpara.setYssPub(pub);
			DigitType = pubpara.getNavType();
			// 添加参数控制保留小数位数，合并太平版本调整 byleeyu 20100827
			cashpayrecadmin = new CashPayRecAdmin(); // 生成现金应收应付控制类
			cashpayrecadmin.setYssPub(pub);
			rateOper = new EachRateOper(); // 新建获取利率的通用类
			rateOper.setYssPub(pub);
			createCashPayRecAdminSql = buildSavingInAccSql();
			createcashtrans(cashpayrecadmin, createCashPayRecAdminSql);

			conn.setAutoCommit(false);
			bTrans = true;
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_CashPayRec"));
			// add by rujiangpeng 20100611 MS01164 QDV4赢时胜（测试）2010年05月26日02_B
			// edited by zhouxiang MS01301 修改日志：
			// 将本界面所有的YSS_ZJDBZLX_DE_RecInterest 改成了YSS_ZJDBZLX_DC_RecInterest
			// 添加参数控制保留小数位数，合并太平版本调整 byleeyu 20100827
			// add by lidaolong #526 QDV4长信基金2011年1月14日01_A
			// 重复进行本金提前提取时，删除之前已产生的数据
			cashpayrecadmin.delete("", dDate, dDate,
					YssOperCons.YSS_ZJDBLX_Income,
					/** shashijie 2012-7-25 STORY 2796 */
					"02DE", "", "", "", "", "", "", -1, 0, "",
					"autoSaving,TakeInterest", "");// modify huangqirong
													// 2012-06-04 bug #4679
					/** end */

			// ---end ---
			cashpayrecadmin.insert(dDate, YssOperCons.YSS_ZJDBLX_Rec,
					/**shashijie 2012-7-25 STORY 2796 */
					YssOperCons.YSS_ZJDBZLX_DE_RecInterest+","+
					/**end*/
					YssOperCons.YSS_ZJDBZLX_DC_RecInterest, "", 1, false, "",
					"autoSaving,TakeInterest", DigitType.trim()
							.equalsIgnoreCase("new") ? false : true);

			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception ex) {
			throw new YssException("生成现金应收应付出现异常！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
    }
	
	/**
	 * add by huangqirong 2012-06-14 bug #4679
	 * */
	private String getDeferralAccCount (String groupCode , String portCode , String sourcesAccs){
		String tempAccs = "";
		ResultSet rs = null;
		if(sourcesAccs.trim().length() > 0 && !"null".equalsIgnoreCase(sourcesAccs.trim())){
			sourcesAccs = operSql.sqlCodes(sourcesAccs);
			String sql = "select * from tb_" + groupCode + "_para_cashaccount where FPortCode = " + dbl.sqlString(portCode) + 
						 " and FCashAcccode in (" + sourcesAccs +" )";
			try {
				rs = dbl.openResultSet(sql);
				while(rs.next()){
					String type = rs.getString("FAccType");
					String subType = rs.getString("FSubAccType");
					if("01".equalsIgnoreCase(type) && "0102".equalsIgnoreCase(subType)){
						tempAccs += rs.getString("FCashAcccode") + ",";
					}
				}
				if(tempAccs.trim().length() > 0)
					tempAccs = tempAccs.substring(0, tempAccs.length() -1);
				rs.close();
			} catch (Exception e) {
				
			}			
		}
		return tempAccs ;
	}
	
	/**
	 * add by huangqirong 2012-06-14 bug #4679
	 * 删除现金应收应付数据
	 * */
	private void deleteCashPayRec(String groupCode , String portCode , String TsfTypeCode , String SubTsfTypeCode , String date , String Acc){
		String nums = "";
		String sql ="";
		ResultSet rs = null;
		
		sql = "select WMSYS.WM_CONCAT(FNum) as FNum from tb_" + groupCode + "_Data_CashPayRec where FPortCode = " + dbl.sqlString(portCode) + 
				" and FTsfTypeCode = " + dbl.sqlString(TsfTypeCode) + 
		  		" and FSubTsfTypeCode = " + dbl.sqlString(SubTsfTypeCode) + 
		  		" and FTransDate = " + dbl.sqlDate(date) +
		  		" and FCashAcccode in( " + operSql.sqlCodes(Acc) + ")";
        try {
        	rs =dbl.openResultSet(sql);
        	if(rs.next()){
        		nums = rs.getString("FNum");
        	}
        	rs.close();
        	if(nums!= null){
        		if(nums.trim().length() > 0 && !"null".equalsIgnoreCase(nums.trim())){
        			sql = "delete from tb_" + groupCode + "_Data_CashPayRec where FNum in (" + operSql.sqlCodes(nums) + ")";
        			dbl.executeSql(sql);
        		}
        	}
        } catch (Exception e) {
            System.out.println(" 删除现金应收应付数据出错\r\n" + e.getMessage());
        } finally {
        	
        }
	}
	
	/**
	 * add by huangqirong 2012-06-14 bug #4679
	 * */
	private String getAssetCode(String groupCode , String portCode){
		String assetcode = "";
		String sql = "select * from tb_" + groupCode + "_para_portfolio where FPortCode = " + dbl.sqlString(portCode);
		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sql);
			if(rs.next()){
				assetcode = rs.getString("FAssetCode");
			}
		} catch (Exception e) {
			System.out.println("查询组合设置中的资产代码出错：\r\n"+e.getMessage());
		}finally{
			
		}
		return assetcode;
	}
    

    /**
     * 生成转出的资金调拨
     * @param cashtransAdmin CashTransAdmin
     * @param sql String
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @throws YssException
     * @return boolean
     */
    private boolean createSellCashTransfer(CashTransAdmin cashtransAdmin, String sql, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        return createFirstCashTransfer(cashtransAdmin, sql, analy1, analy2, analy3); //此处调用首期的方法，其逻辑与其是一致的
    }

    /**
     * 拼装获取转出流入的资金调拨的数据
     * @return String
     */
    private String buildSellSavingInAccSql() {
        StringBuffer buf = new StringBuffer();
        buf.append("select distinct *  from (");
        buf.append("select saving.*,cash.FCuryCode from (select FNum,FSavingDate as FBargainDate,FSavingDate as FTransferDate,FInMoney, FCashAccCode,"); //到期日期
        buf.append("FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FIncludeInterest,FTradeType,FSavingType,FATTRCLSCODE "); //定存类型、交易类型  NO.125 用户需要对组合按资本类别进行子组合的分类   add by jiangshichao 添加所属分类
        buf.append(" from ");
        buf.append(pub.yssGetTableName("Tb_cash_savinginacc"));
        buf.append(" where FCheckState = 1 ");
        buf.append(" and FTradeType = ").append(dbl.sqlString(YssOperCons.YSS_SAVING_SELL)); //获取卖出的定存数据
        buf.append(" and FSavingDate = ").append(dbl.sqlDate(dDate)); //筛选到期日期
        buf.append(") saving left join (");
        buf.append("select FCashAccCode,FCuryCode from ");
        buf.append(pub.yssGetTableName("Tb_PARA_Cashaccount"));
        buf.append(" where FCheckState = 1) cash on cash.FCashAccCode = saving.FCashAccCode");
        buf.append(") x");
        return buf.toString();
    }

    /**
     * 生成定存到期的资金调拨
     * @param cashtransAdmin CashTransAdmin
     * @param sql String
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @throws YssException
     * @return //edit by licai 20101116 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。原方法需要重复执行sql,将收益支付过程放进资金调拨方法内置执行
     */
    private void createMatureCashTransfer(CashTransAdmin cashtransAdmin, String sql, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        ResultSet rs = null;
        ResultSet AccountRs=null;
        ResultSet subRs = null;
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
        String outTransferSql = null;
        double matureMoney = 0D; //到期金额
        try {
        	//---------edit by songjie 20110125 BUG 979 QDV4赢时胜(测试)2011年1月21日01_B------------------//
        	//edit by licai 20101116 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。
			// edited by zhouxiang MS01455 循环账户产生利息的资金调拨
			String seachDateStr = "select  fnum,fsavingdate,fcashacccode,FSavingType from "
					+ pub.yssGetTableName("TB_cash_savinginacc")
					+ " where fmaturedate=" + dbl.sqlDate(this.dDate);
			AccountRs = dbl.queryByPreparedStatement(seachDateStr);
			while (AccountRs.next()) {
				seachDateStr = createMaturePaidSql(AccountRs.getString("fnum"),
						AccountRs.getDate("fsavingdate"), AccountRs
								.getString("fcashacccode"), analy1, analy2,
						analy3);// 资金调拨
				//产生到期资金调拨与现金应收应付
				createPaidOperation(seachDateStr, analy1, analy2, analy3); // 获取到期收益支付的数据
			}
			// edit by licai 20101116 BUG #191==========================end=
          //---------edit by songjie 20110125 BUG 979 QDV4赢时胜(测试)2011年1月21日01_B------------------//
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
//                if (null != rs.getString("FSavingNum")) { //若此条定存已经使用手工方式进行处理，则不再此处进行处理
//                    continue; //执行下一条数据
//                }
                /**shashijie 2011.05.11 STORY #815 &#34;固定利率&#34;类型的定存需要改成同 “固定收益”模式一样*/
                if (isOverDayProfession() && //如果使用新业务流程计算定存,则普通定存4与协定定存2到期日不需产生资金调拨
                		("2".equals(rs.getString("FSavingType")) || "4".equals(rs.getString("FSavingType"))) ) {
					continue;
				}
                /**end*/
                transfer = setTransfer(rs); //获取资金调拨数据
                /*下面调整了定存类型的标识，通知定存为3*/
                if (3 == rs.getInt("FSavingType") && YssOperCons.YSS_SAVING_FIRST.equalsIgnoreCase(rs.getString("FTradeType"))) { //若为通知定存的首期数据，不需要进行到期处理。（通知取款时产生到期数据）
                    continue;
                }
                
                
                //modify by zhangfa 20100919 MS01745    不同业务日期做定存业务，产生的资金调拨正确    QDV4上海2010年9月16日01_B    
                if (YssOperCons.YSS_SAVING_BUY.equalsIgnoreCase(rs.getString("FTradeType"))
                		//edit by licai 20101116 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。
                		||YssOperCons.YSS_SAVING_FIRST.equalsIgnoreCase(rs.getString("FTradeType"))) { //到期交易类型为首期时//add by licai 20101117 BUG #300 BUG #300 定存到期需要根据固定利率将利息连同成本转到活期账户中 
               
                		//add by fangjiang 2010.11.29 TASK #1096::协议存款业务需支持提前提取本金的功能
                        double foutmoney = 0D;
                        ResultSet rs1 = null;
                        String sql1 = " select sum(foutmoney) as foutmoney from " + pub.yssGetTableName("TB_Cash_Consavingpriext") 
                        				+ " where fcheckstate = 1 and Fconsavingnum = " + dbl.sqlString(rs.getString("fnum"))
                        				/**shashijie 2012-7-25 STORY 2796 增加条件,判断为本金提出时才取出流出金额*/
										+" And FTakeType = '0' "
										/**end*/
                        				+ " and fextdate <= " + dbl.sqlDate(dDate);
                        try {
                        	rs1 = dbl.queryByPreparedStatement(sql1);
                            while (rs1.next()) {
                            	foutmoney = rs1.getDouble("foutmoney");
                            }
                        } catch(Exception e) {
                        	throw new YssException("获取协议定存本金提取出错！", e);
                        } finally {
                        	 dbl.closeResultSetFinal(rs1);
                        }
                        //--------------
                        // add by lidaolong ; #526 QDV4长信基金2011年1月14日01_A
                        //当普通定存的本金已被提前支取，则不需要进行如此处理，因为它已经被“提前到期处理”了
                        /**shashijie 2012-7-19 STORY 2796 若是提取利息则不需要跳出*/
                        if ("4".equals(rs.getString("FSavingType")) && 0 < foutmoney && !"1".equals(rs.getString("FTakeType").trim())){
                        	continue;
                        }
						/**end*/
                    if("fixrate".equals(rs.getString("FCALCTYPE"))){//如果计息方式为固定利率，存入金额+收益支付利息（所含利息与计提利息）
                        //---end -- -
                		matureMoney = YssD.add(YssD.sub(rs.getDouble("FInMoney"), foutmoney), rs.getDouble("FMONEY")); //modify by fangjiang 2010.11.29 TASK #1096::协议存款业务需支持提前提取本金的功能
                	}else{
                    //edit by licai 20101116 BUG #191 =========================================end==                		
                		matureMoney = YssD.add(rs.getDouble("FInMoney"), rs.getDouble("FIncludeInterest") , 
                				rs.getDouble("FRecInterest")); //存入金额+所含利息 + 固定收益
                		
                		//--- add by songjie 2012.10.10 BUG 5877 QDV4赢时胜(上海)2012年09月27日03_B start---//
                        /**shashijie 2012-7-19 STORY 2796 提前支取利息*/
    					//若有提前支取利息,则需要扣除
                        if ("1".equals(rs.getString("FTakeType").trim())){
                        	matureMoney = YssD.sub(matureMoney, rs.getDouble("Foutmoney"));
    					}
    					/**end*/
                        //--- add by songjie 2012.10.10 BUG 5877 QDV4赢时胜(上海)2012年09月27日03_B end---//
                	}
                    
                } else { //其他类型
                    matureMoney = YssD.add(rs.getDouble("FInMoney"), rs.getDouble("FRecInterest")); //存入金额+利息
                }
                //---------------------------MS01745----------------------------------------------------------------------   
                
                transferSet = setMatureTransferSet(rs, analy1, analy2, analy3, false, matureMoney); //获取流入资金调拨子数据
                subTransfer = new ArrayList(); //实例化放置资金调拨子数据的容器
                subTransfer.add(transferSet); //将资金调拨子数据放入容器
                outTransferSql = buildMatureSavingInAccSql(rs.getString("FNum"));
                /*获取流出的资金调拨数据*/
                //--------------------------
                subRs = dbl.queryByPreparedStatement(outTransferSql);
                if (subRs.next()) { //获取金额最到的一个账户来作为流入账户
                    transferSet = setMatureTransferSet(subRs, analy1, analy2, analy3, true, matureMoney); //获取流出资金调拨子数据
                    subTransfer.add(transferSet); //将资金调拨子数据放入容器
                }

                dbl.closeResultSetFinal(subRs);

                transfer.setSubTrans(subTransfer); //将子数据放入资金调拨中
                //这里调整，如果是国内主流版本则产生到期本金的资金调拨，如果是太平版本暂不产生本金的资金调拨数据 合并太平版本调整 by leeyu 20100831
                if (sPara != null && sPara.trim().equalsIgnoreCase("new")) {
                	cashtransAdmin.addList(transfer);
                }
            }  
        } catch (Exception e) {
            throw new YssException("生成定存的资金调拨出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(AccountRs);//关闭游标 by leeyu 20100909
        }
    }
    
    /**获取每日利息
     * shashijie 2011.04.14 */
    private double getEveryResult(ResultSet rs, double allResult) throws YssException,SQLException {
    	int iDays = 0;
    	//初始期间设置,不为空时才处理
		PeriodBean period = new PeriodBean();
		getPeriodBean(period,rs);
		
    	//一下为获取年天数，以计算天利息。前提为不是固定收益的计息方式
        if (null == rs.getString("FCALCTYPE") || rs.getString("FCALCTYPE").trim().length() == 0
            || !"fixincome".equalsIgnoreCase(rs.getString("FCALCTYPE"))) { //当没有计息方式设置，或者计息方式不是固定收益时，需要转换。
            if (period.getDayOfYear() == -1 || period.getPeriodType() == 1) { //此为默认条件
                iDays = 360; //默认为360天
            } else {
                iDays = period.getDayOfYear(); //获取期间设置的天数
            }
            allResult = YssD.div(allResult, iDays); //年利息除以年天数，得到每日利息
        }
        return allResult;
	}
    
    /**初始期间设置对象 */
    private void getPeriodBean(PeriodBean period, ResultSet rs) throws YssException,SQLException {
    	if (period==null) {
    		throw new YssException("期间设置对象不能为空!");
		}
    	//现金账户期间设置
    	if (rs.getString("FPeriodCode")==null || rs.getString("FPeriodCode").trim().length()<1 
    			|| rs.getString("FPeriodCode").trim().equalsIgnoreCase("null")) {
    		
			throw new YssException("请先维护现金帐户【" + rs.getString("FPeriodCode") + "】的期间设置");
		}
        period.setYssPub(pub);
        period.setPeriodCode(rs.getString("FPeriodCode"));
        period.getSetting();
	}

	/**获取应得的所有利息*/
    private double getInterest(ResultSet rs ,double result) throws YssException,SQLException {
    	double allInterest;//总利息
    	double dateNum;//总期限天数
    	//初始期间设置,不为空时才处理
		PeriodBean period = new PeriodBean();
		getPeriodBean(period,rs);
    	if (period.getDayInd().equalsIgnoreCase("2")){//头尾均计
    		dateNum = YssD.add(YssFun.dateDiff(rs.getDate("FSavingDate"),rs.getDate("FMatureDate")),1);
    	} else {
    		dateNum = YssFun.dateDiff(rs.getDate("FSavingDate"),rs.getDate("FMatureDate"));
		}
    	//(每日利息*总期限天数)保留位数
    	allInterest = YssD.mul(result, dateNum);
    	allInterest = this.getSettingOper().reckonRoundMoney(rs.getString("FRoundCode"), allInterest);
    	return allInterest;
	}
    

	/**计算固定利率的普通定存利息 shashijie 2011.04.13 STORY #815 
	 * FInMoney 总金额
     */
    private double calcCommSavingWithFixRate(ResultSet rs, java.util.Date dDate ,
    		double FInMoney) throws YssException, SQLException {
        double result = 0D;
        //计息公式与舍入设置
        if (rs.getString("FFormulaCode") != null && rs.getString("FRoundCode") != null) {
			//(计息公式,舍入设置,总金额,业务日期)
			result = this.getSettingOper().calMoneyByPerExp(rs.getString("FFormulaCode"), rs.getString("FRoundCode"),
	                FInMoney, dDate); //使用计息公式进行计算
        } else {
            throw new YssException("存款编号为【" + rs.getString("FNum") +
                                   "】的存款没有设置计息公式或舍入方式，不能进行内部公式计算，请检查!");
        }

        return result;
    }
    

    /**
     * 设置定存首期的资金调拨数据
     * @param rs ResultSet
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @return TransferSetBean
     * @throws YssException
     */
    private TransferSetBean setMatureTransferSet(ResultSet rs, boolean analy1, boolean analy2, boolean analy3, boolean inOut, double money) throws YssException {
        TransferSetBean transferset = null;
        try {
            transferset = setTransferSet(rs, analy1, analy2, analy3);
            transferset.setDMoney(money);
            //---
            if (inOut) { //true时，说明为流入帐号

                transferset.setIInOut(1);
            } else { //流出帐号

                transferset.setIInOut( -1);
            }
            //---
        } catch (Exception e) {
            throw new YssException("设置定存首期的资金调拨数据出现异常！", e);
        }
        return transferset;
    }

    /**
     * 拼装获取到期流出的资金调拨的数据
     * @return String
     */
    private String buildMatureSavingOutAccSql() {
        StringBuffer buf = new StringBuffer();
        //edit by licai 20101116 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。 
        /**shashijie 2012-7-19 STORY 2796 */
        buf.append("select a.*,c.fmoney , Nvl(d.Foutmoney, 0) Foutmoney, Nvl(d.Ftaketype,' ') Ftaketype ");
		/**end*/
        
        buf.append(" from");
        buf.append("(");
        //edit by licai 20101116 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。==end
        buf.append("select distinct *  from (");
        buf.append("select saving.*,cash.FCuryCode ,cash.FPeriodCode FROM (select FNum,FMatureDate as FBargainDate,FMatureDate as FTransferDate,FInMoney, FCashAccCode,"); //到期日期
        buf.append("FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FATTRCLSCODE," +//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 
        //modify by zhangfa 20100919 	MS01745    不同业务日期做定存业务，产生的资金调拨正确    QDV4上海2010年9月16日01_B    	
        		"FIncludeInterest,FRecInterest," +
        //------------------------------------------------------------------------------------------------------- 
        		/**shashijie 2011.04.13 增加计息公式与舍入设置获取等等值*/
        		" FFormulaCode,FRoundCode, FSavingDate, FMatureDate, FDepdurCode , " +
        		/**end*/
        		"FTradeType,FSavingType,FCALCTYPE"); //定存类型、交易类型
        buf.append(" from ");
        buf.append(pub.yssGetTableName("Tb_cash_savinginacc"));
        buf.append(" where FCheckState = 1 ");
        buf.append(" and FTradeType in (").append(dbl.sqlString(YssOperCons.YSS_SAVING_FIRST)).append(",").append(dbl.sqlString(YssOperCons.YSS_SAVING_BUY)).append(",").
            append(dbl.sqlString(YssOperCons.YSS_SAVING_CIRCUCATCH)).append(")"); //获取首期、买入、通知取款的定存数据
        buf.append(" and FMatureDate = ").append(dbl.sqlDate(dDate)); //筛选到期日期
        buf.append(") saving left join (");
        buf.append("select FCashAccCode,FCuryCode ,FPeriodCode FROM ");
        buf.append(pub.yssGetTableName("Tb_PARA_Cashaccount"));
        buf.append(" where FCheckState = 1) cash on cash.FCashAccCode = saving.FCashAccCode");
        buf.append(") x");
        buf.append(" left join (select FSavingNum, FRelaNum from "); //关联资金调拨数据，已获取手工生成的资金调拨。当为手工生成的资金调拨，则在业务处理中不进行处理
        buf.append(pub.yssGetTableName("Tb_cash_transfer"));
        buf.append(" where FNumType = 'SavMature' "); //此处的SavMature为手工生成到期资金调拨的标准。
        buf.append(" and FTransferDate = ").append(dbl.sqlDate(dDate)).append(" and fcheckstate=1 ");//edit by zhouwei 20120318 增加状态的判断
        buf.append(" ) handTransfer on x.FNum = handTransfer.FSavingNum ");
        //edit by licai 20101116 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。 
//        buf.append("");        
        buf.append(" )a");
        buf.append(" left join (select FSavingNum,FRelaNum,Fnum ");
        buf.append(" from ");
        buf.append(pub.yssGetTableName("Tb_cash_transfer"));
        buf.append(" where FNumType='autoSaveInterest' and FTransferDate=").append(dbl.sqlDate(dDate));
        buf.append(" )t1 on a.FNum=t1.FSavingNum");
        buf.append(" left join ");
        buf.append(pub.yssGetTableName("Tb_Cash_Subtransfer")).append(" c");
        buf.append(" on c.fnum=t1.fnum");
        //edit by licai 20101116 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。==end==
        /**shashijie 2012-7-19 STORY 2796 关联提前支取表 */
        buf.append("  Left Join (Select Sum(D1.Foutmoney) Foutmoney," +
        		" D1.Fconsavingnum," +
        		" D1.Ftaketype" +
        		" From "+pub.yssGetTableName("Tb_Cash_Consavingpriext")+" D1" +
				" Where D1.Fcheckstate = 1" +
				" Group By D1.Fconsavingnum, D1.Ftaketype) d On d.Fconsavingnum = a.Fnum ");
		/**end*/
        return buf.toString();
    }

    /**
     * 拼装获取到期流入的资金调拨的数据
     * @param sInNum String
     * @return String
     */
    private String buildMatureSavingInAccSql(String sInNum) {
        StringBuffer buf = null;
        buf = buildStandSavingOutAccSqlBuf(sInNum);
        buf.append(" order by FOutMoney desc"); //以流出金额为排序条件
        return buf.toString();
    }
	/**
	 * MS01164   QDV4赢时胜（测试）2010年05月26日02_B 
	 * 生成定存的现金应收应付
	 */
	private void createcashtrans(CashPayRecAdmin cashpayrecadmin, String sql) throws YssException{
    	 ResultSet rs = null;
    	 CashPecPayBean  cashpecpay = null;
         try{
        	 rs = dbl.queryByPreparedStatement(sql);
             while (rs.next()){
            	  cashpecpay=setCashPecPay(rs);
            	  cashpayrecadmin.addList(cashpecpay);
             }
         } catch (Exception e) {
             throw new YssException("生成定存的资金调拨出现异常！", e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }        
    }

    /**
     * 生成首期和买入类型定存的资金调拨
     * @param cashtransAdmin CashTransAdmin
     * @param sql String
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @throws YssException
     * @return boolean
     */
    private boolean createFirstCashTransfer(CashTransAdmin cashtransAdmin, String sql, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        ResultSet rs = null;
        ResultSet subRs = null;
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
        String outTransferSql = null;
        boolean haveFirstData = false; //判断是否存在首期数据
        boolean isBuy = false; //判断是否为买入类型
        
        try {
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
                haveFirstData = true; //若存在，则将其赋值为true                
                transfer = setTransfer(rs); //获取资金调拨数据
                transferSet = setFirtTransferSet(rs, analy1, analy2, analy3, true); //获取流入资金调拨子数据
                subTransfer = new ArrayList(); //实例化放置资金调拨子数据的容器
                subTransfer.add(transferSet); //将资金调拨子数据放入容器
                if (YssOperCons.YSS_SAVING_BUY.equalsIgnoreCase(rs.getString("FTradeType"))) { //若为买入，则增加一笔利息的流出
                    isBuy = true; //在此设置此bool值的目的是在子循环中，控制只在第一条流出有利息的资金调拨数据
                }
                outTransferSql = buildStandSavingOutAccSql(rs.getString("FNum"));
                /*获取流出的资金调拨数据*/
                //--------------------------
                subRs = dbl.queryByPreparedStatement(outTransferSql);
                while (subRs.next()) {
                    if (isBuy) { //若为第一条流出账户，则产生利息的资金调拨
                        transferSet = setTransferSet(subRs, analy1, analy2, analy3);
                        transferSet.setDMoney(rs.getDouble("FIncludeInterest")); //包含利息
                        transferSet.setIInOut( -1); //流出
                        subTransfer.add(transferSet); //将资金调拨子数据放入容器
                        isBuy = false; //设置为false，以使之后的帐号不会产生多余的资金调拨
                    }
                    transferSet = setFirtTransferSet(subRs, analy1, analy2, analy3, false); //获取流出资金调拨子数据
                    subTransfer.add(transferSet); //将资金调拨子数据放入容器
                }
                dbl.closeResultSetFinal(subRs);
                transfer.setSubTrans(subTransfer); //将子数据放入资金调拨中
                cashtransAdmin.addList(transfer);
            }
        } catch (Exception e) {
            throw new YssException("生成定存的资金调拨出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return haveFirstData;
    }


	/**
     * 拼装获取首期流入的资金调拨的数据
     * @return String
     */
    private String buildFirstSavingInAccSql() {
        StringBuffer buf = new StringBuffer();
        buf.append("select distinct *  from (");
        buf.append("select saving.*,cash.FCuryCode " +
        		", cash.FPeriodCode " + //shashijie 2011.04.19 
        		" from (select FNum,FSavingDate as FBargainDate,FSavingDate as FTransferDate,FInMoney, FCashAccCode,");
        buf.append("FIncludeInterest,FTradeType,FInterestAccCode,"); //增加利息金额和交易类型及利息帐号
        buf.append("FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAttrClsCode");//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22  添加所属分类字段
        /**shashijie 2011.04.19 增加查询字段 */
        buf.append("  ,FSavingDate, FMatureDate, FFormulaCode, FRoundCode, FCalcType ,FRecInterest, FSavingType,  FBasicMoney, FBasicRate ");
        /**end*/
        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("Tb_cash_savinginacc"));
        buf.append(" where FCheckState = 1 ");
        buf.append(" and FTradeType in (").append(dbl.sqlString(YssOperCons.YSS_SAVING_FIRST)).append(",").append(dbl.sqlString(YssOperCons.YSS_SAVING_BUY)).append(")");
        buf.append(" and FSavingDate = ").append(dbl.sqlDate(dDate));
        buf.append(") saving LEFT JOIN (");
        buf.append("select FCashAccCode,FCuryCode " +
        		" ,FPeriodCode " + //shashijie 2011.04.19 
        		" FROM ");
        buf.append(pub.yssGetTableName("Tb_Para_CashAccount"));
        buf.append(" where FCheckState = 1) cash on cash.FCashAccCode = saving.FCashAccCode");
        buf.append(") x");
        return buf.toString();
    }
	//MS01164   QDV4赢时胜（测试）2010年05月26日02_B 
    private String buildSavingInAccSql() {
        StringBuffer buf = new StringBuffer();
        buf.append("select distinct *  from (");
        buf.append("select saving.*,cash.FCuryCode from (select FNum,FSavingDate as FBargainDate,FSavingDate as FTransferDate,FInMoney, FCashAccCode,");
        buf.append("FIncludeInterest,FTradeType,FInterestAccCode,");
        buf.append(" FSavingType,");//add by lidaolong 2011.02.17 #526 QDV4长信基金2011年1月14日01_A
        buf.append("FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FATTRCLSCODE"); //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
        buf.append(" from ");
        buf.append(pub.yssGetTableName("Tb_cash_savinginacc"));
        buf.append(" where FCheckState = 1 ");
        buf.append(" and FTradeType in (").append(dbl.sqlString(YssOperCons.YSS_SAVING_BUY)).append(",'first')");
        //edit by lidaolong #526 QDV4长信基金2011年1月14日01_A
        buf.append(" and (  FSavingDate = ").append(dbl.sqlDate(dDate));
        buf.append(" or fnum in (select distinct fconsavingnum from " + pub.yssGetTableName("TB_Cash_Consavingpriext") + " where fcheckstate=1 " +
        /**shashijie 2012-7-26 STORY 2796 增加本金类型提取判断 */
		" And FTakeType = 0 "+
		/**end*/
        " and fextdate =").append(dbl.sqlDate(dDate)).append(" ))");
        //end  lidaolong 
        buf.append(") saving left join (");
        buf.append("select FCashAccCode,FCuryCode from ");
        buf.append(pub.yssGetTableName("Tb_PARA_Cashaccount"));
        buf.append(" where FCheckState = 1) cash on cash.FCashAccCode = saving.FCashAccCode");
        buf.append(") x");
        //add by lidaolong 2011.02.17 #526 QDV4长信基金2011年1月14日01_A
        buf.append(" left join (select distinct fconsavingnum from ").append(pub.yssGetTableName("TB_Cash_Consavingpriext"));
        buf.append(" ) ext on  ext.fconsavingnum = x.FNum ");
        buf.append(" left join (select distinct fcashacccode,FBal from ").append(pub.yssGetTableName("tb_stock_cashpayrec"));
        buf.append(" where FCheckState = 1 and FSubTsfTypeCode='06DE' and ").append(operSql.sqlStoragEve(dDate)).append("" +
        		") rec  ON rec.fcashacccode =x.FInterestAccCode");
        //---end lidaolong----
        return buf.toString();
    }

    /**
     * 拼装获取标准流出的资金调拨的数据
     * @param sInNum String
     * @return String
     */
    private String buildStandSavingOutAccSql(String sInNum) {
        StringBuffer buf = null;
        buf = buildStandSavingOutAccSqlBuf(sInNum);
        return buf.toString();
    }

    /**
     * 拼装标准的定存流出帐号的sql语句
     * @param sInNum String
     * @return StringBuffer
     */
    private StringBuffer buildStandSavingOutAccSqlBuf(String sInNum) {
        StringBuffer buf = new StringBuffer();
        buf.append("select distinct *  from (");
        buf.append("select saving.*,cash.FCuryCode from ");
        buf.append("(select FNum,FOutMoney,FCashAccCode, ");
        buf.append("FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FATTRCLSCODE");//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22  添加所属分类字段
        buf.append(" from ");
        buf.append(pub.yssGetTableName("Tb_cash_savingoutacc"));
        buf.append(" where FInAccNum = ").append(dbl.sqlString(sInNum));
        buf.append(") saving left join (");
        buf.append("select FCashAccCode,FCuryCode from ");
        buf.append(pub.yssGetTableName("Tb_PARA_Cashaccount"));
        buf.append(" where FCheckState = 1) cash on cash.FCashAccCode = saving.FCashAccCode");
        buf.append(") x");
        return buf;
    }

    /**
     * 设置资金调拨数据
     * @param rs ResultSet 携带资金调拨数据的记录集
     * @return TransferBean 资金调拨
     * @throws YssException
     */
    private TransferBean setTransfer(ResultSet rs) throws YssException {
        TransferBean transfer = null;
        try {
            transfer = new TransferBean();
            transfer.setDtTransDate(rs.getDate("FBargainDate")); //业务日期
            transfer.setDtTransferDate(rs.getDate("FTransferDate")); //调拨日期
            transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
            transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_SAVING);
            transfer.setFNumType("autoSaving"); //编号类型为标准回购
            transfer.setFRelaNum(rs.getString("FNum")); //关联编号
            transfer.setSavingNum(rs.getString("FNum")); //定存编号
            transfer.setSrcCashAccCode("FCashAccCode");
            transfer.checkStateId = 1;
            transfer.setDataSource(1);
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
        return transfer; //返回资金调拨数据
    }
	/**
	 * 设置现金应收应付数据 MS01164   QDV4赢时胜（测试）2010年05月26日02_B 
	 * @param rs ResultSet 携带现金应收应付数据的记录集
	 * @return CashPecPayBean 现金应收应付
	 * @throws YssException
	 */
	private CashPecPayBean setCashPecPay(ResultSet rs) throws YssException{
    	CashPecPayBean cashpecpay = null;
    	try{
    		cashpecpay = new CashPecPayBean();
    		cashpecpay.setTradeDate(rs.getDate("FBargainDate"));//业务日期
    		cashpecpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);//业务类型
    		cashpecpay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_DC_RecInterest);//业务子类型 edited by zhouxiang MS01301
    		//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    		cashpecpay.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
    		//--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    		cashpecpay.setRelaNum(rs.getString("FNum"));//关联编号
    		cashpecpay.setRelaNumType("autoSaving");//关联编号类型    		
    		//add by lidaolong #526 QDV4长信基金2011年1月14日01_A
    		if (rs.getString("FSavingType").equals("4") && rs.getString("fconsavingnum") != null && !rs.getString("fconsavingnum").equals("")){
    			cashpecpay.setMoney(rs.getDouble("FBal"));//金额
    			cashpecpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);//业务类型
    			cashpecpay.setSubTsfTypeCode("02DE");
    			cashpecpay.setTradeDate(dDate);
    		}else {
    			cashpecpay.setMoney(rs.getDouble("FIncludeInterest"));//金额
    		}
    		//end lidaolong
    		cashpecpay.setDataSource(1);//来源标志
    		cashpecpay.checkStateId = 1;
    		cashpecpay.setPortCode(rs.getString("FPortCode"));//组合代码
    		cashpecpay.setNum(rs.getString("FNum"));
    		cashpecpay.setCashAccCode(rs.getString("FInterestAccCode"));//现金账户
    		cashpecpay.setCuryCode(rs.getString("FCuryCode"));//币种代码
    		cashpecpay.setInOutType(1);//方向
    		cashpecpay.setInvestManagerCode(rs.getString("FAnalySiscode1"));
    		//add by rujiangpeng20100611  MS01164   QDV4赢时胜（测试）2010年05月26日02_B 
    		double dBaseRate = this.getSettingOper().getCuryRate(dDate,rs.getString("FCuryCode"), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE); //获取基础汇率
                rateOper.getInnerPortRate(dDate,rs.getString("FCuryCode"),
                                          rs.getString("FPortCode"));
            double dPortRate = rateOper.getDPortRate(); //获取组合汇率
    		cashpecpay.setBaseCuryRate(dBaseRate);//基础汇率
    		cashpecpay.setPortCuryRate(dPortRate);//组合汇率
    		double bacecurymoney = this.getSettingOper().calBaseMoney(cashpecpay.getMoney(),dBaseRate);//edit by lidaolong #526 QDV4长信基金2011年1月14日01_A
    		double portcurymoney = this.getSettingOper().calPortMoney(cashpecpay.getMoney(),dBaseRate,dPortRate,rs.getString("FCuryCode"),dDate,sPortCode); //edit by lidaolong #526 QDV4长信基金2011年1月14日01_A
    		cashpecpay.setBaseCuryMoney(bacecurymoney);
    		cashpecpay.setPortCuryMoney(portcurymoney);
    		
    	}catch (Exception e) {
            throw new YssException("设置现金应收应付数据出现异常！", e);
        }
        return cashpecpay; //返回资金调拨数据
    	
    }

    /**
     * 设置定存首期的资金调拨数据
     * @param rs ResultSet
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @return TransferSetBean
     * @throws YssException
     */
    private TransferSetBean setFirtTransferSet(ResultSet rs, boolean analy1, boolean analy2, boolean analy3, boolean inOut) throws YssException {
        TransferSetBean transferset = null;
        try {
            transferset = setTransferSet(rs, analy1, analy2, analy3);
            //---
            if (inOut) { //true时，说明为流入帐号
                transferset.setDMoney(rs.getDouble("FInMoney"));
                transferset.setIInOut(1);
            } else { //流出帐号
                transferset.setDMoney(rs.getDouble("FOutMoney"));
                transferset.setIInOut( -1);
            }
            //---
        } catch (Exception e) {
            throw new YssException("设置定存首期的资金调拨数据出现异常！", e);
        }
        return transferset;
    }

    /**
     * 设置资金调拨子数据的基本数据
     * @param rs ResultSet 携带资金调拨子数据的记录集
     * @return TransferSetBean 资金调拨子数据
     * @throws YssException
     */
    private TransferSetBean setTransferSet(ResultSet rs, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        TransferSetBean transferSet = null;
        try {
            transferSet = new TransferSetBean();
            double dBaseRate = 1;
            double dPortRate = 1;

            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("FCuryCode"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE); //获取基础汇率

            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                      rs.getString("FPortCode"));
            dPortRate = rateOper.getDPortRate(); //获取组合汇率

            transferSet.setSPortCode(rs.getString("FPortCode"));

            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            transferSet.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            
            //---设置分析代码 ---------------------------------------------------------
            transferSet.setSAnalysisCode1(analy1 ? rs.getString("FAnalysisCode1") : " ");
            transferSet.setSAnalysisCode2(analy2 ? rs.getString("FAnalysisCode2") : " ");
            transferSet.setSAnalysisCode3(analy3 ? rs.getString("FAnalysisCode3") : " ");
            //-----------------------------------------------------------------------

            transferSet.setSCashAccCode(rs.getString("FCashAccCode"));

            /*此处的金额的设置将移至其外包装的方法中*/

            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }

        return transferSet;
    }

    /**
     * 拼装转出的收益数据
     * @param analys1 boolean
     * @param analys2 boolean
     * @param analys3 boolean
     * @return String
     */
    private String createSellPaidSql(boolean analys1, boolean analys2, boolean analys3) {
        StringBuffer buf = new StringBuffer();
        //---- 调整转出的数据的账户获取方式，获取转出账户的信息 ---------------
        buf.append("select saving.*,FCuryCode,FBal,FBaseCuryBal,FPortCuryBal,FMoney,FBaseCuryMoney,FPortCuryMoney,FRelaType from (select inAcc.*, outAcc.Fcashacccode as FOutCashAccCode from "); //增加编号类型
        buf.append(" (select * from ").append(pub.yssGetTableName("Tb_cash_savinginacc"));
        buf.append(" where FCheckState = 1 and FTradeType = ").append(dbl.sqlString(YssOperCons.YSS_SAVING_SELL)).append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode)); //只获取转出的数据
        buf.append(" and FSavingDate = ").append(dbl.sqlDate(this.dDate));
        buf.append(") inAcc left join (select * from ").append(pub.yssGetTableName("Tb_cash_savingOutacc"));
        buf.append(" where FCheckState = 1) outAcc on inAcc.FNum = outAcc.FInAccNum");
        //--------------------------------------------------------------
        buf.append(" ) saving left join (select stock.FPortCode as FPortCode,stock.FCashAccCode as FCashAccCode,");
        buf.append("stock.FAnalysisCode1 as FAnalysisCode1,stock.FAnalysisCode2 as FAnalysisCode2,stock.FAnalysisCode3 as FAnalysisCode3,stock.FCuryCode as FCuryCode, ");
        buf.append("FBal,FBaseCuryBal,FPortCuryBal, FMoney,FBaseCuryMoney,FPortCuryMoney,FRelaType from (select FStorageDate,"); //增加编号类型
        buf.append("FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FCuryCode,FBal,FBaseCuryBal,FPortCuryBal from ");
        buf.append(pub.yssGetTableName("tb_stock_cashpayrec"));
        buf.append(" where FSubTsfTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest));
        buf.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode));
        buf.append(" and ").append(operSql.sqlStoragEve(dDate)).append(") stock "); //昨日库存余额
        buf.append(" left join (select FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,");
        buf.append("FCuryCode,FMoney,FBaseCuryMoney,FPortCuryMoney,FRelaType from ").append(pub.yssGetTableName("Tb_data_cashpayrec")); //获取当日发生额,增加编号类型
        buf.append(" where FSubTsfTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest));
        buf.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode));
        buf.append(" and FTransDate = ").append(dbl.sqlDate(dDate));
        buf.append(" ) cashpay on stock.FPortCode = cashpay.FPortCode and stock.FCashAccCode = cashpay.FCashAccCode");
        if (analys1) {
            buf.append(" and stock.FAnalysisCode1 = cashpay.FAnalysisCode1");
        }
        if (analys2) {
            buf.append(" and stock.FAnalysisCode2 = cashpay.FAnalysisCode2");
        }
        if (analys3) {
            buf.append(" and stock.FAnalysisCode3 = cashpay.FAnalysisCode3");
        }
        buf.append(" ) data on saving.FPortCode = data.FPortCode and saving.FOutCashAccCode = data.FCashAccCode "); //调整账户的获取的方式，获取流出账户的信息
        if (analys1) {
            buf.append(" and saving.FAnalysisCode1 = data.FAnalysisCode1");
        }
        if (analys2) {
            buf.append(" and saving.FAnalysisCode2 = data.FAnalysisCode2");
        }
        if (analys3) {
            buf.append(" and saving.FAnalysisCode3 = data.FAnalysisCode3");
        }
        return buf.toString();
    }

    /**
     * 拼装获取到期支付的数据
     * @param Fnum  
     * @param analys1 boolean
     * @param analys2 boolean
     * @param analys3 boolean
     * @return String
     * @throws YssException 

     */
    private String createMaturePaidSql(String Fnum,Date Startdate,String account,boolean analys1, boolean analys2, boolean analys3) throws YssException {
        
    	//edited by zhouxiang MS01455 新的定存计息转收入的方法 使用到期日期查询系统现存的没有关联编号的应收应付数据
		ResultSet subrs = null;
		ResultSet figureRs = null;
		String addSqlB = "";
		String addSqlA = "";
		String strSql = "";
		String strfigureType = "0";
		String figureSql = // 获取计息方式
		"select  pe.FDayInd from "
				+ pub.yssGetTableName("Tb_Para_Cashaccount")
				+ " ca1 left join (select fperiodcode,FDayInd"
				+ " from "
				+ pub.yssGetTableName("tb_para_period")
				+ " where FCheckState = 1) pe on ca1.fperiodcode = pe.fperiodcode"
				+ " join (select fcashacccode, max(fstartdate) as fstartdate from "
				+ pub.yssGetTableName("Tb_Para_Cashaccount")
				+ " where fstartdate <= "
				+ dbl.sqlDate(new java.util.Date())
				+ " group by fcashacccode) ca2 on ca1.fcashacccode = ca2.fcashacccode and ca1.fstartdate = ca2.fstartdate"
				//+ " where FCheckState = 1 and fcashacccode="
				+ " where ca1.FCheckState = 1 and ca1.fcashacccode="//给字段添加表别名，防止部分数据库报错 by leeyu 20100909
				+ dbl.sqlString(account);
		try {
			figureRs = dbl.queryByPreparedStatement(figureSql);
			if (figureRs.next()) {
				strfigureType = figureRs.getString("FDayInd");
				if (strfigureType == null) strfigureType = "0";//增加默认值，否则对于外部计息来源的到期处理会报错  hukun 2012.2.24
			}
			dbl.closeResultSetFinal(figureRs);//太平资产版本调整  2010.08.26 
			
			strSql = "select * from "//查询应收应付的第一笔记录是否含有关联编号
					+ pub.yssGetTableName("tb_data_cashpayrec")
					+ " where ftransdate ="
					+ (strfigureType.equalsIgnoreCase("0") ? dbl//使用计息方式对收益计息的日期进行控制‘0’计头查询
							.sqlDate(Startdate) : dbl.sqlDate(YssFun.addDay(
							Startdate, 1))) + " and fportcode ="
					+ dbl.sqlString(this.sPortCode) + " and fcashacccode ="
					+ dbl.sqlString(account) + " and fsubtsftypecode ="
					+ dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest)
					+ " and frelanum is null";
			subrs = dbl.queryByPreparedStatement(strSql);
			if (subrs.next()) {//没有编号则用没有编号的方法统计应收应付余额
				addSqlB = "select sum(m.fmoney) as fmoney  ,m.fcashacccode,m.FCuryCode from "
						+ pub.yssGetTableName("Tb_data_cashpayrec")
						+ " m  join (select * from "
						+ pub.yssGetTableName("tb_cash_savinginacc")
						+ " where fportcode = "
						+ dbl.sqlString(this.sPortCode)
						+ "  and Fcashacccode = "
						+ dbl.sqlString(account)
						+ "  and fmaturedate ="
						+ dbl.sqlDate(dDate)
						+ " ) n on m.fportcode =n.fportcode and m.Fcashacccode = n.Fcashacccode"
						+ " where ftransdate >="//没有关联编号的计息算法：计头则统计从第一天开始，最后一天不计.否则从交易的第二天开始到最后一天
						+ (strfigureType.equalsIgnoreCase("0") ? dbl
								.sqlDate(Startdate) : dbl.sqlDate(YssFun
								.addDay(Startdate, 1)))
						+ " and  ftransdate<="
						+ (strfigureType.equalsIgnoreCase("0") ? dbl
								.sqlDate(YssFun.addDay(this.dDate, -1)) : dbl
								.sqlDate(this.dDate))
						+ " and frelanum is null"
						+ " and FSubTsfTypeCode = "
						+ dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest)
						+ " and m.fcashacccode="
						+ dbl.sqlString(account)
						+ "and m.FCheckState = 1 "  //add by fangjiang 2012.03.03
						+ " group by m.fcashacccode,m.FCuryCode";

			} else {//否则有关联编号则使用关联编号索引应收应付统计库存
				addSqlB = "select sum(m.fmoney) as fmoney  ,m.fcashacccode,m.FCuryCode from "
						+ pub.yssGetTableName("Tb_data_cashpayrec")
						+ " m where ftransdate between "
						+ dbl.sqlDate(Startdate)
						+ " and "
						+ dbl.sqlDate(this.dDate)
						+ " and m.frelanum="
						+ dbl.sqlString(Fnum)
						+ " and (FSubTsfTypeCode = "
						+ dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest)
						//add by licai 20101115 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。 
						+ " or FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DC_RecInterest)
						+ ") and m.fcashacccode= "
						+ dbl.sqlString(account)
						//add by licai 20101115 BUG #191 定期存款业务做买入时，到期日缺少一笔买入利息的收入。==end 
						+ "and m.FCheckState = 1 "  //add by fangjiang 2012.03.03
						+ " group by m.fcashacccode,m.FCuryCode";
				addSqlA = " and t.frelanum =" + dbl.sqlString(Fnum);
			}
		} catch (Exception e) {
			throw new YssException("查询首期关联编号出现异常！", e);
		}
		
		dbl.closeResultSetFinal(subrs);//太平资产版本调整  2010.08.26 
		//----end------MS01455------------------------------------------------------------------------------------			
    	
    	StringBuffer buf = new StringBuffer();
        buf.append("select paid.*,rec.FRelaNum,rec.FRelaType , pc.* " +
        		/**shashijie 2012-7-25 STORY 2796 */
        		" ,a.Foutmoney, a.Ftaketype " +
				/**end*/
        		" FROM (");
        buf.append("select saving.*,FCuryCode,FBal,FBaseCuryBal,FPortCuryBal,FMoney,FBaseCuryMoney,FPortCuryMoney from (select * from ");
        buf.append(pub.yssGetTableName("Tb_cash_savinginacc"));
        buf.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(this.sPortCode));
        buf.append(" and FMatureDate = ").append(dbl.sqlDate(this.dDate)).append(" and fnum=").append(dbl.sqlString(Fnum));
        buf.append(" ) saving left join (select stock.FPortCode as FPortCode,stock.FCashAccCode as FCashAccCode,stock.fattrclscode,");//NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.17
      
      /*  buf.append("stock.FAnalysisCode1 as FAnalysisCode1,stock.FAnalysisCode2 as FAnalysisCode2,stock.FAnalysisCode3 as FAnalysisCode3,stock.FCuryCode, ");
        buf.append("FBal,FBaseCuryBal,FPortCuryBal, FMoney,FBaseCuryMoney,FPortCuryMoney from (select FStorageDate,");
        buf.append("FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FCuryCode,FBal,FBaseCuryBal,FPortCuryBal from ");
        buf.append(pub.yssGetTableName("tb_stock_cashpayrec"));
        buf.append(" where FSubTsfTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest));
        buf.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode));
        buf.append(" and ").append(operSql.sqlStoragEve(dDate)).append(") stock "); //昨日库存余额
*/        
        
        //end by zhouxiang MS01455 新的定存计息转收入的方法 使用到期日期查询系统现存的没有关联编号的应收应付数据
        buf.append("stock.FAnalysisCode1 as FAnalysisCode1,stock.FAnalysisCode2 as FAnalysisCode2,stock.FAnalysisCode3 as FAnalysisCode3,stock.FCuryCode, ");
        buf.append("FBal,FBaseCuryBal,FPortCuryBal, FMoney,FBaseCuryMoney,FPortCuryMoney from (");
        buf.append("select distinct Fportcode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,t.FCashAccCode, r.FCuryCode,r.fmoney as FBal,fattrclscode,");//NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.17
        buf.append("r.fmoney as   FBaseCuryBal,r.fmoney as   FPortCuryBal from ").append(pub.yssGetTableName("Tb_data_cashpayrec"));
        buf.append(" t join (").append(addSqlB);
        buf.append(" )r on t.Fcashacccode =r.Fcashacccode where ftransdate =").append(dbl.sqlDate(Startdate));
        buf.append(addSqlA);
        //----end------MS01455------------------------------------------------------------------------------------
        
        
        
        buf.append(") stock ");//直接使用关联编号从应收应付里面查找所有的应收应付数据统计成余额，所有不存在昨日余额+今日发生额的情况，将余额的fmoney用''代替
        buf.append(" left join (select FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,fattrclscode,");//NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.17
        buf.append("FCuryCode,'' as FMoney,FBaseCuryMoney,FPortCuryMoney from ").append(pub.yssGetTableName("Tb_data_cashpayrec")); //获取当日发生额
        buf.append(" where FSubTsfTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest));
        buf.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode));
        buf.append(" and FTransDate = ").append(dbl.sqlDate(dDate));
        buf.append(" ) cashpay on stock.FPortCode = cashpay.FPortCode and stock.FCashAccCode = cashpay.FCashAccCode and stock.fattrclscode = cashpay.fattrclscode ");//NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.17
        if (analys1) {
            buf.append(" and stock.FAnalysisCode1 = cashpay.FAnalysisCode1");
        }
        if (analys2) {
            buf.append(" and stock.FAnalysisCode2 = cashpay.FAnalysisCode2");
        }
        if (analys3) {
            buf.append(" and stock.FAnalysisCode3 = cashpay.FAnalysisCode3");
        }
        //edited by zhouxiang MS01455
        buf.append(" ) data on saving.FPortCode = data.FPortCode and saving.FInterestAccCode = data.FCashAccCode");
        //----end------MS01455------------------------------------------------------------------------------------
        if (analys1) {
            buf.append(" and saving.FAnalysisCode1 = data.FAnalysisCode1");
        }
        if (analys2) {
            buf.append(" and saving.FAnalysisCode2 = data.FAnalysisCode2");
        }
        if (analys3) {
            buf.append(" and saving.FAnalysisCode3 = data.FAnalysisCode3");
        }
        buf.append(" and saving.fattrclscode = data.fattrclscode ) paid ");//NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.17
        buf.append(" left join (select FRelaNum,FRelaType,FPortCode,FCashAccCode,fattrclscode,");//NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.17
        buf.append("FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from ").append(pub.yssGetTableName("Tb_data_cashpayrec"));
        buf.append(" where FSubTsfTypeCode = ").append(dbl.sqlString("02DE")); //获取支付数据
        buf.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode));
        buf.append(" and FTransDate = ").append(dbl.sqlDate(dDate));
        buf.append(") rec on paid.FPortCode = rec.FPortCode and paid.FInterestAccCode = rec.FCashAccCode  and rec.FRelaNum=paid.fnum  and paid.fattrclscode = rec.fattrclscode");//NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.17
        if (analys1) {
            buf.append(" and paid.FAnalysisCode1 = rec.FAnalysisCode1");
        }
        if (analys2) {
            buf.append(" and paid.FAnalysisCode2 = rec.FAnalysisCode2");
        }
        if (analys3) {
            buf.append(" and paid.FAnalysisCode3 = rec.FAnalysisCode3");
        }
        /**shashijie 2011.04.14 追加查询期间设置表,根据资金调拨的现金账户*/
        buf.append(" LEFT JOIN ( SELECT FCashAccCode,FPeriodCode FROM " + pub.yssGetTableName("Tb_Para_Cashaccount") + 
        		" WHERE FCheckState = 1 ) pc ON pc.FCashAccCode = paid.FCashAccCode ");
        /**end*/
        /**shashijie 2012-7-25 STORY 2796*/
		buf.append(" Left Join (Select A1.Fconsavingnum," +
				" Sum(A1.Foutmoney) Foutmoney," +
				" A1.Ftaketype" +
				" From "+pub.yssGetTableName("Tb_Cash_Consavingpriext")+" A1" +
				" Where A1.Fcheckstate = 1" +
				" And A1.Fextdate <= "+dbl.sqlDate(dDate)+
                " And A1.Fconsavingnum = "+dbl.sqlString(Fnum)+
                " Group By A1.Fconsavingnum, A1.Ftaketype) a On a.Fconsavingnum = Paid.Fnum ");
		/**end*/
        return buf.toString();
    }

    private SavingOutAccBean setSavingInterest(ResultSet rs, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        SavingOutAccBean interest = null;
        double dBaseRate = 0D;
        double dPortRate = 0D;
        CashAccountBean cashAcc = null;//账户设置
        try {
            interest = new SavingOutAccBean();
            interest.setOutAccDate(this.dDate);
            if (dbl.isFieldExist(rs, "FOutCashAccCode")) {
            	interest.setOutCashAccCode(rs.getString("FOutCashAccCode")); // add by wangzuochun 2010.06.08  MS01166    普通定存，做“转出”交易后，原账户利息库存没有冲减掉    QDV4国内（测试）2010年05月10日02_AB   
			}
            
            interest.setCashAccCode(rs.getString("FInterestAccCode")); //利息账户
            interest.setPortCode(rs.getString("FPortCode"));
            if (analy1) {
                interest.setInvMgrCode(rs.getString("FAnalysisCode1"));
            }
            if (analy2) {
                interest.setCatCode(rs.getString("FAnalysisCode2"));
            }
            interest.setStrAttrClsCode(rs.getString("fattrclscode"));
            interest.setRecIntrest(YssD.add(rs.getDouble("FMoney"), rs.getDouble("FBal"))); //昨日余额+今日发生
            
            
            //----未计提过利息的情况下，为避免获取不到货币代码，在此进行异常提示。sj -------------------------------------
            if(interest.getRecIntrest() == 0.0){
            	//throw new YssException("【" + rs.getString("FCashAccCode") + "】账户未计提定存利息，请进行利息计提。" );
            	//-----若为0，则无法获取定存币种，需通过账户来获取币种。---//
            	cashAcc = new CashAccountBean();
            	cashAcc.setYssPub(pub);
            	cashAcc.getSetting();
            	//------------------------------------------------------//
            }
            //--------------------------------------------------------------------------------------------------------
            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                null == rs.getString("FCuryCode")?cashAcc.getStrCurrencyCode():rs.getString("FCuryCode"), rs.getString("FPortCode"),//若从数据库中无法获取币种，则使用账户币种
                YssOperCons.YSS_RATE_BASE); //获取基础汇率
            interest.setBaseRate(dBaseRate);

            rateOper.getInnerPortRate(dDate, null == rs.getString("FCuryCode")?cashAcc.getStrCurrencyCode():rs.getString("FCuryCode"),//若从数据库中无法获取币种，则使用账户币种
                                      rs.getString("FPortCode"));
            dPortRate = rateOper.getDPortRate(); //获取组合汇率
            interest.setPortRate(dPortRate);
           
            interest.setNum(rs.getString("FNum")); //将定存编号放入

        } catch (Exception e) {
            throw new YssException("设置定存收益数据出现异常！ ", e);
        }
        return interest;
    }

    /**
     * 设置定存收益支付数据
     * @param interest SavingOutAccBean
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @throws YssException
     */
    private void excutePaid(SavingOutAccBean interest, boolean analy1, boolean analy2, boolean analy3,String savingType) throws YssException {
        String savingNum = null;
        AccPaid accpaid = new AccPaid();
        ArrayList alPaid = new ArrayList();
        PaidAccIncome paidAcc = new PaidAccIncome();
        CashAccountBean cashAcc = new CashAccountBean();
        try {
            cashAcc.setYssPub(pub);
            cashAcc.setStrCashAcctCode(interest.getCashAccCode());
            cashAcc.getSetting();

            paidAcc.setYssPub(pub);
            accpaid.setDDate(interest.getOutAccDate());
            accpaid.setCashAccCode(interest.getCashAccCode());
            accpaid.setOutCashAccCode(interest.getOutCashAccCode());  // add by wangzuochun 2010.06.08  MS01166    普通定存，做“转出”交易后，原账户利息库存没有冲减掉    QDV4国内（测试）2010年05月10日02_AB   
            accpaid.setPortCode(interest.getPortCode());
            if (analy1) {
                accpaid.setInvmgrCode(interest.getInvMgrCode());
            }
            if (analy2) {
                accpaid.setCatCode(interest.getCatCode());
            }

            accpaid.setCuryCode(cashAcc.getStrCurrencyCode());
            accpaid.setLx(interest.getRecIntrest()); //此处为现金应收应付的数据
            accpaid.setMoney(interest.getRecIntrest()); //此处为资金调拨的数据
            /**shashijie 2011.05.11 STORY #815 &#34;固定利率&#34;类型的定存需要改成同 “固定收益”模式一样*/
            if (isOverDayProfession() && //如果使用新业务流程计算定存,则普通定存4与协定定存2到期日不需产生资金调拨
            		("2".equals(savingType) || "4".equals(savingType)) ) {
            	accpaid.setMoney(0); //此处为资金调拨的数据
			}
            /**end*/
            accpaid.setTsfTypeCode("02");
            accpaid.setSubTsfTypeCode("02DE");
            accpaid.setBaseCuryRate(interest.getBaseRate());
            accpaid.setPortCuryRate(interest.getPortRate());
            accpaid.setNumType("autoSaveInterest"); //自动产生支付数据的标识
            accpaid.setRelaNum(interest.getNum());
            accpaid.setBaseMoney(this.getSettingOper().calBaseMoney(accpaid.
                getMoney(), accpaid.getBaseCuryRate()));
            accpaid.setPortMoney(this.getSettingOper().calPortMoney(accpaid.
                getMoney(), accpaid.getBaseCuryRate(), accpaid.getPortCuryRate(),

                accpaid.getCuryCode(), accpaid.getDDate(), accpaid.getPortCode()));
            accpaid.setAttrClsCode(interest.getStrAttrClsCode());//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2011.01.17
            alPaid.add(accpaid);
            savingNum = interest.getNum(); //获取定存编号
            paidAcc.isCheckData = "true"; //在此处设置产生的数据是否为已审核状态
            
          //add by huangqirong 2012-06-14 bug #4679 中金为了删除历史数据重做重新产生对应应收应付
            if("003".equalsIgnoreCase(pub.getPrefixTB()) && "4".equalsIgnoreCase(savingType)){
	           	 if("003".equalsIgnoreCase(this.getAssetCode(pub.getPrefixTB(), this.sPortCode))){ //判断资产代码           		 
	           			 String sourceCashAcc = this.getDeferralAccCount(pub.getPrefixTB(), this.sPortCode, interest.getCashAccCode()); //判断是否为定存账户
	           			 this.deleteCashPayRec(pub.getPrefixTB() , this.sPortCode , "02" , "02DE" , YssFun.formatDate(dDate, "yyyy-MM-dd") , sourceCashAcc);
				}
			}/*else if("002".equalsIgnoreCase(pub.getPrefixTB()) && "4".equalsIgnoreCase(savingType)){
	           	 if("002".equalsIgnoreCase(this.getAssetCode(pub.getPrefixTB(), this.sPortCode))){ //判断资产代码
	           			 String sourceCashAcc = this.getDeferralAccCount(pub.getPrefixTB(), this.sPortCode, interest.getCashAccCode()); //判断是否为定存账户
	           			 this.deleteCashPayRec(pub.getPrefixTB() , this.sPortCode , "02" , "02DE" , YssFun.formatDate(dDate, "yyyy-MM-dd") , sourceCashAcc);
	           		
	           	 }
            }*/
            //---end---
            
            paidAcc.saveIncome(alPaid, savingNum, true); //此处的true说明到期处理为自动处理方式
        } catch (Exception e) {
            throw new YssException("设置定存收益支付数据出现异常！", e);
        }
    }

    /**
     *
     * @param sql String
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @throws YssException
     */
    private void createPaidOperation(String sql, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        ResultSet rs = null;
        SavingOutAccBean interest = null;
        try {
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
                if ("handSaveInterest".equalsIgnoreCase(rs.getString("FRelaType"))) { //若此支付数据为手工产生，则不进行处理
                    continue;
                }
                // add by lidaolong ; #526 QDV4长信基金2011年1月14日01_A
                if ("4".equals(rs.getString("FSavingType"))){
                    double foutmoney = 0D;
                    ResultSet rs1 = null;
                    String sql1 = " select sum(foutmoney) as foutmoney from " + pub.yssGetTableName("TB_Cash_Consavingpriext") 
                    				+ " where fcheckstate = 1 and Fconsavingnum = " + dbl.sqlString(rs.getString("fnum"))
                    				/**shashijie 2012-7-25 STORY 2796 增加条件,判断为本金提出时才取出流出金额*/
									+" And FTakeType = '0' "
									/**end*/
                    				+ " and fextdate <= " + dbl.sqlDate(dDate);
                    try {
                    	rs1 = dbl.queryByPreparedStatement(sql1);
                        while (rs1.next()) {
                        	foutmoney = rs1.getDouble("foutmoney");
                        }
                    } catch(Exception e) {
                    	throw new YssException("获取协议定存本金提取出错！", e);
                    } finally {
                    	 dbl.closeResultSetFinal(rs1);
                    }
                    //--------------
                   
                  //当普通定存的本金已被提前支取，则不需要进行如此处理，因为它已经被“提前到期处理”了 
                    if ( 0 < foutmoney){
                    	continue;
                    }
                }
                //---end by lidaolong
                interest = setSavingInterest(rs, analy1, analy2, analy3);
                /**shashijie 2012-7-25 STORY 2796 减去提前支取利息的金额*/
				if ("1".equals(rs.getString("Ftaketype"))) {
					double money = YssD.sub(interest.getRecIntrest(),rs.getDouble("Foutmoney"));
					interest.setRecIntrest(money);
				}
				/**end*/
                /**shashijie 2011.05.12 STORY #815 这里多传入一个参数,存款类型*/
                excutePaid(interest, analy1, analy2, analy3,rs.getString("FSavingType"));
                /**end*/
            }
        } catch (Exception e) {
            throw new YssException("生成定存收益支付数据出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    
    /**
     * add by wangzuochun 2010.08.25  MS01606    定存业务处理后，不能删除历史资金调拨数据    QDV4赢时胜(测试)2010年08月12日07_B    
     * 删除调拨类型01，调拨子类型0003；调拨类型02，调拨子类型02DE的历史资金调拨数据    
     * @param outAcc
     * @throws YssException
     */
    public void delOldCashTransfer(CashTransAdmin cashTransAdmin) throws YssException {
    	
    	ResultSet rs = null;
    	ResultSet rsTemp = null;
        String strTransNum = ""; //资金调拨编号
        String strSavingNum = ""; 
        String strSql = "";
        TransferBean transfer = null;
        
		try {
			// 把要删除的资金调拨的定存编号拼接起来
			for (int i = 0; i < cashTransAdmin.getAddList().size(); i++ ){
				transfer = (TransferBean) cashTransAdmin.getAddList().get(i);
				if ("01".equals(transfer.getStrTsfTypeCode()) && 
							"0003".equals(transfer.getStrSubTsfTypeCode())){
					
					strSavingNum +=  transfer.getSavingNum() + ",";
				}
			}
			
			if (strSavingNum.length() > 1) {
				strSavingNum = strSavingNum
						.substring(0, strSavingNum.length() - 1);
				strSavingNum = operSql.sqlCodes(strSavingNum);
				
				//查询当天调拨类型01，调拨子类型0003的历史资金调拨数据
				strSql = " Select * from "
						+ pub.yssGetTableName("Tb_cash_transfer")
						+ " Where FTsfTypeCode = '01' and FSubTsfTypeCode = '0003' and FNumType = 'Saving' "
						+ " and FSavingNum in (" + strSavingNum + ") "
						+ " and FTransferDate = "
						+ dbl.sqlDate(dDate);
						
				rs = dbl.queryByPreparedStatement(strSql);
				
				// 把要删除的资金调拨编号拼接起来
				while (rs.next()) {
					strTransNum += rs.getString("FNum") + ",";
				}
				dbl.closeResultSetFinal(rs);
			}
			
			
			
			if (strTransNum.length() > 1) {
				strTransNum = strTransNum
						.substring(0, strTransNum.length() - 1);
				strTransNum = operSql.sqlCodes(strTransNum);
				if (strTransNum.trim().length() > 0) {
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_Transfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);

					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_SubTransfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);
				}
			}
			
			strTransNum = "";
			
			strSql = " Select * from "
					+ pub.yssGetTableName("Tb_Cash_Transfer")
					+ " Where FTsfTypeCode = '02' and FSubTsfTypeCode = '02DE' and FNumType = 'CashPay' "
					+ " and FRelaNum like 'SRP%' and FTransferDate = "
					+ dbl.sqlDate(dDate);

			rsTemp = dbl.queryByPreparedStatement(strSql);
			// 把要删除的资金调拨编号拼接起来
			while (rsTemp.next()) {
				strTransNum += rsTemp.getString("FNum") + ",";
			}
			dbl.closeResultSetFinal(rsTemp);

			if (strTransNum.length() > 1) {
				strTransNum = strTransNum
						.substring(0, strTransNum.length() - 1);
				strTransNum = operSql.sqlCodes(strTransNum);
				if (strTransNum.trim().length() > 0) {
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_Transfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);

					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_SubTransfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);
				}
			}
			
		} catch (Exception e) {
			throw new YssException("删除历史的资金调拨出错" + "\r\n" + e.getMessage(),e);
		}
		finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsTemp);
		}
	}
    
    //add by fangjiang 2010.11.29 STORY #97 协议存款业务需支持提前提取本金的功能    
    private String buildPrincipalExtSql() { 
    	String principalExtSql = " select " +
    							/**shashijie 2011.05.11 STORY #815 &#34;固定利率&#34;类型的定存需要改成同 “固定收益”模式一样*/
    							" DISTINCT a.*,cash.*,saving.*,stock.*,z.FCashaccCode as FInCashAccCode ,x.FPeriodCode " +
    							/**end*/
    							" from ( " + " select * from " + pub.yssGetTableName("TB_Cash_Consavingpriext")
    							+ " where fcheckstate = 1 and FExtDate = " + dbl.sqlDate(dDate)
    							+ " ) a LEFT JOIN ( SELECT FCashAccCode, FCuryCode "
    							+ " FROM " + pub.yssGetTableName("TB_Para_Cashaccount") 
    							+ " where FCheckState = 1 ) cash on cash.FCashAccCode = a.FInCashAccountCode "
    							// add by lidaolong QDV4长信基金2011年1月14日01_A
    							+" left join "+pub.yssGetTableName("Tb_Cash_SavingInAcc") +" saving on saving.fnum = a.fconsavingnum"
    							+ " left join (select FPortCode,FBal,fcashacccode from "+pub.yssGetTableName("tb_stock_cashpayrec")
    											+" where FSubTsfTypeCode = "+dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest)    										
    											+" and FPortCode = "+dbl.sqlString(this.sPortCode)
    											+" and "+operSql.sqlStoragEve(dDate)
    											+") stock on "	+ "stock.fcashacccode= saving.FInterestAccCode" +
    							//---end---
    							/**shashijie 2011.05.11 STORY #815 &#34;固定利率&#34;类型的定存需要改成同 “固定收益”模式一样*/
    							" LEFT JOIN "+pub.yssGetTableName("Tb_Cash_SavingOutAcc")+" z on z.FInAccNum = saving.fnum "+
    							" LEFT JOIN "+pub.yssGetTableName("TB_Para_Cashaccount")+" x on saving.FCashaccCode = x.FCashaccCode ";
    							/**end*/
    	return principalExtSql;
    } 
    
    private TransferBean setTransfer1(ResultSet rs) throws YssException {
        TransferBean transfer = null;
        try {
            transfer = new TransferBean();
            transfer.setDtTransDate(rs.getDate("FExtDate")); //业务日期
            transfer.setDtTransferDate(rs.getDate("FExtDate")); //调拨日期
            transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
            transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_Principal_Ext);
            transfer.setFNumType("principalExt"); //
            transfer.setFRelaNum(rs.getString("FNum")); //关联编号
            transfer.setSavingNum(rs.getString("FConSavingNum")); //协议定存编号
            transfer.checkStateId = 1;
            transfer.setDataSource(1);
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
        return transfer; //返回资金调拨数据
    }
    
    private TransferSetBean setTransferSet(ResultSet rs, boolean analy1, boolean analy2, boolean analy3, boolean inOut) throws YssException {
        TransferSetBean transferSet = null;
        try {
            transferSet = new TransferSetBean();
            double dBaseRate = 1;
            double dPortRate = 1;

            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("FCuryCode"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE); //获取基础汇率

            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                      rs.getString("FPortCode"));
            dPortRate = rateOper.getDPortRate(); //获取组合汇率

            transferSet.setSPortCode(rs.getString("FPortCode"));

            //---设置分析代码 ---------------------------------------------------------
            transferSet.setSAnalysisCode1(analy1 ? rs.getString("FAnalysisCode1") : " ");
            transferSet.setSAnalysisCode2(analy2 ? rs.getString("FAnalysisCode2") : " ");
            transferSet.setSAnalysisCode3(analy3 ? rs.getString("FAnalysisCode3") : " ");
            //-----------------------------------------------------------------------
            
            if(inOut) {
            	transferSet.setSCashAccCode(rs.getString("FInCashAccountCode"));
            } else {
            	transferSet.setSCashAccCode(rs.getString("FOutCashAccountCode"));
            }        

            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }

        return transferSet;
    }
    
    private TransferSetBean setPrincipalExtTransferSet(ResultSet rs, boolean analy1, boolean analy2, boolean analy3, boolean inOut) throws YssException {
        TransferSetBean transferset = null;
        try {
            transferset = setTransferSet(rs, analy1, analy2, analy3, inOut);
            //---
            if (inOut) { //true时，说明为流入帐号
                transferset.setDMoney(rs.getDouble("FOutMoney"));
                transferset.setIInOut(1);
            } else { //流出帐号
                transferset.setDMoney(rs.getDouble("FOutMoney"));
                transferset.setIInOut(-1);
            }
            //---
        } catch (Exception e) {
            throw new YssException("设置定存首期的资金调拨数据出现异常！", e);
        }
        return transferset;
    }
    
    /**	shashijie,2011-4-25加上注释	本金提取业务处理 */
    private void createPrincipalExtCashTransfer(CashTransAdmin cashtransAdmin,String sql, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        ResultSet rs = null;
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = new ArrayList(); 
        try {
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
            	
            	subTransfer = new ArrayList();
            	
                transfer = setTransfer1(rs); //获取资金调拨数据
                
                //-----edit by lidaolong #526 QDV4长信基金2011年1月14日01_A----
                //判断是不是普通定存业务，如果是普通定存业务则产生二笔资金调拨。
                //一笔为金额为全部本金和该笔定存应收利息库存的总和，另一笔为金额为昨日应收利息库存中金额
                if (rs.getString("FSavingType").equals("4")){//普通定存的处理
                	 
                	/**shashijie 2012-7-19 STORY 2796 利息提前支取*/
					if (rs.getString("FTakeType").equals("1")) {
						//产生资金调拨
						doTransFerSet(rs,cashtransAdmin,analy1, analy2, analy3);
						
						continue ;
					}
                	/**end*/
                	 
                	//1.金额为全部本金和该笔定存应收利息库存的总和
                	transferSet = setPrincipalAndInterest(rs, analy1, analy2, analy3, 1);
                	subTransfer.add(transferSet);
                	 
                	transferSet = setPrincipalAndInterest(rs, analy1, analy2, analy3, 1);
                	transferSet.setSCashAccCode(rs.getString("FInCashAccountCode"));
                	transferSet.setIInOut(1);
                	subTransfer.add(transferSet);
                	 
                	transfer.setSubTrans(subTransfer); //将子数据放入资金调拨中
                    cashtransAdmin.addList(transfer);
                	//2.金额为昨日应收利息库存中金额
                    TransferBean transfer2 = setTransfer1(rs);
                    transfer2.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);//调拨类型为02收入
                    transfer2.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_DEPOSIT_INTEREST);//调拨子类型为02DE存款利息
                     
                    ArrayList arryList = new ArrayList();
                    transferSet = setPrincipalAndInterest(rs, analy1, analy2, analy3, 2);
                    arryList.add(transferSet); //将资金调拨子数据放入容器
                     
                    transfer2.setSubTrans(arryList); //将子数据放入资金调拨中
                    cashtransAdmin.addList(transfer2);
                     
                     
                }else {//协议存款业务处理
                	transferSet = setPrincipalExtTransferSet(rs, analy1, analy2, analy3, true); //获取流入资金调拨子数据
                    subTransfer.add(transferSet); //将资金调拨子数据放入容器
                     
                    transferSet = setPrincipalExtTransferSet(rs, analy1, analy2, analy3, false); //获取流出资金调拨子数据
                    subTransfer.add(transferSet); //将资金调拨子数据放入容器
               
                    transfer.setSubTrans(subTransfer); //将子数据放入资金调拨中
                    cashtransAdmin.addList(transfer);
                }
                //---end ----
                
                //cashtransAdmin.delete(sSavingNum, sCprNum);
                
            }
        } catch (Exception e) {
            throw new YssException("生成协议定存本金提取的资金调拨出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
	//-----------------------------
    //add by lidaolong #526 QDV4长信基金2011年1月14日01_A----
    private TransferSetBean setPrincipalAndInterest(ResultSet rs, boolean analy1, boolean analy2, boolean analy3, int num) throws YssException {
        TransferSetBean transferset = null;
        try {
        	if (num == 1){//金额为全部本金和该笔定存应收利息库存的总和
        		transferset = setTransferSet(rs, analy1, analy2, analy3, false);        
                transferset.setDMoney(YssD.add(rs.getDouble("FOutMoney"),rs.getDouble("FBal")));
                transferset.setIInOut(-1);
        	}else if (num == 2){//金额为昨日应收利息库存中金额
        		transferset = setTransferSet(rs, analy1, analy2, analy3, false);        
                transferset.setDMoney(rs.getDouble("FBal"));
                transferset.setIInOut(1);
        		
        	}                 
        } catch (Exception e) {
            throw new YssException("设置定存的资金调拨数据出现异常！", e);
        }
        return transferset;
    }
 //----end---
    

	/**shashijie 2012-7-19 STORY 2796  */
	private void doTransFerSet(ResultSet rs, CashTransAdmin cashtransAdmin,
			boolean analy1, boolean analy2, boolean analy3) throws Exception {
		TransferBean transfer = null;//资金调拨
        TransferSetBean transferSet = null;//资金调拨子表
        ArrayList subTransfer = new ArrayList(); //实例化放置资金调拨子数据的容器
		
		transfer = setTransfer1(rs);//获取资金调拨数据
		transfer.setFNumType("TakeInterest");//编号类型
		
		//第一笔资金调拨
		//流入活期账户
		transferSet = setPrincipalAndInterest(rs, analy1, analy2, analy3, 2);
		transferSet.setDMoney(rs.getDouble("FOutMoney"));
    	transferSet.setSCashAccCode(rs.getString("FInCashAccountCode"));//流入账户
    	subTransfer.add(transferSet);
		//流出定存账户,利息金额一致
    	TransferSetBean transferSet2 = setPrincipalAndInterest(rs, analy1, analy2, analy3, 1);
    	transferSet2.setDMoney(rs.getDouble("FOutMoney"));
    	transferSet2.setSCashAccCode(rs.getString("FOutCashAccountCode"));//流出账户
    	subTransfer.add(transferSet2);
    	
    	transfer.setSubTrans(subTransfer);//将子数据放入资金调拨中
        cashtransAdmin.addList(transfer);
        
        //第二笔资金调拨
        //流入定存账户
        TransferBean transfer3 = setTransfer1(rs);
        transfer3.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);//调拨类型为02收入
		transfer3.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_DEPOSIT_INTEREST);//调拨子类型为02DE存款利息
        transfer3.setFNumType("TakeInterest");//编号类型
        TransferSetBean transferSet3 = setPrincipalAndInterest(rs, analy1, analy2, analy3, 2);
        transferSet3.setDMoney(rs.getDouble("FOutMoney"));//金额
        transferSet3.setSCashAccCode(rs.getString("FOutCashAccountCode"));//流出账户
        ArrayList subTransfer3 = new ArrayList();
        subTransfer3.add(transferSet3);
        transfer3.setSubTrans(subTransfer3);
        cashtransAdmin.addList(transfer3);
	}
	
    /**shashijie 2012-7-19 STORY 2796 提前支取利息产生现金应收应付 */
	private void doCashPayRecAdmin() throws YssException {
		CashPayRecAdmin cashpayrecadmin = new CashPayRecAdmin(); //生成现金应收应付控制类
		cashpayrecadmin.setYssPub(pub);
        //获取数据
        getCashPayRecAdmin(cashpayrecadmin);             

        boolean bTrans = false;
   	 	Connection conn = dbl.loadConnection();
        try {
        	conn.setAutoCommit(false);
            bTrans = true;
            //锁表
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_CashPayRec")); 
            
            cashpayrecadmin.delete("", dDate, dDate, YssOperCons.YSS_ZJDBLX_Income,
            		YssOperCons.YSS_ZJDBZLX_DEPOSIT_INTEREST, "", "","", "", "","", -1, 0, "","TakeInterest", "");
                     
            cashpayrecadmin.insert(dDate, YssOperCons.YSS_ZJDBLX_Income, YssOperCons.YSS_ZJDBZLX_DEPOSIT_INTEREST, 
            		"", 1, false, "", "TakeInterest", false);
            
        	conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
   	 	}catch (Exception ex) {
            throw new YssException("生成现金应收应付出现异常！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        
	}

	/**shashijie 2012-7-19 STORY 2796 获取数据 */
	private void getCashPayRecAdmin(CashPayRecAdmin cashpayrecadmin) throws YssException {
		ResultSet rs = null;
		CashPecPayBean cashpecpay = null;
		try {
			String query = getTakeInterestQuery();
			rs = dbl.queryByPreparedStatement(query);
			while (rs.next()) {
				cashpecpay = setCashPecPay(rs.getDate("FExtDate"), YssOperCons.YSS_ZJDBLX_Income, 
						YssOperCons.YSS_ZJDBZLX_DEPOSIT_INTEREST, rs.getString("FAttrClsCode"), 
						rs.getString("FConSavingNum"), "TakeInterest", rs.getDouble("FOUTMONEY"), rs.getString("FPortCode"), 
						rs.getString("FOutCashAccountCode"), rs.getString("FCuryCode"), rs.getString("FAnalySiscode1"));
				cashpayrecadmin.addList(cashpecpay);
			}
		} catch (Exception e) {
			throw new YssException("生成定存的资金调拨出现异常！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**shashijie 2012-7-19 STORY 2796 */
	private String getTakeInterestQuery() {
		String query = " Select A1.*, B1.Fattrclscode,b1.FAnalySiscode1, C1.Fcurycode" +
			" From "+pub.yssGetTableName("Tb_Cash_Consavingpriext")+" A1" +
			" Join "+pub.yssGetTableName("Tb_Cash_Savinginacc")+" B1 On A1.Fconsavingnum = B1.Fnum" +
			" Left Join (Select Fcashacccode, Fcurycode" +
			" From "+pub.yssGetTableName("Tb_Para_Cashaccount")+
            " Where Fcheckstate = 1) C1 On C1.Fcashacccode = B1.Fcashacccode" +
            " Where a1.Fcheckstate = 1" +
            " And a1.Fextdate = "+dbl.sqlDate(this.dDate)+
            " And a1.Fportcode = "+dbl.sqlString(this.sPortCode)+
            " And a1.FTakeType = 1 ";
		return query;
	}

	/**shashijie 2012-7-19 STORY 2796 */
	private CashPecPayBean setCashPecPay(Date FextDate ,String TsfTypeCode,String SubTsfTypeCode,
			String Fattrclscode,String FNum,String RelaNumType,double Money,
			String FPortCode,String CashAccCode,String FCuryCode,
			String FAnalySiscode1) throws YssException{
    	CashPecPayBean cashpecpay = null;
    	try{
    		cashpecpay = new CashPecPayBean();
    		cashpecpay.setTradeDate(FextDate);//业务日期
    		cashpecpay.setTsfTypeCode(TsfTypeCode);//业务类型
    		cashpecpay.setSubTsfTypeCode(SubTsfTypeCode);//业务子类型
    		cashpecpay.setStrAttrClsCode(Fattrclscode);//所属分类
    		cashpecpay.setRelaNum(FNum);//关联编号
    		cashpecpay.setRelaNumType(RelaNumType);//关联编号类型    		
			cashpecpay.setMoney(Money);//金额
			
    		cashpecpay.setDataSource(1);//来源标志
    		cashpecpay.checkStateId = 1;
    		cashpecpay.setPortCode(FPortCode);//组合代码
    		cashpecpay.setNum(FNum);//编号
    		cashpecpay.setCashAccCode(CashAccCode);//现金账户
    		cashpecpay.setCuryCode(FCuryCode);//币种代码
    		cashpecpay.setInOutType(1);//方向
    		cashpecpay.setInvestManagerCode(FAnalySiscode1);//投资经理代码
    		double dBaseRate = this.getSettingOper().getCuryRate(
    				dDate,
    				FCuryCode,
    				FPortCode,
                    YssOperCons.YSS_RATE_BASE); //获取基础汇率
            double dPortRate = this.getSettingOper().getCuryRate(//组合汇率
					this.dDate, 
					"", 
					this.sPortCode, 
					YssOperCons.YSS_RATE_PORT);//获取组合汇率
    		cashpecpay.setBaseCuryRate(dBaseRate);//基础汇率
    		cashpecpay.setPortCuryRate(dPortRate);//组合汇率
    		double bacecurymoney = this.getSettingOper().calBaseMoney(cashpecpay.getMoney(),dBaseRate);
    		double portcurymoney = this.getSettingOper().calPortMoney(cashpecpay.getMoney(),dBaseRate,
    				dPortRate,FCuryCode,dDate,sPortCode);
    		cashpecpay.setBaseCuryMoney(bacecurymoney);
    		cashpecpay.setPortCuryMoney(portcurymoney);
    		
    	}catch (Exception e) {
            throw new YssException("设置现金应收应付数据出现异常！", e);
        }
        return cashpecpay;
    }
	
	
	
}
