package com.yss.main.operdeal.report;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.parasetting.*;
import com.yss.main.report.*;
import com.yss.pojo.cache.*;
import com.yss.pojo.sys.*;
import com.yss.util.*;
import com.yss.vsub.*;

public class BaseBuildValuationRep
    extends BaseBean implements IBuildReport {
    private HashMap hmFieldSec;
    private HashMap hmFieldCash;
    private HashMap hmFieldInvest;
    private HashMap hmTypeNameSec;
    private HashMap hmTypeNameCash;
    private String valSecDefine;
    private String valCashDefine;
    private String valInvestDefine;
    private java.util.Date dDate;
    private String portCode;

    private String invMgrCode; //chenyibo  20071003

    private ValuationRepBean valReport;
    private double dBaseTotal;
    private double dPortTotal;
    private double dBaseUseCashTotal = 0;   //基础货币可用头寸
    private double dPortUseCashTotal = 0;   //组合货币可用头寸
    private double dBaseIncMv = 0;          //基础货币估值增值
    private double dPortIncMv = 0;          //组合货币估值增值
    private double dBaseExFx = 0;           //基础货币汇兑损益
    private double dPortExFx = 0;           //组合货币汇兑损益
    private double dBaseUseCash = 0;        //基础货币可用头寸
    private double dPortUseCash = 0;        //组合货币可用头寸

    private double dAmount = 0;
    private boolean bSaveNet;

    private boolean sInvMgrGroup = false; //sInvMgrGroup 为 false:出报表时。

    //sInvMgrGroup 为 true:报表出完后，要以投资经理为组来计算净值，
    //并且保存到净值表中。则将sInvMgrGroup设为true。
    private HashMap hmNetValue; //存储每个投资经理的资产净值
    private double dBaseNetValue = 0;
    private double dPortNetValue = 0;
    double dPortRate = 1;
    public String getPortCode() {
        return portCode;
    }

    public void setDDate(java.util.Date dDate) {
        this.dDate = dDate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public java.util.Date getDDate() {
        return dDate;
    }

    public BaseBuildValuationRep() {
    }

    /**
     * initBuildReport
     *
     * @param bean BaseBean
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        setRelaMap();
        valReport = (ValuationRepBean) bean;
        dDate = valReport.getDDate();
        portCode = valReport.getPortCode();
        invMgrCode = valReport.getInvMgrCode(); //chenyibo 20071003
        valSecDefine = valReport.getValSecDefine();
        valCashDefine = valReport.getValCashDefine();
        valInvestDefine = valReport.getValInvestDefine();
        PortfolioBean port = new PortfolioBean();
        port.setYssPub(pub);
        port.setPortCode(portCode);
        port.getSetting();
    }

    /**
     * buildReport
     *
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        setSecClassTable();
        setCashClassTable();
        setInvestClassTable();
        StringBuffer buf = new StringBuffer();
        dBaseTotal = 0;
        dPortTotal = 0;
        buf.append(buildSecRepData());
        buf.append(buildCashRepData());
        buf.append(buildInvestRepData());
        buf.append(buildTotalValueRow());
        buf.append(buildUseCash());

        YssOperFun fun = new YssOperFun(pub);
        buf.append(buildTotalAmountRow());
        
        System.out.println(buf);
        
        if (this.dAmount != 0) { //如果数量不为零 就算单位净值
            buf.append(buildUnitValueRow());
        }
        
        System.out.println(buf);
        
        buf.append(buildTotalInvsertNavValue());
        //this.saveReport("");//由新净值统计生成资产净值数据。在这里不再生成。sj edit 20080722
        buf.append(buildTotalValRow()); // sj  估值增值
        buf.append(buildTotalExchangeRow()); //sj  汇兑损益
        buf.append(buildTotalUnprofitRow()); //损益平准金（未实现）
        buf.append(buildTotalprofitRow()); //损益平准金（已实现）
        
        System.out.println(buf);
        
        return buf.toString();
    }

    protected String buildUnitValueRow() throws YssException {
        String sResult = "";
        int iDigit = 0; //保留的位数,增加对单位净值保留小数位数的处理 by liyu 080527
        valReport.setDefaultParam();
        valReport.setExtendNum(1);
        valReport.setKeyCode("单位净值：");
        CtlPubPara ctlPara = new CtlPubPara();
        ctlPara.setYssPub(pub);
        iDigit = Integer.parseInt(ctlPara.getCashUnit(valReport.getPortCode()));
        valReport.setHoldDigit(iDigit);		//add by huangqirong 2012-03-01 story #2088 保留位数
        valReport.setBaseMValue(YssD.round(YssD.div(this.dBaseTotal, dAmount),
                                           iDigit));
        valReport.setPortMValue(YssD.round(YssD.div(this.dPortTotal, dAmount),
                                           iDigit));
        sResult = valReport.buildTotalValueRowStr1();	//modify by huangqirong 2012-03-01 story #2088 保留位数
        sResult = sResult + "\r\n";
        return sResult;
    }

    private String strFromat = "ExtendNum	PlusMark	KeyCode	KeyName	Space	Space Space SParAmt	Space	Space	Space	Space	Space	Space	Space	Space	Space	Space	Space	Space	CuryCode	BaseMValue;#,##0.0000	Space	Space	Space	Space	Space	Space	CuryCode	PortMValue;#,##0.0000	Space	Space	Space	Space";

    protected String buildTotalTotalFeeRow() throws YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        double dBaseTotalFee = 0;
        double dPortTotalFee = 0;
        double dBaseRate = 0;
        double dPortRate = 0;
        valReport.setDefaultParam();
        valReport.setExtendNum(1);
        valReport.setKeyCode("交易费用");
        valReport.setCuryCode("合计：");

        try {
            strSql =
                "select sum(a.FTradeFee1 + a.FTradeFee2 + a.FTradeFee3 + a.FTradeFee4 + " +
                " a.FTradeFee5 + a.FTradeFee6 + a.FTradeFee7 + a.FTradeFee8) as FSumFee, b.FTradeCury from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " a left join (select * from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") b on a.FSecurityCode = b.FSecurityCode" +
                " group by b.FTradeCury";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dBaseRate = this.getSettingOper().getCuryRate(this.dDate,
                    rs.getString("FTradeCury"), this.portCode,
                    YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(this.dDate,
                    rs.getString("FTradeCury"), this.portCode,
                    YssOperCons.YSS_RATE_PORT);
                dBaseTotalFee +=
                    this.getSettingOper().calBaseMoney(rs.getDouble("FSumFee"),
                    dBaseRate);
                //增加判断是否用直接方式来计算组合货币成本 2008-11-24 linjunyun bug:Ms00011
                //增加参数sCuryCode，dDate，sPortCode
                dPortTotalFee +=
                    this.getSettingOper().calPortMoney(rs.getDouble("FSumFee"),
                    dBaseRate, dPortRate, valReport.getCuryCode(), this.dDate, this.portCode);
            }
            dBaseTotal = YssD.sub(dBaseTotal, dBaseTotalFee);
            dPortTotal = YssD.sub(dPortTotal, dPortTotalFee);
            sResult = valReport.buildTotalValueRowStr();
            sResult = sResult + "\r\n";
        } catch (Exception e) {
            throw new YssException(e);
        }finally{
        	dbl.closeResultSetFinal(rs);//关闭结果集 by leeyu 20100903
        }
        return sResult;
    }

    protected String buildUseCash() throws YssException {
        String sResult = "";
        valReport.setDefaultParam();
        valReport.setExtendNum(1);
        valReport.setKeyCode("可用头寸");
        valReport.setCuryCode("合计：");
        valReport.setBaseMValue(dBaseUseCashTotal);
        valReport.setPortMValue(dPortUseCashTotal);
        sResult = valReport.buildTotalValueRowStr();
        sResult = sResult + "\r\n";
        return sResult;
    }

    protected String buildTotalAmountRow() throws YssException {
        String sResult = "";
        valReport.setDefaultParam();
        valReport.setExtendNum(1);
        valReport.setKeyCode("实收资本");
        valReport.setCuryCode("合计：");

        ResultSet rs = null;
        try {
            //彭彪20071025 取最后一天TA库存的资本数量
            String strSql = "select * from " + pub.yssGetTableName("Tb_Stock_TA")
                + " where FStorageDate= " + dbl.sqlDate(dDate) +
                " and FPortCode=" + dbl.sqlString(portCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dAmount = rs.getDouble("FStorageAmount"); //得到数量保存起来
                valReport.setSParAmt(YssFun.formatNumber(dAmount, "#,##0.#####"));
                //2008.05.21 蒋锦 修改
                valReport.setBaseMValue(this.getSettingOper().calBaseMoney(rs.
                    getDouble("FStorageAmount"), rs.getDouble("FBaseCuryRate")));
                valReport.setPortMValue(rs.getDouble("FStorageAmount"));
            }
            sResult = valReport.buildTotalValueRowStr();
            sResult = sResult + "\r\n";
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取实收资本数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //显示投资经理净值
    protected String buildTotalInvsertNavValue() throws YssException {
        String sResult = "";
        ResultSet rs = null;
        Iterator iter = null;
        YssNetValue netValue = null;
        InvestManagerBean ivtBean = new InvestManagerBean();
        boolean bFlag = false;
        try {
            ivtBean.setYssPub(pub);
            countNetValue();
            if (hmNetValue.size() > 0) {
                //-----------判断净值是否区分了投资经理
                iter = hmNetValue.values().iterator();
                if (iter.hasNext()) {
                    netValue = (YssNetValue) iter.next();
                    if (netValue.getInvMgrCode().trim().length() > 0) {
                        bFlag = true;
                    }
                }
                //-----------------------------------------------------------
                if (bFlag) {
                    iter = hmNetValue.values().iterator();
                    while (iter.hasNext()) {
                        netValue = (YssNetValue) iter.next();
                        ivtBean.setStrCode(netValue.getInvMgrCode());
                        ivtBean.getSetting();
                        valReport.setDefaultParam();
                        valReport.setExtendNum(1);
                        valReport.setKeyCode(ivtBean.getStrName() + "的净值");
                        valReport.setCuryCode("合计：");
                        valReport.setBaseMValue(netValue.getBaseNetValue());
                        valReport.setPortMValue(netValue.getPortNetValue());
                        sResult = sResult + valReport.buildTotalValueRowStr();
                        sResult = sResult + "\r\n";
                    }
                }
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取实收资本数据出错");
        }

        finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String buildTotalValueRow() throws YssException {
        String sResult = "";
        valReport.setDefaultParam();
        valReport.setExtendNum(1);
        valReport.setKeyCode("资产净值");
        valReport.setCuryCode("合计：");
        valReport.setBaseMValue(dBaseTotal);
        valReport.setPortMValue(dPortTotal);
        sResult = valReport.buildTotalValueRowStr();
        sResult = sResult + "\r\n";
        return sResult;
    }

    protected String buildFeeRecRepData() throws YssException {
        return "";
    }

    protected String buildSecRepData() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        ResultSet rsSub = null;
        String[] valSecDefineAry = null;
        String sWhereSql = "";
        StringBuffer buf = new StringBuffer();
        String SumString = null;
        String DetailString = null;
        try {
            valSecDefineAry = valSecDefine.split(";");
            strSql = "select * from tb_temp_gzClsSec_" + pub.getUserCode() +
                " order by FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (valSecDefineAry.length >=
                    rs.getString("FCode").split("\f").length) {
                    SumString = buildSecRepSumRowData(valSecDefineAry,
                        rs.getString("FCode")); //为了防止有空记录
                    if (SumString.length() > 0) {
                        buf.append(SumString).append(
                            "\r\n");
                    }
                }
                if (valSecDefineAry.length ==
                    rs.getString("FCode").split("\f").length) {
                    DetailString = buildSecRepDetailRowData(valSecDefineAry,
                        rs.getString("FCode"));
                    if (DetailString.length() > 0) {
                        buf.append(DetailString);
                    }
                }
            }
            sResult = buf.toString() + "\r\n";
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取证券估值数据出错");
        }finally{
        	dbl.closeResultSetFinal(rs);//关闭结果集 add by leeyu 20100903
        }
    }

    protected String buildSecRepSumRowData(String[] defineAry, String sValue) throws
        YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        String sWhereSql = "";
        String sWhereSql2 = "";
        String sWhereSql3 = "";
        String[] sValueAry = null;
        try {
            sWhereSql = builderSecWhereSql(defineAry, sValue);
            sWhereSql2 = builderSecWhereSql2(defineAry, sValue);
            sWhereSql3 = builderSecWhereSql3(defineAry, sValue); //sj add 为了用attrCls这一新的级别，与库存的级别分开。20071205
            sValueAry = sValue.split("\f");
            strSql = "select a.* from (" +
                " select a1.*, a2.*,a3.*,a4.*,a5.*,a6.*,a7.* from (select FPortCode,sum(FStorageAmount) as FStorageAmount," +
                " sum(case when FSubCatCode = 'FU01' or FCatCode = 'FW' or FSubCatCode = 'OP02' then 0 else FVStorageCost end) as FVStorageCost," +
                " sum(case when FSubCatCode = 'FU01' or FCatCode = 'FW' or FSubCatCode = 'OP02' then 0 else FVBaseCuryCost end) as FVBaseCuryCost," +
                " sum(case when FSubCatCode = 'FU01' or FCatCode = 'FW' or FSubCatCode = 'OP02' then 0 else FVPortCuryCost end) as FVPortCuryCost from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " a11 join (select FSecurityCode,FSecurityName,FCatCode,FSubCatCode from " +
                //pub.yssGetTableName("Tb_Para_Security") +
                pub.yssGetTableName("Tb_Para_Security") + " a121 "+//添加别名 合并太平版本调整 by leeyu 20100824
                //sWhereSql3 + " and FCheckState = 1) a12" + // sj sWhereSql replace to sWhereSql3
                //合并太平版本调整，by leeyu 20100824 SQL脚本优化
                sWhereSql3 +
                " and FStartDate=(select max(FStartDate) as FStartDate from "+
                pub.yssGetTableName("Tb_Para_Security") +" a122 where a121.FSecurityCode= a122.FSecurityCode )"+
                " and FCheckState = 1) a12" +
                " on a11.FSecurityCode = a12.FSecurityCode" +
                /* 将此句放在 a12视图里面处理，可提高SQL查询效率 by leeyu 20100824 合并太平版本调整 
                 " join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 group by FSecurityCode) r on a12.FSecurityCode = r.FSecurityCode" +
                */
                " where FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysisCode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a1 left join" +
                //------------------------------------------------------------下段取成本和估值增值的汇兑损益
                " (select FPortCode as FPortCode2,sum(FVBal) as FSYVBal," +
                //------fanghaoln  MS00610 QDV4建行2009年07月29日01_B -------------------------------------------
                " sum(case when a232.FStorageAmount = 0 then 0 when a23.FStorageAmount=0 then 0 else FVBaseCuryBal end) as FSYVBaseCuryBal," + //当单一的原币估值增值的数据为0时，不取基础货币的估值增值。它是一笔反冲的数据
                " sum(case when a232.FStorageAmount = 0 then 0 when a23.FStorageAmount=0 then 0 else FVPortCuryBal end) as FSYVPortCuryBal from " + //当单一的原币估值增值的数据为0时，不取组合货币的估值增值。它是一笔反冲的数据
                //----------------------------MS00610 end-------------------------------------------------------
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a21 " +
               //------fanghaoln  MS00610 QDV4建行2009年07月29日01_B -------------------------------------------
               " join (" +
               " select Fsecuritycode,sum(FStorageAmount) as FStorageAmount,sum(FStorageCost) as FStorageCost from (select Fsecuritycode,FStorageAmount, FStorageCost " +
               " from " + pub.yssGetTableName("Tb_Stock_Security") +
               " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode) + //不用判断成本，在权证、股指期货中都没有成本
               (invMgrCode != null && invMgrCode.length() > 0 ?
                " and FAnalysisCode1=" + dbl.sqlString(invMgrCode) : "") +
               " and FYearMonth = " + dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
               " and FStorageDate = " +
               dbl.sqlDate(dDate) + //判断日
               ")  group by FSecurityCode" +
               ")" +
               " a232 on a21.FSecurityCode = a232.FSecurityCode " +
               //--------------------------------------------------MS00610 end---------------------------------------------------------------
                //-------fanghaoln 20090810 MS00575   QDV4华夏2009年7月23日01_B    -----------去除证券库存中数量<=0 -----------------------------
                " join (" +
                " select Fsecuritycode,sum(FStorageAmount) as FStorageAmount,sum(FStorageCost) as FStorageCost from (select Fsecuritycode,FStorageAmount, FStorageCost " +
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode) + //不用判断成本，在权证、股指期货中都没有成本
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysisCode1=" + dbl.sqlString(invMgrCode) : "") +
                //fanghaoln QDV4建行2009年9月02日01_B ：  MS00670
                " and FYearMonth <> " + dbl.sqlString(YssFun.formatDate(dDate, "yyyy")+"00") +
                //====================================================================================
                " and FStorageDate = " +
                dbl.sqlDate(YssFun.addDay(dDate, -1)) + //判断前日
                " union all " +
                " select fsecuritycode,FTradeAmount as FStorageAmount,0 as FStorageCost " +
                " from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode) + //不用判断成本，在权证、股指期货中都没有成本
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FInvMgrCode=" + dbl.sqlString(invMgrCode) : "") +
                " and FBargainDate = " +
                dbl.sqlDate(dDate) + //判断前日
                ") allData group by FSecurityCode" +
                " having sum(FStorageAmount) > 0 " +
                ")" +
                " a23 on a21.FSecurityCode = a23.FSecurityCode " +
                //---------------------------------------------------fanghaoln--------------------------------------------------------------
                " join (select FSecurityCode,FSecurityName,FCatCode,FSubCatCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                sWhereSql3 + " and FCheckState = 1) a22" + // sj sWhereSql replace to sWhereSql3
                " on a21.FSecurityCode = a22.FSecurityCode" +
                " where ((FSubTsfTypeCode like '9905%' and FSubCatCode <> 'FU01' and FCatCode <> 'FW' and FSubCatCode <> 'OP02')" +
                " or FSubTsfTypeCode like '9909%')" + //取成本汇兑损益和估值增值汇兑损益的调拨类型 胡昆 20070918
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysisCode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a2" +
                " on a1.FPortCode = a2.FPortCode2  left join" +
                //------fanghaoln  MS00755 QDV4建行2009年08月27日01_B -------------------------------------------
                //------------------------------------------------------------下段取估值增值
                " (select FPortCode as FPortCode3,sum(FVBal) as FYKVBal,sum(FVBaseCuryBal) as FYKVBaseCuryBal," +
                " sum(FVPortCuryBal) as FYKVPortCuryBal from " +
