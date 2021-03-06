package com.yss.main.operdeal.opermanage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.main.cashmanage.TransferBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * @author shashijie ,2011-09-27 上午10:24:15 STORY 1561 送股税金业务
 */
public class OperBounsShareRate extends BaseOperManage {
	
    CashTransAdmin cashtransAdmin = new CashTransAdmin(); //生成资金调拨控制类
    CashPayRecAdmin cashpayrecadmin = new CashPayRecAdmin(); //生成现金应收应付控制类
    
    public OperBounsShareRate() {
    }

    /**
     * 执行业务处理
     */
    public void doOpertion() throws YssException {
    	//送股税金业务
    	doOpertionExchangeStock();
        //此处产生现金应收应付
        createCashPayRecAdmin();
    }

    /** 处理送股税金业务
     * @author shashijie ,2011-09-27 , STORY 1561
     * @modified 
     */
    private void doOpertionExchangeStock() throws YssException {
    	ResultSet rs = null;
    	try {
    		String strSql = getStrSql();//获取sql
    		rs = dbl.queryByPreparedStatement(strSql);
    		//处理数据
    		processCanRefundMoney(rs);
		} catch (Exception e) {
			throw new YssException("处理送股税金时出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**处理数据
	 * @param rs
	 * @author shashijie ,2011-09-27 , STORY 1561
	 * @modified 
	 */
	private void processCanRefundMoney(ResultSet rs) throws YssException {
		try {
			while (rs.next()) {
				//现金应收应付(应付)
				CashPecPayBean cashpecpay = setCashPecPay(rs,YssOperCons.YSS_ZJDBLX_Pay,"07SE");
            	cashpayrecadmin.addList(cashpecpay);
			}
		} catch (Exception e) {
			throw new YssException("处理送股税金数据时出错！",e);
		}
	}

	/**获取送股表
	 * @return
	 * @author shashijie ,2011-09-27 , STORY 1561
	 * @modified 
	 */
	private String getStrSql() {
		String strSql = "select a.*,b.FFaceAmount,b.FTradeCury,c.FCashAccCode,d.FStorageAmount "+
			  " From "+pub.yssGetTableName("Tb_Data_BonusShare")+" a "+//--送股
			  " left join "+pub.yssGetTableName("Tb_Para_Security")+
			  " b on a.FSSecurityCode = b.FSecurityCode "+//--证券信息维护
			  " left join (select a2.FCuryCode,a2.FPortCode,Max(a2.FCashAccCode) as FCashAccCode "+
			  " From "+pub.yssGetTableName("Tb_Para_CashAccLink")+" a2 where a2.FCheckState = 1 "+
			  " Group by a2.FCuryCode,a2.FPortCode ) c on c.FCuryCode = b.FTradeCury "+//--账户链接
			  //昨日库存数量
			  " left join (select a3.FSecurityCode,a3.FStorageAmount From "+pub.yssGetTableName("Tb_Stock_Security")+
			  " a3 where a3.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1))+
			  //---------------------------bug 3164 by zhouwei 20111121 QDV4海富通基金2011年11月15日01_B
			  " and a3.FCheckState = 1 and a3.FPortCode ="+dbl.sqlString(this.sPortCode)+" ) d on a.FSSecurityCode = d.FSecurityCode "+
			  " where a.FExRightDate = "+dbl.sqlDate(dDate)+" and a.FTaxRate > 0 and c.FPortCode = "+
			  dbl.sqlString(this.sPortCode)+" and a.FCheckState = 1";
		return strSql;
	}
	

	/**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate;//调拨日期
        this.sPortCode = portCode;//组合
        cashtransAdmin.setYssPub(pub);//资金调拨
        cashpayrecadmin.setYssPub(pub);//现金应收应付
    }

	/**生成现金应收应付数据
	 * @throws YssException
	 * @author shashijie ,2011-09-27 , STORY 1561
	 * @modified 
	 */
	private void createCashPayRecAdmin() throws YssException{
		try{
			//删除之前生成的应收数据
			cashpayrecadmin.delete("", dDate, dDate, "", "", "", "", sPortCode, "",
					"", "", 1,0,"","BounsShareRate");
			//生成现金应收应付数据
			cashpayrecadmin.insert();
			
			//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(cashpayrecadmin.getAddList()==null || cashpayrecadmin.getAddList().size()==0){
    			this.sMsg="        当日无业务";
    		}
    	 }catch (Exception ex) {
             throw new YssException("生成现金应收应付出现异常！", ex);
         } finally {
             //dbl.endTransFinal(conn, bTrans);
         }    	
    } 
	
	/**设置现金应收应付数据 
	 * @param rs 结果集
	 * @param TypeCode 业务类型
	 * @param TsfTypeCode 业务子类型
	 * @author shashijie ,2011-09-27 , STORY 1561
	 * @modified 
	 */
	private CashPecPayBean setCashPecPay(ResultSet rs,String TypeCode,String TsfTypeCode) throws YssException{
    	CashPecPayBean cashpecpay = null;
    	try{
    		cashpecpay = new CashPecPayBean();
    		//cashpecpay.setTradeDate(rs.getDate("FOperDate"));//业务日期
    		cashpecpay.setTradeDate(dDate);//业务日期
    		cashpecpay.setTsfTypeCode(TypeCode);//业务类型
    		cashpecpay.setSubTsfTypeCode(TsfTypeCode);//业务子类型
    		cashpecpay.setStrAttrClsCode(" ");//所属分类
    		cashpecpay.setRelaNum(rs.getString("FSSecurityCode"));//关联编号
    		cashpecpay.setRelaNumType("BounsShareRate");//关联编号类型
    		//获取应付税金(送股数量  * 股票面值  * 税率)
    		double money = getMoney(rs);
    		cashpecpay.setMoney(money);//金额
    		
    		cashpecpay.setDataSource(1);//来源标志
    		cashpecpay.checkStateId = 1;
    		cashpecpay.setPortCode(this.sPortCode);//组合代码
    		cashpecpay.setNum("");//编号
    		cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));//现金账户
    		cashpecpay.setCuryCode(rs.getString("FTradeCury"));//币种代码
    		cashpecpay.setInOutType(1);//方向
    		//cashpecpay.setInvestManagerCode();//投资经理代码
    		//公共获取汇率类
    		BaseOperDeal base = new BaseOperDeal();
    		base.setYssPub(pub);
    		//基础汇率
            double dBaseRate = base.getCuryRate(dDate, rs.getString("FTradeCury"),
											sPortCode,
											YssOperCons.YSS_RATE_BASE);
            //组合汇率
            double dPortRate = base.getCuryRate(dDate, rs.getString("FTradeCury"),
						                    sPortCode,
						                    YssOperCons.YSS_RATE_PORT);
    		cashpecpay.setBaseCuryRate(dBaseRate);//基础汇率
    		cashpecpay.setPortCuryRate(dPortRate);//组合汇率
    		//基础货币金额
    		double bacecurymoney = YssD.mul(cashpecpay.getMoney(),cashpecpay.getBaseCuryRate());
    		cashpecpay.setBaseCuryMoney(bacecurymoney);
    		//组合货币金额
    		double portcurymoney = YssD.div(bacecurymoney, cashpecpay.getPortCuryRate());
    		cashpecpay.setPortCuryMoney(portcurymoney);
    	}catch (Exception e) {
            throw new YssException("设置现金应收应付数据出现异常！", e);
        }
        return cashpecpay; //返回资金调拨数据
    	
    }

