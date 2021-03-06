package com.yss.main.operdeal.datainterface.cnstock.shstock;

import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.dsub.*;
import java.sql.*;
import com.yss.util.*;
import com.yss.main.operdata.BondInterestBean;
import com.yss.main.operdeal.datainterface.pretfun.*;

import java.util.*;

import com.yss.main.operdeal.datainterface.cnstock.CtlStock;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ExchangeBondBean;
import com.yss.main.operdeal.datainterface.cnstock.pojo.FeeAttributeBean;
import com.yss.main.operdeal.datainterface.cnstock.pojo.FeeWayBean;
import com.yss.main.operdeal.datainterface.cnstock.pojo.PublicMethodBean;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadTypeBean;
import com.yss.main.datainterface.cnstock.BrokerRateBean;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;
import com.yss.main.datainterface.cnstock.RateSpeciesTypeBean;

public class SHJSMXBean extends DataBase{
	//delete by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
	//String assetGroupCode = null; //组合群代码
	HashMap hmMTVInfo = null;
	ArrayList alShowZqdm = new ArrayList();
	SHGHBean shgh = null;
	ArrayList alZQCodes = new ArrayList();//用于储存要存到债券利息表的债券代码
	ArrayList alZQInfo = new ArrayList();//用于储存要存到债券利息表的数据
	/**
	 * 构造函数
	 */
    public SHJSMXBean() {
    	
    }
    
    public ArrayList getAlShowZqdm(){
    	return alShowZqdm;
    }
	
