package com.yss.main.operdata;

import java.math.BigDecimal;
import java.sql.*;
import com.yss.util.*;
import com.yss.main.dao.*;
import com.yss.dsub.BaseDataSettingBean;

/**
 * <p>Title:BondParameterBean </p>
 * <p>Description:债券计息维护 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class BondParameterBean
      extends BaseDataSettingBean
      implements IDataSetting {

   private String SecurityCode = ""; //证券代码
   private String SecurityName = ""; //证券名称
   private String RecordDate = ""; //权益登记日
   private String CurCpnDate = ""; //本次起息日
   private String NextCpnDate = ""; //下次起息日
   //edit by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B 修改参数类型 from double to BigDecimal
   private BigDecimal IntAccPer100 = new BigDecimal(-1); //百元利息
   private int IntDay; //已计提天数
   
   private String strIsOnlyColumns = "0"; //在初始登陆时是否只显示列，不查询数据
   private String sRecycled="";//保存未解析前的字符串

   private String oldSecurityCode = "";
   private String oldRecordDate = "";

   private BondParameterBean filterType;
   public void setIntDay(int IntDay) {
      this.IntDay = IntDay;
   }

   public int getIntDay() {
      return IntDay;
   }

   //---edit by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B start---//
   // 修改参数类型 from double to BigDecimal
   public void setIntAccPer100(BigDecimal IntAccPer100) {
      this.IntAccPer100 = IntAccPer100;
   }

   public BigDecimal getIntAccPer100() {
      return IntAccPer100;
   }
   //---edit by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B end---//
   public BondParameterBean() {
   }
   public void setSecurityCode(String securityCode)
   {
      this.SecurityCode=securityCode;
   }
   public String getSecurityCode()
   {
      return SecurityCode;
   }

   /**
    * parseRowStr
    * 解析债券计息数据
    * @param sRowStr String
    */
   public void parseRowStr(String sRowStr) throws YssException {
      String[] reqAry = null;
      String sTmpStr = "";
      try {
         if (sRowStr.trim().length() == 0)return;
         if (sRowStr.indexOf("\r\t") >= 0) {
            sTmpStr = sRowStr.split("\r\t")[0];
         }
         else {
            sTmpStr = sRowStr;
         }
         sRecycled=sRowStr;//把未解析的字符串先赋给sRecycled
         reqAry = sTmpStr.split("\t");
         this.SecurityCode = reqAry[0];
         this.SecurityName = reqAry[1];
         this.RecordDate = reqAry[2];
         this.CurCpnDate = reqAry[3];
         this.NextCpnDate = reqAry[4];
         if(YssFun.isNumeric(reqAry[5])){
        	 //edit by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B 
            this.IntAccPer100 = new BigDecimal(reqAry[5]);
         }
         if(YssFun.isNumeric(reqAry[6])){
            this.IntDay = Integer.parseInt(reqAry[6]);
         }
         super.checkStateId = Integer.parseInt(reqAry[7]);
         this.oldSecurityCode = reqAry[8];
         this.oldRecordDate = reqAry[9];
         this.strIsOnlyColumns = reqAry[10];
         super.parseRecLog();
         if (sRowStr.indexOf("\r\t") >= 0) {
            if (this.filterType == null) {
               this.filterType = new BondParameterBean();
               this.filterType.setYssPub(pub);
            }
            this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
         }
      }
      catch (Exception e) {
         throw new YssException("解析债券计息数据信息出错", e);
      }

   }

   /**
    * buildRowStr
    *
    * @return String
    */
   public String buildRowStr() throws YssException {
      StringBuffer buf = new StringBuffer();
      buf.append(this.SecurityCode).append("\t");
      buf.append(this.SecurityName).append("\t");
      buf.append(this.RecordDate).append("\t");
      buf.append(this.CurCpnDate).append("\t");
      buf.append(this.NextCpnDate).append("\t");
      buf.append(this.IntAccPer100).append("\t");
      buf.append(this.IntDay).append("\t");
      
      buf.append(super.buildRecLog());
      return buf.toString();
   }

   /**
    * addOperData
    * 新增债券计息数据
    * @return String
    */
   public String addSetting() throws YssException {
      Connection conn = dbl.loadConnection();
      boolean bTrans = false; //代表是否开始了事务
      String strSql = "";
      try {
         strSql =
               "insert into " + pub.yssGetTableName("Tb_Para_Bondparamater") +
               "(FSecurityCode,FRecordDate,FCurCpnDate,FNextCpnDate,FIntAccPer100,FIntDay,FCheckState,FCreator,FCreateTime,FCheckUser)" +
               " values(" + dbl.sqlString(this.SecurityCode) + "," +
               dbl.sqlDate(this.RecordDate) + "," +
               dbl.sqlDate(this.CurCpnDate) + "," +
               dbl.sqlDate(this.NextCpnDate) + "," +
               this.IntAccPer100 + "," +
               this.IntDay + "," +
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
         return buildRowStr();
      }
      catch (Exception e) {
         throw new YssException("新增债券计息数据出错", e);
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }
   }

   /**
    * editOperData
    * 修改债券计息数据
    * @return String
    */
   public String editSetting() throws YssException {
      Connection conn = dbl.loadConnection();
      boolean bTrans = false; //代表是否开始了事务
      String strSql = "";
      try {
         strSql =
               "update " + pub.yssGetTableName("Tb_Para_Bondparamater") +
               " set FSecurityCode = " + dbl.sqlString(this.SecurityCode) +
               ",FRecordDate = " + dbl.sqlDate(this.RecordDate) +
               ",FCurCpnDate = " + dbl.sqlDate(this.CurCpnDate) +
               ",FNextCpnDate = " + dbl.sqlDate(this.NextCpnDate) +
               ",FIntAccPer100 = " + this.IntAccPer100 +
               ",FIntDay = " + this.IntDay +
               ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
               ",FCreator = " + dbl.sqlString(this.creatorCode) +
               ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
               ",FCheckUser = " +
               (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
               " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
               " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate);

         conn.setAutoCommit(false);
         bTrans = true;
         dbl.executeSql(strSql);
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
         return buildRowStr();
      }
      catch (Exception e) {
         throw new YssException("修改债券计息数据出错", e);
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }
   }

   /**
    * checkInput
    * 检查输入是否合法
    * @param btOper byte
    */
   public void checkInput(byte btOper) throws YssException {
      dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Bondparamater"),
                             "FSecurityCode,FRecordDate",
                             this.SecurityCode + "," + this.RecordDate,
                             this.oldSecurityCode + "," + this.oldRecordDate);

   }

   /**
    *
    * @throws YssException
    */
   public void checkSetting() throws YssException {
      
      String strSql = "";
      String[] arrData = null;
      boolean bTrans = false; //代表是否开始了事务
      Connection conn = dbl.loadConnection();
      try {
         conn.setAutoCommit(false);
         bTrans = true;
         //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
         if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase("")) ) {
            arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
               if (arrData[i].length() == 0)continue;
               this.parseRowStr(arrData[i]);
               strSql = "update " + pub.yssGetTableName("Tb_Para_Bondparamater") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where  FSecurityCode = " +
                     dbl.sqlString(this.SecurityCode) +
                     " and FRecordDate = " + dbl.sqlDate(this.RecordDate);
               dbl.executeSql(strSql);
            }
         }
         
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (Exception e) {
         throw new YssException("审核债券计息数据出错", e);
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }
   }
   /**
    * delOperData
    * 删除债券计息数据,即放入回收站
    */
   public void delSetting() throws YssException {
      Connection conn = dbl.loadConnection();
      boolean bTrans = false; //代表是否开始了事务
      String strSql = "";
      try {
         strSql = "update " + pub.yssGetTableName("Tb_Para_Bondparamater") +
               " set FCheckState = " + this.checkStateId +
               ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
               ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
               "' where FSecurityCode = " + dbl.sqlString(this.SecurityCode) +
               " and FRecordDate = " + dbl.sqlDate(this.RecordDate);

         conn.setAutoCommit(false);
         bTrans = true;
         dbl.executeSql(strSql);
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (Exception e) {
         throw new YssException("删除债券计息数据出错", e);
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }

   }

   /**
    * buildFilterSql
    * 筛选条件
    * @throws YssException
    * @return String
    */
   private String buildFilterSql() throws YssException {
      String sResult = "";
      if (this.filterType != null) {
         sResult = " where 1=1";
         if (this.filterType.strIsOnlyColumns.equals("1")) {
            sResult = sResult + " and 1 = 2 ";
            return sResult;
         }
         if (this.filterType.SecurityCode.length() != 0) { 
            sResult = sResult + " and a.FSecurityCode like '" +
                  filterType.SecurityCode.replaceAll("'", "''") + "%'";
         }
         if (this.filterType.RecordDate.length() != 0 &&
             !this.filterType.RecordDate.equals("9998-12-31")) {
            sResult = sResult + " and a.FRecordDate = " +
                  dbl.sqlDate(filterType.RecordDate);
         }
         if (this.filterType.CurCpnDate.length() != 0 &&
             !this.filterType.CurCpnDate.equals("9998-12-31")) {
            sResult = sResult + " and a.FCurCpnDate = " +
                  dbl.sqlDate(filterType.CurCpnDate);
         }
         if (this.filterType.NextCpnDate.length() != 0 &&
             !this.filterType.NextCpnDate.equals("9998-12-31")) {
            sResult = sResult + " and a.FNextCpnDate = " +
                  dbl.sqlDate(filterType.NextCpnDate);
         }
		 //edit by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B
         if (this.filterType.IntAccPer100.compareTo(new BigDecimal("0")) > 0) {
            sResult = sResult + " and a.FIntAccPer100 = " +
                  filterType.IntAccPer100;
         }
         if (this.filterType.IntDay > 0) {
            sResult = sResult + " and a.FIntDay = " +
                  filterType.IntDay;
         }
      }
      return sResult;
   }

   /**
    * getListViewData1
    * 获取债券计息数据
    * @return String
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
         strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FSecurityName as FSecurityName" +
               " from " +
               pub.yssGetTableName("Tb_Para_Bondparamater") + " a " +
               " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
               " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
               
               " left join (select q.FSecurityCode as FSecurityCode,q.FSecurityName as FSecurityName from " +
               pub.yssGetTableName("Tb_Para_Security") + " q join " +
               "(select FSecurityCode,max(FStartDate) as FStartDate from " +
               pub.yssGetTableName("Tb_Para_Security") +
               " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
               " and FCheckState = 1 group by FSecurityCode) r " + " on q.FSecurityCode = r.FSecurityCode and q.FStartDate = r.FStartDate) e on a.FSecurityCode = e.FSecurityCode" +

               buildFilterSql() +
               " order by FCheckState, FCreateTime desc";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
        	//---add by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B start---// 
        	bufShow.append(rs.getString("FSecurityCode")).append("\t");
        	bufShow.append(rs.getString("FSecurityName")).append("\t");
        	bufShow.append(YssFun.formatDate(rs.getDate("FRecordDate"))).append("\t");
        	bufShow.append(YssFun.formatDate(rs.getDate("FCurCpnDate"))).append("\t");
        	bufShow.append(YssFun.formatDate(rs.getDate("FNextCpnDate"))).append("\t");
        	bufShow.append(rs.getBigDecimal("FIntAccPer100") + "").append("\t");
        	bufShow.append(rs.getInt("FIntDay")).append("\t");
        	bufShow.append(rs.getString("FCreator")).append("\t");
        	bufShow.append(rs.getString("FCreateTime")).append("\t");
        	bufShow.append(rs.getString("FCheckUser")).append("\t");
        	bufShow.append(rs.getString("FCheckTime")).append(YssCons.YSS_LINESPLITMARK);
        	//---add by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B end---//
        	//---delete by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B start---//
//            bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
//                  append(YssCons.YSS_LINESPLITMARK);
        	//---delete by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B end---//
            
        	setResultSetAttr(rs);
            
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
      }
      catch (Exception e) {
         throw new YssException("获取债券计息数据出错！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
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
         sHeader = "证券品种\t登记日\t本次起息日\t下次起息日\t百元利息\t已计提天数";
         strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FSecurityName as FSecurityName" +
               " from " +
               pub.yssGetTableName("Tb_Para_Bondparamater") + " a " +
               " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
               " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
               
               " left join (select q.FSecurityCode as FSecurityCode,q.FSecurityName as FSecurityName from " +
               pub.yssGetTableName("Tb_Para_Security") + " q join " +
               "(select FSecurityCode,max(FStartDate) as FStartDate from " +
               pub.yssGetTableName("Tb_Para_Security") +
               " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
               " and FCheckState = 1 group by FSecurityCode) r " + " on q.FSecurityCode = r.FSecurityCode and q.FStartDate = r.FStartDate) e on a.FSecurityCode = e.FSecurityCode" +
               buildFilterSql() +
               " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            bufShow.append( (rs.getString("FSecurityName") + "").trim()).append(
                  "\t");
            bufShow.append(YssFun.formatDate(rs.getDate("FRecordDate"))).append(
                  "\t");
            bufShow.append(YssFun.formatDate(rs.getDate("FCurCpnDate"))).append(
                  "\t");
            bufShow.append(YssFun.formatDate(rs.getDate("FNextCpnDate"))).
                  append(
                  "\t");
            //edit by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B
            bufShow.append(rs.getBigDecimal("FIntAccPer100")).append("\t");
            bufShow.append(rs.getString("FIntDay")).append(YssCons.
                  YSS_LINESPLITMARK);

            setResultSetAttr(rs);

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
      }
      catch (Exception e) {
         throw new YssException("获取债券计息数据出错!", e);
      }
      finally {
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
    * getOperData
    */
   public void getOperData() {
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
    * saveMutliOperData
    *
    * @param sMutilRowStr String
    * @return String
    */
   public String saveMutliOperData(String sMutilRowStr) {
      return "";
   }

   public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
      this.SecurityCode = rs.getString("FSecurityCode") + "";
      this.SecurityName = rs.getString("FSecurityName") + "";
      this.RecordDate = rs.getDate("FRecordDate") + "";
      this.CurCpnDate = rs.getDate("FCurCpnDate") + "";
      this.NextCpnDate = rs.getDate("FNextCpnDate") + "";
      //edit by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B
      this.IntAccPer100 = rs.getBigDecimal("FIntAccPer100");
      this.IntDay = rs.getInt("FIntDay");
      
      super.setRecLog(rs);

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
  public String getBeforeEditData() throws YssException{
	  BondParameterBean befEditBean=new BondParameterBean();
     String strSql = "";
     ResultSet rs=null;
     try {
        strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FSecurityName as FSecurityName" +
                 " from " +
                 pub.yssGetTableName("Tb_Para_Bondparamater") + " a " +
                 " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                 " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                 
                 " left join (select q.FSecurityCode as FSecurityCode,q.FSecurityName as FSecurityName from " +
                 pub.yssGetTableName("Tb_Para_Security") + " q join " +
                 "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                 pub.yssGetTableName("Tb_Para_Security") +
                 " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                 " and FCheckState = 1 group by FSecurityCode) r " + " on q.FSecurityCode = r.FSecurityCode and q.FStartDate = r.FStartDate) e on a.FSecurityCode = e.FSecurityCode" +
                 " where  a.FSecurityCode ="+dbl.sqlString(this.oldSecurityCode) +
                 " and   a.FRecordDate=" + dbl.sqlDate(this.oldRecordDate)+
                 " order by a.FCheckState, a.FCreateTime desc";


      rs = dbl.openResultSet(strSql);
      while (rs.next()) {
        befEditBean.SecurityCode = rs.getString("FSecurityCode") + "";
        befEditBean.SecurityName = rs.getString("FSecurityName") + "";
        befEditBean.RecordDate = rs.getDate("FRecordDate") + "";
        befEditBean.CurCpnDate = rs.getDate("FCurCpnDate") + "";
        befEditBean.NextCpnDate = rs.getDate("FNextCpnDate") + "";
        //edit by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B
        befEditBean.IntAccPer100 = rs.getBigDecimal("FIntAccPer100");
        befEditBean.IntDay = rs.getInt("FIntDay");
        

      }
     return befEditBean.buildRowStr();
     }catch(Exception e)
     {
        throw new YssException(e.getMessage());
     }
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
   public IDataSetting getSetting() throws YssException{
      String strSql="";
      ResultSet rs=null;
      try
      {
         strSql="select * from "+pub.yssGetTableName("Tb_Para_Bondparamater")+
               " where FsecurityCode="+dbl.sqlString(this.SecurityCode);
         rs=dbl.openResultSet(strSql);
         while(rs.next())
         {
        	//edit by songjie 2011.11.02 BUG 2371 QDV4赢时胜(测试)2011年8月3日01_B start
            this.IntAccPer100 = rs.getBigDecimal("FIntAccPer100");
            this.IntDay = rs.getInt("FIntDay");
         }
      }catch(Exception e)
      {
         throw new YssException();
      }finally
      {
            dbl.closeResultSetFinal(rs);
      }
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
    * 从回收站删除数据，即从数据库彻底删除数据
    * @throws YssException
    */
   public void deleteRecycleData() throws YssException {
      String strSql = "";
           String[] arrData=null;
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
                    if (arrData[i].length() == 0)continue;
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                          pub.yssGetTableName("Tb_Para_Bondparamater") +
                          " where FSecurityCode = " +
                          dbl.sqlString(this.SecurityCode) +
                          " and FRecordDate = " + dbl.sqlDate(this.RecordDate);
                    //执行sql语句
                    dbl.executeSql(strSql);
                 }
              }
              
              conn.commit();
              bTrans = false;
              conn.setAutoCommit(true);
           }
           catch (Exception e) {
              throw new YssException("清除数据出错", e);
           }
           finally {
              dbl.endTransFinal(conn, bTrans);
           }

   }


public String getListViewGroupData1() throws YssException {
	// TODO Auto-generated method stub
	return null;
}


public String getListViewGroupData2() throws YssException {
	// TODO Auto-generated method stub
	return null;
}


public String getListViewGroupData3() throws YssException {
	// TODO Auto-generated method stub
	return null;
}


public String getListViewGroupData4() throws YssException {
	// TODO Auto-generated method stub
	return null;
}


public String getListViewGroupData5() throws YssException {
	// TODO Auto-generated method stub
	return null;
}


public String getTreeViewGroupData1() throws YssException {
	// TODO Auto-generated method stub
	return null;
}


public String getTreeViewGroupData2() throws YssException {
	// TODO Auto-generated method stub
	return null;
}


public String getTreeViewGroupData3() throws YssException {
	// TODO Auto-generated method stub
	return null;
}

}
