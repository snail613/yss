package com.yss.vsub;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.YssFun;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import java.util.*;

public class YssDbFun {
    protected YssPub pub = null; //全局变量
    protected DbBase dbl = null; //数据连接已经处理

    public YssDbFun(YssPub ysspub) {
        pub = ysspub;
        dbl = ysspub.getDbLink();
    }

    public void intoDb(String tableName, String sFields, String sData,
                       String delSql) throws YssException {
        intoDb(tableName, sFields, sData, delSql, "");
    }

    public void intoDb(String tableName, String sFields, String sData,
                       String delSql, String sAutoCodeField) throws YssException {
        String[] rowsArry = sData.split(YssCons.YSS_LINESPLITMARK);
        String[][] fieldInfo = null;
        boolean bTrans = false;
        ResultSet rs = null;
        Connection conn = null;
        Statement st = null;
        try {
            conn = dbl.loadConnection();
            bTrans = true;
            conn.setAutoCommit(false);
            if (delSql.length() > 0) {
                dbl.executeSql(delSql);
            }
            rs = dbl.openResultSet("select " +
                                   (sFields.length() > 0 ? sFields : " * ")
                                   + " from " + tableName + " where 1=2 ");
            ResultSetMetaData rsmd = rs.getMetaData();
            fieldInfo = new String[rsmd.getColumnCount()][2];
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                fieldInfo[i - 1][0] = rsmd.getColumnName(i);
                fieldInfo[i - 1][1] = rsmd.getColumnTypeName(i);
            }
            String tmpValue = null;

            st = conn.createStatement();
            for (int i = 0; i <= rowsArry.length - 1; i++) {
                StringBuffer intoSql = new StringBuffer("insert into " + tableName
                    + (sFields.length() > 0 ? " (" + sFields + ") " : "") +
                    " values ( ");
                String[] colsArry = rowsArry[i].split("\t");
                String[] fieldArry = sFields.split(",");
                for (int j = 0; j <= fieldArry.length - 1; j++) {
                    if (j < colsArry.length) {
                        tmpValue = colsArry[j];
                    } else {
                        tmpValue = "";
                    }
                    if (sAutoCodeField.length() > 0) {
                        if (fieldArry[j].equalsIgnoreCase(sAutoCodeField)) {
                            tmpValue = getNextInnerCode(tableName, sAutoCodeField,
                                "000000", "");
                        }
                    }
                    if (tmpValue != null) {
                        if (fieldInfo[j][1].indexOf("VARCHAR") > -1) {
                            if (tmpValue.indexOf("'") > 0) {
                            	/**shashijie 2012-7-1 STORY 2475 */
                            	tmpValue = tmpValue.replaceAll("'", "''");
								/**end*/
                            }
                            tmpValue = "'" +
                                (tmpValue.length() == 0 ? " " : tmpValue) + "'";
                        }
                        if (fieldInfo[j][1].indexOf("DATE") > -1) {
                            tmpValue = dbl.sqlDate( (YssFun.isDate(tmpValue)) ?
                                tmpValue : "1900-1-1");
                        }
                    }
                    intoSql.append(tmpValue);
                    if (j < fieldInfo.length - 1) {
                        intoSql.append(",");
                    }
                }
                intoSql.append(")");
                st.addBatch(intoSql.toString());
            }
            st.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存数据库时发生错误", e);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 返回sql语句指定的值。出错返回空
     * @param sSql String
     * @param sDefault String
     * @return String
     */
    public String GetValuebySql(String sSql, String sDefault) throws
        YssException {
        ResultSet Rs = null;
        try {
            Rs = dbl.openResultSet(sSql);
            if (Rs.next()) {
                if (! (Rs.getString(1) == null ||
                       Rs.getString(1).equalsIgnoreCase("null"))) {
                    sDefault = Rs.getString(1);
                }
            }
            Rs.getStatement().close();
            Rs = null;
            return sDefault;
        } catch (Exception sqle) {
            throw new YssException("获取数据出错！", sqle);
        } finally {
            dbl.closeResultSetFinal(Rs);
        }
    }

    public String[] getValueArybySql(String sSql) throws YssException {
        ResultSet Rs = null;
        String[] sResultAry = null;
        ResultSetMetaData rsmd = null;
        try {
            Rs = dbl.openResultSet(sSql);
            rsmd = Rs.getMetaData();
            if (Rs.next()) {
                sResultAry = new String[rsmd.getColumnCount() - 1];
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    sResultAry[i] = Rs.getString(i + 1) + "";
                }
            }
            Rs.getStatement().close();
            Rs = null;
            return sResultAry;
        } catch (Exception sqle) {
            throw new YssException("获取数据出错！", sqle);
        } finally {
            dbl.closeResultSetFinal(Rs);
        }

    }

    public String GetValuebySql(String sSql) throws YssException {
        return GetValuebySql(sSql, "");
    }

