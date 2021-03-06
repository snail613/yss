package com.yss.main.operdeal;
//xuqiji 20090805 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015    国内权益处理
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//---------------end---------------------------//
import java.util.Date;

import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.main.settlement.*;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;

public class BaseTradeSettlement
    extends BaseBean implements ICashTransfer {

    private java.util.Date startDate = new java.util.Date();
    private java.util.Date endDate = new java.util.Date();
    private String portCode = "";
    private String nums = "";
    private boolean compTemp;
    private String params = ""; // 处理在交易子表中保存 实际结算帐户等参数 add by liyu 1128   
	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A start---//
	public SingleLogOper logOper;
	public String logSumCode = "";//汇总日志编号
	public boolean comeFromDD = false;//是否由调度方案调用
	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A end---//
    public BaseTradeSettlement() {
    }

    /**
     * init
     *
     * @param param BaseBean
     */
    public void init(BaseBean param) {
        TradeSettleBean tsb = (TradeSettleBean) param;
        nums = tsb.getNums();
        compTemp = tsb.isCompTemp();
        params = tsb.getParams();
    }

    /**
     * cashInTarget
     *
     * @return String
     */
    public String cashInTarget() throws YssException {

    	createCashTransfer();
        return "";
    }

    /**
     * cashOutTarget
     *
     * @return String
     */
    public String cashOutTarget() throws YssException {
        unCreateCashTransfer();
        return "";
    }

    public void createCashTransfer() throws
        YssException {
    	//添加锁，将此部分锁起来，原因是多组合并发交易结算时防止生成重复资金调拨编号 
    	//add by wangzuochun 2011.02.12 BUG #1047 多组合并发交易结算，产生资金调拨数据时，系统提示违反唯一约束。
    	synchronized(YssGlobal.objCashTsfLock){
    	

	        String strSql = "";
	        String strSqlSub = "";
	        String tmpSql = "";
	        String tmpSql1 = "";
	        boolean tmp1 = true;
	        ResultSet rs = null;
	        ResultSet rsTmp = null;
	        ResultSet rsTmp1 = null;
	        PreparedStatement pst = null; //资金调拨主表
	        PreparedStatement pstSub = null; //资金调拨子表
	        String strError = "产生资金调拨数据出错";
	        String sTransferType = "", sTradeType = ""; //调拨类型,交易类型
	        double dBaseCuryRate = 0, dPortCuryRate = 0; //基础汇率，组合汇率
	        int inout = 1; //1划入，-1划出
	        String sFNum = ""; //调拨编号
	        String sFTmpNum = ""; //存储已删除的主表记录的FNum，便于删除子表中相同FNum的记录
	        Connection conn = dbl.loadConnection();
	        boolean bTrans = false; //代表是否开始了事务
	        String sDesc = "";
	        String sTmpCusCat = "";
	
	        boolean analy1;
	        boolean analy2;
	        
	        //------ add by wangzuochun 2011.02.23 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。 
	        int allRecord = 0; //总的结算数；
	        int allCount = 0; //循环次数，1000条循环一次
	        String[] strArrNums = null;
	        StringBuffer buf = new StringBuffer();
	        //---------------BUG #1003--------------//
	        //add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A
	        String tradeNum = "";//交易编号
	        try {
	        	boolean bTPVer =false;//区分是太平资产版本还是QDII的版本,合并版本时调整
	        	CtlPubPara pubPara = null; //区分太平资产与QD参数，合并版本时调整 by leeyu
	        	pubPara =new CtlPubPara();
	        	pubPara.setYssPub(pub);
	        	String sPara =pubPara.getNavType();//通过净值表类型来判断
	        	if(sPara!=null && sPara.trim().equalsIgnoreCase("new")){
	        		bTPVer=false;//国内QDII模式
	        	}else{
	        		bTPVer=true;//太平资产模式
	        	}
	            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
	            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
	
	            BaseOperDeal baseOperBean = (BaseOperDeal) pub.getOperDealCtx().
	                getBean("baseoper");
	            baseOperBean.setYssPub(pub);

	            conn.setAutoCommit(false);
	            bTrans = true;
	            
	            //向调拨表插入数据
	            strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
	                " (FNum,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode,FTransferDate,FTransferTime,FTransDate,FTradeNum," +
	                "FSecurityCode,FDataSource,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" +
	                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	            pst = conn.prepareStatement(strSql);
	
	            //向调拨子表插入数据
	            strSqlSub = "insert into " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
	               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//  
	                " (FNum,FSubNum,FInOut,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAttrClsCode,FCashAccCode,FMoney," +
	                "FBaseCuryRate,FPortCuryRate,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDesc)" +
	                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	              //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//  
	            pstSub = conn.prepareStatement(strSqlSub);
	
	            if (params != null && params.length() > 0) {
	                //更新交易数据结算标志
	                TradeSubBean tradeSub = new TradeSubBean();
	                tradeSub.setYssPub(pub);
	                tradeSub.parseRowStr(params);
					// ----- MS00799 QDV4中保2009年11月4日01_B 2009.11.13 蒋世超添加 修正单笔结算反审核交易数据的bug. ------------------
					//tradeSub.editSetting(true);//合并太平版本代码
	                tradeSub.updateSetInfo(); //--- MS01740 QDV4赢时胜深圳2010年9月14日01_B 未进行交易结算便估值和延迟结算产生的问题 add by jiangshichao
					// ------------------- MS00799 QDV4中保2009年11月4日01_B end -----------------------------------------------------
	            }
	            
	            /**Start 20131024 modified by liubo.Bug #81990. QDV4招商基金2013年10月24日01_B
	             * 在BUG 80715的基础上修改一下：一次执行多条交易数据的结算时，才进行对实际结算日期的纠正。
	             * 因为单笔结算会有手动输入的实际结算日期的选项，这种情况下再用结算日期来作为实际结算日期就有问题*/
	            else
	            {
	            	// //add by maxin BUG #86549 QDV4赢时胜(上海)2013年12月27日01_B（场内交易业务界面大批量反审核数据报错，提示“列表中的最大表达式数为1000”） 
	            	String num1="";
	            	String num2="";
	            	String[] numss =nums.split(",");
	            	for (int i = 0; i < numss.length; i++) {
						 
						 if(i>999){
							 num2+=numss[i]+",";
						 }else{
							 num1+=numss[i]+",";
						 }
					}
	            	/**Start 20131009 added by liubo.Bug #80715.QDV4赢时胜(上海)2013年10月08日01_B
	            	 * 提出人指出4198需求的原因，批量延迟结算交易数据，然后结算这些交易数据。
	            	 * 这些交易数据被再次被反结算后重新结算，实际结算日期不会跟着结算日期变化，而是保留的延迟结算时手动输入的实际结算日期。
	            	 * 提出人要求结算时实际结算日期跟着结算日期关联*/
	            	strSql = "update " + pub.yssGetTableName("tb_Data_subTrade") +
	                " set FFactSettleDate = FSettleDate " +
	                " where FNum in (" + operSql.sqlCodes(num1) + ") or FNum in (" +  operSql.sqlCodes(num2) + ")";
	            	
	            	dbl.executeSql(strSql);
	            	/**End 20131009 added by liubo.Bug #80715.QDV4赢时胜(上海)2013年10月08日01_B*/
	            }
	            //------ add by wangzuochun 2011.02.23 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。 
	            if (this.nums != null && this.nums.length() > 0){
	            	strArrNums = this.nums.split(",");
	            	allRecord = strArrNums.length; //得到总的结算数
	            	allCount = (allRecord % 1000 == 0 ? allRecord / 1000 : allRecord / 1000 + 1);//得到循环次数
	            	
	            }
	            /**End 20131024 modified by liubo.Bug #81990. QDV4招商基金2013年10月24日01_B*/
	            
	            //------------------------------- BUG #1003 -------------------------------------// 
	
	            
	            //------ modify by wangzuochun 2011.02.23 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。 
	            for (int i = 0; i < allCount; i++){
	            
		            //取交易记录
		            strSql =
		                "select a.*,b.FTradeCury as sTradeCuryCode,b.FSecurityName,c.FPortCury as sPortCuryCode,b.FCusCatCode," +
		                //edit by songjie 2011.06.15 报未明确到列 FCatCode, FSubCatCode 改为 b.FCatCode, b.FSubCatCode
						"b.FCatCode, b.FSubCatCode, e.FTradeTypeName," +
		                //-------------------为了使描述信息更清晰。sj modified 20081224 -----//
		                "o.FInvMgrName as FInvMgrName,q.FBrokerName as FBrokerName," +
		                //----------------------------------------------------------------//
		                //edit by songjie 2012.03.03 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A 添加 div.FConfirmMoney, div.FAdjustMoney
		                "d.FCatCode as FCatCode,e.FCashInd as FCashInd,bb.FIsInvest as FIsInvest, div.FDivTOInvNum from " +
		                pub.yssGetTableName("Tb_Data_SubTrade") + " a " +
		                //------------------start----by guyichuan 20110513 STORY #741
		                " left join (select FCashAccCode,FCuryCode from "+ pub.yssGetTableName("Tb_Para_CashAccount")+" )fc"+
		                "  on a.FCashAccCode=fc.FCashAccCode"+
		                " left join (select FSecurityCode,FCuryCode,FDividendDate,FDivdendType,FRecordDate,FIsInvest"+
		                " from "+pub.yssGetTableName("Tb_Data_Dividend")+" where FCheckState = 1  and FIsInvest=1"+
		                " )bb on a.FSecurityCode=bb.FSecurityCode"+
		                " and fc.FCuryCode=bb.FCuryCode  and a.FBargainDate=bb.FDividendDate"+
		                " and a.FDivdendType=bb.FDivdendType and a.FRecordDate=bb.FRecordDate"+
		                //-----------end --STORY #741-----------
		                " left join (select h.FSecurityCode as FSecurityCode,h.FSecurityName,h.FTradeCury as FTradeCury,FCusCatCode, FCatCode, FSubCatCode from " +
		                pub.yssGetTableName("Tb_Para_Security") + " h join " +
		                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
		                pub.yssGetTableName("Tb_Para_Security") +
		                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
		                " and FCheckState = 1 group by FSecurityCode) i " + " on h.FSecurityCode = i.FSecurityCode and h.FStartDate = i.FStartDate) b on a.FSecurityCode = b.FSecurityCode" +
		                //-----------------------
		             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
		                
		    
		                " left join (select FPortCode, FPortCury from " +
		                pub.yssGetTableName("Tb_Para_Portfolio") + 		               
		                " where  FCheckState = 1 ) c on a.FPortCode = c.FPortCode" +
		            
		                //end by lidaolong
		                //-----------------------
		                " left join (select l.FCatCode as FCatCode,l.FSecurityCode as FSecurityCode from " +
		                pub.yssGetTableName("Tb_Para_Security") + " l join " +
		                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
		                pub.yssGetTableName("Tb_Para_Security") +
		                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
		                " and FCheckState = 1 group by FSecurityCode) m " + " on l.FSecurityCode = m.FSecurityCode and l.FStartDate = m.FStartDate) d on a.FSecurityCode = d.FSecurityCode" +
		                //-----------------------
		                " left join (select FTradeTypeCode,FTradeTypeName,FCashInd from Tb_Base_TradeType where FCheckState = 1) e on a.FTradeTypeCode = e.FTradeTypeCode" +
		                //--------------为了使描述信息更清晰。sj modified 20081224 -----------------------------------------------//
		                " left join (select FInvMgrCode,FInvMgrName from " + pub.yssGetTableName("Tb_Para_InvestManager") +
		                " where FCheckState = 1) o on a.FInvMgrCode = o.FInvMgrCode " +
		                " left join (select FBrokerCode,FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker") +
		                " where FCheckState = 1) q on a.FBrokerCode = q.FBrokerCode" +
		                //-----------------------------------------------------------------------------------------------------//
		                //---add by songjie 2012.03.03 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A start---//
		                //获取分红转投数据
		                " left join (select FTradeNum, FNum as FDivTOInvNum from " + 
		                pub.yssGetTableName("Tb_Data_DividendToInvest") 
		                + " where FCheckState = 1) div on a.FNum = div.FTradeNum " + 
		                //---add by songjie 2012.03.03 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A end---//
		                " where (FCheckState = 1 or (FCheckState = 3 and FCreator = " +
		                dbl.sqlString(pub.getUserCode()) + "))"; //"and FAutoSettle = 1 ";
		            if (portCode.length() > 0) {
		                strSql = strSql + " and FFactSettleDate between " +
		                    dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate) +
		                    " and FPortCode in (" +this.operSql.sqlCodes(portCode) + ")";//xuqiji 20090805 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015    国内权益处理
		            }
		            if (nums.length() > 0) {
		            	//------ modify by wangzuochun 2011.02.23 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。 
		            	if (allCount == 1){
		            		strSql = strSql + " and FNum in (" + operSql.sqlCodes(nums) + ") ";
		            	}
		            	else{
		            		int iNext = allCount - i;
		            		
		            		for (int j = i * 1000; j < (iNext > 1 ? (i + 1)*1000 : strArrNums.length); j++){
		            			buf.append(strArrNums[j]).append(",");
		            		}
		            		
		            		String strNums = buf.toString();
		            		buf.delete(0, buf.length());
		            		
		            		if (strNums.length() > 1){
		            			strNums = strNums.substring(0,strNums.length() - 1);
		            		}
		            		
		            		strSql = strSql + " and FNum in (" + operSql.sqlCodes(strNums) + ") ";
		            	}
		            	//-------------------------------- BUG #1003 ---------------------------//
		            }
		            rs = dbl.openResultSet(strSql);
		
		            while (rs.next()) {
		            	//edit by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A
		            	/** add by huangqirong 2012-04-27 story #2565 **/
		            	tradeNum = rs.getString("FNUM");
		            	double etfCashAlternat = rs.getDouble("FETFCashAlternat"); //现金替代
		            	double etfBalaMoney = rs.getDouble("FETFBalaMoney"); //现金差额
		            	
		            	if( etfCashAlternat != 0 || etfBalaMoney != 0 ){		            		
		            		this.createCashTransfer1(rs, tradeNum , etfCashAlternat,  etfBalaMoney , pst ,pstSub , analy1 ,analy2 ,bTPVer ,sTmpCusCat);		            		
		            		continue;
		            	}			           	            	
		            	/****end****/
		            	
		                //2009.07.03 蒋锦 修改 如果交易类型为网上中签，不产生资金调拨，只更新结算标示
		                //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
		                //MS00022   国内债券业务   QDV4.1赢时胜（上海）2009年4月20日22_A
		                if (rs.getDouble("FFactSettleMoney") == 0 ||
		                    rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_WSZQ) ||
		                    (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_XZWSZQ ) && 
		                    //edit by songjie 2012.04.27 BUG 4432 QDV4赢时胜(测试)2012年04月27日03_B 添加FI07、FI06判断
		                    (rs.getString("FSubCatcode").equals("FI06") || rs.getString("FSubCatcode").equals("FI07"))
		                    )
		                    ||  rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_PG) ) {//STORY 2538 add by zhouwei 20120521 配股交易数据结算不产生资金调拨
		                    //现额为0的交易，更新交易数据结算标志
		                    strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
		                        " set FSettleState = 1 where FNum = '" +
		                        rs.getString("FNum") +
		                        "' and FCheckState = 1";
		                    dbl.executeSql(strSql);
		                    continue;
		                }
		
		                sTradeType = rs.getString("FTradetypeCode");
		                if (sTradeType.equalsIgnoreCase("06")) {
		                    //by guyichuan 20110513 STORY #741 如果是股票分红转投的就不产生资金调拨  只更新结算标示
		                	if(rs.getString("FIsInvest")!=null&&"1".equals(rs.getString("FIsInvest").trim())){
		                		strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
		                        " set FSettleState = 1 where FNum = '" +
		                        rs.getString("FNum") +
		                        "' and FCheckState = 1";
		                    dbl.executeSql(strSql);
		                    continue;
		                	}
		                	
		                	sTransferType = "02";
		                } 
		                //MS01336 add by zhangfa 20100817
		                else if(sTradeType.equalsIgnoreCase("88")){
		                	 sTransferType ="02";
		                }
		                //-------------------------------
		                //story 1574 add by zhouwei 20111102分红转投
		                else if(sTradeType.equalsIgnoreCase("39")){
		                	 sTransferType ="02";
		                }
		                //-------------------------------
		                else {
		                    sTransferType = "05";
		                }
		                sFNum = "C" +
		                    YssFun.formatDatetime(rs.getDate("FFactSettleDate")).
		                    substring(0, 8) +
		                    
		                    //20120906 modified by liubo.Bug #5454
		                    //按原有的生成资金调拨编号的逻辑，不按调拨日期取已存在编号的话，可能会取到一个极大的数
		                    //在生成新的调拨编号时，无论结算了几笔，都只会生成100000这个编号，如此就会造成资金调拨数据表的违反主键约束
		                    //===========================================
//		                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"),
//		                                           dbl.sqlRight("FNUM", 6), "000001");
		                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"),
                                    dbl.sqlRight("FNUM", 6), "000001"," where FTransferDate = " + dbl.sqlDate(rs.getDate("FFactSettleDate")),12);

	                    	//========================end===================
		
		                //获取将要被删除的那条资金调拨数据中的FNum，以便删除子表中相同的FNum的记录
		                tmpSql = "select FNum from " +
		                    pub.yssGetTableName("Tb_Cash_Transfer") +
		                    " where FTsfTypeCode='" + sTransferType +
		                    "' and FTransferDate=" +
		                    dbl.sqlDate(rs.getDate("FFactSettleDate")) +
		                    " and FSecurityCode='" + rs.getString("FSecurityCode") +
		                    "' and FTradeNum = '" + rs.getString("FNum") +
		                    "' and FCheckState=1";
		                rsTmp = dbl.openResultSet(tmpSql);
		                if (rsTmp.next()) {
		                    sFTmpNum = rsTmp.getString("FNum");
		                }
		                if (rsTmp != null) {
		                    dbl.closeResultSetFinal(rsTmp);
		
		                    //删除子表中原有的相同的调拨数据
		                }
		                strSqlSub = "delete from " +
		                    pub.yssGetTableName("Tb_Cash_SubTransfer") +
		                    " where FNum in ('" + sFTmpNum + "','" + sFNum + "')";
		                dbl.executeSql(strSqlSub);
		
		                //删除主表中原有的相同的调拨数据
		                strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
		                    " where FTsfTypeCode='" + sTransferType +
		                    "' and FTransferDate=" +
		                    dbl.sqlDate(rs.getDate("FFactSettleDate")) +
		                    " and FTransDate=" + dbl.sqlDate(rs.getDate("FBargainDate")) +
		                    " and FSecurityCode='" + rs.getString("FSecurityCode") +
		                    "' and FTradeNum = '" + rs.getString("FNum") +
		                    "' and FCheckState=1"; // and FDataSource=1";
		                dbl.executeSql(strSql);
		                
		                //获取调拨子类型
		//            tmpSql1 =
		//                  "select FSubTsfTypeCode from Tb_Base_SubTransferType where FSubTsfTypeCode = '" +
		//                  sTransferType + rs.getString("FCatCode") + "'";
		                tmpSql1 =
		                    "select FSubTsfTypeCode from Tb_Base_SubTransferType where FCheckState =1 and FSubTsfTypeCode = '" +
		                    sTransferType + rs.getString("FCatCode") + "'"; //修改目的:只获取已审核的调拨子类型。sj modified 20081225 MS00119
		                rsTmp1 = dbl.openResultSet(tmpSql1);
		                if (!rsTmp1.next()) {
		                    tmp1 = false;
		                }
		                //------如果存在调拨子类型；add by wangzuochun  2010.09.19  MS01761    批量结算产生的资金调拨没有调拨子类型    QDV4赢时胜上海2010年09月17日01_B   
		                else{
		                	tmp1 = true;
		                }
		                //------MS01761------//
		                if (rsTmp1 != null) {
		                    dbl.closeResultSetFinal(rsTmp1);
		
		                    //插入数据到调拨主表
		                }
		                
		                //---add by songjie 2011.10.26 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B start---//
		                //若交易方式 为:锁定 、新股流通 、新债流通    交易数据，则不生成资金调拨数据       update by guolongchao 20111101 bug3001 添加新债流通(62)
		                if(!(rs.getString("FTradeTypeCode").equals("45") || rs.getString("FTradeTypeCode").equals("46")
		                		|| rs.getString("FTradeTypeCode").equals("62")))
		                {
		                	//---add by songjie 2012.03.01 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A start---//
		                	if(rs.getString("FTradeTypeCode").equals("06") && rs.getString("FDivTOInvNum") != null){
				                strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
			                    " set FSettleState=1 where FNum = '" + rs.getString("FNum") +
			                    "'";
				                dbl.executeSql(strSql);
		                		
		                		continue;
		                	}
		                	//---add by songjie 2012.03.01 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A end---//
		                		
		                	pst.setString(1, sFNum);
		                	pst.setString(2, sTransferType);
		                	if (rs.getString("FTradeTypeCode").equalsIgnoreCase("06")) { //当交易类型为股票分红，则固定写成02DV. sj modified 20081224 MS00119
		                		pst.setString(3,
		                			"02DV"); //此处之所以要写成固定的方式，因此交易的品中类型为EQ。故程序中拼接的调拨子类型就一定是02EQ。系统中不存在这样的子类型，所以在之前的子类型的输入为空格。
		                	
		                	}else if(rs.getString("FTradeTypeCode").equalsIgnoreCase("39")){//story 1574 add by zhouwei 20111102 分红转投，生成的调拨子类型为02DV
		                		pst.setString(3, "02DV");
		                	} else {
		                		pst.setString(3,
		                					tmp1 ? sTransferType + rs.getString("FCatCode") :
		                                  	" ");
		                	}
		                	//------ modify by wangzuochun BUG #256 业务处理时，报资金调拨出错（QDV4赢时胜(测试)2010年10月29日01_B.xls） 
		                	//fanghaoln QDV4赢时胜上海2010年04月27日01_AB 
		                	pst.setString(4, rs.getString("fattrclscode") != null ? rs.getString("fattrclscode"):" ");
		                	//-----------------------end----------------------------------------------
		                	//-----------------------------------------BUG #256 ----------------------------------------//
		                	pst.setDate(5, rs.getDate("FFactSettleDate"));
		                	pst.setString(6, "00:00:00");
		                	pst.setDate(7, rs.getDate("FBargainDate"));
		                	pst.setString(8, rs.getString("FNum"));
		                	pst.setString(9, rs.getString("FSecurityCode"));
		                	pst.setInt(10, 1);
		                	pst.setString(11, " ");
		                	pst.setInt(12, (compTemp ? 3 : 1)); //FCheckState=3表示是用于监控临时存储的状态
		                	pst.setString(13, pub.getUserCode());
		                	pst.setString(14, YssFun.formatDatetime(new java.util.Date()));
		                	pst.setString(15, pub.getUserCode());
		                	pst.setString(16, YssFun.formatDatetime(new java.util.Date()));
		                	int cashInt = rs.getInt("FCashInd");
		                	if(cashInt!=0)  //bug 3700 交易类型为为无 不产生资金调拨 modified by yeshenghong 20120213
		                	{
		                		pst.executeUpdate();
		                	}
		
		                	//插入数据到调拨子表
		                	pstSub.setString(1, sFNum);
		                	pstSub.setString(2, "00001");
		                	pstSub.setInt(3, rs.getInt("FCashInd"));
		                	pstSub.setString(4, rs.getString("FPortCode"));
		                	pstSub.setString(5, (analy1 ? rs.getString("FInvMgrCode") : " "));
		                	if (analy2) {
		                		if(bTPVer){//当为太平版本时，将分析代码设置为：所属分类 合并太平版本代码
		                			//MS01230    交易结算生成的资金调拨的所属分类未使用交易所属分类  张发    QDV4中保2010年5月27日01_B   
		                			pstSub.setString(6, 
		                					rs.getString("FAttrClsCode").trim().length() > 0 ? rs.getString("FAttrClsCode"): rs.getString("FCatCode")); 
		                		}else{
		                			sTmpCusCat = rs.getString("FCusCatCode") + "";
		                			if (sTmpCusCat.equalsIgnoreCase("null") ||
		                					sTmpCusCat.trim().length() == 0) {
		                				pstSub.setString(6, rs.getString("FCatCode"));
		                			} else {
		                				pstSub.setString(6, YssFun.right(sTmpCusCat, 2));
		                			}
		                		}
		                	} else {
		                		pstSub.setString(6, " ");
		                	}
		                	pstSub.setString(7, " ");
		                	//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
		                	//modify huangqirong 2012-05-10 story #2565
		                	if(rs.getString("FAttrClsCode") == null || rs.getString("FAttrClsCode").trim().length() == 0)
		                		pstSub.setString(8," ");
		                	else		                		
		                		pstSub.setString(8, rs.getString("FAttrClsCode"));
		                	//---end---
		                	//--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
		                	pstSub.setString(9, rs.getString("FFactCashAccCode"));
		                	pstSub.setDouble(10, rs.getDouble("FFactSettleMoney"));
		                	pstSub.setDouble(11, rs.getDouble("FFactBaseRate"));
		                	pstSub.setDouble(12, rs.getDouble("FFactPortRate"));
		                	pstSub.setInt(13, (compTemp ? 3 : 1)); //FCheckState=3表示是用于监控临时存储的状态
		                	pstSub.setString(14, pub.getUserCode());
		                	pstSub.setString(15, YssFun.formatDatetime(new java.util.Date()));
		                	pstSub.setString(16, pub.getUserCode());
		                	pstSub.setString(17, YssFun.formatDatetime(new java.util.Date()));
		                	if (rs.getString("FTradeTypeCode").equalsIgnoreCase("06")) { //当为分红派息时，备注信息增加投资经理和券商的信息。sj modify 20081127 暂时 无bug QDV4中保2008年11月24日01_A
		                		sDesc = "[" + YssFun.formatDate(rs.getDate("FBargainDate")) +
		                			"]-" +
		                			rs.getString("FTradeTypeName") +
		                			"-[" +
		                			rs.getString("FSecurityName") +
		//                     "[" + rs.getString("FSecurityCode") + "]";
		                			"]"; //只保留证券名称，代码去除。进行再次美化处理。sj modified 20081225
		                		sDesc += rs.getString("FInvMgrCode") == null || rs.getString("FInvMgrCode").trim().length() == 0 ? "" :
		                			"-[" + rs.getString("FInvMgrName") + "]";
		                		sDesc += rs.getString("FBrokerCode") == null || rs.getString("FBrokerCode").trim().length() == 0 ? "" :
		                			"-[" + rs.getString("FBrokerName") + "]";
		                	} else {
		                		sDesc = "[" + YssFun.formatDate(rs.getDate("FBargainDate")) +
		                			"]" +
		                			rs.getString("FTradeTypeName") +
		                			rs.getString("FSecurityName") +
		                			"[" + rs.getString("FSecurityCode") + "]";
		                	}
		                	pstSub.setString(18, sDesc);
		                	if(cashInt!=0)  //bug 3700 交易类型为为无 不产生资金调拨 modified by yeshenghong 20120213
		                	{
		                		pstSub.executeUpdate();
		                	}
		
		                	//2009.06.22 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》  国内基金业务
		                	//如果品种子类型为 ETF 基金，交易类型为申购或者赎回,就需要产生现金差额的资金调拨
		                	if(rs.getString("FSubCatCode")!= null ){  //modify by zhangjun 2012-03-01 BUG3836
		                		if(rs.getString("FSubCatCode").trim().length() > 0 ){
		                			if (rs.getString("FSubCatCode").equalsIgnoreCase(YssOperCons.YSS_ZQPZZLX_TR04) &&
				                			(rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_SGou) ||
				                					rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_SH))) {
				                		insertCashTransferForETFBalanceMoney(rs, pst, pstSub);
				                	}
		                		}
		                		
		                	}
		                }
		                //---add by songjie 2011.10.26 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B end---//
		
		                strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
		                    " set FSettleState=1 where FNum = '" + rs.getString("FNum") +
		                    "'";
		                dbl.executeSql(strSql);
		            }
		            dbl.closeResultSetFinal(rs); //------ add by wangzuochun 2011.02.23 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。 
	            }
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
	            
	        } catch (Exception e) {
	            throw new YssException(strError + "\r\n" + e.getMessage(), e);
	        } finally {
	            dbl.closeStatementFinal(pst);
	            dbl.closeStatementFinal(pstSub);
	            dbl.endTransFinal(conn, bTrans);
	            dbl.closeResultSetFinal(rs);
	            dbl.closeResultSetFinal(rsTmp);
	            dbl.closeResultSetFinal(rsTmp1);
	        }
    	}
    }
    
    /*
     * add by huangqirong 2012-04-27 story #2565
     * ETF现金替代、现金差额
     * */
    public void createCashTransfer1(ResultSet rs, String sNum, double etfCashAlternat, double etfBalaMoney , 
			    		PreparedStatement pst , PreparedStatement pstSub ,boolean analy1 ,boolean analy2 , 
			    		boolean bTPVer ,String sTmpCusCat) throws YssException {
    	String strSql = "";

        try {
        	
        	strSql = " delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
		             " where FNum in (select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") +
		             " where FTradeNum = " + dbl.sqlString(sNum) + ")";
            dbl.executeSql(strSql);

	        strSql = " delete from " + pub.yssGetTableName("tb_Cash_Transfer") +
	                 " where FTradeNum = " + dbl.sqlString(sNum) ;
	        dbl.executeSql(strSql);
            
	        String sDesc = "";
	        String sFNum = "";
	        String sTransferType = "05";
	        String subTransFerType = "";
	        boolean siContinue = true; 
	        boolean isAlternat = true;//是否为替代金额
	        
	        while(siContinue){
	        	
                if(etfCashAlternat == 0 && isAlternat){
                	isAlternat = false;
                	continue;
                }else if(etfCashAlternat != 0 && isAlternat) {                	
                	subTransFerType = "05CR";	
                }else if(etfBalaMoney == 0 && !isAlternat ){
                	siContinue = false ;
                	continue;
                }else if(etfBalaMoney != 0 && !isAlternat ){
                	siContinue = false;
                	subTransFerType = "05CB";	                	
                }	
                
                //edit by songjie 2012.06.20 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A 
                //现金替代金额的结算日期改为 取 交易子表的现金替代结算日期
                sFNum = "C" + YssFun.formatDatetime(isAlternat ? rs.getDate("FMtReplaceDate") : rs.getDate("FETFBalaSettleDate")).
			                  substring(0, 8) + dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"), dbl.sqlRight("FNUM", 6), "000001");
                
                
            	pst.setString(1, sFNum);
            	pst.setString(2, sTransferType);	                	
            	pst.setString(3, subTransFerType);	                	
            	pst.setString(4, rs.getString("fattrclscode") != null ? rs.getString("fattrclscode"):" ");	
            	
            	//edit by songjie 2012.06.20 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A 
            	//现金替代金额的结算日期改为 取 交易子表的现金替代结算日期
            	pst.setDate(5, isAlternat ? rs.getDate("FMtReplaceDate") : rs.getDate("FETFBalaSettleDate"));//现金替代是结算日期，现金差额是差额结算日期	                	
            	pst.setString(6, "00:00:00");
            	pst.setDate(7, rs.getDate("FBargainDate"));
            	pst.setString(8, rs.getString("FNum"));
            	pst.setString(9, rs.getString("FSecurityCode"));
            	pst.setInt(10, 1);
            	pst.setString(11, " ");
            	pst.setInt(12, (compTemp ? 3 : 1)); //FCheckState=3表示是用于监控临时存储的状态
            	pst.setString(13, pub.getUserCode());
            	pst.setString(14, YssFun.formatDatetime(new java.util.Date()));
            	pst.setString(15, pub.getUserCode());
            	pst.setString(16, YssFun.formatDatetime(new java.util.Date()));

        		pst.executeUpdate();            	

            	//插入数据到调拨子表
            	pstSub.setString(1, sFNum);
            	pstSub.setString(2, "00001");
            	pstSub.setInt(3, rs.getInt("FCashInd"));
            	pstSub.setString(4, rs.getString("FPortCode"));
            	pstSub.setString(5, (analy1 ? rs.getString("FInvMgrCode") : " "));
            	if (analy2) {
            		if(bTPVer){//当为太平版本时，将分析代码设置为：所属分类 合并太平版本代码
            			// 交易结算生成的资金调拨的所属分类未使用交易所属分类   
            			if(rs.getString("FAttrClsCode") == null || rs.getString("FAttrClsCode").trim().length() == 0)
	                		pstSub.setString(6,rs.getString("FCatCode"));
	                	else 
	                		pstSub.setString(6,rs.getString("FAttrClsCode"));	                			
            		}else{
            			sTmpCusCat = rs.getString("FCusCatCode") + "";
            			if (sTmpCusCat.equalsIgnoreCase("null") ||
            					sTmpCusCat.trim().length() == 0) {
            				pstSub.setString(6, rs.getString("FCatCode"));
            			} else {
            				pstSub.setString(6, YssFun.right(sTmpCusCat, 2));
            			}
            		}
            	} else {
            		pstSub.setString(6, " ");
            	}
            	pstSub.setString(7, " ");
            	//---用户需要对组合按资本类别进行子组合的分类 
            	if(rs.getString("FAttrClsCode") == null || rs.getString("FAttrClsCode").trim().length() == 0)
            		pstSub.setString(8," ");
            	else 
            		pstSub.setString(8,rs.getString("FAttrClsCode"));
            	// 用户需要对组合按资本类别进行子组合的分类  
            	
            	/******现金替代 *现金差额   金额和账户*******/
            	double money = 0.0 ;
            	String cashAcc = "";
            	
            	if(isAlternat){
            		money = rs.getDouble("FETFCashAlternat");
            		cashAcc = rs.getString("FCashAccCode");	                		
            	}else {
            		money = rs.getDouble("FETFBalaMoney");
            		cashAcc = rs.getString("FETFBalaAcctCode");            		
            	}	                	
            	
            	pstSub.setString(9, cashAcc);	                	
            	pstSub.setDouble(10, money);	                	
            	pstSub.setDouble(11, rs.getDouble("FFactBaseRate"));
            	pstSub.setDouble(12, rs.getDouble("FFactPortRate"));
            	pstSub.setInt(13, (compTemp ? 3 : 1)); //FCheckState=3表示是用于监控临时存储的状态
            	pstSub.setString(14, pub.getUserCode());
            	pstSub.setString(15, YssFun.formatDatetime(new java.util.Date()));
            	pstSub.setString(16, pub.getUserCode());
            	pstSub.setString(17, YssFun.formatDatetime(new java.util.Date()));
            	if (rs.getString("FTradeTypeCode").equalsIgnoreCase("06")) { //当为分红派息时，备注信息增加投资经理和券商的信息。
            		sDesc = "[" + YssFun.formatDate(rs.getDate("FBargainDate")) +
            			"]-" +
            			rs.getString("FTradeTypeName") +
            			"-[" +
            			rs.getString("FSecurityName") +
            			"]"; //只保留证券名称，代码去除。进行再次美化处理。
            		sDesc += rs.getString("FInvMgrCode") == null || rs.getString("FInvMgrCode").trim().length() == 0 ? "" :
            			"-[" + rs.getString("FInvMgrName") + "]";
            		sDesc += rs.getString("FBrokerCode") == null || rs.getString("FBrokerCode").trim().length() == 0 ? "" :
            			"-[" + rs.getString("FBrokerName") + "]";
            	} else {
            		sDesc = "[" + YssFun.formatDate(rs.getDate("FBargainDate")) +
            			"]" +
            			rs.getString("FTradeTypeName") +
            			rs.getString("FSecurityName") +
            			"[" + rs.getString("FSecurityCode") + "]";
            	}
            	pstSub.setString(18, sDesc);
            
        		pstSub.executeUpdate();
        		
        		if(isAlternat )
        			isAlternat = false;
            }
                
            strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                     " set FSettleState=1 where FNum = '" + rs.getString("FNum") + "'";
            dbl.executeSql(strSql);	  
        
        }catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } 
    }

    public void unCreateCashTransfer() throws
        YssException {

        String strSql = "";
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        try {
        	
        	//------ add by wangzuochun 2011.02.23 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。 
	        int allRecord = 0; //总的结算数；
	        int allCount = 0; //循环次数，1000条循环一次
	        String[] strArrNums = null;
	        StringBuffer buf = new StringBuffer();
	        String strNums = "";
	        //---------------BUG #1003--------------//
	        
	        //------ add by wangzuochun 2011.02.23 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。 
            if (this.nums != null && this.nums.length() > 0){
            	strArrNums = this.nums.split(",");
            	allRecord = strArrNums.length; //得到总的结算数
            	allCount = (allRecord % 1000 == 0 ? allRecord / 1000 : allRecord / 1000 + 1);//得到循环次数
            }
            //------------------------------- BUG #1003 -------------------------------------// 

            conn.setAutoCommit(false);
            bTrans = true;
            
           //------ modify by wangzuochun 2011.02.23 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。 
            for (int i = 0; i < allCount; i++){
            	
            	//------ modify by wangzuochun 2011.02.23 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。 
            	if (allCount == 1){
            		strNums = this.nums;
            	}
            	else{
            		int iNext = allCount - i;
            		
            		for (int j = i * 1000; j < (iNext > 1 ? (i + 1)*1000 : strArrNums.length); j++){
            			buf.append(strArrNums[j]).append(",");
            		}
            		
            		strNums = buf.toString();
            		buf.delete(0, buf.length());
            		
            		if (strNums.length() > 1){
            			strNums = strNums.substring(0,strNums.length() - 1);
            		}
            	}
            	
	            strSql = "delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
	                " where FNum in (select FNum from " +
	                pub.yssGetTableName("Tb_Cash_Transfer") +
	                " where FTradeNum in (" + operSql.sqlCodes(strNums) + "))";
	            dbl.executeSql(strSql);
	
	            strSql = "delete from " + pub.yssGetTableName("tb_Cash_Transfer");
	            if (portCode.length() > 0) {
	                strSql = strSql + " where FFactSettleDate between " +
	                    dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate) +
	                    " and FPortCode in (" + portCode + ")";
	            }
	            if (strNums.length() > 0) {
	                strSql = strSql + " where FTradeNum in (" + operSql.sqlCodes(strNums) +
	                    ") ";
	            }
	            dbl.executeSql(strSql);
	
	            //更新交易数据结算标志为0
	            strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
	                " set FSettleState=0 where FNum in (" + operSql.sqlCodes(strNums) +
	                ")";
	            dbl.executeSql(strSql);
            }
            //-------------------------------- BUG #1003 ---------------------------//

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 2009.06.22 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》  国内基金业务
     * 产生 ETF 现金差额的资金调拨
     * @param rsTrade ResultSet：交易数据
     * @param pst PreparedStatement：调拨主表
     * @param pstSub PreparedStatement：调拨子表
     * @throws YssException
     */
    private void insertCashTransferForETFBalanceMoney(ResultSet rsTrade,
        PreparedStatement pst,
        PreparedStatement pstSub) throws YssException {
        String sFNum = "";
        String sTmpCusCat = "";

        boolean analy1;
        boolean analy2;

        try {
            if (rsTrade.getDouble("FETFBalaMoney") == 0) {
                return;
            }
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");

            sFNum = "C" +
                YssFun.formatDatetime(rsTrade.getDate("FFactSettleDate")).
                substring(0, 8) +
                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"),
                                       dbl.sqlRight("FNUM", 6), "000001");
            pst.setString(1, sFNum);
            pst.setString(2, "02");
            pst.setString(3, "02TR");
            pst.setString(4, " ");
            pst.setDate(5, rsTrade.getDate("FETFBalaSettleDate"));
            pst.setString(6, "00:00:00");
            pst.setDate(7, rsTrade.getDate("FBargainDate"));
            pst.setString(8, rsTrade.getString("FNum"));
            pst.setString(9, rsTrade.getString("FSecurityCode"));
            pst.setInt(10, 1);
            pst.setString(11, " ");
            pst.setInt(12, (compTemp ? 3 : 1)); //FCheckState=3表示是用于监控临时存储的状态
            pst.setString(13, pub.getUserCode());
            pst.setString(14, YssFun.formatDatetime(new java.util.Date()));
            pst.setString(15, pub.getUserCode());
            pst.setString(16, YssFun.formatDatetime(new java.util.Date()));
            pst.executeUpdate();

            //插入数据到调拨子表
            pstSub.setString(1, sFNum);
            pstSub.setString(2, "00001");
            pstSub.setInt(3, rsTrade.getInt("FCashInd"));
            pstSub.setString(4, rsTrade.getString("FPortCode"));
            pstSub.setString(5, (analy1 ? rsTrade.getString("FInvMgrCode") : " "));
            if (analy2) {
                sTmpCusCat = rsTrade.getString("FCusCatCode") + "";
                if (sTmpCusCat.equalsIgnoreCase("null") ||
                    sTmpCusCat.trim().length() == 0) {
                    pstSub.setString(6, rsTrade.getString("FCatCode"));
                } else {
                    pstSub.setString(6, YssFun.right(sTmpCusCat, 2));
                }
            } else {
                pstSub.setString(6, " ");
            }
            pstSub.setString(7, " ");
            pstSub.setString(8, rsTrade.getString("FETFBalaAcctCode"));
            pstSub.setDouble(9, rsTrade.getDouble("FETFBalaMoney"));
            pstSub.setDouble(10, rsTrade.getDouble("FFactBaseRate"));
            pstSub.setDouble(11, rsTrade.getDouble("FFactPortRate"));
            pstSub.setInt(12, (compTemp ? 3 : 1)); //FCheckState=3表示是用于监控临时存储的状态
            pstSub.setString(13, pub.getUserCode());
            pstSub.setString(14, YssFun.formatDatetime(new java.util.Date()));
            pstSub.setString(15, pub.getUserCode());
            pstSub.setString(16, YssFun.formatDatetime(new java.util.Date()));
            pstSub.setString(17, "");
            pstSub.executeUpdate();

        } catch (Exception ex) {
            throw new YssException("产生 ETF 现金差额应收应付数据出错！", ex);
        }
    }

    public String getNums() {
        return nums;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getPortCode() {
        return portCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
