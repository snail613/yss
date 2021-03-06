package com.yss.main.datainterface.compare;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.base.*;
import com.yss.main.dao.*;
import com.yss.main.datainterface.*;
import com.yss.util.*;

/**
 * 接口核对再保存功能的一个辅助类，功能为：
 * 处理查询条件、操作临时表、解析SQl及获取SQL的值
 * QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090430
 */
public class DaoCompQuery
    extends
    BaseCalcFormula implements IYssConvert {
    public DaoCompQuery() {
    }

    private java.util.Date dStartDate = null; //起始日期
    private java.util.Date dEndDate = null; //结束日期
    private String sCompType = ""; //核对类型

    private Hashtable htDynamic; //动态条件

    private String sCompCode = ""; //数据比对代码
    private String sTempTab = ""; //临时表名称
    private HashMap hmFieldType; //字段类型

    /**
     * 下面的属性变量用于做字符串字符转换时用的
     */
    private HashMap hmSpecialValue; //特殊字符的值
    private ArrayList alField; //字符数组
    private ResultSet rsValue = null;

    /**
     * 解析查询条件
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] arrReq = null;
        arrReq = sRowStr.split("\f\f");
        if (YssFun.isDate(arrReq[0])) {
            dStartDate = YssFun.toDate(arrReq[0]);
        }
        if (YssFun.isDate(arrReq[1])) {
            dEndDate = YssFun.toDate(arrReq[1]);
        }
        sCompType = arrReq[2];
        sCompCode = arrReq[3];
        //下面为动态条件部分
        if (arrReq.length <= 4) {
            return;
        }
        if (htDynamic == null) {
            htDynamic = new Hashtable();
        }
        for (int i = 4; i < arrReq.length; i++) {
            //key=字段代码，value=字段的值
            htDynamic.put(arrReq[i].split("\t")[0], arrReq[i].split("\t")[1]);
        }
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void init(Object obj) throws YssException {
        this.sign = "(,),+,-,*,/,[,],#";
        this.formula = (String) obj;
    }

    public Object getExpressValueEx(String sExpress, ArrayList alParams,
                                    String sEndStr) throws YssException {
        String sResult = "";
        try {
            if (sExpress.toLowerCase().endsWith("#")) {
                //sResult = prepSpecialStr(sExpress,alParams);
            }
            sResult += sEndStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return sResult;
    }

    public Object getExpressValue(String sExpress, ArrayList alParams) throws
        YssException {
        return getExpressValueEx(sExpress, alParams, "");
    }

    /**
     * 根据关键字获取相应的数据
     * @param sKeyword String
     * @return Object
     * @throws YssException
     */
    public Object getKeywordValue(String sKeyword) throws YssException {
        Object objResult = null;
        try {
            if (sKeyword.equals("+")) {
                objResult = "+";
            } else if (sKeyword.equals("-")) {
                objResult = "-";
            } else if (sKeyword.equals("*")) {
                objResult = "*";
            } else if (sKeyword.equals("/")) {
                objResult = "/";
            } else if (sKeyword.equals("(")) {
                objResult = "(";
            } else if (sKeyword.equals(")")) {
                objResult = ")";
            } else {
                if (this.hmFieldType != null && this.hmFieldType.get(sKeyword) != null) { //这里在核对时用到
                    if ( ( (String) hmFieldType.get(sKeyword)).indexOf("CHAR") > -1) {
                        objResult = rsValue.getString(sKeyword);
                    } else if ( ( (String) hmFieldType.get(sKeyword)).indexOf("DATE") > -1) {
                        objResult = YssFun.formatDate(rsValue.getDate(sKeyword), "yyyy-MM-dd");
                    } else if ( ( (String) hmFieldType.get(sKeyword)).indexOf("NUMBER") > -1) {
                        objResult = new Double(rsValue.getDouble(sKeyword));
                    }
                } else { //这里在保存时用到
                    objResult = prepSpecialStr(sKeyword);
                }
            }
        } catch (Exception ex) {
            System.err.print(ex.getMessage());
        }
        return objResult;
    }

    /**
     * 用值将特定的字符串替换掉
     * @param sExpress String
     * @param alList ArrayList
     * @return String
     */
    private String prepSpecialStr(String sExpress) {
        String sResult = ""; //保存结果值，用于返回值
        String sSpecialValue = ""; //暂存特定字符的值,可从hmSpecialValue中取值
        String[] arrValue = null; //将sSpecialValue值用逗号分隔，然后根据字段类型转换相应的格式

        sResult = sExpress;
        for (int iF = 0; iF < alField.size(); iF++) {
            DaoCompareField field = (DaoCompareField) alField.get(iF);
            sSpecialValue = String.valueOf(this.hmSpecialValue.get(field.getSFieldCode()));
            if (field.getSFieldType().equalsIgnoreCase("varchar")) { //字符型的就用''号隔开
                sSpecialValue = operSql.sqlCodes(sSpecialValue);
            } else if (field.getSFieldType().equalsIgnoreCase("number")) {
                arrValue = sSpecialValue.split(",");
                sSpecialValue = "";
                for (int i = 0; i < arrValue.length; i++) {
                    if (arrValue[i].length() == 0) {
                        arrValue[i] = "0"; //默认值
                    }
                    sSpecialValue = sSpecialValue + arrValue[i] + ",";
                }
            } else {
                arrValue = sSpecialValue.split(",");
                sSpecialValue = "";
                for (int i = 0; i < arrValue.length; i++) {
                    if (arrValue[i].length() == 0) {
                        arrValue[i] = dbl.sqlDate("1900-01-01"); //默认值
                    } else {
                        arrValue[i] = dbl.sqlDate(arrValue[i]);
                    }
                    sSpecialValue = sSpecialValue + arrValue[i] + ",";
                }
            }
            if (sSpecialValue.endsWith(",")) {
                sSpecialValue = sSpecialValue.substring(0, sSpecialValue.length() - 1);
            }
            if (sResult.equalsIgnoreCase(field.getSFieldCode())) {
                sResult = sResult.replaceAll(field.getSFieldCode(), sSpecialValue);
                return sResult;
            }
        }
        if (sResult.equals("\\[") || sResult.equals("\\]") || sResult.equals("#")) { //去掉[,],#号特殊符号
            sResult = "";
        }
        return sResult;
    }

    /**
     * 获取一个值
     */
    public Object getResultSet(Object value) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        try {
            sqlStr = "select " + value + " as FValue from Tb_Sys_UserList";
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                value = rs.getObject("FValue");
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return value;
    }

    /**
     * 创建临时表的过程
     */
    public void createTempTab() throws YssException {
        String sqlStr = "";
        StringBuffer buf = null;
        String PkFields = ""; //主键
        ResultSet rs = null;
        String sFieldPre = "";
        String sFieldType = "";
        try {
            if (dbl.yssTableExist(this.sTempTab)) {
                return;
            }
            buf = new StringBuffer();
            sqlStr = "select * from " + pub.yssGetTableName("Tb_Dao_CompField") +
                " where FCompCode=" + dbl.sqlString(sCompCode) + " order by FOrderIndex";
            rs = dbl.openResultSet(sqlStr);
            if (dbl.dbType == YssCons.DB_ORA) {
                while (rs.next()) {
                    if (rs.getString("FFieldType").equalsIgnoreCase("date")) {
                        sFieldType = "date";
                        sFieldPre = "";
                    } else if (rs.getString("FFieldType").equalsIgnoreCase("varchar")) {
                        sFieldType = "varchar2";
                        sFieldPre = "(" + rs.getString("FFieldPre") + ")";
                    } else {
                        sFieldType = "number";
                        sFieldPre = "(" + rs.getString("FFieldPre") + ")";
                    }
                    if (rs.getInt("FPKFIELD") == 1) {
                        PkFields += rs.getString("FFIELDCODE") + ",";
                    }
                    buf.append(rs.getString("FFieldCode")).append(" ").append(sFieldType).append(" ").append(sFieldPre).append(" ,"); //字段名称与精度
                }
                buf.append("FID").append(" ").append("Varchar2(20) "); //去掉最后一个逗号，因为这时不加载表这键信息
                PkFields += "FID,";
                if (PkFields.endsWith(",")) {
                    PkFields = PkFields.substring(0, PkFields.length() - 1);
                }
                if (PkFields.length() > 0) {
                    PkFields = " CONSTRAINT PK_" + sTempTab + " PRIMARY KEY(" +
                        PkFields + ")";
                }

                sqlStr = "create table " + sTempTab + " (" + buf.toString() + ")"; //去掉最后一个逗号 by leeyu 20090430 经与杨芳沟通，他认为去掉建临时表主键为好

            } else if (dbl.dbType == YssCons.DB_DB2) {
                while (rs.next()) {
                    if (rs.getString("FFieldType").equalsIgnoreCase("date")) {
                        sFieldType = "date";
                        sFieldPre = "";
                    } else if (rs.getString("FFieldType").equalsIgnoreCase("varchar")) {
                        sFieldType = "varchar";
                        sFieldPre = "(" + rs.getString("FFieldPre") + ")";
                    } else {
                        sFieldType = "decimal";
                        sFieldPre = "(" + rs.getString("FFieldPre") + ")";
                    }
                    if (rs.getInt("FPKFIELD") == 1) {
                        PkFields += rs.getString("FFIELDCODE") + ",";
                    }
                    buf.append(sFieldType).append(" ").append(sFieldPre).append(" ,"); //字段名称与精度
                }
                buf.append("FID").append(" ").append("Varchar(20) "); //去掉最后一个逗号，因为这时不加载表这键信息
                PkFields += "FID,";
                if (PkFields.endsWith(",")) {
                    PkFields = PkFields.substring(0, PkFields.length() - 1);
                }
                String sPKStr = "";
                if (sTempTab.length() > 14) {
                    sPKStr = sTempTab.substring(0, 13);
                } else {
                    sPKStr = sTempTab;
                }
                if (PkFields.length() > 0) {
                    PkFields = " CONSTRAINT PK_" + sPKStr + " PRIMARY KEY(" +
                        PkFields + ")";
                }

                sqlStr = "create table " + sTempTab + " (" + buf.toString() + ")"; //去掉最后一个逗号 by leeyu 20090430 经与杨芳沟通，他认为去掉建临时表主键为好
            }
            dbl.executeSql(sqlStr);
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 将SQL重新处理
     * @param sql String
     * @return String
     */
    public String buildSql(String sql) {
        if (dStartDate != null) {
            sql = sql.replaceAll("<D1>", dbl.sqlDate(YssFun.formatDate(dStartDate)));
            sql = sql.replaceAll("< D1 >", dbl.sqlDate(YssFun.formatDate(dStartDate)));
            sql = sql.replaceAll("<d1>", dbl.sqlDate(YssFun.formatDate(dStartDate)));
            sql = sql.replaceAll("< d1 >", dbl.sqlDate(YssFun.formatDate(dStartDate)));
        }
        if (dEndDate != null) {
            sql = sql.replaceAll("<D2>", dbl.sqlDate(YssFun.formatDate(dEndDate)));
            sql = sql.replaceAll("< D2 >", dbl.sqlDate(YssFun.formatDate(dEndDate)));
            sql = sql.replaceAll("<d2>", dbl.sqlDate(YssFun.formatDate(dEndDate)));
            sql = sql.replaceAll("< d2 >", dbl.sqlDate(YssFun.formatDate(dEndDate)));
        }
        DaoPretreatBean daoPret = new DaoPretreatBean();
        daoPret.setYssPub(pub);
        sql = daoPret.buildSql(sql);
        return sql;
    }

    /**
     * 删除表
     * @throws YssException
     */
    public void dropTempTab() throws YssException {
        try {
            if (dbl.yssTableExist(sTempTab)) {
            	/**shashijie 2011-10-21 STORY 1698 */
                dbl.executeSql(dbl.doOperSqlDrop("drop table " + sTempTab));
                /**end*/
            }
        } catch (Exception ex) {
            throw new YssException("删除临时表出错");
        }
    }

    /**
     * 快速删除表数据
     * @param iType int
     * @return String
     */
    public void deleteTempTab() throws YssException {
        String sqlStr = "";
        try {
            if (dbl.dbType == YssCons.DB_ORA) {
                sqlStr = "truncate table " + this.sTempTab;
            } else if (dbl.dbType == YssCons.DB_DB2) {

            } else if (dbl.dbType == YssCons.DB_SQL) {

            }
            dbl.executeSql(sqlStr);
        } catch (Exception ex) {
            throw new YssException("删除临时表数据出错");
        }
    }

    public String getSCompType() {
        return sCompType;
    }

    public Hashtable getHtDynamic() {
        return htDynamic;
    }

    public Date getDStartDate() {
        return dStartDate;
    }

    public void setDEndDate(Date dEndDate) {
        this.dEndDate = dEndDate;
    }

    public void setSCompType(String sCompType) {
        this.sCompType = sCompType;
    }

    public void setHtDynamic(Hashtable htDynamic) {
        this.htDynamic = htDynamic;
    }

    public void setDStartDate(Date dStartDate) {
        this.dStartDate = dStartDate;
    }

    public void setSTempTab(String sTempTab) {
        this.sTempTab = sTempTab;
    }

    public void setSCompCode(String sCompCode) {
        this.sCompCode = sCompCode;
    }

    public Date getDEndDate() {
        return dEndDate;
    }

    public void setRsValue(ResultSet rs) {
        this.rsValue = rs;
    }

    public String getSTempTab() {
        return sTempTab;
    }

    public String getSCompCode() {
        return sCompCode;
    }

    public void setHmFieldType(HashMap hmFieldType) {
        this.hmFieldType = hmFieldType;
    }

    public void setHmSpecialValue(HashMap hmSpecialValue) {
        this.hmSpecialValue = hmSpecialValue;
    }

    public void setAlField(ArrayList alField) {
        this.alField = alField;
    }

}
