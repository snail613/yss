package com.yss.dsub;

import java.sql.*;

import com.yss.util.*;
import com.yss.vsub.*;

/**
 *
 * <p>Title: Ora </p>
 * <p>Description: Oracle 数据库处理</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class Ora
    extends BaseBean {
    private DbBase dbl = null; //DbBase类实例
    public Ora(DbBase db) {
        dbl = db;
    }

    public void setYssPub(YssPub ysspub) {
        pub = ysspub;
        dbl = ysspub.getDbLink();
    }

    /**
     * 通过表名删除表
     * @param sName String
     * @throws YssException
     * @return boolean
     */
    public boolean OraRemoveTable(String sName) throws YssException {
        return RemoveOrExist(sName, true);
    }

    /**
     * 看表是否存在
     * @param sName String
     * @throws YssException
     * @return boolean
     */
    public boolean OraTableExist(String sName) throws YssException {
        return RemoveOrExist(sName, false);
    }

    /**
     * 移除表或判断表是否存在
     * @param sName String
     * @param bRemove boolean true 为移除表
     * @throws YssException
     * @return boolean
     */

    private boolean RemoveOrExist(String sName, boolean bRemove) throws
        YssException {
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        boolean bState = false;
        try {
            rs = dbl.openResultSet(
                "select table_name from user_tables where table_name='" +
                sName.toUpperCase() + "'");
            if (rs.next()) {
                if (bRemove) {
                    conn.setAutoCommit(false);
                    bTrans = true;
                    dbl.executeSql("drop table " + sName);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
                bState = true;
            } else {
                if (bRemove) {
                    bState = true;
                }
            }

            rs.getStatement().close();
            return bState;
        } catch (SQLException sqle) {
            throw new YssException("删除数据库表" + sName + "出错！", sqle);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 创建独立于组合的表
     * @throws YssException
     * @return boolean
     */
    public boolean CreateCommon() throws YssException {
        String[][] sSql = new String[16][2];
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        int j = 0;

        try {
            for (int i = 0; i < sSql.length; i++) {
                if (OraTableExist(sSql[i][0])) {
                    OraRemoveTable(sSql[i][0]);
                }
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql("Create Table " + sSql[i][0] + "(" + sSql[i][1] +
                               ")");
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return true;

        } catch (SQLException sqle) {
            throw new YssException("创建系统表出错！", sqle);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    /**
     * yeshenghong 20111209
     * 创建套账涉及的建表(覆盖建套账前缀表、套账年份前缀表；不覆盖建年份相关表)
     * @param sPre String：年份套账前缀
     */
    protected void createSet(String sPre) throws YssException {
       StringBuffer tabCreated = new StringBuffer(); //记录已经创建的表
       String sSql[][] = getYearSetTables();
       String sTmp = sPre.substring(0, 1) + sPre.substring(5, sPre.length());
       Statement st = null;

       try {
          st = dbl.openStatement();
          //年份套账表
          for (int i = 0; i < sSql.length; i++) {
             System.out.print(sSql[i][0]);
             if (sSql[i][0] != null) {
                if (dbl.yssTableExist(sPre + sSql[i][0]))
                   st.executeUpdate("drop table " + sPre + sSql[i][0]);
                st.executeUpdate("create table " + sPre + sSql[i][0] + "(" +
                                 sSql[i][1] + ")");
                tabCreated.append("\t").append(sPre + sSql[i][0]);
             }
          }
          //创建部分表的索引
          st.executeUpdate("CREATE INDEX Date_" + sPre + "fcwvch ON " + sPre +
                           "fcwvch(fdate)");

          //再看年份的表，因为可能套账创建的这个年份还没有其它套账
          sTmp = sPre.substring(0, 5);
          sSql = getYearTables();
          for (int i = 0; i < sSql.length; i++) {
             if (sSql[i][0] != null && !dbl.yssTableExist(sTmp + sSql[i][0])) {
                st.executeUpdate("create table " + sTmp + sSql[i][0] + "(" +
                                 sSql[i][1] + ")");
                tabCreated.append("\t").append(sTmp + sSql[i][0]);
             }
          }
          st.close();
       }
       catch (Exception e) {
          if (tabCreated.length() > 0) {
             String sRoll[] = tabCreated.substring(1).split("\t", -1);
             for (int i = 0; i < sRoll.length; i++) {
                try {
                	if(st!=null)
                	{
                		st.executeUpdate("drop table " + sRoll[i]);
                	}
                }
                catch (Exception ee) {}
             }
          }
          try {
             if (st != null) st.close();
          }
          catch (Exception ee) {}
          throw new YssException("创建套账出错！", e);
       }
    }
  //BUG5458 modified by yeshenghong 20120830
    protected void dropSet(String sPre) throws YssException {
        String sSql[][] = getYearSetTables();
        try {
           //年份套账表
           for (int i = 0; i < sSql.length; i++) {
              if (sSql[i][0] != null) {
                 if (dbl.yssTableExist(sPre + sSql[i][0]))
                    dbl.executeSql("drop table " + sPre + sSql[i][0]);
              }
           }
        }catch (Exception e) {
        	throw new YssException("创建套账出错！", e);
        }
    }
    
    
    
    //得到只有年份前缀的建表语句
    private String[][] getYearTables() {
       String[][] sSql = new String[1][2];
       sSql[0][0] = "JjHzHq"; //行情记录

       sSql[0][1] = "FDate DATE NOT NULL ,FZqdm varchar2 (6)  NOT NULL ,"
             + "FSzsh varchar2 (1)  NOT NULL,"
             +
             "FHqSsj decimal(18, 4) NOT NULL ,FHqPjj decimal(18, 4) NOT NULL ,"
             +
             "FZrSp decimal(18, 2) NOT NULL ,FJrKp decimal(18, 2) NOT NULL ,"
             +
             "FZgCj decimal(18, 2) NOT NULL ,FZdCj decimal(18, 2) NOT NULL ,"
             +
             "FCjSl DECIMAL (19,0) NOT NULL ,FCjJe decimal(18, 2) NOT NULL ,"
             +
             "FZf decimal(18, 2) NOT NULL,FPzbz varchar2 (1),"
             + "FBJSJ  DECIMAL(18, 4)  NOT NULL ,"
             + "FSJSJ  DECIMAL(18, 4)  NOT NULL ,"
             + "FWfsyl  DECIMAL(18,6)  NOT NULL ," //20050901 wxd add
             + "FYwDate DATE  ,"
             + "PRIMARY KEY (FDate,FZqdm,FSzsh,FZf)";

       return sSql;
    }
   
    //得到年份套账前缀的建表语句
    private String[][] getYearSetTables( ) {
       String[][] sSql = getYearsetTables();

       sSql[0][1] = "fterm number(5) DEFAULT 0 NOT NULL ,fvchclsid varchar2(20) DEFAULT ' ' NOT NULL ,"
           + "fvchpdh number(10) DEFAULT 0 NOT NULL ,fvchbh number(5) NOT NULL ,"
           + "fvchzy varchar2(80) DEFAULT ' ' NOT NULL ,"
           + "fkmh varchar2(50) NOT NULL ,fcyid varchar2(3) NOT NULL ,"
           +
           "frate DECIMAL(18,4) DEFAULT 1 NOT NULL ,fyhdzbz number(2) DEFAULT 0 NOT NULL ,"
           + "fbal number(19,4) DEFAULT 0 NOT NULL ,"
           + "fjd varchar2(2) NOT NULL ,"
           +
           "fbbal number(19,4) DEFAULT 0 NOT NULL ," +
           "fsl number(21,6) DEFAULT 0 NOT NULL ,"
           + "fbsl number(21,6) DEFAULT 0 NOT NULL ," +
           "fdj DECIMAL(18,4) DEFAULT 0 NOT NULL ,"
           + "fdate date NOT NULL ,fywdate date NOT NULL ," + 
           "ffjzs number(5) DEFAULT 0 NOT NULL ,fzdr varchar2(20) DEFAULT ' ' NOT NULL ,"
           + "fcheckr varchar2(20) DEFAULT ' ' NOT NULL ,fxgr varchar2(20) DEFAULT ' ' NOT NULL ,"
           +
           "fgzr varchar2(20) DEFAULT ' ' NOT NULL ,fgzbz number(2) DEFAULT 0 NOT NULL ,"
           + "fpzly varchar2(20) DEFAULT 'HD' NOT NULL ,fzqjyfs varchar2(20) DEFAULT ' ' NOT NULL ,"
           +
           "fmemo varchar2(1) DEFAULT ' ' NOT NULL ,fnumid number(10) DEFAULT 0 NOT NULL ,"
           + "fcashid varchar2(30) DEFAULT ' ' NOT NULL ,fpz1 varchar2(30) DEFAULT ' ' NOT NULL ,"
           +
           "fpz2 varchar2(30) DEFAULT ' ' NOT NULL, ffromset number(5) default 0,"
           //Jw20060208保险不用这个字段+ "ffromPZBH number(10) default 0," //来源的凭证号
           + " ftolevel number(3) default 0, fupload number(3) default 0 ,FAuxiAcc varchar2(100) DEFAULT ' ' NOT NULL," +
           	" FConfirmer varchar2(20),FVCHNUMRELA VARCHAR2(20), primary key (fterm,fvchpdh,fvchbh,FAuxiAcc)";
           //bug5646 modified by yeshenghong 20120913
       sSql[1][1] =
             "FAcctCode varchar2(50) NOT NULL ,FAcctName varchar2(50) NOT NULL ,"
             +
             "FAcctLevel number(3) NOT NULL ,FAcctParent varchar2(50) NOT NULL ,"
             +
             "FAcctDetail number(3) NOT NULL ,FAcctClass varchar2(50) NOT NULL ,"
             +
             "FAcctAttr varchar2(100) NOT NULL ,FAcctAttrID varchar2(20) NOT NULL ,"
             + "FCurCode varchar2(3) NOT NULL ,FBalDC number(5) NOT NULL ,"
             + "FAmount number(3) NOT NULL ,FCarryAcc number(3) NOT NULL ,"
             + "FEName varchar2(50) NOT NULL ,FBy varchar2(30) NOT NULL ,FAuxiAcc varchar2(100) DEFAULT ' ' NOT NULL ," +
             //edit by songjie 2013.01.22 组合设置点击创建复制报错，由于财务系统科目表少字段导致，现已添加 FGVSTAT
         	 " FPORTCLSCODE varchar2(20) DEFAULT ' ',FGVSTAT CHAR(1), primary key(FACCTCODE)";
       		//bug5646 modified by yeshenghong 20120913
       sSql[2][1] =
             "FCurCode varchar2(3) NOT NULL primary key ,FCurName varchar2(50) NOT NULL ,"
             + "FCurRate DECIMAL(18,4) NOT NULL ,FIsBase number(3) NOT NULL";
       sSql[3][1] =
             "FMonth number(3) NOT NULL primary key ,FCloser varchar2(50) NOT NULL ,"
             + "FTime date NOT NULL";

       sSql[4][1] =
             "FTabNum number(5) NOT NULL primary key ,FTabName varchar2(50) NOT NULL ,"
             + "FRows number(5) NOT NULL ,FCols number(5) NOT NULL ,"
             + "FFRows number(3) NOT NULL ,FFCols number(3) NOT NULL ,"
             +
             "FRCSize VARCHAR2(2000) NOT NULL ,FBalFmt varchar2(500) NOT NULL ,"
             + "FAuthor varchar2(50) NOT NULL ,FTabType number(3) NOT NULL ,"
             +
             "FDescription varchar2(1000) NOT NULL ,FMerge varchar2(1000) NOT NULL ,"
             + "FPrint varchar2(200) NOT NULL";

       sSql[5][1] = "FTabNum number(5) NOT NULL ,FRow number(5) NOT NULL ,"
             //2008.10.29 蒋锦 修改 将 FContent 字段加长为 4000 BUG：0000482
             + "FCol number(5) NOT NULL ,FContent varchar2(4000) NOT NULL ,"
             + "FLLine number(3) NOT NULL ,"
             + "FTLine number(3) NOT NULL ,FRLine number(3) NOT NULL ,"
             + "FBLine number(3) NOT NULL ,FLColor number(10) NOT NULL ,"
             + "FTColor number(10) NOT NULL ,FRColor number(10) NOT NULL ,"
             + "FBColor number(10) NOT NULL ,FBackColor number(10) NOT NULL ,"
             +
             "FForeColor number(10) NOT NULL ,FFontName varchar2(50) NOT NULL ,"
             + "FFontSize DECIMAL(18,4) NOT NULL ,FFontStyle number(3) NOT NULL ,"
             + "FDataType number(3) NOT NULL ,FFormat varchar2(99) NOT NULL ,"
             + "primary key (FTabNum,FRow,FCol)";

       sSql[6][1] = "FTabNum number(5) NOT NULL ,FNum number(5) NOT NULL ,"
             +
             "FDescription varchar2(50) NOT NULL ,FSDate varchar2(50) NOT NULL ,"
             +
             "FEDate varchar2(50) NOT NULL ,FCurUnit varchar2(50) NOT NULL ,"
             + "FCurRatio DECIMAL(18,4) NOT NULL ,primary key (FTabNum,FNum)";

       sSql[7][1] = "FTabNum number(5) NOT NULL ,FNum number(5) NOT NULL ,"
             + "FRow number(5) NOT NULL ,FCol number(5) NOT NULL ,"
             +
             "FData varchar2(50) NOT NULL, primary key (FTabNum,FNum,FRow,FCol)";

       sSql[8][1] = "FMonth number(3) NOT NULL ,FAcctCode varchar2(50) NOT NULL ,"
           + "FCurCode varchar2(3) NOT NULL ,"
           + "FStartBal number(19,4) DEFAULT 0 NOT NULL,FDebit number(19,4) DEFAULT 0 NOT NULL,"
           + "FCredit number(19,4) DEFAULT 0 NOT NULL,FAccDebit number(19,4) DEFAULT 0 NOT NULL,"
           + "FAccCredit number(19,4) DEFAULT 0 NOT NULL,FEndBal number(19,4) DEFAULT 0 NOT NULL,"
           + "FBStartBal number(19,4) DEFAULT 0 NOT NULL,FBDebit number(19,4) DEFAULT 0 NOT NULL,"
           + "FBCredit number(19,4) DEFAULT 0 NOT NULL,FBAccDebit number(19,4) DEFAULT 0 NOT NULL,"
           + "FBAccCredit number(19,4) DEFAULT 0 NOT NULL,FBEndBal number(19,4) DEFAULT 0 NOT NULL,"
           + "FAStartBal number(19,4) DEFAULT 0 NOT NULL,FADebit number(19,4) DEFAULT 0 NOT NULL,"
           + "FACredit number(19,4) DEFAULT 0 NOT NULL,FAAccDebit number(19,4) DEFAULT 0 NOT NULL,"
           + "FAAccCredit number(19,4) DEFAULT 0 NOT NULL,FAEndBal number(19,4) DEFAULT 0 NOT NULL,"
           +
           "FIsDetail number(3) NOT NULL,FAuxiAcc VARCHAR2(100)  NOT NULL,primary key (FMonth,FAcctCode," +
           "FCurCode,FAuxiAcc)";


       sSql[9][1] = "FDate DATE NOT NULL ," //yjx 2005-11-15
             + "FAcctCode varchar2 (50)  ,"
             + "FYwLx varchar2 (10)  NOT NULL ,"
             + "FSl decimal(18, 4) ,"
             + "FJe decimal(18, 4) ,"
             + "FJyLy varchar2 (10)  NOT NULL, "
             + "FId decimal(18, 4) NOT NULL,"
             + "primary key (Fid)";

       sSql[10][1] = "Fdate DATE NOT NULL ,"
             + "Fpzkmh VARCHAR2 (50) NOT NULL ,"
             + "Fbal decimal(18, 4) NOT NULL ,"
             + "Fbbal decimal(18, 4) NOT NULL ,"
             + "Ftype decimal(18, 4) NOT NULL,"
             + "Fpzjyfs VARCHAR2 (50)  NOT NULL";

     //BUG3178博时库进行年终结转报错  add by jiangshichao 2011.11.19 调整辅助核算项代码和名称长度
       sSql[11][1] = "AuxiAccID VARCHAR2 (50) NOT NULL ,"
             + "AuxiAccName VARCHAR2 (100) NOT NULL ,"
             + "Remark  VARCHAR2 (50) NOT NULL ,"
             + "Primary Key (AuxiAccID)";

       return sSql;
    }
    
    /**
     * 同时年份套账号前缀的数据库表清单
     * @return String[][]
     */
    public static String[][] getYearsetTables() {
       String tabYearSet[][] = new String[12][2];
       //Jw20060411"LBalanceInOnt"; //成本余额表改为只带套账号
       //财务系统表
       tabYearSet[0][0] = "fcwvch";
       tabYearSet[1][0] = "LAccount";
       tabYearSet[2][0] = "LCurrency";
       tabYearSet[3][0] = "LClose";
       tabYearSet[4][0] = "LTabList";
       tabYearSet[5][0] = "LTabCell";
       tabYearSet[6][0] = "LTabDataList";
       tabYearSet[7][0] = "LTabData";
       tabYearSet[8][0] = "LBalance";
       tabYearSet[9][0] = "LGzMsMxb"; //新增国债免税明细表
       tabYearSet[10][0] = "GzVch";
       tabYearSet[11][0] = "AuxiAccSet";
       return tabYearSet;
    }
    
    
    /**
     * 创建系统数据表格，根据传入的组合代码来建立
     * @param strPrefix String
     * @param bRollback boolean
     * @throws YssException
     * @return boolean
     */
    public boolean OraCreateSetTables(String strPrefix, boolean bRollback) throws
        YssException {
        String sPrefix = "_" + strPrefix; //只有套帐号的或者年份的
        String[][] sSql = new String[87][2];
        boolean bVchExist = false, bTmp = false, bTmp1 = true;
        int iLoop;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        int j = 0;

        try {
            conn.setAutoCommit(false);
            for (int i = 0; i < sSql.length; i++) {
                if (OraTableExist(sSql[i][0])) {
                    OraRemoveTable(sSql[i][0]);
                    bTmp1 = true;
                } else {
                    bTmp1 = true;
                }
                if (bTmp1) {
                    dbl.executeSql("Create Table " + sSql[i][0] + "(" + sSql[i][1] +
                                   ")");
                }
                System.out.println("num===" + i + ";name===:" + sSql[i][0]);
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (Exception sqle) {
            throw new YssException("创建系统表出错！", sqle);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * createGroupTables
     *
     * @param sPre String
     */
    public void createGroupTables(String sPre) {
    }

    /**
     * updateGroupOnly
     *
     * @param sPre String
     */
    public void updateGroupOnly(String sPre) throws YssException {
        String sVersion = "1.0.0.0000";
        String strSql = "";
        try {

            OraTable ora = new OraTable(dbl);
            ora.setYssPub(pub);
            sVersion = ora.getVersion(sPre);
            if (sVersion.compareTo("1.0.0.0001") < 0) {
                ora.updateVersion1A0A0A0001(sPre);
            }
            if (sVersion.compareTo("1.0.0.0002") < 0) {
                ora.updateVersion1A0A0A0002(sPre);
            }
            if (sVersion.compareTo("1.0.0.0003") < 0) {
                ora.updateVersion1A0A0A0003(sPre);
            }
            if (sVersion.compareTo("1.0.0.0004") < 0) {
                ora.updateVersion1A0A0A0004(sPre);
            }

        } catch (Exception e) {
            throw new YssException("调整表结构失败，请重新登陆！", e);
        }
    }

    /**
     * updateCommon
     */
    public void updateCommon() throws YssException {
        ResultSet Rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = null;
        try {
            if (!dbl.yssTableExist("TB_FUN_VERSION")) {
                strSql = "CREATE TABLE TB_FUN_VERSION (" +
                    "FAssetGroupCode VARCHAR2(20)  NOT NULL," +
                    "FVERNUM     VARCHAR2(50)  NOT NULL," +
                    "FISSUEDATE  DATE          NOT NULL," +
                    "FDesc       VARCHAR2(1000)     NULL," +
                    "FCreateDate DATE          NOT NULL," +
                    "FCreateTime VARCHAR2(20)  NOT NULL," +
                    "CONSTRAINT PK_TB_FUN_VERSION " +
                    "PRIMARY KEY (FAssetGroupCode,FVERNUM))";
                dbl.executeSql(strSql);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("调整表结构失败，请重新登陆！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * createCommonTables
     */
    public void createCommonTables() {
    }
}
