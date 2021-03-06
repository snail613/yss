package com.yss.dbupdate;

import com.yss.dsub.*;
import com.yss.util.*;
import java.sql.ResultSet;
import java.sql.Connection;

public class BaseDbUpdate
    extends BaseBean {

    public BaseDbUpdate() {
    }

    /**
     * 2009-06-01 蒋锦 添加
     * 判断输入的版本号是否存在
     * MS00352:QDV4赢时胜（上海）2009年4月7日01_A
     * @param verNum String：需要判断的版本号
     * @return boolean：存在：true 不存在：false
     * @throws YssException
     */
    public boolean isExistsSuccessVerNum(String verNum) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        boolean isExists = false;
        try {
            strSql = "SELECT FVernum FROM TB_FUN_VERSION WHERE FVerNum = " + dbl.sqlString(verNum);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                isExists = true;
            }
        } catch (Exception ex) {
            throw new YssException("判断版本号是否存失败！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return isExists;
    }
    /**
     * 20100526 xuqiji 添加
     * 判断输入的版本号是否存在并且是更新成功
     * @param verNum String：需要判断的版本号
     * @return boolean：存在：true 不存在：false
     * @throws YssException
     */
    public boolean isExistsUpdateSuccessVerNum(String verNum) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        boolean isExists = false;
        try {
            strSql = "SELECT FVernum FROM TB_FUN_VERSION WHERE FFinish = 'Success' and FVerNum = " + dbl.sqlString(verNum) + 
            		" and FAssetGroupCode <> " + dbl.sqlString(pub.getAssetGroupCode());
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                isExists = true;
            }
        } catch (Exception ex) {
            throw new YssException("判断版本号是否存在并且是更新成功的版本失败！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return isExists;
    }

    /**
     * 使用表名删除表
     * @param sTableName String：表名
     * @throws YssException
     */
    public void dropTableByTableName(String sTableName) throws YssException {
        String strSql = "";
        try {
            strSql = "DROP TABLE " + sTableName;
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("删除表出错！", e);
        }
    }

    /**
     * Oracle Database 根据表名获取命名不规范的主键名
     * @param TableName String
     * @throws YssException
     * @return String: 如果存在命名不规范的主键名，返回主键名如果没有，返回""
     */
    public String getPrimaryKeyByTableName_Ora(String TableName) throws
        YssException {
        String strSql = "";
        String strKeyName = "";
        ResultSet rsKeyName = null;
        try {
            strSql = "SELECT CONSTRAINT_NAME " +
                "FROM USER_CONSTRAINTS " +
                "WHERE TABLE_NAME = " + dbl.sqlString(TableName) +
                " AND CONSTRAINT_NAME <> 'PK_' || TABLE_NAME " +
                "AND CONSTRAINT_TYPE = 'P'";
            rsKeyName = dbl.openResultSet(strSql);
            if (rsKeyName.next()) {
                strKeyName = rsKeyName.getString("CONSTRAINT_NAME");
            }
            return strKeyName;
        } catch (Exception e) {
            throw new YssException("获取主键名出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsKeyName);
        }
    }

    /**
     * Oracle Database 根据表名获取主键名
     * @param TableName String
     * @throws YssException
     * @return String: 如果存在主键，返回主键名如果没有，返回""
     */
    public String getIsNullPKByTableName_Ora(String TableName) throws
        YssException {
        String strSql = "";
        String strKeyName = "";
        ResultSet rsKeyName = null;
        try {
            strSql = "SELECT CONSTRAINT_NAME " +
                "FROM USER_CONSTRAINTS " +
                "WHERE TABLE_NAME = UPPER(" + dbl.sqlString(TableName) + ")" +
                "AND CONSTRAINT_TYPE = 'P'";
            rsKeyName = dbl.openResultSet(strSql);
            if (rsKeyName.next()) {
                strKeyName = rsKeyName.getString("CONSTRAINT_NAME");
            }
            return strKeyName;
        } catch (Exception e) {
            throw new YssException("获取主键名出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsKeyName);
        }
    }

    /**
     * Oracle Database 根据表名和约束名删除约束
     * @param strTableName String: 表名
     * @param strKeyName String: 约束名
     * @throws YssException
     */
    public void deleteKeyName_Ora(String strTableName, String strKeyName) throws
        YssException {
        String strSql = "";
		/**shashijie 2012-7-2 STORY 2475 */
        //Connection conn = dbl.loadConnection();
		/**end*/
        try {
            strSql = "ALTER TABLE " + strTableName +
                " DROP CONSTRAINT " + "\"" + strKeyName + "\" CASCADE";

            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("删除约束出错！", e);
        }
    }

    /**
     * Oracle Database 为指定的表添加指定主键名和列名的主键
     * @param strTableName String: 添加主键的表
     * @param strKeyName String: 主键名 也可以为 "" 默认为PK_ + 表名
     * @param strCols String: 组成主键的所有列的列名
     * @throws YssException
     */
    public void addPrimaryKey_Ora(String strTableName, String strKeyName,
                                  String strCols) throws YssException {
        String strSql = "";
		/**shashijie 2012-7-2 STORY 2475 */
        //Connection conn = dbl.loadConnection();
		/**end*/
        try {
            if (strKeyName.trim().length() == 0) {
                strKeyName = "PK_" + strTableName;
            }
            if (strKeyName.length() > 30) {
                strKeyName = strKeyName.substring(0, 30);
            }
            strSql = "ALTER TABLE " + strTableName +
                " ADD CONSTRAINT " + "\"" + strKeyName + "\"" +
                " PRIMARY KEY(" + strCols + ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("新增主键约束出错！", e);
        }
    }

    /**
     * Oracle Database 得到组成指定表，指定约束名的所有列的列名
     * @param strTableName String: 指定的表
     * @param strKeyName String: 约束名
     * @throws YssException
     * @return String
     */
    public String getColumnNames_Ora(String strTableName, String strKeyName) throws
        YssException {
        ResultSet rsCols = null;
        String strSql = "";
        try {
            strSql = "SELECT COLUMN_NAME FROM USER_CONS_COLUMNS" +
                " WHERE TABLE_NAME = " + dbl.sqlString(strTableName) +
                " AND CONSTRAINT_NAME = " + dbl.sqlString(strKeyName) +
                " ORDER BY POSITION";
            rsCols = dbl.openResultSet(strSql); //查出主键所包含的所有列的列名

            String strCols = ""; //保存构成主键的所有列的列名
            while (rsCols.next()) {
                strCols += rsCols.getString("COLUMN_NAME") + ",";
            }

            if (strCols.trim().length() == 0) {
                throw new YssException("找不到构成约束的列！");
            }
            strCols = strCols.substring(0, strCols.length() - 1); //去掉字符串中最后一个逗号
            return strCols;
        } catch (Exception e) {
            throw new YssException("查询组成约束的列名出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsCols);
        }
    }

    /**
     * Oracle Database 更新无组合群代码作为前缀的表的主键名
     */
    public void updatePKName_Ora() throws YssException {
        ResultSet rsPrimary = null;
        ResultSet rsIsRepeat = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        String strTableName = ""; //保存表名
        String strKeyName = ""; //保存主键名
        String strNewKeyName = ""; //保存新主键名
        boolean bTrans = false; //代表是否开始事务

        try {
            strSql = "SELECT a.TABLE_NAME, a.CONSTRAINT_NAME " +
                "FROM (SELECT TABLE_NAME, CONSTRAINT_NAME " +
                "FROM USER_CONSTRAINTS " +
                "WHERE LOWER(SUBSTR(TABLE_NAME, 0, 6)) = 'tb_sys' " +
                "OR LOWER(SUBSTR(TABLE_NAME, 0, 7)) = 'tb_base' " +
                "OR LOWER(SUBSTR(TABLE_NAME, 0, 6)) = 'tb_fun') b " +
                "LEFT JOIN USER_CONSTRAINTS a ON a.TABLE_NAME = b.TABLE_NAME " +
                "AND a.CONSTRAINT_NAME = b.CONSTRAINT_NAME " +
                "WHERE a.CONSTRAINT_NAME <> 'PK_' || a.TABLE_NAME " +
                "AND a.CONSTRAINT_TYPE = 'P'";
            rsPrimary = dbl.openResultSet(strSql); //查出不符合命名规范的主键名和所在的表名

            while (rsPrimary.next()) {
                strTableName = rsPrimary.getString("TABLE_NAME");
                strKeyName = this.getPrimaryKeyByTableName_Ora(strTableName); //根据表名取键名
                if (strKeyName.trim().length() == 0) { //如果条件已不存在则进行下一个循环
                    continue;
                }
                //------------------拼接出新的主键名-----------------//
                strNewKeyName = "PK_" + strTableName;
                if (strNewKeyName.length() > 30) { //主键名有30长度的限制
                    strNewKeyName = strNewKeyName.substring(0, 30);
                }
                //-------------------------------------------------//
                //----------------判断新主键名是否被占用---如果被占用就先处理掉-------------//
                strSql = "SELECT TABLE_NAME " +
                    "FROM USER_CONSTRAINTS " +
                    "WHERE CONSTRAINT_NAME = " + dbl.sqlString(strNewKeyName) +
                    " AND CONSTRAINT_TYPE = 'P'";
                rsIsRepeat = dbl.openResultSet(strSql);
                if (rsIsRepeat.next()) {
                    String strRTableName = rsIsRepeat.getString("TABLE_NAME"); //获取占用键名的表名
                    String strCols = this.getColumnNames_Ora(strRTableName,
                        strNewKeyName); //获取组成主键的所有列

                    conn.setAutoCommit(false);
                    bTrans = true;
                    this.deleteKeyName_Ora(strRTableName, strNewKeyName);
                    this.addPrimaryKey_Ora(strRTableName, "", strCols);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
                //--------------------------------------------------------------------//

                String strCols = this.getColumnNames_Ora(strTableName, strKeyName); //获取组成主键的所有列
                //----------------删除原有主键
                conn.setAutoCommit(false);
                bTrans = true;
                this.deleteKeyName_Ora(strTableName, strKeyName);
                //-----------------添加新主键
                this.addPrimaryKey_Ora(strTableName, "", strCols);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        }

        catch (Exception e) {
            throw new YssException("更新通用数据表主键名出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsPrimary);
            dbl.closeResultSetFinal(rsIsRepeat);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * Oracle Database 更新带有组合群代码作为前缀的表的主键名
     * @param sPre String: 前缀
     */
    public void updatePKName_Ora(String sPre) throws YssException {
        ResultSet rsPrimary = null;
        ResultSet rsCols = null;
        ResultSet rsIsRepeat = null;
        String strSql = "";
        Connection conn = dbl.loadConnection();
        String strTableName = null; //保存表名
        String strKeyName = null; //保存原主键名
        String strNewKeyName = null; //保存新主键名

        boolean bTrans = false; //代表是否开始了事务

        try {
            //-----------查出不符合命名规范的主键名和所在的表名
            strSql = "SELECT TABLE_NAME, CONSTRAINT_NAME " +
                "FROM USER_CONSTRAINTS " +
                "WHERE CONSTRAINT_NAME <> 'PK_' || TABLE_NAME " +
                "AND LOWER(SUBSTR(TABLE_NAME, 0, 7)) = 'tb_" + sPre + "_'" +
                "AND CONSTRAINT_TYPE = 'P'";
            rsPrimary = dbl.openResultSet(strSql);

            while (rsPrimary.next()) {
                strTableName = rsPrimary.getString("TABLE_NAME");
                strKeyName = this.getPrimaryKeyByTableName_Ora(strTableName); //根据表名取键名
                if (strKeyName.trim().length() == 0) { //如果条件已不存在则进行下一个循环
                    continue;
                }
                //------------------拼接出新的主键名-----------------//
                strNewKeyName = "PK_" + strTableName;
                if (strNewKeyName.length() > 30) { //主键名有30长度的限制
                    strNewKeyName = strNewKeyName.substring(0, 30);
                }
                //-------------------------------------------------//
                //----------------判断新主键名是否被占用---如果被占用就先处理掉-------------//
                strSql = "SELECT TABLE_NAME " +
                    "FROM USER_CONSTRAINTS " +
                    "WHERE CONSTRAINT_NAME = " + dbl.sqlString(strNewKeyName) +
                    " AND CONSTRAINT_TYPE = 'P'";
                rsIsRepeat = dbl.openResultSet(strSql);
                if (rsIsRepeat.next()) {
                    String strRTableName = rsIsRepeat.getString("TABLE_NAME"); //获取占用键名的表名
                    String strCols = this.getColumnNames_Ora(strRTableName,
                        strNewKeyName); //获取组成主键的所有列

                    conn.setAutoCommit(false);
                    bTrans = true;
                    this.deleteKeyName_Ora(strRTableName, strNewKeyName);
                    this.addPrimaryKey_Ora(strRTableName, "", strCols);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
                //--------------------------------------------------------------------//

                String strCols = this.getColumnNames_Ora(strTableName, strKeyName); //获取组成主键的所有列
                //----------------删除原有主键
                conn.setAutoCommit(false);
                bTrans = true;
                this.deleteKeyName_Ora(strTableName, strKeyName);
                //-----------------添加新主键
                this.addPrimaryKey_Ora(strTableName, "", strCols);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            throw new YssException("更新组合群表主键名出错！", e);
        } finally {
        	//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeResultSetFinal(rsPrimary,rsIsRepeat);
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            dbl.closeResultSetFinal(rsCols);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 在DB2数据库中根据表名获取命名不规范的主键名
     * @param TableName String
     * @throws YssException
     * @return String: 如果存在命名不规范的主键名，返回主键名如果没有，返回""
     */
    public String getPrimaryKeyByTableName_DB2(String TableName) throws
        YssException {
        String strSql = "";
        String strKeyName = "";
        ResultSet rsKeyName = null;
        try {
            strSql = "SELECT CONSTNAME " +
                "FROM SYSCAT.TABCONST " +
                "WHERE TABNAME = " + dbl.sqlString(TableName) +
                " AND CONSTNAME <> CAST('PK_' || LEFT(TABNAME, 7) || RIGHT(TABNAME, 7) AS VARCHAR(18)) " +
                "AND TYPE = 'P'";
            rsKeyName = dbl.openResultSet(strSql);
            if (rsKeyName.next()) {
                strKeyName = rsKeyName.getString("CONSTNAME");
            }
            return strKeyName;
        } catch (Exception e) {
            throw new YssException("获取主键名出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsKeyName);
        }
    }

    /**
     * 在DB2中根据表名获取主键名
     * @param TableName String
     * @throws YssException
     * @return String: 如果存在主键，返回主键名如果没有，返回""
     */
    public String getIsNullPKByTableName_DB2(String TableName) throws
        YssException {
        String strSql = "";
        String strKeyName = "";
        ResultSet rsKeyName = null;
        try {
            strSql = "SELECT CONSTNAME " +
                "FROM SYSCAT.TABCONST " +
                "WHERE TABNAME = " + dbl.sqlString(TableName) +
                "AND TYPE = 'P'";
            rsKeyName = dbl.openResultSet(strSql);
            if (rsKeyName.next()) {
                strKeyName = rsKeyName.getString("CONSTNAME");
            }
            return strKeyName;
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0000 获取主键名出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsKeyName);
        }
    }

    /**
     * DB2 中根据表名和约束名删除约束
     * @param strTableName String: 表名
     * @param strKeyName String: 约束名
     * @throws YssException
     */
    public void deleteKeyName_DB2(String strTableName, String strKeyName) throws
        YssException {
        String strSql = "";
        try {
            strSql = "ALTER TABLE " + strTableName +
                " DROP CONSTRAINT " + "\"" + strKeyName + "\"";

            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("删除约束出错！", e);
        }
    }

    /**
     * DB2 中为指定的表添加指定主键名和列名的主键
     * @param strTableName String: 添加主键的表
     * @param strKeyName String: 主键名 也可以为 "" 默认为PK_ + 表名
     * @param strCols String: 组成主键的所有列的列名
     * @throws YssException
     */
    public void addPrimaryKey_DB2(String strTableName, String strKeyName,
                                  String strCols) throws YssException {
        String strSql = "";
        try {
            if (strKeyName.trim().length() == 0) {
                strKeyName = "PK_" + strTableName.substring(0, 7) +
                    strTableName.substring(strTableName.length() - 7);
            }
            strSql = "ALTER TABLE " + strTableName +
                " ADD CONSTRAINT " + "\"" + strKeyName + "\"" +
                " PRIMARY KEY(" + strCols + ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("新增主键约束出错！", e);
        }
    }

    /**
     * DB2 中得到组成指定表，指定约束名的所有列的列名
     * @param strTableName String: 指定的表
     * @param strKeyName String: 约束名
     * @throws YssException
     * @return String
     */
    public String getColumnNames_DB2(String strTableName, String strKeyName) throws
        YssException {
        ResultSet rsCols = null;
        String strSql = "";
        try {
            strSql = "SELECT COLNAME FROM SYSCAT.KEYCOLUSE " +
                " WHERE TABNAME = " + dbl.sqlString(strTableName) +
                " AND CONSTNAME = " + dbl.sqlString(strKeyName) +
                " ORDER BY COLSEQ";
            rsCols = dbl.openResultSet(strSql); //查出主键所包含的所有列的列名

            String strCols = ""; //保存构成主键的所有列的列名
            while (rsCols.next()) {
                strCols += rsCols.getString("COLNAME") + ",";
            }

            if (strCols.trim().length() == 0) {
                throw new YssException("找不到构成约束的列！");
            }
            strCols = strCols.substring(0, strCols.length() - 1); //去掉字符串中最后一个逗号
            return strCols;
        } catch (Exception e) {
            throw new YssException("查询组成约束的列名出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsCols);
        }
    }

    /**
     * DB2 中更新无组合群代码作为前缀的表的主键名
     */
    public void updatePKName_DB2() throws YssException {
        ResultSet rsPrimary = null;
        ResultSet rsIsRepeat = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        String strTableName = ""; //保存表名
        String strKeyName = ""; //保存主键名
        String strNewKeyName = ""; //保存新主键名
        boolean bTrans = false; //代表是否开始事务

        try {
            strSql = "SELECT a.TABNAME, a.CONSTNAME " +
                "FROM (SELECT TABNAME, CONSTNAME " +
                "FROM SYSCAT.TABCONST " +
                "WHERE LOWER(SUBSTR(TABNAME, 1, 6)) = 'tb_sys' " +
                "OR LOWER(SUBSTR(TABNAME, 1, 7)) = 'tb_base' " +
                "OR LOWER(SUBSTR(TABNAME, 1, 6)) = 'tb_fun') b " +
                "LEFT JOIN SYSCAT.TABCONST a ON a.TABNAME = b.TABNAME " +
                "AND a.CONSTNAME = b.CONSTNAME " +
                "WHERE a.CONSTNAME <> CAST('PK_' || LEFT(a.TABNAME, 7) || RIGHT(a.TABNAME, 7) AS VARCHAR(18)) " +
                "AND a.TYPE = 'P'";
            rsPrimary = dbl.openResultSet(strSql); //查出不符合命名规范的主键名和所在的表名

            while (rsPrimary.next()) {
                strTableName = rsPrimary.getString("TABNAME");
                strKeyName = this.getPrimaryKeyByTableName_DB2(strTableName); //根据表名取键名
                if (strKeyName.trim().length() == 0) { //如果条件已不存在则进行下一个循环
                    continue;
                }
                //------------------拼接出新的主键名-----------------//
                strNewKeyName = "PK_" + strTableName.substring(0, 7) +
                    strTableName.substring(strTableName.length() - 7);
                //-------------------------------------------------//
                //----------------判断新主键名是否被占用---如果被占用就先处理掉-------------//
                strSql = "SELECT TABNAME " +
                    "FROM SYSCAT.TABCONST " +
                    "WHERE CONSTNAME = " + dbl.sqlString(strNewKeyName) +
                    " AND TYPE = 'P'";
                rsIsRepeat = dbl.openResultSet(strSql);
                if (rsIsRepeat.next()) {
                    String strRTableName = rsIsRepeat.getString("TABNAME"); //获取占用键名的表名
                    String strCols = this.getColumnNames_DB2(strRTableName,
                        strNewKeyName); //获取组成主键的所有列

                    conn.setAutoCommit(false);
                    bTrans = true;
                    this.deleteKeyName_DB2(strRTableName, strNewKeyName);
                    this.addPrimaryKey_DB2(strRTableName, "", strCols);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
                //--------------------------------------------------------------------//

                String strCols = this.getColumnNames_DB2(strTableName, strKeyName); //获取组成主键的所有列
                //----------------删除原有主键
                conn.setAutoCommit(false);
                bTrans = true;
                this.deleteKeyName_DB2(strTableName, strKeyName);
                //-----------------添加新主键
                this.addPrimaryKey_DB2(strTableName, "", strCols);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        }

        catch (Exception e) {
            throw new YssException("更新通用数据表主键名出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsPrimary);
            dbl.closeResultSetFinal(rsIsRepeat);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * DB2 中更新带有组合群代码作为前缀的表的主键名
     * @param sPre String: 前缀
     */
    public void updatePKName_DB2(String sPre) throws YssException {
        ResultSet rsPrimary = null;
        ResultSet rsCols = null;
        ResultSet rsIsRepeat = null;
        String strSql = "";
        Connection conn = dbl.loadConnection();
        String strTableName = null; //保存表名
        String strKeyName = null; //保存原主键名
        String strNewKeyName = null; //保存新主键名

        boolean bTrans = false; //代表是否开始了事务

        try {
            //-----------查出不符合命名规范的主键名和所在的表名
            strSql = "SELECT TABNAME, CONSTNAME " +
                "FROM SYSCAT.TABCONST " +
                "WHERE CONSTNAME <> CAST('PK_' || LEFT(TABNAME, 7) || RIGHT(TABNAME, 7) AS VARCHAR(18)) " +
                "AND LOWER(SUBSTR(TABNAME, 1, 7)) = 'tb_" + sPre + "_'" +
                "AND TYPE = 'P'";
            rsPrimary = dbl.openResultSet(strSql);

            while (rsPrimary.next()) {
                strTableName = rsPrimary.getString("TABNAME");
                strKeyName = this.getPrimaryKeyByTableName_DB2(strTableName); //根据表名取键名
                if (strKeyName.trim().length() == 0) { //如果条件已不存在则进行下一个循环
                    continue;
                }
                //------------------拼接出新的主键名-----------------//
                strNewKeyName = "PK_" + strTableName.substring(0, 7) +
                    strTableName.substring(strTableName.length() - 7);
                //-------------------------------------------------//
                //----------------判断新主键名是否被占用---如果被占用就先处理掉-------------//
                strSql = "SELECT TABNAME " +
                    "FROM SYSCAT.TABCONST " +
                    "WHERE CONSTNAME = " + dbl.sqlString(strNewKeyName) +
                    " AND TYPE = 'P'";
                rsIsRepeat = dbl.openResultSet(strSql);
                if (rsIsRepeat.next()) {
                    String strRTableName = rsIsRepeat.getString("TABNAME"); //获取占用键名的表名
                    String strCols = this.getColumnNames_DB2(strRTableName,
                        strNewKeyName); //获取组成主键的所有列

                    conn.setAutoCommit(false);
                    bTrans = true;
                    this.deleteKeyName_DB2(strRTableName, strNewKeyName);
                    this.addPrimaryKey_DB2(strRTableName, "", strCols);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
                //--------------------------------------------------------------------//

                String strCols = this.getColumnNames_DB2(strTableName, strKeyName); //获取组成主键的所有列

                conn.setAutoCommit(false);
                bTrans = true;
                //----------------删除原有主键
                this.deleteKeyName_DB2(strTableName, strKeyName);
                //----------------添加新主键
                this.addPrimaryKey_DB2(strTableName, "", strCols);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("更新组合群表主键名出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsPrimary);
            dbl.closeResultSetFinal(rsCols);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //创建表
    public void createTable() throws YssException {

    }

    /**
     * MS00010 add by songjie 2009-05-11
     * QDV4赢时胜（上海）2009年02月01日10_A
     * 添加doUpdate方法 用于调用历史数据转换的更新代码
     * @param hmInfo HashMap
     * @throws YssException
     */
    public void doUpdate(java.util.HashMap hmInfo) throws YssException {

    }

    //增加表字段
    public void addTableField() throws YssException {

    }

    //调制表名
    public void adjustTableName() throws YssException {

    }

    //调整字段名
    public void adjustFieldName() throws YssException {

    }

    //调整字段精度
    public void adjustFieldPrecision() throws YssException {

    }

    //调整数据
    public void adjustTableData() throws YssException {

    }

    //创建表
    public void createTable(String sPre) throws YssException {

    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {

    }

    //调制表名
    public void adjustTableName(String sPre) throws YssException {

    }

    //调整字段名
    public void adjustFieldName(String sPre) throws YssException {

    }

    //调整字段精度
    public void adjustFieldPrecision(String sPre) throws YssException {

    }

    //调整数据
    public void adjustTableData(String sPre) throws YssException {

    }

    //调整带组合群号的表的主键
    public void adjustTableKey(String sPre) throws YssException {

    }

    //执行系统数据中的 SQL 语句
    public void executeSysDataSql() throws YssException {

    }

    /**
     * 检查是否要更新的数据已存在 sj add 20080221
     * @param tabName String
     * @param keyFields Hashtable
     * @param values Hashtable
     * @param isSysTab boolean
     * @throws YssException
     * @return boolean
     */
    protected boolean chValueEx(String tabName, java.util.Hashtable keyFields,
                                java.util.Hashtable values,
                                boolean isSysTab) throws YssException {
        boolean hasValue = false;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select * from " +
                (isSysTab == true ? pub.yssGetTableName(tabName) : tabName) + //是否为系统表
                " where 1 = 1 " + buildWh(keyFields, values);
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                hasValue = true;
            }
            return hasValue;
        } catch (Exception e) {
            throw new YssException("表中已存在与更新记录相同的数据！", e);
        }
    }

    /**
     * 通过在装有字段名称和类型的keyFields,装有字段名称和值得values来拼装where条件语句 sj add 20080221
     * @param keyFields Hashtable
     * @param values Hashtable
     * @throws YssException
     * @return String
     */
    private String buildWh(java.util.Hashtable keyFields,
                           java.util.Hashtable values) throws YssException {
        String whereStr = "";
        String field = ""; //字段名称
        String fType = ""; //字段类型
        try {
            java.util.Iterator it = keyFields.keySet().iterator();
            while (it.hasNext()) {
                field = (String) it.next();
                fType = (String) keyFields.get(field);
                if (fType.equals("Date")) {
                    whereStr += " and " + field + " = " +
                        dbl.sqlDate( (String) values.get(field));
                } else if (fType.equals("String")) {
                    whereStr += " and " + field + " = " +
                        dbl.sqlString( (String) values.get(field));
                } else {
                    whereStr += " and " + field + " = " + (String) values.get(field);
                }
            }
            return whereStr;
        } catch (Exception e) {
            throw new YssException("生成条件语句出错！", e);
        }
    }

    /**
     * 根据表名获取主键名。
     * @param tableName String
     * @return String
     * @throws YssException
     */
    public String getIsNullPKByTableName(String tableName) throws
        YssException {
        String PKName = "";
        try {
            if (dbl.getDBType() == YssCons.DB_ORA) {
                PKName = this.getIsNullPKByTableName_Ora(tableName);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                PKName = getIsNullPKByTableName_DB2(tableName);
            }
            return PKName;
        } catch (Exception e) {
            throw new YssException("获取主键出错！", e);
        }
    }

    public void deleteKeyName(String strTableName, String strKeyName) throws
        YssException {
        try {
            if (dbl.getDBType() == YssCons.DB_ORA) {
                deleteKeyName_Ora(strTableName, strKeyName);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                deleteKeyName_DB2(strTableName, strKeyName);
            }
        } catch (Exception e) {
            throw new YssException("删除主键出错！", e);
        }
    }

    /**
     * 删除索引
     * @param strIndexName String
     * @throws YssException
     */
    public void deleteIndex(String strIndexName) throws YssException {
        String sqlStr = "";
        try {
            if (existIndex(strIndexName)) { //判断是否存在索引，若存在则删除。
                sqlStr = "drop index " + strIndexName;
                dbl.executeSql(sqlStr);
            }
        } catch (Exception e) {
            throw new YssException("删除索引出错！", e);
        }
    }

    /**
     * 判断是否存在索引。
     * @param strIndexName String
     * @return boolean
     * @throws YssException
     */
    private boolean existIndex(String strIndexName) throws YssException {
        boolean existIndex = false;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select * from USER_IND_COLUMNS where INDEX_NAME = " +
                dbl.sqlString(strIndexName);
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                existIndex = true;
            }
            return existIndex;
        } catch (Exception e) {
            throw new YssException("获取索引出错！", e);
        }
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
    }

    /***
     * 根据表来判断表的字段是否存在 ---不存在返回 true,存在返回 false
     * sTabName :表名
     * cloumsn : 要查询的表字段 集,多个字段中间用"," 分隔
     */
    protected boolean existsTabColumn_Ora(String sTabName, String columns) throws YssException {
        boolean existCol = true;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select * from user_col_comments where upper(table_name)=upper(" + dbl.sqlString(sTabName) + ")" +
                " and upper(Column_Name) in (" + operSql.sqlCodes(columns.toUpperCase()) + ")";
            
            //---edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A start---//
            if(sTabName.equalsIgnoreCase("T_Plugin_Log")){//若为业务日志表相关sql，替换为业务日志相关的数据库连接
            	rs = dblBLog.openResultSet(sqlStr);
            }else{
            	rs = dbl.openResultSet(sqlStr);
            }
            //---edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A end---//	
            
            while (rs.next()) {
                existCol = false;
            }
            return existCol;
        } catch (Exception e) {
            throw new YssException("查询Oracle表" + sTabName + "的字段" + columns + "时出错", e);
        } finally {
        	//---edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	if(sTabName.equalsIgnoreCase("T_Plugin_Log")){//若为业务日志表相关sql，替换为业务日志相关的数据库连接
        		dblBLog.closeResultSetFinal(rs);
        	}else{
        		dbl.closeResultSetFinal(rs);
        	}
        	//---edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A end---//
        }
    }

    /***
     * 根据表来判断表的字段是否存在 true:不存在 false:存在
     * sTabName :表名
     * cloumsn : 要查询的表字段 集,多个字段中间用"," 分隔
     */
    protected boolean existsTabColumn_DB2(String sTabName, String columns) throws YssException {
        boolean existCol = true;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select * from SYSIBM.COLUMNS_S where upper(table_name)=upper(" + dbl.sqlString(sTabName) + ")" +
                " and upper(Column_Name) in (" + operSql.sqlCodes(columns.toUpperCase()) + ")";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                existCol = false;
            }
            return existCol;
        } catch (Exception e) {
            throw new YssException("查询DB2表" + sTabName + "的字段" + columns + "时出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
}
