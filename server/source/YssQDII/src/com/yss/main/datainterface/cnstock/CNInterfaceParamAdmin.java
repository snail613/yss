package com.yss.main.datainterface.cnstock;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssException;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadTypeBean;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import com.yss.main.operdeal.datainterface.cnstock.pojo.TradeFeeBean;
import com.yss.util.YssFun;
import java.sql.ResultSet;
import com.yss.main.operdeal.datainterface.cnstock.pojo.SeatPortfolioBean;
import com.yss.util.YssCons;
import com.yss.main.dao.IYssConvert;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ExchangeBondBean;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import com.yss.main.operdeal.datainterface.cnstock.pojo.FeeWayBean;

/**
 * QDV4.1赢时胜（上海）2009年4月20日03_A
 * MS00003
 * 数据接口参数设置数据库访问对象
 * create by javachaos
 * 2009-06-17
 */
public class CNInterfaceParamAdmin
    extends BaseDataSettingBean implements IDataSetting, IYssConvert {
    public CNInterfaceParamAdmin() {
        rslist = new ArrayList();
        fmlist = new ArrayList();
        eblist = new ArrayList();
        bflist = new ArrayList();
    }

    private ReadTypeBean rt; //读数处理方式参数对象
    private List rslist; //读数处理方式参数对象集合
    private TradeFeeBean fm; //交易费用计算方式对象
    private List fmlist; //交易费用计算方式对象集合
    private ExchangeBondBean eb; //交易所债券参数对象
    private List eblist; //交易所债券参数对象集合
    private FeeWayBean bf; //费用承担方向对象
    private List bflist; //费用承担方向对象集合
    private String portCode = ""; //删除的组合
    private String tab = ""; //当前操作的tab页
    private String msg = "";
    /**
     * 验证信息
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        if (YssCons.OP_ADD == btOper) {
            String strSql = "";
            ResultSet rs = null;
            List etmp = new ArrayList();
            try {
                for (int i = 0; i < eblist.size(); i++) { //遍历
                    eb = (ExchangeBondBean) eblist.get(i);
                    strSql = "select eb.*,pp.fportname,sa.fassetgroupname from " + pub.yssGetTableName("TB_DAO_ExchangeBond") +
                        " eb left join " + pub.yssGetTableName("Tb_Para_Portfolio") +
                        " pp on eb.fportcode = pp.FPortCode left join " + pub.yssGetTableName("tb_sys_assetgroup") +
                        " sa on eb.fassetgroupcode = sa.fassetgroupcode where eb.FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) +
                        " and eb.FPortCode = " + dbl.sqlString(eb.getPortCode()) +
                        "and eb.FMarket = " + dbl.sqlString(eb.getMarket()) +
                        " and eb.FCatCode = " + dbl.sqlString(eb.getCatCode()); //查看当前配置是否存在，如果存在则提示用户
                    rs = dbl.openResultSet(strSql);
                    if (rs.next()) { //存在
                        eb.setAssetGroupName(rs.getString("fassetgroupname")); //组合群名称
                        eb.setPortName(rs.getString("fportname")); //组合名称
                        msg += eb.buildMsgStr();
                    } else {
                        etmp.add(eb); //增加
                    }
                    dbl.closeResultSetFinal(rs); //每次循环都要关闭游标 sunkey@Modify
                }
                eblist = etmp; //可以增加部分
            } catch (Exception e) {
                throw new YssException("交易所债券参数验证出错", e);
            } finally {
                dbl.closeResultSetFinal(rs);
            }
        }
    }

    /**
     * 增加
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String strSql = "";
        String errmsg = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if ("0".equals(tab)) { //读取处理方式
                errmsg = "读取处理方式增加出错！";
                //删除数据
                strSql = "delete from "+ pub.yssGetTableName("TB_DAO_ReadType") + " where FPortCode in (" +
                    strWhere(portCode) + ") and FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode());
                dbl.executeSql(strSql);
                //增加
                for (int i = 0; i < rslist.size(); i++) {
                    rt = (ReadTypeBean) rslist.get(i);
                    strSql =
                        "insert into " + pub.yssGetTableName("TB_DAO_ReadType") + " (FAssetGroupCode,FPortCode,FAssetClass" +
                        ",FWBSBelong,FExchangePreci,FSHNum,FParameter,FHOLIDAYSCODE,FExchangeFhggain, FCURRENCYCODEA, FCURRENCYCODESHB" +//fanghaoln 20100427 MS01079 QDV4招商基金2010年4月9日01_B 
                        ", FCURRENCYCODESZB, FINVMGRCODESH, FINVMGRCODESZ, FHOLIDAYSCODESH, FHOLIDAYSCODESZ, FDELAYDATEA, FDELAYDATEB) values(" +//edit by yanghaiming 20100417 增加B股业务的读数处理
                        dbl.sqlString(pub.getAssetGroupCode()) + "," +
                        dbl.sqlString(rt.getPortCode()) + "," +
                        dbl.sqlString(rt.getAssetClass()) + "," +
                        dbl.sqlString(rt.getWBSBelong()) + "," +
                        rt.getExchangePreci() + "," +
                        dbl.sqlString(rt.getShNum()) + "," +
                        dbl.sqlString(rt.getParameter()) + "," +
                        dbl.sqlString(rt.getHolidaysCode()) + "," +
                        rt.getExchangeFhggain() + "," +//fanghaoln 20100427 MS01079 QDV4招商基金2010年4月9日01_B 
                        dbl.sqlString(rt.getCurrencyCodeA()) + "," +
                        dbl.sqlString(rt.getCurrencyCodeSHB()) + "," +
                        dbl.sqlString(rt.getCurrencyCodeSZB()) + "," +
                        dbl.sqlString(rt.getInvMgrCodeSH()) + "," +
                        dbl.sqlString(rt.getInvMgrCodeSZ()) + "," +
                        dbl.sqlString(rt.getHolidaysCodeSH()) + "," +
                        dbl.sqlString(rt.getHolidaysCodeSZ()) + "," +
                        rt.getDelayDateA() + "," +
                        rt.getDelayDateB() + ")";
                    dbl.executeSql(strSql); //执行
                    //---------------add by yanghaiming 20100302 MS00883 QDII4.1赢时胜上海2010年02月12日04_B
                    if(rt.getParameter().equalsIgnoreCase("04")){
                    	strSql = "update " + pub.yssGetTableName("Tb_Para_Fee") + " a " +
                    			"set a.fassumeman=0,a.franktype=0 where a.ffeecode in ('YSS_SHYJ','YSS_SZYJ')";//story 1578 update by zhouwei 20111103 上海佣金和深圳佣金
                    	dbl.executeSql(strSql);
                    }else{
                    	strSql = "update " + pub.yssGetTableName("Tb_Para_Fee") + " a " +
            					"set a.fassumeman=2,a.franktype=0 where a.ffeecode in ('YSS_SHYJ','YSS_SZYJ')";//story 1578 update by zhouwei 20111103 上海佣金和深圳佣金
                    	dbl.executeSql(strSql);
                    }
                    //--------------------------------------------------------
                }
            } else if ("1".equals(tab)) { //交易所债券参数设置
                errmsg = "交易所债券参数增加出错！";
                for (int i = 0; i < eblist.size(); i++) {
                    eb = (ExchangeBondBean) eblist.get(i);
                    //插入数据
                    strSql =
                        "insert into " + pub.yssGetTableName("TB_DAO_ExchangeBond") + " (FAssetGroupCode,FPortCode,FMarket," +
                        "FCatCode,FBondTradeType,FCommisionType,FInteDutyType,FStartDate,FCreator,FCreateTime,FCheckState) values(" +
                        dbl.sqlString(pub.getAssetGroupCode()) + "," +
                        dbl.sqlString(eb.getPortCode()) + "," +
                        dbl.sqlString(eb.getMarket()) + "," +
                        dbl.sqlString(eb.getCatCode()) + "," +
                        dbl.sqlString(eb.getBondTradeType()) + "," +
                        dbl.sqlString(eb.getCommisionType()) + "," +
                        dbl.sqlString(eb.getInteDutyType()) + "," +
                        dbl.sqlDate(eb.getStartDate()) + "," +
                        dbl.sqlString(pub.getUserCode()) + "," +
                        dbl.sqlString(YssFun.formatDate(new Date())) + ",0)";
                    dbl.executeSql(strSql); //执行
                }
            } else if ("2".equals(tab)) { //交易费用计算方式
                errmsg = "交易费用计算方式增加出错！";
                for (int i = 0; i < fmlist.size(); i++) { //遍历交易费用计算方式对象集合
                    fm = (TradeFeeBean) fmlist.get(i); //获得交易奋勇计算方式对象
                    //删除数据
                    strSql =
                        "delete from " + pub.yssGetTableName("TB_DAO_TradeFee") + " where FAssetGroupCode = " +
                        dbl.sqlString(pub.getAssetGroupCode()) +
                        " and FPortCode = " +
                        dbl.sqlString(fm.getPortCode());
                    dbl.executeSql(strSql); //执行
                    //增加
                    strSql =
                        "insert into " + pub.yssGetTableName("TB_DAO_TradeFee") + " (FAssetGroupCode,FPortCode,FTradeDetails" +
                        ",FTradeNum,FTradeSum) values(" +
                        dbl.sqlString(pub.getAssetGroupCode()) + "," +
                        dbl.sqlString(fm.getPortCode()) + "," +
                        dbl.sqlString(fm.getTradeDetails()) + "," +
                        dbl.sqlString(fm.getTradeNum()) + "," +
                        dbl.sqlString(fm.getTradeSum()) + ")";
                    dbl.executeSql(strSql); //执行
                }
            } else if ("3".equals(tab)) { //费用承担方向
                errmsg = "费用承担方向增加出错！";
                for (int i = 0; i < bflist.size(); i++) { //遍历费用承担方向对象集合
                    bf = (FeeWayBean) bflist.get(i); //获得费用承担方向对象
                    //删除数据
                    strSql =
                        "delete from " + pub.yssGetTableName("TB_DAO_FeeWay") + " where FAssetGroupCode = " +
                        dbl.sqlString(pub.getAssetGroupCode()) +
                        " and FPortCode = " + dbl.sqlString(bf.getPortCode()) +
                        " and FBrokerCode = " +
                        dbl.sqlString(bf.getBrokerCode()) + " and FSeatCode = " +
                        dbl.sqlString(bf.getSeatCode());
                    dbl.executeSql(strSql); //执行
                    //增加
                    strSql =
                        "insert into " + pub.yssGetTableName("TB_DAO_FeeWay") + " (FAssetGroupCode,FPortCode,FBrokerCode" +
                        ",FSeatCode,FProductBear,FBrokerBear) values(" +
                        dbl.sqlString(pub.getAssetGroupCode()) + "," +
                        dbl.sqlString(bf.getPortCode()) + "," +
                        dbl.sqlString(bf.getBrokerCode()) + "," +
                        dbl.sqlString(bf.getSeatCode()) + "," +
                        dbl.sqlString(bf.getProductBear()) + "," +
                        dbl.sqlString(bf.getBrokerBear()) + ")";
                    dbl.executeSql(strSql); //执行
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(errmsg, e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * 修改数据
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            eb = (ExchangeBondBean) eblist.get(0); //获得交易所债券参数对象（原始数据）
            strSql =
                " where FCheckState = 0 and  FAssetGroupCode = " +
                dbl.sqlString(pub.getAssetGroupCode()) + " and FPortCode = " +
                dbl.sqlString(eb.getPortCode()) + " and FMarket = " +
                dbl.sqlString(eb.getMarket()) + " and FCatCode = " +
                dbl.sqlString(eb.getCatCode());
            eb = (ExchangeBondBean) eblist.get(1); //获得交易所债券参数对象（新数据）
            strSql = new StringBuffer("update ").append(pub.yssGetTableName("TB_DAO_ExchangeBond"))
                .append(" set FMarket = ")
                .append(dbl.sqlString(eb.getMarket()))
                .append(" , FCatCode = ")
                .append(dbl.sqlString(eb.getCatCode()))
                .append(" , FBondTradeType = ")
                .append(dbl.sqlString(eb.getBondTradeType()))
                .append(" , FCommisionType = ")
                .append(dbl.sqlString(eb.getCommisionType()))
                .append(" , FInteDutyType = ")
                .append(dbl.sqlString(eb.getInteDutyType()))
                .append(" , FStartDate = ")
                .append(dbl.sqlDate(eb.getStartDate()))
                .append(" , FCreator = ")
                .append(dbl.sqlString(pub.getUserCode()))
                .append(" , FCreateTime = ")
                .append(dbl.sqlString(YssFun.formatDate(new Date())))
                .append(strSql).toString();
            dbl.executeSql(strSql); //执行
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("交易所债券参数修改出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * 删除数据
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            eb = (ExchangeBondBean) eblist.get(0);
            //删除数据
            strSql =
                "delete from " + pub.yssGetTableName("TB_DAO_ExchangeBond") + " where FCheckState = 0 and FAssetGroupCode = " +
                dbl.sqlString(pub.getAssetGroupCode()) + " and FPortCode = " +
                dbl.sqlString(eb.getPortCode()) + " and FMarket = " +
                dbl.sqlString(eb.getMarket()) + " and FCatCode = " +
                dbl.sqlString(eb.getCatCode());
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("交易所债券参数删除出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 审核/反审核信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            eb = (ExchangeBondBean) eblist.get(0);
            //审核/反审核
            strSql = "update " + pub.yssGetTableName("TB_DAO_ExchangeBond") + " set FCheckState = case when FCheckState = 0 then 1 else 0 end " +
                " , FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = " + dbl.sqlString(YssFun.formatDate(new Date())) +
                "where FPortCode = " +
                dbl.sqlString(eb.getPortCode()) + " and FAssetGroupCode = " +
                dbl.sqlString(pub.getAssetGroupCode()) + " and FMarket = " +
                dbl.sqlString(eb.getMarket()) + " and FCatCode = " +
                dbl.sqlString(eb.getCatCode());
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("交易所债券参数审核/反审核出错", e);
        } finally {
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

    public void deleteRecycleData() throws YssException {
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    /**
     * 包含券商和席位的TreeView
     * @return String
     * @throws YssException
     */
    public String getTreeViewData2() throws YssException {
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        StringBuffer bufSql = new StringBuffer();
        SeatPortfolioBean seatTree = null;
        String parentCode = ""; //父节点编号
        String nodeCode = ""; //操作组合代码
        String nodeName = ""; //操作组合名称
        String operPortCode = ""; //操作节点代码
        String operPortName = ""; //操作节点名称
        String portCode = ""; //组合代码
        String portName = ""; //组合名称
        String brokerCode = ""; //券商代码
        String brokerName = ""; //券商名称
        String seatCode = ""; //席位代码
        String seatName = ""; //席位名称
        String NodeOrderCode = ""; //序号
        int nodeType = 0; //类型
        String boyBrokerCode = ""; //券商引用节点
        String boySeatCode = ""; //席位引用节点
        String code = ""; //操作组合的菜单代码
        int orderCode = 0; //根节点排序
        int detailPortOrderCode = 0; //操作组合中明晰组合的排序代码
        int brokerNum = 0; //券商级别的排序
        int ceatNum = 0; //席位级别的排序
        int temp = 0; //标识父类的级别
        String sResult = "";
        try {
            bufSql.append(" select * ");
            bufSql.append(" from (SELECT FOperPortCode, ");
            bufSql.append(" FOperPortName, ");
            bufSql.append(" FPortCode, ");
            bufSql.append(" FPortName, ");
            bufSql.append(" ' ' as FBrokerCode, ");
            bufSql.append(" ' ' as FBrokerName, ");
            bufSql.append(" ' ' as fseatcode, ");
            bufSql.append(" ' ' as fseatname, ");
            bufSql.append(" FPortType, ");
            bufSql.append(" FType ");
            bufSql.append(" FROM (SELECT a.FOperPortCode, ");
            bufSql.append(" a.FOperPortName, ");
            bufSql.append(" a.FPortCode, ");
            bufSql.append(" b.FPortName, ");
            bufSql.append(" a.FPortType, ");
            bufSql.append(" 0 AS FType ");
            bufSql.append(" FROM (SELECT * ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Operportfolio"));
            bufSql.append(" WHERE FCheckState = 1) a ");
            bufSql.append(" LEFT JOIN (SELECT a.fportcode, a.fportname ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" a ");
            bufSql.append(" join (select DISTINCT FPortCode ");
            bufSql.append(" from Tb_Sys_Userright ");
            bufSql.append(" where FAssetGroupCode = ").append(dbl.sqlString(pub.getAssetGroupCode()));
            bufSql.append(" and FUserCode = ").append(dbl.sqlString(pub.getUserCode())); ;
            bufSql.append(" and FRightType = " + dbl.sqlString(YssCons.YSS_SYS_RIGHTTYPE_PORT) + ") rp ON a.fportcode = ");
            bufSql.append(" rp.fportcode ");
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FEnabled = 1) b ON a.fportcode = ");
            bufSql.append(" b.fportcode ");
            bufSql.append(" UNION ");
            bufSql.append(" select op.foperportcode AS FOperPortCode, ");
            bufSql.append(" op.foperportname AS FOperPortName, ");
            bufSql.append(" pf.FPortCode, ");
            bufSql.append(" FPortName, ");
            bufSql.append(" 1 AS fporttype, ");
            bufSql.append(" 1 AS FType ");
            bufSql.append(" FROM ").append(pub.yssGetTableName(
                "Tb_Para_Portfolio")).
                append(" pf ");
            bufSql.append(" left join (select DISTINCT FPortCode ");
            bufSql.append(" from Tb_Sys_Userright ");
            bufSql.append(" where FAssetGroupCode = ").append(dbl.sqlString(pub.
                getAssetGroupCode())); ;
            bufSql.append(" and FUserCode = ").append(dbl.sqlString(pub.
                getUserCode())); ;
            bufSql.append(" and FRightType = " +
                          dbl.sqlString(YssCons.YSS_SYS_RIGHTTYPE_PORT) +
                          ") rp ON pf.fportcode = rp.fportcode ");
            bufSql.append(" left join " + pub.yssGetTableName("Tb_Para_Operportfolio") + " op on op.fportcode = pf.fportcode and op.fcheckstate = 1");
            bufSql.append(" WHERE pf.FCheckState = 1 ");
            bufSql.append(" AND pf.FEnabled = 1 ");
            bufSql.append(" AND pf.FPortCode NOT IN ");
            bufSql.append(" (SELECT FPortCode ");
            bufSql.append(" FROM ").append(pub.yssGetTableName(
                "Tb_Para_Operportfolio"));
            bufSql.append(" WHERE FCheckState = 1)) tab ");
            bufSql.append(" UNION ");
            bufSql.append(" select op.foperportcode AS FOperPortCode, ");
            bufSql.append(" op.foperportname AS FOperPortName, ");
            bufSql.append(" pr.FPORTCODE, ");
            bufSql.append(" ' ' as fportname, ");
            bufSql.append(" FSUBCODE as FBrokerCode, ");
            bufSql.append(" FBrokerName, ");
            bufSql.append(" ' ' as fseatcode, ");
            bufSql.append(" ' ' as fseatname, ");
            bufSql.append(" 2 AS fporttype, ");
            bufSql.append(" 2 AS FType ");
            bufSql.append(" from ").append(pub.yssGetTableName( "Tb_Para_Portfolio_RelaShip")).append(" pr left join ");
            //delete by songjie 2010.03.29 国内 : MS00948 QDII4.1赢时胜上海2010年03月27日01_B
//            bufSql.append(pub.yssGetTableName("Tb_Para_Broker"))
            //add by songjie 2010.03.29 国内 : MS00948 QDII4.1赢时胜上海2010年03月27日01_B
            //edit by songjie 2011.03.15 不以最大的启用日期查询数据 将broker1 改为 broker2
            bufSql.append(" (select broker2.Fbrokercode, broker2.FBrokerName, broker2.FStartDate from ")
            //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//            .append(" (select FBrokerCode, max(FSTARTDATE) as FStartDate from ")
//            .append(pub.yssGetTableName("Tb_Para_Broker"))
//            .append(" where fcheckstate = 1 group by FBrokerCode) broker1 ")
            //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
            //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
            .append(" (select FBrokerCode, FBrokerName, FStartDate from ")
            .append(pub.yssGetTableName("Tb_Para_Broker")).append(" where FCheckState = 1) broker2 ) ")
            //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
            //delete by songjie 2011.03.15 不以最大的启用日期查询数据
//            .append(" broker1.fBrokerCode = broker2.fbrokercode and broker1.FStartDate = broker2.FStartDate) ")
            //add by songjie 2010.03.29 国内 : MS00948 QDII4.1赢时胜上海2010年03月27日01_B
            //edit by songjie 2010.03.29 国内 : MS00948 QDII4.1赢时胜上海2010年03月27日01_B
            .append(" pb on FSUBCODE = FBrokerCode left join " + pub.yssGetTableName("Tb_Para_Operportfolio") +
                " op on pr.fportcode = op.fportcode and op.fcheckstate = '1' ");
            bufSql.append(" where FRELATYPE = 'Broker' ");
            bufSql.append(" AND pr.FCHECKSTATE = '1'");
            bufSql.append(" UNION ");
            bufSql.append(" select op.foperportcode AS FOperPortCode, ");
            bufSql.append(" op.foperportname AS FOperPortName, ");
            bufSql.append(" pr.FPORTCODE, ");
            bufSql.append(" ' ' as fportname, ");
            bufSql.append(" pr.FSUBCODE as FBrokerCode, ");
            bufSql.append(" pb.FBrokerName, ");
            //modify by zhangfa MS01679    交易所回购，已设置费用承担方式参数，但是导入过户库仍提示    QDV4赢时胜（上海）2010年8月18日01_B    
            bufSql.append(" ts.fseatnum as fseatcode, ");
            //--------------------------------------------------------------------------------------------------------------------
            bufSql.append(" ts.fseatname, ");
            bufSql.append(" 3 AS fporttype, ");
            bufSql.append(" 3 AS FType ");
            bufSql.append(" from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")).append(" pr left join ");
            //delete by songjie 2010.03.29 国内 : MS00948 QDII4.1赢时胜上海2010年03月27日01_B
//            bufSql.append(pub.yssGetTableName("Tb_Para_Broker"))
            //add by songjie 2010.03.29 国内 : MS00948 QDII4.1赢时胜上海2010年03月27日01_B
            //edit by songjie 2011.03.15 不以最大的启用日期查询数据 将broker1 改为 broker2
            bufSql.append(" (select broker2.Fbrokercode, broker2.FBrokerName, broker2.FStartDate from ")
            //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//            .append(" (select FBrokerCode, max(FSTARTDATE) as FStartDate from ")
//            .append(pub.yssGetTableName("Tb_Para_Broker"))
//            .append(" where fcheckstate = 1 group by FBrokerCode) broker1 ")
            //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
            //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
            .append(" (select FBrokerCode, FBrokerName, FStartDate from ")
            .append(pub.yssGetTableName("Tb_Para_Broker")).append(" where FCheckState = 1) broker2 ) ")
            //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
            //delete by songjie 2011.03.15 不以最大的启用日期查询数据
//            .append(" broker1.fBrokerCode = broker2.fbrokercode and broker1.FStartDate = broker2.FStartDate) ")
            //add by songjie 2010.03.29 国内 : MS00948 QDII4.1赢时胜上海2010年03月27日01_B    
            //edit by songjie 2010.03.29 国内 : MS00948 QDII4.1赢时胜上海2010年03月27日01_B
            .append(" pb on FSUBCODE = pb.FBrokerCode ");
            bufSql.append(" left join " + pub.yssGetTableName("Tb_Para_TradeSeat") + " ts on pb.FBrokerCode =  ts.fbrokercode and ts.fcheckstate = '1'");
            bufSql.append(" left join " + pub.yssGetTableName("Tb_Para_Operportfolio") + " op on pr.fportcode = op.fportcode  and op.fcheckstate = '1' ");
            bufSql.append(" where FRELATYPE = 'Broker'  AND pr.FCHECKSTATE = '1' and ts.fbrokercode is not null) tb2 ");
            bufSql.append(
                " ORDER BY FOperPortCode,FPortCode,FBrokerCode,FBrokerCode,FPortType, FType ");
            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                if (rs.getInt("FType") == 0) { //拼接操作组合
                    if (rs.getInt("fporttype") == 0) {
                        operPortCode = rs.getString("FOperPortCode"); //操作组合代码
                        operPortName = rs.getString("FOperPortName"); //操作组合名称
                        nodeCode = operPortCode; //当前显示的代码
                        nodeName = operPortName; //当前显示得名称
                        NodeOrderCode = YssFun.formatNumber(orderCode++, "000"); //组装成001
                        code = operPortCode; //操作组合下面的明细组合进行引用
                        parentCode = "[root]"; //当前为root
                        detailPortOrderCode = 0;
                        nodeType = rs.getInt("fporttype"); //类型
                        temp = 0; //操作组合
                    } else { //拼接操作组合明细组合
                        operPortCode = rs.getString("FOperPortCode"); //操作组合代码
                        operPortName = rs.getString("FOperPortName"); //操作组合名称
                        portCode = rs.getString("FOperPortCode") + "-" +
                            rs.getString("FPortCode"); //组合代码
                        portName = rs.getString("FPortName"); //组合名称
                        nodeCode = operPortCode + "-" + portCode; //当前显示的代码
                        nodeName = portName; //当前显示得名称
                        NodeOrderCode = YssFun.formatNumber(orderCode - 1, "000") +
                            YssFun.formatNumber(detailPortOrderCode++, "000"); //组装成000001
                        parentCode = code; //引用操作组合的代码，标示为其父节点
                        boyBrokerCode = operPortCode + "-" + portCode; //子节点引用（也就是由券商引用）
                        nodeType = rs.getInt("fporttype"); //类型
                        temp = 1; //操作组合之下的组合
                        brokerNum = 0; //初始化
                    }
                } else if (rs.getInt("FType") == 1) { //拼接组合
                    operPortCode = rs.getString("FOperPortCode"); //操作组合代码
                    operPortName = rs.getString("FOperPortName"); //操作组合名称
                    portCode = rs.getString("FPortCode"); //组合代码
                    portName = rs.getString("FPortName"); //组合名称
                    if (operPortCode == null || operPortCode.length() == 0) {
                        nodeCode = " " + portCode; //子节点引用（也就是由券商引用）
                    } else {
                        nodeCode = " " + operPortCode + "-" + portCode; //子节点引用（也就是由券商引用）
                    }
                    nodeName = portName; //当前显示得名称
                    NodeOrderCode = YssFun.formatNumber(orderCode++, "000"); //组装成002
                    parentCode = "[root]"; //由于这些组合不再操作组合之下所以为root
                    if (operPortCode == null || operPortCode.length() == 0) {
                        boyBrokerCode = " " + portCode; //子节点引用（也就是由券商引用）
                    } else {
                        boyBrokerCode = " " + operPortCode + "-" + portCode; //子节点引用（也就是由券商引用）
                    }
                    nodeType = rs.getInt("fporttype"); //类型
                    temp = 0; //独立的组合
                    brokerNum = 0; //初始化
                } else if (rs.getInt("FType") == 2) { //拼接券商
                    operPortCode = rs.getString("FOperPortCode"); //操作组合代码
                    operPortName = rs.getString("FOperPortName"); //操作组合名称
                    portCode = rs.getString("FPortCode"); //组合代码
                    portName = rs.getString("FPortName"); //组合名称
                    brokerCode = rs.getString("FBrokerCode"); //券商代码
                    brokerName = rs.getString("FBrokerName"); //券商名称
                    if (operPortCode == null || operPortCode.length() == 0) {
                        nodeCode = portCode + "-" + brokerCode; //当前显示的代码
                    } else {
                        nodeCode = operPortCode + "-" + portCode + "-" + brokerCode; //当前显示的代码
                    }

                    nodeName = brokerName; //当前显示得名称
                    nodeType = rs.getInt("fporttype"); //类型
                    if (temp == 0) { //判断父节点属于第几级
                        temp = 1;
                        NodeOrderCode = YssFun.formatNumber(orderCode - 1, "000") +
                            YssFun.formatNumber(brokerNum++, "000"); //组装成000001
                    } else {
                        temp = 2;
                        NodeOrderCode = YssFun.formatNumber(detailPortOrderCode -
                            1,
                            "000000") +
                            YssFun.formatNumber(brokerNum++, "000"); //组装成000000001
                    }
                    ceatNum = 0; //初始化
                    parentCode = boyBrokerCode; //券商理财引用
                    if (operPortCode == null || operPortCode.length() == 0) {
                        boySeatCode = portCode + "-" + brokerCode; //子节点引用（也就是席位引用）
                    } else {
                        boySeatCode = operPortCode + "-" + portCode + "-" + brokerCode; //子节点引用（也就是席位引用）
                    }
                } else if (rs.getInt("FType") == 3) { //拼接席位
                    operPortCode = rs.getString("FOperPortCode"); //操作组合代码
                    operPortName = rs.getString("FOperPortName"); //操作组合名称
                    portCode = rs.getString("FPortCode"); //组合代码
                    portName = rs.getString("FPortName"); //组合名称
                    brokerCode = rs.getString("FBrokerCode"); //券商代码
                    brokerName = rs.getString("FBrokerName"); //券商名称
                    seatCode = rs.getString("FSeatCode"); //席位代码
                    seatName = rs.getString("FSeatName"); //席位名称
                    if (operPortCode == null || operPortCode.length() == 0) {
                        nodeCode = portCode + "-" + brokerCode + "-" + seatCode; //当前显示的代码
                    } else {
                        nodeCode = operPortCode + "-" + portCode + "-" + brokerCode + "-" + seatCode; //当前显示的代码
                    }
                    nodeName = seatName; //当前显示得名称
                    nodeType = rs.getInt("fporttype"); //类型
                    if (temp == 1) { //判断父节点属于第几级
                        NodeOrderCode = YssFun.formatNumber(brokerNum - 1,
                            "000000") +
                            YssFun.formatNumber(ceatNum++, "000"); //组装成000000001
                    } else {
                        NodeOrderCode = YssFun.formatNumber(brokerNum - 1,
                            "000000000") +
                            YssFun.formatNumber(ceatNum++, "000"); //组装成000000000001
                    }

                    parentCode = boySeatCode;
                }
                seatTree = new SeatPortfolioBean(); //树节点对象
                seatTree.setNodeCode(nodeCode); //当前显示代码
                seatTree.setNodeName(nodeName); //当前显示名称
                seatTree.setOperPortCode(operPortCode); //操作组合代码
                seatTree.setOperPortName(operPortName); //操作组合名称
                seatTree.setPortCode(portCode); //组合代码
                seatTree.setPortName(portName); //组合名称
                seatTree.setBrokerCode(brokerCode); //券商代码
                seatTree.setBrokerName(brokerName); //券商名称
                seatTree.setSeatCode(seatCode); //席位代码
                seatTree.setSeatName(seatName); //席位名称
                seatTree.setNodeType(nodeType); //节点类型
                seatTree.setParentCode(parentCode); //父节点代码
                seatTree.setOrderCode(NodeOrderCode); //排序编号
                //清空参数
                operPortCode = "";
                operPortName = "";
                portCode = "";
                portName = "";
                brokerCode = "";
                brokerName = "";
                seatCode = "";
                seatName = "";
                buf.append(seatTree.buildTreeStr());
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

    public String getListViewData1() throws YssException {
        return "";
    }

    /**
     * 已审核
     * @return String
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        String sShowDataAry = "";
        String sAllDataAry = "";
        try {
            strSql =
                "select eb.*,pp.fportname,ap.fassetgroupname from " + pub.yssGetTableName("TB_DAO_ExchangeBond") + " eb left join " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " pp on eb.fportcode  = pp.FPortCode left join TB_SYS_ASSETGROUP ap on eb.fassetgroupcode = ap.fassetgroupcode  where eb.FCheckState = 1 " +
                "and eb.fassetgroupcode = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
                " order by eb.fassetgroupcode,eb.fportcode,eb.fmarket,eb.fcatcode"; ;
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                eb = new ExchangeBondBean();
              //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
//                eb.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
//                eb.setAssetGroupName(rs.getString("FAssetGroupName")); //组合群名称
              //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
                eb.setPortCode(rs.getString("FPortCode")); //组合代码
                eb.setPortName(rs.getString("FPortName")); //组合名称
                eb.setMarket(rs.getString("FMarket")); //市场
                eb.setCatCode(rs.getString("FCatCode")); //品种
                eb.setBondTradeType(rs.getString("FBondTradeType")); //债券交易方式
                eb.setCommisionType(rs.getString("FCommisionType")); //佣金计算方式
                eb.setInteDutyType(rs.getString("FInteDutyType")); //利息税计算方式
                eb.setStartDate(rs.getDate("FStartDate")); //启用日期
                eb.setCreator(rs.getString("FCreator")); //创建人
                eb.setCreateTime(rs.getString("FCreateTime")); //创建时间
                eb.setCheckUser(rs.getString("FCheckUser") == null ? "" :
                                rs.getString("FCheckUser")); //审核人
                eb.setCheckTime(rs.getString("FCheckTime") == null ? "" :
                                rs.getString("FCheckTime")); //审核时间
                sShowDataAry += eb.buildLiseStr(); //内容
                sAllDataAry += eb.buildLiseStr2(); //tag
            }
            if (sShowDataAry.length() > 2) {
                sShowDataAry = sShowDataAry.substring(0,
                    sShowDataAry.length() - 2) +
                    "\r\f";
                sAllDataAry = sAllDataAry.substring(0, sAllDataAry.length() - 2);
                sResult = sShowDataAry + sAllDataAry;
            } else {
                sResult = "\r\f";
            }
        } catch (Exception e) {
            throw new YssException("交易所债券参数查询出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * 未审核
     * @return String
     * @throws YssException
     */
    public String getListViewData3() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        String sShowDataAry = "";
        String sAllDataAry = "";
        try {
            strSql =
                "select eb.*,pp.fportname,ap.fassetgroupname from " + pub.yssGetTableName("TB_DAO_ExchangeBond") + " eb left join " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " pp on eb.fportcode  = pp.FPortCode left join TB_SYS_ASSETGROUP ap on eb.fassetgroupcode = ap.fassetgroupcode  where eb.FCheckState = 0 " +
                "and eb.fassetgroupcode = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
                " order by eb.fassetgroupcode,eb.fportcode,eb.fmarket,eb.fcatcode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                eb = new ExchangeBondBean();
                
                //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
//                eb.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
//                eb.setAssetGroupName(rs.getString("FAssetGroupName")); //组合群名称
                //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
                
                eb.setPortCode(rs.getString("FPortCode")); //组合代码
                eb.setPortName(rs.getString("FPortName")); //组合名称
                eb.setMarket(rs.getString("FMarket")); //市场
                eb.setCatCode(rs.getString("FCatCode")); //品种
                eb.setBondTradeType(rs.getString("FBondTradeType")); //债券交易方式
                eb.setCommisionType(rs.getString("FCommisionType")); //佣金计算方式
                eb.setInteDutyType(rs.getString("FInteDutyType")); //利息税计算方式
                eb.setStartDate(rs.getDate("FStartDate")); //启用日期
                eb.setCreator(rs.getString("FCreator")); //创建人
                eb.setCreateTime(rs.getString("FCreateTime")); //创建时间
                eb.setCheckUser(rs.getString("FCheckUser") == null ? "" :
                                rs.getString("FCheckUser")); //审核人
                eb.setCheckTime(rs.getString("FCheckTime") == null ? "" :
                                rs.getString("FCheckTime")); //审核时间
                sShowDataAry += eb.buildLiseStr(); //内容
                sAllDataAry += eb.buildLiseStr2(); //tag
            }
            if (sShowDataAry.length() > 2) {
                sShowDataAry = sShowDataAry.substring(0,
                    sShowDataAry.length() - 2) +
                    "\r\f";
                sAllDataAry = sAllDataAry.substring(0, sAllDataAry.length() - 2);
                sResult = sShowDataAry + sAllDataAry;
            } else {
                sResult = "\r\f";
            }
        } catch (Exception e) {
            throw new YssException("交易所债券参数查询出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;

    }

    public String getListViewData4() throws YssException {
        return msg;
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
     * 解析数据
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        if (sRowStr.length() == 0) {
            return;
        }
        String[] rmpAry = sRowStr.split("\t");
        tab = rmpAry[0];
        //判断第一个字符是第一个tab页，进行解析不同的值进行装载bean
        if ("0".equals(tab)) { //读取处理方式
            String[] port = rmpAry[1].split(","); //将组合解析成数组
            String[] parameter = rmpAry[6].split(","); //解析参数
            portCode = rmpAry[1]; //删除的组合
            for (int i = 0; i < port.length; i++) { //按照组合循环
                for (int j = 0; j < parameter.length; j++) { //按照参数类型循环
                    rt = new ReadTypeBean(); //构建读取方式对象
                    rt.setPortCode(port[i]); //组合代码
                    rt.setAssetClass(rmpAry[2]); //默认资产分类
                    rt.setWBSBelong(rmpAry[3]); //可分离归入
                    rt.setExchangePreci(Integer.parseInt(rmpAry[4])); //百元利息保留位数
                    rt.setShNum(rmpAry[5]); //百元利息保留位数
                    rt.setParameter("".equals(parameter[j]) ? " " : parameter[j]); //参数
                    rt.setHolidaysCode(rmpAry[7]);//节假日群代码  add by yanghaiming 20100223 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
                    //fanghaoln 20100427 MS01079 QDV4招商基金2010年4月9日01_B 
                    rt.setExchangeFhggain(Integer.parseInt(rmpAry[8])); //回购收益保留位数
                    //---------------------------end ---MS01079---------------------------
                    //add by yanghaiming 20100417 B股业务
                    rt.setHolidaysName(rmpAry[9]);
                    rt.setHolidaysCodeSH(rmpAry[10]);
                    rt.setHolidaysNameSH(rmpAry[11]);
                    rt.setHolidaysCodeSZ(rmpAry[12]);
                    rt.setHolidaysNameSZ(rmpAry[13]);
                    rt.setInvMgrCodeSH(rmpAry[14]);
                    rt.setInvMgrCodeSZ(rmpAry[15]);
                    rt.setCurrencyCodeA(rmpAry[16]);
                    rt.setCurrencyNameA(rmpAry[17]);
                    rt.setCurrencyCodeSHB(rmpAry[18]);
                    rt.setCurrencyNameSHB(rmpAry[19]);
                    rt.setCurrencyCodeSZB(rmpAry[20]);
                    rt.setCurrencyNameSZB(rmpAry[21]);
                    rt.setDelayDateA(Integer.parseInt(rmpAry[22]));
                    rt.setDelayDateB(Integer.parseInt(rmpAry[23]));
                    //add by yanghaiming 20100417 B股业务
                    rslist.add(rt); //增加到读数处理方式参数对象集合
                }
            }
        } else if ("1".equals(tab)) { //交易所债券参数增加、删除使用同一个编号
            String[] code;
            String[] catCode = rmpAry[3].split("\f"); //解析产品，组成交易所债券参数对象
            String[] port = rmpAry[1].split(","); //将组合解析成数组
            portCode = rmpAry[1]; //删除的组合
            for (int k = 0; k < port.length; k++) {
                for (int i = 0; i < catCode.length; i++) { //循环遍历，然后按照,截取对象
                    code = catCode[i].split(","); //按照,分开数据，然后组成对象
                    for (int j = 1; j < code.length; j++) { //第一个是交易所信息
                        eb = new ExchangeBondBean();
                        eb.setPortCode(port[k]); //组合代码
                        eb.setMarket(code[0]); //市场代码
                        eb.setCatCode(code[j]); //产品代码
                        eb.setBondTradeType(rmpAry[4]); //债券交易方式
                        eb.setCommisionType(rmpAry[5]); //佣金计算方式
                        eb.setInteDutyType(rmpAry[6]); //利息税计算方式
                        eb.setStartDate(YssFun.toDate(rmpAry[7])); //启用日期
                        eblist.add(eb); //增加到交易所债券参数对象集合中
                    }
                }
            }
        } else if ("2".equals(tab)) { //交易费用计算方式增加
            String[] port = rmpAry[1].split(","); //将组合解析成数组
            for (int i = 0; i < port.length; i++) { //按照组合循环
                fm = new TradeFeeBean();
                fm.setPortCode(port[i]); //组合代码
                fm.setTradeDetails(rmpAry[2]); //按成交明细计算
                fm.setTradeNum(rmpAry[3]); //按申请编号计算
                fm.setTradeSum(rmpAry[4]); //按成交汇总计算
                fmlist.add(fm); //增加到交易费用计算方式对象集合
            }
        } else if ("0sel".equals(tab)) { //读取处理方式查询
            portCode = rmpAry[1];
            rt = new ReadTypeBean(); //构建读取方式对象
            rt.setPortCode(portCode); //组合代码
        } else if ("1sel".equals(tab)) { //交易所债券参数查询
            String[] tmpAry = sRowStr.split("\f");
            rmpAry = tmpAry[0].split("\t"); //原始数据
            eb = new ExchangeBondBean();
            eb.setPortCode(rmpAry[1]); //组合代码
            eb.setMarket(rmpAry[2]); //市场代码
            eb.setCatCode(rmpAry[3]); //产品代码
            eb.setBondTradeType(rmpAry[4]); //债券交易方式
            eb.setCommisionType(rmpAry[5]); //佣金计算方式
            eb.setInteDutyType(rmpAry[6]); //利息税计算方式
            eb.setStartDate(YssFun.toDate(rmpAry[7])); //启用日期
            eblist.add(eb); //增加到交易所债券参数对象集合中
            rmpAry = tmpAry[1].split("\t"); //更新数据
            eb = new ExchangeBondBean();
            eb.setPortCode(rmpAry[0]); //组合代码
            eb.setMarket(rmpAry[2].split(",")[0]); //市场代码
            eb.setCatCode(rmpAry[2].split(",")[1]); //产品代码
            eb.setBondTradeType(rmpAry[3]); //债券交易方式
            eb.setCommisionType(rmpAry[4]); //佣金计算方式
            eb.setInteDutyType(rmpAry[5]); //利息税计算方式
            eb.setStartDate(YssFun.toDate(rmpAry[6])); //启用日期
            eblist.add(eb); //增加到交易所债券参数对象集合中

        } else if ("1edit".equals(tab)) { //交易所债券参数修改/交易所债券参数检查数据重复 共用一个同一个处理编号
            String[] tmpAry = sRowStr.split("\f");
            rmpAry = tmpAry[0].split("\t"); //原始数据
            eb = new ExchangeBondBean();
            eb.setPortCode(rmpAry[1]); //组合代码
            eb.setMarket(rmpAry[2]); //市场代码
            eb.setCatCode(rmpAry[3]); //产品代码
            eb.setBondTradeType(rmpAry[4]); //债券交易方式
            eb.setCommisionType(rmpAry[5]); //佣金计算方式
            eb.setInteDutyType(rmpAry[6]); //利息税计算方式
            eb.setStartDate(YssFun.toDate(rmpAry[7])); //启用日期
            eblist.add(eb); //增加到交易所债券参数对象集合中
            rmpAry = tmpAry[1].split("\t"); //更新数据
            eb = new ExchangeBondBean();
            eb.setPortCode(rmpAry[0]); //组合代码
            eb.setMarket(rmpAry[2].split(",")[0]); //市场代码
            eb.setCatCode(rmpAry[2].split(",")[1]); //产品代码
            eb.setBondTradeType(rmpAry[3]); //债券交易方式
            eb.setCommisionType(rmpAry[4]); //佣金计算方式
            eb.setInteDutyType(rmpAry[5]); //利息税计算方式
            eb.setStartDate(YssFun.toDate(rmpAry[6])); //启用日期
            eblist.add(eb); //增加到交易所债券参数对象集合中
        } else if ("2sel".equals(tab)) { //交易费用计算方式查询
            portCode = rmpAry[1];
            fm = new TradeFeeBean(); //构建易费用计算方式对象
            fm.setPortCode(portCode); //组合代码
        } else if ("3".equals(tab)) { //费用承担方向增加
            String[] tmpAry = sRowStr.split("\f");
            String[] str;
            for (int i = 1; i < tmpAry.length; i++) {
                str = tmpAry[i].split("\t");
                bf = new FeeWayBean();
                bf.setPortCode("".equals(str[1]) ? " " : str[1]); //组合代码
                bf.setBrokerCode("".equals(str[2]) ? " " : str[2]); //券商代码
                bf.setSeatCode("".equals(str[3]) ? " " : str[3]); //席位代码
                bf.setProductBear("".equals(str[4]) ? " " : str[4]); //产品承担
                bf.setBrokerBear("".equals(str[5]) ? " " : str[5]); //券商承担
                bflist.add(bf);
            }
        } else if ("3sel".equals(tab)) { //费用承担方向查询
            bf = new FeeWayBean();
            bf.setPortCode("".equals(rmpAry[2]) ? " " : rmpAry[2]); //组合代码
            bf.setBrokerCode("".equals(rmpAry[3]) ? " " : rmpAry[3]); //券商代码
            bf.setSeatCode("".equals(rmpAry[4]) ? " " : rmpAry[4]); //席位代码
            bf.setProductBear("".equals(rmpAry[5]) ? " " : rmpAry[5]); //产品承担
            bf.setBrokerBear("".equals(rmpAry[6]) ? " " : rmpAry[6]); //券商承担
        }
    }

    /**
     * 组装数据
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        if ("0sel".equals(tab)) { //读数方式查询数据组织
            String paramter = "";
            for (int i = 0; i < rslist.size(); i++) {
                rt = (ReadTypeBean) rslist.get(i); //获得读数方式对象
                //组装参数对象
                paramter = paramter + rt.getParameter() + ",";
            }
            if (paramter.length() != 0) {
                rt.setParameter(paramter.substring(0, paramter.length() - 1));
                return rt.buildRowStr(); //返回
            }
        } else if ("2sel".equals(tab)) { //交易费用计算方式
            return fm.buildRowStr();
        } else if ("3sel".equals(tab)) { //费用承担方向
            return bf.buildRowStr();
        }
        return "";
    }

    /**
     * 获取一条数据
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String getOperValue(String sType) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String errmsg = "";
        try {
            if ("0sel".equals(tab)) { //读取处理方式
                errmsg = "读取处理方式查询出错！";
              //edit by yanghaiming 20100417 B股业务
                strSql = " select a.*,b.fholidaysname as Fholidaysname,c.fholidaysname as fholidaysnamesh,d.fholidaysname as fholidaysnamesz," +
                	" e.fcuryname as fcurrencynamea, f.fcuryname as fcurrencynameshb, g.fcuryname as fcurrencynameszb from " + 
                	pub.yssGetTableName("TB_DAO_ReadType") + 
                	" a left join (select * from Tb_Base_Holidays)b on a.fholidayscode = b.fholidayscode" +
                	" left join (select * from Tb_Base_Holidays)c on a.fholidayscodesh = c.fholidayscode" +
                	" left join (select * from Tb_Base_Holidays)d on a.fholidayscodesz = d.fholidayscode" +
                	" left join (select * from " + pub.yssGetTableName("Tb_Para_Currency") + ")e on a.fcurrencycodea = e.fcurycode" +
                	" left join (select * from " + pub.yssGetTableName("Tb_Para_Currency") + ")f on a.fcurrencycodeshb = f.fcurycode" +
                	" left join (select * from " + pub.yssGetTableName("Tb_Para_Currency") + ")g on a.fcurrencycodeszb = g.fcurycode" +
                	" where FPortCode = " +
                    dbl.sqlString(rt.getPortCode()) + " and FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode());
              //edit by yanghaiming 20100417 B股业务
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    rt = new ReadTypeBean();
                    rt.setAssetClass(rs.getString("FAssetClass")); //默认资产分类
                    rt.setWBSBelong(rs.getString("FWBSBelong")); //可分离债归入
                    rt.setExchangePreci(rs.getInt("FExchangePreci")); //交易所每百元债券利息保留位数
                    rt.setShNum(rs.getString("FSHNum") == null ? "" :
                                rs.getString("FSHNum")); //上海对账库信箱号
                    rt.setParameter(rs.getString("FParameter")); //参数
                    rt.setHolidaysCode(rs.getString("FHOLIDAYSCODE")); //节假日群代码   add by yanghaiming 20100223 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
                    //fanghaoln 20100427 MS01079 QDV4招商基金2010年4月9日01_B 
                    rt.setExchangeFhggain(rs.getInt("FExchangeFhggain")); //回购收益保留位数
                    //---------------------------end ---MS01079---------------------------
                    //add by yanghaiming 20100417 B股业务
                    rt.setHolidaysName(rs.getString("Fholidaysname"));
                    rt.setHolidaysCodeSH(rs.getString("FHOLIDAYSCODESH"));
                    rt.setHolidaysNameSH(rs.getString("fholidaysnamesh"));
                    rt.setHolidaysCodeSZ(rs.getString("FHOLIDAYSCODESZ"));
                    rt.setHolidaysNameSZ(rs.getString("fholidaysnamesz"));
                    rt.setInvMgrCodeSH(rs.getString("FINVMGRCODESH"));
                    rt.setInvMgrCodeSZ(rs.getString("FINVMGRCODESZ"));
                    rt.setCurrencyCodeA(rs.getString("FCURRENCYCODEA"));
                    rt.setCurrencyNameA(rs.getString("fcurrencynamea"));
                    rt.setCurrencyCodeSHB(rs.getString("FCURRENCYCODESHB"));
                    rt.setCurrencyNameSHB(rs.getString("fcurrencynameshb"));
                    rt.setCurrencyCodeSZB(rs.getString("FCURRENCYCODESZB"));
                    rt.setCurrencyNameSZB(rs.getString("fcurrencynameszb"));
                    rt.setDelayDateA(rs.getInt("FDELAYDATEA"));
                    rt.setDelayDateB(rs.getInt("FDELAYDATEB"));
                    //add by yanghaiming 20100417 B股业务
                    rslist.add(rt);
                }
                dbl.closeResultSetFinal(rs);
            } else if ("1edit".equals(tab)) { //交易所债券参数修改/交易所债券参数检查数据重复 共用一个同一个处理编号
                errmsg = "交易所债券参数数据验证出错！";
                //根据信息查询数据是否存在，不存在返回false，存在返回true
                boolean type = false;
                eb = (ExchangeBondBean) eblist.get(0); //原始数据
                ExchangeBondBean ebt = (ExchangeBondBean) eblist.get(1); //新数据
                strSql =
                    "select * from " + pub.yssGetTableName("TB_DAO_ExchangeBond") + " where FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode()) + " and FPortCode = " +
                    dbl.sqlString(ebt.getPortCode()) + " and FMarket = " +
                    dbl.sqlString(ebt.getMarket()) + " and FCatCode = " +
                    dbl.sqlString(ebt.getCatCode());
                rs = dbl.openResultSet(strSql);
                while (rs.next()) { //代表数据存在
                    if (!eb.getMarket().equals(rs.getString("FMarket")) ||
                        !eb.getCatCode().equals(rs.getString("FCatCode"))) { //如果不是本条数据
                        type = true;
                    }
                }
                if (type) {
                    return "true";
                } else {
                    return "false";
                }
            } else if ("2sel".equals(tab)) { //交易费用计算方式
                errmsg = "读取交易费用计算方式查询出错！";
                strSql = "select * from " + pub.yssGetTableName("TB_DAO_TradeFee") + " where FPortCode = " +
                    dbl.sqlString(fm.getPortCode()) + " and FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode());
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    fm = new TradeFeeBean();
                    fm.setPortCode(rs.getString("FPortCode")); //组合代码
                    fm.setTradeDetails(rs.getString("FTradeDetails") == null ? "" : rs.getString("FTradeDetails")); ; //按成交明细计算
                    fm.setTradeNum(rs.getString("FTradeNum") == null ? "" : rs.getString("FTradeNum")); //按申请编号计算
                    fm.setTradeSum(rs.getString("FTradeSum") == null ? "" : rs.getString("FTradeSum")); //按成交汇总计算
                }
            } else if ("3sel".equals(tab)) { //费用承担方向
                errmsg = "费用承担方向查询出错！";
                strSql = "select * from " + pub.yssGetTableName("TB_DAO_FeeWay") + " where FPortCode = " +
                    dbl.sqlString(bf.getPortCode()) + " and FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode()) + " and FBrokerCode = " +
                    dbl.sqlString(bf.getBrokerCode()) + " and FSeatCode = " +
                    dbl.sqlString(bf.getSeatCode());
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bf = new FeeWayBean();
                    bf.setProductBear(rs.getString("FProductBear") == null ?
                                      "" : rs.getString("FProductBear")); //产品承担
                    bf.setBrokerBear(rs.getString("FBrokerBear") == null ?
                                     "" : rs.getString("FBrokerBear")); //券商承担
                }
            }
        } catch (Exception e) {
            throw new YssException(errmsg, e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return buildRowStr();
    }

    /**
     * 方法主要是用来遍历界面来多个字符的然后放回一个可以拼接到 in里的形式 例如：传入 1,2,3 返回 '1','2','3'
     * @param frmValue String
     * @return String
     */
    public String strWhere(String frmValue) {
        String[] RegCode = frmValue.split(",");
        String Rc = "'";
        if (RegCode.length == 1) {
            if (!RegCode[0].trim().equalsIgnoreCase("")) {
                Rc = "'" + frmValue + "'";
            } else {
                Rc = "";
            }
        } else {
            for (int i = 0; i < RegCode.length; i++) {
                if (i == RegCode.length - 1) {
                    Rc += RegCode[i] + "'";
                } else {
                    Rc += RegCode[i] + "','";
                }
            }
        }
        return Rc;
    }

    /**
     * 返回Map对象，里面封装TradeFeeBean（交易费用计算方式）对象，key为主键，value为对象.主键为空格隔开
     * @return Map
     */
    public Map getTradeFeeBean() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String errmsg = "";
        Map map = new HashMap();
        String key = "";
        String value = "";
        try {
            errmsg = "读取交易费用计算方式出错！";
            //利用for循环从数据库依次遍历
            for (int i = 1; i <= 12; i++) {
                if (i <= 9) {
                    key = "0" + i;
                } else {
                    key = i + "";
                }
                //按成交明细计算
                strSql =
                    " select * from " + pub.yssGetTableName("TB_DAO_TradeFee") + " where FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +
                    " and FTradeDetails like '%" + key + "%'";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    value = value + rs.getString("FPortCode") + ","; //组合代码
                }
                dbl.closeResultSetFinal(rs);
                if (value.length() > 1) { //说明在产品部分存在当前收费类别
                    value = value.substring(0, value.length() - 1);
                    map.put(key + "cjmx", value); //成交明细
                }
                value = "";
                //按申请编号计算
                strSql =
                    " select * from " + pub.yssGetTableName("TB_DAO_TradeFee") + " where FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +
                    " and FTradeNum like '%" + key + "%'";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    value = value + rs.getString("FPortCode") + ","; //组合代码
                }
                dbl.closeResultSetFinal(rs);
                if (value.length() > 1) {
                    value = value.substring(0, value.length() - 1);
                    map.put(key + "sqbh", value); //申请编号
                }
                value = "";
                //按成交汇总计算
                strSql =
                    " select * from " + pub.yssGetTableName("TB_DAO_TradeFee") + " where FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +
                    " and FTradeSum like '%" + key + "%'";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    value = value + rs.getString("FPortCode") + ","; //组合代码
                }
                dbl.closeResultSetFinal(rs);
                if (value.length() > 1) {
                    value = value.substring(0, value.length() - 1);
                    map.put(key + "cjhz", value); //成交汇总
                }
                value = "";
            }
        } catch (Exception e) {
            throw new YssException(errmsg, e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return map;
    }

    /**
     * 返回Map对象，里面封装FeeWayBean（费用承担方向）对象，key为主键，value为对象.主键为空格隔开
     * @return Map
     */
    public Map getFeeWayBean() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String errmsg = "";
        Map map = new HashMap();
        FeeWayBean fw = null;
        String key = "";
        try {
            errmsg = "读取费用承担方向出错！";
            strSql =
                " select * from " + pub.yssGetTableName("TB_DAO_FeeWay") + " where FAssetGroupCode = " +
                dbl.sqlString(pub.getAssetGroupCode());
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                fw = new FeeWayBean();
                fw.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                fw.setPortCode(rs.getString("FPortCode")); //组合代码
                fw.setBrokerCode(rs.getString("FBrokerCode")); //券商代码
                fw.setSeatCode(rs.getString("FSeatCode")); //席位代码
                fw.setProductBear(rs.getString("FProductBear")); //产品承担
                fw.setBrokerBear(rs.getString("FBrokerBear")); //券商承担
                key = rs.getString("FAssetGroupCode") + " " +
                    rs.getString("FPortCode") + " " + rs.getString("FBrokerCode") +
                    " " + rs.getString("FSeatCode");
                map.put(key, fw);
            }
        } catch (Exception e) {
            throw new YssException(errmsg, e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return map;
    }
    
    /**
     * 获取组合、券商和席位所对应的由券商承担的费用品种类型
     * B股业务	panjunfang add 20100518
     * @param assetGroupCode
     * @param portCode
     * @param brokerCode
     * @param seatCode
     * @return
     * @throws YssException
     */
    public ArrayList getBrokerBearCost(String assetGroupCode,String portCode,String brokerCode,String seatCode) throws YssException{
    	ArrayList aList = null;
    	try{
    		HashMap hmFeeWay = (HashMap)getFeeWayBean();
            FeeWayBean feeWay = (FeeWayBean) hmFeeWay.get(assetGroupCode + " " + portCode +
                    " " + brokerCode + " " + seatCode); //获取交易接口参数设置的费用承担方向分页的相关数据
            if (feeWay == null) {
                throw new YssException("请在交易接口参数设置界面设置已选组合的费用承担参数！");
            }
            String brokerBear = feeWay.getBrokerBear(); //获取由券商承担的费用数据
            String[] brokerBears = brokerBear.split(","); //用逗号拆分数据
            aList = new ArrayList(); //新建ArrayList
            for (int i = 0; i < brokerBears.length; i++) {
            	aList.add(brokerBears[i]); //将费用代码添加到alBears中
            }
    	}catch(Exception e){
    		throw new YssException("获取券商所承担费用品种出错！",e);
    	}
    	return aList;
    }

    /**
     * 返回Map对象，里面封装ExchangeBondBean（交易所债券参数设置）对象，key为主键，value为对象.主键为空格隔开
     * @return Map
     */
    public Map getExchangeBondBean() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String errmsg = "";
        Map map = new HashMap();
        ExchangeBondBean ex = null;
        String key = "";
        try {
            errmsg = "读取交易所债券参数设置出错！";
            strSql =
                " select * from " + pub.yssGetTableName("TB_DAO_ExchangeBond") + " where FCheckState = 1 and FAssetGroupCode = " +
                dbl.sqlString(pub.getAssetGroupCode()) + " and FStartDate <= " +
                dbl.sqlDate(new Date());
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                ex = new ExchangeBondBean();
                //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
//                ex.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                ex.setPortCode(rs.getString("FPortCode")); //组合代码
                ex.setMarket(rs.getString("FMarket")); //市场
                ex.setCatCode(rs.getString("FCatCode")); //品种
                ex.setBondTradeType(rs.getString("FBondTradeType")); //债券交易方式
                ex.setCommisionType(rs.getString("FCommisionType")); //佣金计算方式
                ex.setInteDutyType(rs.getString("FInteDutyType")); //利息税计算方式
                ex.setStartDate(rs.getDate("FStartDate")); //启用日期
                key = rs.getString("FAssetGroupCode") + " " +
                    rs.getString("FPortCode") + " " + rs.getString("FMarket") +
                    " " + rs.getString("FCatCode");
                map.put(key, ex);
            }
        } catch (Exception e) {
            throw new YssException(errmsg, e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return map;
    }

    /**
     * 返回Map对象，里面封装ReadTypeBean（读数处理方式）对象，key为主键，value为对象.主键为空格隔开
     * @return Map
     */
    public Map getReadTypeBean() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ResultSet subRs=null;
        String errmsg = "";
        Map map = new HashMap();
        ReadTypeBean rt = null;
        String key = "";
        String groupCode = "";
        String portCode = "";
        String assetClass = "";
        String wBSBelong = "";
        int exchangePreci = 0;
        String shNum = "";
        //add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
        String holidaysCode = "";//节假日群代码
        //add by yanghaiming 20100417 B股业务
        String holidaysCodeSH = "";
        String holidaysCodeSZ = "";
        String invMgrCodeSH = "";
        String invMgrCodeSZ = "";
        String currencyCodeA = "";
        String currencyCodeSHB = "";
        String currencyCodeSHZ = "";
        int delayDateA = 0;
        int delayDateB = 0;
        List list = new ArrayList();
        try {
            errmsg = "读取读数处理方式出错！";
            strSql =
                " select * from " + pub.yssGetTableName("TB_DAO_ReadType") + " where FAssetGroupCode = " +
                //modified by liubo.Story #1916
                //在进行跨组合群处理时，pub.getPrefixTB()和pub.getAssetGroupCode的值可能不一致
                //===============================
//                dbl.sqlString(pub.getAssetGroupCode()) +
                dbl.sqlString(pub.getPrefixTB()) +
                //==============end=================
                " order by FAssetGroupCode,FPortCode,FParameter";
            rs = dbl.openResultSet(strSql);
            //begin MS01530 没有设置接口参数时，导入国内接口数据会报错  zhouxiang -------------------
            //add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
            int count = 0;
            //--- delete by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
//            subRs=dbl.openResultSet(strSql);
//            if(!subRs.next())
//            {
//            throw new YssException("数据接口参数设置没有相关的信息，请检查后重试！");}
            //--- delete by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            //end   MS01530 没有设置接口参数时，导入国内接口数据会报错  zhouxiang -------------------
            while (rs.next()) {
            	//add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
            	count++;
                if (groupCode.equals(rs.getString("FAssetGroupCode")) &&
                    portCode.equals(rs.getString("FPortCode"))) { //如果当前存储的组合与上一组合一致
                    list.add(rs.getString("FParameter")); //参数
                } else {
                    if (groupCode.length() > 0 && portCode.length() > 0) {
                        //插入值
                        rt = new ReadTypeBean();
                        rt.setAssetGroupCode(groupCode);
                        rt.setPortCode(portCode);
                        rt.setAssetClass(assetClass);
                        rt.setWBSBelong(wBSBelong);
                        rt.setExchangePreci(exchangePreci);
                        rt.setShNum(shNum);
                        rt.setParameters(list);
                        //add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
                        rt.setHolidaysCode(holidaysCode);
                        //add by yanghaiming 20100417 B股业务
                        rt.setHolidaysCodeSH(holidaysCodeSH);
                        rt.setHolidaysCodeSZ(holidaysCodeSZ);
                        rt.setInvMgrCodeSH(invMgrCodeSH);
                        rt.setInvMgrCodeSZ(invMgrCodeSZ);
                        rt.setCurrencyCodeA(currencyCodeA);
                        rt.setCurrencyCodeSHB(currencyCodeSHB);
                        rt.setCurrencyCodeSZB(currencyCodeSHZ);
                        rt.setDelayDateA(delayDateA);
                        rt.setDelayDateB(delayDateB);
                        key = groupCode + " " + portCode;
                        map.put(key, rt);
                        list = new ArrayList();
                    }
                    //获得新值
                    groupCode = rs.getString("FAssetGroupCode"); //组合群代码
                    portCode = rs.getString("FPortCode"); //组合代码
                    assetClass = rs.getString("FAssetClass"); //默认资产分类
                    wBSBelong = rs.getString("FWBSBelong"); //可分离债归入
                    exchangePreci = rs.getInt("FExchangePreci"); //债券利息保留位数交易所每百元
                    shNum = rs.getString("FSHNum"); //上海对账库信箱号
                    //add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
                    holidaysCode = rs.getString("FHolidaysCode");
                    //add by yanghaiming 20100417 B股业务
                    holidaysCodeSH = rs.getString("FHOLIDAYSCODESH");
                    holidaysCodeSZ = rs.getString("FHOLIDAYSCODESZ");
                    invMgrCodeSH = rs.getString("FINVMGRCODESH");
                    invMgrCodeSZ = rs.getString("FINVMGRCODESZ");
                    currencyCodeA = rs.getString("FCURRENCYCODEA");
                    currencyCodeSHB = rs.getString("FCURRENCYCODESHB");
                    currencyCodeSHZ = rs.getString("FCURRENCYCODESZB");
                    delayDateA = rs.getInt("FDELAYDATEA");
                    delayDateB = rs.getInt("FDELAYDATEB");
                    //add by yanghaiming 20100417 B股业务
                    list.add(rs.getString("FParameter")); //参数
                }
            }
            
            //--- add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(count == 0){
            	throw new YssException("数据接口参数设置没有相关的信息，请检查后重试！");
            }
            //--- add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            
            if (groupCode.length() != 0 && portCode.length() != 0) { //因为组合群代码和组合代码不能为空，所以如果这里长度>0则代表有一条数据没有增加到map中（最后一条数据）
                //插入值
                rt = new ReadTypeBean();
                rt.setAssetGroupCode(groupCode);
                rt.setPortCode(portCode);
                rt.setAssetClass(assetClass);
                rt.setWBSBelong(wBSBelong);
                rt.setExchangePreci(exchangePreci);
                rt.setShNum(shNum);
                rt.setParameters(list);
                //add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
                rt.setHolidaysCode(holidaysCode);
                //add by yanghaiming 20100417 B股业务
                rt.setHolidaysCodeSH(holidaysCodeSH);
                rt.setHolidaysCodeSZ(holidaysCodeSZ);
                rt.setInvMgrCodeSH(invMgrCodeSH);
                rt.setInvMgrCodeSZ(invMgrCodeSZ);
                rt.setCurrencyCodeA(currencyCodeA);
                rt.setCurrencyCodeSHB(currencyCodeSHB);
                rt.setCurrencyCodeSZB(currencyCodeSHZ);
                rt.setDelayDateA(delayDateA);
                rt.setDelayDateB(delayDateB);
                
                key = groupCode + " " + portCode;
                map.put(key, rt);
                //delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                //list = new ArrayList();
            }
        } catch (Exception e) {
            throw new YssException(errmsg, e);
        } finally {
        	//edit by songjie 2011.04.07 关闭resultSet
            dbl.closeResultSetFinal(rs,subRs);
        }
        return map;
    }

}
