package com.yss.main.operdeal.platform.pfoper.pubpara.innerparams;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: 获取国内业务通用参数的基础工具类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author sj
 * @version 1.0
 */
public abstract class BaseInnerPubParamsDeal
    extends BaseBean {
    private HashMap allPubParamsMap = null;

    public BaseInnerPubParamsDeal() {
    }

    public HashMap getAllPubParamsMap() {
        return allPubParamsMap;
    }

    /**
     * 当有其他函数已经调用获取参数的方法，并且在哈希Map中存值时调用
     * @param allPubParamsMap HashMap
     */
    public void setAllPubParamsMap(HashMap allPubParamsMap) {
        this.allPubParamsMap = allPubParamsMap;
    }

    /**
     * 通过组合代码和通用参数名称(key)来获取之前获取的参数值
     * @param portCode String
     * @param key String
     * @return Object
     * @throws YssException
     */
    public Object getResultWithPortAndKey(String portCode, String key) throws
        YssException {
        HashMap singlePortPubParamMap = null;
        Object resultObj = null;
        if (null != allPubParamsMap) {
            singlePortPubParamMap = (HashMap) allPubParamsMap.get(portCode);
            if (null == singlePortPubParamMap) {
                throw new YssException("获取组合级别通用参数出现异常！");
            }
            resultObj = singlePortPubParamMap.get(key);
        }
        return resultObj;
    }

    /**
     * 获取涉及到的组合的通用参数设置
     * @param portCodes String
     * @return HashMap
     * @throws YssException
     */
    public HashMap getAllPubParams(String portCodes) throws YssException {
        HashMap pubParams = new HashMap(); //放置所有通用参数设置的容器，以不同组合为分类，其中又包含容器
        String portCode = null;
        if (null != this.allPubParamsMap) { //当通用参数容器已经有值
            pubParams = this.allPubParamsMap; //则将其赋其值
        } else {
            this.allPubParamsMap = new HashMap();
            pubParams = this.allPubParamsMap;
        }
        HashMap subParams = null; //各不同组合的容器
        String[] ports = portCodes.split(","); //分割组合
        for (int port = 0; port < ports.length; port++) { //循环组合
            subParams = getPubParas(ports[port]); //获取各组合的通用参数设置
            if (ports[port].startsWith("'") && ports[port].endsWith("'")) { //当有'001'样式的组合时，格式化
                portCode = ports[port];
                portCode = portCode.replaceAll("'", "");
            } else {
                portCode = ports[port];
            }
            pubParams.put(portCode, subParams); //以组合为key，放置个组合的通用参数设置容器
        }
        return pubParams; //返回容器
    }

    /**
     * 获取国内组合级别的通用参数设置，现参数只有回购的通用参数设置。
     * @param portCode String 组合代码
     * @return HashMap 此组合的参数设置
     * @throws YssException
     */
    private HashMap getPubParas(String portCode) throws YssException {
        HashMap subParams = null; //放置通用参数内容的容器
        if (portCode.startsWith("'") && portCode.endsWith("'")) { //当有'001'样式的组合时，格式化
            portCode = portCode.replaceAll("'", "");
        }
        subParams = new HashMap(); //新建容器
        getInnerPubParams(subParams, portCode); //调用获取通用参数信息的方法，将参数设置放入先前生成的容器中。
        return subParams; //返回容器
    }

    public abstract void getInnerPubParams(HashMap subParams, String portCode) throws
        YssException;

    /**
     * 转换值类型，将从通用参数中获取的值转换成Boolean值
     * @param fromValue String 通用参数的值
     * @return Boolean
     */
    public Boolean convertToBoolean(String fromValue) {
        Boolean toValue = null;
        if ("Yes".equalsIgnoreCase(fromValue)) { //当获取的值为'Yes'，时将其转换为Boolean值true
            toValue = new Boolean(true);
            return toValue;
        } else if ("No".equalsIgnoreCase(fromValue)) { //当获取的值为'No'，时将其转换为Boolean值false
            toValue = new Boolean(false);
            return toValue;
        }
        if ("True".equalsIgnoreCase(fromValue)) { //当获取的值为'True'，时将其转换为Boolean值true
            toValue = new Boolean(true);
            return toValue;
        } else if ("False".equalsIgnoreCase(fromValue)) { ////当获取的值为'False'，时将其转换为Boolean值false
            toValue = new Boolean(false);
            return toValue;
        }
        if ("Head".equalsIgnoreCase(fromValue)) { //当为head（计头不计尾）
            toValue = new Boolean(true);
        } else if ("Trail".equalsIgnoreCase(fromValue)) { //当为Trail（计尾不计头）
            toValue = new Boolean(false);
        }
        if ("1".equalsIgnoreCase(fromValue)) {
            toValue = new Boolean(true);
        } else if ("0".equalsIgnoreCase(fromValue)) {
            toValue = new Boolean(false);
        }
        return toValue;
    }

    /**
     * 通过sKey，将容器中的Boolean值转换为基本类型bool值
     * @param singlePortPubParamMap HashMap 通用参数容器
     * @param sKey String 键值
     * @return boolean
     */
    public boolean convertToBool(HashMap singlePortPubParamMap, String sKey) {
        boolean bool = false;
        bool = ( (Boolean) singlePortPubParamMap.get(sKey)).booleanValue(); //将封装类转换成基础类型的boolean
        return bool;
    }

    /**
     * 判断是否在记录集中存在传入的字段
     * @param rs ResultSet
     * @param columnName String 对比的字段名
     * @return boolean
     * @throws YssException
     */
    public boolean judgeExistField(ResultSet rs, String columnName) throws YssException {
        boolean existField = false;
        ResultSetMetaData metaData = null;
        try {
            metaData = rs.getMetaData();
            for (int field = 1; field <= metaData.getColumnCount(); field++) {
                if (columnName.equalsIgnoreCase(metaData.getColumnName(field))) {
                    existField = true;
                    return existField;
                }
            }
        } catch (Exception e) {
            throw new YssException("获取原数据出现异常！", e);
        }
        return existField;
    }
}
