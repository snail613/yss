package com.yss.main.operdeal.datainterface.cnstock.pojo;

import com.yss.dsub.*;
import com.yss.util.*;


/**
 * QDV4.1赢时胜（上海）2009年4月20日03_A
 * MS00003
 * 包含券商和席位的TreeView实体类
 * create by javachaos
 * 2009-06-17
 */
public class SeatPortfolioBean extends BaseDataSettingBean{
   public SeatPortfolioBean() {
   }

   public String getBrokerCode() {
      return brokerCode;
   }

   public String getBrokerName() {
      return brokerName;
   }

   public int getNodeType() {
      return nodeType;
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

   public String getPortName() {
      return portName;
   }

   public String getSeatCode() {
      return seatCode;
   }

   public String getSeatName() {
      return seatName;
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

   public void setBrokerCode(String brokerCode) {
      this.brokerCode = brokerCode;
   }

   public void setBrokerName(String brokerName) {
      this.brokerName = brokerName;
   }

   public void setNodeType(int nodeType) {
      this.nodeType = nodeType;
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

   public void setPortName(String portName) {
      this.portName = portName;
   }

   public void setSeatCode(String seatCode) {
      this.seatCode = seatCode;
   }

   public void setSeatName(String seatName) {
      this.seatName = seatName;
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

   private String operPortCode = ""; //操作组合代码
    private String operPortName = ""; //操作组合名称
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String brokerCode = "";//券商代码
    private String brokerName = "";//券商名称
    private String seatCode = "";//席位代码
    private String seatName = "";//席位名称
    private int nodeType; //节点类型（操作组合 - 0、组合 - 1、券商 - 2、席位 - 3）
    private String parentCode = "";//用于创建树形结构的父菜单编号
    private String orderCode = "";//用于创建树形结构的排序编号
    private String nodeCode = "";//用于创建树形结构的节点代码
    private String nodeName = "";//用于创建树形结构的节点名称
    /**
     * 组装数据
     * @return String
     * @throws YssException
     */
    public String buildTreeStr() throws YssException{
	StringBuffer buf = new StringBuffer();
	buf.append(nodeCode).append(YssCons.YSS_ITEMSPLITMARK1);//用于创建树形结构的节点代码
	buf.append(nodeName).append(YssCons.YSS_ITEMSPLITMARK1);//用于创建树形结构的节点名称
	buf.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);//父节点菜单
	buf.append(orderCode).append(YssCons.YSS_ITEMSPLITMARK1);//排序编号
	buf.append(operPortCode).append(YssCons.YSS_ITEMSPLITMARK1);//操作组合代码
	buf.append(operPortName).append(YssCons.YSS_ITEMSPLITMARK1);//操作组合名称
	buf.append(portCode).append(YssCons.YSS_ITEMSPLITMARK1);//组合代码
	buf.append(portName).append(YssCons.YSS_ITEMSPLITMARK1);//组合名称
	buf.append(brokerCode).append(YssCons.YSS_ITEMSPLITMARK1); //券商代码
	buf.append(brokerName).append(YssCons.YSS_ITEMSPLITMARK1);//券商名称
	buf.append(seatCode).append(YssCons.YSS_ITEMSPLITMARK1);//席位代码
	buf.append(seatName + "").append(YssCons.YSS_ITEMSPLITMARK1);//席位名称
	buf.append(nodeType).append(YssCons.YSS_LINESPLITMARK);//节点类型
	return buf.toString();
     }

}
