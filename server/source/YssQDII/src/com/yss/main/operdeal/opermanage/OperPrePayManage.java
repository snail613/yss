package com.yss.main.operdeal.opermanage;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.main.cashmanage.*;
import com.yss.main.operdata.*;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.util.*;

/**
 *
 * <p>Title: 预提待摊费用业务处理类</p>
 *
 * <p>Description: 实际预提待摊费用业务的逻辑处理 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author 潘君方
 * @version 4.1
 */
public class OperPrePayManage
    extends BaseOperManage {
	
	String exceptionInfo = null;//add by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B
	
    public OperPrePayManage() {
    }

    /**
     * 执行待摊费用业务处理
     * MS00017 QDV4.1赢时胜（上海）2009年4月20日17_A
     * created by panjunfang 20090623
     * @throws YssException
     * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage
     *   method
     */
    public void doOpertion() throws YssException {

        try {
            createCashTransfer();   //生成当日待摊资金调拨
            createInvestPay();      //生成对应的运营收支款
            //----add by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B----//
            if(exceptionInfo != null){
            	throw new YssException(exceptionInfo);
            }
            //----add by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B----//
        } catch (Exception ex) {
            throw new YssException("处理待摊业务出现异常！", ex);
        }
    }

    /**
     * 产生一笔流出的资金调拨
     * 调拨类型为03（费用），调拨子类型为03IV（待摊费用），调拨金额为待摊费用设置的待摊金额
     * createCashTransfer
     */
    private void createCashTransfer() throws YssException {
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
        CashTransAdmin cashtransAdmin = null;
        ResultSet rs = null;
        ResultSet rsTransition = null;
        String strSql = null;
        try {
            if (null == cashtransAdmin) {
                cashtransAdmin = new CashTransAdmin(); //生成资金调拨控制类
                cashtransAdmin.setYssPub(pub);
            }
            strSql = buildStrSql();                 //获取资金调拨数据的sql语句
            rs = dbl.queryByPreparedStatement(strSql);         //执行产生资金调拨数据的sql语句
            while (rs.next()) {
                if(rs.getString("FCuryCode") != null && rs.getString("FCuryCode").trim().length() > 0){
                    //国内业务中对历史数据的处理：国内业务之前版本中待摊业务通过手动录入资金调拨，而在国内版本中待摊业务为自动处理，自动产生资金调拨，应避免因客户对历史数据重复做帐而再次产生资金调拨，导致对同一待摊业务产生两次资金调拨的问题。
                    //国内业务中将投资运营收支界面中的币种代码替换为现金账户，故可通过判断币种代码字段是否有值来确定该笔待摊业务是否为历史数据，如果为历史数据，则给出提示并不再往下执行待摊业务处理
                	//----delete by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B----//
//                    throw new YssException("对不起，投资运营收支【" + rs.getString("FIVPayCatCode") + "," + rs.getString("FPortCode") + "," + rs.getString("FStartDate") +
//                                           "】已手动进行过待摊业务处理，请不要重复处理！");
                	//----delete by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B----//
                	//----add by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B----//
                    exceptionInfo = "对不起，投资运营收支【" + rs.getString("FIVPayCatCode") + "," + rs.getString("FPortCode") + "," + 
                    rs.getString("FStartDate") +"】已手动进行过待摊业务处理，请不要重复处理！";
                    continue;
                    //----add by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B----//
                }
                if (rs.getString("FCASHACCCODE") == null) {
                    throw new YssException("对不起，运营收支品种【" + rs.getString("FIVPayCatName") + "】的现金账户未设置!" +
                                           "\n请到 业务参数-投资运营收支设置 中重新设置重新设置对应的现金账户，谢谢！");
                }
                transfer = setTransfer(rs);         //获取资金调拨数据
                transferSet = setTransferSet(rs);   //获取资金调拨子数据
                subTransfer = new ArrayList();      //实例化放置资金调拨子数据的容器
                subTransfer.add(transferSet);       //将资金调拨子数据放入容器
                transfer.setSubTrans(subTransfer);  //将子数据放入资金调拨中
                cashtransAdmin.addList(transfer);
            }
            //added by liubo.Story #2139
            //获取启用了预提转待摊，且所选择日期大于等于转换日期的预提项，生成一笔调拨子类型为03IV，金额为该预提项实付金额的资金调拨数据
            //===================================
            strSql = buildSqlStrOfTransition();
            rsTransition = dbl.queryByPreparedStatement(strSql);         
            while (rsTransition.next()) {
            	//20120326 deleted by liubo.Bug #3995
            	//=================================
//                if(rsTransition.getString("FCuryCode") != null && rsTransition.getString("FCuryCode").trim().length() > 0){
//                    exceptionInfo = "对不起，投资运营收支【" + rsTransition.getString("FIVPayCatCode") + "," + rsTransition.getString("FPortCode") + "," + 
//                    rsTransition.getString("FStartDate") +"】已手动进行过待摊业务处理，请不要重复处理！";
//                    continue;
//                }
            	//==============end===================
            	
                if (rsTransition.getString("FCASHACCCODE") == null) {
                    throw new YssException("对不起，运营收支品种【" + rsTransition.getString("FIVPayCatName") + "】的现金账户未设置!" +
                                           "\n请到 业务参数-投资运营收支设置 中重新设置重新设置对应的现金账户，谢谢！");
                }
                transfer = setTransferForTransition(rsTransition);         //获取资金调拨数据
                transferSet = setTransferSetForTransition(rsTransition);   //获取资金调拨子数据
                subTransfer = new ArrayList();      //实例化放置资金调拨子数据的容器
                subTransfer.add(transferSet);       //将资金调拨子数据放入容器
                transfer.setSubTrans(subTransfer);  //将子数据放入资金调拨中
                cashtransAdmin.addList(transfer);
            }
            //===============end====================
            
            cashtransAdmin.insert("",dDate,dDate,YssOperCons.YSS_ZJDBLX_Fee,YssOperCons.YSS_ZJDBZLX_IV_Fee,"","","","","","PrePaid","",1,"",sPortCode,-1,"","","",true,"");//插入资金调拨，传入调拨日期、业务日期、调拨类型、调拨子类型、关联编号类型、组合代码和自动录入标志来删除原有调拨数据
         //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(cashtransAdmin.getAddList()==null || cashtransAdmin.getAddList().size()==0){
    			this.sMsg="        当日无业务";
    		}
		} catch (Exception ex) {
            throw new YssException("生成待摊的资金调拨出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs,rsTransition);
        }

    }

    /**
     * 产生一笔相应的运营应收应付数据，业务类型为‘应收’，业务子类型为‘应收运营收支款’
     * @param rs ResultSet
     * @throws YssException
     */
    private void createInvestPay() throws YssException {
        InvestPayRecBean invest = null;
        InvestPayAdimin investpayAdmin = null;
        ResultSet rs = null;
        String strIVPayCatCodes = "";
        String strSql = "";
        try {
            if (investpayAdmin == null) {
                investpayAdmin = new InvestPayAdimin();
                investpayAdmin.setYssPub(pub);
            }
            strSql = buildStrSql();         //获取资金调拨数据的sql语句
            rs = dbl.queryByPreparedStatement(strSql); //执行产生资金调拨数据的sql语句
            while (rs.next()) {
            	//----add by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B----//
                //国内业务中对历史数据的处理：国内业务之前版本中待摊业务通过手动录入资金调拨，而在国内版本中待摊业务为自动处理，自动产生资金调拨，应避免因客户对历史数据重复做帐而再次产生资金调拨，导致对同一待摊业务产生两次资金调拨的问题。
                //国内业务中将投资运营收支界面中的币种代码替换为现金账户，故可通过判断币种代码字段是否有值来确定该笔待摊业务是否为历史数据，如果为历史数据，则给出提示并不再往下执行待摊业务处理
            	if(rs.getString("FCuryCode") != null && rs.getString("FCuryCode").trim().length() > 0){
            		continue;
            	}
            	//----add by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B----//
                invest = setInvest(rs);
                strIVPayCatCodes += rs.getString("FIVPayCatCode") + ",";// #409,QDV4国泰2010年11月11日01_B 系统无法处理多比预提待摊的数据 panjunfang modify 20101113
                investpayAdmin.addList(invest);
            }
            if (strIVPayCatCodes.length() > 1) {
                strIVPayCatCodes = strIVPayCatCodes.substring(0, strIVPayCatCodes.length() - 1);
            }
            investpayAdmin.insert(dDate, dDate,YssOperCons.YSS_ZJDBLX_Rec,
                                  YssOperCons.YSS_ZJDBZLX_IV_Rec, strIVPayCatCodes,sPortCode ,
                                  "", 0);
            
            createInvestPayForFTransition();		//added by liubo.Story #2139
            
        } catch (Exception ex) {
            throw new YssException("生成运营收支款出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //added by liubo.Story #2139
    //获取启用了预提转待摊，且所选择日期大于等于转换日期的预提项，生成两笔应收应付
    //生成一笔业务子类型为03IV，金额为‘开始日期’至‘转换日期’前一天所计提的07IV之和，业务日期为转换日期的运营应收应付，用于冲减该运营费用由‘开始日期’至‘转换日期’前一天所计提的07IV数据
    //(考虑到该费用在‘转换日期’前在｛收益支付｝中有可能手工支付过，因此计算原币金额时，直接取运营库存中在‘转换日期’前一天的库存。（即Tb_XXX_Stock_Invest表，FBAL字段）。)
    //生成一笔业务子类型为06IV，金额为：实付金额-运营库存中在‘转换日期’前一天的库存，业务日期为转换日期的运营应收应付
    //最后再删除掉该运营费用在运营应收应付数据中‘转换日期’至‘结束日期’07IV类型的应付数据，防止用户误操作
    //=================================
    private void createInvestPayForFTransition() throws YssException
    {
    	InvestPayRecBean invest = null;
        InvestPayAdimin investpayAdmin = null;
        ResultSet rs = null;
        ResultSet rsTransition = null;
        String strIVPayCatCodes = "";
        String strSql = "";
        try {
            if (investpayAdmin == null) {
                investpayAdmin = new InvestPayAdimin();
                investpayAdmin.setYssPub(pub);
            }
            strSql = buildSqlStrOfTransition();
            rs = dbl.queryByPreparedStatement(strSql);            
            while (rs.next()) {
            	
            	if(rs.getString("FCuryCode") != null && rs.getString("FCuryCode").trim().length() > 0){
            		continue;
            	}
            	
            	strSql = "select FBal as Total from " + pub.yssGetTableName("Tb_Stock_Invest") +
            			" where FIVPayCatCode = " + dbl.sqlString(rs.getString("FIVPayCatCode")) +" and FPortCode = " + dbl.sqlString(rs.getString("FPortCode")) + 
            			" and FStorageDate = " + dbl.sqlDate(YssFun.addDate(rs.getDate("FTransitionDate"), -1, Calendar.DAY_OF_MONTH));
            	
            	rsTransition = dbl.queryByPreparedStatement(strSql);
            	
            	while(rsTransition.next())
            	{
	                invest = setInvestForFTransition(rs,"06","06IV", YssD.sub(rs.getDouble("FPaidIn"), rsTransition.getDouble("Total")));
	                investpayAdmin.addList(invest);
		            invest = setInvestForFTransition(rs,"03","03IV",rsTransition.getDouble("Total"));
		            investpayAdmin.addList(invest);
		                
            	}
	            dbl.closeResultSetFinal(rsTransition);

	            strIVPayCatCodes += rs.getString("FIVPayCatCode") + ",";
	            
	            investpayAdmin.delete(rs.getDate("FTransitionDate"), rs.getDate("FACEndDate"), "07", "07IV", rs.getString("FIVPayCatCode"), rs.getString("FPortCode"), "", rs.getString("FAnalysisCode1"), rs.getString("FAnalysisCode2"), 0, "","");
            }
            if (strIVPayCatCodes.length() > 1) {
                strIVPayCatCodes = strIVPayCatCodes.substring(0, strIVPayCatCodes.length() - 1);
            }
            
            if (strIVPayCatCodes != null)
            {
	            if (!strIVPayCatCodes.trim().equals(""))
	            {
	
	                investpayAdmin.insert(dDate, dDate,"03,06",
	                        "03IV,06IV", strIVPayCatCodes,sPortCode ,
	                        "", 0);
	            }
            }
            
            
        }
        catch (Exception ex) {
            throw new YssException("生成运营收支款出现异常！", ex);
        } 
        finally {
            dbl.closeResultSetFinal(rs,rsTransition);
        }
    }
    //==============end===================

    /**
     * setInvest
     *
     * @param rs ResultSet
     * @return InvestPayRecBean
     */
    private InvestPayRecBean setInvest(ResultSet rs) throws YssException {
        InvestPayRecBean invest = new InvestPayRecBean();
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        double Money = 0;
        double BaseMoney = 0;
        double PortMoney = 0;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        try {
            BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取待摊费用支付当日的基础汇率
                rs.getString("FCurrencyCode"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCurrencyCode"),
                                      rs.getString("FPortCode"));
            PortCuryRate = rateOper.getDPortRate(); //获取待摊费用支付当日的组合汇率

            Money = rs.getDouble("FACTotalMoney"); //原币金额
            BaseMoney = this.getSettingOper().calBaseMoney(Money, BaseCuryRate); //计算基础货币金额
            PortMoney = this.getSettingOper().calPortMoney(Money, BaseCuryRate,
                PortCuryRate,
                rs.getString("FCurrencyCode"),
                dDate,
                rs.getString("FPortCode")); //计算组合货币金额
            invest.setBaseCuryRate(BaseCuryRate);
            invest.setPortCuryRate(PortCuryRate);
            invest.setMoney(Money);
            invest.setBaseCuryMoney(BaseMoney);
            invest.setPortCuryMoney(PortMoney);
            invest.setFIVPayCatCode(rs.getString("FIVPayCatCode"));
            invest.setPortCode(rs.getString("FPortCode"));
            invest.setAnalysisCode1(rs.getString("FAnalysisCode1"));
            invest.setTradeDate(rs.getDate("FACBeginDate"));
            invest.setTsftTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
            invest.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_IV_Rec);
            invest.setCuryCode(rs.getString("FCurrencyCode"));
            invest.setCheckState(1); //设置为审核状态，默认情况下为为审核状态
        } catch (Exception e) {
            throw new YssException("设置运营应收应付数据时出现异常！", e);
        }
        return invest;
    }

    /**
     * added by liubo.Story #2139.setInvestForFTransition
     *
     * @param rs ResultSet
     * String sTsftTypeCode		业务类型
     * String sSubTsfTypeCode	业务子类型
     * double dbTotal 			实付金额-运营库存中在‘转换日期’前一天的库存
     * @return InvestPayRecBean
     */
    private InvestPayRecBean setInvestForFTransition(ResultSet rs ,String sTsftTypeCode, String sSubTsfTypeCode, double dbTotal) throws YssException {
        InvestPayRecBean invest = new InvestPayRecBean();
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        double Money = 0;
        double BaseMoney = 0;
        double PortMoney = 0;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        try {
            BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取待摊费用支付当日的基础汇率
                rs.getString("FCurrencyCode"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCurrencyCode"),
                                      rs.getString("FPortCode"));
            PortCuryRate = rateOper.getDPortRate(); //获取待摊费用支付当日的组合汇率

            Money = dbTotal; //原币金额
            BaseMoney = this.getSettingOper().calBaseMoney(Money, BaseCuryRate); //计算基础货币金额
            PortMoney = this.getSettingOper().calPortMoney(Money, BaseCuryRate,
                PortCuryRate,
                rs.getString("FCurrencyCode"),
                dDate,
                rs.getString("FPortCode")); //计算组合货币金额
            invest.setBaseCuryRate(BaseCuryRate);
            invest.setPortCuryRate(PortCuryRate);
            invest.setMoney(Money);
            invest.setBaseCuryMoney(BaseMoney);
            invest.setPortCuryMoney(PortMoney);
            invest.setFIVPayCatCode(rs.getString("FIVPayCatCode"));
            invest.setPortCode(rs.getString("FPortCode"));
            invest.setAnalysisCode1(rs.getString("FAnalysisCode1"));
            invest.setTradeDate(rs.getDate("FTransitionDate"));
            invest.setTsftTypeCode(sTsftTypeCode);
            invest.setSubTsfTypeCode(sSubTsfTypeCode);
            invest.setCuryCode(rs.getString("FCurrencyCode"));
            invest.setCheckState(1); //设置为审核状态，默认情况下为为审核状态
        } catch (Exception e) {
            throw new YssException("设置运营应收应付数据时出现异常！", e);
        }
        return invest;
    }
    //===============end==================

    /**
     * setTransferSet
     *
     * @param rs ResultSet
     * @return TransferSetBean
     */
    private TransferSetBean setTransferSet(ResultSet rs) throws YssException {
        TransferSetBean transferSet = null;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        try {
            transferSet = new TransferSetBean();
            double dBaseRate = 1;
            double dPortRate = 1;

            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("FCurrencyCode"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE); //获取业务当天的基础汇率

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCurrencyCode"),
                                      rs.getString("FPortCode"));
            dPortRate = rateOper.getDPortRate(); //获取业务当天的组合汇率

            transferSet.setIInOut( -1); //流出
            transferSet.setSPortCode(rs.getString("FPortCode"));
            transferSet.setSAnalysisCode1(null == rs.getString("FAnalysisCode1") ? "" :
                                          rs.getString("FAnalysisCode1"));
            transferSet.setSAnalysisCode2(null == rs.getString("FAnalysisCode2") ? "" :
                                          rs.getString("FAnalysisCode2"));
            transferSet.setSAnalysisCode3(null == rs.getString("FAnalysisCode3") ? "" :
                                          rs.getString("FAnalysisCode3"));
            transferSet.setSCashAccCode(rs.getString("FCASHACCCODE")); //设置现金账户
            transferSet.setDMoney(rs.getDouble("FACTotalMoney")); //设置金额
            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
        return transferSet; //返回资金调拨子数据
    }
    
    //added by liubo.Story #2139
    //================================
    private TransferSetBean setTransferSetForTransition(ResultSet rs) throws YssException {
        TransferSetBean transferSet = null;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        try {
            transferSet = new TransferSetBean();
            double dBaseRate = 1;
            double dPortRate = 1;

            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("FCurrencyCode"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE); //获取业务当天的基础汇率

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCurrencyCode"),
                                      rs.getString("FPortCode"));
            dPortRate = rateOper.getDPortRate(); //获取业务当天的组合汇率

            transferSet.setIInOut( -1); //流出
            transferSet.setSPortCode(rs.getString("FPortCode"));
            transferSet.setSAnalysisCode1(null == rs.getString("FAnalysisCode1") ? "" :
                                          rs.getString("FAnalysisCode1"));
            transferSet.setSAnalysisCode2(null == rs.getString("FAnalysisCode2") ? "" :
                                          rs.getString("FAnalysisCode2"));
            transferSet.setSAnalysisCode3(null == rs.getString("FAnalysisCode3") ? "" :
                                          rs.getString("FAnalysisCode3"));
            transferSet.setSCashAccCode(rs.getString("FCASHACCCODE")); //设置现金账户
            transferSet.setDMoney(rs.getDouble("FPaidIn")); //设置金额
            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
        return transferSet; //返回资金调拨子数据
    }
    //=============end===================

    /**
     * setTransfer
     *
     * @param rs ResultSet
     * @return TransferBean
     */
    private TransferBean setTransfer(ResultSet rs) throws YssException {
        TransferBean transfer = null;
        try {
            transfer = new TransferBean();
            transfer.setDtTransDate(rs.getDate("FACBeginDate")); //业务日期为运营收支品种开始日期
            transfer.setDtTransferDate(rs.getDate("FACBeginDate")); //调拨日期为运营收支品种开始日期
            transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);
            transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_IV_Fee);
            transfer.setFNumType("PrePaid"); //设置关联编号类型为待摊
            transfer.checkStateId = 1;
            transfer.setDataSource(1);
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
        return transfer; //返回资金调拨数据
    }
    
    //added by liubo.Story #2139
    //====================================
    private TransferBean setTransferForTransition(ResultSet rs) throws YssException {
        TransferBean transfer = null;
        try {
            transfer = new TransferBean();
            transfer.setDtTransDate(rs.getDate("FTransitionDate")); //业务日期为转换日期
            transfer.setDtTransferDate(rs.getDate("FTransitionDate")); //调拨日期为转换日期
            transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);
            transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_IV_Fee);
            transfer.setFNumType("PrePaid"); //设置关联编号类型为待摊
            transfer.checkStateId = 1;
            transfer.setDataSource(1);
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
        return transfer; //返回资金调拨数据
    }

    /**
     * buildCreateCashTransferSql
     *
     * @return String
     */
    private String buildStrSql() throws YssException {
        String strSql = "";
        //获取待摊开始日期为当前业务日期下的所有待摊投资运营收支信息
        strSql = "SELECT y.*,CA.FCuryCode as FCurrencyCode FROM (" +
            " SELECT IP.*,IP1.* FROM ( SELECT FIVPayCatCode as IPfivpaycatcode ,FPortCode as IPFPortCode,max(fstartdate) as IPFstartdate from " + pub.yssGetTableName("Tb_Para_InvestPay") +
            " GROUP BY FIVPayCatCode,FPortCode) IP JOIN ( " +
            " SELECT FIVPayCatCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCuryCode," +
            " FCashAccCode,FACRoundCode,FACBeginDate,FACEndDate,FExpirDate,FACTotalMoney,MAX(FStartDate) AS FStartDate " +
            " FROM " + pub.yssGetTableName("Tb_Para_InvestPay") +
            " GROUP BY FIVPayCatCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCuryCode,FCashAccCode,FACRoundCode,FACBeginDate,FACEndDate,FExpirDate,FACTotalMoney)" +
            " IP1 ON IP.IPfivpaycatcode = IP1.FIVPayCatCode and IP.IPFPortCode = IP1.FPortCode and IP.IPFstartdate = IP1.FStartDate JOIN (SELECT FIVPayCatCode as bFIVPayCatCode,FPortCode as bFPortCode " +
            " FROM " + pub.yssGetTableName("Tb_Para_InvestPay") +
            " WHERE FCHECKSTATE = 1 AND FACBeginDate = " + dbl.sqlDate(dDate) +
            " and FPortCode = " + dbl.sqlString(this.sPortCode) + " ) IP2 " +
            " on IP1.FIVPayCatCode = IP2.bFIVPayCatCode  and IP1.FPortCode = IP2.bFPortCode" +
            " join (select * from Tb_Base_InvestPayCat where FIVType in ('deferredFee') and FCHECKSTATE = 1" +
            " ) IPC on IPC.FIVPayCatCode = IP1.FIVPayCatCode" +
            //// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            /* 
            
        
           pub.yssGetTableName("Tb_Para_CashAccount") + " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
           */ 
            " ) y left join (select FCashAccCode,FCuryCode, FStartDate from " +
            pub.yssGetTableName("Tb_Para_CashAccount") + " where  " +
            
            //end by lidaolong
            "  FCheckState = 1 and FState =0 ) CA on CA.FCashAccCode = y.FCashAccCode";

        return strSql;
    }
    
    //added by liubo.Story #2139
    //==================================
    private String buildSqlStrOfTransition() throws YssException {
        String strSql = "";
        //获取待摊开始日期为当前业务日期下的所有待摊投资运营收支信息
        strSql = "SELECT y.*,CA.FCuryCode as FCurrencyCode FROM (" +
            " SELECT IP.*,IP1.* FROM ( SELECT FIVPayCatCode as IPfivpaycatcode ,FPortCode as IPFPortCode,max(fstartdate) as IPFstartdate from " + pub.yssGetTableName("Tb_Para_InvestPay") +
            " GROUP BY FIVPayCatCode,FPortCode) IP JOIN ( " +
            " SELECT FIVPayCatCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCuryCode," +
            " FCashAccCode,FACRoundCode,FACBeginDate,FACEndDate,FExpirDate,FACTotalMoney,MAX(FStartDate) AS FStartDate ,FPaidIn,FTransitionDate" +
            " FROM " + pub.yssGetTableName("Tb_Para_InvestPay") +
            " GROUP BY FIVPayCatCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCuryCode,FCashAccCode,FACRoundCode,FACBeginDate,FACEndDate,FExpirDate,FACTotalMoney,FPaidIn ,FTransitionDate)" +
            " IP1 ON IP.IPfivpaycatcode = IP1.FIVPayCatCode and IP.IPFPortCode = IP1.FPortCode and IP.IPFstartdate = IP1.FStartDate JOIN (SELECT FIVPayCatCode as bFIVPayCatCode,FPortCode as bFPortCode " +
            " FROM " + pub.yssGetTableName("Tb_Para_InvestPay") +
            " WHERE FCHECKSTATE = 1 AND FTransition = '1' AND FTransitionDate = " + dbl.sqlDate(dDate) +
            " and FPortCode = " + dbl.sqlString(this.sPortCode) + " ) IP2 " +
            " on IP1.FIVPayCatCode = IP2.bFIVPayCatCode  and IP1.FPortCode = IP2.bFPortCode" +
            " join (select * from Tb_Base_InvestPayCat where FIVType in ('accruedFee') and FCHECKSTATE = 1" +
            " ) IPC on IPC.FIVPayCatCode = IP1.FIVPayCatCode" +
            //// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            /* 
            
        
           pub.yssGetTableName("Tb_Para_CashAccount") + " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
           */ 
            " ) y left join (select FCashAccCode,FCuryCode, FStartDate from " +
            pub.yssGetTableName("Tb_Para_CashAccount") + " where  " +
            
            //end by lidaolong
            "  FCheckState = 1 and FState =0 ) CA on CA.FCashAccCode = y.FCashAccCode";

        return strSql;
    }
    //================end==================

    /**
     * 获取现金帐户
     * @throws YssException
     * @return String
     */
    public String getCashAcc(ResultSet rs) throws YssException {
        CashAccountBean caBean = null;
        CashAccLinkBean cashAccLink = null;
        try {
            cashAccLink = new CashAccLinkBean();
            cashAccLink.setYssPub(pub);
            cashAccLink.setStrPortCode(rs.getString("FPortCode"));
            cashAccLink.setCuryCode(rs.getString("FCuryCode"));
            caBean = (CashAccountBean) cashAccLink.getSetting();
            if (caBean != null) {
                return caBean.getStrCashAcctCode();
            } else {
                return " ";
            }
        } catch (Exception e) {
            throw new YssException("获取现金帐户出错",e);
        }
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
}
