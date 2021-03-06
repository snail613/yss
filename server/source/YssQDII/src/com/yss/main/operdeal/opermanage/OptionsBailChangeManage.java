package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import com.yss.commeach.EachGetPubPara;
import com.yss.main.basesetting.ExchangeBean;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 此类做期权保证金变动时，改变保证金金额的业务处理类
 * @author xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
 *
 */
public class OptionsBailChangeManage extends BaseOperManage{
	private ArrayList cashTransArr = null; //存放资金调拨数据
	public OptionsBailChangeManage() {
		super();
	}
	/**
	 * 初始化变量
	 */
	public void initOperManageInfo(Date dDate, String portCode) throws YssException {
		this.dDate = dDate;
        this.sPortCode = portCode;
	}
	/**
	 * 入口方法
	 */
	public void doOpertion() throws YssException {
		try{
			saveCashTransferData();//产生资金调拨数据
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	 /**
     * saveCashTransferData 产生资金调拨数据
     *
     * @param rs ResultSet
     * @param dChangeMoney double 调整金额
     */
    private void saveCashTransferData()throws YssException {
        CashTransAdmin cashtrans = null; //初始化资金调拨操作类
        String filtersRelaNums = ""; //保存交易编号
        boolean bTrans = false; //事务控制
        Connection conn = dbl.loadConnection(); //获取连接
        try {
            cashTransArr = setCashTransferDate(); //设置资金调拨数据返回ArrayList
            if (cashTransArr.size() > 0) { //是否当天有交易
                cashtrans = new CashTransAdmin();
                cashtrans.setYssPub(pub);
                cashtrans.addList(cashTransArr); //子资金调拨的值放入TransferBean中的arrayList中
                //增加事务控制和锁，以免在多用户同时处理时出现调拨编号重复
                conn.setAutoCommit(false); //设置不自动提交事务
                bTrans = true;
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer")); //给操作的表加锁
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));
                //编号类型为FUChBailMoney 表示期货保证金调整金额
                cashtrans.insert(this.dDate,"FPChBailMoney", filtersRelaNums); //插入数据到资金调拨表和子表中
                conn.commit(); //提交事务
                conn.setAutoCommit(true); //设置为自动提交事务
                bTrans = false;
                
            }
            //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
        	//当日产生数据，则认为有业务。
        	if(cashTransArr==null || cashTransArr.size()==0){
        			this.sMsg="        当日无业务";
        	}
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }finally{
            dbl.endTransFinal(conn,bTrans);
        }
    }
    /**
     * setCashTransferDate 设置资金调拨数据返回ArrayList
     *
     * @param rs ResultSet
     * @param rst ResultSet
     * @param dChangeMoney double
     * @return ArrayList
     */
    private ArrayList setCashTransferDate() throws YssException {
        boolean analy1; //分析代码1
        boolean analy2; //分析代码2
        boolean analy3; //分析代码3
        ArrayList curCashTransArr = null; //保存资金调拨数据
        TransferBean transfer = null; //调拨类初始化
        TransferSetBean transferset = null; //调拨子类初始化
        ArrayList subtransfer = null; //保存资金子调拨
        String sql = "";
        ResultSet rs = null;
        try{
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            curCashTransArr = new ArrayList();
            
            rs = dbl.queryByPreparedStatement(" select * from "+pub.yssGetTableName("tb_data_optionbailchange")+" where fcheckstate=1 and FCHANGEDATE = "+dbl.sqlDate(this.dDate)+" and fportcode in ("+this.operSql.sqlCodes(this.sPortCode)+")");
            
            while(rs.next()){
            	 subtransfer = new ArrayList();
                 transfer = setTransferAttr(rs, "IN"); //设置调拨类型
                 
                 //设置调拨子类型，开仓变动保证金流出
                 transferset = setTransferSetAttr(rs, analy1, analy2, analy3, "PC");
                 subtransfer.add(transferset);
                 //设置调拨子类型， 开仓初始保证金流入
                 transferset = setTransferSetAttr(rs, analy1, analy2, analy3, "PCWITHFEE");
                 subtransfer.add(transferset);
                 transfer.setSubTrans(subtransfer); //设置调拨子类型
                 curCashTransArr.add(transfer);
            }
            
//            //从期货、期权保证金调整表，库存表，期权交易数据表中，获取调整保证金金额，库存数量，当天之前所有交易的保证金总额
//            sql=getChangeMoneyStorageMountTotalBegBailMoney(analy1,analy2);
//            rs = dbl.openResultSet(sql);
//            while(rs.next()) {
//                subtransfer = new ArrayList();
//                transfer = setTransferAttr(rs, "IN"); //设置调拨类型
//                //设置调拨子类型，开仓变动保证金流出
//                transferset = setTransferSetAttr(rs, analy1, analy2, analy3, "PC");
//                subtransfer.add(transferset);
//                //设置调拨子类型， 开仓初始保证金流入
//                transferset = setTransferSetAttr(rs, analy1, analy2, analy3, "PCWITHFEE");
//                subtransfer.add(transferset);
//                transfer.setSubTrans(subtransfer); //设置调拨子类型
//                curCashTransArr.add(transfer);
//            }
        }catch(Exception e){
            throw new YssException("设置资金调拨数据返回ArrayList出错！",e);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
        return curCashTransArr;
    }
    
    /**************************************************************
     * 估算法需进行修改：金额=调整金额-昨日初始保证金账户余额；
     * @ desc 【STORY #863 香港、美国股指期权交易区别  期权需求变更】 
     * @author benson
     * @date 2011.06.20 
     * @param rs
     * @param analy1
     * @param analy2
     * @param analy3
     * @param type
     * @return
     * @throws YssException
     */
    private TransferSetBean setTransferSetAttr(ResultSet rs, boolean analy1,
          boolean analy2, boolean analy3,
         String type) throws YssException {
    	
    	TransferSetBean transferset = new TransferSetBean();
        double dBaseRate = 0; //基础汇率
        double dPortRate = 0; //组合汇率
        double money = 0.0; //调拨金额
        double dYesterDayBailStock = 0;//昨日初始保证金余额
        SecurityBean security = null; //证券信息类
        CashAccountBean cash = null; //帐户信息
        PortfolioBean port = null;//组合信息
        ExchangeBean exchange = null;//交易所信息
        String sType = ""; 
        String sDesc = "";
        try {
        	       	
            transferset.setSPortCode(rs.getString("FPortCode")); //设置组合代码
           
            //期权保证金调整界面，只有券商代码，所以在这里其他两个分析代码默认设置为空
            transferset.setSAnalysisCode1(" ");
            if (analy2) {
                transferset.setSAnalysisCode2(rs.getString("FBrokerCode")); 
            }else{
                transferset.setSAnalysisCode2(" ");
            }
            transferset.setSAnalysisCode3(" ");
            
            if (type.equalsIgnoreCase("kCBailIN")) { //开仓变动保证金流入
                transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                money = rs.getDouble("FBegBailMoney"); //调拨金额
                transferset.setIInOut(1); //调拨方向 流入
            } else if (type.equalsIgnoreCase("kCBailOut")) { //开仓初始保证金流出
                transferset.setSCashAccCode(rs.getString("FBegBailAcctCode"));
                money = rs.getDouble("FBegBailMoney");
                transferset.setIInOut( -1);//调拨方向 流出
            } else if (type.equalsIgnoreCase("PC")) { //平仓的设置，流出。
                transferset.setSCashAccCode(rs.getString("FChageBailAcctCode")); //变动保证金，流出
                dYesterDayBailStock = getYesTerDayBailStock(rs.getString("FPORTCODE"),rs.getString("FSecurityCode"),rs.getString("FEXCHANGECODE"),rs.getString("FBROKERCODE"), analy2);
                money =YssD.sub(rs.getDouble("FChangeMoney"), dYesterDayBailStock);
                transferset.setIInOut( -1);//调拨方向 流出
            } else if (type.equalsIgnoreCase("PCWITHFEE")) { //平仓的设置，流入。
                transferset.setSCashAccCode(rs.getString("FBegBailAcctCode")); //初始保证金，流入
                dYesterDayBailStock = getYesTerDayBailStock(rs.getString("FPORTCODE"),rs.getString("FSecurityCode"),rs.getString("FEXCHANGECODE"),rs.getString("FBROKERCODE"), analy2);
                money =YssD.sub(rs.getDouble("FChangeMoney"), dYesterDayBailStock);
                transferset.setIInOut(1);//调拨方向 流入
                
            }
            
             cash = new CashAccountBean();
             cash.setYssPub(pub);
             cash.setStrCashAcctCode(rs.getString("FCHAGEBAILACCTCODE")); //币种取自变动保证金帐户
             cash.getSetting();
             
             port = new PortfolioBean();
             port.setYssPub(pub);
             port.getSetting();
             
            dBaseRate = this.getSettingOper().getCuryRate(dDate,
            		cash.getStrCurrencyCode(),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
            dPortRate = this.getSettingOper().getCuryRate(dDate,
                    port.getCurrencyCode(),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);
            
           
            if(rs.getString("FSecurityCode").trim().length()!=0){
              security = new SecurityBean();
              security.setYssPub(pub);
              security.setSecurityCode(rs.getString("FSecurityCode"));//设置证券代码
              security.getSetting();
              sDesc ="【证券 "+security.getStrSecurityName()+" 的保证金调整】";
           }else{
        	   exchange = new ExchangeBean();
        	   exchange.setYssPub(pub);
        	   exchange.setStrExchangeCode(rs.getString("FEXCHANGECODE"));
        	   exchange.getSetting();
        	   sDesc ="【交易所  "+exchange.getStrExchangeName()+" 的保证金调整】";
            }
            transferset.setDMoney(money);//设置调拨金额
            transferset.setDBaseRate(dBaseRate);//设置基础汇率
            transferset.setDPortRate(dPortRate);//设置组合汇率
            transferset.setSDesc(sDesc);
            transferset.checkStateId = 1;

        } catch (Exception e) {
            throw new YssException("设置资金调拨子表数据出错！", e);
        }
        return transferset;
    }
    /*****************************************************************
     *  获取昨日期权金余额
     *  @ desc 【STORY #863 香港、美国股指期权交易区别  期权需求变更】 
     * @author benson
     * @date   2011.06.20
     * @param portcode
     * @param securitycode
     * @param exchangecode
     * @param brokerCode
     * @param analy2
     * @return
     * @throws YssException
     */
    private double getYesTerDayBailStock(String portcode,String securitycode,String exchangecode,String brokerCode,boolean analy2) throws YssException{
    	
    	double totalMoney =0;
    	StringBuffer buff = new StringBuffer();
    	ResultSet rs = null;
    	String sType = "";
    	boolean brokerFlag = false;
    	EachGetPubPara pubPara = new EachGetPubPara();;
        pubPara.setYssPub(pub);
        String sCostAccount_Para="";//核算成本参数
        String[] CostAccountParas=null;
    	String sExchange="",sSubCatCode="";
    	Date StartDate=null;
    	try{
    		// 说明： 昨日保证金额余额不直接取现金库存，是因为存在期权、期货不同交易所共用一个保证金账户情况。
    		//获取昨日初始保证金余额时，取数细分到那个程度，由初始保证金界面设置决定
            //如果设置了交易证券，取数则细分到每支证券，如果没设置，默认细分到交易所
            //如果设置了券商并且分析代码2设置为true,则取数也要区分券商；
    		 sType = securitycode.trim().length()==0?"exchange":"security";
    		 brokerFlag = brokerCode.trim().length()==0?false:true;
             analy2 = analy2&&brokerFlag;
    		
    		if(sType.equalsIgnoreCase("exchange")){
    			//期权调整金额明细到交易所
    			buff.append(" select sum(opt.fbegbailmoney) as FTotalBailMoney,opt.fexchangecode,opt.fportcode ");
    			buff.append(analy2 == true ? " ,opt.fbrokercode":"");
    			buff.append(" from (select case when ftradetypecode='02' and foffsetflag = 'off' then  -fbegbailmoney else fbegbailmoney end as fbegbailmoney, ");
    			buff.append(" sec.fexchangecode,fportcode ");
    			buff.append(analy2 == true ? " ,d.fbrokercode":"");
    			buff.append(" from ").append(pub.yssGetTableName("tb_data_optionstrade")).append(" c ");
    			buff.append(" join ");
                buff.append(" (select fsecuritycode,fexchangecode from ").append(pub.yssGetTableName("tb_para_security"));
                buff.append(" b where fcheckstate=1 and fcatcode='FP' ");
                buff.append(" and fexchangecode =").append(dbl.sqlString(exchangecode));
                buff.append(" )sec on c.fsecuritycode = sec.fsecuritycode ");
    			buff.append(" where c.FCheckState = 1");
    			buff.append(" and c.fportcode =").append(dbl.sqlString(portcode));
    			buff.append(" and c.FBargaindate < ").append(dbl.sqlDate(this.dDate));
    			if(analy2){
	            	 buff.append(" ,and c.FBROKERCODE =").append(dbl.sqlString(brokerCode));
	                }
    			buff.append(" and c.ftradetypecode in( ").append(dbl.sqlString(YssOperCons.YSS_JYLX_Sale));//卖出
    			buff.append(" ,").append(dbl.sqlString(YssOperCons.YSS_JYLX_Excerise));//期权行权
    			buff.append(",").append(dbl.sqlString(YssOperCons.YSS_JYLX_DropExcerise)).append(")");//期权放弃行权
    			
    			buff.append(" union ");
    			buff.append(" select fer.FMoney, fer.FSecurityCode, fer.FPortCode ");
    			buff.append(analy2 == true ? " ,fer.FAnalysisCode2 as fbrokercode":"");
    			buff.append("  from (select tf.*, sub.*,optionsvalcal.fexchagecode  from ").append( pub.yssGetTableName("tb_cash_transfer"));
    			buff.append(" tf join (select * from ").append( pub.yssGetTableName("tb_cash_subtransfer"));
    			buff.append("  where FCheckState = 1 and FPortCode =").append(dbl.sqlString(portcode));
	            if(analy2){
  	            	 buff.append(" ,and FAnalysisCode2 =").append(dbl.sqlString(brokerCode));
  	                }
	            buff.append(" and FInOut = 1) sub on tf.fnum = sub.fnum ");
	            buff.append(" join (select FCASHACCCODE,fexchagecode from ").append(pub.yssGetTableName("tb_data_optionsvalcal"));
	            buff.append(" where fcheckstate=1 and fmarktype=0 and fexchagecode=").append(dbl.sqlString(exchangecode));
	            buff.append(" AND FPORTCODE=").append(dbl.sqlString(portcode)).append(" )optionsvalcal on sub.fcashacccode = optionsvalcal.fcashacccode ") ; 
	            buff.append(" where tf.FcheckState = 1 and tf.FTransferDate < ").append(dbl.sqlDate(this.dDate));
	            buff.append(" and tf.FSubTsfTypeCode =").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FP_CHM));
	            buff.append(" ) fer ) opt ");
                buff.append(" group by opt.fportcode,opt.fexchangecode ");
                buff.append(analy2 == true ? " ,opt.fbrokercode":"");
                
    		}else if(sType.equalsIgnoreCase("security")){
    			//期权调整金额明细到期权合约
    			buff.append(" select sum(opt.fbegbailmoney) as FTotalBailMoney,opt.fsecuritycode,opt.fportcode ");
    			buff.append(analy2 == true ? " ,opt.fbrokercode":"");
    			buff.append(" from (select case when ftradetypecode='02' and foffsetflag = 'off' then  fbegbailmoney else -fbegbailmoney end as fbegbailmoney, ");
    			buff.append(" fsecuritycode,fportcode ");
    			buff.append(analy2 == true ? " ,d.fbrokercode":"");
    			buff.append(" from ").append(pub.yssGetTableName("tb_data_optionstrade"));
    			buff.append(" c where c.FCheckState = 1");
    			buff.append(" and c.fportcode =").append(dbl.sqlString(portcode));
    			buff.append(" and c.FBargaindate < ").append(dbl.sqlDate(this.dDate));
    			if(analy2){
	            	 buff.append(" ,and c.FBROKERCODE =").append(dbl.sqlString(brokerCode));
	                }
    			buff.append(" and fsecuritycode = ").append(dbl.sqlString(securitycode));
    			buff.append(" and c.ftradetypecode in( ").append(dbl.sqlString(YssOperCons.YSS_JYLX_Sale));//卖出
    			buff.append(" ,").append(dbl.sqlString(YssOperCons.YSS_JYLX_Excerise));//期权行权
    			buff.append(",").append(dbl.sqlString(YssOperCons.YSS_JYLX_DropExcerise)).append(")");//期权放弃行权
    			buff.append(" union ");
    			buff.append(" select fer.FMoney, fer.FSecurityCode, fer.FPortCode ");
    			buff.append(analy2 == true ? " ,fer.FAnalysisCode2 as fbrokercode":"");
    			buff.append("  from (select tf.*, sub.* from ").append( pub.yssGetTableName("tb_cash_transfer"));
    			buff.append(" tf join (select * from ").append( pub.yssGetTableName("tb_cash_subtransfer"));
    			buff.append("  where FCheckState = 1 and FPortCode =").append(dbl.sqlString(portcode));
	            if(analy2){
  	            	 buff.append(" ,and FAnalysisCode2 =").append(dbl.sqlString(brokerCode));
  	                }
	            buff.append(" and FInOut = 1) sub on tf.fnum = sub.fnum ");
	            buff.append(" where tf.FcheckState = 1 and tf.FTransferDate < ").append(dbl.sqlDate(this.dDate));
	            buff.append(" and tf.fsecuritycode = ").append(dbl.sqlString(securitycode));
	            buff.append(" and tf.FSubTsfTypeCode =").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FP_CHM));
	            buff.append(" ) fer ");
                buff.append(") opt group by opt.fportcode,opt.fsecuritycode ");
                buff.append(analy2 == true ? " ,opt.fbrokercode":"");
    		}
    		