	/**
	 * 获取应付税金
	 * @return
	 * @author shashijie ,2011-10-9 , STORY 1561
	 * @modified
	 */
    private double getMoney(ResultSet rs) throws YssException ,SQLException {
    	CtlPubPara pubPara=new CtlPubPara();//通用参数实例化
        pubPara.setYssPub(pub);//设置Pub
    	double rate = 0;//送股比例
    	double FStorageAmount = rs.getDouble("FStorageAmount");//库存数量
    	String rightsRatioMethods = (String) pubPara.getRightsRatioMethods(sPortCode);//获取通用参数值
		//送股数量  * 股票面值  * 税率
    	if (rightsRatioMethods.equalsIgnoreCase("PreTaxRatio")) {//税前
			rate = rs.getDouble("FPreTaxRatio");
		} else {//税后
			rate = rs.getDouble("FAfterTaxRatio");
		}
    	//昨日库存数量  * 送股比例  * 股票面值  * 税率
    	double money = getDoMoney(rate,FStorageAmount,rs.getDouble("FFaceAmount"),rs.getDouble("FTaxRate"));
		return money;
	}
    

	/**昨日库存数量  * 送股比例  * 股票面值  * 税率
	 * @param rate 送股比例
	 * @param fStorageAmount 昨日库存数量
	 * @param FFaceAmount 股票面值
	 * @param FTaxRate 税率
	 * @return
	 * @author shashijie ,2011-10-9 , STORY 1561
	 * @modified 
	 */
	private double getDoMoney(double rate, double fStorageAmount,
			double FFaceAmount, double FTaxRate) {
		double money = 0;
		try {
			money = YssD.mul(rate,fStorageAmount,FFaceAmount,FTaxRate);
		} catch (Exception e) {
			money = 0;
		}
		return money;
	}

