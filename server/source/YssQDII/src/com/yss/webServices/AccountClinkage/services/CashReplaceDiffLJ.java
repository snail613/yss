/**   
* @Title: CashAboutPL.java 
* @Package com.yss.webServices.AccountClinkage.services 
* @Description: TODO( ) 
* @author KR
* @date 2013-06-07 上午01:45:21 
* @version V4.0   
*/
package com.yss.webServices.AccountClinkage.services;

import java.sql.ResultSet;
import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;
import com.yss.util.YssException;
import com.yss.vsub.YssFinance;
import com.yss.webServices.AccountClinkage.AbsService;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.client.AccountClinkageService_Service;

/** 
 * @ClassName: CashAboutPL 
 * @Description: TODO(  ) 
 * @author KR 
 * @date 2013-06-07 上午01:45:21 
 *  add by huangqirong 2013-06-07 需求北京-[建设银行]QDII系统[高]20130419001
 *  3.1.5	获取现金差额和现金替代款（联机）
 */
public class CashReplaceDiffLJ extends AbsService {

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doReqBatch()
	 */
	@Override
	public void doReqBatch() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doReqLinkage()
	 */
	@Override
	public void doReqLinkage() {
		// TODO Auto-generated method stub
        
	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doReqOnLine()
	 */
	@Override
	public void doReqOnLine() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doResBatch()
	 */
	@Override
	public void doResBatch() {
		
	}
	
	/**
	 * 退款
	 * 赎回
	 * */
//	private String getRepairMoney(String groupCode ,String portCode , String date , String tradeType){
//		String sql = " select a.jjdm, a.zjlx, a.xwhy, a.frefunddate, " +
//					 " sum(round(a.fsumreturn * a.amount / p.fnormscale, 2)) as sumreturns, r.fagencycode " +
//					 " from (select a.jjdm, a.zjlx, a.amount, a.xwhy, b.frefunddate, b.fsumreturn, a.fstockholdercode " +
//					 " from (select a.fsecuritycode1 as jjdm, case when a.ftradetypecode = '102' then '02' " +
//					 " when a.ftradetypecode = '103' then '04' end as zjlx, a.fbargaindate as jyrq, " +
//					 " sum(a.ftradeamount) as amount,a.fportcode,a.fclearcode as xwhy,a.fstockholdercode" +
//					 " from tb_" + groupCode + "_etf_jsmxinterface a  where a.frecordtype = '003' " +
//					 " and a.ftradetypecode = '103' and a.fresultcode='0000' a.FPortCode = " + 
//                 												this.getPub().getDbLink().sqlString(portCode) +
//                 	 " group by a.fportcode, a.fsecuritycode1, a.fbargaindate, a.fclearcode, a.fstockholdercode," +
//                 	 " a.ftradetypecode) a join (select b.frefunddate, b.fbuydate, b.fmakeupdate1, " +
//                 	 " b.fsumreturn + nvl(money,0) as fsumreturn, tradetype from (select k.frefunddate," +
//                 	 " k.fbuydate, k.fmakeupdate1, sum(-k.fsumreturn) as fsumreturn, '04' as tradetype" +
//                 	 " from tb_" + groupCode + "_etf_standingbook k where k.frefunddate = " + 
//                 	 this.getPub().getDbLink().sqlDate(date) + 
//                     " and k.fbs = 'S' and k.fsecuritycode != ' ' group by k.frefunddate, k.fbuydate,k.fmakeupdate1) b" +
//                     " left join (select s.fdate, sum(s.ftotalmoney) as money from tb_" + groupCode + "_etf_stocklist s " +
//                     " where s.freplacemark = '6'  AND S.FDATE = (SELECT  DISTINCT K.FBUYDATE FROM " +
//                     " tb_" + groupCode + "_etf_standingbook K " +
//                     " WHERE K.FREFUNDDATE =" + this.getPub().getDbLink().sqlDate(date) + 
//                     " AND K.FBS='S') group by s.fdate) s on 1=1 " +                
//                     " ) b on a.jyrq = b.fbuydate and b.tradetype = a.zjlx) a join (select p.fportcode, p.fnormscale " +
//                     " from tb_" + groupCode + "_etf_param p) p on 1 = 1 join (select r.fseatcode, r.fagencycode from " +
//                     " TB_" + groupCode + "_ETF_BROKER r) r on a.xwhy = r.fseatcode group by a.jjdm, a.zjlx, a.xwhy, " +
//                     " a.frefunddate, r.fagencycode " ;
//		return sql;
//	}
	

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doResLinkage()
	 */
	@Override
	public void doResLinkage() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doResOnLine()
	 */
	@Override
	public void doResOnLine() {
		// TODO Auto-generated method stub
		this.getOnLineData();
	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#setDataType()
	 */
	@Override
	public void setDataType() {
		// TODO Auto-generated method stub
		this.dataType = 1; //联机
	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#setOperType()
	 */
	@Override
	public void setOperType() {
		this.operType = 1; //响应
	}
	
	/**
	 * 获取ETF组合对应的账户币种
	 * */
	private String getETFCuryCode(String groupCode , String portCode){
		String sql =" select Fcurycode from (select p.fportcode, p.fnormscale, FCashAccCode " +
				    " from tb_" +groupCode + "_etf_param p where FPortCode = " + 
				    this.getPub().getDbLink().sqlString(portCode) + " and fcheckstate = 1) para " +
				    " left join (select fcashacccode, fcurycode from tb_" + groupCode + "_para_cashaccount " +
				    " where fcheckstate = 1 and fportcode = " + this.getPub().getDbLink().sqlString(portCode) +
				    ") pca on para.fcashacccode = pca.fcashacccode " ;
		return sql;
	}
	
	/**
	 * 现金差额 进 退
	 * tradeType : 102 = 申购 , 103 = 赎回 
	 * */
	private String getCashDiff(String groupCode ,String portCode , String date , String tradeType){
		YssFinance finance = new YssFinance();
		finance.setYssPub(this.getPub());
		String setCode = "";
		try {
			setCode = finance.getBookSetId(groupCode , portCode);
		} catch (YssException e) {
			e.printStackTrace();
			this.setDoSign("1");
			this.setReplyRemark(this.getReplyRemark() + "获取套帐号出错：" + e.getMessage());
			System.out.println("获取套帐号出错：" + e.getMessage());
		}
		
		String sql = " select a.jjdm, a.zjlx, a.xwhy," + this.getPub().getDbLink().sqlDate(date) + " as frefunddate," +
					 " a.money * a.amount / p.fnormscale as sumreturns," +
					 " r.fagencycode ,pca.fcurycode as fcurycode" +
					 " from (select a.jjdm, a.zjlx, a.amount, a.xwhy, a.jyrq, b.money " +
					 " from (select a.fsecuritycode1 as jjdm," +
                     " '03' as zjlx," +
                     " sum(case when a.ftradetypecode = '103' then " +
                     " -a.ftradeamount when a.ftradetypecode = '102' then a.ftradeamount end) as amount," +
                     " a.fclearcode as xwhy, a.fbargaindate as jyrq, " +
                     " 0 " +
                     " from tb_" + groupCode + "_etf_jsmxinterface a " +                     
                     " where a.frecordtype = '003' and a.fresultcode='0000' " +
                     //WDay[<D1>;CH;-1] --当前日前的前一个进内工作日，或者交收日的前两个个工作日
                     " and a.fbargaindate = " + this.getPub().getDbLink().sqlDate(date) +
                     " and a.ftradetypecode = " + this.getPub().getDbLink().sqlString(tradeType) +
                     " and a.FPortCode = " + this.getPub().getDbLink().sqlString(portCode) + 
                     " group by a.fportcode,  a.fsecuritycode1, a.fbargaindate, a.fclearcode) a " +
                     " join (select round(b.fstandardmoneymarketvalue,4) as money, b.fdate as fnavdate " +
                     " from tb_" + groupCode + "_rep_guessvalue b where b.fportcode = " + 
                     this.getPub().getDbLink().sqlString(setCode) + " and b.facctcode like '9802') b on a.jyrq =" +
                     " b.fnavdate) a join (select p.fportcode, p.fnormscale,p.fcashacccode from tb_" + groupCode + 
                     "_etf_param p where p.FPortCode = " + this.getPub().getDbLink().sqlString(portCode) +
                     ") p on 1 = 1 left join (select r.fseatcode, r.fagencycode from " +
                     " TB_" + groupCode + "_ETF_BROKER r where r.fcheckstate=1) r on a.xwhy = r.fseatcode " +
                     "left join (select fcashacccode,fcurycode from tb_" + groupCode + "_para_cashaccount " +
                     " where fcheckstate = 1 and fportcode = " + this.getPub().getDbLink().sqlString(portCode) +") pca " +
                     " on p.fcashacccode =pca.fcashacccode" ;
		return sql;
	}
	
	/**
	 * 现金替代;退 补款
	 * 申购 都有退补款
	 * 退 为正，负为补
	 * */
	private String getReturnMoney(String groupCode ,String portCode, String date ,boolean isDaYu0){
		String sql = " select tbk.*,pca.fcurycode as fcurycode from " +
				" ( select a.fportcode ,a.jjdm, a.zjlx, a.xwhy, a.frefunddate, " +
				" sum(round(a.fsumreturn * a.amount / p.fnormscale, 2)) as sumreturns," +
        		" r.fagencycode from (select a.fportcode,a.jjdm, a.zjlx, a.amount," +
        		" a.xwhy, b.frefunddate, b.fsumreturn, a.fstockholdercode from (select a.fsecuritycode1 as jjdm," +
        		" a.fstockholdercode, case when a.ftradetypecode = '102' then '02' when a.ftradetypecode = '103' then " +
                " '04' end as zjlx, a.fbargaindate as jyrq, sum(a.ftradeamount) as amount, a.fportcode, " +
                " a.fclearcode as xwhy from tb_" + groupCode + "_etf_jsmxinterface a  where a.frecordtype = '003' " +
                " and a.FPortCode = " + this.getPub().getDbLink().sqlString(portCode) +
                " and a.ftradetypecode = '102' and a.fresultcode='0000' group by a.fportcode, a.fsecuritycode1, " +
                " a.fbargaindate, a.fclearcode, a.fstockholdercode, a.ftradetypecode) a join (select k.frefunddate," +
                " k.fbuydate, k.fmakeupdate1, sum(k.fsumreturn) as fsumreturn, '02' as tradetype from " +
                " tb_" + groupCode + "_etf_standingbook k where k.frefunddate = " + 
                this.getPub().getDbLink().sqlDate(date)+ " and k.FPortCode = " + 
                this.getPub().getDbLink().sqlString(portCode) +
                " and k.fbs = 'B'" + (isDaYu0 ? " and k.fsumreturn > 0 " : " and k.fsumreturn < 0 " ) +
                " and k.fsecuritycode != ' ' group by k.frefunddate, k.fbuydate, k.fmakeupdate1) b " +
                " on a.jyrq = b.fbuydate and b.tradetype = a.zjlx) a join (select p.fportcode, p.fnormscale " +
                " from tb_" + groupCode + "_etf_param p where FPortCode = " + 
                 												this.getPub().getDbLink().sqlString(portCode) +
                ") p on 1 = 1 join (select r.fseatcode, r.fagencycode from " +
                " TB_" + groupCode + "_ETF_BROKER r) r on a.xwhy = r.fseatcode " +
                " group by a.fportcode,a.jjdm, a.zjlx, a.xwhy, a.frefunddate, r.fagencycode " +
                ") tbk left join (select p.fportcode, p.fnormscale,FCashAccCode from tb_" + groupCode + "_etf_param p" +
                " where FPortCode = " + this.getPub().getDbLink().sqlString(portCode)+ " and fcheckstate = 1) para " +
                " on tbk.fportcode = para.fportcode left join (select fcashacccode, fcurycode from " +
                " tb_" + groupCode + "_para_cashaccount where fcheckstate = 1 and fportcode = " + 
                 this.getPub().getDbLink().sqlString(portCode)+ ") pca on para.fcashacccode = pca.fcashacccode";
		return sql;
	}
	
	/**
	 * 写入Txt文件
	 * */
	private void getOnLineData(){
		if(this.getRequestMsgXml() == null )
			return;
		
		if("1111".equalsIgnoreCase(this.getRequestMsgXml().getRootElement().element("head").elementText("replycode")))
			return;
		
		Element body = this.getRecodeEle(); 	//获取body标签
		String accountDate = body.element("ENTITY").element("accountsDate").getText();
		String productCode = body.element("ENTITY").element("productCode").getText();
		String assetPort = this.getRequestMsgXml().getRootElement().element("body").element("ENTITY").elementText("productCode");
		
		/**无资产组合则走批量接口*/
		if(assetPort == null || assetPort.trim().length() == 0){
			AccountClinkageService_Service service = new AccountClinkageService_Service();
			this.getRequestMsgXml().getRootElement().element("head").element("txcode").setText("AL044IT04");
			String result  = service.getAccountClinkageServicePort().doDeal(this.getRequestMsgXml().asXML());
			this.setResponesMsgXml(Console.parseXml(result));
			return;
		}
		Document doc = null;
		ResultSet rs1 = null;
		boolean isAppend = false;
		try {
			doc = Console.createXml(null, "100", "100", "1.0", "", this.getTxcode(), false);
			Element body1 = doc.getRootElement().addElement("body");
			
			String [] groupPorts = this.getPortCodeBySetCode(productCode);//根据资产代码获取组合代码
			System.out.println(groupPorts == null || groupPorts[0] == null || groupPorts[1] == null);
			//一下为俩个判断组合是否为空
			if(groupPorts == null || groupPorts[0] == null || groupPorts[1] == null){
				return;
			}else if("".equalsIgnoreCase(groupPorts[0].trim()) || "".equalsIgnoreCase(groupPorts[1].trim())){
				return;
			}
			
			if(!this.getPub().getDbLink().yssTableExist(("tb_" + groupPorts[0] + "_para_portfolio").toUpperCase()))
				return;
			
			Hashtable<String, String> htCuryToNums = this.getMarket(groupPorts[0] , "AC_DicCuryToNum", 
            "获取币种转编码出错：");
				
			rs1 = this.getPub().getDbLink().openResultSet("select * from tb_" + groupPorts[0] + 
					"_para_portfolio where fcheckstate = 1 and Fenabled = 1 and FAssetCode = " + 
					this.getPub().getDbLink().sqlString(productCode));
				
			while (rs1.next()) {
				String assetCode = rs1.getString("FAssetCode");
				String portCode = rs1.getString("FPortCode");

				if (isAppend)
					isAppend = false;

				String marginIn = this.getDatabySql(this.getCashDiff(
						groupPorts[0], portCode, accountDate, "102"),
						"sumreturns", "获取现金差额进款出错：");

				if (marginIn != null && marginIn.trim().length() > 0 && !isAppend 
						&& Double.parseDouble(marginIn) != 0)
					isAppend = true;

				String marginOut = this.getDatabySql(this.getCashDiff(
						groupPorts[0], portCode, accountDate, "103"),
						"sumreturns", "获取现金差额出款出错：");

				if (marginOut != null && marginOut.trim().length() > 0
						&& !isAppend && Double.parseDouble(marginOut) != 0)
					isAppend = true;

				String replaceFade = this.getDatabySql(this.getReturnMoney(
						groupPorts[0], portCode, accountDate, true),
						"sumreturns", "获取现金替代款退款出错：");

				if (replaceFade != null && replaceFade.trim().length() > 0
						&& !isAppend && Double.parseDouble(replaceFade) != 0)
					isAppend = true;

				String replaceFill = this.getDatabySql(this.getReturnMoney(
						groupPorts[0], portCode, accountDate, false),
						"sumreturns", "获取现金替代款退款出错：");

				if (replaceFill != null && replaceFill.trim().length() > 0
						&& !isAppend && Double.parseDouble(replaceFill) != 0)
					isAppend = true;

				String currency = this.getDatabySql(this.getETFCuryCode(
						groupPorts[0], portCode), "FcuryCode", "获取币种出错：");

				String tmp = "";
				if (htCuryToNums != null)
					tmp = htCuryToNums.get(currency);
				if (tmp != null && tmp.trim().length() > 0)
					currency = tmp;
				else
					currency = "156";

				if (isAppend) {
					Element record0 = body1.addElement("record");
					record0.addElement("productCode").setText(assetCode);
					record0.addElement("payDate").setText(accountDate);
					record0.addElement("marginIn").setText(marginIn);
					record0.addElement("marginOut").setText(marginOut);
					record0.addElement("replaceFade").setText(replaceFade);
					record0.addElement("replaceFill").setText(replaceFill);
					record0.addElement("currency").setText(currency);
				}
			}
		} catch (Exception e) {
			this.setDoSign("1");
			this.setReplyRemark(this.getReplyRemark() + "读取现金替代或差额数据出错：" + e.getMessage() + "\n");
			System.out.println("读取现金替代或差额数据出错：" + e.getMessage());
		}finally{
			this.setResponesMsgXml(doc);
			this.getPub().getDbLink().closeResultSetFinal(rs1);
		}
	}
}
