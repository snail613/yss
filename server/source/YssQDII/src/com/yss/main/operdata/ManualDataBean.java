package com.yss.main.operdata;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

//QDV4赢时胜（上海）2009年04月15日01_B MS00382
import com.yss.commeach.*;
import com.yss.dsub.*;
import com.yss.main.cashmanage.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.*;
import com.yss.main.storagemanage.*;
import com.yss.main.syssetting.RightBean;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 *
 * <p>Title:业务数据录入 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author yeshenghong 
 * story 1927
 * @version 1.0
 */
public class ManualDataBean
    extends BaseDataSettingBean implements IDataSetting {
	
	private String  operCode;       
	 private String  portCode;   
	 private String  operDate;    
	 private String  tplNum ;    
	 private String  acctCode;     
	 private String  fdcWay;    
	 private String  curcode;       
	 private String  fmoney; 
	 private String  fcuryrate;     
	 private String  fportmoney;     
	 private String  tradeNum ;      
	 private String  accountType;   
	 private String  subTSFTypeCode ; 
	 private String  securityCode;  
	 private String  cashAccCode ;  
	 private String  feeCode; 
	 private String  vchZY;        
	 private String  entityCode ;
	 private String  portName;
	 private String  auxiAcc;
	 
	 public String getAuxiAcc() {
		return auxiAcc;
	}

	public void setAuxiAcc(String auxiAcc) {
		this.auxiAcc = auxiAcc;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getTplName() {
		return tplName;
	}

	public void setTplName(String tplName) {
		this.tplName = tplName;
	}

	private String tplName;
	 
	 public String getOperCode() {
		return operCode;
	}

	public void setOperCode(String operCode) {
		this.operCode = operCode;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getOperDate() {
		return operDate;
	}

	public void setOperDate(String operDate) {
		this.operDate = operDate;
	}

	public String getTplNum() {
		return tplNum;
	}

	public void setTplNum(String tplNum) {
		this.tplNum = tplNum;
	}

	public String getAcctCode() {
		return acctCode;
	}

	public void setAcctCode(String acctCode) {
		this.acctCode = acctCode;
	}

	public String getFdcWay() {
		return fdcWay;
	}

	public void setFdcWay(String fdcWay) {
		this.fdcWay = fdcWay;
	}

	public String getCurcode() {
		return curcode;
	}

	public void setCurcode(String curcode) {
		this.curcode = curcode;
	}

	public String getFmoney() {
		return fmoney;
	}

	public void setFmoney(String fmoney) {
		this.fmoney = fmoney;
	}

	public String getFcuryrate() {
		return fcuryrate;
	}

	public void setFcuryrate(String fcuryrate) {
		this.fcuryrate = fcuryrate;
	}

	public String getFportmoney() {
		return fportmoney;
	}

	public void setFportmoney(String fportmoney) {
		this.fportmoney = fportmoney;
	}

	public String getTradeNum() {
		return tradeNum;
	}

	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getSubTSFTypeCode() {
		return subTSFTypeCode;
	}

	public void setSubTSFTypeCode(String subTSFTypeCode) {
		this.subTSFTypeCode = subTSFTypeCode;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getCashAccCode() {
		return cashAccCode;
	}

	public void setCashAccCode(String cashAccCode) {
		this.cashAccCode = cashAccCode;
	}

	public String getFeeCode() {
		return feeCode;
	}

	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}

	public String getVchZY() {
		return vchZY;
	}

	public void setVchZY(String vchZY) {
		this.vchZY = vchZY;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

    private String sRecycled = "";  //保存未解析前的字符串

    private ManualDataBean filterType;
    public ManualDataBean getFilterType() {
        return filterType;
    }
    
    public ManualDataBean() {
    }
    
    /*
     * 添加业务数据
     * @see com.yss.main.dao.IDataSetting#addSetting()
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String transferNo = "";
        String secRecPayNo = "";
        String cashRecPayNo = "";
        String feeRecPayNo = ""; 
        String integratedNo = "";
		String subIntNo = "";
        String[] arrData = null;
        
        Date operationDate = new Date();
        int vchPdh = -1;
        int lYear = -1;
        int lMonth = -1;
        int lnSet = -1;
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);
        SecRecPayAdmin secRecPayAdmin = new SecRecPayAdmin();
        secRecPayAdmin.setYssPub(pub);
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        integrateAdmin.setYssPub(pub);
        InvestPayAdimin investPayAdmin = new InvestPayAdimin();
        investPayAdmin.setYssPub(pub);
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
        	BaseOperDeal bod = new BaseOperDeal();
            bod.setYssPub(pub);
           
        	conn.setAutoCommit(false);
            bTrans = true;
            if ( (!sRecycled.equalsIgnoreCase("")) && sRecycled != null) {
            	if(this.sRecycled.indexOf("\r\t")>0)
            	{
            		arrData = sRecycled.split("\r\t")[0].split("\f\f");
            	}else
            	{
            		arrData = sRecycled.split("\f\f");
            	}
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                this.parseVchStr(arrData[i]);
                transferNo = "";
                secRecPayNo = "";
                cashRecPayNo = "";
                feeRecPayNo = ""; 
                integratedNo = "";
				subIntNo = "";
                if(vchPdh<0){
    	            SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd"); 
    	            operationDate = formate.parse(this.operDate);
    	            Date date = formate.parse(this.operDate);
    	            formate =  new SimpleDateFormat("MM"); 
    	            lMonth = Integer.parseInt(formate.format(date));
    	            formate = new SimpleDateFormat("yyyy");
    	            lYear = Integer.parseInt(formate.format(date));
    	            lnSet = Integer.parseInt(this.getSetCode());
    	            vchPdh = this.Vch_NextPdh(lMonth, lYear, lnSet);
    	            }
                strSql = "insert into lvchbin(Fsetcode,FDelDate,Fterm,Fvchpdh,Fvchbh,Fvchzy,Fkmh,Fcyid,Frate,Fbal,Fjd,Fbbal,Fsl,FBsl,Fdate,Fywdate,Fzdr,FMemo) values (" +
        			 	lnSet + "," + 
        			 	dbl.sqlDate(new Date()) + "," +
        			 	lMonth + "," + vchPdh + "," + 
        			 	dbl.sqlString(this.entityCode)+ "," + 
        			 	dbl.sqlString(this.vchZY) + "," + 
        			 	dbl.sqlString(this.acctCode)  + "," + 
        			 	dbl.sqlString(this.curcode)  + "," + 
        			 	dbl.sqlString(this.fcuryrate)  + "," +
        			 	dbl.sqlString(this.fmoney)  + "," + 
        			 	(this.fdcWay.equals("0") ? "'J'" : "'D'")  + "," + 
        			 	dbl.sqlString(this.fportmoney)  + "," +
        			 	dbl.sqlString(this.tradeNum)  + "," + 
        			 	dbl.sqlString(this.tradeNum)  + "," + 
        			 	dbl.sqlDate(this.operDate)  + "," + 
        			 	dbl.sqlDate(this.operDate)  + "," + 
        			 	dbl.sqlString(pub.getUserCode()) + "," + 
        			 	"'Y'" + 
	    				")";
//	    				from " +this.getTablePrefix(lYear, lnSet)  + "fcwvch where Fvchpdh = " + vchPdh + " and Fterm = " + lMonth;
	                dbl.executeSql(strSql);//插入到回收站
	            
	            double baseRate = bod.getCuryRate(operationDate, this.curcode,this.portCode,YssOperCons.YSS_RATE_BASE);
	            double portRate = bod.getCuryRate(operationDate, this.curcode,this.portCode,YssOperCons.YSS_RATE_PORT);
    	        double baseMoney = Double.parseDouble(this.fmoney) * baseRate; 
    	        if(Double.parseDouble(this.fmoney)==0)
    	        {
    	        	baseMoney = Double.parseDouble(this.fportmoney) * portRate; //BUG4456 由本币折算基础货币
    	        }
    	        //modify by jsc  在这里添加判断，如果本币金额不为0，则进行折算，不然给默认值为1
//    	        double portRate = 1 ;
    	        if(Double.parseDouble(this.fportmoney)!=0){
    	        	portRate = baseMoney / Double.parseDouble(this.fportmoney);
    	        }
	            String strNumberDate = YssFun.formatDate(this.operDate, "yyyyMMdd").substring(0, 8);
	            
	            int dicFlag = 1;//BUG5812 modified by  yeshenghong 20120920
	            if(this.acctCode.substring(0, 1).equals("1"))//资产类
	            {
	            	dicFlag =  this.fdcWay.equals("0")? 1 : -1;
	            }else if(this.acctCode.substring(0, 1).equals("2"))
	            {
	            	dicFlag =  this.fdcWay.equals("0")? -1 : 1;
	            }
	            String dataStyle = this.getDataStyles();
	            if(dataStyle.equals("ZHYW"))
	            {
	            	 integratedNo = "E" + YssFun.formatDate(YssFun.toDate(this.operDate), "yyyyMMdd") +
	                 dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Integrated"),
	                                        dbl.sqlRight("FNUM", 6),
	                                        "000001",
	                                        " where FExchangeDate=" + dbl.sqlDate(this.operDate) +
	                                        " or FExchangeDate=" + dbl.sqlDate("9998-12-31") +
	                                        " or FNum like 'E" + YssFun.formatDate(YssFun.toDate(this.operDate), "yyyyMMdd") + "%'");
	            	 //delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
//	            	subIntNo = integratedNo + YssFun.formatNumber(i + 1, "00000");
	            	 //add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
	            	subIntNo = integrateAdmin.getKeyNum();
	            	double tradeNo = Double.parseDouble(this.tradeNum);
	            	double oriMoney = Double.parseDouble(this.fmoney);
	            	double portMoney = Double.parseDouble(this.fportmoney);
	            	//--- edit by songjie 2013.05.21 STORY #3821 需求深圳-[大成基金]QDV4.0[紧急]20130407001 start---//
	            	int direction = (this.fdcWay.equals("0")? 1 : -1);
	            	//--- edit by songjie 2013.05.21 STORY #3821 需求深圳-[大成基金]QDV4.0[紧急]20130407001 end---//
	            	strSql = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
			            "(FNum,FSubNum,FInOutType,FExchangeDate,FOperDate,FSecurityCode,FAttrClsCode,FRelaNum,FNumType,FTradeTypeCode," +
			            " FPortCode,FTsfTypeCode,FSubTsfTypeCode,FAnalysisCode1, FAnalysisCode2,FAnalysisCode3, FAmount," +
			            " FExchangeCost,FMExCost,FVExCost,FPortExCost,FMPortExCost,FVPortExCost,FBaseExCost,FMBaseExCost," +
			            " FVBaseExCost,FBaseCuryRate,FPortCuryRate,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)"
			            + " values(" +
			            dbl.sqlString(integratedNo) + "," +
			            dbl.sqlString(subIntNo) + "," +
			            dicFlag + "," + //bug5812 20120920 yeshenghong
//			            (this.fdcWay.equals("0")? 1 : -1) + "," + //bug4455 20120510 yeshenghong
			            dbl.sqlDate(this.operDate) + "," +
			            dbl.sqlDate(this.operDate) + "," +
			            dbl.sqlString(this.securityCode) + "," +
			            "' ', ' ', ' ', " +
			            (this.fdcWay.equals("0")? "'01'" : "'02'") + "," + 
			            dbl.sqlString(this.portCode) + "," +
			            dbl.sqlString(this.subTSFTypeCode.substring(0,2)) + "," +
	                    dbl.sqlString(this.subTSFTypeCode) + "," +
	                    "' ', ' ', ' ', " +
	                    tradeNo * (this.fdcWay.equals("0")? 1 : -1) + "," +
	                    oriMoney * direction + "," +
	                    oriMoney * direction + "," +
	                    oriMoney * direction + "," +
	                    portMoney * direction + "," +
	                    portMoney * direction + "," +
	                    portMoney * direction + "," +
	                    baseMoney * direction + "," +
	                    baseMoney * direction + "," +
	                    baseMoney * direction + "," +
	                    baseRate + "," +
	                    portRate + "," +
	                    dbl.sqlString(this.vchZY) + "," +
	                    (pub.getSysCheckState() ? "0" : "1") + "," +
		                dbl.sqlString(this.creatorCode) +
		                ", " + dbl.sqlString(this.creatorTime) + "," +
		                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
		                ", ''" + 
			            ")";
	            	dbl.executeSql(strSql);//插入综合业务
	            }
	            
	            if(dataStyle.equals("ZQYSYF"))
	            {
	            	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//		            secRecPayNo = "SRP" + strNumberDate + dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_SecRecPay"),dbl.sqlRight("FNum", 9), "000000001",
//	                               " where Ftransdate =" + dbl.sqlDate(this.operDate));
	            	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
	            	//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
	            	secRecPayNo = secRecPayAdmin.getKeyNum();
		            strSql = "insert into " + pub.yssGetTableName("Tb_Data_SecRecPay") +
		                    "(FNum,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FSecurityCode,FCatType,FAttrClsCode,FTsfTypeCode,FSubTsfTypeCode,FCuryCode,FInOut"
		                    + ",FMoney, FBaseCuryRate,FBaseCuryMoney,FPortCuryRate, FPortCuryMoney,FMoneyF,FBaseCuryMoneyF,FPortCuryMoneyF," +
		                    " fmmoney,fvmoney,fmbasecurymoney,fvbasecurymoney,fmportcurymoney,fvportcurymoney,FDataSource,FStockInd," //add 所属分类,品种类型 sj 20071202
		                    //edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin = 1 表示为手工录入的数据
		                    + "Fdesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDataOrigin)" 
		                    + " values(" +
		                    dbl.sqlString(secRecPayNo) + "," +
		                    dbl.sqlDate(this.operDate) + "," +
		                    dbl.sqlString(this.portCode) + "," +
		                    "' ', ' ', ' ', " +
		                    dbl.sqlString(this.securityCode) + "," +
		                    "' ',' '," + 
		                    dbl.sqlString(this.subTSFTypeCode.substring(0,2)) + "," +
		                    dbl.sqlString(this.subTSFTypeCode) + "," +
		                    dbl.sqlString(this.curcode) + "," +
		                    dicFlag + "," + //bug5812 20120920 yeshenghong
		                    //(this.fdcWay.equals("0")? 1 : -1) + "," + //bug4455 20120510 yeshenghong
		                    dbl.sqlString(this.fmoney) + "," +
		                    baseRate + "," +
		                    baseMoney + "," +
		                    portRate + "," +
		                    dbl.sqlString(this.fportmoney) + "," +
		                    dbl.sqlString(this.fmoney) + "," +
		                    baseMoney + "," +
		                    dbl.sqlString(this.fportmoney) + "," +
		                    dbl.sqlString(this.fmoney) + "," +
		                    dbl.sqlString(this.fmoney) + "," +
		                    baseMoney + "," +
		                    baseMoney + "," +
		                    dbl.sqlString(this.fportmoney) + "," +
		                    dbl.sqlString(this.fportmoney) + "," +
		                    " 1,0," +
		                    dbl.sqlString(this.vchZY) + "," +
		                    (pub.getSysCheckState() ? "0" : "1") + "," +
			                dbl.sqlString(this.creatorCode) +
			                ", " + dbl.sqlString(this.creatorTime) + "," +
			                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
			                ", ''" + 
		            		",1)";//edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin = 1 表示为手工录入的数据
	            	dbl.executeSql(strSql);//插入证券营收应付表
	            }
	            
	            if(dataStyle.equals("XJYSYF"))
	            {
		            strNumberDate = YssFun.formatDate(this.operDate,YssCons.YSS_DATETIMEFORMAT).substring(0, 8);
		            //---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//		            cashRecPayNo = "CRP" + strNumberDate + dbFun.getNextInnerCode(pub.yssGetTableName(
//	                        "Tb_Data_CashPayRec"),dbl.sqlRight("FNum", 9), "000000001",
//	                         " where FTransDate =" + dbl.sqlDate(this.operDate), 1);
		            //---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
		            //add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
		            cashRecPayNo = cashPayRecAdmin.getKeyNum();
		            strSql = "insert into " + pub.yssGetTableName("Tb_Data_CashPayRec") +
			                "(FNum,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FTsfTypeCode,FSubTsfTypeCode,FCuryCode,FInOut"
			                + ",FMoney, FBaseCuryRate,FBaseCuryMoney,FPortCuryRate, FPortCuryMoney,FDataSource,FStockInd," 
			                //edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin = 1 表示为手工录入的数据
			                + "Fdesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDataOrigin)" 
			                + " values(" +
			                dbl.sqlString(cashRecPayNo) + "," +
			                dbl.sqlDate(this.operDate) + "," +
			                dbl.sqlString(this.portCode) + "," +
			                "' ', ' ', ' ', " +
			                dbl.sqlString(this.cashAccCode) + "," +
			                dbl.sqlString(this.subTSFTypeCode.substring(0,2)) + "," +
			                dbl.sqlString(this.subTSFTypeCode) + "," +
			                dbl.sqlString(this.curcode) + "," +
//			                (this.fdcWay.equals("0")? 1 : -1) + "," + 
			                dicFlag + "," + //bug5812 20120920 yeshenghong
//			                "1," + //bug4455 20120510 yeshenghong
			                dbl.sqlString(this.fmoney) + "," +
			                baseRate + "," +
			                baseMoney + "," +
			                portRate + "," +
			                dbl.sqlString(this.fportmoney) + "," +
			                " 1,0," +
			                dbl.sqlString(this.vchZY) + "," +
			                (pub.getSysCheckState() ? "0" : "1") + "," +
			                dbl.sqlString(this.creatorCode) +
			                ", " + dbl.sqlString(this.creatorTime) + "," +
			                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
			                ", ''" + 
			        		",1)";//edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin = 1 表示为手工录入的数据
	            	 dbl.executeSql(strSql);//插入现金应收应付表
	            }
	            
	            if(dataStyle.equals("YYYSYF"))
	            {
		            strNumberDate = YssFun.formatDate(this.operDate,YssCons.YSS_DATETIMEFORMAT).substring(0, 8);
					//---delete by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//					feeRecPayNo = "IPR" + strNumberDate + dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_InvestPayRec"),
//					                 dbl.sqlRight("FNum", 9), "000000001", " where FNum like 'IPR" + strNumberDate + "%'", 1);
					//---delete by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
					//add by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
		            feeRecPayNo = investPayAdmin.getNum();
		            
					strSql = "insert into " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
			                "(FNum,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FIVPayCatCode,FTsfTypeCode,FSubTsfTypeCode,FCuryCode"
			                + ",FMoney, FBaseCuryRate,FBaseCuryMoney,FPortCuryRate, FPortCuryMoney,FDataSource,FStockInd," 
			                //edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin = 1 表示为手工录入的数据
			                + "Fdesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDataOrigin)" 
			                + " values(" +
			                dbl.sqlString(feeRecPayNo) + "," +
			                dbl.sqlDate(this.operDate) + "," +
			                dbl.sqlString(this.portCode) + "," +
			                "' ', ' ', ' ', " +
			                dbl.sqlString(this.feeCode) + "," +
			                dbl.sqlString(this.subTSFTypeCode.substring(0,2)) + "," +
			                dbl.sqlString(this.subTSFTypeCode) + "," +
			                dbl.sqlString(this.curcode) + "," +
			                dbl.sqlString(this.fmoney) + "," +
			                baseRate + "," +
			                baseMoney + "," +
			                portRate + "," +
			                dbl.sqlString(this.fportmoney) + "," +
			                " 1,0," +
			                dbl.sqlString(this.vchZY) + "," +
			                (pub.getSysCheckState() ? "0" : "1") + "," +
			                dbl.sqlString(this.creatorCode) +
			                ", " + dbl.sqlString(this.creatorTime) + "," +
			                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
			                ", ''" + 
			                ",1)";//edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin = 1 表示为手工录入的数据
						dbl.executeSql(strSql);//插入运营应收应付表
	            }
	            
	            if(dataStyle.equals("ZJDB"))
	            {
	            	transferNo = "C" + YssFun.formatDate(this.operDate, "yyyyMMdd") +
		            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"),
		                                   dbl.sqlRight("FNUM", 6), "000001");
	            	strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
		                "(FNum,FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransferTime,FTransDate,FSecurityCode," +
		                "FDataSource,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
		                dbl.sqlString(transferNo) + "," +
		                dbl.sqlString(this.subTSFTypeCode.substring(0,2)) + "," +
		                dbl.sqlString(this.subTSFTypeCode) + "," +
		                dbl.sqlDate(this.operDate) + "," +//调拨类型取业务时间
		                "'00:00:00'" + "," +//调拨时间 
		                dbl.sqlDate(this.operDate) + "," +
		                dbl.sqlString(this.securityCode) + "," +
		                "0," +
		                dbl.sqlString(this.vchZY) + "," +
		                (pub.getSysCheckState() ? "0" : "1") + "," +
		                dbl.sqlString(this.creatorCode) + ", " +
		                dbl.sqlString(this.creatorTime) + "," +
		                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
		                ", ''" + 
		                ")";
	            
	            	dbl.executeSql(strSql);//插入资金调拨数据
  
		            strSql = "insert into " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
	                "(FNum,FSubNum,FInOut,FPortCode,FCashAccCode,FMoney,FBaseCuryRate,FPortCuryRate," +
	                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
	                dbl.sqlString(transferNo) + "," +
	                dbl.sqlString(YssFun.formatNumber(i + 1, "00000")) + "," +
//	                "-1," +
	                (this.fdcWay.equals("0")? 1 : -1) + "," + 
	                dbl.sqlString(this.portCode) + "," +
	                dbl.sqlString(this.cashAccCode) + "," +
	                dbl.sqlString(this.fmoney) + "," +
	                baseRate + "," +
	                portRate + "," +
	                dbl.sqlString(this.vchZY) + "," +
	                (pub.getSysCheckState() ? "0" : "1") + "," +
	                dbl.sqlString(this.creatorCode) + ", " +
	                dbl.sqlString(this.creatorTime) + "," +
	                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
	                ", ''" + 
	                ")";
	            	dbl.executeSql(strSql);//插入资金调拨子表
	            }

            	strSql =	
	                "insert into " + pub.yssGetTableName("Tb_Data_ManualOperation") + "(fopercode,fportcode,foperdate,ftplnum,fentitycode,facctcode,fdcway,fcurcode,fmoney,fcuryrate ,fportmoney,ftradenum,faccountingtype," +
	                " fsubtsftypecode,fsecuritycode,fcashacccode,fivpaycatcode,fvchzy,fvchpdh,ftransferno,fsecrecpayno, fcashrecpayno, finvestrecpayno, fintegratedno,fauxiacc,fcheckstate,fcreator,fcreatetime,FCheckUser,FCheckTime)"
	                + " values(" + 
	                dbl.sqlString(this.operCode) + "," +
	                dbl.sqlString(this.portCode) + "," +
	                dbl.sqlDate(this.operDate) + "," +
	                dbl.sqlString(this.tplNum) + "," +
	                dbl.sqlString(this.entityCode) + "," + 
	                dbl.sqlString(this.acctCode) + "," +
	                dbl.sqlString(this.fdcWay) + "," +
	                dbl.sqlString(this.curcode) + "," +
	                dbl.sqlString(this.fmoney) + "," +
	                dbl.sqlString(this.fcuryrate) + "," +
	                dbl.sqlString(this.fportmoney) + "," +
	                dbl.sqlString(this.tradeNum) + "," +
	                dbl.sqlString(this.accountType) + "," +
	                dbl.sqlString(this.subTSFTypeCode) + "," +
	                dbl.sqlString(this.securityCode) + "," +
	                dbl.sqlString(this.cashAccCode) + "," +
	                dbl.sqlString(this.feeCode) + "," +
	                dbl.sqlString(this.vchZY) + "," +
	                vchPdh + "," +
	                dbl.sqlString(transferNo) + "," +
	                dbl.sqlString(secRecPayNo) + "," +
	                dbl.sqlString(cashRecPayNo) + "," +
	                dbl.sqlString(feeRecPayNo) + "," +
	                dbl.sqlString(integratedNo) + "," +
	                dbl.sqlString(this.auxiAcc) + "," +
	                (pub.getSysCheckState() ? "0" : "1") + "," +
	                dbl.sqlString(this.creatorCode) +
	                ", " + dbl.sqlString(this.creatorTime) + "," +
	                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
	                ", ''" + 
	                ")";
	            dbl.executeSql(strSql);//插入手工数据表

                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增业务数据出错 " ,e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }
    
   
    private String getDataStyles() throws YssException
    {
    	String dataStyles = "";
        String SqlStr;
        ResultSet rs = null;
        try {
	  		   
  	       SqlStr = "select fdatastyle from tb_base_actstfrela where fcheckstate = 1 and fsubjecttype = " + this.acctCode.substring(0, 1) +
  	              " and faccsubtypecode = " + dbl.sqlString(this.accountType) + 
  	              //--- add by songjie 2013.05.21 STORY #3821 需求深圳-[大成基金]QDV4.0[紧急]20130407001 start---//
  	              " and FSubTsfTypeCode = " + dbl.sqlString(this.subTSFTypeCode) ;
  	              //--- add by songjie 2013.05.21 STORY #3821 需求深圳-[大成基金]QDV4.0[紧急]20130407001 end---//
  	       rs = dbl.openResultSet(SqlStr);
  	       if (rs.next()) {
  	    	  dataStyles = rs.getString("fdatastyle");
  	       }
  	       return dataStyles;
  		} catch (Exception e) {
  			throw new YssException("查询数据类型出错",e);
  		} finally
  		{
  			dbl.closeResultSetFinal(rs);
  		}
    }

    
    
    /**
     * 是否月结帐
     * @param lMonth int
     * @param lMonth1 int
     * @param lYear int
     * @param lnSet int
     * @throws YssException
     * @throws SQLException
     * @return boolean
     */
    public boolean MonthClosed( ) throws YssException {
       String sTmp = "";
       String SqlStr;
       int lYear = -1;
       int lMonth = -1;
       int lnSet = -1;
       boolean bBack;
       ResultSet rs = null;
       SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd"); 
       Date date;
	   try {
		   date = formate.parse(this.operDate);
		   formate =  new SimpleDateFormat("MM"); 
	       lMonth = Integer.parseInt(formate.format(date));
	       formate = new SimpleDateFormat("yyyy");
	       lYear = Integer.parseInt(formate.format(date));
	       lnSet = Integer.parseInt(this.getSetCode());
	       sTmp = this.getTablePrefix(lYear, lnSet) + "LClose";
	       if (!dbl.yssTableExist(sTmp)) {
	          return false;
	       }
	       SqlStr = "select FMonth from " + sTmp + " where FMonth = " + lMonth +
	              " group by FMonth";
	       rs = dbl.openResultSet(SqlStr);
	       if (rs.next()) {
	          bBack = true;
	       }
	       else {
	          bBack = false;
	       }
	       return bBack;
		} catch (Exception e) {
			throw new YssException("查询月结出错",e);
		} finally
		{
			dbl.closeResultSetFinal(rs);
		}
    }
    
    
    
    /**
	 * 检查操作的日期段内净值表和财务估值表是否被确认，适用于数据完整性控制 xuxuming 20090807
	 * MS00543:QDV4赢时胜（上海）2009年6月22日01_AB
	 * 
	 * modify by wangzuochun 2009.12.3 MS00767 对组合1的资产进行估值，提示财务估值表已经确认，不能估值
	 * QDV4赢时胜（上海）2009年10月28日04_B
	 * 
	 * @return String
	 */
	private String checkGuessNavStat() throws YssException {
		String strSql = ""; // 保存sql语句的字符串
		String strPortID = "";
		ResultSet rs = null; // 定义一个结果集用来保存sql查出的结果
		java.util.Date dResultDate = null; // 保存上面两个日期的较大者
		java.util.Date dMinDate = null; // 最小的业务日期
		String strReturn = "0"; // 最后的返回字符串，存储0表示前台传入的期间段内无财务估值表或净值表确认
		// modify by nimengjing 2011.2.16 BUG #1001 交易数据界面，同时反审核大数据量时（如：一千条数据）系统报错
		StringBuffer buf = new StringBuffer();
		try {
			String[] arrPortcode = null;
			if (this.portCode.indexOf(",") > 0) {
				arrPortcode = this.portCode.split(",");
			}
			if (arrPortcode != null) {
				for (int i = 0; i < arrPortcode.length; i++) {
					strPortID = getPortID(arrPortcode[i]);
					if (strPortID != null && strPortID.length() > 0) {
						buf = buf.append(strPortID).append(",");
					}
				}
			} else {
				strPortID = getPortID(this.portCode);
			}
			if (buf.length() > 1) {
				strPortID = buf.toString().substring(0, buf.length() - 1);
			}
			buf.delete(0, buf.length());//清除buf
			for (int j =0;j<strPortID.split(",").length;j++){
			strSql = "SELECT a.fdate,b.fnavdate FROM (select max(fdate) as fdate from "
					+ pub.yssGetTableName("Tb_Rep_Guessvalue")
					+ " where facctlevel = '1' and facctcode='C100' and FPortCode in ("
					/*+ operSql.sqlCodes(strPortID) + ")" */
					+ operSql.sqlCodes(strPortID.split(",")[j]) + ")" 
					
		   // --------------------------------end bug #1001--------------------------------------
					+ ") a,"
					+ "(select max(fnavdate) as fnavdate from "
					+ pub.yssGetTableName("Tb_Data_Navdata")
					+ " where FKeyCode = 'confirm'";
			if (this.portCode != null && !"".equals(this.portCode.trim())) {// edit
				strSql += " and FPORTCODE in ('"
					+portCode.split(",")[j]+"')";
			}
			strSql += " ) b";

			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				// 财务估值表确认日期：rs.getDate("FDate")；净值表确认日期:rs.getDate("fnavdate")
				// 1.如果净值表和财务估值表确认日期都不为null ，取二者中较大的日期
				if (rs.getDate("FDate") != null
						&& rs.getDate("fnavdate") != null) {
					dResultDate = YssFun.dateDiff(rs.getDate("FDate"), rs
							.getDate("fnavdate")) > 0 ? rs.getDate("fnavdate")
							: rs.getDate("FDate");
				} else {
					// 2如果净值表或财务估值表只有一个有确认日期，做以下处理
					if (rs.getDate("FDate") != null) {
						// a.如果财务估值表日期不为null，取财物估值表日期
						dResultDate = rs.getDate("FDate");
					} else if (rs.getDate("fnavdate") != null) {
						// 如果净值表日期不为null,取净值表日期
						dResultDate = rs.getDate("fnavdate");
					}
				}
			}
			
			dbl.closeResultSetFinal(rs);
			
			// 如果财务估值表和净值表都没确认，不执行处理，直接返回0
			if (dResultDate != null) {
				dMinDate = YssFun.toDate(this.operDate);
				}
				// 如果最小的业务日期比净值表、财务估值表确认日期小，返回1，提示最小日期到确认日期期间段已确认
				if (dMinDate != null
						&& YssFun.dateDiff(dMinDate, dResultDate) >= 0) {
					strReturn = "组合【"+portCode.split(",")[j]+"】对应的业务日期【"+YssFun.formatDate(dMinDate, 
							"yyyy-MM-dd")+"】至【"+dResultDate.toString()+"】对应的所有业务已确认，" +
							"业务数据不可执行当前操\r\n作，如必须操作该业务日期的业务请先反确认财务估值表和净值表！";
				}
			}
		} catch (Exception e) {
			throw new YssException("获取财务估值表和净值表是否确认时出错！", e);
			//throw new YssException(e.getMessage(),e);
		} finally {
			dbl.closeResultSetFinal(rs); // 释放资源
		}
		return strReturn;
	}
	
	private String getPortID(String sPortCode) throws YssException {
		ResultSet rs = null;
		ResultSet rs2 = null;
		String sqlStr = "";
		String strSql = "";
		String strAssetCode = "";
		String strPortID = "";
		try {
			sqlStr = "select FAssetCode from "
					+ pub.yssGetTableName("tb_para_portfolio")
					+ " where FPortCode = " + dbl.sqlString(sPortCode);
			rs = dbl.openResultSet(sqlStr);
			if (rs.next()) {
				strAssetCode = rs.getString("FAssetCode");
				strSql = "Select FSetCode from Lsetlist where FSetID = "
						+ dbl.sqlString(strAssetCode);
				rs2 = dbl.openResultSet(strSql);
				if (rs2.next()) {
					strPortID = rs2.getString("FSetCode");
				}
				dbl.closeResultSetFinal(rs2);
			}
			dbl.closeResultSetFinal(rs2);
			dbl.closeResultSetFinal(rs);
		} catch (Exception ex) {
			throw new YssException("取组合信息出错,请检查设置是否正确", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rs2);
		}
		return strPortID;
	}
    
	/*
	 * 修改时调用添加的方法
	 */
    public void addSetting(int vchPdh,String[] transferNos,String[] secRecPayNos,String[] cashRecPayNos,String[] feeRecPayNos,String[] integratedNos) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        Date operationDate = new Date();
        String[] arrData = null;
        String transferNo = "";
        String secRecPayNo = "";
        String cashRecPayNo = "";
        String feeRecPayNo = ""; 
        String integratedNo = "";
		String subIntNo = "";
        int lYear = -1;
        int lMonth = -1;
        int lnSet = -1;
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);
        SecRecPayAdmin secRecPayAdmin = new SecRecPayAdmin();
        secRecPayAdmin.setYssPub(pub);
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
    	integrateAdmin.setYssPub(pub);
        InvestPayAdimin investPayAdmin = new InvestPayAdimin();
        investPayAdmin.setYssPub(pub);
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
	    	 SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd"); 
	         operationDate = formate.parse(this.operDate);
	         Date date = formate.parse(this.operDate);
	         formate =  new SimpleDateFormat("MM"); 
	         lMonth = Integer.parseInt(formate.format(date));
	         formate = new SimpleDateFormat("yyyy");
	         lYear = Integer.parseInt(formate.format(date));
	         lnSet = Integer.parseInt(this.getSetCode());
	         BaseOperDeal bod = new BaseOperDeal();
	         bod.setYssPub(pub);
	         conn.setAutoCommit(false);
	         bTrans = true;
	         
	         strSql = "delete from " + pub.yssGetTableName("Tb_Data_ManualOperation") +
	         		" where fopercode = " + dbl.sqlString(this.operCode);
	         dbl.executeSql(strSql); //删除手工数据
	         
	         if(vchPdh>0){
	        	 strSql = "delete from lvchbin where Fvchpdh = " + vchPdh +
			     			" and Fterm = " + lMonth + " and Fsetcode = " + lnSet +
//			     			" and Fvchzy = " + dbl.sqlString(this.) + 
			     			" and Fywdate = " + dbl.sqlDate(this.operDate);
	        	 	dbl.executeSql(strSql);//清除回收站中的凭证
//		         strSql = "delete from " + this.getTablePrefix(lYear, lnSet)  + "fcwvch where Fvchpdh = " + vchPdh + " and Fterm = " + lMonth;
		         dbl.executeSql(strSql);//删除凭证
	         }
	         
	         for(int i=0;i<secRecPayNos.length;i++)
	         {
		         strSql = "delete from " + pub.yssGetTableName("Tb_Data_SecRecPay") +
	             " where FNum = " + dbl.sqlString(secRecPayNos[i]);
		         dbl.executeSql(strSql);//删除证券应收应付
	         }
	         for(int i=0;i<cashRecPayNos.length;i++)
	         {
		         strSql = "delete from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
	             " where FNum = " + dbl.sqlString(cashRecPayNos[i]);
		         dbl.executeSql(strSql);//删除现金应收应付
	         }
	         
	         for(int i=0;i<feeRecPayNos.length;i++)
	         {
		         strSql = "delete from " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
	             " where FNum = " + dbl.sqlString(feeRecPayNos[i]);
		         dbl.executeSql(strSql);//删除运营应收应付
	         }
	         for(int i=0;i<integratedNos.length;i++)
	         {
		         strSql = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") + 
	             " where FNum = " + dbl.sqlString(integratedNos[i]);
		         dbl.executeSql(strSql);//删除综合业务
	         }
	         for(int i=0;i<transferNos.length;i++)
	         {
		         strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
		         		" where fnum = " + dbl.sqlString(transferNos[i]);
		         dbl.executeSql(strSql);//删除调拨 
		         strSql = "delete from " +  pub.yssGetTableName("Tb_Cash_SubTransfer") +
		         		" where fnum = " + dbl.sqlString(transferNos[i]);
		         dbl.executeSql(strSql);//删除子调拨 
    		 }
	         
	         int dicFlag = 1;//BUG5812 modified by  yeshenghong 20120920
             if(this.acctCode.substring(0, 1).equals("1"))//资产类
             {
            	dicFlag =  this.fdcWay.equals("0")? 1 : -1;
             }else if(this.acctCode.substring(0, 1).equals("2"))
             {
            	dicFlag =  this.fdcWay.equals("0")? -1 : 1;
             }
	         String strNumberDate = YssFun.formatDate(this.operDate, "yyyyMMdd").substring(0, 8);
	         
	         if((!sRecycled.equalsIgnoreCase("")) && sRecycled != null) {
            	if(this.sRecycled.indexOf("\r\t")>0)
            	{
            		arrData = sRecycled.split("\r\t")[0].split("\f\f");
            	}else
            	{
            		arrData = sRecycled.split("\f\f");
            	}
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseVchStr(arrData[i]);
                    transferNo = "";
                    secRecPayNo = "";
                    cashRecPayNo = "";
                    feeRecPayNo = ""; 
                    integratedNo = "";
					subIntNo = "";
                    strSql = "insert into lvchbin(Fsetcode,FDelDate,Fterm,Fvchpdh,Fvchbh,Fvchzy,Fkmh,Fcyid,Frate,Fbal,Fjd,Fbbal,Fsl,FBsl,Fdate,Fywdate,Fzdr,FMemo) values (" +
			    			 	lnSet + "," + 
			    			 	dbl.sqlDate(new Date()) + "," +
			    			 	lMonth + "," + vchPdh + "," + 
			    			 	dbl.sqlString(this.entityCode)+ "," + 
			    			 	dbl.sqlString(this.vchZY) + "," + 
			    			 	dbl.sqlString(this.acctCode)  + "," + 
			    			 	dbl.sqlString(this.curcode)  + "," + 
			    			 	dbl.sqlString(this.fcuryrate)  + "," +
			    			 	dbl.sqlString(this.fmoney)  + "," + 
			    			 	(this.fdcWay.equals("0") ? "'J'" : "'D'")  + "," + 
			    			 	dbl.sqlString(this.fportmoney)  + "," +
			    			 	dbl.sqlString(this.tradeNum)  + "," + 
			    			 	dbl.sqlString(this.tradeNum)  + "," + 
			    			 	dbl.sqlDate(this.operDate)  + "," + 
			    			 	dbl.sqlDate(this.operDate)  + "," + 
			    			 	dbl.sqlString(pub.getUserCode()) + "," + 
		        			 	"'Y'" + 
			    			 	")";
                    dbl.executeSql(strSql);//插入到回收站
			        
			        double baseRate = bod.getCuryRate(operationDate, this.curcode,this.portCode,YssOperCons.YSS_RATE_BASE);
		            double portRate = bod.getCuryRate(operationDate, this.curcode,this.portCode,YssOperCons.YSS_RATE_PORT);
	    	        double baseMoney = Double.parseDouble(this.fmoney) * baseRate; 
	    	        if(Double.parseDouble(this.fmoney)==0)
	    	        {
	    	        	baseMoney = Double.parseDouble(this.fportmoney) * portRate; //BUG4456 由本币折算基础货币
	    	        }
	    	        //modify by jsc  在这里添加判断，如果本币金额不为0，则进行折算，不然给默认值为1
	    	        if(Double.parseDouble(this.fportmoney)!=0){
	    	        	portRate = baseMoney / Double.parseDouble(this.fportmoney);
	    	        }
			    
		            String dataStyle = this.getDataStyles();
		            if(dataStyle.equals("ZHYW"))
		            {
		            	integratedNo = "E" + YssFun.formatDate(YssFun.toDate(this.operDate), "yyyyMMdd") +
			            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Integrated"),
			                                   dbl.sqlRight("FNUM", 6),
			                                   "000001",
			                                   " where FExchangeDate=" + dbl.sqlDate(this.operDate) +
			                                   " or FExchangeDate=" + dbl.sqlDate("9998-12-31") +
			                                   " or FNum like 'E" + YssFun.formatDate(YssFun.toDate(this.operDate), "yyyyMMdd") + "%'");
		            	//delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
//		            	subIntNo = integratedNo + YssFun.formatNumber(i + 1, "00000");
		            	 //add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
		            	subIntNo = integrateAdmin.getKeyNum();
		            	double tradeNo = Double.parseDouble(this.tradeNum);
		            	double oriMoney = Double.parseDouble(this.fmoney);
		            	double portMoney = Double.parseDouble(this.fportmoney);
		            	//--- edit by songjie 2013.05.21 STORY #3821 需求深圳-[大成基金]QDV4.0[紧急]20130407001 start---//
		            	int direction = (this.fdcWay.equals("0")? 1 : -1);
		            	//--- edit by songjie 2013.05.21 STORY #3821 需求深圳-[大成基金]QDV4.0[紧急]20130407001 end---//
		            	strSql = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
				            "(FNum,FSubNum,FInOutType,FExchangeDate,FOperDate,FSecurityCode,FAttrClsCode,FRelaNum,FNumType,FTradeTypeCode," +
				            " FPortCode,FTsfTypeCode,FSubTsfTypeCode,FAnalysisCode1, FAnalysisCode2,FAnalysisCode3, FAmount," +
				            " FExchangeCost,FMExCost,FVExCost,FPortExCost,FMPortExCost,FVPortExCost,FBaseExCost,FMBaseExCost," +
				            " FVBaseExCost,FBaseCuryRate,FPortCuryRate,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)"
				            + " values(" +
				            dbl.sqlString(integratedNo) + "," +
				            dbl.sqlString(subIntNo) + "," +
				            //(this.fdcWay.equals("0")? 1 : -1) + "," + dicFlag
//				            "1," + //bug4455 20120510 yeshenghong
				            dicFlag + "," + //bug5812 20120920 yeshenghong
				            dbl.sqlDate(this.operDate) + "," +
				            dbl.sqlDate(this.operDate) + "," +
				            dbl.sqlString(this.securityCode) + "," +
				            "' ', ' ', ' ', " +
				            (this.fdcWay.equals("0")? "'01'" : "'02'") + "," + 
				            dbl.sqlString(this.portCode) + "," +
				            dbl.sqlString(this.subTSFTypeCode.substring(0,2)) + "," +
		                    dbl.sqlString(this.subTSFTypeCode) + "," +
		                    "' ', ' ', ' ', " +
		                    tradeNo * (this.fdcWay.equals("0")? 1 : -1) + "," +
		                    oriMoney * direction + "," +
		                    oriMoney * direction + "," +
		                    oriMoney * direction + "," +
		                    portMoney * direction + "," +
		                    portMoney * direction + "," +
		                    portMoney * direction + "," +
		                    baseMoney * direction + "," +
		                    baseMoney * direction + "," +
		                    baseMoney * direction + "," +
		                    baseRate + "," +
		                    portRate + "," +
		                    dbl.sqlString(this.vchZY) + "," +
		                    (pub.getSysCheckState() ? "0" : "1") + "," +
			                dbl.sqlString(this.creatorCode) +
			                ", " + dbl.sqlString(this.creatorTime) + "," +
			                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
			                ", ''" + 
				            ")";
		            	dbl.executeSql(strSql);//插入综合业务
		            }
		            
		            if(dataStyle.equals("ZQYSYF"))
		            {
		            	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//			            secRecPayNo = "SRP" + strNumberDate + dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_SecRecPay"),dbl.sqlRight("FNum", 9), "000000001",
//		                               " where Ftransdate =" + dbl.sqlDate(this.operDate));
		            	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
		            	//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
		            	secRecPayNo = secRecPayAdmin.getKeyNum();
			            strSql = "insert into " + pub.yssGetTableName("Tb_Data_SecRecPay") +
			                    "(FNum,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FSecurityCode,FCatType,FAttrClsCode,FTsfTypeCode,FSubTsfTypeCode,FCuryCode,FInOut"
			                    + ",FMoney, FBaseCuryRate,FBaseCuryMoney,FPortCuryRate, FPortCuryMoney,FMoneyF,FBaseCuryMoneyF,FPortCuryMoneyF," +
			                    " fmmoney,fvmoney,fmbasecurymoney,fvbasecurymoney,fmportcurymoney,fvportcurymoney,FDataSource,FStockInd," //add 所属分类,品种类型 sj 20071202
			                    + "Fdesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" 
			                    + " values(" +
			                    dbl.sqlString(secRecPayNo) + "," +
			                    dbl.sqlDate(this.operDate) + "," +
			                    dbl.sqlString(this.portCode) + "," +
			                    "' ', ' ', ' ', " +
			                    dbl.sqlString(this.securityCode) + "," +
			                    "' ',' '," + 
			                    dbl.sqlString(this.subTSFTypeCode.substring(0,2)) + "," +
			                    dbl.sqlString(this.subTSFTypeCode) + "," +
			                    dbl.sqlString(this.curcode) + "," +
			                    dicFlag + "," + //bug5812 20120920 yeshenghong
//			                    "1," + //bug4455 20120510 yeshenghong
			                    //需要修改   资产类为 1 × 借贷方向， 负债类为-1 * 借贷方向
			                    //(this.fdcWay.equals("0")? 1 : -1) + "," + 
			                    dbl.sqlString(this.fmoney) + "," +
			                    baseRate + "," +
			                    baseMoney + "," +
			                    portRate + "," +
			                    dbl.sqlString(this.fportmoney) + "," +
			                    dbl.sqlString(this.fmoney) + "," +
			                    baseMoney + "," +
			                    dbl.sqlString(this.fportmoney) + "," +
			                    dbl.sqlString(this.fmoney) + "," +
			                    dbl.sqlString(this.fmoney) + "," +
			                    baseMoney + "," +
			                    baseMoney + "," +
			                    dbl.sqlString(this.fportmoney) + "," +
			                    dbl.sqlString(this.fportmoney) + "," +
			                    " 1,0," +
			                    dbl.sqlString(this.vchZY) + "," +
			                    (pub.getSysCheckState() ? "0" : "1") + "," +
				                dbl.sqlString(this.creatorCode) +
				                ", " + dbl.sqlString(this.creatorTime) + "," +
				                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
				                ", ''" + 
			            		")";
		            	dbl.executeSql(strSql);//插入证券营收应付表
		            }
		            
		            if(dataStyle.equals("XJYSYF"))
		            {
			            strNumberDate = YssFun.formatDate(this.operDate,YssCons.YSS_DATETIMEFORMAT).substring(0, 8);
			            //---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//			            cashRecPayNo = "CRP" + strNumberDate + dbFun.getNextInnerCode(pub.yssGetTableName(
//		                        "Tb_Data_CashPayRec"),dbl.sqlRight("FNum", 9), "000000001",
//		                         " where FTransDate =" + dbl.sqlDate(this.operDate), 1);
			            //---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
			            //add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
			            cashRecPayNo = cashPayRecAdmin.getKeyNum();
			            strSql = "insert into " + pub.yssGetTableName("Tb_Data_CashPayRec") +
				                "(FNum,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FTsfTypeCode,FSubTsfTypeCode,FCuryCode,FInOut"
				                + ",FMoney, FBaseCuryRate,FBaseCuryMoney,FPortCuryRate, FPortCuryMoney,FDataSource,FStockInd," 
				                + "Fdesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" 
				                + " values(" +
				                dbl.sqlString(cashRecPayNo) + "," +
				                dbl.sqlDate(this.operDate) + "," +
				                dbl.sqlString(this.portCode) + "," +
				                "' ', ' ', ' ', " +
				                dbl.sqlString(this.cashAccCode) + "," +
				                dbl.sqlString(this.subTSFTypeCode.substring(0,2)) + "," +
				                dbl.sqlString(this.subTSFTypeCode) + "," +
				                dbl.sqlString(this.curcode) + "," +
				                //(this.fdcWay.equals("0")? 1 : -1) + "," + 
//				                "1," + //bug4455 20120510 yeshenghong
				                dicFlag + "," + //bug5812 20120920 yeshenghong
				                dbl.sqlString(this.fmoney) + "," +
				                baseRate + "," +
				                baseMoney + "," +
				                portRate + "," +
				                dbl.sqlString(this.fportmoney) + "," +
				                " 1,0," +
				                dbl.sqlString(this.vchZY) + "," +
				                (pub.getSysCheckState() ? "0" : "1") + "," +
				                dbl.sqlString(this.creatorCode) +
				                ", " + dbl.sqlString(this.creatorTime) + "," +
				                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
				                ", ''" + 
				        		")";
		            	 dbl.executeSql(strSql);//插入现金应收应付表
		            }
		            
		            if(dataStyle.equals("YYYSYF"))
		            {
			            strNumberDate = YssFun.formatDate(this.operDate,YssCons.YSS_DATETIMEFORMAT).substring(0, 8);
			            //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//						feeRecPayNo = "IPR" + strNumberDate + dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_InvestPayRec"),
//						                 dbl.sqlRight("FNum", 9), "000000001", " where FNum like 'IPR" + strNumberDate + "%'", 1);
						//---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
			            //add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
			            feeRecPayNo = investPayAdmin.getNum();
						strSql = "insert into " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
				                "(FNum,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FIVPayCatCode,FTsfTypeCode,FSubTsfTypeCode,FCuryCode"
				                + ",FMoney, FBaseCuryRate,FBaseCuryMoney,FPortCuryRate, FPortCuryMoney,FDataSource,FStockInd," 
				                + "Fdesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" 
				                + " values(" +
				                dbl.sqlString(feeRecPayNo) + "," +
				                dbl.sqlDate(this.operDate) + "," +
				                dbl.sqlString(this.portCode) + "," +
				                "' ', ' ', ' ', " +
				                dbl.sqlString(this.feeCode) + "," +
				                dbl.sqlString(this.subTSFTypeCode.substring(0,2)) + "," +
				                dbl.sqlString(this.subTSFTypeCode) + "," +
				                dbl.sqlString(this.curcode) + "," +
				                dbl.sqlString(this.fmoney) + "," +
				                baseRate + "," +
				                baseMoney + "," +
				                portRate + "," +
				                dbl.sqlString(this.fportmoney) + "," +
				                " 1,0," +
				                dbl.sqlString(this.vchZY) + "," +
				                (pub.getSysCheckState() ? "0" : "1") + "," +
				                dbl.sqlString(this.creatorCode) +
				                ", " + dbl.sqlString(this.creatorTime) + "," +
				                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
				                ", ''" + 
				                ")";
							dbl.executeSql(strSql);//插入运营应收应付
		            }
		            
		            if(dataStyle.equals("ZJDB"))
		            {
		            	transferNo = "C" + YssFun.formatDate(this.operDate, "yyyyMMdd") +
			            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"),
			                                   dbl.sqlRight("FNUM", 6), "000001");
		            	strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
			                "(FNum,FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransferTime,FTransDate,FSecurityCode," +
			                "FDataSource,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
			                dbl.sqlString(transferNo) + "," +
			                dbl.sqlString(this.subTSFTypeCode.substring(0,2)) + "," +
			                dbl.sqlString(this.subTSFTypeCode) + "," +
			                dbl.sqlDate(this.operDate) + "," +//调拨类型取业务时间
			                "'00:00:00'" + "," +//调拨时间 
			                dbl.sqlDate(this.operDate) + "," +
			                dbl.sqlString(this.securityCode) + "," +
			                "0," +
			                dbl.sqlString(this.vchZY) + "," +
			                (pub.getSysCheckState() ? "0" : "1") + "," +
			                dbl.sqlString(this.creatorCode) + ", " +
			                dbl.sqlString(this.creatorTime) + "," +
			                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
			                ", ''" + 
			                ")";
		            
		            	dbl.executeSql(strSql);//插入资金调拨数据
	  
			            strSql = "insert into " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
		                "(FNum,FSubNum,FInOut,FPortCode,FCashAccCode,FMoney,FBaseCuryRate,FPortCuryRate," +
		                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
		                dbl.sqlString(transferNo) + "," +
		                dbl.sqlString(YssFun.formatNumber(i + 1, "00000")) + "," +
//		                "-1," +
		                (this.fdcWay.equals("0")? 1 : -1) + "," + 
		                dbl.sqlString(this.portCode) + "," +
		                dbl.sqlString(this.cashAccCode) + "," +
		                dbl.sqlString(this.fmoney) + "," +
		                baseRate + "," +
		                portRate + "," +
		                dbl.sqlString(this.vchZY) + "," +
		                (pub.getSysCheckState() ? "0" : "1") + "," +
		                dbl.sqlString(this.creatorCode) + ", " +
		                dbl.sqlString(this.creatorTime) + "," +
		                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
		                ", ''" + 
		                ")";
		            	dbl.executeSql(strSql);//插入资金调拨子表
		            }

	            	strSql =	
		                "insert into " + pub.yssGetTableName("Tb_Data_ManualOperation") + "(fopercode,fportcode,foperdate,ftplnum,fentitycode,facctcode,fdcway,fcurcode,fmoney,fcuryrate ,fportmoney,ftradenum,faccountingtype," +
		                " fsubtsftypecode,fsecuritycode,fcashacccode,fivpaycatcode,fvchzy,fvchpdh,ftransferno,fsecrecpayno, fcashrecpayno, finvestrecpayno, fintegratedno,fauxiacc,fcheckstate,fcreator,fcreatetime,FCheckUser,FCheckTime)"
		                + " values(" + 
		                dbl.sqlString(this.operCode) + "," +
		                dbl.sqlString(this.portCode) + "," +
		                dbl.sqlDate(this.operDate) + "," +
		                dbl.sqlString(this.tplNum) + "," +
		                dbl.sqlString(this.entityCode) + "," + 
		                dbl.sqlString(this.acctCode) + "," +
		                dbl.sqlString(this.fdcWay) + "," +
		                dbl.sqlString(this.curcode) + "," +
		                dbl.sqlString(this.fmoney) + "," +
		                dbl.sqlString(this.fcuryrate) + "," +
		                dbl.sqlString(this.fportmoney) + "," +
		                dbl.sqlString(this.tradeNum) + "," +
		                dbl.sqlString(this.accountType) + "," +
		                dbl.sqlString(this.subTSFTypeCode) + "," +
		                dbl.sqlString(this.securityCode) + "," +
		                dbl.sqlString(this.cashAccCode) + "," +
		                dbl.sqlString(this.feeCode) + "," +
		                dbl.sqlString(this.vchZY) + "," +
		                vchPdh + "," +
		                dbl.sqlString(transferNo) + "," +
		                dbl.sqlString(secRecPayNo) + "," +
		                dbl.sqlString(cashRecPayNo) + "," +
		                dbl.sqlString(feeRecPayNo) + "," +
		                dbl.sqlString(integratedNo) + "," +
		                dbl.sqlString(this.auxiAcc) + "," +
		                (pub.getSysCheckState() ? "0" : "1") + "," +
		                dbl.sqlString(this.creatorCode) +
		                ", " + dbl.sqlString(this.creatorTime) + "," +
		                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
		                ", ''" + 
		                ")";
		            dbl.executeSql(strSql);//插入手工数据表
        
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
//            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改业务数据出错",e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * buildRowStr
     * 获取数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.operCode).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.operDate).append("\t");
        buf.append(this.tplNum).append("\t");
        buf.append(this.vchZY).append("\t");
        buf.append(this.checkStateId).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
    }
    
    public String buildDetailedVchStr()
    {
    	StringBuffer buf = new StringBuffer();
        buf.append(this.operCode).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.operDate).append("\t");
        buf.append(this.tplNum).append("\t");
        buf.append(this.vchZY).append("\t");
        buf.append(this.entityCode).append("\t");
        buf.append(this.acctCode).append("\t");
        buf.append(this.fdcWay).append("\t");
        buf.append(this.curcode).append("\t");
        buf.append(this.fmoney).append("\t");
        buf.append(this.fcuryrate).append("\t");
        buf.append(this.fportmoney).append("\t");
        buf.append(this.tradeNum).append("\t");
        buf.append(this.accountType).append("\t");
        buf.append(this.subTSFTypeCode).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.cashAccCode).append("\t");
        buf.append(this.feeCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.tplName).append("\t");
        buf.append(super.buildRecLog());
        
        return buf.toString();
    }

    /**
     * checkInput
     * 验证数据
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
//        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_ManualOperation"),
//                               "FNum", this.portCode, this.oldNum);
    }

    /**
     * 修改时间：2008年3月28号
     * 修改人：单亮
     * 原方法功能：只能处理业务的审核和未审核的单条信息。
     * 新方法功能：可以处理业务审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理业务审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        int lYear = -1;
        int lMonth = -1;
        int lnSet = -1;
        int vchPdh = -1;
        int vchNum = -1;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( (!sRecycled.equalsIgnoreCase("")) && sRecycled != null) {     
                if(this.sRecycled.indexOf("\r\t")>0)
            	{
            		arrData = sRecycled.split("\r\t")[0].split("\r\n");
            	}else
            	{
            		arrData = sRecycled.split("\r\n");
            	}
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseSimpleInfo((arrData[i]));
                    
                    strSql = "select distinct Fvchpdh from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
        			" where fopercode = " + dbl.sqlString(this.operCode);
		        	rs = dbl.openResultSet(strSql);
		        	if(rs.next())
		        	{
		        		vchPdh = rs.getInt("Fvchpdh");
		        	}
		        	dbl.closeResultSetFinal(rs);
                    SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd"); 
    	            Date date = formate.parse(this.operDate);
    	            formate =  new SimpleDateFormat("MM"); 
    	            lMonth = Integer.parseInt(formate.format(date));
    	            formate = new SimpleDateFormat("yyyy");
    	            lYear = Integer.parseInt(formate.format(date));
	            	lnSet = Integer.parseInt(this.getSetCode());
	            	strSql = "select distinct Fvchpdh from " +  this.getTablePrefix(lYear, lnSet)  + "fcwvch "+ 
        					" where Fvchpdh = " + vchPdh + " and Fterm = " + lMonth;
		        	rs = dbl.openResultSet(strSql);
		        	if(rs.next())
		        	{
		        		vchPdh = this.Vch_NextPdh(lMonth, lYear, lnSet);
		        	}
	            	if(this.checkStateId==1)//审核
	            	{//2275审核插入
	            			strSql = " insert into " + this.getTablePrefix(lYear, lnSet)  + "fcwvch (" +
		    	            		"Fterm,	Fvchpdh, Fvchbh, Fvchzy,Fkmh,Fcyid,Frate,Fbal,Fjd,Fbbal,Fsl,FBsl,Fdate,Fywdate,Fauxiacc,Fzdr,FConfirmer) select " +
		    	            		 lMonth + "," + vchPdh + "," + 
		    	            		 " t.fentitycode, t.fvchzy, t.facctcode, t.fcurcode, t.fcuryrate, t.fmoney," +
		    	            	     " (case when t.fdcway = 0 then 'J' else 'D' end) fdcway, " + 
		    	            	     " t.fportmoney, t.ftradenum, t.ftradenum, t.foperdate, t.foperdate, t.fauxiacc, " + 
		    	            	     dbl.sqlString(pub.getUserCode()) + ", " + 
		    	            	     dbl.sqlString(pub.getUserCode()) + 
		    	            	     " from " + pub.yssGetTableName("Tb_Data_ManualOperation") + " t " +
		    	            	     " where t.fopercode = " + this.operCode;
	            			dbl.executeSql(strSql);//插入凭证
		            		strSql = "delete from lvchbin where Fvchpdh = " + vchPdh + " and Fterm = " + lMonth + " and Fsetcode = " + lnSet +
			            			" and Fywdate = " + dbl.sqlDate(this.operDate) + " and FMemo = 'Y'"; 
		                	dbl.executeSql(strSql);//删除回收站
		                	
		                	strSql = " update " +pub.yssGetTableName("Tb_Data_ManualOperation") + " set Fvchpdh = " + vchPdh + 
		                			" where fopercode = " + dbl.sqlString(this.operCode);
		                	dbl.executeSql(strSql);//更新凭证号
//	            		}
	            	}else if(this.checkStateId==0)
	            	{//反审核   删除到回收站中
	            		strSql = "insert into lvchbin(Fsetcode,FDelDate,Fterm,Fvchpdh,Fvchbh,Fvchzy,Fkmh,Fcyid,Frate,Fbal,Fjd,Fbbal,Fsl,FBsl,Fdate,Fywdate,Fzdr,FMemo) " +
		            		" select " + lnSet + "," + 
		            		dbl.sqlDate(new Date()) + ", Fterm,Fvchpdh,Fvchbh,Fvchzy,Fkmh,Fcyid,Frate,Fbal,Fjd,Fbbal,Fsl,FBsl,Fdate,Fywdate,Fzdr ,'Y' from " +
		            		this.getTablePrefix(lYear, lnSet)  + "fcwvch where Fvchpdh = " + vchPdh + " and Fterm = " + lMonth;
	            		dbl.executeSql(strSql);//插入到回收站
	            		strSql = "delete from " + this.getTablePrefix(lYear, lnSet)  + "fcwvch where Fvchpdh = " + vchPdh + " and Fterm = " + lMonth;
	            		dbl.executeSql(strSql);//删除凭证
	            	}
                	
	            	HashSet<String> transferNoArray = new HashSet<String>();
	            	HashSet<String> secRecPayNoArray = new HashSet<String>();
	            	HashSet<String> cashRecPayNoArray = new HashSet<String>();
	            	HashSet<String> feeRecPayNoArray = new HashSet<String>();
	            	HashSet<String> integratedNoArray = new HashSet<String>();
		        	strSql = "select  Ftransferno,fsecrecpayno, fcashrecpayno, finvestrecpayno, fintegratedno from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
        			" where fopercode = " + dbl.sqlString(this.operCode) + " order by fentitycode desc";
		        	rs = dbl.openResultSet(strSql);
		        	while(rs.next())
		        	{ //drop table
		        		String transferNo = rs.getString("Ftransferno");
		        		if(transferNo!=null&&!transferNo.equals(""))
		        		{
		        			transferNoArray.add(transferNo);
		        		}
		        		String secRecPayNo = rs.getString("fsecrecpayno");
		        		if(secRecPayNo!=null&&!secRecPayNo.equals(""))
		        		{
		        			secRecPayNoArray.add(secRecPayNo);
		        		}
		        		String cashRecPayNo = rs.getString("fcashrecpayno");
		        		if(cashRecPayNo!=null&&!cashRecPayNo.equals(""))
		        		{
		        			cashRecPayNoArray.add(cashRecPayNo);
		        		}
		        		String feeRecPayNo = rs.getString("finvestrecpayno");
		        		if(feeRecPayNo!=null&&!feeRecPayNo.equals(""))
		        		{
		        			feeRecPayNoArray.add(feeRecPayNo);
		        		}
		        		String integratedNo = rs.getString("fintegratedno");
		        		if(integratedNo!=null&&!integratedNo.equals(""))
		        		{
		        			integratedNoArray.add(integratedNo);
		        		}
		        	}
		        	String[] transferNos = (String[]) transferNoArray.toArray(new String[0]) ;
		        	String[] secRecPayNos =  (String[]) secRecPayNoArray.toArray(new String[0]) ;
		            String[] cashRecPayNos = (String[]) cashRecPayNoArray.toArray(new String[0]) ;
		            String[] feeRecPayNos = (String[]) feeRecPayNoArray.toArray(new String[0]) ;
		            String[] integratedNos = (String[]) integratedNoArray.toArray(new String[0]) ;
		        	
		            for(int j=0;j<secRecPayNos.length;j++)
		            {
			        	strSql = "update " + pub.yssGetTableName("Tb_Data_SecRecPay") +
					        	" set FCheckState = " +
			                    this.checkStateId + ", FCheckUser = " +
			                    dbl.sqlString(pub.getUserCode()) +
			                    ", FCheckTime = '" +
			                    YssFun.formatDatetime(new java.util.Date()) + "'" +
					             " where FNum = " + dbl.sqlString(secRecPayNos[j]);
				         dbl.executeSql(strSql);//更新证券应收应付
		            }
			         
		            for(int j=0;j<cashRecPayNos.length;j++)
		            {
			         strSql = "update " + pub.yssGetTableName("Tb_Data_CashPayRec") +
					         " set FCheckState = " +
			                    this.checkStateId + ", FCheckUser = " +
			                    dbl.sqlString(pub.getUserCode()) +
			                    ", FCheckTime = '" +
			                    YssFun.formatDatetime(new java.util.Date()) + "'" +
				             " where FNum = " + dbl.sqlString(cashRecPayNos[j]);
			         dbl.executeSql(strSql);//更新现金应收应付
		            }
		            
		            for(int j=0;j<feeRecPayNos.length;j++)
		            {
			         strSql = "update " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
					         " set FCheckState = " +
			                    this.checkStateId + ", FCheckUser = " +
			                    dbl.sqlString(pub.getUserCode()) +
			                    ", FCheckTime = '" +
			                    YssFun.formatDatetime(new java.util.Date()) + "'" +
				             " where FNum = " + dbl.sqlString(feeRecPayNos[j]);
			         dbl.executeSql(strSql);//更新运营应收应付
		            }
			         
		            for(int j=0;j<integratedNos.length;j++)
		            {
			         strSql = "update " + pub.yssGetTableName("Tb_Data_Integrated") + 
					         " set FCheckState = " +
			                    this.checkStateId + ", FCheckUser = " +
			                    dbl.sqlString(pub.getUserCode()) +
			                    ", FCheckTime = '" +
			                    YssFun.formatDatetime(new java.util.Date()) + "'" +
				             " where FNum = " + dbl.sqlString(integratedNos[j]);
			         dbl.executeSql(strSql);//更新综合业务
		            }
                    
		            for(int j=0;j<transferNos.length;j++)
		            {
                    strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
	                    " set FCheckState = " +
	                    this.checkStateId + ", FCheckUser = " +
	                    dbl.sqlString(pub.getUserCode()) +
	                    ", FCheckTime = '" +
	                    YssFun.formatDatetime(new java.util.Date()) + "'" +
		         		" where fnum = " + dbl.sqlString(transferNos[j]);
                    dbl.executeSql(strSql);//更新调拨 
		         
                    strSql = "update " +  pub.yssGetTableName("Tb_Cash_SubTransfer") +
	                    " set FCheckState = " +
	                    this.checkStateId + ", FCheckUser = " +
	                    dbl.sqlString(pub.getUserCode()) +
	                    ", FCheckTime = '" +
	                    YssFun.formatDatetime(new java.util.Date()) + "'" +
		         		" where fnum = " + dbl.sqlString(transferNos[j]);
                    dbl.executeSql(strSql);//更新子调拨 
		            }
                    
                    strSql = "update " + pub.yssGetTableName("Tb_Data_ManualOperation") +
	                    " set FCheckState = " +
	                    this.checkStateId + ", FCheckUser = " +
	                    dbl.sqlString(pub.getUserCode()) +
	                    ", FCheckTime = '" +
	                    YssFun.formatDatetime(new java.util.Date()) + "'" +
	                    " where fopercode = " + dbl.sqlString(this.operCode);
                    dbl.executeSql(strSql);//更新手工数据
	         
                }
            }
            //如果sRecycled为空，而Num不为空，则按照Num来执行sql语句
            else if ((this.operCode != null&&!this.operCode.equalsIgnoreCase(""))) {
            	
            	strSql = "select distinct Fvchpdh from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
    			" where fopercode = " + dbl.sqlString(this.operCode);
	        	rs = dbl.openResultSet(strSql);
	        	if(rs.next())
	        	{
	        		vchPdh = rs.getInt("Fvchpdh");
	        	}
	        	dbl.closeResultSetFinal(rs);
                SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd"); 
	            Date date = formate.parse(this.operDate);
	            formate =  new SimpleDateFormat("MM"); 
	            lMonth = Integer.parseInt(formate.format(date));
	            formate = new SimpleDateFormat("yyyy");
	            lYear = Integer.parseInt(formate.format(date));
            	lnSet = Integer.parseInt(this.getSetCode());
            	strSql = "select distinct Fvchpdh from " +  this.getTablePrefix(lYear, lnSet)  + "fcwvch "+ 
						" where Fvchpdh = " + vchPdh + " and Fterm = " + lMonth;
		    	rs = dbl.openResultSet(strSql);
		    	if(rs.next())
		    	{
		    		vchPdh = this.Vch_NextPdh(lMonth, lYear, lnSet);
		    	}
            	if(this.checkStateId==1)//审核
            	{
	    			strSql = " insert into " + this.getTablePrefix(lYear, lnSet)  + "fcwvch (" +
			        		"Fterm,	Fvchpdh, Fvchbh, Fvchzy,Fkmh,Fcyid,Frate,Fbal,Fjd,Fbbal,Fsl,FBsl,Fdate,Fywdate,Fauxiacc,Fzdr) select " +
			        		 lMonth + "," + vchPdh + "," + 
			        		 " t.fentitycode, t.fvchzy, t.facctcode, t.fcurcode, t.fcuryrate, t.fmoney," +
			        	     " (case when t.fdcway = 0 then 'J' else 'D' end) fdcway, " + 
			        	     " t.fportmoney, t.ftradenum, t.ftradenum, t.foperdate, t.foperdate, t.fauxiacc, " + 
			        	     dbl.sqlString(pub.getUserCode()) + " from " + pub.yssGetTableName("Tb_Data_ManualOperation") + " t " +
			        	     " where t.fopercode = " + this.operCode;
	    			dbl.executeSql(strSql);//插入凭证
	        		strSql = "delete from lvchbin where Fvchpdh = " + vchPdh + " and Fterm = " + lMonth + " and Fsetcode = " + lnSet +
			        		" and Fvchzy = " + dbl.sqlString(this.vchZY) + 
			        		" and Fywdate = " + dbl.sqlDate(this.operDate) + " and FMemo = 'Y'";;
	            	dbl.executeSql(strSql);//删除回收站
	            	strSql = " update " +pub.yssGetTableName("Tb_Data_ManualOperation") + " set Fvchpdh = " + vchPdh + 
		        			" where fopercode = " + dbl.sqlString(this.operCode);
			            	dbl.executeSql(strSql);//更新凭证号
	            	
            	}else if(this.checkStateId==0)//反审核
            	{
            		strSql = "insert into lvchbin(Fsetcode,FDelDate,Fterm,Fvchpdh,Fvchbh,Fvchzy,Fkmh,Fcyid,Frate,Fbal,Fjd,Fbbal,Fsl,FBsl,Fdate,Fywdate,Fzdr,FMemo) " +
            				" select " + lnSet + "," + 
		            		dbl.sqlDate(new Date()) + ", Fterm,Fvchpdh,Fvchbh,Fvchzy,Fkmh,Fcyid,Frate,Fbal,Fjd,Fbbal,Fsl,FBsl,Fdate,Fywdate,Fzdr,'Y' from " +
		            		this.getTablePrefix(lYear, lnSet)  + "fcwvch where Fvchpdh = " + vchPdh + " and Fterm = " + lMonth;
	        		dbl.executeSql(strSql);//插入到回收站
	        		strSql = "delete from " + this.getTablePrefix(lYear, lnSet)  + "fcwvch where Fvchpdh = " + vchPdh + " and Fterm = " + lMonth;
	        		dbl.executeSql(strSql);//删除凭证
                	
            	}
            	
            	HashSet<String> transferNoArray = new HashSet<String>();
            	HashSet<String> secRecPayNoArray = new HashSet<String>();
            	HashSet<String> cashRecPayNoArray = new HashSet<String>();
            	HashSet<String> feeRecPayNoArray = new HashSet<String>();
            	HashSet<String> integratedNoArray = new HashSet<String>();
	        	strSql = "select  Ftransferno,fsecrecpayno, fcashrecpayno, finvestrecpayno, fintegratedno from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
    			" where fopercode = " + dbl.sqlString(this.operCode) + " order by fentitycode desc";
	        	rs = dbl.openResultSet(strSql);
	        	while(rs.next())
	        	{
	        		String transferNo = rs.getString("Ftransferno");
	        		if(transferNo!=null&&!transferNo.equals(""))
	        		{
	        			transferNoArray.add(transferNo);
	        		}
	        		String secRecPayNo = rs.getString("fsecrecpayno");
	        		if(secRecPayNo!=null&&!secRecPayNo.equals(""))
	        		{
	        			secRecPayNoArray.add(secRecPayNo);
	        		}
	        		String cashRecPayNo = rs.getString("fcashrecpayno");
	        		if(cashRecPayNo!=null&&!cashRecPayNo.equals(""))
	        		{
	        			cashRecPayNoArray.add(cashRecPayNo);
	        		}
	        		String feeRecPayNo = rs.getString("finvestrecpayno");
	        		if(feeRecPayNo!=null&&!feeRecPayNo.equals(""))
	        		{
	        			feeRecPayNoArray.add(feeRecPayNo);
	        		}
	        		String integratedNo = rs.getString("fintegratedno");
	        		if(integratedNo!=null&&!integratedNo.equals(""))
	        		{
	        			integratedNoArray.add(integratedNo);
	        		}
	        	}
	        	String[] transferNos = (String[]) transferNoArray.toArray(new String[0]) ;
	        	String[] secRecPayNos =  (String[]) secRecPayNoArray.toArray(new String[0]) ;
	            String[] cashRecPayNos = (String[]) cashRecPayNoArray.toArray(new String[0]) ;
	            String[] feeRecPayNos = (String[]) feeRecPayNoArray.toArray(new String[0]) ;
	            String[] integratedNos = (String[]) integratedNoArray.toArray(new String[0]) ;
	        	
	            for(int j=0;j<secRecPayNos.length;j++)
	            {
		        	strSql = "update " + pub.yssGetTableName("Tb_Data_SecRecPay") +
				        	" set FCheckState = " +
		                    this.checkStateId + ", FCheckUser = " +
		                    dbl.sqlString(pub.getUserCode()) +
		                    ", FCheckTime = '" +
		                    YssFun.formatDatetime(new java.util.Date()) + "'" +
				             " where FNum = " + dbl.sqlString(secRecPayNos[j]);
			         dbl.executeSql(strSql);//更新证券应收应付
	            }
		         
	            for(int j=0;j<cashRecPayNos.length;j++)
	            {
		         strSql = "update " + pub.yssGetTableName("Tb_Data_CashPayRec") +
				         " set FCheckState = " +
		                    this.checkStateId + ", FCheckUser = " +
		                    dbl.sqlString(pub.getUserCode()) +
		                    ", FCheckTime = '" +
		                    YssFun.formatDatetime(new java.util.Date()) + "'" +
			             " where FNum = " + dbl.sqlString(cashRecPayNos[j]);
		         dbl.executeSql(strSql);//更新现金应收应付
	            }
	            
	            for(int j=0;j<feeRecPayNos.length;j++)
	            {
		         strSql = "update " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
				         " set FCheckState = " +
		                    this.checkStateId + ", FCheckUser = " +
		                    dbl.sqlString(pub.getUserCode()) +
		                    ", FCheckTime = '" +
		                    YssFun.formatDatetime(new java.util.Date()) + "'" +
			             " where FNum = " + dbl.sqlString(feeRecPayNos[j]);
		         dbl.executeSql(strSql);//更新运营应收应付
	            }
		         
	            for(int j=0;j<integratedNos.length;j++)
	            {
		         strSql = "update " + pub.yssGetTableName("Tb_Data_Integrated") + 
				         " set FCheckState = " +
		                    this.checkStateId + ", FCheckUser = " +
		                    dbl.sqlString(pub.getUserCode()) +
		                    ", FCheckTime = '" +
		                    YssFun.formatDatetime(new java.util.Date()) + "'" +
			             " where FNum = " + dbl.sqlString(integratedNos[j]);
		         dbl.executeSql(strSql);//更新综合业务
	            }
             
	            for(int j=0;j<transferNos.length;j++)
	            {
                 strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
	                    " set FCheckState = " +
	                    this.checkStateId + ", FCheckUser = " +
	                    dbl.sqlString(pub.getUserCode()) +
	                    ", FCheckTime = '" +
	                    YssFun.formatDatetime(new java.util.Date()) + "'" +
		         		" where fnum = " + dbl.sqlString(transferNos[j]);
                 dbl.executeSql(strSql);//更新调拨 
		         
                 strSql = "update " +  pub.yssGetTableName("Tb_Cash_SubTransfer") +
	                    " set FCheckState = " +
	                    this.checkStateId + ", FCheckUser = " +
	                    dbl.sqlString(pub.getUserCode()) +
	                    ", FCheckTime = '" +
	                    YssFun.formatDatetime(new java.util.Date()) + "'" +
		         		" where fnum = " + dbl.sqlString(transferNos[j]);
                 dbl.executeSql(strSql);//更新子调拨 
	            }
                 
                 strSql = "update " + pub.yssGetTableName("Tb_Data_ManualOperation") +
	                    " set FCheckState = " +
	                    this.checkStateId + ", FCheckUser = " +
	                    dbl.sqlString(pub.getUserCode()) +
	                    ", FCheckTime = '" +
	                    YssFun.formatDatetime(new java.util.Date()) + "'" +
	                    " where fopercode = " + dbl.sqlString(this.operCode);
                 dbl.executeSql(strSql);//更新手工数据

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核业务数据出错",e);
        } finally {
        	dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 删除数据，即将数据放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        ResultSet rs = null;
        try {
        	
        	HashSet<String> transferNoArray = new HashSet<String>();
        	HashSet<String> secRecPayNoArray = new HashSet<String>();
        	HashSet<String> cashRecPayNoArray = new HashSet<String>();
        	HashSet<String> feeRecPayNoArray = new HashSet<String>();
        	HashSet<String> integratedNoArray = new HashSet<String>();
        	strSql = "select  Ftransferno,fsecrecpayno, fcashrecpayno, finvestrecpayno, fintegratedno from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
			" where fopercode = " + dbl.sqlString(this.operCode) + " order by fentitycode desc";
        	rs = dbl.openResultSet(strSql);
        	while(rs.next())
        	{
        		String transferNo = rs.getString("Ftransferno");
        		if(transferNo!=null&&!transferNo.equals(""))
        		{
        			transferNoArray.add(transferNo);
        		}
        		String secRecPayNo = rs.getString("fsecrecpayno");
        		if(secRecPayNo!=null&&!secRecPayNo.equals(""))
        		{
        			secRecPayNoArray.add(secRecPayNo);
        		}
        		String cashRecPayNo = rs.getString("fcashrecpayno");
        		if(cashRecPayNo!=null&&!cashRecPayNo.equals(""))
        		{
        			cashRecPayNoArray.add(cashRecPayNo);
        		}
        		String feeRecPayNo = rs.getString("finvestrecpayno");
        		if(feeRecPayNo!=null&&!feeRecPayNo.equals(""))
        		{
        			feeRecPayNoArray.add(feeRecPayNo);
        		}
        		String integratedNo = rs.getString("fintegratedno");
        		if(integratedNo!=null&&!integratedNo.equals(""))
        		{
        			integratedNoArray.add(integratedNo);
        		}
        	}
        	String[] transferNos = (String[]) transferNoArray.toArray(new String[0]) ;
        	String[] secRecPayNos =  (String[]) secRecPayNoArray.toArray(new String[0]) ;
            String[] cashRecPayNos = (String[]) cashRecPayNoArray.toArray(new String[0]) ;
            String[] feeRecPayNos = (String[]) feeRecPayNoArray.toArray(new String[0]) ;
            String[] integratedNos = (String[]) integratedNoArray.toArray(new String[0]) ;
        	
        	conn.setAutoCommit(false);
            bTrans = true;
            
        	
        	for(int i=0;i<secRecPayNos.length;i++)
        	{
	        	strSql = "update " + pub.yssGetTableName("Tb_Data_SecRecPay") +
			        	" set FCheckState = " +
	                    this.checkStateId + ", FCheckUser = " +
	                    dbl.sqlString(pub.getUserCode()) +
	                    ", FCheckTime = '" +
	                    YssFun.formatDatetime(new java.util.Date()) + "'" +
			             " where FNum = " + dbl.sqlString(secRecPayNos[i]);
		        dbl.executeSql(strSql);//删除证券应收应付
        	}
	         
        	for(int i=0;i<cashRecPayNos.length;i++)
        	{
		         strSql = "update " + pub.yssGetTableName("Tb_Data_CashPayRec") +
				         " set FCheckState = " +
		                    this.checkStateId + ", FCheckUser = " +
		                    dbl.sqlString(pub.getUserCode()) +
		                    ", FCheckTime = '" +
		                    YssFun.formatDatetime(new java.util.Date()) + "'" +
			             " where FNum = " + dbl.sqlString(cashRecPayNos[i]);
		         dbl.executeSql(strSql);//删除现金应收应付
        	}
        	
        	for(int i=0;i<feeRecPayNos.length;i++)
        	{
		         strSql = "update " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
				         " set FCheckState = " +
		                    this.checkStateId + ", FCheckUser = " +
		                    dbl.sqlString(pub.getUserCode()) +
		                    ", FCheckTime = '" +
		                    YssFun.formatDatetime(new java.util.Date()) + "'" +
			             " where FNum = " + dbl.sqlString(feeRecPayNos[i]);
		         dbl.executeSql(strSql);//删除运营应收应付
        	}
	         
        	for(int i=0;i<integratedNos.length;i++)
        	{
		         strSql = "update " + pub.yssGetTableName("Tb_Data_Integrated") + 
				         " set FCheckState = " +
			                this.checkStateId + ", FCheckUser = " +
			                dbl.sqlString(pub.getUserCode()) +
			                ", FCheckTime = '" +
			                YssFun.formatDatetime(new java.util.Date()) + "'" +
			             " where FNum = " + dbl.sqlString(integratedNos[i]);
		         dbl.executeSql(strSql);//删除综合业务
        	}
        	
        	for(int i=0;i<transferNos.length;i++)
        	{
	            strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
		            " set FCheckState = " +
		            this.checkStateId + ", FCheckUser = " +
		            dbl.sqlString(pub.getUserCode()) +
		            ", FCheckTime = '" +
		            YssFun.formatDatetime(new java.util.Date()) + "'" +
		     		" where fnum = " + dbl.sqlString(transferNos[i]);
	            dbl.executeSql(strSql);//删除调拨 
	
	            strSql = "update " +  pub.yssGetTableName("Tb_Cash_SubTransfer") +
			        " set FCheckState = " +
			        this.checkStateId + ", FCheckUser = " +
			        dbl.sqlString(pub.getUserCode()) +
			        ", FCheckTime = '" +
			        YssFun.formatDatetime(new java.util.Date()) + "'" +
			 		" where fnum = " + dbl.sqlString(transferNos[i]);
	            dbl.executeSql(strSql);//删除子调拨 
        	}
        	
            strSql = "update " + pub.yssGetTableName("Tb_Data_ManualOperation") +
	            " set FCheckState = " +
	            this.checkStateId + ", FCheckUser = " +
	            dbl.sqlString(pub.getUserCode()) +
	            ", FCheckTime = '" +
	            YssFun.formatDatetime(new java.util.Date()) + "'" +
	            " where fopercode = " + dbl.sqlString(this.operCode);
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除业务数据出错！",e);
        } finally {
        	dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editOperData
     * 修改业务数据
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
        	int vchpdh = -1;
        	strSql = "select distinct Fvchpdh from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
        			" where fopercode = " + dbl.sqlString(this.operCode);
        	rs = dbl.openResultSet(strSql);
        	if(rs.next())
        	{
        		vchpdh = rs.getInt("Fvchpdh");
        	}
        	
        	HashSet<String> transferNoArray = new HashSet<String>();
        	HashSet<String> secRecPayNoArray = new HashSet<String>();
        	HashSet<String> cashRecPayNoArray = new HashSet<String>();
        	HashSet<String> feeRecPayNoArray = new HashSet<String>();
        	HashSet<String> integratedNoArray = new HashSet<String>();
        	strSql = "select  Ftransferno,fsecrecpayno, fcashrecpayno, finvestrecpayno, fintegratedno from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
			" where fopercode = " + dbl.sqlString(this.operCode) + " order by fentitycode desc";
        	rs = dbl.openResultSet(strSql);
        	while(rs.next())
        	{
        		String transferNo = rs.getString("Ftransferno");
        		if(transferNo!=null&&!transferNo.equals(""))
        		{
        			transferNoArray.add(transferNo);
        		}
        		String secRecPayNo = rs.getString("fsecrecpayno");
        		if(secRecPayNo!=null&&!secRecPayNo.equals(""))
        		{
        			secRecPayNoArray.add(secRecPayNo);
        		}
        		String cashRecPayNo = rs.getString("fcashrecpayno");
        		if(cashRecPayNo!=null&&!cashRecPayNo.equals(""))
        		{
        			cashRecPayNoArray.add(cashRecPayNo);
        		}
        		String feeRecPayNo = rs.getString("finvestrecpayno");
        		if(feeRecPayNo!=null&&!feeRecPayNo.equals(""))
        		{
        			feeRecPayNoArray.add(feeRecPayNo);
        		}
        		String integratedNo = rs.getString("fintegratedno");
        		if(integratedNo!=null&&!integratedNo.equals(""))
        		{
        			integratedNoArray.add(integratedNo);
        		}
        	}
        	String[] transferNos = (String[]) transferNoArray.toArray(new String[0]) ;
        	String[] secRecPayNos =  (String[]) secRecPayNoArray.toArray(new String[0]) ;
            String[] cashRecPayNos = (String[]) cashRecPayNoArray.toArray(new String[0]) ;
            String[] feeRecPayNos = (String[]) feeRecPayNoArray.toArray(new String[0]) ;
            String[] integratedNos = (String[]) integratedNoArray.toArray(new String[0]) ;
	
            addSetting(vchpdh,transferNos,secRecPayNos,cashRecPayNos,feeRecPayNos,integratedNos);

            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改业务数据出错！" ,e);
        } finally {
        	dbl.closeResultSetFinal(rs);
        }

    }
    
    //用于viewlist中的显示
    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
           
            if (this.filterType!=null && this.filterType.isOnlyColumns.equals("1")) {
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);

                sVocStr = vocabulary.getVoc(YssCons.YSS_TA_TradeType + "," +
                                            YssCons.YSS_TA_CatType);
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr;//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            if (strSql.length() != 0) {

                yssPageInationBean.setsQuerySQL(strSql);
                yssPageInationBean.setsTableName("ManualOperation");
                //rs =dbl.openResultSet(yssPageInationBean);please recover it immediately
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                	bufShow.append(rs.getString("fopercode") + "").append("\t");
                	bufShow.append(rs.getDate("foperdate")).append("\t");
//                	bufShow.append(rs.getString("fopercode") + "").append("\t");
                	bufShow.append(rs.getString("FPortCode") + "").append("\t");
                	bufShow.append(rs.getString("fvchzy") + "").append("\t");
                	setResult(rs);
                	bufShow.append(YssCons.YSS_LINESPLITMARK);
                    bufAll.append(this.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
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

//            sVocStr = vocabulary.getVoc(YssCons.YSS_TA_TradeType + "," +
//                                        YssCons.YSS_TA_CatType);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取业务数据出错！"+ "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /***
     * 根据 结果集给各变量赋值
     */
    private void setResult(ResultSet rs) throws SQLException, YssException {
    	this.operDate = rs.getDate("foperdate").toString();
    	this.operCode = rs.getString("fopercode");
    	this.portCode = rs.getString("FPortCode");
    	this.vchZY = rs.getString("fvchzy");
    	this.checkStateId = rs.getInt("fcheckstate");
    }
    

    /**
     * 此方法已被修改
     * 修改时间：2008年2月23号
     * 修改人：单亮
     * 原方法的功能：查询出费用连接数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        try {
			RightBean right = new RightBean();
			right.setYssPub(pub);
            //if (!this.strisOnlyColumnss.trim().equals("0")) {//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji 这里调用基类的方法处理加载后的数据
                strSql = "select distinct fopercode,foperdate, fportcode,fvchzy,fcheckstate from " + pub.yssGetTableName("Tb_Data_ManualOperation")
                		+ buildFilterSql() + " order by fopercode asc";// + " where fcheckstate = 1"

        } catch (Exception e) {
            throw new YssException("获取业务数据出错" + "\r\n" + e.getMessage(), e);
        }
        return builderListViewData(strSql);
    }

    /**
     * getListViewData2
     *查看详细信息
     * @return String
     * @throws YssException 
     */
    public String getListViewData2() throws YssException {
		 String strSql = "";
        try {
			RightBean right = new RightBean();
			right.setYssPub(pub);
			 SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd"); 
	            Date date = formate.parse(this.operDate);
	            formate = new SimpleDateFormat("yyyy");
	            int lYear = Integer.parseInt(formate.format(date));
	            int lnSet = Integer.parseInt(this.getSetCode());
			strSql = "select m.FOPERCODE,m.FPORTCODE,m.FOPERDATE,m.FTPLNUM,(m.FACCTCODE || '_' || a.facctname) as facctcode, m.FDCWAY, m.FCURCODE," + 
	          		" to_char(m.FMONEY) as fmoney, to_char(m.FCURYRATE) as fcuryrate, to_char(m.FPORTMONEY) as fportmoney, to_char(m.FTRADENUM) as ftradenum,m.FACCOUNTINGTYPE, m.FSUBTSFTYPECODE," +  
	          		" m.FSECURITYCODE,  m.FCASHACCCODE, m.FIVPAYCATCODE, m.FVCHZY,  m.FCHECKSTATE, m.FCREATOR," + 
	          		" m.FCREATETIME, m.FCHECKUSER, m.FCHECKTIME,m.FENTITYCODE,t.fportname,b.ftplname, m.fauxiacc  from " +  
	          		pub.yssGetTableName("Tb_Data_ManualOperation") + " m join " + 
	          		pub.yssGetTableName("Tb_Para_Portfolio") + " t on m.fportcode = t.fportcode left join "+
	          		pub.yssGetTableName("Tb_Data_Maintenancetpl") + " b on m.ftplnum = b.ftplnum " +
	          		" left join  (select *  from " + this.getTablePrefix(lYear, lnSet)  + "laccount) a on m.facctcode = a.facctcode" +
	          		" where  fopercode = " + dbl.sqlString(this.operCode) + " order by fentitycode asc "; 
        } catch (Exception e) {
            throw new YssException("获取业务数据信息" + "\r\n" + e.getMessage(), e);
        }
        return builderListViewData2(strSql);
    }
    
    
    public String builderListViewData2(String strSql) throws YssException {
        String sShowDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        try {
            if (strSql.length() != 0) {

//                yssPageInationBean.setsQuerySQL(strSql);
//                yssPageInationBean.setsTableName("ManualOperation");
                //rs =dbl.openResultSet(yssPageInationBean);please recover it immediately
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append(rs.getString("foperCode")).append("\t");
                    bufShow.append(rs.getString("fportCode")).append("\t");
                    bufShow.append(rs.getDate("foperDate")).append("\t");
                    bufShow.append(rs.getString("ftplNum")).append("\t");
                    bufShow.append(rs.getString("fvchZY")).append("\t");
                    bufShow.append(rs.getString("fentityCode")).append("\t");
                    bufShow.append(rs.getString("facctCode")).append("\t");
                    bufShow.append(rs.getString("fdcWay")).append("\t");
                    bufShow.append(rs.getString("fcurcode")).append("\t");
//                    bufShow.append(rs.getFloat("fmoney")).append("\t");
                    bufShow.append(rs.getString("fmoney")).append("\t");
                    bufShow.append(rs.getString("fcuryrate")).append("\t");
                    bufShow.append(rs.getString("fportmoney")).append("\t");
                    bufShow.append(rs.getString("ftradeNum")).append("\t");
                    bufShow.append(rs.getString("faccountingtype")).append("\t");
                    bufShow.append(rs.getString("fsubTSFTypeCode")).append("\t");
                    bufShow.append(rs.getString("fsecurityCode")).append("\t");
                    bufShow.append(rs.getString("fcashAccCode")).append("\t");
                    bufShow.append(rs.getString("fivpaycatcode")).append("\t");
                    bufShow.append(rs.getString("fportname")).append("\t");
                    bufShow.append(rs.getString("ftplname")).append("\t");
                    bufShow.append(rs.getString("fauxiacc")).append("\t");
                	setResult(rs);
                	bufShow.append(YssCons.YSS_LINESPLITMARK);
                }
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            return sShowDataStr + "\r\f"  ;

        } catch (Exception e) {
            throw new YssException("获取业务数据信息出错！"+ "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /*
     * 
     * 获取套账号
     */
    private String getSetCode( ) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String portInfo = "";
        try {
        	//FLinkCode,FLinkName,FPortCode,FBookSetCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser
            sqlStr = "select distinct l.fsetcode from lsetlist l join " + pub.yssGetTableName("Tb_Para_Portfolio") + 
            		" t on l.fsetid = t.fassetcode  where t.fportcode = " + dbl.sqlString(this.portCode);
            rs = dbl.queryByPreparedStatement(sqlStr); 
            if (rs.next()) {
            	portInfo = rs.getString("fsetcode");
            }
            return portInfo;
        } catch (Exception e) {
            throw new YssException("获取套账号出错！"+ "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    	
    /**
     * 获取当月最大的下一个凭证号
     * @param lMonthTmp int
     * @throws YssException
     * @throws SQLException
     * @return int
     */
    private int Vch_NextPdh(int lMonthTmp,int lYear, int lnSet) throws YssException, //邵宏伟20050414
          SQLException {
       ResultSet rs = null;
       int lPdhtmp = 0;
       int lBinpdh = 0;
       try
       {
	       String SqlStr = "select Max(fvchpdh) as MaxVchNum from " +
	             this.getTablePrefix(lYear, lnSet) +
	             "fcwvch where fterm=" + lMonthTmp;
	       rs = dbl.openResultSet(SqlStr);
	       if (rs.next()) {
	          lPdhtmp = rs.getInt("MaxVchNum") + 1;
	       }
	       dbl.closeResultSetFinal(rs);
	       SqlStr = " select max(fvchpdh) as MaxVchNum from lvchbin  where to_char(fywdate,'yyyymmdd') like '" + lYear + "%'" + 
	       					" and fsetcode = " + lnSet + " and fterm = " + lMonthTmp;
	       rs = dbl.openResultSet(SqlStr);
	       if (rs.next()) {
	    	   lBinpdh = rs.getInt("MaxVchNum") + 1;
	       }
	       return lPdhtmp > lBinpdh ? lPdhtmp : lBinpdh;
       }
       catch(Exception e)
       {
    	   throw new YssException("获取凭证单号出错，请确认"+lYear+"年套账是否存在!" + "\r\n"+ e.getMessage());
       }finally
       {
    	   dbl.closeResultSetFinal(rs);
       }
    }
    
    protected String getTablePrefix(int lYear, int lnSet) {
		String stmp;
		if ((lYear > 999) && (lnSet != 0)) { //年份四位
			stmp = "A"  + lYear +
			 new DecimalFormat("000").format(lnSet);
			return (stmp.length() == 1) ? "" : stmp;
			}
			return "";
	}
    
    /**
     * getListViewData3
     *获取模板信息
     * @return String
     * @throws YssException 
     */
    public String getListViewData3() throws YssException {
    	 String strSql = "";
         try {
 			RightBean right = new RightBean();
 			right.setYssPub(pub);
 			
            SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd"); 
            Date date = formate.parse(this.operDate);
            formate = new SimpleDateFormat("yyyy");
            int lYear = Integer.parseInt(formate.format(date));
            int lnSet = Integer.parseInt(this.getSetCode());
            strSql =  "select t.FAcctCode,a.facctname,a.fauxiacc,t.FDCWay,t.FAccountingType,t.FSubTsfTypeCode, t.fentitycode, a.fcurcode from " +
            pub.yssGetTableName("Tb_Data_Mtpldatadetail") + " t join " + this.getTablePrefix(lYear, lnSet)  + "laccount a" +
            " on t.facctcode = a.facctcode "  + " where  ftplnum = " + 
			dbl.sqlString(this.tplNum) + " order by fentitycode asc"; 
         } catch (Exception e) {
             throw new YssException("获取模板信息出错" + "\r\n" + e.getMessage(), e);
         }
         return builderListViewData3(strSql);
    }
    
    public String builderListViewData3(String strSql) throws YssException {
        String sShowDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        try {
            if (strSql.length() != 0) {

//                yssPageInationBean.setsQuerySQL(strSql);
//                yssPageInationBean.setsTableName("ManualOperation");
                //rs =dbl.openResultSet(yssPageInationBean);please recover it immediately
            	String sql = "select ftplresume from " + 
					pub.yssGetTableName("Tb_Data_Maintenancetpl") + " where  ftplnum = " + 
					dbl.sqlString(this.tplNum); 
            	rs = dbl.openResultSet(sql);
            	if(rs.next())
            	{
            		this.vchZY = rs.getString("ftplresume");
            	}
                rs = dbl.openResultSet(strSql);
                String acctShowCode = "";
                while (rs.next()) {
                	String facctCode = rs.getString("facctCode");
                	String acctName = rs.getString("facctname");
                	acctShowCode = facctCode + "_" + acctName;
                    bufShow.append("").append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append(this.vchZY).append("\t");
                    bufShow.append(rs.getString("fentityCode")).append("\t");//保留
//                    bufShow.append(rs.getString("facctCode")).append("\t");//保留
                    bufShow.append(acctShowCode).append("\t");//保留
                    bufShow.append(rs.getString("fdcWay")).append("\t");//保留
                    bufShow.append(rs.getString("fcurcode")).append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append(rs.getString("faccountingtype")).append("\t");//保留
                    bufShow.append(rs.getString("fsubTSFTypeCode")).append("\t");//保留
                    bufShow.append("").append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append("").append("\t");
                    bufShow.append(rs.getString("fauxiacc")).append("\t");
                	//setResult(rs);
                	bufShow.append(YssCons.YSS_LINESPLITMARK);
                }
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            return sShowDataStr + "\r\f"  ;

        } catch (Exception e) {
            throw new YssException("获取模板信息出错，请确认业务日期套账是否存在！"+ "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getOperData
     */
    public void getOperData() {
    }

    /**
     * getTreeViewData1
     *此方法被改用 用于获取控件控制信息
     * @return String
     * @throws YssException 
     */
    public String getTreeViewData1() throws YssException {
    	String strSql = "";
    	ResultSet rs = null;
    	String securCode = "";
    	String feeCode = "";
    	String cashCode = "";
    	StringBuffer bufShow = new StringBuffer();
        try {
			RightBean right = new RightBean();
			right.setYssPub(pub);
			if(this.acctCode.startsWith("1"))
	    	{
				strSql = "select FSECURITYSHOW,FIVPAYCATSHOW,FCASHACCSHOW,FSUBTSFTYPECODE from " + 
				" Tb_BASE_ACTSTFRELA where FCheckState = 1 and FSubjectType = 1 and FACCSUBTYPECODE = " + dbl.sqlString(this.accountType);
	    	}else if(this.acctCode.startsWith("2"))
	    	{
	    		strSql = "select FSECURITYSHOW,FIVPAYCATSHOW,FCASHACCSHOW,FSUBTSFTYPECODE from " + 
				" Tb_BASE_ACTSTFRELA where FCheckState = 1 and FSubjectType = 2 and FACCSUBTYPECODE = " + dbl.sqlString(this.accountType);
	    	}else if(this.acctCode.startsWith("3"))
	    	{
	    		strSql = "select FSECURITYSHOW,FIVPAYCATSHOW,FCASHACCSHOW,FSUBTSFTYPECODE from " + 
				" Tb_BASE_ACTSTFRELA where FCheckState = 1 and FSubjectType = 3 and FACCSUBTYPECODE = " + dbl.sqlString(this.accountType);
	    	}else
	    	{
	    		strSql = "select FSECURITYSHOW,FIVPAYCATSHOW,FCASHACCSHOW,FSUBTSFTYPECODE from " + 
	    		" Tb_BASE_ACTSTFRELA where FCheckState = 1 and FACCSUBTYPECODE = " + dbl.sqlString(this.accountType);
	    	}
			rs = dbl.openResultSet(strSql);
			while(rs.next())
			{
				if(securCode.equals("")){
					securCode = rs.getString("FSECURITYSHOW");
					bufShow.append(securCode).append("\t");
				}
				if(cashCode.equals("")){
					cashCode = rs.getString("FCASHACCSHOW");
					bufShow.append(cashCode).append("\t");
				}
				if(feeCode.equals("")){
					feeCode = rs.getString("FIVPAYCATSHOW");	
					bufShow.append(feeCode).append("\t");
				}
				
				bufShow.append("'" + rs.getString("FSUBTSFTYPECODE") + "'").append(",");
			}
				String resultStr = "";
				if(bufShow.length()>0)
				{
					resultStr = bufShow.substring(0, bufShow.length()-1);
				}
				bufShow = new StringBuffer();
	            bufShow.append(resultStr).append("\t");
	            return bufShow.toString();	
        } catch (Exception e) {
            throw new YssException("获取业务数据控件设置信息出错！" + "\r\n" + e.getMessage(), e);
        }  finally {
            dbl.closeResultSetFinal(rs);
        }
        
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }
    
    /**
     * parseRowStr
     * 解析业务数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry = null;

        try {
	        	if (sRowStr.trim().length() == 0) {
	                 return;
	            }
	            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
	            if (sRowStr.indexOf("\r\t") >= 0) {
	                if (this.filterType == null) {
	                    this.filterType = new ManualDataBean();
	                    this.filterType.setYssPub(pub);
	                }
	                this.filterType.parseSimpleInfo(sRowStr.split("\r\t")[1]);
	            }
	            if(this.sRecycled.indexOf("\f\f")>=0)
	            {
	            	reqAry = sRecycled.split("\f\f")[0];
	            	this.parseVchStr(reqAry);
	            }else
	            {
	            	reqAry = sRecycled.split("\r\n")[0];
	            	this.parseSimpleInfo(reqAry);
	            }
        } catch (Exception e) {
            throw new YssException("解析业务数据出错"+ "\r\n" + e.getMessage(), e);
        }
    }
    
    private void parseSimpleInfo(String rowStr)
    {
    	String[] reqAry = rowStr.split("\t"); 
    	if(reqAry.length>=6)
		{
    		this.operCode = reqAry[0];
        	this.portCode = reqAry[1];
        	if(reqAry[2]!=null&&!reqAry[2].equals(""))
    		{
        	this.tplNum = reqAry[2];
    		}
        	this.operDate = reqAry[3];
    		this.vchZY = reqAry[4];
    		if(reqAry[5]!=null&&!reqAry[5].equals(""))
    		{
    			this.checkStateId = Integer.parseInt(reqAry[5]);
    		}
    		this.isOnlyColumns = reqAry[6];
		}
    	super.parseRecLog(); 
    }

    private void parseVchStr(String rowStr)
    {
    	String[] reqAry =  rowStr.split("\t");
    	this.operCode = reqAry[0];
    	this.portCode = reqAry[1];
    	this.operDate = reqAry[2];
    	this.tplNum = reqAry[3];
    	this.entityCode = reqAry[4];
    	this.acctCode = reqAry[5];
    	this.fdcWay = reqAry[6];
    	this.curcode = reqAry[7];
    	this.fmoney = reqAry[8];
    	this.fcuryrate = reqAry[9];
    	this.fportmoney = reqAry[10];
    	this.tradeNum = reqAry[11];
    	this.accountType = reqAry[12];
    	this.subTSFTypeCode = reqAry[13];
    	this.securityCode = reqAry[14];
    	this.cashAccCode = reqAry[15];
    	this.feeCode = reqAry[16];
    	this.vchZY = reqAry[17];
    	this.auxiAcc = reqAry[18];
    	this.checkStateId = YssFun.toInt(reqAry[19]);
    	super.parseRecLog();
    }
    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji 这里调用基类的方法处理加载后的数据
            if (this.filterType.isOnlyColumns.equalsIgnoreCase("1")) {
                sResult = sResult + " and 1=2 ";
                return sResult;
            }
            //业务编号
            if (this.filterType.operCode.length() != 0) {
                sResult = sResult + " and fopercode like '" +
                    filterType.operCode.replaceAll("'", "''") + "%'";
            }
            //组合编号
            if (this.filterType.portCode.length() != 0) {
                sResult = sResult + " and FPortCode like '" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }
            
            if (this.filterType.vchZY.length() != 0) {
            	 sResult = sResult + " and FVchZy like '" +
                 filterType.vchZY.replaceAll("'", "''") + "%'";
            }
            
            if (this.filterType.operDate != null &&
                !YssFun.formatDate(filterType.operDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and FOperDate = " +
                    dbl.sqlDate(filterType.operDate);
            }
            
            
        }
        return sResult;
    }

    /**
     * saveMutliOperData
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliOperData(String sMutilRowStr) {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
//    	if(sType.equals("treeview1"))
//    	{
//    		
//    	}
        return "";
    }
    
//    private String 
    

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {

    	return null;
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }


    /**
     * 删除回收站内的数据，即将数据从数据库彻底删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
    	int vchpdh = -1;
    	int lMonth = -1;
    	int lnSet = -1;
        try {
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
            	if(this.sRecycled.indexOf("\r\t")>0)
            	{
            		arrData = sRecycled.split("\r\t")[0].split("\r\n");
            	}else
            	{
            		arrData = sRecycled.split("\r\n");
            	}
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseSimpleInfo(arrData[i]);
            		SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd"); 
    	            Date date = formate.parse(this.operDate);
    	            formate =  new SimpleDateFormat("MM"); 
    	            lMonth = Integer.parseInt(formate.format(date));
    	            lnSet = Integer.parseInt(this.getSetCode());
        	        
    	            strSql = "select distinct Fvchpdh from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
    				" where fopercode = " + dbl.sqlString(this.operCode);
    				rs = dbl.openResultSet(strSql);
    				if(rs.next())
    				{
    					vchpdh = rs.getInt("Fvchpdh");
    				}
    				
    				HashSet<String> transferNoArray = new HashSet<String>();
	            	HashSet<String> secRecPayNoArray = new HashSet<String>();
	            	HashSet<String> cashRecPayNoArray = new HashSet<String>();
	            	HashSet<String> feeRecPayNoArray = new HashSet<String>();
	            	HashSet<String> integratedNoArray = new HashSet<String>();
		        	strSql = "select  Ftransferno,fsecrecpayno, fcashrecpayno, finvestrecpayno, fintegratedno from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
        			" where fopercode = " + dbl.sqlString(this.operCode) + " order by fentitycode desc";
		        	rs = dbl.openResultSet(strSql);
		        	while(rs.next())
		        	{
		        		String transferNo = rs.getString("Ftransferno");
		        		if(transferNo!=null&&!transferNo.equals(""))
		        		{
		        			transferNoArray.add(transferNo);
		        		}
		        		String secRecPayNo = rs.getString("fsecrecpayno");
		        		if(secRecPayNo!=null&&!secRecPayNo.equals(""))
		        		{
		        			secRecPayNoArray.add(secRecPayNo);
		        		}
		        		String cashRecPayNo = rs.getString("fcashrecpayno");
		        		if(cashRecPayNo!=null&&!cashRecPayNo.equals(""))
		        		{
		        			cashRecPayNoArray.add(cashRecPayNo);
		        		}
		        		String feeRecPayNo = rs.getString("finvestrecpayno");
		        		if(feeRecPayNo!=null&&!feeRecPayNo.equals(""))
		        		{
		        			feeRecPayNoArray.add(feeRecPayNo);
		        		}
		        		String integratedNo = rs.getString("fintegratedno");
		        		if(integratedNo!=null&&!integratedNo.equals(""))
		        		{
		        			integratedNoArray.add(integratedNo);
		        		}
		        	}
		        	String[] transferNos = (String[]) transferNoArray.toArray(new String[0]) ;
		        	String[] secRecPayNos =  (String[]) secRecPayNoArray.toArray(new String[0]) ;
		            String[] cashRecPayNos = (String[]) cashRecPayNoArray.toArray(new String[0]) ;
		            String[] feeRecPayNos = (String[]) feeRecPayNoArray.toArray(new String[0]) ;
		            String[] integratedNos = (String[]) integratedNoArray.toArray(new String[0]) ;
                	
                    strSql = "delete from lvchbin where Fvchpdh = " + vchpdh +
                    			" and Fterm = " + lMonth + " and Fsetcode = " + lnSet +
                    			" and Fvchzy = " + dbl.sqlString(this.vchZY) + 
                    			" and Fywdate = " + dbl.sqlDate(this.operDate)  + " and FMemo = 'Y'"; ;
                	dbl.executeSql(strSql);//清除回收站中的凭证
                	
                	for(int j=0;j<secRecPayNos.length;j++)
                	{
	                	strSql = "delete from " + pub.yssGetTableName("Tb_Data_SecRecPay") +
			       	         " where FNum = " + dbl.sqlString(secRecPayNos[j]);
		       	        dbl.executeSql(strSql);//删除证券应收应付
                	}
       	         	
                	for(int j=0;j<cashRecPayNos.length;j++)
                	{
		       	        strSql = "delete from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
		       	        " where FNum = " + dbl.sqlString(cashRecPayNos[j]);
		       	        dbl.executeSql(strSql);//删除现金应收应付
                	}
       	         	
                	for(int j=0;j<feeRecPayNos.length;j++)
                	{
		       	        strSql = "delete from " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
		       	        " where FNum = " + dbl.sqlString(feeRecPayNos[j]);
		       	        dbl.executeSql(strSql);//删除运营应收应付
                	}
       	         	
                	for(int j=0;j<integratedNos.length;j++)
                	{
	       	         	strSql = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") + 
	       	         	" where FNum = " + dbl.sqlString(integratedNos[j]);
	       	         	dbl.executeSql(strSql);//删除综合业务
                	}
                	
                	for(int j=0;j<transferNos.length;j++)
                	{
	                    strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
		         			" where fnum = " + dbl.sqlString(transferNos[j]);
	                    dbl.executeSql(strSql);//删除调拨 
		         
	                    strSql = "delete from " +  pub.yssGetTableName("Tb_Cash_SubTransfer") +
		         			" where fnum = " + dbl.sqlString(transferNos[j]);
	                    dbl.executeSql(strSql);//删除子调拨 
                	}
                    strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_ManualOperation") +
                    " where fopercode = " + dbl.sqlString(this.operCode);
                    //清楚手工数据
	                dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而Num不为空，则按照Num来执行sql语句
            else if (this.operCode != "" && this.operCode != null) {
            	SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd"); 
	            Date date = formate.parse(this.operDate);
	            formate =  new SimpleDateFormat("MM"); 
	            lMonth = Integer.parseInt(formate.format(date));
	            lnSet = Integer.parseInt(this.getSetCode());
    	        
	            strSql = "select distinct Fvchpdh from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
				" where fopercode = " + dbl.sqlString(this.operCode);
				rs = dbl.openResultSet(strSql);
				if(rs.next())
				{
					vchpdh = rs.getInt("Fvchpdh");
				}
				
				HashSet<String> transferNoArray = new HashSet<String>();
            	HashSet<String> secRecPayNoArray = new HashSet<String>();
            	HashSet<String> cashRecPayNoArray = new HashSet<String>();
            	HashSet<String> feeRecPayNoArray = new HashSet<String>();
            	HashSet<String> integratedNoArray = new HashSet<String>();
	        	strSql = "select  Ftransferno,fsecrecpayno, fcashrecpayno, finvestrecpayno, fintegratedno from " + pub.yssGetTableName("Tb_Data_ManualOperation") + 
    			" where fopercode = " + dbl.sqlString(this.operCode) + " order by fentitycode desc";
	        	rs = dbl.openResultSet(strSql);
	        	while(rs.next())
	        	{
	        		String transferNo = rs.getString("Ftransferno");
	        		if(transferNo!=null&&!transferNo.equals(""))
	        		{
	        			transferNoArray.add(transferNo);
	        		}
	        		String secRecPayNo = rs.getString("fsecrecpayno");
	        		if(secRecPayNo!=null&&!secRecPayNo.equals(""))
	        		{
	        			secRecPayNoArray.add(secRecPayNo);
	        		}
	        		String cashRecPayNo = rs.getString("fcashrecpayno");
	        		if(cashRecPayNo!=null&&!cashRecPayNo.equals(""))
	        		{
	        			cashRecPayNoArray.add(cashRecPayNo);
	        		}
	        		String feeRecPayNo = rs.getString("finvestrecpayno");
	        		if(feeRecPayNo!=null&&!feeRecPayNo.equals(""))
	        		{
	        			feeRecPayNoArray.add(feeRecPayNo);
	        		}
	        		String integratedNo = rs.getString("fintegratedno");
	        		if(integratedNo!=null&&!integratedNo.equals(""))
	        		{
	        			integratedNoArray.add(integratedNo);
	        		}
	        	}
	        	String[] transferNos = (String[]) transferNoArray.toArray(new String[0]) ;
	        	String[] secRecPayNos =  (String[]) secRecPayNoArray.toArray(new String[0]) ;
	            String[] cashRecPayNos = (String[]) cashRecPayNoArray.toArray(new String[0]) ;
	            String[] feeRecPayNos = (String[]) feeRecPayNoArray.toArray(new String[0]) ;
	            String[] integratedNos = (String[]) integratedNoArray.toArray(new String[0]) ;
            	
                strSql = "delete from lvchbin where Fvchpdh = " + vchpdh +
                			" and Fterm = " + lMonth + " and Fsetcode = " + lnSet +
                			" and Fvchzy = " + dbl.sqlString(this.vchZY) + 
                			" and Fywdate = " + dbl.sqlDate(this.operDate);
            	dbl.executeSql(strSql);//清除回收站中的凭证
            	
            	for(int j=0;j<secRecPayNos.length;j++)
            	{
                	strSql = "delete from " + pub.yssGetTableName("Tb_Data_SecRecPay") +
		       	         " where FNum = " + dbl.sqlString(secRecPayNos[j]);
	       	        dbl.executeSql(strSql);//删除证券应收应付
            	}
   	         	
            	for(int j=0;j<cashRecPayNos.length;j++)
            	{
	       	        strSql = "delete from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
	       	        " where FNum = " + dbl.sqlString(cashRecPayNos[j]);
	       	        dbl.executeSql(strSql);//删除现金应收应付
            	}
   	         	
            	for(int j=0;j<feeRecPayNos.length;j++)
            	{
	       	        strSql = "delete from " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
	       	        " where FNum = " + dbl.sqlString(feeRecPayNos[j]);
	       	        dbl.executeSql(strSql);//删除运营应收应付
            	}
   	         	
            	for(int j=0;j<integratedNos.length;j++)
            	{
       	         	strSql = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") + 
       	         	" where FNum = " + dbl.sqlString(integratedNos[j]);
       	         	dbl.executeSql(strSql);//删除综合业务
            	}
            	
            	for(int j=0;j<transferNos.length;j++)
            	{
                    strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
	         			" where fnum = " + dbl.sqlString(transferNos[j]);
                    dbl.executeSql(strSql);//删除调拨 
	         
                    strSql = "delete from " +  pub.yssGetTableName("Tb_Cash_SubTransfer") +
	         			" where fnum = " + dbl.sqlString(transferNos[j]);
                    dbl.executeSql(strSql);//删除子调拨 
            	}
                strSql = "delete from " +
                pub.yssGetTableName("Tb_Data_ManualOperation") +
                " where fopercode = " + dbl.sqlString(this.operCode);
                //清楚手工数据
                dbl.executeSql(strSql);
                
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除业务数据出错"+ "\r\n" + e.getMessage(), e);
        } finally {
        	dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    
    public String getTreeViewGroupData1() throws YssException {
        return "";
    }
    
    /**
     * 
     *此方法被改用 用于获取核算类型 调拨类型信息 
     */
    public String getTreeViewGroupData2() throws YssException {
    	 ResultSet rs = null;
         StringBuffer bufShow = new StringBuffer(); 
         try
         {
        	   if(this.acctCode!=null&&!this.acctCode.trim().equals(""))
        	   {
        		   String SqlStr = " select distinct faccsubtypecode, faccountsubtype  from " + 
        		   					" Tb_Base_ACTSTFRELA where FCheckState = 1 and FSubjectType = " + this.acctCode.substring(0,1);
        		   if(!this.acctCode.substring(0,1).equals("1")&&!this.acctCode.substring(0,1).equals("2")&&!this.acctCode.substring(0,1).equals("3"))
        		   {
	        		   SqlStr = " select distinct faccsubtypecode, faccountsubtype  from Tb_Base_ACTSTFRELA where FCheckState = 1 ";
        		   }
				       rs = dbl.openResultSet(SqlStr);
				       while (rs.next()) {
				    	 bufShow.append(rs.getString("faccsubtypecode")).append(","); 
				    	 bufShow.append(rs.getString("faccountsubtype")).append(";"); 
				       }
			       String resultStr = "";
				   if(bufShow.length()>0)
				   {
					resultStr = bufShow.substring(0, bufShow.length()-1);
				   }
				   bufShow = new StringBuffer();
			       bufShow.append(" , ;").append(resultStr);
        	   }else
        	   {
			       String SqlStr = " select distinct faccsubtypecode, faccountsubtype  from Tb_Base_ACTSTFRELA where FCheckState = 1 ";
			       rs = dbl.openResultSet(SqlStr);
			       while (rs.next()) {
			    	 bufShow.append(rs.getString("faccsubtypecode")).append(","); 
			    	 bufShow.append(rs.getString("faccountsubtype")).append(";"); 
			       }
			       String resultStr = "";
				   if(bufShow.length()>0)
				   {
					resultStr = bufShow.substring(0, bufShow.length()-1);
				   }
				   bufShow = new StringBuffer();
			       bufShow.append(" , ;").append(resultStr);
        	   }
		       return bufShow.toString();	
         }
         catch(Exception e)
         {
      	   throw new YssException("获取核算类型控件信息出错！" ,e);
         }finally
         {
      	   dbl.closeResultSetFinal(rs);
         }
    	
    }

    /*
     * 用与获取业务编号
     * 
     */
    public String getTreeViewGroupData3() throws YssException {
    	  ResultSet rs = null;
          int lPdhtmp = 0;
          try
          {
   	       String SqlStr = "select Max(fopercode) as fopercode from " +
   	       pub.yssGetTableName("Tb_Data_ManualOperation");
   	            
   	       rs = dbl.openResultSet(SqlStr);
   	       if (rs.next()) {
   	    	   lPdhtmp = rs.getInt("fopercode") + 1;
   	       }
   	       return String.valueOf(lPdhtmp);
          }
          catch(Exception e)
          {
       	   throw new YssException("获取业务编号出错！" + e.getMessage());
          }finally
          {
       	   dbl.closeResultSetFinal(rs);
          }
    }

    public String getListViewGroupData1() throws YssException {
    	return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    /*
     * 用于判断月结
     * 
     */
    public String getListViewGroupData4() throws YssException {
    	StringBuffer bufShow = new StringBuffer();
    	if(this.MonthClosed())
    	{
    		bufShow.append("YJYJ").append("\f\f");
    	}else
    	{
    		bufShow.append("MYYJ").append("\f\f");
    	}
    	bufShow.append(this.checkGuessNavStat());
        return bufShow.toString();
    }
    
    /*
     * 用于获取模板编号
     * @see com.yss.main.dao.IClientListView#getListViewGroupData5()
     */
    public String getListViewGroupData5() throws YssException {
    	String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		int iResult = 0;
		try
		{
			strSql = "Select max(FTplNum) as maxNum from " + pub.yssGetTableName("tb_data_maintenancetpl");
			rs = dbl.queryByPreparedStatement(strSql);
			if (rs.next())
			{
				iResult = rs.getInt("maxNum");
			}
			iResult = iResult + 1;
			sReturn = String.valueOf(iResult);
			for (int i = sReturn.length(); i < 8; i++)
			{
				sReturn = "0" + sReturn;
			}
		}
		catch(Exception ye)
		{
			throw new YssException("获取模板编号出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		return sReturn;
        
    }

}
