package com.yss.main.parasetting.pojo;

import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * add by wangzuochun 2009.06.04 MS00002
 * QDV4赢时胜（上海）2009年4月20日02_A
 * <p>Title:OperPortfolioBean </p>
 *
 * <p>Description:操作组合设置的实体类 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 */
public class OperPortfolioBean
    extends BaseDataSettingBean {
    public OperPortfolioBean() {
    }

    private String operPortCode = "";   //操作组合代码
    private String operPortName = "";   //操作组合名称
    private String desc = "";           //操作组合描述
    private String portCode = "";       //组合代码
    private String portName = "";       //组合名称
    private int portType;               //区分组合类型
    private String oldPortCode;         //修改用原组合代码
    private String oldOperPortCode;     //修改用原操作组合代码
    private int oldPortType;            //修改用原区分组合类型
    private String parentCode = "";     //用于创建树形结构的父菜单编号
    private String orderCode = "";      //用于创建树形结构的排序编号
    private String nodeCode = "";       //用于创建树形结构的节点代码
    private String nodeName = "";       //用于创建树形结构的节点名称
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    private OperPortfolioBean filterType;   //筛选器
    
    private ArrayList alist = new ArrayList();
    private boolean bETF = false;//判断是否只加载资产类型和资产子类型为ETF基金的组合 panjunfang add 20091010， MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A

    public String getDesc() {
        return desc;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getOperPortCode() {
        return operPortCode;
    }

    public String getOperPortName() {
        return operPortName;
    }

    public String getPortCode() {
        return portCode;
    }

    public int getPortType() {
        return portType;
    }

    public String getOldOperPortCode() {
        return oldOperPortCode;
    }

    public int getOldPortType() {
        return oldPortType;
    }

    public OperPortfolioBean getFilterType() {
        return filterType;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getPortName() {
        return portName;
    }

    public ArrayList getAlist() {
        return alist;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public boolean isBETF() {
        return bETF;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setOperPortCode(String operPortCode) {
        this.operPortCode = operPortCode;
    }

    public void setOperPortName(String operPortName) {
        this.operPortName = operPortName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPortType(int portType) {
        this.portType = portType;
    }

    public void setOldOperPortCode(String oldOperPortCode) {
        this.oldOperPortCode = oldOperPortCode;
    }

    public void setOldPortType(int oldPortType) {
        this.oldPortType = oldPortType;
    }

    public void setFilterType(OperPortfolioBean filterType) {
        this.filterType = filterType;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public void setBETF(boolean bETF) {
        this.bETF = bETF;
    }

    public void setAl(ArrayList alist) {
        this.alist = alist;
    }

    /**
     * 解析前台发送来的操作组合设置请求
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String operAry[] = null;
        String sTmpStr = "";
        try {
            //如果请求数据为“” ，直接返回，不做任何操作
            if (sRowStr.equals("")) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.equals("ETF")) {
                this.bETF = true;
                return;
            }
            //如果有\r\t代表有筛选条件，只有下标为0的才是真正的数据，下标为1的为筛选数据
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            //因为传入的数据时操作组合+组合信息，一个操作组合可能包含多个组合，因此要拆分成多条记录
            if (sTmpStr.indexOf("\f\f") >= 0) {
                operAry = sTmpStr.split("\f\f");
            }
            //多条回收站数据的解析
            if (sTmpStr.indexOf("\f\f\r\n") >= 0) {
                operAry = sTmpStr.split("\f\f\r\n");
            }

            //将数据进行解析封装
            for (int i = 0; i < operAry.length; i++) {
                OperPortfolioBean bean = new OperPortfolioBean();
                bean.setYssPub(pub);
                reqAry = operAry[i].split("\t");
                bean.operPortCode = reqAry[0];
                bean.operPortName = reqAry[1];
                bean.desc = reqAry[2];
                bean.portCode = reqAry[3];
                bean.portType = Integer.parseInt(reqAry[4]);
                bean.checkStateId = Integer.parseInt(reqAry[5]);
                bean.oldOperPortCode = reqAry[6];
                bean.oldPortCode = reqAry[7];
                bean.oldPortType = Integer.parseInt(reqAry[8]);
                if(reqAry.length == 10)				//add by huangqirong 2012-08-07 story #2831 增加识别组合群
                	bean.assetGroupCode = reqAry[9]; //add by huangqirong 2012-08-07 story #2831 增加识别组合群
                bean.parseRecLog();
                alist.add(bean);
            }
            //筛选器的数据解析封装
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new OperPortfolioBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析操作组合设置请求出错!", e);
        }
    }

    /**
     * 通过拼接字符串来获取数据字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(operPortCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(operPortName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(desc).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(portCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(portName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(portType).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.assetGroupCode).append(YssCons.YSS_ITEMSPLITMARK1);//add by huangqirong 2012-08-07 story #2831 增加识别组合群
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 将数据组装成树状结构的字符串
     * @return String
     * @throws YssException
     */
    public String buildTreeStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(nodeCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(nodeName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(orderCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(operPortCode).append(YssCons.YSS_ITEMSPLITMARK1); ;
        buf.append(operPortName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(portCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(portName + "").append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(portType).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(assetGroupCode).append(YssCons.YSS_LINESPLITMARK);

        return buf.toString();
    }
}
