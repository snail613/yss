package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.ResultSet;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/**
 * @Description:检查除权股票行情是否有效
 * @author  guolongchao 20111126 STORY 1850 QDV4嘉实2011年11月09日01_A代码开发 
 * 当原先持仓的那只股票【没有行情】或【有行情，但是“行情数据”表中的FMarketStatus字段不为ACTV时】，
 * 在估值检查时都需要提示“股票在除权日停牌，请检查行情”
 */
public class CheckValidityMarket extends BaseValCheck 
{
	public CheckValidityMarket() 
	{
	}

	public String doCheck(Date dTheDay, String sPortCode) throws Exception 
	{
		 String sReturn = "";
		 String strSql = "";
		 ResultSet rs = null;
		 int iIsError = 0; //记录出错数据数量
		 String rightFsecuritycode="";//权益证券代码
		 String marketFsecuritycode="";//行情证券代码
		 
		 boolean bIsMarketValuePub = Boolean.valueOf((String) pub.getHtPubParams().get("marketvalue")).booleanValue(); // 获取PUB中参数的值 MS00131
		 try 
		 {
//			 strSql="select a.fsecuritycode rightfsecuritycode,b.fsecuritycode marketfsecuritycode" +
//			 		"       ,b.FMktValueDate ,c.fportname,d.FSecurityName from "+
//                    " (  select fsecuritycode,FPortcode from " + pub.yssGetTableName("tb_data_rightsissue") +  //配股权益
//                    "   where fexrightdate = " + dbl.sqlDate(dTheDay) +" AND FPortcode = " + dbl.sqlString(sPortCode)+ 
//                    "  union  select fsecuritycode,FPortcode  from " + pub.yssGetTableName("tb_data_dividend") + //股票分红
//                    " where fdividenddate = " + dbl.sqlDate(dTheDay) +" AND FPortcode = " + dbl.sqlString(sPortCode)+    
//                    "  union  select fssecuritycode,FPortcode from " + pub.yssGetTableName("tb_data_bonusshare") + //送股权益
//                    " where fexrightdate = " + dbl.sqlDate(dTheDay) +" AND FPortcode = " + dbl.sqlString(sPortCode)+  
//                    " ) a "+ 
//                    "  left join " +
//                    "  (select  * from "+(bIsMarketValuePub ? "Tb_Base_MarketValue" : pub.yssGetTableName("Tb_Data_MarketValue"))+
//                    "    where FMktValueDate = "+ dbl.sqlDate(dTheDay) +" AND FPortcode = " + dbl.sqlString(sPortCode)+  " and FMarketStatus='ACTV' " +
//                    "  ) b "+
//                    "  on a.fsecuritycode=b.fsecuritycode and a.FPortcode=b.FPortcode"+
//			        "  left join (select fportcode,fportname from "+pub.yssGetTableName("tb_para_portfolio")+" ) c on c.fportcode=a.fportcode "+
//                    "  left join  (select FSecurityCode, FSecurityName from "+pub.yssGetTableName("tb_para_security")+" ) d on d.fsecuritycode=a.fsecuritycode ";
			 
			 strSql="select a.fsecuritycode rightfsecuritycode,b.fsecuritycode marketfsecuritycode" +
		 		"       ,b.FMktValueDate ,c.fportname,d.FSecurityName from "+
             " (  select fsecuritycode,FPortcode from " + pub.yssGetTableName("tb_data_rightsissue") +  //配股权益
             "   where fcheckstate=1 and fexrightdate = " + dbl.sqlDate(dTheDay)+ 
             "  union  select fsecuritycode,FPortcode  from " + pub.yssGetTableName("tb_data_dividend") + //股票分红
             " where fcheckstate=1 and fdividenddate = " + dbl.sqlDate(dTheDay) +    
             "  union  select fssecuritycode,FPortcode from " + pub.yssGetTableName("tb_data_bonusshare") + //送股权益
             " where fcheckstate=1 and fexrightdate = " + dbl.sqlDate(dTheDay) +  
             " ) a "+ 
             "  left join " +
             "  (select  * from "+(bIsMarketValuePub ? "Tb_Base_MarketValue" : pub.yssGetTableName("Tb_Data_MarketValue"))+
             "    where FMktValueDate = "+ dbl.sqlDate(dTheDay) +" and FMarketStatus='ACTV'  and fcheckstate=1" +
             "  ) b "+
             "  on a.fsecuritycode=b.fsecuritycode "+
		     "  left join (select fportcode,fportname from "+pub.yssGetTableName("tb_para_portfolio")+" ) c on c.fportcode="+dbl.sqlString(sPortCode)+
             "  left join  (select FSecurityCode, FSecurityName from "+pub.yssGetTableName("tb_para_security")+" ) d on d.fsecuritycode=a.fsecuritycode " +
             " join (select distinct fsecuritycode from " + pub.yssGetTableName("Tb_Stock_Security") + " where " +
             " fstoragedate = " + dbl.sqlDate(dTheDay) +
             " and fportcode = " + dbl.sqlString(sPortCode) + ") e on e.fsecuritycode = a.fsecuritycode ";
			 //modified by yeshenghong 20120420 BUG4214
		       rs = dbl.queryByPreparedStatement(strSql); 
		       while (rs.next()) 
		       {
		    	   rightFsecuritycode=rs.getString("rightfsecuritycode");
		    	   marketFsecuritycode=rs.getString("marketfsecuritycode");
		    	   if((rightFsecuritycode!=null&&rightFsecuritycode.trim().length()>0)&&(marketFsecuritycode==null||marketFsecuritycode.trim().length()==0))
		    	   {
		    		   if (iIsError == 0) 
			           {
			               runStatus.appendValCheckRunDesc("\r\n        ------------------------------------");
			               runStatus.appendValCheckRunDesc("\r\n            股票在除权日停牌，请检查行情信息：         ");
			           }
			           runStatus.appendValCheckRunDesc("\r\n            组合：" + sPortCode + " " + rs.getString("FPortName") +
			                                           "\r\n            行情日期：" + YssFun.formatDate(dTheDay, "yyyy-MM-dd") +
			                                           "\r\n            股票代码：" + rightFsecuritycode + " " + rs.getString("FSecurityName"));
			           if (this.sNeedLog.equals("true"))
			           {
			                	this.writeLog("\r\n            组合：" + sPortCode + " " + rs.getString("FPortName") +
			                                  "\r\n            行情日期：" + YssFun.formatDate(dTheDay, "yyyy-MM-dd") +
			                                  "\r\n            股票代码：" + rightFsecuritycode + " " + rs.getString("FSecurityName"));
			           }
			           iIsError++;
			           this.sIsError = "false";
			           
			           //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					   //获取日志信息
			           this.checkInfos += "\r\n            股票在除权日停牌，请检查行情信息：         " +
			           					  "\r\n            组合：" + sPortCode + " " + rs.getString("FPortName") +
			           					  "\r\n            行情日期：" + YssFun.formatDate(dTheDay, "yyyy-MM-dd") +
			           					  "\r\n            股票代码：" + rightFsecuritycode + " " + rs.getString("FSecurityName");
			           //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		    	   }		          
		        }
		   } 
		   catch (Exception e) 
		   {
		       throw new YssException("检查除权股票行情是否有效出错！", e);
		   } 
		   finally 
		   {
		       dbl.closeResultSetFinal(rs);
		   }
		   return sReturn;
     }
}
