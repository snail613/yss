package com.yss.main.operdeal.opermanage;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.main.cashmanage.TransferBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssOperCons;

/** @author shashijie ,2011-8-26 上午10:33:47 STORY 1327 挂账销账业务处理 */
public class OperExtensionSell extends BaseOperManage {
	
    CashTransAdmin cashtransAdmin = new CashTransAdmin(); //生成资金调拨控制类
    CashPayRecAdmin cashpayrecadmin = new CashPayRecAdmin(); //生成现金应收应付控制类
    
    public OperExtensionSell() {
    }

    /**
     * 执行业务处理
     */
    public void doOpertion() throws YssException {
    	//挂账销账业务
    	doOpertionExtensionSell();
        //此处产生现金应收应付
        createCashPayRecAdmin();
    }

    /** 处理挂账销账业务
     * @author shashijie ,2011-8-26 , STORY 1327
     * @modified 
     */
    private void doOpertionExtensionSell() throws YssException {
    	ResultSet rs = null;
    	try {
    		String strSql = getStrSql();//获取sql
    		rs = dbl.queryByPreparedStatement(strSql);
    		//处理数据
    		processCanRefundMoney(rs);
		} catch (Exception e) {
			throw new YssException("处理挂账销账时出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**处理数据
	 * @param rs
	 * @author shashijie ,2011-8-26 , STORY 1327
	 * @modified 
	 */
	private void processCanRefundMoney(ResultSet rs) throws YssException {
		try {
			//判断分析代码存不存在
			/*boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
			boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
			boolean analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");*/
			while (rs.next()) {
				//挂账
				if (rs.getString("FTsfTypeCode").equals(YssOperCons.Yss_ZJDBLX_Extension)) {
					if (rs.getInt("FInOut")==1) {//流入
						//现金应收应付
						CashPecPayBean cashpecpay = setCashPecPay(rs,YssOperCons.YSS_ZJDBLX_Pay,"07GZ",1);
		            	cashpayrecadmin.addList(cashpecpay);
					} else {//流出
						//现金应收应付
						CashPecPayBean cashpecpay = setCashPecPay(rs,YssOperCons.YSS_ZJDBLX_Rec,"06GZ",1);
		            	cashpayrecadmin.addList(cashpecpay);
					}
				}
				//销账
				if (rs.getString("FTsfTypeCode").equals(YssOperCons.Yss_ZJDBLX_Sell)) {
					if (rs.getInt("FInOut")==1) {//流入
						//现金应收应付
						CashPecPayBean cashpecpay = setCashPecPay(rs,YssOperCons.YSS_ZJDBLX_Rec,"06XZ",-1);
		            	cashpayrecadmin.addList(cashpecpay);
					} else {//流出
						//现金应收应付
						CashPecPayBean cashpecpay = setCashPecPay(rs,YssOperCons.YSS_ZJDBLX_Pay,"07XZ",-1);
		            	cashpayrecadmin.addList(cashpecpay);
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("处理挂账销账数据时出错！",e);
		}
	}

	/**获取综合业务表
	 * @return
	 * @author shashijie ,2011-8-26 , STORY 1327
	 * @modified 
	 */
	private String getStrSql() {
		String strSql = " select * from "+pub.yssGetTableName("Tb_Cash_Transfer")+" a "+
			" left join "+pub.yssGetTableName("Tb_Cash_SubTransfer")+" b on a.FNum = b.FNum "+
			" left join (select FCashAccCode,FCuryCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+
			" ) c on b.FCashAccCode = c.FCashAccCode "+
			" where FTransferDate = "+dbl.sqlDate(dDate)+" and (FTsfTypeCode = "+YssOperCons.Yss_ZJDBLX_Extension+
			" or FTsfTypeCode = "+YssOperCons.Yss_ZJDBLX_Sell+" ) and a.FCheckState = 1 ";
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
	 * @author shashijie ,2011-8-26 , STORY 1327
	 * @modified 
	 */
	private void createCashPayRecAdmin() throws YssException{
		try{
			//删除之前生成的应收数据
			cashpayrecadmin.delete("", dDate, dDate, "", "", "", "", sPortCode, "",
					"", "", 1,0,"","ExtensionSell");
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
	 * @author shashijie ,2011-8-26 , STORY 1327
	 * @modified 
	 */
	private CashPecPayBean setCashPecPay(ResultSet rs,String TypeCode,String TsfTypeCode,
			int InOutType) throws YssException{
    	CashPecPayBean cashpecpay = null;
    	try{
    		cashpecpay = new CashPecPayBean();
    		cashpecpay.setTradeDate(rs.getDate("FTransferDate"));//业务日期
    		cashpecpay.setTsfTypeCode(TypeCode);//业务类型
    		cashpecpay.setSubTsfTypeCode(TsfTypeCode);//业务子类型
    		cashpecpay.setStrAttrClsCode(rs.getString("FAttrClsCode")==null?" ":rs.getString("FAttrClsCode"));//所属分类
    		cashpecpay.setRelaNum(rs.getString("FNum"));//关联编号
    		cashpecpay.setRelaNumType("ExtensionSell");//关联编号类型
    		//金额
    		cashpecpay.setMoney(rs.getDouble("FMoney"));//金额
    		cashpecpay.setDataSource(1);//来源标志
    		cashpecpay.checkStateId = 1;
    		cashpecpay.setPortCode(rs.getString("FPortCode"));//组合代码
    		cashpecpay.setNum(rs.getString("FNum"));//编号
    		cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));//现金账户
    		cashpecpay.setCuryCode(rs.getString("FCuryCode"));//币种代码
    		cashpecpay.setInOutType(InOutType);//方向
    		//cashpecpay.setInvestManagerCode();//投资经理代码
    		cashpecpay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
    		cashpecpay.setPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
    		//基础货币金额
    		double bacecurymoney = YssD.mul(cashpecpay.getMoney(),cashpecpay.getBaseCuryRate());
    		cashpecpay.setBaseCuryMoney(bacecurymoney);
    		//组合货币金额
    		double portcurymoney = YssD.div(bacecurymoney, cashpecpay.getPortCuryRate());
    		cashpecpay.setPortCuryMoney(portcurymoney);
    		//---设置分析代码 ---------------------------------------------------------
    		/*cashpecpay.setSAnalysisCode1(analy1 ? rs.getString("FAnalysisCode1") : " ");
    		cashpecpay.setSAnalysisCode2(analy2 ? rs.getString("FAnalysisCode2") : " ");
    		cashpecpay.setSAnalysisCode3(analy3 ? rs.getString("FAnalysisCode3") : " ");*/
            //-----------------------------------------------------------------------
    	}catch (Exception e) {
            throw new YssException("设置现金应收应付数据出现异常！", e);
        }
        return cashpecpay;
    	
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