	public void makeData(java.util.Date tradeDate, String portCode, String checkState, HashMap hmParam) throws YssException {
        shgh = new SHGHBean();
        shgh.setYssPub(pub);
        
		insertIntoHzJkMx();
		insertIntoBondInterest();
	}
	

	
	/**
	 * 将ShJsMx表的数据处理到HzJkMx中
	 * @throws YssException
	 */
	private void insertIntoHzJkMx()throws YssException{
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        ResultSet rs = null; //声明结果集
        String strSql = null; //储存sql语句
        String strSql1= null;
        PreparedStatement pstmt = null; //声明PreparedStatement
        String[] arrPortCodes = null; 
        String TSInfo = null;
        ReadTypeBean readType = null; //声明读数处理方式实体类
        String FTZBZ = null;
        String[] subTSInfo = null;
        String tradeSeats = null;
        String stockHolders = null;
        String zqbz = "";
        String zqdm = "";
        boolean isGzlx = false;
        FeeAttributeBean feeAttribute = null; //声明费用属性实体类
        try{
            con.setAutoCommit(false); //开启事务
            bTrans = true;
        	
            CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin(); //新建CNInterfaceParamAdmin
            interfaceParam.setYssPub(pub);

            //获取数据接口参数设置的读书处理方式界面设置的参数对应的HashMap
            hmReadType = (HashMap) interfaceParam.getReadTypeBean();
            
            CtlStock cs = new CtlStock();
            cs.setYssPub(pub);
            hmPortHolderSeat = cs.getPStockHolderAndSeat(this.sPort);
            //获取SHGH表中所有席位代码对应的券商代码对应的HashMap
            hmBrokerCode = getBrokerCode("XWDM", "SHJSMX", false, sDate,sPort);
            
            //获取数据接口参数设置的交易所债券参数设置界面设置的参数对应的HashMap
            hmExchangeBond = (HashMap) interfaceParam.getExchangeBondBean();

            //获取数据接口参数设置的交易费用计算方式界面设置的参数对应的HashMap
            hmTradeFee = (HashMap) interfaceParam.getTradeFeeBean();

            //获取数据接口参数设置的费用承担方向界面设置的参数对应的HashMap
            hmFeeWay = (HashMap) interfaceParam.getFeeWayBean();

            RateSpeciesTypeBean rateSpeciesType = new RateSpeciesTypeBean();
            rateSpeciesType.setYssPub(pub);

            //获取交易费率品种设置界面设置的费率对应的HashMap
            //edit by songjie 2010.03.22 MS00924 QDV4赢时胜（测试）2010年03月19日02_B
            hmRateSpeciesType = (HashMap) rateSpeciesType.getRateSpeciesTypeBean(this.sDate);
            
            BrokerRateBean brokerRate = new BrokerRateBean();
            brokerRate.setYssPub(pub);

            //获取券商佣金利率设置界面设置的券商佣金利率对应的HashMap
            hmBrokerRate = (HashMap) brokerRate.getBrokerReateBean(this.sDate);
            
            hmMTVInfo = super.getMTVSelInfo();
            
            assetGroupCode = pub.getAssetGroupCode();//组合群代码
            
            //在交易接口明细表中删除相关业务日期和已选组合代码的数据
            strSql = " delete from " + pub.yssGetTableName("Tb_HzJkMx") + " where FInDate = " +
                dbl.sqlDate(this.sDate) + " and FPortCode in(" + operSql.sqlCodes(this.sPort) + 
                ") and FJKDM = 'SHJSMX'";
            dbl.executeSql(strSql);
            
            arrPortCodes = this.sPort.split(","); //储存拆分的组合代码
           
            
            strSql1 = " insert into " + pub.yssGetTableName("Tb_HzJkMx") +"(FDate, FZqdm, FSzsh, FGddm, FJyxwh, " +
            " FBs, FCjsl, FCjjg, FCjje, FYhs, FJsf, FGhf, FZgf, FYj, FGzlx, Fhggain, FZqbz, Fywbz, " +
            " FSqbh, Fqtf, Zqdm, Ffxj, Findate, FTZBZ, FPortCode, FJYFS, FSqGzlx, FCreator, FCreateTime,FJKDM)" +
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = dbl.openPreparedStatement(strSql1); //将SHJSMX表中处理后的数据储存到交易接口明细表中
            
            for (int i = 0; i < arrPortCodes.length; i++) { //循环组合代码
                TSInfo = (String) hmPortHolderSeat.get(arrPortCodes[i]); //获取组合代码对应的席位代码以及股东代码
                //根据组合群代码和组合代码获取对应的读数处理方式参数数据
                readType = (ReadTypeBean) hmReadType.get(assetGroupCode + " " + arrPortCodes[i]);

                if (readType == null) {
                    throw new YssException("请在交易接口参数设置中设置已选组合的读数处理方式！");
                }
                FTZBZ = readType.getAssetClass(); //获取组合代码对应的资产标志参数

                if (FTZBZ.equals("01")) {
                    FTZBZ = "C"; //表示交易类金融资产类
                }
                if (FTZBZ.equals("02")) {
                    FTZBZ = "S"; //表示可供出售类金融资产类
                }
                if (FTZBZ.equals("03")) {
                    FTZBZ = "F"; //表示持有到期类金融资产类
                }
                
                subTSInfo = TSInfo.split("\t"); //拆分组合代码对应的股东代码数据和席位代码数据

                if (subTSInfo.length > 1) {
                    tradeSeats = subTSInfo[0]; //获取席位代码数据
                    stockHolders = subTSInfo[1]; //获取股东代码数据
                }
                
                strSql = " select * from SHJSMX a left join (select * from "+ pub.yssGetTableName("Tb_Para_TradeSeat") + 
                " where FCheckState = 1) b on a.XWDM = b.FSeatNum " + " where GDDM  in(" + operSql.sqlCodes(stockHolders) +
                ") and XWDM in(" + operSql.sqlCodes(tradeSeats) + ") and FDate = " + dbl.sqlDate(this.sDate);
                rs = dbl.openResultSet(strSql); //在SHJSMX表中查找相关股东和席位的数据
                
                while (rs.next()) {
                	zqdm = rs.getString("ZQDM") + " CG";
                	
                	if(rs.getString("ZQLB").equals("GZ")){
                		zqbz = "ZQ";
                	}else{
                		zqbz = "GP";
                	}

                	feeAttribute = new FeeAttributeBean();
                	
                    feeAttribute.setSecuritySign(zqbz); //设置证券标志
                    feeAttribute.setBusinessSign("KZZGP"); //设置业务标志
                    feeAttribute.setBs(rs.getString("BS")); //设置买卖标志
                    feeAttribute.setZqdm(zqdm); //设置转换后的证券代码
                    feeAttribute.setPortCode(arrPortCodes[i]); //设置组合代码
                    feeAttribute.setGsdm(rs.getString("XWDM")); //设置席位号
                    feeAttribute.setDate(rs.getDate("FDate")); //设置交易日期
                    feeAttribute.setReadType(readType); //设置读数处理方式参数数据
                    feeAttribute.setCjje(0); //设置成交金额
                    feeAttribute.setCjsl(rs.getDouble("CJSL")); //设置成交数量
                    feeAttribute.setCjjg(rs.getDouble("CJJG")); //设置成交价格
                    feeAttribute.setJyfs("PT");//设置交易方式
                    feeAttribute.setSeatCode(rs.getString("XWDM"));//设置交易席位代码
                    if(hmBrokerCode.isEmpty()){
                    	feeAttribute.setHmBrokerCode(getBrokerCode("GSDM", "SHJSMX", false, this.sDate,arrPortCodes[i]));
                    }
                    
                    calculateFee(feeAttribute); //计算费用
                	
                    if(feeAttribute.getSecuritySign().equalsIgnoreCase("ZQ")){
                    	isGzlx = super.judgeGzlx(feeAttribute);
                    }
                	
                    pstmt.setDate(1, YssFun.toSqlDate(rs.getDate("FDate"))); //交易日期  FDate
                    pstmt.setString(2, zqdm); //转换后的证券代码   FZqdm
                    pstmt.setString(3, "CG"); //交易所代码   FSzsh
                    pstmt.setString(4, rs.getString("GDDM"));//股东代码   FGddm
                    pstmt.setString(5, rs.getString("XWDM"));//席位代码   FJyxwh
                    pstmt.setString(6, rs.getString("BS")); //买卖标志   FBs
                    pstmt.setDouble(7, feeAttribute.getCjsl());//成交数量   FCjsl
                    pstmt.setDouble(8, feeAttribute.getCjjg());//成交价格   FCjjg
                    pstmt.setDouble(9, 0); //成交金额   FCjje
                    pstmt.setDouble(10, rs.getDouble("YHS")); //印花税   FYhs
                    pstmt.setDouble(11, rs.getDouble("JSF")); //经手费   FJsf
                    pstmt.setDouble(12, rs.getDouble("GHF")); //过户费   FGhf
                    pstmt.setDouble(13, rs.getDouble("ZGF")); //证管费   FZgf
                    pstmt.setDouble(14, 0); //佣金   FYj
                    if(isGzlx){
                    	pstmt.setDouble(15, feeAttribute.getFGzlx()); //税后的国债利息    FGzlx
                    }else{
                    	pstmt.setDouble(15, feeAttribute.getFBeforeGzlx()); //税前的国债利息    FGzlx
                    }
                    pstmt.setDouble(16, 0); //回购收益    Fhggain
                    pstmt.setString(17, zqbz); //证券标志    FZqbz
                    pstmt.setString(18, "KZZGP"); //业务标志    Fywbz
                    pstmt.setString(19, rs.getString("SQBH")); //申请编号    FSqbh
                    pstmt.setDouble(20, 0); //结算费    Fqtf
                    if(zqbz.equals("GP")){
                    	pstmt.setString(21,getZZGDM(rs.getString("SQBH")));
                    }else{
                    	pstmt.setString(21, rs.getString("ZQDM")); //转换前的证券代码    Zqdm
                    }
                    pstmt.setDouble(22, 0); //风险金    Ffxj
                    pstmt.setDate(23, YssFun.toSqlDate(rs.getDate("FDate"))); //系统读数日期    Findate
                    pstmt.setString(24, FTZBZ); //投资标志    FTZBZ
                    pstmt.setString(25, arrPortCodes[i]); //组合代码    FPortCode
                    pstmt.setString(26, "PT");// 交易方式   FJYFS
                    pstmt.setDouble(27, feeAttribute.getFBeforeGzlx()); //税前的国债利息    FSqGzlx
                    pstmt.setString(28, pub.getUserCode()); //用户代码    FCreator
                    pstmt.setString(29, YssFun.formatDatetime(new java.util.Date())); //创建日期    FCreateTime
                    pstmt.setString(30, "SHJSMX");//接口代码   FJKDM
                    
                    pstmt.addBatch();
                }
                
                dbl.closeResultSetFinal(rs);
                rs = null;
            }
            
            pstmt.executeBatch();
            dbl.closeStatementFinal(pstmt); //关闭pstmt
            
            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        }catch(Exception e){
        	throw new YssException("将上海结算明细表的数据处理到交易接口明细库出错！", e);
        }finally{
            dbl.endTransFinal(con, bTrans); //关闭连接
            dbl.closeStatementFinal(pstmt); //关闭pstmt
            dbl.closeResultSetFinal(rs); //关闭结果集
        }
	}
	
