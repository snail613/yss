package com.yss.main.account.accsetting.pojo;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.yss.util.YssCons;

/**
 * <p>Title: 核算结构节点定义</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author CTQ
 * @version 1.0
 */
public class AuxStructNodeBean extends BaseDataSettingBean{
    private String nodeCode="";     //节点代码
    private String nodeName="";     //节点名称

    private String parentCode="";       //上级节点代码
    private String parentName="";       //上级节点名称

    private String auxCode = ""; //核算项目代码
    private String auxName = ""; //核算项目名称

    private String auxDetailCode = ""; //明细项目代码
    private String auxDetailName = ""; //明细项目名称

    private int level; //节点级别
    private boolean detail; //是否明细项目
    private boolean amount; //是否合算数量

    public AuxStructNodeBean() {
    }

    /**
     * 从记录集中获取字段值
     * @param rs ResultSet
     * @throws YssException
     */
    public void setValues(ResultSet rs) throws YssException {
        try {
            this.nodeCode = rs.getString("FNodeCode");
            this.nodeName = rs.getString("FNodeName");

            this.parentCode = rs.getString("FParentCode");
            this.parentName = rs.getString("FParentName");

            this.auxCode = rs.getString("FAuxCode");
            this.auxName = rs.getString("FAuxName");

            this.auxDetailCode = rs.getString("FAuxDetailCode")==null?"":rs.getString("FAuxDetailCode");
            //this.auxDetailName = rs.getString("FAuxDetailName");

            this.level=rs.getInt("FLevel");
            this.amount=rs.getInt("FAmount")==0?false:true;
            this.detail=rs.getInt("FDetail")==0?false:true;

        } catch (SQLException sex) {
            throw new YssException(sex.toString());
        }
    }

    /**
     * 从字符串中获取字段值
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
           if (sRowStr.trim().length() == 0)
              return;

           reqAry = sRowStr.split(YssCons.YSS_ITEMSPLITMARK1);

           this.nodeCode=reqAry[0];
           this.nodeName=reqAry[1];

           this.parentCode=reqAry[2];
           this.parentName=reqAry[3];

           this.auxCode=reqAry[4];
           this.auxName=reqAry[5];

           this.auxDetailCode=reqAry[6];
           this.auxDetailName=reqAry[7];

           this.level=Integer.parseInt(reqAry[8]);
           this.amount=reqAry[9].trim().equalsIgnoreCase("T")?true:false;
           this.detail=reqAry[10].trim().equalsIgnoreCase("T")?true:false;

        }
        catch (Exception e) {
           throw new YssException("解析系统表字典信息出错", e);
        }
    }

    /**
     * 将字段值合成字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException{
       StringBuffer buf =new StringBuffer();
       buf.append(this.nodeCode).append(YssCons.YSS_ITEMSPLITMARK1);
       buf.append(this.nodeName).append(YssCons.YSS_ITEMSPLITMARK1);

       buf.append(this.parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
       buf.append(this.parentName).append(YssCons.YSS_ITEMSPLITMARK1);

       buf.append(this.auxCode).append(YssCons.YSS_ITEMSPLITMARK1);
       buf.append(this.auxName).append(YssCons.YSS_ITEMSPLITMARK1);

       buf.append(this.auxDetailCode).append(YssCons.YSS_ITEMSPLITMARK1);
       buf.append(this.auxDetailName).append(YssCons.YSS_ITEMSPLITMARK1);

       buf.append(this.level).append(YssCons.YSS_ITEMSPLITMARK1);
       buf.append(this.amount?"T":"F").append(YssCons.YSS_ITEMSPLITMARK1);
       buf.append(this.detail?"T":"F").append(YssCons.YSS_ITEMSPLITMARK1);

       buf.append("null");

       return buf.toString();
   }

    public boolean isAmount() {
        return amount;
    }

    public String getAuxDetailCode() {
        return auxDetailCode;
    }

    public String getAuxDetailName() {
        return auxDetailName;
    }

    public String getAuxCode() {
        return auxCode;
    }

    public String getAuxName() {
        return auxName;
    }

    public boolean isDetail() {
        return detail;
    }

    public int getLevel() {
        return level;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public String getParentName() {
        return parentName;
    }

    public void setAmount(boolean amount) {
        this.amount = amount;
    }

    public void setAuxCode(String auxCode) {
        this.auxCode = auxCode;
    }

    public void setAuxDetailCode(String auxDetailCode) {
        this.auxDetailCode = auxDetailCode;
    }

    public void setAuxDetailName(String auxDetailName) {
        this.auxDetailName = auxDetailName;
    }

    public void setAuxName(String auxName) {
        this.auxName = auxName;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setDetail(boolean detail) {
        this.detail = detail;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
