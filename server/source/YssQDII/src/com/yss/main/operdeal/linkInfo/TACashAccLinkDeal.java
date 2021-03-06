package com.yss.main.operdeal.linkInfo;

import com.yss.main.taoperation.TaCashAccLinkBean;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import com.yss.util.YssCons;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.util.YssFun;

public class TACashAccLinkDeal
    extends BaseLinkInfoDeal {
    private TaCashAccLinkBean TaCashAccLink = null;
    private String sellNetCode = "";
    private String portClsCode = "";
    private String portCode = "";
    private String sellTypeCode = "";
    private String curyCode = "";
    private java.util.Date dStartDate = null;
    private String cashAccCode = "";

    public TACashAccLinkDeal() {
    }

    public void setLinkAttr(BaseDataSettingBean LinkInfoBean) throws
        YssException {
        TaCashAccLink = (TaCashAccLinkBean) LinkInfoBean;
        if (TaCashAccLink != null) {
            setLinkParaAttr(TaCashAccLink.getSellNetCode(),
                            TaCashAccLink.getPortClsCode(),
                            TaCashAccLink.getPortCode(),
                            TaCashAccLink.getSellTypeCode(),
                            TaCashAccLink.getCuryCode(),
                            YssFun.parseDate(TaCashAccLink.getStartDate()));
        }
    }

    private void setLinkParaAttr(String SellNetCode, String PortClsCode,
                                 String PortTypeCode, String SellTypeCode,
                                 String CuryCode, java.util.Date dStartDate) {
        this.sellNetCode = SellNetCode;
        this.portClsCode = PortClsCode;
        this.portCode = PortTypeCode;
        this.sellTypeCode = SellTypeCode;
        this.curyCode = CuryCode;
        this.dStartDate = dStartDate;
    }

    public String buildLinkCondition() {
        StringBuffer buf = new StringBuffer();
        buf.append("FSellNetCode = " +
                   dbl.sqlString(this.sellNetCode)).append("\t");
        buf.append("FCuryCode = " + dbl.sqlString(this.curyCode)).append("\t");
        buf.append("FSellTypeCode = " +
                   dbl.sqlString(this.sellTypeCode)).append("\t");
        buf.append("FPortCode = " + dbl.sqlString(this.portCode)).append("\t");
        buf.append("FCuryCode = " + dbl.sqlString(this.curyCode));
        return buf.toString();
    }

    public String createTempData() throws YssException {
        String strSql = "";
        String sTmpTableName = "";
        try {
            strSql =
                "select a.* from (select FPortCode,FSellNetCode,FPortClsCode" +
                " ,FSellTypeCode,FCuryCode,FCashAccCode" +
                ",max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_TA_CashAccLink") +
                " where FStartDate <= " +
                dbl.sqlDate(this.dStartDate) +
                " and (FSellNetCode = " + dbl.sqlString(this.sellNetCode) +
                " or FsellNetCode=' ')" +
                " and (FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                " or FPortClsCode =' ')" +
                " and (FPortCode = " + dbl.sqlString(this.portCode) +
                " or FPortCode =' ')" +
                " and (FSellTypeCode = " + dbl.sqlString(this.sellTypeCode) +
                " or FSellTypeCode=' ')" +
                " and (FCuryCode = " + dbl.sqlString(this.curyCode) +
                " or FCuryCode=' ') and FCheckState = 1" +
                " group by FPortCode,FSellNetCode,FPortClsCode" +
                ",FSellTypeCode,FCuryCode,FCashAccCode " +
                " order by FPortClsCode desc " +
                ") a " +
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            
                
                " join (select FCashAccCode,  FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FCuryCode = " +
                dbl.sqlString(this.curyCode) +
               
                
                //end by lidaolong 
                "  " +
                ") b on a.FCashAccCode = b.FCashAccCode ";

            sTmpTableName = "V_Tmp_TA_CashAccLink_" + pub.getUserCode();
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
        CashAccountBean cashaccount = null;
        ArrayList list = null;
        try {
        	//---add by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B start---//
            strSql =
                "select a.* from (select FPortCode,FSellNetCode,FPortClsCode" +
                " ,FSellTypeCode,FCuryCode,FCashAccCode" +
                ",max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_TA_CashAccLink") +
                " where FStartDate <= " +
                dbl.sqlDate(this.dStartDate) +
                " and (FSellNetCode = " + dbl.sqlString(this.sellNetCode) +
                " or FsellNetCode=' ')" +
                " and (FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                " or FPortClsCode =' ')" +
                " and (FPortCode = " + dbl.sqlString(this.portCode) +
                " or FPortCode =' ')" +
                " and (FSellTypeCode = " + dbl.sqlString(this.sellTypeCode) +
                " or FSellTypeCode=' ')" +
                " and (FCuryCode = " + dbl.sqlString(this.curyCode) +
                " or FCuryCode=' ') and FCheckState = 1" +
                " group by FPortCode,FSellNetCode,FPortClsCode" +
                ",FSellTypeCode,FCuryCode,FCashAccCode " +
                " order by FPortClsCode desc " +
                ") a " +
                " join (select FCashAccCode,  FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FCuryCode = " +
                dbl.sqlString(this.curyCode) +
                "  " +
                ") b on a.FCashAccCode = b.FCashAccCode ";
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
                this.cashAccCode = rs.getString("FCashAccCode") + "";
                if (this.cashAccCode.length() != 0) {
                    cashaccount = new CashAccountBean();
                    cashaccount.setYssPub(pub);
                    cashaccount.setStrCashAcctCode(this.cashAccCode);
                    cashaccount.getSetting();
                }
            }
            if (cashaccount != null) {
                list.add(cashaccount);
            }
        } catch (Exception e) {
            throw new YssException("获取数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
            return list;
        }
    }

}
