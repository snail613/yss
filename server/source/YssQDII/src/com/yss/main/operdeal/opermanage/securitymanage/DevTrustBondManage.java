package com.yss.main.operdeal.opermanage.securitymanage;

import java.sql.*;
import java.util.*;

import com.yss.commeach.*;
import com.yss.dsub.*;
import com.yss.main.dao.ICostCalculate;
import com.yss.main.operdata.*;
import com.yss.main.parasetting.*;
import com.yss.pojo.cache.YssCost;
import com.yss.util.*;

/**
 *
 * <p>Title: 债券转托管业务处理</p>
 *
 * <p>Description: MS00022    国内债券业务    QDV4.1赢时胜（上海）2009年4月20日22_A</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable  2009.07.03 蒋锦 添加
 * @version 1.0
 */
public class DevTrustBondManage extends BaseBean{
    private String newSecurityCode = "";//转入证券代码
    private String portCode = "";
    private java.util.Date bargainDate;
    private boolean analy1;
    private boolean analy2;
    private boolean analy3;


    public java.util.Date getBargainDate() {
        return bargainDate;
    }

    public String getPortCode() {
        return portCode;
    }

    public boolean isAnaly1() {
        return analy1;
    }

    public boolean isAnaly2() {
        return analy2;
    }

    public boolean isAnaly3() {
        return analy3;
    }

