package com.yss.dbupdate.autoupdatetables.updateexecute;

import com.yss.util.*;

public class updExecute
    extends BaseUpdateExecute {
    //用于产生不重复的临时表名，每产生一个表名，此变量会被累加
    private int num = 0;
    public updExecute() {
    }

    /**
     * 更新过程的异常信息
     * @return String
     * @throws YssException
     */
    protected String getErrorMessage() throws YssException {
        return "表结构更新执行中出错！";
    }

    /**
     * 得到临时表名
     * @return String
     * @throws YssException
     */
    protected String getTmpTableName() throws YssException {
        //临时表名通过当前系统的年月日时分秒加上累加的标号产生
        //如果要修改这部分，请注意表名长度不要超过18位字符
        num++;
        String sTmpTabName = "";
        String sTime = YssFun.formatDate(new java.util.Date(), "yyyyMMddHHmmss") + String.valueOf(num);
        sTmpTabName = "TB_" + sTime + String.valueOf(num);
        return sTmpTabName;
    }

    /**
     * 删除约束
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void dropTableCons(StringBuffer sqlBuf) throws YssException {
        try {
            this.StandardDropTableConsProc(sqlBuf);
        } catch (Exception e) {
            throw new YssException("执行更新过程出错！\r\n", e);
        }
    }

    protected void renameTable(StringBuffer sqlBuf) throws YssException {
        String sql = "";
        try {
            if (this.oldTable == null) {
                //说明是新建表，直接返回，不需要更名
                return;
            }

            sql = sqlBuild.getRenameStr(newTable.getFTableName(), tmpTableName);
            sqlBuf.append(sql).append("\n");
            dbl.executeSql(sql);
        } catch (Exception e) {
            throw new YssException("执行更新过程出错，Rename 表出错！\r\n", e);
        }
    }

    /**
     * 创建新表
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void createTable(StringBuffer sqlBuf) throws YssException {
        try {
            this.StandardCreateTableProc(sqlBuf);
        } catch (Exception e) {
            throw new YssException("执行更新过程出错！\r\n", e);
        }
    }

    protected void copyDate(StringBuffer sqlBuf) throws YssException { //xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表
        String doUpdate = "update"; //xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表
        try {
        	///**Start---panjunfang 2013-11-22 BUG 83351 */
        	//复制数据前的检查
        	//检查内容有：1、若更新前表字段长度大于新定义的表字段长度，则更新临时表的该字段长度和旧表字段长度一致
        	this.StandardAlterTableProc(sqlBuf,newTable.getFTableName());
			/**End---panjunfang 2013-11-22 BUG 83351*/
            this.StandardCopyDataProc(sqlBuf, doUpdate); //xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表
        } catch (Exception e) {
            throw new YssException("执行更新过程出错！\r\n", e);
        }
    }

    protected void createTableCons(StringBuffer sqlBuf) throws YssException {
        try {
            this.StandardCreateConsProc(sqlBuf);
        } catch (Exception e) {
            throw new YssException("执行更新过程出错！\r\n", e);
        }
    }

    /**
     * 更新的过程不需要删除临时表
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void dropTmpTable(StringBuffer sqlBuf) throws YssException {
    	// #4251 :: 现需重新定义临时表命名规则，以便在更新表结构后将临时表删除 add by jiangshichao 2011.04.09 删除更新表结构时临时表。
    	 try {
             this.StandardDropTableProc(sqlBuf);
         } catch (Exception e) {
             throw new YssException("数据检查出错！\r\n", e);
         }
    }

    protected void registerTableName(StringBuffer updateTables) throws
        YssException {
        this.StandardRegisterTableName(updateTables);
    }
}
