package com.yss.main.etfoperation;

import com.yss.main.dao.IDataSetting;
import java.sql.ResultSet;
import com.yss.util.YssCons;
import com.yss.util.YssFun;
import com.yss.util.YssException;
import java.sql.Connection;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.etfoperation.pojo.ShareConvertInfoSetBean;
import com.yss.main.parasetting.PortfolioBean;

/**
 * <p>Title: 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A</p>
 *
 * <p>Description: 份额折算后台操作类</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ShareConvertInfoSetAdmin
      extends BaseDataSettingBean implements IDataSetting {

   private ShareConvertInfoSetBean convertInfoSet = null;//份额折算实体bean
   private String sRecycled = "";//保存回收站删除的数据

   public ShareConvertInfoSetAdmin() {
   }
   /**
    * 输入检查：新增，修改，复制数据时检查主键是否重复
    */
   public void checkInput(byte btOper) throws YssException {
      dbFun.checkInputCommon(btOper,
                             pub.yssGetTableName("Tb_ETF_ShareConvert"),
                             "FPortCode,FConvertDate",
                             this.convertInfoSet.getPortCode()+","+this.convertInfoSet.getConvertDate(),
                             this.convertInfoSet.getOldPortCode()+","+this.convertInfoSet.getOldConvertDate());
   }
   /**
    * 新增一条数据
    */
   public String addSetting() throws YssException {
      String strSql = "";
      String strNum = "";
      String strNumDate = "";
      boolean bTrans = false; //代表是否开始了事务
      Connection conn = dbl.loadConnection();
      try {
         conn.setAutoCommit(false);
         bTrans = true;
         strSql = "insert into " + pub.yssGetTableName("Tb_ETF_ShareConvert") +
               "(FPortCode,FConvertDate,FConvertScale,FDESC," +
               " FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" +
               " values(" +
               dbl.sqlString(this.convertInfoSet.getPortCode()) + "," +
                             dbl.sqlDate(this.convertInfoSet.getConvertDate()) + "," +
                             this.convertInfoSet.getConvertScale() + "," +
                             dbl.sqlString(this.convertInfoSet.getDesc()) + "," +
                             (pub.getSysCheckState() ? "0" : "1") + "," +
                             dbl.sqlString(this.convertInfoSet.creatorCode) + "," +
                             dbl.sqlString(this.convertInfoSet.creatorTime) + "," +
                             (pub.getSysCheckState() ? "' '" :
                              dbl.sqlString(this.convertInfoSet.creatorCode)) + "," +
                             (pub.getSysCheckState() ? "' '" :
                              dbl.sqlString(this.convertInfoSet.checkTime)) +
                             ")";

         dbl.executeSql(strSql);
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
         return "";
      }
      catch (Exception e) {
         throw new YssException("新增份额折算信息出错", e);
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }

   }
   /**
    * 修改数据
    */
   public String editSetting() throws YssException {
      String strSql = "";
      boolean bTrans = false; //代表是否开始了事务
      Connection conn = dbl.loadConnection();
      try {
         conn.setAutoCommit(false);
         bTrans = true;
         strSql = "update " + pub.yssGetTableName("Tb_ETF_ShareConvert") +
               " set FPortCode = " + dbl.sqlString(this.convertInfoSet.getPortCode()) +
               ",FConvertDate = " + dbl.sqlDate(this.convertInfoSet.getConvertDate()) +
               ",FConvertScale = " + this.convertInfoSet.getConvertScale() +
               ",FDESC = " + dbl.sqlString(this.convertInfoSet.getDesc()) +
               ",FCheckState = " + dbl.sqlString(pub.getSysCheckState() ? "0" : "1") +
               ",FCreator = " + dbl.sqlString(this.convertInfoSet.creatorCode) +
               ",FCreateTime = " + dbl.sqlString(this.convertInfoSet.creatorTime) +
               " where FPortCode = " + dbl.sqlString(this.convertInfoSet.getOldPortCode())+
                " and FConvertDate ="+ dbl.sqlDate(this.convertInfoSet.getOldConvertDate());
         dbl.executeSql(strSql);
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
         return buildRowStr();
      }
      catch (Exception e) {
         throw new YssException("修改份额折算信息出错", e);
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }
   }
   /**
    * 删除数据，先放到回收站
    */
   public void delSetting() throws YssException {
      Connection conn = dbl.loadConnection();
      boolean bTrans = false;
      String strSql = "";
      try {
         conn.setAutoCommit(false);
         bTrans = true;
         strSql = "update " + pub.yssGetTableName("Tb_ETF_ShareConvert") +
               " set FCheckState = " +  this.convertInfoSet.checkStateId +
               ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
               ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
               " where FPortCode = " + dbl.sqlString(this.convertInfoSet.getPortCode())+
               " and FConvertDate ="+ dbl.sqlDate(this.convertInfoSet.getConvertDate());
         dbl.executeSql(strSql);
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (Exception e) {
         throw new YssException("删除份额折算信息出错", e);
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }
   }
   /**
    * 回收站还原，以及审核，反审核功能
    */
   public void checkSetting() throws YssException {
      String strSql = ""; //定义一个字符串来放SQL语句
      String[] arrData = null; //定义一个字符数组来循环删除
      boolean bTrans = false; //代表是否开始了事务
      Connection conn = dbl.loadConnection(); //打开一个数据库联接
      try {
          conn.setAutoCommit(false); //开启一个事务
          bTrans = true; //代表是否关闭事务
          //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
          if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空
        	  arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
              for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                  if (arrData[i].length() == 0) {
                      continue; //如果数组里没有内容就执行下一个内容
                  }
                  this.parseRowStr(arrData[i]); //解析这个数组里的内容
                  strSql = "update " + pub.yssGetTableName("Tb_ETF_ShareConvert") +
                      " set FCheckState = " + this.convertInfoSet.checkStateId;
                  // 如果是审核操作，则获取审核人代码和审核时间
                  if (this.convertInfoSet.checkStateId == 1) {
                      strSql += ", FCheckUser = '" +
                          pub.getUserCode() + "' , FCheckTime = '" +
                          YssFun.formatDatetime(new java.util.Date()) + "'";
                  }
                  strSql += " where FPortCode = " +
                      dbl.sqlString(this.convertInfoSet.getPortCode())+
                      " and FConvertDate = "+ dbl.sqlDate(this.convertInfoSet.getConvertDate());
                  dbl.executeSql(strSql); //执行更新操作
              }
          }
          conn.commit(); //提交事务
          bTrans = false;
          conn.setAutoCommit(true);
      } catch (Exception e) {
          throw new YssException("审核份额折算信息出错", e);
      } finally {
          dbl.endTransFinal(conn, bTrans); //释放资源
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
   /**
    * 回收站清除功能
    */
   public void deleteRecycleData() throws YssException {
      String strSql = ""; //定义一个放SQL语句的字符串
      String[] arrData = null; //定义一个字符数组来循环删除
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
                     pub.yssGetTableName("Tb_ETF_ShareConvert") +
                     " where FPortCode = " +
                     dbl.sqlString(this.convertInfoSet.getPortCode())+
                     " and FConvertDate =" + dbl.sqlDate(this.convertInfoSet.getConvertDate()); //SQL语句
               //执行sql语句
               dbl.executeSql(strSql);
            }
         }
         //sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
         else if (this.convertInfoSet.getPortCode() != null &&
                  this.convertInfoSet.getPortCode() != "") {
            strSql = "delete from " +
                  pub.yssGetTableName("Tb_ETF_ShareConvert") +
                  " where FPortCode = " +
                  dbl.sqlString(this.convertInfoSet.getPortCode())+
                  " and FConvertDate = "+ dbl.sqlDate(this.convertInfoSet.getConvertDate()); //SQL语句
            //执行sql语句
            dbl.executeSql(strSql);
         }
         conn.commit(); //提交事物
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (Exception e) {
         throw new YssException("清除数据出错", e);
      }
      finally {
         dbl.endTransFinal(conn, bTrans); //释放资源
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
    * 查询数据
    */
   public String getListViewData1() throws YssException {
      String strSql = "";//定义一个存放sql语句的字符串
      try{
          strSql = "select y.* from " +
                " (select a.*,b.FPortName,c.FUserName as FCreatorName, d.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_ETF_ShareConvert") +
                " a left join (select FPortCode,FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " ) b on a.FPortCode = b.FPortCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                buildFilterSql() + ") y order by y.FCheckState, y.FCreateTime desc";

      }catch(Exception e){
           throw new YssException("获取份额折算数据出错！" + "\r\n" + e.getMessage(), e);
      }
        return this.builderListViewData(strSql);
   }

   /**
    * buildFilterSql
    *筛选条件
    * @return String
    */
   private String buildFilterSql() {
      String sResult = "";
      ShareConvertInfoSetBean filterType = this.convertInfoSet.getFilterType();
      if (filterType != null) {
          sResult = " where 1=1";
          if (filterType.getPortCode().length() != 0) {
              sResult = sResult + " and a.FPortCode like '" +
                  filterType.getPortCode().replaceAll("'", "''") + "%'";
          }
          if (filterType.getConvertScale() != null) {
              sResult = sResult + " and a.FConvertScale = " +
                  filterType.getConvertScale();
          }
          if (filterType.getConvertDate().length() != 0 &&
              ! (filterType.getConvertDate().equals("9998-12-31") ||
                 filterType.getConvertDate().equals("1900-01-01"))) {
              sResult = sResult + " and a.FConvertDate >= " +
                  dbl.sqlDate(filterType.getConvertDate());
          }
      }
        return sResult;
   }

   /**
    * builderListViewData 按照一定规则拼接查询出的数据
    *
    * @param strSql String
    * @return String
    */
   private String builderListViewData(String strSql) throws YssException {
      String sHeader = "";
      String sShowDataStr = "";
      String sAllDataStr = "";

      StringBuffer bufShow = new StringBuffer();
      StringBuffer bufAll = new StringBuffer();
      ResultSet rs = null;
      try {
          sHeader = this.getListView1Headers();
          if (strSql == "") {
              return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                  "\r\f" +
                  this.getListView1ShowCols();
          }
          rs = dbl.openResultSet(strSql);
          while (rs.next()) {
              bufShow.append(super.buildRowShowStr(rs,
                  this.getListView1ShowCols())).
                  append(YssCons.YSS_LINESPLITMARK);
              this.convertInfoSet.setShareConvertInfoAttr(rs);
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
          return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
              this.getListView1ShowCols();
      } catch (Exception e) {
          throw new YssException("获取份额折算信息设置数据出错！", e);
      } finally {
          dbl.closeResultSetFinal(rs);
      }
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
    * 解析前台传来的数据
    */
   public void parseRowStr(String sRowStr) throws YssException {
      if (convertInfoSet == null) {
         convertInfoSet = new ShareConvertInfoSetBean();
         convertInfoSet.setYssPub(pub);
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
      convertInfoSet.parseRowStr(sRowStr);
      sRecycled = sRowStr;
   }
   /**
    * 拼接数据
    */
   public String buildRowStr() throws YssException {
      return convertInfoSet.buildRowStr();
   }
   /**
    * 单独处理前台功能调用
    */
   public String getOperValue(String sType) throws YssException {
	   String sTAConvert="";//保存判断当天是否有份额折算数据的返回值用“true”,“false”
	   String scheckPortCode="";//判断净值统计时所选组合是不是ETF组合
	   try{
		   if(sType.equalsIgnoreCase("chbTAConvert")){
			   sTAConvert=getConvertInfo();//判断当天是否有份额折算数据
			   return sTAConvert;
		   }
		   if(sType.equalsIgnoreCase("checkPortCode")){
			   scheckPortCode=getPortCode();
			   return scheckPortCode;
		   }
	   }catch(Exception e){
		   throw new YssException(e.getMessage());
	   }
//      if (sType != null && sType.equalsIgnoreCase("calBondInterest")) { //通过点击窗口按钮计算应计转出债券利息的方法
//         BaseBondOper bondOper = null;
//         BaseOperDeal operDeal = new BaseOperDeal();
//         operDeal.setYssPub(pub);
//         YssBondIns bondIns = new YssBondIns();
//         bondOper = operDeal.getSpringRe(this.convertInfoSet.getStrSecurityCode(),
//                                         "Sell"); //生成BaseBondOper
//         bondIns.setInsType("Sell");
//         if (bondOper == null) {
//            return "";
//         }
//         bondIns.setSecurityCode(this.convertInfoSet.getStrSecurityCode());
//         bondIns.setInsDate(YssFun.toDate(this.convertInfoSet.getStrBargainDate()));
//         bondIns.setInsAmount(this.convertInfoSet.getDbOutAmount()); //转出数量
//         bondIns.setPortCode(this.convertInfoSet.getStrPortCode());
//         bondOper.setYssPub(pub);
//         bondOper.init(bondIns);
//         this.convertInfoSet.setDbOutInverest(bondOper.calBondInterest()); //转出债券利息
//      }
//      else if (sType != null && sType.equalsIgnoreCase("calValInc")) { //通过点击窗口按钮计算债券转出估值增值的方法
//         BaseAvgCostCalculate avgCostValInc = new BaseAvgCostCalculate();
//         SecPecPayBean pay = null;
//         avgCostValInc.setYssPub(pub);
//         avgCostValInc.initCostCalcutate(YssFun.toDate(this.convertInfoSet.
//               getStrBargainDate()),
//                                         this.convertInfoSet.getStrPortCode(),
//                                         this.convertInfoSet.getStrInvMgrCode(),
//                                         "", "");
//         pay = avgCostValInc.getCarryRecPay(this.convertInfoSet.getStrSecurityCode(),
//                                            this.convertInfoSet.getDbOutAmount(),
//                                            this.convertInfoSet.getStrTradeNo(),
//                                            "", "", YssOperCons.YSS_ZJDBLX_MV,
//                                            "");
//         if (pay == null) {
//            return "";
//         }
//         this.convertInfoSet.setDbOutValInc(pay.getMoney()); //转出估值增值
//      }
//      else if (sType != null && sType.equalsIgnoreCase("calCarryCost")) { //转出成本
//         if (this.convertInfoSet.getStrTradeNo().length() == 0) {
//            String strNumDate = YssFun.formatDatetime(YssFun.toDate(this.
//                  convertInfoSet.
//                  getStrBargainDate())).substring(0, 8);
//            String sNum = strNumDate +
//                  dbFun.getNextInnerCode(pub.yssGetTableName(
//                        "Tb_Data_DevTrustBond"),
//                                         dbl.sqlRight("FNUM", 9),
//                                         "000000000",
//                                         " where FNum like 'DTB"
//                                         + strNumDate + "%'", 1);
//            sNum = "DTB" + sNum;
//            this.convertInfoSet.setStrTradeNo(sNum);
//         }
//         ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
//               "avgcostcalculate");
//
//         costCal.initCostCalcutate(YssFun.toDate(convertInfoSet.getStrBargainDate()),
//                                   convertInfoSet.getStrPortCode(),
//                                   convertInfoSet.getStrInvMgrCode(),
//                                   "",
//                                   "");
//         costCal.setYssPub(pub);
//         //获取冲减的成本
//         YssCost cost = costCal.getCarryCost(convertInfoSet.getStrSecurityCode(),
//                                             convertInfoSet.getDbOutAmount(),
//                                             this.convertInfoSet.getStrTradeNo(),
//                                             "devTrustbond",
//                                             YssOperCons.YSS_JYLX_Buy + "," +
//                                             YssOperCons.YSS_JYLX_Sale + "," +
//                                             YssOperCons.YSS_JYLX_YHJZQCX);
//         costCal.roundCost(cost, 2);
//         convertInfoSet.setDbOutMoney(cost.getCost());
//      }

      return buildRowStr();
   }
    /**
     * 判断净值统计时所选组合是不是ETF组合
     * @return
     */
   	private String getPortCode() throws YssException{
   		String scheckPortCode="";//判断净值统计时所选组合是不是ETF组合的返回值是组合的资产子类型
   		ResultSet rs=null;
   		StringBuffer buff=null;
   		try{
   			
   			buff=new StringBuffer(50);
   			buff.append(" select * from ").append(pub.yssGetTableName("Tb_Para_Portfolio"));
   			buff.append(" where FCheckState = 1 and FPortCode =");
   			
   		}catch(Exception e){
   			throw new YssException("判断净值统计时所选组合是不是ETF组合出错！",e);
   		}finally{
   			dbl.closeResultSetFinal(rs);
   		}
   		return scheckPortCode;
}

	/**
   	 * 此方法是判断当天是否有份额折算数据，有返回“true”,否则返回“false”
   	 * @return
   	 * @throws YssException
   	 */
	private String getConvertInfo() throws YssException{
		String sTAConvert="false";
		ResultSet rs=null;
		StringBuffer buff=null;
		try{
			buff=new StringBuffer(20);
			buff.append(" select * from ").append(pub.yssGetTableName("Tb_ETF_ShareConvert"));
			buff.append(" where FPortCode =").append(dbl.sqlString(this.convertInfoSet.getPortCode()));
			buff.append(" and FConvertDate =").append(dbl.sqlDate(this.convertInfoSet.getConvertDate()));
			buff.append(" and FCheckState = 1");
			
			rs=dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			if(rs.next()){
				sTAConvert="true";
			}
		}catch(Exception e){
			throw new YssException("获取份额折算数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return sTAConvert;
	}
}









