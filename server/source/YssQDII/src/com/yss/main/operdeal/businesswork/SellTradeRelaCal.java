package com.yss.main.operdeal.businesswork;

import java.sql.*;
import java.util.*;

import com.yss.main.operdata.*;
import com.yss.main.operdeal.businesswork.pojo.*;
import com.yss.manager.*;
import com.yss.util.*;

/**
 *
 * <p>Title: 计算卖出交易中的关联数据</p>
 *
 * <p>Description: 暂时只计算卖出证券的估值增值</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SellTradeRelaCal
    extends BaseBusinWork {

    public SellTradeRelaCal() {
    }

    /**
     * doOperation
     * 计算卖出证券交易的关联数据
     * @param sType String
     */
    public String doOperation(String sType) throws YssException {
        this.doSecPecPay();
        return "";
    }

    private void doSecPecPay() throws YssException {
        String strSql = "";
        TradeSellRelaPojo tsr = null;
        SecPecPayBean secpecpay = null;
        SecRecPayAdmin srpa = new SecRecPayAdmin();
        HashMap hmEveStg = null;
        HashMap newhmEveStg = null;
        Iterator iter = null;
        String sKey = "";
        boolean analy1;
        boolean analy2;
        boolean analy3;
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(bTrans);
            bTrans = true;
            //删除已有数据
            strSql = "delete from " + pub.yssGetTableName("TB_Data_TradeSellRela") +
                " where FNum in " +
                "(select FNum from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " where ftradetypecode = '02' and fbargaindate = " +
                dbl.sqlDate(workDate) + " and FPortCode in(" + this.operSql.sqlCodes(portCodes) + "))";
            dbl.executeSql(strSql);
            //插入新数据
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            hmEveStg = this.getEveStg(workDate);
            newhmEveStg = this.setAppreciationData(workDate, hmEveStg, analy1,
                analy2, analy3);
            iter = newhmEveStg.keySet().iterator();
            while (iter.hasNext()) {
                sKey = (String) iter.next();
                tsr = (TradeSellRelaPojo) newhmEveStg.get(sKey);
                strSql = "insert into " + pub.yssGetTableName("TB_Data_TradeSellRela") +
                    " (FNum,FTsfTypeCode,FSubTsfTypeCode,FAppreciation,FMAppreciation, " +
                    " FVAppreciation,FBaseAppreciation,FMBaseAppreciation,FVBaseAppreciation, " +
                    " FPortAppreciation,FMPortAppreciation,FVPortAppreciation,FRevenue) " +
                    " values(" +
                    dbl.sqlString(tsr.getNum()) + "," +
                    dbl.sqlString(tsr.getTsftypecode()) + "," +
                    dbl.sqlString(tsr.getSubtsftypecode()) + "," +
                    YssD.round(tsr.getAppreciation(), 2) + "," +
                    YssD.round(tsr.getMAppreciation(), 2) + "," +
                    YssD.round(tsr.getVAppreciation(), 2) + "," +
                    YssD.round(tsr.getBaseAppreciation(), 2) + "," +
                    YssD.round(tsr.getMBaseAppreciation(), 2) + "," +
                    YssD.round(tsr.getVBaseAppreciation(), 2) + "," +
                    YssD.round(tsr.getPortAppreciation(), 2) + "," +
                    YssD.round(tsr.getMPortAppreciation(), 2) + "," +
                    YssD.round(tsr.getVPortAppreciation(), 2) + "," +
                    0 + ")";
                dbl.executeSql(strSql);

                secpecpay = this.setDatatoSecRecPay(tsr);
                srpa.addList(secpecpay);
            }
            con.commit();
            con.setAutoCommit(bTrans);
            bTrans = false;
            srpa.setYssPub(pub);
            srpa.insert(workDate, workDate, "09,99", "09EQ,9909EQ,9905EQ",
                        portCodes, -99, 0); //MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
        } catch (Exception ex) {
            throw new YssException("计算卖出证券估值增值出错" + "\r\n" + ex.getMessage(), ex);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
    }

    //获取证券应收应付表和证券库存表的联合信息，放到hash表中
    private HashMap getEveStg(java.util.Date dDate) throws
        YssException {
        HashMap hmEveStg = new HashMap();
        TradeSellRelaPojo tsr = null;
        boolean analy1;
        boolean analy2;
        boolean analy3;
        ResultSet rs = null;
        String sKey = "";
        String strSql = "";
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            strSql = "select a.*,b.fstorageamount from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") + " a" +
                " left join (select * from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " where fcheckstate = 1) b" +
                " on a.fsecuritycode = b.fsecuritycode and a.fstoragedate = b.fstoragedate" +
                " where a.fsubtsftypecode in ('09EQ','9909EQ','9905EQ') and a.fcheckstate = 1" +
                " and a." + this.operSql.sqlStoragEve(dDate) +
                " and a.fportcode in (" + this.operSql.sqlCodes(portCodes) + ") order by a.fsecuritycode,a.ftsftypecode desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                tsr = new TradeSellRelaPojo();
                sKey = rs.getString("fsecuritycode") + "\f" +
                    rs.getString("fsubtsftypecode") + "\f" +
                    rs.getString("FPortCode") +
                    (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "");
                tsr.setSecuritycode(rs.getString("fsecuritycode"));
                tsr.setTsftypecode(rs.getString("FTsfTypeCode"));
                tsr.setSubtsftypecode(rs.getString("FSubTsfTypeCode"));
                tsr.setPortCode(rs.getString("FPortCode"));
                tsr.setBal(rs.getDouble("fbal"));
                tsr.setMBal(rs.getDouble("fmbal"));
                tsr.setVBal(rs.getDouble("fvbal"));
                tsr.setBaseCuryBal(rs.getDouble("fbasecurybal"));
                tsr.setMBaseCuryBal(rs.getDouble("fmbasecurybal"));
                tsr.setVBaseCuryBal(rs.getDouble("fvbasecurybal"));
                tsr.setPortCuryBal(rs.getDouble("fportcurybal"));
                tsr.setMPortCuryBal(rs.getDouble("fmportcurybal"));
                tsr.setVPortCuryBal(rs.getDouble("fvportcurybal"));
                tsr.setStorageAmount(rs.getDouble("fstorageamount"));
                tsr.setAnalysisCode1( (analy1 ? rs.getString("FAnalysisCode1") :
                                       " "));
                tsr.setAnalysisCode2( (analy2 ? rs.getString("FAnalysisCode2") :
                                       " "));
                tsr.setAnalysisCode3( (analy3 ? rs.getString("FAnalysisCode3") :
                                       " "));
                tsr.setCuryCode(rs.getString("FCuryCode"));
                hmEveStg.put(sKey, tsr);
            }
        } catch (Exception ex) {
            throw new YssException("获取证券应收应付和库存联合数据出错" + "\r\n" + ex.getMessage(),
                                   ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmEveStg;
    }

    //查询交易数据子表和证券应收应付表的联合信息，对比hash表修改数据
    private HashMap setAppreciationData(java.util.Date dDate, HashMap hmEveStg,
                                        boolean bAnaly1, boolean bAnaly2,
                                        boolean bAnaly3) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        TradeSellRelaPojo tsr = null;
        TradeSellRelaPojo newtsr = null;
        String sKey = "";
        HashMap newhmEveStg = new HashMap();
        try {
            strSql =
                "select a.*,b.FTsfTypeCode,b.FSubTsfTypeCode, " +
                " b.fanalysiscode1,b.fanalysiscode2,b.fanalysiscode3 from " +
                pub.yssGetTableName("Tb_Data_SubTrade") + " a" +
                " left join(select * from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " where " + this.operSql.sqlStoragEve(dDate) +
                " and fsubtsftypecode in ('09EQ','9909EQ','9905EQ') " +
                " and fcheckstate = 1) b on a.fsecuritycode = b.fsecuritycode" +
                " where a.ftradetypecode = '02' and a.fcheckstate = 1" +
                " and a.fbargaindate = " + dbl.sqlDate(dDate) +
                " and a.fportcode in (" + this.operSql.sqlCodes(portCodes) + ") order by FNum,fsubtsftypecode desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FSubTsfTypeCode") + "\f" +
                    rs.getString("FPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("FInvMgrCode") : "") +
                    (bAnaly2 ? "\f" + rs.getString("FBrokerCode") : "") +
                    (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "");
                if (hmEveStg.containsKey(sKey)) {
                    tsr = (TradeSellRelaPojo) hmEveStg.get(sKey);
                    newtsr = new TradeSellRelaPojo(); //新建一个实例保存到另一Hash表中
                    newtsr.setNum(rs.getString("FNum"));
                    newtsr.setTsftypecode(rs.getString("FTsfTypeCode"));
                    newtsr.setSubtsftypecode(rs.getString("FSubTsfTypeCode"));
                    newtsr.setAnalysisCode1(tsr.getAnalysisCode1());
                    newtsr.setAnalysisCode2(tsr.getAnalysisCode2());
                    newtsr.setAnalysisCode3(tsr.getAnalysisCode3());
                    newtsr.setCuryCode(tsr.getCuryCode());
                    newtsr.setPortCode(tsr.getPortCode());
                    newtsr.setSecuritycode(tsr.getSecuritycode());
                    newtsr.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                    newtsr.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                    //如果卖出数量小于库存数量则进行计算，否则就是卖空，直接取估值余额
                    if (rs.getDouble("FTradeAmount") <
                        tsr.getStorageAmount()) {
                        //----------------------------------------------------
                        newtsr.setAppreciation(YssD.round(YssD.mul(YssD.div(tsr.
                            getBal(), tsr.getStorageAmount())
                            , rs.getDouble("FTradeAmount"))
                            , 2));
                        tsr.setBal(tsr.getBal() - newtsr.getAppreciation());
                        //----------------------------------------------------
                        newtsr.setMAppreciation(YssD.round(YssD.mul(YssD.div(tsr.
                            getMBal(), tsr.getStorageAmount())
                            , rs.getDouble("FTradeAmount"))
                            , 2));
                        tsr.setMBal(tsr.getMBal() - newtsr.getMAppreciation());
                        //----------------------------------------------------
                        newtsr.setVAppreciation(YssD.round(YssD.mul(YssD.div(tsr.
                            getVBal(), tsr.getStorageAmount())
                            , rs.getDouble("FTradeAmount"))
                            , 2));
                        tsr.setVBal(tsr.getVBal() - newtsr.getVAppreciation());
                        //----------------------------------------------------
                        newtsr.setBaseAppreciation(YssD.round(YssD.mul(YssD.div(tsr.
                            getBaseCuryBal(), tsr.getStorageAmount())
                            , rs.getDouble("FTradeAmount"))
                            , 2));
                        tsr.setBaseCuryBal(tsr.getBaseCuryBal() -
                                           newtsr.getBaseAppreciation());
                        //----------------------------------------------------
                        newtsr.setMBaseAppreciation(YssD.round(YssD.mul(YssD.div(tsr.
                            getMBaseCuryBal(), tsr.getStorageAmount())
                            , rs.getDouble("FTradeAmount"))
                            , 2));
                        tsr.setMBaseCuryBal(tsr.getMBaseCuryBal() -
                                            newtsr.getMBaseAppreciation());
                        //----------------------------------------------------
                        newtsr.setVBaseAppreciation(YssD.round(YssD.mul(YssD.div(tsr.
                            getVBaseCuryBal(), tsr.getStorageAmount())
                            , rs.getDouble("FTradeAmount"))
                            , 2));
                        tsr.setVBaseCuryBal(tsr.getVBaseCuryBal() -
                                            newtsr.getVBaseAppreciation());
                        //----------------------------------------------------
                        newtsr.setPortAppreciation(YssD.round(YssD.mul(YssD.div(tsr.
                            getPortCuryBal(), tsr.getStorageAmount())
                            , rs.getDouble("FTradeAmount"))
                            , 2));
                        tsr.setPortCuryBal(tsr.getPortCuryBal() -
                                           newtsr.getPortAppreciation());
                        //----------------------------------------------------
                        newtsr.setMPortAppreciation(YssD.round(YssD.mul(YssD.div(tsr.
                            getMPortCuryBal(), tsr.getStorageAmount())
                            , rs.getDouble("FTradeAmount"))
                            , 2));
                        tsr.setMPortCuryBal(tsr.getMPortCuryBal() -
                                            newtsr.getMPortAppreciation());
                        //----------------------------------------------------
                        newtsr.setVPortAppreciation(YssD.round(YssD.mul(YssD.div(tsr.
                            getVPortCuryBal(), tsr.getStorageAmount())
                            , rs.getDouble("FTradeAmount"))
                            , 2));
                        tsr.setVPortCuryBal(tsr.getVPortCuryBal() -
                                            newtsr.getVPortAppreciation());
                        //----------------------------------------------------
                    } else {
                        newtsr.setAppreciation(tsr.getBal());
                        newtsr.setMAppreciation(tsr.getMBal());
                        newtsr.setVAppreciation(tsr.getVBal());
                        newtsr.setBaseAppreciation(tsr.getBaseCuryBal());
                        newtsr.setMBaseAppreciation(tsr.getMBaseCuryBal());
                        newtsr.setVBaseAppreciation(tsr.getVBaseCuryBal());
                        newtsr.setPortAppreciation(tsr.getPortCuryBal());
                        newtsr.setMPortAppreciation(tsr.getMPortCuryBal());
                        newtsr.setVPortAppreciation(tsr.getVPortCuryBal());
                    }
                    tsr.setStorageAmount(tsr.getStorageAmount() -
                                         rs.getDouble("FTradeAmount"));
                    //----------------------------------------------------
                    newhmEveStg.put(newtsr.getNum() + "\f" + sKey, newtsr);
                }
            }
        } catch (Exception ex) {
            throw new YssException("修改HASH表数据出错" + "\r\n" + ex.getMessage(), ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return newhmEveStg;
    }

    //传值给证券应收应付实体类
    private SecPecPayBean setDatatoSecRecPay(TradeSellRelaPojo tsr) {
        SecPecPayBean secpecpay = new SecPecPayBean();
        secpecpay.setTransDate(workDate);
        secpecpay.setStrPortCode(tsr.getPortCode());
        secpecpay.setStrSecurityCode(tsr.getSecuritycode());
        secpecpay.setStrTsfTypeCode(tsr.getTsftypecode());
        secpecpay.setStrSubTsfTypeCode(tsr.getSubtsftypecode());
        secpecpay.setInvMgrCode(tsr.getAnalysisCode1());
        secpecpay.setBrokerCode(tsr.getAnalysisCode2());
        secpecpay.setStrCuryCode(tsr.getCuryCode());
        secpecpay.setCatTypeCode(" ");
        secpecpay.setBaseCuryRate(tsr.getBaseCuryRate());
        secpecpay.setPortCuryRate(tsr.getPortCuryRate());
        secpecpay.setMoney(tsr.getAppreciation());
        secpecpay.setMMoney(tsr.getMAppreciation());
        secpecpay.setVMoney(tsr.getVAppreciation());
        secpecpay.setBaseCuryMoney(tsr.getBaseAppreciation());
        secpecpay.setMBaseCuryMoney(tsr.getMBaseAppreciation());
        secpecpay.setVBaseCuryMoney(tsr.getVBaseAppreciation());
        secpecpay.setPortCuryMoney(tsr.getPortAppreciation());
        secpecpay.setMPortCuryMoney(tsr.getMPortAppreciation());
        secpecpay.setVPortCuryMoney(tsr.getVPortAppreciation());
        secpecpay.setCheckState(1);
        secpecpay.setInOutType( -1);
        return secpecpay;
    }

}
