package com.yss.main.operdeal.report.accbook.cashbook;

import java.sql.*;
import com.yss.util.*;
import com.yss.main.operdeal.report.accbook.pojo.*;
import com.yss.pojo.sys.*;
import com.yss.dsub.*;
import com.yss.main.cusreport.*;
import java.util.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.operdeal.report.accbook.BaseAccBook;

public class CashBookSumCost
    extends BaseAccBook {
    public CashBookSumCost() {
    }

    private String sPortCode = ""; //组合编号

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
            hmResult = getCashHashTable(); // 得到数据
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
                    cashTradeSum.setInMoney(cashTradeSum.getInMoney() +
                                            rs.getDouble("FMoney"));
                }
                //资金方向流出
                else if (rs.getInt("FInOut") == -1) {
                    cashTradeSum.setOutMoney(cashTradeSum.getOutMoney() +
                                             rs.getDouble("FMoney"));
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
                        cashTradeSum.setPortInMoney(cashTradeSum.getPortInMoney() +
                            dbPortInMoney);
                    } else if (rs.getInt("FInOut") == -1) {
                        dbPortOutMoney = this.getSettingOper().calPortMoney(
                            rs.getDouble("FMoney"),
                            cashTradeSum.getBaseCuryRate(),
                            cashTradeSum.getPortCuryRate(),
                            //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            "", rs.getDate("FTransDate"), sPortCode);
                        cashTradeSum.setPortOutMoney(cashTradeSum.getPortOutMoney() +
                            dbPortOutMoney);
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
                    cashSum.setFinalMoney(cashSum.getInitialMoney() +
                                          cashSum.getInMoney() - cashSum.getOutMoney());
                    //单条记录本位币期末金额 = 本位币期初 + 本位币流入 + 汇兑损益 - 本位币流出
                    cashSum.setPortFinalCost(cashSum.getPortInitialCost() +
                                             cashSum.getPortInMoney() +
                                             cashSum.getPortRateFx() -
                                             cashSum.getPortOutMoney());
                    //合计期初金额
                    totalCashSum.setInitialMoney(totalCashSum.getInitialMoney() +
                                                 cashSum.getInitialMoney());
                    //合计流入金额
                    totalCashSum.setInMoney(totalCashSum.getInMoney() +
                                            cashSum.getInMoney());
                    //合计流出金额
                    totalCashSum.setOutMoney(totalCashSum.getOutMoney() +
                                             cashSum.getOutMoney());
                    //合计期末金额
                    totalCashSum.setFinalMoney(totalCashSum.getFinalMoney() +
                                               cashSum.getFinalMoney());
                    //合计本位币期初金额
                    totalCashSum.setPortInitialCost(totalCashSum.getPortInitialCost() +
                        cashSum.getPortInitialCost());
                    //合计本位币流入金额
                    totalCashSum.setPortInMoney(totalCashSum.getPortInMoney() +
                                                cashSum.getPortInMoney());
                    //合计本位币流出金额
                    totalCashSum.setPortOutMoney(totalCashSum.getPortOutMoney() +
                                                 cashSum.getPortOutMoney());
                    //合计汇兑损益
                    totalCashSum.setPortRateFx(totalCashSum.getPortRateFx() +
                                               cashSum.getPortRateFx());
                    //合计本位币期末金额
                    totalCashSum.setPortFinalCost(totalCashSum.getPortFinalCost() +
                                                  cashSum.getPortFinalCost());
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
                sSelectFiled = sSelectFiled.replaceAll("FSubAccTypeCode",
                    "FSubAccType");
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
            //edited by zhouxiang MS01261    现金台账界面，点击展开，任意选择一个现金账户，点击查询报错    QDV4赢时胜(测试)2010年6月2日2_B 
                if(invmgrSecField.length()>1){  
                	sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode", invmgrSecField);
                }else
                    sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode", "''");
               
            }else
            { invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            if(invmgrSecField=="")
            {sSelectFiled="''" + " AS FAccCode";}
            else{sSelectFiled=invmgrSecField + " AS FAccCode";}
            
            }
            if(sGroupFiled.equals(""))
            {sGroupFiled="''";}             
           //-----------------------end --------------------------------------------
            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);
            //edited by zhouxiang MS01261    现金台账界面，点击展开，任意选择一个现金账户，点击查询报错    QDV4赢时胜(测试)2010年6月2日2_B 
            if(sTableRele==null)
            {sTableRele="(select FInvMgrCode, FInvMgrName as FAccName " +
            			"from " + pub.yssGetTableName("tb_para_investmanager") + " where FCheckState = 1) " +
            			"z on a1.FAccCode = z.FInvMgrCode";
            }
          //-----------------------end --------------------------------------------
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
            bufSql.append(" WHERE ");
            bufSql.append(" FTsfTypeCode = '99' and (FSubTsfTypeCode like '9905%' or FSubTsfTypeCode like '9909%')");
            bufSql.append(" AND FCheckState = 1");
            if (!bIsBegin) {
                bufSql.append(" AND " + dbl.sqlSubStr("FYearMonth", "5", "2") +
                              " <> '00'");
            }
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
                bufSql.append(" AND " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'");
            }
            bufSql.append(sWhereFiled);
            bufSql.append(" GROUP BY FCuryCode ," + sGroupFiled + ") a1");
            bufSql.append(" LEFT JOIN " + sTableRele);
        }catch (Exception e) {
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
                sSelectFiled = sSelectFiled.replaceAll("FSubAccTypeCode",
                    "FSubAccType");
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
                //edited by zhouxiang MS01261    现金台账界面，点击展开，任意选择一个现金账户，点击查询报错    QDV4赢时胜(测试)2010年6月2日2_B 
                if(invmgrSecField.length()>1){ 
                	sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode", invmgrSecField);
                }else
                    sSelectFiled = sSelectFiled.replaceAll("FInvMgrCode", "''");
            }else
            { invmgrSecField = this.getSettingOper().getStorageAnalysisField(
                    YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            if(invmgrSecField=="")
            {sSelectFiled="''" + " AS FAccCode";}
            else{sSelectFiled=invmgrSecField + " AS FAccCode";}
            }
            	//---------------end-----------------
            //取关联表的 SQL
            sTableRele = (String)this.hmTableRela.get(this.aryAccBookDefine[this.
                aryAccBookLink.length - 1]);
            //edited by zhouxiang MS01261    现金台账界面，点击展开，任意选择一个现金账户，点击查询报错    QDV4赢时胜(测试)2010年6月2日2_B 
            if(sTableRele==null)
            {sTableRele="(select FInvMgrCode, FInvMgrName as FAccName " +
            			"from " + pub.yssGetTableName("tb_para_investmanager") + " where FCheckState = 1) " +
            			"z on a1.FAccCode = z.FInvMgrCode";
            }
          //-----------------------end --------------------------------------------
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
            transSql.append(" FROM " + pub.yssGetTableName("Tb_Cash_Transfer"));
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
            transSql.append(" FROM " + pub.yssGetTableName("Tb_Cash_SubTransfer"));
            transSql.append(" WHERE FCheckState = 1) b ON a.FNum = b.FNum");
            transSql.append(" LEFT JOIN (SELECT FCashAccCode AS FCode, FCuryCode, FBankCode, FAccType, FSubAccType");
            transSql.append(
                " FROM " + pub.yssGetTableName("Tb_Para_CashAccount") + ") c ON b.FCashAccCode = c.FCode");
            transSql.append(" WHERE FTransferDate BETWEEN " +
                            dbl.sqlDate(this.dBeginDate));
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
