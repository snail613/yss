package com.yss.main.platform.pfsystem.facecfg.pojo;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/***
 * 解析控件的属性
 */

public class FaceCfgParamBean
    extends BaseBean implements IYssConvert {
    private boolean bIsMust = false; //必须输入
    private boolean bIsAll = false; //全部
    private boolean bIsReadOnly = false; //不能修改
    private boolean bIsNumeric = false; //只能是数字
    private boolean bChecked = false; //是否选择
    private String sCtlCaption = ""; //输入控件对应的标题
    private String sCtlName = ""; //控件名称
    private String sURL = ""; //URL
    private String sClassName = ""; //Class Name
    private String sDllName = ""; //Dll Name
    private String sFrmClassName = ""; //Frm Clas Name
    private String sDataSource = ""; //DataSource
    private String sFormatStr = ""; //格式字符串
    private String sShowText = ""; //显示文本
    private String sBtnType = ""; //按钮类型 一般为"find"
    private String sShowAttrCode = ""; //加载显示代码
    private String sShowAttrName = ""; //加载显示名称
    private String sSelScript = ""; //显示数据时的条件
    private String sCtlInd = ""; //控件标识
    private int iFormat = 0; //格式 "custom"
    private int iCtlType = 0; //控件类型
    private int iIntLen = 0; //整数位数
    private int iDecLen = 0; //小数位数
    private int iMaxLength = 0; //最大长度
    private int iTextAlign = 0; //对齐方式 0:左 1: 中 2:右
    private int iLength = 0; //控件长度
    private int iLocalX = 0; //位置 X 坐标
    private int iLocalY = 0; //位置 Y 坐标
    private int iWidth = 0; //宽度
    private int iHeight = 0; //高度
    private int iTabIndex = 0; //Table Index
    private int iParamIndex = 0; //Param Index
    //2008.11.17 蒋锦 添加
    //编号：MS00009 文档：QDV4交银施罗德2008年11月06日01_A
    private String sDefault = ""; //TextBox 控件的默认值

    private String sCtlGrpCode = ""; //控件组代码
    private String sCtlCode = ""; //控件代码
    
    //add by huangqirong 2012-01-31 story #1284
    private String sRelationType = "";
    
    public String getRelationType(){
    	return this.sRelationType;
    }
    
    public void setRelationType(String relationType){
    	this.sRelationType=relationType;
    }
    //---end---
    
    public FaceCfgParamBean() {
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
        String[] arrStr = sRowStr.split("\t");
        this.sCtlGrpCode = arrStr[0];
        this.sCtlCode = arrStr[1];
    }

    /**
     * 可以用SQL的方式取 控件的参数值
     * 需参数有:控件组代码,控件代码
     * @throws YssException
     */
    public void getSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        String sqlStr = "";
        try {
            sqlStr = " select FCtlType,FParam from Tb_PFSys_FaceCfgInfo where FCtlGrpCode =" +
                dbl.sqlString(this.sCtlGrpCode) + " and FCtlCode =" +
                dbl.sqlString(this.sCtlCode);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                getCtlParams(rs.getString("FParam"), rs.getInt("FCtlType"));
            }
        } catch (Exception e) {
            throw new YssException("用SQL取控件的属性出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, false);
        }
    }

    /**
     * 取控件的参数值
     * @param sParam String 参数字符串
     * @param iType int 控件类型
     * @throws YssException
     */
    public void getCtlParams(String sParam, int iType) throws YssException {
        String[] arrPar = null;

        try {
        	//add by huangqirong 2012-01-31 story #1284   
        	if(sParam.indexOf("\b\b") > -1){        		
        		String [] temArr = sParam.split("\b\b");
        		sParam = temArr[0];
        		if(temArr.length >= 2)
        			this.sRelationType = temArr[1];        		
        	}
        	//---end---
        	
            arrPar = sParam.split(YssCons.YSS_ITEMSPLITMARK2);
            if (iType == 0) {
                //Label
                setBaseParams(arrPar);
            } else if (iType == 1) {
                //TextBox
                if (arrPar[0].equalsIgnoreCase("true")) {
                    this.bIsMust = true;
                } else {
                    this.bIsMust = false;
                }
                if (arrPar[1].equalsIgnoreCase("true")) {
                    this.bIsReadOnly = true;
                } else {
                    this.bIsReadOnly = false;
                }
                if (arrPar[2].equalsIgnoreCase("true")) {
                    this.bIsNumeric = true;
                } else {
                    this.bIsNumeric = false;
                }
                this.sCtlCaption = arrPar[3];
                if (YssFun.isNumeric(arrPar[4])) {
                    this.iIntLen = YssFun.toInt(arrPar[4]);
                }
                if (YssFun.isNumeric(arrPar[5])) {
                    this.iDecLen = YssFun.toInt(arrPar[5]);
                }
                if (YssFun.isNumeric(arrPar[6])) {
                    this.iMaxLength = YssFun.toInt(arrPar[6]);
                }
                if (YssFun.isNumeric(arrPar[7])) {
                    this.iTextAlign = YssFun.toInt(arrPar[7]);
                }
                //2008.11.17 蒋锦 添加
                //编号：MS00009 文档：QDV4交银施罗德2008年11月06日01_A
                if (arrPar.length > 15) {
                    this.sDefault = arrPar[8];
                }
                setBaseParams(arrPar);
            } else if (iType == 2) {
                //ComboBox
                this.sDataSource = arrPar[0];
                setBaseParams(arrPar);
            } else if (iType == 3) {
                //DateTimePicker
                if (YssFun.isNumeric(arrPar[0])) {
                    this.iFormat = YssFun.toInt(arrPar[0]);
                }
                this.sFormatStr = arrPar[1];
                if (arrPar[2].equalsIgnoreCase("true")) {
                    this.bChecked = true;
                } else {
                    this.bChecked = false;
                }
                setBaseParams(arrPar);
            } else if (iType == 4) {
                //SelectControl
                if (arrPar[0].equalsIgnoreCase("true")) {
                    this.bIsMust = true;
                } else {
                    this.bIsMust = false;
                }
                this.sCtlCaption = arrPar[1];
                if (YssFun.isNumeric(arrPar[2])) {
                    this.iMaxLength = YssFun.toInt(arrPar[2]);
                }
                this.sURL = arrPar[3];
                this.sClassName = arrPar[4];
                this.sBtnType = arrPar[5];
                this.sShowAttrCode = arrPar[6];
                this.sShowAttrName = arrPar[7];
                this.sSelScript = arrPar[8];
                this.sDllName = arrPar[9];
                this.sFrmClassName = arrPar[10];
                if (arrPar[11].equalsIgnoreCase("true")) {
                    this.bIsAll = true;
                } else {
                    this.bIsAll = false;
                }
                setBaseParams(arrPar);
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }

    /**
     * 基本的参数值
     * @param arrPar String[]
     * @throws YssException
     */
    private void setBaseParams(String[] arrPar) throws YssException {
        String[] arrSize = null;
        String[] arrLocal = null;
        int iL = arrPar.length;
        if (arrPar[iL - 3].indexOf(",") >= 0 && arrPar[iL - 4].indexOf(",") >= 0) { //判断倒数3，4项是否为location,size。若是则获取ind sj edit 20080321
            this.sCtlInd = arrPar[--iL];
        }
        if (YssFun.isNumeric(arrPar[--iL])) {
            this.iTabIndex = YssFun.toInt(arrPar[iL]);
        }
        //arrLocal = arrPar[iL].split(",");
        arrLocal = arrPar[--iL].split(",");
        if (arrLocal.length >= 2) {
            if (YssFun.isNumeric(arrLocal[0])) {
                this.iLocalX = YssFun.toInt(arrLocal[0]);
            }
            if (YssFun.isNumeric(arrLocal[1])) {
                this.iLocalY = YssFun.toInt(arrLocal[1]);
            }
        }
        arrSize = arrPar[--iL].split(",");
        if (arrSize.length >= 2) {
            if (YssFun.isNumeric(arrSize[0])) {
                this.iWidth = YssFun.toInt(arrSize[0]);
            }
            if (YssFun.isNumeric(arrSize[1])) {
                this.iHeight = YssFun.toInt(arrSize[1]);
            }
        }
        this.sShowText = arrPar[--iL];
        if (YssFun.isNumeric(arrPar[--iL])) {
            this.iCtlType = YssFun.toInt(arrPar[iL]);
        }
        if (YssFun.isNumeric(arrPar[--iL])) {
            this.iParamIndex = YssFun.toInt(arrPar[iL]);
        }
        this.sCtlName = arrPar[--iL];
    }

    public String getSURL() {
        return sURL;
    }

    public String getSShowText() {
        return sShowText;
    }

    public String getSShowAttrName() {
        return sShowAttrName;
    }

    public String getSShowAttrCode() {
        return sShowAttrCode;
    }

    public String getSSelScript() {
        return sSelScript;
    }

    public String getSFrmClassName() {
        return sFrmClassName;
    }

    public String getSFormatStr() {
        return sFormatStr;
    }

    public String getSDllName() {
        return sDllName;
    }

    public String getSDataSource() {
        return sDataSource;
    }

    public String getSCtlName() {
        return sCtlName;
    }

    public String getSCtlGrpCode() {
        return sCtlGrpCode;
    }

    public String getSCtlCode() {
        return sCtlCode;
    }

    public String getSCtlCaption() {
        return sCtlCaption;
    }

    public String getSClassName() {
        return sClassName;
    }

    public String getSBtnType() {
        return sBtnType;
    }

    public int getIWidth() {
        return iWidth;
    }

    public int getITextAlign() {
        return iTextAlign;
    }

    public int getITabIndex() {
        return iTabIndex;
    }

    public int getIParamIndex() {
        return iParamIndex;
    }

    public int getIMaxLength() {
        return iMaxLength;
    }

    public int getILocalY() {
        return iLocalY;
    }

    public int getILocalX() {
        return iLocalX;
    }

    public int getILength() {
        return iLength;
    }

    public int getIIntLen() {
        return iIntLen;
    }

    public int getIHeight() {
        return iHeight;
    }

    public int getIFormat() {
        return iFormat;
    }

    public int getIDecLen() {
        return iDecLen;
    }

    public int getICtlType() {
        return iCtlType;
    }

    public boolean isBIsReadOnly() {
        return bIsReadOnly;
    }

    public boolean isBIsNumeric() {
        return bIsNumeric;
    }

    public boolean isBIsMust() {
        return bIsMust;
    }

    public boolean isBIsAll() {
        return bIsAll;
    }

    public void setBChecked(boolean bChecked) {
        this.bChecked = bChecked;
    }

    public void setSURL(String sURL) {
        this.sURL = sURL;
    }

    public void setSShowText(String sShowText) {
        this.sShowText = sShowText;
    }

    public void setSShowAttrName(String sShowAttrName) {
        this.sShowAttrName = sShowAttrName;
    }

    public void setSShowAttrCode(String sShowAttrCode) {
        this.sShowAttrCode = sShowAttrCode;
    }

    public void setSSelScript(String sSelScript) {
        this.sSelScript = sSelScript;
    }

    public void setSFrmClassName(String sFrmClassName) {
        this.sFrmClassName = sFrmClassName;
    }

    public void setSFormatStr(String sFormatStr) {
        this.sFormatStr = sFormatStr;
    }

    public void setSDllName(String sDllName) {
        this.sDllName = sDllName;
    }

    public void setSDataSource(String sDataSource) {
        this.sDataSource = sDataSource;
    }

    public void setSCtlName(String sCtlName) {
        this.sCtlName = sCtlName;
    }

    public void setSCtlGrpCode(String sCtlGrpCode) {
        this.sCtlGrpCode = sCtlGrpCode;
    }

    public void setSCtlCode(String sCtlCode) {
        this.sCtlCode = sCtlCode;
    }

    public void setSCtlCaption(String sCtlCaption) {
        this.sCtlCaption = sCtlCaption;
    }

    public void setSClassName(String sClassName) {
        this.sClassName = sClassName;
    }

    public void setSBtnType(String sBtnType) {
        this.sBtnType = sBtnType;
    }

    public void setIWidth(int iWidth) {
        this.iWidth = iWidth;
    }

    public void setITextAlign(int iTextAlign) {
        this.iTextAlign = iTextAlign;
    }

    public void setITabIndex(int iTabIndex) {
        this.iTabIndex = iTabIndex;
    }

    public void setIParamIndex(int iParamIndex) {
        this.iParamIndex = iParamIndex;
    }

    public void setIMaxLength(int iMaxLength) {
        this.iMaxLength = iMaxLength;
    }

    public void setILocalY(int iLocalY) {
        this.iLocalY = iLocalY;
    }

    public void setILocalX(int iLocalX) {
        this.iLocalX = iLocalX;
    }

    public void setILength(int iLength) {
        this.iLength = iLength;
    }

    public void setIIntLen(int iIntLen) {
        this.iIntLen = iIntLen;
    }

    public void setIHeight(int iHeight) {
        this.iHeight = iHeight;
    }

    public void setIFormat(int iFormat) {
        this.iFormat = iFormat;
    }

    public void setIDecLen(int iDecLen) {
        this.iDecLen = iDecLen;
    }

    public void setICtlType(int iCtlType) {
        this.iCtlType = iCtlType;
    }

    public void setBIsReadOnly(boolean bIsReadOnly) {
        this.bIsReadOnly = bIsReadOnly;
    }

    public void setBIsNumeric(boolean bIsNumeric) {
        this.bIsNumeric = bIsNumeric;
    }

    public void setBIsMust(boolean bIsMust) {
        this.bIsMust = bIsMust;
    }

    public void setBIsAll(boolean bIsAll) {
        this.bIsAll = bIsAll;
    }

    public void setSCtlInd(String sCtlInd) {
        this.sCtlInd = sCtlInd;
    }

    public void setSDefault(String sDefault) {
        this.sDefault = sDefault;
    }

    public boolean isBChecked() {
        return bChecked;
    }

    public String getSCtlInd() {
        return sCtlInd;
    }

    public String getSDefault() {
        return sDefault;
    }
}
