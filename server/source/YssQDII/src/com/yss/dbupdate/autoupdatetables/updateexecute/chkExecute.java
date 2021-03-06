package com.yss.dbupdate.autoupdatetables.updateexecute;

import com.yss.util.*;

/**
 *
 * <p>Title: 实现检查更新检查的流程</p>
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
public class chkExecute
    extends BaseUpdateExecute {
    public chkExecute() {
    }

    /**
     * 检查过程和更新的过程如果出现异常抛出的异常信息是不一样的，在这里实现
     * @return String
     * @throws YssException
     */
    protected String getErrorMessage() throws YssException {
        return "表结构更新前检查出错！";
    }

    /**
     * 检查的过程使用同一个临时表表名
     * @return String
     * @throws YssException
     */
    protected String getTmpTableName() throws YssException {
        return "TB_Tmp_UpdateCheckTable";
    }

    /**
     * 检查的过程不需要删除约束
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void dropTableCons(StringBuffer sqlBuf) throws YssException {
    }

    /**
     * 检查的过程不需要修改表名
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void renameTable(StringBuffer sqlBuf) throws YssException {
    }

    /**
     * 创建表的过程与更新不同
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void createTable(StringBuffer sqlBuf) throws YssException {
        try {
            //如果临时表已存在删除临时表
            this.dropTmpTable(sqlBuf);
            //创建表时要指定表名
            this.StandardCreateTableProc(sqlBuf, this.tmpTableName);
        } catch (Exception e) {
            throw new YssException("数据检查出错！\r\n", e);
        }
    }

    /**
     * 复制数据的过程是一样的
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void copyDate(StringBuffer sqlBuf) throws YssException { //xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表
        String doCkeck = "check"; //xuqiji MS00352    新建组合群时能够自动创建对应的一套表    20090416
        try {
        	///**Start---panjunfang 2013-11-22 BUG 83351 */
        	//复制数据前的检查
        	//检查内容有：1、若更新前表字段长度大于新定义的表字段长度，则更新临时表的该字段长度和旧表字段长度一致
        	StandardAlterTableProc(sqlBuf,this.tmpTableName);
			/**End---panjunfang 2013-11-22 BUG 83351*/
            this.StandardCopyDataProc(sqlBuf, doCkeck); //xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表
        } catch (Exception e) {
            throw new YssException("数据检查出错！\r\n", e);
        }
    }

    /**
     * 创建约束的过程与更新有所不同，需要指定表名和约束名
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void createTableCons(StringBuffer sqlBuf) throws YssException {
        String sPKName = "";
        try {
            //得到约束名
            sPKName = "PK_" + this.tmpTableName.substring(0, 14);
            this.StandardCreateConsProc(sqlBuf, this.tmpTableName, sPKName);
        } catch (Exception e) {
            throw new YssException("数据检查出错！\r\n", e);
        }
    }

    /**
     * 删除临时表
     * @param sqlBuf StringBuffer
     * @throws YssException
     */
    protected void dropTmpTable(StringBuffer sqlBuf) throws YssException {
        try {
            this.StandardDropTableProc(sqlBuf);
        } catch (Exception e) {
            throw new YssException("数据检查出错！\r\n", e);
        }
    }

    /**
     * 检查过程不记录表名
     * @param updateTables StringBuffer
     * @throws YssException
     */
    protected void registerTableName(StringBuffer updateTables) throws
        YssException {
    }
}
