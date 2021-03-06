package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Hashtable;

import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 此类检查期货估值保证金结算账户是否设置，并且设置的账户是否和变动保证金账户一致
 * @author xuqiji 20100526 MS01183 QDV4赢时胜(测试)2010年05月26日01_A  
 *
 */
public class CheckFuturesBailAccount extends BaseValCheck{

	public CheckFuturesBailAccount() {
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
			doCheckFuturesBailAccount(dTheDay,sPortCodes);//检查期货估值保证金结算账户是否设置，并且设置的账户是否和变动保证金账户一致
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		return result;
	}
	/**
	 * 检查期货估值保证金结算账户是否设置，并且设置的账户是否和变动保证金账户一致
	 * @param theDay
	 * @param portCodes
	 */
	private void doCheckFuturesBailAccount(Date theDay, String portCodes) throws YssException{
		String sAccountType = "";
		String result = "";
		try{
            //获取期货结算类型
            sAccountType = getAccountTypeBy(portCodes);
            valutionGainsTransfer(theDay,portCodes,sAccountType);
			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	 /**
     * 估值增值收益资金调拨，当天的估值增值发生额
     * 估值增值收益=今日估值增值-昨日估值增值+今日卖出估值增值
     * 使用移动加权平均法计算时要关联证券代码和组合，证券代码存在TB_DATA_FUTTRADERELA表的FNum字段中
     * @param dwork Date 业务日期(估值日期)
     * @param portCode String 组合代码
     * @return double
     */
    public void valutionGainsTransfer(java.util.Date dwork, String portCode, String sAccountType) throws YssException {
        ResultSet rs = null;
        int iIsError = 0; //记录出错数据数量
        StringBuffer buf = new StringBuffer();
        if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)) {
            buf.append(
                "SELECT T.FMONEY AS TMONEY, Y.FMONEY AS YMONEY, TS.FMONEY TSMONEY,");
            buf.append(
                "TRADE.FBASECURYRATE,TRADE.FPORTCURYRATE,TRADE.FCHAGEBAILACCTCODE,");
            buf.append("TRADE.FSECURITYCODE,trade.fSecurityName,TRADE.FBARGAINDATE,TRADE.FSETTLEDATE,");
			buf.append("TRADE.FInvMgrCode,T.FNUM,TRADE.FCashAccCode");
            buf.append(",TRADE.FNum as TradeNum");
            buf.append(" FROM (SELECT FMONEY, FNUM, FPortCode");
            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
            buf.append(" WHERE FTRANSDATE =" + dbl.sqlDate(dwork));
            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            buf.append(" AND FTSFTYPECODE = '09FU01' AND FSTORAGEAMOUNT<>0) T");
            buf.append(" LEFT JOIN (SELECT FMONEY, FNUM, FPortCode");
            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
            buf.append(" WHERE FTRANSDATE = " + dbl.sqlDate(YssFun.addDay(dwork, -1)));
            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            buf.append(" AND FTSFTYPECODE = '09FU01') Y ON T.FNUM = Y.FNUM AND T.FPortCode = Y.FPortCode");
            buf.append(" LEFT JOIN (SELECT SUM(FMONEY) AS FMONEY, FNUM, FPortCode");
            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
            buf.append(" WHERE FTRANSDATE = " + dbl.sqlDate(dwork));
            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            buf.append(" AND FTSFTYPECODE = '19FU01' GROUP BY FNum, FPortCode) TS ON T.FNUM = TS.FNUM AND T.FPortCode = TS.FPortCode");
			buf.append(" LEFT JOIN (select ss.*,op.fcashacccode as FCashAccCode from(SELECT tr2.*, s.fexchangecode,s.fSecurityName ");
            buf.append(" FROM (SELECT MAX(FNum) AS FNum, FSECURITYCODE, FPortCode ");
            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTURESTRADE "));
            buf.append(" WHERE FCheckState = 1 ");
            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            buf.append(" AND FBargainDate <= ").append(dbl.sqlDate(dwork));
            buf.append(" GROUP BY FSecurityCode, FPortCode) tr1 ");
            buf.append(
                " JOIN (SELECT FBASECURYRATE, FPORTCURYRATE, FCHAGEBAILACCTCODE, ");
            buf.append(" FNUM, FPortCode, FSECURITYCODE, FBARGAINDATE, ");
            buf.append(" FSETTLEDATE, FInvMgrCode ");
            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTURESTRADE "));
            buf.append(" WHERE FPORTCODE = " + dbl.sqlString(portCode) +
                       ") tr2 ON tr1.FNum = tr2.FNum");
            buf.append(" join (select * from ");
            buf.append(pub.yssGetTableName("tb_para_security"));//证券信息表
            buf.append(" where FCheckState = 1) s on s.FSecurityCode = tr1.FSecurityCode) ss");
            buf.append(" left join (select * from ");
            buf.append(pub.yssGetTableName("tb_data_optionsvalcal"));//期权和期货保证金账户设置表
            buf.append(" where FCheckState = 1 and FMarkType=1) op on ss.FPortCode = op.FPortCode and ss.fexchangecode = op.fexchagecode");
			buf.append(" and ss.FCHAGEBAILACCTCODE=op.fcashacccode");
            buf.append(") TRADE ON T.FNUM = ");
            buf.append(" TRADE.FSecurityCode  AND T.FPortCode = TRADE.FPortCode");
        } else {
            buf.append(
                "SELECT T.FMONEY AS TMONEY, Y.FMONEY AS YMONEY, TS.FMONEY TSMONEY,");
            buf.append(
                "TRADE.FBASECURYRATE,TRADE.FPORTCURYRATE,TRADE.FCHAGEBAILACCTCODE,");
            buf.append("TRADE.FSECURITYCODE,trade.fSecurityName,TRADE.FBARGAINDATE,TRADE.FSETTLEDATE,");
			buf.append("TRADE.FInvMgrCode,TRADE.FNUM,TRADE.FCashAccCode");
            buf.append(",TRADE.FNum as TradeNum");
            buf.append(" FROM (SELECT FMONEY, FNUM");
            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
            buf.append(" WHERE FTRANSDATE =" + dbl.sqlDate(dwork));
            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            buf.append(" AND FTSFTYPECODE = '09FU01' AND FSTORAGEAMOUNT<>0) T");
            buf.append(" LEFT JOIN (SELECT FMONEY, FNUM");
            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
            buf.append(" WHERE FTRANSDATE = " + dbl.sqlDate(YssFun.addDay(dwork, -1)));
            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            buf.append(" AND FTSFTYPECODE = '09FU01') Y ON T.FNUM = Y.FNUM");
            buf.append(" LEFT JOIN (SELECT SUM(FMONEY) AS FMoney, FNUM");
            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
            buf.append(" WHERE FTRANSDATE = " + dbl.sqlDate(dwork));
            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            buf.append(" AND FTSFTYPECODE = '19FU01' GROUP BY FNUM) TS ON T.FNUM = TS.FNUM");
			buf.append(" JOIN (select aa.*,op.fcashacccode as FCashAccCode from(");
			buf.append("SELECT f.FBASECURYRATE,f.FPORTCURYRATE,f.FCHAGEBAILACCTCODE,FNUM,");
			buf.append("f.FSECURITYCODE,f.FBARGAINDATE,f.FSETTLEDATE,f.FInvMgrCode,f.fportcode,s.fexchangecode,s.fSecurityName ");
			buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTURESTRADE")).append(" f ");
			buf.append(" join (select * from ");
			buf.append(pub.yssGetTableName("tb_para_security"));//证券信息表
			buf.append(" where FCheckState = 1) s on f.fsecuritycode = s.fsecuritycode) aa");
			buf.append(" left join (select * from ");
			buf.append(pub.yssGetTableName("tb_data_optionsvalcal"));//期权和期货保证金账户设置表
			buf.append(" where FCheckState = 1 and FMarkType=1) op on aa.FPORTCODE = op.FPORTCODE and aa.fexchangecode = op.fexchagecode");
			buf.append(" and aa.FCHAGEBAILACCTCODE=op.fcashacccode");
			buf.append(" WHERE aa.FPORTCODE = '" + portCode +
                   "') TRADE ON T.FNUM =TRADE.FNUM");
        }
        try {
            rs = dbl.queryByPreparedStatement(buf.toString()); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	if(rs.getString("FCashAccCode") == null){
            		if (iIsError == 0) {
                        runStatus.appendValCheckRunDesc(
                            "\r\n        ------------------------------------");
                        runStatus.appendValCheckRunDesc(
                            "\r\n        以下检查期货估值保证金结算账户是否设置，并且设置的账户是否和变动保证金账户一致：");
                        
                        //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                        //获取业务日志信息
						this.checkInfos += "\r\n        以下检查期货估值保证金结算账户是否设置，并且设置的账户是否和变动保证金账户一致：";
                    }
                    runStatus.appendValCheckRunDesc("\r\n            组合：" +
                    								portCode +
                                                    "\r\n            股票期货代码：" +
                                                    rs.getString("FSecurityCode") +
                                                    "\r\n            股票期货名称：" +
                                                    rs.getString("fSecurityName") +
                                                    "\r\n            没有设置期货估值结算保证金账户，请到期权期货保证金账户设置界面设置，并保证设置账户与期货变动保证金账户保持一致！");
                    
        			//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        			//获取业务日志信息
					this.checkInfos += "\r\n            组合：" + portCode +
                    				   "\r\n            股票期货代码：" + rs.getString("FSecurityCode") +
                    				   "\r\n            股票期货名称：" + rs.getString("fSecurityName") +
                    				   "\r\n            没有设置期货估值结算保证金账户，请到期权期货保证金账户设置界面设置，并保证设置账户与期货变动保证金账户保持一致！";
        			//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    
                    if (this.sNeedLog.equals("true"))
                    {
                    	this.writeLog("\r\n            组合：" +
								portCode +
                                "\r\n            股票期货代码：" +
                                rs.getString("FSecurityCode") +
                                "\r\n            股票期货名称：" +
                                rs.getString("fSecurityName") +
                                "\r\n            没有设置期货估值结算保证金账户，请到期权期货保证金账户设置界面设置，并保证设置账户与期货变动保证金账户保持一致！");
                    }
            	}
            }
        } catch (Exception ex) {
            throw new YssException("检查期货估值保证金结算账户是否设置，并且设置的账户是否和变动保证金账户一致出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	 /**
     * 通过组合代码获取期货核算方式
     * @param sPortCode String：组合代码
     * @return String
     */
    private String getAccountTypeBy(String sPortCode) throws YssException {
        CtlPubPara pubPara = new CtlPubPara();
        pubPara.setYssPub(pub);
        Hashtable htAccountType = pubPara.getFurAccountType("AccoutType");
        String sResult = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO;
        String sTheDayFirstFIFO = (String) htAccountType.get(YssOperCons.
            YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO);
        String sModAge = (String) htAccountType.get(YssOperCons.
            YSS_FUTURES_ACCOUNTTYPE_MODAVG);
        if (sTheDayFirstFIFO != null && sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
            sResult = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO;
        } else if (sModAge != null && sModAge.indexOf(sPortCode) != -1) {
            sResult = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG;
        }
        return sResult;
    }
}


















