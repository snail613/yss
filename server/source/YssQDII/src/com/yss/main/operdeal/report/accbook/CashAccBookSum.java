package com.yss.main.operdeal.report.accbook;

import java.sql.*;
import com.yss.util.*;
import com.yss.main.operdeal.report.accbook.pojo.*;
import com.yss.pojo.sys.*;
import com.yss.dsub.*;
import com.yss.main.cusreport.*;
import java.util.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.parasetting.PortfolioBean;

public class CashAccBookSum
    extends BaseAccBook {
    public CashAccBookSum() {
    }

    private String sPortCode = ""; //组合编号
    //如果统计应收应付库存时，Code 为调拨类型或者调拨子类型，则保存在变量中
    //private String sSubTsfType = "";
    //private String sTsfType = "";
    /**
     * 获取报表数据入口 （基类方法的实现）
     * @param sType String
     * @throws YssException
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        HashMap hmResult = null;
        ArrayList arrResult = null;
        try {

            hmResult = getResultHashTable(); // 得到数据
            arrResult = getOrderList(hmResult);
            sResult = buildRowCompResult(arrResult); //得到含格式的数据  父类中
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取现金汇总台帐出错： \n" + e.getMessage());
        }
    }

    /**
     * 将数据与报表数据源传入得到相应格式数据
     * @throws YssException
     */
    protected String buildRowCompResult(ArrayList arrResult) throws YssException {
        String strSql = "";

        String strReturn = "";
        ResultSet rs = null;
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";
        RepTabCellBean rtc = null;
        CashAccBookSumBean cashAccBook;
        try {
            for (int i = 0; i < arrResult.size(); i++) {
                cashAccBook = (CashAccBookSumBean) arrResult.get(i);
                hmCellStyle = getCellStyles("DsCashAcc001");
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                    " where FRepDsCode = " + dbl.sqlString("DsCashAcc001") +
                    " and FCheckState = 1 order by FOrderIndex";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    //获得样式
                    sKey = "DsCashAcc001" + "\tDSF\t-1\t" +
                        rs.getString("FOrderIndex");

                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");

                    buf.append(YssReflection.getPropertyValue(cashAccBook,
                        rs.getString("FDsField")) +
                               "\t");
                }
                dbl.closeResultSetFinal(rs);
                if (buf.toString().trim().length() > 1) {
                    strReturn = strReturn + buf.toString().substring(0,
                        buf.toString().length() - 1);
                    buf.delete(0, buf.toString().length());
                    strReturn = strReturn + "\r\n"; //每一个单元格用\r\n隔开
                }

            }
        } catch (Exception e) {
            throw new YssException("获取格式出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return strReturn;
    }

    /**
     * 从数据库中取数存入哈希表
     * @throws YssException
     */
    public HashMap getResultHashTable() throws YssException {
        HashMap hmResult = null;
        try {
            //应收应付现金台帐
            if (this.bIsRecPay) {
                hmResult = this.getRecPayHashTable();
            }
            //普通证券台帐
            else {
                hmResult = this.getCashHashTable();
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return hmResult;
    }

    /**
     * 从数据库中取数存入哈希表
     * 普通现金台帐
     * @throws YssException
     */
    public HashMap getCashHashTable() throws YssException {
        CashAccBookSumBean cashSum = null;
        CashAccBookSumBean cashTradeSum = null;
        ResultSet rs = null;
        String sKey = "";
        HashMap hmResult = new HashMap();
        double dbPortInMoney = 0;
        double dbPortOutMoney = 0;
        try {
            //得到期初库存
            rs = dbl.openResultSet(this.getStartCashSql(true).toString());
            while (rs.next()) {
                cashSum = new CashAccBookSumBean();
                cashSum.setCode(rs.getString("FAccCode"));
                cashSum.setName(rs.getString("FAccName"));
                cashSum.setCuryCode(rs.getString("FCuryCode"));
                cashSum.setInitialMoney(rs.getDouble("FAccBalance"));

                //如果有组合货币
                if (this.bIsPort) {
                    cashSum.setPortInitialCost(rs.getDouble("FStartAmount"));
                }

                sKey = cashSum.getCode() + "\f" + cashSum.getCuryCode();
                hmResult.put(sKey, cashSum);
            }
            dbl.closeResultSetFinal(rs);

            //得到调拨期间数据
            rs = dbl.openResultSet(this.getTransSql().toString());
            while (rs.next()) {
                sKey = "";
                cashTradeSum = null;
                sKey = rs.getString("FAccCode") + "\f" + rs.getString("FCuryCode");
                cashTradeSum = (CashAccBookSumBean) hmResult.get(sKey);
                if (cashTradeSum == null) {
                    cashTradeSum = new CashAccBookSumBean();
                }
                cashTradeSum.setCode(rs.getString("FAccCode"));
                cashTradeSum.setName(rs.getString("FAccName"));
                cashTradeSum.setCuryCode(rs.getString("FCuryCode"));
                cashTradeSum.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                //资金方向流入
                if (rs.getInt("FInOut") == 1) {
                    cashTradeSum.setInMoney(cashTradeSum.getInMoney() + rs.getDouble("FMoney"));
                }
                //资金方向流出
                else if (rs.getInt("FInOut") == -1) {
                    cashTradeSum.setOutMoney(cashTradeSum.getOutMoney() + rs.getDouble("FMoney"));
                }
                //如果有组合
                if (this.bIsPort) {
                    cashTradeSum.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                    //计算组合金额
                    //资金方向流入
                    if (rs.getInt("FInOut") == 1) {
                        dbPortInMoney = this.getSettingOper().calPortMoney(
                            rs.getDouble("FMoney"),
                            cashTradeSum.getBaseCuryRate(),
                            cashTradeSum.getPortCuryRate(),
                            //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            "", rs.getDate("FTransDate"), sPortCode);
                        cashTradeSum.setPortInMoney(cashTradeSum.getPortInMoney() + dbPortInMoney);
                    } else if (rs.getInt("FInOut") == -1) {
                        dbPortOutMoney = this.getSettingOper().calPortMoney(
                            rs.getDouble("FMoney"),
                            cashTradeSum.getBaseCuryRate(),
                            cashTradeSum.getPortCuryRate(),
                            //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            "", rs.getDate("FTransDate"), sPortCode);
                        cashTradeSum.setPortOutMoney(cashTradeSum.getPortOutMoney() + dbPortOutMoney);
                    }
                }
                hmResult.put(sKey, cashTradeSum);
            }
            dbl.closeResultSetFinal(rs);

            //得到期末数据
            rs = dbl.openResultSet(this.getStartCashSql(false).toString());
            while (rs.next()) {
                cashSum = null;
                sKey = "";
                sKey = rs.getString("FAccCode") + "\f" + rs.getString("FCuryCode");
                cashSum = (CashAccBookSumBean) hmResult.get(sKey);
                if (cashSum == null) {
                    cashSum = new CashAccBookSumBean();
                }
                cashSum.setCode(rs.getString("FAccCode"));
                cashSum.setName(rs.getString("FAccName"));
                cashSum.setCuryCode(rs.getString("FCuryCode"));
                cashSum.setPortRateFx(rs.getDouble("FFX"));
                //-------------------取汇率---------------------//
                //基础汇率
                BaseOperDeal operDeal = new BaseOperDeal();
                operDeal.setYssPub(pub);
                cashSum.setBaseCuryRate(
                    operDeal.getValCuryRate(this.dEndDate,
                                            cashSum.getCuryCode(),
                                            "", YssOperCons.YSS_RATEVAL_BASE));
                //组合汇率
                if (this.bIsPort) {
                    PortfolioBean port = new PortfolioBean();
                    port.setYssPub(pub);
                    port.setPortCode(this.sPortCode);
                    port.getSetting();
                    cashSum.setPortCuryRate(
                        operDeal.getValCuryRate(this.dEndDate,
                                                port.getCurrencyCode(),
                                                this.sPortCode,
                                                YssOperCons.YSS_RATEVAL_PORT));
                }
                //---------------------------------------------//
                hmResult.put(sKey, cashSum);
            }
        } catch (Exception e) {
            throw new YssException("证券台帐获取结果出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmResult;
    }

    /**
     * 从数据库中取数存入哈希表
     * 应收应付现金台帐
     * @throws YssException
     */
    public HashMap getRecPayHashTable() throws YssException {
        ResultSet rs = null;
        HashMap hmResult = new HashMap();
        String sHmKey = "";
        try {
            //----------------获取期初-------------//
            rs = dbl.openResultSet(this.getStartCashRecPaySql(true).toString());
            while (rs.next()) {
                sHmKey = "";
                CashAccBookSumBean cashBook = new CashAccBookSumBean();
                cashBook.setCode(rs.getString("FAccCode"));
                cashBook.setName(rs.getString("FAccName"));
                cashBook.setCuryCode(rs.getString("FCuryCode"));
                cashBook.setInitialMoney(rs.getDouble("FBal"));
                if (this.bIsPort) {
                    cashBook.setPortInitialCost(rs.getDouble("FPortCuryBal"));
                }
                sHmKey = cashBook.getCode() + "\f" + cashBook.getCuryCode();
                hmResult.put(sHmKey, cashBook);
            }
            dbl.closeResultSetFinal(rs);
            //-----------------------------------//
            //-------------获取台帐期间发生数据---------------//
            //取流入
            rs = dbl.openResultSet(this.getCashRecSql().toString());
            while (rs.next()) {
                CashAccBookSumBean cashBook = null;
                sHmKey = "";
                sHmKey = rs.getString("FAccCode") + "\f" + rs.getString("FCuryCode");
                cashBook = (CashAccBookSumBean) hmResult.get(sHmKey);
                if (cashBook == null) {
                    cashBook = new CashAccBookSumBean();
                }
                cashBook.setCode(rs.getString("FAccCode"));
                cashBook.setName(rs.getString("FAccName"));
                cashBook.setCuryCode(rs.getString("FCuryCode"));
                cashBook.setInMoney(rs.getDouble("FMoney"));
                if (this.bIsPort) {
                    cashBook.setPortInMoney(rs.getDouble("FPortCuryMoney"));
                }
                //-------------------取汇率---------------------//
                //基础汇率
                BaseOperDeal operDeal = new BaseOperDeal();
                operDeal.setYssPub(pub);
                cashBook.setBaseCuryRate(
                    operDeal.getValCuryRate(this.dEndDate,
                                            cashBook.getCuryCode(),
                                            "", YssOperCons.YSS_RATEVAL_BASE));
                //组合汇率
                if (this.bIsPort) {
                    PortfolioBean port = new PortfolioBean();
                    port.setYssPub(pub);
                    port.setPortCode(this.sPortCode);
                    port.getSetting();
                    cashBook.setPortCuryRate(
                        operDeal.getValCuryRate(this.dEndDate,
                                                port.getCurrencyCode(),
                                                this.sPortCode,
                                                YssOperCons.YSS_RATEVAL_PORT));
                }
                //---------------------------------------------//
                hmResult.put(sHmKey, cashBook);
            }
            //取流出
            rs = dbl.openResultSet(this.getCashPaySql().toString());
            while (rs.next()) {
                CashAccBookSumBean cashBook = null;
                sHmKey = "";
                sHmKey = rs.getString("FAccCode") + "\f" + rs.getString("FCuryCode");
                cashBook = (CashAccBookSumBean) hmResult.get(sHmKey);
                if (cashBook == null) {
                    cashBook = new CashAccBookSumBean();
                }
                cashBook.setCode(rs.getString("FAccCode"));
                cashBook.setName(rs.getString("FAccName"));
                cashBook.setCuryCode(rs.getString("FCuryCode"));
                cashBook.setOutMoney(rs.getDouble("FMoney"));
                if (this.bIsPort) {
                    cashBook.setPortOutMoney(rs.getDouble("FPortCuryMoney"));
                }
                //-------------------取汇率---------------------//
                //基础汇率
                BaseOperDeal operDeal = new BaseOperDeal();
                operDeal.setYssPub(pub);
                cashBook.setBaseCuryRate(
                    operDeal.getValCuryRate(this.dEndDate,
                                            cashBook.getCuryCode(),
                                            "", YssOperCons.YSS_RATEVAL_BASE));
                //组合汇率
                if (this.bIsPort) {
                    PortfolioBean port = new PortfolioBean();
                    port.setYssPub(pub);
                    port.setPortCode(this.sPortCode);
                    port.getSetting();
                    cashBook.setPortCuryRate(
                        operDeal.getValCuryRate(this.dEndDate,
                                                port.getCurrencyCode(),
                                                this.sPortCode,
                                                YssOperCons.YSS_RATEVAL_PORT));
                }
                //---------------------------------------------//
                hmResult.put(sHmKey, cashBook);
            }
            dbl.closeResultSetFinal(rs);
            //---------------------------------------------//
            //---------------获取期末汇兑损益----------------//
            //只有在取组合货币的情况下才有汇兑损益
            if (this.bIsPort) {
                rs = dbl.openResultSet(this.getStartCashRecPaySql(false).toString());
                while (rs.next()) {
                    sHmKey = "";
                    CashAccBookSumBean cashBook = null;
                    cashBook = (CashAccBookSumBean) hmResult.get(rs.getString("FAccCode") + "\f" + rs.getString("FCuryCode"));
                    if (cashBook == null) {
                        break;
                    }
                    cashBook.setPortRateFx(rs.getDouble("FFX"));
                }
                dbl.closeResultSetFinal(rs);
            }
            //---------------------------------------------//

        } catch (Exception e) {
            throw new YssException("现金台帐获取应收应付结果出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmResult;
    }

    /**
        /**
      * 排序和计算储存在结果哈希表中的数据
      * @throws YssException
      * @return ArrayList
      */
     public ArrayList getOrderList(HashMap hmResult) throws YssException {
         ArrayList arrResult = new ArrayList();
         java.util.Iterator it = null;
         CashAccBookSumBean cashSum = null;
         CashAccBookSumBean totalCashSum = new CashAccBookSumBean();
         YssMapAdmin mapAdmin = null;
         BaseComparator comparator = new BaseComparator();

         try {
             if (hmResult == null) {
                 return null;
             }
             //--------------------先排序---------------------//
             mapAdmin = new YssMapAdmin(hmResult, comparator);
             it = mapAdmin.sortMap().iterator();
             //----------------------------------------------//

             while (it.hasNext()) {
                 cashSum = (CashAccBookSumBean) it.next();
                 if (cashSum != null) {
                     //单条记录原币期末金额 = 期初 + 流入 - 流出
                     cashSum.setFinalMoney(cashSum.getInitialMoney() + cashSum.getInMoney() - cashSum.getOutMoney());
                     //单条记录本位币期末金额 = 本位币期初 + 本位币流入 + 汇兑损益 - 本位币流出
                     cashSum.setPortFinalCost(cashSum.getPortInitialCost() +
                                              cashSum.getPortInMoney() +
                                              cashSum.getPortRateFx() -
                                              cashSum.getPortOutMoney());
                     //合计期初金额
                     totalCashSum.setInitialMoney(totalCashSum.getInitialMoney() + cashSum.getInitialMoney());
                     //合计流入金额
                     totalCashSum.setInMoney(totalCashSum.getInMoney() + cashSum.getInMoney());
                     //合计流出金额
                     totalCashSum.setOutMoney(totalCashSum.getOutMoney() + cashSum.getOutMoney());
                     //合计期末金额
                     totalCashSum.setFinalMoney(totalCashSum.getFinalMoney() + cashSum.getFinalMoney());
                     //合计本位币期初金额
                     totalCashSum.setPortInitialCost(totalCashSum.getPortInitialCost() + cashSum.getPortInitialCost());
                     //合计本位币流入金额
                     totalCashSum.setPortInMoney(totalCashSum.getPortInMoney() + cashSum.getPortInMoney());
                     //合计本位币流出金额
                     totalCashSum.setPortOutMoney(totalCashSum.getPortOutMoney() + cashSum.getPortOutMoney());
                     //合计汇兑损益
                     totalCashSum.setPortRateFx(totalCashSum.getPortRateFx() + cashSum.getPortRateFx());
                     //合计本位币期末金额
                     totalCashSum.setPortFinalCost(totalCashSum.getPortFinalCost() + cashSum.getPortFinalCost());
                     arrResult.add(cashSum);
                 }
             }
             totalCashSum.setCuryCode("合计：");
             arrResult.add(totalCashSum);
         } catch (Exception e) {
             throw new YssException("现金台帐序列化台帐哈希表出错！", e);
         }
         return arrResult;
     }

    /**
     * 获取 现金应收应付台帐期初 或 期末 库存的 SQL 语句
     * @param bIsBegin: 统计期初为 true, 统计期末为 false, 期末可取汇兑损益
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getStartCashRecPaySql(boolean bIsBegin) throws YssException {
        StringBuffer bufSql = new StringBuffer(1700);
        String sSelectFiled = ""; //拼接查询字段
        String sGroupFiled = ""; //分组字段
        String sTableRele = ""; //关联表 SQL 语句
        String sWhereFiled = ""; //Where 条件过滤
        String invmgrSecField = "";
        try {
            sSelectFiled = ( (String)this.hmFieldIndRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1])) +
                "Code" + " AS FAccCode";
            sGroupFiled = (String)this.hmFieldRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);

            if (sSelectFiled.indexOf("FSubAccTypeCode") != -1 ||
                sSelectFiled.indexOf("FAccTypeCode") != -1) {
                sSelectFiled = sSelectFiled.replaceAll("FSubAccTypeCode",
                    "FSubAccType");
                sSelectFiled = sSelectFiled.replaceAll("FAccTypeCode", "FAccType");
            }

            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //当取期末汇兑损益的时候，如果条件中有调拨类型和调拨子类型要替换为相应的汇兑损益代码
                if (!bIsBegin) {
                    if (this.aryAccBookDefine[i].equalsIgnoreCase("FTsfTypeCode")) {
                        sWhereFiled = sWhereFiled + " AND " +
                            (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                            " = " +
                            dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX);
                        continue;
                    }
                    if (this.aryAccBookDefine[i].equalsIgnoreCase("FSubTsfTypeCode")) {
                        sWhereFiled = sWhereFiled + " AND " +
                            (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                            " = " +
                            dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX + this.aryAccBookLink[i + 1]);
                        continue;
                    }
                }
                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }
            if (sSelectFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode",
                    invmgrSecField);
            }

            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);

            bufSql.append("SELECT a1.*, z.FAccName");
            bufSql.append(" FROM (SELECT SUM(FBal) AS FBal,");
            bufSql.append(" SUM(FPortCuryBal) AS FPortCuryBal,");
            bufSql.append(" SUM(c.FFX) AS FFX,");
            bufSql.append(" FCuryCode,");
            bufSql.append(sSelectFiled);
            bufSql.append(" FROM (SELECT FYearMonth,");
            bufSql.append(" FStorageDate,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FCashAccCode,");
            bufSql.append(" FTsfTypeCode,");
            bufSql.append(" FSubTsfTypeCode,");
            bufSql.append(" FCuryCode,");
            bufSql.append(" FBal,");
            bufSql.append(" FPortCuryBal,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Stock_CashPayRec")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
            bufSql.append(" WHERE SUBSTR(FYearMonth, 5, 2) <> '00'");
            bufSql.append(" AND FTsfTypeCode in ('07', '06')) a");
            bufSql.append(" JOIN (SELECT FCashAccCode AS FCashCode,");
            bufSql.append(" FCashAccName,");
            bufSql.append(" FAccType,");
            bufSql.append(" FSubAccType,");
            bufSql.append(" FBankCode");
            bufSql.append(" FROM ");
            bufSql.append(pub.yssGetTableName("TB_Para_CashAccount")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
            bufSql.append(") b ON a.FCashAccCode =");
            bufSql.append(" b.FCashCode");
            //---------------取汇兑损益--------------//
            bufSql.append(" LEFT JOIN (SELECT FStorageDate AS FXStorageDate,");
            bufSql.append(" FPortCode AS FXPortCode,");
            bufSql.append(" FAnalysisCode1 AS FXAnalysisCode1,");
            bufSql.append(" FAnalysisCode2 AS FXAnalysisCode2,");
            bufSql.append(" FAnalysisCode3 AS FXAnalysisCode3,");
            bufSql.append(" FCashAccCode AS FXCashAccCode,");
            bufSql.append(" FSubTsfTypeCode AS FXSubTsfTypeCode,");
            bufSql.append(" FPortCuryBal AS FFX");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Stock_CashPayRec")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
            bufSql.append(" WHERE SUBSTR(FYearMonth, 5, 2) <> '00') c ON a.FStorageDate = c.FXStorageDate");
            bufSql.append(" AND a.FPortCode = c.FXPortCode");
            bufSql.append(" AND a.FAnalysisCode1 = c.FXAnalysisCode1");
            bufSql.append(" AND a.FAnalysisCode2 = c.FXAnalysisCode2");
            bufSql.append(" AND a.FAnalysisCode3 = c.FXAnalysisCode3");
            bufSql.append(" AND a.FCashAccCode = c.FXCashAccCode");
            bufSql.append(" AND (" + dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) +
                          " || a.FSubTsfTypeCode) = c.FXSubTsfTypeCode");
            //--------------------------------------//
            bufSql.append(" WHERE a.FCheckState = 1");
            if (bIsBegin) {
                bufSql.append(" AND " + this.operSql.sqlStoragEve(dBeginDate));
            } else {
                bufSql.append(" AND FStorageDate = " + dbl.sqlDate(this.dEndDate));
            }
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY FCuryCode, " + sGroupFiled + " ) a1");
            bufSql.append(" LEFT JOIN " + sTableRele);
        } catch (Exception e) {
            throw new YssException("获取现金应收应付台帐期初库存的 SQL 语句出错！", e);
        }
        return bufSql;
    }

    /**
     * 现金应收台帐获取流入数据的 SQL 语句
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getCashRecSql() throws YssException {
        StringBuffer bufSql = new StringBuffer(1100);
        String sSelectFiled = ""; //拼接查询字段
        String sGroupFiled = ""; //分组字段
        String sTableRele = ""; //关联表 SQL 语句
        String sWhereFiled = ""; //Where 条件过滤
        String invmgrSecField = "";
        try {
            sSelectFiled = ( (String)this.hmFieldIndRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1])) +
                "Code" + " AS FAccCode";

            sGroupFiled = (String)this.hmFieldRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);

            if (sSelectFiled.indexOf("FSubAccTypeCode") != -1 ||
                sSelectFiled.indexOf("FAccTypeCode") != -1) {
                sSelectFiled = sSelectFiled.replaceAll("FSubAccTypeCode",
                    "FSubAccType");
                sSelectFiled = sSelectFiled.replaceAll("FAccTypeCode", "FAccType");
            }

            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //取链接中的组合编号，在取汇率时需要使用
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Port")) {
                    sPortCode = this.aryAccBookLink[i + 1];
                }
                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }
            if (sSelectFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode",
                    invmgrSecField);
            }

            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);

            bufSql.append("SELECT a1.FMoney,");
            bufSql.append(" a1.FPortCuryMoney,");
            bufSql.append(" a1.FCuryCode,");
            bufSql.append(" a1.FAccCode,");
            bufSql.append(" z.FAccName");
            bufSql.append(" FROM (SELECT SUM(FMoney) AS FMoney,");
            bufSql.append(" SUM(FPortCuryMoney) AS FPortCuryMoney,");
            bufSql.append(" FCuryCode,");
            bufSql.append(sSelectFiled);
            bufSql.append(" FROM (SELECT FTransDate,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FCashAccCode AS CashCode,");
            bufSql.append(" FTsfTypeCode,");
            bufSql.append(" FSubTsfTypeCode,");
            bufSql.append(" FCuryCode,");
            bufSql.append(" FMoney,");
            bufSql.append(" FPortCuryMoney,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_CashPayRec")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
            bufSql.append(" WHERE FTsfTypeCode IN (" + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) +
                          "," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + ")) a");
            bufSql.append(" JOIN (SELECT FCashAccCode, FAccType, FSubAccType, FBankCode");
            bufSql.append(" FROM ").append(pub.yssGetTableName("TB_Para_CashAccount")).append(") b ON a.CashCode ="); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
            bufSql.append(" b.FCashAccCode");
            bufSql.append(" WHERE a.FCheckState = 1");
            bufSql.append(" AND FTransDate BETWEEN " + dbl.sqlDate(this.dBeginDate) + " AND ");
            bufSql.append(dbl.sqlDate(this.dEndDate));
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY FCuryCode, " + sGroupFiled + " ) a1");
            bufSql.append(" LEFT JOIN " + sTableRele);
        } catch (Exception e) {
            throw new YssException("现金应收应付台帐获取流入数据的 SQL 语句出错！", e);
        }
        return bufSql;
    }

    /**
     * 现金应付台帐获取流入数据的 SQL 语句
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getCashPaySql() throws YssException {
        StringBuffer bufSql = new StringBuffer(1300);
        String sSelectFiled = ""; //拼接查询字段
        String sGroupFiled = ""; //分组字段
        String sTableRele = ""; //关联表 SQL 语句
        String sWhereFiled = ""; //Where 条件过滤
        String invmgrSecField = "";
        //判断是否以调拨类型或者调拨子类型作为 SELECT 字段
        boolean bIsTsfType = false;
        boolean bIsSubTsfType = false;
        try {
            sSelectFiled = ( (String)this.hmFieldIndRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1])) +
                "Code" + " AS FAccCode";
            if (sSelectFiled.indexOf("FTsfTypeCode") != -1) {
                sSelectFiled = "";
                sSelectFiled = " CASE WHEN FTsfTypeCode = " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) +
                    " THEN " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) +
                    " WHEN FTsfTypeCode = " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) +
                    " THEN " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) +
                    " END AS FAccCode";
            }
            if (sSelectFiled.indexOf("FSubTsfTypeCode") != -1) {
                sSelectFiled = "";
                sSelectFiled = " CASE WHEN " + dbl.sqlSubStr("FSubTsfTypeCode", "1", "2") +
                    " = " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) +
                    " THEN " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + " || " +
                    dbl.sqlSubStr("FSubTsfTypeCode", "3") +
                    " WHEN " + dbl.sqlSubStr("FSubTsfTypeCode", "1", "2") + " = " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + " THEN " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + " || " +
                    dbl.sqlSubStr("FSubTsfTypeCode", "3") +
                    " END AS FAccCode";
            }

            sGroupFiled = (String)this.hmFieldRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);

            if (sSelectFiled.indexOf("FSubAccTypeCode") != -1 ||
                sSelectFiled.indexOf("FAccTypeCode") != -1) {
                sSelectFiled = sSelectFiled.replaceAll("FSubAccTypeCode",
                    "FSubAccType");
                sSelectFiled = sSelectFiled.replaceAll("FAccTypeCode", "FAccType");
            }

            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //取流出数据时，如果条件中有调拨类型或者调拨子类型
                if (this.aryAccBookDefine[i].equalsIgnoreCase("TsfType") ||
                    this.aryAccBookDefine[i].equalsIgnoreCase("SubTsfType")) {
                    //如果类型为“应收”则使用“收入”替代
                    if (this.aryAccBookLink[i +
                        1].substring(0,
                                     2).equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Rec)) {
                        sWhereFiled = sWhereFiled + " AND " +
                            (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                            " = " +
                            dbl.sqlString(this.aryAccBookLink[i +
                                          1].replaceAll(YssOperCons.
                            YSS_ZJDBLX_Rec, YssOperCons.YSS_ZJDBLX_Income));
                        continue;
                    }
                    //如果类型为“应付”则使用“费用”替代
                    if (this.aryAccBookLink[i +
                        1].substring(0,
                                     2).equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Pay)) {
                        sWhereFiled = sWhereFiled + " AND " +
                            (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                            " = " +
                            dbl.sqlString(this.aryAccBookLink[i +
                                          1].replaceAll(YssOperCons.
                            YSS_ZJDBLX_Pay, YssOperCons.YSS_ZJDBLX_Fee));
                        continue;
                    }

                }
                //取链接中的组合编号，在取汇率时需要使用
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Port")) {
                    sPortCode = this.aryAccBookLink[i + 1];
                }
                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }
            if (sSelectFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode",
                    invmgrSecField);
            }

            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);

            bufSql.append("SELECT a1.FMoney,");
            bufSql.append(" a1.FPortCuryMoney,");
            bufSql.append(" a1.FCuryCode,");
            bufSql.append(" a1.FAccCode,");
            bufSql.append(" z.FAccName");
            bufSql.append(" FROM (SELECT SUM(FMoney) AS FMoney,");
            bufSql.append(" SUM(FPortCuryMoney) AS FPortCuryMoney,");
            bufSql.append(" FCuryCode,");
            bufSql.append(sSelectFiled);
            bufSql.append(" FROM (SELECT FTransDate,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FCashAccCode AS CashCode,");
            bufSql.append(" FTsfTypeCode,");
            bufSql.append(" FSubTsfTypeCode,");
            bufSql.append(" FCuryCode,");
            bufSql.append(" FMoney,");
            bufSql.append(" FPortCuryMoney,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_CashPayRec")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
            bufSql.append(" WHERE FTsfTypeCode IN (" +
                          dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) +
                          "," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + ")");
            bufSql.append(" AND (FSubTsfTypeCode LIKE '02%' OR");
            bufSql.append(" FSubTsfTypeCode LIKE '03%')) a");
            bufSql.append(" JOIN (SELECT FCashAccCode, FAccType, FSubAccType, FBankCode");
            bufSql.append(" FROM ").append(pub.yssGetTableName("TB_Para_CashAccount")).append(") b ON a.CashCode ="); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
            bufSql.append(" b.FCashAccCode");
            bufSql.append(" WHERE a.FCheckState = 1");
            bufSql.append(" AND FTransDate BETWEEN " + dbl.sqlDate(this.dBeginDate) + " AND ");
            bufSql.append(dbl.sqlDate(this.dEndDate));
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY FCuryCode, " + sGroupFiled + " ) a1");
            bufSql.append(" LEFT JOIN " + sTableRele);

        } catch (Exception e) {
            throw new YssException("现金应付汇总台帐获取流入数据的 SQL 语句出错！", e);
        }
        return bufSql;
    }

    /**
     * 获取 现金台帐期初、期末信息 SQL
     * true 为期初  false 为期末
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getStartCashSql(boolean bIsBegin) throws YssException {
        StringBuffer bufSql = new StringBuffer(1000);
        String sSelectFiled = ""; //拼接查询字段
        String sGroupFiled = ""; //分组字段
        String sTableRele = ""; //关联表 SQL 语句
        String sWhereFiled = ""; //Where 条件过滤
        String sRPWhereFiled = ""; //应收应付的 Where 条件
        String invmgrSecField = "";
        //------------------------
        try {
            sSelectFiled = ( (String)this.hmFieldIndRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1])) +
                "Code" + " AS FAccCode";
            sGroupFiled = (String)this.hmFieldRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);

            if (sSelectFiled.indexOf("FSubAccTypeCode") != -1 ||
                sSelectFiled.indexOf("FAccTypeCode") != -1) {
                sSelectFiled = sSelectFiled.replaceAll("FSubAccTypeCode", "FSubAccType");
                sSelectFiled = sSelectFiled.replaceAll("FAccTypeCode", "FAccType");
            }

            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {

                //如果链接中有投资组合
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Port")) {
                    sRPWhereFiled = " AND " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " + dbl.sqlString(this.aryAccBookLink[i + 1]);
                    sPortCode = this.aryAccBookLink[i + 1];
                }

                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }
            if (sSelectFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode",
                    invmgrSecField);
            }

            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);
            //拼接 SQL
            bufSql.append("SELECT a1.*, a1.FAccCode, z.FAccName");
            bufSql.append(" FROM (SELECT " + sSelectFiled + ",");
            bufSql.append(" FCuryCode,");
            bufSql.append(" SUM(FAccBalance) AS FAccBalance,");
            bufSql.append(" SUM(FPortCuryBal) AS FStartAmount,");
            bufSql.append(" SUM(FFX) AS FFX");
            bufSql.append(" FROM (SELECT a.*, ");
            bufSql.append(" b.FAccType,");
            bufSql.append(" b.FSubAccType,");
            bufSql.append(" b.FBankCode,");
            bufSql.append(" d.FFX");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_Cash") +
                          " a");
            bufSql.append(
                " LEFT JOIN (SELECT FCashAccCode, FAccType, FSubAccType, FBankCode");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Para_CashAccount") +
                          " ) b ON a.FCashAccCode =");
            bufSql.append(" b.FCashAccCode");
            //---------------------取汇兑损益-----------------//
            bufSql.append(" LEFT JOIN (SELECT SUM(FPortCuryBal) AS FFX,");
            bufSql.append(" FCashAccCode,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FStorageDate,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_CashPayRec"));
            bufSql.append(" WHERE " + dbl.sqlSubStr("FYearMonth", "5", "2") +
                          " <> '00'");
            bufSql.append(" AND FTsfTypeCode = '99' and (FSubTsfTypeCode like '9905%' or FSubTsfTypeCode like '9909%')");
            bufSql.append(sRPWhereFiled);
            bufSql.append("GROUP BY ");
            bufSql.append(" FCashAccCode,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FStorageDate,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3");
            bufSql.append(" ) d ON d.FCashAccCode = a.FCashAccCode AND");
            bufSql.append(" d.FPortCode = a.FPortCode AND");
            bufSql.append(" a.FStorageDate = d.FStorageDate AND");
            bufSql.append(" a.FAnalysisCode1 = d.FAnalysisCode1 AND");
            bufSql.append(" a.FAnalysisCode2 = d.FAnalysisCode2 AND");
            bufSql.append(" a.FAnalysisCode3 = d.FAnalysisCode3");
            bufSql.append(" ) a");
            //-------------------------------------------------//
            bufSql.append(" WHERE FCheckState = 1");
            //判断时间取期初还是期末
            if (bIsBegin) {
                //期初取前一天的时间
                bufSql.append(" AND " + this.operSql.sqlStoragEve(dBeginDate));
            } else {
                //期末取最后日期
                bufSql.append(" AND FStorageDate = " + dbl.sqlDate(this.dEndDate));
            }
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY FCuryCode, " + sGroupFiled + ") a1");
            bufSql.append(" LEFT JOIN " + sTableRele);
        } catch (Exception e) {
            throw new YssException("现金台帐获取期初 SQL 语句出错！", e);
        }
        return bufSql;
    }

    /**
     * 现金台帐 获取交易期间的 SQL 语句
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getTransSql() throws YssException {
        StringBuffer transSql = new StringBuffer();
        String sSelectFiled = "";
        String sWhereFiled = ""; //Where 条件过滤
        String sTableRele = ""; //关联表 SQL 语句
        String invmgrSecField = "";
        try {
            sSelectFiled = ( (String)this.hmFieldIndRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1])) +
                "Code" + " AS FAccCode";

            if (sSelectFiled.indexOf("FSubAccTypeCode") != -1 ||
                sSelectFiled.indexOf("FAccTypeCode") != -1) {
                sSelectFiled = sSelectFiled.replaceAll("FSubAccTypeCode", "FSubAccType");
                sSelectFiled = sSelectFiled.replaceAll("FAccTypeCode", "FAccType");
            }

            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //如果链接中有投资组合
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Port")) {
                    sPortCode = this.aryAccBookLink[i + 1];
                }

                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }

            if (sSelectFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode",
                    invmgrSecField);
            }

            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);

            transSql.append("SELECT a1.*, z.FAccName");
            transSql.append(" FROM (SELECT b.*,");
            transSql.append(" a.FTransferDate,");
            transSql.append(" a.FTransDate,");
            transSql.append(" a.FTradeNum,");
            transSql.append(" c.FCuryCode,");
            transSql.append(" c.FBankCode,");
            transSql.append(sSelectFiled);
            transSql.append(" FROM (SELECT FNum,");
            transSql.append(" FTransferDate,");
            transSql.append(" FTransDate,");
            transSql.append(" FTradeNum,");
            transSql.append(" FCheckState");
            transSql.append(" FROM ").append(pub.yssGetTableName("Tb_Cash_Transfer")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
            transSql.append(" WHERE FCheckState = 1) a");
            transSql.append(" LEFT JOIN (SELECT FNum,");
            transSql.append(" FSubNum,");
            transSql.append(" FInOut,");
            transSql.append(" FPortCode,");
            transSql.append(" FAnalysisCode1,");
            transSql.append(" FAnalysisCode2,");
            transSql.append(" FAnalysisCode3,");
            transSql.append(" FCashAccCode,");
            transSql.append(" FMoney,");
            transSql.append(" FBaseCuryRate,");
            transSql.append(" FPortCuryRate,");
            transSql.append(" FCheckState");
            transSql.append(" FROM ").append(pub.yssGetTableName("Tb_Cash_SubTransfer")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
            transSql.append(" WHERE FCheckState = 1) b ON a.FNum = b.FNum");
            transSql.append(" LEFT JOIN (SELECT FCashAccCode AS FCode, FCuryCode, FBankCode, FAccType, FSubAccType");
            transSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_CashAccount")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
            transSql.append(") c ON b.FCashAccCode = c.FCode");
            transSql.append(" WHERE FTransferDate BETWEEN " + dbl.sqlDate(this.dBeginDate));
            transSql.append(" AND " + dbl.sqlDate(this.dEndDate));
            transSql.append(sWhereFiled);
            transSql.append(" ORDER BY b.FNum, b.FSubNum) a1");
            transSql.append(" LEFT JOIN ");
            transSql.append(sTableRele);
        } catch (Exception e) {
            throw new YssException("现金台帐获取交易期间的 SQL 语句出错！", e);
        }
        return transSql;

    }

    /**
     * 完成哈希表的初始化工作
     * @throws YssException
     */
    public void initHashTable() throws YssException {

        String invmgrField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.
            YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_InvMgr);
        String catField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.
            YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_CatType);

        this.hmSelectField = new HashMap();
        this.hmSelectField.put("Acc", "FCashAccCode as AccCode"); //帐户
        this.hmSelectField.put("Bank", "FBankCode as as AccCode"); //银行
        this.hmSelectField.put("CatType", catField + " as AccCode"); //投资品种
        this.hmSelectField.put("Cury", "FCuryCode as AccCode"); //货币
        this.hmSelectField.put("InvMgr", invmgrField + " as AccCode"); //投资经理
        this.hmSelectField.put("Port", "FPortCode as AccCode"); //投资组合

        this.hmSelectField.put("AccType", "FAccType  as AccCode"); //帐户类型
        this.hmSelectField.put("SubAccType", "FSubAccType  as AccCode"); //帐户子类型
        this.hmSelectField.put("TsfType", "FTsfTypeCode  as AccCode"); //调拨类型
        this.hmSelectField.put("SubTsfType", "FSubTsfTypeCode  as AccCode"); //调拨子类型

        this.hmFieldRela = new HashMap();
        hmFieldRela.put("InvMgr", invmgrField);
        hmFieldRela.put("Port", "FPortCode");
        hmFieldRela.put("Acc", "FCashAccCode");
        hmFieldRela.put("Cury", "FCuryCode");
        hmFieldRela.put("Bank", "FBankCode");
        hmFieldRela.put("CatType", catField);

        this.hmFieldRela.put("AccType", "FAccType"); //帐户类型
        this.hmFieldRela.put("SubAccType", "FSubAccType"); //帐户子类型
        this.hmFieldRela.put("TsfType", "FTsfTypeCode"); //调拨类型
        this.hmFieldRela.put("SubTsfType", "FSubTsfTypeCode"); //调拨子类型

        hmFieldIndRela = new HashMap();
        hmFieldIndRela.put("InvMgr", "FInvMgr");
        hmFieldIndRela.put("CatType", "FCat");
        hmFieldIndRela.put("Port", "FPort");
        hmFieldIndRela.put("Acc", "FCashAcc");
        hmFieldIndRela.put("Bank", "FBank");
        hmFieldIndRela.put("Cury", "FCury");
        hmFieldIndRela.put("AccType", "FAccType"); //帐户类型
        hmFieldIndRela.put("SubAccType", "FSubAccType"); //帐户子类型
        hmFieldIndRela.put("TsfType", "FTsfType"); //调拨类型
        hmFieldIndRela.put("SubTsfType", "FSubTsfType"); //调拨子类型

        hmTableRela = new HashMap();
        hmTableRela.put("InvMgr",
                        "(select FInvMgrCode,FInvMgrName as FAccName from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FCheckState = 1) z on a1.FAccCode = z.FInvMgrCode");
        hmTableRela.put("Port",
                        "(select FPortCode,FPortName as FAccName from " +
                        pub.yssGetTableName("Tb_Para_Portfolio") +
                        " where FCheckState = 1) z on a1.FAccCode = z.FPortCode");
        hmTableRela.put("Acc",
                        "(select FCashAccCode,FCashAccName as FAccName from " +
                        pub.yssGetTableName("tb_para_cashaccount") +
                        " where FCheckState = 1) z on a1.FAccCode = z.FCashAccCode");
        hmTableRela.put("Bank",
                        "(select FBankCode,FBankName as FAccName from " +
                        pub.yssGetTableName("Tb_Para_Bank") +
                        " where FCheckState = 1) z on a1.FAccCode = z.FBankCode");
        hmTableRela.put("Cury",
                        "(select FCuryCode,FCuryName as FAccName from " +
                        pub.yssGetTableName("Tb_Para_Currency") +
                        " where FCheckState = 1) z on a1.FAccCode = z.FCuryCode");

        hmTableRela.put("AccType",
                        "(select FAccTypeCode,FAccTypeName as FAccName from Tb_Base_AccountType" +
                        " where FCheckState = 1) z on a1.FAccCode = z.FAccTypeCode");

        hmTableRela.put("SubAccType",
                        "(select FSubAccTypeCode,FSubAccTypeName as FAccName from Tb_Base_SubAccountType" +
                        " where FCheckState = 1) z on a1.FAccCode = z.FSubAccTypeCode");

        hmTableRela.put("TsfType",
                        "(select FTsfTypeCode,FTsfTypeName as FAccName from Tb_Base_TransferType " +
                        " where FCheckState = 1) z on a1.FAccCode = z.FTsfTypeCode");

        hmTableRela.put("SubTsfType",
                        "(select FSubTsfTypeCode,FSubTsfTypeName as FAccName from Tb_Base_SubTransferType " +
                        " where FCheckState = 1) z on a1.FAccCode = z.FSubTsfTypeCode");

    }
}
