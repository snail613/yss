/**
 * 
 */
package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.ibm.db2.jcc.a.db;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.parasetting.FeeBean;
import com.yss.main.parasetting.SecurityLendBean;
import com.yss.pojo.cache.YssCost;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * @包名：com.yss.main.operdata
 * @文件名：TradeSecurityLendBean.java
 * @创建人：zhangfa
 * @创建时间：2010-10-28
 * @版本号：0.1
 * @说明：TODO
 * <P> 
 * @修改记录
 * 日期        |   修改人       |   版本         |   说明<br>
 * ----------------------------------------------------------------<br>
 * 2010-10-28 | zhangfa | 0.1 |  
 */
public class TradeSecurityLendBean extends BaseDataSettingBean implements IDataSetting{
	private String num = ""; // 交易拆分数据流水号
	private String securityCode = ""; // 交易证券代码
	private String securityName = ""; // 交易证券名称
	private String bargainDate = "1900-01-01"; // 成交日期
	private String bargainTime = "00:00:00"; // 成交时间
	private String tradeCode = ""; // 交易方式代码
	private String tradeName = ""; // 交易方式名称
	private String settleDate = "1900-01-01"; // 结算日期
	private String settleTime = "00:00:00"; // 结算时间
	private String invMgrCode = ""; // 投资经理代码
	private String invMgrName = ""; // 投资经理名称
	private String portCode = ""; // 组合代码
	private String portName = ""; // 组合名称
	private String brokerCode = ""; // 券商代码
	private String brokerName = ""; // 券商名称
	private String attrClsCode = ""; // 所属分类代码
	private String attrClsName = ""; // 所属分类名称
	private String cashAcctCode = ""; // 现金帐户代码
	private String cashAcctName = ""; // 现金帐户名称
	private String agreementType = "";// 协议类型
	private double tradeAmount; // 交易数量
	private double tradePrice; // 交易价格
	private double totalCost; // 实付金额
	/** add by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
//	private String collateralCode = "";// 抵押物代码
//	private String collateralName = "";// 抵押物名称
	private String bailCode = "";// 保证金代码
	private String bailName = "";// 保证金名称
	private double bailMoney; //保证金金额
	/** -----end----- */
	 
	private String collateralRatio = "";// 抵押物比例
	
	private String fees = "";
	private String FFeeCode1 = ""; // 为了直接在后台进行汇总计算
	private String FFeeCode2 = "";
	private String FFeeCode3 = "";
	private String FFeeCode4 = "";
	private String FFeeCode5 = "";
	private String FFeeCode6 = "";
	private String FFeeCode7 = "";
	private String FFeeCode8 = "";

	private double FTradeFee1; // 为了直接在后台进行汇总计算
	private double FTradeFee2;
	private double FTradeFee3;
	private double FTradeFee4;
	private double FTradeFee5;
	private double FTradeFee6;
	private double FTradeFee7;
	private double FTradeFee8;

	private String periodDate = "1900-01-01"; // 期限日期
	private String lendRatio = ""; // 借贷利率
	private String strPeriodCode = ""; // 期间代码
	private String strPeriodName = ""; // 期间名称
	private String strFormulaCode = " "; // 利息公式代码
	private String strFormulaName = ""; // 利息公式名称
	
	private TradeSecurityLendBean filterType;
	
	private String sRecycled = "";
	
	
	
	 private boolean bOverGroup =false;//判断是否跨组合群
	 private String assetGroupCode = ""; //组合群代码
	 private String assetGroupName = ""; //组合群名称
	 
	 private YssCost cost = new YssCost();   //成本
	 
	 private double auto=0;//1为自动产生的证券借贷交易数据,默认为手工录入数据 0

