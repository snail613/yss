package com.yss.main.operdeal.report.accbook.secbook;

import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.util.*;
import com.yss.main.operdeal.report.accbook.pojo.*;
import java.util.ArrayList;
import com.yss.pojo.sys.YssMapAdmin;
import com.yss.dsub.BaseComparator;
import com.yss.main.cusreport.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.operdeal.report.accbook.BaseAccBook;

public class SecBookSumCost
    extends BaseAccBook {
    private String sPortCode = ""; //条件中的组合编号

    public SecBookSumCost() {
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
            hmResult = this.getSecResultHashTable(); // 得到数据
            arrResult = getOrderList(hmResult);
            sResult = buildRowCompResult(arrResult); //得到含格式的数据  父类中
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取证券汇总台帐出错： \n" + e.getMessage());
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
        SecAccBookSumBean secAccBook;
        try {
            for (int i = 0; i < arrResult.size(); i++) {
                secAccBook = (SecAccBookSumBean) arrResult.get(i);
                hmCellStyle = getCellStyles("DsSecAcc001");
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                    " where FRepDsCode = " + dbl.sqlString("DsSecAcc001") +
                    " and FCheckState = 1 order by FOrderIndex";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    //获得样式
                    sKey = "DsSecAcc001" + "\tDSF\t-1\t" +
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

    public HashMap getSecResultHashTable() throws YssException {
        ResultSet rs = null;
        SecAccBookSumBean secAccBook = null;
        String sHmSckKey = "";
        String sHmTrdKey = "";
        HashMap hmResult = new HashMap();
        try {
            //执行取期初的 SQL
            rs = dbl.openResultSet(this.getStartStorageSql(true).toString());
            while (rs.next()) {
                secAccBook = new SecAccBookSumBean();
                secAccBook.setKeyCode(rs.getString("FAccCode"));
                secAccBook.setKeyName(rs.getString("FAccName"));
                secAccBook.setCuryCode(rs.getString("FCuryCode"));
                secAccBook.setBeginCost(rs.getDouble("FStartCost"));
                secAccBook.setBeginAmount(rs.getDouble("FStartAmount"));

                if (this.bIsPort) { //如果有组合货币
                    secAccBook.setPortBeginCost(rs.getDouble("FPStartCost"));
                }
                sHmSckKey = secAccBook.getKeyCode() + "\f" +
                    secAccBook.getCuryCode();
                //-------------------取汇率---------------------//
                //基础汇率
                BaseOperDeal operDeal = new BaseOperDeal();
                operDeal.setYssPub(pub);
                secAccBook.setBaseRate(
                    operDeal.getValCuryRate(this.dEndDate,
                                            secAccBook.getCuryCode(),
                                            "", YssOperCons.YSS_RATEVAL_BASE));
                //组合汇率
                if (this.bIsPort) {
                    PortfolioBean port = new PortfolioBean();
                    port.setYssPub(pub);
                    port.setPortCode(this.sPortCode);
                    port.getSetting();
                    secAccBook.setPortRate(
                        operDeal.getValCuryRate(this.dEndDate,
                                                port.getCurrencyCode(),
                                                this.sPortCode,
                                                YssOperCons.YSS_RATEVAL_PORT));
                }
                //---------------------------------------------//
                hmResult.put(sHmSckKey, secAccBook);
            }
            dbl.closeResultSetFinal(rs);

            //执行取期间的 SQL
            rs = dbl.openResultSet(this.getTradeSql().toString());
            while (rs.next()) {
                SecAccBookSumBean tmpAccBook = null;
                //拼装哈希表的 Key
                sHmTrdKey = rs.getString("FAccCode") + "\f" +
                    rs.getString("FTradeCury");
                tmpAccBook = (SecAccBookSumBean) hmResult.get(sHmTrdKey);
                //如果哈希表中没有对应键的实体就 new 一个出来
                if (tmpAccBook == null) {
                    tmpAccBook = new SecAccBookSumBean();
                }
                tmpAccBook.setKeyCode(rs.getString("FAccCode"));
                tmpAccBook.setKeyName(rs.getString("FAccName"));
                tmpAccBook.setCuryCode(rs.getString("FTradeCury"));
                //判断数量方向是流入还是流出
                if (rs.getInt("FAmountInd") == 1) { //如果数量方向是流入
                    tmpAccBook.setInAmount(rs.getDouble("FTrdAmount"));
                    tmpAccBook.setInCost(rs.getDouble("FCost"));
                    if (this.bIsPort) { //判断是否取组合货币
                        tmpAccBook.setPortInCost(rs.getDouble("FPortCuryCost"));
                    }
                } else if (rs.getInt("FAmountInd") == -1) { //如果数量方向是流出
                    tmpAccBook.setOutAmount(rs.getDouble("FTrdAmount"));
                    tmpAccBook.setOutCost(rs.getDouble("FCost"));
                    if (this.bIsPort) { //判断是否取组合货币
                        tmpAccBook.setPortOutCost(rs.getDouble("FPortCuryCost"));
                    }
                }
                //-------------------取汇率---------------------//
                //基础汇率
                BaseOperDeal operDeal = new BaseOperDeal();
                operDeal.setYssPub(pub);
                tmpAccBook.setBaseRate(
                    operDeal.getValCuryRate(this.dEndDate,
                                            tmpAccBook.getCuryCode(),
                                            "", YssOperCons.YSS_RATEVAL_BASE));
                //组合汇率
                if (this.bIsPort) {
                    PortfolioBean port = new PortfolioBean();
                    port.setYssPub(pub);
                    port.setPortCode(this.sPortCode);
                    port.getSetting();
                    tmpAccBook.setPortRate(
                        operDeal.getValCuryRate(this.dEndDate,
                                                port.getCurrencyCode(),
                                                this.sPortCode,
                                                YssOperCons.YSS_RATEVAL_PORT));
                }
                //---------------------------------------------//
                hmResult.put(sHmTrdKey, tmpAccBook);
            }
            dbl.closeResultSetFinal(rs);

            //取期末的 SQL
            rs = dbl.openResultSet(this.getStartStorageSql(false).toString());
            while (rs.next()) {
                SecAccBookSumBean tmpEndAccBook =
                    (SecAccBookSumBean) hmResult.get(rs.getString("FAccCode") +
                    "\f" + rs.getString("FCuryCode"));
                if (tmpEndAccBook != null) {
                    tmpEndAccBook.setBal(rs.getDouble("FBal"));
                    //判断是否需要本位币
                    if (bIsPort) {
                        tmpEndAccBook.setPortBal(rs.getDouble("FPortCuryBal"));
                        tmpEndAccBook.setExchangeInDec(rs.getDouble("FFX"));
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("将证券台账数据装入哈希表出错！", e);
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
        SecAccBookSumBean secAccBook = null; //哈希表中迭代出来的实体
        SecAccBookSumBean totalSecBook = new SecAccBookSumBean(); //储存和计数据
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
                secAccBook = (SecAccBookSumBean) it.next();
                //单条记录的期末数量
                secAccBook.setEndAmount(
                    secAccBook.getBeginAmount() + secAccBook.getInAmount() -
                    secAccBook.getOutAmount());
                //单条记录原币期末成本 = 期初成本 + 流入成本 - 流出成本
                secAccBook.setEndCost(
                    secAccBook.getBeginCost() + secAccBook.getInCost() -
                    secAccBook.getOutCost());
                //单条记录本位币期末成本 = 期初成本 + 流入成本 - 流出成本
                secAccBook.setPortEndCost(
                    secAccBook.getPortBeginCost() + secAccBook.getPortInCost() -
                    secAccBook.getPortOutCost());
                //单条记录本位币估值增值 = 估值增值 + 汇兑损益
                secAccBook.setPortBal(
                    secAccBook.getPortBal() + secAccBook.getExchangeInDec());
                //单条记录原币市值
                secAccBook.setMarketValue(
                    secAccBook.getEndCost() + secAccBook.getBal());
                //单条记录本位币市值 = 期末成本 + 估值增值
                secAccBook.setPortMarketValue(
                    secAccBook.getPortEndCost() + secAccBook.getPortBal());
                //合计期初数量
                totalSecBook.setBeginAmount(
                    totalSecBook.getBeginAmount() + secAccBook.getBeginAmount());
                //合计流入数量
                totalSecBook.setInAmount(
                    totalSecBook.getInAmount() + secAccBook.getInAmount());
                //合计流出数量
                totalSecBook.setOutAmount(
                    totalSecBook.getOutAmount() + secAccBook.getOutAmount());
                //合计期末数量
                totalSecBook.setEndAmount(
                    totalSecBook.getEndAmount() + secAccBook.getEndAmount());
                //合计原币期初成本
                totalSecBook.setBeginCost(
                    totalSecBook.getBeginCost() + secAccBook.getBeginCost());
                //合计原币流入成本
                totalSecBook.setInCost(
                    totalSecBook.getInCost() + secAccBook.getInCost());
                //合计原币流出成本
                totalSecBook.setOutCost(
                    totalSecBook.getOutCost() + secAccBook.getOutCost());
                //合计原币期末成本
                totalSecBook.setEndCost(
                    totalSecBook.getEndCost() + secAccBook.getEndCost());
                //合计本位币期初成本
                totalSecBook.setPortBeginCost(
                    totalSecBook.getPortBeginCost() + secAccBook.getPortBeginCost());
                //合计本位币流出成本
                totalSecBook.setPortOutCost(
                    totalSecBook.getPortOutCost() + secAccBook.getPortOutCost());
                //合计本位币流入成本
                totalSecBook.setPortInCost(
                    totalSecBook.getPortInCost() + secAccBook.getPortInCost());
                //合计本位币期末成本
                totalSecBook.setPortEndCost(
                    totalSecBook.getPortEndCost() + secAccBook.getPortEndCost());
                //合计原币市值
                totalSecBook.setMarketValue(
                    totalSecBook.getMarketValue() + secAccBook.getMarketValue());
                //合计本位币估值增值
                totalSecBook.setPortBal(totalSecBook.getPortBal() +
                                        secAccBook.getPortBal());
                //合计本位币市值
                totalSecBook.setPortMarketValue(
                    totalSecBook.getPortMarketValue() + secAccBook.getPortMarketValue());
                //将单条记录实体存入
                arrResult.add(secAccBook);
            }
            totalSecBook.setKeyName("合计：");
            arrResult.add(totalSecBook); //将合计值放到 List 的最后面
        } catch (Exception e) {
            throw new YssException("证券台帐序列化台帐哈希表出错！", e);
        }
        return arrResult;
    }

    /**
     * 获取 证券台帐期初信息 SQL
     * @param bIsBegin: ture 取期初库存和成本， false 取期末库存和成本
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getStartStorageSql(boolean bIsBegin) throws YssException {
        StringBuffer bufSql = new StringBuffer(2200);
        String sSelectFiled = ""; //SELECT 选取的字段
        String sGroupFiled = ""; //台帐链接的倒数第二个字段
        String sWhereFiled = ""; //Where 条件中的字段
        String sRPWhereFiled = ""; //应收应付的 Where 条件
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
                    sRPWhereFiled = " AND " + brokerSecField + " = " +
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
                    sRPWhereFiled = " AND " + invmgrSecField + " = " +
                        dbl.sqlString(this.aryAccBookLink[i + 1]);
                    continue;
                }
                //如果链接中有投资组合
                if (this.aryAccBookDefine[i].equalsIgnoreCase("Port")) {
                    sRPWhereFiled = " AND " + (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) +
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
                //fanghaoln MS01063 QDV4赢时胜(测试)2010年04月01日01_B
                if(invmgrSecField.length()>1){
                	sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode", invmgrSecField);
                }else
                sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode", "''");
                //-----------------------end --------------------------------------------
            }
            if (sSelectFiled.indexOf("FBrokerCode") != -1) {
                brokerSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                //fanghaoln MS01063 QDV4赢时胜(测试)2010年04月01日01_B
                if(brokerSecField.length()>1){
                	sSelectFiled = sSelectFiled.replaceAll("FBrokerCode", brokerSecField);
                }else
                sSelectFiled = sSelectFiled.replaceAll("FBrokerCode", "''");
                //-----------------------end --------------------------------------------
                //sSelectFiled = sSelectFiled.replaceAll("FBrokerCode", brokerSecField);
            }
            if (sGroupFiled.indexOf("FInvMgrCode") != -1) {
                invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
                //fanghaoln MS01063 QDV4赢时胜(测试)2010年04月01日01_B
                if(invmgrSecField.length()>1){
                	sGroupFiled = sGroupFiled.replaceAll("FInvMgrCode", invmgrSecField);
                }else
                	sGroupFiled = sGroupFiled.replaceAll("FInvMgrCode", "''");
                //-----------------------end --------------------------------------------
                //sGroupFiled = sGroupFiled.replaceAll("FInvMgrCode", invmgrSecField);
            }
            if (sGroupFiled.indexOf("FBrokerCode") != -1) {
                brokerSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
                //fanghaoln MS01063 QDV4赢时胜(测试)2010年04月01日01_B
                if(brokerSecField.length()>1){
                	sGroupFiled = sGroupFiled.replaceAll("FBrokerCode", brokerSecField);
                }else
                	sGroupFiled = sGroupFiled.replaceAll("FBrokerCode", "''");
                //-----------------------end --------------------------------------------
                //sGroupFiled = sGroupFiled.replaceAll("FBrokerCode", brokerSecField);
            }
            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);

            //拼接 SQL
            bufSql.append("SELECT a1.*, a1.FAccCode, z.FAccName");
            bufSql.append(" FROM (SELECT " + sSelectFiled + ",");
            bufSql.append(" FCuryCode,");
            bufSql.append(" SUM(FStorageCost) AS FStartCost,");
            bufSql.append(" SUM(FPortCuryCost) AS FPStartCost,");
            bufSql.append(" SUM(FStorageAmount) AS FStartAmount,");
            bufSql.append(" SUM(FBal) AS FBal,");
            bufSql.append(" SUM(FPortCuryBal) AS FPortCuryBal,");
            bufSql.append(" SUM(FFX) AS FFX");
            bufSql.append(" FROM (SELECT a.*, ");
            bufSql.append(" b.FCatCode,");
            bufSql.append(" b.FSubCatCode,");
            bufSql.append(" c.FBal,");
            bufSql.append(" c.FPortCuryBal,");
            bufSql.append(" d.FFX");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_Security") + " a");
            bufSql.append(" LEFT JOIN (SELECT FSecurityCode, FCatCode, FSubCatCode");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Para_Security") + " ) b ON a.fsecuritycode =");
            bufSql.append(" b.fsecuritycode");
            //--------------------取估值增值--------------------//
            bufSql.append(" LEFT JOIN (SELECT SUM(FBal) AS FBal,");
            bufSql.append(" SUM(FPortCuryBal) AS FPortCuryBal,");
            bufSql.append(" FSecurityCode,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FStorageDate,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FYearMonth AS FBYearMonth");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_SecRecPay"));
            bufSql.append(" WHERE ");
            bufSql.append(" FTsfTypeCode = '09' ");
            if (!bIsBegin) {
                bufSql.append(" AND " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'");
            }
            bufSql.append(sRPWhereFiled);
            bufSql.append("GROUP BY ");
            bufSql.append(" FSecurityCode,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FStorageDate,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FYearMonth");
            bufSql.append(" ) c ON c.FSecurityCode = a.FSecurityCode AND");
            bufSql.append(" c.FPortCode = a.FPortCode AND");
            bufSql.append(" a.FStorageDate = c.FStorageDate AND");
            bufSql.append(" a.FAnalysisCode1 = c.FAnalysisCode1 AND");
            bufSql.append(" a.FAnalysisCode2 = c.FAnalysisCode2 AND");
            bufSql.append(" a.FAnalysisCode3 = c.FAnalysisCode3 AND");
            bufSql.append(" a.FYearMonth = c.FBYearMonth");
            //----------------------------------------------------//
            //---------------------取汇兑损益-----------------//
            bufSql.append(" LEFT JOIN (SELECT SUM(FPortCuryBal) AS FFX,");
            bufSql.append(" FSecurityCode,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FStorageDate,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FYearMonth AS FXYearMonth");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_SecRecPay"));
            bufSql.append(" WHERE ");
            bufSql.append(" FTsfTypeCode = '99' and (FSubTsfTypeCode like '9905%' or FSubTsfTypeCode like '9909%')");
            if (!bIsBegin) {
                bufSql.append(" AND " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'");
            }
            bufSql.append(sRPWhereFiled);
            bufSql.append("GROUP BY ");
            bufSql.append(" FSecurityCode,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FStorageDate,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FYearMonth");
            bufSql.append(" ) d ON d.FSecurityCode = a.FSecurityCode AND");
            bufSql.append(" d.FPortCode = a.FPortCode AND");
            bufSql.append(" a.FStorageDate = d.FStorageDate AND");
            bufSql.append(" a.FAnalysisCode1 = d.FAnalysisCode1 AND");
            bufSql.append(" a.FAnalysisCode2 = d.FAnalysisCode2 AND");
            bufSql.append(" a.FAnalysisCode3 = d.FAnalysisCode3 AND");
            bufSql.append(" a.FYearMonth = d.FXYearMonth");
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
                bufSql.append(" AND " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'");
            }
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY FCuryCode, " + sGroupFiled + ") a1");
            bufSql.append(" LEFT JOIN " + sTableRele);
        } catch (Exception e) {
            throw new YssException("获取证券台帐期初库存 SQL 语句出错！", e);
        }
        return bufSql;
    }

    /**
     * 证券台帐 获取交易期间的 SQL 语句
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer getTradeSql() throws YssException {
        StringBuffer bufSql = new StringBuffer(1100);
        String sSelectFiled = ""; //SELECT 选取的字段
        String sGroupFiled = ""; //台帐链接的倒数第二个字段
        String sWhereFiled = ""; //Where 条件中的字段
        String sTableRele = ""; //关联表 SQL 语句
        try {
            sSelectFiled = ( (String)this.hmFieldIndRela.get(this.aryAccBookDefine[this.aryAccBookLink.length - 1])) +
                "Code" + " AS FAccCode";

            sGroupFiled = (String)this.hmFieldRela.get(this.aryAccBookDefine[this.aryAccBookLink.length - 1]);
            //拼接 WHERE 语句；
            for (int i = 0; i < this.aryAccBookLink.length - 1; i++) {
                sWhereFiled = sWhereFiled + " AND " +
                    (String)this.hmFieldRela.get(this.aryAccBookDefine[i]) + " = " +
                    dbl.sqlString(this.aryAccBookLink[i + 1]);
            }
            sWhereFiled = sWhereFiled.replaceAll("FCuryCode", "FTradeCury");
            sGroupFiled = sGroupFiled.replaceAll("FCuryCode", "FTradeCury");
            sSelectFiled = sSelectFiled.replaceAll("FCuryCode", "FTradeCury");
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.aryAccBookLink.length - 1]);

            bufSql.append("SELECT a1.*, z.FAccName");
            bufSql.append(" FROM (SELECT " + sSelectFiled + ",");
            bufSql.append(" d.FCashInd,");
            bufSql.append(" d.FAmountInd,");
            bufSql.append(" c.FTradeCury,");
            bufSql.append(" SUM(c.FCost) AS FCost,");
            bufSql.append(" SUM(c.FPortCuryCost) AS FPortCuryCost,");
            bufSql.append(" SUM(c.FTradeAmount) AS FTrdAmount");
            bufSql.append(" FROM (SELECT a.*, b.FTradeCury, b.FSubCatCode, b.FCatCode");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_SubTrade") + " a");
            bufSql.append(" LEFT JOIN (SELECT FTradeCury, FSecurityCode, FSubCatCode, FCatCode");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_para_security") + ") b ON a.FSecurityCode = b.FSecurityCode) c");
            bufSql.append(" LEFT JOIN (SELECT FCashInd, FTradeTypeCode, FAmountInd");
            bufSql.append(" FROM TB_Base_TradeType");
            bufSql.append(" WHERE FCheckState = 1) d ON c.FTradeTypeCode = d.FTradeTypeCode");
            bufSql.append(" WHERE FCheckState = 1");
            bufSql.append(" AND FBargaindate BETWEEN " + dbl.sqlDate(this.dBeginDate) + " AND ");
            bufSql.append(dbl.sqlDate(this.dEndDate));
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY c.FTradeCury, d.FCashInd, d.FAmountInd," + sGroupFiled + " ) a1");
            bufSql.append(" LEFT JOIN " + sTableRele);
        } catch (Exception e) {
            throw new YssException("证券台帐，台帐期间交易 SQL 语句出错！", e);
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
