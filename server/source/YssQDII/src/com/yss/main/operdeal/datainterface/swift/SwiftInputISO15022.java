package com.yss.main.operdeal.datainterface.swift;

import com.yss.util.*;
import com.yss.main.dao.IDataSetting;
import com.yss.main.datainterface.swift.*;

import java.sql.*;
import java.util.*;

/**
 * QDV4赢时胜（深圳）2009年5月12日01_A MS00455
 * ISO15022标准的报文导入实现类
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author by leeyu 20090608
 * @version 1.0
 */
public class SwiftInputISO15022
    extends BaseSwiftInputOper {
    //private ArrayList alSavelist =null; //用于存放保存的报文文件信息的
    //private ArrayList alCyclelist=null; //保存模板中循环的记录
    //private HashMap hmTmpField;         //用于保存临时表字段及值
    public SwiftInputISO15022() {
    }

    /**
     * 按type类型解析数据
     * @param sResStr String
     * @param type String
     * @throws YssException
     */
    public void parseReqsRow(String sResStr, String type) throws YssException {
        String[] arrRes = null;
        if (type.equalsIgnoreCase("import")) {
            operDatas = sResStr;
        } else if (type.equalsIgnoreCase("parse")) { //解析一行从前台传过来的SWIFT落地文件等信息
            arrRes = sResStr.split("\t");
            SaveSwiftContentBean content = new SaveSwiftContentBean();
            content.setSwiftDate(arrRes[0]);
            content.setFileName(arrRes[1]);
            content.setSwiftType(arrRes[4]);
            content.setSwiftText(arrRes[7]);
            content.setReflow(arrRes[9]);
            content.setSwiftNum(arrRes[10]);
            content.setSwiftIndex(arrRes[11]);
            content.setPortCode(portCode);
            content.setCheckState(1); //导入时默认为审核状态
            content.setSwiftStatus(swiftStatus);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            swiftType =content.getSwiftType();
            DaoSwiftSet daoSwift=new DaoSwiftSet();
            daoSwift.setYssPub(pub);
            daoSwift.setSwiftType(swiftType);
            daoSwift.setSSwiftStatus(swiftStatus);
            daoSwift.setSReflow(arrRes[9]); //2010.05.10 add by jiangshichao 
            daoSwift.getSetting();
            content.setSwiftCode(daoSwift.getsWiftCode());
            //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            alSavelist.add(content);
        }
    }

    /**
     * 导入报文时：加载导入流向报文信息(包括报文类型、业务类型、报文路径、导入报文名),返回前台并显示的列信息,以便前台根据文件名读数据
     * @return String
     * @throws YssException
     */
    public String loadSWIFTListView() throws YssException {
        //1:查找所有报文流向为导入的报文模板，并取出业务类型、报文路径、报文类型
        //2:从报文原文表中取出当日所有已导入的报文，取出信息：报文名(唯一识别是哪个报文的)
        //单行记录返回headerCode数据格式
        String swiftName = ""; //导入的Swift文件名
        ResultSet rs = null;
        String sqlStr = "";
        StringBuffer bufResult = new StringBuffer(); //保存结果的
        try {
            //1:根据条件获取所有流向为导入的SWIFT报文模板
        	//edit by licai 20101221 BUG #668 Swift报文导入后执行状态显示有问题 
            /*sqlStr = "select a.*,b.FVocName as FOperTypeName,c.FSwiftIndex,c.FDate  from " + pub.yssGetTableName("Tb_Dao_Swift") +*/
            sqlStr = "select a.*,b.FVocName as FOperTypeName,c.FSwiftIndex,c.FDate,c.FFullFileName  from " + pub.yssGetTableName("Tb_Dao_Swift") +
            //edit by licai 20101221 BUG #668 =============================end
                " a left join (select FVocCode, FVocName from tb_fun_vocabulary where FVocTypeCode = '" + YssCons.YSS_SWIFT_OPERTYPE +
                "') b on a.fopertype = b.Fvoccode " +
            	//edit by licai 20101221 BUG #668 Swift报文导入后执行状态显示有问题
                /*" left join (select distinct FSwiftIndex, fSwiftType,FSwiftStatus,FSwiftCode,FDate from " + pub.yssGetTableName("Tb_Data_Originalswift") +*///by leeyu 2010104 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                " left join (select distinct FFullFileName,FSwiftIndex, fSwiftType,FSwiftStatus,FSwiftCode,FDate from " + pub.yssGetTableName("Tb_Data_Originalswift") +//by leeyu 2010104 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                //edit by licai 20101221 BUG #668 =============================end
                " where FReflow = 'in' "+(YssFun.formatDate(startDate, "yyyy-MM-dd").equals("9998-12-31")||YssFun.formatDate(startDate,"yyyy-MM-dd").equalsIgnoreCase("1900-01-01")?"": "and FDate = " + dbl.sqlDate(startDate)) +//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                " ) c on a.FSwiftType = c.FSwiftType and a.FSwiftStatus=c.FSwiftStatus and a.FSwiftCode=c.FSwiftCode " +//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                " where 1=1 and a.FCheckState=1 and a.FSWIFTSTATUS='NEW'" +//添加上审核条件 by leeyu 20100104
                (swiftReflow != null && swiftReflow.length() > 0 ? " and a.FReflow =" + dbl.sqlString(swiftReflow) : "") +
                ( (swiftOperType != null && swiftOperType.length() > 0 && !swiftOperType.equalsIgnoreCase("99")) ? " and a.FOperType=" + dbl.sqlString(swiftOperType) : "") +
                ( (swiftStandard != null && swiftStandard.length() > 0 && !swiftStandard.equalsIgnoreCase("99")) ? " and a.FCriterion=" + dbl.sqlString(swiftStandard) : "") +
                (swiftType != null && swiftType.length() > 0 ? " and a.FSwiftType=" + dbl.sqlString(swiftType) : "");
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	startDate =rs.getDate("FDate")==null?YssFun.toDate("1900-01-01"):rs.getDate("FDate");//by leeyu 20100104 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                swiftName = getFileNameStr(rs.getString("FSwiftType"),rs.getString("FSwiftCode"));
                rowData[0] = ""; //编号，暂为空
                rowData[1] = rs.getString("FSwiftType"); //报文类型
                rowData[2] = YssFun.formatDate(startDate, "yyyy-MM-dd"); //导入日期
                rowData[3] = ""; //报文状态
                rowData[4] = ""; //执行状态暂为空
                rowData[5] = rs.getString("FSwiftDesc"); //摘要
                bufResult.append(this.buildRowStr()).append("\r\f"); //1:单行数据信息
                bufResult.append(rs.getString("FOperType")).append("\t");
                bufResult.append(rs.getString("FOperTypeName")).append("\r\f"); //2:分组信息，这里取业务类型数据
                bufResult.append(swiftName).append("\r\f"); //3:导入的配置文件名信息
                //edit by licai 20101221 BUG #668 Swift报文导入后执行状态显示有问题 
                bufResult.append(rs.getString("FFullFileName")).append("\r\f"); //4:已导入的原文文件名信息
                //edit by licai 20101221 BUG #668
                bufResult.append(rs.getString("FCriterion")).append("\t"); //5:其他项，包括：报文标准，报文正文
                bufResult.append(rs.getString("FSwiftIndex"));
                bufResult.append("\r\b\n");
            }
            if (bufResult.length() > 3) {
                bufResult.setLength(bufResult.length() - 3);
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return bufResult.toString();
    }

    /**
     * 获取已保存到SWIFT原文表的SWIFT文件名
     * @param swiftType SWIFT类型代码
     * @return String
     * @throws YssException
     */
    private String getOriginalSWIFTName(String swiftType,String swiftStatus) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        StringBuffer buf;
        try {
            buf = new StringBuffer();
            sqlStr = "select distinct FFullFileName from " + pub.yssGetTableName("Tb_Data_OriginalSWIFT") +
                " where Freflow = 'in' " + //这里取导入的数据类型
                " and FSwiftType = " + dbl.sqlString(swiftType) +
                " and FSwiftStatus ="+dbl.sqlString(swiftStatus)+
                ((YssFun.formatDate(endDate,"yyyy-MM-dd").equalsIgnoreCase("1900-01-01")||
                YssFun.formatDate(endDate,"yyyy-MM-dd").equalsIgnoreCase("9998-12-31"))?"":(//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                " and (FDate between " + dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate) + ") "));
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                buf.append(rs.getString("FFullFileName")).append("\r\n");
            }
        } catch (Exception ex) {
            throw new YssException("获取SWIFT【" + swiftType + "】类型的原文表名出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return buf.toString();
    }

    /**
     * 解析数据,将一行报文原文解析成报文实体相关数据
     * 可用于导入SWIFT报文时的处理
     * @param objData 一行报文原文数据
     * @throws YssException
     */
    public IDataSetting parseEntityStr(Object objData) throws YssException {
        //根据ISO15022标准，解析数据，并到 DaoSwiftEntitySet中
        String sData = (String) objData;
        DaoSwiftEntitySet swiftEntityData = new DaoSwiftEntitySet(); //解析后存放值的
        //Do parse something
        String[] arrFormat = sData.split(":");
        if (swiftType.toUpperCase().startsWith("MT5")) { //MT5 类型的解析
        	//edit by songjie 2011.04.12 BUG 1644 QDV4建行2011年03月31日01_B
            if (arrFormat.length >= 4) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                //---add by songjie 2011.04.12 BUG 1644 QDV4建行2011年03月31日01_B---//
                if(arrFormat.length > 4){
                	int j = 0;
                	for(int i = 0; i < sData.length(); i++){
                		if(sData.charAt(i) == ':'){
                			j++;
                		}
                		if(j == 3){
                			arrFormat[3] = sData.substring(i + 1,sData.length());
                			break;
                		}
                	}
                }
                //---add by songjie 2011.04.12 BUG 1644 QDV4建行2011年03月31日01_B---//
                if (arrFormat[2].length() == 0) {
                    if (arrFormat[3].indexOf("/") > -1) {
                        swiftEntityData.setSQualifier(arrFormat[3].substring(0, arrFormat[3].indexOf("/")));
                        swiftEntityData.setSContent(arrFormat[3].substring(arrFormat[3].indexOf("/")));
                    } else {
                        swiftEntityData.setSQualifier("");
                        swiftEntityData.setSContent(arrFormat[3]);
                    }
                } else {
                    swiftEntityData.setSQualifier(arrFormat[2]);
                    swiftEntityData.setSContent(arrFormat[3]);
                }
            } else if (arrFormat.length == 3) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSContent(arrFormat[2]);
                swiftEntityData.setSQualifier("");
            } else if (arrFormat.length == 2) {
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSQualifier("");
                swiftEntityData.setSContent("");
            }
        } else if (swiftType.toUpperCase().startsWith("MT3")) { //MT3 类型的解析
            if (arrFormat.length == 4) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                if (arrFormat[2].length() == 0) {
                    swiftEntityData.setSContent(arrFormat[3]);
                    swiftEntityData.setSQualifier("");
                } else {
                    swiftEntityData.setSQualifier(arrFormat[2]);
                    swiftEntityData.setSContent(arrFormat[3]);
                }
            } else if (arrFormat.length == 3) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSContent(arrFormat[2]);
                swiftEntityData.setSQualifier("");
            } else if (arrFormat.length == 2) {
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSQualifier("");
                swiftEntityData.setSContent("");
            }
        } else if (swiftType.toUpperCase().startsWith("MT9")) { //MT9 类型的解析
            if (arrFormat.length == 4) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                if (arrFormat[2].length() == 0) {
                    swiftEntityData.setSContent(arrFormat[3]);
                    swiftEntityData.setSQualifier("");
                } else {
                    swiftEntityData.setSQualifier(arrFormat[2]);
                    swiftEntityData.setSContent(arrFormat[3]);
                }
            } else if (arrFormat.length == 3) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSContent(arrFormat[2]);
                swiftEntityData.setSQualifier("");
            } else if (arrFormat.length == 2) {
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSQualifier("");
                swiftEntityData.setSContent("");
            }
        } else if (swiftType.toUpperCase().startsWith("MT1")) { //MT1 类型的解析
            if (arrFormat.length == 4) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                if (arrFormat[2].length() == 0) {
                    swiftEntityData.setSContent(arrFormat[3]);
                    swiftEntityData.setSQualifier("");
                } else {
                    swiftEntityData.setSQualifier(arrFormat[2]);
                    swiftEntityData.setSContent(arrFormat[3]);
                }
            } else if (arrFormat.length == 3) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSContent(arrFormat[2]);
                swiftEntityData.setSQualifier("");
            } else if (arrFormat.length == 2) {
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSQualifier("");
                swiftEntityData.setSContent("");
            }
        } else {

        }
        return swiftEntityData;
    }

    /**
     * 根据一行报文模板信息生成一行报文数据
     * 可用于导出SWIFT报文时的处理
     * 导入方法里，只处理编写处理第一行数据
     * @param entity 报文模板
     * @param objData 数据信息
     * @return String 返回一条完整的报文数据，包括必要的报文开头，报文数据
     * @throws YssException
     */
    public String buildEntityStr(IDataSetting entity, Object objData) throws
        YssException {
        StringBuffer buf = new StringBuffer();
        DaoSwiftEntitySet entitySet = (DaoSwiftEntitySet) entity;
        buf.append(":").append(entitySet.getSTag().trim());
        buf.append(":");
        if (entitySet.getSContent() != null && entitySet.getSContent().trim().length() > 0) {
            buf.append(entitySet.getSContent().trim());
        } else {
            if (entitySet.getSOption().startsWith(":")) {
                buf.append(":");
            }
            if (entitySet.getSQualifier() != null && entitySet.getSQualifier().trim().length() > 0) {
                buf.append(entitySet.getSQualifier().trim());
            }
        }
//      if(swiftType.toUpperCase().startsWith("MT5")){        //MT5 类型的处理
//         buf.append(":").append(entitySet.getSTag());
//         if(entitySet.getSContent()!=null && entitySet.getSContent().trim().length()>0){
//            buf.append(":").append(entitySet.getSContent().trim());
//         }else{
//            buf.append(":").append(entitySet.getSOption().trim());
//         }
//      }else if(swiftType.toUpperCase().startsWith("MT3")){  //MT3 类型的处理
//         buf.append(":").append(entitySet.getSTag());
//         if(entitySet.getSContent()!=null && entitySet.getSContent().trim().length()>0){
//            if(!entitySet.getSContent().equalsIgnoreCase("CrLf")||!entitySet.getSContent().equalsIgnoreCase("(CrLf)"))
//               buf.append(":").append(entitySet.getSContent().trim());
//         }else{
//            if(!entitySet.getSOption().equalsIgnoreCase("CrLf")||!entitySet.getSOption().equalsIgnoreCase("(CrLf)"))
//               buf.append(":").append(entitySet.getSOption().trim());
//         }
//      }else if(swiftType.toUpperCase().startsWith("MT9")){  //MT9 类型的处理
//         buf.append(":").append(entitySet.getSTag());
//         if(entitySet.getSContent()!=null && entitySet.getSContent().trim().length()>0){
//            buf.append(":").append(entitySet.getSContent().trim());
//         }else{
//            buf.append(":").append(entitySet.getSOption().trim());
//         }
//      }else if(swiftType.toUpperCase().startsWith("MT1")){  //MT1 类型的处理
//         buf.append(":").append(entitySet.getSTag());
//         if(entitySet.getSContent()!=null && entitySet.getSContent().trim().length()>0){
//            buf.append(":").append(entitySet.getSContent().trim());
//         }else{
//            buf.append(":").append(entitySet.getSOption().trim());
//         }
//      }else{
//
//      }
        return buf.toString();
    }

    /**
     * 查询报文信息
     * @return String
     * @throws YssException
     */
    public String querySWIFTList() throws YssException {
        //1:从报文原表中查询出报文信息：返回headerCode数据格式
        //1:查找所有报文流向为导入的报文模板，并取出业务类型、报文路径、报文类型
        //2:从报文原文表中取出当日所有已导入的报文，取出信息：报文名(唯一识别是哪个报文的)
        //单行记录返回headerCode数据格式
        //String originalName="";//原文Swift文件名
        String swiftName = ""; //导入的Swift文件名
        ResultSet rs = null;
        String sqlStr = "";
        StringBuffer bufResult = new StringBuffer(); //保存结果的
        try {
            //1:根据条件获取所有流向为导入的SWIFT报文模板
            sqlStr = "select a.*, b.FOpertype, b.FOperTypeName, b.FCriterion,b.FSwiftDesc  from " +
                pub.yssGetTableName("Tb_Data_OriginalSwift") + " a " +
                " left join (select b1.fswiftcode,b1.FSwiftDesc,b1.FSwiftType,b1.FOperType,b1.FCriterion,b2.FVocName as FOperTypeName from " +
                pub.yssGetTableName("Tb_Dao_Swift") + " b1 left join (select FVocCode, FVocName from Tb_Fun_Vocabulary where FVocTypeCode = '" +
                YssCons.YSS_SWIFT_OPERTYPE + "') b2 " +
                " on b1.FOperType = b2.FVocCode) b on a.Fswifttype = b.FSwifttype  and a.fswiftcode = b.fswiftcode where 1=1 " +
                (swiftReflow != null && swiftReflow.length() > 0 ? " and a.FReflow =" + dbl.sqlString(swiftReflow) : "") +
                ( (swiftOperType != null && swiftOperType.length() > 0 && !swiftOperType.equalsIgnoreCase("99")) ? " and b.FOperType=" + dbl.sqlString(swiftOperType) : "") +
                ( (swiftStandard != null && swiftStandard.length() > 0 && !swiftStandard.equalsIgnoreCase("99")) ? " and b.FCriterion=" + dbl.sqlString(swiftStandard) : "") +
                (swiftType != null && swiftType.length() > 0 ? " and a.FSwiftType=" + dbl.sqlString(swiftType) : "") +
                ( (YssFun.formatDate(startDate, "yyyy-MM-dd").equals("9998-12-31") || YssFun.formatDate(startDate, "yyyy-MM-dd").equals("1900-01-01")) ? " " : " and a.FDate= " + dbl.sqlDate(startDate));
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                //originalName = getOriginalSWIFTName(rs.getString("FSwiftType"));
            	startDate= rs.getDate("FDate");//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                rowData[0] = rs.getString("FSwiftNum"); //编号
                rowData[1] = rs.getString("FSwiftType"); //报文类型
                rowData[2] = YssFun.formatDate(startDate, "yyyy-MM-dd"); //导入日期
                rowData[3] = ""; //报文状态暂为空
                rowData[4] = ""; //执行状态暂为空
                rowData[5] = rs.getString("FSwiftDesc"); //摘要
                bufResult.append(this.buildRowStr()).append("\r\f"); //1:单行数据信息
                bufResult.append(rs.getString("FOperType")).append("\t");
                bufResult.append(rs.getString("FOperTypeName")).append("\r\f"); //2:分组信息，这里取业务类型数据
                bufResult.append(swiftName).append("\r\f"); //3:导入的配置文件名信息
                bufResult.append(rs.getString("FFullFileName")).append("\r\f"); //4:已导入的原文文件名信息
                bufResult.append(rs.getString("FCriterion")).append("\t"); //5:其他项，包括：报文标准，报文正文
                bufResult.append(rs.getString("FSwiftIndex")).append("\t");
                bufResult.append(dbl.clobStrValue(rs.getClob("FText")));
                bufResult.append("\r\b\n");
            }
            if (bufResult.length() > 3) {
                bufResult.setLength(bufResult.length() - 3);
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return bufResult.toString();
    }

    /**
     * 解析SWIFT并保存到临时表里
     * 解析过程：
     * 对于ISO15022格式的报文采用，读出报文内容原文，然后解析出单行，再将单行报文解析成报文模板对应数据;
     * 再拿解析后的数据在报文模板查数据，如果在报文模板中查出数据有则说明数据正确，
     * 此时就可以将数据存入到模板临时表字段里
     * @return String
     * @throws YssException
     */
    public String parseSaveSWIFT() throws YssException {
        //1:先将报文原文存放在报文原文表中。
        //2:查报文原文表中的数据，根据上面的解析过程解析数据，其步骤为：
        //  A:解析报文原文，取出报文主体内容，并读出报文类型
        //  B:根据报文类型取出报文模板(可放到ArrayList中,item=DaoSwiftEntitySet)
        //  C:取出报文模板第一行,并生成第一行的数据,可通过buildEntityStr()方法生成.
        //  D:在报文主体内容变量中找到开始头部信息,并截取从头到尾的报文内容块.
        //  E:用\r\n将报文内容分开,并放到报文内容数组中.
        //  F:遍历报文内容数组,查数组头以":"开关的报文,如果数组头不以":"开头就认为是上一行的数据
        //  G:将数据解析放到DaoSwiftEntitySet中,可通过parseEntityStr()方法实现
        //  H:遍历报文模板,如果数据存在于报文模板中,则可将数据放入到临时表字段的HashMap中(key=临时表字段,value=swift报文内容);
        //              注:此时需查一下HashMap,如果键已存在,则提示报文数据重复,与邹确定说同条报文数据不可能重复,而这里有可能是模板临时表字段设置重复了
        //              如果数据不与模板对应,则提示错误信息:抛出准确的提示,包括两方面:内容与模板
        //  I:根据临时表HashMap表生成可insert 临时表的SQL语句,再将值一一对应于临时表字段,执行插入数据操作.
        //3:插入临时表失败或系统报错则回滚数据并删除本条报文原文件中的报文数据
        String sSuccess = "success"; //返回值，success：为成功
        String[] arrRes = null; //SWIFT行数据
        String[] arrPrep = null; //保存预处理代码的
        Connection conn = null;
        ArrayList alTemp = null; //保存SWIFT模板信息
        SaveSwiftContentBean content;
        DaoSwiftSet swift;
        ArrayList alSWIFTType = null; //保存SWIFT类型的,用于删除临时表数据时用，防止删除多余的数据
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            alSWIFTType = new ArrayList();
            alSavelist = new ArrayList();
            arrRes = operDatas.split("\n\r");
            for (int i = 0; i < arrRes.length; i++) {
                parseReqsRow(arrRes[i], "parse");
            }
            //saveSwift.setConn(conn);
            saveSwift.set(alSavelist);
            conn.setAutoCommit(bTrans);
            bTrans = true;
            saveSwift.insert(true);
            //取出SWIFT模板信息
            //循环每行报文数据
            for (int iRow = 0; iRow < alSavelist.size(); iRow++) {
                content = (SaveSwiftContentBean) alSavelist.get(iRow);
                swiftType = content.getSwiftType();
                swift = new DaoSwiftSet();
                swift.setYssPub(pub);
                swift.setSwiftType(swiftType);                
                swift.setSSwiftStatus(content.getSwiftStatus());
                swift.setSReflow(content.getReflow());
                swift.getSetting();
                alTemp = getSwiftTemp(content.getSwiftType(),content.getSwiftStatus(),content.getSwiftCode());
                if (swiftType.startsWith("MT545")) { //对MT5系列的报文的单独处理
                    //原因是MT5系列的报文存在一个报文多笔同种业务的情况。这里需单独处理
                    SwiftInputISO15022 baseISO = (SwiftInputISO15022) pub.
                        getDataInterfaceCtx().getBean(swiftType.toLowerCase());
                    baseISO.setYssPub(pub);
                    baseISO.alSavelist = alSavelist;
                    content = baseISO.splitDatas(content, alTemp); //通过替换，将解析后新的数据替换掉旧的数据
                    alSavelist = baseISO.alSavelist;
                }
                hmTmpField = new HashMap();
                parseContent(content, alTemp);
                //根据SWIFT类型先做一些必要的处理
                if (!alSWIFTType.contains(content.getSwiftType())) {
                    alSWIFTType.add(content.getSwiftType());
                    //1:检查或是创建SWIFT模板配置的临时表
                    checkOrCreateTable(getYssTableName(swift.getSTableCode()));
                    //2:删除SWIFT模板配置的临时表数据
                    deleteSWIFTTab(swift.getSTableCode()); //如果有重复类型数据时不执行删除操作
                }
                //3:插入数据到SWIFT模板的临时表中。
                insertSWIFTTab(swift.getSTableCode());
                arrPrep = swift.getSDSCode().split(",");
                for (int iPrep = 0; iPrep < arrPrep.length; iPrep++) { //循环预处理，执行预处理操作
                    if (arrPrep[iPrep].trim().length() == 0) {
                        continue;
                    }
                    baseOperDeal.init(content.getPortCode());
                    baseOperDeal.doOnePretreat(arrPrep[iPrep]);
                }
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            sSuccess = "failure"; //失败的
            throw new YssException("数据保存不成功!", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return sSuccess;
    }

    /**
     * 获取报文模板的起始位置,并返回
     * @param alSwiftTemp ArrayList
     * @return DaoSwiftEntitySet
     * @throws YssException
     */
    protected DaoSwiftEntitySet getStartRowTemp(ArrayList alSwiftTemp) throws YssException {
        DaoSwiftEntitySet entitySet = null;
        for (int i = 0; i < alSwiftTemp.size(); i++) {
            entitySet = (DaoSwiftEntitySet) alSwiftTemp.get(i);
            if (entitySet.getSIndex().equals("1") &&
                (entitySet.getSStatus().equals("M") || entitySet.getSStatus().equals("O"))) {
                break;
            }
        }
        return entitySet;
    }

    //--------------------------------------------------------------------------//
    /**
     * 判断内容值是否在模板中，若在返回TRUE，不在返回FALSE
     * 另：往临时表字段插入值
     * @param alSwiftTemp ArrayList
     * @param dataEntity DaoSwiftEntitySet
     * @return boolean
     * @throws YssException
     */
    protected boolean queryTemp(ArrayList alSwiftTemp, DaoSwiftEntitySet dataEntity) throws YssException {
        boolean bCheck = false; //默认为假，没有
        String sTempData = ""; //存放最终内容数据
        DaoSwiftEntitySet entity;
        for (int i = 0; i < alSwiftTemp.size(); i++) {
            entity = (DaoSwiftEntitySet) alSwiftTemp.get(i);
            if (entity.getSTag().equalsIgnoreCase(dataEntity.getSTag()) && //这里比较模板的标记
                (entity.getSQualifier().trim().equalsIgnoreCase(dataEntity.getSQualifier()) || //比较模板限定符
                 entity.getSQualifier().equalsIgnoreCase(dataEntity.getSQualifier().trim().length() + "!c") ||
                 entity.getSQualifier().equalsIgnoreCase(dataEntity.getSQualifier().trim().length() + "c") ||
                 entity.getSContent().trim().equalsIgnoreCase(dataEntity.getSContent()))) { //比较模板内容
                sTempData = dataEntity.getSContent(); //取内容数据
                if (entity.getSQualifier() != null && entity.getSQualifier().trim().length() > 0) { //这里判断限定符
                    //if(sTempData.trim().length()>entity.getSQualifier().trim().length())
                    //sTempData =sTempData.substring(entity.getSQualifier().length());//这里截掉有限定符开头的数据
                }
                if (sTempData.startsWith("//")) { //去除数据前的单斜线或双斜线
                    sTempData = sTempData.substring(2);
                } else if (sTempData.startsWith("/")) {
                    sTempData = sTempData.substring(1);
                }
                if (sTempData.indexOf(",") > -1) { //如果内容中有逗号的话，将最后一个逗号转换成小数点，并去掉前面的逗号。
                    String[] arrTmp = sTempData.split(",");
                    sTempData = "";
                    for (int it = 0; it < arrTmp.length; it++) {
                        if (it == arrTmp.length - 1) {
                            sTempData = sTempData + "." + arrTmp[it];
                        } else {
                            sTempData = sTempData + arrTmp[it];
                        }
                    }
                    if (sTempData.startsWith(".")) {
                        sTempData = sTempData.substring(1);
                    }
                }
                System.out.println(sTempData);
                if (entity.getSTableField() != null && entity.getSTableField().trim().length() > 0) {
                    hmTmpField.put(entity.getSTableField().toUpperCase(), sTempData); //将数据放到HashMap中
                    alSwiftTemp.remove(i); //将此条删除掉，以免下次查的时候再查到它
                }
                bCheck = true;
                break;
            }
        }
        return bCheck;
    }

    /**
     * 解析报文数据
     * @param sContent String
     * @param alSwiftTemp ArrayList
     * @throws YssException
     */
    protected void parseContent(SaveSwiftContentBean saveContent, ArrayList alSwiftTemp) throws YssException {
        String[] arrCon = null;
        String sRowStr = "";
        boolean bCheck = false; //检查是否存在
        int firstRow = 0;
        int beginRow = 0;
        try {
            arrCon = saveContent.getSwiftText().split("\r\n"); //根据换行来处理，注：起始行应以":"开始
            //判断一下第一行情况,防止前台解析的数据过来有问题、出错
            for (firstRow = 0; firstRow < arrCon.length; firstRow++) {
                String sFirstTempStr = "";
                DaoSwiftEntitySet firRowEntity = getStartRowTemp(alSwiftTemp);
                sFirstTempStr = buildEntityStr(firRowEntity, null);
                if (arrCon[firstRow].startsWith(sFirstTempStr)) {
                    break;
                }
                if (firstRow == arrCon.length - 1) {
                    throw new YssException("编号为【" + saveContent.getSwiftNum() + "】报文原文数据不正确!");
                }
            }
            //循环原文文件，查找每一行报文的模板
            for (int iRow = firstRow; iRow < arrCon.length; iRow++) {
                if (!arrCon[iRow].startsWith(":")) { //如果数据不以":"开始的话就读下一行，因为本行数据是上一个模板的数据部分
                    continue;
                } else {
                    beginRow = iRow;
                }
                sRowStr = getRowContent(arrCon, beginRow);
                bCheck = queryTemp(alSwiftTemp, (DaoSwiftEntitySet) parseEntityStr(sRowStr));
                if (bCheck) {
                    continue;
                } else {
                	throw new YssException("报文模板为【" + saveContent.getSwiftType() + "】,序号为【" + saveContent.getWwiftIndex() + "】报文数据【"+sRowStr+"】解析处理后没有找到对应的配置模板。\r\n请检查数据或模板配置！");
                    //throw new YssException("报文模板为【" + saveContent.getSwiftType() + "】,序号为【" + saveContent.getWwiftIndex() + "】数据解析处理后没有找到对应的配置模板。\r\n请检查数据或模板配置！");
                }
            }
        } catch (Exception ex) {
            throw new YssException(ex);
        }
    }
    
    /**
     * 解析报文数据到HashMap表中
     * @param saveContent
     * @param alSwiftTemp
     * @return
     * @throws YssException
     */
    public HashMap getSwiftContent(SaveSwiftContentBean saveContent, ArrayList alSwiftTemp) throws YssException{
    	hmTmpField =new HashMap();
    	try{
    		parseContent(saveContent,alSwiftTemp);
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage(),ex);
    	}
    	return hmTmpField;
    }
    /**
     * 通过模板解析将原有的报文数据解析成多条报文记录
     * 将第1-N条报文再放入报文List中，
     * 将第0条报文返回，
     * 这样完成一条报文解析成多条报文的过程
     * @param saveContent SaveSwiftContentBean
     * @param alSwiftData ArrayList
     * @param alSwiftTemp ArrayList
     * @return SaveSwiftContentBean
     * @throws YssException
     */
    protected SaveSwiftContentBean splitDatas(SaveSwiftContentBean saveContent, ArrayList alSwiftTemp) throws YssException {
        return null;
    }

    /**
     * 取一个模板代码的值
     * 条件是取以本次:开始到下一个以:之间的数据
     * @param arrContent String[]
     * @param iStartRow int
     * @return String
     */
    protected String getRowContent(String[] arrContent, int iStartRow) {
        StringBuffer buf = new StringBuffer();
        buf.append(arrContent[iStartRow]);
        iStartRow++;
        if (iStartRow < arrContent.length) {
            for (int i = iStartRow; i < arrContent.length; i++) {
                if (arrContent[i].startsWith(":")) {
                    break;
                }
                buf.append("\r\n").append(arrContent[i]);
            }
        }
        return buf.toString();
    }

}
