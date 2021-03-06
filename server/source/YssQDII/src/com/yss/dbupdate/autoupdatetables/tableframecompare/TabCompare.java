package com.yss.dbupdate.autoupdatetables.tableframecompare;

import java.sql.*;
import java.util.*;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.dsub.*;
import com.yss.util.*;

/**
 *
 * <p>Title: 表结构比较</p>
 *
 * <p>Description: 比较表结构，把有差异的表名都返回出去</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TabCompare
    extends BaseBean {
    public TabCompare() {
    }

    /**
     * 入口方法
     * @return ArrayList：所有有差异的表名，List 中存的是字符串
     * @throws YssException
     */
    public ArrayList getDiffFrameTablesName() throws YssException {
        //先用 Set 存表明，就不会有重复了
        Set set = new HashSet();
        ResultSet rs = null;
        Iterator it = null;
        ArrayList alTabName = new ArrayList();
        try {
            //创建临时字典表
            TmpTabCreate tabCreate = new TmpTabCreate();
            tabCreate.setYssPub(pub);
            tabCreate.CreateTmpDictTables();

            //看是否有新表
            rs = dbl.openResultSet(getCheckTableStr().toString());
            while (rs.next()) {
                set.add(rs.getString("FTableName"));
            }
            dbl.closeResultSetFinal(rs);

            //是否存在列的修改
            rs = dbl.openResultSet(getCheckTableCloumsStr().toString());
            while (rs.next()) {
                set.add(rs.getString("FTableName"));
            }
            dbl.closeResultSetFinal(rs);

            //比较默认值，Oracle 的默认值在 Oracle 中存在 Lang 数据类型里面，不能直接比较了，单独用个方法
            CheckClosDefault(set);

            //察看是否有表没有主键约束
            rs = dbl.openResultSet(getCheckTableConStr().toString());
            while (rs.next()) {
                set.add(rs.getString("FTableName"));
            }
            dbl.closeResultSetFinal(rs);

            //察看主键的组成是否有修改
            rs = dbl.openResultSet(getCheckTableConsColsStr().toString());
            while (rs.next()) {
                set.add(rs.getString("FTableName"));
            }

            //把 Set 里面的数据弄出来
            it = set.iterator();
            while (it.hasNext()) {
                alTabName.add(it.next());
            }
        } catch (Exception e) {
            throw new YssException("表结构比较出错！\r\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alTabName;
    }
    /**
     * xuqiji 20100526
     * 当有多个组合群更新时，第一个组合群更新成功，并且此时一些系统表被更新为最高版本，此时更新其它组合群时，要排除重新更新这些系统表
     * @param alTabName
     */
    public void removeTableByOtherAssetGroupUpdated(String verNum,ArrayList alTabName) throws YssException{
		BaseDbUpdate base = null;
		String sTableName ="";
    	try{
    		base = new BaseDbUpdate();
    		base.setYssPub(pub);
    		if(base.isExistsUpdateSuccessVerNum(verNum)){
    			for (int i = 0; i < alTabName.size(); i++) {
    				sTableName = (String) alTabName.get(i);
    				if(sTableName.toLowerCase().startsWith("tb_sys") ||
    						sTableName.toLowerCase().startsWith("tb_pfsys") ||
    			            sTableName.toLowerCase().startsWith("tb_base")  ||
    			            sTableName.toLowerCase().startsWith("tb_fun")){
    					alTabName.remove(i);
    				}
				}
    		}
		}catch (Exception e) {
			throw new YssException("移除已经更新为最高版本的系统表出错！",e);
		}
		
	}

	/**
     * 比较默认值
     * @param set Set：放表结构有差异表名
     * @throws YssException
     */
    private void CheckClosDefault(Set set) throws YssException {
        ResultSet rs = null;
        String sDefaultA = "";
        String sDefaultB = "";
        try {
            rs = dbl.openResultSet(getCheckCloDefaultStr().toString());
            while (rs.next()) {
                sDefaultA = rs.getString("FDataDefault");
                //Oracle 的 Lang 类型，需要特别的处理
                if (dbl.dbType == YssCons.DB_ORA) {
                    sDefaultB = dbl.clobStrValue(rs.getClob("Data_Default"));
                } else {
                    sDefaultB = rs.getString("Data_Default");
                }
                //这一砣判断，只是为了确认是否两边都没有默认值！
                if ( (sDefaultA == null || sDefaultA.trim().equalsIgnoreCase("null") || sDefaultA.trim().length() == 0) &&
                    (sDefaultB == null || sDefaultB.trim().length() == 0 || sDefaultB.trim().equalsIgnoreCase("null"))) {
                    continue;
                }
                //这一砣判断两边都有默认值，而且默认值不同，才说明有差异
                else if ( (sDefaultA == null || sDefaultB == null) ||
                         !sDefaultA.trim().equals(sDefaultB.trim())) {
                    set.add(rs.getString("FTableName"));
                }
            }
        } catch (Exception e) {
            throw new YssException("比较默认值出错！\r\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 比较表名的 SQL
     * @return StringBuffer
     * @throws YssException
     */
    private StringBuffer getCheckTableStr() throws YssException {
        StringBuffer buf = new StringBuffer();

        buf.append("select FTABLENAME from TB_FUN_AllTableName where FTABLENAME not in (select TABLE_NAME from tb_tmp_Tables)");

        return buf;
    }

    /**
     * 比较列数据的 SQL
     * @return StringBuffer
     * @throws YssException
     */
    private StringBuffer getCheckTableCloumsStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        /**modify by liuxiaojun stroy 4156 20130826  关于复制数据时数据的长度、大小可能不容纳导致的错误*/
        buf.append(" SELECT DISTINCT a.FTableName ");
        buf.append(" FROM (SELECT *");
        buf.append(" FROM Tb_Fun_Columns) a ");
        buf.append(" LEFT JOIN (SELECT *");
        buf.append(" FROM tb_tmp_Columns) b ON a.FTableName = b.Table_Name ");
        buf.append(" AND a.FColumnName = b.Column_Name ");
        buf.append(" WHERE (b.Table_Name IS NULL ");
        buf.append(" OR b.Column_Name IS NULL) ");
        buf.append(" OR a.Fdatatype <> b.data_type ");
        buf.append(" OR a.fdatalength > b.data_length ");
        buf.append(" OR a.fdataprecision <> b.data_precision ");
        buf.append(" OR a.fdatascale <> b.data_scale ");
        buf.append(" OR a.fnullable <> b.nullable ");
        /**end stroy 4156*/
        return buf;
    }

    /**
     * 取出所有表所有列默认值的 SQL
     * @return StringBuffer
     * @throws YssException
     */
    private StringBuffer getCheckCloDefaultStr() throws YssException {
        StringBuffer bufSql = new StringBuffer();

        bufSql.append(" SELECT a.FTableName, a.FDataDefault, b.Data_Default ");
        bufSql.append(" FROM (SELECT * ");
        bufSql.append(" FROM Tb_Fun_Columns) a ");
        bufSql.append(" LEFT JOIN (SELECT * ");
        bufSql.append(" FROM tb_tmp_Columns) b ON a.FTableName = b.Table_Name ");
        bufSql.append(" AND a.FColumnName = b.Column_Name ");

        return bufSql;
    }

    /**
     * 比较约束的 SQL
     * @return StringBuffer
     * @throws YssException
     */
    private StringBuffer getCheckTableConStr() throws YssException {
        StringBuffer bufSql = new StringBuffer();

        //SQL 语句中不能比对主键名称，应为 Oracle 的主键名称和 DB2 的主键名称不一样。
        bufSql.append(" SELECT * FROM TB_FUN_Constraints WHERE FTableName NOT IN (SELECT Table_Name FROM tb_tmp_Const)");

        return bufSql;
    }

    /**
     * 比较约束组成的 SQL
     * @return StringBuffer
     * @throws YssException
     */
    private StringBuffer getCheckTableConsColsStr() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        //SQL 语句中不能比对主键名称，应为 Oracle 的主键名称和 DB2 的主键名称不一样。
        bufSql.append(" SELECT DISTINCT a.FTableName ");
        bufSql.append(" FROM (SELECT * ");
        bufSql.append(" FROM TB_FUN_CONSCOLS) a ");
        bufSql.append(" LEFT JOIN (SELECT * ");
        bufSql.append(" FROM tb_tmp_ConstCols) b ON a.Ftablename = b.Table_Name");
        bufSql.append(" AND a.FColumnName = b.Column_Name ");
        bufSql.append(" WHERE b.constraint_name IS NULL ");
        bufSql.append(" OR b.Table_Name IS NULL ");
        bufSql.append(" OR b.Column_Name IS NULL ");

        return bufSql;
    }
}
