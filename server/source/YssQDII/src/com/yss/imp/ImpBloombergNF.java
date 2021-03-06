package com.yss.imp;

import com.yss.util.YssException;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssFun;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.yss.main.parasetting.FeeBean;
import com.yss.util.YssD;
import com.yss.util.YssOperCons;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.parasetting.CashAccountBean;

public class ImpBloombergNF
      extends BaseDataSettingBean {
   public ImpBloombergNF() {
   }

public String saveBloombergData(String strValues) throws YssException {
   ResultSet rs = null;
   Connection conn = dbl.loadConnection();
   boolean bTrans = false ;
   //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
   PreparedStatement psSub = null;
   //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
   try{
      conn.setAutoCommit(false);
      bTrans = true;
      int intCount = 0;

      if (!dbl.yssTableExist(pub.yssGetTableName("Tb_Data_SubTrade_blm"))) {
         try {
            dbl.executeSql("CREATE Table " +
                           pub.yssGetTableName("Tb_Data_SubTrade_blm") + "("
                           + "FNum                VARCHAR2(20)      NOT NULL,"
                           + "FSecurityCode       VARCHAR2(20)      NOT NULL,"
                           + "FPortCode           VARCHAR2(20),"
                           + "FBrokerCode         VARCHAR2(20)      NOT NULL,"
                           + "FInvMgrCode         VARCHAR2(20)      NOT NULL,"
                           + "FTradeTypeCode      VARCHAR2(20)      NOT NULL,"
                           + "FCashAccCode        VARCHAR2(20)      NOT NULL,"
                           + "FAttrClsCode        VARCHAR2(20),"
                           + "FBargainDate        DATE              NOT NULL,"
                           + "FBargainTime        VARCHAR2(20)      NOT NULL,"
                           + "FSettleDate         DATE              NOT NULL,"
                           + "FSettleTime         VARCHAR2(20)      NOT NULL,"
                           + "FAutoSettle         NUMBER(1, 0)      NOT NULL,"
                           + "FPortCuryRate       NUMBER(18, 12)    NOT NULL,"
                           + "FBaseCuryRate       NUMBER(18, 12)    NOT NULL,"
                           + "FAllotProportion    NUMBER(18, 8)     NOT NULL,"
                           + "FOldAllotAmount     NUMBER(18, 4)     NOT NULL,"
                           + "FAllotFactor        NUMBER(18, 4)     NOT NULL,"
                           + "FTradeAmount        NUMBER(18, 4)     NOT NULL,"
                           + "FTradePrice         NUMBER(18, 4)     NOT NULL,"
                           + "FTradeMoney         NUMBER(18, 4)     NOT NULL,"
                           + "FAccruedInterest    NUMBER(18, 4),"
                           + "FFeeCode1           VARCHAR2(20),"
                           + "FTradeFee1          NUMBER(18, 4),"
                           + "FFeeCode2           VARCHAR2(20),"
                           + "FTradeFee2          NUMBER(18, 4),"
                           + "FFeeCode3           VARCHAR2(20),"
                           + "FTradeFee3          NUMBER(18, 4),"
                           + "FFeeCode4           VARCHAR2(20),"
                           + "FTradeFee4          NUMBER(18, 4),"
                           + "FFeeCode5           VARCHAR2(20),"
                           + "FTradeFee5          NUMBER(18, 4),"
                           + "FFeeCode6           VARCHAR2(20),"
                           + "FTradeFee6          NUMBER(18, 4),"
                           + "FFeeCode7           VARCHAR2(20),"
                           + "FTradeFee7          NUMBER(18, 4),"
                           + "FFeeCode8           VARCHAR2(20),"
                           + "FTradeFee8          NUMBER(18, 4),"
                           + "FTotalCost          NUMBER(18, 4),"
                           +
                  "FCost               NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                           +
                  "FMCost              NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                           +
                  "FVCost              NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                           +
                  "FBaseCuryCost       NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                           +
                  "FMBaseCuryCost      NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                           +
                  "FVBaseCuryCost      NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                           +
                  "FPortCuryCost       NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                           +
                  "FMPortCuryCost      NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                           +
                  "FVPortCuryCost      NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                           +
                  "FSettleState        NUMBER(1, 0)      DEFAULT 0 NOT NULL,"
                           + "FOrderNum           VARCHAR2(20),"
                           + "FDataSource         NUMBER(1, 0)      NOT NULL,"
                           + "FDesc               VARCHAR2(100),"
                           + "FCheckState         NUMBER(1, 0)      NOT NULL,"
                           + "FCreator            VARCHAR2(20)      NOT NULL,"
                           + "FCreateTime         VARCHAR2(20)      NOT NULL,"
                           + "FCheckUser          VARCHAR2(20),"
                           + "FCheckTime          VARCHAR2(20),"
                           + "FIsinCode          VARCHAR2(20),"
                           + "CONSTRAINT " +
                           pub.yssGetTableName("PK_Tb_Data_SubTrade_blm") +
                           " PRIMARY KEY (FNum))");
         }
         catch (Exception e) {
            throw new YssException("保存澎博接口数据失败！", e);
         }
      }

      String[] strRows = strValues.split("\r\n");
      Date dateTrade = YssFun.toSqlDate(strRows[0].split(" ")[0]);

      if (strRows[1].equalsIgnoreCase("Delete")) {
         conn.setAutoCommit(false);
         bTrans = true;
         dbl.executeSql("delete from " + pub.yssGetTableName("Tb_Data_SubTrade_blm") + " where FBargainDate = " + dbl.sqlDate(dateTrade));
         conn.commit();
         conn.setAutoCommit(true);
         bTrans = false;
         return "";
      }
      if(!strRows[strRows.length-1].equals("yes")){
    	  String temp2="";
    	  String temp3="";
    	  if(strRows[1].equals("US")){
    		  temp2="U";
    		  temp3="【美国市场】";
    	  }else if(strRows[1].equals("LI")){
    		  temp2="L";
    		  temp3="【英国市场】";
    	  }else if(strRows[1].equals("HK")){  	
    		  temp2="H";
    		  temp3="【香港市场】";
    	  }else{
    		  temp2="OTHER";
    		  temp3="【其它市场】";
    	  }
          String temp ="select y.fsecuritycode from " + pub.yssGetTableName("Tb_Data_SubTrade") +
           " y where y.FBargainDate = " + dbl.sqlDate(dateTrade)+" and y.fdatabirth='OMGEO' and substr(y.fsecuritycode,instr(y.fsecuritycode,' ')+1,1)= '"+temp2+"'";
          rs=dbl.openResultSet(temp);
          if(rs.next()){
        	  return "yes|BloomBerg"+temp3+"交易数据已经导入，是否要重新导入覆盖原有数据";
          }
      }else{
     	 String[] temp = strRows;
    	 strRows= new String[strRows.length-1];
    	 for(int i =0;i<temp.length-1;i++){
    		 strRows[i]=temp[i];
    	 }
     }
      //导入交易数据之前,进行证券代码的比对,核查系统里没有维护的新券.
      StringBuffer sb = new StringBuffer();
      List lst= new ArrayList();
      for (int i = 2; i < strRows.length; i++) {
    	  String[] strCols = strRows[i].split("\t", -1);
          if (strCols.length < 31) {
             throw new YssException("保存澎博接口数据失败！\r\n数据格式不正确，请检查第" + i + "行。");
          }
          String str1=strCols[0].split(" ")[0];
          String str2=strCols[0];
          sb.append("'"+str1+"',").append("'"+str2+"',");
          lst.add(str1);
      }
      String sqlstr=sb.substring(0, sb.length()-1);

      String TempSql="select distinct fmarketcode from "+pub.yssGetTableName("Tb_Para_Security")+ " where fmarketcode in ("+sqlstr+")";
      rs=dbl.openResultSet(TempSql);

      List lst2= new ArrayList();

      while (rs.next()) {
    	  lst2.add(rs.getString("fmarketcode"));
      }
      
      rs.getStatement().close();
      rs = null;
      
      StringBuffer tempSb= new StringBuffer();
      for (int j=0;j<lst.size();j++){
    	  String temp= lst.get(j).toString();
    	  if(lst2.contains(temp)){
    		  continue;
    	  }else{
    		  tempSb.append(temp+",");
    	  }
      }
      
      
      if (tempSb.length()!=0) {
         throw new YssException("系统中没有下列证券代码的证券" + tempSb.toString() +
                                "，请检查设置！");
      }
      dbl.executeSql("delete from " + pub.yssGetTableName("Tb_Data_SubTrade_blm") + " where FBargainDate = " + dbl.sqlDate(dateTrade));

      String strSql = "insert into " + pub.yssGetTableName("Tb_Data_SubTrade_blm") +
            "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
            " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
            " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
            " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST," +
            " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
            " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
            " FTotalCost, FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryCost,FMPortCuryCost,FVPortCuryCost, " +
            " FOrderNum, FDataSource, FDesc, FCheckState, FCreator, FCreateTime, FCheckUser,FIsinCode)" +
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
      psSub = dbl.openPreparedStatement(strSql);
      //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//

      for (int i = 2; i < strRows.length; i++) {
         String[] strCols = strRows[i].split("\t", -1);
         if (strCols.length < 31) {
            throw new YssException("保存澎博接口数据失败！\r\n数据格式不正确，请检查第" + i + "行。");
         }

         if (strCols[0].equalsIgnoreCase("CASH")) {
            continue;
         }

         Date bargainDate=YssFun.toSqlDate(YssFun.left(strCols[8],4)+"-"+YssFun.mid(strCols[8],4,2)+"-"+YssFun.right(strCols[8],2));
         if(!dateTrade.equals(bargainDate))  continue;

         String str1=strCols[0].split(" ")[0];
         String str2=strCols[0];
         strSql="select distinct fsecuritycode from "+pub.yssGetTableName("Tb_Para_Security")+ " where fmarketcode in ('"+str1+"','"+str2+"')";
         rs=dbl.openResultSet(strSql);

         String strCode="";

         while (rs.next()) {
            strCode=rs.getString("fsecuritycode");
         }
         rs.getStatement().close();
         rs = null;
         if (strCode.trim().length()==0) {
            throw new YssException("系统中没有下列证券代码的证券" + str2.toString() +
                                   "，清检查设置！");
         }
      

         double dubBaseRate = this.getSettingOper().getCuryRate(dateTrade,
               strCols[7], "GFund", YssOperCons.YSS_RATE_BASE); //基础汇率
         double dubPortRate = this.getSettingOper().getCuryRate(dateTrade,
               pub.getPortBaseCury("GFund"), "GFund", YssOperCons.YSS_RATE_PORT); //组合汇率 // edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A


         String strNum = "T" + YssFun.formatDate(YssFun.toSqlDate(YssFun.left(strCols[8],4)+"-"+YssFun.mid(strCols[8],4,2)+"-"+YssFun.right(strCols[8],2)),"yyyyMMdd") + YssFun.formatNumber(intCount,"0000") ;
         psSub.setString(1, strNum); //编号
         psSub.setString(2, strCode); //证券代码
         psSub.setString(3, strCols[2]); //组合代码
         psSub.setString(4, strCols[4]); //券商代码
         psSub.setString(5, strCols[5]); //投资经理代码
         psSub.setString(6, strCols[6]); //交易方式
         psSub.setString(7, " "); //现金帐户代码
         psSub.setString(8, " "); //所属分类
         psSub.setDate(9, YssFun.toSqlDate(YssFun.left(strCols[8],4)+"-"+YssFun.mid(strCols[8],4,2)+"-"+YssFun.right(strCols[8],2))); //成交日期
         psSub.setString(10, "00:00:00"); //成交时间
         psSub.setDate(11, YssFun.toSqlDate(YssFun.left(strCols[10],4)+"-"+YssFun.mid(strCols[10],4,2)+"-"+YssFun.right(strCols[10],2))); //结算日期
         psSub.setString(12, "00:00:00"); //结算时间
         psSub.setInt(13, 1); //自动结算
         psSub.setDouble(14, dubPortRate); //组合汇率
         psSub.setDouble(15, dubBaseRate); //基础汇率

         psSub.setDouble(16, 1); //分配比例
         psSub.setDouble(17, Double.parseDouble(strCols[14])); //原始分配数量

         psSub.setDouble(18, 1); //分配因子
         psSub.setDouble(19, Double.parseDouble(strCols[14])); //交易数量
         psSub.setDouble(20, Double.parseDouble(strCols[15])); //交易价格
         psSub.setDouble(21, Double.parseDouble(strCols[17])); //交易金额
         psSub.setDouble(22, strCols[18].length()==0?0: Double.parseDouble(strCols[18])); //应计利息
         psSub.setString(23, strCols[19]); //费用代码1
         psSub.setDouble(24, Double.parseDouble(strCols[20])); //交易费用1
         psSub.setString(25, strCols[21]); //费用代码2
         psSub.setDouble(26, Double.parseDouble(strCols[22])); //交易费用2
         psSub.setString(27, strCols[23]); //费用代码3
         psSub.setDouble(28, Double.parseDouble(strCols[24])); //交易费用3
         psSub.setString(29, strCols[25]); //费用代码4
         psSub.setDouble(30, Double.parseDouble(strCols[26])); //交易费用4
         psSub.setString(31, strCols[27]); //费用代码5
         psSub.setDouble(32, Double.parseDouble(strCols[28])); //交易费用5
         psSub.setString(33, " "); //费用代码6
         psSub.setDouble(34, 0); //交易费用6
         psSub.setString(35, " "); //费用代码7
         psSub.setDouble(36, 0); //交易费用7
         psSub.setString(37, " "); //费用代码8
         psSub.setDouble(38, 0); //交易费用8

         double dubTotal = Double.parseDouble(strCols[17]) +
               (YssD.add(Double.parseDouble(strCols[20]), Double.parseDouble(strCols[22]),Double.parseDouble(strCols[24]),Double.parseDouble(strCols[26]),Double.parseDouble(strCols[28]))) *
               ((strCols[6].equalsIgnoreCase("01")||strCols[6].equalsIgnoreCase("1")) ? 1 : -1);
         psSub.setDouble(39, dubTotal); //投资总成本

         double dubCurr = YssFun.roundIt(dubBaseRate * 0, 2);
         psSub.setDouble(40, 0); //原币核算成本
         psSub.setDouble(41, 0); //原币管理成本
         psSub.setDouble(42, 0); //原币估值成本
         psSub.setDouble(43, dubCurr); //基础货币核算成本
         psSub.setDouble(44, dubCurr); //基础货币管理成本
         psSub.setDouble(45, dubCurr); //基础货币估值成本
         dubCurr = YssFun.roundIt(dubCurr / dubPortRate, 2);
         psSub.setDouble(46, dubCurr); //组合货币核算成本
         psSub.setDouble(47, dubCurr); //组合货币管理成本
         psSub.setDouble(48, dubCurr); //组合货币估值成本
         psSub.setString(49, strCols[30]); //订单编号
         psSub.setInt(50, 1); //数据来源
         psSub.setString(51, strCols[31]); //描述
         psSub.setInt(52, 0); //审核状态
         psSub.setString(53, pub.getUserName()); //创建人、修改人

         String strNowDate = YssFun.formatDate(new java.util.Date(),"yyyyMMdd HH:mm:ss");
         psSub.setString(54, strNowDate); //创建、修改时间
         psSub.setString(55, " "); //复核人
         psSub.setString(56, strCols[1].split(" ")[0]); //Isin码

         psSub.addBatch();
         intCount++;

      }

      psSub.executeBatch();
      conn.commit();
      conn.setAutoCommit(true);
      bTrans = false;

      changeToTradeData(dateTrade,strRows[1]);
   }
   catch (BatchUpdateException bue) {
      throw new YssException("保存澎博接口数据失败！", bue);
   }
   catch (Exception e) {
      throw new YssException("保存澎博接口数据失败！", e);
   }
   finally {
      dbl.closeResultSetFinal(rs);
      dbl.endTransFinal(conn, bTrans);
      //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
      dbl.closeStatementFinal(psSub);
      //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
   }
   
   return "";
 }

   public void changeToTradeData(Date dateTrade,String market) throws YssException {
   ResultSet rs = null;
   int intCount;

   Connection conn = dbl.loadConnection();
   boolean bTrans = false;
   //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
   PreparedStatement ps = null;
   PreparedStatement psSub = null;
   //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
   try {
      conn.setAutoCommit(false);
      bTrans = true;
      //通过字典取得美国市场的转换
      if(market.equalsIgnoreCase("US")){
    	  rs=dbl.openResultSet("  select a.fsrcconent,a.fcnvconent from " + pub.yssGetTableName("Tb_Dao_Dict") + " a where a.fdictcode = 'DCT000004'");
    	  String temp="";
    	  while(rs.next()){
    		  temp +=" b.fexchangecode='"+rs.getString("fsrcconent").trim()+"' or";
    	  }
    	  rs.getStatement().close();
    	  rs=null;
    	  if(!temp.equals("")){
    		  temp=temp.substring(0,temp.length()-3);
    	  }
    	  market=" ("+temp+")";
      }else if(market.equalsIgnoreCase("OTHER")){
    	  market=" b.fexchangecode not in ('UA','UQ','UP','UN','HK','LI')";
      }else{
    	  market=" b.fexchangecode='"+market+"'";
      }
      
      
      String strNowDate = YssFun.formatDate(new java.util.Date(),
                                            "yyyyMMdd HH:mm:ss");
      BaseCashAccLinkDeal cashacc = new BaseCashAccLinkDeal();
      cashacc.setYssPub(pub);
      CashAccountBean caBean = null;

      String strSql = "insert into " + pub.yssGetTableName("Tb_Data_Trade") +
            "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
            " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
            " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTFACTOR," +
            " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FUNITCOST,FACCRUEDINTEREST," +
            " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
            " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
            " FTotalCost , FOrderNum, FDesc, FCheckState, FCreator, FCreateTime,FCheckUser,FCheckTime)" + //liyu 1128 新增三个字段
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
      ps = dbl.openPreparedStatement(strSql);
      //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
      strSql = "insert into " + pub.yssGetTableName("Tb_Data_SubTrade") +
            "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
            " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
            " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
            " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST," +
            " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
            " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
            " FTotalCost, FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryCost,FMPortCuryCost,FVPortCuryCost, " +
            " FOrderNum, FDataSource, FDesc, FCheckState, FCreator, FCreateTime, FCheckUser,FCheckTime,FFactSETTLEDATE,FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate,FDataBirth,FSeatCode)" + //liyu 1128 新增三个字段
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
      psSub = dbl.openPreparedStatement(strSql);
      //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
      String test="delete from " + pub.yssGetTableName("Tb_Data_Trade") +
      " where fnum in (select substr(fnum,1,15) fnum from "+
      pub.yssGetTableName("Tb_Data_SubTrade") +" a left join "+pub.yssGetTableName("Tb_Para_Security")+ " b on a.fsecuritycode=b.fsecuritycode where a.fbargaindate="+
      dbl.sqlDate(dateTrade)+ " and (a.fdatabirth='OMGEO' or a.fdatabirth='BROKER') and "+market+")";
      dbl.executeSql(test);
      test ="delete from " + pub.yssGetTableName("Tb_Data_SubTrade") +
      " where fnum in (select fnum from "+
      pub.yssGetTableName("Tb_Data_SubTrade") +" a left join "+pub.yssGetTableName("Tb_Para_Security")+ " b on a.fsecuritycode=b.fsecuritycode where a.fbargaindate="+
      dbl.sqlDate(dateTrade)+ " and (a.fdatabirth='OMGEO' or a.fdatabirth='BROKER') and "+market+")";
      dbl.executeSql(test);
      
      //判断在导入交易数据的当天有没有汇率数据。
      rs = dbl.openResultSet("select * from "
					+ pub.yssGetTableName("Tb_data_exchangerate")
					+ " x where x.fexratedate = "+ dbl.sqlDate(dateTrade));
	if (!rs.next()) {
		throw new YssException("系统中没有今天的汇率数据，请在导入交易数据前维护当天汇率数据！");
	}
	rs.getStatement().close();
	//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
	rs.close();
	rs = null;  
	
      strSql="select max(to_number(substr(fnum,12,4))) fnum from "+pub.yssGetTableName("Tb_Data_SubTrade") +
            " where substr(fnum,2,8)='"+YssFun.formatDate(dateTrade, "yyyyMMdd")+"'";
      rs=dbl.openResultSet(strSql);
      if(rs.next())
      {
         intCount = 1 + rs.getInt("fnum");
      }
      else
      {
         intCount = 1;
      }
      rs=null;
      //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
      dbl.closeResultSetFinal(rs);
      
      strSql = "Select n.fcnvconent,case when a.fcnvconent is null or a.fcnvconent='' then t.fbrokercode else a.fcnvconent end as FBrokerCode, t.*,k.fcnvconent fmgrcode from "+ pub.yssGetTableName("Tb_Data_SubTrade_blm")+
             " t left join "+pub.yssGetTableName("Tb_Dao_Dict")+" n on t.fportcode = n.fsrcconent and n.fdictcode = '002'"+
             "left join "+pub.yssGetTableName("Tb_Dao_Dict")+" k on t.finvmgrcode=k.fsrcconent and k.fdictcode='004' left join "+pub.yssGetTableName("Tb_Dao_Dict")+
             " a on t.fbrokercode = a.fsrcconent and a.fdictcode='DCT000001' left join "+pub.yssGetTableName("Tb_Para_Security")+ " b on t.fsecuritycode=b.fsecuritycode and b.fcheckstate=1 where t.fdesc in('2','6') and t.fbrokercode not in ('BB') and fbargaindate = " + dbl.sqlDate(dateTrade) + " and " + market
             +" and t.fnum not in (select x.fnum from(select * from " + pub.yssGetTableName("Tb_Data_SubTrade_blm") 
             +" x where x.fdesc='2' and fbargaindate = " + dbl.sqlDate(dateTrade) + " )x "
             +" join (select * from " + pub.yssGetTableName("Tb_Data_SubTrade_blm")
             + " x where x.fdesc='102' and fbargaindate = " + dbl.sqlDate(dateTrade)
             +" )y on x.fsecuritycode=y.fsecuritycode and x.fbrokercode=y.fbrokercode and x.ftradeamount=y.ftradeamount and x.ftradeprice="
             +" y.ftradeprice and x.ftotalcost=y.ftotalcost and x.fordernum=y.fordernum)";
      rs = dbl.openResultSet(strSql);
      while (rs.next()) {
    	  String StrBStmp = new String();
     	 if((rs.getString("FTradeTypeCode").equalsIgnoreCase("1") || rs.getString("FTradeTypeCode").equalsIgnoreCase("01"))){
     		 StrBStmp = "02";
     	 }else if((rs.getString("FTradeTypeCode").equalsIgnoreCase("2") || rs.getString("FTradeTypeCode").equalsIgnoreCase("02"))){
     		 StrBStmp = "09";
     	 }else{
     		 StrBStmp = "00";
     	 }
          String strNum = "T" +
                YssFun.formatDate(dateTrade, "yyyyMMdd") + StrBStmp +
                YssFun.formatNumber(intCount, "0000");

         String strCashAccCode = " ";
         String strBS = (rs.getString("FTradeTypeCode").equalsIgnoreCase("1") || rs.getString("FTradeTypeCode").equalsIgnoreCase("01")) ?
                 "01" : "02";
         cashacc.setLinkParaAttr(rs.getString("fmgrcode"), rs.getString("fcnvconent"), rs.getString("FSecurityCode"), rs.getString("FBrokerCode"),
                                 rs.getString("FTradeTypeCode"), dateTrade);
         caBean = cashacc.getCashAccountBean();
         if (caBean != null) {
            strCashAccCode = caBean.getStrCashAcctCode();
         }
         ps.setString(1, strNum); //编号
         ps.setString(2, rs.getString("FSecurityCode")); //证券代码
         ps.setString(3, rs.getString("fcnvconent")); //组合代码
         ps.setString(4, rs.getString("FBrokerCode")); //券商代码
         ps.setString(5, rs.getString("fmgrcode")); //投资经理代码
         ps.setString(6, strBS); //交易方式
         ps.setString(7, strCashAccCode); //现金帐户代码
         ps.setString(8, rs.getString("FAttrClsCode")); //所属分类
         ps.setDate(9, rs.getDate("FBargainDate")); //成交日期
         ps.setString(10, rs.getString("FBargainTime")); //成交时间
         ps.setDate(11, rs.getDate("FSettleDate")); //结算日期
         ps.setString(12, rs.getString("FSettleTime")); //结算时间
         ps.setInt(13, rs.getInt("FAutoSettle")); //自动结算
         ps.setDouble(14, rs.getDouble("FPortCuryRate")); //组合汇率
         ps.setDouble(15, rs.getDouble("FBaseCuryRate")); //基础汇率
         ps.setDouble(16, rs.getDouble("FAllotFactor")); //分配因子
         ps.setDouble(17, rs.getDouble("FTradeAmount")); //交易数量
         ps.setDouble(18, rs.getDouble("FTradePrice")); //交易价格
         ps.setDouble(19, rs.getDouble("FTradeMoney")); //交易金额
         ps.setDouble(20, rs.getDouble("FTradePrice")); //单位成本
         ps.setDouble(21, rs.getDouble("FACCRUEDINTEREST")); //应计利息
         ps.setString(22, "COMMISSIONS"); //费用代码1
         ps.setDouble(23, rs.getDouble("FTradeFee1")); //交易费用1
         ps.setString(24, "Fee"); //费用代码2
         ps.setDouble(25, rs.getDouble("FTradeFee2")+rs.getDouble("FTradeFee3")+rs.getDouble("FTradeFee4")+rs.getDouble("FTradeFee5")); //交易费用2
         ps.setString(26, " "); //费用代码3
         ps.setDouble(27, 0); //交易费用3
         ps.setString(28, " "); //费用代码4
         ps.setDouble(29, 0); //交易费用4
         ps.setString(30, " "); //费用代码5
         ps.setDouble(31, 0); //交易费用5
         ps.setString(32, " "); //费用代码6
         ps.setDouble(33, 0); //交易费用6
         ps.setString(34, " "); //费用代码7
         ps.setDouble(35, 0); //交易费用7
         ps.setString(36, " "); //费用代码8
         ps.setDouble(37, rs.getDouble("FTradeFee8")); //交易费用8
         ps.setDouble(38, rs.getDouble("FTotalCost")); //投资总成本
         ps.setString(39, rs.getString("FOrderNum")); //订单编号
         ps.setString(40, rs.getString("FDESC")); //描述
         ps.setInt(41, 1); //审核状态
         ps.setString(42, pub.getUserName()); //创建人、修改人
         ps.setString(43, strNowDate); //创建、修改时间
         ps.setString(44, pub.getUserName()); //复核人
         ps.setString(45, strNowDate); //复核时间

         psSub.setString(1, strNum+ "00000"); //编号
         psSub.setString(2, rs.getString("FSecurityCode")); //证券代码
         psSub.setString(3, rs.getString("fcnvconent")); //组合代码
         psSub.setString(4, rs.getString("FBrokerCode")); //券商代码
         psSub.setString(5, rs.getString("FMGRCODE")); //投资经理代码
         psSub.setString(6, strBS); //交易方式
         psSub.setString(7, strCashAccCode); //现金帐户代码
         psSub.setString(8, rs.getString("FAttrClsCode")); //所属分类
         psSub.setDate(9, rs.getDate("FBargainDate")); //成交日期
         psSub.setString(10, rs.getString("FBargainTime")); //成交时间
         psSub.setDate(11, rs.getDate("FSettleDate")); //结算日期
         psSub.setString(12, rs.getString("FSettleTime")); //结算时间
         psSub.setInt(13, rs.getInt("FAutoSettle")); //自动结算
         psSub.setDouble(14, rs.getDouble("FPortCuryRate")); //组合汇率
         psSub.setDouble(15, rs.getDouble("FBaseCuryRate")); //基础汇率

         psSub.setDouble(16, rs.getDouble("FAllotProportion")); //分配比例
         psSub.setDouble(17, rs.getDouble("FTradeAmount")); //原始分配数量

         psSub.setDouble(18, rs.getDouble("FAllotFactor")); //分配因子
         psSub.setDouble(19, rs.getDouble("FTradeAmount")); //交易数量
         psSub.setDouble(20, rs.getDouble("FTradePrice")); //交易价格
         psSub.setDouble(21, rs.getDouble("FTradeMoney")); //交易金额
         psSub.setDouble(22, rs.getDouble("FACCRUEDINTEREST")); //应计利息
         psSub.setString(23, "COMMISSIONS"); //费用代码1
         psSub.setDouble(24, rs.getDouble("FTradeFee1")); //交易费用1
         psSub.setString(25, "Fee"); //费用代码2
         psSub.setDouble(26, rs.getDouble("FTradeFee2")+rs.getDouble("FTradeFee3")+rs.getDouble("FTradeFee4")+rs.getDouble("FTradeFee5")); //交易费用2
         psSub.setString(27, " "); //费用代码3
         psSub.setDouble(28, 0); //交易费用3
         psSub.setString(29, " "); //费用代码4
         psSub.setDouble(30, 0); //交易费用4
         psSub.setString(31, " "); //费用代码5
         psSub.setDouble(32, 0); //交易费用5
         psSub.setString(33, " "); //费用代码6
         psSub.setDouble(34, 0); //交易费用6
         psSub.setString(35, " "); //费用代码7
         psSub.setDouble(36, 0); //交易费用7
         psSub.setString(37, " "); //费用代码8
         psSub.setDouble(38, 0); //交易费用8
         psSub.setDouble(39, rs.getDouble("FTotalCost")); //投资总成本
         double dubCurr = YssFun.roundIt(rs.getDouble("FBaseCuryRate") *
                                         rs.getDouble("FTotalCost"), 2);
         psSub.setDouble(40, rs.getDouble("FTotalCost")); //原币核算成本
         psSub.setDouble(41, rs.getDouble("FTotalCost")); //原币管理成本
         psSub.setDouble(42, rs.getDouble("FTotalCost")); //原币估值成本
         psSub.setDouble(43, dubCurr); //基础货币核算成本
         psSub.setDouble(44, dubCurr); //基础货币管理成本
         psSub.setDouble(45, dubCurr); //基础货币估值成本
         dubCurr = YssFun.roundIt(dubCurr / rs.getDouble("FPortCuryRate"), 2);
         psSub.setDouble(46, dubCurr); //组合货币核算成本
         psSub.setDouble(47, dubCurr); //组合货币管理成本
         psSub.setDouble(48, dubCurr); //组合货币估值成本
         psSub.setString(49, rs.getString("FOrderNum")); //订单编号
         psSub.setInt(50, rs.getInt("FDataSource")); //数据来源
         psSub.setString(51, rs.getString("FDESC")); //描述
         psSub.setInt(52, 1); //审核状态
         psSub.setString(53, pub.getUserName()); //创建人、修改人
         psSub.setString(54, strNowDate); //创建、修改时间
         psSub.setString(55, pub.getUserName()); //复核人
         psSub.setString(56, strNowDate); //复核时间
         psSub.setDate(57, rs.getDate("FSettleDate")); //实际结算日期
         psSub.setString(58, strCashAccCode); //实际结算帐户
         psSub.setDouble(59, rs.getDouble("FTotalCost")); //实际结算金额
         psSub.setDouble(60, 1); //兑换汇率
         psSub.setDouble(61, rs.getDouble("FBaseCuryRate")); //实际结算基础汇率
         psSub.setDouble(62, rs.getDouble("FPortCuryRate")); //实际结算组合汇率

         psSub.setString(63, "OMGEO"); //实际结算组合汇率
         psSub.setString(64, rs.getString("FOrderNum"));//订单编号；
         ps.addBatch();
         psSub.addBatch();
         intCount++;
      }
      rs.getStatement().close();
      //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
      rs.close();
      rs = null;
      ps.executeBatch();
      psSub.executeBatch();

      conn.commit();
      conn.setAutoCommit(true);
      bTrans = false;
   }
   catch (BatchUpdateException bue) {
      throw new YssException("保存券商接口数据失败！", bue);
   }
   catch (Exception e) {
      throw new YssException("保存券商接口数据失败！", e);
   }
   finally {
      dbl.closeResultSetFinal(rs);
      dbl.endTransFinal(conn, bTrans);
      //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
      dbl.closeStatementFinal(ps,psSub);
      //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
   }
}


   private double[] getFee(String strValue) throws YssException{
      FeeBean fee = new FeeBean();
      fee.setYssPub(pub);
      fee.parseRowStr(strValue);
      double dubReturn[] = new double[2]; //0 为佣金， 1 为其他费用合计
      dubReturn[0] = 0;
      dubReturn[1] = 0;
      String[] strList = fee.getListViewData3().split("\r\f");
      if (strList.length < 2) return dubReturn;
      strList = strList[2].split("\f\f");
      for (int i = 0 ; i < strList.length -1 ; i++) { //最后一个是total可以不用考虑
         String[] strCols = strList[i].split("\n");
         if (strCols[0].equalsIgnoreCase("COMMISSIONS")) {
            dubReturn[0] = YssFun.toDouble(strCols[2]);
         }else {
            dubReturn[1] = YssD.add(dubReturn[1] , YssFun.toDouble(strCols[2]));
         }
      }
      return dubReturn;

   }
}
