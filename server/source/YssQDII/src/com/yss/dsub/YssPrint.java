package com.yss.dsub;

//打印服务实现，包括打印设置的加载和保存，和凭证打印中凭证信息的收集

import java.sql.*;
import com.yss.util.*;

public class YssPrint {
    public YssPrint() {
    }

    //辅助变量
    private YssPub pubVar = null;
    private DbBase dbLink = null;

    //常用表名辅助变量
    public final void setYssPub(YssPub pub) {
        pubVar = pub;
        dbLink = pub.getDbLink();
    }

    /**
     * 加载报表打印设置
     * @param param String：标记 vbtab 四个字体名用vbtab间隔
     * @throws YssException
     * @return String：标记的设置信息，和字体信息
     */
    public String loadRptPrintSetup(String param) throws YssException {
        String arr[] = null;
        ResultSet rs = null;
//        Statement st = null;
        StringBuffer buf = new StringBuffer();
        String stmp = null;
        int i;
        PreparedStatement pst = null;
        //arr=param.trim().split("\t",-1); .trim会去除开头和结尾的\t导致出错
        arr = YssFun.trim(param).split("\t", -1);
        try {
            if (arr[0].length() != 0) { //加载页面设置信息
                rs = dbLink.openResultSet(" select * from cwgrid where upper(ftype) = " +  dbLink.sqlString(arr[0].toUpperCase()));
                if (rs.next()) {
                    stmp = rs.getString("PaperSize");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");
                    stmp = rs.getString("Orientation");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");
                    stmp = rs.getString("MarginLeft");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");
                    stmp = rs.getString("MarginRight");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");
                    stmp = rs.getString("MarginTop");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");
                    stmp = rs.getString("MarginBottom");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");
                    stmp = rs.getString("PaperWidth");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");
                    stmp = rs.getString("PaperHeight");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");

                    //下面几个因为null时的默认值正好是0所以可以简化
                    buf.append(rs.getShort("Color")).append("\t");
                    buf.append(rs.getShort("Fill")).append("\t");
                    buf.append(rs.getShort("OpName")).append("\t");
                    buf.append(rs.getShort("mDate")).append("\t");

                    stmp = rs.getString("PageCode");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");
                    stmp = rs.getString("JeLine");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");
                    stmp = rs.getString("AutoFit");
                    if (stmp != null) {
                        buf.append(stmp);
                    }
                    buf.append("\t");

                    //以下字段默认为0
                    arr[1] = rs.getString("Title1");
                    buf.append(arr[1]).append("\t");
                    arr[2] = rs.getString("Title2");
                    buf.append(arr[2]).append("\t");
                    arr[3] = rs.getString("Title3");
                    buf.append(arr[3]).append("\t");
                    arr[4] = rs.getString("Title4");
                    buf.append(arr[4]);
                }
                rs.close();
            }
            //加载title1234的字体信息
            pst = dbLink.openPreparedStatement(" select * from font where ID= ? ");
            for (i = 1; i <= 4; i++) {
                buf.append("\r\n");
                pst.setString(1,arr[i]);
                rs = pst.executeQuery();//modified by yeshenghong for CCB security check 20121018 
//                	dbLink.openResultSet("select * from font where ID=" + arr[i]);
                if (rs.next()) {
                    stmp = rs.getString("Name");
                    if (!rs.wasNull()) {
                        buf.append(stmp);
                    }
                    buf.append("\t");
                    stmp = rs.getString("FSize");
                    if (!rs.wasNull()) {
                        buf.append(stmp);
                    }
                    buf.append("\t");

                    buf.append(rs.getShort("Bold")).append("\t");
                    buf.append(rs.getShort("Italic"));
                }
                rs.close();
            }
            return buf.toString();
        } catch (SQLException se) {
            throw new YssException("加载报表打印设置出错！", se);
        } finally {
        	dbLink.closeStatementFinal(pst);
        	dbLink.closeResultSetFinal(rs);
        }
    }