	/**
     * 删除调拨类型01，调拨子类型0003；调拨类型02，调拨子类型02DE的历史资金调拨数据    
     */
    public void delOldCashTransfer(CashTransAdmin cashTransAdmin) throws YssException {
    	ResultSet rs = null;
    	ResultSet rsTemp = null;
        String strTransNum = ""; //资金调拨编号
        String strSavingNum = ""; 
        String strSql = "";
        TransferBean transfer = null;
        
		try {
			// 把要删除的资金调拨的定存编号拼接起来
			for (int i = 0; i < cashTransAdmin.getAddList().size(); i++ ){
				transfer = (TransferBean) cashTransAdmin.getAddList().get(i);
				if ("01".equals(transfer.getStrTsfTypeCode()) && 
							"0003".equals(transfer.getStrSubTsfTypeCode())){
					
					strSavingNum +=  transfer.getSavingNum() + ",";
				}
			}
			
			if (strSavingNum.length() > 1) {
				strSavingNum = strSavingNum
						.substring(0, strSavingNum.length() - 1);
				strSavingNum = operSql.sqlCodes(strSavingNum);
				
				//查询当天调拨类型01，调拨子类型0003的历史资金调拨数据
				strSql = " Select * from "
						+ pub.yssGetTableName("Tb_cash_transfer")
						+ " Where FTsfTypeCode = '01' and FSubTsfTypeCode = '0003' and FNumType = 'Saving' "
						+ " and FSavingNum in (" + strSavingNum + ") "
						+ " and FTransferDate = "
						+ dbl.sqlDate(dDate);
						
				rs = dbl.queryByPreparedStatement(strSql);
				
				// 把要删除的资金调拨编号拼接起来
				while (rs.next()) {
					strTransNum += rs.getString("FNum") + ",";
				}
				dbl.closeResultSetFinal(rs);
			}
			
			
			
			if (strTransNum.length() > 1) {
				strTransNum = strTransNum
						.substring(0, strTransNum.length() - 1);
				strTransNum = operSql.sqlCodes(strTransNum);
				if (strTransNum.trim().length() > 0) {
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_Transfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);

					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_SubTransfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);
				}
			}
			
			strTransNum = "";
			
			strSql = " Select * from "
					+ pub.yssGetTableName("Tb_Cash_Transfer")
					+ " Where FTsfTypeCode = '02' and FSubTsfTypeCode = '02DE' and FNumType = 'CashPay' "
					+ " and FRelaNum like 'SRP%' and FTransferDate = "
					+ dbl.sqlDate(dDate);

			rsTemp = dbl.queryByPreparedStatement(strSql);
			// 把要删除的资金调拨编号拼接起来
			while (rsTemp.next()) {
				strTransNum += rsTemp.getString("FNum") + ",";
			}
			dbl.closeResultSetFinal(rsTemp);

			if (strTransNum.length() > 1) {
				strTransNum = strTransNum
						.substring(0, strTransNum.length() - 1);
				strTransNum = operSql.sqlCodes(strTransNum);
				if (strTransNum.trim().length() > 0) {
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_Transfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);

					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_SubTransfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);
				}
			}
			
		} catch (Exception e) {
			throw new YssException("删除历史的资金调拨出错" + "\r\n" + e.getMessage(),e);
		}
		finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsTemp);
		}
	}
    
    
}
