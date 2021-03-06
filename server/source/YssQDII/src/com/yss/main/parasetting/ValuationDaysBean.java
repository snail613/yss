package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title:估值日群设置 </p>
 *
 * <p>Description: 用于处理估值日群的增删改查等</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: yss</p>
 * MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A
 * @author panjunfang 20090710
 * @version 1.0
 */
public class ValuationDaysBean
    extends BaseDataSettingBean implements IDataSetting {

    private String strValuationdaysCode;    //估值日群代码
    private String strValuationdaysName;    //估值日群名称
    private String strPortCode;             //组合代码
    private String strPortName;             //组合名称
    private String strDesc;                 //描述
    private String strOldValuationdaysCode;
    private String strOldPortCode;
    private boolean bCopy = false;//判断是否为复制操作，若为复制则同时将估值日群对应的估值日复制
    private ValuationDaysBean filterType;
    private String sRecycled = "";          //为增加还原和删除功能加的一个中介字符串变量
    public ValuationDaysBean() {
    }

    /**
     * checkInput
     * 检查估值日群数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        if(this.bCopy){
            dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_ValuationDays"),
                                   "FPortCode",
                                   this.strPortCode,
                                   "");
        }else{
            dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_ValuationDays"),
                                   "FPortCode",
                                   this.strPortCode,
                                   this.strOldPortCode);
        }
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //自动回滚事物
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);//手动处理事务的提交

            strSql = "insert into " + pub.yssGetTableName("Tb_Para_ValuationDays") +
                "(FValuationdaysCode,FValuationdaysName,FPortCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                "values(" + dbl.sqlString(this.strValuationdaysCode) + "," +
                dbl.sqlString(this.strValuationdaysName) + "," +
                dbl.sqlString(this.strPortCode) + "," +
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";
            dbl.executeSql(strSql);

            if (this.bCopy) { //如果为复制，则将该组合群对应的组合设置同时复制
                strSql = "insert into " + pub.yssGetTableName("Tb_Para_ValuationDay") +
                    " (FPORTCODE,FVALUATIONDAYSCODE,FDATE,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" +
                    " (select " + dbl.sqlString(this.strPortCode) + " as FPORTCODE," +
                    dbl.sqlString(this.strValuationdaysCode) + " as FVALUATIONDAYSCODE,FDATE,FDESC," +
                    (pub.getSysCheckState() ? "0" : "1") + " as FCHECKSTATE," +
                    dbl.sqlString(this.creatorCode) + " as FCREATOR, " +
                    dbl.sqlString(this.creatorTime) + " as FCREATETIME,'' as FCHECKUSER,'' as FCHECKTIME from " +
                    pub.yssGetTableName("Tb_Para_ValuationDay") +
                    " where FValuationdaysCode = " + dbl.sqlString(this.strOldValuationdaysCode) +
                    " and FPortCode = " + dbl.sqlString(this.strOldPortCode) + ")";
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增估值日群出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * 更新数据
     * 注意：此处不仅要更新估值日群的数据，还要更新估值日的数据
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //自动回滚事物
        Connection conn = dbl.loadConnection();
        try {
            //手动处理事务
            conn.setAutoCommit(false);

            //修改估值日群的数据信息
            strSql = "update " + pub.yssGetTableName("Tb_Para_ValuationDays") +
                " set FValuationdaysCode = " + dbl.sqlString(this.strValuationdaysCode) + "," +
                "FValuationdaysName = " + dbl.sqlString(this.strValuationdaysName) + "," +
                "FPortCode = " + dbl.sqlString(this.strPortCode) + "," +
                "FDesc = " + dbl.sqlString(this.strDesc) + "," +
                "FCheckState = " + (pub.getSysCheckState() ? "0" : "1") + "," +
                "FCreator = " + dbl.sqlString(this.creatorCode) + "," +
                "FCreateTime = " + dbl.sqlString(this.creatorTime) + "," +
                "FCheckUser = " + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FValuationdaysCode = " + dbl.sqlString(this.strOldValuationdaysCode) +
                " and FPortCode = " + dbl.sqlString(this.strOldPortCode);
            dbl.executeSql(strSql);

            //修改估值日表中对应的数据
            strSql = "update " + pub.yssGetTableName("Tb_Para_ValuationDay") +
                " set FValuationdaysCode = " + dbl.sqlString(this.strValuationdaysCode) + "," +
                "FPortCode = " + dbl.sqlString(this.strPortCode) +
                " where FValuationdaysCode = " + dbl.sqlString(this.strOldValuationdaysCode) +
                " and FPortCode = " + dbl.sqlString(this.strOldPortCode);
            dbl.executeSql(strSql);

            //提交事物
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("更新估值日群出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * 删除估值日群和估值日，实际是更新审核状态为2
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //自动回滚事物
        Connection conn = dbl.loadConnection();
        try {
            //手动处理事务
            conn.setAutoCommit(false);

            //删除估值日群数据
            strSql = "update " + pub.yssGetTableName("Tb_Para_ValuationDays") +
                " set FCheckState = 2" +
                " where FValuationdaysCode = " + dbl.sqlString(this.strOldValuationdaysCode) +
                " and FPortCode = " + dbl.sqlString(this.strOldPortCode);
            dbl.executeSql(strSql);

            //同时删除估值日表中相对应的数据
            strSql = "update " + pub.yssGetTableName("Tb_Para_ValuationDay") +
                " set FCheckState = 2" +
                " where FValuationdaysCode = " + dbl.sqlString(this.strValuationdaysCode) +
                " and FPortCode = " + dbl.sqlString(this.strPortCode);
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除估值日群设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 审核估值日群和估值日的数据
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";         //定义一个字符串来放SQL语句
        String strSqlChild = "";    //定义一个字符串来放SQL语句
        String[] arrData = null;    //定义一个字符数组来循环还原
        boolean bTrans = true;      //自动回滚事物
        Statement st = null;        //处理批量数据
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            //手动处理事务
            conn.setAutoCommit(false);
            //创建Statment对象
            st = conn.createStatement();

            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) { //判断传来的内容是否为空
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql =
                        "update " + pub.yssGetTableName("Tb_Para_ValuationDays") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FValuationdaysCode = " + dbl.sqlString(this.strOldValuationdaysCode) +
                        " and FPortCode = " + dbl.sqlString(this.strOldPortCode); //更新数据的SQL语句
                    //审核和反审核估值日群的时候同时审核字表中的数据
                    strSqlChild =
                        "UPDATE " + pub.yssGetTableName("Tb_Para_ValuationDay") +
                        " SET FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FValuationdaysCode = " + dbl.sqlString(this.strOldValuationdaysCode) +
                        " and FPortCode = " + dbl.sqlString(this.strOldPortCode);

                    //将两条数据的更新都加入到批处理中
                    st.addBatch(strSql);
                    st.addBatch(strSqlChild);
                }
                st.executeBatch();
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核估值日群设置信息出错", e);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans); //处理事务
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
        String strSql = "";         //定义一个字符串来放SQL语句
        String strSqlChild = "";    //定义一个字符串来放SQL语句
        String[] arrData = null;    //定义一个字符数组来循环删除
        boolean bTrans = true;
        Connection conn = dbl.loadConnection();
        Statement st = null;
        try {
            //判断回收站是否为空，如果不为空则根据解析的字符串执行SQL语句删除数据
            if (sRecycled != null && sRecycled.length() != 0) {
                st = conn.createStatement();
                //按照规定的解析规则对数据进行解析
                arrData = sRecycled.split("\r\n");
                //手动处理事务
                conn.setAutoCommit(false);
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Para_ValuationDays") +
                        " where FValuationdaysCode = " + dbl.sqlString(this.strOldValuationdaysCode) +
                        " and FPortCode = " + dbl.sqlString(this.strOldPortCode);
                    //同时彻底删除该估值日群对应的估值日
                    strSqlChild = "delete from " + pub.yssGetTableName("Tb_Para_ValuationDay") +
                        " where FValuationdaysCode = " + dbl.sqlString(this.strOldValuationdaysCode) +
                        " and FPortCode = " + dbl.sqlString(this.strOldPortCode);

                    st.addBatch(strSql);
                    st.addBatch(strSqlChild);
                }
                //执行批处理
                st.executeBatch();
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(false);
            }
        } catch (Exception e) {
            throw new YssException("清除数据出错！", e);
        } finally {
            dbl.closeStatementFinal(st);    //释放资源
            dbl.endTransFinal(conn, bTrans);//事物处理
        }
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
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

    /**
     * 获取所有数据：审核、反审核、回收站
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();

            //通过估值日群表连接组合群通过组合群代码匹配，同时连接两次用户表，只查询存在的用户处理的数据
            strSql = "select a.*, pf.FPortName,d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_ValuationDays") + " a " +
                //edited by zhouxiang MS01277    组合设置中有两笔组合代码一致，但组合名称和启用日期不一致的数据    QDV4赢时胜(测试)2010年06月3日04_B     周翔  
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
        
                " left join " + "(select  k.fportcode,k.fportname from "+pub.yssGetTableName("Tb_Para_Portfolio")+
                " k   where k.Fcheckstate =1 " 
                + ") pf on pf.FPortCode = a.FPortCode " +
            
                
                //end by lidaolong 
                //------------end-------------
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.strValuationdaysCode = rs.getString("FValuationdaysCode") + "";
                this.strValuationdaysName = rs.getString("FValuationdaysName") + "";
                this.strPortCode = rs.getString("FPortCode") + "";
                this.strPortName = rs.getString("FPortName") + "";
                this.strDesc = rs.getString("FDesc");
                super.setRecLog(rs);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取估值日群信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * buildFilterSql
     *筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strValuationdaysCode.length() != 0) {
                sResult = sResult + " and a.FValuationdaysCode like '" +
                    filterType.strValuationdaysCode.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filterType.strValuationdaysName.length() != 0) {
                sResult = sResult + " and a.FValuationdaysName like '" +
                    filterType.strValuationdaysName.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filterType.strPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.strPortCode.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
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

    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     * 解析数据
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
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
            this.strValuationdaysCode = reqAry[0];
            this.strValuationdaysName = reqAry[1];
            this.strPortCode = reqAry[2];
            this.strDesc = reqAry[3];
            this.checkStateId = Integer.parseInt(reqAry[4]);
            this.strOldValuationdaysCode = reqAry[5];
            this.strOldPortCode = reqAry[6];
            if(reqAry[7].equals("true")){
                this.bCopy = true;
            }
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ValuationDaysBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析估值日群设置请求出错", e);
        }
    }

    /**
     * 组装数据
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strValuationdaysCode).append("\t");
        buf.append(this.strValuationdaysName).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }
}
