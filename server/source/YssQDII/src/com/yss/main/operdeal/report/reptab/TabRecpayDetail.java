package com.yss.main.operdeal.report.reptab;
import com.yss.base.BaseAPOperValue;

import com.yss.main.dayfinish.OffAcctBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import java.util.HashMap;
import java.sql.*;
import java.util.Iterator;

;
/*
 * 计算应收应付明细表
 * */
public class TabRecpayDetail
      extends BaseAPOperValue {

   private java.util.Date dBeginDate;
   private java.util.Date dEndDate;
   private String portCode;
   // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao ---
   private boolean isCreate; //是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询
   // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出  end -------------------
   
   private static final int Jd = 11; //借贷
   private static final int JY = 21; //结余

   private class RecpayDetailBean {

      //   int ftype;
      //     String fCuryCode; //货币
      String fdesc;
      String fbroker;
      String fprojectname;
      //   String fprojectcode;
      double fjd;
      double fJie; //
      double fDai; //
      double fresult;
      String fportCode; //组合
      java.sql.Date frpDate;
      java.sql.Date fgussDate;
      String forder;
      public void RecpayDetailBean() {
      }
   }

   public TabRecpayDetail() {
   }

   public void init(Object bean) throws YssException {
      String reqAry[] = null;
      String reqAry1[] = null;
      String sRowStr = (String) bean;
      if (sRowStr.trim().length() == 0) {
         return;
      }
      reqAry = sRowStr.split("\n");
//      reqAry1 = reqAry[0].split("\r");
//      this.dBeginDate = YssFun.toDate(reqAry1[1]);
//      reqAry1 = reqAry[1].split("\r");
//      this.dEndDate = YssFun.toDate(reqAry1[1]);
//      reqAry1 = reqAry[2].split("\r");
//      //  reqAry1 = reqAry[1].split("\r");
//      this.portCode = reqAry1[1];
//      // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao --- 
//      reqAry1 = reqAry[3].split("\r");
//      this.isCreate = reqAry1[1].equalsIgnoreCase("0")?false:true;
//      // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出  end -------------------
//      
      	//==================修改解析控件的值，前台控件值为空时不传值导致解析出错  edit by qiuxufeng 20101109 
		for (int i = 0; i < reqAry.length; i++) {
			reqAry1 = reqAry[i].split("\r");
			if(reqAry1[0].equalsIgnoreCase("1")) {
				this.dBeginDate = YssFun.toDate(reqAry1[1]);
			} else if(reqAry1[0].equalsIgnoreCase("2")) {
				this.dEndDate = YssFun.toDate(reqAry1[1]);
			} else if(reqAry1[0].equalsIgnoreCase("3")) {
				this.portCode = reqAry1[1];
			} else if(reqAry1[0].equalsIgnoreCase("4")) {
				this.isCreate = reqAry1[1].equalsIgnoreCase("0")?false:true;
			}
		}
		//=========end=========
   }

   public Object invokeOperMothed() throws YssException {
      HashMap valueMap = null;
      //createTempRecpayDetail();
      valueMap = new HashMap();
      try {
    	  createTempRecpayDetail();
    	  // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao --- 
    	 if(isCreate){
    		 //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
			 OffAcctBean offAcct = new OffAcctBean();
			 offAcct.setYssPub(this.pub);
			 String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
			 String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
			 if(!tmpInfo.trim().equalsIgnoreCase("")) {
				 return "<OFFACCT>" + tmpInfo;
			 }
			 //=================end=================
    		 getRecpay(valueMap);
    	 }
    	  // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出  end -------------------
      }
      catch (YssException ex) {
         throw new YssException(ex.getMessage());
      }

      return "";
   }

   /**
    * 创建用于存放未实现数据的表。
    * @throws YssException
    */
   private void createTempRecpayDetail() throws YssException {
      String strSql = "";
      try {
    	  // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao ---
        // if (dbl.yssTableExist(pub.yssGetTableName("tb_temp_RecpayDetail"))) {
    	  if (dbl.yssTableExist(pub.yssGetTableName("tb_rep_RecpayDetail"))) { //把临时表改为永久表
            return;
         }
         else {
            strSql = "create table " +
                 // pub.yssGetTableName("tb_temp_RecpayDetail") +
                 // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao
                  pub.yssGetTableName("tb_rep_RecpayDetail")+ 
                  " ( frpDate Date," +
                  " fdesc varchar2(100)," +
                  " fbroker varchar2(50)," +
                  " fproject varchar2(50)," +
                  " fJie number(18,6)," +
                  " fDai number(18,6)," +
                  " fjd  number(18,6)," +
                  " fresult number(18,6)," +
                  " FPortCode varchar2(20)," +
                  " forder varchar2(100)," +
                  " fgussDate Date )";
            dbl.executeSql(strSql);
         }
      }
      catch (Exception e) {
         throw new YssException("生成临时应收应付明细表出错!");
      }
   }

   
   private void deleteFromRecpayDetail() throws YssException {
      String sqlStr = "Delete from  " +
            //pub.yssGetTableName("tb_temp_RecpayDetail") +
            // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao
//            dbl.yssTableExist(pub.yssGetTableName("tb_rep_RecpayDetail"))+ 
      		//edit by qiuxufeng 20101117 
            pub.yssGetTableName("tb_rep_RecpayDetail")+ 
            " where fgussDate=" + dbl.sqlDate(dEndDate) +
            " and  FPortCode=" + dbl.sqlString(this.portCode);
      try {
         dbl.executeSql(sqlStr);
      }
      catch (Exception ex) {
         throw new YssException(ex.getMessage());
      }
   }

   /**
    * 将未实现的数据插入数据库
    * @param valueMap HashMap
    * @throws YssException
    */
   private void insertToTempRecpayDetail(HashMap valueMap) throws YssException {
      if (null == valueMap || valueMap.isEmpty()) {
         return;
      }
      RecpayDetailBean RecpayDetail = null;
      Object object = null;
      PreparedStatement prst = null;
      String sqlStr = "insert into " +
            //pub.yssGetTableName("tb_temp_RecpayDetail") +
            // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao
            //dbl.yssTableExist(pub.yssGetTableName("tb_rep_RecpayDetail"))+ 
      		//edit by qiuxufeng 20101117 
            pub.yssGetTableName("tb_rep_RecpayDetail")+ 
            "(fdesc,fbroker,fproject,fJie,fDai,fresult,FPortCode,frpDate,fgussDate,forder,fjd)" +
            " values(?,?,?,?,?,?,?,?,?,?,?)";
      try {

         prst = dbl.openPreparedStatement(sqlStr);
         Iterator it = valueMap.keySet().iterator();
         while (it.hasNext()) {
            RecpayDetail = (com.yss.main.operdeal.report.reptab.TabRecpayDetail.
                            RecpayDetailBean) valueMap.get( (String) it.next());

            prst.setString(1, RecpayDetail.fdesc);
            prst.setString(2, RecpayDetail.fbroker);
            prst.setString(3, RecpayDetail.fprojectname);
            prst.setDouble(4, RecpayDetail.fJie);
            prst.setDouble(5, RecpayDetail.fDai);
            prst.setDouble(6, RecpayDetail.fresult);
            prst.setString(7, RecpayDetail.fportCode);
            prst.setDate(8, RecpayDetail.frpDate);
            prst.setDate(9, RecpayDetail.fgussDate);
            prst.setString(10, RecpayDetail.forder.toString());
            prst.setDouble(11, RecpayDetail.fjd);
            prst.executeUpdate();
         }

      }
      catch (YssException ex) {
         throw new YssException("insert error", ex);
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
			  //add by rujiangpeng 20100603打开多张报表系统需重新登录
			  finally{
				dbl.closeStatementFinal(prst);
   }
		   }

   /**
    * 获取未实现的数据
    * @param valueMap HashMap
    * @throws YssException
    * @throws SQLException
    */
   private void getRecpay(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }
      Connection conn = dbl.loadConnection();
      boolean bTrans = false;
      try {
         conn.setAutoCommit(false);
      }
      catch (SQLException ex) {
      }
      bTrans = true;
      deleteFromRecpayDetail(); //先删除已有的数据。

      getRecpayDetail(valueMap);

      insertToTempRecpayDetail(valueMap);

      try {
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (SQLException ex1) {
         throw new YssException(ex1.getMessage());
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }

   }

   /**
    * 合并邵宏伟修改报表，xuqiji 20100608
    * 获取应收应付明细信息。
    * @throws YssException
    */

   private void getRecpayDetail(HashMap valueMap) throws YssException {
      String sqlStr = "";
      if (YssFun.formatDate(this.dEndDate, "MM").toString().equals("01")) {
         ///一月份
         sqlStr = "select " + dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) + dbl.sqlJN() + "'#'" + dbl.sqlJN() + "FPortCode" + dbl.sqlJN() + "'#'" + dbl.sqlJN() + "rownum as forder, stoDate, '' as brokerName, flag as flag, fivpaycatname, subtypeName, JportMoney, DportMoney, occur, round(sum(occur) over(order by rownum), 2) as bal, Fportcode from ( "
               //期初数
               + " select " + dbl.sqlDate(this.dBeginDate) + " as stoDate, '' as fivpaycatname, 'Balance b/f (承上結餘)' as subtypeName, 0 as JportMoney, 0 as DportMoney, 'qichu' as flag, round(sum(FPortCuryBal), 2) as occur, Fportcode from (select 0 as FPortCuryBal, " + dbl.sqlString(this.portCode) + " as Fportcode from dual "
               + " union all "
               + " select sum(case when ftsftypecode = '06' then round(nvl(FBal * FExRate, 0), 2) when ftsftypecode = '07' then -1*round(nvl(FBal * FExRate, 0), 2) else round(nvl(FBal * FExRate, 0), 2) end) as FPortCuryBal, Fportcode from " + pub.yssGetTableName("Tb_Stock_Investpayrec") + " rr "
               + " left join (select rate1.FCuryCode, FBaseRate / FPortRate as FExRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate, FCuryCode from " + pub.yssGetTableName("tb_data_valrate") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FValDate <= " + dbl.sqlDate(this.dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(this.portCode) + ") rate on rr.fcurycode = rate.fcurycode"
               + " where rr.fyearmonth = to_char(" + dbl.sqlDate(this.dEndDate) + ", 'yyyy') || '00' and rr.fivpaycatcode <> 'IV001' and ftsftypecode in ('06','07') and fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portCode) + " group by fportcode "
               + " union all "
               + " select round(sum(fportcurybal), 2) as FPortCuryBal,fportcode from (select sum(case when d.ftsftypecode='06' then round(nvl(FBal * FExRate, 0), 2) else -1*round(nvl(FBal * FExRate, 0), 2) end) as fportcurybal, d.fportcode,d.fcurycode from " + pub.yssGetTableName("tb_stock_cashpayrec") + " d "
               + " left join (select rate1.FCuryCode, FBaseRate / FPortRate as FExRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate, FCuryCode from " + pub.yssGetTableName("tb_data_valrate") + " where FPortCode = " + dbl.sqlString(this.portCode) + " and FValDate <= " + dbl.sqlDate(this.dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(this.portCode) + ") rate on d.fcurycode = rate.fcurycode"
               + " where d.fportcode=" + dbl.sqlString(this.portCode) + " and d.fcheckstate=1 and d.fyearmonth= to_char(" + dbl.sqlDate(this.dBeginDate) +", 'yyyy')||'00'  and d.ftsftypecode in ('06','07') and d.fsubtsftypecode not in ('06DE','06FI','06TD','06DV','07TD') group by d.fportcode, d.fcurycode) group by fportcode) group by Fportcode "
               + " union all "
               + " select ftransdate as stoDate, fivpaycatname as fivpaycatname, fdesc as subtypeName, round(JportMoney, 2) as JportMoney, round(DportMoney, 2) as DportMoney, 'fasheng' as flag, round(JportMoney, 2) - round(DportMoney, 2) as occur, Fportcode from ( "
               /*借方款项*/
               + " select a.Fportcode, b.fivpaycatname, c.ftsftypename, a.fdesc, sum(a.fmoney * j.fbaserate) as JportMoney, 0 as DportMoney, a.ftransdate,  a.fivpaycatcode from " + pub.yssGetTableName("Tb_Data_InvestPayRec") + " a  left join Tb_Base_InvestPayCat b on a.fivpaycatcode = b.fivpaycatcode left join Tb_Base_TransferType c on a.FTsfTypeCode = c.ftsftypecode left join (select  c.fcurycode, c.fbaserate  from " + pub.yssGetTableName("tb_data_valrate") + " c  where c.fcheckstate = 1"
               + " and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") j on a.fcurycode = j.fcurycode where a.ftransdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + " and a.FTsfTypeCode = '06' and a.fivpaycatcode <> 'IV001' and a.fcheckstate = 1  and a.fportcode = " + dbl.sqlString(this.portCode) + "  group by a.ftransdate, b.fivpaycatname, a.fdesc, c.ftsftypename, a.fivpaycatcode, a.Fportcode "
               + " union all "
               + " select cc.Fportcode, '现金应收' as fivpaycatname,  c.ftsftypename,  cc.fdesc, sum((case when cc.finout=1 then cc.fmoney else -1*cc.fmoney end ) * j.fbaserate) as JportMoney, 0 as DportMoney,  cc.ftransdate, '' as fivpaycatcode   from " + pub.yssGetTableName("tb_data_cashpayrec") + " cc left join Tb_Base_TransferType c on cc.FTsfTypeCode = c.ftsftypecode left join (select  c.fcurycode, c.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " c where c.fcheckstate = 1 "
               + " and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") j on cc.fcurycode =  j.fcurycode where cc.fcheckstate = 1  and cc.ftsftypecode = '06' " + " and cc.fportcode = " + dbl.sqlString(this.portCode) + " and cc.fsubtsftypecode not in ('06DE', '06DV', '06FI', '06TD') and cc.ftransdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + " group by Fportcode, ftsftypename, cc.fdesc, ftransdate "
               + " union all "
               + " select jj.Fportcode, '冲减应付' as fivpaycatname, jj.ftsftypename, jj.fdesc, sum(jj.fmoney * jj.fbaserate) as JportMoney, 0 as DportMoney, jj.ftransferdate, '' as fivpaycatcode from (select t2.fportcode, t2.fmoney, t2.fdesc, t1.ftransferdate, t1.fsubtsftypecode, t1.ftsftypecode, pc.fcurycode, j.fbaserate, c1.ftsftypename from " + pub.yssGetTableName("tb_cash_transfer") + " t1 join " + pub.yssGetTableName("tb_cash_subtransfer") + " t2 on t1.fnum = t2.fnum "
               + " join " + pub.yssGetTableName("tb_para_cashaccount") + " pc on pc.fcashacccode = t2.fcashacccode and pc.fportcode = t2.fportcode left join Tb_Base_TransferType c1 on t1.FTsfTypeCode = c1.ftsftypecode left join (select c.fcurycode, c.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " c where c.fcheckstate = 1   and c.fportcode =" + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + " ) j on pc.fcurycode =  j.fcurycode "
               + " where t1.fcheckstate = 1 and pc.fcheckstate = 1 and t2.fcheckstate = 1 and t2.fportcode = " + dbl.sqlString(this.portCode) + " and (T1.FCPRNum is not null or T1.FIPRNum is not NULL) and not exists ( select * from " + pub.yssGetTableName("tb_data_investpayrec") + " q where q.fnum=t1.fiprnum and q.fivpaycatcode='IV001') and t1.ftransdate   between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + " and t1.fcheckstate = 1 and t1.ftsftypecode = '03' and t2.finout = -1"
               //加入9803的冲减数据
               + " union all select t1.fportcode,-t1.fmoney,t1.fdesc,t1.ftransdate as ftransferdate,t1.ftsftypecode,t1.fsubtsftypecode,t1.fcurycode,t2.fbaserate,' ' as ftsftypename "
               + " from " + pub.yssGetTableName("tb_data_investpayrec") + " t1 left join (select c.fcurycode, c.fbaserate from " + pub.yssGetTableName("Tb_data_valrate") + " c "
               + " where c.fcheckstate = 1 and c.fportcode = " + dbl.sqlString(this.portCode) +  " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") t2 on t1.fcurycode = t2.fcurycode "
               + " where t1.fsubtsftypecode like '9803%' and t1.fivpaycatcode <> 'IV001' and t1.fportcode=" + dbl.sqlString(this.portCode) +  " and t1.ftransdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate)
               + " and t1.fcheckstate = 1 ) jj group by Fportcode, ftsftypename, fdesc, ftransferdate "
               /*贷方款项*/
               + " union all "
               + " select g.Fportcode, g.fivpaycatname, g.ftsftypename, g.fdesc, 0 as JportMoney, sum(g.fmoney * g.fbaserate) as DportMoney, g.ftransdate,  g.fivpaycatcode from (select a.Fportcode, a.fanalysiscode1, a.fanalysiscode3, a.fivpaycatcode, a.ftsftypecode, a.fsubtsftypecode, a.fcurycode, a.fmoney, b.fivpaycatname, c.ftsftypename, a.fdesc, a.ftransdate, j.fbaserate from " + pub.yssGetTableName("Tb_Data_InvestPayRec") + " a left join Tb_Base_InvestPayCat b on a.fivpaycatcode = b.fivpaycatcode left join Tb_Base_TransferType c on a.FTsfTypeCode = c.ftsftypecode "
               + " left join (select  c.fcurycode, c.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " c where c.fcheckstate = 1 and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + " ) j on a.fcurycode = j.fcurycode where a.FTsfTypeCode = '07' and a.fivpaycatcode<>'IV001' and a.ftransdate between " + dbl.sqlDate(this.dBeginDate) +" and " + dbl.sqlDate(this.dEndDate) + " and a.fcheckstate = 1 and b.fcheckstate = 1 and c.fcheckstate = 1" + " and a.fportcode = " + dbl.sqlString(this.portCode) + " ) g "
               + " group by g.Fportcode, g.fivpaycatname, g.ftsftypename, g.fdesc, g.fivpaycatcode, g.ftransdate "
               + " union all "
               + " select cc.Fportcode, '现金应付' as fivpaycatname,c.ftsftypename, cc.fdesc,  0 as JportMoney, sum((case when cc.finout=1 then cc.fmoney else -1*cc.fmoney end) * j.fbaserate) as DportMoney,    cc.ftransdate,  '' as fivpaycatcode from " + pub.yssGetTableName("tb_data_cashpayrec") + " cc left join Tb_Base_TransferType c on cc.FTsfTypeCode =  c.ftsftypecode left join (select  c.fcurycode, c.fbaserate from " + pub.yssGetTableName("Tb_data_valrate") + " c where c.fcheckstate = 1 and c.fportcode =" + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + " ) j on cc.fcurycode =   j.fcurycode "
               + " where cc.fcheckstate = 1 and c.fcheckstate = 1 and cc.ftsftypecode = '07' and cc.fportcode = " + dbl.sqlString(this.portCode) + " and cc.fsubtsftypecode not in ('02DV', '02DE', '02FI', '02TD', '07DE', '07DV', '07FI', '07TD') and cc.ftransdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + " group by Fportcode, ftsftypename, cc.fdesc, ftransdate "
               /*冲减应收*/
               + " union all "
               + " select jj.Fportcode, '冲减应收' as fivpaycatname, jj.ftsftypename, jj.fdesc,0 as JportMoney, sum(jj.fmoney * jj.fbaserate) as DportMoney, jj.ftransferdate, '' as fivpaycatcode from (select t2.fportcode, t2.fmoney, t2.fdesc, t1.ftransferdate, t1.fsubtsftypecode, t1.ftsftypecode, pc.fcurycode, j.fbaserate, c1.ftsftypename from " + pub.yssGetTableName("tb_cash_transfer") + " t1 join " + pub.yssGetTableName("tb_cash_subtransfer") + " t2 on t1.fnum = t2.fnum left join " + pub.yssGetTableName("tb_para_cashaccount") + " pc on pc.fcashacccode = t2.fcashacccode and pc.fportcode = t2.fportcode "
               + " left join Tb_Base_TransferType c1 on t1.FTsfTypeCode = c1.ftsftypecode left join (select  c.fcurycode, c.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " c where c.fcheckstate = 1 and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + " ) j on pc.fcurycode = j.fcurycode where t1.fcheckstate = 1 and pc.fcheckstate = 1 and t2.fcheckstate = 1 and c1.fcheckstate = 1 and (T1.FCPRNum is not null or T1.FIPRNum is not NULL ) and not exists (select * from " + pub.yssGetTableName("tb_data_investpayrec") + " ip "
               + " where ip.fivpaycatcode='IV001' and ip.fnum=t1.fiprnum) and t1.ftransdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) +  " and t1.ftsftypecode = '02' and t2.finout = 1 and t2.fportcode = " + dbl.sqlString(this.portCode) + ") jj " + " group by Fportcode, ftsftypename, fdesc, ftransferdate) order by stoDate, Fportcode) x ";
      }
      else {
         sqlStr = "select " + dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) + dbl.sqlJN() + "'#'" + dbl.sqlJN() + "FPortCode" + dbl.sqlJN() + "'#'" + dbl.sqlJN() + "rownum" + " as forder, stoDate, '' as brokerName, flag as flag, fivpaycatname, subtypeName, JportMoney, DportMoney, occur, round(sum(occur) over(order by rownum), 2) as bal, Fportcode from ( "
               //期初数
               + " select " + dbl.sqlDate(this.dBeginDate) + " as stoDate, '' as fivpaycatname, 'Balance b/f (承上結餘)' as subtypeName, 0 as JportMoney, 0 as DportMoney, 'qichu' as flag, round(sum(FPortCuryBal), 2) as occur, Fportcode from (select 0 as FPortCuryBal, " + dbl.sqlString(this.portCode) + " as Fportcode from dual "
               + " union all "
               // 获取期初数
               + " select sum(case when ftsftypecode = '07' then -1*round(nvl(FBal * fbaserate, 0), 2) else round(nvl(FBal * fbaserate, 0), 2) end) as  FPortCuryBal, Fportcode from " + pub.yssGetTableName("Tb_Stock_Investpayrec") + " rr left join (select n.fcurycode,fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " n where n.fvaldate = " + dbl.sqlDate(this.dEndDate) + " and n.fportcode=" + dbl.sqlString(this.portCode) + " and n.fcheckstate=1) r on r.fcurycode=rr.fcurycode  "
               + "  where rr.fstoragedate = " + dbl.sqlDate(this.dBeginDate) + "-1 and rr.fivpaycatcode <> 'IV001' and fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portCode) + " group by fportcode "
               + " union all "
               + " select round(sum(fportcurybal),2) as FPortCuryBal, fportcode from(select sum(case when d.ftsftypecode='06' then  d.fbal*m.fbaserate  else -1*d.fbal*m.fbaserate end) as fportcurybal, d.fportcode,d.fcurycode from " + pub.yssGetTableName("tb_stock_cashpayrec") + " d left join ( select  ns.fcurycode,ns.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " ns where ns.fcheckstate = 1 and ns.fportcode = " + dbl.sqlString(this.portCode) + " and ns.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") m on d.fcurycode = m.fcurycode "
               + " where d.fportcode = " + dbl.sqlString(this.portCode) + " and d.fcheckstate = 1 and d.fstoragedate = " + dbl.sqlDate(this.dBeginDate) + " -1 and d.fyearmonth <> to_char( " + dbl.sqlDate(this.dBeginDate) + ",'yyyy')||'00' and d.ftsftypecode in ('06','07') and d.fsubtsftypecode not in ('02DV', '02DE', '02FI', '02TD','06DE','06FI','06TD','06DV','07TD','07DE','07FI','07DV') group by d.fportcode, d.fcurycode) group by fportcode )  group by Fportcode "
               + " union all "
               + " select ftransdate as stoDate,  fivpaycatname as fivpaycatname, fdesc as subtypeName, round(JportMoney, 2) as JportMoney, round(DportMoney, 2) as DportMoney, 'fasheng' as flag, round(JportMoney, 2) - round(DportMoney, 2) as occur, Fportcode from ( "
               /*借方款项*/
               + " select a.Fportcode, b.fivpaycatname, c.ftsftypename, a.fdesc, sum(a.fmoney * j.fbaserate) as JportMoney, 0 as DportMoney, a.ftransdate,  a.fivpaycatcode from " + pub.yssGetTableName("Tb_Data_InvestPayRec") + " a  left join Tb_Base_InvestPayCat b on a.fivpaycatcode = b.fivpaycatcode left join Tb_Base_TransferType c on a.FTsfTypeCode = c.ftsftypecode left join (select  c.fcurycode, c.fbaserate  from " + pub.yssGetTableName("tb_data_valrate") + " c  where c.fcheckstate = 1 "
               + " and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") j on a.fcurycode = j.fcurycode where  a.ftransdate between " + dbl.sqlDate(this.dBeginDate) +  " and " + dbl.sqlDate(this.dEndDate) + " and a.FTsfTypeCode = '06' and a.fcheckstate = 1 and a.fivpaycatcode<>'IV001'  and a.fportcode = " + dbl.sqlString(this.portCode) + " group by a.ftransdate, b.fivpaycatname, a.fdesc, c.ftsftypename, a.fivpaycatcode, a.Fportcode "
               + " union all "
               + " select cc.Fportcode, '现金应收' as fivpaycatname,  c.ftsftypename,  cc.fdesc," +"   sum(cc.fmoney * j.fbaserate) as JportMoney,    0 as DportMoney,  cc.ftransdate, '' as fivpaycatcode from " + pub.yssGetTableName("tb_data_cashpayrec") + " cc left join Tb_Base_TransferType c on cc.FTsfTypeCode = c.ftsftypecode left join (select  c.fcurycode, c.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " c where c.fcheckstate = 1 "
               + " and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") j on cc.fcurycode =  j.fcurycode where cc.fcheckstate = 1 and cc.ftsftypecode = '06' and cc.fportcode = " + dbl.sqlString(this.portCode) + " and cc.fsubtsftypecode not in ( '06DE', '06DV', '06FI', '06TD') and cc.ftransdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + " group by Fportcode, ftsftypename, cc.fdesc, ftransdate "
               + " union all "
               + " select jj.Fportcode, '冲减应付' as fivpaycatname, jj.ftsftypename, jj.fdesc, sum(jj.fmoney * jj.fbaserate) as JportMoney, 0 as DportMoney, jj.ftransferdate, '' as fivpaycatcode from (select t2.fportcode, t2.fmoney,  t2.fdesc, t1.ftransferdate, t1.fsubtsftypecode, t1.ftsftypecode, pc.fcurycode, j.fbaserate, c1.ftsftypename from " + pub.yssGetTableName("tb_cash_transfer") + " t1 join " + pub.yssGetTableName("tb_cash_subtransfer") + " t2 on t1.fnum = t2.fnum join " + pub.yssGetTableName("tb_para_cashaccount") + " pc on pc.fcashacccode = "
               //+ " t2.fcashacccode and pc.fportcode = t2.fportcode left join Tb_Base_TransferType c1 on t1.FTsfTypeCode = c1.ftsftypecode left join (select  c.fcurycode, c.fbaserate from Tb_001_data_valrate c where c.fcheckstate = 1 and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") j on pc.fcurycode =  j.fcurycode where t1.fcheckstate = 1 and pc.fcheckstate = 1 and t2.fcheckstate = 1  and (T1.FCPRNum is not null or T1.FIPRNum is not NULL) "
               //edit by qiuxufeng 20101119 与原版本数据核对不一致，核对语句并修改
               + " t2.fcashacccode and pc.fportcode = t2.fportcode left join " + pub.yssGetTableName("tb_data_investpayrec") + " t3 on t1.fiprnum = t3.fnum left join Tb_Base_TransferType c1 on t1.FTsfTypeCode = c1.ftsftypecode left join (select  c.fcurycode, c.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " c where c.fcheckstate = 1 and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") j on pc.fcurycode =  j.fcurycode where t1.fcheckstate = 1 and pc.fcheckstate = 1 and t2.fcheckstate = 1  and (T1.FCPRNum is not null or T1.FIPRNum is not NULL) "
               + " and t1.ftransdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + " and t1.ftsftypecode = '03' and t2.finout = -1 "
               //加入9803的冲减数据
               //+ " union all select t1.fportcode,-t1.fmoney,t1.fdesc,t1.ftransdate as ftransferdate,t1.ftsftypecode,t1.fsubtsftypecode,t1.fcurycode,t2.fbaserate,' ' as ftsftypename "
               //edit by qiuxufeng 20101119 与原版本数据核对不一致，核对语句并修改
               + " and t3.fivpaycatcode <> 'IV001' and t3.fcheckstate = 1 union all select t1.fportcode,-t1.fmoney,t1.fdesc,t1.ftransdate as ftransferdate,t1.ftsftypecode,t1.fsubtsftypecode,t1.fcurycode,t2.fbaserate,' ' as ftsftypename "
               + " from " + pub.yssGetTableName("tb_data_investpayrec") + " t1 left join (select c.fcurycode, c.fbaserate from " + pub.yssGetTableName("Tb_data_valrate") + " c "
               + " where c.fcheckstate = 1 and c.fportcode = " + dbl.sqlString(this.portCode) +  " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") t2 on t1.fcurycode = t2.fcurycode "
               + " where t1.fsubtsftypecode like '9803%' and t1.fivpaycatcode <> 'IV001' and t1.fportcode=" + dbl.sqlString(this.portCode) +  " and t1.ftransdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate)
               + " and t1.fcheckstate = 1 ) jj group by Fportcode, ftsftypename, fdesc, ftransferdate "
               /*贷方款项*/
               + " union all "
               + " select g.Fportcode, g.fivpaycatname, g.ftsftypename, g.fdesc, 0 as JportMoney, sum(g.fmoney * g.fbaserate) as DportMoney, g.ftransdate, g.fivpaycatcode from (select a.Fportcode, a.fanalysiscode1, a.fanalysiscode3, a.fivpaycatcode, a.ftsftypecode, a.fsubtsftypecode, a.fcurycode, a.fmoney, b.fivpaycatname, c.ftsftypename, a.fdesc, a.ftransdate, j.fbaserate from " + pub.yssGetTableName("Tb_Data_InvestPayRec") + " a left join Tb_Base_InvestPayCat b on a.fivpaycatcode = b.fivpaycatcode "
               + " left join Tb_Base_TransferType c on a.FTsfTypeCode = c.ftsftypecode left join (select  c.fcurycode, c.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " c where c.fcheckstate = 1 and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") j on a.fcurycode = j.fcurycode where a.FTsfTypeCode = '07' and a.ftransdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) + " and a.fcheckstate = 1 and a.fivpaycatcode<>'IV001' and b.fcheckstate = 1  and c.fcheckstate = 1"
               + " and a.fportcode = " + dbl.sqlString(this.portCode) + ") g  group by g.Fportcode, g.fivpaycatname, g.ftsftypename, g.fdesc, g.fivpaycatcode, g.ftransdate "
               ////现金应付
               + " union all "
               + " select cc.Fportcode, '现金应付' as fivpaycatname,c.ftsftypename, cc.fdesc, 0 as JportMoney, sum(cc.fmoney * j.fbaserate) as DportMoney, cc.ftransdate, '' as fivpaycatcode from " + pub.yssGetTableName("tb_data_cashpayrec") + " cc left join Tb_Base_TransferType c on cc.FTsfTypeCode = c.ftsftypecode left join (select c.fcurycode, c.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " c where c.fcheckstate = 1 "
               + " and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") j on cc.fcurycode = j.fcurycode where cc.fcheckstate = 1 and c.fcheckstate = 1 and cc.ftsftypecode = '07' and cc.fportcode = " + dbl.sqlString(this.portCode) + " and cc.fsubtsftypecode not in ( '07DE',  '07FI','07DV', '07TD') and cc.ftransdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate) +" group by Fportcode, ftsftypename, cc.fdesc, ftransdate "
               ////////////冲减应收
               + " union all "
               + " select jj.Fportcode, '冲减应收' as fivpaycatname, jj.ftsftypename, jj.fdesc, 0 as JportMoney, sum(jj.fmoney * jj.fbaserate) as DportMoney, jj.ftransferdate, '' as fivpaycatcode from (select t2.fportcode, t2.fmoney, t2.fdesc, t1.ftransferdate, t1.fsubtsftypecode, t1.ftsftypecode, pc.fcurycode, j.fbaserate, c1.ftsftypename  from " + pub.yssGetTableName("tb_cash_transfer") + " t1 join " + pub.yssGetTableName("tb_cash_subtransfer") + " t2 on t1.fnum = t2.fnum left join " + pub.yssGetTableName("tb_para_cashaccount") + " pc on pc.fcashacccode = t2.fcashacccode  and pc.fportcode = t2.fportcode "
               + " left join Tb_Base_TransferType c1 on t1.FTsfTypeCode = c1.ftsftypecode left join (select  c.fcurycode, c.fbaserate from " + pub.yssGetTableName("tb_data_valrate") + " c where c.fcheckstate = 1  and c.fportcode = " + dbl.sqlString(this.portCode) + " and c.fvaldate = " + dbl.sqlDate(this.dEndDate) + ") j on pc.fcurycode = j.fcurycode where t1.fcheckstate = 1 and pc.fcheckstate = 1 and t2.fcheckstate = 1 and c1.fcheckstate = 1 and (T1.FCPRNum is not null or T1.FIPRNum is not NULL) and t1.Ftransferdate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate)
               + " and t1.ftsftypecode = '02' and t2.finout = 1 and t2.fportcode = " + dbl.sqlString(this.portCode) + ") jj group by Fportcode, ftsftypename, fdesc, ftransferdate   ) "
               + " where Fportcode =  " + dbl.sqlString(this.portCode) + " order by stoDate, Fportcode) x ";
      }
      
      ResultSet rs = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         setResultValue(valueMap, rs);

      }
      catch (YssException ex) {
         throw new YssException("获取应收应付明细数据出错！", ex);
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   /**
    * 将未实现的数据封装放入HashMap中。
    * @param valueMap HashMap
    * @param rs ResultSet
    * @throws YssException
    */
   private void setResultValue(HashMap valueMap, ResultSet rs) throws
         YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }
      if (null == rs) {
         return;
      }
      RecpayDetailBean RecpayDetail = null;
      try {
         while (rs.next()) {
            RecpayDetail = new RecpayDetailBean();
            RecpayDetail.fbroker = "";
            RecpayDetail.fdesc = rs.getString("subtypename");
            RecpayDetail.fprojectname = rs.getString("fivpaycatname");
            RecpayDetail.fJie = rs.getDouble("jPortMoney");
            RecpayDetail.fDai = rs.getDouble("dPortMoney");
            RecpayDetail.fjd = rs.getDouble("occur");
            RecpayDetail.fresult = rs.getDouble("bal");
            RecpayDetail.frpDate = rs.getDate("stodate");
            RecpayDetail.fgussDate = YssFun.toSqlDate(dEndDate);
            RecpayDetail.fportCode = rs.getString("fportCode");
            RecpayDetail.forder = rs.getString("forder");
            valueMap.put(RecpayDetail.forder, RecpayDetail);

         }
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }
}

