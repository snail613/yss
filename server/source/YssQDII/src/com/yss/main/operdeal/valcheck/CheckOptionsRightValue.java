package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 检查内在估值法估值股票期权时，行权日是否有行权价值
 * @author xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
 *
 */
public class CheckOptionsRightValue extends BaseValCheck{

	public CheckOptionsRightValue() {
		super();
	}
	/**
	 * 入口方法
	 * @param dTheDay 日期
	 * @param sPortCodes 组合代码
	 */
	public String doCheck(Date dTheDay, String sPortCodes) throws Exception {
		String result = "";
		try{
			doCheckOptionsRightValue(dTheDay,sPortCodes);//检查内在估值法估值股票期权时，行权日是否有行权价值的具体方法
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		return result;
	}
	/**
	 * 检查内在估值法估值股票期权时，行权日是否有行权价值的具体方法
	 * @param theDay 日期
	 * @param portCodes 组合代码
	 * @throws YssException 
	 */
	private void doCheckOptionsRightValue(Date theDay, String portCodes) throws YssException {
		ResultSet rs = null;
	    StringBuffer buff = null;
	    int iIsError = 0; //记录出错数据数量
        boolean analy1;
        boolean analy2;
        String strYearMonth = "";
        double dLastAmount = 0;//最后数量
        double dMarketPrice = 0;//行情价格
        String sMarketPriceCode = "";//行情价格字段
		try{
       	 	//分析代码
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            
            strYearMonth = YssFun.left(YssFun.formatDate(theDay), 4) + "00";//赋值
            
			buff = new StringBuffer();
			/**
			 * 逻辑：昨日库存表关联期权信息表条件行权日为操纵日期的期权关联估值方法-内在估值法，关联行情表取出行情估值方法
			 * 关联综合业务表（排除当天的放弃行权数据）
			 */
			buff.append(" select a.*,op.foptioncode,op.foptionName,op.ftradetypecode,op.ftsecuritycode,op.fexerciseprice,b.FMktPriceCode,");
			buff.append(" market2.fycloseprice,market2.fopenprice,market2.ftopprice,market2.flowprice,market2.fclosingprice,");
			buff.append(" market2.faverageprice,d.* from ").append(pub.yssGetTableName("tb_stock_security")).append(" a ");//库存表
			buff.append(" join (select e.* from ").append(pub.yssGetTableName("tb_para_optioncontract"));//期权信息设置表
			buff.append(" e join (select * from ").append(pub.yssGetTableName("tb_para_security"));
			buff.append(" where FCheckState = 1 and FSubCatCode = 'FP02') f on e.foptioncode = f.fsecuritycode");
			buff.append(" where e.FCheckState = 1 and FExpiryDate = ").append(dbl.sqlDate(theDay));
			buff.append(" ) op on a.fsecuritycode = op.FOptionCode");
			buff.append(" join (select li.FLinkCode, li.FMTVCode, me.fmktsrccode, me.fmktpricecode from ");
			buff.append(pub.yssGetTableName("Tb_Para_MTVMethodLink"));//估值方法链接表
			buff.append(" li join (select * from ").append(pub.yssGetTableName("tb_para_mtvmethod"));//估值方法表
			buff.append(" where FCheckState = 1) me on li.fmtvcode = me.fmtvcode");
			buff.append(" where li.FCheckState = 1) b on a.Fsecuritycode = b.FLinkCode");
			buff.append(" left join (select m3.* from (select max(FMktValueDate) as FMktValueDate,FSecurityCode,FMktSrcCode from ");
			buff.append(pub.yssGetTableName("Tb_Data_MarketValue"));//行情表
			buff.append(" where FCheckState = 1 and FMktValueDate <= ").append(dbl.sqlDate(theDay));
			buff.append(" group by FSecurityCode, FMktSrcCode) m4 ");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("Tb_Data_MarketValue"));//行情表
			buff.append(" where FCheckState = 1) m3 on m4.FSecurityCode = m3.FSecurityCode");
			buff.append(" and m4.FMktValueDate = m3.FMktValueDate and m4.FMktSrcCode = m3.Fmktsrccode) market2 ");
			buff.append(" on op.fTsecuritycode = market2.FSecurityCode and market2.FMktSrcCode = b.FMktSrcCode");
			buff.append(" left join (select sum(c.FAmount) as FAmount,c.fsecuritycode,c.fportcode");
            buff.append(analy1 == true ? ",c.fanalysiscode1 " : "");
            buff.append(analy2 == true ? ",c.fanalysiscode2" : "");
			buff.append(" from ").append(pub.yssGetTableName("tb_data_integrated"));//综合业务表
			buff.append(" c where FCheckState = 1 and FPortCode in (").append(this.operSql.sqlCodes(portCodes)).append(")");
			buff.append(" and FExchangedate = ").append(dbl.sqlDate(theDay));
			buff.append(" and c.ftradetypecode <> '34FP'");
			buff.append(" group by c.fsecuritycode,c.fportcode ");
            buff.append(analy1 == true ? ",c.fanalysiscode1 " : "");
            buff.append(analy2 == true ? ",c.fanalysiscode2" : "");
			buff.append(") d");
			buff.append(" on a.fsecuritycode = d.fsecuritycode and a.fportcode = d.fportcode ");
            buff.append(analy1 == true ? " and a.fanalysiscode1 = d.fanalysiscode1 " : "");
            buff.append(analy2 == true ? " and a.fanalysiscode2 = d.fanalysiscode2 " : "");
            buff.append(" where a.fstoragedate = ").append(dbl.sqlDate(YssFun.addDay(theDay,-1)));
            buff.append(" and a.fyearmonth <> ").append(dbl.sqlString(strYearMonth));
            buff.append(" and a.fportcode in( ").append(this.operSql.sqlCodes(portCodes)).append(")");
            
            rs = dbl.queryByPreparedStatement(buff.toString()); //modify by fangjiang 2011.08.14 STORY #788
            while(rs.next()){
            	dLastAmount = YssD.add(rs.getDouble("FStorageAmount"),rs.getDouble("FAmount"));//昨日库存与当天交易总量
            	sMarketPriceCode = rs.getString("FMktPriceCode");//标的证券的行情字段，即用什么行情估值
            	dMarketPrice = rs.getDouble(sMarketPriceCode);//行情
            	if(dLastAmount > 0&&rs.getString("ftradetypecode").equalsIgnoreCase("CALL")){//买入认购状态
            		if(YssD.sub(dMarketPrice,rs.getDouble("fexerciseprice")) > 0){//标的证券行情与行权价之差
            			if (iIsError == 0) {
                            runStatus.appendValCheckRunDesc(
                                "\r\n        ------------------------------------");
                            runStatus.appendValCheckRunDesc(
                                "\r\n        以下检查股票期权行权日是否有行权价值：");
                            
                            //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            //获取业务日志信息
							this.checkInfos += "\r\n        以下检查股票期权行权日是否有行权价值：";
                        }
                        runStatus.appendValCheckRunDesc("\r\n            组合：" +
                                                        rs.getString("FPortCode") +
                                                        "\r\n            股票期权代码：" +
                                                        rs.getString("FSecurityCode") +
                                                        "\r\n            股票期权名称：" +
                                                        rs.getString("foptionName") +
                                                        "\r\n            行权日有行权价值！");
                        
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //获取业务日志信息
						this.checkInfos += "\r\n            组合：" + rs.getString("FPortCode") +
                        "\r\n            股票期权代码：" + rs.getString("FSecurityCode") +
                        "\r\n            股票期权名称：" + rs.getString("foptionName") +
                        "\r\n            行权日有行权价值！";
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                        
                        if (this.sNeedLog.equals("true"))
                        {
                        	this.writeLog("\r\n            组合：" +
                                    rs.getString("FPortCode") +
                                    "\r\n            股票期权代码：" +
                                    rs.getString("FSecurityCode") +
                                    "\r\n            股票期权名称：" +
                                    rs.getString("foptionName") +
                                    "\r\n            行权日有行权价值！");
                        }
            		}
            	}else if(dLastAmount < 0&&rs.getString("ftradetypecode").equalsIgnoreCase("CALL")){//卖出认购状态
            		if(YssD.sub(dMarketPrice,rs.getDouble("fexerciseprice")) < 0){
            			if (iIsError == 0) {
                            runStatus.appendValCheckRunDesc(
                                "\r\n        ------------------------------------");
                            runStatus.appendValCheckRunDesc(
                                "\r\n        以下检查股票期权行权日是否有行权价值：");
                            
                            //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            //获取业务日志信息
							this.checkInfos += "\r\n        以下检查股票期权行权日是否有行权价值：";
                        }
                        runStatus.appendValCheckRunDesc("\r\n            组合：" +
                                                        rs.getString("FPortCode") +
                                                        "\r\n            股票期权代码：" +
                                                        rs.getString("FSecurityCode") +
                                                        "\r\n            股票期权名称：" +
                                                        rs.getString("foptionName") +
                                                        "\r\n            行权日有行权价值！");
                        
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //获取业务日志信息
						this.checkInfos += "\r\n            组合：" + rs.getString("FPortCode") +
                        					"\r\n            股票期权代码：" + rs.getString("FSecurityCode") +
                        					"\r\n            股票期权名称：" + rs.getString("foptionName") +
                        					"\r\n            行权日有行权价值！";
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                        
                        if (this.sNeedLog.equals("true"))
                        {
                        	this.writeLog("\r\n            组合：" +
                                    rs.getString("FPortCode") +
                                    "\r\n            股票期权代码：" +
                                    rs.getString("FSecurityCode") +
                                    "\r\n            股票期权名称：" +
                                    rs.getString("foptionName") +
                                    "\r\n            行权日有行权价值！");
                        }
            		}
            	}else if(dLastAmount > 0&&rs.getString("ftradetypecode").equalsIgnoreCase("PUT")){//买入认沽状态
            		if(YssD.sub(dMarketPrice,rs.getDouble("fexerciseprice")) < 0){
            			if (iIsError == 0) {
                            runStatus.appendValCheckRunDesc(
                                "\r\n        ------------------------------------");
                            runStatus.appendValCheckRunDesc(
                                "\r\n        以下检查股票期权行权日是否有行权价值：");
                            
                            //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            //获取业务日志信息
							this.checkInfos += "\r\n        以下检查股票期权行权日是否有行权价值：";
                        }
                        runStatus.appendValCheckRunDesc("\r\n            组合：" +
                                                        rs.getString("FPortCode") +
                                                        "\r\n            股票期权代码：" +
                                                        rs.getString("FSecurityCode") +
                                                        "\r\n            股票期权名称：" +
                                                        rs.getString("foptionName") +
                                                        "\r\n            行权日有行权价值！");
                        
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //获取业务日志信息
						this.checkInfos += "\r\n            组合：" + rs.getString("FPortCode") +
                        					"\r\n            股票期权代码：" + rs.getString("FSecurityCode") +
                        					"\r\n            股票期权名称：" + rs.getString("foptionName") +
                        					"\r\n            行权日有行权价值！";
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                        
                        if (this.sNeedLog.equals("true"))
                        {
                        	this.writeLog("\r\n            组合：" +
                                    rs.getString("FPortCode") +
                                    "\r\n            股票期权代码：" +
                                    rs.getString("FSecurityCode") +
                                    "\r\n            股票期权名称：" +
                                    rs.getString("foptionName") +
                                    "\r\n            行权日有行权价值！");
                        }
            		}
            	}else if(dLastAmount < 0&&rs.getString("ftradetypecode").equalsIgnoreCase("PUT")){//卖出认沽状态
            		if(YssD.sub(dMarketPrice,rs.getDouble("fexerciseprice")) > 0){
            			if (iIsError == 0) {
                            runStatus.appendValCheckRunDesc(
                                "\r\n        ------------------------------------");
                            runStatus.appendValCheckRunDesc(
                                "\r\n        以下检查股票期权行权日是否有行权价值：");
                            
                            //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            //获取业务日志信息
							this.checkInfos += "\r\n        以下检查股票期权行权日是否有行权价值：";
                        }
                        runStatus.appendValCheckRunDesc("\r\n            组合：" +
                                                        rs.getString("FPortCode") +
                                                        "\r\n            股票期权代码：" +
                                                        rs.getString("FSecurityCode") +
                                                        "\r\n            股票期权名称：" +
                                                        rs.getString("foptionName") +
                                                        "\r\n            行权日有行权价值！");
                        
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //获取业务日志信息
						this.checkInfos += "\r\n            组合：" + rs.getString("FPortCode") +
                        					"\r\n            股票期权代码：" + rs.getString("FSecurityCode") +
                        					"\r\n            股票期权名称：" + rs.getString("foptionName") +
                        					"\r\n            行权日有行权价值！";
                        //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                        
                        if (this.sNeedLog.equals("true"))
                        {
                        	this.writeLog("\r\n            组合：" +
                                    rs.getString("FPortCode") +
                                    "\r\n            股票期权代码：" +
                                    rs.getString("FSecurityCode") +
                                    "\r\n            股票期权名称：" +
                                    rs.getString("foptionName") +
                                    "\r\n            行权日有行权价值！");
                        }
            		}
            	}
            	 
                 iIsError++;
                 this.sIsError = "false";
            	
            }
			
		}catch (Exception e) {
			throw new YssException("检查股票期权行权日是否有行权价值出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}

















