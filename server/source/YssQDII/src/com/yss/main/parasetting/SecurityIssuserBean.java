package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class SecurityIssuserBean
    extends BaseDataSettingBean implements IDataSetting {
    private String securityIssuserCode; //发行人代码
    private String securityIssuserName; //发行人名称

    private String ArtPerson; //法人代表
    private String CapitalCuryCode; //币种代码
    private double RegCapital = 0.0; //注册资金
    private String RegAddr; // 注册地址
    private String CapitalCuryName; //币种名称
    private String desc; //描述
    private String parentCode = ""; //母公司代码
    private String parentName = ""; //母公司民称
    private String oldSecIssuerCode;
    private String sRecycled = "";
    private String sOrgCodeType = ""; //机构代码类型  2008-6-3 单亮
    private String sOrgCode = ""; //机构代码  2008-6-3 单亮
   
    //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao start ---// 
    private String sManagerOrg = "";
    private String sCreditRating = "";
   //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao end ---//
    
    private java.util.Date startDate;
    private java.util.Date oldStartDate;
    private SecurityIssuserBean filterType;
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public String getParentName() {
        return parentName;
    }

    public SecurityIssuserBean() {

    }

    
    
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.securityIssuserCode = reqAry[0];
            this.securityIssuserName = reqAry[1];

            this.ArtPerson = reqAry[2];
            this.CapitalCuryCode = reqAry[3];

            this.RegCapital = YssFun.toDouble(reqAry[4]);
            this.RegAddr = reqAry[5];
            this.desc = reqAry[6];
            this.parentCode = reqAry[7];
            this.parentName = reqAry[8];
            super.checkStateId = Integer.parseInt(reqAry[9]);
            this.oldSecIssuerCode = reqAry[10];
            this.startDate = YssFun.toDate(reqAry[11]);
            this.oldStartDate = YssFun.toDate(reqAry[12]);

            this.sOrgCodeType = reqAry[13]; //机构代码类型  2008-6-3  单亮
            this.sOrgCode = reqAry[14]; //机构代码  2008-6-3  单亮
          //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao start ---// 
            this.sCreditRating = reqAry[15];
            this.sManagerOrg = reqAry[16];
           //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao end ---//
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SecurityIssuserBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析席发行人信息出错", e);
        }

    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.securityIssuserCode).append("\t");
        buf.append(this.securityIssuserName).append("\t");

        buf.append(this.ArtPerson).append("\t");
        buf.append(this.CapitalCuryCode).append("\t");
        buf.append(this.CapitalCuryName).append("\t");
        buf.append(this.RegCapital).append("\t");
        buf.append(this.RegAddr).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.parentCode).append("\t");
        buf.append(this.parentName).append("\t");
        buf.append(YssFun.formatDate(this.startDate)).append("\t");
        buf.append(this.sOrgCode).append("\t"); //机构代码类型 2008-6-3 单亮
        buf.append(this.sOrgCodeType).append("\t"); //机构代码 2008-6-3 单亮
        //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao start ---//
        buf.append(this.sCreditRating).append("\t");
        buf.append(this.sManagerOrg).append("\t");
        //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao end ---//
        buf.append(super.buildRecLog());

        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
    	//----edit by songjie 2011.03.11 不以启用日期作为查询主键数据的参数----//
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_AffiliatedCorp"), "FAffCorpCode",
                               this.securityIssuserCode,
                               this.oldSecIssuerCode);
        //----edit by songjie 2011.03.11 不以启用日期作为查询主键数据的参数----//

    }

    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */

    /*public void saveSetting(byte btOper) throws YssException {
       Connection conn = dbl.loadConnection();
       boolean bTrans = false;
       String strSql = "";
       try {
          if (btOper == YssCons.OP_ADD) {
             strSql =
                  "insert into " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + "(FAffCorpCode,FAffCorpName, FParentCode,FStartDate,FArtPerson, " +
                   " FCapitalCury, FRegCapital, FRegAddr, FDesc, FCheckState,FCreator,FCreateTime,FCheckUser) " +
                   " values(" + dbl.sqlString(this.securityIssuserCode) + "," +
                   dbl.sqlString(this.securityIssuserName) + "," +
                   dbl.sqlString(this.parentCode) + "," +
                   dbl.sqlDate(this.startDate) + "," +
                   dbl.sqlString(this.ArtPerson) + "," +
                   dbl.sqlString(this.CapitalCuryCode) + "," +
                   this.RegCapital + "," +
                   dbl.sqlString(this.RegAddr) + "," +
                   dbl.sqlString(this.desc) + "," +
                   (pub.getSysCheckState()?"0":"1") + "," +
                   dbl.sqlString(this.creatorCode) + ", " +
                   dbl.sqlString(this.creatorTime) + "," +
                   (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
          }
          else if (btOper == YssCons.OP_EDIT) {
           strSql = "update " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " set FAffCorpCode=" +
                    dbl.sqlString(this.securityIssuserCode) + ",FAffCorpName=" +

                    dbl.sqlString(this.securityIssuserName) + ",FParentCode="+
                   dbl.sqlString(this.parentCode) + ",FStartDate="+
                    dbl.sqlDate(this.startDate) + ",FArtPerson="+
                    dbl.sqlString(this.ArtPerson) + ",FCapitalCury="+
                    dbl.sqlString(this.CapitalCuryCode) + ",FRegCapital=" +
                    this.RegCapital+ ",FRegAddr="+
                    dbl.sqlString(this.RegAddr) + ",FDesc="+
                    dbl.sqlString(this.desc) + ",FCheckState = " +
                    (pub.getSysCheckState()?"0":"1") + ",FCreator=" +
                    dbl.sqlString(this.creatorCode) +",FCreateTime="+
                    dbl.sqlString(this.creatorTime)+ ",FCheckUser = " +
                    (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                    "where FAffCorpCode=" + dbl.sqlString(this.oldSecIssuerCode)+
                    "and FStartDate=" + dbl.sqlDate(this.oldStartDate);
          }

          else if (btOper == YssCons.OP_DEL) {
             strSql = "update " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " set FCheckState = " +
                   this.checkStateId + ", FCheckUser = " +
                   dbl.sqlString(pub.getUserCode()) +
                   ", FCheckTime = '" +
                   YssFun.formatDatetime(new java.util.Date()) + "'" +
                   " where FAffCorpCode = " +
                   dbl.sqlString(this.securityIssuserCode)+ "and FStartDate=" +
                   dbl.sqlDate(this.startDate);

          }
          else if (btOper == YssCons.OP_AUDIT) {

             System.out.println(this.checkStateId);

             strSql = "update " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " set FCheckState = " +
                   this.checkStateId + ", FCheckUser = " +
                   dbl.sqlString(pub.getUserCode()) +
                   ",FCheckTime = '" +
                   YssFun.formatDatetime(new java.util.Date()) + "'" +
                   " where FAffCorpCode="+ dbl.sqlString(this.securityIssuserCode)+ " and "+
                   " FStartDate=" + dbl.sqlDate(this.startDate);

          }
          conn.setAutoCommit(false);
          bTrans = true;
          dbl.executeSql(strSql);
          conn.commit();
          bTrans = false;
          conn.setAutoCommit(true);
       }
       catch (Exception e) {
          throw new YssException("设置发行人信息出错！", e);
       }
       finally {
          dbl.endTransFinal(conn, bTrans);
       }
        }*/
    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + "(FAffCorpCode,FAffCorpName, FParentCode,FStartDate,FArtPerson, " +
                " FCapitalCury, FRegCapital, FRegAddr,FOrgCodeType,FOrgCode, FDesc, FCheckState,FCreator,FCreateTime,FCheckUser,Fcreditrating,FManagerorg) " +
                " values(" + dbl.sqlString(this.securityIssuserCode) + "," +
                dbl.sqlString(this.securityIssuserName) + "," +
                dbl.sqlString(this.parentCode) + "," +
                dbl.sqlDate(this.startDate) + "," +
                dbl.sqlString(this.ArtPerson) + "," +
                dbl.sqlString(this.CapitalCuryCode) + "," +
                this.RegCapital + "," +
                dbl.sqlString(this.RegAddr) + "," +
                dbl.sqlString(this.sOrgCodeType) + "," + //机构代码类型 2008-6-3  单亮
                dbl.sqlString(this.sOrgCode) + "," + //机构代码 2008-6-3  单亮
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + ", " +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +","+
                //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao start ---//
                dbl.sqlString(this.sCreditRating)+","+
                dbl.sqlString(this.sManagerOrg)+")";
                //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao end ---//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加证券发行人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " set FAffCorpCode=" +
                dbl.sqlString(this.securityIssuserCode) + ",FAffCorpName=" +

                dbl.sqlString(this.securityIssuserName) + ",FParentCode=" +
                dbl.sqlString(this.parentCode) + ",FStartDate=" +
                dbl.sqlDate(this.startDate) + ",FArtPerson=" +
                dbl.sqlString(this.ArtPerson) + ",FCapitalCury=" +
                dbl.sqlString(this.CapitalCuryCode) + ",FRegCapital=" +
                this.RegCapital + ",FRegAddr=" +
                dbl.sqlString(this.RegAddr) + ",FDesc=" +
                dbl.sqlString(this.desc) + ",FOrgCodeType=" +
                dbl.sqlString(this.sOrgCodeType) + ",FOrgCode=" + //机构代码类型 2008-6-3 单亮
                dbl.sqlString(this.sOrgCode) + ",FCheckState = " + //机构代码 2008-6-3 单亮
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator=" +
                dbl.sqlString(this.creatorCode) + ",FCreateTime=" +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
              //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao start ---//
                " ,FManagerorg = "+dbl.sqlString(this.sManagerOrg)+
                " ,Fcreditrating = "+dbl.sqlString(this.sCreditRating)+
            //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao end ---//
                
                "where FAffCorpCode=" + dbl.sqlString(this.oldSecIssuerCode) +
                "and FStartDate=" + dbl.sqlDate(this.oldStartDate);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改证券发行人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据即放入数据库
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FAffCorpCode = " +
                dbl.sqlString(this.securityIssuserCode) + "and FStartDate=" +
                dbl.sqlDate(this.startDate);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除证券发行人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 修改时间：2008年3月25号
     * 修改人：单亮
     * 原方法功能：只能处理发行人信息的审核和未审核的单条信息。
     * 新方法功能：可以处理发行人信息审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理发行人信息审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//     String strSql = "";
