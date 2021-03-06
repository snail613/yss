package com.yss.main.platform.expinfadjust.pojo;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * 指数信息调整
 * “目前纳斯达克指数100”，加载的数据为有持仓且证券品种为股票，子品种为指数股票的证券。
 * “最新纳斯达克指数100”，加载的数据为从外部读进来的数据
 * 20090908,MS00473,国泰需根据最新的纳斯达克指数100信息来调整即将发行的LOF基金中的股票信息,QDV4国泰2009年6月01日01_A
 * <p>Title: 指数信息调整 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author xuxuming 20090904
 * @version 1.0
 */
public class ExpInfoBean
    extends BaseDataSettingBean {
    private String sSecurityCode = "";  //证券代码
    private String sSecurityName = "";  //证券名称
    private String sAttrClsCode = "";   //所属分类,由此区别 指数型、积极型股票
    private String sDate = "";          //日期,对应证券库存、证券应收应付库存 的 库存日期
    private String sIsinCode = "";      //Insi 代码
    private String strPortCode = "";//组合代码  add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
    private String strExpType = "";//指数类型标识  add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111

    public String getSDate() {
        return sDate;
    }

    public String getSSecurityCode() {
        return sSecurityCode;
    }

    public String getSSecurityName() {
        return sSecurityName;
    }

    public String getSAttrClsCode() {
        return sAttrClsCode;
    }

    public void setSIsinCode(String sIsinCode) {
        this.sIsinCode = sIsinCode;
    }

    public void setSDate(String sDate) {
        this.sDate = sDate;
    }

    public void setSSecurityCode(String sSecurityCode) {
        this.sSecurityCode = sSecurityCode;
    }

    public void setSSecurityName(String sSecurityName) {
        this.sSecurityName = sSecurityName;
    }

    public void setSAttrClsCode(String sAttrClsCode) {
        this.sAttrClsCode = sAttrClsCode;
    }

    public String getSIsinCode() {
        return sIsinCode;
    }

    public String getStrPortCode() {
		return strPortCode;
	}

	public void setStrPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}

	public String getStrExpType() {
		return strExpType;
	}

	public void setStrExpType(String strExpType) {
		this.strExpType = strExpType;
	}

	public ExpInfoBean() {
    }

    /**
     * 解析,从字符串中获取字段
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split(YssCons.YSS_ITEMSPLITMARK1);
            this.sSecurityCode = reqAry[0];
            this.sSecurityName = reqAry[1];
            this.sAttrClsCode = reqAry[2];
            this.sDate = YssFun.formatDate(reqAry[3], "yyyy-MM-dd");
            this.sIsinCode = reqAry[4];
            this.strPortCode = reqAry[5];//add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
            this.strExpType = reqAry[6];//add by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
        } catch (Exception e) {
            //throw new YssException("解析纳斯达克指数100数据出错！", e);
            throw new YssException("解析指数信息数据出错！", e);//edit by qiuxufeng 139 QDV4赢时胜（深圳）2010年10月26日01_A 20101111
        }
    }

    /**
     * 从记录集中获取字段值,设置到对应的属性中
     * @param rs ResultSet
     * @throws YssException
     */
    public void setValues(ResultSet rs) throws YssException {
        try {
            this.sSecurityCode = rs.getString("FSecurityCode");
            this.sSecurityName = rs.getString("FSecurityName");
            this.sAttrClsCode = rs.getString("FAttrClsCode");
            this.sDate = YssFun.formatDate(rs.getDate("FStorageDate"),"yyyy-MM-dd");
            this.sIsinCode = rs.getString("FISINCode");
        } catch (SQLException sex) {
            throw new YssException("设置属性值出错！",sex);
        }
    }

    /**
     * 20090908,MS00473,国泰需根据最新的纳斯达克指数100信息来调整即将发行的LOF基金中的股票信息,QDV4国泰2009年6月01日01_A
     * 将字段值合成字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sSecurityCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.sSecurityName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.sAttrClsCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.sDate).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.sIsinCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.strPortCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.strExpType).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append("null");

        return buf.toString();
    }

}
