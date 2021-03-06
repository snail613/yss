package com.yss.main.operdeal.opermanage;

import java.util.*;
import com.yss.util.*;

import java.sql.*;

import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.manager.CashTransAdmin;
import com.yss.commeach.EachRateOper;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.operdata.SecIntegratedBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.innerparams.InnerPubParamsWithPurchase;
import com.yss.manager.CashPayRecAdmin;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.income.stat.StatPurchaseIns;
import com.yss.manager.SecRecPayAdmin;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.parasetting.SecurityBean;

/**
 * <p>Title: </p>
 *
 * <p>Description: 对资金调拨的业务处理</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author sj
 * @version 1.0
 * MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
 */
public class OperPurchaseManage
    extends BaseOperManage {
    EachRateOper rateOper = null;
    InnerPubParamsWithPurchase purchaseParams = null;
    public OperPurchaseManage() {
    }

    /**
     * 执行业务处理(操作资金调拨)
     *
     * @throws YssException
     * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage
     *   method
     */
    public void doOpertion() throws YssException {
        getPurchaseParams();    //设置组合通用参数

        createIntegrated();     //生成证券变更数据（综合业务）

        createCashTransfer();   //生成当日的资金调拨

        createPurchaseDurFromDurTo(); //生成回购的应收应付数据

        purchaseIncome(dDate);  //生成回购的收入数据
        
        createFreezeSecStock();
    }

    /**
     * 初始化信息(回购)
     *
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage
     *   method
     */
    public void initOperManageInfo(java.util.Date dDate, String portCode) throws
        YssException {
        this.dDate = dDate;         //调拨日期
        this.sPortCode = portCode;  //组合
    }

    /**
     * 新建回购的资金调拨
     * @throws YssException
     */
    private void createCashTransfer() throws YssException {
        String createCashTransferSql = null;
        CashTransAdmin cashtransAdmin = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            cashtransAdmin = new CashTransAdmin(); //生成资金调拨控制类
            cashtransAdmin.setYssPub(pub);

            rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);

            createCashTransferSql = buildCreateCashTransferSqlWithPurchase(); //获取回购业务中的资金调拨数据的sql语句
            createCashTransferWithSqlParam(cashtransAdmin, createCashTransferSql);
            
            if(cashtransAdmin.getAddList().size()>0){
            	this.sMsg="";
            }
            
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer"));      //lock table
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));   //lock table
            //------ modify by wangzuochun 2010.11.23 BUG #493 回购业务处理未考虑多组合，报错
            cashtransAdmin.insert(dDate, "standardRe", this.sPortCode, ""); //插入资金调拨,以调拨日期和关联编号类型,自动生成的来删除已有资金调拨
            //------------------------- BUG #493 -----------------------//
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("生成回购的资金调拨出现异常！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 生成回购资金调拨数据
     * @param cashtransAdmin CashTransAdmin
     * @param sql String 获取资金调拨的sql语句
     * @throws YssException
     */
    private void createCashTransferWithSqlParam(CashTransAdmin cashtransAdmin,
                                                String sql) throws YssException {
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ResultSet rs = null;
        ArrayList subTransfer = null;
        try {
            rs = dbl.queryByPreparedStatement(sql);            //执行产生资金调拨数据的sql语句
            while (rs.next()) {
                transfer = setTransfer(rs);         //获取资金调拨数据
                transferSet = setTransferSet(rs);   //获取资金调拨子数据
                subTransfer = new ArrayList();      //实例化放置资金调拨子数据的容器
                subTransfer.add(transferSet);       //将资金调拨子数据放入容器
                transfer.setSubTrans(subTransfer);  //将子数据放入资金调拨中
                cashtransAdmin.addList(transfer);
            }
        } catch (Exception e) {
            throw new YssException("生成回购资金调拨数据出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 业务结算
     * @throws YssException
     */
    private void settlementTrade() throws YssException {
        String sql = null;
        try {
            sql = buildSettlementSubtrade();
            dbl.executeSql(sql);
        } catch (Exception e) {
            throw new YssException("交易结算出现异常！", e);
        }
    }

    /**
     * 拼装结算业务资料的sql语句
     * @return String
     * @throws YssException
     */
    private String buildSettlementSubtrade() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append("UPDATE ");
        buf.append(pub.yssGetTableName("TB_DATA_SUBTRADE"));
        buf.append(" SET FSettleState = 1 ");
        buf.append(",FCheckTime = ");
        buf.append(dbl.sqlString(YssFun.formatDatetime(new java.util.Date())));
        buf.append(" WHERE  FTradeTypeCode in(");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_ZRE)).append(","); //正回购
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_NRE)); //逆回购
        buf.append(") AND FCHECKSTATE = 1 AND FFactSettleDate = ");
        buf.append(dbl.sqlDate(dDate));
        return buf.toString();
    }

    /**
     * 拼装获取业务资料中的交易所回购的资金调拨的sql语句
     * @return String
     * @throws YssException
     */
    private String buildCreateCashTransferSqlWithSubtrade() throws YssException {
        StringBuffer buf = null;
        buf = new StringBuffer();
        buf.append("SELECT PURCHASE.*,CASHACC.FCURYCODE AS FCURYCODE FROM ");
        buf.append("(SELECT ");
        buf.append("FNum,");
        buf.append("FSECURITYCODE,");
        buf.append("FPORTCODE,");
        buf.append("FTRADETYPECODE,");
        buf.append("FCASHACCCODE,");
        buf.append("FBARGAINDATE AS FLOGICEDATE,");
        buf.append("FSETTLEDATE AS FDEALDATE,");
        buf.append("FPORTCURYRATE,");
        buf.append("FBASECURYRATE,");
        buf.append("FTRADEAMOUNT,");
        buf.append("FTRADEMONEY,");
        buf.append("FTotalCost,");
        buf.append("FAccruedinterest as FPURCHASEGAIN,"); //将应计利息设为 固定的回购利息来处理

        buf.append("'head' as FType"); //设置回购的首期还是到期的类型，head -- 首期、mature -- 到期

        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("TB_DATA_SUBTRADE")); //获取回购的所有信息
        buf.append(" WHERE  FTradeTypeCode in(");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_ZRE)).append(","); //正回购
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_NRE)).append(","); //逆回购
        //add by zhouwei 20120523 bug 4284 买断式回购
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_REMR)).append(",");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_REMC));
        buf.append(") AND FCHECKSTATE = 1 AND FFactSettleDate = ");
        buf.append(dbl.sqlDate(dDate));

        buf.append(" union all ");

        buf.append("SELECT ");
        buf.append("FNum,");
        buf.append("FSECURITYCODE,");
        buf.append("FPORTCODE,");
        buf.append("FTRADETYPECODE,");
        buf.append("FCASHACCCODE,");
        buf.append("FMATUREDATE AS FLOGICEDATE,");
        buf.append("FMATURESETTLEDATE AS FDEALDATE,");
        buf.append("FPORTCURYRATE,");
        buf.append("FBASECURYRATE,");
        buf.append("FTRADEAMOUNT,");
        buf.append("FTRADEMONEY,");
        buf.append("FTotalCost,");
        buf.append("FAccruedinterest as FPURCHASEGAIN,"); //将应计利息设为 固定的回购利息来处理

        buf.append("'mature' as FType"); //设置回购的首期还是到期的类型，head -- 首期、mature -- 到期

        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("TB_DATA_SUBTRADE")); //获取业务资料中回购的所有信息
        buf.append(" WHERE  FTradeTypeCode in(");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_ZRE)).append(","); //正回购
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_NRE)).append(","); //逆回购
        //add by zhouwei 20120523 bug 4284 买断式回购
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_REMR)).append(",");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_REMC));
        buf.append(") AND FCHECKSTATE = 1 AND FMatureSettleDate = ");
        buf.append(dbl.sqlDate(dDate));

        buf.append(") PURCHASE ");
        buf.append(" LEFT JOIN (SELECT FCASHACCCODE,FCURYCODE FROM "); //关联账户为了获取交易货币
        buf.append(pub.yssGetTableName("TB_PARA_CASHACCOUNT"));
        buf.append(" WHERE FCHECKSTATE = 1) CASHACC ");
        buf.append(" ON PURCHASE.FCASHACCCODE = CASHACC.FCASHACCCODE");
        return buf.toString();
    }

    /**
     * 拼装获取回购资金调拨数据的sql语句
     * @return String 完成拼装的sql语句
     * @throws YssException
     */
    private String buildCreateCashTransferSqlWithPurchase() throws YssException {
        StringBuffer buf = null;
        buf = new StringBuffer();
        buf.append("SELECT PURCHASE.*,CASHACC.FCURYCODE AS FCURYCODE FROM ");
        buf.append("(SELECT ");
        buf.append("FNum,");
        buf.append("FSECURITYCODE,");
        buf.append("FPORTCODE,");
        buf.append("FTRADETYPECODE,");
        buf.append("FCASHACCCODE,");
        buf.append("FBARGAINDATE AS FLOGICEDATE,"); // 统一将业务日期设置为此字段
        buf.append("FSETTLEDATE AS FDEALDATE,");    //统一将调拨日期设置为此字段
        buf.append("FPORTCURYRATE,");
        buf.append("FBASECURYRATE,");
        buf.append("FTRADEMONEY,");
        buf.append("FTotalCost,");
        buf.append("FPURCHASEGAIN,");
        
        buf.append("FTradeHandleFee,");
        buf.append("FBankHandleFee,");
        buf.append("FSetServiceFee,");
        buf.append("FInvmgrCode,");//modify by nimengjing 2010.11.30 bug #508 银行间回购业务处理产生的资金调拨数据不能带出投资经理 

        buf.append("'head' as FType"); //设置回购的首期还是到期的类型，head -- 首期、mature -- 到期

        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("TB_DATA_PURCHASE")); //获取回购的所有信息
        buf.append(" WHERE FCHECKSTATE = 1 AND FBARGAINDATE = ");//调整为交易日期
        buf.append(dbl.sqlDate(dDate));
        //------ add by wangzuochun 2010.11.23 BUG #493 回购业务处理未考虑多组合，报错-------//
        buf.append(" and FPortCode = ");
        buf.append(dbl.sqlString(this.sPortCode));
        //----------------BUG #493-----------------//

        buf.append(" union all ");

        buf.append("SELECT ");
        buf.append("FNum,");
        buf.append("FSECURITYCODE,");
        buf.append("FPORTCODE,");
        buf.append("FTRADETYPECODE,");
        buf.append("FCASHACCCODE,");
        buf.append("FMATUREDATE AS FLOGICEDATE,");      // 统一将业务日期设置为此字段
        buf.append("FMATURESETTLEDATE AS FDEALDATE,");  //统一将调拨日期设置为此字段
        buf.append("FPORTCURYRATE,");
        buf.append("FBASECURYRATE,");
        buf.append("FTRADEMONEY,");
        buf.append("FTotalCost,");
        buf.append("FPURCHASEGAIN,");
        
        buf.append("FTradeHandleFee,");
        buf.append("FBankHandleFee,");
        buf.append("FSetServiceFee,");
        buf.append("FInvmgrCode,");//modify by nimengjing 2010.11.30 bug #508 银行间回购业务处理产生的资金调拨数据不能带出投资经理 

        buf.append("'mature' as FType"); //设置回购的首期还是到期的类型，head -- 首期、mature -- 到期

        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("TB_DATA_PURCHASE"));            //获取回购的所有信息
        buf.append(" WHERE FCHECKSTATE = 1 AND FMATUREDATE = ");  //****这里需要修改为到期日期(再次进行修改)
        buf.append(dbl.sqlDate(dDate));
        //------ add by wangzuochun 2010.11.23 BUG #493 回购业务处理未考虑多组合，报错-------//
        buf.append(" and FPortCode = ");
        buf.append(dbl.sqlString(this.sPortCode));
        //------------------BUG #493-----------------//

        buf.append(") PURCHASE ");
        //begin by zhouxiang MS01663    启用日期不同的现金账户，引起资金调拨界面和应收应付界面产生重复数据    QDV4赢时胜(上海开发部)2010年08月30日01_B    
       
     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码

        
        buf.append(" LEFT JOIN (SELECT FCASHACCCODE,FCURYCODE FROM ");  //关联账户为了获取交易货币
        buf.append(pub.yssGetTableName("TB_PARA_CASHACCOUNT "));
 
        //end by lidaolong
        //end-- by zhouxiang MS01663    启用日期不同的现金账户，引起资金调拨界面和应收应付界面产生重复数据    QDV4赢时胜(上海开发部)2010年08月30日01_B    
        buf.append(" WHERE FCHECKSTATE = 1) CASHACC ");
        buf.append(" ON PURCHASE.FCASHACCCODE = CASHACC.FCASHACCCODE");
        return buf.toString();
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
            transfer.setDtTransDate(rs.getDate("FLOGICEDATE"));     //成交日期为业务日期
            transfer.setDtTransferDate(rs.getDate("FDEALDATE"));    //结算日期为调拨日期
            transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Cost);
            transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Cost + "RE");
            transfer.setFNumType("standardRe");             //编号类型为标准回购
            transfer.setFRelaNum(rs.getString("FNum"));     //关联编号
            transfer.setSrcCashAccCode("FCashAccCode");
            transfer.checkStateId = 1;
            transfer.setDataSource(1);
            //fanghaoln 20100512 fanghaoln  MS01125 QDV4赢时胜上海2010年04月27日01_AB   
            if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("24")){
            	transfer.setStrAttrClsCode("SellRepo");
            	transfer.setStrAttrClsName("正回购");
            }else if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("25")){
            	transfer.setStrAttrClsCode("AntiRepo");
            	transfer.setStrAttrClsName("逆回购");
            }
            //----------------------------------------end --------MS01125---------------------------
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
        return transfer; //返回资金调拨数据
    }

    /**
     * 设置资金调拨子数据
     * @param rs ResultSet 携带资金调拨子数据的记录集
     * @return TransferSetBean 资金调拨子数据
     * @throws YssException
     */
    private TransferSetBean setTransferSet(ResultSet rs) throws YssException {
        String columnName = null; //字段名
        TransferSetBean transferSet = null;
        boolean resltFee = false;
        double purchaseIncome = 0D;
        try {
            ResultSetMetaData metaData = rs.getMetaData();

            transferSet = new TransferSetBean();
            double dBaseRate = 1;
            double dPortRate = 1;

            //获取基础汇率
            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("FCuryCode"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                      rs.getString("FPortCode"));
            //获取组合汇率
            dPortRate = rateOper.getDPortRate();

            resltFee = ( (Boolean) purchaseParams.getResultWithPortAndKey(rs.getString("FPortCode"), YssOperCons.YSS_INNER_PURCHASEWFEE)).booleanValue();
            
            transferSet.setSPortCode(rs.getString("FPortCode"));
            
            for (int col = 1; col <= metaData.getColumnCount(); col++) {    //使用原数据
                columnName = metaData.getColumnName(col);                   //获取字段名
                if ("FInvMgrCode".equalsIgnoreCase(columnName)) {           //若有投资经理字段
                    transferSet.setSAnalysisCode1(null == rs.getString("FInvMgrCode") ? "" : rs.getString("FInvMgrCode"));
                }
            }

            for (int col = 1; col <= metaData.getColumnCount(); col++) {    //使用原数据
                columnName = metaData.getColumnName(col);                   //获取字段名
                if ("FBrokerCode".equalsIgnoreCase(columnName)) {           //若有券商字段
                    transferSet.setSAnalysisCode2(null == rs.getString("FBrokerCode") ? "" : rs.getString("FBrokerCode"));
                }
            }

            transferSet.setSCashAccCode(rs.getString("FCashAccCode"));
            if (YssOperCons.YSS_JYLX_NRE.equalsIgnoreCase(rs.getString("FTradeTypeCode"))
            		|| YssOperCons.YSS_JYLX_REMR.equalsIgnoreCase(rs.getString("FTradeTypeCode"))) { //买入回购 -- 及逆回购   add by zhouwei 20120523 bug 4284 买断式回购
                if ("head".equalsIgnoreCase(rs.getString("FType"))) {   //类型为首期
                    transferSet.setDMoney(rs.getDouble("FTotalCost"));  //实收实付金额
                    transferSet.setIInOut( -1); //流出
                } else if ("mature".equalsIgnoreCase(rs.getString("FType"))) { //类型为到期
                	
                	//purchaseIncome = YssD.sub(rs.getDouble("FPurchaseGain"), YssD.add(rs.getDouble("FTradeHandleFee"), rs.getDouble("FBankHandleFee"), rs.getDouble("FSetServiceFee"))); //回购收益-（交易手续费+银行手续费+结算服务费）
                	purchaseIncome = rs.getDouble("FPurchaseGain");//这里调整为不加费用 by leeyu 20100329
                	transferSet.setDMoney(YssD.add(rs.getDouble("FTradeMoney"), purchaseIncome)); //交易金额+回购收益 -- purchaseIncome 替换为rs.getDouble("FPurchaseGain")
                    transferSet.setIInOut(1);   //流入
                }
            } else if (YssOperCons.YSS_JYLX_ZRE.equalsIgnoreCase(rs.getString("FTradeTypeCode"))
            		|| YssOperCons.YSS_JYLX_REMC.equalsIgnoreCase(rs.getString("FTradeTypeCode"))) { //卖出回购 --及正回购
                if ("head".equalsIgnoreCase(rs.getString("FType"))) {   //类型为首期
                    transferSet.setDMoney(rs.getDouble("FTotalCost"));  //实收实付金额
                    transferSet.setIInOut(1);   //流入
                } else if ("mature".equalsIgnoreCase(rs.getString("FType"))) { //类型为到期
                	//purchaseIncome = YssD.add(rs.getDouble("FPurchaseGain"), YssD.add(rs.getDouble("FTradeHandleFee"), rs.getDouble("FBankHandleFee"), rs.getDouble("FSetServiceFee"))); //回购收益+（交易手续费+银行手续费+结算服务费）
                	purchaseIncome = rs.getDouble("FPurchaseGain");//这里调整为不加费用 by leeyu 20100329
                	transferSet.setDMoney(YssD.add(rs.getDouble("FTradeMoney"),purchaseIncome)); //交易金额+回购收益 -- purchaseIncome 替换为rs.getDouble("FPurchaseGain")
                    transferSet.setIInOut( -1); //流出
                }
            }

            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
        return transferSet; //返回资金调拨子数据
    }

    /**
     * 生成证券相关数据（综合业务）
     * @throws YssException
     */
    private void createIntegrated() throws YssException {
        String sqlStr = null;
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement ptmt = null;
        YssPreparedStatement  ptmt = null;
        //=============end====================
        ResultSet rs = null;
        SecIntegratedBean integrade = null;
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        int i = 0;
        try {
            con.setAutoCommit(bTrans);
            delIntegradeData(); //删除综合业务数据
            ptmt = buildIntegratedWithPurchase();       //设置预处理语句
            sqlStr = buildPurchaseSqlWithPurchase();    //获取场外回购的数据
            rs = dbl.queryByPreparedStatement(sqlStr);
            while (rs.next()) {
                i++; //用于生成回购的综合业务的编号
                integrade = setIntegradeData(rs);       //设置综合业务数据
                addBatchPreparedStatement(integrade, ptmt, i); //将获取的综合业务数据设置入预处理的批处理中
            }
            bTrans = true;
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Integrated")); //lock table

            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
            
          //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    	  //当日产生数据，则认为有业务。
            if(integrade==null){
            	this.sMsg="        当日无业务";
            }
            
        } catch (Exception e) {
            throw new YssException("生成回购的证券变动成本出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(ptmt);
            dbl.endTransFinal(con, bTrans);
        }
    }

    /**
     * 删除综合业务数据
     * @throws YssException
     */
    private void delIntegradeData() throws YssException {
        String sqlStr = null;
        try {
            sqlStr = delIntegradeSql();
            dbl.executeSql(sqlStr);
        } catch (Exception e) {
            throw new YssException("删除综合业务数据出现异常！", e);
        }
    }

    /**
     * 拼装删除综合业务数据的sql语句
     * @return String
     */
    private String delIntegradeSql() {
        StringBuffer buf = new StringBuffer();
        buf.append("DELETE FROM ");
        buf.append(pub.yssGetTableName("TB_DATA_Integrated"));
        buf.append(" WHERE  FTRADETYPECODE IN (");//添加交易类型的筛选，避免错误的删除综合业务数据。
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_ZRE)).append(",").append(dbl.sqlString(YssOperCons.YSS_JYLX_NRE));
        //add by zhouwei 20120523 bug 4284 买断式回购
        buf.append(",").append(dbl.sqlString(YssOperCons.YSS_JYLX_REMR)).append(",").append(dbl.sqlString(YssOperCons.YSS_JYLX_REMC));
        buf.append(") AND FEXCHANGEDATE = ");
        buf.append(dbl.sqlDate(dDate));
        buf.append(" AND FPORTCODE= ");// BUG #1089 多组合处理回购业务时，将所TB_001_DATA_INTEGRATED里的信息删除  add by wuweiqi 20110214
        buf.append(dbl.sqlString(this.sPortCode));
        return buf.toString();
    }

    /**
     * 设置综合业务的数据
     * @param rs ResultSet
     * @return SecIntegratedBean
     * @throws YssException
     */
    private SecIntegratedBean setIntegradeData(ResultSet rs) throws YssException {
        SecIntegratedBean integrade = null;
        double dCost = 0D;
        double dBaseRate = 0D;
        double dPortRate = 0D;
        try {
            integrade = new SecIntegratedBean();
            integrade.setIInOutType(1); //方向为流入
            integrade.setSSecurityCode(rs.getString("FSecurityCode"));
            integrade.setSExchangeDate(YssFun.formatDate(dDate, "yyyy-MM-dd")); //获取当前日期为兑换日期
            integrade.setSOperDate(YssFun.formatDate(rs.getDate("FBargainDate"))); //业务日期
            integrade.setSTradeTypeCode(rs.getString("FTradeTypeCode")); //7
            //fanghaoln 20100512 fanghaoln  MS01125 QDV4赢时胜上海2010年04月27日01_AB   
            if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("24")){
            	integrade.setAttrClsCode("SellRepo");
            	integrade.setAttrClsName("正回购");
            	integrade.setSNumType("OPurRE"); //MS01125 设置关联编号类型 liuwei 
            }else if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("25")){
            	integrade.setAttrClsCode("AntiRepo");
            	integrade.setAttrClsName("逆回购");
            	integrade.setSNumType("OPurRE"); //MS01125 设置关联编号类型 liuwei 
            }
            //----------------------------------------end --------MS01125---------------------------
            //fanghaoln 20100521 MS01139 QDV4赢时胜上海2010年04月28日02_AB-------------------------
            integrade.setSRelaNum(rs.getString("FNum"));
            
            integrade.setSTsfTypeCode(YssOperCons.YSS_ZJDBLX_Cost);
            integrade.setSSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Cost + "RE");
            //-------------------------end----------MS01139----------------------------------
            integrade.setSPortCode(rs.getString("FPortCode")); //10
            integrade.setSAnalysisCode1(null == rs.getString("FInvMgrCode") ? " " : rs.getString("FInvMgrCode"));
            integrade.setSAnalysisCode2(" ");
            integrade.setSAnalysisCode3(" ");
            integrade.setDAmount(rs.getDouble("FTradeMoney")); //以金额来代替数量
            dCost = calcPurchaseCost(rs); //计算成本
            integrade.setDCost(dCost);
            integrade.setDMCost(dCost);
            integrade.setDVCost(dCost);
            dBaseRate = rs.getDouble("FBaseCuryRate"); //获取回购业务数据中的基础汇率
            dPortRate = rs.getDouble("FPortCuryRate"); //获取回购业务数据中的基础汇率
            calcPurchaseBaseCost(integrade, dBaseRate); //计算基础成本
            calcPurchasePortCost(rs, dDate, integrade, dBaseRate, dPortRate); //计算组合成本
            integrade.setDBaseCuryRate(dBaseRate);
            integrade.setDPortCuryRate(dPortRate);

        } catch (Exception e) {
            throw new YssException("设置综合业务的数据出现异常！", e);
        }
        return integrade;
    }

    /**
     * 设置批处理语句的值
     * @param integrade SecIntegratedBean 设置的值
     * @param ptmt PreparedStatement 预处理语句
     * @param i int
     * @throws YssException
     */
	//modified by liubo.Story #2145
    //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
    //=================================
    private void addBatchPreparedStatement(SecIntegratedBean integrade,
                                           YssPreparedStatement ptmt, int i) throws

    //=============end====================
        YssException {
        String sNewNum = "E" +
            YssFun.formatDate(dDate,"yyyyMMdd") +
            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Integrated"),
                                   dbl.sqlRight("FNUM", 6),
                                   "000001",
                                   " where FExchangeDate=" +dbl.sqlDate(dDate) +
                                   " or FExchangeDate=" +dbl.sqlDate("9998-12-31"));
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        integrateAdmin.setYssPub(pub);
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
            ptmt.setString(1, sNewNum);
            //edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
            String sSubNum = integrateAdmin.getKeyNum();
            ptmt.setString(2, sSubNum);
            ptmt.setInt(3, integrade.getIInOutType()); //流入、流出
            ptmt.setString(4, integrade.getSSecurityCode());
            ptmt.setDate(5, YssFun.toSqlDate(dDate)); //兑换日期
            ptmt.setDate(6, YssFun.toSqlDate(integrade.getSOperDate())); //业务日期
            ptmt.setString(7, integrade.getSTradeTypeCode());
            ptmt.setString(8, integrade.getSRelaNum().length()>1?integrade.getSRelaNum():" ");//fanghaoln 20100521 MS01139 QDV4赢时胜上海2010年04月28日02_AB
            ptmt.setString(9, integrade.getSNumType()==null? " ":integrade.getSNumType());//MS01125 加入关联编号类型 liuwei
            ptmt.setString(10, integrade.getSPortCode());
            ptmt.setString(11, integrade.getSAnalysisCode1());
            ptmt.setString(12, integrade.getSAnalysisCode2());
            ptmt.setString(13, integrade.getSAnalysisCode3());
            ptmt.setDouble(14, integrade.getDAmount());
            ptmt.setDouble(15, integrade.getDCost());
            ptmt.setDouble(16, integrade.getDMCost());
            ptmt.setDouble(17, integrade.getDVCost());
            ptmt.setDouble(18, integrade.getDBaseCost());
            ptmt.setDouble(19, integrade.getDMBaseCost());
            ptmt.setDouble(20, integrade.getDVBaseCost());
            ptmt.setDouble(21, integrade.getDPortCost());
            ptmt.setDouble(22, integrade.getDMPortCost());
            ptmt.setDouble(23, integrade.getDVPortCost());
            ptmt.setDouble(24, integrade.getDBaseCuryRate());
            ptmt.setDouble(25, integrade.getDPortCuryRate());
            ptmt.setString(26, " ");
            ptmt.setString(27, " ");
            ptmt.setInt(28, 1);
            ptmt.setString(29, pub.getUserCode());
            ptmt.setString(30, YssFun.formatDatetime(new java.util.Date()));
            ptmt.setString(31, integrade.getSTsfTypeCode().length()>1?integrade.getSTsfTypeCode():" ");//fanghaoln 20100521 MS01139 QDV4赢时胜上海2010年04月28日02_AB
            ptmt.setString(32, integrade.getSSubTsfTypeCode().length()>1?integrade.getSSubTsfTypeCode():" ");//fanghaoln 20100521 MS01139 QDV4赢时胜上海2010年04月28日02_AB
            //fanghaoln 20100511 MS01125 QDV4赢时胜上海2010年04月27日01_AB 
            ptmt.setString(33, integrade.getAttrClsCode().length()>1?integrade.getAttrClsCode():" ");
            //-------------------------------end ----------MS01125--------------------------------
            ptmt.executeUpdate(); //执行处理
        } catch (Exception e) {
            throw new YssException("设置预处理值时出现异常！", e);
        }
    }

    /**
     * 拼装获取回购数据的sql语句
     * @return String
     * sj
     */
    private String buildPurchaseSqlWithPurchase() {
        StringBuffer buf = new StringBuffer();
        buf.append(
            "SELECT PURCHASE.*, SECURITY.FSUBCATCODE, CASHACC.FCURYCODE AS FTRADECURY ");//fanghaoln 20100521 MS01139 QDV4赢时胜上海2010年04月28日02_AB
        buf.append(" FROM (SELECT * FROM ");
        buf.append(pub.yssGetTableName("TB_DATA_PURCHASE")); //获取回购数据
        buf.append(" WHERE FCHECKSTATE = 1 AND FPORTCODE = ");//增加对审核状态的判断
        buf.append(dbl.sqlString(this.sPortCode));
        buf.append(" AND FBARGAINDATE = ");
        buf.append(dbl.sqlDate(dDate));
        buf.append(") PURCHASE");
        buf.append("  LEFT JOIN (SELECT FSECURITYCODE, FCATCODE, FSUBCATCODE ");
        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("TB_PARA_SECURITY")); //获取对应的证券信息
        buf.append(" WHERE FCHECKSTATE = 1) SECURITY ON SECURITY.FSECURITYCODE = PURCHASE.FSECURITYCODE ");
        buf.append(" LEFT JOIN (SELECT FCASHACCCODE, FCURYCODE ");
        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("TB_PARA_CASHACCOUNT")); //获取对应的账户信息
        buf.append(" WHERE FCHECKSTATE = 1) CASHACC ON CASHACC.FCASHACCCODE = PURCHASE.FCASHACCCODE");
        //fanghaoln 20100521 MS01139 QDV4赢时胜上海2010年04月28日02_AB-------------------------
