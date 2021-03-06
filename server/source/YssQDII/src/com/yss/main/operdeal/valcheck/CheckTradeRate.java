package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.ResultSet;
import com.yss.util.YssException;

/**
 *
 * <p>Title: </p>
 * <p>Description: 检查交易证券的汇率与汇率表中汇率是否一致</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CheckTradeRate
    extends BaseValCheck {
    public CheckTradeRate() {
    }

    public String doCheck(Date dTheDay, String sPortCode) throws
        Exception {
        String sReturn = "";
        String strSql = "";
        ResultSet rs = null;
        int iIsError = 0; //记录出错数据数量
        try {
            //2008.1 添加  将交易字表链接汇率表 判断汇率
            strSql =
                "SELECT distinct A.Fportcode,D.FPortName,A.FSECURITYCODE,A.FBargainDate, A.FTRADECURY " +
              //modify by zhangfa 20100911  MS01716     分红权益信息设置里设置与交易货币不同的币种，不应提示汇率不一致    QDV4交银施罗德2010年09月08日01_B 
                ", A.PFCuryCode "+
              //-----------------------------------------------------------------------------------------------------------------------------------  
                "FROM (" +
                " SELECT A1.FSECURITYCODE,A1.FBARGAINDATE,A2.FTRADECURY,A1.FBaseCuryRate,A3.FPORTCURY,A3.FAssetGroupCode,A1.FPortCuryRate,'" +
                pub.getPortBaseCury(sPortCode) + "' as FBASECURY,A1.FPortCode" +// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
              //modify by zhangfa 20100911  MS01716     分红权益信息设置里设置与交易货币不同的币种，不应提示汇率不一致    QDV4交银施罗德2010年09月08日01_B 
                " ,Pc.PFCuryCode"+
              //----------------------------------------------------------------------------------------------------------------------------------  
                " FROM " +pub.yssGetTableName("TB_DATA_SUBTRADE") + " A1" +
                " LEFT JOIN " + pub.yssGetTableName("TB_PARA_SECURITY") +
                " A2 ON A1.fsecuritycode=A2.fsecuritycode" +
                " LEFT JOIN " + "(select * from "+pub.yssGetTableName("TB_PARA_PORTFOLIO") +" where fcheckstate =1 )"+
                " A3 ON A1.fportcode=A3.fportcode" +
               //modify by zhangfa 20100911  MS01716     分红权益信息设置里设置与交易货币不同的币种，不应提示汇率不一致    QDV4交银施罗德2010年09月08日01_B 
                " left join (select FCuryCode as PFCuryCode,FCashAccCode from "+pub.yssGetTableName("TB_para_cashaccount") +
                " where FPortCode =" + dbl.sqlString(sPortCode) +" and FCheckState = 1"+
                " ) pc on A1.Fcashacccode=pc.FCashAccCode "+
                //--------------------------------------------------------------------------------------------------------
                " WHERE FBargainDate = " + dbl.sqlDate(dTheDay) +
                " AND A1.FPortCode =" + dbl.sqlString(sPortCode) +
                //2008.11.28 蒋锦 修改 添加 FCheckState 的查询条件 编号：MS00027
                " AND A1.FCheckState = 1) A" +
                " LEFT JOIN (SELECT * FROM " + pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
                //2008.1 修改 蒋锦 将日期作为子查询的条件更为准确
                " WHERE FEXRATEDATE = " + dbl.sqlDate(dTheDay) +" and fcheckstate=1"+//edited by zhouxiang MS1378
                //------ 增加关联基础货币的条件 modiby wangzuochun 2011.04.02 BUG #1638 估值检查汇率有问题 
                " and (FPortCode =" + dbl.sqlString(sPortCode)+" or FPortCode =' ')"+ ") B ON A.FTRADECURY = B.FCURYCODE and A.fbasecury = b.fmarkcury " + //彭鹏 2008.4.1 BUG0000146
                " LEFT JOIN (SELECT * FROM " + pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
                " WHERE FEXRATEDATE = " + dbl.sqlDate(dTheDay) +" and fcheckstate=1"+//edited by zhouxiang MS1378
                " and (FPortCode =" + dbl.sqlString(sPortCode)+" or FPortCode =' ')"+ ") C ON A.FPORTCURY = C.FCURYCODE and A.fbasecury = c.fmarkcury " + //彭鹏 2008.4.1 BUG0000146 modify by fangjiang 2011.04.24 BUG 1768
                //----------------------------------------------  BUG #1638 估值检查汇率有问题  -----------------------------------------------------------------//
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " D ON A.FPortCode = D.FPortCode " +
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Currency") +
                " E ON A.FTRADECURY = E.FCuryCode" +
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Currency") +
                " F ON A.FPORTCURY = F.FCuryCode" +
                // edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                //add by zhangfa 20101019 MS01826    估值检查在当天有汇率的情况下提示不一致    QDV4赢时胜深圳2010年9月30日01_B              
              //  " left join (select FBaseCury,FAssetGroupCode  from TB_SYS_ASSETGROUP) sa on sa.FAssetGroupCode=A.FAssetGroupCode "+
                //------------------------MS01826-----------------------------------------------------------------------
                //modify by zhangfa 20100910  MS01717     基础货币是美元，分红产生1笔交易数据，估值检查，会报错    QDV4交银施罗德2010年9月8日01_B    
                //modify by zhangfa 20100911  MS01716     分红权益信息设置里设置与交易货币不同的币种，不应提示汇率不一致    QDV4交银施罗德2010年09月08日01_B  
                " where  (case when (A.FTRADECURY = A.PFCuryCode or A.PFCuryCode= A.PFCuryCode )then 0  " +  //modify by fangjiang 2011.04.24 BUG 1768
                " else (A.FBaseCuryRate - (case when B.FEXRATE1 is null then 0 else "+
                " B.FEXRATE1 end) / E.FFactor) end) <> 0  " +
                //-----------------------------------------------------------------------------------------------------------------------
              //modify by zhangfa 20101019 MS01826    估值检查在当天有汇率的情况下提示不一致    QDV4赢时胜深圳2010年9月30日01_B  
                " OR (case when(A.FPORTCURY='"+pub.getPortBaseCury(sPortCode)+"' ) then 0 else"+// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                "(A.FPortCuryRate - ( case when C.FEXRATE1 is null then 0 else C.FEXRATE1 end )/ F.FFactor)end) <> 0"; //单亮 2008-4-22 BUG0000166
              //---------------------------MS01826--------------------------------------------------------------------
            	//edited by zhouxiang MS1378
            rs = dbl.openResultSet(strSql); 
            while (rs.next()) {
                if (iIsError == 0) {
                    runStatus.appendValCheckRunDesc(
                        "\r\n        ------------------------------------");
                    runStatus.appendValCheckRunDesc("\r\n        以下交易证券的汇率与汇率表中汇率不一致：");
                }
                runStatus.appendValCheckRunDesc("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                                                "\r\n            交易证券：" + rs.getString("FSECURITYCODE") +
                                                //2008.01.15 添加 蒋锦 添加交易货币的显示 方便查询
                                                "\r\n            交易货币：" + rs.getString("FTRADECURY") +
                                                "\r\n            交易日期：" + rs.getDate("FBargainDate"));
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                            "\r\n            交易证券：" + rs.getString("FSECURITYCODE") +
                            "\r\n            交易货币：" + rs.getString("FTRADECURY") +
                            "\r\n            交易日期：" + rs.getDate("FBargainDate"));
                }
                iIsError++;
                this.sIsError = "false";
                
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                //获取日志信息
				this.checkInfos += "\r\n        以下交易证券的汇率与汇率表中汇率不一致："+
                                   "\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                                   "\r\n            交易证券：" + rs.getString("FSECURITYCODE") +
                                   "\r\n            交易货币：" + rs.getString("FTRADECURY") +
                                   "\r\n            交易日期：" + rs.getDate("FBargainDate");
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            }
        } catch (Exception e) {
            throw new YssException("检查汇率信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }
}