    //返回记录集记录总数目
    public int SqlRecordCount(String sSql) throws YssException {
        ResultSet hzrs = null;
        int iTem = 0;
        try {
            hzrs = dbl.openResultSet(sSql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                     ResultSet.CONCUR_READ_ONLY);
            hzrs.last();
            iTem = hzrs.getRow();
            hzrs.getStatement().close();
            hzrs = null;
            return iTem;
        } catch (Exception se) {
            throw new YssException("获取记录集数量失败！", se);
        } finally {
            dbl.closeResultSetFinal(hzrs);
        }
    }

    public String getNextInnerCode(String sTable, String sInnerField,
                                   String sDefault) throws
        YssException {
        return getNextInnerCode(sTable, sInnerField, sDefault, "", 1);
    }

    public String getNextInnerCode(String sTable, String sInnerField,
                                   String sDefault, String sWhereSql) throws
        YssException {
        return getNextInnerCode(sTable, sInnerField, sDefault, sWhereSql, 1);
    }

    /**
     * 获取下一个内部编号
     * @param sTable String  表名
     * @param sInnerField String 内部编号的字段名
     * @return String
     */
    public String getNextInnerCode(String sTable, String sInnerField,
                                   String sDefault, String sWhereSql, int iOnlyNumber) throws
        YssException {
        String result = "";
        int iAscii = 0;
        String s = "", ss = "";
        String sFormat = "";
        String defaultMaxNum = ""; //add by huangqirong 2012-11-26 bug #6354 默认最大 编号
        String sCurCode = GetValuebySql("select max(" + sInnerField + ") from " +
                                        sTable + sWhereSql);
        if (sCurCode.length() == 0) {
            return sDefault;
        }
        if (iOnlyNumber == 0) {
            for (int i = sCurCode.length() - 1; i >= 0; i--) {
                s = sCurCode.substring(i, i + 1);
                iAscii = getAscii(s);
                if (iAscii == 90) {
                    iAscii = 48;
                    ss = getAsciiStr(iAscii) + ss;
                } else if (iAscii == 57) {
                    iAscii = 65;
                    ss = getAsciiStr(iAscii) + ss;
                    break;
                } else {
                    iAscii++;
                    ss = getAsciiStr(iAscii) + ss;
                    break;
                }
            }
            result = sCurCode.substring(0, sCurCode.length() - ss.length());
            result = result + ss;
        } else if (iOnlyNumber == 1) {
            if (YssFun.isNumeric(sCurCode)) {
                int iNum = Integer.parseInt(sCurCode);
                iNum = iNum + 1;
                result = String.valueOf(iNum);
                if (sDefault.length() > 0) {
                    for (int j = 0; j < sDefault.length(); j++) {
                        sFormat += "0";
                        defaultMaxNum += "9";	//add by huangqirong 2012-11-26 bug #6354
                    }
                    int imaxNum = Integer.parseInt(defaultMaxNum);//add by huangqirong 2012-11-26 bug #6354
                    
                    if(iNum > imaxNum)//add by huangqirong 2012-11-26 bug #6354
                    	sFormat +="0";//add by huangqirong 2012-11-26 bug #6354
                    	
                    result = YssFun.formatNumber(iNum, sFormat);
                }
//            if (result.length() > sCurCode.length())
//            {
//               result = "1";
//            }
//            while(result.length() < sCurCode.length())
//            {
//               result = "0" + result;
//            }
            }
            //add by xuqiji 20090513：QDV4赢时胜（上海）2009年5月06日01_B  MS00433    新建股指期货交易数据时产生的交易编号有误
        } else if (iOnlyNumber == 10) {
            if (YssFun.isNumeric(sCurCode)) {
                int iNum = Integer.parseInt(sCurCode);
                iNum = iNum + 10;
                result = String.valueOf(iNum);
                if (sDefault.length() > 0) {
                    for (int j = 0; j < sDefault.length(); j++) {
                        sFormat += "0";
                    }
                    result = YssFun.formatNumber(iNum, sFormat);
                }
            }
        }
        
        //20120906 added by liubo.Bug #5454
        //交易结算时，生成的资金调拨编号如果超过999999，在生成新的调拨编号时，无论结算了几笔，都只会生成100000这个编号，如此就会造成资金调拨数据表的违反主键约束
        //当天的资金调拨编号若存在999999，则给出一个友好提示，避免直接抛出违反主键约束的异常
        //================================
        else if (iOnlyNumber == 12) {
            if (YssFun.isNumeric(sCurCode)) {
                int iNum = Integer.parseInt(sCurCode);
                if (iNum == 999999)
                {
                	throw new YssException("当天生成的交易结算编号达到最大值999999！请对已存在的资金调拨进行处理后再重试！");
                }
                iNum = iNum + 1;
                result = String.valueOf(iNum);
                if (sDefault.length() > 0) {
                    for (int j = 0; j < sDefault.length(); j++) {
                        sFormat += "0";
                    }
                    result = YssFun.formatNumber(iNum, sFormat);
                }
            if (result.length() > sCurCode.length())
            {
               result = "1";
            }
            while(result.length() < sCurCode.length())
            {
               result = result + "0";
            }
            }
        }
        //============end====================
        return result;
    }

