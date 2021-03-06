package com.yss.main.operdeal.platform.pfoper.inoutcfg;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import com.yss.main.dao.*;
import com.yss.main.operdeal.platform.pfoper.inoutcfg.pojo.*;
import com.yss.main.platform.pfsystem.inoutcfg.*;
import com.yss.util.*;

public class ExportCfgDataOper
    extends BaseInOutCfgDeal {

    public ExportCfgDataOper() {
    }

    /**
     * 返回导出的数据
     * @param sRequestStr 前台list传过来的数据
     * @return String
     * @throws YssException
     */
    public String exportData(String sRequestStr) throws YssException {
        String sResult = "";
        Hashtable htOutParam = null;
        InOutCfgParamBean inOutCfgPOJO = null;
        Object obj = null;
        IDataSetting bean = null;
        try {
            if (inOutCfgPOJO == null) {
                inOutCfgPOJO = setInOutCfgArr();
            }
            obj = inOutCfgPOJO.getBean();
            bean = (IDataSetting) obj;
            htOutParam = inOutCfgPOJO.getHtOutParam();
            sResult = disposeData(sRequestStr, bean, htOutParam);
        } catch (Exception ex) {
            throw new YssException("导出数据处理失败", ex);
        }
        return sResult;
    }

    /**
     * 按主表来处理数据
     * sRequestStr 前台传过来的记录 bean 当前用到的 javaBean文件 htOutParam 配置脚本
     */
    private String disposeData(String sRequestStr, IDataSetting bean, Hashtable htOutParam) throws YssException {
        try {
            String sSqlStr = "";            //查询SQL
            String sDataSourceValue = "";   //值
            String sKey = "";               //关键字
            String sTabName = "";           //表名
            StringBuffer bufResult = new StringBuffer();
            String[] arrOneData = sRequestStr.split("\r\n\r\n");
            OutSourceParamBean outParamBean = null;
            Class cl = bean.getClass();
            Field[] fAttr = cl.getDeclaredFields();
            bean.setYssPub(pub);
            for (int iP = 0; iP < htOutParam.size(); iP++) {
                sKey = "[source" + (iP + 1) + "]";
                if (htOutParam.get(sKey) != null) {
                    outParamBean = (OutSourceParamBean) htOutParam.get(sKey);
                    for (int iRow = 0; iRow < arrOneData.length; iRow++) {
                    	//modify  by zhangfa 20101018 MS01742    接口自定义配置中包含分隔符~，业务平台导出时会报错    QDV4华夏2010年09月14日01_B
                    	arrOneData[iRow] = replaceAll(arrOneData[iRow],"out");//添加对字符的转换 modify  by wangzuochun 2010.04.16 MS01081    系统增加通过通用导入导出来导词汇、菜单条、功能调用、权限等功能    QDV4赢时胜上海2010年03月12日01_AB
                    	//---------------------------------------------------------------------------------------------------------------
                        bean.parseRowStr(arrOneData[iRow]);
                        sSqlStr = getBeanAttr(outParamBean, bean, fAttr);
                        sDataSourceValue = outParamBean.getSTmpData() +
                            buildDataSource(sSqlStr);
                        outParamBean.setSTmpData(sDataSourceValue);
                    }
                    bufResult.append(outParamBean.getSTmpTabName()).append("\f\f\f");
                    bufResult.append(buildTabParam(sSqlStr)).append("\f\f\f");
                    bufResult.append(outParamBean.getSTmpData()).append("\f\f\r\f\f");
                }
            }
            sTabName = "tb_pfsys_inoutcfg"; //另要加上配置参数数据
            sSqlStr = "select * from Tb_PFSys_inOutCfg where FInOutCode =" + dbl.sqlString(this.sInOutCodes);
            sDataSourceValue = buildDataSource(sSqlStr);
            bufResult.append(sTabName).append("\f\f\f");
            bufResult.append(buildTabParam(sSqlStr)).append("\f\f\f");
            bufResult.append(sDataSourceValue);
            return bufResult.toString();
        } catch (Exception ex) {
            throw new YssException("处理数据出错", ex);
        }
    }

    //加载listview中的数据
    public String loadListView() throws YssException {
        String sParam = "";
        String sXML_File = "";
        String sResult = "";
        InOutCfgParamBean inOutCfgPOJO = null;
        try {
            if (inOutCfgPOJO == null) {
                inOutCfgPOJO = setInOutCfgArr();
            }
            sXML_File = inOutCfgPOJO.getSModule();
            sParam = inOutCfgPOJO.getRevokeURL();
            if (sParam.length() == 0) {
                throw new YssException("系统找不到关联的功能调用代码,请设置相关联的参数");
            }
            inOutCfgPOJO.parseResRowStr(sParam);
            sResult = getData(inOutCfgPOJO, sXML_File, inOutCfgPOJO.getSRev_BeanName());
            sResult = sParam + "\f\n\r" + sResult;
            //将获取的URL也传到前台去,这样初次加载时可以减少一次向后台的访问
        } catch (Exception ex) {
            throw new YssException("获取列表值出错", ex);
        }
        return sResult;
    }

    /**
     * 根据SQL及字段来加载数据
     * @param sql 查询语句
     * @param sTabParam 表的相关信息,如类型,长度等
     * 返回 查询结果
     * @throws YssException
     */
    private String buildDataSource(String sql) throws YssException {
        String[] arrParam = null;
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        String strFieldValue=""; //QDV4华夏2009年7月28日02_B MS00602 by leeyu 20090813
        try {
            rs = dbl.openResultSet(sql);
            arrParam = dbFun.getFieldsParam(rs);
            while (rs.next()) {
                for (int iF = 0; iF < arrParam.length; iF++) {
                    // 导出时判断一下，防止有为空的字段值，这里将为空的字段值转为""而不是空格或其他的值，目的是做到与原表值保持一致 QDV4华夏2009年7月28日02_B MS00602 by leeyu 20090813
                    strFieldValue="";
                    if (arrParam[iF].split(",")[1].equalsIgnoreCase("clob")) {
                        strFieldValue =dbl.clobStrValue(rs.getClob(arrParam[iF].split(",")[0]));
                    // ------------xuqiji 20100326 MS00940 赢时胜(测试)2010年3月25日5_B 配置参数通用导入界面导入调拨类型的文件时报错--//
                    }else if(arrParam[iF].split(",")[1].equalsIgnoreCase("date")){
                    	strFieldValue =YssFun.formatDate(rs.getDate(arrParam[iF].split(",")[0]),"yyyy-MM-dd");
                    }else {
                    //------------------------------------end----------------------------//
                    	//modify  by zhangfa 20101018 MS01742    接口自定义配置中包含分隔符~，业务平台导出时会报错    QDV4华夏2010年09月14日01_B 
                    	if(rs.getString(arrParam[iF].split(",")[0])!=null){
                    		strFieldValue =replaceAll(rs.getString(arrParam[iF].split(",")[0]),"out");
                    	}else{
                    		strFieldValue =rs.getString(arrParam[iF].split(",")[0]);
                    	}
                        
                        //--------------------MS01742-------------------------------------------------------------------------------------
                    }
                    if(strFieldValue==null)
                        strFieldValue="";//如果字段的值为空，则当前字段值就为"",不能为其他
                    buf.append(strFieldValue);
                    //QDV4华夏2009年7月28日02_B MS00602 by leeyu 20090813
                    buf.append("~");
                }
                buf.append("@~@");
            }
            return buf.toString();
        } catch (Exception ex) {
            throw new YssException("执行SQL来查询数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //根据主表的信息取值
    private String getData(InOutCfgParamBean inOutCfgPOJO, String xmlName, String beanId) throws YssException {
        Object obj = null;
        IDataSetting bean = null;
        String sRes = "";
        try {
            inOutCfgPOJO.setModuleName(xmlName);
            inOutCfgPOJO.setSBeanId(beanId);
            obj = inOutCfgPOJO.getBean();
            if (obj instanceof IDataSetting) {
                bean = (IDataSetting) obj;
                bean.setYssPub(pub);
                if (inOutCfgPOJO.getSRev_ShowList().equalsIgnoreCase("listview1")) {
                    sRes = bean.getListViewData1();
                } else if (inOutCfgPOJO.getSRev_ShowList().equalsIgnoreCase("listview2")) {
                    sRes = bean.getListViewData2();
                } else if (inOutCfgPOJO.getSRev_ShowList().equalsIgnoreCase("listview3")) {
                    sRes = bean.getListViewData3();
                } else if (inOutCfgPOJO.getSRev_ShowList().equalsIgnoreCase("listview4")) {
                    sRes = bean.getListViewData4();
                } else if (inOutCfgPOJO.getSRev_ShowList().equalsIgnoreCase("treeview1")) {
                    sRes = bean.getTreeViewData1();
                } else if (inOutCfgPOJO.getSRev_ShowList().equalsIgnoreCase("treeview2")) {
                    sRes = bean.getTreeViewData2();
                } else if (inOutCfgPOJO.getSRev_ShowList().equalsIgnoreCase("treeview3")) {
                    sRes = bean.getTreeViewData3();
                } else {
                    throw new YssException("无实现方法获取,获取数据出错");
                }
            }
        } catch (Exception e) {
            throw new YssException("获取数据出错", e);
        }
        return sRes;
    }

    //根据查询出的结果往ArrayList中赋值
    private InOutCfgParamBean setInOutCfgArr() throws YssException {
        InOutCfgBean inOutCfg = new InOutCfgBean();
        inOutCfg.setYssPub(pub);
        InOutCfgParamBean inOutPOJO = null;
        try {
            inOutCfg.setStrInOutCode(sInOutCodes);
            inOutCfg.getSetting();
            inOutPOJO = new InOutCfgParamBean();
            inOutPOJO.setYssPub(pub);
            inOutPOJO.parseOutRowStr(inOutCfg.getStrOutCfgScript());
            return inOutPOJO;
        } catch (Exception e) {
            throw new YssException("取配置信息出错", e);
        }
    }

    /**
     * 将SQL中的值关键字换成具体的值
     * @param outSource 配置信息
     * @param bean JavaBean
     * @param fAttr JavaBean中的属性列表
     * @return String
     * @throws YssException
     */
    private String getBeanAttr(OutSourceParamBean outSource, IDataSetting bean, Field[] fAttr) throws YssException {
        String sTmpStr = "";
        String sKeyVaue = "";
        try {
            sTmpStr = outSource.getSSqlSource().toLowerCase();
            for (int iAttr = 0; iAttr < fAttr.length; iAttr++) {
                fAttr[iAttr].setAccessible(true);
                sKeyVaue = fAttr[iAttr].getName().toLowerCase();
                if (sTmpStr.indexOf("s<" + sKeyVaue + ">") > 0 || sTmpStr.indexOf("s <" + sKeyVaue + ">") > 0) { //字符串型
                    sTmpStr = sTmpStr.replaceAll("s<" + sKeyVaue + ">", dbl.sqlString(fAttr[iAttr].get(bean).toString()));
                    sTmpStr = sTmpStr.replaceAll("s <" + sKeyVaue + ">", dbl.sqlString(fAttr[iAttr].get(bean).toString()));
                } else if (sTmpStr.indexOf("d<" + sKeyVaue + ">") > 0 || sTmpStr.indexOf("d <" + sKeyVaue + ">") > 0) { //日期型
                    sTmpStr = sTmpStr.replaceAll("d<" + sKeyVaue + ">", dbl.sqlDate(fAttr[iAttr].get(bean).toString()));
                    sTmpStr = sTmpStr.replaceAll("d <" + sKeyVaue + ">", dbl.sqlDate(fAttr[iAttr].get(bean).toString()));
                } else if (sTmpStr.indexOf("n<" + sKeyVaue + ">") > 0 || sTmpStr.indexOf("n <" + sKeyVaue + ">") > 0) { //数值型
                    sTmpStr = sTmpStr.replaceAll("d<" + sKeyVaue + ">", fAttr[iAttr].get(bean).toString());
                    sTmpStr = sTmpStr.replaceAll("d <" + sKeyVaue + ">", fAttr[iAttr].get(bean).toString());
                }
                fAttr[iAttr].setAccessible(false);
            }
            sTmpStr = pretExpress(sTmpStr);
        } catch (Exception ex) {
            throw new YssException("编辑SQL出错", ex);
        }
        return sTmpStr;
    }

}
