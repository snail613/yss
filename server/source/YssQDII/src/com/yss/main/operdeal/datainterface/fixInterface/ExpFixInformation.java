package com.yss.main.operdeal.datainterface.fixInterface;

import java.sql.*;
import java.util.*;

import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.util.*;

/**
 * <p>Title:xuqiji 20090709：QDV4中保2009年06月09日01_A  MS00497 中保接口需求-净值信息表 </p>
 *
 * <p>Description: 此类做净值信息数据导出前一些数据的查询，并保存数据先到临时表</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ExpFixInformation extends DataBase{
    private String TRADEDATE="";//业务日期
    private String FUND_CODE="";//组合代码
    private String SUB_KIND="";//组合小类别(股票组合，债券组合，货币市场组合)
    private String MARKET_VALUE="";//组合本币净值市值
    private String COST_VALUE="";//组合本币净值成本
    private String MARKET_VALUE3="";//原币净值市值
    private String COST_VALUE3="";//原币净值成本
    private String CURYCODE="";//原币币种
    private String declare_sum="";//当日流入本币金额
    private String Redeem_sum="";//当日流出本币金额
    private String Decalre_sum3="";//当日流入原币金额
    private String Redeem_sum3="";//当日流出原币金额
    private String MANAGER="";//投资经理编号
    public ExpFixInformation() {
    }
    /**
     * 入口方法
     * @throws YssException
     */
    public void inertData() throws YssException {
        this.doCreaterTMPTable();
        String[] port=this.sPort.split(",");
        for(int i=0;i<port.length;i++){
            doInsertData(port[i], this.sDate);
        }
    }
    /**
     * 往临时表中插入数据
     * @param sPort String 组合代码
     * @param sDate String 操作日期
     * @throws YssException
     */
    private void doInsertData(String sPort, java.util.Date sDate) throws YssException {
        String sql="";
        ResultSet rs=null;
        HashMap cashTransferOUTMap = null;//保存获取操作日期，操作组合的原币和组合货币流出的资金调拨
        HashMap cashTransferINMap=null;//保存获取操作日期，操作组合的原币和组合货币流入的资金调拨
        String key="";
        String data="";//保存数据
        ArrayList saveData=new ArrayList();
        try{
            cashTransferOUTMap=this.getYBAndPortCuryCashTransferOUT(sPort,sDate);//获取操作日期，操作组合的原币和组合货币流出的资金调拨
            cashTransferINMap=this.getYBAndPortCuryCashTransferIN(sPort,sDate);//获取操作日期，操作组合的原币和组合货币流入的资金调拨
            sql=getYBAndPortCuryCostAndMarket(sPort,sDate);//获取操作日期、操作组合的原币和组合货币市值,原币和组合货币成本的sql语句

            rs=dbl.openResultSet(sql);
            while(rs.next()){
                //以投资经理，币种代码，组合小类别做为键值
                key=rs.getString("MANAGER")+"\f"+rs.getString("CURYCODE")+"\f"+rs.getString("SUB_KIND");
                //把数据拼接保存
                data=Double.toString(rs.getDouble("COST_VALUE3"))+"\f"+Double.toString(rs.getDouble("COST_VALUE"))+
                        "\f"+Double.toString(rs.getDouble("MARKET_VALUE3"))+"\f"+
                        Double.toString(rs.getDouble("MARKET_VALUE"));
                if(cashTransferINMap.containsKey(key)){//判断资金调拨流入数据中是否有这个key-values
                    data+="\f"+cashTransferINMap.get(key);
                }else{
                    data+="\f"+0+"\f"+0;//如果资金调拨中没有key-values 直接拼接“0”
                }
                if(cashTransferOUTMap.containsKey(key)){//判断资金调拨流出数据中是否有这个key-values
                    data+="\f"+cashTransferOUTMap.get(key);
                }else{
                    data+="\f"+0+"\f"+0;//如果资金调拨中没有key-values 直接拼接“0”
                }
                data+="\f"+key;
                saveData.add(data);//把拼接的数据用ArrayList保存
            }
            realInsertDataIntoTem(saveData,sPort);//插入拼接的数据到临时表
        }catch(Exception e){
            throw new YssException(e.getMessage());
        }finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * realInsertDataIntoTem批量插入数据到临时表tmp_fixInformation
     *
     * @param saveData ArrayList
     */
    private void realInsertDataIntoTem(ArrayList saveData,String sPort) throws YssException {
        PreparedStatement pst = null;
        StringBuffer buff = null;
        String[] data=null;
        try{
            buff=new StringBuffer();
            buff.append(" insert into ").append("tmp_fixInformation");
            buff.append("(TRADEDATE,FUND_CODE,SUB_KIND,MARKET_VALUE,COST_VALUE,MARKET_VALUE3,");
            buff.append(" COST_VALUE3,CURYCODE,declare_sum,Redeem_sum,Decalre_sum3,Redeem_sum3,");
            buff.append(" MANAGER)");
            buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?)");

            pst=dbl.openPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            for(int i=0;i<saveData.size();i++){
                data=saveData.get(i).toString().split("\f");
                //xuqiji 20090817:QDV4中保2009年08月14日01_B  MS00630    净值信息表一些数据中的部分字段为空
                if(Double.parseDouble(data[3])==0&&Double.parseDouble(data[1])==0&&
                      Double.parseDouble(data[2])==0&&Double.parseDouble(data[0])==0&&
                      Double.parseDouble(data[5])==0&&Double.parseDouble(data[7])==0&&
                      Double.parseDouble(data[4])==0&&Double.parseDouble(data[6])==0){
                   continue;
                }
                //---------------------------end 20090817----------------------------------//
                pst.setDate(1,YssFun.toSqlDate(this.sDate));
                pst.setString(2,sPort);
                pst.setString(3,data[10]);
                pst.setDouble(4,Double.parseDouble(data[3]));
                pst.setDouble(5,Double.parseDouble(data[1]));

                pst.setDouble(6,Double.parseDouble(data[2]));
                pst.setDouble(7,Double.parseDouble(data[0]));
                pst.setString(8,data[9]);
                pst.setDouble(9,Double.parseDouble(data[5]));
                pst.setDouble(10,Double.parseDouble(data[7]));
                pst.setDouble(11,Double.parseDouble(data[4]));
                pst.setDouble(12,Double.parseDouble(data[6]));
                pst.setString(13,data[8]);

                pst.addBatch();
            }
            pst.executeBatch();
        }catch(Exception e){
            throw new YssException("批量插入数据到临时表出错！",e);
        }finally{
            dbl.closeStatementFinal(pst);
        }
    }

    /**
     * 创建净值信息数据导出临时表tmp_fixInformation
     * @throws YssException
     */
    private void doCreaterTMPTable() throws YssException {
        StringBuffer buff=null;
        try{
            buff=new StringBuffer();
            if(dbl.yssTableExist("tmp_fixInformation")){//如果数据库中已经有表，先删除再创建
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop(" drop table tmp_fixInformation"));
                /**end*/
            }
            buff.append(" create table tmp_fixInformation(");
            buff.append(" TRADEDATE DATE NOT NULL,");
            buff.append(" FUND_CODE VARCHAR2(20) NOT NULL,");
            buff.append(" SUB_KIND VARCHAR2(20) NOT NULL,");
            buff.append(" MARKET_VALUE NUMBER(18, 4) DEFAULT 0 NOT NULL,");
            buff.append(" COST_VALUE NUMBER(18, 4) DEFAULT 0 NOT NULL,");
            buff.append(" MARKET_VALUE3 NUMBER(18, 4) DEFAULT 0 NOT NULL,");
            buff.append(" COST_VALUE3 NUMBER(18, 4) DEFAULT 0 NOT NULL,");
            buff.append(" CURYCODE VARCHAR2(20) DEFAULT 0 NOT NULL,");
            buff.append(" declare_sum NUMBER(18, 4) DEFAULT 0 NOT NULL,");
            buff.append(" Redeem_sum NUMBER(18, 4) DEFAULT 0 NOT NULL,");
            buff.append(" Decalre_sum3 NUMBER(18, 4) DEFAULT 0 NOT NULL,");
            buff.append(" Redeem_sum3 NUMBER(18, 4) DEFAULT 0 NOT NULL,");
            buff.append(" MANAGER VARCHAR2(20) NOT NULL");
            buff.append(")");
            dbl.executeSql(buff.toString());
        }catch(Exception e){
            throw new YssException("创建净值信息数据导出临时表出错！",e);
        }
    }

    /**
     * 获取操作日期，操作组合的原币和组合货币流出的资金调拨
     * @param sPort String 组合代码
     * @param sDate Date 操作日期
     * @return HashMap
     * @throws YssException
     */
    private HashMap getYBAndPortCuryCashTransferOUT(String sPort, java.util.Date sDate) throws YssException {
        HashMap cashTransferOUTMap = null;
        StringBuffer buff = null;
        ResultSet rs = null;
        try {
            cashTransferOUTMap = new HashMap();
            buff = new StringBuffer();
            buff.append(" select sum(FMoney) as Redeem_sum3,");//当日流出原币金额
            buff.append(" sum(FMoney * (case when FBaseCuryRate = 0 then 1 else FBaseCuryRate end)/");
            buff.append(" (case when FPortCuryRate = 0 then 1 else FPortCuryRate end)) as Redeem_sum,");//当日流出本币金额=原币金额*基础汇率/组合汇率
            buff.append(" t.FAnalysisCode1 as MANAGER,");
            buff.append(" case when t.FAnalysisCode2 = 'DE' then 'MK' else t.FAnalysisCode2 end  as SUB_KIND,");//合太平版本代码
            buff.append(" t.FCuryCode as CURYCODE");
            buff.append(" from (");
            buff.append(" select tf.*, stf.*");
            buff.append(" from ");
            buff.append(pub.yssGetTableName("Tb_Cash_Transfer")).append(" tf");//资金调拨表
            buff.append(" join (");
            buff.append(" select s.*, c.fcurycode");
            buff.append(" from ");
            buff.append(pub.yssGetTableName("Tb_Cash_SubTransfer")).append(" s");//资金调拨子表
            buff.append(" left join (");
            buff.append(" select *");
            buff.append(" from ");
            buff.append(pub.yssGetTableName("Tb_Para_CashAccount"));//现金账户表
            buff.append(" where FCheckState = 1");
            buff.append(" and FPortCode = ").append(dbl.sqlString(sPort));
            buff.append(" ) c on s.fcashacccode = c.FCashAccCode");
            buff.append(" where s.FCheckState = 1");
            buff.append(" and s.FPortCode =").append(dbl.sqlString(sPort));
            buff.append(" and s.FInOut = -1");//方向为流出
            buff.append(" ) stf on tf.fnum = stf.fnum");
            buff.append(" where tf.FCheckState = 1");
            buff.append(" and tf.FTransferDate=").append(dbl.sqlDate(sDate));
            buff.append(" and tf.FTsfTypeCode ='04'");
            buff.append(" ) t group by t.FAnalysisCode1,t.FAnalysisCode2,t.FCuryCode");//以投资经理，分析代码2，币种代码，进行分组

            rs=dbl.openResultSet(buff.toString());
            while(rs.next()){
                this.MANAGER=rs.getString("MANAGER");//投资经理
                this.CURYCODE=rs.getString("CURYCODE");//币种代码
                this.SUB_KIND=rs.getString("SUB_KIND");//现金类的取分析代码2
                this.Redeem_sum3=Double.toString(rs.getDouble("Redeem_sum3"));//当日流出原币金额
                this.Redeem_sum=Double.toString(rs.getDouble("Redeem_sum"));//当日流出本币金额
                cashTransferOUTMap.put(this.MANAGER+"\f"+this.CURYCODE+"\f"+this.SUB_KIND,
                                       this.Redeem_sum3+"\f"+this.Redeem_sum);//以键值对放入
            }
        } catch (Exception e) {
            throw new YssException("获取操作日期，操作组合的原币和组合货币流出的资金调拨出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return cashTransferOUTMap;
    }

    /**
     * 获取操作日期，操作组合的原币和组合货币流入的资金调拨
     * @param sPort String 组合代码
     * @param sDate Date 操作日期
     * @return HashMap
     * @throws YssException
     */
    private HashMap getYBAndPortCuryCashTransferIN(String sPort,java.util.Date sDate) throws YssException {
        HashMap cashTransferINMap=null;
        StringBuffer buff=null;
        ResultSet rs=null;
        try{
            cashTransferINMap=new HashMap();
            buff=new StringBuffer();
            buff = new StringBuffer();
            buff.append(" select sum(FMoney) as Decalre_sum3,");//当日流入原币金额
            buff.append(" sum(FMoney * (case when FBaseCuryRate = 0 then 1 else FBaseCuryRate end)/");
            buff.append(" (case when FPortCuryRate = 0 then 1 else FPortCuryRate end)) as declare_sum,");//当日流入本币金额=原币金额*基础汇率/组合汇率
            buff.append(" t.FAnalysisCode1 as MANAGER,");
            buff.append("case when t.FAnalysisCode2 = 'DE' then 'MK' else t.FAnalysisCode2 end as SUB_KIND,");//合太平版本代码
            buff.append(" t.FCuryCode as CURYCODE");
            buff.append(" from (");
            buff.append(" select tf.*, stf.*");
            buff.append(" from ");
            buff.append(pub.yssGetTableName("Tb_Cash_Transfer")).append(" tf");//资金调拨表
            buff.append(" join (");
            buff.append(" select s.*, c.fcurycode");
            buff.append(" from ");
            buff.append(pub.yssGetTableName("Tb_Cash_SubTransfer")).append(" s");//资金调拨子表
            buff.append(" left join (");
            buff.append(" select *");
            buff.append(" from ");
            buff.append(pub.yssGetTableName("Tb_Para_CashAccount"));//现金账户表
            buff.append(" where FCheckState = 1");
            buff.append(" and FPortCode = ").append(dbl.sqlString(sPort));
            buff.append(" ) c on s.fcashacccode = c.FCashAccCode");
            buff.append(" where s.FCheckState = 1");
            buff.append(" and s.FPortCode =").append(dbl.sqlString(sPort));
            buff.append(" and s.FInOut = 1");//方向为流入
            buff.append(" ) stf on tf.fnum = stf.fnum");
            buff.append(" where tf.FCheckState = 1");
            buff.append(" and tf.FTransferDate=").append(dbl.sqlDate(sDate));
            buff.append(" and tf.FTsfTypeCode ='04'");
            buff.append(" ) t group by t.FAnalysisCode1,t.FAnalysisCode2, t.FCuryCode");//以投资经理，分析代码2，币种代码，进行分组

            rs=dbl.openResultSet(buff.toString());
            while(rs.next()){
                this.MANAGER=rs.getString("MANAGER");//投资经理
                this.CURYCODE=rs.getString("CURYCODE");//币种代码
                this.SUB_KIND=rs.getString("SUB_KIND");//现金类的取分析代码2
                this.Decalre_sum3=Double.toString(rs.getDouble("Decalre_sum3"));//当日流入原币金额
                this.declare_sum=Double.toString(rs.getDouble("declare_sum"));//当日流入本币金额
                cashTransferINMap.put(this.MANAGER+"\f"+this.CURYCODE+"\f"+this.SUB_KIND,
                                       this.Decalre_sum3+"\f"+this.declare_sum);//以键值对放入
            }
        }catch(Exception e){
            throw new YssException("获取操作日期，操作组合的原币和组合货币流入的资金调拨出错！",e);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
        return cashTransferINMap;
    }

    /**
     * 获取操作日期、操作组合的原币和组合货币市值,原币和组合货币成本的sql语句
     * @param sPort String 组合代码
     * @param sDate Date 操作日期
     * @return HashMap
     */
    private String getYBAndPortCuryCostAndMarket(String sPort,java.util.Date sDate) throws YssException {
        StringBuffer buff=null;
        try{
            buff=new StringBuffer();
            //对于证券类的以证券的品种类型，投资经理和币种进行分组，现金类的以分析代码2，投资经理，币种进行分组
            //求总以投资经理，币种，组合小类别进行分组
            buff.append(" select");
            buff.append(" sum(COST_VALUE3) as COST_VALUE3,");//原币成本
            buff.append(" sum(COST_VALUE) as COST_VALUE,");//组合货币成本=原币成本*基础汇率/组合汇率
            buff.append(" sum(MARKET_VALUE3) as MARKET_VALUE3,");//原币市值
            buff.append(" sum(MARKET_VALUE) as MARKET_VALUE,");//组合货币市值=原币市值*基础汇率/组合汇率
            buff.append(" total.MANAGER,");//投资经理
            buff.append(" total.SUB_KIND,");//组合小类别，证券类的取证券的品种类型，现金类的取分析代码2
            buff.append(" total.CURYCODE");//原币币种
            buff.append(" from (");
            buff.append(" select ");//计算证券库存中原币成本，市值和组合货币成本，市值
            buff.append(" sum(FStorageCost) as COST_VALUE3,");
            buff.append(" sum(FStorageCost * (case when FBaseRate = 0 then 1 else FBaseRate end)/");
            buff.append(" (case when FPortRate = 0 then 1 else FPortRate end)) as COST_VALUE,");
            buff.append(" sum(FStorageAmount * FPrice) as MARKET_VALUE3,");
            buff.append(" sum(FStorageAmount * FPrice * (case when FBaseRate = 0 then 1 else FBaseRate end) /");
            buff.append(" (case when FPortRate = 0 then 1 else FPortRate end)) as MARKET_VALUE,");
            buff.append(" tt.FAnalysisCode1 as MANAGER,");
            buff.append("case when  tt.fcatcode = 'DE' then 'MK' else  tt.fcatcode end as SUB_KIND,");//合太平版本代码
            buff.append(" tt.FCuryCode as CURYCODE");
            buff.append(" from (");
            buff.append(" select se.*,pu.fcatcode, valM.FPrice, valR.FBaseRate, valR.FPortRate");
            buff.append(" from ");
            buff.append(pub.yssGetTableName("tb_stock_security")).append(" se ");//证券库存表
            buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_para_security"));//证券信息表
            buff.append(" where FCheckState = 1) pu on se.fsecuritycode = pu.fsecuritycode");
            buff.append(" left join (select e2.* from (select max(FValDate) as FValDate,FPortCode,");
            buff.append(" FSecurityCode  from ").append(pub.yssGetTableName("Tb_Data_ValMktPrice"));//估值行情表
            buff.append(" where FCheckState = 1 and FPortCode =").append(dbl.sqlString(sPort));
            buff.append(" and FValDate <=").append(dbl.sqlDate(sDate));
            buff.append(" group by FPortCode, FSecurityCode) e1 left join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Data_ValMktPrice"));//估值行情表
            buff.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(sPort));
            buff.append(" ) e2 on e1.FValDate = e2.FValDate and e1.FPortCode = e2.FPortCode ");
            buff.append(" and e1.FSecurityCode = e2.fsecuritycode) valM");
            buff.append(" on se.fportcode = valM.fportcode and se.fsecuritycode = valM.fsecuritycode");
            buff.append(" left join (select e3.* from (select max(FValDate) as FValDate,FPortCode,FCuryCode from ");
            buff.append(pub.yssGetTableName("Tb_Data_ValRate"));//估值汇率表
            buff.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(sPort));
            buff.append(" and FValDate <=").append(dbl.sqlDate(sDate));
            buff.append(" group by FPortCode, FCuryCode) e4 left join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Data_ValRate"));//估值汇率表
            buff.append(" where FCheckState = 1 and FPortCode =").append(dbl.sqlString(sPort));
            buff.append(" ) e3 on e3.FValDate = e4.FValDate and e3.FPortCode = e4.FPortCode");
            buff.append(" and e3.FCuryCode = e4.FCuryCode) valR on se.fportcode = valR.fportcode");
            buff.append(" and se.FCuryCode = valR.FCuryCode");
            buff.append(" where se.fcheckstate = 1 and se.FStorageDate=").append(dbl.sqlDate(sDate));
            buff.append(" and se.fportcode = ").append(dbl.sqlString(sPort));
            buff.append(" ) tt group by tt.FAnalysisCode1, tt.fcatcode, tt.FCuryCode");
            buff.append(" union all ");
            buff.append(" select");//计算现金库存中原币成本，市值和组合货币成本，市值
            buff.append(" sum(FAccBalance) as COST_VALUE3,");
            buff.append(" sum(FAccBalance * (case when FBaseRate = 0 then 1 else FBaseRate end) /");
            buff.append(" (case when FPortRate = 0 then 1 else FPortRate end)) as COST_VALUE,");
            buff.append(" sum(FAccBalance) as MARKET_VALUE3,");
            buff.append(" sum(FAccBalance * (case when FBaseRate = 0 then 1 else FBaseRate end) /");
            buff.append(" (case when FPortRate = 0 then 1 else FPortRate end)) as MARKET_VALUE,");
            buff.append(" tt2.FAnalysisCode1 as MANAGER,");
            buff.append(" case when tt2.FAnalysisCode2 = 'DE' then 'MK' else  tt2.FAnalysisCode2 end as SUB_KIND,");//合太平版本代码
            buff.append(" tt2.FCuryCode as CURYCODE");
            buff.append(" from (select ca.*, valR2.FBaseRate, valR2.FPortRate from ");
            buff.append(pub.yssGetTableName("tb_stock_cash")).append(" ca ");//现金库存表
            buff.append(" left join (select e5.* from (select max(FValDate) as FValDate,FPortCode,FCuryCode from ");
            buff.append(pub.yssGetTableName("Tb_Data_ValRate"));//估值汇率表
            buff.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(sPort));
            buff.append(" and FValDate <=").append(dbl.sqlDate(sDate));
            buff.append(" group by FPortCode, FCuryCode) e6 left join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Data_ValRate"));//估值汇率表
            buff.append(" where FCheckState = 1 and FPortCode =").append(dbl.sqlString(sPort));
            buff.append(" ) e5 on e5.FValDate =e6.FValDate and e5.FPortCode = e6.FPortCode");
            buff.append(" and e5.FCuryCode = e6.FCuryCode) valR2 on ca.fportcode = valR2.fportcode");
            buff.append(" and ca.FCuryCode = valR2.FCuryCode");
            buff.append(" where ca.fcheckstate = 1 and ca.FStorageDate =").append(dbl.sqlDate(sDate));
            buff.append(" and ca.fcashacccode not like '%-%'");  //筛选虚拟账户//合太平版本代码
            buff.append(" and ca.fportcode =").append(dbl.sqlString(sPort));
            buff.append(" ) tt2 group by tt2.FAnalysisCode1, tt2.FAnalysisCode2, tt2.FCuryCode");
            buff.append(" union all ");
            buff.append(" select");//计算证券应收应付库存中原币成本，市值和组合货币成本，市值
            buff.append(" sum(0) as COST_VALUE3,");
            buff.append(" sum(0) as COST_VALUE,");
            buff.append(" sum((case when FTsfTypeCode='07' then (FBal*-1) else FBal end)) as MARKET_VALUE3,");
            buff.append(" sum((case when FTsfTypeCode='07' then (FBal*-1) else FBal end) *");
            buff.append(" (case when FBaseRate = 0 then 1 else FBaseRate end) /");
            buff.append(" (case when FPortRate = 0 then 1 else FPortRate end)) as MARKET_VALUE,");
            buff.append(" tt3.FAnalysisCode1 as MANAGER,");
            buff.append(" case when tt3.fcatcode = 'DE' then 'MK' else tt3.fcatcode end as SUB_KIND,");//合太平版本代码
            buff.append(" tt3.FCuryCode as CURYCODE");
            buff.append(" from (select sep.*,ps.fcatcode,valR3.FBaseRate, valR3.FPortRate, st.FStorageAmount from ");
            buff.append(pub.yssGetTableName("tb_stock_secrecpay")).append(" sep ");//证券应收应付库存表
            buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_para_security"));//证券信息表
            buff.append(" where FCheckState = 1) ps on sep.fsecuritycode =ps.fsecuritycode");
            buff.append(" left join (select e7.* from (select max(FValDate) as FValDate,FPortCode,FCuryCode from ");
            buff.append(pub.yssGetTableName("Tb_Data_ValRate"));//估值汇率表
            buff.append(" where FCheckState = 1 and FPortCode =").append(dbl.sqlString(sPort));
            buff.append(" and FValDate <=").append(dbl.sqlDate(sDate));
            buff.append(" group by FPortCode, FCuryCode) e8 left join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Data_ValRate"));//估值汇率表
            buff.append(" where FCheckState = 1 and FPortCode =").append(dbl.sqlString(sPort));
            buff.append(" ) e7 on e7.FValDate = e8.FValDate and e7.FPortCode = e8.FPortCode");
            buff.append(" and e7.FCuryCode = e8.FCuryCode) valR3");
            buff.append(" on sep.fportcode =valR3.fportcode and sep.FCuryCode = valR3.FCuryCode");
            buff.append(" join (select s1.* from ");
            buff.append(pub.yssGetTableName("tb_stock_security")).append(" s1 ");//关联证券库存表，取出库存数量不为0的数据
            buff.append(" where s1.FCheckState = 1 and s1.fstorageamount<>0");
            buff.append(" and s1.FPortCode =").append(dbl.sqlString(sPort));
            buff.append(" and s1.FStorageDate =").append(dbl.sqlDate(sDate));
            buff.append(" ) st on sep.fportcode =st.fportcode and sep.fsecuritycode =st.fsecuritycode");
            buff.append(" and sep.fanalysiscode1 = st.fanalysiscode1 and sep.fanalysiscode2 = st.fanalysiscode2");
            buff.append(" where sep.fcheckstate = 1 and sep.FStorageDate=").append(dbl.sqlDate(sDate));
            buff.append(" and sep.fportcode =").append(dbl.sqlString(sPort));
            buff.append(" and sep.ftsftypecode in('06','07')) tt3");
            buff.append(" group by tt3.FAnalysisCode1, tt3.fcatcode, tt3.FCuryCode");
            buff.append(" union all ");
            buff.append(" select");//计算现金应收应付库存中原币成本，市值和组合货币成本，市值
            buff.append(" sum(0) as COST_VALUE3,");
            buff.append(" sum(0) as COST_VALUE,");
            buff.append(" sum((case when FTsfTypeCode='07' then (FBal*-1) else FBal end)) as MARKET_VALUE3,");
            buff.append(" sum((case when FTsfTypeCode='07' then (FBal*-1) else FBal end) *");
            buff.append(" (case when FBaseRate = 0 then 1 else FBaseRate end) /");
            buff.append(" (case when FPortRate = 0 then 1 else FPortRate end)) as MARKET_VALUE,");
            buff.append(" tt4.FAnalysisCode1 as MANAGER,");
            buff.append(" case when tt4.FAnalysisCode2 = 'DE' then 'MK' else tt4.FAnalysisCode2 end as SUB_KIND,");//合太平版本代码
            buff.append(" tt4.FCuryCode as CURYCODE");
            buff.append(" from (select cap.*, valR4.FBaseRate, valR4.FPortRate from ");
            buff.append(pub.yssGetTableName("tb_stock_cashpayrec")).append(" cap ");//现金应收应付库存表
            buff.append(" left join (select e9.* from (select max(FValDate) as FValDate,FPortCode,FCuryCode from ");
            buff.append(pub.yssGetTableName("Tb_Data_ValRate"));//估值汇率表
            buff.append(" where FCheckState = 1 and FPortCode =").append(dbl.sqlString(sPort));
            buff.append(" and FValDate <=").append(dbl.sqlDate(sDate));
            buff.append(" group by FPortCode, FCuryCode) e10 left join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Data_ValRate"));//估值汇率表
            buff.append(" where FCheckState = 1 and FPortCode =").append(dbl.sqlString(sPort));
            buff.append(" ) e9 on e9.FValDate = e10.FValDate and e9.FPortCode = e10.FPortCode");
            buff.append(" and e9.FCuryCode = e10.FCuryCode) valR4");
            buff.append(" on cap.fportcode =valR4.fportcode and cap.FCuryCode = valR4.FCuryCode");
            buff.append(" where cap.fcheckstate = 1 and cap.FStorageDate=").append(dbl.sqlDate(sDate));
            buff.append(" and cap.fportcode =").append(dbl.sqlString(sPort));
            buff.append(" and cap.fcashacccode not like '%-%'");  //筛选虚拟账户//合太平版本代码
            buff.append(" and cap.ftsftypecode in('06','07')) tt4");
            buff.append(" group by tt4.FAnalysisCode1, tt4.FAnalysisCode2, tt4.FCuryCode");
            //以投资经理，组合小类别（证券类的取证券的品种类型，现金类的取分析代码2），币种代码 进行分组
            buff.append(" ) total group by total.MANAGER, total.SUB_KIND, total.CURYCODE");

        }catch(Exception e){
            throw new YssException("获取操作日期、操作组合的原币和组合货币市值,原币和组合货币成本出错！",e);
        }
        return buff.toString();
    }
    public String getCOST_VALUE() {
        return COST_VALUE;
    }

    public String getCOST_VALUE3() {
        return COST_VALUE3;
    }

    public String getCURYCODE() {
        return CURYCODE;
    }

    public String getDecalre_sum3() {
        return Decalre_sum3;
    }

    public String getDeclare_sum() {
        return declare_sum;
    }

    public String getTRADEDATE() {
        return TRADEDATE;
    }

    public String getSUB_KIND() {
        return SUB_KIND;
    }

    public String getRedeem_sum3() {
        return Redeem_sum3;
    }

    public String getRedeem_sum() {
        return Redeem_sum;
    }

    public String getMARKET_VALUE3() {
        return MARKET_VALUE3;
    }

    public String getMARKET_VALUE() {
        return MARKET_VALUE;
    }

    public String getFUND_CODE() {
        return FUND_CODE;
    }

    public String getMANAGER() {
        return MANAGER;
    }

    public void setCOST_VALUE(String COST_VALUE) {
        this.COST_VALUE = COST_VALUE;
    }

    public void setCOST_VALUE3(String COST_VALUE3) {
        this.COST_VALUE3 = COST_VALUE3;
    }

    public void setDecalre_sum3(String Decalre_sum3) {
        this.Decalre_sum3 = Decalre_sum3;
    }

    public void setDeclare_sum(String declare_sum) {
        this.declare_sum = declare_sum;
    }

    public void setTRADEDATE(String TRADEDATE) {
        this.TRADEDATE = TRADEDATE;
    }

    public void setSUB_KIND(String SUB_KIND) {
        this.SUB_KIND = SUB_KIND;
    }

    public void setRedeem_sum3(String Redeem_sum3) {
        this.Redeem_sum3 = Redeem_sum3;
    }

    public void setRedeem_sum(String Redeem_sum) {
        this.Redeem_sum = Redeem_sum;
    }

    public void setMARKET_VALUE3(String MARKET_VALUE3) {
        this.MARKET_VALUE3 = MARKET_VALUE3;
    }

    public void setMARKET_VALUE(String MARKET_VALUE) {
        this.MARKET_VALUE = MARKET_VALUE;
    }

    public void setMANAGER(String MANAGER) {
        this.MANAGER = MANAGER;
    }

    public void setFUND_CODE(String FUND_CODE) {
        this.FUND_CODE = FUND_CODE;
    }
}