    public void setBargainDate(java.util.Date bargainDate) {
        this.bargainDate = bargainDate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setAnaly1(boolean analy1) {
        this.analy1 = analy1;
    }

    public void setAnaly2(boolean analy2) {
        this.analy2 = analy2;
    }

    public void setAnaly3(boolean analy3) {
        this.analy3 = analy3;
    }

    public DevTrustBondManage(YssPub pub) {
        setYssPub(pub);
    }
    /**
     * 处理业务的入口方法 xuqiji 20100414
     * @param alRecPay
     * @param alIntegrated
     * @param alSecurity
     * @throws YssException
     */
    public void doManage(ArrayList alRecPay,ArrayList alIntegrated, ArrayList alSecurity) throws YssException{
    	try{
    		DevolveTrusteeshipBondDeal(alRecPay,alIntegrated,alSecurity);//处理债券转托管业务
    		BondCurrencyDeal(alRecPay,alIntegrated,alSecurity);//处理债券转流通业务
    	}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
    	
    }
    
    public void DevolveTrusteeshipBondDeal(ArrayList alRecPay,
                                           ArrayList alIntegrated,
                                           ArrayList alSecurity) throws YssException{
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "SELECT a.*, b.fcatcode, b.fsubcatcode, b.ftradecury,b.FEXCHANGECODE as FEXCHANGECODE" +
                " FROM " + pub.yssGetTableName("Tb_Data_DevTrustBond") + " a" +
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Security") + " b" +
                " ON a.FSecurityCode = b.FSecurityCode" +
                " WHERE a.FCheckState = 1" +
                " AND FPortCode IN (" + operSql.sqlCodes(portCode) + ")" +
                " AND FBARGAINDATE = " + dbl.sqlDate(bargainDate)+ " and FBondTradeType = " + dbl.sqlString("bond_trusteeship");
            rs = dbl.queryByPreparedStatement(strSql);
            while(rs.next()){
            	//modify by zhouwei 20120419 如果转入债券不为空，则生成新证券信息
            	if(rs.getString("FinSecurityCode")==null || rs.getString("FinSecurityCode").equals("")
            			|| rs.getString("FinSecurityCode").equals("null")){
                    getNewSecurityCode(alSecurity, rs);
            	}
                getIntegRateData(alIntegrated, rs);
                getRecPayData(alRecPay, rs);
            }
        } catch (Exception ex) {
            throw new YssException("处理债券转托管业务出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取转出证券代码
     * @param rs ResultSet
     * @throws YssException
     */
    private void getNewSecurityCode(ArrayList alSecurity,
                                    ResultSet rs) throws YssException{

        String sOutExchangCode = ""; //转出市场
        String sInExchangCode = ""; //转入市场
        String sSecurityCode = ""; //转出证券代码
        SecurityBean security = new SecurityBean();
        try {
            sOutExchangCode = rs.getString("FOutExchangeCode");
            sInExchangCode = rs.getString("FInExchangeCode");
            sSecurityCode = rs.getString("FSecurityCode");
            //----------- 转入证券的证券代码在界面上并没有录入，所以证券代码需要我们自己生成，
            //            先将转出证券代码的交易所后缀(CG、CS)删掉，再加上转入市场的交易所代码作为新的后缀，
            //            同时如果新生成的证券代码在证券信息表中并不存在则需要将新的证券代码添加如证券信息表中---------------//
            //---edit by songjie 2011.11.09 BUG 3042 QDV4赢时胜(测试)2011年11月01日02_B 若证券代码不足三位 则不截取 start---//
			if ((sOutExchangCode.endsWith("CG") ||
                sOutExchangCode.endsWith("CS") ||
                sOutExchangCode.endsWith("CY")) && sSecurityCode.length() > 3) {
            //---edit by songjie 2011.11.09 BUG 3042 QDV4赢时胜(测试)2011年11月01日02_B end---//
				sSecurityCode = sSecurityCode.substring(0, sSecurityCode.length() - 3);
            }
            sSecurityCode = sSecurityCode + " " + sInExchangCode;
            newSecurityCode = sSecurityCode;
            //------------------------------------------------------------------------------------------------------
            //------------获取转入证券的证券信息
            //先得到转出证券的证券信息
            security.setYssPub(pub);
            security.setStrSecurityCode(rs.getString("FSecurityCode"));
            security.getSetting();
            security.setExchangeCode(sInExchangCode);
            //将转出证券的证券代码改为新生成的转入证券的证券代码
            security.setStrOldSecurityCode(security.getStrSecurityCode());
            security.setStrSecurityCode(sSecurityCode);

            alSecurity.add(security);

        } catch (Exception ex) {
            throw new YssException("创建转出证券信息出错！", ex);
        }
    }

    private void getRecPayData(ArrayList alRecPay,
                               ResultSet rs) throws YssException{
        SecPecPayBean recPay = null;
        double baseCuryRate = 0;
        double portCuryRate = 0;
        try {
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
            baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FBARGAINDATE"),
                rs.getString("FTradeCury"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(bargainDate, rs.getString("FTradeCury"),
                                      rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();

            //---------------转出债券利息----------------//
            recPay = new SecPecPayBean();
            recPay.setInvestType(rs.getString("FInvestType"));
            recPay.setTransDate(rs.getDate("FBargainDate"));
            recPay.setCheckState(1);
            recPay.setAttrClsCode(rs.getString("FEXCHANGECODE").equals("CY")?" ":rs.getString("FAttrClsCode"));//MODIFY BY ZHOUWEI 20120424 银行间所属分类为空值
            recPay.setRelaNum(rs.getString("FNum"));
            recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_DEVTRUSTBOND);
            recPay.setStrPortCode(rs.getString("FPortCode"));
            if (this.analy1) {
                recPay.setInvMgrCode(rs.getString("FInvMgrCode"));
            } else {
                recPay.setInvMgrCode(" ");
            }
            recPay.setBrokerCode(" ");

            recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
            recPay.setStrCuryCode(rs.getString("FTradeCury"));
            recPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
            recPay.setStrSubTsfTypeCode("06FI_B");

            recPay.setInOutType(-1);
            recPay.setMoney(rs.getDouble("FBondIns"));

            recPay.setBaseCuryRate(baseCuryRate);
            recPay.setPortCuryRate(portCuryRate);

            recPay.setMMoney(recPay.getMoney());
            recPay.setVMoney(recPay.getMoney());

            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                recPay.getBaseCuryRate(), 2));
            recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
            recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());

            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                recPay.getBaseCuryRate(), recPay.getPortCuryRate(),
                rs.getString("FTradeCury"), bargainDate, recPay.getStrPortCode(), 2));

            recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
            recPay.setVPortCuryMoney(recPay.getPortCuryMoney());

            recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
            recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
            recPay.setMoneyF(recPay.getMoney());
            alRecPay.add(recPay);

            //-----------------------转入债券利息--------------------------//
            recPay = new SecPecPayBean();
            recPay.setInvestType(rs.getString("FInINVESTTYPE"));
            recPay.setTransDate(rs.getDate("FBargainDate"));
            recPay.setCheckState(1);
            recPay.setAttrClsCode(rs.getString("FInATTRCLSCODE"));//转入所属分类
            recPay.setRelaNum(rs.getString("FNum"));
            recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_DEVTRUSTBOND);
            recPay.setStrPortCode(rs.getString("FPortCode"));
            if (this.analy1) {
                recPay.setInvMgrCode(rs.getString("FInvMgrCode"));
            } else {
                recPay.setInvMgrCode(" ");
            }
            recPay.setBrokerCode(" ");