    		rs = dbl.queryByPreparedStatement(buff.toString());
    		if(rs.next()){
    			totalMoney = rs.getDouble("FTotalBailMoney");
    		}
    		
    		buff.setLength(0);
    		dbl.closeResultSetFinal(rs);
    		
    		
    		/************************************************************
    		 *  如果设置了不核算期权成本，那么保证金部分还要包括买入的开平仓
    		 *  部分的保证金
    		 */
    		sCostAccount_Para =pubPara.getOptCostAccountSet(portcode);
    		if(sCostAccount_Para.trim().length()>0){
    			CostAccountParas = sCostAccount_Para.split("\t");
    			sExchange=CostAccountParas[0];
    			sSubCatCode=CostAccountParas[1];
    	    	StartDate=YssFun.parseDate(CostAccountParas[2]);
    	    	

    	    	buff.append(" select sum(case when c.foffsetflag = 'off' then -c.fbegbailmoney else c.fbegbailmoney end) as FTotalBailMoney,fportcode ");
    			buff.append(sType.equalsIgnoreCase("exchange")?" ,d.fexchangecode ":" ,c.fsecuritycode ");
    			buff.append(analy2 == true ? " ,c.fbrokercode":"");
    			buff.append(" from ").append(pub.yssGetTableName("tb_data_optionstrade"));
    			buff.append(" c join (select * from " + pub.yssGetTableName("tb_para_security") + " where fcheckstate=1 ");
    			if(sType.equalsIgnoreCase("exchange")){
    				buff.append(" and fexchangecode= ").append(dbl.sqlString(exchangecode));
    			}
    			buff.append(" and fsubcatcode= ").append(dbl.sqlString(sSubCatCode)).append(")d ");
    			buff.append(" on c.fsecuritycode = d.fsecuritycode");
    			buff.append(" where c.FCheckState = 1");
    			buff.append(" and c.fportcode =").append(dbl.sqlString(portcode));
    			buff.append(" and c.FBargaindate < ").append(dbl.sqlDate(this.dDate));
    			buff.append(" and c.FBargaindate >= ").append(dbl.sqlDate(StartDate));//从启用日期到前一天
    			if(analy2){
	            	 buff.append(" ,and c.FBROKERCODE =").append(dbl.sqlString(brokerCode));
	                }
    			buff.append(" and c.ftradetypecode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_Buy));//买入
    			buff.append(sType.equalsIgnoreCase("exchange")?" group by d.fexchangecode ":" group by c.fsecuritycode ").append(" ,c.fportcode");
                buff.append(analy2 == true ? " ,c.fbrokercode":"");
                
                rs = dbl.queryByPreparedStatement(buff.toString());
        		if(rs.next()){
        			totalMoney += rs.getDouble("FTotalBailMoney");
        		}
        		
        		buff.setLength(0);
        		dbl.closeResultSetFinal(rs);
    		}
    		
    		return totalMoney;
    	}catch(Exception e){
    		throw new YssException("获取昨日初始保证金库存出错.....");
    	}
    }
    
    /**从期货、期权保证金调整表，库存表，期权交易数据表中，获取调整保证金金额，库存数量，当天之前所有交易的保证金总额的sql语句
     * getChangeMoneyStorageMountTotalBegBailMoney
     *@param analy1 分析代码1
     *@param analy2 分析代码2
     * @return String
     */