	/**
	 * add by songjie 2012.05.14 
	 * STORY #2599 QDV4赢时胜(上海开发部)2012年05月07日01_A
	 * @param sqbh
	 * @return
	 * @throws YssException
	 */
	private String getZZGDM(String sqbh) throws YssException{
		String zqdm = "";
		ResultSet rs = null;
		String strSql = "";
		try{
			strSql = " select * from SHJSMX where FDate = " + dbl.sqlDate(this.sDate) + " and ZQLB = 'GZ' and SQBH = " + 
			dbl.sqlString(sqbh) + " and CJJG = 0 ";
			rs = dbl.openResultSet(strSql);
			if(rs.next()){
				zqdm = rs.getString("ZQDM");
			}
			return zqdm;
		}catch(Exception e){
			throw new YssException("查找债转股债券数据出错！",e);
		}finally{
			
		}
	}
	
	public void calculateFee(FeeAttributeBean feeAttribute) throws YssException {
        double FJsf = 0; //初始化经手费
        double FZgf = 0; //初始化证管费
        double FYhs = 0; //初始化印花税
        double FGhf = 0; //初始化过户费
        double FYj = 0; //初始化佣金
        double Ffxj = 0; //初始化风险金
        double Fqtf = 0; //初始化结算费
        double Fhggain = 0; //初始化回购收益
        double FBeforeGzlx = 0; //初始化税前债券利息
        double FGzlx = 0; //初始化税后债券利息
        double startMoney = 0; //起点金额
        
        HashMap hmZQRate = null;// 用于储存债券的每百元债券利息
        HashMap hmPerZQRate = null;//用于储存债券利息表查询出的数据
        BondInterestBean bondInterest = null;
        String haveInfo = null;

        String securitySign = feeAttribute.getSecuritySign(); //获取证券标志
        String businessSign = feeAttribute.getBusinessSign(); //获取业务标志
        String zqdm = feeAttribute.getZqdm(); //获取证券代码
        String portCode = feeAttribute.getPortCode(); //获取组合代码
        String gsdm = feeAttribute.getGsdm(); //获取席位号
        String seatcode=feeAttribute.getSeatCode();//获取席位代码 add zhouxiang MS1299
        java.util.Date date = feeAttribute.getDate(); //获取交易日期
        //获取买卖标志 add by songjie 2009.12.21 MS00847 QDV4赢时胜（北京）2009年11月30日03_B
        String bs = feeAttribute.getBs(); 
        String tradeTypeCode = feeAttribute.getJyfs();//交易方式  ：大宗交易/普通交易

        double cjje = feeAttribute.getCjje(); //获取成交金额
        double cjjg = feeAttribute.getCjjg(); //获取成交价格
        double cjsl = feeAttribute.getCjsl(); //获取成交数量

        boolean jsfParamNum = false;//用于判断经手费的计算公式中是否包含国债利息参数
        boolean zgfParamNum = false;//用于判断证管费的计算公式中是否包含国债利息参数
        boolean yjParamNum = false;//用于判断佣金的计算公式中是否包含国债利息参数
        boolean fxjParamNum = false;//用于判断风险金的计算公式中是否包含国债利息参数

        CtlStock ctlStock = new CtlStock();
        ctlStock.setYssPub(pub);

        //获取数据接口参数设置的读数处理方式界面设置的参数
        ReadTypeBean readType = feeAttribute.getReadType();

        if (hmBrokerCode == null) {
            hmBrokerCode = feeAttribute.getHmBrokerCode();
        }
        if (hmFeeWay == null) {
            hmFeeWay = feeAttribute.getHmFeeWay();
        }
        if (hmRateSpeciesType == null) {
            hmRateSpeciesType = feeAttribute.getHmRateSpeciesType();
        }
        if (hmBrokerRate == null) {
            hmBrokerRate = feeAttribute.getHmBrokerRate();
        }
        if (hmExchangeBond == null) {
            hmExchangeBond = feeAttribute.getHmExchangeBond();
        }
        String brokerCode = (String) hmBrokerCode.get(seatcode);
        FeeWayBean feeWay = (FeeWayBean) hmFeeWay.get(pub.getAssetGroupCode() + " " +
            portCode + " " + brokerCode + " " + seatcode); //获取交易接口参数设置的费用承担方向分页的相关数据

        if(feeWay == null){
            throw new YssException("请在交易接口参数设置界面设置已选组合的费用承担参数！");
        }

        BrokerRateBean brokerRate = null;
        ExchangeBondBean exchangeBond = null;

        String brokerBear = feeWay.getBrokerBear(); //获取由券商承担的费用数据
        ArrayList alBears = shgh.splitBrokerBear(brokerBear); //拆分费用数据
        
        PublicMethodBean pmBean = new PublicMethodBean();
        pmBean.setYssPub(pub);
        
        try {
        	if (securitySign.equalsIgnoreCase("GP")) {
        		if (businessSign.equalsIgnoreCase("KZZGP")) {//可转债股票
                    if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                        FJsf = 0;
                        FZgf = 0;
                    }
                    FYhs = 0;
                    if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                          FGhf = 0; //如果业务标志为债转股，则过户费=0
                    }
        		}
        	}
        	
