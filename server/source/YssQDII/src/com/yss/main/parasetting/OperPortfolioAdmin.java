package com.yss.main.parasetting;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.parasetting.pojo.*;
import com.yss.main.syssetting.RightBean;
import com.yss.util.*;

/**
 * add by wangzuochun 2009.06.04 MS00002
 * QDV4赢时胜（上海）2009年4月20日02_A
 * <p>Title: OperPortfolioAdmin</p>
 *
 * <p>Description: 操作组合设置</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 */
public class OperPortfolioAdmin
    extends BaseDataSettingBean implements
    IDataSetting {
    private OperPortfolioBean opbean = null;
    private String sRecycled = "";
    public OperPortfolioAdmin() {
    }

    /**
     * 检查操作组合设置数据是否合法
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        ArrayList checkList = this.opbean.getAlist();
        for (int i = 0; i < checkList.size(); i++) {
            OperPortfolioBean operBean = (OperPortfolioBean) checkList.get(i);
            dbFun.checkInputCommon(btOper,
                                   pub.yssGetTableName("Tb_Para_OperPortfolio"),
                                   "FOperPortCode",
                                   operBean.getOperPortCode(),
                                   operBean.getOldOperPortCode());
        }
    }

    /**
     * 增加操作组合操作，对应前台的“新建”
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        Statement st = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            st = conn.createStatement(); //获取Statement对象,用于批量处理
            ArrayList alist = this.opbean.getAlist();
            // 向操作组合设置表中循环插入操作组合记录
            for (int i = 0; i < alist.size(); i++) {
                OperPortfolioBean addBean = (OperPortfolioBean) alist.get(i);
                strSql = "insert into " +
                    pub.yssGetTableName("Tb_Para_OperPortfolio") +
                    "" +
                    "(FOperPortCode,FOperPortName,FDesc,FPortCode,FPortType,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FAssetGroupCode)" +   //modify by huangqirong 2012-08-07 story #2831 增加识别组合群
                    " values(" +
                    dbl.sqlString(addBean.getOperPortCode()) + "," +
                    dbl.sqlString(addBean.getOperPortName()) + "," +
                    dbl.sqlString(addBean.getDesc()) + "," +
                    dbl.sqlString(addBean.getPortCode()) + "," +
                    Integer.toString(addBean.getPortType()) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(addBean.creatorCode) + "," +
                    dbl.sqlString(addBean.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" : dbl.sqlString(addBean.creatorCode)) + "," +
                    (pub.getSysCheckState() ? "' '" : dbl.sqlString(addBean.checkTime)) +
                    "," + dbl.sqlString(addBean.getAssetGroupCode()) + //add by huangqirong 2012-08-07 story #2831 增加识别组合群
                    ")";
                st.addBatch(strSql); //加入批处理
            }
            st.executeBatch(); //持久化处理 ，批处理执行
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加操作组合信息出错", e);
        } finally {
            dbl.closeStatementFinal(st); //关闭游标资源
            dbl.endTransFinal(conn, bTrans); //事物处理
        }
        return null;
    }

    /**
     * 修改操作组合操作，对应前台的“修改”
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        Statement st = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            st = conn.createStatement(); //获取Statement对象,用于批量处理
            //-- 取得当前操作组合Bean的List，List中存的是操作组合对象.
            ArrayList alist = this.opbean.getAlist();
            //-- List第一个操作组合对象的组合类型是0，其余对象的组合类型是1（FPortType:1代表组合，0代表操作组合）
            OperPortfolioBean editBean = (OperPortfolioBean) alist.get(0);
            // 先根据原来的操作组合代码删除操作组合设置表中操作组合的记录
            strSql = " delete from " + pub.yssGetTableName("Tb_Para_OperPortfolio") +
                " where FOperPortCode = " + dbl.sqlString(editBean.getOldOperPortCode());
            dbl.executeSql(strSql);
            // 再向操作组合设置表中循环插入操作组合记录
            for (int i = 0; i < alist.size(); i++) {

                editBean = (OperPortfolioBean) alist.get(i);
                strSql = "insert into " +
                    pub.yssGetTableName("Tb_Para_OperPortfolio") +
                    "" +
                    "(FOperPortCode,FOperPortName,FDesc,FPortCode,FPortType,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FAssetGroupCode)" +  //modify by huangqirong 2012-08-07 story #2831 增加识别组合群
                    " values(" +
                    dbl.sqlString(editBean.getOperPortCode()) + "," +
                    dbl.sqlString(editBean.getOperPortName()) + "," +
                    dbl.sqlString(editBean.getDesc()) + "," +
                    dbl.sqlString(editBean.getPortCode()) + "," +
                    Integer.toString(editBean.getPortType()) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(editBean.creatorCode) + "," +
                    dbl.sqlString(editBean.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" : dbl.sqlString(editBean.creatorCode)) + "," +
                    (pub.getSysCheckState() ? "' '" : dbl.sqlString(editBean.checkTime)) +
                    "," + dbl.sqlString(editBean.getAssetGroupCode()) + //add by huangqirong 2012-08-07 story #2831 增加识别组合群
                    ")";
                st.addBatch(strSql); //加入批处理
            }
            st.executeBatch(); //持久化处理 ，批处理执行
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改操作组合信息出错", e);
        } finally {
            dbl.closeStatementFinal(st); //关闭游标资源
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * 删除操作组合操作，即放入回收站，对应前台的“删除”
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            ArrayList alist = this.opbean.getAlist();

            OperPortfolioBean delBean = (OperPortfolioBean) alist.get(0);
            // 设置审核状态码为2，这样就可以将删除掉的操作组合放入回收站
            strSql = "update " + pub.yssGetTableName("Tb_Para_OperPortfolio") +
                " set FCheckState = 2 " +
                "where FOperPortCode = " +
                dbl.sqlString(delBean.getOperPortCode());
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除操作组合信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 可以处理操作组合设置审核、反审核、回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = null;
        String[] arrData = null;
        Statement st = null;
        try {
            conn = dbl.loadConnection();
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            st = conn.createStatement(); //获取Statement对象,用于批量处理
            ArrayList alist = this.opbean.getAlist();

            // 根据操作组合的条数，循环设置操作组合的审核状态码
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                OperPortfolioBean checkBean = (OperPortfolioBean) alist.get(i);

                strSql = "update " + pub.yssGetTableName("Tb_Para_OperPortfolio") +
                    " set FCheckState = " +
                    checkBean.checkStateId;
                // 如果是审核操作，则获取审核人代码和审核时间
                if (checkBean.checkStateId == 1) {
                    strSql += ", FCheckUser = '" +
                        pub.getUserCode() + "' , FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'";
                }
                strSql += " where FOperPortCode = " +
                    dbl.sqlString(checkBean.getOperPortCode());
                //执行sql语句
                //--
                st.addBatch(strSql); //加入批处理
            }
            st.executeBatch(); //持久化处理 ，批处理执行
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核操作组合信息出错", e);
        } finally {
            dbl.closeStatementFinal(st); //关闭游标资源
            dbl.endTransFinal(conn, bTrans);
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
     * 筛选条件
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.opbean.getFilterType() != null) {
                sResult = "";
                //-- 取得当前操作组合Bean的List，List中存的是操作组合对象.
                ArrayList alist = this.opbean.getFilterType().getAlist();
                if (alist.size() == 0) {
                    return sResult;
                } else {
                    // List中所有对象的操作组合代码，操作组合名称，描述都是一样的，所以只要取得第一个对象即可
                    OperPortfolioBean filterBean = (OperPortfolioBean) alist.get(0);
                    if (filterBean.getOperPortCode().length() != 0) {
                        sResult = sResult + " and a.FOperPortCode like '" +
                            filterBean.getOperPortCode().replaceAll("'", "''") + "%'";
                    }
                    if (filterBean.getOperPortName().length() != 0) {
                        sResult = sResult + " and a.FOperPortName like '" +
                            filterBean.getOperPortName().replaceAll("'", "''") + "%'";
                    }
                    if (filterBean.getDesc().length() != 0) {
                        sResult = sResult + " and a.FDesc like '" +
                            filterBean.getDesc().replaceAll("'", "''") + "%'";
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选操作组合设置数据出错", e);
        }
        return sResult;
    }

    /**
     * 从回收站彻底删除数据,单条和多条信息都可以
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Statement st = null;
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            bTrans = true;
            st = conn.createStatement(); //获取Statement对象,用于批量处理
            arrData = sRecycled.split("\r\n");
            ArrayList alist = this.opbean.getAlist();
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                OperPortfolioBean recycleBean = (OperPortfolioBean) alist.get(i);
                // 从操作组合表中删除操作组合记录
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_OperPortfolio") +
                    " where FOperPortCode = '" + recycleBean.getOperPortCode() + "'";
                //执行sql语句
                //--
                st.addBatch(strSql); //加入批处理
            }
            st.executeBatch(); //持久化处理 ，批处理执行
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.closeStatementFinal(st); //关闭游标资源
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }
    /**
     * panjunfang modify 20090916
     * desc:如果操作组合下没有明细组合则前台无需显示该操作组合
     * desc:如果操作组合下没有有权限的明细组合则前台无需显示该操作组合
     * @return String
     * @throws YssException
     */
    public String getTreeViewData2() throws YssException {
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        StringBuffer bufSql = new StringBuffer();
        OperPortfolioBean operTree = null;
        String parentCode = "";
        String NodeCode = "";
        String NodeName = "";
        String NodeOrderCode = "";
        String OperPortCode = ""; //操作组合的菜单代码
        int tmpInt = 0;//判断操作组合下是否有明细组合,值大于0代表有 panjunfang add 20090916
        OperPortfolioBean tmpOperTree = new OperPortfolioBean();//用于存放操作组合 panjunfang add 20090916
        int OrderCode = 0;
        int detailPortOrderCode = 0; //操作组合中明晰组合的排序代码 2009.04.29 蒋锦 添加 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
        String sResult = "";
        RightBean right = new RightBean();
        try {
        	//20121128 added by liubo.海富通测试问题：调度方案执行、收益支付等界面的组合选择框没有考虑权限继承的问题
            //============================================
        	right.setYssPub(pub);
        	right.setUserCode(pub.getUserCode());
            //================end============================
        	
            bufSql.append("SELECT distinct *");
            bufSql.append(" FROM (SELECT a.FOperPortCode, a.FOperPortName, a.FPortCode, b.FPortName, ");
            bufSql.append(" a.FPortType, 0 AS FType ");
            bufSql.append(" FROM (SELECT * ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Operportfolio"));
            bufSql.append(" WHERE FCheckState = 1) a ");
            bufSql.append(" LEFT JOIN (SELECT a.fportcode, a.fportname,a.fstartdate ");
            
            bufSql.append(" from ");
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//          //------------xuqiji 20100629 MS01286 QDV4赢时胜(测试)2010年06月3日05_B-------------------//
//            bufSql.append(" from (select max(FStartDate) as FStartDate, FPortCode ");
//            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Portfolio"));
//            bufSql.append(" where FCheckState = 1 and FStartDate <=").append(dbl.sqlDate(new Date())).append(" group by FPortCode order by FPortCode) c");
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
            //----edit by songjie 2011.03.14 不以最大的启用日期查询数据----//
            bufSql.append(" (select * from ").append(pub.yssGetTableName("Tb_Para_Portfolio"));
            bufSql.append(" where FCheckState = 1) a ");
            //----edit by songjie 2011.03.14 不以最大的启用日期查询数据----//
            //-------------------------------end--------------------------------------//
            bufSql.append(" join (select DISTINCT FPortCode ");
            bufSql.append(" from Tb_Sys_Userright ");
            bufSql.append(" where FAssetGroupCode = ").append(dbl.sqlString(pub.getAssetGroupCode()));
            bufSql.append(" and FUserCode = ").append(dbl.sqlString(pub.getUserCode()));
            bufSql.append(" and FRightType = " + dbl.sqlString(YssCons.YSS_SYS_RIGHTTYPE_PORT) + ") rp ON a.fportcode =  rp.fportcode");
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//            //add by zhouxiang MS01293    设置两个相同的组合代码，不同组合名称时，打开业务处理界面时报错    QDV4赢时胜(测试)2010年6月11日2_B  
//            bufSql.append(" join (select x.fportcode,max(x.fstartdate) as fstartdate from ").append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" x");
//            bufSql.append(" where x.fcheckstate =1 group by x.fportcode)").append(" z");
//            bufSql.append("	on a.fportcode=z.fportcode and a.fstartdate=z.fstartdate");
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
            //-------------end--------------
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FEnabled = 1) b ON a.fportcode = b.fportcode ");
            bufSql.append(" UNION ALL ");
            bufSql.append(" select ' ' AS FOperPortCode, ' ' AS FOperPortName, pf.FPortCode, FPortName, ");
            bufSql.append(" 1 AS fporttype, 1 AS FType ");
            
//            //------------xuqiji 20100629 MS01286 QDV4赢时胜(测试)2010年06月3日05_B-------------------//
//            bufSql.append(" from (select max(FStartDate) as FStartDate, FPortCode ");
//            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Portfolio"));
//            bufSql.append(" where FCheckState = 1 and FStartDate <=").append(dbl.sqlDate(new Date())).append("group by FPortCode order by FPortCode) e");
            //----edit by songjie 2011.03.14 不以最大的启用日期查询数据----//
            bufSql.append(" from (select * from ").append(pub.yssGetTableName("Tb_Para_Portfolio"));
            bufSql.append(" where FCheckState = 1) pf ");
            //----edit by songjie 2011.03.14 不以最大的启用日期查询数据----//
            //---------------------------------end---------------------------------//
            bufSql.append(" join (select DISTINCT FPortCode");
            bufSql.append(" from Tb_Sys_Userright");
            bufSql.append(" where FAssetGroupCode = ").append(dbl.sqlString(pub.getAssetGroupCode()));
            bufSql.append(" and (FUserCode = ").append(dbl.sqlString(pub.getUserCode()));
	    	//20121129 modified by liubo.Story #2737
	    	//获取权限时需要考虑权限继承的问题，包括用户赋予的权限和角色权限。
    		//即获取权限继承设置中，当天日期大于起始日期，小于结束日期，受托人中包含当前用户的数据，将该条数据记录的属于当前组合群的组合，与委托人代码拼接成OR语句
    		//===============================
            bufSql.append(right.getInheritedRights(pub.getAssetGroupCode(), "")).append(")");
            //===================end=========================
            bufSql.append(" and FRightType = " + dbl.sqlString(YssCons.YSS_SYS_RIGHTTYPE_PORT) + ") rp ON pf.fportcode =  rp.fportcode");
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//            //add by zhouxiang MS01293    设置两个相同的组合代码，不同组合名称时，打开业务处理界面时报错    QDV4赢时胜(测试)2010年6月11日2_B  --------------------------------
//            bufSql.append(" join (select c.fportcode ,max(c.fstartdate) as fstartdate from ").append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" c");
//            bufSql.append(" where c.fcheckstate=1 group by c.fportcode) d on  pf.fportcode=d.fportcode and pf.fstartdate=d.fstartdate");
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
            //---------------end-------------------------------
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FEnabled = 1 ");
            bufSql.append(" AND pf.FPortCode NOT IN (SELECT FPortCode ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Operportfolio"));
            bufSql.append(" WHERE FCheckState = 1)) da");
            //========================== 增加组合的筛选 ===========================
            //MS00003-QDV4.1赢时胜上海2009年2月1日03_A: 参数设置布局分散不便操作
            //add by sunkey 20090411
            //流程的相关信息是存放在pub里的，如果取得到流程的信息就要对组合进行筛选
            if (pub.getFlow() != null) {
                FlowBean flow = (FlowBean) pub.getFlow().get(pub.getUserCode());
                if (flow != null) {
                    String tmpPorts = flow.getFPorts();
                    if (tmpPorts != null && !tmpPorts.trim().equals("")) {
                        //将组合作为筛选条件放入上述sql语句
                        bufSql.append(" Where da.FPortCode in (" + operSql.sqlCodes(tmpPorts) + ")");
                    }
                }
            }
            //=============================End MS00003===========================
            bufSql.append(" ORDER BY FOperPortCode,  FPortType, FType, FPortCode ");

            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                operTree = new OperPortfolioBean();
                if (rs.getInt("FType") == 0) {
                    //拼接操作组合
                    if (rs.getDouble("fporttype") == 0) {
                        NodeCode = rs.getString("FOperPortCode");
                        NodeName = rs.getString("FOperPortName");
                        NodeOrderCode = YssFun.formatNumber(OrderCode++, "000");
                        OperPortCode = NodeCode;
                        parentCode = "[root]";
                        detailPortOrderCode = 0;
                        tmpInt = 0;
                        tmpOperTree = new OperPortfolioBean();
                    } else { //拼接操作组合中的明晰组合
                        if(rs.getString("FPortName") == null){//如果组合名称为null，则表示该当前用户下该操作组合没有有权限的明细组合，则不需将此明细组合数据传至前台
                            continue;
                        }
                        NodeCode = rs.getString("FOperPortCode") + "-" +
                            rs.getString("FPortCode");
                        NodeName = rs.getString("FPortName");
                        NodeOrderCode = YssFun.formatNumber(OrderCode - 1,
                            "000") +
                            YssFun.formatNumber(detailPortOrderCode++,
                                                "000");
                        parentCode = OperPortCode;
                        tmpInt ++ ;
                    }
                    if(tmpInt == 0){//将操作组合放置于临时对象中
                        tmpOperTree.setNodeCode(NodeCode);
                        tmpOperTree.setNodeName(NodeName);
                        tmpOperTree.setParentCode(parentCode);
                        tmpOperTree.setOrderCode(NodeOrderCode);
                        tmpOperTree.setOperPortCode(rs.getString("FOperPortCode"));
                        tmpOperTree.setOperPortName(rs.getString("FOperPortName"));
                        tmpOperTree.setPortCode(rs.getString("FPortCode"));
                        tmpOperTree.setPortName(rs.getString("FPortName"));
                        tmpOperTree.setPortType(rs.getInt("fporttype"));
                        tmpOperTree.setAssetGroupCode(pub.getAssetGroupCode());
                    }
                    if(tmpInt > 0){//如果操作组合下有明细组合，则将该操作组合及其明细组合返回前台显示，否则不显示该操作组合  panjunfang add 20090916
                        operTree.setNodeCode(NodeCode);
                        operTree.setNodeName(NodeName);
                        operTree.setParentCode(parentCode);
                        operTree.setOrderCode(NodeOrderCode);
                        operTree.setOperPortCode(rs.getString("FOperPortCode"));
                        operTree.setOperPortName(rs.getString("FOperPortName"));
                        operTree.setPortCode(rs.getString("FPortCode"));
                        operTree.setPortName(rs.getString("FPortName"));
                        operTree.setPortType(rs.getInt("fporttype"));
                        operTree.setAssetGroupCode(pub.getAssetGroupCode());
                        if (tmpInt == 1) {//在存放明细组合前先存放明细组合对应的操作组合
                            buf.append(tmpOperTree.buildTreeStr());
                        }
                        buf.append(operTree.buildTreeStr());
                    }

                } else { //拼接未包含在操作组合中的明晰组合
                    NodeCode = rs.getString("FPortCode");
                    NodeName = rs.getString("FPortName");
                    NodeOrderCode = YssFun.formatNumber(OrderCode++, "000");
                    parentCode = "[root]";

                    operTree.setNodeCode(NodeCode);
                    operTree.setNodeName(NodeName);
                    operTree.setParentCode(parentCode);
                    operTree.setOrderCode(NodeOrderCode);
                    operTree.setOperPortCode(rs.getString("FOperPortCode"));
                    operTree.setOperPortName(rs.getString("FOperPortName"));
                    operTree.setPortCode(rs.getString("FPortCode"));
                    operTree.setPortName(rs.getString("FPortName"));
                    operTree.setPortType(rs.getInt("fporttype"));
                    operTree.setAssetGroupCode(pub.getAssetGroupCode());
                    buf.append(operTree.buildTreeStr());
                }
            }
            if (buf.length() > 2) {
                sResult = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
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
     * 查询出操作组合表的数据并以一定格式显示，并显示回收站的数据
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        //-- 查询操作组合表，并查出创建者名称，审核者名称和组合名称
        strSql = "select y.* from " +
            "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName from " +
            pub.yssGetTableName("Tb_Para_OperPortfolio") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FPortCode,FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") +
            ") d on a.FPortCode = d.FPortCode where FPortType = '0' " + buildFilterSql() +
            ") y order by y.FCheckState, y.FCreateTime desc";

        return this.builderListViewData(strSql);
    }

    /**
     * 以字符串的形式返回操作组合listview中显示的操作组合的相关数据
     * @param strSql String
     * @return String
     * @throws YssException
     */
    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "" ; //add by huangqirong 2012-08-07 story #2831 增加识别组合群
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setOperPortfolioAttr(rs);
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
           //add by huangqirong 2012-08-07 story #2831 增加识别组合群
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_MTV_EXCHANGERATE + "," + YssCons.YSS_OPER_COST +
                    "," + YssCons.YSS_PRT_ASSETTYPE + "," + YssCons.YSS_PRT_SUBASSETTYPE + "," + YssCons.YSS_TDS_SEATTYPE);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
          //  ---end---
                "\r\f" + this.getListView1ShowCols()+ "\r\f" + "voc" + sVocStr; //modify by huangqirong 2012-08-07 story #2831 增加识别组合群
        } catch (Exception e) {
            throw new YssException("获取操作组合设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 为各项变量赋值
     * @param rs ResultSet
     * @throws SQLException
     */
    public void setOperPortfolioAttr(ResultSet rs) throws SQLException {
        this.opbean.setOperPortCode(rs.getString("FOperPortCode"));
        this.opbean.setOperPortName(rs.getString("FOperPortName"));
        this.opbean.setDesc(rs.getString("FDesc") + "");
        this.opbean.setPortCode(rs.getString("FPortCode"));
        this.opbean.setPortName(rs.getString("FPortName") + "");
        this.opbean.setPortType(rs.getInt("FPortType"));
        opbean.setRecLog(rs);
    }

    /**
     * modify by huangqirong 2012-08-07 story #2831
     * */
    public String getListViewData2() throws YssException {
    	String strSql = "";        
        strSql = "select y.* from " +
            "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName from " +
            pub.yssGetTableName("Tb_Para_OperPortfolio") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FPortCode,FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") +
            ") d on a.FPortCode = d.FPortCode where FPortType = '0' " + buildFilterSql() +
            ") y where y.fcheckstate = 1 order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);
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
     * 调用OperPortfolioBean对象的解析方法来解析前台发送来的字符串
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        if (opbean == null) {
            opbean = new OperPortfolioBean();
            opbean.setYssPub(pub);
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
        opbean.parseRowStr(sRowStr);
        sRecycled = sRowStr;
    }

    /**
     * 调用OperPortfolioBean对象的拼接字符串方法来获取数据字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        return opbean.buildRowStr();
    }

    /**
     * 以字符串的形式返回组合listview中显示的组合的相关数据
     * @return String
     * modify huangqirong 2012-08-07 story #2831 加载所有组合群的操作组合设置
     * @throws YssException
     */
    public String getListViewPort() throws YssException {
        String strName = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sql ="";
        ResultSet rsAG = null;
        String oldGroupCode = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ArrayList alist = this.opbean.getAlist();
        try {
            // List中所有对象的操作组合代码，操作组合名称，描述都是一样的，所以只要取得第一个对象即可
            OperPortfolioBean bean = (OperPortfolioBean) alist.get(0);
            strName = "明细组合";
            //edit by songjie 2011.03.14 不显示启用日期
            sHeader = "组合群代码\t明细组合代码\t明细组合名称";
            //--查询操作组合中所包含组合的所有信息，这样浏览组合的时候就可以显示出来
            oldGroupCode = pub.getAssetGroupCode();            
            sql = "select distinct FAssetGroupcode from Tb_" + oldGroupCode + "_Para_OperPortfolio" + 
            		" where FOperPortCode = " + dbl.sqlString(bean.getOperPortCode()) + " and FPortCode <> ' ' order by FAssetGroupcode";
            rsAG = dbl.openResultSet(sql);
            while(rsAG.next()){
            	pub.setAssetGroupCode(rsAG.getString("FAssetGroupcode"));
            	pub.setPrefixTB(rsAG.getString("FAssetGroupcode"));           	
            	
	            strSql = "select y.* from " +
	                "(select a.FOperPortCode, b.*, c.FUserName as FCreatorName, d.FUserName as FCheckUserName, " +
	                " p.FCuryName as FCuryName, "+//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
	                
	                " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金
	                
	                "e.FAssetGroupName as FAssetGroupName, f.FCuryName as FCurrencyName, ff.FExRateSrcName as FBaseRateSrcName, gg.FExRateSrcName as FPortRateSrcName from " +
	                "(select FOperPortCode, FPortCode from Tb_" + oldGroupCode + "_Para_OperPortfolio "+
	                " where FOperPortCode = '" + bean.getOperPortCode() + "' and FPortCode <> ' ') a " +
	                "left join (select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + ") b on a.Fportcode = b.Fportcode " +
	              //---//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
	                "    left join (select FCuryName,FCuryCode from "+ pub.yssGetTableName("Tb_Para_Currency")+" ) p on p.FCuryCode = b.FCuryCode  " +
	                //---end QDV4上海2010年12月10日02_A-------------
	                
	                //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
	                " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on b.FAimETFCode = s.FSecurityCode " +            
	                //-----add by zhangjun 2012-04-26 ETF联接基金
	                
	                "left join (select FUserCode, FUserName from Tb_Sys_UserList) c on b.FCreator = c.FUserCode " +
	                "left join (select FUserCode, FUserName from Tb_Sys_UserList) d on b.FCheckUser = d.FUserCode " +
	                "left join (select FAssetGroupCode, FAssetGroupName from Tb_Sys_AssetGroup) e on b.FAssetGroupCode = e.FAssetGroupCode " +
	                "left join (select FExRateSrcCode, FExRateSrcName from " + pub.yssGetTableName("Tb_Para_ExRateSource") +
	                " where FCheckState = 1) ff on b.FBaseRateSrcCode = ff.FExRateSrcCode " +
	                "left join (select FExRateSrcCode, FExRateSrcName from " + pub.yssGetTableName("Tb_Para_ExRateSource") +
	                " where FCheckState = 1) gg on b.FPortRateSrcCode = gg.FExRateSrcCode " +
	                "left join (select FCuryCode, FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") +
	                ") f on b.FPortCury = f.FCuryCode) y where y.FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) +
	                "  order by y.fstartdate desc, y.FCheckState, y.FCreateTime desc";
	
	            rs = dbl.openResultSet(strSql);
	            while (rs.next()) {
	            	bufShow.append( (rs.getString("fassetgroupcode") + "").trim()).append("\t");
	                bufShow.append( (rs.getString("FPortCode") + "").trim()).append("\t");
	                //edit by songjie 2011.03.14 不显示启用日期
	                bufShow.append( (rs.getString("FPortName") + "").trim()).append(YssCons.YSS_LINESPLITMARK);
	                //delete by songjie 2011.03.14 不显示启用日期
	//                bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"), YssCons.YSS_DATEFORMAT)).append(YssCons.YSS_LINESPLITMARK);
	
	                PortfolioBean portfolio = new PortfolioBean();
	                portfolio.setYssPub(pub);
	                portfolio.setPortfolioAttr(rs);
	                portfolio.setDataType("operport");
	                bufAll.append(portfolio.buildRowStr()).append(YssCons.
	                    YSS_LINESPLITMARK);
	            }
	            rs.close();
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
            throw new YssException("获取可用" + strName + "信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsAG);
            if(!"".equalsIgnoreCase(oldGroupCode)){
            	pub.setPrefixTB(oldGroupCode);
            	pub.setAssetGroupCode(oldGroupCode);
            }
        }
    }

    public String getOperValue(String sType) throws YssException {
        String strViewPort = "";
        if (sType.equalsIgnoreCase("listViewPort")) {
            strViewPort = getListViewPort();
        }
        return strViewPort;
    }
}