    private int getAscii(String str) {
        byte[] cc = str.getBytes();
        int b = (int) cc[0];
        return b;
    }

    private String getAsciiStr(int iAscii) {
        char a[] = new char[1];
        a[0] = (char) iAscii;
        String ss = new String(a);
        return ss;
    }
    
    public String getNextDataInnerCode() throws YssException
    {
    	ResultSet rs = null;
    	String strSql = " select SEQ_DATA_REDCORDCODE.nextval from dual ";
    	String dataNum = "";
    	try {
    		rs = dbl.openResultSet(strSql);
    		if(rs.next())
    		{
    			dataNum = rs.getString(1);
    		}
    		int dataLength = dataNum.length();
    		int lenDiffer = 9 - dataLength;
    		for(int i=0;i<lenDiffer;i++)
    		{
    			dataNum = "0" + dataNum;
    		}
    		return dataNum;
		} catch (Exception sqle) {
            throw new YssException("获取数据出错！", sqle);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //生成树行表结构中的排序号
    public String treeBuildOrderCode(String sTableName, String sKeyField,
                                     String sParentCode, int iOrderCode) throws
        YssException {
        try {
            String sql = "select FOrderCode from " + sTableName + " where " +
                sKeyField + " = '" +
                sParentCode + "'";
            String sParentOrder = GetValuebySql(sql);
            String reOrderCode = sParentOrder +
                YssFun.formatNumber(iOrderCode, "000");
            return reOrderCode;
        } catch (Exception e) {
            throw new YssException("获取表【" + sTableName + "】的排序号出错", e);
        }
    }

    //用于树行表结构的整理排序号
    public void treeAdjustOrder(String sTableName, String sKeyField,
                                String sKeyValue, int iOrderCode) throws
        YssException {
        String sql = "";
        String sOldOrder = "";
        String sNewOrder = "";
        try {
            sql = "select FOrderCode from " + sTableName + " where " +
                sKeyField + " = '" +
                sKeyValue + "'";
            sOldOrder = GetValuebySql(sql);
            sNewOrder = YssFun.formatNumber(iOrderCode, "000");
            if (sOldOrder.length() > sNewOrder.length()) {
                sNewOrder = YssFun.left(sOldOrder,
                                        sOldOrder.length() - sNewOrder.length()) + sNewOrder;
            }
            
            //---QDV4海富通2011年01月17日01_B-----
            //第一步：根据FBarCode更新父结点（也就是要更新的节点），第二步再更新该结点下的所有子节点
            //这样做不会把其它的结点也对应更新了
            String parentSql = "update " + sTableName + " set FOrderCode = '" + sNewOrder + "'" +
            dbl.sqlJoinString() +
            dbl.sqlRight("FOrderCode",
                         dbl.sqlLen("FOrderCode") + "-" +
                         String.valueOf(sOldOrder.length())) +
            " where FOrderCode = '" +
            sOldOrder + "' and " + sKeyField + " = '" +
            sKeyValue + "'"
            ;
            dbl.executeSql(parentSql);
            
            String sSql =
                "update " + sTableName + " set FOrderCode = '" + sNewOrder + "'" +
                dbl.sqlJoinString() +
                dbl.sqlRight("FOrderCode",
                             dbl.sqlLen("FOrderCode") + "-" +
                             String.valueOf(sOldOrder.length())) +
                " where FOrderCode like '" +
                sOldOrder + "%' "+
                "and FOrderCode <>'"+sOldOrder+"' and "+
                "length(substr(FOrderCode, - (length(FOrderCode) - 6))) >0"// add by lidaolong
                ;
            //---edit by lidaolong 20110121---------------
            dbl.executeSql(sSql);
        } catch (Exception e) {
            throw new YssException("调整表【" + sTableName + "】的排序号出错", e);
        }
    }

    //在树行表结构当修改了代码后同时更新所有已它为父节点的代码
    public void treeAdjustParentCode(String sTableName, String sKeyField,
                                     String sOldKeyValue, String sNewKeyValue) throws
        YssException {
        String strSql = "";
        try {
            if (!sOldKeyValue.equalsIgnoreCase(sNewKeyValue)) {
                strSql = "update " + sTableName + " set " + sKeyField + " = " +
                    dbl.sqlString(sNewKeyValue) + " where " + sKeyField + " = " +
                    dbl.sqlString(sOldKeyValue);
                dbl.executeSql(strSql);
            }
        } catch (Exception e) {
            throw new YssException("调整表【" + sTableName + "】的父编号出错", e);
        }
    }

    //在树行表结构中如果子节点被审核，那么所有的父节点也都跟着审核
    public void treeCheckParentNode(String sTableName, String sSubOrderCode,
                                    int iCheckState) throws YssException {
        String strSql = "";
        String strOrderCode = "";
        try {
            if (sSubOrderCode.length() > 3) {
                strOrderCode = strOrderCode.substring(0, strOrderCode.length() - 3);
                String strParentCodeList = dbl.sqlString(strOrderCode);
                while (strOrderCode.length() > 3) {
                    strParentCodeList += ",";
                    strOrderCode = strOrderCode.substring(0,
                        strOrderCode.length() - 3);
                    strParentCodeList += dbl.sqlString(strOrderCode);
                }
                strSql = "update " + sTableName + " set FCheckState  = " +
                    String.valueOf(iCheckState) + " where FOrderCode in (" +
                    strParentCodeList + ")";
                dbl.executeSql(strSql);
            }
        } catch (Exception e) {
            throw new YssException("调整表【" + sTableName + "】的审核状态出错", e);
        }
    }

    //在树行表结构中如果父节点修改了，那么排序代码也要跟着变动
    public void treeChangeParentCode(String sTableName, String sKeyField,
                                     String sOldKeyValue, String sNewParentValue) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        String newOrderCode = "";
        String oldOrderCode = "";
        try {
            strSql = "select FOrderCode from " + sTableName + " where " +
                sKeyField + " = " +
                dbl.sqlString(sNewParentValue);
            newOrderCode = this.GetValuebySql(strSql);

            strSql = "select FOrderCode from " + sTableName + " where " +
                sKeyField + " = " + dbl.sqlString(sOldKeyValue);
            oldOrderCode = this.GetValuebySql(strSql);

            strSql = "update " + sTableName + " set FParentCode = " +
                dbl.sqlString(sNewParentValue) + " where " + sKeyField + " = " +
                dbl.sqlString(sOldKeyValue);
            dbl.executeSql(strSql);

            strSql = "update " + sTableName + " set FOrderCode = " +
                dbl.sqlString(newOrderCode) + dbl.sqlJoinString() +
                dbl.sqlRight("FOrderCode",
                             dbl.sqlLen("FOrderCode") + "-" +
                             String.valueOf(oldOrderCode.length() - 3)) +
                " where FOrderCode like '" + oldOrderCode + "%'";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("调整表【" + sTableName + "】的父节点出错", e);
        }
    }

    //通用的审核过程
    public void auditCommon(String sTableName, String sKeyField,
                            String sKeyValue, int iCheckState) throws
        YssException {
        Connection conn = dbl.loadConnection();
        PreparedStatement pst = null;
        boolean bTrans = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            pst = conn.prepareStatement(
                "update " + sTableName +
                " set FCheckState = ?, FCheckUser = ?, FCheckTime = ? " +
                " where " + sKeyField + " = '" + sKeyValue + "'",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pst.setInt(1, iCheckState);
            pst.setString(2, pub.getUserCode());
            pst.setString(3, YssFun.formatDatetime(new java.util.Date()));
            pst.executeUpdate();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核出错", e);
        }finally
        {
        	dbl.endTransFinal(conn, bTrans);
        	 dbl.closeStatementFinal(pst);
        }
    }

    //通用的删除过程，可根据多个字段联合查询删除，多个字段用","分割
    public void delCommon(String sTableName, String sKeyFields,
                          String sKeyValues) throws YssException {
        StringBuffer buf = new StringBuffer();
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            if (sKeyFields.indexOf(",") > 0) {
                String[] sKeyFieldAry = sKeyFields.split(",");
                String[] sKeyValueAry = sKeyValues.split(",");
                buf.append(" where ").append(sKeyFieldAry[0]).append(" = ").append(
                    sKeyValueAry[0]);
                for (int i = 1; i < sKeyFieldAry.length; i++) {
                    buf.append(" and ").append(sKeyFieldAry[i]).append(" = ").append(
                        sKeyValueAry[i]);
                }
            } else {
                buf.append(" where ").append(sKeyFields).append(" = ").append(
                    sKeyValues);
            }
            conn.setAutoCommit(false);
            bTrans = true;
//         strSql = "delete from " + sTableName + buf.toString();
            strSql = "update " + sTableName + " set FCheckState = 2 " +
                buf.toString();
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            dbl.endTransFinal(conn, bTrans);
            throw new YssException("删除菜单设置出错", e);
        }
    }

