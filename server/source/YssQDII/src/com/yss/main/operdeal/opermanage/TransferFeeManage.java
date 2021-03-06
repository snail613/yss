package com.yss.main.operdeal.opermanage;

import java.util.*;

import com.yss.util.*;
import com.yss.commeach.EachRateOper;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.manager.CashTransAdmin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author fj
 * story 1321
 *
 */
public class TransferFeeManage extends BaseOperManage {

	public void initOperManageInfo(Date dDate, String portCode) throws YssException {
       this.dDate = dDate;
       this.sPortCode = portCode;
    }
	
	public void doOpertion() throws YssException{
		createFeeCashTrans();    
    }
	
	private void createFeeCashTrans() throws YssException { 
		if(!dbl.yssTableExist("qstransfer")){
			this.sMsg="        当日无业务";
			return;
		}
		String strSql = "";
		ResultSet rs = null;
		TransferSetBean transfersetOut = null;
		TransferBean tran = null;
		ArrayList tranSetList = null;
		CashTransAdmin tranAdmin = new CashTransAdmin();
		try {
			//modify by fangjiang 2011.09.09 bug 2597, bug 2652
			strSql =  "select a.fdate, sum(a.famount) as famount, c.fcashacccode, c.fcashaccname, d.fportcode, e.fanalysiscode1 from " +
			          " (select fnum, ffundcode, fdate, famount from qstransfer where ftype = '906' and fdate = " + dbl.sqlString(YssFun.formatDate(this.dDate,"yyyy/MM/dd")) +
			          ") a join (select fnum, ffundcode, ftransdate, ffromcode from qstransferins " +
			          " where ftype = '906') b on a.fdate = b.ftransdate and a.ffundcode = b.ffundcode and a.fnum = b.fnum " +
			          " join (select fcashacccode, fcashaccname, FBankAccount from " + pub.yssGetTableName("Tb_para_cashaccount") +
			          " where fcheckstate = 1) c on b.ffromcode = c.FBankAccount " +
			          " join (select fportcode, fassetcode from " + pub.yssGetTableName("Tb_para_portfolio") +
			          " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.sPortCode) + ") d on a.ffundcode = d.fassetcode " +
			          " join (select fcashacccode, fanalysiscode1 from " + pub.yssGetTableName("Tb_Stock_Cash") +
			          " where fcheckstate = 1 and fstoragedate = " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
			          " ) e on c.fcashacccode = e.fcashacccode group by a.fdate, c.fcashacccode, c.fcashaccname, d.fportcode, e.fanalysiscode1 ";
			//----------------
			rs = dbl.queryByPreparedStatement(strSql);
	        while (rs.next()) {	
	    		tran = new TransferBean();
	    		tran.setYssPub(pub);
	    		tran.setDtTransDate(this.dDate); 
	    		tran.setDtTransferDate(this.dDate); 
	    		tran.setStrTsfTypeCode("03"); 
	    		tran.setStrSubTsfTypeCode("0303"); 
	    		tran.setDataSource(1); 
	    		tran.setFNumType("transferFee"); 
	    		tran.checkStateId = 1;
	    		tran.creatorTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
	    		
	    		transfersetOut = new TransferSetBean();
	    		transfersetOut.setDMoney(rs.getDouble("famount"));
	    		transfersetOut.setSPortCode(rs.getString("FPortCode")); // 组合代码
	    		transfersetOut.setSCashAccCode(rs.getString("FCashAccCode")); // 现金帐户代码
	    		transfersetOut.setSAnalysisCode1(rs.getString("fanalysiscode1"));
	    		transfersetOut.setDBaseRate(getCuryRate(rs.getString("FCashAccCode"), 
	    								   this.dDate, 
	    								   0, 
	    								   rs.getString("FPortCode")));
	    		transfersetOut.setDPortRate(getCuryRate(rs.getString("FCashAccCode"), 
	    								   this.dDate, 
	    								   1, 
	    								   rs.getString("FPortCode")));
	    		transfersetOut.checkStateId = 1;
	    		transfersetOut.setIInOut( -1);
	    		
	    		tranSetList = new ArrayList();
	    		tranSetList.add(transfersetOut);
	    		tran.setSubTrans(tranSetList);
	    		
	    		tranAdmin.addList(tran);   
	    		
	        }
	        tranAdmin.setYssPub(pub);			
    		tranAdmin.insert(this.dDate, "transferFee", this.sPortCode, "");	
    		//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(tranAdmin.getAddList()==null || tranAdmin.getAddList().size()==0){
    			this.sMsg="        当日无业务";
    		}
		}
		catch (Exception e) {
            throw new YssException("划款手续费处理出现异常！",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
	}
	
	private double getCuryRate(String cashacc, java.util.Date tradeDate, int CuryRateType, String portCode) throws YssException {
		String strSql = "";
		double reCuryRate = 0.0;
		String Cury = "";
		ResultSet rs = null;
		EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
		rateOper.setYssPub(pub);
		try {
			strSql = "select FCuryCode from " + pub.yssGetTableName("tb_Para_CashAccount") +
					 " where FCashAccCode = " + dbl.sqlString(cashacc) + " and FCheckState = 1";
			rs = dbl.queryByPreparedStatement(strSql);
			if (rs.next()) {
				Cury = rs.getString("FCuryCode");
			}
			if (CuryRateType == 0) {
				reCuryRate = this.getSettingOper().getCuryRate(tradeDate, Cury, portCode, YssOperCons.YSS_RATE_BASE); //基础汇率
			} else if (CuryRateType == 1) {
				rateOper.getInnerPortRate(tradeDate, Cury, portCode);
				reCuryRate = rateOper.getDPortRate(); //组合汇率
			}
			return reCuryRate;
		} catch (Exception ex) {
			throw new YssException("获取数据出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
}