//                //------------------------------------------------------------下段取估值增值
//                " (select FPortCode as FPortCode3,sum(FVBal) as FYKVBal," +
//                //------ MS00399 QDV4建行2009年4月21日01_B -------------------------------------------
//                " sum(case when FVBal = 0 then 0 else FVBaseCuryBal end) as FYKVBaseCuryBal," + //当单一的原币估值增值的数据为0时，不取基础货币的估值增值。它是一笔反冲的数据
//                " sum(case when FVBal = 0 then 0 else FVPortCuryBal end) as FYKVPortCuryBal from " + //当单一的原币估值增值的数据为0时，不取组合货币的估值增值。它是一笔反冲的数据
                //-------------------------- end MS00755---------------------------------------------------------
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a31 " +
                " join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                sWhereSql3 + " and FCheckState = 1) a32" + // sj sWhereSql replace to sWhereSql3
                " on a31.FSecurityCode = a32.FSecurityCode" +
                " where FTsfTypeCode = '09'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysisCode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a3 " +
                " on a1.FPortCode = a3.FPortCode3 left join " +
                //------------------------------------------------------------下段取债券应收利息的汇兑损益
                " (select FPortCode as FPortCode4,sum(FVBal) as FLXSYVBal,sum(FVBaseCuryBal) as FLXSYVBaseCuryBal," +
                " sum(FVPortCuryBal) as FLXSYVPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a41 join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                sWhereSql3 + " and FCheckState = 1) a42" + // sj sWhereSql replace to sWhereSql3
                " on a41.FSecurityCode = a42.FSecurityCode" +
                " where FTsfTypeCode = '99' and FSubTsfTypeCode like '9906%'  and FSubTsfTypeCode not like '9906%DV%'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysisCode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a4" +
                " on a1.FPortCode = a4.FPortCode4 left join " +
                //------------------------------------------------------------下段取债券应收利息
                " (select FPortCode as FPortCode5,sum(FVBal) as FLXVBal,sum(FVBaseCuryBal) as FLXVBaseCuryBal," +
                " sum(FVPortCuryBal) as FLXVPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a51 join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                sWhereSql3 + " and FCheckState = 1) a52" + // sj sWhereSql replace to sWhereSql3
                " on a51.FSecurityCode = a52.FSecurityCode" +
                " where FTsfTypeCode = '06' and (FSubTsfTypeCode like '06%' and FSubTsfTypeCode not like '06%DV%')" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysisCode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a5" +
                " on a1.FPortCode = a5.FPortCode5 " +
                //-------------------------------------------------------------下段取应付利息 sj 20071118 add
                " left join (select FPortCode as FPortCode6,sum(FVBal) as FOutLXVBal,sum(FVBaseCuryBal) as FOutLXVBaseCuryBal," +
                " sum(FVPortCuryBal) as FOutLXVPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a61 join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                sWhereSql3 + " and FCheckState = 1) a62" + // sj sWhereSql replace to sWhereSql3
                " on a61.FSecurityCode = a62.FSecurityCode" +
                " where FTsfTypeCode = '07' and (FSubTsfTypeCode like '07%')  and FSubTsfTypeCode <> '07FI'" + //债券应付利息是用来冲减应收利息的，不能进入净值 胡坤  20070130（长盛测试修改）
                " and FPortCOde = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysisCode1 = " + dbl.sqlString(invMgrCode) : "") + //是FAnalysisCode1不是FAnanlysisCode1 ，杨文奇20071126
                " group by FPortCode) a6" +
                " on a1.FPortCode = a6.FPortCode6 " +
                //------------------------------------------------------------下段取债券应付利息的汇兑损益
                " left join (select FPortCode as FPortCode7,sum(FVBal) as FOutLXSYVBal,sum(FVBaseCuryBal) as FOutLXSYVBaseCuryBal," +
                " sum(FVPortCuryBal) as FOutLXSYVPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a71 join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                sWhereSql3 + " and FCheckState = 1) a72" + // sj sWhereSql replace to sWhereSql3
                " on a71.FSecurityCode = a72.FSecurityCode" +
                " where FTsfTypeCode = '99' and (FSubTsfTypeCode like '9907%'" +
                //-------------------------------------------------------------------------------------
                " and FSubTsfTypeCode <> '9907FI') " + //不需要应付利息的汇兑损益 sj edit 20080504
                //-------------------------------------------------------------------------------------
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysisCode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a7" +
                //======================================================================================================
                //fanghaoln  MS00610 QDV4建行2009年07月29日01_B 增加条件过虑掉权证行权后产生的汇总损益
                " on a1.FPortCode = a7.FPortCode7 " +
                " where a1.FStorageAmount <> 0" +
                " or a1.FVStorageCost <> 0" +
                " or a3.FYKVBal <> 0" +
                " or a5.FLXVBal <> 0" +
                //------fanghaoln  MS00610 QDV4建行2009年07月29日01_B -------------------------------------------
                 " or ((a2.FSYVPortCuryBal+a3.FYKVPortCuryBal)<>0 and a3.FYKVPortCuryBal<>0) "+
                " ) a ";
             rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                valReport.setDefaultParam();
                valReport.setExtendNum(sValueAry.length);
                valReport.setPlusMark(YssCons.YSS_REPORTSHOWDETAIL);
                setStrSpace(defineAry, sValueAry, "Sec"); //sj ------------------- 按级别设置对齐方式
                valReport.setCuryCode("合计：");
                valReport.setSParAmt(YssFun.formatNumber(rs.getDouble(
                    "FStorageAmount"), "#,##0.00##")); //数量存在小数位fazmm20071106

                valReport.setBookCost(rs.getDouble("FVStorageCost"));
                valReport.setAccrInt(rs.getDouble("FLXVBal"));
                valReport.setAccrOut(rs.getDouble("FOutLXVBal")); // sj 20071118 应付利息 add
                valReport.setGLMValue(rs.getDouble("FYKVBal"));
                valReport.setMValue(YssD.add(rs.getDouble("FVStorageCost"),
                                             rs.getDouble("FYKVBal")));

                valReport.setBaseBookCost(rs.getDouble("FVBaseCuryCost"));
                valReport.setBaseAccrInt(YssD.add(rs.getDouble("FLXVBaseCuryBal"),
                                                  rs.getDouble("FLXSYVBaseCuryBal"))); // sj 20071118 应付利息 add //应收利息显示基础货币金额+利息基础货币汇兑损益
                valReport.setBaseAccrOut(YssD.add(rs.getDouble("FOutLXVBaseCuryBal"),
                                                  rs.getDouble(
                    "FOutLXSYVBaseCuryBal"))); // sj 20071118 应付利息 add //应付利息显示基础货币金额+利息基础货币汇兑损益
                valReport.setBaseGLMValue(YssD.add(rs.getDouble("FYKVBaseCuryBal"),
                    rs.getDouble("FSYVBaseCuryBal")));
                valReport.setBaseMValue(YssD.add(YssD.add(rs.getDouble(
                    "FVBaseCuryCost"),
                    rs.getDouble("FYKVBaseCuryBal")),
                                                 rs.getDouble("FSYVBaseCuryBal")));
                valReport.setBaseGLFX(rs.getDouble("FSYVBaseCuryBal"));
                valReport.setBaseGLFXAccrInt(YssD.sub(rs.getDouble(
                    "FLXSYVBaseCuryBal"), rs.getDouble("FOutLXSYVBaseCuryBal")));

                valReport.setPortBookCost(rs.getDouble("FVPortCuryCost"));
                valReport.setPortAccrInt(YssD.add(rs.getDouble("FLXVPortCuryBal"),
                                                  rs.getDouble("FLXSYVPortCuryBal"))); // sj 20071118 应付利息 add //应收利息显示组合货币金额+利息基础货币汇兑损益
                valReport.setPortAccrOut(YssD.add(rs.getDouble("FOutLXVPortCuryBal"),
                                                  rs.getDouble(
                    "FOutLXSYVPortCuryBal"))); // sj 20071118 应付利息 add //应付利息显示组合货币金额+利息基础货币汇兑损益
                valReport.setPortGLMValue(YssD.add(rs.getDouble("FYKVPortCuryBal"),
                    rs.getDouble("FSYVPortCuryBal")));
                valReport.setPortMValue(YssD.add(YssD.add(rs.getDouble(
                    "FVPortCuryCost"),
                    rs.getDouble("FYKVPortCuryBal")),
                                                 rs.getDouble("FSYVPortCuryBal")));
                valReport.setPortGLFX(rs.getDouble("FSYVPortCuryBal"));
                valReport.setPortGLFXAccrInt(YssD.sub(rs.getDouble(
                    "FLXSYVPortCuryBal"), rs.getDouble("FOutLXSYVPortCuryBal")));

                sResult = valReport.buildTotalRowStr();
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取证券估值合计数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String buildSecRepDetailRowData(String[] defineAry, String sValue) throws
        YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        String[] sValueAry = null;
        String sWhereSql = "";
        String sWhereSql3 = "";
        StringBuffer buf = new StringBuffer();
        double dBaseRate = 1;
        try {
            sWhereSql = builderSecWhereSql(defineAry, sValue);
            sWhereSql3 = builderSecWhereSql3(defineAry, sValue);
            sValueAry = sValue.split("\f");
            strSql =
                " select a1.*, a2.*,a3.*,a4.*,a5.*,a6.*,a7.*,b.FSecurityName, b.FTradeCury as FCuryCode,b.FCuryName " +
                " ,e.FPrice as FMarketPrice " + //估值行情表中的数据。sj edit 20080826
                ",e.FOTPRICE1 as FOTPRICE1,e.FOTPRICE2 as FOTPRICE2,e.FOTPRICE3 as FOTPRICE3,d.FPurchaseType as FPurchaseType " + //sj 20071109 add
                " from (select a11.FSecurityCode,a12.FMarketCode,FPortCode" +
                //"FMarketPrice," + 不再需要.sj edit 20080826
                ",a12.FISINCode as FISINCode,a12.FExternalCode as FExternalCode," +
                " FBaseCuryRate,FPortCuryRate,sum(FStorageAmount) as FStorageAmount," +
                " sum(case when FSubCatCode = 'FU01' or FCatCode = 'FW' or FSubCatCode = 'OP02' then 0 else FVStorageCost end) as FVStorageCost," +
                " sum(case when FSubCatCode = 'FU01' or FCatCode = 'FW' or FSubCatCode = 'OP02' then 0 else FVBaseCuryCost end) as FVBaseCuryCost," +
                " sum(case when FSubCatCode = 'FU01' or FCatCode = 'FW' or FSubCatCode = 'OP02' then 0 else FVPortCuryCost end) as FVPortCuryCost from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " a11 join (select FSecurityCode,FMarketCode,FSecurityName,FCatCode,FSubCatCode,FISINCode,FExternalCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql3) + // sj sWhereSql replace to sWhereSql3
                " and FCheckState = 1) a12" +
                " on a11.FSecurityCode = a12.FSecurityCode" +
                " join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 group by FSecurityCode) r on a12.FSecurityCode = r.FSecurityCode" +

                (this.sInvMgrGroup ? sWhereSql3 : " where 1=1 ") + //sj sWhereSql replace to sWhereSql3
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and fcheckstate=1 " + //sun 080402 如果在库存中删除或未审核，这里判断下
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysiscode1 =" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a11.FSecurityCode,a12.FMarketCode,FMarketPrice,FBaseCuryRate,FPortCuryRate,a12.FISINCode,a12.FExternalCode) a1 left join" +
                //------------------------------------------------------------下段取成本和估值增值的汇兑损益
                " (select a21.FSecurityCode as FSecurityCode2, FPortCode as FPortCode2,sum(FVBal) as FSYVBal,sum(FVBaseCuryBal) as FSYVBaseCuryBal," +
                " sum(FVPortCuryBal) as FSYVPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a21 " +
                //-------fanghaoln 20090810 MS00575   QDV4华夏2009年7月23日01_B    -----------去除证券库存中数量<=0 -----------------------------
                " join (" +
                " select Fsecuritycode,sum(FStorageAmount) as FStorageAmount,sum(FStorageCost) as FStorageCost from (select Fsecuritycode,FStorageAmount, FStorageCost " +
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode) + //不用判断成本，在权证、股指期货中都没有成本
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysisCode1=" + dbl.sqlString(invMgrCode) : "") +
                //fanghaoln QDV4建行2009年9月02日01_B ：  MS00670
                " and FYearMonth <> " + dbl.sqlString(YssFun.formatDate(dDate, "yyyy")+"00") +
                //========================================================================================
                " and FStorageDate = " + //dbl.sqlDate(dDate) +
                dbl.sqlDate(YssFun.addDay(dDate, -1)) + //判断前日
                " union all " +
                " select fsecuritycode,FTradeAmount as FStorageAmount,0 as FStorageCost " +
                " from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode) + //不用判断成本，在权证、股指期货中都没有成本
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FInvMgrCode=" + dbl.sqlString(invMgrCode) : "") +
                " and FBargainDate = " + //dbl.sqlDate(dDate) +
                dbl.sqlDate(dDate) + //判断前日
               //-------fanghaoln 20091020 MS00714   QDV4华夏2009年9月24日01_B    -----------加上综合业务中做的类似换股的这种业务 -----------------------------
                " union all " +
                " select fsecuritycode,fAMOUNT as FStorageAmount,0 as FStorageCost " +
                " from " + pub.yssGetTableName("Tb_Data_Integrated") +
                " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode) + //不用判断成本，在权证、股指期货中都没有成本
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FInvMgrCode=" + dbl.sqlString(invMgrCode) : "") +
                " and FOperDate = " + //dbl.sqlDate(dDate) +
                dbl.sqlDate(dDate) + //判断当天可做综合业务
              //---------------------------------------------------end MS00714 fanghaoln---------------------------------------------------------------
                ") allData " +
                " group by FSecurityCode" +
                " having sum(FStorageAmount) > 0 " +
                ")" +
                " a23 on a21.FSecurityCode = a23.FSecurityCode " +
                //---------------------------------------------------fanghaoln---------------------------------------------------------------
                " join (select FSecurityCode,FSecurityName,FCatCode,FSubCatCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql3) + //sj sWhereSql replace to sWhereSql3
                " and FCheckState = 1) a22" +
                " on a21.FSecurityCode = a22.FSecurityCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and ((FSubTsfTypeCode like '9905%' and (FSubCatCode <> 'FU01' and FCatCode <> 'FW' and FSubCatCode <> 'OP02'))" + //期货和远期的成本是用来算估值增值的，所以成本汇兑损益不能统计到净值
                " or FSubTsfTypeCode like '9909%')" + //取成本汇兑损益和估值增值汇兑损益的调拨类型 胡昆 20070918
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a21.FSecurityCode) a2" +
                " on a1.FPortCode = a2.FPortCode2 and a1.FSecurityCode = a2.FSecurityCode2 left join" +
                //------fanghaoln  MS00755 QDV4建行2009年08月27日01_B -------------------------------------------
                //------------------------------------------------------------下段取估值增值
                " (select a31.FSecurityCode as FSecurityCode3, FPortCode as FPortCode3,sum(FVBal) as FYKVBal,sum(FVBaseCuryBal) as FYKVBaseCuryBal," +
                " sum(FVPortCuryBal) as FYKVPortCuryBal from " +
