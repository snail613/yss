package com.yss.main.operdeal.report.accbook.secbook;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.ArrayList;

import com.yss.util.*;
import com.yss.main.operdeal.report.accbook.pojo.*;
import com.yss.pojo.sys.YssMapAdmin;
import com.yss.dsub.BaseComparator;
import com.yss.main.cusreport.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.operdeal.report.accbook.BaseAccBook;

public class SecBookSumRecPay
    extends BaseAccBook {

    private String sPortCode = ""; //条件中的组合编号

    public SecBookSumRecPay() {
    }

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
            hmResult = getRecPayResultHashTable(); // 得到数据
            arrResult = getOrderList(hmResult);
            sResult = buildRowCompResult(arrResult); //得到含格式的数据  父类中
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取证券应收应付汇总台帐出错： \n" + e.getMessage());
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
        SecBookSumRecPayBean secAccBook;
        try {
            for (int i = 0; i < arrResult.size(); i++) {
                secAccBook = (SecBookSumRecPayBean) arrResult.get(i);
                hmCellStyle = getCellStyles("DsSecAccRecPay001");
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                    " where FRepDsCode = " + dbl.sqlString("DsSecAccRecPay001") +
                    " and FCheckState = 1 order by FOrderIndex";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    //获得样式
                    sKey = "DsSecAccRecPay001" + "\tDSF\t-1\t" +
                        rs.getString("FOrderIndex");

                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");

                    buf.append(YssReflection.getPropertyValue(secAccBook,
                        rs.getString("FDsField")) +
                               "\t");
                }
                dbl.closeResultSetFinal(rs);
                if (buf.toString().trim().length() > 1) {
                    strReturn = strReturn + buf.toString().substring(0,
                        buf.toString().length() - 1);
                    buf.delete(0, buf.toString().length()); //将BUF 清空
                    strReturn = strReturn + "\r\n"; //每一个行用\r\n隔开
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
     * 将证券应收应付台账数据装入哈希表
     * @throws YssException
     * @return HashMap
     */
    public HashMap getRecPayResultHashTable() throws YssException {
        ResultSet rs = null;
        SecBookSumRecPayBean secAccBook = null;
        String sHmRecPayKey = "";
        HashMap hmResult = new HashMap();
        try {
            //-----------------取应收应付期初----------------//
            rs = dbl.openResultSet(this.getStartRecPayStorageSql(true).toString());
            while (rs.next()) {
                secAccBook = new SecBookSumRecPayBean();
                secAccBook.setKeyCode(rs.getString("FAccCode"));
                secAccBook.setKeyName(rs.getString("FAccName"));
                secAccBook.setCuryCode(rs.getString("FCuryCode"));
                secAccBook.setBeginMoney(rs.getDouble("FBal"));
                if (this.bIsPort) {
                    secAccBook.setPortBeginMoney(rs.getDouble("FPortCuryBal"));
                }
                sHmRecPayKey = secAccBook.getKeyCode() + "\f" +
                    secAccBook.getCuryCode();
                //-------------------取汇率---------------------//
                //基础汇率
                BaseOperDeal operDeal = new BaseOperDeal();
                operDeal.setYssPub(pub);
                secAccBook.setBaseCuryRate(
                    operDeal.getValCuryRate(this.dEndDate,
                                            secAccBook.getCuryCode(),
                                            "", YssOperCons.YSS_RATEVAL_BASE));
                //组合汇率
                if (this.bIsPort) {
                    PortfolioBean port = new PortfolioBean();
                    port.setYssPub(pub);
                    port.setPortCode(this.sPortCode);
                    port.getSetting();
                    secAccBook.setPortCuryRate(
                        operDeal.getValCuryRate(this.dEndDate,
                                                port.getCurrencyCode(),
                                                this.sPortCode,
                                                YssOperCons.YSS_RATEVAL_PORT));
                }
                //---------------------------------------------//

                hmResult.put(sHmRecPayKey, secAccBook);
            }
            dbl.closeResultSetFinal(rs);
            //--------------------------------------------//
            //----------------取交易期间发生额----------------//
            //取应收
            rs = dbl.openResultSet(this.getTradeRecSql().toString());
            while (rs.next()) {
                sHmRecPayKey = "";
                sHmRecPayKey = rs.getString("FAccCode") + "\f" +
                    rs.getString("FCuryCode");
                SecBookSumRecPayBean recRecAccBook = null;
                recRecAccBook = (SecBookSumRecPayBean) hmResult.get(sHmRecPayKey);
                if (recRecAccBook == null) {
                    recRecAccBook = new SecBookSumRecPayBean();
                }
                recRecAccBook.setKeyCode(rs.getString("FAccCode"));
                recRecAccBook.setKeyName(rs.getString("FAccName"));
                recRecAccBook.setCuryCode(rs.getString("FCuryCode"));

                recRecAccBook.setInMoney(rs.getDouble("FMoney"));
                if (this.bIsPort) {
                    recRecAccBook.setPortInMoney(rs.getDouble("FPortCuryMoney"));
                }
                //-------------------取汇率---------------------//
                //基础汇率
                BaseOperDeal operDeal = new BaseOperDeal();
                operDeal.setYssPub(pub);
                recRecAccBook.setBaseCuryRate(
                    operDeal.getValCuryRate(this.dEndDate,
                                            recRecAccBook.getCuryCode(),
                                            "", YssOperCons.YSS_RATEVAL_BASE));
                //组合汇率
                if (this.bIsPort) {
                    PortfolioBean port = new PortfolioBean();
                    port.setYssPub(pub);
                    port.setPortCode(this.sPortCode);
                    port.getSetting();
                    recRecAccBook.setPortCuryRate(
                        operDeal.getValCuryRate(this.dEndDate,
                                                port.getCurrencyCode(),
                                                this.sPortCode,
                                                YssOperCons.YSS_RATEVAL_PORT));
                }
                //---------------------------------------------//
                hmResult.put(sHmRecPayKey, recRecAccBook);
            }
            dbl.closeResultSetFinal(rs);
            //取应付
            StringBuffer bufPaySql = this.getTradePaySql();
            if (bufPaySql.length() != 0) {
                rs = dbl.openResultSet(bufPaySql.toString());
                while (rs.next()) {
                    sHmRecPayKey = "";
                    sHmRecPayKey = rs.getString("FAccCode") + "\f" +
                        rs.getString("FCuryCode");
                    SecBookSumRecPayBean recPayAccBook = null;
                    recPayAccBook = (SecBookSumRecPayBean) hmResult.get(sHmRecPayKey);
                    if (recPayAccBook == null) {
                        recPayAccBook = new SecBookSumRecPayBean();
                    }
                    recPayAccBook.setKeyCode(rs.getString("FAccCode"));
                    recPayAccBook.setKeyName(rs.getString("FAccName"));
                    recPayAccBook.setCuryCode(rs.getString("FCuryCode"));

                    recPayAccBook.setOutMoney(rs.getDouble("FMoney"));
                    if (this.bIsPort) {
                        recPayAccBook.setPortOutMoney(rs.getDouble("FPortCuryMoney"));
                    }
                    hmResult.put(sHmRecPayKey, recPayAccBook);
                }
                dbl.closeResultSetFinal(rs);
            }
            //--------------------------------------------//
            //--------------取期末汇兑损益------------------//
            //在有组合的情况下才有汇兑损益
            if (this.bIsPort) {
                rs = dbl.openResultSet(this.getStartRecPayStorageSql(false).
                                       toString());
                while (rs.next()) {
                    sHmRecPayKey = "";
                    sHmRecPayKey = rs.getString("FAccCode") + "\f" +
                        rs.getString("FCuryCode");
                    SecBookSumRecPayBean recPayAccBook = null;
                    recPayAccBook = (SecBookSumRecPayBean) hmResult.get(sHmRecPayKey);
                    if (recPayAccBook == null) {
                        break;
                    }
                    recPayAccBook.setExchangeInDec(rs.getDouble("FFX"));
                }
            }
            //--------------------------------------------//
        } catch (Exception e) {
            throw new YssException("将证券应收应付台账数据装入哈希表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmResult;
    }

    /**
     * 排序和计算储存在结果哈希表中的数据
     * @throws YssException
     * @return ArrayList
     */
    public ArrayList getOrderList(HashMap hmResult) throws YssException {
        ArrayList arrResult = new ArrayList();
        java.util.Iterator it = null;
        SecBookSumRecPayBean secAccBook = null; //哈希表中迭代出来的实体
        SecBookSumRecPayBean totalSecBook = new SecBookSumRecPayBean(); //储存和计数据
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
                secAccBook = (SecBookSumRecPayBean) it.next();
                //单条记录原币期末成本 = 期初成本 + 流入成本 - 流出成本
                secAccBook.setEndMoney(
                    secAccBook.getBeginMoney() + secAccBook.getInMoney() -
                    secAccBook.getOutMoney());
                //单条记录本位币期末成本 = 期初成本 + 流入成本 - 流出成本
                secAccBook.setPortEndMoney(
                    secAccBook.getPortBeginMoney() + secAccBook.getPortInMoney() -
                    secAccBook.getPortOutMoney());
                //单条记录本位币市值 = 期末成本 + 汇兑损益
                secAccBook.setPortMarketValue(
                    secAccBook.getPortEndMoney() + secAccBook.getExchangeInDec());
                //合计原币期初成本
                totalSecBook.setBeginMoney(
                    totalSecBook.getBeginMoney() + secAccBook.getBeginMoney());
                //合计原币流入成本
                totalSecBook.setInMoney(
                    totalSecBook.getInMoney() + secAccBook.getInMoney());
                //合计原币流出成本
                totalSecBook.setOutMoney(
                    totalSecBook.getOutMoney() + secAccBook.getOutMoney());
                //合计原币期末成本
                totalSecBook.setEndMoney(
                    totalSecBook.getEndMoney() + secAccBook.getEndMoney());
                //合计本位币期初成本
                totalSecBook.setPortBeginMoney(
                    totalSecBook.getPortBeginMoney() + secAccBook.getPortBeginMoney());
                //合计本位币流出成本
                totalSecBook.setPortOutMoney(
                    totalSecBook.getPortOutMoney() + secAccBook.getPortOutMoney());
                //合计本位币流入成本
                totalSecBook.setPortInMoney(
                    totalSecBook.getPortInMoney() + secAccBook.getPortInMoney());
                //合计本位币期末成本
                totalSecBook.setPortEndMoney(
                    totalSecBook.getPortEndMoney() + secAccBook.getPortEndMoney());
                //合计本位币汇兑损益
                totalSecBook.setExchangeInDec(
                    totalSecBook.getExchangeInDec() + secAccBook.getExchangeInDec());
                //合计本位币市值
                totalSecBook.setPortMarketValue(
                    totalSecBook.getPortMarketValue() +
                    secAccBook.getPortMarketValue());
                //将单条记录实体存入
                arrResult.add(secAccBook);
            }
            totalSecBook.setCuryCode("合计：");
            arrResult.add(totalSecBook); //将合计值放到 List 的最后面
        } catch (Exception e) {
            throw new YssException("证券应收应付台帐序列化台帐哈希表出错！", e);
        }
        return arrResult;
    }

    /**
     * 获取应收应付期初的 SQL 语句
     * @param bIsBegin: 为 true 取期初库存，为 false 取期末库存
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getStartRecPayStorageSql(boolean bIsBegin) throws YssException {
        StringBuffer bufSql = new StringBuffer(2000);
        String sSelectFiled = ""; //SELECT 选取的字段
        String sGroupFiled = ""; //台帐链接的倒数第二个字段
        String sWhereFiled = ""; //Where 条件中的字段
        String sTableRele = ""; //关联表 SQL 语句
        String invmgrSecField = "";
        String brokerSecField = "";
        try {
            sSelectFiled = ( (String)this.hmFieldIndRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1])) +
                "Code" + " AS FAccCode";
            sGroupFiled = (String)this.hmFieldRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);
            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //如果链接中有券商
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Broker")) {
                    //取券商所在的分析代码
                    brokerSecField = this.getSettingOper().getStorageAnalysisField(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                    sWhereFiled = sWhereFiled + " AND " + brokerSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                //如果链接中有投资经理
                if (this.aryAccBookDefine[i].equalsIgnoreCase("InvMgr")) {
                    //取投资经理所在的分析代码
                    invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                    sWhereFiled = sWhereFiled + " AND " + invmgrSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
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
                sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode", invmgrSecField);
            }
            if (sSelectFiled.indexOf("FBrokerCode") != -1) {
                brokerSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                sSelectFiled = sSelectFiled.replaceAll("FBrokerCode", brokerSecField);
            }
            if (sGroupFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                sGroupFiled = sGroupFiled.replaceAll("FInvMgrCode", invmgrSecField);
            }
            if (sGroupFiled.indexOf("FBrokerCode") != -1) {
                brokerSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                sGroupFiled = sGroupFiled.replaceAll("FBrokerCode", brokerSecField);
            }

            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);
            bufSql.append("SELECT a1.*, z.FAccName");
            bufSql.append(" FROM (SELECT SUM(FBal) AS FBal,");
            bufSql.append(" SUM(FPortCuryBal) AS FPortCuryBal,");
            bufSql.append(" SUM(FFX) AS FFX,");
            bufSql.append(" FCuryCode,");
            bufSql.append(sSelectFiled);
            bufSql.append(" FROM (SELECT FYearMonth,");
            bufSql.append(" FStorageDate,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FSecurityCode as FSecurityCode1,");
            bufSql.append(" FTsfTypeCode,");
            bufSql.append(" FSubTsfTypeCode,");
            bufSql.append(" FCuryCode,");
            bufSql.append(" FBal,");
            bufSql.append(" FPortCuryBal,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_SecRecPay"));
            bufSql.append(" WHERE ");
            bufSql.append(" (FTsfTypeCode = '07' OR FTsfTypeCode = '06' OR");
            bufSql.append(" FTsfTypeCode = '09')) a");
            bufSql.append(" JOIN (SELECT * FROM " + pub.yssGetTableName("TB_Para_Security") + ") b ON a.FSecurityCode1 = b.FSecurityCode");
            //---------------------取汇兑损益-------------------//
            bufSql.append(" LEFT JOIN (SELECT FStorageDate AS FDate,");
            bufSql.append(" FPortCode AS FPort,");
            bufSql.append(" FAnalysisCode1 AS FAnalysis1,");
            bufSql.append(" FAnalysisCode2 AS FAnalysis2,");
            bufSql.append(" FAnalysisCode3 AS FAnalysis3,");
            bufSql.append(" FSecurityCode AS FSecurityCode2,");
            bufSql.append(" FCatType,");
            bufSql.append(" FAttrClsCode,");
            bufSql.append(" FYearMonth AS FXYearMonth,");
            bufSql.append(" SUM(FPortCuryBal) AS FFX");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_SecRecPay"));
            bufSql.append(" WHERE FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX));
            if (!bIsBegin) {
                bufSql.append(" AND " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'");
            }
            bufSql.append(" AND FCHECKSTATE = 1");
            bufSql.append(" AND (FSubTsfTypeCode LIKE '9905%' OR");
            bufSql.append(" FSubTsfTypeCode LIKE '9909%')");
            bufSql.append(" GROUP BY FStorageDate,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FSecurityCode,");
            bufSql.append(" FCatType,");
            bufSql.append(" FYearMonth,");
            bufSql.append(" FAttrClsCode) c ON a.FStorageDate = c.FDate");
            bufSql.append(" AND a.FPortCode = c.FPort");
            bufSql.append(" AND a.FAnalysisCode1 =");
            bufSql.append(" c.FAnalysis1");
            bufSql.append(" AND a.FAnalysisCode2 =");
            bufSql.append(" c.FAnalysis2");
            bufSql.append(" AND a.FAnalysisCode3 =");
            bufSql.append(" c.FAnalysis3");
            bufSql.append(" AND a.FSecurityCode1 =");
            bufSql.append(" c.FSecurityCode2");
            bufSql.append(" AND a.FYearMonth = c.FXYearMonth");
            //-------------------------------------------------//
            bufSql.append(" WHERE a.FCheckState = 1");
            if (bIsBegin) {
                bufSql.append(" AND " + this.operSql.sqlStoragEve(dBeginDate));
            } else {
                bufSql.append(" AND FStorageDate = " + dbl.sqlDate(this.dEndDate));
                bufSql.append(" AND " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'");
            }
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY FCuryCode, ");
            bufSql.append(sGroupFiled);
            bufSql.append(") a1 LEFT JOIN " + sTableRele);
        } catch (Exception e) {
            throw new YssException("证券台帐获取应收应付期初 SQL 出错！", e);
        }
        return bufSql;
    }

    /**
     * 获取应收发生数据的 SQL
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getTradeRecSql() throws YssException {
        StringBuffer bufSql = new StringBuffer(1000);
        String sSelectFiled = ""; //SELECT 选取的字段
        String sGroupFiled = ""; //台帐链接的倒数第二个字段
        String sWhereFiled = ""; //Where 条件中的字段
        String sTableRele = ""; //关联表 SQL 语句
        String invmgrSecField = "";
        String brokerSecField = "";
        try {
            sSelectFiled = ( (String)this.hmFieldIndRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1])) +
                "Code" + " AS FAccCode";
            sGroupFiled = (String)this.hmFieldRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);
            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //如果链接中有券商
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Broker")) {
                    //取券商所在的分析代码
                    brokerSecField = this.getSettingOper().getStorageAnalysisField(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                    sWhereFiled = sWhereFiled + " AND " + brokerSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                //如果链接中有投资经理
                if (this.aryAccBookDefine[i].equalsIgnoreCase("InvMgr")) {
                    //取投资经理所在的分析代码
                    invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                    sWhereFiled = sWhereFiled + " AND " + invmgrSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                //如果链接中有投资组合
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Port")) {
                    this.sPortCode = this.aryAccBookLink[i + 1];
                }

                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }

            if (sSelectFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode", invmgrSecField);
            }
            if (sSelectFiled.indexOf("FBrokerCode") != -1) {
                brokerSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                sSelectFiled = sSelectFiled.replaceAll("FBrokerCode", brokerSecField);
            }
            if (sGroupFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                sGroupFiled = sGroupFiled.replaceAll("FInvMgrCode", invmgrSecField);
            }
            if (sGroupFiled.indexOf("FBrokerCode") != -1) {
                brokerSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                sGroupFiled = sGroupFiled.replaceAll("FBrokerCode", brokerSecField);
            }

            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);

            bufSql.append("SELECT a1.*, z.FAccName");
            bufSql.append(" FROM (SELECT SUM(FMoney) AS FMoney,");
            bufSql.append(" SUM(FPortCuryMoney) AS FPortCuryMoney,");
            bufSql.append(" FCuryCode,");
            bufSql.append(sSelectFiled);
            bufSql.append(" FROM (SELECT FTransDate,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FSecurityCode as FSecurityCode1,");
            bufSql.append(" FTsfTypeCode,");
            bufSql.append(" FSubTsfTypeCode,");
            bufSql.append(" FCuryCode,");
            bufSql.append(" FMoney,");
            bufSql.append(" FPortCuryMoney,");
            bufSql.append(" FCheckState");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_SecRecPay"));
            bufSql.append(" WHERE FTsfTypeCode IN ('07', '06', '09')");
            bufSql.append(" ) a");
            bufSql.append(" JOIN (SELECT * FROM " + pub.yssGetTableName("TB_Para_Security") + ") b ON a.FSecurityCode1 = b.FSecurityCode");
            bufSql.append(" WHERE a.FCheckState = 1");
            bufSql.append(" AND FTransDate BETWEEN " + dbl.sqlDate(this.dBeginDate));
            bufSql.append(" AND " + dbl.sqlDate(this.dEndDate));
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY FCuryCode, " + sGroupFiled);
            bufSql.append(" ) a1");
            bufSql.append(" LEFT JOIN " + sTableRele);

        } catch (Exception e) {
            throw new YssException("证券台帐获取应收应付发生 SQL 出错！", e);
        }
        return bufSql;
    }

    /**
     * 取应收所对应的应付的 SQL，只在有应付的数据的情况下才返回 SQL，否则返回空 StringBuffer
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getTradePaySql() throws YssException {
        StringBuffer bufSql = new StringBuffer(1200);
        String sSelectFiled = ""; //SELECT 选取的字段
        String sGroupFiled = ""; //台帐链接的倒数第二个字段
        String sWhereFiled = ""; //Where 条件中的字段
        String sTableRele = ""; //关联表 SQL 语句
        String invmgrSecField = "";
        String brokerSecField = "";
        boolean bIsPay = false; //判断是否有应付
        //判断是否以调拨类型或者调拨子类型作为 SELECT 字段
        boolean bIsTsfType = false;
        boolean bIsSubTsfType = false;
        try {
            sSelectFiled = ( (String)this.hmFieldIndRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1])) +
                "Code" + " AS FAccCode";
            if (sSelectFiled.indexOf("FTsfTypeCode") != -1) {
                bIsTsfType = true;
                bIsPay = true;
            }
            if (sSelectFiled.indexOf("FSubTsfTypeCode") != -1) {
                bIsSubTsfType = true;
                bIsPay = true;
            }

            sGroupFiled = (String)this.hmFieldRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);
            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                //如果链接中有券商
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Broker")) {
                    //取券商所在的分析代码
                    brokerSecField = this.getSettingOper().getStorageAnalysisField(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                    sWhereFiled = sWhereFiled + " AND " + brokerSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                //如果链接中有投资经理
                if (this.aryAccBookDefine[i].equalsIgnoreCase("InvMgr")) {
                    //取投资经理所在的分析代码
                    invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                    sWhereFiled = sWhereFiled + " AND " + invmgrSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                //如果链接中有投资组合
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Port")) {
                    this.sPortCode = this.aryAccBookLink[i + 1];
                }
                //获取调拨类型
                if (this.aryAccBookDefine[i].equalsIgnoreCase("TsfType") &&
                    this.aryAccBookLink[i + 1].equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Rec)) {
                    sWhereFiled = sWhereFiled + " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString( (this.aryAccBookLink[i +
                                        1]).replaceAll(YssOperCons.YSS_ZJDBLX_Rec,
                        YssOperCons.YSS_ZJDBLX_Pay)) +
                        " OR " + (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " + dbl.sqlString( (this.aryAccBookLink[i +
                                                1]).replaceAll(YssOperCons.YSS_ZJDBLX_Rec,
                        YssOperCons.YSS_ZJDBLX_Income)) + ")";
                    bIsPay = true;
                    continue;
                }
                //获取调拨子类型
                if (this.aryAccBookDefine[i].equalsIgnoreCase("SubTsfType") &&
                    this.aryAccBookLink[i + 1].substring(0, 2).equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Rec)) {
                    sWhereFiled = sWhereFiled + " AND (" +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " +
                        dbl.sqlString( (this.aryAccBookLink[i +
                                        1]).replaceAll(YssOperCons.YSS_ZJDBLX_Rec,
                        YssOperCons.YSS_ZJDBLX_Pay)) +
                        " OR " +
                        (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                        " = " + dbl.sqlString( (this.aryAccBookLink[i +
                                                1]).replaceAll(YssOperCons.
                        YSS_ZJDBLX_Rec,
                        YssOperCons.YSS_ZJDBLX_Income)) + ")";
                    bIsPay = true;
                    continue;
                }
                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
                    " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }

            if (sSelectFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode", invmgrSecField);
            }
            if (sSelectFiled.indexOf("FBrokerCode") != -1) {
                brokerSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                sSelectFiled = sSelectFiled.replaceAll("FBrokerCode", brokerSecField);
            }
            if (sGroupFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                sGroupFiled = sGroupFiled.replaceAll("FInvMgrCode", invmgrSecField);
            }
            if (sGroupFiled.indexOf("FBrokerCode") != -1) {
                brokerSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                sGroupFiled = sGroupFiled.replaceAll("FBrokerCode", brokerSecField);
            }

            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);
            //判断是否有应付的数据
            if (bIsPay) {
                bufSql.append("SELECT a1.FMoney,");
                bufSql.append(" a1.FPortCuryMoney,");
                bufSql.append(" a1.FCuryCode,");
                bufSql.append(" z.FAccName,");
                if (bIsTsfType) {
                    bufSql.append(" CASE WHEN a1.FAccCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income));
                    bufSql.append(" THEN " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec));
                    bufSql.append(" WHEN a1.FAccCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee));
                    bufSql.append(" THEN " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay));
                    bufSql.append(" END AS FAccCode");
                } else if (bIsSubTsfType) {
                    bufSql.append(" CASE WHEN SUBSTR(a1.FAccCode, 1, 2) = ");
                    bufSql.append(dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + " THEN ");
                    bufSql.append(dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + " || SUBSTR(a1.FAccCode, 3)");
                    bufSql.append(" WHEN SUBSTR(a1.FAccCode, 1, 2) = ");
                    bufSql.append(dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + " THEN ");
                    bufSql.append(dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + " || SUBSTR(a1.FAccCode, 3)");
                    bufSql.append(" END AS FAccCode");
                } else {
                    bufSql.append(" a1.FAccCode");
                }
                bufSql.append(" FROM (SELECT SUM(FMoney) AS FMoney,");
                bufSql.append(" SUM(FPortCuryMoney) AS FPortCuryMoney,");
                bufSql.append(" FCuryCode,");
                bufSql.append(sSelectFiled);
                bufSql.append(" FROM (SELECT FTransDate,");
                bufSql.append(" FPortCode,");
                bufSql.append(" FAnalysisCode1,");
                bufSql.append(" FAnalysisCode2,");
                bufSql.append(" FAnalysisCode3,");
                bufSql.append(" FSecurityCode,");
                bufSql.append(" FTsfTypeCode,");
                bufSql.append(" FSubTsfTypeCode,");
                bufSql.append(" FCuryCode,");
                bufSql.append(" FMoney,");
                bufSql.append(" FPortCuryMoney,");
                bufSql.append(" FCheckState");
                bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_SecRecPay"));
                bufSql.append(" WHERE FTsfTypeCode IN (");
                bufSql.append(dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay));
                bufSql.append(", " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + ")");
                bufSql.append(" ) a");
                bufSql.append(" JOIN (SELECT FSecurityCode AS FSecCode,");
                bufSql.append(" FSecurityName,");
                bufSql.append(" FCatCode,");
                bufSql.append(" FSubCatCode");
                bufSql.append("  FROM " + pub.yssGetTableName("TB_Para_Security") + ") b ON a.FSecurityCode = b.FSecCode");
                bufSql.append(" WHERE a.FCheckState = 1");
                bufSql.append(" AND FTransDate BETWEEN " +
                              dbl.sqlDate(this.dBeginDate));
                bufSql.append(" AND " + dbl.sqlDate(this.dEndDate));
                bufSql.append(sWhereFiled);
                bufSql.append(" GROUP BY FCuryCode, " + sGroupFiled);
                bufSql.append(" ) a1");
                bufSql.append(" LEFT JOIN " + sTableRele);
            }
        } catch (Exception e) {
            throw new YssException("获取应付发生额的 SQL 语句出错！", e);
        }
        return bufSql;
    }

    /**
     * 完成哈希表的初始化工作
     * @throws YssException
     */
    public void initHashTable() throws YssException {
        try {
            this.hmSelectField = new HashMap();
            this.hmSelectField.put("InvMgr", "FInvMgrCode as FStkCode");
            this.hmSelectField.put("CatType", "FCatCode as FStkCode");
            this.hmSelectField.put("Port", "FPortCode as FStkCode");
            this.hmSelectField.put("SubCatType", "FSubCatCode as FStkCode");
            this.hmSelectField.put("Broker", "FBrokerCode as FStkCode");
            this.hmSelectField.put("Cury", "FCuryCode as FStkCode");
            this.hmSelectField.put("SecCode", "FSecurityCode as FStkCode");
            this.hmSelectField.put("SubTsfType", "FSubTsfTypeCode as FStkCode");
            this.hmSelectField.put("TsfType", "FTsfTypeCode as FStkCode");

            this.hmFieldRela = new HashMap();
            this.hmFieldRela.put("SecCode", "FSecurityCode");
            this.hmFieldRela.put("Broker", "FBrokerCode");
            this.hmFieldRela.put("InvMgr", "FInvMgrCode");
            this.hmFieldRela.put("Port", "FPortCode");
            this.hmFieldRela.put("Cury", "FCuryCode");
            this.hmFieldRela.put("CatType", "FCatCode");
            this.hmFieldRela.put("SubCatType", "FSubCatCode");
            this.hmFieldRela.put("TsfType", "FTsfTypeCode");
            this.hmFieldRela.put("SubTsfType", "FSubTsfTypeCode");

            hmFieldIndRela = new HashMap();
            hmFieldIndRela.put("InvMgr", "FInvMgr");
            hmFieldIndRela.put("CatType", "FCat");
            hmFieldIndRela.put("Port", "FPort");
            hmFieldIndRela.put("SubCatType", "FSubCat");
            hmFieldIndRela.put("Broker", "FBroker");
            hmFieldIndRela.put("Cury", "FCury");
            hmFieldIndRela.put("SecCode", "FSecurity");
            hmFieldIndRela.put("TsfType", "FTsfType"); //调拨类型
            hmFieldIndRela.put("SubTsfType", "FSubTsfType"); //调拨子类型

            hmTableRela = new HashMap();
            hmTableRela.put("InvMgr",
                            "(select FInvMgrCode,FInvMgrName as FAccName from " +
                            pub.yssGetTableName("tb_para_investmanager") +
                            " where FCheckState = 1) z on a1.FAccCode = z.FInvMgrCode");
            hmTableRela.put("CatType",
                            "(select FCatCode,FCatName as FAccName from Tb_Base_Category" +
                            " where FCheckState = 1) z on a1.FAccCode = z.FCatCode");
            hmTableRela.put("Port",
                            "(select FPortCode,FPortName as FAccName from " +
                            pub.yssGetTableName("Tb_Para_Portfolio") +
                            " where FCheckState = 1) z on a1.FAccCode = z.FPortCode");
            hmTableRela.put("SubCatType",
                            "(select FSubCatCode,FSubCatName as FAccName from Tb_Base_SubCategory" +
                            " where FCheckState = 1) z on a1.FAccCode = z.FSubCatCode");
            hmTableRela.put("SecCode",
                            "(select FSecurityCode,FSecurityName as FAccName from " +
                            pub.yssGetTableName("tb_para_security") +
                            " where FCheckState = 1) z on a1.FAccCode = z.FSecurityCode");
            hmTableRela.put("Broker",
                            "(select FBankCode,FBankName as FAccName from " +
                            pub.yssGetTableName("Tb_Para_Bank") +
                            " where FCheckState = 1) z on a1.FAccCode = z.FBankCode");
            hmTableRela.put("Cury",
                            "(select FCuryCode,FCuryName as FAccName from " +
                            pub.yssGetTableName("Tb_Para_Currency") +
                            " where FCheckState = 1) z on a1.FAccCode = z.FCuryCode");
            hmTableRela.put("TsfType",
                            "(SELECT FTsfTypeCode, FTsfTypeName AS FAccName FROM " +
                            "Tb_Base_TransferType " +
                            "WHERE FCheckState = 1) z ON a1.FAccCode = z.FTsfTypeCode");
            hmTableRela.put("SubTsfType",
                            "(SELECT FSubTsfTypeCode, FSubTsfTypeName AS FAccName FROM " +
                            "Tb_Base_SubTransferType " +
                            "WHERE FCheckState = 1) z ON a1.FAccCode = z.FSubTsfTypeCode");
        } catch (Exception e) {
            throw new YssException("证券台帐初始化哈希表出错！", e);
        }
    }
}
