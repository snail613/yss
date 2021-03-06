package com.yss.dbupdate.autoupdatetables.updatemain;

import java.io.*;
import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;
import oracle.jdbc.*;
import oracle.sql.*;

/**
 *
 * <p>Title: 数据库版本号控制类</p>
 *
 * <p>Description: 获取和写入版本号等操作</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class VersionControl
    extends BaseBean {
    public VersionControl() {
    }

    /**
     * 获取当天数据库中，所登陆组合群的最大版本
     * @param sAssetGroupCode String：登陆组合群代码
     * @return String：最大版本号
     * @throws YssException
     */
    private String getCurrentDataBaseMaxVerNum(String sAssetGroupCode) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String sMaxVer = "";
        try {
            sqlStr = "SELECT FVerNum FROM TB_FUN_Version WHERE FAssetGroupCode = " +
                dbl.sqlString(sAssetGroupCode) + " ORDER BY FVerNum DESC";
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                sMaxVer = rs.getString("FVerNum");
            } else {
                sMaxVer = "1.0.1.0011";
            }
        } catch (Exception e) {
            throw new YssException("获取当前数据库最大版本号出错！\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sMaxVer;
    }

    /**
     * 获取所有需要更新的版本号
     * @param sAssetGroupCode String：登陆组合群代码
     * @return ArrayList：需要更新的版本号链表
     * @throws YssException
     */
    public ArrayList getNeedUpdateVerNum(String sAssetGroupCode) throws YssException {
        ArrayList alVerNum = new ArrayList();
        String sCurrMaxVer = "";
        try {
            //获取数据库当前最大版本
            sCurrMaxVer = getCurrentDataBaseMaxVerNum(sAssetGroupCode);

            for (int i = 0; i < YssCons.YSS_AUTOUPDATE_VERSIONS.length; i++) {
                if (sCurrMaxVer.equalsIgnoreCase(YssCons.YSS_AUTOUPDATE_VERSIONS[i][0])) {
                    for (i = i + 1; i < YssCons.YSS_AUTOUPDATE_VERSIONS.length; i++) {
                        alVerNum.add(YssCons.YSS_AUTOUPDATE_VERSIONS[i]);
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("获取未更新的版本号出错！", e);
        }
        return alVerNum;
    }

    /**
     * 更新数据库中的版本号信息
     * @param sAssetGroupCode String：被更新的组合群
     * @param sUserCode String：启动更新的用户代码
     * @param sVerNum String：更新的版本号
     * @param sResult String：更新结果，有失败和成功两种结果
     * @param hmInfo HashMap：更新信息，包括执行的 SQL 语句，出现的异常，被更新的表
     * @throws YssException
     */
    public void updateVersionInfo(String sAssetGroupCode, String sUserCode,
                                  String sVerNum, String sResult, HashMap hmInfo) throws YssException {

        Connection conn = dbl.loadConnection();
        boolean bTrans = true; //默认事物自动回滚

        //xuqiji 20090506:QDV4赢时胜（上海）2009年4月7日01_A  MS00352    新建组合群时能够自动创建对应的一套表  ---------------//
        StringBuffer bufSql = new StringBuffer(200000);
        String StrErrinfo = null;
        //--------------------------------------end---------------------------------------------------------------------//
        try {
            bufSql.append(" INSERT INTO TB_FUN_VERSION ");
            bufSql.append(" (FAssetGroupCode, ");
            bufSql.append(" FVerNum, ");
            bufSql.append(" FIssueDate, ");
            bufSql.append(" FFinish, ");
            bufSql.append(" FUSERCODE,");
            bufSql.append(" FCreateDate, ");
            bufSql.append(" FCreateTime, ");
            bufSql.append(" FUPDATETABLES, ");
            bufSql.append(" FERRORINFO, ");
            bufSql.append(" FSQLSTR) ");
            bufSql.append(" VALUES ( ");
            bufSql.append(dbl.sqlString(sAssetGroupCode)).append(", ");
            bufSql.append(dbl.sqlString(sVerNum)).append(", ");
            bufSql.append(dbl.sqlDate(new java.util.Date())).append(", ");
            bufSql.append(dbl.sqlString(sResult)).append(", ");
            bufSql.append(dbl.sqlString(sUserCode)).append(", ");
            bufSql.append(dbl.sqlDate(new java.util.Date())).append(", ");
            bufSql.append(dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))).append(", ");

            /* MS00352:QDV4赢时胜（上海）2009年4月7日01_A 新建组合群时能够自动创建对应的一套表
             * modify by xuqiji 20090416 对调整过字段类型为clob的字段进行处理
             **---------------------------------------------------------------------*/

            //Oracle 直接插入占位符，db2的clob有单次插入字符串长度不超过32k的限制，因此先插入“ ”
            if (dbl.dbType == YssCons.DB_ORA) {
                bufSql.append("EMPTY_CLOB()").append(",");
            } else {
                bufSql.append(dbl.sqlString(" ")).append(",");
            }

            //异常信息中包含16禁止代码00的字符，db2将无法解析，因此要将此种信息过滤
            if (dbl.dbType == YssCons.DB_ORA) {
                StrErrinfo = ( (StringBuffer) hmInfo.get("errinfo")).toString();
            } else {
                StrErrinfo = ( (StringBuffer) hmInfo.get("errinfo")).toString();
                char[] errorinfo = StrErrinfo.toCharArray();
                for (int i = 0; i < errorinfo.length; i++) {
                    if (errorinfo[i] == 00) {
                        errorinfo[i] = ' ';
                    }
                }
                StrErrinfo = new String(errorinfo).replaceAll(";", ",");
            }
            bufSql.append(dbl.sqlString(StrErrinfo)).append(", ");

            //Oracle 直接插入占位符，db2的clob有单次插入字符串长度不超过32k的限制，因此先插入“ ”
            if (dbl.dbType == YssCons.DB_ORA) {
                bufSql.append("EMPTY_CLOB()").append(")");
            } else {
                bufSql.append(dbl.sqlString(" ")).append(" ) ");
            }
            //---------------------------------------------end-------------------//
            conn.setAutoCommit(false);
            dbl.executeSql(bufSql.toString());

            //更新clob类型对象数据
            updateClobField(hmInfo, sVerNum, sAssetGroupCode, conn);

            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("数据库自动更新，更新版本：" + sVerNum + " 版本号出错！\n", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 更新版本表中的clob类型数据
     * @param tempSqlinfo String 要插入的数据
     * @param sVerNum String 更新的版本
     * @param sAssetGroupCode String 组合代码
     * @param conn Connection 数据库连接
     * @param updateField String 要插入数据对应的字段
     * @throws YssException 异常
     * @throws SQLException sql异常
     * @throws IOException IO异常
     * xuqiji 20090506:QDV4赢时胜（上海）2009年4月7日01_A  MS00352    新建组合群时能够自动创建对应的一套表
     */
    private void updateClobField(HashMap hmInfo, String sVerNum,
                                 String sAssetGroupCode, Connection conn) throws YssException, SQLException, IOException {
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Reader reader1 = null;
        Reader reader2 = null;
        String strSql = null;
        String strTab = ( (StringBuffer) hmInfo.get("updatetables")).toString(); //存放更新表信息
        String strSqlInfo = ( (StringBuffer) hmInfo.get("sqlinfo")).toString(); //存放更新表的sql语句
        try {
            //更新ORACLE中的clob字段
            if (dbl.dbType == YssCons.DB_ORA) {
                //查询更新的表的SQL语句，将要更新的clob类型字段查询出来
                strSql = "SELECT FUPDATETABLES,FSQLSTR FROM TB_FUN_VERSION" +
                    " WHERE FAssetGroupCode= " + dbl.sqlString(sAssetGroupCode) +
                    " AND FVerNum = " + dbl.sqlString(sVerNum);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    //首先从数据库中取出字段的clob打数据
                	//modify by jsc 20120809 连接池对大对象的特殊处理；STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
//                    CLOB clobTab = ( (OracleResultSet) rs).getCLOB("FUPDATETABLES");
//                    CLOB clobSql = ( (OracleResultSet) rs).getCLOB("FSQLSTR");
                	CLOB clobTab = dbl.CastToCLOB(rs.getClob("FUPDATETABLES"));
                	CLOB clobSql = dbl.CastToCLOB(rs.getClob("FSQLSTR"));
                    //将数据重新赋值
                    clobTab.putString(1, strTab);
                    clobSql.putString(1, strSqlInfo);

                    //生成更新的sql语句
                    strSql = "UPDATE TB_FUN_VERSION" +
                        " SET FUPDATETABLES = ?,FSQLSTR = ? WHERE FAssetGroupCode = " +
                        dbl.sqlString(sAssetGroupCode) +
                        " AND FVerNum = " + dbl.sqlString(sVerNum); //更新版本表
                    pstmt = conn.prepareStatement(strSql);
                    pstmt.setClob(1, clobTab);
                    pstmt.setClob(2, clobSql);
                }
            } else { //更新DB2中的clob字段
                strSql = "UPDATE TB_FUN_VERSION" +
                    " SET FUPDATETABLES = ?,FSQLSTR =? WHERE FAssetGroupCode = " +
                    dbl.sqlString(sAssetGroupCode) +
                    " AND FVerNum = " + dbl.sqlString(sVerNum);
                pstmt = conn.prepareStatement(strSql);
                //使用流读取clob对象中的数据
                reader1 = new StringReader(strTab);
                pstmt.setCharacterStream(1, reader1, strTab.length());
                //MS00010 QDV4赢时胜（上海）2009年02月01日10_A edit by songjie 2009-05-13 将StringReader(strSqlInfo)改为StringReader(strSqlInfo.replaceAll("'","`"))
                reader2 = new StringReader(strSqlInfo.replaceAll("'", "`"));
                pstmt.setCharacterStream(2, reader2, strSqlInfo.length());
            }
			/**shashijie 2012-7-2 STORY 2475 */
            if (pstmt != null) {
            	pstmt.executeUpdate();
                pstmt.close();
			}
			/**end*/
        } catch (Exception e) {
            throw new YssException("更新版本表CLOB类型对象出错！", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            dbl.closeResultSetFinal(rs);
            if (reader1 != null) {
                reader1.close();
            }
            if (reader2 != null) {
                reader2.close();
            }
        }
    }
}
