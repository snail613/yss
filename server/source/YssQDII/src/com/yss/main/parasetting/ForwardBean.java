package com.yss.main.parasetting;

import java.math.*;
import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class ForwardBean
    extends BaseDataSettingBean implements IDataSetting {
    private String securityCode = "";
    private String securityName = "";
    private String depDurCode = "";
    private String depDurName = "";
    private double trustRate;

    private String settleCuryCode = "";
    private String settleCuryName = "";

    private String buyCuryCode = "";
    private String buyCuryName = "";

    private String calcPriceMeticCode = ""; //价格算法
    private String calcPriceMeticName = "";

    private String desc = "";
    private String oldSecurityCode = "";
    private String sRecycled = "";

    private ForwardBean filterType;
    public String getSecurityName() {
        return securityName;
    }

    public String getDesc() {
        return desc;
    }

    public double getTrustRate() {
        return trustRate;
    }

    public ForwardBean getFilterType() {
        return filterType;
    }

    public String getDepDurCode() {
        return depDurCode;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getDepDurName() {
        return depDurName;
    }

    public String getSettleCuryCode() {
        return settleCuryCode;
    }

    public void setSettleCuryName(String settleCuryName) {
        this.settleCuryName = settleCuryName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTrustRate(double trustRate) {
        this.trustRate = trustRate;
    }

    public void setFilterType(ForwardBean filterType) {
        this.filterType = filterType;
    }

    public void setDepDurCode(String depDurCode) {
        this.depDurCode = depDurCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setDepDurName(String depDurName) {
        this.depDurName = depDurName;
    }

    public void setSettleCuryCode(String settleCuryCode) {
        this.settleCuryCode = settleCuryCode;
    }

    public void setOldSecurityCode(String oldSecurityCode) {
        this.oldSecurityCode = oldSecurityCode;
    }

    public void setBuyCuryName(String buyCuryName) {
        this.buyCuryName = buyCuryName;
    }

    public void setBuyCuryCode(String buyCuryCode) {
        this.buyCuryCode = buyCuryCode;
    }

    public void setCalcPriceMeticName(String calcPriceMeticName) {
        this.calcPriceMeticName = calcPriceMeticName;
    }

    public void setCalcPriceMeticCode(String calcPriceMeticCode) {
        this.calcPriceMeticCode = calcPriceMeticCode;
    }

    public String getSettleCuryName() {
        return settleCuryName;
    }

    public String getOldSecurityCode() {
        return oldSecurityCode;
    }

    public String getBuyCuryName() {
        return buyCuryName;
    }

    public String getBuyCuryCode() {
        return buyCuryCode;
    }

    public String getCalcPriceMeticName() {
        return calcPriceMeticName;
    }

    public String getCalcPriceMeticCode() {
        return calcPriceMeticCode;
    }

    public ForwardBean() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
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
            this.securityCode = reqAry[0];
            this.depDurCode = reqAry[1];
            this.trustRate = YssFun.toDouble(reqAry[2]);

            this.settleCuryCode = reqAry[3];

            this.desc = reqAry[4];
            this.checkStateId = YssFun.toInt(reqAry[5]);

            this.oldSecurityCode = reqAry[6];
            this.buyCuryCode = reqAry[7];
            this.calcPriceMeticCode = reqAry[8];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ForwardBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析远期品种信息设置出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.securityCode);
        buf.append("\t");
        buf.append(this.securityName);
        buf.append("\t");
        buf.append(this.depDurCode);
        buf.append("\t");
        buf.append(this.depDurName);
        buf.append("\t");
        buf.append(this.trustRate);
        buf.append("\t");
        buf.append(this.settleCuryCode);
        buf.append("\t");
        buf.append(this.settleCuryName);
        buf.append("\t");
        buf.append(this.desc);
        buf.append("\t");
        buf.append(this.buyCuryCode);
        buf.append("\t");
        buf.append(this.buyCuryName);
        buf.append("\t");
        buf.append(this.calcPriceMeticCode);
        buf.append("\t");
        buf.append(this.calcPriceMeticName);
        buf.append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Forward"),
                               "FSecurityCode",
                               this.securityCode,
                               this.oldSecurityCode);

    }

    public String getAllSetting() {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.securityCode != null && this.filterType.securityCode.length() != 0) {
                    sResult = sResult + " and a.FSecurityCode like '" +
                        filterType.securityCode.replaceAll("'", "''") + "%'";
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.securityCode != null && this.filterType.depDurCode.length() != 0) {
                    sResult = sResult + " and a.FDepDurCode like '" +
                        filterType.depDurCode.replaceAll("'", "''") + "%'";
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.securityCode != null && this.filterType.settleCuryCode.length() != 0) {
                    sResult = sResult + " and a.FSaleCury like '" +
                        filterType.settleCuryCode.replaceAll("'", "''") + "%'";
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.securityCode != null && this.filterType.desc.length() != 0) {
                    sResult = sResult + " and a.FDesc like '" +
                        filterType.desc.replaceAll("'", "''") + "%'";
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.securityCode != null && this.filterType.buyCuryCode.length() != 0) {
                    sResult = sResult + " and a.FBuyCury like '" +
                        filterType.buyCuryCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.trustRate > 0) {
                    sResult = sResult + " and a.FTRUSTRATE=" +
                        filterType.trustRate;
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.calcPriceMeticCode != null && this.filterType.calcPriceMeticCode.length() != 0) {
                    sResult = sResult + " and a.FCalcPriceMetic like '" +
                        filterType.calcPriceMeticCode.replaceAll("'", "''") + "%'";
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选远期品种信息设置数据出错", e);
        }
        return sResult;
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取远期品种信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 此方法已被修改
     * 修改时间：2008年2月25号
     * 修改人：单亮
     * 原方法的功能：查询出远期品种信息设置数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            " (select FSecurityCode,FCheckState from " +
            pub.yssGetTableName("Tb_Para_Forward") +
            //修改前的代码
            //" where FCheckState <> 2 group by FSecurityCode,FCheckState) x join" +
            //修改后的代码
            //----------------------------begin
            "  group by FSecurityCode,FCheckState) x join" +
            //-----------------------------end
            " (select a.*, " +
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName," +
            " e.FCuryName as FSaleCuryName,f.FDepDurName as FDepDurName,g.FCuryName as FBuyCuryName,h.FCIMName as FCalcPriceMeticName " +
            " from " + pub.yssGetTableName("Tb_Para_Forward") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") + ") d  on a.FSecurityCode = d.FSecurityCode " +
            " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " where FCheckState = 1) e on e.FCuryCode = a.FSaleCury " +
            " left join (select FDepDurCode,FDepDurName from " + pub.yssGetTableName("Tb_Para_DepositDuration") + " where FCheckState = 1) f on f.FDepDurCode = a.FDepDurCode " +
            " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " where FCheckState = 1) g on g.FCuryCode = a.FBuyCury " +
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) h on a.FCalcPriceMetic = h.FCIMCode " + //sj 20071207 add
            buildFilterSql() +
            ") y on x.FSecurityCode = y.FSecurityCode " +
            " order by y.FCheckState, y.FCreateTime";
        return this.builderListViewData(strSql);
    }

    public String getListViewData4() throws YssException {
        String strSql = "";
        return strSql;
    }

    public String getListViewData2() throws YssException {
        String strSql = "";
        return strSql;
    }

    public String getListViewData3() throws YssException {
        String strSql = "";
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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_Forward") +
                "(FSECURITYCODE, FDepDurCode, FTRUSTRATE, FSaleCury,FBuyCury, FDesc,FCALCPRICEMETIC," +
                " FCHECKSTATE, FCREATOR, FCREATETIME,FCheckUser) values(" +
                dbl.sqlString(this.securityCode) + "," +
                dbl.sqlString(this.depDurCode) + "," +
                this.trustRate + "," +
                dbl.sqlString(this.settleCuryCode) + "," +
                dbl.sqlString(this.buyCuryCode) + "," +
                dbl.sqlString(this.desc) + "," +
                dbl.sqlString(this.calcPriceMeticCode) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加远期品种信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_Forward") +
                " set " +
                "  FSECURITYCODE = " + dbl.sqlString(this.securityCode) +
                ", FDepDurCode = " + dbl.sqlString(this.depDurCode) +
                ", FTRUSTRATE = " + this.trustRate +
                ", FSaleCury = " + dbl.sqlString(this.settleCuryCode) +
                ", FBuyCury = " + dbl.sqlString(this.buyCuryCode) +
                ", FDesc = " + dbl.sqlString(this.desc) +
                ", FCALCPRICEMETIC = " + dbl.sqlString(this.calcPriceMeticCode) +
                ", FCHECKSTATE = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FSECURITYCODE = " +
                dbl.sqlString(this.oldSecurityCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改远期品种信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * 删除数据即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int Count = 0;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_Forward") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSECURITYCODE = " +
                dbl.sqlString(this.oldSecurityCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 修改时间：2008年3月25号
     * 修改人：单亮
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 修改后不影响原方法的功能
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//    String strSql = "";
//    boolean bTrans = false; //代表是否开始了事务
//    Connection conn = dbl.loadConnection();
//    try {
//       strSql = "update " + pub.yssGetTableName("Tb_Para_Forward") +
//               " set FCheckState = " + this.checkStateId +
//               ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//               "' where FSECURITYCODE = " +
//               dbl.sqlString(this.oldSecurityCode);
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//       }
//       catch (Exception e) {
//            throw new YssException("审核债券信息出错", e);
//       }
//         finally {
//            dbl.endTransFinal(conn, bTrans);
//         }
        //修改后的代码
        //---------------------------begin
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
                    strSql = "update " + pub.yssGetTableName("Tb_Para_Forward") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FSECURITYCODE = " +
                        dbl.sqlString(this.oldSecurityCode);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而oldSecurityCode不为空，则按照oldSecurityCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (oldSecurityCode != null && !oldSecurityCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Forward") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FSECURITYCODE = " +
                    dbl.sqlString(this.oldSecurityCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核债券信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //------------------end
    }

    public void setSecurityAttr(ResultSet rs) throws SQLException {
        this.securityCode = rs.getString("FSecurityCode");
        this.securityName = rs.getString("FSecurityName");
        this.depDurCode = rs.getString("FDepDurCode");
        this.depDurName = rs.getString("FDepDurName");
        this.trustRate = rs.getDouble("FTRUSTRATE");

        this.settleCuryCode = rs.getString("FSaleCury");
        this.settleCuryName = rs.getString("FSaleCuryName");
        this.desc = rs.getString("FDesc");
        this.buyCuryCode = rs.getString("FBuyCury");
        this.buyCuryName = rs.getString("FBuyCuryName");
        this.calcPriceMeticCode = rs.getString("FCalcPriceMetic");
        this.calcPriceMeticName = rs.getString("FCalcPriceMeticName");
        super.setRecLog(rs);
    }

    public String getOperValue(String sType) {
        return "";
    }
    
    // add by fangjiang 2010.09.27 MS01790 QDV4赢时胜(上海开发部)2010年09月09日09_B 
    public String getBeforeEditData() throws YssException {
    	ForwardBean fb = new ForwardBean();
        String strSql = "";
        ResultSet rs = null;
        try {
        	strSql = "select y.* from " +
            " (select FSecurityCode,FCheckState from " +
            pub.yssGetTableName("Tb_Para_Forward") +
            "  group by FSecurityCode,FCheckState) x join" +
            " (select a.*, " +
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName," +
            " e.FCuryName as FSaleCuryName,f.FDepDurName as FDepDurName,g.FCuryName as FBuyCuryName,h.FCIMName as FCalcPriceMeticName " +
            " from " + pub.yssGetTableName("Tb_Para_Forward") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") + ") d  on a.FSecurityCode = d.FSecurityCode " +
            " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " where FCheckState = 1) e on e.FCuryCode = a.FSaleCury " +
            " left join (select FDepDurCode,FDepDurName from " + pub.yssGetTableName("Tb_Para_DepositDuration") + " where FCheckState = 1) f on f.FDepDurCode = a.FDepDurCode " +
            " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " where FCheckState = 1) g on g.FCuryCode = a.FBuyCury " +
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) h on a.FCalcPriceMetic = h.FCIMCode " + 
            " where a.FSECURITYCODE = " + dbl.sqlString(this.oldSecurityCode) +
            ") y on x.FSecurityCode = y.FSecurityCode " +
            " order by y.FCheckState, y.FCreateTime";
        	
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	fb.securityCode = rs.getString("FSecurityCode");
            	fb.securityName = rs.getString("FSecurityName");
            	fb.depDurCode = rs.getString("FDepDurCode");
            	fb.depDurName = rs.getString("FDepDurName");
            	fb.trustRate = rs.getDouble("FTRUSTRATE");
            	fb.settleCuryCode = rs.getString("FSaleCury");
            	fb.settleCuryName = rs.getString("FSaleCuryName");
            	fb.desc = rs.getString("FDesc");
            	fb.buyCuryCode = rs.getString("FBuyCury");
            	fb.buyCuryName = rs.getString("FBuyCuryName");
            	fb.calcPriceMeticCode = rs.getString("FCalcPriceMetic");
            	fb.calcPriceMeticName = rs.getString("FCalcPriceMeticName");
            }
            return fb.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }
    //-----------------------

    /**
     * 删除回收站的数据即从回收站彻底删除数据
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
                        pub.yssGetTableName("Tb_Para_Forward") +
                        " where FSECURITYCODE = " +
                        dbl.sqlString(this.oldSecurityCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (oldSecurityCode != "" && oldSecurityCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_Forward") +
                    " where FSECURITYCODE = " +
                    dbl.sqlString(this.oldSecurityCode);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
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