//        buf.append(" LEFT JOIN (SELECT FRelaNum, FTsfTypeCode,FSubTsfTypeCode ");
//        buf.append(" FROM ");
//        buf.append(pub.yssGetTableName("Tb_Cash_Transfer")); //获取对应的账户信息
//        buf.append(" WHERE FCHECKSTATE = 1) Transfer ON PURCHASE.Fnum =Transfer.FRelanum");
        //--------------end -------------------MS01139----------------------------------------
        return buf.toString();
    }
    
    /**
     * 生成回购应付费用的查询语句
     * by leeyu 20100327
     * @return
     */
    private String buildPayPurchaseSqlWithPurchase(){
    	StringBuffer buf = new StringBuffer();
        buf.append(
            "SELECT PURCHASE.*, SECURITY.FSUBCATCODE, CASHACC.FCURYCODE AS FTRADECURY ");
        buf.append(" FROM (SELECT * FROM ");
        buf.append(pub.yssGetTableName("TB_DATA_PURCHASE")); //获取回购数据
        buf.append("  WHERE FCHECKSTATE = 1 AND FPORTCODE = ");//增加对审核状态的判断
        buf.append(dbl.sqlString(this.sPortCode));
        buf.append(" AND FBARGAINDATE = ");
        buf.append(dbl.sqlDate(dDate));
        buf.append(") PURCHASE");
        buf.append("  LEFT JOIN (SELECT FSECURITYCODE, FCATCODE, FSUBCATCODE ");
        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("TB_PARA_SECURITY")); //获取对应的证券信息
        buf.append(" WHERE FCHECKSTATE = 1) SECURITY ON SECURITY.FSECURITYCODE = PURCHASE.FSECURITYCODE ");
        //begin by zhouxiang MS01663    启用日期不同的现金账户，引起资金调拨界面和应收应付界面产生重复数据    QDV4赢时胜(上海开发部)2010年08月30日01_B    
     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
  
        buf.append(" LEFT JOIN (SELECT m.FCASHACCCODE, m.FCURYCODE ");
        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("TB_PARA_CASHACCOUNT m ")); //获取对应的账户信息

        //end by lidaolong
        buf.append(" WHERE m.FCHECKSTATE = 1) CASHACC ON CASHACC.FCASHACCCODE = PURCHASE.FCASHACCCODE");
        //end-- by zhouxiang MS01663    启用日期不同的现金账户，引起资金调拨界面和应收应付界面产生重复数据    QDV4赢时胜(上海开发部)2010年08月30日01_B    
        return buf.toString();
    }
    /**
     * 生成插入综合业务表的预处理语句
     * @return PreparedStatement
     */
    private YssPreparedStatement buildIntegratedWithPurchase() throws YssException {
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
        YssPreparedStatement ptmt = null;
        try {
            ptmt = dbl.getYssPreparedStatement(
                buildIntegratedPreparedStatement()); //调用拼装录入综合业务表的预处理语句
        } 

        //=============end====================
        catch (Exception ex) {
            throw new YssException("生成回购处理入综合业务的预处理语句出现异常！", ex);
        }
        return ptmt;
    }

    /**
     * 拼装录入综合业务表的预处理语句
     * @return String
     */
    private String buildIntegratedPreparedStatement() {
        StringBuffer buf = new StringBuffer();
        buf.append("insert into ");
        buf.append(pub.yssGetTableName("Tb_Data_Integrated"));
        buf.append(" (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType,");
        buf.append(" FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost,");
        buf.append(" FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost,");
        buf.append(
            " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,");
        buf.append(//fanghaoln 20100511 MS01125 QDV4赢时胜上海2010年04月27日01_AB  
            " FCheckState,FCreator,FCreateTime,FTsfTypeCode,FSubTsfTypeCode,Fattrclscode )");
        buf.append(
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        //------------------------end-------MS01125----------------------------------------
        return buf.toString();
    }

    /**
     * 计算成本
     * @param rs ResultSet 包含成本的信息
     * @return double
     * @throws YssException
     * sj
     */
    private double calcPurchaseCost(ResultSet rs) throws YssException {
        double cost = 0D;
        Object result = null;
        try {
            result = purchaseParams.getResultWithPortAndKey(this.sPortCode,
                YssOperCons.YSS_INNER_PURCHASEBIC);
            if ( ( (Boolean) result).booleanValue()) { //银行间回购交易费用入成本,
                if (YssOperCons.YSS_JYLX_NRE.equalsIgnoreCase(rs.getString("FTradeTypeCode"))) { //买入回购 -- 逆回购
                    cost = YssD.add(rs.getDouble("FTradeMoney"),
                                    YssD.add(rs.getDouble("FTradeHandleFee"),
                                             rs.getDouble("FBankHandleFee"),
                                             rs.getDouble("FSetServiceFee"))); //交易金额+交易费用
                } else if (YssOperCons.YSS_JYLX_ZRE.equalsIgnoreCase(rs.getString("FTradeTypeCode"))) { //卖出回购 -- 正回购 
                    cost = YssD.add(rs.getDouble("FTradeMoney"),
                                    YssD.add(rs.getDouble("FTradeHandleFee"),
                                             rs.getDouble("FBankHandleFee"),
                                             rs.getDouble("FSetServiceFee"))); //交易金额+交易费用 bug4223 modify by zhouwei 20120409
                }
            } else { //不入成本
                cost = rs.getDouble("FTradeMoney"); //交易金额
            }
        } catch (Exception e) {
            throw new YssException("计算回购库存原币成本出现异常!", e);
        }
        return cost;
    }

    /**
     * 计算基础成本
     * @param secstorage SecurityStorageBean 之前设置的库存信息
     * @param dBaseRate double
     * @return double
     * @throws YssException
     * sj
     */
    private void calcPurchaseBaseCost(SecIntegratedBean integrade,
                                      double dBaseRate) throws YssException {
        if (null != integrade) {
            integrade.setDBaseCost(this.getSettingOper().calBaseMoney(integrade.getDCost(), dBaseRate));
            integrade.setDMBaseCost(this.getSettingOper().calBaseMoney(integrade.getDMCost(), dBaseRate));
            integrade.setDVBaseCost(this.getSettingOper().calBaseMoney(integrade.getDVCost(), dBaseRate));
        }
    }

    /**
     * 计算组合成本
     * @param rs ResultSet 包含组合成本的信息
     * @param dDate Date
     * @param secstorage SecurityStorageBean 之前设置的库存信息
     * @param dBaseRate double
     * @param dPortRate double
     * @return double
     * @throws YssException
     * sj
     */
    private void calcPurchasePortCost(ResultSet rs, java.util.Date dDate,
                                      SecIntegratedBean integrade,
                                      double dBaseRate, double dPortRate) throws
        YssException {
        double portCuryCost = 0D;
        double mPortCuryCost = 0D;
        double vPortCuryCost = 0D;
        if (null != integrade) {
            try {
                portCuryCost = this.getSettingOper().calPortMoney(integrade.getDCost(),
                    dBaseRate, dPortRate, rs.getString("FTradeCury"),
                    dDate, rs.getString("FPortCode"));
                integrade.setDPortCost(portCuryCost);

                mPortCuryCost = this.getSettingOper().
                    calPortMoney(integrade.getDMCost(),
                                 dBaseRate, dPortRate,

                                 rs.getString("FTradeCury"), dDate,
                                 rs.getString("FPortCode"));
                integrade.setDMPortCost(mPortCuryCost);

                vPortCuryCost = this.getSettingOper().
                    calPortMoney(integrade.getDVCost(),
                                 dBaseRate, dPortRate,

                                 rs.getString("FTradeCury"), dDate,
                                 rs.getString("FPortCode"));
                integrade.setDVPortCost(vPortCuryCost);
            } catch (SQLException ex) {
                throw new YssException("设置回购组合库存金额出现异常!", ex);
            }
        }
    }

    /**
     * 生成回购的应收应付数据
     * @throws YssException
     */
    private void createPurchaseDurFromDurTo() throws YssException {
        Connection conn = dbl.loadConnection(); //需要在此进行事务控制
        ArrayList arrPre = null;
        String relaNums = "";
        CashPecPayBean cashPecPay = null;
        boolean bTrans = false;
        try {
            bTrans = true;
            conn.setAutoCommit(false);
            CashPayRecAdmin prAdmin = new CashPayRecAdmin();
            prAdmin.setYssPub(pub);
            createPurchaseTDWithSubtrade(prAdmin); //生成业务资料中的回购未清算数据
            createPurchaseTDWithPurchase(prAdmin); //生成回购业务中的未清算数据
            createPayPurchaseTDWWithPurchase(prAdmin);  //生成银行间首期回购的应付费用数据 by leeyu 20100327
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_CashPayRec")); //锁表
            // ---- 此处需要增加对关联编号类型筛选 -----------------------------------
            arrPre = prAdmin.getList();//获取现金应收应付的所有数据
            for (int i = 0;i<arrPre.size();i++){
                cashPecPay = (CashPecPayBean)arrPre.get(i);
                relaNums += cashPecPay.getRelaNum() + ",";//将应收应付中的关联编号数据获取
            }
            if (relaNums.length() > 0){
                relaNums = relaNums.substring(0,relaNums.length() - 1);//规整关联编号
            }
            
            if(prAdmin.getAddList().size()>0){
            	this.sMsg="";
            }
            //edited by zhouxiang MS01132 结算服务费、交易手续费及银行费用需分开不同类型进行费用的统计（国内，9月发布)
            prAdmin.insert(dDate, "06,07,02,03","06TD,07TD,02TD,03TD,07RE01,07RE02,07RE03", this.sPortCode,0, false, relaNums, "standardRe");//增加对关联编号的筛选条件 //添加回购费用
            //--------------------------------------------------------------------
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("生成回购之未清算款出现异常！", e);
        } finally {
            dbl.endTransFinal(bTrans);
        }
    }

    /**
     * 生成业务资料中的回购未清算数据
     * @param prAdmin CashPayRecAdmin
     */
    private void createPurchaseTDWithSubtrade(CashPayRecAdmin prAdmin) throws YssException {
        ResultSet rs = null;
        String sql = null;
        CashPecPayBean cashPecPay = null;
        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

            sql = createPurchaseTDWithSubtradeSql();
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
                cashPecPay = getCashPecPayData(rs,prAdmin, analy1, analy2, analy3);
            }
        } catch (Exception e) {
            throw new YssException("生成业务资料中的回购未清算数据出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 生成获取业务资料中回购业务的未清算数据的sql语句
     * @return String
     */
    private String createPurchaseTDWithSubtradeSql() {
        StringBuffer buf = new StringBuffer();
        buf.append("select x.* from (select a1.FCashAccCode,");
        buf.append("'exchange' as FType,"); //业务资料(场内)
        buf.append("a1.FNum,a1.FPortCode,FTotalCost,FTradeMoney,FBargainDate,FSettleDate,FFactSettleDate,");//增加获取交易金额
        buf.append("a1.FInvMgrCode,a1.FBrokerCode,a1.FTradeTypeCode,FTradeAmount,");
        buf.append("FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,");
        buf.append("FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FBaseCuryRate,FPortCuryRate,a2.FCashInd,");
        buf.append("a3.FCuryCode as FCashCuryCode,a1.FSecurityCode as FSecurityCode,a1.FMatureDate as FMatureDate,");
        buf.append("a1.FMatureSettleDate as FMatureSettleDate,a1.FAccruedinterest as FPurchaseGain"); //将回购利息统一设置为FPurchaseGain
        buf.append(",FTradeFee1,FTradeFee2,FTradeFee3,FTradeFee4,FTradeFee5,FTradeFee6,FTradeFee7,FTradeFee8");//添加费用 by leeyu 20100327
        buf.append(" from (select FNum,FSecurityCode,FCashAccCode,FPortCode,FBargainDate,FSettleDate,FFactSettleDate,");
        buf.append("FInvMgrCode,FBrokerCode,FTradeTypeCode,FTradeAmount,FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,");
        buf.append("FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FBaseCuryRate,FPortCuryRate,FMatureDate,");
        buf.append("FMatureSettleDate,FAccruedinterest,FTotalCost,FTradeMoney");//增加获取交易金额
        buf.append(",FTradeFee1,FTradeFee2,FTradeFee3,FTradeFee4,FTradeFee5,FTradeFee6,FTradeFee7,FTradeFee8");//添加费用 by leeyu 20100327
        buf.append(" from ");
        buf.append(pub.yssGetTableName("Tb_Data_SubTrade")); //获取业务资料数据
        buf.append(" where FCheckState = 1 ");
        buf.append(" and FPortCode = ");
        buf.append(dbl.sqlString(this.sPortCode));

        buf.append(" and FTradeTypeCode in (");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_ZRE)).append(",");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_NRE)).append(",");//调整为逆回购
        //add by zhouwei 20120523 bug 4284 买断式回购
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_REMR)).append(",");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_REMC));
        buf.append(")");

        buf.append(" and ((FBargainDate <> FFactSettleDate) or (FMatureDate <> FMatureSettleDate)) ");
        buf.append(" and FSettleState <> 2");
        buf.append(" and ((FBargainDate = ").append(dbl.sqlDate(dDate));
        buf.append("or  FFactSettleDate = ").append(dbl.sqlDate(dDate));
        buf.append(") or (FMatureDate = ").append(dbl.sqlDate(dDate));
        buf.append("or FMatureSettleDate = ").append(dbl.sqlDate(dDate));
        buf.append("))) a1 left join (select FCashIND,FTradeTypeCode from Tb_Base_TradeType where FCheckState = 1) a2 on a1.FTradeTypeCode = "); //获取交易中的资金方向
        buf.append(" a2.FTradeTypeCode left join (select FCashAccCode,FCuryCode ");
        buf.append(" from ");
        buf.append(pub.yssGetTableName("Tb_Para_CashAccount")); //获取帐户的币种
        buf.append(" where FCheckState = 1) a3 on a1.FCashAccCode = a3.FCashAccCode) x ");

        return buf.toString();
    }

    /**
     * 生成回购业务中的未清算数据
     * @param prAdmin CashPayRecAdmin
     */
    private void createPurchaseTDWithPurchase(CashPayRecAdmin prAdmin) throws YssException {
        ResultSet rs = null;
        String sql = null;
        CashPecPayBean cashPecPay = null;
        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

            sql = createPurchaseTDWithPurchaseSql();
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
                cashPecPay = getCashPecPayData(rs,prAdmin, analy1, analy2, analy3);
            }
        } catch (Exception e) {
            throw new YssException("生成回购业务中的未清算数据出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 生成银行间首期回购的应付费用数据
     * by leeyu 20100327
     * @throws YssException
     */
    private void createPayPurchaseTDWWithPurchase(CashPayRecAdmin prAdmin) throws YssException{
    	ResultSet rs =null;
    	String sql="";
    	boolean analy1=false;
    	boolean analy2=false;
    	boolean analy3=false;
    	try{
    		analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
    		sql =buildPayPurchaseSqlWithPurchase();
    		rs =dbl.queryByPreparedStatement(sql);
    		while(rs.next()){
    			buildPayPurchaseSqlWithPurchase(rs,analy1,analy2,analy3,prAdmin);
    		}    		
    	}catch(Exception ex){
    		throw new YssException("生成回购费用的应付数据出错",ex);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}    	
    }
    /**
     * 生成获取回购业务中的未清算数据的sql语句
     * @return String
     */
    private String createPurchaseTDWithPurchaseSql() {
        StringBuffer buf = new StringBuffer();
        buf.append("select x.* from (select a1.FCashAccCode,");
        buf.append("'purchase' as FType,"); //回购业务(场外)
        buf.append("a1.FNum,a1.FPortCode,FTotalCost,FTotalCost as FTradeMoney,FBargainDate,FSettleDate as FFactSettleDate,");//获取交易金额
        buf.append("a1.FInvMgrCode,a1.FTradeTypeCode,FTradeMoney as FTradeAmount,"); //在回购业务中金额等于数量
        buf.append("FBaseCuryRate,FPortCuryRate,a2.FCashInd,");
        buf.append("a3.FCuryCode as FCashCuryCode,a1.FSecurityCode as FSecurityCode,a1.FMatureDate as FMatureDate,");
        buf.append("a1.FMatureSettleDate as FMatureSettleDate,a1.FPurchaseGain"); //将回购利息统一设置为FPurchaseGain
        buf.append(",FTradeHandleFee,FBankHandleFee,FSetServiceFee,");// by leeyu 20100327
        
      //alter by liuwei MS01125  获取场外回购业务数据时加上所属分类
        buf.append("case when a1.FTradeTypeCode = '24' then  'SellRepo'  when a1.FTradeTypeCode = '25' then  'AntiRepo' ");
        buf.append("else ' ' end as FATTRCLSCODE");
        //alter by liuwei MS01125  
        
        buf.append(" from (select FNum,FSecurityCode,FCashAccCode,FPortCode,FBargainDate,FSettleDate,");
        buf.append("FInvMgrCode,FTradeTypeCode,FTradeMoney,");
        buf.append("FBaseCuryRate,FPortCuryRate,FMatureDate,");
        buf.append("FMatureSettleDate,FPurchaseGain,FTotalCost");
        buf.append(",FTradeHandleFee,FBankHandleFee,FSetServiceFee");// by leeyu 20100327
        
        buf.append(" from ");
        buf.append(pub.yssGetTableName("Tb_Data_Purchase")); //获取回购业务数据
        buf.append(" where FCheckState = 1 ");
        buf.append(" and FPortCode = ");
        buf.append(dbl.sqlString(this.sPortCode));

        buf.append(" and ((FBargainDate <> FSettleDate) or (FMatureDate <> FMatureSettleDate)) ");

        buf.append(" and ((FBargainDate = ").append(dbl.sqlDate(dDate));
        buf.append("or  FSettleDate = ").append(dbl.sqlDate(dDate));
        buf.append(") or (FMatureDate = ").append(dbl.sqlDate(dDate));
        buf.append("or FMatureSettleDate = ").append(dbl.sqlDate(dDate));

        buf.append("))) a1 left join (select FCashIND,FTradeTypeCode from Tb_Base_TradeType where FCheckState = 1) a2 on a1.FTradeTypeCode = "); //获取交易中的资金方向
        buf.append(" a2.FTradeTypeCode left join (select FCashAccCode,FCuryCode ");
        buf.append(" from ");
        buf.append(pub.yssGetTableName("Tb_Para_CashAccount")); //获取帐户的币种
        buf.append(" where FCheckState = 1) a3 on a1.FCashAccCode = a3.FCashAccCode) x ");
        return buf.toString();
    }

    private CashPecPayBean getCashPecPayData(ResultSet rs, CashPayRecAdmin prAdmin,boolean analy1, boolean analy2, boolean analy3) throws YssException {
        CashPecPayBean cashPecPay = new CashPecPayBean();
        CashPecPayBean reCashpecpay = null;
        StatPurchaseIns purchaseIncome =null;//采用此类的方法计算 by leeyu 20100327
        double dBaseRate = 0D;
        double dPortRate = 0D;
        try {
            if (rs.getString("FCashCuryCode") == null || rs.getString("FCashCuryCode").trim().length() == 0) {
                throw new YssException("系统进行回购业务处理,【" + rs.getString("FCashAccCode") +"】的现金账户对应的货币信息不存在!" + "\n" +
                                       "请核查以下信息：" + "\n" +
                                       "1.【现金账户设置】中该账户信息是否存在且已审核!" + "\n" +
                                       "2.【现金账户设置】中该现金账户代码设置是否正确!");
            }

            cashPecPay.setTradeDate(dDate);
            cashPecPay.setCashAccCode(rs.getString("FCashAccCode"));
            cashPecPay.setPortCode(rs.getString("FPortCode"));

            if (rs.getString("FCashCuryCode") == null || rs.getString("FCashCuryCode").trim().length() == 0) {
                throw new YssException("证券代码为【" + rs.getString("FSecurityCode") + "】,交易日期为【" + YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd") + "】的现金帐户为空，请先设置！");
            }
            dBaseRate = rs.getDouble("FBaseCuryRate");
            dPortRate = rs.getDouble("FPortCuryRate");
            if (analy1) {
                cashPecPay.setInvestManagerCode(rs.getString("FInvMgrCode"));
            } else {
                cashPecPay.setInvestManagerCode(" ");
            }

            cashPecPay.setDataSource(0); //自动
            cashPecPay.setCuryCode(rs.getString("FCashCuryCode"));
            //--- 将赋值设置在此进行 ------------------------------------------------------------- //
            cashPecPay.setBaseCuryRate(dBaseRate);
            cashPecPay.setPortCuryRate(dPortRate);

            cashPecPay.setRelaNum(rs.getString("FNum")); //关联编号
            cashPecPay.setRelaNumType("standardRe"); //回购类型

            cashPecPay.checkStateId = 1;
            //---------------------------------------------------------------------------------- //

            if (rs.getInt("FCashInd") == -1) { //现金方向流出
                if (YssFun.dateDiff(dDate, rs.getDate("FBargainDate")) == 0 //当日为交易日，产生应付清算款
                    && YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FFactSettleDate")) > 0 //MS00461 QDV4赢时胜（上海）2009年5月19日03_B 当实际结算日期大于交易日期时，才产生数据
                    ) { //当实际结算日期不是当前日期时
                    cashPecPay.setMoney(rs.getDouble("FTotalCost")); //因增加了回购的计算，其包括应收利息。所以金额的获取放入每个明晰。sj edit 20081201
                    cashPecPay.setBaseCuryMoney(this.getSettingOper().
                                                calBaseMoney(rs.getDouble(
                        "FTotalCost"), dBaseRate));
                    cashPecPay.setPortCuryMoney(this.getSettingOper().
                                                calPortMoney(rs.getDouble(
                        "FTotalCost"), dBaseRate, dPortRate,

                        rs.getString("FCashCuryCode"), dDate,
                        rs.getString("FPortCode")));
                    cashPecPay.setTsfTypeCode("07"); //应付未清算款
                    cashPecPay.setSubTsfTypeCode("07TD"); //应付未清算款项
                } else if (YssFun.dateDiff(rs.getDate("FFactSettleDate"), dDate) == 0 //当日为实际结算日，产生实付清算款
                           && YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FFactSettleDate")) > 0 //MS00461 QDV4赢时胜（上海）2009年5月19日03_B 当实际结算日期大于交易日期时，才产生数据
                    ) { //当调拨日期是当前日期时，说明是实付款项，按照前日汇率流出
                	cashPecPay.setMoney(rs.getDouble("FTotalCost")); //因增加了回购的计算，其包括应收利息。所以金额的获取放入每个明晰。sj edit 20081201
                    cashPecPay.setBaseCuryMoney(this.getSettingOper().
                                                calBaseMoney(rs.getDouble(
                        "FTotalCost"), dBaseRate));
                    cashPecPay.setPortCuryMoney(this.getSettingOper().
                                                calPortMoney(rs.getDouble(
                        "FTotalCost"), dBaseRate, dPortRate,

                        rs.getString("FCashCuryCode"), dDate,
                        rs.getString("FPortCode")));                	
                    cashPecPay.setTsfTypeCode("03"); //实付未清算款
                    cashPecPay.setSubTsfTypeCode("03TD"); //实付未清算款项
                }
                //--------------当交易类型为回购时，在到期时会有一次反向的影式交易。所以与之前的类型相反 sj modify 20081201 MS00063-------//
                //--- MS00444 QDV4南方2009年05月10日02_B 调整语句执行流程，重新启动一个if判断，而不是之前的一个if语句判断 sj ------------------------------------------------
                if (YssFun.dateDiff(rs.getDate("FMatureDate"), dDate) == 0 && //当日为到期日期
                    YssFun.dateDiff(rs.getDate("FMatureDate"), rs.getDate("FMatureSettleDate")) > 0) { ////到结算到期日期大于到期日期。
                    reCashpecpay = (CashPecPayBean) cashPecPay.clone();
                    // by leeyu 20100327
					reCashpecpay.setMoney(YssD.add(rs
							.getDouble("FPurchaseGain"), rs
							.getDouble("FTradeMoney"))); // 金额为成本+应收利息
					reCashpecpay.setBaseCuryMoney(this.getSettingOper()
							.calBaseMoney(
									YssD.add(rs.getDouble("FPurchaseGain"), rs
											.getDouble("FTradeMoney")),
									dBaseRate));
					reCashpecpay.setPortCuryMoney(this.getSettingOper()
							.calPortMoney(
									YssD.add(rs.getDouble("FPurchaseGain"), rs
											.getDouble("FTradeMoney")),
									dBaseRate, dPortRate,

									rs.getString("FCashCuryCode"), dDate,
									rs.getString("FPortCode")));
//                    purchaseIncome = new StatPurchaseIns();
//                    purchaseIncome.setYssPub(pub);
//                    purchaseIncome.setPortCodes(rs.getString("FPortCode"));
//                    purchaseIncome.getPurchaseParams();
//                    
//                    reCashpecpay.setMoney(purchaseIncome.calcPurcchaseIncomeUtil(rs)); //调整为采用计算资金调拨的方式计算
//                    reCashpecpay.setMoney(YssD.add(reCashpecpay.getMoney(),rs.getDouble("FTradeMoney")));//再加上本金 by leeyu 20100327
                    
//                    reCashpecpay.setBaseCuryMoney(this.getSettingOper().
//                    		calBaseMoney(reCashpecpay.getMoney(),dBaseRate));                    
//                    reCashpecpay.setPortCuryMoney(this.getSettingOper().
//                    			calPortMoney(reCashpecpay.getMoney(),
//                    			dBaseRate, dPortRate,
//                    			rs.getString("FCashCuryCode"),
//                    			dDate,
//                    			rs.getString("FPortCode")));
                    // by leeyu 20100327
                    reCashpecpay.setTsfTypeCode("06"); //应付未清算款
                    reCashpecpay.setSubTsfTypeCode("06TD"); //应付未清算款项
                } else if (YssFun.dateDiff(rs.getDate("FMatureSettleDate"), dDate) == 0 && //若为到期结算日期，则生成
                           YssFun.dateDiff(rs.getDate("FMatureDate"), rs.getDate("FMatureSettleDate")) > 0) { //到结算到期日期不等于到期日期，则生成一笔收入。
                    reCashpecpay = (CashPecPayBean) cashPecPay.clone();
                    // by leeyu 20100327
                    reCashpecpay.setMoney(YssD.add(rs.
                                                 getDouble("FPurchaseGain"),
                                                 rs.getDouble("FTradeMoney"))); //金额为成本+应收利息

                    reCashpecpay.setBaseCuryMoney(this.getSettingOper().
                                                calBaseMoney(YssD.add(rs.
                        getDouble("FPurchaseGain"),
                        rs.getDouble("FTradeMoney")),
                        dBaseRate));
                    reCashpecpay.setPortCuryMoney(this.getSettingOper().
                                                calPortMoney(YssD.add(rs.
                        getDouble("FPurchaseGain"),
                        rs.getDouble("FTradeMoney")),
                        dBaseRate, dPortRate,

                        rs.getString("FCashCuryCode"),
                        dDate,
                        rs.getString("FPortCode")));
//                    purchaseIncome = new StatPurchaseIns();
//                    purchaseIncome.setYssPub(pub);
//                    purchaseIncome.setPortCodes(rs.getString("FPortCode"));
//                    purchaseIncome.getPurchaseParams();
//                    
//                    reCashpecpay.setMoney(purchaseIncome.calcPurcchaseIncomeUtil(rs)); //调整为采用计算资金调拨的方式计算
//                    reCashpecpay.setMoney(YssD.add(reCashpecpay.getMoney(),rs.getDouble("FTradeMoney")));//再加上本金 by leeyu 20100327
//                    reCashpecpay.setBaseCuryMoney(this.getSettingOper().
//                    		calBaseMoney(reCashpecpay.getMoney(),dBaseRate));
//                    reCashpecpay.setPortCuryMoney(this.getSettingOper().
//                			calPortMoney(reCashpecpay.getMoney(),
//                			dBaseRate, dPortRate,
//                			rs.getString("FCashCuryCode"),
//                			dDate,
//                			rs.getString("FPortCode")));
                    // by leeyu 20100327
                    reCashpecpay.setTsfTypeCode("02"); //实付未清算款
                    reCashpecpay.setSubTsfTypeCode("02TD"); //实付未清算款项
                }
                //----------------------------------------------------------------------------------------------------
            } else { //流入都按照当日汇率
                if (YssFun.dateDiff(dDate, rs.getDate("FBargainDate")) == 0 //当日为交易日，产生应收未清算款
                    && YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FFactSettleDate")) > 0 // 当实际结算日期大于交易日期时，才产生数据
                    ) { //当实际日期不是当前日期时
                    cashPecPay.setMoney(rs.getDouble("FTotalCost"));
                    cashPecPay.setBaseCuryMoney(this.getSettingOper().
                                                calBaseMoney(rs.getDouble(
                        "FTotalCost"), dBaseRate));
                    cashPecPay.setPortCuryMoney(this.getSettingOper().
                                                calPortMoney(rs.getDouble(
                        "FTotalCost"), dBaseRate, dPortRate,

                        rs.getString("FCashCuryCode"), dDate,
                        rs.getString("FPortCode")));
                    cashPecPay.setTsfTypeCode("06"); //应付未清算款
                    cashPecPay.setSubTsfTypeCode("06TD"); //应付未清算款项
                } else if (YssFun.dateDiff(rs.getDate("FFactSettleDate"), dDate) == 0
                           && YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FFactSettleDate")) > 0 // 当实际结算日期大于交易日期时，才产生数据
                    ) { //当调拨日期是当前日期时，说明是实收款项
                    cashPecPay.setMoney(rs.getDouble("FTotalCost"));
                    cashPecPay.setBaseCuryMoney(this.getSettingOper().
                                                calBaseMoney(rs.getDouble(
                        "FTotalCost"), dBaseRate));
                    cashPecPay.setPortCuryMoney(this.getSettingOper().
                                                calPortMoney(rs.getDouble(
                        "FTotalCost"), dBaseRate, dPortRate,

                        rs.getString("FCashCuryCode"), dDate,
                        rs.getString("FPortCode")));
                    cashPecPay.setTsfTypeCode("02"); //实收未清算款
                    cashPecPay.setSubTsfTypeCode("02TD"); //实收未清算款项
                }
                // --------------以下为到期处理的方式 ------------------------------------------------- //
                if (YssFun.dateDiff(rs.getDate("FMatureDate"), dDate) == 0 && //当日为到期日期
                    YssFun.dateDiff(rs.getDate("FMatureDate"), rs.getDate("FMatureSettleDate")) > 0) {
                    reCashpecpay = (CashPecPayBean) cashPecPay.clone();
                    // by leeyu 20100327
                    reCashpecpay.setMoney(YssD.add(rs.
                                                 getDouble("FPurchaseGain"),
                                                 rs.getDouble("FTradeMoney"))); //金额为成本+应收利息
                    reCashpecpay.setBaseCuryMoney(this.getSettingOper().
                                                calBaseMoney(YssD.add(rs.
                        getDouble("FPurchaseGain"),
                        rs.getDouble("FTradeMoney")),
                        dBaseRate));
                    reCashpecpay.setPortCuryMoney(this.getSettingOper().
                                                calPortMoney(YssD.add(rs.
                        getDouble("FPurchaseGain"),
                        rs.getDouble("FTradeMoney")),
                        dBaseRate, dPortRate,

                        rs.getString("FCashCuryCode"),
                        dDate,
                        rs.getString("FPortCode")));
//                    purchaseIncome = new StatPurchaseIns();
//                    purchaseIncome.setYssPub(pub);
//                    purchaseIncome.setPortCodes(rs.getString("FPortCode"));
//                    purchaseIncome.getPurchaseParams();
//                    
//                    reCashpecpay.setMoney(purchaseIncome.calcPurcchaseIncomeUtil(rs)); //调整为采用计算资金调拨的方式计算
//                    reCashpecpay.setMoney(YssD.add(reCashpecpay.getMoney(),rs.getDouble("FTradeMoney")));//再加上本金 by leeyu 20100327
//                    reCashpecpay.setBaseCuryMoney(this.getSettingOper().
//                    		calBaseMoney(reCashpecpay.getMoney(),dBaseRate));
//                    reCashpecpay.setPortCuryMoney(this.getSettingOper().
//                			calPortMoney(reCashpecpay.getMoney(),
//                			dBaseRate, dPortRate,
//                			rs.getString("FCashCuryCode"),
//                			dDate,
//                			rs.getString("FPortCode")));
                    // by leeyu 20100327
                    reCashpecpay.setTsfTypeCode("07"); //应付未清算款
                    reCashpecpay.setSubTsfTypeCode("07TD"); //应付未清算款项
                } else if (YssFun.dateDiff(rs.getDate("FMatureSettleDate"), dDate) == 0 && //若为到期结算日期，则生成
                           YssFun.dateDiff(rs.getDate("FMatureDate"), rs.getDate("FMatureSettleDate")) > 0) {
                    reCashpecpay = (CashPecPayBean) cashPecPay.clone();
                    // by leeyu 20100327
                    reCashpecpay.setMoney(YssD.add(rs.
                                                 getDouble("FPurchaseGain"),
                                                 rs.getDouble("FTradeMoney"))); //金额为成本+应收利息
                    reCashpecpay.setBaseCuryMoney(this.getSettingOper().
                                                calBaseMoney(YssD.add(rs.
                        getDouble("FPurchaseGain"),
                        rs.getDouble("FTradeMoney")),
                        dBaseRate));
                    reCashpecpay.setPortCuryMoney(this.getSettingOper().
                                                calPortMoney(YssD.add(rs.
                        getDouble("FPurchaseGain"),
                        rs.getDouble("FTradeMoney")),
                        dBaseRate, dPortRate,

                        rs.getString("FCashCuryCode"),
                        dDate,
                        rs.getString("FPortCode")));
//                    purchaseIncome = new StatPurchaseIns();
//                    purchaseIncome.setYssPub(pub);
//                    purchaseIncome.setPortCodes(rs.getString("FPortCode"));
//                    purchaseIncome.getPurchaseParams();
//                    
//                    reCashpecpay.setMoney(purchaseIncome.calcPurcchaseIncomeUtil(rs)); //调整为采用计算资金调拨的方式计算
//                    reCashpecpay.setMoney(YssD.add(reCashpecpay.getMoney(),rs.getDouble("FTradeMoney")));//再加上本金 by leeyu 20100327
//                    reCashpecpay.setBaseCuryMoney(this.getSettingOper().
//                    		calBaseMoney(reCashpecpay.getMoney(),dBaseRate));
//                    reCashpecpay.setPortCuryMoney(this.getSettingOper().
//                			calPortMoney(reCashpecpay.getMoney(),
//                			dBaseRate, dPortRate,
//                			rs.getString("FCashCuryCode"),
//                			dDate,
//                			rs.getString("FPortCode")));
                    // by leeyu 20100327
                    reCashpecpay.setTsfTypeCode("03"); //实付未清算款
                    reCashpecpay.setSubTsfTypeCode("03TD"); //实付未清算款项
                }
                //----------------------------------------------------------------------------------------------------

            }
            prAdmin.addList(cashPecPay);

            if (null != reCashpecpay) {
                prAdmin.addList(reCashpecpay);
                reCashpecpay = null; //将克隆的对象重新null化，以防在再次循环时因判断条件错误赋值
            }

        } catch (Exception e) {
            throw new YssException("设置回购未清算款数据出现异常！ ", e);
        }
        return cashPecPay;
    }

    /**
     * 将场外回购的费用生成一笔应付的数据 by leeyu 20100327
     * @param dTransDate
     * @param sPortCode
     * @param sSecurityCode
     * @param sAnalysisCode1
     * @param sAnalysisCode2
     * @param sAnalysisCode3
     * @param sTradeType
     * @param dTotalFees
     * @param dBaseRate
     * @param dPortRate
     * @param secPayAdmin
     * @param sInvestType
     * @throws YssException
     */
    private void buildPayPurchaseSqlWithPurchase(ResultSet rs,
            
            boolean analy1,boolean analy2, boolean analy3,
            CashPayRecAdmin prAdmin) throws YssException {
	    	//edited by zhouxiang MS01132    结算服务费、交易手续费及银行费用需分开不同类型进行费用的统计（国内，9月发布）    
			
    	try{
    		double TradeHandleFee=rs.getDouble("FTradeHandleFee"); 	//07RE01   应付银行间回购交易手续费 
	        double BankHandleFee=rs.getDouble("FBankHandleFee");	//07RE02   应付银行间回购银行手续费
	        double SetServiceFee=rs.getDouble("FSetServiceFee");	//07RE03   应付银行间回购结算服务费

    		/*dTotalFees=YssD.add(rs.getDouble("FTradeHandleFee"), rs.getDouble("FBankHandleFee"),rs.getDouble("FSetServiceFee"));//将费用加起来
			
*/	    	
	        //07RE01  	将应付银行间回购交易手续费   加入到应付应付中   
    		if ( TradeHandleFee!=0 ) {//如果费用为0，返回
    			prAdmin.addList(getFeeCashPecPay(TradeHandleFee,rs,YssOperCons.YSS_ZJDBZLX_RE_PayTradeHandleFee,analy1));
	        }
	    	
            //07RE02   	将应付银行间回购银行手续费  加入到应收应付中
    		if ( BankHandleFee!=0 ) {//如果费用为0，返回
    			prAdmin.addList(getFeeCashPecPay(BankHandleFee,rs,YssOperCons.YSS_ZJDBZLX_RE_PayBankHandleFee,analy1));
	        }
    		//07RE03   应付银行间回购结算服务费
    		if ( SetServiceFee!=0 ) {//如果费用为0，返回
    			prAdmin.addList(getFeeCashPecPay(SetServiceFee,rs,YssOperCons.YSS_ZJDBZLX_RE_PaySetServiceFee,analy1));
	        }
	    	
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage());
    	}
    	
//    	cashPecPay.setStrPortCode(sPortCode);
//        secpecpay.setInvMgrCode(sAnalysisCode1);
//        secpecpay.setBrokerCode(sAnalysisCode2);
//        secpecpay.setStrSecurityCode(sSecurityCode);
//        secpecpay.setInvestType(sInvestType);
//
//        secpecpay.setStrCuryCode(operFun.getSecCuryCode(sSecurityCode));
//        secpecpay.setMoney(dTotalFees);
//        secpecpay.setMMoney(dTotalFees);
//        secpecpay.setVMoney(dTotalFees);
//
//        secpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
//        		dTotalFees, dBaseRate));
//        secpecpay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
//        		dTotalFees, dBaseRate));
//        secpecpay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
//        		dTotalFees, dBaseRate));
//        secpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
//        		dTotalFees, dBaseRate, dPortRate,
//            //增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
//       secpecpay.getStrCuryCode(), dTransDate, sPortCode));
//        secpecpay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
//        		dTotalFees, dBaseRate, dPortRate,
//            //增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
//        secpecpay.getStrCuryCode(), dTransDate, sPortCode));
//        secpecpay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
//        		dTotalFees, dBaseRate, dPortRate,
//            //增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
//        secpecpay.getStrCuryCode(), dTransDate, sPortCode));
//        secpecpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); //应付
//        secpecpay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_RE_PayInterestFee);//应付回购费用
//        
//        secpecpay.setBaseCuryRate(dBaseRate);
//        secpecpay.setPortCuryRate(dPortRate);
//        secpecpay.checkStateId = 1;

        
    }
  
	/**
     * 获取此组合的回购通用参数设置
     * @throws YssException
     */
    private void getPurchaseParams() throws YssException {
        purchaseParams = new InnerPubParamsWithPurchase();
        purchaseParams.setYssPub(pub);
        purchaseParams.getAllPubParams(this.sPortCode); //获取所有回购通用参数对此组合的设置
    }

    /**
     * 生成回购的收入数据，业务资料中
     * @param dDate Date
     * @param secPayAdmin SecRecPayAdmin
     * @throws YssException
     */
    private SecRecPayAdmin subTradePurchaseIncome(java.util.Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dPhIncome = 0;
        SecRecPayAdmin secPayAdmin = new SecRecPayAdmin();
        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;
        StatPurchaseIns purchaseIncome = null;
        
        double exchageIncome = 0D;
        
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security"); //判断分析代码
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            strSql = "select a.FSecurityCode,a.FAccruedinterest as FPurchaseGain,a.FPortCode as FPortCode," + 
                " a.FTradeTypeCode as FTradeTypeCode," + 
                " a.FInvMgrCode as FInvMgrCode," +
                " a.FBrokerCode as FBrokerCode," +
                " a.FBaseCuryRate as FBaseCuryRate," +
                " a.FPortCuryRate as FPortCuryRate," +
                " a.FInvestType as FInvestType," +
                " a.FMatureDate as FMatureDate," +
            
                " a.FTradeFee1 as FTradeFee1," +
                " a.FTradeFee2 as FTradeFee2," +
                " a.FTradeFee3 as FTradeFee3," +
                " a.FTradeFee4 as FTradeFee4," +
                " a.FTradeFee5 as FTradeFee5," +
                " a.FTradeFee6 as FTradeFee6," +
                " a.FTradeFee7 as FTradeFee7," +
                " a.FTradeFee8 as FTradeFee8," +
               
                " 'exchange' as FType , " +
                
                //alter by liuwei 获取业务资料中的回购数据时应加上所属分类  20100619
                "case  when FAttrClsCode is null or FAttrClsCode='' then ' '  else FAttrClsCode end as FAttrClsCode "+
                //--------------------------------------------
                " from " + pub.yssGetTableName("Tb_Data_SubTrade") +//增加了场内的类型
                " a left join (select FFeeCode,FAccountingWay as FAW1 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") b on a.FFeeCode1 = b.FFeeCode" +

                " left join (select FFeeCode,FAccountingWay as FAW2 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") c on a.FFeeCode2 = c.FFeeCode" +

                " left join (select FFeeCode,FAccountingWay as FAW3 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") d on a.FFeeCode3 = d.FFeeCode" +

                " left join (select FFeeCode,FAccountingWay as FAW4 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") e on a.FFeeCode4 = e.FFeeCode" +

                " left join (select FFeeCode,FAccountingWay as FAW5 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f on a.FFeeCode5 = f.FFeeCode" +

                " left join (select FFeeCode,FAccountingWay as FAW6 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") g on a.FFeeCode6 = g.FFeeCode" +

                " left join (select FFeeCode,FAccountingWay as FAW7 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") h on a.FFeeCode7 = h.FFeeCode" +

                " left join (select FFeeCode,FAccountingWay as FAW8 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") i on a.FFeeCode8 = i.FFeeCode" +

                " left join (select FSecurityCode as FSecurityCode_j,FCatCode,FSubCatCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") j on a.FSecurityCode = j.FSecurityCode_j" +

                " where (FMatureDate " +
                " between " + dbl.sqlDate(dDate) + " and " +
                dbl.sqlDate(dDate) +
                " ) and FCheckState = 1 and FPortCode in (" + dbl.sqlString(this.sPortCode) + ")" +

                " AND FTradeTypeCode IN ('24', '25','78','79')" +//add by zhouwei 20120523 bug 4284 买断式回购
                " order by FNum";

            rs = dbl.queryByPreparedStatement(strSql);
            while (rs.next()) {
                //dPhIncome = rs.getDouble("FAccruedinterest");
                
                purchaseIncome = new StatPurchaseIns();
                purchaseIncome.setYssPub(pub);
                purchaseIncome.setPortCodes(this.sPortCode);
                purchaseIncome.getPurchaseParams();
                
                exchageIncome = purchaseIncome.calcPurcchaseIncomeUtil(rs);
//                if (rs.getInt("FAW1") == 0) { //计入成本
//                    dPhIncome = YssD.sub(dPhIncome,
//                                         rs.getDouble("FTradeFee1"));
//                }
//                if (rs.getInt("FAW2") == 0) { //计入成本
//                    dPhIncome = YssD.sub(dPhIncome,
//                                         rs.getDouble("FTradeFee2"));
//                }
//                if (rs.getInt("FAW3") == 0) { //计入成本
//                    dPhIncome = YssD.sub(dPhIncome,
//                                         rs.getDouble("FTradeFee3"));
//                }
//                if (rs.getInt("FAW4") == 0) { //计入成本
//                    dPhIncome = YssD.sub(dPhIncome,
//                                         rs.getDouble("FTradeFee4"));
//                }
//                if (rs.getInt("FAW5") == 0) { //计入成本
//                    dPhIncome = YssD.sub(dPhIncome,
//                                         rs.getDouble("FTradeFee5"));
//                }
//                if (rs.getInt("FAW6") == 0) { //计入成本
//                    dPhIncome = YssD.sub(dPhIncome,
//                                         rs.getDouble("FTradeFee6"));
//                }
//                if (rs.getInt("FAW7") == 0) { //计入成本
//                    dPhIncome = YssD.sub(dPhIncome,
//                                         rs.getDouble("FTradeFee7"));
//                }
//                if (rs.getInt("FAW8") == 0) { //计入成本
//                    dPhIncome = YssD.sub(dPhIncome,
//                                         rs.getDouble("FTradeFee8"));
//                }
                insertSecPecPay(rs.getDate("FMatureDate"), //往证券应收应付款插入数据，回购收益
                                rs.getString("FPortCode"),
                                rs.getString("FSecurityCode"),
                                analy1 ? rs.getString("FInvMgrCode") : " ", //判断分析代码
                                analy2 ? rs.getString("FBrokerCode") : " ", "", //判断分析代码
                                rs.getString("FTradeTypeCode"),
                                //dPhIncome,
                                exchageIncome,
                                rs.getDouble("FBaseCuryRate"),
                                rs.getDouble("FPortCuryRate"), secPayAdmin, rs.getString("FInvestType"),rs.getString("FAttrClsCode")); //增加投资类型 modify by wangzuochun 2009.08.15 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
            }
        } catch (Exception e) {
            throw new YssException("设置回购应收应付数据出错!\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return secPayAdmin;
    }

    /**
     * xuqiji 20100712  处理应收应付和到期收益的金额上有很多出入和问题  QDV4招商基金2010年7月7日01_B_回购
     * 在统计库存之前，获取回购的收益数据，以便冲减应收数据
     * @param dDate Date
     * @throws YssException
     */
    private SecRecPayAdmin bankPurchaseInome(java.util.Date dDate) throws YssException{
        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;
        double bankPurchaseIncome = 0D;
        StatPurchaseIns purchaseIncome = null;
        ResultSet rs = null;
        String sqlStr = "";
        SecRecPayAdmin secPayAdmin = null;
        try
        {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            secPayAdmin = new SecRecPayAdmin();
            purchaseIncome = new StatPurchaseIns();
            purchaseIncome.setYssPub(pub);
            purchaseIncome.setPortCodes(this.sPortCode);
            purchaseIncome.getPurchaseParams();
            sqlStr = "SELECT PURCHASE.*,INTEGRATED.FINVESTTYPE " +
                     " FROM (SELECT FNum,FSECURITYCODE,FPORTCODE,FINVMGRCODE,FTRADETYPECODE,FCASHACCCODE,FBARGAINDATE,FMATUREDATE,FBASECURYRATE,FPORTCURYRATE," +
                     // 此处加上所属分类 liuwei MS01125 20100619
                     "Case When FTradeTypeCode='24'  then   'SellRepo'   When FTradeTypeCode='25' then 'AntiRepo' else ' ' end as FAttrClsCode" +
                     
                     " FROM " + pub.yssGetTableName("TB_DATA_PURCHASE") + " WHERE FCHECKSTATE = 1 AND FPORTCODE = " +
                     dbl.sqlString(this.sPortCode) +
                     " AND FMATUREDATE = " + dbl.sqlDate(dDate) +
                     " ) PURCHASE LEFT JOIN " +
                     //-----------zhouxiang 20100712 MS01424 处理应收应付和到期收益的金额上有很多出入和问题  QDV4招商基金2010年7月7日01_B_回购 ---//
                     " (SELECT distinct FSECURITYCODE,FOPERDATE,FTRADETYPECODE, FPORTCODE,FINVESTTYPE,FANALYSISCODE1 FROM " +
                     pub.yssGetTableName("TB_DATA_INTEGRATED") +
                     " WHERE FCHECKSTATE = 1 and foperdate <= "+ dbl.sqlDate(dDate) +" AND FPORTCODE in (" +
                     //------------------------------end-------------------------//
                      dbl.sqlString(this.sPortCode) +
                     ")) INTEGRATED " +
                     " ON PURCHASE.FSECURITYCODE = INTEGRATED.FSECURITYCODE AND PURCHASE.FTRADETYPECODE = INTEGRATED.FTRADETYPECODE AND PURCHASE.FPORTCODE = INTEGRATED.FPORTCODE " +
                     " AND (CASE WHEN PURCHASE.FINVMGRCODE IS NULL THEN ' ' ELSE PURCHASE.FINVMGRCODE END)= INTEGRATED.FANALYSISCODE1 AND PURCHASE.FBARGAINDATE = INTEGRATED.FOPERDATE ";
            rs = dbl.queryByPreparedStatement(sqlStr);
            while(rs.next()){
            	////alter by leeyu MS01125  同一证券有正回购和逆回购时会有两条数据
            	String key = rs.getString("FSecurityCode")+"\f"+YssFun.formatDate(dDate,"yyyy-MM-dd")+"\f"+rs.getString("FPortCode")+"\f"+rs.getString("FTradeTypeCode");

                bankPurchaseIncome = purchaseIncome.calcPurchaseIncome(rs.getDate("FBARGAINDATE"),rs.getString("FSecurityCode"), analy1,analy2,analy3).get(key)==null?0:Double.parseDouble(String.valueOf(purchaseIncome.calcPurchaseIncome(dDate,rs.getString("FSecurityCode"), analy1,analy2,analy3).get(key)));
                //回购业务传个交易日期进去 MS01424    处理应收应付和到期收益的金额上有很多出入和问题    QDV4招商基金2010年7月7日01_B_回购   edited by zhouxiang 

                bankPurchaseIncome = purchaseIncome.calcPurchaseIncome(dDate,rs.getString("FNum"),rs.getString("FSecurityCode"), analy1,analy2,analy3).get(key)==null?0:Double.parseDouble(String.valueOf(purchaseIncome.calcPurchaseIncome(dDate,rs.getString("FNum"),rs.getString("FSecurityCode"), analy1,analy2,analy3).get(key)));

                ////alter by leeyu MS01125  同一证券有正回购和逆回购时会有两条数据
                insertSecPecPay(rs.getDate("FMatureDate"), //往证券应收应付款插入数据，回购收益
                                rs.getString("FPortCode"),
                                rs.getString("FSecurityCode"),
                                analy1 ? rs.getString("FInvMgrCode") : " ",
                                analy2 ? " " : " ", "",
                                rs.getString("FTradeTypeCode"),
                                bankPurchaseIncome,
                                rs.getDouble("FBaseCuryRate"),
                                rs.getDouble("FPortCuryRate"), secPayAdmin, rs.getString("FInvestType"),rs.getString("FAttrClsCode")); // 此处将所属分类传入 MS01125 20100619 liuwei
                               
            }
        }
        catch(Exception e){
            throw new YssException("获取回购收益数据出现异常！",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
        return secPayAdmin;
    }

    /**
     * 生成回购的收入数据，包括场内和银行间。
     * @param dDate Date
     * @throws YssException
     */
    private void purchaseIncome(java.util.Date dDate) throws YssException{
        SecRecPayAdmin secPayAdmin = null;
        SecRecPayAdmin bankSecPayAdmin = null;

        secPayAdmin = subTradePurchaseIncome(dDate);
        bankSecPayAdmin = bankPurchaseInome(dDate);
        if (null != bankSecPayAdmin.getList() && bankSecPayAdmin.getList().size() > 0){
        	//-----xuqiji 20100630 MS01341 冲应收的代码处理无法产生交易所回购的冲应收的数据 QDV4招商基金2010年6月23日01_B -----//
            //secPayAdmin.addList(bankSecPayAdmin.getList());
        	for(int i=0;i < bankSecPayAdmin.getList().size(); i++){
        		secPayAdmin.addList((SecPecPayBean) bankSecPayAdmin.getList().get(i));
        	}
        	//------------------------------------end---------------------------------------//
        }
        
        if(secPayAdmin.getList().size()>0){
        	this.sMsg="";
        }
        secPayAdmin.setYssPub(pub);
        secPayAdmin.insert(dDate, dDate, YssOperCons.YSS_ZJDBLX_Fee + "," + YssOperCons.YSS_ZJDBLX_Income,
                                          YssOperCons.YSS_ZJDBZLX_RE_Fee + "," + YssOperCons.YSS_ZJDBZLX_RE_Income,
                              sPortCode, -99);
    }

    /**
     * 设置回购收入的数据
     * @param dTransDate Date
     * @param sPortCode String
     * @param sSecurityCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param sTradeType String
     * @param dInterest double
     * @param dBaseRate double
     * @param dPortRate double
     * @param secPayAdmin SecRecPayAdmin
     * @param sInvestType String
     * @throws YssException
     */
    // 改方法中加入所属分类参数  FAttrClsCode 以便生成回购收益时加入所属分类  liuwei 20100619 MS01125 
    private void insertSecPecPay(java.util.Date dTransDate,
                                     String sPortCode, String sSecurityCode,
                                     String sAnalysisCode1,
                                     String sAnalysisCode2, String sAnalysisCode3,
                                     String sTradeType, double dInterest,
                                     double dBaseRate, double dPortRate,
                                     SecRecPayAdmin secPayAdmin, String sInvestType, String FAttrClsCode) throws //增加投资类型参数 sInvestType,  modify by wangzuochun 2009.08.15 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
            YssException {
            if (dInterest == 0) {
                return;
            }
            SecPecPayBean secpecpay = new SecPecPayBean();
            SecurityBean sec = new SecurityBean();
            secpecpay.setTransDate(dTransDate);
            secpecpay.setStrPortCode(sPortCode);
            secpecpay.setInvMgrCode(sAnalysisCode1);
            secpecpay.setBrokerCode(sAnalysisCode2);
            secpecpay.setStrSecurityCode(sSecurityCode);
            secpecpay.setInvestType(sInvestType);  //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;

            secpecpay.setStrCuryCode(operFun.getSecCuryCode(sSecurityCode));
            secpecpay.setMoney(dInterest);
            secpecpay.setMMoney(dInterest);
            secpecpay.setVMoney(dInterest);

            secpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                dInterest, dBaseRate));
            secpecpay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
                dInterest, dBaseRate));
            secpecpay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
                dInterest, dBaseRate));
            secpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                dInterest, dBaseRate, dPortRate,
                //增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                secpecpay.getStrCuryCode(), dTransDate, sPortCode));
            secpecpay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
                dInterest, dBaseRate, dPortRate,
                //增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                secpecpay.getStrCuryCode(), dTransDate, sPortCode));
            secpecpay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
                dInterest, dBaseRate, dPortRate,
                //增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                secpecpay.getStrCuryCode(), dTransDate, sPortCode));
            
            secpecpay.setAttrClsCode(FAttrClsCode); //alter by liuwei MS01125 设置回购收益时加入所属分类
            
            if (sTradeType.equalsIgnoreCase("24") || sTradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC)) { //sj add  正回购   add by zhouwei 20120523 bug 4284 买断式回购
                sec.setYssPub(pub);
                sec.setSecurityCode(sSecurityCode);
                secpecpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee); //费用
                secpecpay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_RE_Fee);
                
            } else if (sTradeType.equalsIgnoreCase("25") || sTradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_REMR)) { //sj add  逆回购
                sec.setYssPub(pub);
                sec.setSecurityCode(sSecurityCode);

                secpecpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income); //产生收入 sj 20080226
                secpecpay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_RE_Income);
              
                
            }
            secpecpay.setBaseCuryRate(dBaseRate);
            secpecpay.setPortCuryRate(dPortRate);
            secpecpay.checkStateId = 1;

            secPayAdmin.addList(secpecpay);
    }
    /**
     * @throws YssException 
     * @throws SQLException 
     * @creator zhouxiang 
     * @方法名：getFeeCashpecpay
     * @参数：tradeHandleFee 费用金额，yssZjdbzlxRePayinterestfee 应收应付类型
     * @返回类型：CashPecPayBean 这里设置一个应收应付数据用来加到应收应付控制类中
     * @说明：此方法为初始化回购应收应付的参数设置
     */
    private CashPecPayBean getFeeCashPecPay(double tradeFee,
			ResultSet rs, String cashPecPayType,boolean analy) throws SQLException, YssException {
    	CashPecPayBean cashPecPay = new CashPecPayBean();
    	if (rs.getString("FTRADECURY") == null || rs.getString("FTRADECURY").trim().length() == 0) {
            throw new YssException("系统进行回购业务处理,【" + rs.getString("FCashAccCode") +"】的现金账户对应的货币信息不存在!" + "\n" +
                                   "请核查以下信息：" + "\n" +
                                   "1.【现金账户设置】中该账户信息是否存在且已审核!" + "\n" +
                                   "2.【现金账户设置】中该现金账户代码设置是否正确!");
        }

        cashPecPay.setTradeDate(dDate);
        cashPecPay.setCashAccCode(rs.getString("FCashAccCode"));
        cashPecPay.setPortCode(rs.getString("FPortCode"));

        if (rs.getString("FTRADECURY") == null || rs.getString("FTRADECURY").trim().length() == 0) {
            throw new YssException("证券代码为【" + rs.getString("FSecurityCode") + "】,交易日期为【" + YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd") + "】的现金帐户为空，请先设置！");
        }
        double dBaseRate = rs.getDouble("FBaseCuryRate");
        double dPortRate = rs.getDouble("FPortCuryRate");
        if (analy) {
            cashPecPay.setInvestManagerCode(rs.getString("FInvMgrCode"));
        } else {
            cashPecPay.setInvestManagerCode(" ");
        }

        cashPecPay.setDataSource(0); //自动
        cashPecPay.setCuryCode(rs.getString("FTRADECURY"));
        //--- 将赋值设置在此进行 ------------------------------------------------------------- //
        cashPecPay.setBaseCuryRate(dBaseRate);
        cashPecPay.setPortCuryRate(dPortRate);

        cashPecPay.setRelaNum(rs.getString("FNum")); //关联编号
        cashPecPay.setRelaNumType("standardRe"); //回购类型

        cashPecPay.checkStateId = 1;
        
        //  应付银行间回购交易手续费
        cashPecPay.setMoney(tradeFee); 
        cashPecPay.setBaseCuryMoney(this.getSettingOper().
                                    calBaseMoney(tradeFee, dBaseRate));
        cashPecPay.setPortCuryMoney(this.getSettingOper().
                                    calPortMoney(tradeFee, dBaseRate, dPortRate,

            rs.getString("FTRADECURY"), dDate,
            rs.getString("FPortCode")));
        cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); 
        cashPecPay.setSubTsfTypeCode(cashPecPayType); 
		// TODO Auto-generated method stub
		return cashPecPay;
	}

    /***************************************************************
     * STORY #1509 监控管理－监控结果  处理冻结证券库存
     * 冻结证券库存，这部分数据只在监控管理时用
     * @throws YssException
     */
    private void createFreezeSecStock() throws YssException{
    	
    	ResultSet rs = null;
    	StringBuffer buff = new StringBuffer();
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//    	PreparedStatement pst = null;
    	YssPreparedStatement pst = null;
        //=============end====================
    	String insertSql = "";
    	String delSql = "";
    	boolean bTrans = false; //代表是否开始了事务
    	 Connection conn = dbl.loadConnection();
    	 double dFreezeAmount =0;
    	try{
    		conn.setAutoCommit(false);
            bTrans = true;
    		delSql = " delete from "+pub.yssGetTableName("tb_stock_FreezeSec")+" where FPORTCODE="+dbl.sqlString(this.sPortCode)
    		         +" and fstoragedate="+dbl.sqlDate(this.dDate);
    		dbl.executeSql(delSql);
    		
    		insertSql = " insert into "+pub.yssGetTableName("tb_stock_FreezeSec")+" (FSECURITYCODE,FYEARMONTH,FSTORAGEDATE," +
    				    " FPORTCODE,FATTRCLSCODE,FCATTYPE,FFREEZEAMOUNT,FINVESTTYPE) values(?,?,?,?,?,?,?,?)";
    		
    		buff.append(" select nvl(a.fportcode,b.fportcode) as fportcode,nvl(a.fsecuritycode,b.fsecuritycode) as fsecuritycode,nvl(a.ffreezeamount,0) as ffreezeamount,nvl(b.ffreezeamount,0) as ffreezestoc  from ");
    		//---------------------------------------------------------------------------------------------------//
    		buff.append(" (select a3.fportcode,a3.fsecuritycode,sum(a3.ffreezeamount) as ffreezeamount from ");
    		buff.append(" (select a1.*,case when ").append(dbl.sqlDate(this.dDate)).append("-fbargaindate=0 then a2.ffreezeamount ");//业务处理日期与成交日期是同一日，则冻结数量
    		buff.append(" when ").append(dbl.sqlDate(this.dDate)).append("-fmaturedate=0  then a2.ffreezeamount * (-1) end as ffreezeamount,");//业务处理日期与回购到期日是同一天，则解冻数量
    		buff.append("  a2.fsecuritycode from ");
    		buff.append(" (select fnum,fportcode,fbargaindate,fmaturedate from ").append(pub.yssGetTableName("tb_data_purchase")); 
    		buff.append(" where fcheckstate=1 and ftradetypecode ='24' ");
    		buff.append(" and fportcode = ").append(dbl.sqlString(this.sPortCode));
    		buff.append("and(fbargaindate=").append(dbl.sqlDate(this.dDate)).append(" or fmaturedate=").append(dbl.sqlDate(this.dDate));
    		buff.append(" ))a1");
    		buff.append(" left join  ").append(pub.yssGetTableName("tb_data_purchaserela")).append(" a2 ");
    		buff.append(" on a1.fnum = a2.fnum )a3 group by  fportcode,fsecuritycode)a");
    		buff.append(" full join ");
    		buff.append(" (select * from ").append(pub.yssGetTableName("tb_stock_FreezeSec"));
    		buff.append(" where fportcode = ").append(dbl.sqlString(this.sPortCode));
    		buff.append(" and fstoragedate=").append(dbl.sqlDate(this.dDate)).append("-1 )b");
    		buff.append(" on a.FSECURITYCODE = b.FSECURITYCODE and a.FPORTCODE = b.FPORTCODE ");
    		//edit by songjie 2011.12.22 执行 queryByPreparedStatement 方法报错，所以改回 openResultSet
    		rs = dbl.openResultSet(buff.toString());
			//modified by liubo.Story #2145
			//==============================
//    		pst = dbl.getPreparedStatement(insertSql);
    		pst = dbl.getYssPreparedStatement(insertSql);
			//==============end================
    		while (rs.next()){
    			//当日库存数量为0，就不再统计入库
    			if(YssD.add(rs.getDouble("ffreezeamount"), rs.getDouble("ffreezestoc"))==0)
    			{
    				continue;
    			}
    			dFreezeAmount = YssD.add(rs.getDouble("ffreezeamount"), rs.getDouble("ffreezestoc"));
    			pst.setString(1, rs.getString("fsecuritycode"));
    			pst.setString(2, YssFun.formatDate(this.dDate, "yyyyMM"));
    			pst.setDate(3, YssFun.toSqlDate(this.dDate));
    			pst.setString(4, rs.getString("fportcode"));
    			pst.setString(5, " ");
    			pst.setString(6, "FI");
    			pst.setDouble(7, dFreezeAmount);
    			pst.setString(8, "C");
    			
    			pst.addBatch();
    		}
    		pst.executeBatch();
    		conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
    	}catch(YssException e){
    		throw new YssException();
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
    		throw new YssException();
		}finally{
    		dbl.endTransFinal(conn, bTrans);
    		dbl.closeResultSetFinal(rs);
    		dbl.closeStatementFinal(pst);
    		
    	}
    }
}