//     boolean bTrans = false; //代表是否开始了事务
//     Connection conn = dbl.loadConnection();
//     try {
//        strSql = "update " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " set FCheckState = " +
//                  this.checkStateId + ", FCheckUser = " +
//                  dbl.sqlString(pub.getUserCode()) +
//                  ",FCheckTime = '" +
//                  YssFun.formatDatetime(new java.util.Date()) + "'" +
//                  " where FAffCorpCode="+ dbl.sqlString(this.securityIssuserCode)+ " and "+
//                  " FStartDate=" + dbl.sqlDate(this.startDate);
//
//        conn.setAutoCommit(false);
//        bTrans = true;
//        dbl.executeSql(strSql);
//        conn.commit();
//        bTrans = false;
//        conn.setAutoCommit(true);
//     }
//
//     catch (Exception e) {
//        throw new YssException("审核证券发行人信息出错", e);
//     }
//     finally {
//        dbl.endTransFinal(conn, bTrans);
//     }
        //修改后的代码
        //--------------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " +
                        pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FAffCorpCode=" +
                        dbl.sqlString(this.securityIssuserCode) + " and " +
                        " FStartDate=" + dbl.sqlDate(this.startDate);

                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而securityIssuserCode不为空，则按照securityIssuserCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (securityIssuserCode != null && !securityIssuserCode.equalsIgnoreCase("")) {
                strSql = "update " +
                    pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FAffCorpCode=" +
                    dbl.sqlString(this.securityIssuserCode) + " and " +
                    " FStartDate=" + dbl.sqlDate(this.startDate);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核证券发行人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //----------------------end

    }

    /**
     *
     * @param rs ResultSet
     * @throws SQLException
     */




    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    public IDataSetting getSetting() {
        return null;
    }

    public String getAllSetting() {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
        	//---edit by songjie 2012.02.22 BUG 3706 QDV4赢时胜(测试)2012年01月1９日01_B start---//
            sResult = " where 1=1 ";
            if (this.filterType.securityIssuserCode.length() != 0) {
                sResult = sResult + " and a.FAffCorpCode like'" +
                    filterType.securityIssuserCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.securityIssuserName.length() != 0) {
                sResult = sResult + " and a.FAffCorpName like'" +
                    filterType.securityIssuserName.replaceAll("'", "''") + "%'";
            }
            
            //add by guolongchao 20120314 BUG3767 添加筛选条件------start
            if (this.filterType.ArtPerson.length() != 0) {
                sResult = sResult + " and a.FArtPerson like'" +
                    filterType.ArtPerson.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.RegAddr.length() != 0) {
                sResult = sResult + " and a.FRegAddr like'" +
                    filterType.RegAddr.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.parentCode.length() != 0) {
                sResult = sResult + " and a.FParentCode like'" +
                    filterType.parentCode.replaceAll("'", "''") + "%'";
            } 
            if (this.filterType.CapitalCuryCode.length() != 0) {
                sResult = sResult + " and a.FCapitalCury like'" +
                    filterType.CapitalCuryCode.replaceAll("'", "''") + "%'";
            } 
            if (this.filterType.RegCapital!= 0) {
                sResult = sResult + " and a.FRegCapital = " +
                    filterType.RegCapital;
            } 
            //add by guolongchao 20120314 BUG3767 添加筛选条件------end
            
            //delete by songjie 2011.03.11 不以启用日期筛选数据
//            if (this.filterType.startDate != null &&
//                !this.filterType.startDate.equals(YssFun.toDate("9998-12-31"))) {
//                sResult = sResult + " and a.FStartDate <= " +
//                    dbl.sqlDate(filterType.startDate);
//            }
            //delete by songjie 2011.03.11 不以启用日期筛选数据
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like'" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
            //2008-5-27 单亮 在筛选时添加机构类型的筛选条件 being
            if (this.filterType.sOrgCode.length() != 0) {
                sResult = sResult + " and a.FOrgCode like'" +
                    filterType.sOrgCode.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.sOrgCodeType.equalsIgnoreCase("99") && this.filterType.sOrgCodeType.length() != 0) {
            	//fanghaoln 20100126 MS00939 QDV4赢时胜(上海)2010年1月18日2_B 
                sResult = sResult + " and a.FOrgCodeType like'" +
                    filterType.sOrgCodeType.replaceAll("'", "''") + "%'";
                //--------------------------end------MS00939------------------------------
            }
            //end
          //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao start ---//
            if (!this.filterType.sCreditRating.equalsIgnoreCase("99") && this.filterType.sCreditRating.length() != 0) {
                sResult = sResult + " and a.Fcreditrating like'" +
                    filterType.sCreditRating.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.sManagerOrg.equalsIgnoreCase("99") && this.filterType.sManagerOrg.length() != 0) {
                sResult = sResult + " and a.FManagerorg like'" +
                    filterType.sManagerOrg.replaceAll("'", "''") + "%'";
            }
            //---edit by songjie 2012.02.22 BUG 3706 QDV4赢时胜(测试)2012年01月1９日01_B end---//
          //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao end ---//

        }
        return sResult;
    }

    public void setSecrityIssuserAttr(ResultSet rs) throws SQLException {
        this.securityIssuserCode = rs.getString("FAffCorpCode") + "";
        this.securityIssuserName = rs.getString("FAffCorpName") + "";
        this.startDate = rs.getDate("FStartDate");
        this.ArtPerson = rs.getString("FArtPerson") + "";

        this.CapitalCuryCode = rs.getString("FCapitalCury") + "";
        this.CapitalCuryName = rs.getString("FCuryName") + "";
        this.RegCapital = rs.getDouble("FRegCapital");
        this.RegAddr = rs.getString("FRegAddr") + "";
        this.desc = rs.getString("FDesc") + "";
        this.parentCode = rs.getString("FParentCode");
      //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao start ---//
        if(dbl.isFieldExist(rs, "FManagerorg")&& dbl.isFieldExist(rs, "Fcreditrating")){
        	this.sManagerOrg = rs.getString("FManagerorg");
        	this.sCreditRating = rs.getString("Fcreditrating");
        }
      //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao end ---//

        super.setRecLog(rs);
    }

    public String getListViewData2() throws YssException {
        String strSql = "";
        strSql =
            " select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCuryName  from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
            " left join( select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ")d on a.FCapitalCury=d.FCuryCode " +
            buildFilterSql() +
            " where a.fcheckstate =1 order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);

    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        ResultSet rs = null;
        ResultSet rs2 = null;
        String sVocStr = ""; //2008-6-3 单亮 存放词汇
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.securityIssuserCode = rs.getString("FAffCorpCode") + "";
                this.securityIssuserName = rs.getString("FAffCorpName") + "";
                this.startDate = rs.getDate("FStartDate");
                this.ArtPerson = rs.getString("FArtPerson") + "";
                this.CapitalCuryCode = rs.getString("FCapitalCury") + "";
                this.CapitalCuryName = rs.getString("FCuryName") + "";
                this.RegCapital = rs.getDouble("FRegCapital");
                this.RegAddr = rs.getString("FRegAddr") + "";
                this.desc = rs.getString("FDesc") + "";
                this.parentCode = rs.getString("FParentCode") + "";
                this.sOrgCodeType = rs.getString("FOrgCodeType") + ""; //机构代码类型  2008-6-3 单亮
                this.sOrgCode = rs.getString("FOrgCode") + ""; //机构代码  2008-6-3 单亮
                this.sManagerOrg = rs.getString("FManagerorg");
                this.sCreditRating = rs.getString("Fcreditrating");
                String str = "select * from " +
                    pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                    " where faffcorpcode='" + this.parentCode + "'";
                rs2 = dbl.openResultSet(str);
                if (rs2.next()) {
                    this.parentName = rs2.getString("FAffCorpName") + "";
                } else {
                    this.parentName = "";
                }
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                dbl.closeResultSetFinal(rs2);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);

            //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao 
            sVocStr = vocabulary.getVoc(YssCons.YSS_FIX_OrgCodeType+","+YssCons.YSS_FIX_CREDITRATING+","+YssCons.YSS_FIX_MANAGERORG);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取发行人信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs2);
        }
    }

    //-----------------------------------------------------------------

    /**
     * 获取当前发行人数据
     * 此方法已被修改
     * 修改时间：2008年2月25号
     * 修改人：单亮
     * 原方法的功能：查询出发行人信息数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {

        String strSql = "";
        strSql =
            "select y.* from " +
            //----delete by songjie 2011.03.10 不以最大的启用日期查询关联机构设置数据----//
//            "(select FAffCorpCode,FCheckState,max(FStartDate) as FStartDate from " +
//            pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " " +
//            " where FStartDate <= " +
//            dbl.sqlDate(new java.util.Date()) +
//            //add by zhangfa 20100929 MS01814    银行间债券交易界面，交易关联加载了未审核界面的数据    QDV4赢时胜(测试)2010年09月26日02_B  
////          " and  fcheckstate=1 "+//edit by licai 20101119 关联机构设置不能显示未审核的数据
//            //--------------------------------------------------------------------------------------------------------------------
//            //修改前的代码
//            //"and FCheckState <> 2 group by FFeeLinkCode,FCheckState) x join" +
//            //修改后的代码
//            //----------------------------begin
//            " group by FAffCorpCode,FCheckState) x join" +
            //----delete by songjie 2011.03.10 不以最大的启用日期查询关联机构设置数据----//
            //----------------------------end
            " (select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCuryName  from " +
            pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
            " left join( select FCuryCode,FCuryName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            ")d on a.FCapitalCury=d.FCuryCode " +
            buildFilterSql() +
            ") y " +//edit by songjie 2011.03.11 不以最大的启用日期查询关联机构设置数据
            "order by y.FCheckState, y.FCreateTime desc, y.FCheckTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     * 获取所有发行人信息数据
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";

        strSql = " select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FCuryName from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
            " left join(select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ")d on a.FCapitalCury=d.FCuryCode " +
            buildFilterSql() +
            " order by a.FStartDate, a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "关联公司代码\t关联公司名称\t启用日期";
            strSql = "select y.* from " +
            //----delete by songjie 2011.03.11 不以最大的启用日期查询关联机构设置数据----//
//                "(select FAffCorpCode,FCheckState,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FAffCorpCode,FCheckState) x join" +
            //----delete by songjie 2011.03.11 不以最大的启用日期查询关联机构设置数据----//
                //----edit by songjie 2011.03.11 不以最大的启用日期查询关联机构设置数据----//
                " (select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCuryName  from " + 
                " (select * from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " where FCheckState = 1 ) a " +
                //----edit by songjie 2011.03.11 不以最大的启用日期查询关联机构设置数据----//
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join( select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ")d on a.FCapitalCury=d.FCuryCode " +
                buildFilterSql() +
                ") y " +//edit by songjie 2011.03.11 不以最大的启用日期查询关联机构设置数据
                "order by y.FCheckState, y.FCreateTime desc ";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FAffCorpCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FAffCorpName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"))).append(
                    YssCons.YSS_LINESPLITMARK);
                setSecrityIssuserAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);

            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取关联公司信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        SecurityIssuserBean befEditBean = new SecurityIssuserBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询关联机构设置数据----//
//                "(select FAffCorpCode,FCheckState,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState <> 2 group by FAffCorpCode,FCheckState) x join" +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询关联机构设置数据----//
                //----edit by songjie 2011.03.11 不以最大的启用日期查询关联机构设置数据----//
                " (select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCuryName  from " + 
                "(select * from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " where FCheckState <> 2 ) a " +
                //----edit by songjie 2011.03.11 不以最大的启用日期查询关联机构设置数据----//
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join( select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ")d on a.FCapitalCury=d.FCuryCode " +
                " where  a.FAffCorpCode =" + dbl.sqlString(this.oldSecIssuerCode) +
                ") y " +//edit by songjie 2011.03.10 不以最大的启用日期查询关联机构设置数据
                "order by y.FCheckState, y.FCreateTime desc ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.securityIssuserCode = rs.getString("FAffCorpCode") + "";
                befEditBean.securityIssuserName = rs.getString("FAffCorpName") + "";
                befEditBean.startDate = rs.getDate("FStartDate");
                befEditBean.ArtPerson = rs.getString("FArtPerson") + "";

                befEditBean.CapitalCuryCode = rs.getString("FCapitalCury") + "";
                befEditBean.CapitalCuryName = rs.getString("FCuryName") + "";
                befEditBean.RegCapital = rs.getDouble("FRegCapital");
                befEditBean.RegAddr = rs.getString("FRegAddr") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
                befEditBean.parentCode = rs.getString("FParentCode");
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /**
     * 删除回收站的数据，即从数据库彻底删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                        " where FAffCorpCode = " +
                        dbl.sqlString(this.securityIssuserCode) +
                        "and FStartDate=" +
                        dbl.sqlDate(this.startDate);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而securityIssuserCode不为空，则按照securityIssuserCode来执行sql语句
            else if (securityIssuserCode != "" && securityIssuserCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                    " where FAffCorpCode = " +
                    dbl.sqlString(this.securityIssuserCode) +
                    "and FStartDate=" +
                    dbl.sqlDate(this.startDate);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }
}