//    private String getChangeMoneyStorageMountTotalBegBailMoney(boolean analy1,boolean analy2) throws YssException {
//        StringBuffer buff=null;
//        String sYearMonth = "";
//        try{
//        	sYearMonth = YssFun.left(YssFun.formatDate(this.dDate,"yyyyMMdd"),4) +"00";
//            //中间SQL语句的 union all 保证金总额算法=今天之前所有开仓保证金总额-今天之前所有平仓保证金总额+今天之前所有保证金调整资金调拨额
//            //库存数量=库存表中前一日库存数量
//            //条件：证券代码，组合代码，投资经理，券商
//            buff = new StringBuffer();
//            buff.append(" select a.*,b.FStorageAmount,b.FBaseCuryRate, b.FPortCuryRate,e.*,f.fexchangename from ").append(pub.yssGetTableName("tb_data_futurebailchange"));
//            buff.append(" a join (select * from ").append(pub.yssGetTableName("tb_stock_security"));
//            buff.append(" where FCheckState =1 and FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
//            buff.append(" and FStoragedate = ").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));
//            buff.append(" and FYearMonth <> ").append(dbl.sqlString(sYearMonth));
//            buff.append(" ) b on a.fsecuritycode = b.fsecuritycode ");
//            buff.append(analy1 == true ? " and a.finvmgrcode = b.FAnalysisCode2":"");
//            buff.append(analy2 == true ? " and a.fbrokercode = b.FAnalysisCode1":"");
//            buff.append(" join (select sum(d.fbegbailmoney) as FTotalBailMoney,d.fsecuritycode,d.fportcode ");
//            buff.append(analy1 == true ? " ,d.finvmgrcode":"");
//            buff.append(analy2 == true ? " ,d.fbrokercode":"");
//            buff.append(" from (select sum(case when c.foffsetflag = 'off' then c.fbegbailmoney else -c.fbegbailmoney end) as fbegbailmoney,c.fsecuritycode,c.fportcode ");
//            buff.append(analy1 == true ? " ,c.finvmgrcode":"");
//            buff.append(analy2 == true ? " ,c.fbrokercode":"");
//            buff.append(" from ").append(pub.yssGetTableName("tb_data_optionstrade"));
//            buff.append(" c where c.FCheckState =1 and c.fportcode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
//            buff.append(" and c.FBargaindate < ").append(dbl.sqlDate(this.dDate));
//            buff.append(" and c.ftradetypecode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_Sale));//卖出
//            buff.append(" group by c.fsecuritycode,c.fportcode");
//            buff.append(analy1 == true ? " ,c.finvmgrcode":"");
//            buff.append(analy2 == true ? " ,c.fbrokercode":"");
//            buff.append(" union all select sum(-c.fbegbailmoney) as fbegbailmoney,c.fsecuritycode,c.fportcode");
//            buff.append(analy1 == true ? " ,c.finvmgrcode":"");
//            buff.append(analy2 == true ? " ,c.fbrokercode":"");
//            buff.append(" from ").append(pub.yssGetTableName("tb_data_optionstrade"));
//            buff.append(" c where c.FCheckState =1 and c.fportcode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
//            buff.append(" and c.FBargaindate < ").append(dbl.sqlDate(this.dDate));
//            buff.append(" and c.ftradetypecode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_Excerise));//期权行权
//            buff.append(" group by c.fsecuritycode,c.fportcode");
//            buff.append(analy1 == true ? " ,c.finvmgrcode":"");
//            buff.append(analy2 == true ? " ,c.fbrokercode":"");
//            buff.append(" union all ");
//            buff.append(" select sum(-c.fbegbailmoney) as fbegbailmoney,c.fsecuritycode,c.fportcode");
//            buff.append(analy1 == true ? " ,c.finvmgrcode":"");
//            buff.append(analy2 == true ? " ,c.fbrokercode":"");
//            buff.append(" from ").append(pub.yssGetTableName("tb_data_optionstrade"));
//            buff.append(" c where c.FCheckState =1 and c.fportcode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
//            buff.append(" and c.FBargaindate < ").append(dbl.sqlDate(this.dDate));
//            buff.append(" and c.ftradetypecode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_DropExcerise));//期权放弃行权
//            buff.append(" group by c.fsecuritycode,c.fportcode");
//            buff.append(analy1 == true ? " ,c.finvmgrcode":"");
//            buff.append(analy2 == true ? " ,c.fbrokercode":"");
//            buff.append(" union all ");
//            buff.append(" select sum(FMoney) as FBegBailMoney,fer.FSecurityCode,fer.FPortCode");
//            buff.append(analy1 == true ? " ,fer.FAnalysisCode1 as finvmgrcode":"");
//            buff.append(analy2 == true ? " ,fer.FAnalysisCode2 as fbrokercode":"");
//            buff.append(" from (select tf.*, sub.* from ").append(pub.yssGetTableName("tb_cash_transfer"));
//            buff.append(" tf join (select * from ").append(pub.yssGetTableName("tb_cash_subtransfer"));
//            buff.append(" where FCheckState = 1 and FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
//            buff.append(" and FInOut = 1) sub on tf.fnum = sub.fnum ");
//            buff.append(" where tf.FcheckState = 1 and tf.FTransferDate < ").append(dbl.sqlDate(this.dDate));
//            buff.append(" and tf.FSubTsfTypeCode =").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FP_CHM));
//            buff.append(" ) fer group by fer.FSecurityCode,fer.FPortCode");
//            buff.append(analy1 == true ? " ,fer.FAnalysisCode1":"");
//            buff.append(analy2 == true ? " ,fer.FAnalysisCode2":"");
//            buff.append(" ) d group by d.fsecuritycode,d.fportcode");
//            buff.append(analy1 == true ? " ,d.finvmgrcode":"");
//            buff.append(analy2 == true ? " ,d.fbrokercode":"");
//            buff.append(" ) e on a.fsecuritycode =e.fsecuritycode and a.fportcode = e.fportcode");
//            buff.append(analy1 == true ? " and a.finvmgrcode = e.finvmgrcode":"");
//            buff.append(analy2 == true ? " and a.fbrokercode = e.fbrokercode":"");
//          //【STORY #863 香港、美国股指期权交易区别  期权需求变更】 modify by jiangshichao 2011.06.18 start	  
//           buff.append(" left join ( select fexchangecode,fexchangename from tb_base_exchange where fcheckstate=1 ) f on a.fexchangecode = f.fexchangecode "); 
//          //【STORY #863 香港、美国股指期权交易区别  期权需求变更】 modify by jiangshichao 2011.06.18 end	  
//        }catch(Exception e){
//            throw new YssException("从期货、期权保证金调整表，库存表，期权交易数据表中，获取调整保证金金额，库存数量，当天之前所有交易的保证金总额出错！",e);
//        }
//        return buff.toString();
//    }
    /**
     * 设置资金调拨子表数据出错
     * @param rs ResultSet
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @param type String
     * @return TransferSetBean
     * @throws YssException
     */
