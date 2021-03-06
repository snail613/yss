package com.yss.main.operdeal.linkInfo;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import java.util.ArrayList;
import com.yss.main.taoperation.TAFeeLink;
import java.sql.ResultSet;
import com.yss.main.parasetting.FeeBean;
import com.yss.util.YssCons;

public class TAFeeLinkDeal
    extends BaseLinkInfoDeal {
    private TAFeeLink feeLink = null;
    
    /**shashijie 2011-09-22 添加注视 */
    private String sellNetCode = "";//销售网点代码
    private String sellTypeCode = "";//销售类型代码
    private String currencyCode = "";//货币代码

    public TAFeeLinkDeal() {
    }

    public void setLinkAttr(BaseDataSettingBean LinkInfoBean) throws
        YssException {
        feeLink = (TAFeeLink) LinkInfoBean;
        if (feeLink != null) {
            setLinkParaAttr(feeLink.getSellNetCode(),
                            feeLink.getSellTypeCode(),
                            feeLink.getCuryCode());
        }
    }

    public String buildLinkCondition() {
        StringBuffer buf = new StringBuffer();
        buf.append("FSellNetCode = " +
                   dbl.sqlString(this.sellNetCode)).append("\t");
        buf.append("FCuryCode = " + dbl.sqlString(this.currencyCode)).append("\t");
        buf.append("FSellTypeCode = " +
                   dbl.sqlString(this.sellTypeCode)).append("\t");
        return buf.toString();
    }

    public String createTempData() throws YssException {
        String strSql = "";
        String sTmpTableName = "";
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_TA_FeeLink") +
                " where (FSellNetCode = " + dbl.sqlString(this.sellNetCode) +
                " or FSellNetCode = ' ')" +
                " and (FSellTypeCode = " + dbl.sqlString(this.sellTypeCode) +
                " or FSellTypeCode = ' ')" +
                " and (FCuryCode = " + dbl.sqlString(this.currencyCode) +
                " or FCuryCode = ' ')" + " and FCheckState <> 2" +
                /**shashijie 2011-09-15 STORY 1580 增加费用类型的过滤(之前不知道为何状态只考虑!=2未考虑已审核未审核状态)*/
                " and FFeeType = 0 ";//0:交易费用,1赎回款手续费
                /**end*/
            sTmpTableName = "V_Tmp_TA_FeeLink_" + pub.getUserCode();
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
        FeeBean fee = null;
        ArrayList list = null;
        try {
        	//---add by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B start---//
            strSql = "select * from " + pub.yssGetTableName("Tb_TA_FeeLink") +
            " where (FSellNetCode = " + dbl.sqlString(this.sellNetCode) +
            " or FSellNetCode = ' ')" +
            " and (FSellTypeCode = " + dbl.sqlString(this.sellTypeCode) +
            " or FSellTypeCode = ' ')" +
            " and (FCuryCode = " + dbl.sqlString(this.currencyCode) +
            " or FCuryCode = ' ')" + " and FCheckState <> 2" +
            " and FFeeType = 0 ";//0:交易费用,1赎回款手续费
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
                for (int j = 1; j <= 6; j++) {
                    if (rs.getString("FFeeCode" + j) != null) {
                        fee = new FeeBean();
                        fee.setYssPub(pub);
                        fee.setFeeCode(rs.getString("FFeeCode" + j));
                        fee.getSetting(); //这里是为了设置FeeBean的属性
                        if (fee.checkStateId == 1) {
                            list.add(fee);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("获取数据至临时存储处出错");
        } finally {
            dbl.closeResultSetFinal(rs);
            return list;
        }
    }

    /*public ArrayList getLinkInfoBeans() throws
          YssException {
       String strSql = "";
       ArrayList list = new ArrayList();
       String[] sFeeCondAry = null;
       String sTmpTableName = "";
       ResultSet rs = null;
       FeeBean fee = null;
       try {
          strSql = "select * from " + pub.yssGetTableName("Tb_TA_FeeLink") +
                " where (FSellNetCode = " + dbl.sqlString(this.sellNetCode) +
                " or FSellNetCode = ' ')" +
                " and (FSellTypeCode = " + dbl.sqlString(this.sellTypeCode) +
                " or FSellTypeCode = ' ')" +
                " and (FCuryCode = " + dbl.sqlString(this.currencyCode) +
                " or FCuryCode = ' ')" + " and FCheckState <> 2";
          sTmpTableName = "Tb_Tmp_TA_FeeLink_" + pub.getUserCode();
          if (dbl.yssTableExist(sTmpTableName)) {
             dbl.executeSql("drop table " + sTmpTableName);
          }
          //-----------------2007.11.30 蒋锦 修改-------考虑使用DB2的情况-----------------//
          if (dbl.getDBType() == YssCons.DB_ORA) {
             dbl.executeSql("create table " + sTmpTableName + " as (" + strSql +
                            ")");
          }
          else if (dbl.getDBType() == YssCons.DB_DB2) {
             dbl.executeSql("create table " + sTmpTableName + " as (" + strSql +
                            ")" + " definition only");
     dbl.executeSql("insert into " + sTmpTableName + "(" + strSql + ")");
          }
          else {
             throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
          }
          //---------------------------------------------------------------------------//
          sFeeCondAry = buildLinkCondition().split("\t");
          for (int i = 0; i <= sFeeCondAry.length; i++) {
             if (i == sFeeCondAry.length) {
                strSql = "select * from " + sTmpTableName;
             }
             else {
                strSql = "select * from " + sTmpTableName + " where " +
                      sFeeCondAry[i];
             }
             rs = dbl.openResultSet(strSql);
             if (rs.next()) {
                for (int j = 1; j <= 6; j++) {
                   if (rs.getString("FFeeCode" + j) != null) {
                      fee = new FeeBean();
                      fee.setYssPub(pub);
                      fee.setFeeCode(rs.getString("FFeeCode" + j));
                      fee.getSetting(); //这里是为了设置FeeBean的属性
                      if (fee.checkStateId == 1) {
                         list.add(fee);
                      }
                   }
                }
                break;
             }
             dbl.closeResultSetFinal(rs);
          }
       }
       catch (Exception e) {
          throw new YssException("获取费用集合出错");
       }
       finally {
          dbl.closeResultSetFinal(rs);
          return list;
       }
        }*/

    public void setLinkParaAttr(String sSellNetCode, String sSellTypeCode,
                                String sCuryCode) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            this.sellNetCode = sSellNetCode;
            this.sellTypeCode = sSellTypeCode;
            this.currencyCode = sCuryCode;
            strSql =
                "select FSellNetCode,FSellTypeCode,FCuryCode from " +
                pub.yssGetTableName("Tb_TA_FeeLink") +
                " where FStartDate = (select Max(FStartDate) from " +
                pub.yssGetTableName("Tb_TA_FeeLink") +
                " where FSellNetCode = " + dbl.sqlString(sSellNetCode) +
                " and FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                ") and FSellTypeCode = " + dbl.sqlString(sSellTypeCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.sellNetCode = rs.getString("FSellNetCode") + "";
                this.sellTypeCode = rs.getString("FSellTypeCode") + "";
                this.currencyCode = rs.getString("FCuryCode") + "";
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
