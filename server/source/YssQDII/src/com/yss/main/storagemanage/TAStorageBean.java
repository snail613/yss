package com.yss.main.storagemanage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.parasetting.AssetStorageCfgBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class TAStorageBean
    extends BaseDataSettingBean implements IDataSetting {
    private String portClsCode = "";
    private String portClsName = "";
    private String oldportClsCode = "";

    private String yearMonth = "";
    private String oldYearMonth = "";

    private java.util.Date storageDate;
    private java.util.Date oldStorageDate;
    private java.util.Date storageInitDate; //库存初始日期
    private String portCode = "";
    private String oldPortCode = "";

    private String portName = "";
    private String curyCode = "";
    private String curyName = "";
    private double storageAmount;//库存数量
    private double cost; //原币成本
    private double portCuryRate;
    private double portCuryCost;
    private double baseCuryRate;
    private double baseCuryCost;
    private double portCuryUnpl; //组合货币未实现损益平准金
    private double baseCuryUnpl;
    private double portCuryPl; //组合货币损益平准金
    private double baseCuryPl;
    private double storageInd;
    private String analysisCode1 = "";
    private String analysisName1 = "";
    private String analysisCode2 = "";
    private String analysisName2 = "";
    private String analysisCode3 = "";
    private String analysisName3 = "";

    private String oldAnalysisCode1 = "";
    private String oldAnalysisCode2 = "";
    private String oldAnalysisCode3 = "";

    private String strIsOnlyColumns = "0";
    private String sRecycled = "";

    private String bBegin = "false"; //是否取期初数

    private double curyUnPl;
    private double curyPl;
    private double convertMoney=0;//TA份额折算金额  20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
    
    /**shashijie 2011-10-19 STORY 1589 */
    private double FPortStorageAmount = 0;//本位币库存数量
    /**end*/
    
    private double clsPortConvertStorageAmount = 0; //分级组合折算后的库存数量
    
    public double getClsPortStorageAmount() {
		return clsPortConvertStorageAmount;
	}

	public void setClsPortStorageAmount(double clsPortConvertStorageAmount) {
		this.clsPortConvertStorageAmount = clsPortConvertStorageAmount;
	}

	private TAStorageBean filterType;

    public String getCuryName() {
        return curyName;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortClsCode() {
        return portClsCode;
    }

    public double getStorageInd() {
        return storageInd;
    }

    public TAStorageBean getFilterType() {
        return filterType;
    }

    public double getBaseCuryPl() {
        return baseCuryPl;
    }

    public double getPortCuryUnpl() {
        return portCuryUnpl;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public java.util.Date getStorageDate() {
        return storageDate;
    }

    public double getPortCuryPl() {
        return portCuryPl;
    }

    public double getBaseCuryUnpl() {
        return baseCuryUnpl;
    }

    public double getPortCuryCost() {
        return portCuryCost;
    }

    public double getCost() {
        return cost;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public double getStorageAmount() {
        return storageAmount;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public double getBaseCuryCost() {
        return baseCuryCost;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortClsName(String portClsName) {
        this.portClsName = portClsName;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPortClsCode(String portClsCode) {
        this.portClsCode = portClsCode;
    }

    public void setStorageInd(double storageInd) {
        this.storageInd = storageInd;
    }

    public void setFilterType(TAStorageBean filterType) {
        this.filterType = filterType;
    }

    public void setBaseCuryPl(double baseCuryPl) {
        this.baseCuryPl = baseCuryPl;
    }

    public void setPortCuryUnpl(double portCuryUnpl) {
        this.portCuryUnpl = portCuryUnpl;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setStorageDate(Date storageDate) {
        this.storageDate = storageDate;
    }

    public void setPortCuryPl(double portCuryPl) {
        this.portCuryPl = portCuryPl;
    }

    public void setBaseCuryUnpl(double baseCuryUnpl) {
        this.baseCuryUnpl = baseCuryUnpl;
    }

    public void setPortCuryCost(double portCuryCost) {
        this.portCuryCost = portCuryCost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setStorageAmount(double storageAmount) {
        this.storageAmount = storageAmount;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public void setBaseCuryCost(double baseCuryCost) {
        this.baseCuryCost = baseCuryCost;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setStorageDate(java.util.Date storageDate) {
        this.storageDate = storageDate;
    }
    //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
    public void setOldPortClsCode(String oldportClsCode) {
        this.oldportClsCode = oldportClsCode;
    }

    public void setOldYearMonth(String oldYearMonth) {
        this.oldYearMonth = oldYearMonth;
    }

    public void setOldStorageDate(Date oldStorageDate) {
        this.oldStorageDate = oldStorageDate;
    }

    public void setOldStorageDate(java.util.Date oldStorageDate) {
        this.oldStorageDate = oldStorageDate;
    }

    public void setAnalysisCode3(String analysisCode3) {
        this.analysisCode3 = analysisCode3;
    }

    public void setAnalysisCode2(String analysisCode2) {
        this.analysisCode2 = analysisCode2;
    }

    public void setAnalysisCode1(String analysisCode1) {
        this.analysisCode1 = analysisCode1;
    }

    public void setBBegin(String bBegin) {
        this.bBegin = bBegin;
    }

    public void setAnalysisName2(String analysisName2) {
        this.analysisName2 = analysisName2;
    }

    public void setAnalysisName3(String analysisName3) {
        this.analysisName3 = analysisName3;
    }

    public void setAnalysisName1(String analysisName1) {
        this.analysisName1 = analysisName1;
    }

    public void setOldAnalysisCode1(String oldAnalysisCode1) {
        this.oldAnalysisCode1 = oldAnalysisCode1;
    }

    public void setOldAnalysisCode3(String oldAnalysisCode3) {
        this.oldAnalysisCode3 = oldAnalysisCode3;
    }

    public void setOldAnalysisCode2(String oldAnalysisCode2) {
        this.oldAnalysisCode2 = oldAnalysisCode2;
    }

    public void setStrIsOnlyColumns(String strIsOnlyColumns) {
        this.strIsOnlyColumns = strIsOnlyColumns;
    }

    public void setStorageInitDate(Date storageInitDate) {
        this.storageInitDate = storageInitDate;
    }

    public void setStorageInitDate(java.util.Date storageInitDate) {
        this.storageInitDate = storageInitDate;
    }

    public void setCuryUnPl(double curyUnPl) {
        this.curyUnPl = curyUnPl;
    }

    public void setCuryPl(double curyPl) {
        this.curyPl = curyPl;
    }
    /**
     * xuqiji 20091015 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @param convertMoney double
     */
    public void setConvertMoney(double convertMoney) {
        this.convertMoney = convertMoney;
    }

    public String getPortClsName() {
        return portClsName;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getOldSellNetCode() {
        return oldportClsCode;
    }

    public String getOldYearMonth() {
        return oldYearMonth;
    }

    public java.util.Date getOldStorageDate() {
        return oldStorageDate;
    }

    public String getAnalysisCode3() {
        return analysisCode3;
    }

    public String getAnalysisCode2() {
        return analysisCode2;
    }

    public String getAnalysisCode1() {
        return analysisCode1;
    }

    public String getBBegin() {
        return bBegin;
    }

    public String getAnalysisName2() {
        return analysisName2;
    }

    public String getAnalysisName3() {
        return analysisName3;
    }

    public String getAnalysisName1() {
        return analysisName1;
    }

    public String getOldAnalysisCode1() {
        return oldAnalysisCode1;
    }

    public String getOldAnalysisCode3() {
        return oldAnalysisCode3;
    }

    public String getOldAnalysisCode2() {
        return oldAnalysisCode2;
    }

    public String getStrIsOnlyColumns() {
        return strIsOnlyColumns;
    }

    public java.util.Date getStorageInitDate() {
        return storageInitDate;
    }

    public double getCuryUnPl() {
        return curyUnPl;
    }

    public double getCuryPl() {
        return curyPl;
    }
    /**
     * xuqiji 20091015 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @return double
     */
    public double getConvertMoney() {
        return convertMoney;
    }

    public TAStorageBean() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            this.sRecycled = sRowStr;

            reqAry = sTmpStr.split("\t");
            this.portClsCode = reqAry[0];
            this.yearMonth = reqAry[1];
            if (YssFun.isDate(reqAry[2])) {
                this.storageDate = YssFun.parseDate(reqAry[2], "yyyy-MM-dd");
            }
            this.portCode = reqAry[3];
            this.curyCode = reqAry[4];
            if (reqAry[5].length() != 0) {
                this.storageAmount = YssFun.toDouble(reqAry[5]);
            }
            if (reqAry[6].length() != 0) {
                this.cost = YssFun.toDouble(reqAry[6]);
            }
            if (reqAry[7].length() != 0) {
                this.portCuryRate = YssFun.toDouble(reqAry[7]);
            }
            if (reqAry[8].length() != 0) {
                this.portCuryCost = YssFun.toDouble(reqAry[8]);
            }
            if (reqAry[9].length() != 0) {
                this.baseCuryRate = YssFun.toDouble(reqAry[9]);
            }
            if (reqAry[10].length() != 0) {
                this.baseCuryCost = YssFun.toDouble(reqAry[10]);
            }
            if (reqAry[11].length() != 0) {
                this.portCuryUnpl = YssFun.toDouble(reqAry[11]);
            }
            if (reqAry[12].length() != 0) {
                this.baseCuryUnpl = YssFun.toDouble(reqAry[12]);
            }
            if (reqAry[13].length() != 0) {
                this.portCuryPl = YssFun.toDouble(reqAry[13]);
            }
            if (reqAry[14].length() != 0) {
                this.baseCuryPl = YssFun.toDouble(reqAry[14]);
            }
            this.analysisCode1 = reqAry[15];
            this.analysisCode2 = reqAry[16];
            this.analysisCode3 = reqAry[17];
            this.checkStateId = YssFun.toInt(reqAry[18]);
            this.oldportClsCode = reqAry[19];
            this.oldYearMonth = reqAry[20];
            if (YssFun.isDate(reqAry[21])) {
                this.oldStorageDate = YssFun.parseDate(reqAry[21], "yyyy-MM-dd");
            }
            this.oldPortCode = reqAry[22];
            this.strIsOnlyColumns = reqAry[23];
            this.bBegin = reqAry[24];
            if (YssFun.isDate(reqAry[25])) {
                this.storageInitDate = YssFun.parseDate(reqAry[25], "yyyy-MM-dd");
            }
            this.oldAnalysisCode1 = reqAry[26];
            this.oldAnalysisCode2 = reqAry[27];
            this.oldAnalysisCode3 = reqAry[28];
            this.curyUnPl = YssFun.toDouble(reqAry[29]);
            this.curyPl = YssFun.toDouble(reqAry[30]);
            /**shashijie 2011-10-19 STORY 1589 */
            if (reqAry[31]!=null && reqAry[31].trim().length()>0) {
            	this.FPortStorageAmount = YssFun.toDouble(reqAry[31]);
			}
            /**end*/
            if (reqAry[32]!=null && reqAry[32].trim().length()>0) {
            	this.clsPortConvertStorageAmount = YssFun.toDouble(reqAry[32]);
			}
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TAStorageBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析TA库存请求信息出错", e);
        }
    }

    /**
     * buildRowStr
     * 返回属性信息
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.portClsCode).append("\t");
        buf.append(this.portClsName).append("\t");
        buf.append(this.yearMonth).append("\t");
        if (this.storageDate != null) {
            buf.append(YssFun.formatDate(this.storageDate)).append("\t");
        } else {
            buf.append("").append("\t");
        }
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.curyCode).append("\t");
        buf.append(this.curyName).append("\t");
        buf.append(this.storageAmount).append("\t");
        buf.append(this.cost).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(this.portCuryCost).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.baseCuryCost).append("\t");
        buf.append(this.portCuryUnpl).append("\t");
        buf.append(this.baseCuryUnpl).append("\t");
        buf.append(this.portCuryPl).append("\t");
        buf.append(this.baseCuryPl).append("\t");
        buf.append(this.analysisCode1).append("\t");
        buf.append(this.analysisName1).append("\t");
        buf.append(this.analysisCode2).append("\t");
        buf.append(this.analysisName2).append("\t");
        buf.append(this.analysisCode3).append("\t");
        buf.append(this.analysisName3).append("\t");
        buf.append(this.bBegin).append("\t");
        buf.append(this.storageInitDate).append("\t");
        buf.append(this.curyUnPl).append("\t");
        buf.append(this.curyPl).append("\t");
        /**shashijie 2011-10-19 STORY 1589 */
        buf.append(this.FPortStorageAmount).append("\t");
        /**end*/
        buf.append(this.clsPortConvertStorageAmount).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 新增一条现TA存信息
     * @throws YssException
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_TA") + "(" +
                "FPortClsCode,FYearMonth,FStorageDate,FPortCode,FCuryCode,FStorageAmount,FCost,FPortCuryRate" +
                ",FPortCuryCost,FBaseCuryRate,FBaseCuryCost,FPortCuryUnpl,FPortCuryPl,FBaseCuryUnpl,FBaseCuryPl" +
                ",FCuryUnpl,FCurypl,FStorageInd,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCheckState,FCreator," +
                "FCreateTime,FCheckUser" +
                /**shashijie 2011-10-19 STORY 1589 */
                ",FPortStorageAmount "+
                /**end*/
                ",FFJZHZSSTORAGEAMOUNT"+//story 2254 add by zhouwei 20120227 折算后库存
                ") values(" +
                dbl.sqlString(this.portClsCode) + "," +
                (this.bBegin.equalsIgnoreCase("false") ?
                 dbl.sqlString(YssFun.formatDate(this.storageDate, "yyyyMM")) :
                 dbl.sqlString(this.yearMonth))
                + "," +
                dbl.sqlDate(this.storageDate) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.curyCode) + "," +
                this.storageAmount + "," +
                this.cost + "," +
                this.portCuryRate + "," +
                this.portCuryCost + "," +
                this.baseCuryRate + "," +
                this.baseCuryCost + "," +
                this.portCuryUnpl + "," +
                this.portCuryPl + "," +
                this.baseCuryUnpl + "," +
                this.baseCuryPl + "," +
                this.curyUnPl + "," +
                this.curyPl + "," +
                0 + "," +
                (this.analysisCode1.length() == 0 ? dbl.sqlString(" ") :
                 dbl.sqlString(this.analysisCode1)) + "," +
                (this.analysisCode2.length() == 0 ? dbl.sqlString(" ") :
                 dbl.sqlString(this.analysisCode2)) + "," +
                (this.analysisCode3.length() == 0 ? dbl.sqlString(" ") :
                 dbl.sqlString(this.analysisCode3)) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) +
                ", " + dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                 /**shashijie 2011-10-19 STORY 1589 */
                 ","+this.FPortStorageAmount+
                 /**end*/
                 ","+this.clsPortConvertStorageAmount+
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";

        } catch (Exception e) {
            throw new YssException("新增TA库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkInput
     * 数据验证
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        //xuqiji 20090603:QDV4中保2009年05月26日01_B  MS00469 添加现金库存时因没有Yearmonth字段导致判断条件不满足而无法增加数据
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Stock_TA"),
                               "FPortClsCode,FYearMonth,FStorageDate,FAnalysisCode1,FAnalysisCode2,FPortCode",
                               //新值
                               this.portClsCode + "," +
                               (this.bBegin.equalsIgnoreCase("false") ? YssFun.formatDate(this.storageDate, "yyyyMM") : this.yearMonth) + "," +
                               YssFun.formatDate(this.storageDate, "yyyy-MM-dd") + "," +
                               //---edit by songjie 2011.05.30 BUG 1990 QDV4赢时胜(测试)2011年5月26日01_B---//
                               (this.analysisCode1.length() == 0 ? " " : this.analysisCode1)  + "," +
                               (this.analysisCode2.length() == 0 ? " " : this.analysisCode2)  + "," +
                               //---edit by songjie 2011.05.30 BUG 1990 QDV4赢时胜(测试)2011年5月26日01_B---//
                               this.portCode,
                               //原始值
                               this.oldportClsCode + "," +
                               (this.oldYearMonth.equals("") ? YssFun.formatDate(this.oldStorageDate, "yyyyMM") : this.oldYearMonth) + "," +
                               YssFun.formatDate( (this.oldStorageDate == null ? YssFun.parseDate("1900-01-01") : this.oldStorageDate), "yyyy-MM-dd")
                               //---edit by songjie 2011.05.30 BUG 1990 QDV4赢时胜(测试)2011年5月26日01_B---//
                               + "," + (this.oldAnalysisCode1.length() == 0 ? " " : this.oldAnalysisCode1) 
                               + "," + (this.oldAnalysisCode2.length() == 0 ? " " : this.oldAnalysisCode2)
                               //---edit by songjie 2011.05.30 BUG 1990 QDV4赢时胜(测试)2011年5月26日01_B---//
                               + "," + this.oldPortCode);
        //----------------------------------------end------------------------------------------------------------//
    }

    /**
     * checkSetting
     * 功能：可以处理库存管理业务中TA库存的设置审核、反审核、和回收站的还原功能。
     *      还原功能可以同时处理多条信息
     * @throws YssException
     * 修改时间：2008年6月11号
     * 修改人：蒋春
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] arrData = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Stock_TA") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                        " and FYearMonth = " +
                        (this.bBegin.equalsIgnoreCase("false") ?
                         dbl.sqlString(YssFun.formatDate(this.
                        storageDate, "yyyyMM")) :
                         dbl.sqlString(this.yearMonth)) +
                        " and FStorageDate = " + dbl.sqlDate(this.storageDate) +
                        " and FPortCode = " + dbl.sqlString(this.portCode) +
                        (this.analysisCode1.length() == 0 ? "" :
                         " and FAnalysisCode1 = " +
                         dbl.sqlString(this.analysisCode1)) +
                        (this.analysisCode2.length() == 0 ? "" :
                         " and FAnalysisCode2 = " +
                         dbl.sqlString(this.analysisCode2)) +
                        (this.analysisCode3.length() == 0 ? "" :
                         " and FAnalysisCode3 = " +
                         dbl.sqlString(this.analysisCode3));

                    dbl.executeSql(strSql);
                }
            } else {
                if (this.portClsCode != null &&
                    this.portClsCode.trim().length() > 0) {
                    strSql = "update " + pub.yssGetTableName("Tb_Stock_TA") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                        " and FYearMonth = " +
                        (this.bBegin.equalsIgnoreCase("false") ?
                         dbl.sqlString(YssFun.formatDate(this.
                        storageDate, "yyyyMM")) :
                         dbl.sqlString(this.yearMonth)) +
                        " and FStorageDate = " + dbl.sqlDate(this.storageDate) +
                        " and FPortCode = " + dbl.sqlString(this.portCode) +
                        (this.analysisCode1.length() == 0 ? "" :
                         " and FAnalysisCode1 = " +
                         dbl.sqlString(this.analysisCode1)) +
                        (this.analysisCode2.length() == 0 ? "" :
                         " and FAnalysisCode2 = " +
                         dbl.sqlString(this.analysisCode2)) +
                        (this.analysisCode3.length() == 0 ? "" :
                         " and FAnalysisCode3 = " +
                         dbl.sqlString(this.analysisCode3));

                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核TA库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 删除一条TA库存信息
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Stock_TA") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                " and FYearMonth = " + (this.bBegin.equalsIgnoreCase("false") ?
                                        dbl.sqlString(YssFun.formatDate(this.
                storageDate, "yyyyMM")) :
                                        dbl.sqlString(this.yearMonth)) +
                " and FStorageDate = " + dbl.sqlDate(this.storageDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                (this.analysisCode1.length() == 0 ? "" :
                 " and FAnalysisCode1 = " + dbl.sqlString(this.analysisCode1)) +
                (this.analysisCode2.length() == 0 ? "" :
                 " and FAnalysisCode2 = " + dbl.sqlString(this.analysisCode2)) +
                (this.analysisCode3.length() == 0 ? "" :
                 " and FAnalysisCode3 = " + dbl.sqlString(this.analysisCode3));
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除TA库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editOperData
     * 编辑一条现金库存信息
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Stock_TA") +
                " set FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                " ,FYearMonth = " + (this.bBegin.equalsIgnoreCase("false") ?
                                     dbl.sqlString(YssFun.formatDate(this.
                storageDate, "yyyyMM")) :
                                     dbl.sqlString(this.yearMonth)) +
                " ,FStorageDate = " + dbl.sqlDate(this.storageDate) +
                " ,FPortCode = " + dbl.sqlString(this.portCode) +
                " ,FCuryCode = " + dbl.sqlString(this.curyCode) +
                " ,FStorageAmount = " + this.storageAmount +
                " ,FCost = " + this.cost +
                " ,FPortCuryRate = " + this.portCuryRate +
                " ,FPortCuryCost = " + this.portCuryCost +
                " ,FBaseCuryRate = " + this.baseCuryRate +
                " ,FBaseCuryCost = " + this.baseCuryCost +
                " ,FPortCuryUnpl = " + this.portCuryUnpl +
                " ,FPortCuryPl = " + this.portCuryPl +
                " ,FBaseCuryUnpl = " + this.baseCuryUnpl +
                " ,FBaseCuryPl = " + this.baseCuryPl +
                " ,FCuryUnpl = " + this.curyUnPl +
                " ,FCurypl = " + this.curyPl +
                " ,FAnalysisCode1 = " +
                (this.analysisCode1.length() == 0 ? dbl.sqlString(" ") :
                 dbl.sqlString(this.analysisCode1)) +
                " ,FAnalysisCode2 = " +
                (this.analysisCode2.length() == 0 ? dbl.sqlString(" ") :
                 dbl.sqlString(this.analysisCode2)) +
                " ,FAnalysisCode3 = " +
                (this.analysisCode3.length() == 0 ? dbl.sqlString(" ") :
                 dbl.sqlString(this.analysisCode3)) +
                " ,FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                " ,FCreator = "
                + dbl.sqlString(this.creatorCode) +
                " ,FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                " ,FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                dbl.sqlString(this.creatorCode)) +
                /**shashijie 2011-10-19 STORY 1589 */
                " ,FPortStorageAmount = "+this.FPortStorageAmount+
                /**end*/
                " ,FFJZHZSSTORAGEAMOUNT="+this.clsPortConvertStorageAmount+
                " where FPortClsCode = " + dbl.sqlString(this.oldportClsCode) +
                " and FYearMonth = " + dbl.sqlString(this.oldYearMonth) + //modified libo 20090514 针对TA库存进行操作时期初数复选框的功能存在缺陷    QDV4赢时胜（上海）2009年5月7日01_B
                " and FStorageDate = " + dbl.sqlDate(this.oldStorageDate) +
                " and FPortCode = " + dbl.sqlString(this.oldPortCode) +
                (this.oldAnalysisCode1.length() == 0 ? " " :
                 " and FAnalysisCode1 = " + dbl.sqlString(this.oldAnalysisCode1)) +
                (this.oldAnalysisCode2.length() == 0 ? " " :
                 " and FAnalysisCode2 = " + dbl.sqlString(this.oldAnalysisCode2)) +
                (this.oldAnalysisCode3.length() == 0 ? " " :
                 " and FAnalysisCode3 = " + dbl.sqlString(this.oldAnalysisCode3));
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改TA库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException, SQLException,
        YssException {
        String sResult = "";

        boolean analy1 = operSql.storageAnalysis("FAnalysisCode1",
                                                 YssOperCons.YSS_KCLX_TA); //判断分析代码存不存在
        boolean analy2 = operSql.storageAnalysis("FAnalysisCode2",
                                                 YssOperCons.YSS_KCLX_TA);

        if (this.filterType != null) {
            //调整期初字段的筛选，期初查询所有；否则根据库存日期查询
            //modified libo 20090514 针对TA QDV4赢时胜（上海）2009年5月7日01_B
            if (this.filterType.bBegin.equalsIgnoreCase("false")) {
//                sResult = " where 1=1 and " + dbl.sqlRight("FYearMonth", 2) + " <> '00'" +
//                    " and a.FstorageDate = " + dbl.sqlDate(filterType.storageDate);
                //MS00672 QDV4赢时胜上海2009年9月03日02_B fanghaoln 20090910
                String sscreat = " where 1=1 and " + dbl.sqlRight("FYearMonth", 2) + " <> '00'"; //把这个SQL语句分开是因为语句里面有判断加在一起会出错
                //当前台没有选择库存日期的时候后台不加上库存日期的查询条件
                //fanghaoln 20091203 MS00843 QDV4赢时胜上海2009年12月1日02_B 修改查询条件
                String sscreat1 = filterType.storageDate.toString().equalsIgnoreCase("Tue Dec 31 00:00:00 CST 2999") ? " " : " and a.FstorageDate = " + dbl.sqlDate(filterType.storageDate);
                sResult = sscreat + sscreat1;
                //======================================================================================================================
            } else if (this.filterType.bBegin.equalsIgnoreCase("true")) {
                //                sResult = " where 1=1 and a.FYearMonth = '" +
//                    YssFun.formatDate(this.filterType.storageDate, "yyyy-MM-dd").substring(0, 4) + "00'";
                //MS00672 QDV4赢时胜上海2009年9月03日02_B fanghaoln 20090910
            	String sscreat = " where 1=1 and " + dbl.sqlRight("FYearMonth", 2) + " = '00'"; //把这个SQL语句分开是因为语句里面有判断加在一起会出错
            	String sscreat1 = filterType.storageDate.toString().equalsIgnoreCase("Tue Dec 31 00:00:00 CST 2999") ? " " : " and a.FstorageDate = " + dbl.sqlDate(filterType.storageDate);
                sResult = sscreat + sscreat1;
                //-----------------------------------------------------end MS00843------------------------------------------------------------------------------
                //================================================End MS00672==================================================================================
            }
            //========================End MS00435================================
            if (this.filterType.strIsOnlyColumns.equals("0")) {
                sResult = sResult + " and 1 = 2";
                return sResult;
            }
          //MS00765 QDV4赢时胜（上海）2009年10月28日02_B  fanghaoln 20091030
            if (this.filterType.portClsCode.length() != 0) {
                sResult = sResult + " and a.FportClsCode like '" +//增加一个筛选条件组合分级代码
                    filterType.portClsCode.replaceAll("'", "''") + "%'";
            }
          //=================================End MS00765 =================
            if (this.filterType.portCode.length() != 0) {
                sResult = sResult + " and a.FportCode like '" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.curyCode.length() != 0) {
                sResult = sResult + " and a.FcuryCode like '" +
                    filterType.curyCode.replaceAll("'", "''") + "%'";
            }
            if (analy1) {
                if (this.filterType.analysisCode1.length() != 0) {
                    sResult = sResult + " and a.FanalysisCode1 like '" +
                        filterType.analysisCode1.replaceAll("'", "''") + "%'";
                }
            }
            if (analy2) {
                if (this.filterType.analysisCode2.length() != 0) {
                    sResult = sResult + " and a.FanalysisCode2 like '" +
                        filterType.analysisCode2.replaceAll("'", "''") + "%'";
                }
            }
            /**shashijie 2011-10-19 STORY 1589 */
            if (this.filterType.FPortStorageAmount != 0) {
				sResult += " and a.FPortStorageAmount = "+this.FPortStorageAmount;
			}
            /**end*/
            
        }
        return sResult;
    }

    /**
     * 获取辅助字段之查询Sql语句
     * @return String
     */
    public String FilterSql() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_TA);
        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                 /*       " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                        i +
                        " from  (select FBrokerCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_broker") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FBrokerCode )x " +
                        " join (select * from " +
                        pub.yssGetTableName("tb_para_broker") + ") y on x.FBrokerCode = y.FBrokerCode and x.FStartDate = y.FStartDate) broker on a.FAnalysisCode" +
                        i + " = broker.FBrokerCode";*/
                    
                    " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                    i +
                    " from  " +
                    pub.yssGetTableName("tb_para_broker") +
                    " y  where y.FCheckState = 1 ) broker on a.FAnalysisCode" +
                    i + " = broker.FBrokerCode";
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("003")) {
                    sResult = sResult +
                        " left join (select FExchangeCode,FExchangeName as FAnalysisName" +
                        i +
                        " from tb_base_exchange) e on a.FAnalysisCode" + i +
                        " = e.FExchangeCode " +
                     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                     /*   " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        " join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) exchange on a.FAnalysisCode" +
                        i + " = exchange.FInvMgrCode";*/
                        
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " n where n.FCheckState = 1 ) exchange on a.FAnalysisCode" +
                        i + " = exchange.FInvMgrCode";
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("004")) {
                    sResult = sResult +
                        " left join (select FCatCode,FCatName as FAnalysisName2 from Tb_Base_Category where FCheckState = 1) category on a.FAnalysisCode" +
                        i + " = category.FCatCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                    
                      /*  " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                        i +
                        "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        "join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr on a.FAnalysisCode" +
                        i + " = invmgr.FInvMgrCode ";*/
                    
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                    i +
                    "  from  " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where n.FCheckState = 1 ) invmgr on a.FAnalysisCode" +
                    i + " = invmgr.FInvMgrCode ";
                    
                    
                    //end by lidaolong
                }

                else {
                    sResult = sResult +
                        " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                        i + ".FAnalysisNull ";
                }
            }
        }

        //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        dbl.closeResultSetFinal(rs);

        return sResult;
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        String sAry[] = null;
        try {
            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_TA); //获得分析代码
            strSql = "select y.* from " +
                "(select FPortClsCode,FYearMonth,FStorageDate,FPortCode from " +
                pub.yssGetTableName("Tb_Stock_TA") +
                " group by FPortclsCode,FYearMonth,FStorageDate,FPortCode) x join " +
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName" +
                ",c.FPortName as FPortName,d.FCuryName as FCuryName,e.FPortClsName as FPortClsName,f.FStorageInitDate as FStorageInitDate " +
                
                //edited by zhouxiang MS01554 纽约银行数据接口，选择交易所再重新选择时，已选的又返回备选项中 
                (sAry[0].length() == 0 ? ", ' ' as FInvMgrName" : sAry[0]) +
                (sAry[1].length() == 0 ? ", ' ' as FCatName" : "") +
                //end    by zhouxiang MS01554 纽约银行数据接口，选择交易所再重新选择时，已选的又返回备选项中 
                
                " from " + pub.yssGetTableName("Tb_Stock_TA") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList ) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList ) c on a.FCheckUser = c.FUserCode" +
                " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState =1 " + //xuqiji 20100409 MS01073 QDV4华夏2010年4月07日01_B 当日的政权持仓和TA持仓重复 
                ") c on a.FPortCode = c.FPortCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ") d on a.FCuryCode = d.FCuryCode " +
                " left join (select distinct(FPortClsCode) as FPortClsCode,FPortClsName from " +
                pub.yssGetTableName("Tb_TA_PortCls") +
                ") e on a.FPortClsCode = e.FPortClsCode " +
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
            /*    " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName,o.FStorageInitDate as FStorageInitDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " o join " +
                "(select FPortCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
                //fanghaoln 20100330 MS01051 QDV4赢时胜(测试)2010年03月23日02_AB
                " where FStartDate <= " + dbl.sqlDate(filterType.storageInitDate) +
                //------------------------end -----MS01051------------------------------
                " and FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
                " group by FPortCode) p " +
                //----xuqiji 20100409 MS01073 QDV4华夏2010年4月07日01_B 当日的政权持仓和TA持仓重复------------------//
                " on o.FPortCode = p.FPortCode and o.FStartDate = p.FStartDate"+ " where o.FCheckState =1 " +  
                ") f on a.FPortCode = f.FPortCode" +
               */
                
                " left join (select FPortCode , FPortName, FStorageInitDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +   
                " where  FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +           
                ") f on a.FPortCode = f.FPortCode" +
               
                //end by lidaolong
                //-------------------------------------end--------------------------------------//
                sAry[1] +
                buildFilterSql() +
                ") y on y.FPortClsCode = x.FPortClsCode and y.FYearMonth = x.FYearMonth and y.FStorageDate = x.FStorageDate and y.FPortCode = x.FPortCode" +
                " order by y.FStorageDate,y.FPortClsCode,y.FYearMonth,y.FPortCode";
        } catch (Exception e) {
            throw new YssException("获取TA库存信息出错 \r\n" + e.getMessage(), e);
        }
        return builderListViewData(strSql);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sDateStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            
            //Adding by Story #1506
            //==============================
            if (this.filterType.strIsOnlyColumns.equals("0")) {
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                       this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + 
                       sDateStr + "\r\f";
            }
            //=============end=================
            if (strSql.length() != 0) {
            	
                //Modifying of Story #1506
                //==========================
                //rs = dbl.openResultSet(strSql);    
                yssPageInationBean.setsQuerySQL(strSql);
                yssPageInationBean.setsTableName("TA");
                rs =dbl.openResultSet(yssPageInationBean);
                //============end==============
                
                while (rs.next()) {
                    bufShow.append(super.buildRowShowStr(rs,
                        this.getListView1ShowCols())).
                        append(YssCons.YSS_LINESPLITMARK);
                    setResultSetAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            }
            //Adding of Story #1506
            //================================
            AssetStorageCfgBean assetgroupcfg = new AssetStorageCfgBean();
            assetgroupcfg.setYssPub(pub);
            sDateStr = assetgroupcfg.getPartSetting("TA");
            //=============end===================
            
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            /* VocabularyBean vocabulary = new VocabularyBean();
             vocabulary.setYssPub(pub);*/
            
            //Modifying of Story #1506
            //================================
//            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
//                this.getListView1ShowCols();
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
            this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + "\r\f" + sDateStr;
        //=================end===============

        } catch (Exception e) {
            throw new YssException("获取TA交易数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
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
     * getOperData
     */
    public void getStorageData() {
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

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        boolean analy1 = operSql.storageAnalysis("FAnalysisCode1",
                                                 YssOperCons.YSS_KCLX_TA); //判断分析代码存不存在
        boolean analy2 = operSql.storageAnalysis("FAnalysisCode2",
                                                 YssOperCons.YSS_KCLX_TA);
        boolean analy3 = operSql.storageAnalysis("FAnalysisCode3",
                                                 YssOperCons.YSS_KCLX_TA);
        this.portClsCode = rs.getString("FPortClsCode");
        this.portClsName = rs.getString("FPortClsName");
        this.yearMonth = rs.getString("FYearMonth");
        this.storageDate = rs.getDate("FStorageDate");
        this.portCode = rs.getString("FPortCode");
        this.portName = rs.getString("FPortName");
        this.curyCode = rs.getString("FCuryCode");
        this.curyName = rs.getString("FCuryName");
        this.storageAmount = rs.getDouble("FStorageAmount");
        this.cost = rs.getDouble("FCost");
        this.portCuryRate = rs.getDouble("FPortCuryRate");
        this.portCuryCost = rs.getDouble("FPortCuryCost");
        this.baseCuryRate = rs.getDouble("FBaseCuryRate");
        this.baseCuryCost = rs.getDouble("FBaseCuryCost");
        this.portCuryUnpl = rs.getDouble("FPortCuryUnpl");
        this.portCuryPl = rs.getDouble("FPortCurypl");
        this.baseCuryUnpl = rs.getDouble("FBaseCuryUnpl");
        this.baseCuryPl = rs.getDouble("FBaseCurypl");
        this.curyUnPl = rs.getDouble("FCuryUnpl");
        this.curyPl = rs.getDouble("FCurypl");
        if (analy1) {
            this.analysisCode1 = rs.getString("FanalysisCode1") + "";
            this.analysisName1 = rs.getString("FInvMgrName") + "";
        }
        if (analy2) {
            this.analysisCode2 = rs.getString("FanalysisCode2") + "";
            this.analysisName2 = rs.getString("FCatName") + "";
        }
        if (analy3) {
            this.analysisCode3 = rs.getString("FanalysisCode3") + "";
            //this.analysisName3 = rs.getString("FCatName") + "";
        }
        this.storageInitDate = rs.getDate("FStorageInitDate");
        /**shashijie 2011-10-19 STORY 1589 */
        this.FPortStorageAmount = rs.getDouble("FPortStorageAmount");
        /**end*/
        this.clsPortConvertStorageAmount=YssD.round(rs.getDouble("FFJZHZSSTORAGEAMOUNT"),4);
        super.setRecLog(rs);
    }

    /**
     * saveMutliOperData
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliStorageData(String sMutilRowStr) throws YssException {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException{
    	/**shashijie 2010-10-19 STORY 1589  本位币数量是否显示*/
    	String strReturn = "";
    	try {
    		if(sType.equalsIgnoreCase("doOperPortStorageAmount")){
    			strReturn = doOperPortCodeDegree();       	
            }
    		//story 2253 add by zhouwei 20120222 对折算后库存数量显示进行控制  start----
    		if(sType.equalsIgnoreCase("checkPortClsType")){
    			TaTradeBean tb=new TaTradeBean();
    			tb.setYssPub(pub);
    			tb.setStrPortCode(this.portCode);
            	Boolean isClass =tb.isMultiClass(this.portCode);           
            	String isShow = "false";
            	if(isClass){//多class需要根据通参来确定核算类型
            		int state=tb.getAccWayState(this.portCode);
            		if(state == 0){//按资产净值 （博时）不显示
            			isShow = "false";
            		}else if(state==1){//按基准资产份额 显示
            			isShow = "true";
            		}
            	}
            	return isShow;           
    		}
        	//story 2253 add by zhouwei 20120227 对折算后库存数量显示进行控制 end-----
            
		} catch (Exception ex) {
            throw new YssException("获取数据出错",ex);
        } finally {
            //dbl.closeResultSetFinal(rs);
        }
        return strReturn;
        /**end*/
    }

    /**
     * 本位币数量是否显示
     * @return
     * @author shashijie ,2011-10-19 , STORY 1589
     * @modified
     */
    private String doOperPortCodeDegree() {
    	String boole = "False";
		ResultSet rs = null;
    	try {
    		String strSql = " select a.* From "+pub.yssGetTableName("Tb_TA_ClassFundDegree")+
    			" a where a.FPortClsCode = "+dbl.sqlString(this.portClsCode)+" and a.FPortCode = "+
    			dbl.sqlString(this.portCode)+" and a.FCuryCode = "+dbl.sqlString(this.curyCode)+
    			" and a.FCheckState = 1 "
                ;
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
				boole = "true";
			}
		} catch (Exception e) {
			boole = "False";
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return boole;
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
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        try {
            // rs = dbl.openResultSet(strSql);
            // while (rs.next()) {
            //befEditBean.strCashAccCode = rs.getString("FCashAccCode") + "";


            // }
            return "";
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * deleteRecycleData
     * 功能：从数据库彻底删除数据
     * @throws YssException
     * 时间：2008年6月11号
     * 修改人：蒋春
     */
    public void deleteRecycleData() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] arrData = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Stock_TA") +
                        " where FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                        " and FYearMonth = " +
                        (this.bBegin.equalsIgnoreCase("false") ?
                         dbl.sqlString(YssFun.formatDate(this.
                        storageDate, "yyyyMM")) :
                         dbl.sqlString(this.yearMonth)) +
                        " and FStorageDate = " + dbl.sqlDate(this.storageDate) +
                        " and FPortCode = " + dbl.sqlString(this.portCode) +
                        (this.analysisCode1.length() == 0 ? "" :
                         " and FAnalysisCode1 = " +
                         dbl.sqlString(this.analysisCode1)) +
                        (this.analysisCode2.length() == 0 ? "" :
                         " and FAnalysisCode2 = " +
                         dbl.sqlString(this.analysisCode2)) +
                        (this.analysisCode3.length() == 0 ? "" :
                         " and FAnalysisCode3 = " +
                         dbl.sqlString(this.analysisCode3));

                    dbl.executeSql(strSql);
                }
            } else {
                if (this.portClsCode != null &&
                    this.portClsCode.trim().length() > 0) {
                    strSql = "delete from " + pub.yssGetTableName("Tb_Stock_TA") +
                        " where FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                        " and FYearMonth = " +
                        (this.bBegin.equalsIgnoreCase("false") ?
                         dbl.sqlString(YssFun.formatDate(this.
                        storageDate, "yyyyMM")) :
                         dbl.sqlString(this.yearMonth)) +
                        " and FStorageDate = " + dbl.sqlDate(this.storageDate) +
                        " and FPortCode = " + dbl.sqlString(this.portCode) +
                        (this.analysisCode1.length() == 0 ? "" :
                         " and FAnalysisCode1 = " +
                         dbl.sqlString(this.analysisCode1)) +
                        (this.analysisCode2.length() == 0 ? "" :
                         " and FAnalysisCode2 = " +
                         dbl.sqlString(this.analysisCode2)) +
                        (this.analysisCode3.length() == 0 ? "" :
                         " and FAnalysisCode3 = " +
                         dbl.sqlString(this.analysisCode3));

                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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

	public double getFPortStorageAmount() {
		return FPortStorageAmount;
	}

	public void setFPortStorageAmount(double fPortStorageAmount) {
		FPortStorageAmount = fPortStorageAmount;
	}
}
