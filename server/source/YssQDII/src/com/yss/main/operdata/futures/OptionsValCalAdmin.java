package com.yss.main.operdata.futures;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.*;

import java.sql.ResultSet;
import java.sql.Connection;
import com.yss.main.operdata.futures.pojo.BailMoneyValCalBean;
import java.sql.Statement;

/**
 * <p> Twice edit xuqiji 20090810 QDV4招商证券2009年07月06日01_A  MS00562 期权和期货结算估值的保证金账户需要独立的界面让用户指定</p>
 * <p>Title: xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持</p>
 *
 * <p>Description:此类为期权估值核算表 TB_xxx_data_OptionsValCal 的实体操作类 更改为--期权和期货保证金账户设置操作类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OptionsValCalAdmin extends BaseDataSettingBean
      implements IDataSetting {
      private OptionsValCalAdmin filterType;//用于筛选
      private BailMoneyValCalBean bailMoneyValCal=new BailMoneyValCalBean();//创建bean对象
      private String sRecycled = ""; //回收站数据
    public OptionsValCalAdmin() {
    }

    /**
     * 清除数据
     * @param dDate Date
     * @throws YssException
     */
    public void deleteData(String sSecurityCode, String sPortCode) throws YssException {
        String strSql = "";
        try {
            strSql = "DELETE FROM " + pub.yssGetTableName("Tb_Data_OptionsValCal") +
                " WHERE FSecurityCode = " + dbl.sqlString(sSecurityCode)
                + " and FPortCode=" + dbl.sqlString(sPortCode);
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("删除期权估值核算表数据出错\r\n", e);
        }
  }
  /**
   * 保存期权估值核算表
   * @param alEntityData ArrayList
   * @param conn Connection
   * @return String
   */
  public String saveMutliSetting(ResultSet rs) throws YssException {
      Connection conn = null;
      boolean bTrans = true;
      StringBuffer buff = null;
      try {
          buff = new StringBuffer();
          buff.append("insert into ").append(pub.yssGetTableName("Tb_Data_OptionsValCal")).append("(");
          buff.append("FSecurityCode,FPortCode,FCashAccCode,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime");
          buff.append(")values(").append(dbl.sqlString(rs.getString("FSecurityCode")));
          buff.append(",").append(dbl.sqlString(rs.getString("FPortCode")));
          buff.append(",").append(dbl.sqlString(rs.getString("FChageBailAcctCode")));
          buff.append(",").append(rs.getDouble("FCheckState"));
          buff.append(",").append(dbl.sqlString(rs.getString("FCreator"))).append(",").append(dbl.sqlString(rs.getString("FCreateTime")));
          buff.append(",").append(dbl.sqlString(rs.getString("FCheckUser"))).append(",")
              .append(dbl.sqlString(rs.getString("FCheckTime"))).append(")");
          conn = dbl.loadConnection();
          conn.setAutoCommit(false);
          dbl.executeSql(buff.toString());
          buff.delete(0, buff.length());
          conn.commit();
          conn.setAutoCommit(true);
          bTrans = false;
      } catch (Exception e) {
          throw new YssException("插入保存期权估值核算表出错！\r\t", e);
      } finally {
          dbl.endTransFinal(conn, bTrans);
      }
      return "";
   }

   /**
    * #1279 使用当前版本设置【期权期货保证金账户设置】时错误 添加交易所代码  add by jiangshichao 2011.03.22
    *新增，复制，修改，审核，反审核数据的限制功能
    * @param btOper byte
    * @throws YssException
    */
   public void checkInput(byte btOper) throws YssException {
       dbFun.checkInputCommon(btOper,pub.yssGetTableName("Tb_Data_OptionsValCal"),
                              "FCashAccCode,FPortCode,FMarkType,FEXCHAGECODE,FStartCashAccCode,FBROKERCODE",
                              this.bailMoneyValCal.getSCashAccCode()+","+this.bailMoneyValCal.getSPortCode()+","+this.bailMoneyValCal.getSMarkType()+","+this.bailMoneyValCal.getSExchageCode() 
                              + "," + this.bailMoneyValCal.getStartCashAccCode() + "," + this.bailMoneyValCal.getBrokeCode(),
                              this.bailMoneyValCal.getSOldCashAccCode()+","+this.bailMoneyValCal.getSOldPortCode()+","+this.bailMoneyValCal.getSOldMarkType()+","+this.bailMoneyValCal.getsOldExchageCode()
                              + "," + this.bailMoneyValCal.getOldStartCashAccCode() + "," + this.bailMoneyValCal.getOldBrokeCode());

    }
    /**
     * 新增数据
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = true;
        StringBuffer buff = null;
        try {
            buff = new StringBuffer();
            buff.append("insert into ").append(pub.yssGetTableName("Tb_Data_OptionsValCal")).append("(");
            buff.append("FCashAccCode,FPortCode,FExchageCode,FMarkType,");
            buff.append("FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime");
            buff.append(",FBROKERCODE,FStartCashAccCode"); //add by huangqirong 2012-12-07 story #3371
            buff.append(")values(");
            buff.append(dbl.sqlString(this.bailMoneyValCal.getSCashAccCode()));
            buff.append(",").append(dbl.sqlString(this.bailMoneyValCal.getSPortCode()));
            buff.append(",").append(dbl.sqlString(this.bailMoneyValCal.getSExchageCode()));
            buff.append(",").append(YssFun.toInt(this.bailMoneyValCal.getSMarkType()));
            buff.append(",").append(dbl.sqlString(this.bailMoneyValCal.getSDesc()));
            buff.append(",").append((pub.getSysCheckState() ? "0" : "1"));
            buff.append(",").append(dbl.sqlString(this.creatorCode));
            buff.append(",").append(dbl.sqlString(this.creatorTime));
            buff.append(",").append((pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)));
            //modify by huangqirong 2012-12-07 story #3371
            buff.append(",").append(pub.getSysCheckState() ? "' '" :dbl.sqlString(this.checkTime));            
            buff.append(",").append(dbl.sqlString(this.bailMoneyValCal.getBrokeCode()));
            buff.append(",").append(dbl.sqlString(this.bailMoneyValCal.getStartCashAccCode()));
            buff.append(")");
            //---end---
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            dbl.executeSql(buff.toString());
            buff.delete(0, buff.length());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("新增数据出错！", e);
        }finally{
            dbl.endTransFinal(conn,bTrans);
        }
        return this.buildRowStr();
    }
    /**
     * 修改数据
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        StringBuffer buff=null;
        try{
            buff=new StringBuffer();
            buff.append(" update ");
            buff.append(pub.yssGetTableName("Tb_Data_OptionsValCal"));
            buff.append(" set FCashAccCode =").append(dbl.sqlString(this.bailMoneyValCal.getSCashAccCode()));
            buff.append(" ,FPortCode =").append(dbl.sqlString(this.bailMoneyValCal.getSPortCode()));
            buff.append(" ,FExchageCode =").append(dbl.sqlString(this.bailMoneyValCal.getSExchageCode()));
            buff.append(" ,FMarkType =").append(YssFun.toInt(this.bailMoneyValCal.getSMarkType()));
            buff.append(" ,FDesc =").append(dbl.sqlString(this.bailMoneyValCal.getSDesc()));
            buff.append(" ,FCHECKSTATE =").append((pub.getSysCheckState() ? "0" : "1"));
            buff.append(" ,FCreator = ").append(dbl.sqlString(this.creatorCode));
            buff.append(" ,FCreateTime = ").append(dbl.sqlString(this.creatorTime));
            buff.append(" ,FCheckUser = ").append((pub.getSysCheckState() ? "' '" :dbl.sqlString(this.creatorCode)));
            buff.append(" ,FCheckTime =").append(dbl.sqlString(this.checkTime));
            buff.append(",FStartCashAccCode = ").append(dbl.sqlString(this.bailMoneyValCal.getStartCashAccCode()));//modify by huangqirong 2012-12-07 story #3371
            buff.append(",FBROKERCODE = ").append(dbl.sqlString(this.bailMoneyValCal.getBrokeCode()));	//modify by huangqirong 2012-12-07 story #3371
            buff.append(" where FCashAccCode =").append(dbl.sqlString(this.bailMoneyValCal.getSOldCashAccCode()));
            buff.append(" and FPortCode =").append(dbl.sqlString(this.bailMoneyValCal.getSOldPortCode()));
            buff.append(" and FMarkType =").append(YssFun.toInt(this.bailMoneyValCal.getSOldMarkType()));
            buff.append(" and FEXCHAGECODE =").append(dbl.sqlString(this.bailMoneyValCal.getsOldExchageCode()));//#1279 使用当前版本设置【期权期货保证金账户设置】时错误  add by jiangshichao 2011.03.22
            buff.append(" and FBROKERCODE = ").append(dbl.sqlString(this.bailMoneyValCal.getOldBrokeCode()));	//modify by huangqirong 2012-12-07 story #3371
            buff.append(" and FStartCashAccCode = ").append(dbl.sqlString(this.bailMoneyValCal.getOldStartCashAccCode()));//modify by huangqirong 2012-12-07 story #3371
            conn.setAutoCommit(false);
            dbl.executeSql(buff.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }catch(Exception e){
            throw new YssException("修改数据出错！",e);
        }finally{
            dbl.endTransFinal(conn,bTrans);
        }
        return this.buildRowStr();
    }
    /**
     * 删除数据，先放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        StringBuffer buff = null;
        try{
            buff=new StringBuffer();
            buff.append(" update ");
            buff.append(pub.yssGetTableName("Tb_Data_OptionsValCal"));
            buff.append(" set FCheckState =").append(this.checkStateId);
            buff.append(" ,FCheckUser =").append(dbl.sqlString(pub.getUserCode()));
            buff.append(" ,FCheckTime =").append(dbl.sqlString(this.checkTime));
            buff.append(" where FCashAccCode =").append(dbl.sqlString(this.bailMoneyValCal.getSOldCashAccCode()));
            buff.append(" and FPortCode =").append(dbl.sqlString(this.bailMoneyValCal.getSOldPortCode()));
            buff.append(" and FMarkType =").append(YssFun.toInt(this.bailMoneyValCal.getSOldMarkType()));
            buff.append(" and FEXCHAGECODE =").append(dbl.sqlString(this.bailMoneyValCal.getsOldExchageCode()));//#1279 使用当前版本设置【期权期货保证金账户设置】时错误  add by jiangshichao 2011.03.22
            buff.append(" and FBROKERCODE = ").append(dbl.sqlString(this.bailMoneyValCal.getOldBrokeCode()));	//modify by huangqirong 2012-12-07 story #3371
            buff.append(" and FStartCashAccCode = ").append(dbl.sqlString(this.bailMoneyValCal.getOldStartCashAccCode()));//modify by huangqirong 2012-12-07 story #3371
            conn.setAutoCommit(false);
            dbl.executeSql(buff.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }catch(Exception e){
            throw new YssException("删除数据出错！",e);
        }finally{
            dbl.endTransFinal(conn,bTrans);
        }
    }

    /**
     * 审核，反审核方法,回收站还原功能
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        StringBuffer buff = null;
        String[] array = null;
        boolean bTrans = true;
        Connection conn = dbl.loadConnection();
        Statement st = null;
        try {
            buff = new StringBuffer();
            conn.setAutoCommit(false);
            if (null != sRecycled && !"".equalsIgnoreCase(sRecycled.trim())) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                array = sRecycled.split("\r\n");
                st = conn.createStatement();
                for (int i = 0; i < array.length; i++) { //循环执行数据还原
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    buff.append(" update ");
                    buff.append(pub.yssGetTableName("Tb_Data_OptionsValCal"));
                    buff.append(" set FCheckState =").append(this.checkStateId);
                    buff.append(" ,FCheckUser =").append(dbl.sqlString(pub.getUserCode()));
                    buff.append(" ,FCheckTime =").append(dbl.sqlString(this.checkTime));
                    buff.append(" where FCashAccCode =").append(dbl.sqlString(this.bailMoneyValCal.getSOldCashAccCode()));
                    buff.append(" and FPortCode =").append(dbl.sqlString(this.bailMoneyValCal.getSOldPortCode()));
                    buff.append(" and FMarkType =").append(YssFun.toInt(this.bailMoneyValCal.getSOldMarkType()));
                    buff.append(" and FEXCHAGECODE =").append(dbl.sqlString(this.bailMoneyValCal.getsOldExchageCode()));//#1279 使用当前版本设置【期权期货保证金账户设置】时错误  add by jiangshichao 2011.03.22
                    buff.append(" and FBROKERCODE = ").append(dbl.sqlString(this.bailMoneyValCal.getOldBrokeCode()));	//modify by huangqirong 2012-12-07 story #3371
                    buff.append(" and FStartCashAccCode = ").append(dbl.sqlString(this.bailMoneyValCal.getOldStartCashAccCode()));//modify by huangqirong 2012-12-07 story #3371
                    st.addBatch(buff.toString());
                    buff.delete(0,buff.length());
                }
                st.executeBatch();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("还原保证金账户设置信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
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
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        StringBuffer buff = null;
        String[] array = null;
        boolean bTrans = true; //代表是否开始事务
        Statement st = null;
        Connection conn = dbl.loadConnection();
        try {
            buff = new StringBuffer();
            if (null != sRecycled && !"".equalsIgnoreCase(sRecycled.trim())) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                array = sRecycled.split("\r\n"); //根据规定的符号，把多个sql语句分别放入数组
                conn.setAutoCommit(false);
                st = conn.createStatement();
                for (int i = 0; i < array.length; i++) { //循环执行这些删除语句
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    buff.append("delete from ");
                    buff.append(pub.yssGetTableName("Tb_Data_OptionsValCal"));
                    buff.append(" where FCashAccCode =").append(dbl.sqlString(this.bailMoneyValCal.getSCashAccCode()));
                    buff.append(" and FPortCode =").append(dbl.sqlString(this.bailMoneyValCal.getSPortCode()));
                    buff.append(" and FMarkType =").append(YssFun.toInt(this.bailMoneyValCal.getSMarkType()));
                    //edit by songjie 2011.07.28 BUG 2172 QDV4赢时胜(测试)2011年6月28日02_B 清除数据报错
                    buff.append(" and FEXCHAGECODE =").append(dbl.sqlString(this.bailMoneyValCal.getSExchageCode()));//#1279 使用当前版本设置【期权期货保证金账户设置】时错误  add by jiangshichao 2011.03.22
                    buff.append(" and FBROKERCODE = ").append(dbl.sqlString(this.bailMoneyValCal.getOldBrokeCode()));	//modify by huangqirong 2012-12-07 story #3371
                    buff.append(" and FStartCashAccCode = ").append(dbl.sqlString(this.bailMoneyValCal.getOldStartCashAccCode()));//modify by huangqirong 2012-12-07 story #3371
                    st.addBatch(buff.toString());
                    buff.delete(0,buff.length());
                }
                st.executeBatch();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
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
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        StringBuffer buff=null;
        ResultSet rs=null;
        String sHeader = "";
        String sVocStr = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try{
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
           //add by yangheng 20100818 MS01310  分页无法显示  QDV4赢时胜(测试)2010年06月18日
            if (this.filterType!=null&&this.filterType.bailMoneyValCal.getStrIsOnlyColumns().equals("0")) {
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_BAILMONEY_SET);
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                    "\r\f" + this.getListView1ShowCols()+ "\r\f"+ yssPageInationBean.buildRowStr() + "\r\f" + "voc" + sVocStr;// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            buff=new StringBuffer();
            buff.append(" select options.*,cashacc.fcashaccname,port.FPortName,e.FVocName as MarkType,");
            buff.append(" ex.fexchangename as FExchageName,b.FUserName as FCreatorName, c.FUserName as FCheckUserName");
            buff.append(" ,nvl(startacc.fcashaccname,' ') as FStartCashName, nvl(broker.fbrokername,' ') as fbrokername");//add by huangqirong 2012-12-07 story #3371 
            buff.append(" from ");
            buff.append(pub.yssGetTableName("tb_data_optionsvalcal")).append(" options ");
            buff.append(" left join (select FUserCode,FUserName from ");
            buff.append(pub.yssGetTableName("Tb_Sys_UserList"));
            buff.append(") b on options.FCreator = b.FUserCode");
            buff.append(" left join (select FUserCode,FUserName from ");
            buff.append(pub.yssGetTableName("Tb_Sys_UserList"));
            buff.append(") c on options.FCheckUser = c.FUserCode ");
            buff.append(" left join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Para_CashAccount"));
            buff.append(" where FCheckState = 1) cashacc on options.fcashacccode =cashacc.fcashacccode");
            
            //------ modify by wangzuochun  2010.07.16  MS01449    组合代码相同而启用日期不同的组合时，新建买入证券据，进行库存统计后，现金库存会增倍 QDV4赢时胜(测试)2010年7月15日01_B 
            //----------------------------------------------------------------------------------------------------
            buff.append(" left join (");//edit by songjie 2011.03.15 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//            buff.append(pub.yssGetTableName("Tb_Para_Portfolio"));
//            buff.append(" where FStartDate <= " + dbl.sqlDate(new java.util.Date()));
            //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
            buff.append(" select FPortCode, FPortName, FStartDate, FPortCury from ");//edit by songjie 2011.03.15 不以最大的启用日期查询数据
            buff.append(pub.yssGetTableName("Tb_Para_Portfolio"));
            buff.append(" where FCheckState = 1) port on options.FPortCode = port.FPortCode ");//edit by songjie 2011.03.15 不以最大的启用日期查询数据
            //-------------------------------------------- MS01449 -------------------------------------------//
            
//            buff.append(" left join (select * from ");
//            buff.append(pub.yssGetTableName("Tb_Para_Portfolio"));
//            buff.append(" where FCheckState = 1) port on options.fportcode = port.fportcode");
            
            buff.append("  left join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Base_Exchange"));
            buff.append(" where FCheckState = 1) ex on options.fexchagecode = ex.fexchangecode");
            buff.append(" left join Tb_Fun_Vocabulary e on ").append("options.FMarkType = e.FVocCode and  e.FVocTypeCode = ");
    	    buff.append(dbl.sqlString(YssCons.YSS_BAILMONEY_SET));
    	    //add by huangqirong 2012-12-07 story #3371
    	    buff.append(" left join (select * from " + pub.yssGetTableName("Tb_Para_CashAccount"));  
    	    buff.append(" where FCheckState = 1) startacc on options.fstartcashacccode = startacc.fcashacccode ") ;
    	    buff.append(" left join (select * from " + pub.yssGetTableName("Tb_Para_Broker")); 
    	    buff.append(" where FCheckState = 1 ) broker on options.fbrokercode = broker.fbrokercode ");
    	    //---end---
            buff.append(this.buildFilterSql());
            
            //QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            //rs=dbl.openResultSet(buff.toString());
			yssPageInationBean.setsQuerySQL(buff.toString());
			yssPageInationBean.setsTableName("optionsvalcal");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            while(rs.next()){
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.bailMoneyValCal.setSCashAccCode(rs.getString("FCashAccCode"));
                this.bailMoneyValCal.setSCashAccName(rs.getString("fcashaccname"));
                this.bailMoneyValCal.setSPortCode(rs.getString("FPortCode"));
                this.bailMoneyValCal.setSPortName(rs.getString("FPortName"));
                this.bailMoneyValCal.setSExchageCode(rs.getString("FExchageCode"));
                this.bailMoneyValCal.setSExchageName(rs.getString("FExchageName"));
                this.bailMoneyValCal.setSMarkType(Integer.toString(rs.getInt("FMarkType")));
                this.bailMoneyValCal.setSDesc(rs.getString("FDesc"));
                //add by huangqirong 2012-12-07 story #3371
                this.bailMoneyValCal.setStartCashAccCode(rs.getString("FStartCashAccCode"));
                this.bailMoneyValCal.setBrokeCode(rs.getString("FBROKERCODE"));
                this.bailMoneyValCal.setStartCashAccName(rs.getString("FStartCashName"));
                this.bailMoneyValCal.setBrokeName(rs.getString("FBrokername"));
                //---end---
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
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_BAILMONEY_SET);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + this.getListView1ShowCols()+ "\r\f"+ yssPageInationBean.buildRowStr() + "\r\f" + "voc" + sVocStr;// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
        }catch(Exception e){
            throw new YssException("查询数据出错！",e);
        }finally{
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    /**
     * 拼接SQL语句条件
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.bailMoneyValCal.getStrIsOnlyColumns().equals("0")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.filterType.bailMoneyValCal.getSCashAccCode().length() != 0) {
                sResult = sResult + " and options.FCashAccCode = '" +
                    filterType.bailMoneyValCal.getSCashAccCode().replaceAll("'", "''") + "'";
            }
            if (this.filterType.bailMoneyValCal.getSPortCode().length() != 0) {
                sResult = sResult + " and options.FPortCode = '" +
                    filterType.bailMoneyValCal.getSPortCode().replaceAll("'", "''") + "'";
            }
            if (this.filterType.bailMoneyValCal.getSExchageCode().length() != 0) {
                sResult = sResult + " and options.FExchageCode = '" +
                    filterType.bailMoneyValCal.getSExchageCode().replaceAll("'", "''") + "'";
            }
            if (! (this.filterType.bailMoneyValCal.getSMarkType().trim().equals("99") || this.filterType.bailMoneyValCal.getSMarkType().trim().length() == 0)) {
            	sResult = sResult + " and options.FMarkType = " +
                			YssFun.toInt(filterType.bailMoneyValCal.getSMarkType());
            }
            //add by huangqirong 2012-12-07 story #3371 
            if (this.filterType.bailMoneyValCal.getStartCashAccCode().trim().length() != 0) {
            	sResult = sResult + " and options.FStartCashAccCode = " +
                			dbl.sqlString(filterType.bailMoneyValCal.getStartCashAccCode());
            }
            if (this.filterType.bailMoneyValCal.getBrokeCode().trim().length() != 0) {
            	sResult = sResult + " and options.FBROKERCODE = " +
            				dbl.sqlString(filterType.bailMoneyValCal.getBrokeCode());
            }
            //---end---
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
     * 解析前台传来的数据
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try{
            if (sRowStr.trim().length() == 0)
            return;
         if (sRowStr.indexOf("\r\t") >= 0) {
            sTmpStr = sRowStr.split("\r\t")[0];
         }
         else {
            sTmpStr = sRowStr;
         }
         sRecycled=sRowStr; //把未解析的字符串先赋给sRecycled
         reqAry = sTmpStr.split("\t");
         this.bailMoneyValCal.setSCashAccCode(reqAry[0]);
         this.bailMoneyValCal.setSCashAccName(reqAry[1]);
         this.bailMoneyValCal.setSPortCode(reqAry[2]);
         this.bailMoneyValCal.setSPortName(reqAry[3]);
         this.bailMoneyValCal.setSExchageCode(reqAry[4]);
         this.bailMoneyValCal.setSExchageName(reqAry[5]);
         this.bailMoneyValCal.setSMarkType(reqAry[6]);
         this.bailMoneyValCal.setSDesc(reqAry[7]);
         this.bailMoneyValCal.setSOldCashAccCode(reqAry[8]);
         this.bailMoneyValCal.setSOldPortCode(reqAry[9]);
         this.bailMoneyValCal.setSOldMarkType(reqAry[10]);
         //#1279 使用当前版本设置【期权期货保证金账户设置】时错误  add by jiangshichao 2011.03.22
         this.bailMoneyValCal.setsOldExchageCode(reqAry[11]);
         this.checkStateId=YssFun.toInt(reqAry[12]);
         this.bailMoneyValCal.setStrIsOnlyColumns(reqAry[13]);
         //add by huangqirong 2012-12-07 story #3371 
         this.bailMoneyValCal.setStartCashAccCode(reqAry[14]);
         this.bailMoneyValCal.setStartCashAccName(reqAry[15]);
         this.bailMoneyValCal.setBrokeCode(reqAry[16]);
         this.bailMoneyValCal.setBrokeName(reqAry[17]);
         this.bailMoneyValCal.setOldStartCashAccCode(reqAry[18]);
         this.bailMoneyValCal.setOldBrokeCode(reqAry[19]);         
         //---end---
         //#1279 使用当前版本设置【期权期货保证金账户设置】时错误 end ---------------------------
         super.parseRecLog();
         if (sRowStr.indexOf("\r\t") >= 0) {
            if (this.filterType == null) {
               this.filterType = new OptionsValCalAdmin();
               this.filterType.setYssPub(pub);
            }
            this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
         }
        }catch(Exception e){
            throw new YssException("解析数据出错！",e);
        }
    }
    /**
     * 拼接数据返回前台
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buff=new StringBuffer();
        buff.append(this.bailMoneyValCal.getSCashAccCode()).append("\t");
        buff.append(this.bailMoneyValCal.getSCashAccName()).append("\t");
        buff.append(this.bailMoneyValCal.getSPortCode()).append("\t");
        buff.append(this.bailMoneyValCal.getSPortName()).append("\t");
        buff.append(this.bailMoneyValCal.getSExchageCode()).append("\t");
        buff.append(this.bailMoneyValCal.getSExchageName()).append("\t");
        buff.append(this.bailMoneyValCal.getSMarkType()).append("\t");
        buff.append(this.bailMoneyValCal.getSDesc()).append("\t");
        //add by huangqirong 2012-12-07 story #3371
        buff.append(this.bailMoneyValCal.getStartCashAccCode()).append("\t");
        buff.append(this.bailMoneyValCal.getStartCashAccName()).append("\t");
        buff.append(this.bailMoneyValCal.getBrokeCode()).append("\t");
        buff.append(this.bailMoneyValCal.getBrokeName()).append("\t");
        //---end---
        buff.append(super.buildRecLog());
        return buff.toString();
    }

    public String getOperValue(String sType) throws YssException {
    	String sReturnData = "";
    	try{
    		if(sType!=null&&sType.indexOf("getCashAccCodePort")!= -1){
    			sReturnData = getCashAccCodePort();
    		}
    	}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
        return sReturnData;
    }

	/**
	 * 获取账户组合代码
	 * @return
	 * @throws YssException
	 */
	private String getCashAccCodePort() throws YssException {
		String sPortCode = "";
		StringBuffer buff = null;
		ResultSet rs = null;
		try{
			buff = new StringBuffer(100);
			buff.append(" select * from ").append(pub.yssGetTableName("Tb_Para_CashAccount"));
			buff.append(" where FcheckState = 1 and fcashacccode = ").append(dbl.sqlString(this.bailMoneyValCal.getSCashAccCode()));
			
			rs = dbl.openResultSet(buff.toString());
			if(rs.next()){
				sPortCode = rs.getString("FPortCode");
			}
			
		}catch (Exception e) {
			throw new YssException("获取账户组合代码出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return sPortCode;
	}
}