//                " (select a31.FSecurityCode as FSecurityCode3, FPortCode as FPortCode3,sum(FVBal) as FYKVBal," +
//                " sum(case when FVBal = 0 then 0 else FVBaseCuryBal end) as FYKVBaseCuryBal," + //当单一的原币估值增值的数据为0时，不取基础货币的估值增值。它是一笔反冲的数据
//                " sum(case when FVBal = 0 then 0 else FVPortCuryBal end) as FYKVPortCuryBal from " + //当单一的原币估值增值的数据为0时，不取组合货币的估值增值。它是一笔反冲的数据
//                //-------------------------- end MS00755---------------------------------------------------------
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a31 join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql3) + //sj sWhereSql replace to sWhereSql3
                " and FCheckState = 1) a32" +
                " on a31.FSecurityCode = a32.FSecurityCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '09'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a31.FSecurityCode) a3 " +
                " on a1.FPortCode = a3.FPortCode3 and a1.FSecurityCode = a3.FSecurityCode3 left join " +
                //------------------------------------------------------------下段取债券应收利息的汇兑损益
                " (select a41.FSecurityCode as FSecurityCode4, FPortCode as FPortCode4,sum(FVBal) as FLXSYVBal,sum(FVBaseCuryBal) as FLXSYVBaseCuryBal," +
                " sum(FVPortCuryBal) as FLXSYVPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a41 join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql3) + //sj sWhereSql replace to sWhereSql3
                " and FCheckState = 1) a42" +
                " on a41.FSecurityCode = a42.FSecurityCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '99' and FSubTsfTypeCode like '9906%' and FSubTsfTypeCode not like '9906%DV%' " + //红利不挂在证券下fazmm20071115
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a41.FSecurityCode) a4" +
                " on a1.FPortCode = a4.FPortCode4 and a1.FSecurityCode = a4.FSecurityCode4 left join " +
                //------------------------------------------------------------下段取应收利息
                " (select a51.FSecurityCode as FSecurityCode5, FPortCode as FPortCode5,sum(FVBal) as FLXVBal,sum(FVBaseCuryBal) as FLXVBaseCuryBal," +
                " sum(FVPortCuryBal) as FLXVPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a51 join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql3) + //sj sWhereSql replace to sWhereSql3
                " and FCheckState = 1) a52" +
                " on a51.FSecurityCode = a52.FSecurityCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '06' and (FSubTsfTypeCode like '06%' and FSubTsfTypeCode not like '06%DV%')" + //红利不放在应收利息下
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a51.FSecurityCode) a5" + //order by FSecurityCode
                " on a1.FPortCode = a5.FPortCode5 and a1.FSecurityCode = a5.FSecurityCode5 " +
                //--------------------------------------------------------
                //------------------------------------------------------------下段取应付利息 sj 20071118 add
                " left join (select a61.FSecurityCode as FSecurityCode6, FPortCode as FPortCode6,sum(FVBal) as FOutLXVBal,sum(FVBaseCuryBal) as FOutLXVBaseCuryBal," +
                " sum(FVPortCuryBal) as FOutLXVPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a61 join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql3) + //sj sWhereSql replace to sWhereSql3
                " and FCheckState = 1) a62" +
                " on a61.FSecurityCode = a62.FSecurityCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '07' and (FSubTsfTypeCode like '07%') and FSubTsfTypeCode <> '07FI'" + //债券应付利息是用来冲减应收利息的，不能进入净值 胡坤  20070130（长盛测试修改）
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a61.FSecurityCode) a6" + //order by FSecurityCode
                " on a1.FPortCode = a6.FPortCode6 and a1.FSecurityCode = a6.FSecurityCode6 " +
                //--------------------------------------------------------------下段取应付利息汇兑损益
                " left join (select a71.FSecurityCode as FSecurityCode7, FPortCode as FPortCode7,sum(FVBal) as FOutLXSYVBal,sum(FVBaseCuryBal) as FOutLXSYVBaseCuryBal," +
                " sum(FVPortCuryBal) as FOutLXSYVPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a71 join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql3) + //sj sWhereSql replace to sWhereSql3
                " and FCheckState = 1) a72" +
                " on a71.FSecurityCode = a72.FSecurityCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '99' and (FSubTsfTypeCode like '9907%'" + //红利不挂在证券下fazmm20071115
                //-------------------------------------------------------------------------------------------
                " and FSubTsfTypeCode <> '9907FI') " + //不需要应付利息的汇兑损益 sj edit 20080504
                //-------------------------------------------------------------------------------------------
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a71.FSecurityCode) a7" +
                " on a1.FPortCode = a7.FPortCode7 and a1.FSecurityCode = a7.FSecurityCode7 " +
                //--------------------------------------------------------------------------------------------------------
                " left join (select FSecurityCode, FSecurityName, b1.FTradeCury, b2.FCuryName from " +
                " (select x.* from ( select * from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) x join (select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(dDate) +
                " group by FSecurityCode ) x2 on x.FSecurityCode  = x2.FSecurityCode  and x.FStartDate=x2.FStartDate" +
                " )b1 left join (select FCuryCode," +
                " FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) b2  on b1.FTradeCury = b2.FCuryCode) b" +
                " on a1.FSecurityCode = b.FSecurityCode" +
                //---------------------------直接从估值行情表中取数 sj edit 20080826 -------------------------------//bug:0000446
                " LEFT JOIN (SELECT c.FValDate, c.FPortCode, c.FSecurityCode, c.FPrice," +
                " c.fotprice1,c.fotprice2,c.fotprice3 " +
                " FROM " + pub.yssGetTableName("TB_Data_ValMktPrice") + " c " +
                " JOIN (SELECT MAX(FValDate) AS FValDate, FSecurityCode" +
                " FROM " + pub.yssGetTableName("TB_Data_ValMktPrice") +
                " WHERE FValDate <= " + dbl.sqlDate(this.dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) + //查询条件转入其中，以免将其他组合的行情最大日期错误获取。sj edit 20081104 bug编号:0000520
                " GROUP BY FSecurityCode) d ON c.FValDate = d.FValDate" +
                " AND c.FSecurityCode = d.FSecurityCode" +
                " WHERE FPortCode = " + dbl.sqlString(this.portCode) +
                " ) e ON a1.FSecurityCode = e.FSecurityCode" +
                //----------------------------------------------------------------------------------------------//
                " left join (select FSecurityCode,FPurchaseType from " +
                pub.yssGetTableName("Tb_Para_Purchase") +
                " ) d on a1.FSecurityCode = d.FSecurityCode " + //sj a.FSecurityCode edit to a1.FSecurityCode 20071118
                " where a1.FStorageAmount<>0 or a1.FVStorageCost<>0 or " +
                //"a2.FSyvBal<>0 or " +  //现在需要汇兑损益了。sj eidt 20080723
                " a3.FYKVBal<>0 " +
                //------fanghaoln  MS00610 QDV4建行2009年07月29日01_B -------------------------------------------
                " or ((a2.FSYVPortCuryBal+a3.FYKVPortCuryBal)<>0 and a3.FYKVPortCuryBal<>0) " +
                " or a5.FLXVBal<>0 order by a1.FSecurityCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                valReport.setDefaultParam();
                valReport.setExtendNum(0);
                valReport.setPlusMark("");
                //证券投资的项目代码，如果有上市代码就显示上市代码，否则显示内部证券代码fazmm20070905
                valReport.setKeyCode(rs.getString("FSecurityCode"));
                valReport.setKeyName(rs.getString("FSecurityName"));
                valReport.setCuryCode(rs.getString("FCuryName"));
                dBaseRate = this.getSettingOper().getValCuryRate(dDate,
                    rs.getString("FCuryCode"), this.portCode,
                    YssOperCons.YSS_RATEVAL_BASE);
                dPortRate = rs.getDouble("FPortCuryRate");
                valReport.setBaseExchangeRate(dBaseRate);
                valReport.setPortExchangeRate(dPortRate);
                valReport.setParAmt(rs.getDouble("FStorageAmount"));
                valReport.setMktPrice(rs.getDouble("FMarketPrice"));
                //----------------------------------------------------------------
                valReport.setFotprice1(rs.getDouble("FOTPRICE1")); //sj add 20071109  其他行情1
                valReport.setFotprice2(rs.getDouble("FOTPRICE2"));
                valReport.setFotprice3(rs.getDouble("FOTPRICE3"));
                //----------------------------------------------------------------
                valReport.setExternalCode(rs.getString("FExternalCode") == null ?
                                          "" : rs.getString("FExternalCode")); //sj add 20071111  外部代码
                valReport.setISINCode(rs.getString("FISINCode") == null ? "" :
                                      rs.getString("FISINCode")); //Insi代码
                //----------------------------------------------------------------
                valReport.setBookCost(rs.getDouble("FVStorageCost"));
                valReport.setAccrInt(rs.getDouble("FLXVBal"));
                valReport.setAccrOut(rs.getDouble("FOutLXVBal")); //sj add 20071118 应付利息
                valReport.setGLMValue(rs.getDouble("FYKVBal"));
                valReport.setMValue(YssD.add(rs.getDouble("FVStorageCost"),
                                             rs.getDouble("FYKVBal")));

                valReport.setBaseBookCost(rs.getDouble("FVBaseCuryCost"));
                valReport.setBaseAccrInt(YssD.add(rs.getDouble("FLXVBaseCuryBal"),
                                                  rs.getDouble("FLXSYVBaseCuryBal"))); //应收利息显示基础货币金额+利息基础货币汇兑损益 胡昆 20071118
                valReport.setBaseAccrOut(YssD.add(rs.getDouble("FOutLXVBaseCuryBal"),
                                                  rs.getDouble(
                    "FOutLXSYVBaseCuryBal"))); //sj add 20071118 应付利息  应付利息显示基础货币金额+利息基础货币汇兑损益 胡昆 20071118
                valReport.setBaseGLMValue(YssD.add(rs.getDouble("FYKVBaseCuryBal"),
                    rs.getDouble("FSYVBaseCuryBal")));
                valReport.setBaseMValue(YssD.add(YssD.add(rs.getDouble(
                    "FVBaseCuryCost"),
                    rs.getDouble("FYKVBaseCuryBal")),
                                                 rs.getDouble("FSYVBaseCuryBal")));
                valReport.setBaseGLFX(rs.getDouble("FSYVBaseCuryBal"));
                valReport.setBaseGLFXAccrInt(YssD.sub(rs.getDouble(
                    "FLXSYVBaseCuryBal"), rs.getDouble("FOutLXSYVBaseCuryBal")));

                valReport.setPortBookCost(rs.getDouble("FVPortCuryCost"));
                valReport.setPortAccrInt(YssD.add(rs.getDouble("FLXVPortCuryBal"),
                                                  rs.getDouble("FLXSYVPortCuryBal"))); //应收利息显示组合货币金额+利息组合货币汇兑损益 胡昆 20071118
                valReport.setPortAccrOut(YssD.add(rs.getDouble("FOutLXVPortCuryBal"),
                                                  rs.getDouble(
                    "FOutLXSYVPortCuryBal"))); //sj add 20071118 应付利息  应付利息显示组合货币金额+利息组合货币汇兑损益 胡昆 20071118
                valReport.setPortGLMValue(YssD.add(rs.getDouble("FYKVPortCuryBal"),
                    rs.getDouble("FSYVPortCuryBal")));
                valReport.setPortMValue(YssD.add(YssD.add(rs.getDouble(
                    "FVPortCuryCost"),
                    rs.getDouble("FYKVPortCuryBal")),
                                                 rs.getDouble("FSYVPortCuryBal")));
                valReport.setPortGLFX(rs.getDouble("FSYVPortCuryBal"));
                valReport.setPortGLFXAccrInt(YssD.sub(rs.getDouble(
                    "FLXSYVPortCuryBal"), rs.getDouble("FOutLXSYVPortCuryBal")));
                if (rs.getString("FPurchaseType") != null &&
                    rs.getString("FPurchaseType").equalsIgnoreCase("RePh")) { //为正回购的话，乘以-1
                    dBaseTotal = YssD.add(dBaseTotal,
                                          YssD.mul(valReport.getBaseMValue(), -1)); //乘以-1
                    dBaseTotal = YssD.sub(YssD.add(dBaseTotal,
                        valReport.getBaseAccrInt()),
                                          valReport.getBaseAccrOut()); //减去应付利息 sj 20071118

                    dPortTotal = YssD.add(dPortTotal,
                                          YssD.mul(valReport.getPortMValue(), -1));
                    dPortTotal = YssD.sub(YssD.add(dPortTotal,
                        valReport.getPortAccrInt()),
                                          valReport.getPortAccrOut()); //减去应付利息

                    if (this.sInvMgrGroup == true) {
                        dBaseNetValue = YssD.add(dBaseNetValue,
                                                 YssD.mul(valReport.getBaseMValue(),
                            -1));
                        dBaseNetValue = YssD.sub(YssD.add(dBaseNetValue,
                            valReport.getBaseAccrInt()), valReport.getBaseAccrOut()); //减去应付利息

                        //计算基础货币和组合货币的估值增值  胡昆 20070917
                        //------fanghaoln  MS00755 QDV4建行2009年08月27日01_B -------------------------------------------
                        SecurityBean secBeen = new SecurityBean(); //证券信息java Bean
                        secBeen.setYssPub(pub);//add by songjie 2012.10.09 报空指针异常
                        secBeen.setStrSecurityCode(rs.getString("FSecurityCode")); //传证券代码
                        secBeen = (SecurityBean) secBeen.getSetting(); //得到证券信息
						//edit by songjie 2012.10.09 报空指针异常
                        if (secBeen != null && secBeen.getCategoryCode().equalsIgnoreCase("OP") && secBeen.getSubCategoryCode().equalsIgnoreCase("OPO2")) { //当是结转权证
                            this.dBaseIncMv = YssD.add(dBaseIncMv,
                                YssD.mul(YssD.add(rs.getDouble(
                                    "FYKVBaseCuryBal"), 0),
                                         -1)); //不加入估值增值
                            this.dPortIncMv = YssD.add(dPortIncMv,
                                YssD.mul(YssD.add(rs.getDouble(
                                    "FYKVPortCuryBal"), 0),
                                         -1)); //不加入估值增值

                        } else {
                            this.dBaseIncMv = YssD.add(dBaseIncMv,
                                YssD.mul(YssD.add(rs.getDouble(
                                    "FYKVBaseCuryBal"), rs.getDouble("FSYVBaseCuryBal")),
                                         -1));
                            this.dPortIncMv = YssD.add(dPortIncMv,
                                YssD.mul(YssD.add(rs.getDouble(
                                    "FYKVPortCuryBal"), rs.getDouble("FSYVPortCuryBal")),
                                         -1));
                        }
                        //-----------------------------end MS00755 ---------------------------------------------------------------
                        this.dBaseExFx = YssD.add(dBaseExFx,
                                                  YssD.mul(YssD.add(rs.getDouble(
                            "FLXSYVBaseCuryBal"),
                            rs.getDouble("FSYVBaseCuryBal")),
                            -1));
                        this.dPortExFx = YssD.add(dPortExFx,
                                                  YssD.mul(YssD.add(rs.getDouble(
                            "FLXSYVPortCuryBal"),
                            rs.getDouble("FSYVPortCuryBal")),
                            -1));

                        dPortNetValue = YssD.add(dPortNetValue,
                                                 YssD.mul(valReport.getPortMValue(),
                            -1));
                        dPortNetValue = YssD.sub(YssD.add(dPortNetValue,
                            valReport.getPortAccrInt()), valReport.getPortAccrOut()); //减去应付利息
                    }
                } else {
                    dBaseTotal = YssD.add(dBaseTotal, valReport.getBaseMValue());
                    dBaseTotal = YssD.sub(YssD.add(dBaseTotal,
                        valReport.getBaseAccrInt()),
                                          valReport.getBaseAccrOut()); //减去应付利息

                    dPortTotal = YssD.add(dPortTotal, valReport.getPortMValue());
                    dPortTotal = YssD.sub(YssD.add(dPortTotal,
                        valReport.getPortAccrInt()),
                                          valReport.getPortAccrOut()); //减去应付利息

                    if (this.sInvMgrGroup == true) {
                        dBaseNetValue = YssD.add(dBaseNetValue,
                                                 valReport.getBaseMValue());
                        dBaseNetValue = YssD.sub(YssD.add(dBaseNetValue,
                            valReport.getBaseAccrInt()), valReport.getBaseAccrOut()); //减去应付利息
                        dBaseNetValue = YssD.add(dBaseNetValue,
                                                 valReport.getBaseGLFXAccrInt());

                        //计算基础货币和组合货币的估值增值  胡昆 20070917
                        this.dBaseIncMv = YssD.add(dBaseIncMv,
                            YssD.add(rs.getDouble(
                                "FYKVBaseCuryBal"),
                                     rs.getDouble("FSYVBaseCuryBal")));
                        this.dPortIncMv = YssD.add(dPortIncMv,
                            YssD.add(rs.getDouble(
                                "FYKVPortCuryBal"),
                                     rs.getDouble("FSYVPortCuryBal")));
                        //计算基础货币和组合货币的汇兑损益  胡昆 20070917
                        //汇兑损益不包含估值增值和库存成本的汇兑损益fazmm20071120
                        this.dBaseExFx = YssD.add(dBaseExFx,
                                                  rs.getDouble("FLXSYVBaseCuryBal"),
                                                  rs.getDouble("FSYVBaseCuryBal"));
                        this.dBaseExFx = YssD.sub(dBaseExFx,
                                                  rs.getDouble("FOutLXSYVBaseCuryBal"));

                        this.dPortExFx = YssD.add(dPortExFx,
                                                  rs.getDouble("FLXSYVPortCuryBal"),
                                                  rs.getDouble("FSYVPortCuryBal"));
                        this.dPortExFx = YssD.sub(dPortExFx,
                                                  rs.getDouble("FOutLXSYVPortCuryBal"));

                        dPortNetValue = YssD.add(dPortNetValue,
                                                 valReport.getPortMValue());
                        dPortNetValue = YssD.sub(YssD.add(dPortNetValue,
                            valReport.getPortAccrInt()), valReport.getPortAccrOut()); //减去应付利息
                    }
                }

                if (!bSaveNet) {
                    buf.append(valReport.buildRowStr()).append("\r\n");
                }
            }
            sResult = buf.toString();
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取证券估值明细数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    protected String getSecSumRowTypeName(String[] sDefineAry,
                                          String[] sValueAry) throws
        YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = (String)this.hmTypeNameSec.get(sDefineAry[sValueAry.length -
                1]);
            strSql = strSql.replaceAll("@", sValueAry[sValueAry.length - 1]);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                sResult = rs.getString(2) + "";
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String builderSecWhereSql(String[] defineAry, String sValue) {
        String[] sValueAry = null;
        String sFieldCode = "";
        String sWhereSql = " where 1=1 ";
        sValueAry = sValue.split("\f");
        for (int i = 0; i < sValueAry.length; i++) {
            sFieldCode = (String) hmFieldSec.get(defineAry[i]);
            if (!sValueAry[i].equalsIgnoreCase("null")) {
                sWhereSql += " and " + sFieldCode + " = " +
                    dbl.sqlString(sValueAry[i]);
            } else {
                sWhereSql += " and " + sFieldCode + " is null ";
            }
        }
        return sWhereSql;
    }

    protected String builderSecWhereSql3(String[] defineAry, String sValue) {
        String[] sValueAry = null;
        String sFieldCode = "";
        String sWhereSql = " where 1=1 ";
        sValueAry = sValue.split("\f");
        for (int i = 0; i < sValueAry.length; i++) {
            sFieldCode = (String) hmFieldSec.get(defineAry[i]);
            if (!sFieldCode.equalsIgnoreCase("FAttrClsCode")) {
                if (!sValueAry[i].equalsIgnoreCase("null")) {
                    sWhereSql += " and " + sFieldCode + " = " +
                        dbl.sqlString(sValueAry[i]);
                } else {
                    sWhereSql += " and " + sFieldCode + " is null ";
                }
            } else {
                continue;
            }
        }

        return sWhereSql;
    }

    protected String builderSecWhereSql2(String[] defineAry, String sValue) {
        String[] sValueAry = null;
        String sFieldCode = "";
        String sWhereSql = " where 1=1 ";
        sValueAry = sValue.split("\f");

        if (!sValueAry[0].equalsIgnoreCase("null")) {
            sWhereSql += " and " + "FAnalysisCode2" + " = " +
                dbl.sqlString(sValueAry[0]);
        } else {
            sWhereSql += " and " + "FAnalysisCode2" + " is null ";
        }
        return sWhereSql;
    }

    protected String buildCashRepData() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        ResultSet rsSub = null;
        String[] valCashDefineAry = null;
        String sWhereSql = "";
        StringBuffer buf = new StringBuffer();
        try {
            valCashDefineAry = valCashDefine.split(";");
            strSql = "select * from tb_Temp_GzClsCash_" + pub.getUserCode() +
                " order by FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (valCashDefineAry.length >=
                    rs.getString("FCode").split("\f").length) {
                    buf.append(buildCashRepSumRowData(valCashDefineAry,
                        rs.getString("FCode"))).
                        append("\r\n");
                }
                if (valCashDefineAry.length ==
                    rs.getString("FCode").split("\f").length) {
                    buf.append(buildCashRepDetailRowData(valCashDefineAry,
                        rs.getString("FCode")));
                }
            }
            sResult = buf.toString() + "\r\n";
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取现金估值数据出错");
        }finally{
        	dbl.closeResultSetFinal(rs);//添加关闭结果集 by leeyu 20100903
        }
    }

    protected String buildCashRepSumRowData(String[] defineAry, String sValue) throws
        YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        String sWhereSql = "";
        String[] sValueAry = null;
        double dBaseRate = 0;
        double dPortRate = 0;

        boolean analy1; //判断是否需要用分析代码；杨
        boolean analy2;
        boolean analy3;

        try {

            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

            sWhereSql = builderCashWhereSql(defineAry, sValue);
            sValueAry = sValue.split("\f"); //,c.FRPCuryCode
            strSql = "select a.*,b.FPortCury  from (" +
                " select a1.*, a2.*,a3.*,a4.*,a5.*,a6.*,a7.*,a8.* from (select FPortCode,sum(FAccBalance) as FAccBalance," +
                " sum(FBaseCuryBal) as FBaseCuryBal," +
                " sum(FPortCuryBal) as FPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                " a11 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                sWhereSql + " and FCheckState = 1 " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " ) a12" +
                " on a11.FCashAccCode = a12.FCashAccCode" +
                " where FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a1 left join" +
                //------------------------------------------------------------下段取现金成本的汇兑损益
                " (select FPortCode as FPortCode2,sum(FBal) as FSYBal,sum(FBaseCuryBal) as FSYBaseCuryBal," +
                " sum(FPortCuryBal) as FSYPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a21 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                sWhereSql + " and FCheckState = 1  " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " ) a22" +
                " on a21.FCashAccCode = a22.FCashAccCode" +
                " where FTsfTypeCode = '99' and FSubTsfTypeCode like '9905%'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a2" +
                " on a1.FPortCode = a2.FPortCode2  left join" +
                //------------------------------------------------------------取存款利息的汇兑损益
                " (select FPortCode as FPortCode3,sum(FBal) as FLXSYBal,sum(FBaseCuryBal) as FLXSYBaseCuryBal," +
                " sum(FPortCuryBal) as FLXSYPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a31 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                sWhereSql + " and FCheckState = 1  " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                ") a32" +
                " on a31.FCashAccCode = a32.FCashAccCode" +
                " where FTsfTypeCode = '99' and FSubTsfTypeCode = '9906DE'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a3" +
                " on a1.FPortCode = a3.FPortCode3 left join" +
                //------------------------------------------------------------取存款利息
                " (select FPortCode as FPortCode4,sum(FBal) as FLXBal,sum(FBaseCuryBal) as FLXBaseCuryBal," +
                " sum(FPortCuryBal) as FLXPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a41 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                sWhereSql + " and FCheckState = 1  " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                ") a42" +
                " on a41.FCashAccCode = a42.FCashAccCode" +
                " where FTsfTypeCode = '06' and FSubTsfTypeCode = '06DE'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a4" +
                " on a1.FPortCode = a4.FPortCode4 left join" +
                //--------------------------------------------------------------取应收款项
                " (select FPortCode as FPortCode5,sum(FBal) as FRecBal,sum(FBaseCuryBal) as FRecBaseCuryBal," +
                " sum(FPortCuryBal) as FRecPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a41 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                sWhereSql + " and FCheckState = 1  " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                ") a42" +
                " on a41.FCashAccCode = a42.FCashAccCode" +
                " where FTsfTypeCode = '06' and FSubTsfTypeCode in ('06TD','06CE','06OT','06DV','06TA','06PF')" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode) a5" +
                " on a1.FPortCode = a5.FPortCode5 left join" +
                //---------------------------------------------------------------取应收款项汇兑损益
                " (select FPortCode as FPortCode6,sum(FBal) as FSYRecBal,sum(FBaseCuryBal) as FSYRecBaseCuryBal," +
                " sum(FPortCuryBal) as FSYRecPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a41 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                sWhereSql + " and FCheckState = 1  " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                ") a42" +
                " on a41.FCashAccCode = a42.FCashAccCode" +
                " where FTsfTypeCode = '99' and FSubTsfTypeCode in ('9906TD','9906CE','9906OT','9906DV','9906TA','9906PF')" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                " group by FPortCode) a6" +
                " on a1.FPortCode = a6.FPortCode6 left join" +
                //--------------------------------------------------------------取应付款项
                " (select FPortCode as FPortCode7,sum(FBal) as FPayBal,sum(FBaseCuryBal) as FPayBaseCuryBal," +
                " sum(FPortCuryBal) as FPayPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a41 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                sWhereSql + " and FCheckState = 1  " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                ") a42" +
                " on a41.FCashAccCode = a42.FCashAccCode" +
                //2008.05.26 蒋锦 添加 07FE
                " where FTsfTypeCode = '07' and FSubTsfTypeCode in ('07TD','07CE','07OT','07DV','07TA','07FE')" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                " group by FPortCode) a7" +
                " on a1.FPortCode = a7.FPortCode7 left join" +
                //---------------------------------------------------------------取应付款项汇兑损益
                " (select FPortCode as FPortCode8,sum(FBal) as FSYPayBal,sum(FBaseCuryBal) as FSYPayBaseCuryBal," +
                " sum(FPortCuryBal) as FSYPayPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a41 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                sWhereSql + " and FCheckState = 1 " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                ") a42" +
                " on a41.FCashAccCode = a42.FCashAccCode" +
                " where FTsfTypeCode = '99' and FSubTsfTypeCode in ('9907TD','9907CE','9907OT','9907DV','9907TA')" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                " group by FPortCode) a8" +
                " on a1.FPortCode = a8.FPortCode8" +
                //---------------------------------------------------------------
                ") a left join (select FPortCode,FPortCury from " +
                pub.yssGetTableName("tb_para_portfolio") +
                " where FCheckState = 1 ) b on a.FPortCode = b.FPortCode";
            //-------------屏蔽为0记录 sj edit 20080603 ----------------------
