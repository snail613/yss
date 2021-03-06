package com.yss.tools;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class NumSortManager
    extends BaseDataSettingBean {
    /*此Bean专用来修改主表及关链子表的编号
     */
    public String sComp = ""; //组合群
    public String sBefPrefix = ""; //改前序号前缀
    public String sAftPrefix = "CA"; //改后序号前缀
    public String sMastTab = ""; //主表;
    public String sMastTabField = ""; //主表字段
    public String[] sTabs = null; //所有的关链子表

    private void doChange(String oldStr, String changeStr) throws YssException {
        ///获取旧oldStr,与新 changeStr
        String[] sSubTabs = null;
        String reSql = "";
        try {
            reSql = " update " + sMastTab +
                " set " + sMastTabField + " =" + dbl.sqlString(changeStr) +
                " where " + sMastTabField + " =" + dbl.sqlString(oldStr);
            dbl.executeSql(reSql);
            for (int i = 0; i < sTabs.length; i++) { //遍历所有的子表或关链表
                sSubTabs = sTabs[i].split("\t");
                if (sSubTabs[0].trim().length() > 0 && sSubTabs[1].trim().length() > 0) {
                    reSql = " update " + sSubTabs[0] +
                        " set " + sSubTabs[1] + " =" + dbl.sqlString(changeStr) +
                        " where " + sSubTabs[1] + " =" + dbl.sqlString(oldStr);
                    dbl.executeSql(reSql);
                }
            }
        } catch (Exception e) {
            throw new YssException("修改表及关链表编号出错", e);
        }
    }

    public String getNumChange() { //此方法用于更改 NUM 不作为显示
        Connection conn = null;
        ResultSet rs = null;
        String reStr = "";
        String sNum = ""; //保存Num
        String[] sTmp = null; //保存多少个不同的时间与日期
        int iStrLen = 0;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            reStr = " select " + sMastTabField + " from " + sMastTab;
            rs = dbl.openResultSet(reStr);
            sBefPrefix = this.getPrefix(rs);
            iStrLen = sBefPrefix.length() + 1;
            reStr = "select count( distinct " +
                dbl.sqlSubStr(this.sMastTabField, iStrLen + "", 8 + "") +
                " ) as " + this.sMastTabField +
                " from " + this.sMastTab +
                " order by " + this.sMastTabField + "";
            rs = dbl.openResultSet(reStr);
            while (rs.next()) {
                if (rs.getInt(1) > 0) {
                    sTmp = new String[rs.getInt(1)];
                } else {
                    sTmp = new String[10];
                }
            }
            reStr = "select distinct " +
                dbl.sqlSubStr(this.sMastTabField, iStrLen + "", 8 + "") +
                " as " + this.sMastTabField +
                "  from " + this.sMastTab +
                " group by " + this.sMastTabField + " order by " +
                this.sMastTabField;
            rs = dbl.openResultSet(reStr);
            int i = 0;
            while (rs.next()) {
                sTmp[i] = rs.getString(this.sMastTabField);
                i++;
            }
            for (int iRow = 0; iRow < sTmp.length; iRow++) {
                reStr = "select " + this.sMastTabField + " from " +
                    this.sMastTab +
                    " where " + sMastTabField + " like '" + sBefPrefix +
                    sTmp[iRow] + "%'";
                rs = dbl.openResultSet(reStr);
                int ii = 1; //作为序号
                while (rs.next()) {
                    sNum = sAftPrefix + sTmp[iRow] +
                        "000000".substring(0,
                                           ("000000".length() - (ii + "").length())) +
                        ii;
                    doChange(rs.getString(sMastTabField), sNum);
                    ii++;
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "修改成功！";
        } catch (SQLException ex) {
            return ex.getMessage();
        } catch (Exception e) {
            return "访问主表出错\r\n" + e.getMessage();
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /*
       private String getTableName(String sTableName) { //将组合写入到表中去
          if (sTableName.toLowerCase().indexOf("tb_temp") < 0) {
             if (YssFun.left(sTableName, 3).equalsIgnoreCase("Tb_")) {
                return YssFun.left(sTableName, 3) + this.sComp +
                      YssFun.right(sTableName, sTableName.length() - 2);
             }
             else {
                return this.sComp + sTableName;
             }
          }
          else {
             return sTableName;
          }
       }*/

    private String getPrefix(ResultSet rs) throws SQLException { //获取字段前缀
        String Prefix = "";
        while (rs.next()) {
            if (Prefix.trim().length() > 0) {
                return Prefix;
            }
            Prefix = rs.getString(1);
            if (Prefix.trim().length() > 14) {
                Prefix = YssFun.left(Prefix, Prefix.length() - YssFun.right(Prefix, 14).length());
            }
        }
        return Prefix;
    }

    public NumSortManager() {

    }

}
