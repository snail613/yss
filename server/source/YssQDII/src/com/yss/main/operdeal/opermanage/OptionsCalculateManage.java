package com.yss.main.operdeal.opermanage;

import java.sql.*;
import java.util.Date;

import com.yss.main.operdata.futures.*;
import com.yss.util.*;

/**
 * <p>Title:by xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持 </p>
 *
 * <p>Description: 此类主要方便做估值增值，先把数据插入到期权估值核算表 TB_xxx_data_OptionsValCal</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OptionsCalculateManage
    extends BaseOperManage {
    public OptionsCalculateManage() {
    }

    /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate; //调拨日期
        this.sPortCode = portCode; //组合
    }

    /**
     * 做业务处理：把数据插入到期权估值核算表
     * @throws YssException
     */
    public void doOpertion() throws YssException {
        InsertOptionsValCal();
    }

    /**
     * InsertOptionsValCal 把数据插入到期权估值核算表
     */
    private void InsertOptionsValCal() throws YssException {
        String sql = "";
        ResultSet rs = null;
        OptionsValCalAdmin valCalAdmin = null;//与数据库交互的操作类
        try {
            valCalAdmin = new OptionsValCalAdmin();
            valCalAdmin.setYssPub(pub);
            sql = getCashAccCode();//获取现金账户代码的方法
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
                //根据证券代码，组合代码删除数据的方法
                valCalAdmin.deleteData(rs.getString("FSecurityCode"),
				rs.getString("FPortCode"));
                valCalAdmin.saveMutliSetting(rs);//保存数据的方法
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getCashAccCode 从期权交易关联表中获取变动保证金账户的SQL 语句
     *
     * @return String
     */
    private String getCashAccCode() throws YssException {
        StringBuffer buff = null;
        try {
            buff = new StringBuffer();
            buff.append("select * from ").append(pub.yssGetTableName("TB_Data_OptionsTrade"));
            buff.append(" where FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode));
            //添加审核状态作为判断条件 邵宏伟 2009-7-3 MS00561:QDV4招商证券2009年07月06日01_B
            buff.append(") and FCheckState = 1 and FBargainDate=").append(dbl.sqlDate(this.dDate));

        } catch (Exception e) {
            throw new YssException("从期权交易关联表中获取变动保证金账户出错\r\t", e);
        }
        return buff.toString();
    }

}