    /**
     * 保存报表打印设置
     * @param param String：普通设置和字体设置，字体设置之间，用\f\f间隔，其他用\t间隔
     * @throws YssException
     */
    public void saveRptPrintSetup(String param) throws YssException {
        String arr1[] = null, tag = null;
        String arr2[] = null;
        int title[] = {
            0, 0, 0, 0};
        int i;
        Connection conn = dbLink.loadConnection();
        Statement st = null;
        ResultSet rs = null;
        boolean btrans = false;
        StringBuffer buf = new StringBuffer();
        String sFields[] = {
            "FType", "PaperSize", "Orientation", "MarginLeft", "MarginRight",
            "MarginTop", "MarginBottom", "PaperWidth", "PaperHeight", "Color",
            "mDate", "OpName", "PageCode", "JeLine", "AutoFit", "Fill"};

        ///arr1 = param.split(YssCons.YSS_LINESPLITMARK, -1);
        arr1 = param.split("\r\n", -1); // by leeyu 080716
        try {
            conn.setAutoCommit(false);
            btrans = true;

            st = dbLink.openStatement(true); //因为要反复使用，所以创建一个statement，这样不必每次打开rs都要建
            //填页面设置
            arr2 = arr1[0].split("\t", -1);

            tag = arr2[0].toUpperCase();
            rs = st.executeQuery("select " + dbLink.sqlStar("cwgrid") +
                                 " from cwgrid where upper(ftype)='" +
                                dbLink.sqlString(tag));
            if (!rs.next()) {
                buf.append("insert into cwgrid (").append(YssFun.join(sFields, ","));
                buf.append(") values('").append(tag).append("',");
                buf.append(YssFun.join(arr2, ",", 1, sFields.length - 1)).append(")"); // by sj
                st.executeUpdate(buf.toString());
            } else {
                title[0] = rs.getInt("Title1");
                title[1] = rs.getInt("Title2");
                title[2] = rs.getInt("Title3");
                title[3] = rs.getInt("Title4");

                for (i = 1; i < sFields.length; i++) {
                    rs.updateString(sFields[i], arr2[i]);
                }

                rs.updateRow();
            }
            rs.close();
            
            int size = 0;
            int bold = 0;
            int italic = 0;
            //保存titlefont
            for (i = 0; i <= 3; i++) {
                arr2 = arr1[i + 1].split("\t", -1);
                size = Integer.parseInt(arr2[1]);
                bold = Integer.parseInt(arr2[2]);//modified by yeshenghong for CCB security check 20121018 
                italic = Integer.parseInt(arr2[3]);
                rs = st.executeQuery(
                    "select ID,Name,FSize,Bold,Italic from Font where ID=" +
                    title[i]);
                if (!rs.next()) {
                    title[i] = getCode("Font");

                    buf.setLength(0);
                    buf.append("insert into Font values(").append(title[i]);
                    buf.append(",").append(dbLink.sqlString(arr2[0])).append(",").append(size);
                    buf.append(",").append(bold).append(",").append(italic);
                    st.executeUpdate(buf.append(")").toString());
                } else {
                    rs.updateString("Name", arr2[0]);
                    rs.updateString("FSize", arr2[1]);
                    rs.updateString("Bold", arr2[2]);
                    rs.updateString("Italic", arr2[3]);

                    rs.updateRow();
                }
               dbLink.closeResultSetFinal(rs);
            }

            st.executeUpdate("update cwgrid set Title1=" + title[0] + ",Title2=" +
                             title[1] + ",Title3=" + title[2] + ",Title4=" +
                             title[3] + " where upper(fType)='" + tag + "'");
            st.close();
            conn.commit();
            conn.setAutoCommit(true);
            return;
        } catch (SQLException se) {
            throw new YssException("保存打印设置出错！", se);
        } finally {
        	dbLink.closeStatementFinal(st);
        	dbLink.closeResultSetFinal(rs);
        	dbLink.endTransFinal(btrans);
        }
    }

    private int getCode(String table) throws SQLException, YssException {
        int i = 1;
        ResultSet rs = null;
        try {
            rs = dbLink.openResultSet("select id from " + table +
                                      " order by id desc");
            if (rs.next()) {
                i = rs.getInt("id") + 1;
            }
            rs.close();
            return i;
        } catch (SQLException se) {
            throw se; //继续向上抛，上层肯定还有处理SQLException的
        } finally {
        	dbLink.closeResultSetFinal(rs);
        }
    }

}
