package com.yss.main.operdata;

import java.sql.*;
import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.parasetting.*;
import com.yss.manager.CashPayRecAdmin;
import com.yss.util.*;

/**
 *
 * <p>Title: 现金应收应付实体Bean </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CashPecPayBean
    extends BaseDataSettingBean implements IDataSetting {

    private String num = "";            //编号
    private String oldNum = "";

    private java.util.Date tradeDate;   //业务日期
    private java.util.Date oldTradeDate;

    private String portCode = "";       //组合代码
    private String portName = "";       //组合名称
    private String oldPortCode = "";

    private String exchangeCode;        //交易代码
    private String exchangeName = "";   //交易名称
    private String oldFAnalysisCode3 = "";

    private String investManagerCode = ""; //投资经理代码
    private String investManagerName = ""; //投资经理名称
    private String oldFAnalysisCode1 = "";

    private String brokerCode = "";     //券商代码
    private String brokerName = "";     //券商名称
    private String oldFAnalysisCode2 = "";

    private String categoryCode = "";   //品种代码
    private String categoryName = "";   //品种名称

    private String cashAccCode = "";    //现金帐户代码
    private String cashAccName = "";    //现金账户名称
    private String oldCashAccCode = "";

    private String tsfTypeCode = "";    //调拨类型代码
    private String tsfTypeName = "";
    private String subTsfTypeCode = ""; //调拨子类型代码
    private String subTsfTypeName = "";
    private int iInOutType = 1;         //流入流出方向,默认为正方向

    private String curyCode = "";       //货币代码
    private String curyName = "";
    private double money;               //原币金额
    private double baseCuryRate;        //基础汇率
    private double baseCuryMoney;       //基础货币金额
    private double portCuryRate;        //组合汇率
    private double portCuryMoney;       //组合货币金额
    private CashAccountBean cashAcc;    //帐户信息

    private java.util.Date startDate;   //起始日期
    private java.util.Date endDate;     //终止日期

    private int dataSource; //来源标识

    private int stockInd=0; //入账标识

    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    private String strAttrClsCode = "";//所属分类
    private String strAttrClsName = "";
    private String strOldAttrClsCode = "";
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    
    
    private String strIsOnlyColumns = "0";  //在初始登陆时是否只显示列，不查询数据
    private String sRecycled = "";          //保存未解析前的字符串
    private String desc = "";               //描述 2008-4-22  单亮
    private String multAuditString = "";    //批量处理数据 linjunyun 2008-11-28 bug:MS00029

    private String relaOrderNum = "";       //关联数据排序编号。 MS00141 QDV4交银施罗德2009年01月4日02_B sj modified

    // MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090522
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    //------------------------------------------------------------------------------------

    //----MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A 添加了关联编号，关联编号类型 ----------
    private String sRelaNum;
    private String sRelaNumType = null;
    public String getRelaNum() {
        return sRelaNum;
    }

    public void setRelaNum(String sRelaNum) {
        this.sRelaNum = sRelaNum;
    }

    public String getRelaNumType() {
        return sRelaNumType;
    }

    public void setRelaNumType(String sRelaNumType) {
        this.sRelaNumType = sRelaNumType;
    }

    //-----------------------------------------------------------------------------------


    private CashPecPayBean filterType = null;
	private SingleLogOper logOper;

    //------ MS00141 QDV4交银施罗德2009年01月4日02_B sj modified ---------//
    public String getRelaOrderNum() {
        return relaOrderNum;
    }

    public void setRelaOrderNum(String sRelaOrderNum) {
        relaOrderNum = sRelaOrderNum;
    }

    //------------------------------------------------------------------//

    public String getCuryName() {
        return curyName;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public String getOldNum() {
        return oldNum;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    /*
       public int getDataSource() {
          return dataSource;
       }
     */
    public String getNum() {
        return num;
    }

    public String getTsfTypeName() {
        return tsfTypeName;
    }

    public double getMoney() {
        return money;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getBaseCuryMoney() {
        return baseCuryMoney;
    }

    public String getTsfTypeCode() {
        return tsfTypeCode;
    }

    public String getPortName() {
        return portName;
    }

    public String getSubTsfTypeCode() {
        return subTsfTypeCode;
    }

    public CashPecPayBean getFilterType() {
        return filterType;
    }

    public double getPortCuryMoney() {
        return portCuryMoney;
    }

    public String getCashAccName() {
        return cashAccName;
    }

    /*
       public int getStockInd() {
          return stockInd;
       }
     */

  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
    public String getStrAttrClsCode() {
		return strAttrClsCode;
	}

	public void setStrAttrClsCode(String strAttrClsCode) {
		this.strAttrClsCode = strAttrClsCode;
	}

	public String getStrAttrClsName() {
		return strAttrClsName;
	}

	public void setStrAttrClsName(String strAttrClsName) {
		this.strAttrClsName = strAttrClsName;
	}

	public String getStrOldAttrClsCode() {
		return strOldAttrClsCode;
	}

	public void setStrOldAttrClsCode(String strOldAttrClsCode) {
		this.strOldAttrClsCode = strOldAttrClsCode;
	}
    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
    
    
    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public void setSubTsfTypeName(String subTsfTypeName) {
        this.subTsfTypeName = subTsfTypeName;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setTsfTypeName(String tsfTypeName) {
        this.tsfTypeName = tsfTypeName;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setBaseCuryMoney(double baseCuryMoney) {
        this.baseCuryMoney = baseCuryMoney;
    }

    public void setTsfTypeCode(String tsfTypeCode) {
        this.tsfTypeCode = tsfTypeCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setSubTsfTypeCode(String subTsfTypeCode) {
        this.subTsfTypeCode = subTsfTypeCode;
    }

    public void setFilterType(CashPecPayBean filterType) {
        this.filterType = filterType;
    }

    public void setPortCuryMoney(double portCuryMoney) {
        this.portCuryMoney = portCuryMoney;
    }

    public void setCashAccName(String cashAccName) {
        this.cashAccName = cashAccName;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setCashAcc(CashAccountBean cashAcc) {
        this.cashAcc = cashAcc;
    }

    public void setInvestManagerName(String investManagerName) {
        this.investManagerName = investManagerName;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setInvestManagerCode(String investManagerCode) {
        this.investManagerCode = investManagerCode;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public void setTradeDate(java.util.Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public void setStrIsOnlyColumns(String strIsOnlyColumns) {
        this.strIsOnlyColumns = strIsOnlyColumns;
    }

    public void setDataSource(int dataSource) {
        this.dataSource = dataSource;
    }

    public void setStockInd(int stockInd) {
        this.stockInd = stockInd;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setInOutType(int iInOutType) {
        this.iInOutType = iInOutType;
    }

    public String getSubTsfTypeName() {
        return subTsfTypeName;
    }

    public CashAccountBean getCashAcc() {
        return cashAcc;
    }

    public String getInvestManagerName() {
        return investManagerName;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getInvestManagerCode() {
        return investManagerCode;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public java.util.Date getTradeDate() {
        return tradeDate;
    }

    public String getStrIsOnlyColumns() {
        return strIsOnlyColumns;
    }

    public int getDataSource() {
        return dataSource;
    }

    public int getStockInd() {
        return stockInd;
    }

    public String getDesc() {
        return desc;
    }

    public int getInOutType() {
        return iInOutType;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupCode = assetGroupName;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }


    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {

        try {
            String[] reqAry = null;
            String sTmpStr = "";
            String sMutiAudit = ""; //linjunyun 2008-11-28 bug:MS00029 批量处理的数据

            //linjunyun 2008-11-28 bug:MS00029 提取批量处理数据
            if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];
                multAuditString = sMutiAudit;
                sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];
            }
            if (sRowStr.length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.num = reqAry[0];

            if (YssFun.isDate(reqAry[1])) {
                this.tradeDate = YssFun.toDate(reqAry[1]);
            }
            this.portCode = reqAry[2];
            this.investManagerCode = reqAry[3];
            this.brokerCode = reqAry[4];
            this.categoryCode = reqAry[5];
            this.cashAccCode = reqAry[6];
            this.tsfTypeCode = reqAry[7];
            this.subTsfTypeCode = reqAry[8];
            this.curyCode = reqAry[9];
            if (reqAry[10].length() != 0) {
                this.money = Double.parseDouble(reqAry[10]);
            }
            if (reqAry[11].length() != 0) {
                this.baseCuryRate = Double.parseDouble(
                    reqAry[11]);
            }
            if (reqAry[12].length() != 0) {
                this.baseCuryMoney = Double.parseDouble(
                    reqAry[12]);
            }
            if (reqAry[13].length() != 0) {
                this.portCuryRate = Double.parseDouble(
                    reqAry[13]);
            }
            if (reqAry[14].length() != 0) {
                this.portCuryMoney = Double.parseDouble(
                    reqAry[14]);
            }

            if (!reqAry[15].equalsIgnoreCase("")) {
                this.startDate = YssFun.toDate(reqAry[15]);
            }
            if (!reqAry[16].equalsIgnoreCase("")) {
                this.endDate = YssFun.toDate(reqAry[16]);
            }

            super.checkStateId = Integer.parseInt(reqAry[17]);
            if (reqAry[18].trim().length() > 0 && YssFun.isNumeric(reqAry[18])) {
                this.iInOutType = YssFun.toInt(reqAry[18]);
            }
            this.strIsOnlyColumns = reqAry[19];
            this.desc = reqAry[20];
            // MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090521
            this.assetGroupCode = reqAry[21];
            this.assetGroupName = reqAry[22];
            
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            this.strAttrClsCode = reqAry[23];
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            //-----------------------------------------------------------------------
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (filterType == null) {
                    this.filterType = new CashPecPayBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (YssException e) {
            throw new YssException("解析现金应收应付出错!");
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.num.trim()).append("\t");
        buf.append(YssFun.formatDate(this.tradeDate)).append("\t");
        buf.append(this.portCode.trim()).append("\t");
        buf.append(this.portName.trim()).append("\t");
        buf.append(this.investManagerCode.trim()).append("\t");
        buf.append(this.investManagerName.trim()).append("\t");
        buf.append(this.brokerCode.trim()).append("\t");
        buf.append(this.brokerName.trim()).append("\t");
        buf.append(this.categoryCode.trim()).append("\t");
        buf.append(this.categoryName.trim()).append("\t");
        buf.append(this.cashAccCode.trim()).append("\t");
        buf.append(this.cashAccName.trim()).append("\t");
        buf.append(this.tsfTypeCode.trim()).append("\t");
        buf.append(this.tsfTypeName.trim()).append("\t");
        buf.append(this.subTsfTypeCode.trim()).append("\t");
        buf.append(this.subTsfTypeName.trim()).append("\t");
        buf.append(this.curyCode.trim()).append("\t");
        buf.append(this.curyName.trim()).append("\t");
        buf.append(this.money).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.baseCuryMoney).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(this.portCuryMoney).append("\t");
        buf.append(this.iInOutType).append("\t");
        buf.append(this.desc).append("\t");             //描述 2008-4-23  单亮
        buf.append(this.assetGroupCode).append("\t");   //组合群代码
        buf.append(this.assetGroupName).append("\t");   //组合群名称
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
    }

    /**
     * setResultSetAttr
     *
     * @param rs ResultSet
     */
    public void setResultSetAttr(ResultSet rs) throws YssException {
        try {
            this.num = rs.getString("FNum") + "";
            this.tradeDate = rs.getDate("FTransDate");
            this.portCode = rs.getString("FPortCode") + "";
            this.portName = rs.getString("FportName") + "";
            this.investManagerCode = rs.getString("FAnalysisCode1") + "";
            this.investManagerName = rs.getString("FAnalysisName1") + "";
            this.brokerCode = rs.getString("FAnalysisCode2") + "";
            this.brokerName = rs.getString("FAnalysisName2") + "";
            this.categoryCode = rs.getString("FAnalysisCode3") + "";
            this.categoryName = rs.getString("FAnalysisName3") + "";
            this.cashAccCode = rs.getString("FCashAccCode") + "";
            this.cashAccName = rs.getString("FCashAccName") + "";
            this.tsfTypeCode = rs.getString("FTsfTypeCode") + "";
            this.tsfTypeName = rs.getString("FTsfTypeName") + "";
            this.subTsfTypeCode = rs.getString("FSubTsfTypeCode") + "";
            this.subTsfTypeName = rs.getString("FSubTsfTypeName") + "";
            this.curyCode = rs.getString("FCuryCode") + "";
            this.curyName = rs.getString("FCuryName") + "";
            this.money = rs.getDouble("FMoney");
            this.baseCuryRate = rs.getDouble("FBaseCuryRate");
            this.baseCuryMoney = rs.getDouble("FBaseCuryMoney");
            this.portCuryRate = rs.getDouble("FPortCuryRate");
            this.portCuryMoney = rs.getDouble("FPortCuryMoney");
            this.iInOutType = rs.getInt("FInOut");
            this.desc = rs.getString("FDesc");  //描述 2008-4-23  单亮
            
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            if(dbl.isFieldExist(rs, "FATTRCLSCODE")){
            	this.strAttrClsCode = rs.getString("FATTRCLSCODE");
            	this.strAttrClsName = rs.getString("FATTRCLSNAME");
            }else{
            	this.strAttrClsCode = "";
            	this.strAttrClsName = "";
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//

            super.setRecLog(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * addOperData
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        String strNumberDate = "";
        String[] tmpDate = null;
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        String editNum = ""; //BUG：000499
        //add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
        CashPayRecAdmin cashPayRecAmin = new CashPayRecAdmin();
        try {
        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        	cashPayRecAmin.setYssPub(pub);
        	this.num = cashPayRecAmin.getKeyNum();
        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        	
        	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//            //不是新建和复制的情况。获取编号中的日期，用以判断业务日期和编号中的日期值是否相等。暂无bug.
//            if (num.trim().length() > 0) {
//                editNum = this.num.substring(3, 11);
//                editNum = editNum.substring(0, 4) + "-" + editNum.substring(4, 6) +
//                    "-" + editNum.substring(6, editNum.length()); //格式化日期值。用以获取Date
//            }
//            //这里加上判断处理,防止多次操作时每次都取最大编号,以影响处理速度 by liyu 080331;判断日期是否相同，若不同则获取最新编号。
//            if (this.num.trim().length() == 0 || YssFun.dateDiff(YssFun.toDate(editNum), this.tradeDate) != 0) {
//                strNumberDate = YssFun.formatDate(this.tradeDate,YssCons.YSS_DATETIMEFORMAT).substring(0, 8);
//                this.num = strNumberDate +
//                    dbFun.getNextInnerCode(pub.yssGetTableName(
//                        "Tb_Data_CashPayRec"),
//                                           dbl.sqlRight("FNum", 9), "000000001",
//                                           " where FTransDate =" +
//                                           dbl.sqlDate(tradeDate), 1);
//                this.num = "SRP" + this.num;
//            }
        	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            //=======BUG:000499
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                "(FNum,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FATTRCLSCODE," +
                "FCashAccCode,FTsfTypeCode,FSubTsfTypeCode,FCuryCode,FMoney,FBaseCuryRate," +
                "FBaseCuryMoney,FPortCuryRate,FPortCuryMoney,FDataSource,FStockInd,FCheckState," +
                //edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin
                "FCreator,FCreateTime,FCheckUser,FInOut,fdesc, FDataOrigin)" +
                "values(" + dbl.sqlString(this.num) + "," +
                dbl.sqlDate(this.tradeDate) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.investManagerCode.length() == 0 ? " " :
                              this.investManagerCode) + "," +
                dbl.sqlString(this.brokerCode.length() == 0 ? " " :
                              this.brokerCode) + "," +
                dbl.sqlString(this.categoryCode.length() == 0 ? " " :
                              this.categoryCode) + "," +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
               (this.strAttrClsCode.trim().length() == 0 ?dbl.sqlString(" "):dbl.sqlString(this.strAttrClsCode))+","+           
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//   
                dbl.sqlString(this.cashAccCode) + "," +
                dbl.sqlString(this.tsfTypeCode) + "," +
                dbl.sqlString(this.subTsfTypeCode) + "," +
                dbl.sqlString(this.curyCode) + "," +
                this.money + "," +
                this.baseCuryRate + "," +
                this.baseCuryMoney + "," +
                this.portCuryRate + "," +
                this.portCuryMoney + "," +
                1 + "," +
//                0 + "," +//edit by xuxuming,2010.01.19.不明白这里为什么要写死，现在要写入其它类型的入账标识  MS00917 怕影响以前的，只好改为下面的方式了
                (this.stockInd!=-1?0:this.stockInd)+","+//add by xuxuming,2010.01.19.需要写入其它类型的入账标识
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," +
                //edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B  FDataOrigin = 1 表示为手工录入的数据
                this.iInOutType + "," + dbl.sqlString(this.desc) +",1)";
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
            //------ add by wangzuochun 2010.09.10  MS01695    TA尾差调整报表在调整现金汇兑损益时有问题    QDV4赢时胜上海2010年9月3日01_B    
			YssGlobal.hmCashRecNums.put(this.tradeDate,this.num);
			//-------------------- MS01695 ------------------------//
        } catch (Exception e) {
            e.printStackTrace();
            throw new YssException("保存金额应收应付出错！");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

        return "";
    }

    /**
     * editOperData
     *
     * @return String
     */

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            //改成先删除再增加的形式，如果用update的方式，当修改日期时编号没有更新过来，这样很可能出现重复键的问题  胡昆  20070924
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " where FNum = " + dbl.sqlString(this.num);
            dbl.executeSql(strSql);
            this.addSetting();
        } catch (Exception e) {
            e.printStackTrace();
            throw new YssException("修改金额应收应付出错！");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }

    /**
     * 将数据放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_CashPayRec") + " " +
                "set FCheckState=" + super.checkStateId + ",FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum = " + dbl.sqlString(this.num);
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YssException("删除金额应收应付出错！");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
    }

    /**
     * 修改时间：2008年3月23号
     * 修改人：单亮
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase("")) ) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum = " +
                        dbl.sqlString(this.num);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else if (num != null&&(!num.equalsIgnoreCase("")) ) {
                strSql = "update " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FNum = " +
                    dbl.sqlString(this.num);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YssException("审核金额应收应付出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
     * getOperData
     */
    public void getOperData() {
    }

    /**
     * 此方法已被修改
     * 修改时间：2008年2月27号
     * 修改人：单亮
     * 原方法的功能：查询出金额应收应付数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() {

//        YssDbOperSql oper = new YssDbOperSql(pub);
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sVocStr = "";
        String sShowCols = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            sShowCols = this.getListView1ShowCols();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
          //add by yangheng 20100820 MS01310  分页无法显示  QDV4赢时胜(测试)2010年06月18日01_A 
            if (this.filterType!=null&&this.filterType.strIsOnlyColumns.equals("1")) {
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_CSP_DATASOURCE + "," +
                                            YssCons.YSS_CSP_STOCKIND);

                String reStr = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                    "\r\f" + sShowCols + "\r\f"+ yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr;
                return reStr;
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql = " select distinct y.* from " +
                "(select FNum from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " " +
                "  ) x join";
            strSql = strSql +
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " g.FCuryName,f.FCashAccName,h.FPortName as FPortName,e.FTsfTypeName as FTsfTypeName,i.FSubTsfTypeName as FSubTsfTypeName"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " ,nvl(j.FAttrClsName,' ') as FAttrClsName";
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//;
            strSql = strSql +
                (this.FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode" +
                this.FilterSql()
                +
                " left join (select o.FCashAccCode,o.FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") + " o " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                "(select FCashAccCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " where FCheckState = 1) f on a.FCashAccCode = f.FCashAccCode"//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                +
                " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u " +
//                " join (select * from " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("tb_para_portfolio") + " v where FCheckState = 1) h on a.FPortCode = h.FPortCode"
                +
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) e on a.FTsfTypeCode = e.FTsfTypeCode" +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) i on a.FSubTsfTypeCode = i.FSubTsfTypeCode" +
                buildFilterSql() +
                ") y on x.FNum =y.FNum " +
                "order by y.FCheckState, y.FCreateTime desc";
            //QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("CashPayRec");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_CSP_DATASOURCE + "," +
                                        YssCons.YSS_CSP_STOCKIND);

            String reStr = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + sShowCols + "\r\f"+ yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr;
            return reStr;

        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.strIsOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.filterType.num.length() != 0) {
                if (filterType.num.indexOf(",") > 0) { //若 num 传进来是一个编号集就用 in by ly 0130
                    sResult = sResult + " and a.FNum in(" +
                        operSql.sqlCodes(filterType.num) + " )";
                } else {
                    sResult = sResult + " and a.FNum like '" +
                        filterType.num.replaceAll("'", "''") + "%'";
                }
            }
            if (this.filterType.investManagerCode.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode1 like '" +
                    filterType.investManagerCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.brokerCode.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode2 like '" +
                    filterType.brokerCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.categoryCode.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode3 like '" +
                    filterType.categoryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.portCode.length() != 0) {
                if (filterType.portCode.split(",").length > 0) {
                    sResult = sResult + " and a.FPortCode in (" +
                        operSql.sqlCodes(filterType.portCode) + ")";
                } else {
                    sResult = sResult + " and a.FPortCode like '" +
                        filterType.portCode.replaceAll("'", "''") + "%'";
                }
            }
            if (this.filterType.cashAccCode.length() != 0) {
                if (filterType.cashAccCode.split(",").length > 0) {
                    sResult = sResult + " and a.FCashAccCode in (" +
                        operSql.sqlCodes(filterType.cashAccCode) + ")";
                } else {
                    sResult = sResult + " and a.FCashAccCode like '" +
                        filterType.cashAccCode.replaceAll("'", "''") + "%'";
                }
            }

            if (this.filterType.curyCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.curyCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.tsfTypeCode.length() != 0) {
                sResult = sResult + " and a.FTsfTypeCode like '" +
                    filterType.tsfTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.subTsfTypeCode.length() != 0) {
                sResult = sResult + " and a.FSubTsfTypeCode like '" +
                    filterType.subTsfTypeCode.replaceAll("'", "''") + "%'";
            }
            //BugNo:0000316 edit by jc
            if (this.filterType.startDate != null &&
                this.filterType.startDate.toString().length() > 0 &&
                this.filterType.endDate != null &&
                this.filterType.endDate.toString().length() > 0) {
                if (this.filterType.startDate.before(this.filterType.endDate)) {
                    sResult = sResult + " and a.FTransDate between " +
                        dbl.sqlDate(this.filterType.startDate) +
                        " and " +
                        dbl.sqlDate(this.filterType.endDate);
                } else {
                    sResult = sResult + " and a.FTransDate between " +
                        dbl.sqlDate(this.filterType.endDate) +
                        " and " +
                        dbl.sqlDate(this.filterType.startDate);
                }
            }
            //----------------------jc
            //----add by songjie 2011.04.08 BUG 1626 QDV4赢时胜(测试)2011年3月31日02_B----//
            else if(this.filterType.tradeDate != null && this.filterType.tradeDate.toString().length() > 0 &&
            		!YssFun.formatDate(this.filterType.tradeDate).equals("9998-12-31")){
                sResult = sResult + " and a.FTransDate = " +
                dbl.sqlDate(this.filterType.tradeDate);
            }
            //----add by songjie 2011.04.08 BUG 1626 QDV4赢时胜(测试)2011年3月31日02_B----//
			// =======add by xxm,2010.01.20===============MS00917======
			if (this.filterType.stockInd != 0) {// 默认值为0.
										// 0表示未入账，1表示入账。这两种情形都不会以此标识来查询。9表示非估值时自动产生的估增数据，‘－1’表示尾差调整数据，此时需要根据此标识来查询
				sResult += " and a.FStockInd =" + this.filterType.stockInd;
			}
			// ===============end=================================

			//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            if (this.filterType.strAttrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.strAttrClsCode.replaceAll("'", "''") + "%'";
            }
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
			
        }
        return sResult;

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
        String sHeader = ""; //表头
        String sShowDataStr = ""; //用于显示的数据
        String sAllDataStr = ""; //全部的数据
        String strSql = "";
        String sShowCols = "";
        String portCode = ""; //SQL语句中的组合代码
        int i = 0;
        String[] reqAry = null;
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = this.getListView1Headers();
            sShowCols = this.getListView1ShowCols();
            strSql = "select show.*,gp.FAssetGroupCode,gp.FAssetGroupName from( "; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
            strSql = strSql + " select y.* from " +
                "(select FNum from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " " +
                " where FCheckState <> 2 ) x join";
            strSql = strSql +
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " g.FCuryName,f.FCashAccName,h.FPortName as FPortName,e.FTsfTypeName as FTsfTypeName,i.FSubTsfTypeName as FSubTsfTypeName, nvl(j.FAttrClsName,' ') as FAttrClsName ";//add by jiangshichao 2010.11.22 NO.125 用户需要对组合按资本类别进行子组合的分类
            strSql = strSql +
                (this.FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3  ") +
                " from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from "+pub.yssGetTableName("Tb_Para_AttributeClass")+" ) j on a.FAttrClsCode = j.FAttrClsCode "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//      
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode" +
                this.FilterSql()
                +
                " left join (select o.FCashAccCode,o.FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") + " o " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                "(select FCashAccCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " where FCheckState = 1 ) f on a.FCashAccCode = f.FCashAccCode"//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                +
                " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u " +
//                " join (select * from " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                pub.yssGetTableName("tb_para_portfolio") +
                " v where FCheckState = 1) h on a.FPortCode = h.FPortCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) e on a.FTsfTypeCode = e.FTsfTypeCode" +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) i on a.FSubTsfTypeCode = i.FSubTsfTypeCode" +
                ") y on x.FNum =y.FNum " +
                " where FPortCode in ( " + operSql.sqlCodes(this.filterType.portCode) + //更改组合代码的获取方式，MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang modify 20090522
                ") and FTransDate between " +
                dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +
                " and y.FDataOrigin = 0 " +//add by songjie 2012.04.06 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 
                " and FTsfTypeCode in ('06','07')"  +//by guyichuan 20110522 STORY #561 增加贷款帐户及应付贷款利息的支持 增加07
                " and FSubTsfTypeCode in ('06DE','07LI') " +
                (this.filterType.cashAccCode.length() > 0 ? (" and FCashAccCode in(" + operSql.sqlCodes(this.filterType.cashAccCode) + ")") : " ") + //更改组合代码的获取方式，MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang modify 20090522
                " order by y.FCheckState, y.FCreateTime desc";
            //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
            strSql = strSql + ") show join TB_SYS_ASSETGROUP gp on gp.fassetgroupcode='" + pub.getPrefixTB() + "'";
            //---------------------------------------------------------------------------------------------------

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
                this.assetGroupCode = rs.getString("FAssetGroupCode"); //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
                this.assetGroupName = rs.getString("FAssetGroupName"); //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
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

            String reStr = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + sShowCols;
            return reStr;

        }

        catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        try {
            if (sType.equalsIgnoreCase("getLx")) {
                //--------sj modified 20090121 QDV4嘉实2009年1月5日01_B  bugID:MS00143 ------------
                this.money = operFun.getCashLX(this.tradeDate, this.cashAccCode,
                                               this.portCode,
                                               this.investManagerCode.length() > 0 ?
                                               this.investManagerCode : " ",
                                               this.categoryCode.length() > 0 ?
                                               this.categoryCode : " ", "",this.tsfTypeCode, this.subTsfTypeCode); //通过前台获取的支付类型，来确认调拨子类型。xuqiji 20100327 MS00952 QDV4赢时胜（测试）2010年03月27日03_B
                //--------------------------------------------------------------------------------
                this.money = YssD.round(money, 2);
            }
            //linjunyun 2008-11-28 bug:MS00029 批量审核/反审核/删除
            if (sType.equalsIgnoreCase("multauditTradeSub")) {
                if (multAuditString.length() > 0) {
                    return this.auditMutli(this.multAuditString); //执行批量审核/反审核/删除
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return buildRowStr();
    }

    private String FilterSql() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        //2009.01.14 蒋锦 添加 异常处理
        try {
            strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
                pub.yssGetTableName("Tb_Para_StorageCfg") +
                " where FCheckState = 1 and FStorageType = " +
                dbl.sqlString(YssOperCons.YSS_KCLX_Cash);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                for (int i = 1; i <= 3; i++) {
                    if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                        rs.getString("FAnalysisCode" + String.valueOf(i)).
                        equalsIgnoreCase("004")) {
                        sResult = sResult +
                            " left join (select uu.FCatCode ,uu.FCatName  as FAnalysisName" +
                            i +
                            " from  (select FCatCode  from " +
                            " Tb_Base_Category " +
                            " where FCheckState = 1 group by FCatCode )kk " +
                            " join (select * from " +
                            " Tb_Base_Category ) uu on kk.FCatCode = uu.FCatCode ) category on a.FAnalysisCode" +
                            i + " = category.FCatCode";
                    } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                               rs.getString("FAnalysisCode" + String.valueOf(i)).
                               equalsIgnoreCase("001")) {
                        sResult = sResult +
                     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
               
                        
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                        i +
                        "  from  " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " n where n.FCheckState = 1 ) invmgr on a.FAnalysisCode" +
                        i + " = invmgr.FInvMgrCode ";
                    
                        //end by lidaolong
                    //bug 2343 add by zhouwei 20111111 修正券商无法显示名称
                    }else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                            rs.getString("FAnalysisCode" + String.valueOf(i)).
                            equalsIgnoreCase("002")) {//券商
                     sResult = sResult +
                     " left join (select n.FBrokerCode ,n.FBrokerName as FAnalysisName" +
                     i +
                     "  from  " +
                     pub.yssGetTableName("Tb_Para_Broker") +
                     " n where n.FCheckState = 1 ) bk on a.FAnalysisCode" +
                     i + " = bk.FBrokerCode ";
                   //bug 2343 add by zhouwei 20111111 修正交易地点无法显示名称
                    } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                            rs.getString("FAnalysisCode" + String.valueOf(i)).
                            equalsIgnoreCase("003")) {//交易地点
                     sResult = sResult +
                     " left join (select n.FExchangeCode ,n.FExchangeName as FAnalysisName" +
                     i +
                     "  from  " +
                     pub.yssGetTableName("Tb_Base_Exchange") +
                     " n where n.FCheckState = 1 ) exc on a.FAnalysisCode" +
                     i + " = exc.FExchangeCode ";
                    }else {
                        sResult = sResult +
                            " left join (select ' ' as FAnalysisNull , ' ' as FAnalysisName" +//调整为有空格的字段，防止在创建分页表时报错 by leeyu 20100813 合并太平版本时调整
                            i + " from  " +
                            pub.yssGetTableName("Tb_Para_StorageCfg") +
                            " where 1=2) tn" + i + " on a.FAnalysisCode" + i +
                            " = tn" +
                            i + ".FAnalysisNull ";
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            //2009.01.08 蒋锦 关掉记录集
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        CashPecPayBean befEditBean = new CashPecPayBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = " select y.* from " +
                "(select FNum from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " " +
                " where FCheckState <> 2 ) x join";
            strSql = strSql +
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " g.FCuryName,f.FCashAccName,h.FPortName as FPortName,e.FTsfTypeName as FTsfTypeName,i.FSubTsfTypeName as FSubTsfTypeName"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " ,nvl(j.FAttrClsName,' ') as FAttrClsName";
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//;
            strSql = strSql +
                (this.FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
              //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode" +
                this.FilterSql()
                +
                " left join (select o.FCashAccCode,o.FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") + " o " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                "(select FCashAccCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " where FCheckState = 1 ) f on a.FCashAccCode = f.FCashAccCode"//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                +
                " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u " +
//                " join (select * from " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("tb_para_portfolio") + " v where FCheckState = 1) h on a.FPortCode = h.FPortCode"
                +
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) e on a.FTsfTypeCode = e.FTsfTypeCode" +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) i on a.FSubTsfTypeCode = i.FSubTsfTypeCode" +
                " where  FNum =" + dbl.sqlString(this.num) +
                ") y on x.FNum =y.FNum " +
                "order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.num = rs.getString("FNum") + "";
                befEditBean.tradeDate = rs.getDate("FTransDate");
                befEditBean.portCode = rs.getString("FPortCode") + "";
                befEditBean.portName = rs.getString("FportName") + "";
                befEditBean.investManagerCode = rs.getString("FAnalysisCode1") + "";
                befEditBean.investManagerName = rs.getString("FAnalysisName1") + "";
                befEditBean.brokerCode = rs.getString("FAnalysisCode2") + "";
                befEditBean.brokerName = rs.getString("FAnalysisName2") + "";
                befEditBean.categoryCode = rs.getString("FAnalysisCode3") + "";
                befEditBean.categoryName = rs.getString("FAnalysisName3") + "";
                befEditBean.cashAccCode = rs.getString("FCashAccCode") + "";
                befEditBean.cashAccName = rs.getString("FCashAccName") + "";
                befEditBean.tsfTypeCode = rs.getString("FTsfTypeCode") + "";
                befEditBean.tsfTypeName = rs.getString("FTsfTypeName") + "";
                befEditBean.subTsfTypeCode = rs.getString("FSubTsfTypeCode") + "";
                befEditBean.subTsfTypeName = rs.getString("FSubTsfTypeName") + "";
                befEditBean.curyCode = rs.getString("FCuryCode") + "";
                befEditBean.curyName = rs.getString("FCuryName") + "";
                befEditBean.money = rs.getDouble("FMoney");
                befEditBean.baseCuryRate = rs.getDouble("FBaseCuryRate");
                befEditBean.baseCuryMoney = rs.getDouble("FBaseCuryMoney");
                befEditBean.portCuryRate = rs.getDouble("FPortCuryRate");
                befEditBean.portCuryMoney = rs.getDouble("FPortCuryMoney");
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                befEditBean.strAttrClsCode = rs.getString("FATTRCLSCODE");
                befEditBean.strAttrClsName = rs.getString("FAttrClsName");
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /**
     * 20110902 added by liubo.Bug #2543
     * 收益计提原来使用的listview3方法，写死了只会往前台返回06DE和07LI子类型的数据。
     * 因为无法确认直接修改listview3方法会否影响其他的功能使用，所以另开listview4方法，增加06PF的子类型的限定，解决前台无法显示申购款计息的问题
     *
     * @return String
     */
    public String getListViewData4()  throws YssException{
    	String sHeader = ""; //表头
        String sShowDataStr = ""; //用于显示的数据
        String sAllDataStr = ""; //全部的数据
        String strSql = "";
        String sShowCols = "";
        String portCode = ""; //SQL语句中的组合代码
        int i = 0;
        String[] reqAry = null;
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = this.getListView1Headers();
            sShowCols = this.getListView1ShowCols();
            strSql = "select show.*,gp.FAssetGroupCode,gp.FAssetGroupName from( "; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
            strSql = strSql + " select y.* from " +
                "(select FNum from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " " +
                " where FCheckState <> 2 ) x join";
            strSql = strSql +
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " g.FCuryName,f.FCashAccName,h.FPortName as FPortName,e.FTsfTypeName as FTsfTypeName,i.FSubTsfTypeName as FSubTsfTypeName, nvl(j.FAttrClsName,' ') as FAttrClsName ";//add by jiangshichao 2010.11.22 NO.125 用户需要对组合按资本类别进行子组合的分类
            strSql = strSql +
                (this.FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3  ") +
                " from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from "+pub.yssGetTableName("Tb_Para_AttributeClass")+" ) j on a.FAttrClsCode = j.FAttrClsCode "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//      
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode" +
                this.FilterSql()
                +
                " left join (select o.FCashAccCode,o.FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") + " o " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                "(select FCashAccCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " where FCheckState = 1 ) f on a.FCashAccCode = f.FCashAccCode"//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                +
                " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u " +
//                " join (select * from " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                pub.yssGetTableName("tb_para_portfolio") +
                " v where FCheckState = 1) h on a.FPortCode = h.FPortCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) e on a.FTsfTypeCode = e.FTsfTypeCode" +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) i on a.FSubTsfTypeCode = i.FSubTsfTypeCode" +
                ") y on x.FNum =y.FNum " +
                " where FPortCode in ( " + operSql.sqlCodes(this.filterType.portCode) + //更改组合代码的获取方式，MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang modify 20090522
                //edit by songjie 2012.04.06 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B
                ") and y.FDataOrigin = 0 and FTransDate between " +
                dbl.sqlDate(this.startDate) + " and " + dbl.sqlDate(this.endDate) +
                " and FTsfTypeCode in ('06','07')"  +//by guyichuan 20110522 STORY #561 增加贷款帐户及应付贷款利息的支持 增加07
                " and FSubTsfTypeCode in ('06DE','07LI','06PF') " +
                 
                (this.filterType.cashAccCode.length() > 0 ? (" and FCashAccCode in(" + operSql.sqlCodes(this.filterType.cashAccCode) + ")") : " ") + //更改组合代码的获取方式，MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang modify 20090522
                " order by y.FCheckState, y.FCreateTime desc";
            //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
            strSql = strSql + ") show join TB_SYS_ASSETGROUP gp on gp.fassetgroupcode='" + pub.getPrefixTB() + "'";
            //---------------------------------------------------------------------------------------------------

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
                this.assetGroupCode = rs.getString("FAssetGroupCode"); //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
                this.assetGroupName = rs.getString("FAssetGroupName"); //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090522
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

            String reStr = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + sShowCols;
            return reStr;

        }

        catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
     * @throws YssException 
     */
    public IDataSetting getSetting() throws YssException {
    	ResultSet rs = null;
        String strSql = "";
        try {
           strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                 " a "
                 + buildFilterSql();
           rs = dbl.openResultSet(strSql);
           while (rs.next()) {
              this.num = rs.getString("FNum") + "";
           }
           return this;
        }
        catch (Exception e) {
           throw new YssException("获取现金应收应付表信息出错！", e);
        }
        finally {
           dbl.closeResultSetFinal(rs);
        }
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
     * 删除回收站的数据，即彻底从数据库删除数据
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
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_CashPayRec") +
                        " where FNum = " + dbl.sqlString(this.num);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else if (num != "" && num != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_CashPayRec") +
                    " where FNum = " + dbl.sqlString(this.num);
                //执行sql语句
                dbl.executeSql(strSql);
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

    /**
     * linjunyun 2008-11-28 bug:MS00029 批量更新审核/反审核/删除后的数据
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     */
    public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null;
        String sqlStr = "";
        java.sql.PreparedStatement psmt = null;
        boolean bTrans = false;
        CashPecPayBean data = null;
        String[] multAudit = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = "update " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = ?";

            psmt = conn.prepareStatement(sqlStr);
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f");
                if (multAudit.length > 0) {
                    for (int i = 0; i < multAudit.length; i++) {
                        data = new CashPecPayBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multAudit[i]);
                        psmt.setString(1, data.num);
                        psmt.addBatch();
                        // ---增加批量删除的日志记录功能----guojianhua add 20100908-------//
                        data=this;
                        logOper = SingleLogOper.getInstance();
						if (this.checkStateId == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (this.checkStateId == 1) {
							data.checkStateId = 1;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						} else if (this.checkStateId == 0) {
							data.checkStateId = 0;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						}
                        // -----------------------------------------//
                    }
                }
                conn.setAutoCommit(false);
                bTrans = true;
                psmt.executeBatch();
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("批量审核凭证数据表出错!");
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(psmt);
        }
        return "";
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

    /**
     * 从后台加载出跨组合群的内容
     * 修改人：panjunfang
     * 修改人时间:20090522
     * MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * @return String
     * @throws YssException
     */
    public String getListViewGroupData3() throws YssException {
        String sGroups = "";                    //定义一个变量用于保存处理后的结果数据
        String sPrefixTB = pub.getPrefixTB();   //保存于原有表前缀
        String[] assetGroupCodes = this.filterType.assetGroupCode.split(YssCons.YSS_GROUPSPLITMARK);    //按组合群的解析符解析组合群代码
        String[] strPortCodes = this.filterType.portCode.split(YssCons.YSS_GROUPSPLITMARK);             //按组合群的解析符解析组合代码
        String[] cashAccCode = this.filterType.cashAccCode.split(YssCons.YSS_GROUPSPLITMARK);           //按组合群的解析符解析现金账户代码
        try {
            for (int i = 0; i < assetGroupCodes.length; i++) {  //遍历所有组合群
                this.assetGroupCode = assetGroupCodes[i];       //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode);           //将该组合群代码设置为表前缀
                this.filterType.portCode = strPortCodes[i];     //获取当前组合群下的所有组合
                this.filterType.cashAccCode = cashAccCode[i];   //获取当前组合群下的所有现金账户代码
                String sGroup = this.getListViewData3();        //调用处理方法
                sGroups = sGroups + sGroup + YssCons.YSS_GROUPSPLITMARK;    //各组合群的处理结果用组合群解析符隔开
            }
            if (sGroups.length() > 7) {
                sGroups = sGroups.substring(0, sGroups.length() - 7);       //去除尾部多余的组合群解析符
            }
        } catch (Exception e) {
            throw new YssException("进行组合群循环处理时出错！", e);
        } finally {
            pub.setPrefixTB(sPrefixTB);     //设回表前缀
        }
        return sGroups;                     //返回处理结果到前台
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }
}
