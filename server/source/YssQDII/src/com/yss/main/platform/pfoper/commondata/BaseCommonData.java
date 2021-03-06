package com.yss.main.platform.pfoper.commondata;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: 通用数据处理实现的一个基类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BaseCommonData
    extends BaseBean {
    protected String strAllData = "";
    protected String sPrimayKey = "";
    public BaseCommonData() {
    }

    /**
     * 新增
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        return "";
    }

    /**
     * 修改
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        return "";
    }

    /**
     * 删除数据
     * @param sMutilRowStr String
     * @return String
     */
    public String delSetting() throws YssException {
        return "";
    }

    /**
     * 多条新增
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * 取数据
     * @return String
     * @throws YssException
     */
    public String getListViewData() throws YssException {
        String sRes = "";
        StringBuffer buf = new StringBuffer();
        buf.append("precashcommon").append("\t").append("预估现金表").append("\r\n");
        buf.append("pretacommon").append("\t").append("预估中登TA表").append("\r\n");
        buf.append("exinoutcommon").append("\t").append("汇入汇出临时表").append("\r\n"); // add by fangjiang 2011.03.13 STORY #529 需在特定数据处理界面增加一个临时表的处理
        buf.append("fundstatusseccommon").append("\t").append("资产分类表_证券").append("\r\n"); // add by fangjiang 2011.07.13 STORY #1279
        buf.append("fundstatuscashcommon").append("\t").append("资产分类表_现金").append("\r\n"); // add by fangjiang 2011.07.13 STORY #1279
        sRes = buf.toString();
        return sRes;
    }

    /**
     * 解析
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {

    }

    /**
     * 连接
     * @return String
     */
    protected String buildRowStr() {
        return "";
    }

    /**
     * 功能扩展
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String operValue(String sType) throws YssException {
        return "";
    }

    /**
     * 条件筛选
     * @return String
     */
    protected String fillterSql() {
        return "";
    }

    /**
     * 取表的字段描述信息
     * @return String
     */
    protected HashMap getTableFieldDesc(String sTableName) throws YssException {
        String sqlStr = "";
        HashMap htFields = new HashMap();
        //String sField ="",sFieldDesc="";
        ResultSet rs = null;
        try {
            sqlStr =
                "select FFieldName,FFieldDesc from TB_FUN_DATADICT where FTabName=" +
                dbl.sqlString(sTableName);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                htFields.put(rs.getString("FFieldName").toUpperCase(), rs.getString("FFieldDesc"));
                //sField+=rs.getString("FFieldName")+"\t";
                //sFieldDesc+=rs.getString("FFieldDesc")+"\t";
            }
            //if(sField.endsWith("\t"))
            //sField=sField.substring(0,sField.length()-1);
            //if(sFieldDesc.endsWith("\t"))
            //sFieldDesc = sFieldDesc.substring(0,sFieldDesc.length()-1);
        } catch (Exception ex) {
            throw new YssException("取表［" + sTableName + "］字段信息出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return htFields; //sFieldDesc+"\r\f"+sField;
    }

    /**
     * 检查表的数据是否存在
     * @param sTable String
     * @param sPKFields String
     * @return boolean 返回值：true有值，false无值
     */
    public void checkInput(byte btOper) throws YssException {
        //checkData(sTable,sPKFields,sPKValues);
    }

    protected void checkData(byte btOper, String sTable, String sPKFields, String sPKValues, String sOldValues) throws YssException {
        String sqlStr = "", oldSql = "";
        String[] arrField = null, arrValue = null, arrOldValue = null;
        ResultSet rs = null;
        try {
            arrField = sPKFields.split(",");
            arrValue = sPKValues.split(";");
            arrOldValue = sOldValues.split(";");
            if (btOper == YssCons.OP_EDIT) {
                sqlStr = " where 1=1 ";
                for (int i = 0; i < arrField.length; i++) {
                    sqlStr += " and " + arrField[i] + "=" + arrValue[i];
                    oldSql += arrField[i] + "<>" + arrOldValue[i] + " and ";
                }
              //modify by zhangfa 20101012 MS01829    特定数据处理界面有问题    QDV4赢时胜(33上线测试)2010年10月9日01_B   
                /**
                if (oldSql.endsWith(" and ")) {
                    oldSql = oldSql.substring(0, oldSql.length() - 5);
                }
                sqlStr = " select '1' from " + sTable + " " + sqlStr + " and " + oldSql;
                */
                String newsPKValues=sPKValues.replaceAll(",", "").replaceAll(" ","");
            	String oldsOldValues=sOldValues.replaceAll(",", "").replaceAll(" ","");
            	if(newsPKValues.equals(oldsOldValues)){
            		return;
            	}
            	sqlStr=" select '1' from " + sTable + " " + sqlStr;
                //------------------------------------------------------------------------------------------------
                rs = dbl.openResultSet(sqlStr);
                if (rs.next()) {
                    throw new YssException("表" + sTable + "中已经存在数据，操作失败");
                }
            } else if (btOper == YssCons.OP_ADD) {
                sqlStr = " where 1=1 ";
                for (int i = 0; i < arrField.length; i++) {
                    sqlStr += " and " + arrField[i] + "=" + arrValue[i];
                }
                sqlStr = " select '1' from " + sTable + sqlStr;
                rs = dbl.openResultSet(sqlStr);
                if (rs.next()) {
                    throw new YssException("表" + sTable + "中已经存在数据，操作失败");
                }
            }
        } catch (Exception ex) {
        	//modify by zhangfa 20100827 MS01656    新建两条相同的数据，点击确定保存时系统会报错    QDV4赢时胜(测试)2010年08月25日04_B  
        	throw new YssException(ex.getMessage(),ex);
        	//---------------------------------------------------------------------------------------------------------------
        }
        // return true;
    }
}
