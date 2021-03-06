package com.yss.dbupdate.autoupdatetables.entitycreator;

import java.sql.*;
import java.util.*;

import com.yss.dbupdate.autoupdatetables.entitycreator.pojo.*;
import com.yss.dsub.*;
import com.yss.util.*;

/**
 *
 * <p>Title: 表结构数据实体生成类</p>
 *
 * <p>Description: 类中有两种方法，getNew... 和 getOld... getNew 用来获取新的表结构实体，getOld 获取系统现有的表结构实体</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OperTableData
    extends BaseBean {
    public OperTableData() {
    }

    /**
     * 获取新的标准表结构实体
     * @param sTabName String：表名
     * @return TableBean
     * @throws YssException
     */
    public TableBean getNewSingleTable(String sTabName) throws YssException {
        TableBean table = null;
        try {
            table = getNewTable(sTabName);
        } catch (Exception e) {
            throw new YssException("获取标准表结构实体出错！\r\n", e);
        }
        return table;
    }

    /**
     * 获取旧的表结构实体
     * 暂时只实现了获取列 2008-12-25 蒋锦
     * @param sTabName String：表名
     * @return TableBean
     * @throws YssException
     */
    public TableBean getOldSingleTable(String sTabName) throws YssException {
        TableBean table = null;
        try {
            table = getOldTable(sTabName);
        } catch (Exception e) {
            throw new YssException("获取未更新得表结构实体出错！\n", e);
        }
        return table;
    }

    /**
     * 获取旧的表结构实体
     * @param sTabName String：表名
     * @return TableBean
     * @throws YssException
     */
    private TableBean getOldTable(String sTabName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        TableBean table;
        try {
            strSql = "SELECT * FROM tb_tmp_Tables WHERE TABLE_NAME = " + dbl.sqlString(sTabName.toUpperCase());
            rs = dbl.openResultSet(strSql);
            if (!rs.next()) {
                return null;
            }
            table = new TableBean();
            table.setFTableName(rs.getString("TABLE_NAME"));
            table.setFTableSpaceName(rs.getString("TableSpace_Name"));

            dbl.closeResultSetFinal(rs);
            table.setColumns(getOldTableColumns(table.getFTableName()));
            table.setPkCons(getOldTablePKCons(table.getFTableName()));

        } catch (Exception e) {
            throw new YssException("获取旧的表结构实体出错！\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return table;
    }

    private TableBean getNewTable(String sTabName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        TableBean table = new TableBean();
        try {
            strSql = "SELECT * FROM TB_FUN_AllTableName WHERE FTableName = " + dbl.sqlString(sTabName.toUpperCase());
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                table.setFTableName(rs.getString("FTableName"));
                table.setFTableSpaceName(rs.getString("FTableSpaceName"));
            }
            dbl.closeResultSetFinal(rs);
            table.setColumns(getNewTableColumns(table.getFTableName()));
            table.setPkCons(getNewTablePKCons(table.getFTableName()));

        } catch (Exception e) {
            throw new YssException("获取新的表结构实体出错！\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return table;
    }

    private ArrayList getOldTableColumns(String sTabName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ArrayList alCols = new ArrayList();
        ColumnsBean sCol = new ColumnsBean();
        try {
            strSql = "SELECT * FROM tb_tmp_Columns WHERE TABLE_NAME = " +
                dbl.sqlString(sTabName.toUpperCase()) +
                " ORDER BY COLUMN_ID";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sCol = new ColumnsBean();
                sCol.setFCLOUMNNAME(rs.getString("COLUMN_NAME"));
                sCol.setFCOLUMNID(rs.getString("COLUMN_ID"));
                sCol.setFDATALENGTH(rs.getString("DATA_LENGTH"));
                sCol.setFDATAPRECISION(rs.getString("DATA_PRECISION"));
                sCol.setFDATASCALE(rs.getString("DATA_SCALE"));
                sCol.setFDATATYPE(rs.getString("DATA_TYPE"));
                sCol.setFDEFULTVALUE(rs.getString("DATA_DEFAULT"));
                sCol.setFNULLABLE(rs.getString("NULLABLE"));
                sCol.setFTABLENAME(rs.getString("TABLE_NAME"));
                alCols.add(sCol);
            }
        } catch (Exception e) {
            throw new YssException("获取旧的表结构的列出错！\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alCols;

    }

    private ArrayList getNewTableColumns(String sTabName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ArrayList alCols = new ArrayList();
        ColumnsBean sCol = new ColumnsBean();
        try {
            strSql = "SELECT * FROM TB_Fun_Columns WHERE FTableName = " + dbl.sqlString(sTabName.toUpperCase()) +
                " ORDER BY FColumnId";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sCol = new ColumnsBean();
                sCol.setFCLOUMNNAME(rs.getString("FCOLUMNNAME"));
                sCol.setFCOLUMNID(rs.getString("FCOLUMNID"));
                sCol.setFDATALENGTH(rs.getString("FDATALENGTH"));
                sCol.setFDATAPRECISION(rs.getString("FDATAPRECISION"));
                sCol.setFDATASCALE(rs.getString("FDATASCALE"));
                sCol.setFDATATYPE(rs.getString("FDATATYPE"));
                sCol.setFDEFULTVALUE(rs.getString("FDATADEFAULT"));
                sCol.setFNULLABLE(rs.getString("FNULLABLE"));
                sCol.setFTABLENAME(rs.getString("FTABLENAME"));
                sCol.setFINSERTSCRIPT(rs.getString("FINSERTSCRIPT"));
                alCols.add(sCol);
            }
        } catch (Exception e) {
            throw new YssException("获取新的表结构的列出错！\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alCols;
    }

    /**
     * 获取旧的表结构的主键
     * @param sTabName String
     * @return ConstraintsBean
     * @throws YssException
     */
    private ConstraintsBean getOldTablePKCons(String sTabName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ConstraintsBean cons = null;
        try {
            strSql =
                "SELECT * FROM tb_tmp_Const WHERE Constraint_Type = 'P' AND TABLE_NAME = " +
                dbl.sqlString(sTabName);
            rs = dbl.openResultSet(strSql);
            if (!rs.next()) {
                return null;
            }
            cons = new ConstraintsBean();
            cons.setFCONSTRAINTNAME(rs.getString("CONSTRAINT_NAME"));
            cons.setFCONTYPE("P");
            cons.setFTABLENAME(rs.getString("TABLE_NAME"));

            cons.setConsCols(getOldTableConsCols(sTabName, cons.getFCONSTRAINTNAME()));
        } catch (Exception e) {
            throw new YssException("获取旧的表结构的主键出错！\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return cons;
    }

    /**
     * 获取新的表结构的主键
     * @param sTabName String
     * @return ConstraintsBean
     * @throws YssException
     */
    private ConstraintsBean getNewTablePKCons(String sTabName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ConstraintsBean cons = null;
        try {
            strSql = "SELECT * FROM TB_Fun_CONSTRAINTS WHERE FTableName = " + dbl.sqlString(sTabName.toUpperCase()) +
                " AND FConType = 'P'";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                cons = new ConstraintsBean();
                cons.setFCONSTRAINTNAME(rs.getString("FCONSTRAINTNAME"));
                cons.setFCONTYPE("P");
                cons.setFTABLENAME(sTabName.toUpperCase());
            }
            //xuqiji 2009 0415  MS00352    新建组合群时能够自动创建对应的一套表------------------
            if (null != cons) {
                if (null != cons.getFCONSTRAINTNAME() ||
                    cons.getFCONSTRAINTNAME().length() != 0) {
                    //xuqiji 2009 0415  MS00352    新建组合群时能够自动创建对应的一套表------------------//
                    cons.setConsCols(getNewTableConsCols(sTabName,
                        cons.getFCONSTRAINTNAME()));
                    //xuqiji 2009 0415  MS00352    新建组合群时能够自动创建对应的一套表------------------
                }
            }
            //-------------xuqiji  MS00352    新建组合群时能够自动创建对应的一套表--------------//
        } catch (Exception e) {
            throw new YssException("获取新的表结构的主键出错！\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return cons;
    }

    /**
     * 获取旧的表结构的主键组成
     * @param sTabName String
     * @param sConsName String
     * @return ArrayList
     * @throws YssException
     */
    private ArrayList getOldTableConsCols(String sTabName, String sConsName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ConsColsBean consCols = null;
        ArrayList alConsCols = new ArrayList();
        try {
            strSql = "SELECT * FROM tb_tmp_ConstCols WHERE Table_Name = " + dbl.sqlString(sTabName.toUpperCase()) +
                " AND constraint_name = " + dbl.sqlString(sConsName.toUpperCase()) +
                " ORDER BY POSITION";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                consCols = new ConsColsBean();
                consCols.setFCOLUMNNAME(rs.getString("Column_Name"));
                consCols.setFCONSTRAINTNAME(rs.getString("constraint_name"));
                consCols.setFPOSITION(rs.getString("POSITION"));
                consCols.setFTABLENAME(rs.getString("Table_Name"));
                alConsCols.add(consCols);
            }
        } catch (Exception e) {
            throw new YssException("获取旧的表结构的主键组成出错！\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alConsCols;
    }

    /**
     * 获取新的表结构的主键组成
     * @param sTabName String：表名
     * @param sConsName String：约束名
     * @return ArrayList
     * @throws YssException
     */
    private ArrayList getNewTableConsCols(String sTabName, String sConsName) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ConsColsBean consCols = null;
        ArrayList alConsCols = new ArrayList();
        try {
            strSql = "SELECT * FROM TB_FUN_ConsCols WHERE FTableName = " + dbl.sqlString(sTabName.toUpperCase()) +
                " AND FConstraintName = " + dbl.sqlString(sConsName.toUpperCase()) +
                " ORDER BY FPOSITION";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                consCols = new ConsColsBean();
                consCols.setFCOLUMNNAME(rs.getString("FCOLUMNNAME"));
                consCols.setFCONSTRAINTNAME(rs.getString("FCONSTRAINTNAME"));
                consCols.setFPOSITION(rs.getString("FPOSITION"));
                consCols.setFTABLENAME(rs.getString("FTABLENAME"));
                alConsCols.add(consCols);
            }
        } catch (Exception e) {
            throw new YssException("获取新的表结构的主键组成出错！\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alConsCols;
    }

}