            recPay.setStrSecurityCode((rs.getString("FinSecurityCode")==null || rs.getString("FinSecurityCode").equals(""))?newSecurityCode:rs.getString("FinSecurityCode"));
            recPay.setStrCuryCode(rs.getString("FTradeCury"));
            recPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
            recPay.setStrSubTsfTypeCode("06FI_B");

            recPay.setInOutType(1);
            recPay.setMoney(rs.getDouble("FBondIns"));

            recPay.setBaseCuryRate(baseCuryRate);
            recPay.setPortCuryRate(portCuryRate);

            recPay.setMMoney(recPay.getMoney());
            recPay.setVMoney(recPay.getMoney());

            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                recPay.getBaseCuryRate(), 2));
            recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
            recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());

            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                recPay.getBaseCuryRate(), recPay.getPortCuryRate(),
                rs.getString("FTradeCury"), bargainDate, recPay.getStrPortCode(), 2));

            recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
            recPay.setVPortCuryMoney(recPay.getPortCuryMoney());

            recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
            recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
            recPay.setMoneyF(recPay.getMoney());
            alRecPay.add(recPay);

            //-----------------转出估值增值---------------//
            recPay = new SecPecPayBean();
            recPay.setInvestType(rs.getString("FInvestType"));
            recPay.setTransDate(rs.getDate("FBargainDate"));
            recPay.setCheckState(1);
            recPay.setAttrClsCode(rs.getString("FAttrClsCode"));
            recPay.setRelaNum(rs.getString("FNum"));
            recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_DEVTRUSTBOND);
            recPay.setStrPortCode(rs.getString("FPortCode"));
            if (this.analy1) {
                recPay.setInvMgrCode(rs.getString("FInvMgrCode"));
            } else {
                recPay.setInvMgrCode(" ");
            }
            recPay.setBrokerCode(" ");

            recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
            recPay.setStrCuryCode(rs.getString("FTradeCury"));
            recPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV);
            recPay.setStrSubTsfTypeCode("09FI");

            recPay.setInOutType(-1);
            recPay.setMoney(rs.getDouble("FApprec"));

            recPay.setBaseCuryRate(baseCuryRate);
            recPay.setPortCuryRate(portCuryRate);

            recPay.setMMoney(recPay.getMoney());
            recPay.setVMoney(recPay.getMoney());

            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                recPay.getBaseCuryRate(), 2));
            recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
            recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());

            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                recPay.getBaseCuryRate(), recPay.getPortCuryRate(),
                rs.getString("FTradeCury"), bargainDate, recPay.getStrPortCode(), 2));

            recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
            recPay.setVPortCuryMoney(recPay.getPortCuryMoney());

            recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
            recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
            recPay.setMoneyF(recPay.getMoney());
            alRecPay.add(recPay);

        } catch (Exception ex) {
            throw new YssException("处理应收应付数据出错！", ex);
        }
    }

    private void getIntegRateData(ArrayList alIntegrated,
                                  ResultSet rs) throws YssException{
        SecIntegratedBean inteCost = null;
        double baseCuryRate = 0;
        double portCuryRate = 0;
        try {
            baseCuryRate = rs.getDouble("FBaseCuryRate");
            portCuryRate = rs.getDouble("FPortCuryRate");
            //---------------转出成本----------------//
            inteCost = new SecIntegratedBean();
            inteCost.setInvestType(rs.getString("FInvestType"));
            inteCost.setIInOutType(1);
            inteCost.setSSecurityCode(rs.getString("FSecurityCode"));
            inteCost.setSExchangeDate(YssFun.formatDate(rs.getDate("FBARGAINDATE"),
                "yyyy-MM-dd"));
            inteCost.setSOperDate(YssFun.formatDate(rs.getDate("FBARGAINDATE"),
                "yyyy-MM-dd"));
            inteCost.setSRelaNum(" ");
            inteCost.setSNumType("securitymanage"); //------ modify by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B 

            inteCost.setSTradeTypeCode(YssOperCons.YSS_JYLX_ZQZTG);

            inteCost.setSPortCode(rs.getString("FPortCode"));
            if (analy1) {
                inteCost.setSAnalysisCode1(rs.getString("FInvMgrCode"));
            } else {
                inteCost.setSAnalysisCode1(" ");
            }
            inteCost.setSAnalysisCode2(" ");

            inteCost.setSAnalysisCode3(" ");

            inteCost.setDAmount(YssD.mul(rs.getDouble("FAmount"), -1));

            inteCost.setDBaseCuryRate(baseCuryRate);
            inteCost.setDPortCuryRate(portCuryRate);

            inteCost.setDCost(YssD.mul(rs.getDouble("FMoney"), -1));

            inteCost.setDBaseCost(this.getSettingOper().calBaseMoney(inteCost.getDCost(),
                baseCuryRate, 2));
            inteCost.setDPortCost(this.getSettingOper().calPortMoney(inteCost.getDCost(),
                baseCuryRate, portCuryRate,
                rs.getString("FTradeCury"), bargainDate, inteCost.getSPortCode(), 2));

            inteCost.setDMCost(inteCost.getDCost());
            inteCost.setDVCost(inteCost.getDCost());

            inteCost.setDMBaseCost(inteCost.getDBaseCost());
            inteCost.setDVBaseCost(inteCost.getDBaseCost());

            inteCost.setDMPortCost(inteCost.getDPortCost());
            inteCost.setDVPortCost(inteCost.getDPortCost());

            inteCost.checkStateId = 1;
            inteCost.setSTsfTypeCode("05");
            inteCost.setSSubTsfTypeCode("05FI");

            inteCost.setAttrClsCode(rs.getString("FEXCHANGECODE").equals("CY")?" ":rs.getString("FAttrClsCode"));//MODIFY BY ZHOUWEI 银行间所属分类为空值

            alIntegrated.add(inteCost);

            //---------------转入成本----------------//
            inteCost = new SecIntegratedBean();
            inteCost.setIInOutType(1);
            inteCost.setInvestType(rs.getString("FInINVESTTYPE"));//modifyby zhouwei 20120424 转入投资类型
            

            inteCost.setSSecurityCode((rs.getString("FinSecurityCode")==null || rs.getString("FinSecurityCode").equals(""))?newSecurityCode:rs.getString("FinSecurityCode"));
            inteCost.setSExchangeDate(YssFun.formatDate(rs.getDate("FBARGAINDATE"),
                "yyyy-MM-dd"));
            inteCost.setSOperDate(YssFun.formatDate(rs.getDate("FBARGAINDATE"),
                "yyyy-MM-dd"));
            inteCost.setSRelaNum(" ");
            inteCost.setSNumType("securitymanage");//------ modify by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B 

            inteCost.setSTradeTypeCode(YssOperCons.YSS_JYLX_ZQZTG);

            inteCost.setSPortCode(rs.getString("FPortCode"));
            if (analy1) {
                inteCost.setSAnalysisCode1(rs.getString("FInvMgrCode"));
            } else {
                inteCost.setSAnalysisCode1(" ");
            }
            inteCost.setSAnalysisCode2(" ");

            inteCost.setSAnalysisCode3(" ");

            inteCost.setDAmount(rs.getDouble("FAmount"));

            baseCuryRate = rs.getDouble("FBaseCuryRate");
            portCuryRate = rs.getDouble("FPortCuryRate");
            inteCost.setDBaseCuryRate(baseCuryRate);
            inteCost.setDPortCuryRate(portCuryRate);

            inteCost.setDCost(rs.getDouble("FMoney"));

            inteCost.setDBaseCost(this.getSettingOper().calBaseMoney(inteCost.getDCost(),
                baseCuryRate, 2));
            inteCost.setDPortCost(this.getSettingOper().calPortMoney(inteCost.getDCost(),
                baseCuryRate, portCuryRate,
                rs.getString("FTradeCury"), bargainDate, inteCost.getSPortCode(), 2));

            inteCost.setDMCost(inteCost.getDCost());
            inteCost.setDVCost(inteCost.getDCost());

            inteCost.setDMBaseCost(inteCost.getDBaseCost());
            inteCost.setDVBaseCost(inteCost.getDBaseCost());

            inteCost.setDMPortCost(inteCost.getDPortCost());
            inteCost.setDVPortCost(inteCost.getDPortCost());

            inteCost.checkStateId = 1;
            inteCost.setSTsfTypeCode("05");
            inteCost.setSSubTsfTypeCode("05FI");

            inteCost.setAttrClsCode(rs.getString("FInATTRCLSCODE"));//MODIFY BY ZHOUWEI 转入银行间所属分类

            alIntegrated.add(inteCost);
        } catch (Exception ex) {
            throw new YssException("处理证券变动数据出错！", ex);
        }
    }
    /**
     * 处理债券转流通业务 xuqiji 20100414
     * @param alRecPay
     * @param alIntegrated
     * @param alSecurity
     * @throws YssException
     */
    public void BondCurrencyDeal(ArrayList alRecPay,ArrayList alIntegrated,ArrayList alSecurity) throws YssException {
		ResultSet rs = null;
		StringBuffer bufSql = new StringBuffer();
		try {
			bufSql.append(" SELECT * from ").append(pub.yssGetTableName("Tb_Data_Devtrustbond")).append(" a ");
			bufSql.append(" join (select FSecurityCode as securityCode,FCatCode,FSubCatCode,FTradeCury from ");
			bufSql.append(pub.yssGetTableName("tb_para_security"));
			bufSql.append(" where FCheckState = 1 and FCatCode = 'FI') b on a.fsecuritycode = b.securitycode");
			bufSql.append(" WHERE a.FCheckState = 1 AND a.FBargaindate =").append(dbl.sqlDate(bargainDate));
			bufSql.append(" and FPortCode IN (").append(operSql.sqlCodes(portCode)).append(")");
			bufSql.append(" and a.FBondTradeType = 'bond_circulation'");

			rs = dbl.queryByPreparedStatement(bufSql.toString());
			while (rs.next()) {
				 //新股流通
				issueDeal(alIntegrated, alRecPay, rs);
			}
		} catch (Exception ex) {
			throw new YssException("处理债券转流通业务出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    private void issueDeal(ArrayList alIntegrated, ArrayList alRecPay,
			ResultSet rs) throws YssException {
		SecIntegratedBean secIntegrate = null;
		SecPecPayBean recPay = null;
		String sCatCode = "";// 证券品种
		double baseCuryRate = 0;
		double portCuryRate = 0;
		try {
			sCatCode = rs.getString("FCatCode");
			ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean("avgcostcalculate");

			costCal.initCostCalcutate(rs.getDate("FBargaindate"), rs.getString("FPortCode"), 
					rs.getString("FInvMgrCode"), "",
					rs.getString("FAttrClsCode"));
			costCal.setYssPub(pub);
			// 获取冲减的成本
			YssCost cost = costCal.getCarryCost(rs.getString("FSecurityCode"),
					rs.getDouble("FAmount"), rs.getString("FNum"), null,"newissue",//null参数：add by guolongchao 20110815  STORY #1207  添加结算日期参数
					YssOperCons.YSS_JYLX_XZLT);
			costCal.roundCost(cost, 2);
			EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
			rateOper.setYssPub(pub);

			baseCuryRate = this.getSettingOper().getCuryRate(
					rs.getDate("FBargaindate"), rs.getString("FTradeCury"),
					rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

			rateOper.getInnerPortRate(bargainDate, rs.getString("FTradeCury"),
					rs.getString("FPortCode"));
			portCuryRate = rateOper.getDPortRate();

			// -------------冲减的成本-------------//
			secIntegrate = new SecIntegratedBean();

			secIntegrate.setIInOutType(-1);
			secIntegrate.setInvestType(rs.getString("FInvestTYpe"));
			secIntegrate.setSExchangeDate(YssFun.formatDate(rs.getDate("FBargaindate"), "yyyy-MM-dd"));
			secIntegrate.setSOperDate(YssFun.formatDate(rs.getDate("FBargaindate"), "yyyy-MM-dd"));
			secIntegrate.setSRelaNum(" ");
			secIntegrate.setSNumType("securitymanage"); //------ modify by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B 

			secIntegrate.setSTradeTypeCode(YssOperCons.YSS_JYLX_XZLT);//新债流通

			secIntegrate.setSPortCode(rs.getString("FPortCode"));
			if (this.analy1) {
				secIntegrate.setSAnalysisCode1(rs.getString("FInvMgrCode"));
			} else {
				secIntegrate.setSAnalysisCode1(" ");
			}
			secIntegrate.setSAnalysisCode2(" ");
			secIntegrate.setSAnalysisCode3(" ");

			secIntegrate.setDAmount(YssD.mul(rs.getDouble("FAmount"),secIntegrate.getIInOutType()));
			secIntegrate.setSSecurityCode(rs.getString("FSecurityCode"));

			secIntegrate.setDCost(YssD.mul(cost.getCost(), secIntegrate.getIInOutType()));
			secIntegrate.setDMCost(YssD.mul(cost.getCost(), secIntegrate.getIInOutType()));
			secIntegrate.setDVCost(YssD.mul(cost.getCost(), secIntegrate.getIInOutType()));

			secIntegrate.setDBaseCost(YssD.mul(cost.getBaseCost(), secIntegrate.getIInOutType()));
			secIntegrate.setDMBaseCost(YssD.mul(cost.getBaseCost(),secIntegrate.getIInOutType()));
			secIntegrate.setDVBaseCost(YssD.mul(cost.getBaseCost(),secIntegrate.getIInOutType()));

			secIntegrate.setDPortCost(YssD.mul(cost.getPortCost(), secIntegrate.getIInOutType()));
			secIntegrate.setDMPortCost(YssD.mul(cost.getPortCost(),secIntegrate.getIInOutType()));
			secIntegrate.setDVPortCost(YssD.mul(cost.getPortCost(),secIntegrate.getIInOutType()));

			secIntegrate.setDPortCuryRate(portCuryRate);

			secIntegrate.setDBaseCuryRate(baseCuryRate);

			secIntegrate.checkStateId = 1;
			secIntegrate.setSTsfTypeCode("05");
			secIntegrate.setSSubTsfTypeCode("05" + rs.getString("FCatCode"));
			secIntegrate.setAttrClsCode(rs.getString("FAttrClsCode"));
			alIntegrated.add(secIntegrate);

			// --------------转到流通的证券变动---------------//
			secIntegrate = new SecIntegratedBean();

			secIntegrate.setIInOutType(1);
			secIntegrate.setInvestType(rs.getString("FInvestType"));
			secIntegrate.setSExchangeDate(YssFun.formatDate(rs.getDate("FBargaindate"), "yyyy-MM-dd"));
			secIntegrate.setSOperDate(YssFun.formatDate(rs.getDate("FBargaindate"), "yyyy-MM-dd"));
			secIntegrate.setSRelaNum(" ");
			secIntegrate.setSNumType("securitymanage"); //------ modify by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B 

			secIntegrate.setSTradeTypeCode(YssOperCons.YSS_JYLX_XZLT);//新债流通

			secIntegrate.setSPortCode(rs.getString("FPortCode"));
			if (this.analy1) {
				secIntegrate.setSAnalysisCode1(rs.getString("FInvMgrCode"));
			} else {
				secIntegrate.setSAnalysisCode1(" ");
			}
			secIntegrate.setSAnalysisCode2(" ");
			secIntegrate.setSAnalysisCode3(" ");

			secIntegrate.setDAmount(rs.getDouble("FAmount"));
			secIntegrate.setSSecurityCode(rs.getString("FSecurityCode"));

			secIntegrate.setDCost(rs.getDouble("FMoney"));
			secIntegrate.setDMCost(secIntegrate.getDCost());
			secIntegrate.setDVCost(secIntegrate.getDCost());

			secIntegrate.setDBaseCost(this.getSettingOper().calBaseMoney(secIntegrate.getDCost(), baseCuryRate, 2));
			secIntegrate.setDMBaseCost(secIntegrate.getDBaseCost());
			secIntegrate.setDVBaseCost(secIntegrate.getDBaseCost());

			secIntegrate.setDPortCost(this.getSettingOper().calPortMoney(
					secIntegrate.getDCost(), baseCuryRate, portCuryRate,
					rs.getString("FTradeCury"), bargainDate,
					secIntegrate.getSPortCode(), 2));
			secIntegrate.setDMPortCost(secIntegrate.getDPortCost());
			secIntegrate.setDVPortCost(secIntegrate.getDPortCost());

			secIntegrate.setDPortCuryRate(portCuryRate);

			secIntegrate.setDBaseCuryRate(baseCuryRate);

			secIntegrate.checkStateId = 1;
			secIntegrate.setSTsfTypeCode("05");
			secIntegrate.setSSubTsfTypeCode("05" + rs.getString("FCatCode"));
			secIntegrate.setAttrClsCode(YssOperCons.YSS_SXFL_PONS); // 转到公开发行新股
			alIntegrated.add(secIntegrate);

			// 如果证券品种是债券则需要将利息转到公开发行
			if (sCatCode.equalsIgnoreCase("FI")) {
				// 冲减的债券利息
				SecPecPayBean carryPay = costCal.getCarryRecPay(rs
						.getString("FSecurityCode"), rs.getDouble("FAmount"),
						rs.getString("FNum"), "newissue",
						YssOperCons.YSS_JYLX_XGLT, YssOperCons.YSS_ZJDBLX_Rec,
						YssOperCons.YSS_ZJDBLX_Rec + rs.getString("FCatCode"));

				// ----------冲减的利息-----------//
				if (carryPay == null) {
					return;
				}
				recPay = new SecPecPayBean();
				recPay.setTransDate(rs.getDate("FBargaindate"));
				recPay.setInvestType(rs.getString("FInvestType"));
				recPay.setCheckState(1);
				recPay.setInOutType(-1);
				recPay.setAttrClsCode(rs.getString("FAttrClsCode"));
				recPay.setRelaNum(rs.getString("FNum"));
				recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE);
				recPay.setStrPortCode(rs.getString("FPortCode"));
				if (this.analy1) {
					recPay.setInvMgrCode(rs.getString("FInvMgrCode"));
				} else {
					recPay.setInvMgrCode(" ");
				}
				recPay.setBrokerCode(" ");
				recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
				recPay.setStrCuryCode(rs.getString("FTradeCury"));
				recPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
				recPay.setStrSubTsfTypeCode("06FI_B");

				recPay.setMoney(carryPay.getMoney());
				recPay.setBaseCuryRate(baseCuryRate);
				recPay.setPortCuryRate(portCuryRate);

				recPay.setMMoney(carryPay.getMMoney());
				recPay.setVMoney(carryPay.getVMoney());

				recPay.setBaseCuryMoney(carryPay.getBaseCuryMoney());
				recPay.setMBaseCuryMoney(carryPay.getMBaseCuryMoney());
				recPay.setVBaseCuryMoney(carryPay.getVBaseCuryMoney());

				recPay.setPortCuryMoney(carryPay.getPortCuryMoney());
				recPay.setMPortCuryMoney(carryPay.getMPortCuryMoney());
				recPay.setVPortCuryMoney(carryPay.getVPortCuryMoney());

				recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
				recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
				recPay.setMoneyF(recPay.getMoney());
				alRecPay.add(recPay);

				// ----------转到流通的利息-----------//
				recPay = new SecPecPayBean();
				recPay.setTransDate(rs.getDate("FBargaindate"));
				recPay.setInvestType(rs.getString("FInvestType"));
				recPay.setCheckState(1);
				recPay.setInOutType(1);
				recPay.setAttrClsCode(YssOperCons.YSS_SXFL_PONS);
				recPay.setRelaNum(rs.getString("FNum"));
				recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE);
				recPay.setStrPortCode(rs.getString("FPortCode"));
				if (this.analy1) {
					recPay.setInvMgrCode(rs.getString("FInvMgrCode"));
				} else {
					recPay.setInvMgrCode(" ");
				}
				recPay.setBrokerCode(" ");
				recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
				recPay.setStrCuryCode(rs.getString("FTradeCury"));
				recPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
				recPay.setStrSubTsfTypeCode("06FI_B");

				recPay.setMoney(carryPay.getMoney());
				recPay.setBaseCuryRate(baseCuryRate);
				recPay.setPortCuryRate(portCuryRate);

				recPay.setMMoney(carryPay.getMMoney());
				recPay.setVMoney(carryPay.getVMoney());

				recPay.setBaseCuryMoney(carryPay.getBaseCuryMoney());
				recPay.setMBaseCuryMoney(carryPay.getMBaseCuryMoney());
				recPay.setVBaseCuryMoney(carryPay.getVBaseCuryMoney());

				recPay.setPortCuryMoney(carryPay.getPortCuryMoney());
				recPay.setMPortCuryMoney(carryPay.getMPortCuryMoney());
				recPay.setVPortCuryMoney(carryPay.getVPortCuryMoney());

				recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
				recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
				recPay.setMoneyF(recPay.getMoney());
				alRecPay.add(recPay);
			}
		} catch (Exception ex) {
			throw new YssException("处理流通业务出错！", ex);
		}
	}

}
