package com.yss.main.operdeal.datainterface.custodydetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.HashMap;

import com.yss.main.operdeal.datainterface.BaseDaoOperDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/*************************************************************
 * #2745::“.DAT”文件类型接口导出需求变更
 * 大成基金2011年02月15日01_A
 * @author benson 
 * 2011.03.02
 * 
 * 文件头：控制标记 + 托管账户号码 +交易金额总和 + 笔数
 * 
 */
public class ExpCustodyDetail extends DataBase {

    private int filerows = 0;
  public ExpCustodyDetail() {

	}
	
	
	/**
     * 入口方法
     * @throws YssException
     */
    public void inertData() throws YssException {

    	//1. 建表
    	createTable();
    	//2. 删除数据
    	delData();
    	//3. 导入数据
    	custodyDetail();
    	
    }
    
    
    private void createTable()throws YssException{
    	
    	String createTabSql = "";
    	try{
    		if (dbl.yssTableExist("TMP_dao_CUSTODYDETAIL")) {
				return;
			} else {
				createTabSql = "create table TMP_dao_CUSTODYDETAIL"
						+ "(CHARGEACCRUEDINTEREST VARCHAR2(16),"
						+ "CHARGEBROKERAGE       VARCHAR2(16),"
						+ "CLEARINGSYSTEM        VARCHAR2(10),"
						+ "CLIENTREFERENCE       VARCHAR2(16),"
						+ "COUNTERPARTYNAME      VARCHAR2(35),"
						+ "COUNTERPARTYSWIFTBIC  VARCHAR2(11),"
						+ "EXECUTEDPRICE         VARCHAR2(14),"
						+ "FFILEINFO             VARCHAR2(200),"
						+ "FUNCTIONCODE          VARCHAR2(4),"
						+ "MARKET                VARCHAR2(10),"
						+ "NAMEOFSECURITIES      VARCHAR2(35),"
						+ "OTHERCHARGESAMOUNT1   VARCHAR2(16),"
						+ "OTHERCHARGESAMOUNT2   VARCHAR2(16),"
						+ "OTHERCHARGESAMOUNT3   VARCHAR2(16),"
						+ "OTHERCHARGESAMOUNT4   VARCHAR2(16),"
						+ "OTHERCHARGESAMOUNT5   VARCHAR2(16),"
						+ "OTHERCHARGESCODE1     VARCHAR2(4),"
						+ "OTHERCHARGESCODE2     VARCHAR2(4),"
						+ "OTHERCHARGESCODE3     VARCHAR2(4),"
						+ "OTHERCHARGESCODE4     VARCHAR2(4),"
						+ "OTHERCHARGESCODE5     VARCHAR2(4),"
						+ "PARTICIPANTID         VARCHAR2(20),"
						+ "QUANTITYOFSECURITIES  VARCHAR2(17),"
						+ "REMARK                VARCHAR2(350),"
						+ "SITYPE                VARCHAR2(4),"
						+ "SECURITIESID          VARCHAR2(20),"
						+ "SECURITIESIDTYPE      VARCHAR2(1),"
						+ "SETTLEMENTAMOUNT      VARCHAR2(16),"
						+ "SETTLEMENTCURRENCY    VARCHAR2(16) not null,"
						+ "SETTLEMENTDATE        VARCHAR2(10),"
						+ "TRADEDATE             VARCHAR2(10)," +
						  "fnum varchar2(50)," +
						  "fbankaccount varchar2(50))";
				dbl.executeSql(createTabSql);
			}
    		
    	}catch(Exception e){
    		throw new YssException("【大成基金托管交收数据接口，创建临时表时报错......】");
    	}
    }
    
    
    
    private void delData() throws YssException{
    	
	try {
		dbl.executeSql("delete from TMP_dao_CUSTODYDETAIL");
    	}catch(Exception e){
    		throw new YssException("【大成基金托管交收数据接口，删除数据时报错......】");
    	}
    }
    
    
    private void custodyDetail() throws YssException{
    	HashMap fileInfoMap = null;
    	fileInfoMap = dealFileInfo();
    	if(fileInfoMap != null){
    		ResultSet rs = getCustodyDetailData();
        	insertData(rs,fileInfoMap);
    	}
    	
    }
    
