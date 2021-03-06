package com.yss.main.operdeal.report.repfix.cashuserable;

import java.sql.ResultSet;

import com.yss.dsub.BaseBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 *
 * <p>Title: 华安现金头寸预测表——人民币</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: 赢时胜</p>
 * create by 陈嘉
 * @author not attributable
 * @version 1.0
 */
public class CashUserableHA
      extends BaseBuildCommonRep {
   public CashUserableHA() {
   }

   StringBuffer bufFianl = new StringBuffer(); //最终返回的字符串
   private CommonRepBean repBean;
   private java.util.Date startDate = null; //期初日期
   private java.util.Date endDate = null; //期末日期
   //private java.util.Date dDate = null; //期末日期
   private String sPort = ""; //组合代码
   private String holiday = ""; //节假日
   private FixPub fixPub = null;
//private int days = 0; //整个期间的天数  即要对这么多天进行头寸预测
/*   
   private HashMap cashEndMap = new HashMap(); //期末额
   private HashMap foreignMap = new HashMap(); //外币帐户,key是外币帐户，value是外币折算成人民币的金额
   private HashMap RmbMap = new HashMap(); //人民币帐户
   private ArrayList preTaList = new ArrayList();
   private ArrayList preCashList = new ArrayList();
   private String preADays = ""; // 预估申购结算延迟天数
   private String preRDays = ""; //赎回结算延迟天数
*/
   // private int days = 0; //预估的总天数
   private java.util.Date maxNetDate = null; //获取有最大净值日期的那一天
   private String sPortCury="", sBaseCury="";//本位币，基础货币
   private double dDQFiMktvalue=0;   //短期债券市值
   private double dCQFiMktvalue =0;//长期债券市值
   private double dPortRate=0;//组合汇率

   /**
    * buildReport
    *
    * @param sType String
    * @return String
    */
   public String buildReport(String sType) throws YssException {
      String sResult = "";
      sResult = buildResult(this.startDate, this.sPort);
      return sResult;
   }

   /**
    * initBuildReport
    * 初始化报表  对前台传过来的参数进行解析
    * @param bean BaseBean
    */
   public void initBuildReport(BaseBean bean) throws YssException {
      fixPub = new FixPub();
      fixPub.setYssPub(pub);
      repBean = (CommonRepBean) bean;
      this.parse(repBean.getRepCtlParam());
      sBaseCury = pub.getPortBaseCury(this.sPort);// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
   }

   //解析前台所传参数
   //参数有：起始日期，截止日期，组合代码，节假日群，
   public void parse(String str) throws YssException {
      String[] sReq = str.split("\n");
      try {
         this.startDate = YssFun.toDate(sReq[0].split("\r")[1]);
         //this.endDate = YssFun.toDate(sReq[1].split("\r")[1]);
         this.sPort = sReq[1].split("\r")[1];
           this.holiday = sReq[2].split("\r")[1];
        // days = YssFun.dateDiff(startDate, endDate); //获取总共要预估的天数 包括节假日
      }
      catch (Exception e) {
         throw new YssException("解析参数出错", e);
      }
   }

   //获得所有资产合计
   public double getAsset(java.util.Date dDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double dAsset = 0;
      try {
         strSql =
               "select sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue from " +pub.yssGetTableName("tb_rep_guessvalue") + " where fdate=" +dbl.sqlDate(dDate) 
               + " and facctcode like '1%' and facctdetail=0 and facctlevel=1 and fcurcode='CNY' and (facctattr not like '%股票投资%' and facctattr not like '%基金投资%' and facctattr not like '%权证投资%' and facctattr not like '%存托投资' and facctattr not like '应收利息%' and facctattr not like '应收股利%')";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            dAsset = rs.getDouble("fstandardmoneymarketvalue") ;
         }
         return dAsset;
      }
      catch (Exception e) {
         throw new YssException("获取所有资产合计出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   //获得CNY可用头寸
   public double getCnyUserCash(java.util.Date dDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double NavValue = 0;
      try {
         strSql = "select nvl(fportmarketvalue,0) as fportmarketvalue from " +
               pub.yssGetTableName("tb_data_navdata") +
               " where fkeycode= 'CNY' and fretypecode = 'UseCash' and fnavdate=" +
               dbl.sqlDate(dDate) + " and fportcode=" + dbl.sqlString(sPort);
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            NavValue = rs.getDouble("fportmarketvalue");
         }
         return NavValue;
      }
      catch (Exception e) {
         throw new YssException("获取资产净值出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }
//获得银行存款
   public double getAbaclance(java.util.Date dDate,String strCury,String strArea) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      String strCon="";
      double dAbalance = 0;
      double dBaseRate=0;
      try {
          //strCury 为传进来的币种 strArea 判读为境内还是境外，此参数是用来区分帐户信息设置中的银行代码中的银行信息描述
          if (strCury.startsWith("!")/*equals("!USD")*/) {
             strCon = " b.fcurycode != '"+strCury.substring(1)+"'";
          }
          else{
             strCon = " b.fcurycode = '"+strCury+"'";
          }
          //获取帐户的银行存款
         strSql = "select sum(faccbalance) AS faccbalance,a.fcurycode as fcurycode from (select * from " +
               pub.yssGetTableName("tb_stock_cash") + " where fstoragedate= " +
               dbl.sqlDate(dDate) + " ) a  join " +
               " (select * from " + pub.yssGetTableName("tb_para_cashaccount")  + " b left join (select * from " +
               pub.yssGetTableName("tb_para_bank") + ") c on b.fbankcode=c.fbankcode where " +
               strCon + " and c.fdesc= " + dbl.sqlString(strArea) + ") d on a.fcashacccode=d.fcashacccode group by a.fcurycode " ;
         rs = dbl.openResultSet(strSql);
         while (rs.next()) { //获取银行存款
            dBaseRate = this.getExchangeRate(rs.getString("fcurycode"), this.sPort, "base", startDate);
            dAbalance = dAbalance + rs.getDouble("faccbalance")*dBaseRate/this.dPortRate;
         }
         return dAbalance;
      }
      catch (Exception e) {
         throw new YssException("获取银行存款出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }


   //获得人民币银行存款
   public double getAbaclanceCNY(java.util.Date dDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double dCNYAbalance = 0;
      double dBaseRate=0;
      try {
         //取所有人民币帐户的银行存款
         strSql = "select sum(faccbalance) AS faccbalance,sum(fportcurybal) as fportcurybal from " +
               pub.yssGetTableName("tb_stock_cash") + " where fstoragedate= " +
               dbl.sqlDate(dDate) + " and fcurycode='CNY'";

         rs = dbl.openResultSet(strSql);
         while (rs.next()) { //获取人民币银行存款
            if (sPortCury.equals("CNY")) {//如果是USD进行申赎的，就要按照T-1日的汇率来折算。
               dCNYAbalance = rs.getDouble("faccbalance");
            }
            else if (sPortCury.equals("USD")) {
               if (this.dPortRate != 0) {//如果组合汇率为0的情况，就直接取组合货币成本
                  dBaseRate = this.getExchangeRate("CNY", this.sPort, "base",startDate);
                  dCNYAbalance = rs.getDouble("faccbalance") * dBaseRate / this.dPortRate;
               }
               else {
                  dCNYAbalance = rs.getDouble("fportcurybal");
               }
            }
         }
         return dCNYAbalance;
      }
      catch (Exception e) {
         throw new YssException("获取人民币银行存款出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   //获得所有非人民币银行存款
   public double getAbaclanceNCNY(java.util.Date dDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double dCNYAbalance = 0;
      try {
         //取所有非人民币帐户的本位币的银行存款
         strSql = "select sum(fportmarketvalue) AS fportmarketvalue from " +
               pub.yssGetTableName("tb_data_navdata") + " where fnavdate= " +
               dbl.sqlDate(dDate) + " and fcurycode != 'CNY'  and fretypecode='Cash' and fdetail=3" ;
         rs = dbl.openResultSet(strSql);
         while (rs.next()) { //获取所有非人民币银行存款
            dCNYAbalance = rs.getDouble("fportmarketvalue");
         }
         return dCNYAbalance;
      }
      catch (Exception e) {
         throw new YssException("获取所有非人民币银行存款出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }


   /**
    * 获得换汇金额
    * @param dDate Date
    * @throws YssException
    * @return double
    */
   public double getRateMoney(java.util.Date dDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double dCNYRateMoney = 0;
      try {
         //如既有换入又有换出是轧差的和
         strSql = "select nvl(sum(fmoney*finout),0) as fratemoney from (select * from " +
               pub.yssGetTableName("tb_cash_subtransfer") +
               " where fnum in ( select fnum from " +
               pub.yssGetTableName("tb_cash_transfer") +
               //此处取的是实际到账以及支付的金额
               "  where ftsftypecode='01' and fsubtsftypecode='0004' and ftransferdate= " +  dbl.sqlDate(dDate) + " )) a join " +
               "(select * from " + pub.yssGetTableName("tb_para_cashaccount") +
               //此处是取所有CNY币的换汇交易
               " where fcurycode='CNY') b on a.fcashacccode=b.fcashacccode ";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) { //获取所有换汇的人民币
            dCNYRateMoney = rs.getDouble("fratemoney");
         }
         return dCNYRateMoney;
      }
      catch (Exception e) {
         throw new YssException("获取所有换汇人民币余额出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }



   //获得所有币种折算成人民币的余额
  /* public double getCNYAbaclance(java.util.Date dDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double dCNYAbalance = 0;
      try {
         strSql =
               "select * from (select fcurycode,faccbalance,fstoragedate from " +
               pub.yssGetTableName("tb_stock_cash") + " where fstoragedate= " +
               dbl.sqlDate(dDate) +
               " and fportcode = " + dbl.sqlString(this.sPort) +" ) a join (select fexrate1 as fbaserate,fcurycode from " +
               pub.yssGetTableName("tb_data_exchangerate") +
               " where fexratedate= " +
               dbl.sqlDate(dDate) + ") b on a.fcurycode =b.fcurycode left join (select fexrate1 as fportrate,fcurycode,fexratedate from " +
               pub.yssGetTableName("tb_data_exchangerate") +
               " where fexratedate= " + dbl.sqlDate(dDate) +
               " and fcurycode = 'CNY' ) c on a.fstoragedate=c.fexratedate";

         rs = dbl.openResultSet(strSql);
         while (rs.next()) { //获取人民币余额 包括外币转换成人民币的金额
            dCNYAbalance = dCNYAbalance +
                  rs.getDouble("faccbalance") * rs.getDouble("fbaserate") /
                  rs.getDouble("fportrate");
         }
         return dCNYAbalance;
      }
      catch (Exception e) {
         throw new YssException("获得所有币种折算成人民币的余额出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }*/
   /**
       * 当日交收申购款(R+2)与申购是相等的，e2取T日入账的应收申购款借方金额； 申购指应收申购款   当日交收申购款指当日申购款到账的金额
       当日交收赎回款(R+7)与赎回f2都取T日入账的应付赎回款和应付赎回费贷方金额（当日交收赎回款与赎回其实还会差赎回费收入，但这里是预测，可以不考虑）；
       * @param dDate Date
       * @throws YssException
       * @return double
       */

   //获取申购金额
   public double getSGMoney(java.util.Date dDate, int flag) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      String strCon = ""; //条件
      double dSGMoney = 0;
      try {
         if (flag == 0) { //当这个标识是零时，代表是应收，当这个标识是一时，代表是申购款
            strCon = " and fconfimdate=" + dbl.sqlDate(dDate);
         }
         else if (flag == 1) {
            strCon = " and fsettledate=" + dbl.sqlDate(dDate);
         }
         strSql = "select nvl(fsettlemoney,0) as fsettlemoney from " +
               pub.yssGetTableName("tb_ta_trade") +
               " where fselltype = '01' and fportcode=" + dbl.sqlString(this.sPort) + strCon;//01代表申购
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            dSGMoney = dSGMoney+rs.getDouble("fsettlemoney");
         }
         return dSGMoney;
      }
      catch (Exception e) {
         throw new YssException("获取到账的申购金额出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   //获取赎回金额
   public double getSHMoney(java.util.Date dDate, int flag) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      String strCon = ""; //条件
      double dSHMoney = 0;
      try {
         if (flag == 0) { //当这个标识是零时，代表是应收，当这个标识是一时，代表是申购款
            strCon = " and fconfimdate=" + dbl.sqlDate(dDate);
         }
         else if (flag == 1) {
            strCon = " and fsettledate=" + dbl.sqlDate(dDate);
         }
         strSql = "select nvl(fsettlemoney,0) as fportmarketvalue from " +
               pub.yssGetTableName("tb_ta_trade") +
               " where fselltype = '02' and FPORTCODE=" + dbl.sqlString(this.sPort) + strCon;//02代表赎回
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            dSHMoney = dSHMoney+rs.getDouble("fportmarketvalue");
         }
         return dSHMoney;
      }
      catch (Exception e) {
         throw new YssException("获取到账的赎回金额出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   //获取资产净值
   public double getNavValue(java.util.Date dDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double NavValue = 0;
      try {
         strSql = "select nvl(fportmarketvalue,0) as fportmarketvalue from " +
               pub.yssGetTableName("tb_data_navdata") +
               " where fkeycode= 'TotalValue' and fnavdate=" +
               dbl.sqlDate(dDate) + " and fportcode=" + dbl.sqlString(sPort);
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            NavValue = rs.getDouble("fportmarketvalue");
         }
         return NavValue;
      }
      catch (Exception e) {
         throw new YssException("获取资产净值出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   //获取外币证券清算款
   public double getInOutMoney(java.util.Date dDate,int flag,String strCat) throws
         YssException {
      String strSql = "";
      ResultSet rs = null;
      String strCon = ""; //条件
      String strCon2= "";
      double dInOutMoney = 0;
      double dBaseRate=0;
      try {
         //外币折成人民币证券清算款
         //取的是所有非CNY的交易买入与卖出的清算款折成本位币的轧差
         if (flag == 0) { //是当天国外股票的净变动额，折算成人民币.
            strCon = " and FBargainDate=" + dbl.sqlDate(dDate) ;
         }
         else if (flag == 1) {
            strCon = " and FSettleDate=" + dbl.sqlDate(dDate);
         }
         if (strCat.equals("EQ")) {
             strCon2 = "c.fcatcode='EQ'";
         } else if (strCat.equals("FI")) {
             strCon2 = "c.fcatcode='FI'";
         }
         strSql = "select sum(case when ftradetypecode ='01' then (-1*ffactsettlemoney) else (ffactsettlemoney) end) as ftotalcost,fcurycode,a.fportcode from (select * from " +
               pub.yssGetTableName("tb_data_subtrade") +
               " where fportcode = " + dbl.sqlString(this.sPort)+ strCon + " and ftradetypecode in ('01','02') "+
               " ) a  join (select * from " + pub.yssGetTableName("tb_para_cashaccount") + " where FCuryCode!='CNY') b on a.fcashacccode=b.fcashacccode" + " join (select * from " + pub.yssGetTableName("tb_para_security") + " ) c" +
               " on a.fsecuritycode=c.fsecuritycode where " + strCon2 + " group by a.fportcode,fcurycode";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            dBaseRate = this.getExchangeRate(rs.getString("fcurycode"),this.sPort, "base", startDate);
            dInOutMoney = dInOutMoney +
                  rs.getDouble("ftotalcost") * dBaseRate / this.dPortRate;
         }
         return dInOutMoney;
      }
      catch (Exception e) {
         throw new YssException("获取外币收付证券清算款出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }
   //获取债券兑付款
   /**
    * 取债券到期兑付的金额
    * strArea 标识是境内还是境外的债券兑付款
    * @param dDate Date
    * @throws YssException
    * @return double
    */
   public double getFiDQDFMoney(java.util.Date dDate, String strArea) throws
         YssException {
      String strSql = "";
      String strCon = "";
      double dFiDQDFMoney = 0;
      double dBaseRate = 0;
      ResultSet rs = null;
      try {
         //获取的是债券到期兑付的金额
         if (strArea.equals("国内")) {
            strCon = "fcurycode='CNY' ";
         }
         else {
            strCon = "fcurycode!='CNY' ";
         }
         strSql =
               "select sum(ffactsettlemoney) as ftotalcost,b.fcurycode as fcurycode from (select * from " +
               pub.yssGetTableName("tb_data_subtrade") +
               " where ftradetypecode = '17' and fsettledate=  " +
               dbl.sqlDate(dDate) + " and fportcode = " +
               dbl.sqlString(this.sPort) + ") a  join (select * from "
               + pub.yssGetTableName("tb_para_cashaccount") + " where " +
               strCon +
               " ) b on a.fcashacccode=b.fcashacccode group by b.fcurycode";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            dBaseRate = this.getExchangeRate(rs.getString("fcurycode"),this.sPort,"base", startDate);
            dFiDQDFMoney = dFiDQDFMoney +
                  rs.getDouble("ftotalcost") * dBaseRate / this.dPortRate; //用当天的汇率去折算，折算成头寸表上要显示的币种的金额
         }
         return dFiDQDFMoney;
      }
      catch (Exception e) {
         throw new YssException("获取债券到期兑付的金额出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   //获取应收以及到账的证券清算款
   public double getInMoney(java.util.Date dDate, int flag) throws
         YssException {
      String strSql = "";
      ResultSet rs = null;
      String strCon = ""; //条件
      double dBaseRate=0;
      double dInMoney = 0;
      try {
         if (flag == 0) { //当这个标识是零时，代表是应收，当这个标识是一时，代表是到账的证券清算款
            strCon = " and FBargainDate<="
                  + dbl.sqlDate(dDate) + " and FSettleDate>" +
                  dbl.sqlDate(dDate);
         }
         else if (flag == 1) {
            strCon = " and FSettleDate=" + dbl.sqlDate(dDate);
         }
         //人民币证券应收清算款
         strSql = "select * from (select nvl(ftotalcost,0) as ftotalcost,fcashacccode,ftradetypecode,FBargainDate,FSettleDate from " +
               pub.yssGetTableName("tb_data_subtrade") +
               " where ftradetypecode = '02'" + strCon + ") a  join (select * from "+pub.yssGetTableName("tb_para_cashaccount")+" where FCuryCode='"+this.sPortCury+"') b on a.fcashacccode=b.fcashacccode ";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            if(this.sPortCury.equals("CNY"))
            {
               dInMoney = rs.getDouble("ftotalcost");
            }
            else if (sPortCury.equals("USD"))
             {
                dBaseRate=this.getExchangeRate("CNY",this.sPort,"base", startDate);
                dInMoney = rs.getDouble("ftotalcost")*dBaseRate;
             }
         }
         
         return dInMoney;
      }
      catch (Exception e) {
         throw new YssException("获取流入证券清算款出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   //获取应付以及支付证券清算款
   public double getOutMoney(java.util.Date dDate, int flag) throws
         YssException {
      String strSql = "";
      ResultSet rs = null;
      String strCon = ""; //条件
      double dInMoney = 0;
      double dBaseRate=0;
      try {
         if (flag == 0) { //当这个标识是零时，代表是应付，当这个标识是一时，代表是到账的证券清算款
            strCon = " and FBargainDate<="
                  + dbl.sqlDate(dDate) + " and FSettleDate>" +
                  dbl.sqlDate(dDate);
         }
         else if (flag == 1) {
            strCon = " and FSettleDate=" + dbl.sqlDate(dDate);
         }
         //人民币证券应付清算款
         strSql = "select * from (select nvl(ftotalcost,0) as ftotalcost,fcashacccode,ftradetypecode,FBargainDate,FSettleDate from " +
               pub.yssGetTableName("tb_data_subtrade") +
               " where fportcode = " + dbl.sqlString(this.sPort) + " and ftradetypecode = '01'" + strCon + ") a  join (select * from "+pub.yssGetTableName("tb_para_cashaccount")+" where FCuryCode='"+this.sPortCury+"') b on a.fcashacccode=b.fcashacccode ";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            if (this.sPortCury.equals("CNY")) {
               dInMoney = rs.getDouble("ftotalcost");
            }
            else if (sPortCury.equals("USD")) {
               dBaseRate = this.getExchangeRate("CNY", this.sPort, "base",
            		   startDate);
               dInMoney = rs.getDouble("ftotalcost") * dBaseRate;
            }
         }
         return dInMoney;
      }
      catch (Exception e) {
         throw new YssException("获取流出证券清算款出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

 //获取当日结算的外汇交易的流入流出
   public double getWhMoney(java.util.Date dDate, int flag,String sCury) throws
         YssException {
      String strSql = "";
      ResultSet rs = null;
      String strCon = ""; //条件
      double dBaseRate=0;
      double dInMoney = 0,dWhMoney = 0;
      try {
    	  if(flag == 0){//主托管行帐户
    		  strCon = "not like '次托管行%'";
    	  }else{//次托帐户
    		  strCon = "like '次托管行%'";
    	  }
    	  
    	 if (sCury.startsWith("!")) {
              strCon = strCon + " and fcurycode != '"+sCury.substring(1)+"'";
         }else{
              strCon = strCon + " and fcurycode = '"+sCury+"'";
         }
    	  
    	  strSql ="select d.*,c.fcurycode from (select case when FInOut=1 then a.FMoney else -a.FMoney end as FMoney,FCashAccCode,ftransferdate,b.Ftsftypecode,b.FSubTsfTypeCode from " +
    	  		  pub.yssGetTableName("Tb_Cash_SubTransfer")+" a  inner join  "+pub.yssGetTableName("Tb_Cash_Transfer")+" b on a.fnum = b.fnum ) d " +
    	  		  "left join (select * from "+pub.yssGetTableName("tb_para_cashaccount")+") c on c.fcashacccode = d.FCashAccCode " +
    	  		  "where FCASHACCNAME "+strCon+" and d.Ftsftypecode='01' and d.FSubTsfTypeCode='0004' and d.ftransferdate =" + dbl.sqlDate(dDate);
    	  rs=dbl.openResultSet(strSql);
         
         while (rs.next()) {
            	   dBaseRate = this.getExchangeRate(rs.getString("fcurycode"), this.sPort, "base", startDate);
            	   dInMoney = rs.getDouble("FMoney")*dBaseRate/this.dPortRate;
            	   dWhMoney = dWhMoney + dInMoney;
         }
         
         return dWhMoney;
      }
      catch (Exception e) {
         throw new YssException("获取外汇交易流入流出金额出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }
   
   //获取是用CNY做为本位币还是用美元做为本位币
public void getPortCury() throws  YssException {
   String strSql = "";
   ResultSet rs = null;
   try {
      //获取本位币币种
      strSql = "select fportcury from " + pub.yssGetTableName("tb_para_portfolio") + " where fportcode =" + dbl.sqlString(this.sPort);
      rs = dbl.openResultSet(strSql);
      while (rs.next()) {
        sPortCury=rs.getString("fportcury");
      }
   }
   catch (Exception e) {
      throw new YssException("获取本位币币种信息出错", e);
   }
   finally {
      dbl.closeResultSetFinal(rs);
   }
}
/**
 *
 * @param dDate Date 日期
 * @param flag int 标示是股票还是债券
 * @throws YssException
 * @return double
 */
//获取股票或者债券的市值
     public double getMarketValue(java.util.Date dDate,int flag) throws
           YssException {
        String strSql = "";
        ResultSet rs = null;
        String strCon = "";
        double dEqMarketValue = 0;
        try {
            //从净值表里获取股票市值
            if (flag == 0) {
               strCon = " and fgradetype1='EQ'";
            }
            //从净值表里获取债券市值
            else if (flag == 1) {
               strCon = " and fgradetype1='FI'";
            }
            strSql = "select a.fportmarketvalue as feqmarketvalue,fcurycode from (select fportmarketvalue,fnavdate,fcurycode from " +
                 pub.yssGetTableName("tb_data_navdata") +
                 " where  fportcode = " + dbl.sqlString(sPort) + " and fnavdate="
                 + dbl.sqlDate(dDate) + " and fretypecode='Security' "  + strCon + " and fdetail =4 and fcurycode != 'CNY'" +
                 ") a";
           rs = dbl.openResultSet(strSql);
           while (rs.next()) {
                   dEqMarketValue = dEqMarketValue + rs.getDouble("feqmarketvalue");
           }
           return dEqMarketValue;
        }
        catch (Exception e) {
           throw new YssException("获取股票的市值", e);
        }
        finally {
           dbl.closeResultSetFinal(rs);
        }
     }

   //获取债券的市值
   public void getFiMarketValue(java.util.Date dDate) throws
       YssException {
       String strSql = "";
       ResultSet rs = null;
       String strCon="";
       try {
           //从净值表里获取所有明细债券的市值数据
           strSql = "select a.fportmarketvalue as ffimarketvalue,b.FInsStartDate as FInsStartDate,b.FInsEndDate as FInsEndDate from (select fportmarketvalue,fnavdate,fkeycode from " +
               pub.yssGetTableName("tb_data_navdata") +
               " where  fportcode = " + dbl.sqlString(sPort)+ " and fcurycode='CNY' and fnavdate="
               + dbl.sqlDate(dDate) + " and fretypecode='Security' and fdetail=0 and fgradetype1='FI' and fgradetype6 is null" +
               ") a join (select * from  " + pub.yssGetTableName("tb_para_fixinterest") + ") b on a.fkeycode =b.fsecuritycode";
           rs = dbl.openResultSet(strSql);
           while (rs.next()) {
               //以下用来判断该债券为长期的还是短期的债券
               if (YssFun.dateDiff(rs.getDate("FInsStartDate"), rs.getDate("FInsEndDate")) <= 365) {
                   dDQFiMktvalue = dDQFiMktvalue + rs.getDouble("ffimarketvalue");
               } else {
                   dCQFiMktvalue = dCQFiMktvalue + rs.getDouble("ffimarketvalue");
               }
           }
       } catch (Exception e) {
           throw new YssException("获取债券的市值", e);
       } finally {
           dbl.closeResultSetFinal(rs);
       }
   }


   /**
    * 显示报表的RMB资产(cash含清算款+短债+股票),人民币可用头寸,换汇,人民币银行存款,非人民币银行存款,小计（银行存款之和）
    * @param dDate Date
    * @throws YssException
    */
   protected void buildUp(java.util.Date dDate) throws YssException {
      try {
       /*  double dTemp = 0;
         java.util.Date secDate = null; //第二天的日期
         secDate = this.getWorkDate(YssFun.addDay(startDate, 1));
         //RMB资产(cash含清算款+短债+股票)
         //(前日余额+换汇+当日交收净额-当日人民币应收清算款-当日RMB应付清算款+短期债券+证券买入金额（包括交易所和银行间T+1）-证券卖出金额（包括交易所和银行间T+1）)
         dTemp = this.getAsset(startDate) +
               this.getSGMoney(secDate, 0) -
               this.getSHMoney(secDate, 0) + this.getInMoney(secDate, 0) -
               this.getOutMoney(secDate, 0);
         bufAssert.append("RMB在途现金").append(",");
         bufAssert.append("RMB资产(cash含清算款+短债+股票)").append(",");
         bufAssert.append(String.valueOf(this.getAsset(startDate))).append(",");
         bufAssert.append(String.valueOf(dTemp)).append(",");
         bufAssert.append(" ").append(",");
         bufAssert.append(" ").append(",");
         bufAssert.append(" ").append(",");
         bufAssert.append(" ").append(",");
         bufAssert.append(" ").append(",");
         bufAssert.append(" ").append(",");
         bufAssert.append(" ").append(",");
         this.bufFianl.append(fixPub.buildRowCompResult(bufAssert.toString(),
               "DSDays000005")).append("\r\n");

         //人民币可用头寸
         //前日余额+前日应收申购款-应付赎回款+前日应收证券清算款-应付证券清算款+换汇)
         dTemp = 0; //再次赋值之前，先把此变量赋值为0
         dTemp = this.getAbaclanceCNY(startDate) +
               this.getSGMoney(startDate, 0) -
               this.getSHMoney(secDate, 0) + this.getInMoney(startDate, 0) -
               this.getOutMoney(secDate, 0);

         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append("人民币可用头寸").append(",");
         bufCNYAbaclance.append(String.valueOf(this.getAbaclanceCNY(startDate))).
               append(
               ",");
         bufCNYAbaclance.append(String.valueOf(dTemp)).append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         this.bufFianl.append(fixPub.buildRowCompResult(bufCNYAbaclance.
               toString(),
               "DSDays000005")).append("\r\n");

         //换汇
         dTemp = 0; //再次赋值之前，先把此变量赋值为0
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append("换汇").append(",");
         bufCNYAbaclance.append(String.valueOf(this.getAbaclanceCNY(startDate))).
               append(",");
         bufCNYAbaclance.append(String.valueOf(dTemp)).append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         this.bufFianl.append(fixPub.buildRowCompResult(bufCNYAbaclance.
               toString(),
               "DSDays000005")).append("\r\n");

         buildEmpty(); //增加一行空行

         //人民币银行存款
         dTemp = 0; //再次赋值之前，先把此变量赋值为0
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append("人民币银行存款").append(",");
         bufCNYAbaclance.append(String.valueOf(this.getAbaclanceCNY(dDate))).
               append(",");
          //第二天取的是前一天的余额+外币股票收付
         bufCNYAbaclance.append(String.valueOf(dTemp)).append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         this.bufFianl.append(fixPub.buildRowCompResult(bufCNYAbaclance.
               toString(),
               "DSDays000005")).append("\r\n");

         //非人民币银行存款
         dTemp=0;
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append("非人民币银行存款").append(",");
         bufCNYAbaclance.append(String.valueOf(this.getAbaclanceNCNY(dDate))).
               append(",");
         //第二天取的是前一天的余额+当日净交收金额+当日RMB应收清算款-当日RMB应付清算款
         bufCNYAbaclance.append(String.valueOf(dTemp)).append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append(" ").append(",");
         this.bufFianl.append(fixPub.buildRowCompResult(bufCNYAbaclance.
               toString(),
               "DSDays000005")).append("\r\n");*/
      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }
   }

   protected void buildEmpty() throws YssException {
      StringBuffer bufEmpty = null;
      try {
         bufEmpty = new StringBuffer();
         //中间空一行
         bufEmpty.append(" ").append(",");
         bufEmpty.append(" ").append(",");
         bufEmpty.append(" ").append(
               ",");
         bufEmpty.append(" ").append(",");
         bufEmpty.append(" ").append(",");
         bufEmpty.append(" ").append(",");
         bufEmpty.append(" ").append(",");
         bufEmpty.append(" ").append(",");
         bufEmpty.append(" ").append(",");
         bufEmpty.append(" ").append(",");
         bufEmpty.append(" ").append(",");
         this.bufFianl.append(fixPub.buildRowCompResult(bufEmpty.toString(),
               "DSDays000005")).append("\r\n");

      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }

   }

   //串接字符串 发送到前台进行显示
   protected String buildResult(java.util.Date startDate, String sPort) throws
         YssException {
      ResultSet rs = null;
      String strResult = "";
      java.util.Date dDate = null;
      StringBuffer bufDate = null;//日期字符串
      StringBuffer bufRMBAssert = null;//串接RMB资产(cash含清算款+短债+股票)
      double drmbzc=0;
      double dqrrmbzc=0;
      StringBuffer bufCNYAbaclance = null;//人民币可用头寸
      double dcnyketc=0;
      double dqrcnyketc=0;//前日人民币可用头寸
      StringBuffer bufRateMoney=null;//换汇
      double dratemoney=0;
      StringBuffer bufCNYStorage=null;//CNY银行存款
      double dcnystorage=0;
      double dqrcnystorage=0;//前日的CNY银行存款

      StringBuffer bufJnUSDStorage=null;//境内美元银行存款
      double dJnUSDstorage=0;
      double dqrJnUSDstorage=0;//前日境内美元银行存款

      StringBuffer bufJwUSDStorage = null; //境外美元银行存款
      double dJwUSDstorage = 0;
      double dqrJwUSDstorage = 0; //前日境外美元银行存款

      StringBuffer bufJwNUSDStorage = null; //境外非美元银行存款
      double dJwNUSDstorage = 0;
      double dqrJwNUSDstorage = 0; //前日境外非美元银行存款

      StringBuffer bufStorage=null;
      double dstorage=0;//小计，所有银行存款

      StringBuffer bufSGDZ = null; //申购款到账的串接字符串
      double dSgdz = 0; //申购款到账
      StringBuffer bufSHZF = null; //赎回款支付的串接字符串
      double dShzf = 0; //赎回款支付
      StringBuffer bufJSJE = null; //串接当日交收净额字符串
      double dJsje = 0; //当日交收净额
      double dQrJsje=0;//前日交收净额
      double dWhJsje = 0; //当日外汇交收净额
      StringBuffer bufYsCnyMoney = null; //应收人民币证券清算款的串接字符串
      double dYsCnyMoney = 0; //应收人民币证券清算款
      StringBuffer bufYfCnyMoney = null; //应付人民币证券清算款的串接字符串
      double dYfCnyMoney = 0; //应付人民币证券清算款

      StringBuffer bufGnFIDFK= null; //国内债券兑付款的字符串
      double dGnFidfk = 0; //国内债券兑付款

      StringBuffer bufInOutMoney = null; //外币股票的支付的字符串
      double dInOutMoney = 0; //外币股票收付

      StringBuffer bufGwzqsf = null; //外币债券的支付的字符串
      double dGwzqsf = 0; //国外债券收付

      StringBuffer bufGwFIDFK= null; //国外债券兑付款的字符串
      double dGwFIDFK = 0; //国外债券兑付款


      StringBuffer bufNavValue = null; //资产净值的串接字符串
      double dNavValue = 0;
      StringBuffer bufSG = null; //申购款的串接字符串
      double dsg = 0; //申购
      StringBuffer bufSH = null; //赎回款的串接字符串
      double dsh = 0; //赎回
      StringBuffer bufAssert = null; //预估净资产的串接字符串
      double dAssert = 0; //预估净资产
      double dQrAssert=0;//前日预估净资产
      StringBuffer bufCashScale = null; //现金比例
      double dcashscale = 0; //现金比例
      StringBuffer bufMarketFi = null; //国内短期债券串接字符串
      double dMarketFi = 0; //国内短期债券
      //double dQrMarketFi = 0; //前日短期债券市值

      StringBuffer bufCqMarketFi = null; //国内长期债券串接字符串
      double dCqMarketFi = 0; //国内长期债券
      //double dCqQrMarketFi = 0; //前日长期期债券市值

      StringBuffer bufHjMarketFi = null; //国内债券合计串接字符串
      double dHjMarketFi = 0; //国内债券合计
      //double dHjQrMarketFi = 0; //前日短期债券市值


      StringBuffer bufFiScale = null; //国内债券比例串接字符串
      double dFiScale = 0; //国内债券比例
      StringBuffer bufBMoney = null; //证券买入金额（包括交易所和银行间T+1）的串接字符串
      double dBMoney = 0; //证券买入金额（包括交易所和银行间T+1）
      StringBuffer bufSMoney = null; //证券卖出金额（包括交易所和银行间T+1）的串接字符串
      double dSMoney = 0; //证券卖出金额（包括交易所和银行间T+1）
      StringBuffer bufBsMoney = null; //国外股票变动的字符串
      double dBsMoney = 0; //国外股票变动
      StringBuffer bufMarketEq = null; //股票投资串接字符串
      double dMarketEq = 0; //股票投资
      double dQrMarketEq = 0; //前日股票投资
      StringBuffer bufEqScale = null; //持股比例串接字符串
      double dEqScale = 0; //持股比例

      StringBuffer bufGwBsMoneyFi = null; //国外债券变动的字符串
      double dGwBsMoneyFi = 0; //国外债券变动
      StringBuffer bufGwMarketFi = null; //国外债券合计
      double dGwMarketFi = 0; //国外债券合计
      double dQrGwMarketFi = 0; //前日国外债券合计
      StringBuffer bufGwFiScale = null; //持债比例串接字符串
      double dGwFiScale = 0; //持债比例

      StringBuffer bufStorageScale = null; //持仓比例合计串接字符串
      double dStorageScale = 0; //持仓比例合计
      StringBuffer bufScale = null; //比例合计串接字符串
      double dScale = 0; //比例合计

      try {
          getPortCury();//获取核算币种
         bufDate = new StringBuffer();//日期
         bufRMBAssert = new StringBuffer();//RMB资产
         bufCNYAbaclance = new StringBuffer();//RMB可用头寸
         bufRateMoney = new StringBuffer();//外汇
         bufCNYStorage = new StringBuffer();//境内人民币银行存款
         bufJnUSDStorage = new StringBuffer();//境内美元帐户银行存款
         bufJwUSDStorage = new StringBuffer();//境外美元帐户银行存款
         bufJwNUSDStorage = new StringBuffer();//境外非美元帐户银行存款
         bufStorage = new StringBuffer();//小计
         bufSGDZ = new StringBuffer();
         bufSHZF = new StringBuffer();
         bufJSJE = new StringBuffer();//当日交收净额
         bufYsCnyMoney = new StringBuffer();
         bufYfCnyMoney = new StringBuffer();
         bufGnFIDFK = new StringBuffer(); //国内债券兑付款
         bufInOutMoney = new StringBuffer(); //外币股票支付
         bufGwzqsf = new StringBuffer(); //外币债券收付
         bufGwFIDFK = new StringBuffer(); //国外债券兑付款
         bufNavValue = new StringBuffer(); //资产净值
         bufSG = new StringBuffer();//申购
         bufSH = new StringBuffer();//赎回
         bufAssert = new StringBuffer();//预估净资产
         bufCashScale = new StringBuffer();
         //国内债券
         bufMarketFi = new StringBuffer();
         bufCqMarketFi = new StringBuffer();
         bufHjMarketFi = new StringBuffer();
         bufFiScale = new StringBuffer();//国内债券比例

         bufBMoney = new StringBuffer();
         bufSMoney = new StringBuffer();
         bufBsMoney = new StringBuffer();
         bufMarketEq = new StringBuffer();
         bufEqScale = new StringBuffer();
         //国外债券
         bufGwBsMoneyFi = new StringBuffer();
         bufGwMarketFi = new StringBuffer();
         bufGwFiScale = new StringBuffer();

         bufStorageScale=new StringBuffer();
         bufScale = new StringBuffer();


         bufDate.append(" ").append(",");
         bufDate.append("日期").append(",");
         //外循环要显示的各个项
         //首先加载银行存款，要考虑节假日
         bufRMBAssert.append("RMB在途现金").append(",");
         bufRMBAssert.append("RMB资产(cash含清算款+短债)").append(",");
         bufCNYAbaclance.append(" ").append(",");
         bufCNYAbaclance.append("人民币可用头寸").append(",");
         bufRateMoney.append(" ").append(",");
         bufRateMoney.append("换汇("+this.sPortCury+"换入换出)").append(",");

         bufCNYStorage.append(" ").append(",");
         bufCNYStorage.append("主托管行-"+this.sPortCury).append(",");
         bufJnUSDStorage.append(" ").append(",");
         bufJnUSDStorage.append("主托管行-"+this.sBaseCury).append(",");
         bufJwUSDStorage.append(" ").append(",");
         bufJwUSDStorage.append("境外"+this.sBaseCury).append(",");
         bufJwNUSDStorage.append(" ").append(",");
         bufJwNUSDStorage.append("境外非"+this.sBaseCury+"货币").append(",");

         bufStorage.append(" ").append(",");
         bufStorage.append("小计").append(",");


         bufSGDZ.append("申购款到帐").append(",");
         bufSGDZ.append("当日交收申购款(R+2)").append(",");
         bufSHZF.append("赎回款支付").append(",");
         bufSHZF.append("当日交收赎回款(R+7)").append(",");
         bufJSJE.append(" ").append(",");
         bufJSJE.append("当日交收净额").append(",");
         bufYsCnyMoney.append(" ").append(",");
         bufYsCnyMoney.append("应收证券清算款("+this.sPortCury+")").append(",");
         bufYfCnyMoney.append(" ").append(",");
         bufYfCnyMoney.append("应付证券清算款("+this.sPortCury+")").append(",");
         bufGnFIDFK.append(" ").append(",");
         bufGnFIDFK.append("国内债券兑付款").append(",");
         bufInOutMoney.append(" ").append(",");
         bufInOutMoney.append("外币股票收付").append(",");
         bufGwzqsf.append(" ").append(",");
         bufGwzqsf.append("外币债券收付").append(",");
         bufGwFIDFK.append(" ").append(",");
         bufGwFIDFK.append("国外债券兑付款").append(",");
         bufNavValue.append(" ").append(",");
         bufNavValue.append("资产净值").append(",");
         bufSG.append(" ").append(",");
         bufSG.append("申购").append(",");
         bufSH.append(" ").append(",");
         bufSH.append("赎回").append(",");
         bufAssert.append(" ").append(",");
         bufAssert.append("预估净资产").append(",");
         bufCashScale.append(" ").append(",");
         bufCashScale.append("现金比例").append(",");

         bufMarketFi.append(" ").append(",");
         bufMarketFi.append("国内短期债券").append(",");
         bufCqMarketFi.append(" ").append(",");
         bufCqMarketFi.append("国内长期债券").append(",");
         bufHjMarketFi.append(" ").append(",");
         bufHjMarketFi.append("国内债券合计").append(",");
         bufFiScale.append(" ").append(",");
         bufFiScale.append("国内债券比例").append(",");

         bufBMoney.append(" ").append(",");
         bufBMoney.append("证券买入金额（包括交易所和银行间T+1）").append(",");
         bufSMoney.append(" ").append(",");
         bufSMoney.append("证券卖出金额（包括交易所和银行间T+1）").append(",");
         bufBsMoney.append(" ").append(",");
         bufBsMoney.append("国外股票变动").append(",");
         bufMarketEq.append(" ").append(",");
         bufMarketEq.append("国外股票合计").append(",");
         bufEqScale.append(" ").append(",");
         bufEqScale.append("持股比例").append(",");
         //国外债券
         bufGwBsMoneyFi.append(" ").append(",");
         bufGwBsMoneyFi.append("国外债券变动").append(",");
         bufGwMarketFi.append(" ").append(",");
         bufGwMarketFi.append("国外债券合计").append(",");
         bufGwFiScale.append(" ").append(",");
         bufGwFiScale.append("持债比例").append(",");

         bufStorageScale.append(" ").append(",");
         bufStorageScale.append("持仓比例").append(",");
         bufScale.append(" ").append(",");
         bufScale.append("比例合计").append(",");
         
         dDate =  YssFun.addDay(startDate, -1); //从期初日期（T-1日）循环开始
         
         //获取组合汇率，把获取的CNY币金额转换成USD
         dPortRate = this.getExchangeRate(sPortCury, this.sPort, "port", startDate);//获取T-1日的组合汇率
         if (dPortRate == 0) {
            dPortRate = 1;
         }
         for (int i = 0; i <= 8; i++) { //内循环天数
            if (i == 0) {
               dDate = getWorkDate(dDate,-1); //获得日期,若为节假日则再获取前一工作日
            }
            else{
               dDate = getWorkDate(YssFun.addDay(dDate, 1),1); //获得日期
            }
            bufDate.append(YssFun.formatDate(dDate,"yyyy-MM-dd")).append(",");//串接日期字符串
            dratemoney = this.getRateMoney(dDate); //换汇
            bufRateMoney.append(String.valueOf(dratemoney)).append(",");
            //当日交收申购款(R+2)
            dSgdz = this.getSGMoney(dDate, 1);
            bufSGDZ.append(String.valueOf(dSgdz)).append(",");
            //当日交收赎回款(R+7)
            dShzf = this.getSHMoney(dDate, 1);
            bufSHZF.append(String.valueOf(dShzf)).append(",");
            //当日交收净额
            dJsje=dSgdz-dShzf;
            bufJSJE.append(String.valueOf(dJsje)).append(",");

            //当日应收证券清算款(CNY)
            dYsCnyMoney = this.getInMoney(dDate,1);
            bufYsCnyMoney.append(String.valueOf(dYsCnyMoney)).append(",");
            //当日应付证券清算款(CNY)
            dYfCnyMoney = this.getOutMoney(dDate, 1);//以负数填列
            bufYfCnyMoney.append(String.valueOf(dYfCnyMoney*(-1))).append(",");
            //外汇交易当日结算金额
            dWhJsje = getWhMoney(dDate,0,this.sPortCury);//主托管行本位币
            
            //国内债券兑付款
            dGnFidfk = getFiDQDFMoney(dDate, "国内");
            bufGnFIDFK.append(String.valueOf(dGnFidfk)).append(",");
            //外币股票收付
            dInOutMoney = this.getInOutMoney(dDate, 1, "EQ");
            bufInOutMoney.append(String.valueOf(dInOutMoney)).append(",");
            //外币债券收付
            dGwzqsf = this.getInOutMoney(dDate, 1, "FI");;
            bufGwzqsf.append(String.valueOf(dGwzqsf)).append(",");
            //国外债券兑付款
            dGwFIDFK = getFiDQDFMoney(dDate, "国外");
            bufGwFIDFK.append(String.valueOf(dGwFIDFK)).append(",");

/**************************************************************银行存款***************************************/
            if (i == 0) {
               dcnystorage = this.getAbaclanceCNY(dDate);
            }
            else {
               dcnystorage = dqrcnystorage + dJsje + dYsCnyMoney - dYfCnyMoney + dWhJsje;//前日人民币银行存款+当日交收净额+当日RMB应收清算款-当日RMB应付清算款 + 外汇交易当日结算金额
            }
            if (i == 0) {
                dJnUSDstorage= this.getAbaclance(dDate,this.sBaseCury,"境内");
            }
            else {
            	if (this.sPortCury.equalsIgnoreCase(this.sBaseCury)) {
                    dJnUSDstorage = dqrJnUSDstorage + dJsje + getWhMoney(dDate,0,this.sBaseCury); //前日境内基础货币帐户+当日交收净额（如果是基础货币申购赎回）
                } else {
                    dJnUSDstorage = dqrJnUSDstorage + getWhMoney(dDate,0,this.sBaseCury); //前日境内基础货币帐户赋值给境内基础货币帐户
                }
               /* if (this.sPortCury.equalsIgnoreCase("USD")) {
                    dJnUSDstorage = dqrJnUSDstorage + dJsje; //前日境内美元帐户+当日交收净额（如果是美元申购赎回）
                } else {
                    dJnUSDstorage = dqrJnUSDstorage; //前日境内美元帐户赋值给境内美元帐户
                }*/
            }
            if (i == 0) {
                dJwUSDstorage= this.getAbaclance(dDate,this.sBaseCury /*"USD"*/,"境外");//境外基础货币(美元)帐户余额
            }
            else {
              //第二天境外美元帐户余额  前日的余额+外币股票收付+外币债券收付+国外债券兑付款
              dJwUSDstorage=dqrJwUSDstorage + dInOutMoney + dGwzqsf + dGwFIDFK + getWhMoney(dDate,1,this.sBaseCury);
            }
            if (i == 0) {
                 dJwNUSDstorage = this.getAbaclance(dDate, "!"+this.sBaseCury, "境外"); //境外非基础货币(美元)帐户余额
            } else {
                 dJwNUSDstorage=dqrJwNUSDstorage + getWhMoney(dDate,1,"!"+this.sBaseCury);
            }
            bufCNYStorage.append(String.valueOf(dcnystorage)).append(",");
            bufJnUSDStorage.append(String.valueOf(dJnUSDstorage)).append(",");
            bufJwUSDStorage.append(String.valueOf(dJwUSDstorage)).append(",");
            bufJwNUSDStorage.append(String.valueOf(dJwNUSDstorage)).append(",");

            dstorage = dcnystorage + dJnUSDstorage + dJwUSDstorage + dJwNUSDstorage;//银行存款小计
            bufStorage.append(String.valueOf(dstorage)).append(",");
/**************************************************************银行存款***************************************/
            //取资产净值
            if (i == 0) {
               dNavValue = this.getNavValue(dDate);
               dAssert=dNavValue;//当是T-1日时，预估净资产=资产净值
               bufNavValue.append(String.valueOf(dNavValue)).append(",");
            }
            else {
               dNavValue = dQrAssert * 1.01; //第二天取的是前一天的预估净资产*0.01，因为第二天是没有资产净值的,依此类推
               bufNavValue.append(String.valueOf(dNavValue)).append(",");
            }
            //申购
            dsg = this.getSGMoney(dDate, 0);
            bufSG.append(String.valueOf(dsg)).append(",");
            //赎回
            dsh = this.getSHMoney(dDate, 0);
            bufSH.append(String.valueOf(-1*dsh)).append(",");//以负数填列

            //取预估净资产
            if (i == 0) {//如果是T-1日的话，预估净资产和资产净值相等
               bufAssert.append(String.valueOf(dAssert)).append(",");
            }
            else {
               dAssert = dNavValue + dsg - dsh;//资产净值+申购-赎回
               bufAssert.append(String.valueOf(dAssert)).append(",");
            }

            if (i == 0) {
               this.getFiMarketValue(dDate);
               dMarketFi = this.dDQFiMktvalue;
               dCqMarketFi = this.dCQFiMktvalue;
            }
            //国内短期债券
            bufMarketFi.append(String.valueOf(dDQFiMktvalue)).append(",");
            //国内长期债券
            bufCqMarketFi.append(String.valueOf(dCQFiMktvalue)).append(",");

            //国内债券合计
            dHjMarketFi = dMarketFi+dCqMarketFi;
            bufHjMarketFi.append(String.valueOf(dHjMarketFi)).append(",");

            if (dAssert == 0) {
               dcashscale = 0;
            }
            else {
               dcashscale = YssFun.roundIt( (dstorage / dAssert) * 100, 2); //现金比例 = 所有币种折算成人民币的金额/预估资产
            }
            bufCashScale.append(String.valueOf(dcashscale) + "%").append(",");

            if (dAssert == 0) {
               dFiScale = 0;
            }
            else {
               dFiScale = YssFun.roundIt( (dMarketFi / dAssert) * 100, 2); //国内债券比例 = 所有币种折算成人民币的金额/预估资产
            }
            bufFiScale.append(String.valueOf(dFiScale) + "%").append(",");
            //证券买入金额（包括交易所和银行间T+1）
            dBMoney = this.getInMoney(dDate, 0);
            bufBMoney.append(String.valueOf(dBMoney)).append(",");
            //证券卖出金额（包括交易所和银行间T+1）
            dSMoney = this.getOutMoney(dDate, 0);
            bufSMoney.append(String.valueOf(dSMoney*(-1))).append(",");
            /********************************************国外股票***********************************************************/
            dBsMoney = -this.getInOutMoney(dDate,0,"EQ");
            bufBsMoney.append(String.valueOf(dBsMoney)).append(","); //国外股票变动的字符串
            if (i == 0) {
               dMarketEq = this.getMarketValue(dDate,0); //国外股票合计
            }
            else {
               dMarketEq = dQrMarketEq * 1.01 + dBMoney - dSMoney + dBsMoney;//股票市值*1.01
            }
            bufMarketEq.append(String.valueOf(dMarketEq)).append(","); //股票投资串接字符串
            if (dAssert == 0) {
               dEqScale = 0;
            }
            else {
               dEqScale = YssFun.roundIt( (dMarketEq / dAssert) * 100, 2); //股票比例=股票的市值/预估资产
            }
            bufEqScale.append(String.valueOf(dEqScale) + "%").append(","); //持股比例串接字符串
             /********************************************国外股票***********************************************************/

             /********************************************国外债券***********************************************************/
             dGwBsMoneyFi= this.getInOutMoney(dDate, 0, "FI");
             bufGwBsMoneyFi.append(String.valueOf(dGwBsMoneyFi)).append(","); //国外债券变动的字符串
             if (i == 0) {
                 dGwMarketFi = this.getMarketValue(dDate, 1); //国外债券合计
             }
             else {
                //第二天国外债券合计 等于 前一日的余额*1.01 + 国外债券变动
                dGwMarketFi=dQrGwMarketFi*1.01 + dGwBsMoneyFi;
             }
             bufGwMarketFi.append(String.valueOf(dGwMarketFi)).append(","); //国外债券串接字符串
             if (dAssert == 0) {
                dGwFiScale = 0;
             }
             else {
                dGwFiScale = YssFun.roundIt( (dGwMarketFi / dAssert) * 100, 2); //国外债券比例=国外债券的市值/预估资产
             }
             bufGwFiScale.append(String.valueOf(dGwFiScale) + "%").append(","); //国外债券比例串接字符串
             /********************************************国外债券***********************************************************/

            //持仓比例=国内债券比例+持股比例+持债比例
            dStorageScale = YssFun.roundIt( (dFiScale + dEqScale+dGwFiScale), 4);
            bufStorageScale.append(String.valueOf(dStorageScale) + "%").append(","); //持仓比例合计串接字符串
             //比例合计=国内债券比例+持股比例+持债比例+现金比例
            dScale = YssFun.roundIt( (dcashscale + dFiScale + dEqScale+dGwFiScale), 4);
            bufScale.append(String.valueOf(dScale) + "%").append(","); //比例合计串接字符串

            //显示报表的RMB资产(cash含清算款+短债+股票)
            //计算公式=前日余额+换汇+当日交收净额+当日RMB应收清算款-当日RMB应付清算款+短期债券+证券买入金额（包括交易所和银行间T+1）-证券卖出金额（包括交易所和银行间T+1）
            if (i == 0) {//如果为第一天，直接获取RMB资产
              drmbzc = this.getAsset(dDate);
           }
           else {
              drmbzc=dqrrmbzc+dratemoney+ dYsCnyMoney - dYfCnyMoney + dGnFidfk + dJsje + dBMoney-dSMoney;
           }
            bufRMBAssert.append(String.valueOf(drmbzc)).append(",");
            //CNY可用头寸
            //计算公式=前日余额+换汇+当日RMB应收清算款-当日RMB应付清算款+债券兑付款+（如果交收净额<0,则为交收净额，否则0）+（如果前日交收净额>0,则为前日交收净额，否则0）
            if (i == 0) {//如果为第一天，直接获取CNY可用头寸
               dcnyketc = getCnyUserCash(dDate);
            }
            else {
               if (dQrJsje <= 0) {
                  dQrJsje = 0;
               }
               if (dJsje >= 0) {//交收净额不能直接赋值，因为要赋值给前日交收金额
                  dcnyketc = dqrcnyketc + dratemoney + dYsCnyMoney -
                        dYfCnyMoney +
                        dGnFidfk + dQrJsje + 0;
               }
               else {
                  dcnyketc = dqrcnyketc + dratemoney + dYsCnyMoney -
                        dYfCnyMoney +
                        dGnFidfk + dQrJsje + dJsje;
               }
            }
            bufCNYAbaclance.append(String.valueOf(dcnyketc)).append(",");
            dqrcnyketc = dcnyketc; //把可用头寸赋值给前日可用头寸
            dqrrmbzc = drmbzc; //把RMB资产赋值给前日RMB资产
            dQrJsje = dJsje; //把交收金额赋值给前日交收金
            dqrcnystorage = dcnystorage; //把人民币银行存款赋值给前日人民币银行存款
            dqrJwUSDstorage = dJwUSDstorage; //把境外美元帐户的余额赋值给前日境外美元帐户
            dqrJwNUSDstorage = dJwNUSDstorage; //把境外非美元帐户的余额赋值给前日境外非美元帐户
            dqrJnUSDstorage=dJnUSDstorage; //把境内美元帐户的余额赋值给前日境外美元帐户
            dQrMarketEq = dMarketEq; //股票投资
            dQrGwMarketFi = dGwMarketFi; //国外债券投资
            dQrAssert = dAssert; //预估净资产赋值给前日预估净资产
         }
         this.bufFianl.append(fixPub.buildRowCompResult(bufDate.toString(),
               "DSDays000005")).append("\r\n");//日期
         this.bufFianl.append(fixPub.buildRowCompResult(bufRMBAssert.toString(),
               "DSDays000005")).append("\r\n");//人民币现金及现金等价物（cash含清算款+短债）
         this.bufFianl.append(fixPub.buildRowCompResult(bufCNYAbaclance.toString(),
               "DSDays000005")).append("\r\n");//RMB可用头寸
         this.bufFianl.append(fixPub.buildRowCompResult(bufRateMoney.toString(),
               "DSDays000005")).append("\r\n"); //外汇
         buildEmpty(); //增加一行空的
         this.bufFianl.append(fixPub.buildRowCompResult(bufCNYStorage.toString(),
               "DSDays000005")).append("\r\n"); //主托管行本位币银行存款
         this.bufFianl.append(fixPub.buildRowCompResult(bufJnUSDStorage.
               toString(),
               "DSDays000005")).append("\r\n"); //境内主托管行基础货币银行存款
         this.bufFianl.append(fixPub.buildRowCompResult(bufJwUSDStorage.
               toString(),
               "DSDays000005")).append("\r\n"); //境外基础货币（USD）银行存款
         this.bufFianl.append(fixPub.buildRowCompResult(bufJwNUSDStorage.
               toString(),
               "DSDays000005")).append("\r\n"); //境外非基础货币（USD）银行存款
         this.bufFianl.append(fixPub.buildRowCompResult(bufStorage.toString(),
               "DSDays000005")).append("\r\n"); //小计
         buildEmpty(); //增加一行空的


         this.bufFianl.append(fixPub.buildRowCompResult(bufSGDZ.toString(),
               "DSDays000005")).append("\r\n");
         this.bufFianl.append(fixPub.buildRowCompResult(bufSHZF.toString(),
               "DSDays000005")).append("\r\n");
         this.bufFianl.append(fixPub.buildRowCompResult(bufJSJE.toString(),
               "DSDays000005")).append("\r\n"); //增加一行交收净额
         buildEmpty(); //增加一行空的
         this.bufFianl.append(fixPub.buildRowCompResult(bufYsCnyMoney.toString(),
               "DSDays000005")).append("\r\n");//应收清算款（本位币）
         this.bufFianl.append(fixPub.buildRowCompResult(bufYfCnyMoney.toString(),
               "DSDays000005")).append("\r\n");//应付清算款（本位币）
         this.bufFianl.append(fixPub.buildRowCompResult(bufGnFIDFK.toString(),
               "DSDays000005")).append("\r\n"); //国内债券兑付款
         buildEmpty(); //增加一行空的
         this.bufFianl.append(fixPub.buildRowCompResult(bufInOutMoney.toString(),
               "DSDays000005")).append("\r\n"); //外币股票收付
         this.bufFianl.append(fixPub.buildRowCompResult(bufGwzqsf.toString(),
               "DSDays000005")).append("\r\n"); //外币债券收付
         this.bufFianl.append(fixPub.buildRowCompResult(bufGwFIDFK.toString(),
               "DSDays000005")).append("\r\n"); //国外债券兑付款
         buildEmpty(); //增加一行空的
         this.bufFianl.append(fixPub.buildRowCompResult(bufNavValue.toString(),
               "DSDays000005")).append("\r\n"); //资产净值
         this.bufFianl.append(fixPub.buildRowCompResult(bufSG.toString(),
               "DSDays000005")).append("\r\n");
         this.bufFianl.append(fixPub.buildRowCompResult(bufSH.toString(),
               "DSDays000005")).append("\r\n");
         this.bufFianl.append(fixPub.buildRowCompResult(bufAssert.toString(),
               "DSDays000005")).append("\r\n"); //预估净资产
         buildEmpty(); //增加一行空的
         this.bufFianl.append(fixPub.buildRowCompResult(bufCashScale.toString(),
               "DSDays000005")).append("\r\n");

         buildEmpty(); //增加一行空的
         this.bufFianl.append(fixPub.buildRowCompResult(bufMarketFi.toString(),
               "DSDays000005")).append("\r\n");//国内短期债券
         this.bufFianl.append(fixPub.buildRowCompResult(bufCqMarketFi.toString(),
               "DSDays000005")).append("\r\n");//国内长期债券
         this.bufFianl.append(fixPub.buildRowCompResult(bufHjMarketFi.toString(),
               "DSDays000005")).append("\r\n");//国内债券合计
         this.bufFianl.append(fixPub.buildRowCompResult(bufFiScale.toString(),
               "DSDays000005")).append("\r\n");
         buildEmpty(); //增加一行空的
         this.bufFianl.append(fixPub.buildRowCompResult(bufBMoney.toString(),
               "DSDays000005")).append("\r\n");
         this.bufFianl.append(fixPub.buildRowCompResult(bufSMoney.toString(),
               "DSDays000005")).append("\r\n");
         buildEmpty(); //增加一行空的
         this.bufFianl.append(fixPub.buildRowCompResult(bufBsMoney.toString(),
               "DSDays000005")).append("\r\n"); //国外股票变动
         this.bufFianl.append(fixPub.buildRowCompResult(bufMarketEq.toString(),
               "DSDays000005")).append("\r\n"); //股票合计
         this.bufFianl.append(fixPub.buildRowCompResult(bufEqScale.toString(),
               "DSDays000005")).append("\r\n"); //持股比例
         this.bufFianl.append(fixPub.buildRowCompResult(bufGwBsMoneyFi.toString(),
               "DSDays000005")).append("\r\n"); //国外债券变动
         this.bufFianl.append(fixPub.buildRowCompResult(bufGwMarketFi.toString(),
               "DSDays000005")).append("\r\n"); //债券合计

         this.bufFianl.append(fixPub.buildRowCompResult(bufGwFiScale.toString(),
               "DSDays000005")).append("\r\n"); //持债比例
         buildEmpty(); //增加一行空的
         this.bufFianl.append(fixPub.buildRowCompResult(bufStorageScale.toString(),
               "DSDays000005")).append("\r\n"); //持仓比例合计
         this.bufFianl.append(fixPub.buildRowCompResult(bufScale.toString(),
               "DSDays000005")).append("\r\n"); //比例合计

         if (this.bufFianl.toString().length() > 2) {
            strResult = bufFianl.toString().substring(0,
                  bufFianl.toString().length() - 2);
         }
         return strResult;
      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

//  取这个日期 的最近一个工作日

   protected java.util.Date getWorkDate(java.util.Date dDate,int iDay) throws
         YssException {
      java.util.Date DReturn = null;
      ResultSet rs = null;
      boolean flag = true; //判断是不是要继续看是不是节假日
      String strSql = "";
      try {
         while (flag) {
            strSql =
                  " select fdate " +
                  " from (select * from tb_base_holidays where fholidayscode = " + dbl.sqlString(this.holiday) + ") m  join (select * " +
                  " from tb_base_childholiday) n on m.fholidayscode = n.fholidayscode where fdate = " +
                  dbl.sqlDate(dDate);
            rs = dbl.openResultSet(strSql);
            if (!rs.next()) {
               DReturn = dDate; //如果不是节假日
               flag = false; //就终止循环
            }
            else {
            	if(iDay>0){
                    dDate = YssFun.addDay(dDate, 1); //如果是节假日 就加上一天继续循环
            	}else{
                    dDate = YssFun.addDay(dDate, -1); //如果是节假日 就加上一天继续循环
            	}
               dbl.closeResultSetFinal(rs); //每循环就要打开一次记录集 所以都要先关闭
            }

         }

         return DReturn;
      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }
//----------------------------------------获取汇率-------------------------------
   public double getExchangeRate(String curyCode, String portCode,
                                 String rateType, java.util.Date dDate) throws
         YssException {
      double sResult = 0.0;
      // java.util.Date inceptionDate = null;
      // java.util.Date navDate = null;
      try {
         /*  inceptionDate = this.getInceptionDate();
           if (this.startDate.equals(inceptionDate)) {
              navDate = startDate;
           }
           else {
              navDate = YssFun.addDay(startDate, -1);
           }*/
         BaseOperDeal operDeal = new BaseOperDeal();
         operDeal.setYssPub(pub);
         sResult = operDeal.getCuryRate(dDate, curyCode,
                                        portCode, rateType);
         return sResult;

      }
      catch (Exception e) {
         throw new YssException("获取汇率出错!", e);
      }
   }

//--------------------------------------获取资产净值------------------------------
   public double getNetValue(java.util.Date startDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double sResult = 0.0;
      java.util.Date inceptionDate = null;
      java.util.Date navDate = null;
      try {

         inceptionDate = this.getInceptionDate();
         if (this.startDate.equals(inceptionDate)) {
            navDate = startDate;
         }
         else {
            navDate = maxNetDate;
         }
         strSql = " select FPortMarketValue from " +
               pub.yssGetTableName("tb_data_navdata") +
               " where FKeyCode='TotalValue' and  FNAVDATE=" +
               dbl.sqlDate(navDate);
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            sResult = rs.getDouble("FPortMarketValue");
         }
         sResult = YssD.mul(sResult, 0.02);
         return sResult;
      }
      catch (Exception e) {
         throw new YssException("获取净值出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

// ---------------------------------获取单位净值----------------------------------
   public double getUnitValue(java.util.Date startDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double sResult = 0.0;
      java.util.Date inceptionDate = null;
      java.util.Date navDate = null;
      try {
         inceptionDate = this.getInceptionDate();
         if (this.startDate.equals(inceptionDate)) {
            navDate = startDate;
         }
         else {
            navDate = maxNetDate;
         }
         strSql = " select FPrice from " +
               pub.yssGetTableName("tb_data_navdata") +
               " where FKeyCode='Unit' and  FNAVDATE=" + dbl.sqlDate(navDate);
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            sResult = rs.getDouble("FPrice");
         }
         return sResult;
      }
      catch (Exception e) {
         throw new YssException("获取单位净值出错", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   public java.util.Date getInceptionDate() throws YssException {
      PortfolioBean port = new PortfolioBean();
      try {
         port.setYssPub(pub);
         port.setPortCode(this.sPort);
         port.getSetting();
         return port.getInceptionDate();

      }
      catch (Exception e) {
         throw new YssException("获取成立日期报错");
      }
   }

   /**
    * 判断日期是不是节假日
    * by 陈嘉
    * @throws YssException
    * @return boolean
    */
   public boolean isHoliday(java.util.Date dDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      boolean isHoliday = true;
      try {
         strSql =
               "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = " +
               dbl.sqlString(this.holiday) + " and FDate=" + dbl.sqlDate(dDate);
         rs = dbl.openResultSet(strSql);
         if (rs.next()) {
            isHoliday = true;
         }
         else {
            isHoliday = false;
         }
         return isHoliday;
      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

public java.util.Date getMaxNetDate() {
	return maxNetDate;
}

public void setMaxNetDate(java.util.Date maxNetDate) {
	this.maxNetDate = maxNetDate;
}

   
}
