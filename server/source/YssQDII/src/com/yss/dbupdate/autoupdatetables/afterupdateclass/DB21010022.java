package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.*;
import java.util.*;

import com.yss.dbupdate.*;
import com.yss.main.syssetting.*;
import com.yss.util.*;

/**
 * <p>Title: 角色权限的转换</p>
 *
 * <p>Description:对角色权限的历史数据进行转换，并将角色权限明细到组合 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author fanghaoln 2009-11-11
 * @bug    MS00590:QDV4赢时胜（上海）2009年7月24日09_B
 */
public class DB21010022
    extends BaseDbUpdate {
    public DB21010022() {
    }

    public void doUpdate(HashMap hmInfo) throws YssException {
        try {
            //如果已经进行了历史数据的转换，则不必再次进行数据转换
            if (!this.isExistsSuccessVerNum(YssCons.YSS_VERSION_1010022)) {
                convertRoleRightData(hmInfo); //用于做历史数据转换
            }
        } catch (Exception ex) {
            throw new YssException("版本 1.0.1.00022 更新出错！", ex);
        }

    }

    /**
     * 根据权限类型表和角色权限表对角色权限进行转换
     * @param hmInfo HashMap
     * @throws YssException
     */
    public void convertRoleRightData(HashMap hmInfo) throws YssException {
    	StringBuffer sqlInfo = null;
        StringBuffer updTables = null;
        sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
        StringBuffer bufSql = new StringBuffer(); //用于储存sql语句
        String sPKName = ""; //用于储存主键名
        ResultSet rs = null; //声明结果集
        try {
            //若有表TB_DAO_SWIFT_fh则删除
            if (dbl.yssTableExist(pub.yssGetTableName("TB_DAO_SWIFT_fh"))) {
                sqlInfo.append("DROP TABLE "+pub.yssGetTableName("TB_DAO_SWIFT_fh"));
                dbl.executeSql("DROP TABLE "+pub.yssGetTableName("TB_DAO_SWIFT_fh"));
            }
            //通过重命名来备份表
            sqlInfo.append("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFT ")+" RENAME TO "+pub.yssGetTableName("TB_DAO_SWIFT_fh"));
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFT ")+" RENAME TO "+pub.yssGetTableName("TB_DAO_SWIFT_fh"));

            //建立表的结构
            sqlInfo.append("create table "+pub.yssGetTableName("TB_DAO_SWIFT ")+" as select * from  "+pub.yssGetTableName("TB_DAO_SWIFT_fh")+" where 1=2");
            dbl.executeSql("create table "+pub.yssGetTableName("TB_DAO_SWIFT ")+" as select * from  "+pub.yssGetTableName("TB_DAO_SWIFT_fh")+" where 1=2");

            //增加新的字段
            if(this.existsTabColumn_Ora(pub.yssGetTableName("TB_DAO_SWIFT"),"FSWIFTCODE")){
            sqlInfo.append("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFT ")+" add FSWIFTCODE varchar2(30) not null ");
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFT ")+" add FSWIFTCODE varchar2(30) not null ");
            }
          bufSql.delete(0, bufSql.length());
            
                        //导入表中原来数据
                        bufSql.append(" Insert into "+pub.yssGetTableName("TB_DAO_SWIFT ")+"( ");
                        bufSql.append(" FSWIFTTYPE,");
                        bufSql.append(" FSWIFTDESC,");
                        bufSql.append(" FTABLECODE,");
                        bufSql.append(" FPATH,");
                        bufSql.append(" FCRITERION,");
                        bufSql.append(" FOPERTYPE,");
                        bufSql.append(" FREFLOW,");
                        bufSql.append(" FDSCODE,");
                        bufSql.append(" FCHECKSTATE,");
                        bufSql.append(" FCREATOR,");
                        bufSql.append(" FCREATETIME,");
                        bufSql.append(" FCHECKUSER,");
                        bufSql.append(" FCHECKTIME,");
                        bufSql.append(" FSWIFTCODE)");
                        bufSql.append(" select FSWIFTTYPE,");
                        bufSql.append(" FSWIFTDESC,");
                        bufSql.append(" FTABLECODE,");
                        bufSql.append(" FPATH,");
                        bufSql.append(" FCRITERION,");
                        bufSql.append(" FOPERTYPE,");
                        bufSql.append(" FREFLOW,");
                        bufSql.append(" FDSCODE,");
                        bufSql.append(" FCHECKSTATE,");
                        bufSql.append(" FCREATOR,");
                        bufSql.append(" FCREATETIME,");
                        bufSql.append(" FCHECKUSER,");
                        bufSql.append(" FCHECKTIME,");
                        bufSql.append(" rownum as FSWIFTCODE  from ");
                        bufSql.append(pub.yssGetTableName("TB_DAO_SWIFT_fh"));
                        sqlInfo.append(bufSql.toString());
                        dbl.executeSql(bufSql.toString()); //做历史数据转换
                        bufSql.delete(0, bufSql.length());
          //添加TB_Sys_UserRight的主键约束
            sqlInfo.append("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFT ")+" ADD CONSTRAINT "+pub.yssGetTableName("TB_DAO_SWIFT ")+" PRIMARY KEY (FSWIFTCODE)");
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFT ")+" ADD CONSTRAINT "+pub.yssGetTableName("TB_DAO_SWIFT ")+" PRIMARY KEY (FSWIFTCODE)");
          //==================================================更新表TB_DAO_SWIFTENTITY=============
            //若有表TB_DAO_SWIFTENTITY_fh则删除
            if (dbl.yssTableExist(pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"))) {
                sqlInfo.append("DROP TABLE "+pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"));
                dbl.executeSql("DROP TABLE "+pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"));
            }
            //通过重命名来备份表
            sqlInfo.append("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+" RENAME TO "+pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"));
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+" RENAME TO "+pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"));

            //建立表的结构
            sqlInfo.append("create table "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+" as select * from  "+pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh")+" where 1=2");
            dbl.executeSql("create table "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+" as select * from  "+pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh")+" where 1=2");

            //增加新的字段
            if(this.existsTabColumn_Ora(pub.yssGetTableName("TB_DAO_SWIFTENTITY"),"FSWIFTCODE")){
            sqlInfo.append("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+" add FSWIFTCODE varchar2(30) not null ");
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+" add FSWIFTCODE varchar2(30) not null ");
            }
          bufSql.delete(0, bufSql.length());
            
                        //导入表中原来数据
                        bufSql.append(" Insert into "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+"( ");
                        bufSql.append(" FSWIFTTYPE,");
                        bufSql.append(" FSTATUS,");
                        bufSql.append(" FINDEX,");
                        bufSql.append(" FCONTENT,");
                        bufSql.append(" FOPTION,");
                        bufSql.append(" FTAG,");
                        bufSql.append(" FQUALIFIER,");
                        bufSql.append(" FFIELDNAME,");
                        bufSql.append(" FFIELDFULLNAME,");
                        bufSql.append(" FTABLEFIELD,");
                        bufSql.append(" FCHECKSTATE,");
                        bufSql.append(" FCREATOR,");
                        bufSql.append(" FCREATETIME,");
                        bufSql.append(" FCHECKUSER,");
                        bufSql.append(" FCHECKTIME,");
                        bufSql.append(" FSWIFTCODE)");
                        bufSql.append(" select a.FSWIFTTYPE,");
                        bufSql.append(" a.FSTATUS,");
                        bufSql.append(" a.FINDEX,");
                        bufSql.append(" a.FCONTENT,");
                        bufSql.append(" a.FOPTION,");
                        bufSql.append(" a.FTAG,");
                        bufSql.append(" a.FQUALIFIER,");
                        bufSql.append(" a.FFIELDNAME,");
                        bufSql.append(" a.FFIELDFULLNAME,");
                        bufSql.append(" a.FTABLEFIELD,");
                        bufSql.append(" a.FCHECKSTATE,");
                        bufSql.append(" a.FCREATOR,");
                        bufSql.append(" a.FCREATETIME,");
                        bufSql.append(" a.FCHECKUSER,");
                        bufSql.append(" a.FCHECKTIME,");
                        bufSql.append(" b.fswiftcode as FSFTCODE ");
                        bufSql.append(" from "+pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh")+" a ");
                        bufSql.append(" left join "+pub.yssGetTableName("TB_DAO_SWIFT")+" b on a.fswifttype=b.fswifttype ");
                        sqlInfo.append(bufSql.toString());
                        dbl.executeSql(bufSql.toString()); //做历史数据转换
                        bufSql.delete(0, bufSql.length());
          //添加TB_Sys_UserRight的主键约束
            sqlInfo.append("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+" ADD CONSTRAINT "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+" PRIMARY KEY (FSTATUS,FINDEX,FCONTENT,FOPTION,FTAG,FQUALIFIER,FSWIFTCODE)");
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+" ADD CONSTRAINT "+pub.yssGetTableName("TB_DAO_SWIFTENTITY ")+" PRIMARY KEY (FSTATUS,FINDEX,FCONTENT,FOPTION,FTAG,FQUALIFIER,FSWIFTCODE)");
        
        } catch (Exception ex) {
            throw new YssException(ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