//               + " where (FLXSYBASECURYBAL+FLXBASECURYBAL <> 0) " +
//               " or (FLXSYPORTCURYBAL+FLXPORTCURYBAL <> 0) " +
//               " or (FPayBaseCuryBal + FSYPayBaseCuryBal<>0) " +
//               " or (FPayPortCuryBal + FSYPayPortCuryBal<>0)"
            //---------------------------------------------------------------
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                valReport.setDefaultParam();
                valReport.setExtendNum(sValueAry.length);
                valReport.setPlusMark(YssCons.YSS_REPORTSHOWDETAIL);
                setStrSpace(defineAry, sValueAry, "Cash"); //sj ------------------- 按级别设置对齐方式
                valReport.setCuryCode("合计：");

                valReport.setBookCost(rs.getDouble("FAccBalance"));
                valReport.setMValue(rs.getDouble("FAccBalance"));
                valReport.setAccrInt(rs.getDouble("FLXBal"));
                valReport.setGLMValue(0);
                valReport.setRecMoney(rs.getDouble("FRecBal"));
                valReport.setPayMoney(rs.getDouble("FPayBal"));

                valReport.setBaseBookCost(rs.getDouble("FBaseCuryBal"));
                valReport.setBaseAccrInt(YssD.add(rs.getDouble("FLXBaseCuryBal"),
                                                  rs.getDouble("FLXSYBaseCuryBal"))); //add sj 20071118 (add "FLXSYBaseCuryBal")
                valReport.setBaseGLMValue(0);
                valReport.setBaseMValue(YssD.add(rs.getDouble("FBaseCuryBal"),
                                                 rs.getDouble("FSYBaseCuryBal")));
                valReport.setBaseGLFX(rs.getDouble("FSYBaseCuryBal"));
                valReport.setBaseGLFXAccrInt(rs.getDouble("FLXSYBaseCuryBal"));

                valReport.setBaseRecMoney(YssD.add(rs.getDouble("FRecBaseCuryBal"),
                    rs.getDouble("FSYRecBaseCuryBal")));
                valReport.setBasePayMoney(YssD.add(rs.getDouble("FPayBaseCuryBal"),
                    rs.getDouble("FSYPayBaseCuryBal")));

                valReport.setPortRecMoney(YssD.add(rs.getDouble("FRecPortCuryBal"),
                    rs.getDouble("FSYRecPortCuryBal")));
                valReport.setPortPayMoney(YssD.add(rs.getDouble("FPayPortCuryBal"),
                    rs.getDouble("FSYPayPortCuryBal")));

                valReport.setPortBookCost(rs.getDouble("FPortCuryBal"));
                valReport.setPortAccrInt(YssD.add(rs.getDouble("FLXPortCuryBal"),
                                                  rs.getDouble("FLXSYPortCuryBal"))); //add sj 20071118 (add "FLXSYPortCuryBal")
                valReport.setPortGLMValue(0);
                valReport.setPortMValue(YssD.add(rs.getDouble("FPortCuryBal"),
                                                 rs.getDouble("FSYPortCuryBal")));
                valReport.setPortGLFX(rs.getDouble("FSYPortCuryBal"));
                valReport.setPortGLFXAccrInt(rs.getDouble("FLXSYPortCuryBal"));

                sResult = valReport.buildTotalRowStr();
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取现金估值合计数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String buildCashRepDetailRowData(String[] defineAry, String sValue) throws
        YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        String[] sValueAry = null;
        String sWhereSql = "";
        StringBuffer buf = new StringBuffer();
        double dBaseRate = 1;
        double dPortRate = 1;
        try {
            sWhereSql = builderCashWhereSql(defineAry, sValue);
            sValueAry = sValue.split("\f");
            strSql = "select a.*,b.FCashAccName,b.FCuryCode,b.FCuryName,c.FRPCashAccName,c.FRPCuryCode,c.FRPCuryName,d.FPortCury,c.FAccAttr from (" + //add FAccAttr sj 20071111
                " select a1.*, a2.*,a3.*,a4.*,a5.*,a6.*,a7.*,a8.* from (select a11.FCashAccCode,FPortCode," +
                " FBaseCuryRate,FPortCuryRate,sum(FAccBalance) as FAccBalance," +
                " sum(FBaseCuryBal) as FBaseCuryBal,sum(FPortCuryBal) as FPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                " a11 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1 " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " ) a12" +
                " on a11.FCashAccCode = a12.FCashAccCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a11.FCashAccCode,FBaseCuryRate,FPortCuryRate) a1 left join" +
                //------------------------------------------------------------下段取现金成本的汇兑损益
                " (select a21.FCashAccCode as FCashAccCode2, FPortCode as FPortCode2,sum(FBal) as FSYBal,sum(FBaseCuryBal) as FSYBaseCuryBal," +
                " sum(FPortCuryBal) as FSYPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a21 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1 " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " ) a22" +
                " on a21.FCashAccCode = a22.FCashAccCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '99' and FSubTsfTypeCode like '9905%'" + //取成本汇兑损益和估值增值汇兑损益的调拨类型 胡昆 20070918
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a21.FCashAccCode) a2" +
                " on a1.FPortCode = a2.FPortCode2 and a1.FCashAccCode = a2.FCashAccCode2 left join" +
                //------------------------------------------------------------取应收存款利息的汇兑损益
                " (select a31.FCashAccCode as FCashAccCode3, FPortCode as FPortCode3,sum(FBal) as FLXSYBal,sum(FBaseCuryBal) as FLXSYBaseCuryBal," +
                " sum(FPortCuryBal) as FLXSYPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a31 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1 " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " ) a32" +
                " on a31.FCashAccCode = a32.FCashAccCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '99' and FSubTsfTypeCode = '9906DE'" + //胡昆 20070918
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a31.FCashAccCode) a3 " +
                " on a1.FPortCode = a3.FPortCode3 and a1.FCashAccCode = a3.FCashAccCode3 left join " +
                //------------------------------------------------------------下段取存款利息
                " (select a41.FCashAccCode as FCashAccCode4, FPortCode as FPortCode4,sum(FBal) as FLXBal,sum(FBaseCuryBal) as FLXBaseCuryBal," +
                " sum(FPortCuryBal) as FLXPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a41 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1  " +
                " and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                ") a42" +
                " on a41.FCashAccCode = a42.FCashAccCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '06' and FSubTsfTypeCode = '06DE'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " group by FPortCode,a41.FCashAccCode) a4" +
                " on a1.FPortCode = a4.FPortCode4 and a1.FCashAccCode = a4.FCashAccCode4 left join" +
                //--------------------------------------------------------------下段取应收款
                " (select a51.FCashAccCode as FCashAccCode5, FPortCode as FPortCode5,sum(FBal) as FRecBal,sum(FBaseCuryBal) as FRecBaseCuryBal," +
                " sum(FPortCuryBal) as FRecPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a51 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1  " +
                " and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " ) a52" +
                " on a51.FCashAccCode = a52.FCashAccCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '06' and FSubTsfTypeCode in ('06TD','06CE','06OT','06DV','06TA','06PF')" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                " group by FPortCode,a51.FCashAccCode) a5" +
                " on a1.FPortCode = a5.FPortCode5 and a1.FCashAccCode = a5.FCashAccCode5 left join" +
                //---------------------------------------------------------------下段取应收款汇兑损益
                " (select a61.FCashAccCode as FCashAccCode6, FPortCode as FPortCode6,sum(FBal) as FSYRecBal,sum(FBaseCuryBal) as FSYRecBaseCuryBal," +
                " sum(FPortCuryBal) as FSYRecPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a61 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1  " +
                " and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " ) a62" +
                " on a61.FCashAccCode = a62.FCashAccCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '99' and FSubTsfTypeCode in ('9906TD','9906CE','9906OT','9906DV','9906TA','9906PF')" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                " group by FPortCode,a61.FCashAccCode) a6" +
                " on a1.FPortCode = a6.FPortCode6 and a1.FCashAccCode = a6.FCashAccCode6 left join" +
                //---------------------------------------------------------------下段取应付款
                " (select a71.FCashAccCode as FCashAccCode7, FPortCode as FPortCode7,sum(FBal) as FPayBal,sum(FBaseCuryBal) as FPayBaseCuryBal," +
                " sum(FPortCuryBal) as FPayPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a71 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1  " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " ) a72" +
                " on a71.FCashAccCode = a72.FCashAccCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '07' and FSubTsfTypeCode in ('07TD','07CE','07OT','07DV','07TA','07FE')" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                " group by FPortCode,a71.FCashAccCode) a7" +
                " on a1.FPortCode = a7.FPortCode7 and a1.FCashAccCode = a7.FCashAccCode7 left join" +
                //---------------------------------------------------------------下段取应付款汇兑损益
                " (select a81.FCashAccCode as FCashAccCode8, FPortCode as FPortCode8,sum(FBal) as FSYPayBal,sum(FBaseCuryBal) as FSYPayBaseCuryBal," +
                " sum(FPortCuryBal) as FSYPayPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " a81 join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1  " +
                "and FAccType <> '04' " + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                ") a82" +
                " on a81.FCashAccCode = a82.FCashAccCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '99' and FSubTsfTypeCode in ('9907TD','9907CE','9907OT','9907DV','9907TA','9907FE')" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                " group by FPortCode,a81.FCashAccCode) a8" +
                " on a1.FPortCode = a8.FPortCode8 and a1.FCashAccCode = a8.FCashAccCode8) a left join" +
                //------------------------------------------------------------
                " (select FCashAccCode,FCashAccName,b1.FCuryCode,b2.FCuryName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " b1 left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) b2 on b1.FCuryCode = b2.FCuryCode" +
                " where FCheckState = 1 " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                ") b on a.FCashAccCode = b.FCashAccCode" +
                //-------------------------------------------------------------
                " left join " +
                //---------------------------------------------------------------------
                " (select FCashAccCode,FCashAccName as FRPCashAccName," +
                " c1.FCuryCode as FRPCuryCode,c2.FCuryName as FRPCuryName," +
                " c1.FAccAttr as FAccAttr" +
                " from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " c1 left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) c2 on c1.FCuryCode = c2.FCuryCode" +
                " where FCheckState = 1 " +
                "and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                ") c on a.FCashAccCode = c.FCashAccCode" +
                //--------------------------------------------------------------
                " left join (select FPortCode,FPortCury from " +
                pub.yssGetTableName("tb_para_portfolio") +
                " where FCheckState = 1) d on a.FPortCode = d.FPortCode" +
                //-------------屏蔽为0记录 sj edit 20080603 ----------------------
                " where FAccBalance<>0 or FBaseCuryBal<>0 or FPortCuryBal<>0 or " +
                " FLxBal<>0  " +
                " or (FLXSYBASECURYBAL+FLXBASECURYBAL <> 0) " +
                " or (FLXSYPORTCURYBAL+FLXPORTCURYBAL <> 0) " +
                " or FRecBal<>0 or FRecBaseCuryBal<>0 or FRecPortCuryBal<>0 " +
                " or FPayBal<>0  " +
                " or (FPayBaseCuryBal + FSYPayBaseCuryBal<>0) " +
                " or (FPayPortCuryBal + FSYPayPortCuryBal<>0)";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                valReport.setDefaultParam();
                valReport.setExtendNum(0);
                valReport.setPlusMark("");
                valReport.setKeyCode(rs.getString("FCashAccCode"));
                valReport.setKeyName(rs.getString("FCashAccName"));
                valReport.setCuryCode(rs.getString("FCuryName"));
                dBaseRate = rs.getDouble("FBaseCuryRate");
                dPortRate = rs.getDouble("FPortCuryRate"); //由于dPortRate写死了为1，所以这里改成取库存表中汇率，杨

                valReport.setBaseExchangeRate(dBaseRate);
                valReport.setPortExchangeRate(dPortRate);
                valReport.setBookCost(rs.getDouble("FAccBalance"));
                valReport.setMValue(rs.getDouble("FAccBalance"));
                valReport.setAccrInt(rs.getDouble("FLXBal"));
                valReport.setGLMValue(0);
                valReport.setRecMoney(rs.getDouble("FRecBal"));
                valReport.setPayMoney(rs.getDouble("FPayBal"));

                valReport.setBaseBookCost(rs.getDouble("FBaseCuryBal"));
                valReport.setBaseAccrInt(YssD.add(rs.getDouble("FLXBaseCuryBal"),
                                                  rs.getDouble("FLXSYBaseCuryBal"))); //add sj 20071118 //应收利息显示基础货币金额+利息基础货币汇兑损益
                valReport.setBaseGLMValue(0);
                valReport.setBaseMValue(YssD.add(rs.getDouble("FBaseCuryBal"),
                                                 rs.getDouble("FSYBaseCuryBal")));
                valReport.setBaseGLFX(rs.getDouble("FSYBaseCuryBal"));
                valReport.setBaseGLFXAccrInt(rs.getDouble("FLXSYBaseCuryBal"));

                valReport.setBaseRecMoney(YssD.add(rs.getDouble("FRecBaseCuryBal"),
                    rs.getDouble("FSYRecBaseCuryBal")));
                valReport.setBasePayMoney(YssD.add(rs.getDouble("FPayBaseCuryBal"),
                    rs.getDouble("FSYPayBaseCuryBal")));

                valReport.setPortBookCost(rs.getDouble("FPortCuryBal"));
                valReport.setPortAccrInt(YssD.add(rs.getDouble("FLXPortCuryBal"),
                                                  rs.getDouble("FLXSYPortCuryBal"))); //add sj 20071118 //应收利息显示组合货币金额+利息组合货币汇兑损益
                valReport.setPortGLMValue(0);
                valReport.setPortMValue(YssD.add(rs.getDouble("FPortCuryBal"),
                                                 rs.getDouble("FSYPortCuryBal")));
                valReport.setPortGLFX(rs.getDouble("FSYPortCuryBal"));
                valReport.setPortGLFXAccrInt(rs.getDouble("FLXSYPortCuryBal"));

                valReport.setPortRecMoney(YssD.add(rs.getDouble("FRecPortCuryBal"),
                    rs.getDouble("FSYRecPortCuryBal")));
                valReport.setPortPayMoney(YssD.add(rs.getDouble("FPayPortCuryBal"),
                    rs.getDouble("FSYPayPortCuryBal")));

                dBaseTotal = YssD.add(dBaseTotal,
                                      YssD.mul(valReport.getBaseMValue(),
                                               rs.getInt("FAccAttr")));
                dBaseTotal = YssD.add(dBaseTotal,
                                      YssD.mul(valReport.getBaseAccrInt(),
                                               rs.getInt("FAccAttr")));
                dBaseTotal = YssD.add(dBaseTotal,
                                      YssD.mul(valReport.getBaseRecMoney(),
                                               rs.getInt("FAccAttr")));
                dBaseTotal = YssD.sub(dBaseTotal,
                                      YssD.mul(valReport.getBasePayMoney(),
                                               rs.getInt("FAccAttr"))); //乘以帐户属性

                //-------------------------可用现金头寸的计算方法改变,与存入库中数相同 sj edit 20080122 ------------------//
                this.dBaseUseCashTotal += YssD.add(dBaseUseCash,
                    YssD.mul(YssD.sub(YssD.add(
                        valReport.
                        getBaseMValue(), valReport.getBaseRecMoney()),
                                      valReport.getBasePayMoney()), rs.getInt("FAccAttr")));
                //----------------------------------------------------------------------------------------------------

                dPortTotal = YssD.add(dPortTotal,
                                      YssD.mul(valReport.getPortMValue(),
                                               rs.getInt("FAccAttr")));
                dPortTotal = YssD.add(dPortTotal,
                                      YssD.mul(valReport.getPortAccrInt(),
                                               rs.getInt("FAccAttr")));
                dPortTotal = YssD.add(dPortTotal,
                                      YssD.mul(valReport.getPortRecMoney(),
                                               rs.getInt("FAccAttr")));
                dPortTotal = YssD.sub(dPortTotal,
                                      YssD.mul(valReport.getPortPayMoney(),
                                               rs.getInt("FAccAttr")));

                //-------------------------可用现金头寸的计算方法改变,与存入库中数相同 sj edit 20080122 ------------------//
                this.dPortUseCashTotal += YssD.add(dPortUseCash,
                    YssD.mul(YssD.sub(YssD.add(
                        valReport.
                        getPortMValue(), valReport.getPortRecMoney()),
                                      valReport.getPortPayMoney()), rs.getInt("FAccAttr")));
                //----------------------------------------------------------------------------------------------------

                if (this.sInvMgrGroup == true) {
                    dBaseNetValue = YssD.add(dBaseNetValue,
                                             YssD.mul(valReport.getBaseMValue(),
                        rs.getInt("FAccAttr")));
                    dBaseNetValue = YssD.add(dBaseNetValue,
                                             YssD.mul(valReport.getBaseAccrInt(),
                        rs.getInt("FAccAttr")));

                    dBaseNetValue = YssD.add(dBaseNetValue,
                                             YssD.mul(valReport.getBaseRecMoney(),
                        rs.getInt("FAccAttr")));
                    dBaseNetValue = YssD.sub(dBaseNetValue,
                                             YssD.mul(valReport.getBasePayMoney(),
                        rs.getInt("FAccAttr")));

                    //计算基础货币和组合货币的汇兑损益,可用头寸  胡昆 20070917
                    this.dBaseExFx = YssD.add(dBaseExFx,
                                              YssD.mul(YssD.add(rs.getDouble(
                                                  "FSYBaseCuryBal"),
                        rs.getDouble("FLXSYBaseCuryBal")), rs.getInt("FAccAttr")));

                    this.dBaseUseCash = YssD.add(dBaseUseCash,
                                                 YssD.mul(YssD.sub(YssD.add(
                        valReport.
                        getBaseMValue(),
                        valReport.getBaseRecMoney()),
                        valReport.getBasePayMoney()), rs.getInt("FAccAttr")));

                    this.dPortExFx = YssD.add(dPortExFx,
                                              YssD.mul(YssD.add(rs.getDouble(
                                                  "FSYPortCuryBal"),
                        rs.getDouble("FLXSYPortCuryBal")), rs.getInt("FAccAttr")));

                    this.dPortUseCash = YssD.add(dPortUseCash,
                                                 YssD.mul(YssD.sub(YssD.add(
                        valReport.
                        getPortMValue(),
                        valReport.getPortRecMoney()),
                        valReport.getPortPayMoney()), rs.getInt("FAccAttr")));

                    dPortNetValue = YssD.add(dPortNetValue,
                                             YssD.mul(valReport.getPortMValue(),
                        rs.getInt("FAccAttr")));
                    dPortNetValue = YssD.add(dPortNetValue,
                                             YssD.mul(valReport.getPortAccrInt(),
                        rs.getInt("FAccAttr")));

                    dPortNetValue = YssD.add(dPortNetValue,
                                             YssD.mul(valReport.getPortRecMoney(),
                        rs.getInt("FAccAttr")));

                    dPortNetValue = YssD.sub(dPortNetValue,
                                             YssD.mul(valReport.getPortPayMoney(),
                        rs.getInt("FAccAttr")));

                }
                if (!bSaveNet) {
                    buf.append(valReport.buildRowStr()).append("\r\n");
                }

            }
            sResult = buf.toString();
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取现金估值明细数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String getCashSumRowTypeName(String[] sDefineAry,
                                           String[] sValueAry) throws
        YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = (String)this.hmTypeNameCash.get(sDefineAry[sValueAry.length -
                1]);
            strSql = strSql.replaceAll("@", sValueAry[sValueAry.length - 1]);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                sResult = rs.getString(2) + "";
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String builderCashWhereSql(String[] defineAry, String sValue) {
        String[] sValueAry = null;
        String sFieldCode = "";
        String sWhereSql = " where 1=1 ";
        sValueAry = sValue.split("\f");
        for (int i = 0; i < sValueAry.length; i++) {
            sFieldCode = (String) hmFieldCash.get(defineAry[i]);
            if (!sValueAry[i].equalsIgnoreCase("null")) {
                sWhereSql += " and " + sFieldCode + " = " +
                    dbl.sqlString(sValueAry[i]);
            } else {
                sWhereSql += " and " + sFieldCode + " is null ";
            }
        }
        return sWhereSql;
    }

    protected String buildInvestRepData() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        ResultSet rsSub = null;
        String[] valInvestDefineAry = null;
        String sWhereSql = "";
        StringBuffer buf = new StringBuffer();
        try {
            valInvestDefineAry = this.valInvestDefine.split(";");
            strSql = "select * from tb_Temp_GzClsInvest_" + pub.getUserCode() +
                " order by FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (valInvestDefineAry.length >=
                    rs.getString("FCode").split("\f").length) {
                    buf.append(buildInvestRepSumRowData(valInvestDefineAry,
                        rs.getString("FCode"))).
                        append("\r\n");
                }
                if (valInvestDefineAry.length ==
                    rs.getString("FCode").split("\f").length) {
                    buf.append(buildInvestRepDetailRowData(valInvestDefineAry,
                        rs.getString("FCode")));
                }
            }
            sResult = buf.toString();
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取运营收支估值数据出错");
        }finally{
        	dbl.closeResultSetFinal(rs);//添加关闭结果集 by leeyu 20100903
        }
    }

    protected String buildInvestRepSumRowData(String[] defineAry, String sValue) throws
        YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        String sWhereSql = "";
        String[] sValueAry = null;
        try {
            sWhereSql = builderInvestWhereSql(defineAry, sValue);
            sValueAry = sValue.split("\f");
            strSql = "select a.* ,  (case when FPayType = 0 then FSRSYBal else FFYSYBal end ) as FSYBal," +
                "(case when FPayType = 0 then FSRSYBaseCuryBal else FFYSYBaseCuryBal end ) as FSYBaseCuryBal," +
                "(case when FPayType = 0 then FSRSYPortCuryBal else FFYSYPortCuryBal end ) as FSYPortCuryBal from (" +
                //--------------------------------------------------------------
                //运营收支成本
                " select FPortCode,FPayType,sum(FBal) as FAccBalance,sum(FBaseCuryBal) as FBaseCuryBal," +
                "sum(FPortCuryBal) as FPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_Invest") +
                " a11 join (select FIVPayCatCode,FPayType from Tb_Base_InvestPayCat " +
                sWhereSql +
                " and FCheckState = 1) a12 on a11.FIVPayCatCode = a12.FIVPayCatCode" +
                " where FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " and FCheckState = 1 group by FPortCode,FPayType) a " +
                //---------------------------------------------------------------
                //收入类汇兑损益
                " left join (select FPortCode,sum(FBal) as FSRSYBal,sum(FBaseCuryBal) as FSRSYBaseCuryBal," +
                "  sum(FPortCuryBal) as FSRSYPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                " a21 join (select FIVPayCatCode,FPayType from Tb_Base_InvestPayCat " +
                sWhereSql +
                " and FCheckState = 1) a22 on a21.FIVPayCatCode = a22.FIVPayCatCode" +
                " where FTsfTypeCode = '99' and FSubTsfTypeCode = '9906IV'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " and FCheckState = 1 group by FPortCode) b on a.FPortCode = b.FPortCode" +
                //---------------------------------------------------------------
                //支出类汇兑损益
                " left join (select FPortCode,sum(FBal) as FFYSYBal,sum(FBaseCuryBal) as FFYSYBaseCuryBal," +
                "  sum(FPortCuryBal) as FFYSYPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                " a31 join (select FIVPayCatCode,FPayType from Tb_Base_InvestPayCat " +
                sWhereSql +
                " and FCheckState = 1) a32 on a31.FIVPayCatCode = a32.FIVPayCatCode" +
                " where FTsfTypeCode = '99' and FSubTsfTypeCode = '9907IV'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " and FCheckState = 1 group by FPortCode) c on a.FPortCode = c.FPortCode"; //if without 应收应付款项 the "+" replace to ;
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                valReport.setDefaultParam();
                valReport.setExtendNum(sValueAry.length);
                valReport.setPlusMark(YssCons.YSS_REPORTSHOWDETAIL);
                setStrSpace(defineAry, sValueAry, "Invest"); //sj ------------------- 按级别设置对齐方式
                valReport.setCuryCode("合计：");

                valReport.setBookCost(rs.getDouble("FAccBalance"));
                valReport.setMValue(rs.getDouble("FAccBalance"));
                valReport.setAccrInt(0);
                valReport.setGLMValue(0);

                valReport.setBaseAccrInt(0);
                valReport.setBaseGLMValue(0);
                valReport.setBaseGLFX(rs.getDouble("FSYBaseCuryBal"));
                valReport.setBaseGLFXAccrInt(0);
                valReport.setBaseBookCost(rs.getDouble("FBaseCuryBal"));
                valReport.setBaseMValue(rs.getDouble("FBaseCuryBal"));

                valReport.setPortBookCost(rs.getDouble("FPortCuryBal"));
                valReport.setPortMValue(rs.getDouble("FPortCuryBal"));
                valReport.setPortAccrInt(0);
                valReport.setPortGLMValue(0);
                valReport.setPortGLFX(rs.getDouble("FSYPortCuryBal"));
                valReport.setPortGLFXAccrInt(0);

                sResult = valReport.buildTotalRowStr();
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取运营收支估值合计数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String buildInvestRepDetailRowData(String[] defineAry,
                                                 String sValue) throws
        YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        String[] sValueAry = null;
        String sWhereSql = "";
        StringBuffer buf = new StringBuffer();
        try {
            sWhereSql = builderInvestWhereSql(defineAry, sValue);
            sValueAry = sValue.split("\f");

            strSql = "select a.*,(case when FPayType = 0 then FSRSYBal else FFYSYBal end ) as FSYBal," +
                "(case when FPayType = 0 then FSRSYBaseCuryBal else FFYSYBaseCuryBal end ) as FSYBaseCuryBal," +
                "(case when FPayType = 0 then FSRSYPortCuryBal else FFYSYPortCuryBal end ) as FSYPortCuryBal " +
                //----------增加汇率的获取 sj edit 20080402 --
                ", d.FBaseRate as FBaseCuryRate" +
                ", d.FPortRate as FPortCuryRate" +
                //------------------------------------------
                " from (" +
                //--------------------------------------------------------------
                //运营收支成本
                " select FPortCode,a11.FIVPayCatCode,FIVPayCatName,FPayType,sum(FBal) as FAccBalance," +
                "sum(FBaseCuryBal) as FBaseCuryBal,sum(FPortCuryBal) as FPortCuryBal " +
                //----------增加货币的获取以便从估值汇率表中获取汇率 sj edit 20080402--
                ",FCuryCode " +
                //---------------------------------------------------------------
                " from " +
                pub.yssGetTableName("Tb_Stock_Invest") +
                " a11 join (select FIVPayCatCode,FIVPayCatName,FPayType from Tb_Base_InvestPayCat " +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1) a12 on a11.FIVPayCatCode = a12.FIVPayCatCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " and FCheckState = 1 group by FPortCode,FPayType,a11.FIVPayCatCode,FIVPayCatName" +
                //--------增加了group字段 sj edit 20080402 -------
                ",FCuryCode " +
                //-----------------------------------------------
                ") a " +
                //---------------------------------------------------------------
                //收入类汇兑损益
                " left join (select FPortCode,a21.FIVPayCatCode,sum(FBal) as FSRSYBal," +
                "sum(FBaseCuryBal) as FSRSYBaseCuryBal,sum(FPortCuryBal) as FSRSYPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                " a21 join (select FIVPayCatCode,FPayType from Tb_Base_InvestPayCat " +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1) a22 on a21.FIVPayCatCode = a22.FIVPayCatCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '99' and FSubTsfTypeCode = '9906IV'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " and FCheckState = 1 group by FPortCode,a21.FIVPayCatCode) b " +
                " on a.FPortCode = b.FPortCode and a.FIVPayCatCode = b.FIVPayCatCode" +
                //---------------------------------------------------------------
                //支出类汇兑损益
                " left join (select FPortCode,a31.FIVPayCatCode,sum(FBal) as FFYSYBal," +
                "sum(FBaseCuryBal) as FFYSYBaseCuryBal,sum(FPortCuryBal) as FFYSYPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                " a31 join (select FIVPayCatCode,FPayType from Tb_Base_InvestPayCat " +
                (this.sInvMgrGroup ? " where 1 = 1 " : sWhereSql) +
                " and FCheckState = 1) a32 on a31.FIVPayCatCode = a32.FIVPayCatCode" +
                (this.sInvMgrGroup ? sWhereSql : " where 1=1 ") +
                " and FTsfTypeCode = '99' and FSubTsfTypeCode = '9907IV'" +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.trim().length() > 0 ?
                 " and FAnalysiscode1=" + dbl.sqlString(invMgrCode) : "") +
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
                " and FCheckState = 1 group by FPortCode,a31.FIVPayCatCode) c " +
                " on a.FPortCode = c.FPortCode and a.FIVPayCatCode = c.FIVPayCatCode" +
                //----------------------从估值汇率表中获取此货币的汇率 sj edit 20080402 -----------------------------------------
                " left join (select FValDate,FPortCode,FCuryCode,FBaseRate,FPortRate from " +
                pub.yssGetTableName("Tb_Data_ValRate") +
                " where FCheckState = 1 and FValDate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(portCode) + ") d " +
                " on a.FCuryCode = d.FCuryCode";
            //-------------------------------------------------------------------------------------------------------------

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                valReport.setDefaultParam();
                valReport.setExtendNum(0);
                valReport.setPlusMark("");
                //-----------------设置汇率 sj edit 20080402 ------------------------
                valReport.setBaseExchangeRate(rs.getDouble("FBaseCuryRate"));
                valReport.setPortExchangeRate(rs.getDouble("FPortCuryRate"));
                //------------------------------------------------------------------
                valReport.setKeyCode(rs.getString("FIVPayCatCode"));
                valReport.setKeyName(rs.getString("FIVPayCatName"));

                valReport.setBookCost(rs.getDouble("FAccBalance"));
                valReport.setMValue(rs.getDouble("FAccBalance"));
                valReport.setAccrInt(0);
                valReport.setGLMValue(0);

                valReport.setBaseBookCost(rs.getDouble("FBaseCuryBal"));
                valReport.setBaseMValue(rs.getDouble("FBaseCuryBal"));
                valReport.setBaseAccrInt(0);
                valReport.setBaseGLMValue(0);
                valReport.setBaseGLFX(rs.getDouble("FSYBaseCuryBal"));
                valReport.setBaseGLFXAccrInt(0);

                valReport.setPortBookCost(rs.getDouble("FPortCuryBal"));
                valReport.setPortMValue(rs.getDouble("FPortCuryBal"));
                valReport.setPortAccrInt(0);
                valReport.setPortGLMValue(0);
                valReport.setPortGLFX(rs.getDouble("FSYPortCuryBal"));
                valReport.setPortGLFXAccrInt(0);

                if (rs.getDouble("FPayType") == 0) { //收入
                    dBaseTotal = YssD.add(dBaseTotal, valReport.getBaseBookCost());
                    dBaseTotal = YssD.add(dBaseTotal, valReport.getBaseGLFX());
                    dBaseTotal = YssD.add(dBaseTotal, valReport.getBaseRecMoney());
                    dPortTotal = YssD.add(dPortTotal, valReport.getPortBookCost());
                    dPortTotal = YssD.add(dPortTotal, valReport.getPortGLFX());
                    dPortTotal = YssD.add(dPortTotal, valReport.getPortRecMoney());
                } else if (rs.getDouble("FPayType") == 1) { //支出
                    dBaseTotal = YssD.sub(dBaseTotal, valReport.getBaseBookCost());
                    dBaseTotal = YssD.sub(dBaseTotal, valReport.getBaseGLFX());
                    dBaseTotal = YssD.sub(dBaseTotal, valReport.getBasePayMoney());
                    dPortTotal = YssD.sub(dPortTotal, valReport.getPortBookCost());
                    dPortTotal = YssD.sub(dPortTotal, valReport.getPortGLFX());
                    dPortTotal = YssD.sub(dPortTotal, valReport.getPortPayMoney());
                }

                if (this.sInvMgrGroup) {
                    if (rs.getDouble("FPayType") == 0) {
                        dBaseNetValue = YssD.add(dBaseNetValue,
                                                 valReport.getBaseBookCost());
                        dBaseNetValue = YssD.add(dBaseNetValue, valReport.getBaseGLFX());
                        dBaseNetValue = YssD.add(dBaseNetValue,
                                                 valReport.getBaseRecMoney());
                        dPortNetValue = YssD.add(dPortNetValue,
                                                 valReport.getPortBookCost());
                        dPortNetValue = YssD.add(dPortNetValue, valReport.getPortGLFX());
                        dPortNetValue = YssD.add(dPortNetValue,
                                                 valReport.getPortRecMoney());
                        //计算基础货币和组合货币的汇兑损益  胡昆 20070917
                        this.dBaseExFx = YssD.add(dBaseExFx, valReport.getBaseGLFX());
                        this.dPortExFx = YssD.add(dPortExFx, valReport.getPortGLFX());

                    } else if (rs.getDouble("FPayType") == 1) {
                        dBaseNetValue = YssD.sub(dBaseNetValue,
                                                 valReport.getBaseBookCost());
                        dBaseNetValue = YssD.sub(dBaseNetValue, valReport.getBaseGLFX());
                        dBaseNetValue = YssD.sub(dBaseNetValue,
                                                 valReport.getBasePayMoney());
                        dPortNetValue = YssD.sub(dPortNetValue,
                                                 valReport.getPortBookCost());
                        dPortNetValue = YssD.sub(dPortNetValue, valReport.getPortGLFX());
                        dPortNetValue = YssD.sub(dPortNetValue,
                                                 valReport.getPortPayMoney());
                        //计算基础货币和组合货币的汇兑损益  胡昆 20070917
                        this.dBaseExFx = YssD.sub(dBaseExFx, valReport.getBaseGLFX());
                        this.dPortExFx = YssD.sub(dPortExFx, valReport.getPortGLFX());
                    }
                }
                if (!bSaveNet) {
                    buf.append(valReport.buildRowStr()).append("\r\n");
                }
            }
            sResult = buf.toString();
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取运营收支估值明细数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    protected String builderInvestWhereSql(String[] defineAry, String sValue) {
        String[] sValueAry = null;
        String sFieldCode = "";
        String sWhereSql = " where 1=1 ";
        sValueAry = sValue.split("\f");
        for (int i = 0; i < sValueAry.length; i++) {
            sFieldCode = (String) hmFieldInvest.get(defineAry[i]);
            if (!sValueAry[i].equalsIgnoreCase("null")) {
                if (!sFieldCode.equalsIgnoreCase("FPayType")) {
                    sWhereSql += " and " + sFieldCode + " = " +
                        dbl.sqlString(sValueAry[i]);
                } else {
                    sWhereSql += " and " + sFieldCode + " = " +
                        sValueAry[i];
                }
            } else {
                sWhereSql += " and " + sFieldCode + " is null ";
            }
        }
        return sWhereSql;
    }

    protected String getInvestSumRowTypeName(String[] sDefineAry,
                                             String[] sValueAry) throws
        YssException {
        String sResult = "";
        try {
            if (YssFun.toInt(sValueAry[0]) == 0) {
                sResult = "收入";
            } else if (YssFun.toInt(sValueAry[0]) == 1) {
                sResult = "支出";
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取汇总运营收支类名称出错");
        }
    }

    /**
     * saveReport
     *
     * @param sReport String
     * @return String
     */
    public String saveReport(String sReport) throws YssException {
        String strSql = "";
        PreparedStatement pst = null;
        YssNetValue netValue = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        Iterator iter = null;
        try {
            //-------需要清零,前面的程序中以赋值 sj add 20080122 --//
            this.dBaseUseCash = 0;
            this.dPortUseCash = 0;
            //---------------------------------------------------
            countNetValue(); //以投资经理分组来计算净值

            conn.setAutoCommit(false);
            bTrans = true;

            //删除原有记录
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_NetValue") +
                " where FNAVDate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode);
            dbl.executeSql(strSql);

            //向资产净值表中插入基础货币资产净值
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_NetValue") +
                " (FNAVDate,FPortCode,FInvMgrCode,FBaseNetValue,FPortNetValue,FAmount,FType,FCheckState,FCreator,FCreateTime)" +
                " values(?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            //插入以下项目  胡昆  20070917
//         “1－资产净值”(已有）
//         “2－单位净值”
//         “3－估值增值”
//         “4－汇兑损益”
//         “5－实收资本”
//         “6－损益平准金（未实现）”
//         “7－损益平准金（已实现）”
//         “8－现金头寸”
            for (int i = 1; i < 9; i++) {
                if (i == 1) { //插入资产净值
                    iter = hmNetValue.values().iterator();
                    while (iter.hasNext()) {
                        netValue = (YssNetValue) iter.next();
                        pst.setDate(1, YssFun.toSqlDate(dDate));
                        pst.setString(2, this.portCode);
                        pst.setString(3, netValue.getInvMgrCode());
                        pst.setDouble(4, netValue.getBaseNetValue());
                        pst.setDouble(5, netValue.getPortNetValue());
                        pst.setDouble(6, 0);
                        pst.setString(7, "01"); //标识－资产净值
                        pst.setInt(8, 1);
                        pst.setString(9, pub.getUserCode());
                        pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                        pst.executeUpdate();
                    }
                } else if (i == 2) { //插入单位净值
                    iter = hmNetValue.values().iterator();
                    while (iter.hasNext()) {
                        netValue = (YssNetValue) iter.next();
                        pst.setDate(1, YssFun.toSqlDate(dDate));
                        pst.setString(2, this.portCode);
                        pst.setString(3, netValue.getInvMgrCode());
                        if (netValue.getCapital() != 0) {
                            //-----------不需要保留小数位 sj 20080312 ---------------------//
                            pst.setDouble(4,
                                          YssD.div(netValue.getBaseNetValue(),
                                netValue.getCapital()));
                            pst.setDouble(5,
                                          YssD.div(netValue.getPortNetValue(),
                                netValue.getCapital()));
                        }
                        //------------------------------------------------------------
                        pst.setDouble(6, 0);
                        pst.setString(7, "02"); //标识－单位净值
                        pst.setInt(8, 1);
                        pst.setString(9, pub.getUserCode());
                        pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                        pst.executeUpdate();
                    }
                } else if (i == 3) { //插入估值增值
                    iter = hmNetValue.values().iterator();
                    while (iter.hasNext()) {
                        netValue = (YssNetValue) iter.next();
                        pst.setDate(1, YssFun.toSqlDate(dDate));
                        pst.setString(2, this.portCode);
                        pst.setString(3, netValue.getInvMgrCode());
                        pst.setDouble(4, netValue.getIncBaseMV());
                        pst.setDouble(5, netValue.getIncPortMV());
                        pst.setDouble(6, 0);
                        pst.setString(7, "03"); //标识－估值增值
                        pst.setInt(8, 1);
                        pst.setString(9, pub.getUserCode());
                        pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                        pst.executeUpdate();
                    }
                } else if (i == 4) { //插入汇兑损益
                    iter = hmNetValue.values().iterator();
                    while (iter.hasNext()) {
                        netValue = (YssNetValue) iter.next();
                        pst.setDate(1, YssFun.toSqlDate(dDate));
                        pst.setString(2, this.portCode);
                        pst.setString(3, netValue.getInvMgrCode());
                        pst.setDouble(4, netValue.getExBaseFX());
                        pst.setDouble(5, netValue.getExPortFX());
                        pst.setDouble(6, 0);
                        pst.setString(7, "04"); //标识－汇兑损益
                        pst.setInt(8, 1);
                        pst.setString(9, pub.getUserCode());
                        pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                        pst.executeUpdate();
                    }
                } else if (i == 5) { //插入实收资本
                    iter = hmNetValue.values().iterator();
                    while (iter.hasNext()) {
                        netValue = (YssNetValue) iter.next();
                        pst.setDate(1, YssFun.toSqlDate(dDate));
                        pst.setString(2, this.portCode);
                        pst.setString(3, netValue.getInvMgrCode());
                        pst.setDouble(4, netValue.getCapitalBaseCuryCost());
                        pst.setDouble(5, netValue.getCapitalPortCuryCost());
                        pst.setDouble(6, netValue.getCapital());
                        pst.setString(7, "05"); //标识－实收资本
                        pst.setInt(8, 1);
                        pst.setString(9, pub.getUserCode());
                        pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                        pst.executeUpdate();
                    }
                } else if (i == 6) { //插入损益平准金（未实现）
                    iter = hmNetValue.values().iterator();
                    while (iter.hasNext()) {
                        netValue = (YssNetValue) iter.next();
                        pst.setDate(1, YssFun.toSqlDate(dDate));
                        pst.setString(2, this.portCode);
                        pst.setString(3, netValue.getInvMgrCode());
                        pst.setDouble(4, netValue.getBaseUnPl());
                        pst.setDouble(5, netValue.getPortUnPl());
                        pst.setDouble(6, 0); //实收资本插入数量
                        pst.setString(7, "06"); //标识－损益平准金（未实现）
                        pst.setInt(8, 1);
                        pst.setString(9, pub.getUserCode());
                        pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                        pst.executeUpdate();
                    }
                } else if (i == 7) { //插入损益平准金（已实现）
                    iter = hmNetValue.values().iterator();
                    while (iter.hasNext()) {
                        netValue = (YssNetValue) iter.next();
                        pst.setDate(1, YssFun.toSqlDate(dDate));
                        pst.setString(2, this.portCode);
                        pst.setString(3, netValue.getInvMgrCode());
                        pst.setDouble(4, netValue.getBasePl());
                        pst.setDouble(5, netValue.getPortPl());
                        pst.setDouble(6, 0);
                        pst.setString(7, "07"); //标识－损益平准金（已实现）
                        pst.setInt(8, 1);
                        pst.setString(9, pub.getUserCode());
                        pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                        pst.executeUpdate();
                    }
                } else if (i == 8) { //插入现金头寸
                    iter = hmNetValue.values().iterator();
                    while (iter.hasNext()) {
                        netValue = (YssNetValue) iter.next();
                        pst.setDate(1, YssFun.toSqlDate(dDate));
                        pst.setString(2, this.portCode);
                        pst.setString(3, netValue.getInvMgrCode());
                        pst.setDouble(4, netValue.getBaseUseCash());
                        pst.setDouble(5, netValue.getPortUseCash());
                        pst.setDouble(6, 0);
                        pst.setString(7, "08"); //标识－现金头寸
                        pst.setInt(8, 1);
                        pst.setString(9, pub.getUserCode());
                        pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                        pst.executeUpdate();
                    }
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新资产净值表信息出错" + "\n" + e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(pst);
        }
        return "";
    }

    protected void countNetValue() throws YssException {
        sInvMgrGroup = true;
        valSecDefine = "InvMgr";
        valCashDefine = "InvMgr";
        valInvestDefine = "InvMgr";

        setSecClassTable();
        setCashClassTable();
        setInvestClassTable();

        hmNetValue = new HashMap();

        buildSecNetValueData();
        buildCashNetValueData();
        buildInvestNetValueData();

        setTAData();
    }

    //设置TA相关的数据，包括实收资本，损益平准金 胡昆 20070917
    private void setTAData() throws YssException {
        String invmgrField = "";
        String strSql = "";
        ResultSet rs = null;
        String sKey = "";
        YssNetValue netValue = null;
        try {
            invmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
            strSql = "select " +
                ((invmgrField != null && invmgrField.length() > 0) ? invmgrField + "," : "") +
                "sum(FStorageAmount) as FStorageAmount," +
                "sum(fcost) as fcost,sum(fbasecurycost) as fbasecurycost,sum(fportcurycost) as fportcurycost," +
                "sum(FBaseCuryUnpl) as FBaseCuryUnpl, sum(FPortCuryUnpl) as FPortCuryUnpl," +
                "sum(FBaseCuryPl) as FBaseCuryPl, sum(FPortCuryPl) as FPortCuryPl" +
                " from " + pub.yssGetTableName("Tb_Stock_TA") +
                " where FStorageDate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                (invMgrCode != null && invMgrCode.length() > 0 ?
                 " and FAnalysisCode1=" + dbl.sqlString(invMgrCode) : "") +
                " and FCheckState = 1" +
                (invmgrField != null && invmgrField.length() > 0 ?
                 " group by " + invmgrField : "");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (invmgrField.length() > 0) {
                    sKey = rs.getString(invmgrField);
                } else {
                    sKey = " ";
                }
                if (hmNetValue.containsKey(sKey)) {
                    netValue = (YssNetValue) hmNetValue.get(sKey);
                    netValue.setCapital(rs.getDouble("FStorageAmount"));
                    netValue.setCapitalCost(rs.getDouble("fcost"));
                    netValue.setCapitalBaseCuryCost(rs.getDouble("fbasecurycost"));
                    netValue.setCapitalPortCuryCost(rs.getDouble("fportcurycost"));

                    netValue.setBaseUnPl(rs.getDouble("FBaseCuryUnpl"));
                    netValue.setBasePl(rs.getDouble("FBaseCuryPl"));
                    netValue.setPortUnPl(rs.getDouble("FPortCuryUnpl"));
                    netValue.setPortPl(rs.getDouble("FPortCuryPl"));
                }
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void buildSecNetValueData() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String[] valSecDefineAry = null;
        YssNetValue netValue = null;
        String sKey = "";
        try {
            valSecDefineAry = valSecDefine.split(";");
            strSql = "select * from tb_temp_gzClsSec_" + pub.getUserCode() +
                " order by FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (valSecDefineAry.length ==
                    rs.getString("FCode").split("\f").length) {
                    //初始化变量
                    dBaseNetValue = 0;
                    dPortNetValue = 0;
                    dBaseIncMv = 0;
                    dPortIncMv = 0;
                    dBaseExFx = 0;
                    dPortExFx = 0;
                    buildSecRepDetailRowData(valSecDefineAry, rs.getString("FCode")); //调用该方法计算证券的净值，估值增值，汇兑损益
                }
                sKey = rs.getString("FCode"); //这里的Key是投资经理，如果不存在就新建，存在就累加
                if (!hmNetValue.containsKey(sKey)) {
                    netValue = new YssNetValue();
                    netValue.setInvMgrCode(sKey);
                    netValue.setBaseNetValue(dBaseNetValue);
                    netValue.setPortNetValue(dPortNetValue);
                    //把估值增值和汇兑损益加入了HashMap中  胡昆 20070917
                    netValue.setIncBaseMV(this.dBaseIncMv);
                    netValue.setIncPortMV(this.dPortIncMv);
                    netValue.setExBaseFX(this.dBaseExFx);
                    netValue.setExPortFX(this.dPortExFx);
                    hmNetValue.put(sKey, netValue);
                } else {
                    netValue = (YssNetValue) hmNetValue.get(sKey);
                    netValue.setBaseNetValue(YssD.add(netValue.getBaseNetValue(),
                        dBaseNetValue));
                    netValue.setPortNetValue(YssD.add(netValue.getPortNetValue(),
                        dPortNetValue));
                    //把估值增值和汇兑损益累加  胡昆 20070917
                    netValue.setIncBaseMV(YssD.add(netValue.getIncBaseMV(),
                        dBaseIncMv));
                    netValue.setIncPortMV(YssD.add(netValue.getIncPortMV(),
                        dPortIncMv));
                    netValue.setExBaseFX(YssD.add(netValue.getExBaseFX(),
                                                  dBaseExFx));
                    netValue.setExPortFX(YssD.add(netValue.getExPortFX(),
                                                  dPortExFx));
                }
            }
        } catch (Exception e) {
            throw new YssException("获取证券估值数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void buildCashNetValueData() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String[] valCashDefineAry = null;
        YssNetValue netValue = null;
        String sKey = "";
        double dTmp;
        try {
            valCashDefineAry = valCashDefine.split(";");
            strSql = "select * from tb_Temp_GzClsCash_" + pub.getUserCode() +
                " order by FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (valCashDefineAry.length ==
                    rs.getString("FCode").split("\f").length) {
                    //初始化变量
                    dBaseNetValue = 0;
                    dPortNetValue = 0;
                    dBaseIncMv = 0;
                    dPortIncMv = 0;
                    dBaseExFx = 0;
                    dPortExFx = 0;
                    buildCashRepDetailRowData(valCashDefineAry,
                                              rs.getString("FCode")); //调用该方法计算现金的净值，汇兑损益
                }
                sKey = rs.getString("FCode"); //这里的Key是投资经理，如果不存在就新建，存在就累加
                if (!hmNetValue.containsKey(sKey)) {
                    netValue = new YssNetValue();
                    netValue.setInvMgrCode(sKey);
                    netValue.setBaseNetValue(dBaseNetValue);
                    netValue.setPortNetValue(dPortNetValue);
                    //把估值增值和汇兑损益加入了HashMap中  胡昆 20070917
                    netValue.setIncBaseMV(this.dBaseIncMv);
                    netValue.setIncPortMV(this.dPortIncMv);
                    netValue.setExBaseFX(this.dBaseExFx);
                    netValue.setExPortFX(this.dPortExFx);
                    netValue.setBaseUseCash(this.dBaseUseCash);
                    netValue.setPortUseCash(this.dPortUseCash);
                    hmNetValue.put(sKey, netValue);
                } else {
                    netValue = (YssNetValue) hmNetValue.get(sKey);
                    netValue.setBaseNetValue(YssD.add(netValue.getBaseNetValue(),
                        dBaseNetValue));
                    netValue.setPortNetValue(YssD.add(netValue.getPortNetValue(),
                        dPortNetValue));
                    //把估值增值和汇兑损益累加  胡昆 20070917
                    netValue.setIncBaseMV(YssD.add(netValue.getIncBaseMV(),
                        dBaseIncMv));
                    netValue.setIncPortMV(YssD.add(netValue.getIncPortMV(),
                        dPortIncMv));
                    netValue.setExBaseFX(YssD.add(netValue.getExBaseFX(),
                                                  dBaseExFx));
                    netValue.setExPortFX(YssD.add(netValue.getExPortFX(),
                                                  dPortExFx));
                    netValue.setBaseUseCash(YssD.add(netValue.getBaseUseCash(),
                        dBaseUseCash));
                    netValue.setPortUseCash(YssD.add(netValue.getPortUseCash(),
                        dPortUseCash));

                }
            }
        } catch (Exception e) {
            throw new YssException("获取现金估值数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void buildInvestNetValueData() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String[] valInvestDefineAry = null;
        YssNetValue netValue = null;
        String sKey = "";
        double dTmp;
        try {
            valInvestDefineAry = this.valInvestDefine.split(";");
            strSql = "select * from tb_Temp_GzClsInvest_" + pub.getUserCode() +
                " order by FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (valInvestDefineAry.length ==
                    rs.getString("FCode").split("\f").length) {
                    dBaseNetValue = 0;
                    dPortNetValue = 0;
                    dBaseExFx = 0;
                    dPortExFx = 0;
                    buildInvestRepDetailRowData(valInvestDefineAry,
                                                rs.getString("FCode"));
                }
                sKey = rs.getString("FCode");
                if (!hmNetValue.containsKey(sKey)) {
                    netValue = new YssNetValue();
                    netValue.setInvMgrCode(sKey);
                    netValue.setBaseNetValue(dBaseNetValue);
                    netValue.setPortNetValue(dPortNetValue);
                    //把汇兑损益加入了HashMap中  胡昆 20070917
                    netValue.setExBaseFX(this.dBaseExFx);
                    netValue.setExPortFX(this.dPortExFx);
                    hmNetValue.put(sKey, netValue);
                } else {
                    netValue = (YssNetValue) hmNetValue.get(sKey);
                    netValue.setBaseNetValue(YssD.add(netValue.getBaseNetValue(),
                        dBaseNetValue));
                    netValue.setPortNetValue(YssD.add(netValue.getPortNetValue(),
                        dPortNetValue));
                    //把汇兑损益累加  胡昆 20070917
                    netValue.setExBaseFX(YssD.add(netValue.getExBaseFX(),
                                                  dBaseExFx));
                    netValue.setExPortFX(YssD.add(netValue.getExPortFX(),
                                                  dPortExFx));
                }

            }
        } catch (Exception e) {
            throw new YssException("获取运营收支估值数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setRelaMap() {
        hmFieldSec = new HashMap();
        hmFieldSec.put("CatType", "FCatCode");
        hmFieldSec.put("CatSubType", "FSubCatCode");
        hmFieldSec.put("CatCusType", "FCusCatCode");
        hmFieldSec.put("Cury", "FTradeCury");
        hmFieldSec.put("InvMgr", "FAnalysisCode1");
        hmFieldSec.put("AttrCls", "FAttrClsCode"); //新加的级别 sj 测 20071205

        hmTypeNameSec = new HashMap();
        hmTypeNameSec.put("CatType",
                          "select FCatCode,FCatName from Tb_Base_Category where FCatCode = '@' and FCheckState = 1");
        hmTypeNameSec.put("CatSubType",
                          "select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FSubCatCode = '@' and FCheckState = 1");
        hmTypeNameSec.put("CatCusType",
                          "select FCusCatCode,FCusCatName from " +
                          pub.yssGetTableName("Tb_Para_CustomCategory") +
                          " where FCusCatCode = '@' and FCheckState = 1");
        hmTypeNameSec.put("Cury", "select FCuryCode,FCuryName from " +
                          pub.yssGetTableName("Tb_Para_Currency") +
                          " where FCuryCode = '@' and FCheckState = 1");
        hmTypeNameSec.put("AttrCls", "select FAttrClsCode,FAttrClsName from " +
                          pub.yssGetTableName("Tb_Para_AttributeClass") +
                          " where FAttrClsCode = '@' and FCheckState = 1"); //新加的级别 sj 测 20071205
        hmTypeNameCash = new HashMap();
        hmTypeNameCash.put("AccType",
                           "select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FAccTypeCode = '@' and FCheckState = 1");
        hmTypeNameCash.put("SubAccType",
                           "select FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType where FSubAccTypeCode = '@' and FCheckState = 1");
        hmTypeNameCash.put("Cury", "select FCuryCode,FCuryName from " +
                           pub.yssGetTableName("Tb_Para_Currency") +
                           " where FCuryCode = '@' and FCheckState = 1");

        hmFieldCash = new HashMap();
        hmFieldCash.put("AccType", "FAccType");
        hmFieldCash.put("SubAccType", "FSubAccType");
        hmFieldCash.put("Cury", "FCuryCode");
        hmFieldCash.put("InvMgr", "FAnalysisCode1");

        hmFieldInvest = new HashMap();
        hmFieldInvest.put("PayType", "FPayType");
        hmFieldInvest.put("InvMgr", "FAnalysisCode1");
    }

    public void setInvestClassTable() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        HashMap hmIndex = new HashMap();
        HashMap hmData = new HashMap();
        String[] sValInvestDefineAry = null;
        YssTreeNode tNode = null;
        int iOrder = 1;
        String sOrderIndex = "";
        try {
            sValInvestDefineAry = valInvestDefine.split(";");
            createTmpTable("tb_Temp_GzClsInvest_");
            hmIndex.put("[root]", "001");
            strSql = "select b.FIVPayCatCode,FPayType,a.FAnalysisCode1 from " +
                pub.yssGetTableName("Tb_Stock_Invest") +
                " a left join (select FIVPayCatCode, FPayType from tb_base_investpaycat " +
                " where FCheckState = 1 ) b on a.FIVPayCatCode = b.FIVPayCatCode " +
                " where FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FCheckState = 1";
            if (invMgrCode != null && invMgrCode.length() > 0) {
                strSql = strSql + " and a.FAnalysisCode1=" +
                    dbl.sqlString(invMgrCode);
            }
            //chenyibo  20071003  如果有投资经理就在 where 条件中加上投资经理
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                for (int i = 0; i < sValInvestDefineAry.length; i++) {
                    tNode = new YssTreeNode();
                    tNode.setCode(builderCode(rs, i + 1, hmFieldInvest,
                                              this.valInvestDefine));
                    if (i == 0) {
                        tNode.setParentCode("[root]");
                    } else {
                        tNode.setParentCode(builderCode(rs, i, hmFieldInvest,
                            this.valInvestDefine));
                    }
                    tNode.setOrderCode( (String) hmIndex.get(tNode.getParentCode()));
                    if (!hmData.containsKey(tNode.getCode())) {
                        hmData.put(tNode.getCode(), tNode);
                        sOrderIndex = (String) hmIndex.get(tNode.getParentCode());
                        iOrder = Integer.parseInt(YssFun.right(sOrderIndex, 3));
                        hmIndex.put(tNode.getCode(), sOrderIndex + "001");
                        iOrder++;
                        sOrderIndex = sOrderIndex.substring(0,
                            sOrderIndex.length() - 3)
                            + YssFun.formatNumber(iOrder, "000");
                        hmIndex.put(tNode.getParentCode(), sOrderIndex);
                    }
                }
            }
            insertTempTable(hmData, "tb_Temp_GzClsInvest_");
        } catch (Exception e) {
            throw new YssException("生成运营收支树形结构临时表数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setCashClassTable() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        HashMap hmIndex = new HashMap();
        HashMap hmData = new HashMap();
        String[] sValCashDefineAry = null;
        YssTreeNode tNode = null;
        int iOrder = 1;
        String sOrderIndex = "";
        try {
            sValCashDefineAry = valCashDefine.split(";");
            createTmpTable("tb_Temp_GzClsCash_");
            hmIndex.put("[root]", "001");
            strSql = "select b.* ,a.FAnalysisCode1 from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                " a left join (select FCashAccCode,FAccType,FSubAccType,FCuryCode" +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " ) b on a.FCashAccCode = b.FCashAccCode " +
                " where a.FStorageDate = " + dbl.sqlDate(dDate) +
                " and a.FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and a.FPortCode = " + dbl.sqlString(portCode);
            if (invMgrCode != null && invMgrCode.length() > 0) {
                strSql = strSql + " and a.FAnalysisCode1=" +
                    dbl.sqlString(invMgrCode);
            }
            //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                for (int i = 0; i < sValCashDefineAry.length; i++) {
                    tNode = new YssTreeNode();
                    tNode.setCode(builderCode(rs, i + 1, hmFieldCash,
                                              this.valCashDefine));
                    if (i == 0) {
                        tNode.setParentCode("[root]");
                    } else {
                        tNode.setParentCode(builderCode(rs, i, hmFieldCash,
                            this.valCashDefine));
                    }
                    tNode.setOrderCode( (String) hmIndex.get(tNode.getParentCode()));
                    if (!hmData.containsKey(tNode.getCode())) {
                        hmData.put(tNode.getCode(), tNode);
                        sOrderIndex = (String) hmIndex.get(tNode.getParentCode());
                        iOrder = Integer.parseInt(YssFun.right(sOrderIndex, 3));
                        hmIndex.put(tNode.getCode(), sOrderIndex + "001");
                        iOrder++;
                        sOrderIndex = sOrderIndex.substring(0,
                            sOrderIndex.length() - 3)
                            + YssFun.formatNumber(iOrder, "000");
                        hmIndex.put(tNode.getParentCode(), sOrderIndex);
                    }
                }
            }
            insertTempTable(hmData, "tb_Temp_GzClsCash_");
        } catch (Exception e) {
            throw new YssException("生成现金树形结构临时表数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public void setSecClassTable() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        HashMap hmIndex = new HashMap();
        HashMap hmData = new HashMap();
        String[] svalSecDefineAry = null;
        YssTreeNode tNode = null;
        int iOrder = 1;
        String sOrderIndex = "";
        try {
            svalSecDefineAry = valSecDefine.split(";");
            createTmpTable("tb_Temp_GzClsSec_");
            hmIndex.put("[root]", "001");
            strSql = //"select b.* , a.FAnalysisCode1 from " +
                "select b.* , a.FAnalysisCode1,a.FAttrClsCode from " + //add a.FAttrClsCode sj 新的级别需要. 20071205
                pub.yssGetTableName("Tb_Stock_Security") +
                " a left join (select FSecurityCode,FCatCode,FSubCatCode,FCusCatCode,FTradeCury" +
                " from " + pub.yssGetTableName("Tb_Para_Security") +
                " ) b on a.FSecurityCode = b.FSecurityCode " +
                " where a.FStorageDate = " + dbl.sqlDate(dDate) +
                " and a.FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and a.FPortCode = " + dbl.sqlString(portCode);
            //需要加分析代码进行转换获取 fazmm20071003
            if (invMgrCode != null && invMgrCode.length() > 0) {
                strSql = strSql + " and a.FAnalysisCode1=" +
                    dbl.sqlString(invMgrCode);
                //chenyibo  20071003  如果有投资经理就在where 条件中加上投资经理
            }

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                for (int i = 0; i < svalSecDefineAry.length; i++) {
                    tNode = new YssTreeNode();
                    tNode.setCode(builderCode(rs, i + 1, hmFieldSec,
                                              this.valSecDefine));
                    if (i == 0) {
                        tNode.setParentCode("[root]");
                    } else {
                        tNode.setParentCode(builderCode(rs, i, hmFieldSec,
                            this.valSecDefine));
                    }
                    tNode.setOrderCode( (String) hmIndex.get(tNode.getParentCode()));
                    if (!hmData.containsKey(tNode.getCode())) {
                        hmData.put(tNode.getCode(), tNode);
                        sOrderIndex = (String) hmIndex.get(tNode.getParentCode());
                        iOrder = Integer.parseInt(YssFun.right(sOrderIndex, 3));
                        hmIndex.put(tNode.getCode(), sOrderIndex + "001");
                        iOrder++;
                        sOrderIndex = sOrderIndex.substring(0,
                            sOrderIndex.length() - 3)
                            + YssFun.formatNumber(iOrder, "000");
                        hmIndex.put(tNode.getParentCode(), sOrderIndex);
                    }
                }
            }
            insertTempTable(hmData, "tb_Temp_GzClsSec_");
        } catch (Exception e) {
            throw new YssException("生成证券树形结构临时表数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void createTmpTable(String sTableName) throws YssException {
        String strSql = "";
        try {
            if (dbl.yssTableExist(pub.yssGetTableName(sTableName +
                pub.getUserCode()))) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                               pub.yssGetTableName(sTableName +
                    pub.getUserCode())));
                /**end*/
            }
            strSql = "create table " +
                pub.yssGetTableName(sTableName + pub.getUserCode()) +
                " (FCode varchar(200)," +
                " FName varchar(50)," +
                " FParentCode varchar(200)," +
                " FOrderCode varchar(50))";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("生成临时表出错");
        }
    }

    public void insertTempTable(HashMap hmData, String sTableName) throws
        YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        PreparedStatement pstmt = null;
        YssTreeNode tNode = null;
        try {
            Iterator iter = hmData.values().iterator();
            conn.setAutoCommit(false);
            strSql = "insert into " +
                pub.yssGetTableName(sTableName + pub.getUserCode()) +
                " (FCode,FName,FParentCode,FOrderCode) values (?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);
            while (iter.hasNext()) {
                tNode = (YssTreeNode) iter.next();
                pstmt.setString(1, tNode.getCode());
                pstmt.setString(2, tNode.getName());
                pstmt.setString(3, tNode.getParentCode());
                pstmt.setString(4, tNode.getOrderCode());
                pstmt.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            throw new YssException("向临时表中插入数据出错");
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
    }

    /**
     * builderCode
     *
     * @param rs ResultSet
     * @param i int
     * @return String
     */
    private String builderCode(ResultSet rs, int idx, HashMap hmField,
                               String sDefine) throws YssException {
        String[] sDefineAry = null;
        String sField = "";
        StringBuffer buf = new StringBuffer();
        try {
            sDefineAry = sDefine.split(";");
            for (int i = 0; i < idx; i++) {
                sField = (String) hmField.get(sDefineAry[i]);
                buf.append(rs.getString(sField) + "").append("\f");
            }
            if (buf.length() > 0) {
                buf.setLength(buf.length() - 1);
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException();
        }
    }

    public void saveNetValue(String sPortCode, java.util.Date dDate) throws
        YssException {
        this.portCode = sPortCode;
        this.dDate = dDate;
        this.setRelaMap();
        bSaveNet = true;
        this.valReport = new ValuationRepBean();
        this.saveReport("");
    }

    /**
     * 按级别分别在项目代码前加上相应的个数的空字符，
     * 以便在前台显示时能够较为明晰的显示不同的级别。
     * sj add 20071205
     * @param sDefineAry String[]
     * @param sValueAry String[]
     * @param Type String
     * @throws YssException
     */
    public void setStrSpace(String[] sDefineAry,
                            String[] sValueAry, String Type) throws YssException {
        switch (sValueAry.length) {
            case 1:
                if (Type.equalsIgnoreCase("Sec")) {
                    valReport.setKeyCode(getSecSumRowTypeName(sDefineAry, sValueAry));
                } else if (Type.equalsIgnoreCase("Cash")) {
                    valReport.setKeyCode(getCashSumRowTypeName(sDefineAry, sValueAry));
                } else if (Type.equalsIgnoreCase("Invest")) {
                    valReport.setKeyCode(getInvestSumRowTypeName(sDefineAry,
                        sValueAry));
                }
                break;
            case 2:
                if (Type.equalsIgnoreCase("Sec")) {
                    valReport.setKeyCode(" " + " " + " " +
                                         getSecSumRowTypeName(sDefineAry, sValueAry));
                } else if (Type.equalsIgnoreCase("Cash")) {
                    valReport.setKeyCode(" " + " " + " " +
                                         getCashSumRowTypeName(sDefineAry, sValueAry));
                } else if (Type.equalsIgnoreCase("Invest")) {
                    valReport.setKeyCode(" " + " " + " " +
                                         getInvestSumRowTypeName(sDefineAry,
                        sValueAry));
                }
                break;
            case 3:
                if (Type.equalsIgnoreCase("Sec")) {
                    valReport.setKeyCode(" " + " " + " " + " " + " " +
                                         getSecSumRowTypeName(sDefineAry, sValueAry));
                } else if (Type.equalsIgnoreCase("Cash")) {
                    valReport.setKeyCode(" " + " " + " " + " " + " " +
                                         getCashSumRowTypeName(sDefineAry, sValueAry));
                } else if (Type.equalsIgnoreCase("Invest")) {
                    valReport.setKeyCode(" " + " " + " " + " " + " " +
                                         getInvestSumRowTypeName(sDefineAry,
                        sValueAry));
                }
                break;
            case 4:
                if (Type.equalsIgnoreCase("Sec")) {
                    valReport.setKeyCode(" " + " " + " " + " " + " " + " " + " " +
                                         getSecSumRowTypeName(sDefineAry, sValueAry));
                } else if (Type.equalsIgnoreCase("Cash")) {
                    valReport.setKeyCode(" " + " " + " " + " " + " " + " " + " " +
                                         getCashSumRowTypeName(sDefineAry, sValueAry));
                } else if (Type.equalsIgnoreCase("Invest")) {
                    valReport.setKeyCode(" " + " " + " " + " " + " " + " " + " " +
                                         getInvestSumRowTypeName(sDefineAry,
                        sValueAry));
                }
                break;
            default:
                if (Type.equalsIgnoreCase("Sec")) {
                    valReport.setKeyCode(getSecSumRowTypeName(sDefineAry, sValueAry));
                } else if (Type.equalsIgnoreCase("Cash")) {
                    valReport.setKeyCode(getCashSumRowTypeName(sDefineAry, sValueAry));
                } else if (Type.equalsIgnoreCase("Invest")) {
                    valReport.setKeyCode(getInvestSumRowTypeName(sDefineAry,
                        sValueAry));
                }
                break;
        }
    }

    /**
     * 估值增值 sj
     * @throws YssException
     * @return String
     */
    protected String buildTotalValRow() throws YssException {
        String sResult = "";
        valReport.setDefaultParam();
        valReport.setExtendNum(1);
        valReport.setKeyCode("估值增值");
        valReport.setCuryCode("合计：");
        ResultSet rs = null;
        double baseMVValue = 0;
        double portMMValue = 0;
        try {
            String strSql = "select * from " +
                pub.yssGetTableName("Tb_Data_NetValue")
                + " where FNavDate= " + dbl.sqlDate(dDate) +
                " and FPortCode=" + dbl.sqlString(portCode) +
                " and FType = '03' AND FInvMgrCode = ' '"; // lzp 20080112 add  字符型 加单引号  否则DB2不兼容 2008.07.16 蒋锦 添加投资经理作为条件
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                baseMVValue += rs.getDouble("FBaseNetValue"); //在表中有可能有多个基金经理,所以要累加. sj 20080228
                portMMValue += rs.getDouble("FPortNetValue");
            }
            valReport.setBaseMValue(baseMVValue); //基础货币成本
            valReport.setPortMValue(portMMValue);
            sResult = valReport.buildTotalValueRowStr();
            sResult = sResult + "\r\n";
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取估值增值数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 汇兑损益 sj
     * @throws YssException
     * @return String
     */
    protected String buildTotalExchangeRow() throws YssException {
        String sResult = "";
        valReport.setDefaultParam();
        valReport.setExtendNum(1);
        valReport.setKeyCode("汇兑损益");
        valReport.setCuryCode("合计：");
        ResultSet rs = null;
        double baseFXValue = 0;
        double portFXValue = 0;
        try {
            String strSql = "select * from " +
                pub.yssGetTableName("Tb_Data_NetValue")
                + " where FNavDate= " + dbl.sqlDate(dDate) +
                " and FPortCode=" + dbl.sqlString(portCode) +
                " and FType = '04' AND FInvMgrCode = ' '"; // lzp 20080112 add  字符型 加单引号  否则DB2不兼容 2008.07.16 蒋锦 添加投资经理作为条件
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                baseFXValue += rs.getDouble("FBaseNetValue"); //在表中有可能有多个基金经理,所以要累加. sj 20080228
                portFXValue += rs.getDouble("FPortNetValue");
            }
            valReport.setBaseMValue(baseFXValue); //基础货币成本
            valReport.setPortMValue(portFXValue);
            sResult = valReport.buildTotalValueRowStr();
            sResult = sResult + "\r\n";
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取汇兑损益数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 损益平准金（未实现） sj
     * @throws YssException
     * @return String
     */
    protected String buildTotalUnprofitRow() throws YssException {
        String sResult = "";
        valReport.setDefaultParam();
        valReport.setExtendNum(1);
        valReport.setKeyCode("损益平准金（未实现）");
        valReport.setCuryCode("合计：");
        ResultSet rs = null;
        double baseUnPLValue = 0;
        double portUnPLValue = 0;
        try {
            String strSql = "select * from " +
                pub.yssGetTableName("Tb_Data_NetValue")
                + " where FNavDate= " + dbl.sqlDate(dDate) +
                " and FPortCode=" + dbl.sqlString(portCode) +
                " and FType = '06' AND FInvMgrCode = ' '"; // lzp 20080112 add  字符型 加单引号  否则DB2不兼容 2008.07.16 蒋锦 添加投资经理作为条件
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                baseUnPLValue += rs.getDouble("FBaseNetValue"); //在表中有可能有多个基金经理,所以要累加. sj 20080228
                portUnPLValue += rs.getDouble("FPortNetValue");
            }
            valReport.setBaseMValue(baseUnPLValue); //基础货币成本
            valReport.setPortMValue(portUnPLValue);
            sResult = valReport.buildTotalValueRowStr();
            sResult = sResult + "\r\n";
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取损益平准金（未实现）数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 损益平准金（已实现） sj
     * @throws YssException
     * @return String
     */
    protected String buildTotalprofitRow() throws YssException {
        String sResult = "";
        valReport.setDefaultParam();
        valReport.setExtendNum(1);
        valReport.setKeyCode("损益平准金（已实现）");
        valReport.setCuryCode("合计：");
        ResultSet rs = null;
        double basePLValue = 0;
        double portPLValue = 0;
        try {
            String strSql = "select * from " +
                pub.yssGetTableName("Tb_Data_NetValue")
                + " where FNavDate= " + dbl.sqlDate(dDate) +
                " and FPortCode=" + dbl.sqlString(portCode) +
                " and FType = '07' AND FInvMgrCode = ' '"; // lzp 20080112 add  字符型 加单引号  否则DB2不兼容 2008.07.16 蒋锦 添加投资经理作为条件
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                basePLValue += rs.getDouble("FBaseNetValue"); //在表中有可能有多个基金经理,所以要累加. sj 20080228
                portPLValue += rs.getDouble("FPortNetValue");
            }
            valReport.setBaseMValue(basePLValue); //基础货币成本
            valReport.setPortMValue(portPLValue);
            sResult = valReport.buildTotalValueRowStr();
            sResult = sResult + "\r\n";
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取损益平准金（已实现）数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
    public String checkReportBeforeSearch(String sReportType){
    	return "";
    }

}
