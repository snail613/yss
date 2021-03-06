
package com.yss.main.operdeal.report.reptab;

import java.sql.*;
import java.util.Date;
import java.util.*;

import com.yss.base.*;
import com.yss.main.dayfinish.OffAcctBean;
import com.yss.util.*;

/**
 *
 * <p>Title:中保 资金注入/注出表 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: YSSTECH</p>
 * @author 陈嘉
 * @version 1.0
 */
public class TabFundInOut
    extends BaseAPOperValue {
    private String startDate; //查询开始时间
    private String endDate; //查询结束时间
    private String portCode; //组合代码
    
    // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao
    private boolean isCreate; //是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询
    // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出  end -----------
    
    private double fAbalance = 0; //结余的金额
    private double dQcAbalanceEQ = 0; //股票期初余额
    private double dQcAbalanceFI = 0; //债券期初余额
    private double dQcAbalanceWT = 0; //权证期初余额
    private double dQcAbalanceDR = 0; //衍生工具期初余额
    private double dQcAbalanceDE = 0; //存款期初余额
    private double dQcAbalanceTR = 0; //基金期初余额
    private double dQcAbalanceQT = 0; //其它期初余额
    private double ybalance = 0; //上期结余汇总
    boolean tf=true;
    private static final int FundInOut_Accumulate = 11; //上月分类合计
    private static final int FundInOut_yTotal = 12; //上月合计
    private static final int FundInOut_TodayIntout = 13; //当月流入流出
    private static final int FundInOut_tTotal = 14; //结余



    public TabFundInOut() {
    }

    /**
     * 初始化页面传过来的数据，分别得到查询的起始时间和结束时间，组合的代码
     * @param bean Object
     * @throws YssException
     */
    public void init(Object bean) throws YssException {
        String reqAry[] = null;
        String reqAry1[] = null;
        String sRowStr = (String) bean;
        if (sRowStr.trim().length() == 0) {
            return;
        }
        reqAry = sRowStr.split("\n");
//        reqAry1 = reqAry[0].split("\r");
//        this.startDate = reqAry1[1];
//        reqAry1 = reqAry[1].split("\r");
//        this.endDate = reqAry1[1];
//        reqAry1 = reqAry[2].split("\r");
//        this.portCode = reqAry1[1];
//        
//         // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao
//        reqAry1 = reqAry[3].split("\r");
//        this.isCreate = reqAry1[1].equalsIgnoreCase("0")?false:true;
//        // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出  end -------------
        
      //==================修改解析控件的值，前台控件值为空时不传值导致解析出错  edit by qiuxufeng 20101109 
		for (int i = 0; i < reqAry.length; i++) {
			reqAry1 = reqAry[i].split("\r");
			if(reqAry1[0].equalsIgnoreCase("1")) {
				this.startDate = reqAry1[1];
			} else if(reqAry1[0].equalsIgnoreCase("2")) {
				this.endDate = reqAry1[1];
			} else if(reqAry1[0].equalsIgnoreCase("3")) {
				this.portCode = reqAry1[1];
			} else if(reqAry1[0].equalsIgnoreCase("4")) {
				this.isCreate = reqAry1[1].equalsIgnoreCase("0")?false:true;
			}
		}
		//=========end=========
    }

    /**
     * 内部类，用于临时表的POJO，存储资金注入注出表的信息
     * <p>Title: </p>
     *
     * <p>Description: </p>
     *
     * <p>Copyright: Copyright (c) 2006</p>
     *
     * <p>Company: </p>
     *
     * @author not attributable
     * @version 1.0
     */
    private class FundInOutBean {
        Date transDate;
        String ccy;
        String desc;
        double inFlow;
        double inFlowHKD;
        double outFlow;
        double outFlowHKD;
        double exRate;
        double portAccbalance;
        String Portcode;
        String forder;
        public FundInOutBean() {
        }
    }

    /**
     * 反射调用的方法，处理临时表的原始数据查询，生成显示报表要用的临时表tb_001_Data_FundInOut
     * modify by wangzuochun 2009.09.09 MS00674 003组合4月报表上期结余合计与3月份的月末对不上 QDV4中保2009年09月03日01_B
     * @return Object
     * @throws YssException
     */
    public Object invokeOperMothed() throws YssException {
        //用于存放资金注入注出表的对象
        LinkedHashMap valueMap = null;
        LinkedHashMap finalValueMap = null;
       // createTempUnrealised();
        valueMap = new LinkedHashMap();
        finalValueMap = new LinkedHashMap();
        String strSql = "";
        ResultSet rs = null;
     	tf=true;
        try {
        	
          createTempUnrealised();
           if (isCreate){
      		 //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
  			 OffAcctBean offAcct = new OffAcctBean();
				offAcct.setYssPub(this.pub);
  			 String tmpDate = startDate + "~n~" + endDate;
  			 String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
  			 if(!tmpInfo.trim().equalsIgnoreCase("")) {
  				 return "<OFFACCT>" + tmpInfo;
  			 }
  			 //=================end=================
        	   deleteFromTempUnrealised(); // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao
        	 //SQL查询语句：用于上月月末余额数据
               strSql = "select * from Tb_Data_FundInOut" +
                     " where FDesc like 'Final%' and FTransDate = " +
                     dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.startDate), -1)) +
                     " and FPortCode = " + dbl.sqlString(this.portCode);
               rs = dbl.openResultSet(strSql);

               //如果有上月月末余额数据则直接获取
               if (rs.next()) {
                  dbl.closeResultSetFinal(rs);
                  //取上个月月末余额作为本月月初余额
                  getStartBalance(YssFun.toDate(this.startDate),
                                  YssFun.toDate(this.endDate), this.portCode,
                                  valueMap);
               }
               //如果无上月月末余额数据则重新计算
               else {
                  //获得年getQcAbalanceJan度的初始余额，即期初额
                  getQcAbalanceJan(YssFun.toDate(this.startDate),
                                   YssFun.toDate(this.endDate), this.portCode,
                                   valueMap);

                  //到本月的余额，包括期初余额和上月的发生额
                  getQcAbalance(YssFun.toDate(this.startDate),
                                YssFun.toDate(this.endDate), this.portCode,
                                valueMap);
               }

                //获得资金注入注出的明细数据
                getDetail(YssFun.toDate(this.startDate), YssFun.toDate(this.endDate),
                          this.portCode, valueMap);

                //将map中的数据插入到临时表中
                insertToTempFundInOut(valueMap);

                //获得本月月末余额
                getFinalBalance(YssFun.toDate(this.startDate),
                              YssFun.toDate(this.endDate), this.portCode, finalValueMap);

                //将finalValueMap中的本月月末余额数据插入到临时表中
                insertToTempFundInOut(finalValueMap);
           }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        finally {
          dbl.closeResultSetFinal(rs);
       }

        return "";
    }

    /**
     * 创建用于存放资金的注入注出数据的表。
     * modify by wangzuochun 2009.09.09 MS00674 003组合4月报表上期结余合计与3月份的月末对不上 QDV4中保2009年09月03日01_B
     * @throws YssException
     */
    private void createTempUnrealised() throws YssException {
        String strSql = "";
        try {
            //如果表存在，则按期初日期、期末日期和组合删除表中的数据
            if (dbl.yssTableExist(pub.yssGetTableName("Tb_rep_FundInOut"))) {
              return;  // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao
            }
            //创建临时表
            else {
                strSql = "create table " +pub.yssGetTableName("Tb_rep_FundInOut")+
                    " (FTransDate DATE," +
                    " FCCY varchar2(20)," +
                    " FDesc varchar2(200)," +
                    " FInflow number(18,2)," +
                    " FInflowHKD number(18,2)," +
                    " FOutflow number(18,2)," +
                    " FoutflowHKD number(18,2)," +
                    " FExRate number(18,5)," +
                    " FPortAccbalance number(18,2)," +
                    " Fportcode varchar2(20)," +
                    " forder varchar2(100)"+
                    ")";
            }
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("生成临时资金注入注出表出错!");
        }
    }

    /**
     * add by wangzuochun 2009.09.09 MS00674 003组合4月报表上期结余合计与3月份的月末对不上 QDV4中保2009年09月03日01_B
     * 取上个月月末余额作为本月月初余额
     * @param dStartDate Date
     * @param dEndDate Date
     * @param port String
     * @param valueMap LinkedHashMap
     * @throws YssException
     */
    public void getStartBalance(java.util.Date dStartDate, java.util.Date dEndDate,
                              String port, LinkedHashMap valueMap) throws
          YssException {
    	   ResultSet rs = null;
           String strSql = "";
           String strDecs = "";
           double checkKey = 0;
           try{
              strSql = "select * from Tb_Data_FundInOut" +
                    " where FDesc like 'Final%' and FTransDate = " +
                    dbl.sqlDate(YssFun.addDay(dStartDate, -1)) + " and FPortCode = " + dbl.sqlString(port);

              FundInOutBean fundInOut = null;

              rs = dbl.openResultSet(strSql);
              while (rs.next()) {
                 fundInOut = new FundInOutBean();
                 strDecs = rs.getString("FDesc");
                 if (strDecs.equalsIgnoreCase("Final - EQ")) {
                    dQcAbalanceEQ = rs.getDouble("FPortAccBalance"); //股票
                    fundInOut.desc = "Balance b/f (上期結餘) - 股票";
                    fundInOut.portAccbalance = dQcAbalanceEQ;
                    checkKey = dQcAbalanceEQ;
                 }
                 else if (strDecs.equalsIgnoreCase("Final - FI")) {
                    dQcAbalanceFI = rs.getDouble("FPortAccBalance"); //债券
                    fundInOut.desc = "Balance b/f (上期結餘) - 债券";
                    fundInOut.portAccbalance = dQcAbalanceFI;
                    checkKey = dQcAbalanceFI;
                 }
                 else if (strDecs.equalsIgnoreCase("Final - WT")) {
                    dQcAbalanceWT = rs.getDouble("FPortAccBalance"); //渦輪
                    fundInOut.desc = "Balance b/f (上期結餘) - 渦輪";
                    fundInOut.portAccbalance = dQcAbalanceWT;
                    checkKey = dQcAbalanceWT;
                 }
                 else if (strDecs.equalsIgnoreCase("Final - DR")) {
                    dQcAbalanceDR = rs.getDouble("FPortAccBalance"); //衍生工具
                    fundInOut.desc = "Balance b/f (上期結餘) - 衍生工具";
                    fundInOut.portAccbalance = dQcAbalanceDR;
                    checkKey = dQcAbalanceDR;
                 }
                 else if (strDecs.equalsIgnoreCase("Final - DE") || strDecs.equalsIgnoreCase("Final - MK")) {
                    dQcAbalanceDE = rs.getDouble("FPortAccBalance"); //存款
                    fundInOut.desc = "Balance b/f (上期結餘) - 存款";
                    fundInOut.portAccbalance = dQcAbalanceDE;
                    checkKey = dQcAbalanceDE;
                 }
                 else if (strDecs.equalsIgnoreCase("Final - TR")) {
                    dQcAbalanceTR = rs.getDouble("FPortAccBalance"); //基金
                    fundInOut.desc = "Balance b/f (上期結餘) - 基金";
                    fundInOut.portAccbalance = dQcAbalanceTR;
                    checkKey = dQcAbalanceTR;
                 }else {
                    dQcAbalanceQT = rs.getDouble("FPortAccBalance"); //其他
                    fundInOut.desc = "Balance b/f (上期結餘) - 其它";
                    fundInOut.portAccbalance = dQcAbalanceQT;
                    checkKey = dQcAbalanceQT;
                 }
                 fundInOut.transDate = dStartDate;
                 fundInOut.ccy = "HKD";
                 fundInOut.forder=YssFun.formatDate(startDate, "yyyyMMdd")+FundInOut_Accumulate;
                 fundInOut.Portcode = rs.getString("FPortcode");
                 valueMap.put(fundInOut.transDate.toString() + Double.toString(checkKey) + fundInOut.desc + fundInOut.ccy, fundInOut);
               //---苏程辉2009年10月16日
                 fAbalance += YssFun.roundIt( rs.getDouble("FPortAccBalance"), 2);
              }
              rs.getStatement().close();
              rs = null;
                 ybalance=fAbalance;

       }
       catch (Exception e) {
          throw new YssException("获取本月月初余额报错");
       }
       finally {
          dbl.closeResultSetFinal(rs);
       }

    }

    /**
     * add by wangzuochun 2009.09.09 MS00674 003组合4月报表上期结余合计与3月份的月末对不上 QDV4中保2009年09月03日01_B
     * 计算出本月月末余额
     * @param dStartDate Date
     * @param dEndDate Date
     * @param port String
     * @param valueMap LinkedHashMap
     * @throws YssException
     */

   public void getFinalBalance(java.util.Date dStartDate, java.util.Date dEndDate,
                              String port, LinkedHashMap valueMap) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        String strDecs = "";
        double checkKey = 0;
        try {
           strSql = "select * from Tb_Data_FundInOut" +
                 " where FDesc like 'Balance%' and FTransDate between " +
                 dbl.sqlDate(dStartDate) + " and " + dbl.sqlDate(dEndDate) +
                 " and FPortCode = " + dbl.sqlString(port);

           strSql += " union select * from Tb_Data_Fundinout" +
                " where (fdesc like '流出%' or fdesc like '流入%') and FTransDate between " +
                dbl.sqlDate(dStartDate) + " and " + dbl.sqlDate(dEndDate) +
                 " and FPortCode = " + dbl.sqlString(port);

           rs = dbl.openResultSet(strSql);

           dQcAbalanceEQ = 0;
           dQcAbalanceFI = 0;
           dQcAbalanceWT = 0;
           dQcAbalanceDR = 0;
           dQcAbalanceDE = 0;
           dQcAbalanceTR = 0;
           dQcAbalanceQT = 0;

           FundInOutBean fundInOut = null;
           while(rs.next()){
              strDecs = rs.getString("FDesc");
              if(strDecs.indexOf("股票") != -1){
                 dQcAbalanceEQ += rs.getDouble("FPortAccBalance"); //股票
              } else if(strDecs.equalsIgnoreCase("流入 - EQ")){
                 dQcAbalanceEQ +=  rs.getDouble("FInFlowHKD");
              }else if(strDecs.equalsIgnoreCase("流出 - EQ")){
                 dQcAbalanceEQ += - rs.getDouble("FOutFlowHKD");
              }
              else if(strDecs.indexOf("债券") != -1){
                 dQcAbalanceFI += rs.getDouble("FPortAccBalance"); //债券
              }else if(strDecs.equalsIgnoreCase("流入 - FI")){
                 dQcAbalanceFI +=  rs.getDouble("FInFlowHKD");
              }
              else if(strDecs.equalsIgnoreCase("流出 - FI")){
                 dQcAbalanceFI +=  - rs.getDouble("FOutFlowHKD");
              }
              else if(strDecs.indexOf("渦輪") != -1){
                 dQcAbalanceWT += rs.getDouble("FPortAccBalance"); //渦輪
              }else  if(strDecs.equalsIgnoreCase("流入 - WT")){
                 dQcAbalanceWT += rs.getDouble("FInFlowHKD");
              }
              else if(strDecs.equalsIgnoreCase("流出 - WT")){
                 dQcAbalanceWT +=  - rs.getDouble("FOutFlowHKD");
              }
              else if(strDecs.indexOf("衍生工具") != -1){
                 dQcAbalanceDR += rs.getDouble("FPortAccBalance"); //衍生工具
              }else if(strDecs.equalsIgnoreCase("流入 - DR")){
                 dQcAbalanceDR += rs.getDouble("FInFlowHKD");
              }
              else if(strDecs.equalsIgnoreCase("流出 - DR")){
                 dQcAbalanceDR +=  - rs.getDouble("FOutFlowHKD");
              }
              else if(strDecs.indexOf("存款") != -1){
                 dQcAbalanceDE += rs.getDouble("FPortAccBalance"); //存款
              } else if(strDecs.equalsIgnoreCase("流入 - DE")){
                 dQcAbalanceDE +=  rs.getDouble("FInFlowHKD");
              }
              else if(strDecs.equalsIgnoreCase("流出 - DE")){
                 dQcAbalanceDE =  - rs.getDouble("FOutFlowHKD");
              }
              else if (strDecs.equalsIgnoreCase("流入 - MK")) {
                 dQcAbalanceDE +=  rs.getDouble("FInFlowHKD");
              }
              else if (strDecs.equalsIgnoreCase("流出 - MK")) {
                 dQcAbalanceDE +=  - rs.getDouble("FOutFlowHKD");
              }
              else if(strDecs.indexOf("基金") != -1){
                 dQcAbalanceTR += rs.getDouble("FPortAccBalance"); //基金
              }else if(strDecs.equalsIgnoreCase("流入 - TR")){
                 dQcAbalanceTR +=  rs.getDouble("FInFlowHKD");
              }
              else if(strDecs.equalsIgnoreCase("流出 - TR")){
                 dQcAbalanceTR +=  - rs.getDouble("FOutFlowHKD");
              }
              else if(strDecs.equalsIgnoreCase("流入 - DR")){
                 dQcAbalanceTR += rs.getDouble("FInFlowHKD");
              }else  if(strDecs.equalsIgnoreCase("流出 - DR")){
                 dQcAbalanceTR +=  - rs.getDouble("FOutFlowHKD");
              }
           }
           rs.getStatement().close();
           rs = null;
           if (dQcAbalanceEQ != 0) {
              fundInOut = new FundInOutBean();
              fundInOut.desc = "Final - EQ";
              fundInOut.portAccbalance = dQcAbalanceEQ;
              checkKey = dQcAbalanceEQ;
              fundInOut.transDate = dEndDate;
              fundInOut.ccy = "HKD";
              fundInOut.Portcode = port;
              valueMap.put(fundInOut.transDate.toString() +
                           Double.toString(checkKey) + fundInOut.desc +
                           fundInOut.ccy, fundInOut);
           }

           if (dQcAbalanceFI != 0) {
              fundInOut = new FundInOutBean();
              fundInOut.desc = "Final - FI";
              fundInOut.portAccbalance = dQcAbalanceFI;
              checkKey = dQcAbalanceFI;
              fundInOut.transDate = dEndDate;
              fundInOut.ccy = "HKD";
              fundInOut.Portcode = port;
              valueMap.put(fundInOut.transDate.toString() +
                           Double.toString(checkKey) + fundInOut.desc +
                           fundInOut.ccy, fundInOut);
           }

           if (dQcAbalanceTR != 0) {
              fundInOut = new FundInOutBean();
              fundInOut.desc = "Final - TR";
              fundInOut.portAccbalance = dQcAbalanceTR;
              checkKey = dQcAbalanceTR;
              fundInOut.transDate = dEndDate;
              fundInOut.ccy = "HKD";
              fundInOut.Portcode = port;
              valueMap.put(fundInOut.transDate.toString() +
                           Double.toString(checkKey) + fundInOut.desc +
                           fundInOut.ccy, fundInOut);
           }

           if (dQcAbalanceDE != 0) {
              fundInOut = new FundInOutBean();
              fundInOut.desc = "Final - DE";
              fundInOut.portAccbalance = dQcAbalanceDE;
              checkKey = dQcAbalanceDE;
              fundInOut.transDate = dEndDate;
              fundInOut.ccy = "HKD";
              fundInOut.Portcode = port;
              valueMap.put(fundInOut.transDate.toString() +
                           Double.toString(checkKey) + fundInOut.desc +
                           fundInOut.ccy, fundInOut);
           }

           if (dQcAbalanceDR != 0) {
              fundInOut = new FundInOutBean();
              fundInOut.desc = "Final - DR";
              fundInOut.portAccbalance = dQcAbalanceDR;
              checkKey = dQcAbalanceDR;
              fundInOut.transDate = dEndDate;
              fundInOut.ccy = "HKD";
              fundInOut.Portcode = port;
              valueMap.put(fundInOut.transDate.toString() +
                           Double.toString(checkKey) + fundInOut.desc +
                           fundInOut.ccy, fundInOut);
           }

           if (dQcAbalanceWT != 0) {
              fundInOut = new FundInOutBean();
              fundInOut.desc = "Final - WT";
              fundInOut.portAccbalance = dQcAbalanceWT;
              checkKey = dQcAbalanceWT;
              fundInOut.transDate = dEndDate;
              fundInOut.ccy = "HKD";
              fundInOut.Portcode = port;
              valueMap.put(fundInOut.transDate.toString() +
                           Double.toString(checkKey) + fundInOut.desc +
                           fundInOut.ccy, fundInOut);
           }

        } catch (Exception e) {
            throw new YssException("获取本月月末余额报错");
        } finally {
            dbl.closeResultSetFinal(rs,rs);
        }
    }


    /**
     * 生成临时表,从LinkedHashMap中遍历数据到表中
     * @param valueMap LinkedHashMap
     * @throws YssException
     */
    private void insertToTempFundInOut(LinkedHashMap valueMap) throws YssException {
        if (null == valueMap || valueMap.isEmpty()) {
            return;
        }
        FundInOutBean getASubData = null;
        StringBuffer bufSql = new StringBuffer();
        boolean bTrans = false; //代表是否开始事务
        Connection conn = dbl.loadConnection();
        PreparedStatement prst = null;
        try {
            bufSql.append("INSERT INTO " +pub.yssGetTableName("Tb_rep_FundInOut")+ "(");
            bufSql.append("FTransdate, FCCY, FDesc, FInflow, FInflowHKD, FOutflow, FOutflowHKD, FExRate, FPortAccbalance,Fportcode,forder)" +
                          " values(?,?,?,?,?,?,?,?,?,?,?)");
            prst = dbl.openPreparedStatement(bufSql.toString());
            Iterator it = valueMap.keySet().iterator();
            while (it.hasNext()) {
                getASubData = (com.yss.main.operdeal.report.reptab.TabFundInOut.
                               FundInOutBean) valueMap.get( (String) it.next());
                prst.setDate(1, YssFun.toSqlDate(getASubData.transDate));
                prst.setString(2, getASubData.ccy);
                prst.setString(3, getASubData.desc);

                prst.setDouble(4, getASubData.inFlow);
                prst.setDouble(5, getASubData.inFlowHKD);
                prst.setDouble(6, YssFun.roundIt(getASubData.outFlow, 2));
                prst.setDouble(7, YssFun.roundIt(getASubData.outFlowHKD, 2));
                prst.setDouble(8, YssFun.roundIt(getASubData.exRate, 5)); //汇率 修改成小数点后5位 alter by chenjia

                prst.setDouble(9, getASubData.portAccbalance);
                prst.setString(10, getASubData.Portcode);
                prst.setString(11, getASubData.forder);
                prst.executeUpdate();
            }
            if(tf){
            	 prst.setDate(1, YssFun.toSqlDate(this.startDate));
                 prst.setString(2, "HKD");
                 prst.setString(3, "Balance b/f (上期結餘) - 合计");
                 prst.setDouble(4, 0);
                 prst.setDouble(5, 0);
                 prst.setDouble(6, 0);
                 prst.setDouble(7, 0);
                 prst.setDouble(8, 0);
                 prst.setDouble(9, ybalance);
                 prst.setString(10,this.portCode);
                 prst.setString(11, YssFun.formatDate(this.startDate, "yyyyMMdd").toString() + FundInOut_yTotal);
                 prst.executeUpdate();
            	tf=false;
            }

        } catch (Exception ex) {
            throw new YssException("生成临时表" + pub.yssGetTableName("tb_Data_FundInOut") + "出错", ex);
        } finally {
			dbl.closeStatementFinal(prst);//add by rujiangpeng 20100603打开多张报表系统需重新登录
            dbl.endTransFinal(conn, bTrans);
        }

    }

    
    // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao
    private void deleteFromTempUnrealised() throws YssException {
        String  strSql = "";
        try {
        	strSql = " delete from " +pub.yssGetTableName("Tb_rep_FundInOut")+
                     " where FTransDate between " + dbl.sqlDate(this.startDate) +
                     " and " + dbl.sqlDate(this.endDate) +
                     " and FPortCode = " + dbl.sqlString(this.portCode);
           dbl.executeSql(strSql);
        }
        catch (Exception ex) {
           throw new YssException(ex.getMessage());
        }
     }
    
    /**
     * 得到表的明细数据
     * modify by wangzuochun 2009.09.09 MS00674 003组合4月报表上期结余合计与3月份的月末对不上 QDV4中保2009年09月03日01_B
     * @param dStartDate Date
     * @param dEndDate Date
     * @param port String
     * @param valueMap LinkedHashMap
     * @throws YssException
     */
    public void getDetail(java.util.Date dStartDate, java.util.Date dEndDate,
                          String port, LinkedHashMap valueMap) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        try {

          	//modify by ctq 2009-1-19 调整现金库存统计方式后资金调拨中资金流出的汇率不再是当日的行情汇率，另外原来SQL中判断组合内调拨的条件似乎也是存在问题，故重写了该SQL。

strSql = "select to_char(ftransferdate, 'yyyyMMdd') ||" +  FundInOut_TodayIntout + " as forder,"
           + " cash.ftransferdate,acc.fcurycode as fccy,cash.fportcode, "
           //根据客户要求从汇率行情表获取资金注入注出交易日汇率
           + " round(b.fbaserate / b.fportrate, 5) as fexrate, " //--round(fhkmoney /fmoney,5) as fexrate,"
           + " decode(cash.finout,1,'流入','流出')||' - '||cash.fanalysiscode2 as fdesc,"
           + " decode(cash.finout,1,round(fmoney,2),0) as finflow,"
           + " decode(cash.finout,1,fhkmoney,0) as finflowhkd,"
           + " decode(cash.finout,-1,round(fmoney,2),0) as foutflow,"
           + " decode(cash.finout,-1,fhkmoney,0) as foutflowhkd "
           + " from ("
           + " select b.fportcode,a.ftransferdate,b.fcashacccode,b.finout,b.fanalysiscode2,sum(b.fmoney) as fmoney,sum(round(b.fmoney*b.fbasecuryrate,2)) as fhkmoney "
           + " from " + pub.yssGetTableName("tb_cash_transfer") + " a "
           + " join " + pub.yssGetTableName("tb_cash_subtransfer") + " b on a.fnum=b.fnum"
           + " where a.ftransferdate between " + dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate)
           + " and a.ftsftypecode = '04' and b.Fcheckstate = 1 and b.fportcode = " + dbl.sqlString(port)
           + " group by b.fportcode,a.ftransferdate,b.fcashacccode,b.finout,b.fanalysiscode2) cash "

           + " join " + pub.yssGetTableName("tb_para_cashaccount") + " acc on cash.fcashacccode=acc.fcashacccode and cash.fportcode=acc.fportcode "
           + " and acc.fstartdate = (select max(fstartdate) from " + pub.yssGetTableName("tb_para_cashaccount") + " c "
           + " where c.fcashacccode=acc.fcashacccode and c.fportcode=acc.fportcode and c.fstartdate<=cash.ftransferdate) "
           //由估值日汇率表获得交易日汇率
           + " join " + pub.yssGetTableName("tb_data_valrate") + " b on b.fcurycode=acc.fcurycode and b.fportcode=cash.fportcode"
   		   + " and b.fvaldate=(select max(fvaldate) from " + pub.yssGetTableName("tb_data_valrate") + " r "
   		   + " where r.fcurycode=b.fcurycode and r.fportcode=b.fportcode and r.fvaldate<=cash.ftransferdate)"
   		   //因为明细和汇总时排序不一致，导致006组合3月份结余数据有问题，添加order by ftransferdate
           + " order by ftransferdate ";
            rs = dbl.openResultSet(strSql);
            setResultValue(valueMap, rs, false);
        } catch (Exception e) {
            throw new YssException("获取本期间的注入注出数据错误");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 得到期初余额
     * @param dStartDate Date
     * @param dEndDate Date
     * @param port String
     * @param valueMap LinkedHashMap
     * @throws YssException
     */
    public void getQcAbalanceJan(java.util.Date dStartDate,
                                 java.util.Date dEndDate,
                                 String port, LinkedHashMap valueMap) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql =
                "select to_char("+dbl.sqlDate(this.startDate)+",'yyyyMMdd')||"+FundInOut_Accumulate+" as forder, " +
                " fportcurybal,fanalysiscode2,FDesc,Fportcode "+
                "from (select sum(fportcurybal) as fportcurybal,fanalysiscode2," +
                " case when fanalysiscode2 = 'EQ' then 'Balance b/f (上期結餘) - 股票'" +
                " when fanalysiscode2 = 'FI' then 'Balance b/f (上期結餘) - 债券'" +
                " when fanalysiscode2 = 'WT' then 'Balance b/f (上期結餘) - 渦輪'" +
                " when fanalysiscode2 = 'TR' then 'Balance b/f (上期結餘) - 基金'" +
                " when fanalysiscode2 = 'DR' then 'Balance b/f (上期結餘) - FOF'" +
                " when fanalysiscode2 = 'DE' then 'Balance b/f (上期結餘) - 存款' else fanalysiscode2 end as FDesc,a.Fportcode from " +
//               pub.yssGetTableName("tb_stock_cash") +
                "(select * from " + pub.yssGetTableName("tb_stock_cash") + " where FCheckState = 1 and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(this.endDate, "yyyy") + "00") +
                " ) a left join (select * " +
                " from " + pub.yssGetTableName("tb_para_cashaccount") +
                ") b on a.fcashacccode = b.fcashacccode " +
                " where b.fsubacctype = '0415' and a.fportcode =" +
                dbl.sqlString(port) + " group by fanalysiscode2,a.Fportcode)";
            rs = dbl.openResultSet(strSql);
            setResultValue(valueMap, rs, true);
        } catch (Exception e) {
            throw new YssException("获取期初余额报错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 得到本月的期初，期初余额+月中发生额
     * @param dStartDate Date
     * @param dEndDate Date
     * @param port String
     * @param valueMap LinkedHashMap
     * @throws YssException
     */
    public void getQcAbalance(java.util.Date dStartDate, java.util.Date dEndDate,
                              String port, LinkedHashMap valueMap) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "select  to_char("+dbl.sqlDate(dStartDate) +",'yyyyMMdd')||"+FundInOut_Accumulate+" as forder, "+ dbl.sqlDate(dStartDate) +
                " as fstoragedate,sum(fmoney * (case when c.fcurycode='JPY' then r.fbaserate/100 else  nvl(r.fbaserate, 1)  end)) as fportcurybal,fanalysiscode2," +
                " case when fanalysiscode2 = 'EQ' then 'Balance b/f (上期結餘) - 股票'" +
                " when fanalysiscode2 = 'FI' then 'Balance b/f (上期結餘) - 债券'" +
                " when fanalysiscode2 = 'TR' then 'Balance b/f (上期結餘) - 基金'" +
                " when fanalysiscode2 = 'WT' then 'Balance b/f (上期結餘) - 渦輪'" +
                " when fanalysiscode2 = 'DR' then 'Balance b/f (上期結餘) - 衍生工具'" +
                " when fanalysiscode2 = 'DE' or fanalysiscode2 = 'MK' then 'Balance b/f (上期結餘) - 存款' else fanalysiscode2 end as FDesc,m.Fportcode" +
                " from (select Ftransferdate,(case when finout=1 then fmoney else (-1)*fmoney end) as fmoney, fcashacccode,fanalysiscode2,Fbasecuryrate ,b.Fportcode" +
                " from (select * from " +
                pub.yssGetTableName("tb_cash_transfer") +
                " ) a join (select * from " +
                pub.yssGetTableName("tb_cash_subtransfer") +
                " )b on a.fnum = b.fnum where a.ftransferdate < " +
                dbl.sqlDate(dStartDate) +
                " and a.ftransferdate >  " + dbl.sqlDate(YssFun.toSqlDate( (YssFun.getYear(dStartDate) - 1) + "-12-31")) + //alter by chenjia 大于期初日期的前一年的最后一天
                " and fportcode=" + dbl.sqlString(port) + " and " +
                "ftsftypecode = '04') m join (select * from " +
                pub.yssGetTableName("tb_para_cashaccount") +
                ") c on m.fcashacccode = c.fcashacccode " + " left join (/*select * from " + pub.yssGetTableName("tb_data_valrate") +
                " where fvaldate= " + "(select max(fvaldate) from " + pub.yssGetTableName("tb_data_valrate")
                + " where fvaldate < " + dbl.sqlDate(dEndDate) + ")" + //小于等于期末日期的最大日期的汇率 alter by chenjia
                " */ select  fvaldate,fportcode,Fcurycode, fbaserate from " + pub.yssGetTableName("tb_data_valrate") +
                " where Fcheckstate=1) r on c.fcurycode = r.fcurycode and c.fportcode=r.fportcode and to_char(m.Ftransferdate,'yyyyMM')=to_char(r.fvaldate,'yyyyMM')and Ftransferdate<fvaldate group by fanalysiscode2,m.Fportcode ";
            if (!YssFun.formatDate(dStartDate, "yyyy-MM-dd").toString().
                substring(0, 7).equals(YssFun.formatDate(this.endDate, "yyyy") + "-01")) { //判断是非期末日期的一月份 alter by chenjia
                rs = dbl.openResultSet(strSql);
                setResultValue(valueMap, rs, true);
            }
        } catch (Exception e) {
            throw new YssException("获取本月余额报错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 将资产注入注出的数据封装放入HashMap中。
     * @param valueMap HashMap
     * @param rs ResultSet
     * @throws YssException
     */
    private void setResultValue(LinkedHashMap valueMap, ResultSet rs, boolean isQcAbalance) throws
        YssException {
        String strCat = "";
        double checkKey = 0; //如果在非1月的报表查询中，前几月有发生额的，结余显示最终的余额
        if (null == valueMap) {
            throw new YssException("未实例化Map！");
        }
        if (null == rs) {
            return;
        }
        FundInOutBean fundInOut = null;
        try {
            while (rs.next()) {
                fundInOut = new FundInOutBean();
                if (isQcAbalance) {
                    strCat = rs.getString("fanalysiscode2");
                    if (strCat.equals("EQ")) {
                        checkKey = dQcAbalanceEQ;
                        dQcAbalanceEQ = dQcAbalanceEQ + rs.getDouble("fportcurybal");
                    } else if (strCat.equals("FI")) {
                        checkKey = dQcAbalanceFI;
                        dQcAbalanceFI = dQcAbalanceFI + rs.getDouble("fportcurybal");
                    } else if (strCat.equals("WT")) {
                        checkKey = dQcAbalanceWT;
                        dQcAbalanceWT = dQcAbalanceWT + rs.getDouble("fportcurybal");
                    } else if (strCat.equals("DR")) {
                        checkKey = dQcAbalanceDR;
                        dQcAbalanceDR = dQcAbalanceDR + rs.getDouble("fportcurybal");
                    } else if (strCat.equals("DE")) {
                        checkKey = dQcAbalanceDE;
                        dQcAbalanceDE = dQcAbalanceDE + rs.getDouble("fportcurybal");
                    } else if (strCat.equals("TR")) {
                        checkKey = dQcAbalanceTR;
                        dQcAbalanceTR = dQcAbalanceTR + rs.getDouble("fportcurybal");
                    }

                    fAbalance = fAbalance + YssFun.roundIt(rs.getDouble("fportcurybal"),2);
                    if (startDate.substring(0, 7).equals(YssFun.formatDate(this.endDate, "yyyy") + "-01")) { //判断是不是期末日期的一月份 alter by chenjia
                        fundInOut.transDate = YssFun.toSqlDate(startDate);
                        fundInOut.ccy = "HKD";
                        fundInOut.desc = rs.getString("FDesc");
                        fundInOut.portAccbalance = rs.getDouble("fportcurybal");
                        fundInOut.Portcode = rs.getString("FPortcode");
                        fundInOut.forder=rs.getString("forder");
                        checkKey = fundInOut.portAccbalance;
                    } else {
                        strCat = rs.getString("fanalysiscode2");
                        if (strCat.equals("EQ")) {
                            fundInOut.portAccbalance = dQcAbalanceEQ;
                        } else if (strCat.equals("FI")) {
                            fundInOut.portAccbalance = dQcAbalanceFI;
                        } else if (strCat.equals("WT")) {
                            fundInOut.portAccbalance = dQcAbalanceWT;
                        } else if (strCat.equals("DR")) {
                            fundInOut.portAccbalance = dQcAbalanceDR;
                        } else if (strCat.equals("DE")) {
                            fundInOut.portAccbalance = dQcAbalanceDE;
                        }else if (strCat.equals("TR")) {
                            fundInOut.portAccbalance = dQcAbalanceTR;
                        }

                        fundInOut.transDate = YssFun.toDate(startDate);
                        fundInOut.ccy = "HKD";
                        fundInOut.desc = rs.getString("FDesc");
                        fundInOut.Portcode = rs.getString("FPortcode");
                        fundInOut.forder=rs.getString("forder");
                        if (checkKey == 0) {
                            checkKey = fundInOut.portAccbalance;
                        }
                    }
                    ybalance=fAbalance;
                } else {
                    fundInOut.transDate = rs.getDate("Ftransferdate");
                    fundInOut.ccy = rs.getString("FCCY");
                    fundInOut.desc = rs.getString("FDesc");
                    fundInOut.inFlow = rs.getDouble("FInflow");
                    fundInOut.inFlowHKD = rs.getDouble("FInflowHKD");
                    fundInOut.outFlow = rs.getDouble("FOutflow");
                    fundInOut.outFlowHKD = rs.getDouble("FOutflowHKD");
                    fundInOut.exRate = rs.getDouble("FExRate");
                    fAbalance = fAbalance + YssFun.roundIt(rs.getDouble("FInflowHKD"),2) -
                    YssFun.roundIt(rs.getDouble("FOutflowHKD"),2);
                    fundInOut.portAccbalance = fAbalance;
                    fundInOut.Portcode = rs.getString("FPortcode");
                    fundInOut.forder=rs.getString("forder");
                    checkKey = fundInOut.portAccbalance;
                }

                valueMap.put(fundInOut.transDate.toString() + Double.toString(checkKey) + fundInOut.desc +fundInOut.ccy, fundInOut);
            }
        } catch (SQLException ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