    public void checkInputCommon(byte btOper, String sTableName,
                                 String sKeyField, String sNewKeyValue,
                                 String sOldKeyValue) throws
        YssException {
        checkInputCommon(btOper, sTableName, sKeyField, sNewKeyValue,
                         sOldKeyValue, null, null);
    }

    //-------------------------------------------------------------------------------------
    //MS00190 QDV4交银施罗德2009年01月15日01_B 2009.02.11方浩
    //因为要修改checkInputCommon（）方法的传入参数，所以保存此方法
    public void checkInputCommon(byte btOper, String sTableName,
                                 String sKeyField, String sNewKeyValue,
                                 String sOldKeyValue,
                                 java.util.Date dNewStartDate,
                                 java.util.Date dOldStartDate) throws
        YssException {
        checkInputCommon(btOper, sTableName,
                         sKeyField, sNewKeyValue,
                         sOldKeyValue,
                         dNewStartDate,
                         dOldStartDate, true); //调修改后的方法
    }

    //------------------------------------------------------------------------------------
    /**
     * checkInputCommon 通用数据验证方法
     * @param btOper byte 操作类型（增加、删除、修改、审核）
     * @param sTableName String 表名
     * @param sKeyField String  关键字
     * @param sNewKeyValue String
     * @param sOldKeyValue String
     * @param dNewStartDate Date
     * @param dOldStartDate Date
     * @param boolean  bIsCheak判断是否要审核
     * @throws YssException
     */
    //-------------------------------------------------------------------------------------
    //MS00190 QDV4交银施罗德2009年01月15日01_B 2009.02.11方浩
    //修改checkInputCommon（）方法的传入参数，报表组设置中，接口群设置中都没有审核与未审核状态，所以传入个参数控制下
    public void checkInputCommon(byte btOper, String sTableName,
                                 String sKeyField, String sNewKeyValue,
                                 String sOldKeyValue,
                                 java.util.Date dNewStartDate,
                                 java.util.Date dOldStartDate, boolean bIsCheak) throws
        YssException {
        String strSql = "";
        HashMap hmFieldType = null;
        String tmpValue = null;
        String sTmpSql1 = "", sTmpSql2 = "";
        String[] sTmpStr1 = null;
        String[] sTmpStr2 = null;
        String[] sTmpStr3 = null;
        ResultSet rs = null;
        String sFieldType = null;
        String sTmpError = "", sTmpState = "";
        try {
            sTmpStr1 = sKeyField.split(",");
            sTmpStr2 = sNewKeyValue.split(",");
            sTmpStr3 = sOldKeyValue.split(",");
            hmFieldType = getFieldsType(sTableName);
            /**shashijie 2011.06.01 诸多调用父类的checkInputCommon修改报异常,这里不知何时修改被注视,所以现在解除注视 BUG 2018 QDV4赢时胜(测试)2011年6月1日01_B*/
            if (btOper == YssCons.OP_EDIT || btOper == YssCons.OP_ADD) {//edit by licai 20101201 BUG #477 凭证模板的辅助核算项现实有问题 
            /**end*/
                if (!sNewKeyValue.equalsIgnoreCase(sOldKeyValue) ||
                    ( (dOldStartDate != null && dNewStartDate != null) ?
                     !dNewStartDate.equals(dOldStartDate) :
                     !sNewKeyValue.equalsIgnoreCase(sOldKeyValue))) {
                    for (int i = 0; i <= sTmpStr1.length - 1; i++) {
                        if (sTmpStr2.length > i) {
                            if (sTmpStr2[i] !=null) {//edited by zhouxiang  sTmpStr2[i].length>0
                                sFieldType = (String) hmFieldType.get(sTmpStr1[i].
                                    toUpperCase());
                                if (sFieldType != null) {
                                    if ( (sFieldType).indexOf("DATE") > -1) {
                                        sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                            dbl.sqlDate(sTmpStr2[i]) + " and ";
                                    } else if ( (sFieldType).indexOf("DECIMAL") > -1) { //2007.12.06 添加  lzp
                                        sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                            sTmpStr2[i] + " and ";
                                    } else {
                                        sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                        	//modify by yangshaokai 2012.1.6 处理 浮动CPI设置 新建 投资组合 为空
                                            dbl.sqlString(sTmpStr2[i].length() > 0 ? sTmpStr2[i] : " ") + " and ";
                                    }
                                }
                            }
                        }
                    }
                    if (sTmpSql1.length() > 0) {
                        sTmpSql1 = YssFun.left(sTmpSql1, sTmpSql1.length() - 4);
                    }
                    strSql = "select " + sKeyField + ",FCheckState from " +
                        sTableName + " where " + sTmpSql1;
                    rs = dbl.openResultSet(strSql);
                    if (rs.next()) {
                        if (pub.getSysCheckState() || rs.getInt("FCheckState") == YssCons.RS_DEL) {
                            sTmpError = "【" + YssFun.getCheckStateName(rs.getInt("FCheckState")) + "】中已经存在";
                            sTmpState = rs.getInt("FCheckState") == 2 ? "请还原该信息" : "请重新输入";
                            throw new YssException(sTmpError + "【" + sNewKeyValue + "】信息，" + sTmpState);
                        } else {
                            throw new YssException("【" + sNewKeyValue + "】信息已经存在，请重新输入");
                        }

                    }
                }
            }
            if (btOper == YssCons.OP_EDIT) {
                for (int i = 0; i <= sTmpStr1.length - 1; i++) {
                    if (sTmpStr3.length > i) {
                        if (sTmpStr3[i].length() > 0) {
                            sFieldType = (String) hmFieldType.get(sTmpStr1[i].
                                toUpperCase());
                            if (sFieldType != null) {
                                if ( (sFieldType).indexOf("DATE") > -1) {
                                    sTmpSql2 = sTmpSql2 + sTmpStr1[i] + " = " +
                                        dbl.sqlDate(sTmpStr3[i]) + " and ";
                                } else if ( (sFieldType).indexOf("DECIMAL") > -1) { //2007.12.02 添加 蒋锦 DB2种可能的情况
                                    sTmpSql2 = sTmpSql2 + sTmpStr1[i] + " = " +
                                        sTmpStr3[i] + " and "; //  lzp 将此行的sTmpStr2[i]修改为sTmpStr3[i]  之前写错了
                                }

                                else {
                                    sTmpSql2 = sTmpSql2 + sTmpStr1[i] + " = " +
                                        dbl.sqlString(sTmpStr3[i]) + " and ";
                                }
                            }
                        }
                    }
                }
                if (sTmpSql2.length() > 0) {
                    sTmpSql2 = YssFun.left(sTmpSql2, sTmpSql2.length() - 4);
                }
                if (bIsCheak) { //MS00190 QDV4交银施罗德2009年01月15日01_B 2009.02.11方浩传入的新参数，接口群设置和报表组设置都没有审核与未审核状态

                    strSql = "select FCheckState from " + sTableName +
                        " where FCheckState <> 2 and " + sTmpSql2;
                    tmpValue = GetValuebySql(strSql);
                    if (tmpValue.trim().length() == 0) {
                        throw new YssException("您正在修改的【" + sNewKeyValue +
                                               "】信息已被其他用户删除，修改失败");
                    }
                    if (pub.getSysCheckState() && tmpValue.equalsIgnoreCase("1")) {
                        throw new YssException("您正在修改的【" + sNewKeyValue +
                                               "】信息已被审核，修改失败");
                    }
                }
            } else if (btOper == YssCons.OP_AUDIT) {
                for (int i = 0; i <= sTmpStr1.length - 1; i++) {
                    if (sTmpStr2.length > i) {
                        if (sTmpStr2[i].length() > 0) {
                            sFieldType = (String) hmFieldType.get(sTmpStr1[i].
                                toUpperCase());
                            if (sFieldType != null) {
                                if ( (sFieldType).indexOf("DATE") > -1) {
                                    sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                        dbl.sqlDate(sTmpStr2[i]) + " and ";
                                } else if ( (sFieldType).indexOf("DECIMAL") > -1) { //2007.12.02 添加 蒋锦 DB2种可能的情况
                                    sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                        sTmpStr2[i] + " and ";
                                } else {
                                    sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                        dbl.sqlString(sTmpStr2[i]) + " and ";
                                }
                            }
                        }
                    }
                }
                if (sTmpSql1.length() > 0) {
                    sTmpSql1 = YssFun.left(sTmpSql1, sTmpSql1.length() - 4);
                }

                strSql = "select FCheckState from " + sTableName +
                    " where FCheckState <> 2  and " + sTmpSql1;
                tmpValue = GetValuebySql(strSql);
                if (tmpValue.trim().length() == 0) {
                    throw new YssException("您想要审核的【" + sNewKeyValue +
                                           "】信息已被其他用户删除，审核失败");
                }
            } else if (btOper == YssCons.OP_DEL) {
                for (int i = 0; i <= sTmpStr1.length - 1; i++) {
                    if (sTmpStr2.length > i) {
                        if (sTmpStr2[i].length() > 0) {
                            sFieldType = (String) hmFieldType.get(sTmpStr1[i].
                                toUpperCase());
                            if (sFieldType != null) {
                                if ( (sFieldType).indexOf("DATE") > -1) {
                                    sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                        dbl.sqlDate(sTmpStr2[i]) + " and ";
                                } else if ( (sFieldType).indexOf("DECIMAL") > -1) { //2007.12.06 添加  lzp
                                    sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                        sTmpStr2[i] + " and ";
                                }

                                else {
                                    sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                        dbl.sqlString(sTmpStr2[i]) + " and ";
                                }
                            }
                        }
                    }
                }
                if (sTmpSql1.length() > 0) {
                    sTmpSql1 = YssFun.left(sTmpSql1, sTmpSql1.length() - 4);
                }
                //---add by yangshaokai 2011.12.20 BUG 3425 QDV4赢时胜(测试)2011年12月16日01_B 若where条件为空 则返回 start---//
                if(sTmpSql1.trim().length() == 0){
                	return;
                }
                //---add by yangshaokai 2011.12.20 BUG 3425 QDV4赢时胜(测试)2011年12月16日01_B 若where条件为空 则返回 end---//
                strSql = "select FCheckState from " + sTableName + " where " +
                    sTmpSql1;

                tmpValue = GetValuebySql(strSql);
                if (pub.getSysCheckState() && tmpValue.equalsIgnoreCase("1")) {
                    throw new YssException("您想要删除的【" + sNewKeyValue +
                                           "】信息已被其他用户审核，不能删除");
                }
            }else if(btOper == YssCons.OP_MutliAdd){
            	if (!sNewKeyValue.equalsIgnoreCase(sOldKeyValue) ||
                        ( (dOldStartDate != null && dNewStartDate != null) ?
                         !dNewStartDate.equals(dOldStartDate) :
                         !sNewKeyValue.equalsIgnoreCase(sOldKeyValue))) {
                        for (int i = 0; i <= sTmpStr1.length - 1; i++) {
                            if (sTmpStr2.length > i) {
                                if (sTmpStr2[i] !=null) {//edited by zhouxiang  sTmpStr2[i].length>0
                                    sFieldType = (String) hmFieldType.get(sTmpStr1[i].
                                        toUpperCase());
                                    if (sFieldType != null) {
                                        if ( (sFieldType).indexOf("DATE") > -1) {
                                            sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                                dbl.sqlDate(sTmpStr2[i]) + " and ";
                                        } else if ( (sFieldType).indexOf("DECIMAL") > -1) { //2007.12.06 添加  lzp
                                            sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                                sTmpStr2[i] + " and ";
                                        } else {
                                            sTmpSql1 = sTmpSql1 + sTmpStr1[i] + " = " +
                                                dbl.sqlString(sTmpStr2[i]) + " and ";

                                        }
                                    }
                                }
                            }
                        }
                        if (sTmpSql1.length() > 0) {
                            sTmpSql1 = YssFun.left(sTmpSql1, sTmpSql1.length() - 4);
                        }
                        strSql = "select " + sKeyField + ",FCheckState from " +
                            sTableName + " where " + sTmpSql1;
                        rs = dbl.openResultSet(strSql);
                        if (rs.next()) {
                            if (pub.getSysCheckState() || rs.getInt("FCheckState") == YssCons.RS_DEL) {
                                sTmpError = "【" + YssFun.getCheckStateName(rs.getInt("FCheckState")) + "】中已经存在";
                                sTmpState = rs.getInt("FCheckState") == 2 ? "请还原该信息" : "请重新输入";
                                throw new YssException(sTmpError + "【" + sNewKeyValue + "】信息，" + sTmpState);
                            } else {
                                throw new YssException("【" + sNewKeyValue + "】信息已经存在，请重新输入");
                            }

                        }
                    }
            }else if(btOper == YssCons.OP_MutliEdit){

                for (int i = 0; i <= sTmpStr1.length - 1; i++) {
                    if (sTmpStr2.length > i) {
                        if (sTmpStr2[i].length() > 0) {
                            sFieldType = (String) hmFieldType.get(sTmpStr1[i].
                                toUpperCase());
                            if (sFieldType != null) {
                                if ( (sFieldType).indexOf("DATE") > -1) {
                                    sTmpSql2 = sTmpSql2 + sTmpStr1[i] + " = " +
                                        dbl.sqlDate(sTmpStr2[i]) + " and ";
                                } else if ( (sFieldType).indexOf("DECIMAL") > -1) { //2007.12.02 添加 蒋锦 DB2种可能的情况
                                    sTmpSql2 = sTmpSql2 + sTmpStr1[i] + " = " +
                                        sTmpStr2[i] + " and "; //  lzp 将此行的sTmpStr2[i]修改为sTmpStr3[i]  之前写错了
                                }

                                else {
                                    sTmpSql2 = sTmpSql2 + sTmpStr1[i] + " = " +
                                        dbl.sqlString(sTmpStr2[i]) + " and ";
                                }
                            }
                        }
                    }
                }
                if (sTmpSql2.length() > 0) {
                    sTmpSql2 = YssFun.left(sTmpSql2, sTmpSql2.length() - 4);
                }
                if (bIsCheak) { //MS00190 QDV4交银施罗德2009年01月15日01_B 2009.02.11方浩传入的新参数，接口群设置和报表组设置都没有审核与未审核状态
                    String[] sTableNameField = sTableName.split("_");
                    String sGroup = sTableNameField[1];
                    
                    strSql = "select FCheckState from " + sTableName +
                    		(pub.getPrefixTB().equals(sGroup) ? " where FCheckState <> 2 and " : " where 1 =1 and ") + 
                    				sTmpSql2;
                    tmpValue = GetValuebySql(strSql);
//                    if (tmpValue.trim().length() == 0) {
//                        throw new YssException("您正在修改的【" + sNewKeyValue +
//                                               "】信息已被其他用户删除，修改失败");
//                    }

                    if (pub.getSysCheckState() && tmpValue.equalsIgnoreCase("1")) {
                        throw new YssException("您正在修改的【" + sNewKeyValue +
                                               "】信息已被审核，修改失败");
                    }
                }
                
                
            
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public HashMap getFieldsType(String sTableName) throws YssException {
        HashMap hmResult = new HashMap();
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        String strSql = "";
        try {
            strSql = "select * from " + sTableName + " where 1=2";
            rs = dbl.openResultSet(strSql);
            rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                hmResult.put(rsmd.getColumnName(i).toUpperCase().trim(), rsmd.getColumnTypeName(i));
            }
            return hmResult;
        } catch (Exception e) {
            throw new YssException("获取表【" + sTableName + "】的字段类型出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public HashMap getFieldsType(ResultSet rs) throws YssException {
        HashMap hmResult = new HashMap();
        ResultSetMetaData rsmd = null;
        String strSql = "";
        try {
            rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                hmResult.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
            }
            return hmResult;
        } catch (Exception e) {
            throw new YssException("获取表的字段类型出错");
        }
    }

    public String[] getFieldsParam(String sTableName) throws YssException {
        String[] arrResult = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        String strSql = "";
        try {
            strSql = "select * from " + sTableName + " where 1=2";
            rs = dbl.openResultSet(strSql);
            rsmd = rs.getMetaData();
            arrResult = new String[rsmd.getColumnCount()];
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                arrResult[i - 1] = rsmd.getColumnName(i) + "," + rsmd.getColumnTypeName(i) + "," + rsmd.getColumnDisplaySize(i);
                //alResult.add(rsmd.getColumnName(i)+","+rsmd.getColumnTypeName(i)+","+rsmd.getColumnDisplaySize(i));
            }
            return arrResult;
        } catch (Exception e) {
            throw new YssException("获取表【" + sTableName + "】的字段参数出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String[] getFieldsParam(ResultSet rs) throws YssException {
        String[] arrResult = null;
        ResultSetMetaData rsmd = null;
        try {
            rsmd = rs.getMetaData();
            arrResult = new String[rsmd.getColumnCount()];
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                arrResult[i - 1] = rsmd.getColumnName(i) + "," + rsmd.getColumnTypeName(i) + "," + rsmd.getColumnDisplaySize(i);
                //alResult.add(rsmd.getColumnName(i)+","+rsmd.getColumnTypeName(i)+","+rsmd.getColumnDisplaySize(i));
            }
            return arrResult;
        } catch (Exception e) {
            throw new YssException("获取表的字段参数出错", e);
        }
    }
    
    /**
     * 获取字段长度信息
     * @param rs
     * @return
     * @throws YssException
     */
    public HashMap getFieldsSizeInfo(ResultSet rs)  throws YssException{
    	HashMap hmResult = new HashMap();
         ResultSetMetaData rsmd = null;
         StringBuffer valBuf = new StringBuffer();
         StringBuffer keyBuf = new StringBuffer();
         try {
             rsmd = rs.getMetaData();
            
             for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            	 valBuf.setLength(0);
            	 keyBuf.setLength(0);
            	 
            	 keyBuf.append(rsmd.getColumnName(i)).append("\t").append(rsmd.getColumnTypeName(i));
            	 if(rsmd.getColumnTypeName(i).equalsIgnoreCase("NUMBER")){
            	     
            		 //说明：一旦字段间加减或者使用统计项后，获取不到数值型字段精度，这里暂时是给出默认值
            		 if(rsmd.getPrecision(i)==0){
            			 valBuf.append("(20,6)");
            		 }else{
            			 valBuf.append("(").append(rsmd.getPrecision(i)).append(",").append(rsmd.getScale(i)).append(")");
            		 }
            		 
            		 hmResult.put(keyBuf.toString(), valBuf.toString());
            		 
            	 }else if(rsmd.getColumnTypeName(i).equalsIgnoreCase("VARCHAR2")||rsmd.getColumnTypeName(i).equalsIgnoreCase("CHAR")){
            		 valBuf.append("(").append(rsmd.getColumnDisplaySize(i)).append(")");
            		 hmResult.put(keyBuf.toString(), valBuf.toString());
            	 }
            	 
             }
             return hmResult;
         } catch (Exception e) {
             throw new YssException("获取表的字段参数出错", e);
         }finally{
        	 valBuf.setLength(0);
        	 keyBuf.setLength(0);
         }
    }
    
    /***
     * 根据表来判断表的字段是否存在 true:不存在 false:存在
     * sTabName :表名
     * cloumsn : 要查询的表字段 
     */
    public boolean existsTabColumn_Ora(String sTabName, String columns) throws YssException {
        boolean existCol = true;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select * from user_col_comments where upper(table_name)=upper(" + dbl.sqlString(sTabName) + ")" +
                " and upper(Column_Name) = " + dbl.sqlString(columns.toUpperCase());
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                existCol = false;
            }
            return existCol;
        } catch (Exception e) {
            throw new YssException("查询Oracle表" + sTabName + "的字段" + columns + "时出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /***
     * 根据表来判断表的字段是否存在 true:不存在 false:存在
     * sTabName :表名
     * cloumsn : 要查询的表字段 
     */
    public boolean existsTabColumn(String sTabName, String columns) throws YssException {
        boolean existCol = true;
        String sqlStr = "";
        ResultSet rs = null;
        try {
        	if(dbl.dbType==YssCons.DB_DB2)
        	{
        		//sqlStr  = " SELECT * FROM SYSCAT.COLUMNS where tabschema = " + dbl.sqlString(dbl.getDB2Schema()) + " and upper(TABNAME) = upper(" + dbl.sqlString(sTabName) + ")" +
        				 // " and upper(COLNAME) = " + dbl.sqlString(columns.toUpperCase());
        	}else
        	{
        		sqlStr = "select * from user_col_comments where upper(table_name)=upper(" + dbl.sqlString(sTabName) + ")" +
                		 " and upper(Column_Name) = " + dbl.sqlString(columns.toUpperCase());
        	}
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                existCol = false;
            }
            return existCol;
        } catch (Exception e) {
            throw new YssException("查询数据库表" + sTabName + "的字段" + columns + "时出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