        	if (securitySign.equalsIgnoreCase("ZQ")) {
                //成交数量：过户库的cjsl×10
                if (feeAttribute.getSelectedFee() == null) {
                    cjsl = (int) (YssD.mul(cjsl, 10));
                }
                
                brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                        brokerCode + " 2 " + gsdm + " FI"); //获取上海债券对应组合代码和券商代码的佣金利率设置实例

                    if(brokerRate == null){
                        throw new YssException("请在券商佣金利率设置中设置上海债券的佣金费率数据！");
                    }

                    hmPerZQRate = super.getPerHundredZQRate(zqdm,date);//在债券利息表中查询债券利息数据

                    haveInfo = (String)hmPerZQRate.get("haveInfo");//判断债券利息表中是否有当前债券利息数据

                    if(haveInfo.equals("false")){ //表示债券利息表中没有当前债券利息数据
                        //计算债券的税前每百元债券利息，税后每百元债券利息并储存到哈希表中
                    	
                    	//修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数 
                    	//edit by songjie 2009.12.21 MS00847 QDV4赢时胜（北京）2009年11月30日03_B
                        hmZQRate = super.calculateZQRate(zqdm, date, bs, portCode);

                        //add by songjie 2010.03.22 QDII国内：MS00925 
                        //QDV4赢时胜（测试）2010年03月19日03_AB
                        if(((String)hmZQRate.get("haveInfo")).equals("false")){
                        	if(!alShowZqdm.contains(zqdm)){
                        		alShowZqdm.add(zqdm);
                        	}
                        }
                        //add by songjie 2010.03.22 QDII国内：MS00925 
                        //QDV4赢时胜（测试）2010年03月19日03_AB
                        
    					FBeforeGzlx = YssFun.roundIt(YssD.mul(Double.parseDouble((String) hmZQRate.get("SQGZLX")), cjsl), 2);
    					FGzlx = YssFun.roundIt(YssD.mul(Double.parseDouble((String) hmZQRate.get("GZLX")), cjsl), 2);

                        bondInterest = new BondInterestBean();//新建债券利息实例
                        bondInterest.setSecurityCode(zqdm);//设置证券代码
                        bondInterest.setIntAccPer100(new BigDecimal((String)hmZQRate.get("SQGZLX")));//设置税前百元利息
                        bondInterest.setSHIntAccPer100(new BigDecimal((String)hmZQRate.get("GZLX")));//设置税后百元利息

                        if(feeAttribute.getSelectedFee() == null){
                            if (!alZQCodes.contains(zqdm)) {
                                alZQCodes.add(zqdm);
                                alZQInfo.add(bondInterest);//将债券利息实例添加到列表中
                            }
                        }
                    }
                    else{
    					FGzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(Double.parseDouble(((String) hmPerZQRate
    									.get("PerGZLX"))), readType.getExchangePreci()), cjsl), 2);
    					FBeforeGzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(Double.parseDouble(((String) hmPerZQRate
    									.get("SHPerGZLX"))), readType.getExchangePreci()), cjsl), 2);
                    }  
        	}
        	
            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FJsf")) {
                feeAttribute.setFJsf(FJsf);
                return;
            }

            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FZgf")) {
                feeAttribute.setFZgf(FZgf);
                return;
            }

            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FGhf")) {
                feeAttribute.setFGhf(FGhf);
                return;
            }

            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYhs")) {
                feeAttribute.setFYhs(FYhs);
                return;
            }

            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("Fqtf")) {
                feeAttribute.setFqtf(Fqtf);
                return;
            }

            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj")) {
                feeAttribute.setFYj(FYj);
                return;
            }

            feeAttribute.setCjje(cjje); //设置成交金额
            feeAttribute.setCjsl(cjsl); //设置成交数量
            feeAttribute.setFJsf(FJsf); //设置经手费
            feeAttribute.setFZgf(FZgf); //设置证管费
            feeAttribute.setFYhs(FYhs); //设置印花税
            feeAttribute.setFGhf(FGhf); //设置过户费
            feeAttribute.setFYj(FYj); //设置佣金
            feeAttribute.setFfxj(Ffxj); //设置风险金
            feeAttribute.setFqtf(Fqtf); //设置结算费
            feeAttribute.setFhggain(Fhggain); //设置回购收益
            feeAttribute.setFBeforeGzlx(FBeforeGzlx); //设置税前债券利息
            feeAttribute.setFGzlx(FGzlx); //设置税后债券利息
        }catch (Exception e) {
            throw new YssException("计算费用出错", e);
        }
	}
	
	   /**
     * 将债券利息数据中没有的债券利息数据插入到表中
     * @throws YssException
     */
    public void insertIntoBondInterest() throws YssException {
        String strSql = "";//用于储存sql语句
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        PreparedStatement pstmt = null;//声明PreparedStatement
        BondInterestBean bondInterest = null;
        Iterator iterator = null;
        String zqdms = "";
        String zqdm = null;
        try{
            iterator = alZQCodes.iterator();//获取迭代器

            while(iterator.hasNext()){
                zqdm = (String)iterator.next();//获取证券代码
                zqdms += zqdm + ",";//拼接证券代码
            }

            if(zqdms.length() >= 1){
                zqdms = zqdms.substring(0, zqdms.length() - 1);//去掉字符串最后逗号
            }

            con.setAutoCommit(false); //设置手动提交事务
            bTrans = true;

            //先在债券利息表中删除需要插入到债券利息表中的债券数据
            strSql = " delete from " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " where FSecurityCode in(" + operSql.sqlCodes(zqdms) +
                //edit by songjie 2010.03.18 MS00920 QDV4赢时胜（测试）2010年03月18日06_B
                ") and FRecordDate = " + dbl.sqlDate(this.sDate);

            dbl.executeSql(strSql);

            //添加数据到债券利息表中
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_BondInterest") +
                "(FSecurityCode,FRecordDate,FCurCpnDate,FNextCpnDate,FIntAccPer100," +
                "FIntDay,FSHIntAccPer100,FDataSource,FCheckState,FCreator,FCreateTime)" +
                "values(?,?,?,?,?,?,?,?,?,?,?)";

            pstmt=dbl.openPreparedStatement(strSql);

            iterator = alZQInfo.iterator();
            while(iterator.hasNext()){
                bondInterest = (BondInterestBean)iterator.next();//获取债券利息实例

                pstmt.setString(1, bondInterest.getSecurityCode());//设置证券代码
                pstmt.setDate(2, YssFun.toSqlDate(this.sDate));//设置业务日期
                pstmt.setDate(3, YssFun.toSqlDate("9998-12-31"));
                pstmt.setDate(4, YssFun.toSqlDate("9998-12-31"));
                pstmt.setBigDecimal(5, bondInterest.getIntAccPer100());//设置税前百元利息
                pstmt.setInt(6, 0);
                pstmt.setBigDecimal(7, bondInterest.getSHIntAccPer100());//设置税后百元利息
                //edit by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
                //数据来源改为 "ZD－自动计算"
                pstmt.setString(8, "ZD");//表示是系统计算而得到的百元债券利息
                pstmt.setInt(9, 1);
                pstmt.setString(10, pub.getUserCode()); //创建人、修改人
                pstmt.setString(11, YssFun.formatDatetime(new java.util.Date())); //创建、修改时间

                pstmt.addBatch();
            }

            pstmt.executeBatch();

            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        }
        catch(Exception e){
            throw new YssException("将数据插入到债券利息表时出错！",e);
        }
        finally{
            dbl.closeStatementFinal(pstmt);
            dbl.endTransFinal(con, bTrans); //关闭连接
        }
    }
}
