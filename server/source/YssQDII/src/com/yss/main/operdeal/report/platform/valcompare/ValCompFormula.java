package com.yss.main.operdeal.report.platform.valcompare;

import com.yss.base.BaseCalcFormula;
import java.util.ArrayList;
import com.yss.util.YssException;
import java.util.HashMap;
import com.yss.vsub.YssFinance;
import com.yss.util.YssFun;
import java.sql.ResultSet;
import com.yss.main.operdeal.report.platform.valcompare.pojo.ValCompDataBean;

/**
 *
 * <p>Title: 解析自定义函数</p>
 *
 * <p>Description: 解析自定义函数，使用函数参数拼接为 SQL 语句 返回查询结果</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ValCompFormula
    extends BaseCalcFormula {

    private java.util.Date compareDate; //核对日期
    private String script = ""; //核对脚本
    private String portCode = ""; //组合代码
    private String compProCode = ""; //核对方案代码
    private int accCode; //组合对应的套帐代码
    private HashMap hmSql = new HashMap(); //装载 SQL 语句

    public String getPortCode() {
        return this.portCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public java.util.Date getCompareDate() {
        return this.compareDate;
    }

    public void setCompareDate(java.util.Date compareDate) {
        this.compareDate = compareDate;
    }

    public String getScript() {
        return this.script;
    }

    public String getCompProCode() {
        return compProCode;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setCompProCode(String compProCode) {
        this.compProCode = compProCode;
    }

    public ValCompFormula() {

    }

    /**
     * 初始化转载 SQL 语句的 HashMap
     * 使用报表类型作键
     * @throws YssException
     */
    public void initFormulaParams() throws YssException {
        YssFinance yssFina = new YssFinance();
        yssFina.setYssPub(pub);
        this.accCode = YssFun.toInt(yssFina.getCWSetCode(this.portCode));
        String sqlGZPort = "SELECT *" +
            " FROM " + pub.yssGetTableName("Tb_Data_NavData") +
            " WHERE FNAVDate = " + dbl.sqlDate(this.compareDate) + " AND" +
            " FPortCode = " + dbl.sqlString(this.portCode) + " AND" +
            " FDetail = 0 AND ";
        String sqlCWPort = "SELECT a.*, " + dbl.sqlSubStr("FAcctCode", dbl.sqlInstr("FAcctCode", "'_'") + " + 1 ") + " AS FCode" +
            " FROM " + pub.yssGetTableName("Tb_Rep_GuessValue") +
            " a WHERE FDate = " + dbl.sqlDate(this.compareDate) + " AND" +
            " FPortCode = " + this.accCode + " AND" +
            " FAcctDetail = 1 AND ";
        //fanghaoln 20100127 MS00912 QDV4南方2010年1月7日01_A 
        String sqlGHPort = "SELECT *" +
        " FROM " + pub.yssGetTableName("Tb_Data_NavData") +
        " WHERE FNAVDate = " + dbl.sqlDate(this.compareDate) + " AND" +
        " FPortCode = " + dbl.sqlString(this.portCode) + " AND ";
        String sqlCHPort = "SELECT a.*, " + dbl.sqlSubStr("FAcctCode", dbl.sqlInstr("FAcctCode", "'_'") + " + 1 ") + " AS FCode" +
        " FROM " + pub.yssGetTableName("Tb_Rep_GuessValue") +
        " a WHERE FDate = " + dbl.sqlDate(this.compareDate) + " AND" +
        " FPortCode = " + this.accCode + " AND ";
        this.hmSql.put("gz", sqlGZPort);
        this.hmSql.put("cw", sqlCWPort);
        this.hmSql.put("gh", sqlGHPort);
        this.hmSql.put("ch", sqlCHPort);
        //-----------------------------------------end---MS00912 -----------------------------------------------------------------
    }

    /**
     * 类入口
     * @return Object
     * @throws YssException
     */
    public Object calcFormulaString() throws YssException {
        Object objFunResult = "";
        try {
            initFormulaParams();
            objFunResult = getFormulaValue(this.script);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return objFunResult;
    }

    public Object getExpressValue(String sExpress, ArrayList alParams) throws
        YssException {
        Object obj = null;
        try {
            sExpress = sExpress.trim();
            if (sExpress.substring(3).equalsIgnoreCase("part")) {
                obj = this.getPartFunction(sExpress, alParams);
            } else if (sExpress.substring(3).equalsIgnoreCase("sum")) {
                obj = getSumFunction(sExpress, alParams);
            } else if (sExpress.substring(3).equalsIgnoreCase("one")) {
                obj = getOneFunction(sExpress, alParams);
            } else {
                throw new YssException("函数语法出错,请检查函数名称！");
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return obj;
    }

    public Object getKeywordValue(String sKeyword) throws YssException {
        Object objResult = sKeyword;
        return objResult;
    }

    /**
     * 解析 one 函数的参数，替换为 SQL 语句，并调用 getOneFunData 方法执行 SQL 语句，返回装载查询结果的 ValCompareResul
     * @param sExpress：自定义函数名称
     * @param alParams ArrayList：函数参数列表，字符串 List
     * @return Object
     * @throws YssException
     */
    public Object getOneFunction(String sExpress, ArrayList alParams) throws YssException {
        String sFunBody = "";
        ArrayList alResult = new ArrayList();
        Object obj = null;
        try {
            if (alParams.size() != 1) {
                throw new YssException("函数语法错误！");
            }
            sFunBody = (String) alParams.get(0);
            sFunBody = sFunBody.replaceAll("&&", "and");
            alResult.add(sFunBody);
            obj = getOneFunData(sExpress, alResult);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return obj;
    }

    /**
     * 解析 sum 函数的参数，替换为 SQL 语句，并调用 getSumFunData 方法执行 SQL 语句，返回装载查询结果的 ValCompareResul
     * @param sExpress：自定义函数名称
     * @param alParams ArrayList：函数参数列表，字符串 List
     * @return Object
     * @throws YssException
     */
    public Object getSumFunction(String sExpress, ArrayList alParams) throws YssException {
        String sFunBody = "";
        ArrayList alResult = new ArrayList();
        Object obj = null;
        try {
            if (alParams.size() != 1) {
                throw new YssException("函数语法错误！");
            }
            sFunBody = (String) alParams.get(0);
            sFunBody = sFunBody.replaceAll("&&", "and");
            alResult.add(sFunBody);
            obj = getSumFunData(sExpress, alResult);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return obj;
    }

    /**
     * 解析 part 函数的参数，替换为 SQL 语句，并调用 getSumFunData 方法执行 SQL 语句，返回装载查询结果的 HashMap
     * @param sExpress：自定义函数名称
     * @param alParams ArrayList：函数参数列表，字符串 List
     * @return Object
     * @throws YssException
     */
    public Object getPartFunction(String sExpress, ArrayList alParams) throws YssException {
        String sFstParam = "";
        String sSecParam = "";
        ArrayList alResult = new ArrayList();
        Object obj = null;
        try {
            if (alParams.size() < 2) {
                throw new YssException("函数语法错误！");
            }
            sFstParam = (String) alParams.get(0);
            sSecParam = (String) alParams.get(1);
            sFstParam = sFstParam.replaceAll("&&", "and");
            alResult.add(sFstParam);
            alResult.add(sSecParam);
            obj = getPartFunData(sExpress, alResult);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return obj;
    }

    /**
     * 执行查询 one 函数 SQL 语句，将查询结果装载入 ValCompareResult 中返回
     * @param sExpress：自定义函数名称
     * @param liParamGroup ArrayList：函数参数列表，字符串 List
     * @return Object
     * @throws YssException
     */
    public Object getOneFunData(String sExpress, ArrayList liParamGroup) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        ValCompDataBean valData = new ValCompDataBean();
        try {
            sqlStr = (String)this.hmSql.get(sExpress.substring(0, 2).toLowerCase()) + (String) liParamGroup.get(0);
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
            	//20121018 modified by liubo.Story #2996
            	//将“gh”的函数也放在这里进行处理，在此之前gh的函数没有进行过处理
            	//==============================
//                if (sExpress.substring(0, 2).equalsIgnoreCase("gz")) 
            	if (sExpress.substring(0, 2).equalsIgnoreCase("gz") || sExpress.substring(0, 2).equalsIgnoreCase("gh")) 
                //=============end=================
                {
                    valData.setGzKeyCode(rs.getString("FKeyCode"));
                    valData.setGzKeyName(rs.getString("FKeyName").substring(1).trim());
                    valData.setGzCost(rs.getDouble("FCost") * rs.getDouble("FInOut"));
                    valData.setGzPortCost(rs.getDouble("FPortCost") * rs.getDouble("FInOut"));
                    valData.setGzMarketValue(rs.getDouble("FMarketValue") * rs.getDouble("FInOut"));
                    valData.setGzPortMarketValue(rs.getDouble("FPortMarketValue") * rs.getDouble("FInOut"));
                    valData.setGzAmount(rs.getDouble("FSParAmt") * rs.getDouble("FInOut"));
                    valData.setGzReTypeCode(rs.getString("FReTypeCode")); //2008.06.12 蒋锦 添加 用于判断类型
                } 
            	//20121018 modified by liubo.Story #2996
            	//将“ch”的函数也放在这里进行处理，在此之前ch的函数没有进行过处理
            	//==============================
//            	else if (sExpress.substring(0, 2).equalsIgnoreCase("cw"))
            	else if (sExpress.substring(0, 2).equalsIgnoreCase("cw") || sExpress.substring(0, 2).equalsIgnoreCase("ch"))
                //==============end================
                {
                    valData.setCwKeyCode(rs.getString("FAcctCode"));
                    valData.setCwKeyName(rs.getString("FAcctName"));
                    valData.setCwCost(rs.getDouble("FCost"));
                    valData.setCwPortCost(rs.getDouble("FStandardMoneyCost"));
                    valData.setCwMarketValue(rs.getDouble("FMarketValue"));
                    valData.setCwPortMarketValue(rs.getDouble(
                        "FStandardMoneyMarketValue"));
                    valData.setCwAmount(rs.getDouble("FAmount"));
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return valData;
    }

    /**
     * 执行查询 sum 函数 SQL 语句，将查询结果装载入 ValCompareResult 中返回
     * @param sExpress：自定义函数名称
     * @param liParamGroup ArrayList：函数参数列表，字符串 List
     * @return Object
     * @throws YssException
     */
    public Object getSumFunData(String sExpress, ArrayList liParamGroup) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        ValCompDataBean valData = new ValCompDataBean();
        try {
            sqlStr = (String)this.hmSql.get(sExpress.substring(0, 2).toLowerCase()) + (String) liParamGroup.get(0);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                if (sExpress.substring(0, 2).equalsIgnoreCase("gz")) {
                    valData.setGzKeyCode(valData.getGzKeyCode() + rs.getString("FKeyCode") + " \f");
                    valData.setGzKeyName(valData.getGzKeyName() + rs.getString("FKeyName").substring(1).trim() + " \f");
                    valData.setGzCost(valData.getGzCost() + rs.getDouble("FCost") * rs.getDouble("FInOut"));
                    valData.setGzPortCost(valData.getGzPortCost() + rs.getDouble("FPortCost") * rs.getDouble("FInOut"));
                    valData.setGzMarketValue(valData.getGzMarketValue() + rs.getDouble("FMarketValue") * rs.getDouble("FInOut"));
                    valData.setGzPortMarketValue(valData.getGzPortMarketValue() + rs.getDouble("FPortMarketValue") * rs.getDouble("FInOut"));
                    valData.setGzAmount(valData.getGzAmount() + rs.getDouble("FSParAmt") * rs.getDouble("FInOut"));
                    valData.setGzReTypeCode(rs.getString("FReTypeCode")); //2008.06.12 蒋锦 添加 用于判断类型
                } else if (sExpress.substring(0, 2).equalsIgnoreCase("cw")) {
                    valData.setCwKeyCode(valData.getCwKeyCode() + rs.getString("FAcctCode") + " \f");
                    valData.setCwKeyName(valData.getCwKeyName() + rs.getString("FAcctName") + " \f");
                    valData.setCwCost(valData.getCwCost() + rs.getDouble("FCost"));
                    valData.setCwPortCost(valData.getCwPortCost() + rs.getDouble("FStandardMoneyCost"));
                    valData.setCwMarketValue(valData.getCwMarketValue() + rs.getDouble("FMarketValue"));
                    valData.setCwPortMarketValue(valData.getCwPortMarketValue() + rs.getDouble(
                        "FStandardMoneyMarketValue"));
                    valData.setCwAmount(valData.getCwAmount() + rs.getDouble("FAmount"));
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return valData;
    }

    /**
     * 执行查询 part 函数 SQL 语句，将查询结果装载入 HashMap 中返回
     * HashMap 键为 part 函数的第二个参数为字段名的查询结果，值为装载所有查询结果的 ValCompareResult
     * @param sExpress：自定义函数名称
     * @param liParamGroup ArrayList：函数参数列表，字符串 List
     * @return Object
     * @throws YssException
     */
    public Object getPartFunData(String sExpress, ArrayList liParamGroup) throws YssException {
        String sqlStr = "";
        String sLink = "";
        ResultSet rs = null;
        ValCompDataBean valData = null;
        HashMap hmResult = new HashMap();
        try {
            sqlStr = (String)this.hmSql.get(sExpress.substring(0, 2).toLowerCase()) + (String) liParamGroup.get(0);
            sLink = ( (String) liParamGroup.get(1)).trim();
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                if (sExpress.substring(0, 2).equalsIgnoreCase("gz")) {
                    valData = new ValCompDataBean();
                    valData.setGzKeyCode(rs.getString("FKeyCode"));
                    valData.setGzKeyName(rs.getString("FKeyName").substring(1).trim());
                    valData.setGzCost(rs.getDouble("FCost") * rs.getDouble("FInOut"));
                    valData.setGzPortCost(rs.getDouble("FPortCost") * rs.getDouble("FInOut"));
                    valData.setGzMarketValue(rs.getDouble("FMarketValue") * rs.getDouble("FInOut"));
                    valData.setGzPortMarketValue(rs.getDouble("FPortMarketValue") * rs.getDouble("FInOut"));
                    valData.setGzAmount(rs.getDouble("FSParAmt"));
                    valData.setGzReTypeCode(rs.getString("FReTypeCode")); //2008.06.12 蒋锦 添加 用于判断类型
                    hmResult.put(rs.getString(sLink), valData);
                } else if (sExpress.substring(0, 2).equalsIgnoreCase("cw")) {
                    valData = new ValCompDataBean();
                    valData.setCwKeyCode(rs.getString("FAcctCode"));
                    valData.setCwKeyName(rs.getString("FAcctName"));
                    valData.setCwCost(rs.getDouble("FCost"));
                    valData.setCwPortCost(rs.getDouble("FStandardMoneyCost"));
                    valData.setCwMarketValue(rs.getDouble("FMarketValue"));
                    valData.setCwPortMarketValue(rs.getDouble(
                        "FStandardMoneyMarketValue"));
                    valData.setCwAmount(rs.getDouble("FAmount"));
                    hmResult.put(rs.getString(sLink), valData);
                }
              //fanghaoln 20100127 MS00912 QDV4南方2010年1月7日01_A  
                	else if (sExpress.substring(0, 2).equalsIgnoreCase("gh")) {
                    valData = new ValCompDataBean();
                    valData.setGzKeyCode(rs.getString("FKeyCode"));
                    valData.setGzKeyName(rs.getString("FKeyName").substring(0).trim());
                    valData.setGzCost(rs.getDouble("FCost") * rs.getDouble("FInOut"));
                    valData.setGzPortCost(rs.getDouble("FPortCost") * rs.getDouble("FInOut"));
                    valData.setGzMarketValue(rs.getDouble("FMarketValue") * rs.getDouble("FInOut"));//核对这资产净值估值
                    valData.setGzPortMarketValue(rs.getDouble("FPortMarketValue") * rs.getDouble("FInOut")==0?rs.getDouble("FPrice") * rs.getDouble("FInOut"):rs.getDouble("FPortMarketValue") * rs.getDouble("FInOut"));
                    valData.setGzAmount(rs.getDouble("FSParAmt"));
                    valData.setGzReTypeCode(rs.getString("FReTypeCode")); //2008.06.12 蒋锦 添加 用于判断类型
                    hmResult.put(rs.getString(sLink), valData);
                } else if (sExpress.substring(0, 2).equalsIgnoreCase("ch")) {
                    valData = new ValCompDataBean();
                    valData.setCwKeyCode(rs.getString("FAcctCode"));
                    valData.setCwKeyName(rs.getString("FAcctName"));
                    valData.setCwCost(rs.getDouble("FCost"));
                    valData.setCwPortCost(rs.getDouble("FStandardMoneyCost"));
                    valData.setCwMarketValue(rs.getDouble("FMarketValue"));
                    valData.setCwPortMarketValue(rs.getDouble(//核对这资产净值 财物
                        "FStandardMoneyMarketValue")==0?rs.getDouble("FSTANDARDMONEYMARKETVALUETORAT"):rs.getDouble("FStandardMoneyMarketValue"));
                    valData.setCwAmount(rs.getDouble("FAmount"));
                    hmResult.put(rs.getString(sLink), valData);
                }
                //------------------------------end-----MS00912----------------------------------------------------------
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmResult;
    }
}
