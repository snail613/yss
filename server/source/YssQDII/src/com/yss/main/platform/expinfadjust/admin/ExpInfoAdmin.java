package com.yss.main.platform.expinfadjust.admin;

import java.sql.*;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.*;
import com.yss.main.platform.expinfadjust.pojo.*;
import com.yss.main.storagemanage.*;
import com.yss.util.*;

/**
 * 指数信息调整 相关操作的类
* 普通股票、指数股票主要是从证券库存中查询
 * <p>Title: 指数信息调整</p>
 * <p>Description:
 * 20090908,MS00473,国泰需根据最新的纳斯达克指数100信息来调整即将发行的LOF基金中的股票信息,QDV4国泰2009年6月01日01_A </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author xuxuming,20090908
 * @version 1.0
 */
public class ExpInfoAdmin
    extends BaseDataSettingBean
    implements IDataSetting {
    private ExpInfoBean expInfData = null;
    private static String sHeader = "证券代码\t证券名称\t证券ISIN码\t日期";
    private String m_sMulRowStr = ""; //批量调整的数据
    public ExpInfoAdmin() {
    }

    /**
     * 数据加载显示的方法
     * @return String
     * @throws YssException
     */
    public String getOperValue(String listView) throws YssException {

        //根据库存表中所属分类得到指数股票.属性名称在表‘Tb_***_Para_AttributeClass’中定义
        if (listView.equalsIgnoreCase("listcurrent")) {
            return this.getListCurrent();
        }
        //得到最新纳斯达克指数100
        if (listView.equalsIgnoreCase("listlatest")) {
            return this.getListLatest();
        }
        //根据库存表中所属分类得到积极型股票，即普通股票
        if (listView.equalsIgnoreCase("listactive")) {
            return this.getListActive();
        }
        //查询 目前纳斯达克指数100 与 最新纳斯达克指数100 差异的部分
        if (listView.equalsIgnoreCase("listcurrentdif")) {
            return this.getCurrentDif();
        }
        //查询 最新纳斯达克指数100 与 目前纳斯达克指数100 差异的部分
        if (listView.equalsIgnoreCase("listlatestdif")) {
            return this.getLatestDif();
        }
        //将目标股票 变为 指数型股票
        if (listView.equalsIgnoreCase("tocurrent")) {
            return this.changeToCurrent();
        }
        //将目标股票 变为  积极型股票
        if (listView.equalsIgnoreCase("toactive")) {
            return this.changeToActive();
        }
        //批量 变为 指数型股票
        if (listView.equalsIgnoreCase("tocurrentall")) {
            return this.changeToCurrentAll();
        }
        //批量 变为  积极型股票
        if (listView.equalsIgnoreCase("toactiveall")) {
            return this.changeToActiveAll();
        }

        return "";
    }

    /**
     * getListActive
     * 根据库存表中所属分类得到证券信息。
     * 主要是查询 积极型股票
     * 从 证券库存表查询,日期为查询日前一日
     * @return String
     */
    public String getListActive() throws YssException {
        String sqlStr = "";
        String sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufAll = new StringBuffer(); //全显示字段
        try {
            //取数原理：
            //从证券库存中取出查询日期前一日的普通股票(CEQ)+ 查询日综合业务中方向为流入的普通股票，连接证券信息查询出证券信息 - 查询日综合业务中方向为流出的普通股票

            //1.从证券库存中取出查询日期前一日的普通股票(CEQ)
            sqlStr = "select d.FSecurityCode, " +
            		"d.fportcode," +//add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101112
            		"b.FSecurityName, b.FISINCode,b.FCatCode, d.FStorageDate,'CEQ' as FAttrClsCode from " +
                " (select a.FSecurityCode as FSecurityCode,a.FStorageDate  as FStorageDate " +
                ", a.fportcode as fportcode " +//add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101112
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " a where a.fattrclscode = 'CEQ' and a.FCheckState=1 ";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and a.FStorageDate = " + dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.expInfData.getSDate()), -1));
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and a.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            
            //2.查询日综合业务中方向为流入的普通股票
            sqlStr += " union select c.FSecurityCode as FSecurityCode,c.FOperDate-1 as FStorageDate " +
            		", c.fportcode as fportcode " +//add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101112
            		" from " + pub.yssGetTableName("Tb_Data_Integrated") +//因为显示在界面左边的数据是当前日期减1，故对综合业务查询出来的数据后，也取减1，这样使得调整时日期一致了
                " c where c.FInOutType = 1 and c.FAttrClsCode = 'CEQ' ";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and c.FOperDate = " + dbl.sqlDate(this.expInfData.getSDate());
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and c.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            
            //3.连接证券信息，使用not exists排除查询日综合业务中方向为流出的普通股票信息
            sqlStr += " ) d left join (select FSecurityCode, FISINCode, FCatCode, FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " ) b on d.FSecurityCode=b.FSecurityCode" +
                " where not exists (select e.FSecurityCode from " + pub.yssGetTableName("Tb_Data_Integrated") +
                " e where e.FInOutType=-1" +
                " and e.FAttrClsCode= 'CEQ'";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and e.FOperDate = " + dbl.sqlDate(this.expInfData.getSDate());
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and e.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            sqlStr += " and e.FSecurityCode=d.FSecurityCode)";
          //==============//add by xxm,20100202.加上品种代码,MS00961 ============
            sqlStr = "select * from ( "+sqlStr+") viewSec where viewSec.FCatCode='EQ'";//只查询股票的信息
            //=======================end===================================
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecurityCode") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FSecurityName") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FISINCode") + "").trim()).append("\t");
                bufShow.append(YssFun.formatDate(rs.getString("FStorageDate"), "yyyy-MM-dd")).append(YssCons.YSS_LINESPLITMARK);

                this.expInfData.setValues(rs); //封装数据
                this.expInfData.setStrPortCode(rs.getString("FPortCode"));//add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101112

                bufAll.append(this.expInfData.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.delete(bufAll.length() - 2, bufAll.length()).toString();
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception ex) {
            throw new YssException("获取积极型股票信息出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 解析前台传来的字符串
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        //包括 /r/f，则是批量数据处理，此处不做处理
        if (sRowStr.indexOf(YssCons.YSS_PASSAGESPLITMARK) > 0) {
            m_sMulRowStr = sRowStr;
            return;
        }
        if (this.expInfData == null) {
            this.expInfData = new ExpInfoBean();
        }
        //将 前台传来的字符串 赋值给该BEAN所对应的属性
        this.expInfData.parseRowStr(sRowStr);
    }

    /**
     * 根据库存表中所属分类得到证券信息。
     * 主要是查询 指数股票
     * 从 证券库存表查询,日期为查询日前一日
     * @author xuxuming
     * @throws YssException
     * @return String
     */
    public String getListCurrent() throws YssException {
        String sqlStr = "";
        String sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufAll = new StringBuffer(); //全显示字段
        try {
            //取数原理：
            //从证券库存中取出查询日期前一日的指数股票(IDXEQ)+ 查询日综合业务中方向为流入的指数股票，连接证券信息查询出证券信息 - 查询日综合业务中方向为流出的指数股票

            //1.从证券库存中取出查询日期前一日的指数股票(IDXEQ)
            //sqlStr = "select d.FSecurityCode, b.FSecurityName, b.FISINCode, b.FCatCode, d.FStorageDate,'IDXEQ' as FAttrClsCode from " +//add by xxm,20100202.加上品种代码,MS00961 
        	//" (select a.FSecurityCode as FSecurityCode,a.FStorageDate  as FStorageDate from " + pub.yssGetTableName("Tb_Stock_Security") +
        	//edit by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101112
        	sqlStr = "select d.FSecurityCode, d.fportcode, b.FSecurityName, b.FISINCode, b.FCatCode, d.FStorageDate,'IDXEQ' as FAttrClsCode from " +//add by xxm,20100202.加上品种代码,MS00961 
                " (select a.FSecurityCode as FSecurityCode,a.FStorageDate  as FStorageDate, a.fportcode as fportcode from " + pub.yssGetTableName("Tb_Stock_Security") +
                " a where a.fattrclscode = 'IDXEQ' and a.FCheckState=1 ";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and a.FStorageDate = " + dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.expInfData.getSDate()), -1));
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and a.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            
            //2.查询日综合业务中方向为流入的指数股票
            //sqlStr += " union select c.FSecurityCode as FSecurityCode,c.FOperDate-1 as FStorageDate, from " + pub.yssGetTableName("Tb_Data_Integrated") +//因为显示在界面左边的数据是当前日期减1，故对综合业务查询出来的数据后，也取减1，这样使得调整时日期一致了
            //edit by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101112
            sqlStr += " union select c.FSecurityCode as FSecurityCode,c.FOperDate-1 as FStorageDate, c.fportcode as fportcode from " + pub.yssGetTableName("Tb_Data_Integrated") +//因为显示在界面左边的数据是当前日期减1，故对综合业务查询出来的数据后，也取减1，这样使得调整时日期一致了
                " c where c.FInOutType = 1 and c.FAttrClsCode = 'IDXEQ' "; //综合业务表中 存在这笔流入
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and c.FOperDate = " + dbl.sqlDate(this.expInfData.getSDate());
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and c.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            
            //3.连接证券信息，使用not exists排除查询日综合业务中方向为流出的指数股票信息
            sqlStr += " ) d left join (select FSecurityCode, FISINCode, FSecurityName,FCatCode from " +//add by xxm,20100202.加上品种代码,MS00961 
                pub.yssGetTableName("Tb_Para_Security") + " ) b on d.FSecurityCode=b.FSecurityCode" +
                " where not exists (select e.FSecurityCode from " + pub.yssGetTableName("Tb_Data_Integrated") + //综合业务表中 不存在这笔流出
                " e where e.FInOutType=-1" + //-1, 流出
                " and e.FAttrClsCode= 'IDXEQ'";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and e.FOperDate = " + dbl.sqlDate(this.expInfData.getSDate());
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and e.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            sqlStr += " and e.FSecurityCode=d.FSecurityCode)";
            //==============//add by xxm,20100202.加上品种代码,MS00961 ============
            sqlStr = "select * from ( "+sqlStr+") viewSec where viewSec.FCatCode='EQ'";//只查询股票的信息
            //=======================end===================================
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecurityCode") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FSecurityName") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FISINCode") + "").trim()).append("\t");
                bufShow.append(YssFun.formatDate(rs.getString("FStorageDate"), "yyyy-MM-dd")).append(YssCons.YSS_LINESPLITMARK);

                this.expInfData.setValues(rs); //封装数据
                this.expInfData.setStrPortCode(rs.getString("FPortCode"));//add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101112

                bufAll.append(this.expInfData.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            //下面两个判断均是为了删除字符串结尾的“\f\f”
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.delete(bufAll.length() - 2, bufAll.length()).toString();
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception ex) {
            throw new YssException("获取指数股票信息出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 最新纳斯达克指数100
     * 所查询的表Tb_001_PFOper_Nasdaq所对应的数据是从外部导入的
     * Tb_001_PFOper_Nasdaq中有，且综合业务中不存在这笔流出(add by xxm 20100201,MS00960 不排除这笔流出了，此处查询出NASDAQ表中当天所有数据)
     * 从Tb_001_PFOper_Nasdaq中查询,日期为查询日
     * @author xuxuming
     * @throws YssException
     * @return String
     */
    public String getListLatest() throws YssException {
        String sqlStr = "";
        String sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufAll = new StringBuffer(); //全显示字段
        try {
            //从纳斯达克指数100中查询最新的股票信息，并且排除综合业务中方向为流出的证券信息，做as是未了通过setValues进行数据封装
            sqlStr = "select a.FTicker as FSecurityCode,a.FISINCode,a.FTickerName as FSecurityName,a.FDate as FStorageDate,'CEQ' as FAttrClsCode from " +
                pub.yssGetTableName("Tb_PFOper_Nasdaq") +
                " a";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " where a.FDate = " + dbl.sqlDate(this.expInfData.getSDate());//根据 传入的 查询日期 来查询
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加指数类型标识查询条件，'n'为纳斯达克指数100，'b'为标普指数500
            if (this.expInfData.getStrExpType() != null && this.expInfData.getStrExpType().trim().length() > 0) {
            	sqlStr += " and a.FType = " + dbl.sqlString(this.expInfData.getStrExpType().toUpperCase());
            }
            /*   //edit by xxm,20100201,MS00960 不排除当天已流出的部分，此处查询出NASDAQ表中当天所有数据
            sqlStr += " and not exists (select c.FSecurityCode from " + pub.yssGetTableName("Tb_Data_Integrated") + //综合业务表中 不存在这笔流出
                " c where c.FInOutType=-1" + //-1, 流出
                " and c.FAttrClsCode= 'CEQ'";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and c.FOperDate = " + dbl.sqlDate(this.expInfData.getSDate());
            }
            sqlStr += " and c.FSecurityCode=a.FTicker)";  */

            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecurityCode") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FSecurityName") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FISINCode") + "").trim()).append("\t");
                bufShow.append(YssFun.formatDate(rs.getString("FStorageDate"), "yyyy-MM-dd")).append(YssCons.YSS_LINESPLITMARK);
                this.expInfData.setValues(rs);
                bufAll.append(this.expInfData.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.delete(bufAll.length() - 2, bufAll.length()).toString();
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception ex) {
//            throw new YssException("获取最新纳斯达克指数股票信息出错！", ex);
            throw new YssException("获取最新指数所含证券信息出错！", ex);//edit by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 得到 目前纳斯达克指数100中和最新纳斯达克指数100存在差异的部分
     * 目前持有的指数类型股票，但最新纳斯达克指数100中没有该股票
     * Tb_***_Stock_Security中指数类型股票中有(日期为查询日前一天)，但Tb_001_PFOper_Nasdaq中没有(日期为查询日)，且综合业务中不存在这笔流出
     */
    public String getCurrentDif() throws YssException {
        String sqlStr = "";
        String sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufAll = new StringBuffer(); //全显示字段
        try {
            //取数原理：
            //从证券库存中取出指数股票信息 + 综合业务中流入方向的指数股票信息 - 综合业务中流出方向的指数股票信息 - 最新的纳斯达克指数100中的股票

            //1.从证券库存中取出指数股票信息
            sqlStr = "select d.FSecurityCode, b.FSecurityName, b.FISINCode,b.FCatCode, d.FStorageDate,'IDXEQ' as FAttrClsCode from " +
                " (select a.FSecurityCode as FSecurityCode,a.FStorageDate  as FStorageDate from " + pub.yssGetTableName("Tb_Stock_Security") +
                " a where a.fattrclscode = 'IDXEQ' and a.FCheckState=1 ";//add by xuxuming,这里只取证券库存中已审核的记录
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and a.FStorageDate = " + dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.expInfData.getSDate()), -1));
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and a.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            
            //2.从综合业务中取出流入方向的指数股票信息
            sqlStr += " union select c.FSecurityCode as FSecurityCode,c.FOperDate-1 as FStorageDate from " + pub.yssGetTableName("Tb_Data_Integrated") +//因为显示在界面左边的数据是当前日期减1，故对综合业务查询出来的数据后，也取减1，这样使得调整时日期一致了
                " c where c.FInOutType = 1 and c.FAttrClsCode = 'IDXEQ' "; //综合业务表中 存在这笔流入
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and c.FOperDate = " + dbl.sqlDate(this.expInfData.getSDate());
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and c.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            
            //3.从综合业务中取出流出方向的指数股票信息
            sqlStr += " ) d left join (select FSecurityCode, FISINCode,FCatCode, FSecurityName from " +//MS00961.加上品种代码
                pub.yssGetTableName("Tb_Para_Security") + " ) b on d.FSecurityCode=b.FSecurityCode" +
                " where not exists (select e.FSecurityCode from " + pub.yssGetTableName("Tb_Data_Integrated") + //综合业务表中 不存在这笔流出
                " e where e.FInOutType=-1" + //-1, 流出
                " and e.FAttrClsCode= 'IDXEQ'";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and e.FOperDate = " + dbl.sqlDate(this.expInfData.getSDate());
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and e.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            sqlStr += " and e.FSecurityCode=d.FSecurityCode)";
            //4.从最新纳斯达克指数100中取出股票信息
            sqlStr += " and not exists (select FTicker from " + pub.yssGetTableName("Tb_PFOper_Nasdaq") + //最新纳斯达克指数100中没有该股票
                " f";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " where f.FDate = " + dbl.sqlDate(this.expInfData.getSDate());//日期为查询日
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加指数类型标识查询条件，'n'为纳斯达克指数100，'b'为标普指数500
            if (this.expInfData.getStrExpType() != null && this.expInfData.getStrExpType().trim().length() > 0) {
            	sqlStr += " and f.FType = " + dbl.sqlString(this.expInfData.getStrExpType().toUpperCase());
            }
            sqlStr += " and d.FSecurityCode=f.FTicker)";
          //==============//add by xxm,20100202.加上品种代码,MS00961 ============
            sqlStr = "select * from ( "+sqlStr+") viewSec where viewSec.FCatCode='EQ'";//只查询股票的信息
            //=======================end===================================
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecurityCode") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FSecurityName") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FISINCode") + "").trim()).append("\t");
                bufShow.append(YssFun.formatDate(rs.getString("FStorageDate"), "yyyy-MM-dd")).append(YssCons.YSS_LINESPLITMARK);
                this.expInfData.setValues(rs);
                bufAll.append(this.expInfData.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.delete(bufAll.length() - 2, bufAll.length()).toString();
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception ex) {
//            throw new YssException("获取目前纳斯达克指数100中和最新纳斯达克指数100存在差异的部分出错！", ex);
            throw new YssException("获取目前指数所含证券中和最新指数所含证券中存在差异的部分出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 得到 最新纳斯达克指数100中和目前纳斯达克指数100存在差异的部分
     * Tb_001_PFOper_Nasdaq中有(日期为查询日)，但Tb_***_Stock_Security中指数类型的股票中没有(日期为查询日前一天)，且综合业务中没有这笔流出
     */
    public String getLatestDif() throws YssException {
        String sqlStr = "";
        String sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufAll = new StringBuffer(); //全显示字段
        try {
        	/*
            //从最新纳斯达克指数100中取出证券信息 - （证券库存为IDXEQ + 综合业务中流入方向的IDXEQ证券）
            sqlStr = "select a.FTicker as FSecurityCode ,a.FISINCode,a.FTickerName as FSecurityName,a.FDate as FStorageDate,'CEQ' as FAttrClsCode from " +
                pub.yssGetTableName("Tb_PFOper_Nasdaq") +
                " a where";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " a.FDate = " + dbl.sqlDate(this.expInfData.getSDate()) + " and";//日期为查询日
            }

            sqlStr += " not exists (select b.FSecurityCode from " + pub.yssGetTableName("Tb_Stock_Security") +
                " b where b.fattrclscode = " + dbl.sqlString(YssOperCons.YSS_SXFL_IDXEQ);
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and b.FStorageDate = " + dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.expInfData.getSDate()), -1));
            }
            sqlStr += " and b.FCheckState=1 and b.FSecurityCode=a.FTicker)";//只和证券库存中已审核的进行比对
            sqlStr += " and not exists (select c.FSecurityCode from " + pub.yssGetTableName("Tb_Data_Integrated") + //综合业务表中 不存在这笔流出
                " c where c.FInOutType=1" + //流入的指数型
                " and c.FAttrClsCode= 'IDXEQ'";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and c.FOperDate = " + dbl.sqlDate(this.expInfData.getSDate());
            }
            sqlStr += " and c.FSecurityCode=a.FTicker)";
            */
            //===add by xuxuming,20091111.综合业务中指数型流出的，也要在差异中显示出来。=========
            //从最新纳斯达克指数100中取出证券信息 - （证券库存为IDXEQ + 综合业务中流入方向的IDXEQ证券-综合业务中流出的IDXEQ）
        	sqlStr = "select a.FTicker as FSecurityCode ,a.FISINCode,a.FTickerName as FSecurityName,a.FDate as FStorageDate,'CEQ' as FAttrClsCode from " +
            pub.yssGetTableName("Tb_PFOper_Nasdaq") +" a "+
            //add by zhangfa 20110106 BUG #773 指数信息调整后：查询证券信息，指数所含证券没有包括新调整进来的指数证券 
            //根据周述晟方案：T-1日没有库存的证券，不需要调整，即不显示
            " join(select b.FSecurityCode as FSecurityCode,b.FStorageDate  as FStorageDate from "+pub.yssGetTableName("Tb_Stock_Security") +" b"+
            " where b.FCheckState = 1 and b.FStorageDate ="+ dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.expInfData.getSDate()), -1))+
            " and b.FPortCode =" + dbl.sqlString(this.expInfData.getStrPortCode())+" ) b on b.FSecurityCode=a.FTicker"+
            //---------------------end 20110106-----------------------------------------------------------------
            "  where";
        if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
            sqlStr += " a.FDate = " + dbl.sqlDate(this.expInfData.getSDate()) + " and ";//日期为查询日
        }
	        //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
	        //增加指数类型标识查询条件，'n'为纳斯达克指数100，'b'为标普指数500
	        if (this.expInfData.getStrExpType() != null && this.expInfData.getStrExpType().trim().length() > 0) {
	        	sqlStr += " a.FType = " + dbl.sqlString(this.expInfData.getStrExpType().toUpperCase()) + " and ";
	        }
            sqlStr += " not exists (select g.FSecurityCode from (select d.FSecurityCode, b.FSecurityName, b.FISINCode, d.FStorageDate,'IDXEQ' as FAttrClsCode from " +
                " (select a.FSecurityCode as FSecurityCode,a.FStorageDate  as FStorageDate from " + pub.yssGetTableName("Tb_Stock_Security") +
                " a where a.fattrclscode = 'IDXEQ' and a.FCheckState=1 ";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and a.FStorageDate = " + dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.expInfData.getSDate()), -1));
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and a.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            
            //2.查询日综合业务中方向为流入的指数股票
            sqlStr += " union select c.FSecurityCode as FSecurityCode,c.FOperDate as FStorageDate from " + pub.yssGetTableName("Tb_Data_Integrated") +
                " c where c.FInOutType = 1 and c.FAttrClsCode = 'IDXEQ' "; //综合业务表中 存在这笔流入
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and c.FOperDate = " + dbl.sqlDate(this.expInfData.getSDate());
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and c.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            
            //3.连接证券信息，使用not exists排除查询日综合业务中方向为流出的指数股票信息
            sqlStr += " ) d left join (select FSecurityCode, FISINCode, FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " ) b on d.FSecurityCode=b.FSecurityCode" +
                " where not exists (select e.FSecurityCode from " + pub.yssGetTableName("Tb_Data_Integrated") + //综合业务表中 不存在这笔流出
                " e where e.FInOutType=-1" + //-1, 流出
                " and e.FAttrClsCode= 'IDXEQ'";
            if (this.expInfData.getSDate() != null && YssFun.isDate(this.expInfData.getSDate())) {
                sqlStr += " and e.FOperDate = " + dbl.sqlDate(this.expInfData.getSDate());
            }
            //add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码查询条件
            if (this.expInfData.getStrPortCode() != null && this.expInfData.getStrPortCode().trim().length() > 0) {
            	sqlStr += " and e.FPortCode = " + dbl.sqlString(this.expInfData.getStrPortCode());
            }
            sqlStr += " and e.FSecurityCode=d.FSecurityCode)";
            sqlStr += " ) g where g.FSecurityCode=a.FTicker )";
        	//============end=================================          
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecurityCode") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FSecurityName") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FISINCode") + "").trim()).append("\t");
                bufShow.append(YssFun.formatDate(rs.getString("FStorageDate"), "yyyy-MM-dd")).append(
                    YssCons.YSS_LINESPLITMARK);
                this.expInfData.setValues(rs);
                bufAll.append(this.expInfData.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.delete(bufAll.length() - 2, bufAll.length()).toString();
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception ex) {
            //throw new YssException("获取最新纳斯达克指数100中和目前纳斯达克指数100存在差异的部分出错！", ex);
        	//edit by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            throw new YssException("获取最新指数所含证券中和目前指数所含证券中存在差异的部分出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 将目标指数型股票调整成积极型
     * 在综合业务中产生四笔数据：指股流出（成本）、普股流入（成本）、指股流出（应收应付）、普股流入（应收应付）
     * 成本 从证券库存中取数据；应收应付 从证券应收应付取数据，包括 股票估值增值、股票成本汇兑损益、股票估值增值汇兑损益
     */
    public String changeToActive() throws YssException {
        this.expInfData.setSAttrClsCode(YssOperCons.YSS_SXFL_IDXEQ); //指数型股票。
        return this.changeAttrCls(YssOperCons.YSS_SXFL_CEQ);

    }

    /**
     * 将积极型股票调整成指数型
     * @return String
     */
    public String changeToCurrent() throws YssException {
        this.expInfData.setSAttrClsCode(YssOperCons.YSS_SXFL_CEQ); //积极型股票。
        return this.changeAttrCls(YssOperCons.YSS_SXFL_IDXEQ);
    }

    /**
     *  在综合业务中产生四笔数据：指股流出（成本）、普股流入（成本）、指股流出（应收应付）、普股流入（应收应付）
     * 成本 从证券库存中取数据；应收应付 从证券应收应付库存取数据，包括 股票估值增值、股票成本汇兑损益、股票估值增值汇兑损益
     * 从　证券应收应付库存表　里面取出　股票估值增值、股票库存成本汇兑损益、股票估值增值汇兑损益，
     * 这些数据　存入　综合业务　表时，会　相应在　证券应收应付款表里产生数据，并与之关联。
     */
    public String changeAttrCls(String toAttrClsCode) throws YssException {
        try {
            SecurityStorageBean secStorDataFilter = new SecurityStorageBean();
            SecurityStorageBean secStorData = new SecurityStorageBean();
            secStorData.setYssPub(pub);
            secStorDataFilter.setStrSecurityCode(this.expInfData.getSSecurityCode());
            secStorDataFilter.setStrStorageDate(this.expInfData.getSDate());
            secStorDataFilter.setAttrCode(this.expInfData.getSAttrClsCode());
            //==============add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码为查询条件
            secStorDataFilter.setStrPortCode(this.expInfData.getStrPortCode());
            //==============end=========
            secStorDataFilter.setBBegin("false");
            secStorDataFilter.setIsOnlyColumns("1");
            secStorData.setFilterType(secStorDataFilter);
            secStorData.getSetting();

            SecIntegratedBean secInteData = new SecIntegratedBean();
            SecIntegratedBean secInteFilterType = new SecIntegratedBean();
            secInteData.setYssPub(pub);
            StringBuffer tempBuf = new StringBuffer();
            if (secStorData.getStrSecurityCode() != null && secStorData.getStrSecurityCode().trim().length() > 0) {
                secInteFilterType.setSTradeTypeCode("101"); // 101:成分股转换
//                secInteFilterType.setInvestType("C"); //投资类型：交易性
                secInteFilterType.setSSecurityCode(secStorData.getStrSecurityCode());
                secInteFilterType.setSOperDate(YssFun.formatDate(YssFun.addDay(YssFun.toDate(secStorData.getStrStorageDate()), 1), "yyyy-MM-dd"));
                secInteFilterType.setSPortCode(secStorData.getStrPortCode());
                secInteData.setFilterType(secInteFilterType);
                secInteData.getSetting(); //如果综合业务表中已有记录，得到记录号，在后面SAVE中删除。

                secInteData.setSExchangeDate(YssFun.formatDate(YssFun.addDay(YssFun.toDate(secStorData.getStrStorageDate()), 1), "yyyy-MM-dd")); //因为之前减去了一天
                secInteData.setSTradeTypeCode("101"); // 101:成分股转换
//                secInteData.setInvestType("C"); //投资类型：交易性
                tempBuf.append(secInteData.buildRowStrForParse()).append("\r\t[null]\r\t");
                secInteData.setSSecurityCode(secStorData.getStrSecurityCode());
                secInteData.setSOperDate(YssFun.formatDate(YssFun.addDay(YssFun.toDate(secStorData.getStrStorageDate()), 1), "yyyy-MM-dd"));
                secInteData.setSPortCode(secStorData.getStrPortCode());
                secInteData.setSAnalysisCode1(secStorData.getStrFAnalysisCode1());
                secInteData.setSAnalysisCode2(secStorData.getStrFAnalysisCode2());
                secInteData.setSAnalysisCode3(secStorData.getStrFAnalysisCode3());
                //modify by fangjiang 2010.11.04 BUG #236 指数调整估值后，指数型证券未减少，反而增倍
                secInteData.setDAmount(-1*YssFun.toDouble(secStorData.getStrStorageAmount()));
                secInteData.setDCost(-1*YssFun.toDouble(secStorData.getStrStorageCost()));
                secInteData.setDMCost(-1*YssFun.toDouble(secStorData.getStrMStorageCost()));
                secInteData.setDVCost(-1*YssFun.toDouble(secStorData.getStrVStorageCost()));
                secInteData.setDBaseCost(-1*YssFun.toDouble(secStorData.getStrBaseCuryCost()));
                secInteData.setDMBaseCost(-1*YssFun.toDouble(secStorData.getStrMBaseCuryCost()));
                secInteData.setDVBaseCost(-1*YssFun.toDouble(secStorData.getStrVBaseCuryCost()));
                secInteData.setDPortCost(-1*YssFun.toDouble(secStorData.getStrPortCuryCost()));
                secInteData.setDMPortCost(-1*YssFun.toDouble(secStorData.getStrMPortCuryCost()));
                secInteData.setDVPortCost(-1*YssFun.toDouble(secStorData.getStrVPortCuryCost()));
                //------------------
                secInteData.setDBaseCuryRate(YssFun.toDouble(secStorData.getStrBaseCuryRate()));
                secInteData.setDPortCuryRate(YssFun.toDouble(secStorData.getStrPortCuryRate()));
                secInteData.checkStateId = secStorData.checkStateId;
                secInteData.creatorCode = secStorData.creatorCode;
                secInteData.creatorTime = secStorData.creatorTime;
                secInteData.checkUserCode = secStorData.checkUserCode;
                secInteData.checkTime = secStorData.checkTime!=null?secStorData.checkTime:"";//防止为空
                secInteData.setAttrClsCode(secStorData.getAttrCode()); //所属分类，积极型股票；积极型股流出
                secInteData.setIInOutType( -1); // 流出
                //edited  by zhouxiang MS01479    在业务平台-指数信息调整界面点击手工调整分页报错，报错内容为“调用的目标发生了异常 
                secInteData.setInvestType(secStorData.getInvestType());
                //end------- zhouxiang MS01479    在业务平台-指数信息调整界面点击手工调整分页报错，报错内容为“调用的目标发生了异常 
                tempBuf.append(secInteData.buildRowStrForParse()).append("\f\f"); //一笔流出(成本)
                secInteData.setAttrClsCode(toAttrClsCode); //所属分类，
                secInteData.setIInOutType(1); // 流入
                //add by fangjiang 2010.11.04 BUG #236 指数调整估值后，指数型证券未减少，反而增倍
                secInteData.setDAmount(YssFun.toDouble(secStorData.getStrStorageAmount()));
                secInteData.setDCost(YssFun.toDouble(secStorData.getStrStorageCost()));
                secInteData.setDMCost(YssFun.toDouble(secStorData.getStrMStorageCost()));
                secInteData.setDVCost(YssFun.toDouble(secStorData.getStrVStorageCost()));
                secInteData.setDBaseCost(YssFun.toDouble(secStorData.getStrBaseCuryCost()));
                secInteData.setDMBaseCost(YssFun.toDouble(secStorData.getStrMBaseCuryCost()));
                secInteData.setDVBaseCost(YssFun.toDouble(secStorData.getStrVBaseCuryCost()));
                secInteData.setDPortCost(YssFun.toDouble(secStorData.getStrPortCuryCost()));
                secInteData.setDMPortCost(YssFun.toDouble(secStorData.getStrMPortCuryCost()));
                secInteData.setDVPortCost(YssFun.toDouble(secStorData.getStrVPortCuryCost()));
                //---------------------
                tempBuf.append(secInteData.buildRowStrForParse()).append("\r\t"); //一笔流入(成本)
            } else { //证券库存中没有对应记录的数据，则判断为执行反操作，将综合业务表中的已经添加的流入和流出记录删除。
                secInteFilterType.setSTradeTypeCode("101"); // 101:成分股转换
//                secInteFilterType.setInvestType("C"); //投资类型：交易性
                secInteFilterType.setSSecurityCode(this.expInfData.getSSecurityCode());
                secInteFilterType.setSOperDate(YssFun.formatDate(YssFun.addDay(YssFun.toDate(this.expInfData.getSDate()), 1), "yyyy-MM-dd"));
                //========add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
                //增加组合代码为查询条件
                secInteFilterType.setSPortCode(secStorData.getStrPortCode());
                //========end=============
                secInteData.setFilterType(secInteFilterType);
                secInteData.getSetting(); //如果综合业务表中已有记录，得到记录号，在后面SAVE中删除。

                if (secInteData.getSOldNum() != null && secInteData.getSOldNum().trim().length() > 0) {
                    tempBuf.append(secInteData.buildRowStrForParse()).append("\r\t[null]\r\t");
                }
            }
             //delete by xuxuming,2010.01.12.不保存这些应收应付数据的流入和流出，系统会自动产生
            //下面开始增加 应收应付 数据,包括 股票估值增值、股票成本汇兑损益、股票估值增值汇兑损益
            SecRecPayBalBean secRecPayBalData = new SecRecPayBalBean(); //对应　证券应收应付库存　表
            SecRecPayBalBean filterType = new SecRecPayBalBean();
            secRecPayBalData.setYssPub(pub);
            filterType.setSSecurityCode(this.expInfData.getSSecurityCode());
            filterType.setDtStorageDate(YssFun.toDate(this.expInfData.getSDate()));
            filterType.setBBegin("false");
            filterType.setAttrClsCode(this.expInfData.getSAttrClsCode());
            SecPecPayBean secPecPayData = new SecPecPayBean(); //对应　证券应收应付款　表
            //存入　综合业务　表时，会　相应在　证券应收应付款表里产生数据，并与之关联
            //所以　证券应收应付数据　先 Set　到　证券应收应付款表里

            //==========股票估值增值====================================================
            filterType.setSTsfTypeCode("09"); //估值增值
            filterType.setSSubTsfTypeCode("09EQ"); //股票估值增值
            //=========add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            //增加组合代码为查询条件
            filterType.setSPortCode(this.expInfData.getStrPortCode());
            //=========end========
            secRecPayBalData.setFilterType(filterType);
            secRecPayBalData.getSetting(); //按filterType条件查询证券应收应付数据
            if (secRecPayBalData.getSSecurityCode() != null && secRecPayBalData.getSSecurityCode().trim().length() > 0) {
                this.copyBeanToSecPecPay(secPecPayData, secRecPayBalData, -1); //流出
                secPecPayData.setBaseCuryRate(secInteData.getDBaseCuryRate());//将基础汇率也保存到应收应付表,add by xuxuming
                secPecPayData.setPortCuryRate(secInteData.getDPortCuryRate());//将基础汇率也保存到应收应付表,add by xuxuming
//                tempBuf.append(secPecPayData.buildRowStrForParse()).append("\f\f");//delete by xuxuming,2010.01.12.MS00902 不保存流出，系统会自动产生一笔负款来冲减
                secPecPayData.setInOutType(1); //流入
                secPecPayData.setAttrClsCode(toAttrClsCode); //所属分类
                tempBuf.append(secPecPayData.buildRowStrForParse()).append("\f\f");
            }
            //==========================================================================
            //==========股票成本汇兑损益====================================================
            filterType.setSTsfTypeCode("99");
            filterType.setSSubTsfTypeCode("9905EQ");
            secRecPayBalData.setFilterType(filterType);
            secRecPayBalData.setSSecurityCode("");
            secRecPayBalData.getSetting(); //按filterType条件查询证券应收应付数据
            if (secRecPayBalData.getSSecurityCode() != null && secRecPayBalData.getSSecurityCode().trim().length() > 0) {
                this.copyBeanToSecPecPay(secPecPayData, secRecPayBalData, -1); //流出
//                tempBuf.append(secPecPayData.buildRowStrForParse()).append("\f\f");//delete by xuxuming,2010.01.12.MS00902 不保存流出，系统会自动产生一笔负款来冲减
                secPecPayData.setInOutType(1); //流入
                secPecPayData.setAttrClsCode(toAttrClsCode); //所属分类
                tempBuf.append(secPecPayData.buildRowStrForParse()).append("\f\f");
            }
            //==========================================================================
            //==========股票估值增值汇兑损益====================================================
            filterType.setSTsfTypeCode("99");
            filterType.setSSubTsfTypeCode("9909EQ");
            secRecPayBalData.setFilterType(filterType);
            secRecPayBalData.setSSecurityCode("");
            secRecPayBalData.getSetting(); //按filterType条件查询证券应收应付数据
            if (secRecPayBalData.getSSecurityCode() != null && secRecPayBalData.getSSecurityCode().trim().length() > 0) {
                this.copyBeanToSecPecPay(secPecPayData, secRecPayBalData, -1); //流出
//                tempBuf.append(secPecPayData.buildRowStrForParse()).append("\f\f");//delete by xuxuming,2010.01.12.MS00902 不保存流出，系统会自动产生一笔负款来冲减
                secPecPayData.setInOutType(1); //流入
                secPecPayData.setAttrClsCode(toAttrClsCode); //所属分类
                tempBuf.append(secPecPayData.buildRowStrForParse()).append("\f\f");
            }
            //==========================================================================
            
            if (!tempBuf.toString() .equals("") && tempBuf.toString().trim().length() > 0) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                secInteData.saveMutliSetting(tempBuf.toString(), true);
            }

        } catch (Exception ex) {
            throw new YssException("保存指数调整信息,调整为指数型出错！", ex);
        }
        return "1";
    }

    /**
     * 将证券应收应付款库存相就数据保存到证券应收应付款表对应字段中
     * @param secPecPayData SecPecPayBean
     * @param secRecPayBalData SecRecPayBalBean
     * @param inOutType int
     */
    public void copyBeanToSecPecPay(SecPecPayBean secPecPayData, SecRecPayBalBean secRecPayBalData, int inOutType) {
        secPecPayData.setStrNum("");
        secPecPayData.setInvMgrCode(secRecPayBalData.getSAnalysisCode1());//得到投资经理代码
        secPecPayData.setStrSubTsfTypeName(""); //只保存CODE，不保存NAME
        secPecPayData.setBrokerCode(secRecPayBalData.getSAnalysisCode2());//券商代码
        secPecPayData.setExchangeCode("");
        secPecPayData.setStrTsfTypeName("");
        secPecPayData.setStrPortCode(secRecPayBalData.getSPortCode());
        secPecPayData.setStrSecurityCode(secRecPayBalData.getSSecurityCode());
        secPecPayData.setOldTransDate(YssFun.addDay(secRecPayBalData.getDtStorageDate(), 1));
        secPecPayData.setStartDate(YssFun.addDay(secRecPayBalData.getDtStorageDate(), 1));
        secPecPayData.setEndDate(YssFun.addDay(secRecPayBalData.getDtStorageDate(), 1));
        secPecPayData.setTransDate(YssFun.addDay(secRecPayBalData.getDtStorageDate(), 1));
        secPecPayData.setCatTypeCode(secRecPayBalData.getCatTypeCode());
        secPecPayData.setAttrClsCode(secRecPayBalData.getAttrClsCode());
        secPecPayData.setStrTsfTypeCode(secRecPayBalData.getSTsfTypeCode());
        secPecPayData.setStrSubTsfTypeCode(secRecPayBalData.getSSubTsfTypeCode());
        secPecPayData.setStrCuryCode(secRecPayBalData.getSCuryCode());
        secPecPayData.setInOutType(inOutType);
        //===========MS00902,估值增值和汇兑损益未转到新的库存中去    QDV4国泰2010年1月5日01_B====
        secPecPayData.setStrFStockInd(9);//add by xuxuming,2010.01.04.入账标识为9，此数据在估值时就不会被系统自动删除了
        //==========end==============================
        secPecPayData.setMoney(secRecPayBalData.getDBal());
        secPecPayData.setMMoney(secRecPayBalData.getDMBal());
        secPecPayData.setVMoney(secRecPayBalData.getDVBal());
        secPecPayData.setBaseCuryMoney(secRecPayBalData.getDBaseBal());
        secPecPayData.setMBaseCuryMoney(secRecPayBalData.getDMBaseBal());
        secPecPayData.setVBaseCuryMoney(secRecPayBalData.getDVBaseBal());
        secPecPayData.setPortCuryMoney(secRecPayBalData.getDPortBal());
        secPecPayData.setMPortCuryMoney(secRecPayBalData.getDMPortBal());
        secPecPayData.setVPortCuryMoney(secRecPayBalData.getDVPortBal());
        secPecPayData.setMoneyF(secRecPayBalData.getBalF());
        secPecPayData.setBaseCuryMoneyF(secRecPayBalData.getBaseBalF());
        secPecPayData.setPortCuryMoneyF(secRecPayBalData.getPortBalF());
        secPecPayData.setCheckState(secRecPayBalData.checkStateId);
        secPecPayData.creatorCode = secRecPayBalData.creatorCode;
        secPecPayData.creatorTime = secRecPayBalData.creatorTime;
        secPecPayData.checkUserCode = secRecPayBalData.checkUserCode;
        secPecPayData.checkTime = secRecPayBalData.checkTime;
      //edited  by zhouxiang MS01479    在业务平台-指数信息调整界面点击手工调整分页报错，报错内容为“调用的目标发生了异常 
        secPecPayData.setInvestType(secRecPayBalData.getSInvestType()); 
      //end---  by zhouxiang MS01479    在业务平台-指数信息调整界面点击手工调整分页报错，报错内容为“调用的目标发生了异常 
    }

    /**
     * 将目标股票调整成积极型,批量调整
     */
    public String changeToActiveAll() throws YssException {
        String curRowStr = "";
        String[] tempAry = null;
        try {
            m_sMulRowStr = this.getCurrentDif(); //得到 目前持有的指数类型股票，但最新纳斯达克指数100中没有该股票，将此股票同步为 积极型
            tempAry = m_sMulRowStr.split(YssCons.YSS_PASSAGESPLITMARK);
            if (tempAry.length > 2) {
                curRowStr = tempAry[2]; //传来的字符串为 sHeader/r/fsShowData/r/fsAllData,此处取sAllData
            }
            if (curRowStr != null && curRowStr.length() > 0) {
                tempAry = curRowStr.split(YssCons.YSS_LINESPLITMARK);
                for (int i = 0; i < tempAry.length; i++) {
                    this.parseRowStr(tempAry[i]);
                    this.changeToActive();
                }
                return "1"; //执行成功
            } else {
                return "0";
            }
        } catch (Exception ex) {
            throw new YssException("批量调整为积极型出错！", ex);
        }
    }

    /**
     * 将目标股票调整成指数型,批量调整
     */
    public String changeToCurrentAll() throws YssException {
        String curRowStr = "";
        String[] tempAry = null;
        try {
            m_sMulRowStr = this.getLatestDif(); //最新纳斯达克指数100中和目前纳斯达克指数100存在差异的部分,将这些股票同步为 指数型
            tempAry = m_sMulRowStr.split(YssCons.YSS_PASSAGESPLITMARK);
            if (tempAry.length > 2) {
                curRowStr = tempAry[2]; //前台传来的字符串为 sHeader/r/fsShowData/r/fsAllData,此处取sAllData
            }

            if (curRowStr != null && curRowStr.length() > 0) {
                tempAry = curRowStr.split(YssCons.YSS_LINESPLITMARK);
                for (int i = 0; i < tempAry.length; i++) {
                    this.parseRowStr(tempAry[i]);
                    this.expInfData.setSDate(YssFun.formatDate(YssFun.addDay(YssFun.toDate(this.expInfData.getSDate()), -1),"yyyy-MM-dd"));//因为最新纳斯达克指数100是根据查询日来获取的，而在下面调整类型的方法中对日期进行加1,然后保存到综合业务，故在这里执行减1.
                    this.changeToCurrent();
                }
                return "1"; //执行成功
            } else {
                return "0";
            }
        } catch (Exception ex) {
            throw new YssException("批量调整为指数型出错！", ex);
        }
    }

    /**
     * 手工调整,把指数股票 调成 普通股票
     */
    public void changeCurrentToCommon(String sSecCode) throws YssException {
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
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
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
     * getListViewGroupData1
     *
     * @return String
     */
    public String getListViewGroupData1() {
        return "";
    }

    /**
     * getListViewGroupData2
     *
     * @return String
     */
    public String getListViewGroupData2() {
        return "";
    }

    /**
     * getListViewGroupData3
     *
     * @return String
     */
    public String getListViewGroupData3() {
        return "";
    }

    /**
     * getListViewGroupData4
     *
     * @return String
     */
    public String getListViewGroupData4() {
        return "";
    }

    /**
     * getListViewGroupData5
     *
     * @return String
     */
    public String getListViewGroupData5() {
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
     *
     * @return String
     */
    public String editSetting() {
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
     * getTreeViewGroupData1
     *
     * @return String
     */
    public String getTreeViewGroupData1() {
        return "";
    }

    /**
     * getTreeViewGroupData2
     *
     * @return String
     */
    public String getTreeViewGroupData2() {
        return "";
    }

    /**
     * getTreeViewGroupData3
     *
     * @return String
     */
    public String getTreeViewGroupData3() {
        return "";
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
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

}
