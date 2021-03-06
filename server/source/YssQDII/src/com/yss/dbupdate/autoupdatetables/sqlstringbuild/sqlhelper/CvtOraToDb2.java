package com.yss.dbupdate.autoupdatetables.sqlstringbuild.sqlhelper;

import java.util.regex.*;

import com.yss.util.*;

/**
 *
 * <p>Title: 用于将 Oracle 的 SQL 语句装换为 DB2 可用的 SQL 语句</p>
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
public class CvtOraToDb2
    implements ISqlStringConvert {
    public CvtOraToDb2() {
    }

    /**
     * 执行转换
     * @param sql String：Oracle SQL
     * @return String：DB2 SQL
     * @throws YssException
     */
    public String sqlConvert(String sql) throws YssException {
        try {
            sql = replaceSql(sql);
            sql = matcherSql(sql);
        } catch (Exception e) {
            throw new YssException("将 Oracle SQL 语句转换为 DB2 SQL 语句出错！\r\n", e);
        }
        return sql;
    }

    /**
     * 进行替换操作
     * @param sql String：Oracle SQL
     * @return String：DB2 SQL
     * @throws YssException
     */
    public String replaceSql(String sql) throws YssException {
        try {
            sql = Pattern.compile("( varchar2[(])", Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" VARCHAR(");
            sql = Pattern.compile("( number[(])", Pattern.CASE_INSENSITIVE).matcher(sql).replaceAll(" DECIMAL(");
        } catch (Exception e) {
            throw new YssException("进行替换操作出错！\n", e);
        }
        return sql;
    }

    /**
     * 进行匹配操作
     * @param sql String：Oracle SQL
     * @return String：DB2 SQL
     * @throws YssException
     */
    public String matcherSql(String sql) throws YssException {
        Matcher m = null;
        Pattern p = null;
        String sFand = "";
        try {
            //----------------完成主键名称的长度地修改-------------------//
            p = Pattern.compile("(?<=CONSTRAINT( {1,10}))\\w+(?=( {1,10})PRIMARY KEY)", Pattern.CASE_INSENSITIVE);
            m = p.matcher(sql);
            if (m.find()) {
                sFand = m.group();
                if (sFand.length() >= 17) {
                    sql = m.replaceAll(sFand.substring(0, 9) + sFand.substring(sFand.length() - 7));
                }
            }
            //--------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("进行匹配操作出错！\n", e);
        }
        return sql;
    }
}
