package com.yss.main.operdeal.datainterface.pretfun;

public class EndDataBean
    extends DataBase {

    /*
       public void makeData(String[] set) throws YssException {
          for (int i = 0; i < set.length; i++) {
             Adjust_XgFK(set[i]);
             Check_SHSZ(set[i]);
             Adjust_SzGzLx(set[i]);
             XwYj_Cal(set[i]);
             Adjust_SzFxj(set[i]);
             Adjust_Fxj(set[i]);
             Update_XgPs(set[i]);
             Update_PxSg(set[i]);

//==         if (calc.getAssetType(set[i]) == 0 && calc.getFundType(set[i]) == 1) //指数基金 调整hzjkqs中股票类型
                Adjust_ZSGP(set[i]);
                AutoAdd_Code(set[i]);
                AutoUpdate_Name(set[i]);
                Update_QySj(set[i]);
//==      }
       }
     */
    /*
     * 信托新股随心打，计算结存成本，投资净收益，卖空后计算托管费、管理费
     */
    /*
        private void xgSxdYw(String set) throws YssException{
     Connection con = dbl.loadConnection();
     Statement st = null;
     ResultSet rs = null;
     boolean bTrans = false;

     String sql = "";
     double dZqSl = 0, dZqJg = 0, dZqJe = 0, dSsl  = 0, dCjJe = 0, dJyFy= 0, dSsje = 0;//中签数量，中签单价，中签金额，卖出数量，卖出成交金额，交易费用，实收金额
     double dJcSl = 0, dJcCb = 0, dTzSy = 0, dGlf = 0, dTgf = 0, dYfSy = 0, dSyJe = 0;//结存数量，结存成本，投资净收益，管理费，托管费，受益人收益，本金及收益合计
     try{
      st = con.createStatement();
         con.setAutoCommit(false);
         bTrans = true;
      sql = "select * from XgHzJkQs where FsDate = " + dbl.sqlDate(base.getDate()) ;
      rs = dbl.openResultSet(sql);
      while(rs.next()){
       dZqSl = rs.getDouble("FZqSl");
       dZqJe = rs.getDouble("FZqJe");//中签金额
       dZqJg = rs.getDouble("FZqJg");//每股中签单价
       dSsl = rs.getDouble("FsSl");
       dCjJe = rs.getDouble("FsCjJe");
       dJyFy = rs.getDouble("FsJyFy");
       dSsje = rs.getDouble("FsSsJe");

       dJcSl = YssD.sub(dZqSl, dSsl);//结存数量
       dJcCb = YssD.round(YssD.mul(dJcSl, dZqJg), 2);

       dTzSy = YssD.round(YssD.sub(dSsje, YssD.mul(dSsl, dZqJg)) , 2);//投资净收益
       dGlf = YssD.round(YssD.mul(dTzSy, 0.11), 2);
       dTgf = YssD.round(YssD.mul(dTzSy, 0.03), 2);
       dYfSy = YssD.sub(dTzSy, dGlf, dTgf);//受益人信托利益
       dSyJe = YssD.add(dZqJe, dYfSy);//受益人本金及收益合计

       sql = "update  XgHzJkQs set FJcSl = " + dJcSl + ", FJcCb = " + dJcCb + ", " +
         "FTzSy = " + dTzSy + ", FGlf = " + dGlf + ", FTgf = " + dTgf + ", FYfSy = " + dYfSy + "," +
         "FSyDate = " + dbl.sqlDate(ds.Get_WorkDay(rs.getDate("FsDate") , 1))+ ", FSyJe = " + dSyJe +
         " where fzqdm = '" + rs.getString("FZqdm")+ "'  and fgddm = '" + rs.getString("FGddm") + "'";
       st.addBatch(sql);
      }
      rs.getStatement().close();
         st.executeBatch();
         con.commit();
         bTrans = false;
     }
     catch(BatchUpdateException ee){
      throw new YssException("处理新股随心打交易数据出错", ee.getNextException());
     }
     catch(Exception e){
      throw new YssException("处理新股随心打交易数据出错", e);
     }
     finally{
      dbl.closeResultSetFinal(rs);
         dbl.closeStatementFinal(st);
         dbl.endTransFinal(con, bTrans);
     }
        }
     */
    /*
// -----------------建行中金集合理财把网上新股的数据删掉，手工维护-----------------------------------------------------------------------------------------------------
       public void DelIPOXgyw(String set) throws YssException {
     String strSql = "";
     Connection con = dbl.loadConnection();
     Statement st = null;
     ResultSet rs = null;

     strSql ="select * from XgYwXx where fdate=" + dbl.sqlDate(base.getDate()) + " and (fxglb='NetIPO')";
     strSql = "select * from (" + strSql + ") a order by FZqdm,FYwlb";
     try{
      rs = dbl.openResultSet(strSql);
      if (rs.next()){
              strSql= "delete from HzJkQs where (fdate=" + dbl.sqlDate(base.getDate()) + " or fdate=" + dbl.sqlDate(ds.Get_WorkDay(base.getDate(),-1)) + ") and fzqbz='XG' ";
              dbl.executeSql(strSql);
            }
      rs.getStatement().close();
      rs=null;
     }
     catch (YssException e) {
      throw e;
     }
     catch (Exception ex) {
      throw new YssException("删除新股业务出错！", ex);
     }
//----------------------------------------------------------------------------------------------------------------------
       }
     */
    /**
     * 调整新股返还金额：当新股有中签时，交易所返还的金额仍是全额返还，因此需要将返还的金额调整为实际的返还金额
     * @param set String      套帐号
     * @throws YssException
     */
    /*
       private void Adjust_XgFK(String set) throws YssException {
          Connection con = dbl.loadConnection();
          Statement st = null;
          ResultSet rs = null;
          boolean bTrans = false;
          try {
             st = con.createStatement();
             con.setAutoCommit(false);
             bTrans = true;
             String strSql = "select fzqdm,fbsfje from HzJkQs where fdate=" + dbl.sqlDate(base.getDate())
                   + " and (fywbz like '%XGZQ' or fywbz='XGZF' or fywbz='KZZZQ' or fywbz='QYZQZQ')";
             rs = dbl.openResultSet(strSql);
             while (rs.next()) {
                strSql = "update HzJkQs set fsje=fsje-" + rs.getDouble("FBsfje") + ",fsssje=fsssje-"
                      + rs.getDouble("FBsfje") + " where (fywbz like '%XGFK' or fywbz='KZZFK' or fywbz='QYZQFK') and fzqdm='" + rs.getString("Fzqdm")
                      + "' and fdate=" + dbl.sqlDate(base.getDate());
                st.addBatch(strSql);
             }
             rs.getStatement().close();
             st.executeBatch();
             con.commit();
             bTrans = false;
          }
          catch (BatchUpdateException ex) {
             throw new YssException("调整新股返还金额出错！", ex);
          }
          catch (Exception ex) {
             throw new YssException("调整新股返还金额出错！", ex);
          }
          finally {
             dbl.closeResultSetFinal(rs);
             dbl.closeStatementFinal(st);
             dbl.endTransFinal(con, bTrans);
          }
       }
     */
    /**
     * 核对上海、深圳两地数据库(成交明细库与清算库核对)
     * @param set String     套帐号
     * @throws YssException
     */
    /*
      private void Check_SHSZ(String set) throws YssException {
      }
     */
    /**
     * 深圳国债利息调整
     * @param set String    套帐号
     * @throws YssException
     */
    /*
        private void Adjust_SzGzLx(String set) throws YssException {
       //深圳交易所国债：
       //某国债买国债利息=统计库的买成交金额-Σ回报库买数量*10*买价格，某国债卖国债利息=统计库的卖成交金额-Σ回报库卖数量*10*卖价格；
       //如一个国债在不同席位上进行交易，计算某个席位的每笔国债成交记录的国债利息=ROUND(成交数量*10*每百元应计利息额，2)，
       //然后按席位和国债代码进行汇总，区分买和卖，分别作为买国债利息和卖国债利息，
       //但该国债的总买国债利息=统计库的买成交金额-Σ回报库买数量*10*买价格，某国债总卖国债利息=统计库的卖成交金额-Σ回报库卖数量*10*卖价格；
       Connection con = dbl.loadConnection();
       Statement st = null;
       ResultSet rs = null, rst = null;
       boolean bTrans = false;
       try {
          st = con.createStatement();
          con.setAutoCommit(false);
          bTrans = true;
          String strSql = "select fzqdm,sum(fbgzlx) as blx,sum(fsgzlx) as slx from " + pub.yssSetPrefix(set)
                + "HzJkQs where fzqbz ='ZQ' and fywbz='GZXQ' and fszsh='S' and fdate=" + dbl.sqlDate(base.getDate()) + " group by fzqdm";
          rs = dbl.openResultSet(strSql);
          while (rs.next()) {
             double dBje = 0, dSje = 0;
             strSql = "select sum(fgzlx) as lx,fbs from " + pub.yssSetPrefix(set) + "HzJkHz where fdate="
                   + dbl.sqlDate(base.getDate()) + " and fzqdm='" + rs.getString("fzqdm") + "' and fzqbz ='ZQ' and fszsh ='S' group by fbs";
             rst = dbl.openResultSet(strSql);
             while (rst.next()) {
                if (rst.getString("fbs").equalsIgnoreCase("B"))
                   dBje = YssFun.roundIt(YssD.sub(rs.getDouble("blx"), rst.getDouble("lx")), 2);
                else
                   dSje = YssFun.roundIt(YssD.sub(rs.getDouble("slx"), rst.getDouble("lx")), 2);
             }
             rst.getStatement().close();
             if (dBje != 0) { //更新回报库中国债利息--原则：更新最大席位号的利息且此席位要有交易
                strSql = "update " + pub.yssSetPrefix(set) + "HzJkHz set fgzlx=fgzlx+(" + dBje + ") where fbs='B' and fzqdm='"
                      + rs.getString("FZqdm") + "' and fjyxwh=(select max(fjyxwh) from " + pub.yssSetPrefix(set)
                      + "HzJkHz where fdate=" + dbl.sqlDate(base.getDate()) + " and fzqdm='" + rs.getString("Fzqdm")
                      + "' and fbs='B' and fzqbz ='ZQ') and fdate=" + dbl.sqlDate(base.getDate()) + " and FJyFs ='" + rs.getString("FJyFs") + "' and fzqbz ='ZQ'";
                st.addBatch(strSql);
             }
             if (dSje != 0) { //更新回报库中国债利息--原则：更新最大席位号的利息且此席位要有交易
                strSql = "update " + pub.yssSetPrefix(set) + "HzJkHz set fgzlx=fgzlx+(" + dSje + ") where fbs='S' and fzqdm='"
                      + rs.getString("FZqdm") + "' and fjyxwh=(select max(fjyxwh) from " + pub.yssSetPrefix(set)
                      + "HzJkHz where fdate=" + dbl.sqlDate(base.getDate()) + " and fzqdm='" + rs.getString("Fzqdm")
                      + "' and fbs='S' and fzqbz ='ZQ') and fdate=" + dbl.sqlDate(base.getDate()) + " and FJyFs ='" + rs.getString("FJyFs") + "' and fzqbz ='ZQ'";
                st.addBatch(strSql);
             }
          }
          rs.getStatement().close();
          st.executeBatch();
          con.commit();
          bTrans = false;
       }
       catch (BatchUpdateException ex) {
          throw new YssException("调整深圳国债利息出错！", ex);
       }
       catch (Exception ex) {
          throw new YssException("调整深圳国债利息出错！", ex);
       }
       finally {
          dbl.closeResultSetFinal(rs, rst);
          dbl.closeStatementFinal(st);
          dbl.endTransFinal(con, bTrans);
       }
        }
     */
    /**
     * 席位佣金、风险金统计
     * @param set String    套帐号
     * @throws YssException
     */
    /*
        public void XwYj_Cal(String set) throws YssException {
       Connection con = dbl.loadConnection();
       Statement st = null;
       ResultSet rs = null, rst = null;
       boolean bTrans = false; //bFxj = yw.varGetValue(pub.getCurrentSet() + "不计提风险金", "0").equalsIgnoreCase("1");
       try {
          st = con.createStatement();
          con.setAutoCommit(false);
          bTrans = true;
          String sXwDm = null;
          double dHg = 0, dFxj = 0, dFxjFl = 0;
          String strSql = "delete from " + pub.yssSetPrefix(set) + "HzXwTjb where fdate=" + dbl.sqlDate(base.getDate());
          st.addBatch(strSql);

          //在HzJkQs数据表的所有债券的Fbje都是为净价的.
          //如果case when fdate>=" + dbl.sqlDate("2002-03-25") + " and fywbz='GZXQ' then"
          //               + " fbje+fbgzlx else fbje end) as bje,sum(case when fdate>=" + dbl.sqlDate("2002-03-25") + " and fywbz='GZXQ' then"
          //               + " fsje+fsgzlx else fsje end) as sje来判断的话,插入到HzXwTjb中的其他债券(企业债/可转债)都是为净价的.
          //通过席位交易量统计表统计出来的上海其他债券的交易量只能统计为净价.fazmm20060710

          strSql =
     "Select fdate,fszsh,fjyxwh,fzqbz,fywbz,sum(fbje+fbgzlx) as bje,sum(fsje+fsgzlx) as sje,sum(fbsl) as bsl,sum(fssl) as ssl,sum(fbyj) as byj,sum(fsyj) as syj,sum(fbyhs)"
                + " as byhs,sum(fsyhs) as syhs from " + pub.yssSetPrefix(set) + "HzJkQS  where FDate=" + dbl.sqlDate(base.getDate())
                + " and (fzqbz in('GP','ZQ','JJ','QZ') or fywbz in('MRHG','MCHG','MRHG_QY','MCHG_QY')) and (fszsh='H' or fszsh='G')group by fdate,fszsh"
                + ",fjyxwh,fzqbz,fywbz order by fdate,fjyxwh";
          rs = dbl.openResultSet(strSql); //上海:买入卖出股票、债券、回购,基金
          while (rs.next()) {
             if ( (calc.getAssetType(set) == 1 || calc.getAssetType(set) == 2 || calc.getAssetType(set) == 5) && calc.getFundType(set) == 0) //券商数据来源模式
                sXwDm = "0";
             else //交易所数据来源模式
                sXwDm = calc.getXwDm(set, rs.getString("FJyxwh"));
             if (sXwDm.length() == 0)throw new YssException("套账【" + calc.getFundName(set) + "】没有找到对应的席位【" + rs.getString("FJyxwh") + "】，请检查席位设定！");
             if (rs.getString("FZqbz").equalsIgnoreCase("HG")) {
                dFxj = 0;
                dHg = YssFun.roundIt(YssD.add(rs.getDouble("Bje"), rs.getDouble("Sje")), 2);
     rst = dbl.openResultSet("Select fjyxwh,sum(fbje) as bje,sum(fsje) as sje,fzqdm from " + pub.yssSetPrefix(set) + "HzJkQS  where FDate=" + dbl.sqlDate(base.getDate()) +
                                        " and fywbz in('MRHG','MCHG','MRHG_QY','MCHG_QY') and fszsh='H' and fywbz = '" + rs.getString("fywbz") +
                                        "' group by fjyxwh,fzqdm order by fjyxwh");
                while (rst.next()) {
                   dFxjFl = calc.getJyLv(set, "HG" + rst.getString("fzqdm"), "", rst.getString("FJyxwh"), "H", "FXJ");
                   dFxj = YssFun.roundIt(YssD.add(YssD.mul(YssD.add(rst.getDouble("Bje"), rst.getDouble("Sje")), dFxjFl), dFxj), 2);
                }
                rst.getStatement().close();
             }
             else {
                dHg = 0;
                dFxj = calc.getJyLv(set, rs.getString("fzqbz").equalsIgnoreCase("ZQ") ? rs.getString("fywbz") : rs.getString("fzqbz"), "", rs.getString("FJyxwh"), "H", "FXJ");
                dFxj = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Bje"), rs.getDouble("Sje")), dFxj), 2);
             }

             strSql = "insert into " + pub.yssSetPrefix(set) + "HzXwTjb(fdate,fqsdm,fqsxw,fzqbz,fywbz,fszsh,fbsl,fssl,fbje,fsje,fbyj"
                   + ",fsyj,fbyhs,fsyhs,fhgje,ffxj) values(" + dbl.sqlDate(base.getDate()) + ",'" + sXwDm + "','" + rs.getString("fjyxwh")
                   + "','" + rs.getString("fzqbz") + "','" + rs.getString("fywbz") + "','" + rs.getString("fszsh") + "'," + rs.getInt("bsl") + "," + rs.getInt("ssl")
                   + "," + rs.getDouble("bje") + "," + rs.getDouble("sje") + "," + rs.getDouble("byj") + "," + rs.getDouble("syj")
                   + "," + rs.getDouble("byhs") + "," + rs.getDouble("syhs") + "," + dHg + "," + dFxj + ")";
             st.addBatch(strSql);
          }
          rs.getStatement().close();

          strSql = "select fdate,fjyxwh,fzqbz,fywbz,sum(case when fdate>=" + dbl.sqlDate("2002-03-25") + " and fywbz='GZXQ' then "
                + "fbje+fbgzlx else fbje end) as bje,sum(case when fdate>=" + dbl.sqlDate("2002-03-25") + " and fywbz='GZXQ' then"
                + " fsje+fsgzlx else fsje end) as sje,sum(fbsl) as bsl,sum(fssl) as ssl,sum(fbyj) as byj,sum(fsyj) as syj,sum(fbyhs"
                + ") as byhs,sum(fsyhs) as syhs from (select fdate,fjyxwh,fzqbz,fywbz,fbs,sum(case fbs when 'B' then fcjje else"
                + " 0 end) as fbje,sum(case fbs when 'S' then fcjje else 0 end) as fsje,sum(case fbs when 'B' then fcjsl else 0 end"
                + ") as fbsl,sum(case fbs when 'S' then fcjsl else 0 end) as fssl,sum(case fbs when 'B' then fgzlx else 0 end) "
                + "as fbgzlx,sum(case fbs when 'S' then fgzlx else 0 end) as fsgzlx,case fbs when 'B' then sum(fyj) else 0 end as"
                + " fbyj,case fbs when 'S' then sum(fyj) else 0 end as fsyj,case fbs when 'B' then sum(fyhs) else 0 end as fbyhs,"
                + "case fbs when 'S' then sum(fyhs) else 0 end as fsyhs from " + pub.yssSetPrefix(set) + "HzJkHz where fdate="
                + dbl.sqlDate(base.getDate()) + " and (fzqbz in('GP','ZQ','JJ') or fywbz in('MRHG','MCHG','MRHG_QY','MCHG_QY')) and"
                + " fszsh='S' group by fdate,fjyxwh,fzqbz,fywbz,fbs) a group by fdate,fjyxwh,fzqbz,fywbz order by fjyxwh";
          rs = dbl.openResultSet(strSql);
          while (rs.next()) {
             if ( (calc.getAssetType(set) == 1 || calc.getAssetType(set) == 2 || calc.getAssetType(set) == 5) && calc.getFundType(set) == 0) //券商数据来源模式
                sXwDm = "0";
             else //交易所数据来源模式
                sXwDm = calc.getXwDm(set, rs.getString("FJyxwh"));
             if (sXwDm.length() == 0) sXwDm = "0";
             if (rs.getString("FZqbz").equalsIgnoreCase("GP")) {
                dFxj = calc.getJyLv(set, "GP", "", rs.getString("FJyxwh"), "S", "FXJ");
                strSql = "select fjyxwh,sum(fcjje) as sje from " + pub.yssSetPrefix(set) + "HzJkHz where fdate="
                      + dbl.sqlDate(base.getDate()) + " and " + dbl.sqlLeft("Fzqdm", 2) + "='60' and fbs='S' and fjyxwh='"
                      + rs.getString("FJyxwh") + "' and fzqbz='GP' and fszsh='S' group by fjyxwh";
                rst = dbl.openResultSet(strSql);
                if (rst.next())
                   dFxj = YssD.add(YssFun.roundIt(YssD.mul(rst.getDouble("Sje"), dFxj), 2), YssFun.roundIt(YssD.mul(YssD.sub(
                         YssD.add(rs.getDouble("Bje"), rs.getDouble("Sje")), rst.getDouble("Sje")), dFxj), 2));
                else
                   dFxj = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Bje"), rs.getDouble("Sje")), dFxj), 2);
                rst.getStatement().close();
                dHg = 0;
             }
             else if (rs.getString("FZqbz").equalsIgnoreCase("HG")) {
                dFxj = 0;
                dHg = YssFun.roundIt(YssD.add(rs.getDouble("Bje"), rs.getDouble("Sje")), 2);
                rst = dbl.openResultSet("select fjyxwh,sum(fbje) as bje,sum(fsje) as sje,fzqdm from (select fjyxwh,sum(case fbs when 'B' then fcjje else 0 end) as fbje,sum(case fbs when 'S' then fcjje else 0 end) as fsje,fzqdm from " +
                                        pub.yssSetPrefix(set) + "HzJkHz where fdate=" + dbl.sqlDate(base.getDate()) + " and  fywbz in('MRHG','MCHG','MRHG_QY','MCHG_QY') and" +
                                        " fszsh='S' and fywbz = '" + rs.getString("fywbz") + "' group by fjyxwh,fzqdm) a group by fjyxwh,fzqdm order by fjyxwh");
                while (rst.next()) {
                   dFxjFl = calc.getJyLv(set, "HG" + rst.getString("fzqdm"), "", rst.getString("FJyxwh"), "H", "FXJ");
                   dFxj = YssFun.roundIt(YssD.add(YssD.mul(YssD.add(rst.getDouble("Bje"), rst.getDouble("Sje")), dFxjFl), dFxj), 2);
                }
                rst.getStatement().close();
             }
             else if (rs.getString("FZqbz").equalsIgnoreCase("ZQ")) {
                dHg = 0;
                dFxj = calc.getJyLv(set, rs.getString("fywbz"), "", rs.getString("FJyxwh"), "S", "FXJ");
                dFxj = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Bje"), rs.getDouble("Sje")), dFxj), 2);
             }
             else {
                dHg = 0;
                dFxj = calc.getJyLv(set, rs.getString("fzqbz"), "", rs.getString("FJyxwh"), "S", "FXJ");
                dFxj = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Bje"), rs.getDouble("Sje")), dFxj), 2);
             }

             strSql = "insert into " + pub.yssSetPrefix(set) + "HzXwTjb(fdate,fqsdm,fqsxw,fzqbz,fywbz,fszsh,fbsl,fssl,fbje,fsje,fbyj"
                   + ",fsyj,fbyhs,fsyhs,fhgje,ffxj) values(" + dbl.sqlDate(base.getDate()) + ",'" + sXwDm + "','" + rs.getString("fjyxwh")
                   + "','" + rs.getString("fzqbz") + "','" + rs.getString("fywbz") + "','S'," + rs.getInt("bsl") + "," + rs.getInt("ssl")
                   + "," + rs.getDouble("bje") + "," + rs.getDouble("sje") + "," + rs.getDouble("byj") + "," + rs.getDouble("syj")
                   + "," + rs.getDouble("byhs") + "," + rs.getDouble("syhs") + "," + dHg + "," + dFxj + ")";
             st.addBatch(strSql);
          }
          rs.getStatement().close();
          st.executeBatch();
          con.commit();
          bTrans = false;
       }
       catch (BatchUpdateException ex) {
          throw new YssException("席位佣金、风险金统计出错！", ex);
       }
       catch (Exception ex) {
          throw new YssException("席位佣金、风险金统计出错！", ex);
       }
       finally {
          dbl.closeResultSetFinal(rs, rst);
          dbl.closeStatementFinal(st);
          dbl.endTransFinal(con, bTrans);
       }
        }
     */
    /**
     * 深圳席位风险金调整
     * @param set String     套帐号
     * @throws YssException
     */
    /*
        public void Adjust_SzFxj(String set) throws YssException {
       //调整深圳风险金:
       //深圳股票风险金计算方法:交易所是按照统计库中的合计金额来计算的，即＝新股(上海)的风险金＋非新股的风险金；
       //而程序中是按席位合计交易金额后计算的(group by fjyxwh),因此若当日有一个以上的席位进行了交易,则风险金的计算可能就会有尾差；
       //这时，就需要对某个席位的风险金进行调整，以保证按席位合计的风险金与实际的风险金相符。
       //调整的原则是：调整最大席位。
       //即如果有n个席位进行了交易，则先算出小席位(共n-1个)的风险金，再用总的风险金减去小席位的风险金(n-1)得到大席位的风险金。
       //在下面这段程序中采取的是：先检查两种方法计算出来的风险金是否有差额，若有，则更新最大席位号的风险金
       Connection con = dbl.loadConnection();
       Statement st = null;
       ResultSet rs = null;
       boolean bTrans = false;
       try {
          double dFxj = 0, dFxjFv = 0;
          st = con.createStatement();
          con.setAutoCommit(false);
          bTrans = true;
          String strSql = "select fjyxwh,sum(fsje) as sje from " + pub.yssSetPrefix(set) + "HzJkQs where fszsh='S' and fzqbz='GP' and "
                + dbl.sqlLeft("fzqdm", 2) + "='60' and fdate=" + dbl.sqlDate(base.getDate()) + " group by fjyxwh";
          rs = dbl.openResultSet(strSql); //新股(上海)的风险金
          while (rs.next()) {
             dFxjFv = calc.getJyLv(set, "GP", "", rs.getString("fjyxwh"), "S", "FXJ");
             dFxj = YssFun.roundIt(YssD.mul(rs.getDouble("sje"), dFxj), 2);
          }
          rs.getStatement().close();

          strSql = "select fjyxwh,sum(fbje) as bje,sum(fsje) as sje from " + pub.yssSetPrefix(set) + "HzJkQs where fdate=" + dbl.sqlDate(base.getDate()) + " and " +
                dbl.sqlLeft("fzqdm", 2) + "<>'60' and fszsh='S' and fzqbz='GP' group by fjyxwh";
          rs = dbl.openResultSet(strSql); //非新股的风险金
          while (rs.next()) {
             dFxjFv = calc.getJyLv(set, "GP", "", rs.getString("fjyxwh"), "S", "FXJ");
             dFxj = YssD.add(dFxj, YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Bje"), rs.getDouble("Sje")), dFxjFv), 2));
          }
          rs.getStatement().close();

          strSql = "select sum(ffxj) as fxj from " + pub.yssSetPrefix(set) + "HzXwTjb where fzqbz='GP' and fszsh='S' and fdate="
                + dbl.sqlDate(base.getDate());
          rs = dbl.openResultSet(strSql); //按席位交易金额计算的风险金合计
          if (rs.next())
             dFxj = YssFun.roundIt(YssD.sub(dFxj, rs.getDouble("fxj")), 2);
          rs.getStatement().close();
          if (dFxj != 0) {
             strSql = "update " + pub.yssSetPrefix(set) + "HzXwTjb set ffxj=ffxj+(" + dFxj + ") where fzqbz='GP' and fszsh='S' and "
                   + "fQsXw=(select max(fQsXw) from " + pub.yssSetPrefix(set) + "HzXwTjb where fszsh='S' and fzqbz='GP' and fdate="
                   + dbl.sqlDate(base.getDate()) + ") and fdate=" + dbl.sqlDate(base.getDate());
             st.addBatch(strSql);
          }
          dFxj = 0;
          strSql = "select fjyxwh,sum(case when fdate>=" + dbl.sqlDate("2002-03-25") + " and fywbz='GZXQ' then fbje+fbgzlx else fbje end) as bje,"
                + "sum(case when fdate>=" + dbl.sqlDate("2002-03-25") + " and fywbz='GZXQ' then fsje+fsgzlx else fsje end) as sje from " + pub.yssSetPrefix(set) + "HzJkQs "
                + "where fszsh='S' and fzqbz ='ZQ' and fywbz='GZXQ' and fdate=" + dbl.sqlDate(base.getDate()) + "group by fjyxwh";
          rs = dbl.openResultSet(strSql); //国债
          while (rs.next()) {
             dFxjFv = calc.getJyLv(set, "GZXQ", "", rs.getString("fjyxwh"), "S", "FXJ");
             dFxj = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Bje"), rs.getDouble("Sje")), dFxjFv), 2);
          }
          rs.getStatement().close();

          strSql = "select sum(ffxj) as fxj from " + pub.yssSetPrefix(set) + "HzXwTjb where fzqbz='ZQ' and fszsh='S' and fdate="
                + dbl.sqlDate(base.getDate());
          rs = dbl.openResultSet(strSql);
          if (rs.next())
             dFxj = YssFun.roundIt(YssD.sub(dFxj, rs.getDouble("fxj")), 2);
          rs.getStatement().close();
          if (dFxj != 0) {
             strSql = "update " + pub.yssSetPrefix(set) + "HzXwTjb set ffxj=ffxj+(" + dFxj + ") where fzqbz='ZQ' and fszsh='S' and "
                   + "fQsXw=(select max(fQsXw) from " + pub.yssSetPrefix(set) + "HzXwTjb where fszsh='S' and fzqbz='ZQ' and fdate="
                   + dbl.sqlDate(base.getDate()) + ") and fdate=" + dbl.sqlDate(base.getDate());
             st.addBatch(strSql);
          }
          dFxj = 0;

          strSql = "select fjyxwh,fzqdm,sum(fbje) as bje,sum(fsje) as sje from " + pub.yssSetPrefix(set) + "HzJkQs where fszsh='S' and fdate="
                + dbl.sqlDate(base.getDate()) + " and fywbz in('MRHG','MCHG') group by fjyxwh,fzqdm";
          rs = dbl.openResultSet(strSql); //回购
          while (rs.next()) {
             dFxjFv = calc.getJyLv(set, "HG" + rs.getString("fzqdm"), "", rs.getString("fjyxwh"), "S", "FXJ");
             dFxj = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Bje"), rs.getDouble("Sje")), dFxjFv), 2);
          }
          rs.getStatement().close();
          strSql = "select sum(ffxj) as fxj from " + pub.yssSetPrefix(set) + "HzXwTjb where fzqbz='HG' and fszsh='S' and fdate="
                + dbl.sqlDate(base.getDate());
          rs = dbl.openResultSet(strSql);
          if (rs.next())
             dFxj = YssFun.roundIt(YssD.sub(dFxj, rs.getDouble("fxj")), 2);
          rs.getStatement().close();
          if (dFxj != 0) {
             strSql = "update " + pub.yssSetPrefix(set) + "HzXwTjb set ffxj=ffxj+(" + dFxj + ") where fzqbz='HG' and fszsh='S' and "
                   + "fQsXw=(select max(fQsXw) from " + pub.yssSetPrefix(set) + "HzXwTjb where fszsh='S' and fzqbz='HG' and fdate="
                   + dbl.sqlDate(base.getDate()) + ") and fdate=" + dbl.sqlDate(base.getDate());
             st.addBatch(strSql);
          }
          st.executeBatch();
          con.commit();
          bTrans = false;
       }
       catch (BatchUpdateException ex) {
          throw new YssException("调整深圳席位风险金出错！", ex);
       }
       catch (Exception ex) {
          throw new YssException("调整深圳席位风险金出错！", ex);
       }
       finally {
          dbl.closeResultSetFinal(rs);
          dbl.closeStatementFinal(st);
          dbl.endTransFinal(con, bTrans);
       }
        }
     */
    /**
     * 更新两地新股配售中签数据
     * @param set String     套帐号
     * @throws YssException
     */
    /*
        private void Update_XgPs(String set) throws YssException {
       //更新HzjkQs中两地新股配售中签数据、增加新股手续费返还和配股手续费返还
       //新股T日配售、T＋1初次配售中签(Ftiq)、T＋3确认配售中签(中签凭证)、T＋4手续费返还(手续费返还凭证)
       Connection con = dbl.loadConnection();
       Statement st = null;
       boolean bTrans = false;
       try {
      java.util.Date date = ds.Get_WorkDay(base.getDate(), 2), dateSx;
      //增加新股手续费返还(T＋4手续费返还)
      boolean bSxDz = yw.varGetValue(set + "新股手续费返回提前计入证券清算款", "0").equalsIgnoreCase("0");
      st = con.createStatement();
      con.setAutoCommit(false);
      bTrans = true;
      String strSql = "update " + pub.yssSetPrefix(set) + "HzJkQs set fdate=" + dbl.sqlDate(date) + " where findate="
            + dbl.sqlDate(base.getDate()) + " and fzqbz='XG' and (fywbz like '%SHZQ' or fywbz like '%SZZQ')";
      st.addBatch(strSql);

      if (bSxDz) //实际返还日才进账
         dateSx = ds.Get_WorkDay(base.getDate(), 3);
      else //新股手续费实际返还是T＋4，把数据做到T＋3，第二日清算(先应收，第二日收到)
         dateSx = ds.Get_WorkDay(base.getDate(), 2);
         //strSql = "delete from " + pub.yssSetPrefix(set) + "HzJkQs where fzqbz='XG' and fywbz='XGSX' and findate=" + dbl.sqlDate(base.getDate());
         //以前在删除新股申购中签和市值配售的手续费返还是分开删除的。现在修改为在这边一次性删除。fazmm20060525
         //从2006－6－19号起没有新股和新债的手续费返回。
      if (base.getDate().before(YssFun.toDate("2006-6-1"))) {
         strSql = "delete from " + pub.yssSetPrefix(set) + "HzJkQs where findate=" + dbl.sqlDate(base.getDate()) + " and fzqbz='XG' and fywbz='XGSX'";
         st.addBatch(strSql);
         strSql = "insert into " + pub.yssSetPrefix(set) + "HzJkQs " + base.strTableQs + " select " + dbl.sqlDate(dateSx) + ",findate,fjyfs,zqdm,fzqdm,fszsh,fjyxwh,0,"
               + "case fszsh when 'H' then round(fbje*" + base.getXgSXF_SH() + ",2)-round(fbje*" + base.getXgJSF_SH() + ",2) else "
               + "round(fbje*" + base.getXgSXF_SZ() + ",2) end,0,0,fbyj,fsyj,fbjsf,fsjsf,fbyhs,fsyhs,fbzgf,fszgf,fbghf,fsghf,fbfxj,fsfxj,fbqtf,fsqtf,fbgzlx,"
               + "fsgzlx,fhggain,0,case fszsh when 'H' then round(fbje*" + base.getXgSXF_SH() + ",2)-round(fbje*" + base.getXgJSF_SH()
               + ",2) else round(fbje*" + base.getXgSXF_SZ() + ",2) end,fzqbz,'XGSX',fqsbz from " + pub.yssSetPrefix(set)
               + "HzJkQs where fdate=" + dbl.sqlDate(date) + " and fzqbz='XG' and (fywbz like '%SHZQ' or fywbz like '%SZZQ')";
         st.addBatch(strSql);
      }
      if (bSxDz)
         dateSx = ds.Get_WorkDay(base.getDate(), 1);
      else //新股手续费实际返还是T＋1，把数据做到T日，第二日清算
         dateSx = base.getDate();
      st.executeBatch();
      //strSql = "delete from " + pub.yssSetPrefix(set) + "HzJkQs where fdate=" + dbl.sqlDate(dateSx) + " and findate="
      //      + dbl.sqlDate(base.getDate()) + " and fzqbz='XG' and fywbz='XGSX'";
      //st.addBatch(strSql); //注意删除数据的方式，不要把市值配售返回的手续费删除了
      //网上申购新股中签时，手续费返回 (新股申购中签、增发中签、可转债中签)
      //增加新股手续费返还(T＋1手续费返还)
      //从20060601开始的新股申购中签的没有手续费返还了.fazmm20060613
      //cwy 只有新股增发、配售增发、配股等有手续费返还，其他没有
      /** 新股没有手续费返还，刘永宜，2007-9-6
        strSql = "insert into " + pub.yssSetPrefix(set) + "HzJkQs " + base.strTableQs + " select " + dbl.sqlDate(dateSx) + ",findate,fjyfs,zqdm,fzqdm,fszsh,fjyxwh,0,"
              + "case fszsh when 'H' then round(fbje*" + base.getXgSXF_SH() + ",2)-round(fbje*" + base.getXgJSF_SH() + ",2) else "
              + "round(fbje*" + base.getXgSXF_SZ() + ",2) end,0,0,fbyj,fsyj,fbjsf,fsjsf,fbyhs,fsyhs,fbzgf,fszgf,fbghf,fsghf,fbfxj,fsfxj,fbqtf,fsqtf,fbgzlx,"
              + "fsgzlx,fhggain,0,case fszsh when 'H' then round(fbje*" + base.getXgSXF_SH() + ",2)-round(fbje*" + base.getXgJSF_SH()
              + ",2) else round(fbje*" + base.getXgSXF_SZ() + ",2) end,fzqbz,'XGSX',fqsbz from " + pub.yssSetPrefix(set)
              + "HzJkQs where fdate=" + dbl.sqlDate(base.getDate()) + "";
        if (base.getDate().before(YssFun.toDate("2006-06-01"))) {
       strSql = strSql + " and fzqbz='XG' and fywbz in('XGZQ','XGZF','PSZFZQ')";
        }
        else {
       strSql = strSql + " and fzqbz='XG' and fywbz in('XGZF','PSZFZQ','ZSPSZFZQ')";
        }
        st.addBatch(strSql);

        strSql = "delete from " + pub.yssSetPrefix(set) + "HzJkQs where fzqbz='QY' and fywbz='PGSX' and findate=" + dbl.sqlDate(base.getDate());
        st.addBatch(strSql);
        strSql = "insert into " + pub.yssSetPrefix(set) + "HzJkQs " + base.strTableQs + " select " + dbl.sqlDate(dateSx) + ",findate,fjyfs,zqdm,fzqdm,fszsh,fjyxwh,0,"
              + "case fszsh when 'H' then round(fbje*" + base.getPgSXF_SH() + ",2)-round(fbje*" + base.getPgJSF_SH() + ",2) else "
              + "round(fbje*" + base.getPgSXF_SZ() + ",2) end,0,0,fbyj,fsyj,fbjsf,fsjsf,fbyhs,fsyhs,fbzgf,fszgf,fbghf,fsghf,fbfxj,fsfxj,fbqtf,fsqtf,fbgzlx,"
              + "fsgzlx,fhggain,0,case fszsh when 'H' then round(fbje*" + base.getPgSXF_SH() + ",2)-round(fbje*" + base.getPgJSF_SH()
              + ",2) else round(fbje*" + base.getPgSXF_SZ() + ",2) end,fzqbz,'PGSX',fqsbz from " + pub.yssSetPrefix(set)
              + "HzJkQs where fdate=" + dbl.sqlDate(base.getDate()) + " and fzqbz='QY' and fywbz like '%PG'";
        st.addBatch(strSql);
        //增加配债/可转债中签手续费返还(手续费实际返还T＋1)
        if (base.getDate().before(YssFun.toDate("2006-6-1"))) {
           strSql = "delete from " + pub.yssSetPrefix(set) + "HzJkQs where fzqbz='XZ' and fywbz='KZZSX' and findate=" + dbl.sqlDate(base.getDate());
           st.addBatch(strSql);
           strSql = "insert into " + pub.yssSetPrefix(set) + "HzJkQs " + base.strTableQs + " select " + dbl.sqlDate(dateSx) + ",findate,fjyfs,zqdm,fzqdm,fszsh,fjyxwh,0,"
                 + "case fszsh when 'H' then round(fbje*" + base.getPzSXF_SH() + ",2)-round(fbje*" + base.getPzJSF_SH() + ",2) else "
                 + "round(fbje*" + base.getPzSXF_SZ() + ",2) end,0,0,fbyj,fsyj,fbjsf,fsjsf,fbyhs,fsyhs,fbzgf,fszgf,fbghf,fsghf,fbfxj,fsfxj,fbqtf,fsqtf,fbgzlx,"
                 + "fsgzlx,fhggain,0,case fszsh when 'H' then round(fbje*" + base.getPzSXF_SH() + ",2)-round(fbje*" + base.getPzJSF_SH()
                 + ",2) else round(fbje*" + base.getPzSXF_SZ() + ",2) end,'XZ','KZZSX',fqsbz from " + pub.yssSetPrefix(set)
       + "HzJkQs where fdate=" + dbl.sqlDate(base.getDate()) + " and fzqbz='XZ'";
           strSql = strSql + "and fywbz in ('KZZXZ','KZZZQ')";
           st.addBatch(strSql);
        }

        st.executeBatch();
        con.commit();
        bTrans = false;
         }
         catch (BatchUpdateException ex) {
        throw new YssException("更新两地新股配售中签数据出错！", ex);
         }
         catch (Exception ex) {
        throw new YssException("更新两地新股配售中签数据出错！", ex);
         }
         finally {
        dbl.closeStatementFinal(st);
        dbl.endTransFinal(con, bTrans);
         }
      }
      */
     /**
      * 更新两地派息、送股数据
      * @param set String    套帐号
      * @throws YssException
      */
     /*
        private void Update_PxSg(String set) throws YssException {
           Connection con = dbl.loadConnection();
           Statement st = null;
           boolean bTrans = false;
           try {
      StringBuffer bufName = new StringBuffer(), bufValue = new StringBuffer();
              bufName.append(set).append("新股、新债业务T日入账").append("\t");
              bufName.append(set).append("权益派息日前一天（T-1日）红利入账");
              bufValue.append("0\t0");
              String[] strValue = yw.varGetValueMore(bufName.toString(), bufValue.toString()).split("\t");
              boolean blnTrrz = strValue[0].equalsIgnoreCase("1");
              boolean bQyPxrz = strValue[1].equalsIgnoreCase("1");

              java.util.Date date = ds.Get_WorkDay(base.getDate(), 1);
              st = con.createStatement();
              con.setAutoCommit(false);
              bTrans = true;
              //新股/新债流通业务日期调整
              String strSql = "update " + pub.yssSetPrefix(set) + "HzJkQs set fdate=" + dbl.sqlDate(date) + " where findate="
      + dbl.sqlDate(base.getDate()) + " and fzqbz in ('XGLT','XZLT')";
              st.addBatch(strSql);

              strSql = "update " + pub.yssSetPrefix(set) + "HzJkQs set fdate=" + dbl.sqlDate(date) + " where findate="
                    + dbl.sqlDate(base.getDate()) + " and fzqbz='QY' and fywbz in('SG','ZSSG','ZBSG','PX','PXDZ','ZQPX','QZ','XJDJ','XJDJDZ')";
              st.addBatch(strSql); //更新HzjkQs中派息、送股的日期为第二日

              if (!bQyPxrz) {
                 strSql = "update " + pub.yssSetPrefix(set) + "HzJkQs set fdate=" + dbl.sqlDate(date) + " where findate="
      + dbl.sqlDate(base.getDate()) + " and fzqbz='QY' and fywbz in('PXDZ','XJDJDZ')";
                 st.addBatch(strSql); //更新HzjkQs中派息到帐的日期为第二日
              }
              //国信证券的理财产品要求申购、返款都要当日处理，即拆成两张凭证来处理。ｃｈｅｒｒｙ060808

              if (!blnTrrz) {
                 //中信托管的瑞盈集合资金信托计划为T+0日
                 strSql = "update " + pub.yssSetPrefix(set) + "HzJkQs set fdate=" + dbl.sqlDate(date) + " where findate="
                       + dbl.sqlDate(base.getDate()) + " and (fzqbz='XG' or fzqbz='XZ') and fywbz in('XGSG','KZZSG','XGFK','KZZFK','QYZQSG','QYZQFK')";
                 st.addBatch(strSql); //更新HzjkQs中新股、可转债申购、返款的日期为第二日
              }
              st.executeBatch();
              con.commit();
              bTrans = false;
           }
           catch (BatchUpdateException ex) {
              throw new YssException("更新两地派息、送股数据出错！", ex);
           }
           catch (Exception ex) {
              throw new YssException("更新两地派息、送股数据出错！", ex);
           }
           finally {
              dbl.closeStatementFinal(st);
              dbl.endTransFinal(con, bTrans);
           }
        }
      */

     /**
      * 指数基金统计库股票类型调整
      * @param set String    套帐号
      * @throws YssException
      */
     /*
        private void Adjust_ZSGP(String set) throws YssException {
           Connection con = dbl.loadConnection();
           Statement st = null;
           ResultSet rs = null;
           boolean bTrans = false;
           try {
              st = con.createStatement();
              con.setAutoCommit(false);
              bTrans = true;
              String strSql = "select distinct fywbz from " + pub.yssSetPrefix(set) + "HzJkQs where fzqbz='GP' and fszsh='S' and fdate="
                    + dbl.sqlDate(base.getDate()), sXw = null, strZqdm = "";
              rs = dbl.openResultSet(strSql); //只有深圳股票才涉及到调整指数、非指数
              if (rs.next())
                 sXw = rs.getString("fywbz");
              rs.getStatement().close();
              if (sXw != null) {
                 strSql = "select sum(case when fbs='B' then fcjje else 0 end) fbje,sum(case when fbs='S' then fcjje else 0 end) fsje,"
                       + "sum(case when fbs='B' then fcjsl else 0 end) fbsl,sum(case when fbs='S' then fcjsl else 0 end) fssl,"
                       + "sum(case when fbs='B' then fyhs else 0 end) fbyhs,sum(case when fbs='S' then fyhs else 0 end) fsyhs,"
                       + "sum(case when fbs='B' then fjsf else 0 end) fbjsf,sum(case when fbs='S' then fjsf else 0 end) fsjsf,"
                       + "sum(case when fbs='B' then fghf else 0 end) fbghf,sum(case when fbs='S' then fghf else 0 end) fsghf,"
                       + "sum(case when fbs='B' then fzgf else 0 end) fbzgf,sum(case when fbs='S' then fzgf else 0 end) fszgf,"
                       + "sum(case when fbs='B' then fyj else 0 end) fbyj,sum(case when fbs='S' then fyj else 0 end) fsyj,"
                       + "fzqdm,fywbz,fszsh,fjyfs from " + pub.yssSetPrefix(set) + "HzJkHz where fdate=" + dbl.sqlDate(base.getDate())
                       + " and fzqbz='GP' and fszsh='S' group by fzqdm,fywbz,fszsh,fjyfs order by fzqdm,fywbz desc";
                 rs = dbl.openResultSet(strSql);
                 while (rs.next()) {
                    if (strZqdm.equalsIgnoreCase(rs.getString("fzqdm"))) {
                       strSql = "update " + pub.yssSetPrefix(set) + "HzJkQs set fbje=fbje-" + rs.getDouble("FBje") + ",fsje=fsje-"
                             + rs.getDouble("FSje") + ",fbsl=fbsl-" + rs.getDouble("FBsl") + ",fssl=fssl-" + rs.getDouble("FSsl")
                             + ",fbyj=fbyj-" + rs.getDouble("fbyj") + ",fsyj=fsyj-" + rs.getDouble("fsyj") + ",fbjsf=fbjsf-"
                             + rs.getDouble("FBJsf") + ",fsjsf=fsjsf-" + rs.getDouble("FSJsf") + ",fbyhs=fbyhs-" + rs.getDouble("FByhs")
                             + ",fsyhs=fsyhs-" + rs.getDouble("FSyhs") + ",fbzgf=fbzgf-" + rs.getDouble("FBzgf") + ",fszgf=fszgf-"
                             + rs.getDouble("FSzgf") + ",fbghf=fbghf-" + rs.getDouble("FBghf") + ",FSghf=FSghf-" + rs.getDouble("FSghf")
                             + ",fbsfje=fbsfje-" + (rs.getDouble("FBje") + rs.getDouble("FByhs") + rs.getDouble("FBJsf")
      + rs.getDouble("FBghf") + rs.getDouble("FBzgf")) + ",fsssje=fsssje-"
                             + (rs.getDouble("FSje") - (rs.getDouble("FSyhs") + rs.getDouble("FSJsf") + rs.getDouble("FSghf")
                             + rs.getDouble("FSzgf"))) + ",fywbz='" + (rs.getString("FYwbz").equalsIgnoreCase("PT") ? "ZS" :
                             rs.getString("FYwbz")) + "' where fzqdm='" + rs.getString("Fzqdm") + "' and fzqbz='GP' and fywbz='"
                             + rs.getString("FYwbz") + "' and fszsh='S' and fdate=" + dbl.sqlDate(base.getDate()) + " and fjyfs ='" + rs.getString("FJyfs") + "'";
                       st.addBatch(strSql);
      strSql = "insert into HzJkQs select fdate,findate,fjyfs,fzqdm,'S',fjyxwh,"
                             + rs.getDouble("FBje") + "," + rs.getDouble("FSje") + "," + rs.getDouble("FBsl") + ","
                             + rs.getDouble("FSsl") + "," + rs.getDouble("fbyj") + "," + rs.getDouble("fsyj") + ","
                             + rs.getDouble("FBJsf") + "," + rs.getDouble("FSJsf") + "," + rs.getDouble("fbyhs") + ","
                             + rs.getDouble("FSyhs") + "," + rs.getDouble("FBzgf") + "," + rs.getDouble("FSzgf") + ","
                             + rs.getDouble("FBghf") + "," + rs.getDouble("FSghf") + ",0,0,0," + (rs.getDouble("FBje")
                             + rs.getDouble("FByhs") + rs.getDouble("FBJsf") + rs.getDouble("FBghf") + rs.getDouble("FBzgf")) + ","
                             + (rs.getDouble("FSje") - (rs.getDouble("FSyhs") + rs.getDouble("FSJsf") + rs.getDouble("FSghf")
                             + rs.getDouble("FSzgf"))) + ",'GP','" + rs.getString("FYwbz") + "','N' from " + pub.yssSetPrefix(set)
                             + "HzJkQs where fzqdm='" + rs.getString("Fzqdm") + "' and fzqbz='GP' and fywbz='" + rs.getString("FYwbz")
                             + "' and fszsh='S' and fdate=" + dbl.sqlDate(base.getDate()) + " and FJyfs ='" + rs.getString("FJyFs") + "'";
                       st.addBatch(strSql);
                    }
                    else { //只有一类席位交易，不用拆分，否则，只需调整席位类型
      if (!sXw.equalsIgnoreCase(rs.getString("FYwbz"))) { //回报库交易席位不等于统计库清算席位
      strSql = "update HzJkQs set fywbz='" + rs.getString("FYwbz") + "' where fzqdm='"
                                + rs.getString("Fzqdm") + "' and fzqbz='GP' and fywbz='" + sXw + "' and fszsh='S' and fdate="
      + dbl.sqlDate(base.getDate()) + " and FJyFs ='" + rs.getString("FJyFs") + "'";
                          st.addBatch(strSql);
                       }
                    }
                    strZqdm = rs.getString("Fzqdm");
                 }
                 rs.getStatement().close();
              }
              st.executeBatch();
              con.commit();
              bTrans = false;
           }
           catch (BatchUpdateException ex) {
              throw new YssException("调整指数基金统计库股票类型出错！", ex);
           }
           catch (Exception ex) {
              throw new YssException("调整指数基金统计库股票类型出错！", ex);
           }
           finally {
              dbl.closeResultSetFinal(rs);
              dbl.closeStatementFinal(st);
              dbl.endTransFinal(con, bTrans);
           }
        }
      */
     /**
      * 自动增加证券科目
      * @param set String    套帐号
      * @throws YssException
      */
     /*
        private void AutoAdd_Code(String set) throws YssException {
           ResultSet rs = null;
           String[] sSet ;
           try {
              //Add_KmCode,get_PzKmh方法均加上年份、套账号参数，因为多套账循环引用此方法将都会引用当前资产的设置shuo20070813
              String strSql = "select fzqdm,zqdm,fzqbz,fywbz,fszsh from " + pub.yssSetPrefix(set)
                    + "HzJkQs where fzqbz in('GP','ZQ','HG','JJ','XG') and fdate=" + dbl.sqlDate(base.getDate());
              rs = dbl.openResultSet(strSql);
              //考虑到多级科目体系，原来的accLenLevel(2)已经不合适了，改为pub.accLenLevel(-1)-1；cherry
              while (rs.next()) {
                    if (YssFun.oneCase(rs.getString("fzqbz"), "GP,XG"))
                       strSql = yf.get_PzKmh(rs.getString("fzqbz"), rs.getString("fywbz"),rs.getString("fzqdm").startsWith("6") ? "H" : "S",
      "",false,pub.getCurrentYear(),YssFun.toInt(set),strPre);
                    else
                       strSql = yf.get_PzKmh(rs.getString("fzqbz"), rs.getString("fywbz"), rs.getString("fszsh"),
      "",false,pub.getCurrentYear(),YssFun.toInt(set),strPre);
      if (strSql.trim().length() > 0) //如果是深圳中签上海的股票，只需要增加上海股票投资就可,上海中签深圳新股取深圳股票投资。
                       if (rs.getString("fzqbz").equalsIgnoreCase("HG")){
                          ds.Add_KmCode(YssFun.left(strSql, pub.accLenLevel(pub.accLenLevel(-1)-1)), rs.getString("fzqdm"), "", false,
                                pub.getCurrentYear(), YssFun.toInt(set));
                          if(pub.getAssetType()==2 && sSet.length>1){
                             for(int i=0;i<sSet.length;i++){
                                ds.Add_KmCode(YssFun.left(strSql, pub.accLenLevel(pub.accLenLevel(-1)-1)),rs.getString("fzqdm"),"",
      false,pub.getCurrentYear(),YssFun.toInt(sSet[i]));
                             }
                             ds.Add_KmCode(YssFun.left(strSql, pub.accLenLevel(pub.accLenLevel(-1)-1)),rs.getString("fzqdm"),"",
      false,pub.getCurrentYear(),pub.getParentSet());
                          }
                       }
                       else{
                          //ds.Add_KmCode(YssFun.left(strSql, pub.accLenLevel(pub.accLenLevel(-1)-1)),rs.getString("fzqdm"),rs.getString("fzqbz").equalsIgnoreCase("XG")?"780" + YssFun.right(rs.getString("fzqdm"), 3):"");
                          String sGdm = "";
                          if (rs.getString("fszsh").equalsIgnoreCase("H") && rs.getString("fzqbz").equalsIgnoreCase("XG")){//上海市场 新股处理：新股，增发，老股东配售;深圳新股申购代码与市场代码相同。
      sGdm = "780" + YssFun.right(rs.getString("fzqdm"), 3);
                             if (YssFun.left(rs.getString("zqdm"), 3).equalsIgnoreCase("740") || rs.getString("zqdm").startsWith("70")) {
                                //"70"老股东配售，"740"新股申购，现在的代码为“780”,但是以“600”开头的股票新增发的新股还用“740”，JjHzXx已有记录。
                                sGdm = "";
                             }
                          }
                          ds.Add_KmCode(YssFun.left(strSql, pub.accLenLevel(pub.accLenLevel(-1)-1)),rs.getString("fzqdm"),
                                        rs.getString("fzqbz").equalsIgnoreCase("XG") ? sGdm : "","", true, pub.getCurrentYear(),
                                        YssFun.toInt(set));
                          if(pub.getAssetType()==2 && sSet.length>1){
                             for(int i=0;i<sSet.length;i++){
                                ds.Add_KmCode(YssFun.left(strSql, pub.accLenLevel(pub.accLenLevel(-1)-1)), rs.getString("fzqdm"),"",
      true,pub.getCurrentYear(),YssFun.toInt(sSet[i]));
                             }
                             ds.Add_KmCode(YssFun.left(strSql, pub.accLenLevel(pub.accLenLevel(-1)-1)), rs.getString("fzqdm"),"",true,
      pub.getCurrentYear(),pub.getParentSet());
                          }
                       }
      if (rs.getString("fzqbz").equalsIgnoreCase("ZQ")) { //增加应收债券利息科目
                       strSql = yf.get_PzKmh("应收利息", rs.getString("fywbz"), rs.getString("fszsh"),"",false,pub.getCurrentYear(),
                                             YssFun.toInt(set),strPre);
                       if (strSql.trim().length() > 0){
                          ds.Add_KmCode(YssFun.left(strSql, pub.accLenLevel(pub.accLenLevel(-1)-1)), rs.getString("fzqdm"), "", false
                                ,pub.getCurrentYear(),YssFun.toInt(set));
                       }
                       if (pub.getAssetType() == 2 && sSet.length>1) {
                          for (int i = 0; i < sSet.length; i++) {
                             ds.Add_KmCode(YssFun.left(strSql, pub.accLenLevel(pub.accLenLevel(-1)-1)), rs.getString("fzqdm"), "",
      false, pub.getCurrentYear(), YssFun.toInt(sSet[i]));
                          }
                          ds.Add_KmCode(YssFun.left(strSql, pub.accLenLevel(pub.accLenLevel(-1)-1)), rs.getString("fzqdm"), "",
      false, pub.getCurrentYear(), pub.getParentSet());
                       }
                    }
              }
           }
           catch (Exception ex) {
      throw new YssException("增加证券科目出错！\tEndDataBean.AutoAdd_Code", ex);
           }
           finally {
              dbl.closeResultSetFinal(rs);
           }
        }
      */
     /**
      * 自动更新科目名称
      * @param set String    套帐号
      * @throws YssException
      */
     /*
        private void AutoUpdate_Name(String set) throws YssException {
           Connection con = dbl.loadConnection();
           Statement st = null;
           ResultSet rs = null;
           boolean bTrans = false;
           try {
              st = con.createStatement();
              con.setAutoCommit(false);
              bTrans = true;
              String strSql;
              strSql = msd.getMaxStartDateSql("JjHzXx", "", base.getDate());

              strSql = "select " + dbl.sqlRight("facctcode", 6) +
      " as fzqdm,a.facctname,b.fzqmc from " + pub.yssTablePrefix(set) +
      "LAccount a, (" + strSql + ") b where " + dbl.sqlRight("a.facctcode", 6)
                    + "=b.fzqdm and a.facctname<>b.fzqmc and a.facctattr like '%股票投资%' and a.facctlevel>2 and a.facctdetail=1 group by " +
                    dbl.sqlRight("a.facctcode", 6) + ",a.facctname,b.fzqmc";

//         strSql = "select " + dbl.sqlRight("facctcode", 6) +
//               " as fzqdm,a.facctname,b.fzqmc from " + pub.yssTablePrefix() +
//               "LAccount a,jjhzxx b where " + dbl.sqlRight("a.facctcode", 6)
//               + "=b.fzqdm and a.facctname<>b.fzqmc and a.facctattr like '股票投资%' and a.facctlevel>2 and a.facctdetail=1 group by " +
//               dbl.sqlRight("a.facctcode", 6) + ",a.facctname,b.fzqmc";

              rs = dbl.openResultSet(strSql);
              while (rs.next()) {
                 strSql = "update " + pub.yssTablePrefix(set) + "LAccount set FAcctName='" + rs.getString("FZqmc")
      + "' where FAcctName='" + rs.getString("FAcctName") + "'";
                 st.addBatch(strSql);
              }
              st.executeBatch();
              con.commit();
              bTrans = false;
           }
           catch (BatchUpdateException ex) {
              throw new YssException("更新科目名称出错！", ex);
           }
           catch (Exception ex) {
              throw new YssException("更新科目名称出错！", ex);
           }
           finally {
              dbl.closeResultSetFinal(rs);
              dbl.closeStatementFinal(st);
              dbl.endTransFinal(con, bTrans);
           }
        }
      */

     /**
      * 根据手工维护的公用权益数据更新当前套账权益
      * @param set String    套帐号
      * @throws YssException
      */
     /*
        private void Update_QySj(String set) throws YssException {
           Connection con = dbl.loadConnection();
           Statement st = null;
           ResultSet rs = null;
           boolean bTrans = false;
           java.util.Date dQyCqr;
           java.util.Date dQyPxr;
           String strSql = "";
           try {
              YssType ytTem = new YssType();
              double dLeft = 1, dRight = 0, dQy = 0;

      StringBuffer bufName = new StringBuffer(), bufValue = new StringBuffer();
              bufName.append(set).append("权益派息日前一天（T-1日）红利入账").append("\t");
              bufName.append(set).append("股票分红按税前利率计算");
              bufValue.append("0\t0");
              String[] strValue = yw.varGetValueMore(bufName.toString(), bufValue.toString()).split("\t");
              boolean bQyPxrz = strValue[0].equalsIgnoreCase("1");
              boolean bSqPxrz = strValue[1].equalsIgnoreCase("1");

              st = con.createStatement();
              con.setAutoCommit(false);
              bTrans = true;

              strSql = "delete from " + pub.yssSetPrefix(set) + "JjQyXx where fqylx in('PG','PX','SG','RGQZ','RZQZ','XJDJ','GFDJ') and fqyCqr =" + dbl.sqlDate(base.getDate());
              st.addBatch(strSql);
              //dbl.executeSql(strSql);
              strSql = msd.getMaxStartDateSql("CsQyXx", "", base.getDate(),
      "fqylx in('PG','PX','SG','RGQZ','RZQZ','XJDJ','GFDJ') and fqycqr = " +
      dbl.sqlDate(base.getDate()) + " and FQybl not in ('银行间','上交所','深交所') ");
      strSql = " select a.*,b.facctattr,b.facctcode from (" + strSql + ") a join " + pub.yssTablePrefix(set) + "laccount b on " + "a.fzqdm=" + dbl.sqlRight("b.facctcode", 6) +
                    " where b.FAcctDetail=1 and (b.facctattr like '%股票投资%' Or b.facctattr like '%基金投资%' or b.facctcode like '%权证投资%') and b.facctattr not like '%配股权证%'";

              rs = dbl.openResultSet(strSql);
              while (rs.next()) {
                 ds.GetAccBalance(rs.getString("facctcode"), rs.getDate("fqydjr"), ytTem, YssFun.toInt(set));
                 if (ytTem.getDouble() > 0) {
                    if (rs.getString("fqybl").indexOf(":") >= 0) {
                       dLeft = Double.parseDouble(YssFun.left(rs.getString("fqybl"), rs.getString("fqybl").indexOf(":")));
      dRight = Double.parseDouble(YssFun.right(rs.getString("fqybl"),
      rs.getString("fqybl").length() - (rs.getString("fqybl").indexOf(":") + 1)));
      dQy = YssFun.roundIt(YssD.div(ytTem.getDouble() * dRight, dLeft), 2);
                    }
                    else {
                       if (rs.getString("fqylx").equalsIgnoreCase("SG") || rs.getString("fqylx").equalsIgnoreCase("PG") || rs.getString("fqylx").indexOf("QZ") >= 0 ||
                           rs.getString("fqylx").indexOf("GFDJ") >= 0) {
      dQy = YssFun.roundIt(YssD.mul(ytTem.getDouble(), rs.getDouble("fqybl")), 0);
                       }
                       else {
                          dQy = YssFun.roundIt(YssD.mul(ytTem.getDouble(), (!bSqPxrz ? rs.getDouble("fqybl") : rs.getDouble("fqyjg"))), 2);
                       }
                    }
                    dQyCqr = rs.getDate("fqycqr");
                    dQyPxr = rs.getDate("fjkjzr");
                    if (bQyPxrz && (rs.getString("fqylx").equalsIgnoreCase("PX") || rs.getString("fqylx").equalsIgnoreCase("XJDJ"))) {
                       dQyPxr = ds.Get_WorkDay(rs.getDate("fjkjzr"), -1);
                    }
                    if (dQy != 0) {
                       strSql = "insert into " + pub.yssSetPrefix(set) +
                             "JjQyXx(fzqdm,fqylx,fqybl,fqyjg,fqydjr,fqycqr,fjkjzr,fkc,fqy,FSh , FZzr , FChk,fstartdate) "
                             + "values('" + rs.getString("facctcode") + "','" + rs.getString("fqylx") + "','" +
                             rs.getString("fqybl") + "',"
      + rs.getDouble("fqyjg") + "," + dbl.sqlDate(rs.getDate("fqydjr")) + "," +
                             dbl.sqlDate(dQyCqr)
      + "," + dbl.sqlDate(dQyPxr) + "," + ytTem.getDouble() + "," + dQy + " , 1 , '" +
                             pub.getCurrentUser() + "', '" + pub.getCurrentUser() + "'," + dbl.sqlDate("1900-1-1") + ")";
                       //dbl.executeSql(strSql);
                       st.addBatch(strSql);
                    }
                 }
              }
              rs.getStatement().close();

              if (bQyPxrz) { //处理提前到帐情况
                 strSql = "delete from " + pub.yssSetPrefix(set) + "JjQyXx where fqylx in('PX','XJDJ') and fqyCqr > fjkJzr and fjkjzr =" +
                       dbl.sqlDate(base.getDate());
                 st.addBatch(strSql);
                 //dbl.executeSql(strSql);
      strSql = msd.getMaxStartDateSql("CsQyXx", "", base.getDate(),
      "fqylx in('PX','XJDJ') and fjkjzr <= fqyCqr and fjkjzr = " +
                                                 dbl.sqlDate(ds.Get_WorkDay(base.getDate(), 1)) + " and FQybl not in ('银行间','上交所','深交所') ");
      strSql = " select a.*,b.facctattr,b.facctcode from (" + strSql + ") a join " + pub.yssTablePrefix(set) + "laccount b on " + "a.fzqdm=" + dbl.sqlRight("b.facctcode", 6) +
                       " where b.FAcctDetail=1 and (b.facctattr like '%股票投资%' Or b.facctattr like '%基金投资%' or b.facctcode like '%权证投资%') and b.facctattr not like '%配股权证%'";

                 rs = dbl.openResultSet(strSql);
                 while (rs.next()) {
                    ds.GetAccBalance(rs.getString("facctcode"), rs.getDate("fqydjr"), ytTem, YssFun.toInt(set));
                    if (ytTem.getDouble() > 0) {
                       if (rs.getString("fqybl").indexOf(":") >= 0) {
                          dLeft = Double.parseDouble(YssFun.left(rs.getString("fqybl"), rs.getString("fqybl").indexOf(":")));
      dRight = Double.parseDouble(YssFun.right(rs.getString("fqybl"),
      rs.getString("fqybl").length() - (rs.getString("fqybl").indexOf(":") + 1)));
      dQy = YssFun.roundIt(YssD.div(ytTem.getDouble() * dRight, dLeft), 2);
                       }
                       else {
                          if (rs.getString("fqylx").equalsIgnoreCase("SG") || rs.getString("fqylx").equalsIgnoreCase("PG") || rs.getString("fqylx").indexOf("QZ") >= 0 ||
                              rs.getString("fqylx").indexOf("GFDJ") >= 0) {
      dQy = YssFun.roundIt(YssD.mul(ytTem.getDouble(), rs.getDouble("fqybl")), 0);
                          }
                          else {
                             dQy = YssFun.roundIt(YssD.mul(ytTem.getDouble(), (!bSqPxrz ? rs.getDouble("fqybl") : rs.getDouble("fqyjg"))), 2);
                          }
                       }
                       dQyCqr = rs.getDate("fqycqr");
                       dQyPxr = rs.getDate("fjkjzr");
                       if (bQyPxrz && (rs.getString("fqylx").equalsIgnoreCase("PX") || rs.getString("fqylx").equalsIgnoreCase("XJDJ"))) {
                          dQyPxr = ds.Get_WorkDay(rs.getDate("fjkjzr"), -1);
                       }
                       if (dQy != 0) {
                          strSql = "insert into " + pub.yssSetPrefix(set) +
                                "JjQyXx(fzqdm,fqylx,fqybl,fqyjg,fqydjr,fqycqr,fjkjzr,fkc,fqy,FSh , FZzr , FChk,fstartdate) "
                                + "values('" + rs.getString("facctcode") + "','" + rs.getString("fqylx") + "','" +
                                rs.getString("fqybl") + "',"
      + rs.getDouble("fqyjg") + "," + dbl.sqlDate(rs.getDate("fqydjr")) + "," +
                                dbl.sqlDate(dQyCqr)
      + "," + dbl.sqlDate(dQyPxr) + "," + ytTem.getDouble() + "," + dQy + " , 1 , '" +
                                pub.getCurrentUser() + "', '" + pub.getCurrentUser() + "'," + dbl.sqlDate("1900-1-1") + ")";
                          //dbl.executeSql(strSql);
                          st.addBatch(strSql);
                       }
                    }
                 }
                 rs.getStatement().close();
              }
              st.executeBatch();
              con.commit();
              bTrans = false;
           }
           catch (BatchUpdateException ex) {
              throw new YssException("更新套账权益数据出错！", ex);
           }
           catch (Exception ex) {
              throw new YssException("更新套账权益数据出错！", ex);
           }
           finally {
              dbl.closeResultSetFinal(rs);
              dbl.closeStatementFinal(st);
              dbl.endTransFinal(con, bTrans);
           }
        }
      */
     /*
        public void Adjust_Fxj(String set) throws YssException {
           Connection con = dbl.loadConnection();
           Statement st = null;
           ResultSet rs = null;
           boolean bTrans = false;
           double sumFxj = 0, xwFxj = 0, subFxj = 0;
           String strSql;
           try {
              //获取汇总接口清算表风险金合计
              strSql = "select sum(Fbfxj+fsfxj) as fxj from " + pub.yssSetPrefix(set) + "HzJkQs "
                    + " where fdate=" + dbl.sqlDate(base.getDate());
              rs = dbl.openResultSet(strSql);
              if (rs.next()) {
                 sumFxj = rs.getDouble(1);
              }
              rs.getStatement().close();
              rs = null;
              //按席位交易金额计算的风险金合计
              strSql = "select sum(ffxj) as fxj from " + pub.yssSetPrefix(set) + "HzXwTjb where fdate=" + dbl.sqlDate(base.getDate());
              rs = dbl.openResultSet(strSql);
              if (rs.next()) {
                 xwFxj = rs.getDouble(1);
              }
              rs.getStatement().close();
              rs = null;
              if (xwFxj == 0)return;
              con.setAutoCommit(false);
              bTrans = true;
              st = con.createStatement();
              //调差
              subFxj = YssD.sub(sumFxj, xwFxj);
              if (subFxj != 0) {
                 //调差处理为：从总交易量最大的一只证券中调整
                 strSql = "select * from " + pub.yssSetPrefix(set) +
                       "HzJkQs where (FZqbz in('GP','JJ') or FYwbz in('GZXQ','MRHG','MCHG')) and (FBFxj > 0 or FSFxj > 0) and fdate=" + dbl.sqlDate(base.getDate()) +
                       " order by fbje+fsje desc";
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                    if (rs.getDouble("FBje") > 0) { //优先考虑从买调整
      if (pub.getAssetType() == 1 && pub.getFundGlr().indexOf("中金公司") > -1) {
      strSql = "update " + pub.yssSetPrefix(set) + "HzJkQs set fbfxj= fbfxj-(" + subFxj + ") , fbyj = fbyj+(" + subFxj + "),fbsfje = fbsfje + (" + subFxj + ") where "
                                + " fdate=" + dbl.sqlDate(base.getDate()) + " and Fzqdm='" + rs.getString("FZqdm") + "' and fjyxwh='" + rs.getString("fjyxwh") + "'";
                       }
                       else {
                          strSql = "update " + pub.yssSetPrefix(set) + "HzJkQs set fbfxj= fbfxj-(" + subFxj + ") where "
      + " fdate=" + dbl.sqlDate(base.getDate()) + " and Fzqdm='" + rs.getString("FZqdm") + "' and (fzqbz in('GP','JJ') or FYwbz in('GZXQ','MRHG','MCHG'))";
                       }
                    }
                    else {
      if (pub.getAssetType() == 1 && pub.getFundGlr().indexOf("中金公司") > -1) {
      strSql = "update " + pub.yssSetPrefix(set) + "HzJkQs set fsfxj= fsfxj-(" + subFxj + ") , fsyj = fsyj+(" + subFxj + "),fsssje = fsssje + (" + subFxj + ") where "
                                + " fdate=" + dbl.sqlDate(base.getDate()) + " and Fzqdm='" + rs.getString("FZqdm") + "'  and fjyxwh='" + rs.getString("fjyxwh") + "'";
                       }
                       else {
                          strSql = "update " + pub.yssSetPrefix(set) + "HzJkQs set fsfxj= fsfxj-(" + subFxj + ") where "
      + " fdate=" + dbl.sqlDate(base.getDate()) + " and Fzqdm='" + rs.getString("FZqdm") + "' and (fzqbz in('GP','JJ') or fywbz in('GZXQ','MRHG','MCHG'))";
                       }
                    }
                    dbl.executeSql(strSql);
                 }
                 rs.getStatement().close();
                 rs = null;
              }
              //风险金计入成本 ---前提,同类券 按席位计入成本的话,就不再按股东代码计入成本---cyp
              strSql = "select FJflb,FQsxw,FZqlb from " + pub.yssSetPrefix(set) + "CsXwFy where FFyfs = 2 and FFylb = 'FXJ' and FSh = 1 ";
              rs = dbl.openResultSet(strSql);
              while (rs.next()) {
                 if (rs.getInt("FJflb") == 1) { //按席位
                    strSql = "upDate " + pub.yssSetPrefix(set) + "HzJkQs set FSssje= FSssje-fsfxj , FBsfje= FBsfje+FBFxj where"
                          + " FDate = " + dbl.sqlDate(base.getDate()) + " and FZqbz = '" + (rs.getString("FZqlb").equalsIgnoreCase("GZXQ") ? "ZQ" : rs.getString("FZqlb")) + "' "
                          + " and FJyxwh = '" + rs.getString("FQsxw") + "'";
                    st.addBatch(strSql);
                 }
                 else { //按股东代码
                    //先按该股东代码在HzJkHz中取出该类券的 买/卖风险金,按 席位+证券代码 汇总
                    strSql = "create view JrcbFxj as "
      + "(select sum( case when fbs = 'B' then  ffxj else 0 end) as fbfxj,"
      + "sum(case when fbs = 'S' then ffxj else 0 end)  as fsfxj,fzqdm,fjyxwh"
                          + " from " + pub.yssSetPrefix(set) + "HzJkHz where fgddm like '" + rs.getString("FQsxw") + "' and fdate = " + dbl.sqlDate(base.getDate())
                          + " and FZqbz = '" + (rs.getString("FZqlb").equalsIgnoreCase("GZXQ") ? "ZQ" : rs.getString("FZqlb")) + "'"
                          + " group by fzqdm,fjyxwh)";
                    st.addBatch(strSql);
                    //再将hzjkqs中的该类券的实际 收/付金额 加/减去 取出的风险金
      strSql = "update " + pub.yssSetPrefix(set) + "hzjkqs a set  "
                          + " fbsfje = fbsfje + (select fbfxj from JrcbFxj b where a.fjyxwh" + dbl.sqlJN() + "a.fzqdm=b.fjyxwh" + dbl.sqlJN() + "b.fzqdm ) ,"
                          + " fsssje = fsssje - (select fsfxj from JrcbFxj b where a.fjyxwh" + dbl.sqlJN() + "a.fzqdm=b.fjyxwh" + dbl.sqlJN() + "b.fzqdm )  "
      + " where fjyxwh" + dbl.sqlJN() + "fzqdm  in ( select b.fjyxwh" + dbl.sqlJN() + "b.fzqdm from JrcbFxj b  where a.fjyxwh" + dbl.sqlJN() + "a.fzqdm=b.fjyxwh" +
                          dbl.sqlJN() + "b.fzqdm ) "
                          + " and fdate = " + dbl.sqlDate(base.getDate())
                          + " and FZqbz = '" + (rs.getString("FZqlb").equalsIgnoreCase("GZXQ") ? "ZQ" : rs.getString("FZqlb")) + "'";
                    st.addBatch(strSql);
                    strSql = " drop view JrcbFxj ";
                    st.addBatch(strSql);
                 }
              }
              st.executeBatch();
              rs.getStatement().close();
              rs = null;
              st.close();
              con.commit();
              bTrans = false;
           }
           catch (Exception e) {
              throw new YssException("调整风险金失败！", e);
           }
           finally {
              dbl.closeResultSetFinal(rs);
              dbl.closeStatementFinal(st);
              dbl.endTransFinal(con, bTrans);
           }
        }
      */
     /*
//共用席位风险金调整
        public void Adjust_ShareXwFxj() throws YssException {
           Connection con = dbl.loadConnection();
           Statement st = null;
           ResultSet rs = null, rst = null, rstmp = null;
           boolean bTrans = false;
           String strSql, sql, SetPrefix, ffddm_sh, ffddm_sz;
           String[] fzqlb;
           double FxjA = 0.0, FxjB = 0.0;//FxjA为hzxwtjb里面每个席位风险金计算好之后的汇总；FxjB为先按席位加总的成交金额再与风险金利率相乘的风险金
           double dFxj = 0.0 ,dFxjFl= 0.0 ,tmpFxj = 0.0, sumFxj = 0.0;
           try {
              con.setAutoCommit(false);
              bTrans = true;
              st = con.createStatement();
              //插入各套帐数据到临时表。
              strSql = "select fsetid,fsetcode,fyear from lsetlist where  FYear = " + pub.getCurrentYear();
              rs = dbl.openResultSet(strSql);
              while (rs.next()) {
                 SetPrefix = pub.yssSetPrefix(rs.getInt("fsetcode"));
                 ffddm_sh = ds.GetValuebySql(msd.getMaxStartDateSql(SetPrefix + "CsGdDm", "SET", base.getDate(), "FSzSh = 'H'", "FGdDm"));
                 ffddm_sz = ds.GetValuebySql(msd.getMaxStartDateSql(SetPrefix + "CsGdDm", "SET", base.getDate(), "FSzSh = 'S'", "FGdDm"));
                 sql = "insert into HzXwTjbTmp select fdate,(case when fszsh='H' then  '" + ffddm_sh + "'  else '" + ffddm_sz + "'  end ) as fgddm, '" + rs.getInt("fsetcode") +
                       "' as fset,fqsdm,fqsxw,fzqbz,fywbz,fszsh,fbsl,fssl,fbje,fsje,fbyj,fsyj,fbyhs,fsyhs,fhgje,ffxj from  " + SetPrefix + "HzXwTjb where fdate = " +
                       dbl.sqlDate(base.getDate());
                 st.addBatch(sql);
              }
              st.executeBatch();
              bTrans = true;
              rs.getStatement().close();
              rs = null;
              st.close();

              //以下按照券种进行循环核对
              fzqlb = "GP\tJJ\tZQ\tHG".split("\t");
              for (int i = 0; i <= fzqlb.length - 1; i++) {
                 strSql = "select fdate,fszsh,fqsxw,fzqbz,fywbz, sum(fbje) as fbje, sum(fsje) as fsje,sum(ffxj) as ffxj from HzXwTjbTmp where fdate = " +
                       dbl.sqlDate(base.getDate());
                 if (fzqlb[i].equalsIgnoreCase("GP") || fzqlb[i].equalsIgnoreCase("JJ") || fzqlb[i].equalsIgnoreCase("QZ")) {
                    strSql += " and fzqbz ='" + fzqlb[i] + "'";
                 }
                 else if (fzqlb[i].equalsIgnoreCase("ZQ")) {
      strSql += " and fzqbz ='" + fzqlb[i] + "' and fywbz = 'GZXQ'";
                 }
                 else if (fzqlb[i].equalsIgnoreCase("HG")) {
                    strSql += " and fzqbz ='" + fzqlb[i] + "' and fywbz in('MRHG','MCHG','MRHG_QY','MCHG_QY')";
                 }
                 strSql += " group by fdate,fszsh,fqsxw,fzqbz,fywbz";
                 rs = dbl.openResultSet(strSql);
                 while (rs.next()) {
                    FxjA = rs.getDouble("ffxj");
                    FxjB = 0.0;
                    if (fzqlb[i].equalsIgnoreCase("GP") || fzqlb[i].equalsIgnoreCase("JJ") || fzqlb[i].equalsIgnoreCase("QZ")) {
                       //FxjB = calc.getJyLv(rs.getString("FSetCode"), rs.getString("fzqbz"), "", rs.getString("fqsxw"), rs.getString("fszsh"), "FXJ");
                       FxjB = 0.00003;
                       FxjB = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("fbje"), rs.getDouble("fsje")), FxjB), 2);
                     }
                    else if (fzqlb[i].equalsIgnoreCase("ZQ")) {
                       //FxjB = calc.getJyLv(rs.getString("FSetCode"), rs.getString("fywbz"), "", rs.getString("fqsxw"), rs.getString("fszsh"), "FXJ");
                       FxjB = 0.00001;
                       FxjB = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("fbje"), rs.getDouble("fsje")), FxjB), 2);
                    }
                    else if (fzqlb[i].equalsIgnoreCase("HG")) {
                       strSql = "select fsetid,fsetcode,fyear from lsetlist where  FYear = " + pub.getCurrentYear();
                       rstmp = dbl.openResultSet(strSql);
                       while (rstmp.next()) {
                          tmpFxj = 0.0;
                          sumFxj = 0.0;
      rst = dbl.openResultSet("Select fdate,fzqdm,sum(fbje) as bje,sum(fsje) as sje from " + pub.yssSetPrefix(rstmp.getString("FSetCode")) + "HzJkQS  where FDate=" +
      dbl.sqlDate(base.getDate()) +
                                                  " and  fjyxwh= '" + rs.getString("fqsxw") + "' and fszsh='" + rs.getString("fszsh") + "' and fywbz = '" + rs.getString("fywbz") +
                                                  "' group by fdate,fzqdm");
                          while (rst.next()) {
                             dFxjFl = calc.getJyLv(rstmp.getString("FSetCode"), "HG" + rst.getString("fzqdm"), "", rs.getString("fqsxw"), rs.getString("fszsh"), "FXJ");
                             tmpFxj = YssFun.roundIt(YssD.mul(YssD.add(rst.getDouble("Bje"), rst.getDouble("Sje")), dFxjFl),2);
      sumFxj = YssFun.roundIt(YssD.add(tmpFxj , sumFxj),2);
                          }
                          rst.getStatement().close();
                          FxjB = FxjB + sumFxj;
                       }
                       rstmp.getStatement().close();
                    }
      dFxj = YssFun.roundIt(YssD.sub(FxjB, FxjA) ,2); //以按照席位先汇总成交金额*风险金利率为准(FxjB)
                    if (dFxj != 0 && Math.abs(dFxj) >= 0.01) {
      sql = "select distinct fdate,fgddm,FSetCode,fqsxw,fzqbz,fszsh,fbje+fsje as fcjje from HzXwTjbTmp where fdate =" + dbl.sqlDate(base.getDate()) + " and fzqbz = '" +
      fzqlb[i] + "' and fszsh = '" + rs.getString("fszsh") + "' and fqsxw = '" + rs.getString("fqsxw") + "' and fywbz ='" + rs.getString("fywbz") + "' order by fcjje desc,fgddm desc";
                       rstmp = dbl.openResultSet(sql);
                       while (rstmp.next()) {
                          if(dFxj != 0 && Math.abs(dFxj) >= 0.01){
      SetPrefix = pub.yssSetPrefix(rstmp.getInt("fsetcode"));
                             dbl.executeSql("update " + SetPrefix + "HzXwTjb set ffxj = ffxj " + (dFxj > 0 ? "+" : "-") + "0.01 where fdate=" + dbl.sqlDate(base.getDate()) +
      " and fszsh ='" + rs.getString("fszsh") + "' and fqsxw ='" + rs.getString("fqsxw") + "' and fzqbz='" + rs.getString("fzqbz") + "' and fywbz = '" +
                                            rs.getString("fywbz") + "'");
                             if (dFxj > 0)
                                dFxj = dFxj - 0.01;
                             else
                                dFxj = dFxj + 0.01;
                          }
                       }
                       rstmp.getStatement().close();
                    }
                 }
              }
              dbl.executeSql("delete from HzXwTjbTmp");
              rs.getStatement().close();
              con.commit();
              bTrans = false;
           }
           catch (Exception e) {
              throw new YssException("共用席位调整风险金失败！", e);
           }
           finally {
              dbl.closeResultSetFinal(rs);
              dbl.closeStatementFinal(st);
              dbl.endTransFinal(con, bTrans);
           }
        }
      */
}
