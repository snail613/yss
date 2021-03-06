package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.ResultSet;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import java.util.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;

/**
 *
 * <p>Title: </p>
 * <p>Description: 检查持仓证券是否有行情</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CheckSecurityMkt
    extends BaseValCheck {
    public CheckSecurityMkt() {
    }

    CtlPubPara ctlPubPara = null;

    public String doCheck(Date dTheDay, String sPortCode) throws Exception {
        String sReturn = "";
        String strSql = "";
        ResultSet rs = null;
        BaseOperDeal baseOperDeal = new BaseOperDeal();
        baseOperDeal.setYssPub(pub);
        int iIsError = 0; //记录出错数据数量
        StringBuffer bufUnChecked = new StringBuffer(); //记录行情没有审核的信息 sunkey 20081207 BugNO:MS00068
        StringBuffer bufRecycle = new StringBuffer(); //记录行情已被删除的信息 sunkey 20081209 BugNO:MS00068
        StringBuffer bufOne = new StringBuffer(); //记录当日无行情的证券信息 sunkey 20081212 BugNO:MS00015
        StringBuffer bufMore = new StringBuffer(); //记录连续N各交易日无行情的证券信息 sunkey 20081212 BugNO:MS00015
        
        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        String logInfoUnCheck = "";//日志信息
        String logInfoRecycle = "";
        String logInfoOne = "";
        String logInfoMore = "";
        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {

            ctlPubPara = new CtlPubPara();
            ctlPubPara.setYssPub(pub);
            int days = ctlPubPara.getMktDayCount(); //获取用户设置的参数检查天数 sunkey 20081204 BugNO:MS00051
            //2008.01.15 修改 蒋锦
            //添加对节假日群字表的左联接，估值日的日期不在节假日群中，且估值当日没有行情时才能确定没有估值日的行情
            //原理 TB_STOCK_SECURITY LEFT JOIN Tb_Para_Security LEFT JOIN Tb_Base_ChildHoliday WHERE Tb_Base_ChildHoliday.FHolidaysCode IS NULL
            strSql =
                "SELECT DISTINCT a.FSecurityCode, d.FSecurityName, d.FCatCode, a.FStorageDate,c.FMktValueDate," +
                " e.FPortCode, e.FPortName,d.FHolidaysCode,b.FCheckState,b.TMktDate " +
                " FROM " + pub.yssGetTableName("TB_STOCK_SECURITY") + " a " +
                " LEFT JOIN (SELECT FSecurityCode, FMktValueDate as TMktDate, FCheckState " + //为了检测没有审核的行情信息，要查询审核状态。 sunkey 20081207 BugNO:MS00068
                " FROM " + pub.yssGetTableName("Tb_Data_MarketValue") +
                " WHERE FMktValueDate = " + dbl.sqlDate(dTheDay) +
//                  " and FCheckState=1) b ON a.FSecurityCode = b.FSecurityCode " +  为了处理没审核的行情信息提示 先将此处修改为如下: sunkey 2008-12-07 BugNO:MS00068
                " ) b ON a.FSecurityCode = b.FSecurityCode " +
                " LEFT JOIN (SELECT MAX(FMktValueDate) AS FMktValueDate, FSecurityCode " +
                " FROM " + pub.yssGetTableName("Tb_Data_MarketValue") +
                " WHERE FMktValueDate <= " + dbl.sqlDate(dTheDay) +
                " and FCheckState=1 GROUP BY FSecurityCode) c ON a.FSecurityCode = c.FSecurityCode " +
                " LEFT JOIN (SELECT FSecurityCode, FSecurityName, FHolidaysCode, FCatCode FROM " +
                pub.yssGetTableName("Tb_Para_Security") +
                " ) d ON a.FSecurityCode = " +
                " d.FSecurityCode " +
                " LEFT JOIN (SELECT FPortCode, FPortName FROM " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " ) e ON a.FPortCode = e.FPortCode " +
                " LEFT JOIN (SELECT FHolidaysCode FROM Tb_Base_ChildHoliday WHERE FDate = " +
                dbl.sqlDate(dTheDay) +
                " AND FCheckState = 1) f ON f.FHolidaysCode = d.FHolidaysCode " +
                " WHERE a.FStorageDate = " + dbl.sqlDate(dTheDay) +
//                  " AND b.FMktValueDate IS NULL " + //将此代码修改为下面的代码  sunkey 20081207 BugNO:MS00068
                //将行情is null修改为为(FcheckState==0 OR FCHECKSTATE IS NULL)，这样可以处理行情没审核的，在下面处理的时候，在判断TMktDate是不是null就OK了
                " AND (b.FCheckState <> 1 OR b.FCheckState IS NULL)" +
                " AND f.FHolidaysCode IS NULL " +
                " AND a.FPortCode = " + dbl.sqlString(sPortCode) +
                " AND " + dbl.sqlSubStr("a.FYearMonth", "5", "2") +
                " <> '00' " +
                " and d.FCatCode<>'FW' and d.FCatCode<>'RE'"; //这里去掉远期证券与回购证券 by leeyu 080721 bug:0000299
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	logInfoUnCheck = "";//日志信息
                logInfoRecycle = "";
                logInfoOne = "";
                logInfoMore = "";
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
                //如果行情日期不为Null，并且是检查单日,并且审核状态不为1,代表有未审核或被删除的行情，直接跳出此条记录进入下一条 sunkey 20081207 BugNO:MS00068
                //取有行情未审核和回收站中的信息
                if (rs.getDate("TMktDate") != null &&
                    rs.getInt("FCheckState") != 1 && days <= 1) {
                    //处理未审核的行情
                    if (rs.getInt("FCheckState") == 0) {
                        bufUnChecked.append("\r\n            组合：" +
                                            rs.getString("FPortCode") + " " +
                                            rs.getString("FPortName") +
                                            "\r\n            证券：" +
                                            rs.getString("FSecurityCode") +
                                            " " +
                                            rs.getString("FSecurityName") +
                                            "\r\n            库存日期：" +
                                            rs.getDate("FStorageDate") +
                                            "\r\n            行情日期：" +
                                            rs.getString("FMktValueDate") + "\r\n ");
                        
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //获取日志信息
						logInfoUnCheck += "\r\n            组合：" +
                        rs.getString("FPortCode") + " " +
                        rs.getString("FPortName") +
                        "\r\n            证券：" +
                        rs.getString("FSecurityCode") +
                        " " +
                        rs.getString("FSecurityName") +
                        "\r\n            库存日期：" +
                        rs.getDate("FStorageDate") +
                        "\r\n            行情日期：" +
                        rs.getString("FMktValueDate") + "\r\n ";
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                        
                        this.sIsError = "false";
                        iIsError++;
                    }
                    //处理回收站中的行情
                    else if (rs.getInt("FCheckState") == 2) {
                        bufRecycle.append("\r\n            组合：" +
                                          rs.getString("FPortCode") + " " +
                                          rs.getString("FPortName") +
                                          "\r\n            证券：" +
                                          rs.getString("FSecurityCode") +
                                          " " +
                                          rs.getString("FSecurityName") +
                                          "\r\n            库存日期：" +
                                          rs.getDate("FStorageDate") +
                                          "\r\n            行情日期：" +
                                          rs.getString("FMktValueDate") + "\r\n ");
                        this.sIsError = "false";
                        iIsError++;
                        
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //获取日志信息
						logInfoRecycle += "\r\n            组合：" +
                        rs.getString("FPortCode") + " " +
                        rs.getString("FPortName") +
                        "\r\n            证券：" +
                        rs.getString("FSecurityCode") +
                        " " +
                        rs.getString("FSecurityName") +
                        "\r\n            库存日期：" +
                        rs.getDate("FStorageDate") +
                        "\r\n            行情日期：" +
                        rs.getString("FMktValueDate") + "\r\n ";
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    }
                    continue;
                }

                //如果没有行情或行情未审核，处理当日或连续N个交易日无行情信息的证券 sunkey 20081212 BugNO:MS00015
//              if(days <= 1){
//              if (rs.getDate("TMktDate") == null || rs.getInt("FCheckState")!=1) {
                //处理当日行情检查，当日是必须无行情的，不必考虑审核状态
                //无行情，检查日期是1天的
                if (days <= 1 && rs.getDate("TMktDate") == null) {
                    bufOne.append("\r\n            组合：" +
                                  rs.getString("FPortCode") +
                                  " " +
                                  rs.getString("FPortName") +
                                  "\r\n            证券：" +
                                  rs.getString("FSecurityCode") +
                                  " " +
                                  rs.getString("FSecurityName") +
                                  "\r\n            库存日期：" +
                                  rs.getDate("FStorageDate") +
                                  "\r\n            行情日期：" +
                                  rs.getString("FMktValueDate") + "\r\n ");
                    this.sIsError = "false";
                    iIsError++;
                    
                    //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    //获取日志信息
					logInfoOne += "\r\n            组合：" +
                    rs.getString("FPortCode") +
                    " " +
                    rs.getString("FPortName") +
                    "\r\n            证券：" +
                    rs.getString("FSecurityCode") +
                    " " +
                    rs.getString("FSecurityName") +
                    "\r\n            库存日期：" +
                    rs.getDate("FStorageDate") +
                    "\r\n            行情日期：" +
                    rs.getString("FMktValueDate") + "\r\n ";
                    //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }
//              }
                //处理连续交易日行情检查
                else if (days > 1) {
                    //当前业务日期推days工作日，如果取到的日期>行情日期
                    //即 业务日期向前推days-1个工作日，如果得到的日期-行情日期>0 ，表示已经连续days个交易日没行情了
                    //（一直无行情或满足行情检查的期限 或者有行情，但行情没有审核的
                    //by sunkey 20081204 BugNo:MS00015
                    if (rs.getDate("FMktValueDate") == null ||
                        YssFun.dateDiff(rs.getDate("FMktValueDate"),
                                        baseOperDeal.getWorkDay(rs.getString(
                                            "FHolidaysCode"), dTheDay,
                        - (days - 1))) > 0) {
                        bufMore.append("\r\n            组合：" +
                                       rs.getString("FPortCode") +
                                       " " +
                                       rs.getString("FPortName") +
                                       "\r\n            证券：" +
                                       rs.getString("FSecurityCode") +
                                       " " +
                                       rs.getString("FSecurityName") +
                                       "\r\n            库存日期：" +
                                       rs.getDate("FStorageDate") +
                                       "\r\n            行情日期：" +
                                       rs.getString("FMktValueDate") + "\r\n ");
                        this.sIsError = "false";
                        iIsError++;
                        
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //获取日志信息
						logInfoMore += "\r\n            组合：" +
                        rs.getString("FPortCode") +
                        " " +
                        rs.getString("FPortName") +
                        "\r\n            证券：" +
                        rs.getString("FSecurityCode") +
                        " " +
                        rs.getString("FSecurityName") +
                        "\r\n            库存日期：" +
                        rs.getDate("FStorageDate") +
                        "\r\n            行情日期：" +
                        rs.getString("FMktValueDate") + "\r\n ";
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    }
                }
//              }
            }
            //如果有错误信息 ，画线 sunkey 20081212 BugNO:MS00015
            if (iIsError != 0) {
                runStatus.appendValCheckRunDesc(
                    "\r\n        ------------------------------------");
            }
            //处理完所有数据之后，如果行情未审核的信息存在，也要提示用户 sunkey 20081207 BugNO:MS00068
            if (bufUnChecked.length() != 0) {
                runStatus.appendValCheckRunDesc("\r\n        以下证券库存当日行情未审核：");
                runStatus.appendValCheckRunDesc(bufUnChecked.toString());
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n        以下证券库存当日行情未审核：");
                	this.writeLog(bufUnChecked.toString());
                }
                
                //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                //获取日志信息
				this.checkInfos += "\r\n        以下证券库存当日行情未审核：" + logInfoUnCheck;
            }
            //处理回收站中的行情信息 sunkey 20081209 BugNO:MS00068
            if (bufRecycle.length() != 0) {
                runStatus.appendValCheckRunDesc(
                    "\r\n        以下证券库存当日行情已被移到回收站：");
                runStatus.appendValCheckRunDesc(bufRecycle.toString());
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n        以下证券库存当日行情已被移到回收站：");
                	this.writeLog(bufRecycle.toString());
                }
                
                //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                //获取日志信息
				this.checkInfos += "\r\n        以下证券库存当日行情已被移到回收站：" + logInfoRecycle;
            }
            //处理当日无行情信息 sunkey 20081212 BugNO:MS00015
            if (bufOne.length() != 0) {
                runStatus.appendValCheckRunDesc("\r\n        以下证券库存无当日行情信息：");
                runStatus.appendValCheckRunDesc(bufOne.toString());
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n        以下证券库存无当日行情信息：");
                	this.writeLog(bufOne.toString());
                }
                
                //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                //获取日志信息
				this.checkInfos += "\r\n        以下证券库存当日行情已被移到回收站：" + logInfoOne;
            }
            //处理连续交易日无行情信息 sunkey 20081212 BugNO:MS00015
            if (bufMore.length() != 0) {
                runStatus.appendValCheckRunDesc("\r\n        以下证券库存至" +
                                                YssFun.formatDate(dTheDay,
                    "yyyy-MM-dd") +
                                                ",\"" + days + "\"个交易日内无行情信息：");
                runStatus.appendValCheckRunDesc(bufMore.toString());
                if (this.sNeedLog.equals("true"))
                {	
                	this.writeLog("\r\n        以下证券库存至" +
                            YssFun.formatDate(dTheDay,
                            "yyyy-MM-dd") +
                                                        ",\"" + days + "\"个交易日内无行情信息：");
                	this.writeLog(bufMore.toString());
                }
                
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                //获取日志信息
				this.checkInfos += "\r\n        以下证券库存至" + YssFun.formatDate(dTheDay,"yyyy-MM-dd") +
                                            ",\"" + days + "\"个交易日内无行情信息：" + logInfoMore;
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            }
            doCheckFWMkt(dTheDay, sPortCode, days);
        } catch (Exception e) {
            throw new YssException("检查证券行情出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }

    /***
     * 处理远期证券行情的检查
     * add by leeyu 080721
     */
    private String doCheckFWMkt(Date dTheDay, String sPortCode, int days) throws
        Exception {
        ResultSet rs = null;
        String sqlStr = "";
        String sReturn = "";
        BaseOperDeal baseOperDeal = new BaseOperDeal(); //日期条件， between .. and sunkey 20081204 BugNo:MS00015
        baseOperDeal.setYssPub(pub);
        int iIsError = 0;
        StringBuffer bufUnChecked = new StringBuffer(); //记录行情没有审核的信息 sunkey 20081207 BugNO:MS00068
        StringBuffer bufRecycle = new StringBuffer(); //记录行情已被删除的信息 sunkey 20081209
        StringBuffer bufOne = new StringBuffer(); //记录当日无行情的证券信息 sunkey 20081212 BugNO:MS00015
        StringBuffer bufMore = new StringBuffer(); //记录连续N各交易日无行情的证券信息 sunkey 20081212 BugNO:MS00015
        
        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        String logInfoUnCheck = "";//业务日志信息
        String logInfoRecycle = "";
        String logInfoOne = "";
        String logInfoMore = "";
        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            sqlStr =
                "SELECT DISTINCT a.FSecurityCode,a.FMarketCode, d.FSecurityName, d.FCatCode, a.FStorageDate, c.FMktValueDate," + //增加了a.fmarketcode 上市代码MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                " e.FPortCode, e.FPortName,b.TMktDate,b.FCheckState " +
                //" FROM " + pub.yssGetTableName("TB_STOCK_SECURITY") + " a " +//证券库存
                //下面在a表里加入上市代码因为远期行情表里的FSecurityCode证券代码里的数据是上市代码 MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                " FROM (select a1.*,a2.FMarketCode from " + pub.yssGetTableName("TB_STOCK_SECURITY") +
                " a1 left join (select * from " + pub.yssGetTableName("Tb_Para_Security") + " ) a2 on a1.fsecuritycode = a2.fsecuritycode) " +
                " a " + //证券库存
                //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                " LEFT JOIN (SELECT FSecurityCode, FMktValueDate as TMktDate,FCheckState " +
                " FROM " + pub.yssGetTableName("Tb_Data_FWMktValue") +
                " WHERE FMktValueDate = " + dbl.sqlDate(dTheDay) +
//                  " and FCheckState=1) b ON a.FSecurityCode = b.FSecurityCode " + //将审核状态为1的条件去掉，这样才能取到行情没有审核的信息，以便提示 sunkey 20081207 BugNO:MS00068
                " ) b ON a.fmarketcode = b.FSecurityCode " + //用a表中的上市代码和远期行情表中的上市代码连接MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                " LEFT JOIN (SELECT MAX(FMktValueDate) AS FMktValueDate, FSecurityCode " +
                " FROM " + pub.yssGetTableName("Tb_Data_FWMktValue") +
                " WHERE FMktValueDate <= " + dbl.sqlDate(dTheDay) +
                " and FCheckState=1 GROUP BY FSecurityCode) c ON a.fmarketcode = c.FSecurityCode " + //用a表中的上市代码和远期行情表中的上市代码连接MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                " LEFT JOIN (SELECT FSecurityCode, FSecurityName, FHolidaysCode, FCatCode FROM " +
                pub.yssGetTableName("Tb_Para_Security") +
                " ) d ON a.FSecurityCode = " +
                " d.FSecurityCode " +
                " LEFT JOIN (SELECT FPortCode, FPortName FROM " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " ) e ON a.FPortCode = e.FPortCode " +
                " LEFT JOIN (SELECT FHolidaysCode FROM Tb_Base_ChildHoliday WHERE FDate = " +
                dbl.sqlDate(dTheDay) +
                " AND FCheckState = 1) f ON f.FHolidaysCode = d.FHolidaysCode " +
                " WHERE a.FStorageDate = " + dbl.sqlDate(dTheDay) +
//                  " AND b.FMktValueDate IS NULL " + //修改条件为 FCheckState=0，因为这个字段是not null的，采用左连接取不到值的时候会默认0，无行情日期在处理逻辑的时候判断 sunkey 20081207 BugNO:MS00068
                //将行情is null修改为为(FcheckState==0 OR FCHECKSTATE IS NULL)，这样可以处理行情没审核的，在下面处理的时候，在判断TMktDate是不是null就OK了
                " AND (b.FCheckState <> 1 OR b.FCheckState IS NULL) " +
                " AND f.FHolidaysCode IS NULL " +
                " AND a.FPortCode = " + dbl.sqlString(sPortCode) +
                " AND " + dbl.sqlSubStr("a.FYearMonth", "5", "2") +
                " <> '00' " +
                " AND d.FCatCode <> 'RE' and d.FCatCode='FW'";
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	logInfoUnCheck = "";//业务日志信息
                logInfoRecycle = "";
                logInfoOne = "";
                logInfoMore = "";
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            	
                //如果行情日期不为Null，并且是检查单日，并且审核状态不为1,代表有未审核或被删除的行情，直接跳出此条记录进入下一条 sunkey 20081207 BugNO:MS00068
                if (rs.getDate("TMktDate") != null &&
                    rs.getInt("FCheckState") != 1 && days <= 1) {
                    //处理未审核的行情
                    if (rs.getInt("FCheckState") == 0) {
                        bufUnChecked.append("\r\n            组合：" +
                                            rs.getString("FPortCode") + " " +
                                            rs.getString("FPortName") +
                                            "\r\n            证券：" +
                                            rs.getString("FSecurityCode") +
                                            " " +
                                            rs.getString("FSecurityName") +
                                            //------------------------------------------------
                                            //加入一个上市代码的提示MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                                            "\r\n            上市代码：" +
                                            rs.getString("FMarketCode") +
                                            //------------------------------------------------
                                            "\r\n            库存日期：" +
                                            rs.getDate("FStorageDate") +
                                            "\r\n            行情日期：" +
                                            rs.getString("FMktValueDate") + "\r\n ");
                        iIsError++;
                        this.sIsError = "false";
                        
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //获取业务日志信息
						logInfoUnCheck += "\r\n            组合：" +
                        rs.getString("FPortCode") + " " +
                        rs.getString("FPortName") +
                        "\r\n            证券：" +
                        rs.getString("FSecurityCode") +
                        " " +
                        rs.getString("FSecurityName") +
                        //------------------------------------------------
                        //加入一个上市代码的提示MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                        "\r\n            上市代码：" +
                        rs.getString("FMarketCode") +
                        //------------------------------------------------
                        "\r\n            库存日期：" +
                        rs.getDate("FStorageDate") +
                        "\r\n            行情日期：" +
                        rs.getString("FMktValueDate") + "\r\n ";
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    }
                    //处理回收站中的行情
                    else if (rs.getInt("FCheckState") == 2) {
                        bufRecycle.append("\r\n            组合：" +
                                          rs.getString("FPortCode") + " " +
                                          rs.getString("FPortName") +
                                          "\r\n            证券：" +
                                          rs.getString("FSecurityCode") +
                                          " " +
                                          rs.getString("FSecurityName") +
                                          //------------------------------------------------
                                          //加入一个上市代码的提示MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                                          "\r\n            上市代码：" +
                                          rs.getString("FMarketCode") +
                                          //------------------------------------------------
                                          "\r\n            库存日期：" +
                                          rs.getDate("FStorageDate") +
                                          "\r\n            行情日期：" +
                                          rs.getString("FMktValueDate") + "\r\n ");
                        iIsError++;
                        this.sIsError = "false";
                        
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //获取业务日志信息
						logInfoRecycle += "\r\n            组合：" +
                        rs.getString("FPortCode") + " " +
                        rs.getString("FPortName") +
                        "\r\n            证券：" +
                        rs.getString("FSecurityCode") +
                        " " +
                        rs.getString("FSecurityName") +
                        //------------------------------------------------
                        //加入一个上市代码的提示MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                        "\r\n            上市代码：" +
                        rs.getString("FMarketCode") +
                        //------------------------------------------------
                        "\r\n            库存日期：" +
                        rs.getDate("FStorageDate") +
                        "\r\n            行情日期：" +
                        rs.getString("FMktValueDate") + "\r\n ";
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    }
                    continue;
                }

                //如果没有行情，处理当日或连续N个交易日无行情信息的证券 sunkey 20081212 BugNO:MS00015
                if (days <= 1 && rs.getDate("TMktDate") == null) {
                    bufOne.append("\r\n            组合：" +
                                  rs.getString("FPortCode") +
                                  " " +
                                  rs.getString("FPortName") +
                                  "\r\n            证券：" +
                                  rs.getString("FSecurityCode") +
                                  " " +
                                  rs.getString("FSecurityName") +
                                  //------------------------------------------------
                                  //加入一个上市代码的提示MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                                  "\r\n            上市代码：" +
                                  rs.getString("FMarketCode") +
                                  //------------------------------------------------
                                  "\r\n            远期库存日期：" +
                                  rs.getDate("FStorageDate") +
                                  "\r\n            远期行情日期：" +
                                  rs.getString("FMktValueDate") + "\r\n ");
                    iIsError++;
                    this.sIsError = "false";
                    
                    //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    //获取业务日志信息
					logInfoOne += "\r\n            组合：" +
                    rs.getString("FPortCode") +
                    " " +
                    rs.getString("FPortName") +
                    "\r\n            证券：" +
                    rs.getString("FSecurityCode") +
                    " " +
                    rs.getString("FSecurityName") +
                    //------------------------------------------------
                    //加入一个上市代码的提示MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                    "\r\n            上市代码：" +
                    rs.getString("FMarketCode") +
                    //------------------------------------------------
                    "\r\n            远期库存日期：" +
                    rs.getDate("FStorageDate") +
                    "\r\n            远期行情日期：" +
                    rs.getString("FMktValueDate") + "\r\n ";
                    //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (days > 1) {
                    //当前业务日期推days工作日，如果取到的日期>行情日期
                    //即 业务日期向前推days-1个工作日，如果得到的日期-行情日期>0 ，表示已经连续days个交易日没行情了
                    //by sunkey 20081204 BugNo:MS00051
                    if (rs.getDate("FMktValueDate") == null ||
                        YssFun.dateDiff(rs.getDate("FMktValueDate"),
                                        baseOperDeal.getWorkDay(rs.getString(
                                            "FHolidaysCode"), dTheDay,
                        - (days - 1))) > 0) {
                        bufMore.append("\r\n            组合：" +
                                       rs.getString("FPortCode") + " " +
                                       rs.getString("FPortName") +
                                       "\r\n            证券：" +
                                       rs.getString("FSecurityCode") +
                                       " " +
                                       rs.getString("FSecurityName") +
                                       //------------------------------------------------
                                       //加入一个上市代码的提示MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                                       "\r\n            上市代码：" +
                                       rs.getString("FMarketCode") +
                                       //------------------------------------------------
                                       "\r\n            远期库存日期：" +
                                       rs.getDate("FStorageDate") +
                                       "\r\n            远期行情日期：" +
                                       rs.getString("FMktValueDate") + "\r\n ");
                        iIsError++;
                        this.sIsError = "false";
                        
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //获取业务日志信息
						logInfoMore += "\r\n            组合：" +
                        rs.getString("FPortCode") + " " +
                        rs.getString("FPortName") +
                        "\r\n            证券：" +
                        rs.getString("FSecurityCode") +
                        " " +
                        rs.getString("FSecurityName") +
                        //------------------------------------------------
                        //加入一个上市代码的提示MS00159 QDV4南方2009年1月5日17_B 2009.02.11方浩
                        "\r\n            上市代码：" +
                        rs.getString("FMarketCode") +
                        //------------------------------------------------
                        "\r\n            远期库存日期：" +
                        rs.getDate("FStorageDate") +
                        "\r\n            远期行情日期：" +
                        rs.getString("FMktValueDate") + "\r\n ";
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    }
                }
            }

            //如果有错误信息 ，画线 sunkey 20081212 BugNO:MS00015
            if (iIsError != 0) {
                runStatus.appendValCheckRunDesc(
                    "\r\n        ------------------------------------");
            }

            //处理完所有数据之后，如果行情未审核的信息存在，也要提示用户 sunkey 20081207 BugNO:MS00068
            if (bufUnChecked.length() != 0) {
                runStatus.appendValCheckRunDesc("\r\n        以下证券库存当日远期行情未审核：");
                runStatus.appendValCheckRunDesc(bufUnChecked.toString());
                if (this.sNeedLog.equals("true"))
                {	
                	this.writeLog("\r\n        以下证券库存当日远期行情未审核：");
                	this.writeLog(bufUnChecked.toString());
                }
                
                //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                //获取业务日志信息
				this.checkInfos += "\r\n        以下证券库存当日远期行情未审核：" + logInfoUnCheck;
            }
            //处理回收站中的行情信息 sunkey 20081209
            if (bufRecycle.length() != 0) {
                runStatus.appendValCheckRunDesc(
                    "\r\n        以下证券库存当日远期行情已被移到回收站：");
                runStatus.appendValCheckRunDesc(bufRecycle.toString());if (this.sNeedLog.equals("true"))
                if (this.sNeedLog.equals("true"))
                {	
                	this.writeLog("\r\n        以下证券库存当日远期行情已被移到回收站：");
                	this.writeLog(bufRecycle.toString());
                }
                
                //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                //获取业务日志信息
				this.checkInfos += "\r\n        以下证券库存当日远期行情已被移到回收站：" + logInfoRecycle;
            }
            //处理当日无行情信息 sunkey 20081212 BugNO:MS00015
            if (bufOne.length() != 0) {
                runStatus.appendValCheckRunDesc("\r\n        以下证券库存无当日远期行情信息：");
                runStatus.appendValCheckRunDesc(bufOne.toString());
                if (this.sNeedLog.equals("true"))
                {	
                	this.writeLog("\r\n        以下证券库存无当日远期行情信息：");
                	this.writeLog(bufOne.toString());
                }
                
                //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                //获取业务日志信息
				this.checkInfos += "\r\n        以下证券库存当日远期行情未审核：" + logInfoOne;
            }
            //处理连续交易日无行情信息 sunkey 20081212 BugNO:MS00015
            if (bufMore.length() != 0) {
                runStatus.appendValCheckRunDesc("\r\n        以下证券库存至" +
                                                YssFun.formatDate(dTheDay,
                    "yyyy-MM-dd") +
                                                ",\"" + days +
                                                "\"个交易日内无远期行情信息：");
                runStatus.appendValCheckRunDesc(bufMore.toString());
                if (this.sNeedLog.equals("true"))
                {	
                	this.writeLog("\r\n        以下证券库存至" +
                            YssFun.formatDate(dTheDay,
                            "yyyy-MM-dd") +
                                                        ",\"" + days +
                                                        "\"个交易日内无远期行情信息：");
                	this.writeLog(bufMore.toString());
                }
                
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                //获取业务日志信息
				this.checkInfos += "\r\n        以下证券库存至" + YssFun.formatDate(dTheDay,"yyyy-MM-dd") +
                                   ",\"" + days + "\"个交易日内无远期行情信息：" + logInfoMore;
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            }

        } catch (Exception ex) {
            throw new YssException("检查远期证券行情出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }
}
