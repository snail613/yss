package com.yss.main.cusreport;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title:RepTabCellBean 报表格式--单元格设置 </p>
 * <p>Description:1 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class RepTabCellBean
    extends BaseDataSettingBean implements IDataSetting {

    private String RepCode = ""; //模板代码
    private String RelaType = "";
    private String Row = "0"; //行
    private String Col = "0"; //列
    private String Content = ""; //内容
    private String LLine = "0"; //左边线
    private String TLine = "0"; //上边线
    private String RLine = "0"; //右边线
    private String BLine = "0"; //下边线
    private String LColor = "0"; //左边线颜色
    private String TColor = "0"; //上边线颜色
    private String RColor = "0"; //右边线颜色
    private String BColor = "0"; //下边线颜色
    private String BackColor = "16777215"; //背景色
    private String ForeColor = "0"; //前景色
    private String FontName = "宋体"; //字体名称
    private String FontSize = "11"; //字体大小
    private String FontStyle = "0"; //字体格式
    private String DataType = "0"; //数据类型
    private String Format = ""; //格式
    private int iMerge = 0; // 合并信息
    private boolean isDsFieldSet = false;

    private String OldRepCode = "";
    public void setIsDsFieldSet(boolean isDsFieldSet) {
        this.isDsFieldSet = isDsFieldSet;
    }

    public void setIMerge(int iMerge) {
        this.iMerge = iMerge;
    }

    public boolean isIsDsFieldSet() {
        return isDsFieldSet;
    }

    public int getIMerge() {
        return iMerge;
    }

    public void setRelaType(String RelaType) {
        this.RelaType = RelaType;
    }

    public String getRelaType() {
        return RelaType;
    }

    public void setFontSize(String FontSize) {
        this.FontSize = FontSize;
    }

    public String getFontSize() {
        return FontSize;
    }

    public void setRow(String Row) {
        this.Row = Row;
    }

    public String getRow() {
        return Row;
    }

    public void setBackColor(String BackColor) {
        this.BackColor = BackColor;
    }

    public String getBackColor() {
        return BackColor;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public String getContent() {
        return Content;
    }

    public void setFontName(String FontName) {
        this.FontName = FontName;
    }

    public String getFontName() {
        return FontName;
    }

    public void setFormat(String Format) {
        this.Format = Format;
    }

    public String getFormat() {
        return Format;
    }

    public void setBLine(String BLine) {
        this.BLine = BLine;
    }

    public String getBLine() {
        return BLine;
    }

    public void setLLine(String LLine) {
        this.LLine = LLine;
    }

    public String getLLine() {
        return LLine;
    }

    public void setBColor(String BColor) {
        this.BColor = BColor;
    }

    public String getBColor() {
        return BColor;
    }

    public void setRLine(String RLine) {
        this.RLine = RLine;
    }

    public String getRLine() {
        return RLine;
    }

    public void setCol(String Col) {
        this.Col = Col;
    }

    public String getCol() {
        return Col;
    }

    public void setForeColor(String ForeColor) {
        this.ForeColor = ForeColor;
    }

    public String getForeColor() {
        return ForeColor;
    }

    public void setRColor(String RColor) {
        this.RColor = RColor;
    }

    public String getRColor() {
        return RColor;
    }

    public void setTColor(String TColor) {
        this.TColor = TColor;
    }

    public String getTColor() {
        return TColor;
    }

    public void setTLine(String TLine) {
        this.TLine = TLine;
    }

    public String getTLine() {
        return TLine;
    }

    public void setLColor(String LColor) {
        this.LColor = LColor;
    }

    public String getLColor() {
        return LColor;
    }

    public String getOldRepCode() {
        return OldRepCode;
    }

    public String getRepCode() {
        return RepCode;
    }

    public void setFontStyle(String FontStyle) {
        this.FontStyle = FontStyle;
    }

    public String getFontStyle() {
        return FontStyle;
    }

    public void setDataType(String DataType) {
        this.DataType = DataType;
    }

    public String getDataType() {
        return DataType;
    }

    public RepTabCellBean() {
    }

    public RepTabCellBean(String sRepCode) {
        this.RepCode = sRepCode;
    }

    public RepTabCellBean(String sRepCode, String sOldRepCode) {
        this.RepCode = sRepCode;
        this.OldRepCode = sOldRepCode;
    }

    public void setRepCode(String sRepCode) {
        this.RepCode = sRepCode;
    }

    public void setOldRepCode(String sOldRepCode) {
        this.OldRepCode = sOldRepCode;
    }

    /**
     * parseRowStr
     * 解析数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        try {
            if (sRowStr.equals("")) {
                return;
            }
            reqAry = sRowStr.split("~");
            if (reqAry.length < 18) {
                return;
            }
            this.Row = reqAry[0];
            this.Col = reqAry[1];
            this.Content = reqAry[2];
            this.LColor = reqAry[3];
            this.LLine = reqAry[4];
            this.TColor = reqAry[5];
            this.TLine = reqAry[6];
            this.BColor = reqAry[7];
            this.BLine = reqAry[8];
            this.RColor = reqAry[9];
            this.RLine = reqAry[10];
            this.BackColor = reqAry[11];
            this.ForeColor = reqAry[12];
            this.FontName = reqAry[13];
            this.FontSize = reqAry[14];
            this.FontStyle = reqAry[15];
            this.DataType = reqAry[16];
            this.Format = reqAry[17] != null ? reqAry[17] : "";
            //modify huangqirong 2012-09-25 bug #5800
            if (reqAry.length > 18) {            	
                if (reqAry[18].trim().length() != 0) {
                    this.RepCode = reqAry[18];
                }
                //this.RelaType = reqAry[20]; 
                
            	if(reqAry.length > 20){
            		if(YssFun.isNumeric(reqAry[20])) {
            			this.iMerge = YssFun.toInt(reqAry[20]);
            		}
            	}else {
            		if(YssFun.isNumeric(reqAry[18])) {
            			this.iMerge = YssFun.toInt(reqAry[18]);
            		}
                }            
            } else {
                this.iMerge = 0;
            }
            //---end---
            
        } catch (Exception e) {
            throw new YssException("解析报表设计详细信息出错\r\n" + e.getMessage(), e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();

        buf.append(this.Row).append("~");
        buf.append(this.Col).append("~");
        buf.append(this.Content).append("~");
        buf.append(this.LColor).append("~");
        buf.append(this.LLine).append("~");
        buf.append(this.TColor).append("~");
        buf.append(this.TLine).append("~");
        buf.append(this.BColor).append("~");
        buf.append(this.BLine).append("~");
        buf.append(this.RColor).append("~");
        buf.append(this.RLine).append("~");
        buf.append(this.BackColor).append("~");
        buf.append(this.ForeColor).append("~");
        buf.append(this.FontName).append("~");
        buf.append(this.FontSize).append("~");
        buf.append(this.FontStyle).append("~");
        buf.append(this.DataType).append("~");
        //buf.append(this.Format.replaceAll("\t",",")).append("~");
        buf.append(Format == null ? "null" : this.Format.replaceAll("\t", ",")).append("~"); //MS00026 by leeyu 出报表时此字段为null时就报错 2008-11-17
        buf.append(this.RepCode).append("~");
        buf.append(this.RelaType).append("~");
        buf.append(this.iMerge);

        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {

    }

    /**
     * saveSetting
     *
     */
    public String addSetting() throws YssException {
        return "";
    }

    /**
     * editSetting：
     *
     * @return String
     */
    public String editSetting() throws YssException {
        return "";
    }

    /**
     * delSetting : 删除一条设置信息
     */
    public void delSetting() throws YssException {

    }

    /**
     * auditSetting : 审核一条设置信息
     */
    public void checkSetting() throws YssException {

    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            sMutilRowAry = sMutilRowStr.split("\f\r\n");

            //  if(sMutilRowAry[0].split("~").length < 18) {
//           if(!this.OldRepCode.equals(this.RepCode)){
//              strSql = "update " + pub.yssGetTableName("Tb_Rep_Cell") +
//                    " set FRelaCode=" + dbl.sqlString(this.RepCode) +
//                    " where FRelaType = " +
//                    dbl.sqlString(this.RelaType) +
//                    " and FRelaCode = " +
//                    dbl.sqlString(this.OldRepCode);
//              dbl.executeSql(strSql);
//          //    return "";
//         //  }
//
//        }
			//modify huangqirong 2012-09-25 bug #5800 解掉注释
            strSql = "delete from " + pub.yssGetTableName("Tb_Rep_Cell") + " where FRelaCode = " +
                dbl.sqlString(this.OldRepCode) + " and FRelaType = " + dbl.sqlString(this.RelaType);
            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Rep_Cell") + " (FRelaCode, FRow, FCol, FContent, " +
                " FLLine, FTLine, FRLine, FBLine, FLColor, FTColor, FRColor, FBColor, FBackColor, FForeColor, " +
                " FFontName, FFontSize, FFontStyle, FDataType, FFormat,FRelaType,FIsMergeCol) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);
//            String preRelaType = "";
            for (int i = 0; i < sMutilRowAry.length; i++) {

                this.parseRowStr(sMutilRowAry[i]);
                if (this.Row.length() == 0) {
                    continue;
                }
                 //modify huangqirong 2012-09-25 bug #5800 注释掉
//                if(!preRelaType.equals(this.RelaType))
//                {
//                	strSql = "delete from " + pub.yssGetTableName("Tb_Rep_Cell") + " where FRelaCode = " +
//                    dbl.sqlString(this.OldRepCode) + " and FRelaType = " + dbl.sqlString(this.RelaType);
//                	dbl.executeSql(strSql);//bug5562 modified by yeshenghong 20120910
//                }
				//--end---
                pstmt.setString(1, this.RepCode);

                if (isDsFieldSet) {
                    this.Row = String.valueOf(Integer.parseInt(this.Row) * -1 - 1);
                }

                pstmt.setInt(2, YssFun.toInt(this.Row));
                pstmt.setInt(3, YssFun.toInt(this.Col));
                pstmt.setString(4, this.Content);
                pstmt.setInt(5, YssFun.toInt(this.LLine));
                pstmt.setInt(6, YssFun.toInt(this.TLine));
                pstmt.setInt(7, YssFun.toInt(this.RLine));
                pstmt.setInt(8, YssFun.toInt(this.BLine));
                pstmt.setInt(9, YssFun.toInt(this.LColor));
                pstmt.setInt(10, YssFun.toInt(this.TColor));
                pstmt.setInt(11, YssFun.toInt(this.RColor));
                pstmt.setInt(12, YssFun.toInt(this.BColor));
                pstmt.setInt(13, YssFun.toInt(this.BackColor));
                pstmt.setInt(14, YssFun.toInt(this.ForeColor));
                pstmt.setString(15, this.FontName);
                pstmt.setInt(16, YssFun.toInt(this.FontSize));
                pstmt.setInt(17, YssFun.toInt(this.FontStyle));
                pstmt.setInt(18, YssFun.toInt(this.DataType));
                pstmt.setString(19, this.Format.replaceAll(",", "\t"));
                pstmt.setString(20, this.RelaType);
                pstmt.setInt(21, this.iMerge);

                pstmt.executeUpdate();
            }
            return "";
        } catch (Exception e) {
            throw new YssException("保存报表格式详细设计信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }

    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getAllSetting
     * @return String
     */
    public String getAllSetting() throws YssException {
        return "";
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";

        return sResult;
    }

    public void setRepAttr(ResultSet rs) throws SQLException {

    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.setRepAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取报表详细设计信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData1
     * 获取报表模板数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";

        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";

        return "";
    }

    public String getListViewData2() throws YssException {
        String strSql = "";

        return "";

    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        return "";
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
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
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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

}
