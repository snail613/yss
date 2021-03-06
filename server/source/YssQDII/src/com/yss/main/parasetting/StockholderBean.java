package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class StockholderBean
    extends BaseDataSettingBean implements IDataSetting {

    private String sStockholderCode = ""; //股东代码
    private String sStockholderName = ""; //股东名称
    private String sExchangeCode = ""; //交易所代码
    private String sExchangeName = ""; //交易所名称
    private String sStockholderNum = ""; //股东编号
    private String sInvestManagerCode = "";//投资经理代码
    private String sInvestManagerName = "";//投资经理名称
    

	private String sDesc = ""; //描述
    private String sOldStockholderCode = "";
    private StockholderBean filterType;
    private String sRecycled = "";

    public StockholderBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();

        buf.append(this.sStockholderCode).append("\t");
        buf.append(this.sStockholderName).append("\t");
        buf.append(this.sExchangeCode).append("\t");
        buf.append(this.sExchangeName).append("\t");
        buf.append(this.sStockholderNum).append("\t");
      //add by yanghaiming 20100421 B股业务  增加投资经理
        buf.append(this.sInvestManagerCode).append("\t");
        buf.append(this.sInvestManagerName).append("\t");
      //add by yanghaiming 20100421 B股业务  增加投资经理
        buf.append(this.sDesc).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
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
     * parseRowStr
     *
     * @param sRowStr String
     */
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

            this.sStockholderCode = reqAry[0];
            this.sStockholderName = reqAry[1];
            this.sExchangeCode = reqAry[2];
            this.sExchangeName = reqAry[3];
            this.sStockholderNum = reqAry[4];
          //add by yanghaiming 20100421 B股业务  增加投资经理
            this.sInvestManagerCode = reqAry[5];
            this.sInvestManagerName = reqAry[6];
          //add by yanghaiming 20100421 B股业务  增加投资经理
            this.sDesc = reqAry[7];
            this.checkStateId = Integer.parseInt(reqAry[8]);
            ;
            this.sOldStockholderCode = reqAry[9];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new StockholderBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析股东设置出错", e);
        }
    }

    /**
     * 此方法已被修改
     *修改时间：2008年2月20号
     * 修改人：单亮
     * 原方法的功能：查询出股东表的数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = this.getListView1Headers();
            //修改前的代码
//      strSql=
//       "select  y.* from (select * from " +pub.yssGetTableName("Tb_Para_Stockholder") + " where FCheckState <> 3 ) x join (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName as FExchangeName" +
//        " from " + pub.yssGetTableName("Tb_Para_Stockholder") + " a" +
//        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
//         " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
//         " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) d on a.FExchangeCode = d.FExchangeCode ) y on x.FStockholderCode=y.FStockholderCode"  +
//        buildFilterSql();
            //修改后的代码
            //------------------------------------
            strSql =
                "select  y.* from (select * from " + pub.yssGetTableName("Tb_Para_Stockholder") +
                " ) x join (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName as FExchangeName,e.FINVMGRNAME as FINVMGRNAME" +//add by yanghaiming 20100421 B股业务  增加投资经理
                " from " + pub.yssGetTableName("Tb_Para_Stockholder") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            
                //edited by zhouxiang MS01450 新建信息时，出现多条信息
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
        
                
                " left join (select m.FINVMGRCODE,m.FINVMGRNAME,m.FSTARTDATE from " + pub.yssGetTableName("Tb_Para_InvestManager")+
                " m  where m.FCHECKSTATE = 1 " +
                ") e on a.FINVMGRCODE = e.FINVMGRCODE" +//add by yanghaiming 20100421 B股业务  增加投资经理
               
                
                
                //end by lidaolong
                //-------------end------------------------------
                
               
               
                " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) d on a.FExchangeCode = d.FExchangeCode ) y on x.FStockholderCode=y.FStockholderCode" +
                buildFilterSql();
            //------------------------------

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setStockholderAttr(rs);
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
            throw new YssException("获取股东信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.sStockholderCode.length() != 0) {
                sResult = sResult + " and x.FStockholderCode like '%" +
                    filterType.sStockholderCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sStockholderName.length() != 0) {
                sResult = sResult + " and x.FStockholderName like '%" +
                    filterType.sStockholderName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sExchangeCode.length() != 0) {
                sResult = sResult + " and x.FExchangeCode like '%" +
                    filterType.sExchangeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sStockholderNum.length() != 0) {
                sResult = sResult + " and x.FStockholderNum like '%" +
                    filterType.sStockholderNum.replaceAll("'", "''") + "%'";
            }
          //add by yanghaiming 20100421 B股业务  增加投资经理
            if(this.filterType.sInvestManagerCode.length() != 0) {
            	sResult = sResult + " and x.FINVMGRCODE = " +
            		dbl.sqlString(filterType.sInvestManagerCode);
            }
          //add by yanghaiming 20100421 B股业务  增加投资经理
            if (this.filterType.sDesc.length() != 0) {
                sResult = sResult + " and x.FDesc like '%" +
                    filterType.sDesc.replaceAll("'", "''") + "%'";
            }

        }
        return sResult;
    }

    public void setStockholderAttr(ResultSet rs) throws YssException {
        try {
            this.sStockholderCode = rs.getString("FStockholderCode");
            this.sStockholderName = rs.getString("FStockholderName");
            this.sExchangeCode = rs.getString("FExchangeCode");
            this.sExchangeName = rs.getString("FExchangeName");
            this.sStockholderNum = rs.getString("FStockholderNum");
            this.sInvestManagerCode = rs.getString("FINVMGRCODE");//add by yanghaiming 20100421 B股业务  增加投资经理
            this.sInvestManagerName = rs.getString("FINVMGRNAME");
            this.sDesc = rs.getString("FDesc");

            super.setRecLog(rs);

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "股东代码\t股东名称";
            strSql =
                "select  y.* from (select FStockholderCode,FStockholderName from " +
                pub.yssGetTableName("Tb_Para_Stockholder") +
                " where FCheckState <> 2) x join (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName as FExchangeName,e.FINVMGRNAME as FINVMGRNAME" +//add by yanghaiming 20100421 B股业务  增加投资经理
                " from " + pub.yssGetTableName("Tb_Para_Stockholder") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FINVMGRCODE,FINVMGRNAME from " + pub.yssGetTableName("Tb_Para_InvestManager") + " where FCHECKSTATE = 1) e on a.FINVMGRCODE = e.FINVMGRNAME" +//add by yanghaiming 20100421 B股业务  增加投资经理
                " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) d on a.FExchangeCode = d.FExchangeCode ) y on x.FStockholderCode=y.FStockholderCode" +
                " where  y.FCheckState=1 order by y.FStockholderCode ";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FStockholderCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FStockholderName") + "").trim()).
                    append("\t").append(YssCons.YSS_LINESPLITMARK);

                setStockholderAttr(rs);
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
            throw new YssException("获取股东信息出错！", e);
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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

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
                "insert into " + pub.yssGetTableName("Tb_Para_Stockholder") +
                "(FStockholderCode, FStockholderName, " +
                "  FExchangeCode, FStockholderNum, FINVMGRCODE, FDesc, FCheckState, FCreator, FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.sStockholderCode) + "," +
                dbl.sqlString(this.sStockholderName) + "," +
                dbl.sqlString(this.sExchangeCode.length() == 0 ? " " :
                              this.sExchangeCode) + "," +
                dbl.sqlString(this.sStockholderNum) + "," +
                dbl.sqlString(this.sInvestManagerCode) + "," +//add by yanghaiming 20100421 B股业务  增加投资经理
                dbl.sqlString(this.sDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加股东信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Para_Stockholder"),
                               "FStockholderCode",
                               this.sStockholderCode,
                               this.sOldStockholderCode);
    }

    /**
     * 此方法已被修改
     * 修改时间：2008年3月20号
     * 修改人：单亮
     * 原方法功能：只能处理审核和未审核的单条信息。
     * 新方法功能：可以处理审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 修改后不影响原方法的功能
     * =====================================
     * 修改时间：2009年6月3日
     * 修改人  : sunkey
     * 修改描述：美化代码，关闭游标资源
     * Bug编号: MS00472:QDV4上海2009年6月02日01_B
     */
    public void checkSetting() throws YssException {
        Connection conn = null;
        String[] arrData = null;
        boolean bTrans = true;
        PreparedStatement stm = null;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
           //arrData = sRecycled.split("\r\f");  //modify by zhangjun 2012-05-09 BUG#4435
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);

            sqlStr = "update " + pub.yssGetTableName("Tb_Para_Stockholder") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" + "  where FStockholderCode =? ";

            //把sql语句付给PreparedStatement
            stm = dbl.openPreparedStatement(sqlStr);

            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.sStockholderCode);
                stm.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("审核股东设置信息出错", e);
        } finally {
            dbl.closeStatementFinal(stm);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_Stockholder") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" + "  where FStockholderCode = " + dbl.sqlString(this.sOldStockholderCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除股东设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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

            strSql = "update " + pub.yssGetTableName("Tb_Para_Stockholder") +
                " set FStockholderCode = " + dbl.sqlString(this.sStockholderCode) +
                ", FStockholderName = " + dbl.sqlString(this.sStockholderName) +
                " , FExchangeCode = " + dbl.sqlString(this.sExchangeCode) +
                " , FStockholderNum = " + dbl.sqlString(this.sStockholderNum) +
                " , FINVMGRCODE = " + dbl.sqlString(this.sInvestManagerCode) + //add by yanghaiming 20100421 B股业务  增加投资经理
                ", FDesc = " + dbl.sqlString(this.sDesc) +
                ", FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FCheckUser = " + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "  where FStockholderCode = " + dbl.sqlString(this.sOldStockholderCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新股东设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
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
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    public String getSOldStockholderCode() {
        return sOldStockholderCode;
    }

    public String getSDesc() {
        return sDesc;
    }

    public String getSExchangeCode() {
        return sExchangeCode;
    }

    public String getSExchangeName() {
        return sExchangeName;
    }

    public String getSStockholderCode() {
        return sStockholderCode;
    }

    public String getSStockholderName() {
        return sStockholderName;
    }

    public String getSStockholderNum() {
        return sStockholderNum;
    }

    public void setSStockholderNum(String sStockholderNum) {
        this.sStockholderNum = sStockholderNum;
    }

    public void setSStockholderName(String sStockholderName) {
        this.sStockholderName = sStockholderName;
    }

    public void setSStockholderCode(String sStockholderCode) {
        this.sStockholderCode = sStockholderCode;
    }

    public void setSExchangeName(String sExchangeName) {
        this.sExchangeName = sExchangeName;
    }

    public void setSExchangeCode(String sExchangeCode) {
        this.sExchangeCode = sExchangeCode;
    }

    public void setSDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    public void setSOldStockholderCode(String sOldStockholderCode) {
        this.sOldStockholderCode = sOldStockholderCode;
    }

    /**
     * 从回收站中彻底删除数据
     * ==========方法重新修改记录================
     * 修改时间：2009年6月3日
     * 修改人  : sunkey
     * 修改描述：美化代码，关闭游标资源
     * Bug编号: MS00472:QDV4上海2009年6月02日01_B
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        PreparedStatement stm = null;
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //根据规定的符号，把多个sql语句分别放入数组
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Para_Stockholder") +
                " where FStockholderCode = ?";

            //把sql语句付给PreparedStatement
            stm = dbl.openPreparedStatement(strSql);
            //循环执行这些删除语句
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.sStockholderCode);
                stm.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.closeStatementFinal(stm);
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
    
    public String getsInvestManagerCode() {
		return sInvestManagerCode;
	}

	public void setsInvestManagerCode(String sInvestManagerCode) {
		this.sInvestManagerCode = sInvestManagerCode;
	}

	public String getsInvestManagerName() {
		return sInvestManagerName;
	}

	public void setsInvestManagerName(String sInvestManagerName) {
		this.sInvestManagerName = sInvestManagerName;
	}
}
