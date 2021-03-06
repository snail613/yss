package com.yss.main.operdeal.linkInfo;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.taoperation.TACashSettleBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;

public class TaCashSettleLinkDeal
    extends BaseLinkInfoDeal {
    private TACashSettleBean taCashSettle = null;
    private String SellNetCode = "";
    private String PortClsCode = "";
    private String PortCode = "";
    private String SellTypeCode = "";
    private String CuryCode = "";
    private java.util.Date dStartDate = null;
    public TaCashSettleLinkDeal() {
    }

    public void setLinkAttr(BaseDataSettingBean LinkInfoBean) throws
        YssException {
        taCashSettle = (TACashSettleBean) LinkInfoBean;
        if (taCashSettle != null) {
            setLinkParaAttr(taCashSettle.getSellNetCode(),
                            taCashSettle.getSellTypeCode(),
                            taCashSettle.getPortClsCode(),
                            taCashSettle.getPortCode(),
                            taCashSettle.getCuryCode(),
                            taCashSettle.getStartDate());
        }
    }

    public void setLinkParaAttr(String sSellNetCode, String sSellTypeCode,
                                String sPortClsCode, String sPortCode,
                                String sCuryCode, java.util.Date dStartDate) throws
        YssException {
        try {
            this.SellNetCode = sSellNetCode;
            this.PortClsCode = sPortClsCode;
            this.PortCode = sPortCode;
            this.SellTypeCode = sSellTypeCode;
            this.CuryCode = sCuryCode;
            this.dStartDate = dStartDate;
        } catch (Exception e) {
            throw new YssException("字段设置出错！");
        }
    }

    public String buildLinkCondition() {
        StringBuffer buf = new StringBuffer();
        buf.append("FSellNetCode = " +
                   dbl.sqlString(this.SellNetCode)).append("\t");
        buf.append("FCuryCode = " + dbl.sqlString(this.CuryCode)).append("\t");
        buf.append("FSellTypeCode = " +
                   dbl.sqlString(this.SellTypeCode)).append("\t");
        buf.append("FPortClsCode = " +
                   dbl.sqlString(this.PortClsCode)).append("\t");
        buf.append("FPortCode = " +
                   dbl.sqlString(this.PortCode)).append("\t");
        return buf.toString();
    }

    public String createTempData() throws YssException {
        String strSql = "";
        String sTmpTableName = "";
        try {
            strSql = "select FSellNetCode,FPortClsCode,FPortCode,FSellTypeCode,FCuryCode,max(FStartDate) as FStartDate " +
                " from " + pub.yssGetTableName("Tb_TA_CashSettle") +
                " where (FSellNetCode = " +
                dbl.sqlString(this.SellNetCode) + " or FSellNetCode = ' ') " +
                " and (FPortClsCode = " +
                dbl.sqlString(this.PortClsCode) + " or FPortClsCode = ' ') " +
                " and (FPortCode = " +
                dbl.sqlString(this.PortCode) + " or FPortCode = ' ') " +
                " and (FSellTypeCode = " +
                dbl.sqlString(this.SellTypeCode) + " or FSellTypeCode = ' ') " +
                " and (FCuryCode = " +
                dbl.sqlString(this.CuryCode) + " or FCuryCode = ' ') " +
                " and FStartDate <=" + dbl.sqlDate(this.dStartDate) +
                " and FCheckState = 1 " +
                " group by FSellNetCode,FPortClsCode,FPortCode,FSellTypeCode,FCuryCode,FStartDate";

            sTmpTableName = "V_Tmp_TA_CStLink_" + pub.getUserCode(); //CashSettleLink缩写为CStLink,以防止View名过长。 sj edit 20080617
            if (dbl.yssViewExist(sTmpTableName)) {
                dbl.executeSql("drop view " + sTmpTableName);
            }
            if (dbl.getDBType() == YssCons.DB_ORA) {
                dbl.executeSql("create view " + sTmpTableName + " as (" + strSql +
                               ")");
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                dbl.executeSql("create view " + sTmpTableName + " as (" + strSql +
                               ")" + " definition only");
                dbl.executeSql("insert into " + sTmpTableName + "(" + strSql + ")");
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }

            return sTmpTableName;
        } catch (Exception e) {
            throw new YssException("获取数据至临时存储处出错");
        }
    }

    public Object getBeans(String sFeeCond) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        TACashSettleBean taCashSettle = null;
        ArrayList list = null;
        try {
        	//---add by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B start---//
            strSql = "select FSellNetCode,FPortClsCode,FPortCode,FSellTypeCode,FCuryCode,max(FStartDate) as FStartDate " +
            " from " + pub.yssGetTableName("Tb_TA_CashSettle") +
            " where (FSellNetCode = " +
            dbl.sqlString(this.SellNetCode) + " or FSellNetCode = ' ') " +
            " and (FPortClsCode = " +
            dbl.sqlString(this.PortClsCode) + " or FPortClsCode = ' ') " +
            " and (FPortCode = " +
            dbl.sqlString(this.PortCode) + " or FPortCode = ' ') " +
            " and (FSellTypeCode = " +
            dbl.sqlString(this.SellTypeCode) + " or FSellTypeCode = ' ') " +
            " and (FCuryCode = " +
            dbl.sqlString(this.CuryCode) + " or FCuryCode = ' ') " +
            " and FStartDate <=" + dbl.sqlDate(this.dStartDate) +
            " and FCheckState = 1 " +
            " group by FSellNetCode,FPortClsCode,FPortCode,FSellTypeCode,FCuryCode,FStartDate";
        	//---add by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B end---//
            if (sFeeCond.trim().length() == 0) {
            	//edit by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B
                strSql = "select * from (" + strSql + ")";
            } else {
            	//edit by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B
                strSql = "select * from (" + strSql + ") where " +
                    sFeeCond;
            }
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                list = new ArrayList();
                taCashSettle = new TACashSettleBean();
                taCashSettle.setSellNetCode(rs.getString("FSellNetCode"));
                taCashSettle.setPortClsCode(rs.getString("FPortClsCode"));
                taCashSettle.setPortCode(rs.getString("FPortCode"));
                taCashSettle.setSellTypeCode(rs.getString("FSellTypeCode"));
                taCashSettle.setCuryCode(rs.getString("FCuryCode"));
                taCashSettle.setStartDate(rs.getDate("FStartDate"));
                taCashSettle.setYssPub(pub);
                taCashSettle.getSetting();
            }
            if (taCashSettle != null) {
                list.add(taCashSettle);
            }
        } catch (Exception e) {
            throw new YssException("获取数据至临时存储处出错");
        } finally {
            dbl.closeResultSetFinal(rs);
            return list;
        }

    }
}
