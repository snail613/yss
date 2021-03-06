package com.yss.main.operdeal.opermanage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * @author shashijie ,2011-8-22 上午10:24:15 STORY 1202 换股对价业务
 */
public class OperExchangeStock extends BaseOperManage {
	
    CashTransAdmin cashtransAdmin = new CashTransAdmin(); //生成资金调拨控制类
    CashPayRecAdmin cashpayrecadmin = new CashPayRecAdmin(); //生成现金应收应付控制类
    
    public OperExchangeStock() {
    }

    /**
     * 执行业务处理
     */
    public void doOpertion() throws YssException {
    	//换股对价业务
    	doOpertionExchangeStock();
    	//此处产生资金调拨
        createCashTransfer();
        //此处产生现金应收应付
        createCashPayRecAdmin();
    }

    /** 处理换股对价业务
     * @author shashijie ,2011-8-22 , STORY 1202
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
			throw new YssException("处理换股对价时出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**处理数据
	 * @param rs
	 * @author shashijie ,2011-8-22 , STORY 1202
	 * @modified 
	 */
	private void processCanRefundMoney(ResultSet rs) throws YssException {
		try {
			while (rs.next()) {
				//若业务日与到帐日是同一天则只产生资金调拨数据
				if (YssFun.dateDiff(rs.getDate("FOperDate"),rs.getDate("FMatureDate"))==0) {
					//设置资金调拨主表实体Bean
	            	doOpsertionTransferBean(rs);
				} else {
					//业务日(产生现金应收应付数据和资金调拨)
					if (YssFun.dateDiff(rs.getDate("FOperDate"), dDate)==0) {
						if (rs.getInt("FInOutType")==1) {//流入
							//现金应收应付(应收)
							CashPecPayBean cashpecpay = setCashPecPay(rs,YssOperCons.YSS_ZJDBLX_Rec,"06OT");
			            	cashpayrecadmin.addList(cashpecpay);
						} else {//流出
							//现金应收应付(应付)
							CashPecPayBean cashpecpay = setCashPecPay(rs,YssOperCons.YSS_ZJDBLX_Pay,"07OT");
			            	cashpayrecadmin.addList(cashpecpay);
						}
						//设置资金调拨主表实体Bean
		            	doOpsertionTransferBean(rs);
					}
					//到账日(产生现金应收应付数据冲减业务日产生的现金应收应付数据)
					if (YssFun.dateDiff(rs.getDate("FMatureDate"), dDate)==0) {
						if (rs.getInt("FInOutType")==1) {//流入
							//现金应收应付(收入)
							CashPecPayBean cashpecpay = setCashPecPay(rs,YssOperCons.YSS_ZJDBLX_Income,"02OT");
			            	cashpayrecadmin.addList(cashpecpay);
						} else {//流出
							//现金应收应付(费用)
							CashPecPayBean cashpecpay = setCashPecPay(rs,YssOperCons.YSS_ZJDBLX_Fee,"03OT");
			            	cashpayrecadmin.addList(cashpecpay);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("处理换股对价数据时出错！",e);
		}
	}

	/**设置资金调拨主表实体Bean
	 * @param rs
	 * @throws YssException
	 * @throws SQLException
	 * @author shashijie ,2011-8-22 , STORY 1202
	 * @modified 
	 */
	private void doOpsertionTransferBean(ResultSet rs) throws YssException, SQLException {
		//判断分析代码存不存在
		boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
		boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
		boolean analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
		//资金调拨
		createPrincipalExtCashTransfer(rs, analy1, analy2, analy3);
	}

	/**获取综合业务表
	 * @return
	 * @author shashijie ,2011-8-22 , STORY 1202
	 * @modified 
	 */
	private String getStrSql() {
		String strSql = "select a.*,b.FTradeCury From "+pub.yssGetTableName("Tb_Data_Integrated")+" a " +
			" Left Join (select a1.FSecurityCode,a1.FTradeCury From "+
			pub.yssGetTableName("Tb_Para_Security")+" a1) b on a.FSecurityCode = b.FSecurityCode "+
			" where (a.FOperDate = "+
			dbl.sqlDate(dDate)+" or a.FMatureDate = "+dbl.sqlDate(dDate)+" ) and a.FPortCode in ("+
			operSql.sqlCodes(sPortCode)+" ) and a.FTradeTypeCode = '80CH' and a.FCheckState = 1 ";
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

    /**生成资金调拨
     * @throws YssException
     * @author shashijie ,2011-8-22 , STORY 1202
     * @modified 
     */
    private void createCashTransfer() throws YssException {
        try {
        	//if (cashtransAdmin.getAddList()!=null && cashtransAdmin.getAddList().size()>0) {
        		//先删后增加资金调拨
        		cashtransAdmin.insert("", null,dDate,YssOperCons.YSS_ZJDBLX_Income+","+YssOperCons.YSS_ZJDBLX_Fee,
        				"03OT,02OT","", "","","", "","ExchangeStock", "",-1,"",sPortCode,0,"","","",true,
        				"","");
        		//cashtransAdmin.insert(dDate ,"ExchangeStock", -1, "");
			//}
        		
        		//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
        		//当日产生数据，则认为有业务。
        		if(cashtransAdmin.getAddList()==null || cashtransAdmin.getAddList().size()==0){
        			this.sMsg="        当日无业务";
        		}	
        		
        } catch (Exception ex) {
            throw new YssException("生成定存的资金调拨出现异常！", ex);
        } finally {
            //dbl.endTransFinal(conn, bTrans);
        }
    }
    
    /**判断通用业务参数是否启用新模式 shashijie,2011-4-25  */
    /*private boolean isOverDayProfession() throws YssException, SQLException {
    	ParaWithPubBean pubBean = new ParaWithPubBean();
        pubBean.setYssPub(pub);
        String FCtlGrpCode = "isBeforehandHandle";//控件组
        String FCtlCode =  "cboIsTrue";//控件
        String FCtlValue = "1,1";//控件值
        String FParaId = null;//排序编号
        ResultSet rs = null;
        try {
        	rs = pubBean.getResultSetByLike(FCtlGrpCode, FCtlCode, FCtlValue, FParaId);
        	if (rs.next()) {
				return true;
			}
		} catch (YssException e) {
			dbl.closeResultSetFinal(rs);
		} finally{
			dbl.closeResultSetFinal(rs);
		}
		return false;
	}*/

	/**生成现金应收应付数据
	 * @throws YssException
	 * @author shashijie ,2011-8-22 , STORY 1202
	 * @modified 
	 */
	private void createCashPayRecAdmin() throws YssException{
		try{
			//if (cashpayrecadmin.getAddList()!=null && cashpayrecadmin.getAddList().size()>0) {
				//删除之前生成的应收数据
				cashpayrecadmin.delete("", dDate, dDate, "", "", "", "", sPortCode, "",
						"", "", 1,0,"","ExchangeStock");
				//生成现金应收应付数据
				cashpayrecadmin.insert();
			//}
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
	 * @author shashijie ,2011-8-22 , STORY 1202
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
    		cashpecpay.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));//所属分类
    		cashpecpay.setRelaNum(rs.getString("FNum"));//关联编号
    		cashpecpay.setRelaNumType("ExchangeStock");//关联编号类型
    		//金额  * 方向
    		cashpecpay.setMoney(YssD.mul(rs.getDouble("FAltogetherCash"),rs.getDouble("FInOutType")));//金额
    		cashpecpay.setDataSource(1);//来源标志
    		cashpecpay.checkStateId = 1;
    		cashpecpay.setPortCode(rs.getString("FPortCode"));//组合代码
    		cashpecpay.setNum(rs.getString("FNum"));//编号
    		cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));//现金账户
    		cashpecpay.setCuryCode(rs.getString("FTradeCury"));//币种代码
    		cashpecpay.setInOutType(1);//方向
    		//cashpecpay.setInvestManagerCode();//投资经理代码
    		cashpecpay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
    		cashpecpay.setPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
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
     * 设置资金调拨子数据的基本数据
     */
    private TransferSetBean setTransferSet(ResultSet rs, boolean analy1, 
    		boolean analy2, boolean analy3) throws YssException {
        TransferSetBean transferSet = null;
        try {
            transferSet = new TransferSetBean();
            transferSet.setSPortCode(rs.getString("FPortCode"));//组合代码
            transferSet.setStrAttrClsCode(rs.getString("FAttrClsCode"));//所属分类
            
            //---设置分析代码 ---------------------------------------------------------
            transferSet.setSAnalysisCode1(analy1 ? rs.getString("FAnalysisCode1") : " ");
            transferSet.setSAnalysisCode2(analy2 ? rs.getString("FAnalysisCode2") : " ");
            transferSet.setSAnalysisCode3(analy3 ? rs.getString("FAnalysisCode3") : " ");
            //-----------------------------------------------------------------------
            //金额  * 方向
            transferSet.setDMoney(YssD.mul(rs.getDouble("FAltogetherCash"),rs.getInt("FInOutType")));//调拨金额
            transferSet.setDBaseRate(rs.getDouble("FBaseCuryRate"));//基础汇率
            transferSet.setDPortRate(rs.getDouble("FPortCuryRate"));//组合汇率
            transferSet.setSCashAccCode(rs.getString("FCashAccCode"));//现金帐户代码
        	transferSet.setIInOut(rs.getInt("FInOutType"));//资金流向 1代表流入;-1代表流出
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }

        return transferSet;
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
    
    /**设置现金调拨（主表）
     * @param rs
     * @param TypeCode
     * @param TsfTypeCode
     * @author shashijie ,2011-8-22 , STORY 1202
     * @modified 
     */
    private TransferBean setTransfer1(ResultSet rs,String TypeCode,String TsfTypeCode) throws YssException {
        TransferBean transfer = null;
        try {
            transfer = new TransferBean();
            transfer.setDtTransDate(rs.getDate("FOperDate"));//业务日期
            transfer.setDtTransferDate(rs.getDate("FMatureDate"));//调拨日期
            transfer.setStrTsfTypeCode(TypeCode);//调拨类型
            transfer.setStrSubTsfTypeCode(TsfTypeCode);//调拨子类型
            transfer.setFNumType("ExchangeStock");//编号类型(换股对价)
            transfer.setFRelaNum(rs.getString("FNum"));//关联编号
            transfer.setCprNum(rs.getString("FNum"));//现金应收应付编号
            transfer.setSrcCashAccCode("FCashAccCode");//来源帐户代码
            transfer.setStrSecurityCode(rs.getString("FSecurityCode"));//投资品种代码
            transfer.checkStateId = 1;
            transfer.setDataSource(1);
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
        return transfer; //返回资金调拨数据
    }
    
    /**设置资金调拨控制类
     * @param rs
     * @param analy1
     * @param analy2
     * @param analy3
     * @throws YssException
     * @author shashijie ,2011-8-22 , STORY 1202
     * @modified 
     */
    private void createPrincipalExtCashTransfer(ResultSet rs,boolean analy1, 
    		boolean analy2, boolean analy3) throws YssException {
        TransferBean transfer = null;//现金调拨（主表）
        TransferSetBean transferSet = null;//资金调拨(子表)
        ArrayList subTransfer = new ArrayList();//存放资金调拨(子表集合)
        try {
        	//设置资金调拨主表
        	if (rs.getInt("FInOutType")==1) {//流入(收入)
        		transfer = setTransfer1(rs,YssOperCons.YSS_ZJDBLX_Income,"02OT");
			} else {//流出(费用)
				transfer = setTransfer1(rs,YssOperCons.YSS_ZJDBLX_Fee,"03OT");
			}
            //资金调拨子表
        	transferSet = setTransferSet(rs, analy1, analy2, analy3);
        	subTransfer.add(transferSet);
        	 
        	transfer.setSubTrans(subTransfer);//将子数据放入资金调拨中
        	cashtransAdmin.addList(transfer);
        } catch (Exception e) {
            throw new YssException("生成协议定存本金提取的资金调拨出现异常！", e);
        } 
    }
    
}
