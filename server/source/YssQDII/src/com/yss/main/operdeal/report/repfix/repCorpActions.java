package com.yss.main.operdeal.report.repfix;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.util.*;
import com.yss.vsub.YssDbOperSql;

/**
 * MS01030 QDV4工银2010年3月05日02_A   
 * 关于公司行为信息表的需求   
 * @author Administrator
 *
 */
public class repCorpActions extends BaseBuildCommonRep {
	private String strActionTypeCode = "";//公司行为类型：分红、配股、送股、配股权证行权(认购行权)、换股等
	private String strSecurityCode = "";//证券代码
	private String strExchangeCode = "";//证券市场
	private String strExRightDate = "";//除权日(查询开始日)
	private String strPayDate = "";//到帐日(查询开始日)
	//---add by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
	private String strExRightEndDate = "";//除权日(查询结束日)
	private String strPayEndDate = "";//到帐日(查询结束日)
	//---add by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
	private String strPortCode = "";//组合代码
    private FixPub fixPub = null;
    
	boolean bDTT = false;
	boolean bBTT = false;
	boolean bRTT = false;
	
    public void initBuildReport(BaseBean bean) throws YssException {
		fixPub = new FixPub();
		fixPub.setYssPub(pub);
		String reqAry[] = null;
		CommonRepBean repBean = (CommonRepBean) bean;
		reqAry = repBean.getRepCtlParam().split("\n"); // 这里是要获得参数
		for (int i = 0; i < reqAry.length; i++) {
			int paraIndex = Integer.parseInt(reqAry[i].split("\r")[0]);
			switch (paraIndex) {
			case 1: {
				strSecurityCode = reqAry[i].split("\r")[1];
				break;
			}
			case 2: {
				strExchangeCode = reqAry[i].split("\r")[1];
				break;
			}
			case 3: {
				strExRightDate = reqAry[i].split("\r")[1].equalsIgnoreCase("9998-12-31")?"":reqAry[i].split("\r")[1];
				break;
			}
			case 4: {
				strPayDate = reqAry[i].split("\r")[1].equalsIgnoreCase("9998-12-31")?"":reqAry[i].split("\r")[1];
				break;
			}
			case 5: {
				strActionTypeCode = reqAry[i].split("\r")[1];
				break;
			}case 6: {
				strPortCode = reqAry[i].split("\r")[1];
				break;
			}
			//---add by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
			case 7: {
				strExRightEndDate = reqAry[i].split("\r")[1].equalsIgnoreCase("9998-12-31")?"":reqAry[i].split("\r")[1];
				break;
			}
			case 8: {
				strPayEndDate = reqAry[i].split("\r")[1].equalsIgnoreCase("9998-12-31")?"":reqAry[i].split("\r")[1];
				break;
			}
			//---add by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
			}
		}
    }
    
    
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        /***********************************************************************************
         * 由于版本22之后的版本中，权益信息设置，包括分红、送股、配股的表结构发生了调整，原有的
         * “权利比例FRATIO“调整为“税前权益比例FPreTaxRatio” 和 ”税后权益比例FAfterTaxRatio“
         * 此处需先判断表字段，才能对应做后续查询操作
         */
        bDTT = isFieldExist("TB_DATA_DIVIDEND","FRATIO");
        bBTT = isFieldExist("TB_DATA_BONUSSHARE","FRATIO");
        bRTT = isFieldExist("TB_DATA_RIGHTSISSUE","FRATIO");
        if(!strActionTypeCode.equalsIgnoreCase("80")){
        	sResult = buildShowData();
        }
        if(strActionTypeCode.equalsIgnoreCase("")||strActionTypeCode.equalsIgnoreCase("80")){
        	sResult += buildShowHG ();
        }
        if(sResult.length()>2){
        	sResult = sResult.substring(0, sResult.length() - 2);
        }
        return sResult;
    }

    //判断字段是否存在表中
    private boolean isFieldExist(String tabName,String Field)throws YssException{
    	ResultSet rs = null; 
    	boolean flag = false;
    	String query = "";
    	
        query = "select * from " +pub.yssGetTableName(tabName)+" where 1=2";
    	try {
			rs = dbl.openResultSet(query);
			flag = dbl.isFieldExist(rs, "FRATIO");
			return flag;
		} catch (Exception e) {
			throw new YssException("统计公司行为信息数据出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    }
    
    /**
     * add by songjie 2011.05.18
     * BUG 1819 QDV4工银2011年04月27日01_B
     * 拼接查询条件对应的sql语句
     * @param exRightField
     * @param payField
     * @return
     * @throws YssException
     */
    private String buildSql2(String exRightField, String payField)throws YssException{
    	String sql = "";
    	if(this.strExRightDate.equals("") && !this.strExRightEndDate.equals("")){
    		sql += " and " + exRightField + " < " + dbl.sqlDate(this.strExRightEndDate);
    	}else if(!this.strExRightDate.equals("") && this.strExRightEndDate.equals("")){
    		sql += " and " + exRightField + " > " + dbl.sqlDate(this.strExRightDate);
    	}else if(!this.strExRightDate.equals("") && !this.strExRightEndDate.equals("")){
    		sql += " and " + exRightField + " between " + dbl.sqlDate(this.strExRightDate) + " and " + dbl.sqlDate(this.strExRightEndDate);
    	}
    	if(this.strPayDate.equals("") && !this.strPayEndDate.equals("")){
    		sql += " and " + payField + " < " + dbl.sqlDate(this.strPayEndDate);
    	}else if(!this.strPayDate.equals("") && this.strPayEndDate.equals("")){
    		sql += " and " + payField + " > " + dbl.sqlDate(this.strPayDate);
    	}else if(!this.strPayDate.equals("") && !this.strPayEndDate.equals("")){
    		sql += " and " + payField + " between " + dbl.sqlDate(this.strPayDate) + " and " + dbl.sqlDate(this.strPayEndDate);
    	}
    	return sql;
    }
    
    /*******************************************************************************
     * 统计配股权证行权、送股、派息、权证送配数据
     * @return
     * @throws YssException
     */
    private String buildShowData() throws YssException{
    	ResultSet rs = null;
    	StringBuffer sbRetn = new StringBuffer();
    	String filterSql1="";
    	String sql1=" ",query="";
    	String sql2="";//add by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B
    	double amount=0;
    	//如果选择了交易类型则匹配上具体类型
    	if(this.strActionTypeCode.equalsIgnoreCase("")){
    		filterSql1 = " and (ftradetypecode="+dbl.sqlString(YssOperCons.YSS_JYLX_RGUXQ)+" or ftradetypecode="+
   			     dbl.sqlString(YssOperCons.YSS_JYLX_PX)+" or ftradetypecode="+dbl.sqlString(YssOperCons.YSS_JYLX_SG)+
   			     " or ftradetypecode="+dbl.sqlString(YssOperCons.YSS_JYLX_QZSP)+")";
    	}else{
    		filterSql1 = " and ftradetypecode="+dbl.sqlString(this.strActionTypeCode);
    	}
    	sql1 = " select ftradetypecode,ftradetypename from tb_base_tradetype where fcheckstate=1 "+filterSql1;

    	        //-----------------------------------------------------------------------------------------------//
    	//edit by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B 添加 a.FTradeTypeCode 和  d.fexchangecode
    	query = " select * from (select f.ftradetypename,a.FTradeTypeCode,a.FSecurityCode,a.FTradeAmount,d.fexchangecode,d.fsecurityname,e.fexchangename,d.ftradecury,a.FFactSettleMoney, "+
    	        " a.FPortSettleMoney,b.FRecordDate,b.FExRightDate,b.FPayDate,b.Fratio,b.FTaxRate,b.FTsecuritycode,b.FBeginScriDate, "+
    	        " b.FEndScriDate,b.FBeginTradeDate,b.FEndTradeDate ,b.FRIPrice from "+
    	        //---- 交易主表 -----------------------------------------------------------------------------//
    	        " (select fportcode,FSecurityCode,FBargainDate,FTradeAmount,FTradeTypeCode,FFactSettleMoney,round(FFactSettleMoney * FBaseCuryRate / FPortCuryRate, 2) as FPortSettleMoney from "+pub.yssGetTableName("TB_DATA_SUBTRADE")+" a1 where fcheckstate=1 and fportcode="+dbl.sqlString(this.strPortCode)+
    	        //只取交易子表中的派息、送股、权证送配、配股权证行权这些交易类型的业务数据
    	        " and exists (select * from ("+sql1+" )a2 where a1.ftradetypecode=a2.ftradetypecode)"+
    	        (this.strSecurityCode.equalsIgnoreCase("")?"":" and fsecuritycode="+dbl.sqlString(this.strSecurityCode))+
    	        //由于配股权证行权的交易日期是认购起始日至认购截止日这段时间的任意某天都可，所以如果交易类型包含了配股权证送配，就不能把除权日作为交易日来作为过滤条件进行
    	        //对交易数据过滤
    	        ((this.strActionTypeCode.equalsIgnoreCase("")||this.strActionTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_RGUXQ))?"":(this.strExRightDate.equalsIgnoreCase("")? "":" and FBargainDate="+dbl.sqlDate(this.strExRightDate)))+" )a"+
    	        //---------------------------------------------------------------------------------------------------------------------------------//
    	        " left join ( select FSecurityCode,FRecordDate,FDividendDate as FExRightDate,FDistributeDate as FPayDate,"+
    	        (!bDTT?" FPreTaxRatio as Fratio, FAfterTaxRatio as FTaxRate,":" Fratio,0 as FTaxRate")+
    	        " ' ' as FTsecuritycode,to_date('99981231','yyyymmdd') as FBeginScriDate,to_date('99981231','yyyymmdd') as FEndScriDate,to_date('99981231','yyyymmdd') as FBeginTradeDate,"+
    	        " to_date('99981231','yyyymmdd') as FEndTradeDate,0 as FRIPrice from "+pub.yssGetTableName("TB_DATA_DIVIDEND")+" where fcheckstate=1"+
    	        (this.strSecurityCode.equalsIgnoreCase("")?"":" and fsecuritycode="+dbl.sqlString(this.strSecurityCode))+
    	        buildSql2("FDividendDate", "FDistributeDate")+//add by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B
    	        //---delete by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
//    	        (this.strExRightDate.equalsIgnoreCase("")?"":" and FDividendDate="+dbl.sqlDate(this.strExRightDate))+
//    	        (this.strPayDate.equalsIgnoreCase("")?"":" and FDistributeDate="+dbl.sqlDate(this.strPayDate))+
    	        //---delete by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
    	        " union all select FSSecurityCode as FSecurityCode,FRecordDate,FExRightDate,FPayDate,"+
    	        (!bBTT?" FPreTaxRatio as Fratio,FAfterTaxRatio as FTaxRate,":"Fratio,0 as FTaxRate")+
    	        " ' ' as FTsecuritycode,to_date('99981231','yyyymmdd') as FBeginScriDate,to_date('99981231','yyyymmdd') as FEndScriDate,to_date('99981231','yyyymmdd') as FBeginTradeDate,"+
    	        " to_date('99981231','yyyymmdd') as FEndTradeDate,0 as FRIPrice from "+pub.yssGetTableName("TB_DATA_BONUSSHARE")+" where fcheckstate=1"+
    	        (this.strSecurityCode.equalsIgnoreCase("")?"":" and FSSecurityCode="+dbl.sqlString(this.strSecurityCode))+
    	        buildSql2("FExRightDate", "FPayDate")+//add by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B
    	        //---delete by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
//    	        (this.strExRightDate.equalsIgnoreCase("")?"":" and FExRightDate="+dbl.sqlDate(this.strExRightDate))+
//    	        (this.strPayDate.equalsIgnoreCase("")?"":" and FPayDate="+dbl.sqlDate(this.strPayDate))+
    	        //---delete by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
    	        " union all select FSecurityCode,FRecordDate,FExRightDate,FPayDate,"+
    	        (!bRTT?" FPreTaxRatio as Fratio,FAfterTaxRatio as FTaxRate,":"Fratio,0 as FTaxRate,")+
    	        " FTsecuritycode,FBeginScriDate,FEndScriDate,FBeginTradeDate,FEndTradeDate,FRIPrice from "+pub.yssGetTableName("TB_DATA_RIGHTSISSUE")+" where fcheckstate=1"+
    	        (this.strSecurityCode.equalsIgnoreCase("")?"":" and fsecuritycode="+dbl.sqlString(this.strSecurityCode))+
    	        buildSql2("FExRightDate", "FPayDate")+//add by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B
    	        //---delete by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
//    	        (this.strExRightDate.equalsIgnoreCase("")?"":" and FExRightDate="+dbl.sqlDate(this.strExRightDate))+
//    	        (this.strPayDate.equalsIgnoreCase("")?"":" and FPayDate="+dbl.sqlDate(this.strPayDate))+
    	        //---delete by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
    	        " )b on a.FSecurityCode = b.FSecurityCode and a.FBargainDate=b.FExRightDate"+
    	        ((this.strActionTypeCode.equalsIgnoreCase("")||this.strActionTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_RGUXQ))?" or (a.FSecurityCode = b.FSecurityCode and " +
    	        " a.ftradetypecode='30' and a.FBargainDate between FBeginScriDate and FEndScriDate) ":"")+
    	        //------------------------------------------------------------------------------------------------------------//
    	        " left join (select fsecuritycode,fsecurityname,fexchangecode,ftradecury from "+pub.yssGetTableName("tb_para_security")+" where fcheckstate=1 "+
    	        (this.strSecurityCode.equalsIgnoreCase("")?"":" and fsecuritycode="+dbl.sqlString(this.strSecurityCode))+
    	        (this.strExchangeCode.equalsIgnoreCase("")?"":" and fexchangecode="+dbl.sqlString(this.strExchangeCode))+
    	        " )d on a.FSecurityCode = d.FSecurityCode "+
    	        //-------------------------------------------------//
    	        " left join (select fexchangecode,fexchangename from tb_base_exchange where fcheckstate=1 "+
    	        (this.strExchangeCode.equalsIgnoreCase("")?"":" and fexchangecode="+dbl.sqlString(this.strExchangeCode))+
    	        " )e on d.fexchangecode=e.fexchangecode "+
    	        //------------------------------------------------//
    	        " left join ("+sql1+" )f on a.ftradetypecode = f.ftradetypecode"+
    	        //---edit by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
    	        " ) g where 1=1 " + 
    	        (this.strExchangeCode.equalsIgnoreCase("")?"":" and fexchangecode="+dbl.sqlString(this.strExchangeCode))+
    	        " order by g.ftradetypecode,g.FSecurityCode";
    	        //---edit by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B---//
		try {
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				StringBuffer rowBuf = new StringBuffer(); // 每行数据
				//1. 只有权益数据，但是没有维护权益数据的则不显示数据
				if(rs.getDate("FExRightDate")==null){
					continue;
				}
				amount = getStorageAmount(rs.getString("FSecurityCode"),rs.getDate("FExRightDate"));
				//2. 除权日没有库存数据，不显示
				if(amount <=0){
					continue;
				}
				/*****************************************************************
				 * 由于TB_DATA_DIVIDEND，TB_DATA_BONUSSHARE，TB_DATA_RIGHTSISSUE都没有交易类型字段，
				 * 而如果在这几张表里都维护了权益数据，仅仅通过证券代码、除权日期去匹配则有可能把其他的另外两种的权益信息也匹配出来了。
				 * 所以这里再通过资金流向，数量流向来过滤。
				 */
				if(rs.getString("ftradetypename").equalsIgnoreCase("分发派息")){
					//分发派息资金流入,数量方向 为无
					if(rs.getDouble("FTradeAmount")==0&&rs.getDouble("FFactSettleMoney")!=0&&rs.getString("FTsecuritycode").equalsIgnoreCase(" ")){
						rowBuf.append(rs.getString("ftradetypename")).append(",");
					}else{
						continue;
					}
				}else if(rs.getString("ftradetypename").equalsIgnoreCase("送股")){
					if(rs.getDouble("FTradeAmount")!=0&&rs.getDouble("FFactSettleMoney")==0&&rs.getString("FTsecuritycode").equalsIgnoreCase(" ")){
						rowBuf.append(rs.getString("ftradetypename")).append(",");
					}else{
						continue;
					}
				}else if(rs.getString("ftradetypename").equalsIgnoreCase("认购行权")){
					rowBuf.append("配股权证行权").append(",");
				}else{
					rowBuf.append(rs.getString("ftradetypename")).append(",");
				}
				rowBuf.append(rs.getString("FSecurityCode")).append(",");
				rowBuf.append(rs.getString("Fsecurityname")).append(",");
				rowBuf.append(rs.getString("FExchangeName")).append(",");
				rowBuf.append(rs.getDate("FRecordDate")).append(",");
				rowBuf.append(rs.getDate("FExRightDate")).append(",");
				rowBuf.append(rs.getDate("FPayDate")).append(",");
				
				/*********************************************************
				 * 说明： 
				 *   Fratio 字段取的是税前比例
				 *   FTaxRate 字段取的是税后比例
				 */
				if (rs.getDouble("FTaxRate") !=0) {
					    rowBuf.append(rs.getDouble("FTaxRate")).append(",");//分红比例
						rowBuf.append(YssFun.roundIt(YssD.div(rs.getDouble("Fratio"), rs.getDouble("FTaxRate")), 4)).append(",");//税率
					} else {
						rowBuf.append(rs.getDouble("Fratio")).append(",");
						rowBuf.append(rs.getDouble("FTaxRate")).append(",");
					}
				rowBuf.append(rs.getString("ftradecury")).append(",");
				rowBuf.append(amount).append(",");
				rowBuf.append(rs.getDouble("FFactSettleMoney")).append(",");
				rowBuf.append(rs.getDouble("FPortSettleMoney")).append(",");
				
				
				
				if(rs.getString("ftradetypename").equalsIgnoreCase("送股")||rs.getString("ftradetypename").equalsIgnoreCase("分发派息")){
					rowBuf.append("－").append(",");
					rowBuf.append("－").append(",");
					rowBuf.append("－").append(",");
				}else {
					rowBuf.append(rs.getString("FTsecuritycode")).append(",");
					rowBuf.append(rs.getDate("FBeginScriDate")).append(",");
					rowBuf.append(rs.getDate("FEndScriDate")).append(",");
				}
				if(rs.getString("ftradetypename").equalsIgnoreCase("送股")||rs.getString("ftradetypename").equalsIgnoreCase("分发派息")){
					rowBuf.append("－").append(",");
					rowBuf.append("－").append(",");
				}else {
					rowBuf.append(rs.getDate("FBeginTradeDate")).append(",");
					rowBuf.append(rs.getDate("FEndTradeDate")).append(",");
				}
				rowBuf.append(rs.getDouble("FRIPrice")).append(",");
				rowBuf.append("－").append(","); 
				
				sbRetn.append(fixPub.buildRowCompResult(rowBuf.toString(),"DSSICBCCS1030")).append("\r\n");
			}
			 return sbRetn.toString();
		} catch (Exception e) {
			throw new YssException("统计公司行为信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    
    private String buildShowHG () throws YssException{
    	ResultSet rs = null;
    	StringBuffer sbRetn = new StringBuffer();
    	String filterSql1="";
    	String query="";

    	try{
    		//edit by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B 添加 b.fexchangecode
    		query = " select * from (select d.ftradetypename,a.FSecurityCode,b.fsecurityname,c.fexchangename,b.fexchangecode,b.ftradecury,0 as FFactSettleMoney, a.famount, "+ 
    		        " 0 as FPortSettleMoney,'' as FRecordDate,a.foperdate as FExRightDate,a.fexchangedate as FPayDate,'' as Fratio," +
    		        " '' as FTaxRate,'' as FTsecuritycode,'' as FBeginScriDate, '' as FEndScriDate,'' as FBeginTradeDate,'' as FEndTradeDate ,0 as FRIPrice , a.finouttype from "+
    		        //-------------------------------------------------------------------------------------------------------------//
    		        " (select fnum,fexchangedate, foperdate, fsecuritycode, finouttype, case when finouttype='-1' then -famount else famount end as famount ,ftradetypecode from "+pub.yssGetTableName("tb_Data_Integrated")+
    		        "  where fcheckstate = 1 and ftradetypecode = '80' and fportcode ="+dbl.sqlString(this.strPortCode)+
    		        (this.strSecurityCode.equalsIgnoreCase("")?"":" and fsecuritycode="+dbl.sqlString(this.strSecurityCode))+
    		        (this.strExRightDate.equalsIgnoreCase("")?"":" and FOperDate="+dbl.sqlDate(this.strExRightDate))+
        	        (this.strPayDate.equalsIgnoreCase("")?"":" and FExchangeDate="+dbl.sqlDate(this.strPayDate))+ " order by fnum ) a"+
    		        //-------------------------------------------------------------------------------------------------------------//
	                " left join (select fsecuritycode,fsecurityname,fexchangecode,ftradecury from "+pub.yssGetTableName("tb_para_security")+" where fcheckstate=1 "+
	               (this.strSecurityCode.equalsIgnoreCase("")?"":" and fsecuritycode="+dbl.sqlString(this.strSecurityCode))+
	               (this.strExchangeCode.equalsIgnoreCase("")?"":" and fexchangecode="+dbl.sqlString(this.strExchangeCode))+
	               " )b on a.FSecurityCode = b.FSecurityCode "+
	               //-------------------------------------------------//
	               " left join (select fexchangecode,fexchangename from tb_base_exchange where fcheckstate=1 "+
	              (this.strExchangeCode.equalsIgnoreCase("")?"":" and fexchangecode="+dbl.sqlString(this.strExchangeCode))+
	              " )c on b.fexchangecode=c.fexchangecode "+
	              //------------------------------------------------//
	              " left join ( select ftradetypecode,ftradetypename from tb_base_tradetype where fcheckstate=1 and ftradetypecode='80')d on a.ftradetypecode = d.ftradetypecode" +
	              //add by songjie 2011.05.18 BUG 1819 QDV4工银2011年04月27日01_B
	              " ) e where 1=1 " + (this.strExchangeCode.equalsIgnoreCase("")?"":" and e.fexchangecode="+dbl.sqlString(this.strExchangeCode));
    
    		rs = dbl.openResultSet(query);
			while (rs.next()) {
				StringBuffer rowBuf = new StringBuffer(); // 每行数据
				rowBuf.append(rs.getString("ftradetypename")).append(",");
				rowBuf.append(rs.getString("FSecurityCode")).append(",");
				rowBuf.append(rs.getString("Fsecurityname")).append(",");
				rowBuf.append(rs.getString("FExchangeName")).append(",");
				rowBuf.append("－").append(",");
				rowBuf.append(rs.getDate("FExRightDate")).append(",");
				rowBuf.append(rs.getDate("FPayDate")).append(",");
				rowBuf.append(rs.getDouble("Fratio")).append(",");
				rowBuf.append(rs.getDouble("Fratio")).append(",");
				rowBuf.append(rs.getString("ftradecury")).append(",");
				rowBuf.append(rs.getDouble("famount")).append(",");

				rowBuf.append(rs.getDouble("FFactSettleMoney")).append(",");
				rowBuf.append(rs.getDouble("FPortSettleMoney")).append(",");
				rowBuf.append("－").append(",");
				rowBuf.append("－").append(",");
			    rowBuf.append("－").append(",");
				rowBuf.append("－").append(",");
				rowBuf.append("－").append(",");
				rowBuf.append(rs.getDouble("FRIPrice")).append(",");
				if(rs.getString("finouttype").equalsIgnoreCase("1")){
					rowBuf.append("换入").append(","); 
				
				}else{
					rowBuf.append("换出").append(","); 
				}
				sbRetn.append(fixPub.buildRowCompResult(rowBuf.toString(),"DSSICBCCS1030")).append("\r\n");
			}
			return sbRetn.toString();
    	}catch (Exception e) {
			throw new YssException("统计公司行为信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    private double getStorageAmount(String securityCode,Date exRightDate)throws YssException{
    	String query ="";
    	ResultSet rs = null;
    	double dStorageAmount=0;
    	YssDbOperSql dbOper = new YssDbOperSql(pub);
    	try{
    		query=" select fstorageamount from "+pub.yssGetTableName("tb_stock_security")+" where fcheckstate=1 and fportcode="+dbl.sqlString(this.strPortCode)
    		     +" and fsecuritycode="+dbl.sqlString(securityCode)+" and "+dbOper.sqlStoragEve(exRightDate);
    		rs = dbl.openResultSet(query);
    		while(rs.next()){
    			dStorageAmount = rs.getDouble("fstorageamount");
    		}
    		return dStorageAmount;
    	} catch (Exception e) {
			throw new YssException("统计公司行为信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
}
