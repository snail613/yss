package com.yss.main.operdeal.report.compliance;

import java.util.*;
import java.sql.ResultSet;

import com.yss.base.*;
import com.yss.util.*;
import com.yss.main.compliance.CompIndexCfgBean;
import java.sql.ResultSetMetaData;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ScriptExpress
    extends BaseCalcFormula {
    ResultSet rsDataSource = null;
    CompIndexCfgBean indexCfg = null;

    public CompIndexCfgBean getIndexCfg() {
        return indexCfg;
    }

    public ResultSet getRsDataSource() {
        return rsDataSource;
    }

    public void setIndexCfg(CompIndexCfgBean indexCfg) {
        this.indexCfg = indexCfg;
    }

    public void setRsDataSource(ResultSet rsDataSource) {
        this.rsDataSource = rsDataSource;
    }

    public ScriptExpress() {
    }

    /**
     * 使用执行数据源后的 ResultSet 和 监控指标配置 来构造
     * @param rsDataSource ResultSet
     * @param indexCfg CompIndexCfgBean
     */
    public ScriptExpress(ResultSet rsDataSource, CompIndexCfgBean indexCfg) {
        this.rsDataSource = rsDataSource;
        this.indexCfg = indexCfg;
    }

    public String calcFormulaString() throws YssException {
        int iRsCount = 0;
        String sComp = "Natural";
        ResultSet rs = null;
        try {
            String[] arrScript = new String[3];
            arrScript[0] = this.indexCfg.getWarnAnalysis();
            arrScript[1] = this.indexCfg.getViolateAnalysis();
            arrScript[2] = this.indexCfg.getForbidAnalysis();

            this.sign = "(,), < , > , = , >= , <= ,+,-,*,/, and , or ,";
            for (int i = 0; i < arrScript.length; i++) {
                if (arrScript[i] == null || arrScript[i].length() == 0 || arrScript[i].equalsIgnoreCase("null")) {
                    continue;
                }
                this.formula = arrScript[i];
                if (this.validateScript()) {
                    switch (i) {
                        case 0:
                            sComp = "Warn";
                            break;
                        case 1:
                            sComp = "Violate";
                            break;
                        case 2:
                            sComp = "Forbid";
                            break;
                        default:
                            sComp = "Natural";
                    }
                }
                dbl.closeResultSetFinal(rs);
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sComp;
    }

    public Object getExpressValue(String sExpress, ArrayList alParams) throws
        YssException {
        String sResult = "";
        try {
            if (sExpress.equalsIgnoreCase("RsCount")) {
                sResult = getRsCountString(alParams);
            }
            //2008.06.16 蒋锦 添加 添加 YssIN 函数的判断
            else if (sExpress.equalsIgnoreCase("yssin")) {
                sResult = YssIn(sExpress, alParams);
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return sResult;
    }

    public Object getKeywordValue(String sKeyword) throws YssException {
        Object objResult = sKeyword;
        return objResult;
    }

    /**
     * 将 RSCount 函数解析为可执行的 SQL 语句 WHERE 条件
     * @param alParams ArrayList：函数的组成参数
     * @throws YssException
     * @return String
     */
    public String getRsCountString(ArrayList alParams) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String strResult = "";
        String sFiledName = "";
        String sFiledValue = "";
        String sFiledType = "";
        String sViewName = "";
        String sAllSelectFiled = "";
        String sParamStr = "";
        ResultSet rsTmp = null;
        String sValue = "";
        try {
            int iParamLen = alParams.size();
            for (int i = 0; i < iParamLen; i++) {
                sParamStr += (String) alParams.get(i) + ";";
            }
            //替换多余字符和转义字符
            sParamStr = sParamStr.substring(0, sParamStr.length() - 1);
            sParamStr = sParamStr.replaceAll("[!]|[！]", "-");
            sParamStr = sParamStr.replaceAll("'", "");
            sParamStr = sParamStr.replaceAll("&&", "AND");
            sParamStr = sParamStr.replaceAll("[|][|]", "OR");
            sParamStr = sParamStr.replaceAll("[{]", "(");
            sParamStr = sParamStr.replaceAll("[}]", ")");

            HashMap hmFiledType = dbFun.getFieldsType(this.rsDataSource);

            boolean bIsFind = true;
            //匹配不以小括号和空格开头和结尾的用两个分号隔开的三串字符
            //第一串必须是字符类型的字符串
            //第二串必须为非字符类型的字符串
            //第三串必须为数字类型的字符串(包括小数)或者字符类型的字符串
            //目的是匹配出RSCount函数体中的内容
            Pattern p = Pattern.compile("([^\\(| ])(\\w)+[;](\\W)+[;](([\\d]+[.]{1}[\\d]+)|(\\w)+)(|[^\\)| ])");
            while (bIsFind) {
                Matcher m = p.matcher(sParamStr);
                if (!m.find()) {
                    bIsFind = false;
                    break;
                }
                int iStart = m.start();
                int iEnd = m.end();
                sParamStr = sParamStr.substring(0, iStart) + "<@>" + sParamStr.substring(iEnd);
                String[] arrParam = m.group().split(";");
                if (arrParam.length != 3) {
                    throw new YssException("RSCount[] 函数解析错误，参数个数出错！");
                }
                sFiledName = arrParam[0];
                sFiledType = (String) hmFiledType.get(sFiledName.toUpperCase());
                sFiledValue = arrParam[2];

                if (sFiledType == null) {
                    throw new YssException("脚本解析出现异常，参数" + sFiledName +
                                           " 不存在，用户脚本无法执行，请核对函数 RsCount[] 的第一个参数！");
                }
                if (sFiledType.equalsIgnoreCase("Varchar") || sFiledType.equalsIgnoreCase("Varchar2") || sFiledType.equalsIgnoreCase("CHAR")) {
                    sFiledValue = dbl.sqlString(sFiledValue);
                } else if (sFiledType.equalsIgnoreCase("Date")) {
                    sFiledValue = dbl.sqlDate(sFiledValue);
                }
                String strTmp = "";
                strTmp = sFiledName + arrParam[1] + sFiledValue;
                sParamStr = sParamStr.replaceAll("<@>", strTmp);
                m.reset();
            }

            sViewName = "V_Temp_IndexDS_" + pub.getUserCode();
            if (dbl.yssViewExist(sViewName)) {
                dbl.executeSql("DROP VIEW " + sViewName);
            }
            //-----------------提取数据源中 Select 的字段------------------//
            rsTmp = dbl.openResultSet(this.indexCfg.getIndexDS(), ResultSet.TYPE_SCROLL_INSENSITIVE);
            ResultSetMetaData rsmd = rsTmp.getMetaData();
            int iFiledCount = rsmd.getColumnCount();
            for (int i = 0; i < iFiledCount; i++) {
                sAllSelectFiled += rsmd.getColumnName(i + 1) + ",";
            }
            sAllSelectFiled = sAllSelectFiled.substring(0, sAllSelectFiled.length() - 1);
            dbl.closeResultSetFinal(rsTmp);
            //----------------------------------------------------------//
            if (dbl.dbType == YssCons.DB_ORA) {
                dbl.executeSql("CREATE VIEW " + sViewName + " (" + sAllSelectFiled + " )" +
                               " AS (" + this.indexCfg.getIndexDS() + ")");
            } else {
                dbl.executeSql("CREATE VIEW " + sViewName + " (" + sAllSelectFiled + " )" +
                               " AS " + this.indexCfg.getIndexDS());
            }
            strSql = "SELECT COUNT(*) AS FCOUNT FROM " + sViewName +
                " WHERE " + sParamStr;
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                strResult = String.valueOf(rs.getInt("FCOUNT"));
            }
            return strResult;
        } catch (Exception e) {
            throw new YssException("执行函数 RsCount[] 出错，请检查用户数据源！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsTmp);
        }
    }
}
