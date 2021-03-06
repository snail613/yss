package com.yss.main.operdeal.datainterface.cnstock.pojo;

import com.yss.util.*;
import java.util.Date;

/**
 * QDV4.1赢时胜（上海）2009年4月20日11_A
 * TB_PUB_DAO_ExchangeBond交易所债券参数设置表的实体类
 * MS00011
 * create by songjie
 * 2009-06-18
 */
public class ExchangeBondBean {
    public ExchangeBondBean() {
    }

  //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
//    private String assetGroupCode = null; //组合群代码
    private String portCode = null; //组合代码
    private String market = null; //市场 01-深交所 02-上交所
    private String catCode = null; //品种 01-国债 02-企业债 03-可转债
    private String bondTradeType = null; //债券交易方式 00-净价交易 01-全价交易
    private String commisionType = null; //佣金计算方式 00-按净价计算佣金 01-按净价加利息税计算佣金 02-按全价计算佣金
    private String inteDutyType = null; //利息税计算方式 00-按明细入成本 01-按明细不入成本（进应收利息） 02-汇总入成本
    private java.util.Date startDate = null; //启用日期

    private String assetGroupName; //组合群名称
    private String portName; //组合名称
    private String creator; //创建人
    private String createTime; //创建日期
    private String checkUser; //审核人
    private String checkTime; //审核时间

    /**
     * 组装tag数据
     * @return String
     * @throws YssException
     */
    public String buildLiseStr() throws YssException {
        StringBuffer buf = new StringBuffer();
      //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
//        buf.append(assetGroupCode + " - " +
//                   assetGroupName).append(YssCons.YSS_ITEMSPLITMARK1); //组合群名称
      //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
        buf.append(portCode + "-" + portName).append(YssCons.YSS_ITEMSPLITMARK1); //组合名称
        buf.append("01".equals(market) ? "上交所" :
                   "深交所").append(YssCons.YSS_ITEMSPLITMARK1); //市场
        buf.append(getCatCodek2v(catCode)).append(YssCons.YSS_ITEMSPLITMARK1); //品种
        buf.append("01".equals(bondTradeType) ? "全价交易" :
                   "净价交易").append(YssCons.YSS_ITEMSPLITMARK1); //债券交易方式
        buf.append(getCommisionk2v(commisionType)).append(YssCons.
            YSS_ITEMSPLITMARK1); //佣金计算方式
        buf.append(getInteDutyk2v(inteDutyType)).append(YssCons.YSS_ITEMSPLITMARK1); //利息税计算方式
        buf.append(YssFun.formatDate(startDate)).append(YssCons.YSS_ITEMSPLITMARK1); //启用日期
        buf.append(creator == null ? "" :
                   creator).append(YssCons.YSS_ITEMSPLITMARK1); //创建人
        buf.append(createTime == null ? "" :
                   createTime).append(YssCons.YSS_ITEMSPLITMARK1); //创建日期
        buf.append(checkUser == null ? "" :
                   checkUser).append(YssCons.YSS_ITEMSPLITMARK1); //审核人
        buf.append(checkTime == null ? "" :
                   checkTime).append(YssCons.YSS_LINESPLITMARK); //审核时间
        return buf.toString();
    }

    /**
     * 组装显示数据
     * @return String
     * @throws YssException
     */
    public String buildLiseStr2() throws YssException {
        StringBuffer buf = new StringBuffer();
      //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
//        buf.append(assetGroupCode).append(YssCons.YSS_ITEMSPLITMARK1); //组合群代码
        buf.append(portCode).append(YssCons.YSS_ITEMSPLITMARK1); //组合代码
        buf.append(market).append(YssCons.YSS_ITEMSPLITMARK1); //市场
        buf.append(catCode).append(YssCons.YSS_ITEMSPLITMARK1); //品种
        buf.append(bondTradeType).append(YssCons.YSS_ITEMSPLITMARK1); //债券交易方式
        buf.append(commisionType).append(YssCons.YSS_ITEMSPLITMARK1); //佣金计算方式
        buf.append(inteDutyType).append(YssCons.YSS_ITEMSPLITMARK1); //利息税计算方式
        buf.append(startDate).append(YssCons.YSS_ITEMSPLITMARK1); //启用日期
        buf.append(creator == null ? "" :
                   creator).append(YssCons.YSS_ITEMSPLITMARK1); //创建人
        buf.append(createTime == null ? "" :
                   createTime).append(YssCons.YSS_ITEMSPLITMARK1); //创建日期
        buf.append(checkUser == null ? "" :
                   checkUser).append(YssCons.YSS_ITEMSPLITMARK1); //审核人
        buf.append(checkTime == null ? "" :
                   checkTime).append(YssCons.YSS_LINESPLITMARK); //审核时间
        return buf.toString();
    }

    /**
     * 组装提示信息
     * @return String
     * @throws YssException
     */
    public String buildMsgStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append("【组合群名称：").append(assetGroupName).append("，"); //组合群代码
        buf.append("组合名称：").append(portName).append("，"); //组合代码
        buf.append("交易市场：").append("01".equals(market) ? "上交所" :
                                   "深交所").append("，"); //市场
        buf.append("债券品种：").append(getCatCodek2v(catCode)).append("】\r\n\r\n"); //品种
        return buf.toString();
    }

    /**
     * 利息税计算方式转换成名称
     * @param key String
     * @return String
     */
    public String getInteDutyk2v(String key) {
        if ("00".equals(key)) {
            return "按明细入成本";
        } else if ("01".equals(key)) {
            return "按明细不入成本（进应收利息）";
        } else if ("02".equals(key)) {
            return "汇总入成本";
        } else {
            return "";
        }
    }

    /**
     * 佣金计算方式转换成名称
     * @param key String
     * @return String
     */
    public String getCommisionk2v(String key) {
        if ("00".equals(key)) {
            return "按净价计算佣金";
        } else if ("01".equals(key)) {
            return "按净价加利息税计算佣金";
        } else if ("02".equals(key)) {
            return "按全价计算佣金";
        } else {
            return "";
        }
    }

    /**
     * 产品代码转换成名称
     * @param key String
     * @return String
     */
    public String getCatCodek2v(String key) {
        if ("01".equals(key)) {
            return "国债";
        } else if ("02".equals(key)) {
            return "企业债";
        } else if ("03".equals(key)) {
            return "可转债";
        } else if ("04".equals(key)) {
            return "分离可转债";
        } else if ("05".equals(key)) {
            return "公司债";
        } else if ("06".equals(key)) {
            return "资产证券化产品";
        }
        return "";
    }

  //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
//    public String getAssetGroupCode() {
//        return assetGroupCode;
//    }
  //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public String getBondTradeType() {
        return bondTradeType;
    }

    public String getCatCode() {
        return catCode;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public String getCommisionType() {
        return commisionType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getCreator() {
        return creator;
    }

    public String getInteDutyType() {
        return inteDutyType;
    }

    public String getMarket() {
        return market;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortName() {
        return portName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public void setInteDutyType(String inteDutyType) {
        this.inteDutyType = inteDutyType;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setCommisionType(String commisionType) {
        this.commisionType = commisionType;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public void setBondTradeType(String bondTradeType) {
        this.bondTradeType = bondTradeType;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

  //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
//    public void setAssetGroupCode(String assetGroupCode) {
//        this.assetGroupCode = assetGroupCode;
//    }
  //delete by songjie 2010.03.18 MS00914 QDV4赢时胜（测试）2010年03月17日01_AB
}
