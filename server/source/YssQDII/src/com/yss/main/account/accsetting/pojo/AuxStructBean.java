package com.yss.main.account.accsetting.pojo;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: 核算结构设置</p>
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
public class AuxStructBean extends BaseDataSettingBean{
    private String setCode = "";        //套账代码
    private String setName = "";        //套账名称

    private String structCode = "";     //结构代码
    private String structName = "";     //结构名称

    private ArrayList nodeList=new ArrayList();            //节点集合

    public AuxStructBean() {
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public void setStructCode(String structCode) {
        this.structCode = structCode;
    }

    public void setStructName(String structName) {
        this.structName = structName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public void setNodeList(ArrayList nodeList) {
        this.nodeList = nodeList;
    }

    public String getSetName() {
        return setName;
    }

    public String getSetCode() {
        return setCode;
    }

    public String getStructCode() {
        return structCode;
    }

    public String getStructName() {
        return structName;
    }

    public ArrayList getNodeList() {
        return nodeList;
    }

    /**
     * 从记录集中获取字段值
     * @param rs ResultSet
     * @throws YssException
     */
    public void setValues(ResultSet rs) throws YssException {
        try {
            this.setCode = rs.getString("FSetCode");
            //this.setName = rs.getString("FSetName");

            this.structCode = rs.getString("FStructCode");
            this.structName = rs.getString("FStructName");

            super.setRecLog(rs);

        } catch (SQLException sex) {
            throw new YssException(sex.toString());
        }
    }

    /**
     * 从字符串中获取字段值
     * @param sRowStr String
     * @throws YssException
     */
    public void parseBasicInfoRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0)
                return;

            reqAry = sRowStr.split("\t");

            this.setCode = reqAry[0];
            this.setName = reqAry[1];

            this.structCode = reqAry[2];
            this.structName = reqAry[3];

        } catch (Exception e) {
            throw new YssException("解析核算结构设置信息出错", e);
        }
    }

    /**
     * 从字符串中获取字段值
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        try {
            String[] arrData = sRowStr.split("\n\f");

            //转换基本核算结构信息
            if (arrData[0].trim().length() != 0) {
                parseBasicInfoRowStr(arrData[0]);
            }

            //转换节点列表信息
            nodeList.clear();
            if (arrData.length >= 2) {
                if (arrData[1].trim().length() != 0) {
                    String[] arrReq = arrData[1].split("\n\t");

                    for (int i = 0; i < arrReq.length; i++) {
                        if (arrReq[i].trim().length() != 0) {
                            AuxStructNodeBean bean = new AuxStructNodeBean();
                            bean.parseRowStr(arrReq[i]);
                            nodeList.add(bean);
                        }
                    }
                }
            }

            super.parseRecLog(); //不能放在parseBasicInfoRowStr方法中，否则转换关联套账时因为关联套账没有Pub而出错

        } catch (Exception e) {
            throw new YssException("解析核算结构设置信息出错", e);
        }
    }

    /**
     * 将字段值合成字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();

        buf.append(buildBasicInfoRowStr()).append("\n\f");

        //生成节点列表信息
        Object[] arrNode = nodeList.toArray();
        for (int i = 0; i < arrNode.length; i++) {
            buf.append( ( (AuxStructNodeBean) arrNode[i]).buildRowStr()).append("\n\t");
        }

        buf.append("\n\fnull");

        return buf.toString();
    }

    /**
     * 只生成基础套账设置信息的字符串，不包含关联套账列表和会计期间列表
     * @return String
     * @throws YssException
     */
    public String buildBasicInfoRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.setCode).append(YssCons.YSS_ITEMSPLITMARK1); ;
        buf.append(this.setName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.structCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.structName).append(YssCons.YSS_ITEMSPLITMARK1);

        buf.append(super.buildRecLog());
        return buf.toString();
    }
}
