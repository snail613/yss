package com.yss.main.operdeal.report.reptab.valrep;

import com.yss.main.operdeal.report.reptab.valrep.pojo.*;
import com.yss.util.*;
import com.yss.dsub.*;
import java.util.*;
import java.sql.*;

public class BaseValRep
    extends BaseBean {
    public BaseValRep() {
    }

    public void set(String sPortCode, java.util.Date dBeginDate, java.util.Date dEndDate) throws YssException {
        this.dBeginDate = dBeginDate;
        this.dEndDate = dEndDate;
        this.sPortCode = sPortCode;
        createTmp();
    }

    protected ValRepBean valBean = null;
    protected java.util.Date dBeginDate = null;
    protected java.util.Date dEndDate = null;
    protected String sPortCode = "";

    public void init(Object bean) throws YssException {
        BaseValRep rep = (BaseValRep) bean;
        this.dBeginDate = rep.dBeginDate;
        this.dEndDate = rep.dEndDate;
        this.sPortCode = rep.sPortCode;
    }

    public ArrayList getValRepData() throws YssException {
        return null;
    }

    public void afterValRepData() throws YssException {
    }

    public void insert(ArrayList list) throws YssException {
        Connection conn = null;
        PreparedStatement stm = null;
        boolean bTrans = false;
        String sqlStr = "";
        sqlStr = "insert into tb_data_PortfolioVal (FSecurityCode,FSecurityName,FStorageAmount,FCuryCode,FBaseCuryRate,FPortCuryRate,FFactRate," +
            "FInsStartDate,FInsEndDate,FAvgCost,FMarketPrice,FVstorageCost,FBoughtInt," +
            "FMvalue,FLXVBal,FBFlxBal,FYKVBal,FSyvBaseCuryBal,FFundAllotProportion,FOrder,FTotalcost,FOtherCost,FValDate,FPortCode)" + //添加估值日期，组合代码 MS00491 QDV4中保2009年06月09日01_B by leeyu 20090621
            "　values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; //添加两个问号 MS00491 QDV4中保2009年06月09日01_B by leeyu 20090621
        try {
            if (list == null) {
                return;
            }
            conn = dbl.loadConnection();
            stm = dbl.openPreparedStatement(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = true;
            for (int i = 0; i < list.size(); i++) {
                valBean = (ValRepBean) list.get(i);
                stm.setString(1, valBean.getSecurityCode());
                stm.setString(2, valBean.getSecurityName());
                stm.setDouble(3, valBean.getStorageAmount());
                stm.setString(4, valBean.getCuryCode());
                stm.setDouble(5, valBean.getBaseCuryRate());
                stm.setDouble(6, valBean.getPortCuryRate());
                stm.setDouble(7, valBean.getFactRate());
                stm.setDate(8, YssFun.toSqlDate(valBean.getInsStartDate() == null ? YssFun.toDate("9998-12-31") : valBean.getInsStartDate()));
                stm.setDate(9, YssFun.toSqlDate(valBean.getInsEndDate() == null ? YssFun.toDate("9998-12-31") : valBean.getInsEndDate()));
                stm.setDouble(10, valBean.getAvgCost());
                stm.setDouble(11, valBean.getMarketPrice());
                stm.setDouble(12, valBean.getVstorageCost());
                stm.setDouble(13, valBean.getBoughtInt());
                stm.setDouble(14, valBean.getMvalue());
                stm.setDouble(15, valBean.getLXVBal());
                stm.setDouble(16, valBean.getBFlxBal());
                stm.setDouble(17, valBean.getYKVBal());
                stm.setDouble(18, valBean.getSyvBaseCuryBal());
                stm.setDouble(19, valBean.getFundAllotProportion());
                stm.setString(20, valBean.getOrder()); 
                stm.setDouble(21, valBean.getTotalCost());
                stm.setDouble(22, valBean.getOtherCost());
                stm.setDate(23, YssFun.toSqlDate(this.dEndDate)); //用报表的结束日期 MS00491 QDV4中保2009年06月09日01_B by leeyu 20090621
                stm.setString(24, this.sPortCode); //报表上的组合代码 MS00491 QDV4中保2009年06月09日01_B by leeyu 20090621
                stm.addBatch();
            }
            stm.executeBatch();
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("添加数据到临时表出错", ex);
        } finally {
            dbl.closeStatementFinal(stm);//add by rujiangpeng 20100603打开多张报表系统需重新登录
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private void createTmp() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(" create table tb_data_PortfolioVal ");
        buf.append(" ( ");
        buf.append(" FSecurityCode varchar2(70), ");
        buf.append(" FSecurityName varchar2(250), ");
        buf.append(" FStorageAmount number(22,4), ");
        buf.append(" FCuryCode varchar2(50), ");
        buf.append(" FBaseCuryRate number(20,15), ");
        buf.append(" FPortCuryRate number(20,15), ");
        buf.append(" FFactRate number(20,15), ");
        buf.append(" FInsStartDate date, ");
        buf.append(" FInsEndDate date, ");
        buf.append(" FAvgCost number(22,4), ");
        buf.append(" FMarketPrice number(22,4), ");
        buf.append(" FVstorageCost number(22,4), ");
        buf.append(" FBoughtInt number(22,4), ");
        buf.append(" FMvalue number(22,4), ");
        buf.append(" FLXVBal number(22,4), ");
        buf.append(" FBFlxBal number(22,4), ");
        buf.append(" FYKVBal number(22,4), ");
        buf.append(" FSyvBaseCuryBal number(22,4), ");
        buf.append(" FFundAllotProportion number(22,4), ");
        buf.append(" FTotalcost number(22,4),");
        buf.append(" FOtherCost number(22,4),");
        buf.append(" FValDate date,"); //添加估值日期字段 MS00491 QDV4中保2009年06月09日01_B by leeyu 20090621
        buf.append(" FPortCode varchar2(20),"); //添加组合代码字段 MS00491 QDV4中保2009年06月09日01_B by leeyu 20090621
        buf.append(" FOrder varchar(200), ");
        buf.append(" constraint pk_tb_data_PortfolioVal Primary key (FOrder,FValDate,FPortCode)");
        buf.append(" ) ");
        try {
            if (dbl.yssTableExist("tb_data_PortfolioVal")) {
                //dbl.executeSql("drop table tb_temp_portfoioVal_" + pub.getUserCode()); //MS00491 QDV4中保2009年06月09日01_B by leeyu 20090621
                dbl.executeSql("delete from tb_data_PortfolioVal where FValDate=" + dbl.sqlDate(dEndDate) +
                               " and FPortCode=" + dbl.sqlString(sPortCode)); //这里如果已经存在表了，就先删除相同的数据
            } else {
                dbl.executeSql(buf.toString());
            }
        } catch (Exception ex) {
            throw new YssException("创建临时表数据出错！", ex);
        } finally {
            buf = null;
        }
    }

}
