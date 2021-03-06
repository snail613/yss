package com.yss.main.operdeal.opermanage;

import java.sql.*;
import java.util.Date;

import com.yss.main.operdeal.*;
import com.yss.util.*;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.main.settlement.TATradeSettleBean;

/**
 * <p>Title: xuqiji 20090805 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015    国内权益处理</p>
 *
 * <p>Description: 业务资料数据和TA交易数据的自动结算，产生资金调拨业务处理类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TradeDataBalanceManage extends BaseOperManage{
    private String sNumAll="";//保存业务资料中交易编号
    public TradeDataBalanceManage() {
    }

    /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate; //操作日期
        this.sPortCode = portCode; //组合代码
    }

    /**
     * 执行业务处理
     * @throws YssException
     */
    public void doOpertion() throws YssException {
        Connection conn=null;
        boolean bTrans=true;
        ResultSet rs=null;
        StringBuffer buff=null;
        try{
            /*以下是业务资料数据的自动结算，产生资金调拨业务处理*/
            buff=new StringBuffer();
            buff.append(" select * from ");
            buff.append(pub.yssGetTableName("tb_data_subtrade"));//交易子表
            buff.append(" where FBargainDate =").append(dbl.sqlDate(this.dDate));//实际结算日期
            buff.append(" and FPortCode =").append(dbl.sqlString(this.sPortCode));//组合代码
            buff.append(" and FAutoSettle = 1 ");//结算类型为-自动结算
            buff.append(" and FSettleState = 0 ");//结算状态-未结算

            rs=dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0,buff.length());

            while(rs.next()){
                sNumAll+=rs.getString("FNum")+",";
            }
            conn=dbl.loadConnection();//打开连接
            conn.setAutoCommit(false);//手动提交事务
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_data_subtrade"));//给操作表加锁

            /*更新业务资料数据结算状态为已结算*/
            buff.append(" update ");
            buff.append(pub.yssGetTableName("tb_data_subtrade"));//交易子表
            buff.append(" set FSettleState=1 where FNum in(");
            buff.append(this.operSql.sqlCodes(sNumAll));
            buff.append(")");

            dbl.executeSql(buff.toString());
            conn.commit();
            conn.setAutoCommit(true);//设置为自动提交事务
            bTrans=false;
            /*产生资金调拨*/
            BaseTradeSettlement tradeSettle=new BaseTradeSettlement();//交易结算产生资金调拨类
            tradeSettle.setYssPub(pub);//设置pub
            tradeSettle.setStartDate(this.dDate);//设置起始日期
            tradeSettle.setEndDate(this.dDate);//设置截止日期
            tradeSettle.setNums(sNumAll);//传入成交编号
            conn.setAutoCommit(false);//手动提交事务
            bTrans=true;
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer"));//操作表加锁
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));//操作表加锁
            if (sNumAll.trim().length() > 0) {
                tradeSettle.createCashTransfer(); //调用保存资金调拨方法
            }
            
          //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(sNumAll.trim().length() ==0){
    			this.sMsg="        当日无业务";
    		}
            
            conn.commit();//提交事务
            conn.setAutoCommit(true);//设置为自动提交事务
            bTrans=false;
            /*下面方法是TA交易数据的自动结算，产生资金调拨业务处理*/
            doTADataBalance();
        }catch(Exception e){
            throw new YssException("业务资料数据自动结算，产生资金调拨出错！",e);
        }finally{
            dbl.endTransFinal(conn,bTrans);
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * TA交易数据的自动结算，产生资金调拨业务处理
     * doTADataBalance
     */
    private void doTADataBalance() throws YssException {
        StringBuffer buff=null;
        StringBuffer bufNum=null;//拼接交易编号
        ResultSet rs=null;
        Connection conn=null;
        boolean bTrans=true;//事务控制
        TaTradeBean taTrade =null;//Ta交易bean
        try{
            buff=new StringBuffer();
            bufNum=new StringBuffer();
            taTrade = new TaTradeBean();
            taTrade.setYssPub(pub);//设置pub
            buff.append(" SELECT * FROM ");
            buff.append(pub.yssGetTableName("Tb_TA_Trade"));//TA交易数据表
            buff.append(" WHERE FCheckState = 1 AND FSettleState = 0");
            buff.append(" AND FPortCode = ").append(dbl.sqlString(this.sPortCode));
            buff.append(" AND FConfimDate = ").append(dbl.sqlDate(this.dDate));

            rs=dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0,buff.length());
            while(rs.next()){
                bufNum.append(rs.getString("FNum")).append(",");//拼接编号
                buff.append(rs.getString("FNum")).append("\t");
                buff.append(rs.getString("FSellType")).append("\t");
                buff.append(rs.getString("FSellNetCode")).append("\t");
                buff.append(rs.getString("FAnalysisCode1")).append("\t");
                buff.append(rs.getString("FAnalysisCode2")).append("\t");
                buff.append(rs.getString("FAnalysisCode3")).append("\t");
                buff.append(rs.getString("FPortCode")).append("\t");
                buff.append(rs.getString("FCashAccCode")).append("\t");
                buff.append(rs.getDate("FMarkDate") == null ? "" : rs.getString("FMarkDate")).append("\t");
                buff.append(rs.getDate("FTradeDate")).append("\t");
                buff.append(rs.getDate("FConfimDate")).append("\t");
                buff.append(rs.getDate("FSettleDate")).append("\t");
                buff.append(rs.getString("FPortCuryRate")).append("\t");
                buff.append(rs.getString("FBaseCuryRate")).append("\t");
                buff.append(rs.getString("FSellAmount")).append("\t");
                buff.append(rs.getString("FSellPrice")).append("\t");
                buff.append(rs.getString("FSellMoney")).append("\t");
                buff.append(rs.getString("FIncomeNotBal") == null ? "" : rs.getString("FIncomeNotBal")).append("\t");
                buff.append(rs.getString("FIncomeBal") == null ? "" : rs.getString("FIncomeBal")).append("\t");
                buff.append(rs.getString("FCuryCode")).append("\t");
                buff.append("").append("\t"); //描述的位置
                taTrade.loadFees(rs);
                buff.append(taTrade.getFees()).append("\t");
                buff.append(rs.getString("FCheckState")).append("\t");
                //buff.append("").append("\t"); //OldNum
                buff.append(rs.getString("FNum")).append("\t"); //OldNum BUG4871业务处理产生重复资金调拨数据。 
                buff.append("1").append("\t");
                buff.append("9998-12-31").append("\t");
                buff.append("9998-12-31").append("\t");
                buff.append("").append("\t"); //fee
                buff.append(rs.getString("FSettleState")).append("\t");
                buff.append(rs.getString("FPortClsCode")).append("\t");
                buff.append(rs.getString("FSettleMoney") == null ? "" : rs.getString("FSettleMoney")).append("\t");
                buff.append(null == rs.getDate("FConfimDate") ? rs.getDate("FSettleDate") : rs.getDate("FConfimDate")).append("\t");
                buff.append(null == rs.getDate("FConfimDate") ? rs.getDate("FSettleDate") : rs.getDate("FConfimDate")).append("\t");
                //------ modify by wangzuochun 2010.10.27
                buff.append(rs.getDouble("FBeMarkMoney")).append("\t");
                buff.append(rs.getDouble("FSplitRatio")).append("\t");                
                buff.append("true").append("\t");
                buff.append(rs.getDouble("FConvertNum")).append("\t");
                buff.append(rs.getDouble("FCashBal")).append("\t");
                buff.append(rs.getDouble("FCashRepAmount")).append("\t");
                buff.append(rs.getDouble("FCASHBALFEE")).append("\t");
                buff.append(rs.getDouble("fpaidinmoney")).append("\t");
                //buff.append(rs.getDouble("fportdegree")).append("\tnull").append("\r\n");
                buff.append(rs.getDouble("fportdegree")).append("\t");  //modify by zhangjun 2012.07.05  BUG4871业务处理产生重复资金调拨数据。 
                buff.append(rs.getDouble("FCASHBALFEE")).append("\t").append(rs.getDouble("FPaidInMoney")).append("\tnull").append("\r\n");//BUG4856调度方案执行到“业务处理类”--“自动结算业务资料和TA交易数据”时报错 

                //-------------------------------------------------------------------------//
            }
            
            if (buff.length() > 0) {
                buff.append("\f\f").append(bufNum).append("\f\f").append("do");
                
               //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
        	   //当日产生数据，则认为有业务。
        		this.sMsg="";
        		
            }else{
                return;
            }
            conn=dbl.loadConnection();//打开连接
            conn.setAutoCommit(false);//手动提交事务
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer"));//操作表加锁
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));//操作表加锁
            //下面产生资金调拨数据和更新Ta交易数据结算状态为已结算
            TATradeSettleBean taSettle =new TATradeSettleBean();//TA结算bean
            taSettle.setYssPub(pub);//设置pub
            taSettle.parseRowStr(buff.toString());//解析数据
            taSettle.setFlag(false);	//20121212 added by liubo.Bug #6584.Flag变量用于控制自动结算TA交易数据时，进行先删后增的操作
            taSettle.doOperation("do");//具体操作
            conn.commit();//提交事务
            conn.setAutoCommit(true);//设置为自动提交事务
            bTrans=false;
        }catch(Exception e){
            throw new YssException("TA交易数据的自动结算，产生资金调拨业务处理出错！",e);
        }finally{
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn,bTrans);
        }
    }
}








