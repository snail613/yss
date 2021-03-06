package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import com.yss.commeach.EachGetPubPara;
import com.yss.commeach.EachRateOper;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.platform.pfoper.pubpara.PubParaBean;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * add by songjie 2012.01.16 
 * STORY 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A
 * @author 宋洁
 *
 */
public class OperAutoPayCmsn extends BaseOperManage {
	CashTransAdmin cashtransAdmin = null;
	CashPayRecAdmin cashpayrecadmin = null;
	EachRateOper rateOper = null;
	
	public void doOpertion() throws YssException {
		//String strSql = "";
		ResultSet rs = null;
		Connection conn = dbl.loadConnection();
		String strParas = "";
		String[] Paras = null,sPorts=null,sBrokers=null,sSeats=null,sPayStyle=null,sHoliday=null,sDelayDay=null;
		//Date dWorkDate = null;
   	 	boolean bTrans = false;
   	 	
   	 	StringBuffer queryBuf = new StringBuffer();
		try{
			/**shashijie 2012-6-5 BUG 4727 当未导入通用参数时不给出未设置通参提示*/
			if (isHaveParas()) {
				this.sMsg="    未导入通参,不执行该业务";
				return ;
			}
			/**end*/
			
			cashtransAdmin = new CashTransAdmin(); // 生成资金调拨控制类
			cashtransAdmin.setYssPub(pub);
			
   		 	cashpayrecadmin = new CashPayRecAdmin(); //生成现金应收应付控制类
   		 	cashpayrecadmin.setYssPub(pub);
			
            rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
            
            EachGetPubPara pubPara = new EachGetPubPara();
            pubPara.setYssPub(pub);
            
            conn.setAutoCommit(false);
            bTrans = true;
   		 	
            //获取自动支付佣金设置   STORY #2024 系统可以自动按年、月、日、季支付佣金，且可以根据券商支付。 add by jiangshichao 
            strParas = pubPara.getAutoPayCmsnSet(this.sPortCode);
            
            Paras = strParas.split("\f");
            
            sDelayDay =  Paras[0].split(","); //延迟日期
            sPayStyle = Paras[1].split(",");  //支付方式
            sBrokers = Paras[2].split(",");   //券商代码
            sHoliday = Paras[3].split(",");   //节假日群代码
            sPorts = Paras[4].split(",");     //组合代码
            sSeats = Paras[5].split(",");	  //席位号
            
            for(int i=0;i<sPorts.length;i++){
            	
            	//如果勾选了实收实付包含佣金，则不再进行处理
            	if(getiiExchangeFhggain(sPorts[i])){
            		continue;
            	}
            	
            	//通过支付方式和延迟日期来判断当前日期是否为支付日期,如果不是支付日期则跳过不再执行下去 STORY #2024 系统可以自动按年、月、日、季支付佣金，且可以根据券商支付。 add by jiangshichao 
//            	dWorkDate = getWorkDate(sPayStyle[i],sDelayDay[i],sHoliday[i]);
//            	if(YssFun.dateDiff(dWorkDate, dDate)!=0){
//            		continue;
//            	}
            	//--- STORY #2024 系统可以自动按年、月、日、季支付佣金，且可以根据券商支付。 add by jiangshichao  
            	queryBuf.append(" select cash.fportcode,cash.fanalysiscode1,cash.fanalysiscode2,cash.fanalysiscode3,cash.fcashacccode,cash.FCuryCode,cash.fattrclscode, ");
            	queryBuf.append(" sum(cash.fmoney) as fmoney,s.FBrokerCode,s.FSeatCode from");
            	queryBuf.append(" (select cash1.*, cash2.FCuryCode from");
            	queryBuf.append(" (select fportcode,fanalysiscode1,fanalysiscode2,fanalysiscode3,fcashacccode, fmoney,frelanum,fattrclscode from ");
            	queryBuf.append(pub.yssGetTableName("Tb_Data_CashPayRec")).append(" where FRelaType = 'subtrade' and FSubTsfTypeCode = '07FE' and FCheckState = 1 and  ");
            	queryBuf.append(" FPortCode =").append(dbl.sqlString(sPorts[i])).append(" )cash1");
            	queryBuf.append(" left join (select FCashAccCode, FCuryCode from  ").append(pub.yssGetTableName("Tb_Para_Cashaccount"));
            	queryBuf.append(" where FCheckState = 1) cash2 on cash1.FCashAccCode = cash2.FCashAccCode)cash");
            	queryBuf.append("  join ");
            	queryBuf.append(" (select s1.fNum as FRelaNum, s1.FSecurityCode,s1.FBrokerCode,s1.FSeatCode from ");
            	
            	queryBuf.append(" (select fNum, FSecurityCode,FBrokerCode, ");
            	//对席位代码进行特殊处理，避免设置了席位代码但不安席位支付的被分组处理
            	queryBuf.append(" case  when FSeatCode is null then ' ' when instr(").append(dbl.sqlString(Paras[5])).append(",FSeatCode)<>0 then ").append(dbl.sqlString(sSeats[i])).append(" else ' '");
            	queryBuf.append(" end as FSeatCode from  ").append(pub.yssGetTableName("Tb_Data_SubTrade"));
            	queryBuf.append(" where ").append(getFactSettleDateWhereSql(sPayStyle[i],sDelayDay[i],sHoliday[i])).append(" and FCheckState = 1 and");
            	queryBuf.append(" fportcode=").append(dbl.sqlString(sPorts[i])).append(" and FBrokerCode=").append(dbl.sqlString(sBrokers[i]));
            	if(sSeats[i].equalsIgnoreCase("space")){
            		//如果没有设置席位的只过滤到券商即可
            		//注意：这里必须要把不按席位支付的放在最后处理，不然会把按席位统计的佣金也将被记入
            		//注意点2： 做自动支付必须进行自动佣金支付设置
            		queryBuf.append(" and (FSeatCode not in (").append(operSql.sqlCodes(Paras[5])).append(")or FSeatCode is null)) s1 join (select FSecurityCode from  ");
            	}else{
            		//如果设置了按席位支付的则要明细到券商
            		queryBuf.append(" and FSeatCode =").append(dbl.sqlString(sSeats[i])).append(" ) s1 join (select FSecurityCode from  ");
            	}
            	
            	queryBuf.append(pub.yssGetTableName("Tb_Para_security")).append(" where FExchangeCode in ('CG', 'CS', 'CY') ");
            	queryBuf.append(" and FCheckState = 1)s2 on s1.FSecurityCode = s2.FSecurityCode)s ");
            	queryBuf.append(" on cash.FRelaNum = s.FRelaNum ");
            	queryBuf.append(" group by cash.fportcode,cash.fanalysiscode1,cash.fanalysiscode2,cash.fattrclscode,cash.fanalysiscode3,cash.fcashacccode,s.FBrokerCode,s.FSeatCode,cash.FCuryCode");

            			rs = dbl.queryByPreparedStatement(queryBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            			while(rs.next()){
            				createCashTransfer(rs);//生成资金调拨数据
            				createCashPayRec(rs);//生成现金应收应付数据
            			}
            			 
            			dbl.closeResultSetFinal(rs);
            			queryBuf.setLength(0);
            }
            
            if(cashpayrecadmin != null && this.cashtransAdmin != null){
            	
            	cashtransAdmin.insert("", dDate, dDate, YssOperCons.YSS_ZJDBLX_Fee,
    				YssOperCons.YSS_ZJDBZLX_FE_RecDomCmsnFEE, "", "", "", "", "",
    				"DomCmsnFee", "", 1, "", this.sPortCode, 0, "", "", "", true, "", "");
    			
                cashpayrecadmin.insert(dDate, YssOperCons.YSS_ZJDBLX_Fee, 
                		YssOperCons.YSS_ZJDBZLX_FE_RecDomCmsnFEE, this.sPortCode,
                		1, false, "", "DomCmsnFee", false);
            
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
                //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
                //当日产生数据，则认为有业务。
                if(cashtransAdmin.getAddList()==null || cashtransAdmin.getAddList().size()==0){
                	this.sMsg="        当日无业务";
                }
            }
		} catch(Exception e) {
			throw new YssException(e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**shashijie 2012-6-5 BUG 4727 判断是否导入通参  :没有导入返回 true*/
	private boolean isHaveParas() throws YssException {
		PubParaBean pubpara = new PubParaBean();
        pubpara.setYssPub(pub);
		pubpara.setParaGroupCode("operationDeal");
		pubpara.setPubParaCode("autopaycmsnset");
		pubpara.setCtlGrpCode("autopaycmsnset");
		String paras = pubpara.getOperValue("isHaveParas");
		if ("false".equals(paras.trim())) {
			return true;
		} else {
			return false;
		}
	}

	private String getFactSettleDateWhereSql(String sPayStyle,String sDelayDay,String sHoliday)throws YssException{
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
		Date dWorkDate = null;
		StringBuffer sWhereSql = new StringBuffer();
		
		try{
			if(sPayStyle.equalsIgnoreCase("0")){
				//按日支付
				//判断是否为工作日，如果不是工作日，则不进行操作
				if(operDeal.isWorkDay(sHoliday, dDate, 0)){
					dWorkDate = operDeal.getWorkDay(sHoliday, dDate, -YssFun.toInt(sDelayDay));
					sWhereSql.append("FFactSettleDate =").append(dbl.sqlDate(dWorkDate));
				}else{
					sWhereSql.append(" 1 = 2");
				}
	        			
			}else if(sPayStyle.equalsIgnoreCase("1")){
				//按月支付
				if(operDeal.isWorkDay(sHoliday, dDate, 0)){
					Date PreWorkDate = operDeal.getWorkDay(sHoliday, dDate, -YssFun.toInt(sDelayDay));//延迟天数前那个工作日
					if(YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))==YssFun.getDay(PreWorkDate)){
						//月末刚好是工作日的情况 
						sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-"+YssFun.getMonth(PreWorkDate)+"-01"));
						sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-"+YssFun.getMonth(PreWorkDate)+"-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))));
					}else{
						//月末不是工作日的情况
						PreWorkDate = operDeal.getWorkDay(sHoliday, dDate, -(YssFun.toInt(sDelayDay)+1));//延迟天数前再往前一个工作日。
						if(YssFun.getMonth(dDate)-YssFun.getMonth(PreWorkDate)>0 && !operDeal.isWorkDay(sHoliday, YssFun.parseDate(YssFun.getYear(PreWorkDate)+"-"+YssFun.getMonth(PreWorkDate)+"-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))), 0)){
							//如果月份不一致则认为是支付日
							sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-"+YssFun.getMonth(PreWorkDate)+"-01"));
							sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-"+YssFun.getMonth(PreWorkDate)+"-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))));
						}else{
							sWhereSql.append(" 1 = 2");
						}
						
					}
					
				}else{
					sWhereSql.append(" 1 = 2");
				}
			}else if(sPayStyle.equalsIgnoreCase("2")){
				//按季支付
				Date PreWorkDate = operDeal.getWorkDay(sHoliday, dDate, -YssFun.toInt(sDelayDay));//延迟天数前那个工作日
				
				if(YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))==YssFun.getDay(PreWorkDate)){
					if(1<=YssFun.getMonth(PreWorkDate)&&YssFun.getMonth(PreWorkDate)<=3){
						sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-01-01"));
						sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-03-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))));
					}else if(4<=YssFun.getMonth(PreWorkDate)&&YssFun.getMonth(PreWorkDate)<=6){
						sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-04-01"));
						sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-06-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))));
					}else if(7<=YssFun.getMonth(PreWorkDate)&&YssFun.getMonth(PreWorkDate)<=9){
						sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-07-01"));
					    sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-09-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))));
					}else if(10<=YssFun.getMonth(PreWorkDate)&&YssFun.getMonth(PreWorkDate)<=12){
						sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-10-01"));
					    sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-12-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))));
					}
				}
				else {
					PreWorkDate = operDeal.getWorkDay(sHoliday, dDate, -(YssFun.toInt(sDelayDay)+1));//延迟天数前再往前一个工作日。
					
					if(YssFun.getMonth(dDate)-YssFun.getMonth(PreWorkDate)>0 && !operDeal.isWorkDay(sHoliday, YssFun.parseDate(YssFun.getYear(PreWorkDate)+"-"+YssFun.getMonth(PreWorkDate)+"-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))),0)){
						if(1<=YssFun.getMonth(PreWorkDate)&&YssFun.getMonth(PreWorkDate)<=3){
							sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-01-01"));
							sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-03-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))));
						}else if(4<=YssFun.getMonth(PreWorkDate)&&YssFun.getMonth(PreWorkDate)<=6){
							sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-04-01"));
							sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-06-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))));
						}else if(7<=YssFun.getMonth(PreWorkDate)&&YssFun.getMonth(PreWorkDate)<=9){
							sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-07-01"));
						    sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-09-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))));
						}else if(10<=YssFun.getMonth(PreWorkDate)&&YssFun.getMonth(PreWorkDate)<=12){
							sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-10-01"));
						    sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-12-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))));
						}
					}else{
						sWhereSql.append(" 1 = 2");
					}
				}
				
				
			}else if(sPayStyle.equalsIgnoreCase("3")){
				//按年支付
				Date PreWorkDate = operDeal.getWorkDay(sHoliday, dDate, -YssFun.toInt(sDelayDay));//延迟天数前那个工作日
				if(YssFun.getMonth(PreWorkDate)==12 && YssFun.getDay(PreWorkDate)==31){
					sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-01-01"));
					sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-12-31"));
				}else{
					PreWorkDate = operDeal.getWorkDay(sHoliday, dDate, -(YssFun.toInt(sDelayDay)+1));//延迟天数前再往前一个工作日。
					if(YssFun.getYear(dDate)-YssFun.getYear(PreWorkDate)>1 && !operDeal.isWorkDay(sHoliday, YssFun.parseDate(YssFun.getYear(PreWorkDate)+"-"+YssFun.getMonth(PreWorkDate)+"-"+YssFun.endOfMonth(YssFun.getYear(PreWorkDate),YssFun.getMonth(PreWorkDate))),0)){
						sWhereSql.append("FFactSettleDate between").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-01-01"));
						sWhereSql.append(" and ").append(dbl.sqlDate(YssFun.getYear(PreWorkDate)+"-12-31"));
					}else{
						sWhereSql.append(" 1 = 2");
					}
				}
				
			}
			
			return sWhereSql.toString();
		}catch(Exception e){ 
			throw new YssException(e);
		}
		
	}
	
	private Date getWorkDate(String sPayStyle,String sDelayDay,String sHoliday)throws YssException{
		Date dWorkDate = null;
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
		try{
			
			if(sPayStyle.equalsIgnoreCase("0")){
				//按日支付
				dWorkDate = operDeal.getWorkDay(sHoliday, dDate, 0);
			}else if(sPayStyle.equalsIgnoreCase("1")){
				//按月支付
				Date date = YssFun.parseDate(YssFun.getYear(dDate)+"-"+YssFun.getMonth(dDate)+"-"+YssFun.endOfMonth(YssFun.getYear(dDate),YssFun.getMonth(dDate)), "yyyy-MM-dd");
				dWorkDate = operDeal.getWorkDay(sHoliday, YssFun.addDay(date, YssFun.toInt(sDelayDay)), 0);
			}else if(sPayStyle.equalsIgnoreCase("2")){
				//按季支付
				
				if(1<=YssFun.getMonth(dDate)&&YssFun.getMonth(dDate)<=3){
					Date date = YssFun.parseDate(YssFun.getYear(dDate)+"-03"+"-"+YssFun.endOfMonth(YssFun.getYear(dDate),YssFun.getMonth(dDate)), "yyyy-MM-dd");
					dWorkDate = operDeal.getWorkDay(sHoliday, YssFun.addDay(date, YssFun.toInt(sDelayDay)), 0);
				}else if(4<=YssFun.getMonth(dDate)&&YssFun.getMonth(dDate)<=6){
					Date date = YssFun.parseDate(YssFun.getYear(dDate)+"-06"+YssFun.endOfMonth(YssFun.getYear(dDate),YssFun.getMonth(dDate)), "yyyy-MM-dd");
					dWorkDate = operDeal.getWorkDay(sHoliday, YssFun.addDay(date, YssFun.toInt(sDelayDay)), 0);
				}else if(7<=YssFun.getMonth(dDate)&&YssFun.getMonth(dDate)<=9){
					Date date = YssFun.parseDate(YssFun.getYear(dDate)+"-09"+YssFun.endOfMonth(YssFun.getYear(dDate),YssFun.getMonth(dDate)), "yyyy-MM-dd");
					dWorkDate = operDeal.getWorkDay(sHoliday, YssFun.addDay(date, YssFun.toInt(sDelayDay)), 0);
				}else if(10<=YssFun.getMonth(dDate)&&YssFun.getMonth(dDate)<=12){
					Date date = YssFun.parseDate(YssFun.getYear(dDate)+"-12"+YssFun.endOfMonth(YssFun.getYear(dDate),YssFun.getMonth(dDate)), "yyyy-MM-dd");
					dWorkDate = operDeal.getWorkDay(sHoliday, YssFun.addDay(date, YssFun.toInt(sDelayDay)), 0);
				}
			}else if(sPayStyle.equalsIgnoreCase("3")){
				//按年支付
				Date date = YssFun.parseDate(YssFun.getYear(dDate)+"-12-31", "yyyy-MM-dd");
				dWorkDate = operDeal.getWorkDay(sHoliday, YssFun.addDay(date, YssFun.toInt(sDelayDay)), 0);
			}
			return dWorkDate;
		}catch(Exception e){
			throw new YssException(e);
		}
	}
	
	
	/**
     * 得到设置的回购收益的小数位数
     * @author fanghaoln 
     * @serialData 20100427
     * @see MS01079 QDV4招商基金2010年4月9日01_B 
     * @param portCode String
     * @return int
     * @throws YssException
     */
    public boolean getiiExchangeFhggain(String portCode) throws YssException{
    	String strSql = "";
        ResultSet rs = null;

        boolean isYsBhYj = false;
        try {
                strSql = "select * from "+ pub.yssGetTableName("TB_DAO_ReadType") + " where FPortCode = " +
                dbl.sqlString(portCode) + " and fparameter ='04' and FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode());
                rs = dbl.openResultSet(strSql);//查出设置的小数位数
                if (rs.next()) {
                	isYsBhYj = true;
                }
        } catch (Exception e) {
        	throw new YssException( e);
        } finally {
        	 dbl.closeResultSetFinal(rs);
        }
    	return isYsBhYj;
    }
	
	public void initOperManageInfo(Date dDate, String portCode)
	throws YssException {
		this.dDate = dDate; // 业务日期
		this.sPortCode = portCode; // 组合
	}
	
	/**
	 * 结算日生成应付佣金对应的资金调拨数据
	 * @param rs
	 * @throws YssException
	 */
	private void createCashTransfer(ResultSet rs) throws YssException {
		TransferBean transfer = null;
		TransferSetBean transferSet = null;
		ArrayList subTransfer = null;
		try {	
			transfer = setTransfer(rs); // 获取资金调拨数据
			transferSet = setTransferSet(rs); // 获取资金调拨子数据
			subTransfer = new ArrayList(); // 实例化放置资金调拨子数据的容器
			subTransfer.add(transferSet); // 将资金调拨子数据放入容器
			transfer.setSubTrans(subTransfer); // 将子数据放入资金调拨中
			cashtransAdmin.addList(transfer);
		} catch (Exception e) {
			throw new YssException("设置资金调拨子数据出现异常！", e);
		}
	}
	
	/**
	 * 设置资金调拨数据
	 * @param rs
	 * @return
	 * @throws YssException
	 */
	private TransferBean setTransfer(ResultSet rs) throws YssException {
		TransferBean transfer = null;
		try {
			// 关联编号的设置问题
			transfer = new TransferBean();
			transfer.setDtTransDate(dDate); // 业务日期为T日
			transfer.setDtTransferDate(dDate); // 调拨日期也为T日
			transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);
			transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FE_RecDomCmsnFEE);
			transfer.setFNumType("DomCmsnFee"); // 设置关联编号类型为国内佣金费用
			transfer.checkStateId = 1;
			transfer.setDataSource(1);
		} catch (Exception e) {
			throw new YssException("设置资金调拨数据出现异常！", e);
		}
		return transfer; // 返回资金调拨数据
	}
	
	/**
	 * 设置资金调拨子数据
	 * @param rs
	 * @return
	 * @throws YssException
	 */
	private TransferSetBean setTransferSet(ResultSet rs) throws YssException {
	    TransferSetBean transferSet = null;
        try {
            transferSet = new TransferSetBean();
            double dBaseRate = 1;
            double dPortRate = 1;             
            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FCuryCode"), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE); //获取业务当天的基础汇率

            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                          rs.getString("FPortCode"));
            dPortRate = rateOper.getDPortRate(); //获取业务当天的组合汇率

            transferSet.setIInOut(-1); //流出
            transferSet.setSPortCode(rs.getString("FPortCode"));
            transferSet.setSAnalysisCode1(null == rs.getString("FAnalysisCode1") ? 
            							  "" : rs.getString("FAnalysisCode1"));
            transferSet.setSAnalysisCode2(null == rs.getString("FAnalysisCode2") ? 
            							  "" : rs.getString("FAnalysisCode2"));
            transferSet.setSAnalysisCode3(null == rs.getString("FAnalysisCode3") ? 
            							  "" : rs.getString("FAnalysisCode3"));
            transferSet.setSCashAccCode(rs.getString("FCashAccCode")); //设置现金账户
            transferSet.setDMoney(rs.getDouble("FMoney")); //调拨金额为应付佣金费用
            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
		return transferSet;
	}
	
	/**
	 * 结算日生成应付佣金对应的现金应收应付数据
	 * @param rs
	 * @throws YssException
	 */
	private void createCashPayRec(ResultSet rs) throws YssException {
		CashPecPayBean cashpecpay = new CashPecPayBean();
  		try{
  			cashpecpay.setTradeDate(dDate);//业务日期
  			cashpecpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);//费用
  			cashpecpay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FE_RecDomCmsnFEE);//佣金费用
  			cashpecpay.setStrAttrClsCode(rs.getString("FAttrClsCode"));
  			cashpecpay.setRelaNumType("DomCmsnFee");//关联编号类型 用佣金费用
  			cashpecpay.setMoney(rs.getDouble("FMoney"));//金额
  			cashpecpay.setDataSource(1);//来源标志
  			cashpecpay.checkStateId = 1;
  			cashpecpay.setPortCode(rs.getString("FPortCode"));//组合代码
  			//cashpecpay.setNum(rs.getString("FNum"));
  			cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));//现金账户
  			cashpecpay.setCuryCode(rs.getString("FCuryCode"));//币种代码
  			cashpecpay.setInOutType(1);//方向
  			//设置分析代码1
			cashpecpay.setInvestManagerCode(null == rs.getString("FAnalysisCode1") ? 
					"" : rs.getString("FAnalysisCode1"));
			//设置分析代码2
			cashpecpay.setBrokerCode(null == rs.getString("FAnalysisCode2") ? 
					"" : rs.getString("FAnalysisCode2"));
			//设置分析代码3
			cashpecpay.setCategoryCode(null == rs.getString("FAnalysisCode3") ? 
					"" : rs.getString("FAnalysisCode3"));	
			
			double dBaseRate = this.getSettingOper().getCuryRate(dDate,
					rs.getString("FCuryCode"), rs.getString("FPortCode"),
					YssOperCons.YSS_RATE_BASE); //获取基础汇率
			
			rateOper.getInnerPortRate(dDate,rs.getString("FCuryCode"),
                                      rs.getString("FPortCode"));
			
			double dPortRate = rateOper.getDPortRate(); //获取组合汇率
			
			cashpecpay.setBaseCuryRate(dBaseRate);//基础汇率
			cashpecpay.setPortCuryRate(dPortRate);//组合汇率
			
			double bacecurymoney = this.getSettingOper().calBaseMoney(cashpecpay.getMoney(),dBaseRate);
			double portcurymoney = this.getSettingOper().calPortMoney(cashpecpay.getMoney(),
				               		dBaseRate,dPortRate,rs.getString("FCuryCode"),dDate,sPortCode); 
			
			cashpecpay.setBaseCuryMoney(bacecurymoney);
			cashpecpay.setPortCuryMoney(portcurymoney);
			
			cashpayrecadmin.addList(cashpecpay);
  		}catch(Exception e){
  			throw new YssException("设置现金应收应付数据出现异常！", e);
  		}
	}
}
