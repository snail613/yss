package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class SectorClassBean
    extends BaseDataSettingBean implements IDataSetting {

    private String secClsCode; //板块分类代码
    private String secClsName; //板块分类名称
    private String parentCode; //上级代码
    private String sectorCode; //上级名称
    private int orderCode; //排序号
    private String desc; //描述
    private int isAuditSubNode;
    private String oldSecClsCode;
    private int oldOrderCode;
    private java.util.Date startDate; //启用日期
    private java.util.Date oldStartDate;
    private SectorClassBean filterType;

    public SectorClassBean() {
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String tmpAry[] = null;
        try {
        	//edit by yanghaiming 20100426 B股业务 增加板块分类
        	if (sRowStr.trim().length() == 0) {
                return;
            }
            tmpAry = sRowStr.split("\t");
            this.secClsCode = tmpAry[0];
            this.secClsName = tmpAry[1];
            this.parentCode = tmpAry[2];
            this.orderCode = Integer.parseInt(tmpAry[3]);
            this.desc = tmpAry[4];
            super.checkStateId = Integer.parseInt(tmpAry[5]);
            this.sectorCode = tmpAry[6];
            this.startDate = YssFun.toDate(tmpAry[7]);
            this.oldSecClsCode = tmpAry[8];
            this.oldOrderCode = Integer.parseInt(tmpAry[9]);
            this.oldStartDate = YssFun.toDate(tmpAry[10]);
            this.isAuditSubNode = Integer.parseInt(tmpAry[11]);
            super.parseRecLog();
        } catch (Exception e) {
            throw new YssException("解析板块分类设置出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.secClsCode.trim());
        buf.append("\t");
        buf.append(this.secClsName.trim());
        buf.append("\t");
        buf.append(this.parentCode.trim());
        buf.append("\t");
        buf.append(this.orderCode);
        buf.append("\t");
        buf.append(this.sectorCode.trim());
        buf.append("\t");
        buf.append(YssFun.formatDate(this.startDate, YssCons.YSS_DATEFORMAT));
        buf.append("\t");
        buf.append(this.desc);
        buf.append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        String strSql = "";
        String tmpValue = "";
        //------ 不再使用启用日期 modify by wangzuochun  2010.09.06  MS01602    板块分类设置，新建板块分类代码相同、启用日期不同的数据报错    QDV4赢时胜(测试)2010年08月12日03_B 
        //---edit by songjie 2012.05.03 BUG 4396 QDV4赢时胜(上海)2012年04月26日05_B start---//
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_SectorClass"), "FSectorCode,FSecClsCode,FStartDate",
                               this.sectorCode + "," + this.secClsCode + "," + YssFun.formatDate(this.startDate,"yyyy-MM-dd"),
                               this.sectorCode + "," + this.oldSecClsCode + "," + YssFun.formatDate(this.oldStartDate,"yyyy-MM-dd"));
        //------------------------------------------MS01602------------------------------------------//
        //---edit by songjie 2012.05.03 BUG 4396 QDV4赢时胜(上海)2012年04月26日05_B end---//
        
        if (this.oldOrderCode != this.orderCode) {
            strSql = "select fordercode from " +
                pub.yssGetTableName("Tb_Para_SectorClass") +
                " where fordercode = '" +
                dbFun.treeBuildOrderCode(pub.yssGetTableName(
                    "Tb_Para_SectorClass"), "FSecClsCode", this.parentCode,
                                         this.orderCode) + "'" +
                " and FParentCode=" + dbl.sqlString(parentCode) + " and FSecClsCode=" + dbl.sqlString(secClsCode); //by liyu
            tmpValue = dbFun.GetValuebySql(strSql);
            if (tmpValue.trim().length() > 0) {
                throw new YssException("菜单排序号【" + this.orderCode +
                                       "】已被【" + tmpValue + "】占用，请重新输入菜单排序号");
            }
        }

    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() {
        return "";
    }

    /**
     * getListViewData2
     *获取已审核的板块分类设置
     * @return String
     */
    public String getListViewData2() throws YssException{
    	String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "板块分类代码\t板块分类名称名称";
            strSql =
                "select a.*, b.FSectorName as FSectorName from " + pub.yssGetTableName("TB_PARA_SECTORCLASS") + " a " +
                " left join (select FSectorCode, FSectorName from " + pub.yssGetTableName("Tb_Para_Sector") + ") b on a.FSectorCode = b.FSectorCode" +
                ( (buildFilterSql().length() > 0) ?
                 buildFilterSql() + " and " : " where ") +
                " a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecClsCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FSecClsName") + "").trim()).
                    append(YssCons.YSS_LINESPLITMARK);

                this.secClsCode = rs.getString("FSecClsCode") + "";
                this.secClsName = rs.getString("FSecClsName") + "";
                this.sectorCode = rs.getString("FSectorCode") + "";
                this.parentCode = rs.getString("FParentCode") + "";
                this.desc = rs.getString("FDesc") + "";
                this.orderCode =  Integer.parseInt(YssFun.right(rs.getString("FOrderCode").replaceAll("'", ""), 3));
                this.startDate = rs.getDate("FStartDate");
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
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
            throw new YssException("获取板块分类设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        String sResult = "";
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Para_SectorClass") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where FSectorCode=" + dbl.sqlString(sectorCode) + "order by a.FOrderCode"; //2008-6-11 单亮 去掉了a.FCheckState <> 2 and 如果有，则在回收站子结点查询不出来
            rs = dbl.openResultSet(strSql); //应加上板块代码 by ly 080302
            while (rs.next()) {
                setSectorClassAttr(rs);
                buf.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sResult = buf.toString().substring(0, buf.toString().length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取板块分类信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    /*public void saveSetting(byte btOper) throws YssException {
       String strSql = "";
       boolean bTrans = false; //代表是否开始了事务
       Connection conn = dbl.loadConnection();
       String strOrderCode = dbFun.treeBuildOrderCode(pub.yssGetTableName("Tb_Para_SectorClass"),"FSecClsCode",this.parentCode,this.orderCode);
       try
       {
          conn.setAutoCommit(false);
          bTrans = true;
          if(btOper == YssCons.OP_ADD)
          {
             strSql = "insert into " + pub.yssGetTableName("Tb_Para_SectorClass") + "" +
                   " (FSecClsCode,FSecClsName,FParentCode,FSectorCode,FOrderCode,FStartDate,FDesc," +
                   "FCheckState,FCreator,FCreateTime)" +
                   "values(" + dbl.sqlString(this.secClsCode) + "," +
                   dbl.sqlString(this.secClsName) + "," +
                   dbl.sqlString(this.parentCode) + "," +
                   dbl.sqlString(this.sectorCode) + "," +
                   dbl.sqlString(strOrderCode) + "," +
                   dbl.sqlDate(this.startDate) + "," +
                   dbl.sqlString(this.desc) + "," +
                   this.checkStateId + "," +
                   dbl.sqlString(this.creatorCode) + "," +
                   dbl.sqlString(this.creatorTime) + ")";
          }
          else if(btOper == YssCons.OP_EDIT)
          {
             if(this.orderCode != this.oldOrderCode) {
                dbFun.treeAdjustOrder( pub.yssGetTableName("Tb_Para_SectorClass"),"FSecClsCode",this.oldSecClsCode,this.orderCode);
             }
             dbFun.treeAdjustParentCode(pub.yssGetTableName("Tb_Para_SectorClass"),"FParentCode",this.oldSecClsCode,this.secClsCode);

             strSql = "update " + pub.yssGetTableName("Tb_Para_SectorClass") + " set FSecClsCode = " +
                   dbl.sqlString(this.secClsCode) + ",FSecClsName = " +
                   dbl.sqlString(this.secClsName) + ",FParentCode = " +
                   dbl.sqlString(this.parentCode) + ",FSectorCode = " +
                   dbl.sqlString(this.sectorCode) + ",FOrderCode = " +
                   dbl.sqlString(strOrderCode) + ",FStartDate = " +
                   dbl.sqlDate(this.startDate) + ",FDesc = " +
                   dbl.sqlString(this.desc) +
                   " where FSecClsCode =" +
                   dbl.sqlString(this.oldSecClsCode) +
                   " and FStartDate = " +
                   dbl.sqlDate(this.oldStartDate);
          }
          else if(btOper == YssCons.OP_DEL)
          {
          strSql = "update " + pub.yssGetTableName("Tb_Para_SectorClass") + " set FCheckState = " +
                   this.checkStateId + ", FCheckUser = " +
                   dbl.sqlString(pub.getUserCode()) +
                   ", FCheckTime = '" +
                   YssFun.formatDatetime(new java.util.Date()) + "'" +
                   " where FSecClsCode = " +
                   dbl.sqlString(this.secClsCode) + " and FStartDate=" +
                   dbl.sqlDate(this.startDate);

          }
          else if(btOper == YssCons.OP_AUDIT)
          {
          // strSql = "update " + pub.yssGetTableName("Tb_Para_SectorClass") + " set FCheckState = " +
              //     this.checkStateId + " where FSecClsCode = " +
              //     dbl.sqlString(this.secClsCode) + " and FStartDate = " +
              //     dbl.sqlDate(this.startDate);
             System.out.println("sunny"+strSql);
             //对节点本身与所有子节点都进行审核
             strSql = "update " + pub.yssGetTableName("Tb_Para_SectorClass") + " set FCheckState = " +
                   this.checkStateId;
                if (this.checkStateId == 1) {
                   strSql += ", FCheckUser = '" + pub.getUserCode() +
                      "', FCheckTime = '" +
                      YssFun.formatDatetime(new java.util.Date()) + "'";
                   if (this.isAuditSubNode == 1) {
                      strSql += " where FOrderCode like '" +
                         strOrderCode + "%'";
                   }
                   else{
                      strSql += " where FOrderCode = '" +
                         strOrderCode + "'";
                   }
                }
                else {
                   strSql += " where FOrderCode like '" +
                         strOrderCode + "%'";
                }

             //审核时，对所有父节点也进行审核
               if (this.checkStateId == 1 && strOrderCode.length() > 3){
                 strOrderCode = strOrderCode.substring(0,strOrderCode.length()-3);
                 String strParentCodeList = dbl.sqlString(strOrderCode);
                 while (strOrderCode.length() > 3){
                   strParentCodeList += ",";
                   strOrderCode = strOrderCode.substring(0,strOrderCode.length()-3);
                   strParentCodeList += dbl.sqlString(strOrderCode);
                 }
                 strSql += " OR FOrderCode in (" + strParentCodeList + ")";
               }

             }
           //修改子节点
           if (btOper == YssCons.OP_EDIT) {
              if (this.orderCode != this.oldOrderCode) {
                 dbFun.treeAdjustOrder(pub.yssGetTableName("Tb_Para_SectorClass"), "fsecclscode",
                                   this.oldSecClsCode, this.orderCode);
              }
              dbFun.treeAdjustParentCode(pub.yssGetTableName("Tb_Para_SectorClass"),"fparentcode",this.oldSecClsCode ,this.secClsCode);
              dbFun.treeChangeParentCode(pub.yssGetTableName("Tb_Para_SectorClass"),"fsecclscode",this.oldSecClsCode,this.parentCode);

          }
          conn.setAutoCommit(false);
          bTrans = true;
          dbl.executeSql(strSql);
          conn.commit();
          bTrans = false;
          conn.setAutoCommit(true);
       }
       catch(Exception e)
       {
          dbl.endTransFinal(conn,bTrans);
          throw new YssException("更新板块分类信息出错",e);
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
        String strOrderCode = dbFun.treeBuildOrderCode(pub.yssGetTableName("Tb_Para_SectorClass"), "FSecClsCode", this.parentCode, this.orderCode);
        try {
            strSql = "select FSecClsCode,FStartDate,FSectorCode,FParentCode,FOrderCode from " + pub.yssGetTableName("Tb_Para_SectorClass") +
                " where FStartDate=" + dbl.sqlDate(startDate) +
                " and FSectorCode =" + dbl.sqlString(sectorCode) + " and FParentCode=" + dbl.sqlString(parentCode) +
                " and FOrderCode=" + dbl.sqlString(strOrderCode);
            ResultSet rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                strSql = "select distinct(FOrderCode) from " + pub.yssGetTableName("Tb_Para_SectorClass") + 
                //edit by songjie 2012.05.03 BUG 4396 QDV4赢时胜(上海)2012年04月26日05_B
				" where FSecClsCode = " + dbl.sqlString(parentCode);
                ResultSet rs1 = dbl.openResultSet(strSql);
                String sParentOrder = "";
                while (rs1.next()) {
                    sParentOrder = rs1.getString("FOrderCode");
                }
                strSql = "select max(FOrderCode) as FOrderCode from " + pub.yssGetTableName("Tb_Para_SectorClass") +
                    " where FStartDate= " + dbl.sqlDate(startDate) +
                    " and FSectorCode = " + dbl.sqlString(sectorCode) +
                    " and FParentCode= " + dbl.sqlString(parentCode);
                rs1 = dbl.openResultSet(strSql);
                while (rs1.next()) {
                    strOrderCode = rs1.getString("FOrderCode");
                    strOrderCode = sParentOrder +
                        YssFun.formatNumber(YssFun.toInt(YssFun.right(strOrderCode, strOrderCode.length() - sParentOrder.length())) + 1, "000");
                }
            } //如果FOrderCode存在就取最大值的下一个OrderCode by ly 080302
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_SectorClass") + "" +
                " (FSecClsCode,FSecClsName,FParentCode,FSectorCode,FOrderCode,FStartDate,FDesc," +
                "FCheckState,FCreator,FCreateTime)" +
                "values(" + dbl.sqlString(this.secClsCode) + "," +
                dbl.sqlString(this.secClsName) + "," +
                dbl.sqlString(this.parentCode) + "," +
                dbl.sqlString(this.sectorCode) + "," +
                dbl.sqlString(strOrderCode) + "," +
                dbl.sqlDate(this.startDate) + "," +
                dbl.sqlString(this.desc) + "," +
                this.checkStateId + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加板块分类信息出错", e);
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
            String strOrderCode = dbFun.treeBuildOrderCode(pub.yssGetTableName("Tb_Para_SectorClass"), "FSecClsCode", this.parentCode, this.orderCode);
            if (this.orderCode != this.oldOrderCode) {
                strSql = "select FSecClsCode,FStartDate,FSectorCode,FParentCode,FOrderCode from " + pub.yssGetTableName("Tb_Para_SectorClass") +
                    " where FStartDate=" + dbl.sqlDate(oldStartDate) +
                    " and FSectorCode =" + dbl.sqlString(sectorCode) + " and FParentCode=" + dbl.sqlString(parentCode) +
                    " and FOrderCode =" + dbl.sqlString(strOrderCode);
                ResultSet rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    throw new SQLException("此序号已被占用,请输入其他序号!"); //添加判断,如果序号已被占用 by ly 080302
                }
                dbFun.treeAdjustOrder(pub.yssGetTableName("Tb_Para_SectorClass"),
                                      "FSecClsCode", this.oldSecClsCode,
                                      this.orderCode);
            }
            dbFun.treeChangeParentCode(pub.yssGetTableName("Tb_Para_SectorClass"),
                                       "fsecclscode", this.oldSecClsCode,
                                       this.parentCode);
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_SectorClass") + " set FSecClsCode = " +
                dbl.sqlString(this.secClsCode) + ",FSecClsName = " +
                dbl.sqlString(this.secClsName) + ",FParentCode = " +
                dbl.sqlString(this.parentCode) + ",FSectorCode = " +
                dbl.sqlString(this.sectorCode) + ",FOrderCode = " +
                dbl.sqlString(strOrderCode) + ",FStartDate = " +
                dbl.sqlDate(this.startDate) + ",FDesc = " +
                dbl.sqlString(this.desc) +
                " where FSecClsCode =" +
                dbl.sqlString(this.oldSecClsCode) +
                " and FStartDate = " +
                dbl.sqlDate(this.oldStartDate);
            dbl.executeSql(strSql);
            dbFun.treeAdjustParentCode(pub.yssGetTableName("Tb_Para_SectorClass"),
                                       "FParentCode", this.oldSecClsCode,
                                       this.secClsCode);

            /*if (this.orderCode != this.oldOrderCode) {
                        dbFun.treeAdjustOrder(pub.yssGetTableName("Tb_Para_SectorClass"), "fsecclscode",
                                          this.oldSecClsCode, this.orderCode);
                     }
                     dbFun.treeAdjustParentCode(pub.yssGetTableName("Tb_Para_SectorClass"),"fparentcode",this.oldSecClsCode ,this.secClsCode);*/
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (SQLException se) {
            throw new YssException(se.getMessage());
        } catch (Exception e) {
            throw new YssException("修改板块分类信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	//------ 此处应该完全删除而不是改变审核状态  modify by wangzuochun 2010.09.06  MS01602    板块分类设置，新建板块分类代码相同、启用日期不同的数据报错    QDV4赢时胜(测试)2010年08月12日03_B 
            /*strSql = "update " + pub.yssGetTableName("Tb_Para_SectorClass") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FSecClsCode = " +
                dbl.sqlString(this.secClsCode) + " and FStartDate=" +
                dbl.sqlDate(this.startDate);*/
            strSql = "delete from " + pub.yssGetTableName("Tb_Para_SectorClass") +
                  " where FSecClsCode = " +
                  dbl.sqlString(this.secClsCode);
            //--------------------------------MS01602---------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除板块分类信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            String strOrderCode = dbFun.treeBuildOrderCode(pub.yssGetTableName("Tb_Para_SectorClass"), "FSecClsCode", this.parentCode, this.orderCode);
            strSql = "update " + pub.yssGetTableName("Tb_Para_SectorClass") + " set FCheckState = " +
                this.checkStateId;
            if (this.checkStateId == 1) {
                strSql += ", FCheckUser = '" + pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'";
            }
            if (this.isAuditSubNode == 1) {
                strSql += " where FOrderCode like '" +
                    strOrderCode + "%'" + " and FCheckState <> 2 "; //--单亮--2008.4.8--添加了and FCheckState <> 2，如果不添加当审核的有子节点的节点的时候，会把删除状态的节点修改为以审核
            } else {
                strSql += " where FOrderCode = '" +
                    strOrderCode + "'";
            }

            /* if (this.isAuditSubNode == 1) {
                strSql += " where FOrderCode like '" +
                   strOrderCode + "%'";
             }
             else{
                strSql += " where FOrderCode = '" +
                   strOrderCode + "'";
             }
                         }
                         else {
             strSql += " where FOrderCode like '" +
                   strOrderCode + "%'";
                         }*/
//this.checkStateId == 1 &&
            //审核时，对所有父节点也进行审核
            if (strOrderCode.length() > 3) {
                strOrderCode = strOrderCode.substring(0, strOrderCode.length() - 3);
                String strParentCodeList = dbl.sqlString(strOrderCode);
                while (strOrderCode.length() > 3) {
                    strParentCodeList += ",";
                    strOrderCode = strOrderCode.substring(0, strOrderCode.length() - 3);
                    strParentCodeList += dbl.sqlString(strOrderCode);
                }
                strSql += " OR FOrderCode in (" + strParentCodeList + ")";
            }

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核板块分类信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void setSectorClassAttr(ResultSet rs) throws SQLException {
        this.secClsCode = rs.getString("FSecClsCode");
        this.secClsName = rs.getString("FSecClsName");
        this.parentCode = rs.getString("FParentCode");
        this.orderCode = Integer.parseInt(YssFun.right(rs.getString("FOrderCode"), 3));
        this.sectorCode = rs.getString("FSectorCode");
        this.startDate = rs.getDate("FStartDate");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);

    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
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
        SectorClassBean befEditBean = new SectorClassBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Para_SectorClass") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  a.FSecClsCode =" + dbl.sqlString(this.oldSecClsCode) +
                " order by a.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.secClsCode = rs.getString("FSecClsCode");
                befEditBean.secClsName = rs.getString("FSecClsName");
                befEditBean.parentCode = rs.getString("FParentCode");
                befEditBean.orderCode = Integer.parseInt(YssFun.right(rs.getString("FOrderCode"), 3));
                befEditBean.sectorCode = rs.getString("FSectorCode");
                befEditBean.startDate = rs.getDate("FStartDate");
                befEditBean.desc = rs.getString("FDesc");

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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
    
    /**
     * buildFilterSql
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        //edit by songjie 2011.10.18 BUG 2785 QDV4招商基金2011年09月15日03_B
        if (this.sectorCode != null && this.sectorCode.length()>0) {
            sResult = sResult + " where a.FSectorCode = '" +
                this.sectorCode.replaceAll("'", "''") + "'";

        }
        return sResult;
    }
}
