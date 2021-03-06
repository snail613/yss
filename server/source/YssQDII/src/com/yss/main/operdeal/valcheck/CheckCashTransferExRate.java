package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.ResultSet;
import com.yss.util.YssException;
import com.yss.util.YssOperCons;

/**
 * <p>Title: CheckCashTransferExRate</p>
 *
 * <p>Description: add by xuqiji 20090605:QDV4海富通2009年06月1日01_AB  MS00470 估值检查时增加对资金调拨中的汇率进行检查的功能 </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CheckCashTransferExRate
    extends BaseValCheck {
    public CheckCashTransferExRate() {
    }

    /**
     * 检查资金调拨中的汇率和当日汇率是否相同
     * @param dTheDay Date 检查日期
     * @param sPortCodes String 组合代码
     * @return String 返回检查出的错误错误信息
     * @throws Exception
     */
    public String doCheck(Date dTheDay, String sPortCodes) throws Exception {
        String result = "";
        ResultSet rs = null;
        StringBuffer buff = null;
        int iIsError = 0; //记录出错数据数量
        String portCury = null;
        
        double baseCuryRate;
        double portCuryRate;
        
        try {
            buff = new StringBuffer();
            portCury = this.getPortCodeCury(sPortCodes);
            //------ modify by wangzuochun   MS01086   资产估值时估值检查中的“检查资金调拨中的汇率是否和当日汇率相同”存在问题    QDV4交银施罗德2010年04月13日01_B 
            //buff.append(" select TransRate.*,standRate.FBaseRate as FStandBaseRate,standRate.FPortRate as FStandPortRate");
            buff.append(" select TransRate.* , ta.*");
            //--------------- MS01086 ---------------//
            buff.append(" from(").append(" select trans.*, cashAcc.Fcurycode ");
            buff.append(" from (").append(
                " select trans.FNum as FNum,trans.frelanum,sub.fportcode as FPortCode,"); //------ modify by wangzuochun   MS01086   资产估值时估值检查中的“检查资金调拨中的汇率是否和当日汇率相同”存在问题    QDV4交银施罗德2010年04月13日01_B 
            buff.append(" sub.fcashacccode as FCashAccCode,sub.fbasecuryrate as FBaseCuryRate,sub.fportcuryrate as FPortCuryRate");
            buff.append(" from (").append(
                " select FNum, FTransferDate, Ftransdate, frelanum from ").//------ modify by wangzuochun   MS01086   资产估值时估值检查中的“检查资金调拨中的汇率是否和当日汇率相同”存在问题    QDV4交银施罗德2010年04月13日01_B 
                append(pub.yssGetTableName("Tb_Cash_Transfer")); //资金调拨的表
            buff.append(
                " where FCheckState = 1 and FRateTradeNum is null and FTransDate = "). //FRateTradeNum is null 外汇交易中产生的资金调拨不用检查
                append(dbl.sqlDate(dTheDay)); //根据业务日期查询资金调拨表中数据
            buff.append(" ) trans join ( ").append(
                " select FNum,FPortCode,FCashAccCode,FMoney,FBaseCuryRate,FPortCuryRate from ");
            buff.append(pub.yssGetTableName("Tb_Cash_SubTransfer")).append( //资金调拨子表
                " where FCheckState = 1 and FPortCode=").append(dbl.sqlString(
                    sPortCodes)).append(" )sub on trans.FNum = sub.FNum)trans ");
            buff.append(
                " left join (select FCashAccCode, FCuryCode, FPortCode from ");
            buff.append(pub.yssGetTableName("Tb_Para_CashAccount")).append( //查询关联现金账户表因为，资金调拨表中没有货币代码
                " where FCheckState = 1) cashAcc on trans.FPortCode = cashAcc.Fportcode ");
            buff.append(
                " and trans.FCashAccCode = cashAcc.Fcashacccode) TransRate");
          //------ modify by wangzuochun   MS01086   资产估值时估值检查中的“检查资金调拨中的汇率是否和当日汇率相同”存在问题    QDV4交银施罗德2010年04月13日01_B 
            buff.append(" left join (select fnum as fnumTa,FtradeDate from ");
            buff.append(pub.yssGetTableName("tb_ta_trade")).append(") ta on TransRate.FrelaNum = ta.fnumTa ");
            rs = dbl.queryByPreparedStatement(buff.toString()); //modify by fangjiang 2011.08.14 STORY #788
            buff.delete(0, buff.length());
          //------ modify by wangzuochun   MS01086   资产估值时估值检查中的“检查资金调拨中的汇率是否和当日汇率相同”存在问题    QDV4交银施罗德2010年04月13日01_B 
            while (rs.next()){
            	
            	if (rs.getString("FNumTa") != null && rs.getString("FRelaNum") != null 
            			&& rs.getString("FRelaNum").equals(rs.getString("FNumTa")) 
            			&& rs.getDate("FTradeDate") != null){
            		baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FTradeDate"), 
            														rs.getString("FCuryCode"), sPortCodes, 
            														YssOperCons.YSS_RATE_BASE);
            		
            		portCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FTradeDate"), 
            											rs.getString("FCuryCode"), sPortCodes, 
														YssOperCons.YSS_RATE_PORT);
            		if (rs.getDouble("FBaseCuryRate") != baseCuryRate ||
            				rs.getDouble("FPortCuryRate") != portCuryRate){
            			
            			if (iIsError == 0) {
            				//add by guyichuan STORY #1236 2011.07.13 在状态栏显示
            				runStatus.appendSchRunDesc(
                            "\r\n        ------------------------------------");
            				runStatus.appendSchRunDesc(
                            "\r\n        以下检查资金调拨中的汇率和当日汇率信息不同：");
            				//--end-STORY #1236--
                            runStatus.appendValCheckRunDesc(
                                "\r\n        ------------------------------------");
                            runStatus.appendValCheckRunDesc(
                                "\r\n        以下检查资金调拨中的汇率和当日汇率信息不同：");
                            
                            //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            //获取业务日志信息
							this.checkInfos += "\r\n        以下检查资金调拨中的汇率和当日汇率信息不同：";
                        }
            			//add by guyichuan STORY #1236 2011.07.13 在状态栏显示
            			runStatus.appendSchRunDesc("\r\n            组合：" +
                                rs.getString("FPortCode") +
                                "\r\n            调拨编号：" +
                                rs.getString("FNum") +
                                "\r\n            帐户：" +
                                rs.getString("FCASHACCCODE") +
                                "\r\n            基础汇率：" +
                                rs.getString("FBaseCuryRate") +
                                "\r\n            组合汇率：" +
                                rs.getString("FPortCuryRate") +
                                "\r\n            资金调拨中的汇率和当日汇率信息不同！");
            			//--end-STORY #1236--
            			runStatus.appendValCheckRunDesc("\r\n            组合：" +
                                rs.getString("FPortCode") +
                                "\r\n            调拨编号：" +
                                rs.getString("FNum") +
                                "\r\n            帐户：" +
                                rs.getString("FCASHACCCODE") +
                                "\r\n            基础汇率：" +
                                rs.getString("FBaseCuryRate") +
                                "\r\n            组合汇率：" +
                                rs.getString("FPortCuryRate") +
                                "\r\n            资金调拨中的汇率和当日汇率信息不同！");
            			
            			//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            			//获取业务日志信息
						this.checkInfos += "\r\n            组合：" +
                        					rs.getString("FPortCode") +
                        					"\r\n            调拨编号：" +
                        					rs.getString("FNum") +
                        					"\r\n            帐户：" +
                        					rs.getString("FCASHACCCODE") +
                        					"\r\n            基础汇率：" +
                        					rs.getString("FBaseCuryRate") +
                        					"\r\n            组合汇率：" +
                        					rs.getString("FPortCuryRate") +
                        					"\r\n            资金调拨中的汇率和当日汇率信息不同！";
            			//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            			
            			if (this.sNeedLog.equals("true"))
                        {
                        	this.writeLog("\r\n            组合：" +
                                    rs.getString("FPortCode") +
                                    "\r\n            调拨编号：" +
                                    rs.getString("FNum") +
                                    "\r\n            帐户：" +
                                    rs.getString("FCASHACCCODE") +
                                    "\r\n            基础汇率：" +
                                    rs.getString("FBaseCuryRate") +
                                    "\r\n            组合汇率：" +
                                    rs.getString("FPortCuryRate") +
                                    "\r\n            资金调拨中的汇率和当日汇率信息不同！");
                        }
            			iIsError++;
            			this.sIsError = "false";
            		}
            	}
            	else{
            		baseCuryRate = this.getSettingOper().getCuryRate(dTheDay, 
            							rs.getString("FCuryCode"), sPortCodes, 
										YssOperCons.YSS_RATE_BASE);

            		portCuryRate = this.getSettingOper().getCuryRate(dTheDay, 
            							rs.getString("FCuryCode"), sPortCodes, 
										YssOperCons.YSS_RATE_PORT);
            		if (rs.getDouble("FBaseCuryRate") != baseCuryRate ||
            				rs.getDouble("FPortCuryRate") != portCuryRate){
            			
            			if (iIsError == 0) {
            				//add by guyichuan STORY #1236 2011.07.13 在状态栏显示
            				 runStatus.appendSchRunDesc(
                             "\r\n        ------------------------------------");
            				 runStatus.appendSchRunDesc(
                             "\r\n        以下检查资金调拨中的汇率和当日汇率信息不同：");
            				//--end-STORY #1236--
                            runStatus.appendValCheckRunDesc(
                                "\r\n        ------------------------------------");
                            runStatus.appendValCheckRunDesc(
                                "\r\n        以下检查资金调拨中的汇率和当日汇率信息不同：");
                            
                            //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            //获取业务日志信息
							this.checkInfos += "\r\n        以下检查资金调拨中的汇率和当日汇率信息不同：";
                        }
            			//add by guyichuan STORY #1236 2011.07.13 在状态栏显示
            			runStatus.appendSchRunDesc("\r\n            组合：" +
                                rs.getString("FPortCode") +
                                "\r\n            调拨编号：" +
                                rs.getString("FNum") +
                                "\r\n            帐户：" +
                                rs.getString("FCASHACCCODE") +
                                "\r\n            基础汇率：" +
                                rs.getString("FBaseCuryRate") +
                                "\r\n            组合汇率：" +
                                rs.getString("FPortCuryRate") +
                                "\r\n            资金调拨中的汇率和当日汇率信息不同！");
            			//--end-STORY #1236--
            			runStatus.appendValCheckRunDesc("\r\n            组合：" +
                                rs.getString("FPortCode") +
                                "\r\n            调拨编号：" +
                                rs.getString("FNum") +
                                "\r\n            帐户：" +
                                rs.getString("FCASHACCCODE") +
                                "\r\n            基础汇率：" +
                                rs.getString("FBaseCuryRate") +
                                "\r\n            组合汇率：" +
                                rs.getString("FPortCuryRate") +
                                "\r\n            资金调拨中的汇率和当日汇率信息不同！");
            			
            			//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            			//获取业务日志信息
						this.checkInfos += "\r\n            组合：" +
                        					rs.getString("FPortCode") +
                        					"\r\n            调拨编号：" +
                        					rs.getString("FNum") +
                        					"\r\n            帐户：" +
                        					rs.getString("FCASHACCCODE") +
                        					"\r\n            基础汇率：" +
                        					rs.getString("FBaseCuryRate") +
                        					"\r\n            组合汇率：" +
                        					rs.getString("FPortCuryRate") +
                        					"\r\n            资金调拨中的汇率和当日汇率信息不同！";
            			//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            			
		            	if (this.sNeedLog.equals("true"))
		                {
		            		this.writeLog("\r\n            组合：" +
		                                  rs.getString("FPortCode") +
		                                  "\r\n            调拨编号：" +
		                                  rs.getString("FNum") +
		                                  "\r\n            帐户：" +
		                                  rs.getString("FCASHACCCODE") +
		                                  "\r\n            基础汇率：" +
		                                  rs.getString("FBaseCuryRate") +
		                                  "\r\n            组合汇率：" +
		                                  rs.getString("FPortCuryRate") +
		                                  "\r\n            资金调拨中的汇率和当日汇率信息不同！");
		                }
            			iIsError++;
            			this.sIsError = "false";
            			
            		}
            	}
            }
        } catch (Exception e) {
            throw new YssException("检查资金调拨中的汇率和当日汇率是否相同信息出错！\r\t", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
    }

    /**
     * 查询组合设置中货币代码
     * @param sPortCodes String 组合代码
     * @return String[] 返回组合代码对应的货币代码
     * @throws YssException
     */
    public String getPortCodeCury(String sPortCodes) throws YssException {
        StringBuffer buff = null;
        String portCury = null;
        ResultSet rs = null;
        try {
            buff = new StringBuffer();
            buff.append(" select FPortCury from ").append(pub.yssGetTableName("Tb_Para_Portfolio"));
            buff.append(" where FPortCode =").append(dbl.sqlString(sPortCodes));

            rs = dbl.queryByPreparedStatement(buff.toString()); //modify by fangjiang 2011.08.14 STORY #788
            buff.delete(0, buff.length());
            while (rs.next()) {
                portCury = rs.getString("FPortCury");
            }
        } catch (Exception e) {
            throw new YssException("查询组合设置中货币代码出错！\r\t", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return portCury;
    }
}
