package com.yss.main.operdeal.opermanage;

import java.util.*;

import com.yss.util.*;
import com.yss.main.operdata.RateTradeBean;
import java.sql.ResultSet;

/**
 * <p>Title: 外汇交易的业务处理</p>
 *
 * <p>Description: 处理外汇交易业务的类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech </p>
 *
 * @author shenjie
 * @version 1.0
 * MS00538 QDV4海富通2009年06月21日02_AB sj
 */
public class OperRateTradeManage
    extends BaseOperManage {
    public OperRateTradeManage() {
    }

    /**
     * 执行业务处理
     *
     * @throws YssException
     * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage
     *   method
     */
    public void doOpertion() throws YssException {
       createRateTradeCashTransfer();
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
       this.dDate = dDate;
       this.sPortCode = portCode;
    }

    /**
     * 重新生成一遍当日的外汇交易的资金调拨
     * @throws YssException
     */
    private void createRateTradeCashTransfer() throws YssException {
        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;
        RateTradeBean rateTrade = null;
        String sql = null;
        ResultSet rs = null;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            sql = getRateTradeSql();
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
                rateTrade = new RateTradeBean();
                rateTrade.setYssPub(pub);
                rateTrade.setPortCode(this.sPortCode);
                rateTrade.setBPortCode(rs.getString("FBPortCode"));
                rateTrade.setTradeDate(dDate);
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                rateTrade.setStrAttrClsCode(rs.getString("FAttrclsCode"));
                rateTrade.setStrBAttrClsCode(rs.getString("FBAttrclsCode"));
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                if (analy1) {
                    rateTrade.setAnalysisCode1(rs.getString("FAnalysisCode1"));
                    rateTrade.setBAnalysisCode1(rs.getString("FBanalysisCode1"));
                }
                if (analy2) {
                    rateTrade.setAnalysisCode2(rs.getString("FAnalysisCode2"));
                    rateTrade.setBAnalysisCode2(rs.getString("FBanalysisCode2"));
                }
                if (analy3) {
                    rateTrade.setAnalysisCode3(rs.getString("FAnalysisCode3"));
                    rateTrade.setBAnalysisCode3(rs.getString("FBanalysisCode3"));
                }
                rateTrade.setBCashAccCode(rs.getString("FbCashAccCode"));
                rateTrade.setSCashAccCode(rs.getString("FsCashAccCode"));
                rateTrade.setSettleDate(rs.getDate("FsettleDate"));
                rateTrade.setBSettleDate(rs.getDate("FBSettleDate"));
                rateTrade.setTradeType(rs.getString("FtradeType"));
                rateTrade.setCatType(rs.getString("FcatType"));
                rateTrade.setExCuryRate(rs.getDouble("FexCuryRate"));
                rateTrade.setLingCuryRate(rs.getDouble("FlongCuryRate"));
                rateTrade.setBMoney(rs.getDouble("FbMoney"));
                rateTrade.setSMoney(rs.getDouble("FsMoney"));

                rateTrade.setBCuryCode(rs.getString("FBCuryCode"));
                rateTrade.setSCuryCode(rs.getString("FSCuryCode"));

                rateTrade.getOperValue("costfx");
                
                //add by huangqriong 2012-09-10 story #2822 
                rateTrade.setBailType(rs.getString("FBAILTYPE"));                
                rateTrade.setBailCashCode(rs.getString("FBAILCASHCODE") == null ? "" : rs.getString("FBAILCASHCODE"));
                
                if("BL".equalsIgnoreCase(rateTrade.getBailType()))
                	rateTrade.setBailScale(rs.getDouble("FBAILSCALE"));
                else if("GD".equalsIgnoreCase(rateTrade.getBailType()))
                	rateTrade.setBailFix(rs.getDouble("FBAILFIX"));
                
                //---end---
                /**Start 20131017 added by liubo.Bug #81103.QDV4赢时胜(深圳)2013年10月14日01_B
                 * 增加RateTradeBean对象的OldNum的赋值，在这个对象里面，若OldNum无值，则不会自动删除历史的资金调拨数据*/
                rateTrade.setOldNum(rs.getString("FNum"));
                /**Start 20131017 added by liubo.Bug #81103.QDV4赢时胜(深圳)2013年10月14日01_B*/
                
                rateTrade.createSavCashTrans(rs.getString("FNum"),rs.getString("FNum"),1);
            }
            
          //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
            if(rateTrade==null){
            	this.sMsg="        当日无业务";
            }
            
        }
        catch (Exception e) {
            throw new YssException("生成外汇交易的资金调拨出现异常！",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 拼装获取外汇数据的sql语句
     * @return String
     */
    private String getRateTradeSql(){
        StringBuffer buf = new StringBuffer();
        buf.append("select * from ");
        buf.append(pub.yssGetTableName("Tb_Data_RateTrade"));
        buf.append(" where FCheckState = 1 ");
        buf.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode));
        buf.append(" and FTradeDate = ").append(dbl.sqlDate(dDate));
        return buf.toString();
    }
}
