package com.yss.dbupdate.autoupdatetables.updateexecute;

import java.util.*;

import com.yss.dbupdate.autoupdatetables.entitycreator.*;
import com.yss.dbupdate.autoupdatetables.entitycreator.pojo.*;
import com.yss.dbupdate.autoupdatetables.sqlstringbuild.*;
import com.yss.dsub.*;
import com.yss.util.*;
import java.sql.ResultSet;

/**
 *
 * <p>Title: 实际的自动更新和更新前检查的基类</p>
 *
 * <p>Description: 本类是更新和检查的基类，实际的更新流程由基类控制，更新前检查和实际更新通过实现基类的抽象方法来进行不同的操作；
 * 以 Standard 开头的方法是在基类中实现的标准方法，如果没有特殊要求子类可直接调用这些方法，完成对于基类抽象方法的实现，将具体实现放在基类中由子类进行选择，
 * 目定是保证灵活性的同时，保证对于实现代码的控制，如非特别必要，子类无需出现实现代码；
 * 本类中除入口方法 updExecute 对外开放外，其他所有方法对外封闭，updExecute 方法不允许子类继承和重写。除 updExecute 方法外的所有方法中的形参 sqlBuf，
 * 皆用于保存所执行的 SQL 语句。
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class BaseUpdateExecute
    extends BaseBean {
    /**
     * 用来生成储存表结构数据实体类的操作类
     */
    protected OperTableData oper;
    /**
     * 用于产生更新表结构的 SQL 语句
     */
    protected SqlStringBuilder sqlBuild;

    /**
     * 储存新的表结构数据的实体
     */
    protected TableBean newTable;
    /**
     * 储存旧的表结构数据的实体
     */
    protected TableBean oldTable;
    /**
     * 更新过程中需要使用的临时表名，检查的过程和更新的过程产生的表名是不一样的
     */
    protected String tmpTableName = "";

    public BaseUpdateExecute() {
    }

    /**
     * 用于已准备好的异常字符串
     * @return String
     * @throws YssException
     */
    protected abstract String getErrorMessage() throws YssException;

    /**
     * 获取临时表表名
     * @return String
     * @throws YssException
     */
    protected abstract String getTmpTableName() throws YssException;

    /**
     * 删除原表的主键，如果以后除了主键还有其他的约束也可以考虑在此方法中实现
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected abstract void dropTableCons(StringBuffer sqlBuf) throws
        YssException;

    /**
     * 更改现有表的表名
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected abstract void renameTable(StringBuffer sqlBuf) throws YssException;

    /**
     * 创建新的表结构
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected abstract void createTable(StringBuffer sqlBuf) throws YssException;

    /**
     * 将旧表中的数据复制到新表
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected abstract void copyDate(StringBuffer sqlBuf) throws YssException;

    /**
     * 创建新表的主键，如果以后除了主键还有其他的约束也可以考虑在此方法中实现
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected abstract void createTableCons(StringBuffer sqlBuf) throws
        YssException;

    /**
     * 删除临时表
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected abstract void dropTmpTable(StringBuffer sqlBuf) throws
        YssException;

    /**
     * 记录更新的表名
     * @param updateTables StringBuffer
     * @throws YssException
     */
    protected abstract void registerTableName(StringBuffer updateTables) throws YssException;

    /**
     * 标准约束删除过程
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void StandardDropTableConsProc(StringBuffer sqlBuf) throws YssException {
        String sql = "";
        String indexName = null;
        try {
            if (oldTable == null) {
                return;
            }
            sql = sqlBuild.getDropConsStr(oldTable);
            if (sql.trim().length() != 0) {
                sqlBuf.append(sql).append("\n");
                dbl.executeSql(sql);
            }
            //xuqiji 20090512:QDV4赢时胜（上海）2009年4月7日01_A   MS00352    新建组合群时能够自动创建对应的一套表----------//
            //无论什么数据库都需要判断是否存在将要被创建的索引
            if (newTable.getPkCons() != null) { //判断新表约束是否为空
                indexName = dbl.getTableIndexKey(newTable.getPkCons().getFCONSTRAINTNAME()); //不为空根据新表约束查询本地库中已存在的索引
            } else {
                if (oldTable.getPkCons() != null) { //如果新表约束为空，则根据本地表的约束查询本地表中存在的索引
                    indexName = dbl.getTableIndexKey(oldTable.getPkCons().getFCONSTRAINTNAME());
                }
            }
            //如果存数据库中仍存在索引，要查询到索引约束所在的表，并删除该表的约束
            if (indexName != null && indexName.trim().length() != 0) {
                dropAllConsByIndex(indexName,sqlBuf);
            }
            //-------------------------------------------end-----------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("删除表主键出错！\r\n", e);
        }
    }

    /**
     * 标准表创建过程，通过成员变量 newTable 中的数据进行创建
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void StandardCreateTableProc(StringBuffer sqlBuf) throws YssException {
        StandardCreateTableProc(sqlBuf, "");
    }

    /**
     * 标准表创建过程（重载），可定义创建的表名
     * @param sqlBuf StringBuffer
     * @param sTabName String：创建的表名
     * @throws YssException
     */
    protected void StandardCreateTableProc(StringBuffer sqlBuf, String sTabName) throws YssException {
        String sql = "";
        try {
            sql = sqlBuild.getCreateTableStr(newTable, sTabName);
            sqlBuf.append(sql).append("\n");
            dbl.executeSql(sql);
        } catch (Exception e) {
            throw new YssException("创建新表出错！\r\n", e);
        }
    }

    /**
     * 标准表修改过程
     * @param sqlBuf StringBuffer
     * @param sTabName String：修改的表名
     * @throws YssException
     * panjunfang add 2013-11-22 解决BUG 83351
     */
    protected void StandardAlterTableProc(StringBuffer sqlBuf, String sTabName) throws YssException {
        StringBuffer sql = null;
        String sAlter = "";
        ResultSet rs = null;
        try {
            if (null == oldTable) {
                return;
            }
        	sql = new StringBuffer(300);
            sql.append("ALTER TABLE ").append(sTabName.toUpperCase()).append(" modify (");
            sAlter = "SELECT DISTINCT a.*, b.data_length FROM (SELECT * FROM Tb_Fun_Columns) a"
            		+ " LEFT JOIN (SELECT * FROM tb_tmp_Columns) b ON a.FTableName = b.Table_Name"
            		+ " AND a.FColumnName = b.Column_Name WHERE  a.fdatalength < b.data_length "
            		+ " AND a.FDataType = 'VARCHAR2'"//暂时只判断VARCHAR2类型字段
            		+ " AND FTableName = " + dbl.sqlString(newTable.getFTableName());
            rs = dbl.openResultSet(sAlter);
            while(rs.next()){
            	sql.append(rs.getString("FColumnName")).append(" ")
            		.append(rs.getString("FDataType")).append("(")
            		.append(rs.getDouble("data_length")).append("),");
            }
            if(sql.toString().endsWith(",")){
            	sql.deleteCharAt(sql.length() - 1);//去掉最后一个逗号
                sql.append(")");
            } else {
            	return;
            }
            sqlBuf.append(sql).append("\n");
            dbl.executeSql(sql.toString());
        } catch (Exception e) {
            throw new YssException("更新标准表出错！\r\n", e);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * 标准表数据复制过程
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void StandardCopyDataProc(StringBuffer sqlBuf, String mark) throws YssException { //xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表
        String sql = "";
        try {
            if (oldTable == null) {
                return;
            }
            sql = sqlBuild.getInsertStr(newTable, oldTable, tmpTableName, mark); //xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表
            if (sql.trim().length() != 0) {
                sqlBuf.append(sql).append("\n");
                dbl.executeSql(sql);
            }
        } catch (Exception e) {
            throw new YssException("复制数据出错！\r\n", e);
        }
    }

    /**
     * 标准约束创建过程，通过成员变量 newTable 中的数据进行创建
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void StandardCreateConsProc(StringBuffer sqlBuf) throws YssException {
        StandardCreateConsProc(sqlBuf, "", "");
    }

    /**
     * 标准约束创建过程（重载），可指定表名和约束名
     * @param sqlBuf StringBuffer
     * @param sTabName String
     * @param sPKName String
     * @throws YssException
     */
    protected void StandardCreateConsProc(StringBuffer sqlBuf, String sTabName, String sPKName) throws YssException {
        String sql = "";
        try {
            if (null != newTable.getPkCons()) { //判断新表是否有主键，没有主键就不用创建主键 xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表
                sql = sqlBuild.getCreateTablePKStr(newTable, sTabName, sPKName);
                if (!"".equals(sql)) {
                    sqlBuf.append(sql).append("\n");
                    dbl.executeSql(sql);
                }
            } //xuqiji 20090416  MS00352    新建组合群时能够自动创建对应的一套表
        } catch (Exception e) {
            throw new YssException("创建表约束出错！\r\n", e);
        }
    }

    /**
     * 标准临时表删除过程
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void StandardDropTableProc(StringBuffer sqlBuf) throws YssException {
        String sql = "";
        try {
            if (dbl.yssTableExist(tmpTableName)) {
                sql = sqlBuild.getDropTableStr(tmpTableName);
                sqlBuf.append(sql).append("\n");
                dbl.executeSql(sql);
            }
        } catch (Exception e) {
            throw new YssException("删除临时表出错！\r\n", e);
        }
    }

    /**
     * 记录更新的表名
     * @param updateTables StringBuffer
     * @throws YssException
     */
    protected void StandardRegisterTableName(StringBuffer updateTables) throws YssException {
        updateTables.append(newTable.getFTableName()).append(",");
    }

    /**
     * 表结构更新的执行方法
     * @param alTabName ArrayList：需要更新的表名，ArrayList 中装载表名字符串
     * @param hmInfo HashMap：Key：sqlinfo（更新过程中所执行的所有 SQL 语句），errinfo（如果有异常，所有的异常描述）
     * @param iExceptionThrowType int：异常抛出的方式，0：捕获到异常，即时抛出，1：将所有表名循环完毕后再抛出
     * @throws YssException
     */
    public final void updExecute(ArrayList alTabName, HashMap hmInfo, int iExceptionThrowType) throws YssException {
        Iterator it = null;
        String sTabName = "";
        String sErrInfo = "";
        //过程中发生异常的表的表名
        String sErrTabName = "";
        //记录 SQL 语句
        StringBuffer sqlBuf = null;
        //记录异常信息
        StringBuffer errBuf = null;
        //记录被更新的表名
        StringBuffer updateTablesBuf = null;
        boolean isFail = false;
        try {

            if (hmInfo == null) {
                throw new YssException("调用执行更新出错，请传入 HashMap 实体！");
            } else {
                sqlBuf = (StringBuffer) hmInfo.get("sqlinfo");
                errBuf = (StringBuffer) hmInfo.get("errinfo");
                updateTablesBuf = (StringBuffer) hmInfo.get("updatetables");
            }

            oper = new OperTableData();
            oper.setYssPub(pub);
            sqlBuild = new SqlStringBuilder(dbl.dbType);
            sqlBuild.setYssPub(pub);    //设置Pub,否则后面获取dbl会出错 sunkey@Modify

            sErrInfo = getErrorMessage();
            //循环表名进行更新或检查
            it = alTabName.iterator();
            while (it.hasNext()) {
                try {
                    sTabName = (String) it.next();
                    newTable = oper.getNewSingleTable(sTabName);
                    oldTable = oper.getOldSingleTable(sTabName);
                    tmpTableName = getTmpTableName();

                    dropTableCons(sqlBuf);
                    renameTable(sqlBuf);
                    createTable(sqlBuf);
                    copyDate(sqlBuf);
                    createTableCons(sqlBuf);
                    dropTmpTable(sqlBuf);
                    registerTableName(updateTablesBuf);
                } catch (Exception e) {
                    //这里面现在没做 try catch 处理哦，因为现在看起来不会有什么异常发生，如果加个 try catch 就很乱了。
                    sErrTabName = "";
                    //记录出错的表名
                    if (newTable != null && newTable.getFTableName().trim().length() > 0) {
                        sErrTabName = newTable.getFTableName();
                    }
                    //如果异常是等循环完成后再抛出，将异常标志 isFail 设置为 true，将异常记录起来
                    if (iExceptionThrowType == 1) {
                        isFail = true;
                        errBuf.append(sErrInfo).append(e.getMessage()).append(sErrTabName).append("\n");
                    } else { //如果异常是即时抛出，将异常记录后，抛出无描述的异常通知上层方法
                        errBuf.append(sErrInfo).append(e.getMessage()).append(sErrTabName);
                        throw new YssException();
                    }
                }
            }
            //完成循环后如果有异常将异常抛出
            if (isFail) {
                //这里抛出的异常仅用于告诉上层方法，有异常产生，实际的异常信息通过形参 hmInfo 输出！
                throw new YssException();
            }
        } catch (Exception e) {
            throw new YssException("表结构更新的执行方法出错！\r\n", e);
        }
    }

    /**
     * 通过索引删除所有约束，包括索引约束、主键约束
     * @param indexName String 索引名称
     * @param sqlBuf StringBuffer 本次更新的sql语句
     * @return String
     */
    public void dropAllConsByIndex(String indexName, StringBuffer sqlBuf) throws YssException {
        String sPKName = "";
        String sTableName = "";
        String sql = "";
        ResultSet rs = null;
        try {
            //获取占用索引约束的表
            sTableName = dbl.getTableByConstaintKey(indexName);
            //如果存在表，将表约束删除
            if (sTableName.length() != 0) {
                if (dbl.dbType == YssCons.DB_ORA) {
                    sql = " select constraint_name from user_constraints where index_name = " + dbl.sqlString(indexName);
                    rs = dbl.openResultSet(sql);
                    if (rs.next()) {
                        sPKName = rs.getString("constraint_name");
                    }
                    //如果取到了约束，将约束删除
                    if (sPKName.length() != 0) {
                        sql = "ALTER TABLE " + sTableName + " DROP CONSTRAINT " + sPKName;
                        if (dbl.getDBType() == YssCons.DB_ORA) {
                            sql += " CASCADE ";
                        }
                    } else {
                        sql = sqlBuild.getDropTableIndex(indexName); //删除本地表中存在的索引
                    }
                    sqlBuf.append(sql).append("\n");
                    dbl.executeSql(sql);
                }
            }
        } catch (Exception e) {
            throw new YssException("删除索引和约束出错！", e);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
    }

}