	 /**
	  * 
	  * @throws YssException 
	 * @方法名：getYTSecStock
	  * @参数：
	  * @返回类型：String
	  * @说明：获取T-1日证券库存数据
	  */
	public String getYTSecStock() throws YssException {
		String reStr = "";
		String strSql = "";
        ResultSet rs = null;
        try{
        	strSql=" select FAmount,FBal from "+pub.yssGetTableName("Tb_Stock_SecRecPay")+
        	       " where FSecurityCode="+dbl.sqlString(this.securityCode)+" and FStorageDate="+dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.bargainDate), -1))+
        	       " and FPortCode="+dbl.sqlString(this.portCode)+
        	       " and FTsfTypeCode="+dbl.sqlString(YssOperCons.YSS_SECLEND_DBLX_SecBCost)+
        	       " and FSubTsfTypeCode="+dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BSC)+
        	       " and FCheckState=1";
        	rs=dbl.openResultSet(strSql);
        	while(rs.next()){
        		reStr=rs.getDouble("FAmount")+"zf";
        		reStr=reStr+rs.getDouble("FBal");
        	}
        }catch (Exception e) {
            throw new YssException("获取T-1日借入成本库存数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		return reStr;

	}
	/**
	 * 
	 * @方法名：getTradePrice
	 * @参数：
	 * @返回类型：double
	 * @说明：TODO
	 */
	public double getTradePrice(String bargainDate,String portCode,String securityCode) throws YssException{
		 
			 double close=0;
			 String strSql = "";
			 ResultSet rs = null;
			 try{
				 strSql=" select (case when bbb.FMktPriceCode='FClosingPrice' then FClosingPrice  else FYClosePrice end)  as FCsMarketPrice from "+pub.yssGetTableName("Tb_Data_MarketValue")+" dm "+
				        " join (select FMktSrcCode,FMktPriceCode from "+pub.yssGetTableName("Tb_Para_MTVMethod")+" a "+
				        " join (select  b.FMTVCode from "+pub.yssGetTableName("Tb_Para_MTVMethodLink")+" b "+
				        " join "+pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")+" c  on b.fmtvcode=c.fsubcode "+
				        " where b.FCheckState=1 and c.FCheckState=1 and b.FLinkCode="+dbl.sqlString(this.securityCode)+
				        " and c.FRelaType = 'MTV' and FPortCode in("+ operSql.sqlCodes(this.portCode) + ")) bb on a.fmtvcode=bb.fmtvcode "+
				        " )bbb on bbb.FMktSrcCode=dm.FMktSrcCode "+
				        " join (select FSecurityCode, max(FMktValueDate) as FMktValueDate from "+pub.yssGetTableName("Tb_Data_MarketValue")+
				        " where FCheckState=1 and FMktValueDate<="+dbl.sqlDate(this.bargainDate)+" group by FSecurityCode) dmm"+
				        " on dmm.FSecurityCode =dm.fsecuritycode and dm.FMktValueDate =dmm.FMktValueDate "+
				        //edit by songjie 2011.07.15 报错：未明确定义列
				        " where dm.FSecurityCode="+dbl.sqlString(this.securityCode);
				 rs=dbl.openResultSet(strSql);
			 while(rs.next()){
				 close=rs.getDouble("FCsMarketPrice");
			 }	 
			 }catch (Exception e) {
		         throw new YssException("获取当天收盘价出错", e);
		     } 
			 return close;
		 }
		/**
		double result=0.0;
		String strSql = "";
        ResultSet rs = null;
        try {
        	strSql="select FPRICE from "+pub.yssGetTableName("tb_data_valmktprice")+
        	       " where FVALDATE="+dbl.sqlDate(bargainDate) +" and FPORTCODE="+dbl.sqlString(this.portCode)+
        	       " and FSECURITYCODE="+dbl.sqlString(this.securityCode);
        	 rs = dbl.openResultSet(strSql);
        	 while(rs.next()){
        		 result=rs.getDouble("FPRICE");
        	 }
        	
        } catch (Exception e) {
            throw new YssException("获取证券行情信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		return result;
		*/
		
	
	 /**
     * 从回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("TB_DATA_SecLendTRADE") +
                    " where FNUM  = " +
                    dbl.sqlString(this.num);

                    dbl.executeSql(strSql);
                }

            }
           
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }
	 public void checkSetting() throws YssException {
		   
	        String strSql = "";
	        String[] arrData = null;
	        boolean bTrans = false; //代表是否开始了事务
	        Connection conn = dbl.loadConnection();

	        try {
	            conn.setAutoCommit(false);
	            bTrans = true;
	            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
	            if (sRecycled != "" && sRecycled != null) {
	                arrData = sRecycled.split("\r\n");
	                for (int i = 0; i < arrData.length; i++) {
	                    if (arrData[i].length() == 0) {
	                        continue;
	                    }
	                    this.parseRowStr(arrData[i]);
	                    strSql = "update " + pub.yssGetTableName("TB_DATA_SecLendTRADE") +
	                        " set FCheckState = " +
	                        this.checkStateId + ", FCheckUser = " +
	                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
	                        YssFun.formatDatetime(new java.util.Date()) + "'" +
	                        " where FNUM  = " +
	                        dbl.sqlString(this.num);


	                    //执行sql语句
	                    dbl.executeSql(strSql);
	                }

	            }
	           
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);

	        }

	        catch (Exception e) {
	            throw new YssException("审核证券借贷交易数据出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }
	//---------------------------------
	    }
	
	public void delSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql ="update " + pub.yssGetTableName("TB_DATA_SecLendTRADE") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FNUM  = " +
                dbl.sqlString(this.num);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除证券借贷交易数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}
	  public void loadFees(ResultSet rs) throws SQLException, YssException {
	        String sName = "";
	        double dFeeMoney = 0;
	        double dTotalFee = 0;
	        StringBuffer buf = new StringBuffer();
	        FeeBean fee = new FeeBean();
	        fee.setYssPub(pub);

	        for (int i = 1; i <= 8; i++) {
	            if (rs.getString("FFeeCode" + i) != null &&
	                rs.getString("FFeeCode" + i).trim().length() > 0) {
	                fee.setFeeCode(rs.getString("FFeeCode" + i));
	                fee.getSetting();
	                //------ add by wangzuochun 2010.09.11 MS01708    交易结算未结算中进行结算时会报错    QDV4建行2010年09月08日01_B    
	                //------ 根据交易子表中的费用代码去查费用设置中的费用，若费用不存在，则跳过此次循环；
	                if (fee.getFeeCode() == null){
	                	continue;
	                }
	                //----------MS01708-----------//
	                sName = fee.getFeeName();
	                if (rs.getString("FTradeFee" + i) != null) {
	                    dFeeMoney = rs.getDouble("FTradeFee" + i);
	                }
	                dTotalFee = YssD.add(dTotalFee, dFeeMoney);
	                buf.append(rs.getString("FFeeCode" + i)).append("\n");
	                buf.append(sName).append("\n");
	                buf.append(dFeeMoney).append("\n");
	                buf.append(fee.buildRowStr().replaceAll("\t", "~")).append(
	                    "\f\n");
	            }
	        }
	        //if (buf.toString().length() > 2) {
	        buf.append("total").append("\n");
	        buf.append("Total: ").append("\n");
	        buf.append(dTotalFee).append("\n");
	        fee.setAccountingWay("0"); //不计入成本
	        buf.append(fee.buildRowStr().replaceAll("\t", "~"));
	        this.fees = buf.toString();
	        //}
	        //else {
	        //   this.fees = "";
	        //}
	    }
	  /**
	   * 
	   * @throws YssException 
	 * @方法名：haveInterests
	   * @参数：
	   * @返回类型：boolean
	   * @说明：检查当天是否有送股权益数据
	   */
	  public boolean haveInterests(String portCode,String date) throws YssException{
		  boolean flag=false;
		  String strSql="";
		  ResultSet rs=null;
		  try{
			  
		  } catch (Exception e) {
	            throw new YssException("检查当天是否有送股权益数据出错", e);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
		  return flag;
		  
	  }
	  
	  /**
	   * 证券借贷收益计提查询数据
	   */
	  public String getListViewData2() throws YssException {
		  String sHeader = "";
		  String listView1ShowCols="";
		  String sShowDataStr = "";
	      String sAllDataStr = "";
	      StringBuffer bufShow = new StringBuffer();
	      StringBuffer bufAll = new StringBuffer();
	      StringBuffer bufSql = new StringBuffer();
	      ResultSet rs = null;
	      try{
	    	  sHeader="证券代码\t证券名称\t组合代码\t组合名称\t券商代码\t券商名称\t交易类型代码\t交易类型名称\t结算起始日代码\t结算起始日名称";
	    	  listView1ShowCols="FSECURITYCODE\tFSECURITYNAME\tFPORTCODE\tFPORTNAME\tFBROKERCODE\tFBROKERNAME\tFTRADETYPECODE\tFTRADETYPENAME\tFSettleDateCode\tFSettleDateName";
	    	  bufSql.append("select a.*,b.fportname,c.fsecurityname,d.fbrokername,e.FSTARTDATE as FSettleDateCode, f.FVocName as FSettleDateName from (")
	    	  //===============借入=======================
	    	  /** modified by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
//              .append("select fsecuritycode,fbrokercode,fportcode,sum(famount) as famount,'B' as ftradetypecode,'借' as ftradetypename from (")
//	    	  //获取前一天借入库存
//	    	  .append("select fsecuritycode,fanalysiscode2 as fbrokercode,fportcode,famount from ").append(pub.yssGetTableName("tb_stock_secrecpay"))
//	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
//	    	  .append(") and fstoragedate =").append(dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.filterType.bargainDate), -1))).append(" and fcheckstate = 1")
//	    	  .append(" and ftsftypecode = ").append(dbl.sqlString(YssOperCons.YSS_SECLEND_DBLX_SecBCost))
//	    	  .append(" and fsubtsftypecode = ").append(dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BSC))
	    	  
	    	  .append("select fsecuritycode,fbrokercode,fportcode,sum(famount) as famount,'B' as ftradetypecode,'借' as ftradetypename from (")
	    	  //获取前一天借入库存
	    	  .append("select fsecuritycode,fbrokercode,fportcode,famount from ").append(pub.yssGetTableName("Tb_Stock_SecOverSell"))
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and FLendDate =").append(dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.filterType.bargainDate), -1)))
	    	  .append(" and FSecurityState = ").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow))
              /** -----end----- */
	    	 
	    	  //当天交易数据，包括送股（关联证券借贷信息设置，区分计息起始日）
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,sum(ftradeamount) as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE")
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate = ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and case when FSTARTDATE = 'fsettledate' then fbargaindate  else fsettledate end  = fsettledate")
	    	  .append(" and fcheckstate = 1 and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BInPaySec)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode")    	  
	    	  //交易日期小于计提日期，且结算日期大于计提日期的借入交易数据，包括送股，不参与计提（关联证券借贷信息设置，区分计息起始日）
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,")
	    	  .append("case when FSTARTDATE = 'fsettledate' then -sum(ftradeamount) else 0 end as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  // 
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE")
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate < ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and fsettledate > ").append(dbl.sqlDate(this.filterType.bargainDate)).append(" and fcheckstate = 1 ")
	    	  .append(" and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BInPaySec)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode,FSTARTDATE")
	    	  //当天借入归还、借入归还送股数据
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,-sum(ftradeamount) as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE")
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate = ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and case when FSTARTDATE = 'fsettledate' then fbargaindate  else fsettledate end  = fsettledate")
	    	  .append(" and fcheckstate = 1 and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rcb)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rbsb)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode")
	    	  .append(" ) group by fsecuritycode,fbrokercode,fportcode")
	    	  //===============借出=======================
	    	  .append(" union all select fsecuritycode,fbrokercode,fportcode,sum(famount) as famount,'L' as ftradetypecode,'贷' as ftradetypename from (")
	    	  //获取前一天借出库存
	    	  .append("select fsecuritycode,fanalysiscode2 as fbrokercode,fportcode,famount from ").append(pub.yssGetTableName("tb_stock_secrecpay"))
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fstoragedate =").append(dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.filterType.bargainDate), -1))).append(" and fcheckstate = 1")
	    	  .append(" and ftsftypecode = ").append(dbl.sqlString(YssOperCons.YSS_SECLEND_DBLX_SecBCost))
	    	  .append(" and fsubtsftypecode = ").append(dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BLC))
	    	  //当天交易并结算的借出交易数据，包括送股
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,sum(ftradeamount) as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE")
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate = ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and case when FSTARTDATE = 'fsettledate' then fbargaindate  else fsettledate end  = fsettledate")
	    	  .append(" and fcheckstate = 1 and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Loan)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BOutRecSec)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode")	  
	    	  //交易日期小于计提日期，且结算日期大于计提日期的借出交易数据，包括送股，不参与计提
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,")
	    	  .append("case when FSTARTDATE = 'fsettledate' then -sum(ftradeamount) else 0  end as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE") 
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate < ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and fsettledate > ").append(dbl.sqlDate(this.filterType.bargainDate)).append(" and fcheckstate = 1 ")
	    	  .append(" and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Loan)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BOutRecSec)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode,FSTARTDATE")	   
	    	  //当天借出归还、借出归还送股数据
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,-sum(ftradeamount) as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE")
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate = ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and case when FSTARTDATE = 'fsettledate' then fbargaindate  else fsettledate end  = fsettledate")
	    	  .append(" and fcheckstate = 1 and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Lr)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Mhlr)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode")	 
	    	  .append(" ) group by fsecuritycode,fbrokercode,fportcode) a ")
	    	  //关联出组合名称
	    	  .append(" left join (select fportcode,fportname from ").append(pub.yssGetTableName("tb_para_portfolio"))
	    	  .append(" where fcheckstate = 1) b on b.fportcode = a.fportcode")
	    	  //关联出证券名称
	    	  .append(" left join (select fsecuritycode, fsecurityname from ").append(pub.yssGetTableName("tb_para_security"))
	    	  .append(" where fcheckstate = 1) c on c.fsecuritycode = a.fsecuritycode")
	    	  //关联出券商名称
	    	  .append(" left join (select fbrokercode, fbrokername from ").append(pub.yssGetTableName("tb_para_broker"))
	    	  .append(" where fcheckstate = 1) d on d.fbrokercode = a.fbrokercode")
	    	  //关联证券借贷信息设置取出计息起始日
	    	  .append(" left join (select fsecuritycode,fbrokercode,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) e on e.fsecuritycode = a.fsecuritycode and e.fbrokercode = a.fbrokercode")
	    	  //关联词汇名称
	    	  .append(" left join Tb_Fun_Vocabulary f on e.FSTARTDATE = f.FVocCode and f.FVocTypeCode = ").append(dbl.sqlString(YssCons.YSS_PARA_STARTSDATE));
	    	  
	    	  rs = dbl.openResultSet(bufSql.toString());
	        	 while (rs.next()) {
	        		 if(rs.getString("FSettleDateCode") == null){
	        			 throw new YssException("请设置证券代码【" + rs.getString("fsecuritycode") + "】，券商代码【" + rs.getString("fbrokercode") + "】对应的证券借贷信息！");
	        		 }
	        		 if(rs.getDouble("famount") <= 0){
	        			 continue;
	        		 }
	                 bufShow.append(super.buildRowShowStr(rs, listView1ShowCols)).
	                     append(YssCons.YSS_LINESPLITMARK);
	                 this.securityCode = rs.getString("FSECURITYCODE");
	                 this.securityName = rs.getString("FSECURITYNAME");
	                 this.portCode = rs.getString("FPORTCODE");
	                 this.portName = rs.getString("FPORTNAME");	     
	                 this.brokerCode = rs.getString("FBROKERCODE");
	                 this.brokerName = rs.getString("FBROKERNAME");
	                 this.tradeCode = rs.getString("FTRADETYPECODE");
	                 this.tradeName = rs.getString("FTRADETYPENAME");
	                 bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
	        	 }
	        	 if (bufShow.toString().length() > 2) {
	                 sShowDataStr = bufShow.toString().substring(0,
	                     bufShow.toString().length() - 2);
	             }

	             if (bufAll.toString().length() > 2) {
	                 sAllDataStr = bufAll.toString().substring(0,
	                     bufAll.toString().length() - 2);
	             }
	    	  return sHeader + "\r\f" + sShowDataStr + "\r\f" +
              sAllDataStr + "\r\f" +listView1ShowCols;
	      }catch (Exception e) {
	    	  throw new YssException("获取证券借贷收益计提数据出错", e);
	      } finally {
	    	  dbl.closeResultSetFinal(rs);
	      }
		}
	 /**
     * getListViewData1
     * 获取证券借贷交易数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = ""; //,sVocStr1="";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =" select * from ( select a.*,b.FSecurityName,c.FTradeTypeName,d.FInvMgrName,e.FPortName,g.FAttrClsName,f.fbrokername,h.fcashaccname,i.FBailAccName, "+
                      //modified by zhaoxianlin 20121107 STORY #3208  fcollateralname修改为 FBailAccName
            	    " j.FPeriodName,k.fformulaname,l.FCreatorName,m.FCheckUserName from "+pub.yssGetTableName("TB_DATA_SecLendTRADE")+" a "+
            	    " left join (select FSECURITYCODE,FSecurityName from "+ pub.yssGetTableName("tb_para_security")+" where FCheckState=1) b on b.FSecurityCode=a.FSECURITYCODE"+
            	    " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState=1) c on c.FTradeTypeCode=a.FTRADETYPECODE"+
            	 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
           
            	    " left join (select FInvMgrCode, FInvMgrName from  "+pub.yssGetTableName("Tb_Para_InvestManager")+
                    " where  FCheckState=1) d on d.FInvMgrCode=a.FINVMGRCODE "+
            	    
                    
                    //end by lidaolong
                    " left join (select FPortCode, FPortName  from  "+pub.yssGetTableName("Tb_Para_Portfolio")+" where FCheckState=1)e on e.FPortCode=a.FPortCode"+
            	    " left join (select FBrokerCode, FBrokerName from  "+pub.yssGetTableName("tb_Para_Broker")+" where FCheckState=1) f on f.FBrokerCode=a.FBROKERCODE"+
            	    " left join (select FAttrClsCode,FAttrClsName from "+pub.yssGetTableName("Tb_Para_AttributeClass")+" where FCheckState=1) g on g.FAttrClsCode=a.FAttrClsCode"+
            	    /**
            	    " left join (select FCashAccCode, FCashAccName from "+pub.yssGetTableName("Tb_Para_CashAccount")+"   where FCheckState=1) h on h.FCashAccCode=a.FCashAccCode"	+	*/
            	 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码 
            	
            	    " left join (select FCashAccCode, FCashAccName from "+pub.yssGetTableName("Tb_Para_CashAccount")+
                    " where  FCheckState=1 ) h on h.FCashAccCode=a.FCashAccCode "+
            	   
                    //end by lidaolong 


                    /** modified by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
                   // " left join (select FCollateralCode , FCollateralName  from "+pub.yssGetTableName("tb_para_Collateral")+"   where FCheckState=1) i on i.FCollateralCode=a.FCollateralCode"+
                    " left join (select FCashAccCode, FCashAccName as FBailAccName from "+pub.yssGetTableName("Tb_Para_CashAccount")+
                    " where  FCheckState=1 ) i on i.FCashAccCode=a.FBailAccCode "+
                    /** -----end----- */
                   
            	    " left join(select FPeriodCode,FPeriodName from "+ pub.yssGetTableName("Tb_Para_Period")+"  where FCheckState=1)j on j.FPeriodCode=a.FPeriodCode"+
            	    " left join (select FFormulaCode, FFormulaName from "+pub.yssGetTableName("Tb_Para_Performula")+"  where FCheckState=1) k on k.FFormulaCode=a.FFormulaCode"+
            	    " left join(select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) l on a.FCreator=l.FUserCode"+
            	    " left join(select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList ) m on a.FCheckUser=m.FUserCode ) "+                   
                   buildFilterSql(); 

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.num=rs.getString("FNUM");
                this.securityCode=rs.getString("FSECURITYCODE");
                this.securityName=rs.getString("FSecurityName");
                this.bargainDate=rs.getDate("FBARGAINDATE")+"";
                this.bargainTime=rs.getString("FBARGAINTIME");
                this.tradeCode=rs.getString("FTRADETYPECODE");
                this.tradeName=rs.getString("FTradeTypeName");
                this.settleDate=rs.getDate("FSETTLEDATE")+"";
                this.settleTime=rs.getString("FSETTLETIME");
                this.invMgrCode=rs.getString("FINVMGRCODE");
                this.invMgrName=rs.getString("FInvMgrName");
                this.portCode=rs.getString("FPORTCODE");
                this.portName=rs.getString("FPortName");           
                this.brokerCode=rs.getString("FBROKERCODE");
                this.brokerName=rs.getString("fbrokername");
                this.attrClsCode=rs.getString("FATTRCLSCODE");
                this.attrClsName=rs.getString("FAttrClsName");
                this.cashAcctCode=rs.getString("FCASHACCCODE");
                
                this.cashAcctName=rs.getString("fcashaccname");
                this.agreementType=rs.getString("FagreementType");
                this.tradeAmount=rs.getDouble("FTRADEAMOUNT");
                this.tradePrice=rs.getDouble("FTRADEPRICE");
                this.totalCost=rs.getDouble("FTOTALCOST");
                /** add by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
//                this.collateralCode=rs.getString("FCollateralCode");
//                this.collateralName=rs.getString("fcollateralname");
                this.bailCode=rs.getString("FBailAccCode");
                this.bailName=rs.getString("FBailAccName");
                this.bailMoney = rs.getDouble("FBailMoney");
                /** -----end----- */
                this.collateralRatio=rs.getDouble("FcollateralRatio")+"";
                loadFees(rs);
                this.periodDate=rs.getDate("FperiodDate")+"";
                this.lendRatio=rs.getDouble("FlendRatio")+"";
                this.strPeriodCode=rs.getString("FPeriodCode");
                this.strPeriodName=rs.getString("FPeriodName");
                this.strFormulaCode=rs.getString("FFormulaCode");
                this.strFormulaName=rs.getString("fformulaname");
 
                this.cost.setCost(rs.getDouble("FCost"));
                this.cost.setMCost(rs.getDouble("FMCost"));
                this.cost.setVCost(rs.getDouble("FVCost"));
                this.cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
                this.cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));
                this.cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));
                this.cost.setPortCost(rs.getDouble("FPortCuryCost"));
                this.cost.setPortMCost(rs.getDouble("FMPortCuryCost"));
                this.cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
                
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

           
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_DATA_AgreementType );
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取证券借贷交易数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#editSetting()
	 */
	public String editSetting() throws YssException {
		    String strSql = "";
	        boolean bTrans = false; //代表是否开始了事务
	        String strBargainDate = null; //保存成交日期
	        String strSubTradeDate = null; //保存交易拆分数据流水号中的日期
	        String newNum = null; //保存新的交易拆分数据流水号
	        Connection conn = dbl.loadConnection();
	        try {
	        	 strBargainDate = YssFun.formatDatetime(YssFun.toDate(this.bargainDate)).
	                substring(0, 8); //得到成交日期
	            strSubTradeDate = this.num.substring(1, 9); //得到交易拆分数据流水号中的日期
	            if (!strBargainDate.equals(strSubTradeDate)) {
	                newNum = "T" + strBargainDate +
	                    dbFun.getNextInnerCode(pub.yssGetTableName("tb_DATA_SecLendTRADE"),
	                                           dbl.sqlRight("FNUM", 6), "000000",
	                                           " where FNum like 'T" + strBargainDate + "%'", 1);

	                //得到修改日期的交易拆分数据流水号
	                newNum = newNum +
	                    dbFun.getNextInnerCode(pub.yssGetTableName("tb_DATA_SecLendTRADE"),
	                                           dbl.sqlRight("FNUM", 5), "00000",
	                                           " where FNum like '" + newNum.replaceAll("'", "''") + "%'");
	            }
	            strSql = "update " + pub.yssGetTableName("TB_DATA_SecLendTRADE") +
	            " set FNUM= " +
                (strBargainDate.equals(strSubTradeDate) == true ?
                 dbl.sqlString(this.num) : dbl.sqlString(newNum)) + 
	                " ,FSECURITYCODE = " +
	                dbl.sqlString(this.securityCode) + ", FBARGAINDATE = " +
	                dbl.sqlDate(this.bargainDate) + " ," +
	                " FBARGAINTIME  = " + dbl.sqlString(this.bargainTime) + "," +
	                " FTRADETYPECODE  = " + dbl.sqlString(this.tradeCode) + "," +
	                " FSETTLEDATE  = " + dbl.sqlDate(this.settleDate) + "," +
	                " FSETTLETIME  = " + dbl.sqlString(this.settleTime) + "," +
	                " FINVMGRCODE  = " + dbl.sqlString(this.invMgrCode.length() == 0 ? " " : this.invMgrCode) + "," +

	                " FPORTCODE  = " + dbl.sqlString(this.portCode) + "," +
	                " FBROKERCODE   = " + dbl.sqlString(this.brokerCode) + "," +
	                " FATTRCLSCODE  = " + dbl.sqlString(this.attrClsCode) + "," +
	                " FCASHACCCODE   = " + dbl.sqlString(this.cashAcctCode) + "," +
	                " FagreementType  = " + dbl.sqlString(this.agreementType) + "," +
	                " FTRADEAMOUNT   = " + this.tradeAmount + "," +
	                
	                
	                
	                " FTRADEPRICE  = " + this.tradePrice + "," +
	                " FTOTALCOST   = " + this.totalCost + "," +
	                /** modified by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
	                //" FCollateralCode  = " + dbl.sqlString(this.collateralCode) + "," +
	                " FBailAccCode =" + dbl.sqlString(this.bailCode) +","+  //保证金账户代码
	                " FBailMoney =" + this.bailMoney + ","+ //保证金金额
	                /** -----end----- */
	                " FcollateralRatio   = " +( this.collateralRatio.length()==0 ?"0": this.collateralRatio)+ "," +
	                this.operSql.buildSaveFeesSql(YssCons.OP_EDIT, this.fees) +
	                " FperiodDate  = " + dbl.sqlDate(this.periodDate) + "," +
	                

	                " FlendRatio  = " + this.lendRatio+ "," +
	                " FPeriodCode   = " + dbl.sqlString(this.strPeriodCode) + "," +
	                " FFormulaCode   = " +(strFormulaCode.length()==0 ?dbl.sqlString(" ") : dbl.sqlString(this.strFormulaCode)) + "," +
	                
	                " FCost ="+this.cost.getCost()+ "," +
	                " FMCost="+this.cost.getMCost()+ "," +
	                " FVCost="+this.cost.getVCost()+ "," +
	                " FBaseCuryCost="+this.cost.getBaseCost()+ "," +
	                " FMBaseCuryCost="+this.cost.getBaseMCost()+ "," +
	                " FVBaseCuryCost="+this.cost.getBaseVCost()+ "," +
	                " FPortCuryCost="+this.cost.getPortCost()+ "," +
	                " FMPortCuryCost="+this.cost.getPortMCost()+ "," +
	                " FVPortCuryCost="+this.cost.getPortVCost()+ "," +
	                
	                " FCheckState = " +
	                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
	                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
	                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
	                (pub.getSysCheckState() ? "' '" :
	                 dbl.sqlString(this.creatorCode)) +
	                " where FNUM = " +
	                dbl.sqlString(this.num)
	                ;
	            conn.setAutoCommit(false);
	            bTrans = true;
	            dbl.executeSql(strSql);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
	        }

	        catch (Exception e) {
	            throw new YssException("修改证券借贷交易数据出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }

	        return null;
	}
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#addSetting()
	 */
	public String addSetting() throws YssException {
		String subNum = "";
        String strNumDate = "";
		String strSql = "";
	    boolean bTrans = false; //代表是否开始了事务
	        Connection conn = dbl.loadConnection();
	        try {
	        	  strNumDate = YssFun.formatDatetime(YssFun.toDate(this.bargainDate)).
	                substring(0, 8);
	        	  this.num = strNumDate + dbFun.getNextInnerCode(
	                      pub.yssGetTableName("tb_DATA_SecLendTRADE"), dbl.sqlRight("FNUM", 6),
	                      "000000", " where FNum like 'T" + strNumDate + "000000".substring(0, 1) + "%'", 1);
	                  this.num = "T" + this.num;
	                  subNum = this.num + dbFun.getNextInnerCode(
	                      pub.yssGetTableName("TB_DATA_SecLendTRADE"),
	                      dbl.sqlRight("FNUM", 5), "00000",
	                      " where FNum like '" + this.num.replaceAll("'", "''") + "%'");
	        	  
	        	
	            strSql = "insert into " + pub.yssGetTableName("TB_DATA_SecLendTRADE") +
	                " (FNUM ,FSecurityCode,FBARGAINDATE ,FBARGAINTIME,FTRADETYPECODE,FSETTLEDATE,FSETTLETIME,FINVMGRCODE,FPORTCODE," +
	                "FBrokerCode,FATTRCLSCODE,FCASHACCCODE,FagreementType,FTRADEAMOUNT,FTRADEPRICE,FTOTALCOST,FbailAccCode,FbailMoney,FcollateralRatio," +
	                //modified by by zhaoxianlin 20121107 STORY #3208 FCollateralCode 替换为 FbailAccCode 并增加保证金金额字段FbailMoney
	                " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
	                " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," + 
	                " FperiodDate,FlendRatio,FPeriodCode,FFormulaCode," +
	                " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, FPortCuryCost,FMPortCuryCost, FVPortCuryCost,"+
	                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
	                " values(" + dbl.sqlString(subNum) + "," +
	                dbl.sqlString(this.securityCode) + "," +
	                dbl.sqlDate(this.bargainDate) + "," +
	                dbl.sqlString(this.bargainTime) + "," +
	                dbl.sqlString(this.tradeCode) + "," +
	                dbl.sqlDate(this.settleDate) + "," +
	                dbl.sqlString(this.settleTime) + "," +
	                dbl.sqlString( (this.invMgrCode.length() == 0) ? " " :
                        this.invMgrCode) + "," + //2007.11.09 修改 蒋锦 如果没有填入投资经理代码就用空格代替
                        dbl.sqlString(this.portCode) + "," +                       
	                dbl.sqlString(this.brokerCode) + "," +
	                dbl.sqlString(this.attrClsCode) + "," +
	                dbl.sqlString(this.cashAcctCode.length() == 0 ? " " :
                        this.cashAcctCode) + "," +
                        dbl.sqlString(this.agreementType) + "," +
                        this.tradeAmount + "," +
                        this.tradePrice + "," +
                        this.totalCost + "," +
                        /** add by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
                        //dbl.sqlString(this.collateralCode) + "," +
                        dbl.sqlString(this.bailCode) + "," +
                        this.bailMoney + "," + //保证金金额
                        /** -----end----- */
                        (this.collateralRatio.length()==0 ?  "0": this.collateralRatio) + "," +
                        this.operSql.buildSaveFeesSql(YssCons.OP_ADD, this.fees) +
                        dbl.sqlDate(this.periodDate) + "," +
                       ( this.lendRatio.length()==0 ? "0" : this.lendRatio) + "," +
                        dbl.sqlString(this.strPeriodCode) + "," +
                        dbl.sqlString(this.strFormulaCode.length()==0 ? " " :this.strFormulaCode ) + "," +
                        
                        this.cost.getCost()+","+this.cost.getMCost()+","+this.cost.getVCost()+","+this.cost.getBaseCost()+","+this.cost.getBaseMCost()+","+
                        this.cost.getBaseVCost()+","+this.cost.getPortCost()+","+this.cost.getPortMCost()+","+this.cost.getPortVCost()+","+
                        
	                (pub.getSysCheckState() ? "0" : "1") + "," +
	                dbl.sqlString(this.creatorCode) + "," +
	                dbl.sqlString(this.creatorTime) + "," +
	                (pub.getSysCheckState() ? "' '" :
	                 dbl.sqlString(this.creatorCode)) +" )" ;
	            conn.setAutoCommit(false);
	            bTrans = true;
	            dbl.executeSql(strSql);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
	        }

	        catch (Exception e) {
	            throw new YssException("增加证券借贷交易数据出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }

	        return null;
	}

	/**
     * parseRowStr
     * 解析证券借贷交易数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
           this.num = reqAry[0]; 
           this.securityCode=reqAry[1];
           this.securityName=reqAry[2];
           this.bargainDate = reqAry[3];
           this.bargainTime = reqAry[4];
           this.tradeCode = reqAry[5];
           this.tradeName=reqAry[6];
           this.settleDate = reqAry[7];
           this.settleTime = reqAry[8];
           this.invMgrCode = reqAry[9];
           this.invMgrName = reqAry[10];
           this.portCode = reqAry[11];
           
           this.portName=reqAry[12]; 
           this.brokerCode=reqAry[13];
           this.brokerName=reqAry[14];
           this.attrClsCode=reqAry[15];
           this.attrClsName=reqAry[16];
           this.cashAcctCode=reqAry[17];
           this.cashAcctName=reqAry[18];
           this.agreementType=reqAry[19];
           if (reqAry[20].length() != 0) {
               this.tradeAmount = Double.parseDouble(reqAry[20]);
           }
           if (reqAry[21].length() != 0) {
               this.tradePrice = Double.parseDouble(reqAry[21]);
           }

           
           if (reqAry[22].length() != 0) {
               this.totalCost = Double.parseDouble(reqAry[22]);
           }
           /** add by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
//           this.collateralCode=reqAry[23];
//           this.collateralName=reqAry[24];
           this.bailCode = reqAry[23];
           this.bailName =  reqAry[24];
           /** -----end----- */
          
           this.collateralRatio=reqAry[25];
           this.fees=reqAry[26];
           this.periodDate=reqAry[27];
           this.lendRatio=reqAry[28];
           this.strPeriodCode=reqAry[29];
           this.strPeriodName=reqAry[30];
           
           this.strFormulaCode=reqAry[31];
           this.strFormulaName=reqAry[32];
           
           this.assetGroupCode = reqAry[33];
           this.assetGroupName = reqAry[34]; 
           
           this.cost.parseRowStr(reqAry[35]);
        	   
           this.checkStateId = Integer.parseInt(reqAry[36]);
           
           if(reqAry[37].length() != 0){//add by zhaoxianlin 20121107 STORY #3208
        	   this.bailMoney = Double.parseDouble(reqAry[37]);
           }
           
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TradeSecurityLendBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析证券借贷交易数据请求出错", e);
        }
    }
	 /**
     * buildRowStr
     *
     * @return String
	 * @throws YssException 
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.num).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.bargainDate).append("\t");
        buf.append(this.bargainTime).append("\t");
        buf.append(this.tradeCode).append("\t");
        buf.append(this.tradeName).append("\t");
        buf.append(this.settleDate).append("\t");
        buf.append(this.settleTime).append("\t");
        
        buf.append(this.invMgrCode).append("\t");
        buf.append(this.invMgrName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.brokerCode).append("\t");
        buf.append(this.brokerName).append("\t");
        
        
        buf.append(this.attrClsCode).append("\t");
        buf.append(this.attrClsName).append("\t");
        buf.append(this.cashAcctCode).append("\t");
        buf.append(this.cashAcctName).append("\t");
        buf.append(this.agreementType).append("\t");
        
        buf.append(this.tradeAmount).append("\t");
        buf.append(this.tradePrice).append("\t");
        buf.append(this.totalCost).append("\t");
        /** add by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
//      buf.append(this.collateralCode).append("\t");
//      buf.append(this.collateralName).append("\t");
        buf.append(this.bailCode).append("\t");
        buf.append(this.bailName).append("\t");
        /** -----end----- */

        buf.append(this.collateralRatio).append("\t");
        buf.append(this.fees).append("\t");
        buf.append(this.periodDate).append("\t");
        buf.append(this.lendRatio).append("\t");
        
        buf.append(this.strPeriodCode).append("\t");
        buf.append(this.strPeriodName).append("\t");
        
        
        buf.append(this.strFormulaCode).append("\t");
		buf.append(this.strFormulaName).append("\t");
		
	    buf.append(this.assetGroupCode).append("\t");
	    buf.append(this.assetGroupName).append("\t");
	    
	    buf.append(this.cost.buildRowStr()).append("\t");
	    buf.append(this.bailMoney).append("\t");  //add by zhaoxianlin 20121107 STORY #3208
	    
        buf.append(super.buildRecLog());
        return buf.toString();
    }
    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
           
            if (this.filterType.portCode.length() != 0) {
                //-----------2009.05.20 蒋锦 添加 MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合-----------//
                //可选择多组合进行查询
                if (this.filterType.portCode.indexOf(",") != -1) {
                    sResult = sResult + " and FPortCode IN (" +
                        operSql.sqlCodes(this.filterType.portCode) + ")";
                } else {
                    sResult = sResult + " and FPortCode like '" +
                        filterType.portCode.replaceAll("'", "''") + "%'";
                }
            }
            if (this.filterType.tradeCode.length() != 0) {
                sResult = sResult + " and FTradeTypeCode = '" +
                    filterType.tradeCode.replaceAll("'", "''") + "'";
            }
           if (this.filterType.bargainDate.length() != 0 &&
                       !this.filterType.bargainDate.equals("9998-12-31")) {
            	//--- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能  add by jiangshichao---
                sResult = sResult + " and FBargainDate=  " +
                    dbl.sqlDate(filterType.bargainDate);
                //--- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能  end ------------------
            }
           /**
           if (this.filterType.settleDate.length() != 0 &&
                   !this.filterType.settleDate.equals("9998-12-31")) {
        	//--- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能  add by jiangshichao---
            sResult = sResult + " and FSETTLEDATE=  " +
                dbl.sqlDate(filterType.settleDate);
            //--- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能  end ------------------
             * 
             
        }
        */
            if (this.filterType.securityCode.length() != 0) {
                sResult = sResult + " and FSecurityCode = '" +
                    filterType.securityCode.replaceAll("'", "''") +
                    "'"
                    /* + "%'"*/; //2008.07.16 蒋锦 修改 暂时不使用通配符， 10G 数据库会报错
            }
            if (this.filterType.brokerCode.length() != 0) {
                sResult = sResult + " and FBROKERCODE = " +dbl.sqlString(filterType.brokerCode);
                  
            }
            if (this.filterType.cashAcctCode.length() != 0) {
                sResult = sResult + " and FCASHACCCODE = " +dbl.sqlString(filterType.cashAcctCode);
                  
            }
            
            if (this.filterType.agreementType.length() != 0) {
                sResult = sResult + " and FagreementType = " +dbl.sqlString(filterType.agreementType);
                  
            }
            if (this.filterType.invMgrCode.length() != 0) {
                sResult = sResult + " and FINVMGRCODE  = " +dbl.sqlString(filterType.invMgrCode);
                  
            }
            if (this.filterType.attrClsCode.length() != 0) {
                sResult = sResult + " and FATTRCLSCODE = " +dbl.sqlString(filterType.attrClsCode);
                  
            }
           
        }
        return sResult;

    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkInput(byte)
	 */
	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}







	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#getAllSetting()
	 */
	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#getSetting()
	 */
	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#saveMutliSetting(java.lang.String)
	 */
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssLogData#getBeforeEditData()
	 */
	public String getBeforeEditData() throws YssException {
		TradeSecurityLendBean tb=new TradeSecurityLendBean();
		String strSql = "";
		ResultSet rs = null;
		try{
			strSql =" select * from ( select a.*,b.FSecurityName,c.FTradeTypeName,d.FInvMgrName,e.FPortName,g.FAttrClsName,f.fbrokername,h.fcashaccname,i.fcollateralname, "+
    	    " j.FPeriodName,k.fformulaname,l.FCreatorName,m.FCheckUserName from "+pub.yssGetTableName("TB_DATA_SecLendTRADE")+" a "+
    	    " left join (select FSECURITYCODE,FSecurityName from "+ pub.yssGetTableName("tb_para_security")+" where FCheckState=1) b on b.FSecurityCode=a.FSECURITYCODE"+
    	    " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState=1) c on c.FTradeTypeCode=a.FTRADETYPECODE"+
    	    " left join (select fb.* from (select FInvMgrCode, max(FStartDate) as FStartDate from   "+pub.yssGetTableName("Tb_Para_InvestManager")+
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +" and FCheckState=1 group by FInvMgrCode ) fa join (select FInvMgrCode, FInvMgrName, FStartDate from " +pub.yssGetTableName("Tb_Para_InvestManager")+") fb on fa.FInvMgrCode = fb.FInvMgrCode and fa.FStartDate = fb.FStartDate ) d on d.FInvMgrCode=a.FINVMGRCODE "+
    	    " left join (select FPortCode, FPortName  from  "+pub.yssGetTableName("Tb_Para_Portfolio")+" where FCheckState=1)e on e.FPortCode=a.FPortCode"+
    	    " left join (select FBrokerCode, FBrokerName from  "+pub.yssGetTableName("tb_Para_Broker")+" where FCheckState=1) f on f.FBrokerCode=a.FBROKERCODE"+
    	    " left join (select FAttrClsCode,FAttrClsName from "+pub.yssGetTableName("Tb_Para_AttributeClass")+" where FCheckState=1) g on g.FAttrClsCode=a.FAttrClsCode"+
    	    /**
    	    " left join (select FCashAccCode, FCashAccName from "+pub.yssGetTableName("Tb_Para_CashAccount")+"   where FCheckState=1) h on h.FCashAccCode=a.FCashAccCode"	+	*/
    	   
    	 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    	
    	    " left join (select FCashAccCode, FCashAccName from "+pub.yssGetTableName("Tb_Para_CashAccount")+
            " where  FCheckState=1 ) h on h.FCashAccCode=a.FCashAccCode "+
    	    
            //end by lidaolong 
            " left join (select FCollateralCode , FCollateralName  from "+pub.yssGetTableName("tb_para_Collateral")+"   where FCheckState=1) i on i.FCollateralCode=a.FCollateralCode"+	
    	    " left join(select FPeriodCode,FPeriodName from "+ pub.yssGetTableName("Tb_Para_Period")+"  where FCheckState=1)j on j.FPeriodCode=a.FPeriodCode"+
    	    " left join (select FFormulaCode, FFormulaName from "+pub.yssGetTableName("Tb_Para_Performula")+"  where FCheckState=1) k on k.FFormulaCode=a.FFormulaCode"+
    	    " left join(select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) l on a.FCreator=l.FUserCode"+
    	    " left join(select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList ) m on a.FCheckUser=m.FUserCode ) "+                   
    	    " where FNum = " + dbl.sqlString(this.num);
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				tb.num=rs.getString("FNUM");
				tb.securityCode=rs.getString("FSECURITYCODE");
				tb.securityName=rs.getString("FSecurityName");
				tb.bargainDate=rs.getDate("FBARGAINDATE")+"";
				tb.bargainTime=rs.getString("FBARGAINTIME");
				tb.tradeCode=rs.getString("FTRADETYPECODE");
				tb.tradeName=rs.getString("FTradeTypeName");
				tb.settleDate=rs.getDate("FSETTLEDATE")+"";
				tb.settleTime=rs.getString("FSETTLETIME");
				tb.invMgrCode=rs.getString("FINVMGRCODE");
				tb.invMgrName=rs.getString("FInvMgrName");
				tb.portCode=rs.getString("FPORTCODE");
				tb.portName=rs.getString("FPortName");           
				tb.brokerCode=rs.getString("FBROKERCODE");
				tb.brokerName=rs.getString("fbrokername");
				tb.attrClsCode=rs.getString("FATTRCLSCODE");
				tb.attrClsName=rs.getString("FAttrClsName");
				tb.cashAcctCode=rs.getString("FCASHACCCODE");
				tb.cashAcctName=rs.getString("fcashaccname");
				tb.agreementType=rs.getString("FagreementType");
				tb.tradeAmount=rs.getDouble("FTRADEAMOUNT");
				tb.tradePrice=rs.getDouble("FTRADEPRICE");
				tb.totalCost=rs.getDouble("FTOTALCOST");
				/** add by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
//				tb.collateralCode=rs.getString("FCollateralCode");
//				tb.collateralName=rs.getString("fcollateralname");
				tb.bailCode=rs.getString("FCollateralCode");
				tb.bailName=rs.getString("fcollateralname");
				/** -----end----- */
				
				tb.collateralRatio=rs.getDouble("FcollateralRatio")+"";
				tb.periodDate=rs.getDate("FperiodDate")+"";
				tb.lendRatio=rs.getDouble("FlendRatio")+"";
				tb.strPeriodCode=rs.getString("FPeriodCode");
				tb.strPeriodName=rs.getString("FPeriodName");
				tb.strFormulaCode=rs.getString("FFormulaCode");
				tb.strFormulaName=rs.getString("fformulaname");
 
				tb.cost.setCost(rs.getDouble("FCost"));
				tb.cost.setMCost(rs.getDouble("FMCost"));
				tb.cost.setVCost(rs.getDouble("FVCost"));
				tb.cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
				tb.cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));
				tb.cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));
				tb.cost.setPortCost(rs.getDouble("FPortCuryCost"));
				tb.cost.setPortMCost(rs.getDouble("FMPortCuryCost"));
				tb.cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
			}
		}catch(Exception e){
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs); // 
			
		}
		return tb.buildRowStr();
	}


	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssConvert#getOperValue(java.lang.String)
	 */
	public String getOperValue(String sType) throws YssException {
		if (sType.equalsIgnoreCase("checkTradeCuryCode")) {
            return this.getCuryCode(this.securityCode);
        }
		if (sType.equalsIgnoreCase("getTradePrice")) {
            return this.getTradePrice(this.bargainDate,this.portCode,this.securityCode)+"";
        }
		if(sType.equalsIgnoreCase("getYTSecStock")){
			return this.getYTSecStock()+"";
		}
		
		return null;
	}

	 //通过证券代码来获得币种
    public String getCuryCode(String strSecurityCode) throws YssException {
        ResultSet rs = null;
        String curyCode = "";
        try {
            String strSql = "select * from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FSecurityCode=" +
                dbl.sqlString(strSecurityCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                curyCode = rs.getString("FTradeCury"); //彭鹏 2008.2.18 字段名写错
            }
            return curyCode;
        } catch (Exception e) {
            throw new YssException("得到货币代码出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }



	

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData3()
	 */
	public String getListViewData3() throws YssException {
		String sVocStr="";
		VocabularyBean vocabulary = new VocabularyBean();
        vocabulary.setYssPub(pub);
		sVocStr = vocabulary.getVoc(YssCons.YSS_DATA_AgreementType );
 
		return this.getListView1Headers() + "\r\f" + "" + "\r\f" + "" +"\r\f" +this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
		 //return this.getListView1Headers() + "\r\f" +" " + "\r\f" +" " + "\r\f" + this.getListView1ShowCols() ;
	
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData4()
	 */
	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData1()
	 */
	public String getListViewGroupData1() throws YssException {
		 this.bOverGroup = true;
	        String sAllGroup = ""; //定义一个字符用来保存执行后的结果传到前台
	        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码
	        //按组合群的解析符解析组合群代码
	        String[] assetGroupCodes = this.filterType.assetGroupCode.split(YssCons.YSS_GROUPSPLITMARK);
	        //按组合群的解析符解析组合代码
	        String[] strPortCodes = this.filterType.portCode.split(YssCons.YSS_GROUPSPLITMARK);
	        try {
	            for (int i = 0; i < assetGroupCodes.length; i++) { //循环遍历每一个组合群
	                this.assetGroupCode = assetGroupCodes[i]; //得到一个组合群代码
	                pub.setPrefixTB(this.assetGroupCode); //修改公共变量的当前组合群代码
	                this.portCode = strPortCodes[i]; //得到一个组合群下的组合代码
	                String sGroup = this.getListViewData2(); //调用以前的执行方法
	                sAllGroup = sAllGroup + sGroup + YssCons.YSS_GROUPSPLITMARK; //组合得到的结果集
	            }
	            if (sAllGroup.length() > 7) { //去除尾部多余的组合群解析符
	                sAllGroup = sAllGroup.substring(0, sAllGroup.length() - 7);
	            }
	        } catch (Exception e) {
	            throw new YssException(e);
	        } finally {
	            pub.setPrefixTB(sPrefixTB); //还原公共变的里的组合群代码
	            this.bOverGroup = false;
	        }
	        return sAllGroup; //把结果返回到前台进行显示
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData2()
	 */
	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData3()
	 */
	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData4()
	 */
	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData5()
	 */
	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData1()
	 */
	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData2()
	 */
	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData3()
	 */
	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData1()
	 */
	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData2()
	 */
	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData3()
	 */
	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public String getBargainDate() {
		return bargainDate;
	}

	public void setBargainDate(String bargainDate) {
		this.bargainDate = bargainDate;
	}

	public String getBargainTime() {
		return bargainTime;
	}

	public void setBargainTime(String bargainTime) {
		this.bargainTime = bargainTime;
	}

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getSettleTime() {
		return settleTime;
	}

	public void setSettleTime(String settleTime) {
		this.settleTime = settleTime;
	}

	public String getInvMgrCode() {
		return invMgrCode;
	}

	public void setInvMgrCode(String invMgrCode) {
		this.invMgrCode = invMgrCode;
	}

	public String getInvMgrName() {
		return invMgrName;
	}

	public void setInvMgrName(String invMgrName) {
		this.invMgrName = invMgrName;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getBrokerCode() {
		return brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getAttrClsCode() {
		return attrClsCode;
	}

	public void setAttrClsCode(String attrClsCode) {
		this.attrClsCode = attrClsCode;
	}

	public String getAttrClsName() {
		return attrClsName;
	}

	public void setAttrClsName(String attrClsName) {
		this.attrClsName = attrClsName;
	}

	public String getCashAcctCode() {
		return cashAcctCode;
	}

	public void setCashAcctCode(String cashAcctCode) {
		this.cashAcctCode = cashAcctCode;
	}

	public String getCashAcctName() {
		return cashAcctName;
	}

	public void setCashAcctName(String cashAcctName) {
		this.cashAcctName = cashAcctName;
	}

	public String getAgreementType() {
		return agreementType;
	}

	public void setAgreementType(String agreementType) {
		this.agreementType = agreementType;
	}

	public double getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public double getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(double tradePrice) {
		this.tradePrice = tradePrice;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	

	public String getBailCode() {
		return bailCode;
	}
	public void setBailCode(String bailCode) {
		this.bailCode = bailCode;
	}
	public String getBailName() {
		return bailName;
	}
	public void setBailName(String bailName) {
		this.bailName = bailName;
	}
	public double getBailMoney() {
		return bailMoney;
	}
	public void setBailMoney(double bailMoney) {
		this.bailMoney = bailMoney;
	}
	public String getCollateralRatio() {
		return collateralRatio;
	}

	public void setCollateralRatio(String collateralRatio) {
		this.collateralRatio = collateralRatio;
	}

	public String getFFeeCode1() {
		return FFeeCode1;
	}

	public void setFFeeCode1(String fFeeCode1) {
		FFeeCode1 = fFeeCode1;
	}

	public String getFFeeCode2() {
		return FFeeCode2;
	}

	public void setFFeeCode2(String fFeeCode2) {
		FFeeCode2 = fFeeCode2;
	}

	public String getFFeeCode3() {
		return FFeeCode3;
	}

	public void setFFeeCode3(String fFeeCode3) {
		FFeeCode3 = fFeeCode3;
	}

	public String getFFeeCode4() {
		return FFeeCode4;
	}

	public void setFFeeCode4(String fFeeCode4) {
		FFeeCode4 = fFeeCode4;
	}

	public String getFFeeCode5() {
		return FFeeCode5;
	}

	public void setFFeeCode5(String fFeeCode5) {
		FFeeCode5 = fFeeCode5;
	}

	public String getFFeeCode6() {
		return FFeeCode6;
	}

	public void setFFeeCode6(String fFeeCode6) {
		FFeeCode6 = fFeeCode6;
	}

	public String getFFeeCode7() {
		return FFeeCode7;
	}

	public void setFFeeCode7(String fFeeCode7) {
		FFeeCode7 = fFeeCode7;
	}

	public String getFFeeCode8() {
		return FFeeCode8;
	}

	public void setFFeeCode8(String fFeeCode8) {
		FFeeCode8 = fFeeCode8;
	}

	public double getFTradeFee1() {
		return FTradeFee1;
	}

	public void setFTradeFee1(double fTradeFee1) {
		FTradeFee1 = fTradeFee1;
	}

	public double getFTradeFee2() {
		return FTradeFee2;
	}

	public void setFTradeFee2(double fTradeFee2) {
		FTradeFee2 = fTradeFee2;
	}

	public double getFTradeFee3() {
		return FTradeFee3;
	}

	public void setFTradeFee3(double fTradeFee3) {
		FTradeFee3 = fTradeFee3;
	}

	public double getFTradeFee4() {
		return FTradeFee4;
	}

	public void setFTradeFee4(double fTradeFee4) {
		FTradeFee4 = fTradeFee4;
	}

	public double getFTradeFee5() {
		return FTradeFee5;
	}

	public void setFTradeFee5(double fTradeFee5) {
		FTradeFee5 = fTradeFee5;
	}

	public double getFTradeFee6() {
		return FTradeFee6;
	}

	public void setFTradeFee6(double fTradeFee6) {
		FTradeFee6 = fTradeFee6;
	}

	public double getFTradeFee7() {
		return FTradeFee7;
	}

	public void setFTradeFee7(double fTradeFee7) {
		FTradeFee7 = fTradeFee7;
	}

	public double getFTradeFee8() {
		return FTradeFee8;
	}

	public void setFTradeFee8(double fTradeFee8) {
		FTradeFee8 = fTradeFee8;
	}

	public String getPeriodDate() {
		return periodDate;
	}

	public void setPeriodDate(String periodDate) {
		this.periodDate = periodDate;
	}

	public String getLendRatio() {
		return lendRatio;
	}

	public void setLendRatio(String lendRatio) {
		this.lendRatio = lendRatio;
	}

	public String getStrPeriodCode() {
		return strPeriodCode;
	}

	public void setStrPeriodCode(String strPeriodCode) {
		this.strPeriodCode = strPeriodCode;
	}

	public String getStrPeriodName() {
		return strPeriodName;
	}

	public void setStrPeriodName(String strPeriodName) {
		this.strPeriodName = strPeriodName;
	}

	public String getStrFormulaCode() {
		return strFormulaCode;
	}

	public void setStrFormulaCode(String strFormulaCode) {
		this.strFormulaCode = strFormulaCode;
	}

	public String getStrFormulaName() {
		return strFormulaName;
	}

	public void setStrFormulaName(String strFormulaName) {
		this.strFormulaName = strFormulaName;
	}

	public TradeSecurityLendBean getFilterType() {
		return filterType;
	}

	public void setFilterType(TradeSecurityLendBean filterType) {
		this.filterType = filterType;
	}

	public String getFees() {
		return fees;
	}

	public void setFees(String fees) {
		this.fees = fees;
	}
	public String getAssetGroupCode() {
		return assetGroupCode;
	}
	public void setAssetGroupCode(String assetGroupCode) {
		this.assetGroupCode = assetGroupCode;
	}
	public String getAssetGroupName() {
		return assetGroupName;
	}
	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}
	public YssCost getCost() {
		return cost;
	}
	public void setCost(YssCost cost) {
		this.cost = cost;
	}
	public double getAuto() {
		return auto;
	}
	public void setAuto(double auto) {
		this.auto = auto;
	}
	
	/**
	 * TODO <Method comments>
	 * @return
	 * @author shashijie ,2011-1-19  需求:STORY #113 证券借贷业务需求  		
	 * 			TASK #2267::证券借贷业务需求 - 调度方案中收益计提增加证券借贷计息的处理
	 * @modified
	 */
	public String getIncomeTypeData(String dCurDate,String sPort) throws YssException{
		StringBuffer strSql = new StringBuffer();;
		ResultSet rs = null;
		StringBuffer strResult = new StringBuffer();
		try {
			if (this.filterType == null) {
                this.filterType = new TradeSecurityLendBean();
                this.filterType.setYssPub(pub);
                this.filterType.bargainDate = dCurDate;
                this.filterType.portCode = sPort;
            }
            
			strSql.append("select a.*,e.FSTARTDATE as FSettleDateCode from (")
	    	  //===============借入=======================
	    	  .append("select fsecuritycode,fbrokercode,fportcode,sum(famount) as famount,'B' as ftradetypecode,'借' as ftradetypename from (")
	    	  //获取前一天借入库存
	    	  .append("select fsecuritycode,fanalysiscode2 as fbrokercode,fportcode,famount from ").append(pub.yssGetTableName("tb_stock_secrecpay"))
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fstoragedate =").append(dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.filterType.bargainDate), -1))).append(" and fcheckstate = 1")
	    	  .append(" and ftsftypecode = ").append(dbl.sqlString(YssOperCons.YSS_SECLEND_DBLX_SecBCost))
	    	  .append(" and fsubtsftypecode = ").append(dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BSC))
	    	  //当天交易数据，包括送股（关联证券借贷信息设置，区分计息起始日）
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,sum(ftradeamount) as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE")
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate = ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and case when FSTARTDATE = 'fsettledate' then fbargaindate  else fsettledate end  = fsettledate")
	    	  .append(" and fcheckstate = 1 and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BInPaySec)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode")    	  
	    	  //交易日期小于计提日期，且结算日期大于计提日期的借入交易数据，包括送股，不参与计提（关联证券借贷信息设置，区分计息起始日）
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,")
	    	  .append("case when FSTARTDATE = 'fsettledate' then -sum(ftradeamount) else 0 end as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE")
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate < ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and fsettledate > ").append(dbl.sqlDate(this.filterType.bargainDate)).append(" and fcheckstate = 1 ")
	    	  .append(" and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BInPaySec)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode,FSTARTDATE")
	    	  //当天借入归还、借入归还送股数据
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,-sum(ftradeamount) as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE")
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate = ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and case when FSTARTDATE = 'fsettledate' then fbargaindate  else fsettledate end  = fsettledate")
	    	  .append(" and fcheckstate = 1 and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rcb)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rbsb)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode")
	    	  .append(" ) group by fsecuritycode,fbrokercode,fportcode")
	    	  //===============借出=======================
	    	  .append(" union all select fsecuritycode,fbrokercode,fportcode,sum(famount) as famount,'L' as ftradetypecode,'贷' as ftradetypename from (")
	    	  //获取前一天借出库存
	    	  .append("select fsecuritycode,fanalysiscode2 as fbrokercode,fportcode,famount from ").append(pub.yssGetTableName("tb_stock_secrecpay"))
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fstoragedate =").append(dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.filterType.bargainDate), -1))).append(" and fcheckstate = 1")
	    	  .append(" and ftsftypecode = ").append(dbl.sqlString(YssOperCons.YSS_SECLEND_DBLX_SecBCost))
	    	  .append(" and fsubtsftypecode = ").append(dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BLC))
	    	  //当天交易并结算的借出交易数据，包括送股
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,sum(ftradeamount) as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE")
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate = ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and case when FSTARTDATE = 'fsettledate' then fbargaindate  else fsettledate end  = fsettledate")
	    	  .append(" and fcheckstate = 1 and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Loan)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BOutRecSec)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode")	  
	    	  //交易日期小于计提日期，且结算日期大于计提日期的借出交易数据，包括送股，不参与计提
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,")
	    	  .append("case when FSTARTDATE = 'fsettledate' then -sum(ftradeamount) else 0 end as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE") 
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate < ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and fsettledate > ").append(dbl.sqlDate(this.filterType.bargainDate)).append(" and fcheckstate = 1 ")
	    	  .append(" and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Loan)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BOutRecSec)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode,FSTARTDATE")	   
	    	  //当天借出归还、借出归还送股数据
	    	  .append(" union all select st.fsecuritycode,st.fbrokercode,fportcode ,-sum(ftradeamount) as famount from ").append(pub.yssGetTableName("TB_DATA_SecLendTRADE"))
	    	  .append(" st left join (select FSECURITYCODE,FBROKERCODE,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) sl on sl.fsecuritycode = st.fsecuritycode and sl.fbrokercode = st.FBROKERCODE")
	    	  .append(" where fportcode in(").append(this.operSql.sqlCodes(this.filterType.portCode))
	    	  .append(") and fbargaindate = ").append(dbl.sqlDate(this.filterType.bargainDate))
	    	  .append(" and case when FSTARTDATE = 'fsettledate' then fbargaindate  else fsettledate end  = fsettledate")
	    	  .append(" and fcheckstate = 1 and ftradetypecode in(").append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Lr)).append(",")
	    	  .append(dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Mhlr)).append(") group by st.fsecuritycode,st.fbrokercode,fportcode")	 
	    	  .append(" ) group by fsecuritycode,fbrokercode,fportcode) a ")
	    	  //关联出组合名称
	    	  .append(" left join (select fportcode,fportname from ").append(pub.yssGetTableName("tb_para_portfolio"))
	    	  .append(" where fcheckstate = 1) b on b.fportcode = a.fportcode")
	    	  //关联出证券名称
	    	  .append(" left join (select fsecuritycode, fsecurityname from ").append(pub.yssGetTableName("tb_para_security"))
	    	  .append(" where fcheckstate = 1) c on c.fsecuritycode = a.fsecuritycode")
	    	  //关联出券商名称
	    	  .append(" left join (select fbrokercode, fbrokername from ").append(pub.yssGetTableName("tb_para_broker"))
	    	  .append(" where fcheckstate = 1) d on d.fbrokercode = a.fbrokercode")
	    	  //关联证券借贷信息设置取出计息起始日
	    	  .append(" left join (select fsecuritycode,fbrokercode,FSTARTDATE from ").append(pub.yssGetTableName("TB_PARA_SECURITYLEND"))
	    	  .append(" where fcheckstate = 1) e on e.fsecuritycode = a.fsecuritycode and e.fbrokercode = a.fbrokercode")
	    	  //关联词汇名称
	    	  .append(" left join Tb_Fun_Vocabulary f on e.FSTARTDATE = f.FVocCode and f.FVocTypeCode = ").append(dbl.sqlString(YssCons.YSS_PARA_STARTSDATE));
	        rs = dbl.openResultSet(strSql.toString());
	        while(rs.next()){
	        	strResult.append(rs.getString("FSecurityCode")).append("\t");
	        	strResult.append(rs.getString("FPortCode")).append("\t");
	        	strResult.append(rs.getString("FBrokerCode")).append("\t");
	        	strResult.append(rs.getString("FTradetypeCode")).append("\t");
	        	strResult.append(rs.getString("FSettleDateCode")).append("\t");
	        	strResult.append(",\t");
	        }
	        if(strResult.length() > 1){
	    		strResult.delete(strResult.length() - 1, strResult.length());
	    	}
	        return strResult.toString();
	    } catch (Exception e) {
	        throw new YssException("获取回购信息代码出错！", e);
	    } finally {
	        dbl.closeResultSetFinal(rs);
	    }
	}

}
