package com.yss.main.operdeal.datainterface.swift;

import com.yss.util.*;
import com.yss.main.operdeal.datainterface.*;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.datainterface.*;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * QDV4赢时胜（深圳）2009年5月12日01_A MS00455
 * by leeyu 20090608
 * <p>Title: 导入报文的基类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ysstech</p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class BaseSwiftInputOper
    extends BaseSwiftOper {
//    protected java.util.Date startDate; //起始日期
//    protected java.util.Date endDate; //结束日期
//    protected String portCode = ""; //组合代码

    protected HashMap hmTmpField; //保存临时表及数据
    protected ArrayList alSavelist = null; //用于存放保存的报文文件信息的
    public BaseSwiftInputOper() {
    }

    public void setStartDate(java.util.Date startdate) {
        startDate = startdate;
    }

    public void setEndDate(java.util.Date enddate) {
        endDate = enddate;
    }

    public void setPortCode(String portcode) {
        portCode = portcode;
    }

    /**
     * 查询报文信息
     * @return String
     * @throws YssException
     */
    public abstract String querySWIFTList() throws YssException;

    /**
     * 解析SWIFT并保存到临时表里
     * @return String
     * @throws YssException
     */
    public abstract String parseSaveSWIFT() throws YssException;

    /**
     * 返回一条Header列的报文信息
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(rowData[0]).append("\t");
        buf.append(rowData[1]).append("\t");
        buf.append(rowData[2]).append("\t");
        buf.append(rowData[3]).append("\t");
        buf.append(rowData[4]).append("\t");
        buf.append(rowData[5]).append("\t");
        //buf.append("\r\n");//行分隔符
        return buf.toString();
    }

    /**
     * 初始化
     * @throws YssException
     */
    public void initBean() throws YssException {
        headerCode = "FSwiftNum\tFSwiftType\tFDate\tFStatus\tFExeStatus\tFSwiftDesc";
        headerName = "报文序号\t报文类型\t导入日期\t报文状态\t执行状态\t报文摘要";
        rowData = new Object[6];
        saveSwift = new SaveSwiftContentBean();
        saveSwift.setYssPub(pub);
        baseOperDeal = new ImpCusInterface();
        baseOperDeal.setYssPub(pub);
        baseOperDeal.init(startDate, endDate, portCode, swiftType, "");
    }

    /**
     * 初始前台窗体的方法
     * @return String
     * @throws YssException
     */
    public String initSWIFTListView() throws YssException {
        String sVocStr = "";
        VocabularyBean vocabulary = new VocabularyBean();
        vocabulary.setYssPub(pub);
        sVocStr = vocabulary.getVoc(YssCons.YSS_SWIFT_CRITERION + "," +
                                    YssCons.YSS_SWIFT_OPERTYPE);
        return headerName + "\r\f\r\f\r\f" +
            headerCode + "\r\f" + "voc" + sVocStr;
    }

    /**
     * 根据SWIFT类型返回文件的全名称
     * @param swiftType String
     * @return String
     * @throws YssException
     */
    public String getFileNameStr(String swiftType,String swiftCode) throws YssException {
        return getFilePathName(swiftType, swiftCode);
    }

    /**
     * 这里采用ImpCusInterface类的相关方法获取文件名相关信息
     * @return String
     * @throws YssException
     */
    private String getFilePathName(String swiftType,String swiftCode) throws YssException {
        StringBuffer buf = new StringBuffer();
        ArrayList alFileNameSet = null;
        ImpCusInterface impcus = (ImpCusInterface) baseOperDeal;
        impcus.setYssPub(pub);
        //调用 DaoCusConfigureBean 作为中间过度类,辅助完成相关处理
        DaoCusConfigureBean cuscfg = new DaoCusConfigureBean();
        cuscfg.setCusCfgCode(swiftType);
        //cuscfg.setFileType("out");//已此文件类型结尾的文件
        alFileNameSet = getFileNameSet(swiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        if (alFileNameSet.size() > 0) {
            impcus.recuFileNames(alFileNameSet, 0, "", buf, cuscfg);
        }
        return buf.toString();
    }

    ////by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874 调整，导入时由于无法找到报文代码，只能根据报文状态与报文类型来找数据，要求导入的报文设置必须唯一
    private ArrayList getFileNameSet(String swiftCode) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        ArrayList alFileName = new ArrayList();
        DaoFileNameBean fileName = null;
        try {
            strSql = " select * from " + pub.yssGetTableName("Tb_Dao_FileName") +
                " where FCusCfgCode ='"+swiftCode+"'" +
                " and FCheckState=1 order by FOrderNum";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                fileName = new DaoFileNameBean();
                fileName.setCusCfgCode(swiftType);
                fileName.setFormat(rs.getString("FFormat"));
                fileName.setFileNameConent(rs.getString("FFIleNameConent"));
                fileName.setFileNameCls(rs.getString("FFileNameCls"));
                fileName.setFileNameDictCode(rs.getString("FFileNameDict"));
                alFileName.add(fileName);
            }
            return alFileName;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 删除临时表的数据
     * @param tabName String
     * @throws YssException
     */
    protected void deleteSWIFTTab(String tabName) throws YssException {
        String sqlStr = "";
        try {
            sqlStr = "truncate table " + getYssTableName(tabName);
            dbl.executeSql(sqlStr);
        } catch (Exception ex) {
            throw new YssException("删除表【" + tabName + "】数据出错", ex);
        }
    }

    /**
     *将数据插入临时表
     * @param tabName String
     * @throws YssException
     */
    protected void insertSWIFTTab(String tabName) throws YssException {
        StringBuffer bufField = null; //保存临时表字段
        StringBuffer bufFieldValue = null; //保存临时表字段值
        String sFieldStr = ""; //保存临时表字段
        String sFieldValueStr = ""; //保存临时表字段值
        String sInsertSql = ""; //插入数据的SQL
        String sFieldType = ""; //单个字段类型
        HashMap hmFieldType = null; //字段类型
        Object[] arrField = null; //字段名
        try {
            arrField = hmTmpField.keySet().toArray();
            bufField = new StringBuffer();
            bufFieldValue = new StringBuffer();
            tabName = getYssTableName(tabName);
            hmFieldType = dbFun.getFieldsType(tabName);
            for (int col = 0; col < arrField.length; col++) {
                if (hmFieldType.get(arrField[col]) != null) {
                    sFieldType = (String) hmFieldType.get(arrField[col]);
                    if (sFieldType.indexOf("CHAR") > -1) {
                        bufField.append(arrField[col]).append(",");
                        bufFieldValue.append(dbl.sqlString(String.valueOf(hmTmpField.get(arrField[col])).replaceAll("'", "''"))).append(",");
                    } else if (sFieldType.indexOf("DATE") > -1) {
                        bufField.append(arrField[col]).append(",");
                        bufFieldValue.append(dbl.sqlDate(String.valueOf(hmTmpField.get(arrField[col])))).append(",");
                    } else {
                        bufField.append(arrField[col]).append(",");
                        bufFieldValue.append(String.valueOf(hmTmpField.get(arrField[col]))).append(",");
                    }
                }
            }
            sFieldStr = bufField.toString();
            sFieldValueStr = bufFieldValue.toString();
            if (sFieldStr.endsWith(",")) {
                sFieldStr = sFieldStr.substring(0, sFieldStr.length() - 1);
            }
            if (sFieldValueStr.endsWith(",")) {
                sFieldValueStr = sFieldValueStr.substring(0, sFieldValueStr.length() - 1);
            }
            if (sFieldStr.trim().length() > 0 && sFieldValueStr.trim().length() > 0) {
                sInsertSql = "insert into " + tabName + " (" + sFieldStr +
                    ") values (" + sFieldValueStr + " )";
                dbl.executeSql(sInsertSql);
            }
        } catch (Exception ex) {
            throw new YssException("插入表【" + tabName + "】数据出错", ex);
        }
    }

}