//    private TransferSetBean setTransferSetAttr1(ResultSet rs, boolean analy1,
//                                               boolean analy2, boolean analy3,
//                                               String type) throws YssException {
//        TransferSetBean transferset = new TransferSetBean();
//        double dBaseRate = 0; //基础汇率
//        double dPortRate = 0; //组合汇率
//        double money = 0.0; //调拨金额
//        SecurityBean security = null; //证券信息类
//        try {
//        	//delete by jiangshichao 2011.06.22 【STORY #863 香港、美国股指期权交易区别  期权需求变更】
//            //dBaseRate = rs.getDouble("FBaseCuryRate");//赋值基础汇率
//            //dPortRate = rs.getDouble("FPortCuryRate");//赋值组合汇率
//        	
//            security = new SecurityBean();
//            security.setYssPub(pub);
//            security.setSecurityCode(rs.getString("FSecurityCode"));//设置证券代码
//            security.getSetting();
//
//            dBaseRate = this.getSettingOper().getCuryRate(dDate,
//            		security.getTradeCuryCode(),
//                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
//            dPortRate = this.getSettingOper().getCuryRate(dDate,
//                    rs.getString("FPortCury"),
//                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);
//            
//            transferset.setSPortCode(rs.getString("FPortCode")); //设置组合代码
//            if (analy1) {
//                transferset.setSAnalysisCode1(rs.getString("FInvMgrCode")); //先删除投资经理
//            }else{
//                transferset.setSAnalysisCode1(" ");
//            }
//            if (analy2) {
//                transferset.setSAnalysisCode2(rs.getString("FBrokerCode")); //先删除券商代码
//            }else{
//                transferset.setSAnalysisCode2(" ");
//            }
//            if (type.equalsIgnoreCase("kCBailIN")) { //开仓变动保证金流入
//                transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
//                money = rs.getDouble("FBegBailMoney"); //调拨金额
//                transferset.setIInOut(1); //调拨方向 流入
//            } else if (type.equalsIgnoreCase("kCBailOut")) { //开仓初始保证金流出
//                transferset.setSCashAccCode(rs.getString("FBegBailAcctCode"));
//                money = rs.getDouble("FBegBailMoney");
//                transferset.setIInOut( -1);//调拨方向 流出
//            } else if (type.equalsIgnoreCase("PC")) { //平仓的设置，流出。
//                transferset.setSCashAccCode(rs.getString("FChageBailAcctCode")); //变动保证金，流出
//                //--- 【STORY #863 香港、美国股指期权交易区别  期权需求变更】 modify by jiangshichao 2011.06.18 start
//                //估算法需进行修改：金额=调整金额-昨日初始保证金账户余额；
//                money =YssD.sub(rs.getDouble("FChangeMoney"), rs.getDouble("FTotalBailMoney"));
//                transferset.setSDesc("【 "+rs.getString("fexchangename")+" 的保证金调整】");
//                //流出金额=调整保证金金额*昨日库存数量+当天之前所有交易的初始保证金总额
//                //money =YssD.add(YssD.mul(rs.getDouble("FChangeMoney"),Math.abs(rs.getDouble("FStorageAmount"))),rs.getDouble("FTotalBailMoney"));
//                //【STORY #863 香港、美国股指期权交易区别  期权需求变更】 modify by jiangshichao 2011.06.18 end 
//                transferset.setIInOut( -1);//调拨方向 流出
//            } else if (type.equalsIgnoreCase("PCWITHFEE")) { //平仓的设置，流入。
//                transferset.setSCashAccCode(rs.getString("FBegBailAcctCode")); //初始保证金，流入
//              //--- 【STORY #863 香港、美国股指期权交易区别  期权需求变更】 modify by jiangshichao 2011.06.18 start
//                //估算法需进行修改：金额=调整金额-昨日初始保证金账户余额；
//                money =YssD.sub(rs.getDouble("FChangeMoney"), rs.getDouble("FTotalBailMoney"));
//                transferset.setSDesc("【 "+rs.getString("fexchangename")+" 的保证金调整】");
//                //流出金额=调整保证金金额*昨日库存数量+当天之前所有交易的初始保证金总额
//                //money =YssD.add(YssD.mul(rs.getDouble("FChangeMoney"),Math.abs(rs.getDouble("FStorageAmount"))),rs.getDouble("FTotalBailMoney"));
//              //--- 【STORY #863 香港、美国股指期权交易区别  期权需求变更】 modify by jiangshichao 2011.06.18 end 
//                transferset.setIInOut(1);//调拨方向 流入
//                
//            }
//            transferset.setDMoney(money);//设置调拨金额
//            transferset.setDBaseRate(dBaseRate);//设置基础汇率
//            transferset.setDPortRate(dPortRate);//设置组合汇率
//
//            transferset.checkStateId = 1;
//
//        } catch (Exception e) {
//            throw new YssException("设置资金调拨子表数据出错！", e);
//        }
//        return transferset;
//    }

    /**
     * 设置调拨数据
     * @param rs ResultSet
     * @param type String 类型
     * @return TransferBean
     * @throws YssExceptions
     */
    private TransferBean setTransferAttr(ResultSet rs,String type) throws
        YssException {
        TransferBean transfer = new TransferBean();//调拨类型实例化Bean
        try {
            transfer.setDtTransferDate(this.dDate);//调拨日期
            transfer.setDtTransDate(this.dDate);//业务日期
            if (type.equalsIgnoreCase("IN")) { //帐户间的内部流动设置。
                transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);//设置调拨类型--01
                transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP_CHM);//设置调拨子类型,调整保证金类型--0006
            }
            transfer.setFNumType("FPChBailMoney");// 设置编号类型
            transfer.setStrSecurityCode(rs.getString("FSecurityCode")); //证券代码

            transfer.checkStateId = 1;

        } catch (Exception ex) {
            throw new YssException("设置资金调拨数据出错!", ex);
        }
        return transfer;
    }
}
