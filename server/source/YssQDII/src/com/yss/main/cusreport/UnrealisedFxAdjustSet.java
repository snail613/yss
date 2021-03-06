package com.yss.main.cusreport;

import java.math.*;
import java.sql.*;
import java.util.Date;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.main.operdeal.report.reptab.TabSummary;

/**
 * 该类主要是对 末兑现资产本金增值/贬值分布表（汇兑） 进行相关信息显示、修改等操作。
 * <p>Title: </p>
 * <p>Description: 数据展开、修改操作
 * MS00666,未兑现资产本金增值/贬值分布表（汇率）    QDV4中保2009年09月02日04_B
 * </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author xuxuming
 * @version 1.0
 */
public class UnrealisedFxAdjustSet
      extends BaseDataSettingBean
      implements IDataSetting {
   private String sRecycled = ""; //回收站数据

//   private String Code; //标示字段，用于排序
   private String sName; //项目名
   private String sCatCode; //品种类型
   private String sSubCatCode; //品种子类型
   private String sCuryCode; //货币
//   private BigDecimal dBal; //原币金额
   private int dUnrealisedType = 0; //纪录类型
   private String sCode; //唯一性编码 合并太平版本
   private BigDecimal dBaseCuryBal; //基础货币金额
   private String sPortCode; //组合
   private java.util.Date dtDate;
   private UnrealisedFxAdjustSet filterType = null;//parseRowStr方法中 用于筛选

   private String sEntityData;
   public String getSName() {
      return sName;
   }
   //getting for sCode 合并太平版本
   public String getSCode() {
      return sCode;
   }

   public String getSCuryCode() {
      return sCuryCode;
   }

   public Date getDtDate() {
      return dtDate;
   }

   public String getSCatCode() {
      return sCatCode;
   }

   public BigDecimal getDBaseCuryBal() {
      return dBaseCuryBal;
   }

   public int getDUnrealisedType() {
      return dUnrealisedType;
   }

   public UnrealisedFxAdjustSet getFilterType() {
      return filterType;
   }

   public String getSSubCatCode() {
      return sSubCatCode;
   }

   public void setSPortCode(String sPortCode) {
      this.sPortCode = sPortCode;
   }

   public void setSName(String sName) {
      this.sName = sName;
   }
   //setting for sCode 合并太平版本
   public void setSCode(String sCode) {
      this.sCode = sCode;
   }

   public void setSCuryCode(String sCuryCode) {
      this.sCuryCode = sCuryCode;
   }

   public void setDtDate(Date dtDate) {
      this.dtDate = dtDate;
   }

   public void setSCatCode(String sCatCode) {
      this.sCatCode = sCatCode;
   }

   public void setDBaseCuryBal(BigDecimal dBaseCuryBal) {
      this.dBaseCuryBal = dBaseCuryBal;
   }

   public void setDUnrealisedType(int dUnrealisedType) {
      this.dUnrealisedType = dUnrealisedType;
   }

   public void setFilterType(UnrealisedFxAdjustSet filterType) {
      this.filterType = filterType;
   }

   public void setSSubCatCode(String sSubCatCode) {
      this.sSubCatCode = sSubCatCode;
   }

   public String getSPortCode() {
      return sPortCode;
   }

   public UnrealisedFxAdjustSet() {
   }

   /**
    * getListViewData1
    * 获取主界面的信息
     * @throws YssException
    * @return String
    */
   public String getListViewData1() throws YssException{
        String sqlStr = ""; //用于存储SQL语句
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer(); //显示字段
        StringBuffer bufAll = new StringBuffer(); //全显示字段

        try {
            sHeader = "组合代码\t日期"; //前台listview头

            //从Summary表中查询组合代码、业务日期，并通过日期、组合代码排序
            sqlStr = "select distinct FPortCode,FDate from " + //组合代码和日期查出来
                pub.yssGetTableName("TB_Data_Unrealised") + " a" + //Unrealised数据表
                buildFilterSql() +  //添加筛选条件的过滤，避免将所有数据查询出来
                " order by FDate,FPortCode ";

            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FPortCode")).append("\t"); //组合代码
                bufShow.append(YssFun.formatDate(rs.getDate("FDate"))).append(YssCons.YSS_LINESPLITMARK); //日期
                setResultSetAttr(rs); //建一个方法
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            //下面两个判断均是为了删除字符串结尾的“\f\f”
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.delete(bufShow.length() - 2, bufShow.length()).toString();
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.delete(bufAll.length() - 2, bufAll.length()).toString();
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + "FPortCode\tFDate"; //把数据返向前台
        } catch (Exception e) {
            throw new YssException("获取Unrealised表数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

   }

   /**
    * setResultSetAttr
    *
    * @param rs ResultSet
    */
   private void setResultSetAttr(ResultSet rs) throws SQLException {
      this.sPortCode = rs.getString("FPortCode");//组合代码
        this.dtDate = rs.getDate("FDate");//日期
   }

   /**
    * buildFilterSql
    *
    * @return String
    */
   private String buildFilterSql() {
      String sResult = "";
        if (this.filterType != null) {
            sResult = sResult + " where 1 = 1";

            if (this.filterType.sPortCode.length() != 0) { //组合代码
                sResult = sResult + " and FPortCode like '" +
                    filterType.sPortCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.dtDate != null && !YssFun.formatDate(filterType.dtDate, "yyyy-MM-dd").equals("9998-12-31")) { //日期
                sResult = sResult + " and FDate = " + dbl.sqlDate(filterType.dtDate);
            }
        }
        return sResult;

   }

   /**
    * getListViewData2
    *  获取对应的修改的信息界面
     * @throws YssException
    * @return String
    */
   public String getListViewData2() throws YssException {
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer(); //显示字段
        StringBuffer bufAll = new StringBuffer(); //全显示字段

        try {
            //=========调整金额数据的显示 保留两位小数，右对齐 MS00559:QDV4中保2009年07月03日01_B libo 20090705==========
            sHeader = "项目名称\t港币等值金额;R";//
			//合并太平版本
//     	       sqlStr = "select FName,FPortCode,FDate,FBal,FBaseCuryBal,FUnrealisedType,FCode from " + //项目代码 项目名称 原币金额 港币金额 项目内码
//  	              pub.yssGetTableName("TB_Data_Unrealised") + buildFilterSql() + " and FUnrealisedType in (3, 4, 5, 6) order by FCode"; //summary表
         	sqlStr = "select b.FName||'#'||a.FName as FName, FPortCode, FDate, FBal, FBaseCuryBal, FUnrealisedType, FCode from (select * from "
               	+ pub.yssGetTableName("TB_Data_Unrealised") + buildFilterSql() +
               	" and FUnrealisedType in (3, 4)) a, (select FName,FCatCode,FSubCatCode from "
               	+ pub.yssGetTableName("TB_Data_Unrealised") + buildFilterSql() + " and FUnrealisedType = 5) b where a.FCatCode = b.FCatCode and a.FSubCatCode = b.FSubCatCode "
               	+ " order by b.FCatCode,b.FSubCatCode,a.FUnrealisedType ";
			   //合并太平版本
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FName")).append("\t"); //项目名称
            	bufShow.append(YssFun.formatNumber(rs.getDouble("FBaseCuryBal"),
                                               	"#,##0.##")).append("\t");
            	bufShow.append(rs.getString("FCode")).append("\t");
            	bufShow.append(YssCons.YSS_LINESPLITMARK); //港币金额，保留两位小数 合并太平版本
                setResultSetAttr1(rs); //建一个方法
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            //======================End MS00559====================================================================

            //将两个返回前台的字符串结尾的"\f\f"删除掉
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.delete(bufShow.length() - 2, bufShow.length()).toString();
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.delete(bufAll.length() - 2, bufAll.length()).toString();
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + "FName\tFBaseCuryBal"; //把数据返向前台
        } catch (Exception e) {
            throw new YssException("获取信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

   }

   /**
    * setResultSetAttr1
    *
    * @param rs ResultSet
    */
   private void setResultSetAttr1(ResultSet rs) throws SQLException {

        this.sName = rs.getString("FName");//名称
        this.sPortCode = rs.getString("FPortCode");//组合代码
        this.dtDate = rs.getDate("FDate");//日期
        this.dBaseCuryBal = rs.getBigDecimal("FBaseCuryBal");//基础金额
        this.dUnrealisedType = rs.getInt("FUnrealisedType");//类型
      	this.sCode = rs.getString("FCode"); //类型 合并太平版本

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
   public String addSetting() {
      return "";
   }

   /**
    * checkInput
    *
    * @param btOper byte
    */
   public void checkInput(byte btOper) {
   }

   /**
    * checkSetting
    */
   public void checkSetting() {
   }

   /**
    * delSetting
    */
   public void delSetting() {
   }

   /**
    * deleteRecycleData
    */
   public void deleteRecycleData() {
   }

   /**
    * editSetting
    *  修改金额的方法
     * 为了方便以后批量处理的扩展，此处使用批量处理的方法处理
    * @return String
    */
   public String editSetting() throws YssException {
      Connection conn = null;         //数据库连接
        String strSql = "";             //用于存储sql语句的字符串
        String[] arrData = null;        //用于存储每条要更新的数据
        Statement st = null;
        boolean bTrans = true;          //事物控制，默认为true代表自动回滚事物，操作成功后将变为false
        try {
            conn = dbl.loadConnection();    //获取数据库连接
            conn.setAutoCommit(false);      //阻止事物自动提交
            st = conn.createStatement();    //通过连接对象创建Statement对象，用于批处理

            arrData = sEntityData.split("\r\f"); //解析数据

            for (int i = 0; i < arrData.length; i++) {

                this.parseRowStr(arrData[i]);   //将单条数据解析成对应的类属性

                //通过实体类的属性更新Unrealised表的数据
                //通过组合代码、日期、项目代码进行匹配，更新金额、基础金额
                //if(this.dUnrealisedType!=5){ 合并太平版本
                   strSql = "update " + pub.yssGetTableName("TB_Data_Unrealised") +

                         " set FBaseCuryBal = " + this.dBaseCuryBal + //基础金额
                         " where FPortCode = " + dbl.sqlString(this.sPortCode) + //组合代码
                         " and FDate= " + dbl.sqlDate(this.dtDate) + //日期
                  	//" and FName = " + dbl.sqlString(this.sName) + // 名称
                  	" and FCode = " + dbl.sqlString(this.sCode);//合并太平版本
                   st.addBatch(strSql); //添加到批处理
				   }
                //}else{//UnrealisedType为5时，要先调用operdeal.report.reptab.TabSummary.java的calcUnrealisedCapital()方法重新计算。 //合并太平版本 不需要了
                       //并且之前要作删除操作
         strSql = "update " + pub.yssGetTableName("TB_Data_Unrealised") + " t set FBaseCuryBal = "
               + " (select sum(case when FUnrealisedType = '4' then FBaseCuryBal else -FBaseCuryBal end ) as FBaseCuryBal  from " + pub.yssGetTableName("TB_Data_Unrealised") + " where FPortCode = "//合并太平资产版本 2010.09.13
               + dbl.sqlString(this.sPortCode) + " and FDate = " + dbl.sqlDate(this.dtDate) + " and FUnrealisedType in (3, 4) "
               + " and FCatCode = t.FCatCode and FSubCatCode = t.FSubCatCode and FCuryCode = t.FCuryCode group by FCatCode, FSubCatCode, FCuryCode) "
               + " where FPortCode = " + dbl.sqlString(this.sPortCode) + " and FDate = " + dbl.sqlDate(this.dtDate) + " and FUnrealisedType = 5";
         st.addBatch(strSql);
           //合并太平资产 2010.09.13
//         TabSummary tabSummaryData = new TabSummary();
//         tabSummaryData.setYssPub(pub);
//         tabSummaryData.setPortCode(this.sPortCode);
//         tabSummaryData.setDEndDate(this.dtDate);
//         tabSummaryData.delCalcUnrealisedCapiPub(); //删除相关记录
//         tabSummaryData.calcUnrealisedCapiPub(); //重新计算，生成报表数据
                //}//合并太平版本
            //}//合并太平版本
            st.executeBatch();

            //提交事物处理
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("修改Unrealised报表信息出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);//modified by yeshenghong for CCB security check 20121018 
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
    * buildRowStr
    *传向前台数据
    * @return String
    */
   public String buildRowStr() {
      StringBuffer buf = new StringBuffer();

        buf.append(this.sPortCode.trim()).append("\t");//组合代码
        buf.append(this.dtDate).append("\t"); //日期
        //=========调整金额数据的显示 只显示两位小数 对BigDecimal数据格式化 ==========

        if (this.dBaseCuryBal != null) { //当主界面不取出数据时,不进行格式化
       buf.append((this.dBaseCuryBal).setScale(2, BigDecimal.ROUND_HALF_UP)).append("\t");
        } else {
            buf.append(this.dBaseCuryBal).append("\t");
       }
        buf.append(this.sName).append("\t"); //项目名称
        buf.append(this.dUnrealisedType).append("\t"); //类型
      	buf.append(this.sCode).append("\t"); //类型 //合并太平版本
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
    *解析前台的数据
    * @param sRowStr String
    */
   public void parseRowStr(String sRowStr) throws YssException {
      String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            this.sRecycled = sRowStr;

            reqAry = sTmpStr.split("\t");
            this.sPortCode = reqAry[0]; //组合代码
            this.dtDate = YssFun.parseDate(reqAry[1]); //日期
       //     this.dBal = new BigDecimal(reqAry[2].trim().length()==0?"0":reqAry[2]); //金额
            this.dBaseCuryBal = new BigDecimal(reqAry[2].trim().length()==0?"0":reqAry[2]); //基础金额

         //   this.sCode = reqAry[4]; //项目代码
            this.sName = reqAry[3]; //项目名称
            if(reqAry[4]!=null&&reqAry[4].trim().length()>0){
               this.dUnrealisedType = Integer.parseInt(reqAry[4]); //类型
            }
            // super.parseRecLog();/时间等
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new UnrealisedFxAdjustSet();
                    this.filterType.setYssPub(pub); //全局的东西
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
                if (sRowStr.split("\r\t").length > 2) {
                    this.sEntityData = sRowStr.split("\r\t")[2]; //实体传入
                }
            }
         	this.sCode = reqAry[5]; //合并太平版本
        } catch (Exception e) {
            throw new YssException("解析信息出错", e);
        }

   }

   /**
    * getBeforeEditData
    *
    * @return String
    */
   public String getBeforeEditData() {
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
