package com.yss.main.operdeal;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.yss.dsub.BaseBean;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.util.YssException;
import com.yss.util.YssOperCons;

public class BaseCashAccLinkDeal
    extends BaseBean {
    private String cashAccCode = "";
    private String invMgrCode = "";
    private String portCode = "";
    private String catCode = "";
    private String subCatCode = "";
    private String exchangeCode = "";
    private String brokerCode = "";
    private String tradeTypeCode = "";
    private String securityCode = "";
    private String curyCode = "";
    private String sType = "";
    private CashAccountBean auxiCashAccount = null; //添加备用帐户Bean QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
    
    private java.util.Date startDate;

    /**
     * auxiCashAccount read-only Attribute
     * 获取辅助帐户信息，通过get属性获取
     * QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
     * @return CashAccountBean
     */
    public CashAccountBean getAuxiCashAccount() {
        return auxiCashAccount;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public String getCatCode() {
        return catCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public String getSubCatCode() {
        return subCatCode;
    }

    public void setSubCatCode(String subCatCode) {
        this.subCatCode = subCatCode;
    }

    public String getTradeTypeCode() {
        return tradeTypeCode;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getSType() {
        return sType;
    }

    public void setTradeTypeCode(String tradeTypeCode) {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setSType(String sType) {
        this.sType = sType;
    }

    /**
     * buildFeeCondition
     *
     * @return String
     */
    public String buildCashAccLinkCondition(String sType) {
        StringBuffer buf = new StringBuffer();
        if (sType != null && sType.length() > 0 && sType.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)) { //若分红的话
            buf.append("FTradeTypeCode = " + dbl.sqlString(this.tradeTypeCode)).append("\t");
            buf.append("FPortCode = " + dbl.sqlString(this.portCode)).append("\t");
            buf.append("FInvMgrCode = " + dbl.sqlString(this.invMgrCode)).append("\t");
            buf.append("FCuryCode = " + dbl.sqlString(this.curyCode)).append("\t");
            buf.append("FSubCatCode = " + dbl.sqlString(this.subCatCode)).append("\t");
            buf.append("FCatCode = " + dbl.sqlString(this.catCode)).append("\t");
            buf.append("FExchangeCode = " + dbl.sqlString(this.exchangeCode)).append("\t");
            buf.append("FBrokerCode = " + dbl.sqlString(this.brokerCode)).append("\t");

        } else {
            buf.append("FInvMgrCode = " + dbl.sqlString(this.invMgrCode)).append("\t");
            buf.append("FPortCode = " + dbl.sqlString(this.portCode)).append("\t");
            buf.append("FExchangeCode = " + dbl.sqlString(this.exchangeCode)).append("\t");
            buf.append("FBrokerCode = " + dbl.sqlString(this.brokerCode)).append("\t");
            buf.append("FTradeTypeCode = " + dbl.sqlString(this.tradeTypeCode)).append("\t");
            buf.append("FSubCatCode = " + dbl.sqlString(this.subCatCode)).append("\t");
            buf.append("FCatCode = " + dbl.sqlString(this.catCode)).append("\t");
            buf.append("FCuryCode = " + dbl.sqlString(this.curyCode)).append("\t");
        }
        return buf.toString();
    }

    /**
     * setLinkParaAttr
     * 设置获取现金帐户需要的链接条件 重载
     * @param sInvMgrCode String
     * @param sPortCode String
     * @param sSecurityCode String
     * @param sBrokerCode String
     * @param sTradeTypeCode String
     * @param dtStartDate Date
     * @param sType String 标记,标明是做哪个业务的,如分红,配股等
     */
    public void setLinkParaAttr(String sInvMgrCode, String sPortCode,
                                String sSecurityCode, String sBrokerCode,
                                String sTradeTypeCode, Date dtStartDate,
                                String sCuryCode, String sType) throws YssException {
        this.invMgrCode = sInvMgrCode;
        this.portCode = sPortCode;
        this.brokerCode = sBrokerCode;
        this.tradeTypeCode = sTradeTypeCode;
        this.startDate = dtStartDate;
        this.curyCode = sCuryCode;
        this.sType = sType;
        if (!this.securityCode.equalsIgnoreCase(sSecurityCode)) {
            this.securityCode = sSecurityCode;
            SecurityBean security = new SecurityBean();
            security.setYssPub(pub);
            security.setSecurityCode(this.securityCode);
            security.getSetting();

            this.catCode = security.getCategoryCode();
            this.subCatCode = security.getSubCategoryCode();
            this.exchangeCode = security.getExchangeCode();
        }
    }

    /**
     * setLinkParaAttr
     * 设置获取现金帐户需要的链接条件
     * @param sInvMgrCode String
     * @param sPortCode String
     * @param sCatCode String
     * @param sSubCatCode String
     * @param sExchangeCode String
     * @param sBrokerCode String
     * @param sTradeType String
     */
    public void setLinkParaAttr(String sInvMgrCode, String sPortCode,
                                String sCatCode, String sSubCatCode,
                                String sExchangeCode, String sBrokerCode,
                                String sTradeTypeCode,
                                java.util.Date dtStartDate, String sCuryCode) throws YssException {
        this.invMgrCode = sInvMgrCode;
        this.portCode = sPortCode;
        this.catCode = sCatCode;
        this.subCatCode = sSubCatCode;
        this.exchangeCode = sExchangeCode;
        this.securityCode = "";
        this.brokerCode = sBrokerCode;
        this.tradeTypeCode = sTradeTypeCode;
        this.startDate = dtStartDate;
        this.curyCode = sCuryCode;
    }

    /**
     * setLinkParaAttr
     * 设置获取现金帐户需要的链接条件
     * @param sInvMgrCode String
     * @param sPortCode String
     * @param sSecurityCode String
     * @param sBrokerCode String
     * @param sTradeTypeCode String
     * @param dtStartDate Date
     */
    public void setLinkParaAttr(String sInvMgrCode, String sPortCode,
                                String sSecurityCode, String sBrokerCode,
                                String sTradeTypeCode, Date dtStartDate) throws
        YssException {
        this.invMgrCode = sInvMgrCode;
        this.portCode = sPortCode;
        this.brokerCode = sBrokerCode;
        this.tradeTypeCode = sTradeTypeCode;
        this.startDate = dtStartDate;
        if (!this.securityCode.equalsIgnoreCase(sSecurityCode)) {
            this.securityCode = sSecurityCode;
            SecurityBean security = new SecurityBean();
            security.setYssPub(pub);
            security.setSecurityCode(this.securityCode);
            security.getSetting();

            this.catCode = security.getCategoryCode();
            this.subCatCode = security.getSubCategoryCode();
            this.exchangeCode = security.getExchangeCode();
            this.curyCode = security.getTradeCuryCode();
        }
    }

    /**
     * setLinkParaAttr
     * 设置获取现金帐户需要的链接条件
     * @param sInvMgrCode String
     * @param sPortCode String
     * @param sSecurityCode String
     * @param sBrokerCode String
     * @param sTradeTypeCode String
     * @param dtStartDate Date
     */
    public void setLinkParaAttr(String sInvMgrCode, String sPortCode,
                                String sSecurityCode, String sBrokerCode,
                                String sTradeTypeCode, Date dtStartDate,
                                String sCuryCode) throws
        YssException {
        setLinkParaAttr(sInvMgrCode, sPortCode,
                        sSecurityCode, sBrokerCode,
                        sTradeTypeCode, dtStartDate,
                        sCuryCode, "");

        /* this.invMgrCode = sInvMgrCode;
         this.portCode = sPortCode;
         this.brokerCode = sBrokerCode;
         this.tradeTypeCode = sTradeTypeCode;
         this.startDate = dtStartDate;
         this.curyCode = sCuryCode;
         if (!this.securityCode.equalsIgnoreCase(sSecurityCode)) {
            this.securityCode = sSecurityCode;
            SecurityBean security = new SecurityBean();
            security.setYssPub(pub);
            security.setSecurityCode(this.securityCode);
            security.getSetting();

            this.catCode = security.getCategoryCode();
            this.subCatCode = security.getSubCategoryCode();
            this.exchangeCode = security.getExchangeCode();
         }*/
    }

    /* getCashAccountBean
     *
     * @return CashAccountBean
     */
    public List getCashAccount() throws YssException {  //  modified by zhaoxianlin 20121105 #story 3159
        String strSql = "", strSqlTmp = ""; //add by leeyu
        String[] sFeeCondAry = null;
        String sTmpTableName = "";
        ResultSet rs = null;
        CashAccountBean cashaccount = null;
        String auxiCashAccCode = ""; //辅助帐户代码 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
        List caAccList = new ArrayList(); //add by zhaoxianlin 20121105 #story 3159
        try {
            strSql = "select a.* from (select FInvMgrCode,FPortCode,FCatCode,FSubCatCode," +
                " FBrokerCode,FTradeTypeCode,FExchangeCode,FLinkLevel," +
                "FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                "FCashAccCode,max(FStartDate) as FStartDate,FCuryCode from " + //此处添加 FCuryCode 因为 buildCashAccLinkCondition()中有这个字段 by leeyu 080611
                pub.yssGetTableName("Tb_Para_CashAccLink") +
                " where FCheckState=1 and FStartDate <= " +
                dbl.sqlDate(startDate) +

                ( (this.catCode.length() == 0) ? " " : " and (FCatCode = " + dbl.sqlString(this.catCode) +
                 " or FCatCode = ' ')") +
                " and (FSubCatCode = " + dbl.sqlString(this.subCatCode) +
                " or FSubCatCode = ' ')" +
                " and (FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
                " or FInvMgrCode = ' ')" +
                " and (FTradeTypeCode = " + dbl.sqlString(this.tradeTypeCode) +
                " or FTradeTypeCode = ' ')" +
                " and (FBrokerCode = " + dbl.sqlString(this.brokerCode) +
                " or FBrokerCode = ' ')" +
                " and (FExchangeCode = " + dbl.sqlString(this.exchangeCode) +
                " or FExchangeCode = ' ')" +
                " and (FCuryCode = " + dbl.sqlString(this.curyCode) + " or FCuryCode = ' ')" +
                " and (FPortCode = " + dbl.sqlString(this.portCode) +
                " or FPortCode = ' ') and FCheckState = 1" +
                " group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode," +
                " FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                "FTradeTypeCode,FExchangeCode,FCashAccCode,FLinkLevel,FCuryCode ) a " +
                " join (select FCashAccCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FCuryCode = " + dbl.sqlString(this.curyCode) +
                " and FStartDate <= " + dbl.sqlDate(startDate) +
                " group by FCashAccCode " +
                ") b on a.FCashAccCode = b.FCashAccCode " +
                " order by a.FLinkLevel desc ";
            /*rs = dbl.openResultSet(strSql); //此处就不用了,by leeyu 080611
                      if (rs.next()) {
               this.cashAccCode = rs.getString("FCashAccCode") + "";

               if (this.cashAccCode.length() != 0) {
                  cashaccount = new CashAccountBean();
                  cashaccount.setYssPub(pub);
                  cashaccount.setStrCashAcctCode(this.cashAccCode);
                  cashaccount.getSetting();
               }
                      }*/
            //取具体的现金帐户
            sTmpTableName = "Tb_Tmp_CashAccLink_" + pub.getUserCode();
            if (dbl.yssTableExist(sTmpTableName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " + sTmpTableName));
                /**end*/
            }
            /*dbl.executeSql("create table " + sTmpTableName + " as (" + strSql +
                           ")");  */
            //--------------------------------------------
            strSqlTmp = "select tmp.FinvMgrCode,tmp.FPortCode,tmp.FCatCode,tmp.FSubCatCode,tmp.FBrokerCode,tmp.FCuryCode," +
                "tmp.FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " tmp.FTradeTypeCode,tmp.FExchangeCode,tmp.FLinkLevel,tmp.FCashAccCode,tmp.FStartDate,sum(T1+T2+T3+T4+T5+T6+T7+T8) as FCount " +
                " from ( select tmp_acc.FinvMgrCode,case when tmp_acc.FinvMgrCode<>' ' then 1 else 0 end as T1," +
                " tmp_acc.FPortCode,case when tmp_acc.FPortCode<>' ' then 1 else 0 end as T2," +
                " tmp_acc.FCatCode,case when tmp_acc.FCatCode<>' ' then 1 else 0 end as T3," +
                " tmp_acc.FSubCatCode,case when tmp_acc.FSubCatCode<>' ' then 1 else 0 end as T4," +
                " tmp_acc.FBrokerCode,case when tmp_acc.FBrokerCode<>' ' then 1 else 0 end as T5," +
                " tmp_acc.FTradeTypeCode,case when tmp_acc.FTradeTypeCode<>' ' then 1 else 0 end as T6," +
                " tmp_acc.FExchangeCode,case when tmp_acc.FExchangeCode<>' ' then 1 else 0 end as T7," +
                " tmp_acc.FCuryCode,case when tmp_acc.FCuryCode<>' ' then 1 else 0 end as T8," +
                " tmp_acc.FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " tmp_acc.FLinkLevel,tmp_acc.FCashAccCode,tmp_acc.FStartDate " +
                " from (" + strSql + ") tmp_acc) tmp group by tmp.FInvMgrCode,tmp.FPortCode,tmp.FCatCode,tmp.FSubCatCode,tmp.FBrokerCode," +
                " tmp.FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " tmp.FTradeTypeCode,tmp.FExchangeCode,tmp.FLinkLevel,tmp.FCashAccCode,tmp.FStartDate,tmp.FCuryCode";
            dbl.executeSql("create table " + sTmpTableName + " as (" + strSqlTmp +
                           ")"); //建表
            strSqlTmp = " select * from " + sTmpTableName + " a where a.FCount=(select max(FCount) as FCount from " + sTmpTableName + ") "; //取最大的值
            rs = dbl.openResultSet_antReadonly(strSqlTmp);
            int iCount = 0; //计算有多少行
            while (rs.next()) {
                iCount = rs.getRow();
            }
            rs.beforeFirst();
            if (iCount > 1) {
                sFeeCondAry = buildCashAccLinkCondition(sType).split("\t"); // by leeyu
                for (int i = 0; i <= sFeeCondAry.length; i++) {
                	//------ modify by wangzuochun 2010.09.07  MS01699    现金账户链接未按账户链接的优先级来取账户    QDV4赢时胜（上海）2010年9月6日01_B    
                    if (i == sFeeCondAry.length) {
                        strSql = "select * from " + sTmpTableName + " order by FLinkLevel desc";
                    } else {
                        strSql = "select * from " + sTmpTableName + " where " +
                            sFeeCondAry[i] + " order by FLinkLevel desc";
                    }
                    //-------------------------------MS01699---------------------------//
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {   // if-->while modified by zhaoxianlin 20121105 #story 3159
                        this.cashAccCode = rs.getString("FCashAccCode") + "";
                        if (this.cashAccCode.length() != 0) {
                            cashaccount = new CashAccountBean();
                            cashaccount.setYssPub(pub);
                            cashaccount.setStrCashAcctCode(this.cashAccCode);
                            cashaccount.setCount(rs.getInt("FCount"));//MS01288 QDV4交银施罗德2010年6月10日01_A  add by jiangshichao 2010.07.02 
                            cashaccount.getSetting();
                            caAccList.add(cashaccount);  //add by zhaoxianlin 20121105 #story 3159
                        }
                        //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                        auxiCashAccCode = rs.getString("FAuxiCashAccCode"); //辅助帐户代码
                        if (auxiCashAccCode != null && auxiCashAccCode.length() > 0) {
                            auxiCashAccount = new CashAccountBean();
                            auxiCashAccount.setYssPub(pub);
                            auxiCashAccount.setStrCashAcctCode(auxiCashAccCode);
                            auxiCashAccount.getSetting();
                            caAccList.add(auxiCashAccount);  //add by zhaoxianlin 20121105 #story 3159
                        }
                        //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                        //break;  // 这里返回一个LIST modified by zhaoxianlin 20121105 #story 3159
                    }
                    dbl.closeResultSetFinal(rs);
                }
            } else if (iCount == 1) {
                while (rs.next()) {
                    cashaccount = new CashAccountBean();
                    cashaccount.setYssPub(pub);
                    cashaccount.setStrCashAcctCode(rs.getString("FCashAccCode"));
                    cashaccount.getSetting();
                    caAccList.add(cashaccount);  //add by zhaoxianlin 20121105 #story 3159
                    //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                    auxiCashAccCode = rs.getString("FAuxiCashAccCode"); //辅助帐户代码
                    if (auxiCashAccCode != null && auxiCashAccCode.length() > 0) {
                        auxiCashAccount = new CashAccountBean();
                        auxiCashAccount.setYssPub(pub);
                        auxiCashAccount.setStrCashAcctCode(auxiCashAccCode);
                        auxiCashAccount.getSetting();
                        caAccList.add(auxiCashAccount);  //add by zhaoxianlin 20121105 #story 3159
                    }
                    //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                }
            }
            //----------------------------
        } catch (Exception e) {
            cashaccount = null;
            throw new YssException("获取符合链接条件的现金帐户信息出错");
        } finally {
            dbl.closeResultSetFinal(rs);
            return caAccList;
        }
    }
    /**
 * add by zhaoxianlin 20121207 #STORY #3371 股指期货需求变更
     * 要求区分券商，这里临时处理
     * @return
     * @throws YssException
     */
    public List getHKCashAccount() throws YssException {  //  modified by zhaoxianlin 20121105 #story 3159
        String strSql = "", strSqlTmp = ""; //add by leeyu
        String[] sFeeCondAry = null;
        String sTmpTableName = "";
        ResultSet rs = null;
        CashAccountBean cashaccount = null;
        String auxiCashAccCode = ""; //辅助帐户代码 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
        List caAccList = new ArrayList(); //add by zhaoxianlin 20121105 #story 3159
        try {
            strSql = "select a.* from (select FInvMgrCode,FPortCode,FCatCode,FSubCatCode," +
                " FBrokerCode,FTradeTypeCode,FExchangeCode,FLinkLevel," +
                "FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                "FCashAccCode,max(FStartDate) as FStartDate,FCuryCode from " + //此处添加 FCuryCode 因为 buildCashAccLinkCondition()中有这个字段 by leeyu 080611
                pub.yssGetTableName("Tb_Para_CashAccLink") +
                " where FCheckState=1 and FStartDate <= " +
                dbl.sqlDate(startDate) +

                ( (this.catCode.length() == 0) ? " " : " and (FCatCode = " + dbl.sqlString(this.catCode) +
                 " or FCatCode = ' ')") +
                " and (FSubCatCode = " + dbl.sqlString(this.subCatCode) +
                " or FSubCatCode = ' ')" +
                " and (FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
                " or FInvMgrCode = ' ')" +
                " and (FTradeTypeCode = " + dbl.sqlString(this.tradeTypeCode) +
                " or FTradeTypeCode = ' ')" +
                " and (FBrokerCode = " + dbl.sqlString(this.brokerCode) +
                //" or FBrokerCode = ' ')" +
                ")" +
                " and (FExchangeCode = " + dbl.sqlString(this.exchangeCode) +
                " or FExchangeCode = ' ')" +
                " and (FCuryCode = " + dbl.sqlString(this.curyCode) + " or FCuryCode = ' ')" +
                " and (FPortCode = " + dbl.sqlString(this.portCode) +
                " or FPortCode = ' ') and FCheckState = 1" +
                " group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode," +
                " FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                "FTradeTypeCode,FExchangeCode,FCashAccCode,FLinkLevel,FCuryCode ) a " +
                " join (select FCashAccCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FCuryCode = " + dbl.sqlString(this.curyCode) +
                " and FStartDate <= " + dbl.sqlDate(startDate) +
                " group by FCashAccCode " +
                ") b on a.FCashAccCode = b.FCashAccCode " +
                " order by a.FLinkLevel desc ";
            /*rs = dbl.openResultSet(strSql); //此处就不用了,by leeyu 080611
                      if (rs.next()) {
               this.cashAccCode = rs.getString("FCashAccCode") + "";

               if (this.cashAccCode.length() != 0) {
                  cashaccount = new CashAccountBean();
                  cashaccount.setYssPub(pub);
                  cashaccount.setStrCashAcctCode(this.cashAccCode);
                  cashaccount.getSetting();
               }
                      }*/
            //取具体的现金帐户
            sTmpTableName = "Tb_Tmp_HKCashAccLink_" + pub.getUserCode();
            if (dbl.yssTableExist(sTmpTableName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " + sTmpTableName));
                /**end*/
            }
            /*dbl.executeSql("create table " + sTmpTableName + " as (" + strSql +
                           ")");  */
            //--------------------------------------------
            strSqlTmp = "select tmp.FinvMgrCode,tmp.FPortCode,tmp.FCatCode,tmp.FSubCatCode,tmp.FBrokerCode,tmp.FCuryCode," +
                "tmp.FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " tmp.FTradeTypeCode,tmp.FExchangeCode,tmp.FLinkLevel,tmp.FCashAccCode,tmp.FStartDate,sum(T1+T2+T3+T4+T5+T6+T7+T8) as FCount " +
                " from ( select tmp_acc.FinvMgrCode,case when tmp_acc.FinvMgrCode<>' ' then 1 else 0 end as T1," +
                " tmp_acc.FPortCode,case when tmp_acc.FPortCode<>' ' then 1 else 0 end as T2," +
                " tmp_acc.FCatCode,case when tmp_acc.FCatCode<>' ' then 1 else 0 end as T3," +
                " tmp_acc.FSubCatCode,case when tmp_acc.FSubCatCode<>' ' then 1 else 0 end as T4," +
                " tmp_acc.FBrokerCode,case when tmp_acc.FBrokerCode<>' ' then 1 else 0 end as T5," +
                " tmp_acc.FTradeTypeCode,case when tmp_acc.FTradeTypeCode<>' ' then 1 else 0 end as T6," +
                " tmp_acc.FExchangeCode,case when tmp_acc.FExchangeCode<>' ' then 1 else 0 end as T7," +
                " tmp_acc.FCuryCode,case when tmp_acc.FCuryCode<>' ' then 1 else 0 end as T8," +
                " tmp_acc.FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " tmp_acc.FLinkLevel,tmp_acc.FCashAccCode,tmp_acc.FStartDate " +
                " from (" + strSql + ") tmp_acc) tmp group by tmp.FInvMgrCode,tmp.FPortCode,tmp.FCatCode,tmp.FSubCatCode,tmp.FBrokerCode," +
                " tmp.FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " tmp.FTradeTypeCode,tmp.FExchangeCode,tmp.FLinkLevel,tmp.FCashAccCode,tmp.FStartDate,tmp.FCuryCode";
            dbl.executeSql("create table " + sTmpTableName + " as (" + strSqlTmp +
                           ")"); //建表
            strSqlTmp = " select * from " + sTmpTableName + " a where a.FCount=(select max(FCount) as FCount from " + sTmpTableName + ") "; //取最大的值
            rs = dbl.openResultSet_antReadonly(strSqlTmp);
            int iCount = 0; //计算有多少行
            while (rs.next()) {
                iCount = rs.getRow();
            }
            rs.beforeFirst();
            if (iCount > 1) {
                sFeeCondAry = buildCashAccLinkCondition(sType).split("\t"); // by leeyu
                for (int i = 0; i <= sFeeCondAry.length; i++) {
                	//------ modify by wangzuochun 2010.09.07  MS01699    现金账户链接未按账户链接的优先级来取账户    QDV4赢时胜（上海）2010年9月6日01_B    
                    if (i == sFeeCondAry.length) {
                        strSql = "select * from " + sTmpTableName + " order by FLinkLevel desc";
                    } else {
                        strSql = "select * from " + sTmpTableName + " where " +
                            sFeeCondAry[i] + " order by FLinkLevel desc";
                    }
                    //-------------------------------MS01699---------------------------//
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {   // if-->while modified by zhaoxianlin 20121105 #story 3159
                        this.cashAccCode = rs.getString("FCashAccCode") + "";
                        if (this.cashAccCode.length() != 0) {
                            cashaccount = new CashAccountBean();
                            cashaccount.setYssPub(pub);
                            cashaccount.setStrCashAcctCode(this.cashAccCode);
                            cashaccount.setCount(rs.getInt("FCount"));//MS01288 QDV4交银施罗德2010年6月10日01_A  add by jiangshichao 2010.07.02 
                            cashaccount.getSetting();
                            caAccList.add(cashaccount);  //add by zhaoxianlin 20121105 #story 3159
                        }
                        //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                        auxiCashAccCode = rs.getString("FAuxiCashAccCode"); //辅助帐户代码
                        if (auxiCashAccCode != null && auxiCashAccCode.length() > 0) {
                            auxiCashAccount = new CashAccountBean();
                            auxiCashAccount.setYssPub(pub);
                            auxiCashAccount.setStrCashAcctCode(auxiCashAccCode);
                            auxiCashAccount.getSetting();
                            caAccList.add(auxiCashAccount);  //add by zhaoxianlin 20121105 #story 3159
                        }
                        //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                        //break;  // 这里返回一个LIST modified by zhaoxianlin 20121105 #story 3159
                    }
                    dbl.closeResultSetFinal(rs);
                }
            } else if (iCount == 1) {
                while (rs.next()) {
                    cashaccount = new CashAccountBean();
                    cashaccount.setYssPub(pub);
                    cashaccount.setStrCashAcctCode(rs.getString("FCashAccCode"));
                    cashaccount.getSetting();
                    caAccList.add(cashaccount);  //add by zhaoxianlin 20121105 #story 3159
                    //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                    auxiCashAccCode = rs.getString("FAuxiCashAccCode"); //辅助帐户代码
                    if (auxiCashAccCode != null && auxiCashAccCode.length() > 0) {
                        auxiCashAccount = new CashAccountBean();
                        auxiCashAccount.setYssPub(pub);
                        auxiCashAccount.setStrCashAcctCode(auxiCashAccCode);
                        auxiCashAccount.getSetting();
                        caAccList.add(auxiCashAccount);  //add by zhaoxianlin 20121105 #story 3159
                    }
                    //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                }
            }
            //----------------------------
        } catch (Exception e) {
            cashaccount = null;
            throw new YssException("获取符合链接条件的现金帐户信息出错");
        } finally {
            dbl.closeResultSetFinal(rs);
            return caAccList;
        }
    }
    /**
     * 取现金账户信息 集合     modified by zhaoxianlin 20121105 #story 3159
     * 系统原先返回单个现金账户，现更改为返回list，再取第一个现金账户
     * @return
     * @throws YssException
     */
    public CashAccountBean getCashAccountBean() throws YssException {
    	List caAccList =null;
    	CashAccountBean cashaccount = null;
        try {
        	caAccList = getCashAccount();
        	Iterator iterator = caAccList.iterator();
        	if(iterator.hasNext()){
        		cashaccount = (CashAccountBean)iterator.next();
        	}
        	return cashaccount;
        } catch (Exception e) {
            throw new YssException("获取符合链接条件的现金帐户信息出错");
        } 
    }
}
