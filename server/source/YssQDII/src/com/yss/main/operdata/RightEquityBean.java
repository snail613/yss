package com.yss.main.operdata;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.rightequity.*;
import com.yss.util.*;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech </p>
 *
 * <p>
 * Modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
 * 删除了废弃不用的代码,对游标进行关闭
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RightEquityBean
    extends BaseBean implements IClientOperRequest {
    String strOperStartDate = ""; //业务起始日期
    String strOperEndDate = ""; //业务截止日期
    String strPortCode = ""; //组合代码"\t"间隔
    String strOperType = ""; //业务类别
    String strWinCode = ""; //窗体代码
    private String strDealInfo = ""; //"yes"有业务，"no"无业务 //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
    public RightEquityBean() {
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
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
        String[] sReqAry = null;
        if (sRowStr.length() > 0) {
            sReqAry = sRowStr.split("\f");
            strOperStartDate = sReqAry[0];
            strOperEndDate = sReqAry[1];
            strPortCode = sReqAry[2];
            strOperType = sReqAry[3];
        }
    }

    /**
     * checkRequest
     *
     * @param sType String
     * @return String
     */
    public String checkRequest(String sType) {
        return "";
    }

    /**
     * doOperation
     *
     * @param sType String
     * @return String
     */
    public String doOperation(String sType) throws YssException {
        BaseRightEquity right = null;
        String result = "";
        String[] types = null;
        try {
            if (this.strOperType.length() > 0) {
                types = strOperType.split("\t");
                for (int i = 0; i < types.length; i++) { //循环执行各种权益处理 sj
                    right = (BaseRightEquity) pub.getOperDealCtx().getBean(
                        types[i].trim());
                    doRight(right);
                }
            }
            return result;
        } catch (Exception e) {
            this.strDealInfo = "no" + "\t" ;
           // throw new YssException(strError + "\r\n" + e.getMessage(), e);
        }
        return result;
    }

    private BaseRightEquity initRightBean(BaseRightEquity right) {
        //right.strPortCode = this.strPortCode;
        right.strOperType = this.strOperType;
        return right;
    }

    private ArrayList diffDate() throws YssException {
        ArrayList arr = new ArrayList();
        int days = 0;
        java.util.Date baseDate = null;
        try {
            if (this.strOperStartDate.length() > 0 &&
                this.strOperEndDate.length() > 0) {
                days = YssFun.dateDiff(YssFun.toDate(this.strOperStartDate),
                                       YssFun.toDate(this.strOperEndDate));
                baseDate = YssFun.toDate(this.strOperStartDate);
            }
            for (int i = 0; i <= days; i++) {
                arr.add(YssFun.addDay(baseDate, i));
            }
            return arr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }

    public String getListViewData1() throws YssException {
        String reDealInfo = this.strDealInfo;
        this.strDealInfo = ""; //因为是类变量，所以在前台获取后需要还原。sj
        return reDealInfo;
    }

    public String getListViewTypeData() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        try {
            sqlStr =
                "select * from TB_FUN_SPINGINVOKE where FFormCode = 'OtherOperDeal'" +
                " order by FSICode";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FSICode")).append("\t");
                bufShow.append(rs.getString("FSIName")).append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            return sShowDataStr;
        } catch (Exception e) {
            throw new YssException("获取Spring调用信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void doRight(BaseRightEquity right) throws YssException {
        ArrayList arr = null;
        ArrayList daydiff = null;
        //---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        Connection conn = null;
        boolean bTrans = true;
        //---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
        	//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
			conn = dbl.loadConnection();// 获取连接
			conn.setAutoCommit(false); // 设置为手动打开连接
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_SubTrade")); // 给操作表加锁
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Trade")); // 给操作表加锁
			//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
			right = initRightBean(right);
			daydiff = this.diffDate();
			if (daydiff != null && daydiff.size() >= 0) {
				for (int i = 0; i < daydiff.size(); i++) {
					right.setYssPub(pub);
					arr = right.getDayRightEquitys((java.util.Date) daydiff.get(i), this.strPortCode);
					right.saveRightEquitys(arr, (java.util.Date) daydiff.get(i), this.strPortCode);
					strDealInfo += right.strDealInfo + "\t";
				}
			}

			//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        } catch (Exception e) {
            strDealInfo += "false" + "\t";
            throw new YssException("权益处理出错", e);
        }
        //---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
    }
}