    private HashMap dealFileInfo() throws YssException{
    	
    	String fileRowsQuery = "";
    	String query = "";
    	ResultSet rs = null;
    	HashMap fileInfoMap = null;
    	int iTotalQuantity = 0; //总交易笔数
    	int count =0;//循环交易子表时用于对交易笔数的计数
    	double dControlAmount =0;//交收金额总和
    	String sBankaccount = "";
    	String sValue = "";
    	boolean bAccount = false;//用户判断银行账户是否一样
    	String key = "";
    	String errorMsg = "【大成基金托管交收数据接口，处理文件头内容时出错......】";
    	try{
    		//1. 获取文件最大行数
    		fileRowsQuery = "select ffilerows from "+pub.yssGetTableName("Tb_Dao_CusConfig")+" where fcuscfgcode="+dbl.sqlString(this.cusCfgCode);
    		rs = dbl.openResultSet(fileRowsQuery);
    		if(rs.next()){
    			/********************************************************************
    			 * 因为导出文件要包含文件头。所以设置的最大行数的时候应该包括文件头这一行，
    			 * 而文件内容最大行数应该减1.这里的文件头的处理，只是针对该接口。
    			 */
    			filerows = rs.getInt("ffilerows")-1;
    		}
    		dbl.closeResultSetFinal(rs);
    		
    		
    		//2. 获取交易总笔数
    		query = " select count(*) as FTotalQuantity from "+pub.yssGetTableName("tb_data_subtrade")+" a where a.fcheckstate = 1 " +
    				" and a.fportcode in ("+operSql.sqlCodes(this.sPort)+") and a.fbargaindate = "+dbl.sqlDate(this.sDate);
    		rs = dbl.openResultSet(query);
    		if(rs.next()){
    			iTotalQuantity = rs.getInt("FTotalQuantity");
    		}else{
    			return fileInfoMap;
    		}
    		dbl.closeResultSetFinal(rs);
    		
    		//3.处理文件头信息
    		/*****************************************************
    		 * 导出文件首先根据现金账户的银行账号分组导出，若该银行账号下的记录条数超过“分页显示最大行数”，则分多个文件导出；
    		 */
    		
    		fileInfoMap = new HashMap();
    		query = " select rownum,b.* from (select a.ffactcashacccode,a.ffactsettlemoney,a.fnum,cash.fbankaccount,a.ftradetypecode from "+pub.yssGetTableName("tb_data_subtrade")+" a " +
    				" left join " +
    				" (select cash1.fcashacccode,cash1.fbankaccount from "+pub.yssGetTableName("tb_para_cashaccount")+" cash1 " +
    				" join " +
    				" (select FCASHACCCODE, max(FSTARTDATE) as FSTARTDATE from "+pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1 " +
    				" group by FCASHACCCODE)cash2 on cash1.fcashacccode = cash2.fcashacccode and cash1.FSTARTDATE = cash2.FSTARTDATE)cash  " +
    				" on a.ffactcashacccode = cash.FCASHACCCODE " +
    				" where a.fcheckstate = 1 and a.fportcode in ("+operSql.sqlCodes(this.sPort)+") and a.fbargaindate = "+dbl.sqlDate(this.sDate)+
    				" order by cash.fbankaccount,a.fnum )b ";
    		rs = dbl.openResultSet(query);
    		while(rs.next()){
    			
    			if(key.trim().length()==0){
    				key = rs.getString("fnum");
    			}
    			if(rs.getString("fbankaccount")==null){
    				errorMsg = "【请先维护账户代码："+rs.getString("ffactcashacccode")+" 的银行账号......】";
    				throw new YssException();
    			}
    			
    			if(!sBankaccount.equalsIgnoreCase(rs.getString("fbankaccount"))){
    				
    				//1. 不同银行账户，在分页显示最大行数不足情况也要导成一个文件
    				if(sBankaccount.trim().length()>0 ){
    					sValue+="title-";
    					sValue+="SI"+sBankaccount;
        				sValue+=dealNum(dControlAmount,true,16,"dControlAmount",2);
        				sValue+=dealNum(rs.getInt("rownum")-1-count,false,3,"",0);
        				sValue+="title-";
        				fileInfoMap.put(key, sValue);
        				sValue="";
        				count= rs.getInt("rownum")-1;
        				key = rs.getString("fnum");
    				}
    				
    				sBankaccount = rs.getString("fbankaccount");
    				dControlAmount = 0;
    				bAccount = false;
    			}
    			
    			if(!rs.getString("ftradetypecode").equalsIgnoreCase("41")||!rs.getString("ftradetypecode").equalsIgnoreCase("40")){
    				dControlAmount +=rs.getDouble("ffactsettlemoney");
    			}
    			
    			//2. 根据银行账户分组，相同银行账户并且分页显示达到最大行数的情况
    			if( (rs.getInt("rownum")-count) % filerows == 0 && bAccount){
    				sValue+="title-";
    				sValue+="SI"+sBankaccount;
					sValue+=dealNum(dControlAmount,true,16,"dControlAmount",2);
					sValue+=dealNum(filerows,false,3,"",0);
					sValue+="title-";
					fileInfoMap.put(key, sValue);
					sValue="";
					key="";
					count= rs.getInt("rownum");
					dControlAmount=0;
    			}
    			
    			//3. 根据银行账户分组，导到最后，不足的分页显示最大行数的那部分
    			if( rs.getInt("rownum") == iTotalQuantity ){
    				sValue+="title-";
    				sValue+="SI"+sBankaccount;
					sValue+=dealNum(dControlAmount,true,16,"dControlAmount",2);
					sValue+=dealNum(rs.getInt("rownum")-count,false,3,"",0);
					sValue+="title-";
					fileInfoMap.put(key, sValue);
					sValue="";
					key="";
					//count= rs.getInt("rownum");
    			}
    			
    			bAccount = true;
    			
    		}
    		return fileInfoMap;
    	}catch(Exception e){
    		throw new YssException(errorMsg);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    private ResultSet getCustodyDetailData() throws YssException{
    	
    	String query = "";
    	ResultSet rs = null;
    	
    	try{
    		query = " select a.*, c.FCounterpartySWIFTBIC,C.FPARTICIPANTID,cash.fbankaccount from " +
    		" ( select a1.*,a2.FSecurityName,a2.FExchangeCode,a2.FTradeCury,a2.FISINCode from "+
			" (select ffactcashacccode,FSecuritycode,Fportcode,FBargainDate," +
			" 'NEWM' as FFunctionCode,case when ftradetypecode='01' then 'RVP' when ftradetypecode='02' then 'DVP' " +
			" when ftradetypecode='40' then 'RFP' when ftradetypecode='41' or ftradetypecode='44' then 'DFP'  else ' ' end as FSIType , " +
			" to_char(FBargainDate,'yyyy/mm/dd') as FTradeDate,to_char(FSettleDate,'yyyy/mm/dd') as  FSettlementDate,'I' as FSecuritiesIDType, " +
			" FTradeAmount as FQuantityofSecurities,to_char(FBargainDate,'yyyymmdd')||rownum as FClientReference, " +
			" FBrokerCode as FCounterpartyName,'DTC' as FClearingSystem,FDESC as FRemark,FTradePrice as FExecutedPrice,FFACTSETTLEMONEY as FSettlementAmount, " +
			" FTradeFee1 as FChargeBrokerage,FTradeFee2 as FChargeAccruedInterest,FFeeCode3 as FOtherChargesCode1,FTradeFee3 as FOtherChargesAmount1, " +
			" FFeeCode4 as FOtherChargesCode2,FTradeFee4 as FOtherChargesAmount2,FFeeCode5 as FOtherChargesCode3,FTradeFee5 as FOtherChargesAmount3, " +
			" FFeeCode6 as FOtherChargesCode4,FTradeFee6 as FOtherChargesAmount4,FFeeCode7 as FOtherChargesCode5,FTradeFee7 as FOtherChargesAmount5, " +
			" fcheckstate,fnum from "+pub.yssGetTableName("tb_data_subtrade")+") a1 " +
			" left join " +
			" (select FSecuritycode,FSecurityName,FExchangeCode,FTradeCury,FISINCode from "+pub.yssGetTableName("tb_para_security")+" where fcheckstate=1 )a2 " +
			" on a1.fsecuritycode = a2.fsecuritycode )a " +
			" left join " +
			" (select FBrokerCode,FEXCHANGECODE,FClearerID as FCounterpartySWIFTBIC,FClearAccount as FParticipantID from "+pub.yssGetTableName("Tb_Para_BrokerSubBny")+" where fcheckstate=1 )c " +
			" on a.FCounterpartyName = c.FBrokerCode  and a.FExchangeCode = c.FEXCHANGECODE " +
			" left join " +
			" (select cash1.fcashacccode,cash1.fbankaccount from "+pub.yssGetTableName("tb_para_cashaccount")+" cash1 " +
			"  join " +
			" (select FCASHACCCODE, max(FSTARTDATE) as FSTARTDATE  from "+pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1  group by FCASHACCCODE)cash2 " +
			" on cash1.fcashacccode = cash2.fcashacccode and cash1.FSTARTDATE = cash2.FSTARTDATE)cash  " +
			" on a.ffactcashacccode = cash.FCASHACCCODE " +
			" where a.fcheckstate=1  and a.fportcode in("+operSql.sqlCodes(this.sPort)+") and a.FBargainDate = " +dbl.sqlDate(this.sDate)+
			" order by cash.fbankaccount,a.fnum ";
	
	       rs = dbl.openResultSet(query);
	       
	       return rs;
    	}catch(Exception e){
    		throw new YssException("【大成基金托管交收数据接口，获取托管交收明细数据时报错......】");
    	}
    } 
    
    
    private void insertData(ResultSet rs,HashMap fileInfoMap) throws YssException{
    	
    	String insertSql = "";
    	PreparedStatement prst = null;
    	int count = 1;
    	String sFileInfo = "";
    	String errorMsg = "【大成基金托管交收数据接口，插入数据时出错......】";
        try{
        	
        	insertSql = " insert into TMP_dao_CUSTODYDETAIL(FFILEINFO,FunctionCode,SIType,TradeDate,SettlementDate,Market,SecuritiesIDType,SecuritiesID,NameofSecurities,"+
                        " QuantityofSecurities,ClientReference,CounterpartySWIFTBIC,CounterpartyName,ClearingSystem,ParticipantID,Remark,SettlementCurrency," +
                        " SettlementAmount,ChargeBrokerage,ChargeAccruedInterest,OtherChargesCode1,OtherChargesAmount1,OtherChargesCode2,OtherChargesAmount2," +
                        " OtherChargesCode3,OtherChargesAmount3,OtherChargesCode4,OtherChargesAmount4,OtherChargesCode5,OtherChargesAmount5,EXECUTEDPRICE,fnum,fbankaccount)" +
                        " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        	
        	prst = dbl.openPreparedStatement(insertSql);
        	while(rs.next()){
        		
        		if(rs.getString("FISINCode")==null){
    				errorMsg = "【请先维护证券代码："+rs.getString("FSecuritycode")+" 的ISIN码......】";
    				throw new YssException();
    			}
        		
        		if(rs.getString("FCounterpartySWIFTBIC")==null){
    				errorMsg = "【请先到券商关联设置页面设置 "+rs.getString("FCounterpartyName")+" 的Clearer ID......】";
    				throw new YssException();
    			}
        		
        		if(rs.getString("FParticipantID")==null){
    				errorMsg = "【请先到券商关联设置页面设置"+rs.getString("FCounterpartyName")+" 的Clear Account......】";
    				throw new YssException();
    			}
        		
        		
        		sFileInfo = (String)fileInfoMap.get(rs.getString("fnum"));
        		if(sFileInfo ==null){
        			sFileInfo = "space";
        		}
        		prst.setString(1, sFileInfo);
        		prst.setString(2, rs.getString("FFunctionCode"));
        		prst.setString(3, rs.getString("FSIType"));
        		prst.setString(4, rs.getString("FTradeDate"));
        		prst.setString(5, rs.getString("FSettlementDate"));
        		//prst.setString(6, dealStr(rs.getString("FExchangeCode"),10,"tail"));
        		prst.setString(6, rs.getString("FExchangeCode"));
        		prst.setString(7, rs.getString("FSecuritiesIDType"));
        		prst.setString(8, dealStr(rs.getString("FISINCode"),20,"tail"));
        		//prst.setString(9, dealStr(rs.getString("FSecurityName"),35,"tail"));
        		prst.setString(9, rs.getString("FSecurityName"));//#3804::针对大成基金dat接口导出需求变更 add by jiangshichao 2011.03.29
        		prst.setString(10, dealNum(rs.getDouble("FQuantityofSecurities"),true,17,"",6));
        		prst.setString(11, dealStr(rs.getString("FClientReference"),16,"tail"));
        		prst.setString(12, dealStr(rs.getString("FCounterpartySWIFTBIC"),11,"tail"));
        		prst.setString(13, dealStr(rs.getString("FCounterpartyName"),35,"tail"));
        		prst.setString(14, dealStr(rs.getString("FClearingSystem"),10,"tail"));
        		prst.setString(15, dealStr(rs.getString("FParticipantID"),20,"tail"));
        		prst.setString(16, dealStr(rs.getString("FRemark"),350,"tail"));
        		if(rs.getString("FSIType").equalsIgnoreCase("RFP")||rs.getString("FSIType").equalsIgnoreCase("DFP")){
        			prst.setString(17, "   ");
        			prst.setString(18, "              ");
        			prst.setString(19, "              ");
            		prst.setString(20, "              ");
            		prst.setString(22, "              ");
            		prst.setString(24, "              ");
            		prst.setString(26, "              ");
            		prst.setString(28, "              ");
            		prst.setString(30, "              ");
            		prst.setString(31, dealNum(rs.getDouble("FExecutedPrice"),true,14,"",6));
        		}else{
        			prst.setString(17, rs.getString("FTradeCury"));
        			if(rs.getDouble("FSettlementAmount")!=0){
        				prst.setString(18, dealNum(rs.getDouble("FSettlementAmount"),true,14,"",2));
        			}else{
        				prst.setString(18, "              ");
        			}
        			if(rs.getDouble("FChargeBrokerage")!=0){
        				prst.setString(19, dealNum(rs.getDouble("FChargeBrokerage"),true,14,"",2));
        			}else{
        				prst.setString(19, "              ");
        			}
                    if(rs.getDouble("FChargeAccruedInterest")!=0){
                    	prst.setString(20, dealNum(rs.getDouble("FChargeAccruedInterest"),true,14,"",2));
                    }else{
                    	prst.setString(20, "              ");
                    }
        			prst.setString(22, "              ");
            		prst.setString(24, "              ");
            		prst.setString(26, "              ");
            		prst.setString(28, "              ");
            		prst.setString(30, "              ");
            		prst.setString(31, dealNum(rs.getDouble("FExecutedPrice"),true,14,"",6));
        		}
				//#3804::针对大成基金dat接口导出需求变更 add by jiangshichao 2011.03.29
        		prst.setString(21, (rs.getString("FOtherChargesCode1")==null||rs.getString("FOtherChargesCode1").trim().length()==0)?"    ":rs.getString("FOtherChargesCode1"));
        		prst.setString(23, (rs.getString("FOtherChargesCode2")==null||rs.getString("FOtherChargesCode2").trim().length()==0)?"    ":rs.getString("FOtherChargesCode2"));
        		prst.setString(25, (rs.getString("FOtherChargesCode3")==null||rs.getString("FOtherChargesCode3").trim().length()==0)?"    ":rs.getString("FOtherChargesCode3"));
        		prst.setString(27, (rs.getString("FOtherChargesCode4")==null||rs.getString("FOtherChargesCode4").trim().length()==0)?"    ":rs.getString("FOtherChargesCode4"));
        		prst.setString(29, (rs.getString("FOtherChargesCode5")==null||rs.getString("FOtherChargesCode5").trim().length()==0)?"    ":rs.getString("FOtherChargesCode5"));
        		prst.setString(32, rs.getString("fnum"));
        		prst.setString(33, rs.getString("fbankaccount"));
        		prst.executeUpdate();
        		count++;
        	}
        }catch(Exception e){
    		throw new YssException(errorMsg);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		dbl.closeStatementFinal(prst);
    	}
    }
    
    /**********************************************************
     *  数值根据规则转换成字符
     * @param para   字段值
     * @param flag   处理方式
     * @param lenth  字段长度
     * @param FieldName 字段名
     * @param digits 小数位数
     * @return
     * @throws YssException
     */
    private String dealNum(double para,boolean flag,int lenth,String FieldName,int digits) throws YssException{
    	
    	String sPara = "";
    	DecimalFormat df = new DecimalFormat();  
    	try{
    		
    		
    		/****************************************************************************
    		 * 【交收金额总和数值处理】
    		 * 注意：
    		 * 1. 若此金额有超过两个小数位，则四舍五入只保留两个小数位；
    		 * 2. 字段中不显示小数点，例如：1234.56显示为123456；即此字段中最后两位为小数点后两位数字；例如：1500显示为150000；
    		 * 3. 字段长度为16，靠右排列并于左侧补足空格至16位长度；
    		 */
    		if(flag){
    			//补足小数位数
    			
    			if(FieldName.equalsIgnoreCase("dControlAmount")){
    				df.setMinimumFractionDigits(2);
    				df.setMaximumFractionDigits(2);
    				sPara = df.format(para);
        			sPara = sPara.replaceAll(",","");
    				int i = (sPara.length())-(sPara.indexOf(".")+1);
    				
    				if(i==0){
    					
    					for(int j =0;j<2;j++){
            				sPara = sPara+"0";
            			}
    				}else if(sPara.equalsIgnoreCase("0")){
    					sPara = "0";
    				}else{
    					String tmp1="";
    					String tmp2="";
    					tmp1=sPara;
    					tmp2=sPara;
    					sPara=tmp1.substring(0, tmp1.length()-3)+tmp2.substring(tmp2.length()-2);
    				}
        			
        			  

        			
    			}else if(para !=0){
    				
    				df.setMinimumFractionDigits(digits);
    				df.setMaximumFractionDigits(digits);
    				sPara = df.format(para);
        			sPara = sPara.replaceAll(",","");
    				int i = (sPara.length())-(sPara.indexOf(".")+1);
        			
    				
                   if(i==0){
    					
    					for(int j =0;j<digits;j++){
            				sPara = sPara+"0";
            			}
    				}else{
    					String tmp1="";
    					String tmp2="";
    					tmp1=sPara;
    					tmp2=sPara;
    					sPara=tmp1.substring(0, tmp1.length()-(digits+1))+tmp2.substring(tmp2.length()-digits);
    				}
    			}else{
    				sPara = "0";
    			}
    		
    			//补足字符位数
    			int j=sPara.length();
    			for(int i=0;i<lenth-j;i++){
    				sPara =" "+sPara;
    			}
    		}else{
    			/*************************************************************************
    			 * 【总笔数数值处理】：
    			 *  为当前文件中明细记录行的总记录行数。例如，此处为100。若记录行数为两位数，则靠右并于前面添0，如020；
    			 */
    			if(para%100==0){
    				para=filerows;
    			}else{
    				para = para%100;
    			}
    			
    			df.setMinimumFractionDigits(0);
				df.setMaximumFractionDigits(0);
				sPara = df.format(para);
    			sPara = sPara.replaceAll(",","");
    			int j=lenth-sPara.length();
    			for(int i=0;i<j;i++){
    				sPara="0"+sPara;
    			}
    		}
    		
    		return sPara;
    	}catch(Exception e){
    		throw new YssException("数值转化出错");
    	}
    }
    
    /**************************************
     * 字符转化规则
     * @param spara    字符值
     * @param length   字符长度
     * @param dealRule 转化规则
     * @return
     * @throws YssException
     */
	private String dealStr(String spara,int length,String dealRule) throws YssException {
		
		String sPara = "";
		int j =0;
		try {
			if(spara==null){
				spara="";
			}
			if(spara.length()==spara.getBytes().length){
				sPara = spara;
				j = spara.length();
			}else{
				sPara = spara;
				j=spara.getBytes().length;  // 汉字字符占字符个数不统一的问题。这里先跟据字节个数来处理
			}
			
			if(dealRule.equalsIgnoreCase("head")){
				for(int i =0;i<length - j;i++){
					sPara=" "+sPara;
				}
			}else if(dealRule.equalsIgnoreCase("tail")){
				for(int i =0;i<length - j;i++){
					sPara=sPara+" ";
				}
			}
			return sPara;
		} catch (Exception e) {
			throw new YssException("字符转化出错");
		}
	}
}
