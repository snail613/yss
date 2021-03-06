package com.yss.main.operdeal.valcheck;

import java.util.Date;

import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssException;
import com.yss.util.YssUtil;

import java.sql.ResultSet;
import com.yss.util.YssFun;

/**
 * 文件功能: 检查股票是否卖空
 * 创建人：单亮
 * 创建时间：2008-7-1
 */
public class CheckSecurityIsNull
    extends BaseValCheck {
    public CheckSecurityIsNull() {
    }

    public String doCheck(Date dTheDay, String sPortCode) throws Exception {
        String sReturn = "";
        ResultSet rs = null;
        String strSql = "";
        int iIsError = 0; //记录出错数据数量
    	String para = "";
    	CtlPubPara pubPara = new CtlPubPara();
    	pubPara.setYssPub(pub);
    	para = pubPara.getEQCheckMode();

    	if(para.equalsIgnoreCase("0")){
		//检查股票持仓不包含未交割数量
    		sReturn = doCheck1(dTheDay,sPortCode);
    		return sReturn;
    	}
    	

        try {
            //2008.08.25 蒋锦 修改 修改库存表和交易子表 LEFT JOIN 关系 BUG：0000430
            strSql = "select a.fsecuritycode," +
                "  d.FSecurityName," +
                "  a.fportcode," +
                "  e.FPortName," +
                "  a.FBargainDate," +
                "  CASE" +
                "  WHEN b.FStorageAmount IS NULL THEN" +
                "  0" +
                "  ELSE" +
                "  b.FStorageAmount" +
                "  END - a.FTradeAmount as Amount" +//edit by songjie 2011.04.14 BUG QDV4华泰证券2011年04月06日01_B
              //---delete by songjie 2011.04.14 BUG QDV4华泰证券2011年04月06日01_B---//
//                "  c.FAmountInd" +
//                "  from (select FSecurityCode," +
//                "  FBargainDate," +
//                "  sum(FTradeAmount) as FTradeAmount," +
//                "  FTradeTypeCode," +
//                "  FPortCode" +
//                "  FROM " + pub.yssGetTableName("Tb_Data_SubTrade") +
//                "  where FBargainDate = " + dbl.sqlDate(dTheDay) +
//                "  and fportcode = " + dbl.sqlString(sPortCode) +
//                "  and fcheckstate = 1" + //BugNo:0000434 edit by jc
//                "  group by FBargainDate, FSecurityCode, FTradeTypeCode, FPortCode) a" +
                //---delete by songjie 2011.04.14 BUG QDV4华泰证券2011年04月06日01_B---//
                //---add by songjie 2011.04.14 BUG QDV4华泰证券2011年04月06日01_B---//
                " from ( select FSecurityCode,FBargainDate,sum(FTradeAmount) as FTradeAmount,FPortCode " + 
                " from (select a1.FSecurityCode,a1.FBargainDate,a1.FTradeAmount * c.famountind as FTradeAmount, " + 
                " a1.FPortCode,a1.FTradeTypeCode from (select FSecurityCode,FBargainDate,sum(FTradeAmount) as FTradeAmount," + 
                " FTradeTypeCode,FPortCode FROM " + pub.yssGetTableName("Tb_Data_SubTrade") + 
                " where FBargainDate = " + dbl.sqlDate(dTheDay) +
                " and fportcode = " + dbl.sqlString(sPortCode) + " and fcheckstate = 1 " + 
                " group by FBargainDate, FSecurityCode, FTradeTypeCode, FPortCode ) a1 " + 
                " left join (select FTradeTypeCode, FAmountInd from Tb_Base_TradeType) c " + 
                " on a1.FTradeTypeCode = c.ftradetypecode) f group by FSecurityCode,FBargainDate,FPortCode ) a " + 
                //---add by songjie 2011.04.14 BUG QDV4华泰证券2011年04月06日01_B---//
                "  LEFT JOIN (select fsecuritycode, FStorageAmount, fportcode" +
                "  FROM " + pub.yssGetTableName("Tb_Stock_Security") +
                "  where fportcode = " + dbl.sqlString(sPortCode) +
                "  and fstoragedate = " + dbl.sqlDate(YssFun.addDay(dTheDay, -1)) + ") b" +
                "  on a.fsecuritycode = b.FSecurityCode" +
                //---delete by songjie 2011.04.14 BUG QDV4华泰证券2011年04月06日01_B---//
//                "  left join (" +
//                "  select FTradeTypeCode,FAmountInd from " + pub.yssGetTableName("Tb_Base_TradeType") +
//                "  )c on a.FTradeTypeCode =  c.FTradeTypeCode " +
                //---delete by songjie 2011.04.14 BUG QDV4华泰证券2011年04月06日01_B---//
                "  left join (" +
                "  select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") +
                "  )d on a.fsecuritycode = d.FSecurityCode" +
                "  left join (" +
                "  select FPortCode,FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                "  )e on a.FPortCode = e.FPortCode" +
//                "  WHERE c.FAmountInd=-1" +//delete by songjie 2011.04.14 BUG QDV4华泰证券2011年04月06日01_B
                //edit by songjie 2011.04.14 BUG QDV4华泰证券2011年04月06日01_B
                "  WHERE CASE WHEN b.FStorageAmount IS NULL THEN 0 ELSE b.FStorageAmount END " +
                /**shashijie 2011.05.05  BUG1652估值检查时，当天买入卖出，结果估值检查提示卖空 */
//                " + a.FTradeAmount < 0";
            	" + a.FTradeAmount < 0";//modified by yeshenghong BUG4430 20120521
            	/**end*/
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (iIsError == 0) {
                    runStatus.appendValCheckRunDesc(
                        "\r\n        ------------------------------------");
                    runStatus.appendValCheckRunDesc("\r\n            以下证券库存已卖空：");
                    
                    //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    //获取业务日志信息
					this.checkInfos += "\r\n            以下证券库存已卖空：";
                }
                runStatus.appendValCheckRunDesc("\r\n            组合：" + rs.getString("fportcode") + " " + rs.getString("FPortName") +
                                                "\r\n            证券：" + rs.getString("fsecuritycode") + " " + rs.getString("FSecurityName") +
                                                "\r\n            日期：" + rs.getDate("FBargainDate"));
                
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                //获取业务日志信息
				this.checkInfos += "\r\n            组合：" + rs.getString("fportcode") + " " + rs.getString("FPortName") +
               					   "\r\n            证券：" + rs.getString("fsecuritycode") + " " + rs.getString("FSecurityName") +
                                   "\r\n            日期：" + rs.getDate("FBargainDate");
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
                iIsError++;
                this.sIsError = "false";
            }

        } catch (Exception e) {
            throw new YssException("检查股票是否卖空出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }


    public String doCheck1(Date dTheDay, String sPortCode) throws YssException{
    	 String sReturn = "";
         ResultSet rs = null,rs1=null;
         String strSql = "",strSql1="";
         int iIsError = 0; //记录出错数据数量
         
         String msg="";
    	try{
    		strSql = "select a.fsecuritycode," +
            "  d.FSecurityName," +
            "  a.fportcode," +
            "  e.FPortName," +
            "  a.FBargainDate," +
           "  c.famountind," +
            "  CASE" +
            "  WHEN b.FStorageAmount IS NULL THEN" +
            "  0" +
            "  ELSE" +
            "  b.FStorageAmount" +
            "  END  as FStorageAmount," + //前一日库存数量
            " a.FTradeAmount as Amount" + 
            //------------------------------------------------//
            "  from (select FSecurityCode," +
            "  FBargainDate," +
            "  sum(FTradeAmount) as FTradeAmount," +
            "  FTradeTypeCode," +
            "  FPortCode" +
            "  FROM " + pub.yssGetTableName("Tb_Data_SubTrade") +
            "  where FBargainDate = " + dbl.sqlDate(dTheDay) +
            "  and fportcode = " + dbl.sqlString(sPortCode) +
            "  and fcheckstate = 1" + 
            "  group by FBargainDate, FSecurityCode, FTradeTypeCode, FPortCode) a" +//根据实际结算日期进行计算交割数量
            //-------------------------------------------------//
            "  LEFT JOIN (select fsecuritycode, FStorageAmount, fportcode" +
            "  FROM " + pub.yssGetTableName("Tb_Stock_Security") +
            "  where fportcode = " + dbl.sqlString(sPortCode) +
            "  and fstoragedate = " + dbl.sqlDate(YssFun.addDay(dTheDay, -1)) + ") b" +
            "  on a.fsecuritycode = b.FSecurityCode" +
            //------------------------------------------------//
            "  left join (" +
            "  select FTradeTypeCode,FAmountInd from " + pub.yssGetTableName("Tb_Base_TradeType") +
            "  where fcheckstate=1 )c on a.FTradeTypeCode =  c.FTradeTypeCode " +
            //-----------------------------------------------//
            "  left join (" +
            "  select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") +
            "  where fcheckstate=1)d on a.fsecuritycode = d.FSecurityCode" +
            //-----------------------------------------------//
            "  left join (" +
            "  select FPortCode,FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") +
           "  where fcheckstate=1)e on a.FPortCode = e.FPortCode"
           +" where famountind=-1";
            //----------------------------------------------//
    		
    		
    		

    	    rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
			/***************************************************************
           	 * 用昨日证券库存和当日证券流出库存进行比较
           	 * 1. 大于0 ，库存减去未交割部分 和 当日流出部分
           	 * 2. 小于0，提示卖空
           	 */ 
            	
           	if(rs.getDouble("FStorageAmount") - rs.getDouble("Amount")>=0){
               	strSql1 = " select b2.fsecuritycode as fsecuritycode, sum(b2.FTradeAmount) as Amount from"+
               	          " (select FTradeTypeCode,FAmountInd from Tb_Base_TradeType where FAmountInd=1 and fcheckstate=1)b1"+
               	          " left join (select * from " + pub.yssGetTableName("tb_data_subtrade") + " where fcheckstate=1 and fportcode="+dbl.sqlString(sPortCode)+
               	          " and FBargainDate <" + dbl.sqlDate(dTheDay) +" and ffactsettledate>"+dbl.sqlDate(dTheDay)+
               	          " and fsecuritycode = "+dbl.sqlString(rs.getString("fsecuritycode"))+
               	          " )b2 on b1.FTradeTypeCode=b2.FTradeTypeCode where b2.FTradeAmount<>0"+
               	          " group by b2.fsecuritycode";
               	
               	rs1 = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
               	if(rs1.next()){
               		if (rs.getDouble("FStorageAmount") - rs.getDouble("Amount")- rs1.getDouble("Amount")>=0 ){//BUG4430 modified by yeshenghong 20120521
               			continue;
               		}else{
               			msg="\r\n           【注意：证券库存已卖空，还有"+YssFun.formatNumber(rs1.getDouble("Amount"), "###,###")+"股未交割过户】";
               		}
               	}else {
               		continue;
               	}
               }
           	
                if (iIsError == 0) {
                    runStatus.appendValCheckRunDesc(
                        "\r\n        ------------------------------------");
                    runStatus.appendValCheckRunDesc("\r\n            以下证券库存已卖空：");
                }
                
                
                
                
                runStatus.appendValCheckRunDesc("\r\n            组合：" + rs.getString("fportcode") + " " + rs.getString("FPortName") +
                                                "\r\n            证券：" + rs.getString("fsecuritycode") + " " + rs.getString("FSecurityName") +
                                                "\r\n            日期：" + YssFun.toSqlDate(dTheDay)+msg);
                iIsError++;
                this.sIsError = "false";
                dbl.closeResultSetFinal(rs1);
            }
    		
    	} catch (Exception e) {
            throw new YssException("检查股票是否卖空出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }
}
