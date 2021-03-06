package com.yss.commeach;

import java.sql.*;

import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EachOperationRela
    extends BaseCommEach {
    public EachOperationRela() {
    }

    private String sFields = "";        //表字段 多个中间用\t分隔
    private String sFieldValues = "";   //字段的值 多个中间用\t分隔
    private String sCaption = "";       //补充说明信息
    private String sExistsTab = "";     // 不执行的表 多个中间用\t分隔
    private boolean bRela = false;      //是否有关联数据，默认为不关联
    private byte bOper = -1;            //操作类型
    private StringBuffer TableName = new StringBuffer(); //保存存在引用的表名 fanghaoln 090323 MS00009 数据完整性控制
    private boolean bDoOper = false;    //默认为不操作

    /**
     * 给EachOperationRela 赋值的方法
     * @param operRela EachOperationRela
     */
    public void setOperationRelaBean(EachOperationRela operRela) {
        this.sFields = operRela.getSFields();
        this.sFieldValues = operRela.getSFieldValues();
        this.sCaption = operRela.getSCaption();
        this.sExistsTab = operRela.getSExistsTab();
        this.bOper = operRela.getBOper();
        this.bDoOper = operRela.isBDoOper();
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(sCaption).append("\t");
        buf.append(bRela).append("\tnull");
        return buf.toString();
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改时间:20090401
    /// BugNO  : MS00009  数据完整性控制 QDV4.1赢时胜上海2009年2月1日08_A
    /// 增加一个数据完整性提示功能.增加一个新的组合方法，存在引用的表名也组合起来传到前台
    /// </summary>
    public String buildRowStrNew() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(sCaption).append("\t");      //组合补充说明信息
        buf.append(bRela).append("\t");         //把是否存在引用表作为一个标识符号传到前台
        buf.append(TableName).append("\tnull"); //组合存在引用的表传到前台
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        if (sType != null && sType.equalsIgnoreCase("operationRela")) {
            getOperationRela();
            return buildRowStr();
        }
        //fanghaoln 090323 MS00009 数据完整性控制
        //这是用来处理前台传来的批量数据的处理的情况
        if (sType != null && sType.equalsIgnoreCase("operationRelaNew")) { //前台传来一个标志区分上面的方法
            getOperationRelaNew();      //调用批量处理数据完整性方法
            return buildRowStrNew();    //调用组合方法，处理后的数据组合起来传到前台
        }
        return "";
    }

    public boolean getOperationRela() throws YssException {
        ResultSet rs = null;
        ResultSet rsTmp = null;
        String sqlStr = "";
        bRela = false; //这里先设置为false要不然当第二次操作会变为true出错
        try {
            if (!bDoOper) {
                return false;
            }
            if (dbl.dbType == YssCons.DB_ORA) {
                //数据完整性优化，orcal10查出回收站里的表。使查出的表名出错的BUG fanghaoln 20090513
                sqlStr = "select Table_Name from user_tab_cols a where 1=1 " +
                    getFieldName(sFields) +
                    (sExistsTab.length() > 0 ? getTabName(sExistsTab) : "") +
                    " and a.Table_Name not like '%TEMP%' and a.Table_Name not like '%TMP%' and a.Table_Name not like '%000' " +
                    //fanghaoln 20090601 修改SQL语句因为反应太慢
                    " and a.table_name not like'%==$0' " +
                    //fanghao 20090610 把不要的视图去除
                    " and a.table_name like'TB/_%'ESCAPE'/' " +
                    " order by Table_Name";
            } else if (dbl.dbType == YssCons.DB_DB2) {
                sqlStr =
                    "select distinct table_name from SYSIBM.COLUMNS_S  where 1=1 " +
                    getFieldName(sFields) +
                    (sExistsTab.length() > 0 ? getTabName(sExistsTab) : "") +
                    " and Table_Name not like '%TEMP%' and Table_Name not like '%TMP%' and Table_Name not like '%000' " +
                    //fanghaoln 20090601 修改SQL语句因为反应太慢
                    " and Table_Name not like'%==$0' " +
                    //fanghao 20090610 把不要的视图去除
                    " and Table_Name like'TB/_%'ESCAPE'/' " +
                    " order by Table_Name";
            }
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                sqlStr = "select '1' from " + rs.getString("Table_Name") +
                    " where 1=1 " + getFieldValue(sFields, sFieldValues);
                rsTmp = dbl.openResultSet(sqlStr);
                if (rsTmp.next()) {
                    bRela = true;
                }
                if (bRela) {
                    break;
                }
                dbl.closeResultSetFinal(rsTmp);
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs, rsTmp);
        }
        return bRela;
    }

    /**修改时间：090323
     * 修改人：fanghaoln
     * BugNO  : MS00009 数据完整性控制
     * 方法功能：判断数据完整性找出相关连的数据表，组合存在引用数据表的表名传到前台
     * 前台传来的批量数据，对每一条数据进行处理，找出它们的引用传到前台。
     * @throws YssException
     */
    public boolean getOperationRelaNew() throws YssException {
        ResultSet rs = null;
        ResultSet rsTmp = null;
        String sqlStr = ""; //sql语句
        String sqlSt = "";
        bRela = false; //这里先设置为false要不然当第二次操作会变为true出错
        //<--
        String sqlCheck="";
        ResultSet checkTmp = null;//edited by libo MS00595 回收站里的审核证券信息应匹配提示   QDV4赢时胜（上海）2009年7月24日14_B-->
        String[] fieldValueses = sFieldValues.split("\t"); //解析传来批量个数
        try {
            if (!bDoOper) {
                return false;
            }
            if (dbl.dbType == YssCons.DB_ORA) {
                //数据完整性优化，orcal10查出回收站里的表。使查出的表名出错的BUG fanghaoln 20090513
                sqlStr = "select Table_Name from user_tab_cols a where 1=1 " +
                    getFieldName(sFields) +
                    (sExistsTab.length() > 0 ? getTabName(sExistsTab) : "") +
                    " and a.Table_Name not like '%TEMP%' and a.Table_Name not like '%TMP%' and a.Table_Name not like '%000' " +
                    //fanghaoln 20090601 修改SQL语句因为反应太慢
                    " and a.table_name not like'%==$0' " +
                    //fanghao 20090610 把不要的视图去除
                    " and a.table_name like'TB/_%'ESCAPE'/' " +
                    //MS00684 QDV4赢时胜（上海）2009年9月05日01_B fanghaoln 20090926
                    " and a.table_name like'%"+pub.getPrefixTB()+"%' " +
                    //--------------------------------end MS00684---------------------
                    " order by Table_Name";
            } else if (dbl.dbType == YssCons.DB_DB2) {
                sqlStr =
                    "select distinct table_name from SYSIBM.COLUMNS_S  where 1=1 " +
                    getFieldName(sFields) +
                    (sExistsTab.length() > 0 ? getTabName(sExistsTab) : "") +
                    " and Table_Name not like '%TEMP%' and Table_Name not like '%TMP%' and Table_Name not like '%000' " +
                    //fanghaoln 20090601 修改SQL语句因为反应太慢
                    " and Table_Name not like'%==$0' " +
                    //fanghao 20090610 把不要的视图去除
                    " and Table_Name like'TB/_%'ESCAPE'/' " +
                    //MS00684 QDV4赢时胜（上海）2009年9月05日01_B fanghaoln 20090926
                    " and table_name like'%"+pub.getPrefixTB()+"%' " + //modified by yeshenghong 20120620 to fit DB2
                    //--------------------------------end MS00684---------------------
                    " order by Table_Name"; //查出DB2数据库里面存在表字段的表名
            }
            for (int i = 0; i < fieldValueses.length; i++) { //批量个数循环
                rs = dbl.openResultSet(sqlStr); //执行SQL语句查出存在这个表字段的表

                while (rs.next()) { //循环存在当前字段的每一个表
                    //<------edited by libo MS00595 回收站里的审核证券信息应匹配提示   QDV4赢时胜（上海）2009年7月24日14_B
                    boolean s=false;
                    sqlCheck="select * from "+rs.getString("Table_Name");//查取出表名
                    checkTmp = dbl.openResultSet(sqlCheck);
                    ResultSetMetaData metaData = checkTmp.getMetaData();//取出表的字段名
                    for(int k=1;k<metaData.getColumnCount();k++)
                    {
                        if(metaData.getColumnName(k).equalsIgnoreCase("Fcheckstate"))
                        {
                            s=true;//有Fcheckstate字段的为true
                        }
                    }
                    if(s)
                    {
                        sqlSt = "select '1' from " + rs.getString("Table_Name") +
                            " where 1=1 " +
                            " and Fcheckstate!=2 " + //edited by libo MS00595 回收站里的审核证券信息应匹配提示   QDV4赢时胜（上海）2009年7月24日14_B
                            getFieldValue(sFields, fieldValueses[i]);
                    }else//字段没有Fcheckstate不加这个条件
                    {
                        sqlSt = "select '1' from " + rs.getString("Table_Name") +
                            " where 1=1 " +
                            getFieldValue(sFields, fieldValueses[i]);
                    }
                    ////edited by libo MS00595 回收站里的审核证券信息应匹配提示   QDV4赢时胜（上海）2009年7月24日14_B------>
                    rsTmp = dbl.openResultSet(sqlSt); //查出存在当前引用的表

                    if (rsTmp.next()) {     //如果存在当前引用的表
                        bRela = true;       //传到前台判断是否有存在引用的表
                        TableName.append(rs.getString("Table_Name")).append("~f~"); //把查出的表名组合起来
                    }
                    dbl.closeResultSetFinal(rsTmp);     //关闭事物，防止游标出界报错
                    dbl.closeResultSetFinal(checkTmp);  //edited by libo
                }
                TableName.append("~l~");        //区分每个条目加一个解析符号
                dbl.closeResultSetFinal(rs);    //关闭事物，防止游标出界报错
            }
            if (TableName.toString().length() > 3) { //当字符串尾部加上~l~时
                TableName = TableName.delete(TableName.length() - 4,
                                             TableName.length() - 1); //去除尾部多余的解析字符
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(checkTmp,rs, rsTmp);
        }
        return bRela;
    }

    private String getTabName(String sTabName) {
        String[] arrTab = sTabName.split("\t");
        String sRes = "";
        for (int i = 0; i < arrTab.length; i++) {
            if (arrTab[i].length() == 0) {
                continue;
            }
            //fanghaoln 20090610 权限明细到组合BUG
            if (arrTab[i].equalsIgnoreCase("tb_para_security")) { //证券信息处理后得到的是它的视图所以加一个查询条件去除它
                sRes += " and upper(Table_Name) not like " + dbl.sqlString("%" +
                    YssFun.right(arrTab[i], arrTab[i].length() - 2).toUpperCase()); //尾部是证券信息的都去除不查出来
            }
            //fanghaoln 20090610 权限明细到组合BUG
            if (arrTab[i].equalsIgnoreCase("Tb_Para_Purchase")) { //回购品种信息设置处理后得到的是它的视图所以加一个查询条件去除它
                sRes += " and upper(Table_Name) not like " + dbl.sqlString("%" +
                    YssFun.right(arrTab[i], arrTab[i].length() - 2).toUpperCase()); //尾部是回购品种信息的都去除不查出来
            }
            //fanghaoln 20090610 权限明细到组合BUG
            if (arrTab[i].equalsIgnoreCase("Tb_Para_IndexFutures")) { //股指期货处理后得到的是它的视图所以加一个查询条件去除它
                sRes += " and upper(Table_Name) not like " + dbl.sqlString("%" +
                    YssFun.right(arrTab[i], arrTab[i].length() - 2).toUpperCase()); //尾部是股指期货的都去除不查出来
            }
            sRes += " and upper(Table_Name) not like " + dbl.sqlString(pub.yssGetTableName(arrTab[i]).replaceAll(pub.getPrefixTB(),"%").toUpperCase());
        }
        return sRes;
    }

    private String getFieldName(String sFieldName) {
        String[] arrField = sFieldName.split("\t");
        String sRes = "";
        for (int i = 0; i < arrField.length; i++) {
            if (arrField[i].length() == 0) {
                continue;
            }
            sRes += " and upper(Column_Name)= " +
                dbl.sqlString(arrField[i].toUpperCase());
        }
        return sRes;
    }
    
    private String getFieldValue(String sFieldName, String sFieldValue) {
        String[] arrField = sFieldName.split("\t");
        String sRes = "";
        for (int i = 0; i < arrField.length; i++) {
            if (arrField[i].length() == 0) {
                continue;
            }
            sRes += " and " + arrField[i] + " = " +
                dbl.sqlString(sFieldValue.split("\t")[i]);
        }
        return sRes;
    }
    
    private String buildFilterCondition(String sFieldName, String[] fieldValueses)
    {
    	String condStr = "";
    	String condStr2 = "";
    	for(int i=0;i<fieldValueses.length;i++)
    	{
    	//add by maxin BUG #86549 QDV4赢时胜(上海)2013年12月27日01_B（场内交易业务界面大批量反审核数据报错，提示“列表中的最大表达式数为1000”）
    		if(i>999){
    		condStr2 += dbl.sqlString(fieldValueses[i]) + ",";
    		}else{
    		condStr += dbl.sqlString(fieldValueses[i]) + ",";
    		}
    	}	
    	condStr = condStr.substring(0,condStr.length()-1);
    	condStr2 = condStr2.substring(0,condStr2.length()-1);
    	String sRes = " and " + sFieldName + " in (" + condStr + ") or "+ sFieldName +" in ("+ condStr2 + ")" ;
    	return sRes;
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] arrReq = null;
        if (sRowStr.length() == 0) {
            return;
        }
        arrReq = sRowStr.split("\r\n", -1);
        this.sFields = arrReq[0];
        this.sFieldValues = arrReq[1];
        sCaption = arrReq[2];
        sExistsTab = arrReq[3];
        if (arrReq[4].length() > 0 && arrReq[4].equalsIgnoreCase("true")) {
            bRela = true;
        } else {
            bRela = false;
        }
        if (YssFun.isNumeric(arrReq[5])) {
            bOper = (byte) YssFun.toInt(arrReq[5]);
        }
        if (arrReq[6].length() > 0 && arrReq[6].equalsIgnoreCase("true")) {
            bDoOper = true;
        } else {
            bDoOper = false;
        }
    }

    public String getSFields() {
        return sFields;
    }

    public String getSFieldValues() {
        return sFieldValues;
    }

    public String getSExistsTab() {
        return sExistsTab;
    }

    public String getSCaption() {
        return sCaption;
    }

    public boolean isBRela() {
        return bRela;
    }

    public void setBDoOper(boolean bDoOper) {
        this.bDoOper = bDoOper;
    }

    public void setSFields(String sFields) {
        this.sFields = sFields;
    }

    public void setSFieldValues(String sFieldValues) {
        this.sFieldValues = sFieldValues;
    }

    public void setSExistsTab(String sExistsTab) {
        this.sExistsTab = sExistsTab;
    }

    public void setSCaption(String sCaption) {
        this.sCaption = sCaption;
    }

    public void setBRela(boolean bRela) {
        this.bRela = bRela;
    }

    public void setBOper(byte bOper) {
        this.bOper = bOper;
    }

    public void setTableName(StringBuffer TableName) {
        this.TableName = TableName;
    }

    public boolean isBDoOper() {
        return bDoOper;
    }

    public byte getBOper() {
        return bOper;
    }

    public StringBuffer getTableName() {
        return TableName;
    }

}
