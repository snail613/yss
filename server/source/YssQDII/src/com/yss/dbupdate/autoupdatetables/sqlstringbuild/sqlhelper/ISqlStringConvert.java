package com.yss.dbupdate.autoupdatetables.sqlstringbuild.sqlhelper;

import com.yss.util.*;

/**
 *
 * <p>Title: SQL 语句装换接口</p>
 *
 * <p>Description: 用于将一种 DBMS 的 SQL 语句装换为另一种 DBMS 可用的 SQL 语句</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface ISqlStringConvert {
    public String sqlConvert(String sql) throws YssException;
}
