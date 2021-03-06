package com.yss.main.operdeal.linkInfo;

import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.parasetting.CashAccLinkBean;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import com.yss.main.parasetting.SecurityBean;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import com.yss.util.YssCons;

public class CashAccLinkDeal
    extends BaseLinkInfoDeal {
    CashAccLinkBean cashAccLink = null;
    CashAccountBean cashAccount = null;
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
    private java.util.Date startDate;
    private ArrayList alAuxiAccount = null; //添加备用帐户Bean QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506

    public CashAccLinkDeal() {
    }

    /**
     * auxiAccount read-only Attribute
     * 获取辅助帐户信息，通过get属性获取
     * QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
     * @return CashAccountBean
     */
    public ArrayList getAlAuxiAccount() {
        return alAuxiAccount;
    }

    public void setLinkAttr(BaseDataSettingBean LinkInfoBean) throws
        YssException {
        cashAccLink = (CashAccLinkBean) LinkInfoBean;
        if (cashAccLink != null) {
            if (cashAccLink.getStrSecurityCode().length() != 0 &&
                cashAccLink.getCuryCode().trim().length() != 0) { //alter by chenjia 要当币种不为空的情况下 才能给币种赋值
                setLinkParaAttr(cashAccLink.getStrInvMgrCode(),
                                cashAccLink.getStrPortCode(),
                                cashAccLink.getStrSecurityCode(),
                                cashAccLink.getStrBrokerCode(),
                                cashAccLink.getStrTradeTypeCode(),
                                cashAccLink.getDtStartDate(),
                                cashAccLink.getCuryCode());
            } else if (cashAccLink.getStrSecurityCode().trim().length() == 0 &&
                       cashAccLink.getCuryCode().trim().length() != 0) {
                setLinkParaAttr(cashAccLink.getStrInvMgrCode(),
                                cashAccLink.getStrPortCode(),
                                cashAccLink.getStrCatCode(),
                                cashAccLink.getStrSubCatCode(),
                                cashAccLink.getStrExchangeCode(),
                                cashAccLink.getStrBrokerCode(),
                                cashAccLink.getStrTradeTypeCode(),
                                cashAccLink.getDtStartDate(),
                                cashAccLink.getCuryCode());
            } else if (cashAccLink.getStrSecurityCode().trim().length() != 0 &&
                       cashAccLink.getCuryCode().trim().length() == 0) {
                setLinkParaAttr(cashAccLink.getStrInvMgrCode(),
                                cashAccLink.getStrPortCode(),
                                cashAccLink.getStrSecurityCode(),
                                cashAccLink.getStrBrokerCode(),
                                cashAccLink.getStrTradeTypeCode(),
                                cashAccLink.getDtStartDate());
            }
        }
    }

    public void setLinkParaAttr(String sInvMgrCode, String sPortCode,
                                String sCatCode, String sSubCatCode,
                                String sExchangeCode, String sBrokerCode,
                                String sTradeTypeCode,
                                java.util.Date dtStartDate, String sCuryCode) throws
        YssException {
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

    public void setLinkParaAttr(String sInvMgrCode, String sPortCode,
                                String sSecurityCode, String sBrokerCode,
                                String sTradeTypeCode,
                                java.util.Date dtStartDate,
                                String sCuryCode) throws
        YssException {
        this.invMgrCode = sInvMgrCode;
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
        }
    }

    public void setLinkParaAttr(String sInvMgrCode, String sPortCode,
                                String sSecurityCode, String sBrokerCode,
                                String sTradeTypeCode,
                                java.util.Date dtStartDate) throws
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

    public String buildLinkCondition() {
        StringBuffer buf = new StringBuffer();
        buf.append("FInvMgrCode = " + dbl.sqlString(this.invMgrCode)).append("\t");
        buf.append("FPortCode = " + dbl.sqlString(this.portCode)).append("\t");
        buf.append("FExchangeCode = " +
                   dbl.sqlString(this.exchangeCode)).append("\t");
        buf.append("FBrokerCode = " + dbl.sqlString(this.brokerCode)).append("\t");
        buf.append("FTradeTypeCode = " + dbl.sqlString(this.tradeTypeCode)).
            append("\t");
        buf.append("FSubCatCode = " + dbl.sqlString(this.subCatCode)).append("\t");
        buf.append("FCatCode = " + dbl.sqlString(this.catCode)).append("\t");
        buf.append("FCuryCode = " + dbl.sqlString(this.curyCode)).append("\t");
        return buf.toString();
    }

    public String createTempData() throws YssException {
        String strSql = "";
        String sTmpTableName = "";
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        try {
            strSql =
                "select a.* from (select FInvMgrCode,FPortCode,FCatCode,FSubCatCode," +
                " FBrokerCode,FTradeTypeCode,FExchangeCode,FLinkLevel," +
                " FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                "FCashAccCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccLink") +
                " where FStartDate <= " +
                dbl.sqlDate(startDate) +
                ( (this.catCode.length() == 0) ? " " :
                 " and (FCatCode = " + dbl.sqlString(this.catCode) +
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
                " and (FCuryCode = " + dbl.sqlString(this.curyCode) +
                " or FCuryCode = ' ')" +
                " and (FPortCode = " + dbl.sqlString(this.portCode) +
                " or FPortCode = ' ') and FCheckState = 1" +
                " group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode," +
                " FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                "FTradeTypeCode,FExchangeCode,FCashAccCode,FLinkLevel " +
                " order by FLinkLevel desc " +
                ") a " +
                //// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
           
                " join (select FCashAccCode, FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FCuryCode = " +
                dbl.sqlString(this.curyCode) +              
                
                //end by lidaolong
                "  " +
                ") b on a.FCashAccCode = b.FCashAccCode ";
            sTmpTableName = "V_Tmp_CashAccLink_" + pub.getUserCode();
            
            con.setAutoCommit(false); //设置手动提交事务
            bTrans = true;
            
            if (dbl.yssViewExist(sTmpTableName)) {
                dbl.executeSql("drop view " + sTmpTableName);
            }
            if (dbl.getDBType() == YssCons.DB_ORA) {
                String tempStr = "create view " + sTmpTableName + " as (" + strSql +
                    ")";
                dbl.executeSql(tempStr);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                dbl.executeSql("create view " + sTmpTableName + " as (" + strSql +
                               ")" + " definition only");
                dbl.executeSql("insert into " + sTmpTableName + "(" + strSql + ")");
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
            
            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
            
            return sTmpTableName;
        } catch (Exception e) {
            throw new YssException("获取数据至临时存储处出错");
        }
        finally{
            dbl.endTransFinal(con, bTrans); //关闭连接
        }
    }

    public Object getBeans(String sFeeCond) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        CashAccountBean cashaccount = null;
        ArrayList list = null;
        String auxiCashAccCode = ""; //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
        CashAccountBean auxiAccount = null; //添加辅助帐户 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
        try {
        	//---add by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B start---//
            strSql =
                "select a.* ,b.FCuryCode as FCuryCode from (select FInvMgrCode,FPortCode,FCatCode,FSubCatCode," + //modify huangqirong 2013-04-26 story #3858添加币种字段，不然报错
                " FBrokerCode,FTradeTypeCode,FExchangeCode,FLinkLevel," +
                " FAuxiCashAccCode," +
                "FCashAccCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccLink") +
                " where FStartDate <= " +
                dbl.sqlDate(startDate) +
                ( (this.catCode.length() == 0) ? " " :
                 " and (FCatCode = " + dbl.sqlString(this.catCode) +
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
                " and (FCuryCode = " + dbl.sqlString(this.curyCode) +
                " or FCuryCode = ' ')" +
                " and (FPortCode = " + dbl.sqlString(this.portCode) +
                " or FPortCode = ' ') and FCheckState = 1" +
                " group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode," +
                " FAuxiCashAccCode," + //添加辅助帐户字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                "FTradeTypeCode,FExchangeCode,FCashAccCode,FLinkLevel " +
                " order by FLinkLevel desc " +
                ") a " +
                " join (select FCashAccCode, FStartDate  ,FCuryCode from " +  //modify huangqirong 2013-04-26 story #3858添加币种字段，不然报错
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FCuryCode = " +
                dbl.sqlString(this.curyCode) +              
                "  " +
                ") b on a.FCashAccCode = b.FCashAccCode ";
            //---add by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B end---//
            if (sFeeCond.trim().length() == 0) {
            	//edit by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B
                strSql = "select * from (" + strSql + ")";
            } else {
            	//edit by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B
                strSql = "select * from (" + strSql + ") where " +
                    sFeeCond;
            }
            strSql = strSql + " order by FLinkLevel desc "; // by leeyu 080626 对现金帐户链接中优先级的处理
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                list = new ArrayList();
                this.cashAccCode = rs.getString("FCashAccCode") + "";
                if (this.cashAccCode.length() != 0) {
                    cashaccount = new CashAccountBean();
                    cashaccount.setYssPub(pub);
                    cashaccount.setStrCashAcctCode(this.cashAccCode);
                    cashaccount.getSetting();
                }
                //添加辅助帐户信息 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
                auxiCashAccCode = rs.getString("FAuxiCashAccCode");
                if (auxiCashAccCode != null && auxiCashAccCode.length() > 0) {
                    auxiAccount = new CashAccountBean();
                    auxiAccount.setYssPub(pub);
                    auxiAccount.setStrCashAcctCode(auxiCashAccCode);
                    auxiAccount.getSetting();
                    alAuxiAccount = new ArrayList();
                    alAuxiAccount.add(auxiAccount);
                }
                //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090506
            }
            if (cashaccount != null) {
                list.add(cashaccount);
            }
        } catch (Exception e) {
            throw new YssException("获取链接数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
            return list;
        }

    }

    /*public ArrayList getLinkInfoBeans() throws
          YssException {
       String strSql = "";
       ArrayList list = new ArrayList();
       String[] sFeeCondAry = null;
       String sTmpTableName = "";
       ResultSet rs = null;
       CashAccountBean cashaccount = null;
       try {
          strSql =
     "select a.* from (select FInvMgrCode,FPortCode,FCatCode,FSubCatCode," +
                " FBrokerCode,FTradeTypeCode,FExchangeCode,FLinkLevel," +
                "FCashAccCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccLink") +
                " where FStartDate <= " +
                dbl.sqlDate(startDate) +

                ( (this.catCode.length() == 0) ? " " :
                 " and (FCatCode = " + dbl.sqlString(this.catCode) +
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
                " and (FCuryCode = " + dbl.sqlString(this.curyCode) +
                " or FCuryCode = ' ')" +
                " and (FPortCode = " + dbl.sqlString(this.portCode) +
                " or FPortCode = ' ') and FCheckState = 1" +
     " group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode," +
                "FTradeTypeCode,FExchangeCode,FCashAccCode,FLinkLevel) a " +
     " join (select FCashAccCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FCuryCode = " +
                dbl.sqlString(this.curyCode) +
                " and FStartDate <= " + dbl.sqlDate(startDate) +
                " group by FCashAccCode " +
                ") b on a.FCashAccCode = b.FCashAccCode " +
                " order by a.FLinkLevel desc ";
          sTmpTableName = "V_Tmp_CashAccLink_" + pub.getUserCode();
          if (dbl.yssViewExist(sTmpTableName)) {
             dbl.executeSql("drop view " + sTmpTableName);
          }
          if (dbl.getDBType() == YssCons.DB_ORA) {
             dbl.executeSql("create view " + sTmpTableName + " as (" + strSql +
                            ")");
          }
          else if (dbl.getDBType() == YssCons.DB_DB2) {
             dbl.executeSql("create view " + sTmpTableName + " as (" + strSql +
                            ")" + " definition only");
     dbl.executeSql("insert into " + sTmpTableName + "(" + strSql + ")");
          }
          else {
             throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
          }
          sFeeCondAry = buildLinkCondition().split("\t");
          for (int i = 0; i <= sFeeCondAry.length; i++) {
             if (i == sFeeCondAry.length) {
                strSql = "select * from " + sTmpTableName;
             }
             else {
                strSql = "select * from " + sTmpTableName + " where " +
                      sFeeCondAry[i];
             }
             rs = dbl.openResultSet(strSql);
             if (rs.next()) {
                this.cashAccCode = rs.getString("FCashAccCode") + "";
                if (this.cashAccCode.length() != 0) {
                   cashaccount = new CashAccountBean();
                   cashaccount.setYssPub(pub);
                   cashaccount.setStrCashAcctCode(this.cashAccCode);
                   cashaccount.getSetting();
                }
                break;
             }
             dbl.closeResultSetFinal(rs);
          }
          if (cashaccount != null) {
             list.add(cashaccount);
          }
       }
       catch (Exception e) {
          cashaccount = null;
          throw new YssException("获取符合链接条件的现金帐户信息出错");
       }
       finally {
          dbl.closeResultSetFinal(rs);
          return list;
       }

        }*/

}
