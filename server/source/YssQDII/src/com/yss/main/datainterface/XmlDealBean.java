package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

public class XmlDealBean
    extends BaseDataSettingBean implements IDataSetting {

    public XmlDealBean() {
    }

    private String seqB = "";
    private String seqC = "";
    private String seqE = "";

    String[] arrSeqB = null;
    String[] arrSeqC = null;
    String[] arrSeqE = null;

    String fileType = "";

    private String num = ""; //交易数据流水号
    private String securityCode = ""; //交易证券代码
    private String securityName = ""; //交易证券名称
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String brokerCode = ""; //券商代码
    private String brokerName = ""; //券商名称
    private String invMgrCode = ""; //投资经理代码
    private String invMgrName = ""; //投资经理名称
    private String tradeCode = ""; //交易方式代码
    private String tradeName = ""; //交易方式名称
    private String cashAcctCode = ""; //现金帐户代码
    private String cashAcctName = ""; //现金帐户名称
    private String attrClsCode = ""; //所属分类代码
    private String attrClsName = ""; //所属分类名称
    private String bargainDate = "1900-01-01"; //成交日期
    private String bargainTime = "00:00:00"; //成交时间
    private String settleDate = "1900-01-01"; //结算日期
    private String settleTime = "00:00:00"; //结算时间
    private String autoSettle = "0"; //自动结算
    private double portCuryRate; //组合汇率
    private double baseCuryRate; //基础汇率
    private double tradeAmount; //交易数量
    private double tradePrice; //交易价格
    private double tradeMoney; //交易总额
    private double unitCost; //单位成本
    private double accruedInterest; //应计利息
    private double allotFactor; //分配因子
    private double totalCost; //投资总成本
    private String orderNum = ""; //订单代码
    private String desc = ""; //交易描述
    private double handAmount; //每手股数
    private double haveAmount; //持有股数/现金
    private String isOnlyColumn = ""; //是否只读取列名的标志
    private String tradeCuryCode = ""; //交易货币代码
    private String portCuryCode = ""; //组合货币代码
    private double factor; //报价因子
    private String fees = "";
    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() {
        return "";
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        return "";
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {

        int len = arrSeqB.length;
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        String tradeNum = "";
        String subNum = "";
        String curyCode = "";
        String[] seqBValue = null;
        String[] seqCValue = null;
        String[] seqEValue = null;
        try {
            Connection conn = dbl.loadConnection();
            if (this.fileType.equals("MT540") || this.fileType.equals("MT541")) {
                tradeCode = "01";
            } else if (this.fileType.equals("MT542") || this.fileType.equals("MT543")) {
                tradeCode = "02";
            }
            for (int i = 0; i < len; i++) {
                seqBValue = arrSeqB[i].split(",");
                bargainDate = seqBValue[0];
                settleDate = seqBValue[1];
                if (seqBValue[2].length() > 0) {
                    tradePrice = Double.parseDouble(seqBValue[2]);
                }
                securityCode = seqBValue[3];

                brokerCode = " ";
                portCode = " ";
                invMgrCode = " ";
                autoSettle = "0";

                BaseOperDeal operDeal = new BaseOperDeal();
                operDeal.setYssPub(pub);

                SecurityBean security = new SecurityBean();
                security.setSectorCode(securityCode);
                security.getSetting();

                portCuryRate = operDeal.getCuryRate(YssFun.toDate(bargainDate),
                    security.getTradeCuryCode(),
                    portCode, "Port");
                baseCuryRate = operDeal.getCuryRate(YssFun.toDate(bargainDate),
                    security.getTradeCuryCode(),
                    portCode, "Base");

                //----------------------------------------
                seqCValue = arrSeqC[i].split(",");
                if (seqCValue[0].length() > 0) {
                    tradeAmount = Double.parseDouble(seqCValue[0]);
                }
                if (seqCValue[1].length() > 0) {
                    tradeMoney = Double.parseDouble(arrSeqC[1]);
                }
                if (seqCValue[2].length() > 0) {
                    totalCost = Double.parseDouble(seqCValue[0]);
                }
                tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName(
                    "tb_data_trade"),
                                                  dbl.sqlRight("FNum", "6"),
                                                  "000000",
                                                  " where FNum like '"
                                                  + tradeNum.replaceAll("'", "''") +
                                                  "%'");
                subNum = tradeNum +
                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_SubTrade"),
                                           dbl.sqlRight("FNUM", 5), "00000",
                                           " where FNum like '"
                                           + this.num.replaceAll("'", "''") +
                                           "%'");
                strSql =
                    "insert into " + pub.yssGetTableName("Tb_Data_SubTrade") +
                    "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
                    " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
                    " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
                    " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST," +
                    " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                    " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
                    " FTotalCost, FOrderNum, FDesc, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser," +
                    " FFactCashAccCode,FFactSettleMoney,FExRate)" + //添加三个字段 by liyu
                    " values(" + dbl.sqlString(subNum) + "," +
                    dbl.sqlString(this.securityCode) + "," +
                    dbl.sqlString(this.portCode) + "," +
                    dbl.sqlString(this.brokerCode) + "," +
                    dbl.sqlString(this.invMgrCode) + "," +
                    dbl.sqlString(this.tradeCode) + "," +
                    dbl.sqlString(this.cashAcctCode.length() == 0 ? " " :
                                  this.cashAcctCode) + "," +
                    dbl.sqlString(this.attrClsCode) + "," +
                    dbl.sqlDate(this.bargainDate) + "," +
                    dbl.sqlString(this.bargainTime) + "," +
                    dbl.sqlDate(this.settleDate) + "," +
                    dbl.sqlString(this.settleTime) + "," +
                    this.autoSettle + "," +
                    this.portCuryRate + "," +
                    this.baseCuryRate + "," +
                    1 + "," +
                    1 + "," +
                    1 + "," +
                    this.tradeAmount + "," +
                    this.tradePrice + "," +
                    this.tradeMoney + "," +
                    this.accruedInterest + "," +
                    this.operSql.buildSaveFeesSql(YssCons.OP_ADD, this.fees) +
                    this.totalCost + "," +
                    dbl.sqlString(this.orderNum) + "," +
                    dbl.sqlString(this.desc) + ",0," +

                    0 + "" +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + "," +
                    dbl.sqlString(this.cashAcctCode.length() == 0 ? " " :
                                  this.cashAcctCode) + "," +
                    this.tradeMoney + "," +
                    1 + ")";
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);

            }
            return "";
        } catch (Exception e) {
            throw new YssException("");
        }
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     */
    public void delSetting() {
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() {
        return "";
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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
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
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
        String[] reqAry = null;
        String sTmpStr = "";

        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\f\f") >= 0) {
                seqB = sRowStr.split("\f\f")[0];
                seqC = sRowStr.split("\f\f")[1];
                seqE = sRowStr.split("\f\f")[2];

                arrSeqB = seqB.split("\r\t");
                arrSeqC = seqC.split("\r\t");
                arrSeqE = seqE.split("\r\t");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
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

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }
}
