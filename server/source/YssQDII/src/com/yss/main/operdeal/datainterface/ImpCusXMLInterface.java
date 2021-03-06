package com.yss.main.operdeal.datainterface;

import java.sql.*;
import java.util.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import com.yss.main.datainterface.*;
import com.yss.pojo.param.dao.*;
import com.yss.util.*;

//用于做XML的导入操作的
public class ImpCusXMLInterface
    extends BaseDaoOperDeal {
    private String pkVal = ""; //主键字段
    private String condVal = ""; //条件字段
    private String condInsVal = ""; //新增条件
    private String condDelVal = ""; //删除条件
    private String connVal = ""; //关联字段
    private String sFileType = ""; //文件的类型,如 MT541,MT535...
    public ImpCusXMLInterface() {
    }

    public String doInterface() throws YssException {
        return "";
    }

    public void insertData(DaoCusConfigureBean cusCfg, String allData) throws YssException { //此方法只处理XML的数据插入到最初临时表
        //处理文件内容,考虑XML无文件头与文件尾两部分
        String[] dataArr = allData.split("\f\f");
        String[] dataContent; //文件内容数据记录集
        String sValue = "";
        String sFields = getTmpTabFields(cusCfg.getTabName()); //获取字段集
        HashMap hmFieldValue = null;
        HashMap hmFieldType = dbFun.getFieldsType(cusCfg.getTabName());
        YssImpData impData = new YssImpData();
        try {
            deleteTmp(cusCfg.getTabName()); // 一次性删除临时表中的数据
            for (int i = 0; i < dataArr.length; i++) {
                impData.parseRowStr(dataArr[i]);
                String bootRoot = impData.getFileContent().substring(1, impData.getFileContent().indexOf(">"));
                bootRoot = "<" + bootRoot + ">";
                dataContent = impData.getFileContent().split(bootRoot); //获取单条记录集
                for (int j = 0; j < dataContent.length; j++) { //处理多条数据
                    if (dataContent[j].trim().length() == 0) {
                        continue;
                    }
                    dataContent[j] = bootRoot + dataContent[j];
                    hmFieldValue = getTmpTabValue(sFields, dataContent[j]);
                    sValue = buildSQLValue(hmFieldType, sFields, hmFieldValue,
                                           cusCfg);
                    insertData(sFields, sValue, cusCfg.getTabName(), hmFieldValue);
                }
            }
        } catch (Exception e) {
            throw new YssException("处理数据时出错", e);
        }
    }

    private String buildSQLValue(HashMap hmFieldType, String sFields, HashMap simpleData, DaoCusConfigureBean cusCfg) throws YssException {
        //根据字段类型返回关联的值 如 'abcdefg',123.245,to_date('2007-10-21','yyyy-MM-dd')
        //并且根据字段的配置信息<位置,长度,转换格式,转换内容>,将字段内容转换成对应的内容
        // hmFieldType 字段类型 ,sFields 临时表字段 , cusCfg 接口 ,
        //simpleData 为无序的单条记录数据 存放的时 key=字段 value=字段对应的文本信息
        String sResult = "";
        String sFieldType = "";
        String sValue = "";
        String sFormat = ""; //转换格式
        String sFileContentDict = ""; //转换内容
        int iLoadIndex = 0; //开始位置
        int iLoadLen = 0; //读取长度
        StringBuffer buf = new StringBuffer();
        String[] sField = sFields.split(",");
        DaoFileContentBean field = new DaoFileContentBean();
        field.setYssPub(pub);
        field.setCusCfgCode(cusCfg.getCusCfgCode());
        try {
            for (int i = 0; i < sField.length; i++) {
                if (simpleData.get(sField[i].toUpperCase()) != null) {
                    sValue = (String) simpleData.get(sField[i].toUpperCase());
                } else {
                    sValue = ""; //增加空值的判断功能 add liyu 1103
                }
                field.setTabField(sField[i]);
                field.getSetting();
                sFormat = field.getFormat();
                if (YssFun.isNumeric(field.getLoadIndex())) {
                    iLoadIndex = Integer.parseInt(field.getLoadIndex());
                }
                if (YssFun.isNumeric(field.getLoadLen())) {
                    iLoadLen = Integer.parseInt(field.getLoadLen());
                }
                if (iLoadLen == 0) {
                    iLoadLen = sValue.length(); //默认值为本字段文本内容的长度
                }
                sFileContentDict = field.getFileContentDict();
                if (sFileContentDict == null) {
                    sFileContentDict = "";
                }
                if (getConnectDict(sFileContentDict, sValue).length() > 0 && getConnectDict(sFileContentDict, sValue) != null) {
                    sValue = getConnectDict(sFileContentDict, sValue); //若有配置信息值,将源内容转换成目标内容
                }
                sFieldType = (String) hmFieldType.get(sField[i].trim().toUpperCase());
                if (sFieldType.indexOf("VARCHAR") > -1) {
                    if (sValue.length() > 0) {
                        if (iLoadIndex > -1) {
                            sValue = sValue.substring(iLoadIndex, iLoadLen);
                        }
                        buf.append(dbl.sqlString(sValue)).append(",");
                    } else {
                        buf.append(dbl.sqlString(" ")).append(",");
                    }
                } else if (sFieldType.indexOf("NUMBER") > -1) {
                    if (sValue.length() > 0) {
                        buf.append(sValue).append(",");
                    } else {
                        buf.append("0").append(",");
                    }
                } else if (sFieldType.indexOf("DATE") > -1) {
                    String tmpDate = ""; //临时日期值
                    if (sValue.trim().length() > 0 && sValue.trim() != "null") { //如果日期内容不为空
                        if (YssFun.isDate(sValue)) { //如果是日期
                            if (sFormat != null &&
                                !sFormat.equalsIgnoreCase("null")) //如果是需要转化成在前台设置的格式
                            //如:yyyy-MM-dd或者 yyyyMMdd
                            {
                                buf.append(dbl.sqlDate(YssFun.formatDate(sValue, sFormat))).append(",");
                            } else {
                                buf.append( //用默认的日期格式
                                    dbl.sqlDate(YssFun.toDate(
                                        sValue))).append(",");
                            }
                        } else { //不是日期格式的先转化成日期格式  如:20070905 就要转成 2007-09-05
                            tmpDate = YssFun.left(sValue, 4) + "-" + YssFun.mid(sValue, 4, 2) +
                                "-" + YssFun.right(sValue, 2);
                            if (sFormat != null &&
                                !sFormat.equalsIgnoreCase("null")) {
                                buf.append(dbl.sqlDate(YssFun.formatDate(tmpDate, sFormat))).append(",");
                            } else {
                                buf.append(dbl.sqlDate(YssFun.toDate(tmpDate))).append(",");
                            }
                        }
                    } else { //如果是日期是空的话就存入"1900-01-01" 默认值
                        buf.append(
                            dbl.sqlDate(YssFun.toDate("1900-01-01"))).append(","); //存入一个默认值
                    }
                }
            }
            sResult = buf.toString();
            if (sResult.length() > 0) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("解析字段类型与值关联时出错", e);
        }
    }

    private synchronized String getConnectDict(String sDictCode, String sSrcCont) throws YssException {
        //根据转换代码与原字段将转换的数据取出,实现数据的转换
        // sDictCode 字典编码, sSrcCont 源内容
        //返回 转换内容
        String sResult = "";
        ResultSet rss = null;
        String sqlStr = "";
        try {
            sqlStr = "select FCnvConent from " +
                pub.yssGetTableName("tb_dao_dict") +
                " where FDictCode=" + dbl.sqlString(sDictCode) +
                " and FSrcConent=" + dbl.sqlString(sSrcCont);
            rss = dbl.openResultSet(sqlStr);
            while (rss.next()) {
                sResult = rss.getString("FCnvConent");
            }
            rss.close();
            return sResult;
        } catch (Exception e) {
            throw new YssException("文件内容转换出错!", e);
        }
    }

    private String getTmpTabFields(String tabName) throws YssException {
        //返回临时表的字段,返回 "aa,bb,cc..."串  tabName 为临时表
        ResultSet rs = null;
        ResultSetMetaData data = null;
        String sqlStr = "";
        StringBuffer buf = new StringBuffer();
        try {
            sqlStr = " select * from " + tabName;
            rs = dbl.openResultSet(sqlStr);
            data = rs.getMetaData();
            for (int i = 1; i <= data.getColumnCount(); i++) {
                buf.append(data.getColumnName(i)).append(",");
            }
            rs.close();
            if (buf.toString().length() > 0) {
                buf.delete(buf.length() - 1, buf.length());
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("解析临时表字段出错", e);
        }
    }

    private HashMap getTmpTabValue(String fields, String simpleData) throws YssException {
        //将临时表字段与内容信息一一对应起来
        // fields 字段集 , simpleData 单条记录内容
        //返回 key=字段 value=字段内容
        String[] sField = fields.split(",");
        String[] sData = simpleData.split("\t");
        HashMap htFields = new HashMap();
        HashMap hmValue = new HashMap();
        int iIndex = 0;
        int iLen = 0;
        String stmpField = "";
        try {
            sFileType = sData[0].substring(1, sData[1].indexOf(">") + 1);
            //1:将值内容放入到一个HashMap中
            for (int i = 0; i < sData.length; i++) {
                if (sData[i].lastIndexOf("<") > -1) {
                    iIndex = sData[i].lastIndexOf("<");
                }
                if (sData[i].lastIndexOf(">") > -1) {
                    iLen = sData[i].lastIndexOf(">");
                } else {
                    iLen = sData[i].length(); //默认为内容的长度
                }
                stmpField = sData[i].substring(iIndex + 1, iLen);
                if (iLen == iIndex) {
                    stmpField = sData[i].substring(1, iLen);
                }
                hmValue.put(stmpField, sData[i].substring(iLen + 1, sData[i].length()));
            }
            //2:根据 sField值,取出对应的值内容
            for (int j = 0; j < sField.length; j++) {
                stmpField = (String) hmValue.get(sField[j]);
                htFields.put(sField[j].toUpperCase(), stmpField);
            }
            return htFields;
        } catch (Exception ex) {
            throw new YssException("字段与字段内容一一对应出错", ex);
        }
    }

    private String DBFieldFormat(String sFields) throws YssException {
        //本方法用于处理DataBase中的关键字,如 Date,Function...字段
        //Oracle 中加 "Date"
        //sql server 中加 [Date]
        //db2 中加
        /*database,datafile,datafiles,date,function,functions,primary,private,public,procedure
         profile,create,table,identified by,order,or,group,groups
         */
        StringBuffer buf = new StringBuffer();
        String sResult = "";
        String sSplit = "";
        String[] arrField = null;
        try {
            if (sFields.indexOf(",") > -0) {
                arrField = sFields.split(","); //处理","分隔的字符串
                sSplit = ",";
            } else if (sFields.indexOf("\t") > 0) {
                arrField = sFields.split("\t"); //处理"\t"分隔的字符串
                sSplit = "\t";
            } else {
                arrField = new String[1];
                arrField[0] = sFields;
            }
            for (int i = 0; i < arrField.length; i++) {
                if (dbl.dbType == YssCons.DB_ORA) {
                    if (arrField[i].equalsIgnoreCase("data")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("date")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("function")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("table")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("group")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("private")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("public")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("or")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("by")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else {
                        buf.append(arrField[i]);
                    }
                }
                buf.append(sSplit);
            }
            sResult = buf.toString();
            if (sResult.length() > 0) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("处理数据库字段时出错", e);
        }
    }

    private void insertData(String sFields, String paraValue, String tabName, HashMap htFieldValue) throws YssException {
        //做SQL语句,将数据插入到临时表
        //sFields 为要插入的字段,paraValue 为插入的值,tabName 为要插入的表,htFieldValue 根据列对应的 HashMap值
        String sqlStr = "";
        //     String[] pkVals=null,condVals=null,condInsVals=null,condDelVals=null,connVals=null;
        Connection conn = null;
        try {
            conn = dbl.loadConnection();
            /*    if(this.readXMLConfig()==false)
                   throw new YssException("解析XML外部配置文件出错,请检查配置文件配置是否正确!");
                if(pkVal.trim().length()>0){
                   if(pkVal.indexOf("\t")>-1)
                      pkVals=pkVal.split("\t");
                   else{
                      pkVals = new String[1];
                      pkVals[0]= pkVal;
                   }
                }
                if(condVal.trim().length()>0){
                   if(condVal.indexOf("\t")>-1)
                      condVals=condVal.split("\t");
                   else{
                      condVals=new String[1];
                      condVals[0]=condVal;
                   }
                }
                if(condInsVal.trim().length()>0){
                   if(condInsVal.indexOf("\t")>-1)
                      condInsVals =condInsVal.split("\t");
                   else{
                      condInsVals=new String[1];
                      condInsVals[0]=condInsVal;
                   }
                }
                if(condDelVal.trim().length()>0){
                   if(condDelVal.indexOf("\t")>-1)
                      condDelVals=condDelVal.split("\t");
                   else{
                      condDelVals=new String[1];
                      condDelVals[0]=condDelVal;
                   }
                }
                if(connVal.trim().length()>0){
                   if(connVal.indexOf("\t")>-1)
                      connVals=connVal.split("\t");
                   else{
                      connVals=new String[1];
                      connVals[0]=connVal;
                   }
                }
                for(int i=0;i<condVals.length;i++){
                   //根据条件字段做插入与删除操作
                   if(condVals[i].trim().length()>0){
                      if (htFieldValue.get(condVals[i].toUpperCase()).equals(condInsVals[i])){ //插入操作
                         deleteData(tabName,htFieldValue,pkVals,pkVals); //删除本条记录
             */
            // sqlStr="delete from "+tabName;
            //  dbl.executeSql(sqlStr);
            sqlStr = "insert into " + tabName + "(" +
                DBFieldFormat(sFields) + ") values (" +
                paraValue + ")";
            dbl.executeSql(sqlStr);
            /*     }else if(htFieldValue.get(condVals[i].toUpperCase()).equals(condDelVals[i])){  //删除操作
                    deleteData(tabName,htFieldValue,pkVals,connVals); //删除关联记录
                 }

              }
                      } */
            conn.commit();
        } catch (Exception e) {
            throw new YssException("插入数据到临时表出错", e);
        } finally {
            dbl.endTransFinal(conn, false);
        }
    }

    private void deleteData(String tabName, HashMap htFieldValue, String[] pkField, String[] connField) throws YssException {
        //根据条件删除临时表的记录
        //htFieldValue 为字段值,
        //pkField 主键字段集,
        //connField 关联字段集
        String sqlStr = "";
        String pkStr = "";
        try {
            for (int i = 0; i < pkField.length; i++) {
                if (pkField[i].trim().length() > 0) {
                    pkStr += " and " + pkField[i] + "=" +
                        dbl.sqlString( (String) htFieldValue.get(connField[i].toUpperCase())); //根据关键字段取出值
                }
            }
            sqlStr = " delete from " + tabName + " where 1=1 " + pkStr;
            dbl.executeSql(sqlStr);
        } catch (Exception e) {
            throw new YssException("删除临时表数据出错", e);
        }
    }

    private void deleteTmp(String sTabName) throws YssException {
        // 一次性删除临时表里的数据
        Connection conn = dbl.loadConnection();
        try {
            String str = "delete from " + sTabName;
            dbl.executeSql(str);
            conn.commit();
        } catch (Exception e) {
            throw new YssException("删除临时表的数据出错", e);
        } finally {
            dbl.endTransFinal(conn, false);
        }
    }
} //end bean
