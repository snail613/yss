package com.yss.main.operdata;

import com.yss.main.dao.IDataSetting;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import com.yss.util.YssException;
import java.sql.ResultSet;
import com.yss.util.YssCons;
import java.sql.SQLException;
import com.yss.util.YssFun;
import java.sql.Connection;
import java.math.BigDecimal;
import java.sql.Statement;

/**
 * <p>Title: 借贷交易数据保证金调整的操作类</p>
 *
 * <p>
 * Description:
 * 该类主要负责期货保证金的信息操作，主要功能如下:
 * BugNo:MS00481
 * QDV4中金2009年06月03日01_A
 * 满足调整股指期货初始保证金金额的功能
 * <br>
 * 1.数据的增、删、改、查操作
 * 2.实体Bean的封装、拆箱
 * 3.数据合法性的检测
 * </br>
 * </p>
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech</p>
 *
 * @author libo 200900707
 * @version 1.0
 */
public class SecLendBailChangeAdmin
    extends BaseDataSettingBean implements IDataSetting {

    private SecLendBailChangeAdmin filterType; //parseRowStr方法中 用于筛选
    private SecLendBailChgBean secLendBailChange = new SecLendBailChgBean(); //bean数据
    public SecLendBailChangeAdmin() {
    }

    /**
     * 输入检查，检查新增，复制，修改数据时，是否主键重复
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("TB_DATA_secLendBailChange"),
                               "FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FChangeDate", //主键--要判断的字段
                               this.secLendBailChange.getSSecurityCode() + "," + this.secLendBailChange.getSPortCode() + "," + this.secLendBailChange.getSBrokerCode() + "," + this.secLendBailChange.getSInvMgrCode() + "," + this.secLendBailChange.getSChangeDate(),
                               this.secLendBailChange.getSOldSecurityCode() + "," + this.secLendBailChange.getSOldPortCode() + "," + this.secLendBailChange.getSOldBrokerCode() + "," + this.secLendBailChange.getSOldInvMgrCode() + "," + this.secLendBailChange.getSOldChangeDate()
            );

    }

    /**
     * 新增数据
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("TB_DATA_secLendBailChange") +
                "(FSecurityCode, FPortCode,FBrokerCode,FInvMgrCode,FChangeDate,FChangeMoney,FCashAccCode,FBailAccCode,FDesc," +
                "FCheckState,FCreator,FCreateTime,FCheckUser,FCHECKTIME) " +
                "values(" +
                dbl.sqlString(this.secLendBailChange.getSSecurityCode()) + "," + //证券代码
                dbl.sqlString(this.secLendBailChange.getSPortCode()) + "," +     //组合代码
                //<--edited by libo 20090714 券商投资经理设为不为必输
                dbl.sqlString(this.secLendBailChange.getSBrokerCode().length() == 0 ? " " : this.secLendBailChange.getSBrokerCode()) + "," + //券商代码
                dbl.sqlString(this.secLendBailChange.getSInvMgrCode().length() == 0 ? " " : this.secLendBailChange.getSInvMgrCode()) + "," + //投资经理代码
                //edited by libo 20090714 券商投资经理设为不为必输-->

                dbl.sqlDate(YssFun.toDate(this.secLendBailChange.getSChangeDate())) + "," +  //调整日期
                this.secLendBailChange.getSChangeMoney() + "," +                             //调整金额
                dbl.sqlString(this.secLendBailChange.getSCashAccCode()) + "," +          //初始保证金帐户
                dbl.sqlString(this.secLendBailChange.getSBailAccCode()) + "," +        //变动保证金帐户
                dbl.sqlString( (this.secLendBailChange.getSDesc().length() == 0) ? " " : this.secLendBailChange.getSDesc()) + "," + //描述
                (pub.getSysCheckState() ? "0" : "1") + "," +        //FCheckState
                dbl.sqlString(this.creatorCode) + "," +             //FCreator
                dbl.sqlString(this.creatorTime) + "," +             //FCreateTime
                (	pub.getSysCheckState() ? "' '" :
                	//-----------edited by zhouxiang MS00151--------------
                	dbl.sqlString(this.creatorCode)) + ","+                //FCheckUser
                	//--------------end by zhouxiang MS00151--------------
                	dbl.sqlString(this.checkTime) +				
                ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增期货保证金数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return this.buildRowStr();
    }

    /**
     * 修改数据
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        //--加入事件处理 edited by libo 20090710
        boolean bTrans = false;
        String strSql = "";
        Connection conn = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            strSql = "update " + pub.yssGetTableName("TB_DATA_secLendBailChange") +
                " set FSecurityCode = " + dbl.sqlString(this.secLendBailChange.getSSecurityCode()) + //证券代码
                ",FPortCode = " + dbl.sqlString(this.secLendBailChange.getSPortCode()) +             //组合代码
                //<--edited by libo 20090714 券商投资经理设为不为必输
                ",FBrokerCode = " + dbl.sqlString(this.secLendBailChange.getSBrokerCode().trim().length() == 0 ? " " : this.secLendBailChange.getSBrokerCode()) + //券商代码
                ",FInvMgrCode = " + dbl.sqlString(this.secLendBailChange.getSInvMgrCode().trim().length() == 0 ? " " : this.secLendBailChange.getSInvMgrCode()) + //投资经理代码
                //edited by libo 20090714 券商投资经理设为不为必输-->

                ",FChangeDate = " + dbl.sqlDate(this.secLendBailChange.getSChangeDate()) +   //调整日期
                ",FChangeMoney = " + this.secLendBailChange.getSChangeMoney() +              //调整金额
                ",FDesc = " + dbl.sqlString(this.secLendBailChange.getSDesc().trim().length() == 0 ? " " : this.secLendBailChange.getSDesc()) + //描述
                ",FCashAccCode = " + dbl.sqlString(this.secLendBailChange.getSCashAccCode   ()) +       //初始保证金帐户
                ",FBailAccCode = " + dbl.sqlString(this.secLendBailChange.getSBailAccCode()) +   //变动保证金帐户
                " ,FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                " ,FCreator = " + dbl.sqlString(this.creatorCode) +
                " ,FCreateTime = " + dbl.sqlString(this.creatorTime) +
                //edit by songjie 2011.11.26 BUG 2832 QDV4赢时胜(测试)2011年9月22日01_B 修改不能保存修改人
                " ,FCheckUser = " + dbl.sqlString(this.creatorCode) +
                " ,FCHECKTIME = " + dbl.sqlString(this.checkTime) +
                " where FSecurityCode = " + dbl.sqlString(this.secLendBailChange.getSOldSecurityCode()) +    //证券代码
                " and FPortCode= " + dbl.sqlString(this.secLendBailChange.getSOldPortCode()) +               //组合代码
                " and FBrokerCode= " + dbl.sqlString(this.secLendBailChange.getSOldBrokerCode()) +           //券商代码
                " and FInvMgrCode= " + dbl.sqlString(this.secLendBailChange.getSOldInvMgrCode()) +           //投资经理
                " and FChangeDate= " + dbl.sqlDate(this.secLendBailChange.getSOldChangeDate());              //变动日期
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改期货保证金信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    /**
     * 删除数据，先放入回收站
     * 修改的实质是把审核状态改变为2
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("TB_DATA_secLendBailChange") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = " +
                dbl.sqlString(this.secLendBailChange.getSSecurityCode()) +
                " and FPortCode= " + dbl.sqlString(this.secLendBailChange.getSPortCode()) + //<--add by libo 20090710修改主键
                " and FBrokerCode= " + dbl.sqlString(this.secLendBailChange.getSBrokerCode()) +
                " and FInvMgrCode= " + dbl.sqlString(this.secLendBailChange.getSInvMgrCode()) +
                " and FChangeDate= " + dbl.sqlDate(this.secLendBailChange.getSChangeDate())
                //add by libo 20090710修改主键-->
                ;
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除期货保证金数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 回收站还原功能，即修改审核状态
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();     //获取一个连接
        boolean bTrans = false;                     //代表是否开始了事务
        String strSql = "";
        String[] arrData = null;
        Statement st = null;                        //用于批量处理
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            st = conn.createStatement();

            arrData = this.secLendBailChange.getSRecycled().split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "update " + pub.yssGetTableName("TB_DATA_secLendBailChange") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSecurityCode = " + dbl.sqlString(this.secLendBailChange.getSSecurityCode()) +
                    " and FPortCode= " + dbl.sqlString(this.secLendBailChange.getSPortCode()) +
                    " and FBrokerCode= " + dbl.sqlString(this.secLendBailChange.getSBrokerCode()) +
                    " and FInvMgrCode= " + dbl.sqlString(this.secLendBailChange.getSInvMgrCode()) +
                    " and FChangeDate= " + dbl.sqlDate(this.secLendBailChange.getSChangeDate());
                st.addBatch(strSql);
            }
            st.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核保证金数据信息出错", e);
        } finally {
            dbl.closeStatementFinal(st);
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
     * 回收站清除功能，直接将数据从数据库中删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        Connection conn = null;
        boolean bTrans = true;
        String[] arrData = null;
        Statement st = null;
        try {
            //获取、设置数据库相关的资源
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            st = conn.createStatement();

            //业务处理，数据的拆解、删除，持久化操作
            arrData = this.secLendBailChange.getSRecycled().split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "delete from " +
                    pub.yssGetTableName("TB_DATA_secLendBailChange") +
                    " where FSecurityCode = " + dbl.sqlString(this.secLendBailChange.getSSecurityCode()) +
                    " and FPortCode= " + dbl.sqlString(this.secLendBailChange.getSPortCode()) +
                    " and FBrokerCode= " + dbl.sqlString(this.secLendBailChange.getSBrokerCode()) +
                    " and FInvMgrCode= " + dbl.sqlString(this.secLendBailChange.getSInvMgrCode()) +
                    " and FChangeDate= " + dbl.sqlDate(this.secLendBailChange.getSChangeDate()) ;
                st.addBatch(strSql);
            }
            st.executeBatch();
            //事物控制
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     *  查询数据
     * @return String
     * @throws YssException
     */
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
     * 获取所有数据，审核、未审核、回收站数据
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            //通过spring配置获取表头信息
            sHeader = getListView1Headers();
            //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
          //add by yangheng MS01310  分页查询
            if (this.filterType!=null&&this.filterType.secLendBailChange.getSChangeDate().equals("1900-01-01")) {
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            sqlStr =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName" + //创建人，核查人
                ",d.FSecurityName as FSecurityName" +   //交易证券
                ",d2.FPortName as FPortName" +          //投资组合
                ",d3.FBrokerName as FBrokerName" +      //交易券商
                ",d4.FInvMgrName as FInvMgrName" +      //投资经理
                ",d5.FCashAccName as FCashAccName" +    //初始保证金帐户
                ",d6.FCashAccName as FBailAccName" +  //变动保证金帐户

                " from " + pub.yssGetTableName("TB_DATA_secLendBailChange") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +      //这个保留
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +   //这个保留

                " left join (select FSecurityCode,FSecurityName,FStartDate,FHandAmount,FFactor,FCatCode " +  //交易证券
                " from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1) d on a.FSecurityCode = d.FSecurityCode " +
               //modify by zhangfa MS01623    新建一条期权保证金调整数据，会产生多条记录    QDV4赢时胜(测试)2010年08月17日2_B    
                " left join (select FPortCode, FPortName " +        //投资组合 edit by songjie 2011.03.15 不以最大的启用日期查询数据
                " from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " where " + 
                " FCheckState = 1) d2 on a.FPortCode = d2.FPortCode " +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
               //----------------------------------------------------------------------------------------------------

                " left join (select FBrokerCode, FBrokerName, FStartDate " +    //交易券商
                " from " + pub.yssGetTableName("Tb_Para_Broker") +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " where " + 
                " FCheckState = 1) d3 on a.FBrokerCode = d3.FBrokerCode " +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//

                //edited by zhouxiang 2010.7.19 MS01450 投资经理出现多条数据
                " left join (select m.FInvMgrCode, m.FInvMgrName, m.FStartDate " +    //投资经理
                " from " + pub.yssGetTableName("Tb_Para_InvestManager m") +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                "  join (select FInvMgrCode,max(fstartdate) as FStartDate"+
//                " from " +pub.yssGetTableName("Tb_Para_InvestManager")+
//                " group by finvmgrcode) e on  m.finvmgrcode=e.finvmgrcode and m.fstartdate=e.fstartdate"+
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " where " + 
                " m.FCheckState = 1) d4 on a.FInvMgrCode = d4.FInvMgrCode " +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //-----------------end------------------------------------
                //modify by zhangfa MS01650    启用日期不同的现金账户引起新建调整保证金产生多比数据    QDV4赢时胜(上海开发部)2010年08月26日01_B    
                " left join (select FCashAccCode, FCashAccName " +  //初始保证金帐户//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " where " + 
                " FCheckState = 1) d5 on a.FCashAccCode = d5.FCashAccCode " +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " left join (select FCashAccCode, FCashAccName " +  //变动保证金帐户 //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                " from  " + pub.yssGetTableName("Tb_Para_CashAccount") +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " where " + 
                " FCheckState = 1) d6 on a.FBailAccCode = d6.FCashAccCode " +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
               //-----------------------------------------------------------------------------------------------------------------------
                buildFilterSql() +
                " order by a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(sqlStr);
            yssPageInationBean.setsTableName("secLendBailChange");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
            	//---add by sognjie 2011.11.26 BUG 2832 QDV4赢时胜(测试)2011年9月22日01_B start---//
            	bufShow.append(rs.getString("FSecurityCode")).append("\t");
            	bufShow.append(rs.getString("FSecurityName")).append("\t");
            	bufShow.append(rs.getString("FPortCode")).append("\t");
            	bufShow.append(rs.getString("FPortName")).append("\t");
            	bufShow.append(rs.getString("FBrokerCode")).append("\t");
            	bufShow.append(rs.getString("FBrokerName")).append("\t");
            	bufShow.append(rs.getString("FCashAccCode")).append("\t");
            	bufShow.append(rs.getString("FCashAccName")).append("\t"); 
            	bufShow.append(rs.getString("FBailAccCode")).append("\t");
            	bufShow.append(rs.getString("FBailAccName")).append("\t"); 
            	bufShow.append(YssFun.formatDate(rs.getDate("FChangeDate"))).append("\t");
            	bufShow.append(rs.getBigDecimal("FChangeMoney")).append("\t");	
            	bufShow.append(rs.getString("FDesc")).append("\t");
            	bufShow.append(rs.getInt("FCheckState")).append("\t");
            	bufShow.append(rs.getString("FCreator") + "").append("\t");
            	bufShow.append(rs.getString("FCreateTime") + "").append("\t");
            	bufShow.append(rs.getString("FCheckUser") + "").append("\t");
            	bufShow.append(rs.getString("FCheckTime") + "").append(YssCons.YSS_LINESPLITMARK);            				
            	//---add by sognjie 2011.11.26 BUG 2832 QDV4赢时胜(测试)2011年9月22日01_B end---//
            	//---delete by sognjie 2011.11.26 BUG 2832 QDV4赢时胜(测试)2011年9月22日01_B start---//
//            	bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
//                    append(YssCons.YSS_LINESPLITMARK);
            	//---delete by sognjie 2011.11.26 BUG 2832 QDV4赢时胜(测试)2011年9月22日01_B end---//
                setResultSetAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK); //"/f/f"
            }

            //删除数据结尾的两个\f
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取期货保证金数据信息出错", e); ///改成你的名字
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
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
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
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
            this.secLendBailChange.setSRecycled(sRowStr);        //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.secLendBailChange.setSSecurityCode(reqAry[0]);  //交易证券
            this.secLendBailChange.setSPortCode(reqAry[1]);      //投资组合
            this.secLendBailChange.setSBrokerCode(reqAry[2]);    //投资券商
            this.secLendBailChange.setSInvMgrCode(reqAry[3]);    //投资经理
            this.secLendBailChange.setSChangeDate(reqAry[4]);    //调整日期

            if (YssFun.isNumeric(reqAry[5])) {                  //调整金额
                this.secLendBailChange.setSChangeMoney(new BigDecimal(reqAry[5].trim().length() == 0 ? "0" : reqAry[5]));
            }
            this.secLendBailChange.setSDesc(reqAry[6]);          //描述
            this.checkStateId = YssFun.toInt(reqAry[7]);        //状态

            this.secLendBailChange.setSCashAccCode(reqAry[8]);   //初始保证金帐户
            this.secLendBailChange.setSBailAccCode(reqAry[9]); //变动保证金帐户
            this.secLendBailChange.setSOldSecurityCode(reqAry[10]);  //旧证券代码
            this.secLendBailChange.setSOldPortCode(reqAry[11]);      //旧投资组合
            this.secLendBailChange.setSOldBrokerCode(reqAry[12]);    //旧投资券商
            this.secLendBailChange.setSOldInvMgrCode(reqAry[13]);    //旧投资经理
            this.secLendBailChange.setSOldChangeDate(reqAry[14]);    //旧调整日期
            //-----------20100427 蒋锦 添加 南方东英期权需求----------//
            //使用品种类型作为筛选条件适应在不同菜单条打开的情况
            //MS01134 增加股票期权和股指期权业务
            this.secLendBailChange.setCategoryCode(reqAry[15]);		//品种类型
            //-----------------------------------------------------//
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SecLendBailChangeAdmin();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析期货保证金信息出错", e);
        }

    }

    /**
     * 把数据拼接为字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.secLendBailChange.getSSecurityCode()).append("\t");  //交易证券
        buf.append(this.secLendBailChange.getSPortCode()).append("\t");      //投资组合

        buf.append(this.secLendBailChange.getSBrokerCode()).append("\t");    //投资券商
        buf.append(this.secLendBailChange.getSInvMgrCode()).append("\t");    //投资经理

        buf.append(this.secLendBailChange.getSChangeDate()).append("\t");    //调整日期
        buf.append(this.secLendBailChange.getSChangeMoney()).append("\t");   //调整金额
        buf.append(this.secLendBailChange.getSDesc()).append("\t");          //描述

        buf.append(this.secLendBailChange.getSSecurityName()).append("\t");  //交易证券名
        buf.append(this.secLendBailChange.getSPortName()).append("\t");      //投资组合名
        buf.append(this.secLendBailChange.getSBrokerName()).append("\t");    //投资券商名
        buf.append(this.secLendBailChange.getSInvMgrName()).append("\t");    //投资经理名

        buf.append(this.secLendBailChange.getSCashAccCode   ()).append("\t");   //初始保证金帐户
        buf.append(this.secLendBailChange.getSBailAccCode()).append("\t"); //变动保证金帐户
        buf.append(this.secLendBailChange.getSCashAccName()).append("\t");   //初始保证金帐户名
        buf.append(this.secLendBailChange.getSBailAccName()).append("\t"); //变动保证金帐户名
        

        buf.append(super.buildRecLog());
        return buf.toString();

    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * 拼接SQL语句
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = sResult + "where 1=1";

            if (this.filterType.secLendBailChange.getSSecurityCode().length() != 0) {    //交易证券
                sResult = sResult + " and a.FSecurityCode = '" +
                    filterType.secLendBailChange.getSSecurityCode().replaceAll("'", "''") + "'";
            }
            if (this.filterType.secLendBailChange.getSPortCode().length() != 0) {        //投资组合
                sResult = sResult + " and a.FPortCode = '" +
                    filterType.secLendBailChange.getSPortCode().replaceAll("'", "''") +
                    "'";
            }
            if (this.filterType.secLendBailChange.getSInvMgrCode().length() != 0) {      //投资经理
                sResult = sResult + " and a.FInvMgrCode like '" +
                    filterType.secLendBailChange.getSInvMgrCode().replaceAll("'", "''") +
                    "%'";
            }

            if (this.filterType.secLendBailChange.getSBrokerCode().length() != 0) {      //投资券商
                sResult = sResult + " and a.FBrokerCode = '" +
                    filterType.secLendBailChange.getSBrokerCode().replaceAll("'", "''") +
                    "'";
            }

            if (this.filterType.secLendBailChange.getSCashAccCode   ().length() != 0) { //初始保证金帐户
                sResult = sResult + " and a.FCashAccCode = '" +
                    filterType.secLendBailChange.getSCashAccCode   ().replaceAll("'", "''") +
                    "'";
            }
            if (this.filterType.secLendBailChange.getSBailAccCode().length() != 0) { //变动保证金帐户
                sResult = sResult + " and a.FBailAccCode = '" +
                    filterType.secLendBailChange.getSBailAccCode().replaceAll("'", "''") +
                    "'";
            }

            if (this.filterType.secLendBailChange.getSChangeDate().length() != 0 &&      //成交日期
                !this.filterType.secLendBailChange.getSChangeDate().equals("9998-12-31")) {
                sResult = sResult + " and a.FChangeDate = " +
                    dbl.sqlDate(YssFun.toDate(this.filterType.secLendBailChange.getSChangeDate()));
            }
            
            //-----------20100427 蒋锦 添加 南方东英期权需求----------//
            //使用品种类型作为筛选条件适应在不同菜单条打开的情况
            //MS01134 增加股票期权和股指期权业务
            if (this.filterType.secLendBailChange.getCategoryCode().length() != 0 ) {	//品种类型
				sResult = sResult
						+ " and d.FCatCode = '"
						+ filterType.secLendBailChange.getCategoryCode()
								.replaceAll("'", "''") + "'";
            }
            //----------------------------------------------------//
        }
        return sResult;
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException,
        SQLException {
        this.secLendBailChange.setSSecurityCode(rs.getString("FSecurityCode"));  //证券代码
        this.secLendBailChange.setSPortCode(rs.getString("FPortCode"));          //组合代码
        this.secLendBailChange.setSBrokerCode(rs.getString("FBrokerCode"));      //券商代码
        this.secLendBailChange.setSInvMgrCode(rs.getString("FInvMgrCode"));      //投资经理代码
        this.secLendBailChange.setSChangeDate(rs.getString("FChangeDate"));      //调整日期
        this.secLendBailChange.setSChangeMoney(rs.getBigDecimal("FChangeMoney"));//调整金额
        this.secLendBailChange.setSDesc(rs.getString("FDesc"));                  //描述
        this.secLendBailChange.setSSecurityName(rs.getString("FSecurityName"));  //证券名称
        this.secLendBailChange.setSPortName(rs.getString("FPortName"));          //组合名称
        //<--edited by libo 20090714 券商投资经理设为不为必输
        //因为插入的地方时插入的" "，所以不必判断null，直接取数就OK了 sunkey
        this.secLendBailChange.setSBrokerName(rs.getString("FBrokerName"));      //券商名称
        this.secLendBailChange.setSInvMgrName(rs.getString("FInvMgrName"));      //投资经理名称
        //edited by libo 20090714 券商投资经理设为不为必输-->
        this.secLendBailChange.setSCashAccCode(rs.getString("FCashAccCode"));        //初始保证金帐户
        this.secLendBailChange.setSBailAccCode(rs.getString("FBailAccCode"));    //变动保证金帐户
        this.secLendBailChange.setSCashAccName(rs.getString("FCashAccName"));        //初始保证金帐户名称
        this.secLendBailChange.setSBailAccName(rs.getString("FBailAccName"));    //变动保证金帐户名称
        super.setRecLog(rs);
    }
}
