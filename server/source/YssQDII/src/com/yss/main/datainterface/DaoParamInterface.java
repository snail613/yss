package com.yss.main.datainterface;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.syssetting.*;
import com.yss.util.*;
import oracle.sql.*;
import com.yss.main.operdeal.BaseOperDeal;

/**
 * <p>Title: </p>
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
public class DaoParamInterface
    extends BaseBean implements IDataInterface {
    //add by songjie 2009-08-18 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
    String startDate = null;//接口处理界面录入的开始日期
    String cfgCode = null;//自定义接口配置代码
   //add  by zhangfa  20100913 MS01723    接口读数未根据组合判断财务估值表、净值表是否有确认时    QDV4交银施罗德2010年9月8日02_B    
    String portCodes=" ";
    //---------------------------------------------------------------------------------------------------------------------
    String combineCode="";//接口导入组合代码
    public DaoParamInterface() {
    }

    public void importData(String sRequestStr) throws YssException {
        String[] tmpStr = sRequestStr.split("\r\f");
        String[] cusConfigStr = tmpStr[0].split("\r\t");
        String[] fileNameStr = tmpStr[1].split("\r\t");
        String[] fileInfoStr = tmpStr[2].split("\r\t");
        String[] fileContentStr = tmpStr[3].split("\r\t");
        String[] pretreatStr = tmpStr[4].split("\r\t");
        String[] pretreatFieldStr = tmpStr[5].split("\f\f");
        String[] tarDelCondStr = tmpStr[6].split("\r\t");
        String[] springStr = tmpStr[7].split("\r\t");
        String[] DataDict = tmpStr[8].split("\r\t");
        String[] DaoDict = tmpStr[9].split("\r\t");
        String[] pretreatFieldStr2 = null;
        String strSql = "";

        DaoCusConfigureBean cusConfigure = null;
        DaoFileNameBean fileName = null; //
        DaoFileInfoBean fileInfo = null;
        DaoFileContentBean fileContent = null;
        DaoPretreatBean pretreat = null;
        DaoPretreatFieldBean pretreatField = null;
        DaoTgtTabCond tgtTabCond = null;
        SpringInvokeBean spring = null;
        DataDictBean datadict = null;
        DaoDictBean daodict = null;
        boolean bTrans = false;

        Connection conn = dbl.loadConnection();

        int fileNameNum = 0;
        int fileInfoNum = 0;
        int fileContentNum = 0;
        int pretFieldNum = 0;
        int delCondNum = 0;

        ResultSet rs = null;
        HashMap hmDell1 = new HashMap(); //根据条件做一次性删除操作 add liyu 1104
        HashMap hmDell2 = new HashMap();
        HashMap hmDell3 = new HashMap();
        HashMap hmDell4 = new HashMap();
        HashMap hmDell5 = new HashMap();
        HashMap hmDataDict = new HashMap(); //add liyu 1114 数据字典,临时表的
        HashMap hmDaoDict = new HashMap(); //add lzp 1121 数据字典
        try {
            //----------------------自定义接口------------------------------
            conn.setAutoCommit(false);

            for (int i = 0; i < cusConfigStr.length; i++) {
                if (!cusConfigStr[i].equals("null") && cusConfigStr[i].length() > 0) {
                    cusConfigure = new DaoCusConfigureBean();
                    cusConfigure.setYssPub(pub);

                    cusConfigure.parseRowStr(cusConfigStr[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Dao_CusConfig") +
                        " where FCusCfgCode=" +
                        dbl.sqlString(cusConfigure.getCusCfgCode());
                    dbl.executeSql(strSql);
                    strSql =
                        " insert into " + pub.yssGetTableName("Tb_Dao_CusConfig") +
                        " (FCusCfgCode,FCusCfgName,FCusCfgType," +
                        " FFileType,FTabName,FFileNameDesc," +
                        " FFileInfoDesc,FFileCntDesc,FFileTrailDesc,FDPCodes," +
                        " FSplitType,FSplitMark,FEndMark,FFileCusCfg,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                        " values(" +
                        dbl.sqlString(cusConfigure.getCusCfgCode()) + "," +
                        dbl.sqlString(cusConfigure.getCusCfgName()) + "," +
                        dbl.sqlString(cusConfigure.getCusCfgType()) + "," +
                        dbl.sqlString(cusConfigure.getFileType()) + "," +
                        dbl.sqlString(cusConfigure.getTabName()) + "," +
                        dbl.sqlString(cusConfigure.getFileNameDesc()) + "," +
                        dbl.sqlString(cusConfigure.getFileInfoDesc()) + "," +
                        dbl.sqlString(cusConfigure.getFileCntDesc()) + "," +
                        dbl.sqlString(cusConfigure.getFileTrailDesc()) + "," +
                        dbl.sqlString(cusConfigure.getDPCodes()) + "," +
                        cusConfigure.getSplitType() + "," +
                        dbl.sqlString(cusConfigure.getSplitMark()) + "," +
                        dbl.sqlString(cusConfigure.getEndMark()) + "," +
                        dbl.sqlString(cusConfigure.getFileCusCfg()) + "," + //lzp 11.22  add
                        1 + "," +
                        dbl.sqlString(pub.getUserCode()) + "," +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        "," +
                        dbl.sqlString(pub.getUserCode()) + ")";
                    dbl.executeSql(strSql);
                }
            }
            //-----------------------------------文件名-------------------------------------
            for (int i = 0; i < fileNameStr.length; i++) {
                if (!fileNameStr[i].equals("null") && fileNameStr[i].length() > 0) {
                    fileNameNum++;
                    fileName = new DaoFileNameBean();
                    fileName.setYssPub(pub);
                    fileName.parseRowStr(fileNameStr[i]);
                    if (hmDell1.get(fileName.getCusCfgCode()) == null) {
                        hmDell1.put(fileName.getCusCfgCode(),
                                    fileName.getCusCfgCode());
                        fileNameNum = 1;
                    } // 若代码不同，则删除一次，避免每次做删除操作，提高执行的效率。下同 add liyu 1104
                    if (fileNameNum == 1) {
                        strSql = " delete from " +
                            pub.yssGetTableName("Tb_Dao_FileName") +
                            " where FCusCfgCode=" +
                            dbl.sqlString(fileName.getCusCfgCode());
                        dbl.executeSql(strSql);
                    }
                    strSql = "insert into " + pub.yssGetTableName("Tb_Dao_FileName") +
                        "(FCusCfgCode,FOrderNum," +
                        " FValueType,FFileNameCls,FFIleNameConent,FFileNameDict,FTabFeild,FFormat," +
                        " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                        " values(" +
                        dbl.sqlString(fileName.getCusCfgCode()) + "," +
                        fileNameNum + "," +
                        fileName.getValueType() + "," +
                        dbl.sqlString(fileName.getFileNameCls()) + "," +
                        dbl.sqlString(fileName.getFileNameConent()) + "," +
                        dbl.sqlString(fileName.getFileNameDictCode()) + "," +
                        dbl.sqlString(fileName.getTabFeildCode()) + "," +
                        dbl.sqlString(fileName.getFormat()) + "," +
                        1 + "," +
                        dbl.sqlString(pub.getUserCode()) + "," +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        "," +
                        dbl.sqlString(pub.getUserCode()) + ")";
                    dbl.executeSql(strSql);
                }
            }
            //----------------------------------文件头--------------------------------------
            for (int i = 0; i < fileInfoStr.length; i++) {
                if (!fileInfoStr[i].equals("null") && fileInfoStr[i].length() > 0) {
                    fileInfoNum++;
                    fileInfo = new DaoFileInfoBean();
                    fileInfo.setYssPub(pub);
                    fileInfo.parseRowStr(fileInfoStr[i]);
                    if (hmDell2.get(fileInfo.getCusCfgCode()) == null) {
                        hmDell2.put(fileInfo.getCusCfgCode(),
                                    fileInfo.getCusCfgCode());
                        fileInfoNum = 1;
                    }
                    if (fileInfoNum == 1) {
                        strSql =
                            "delete from " + pub.yssGetTableName("Tb_Dao_FileInfo") +
                            " where FCusCfgCode=" +
                            dbl.sqlString(fileInfo.getCusCfgCode());
                        dbl.executeSql(strSql);
                    }
                    strSql = "insert into " + pub.yssGetTableName("Tb_Dao_FileInfo") +
                        "(FCusCfgCode,FOrderNum," +
                        " FLoadRow,FLoadIndex,FLoadLen,FTabFeild,FFormat,FFileInfoDict," +
                        " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                        " values(" +
                        dbl.sqlString(fileInfo.getCusCfgCode()) + "," +
                        fileInfoNum + "," +
                        dbl.sqlString(fileInfo.getLoadLen()) + "," +
                        dbl.sqlString(fileInfo.getTabField()) + "," +
                        dbl.sqlString(fileInfo.getFormat()) + "," +
                        dbl.sqlString(fileInfo.getFileInfoDict()) + "," +
                        1 + "," +
                        dbl.sqlString(pub.getUserCode()) + "," +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        "," +
                        dbl.sqlString(pub.getUserCode()) + ")";
                    dbl.executeSql(strSql);

                }
            }
            //------------------------------------文件内容---------------------------------
            for (int i = 0; i < fileContentStr.length; i++) {
                if (!fileContentStr[i].equals("null") &&
                    fileContentStr[i].length() > 0) {
                    fileContentNum++;
                    fileContent = new DaoFileContentBean();
                    fileContent.setYssPub(pub);
                    fileContent.parseRowStr(fileContentStr[i]);
                    if (hmDell3.get(fileContent.getCusCfgCode()) == null) {
                        hmDell3.put(fileContent.getCusCfgCode(),
                                    fileContent.getCusCfgCode());
                        fileContentNum = 1;
                    }
                    if (fileContentNum == 1) {
                        strSql = "delete from " +
                            pub.yssGetTableName("Tb_Dao_FileContent") +
                            " where FCusCfgCode=" +
                            dbl.sqlString(fileContent.getCusCfgCode());
                        dbl.executeSql(strSql);
                    }
                    strSql = "insert into " +
                        pub.yssGetTableName("Tb_Dao_FileContent") +
                        "(FCusCfgCode,FOrderNum," +
                        " FBeginRow,FLoadIndex,FLoadLen,FTabFeild,FFormat,FFileContentDict," +
                        " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                        " values(" +
                        dbl.sqlString(fileContent.getCusCfgCode()) + "," +
                        fileContentNum + "," +
                        fileContent.getBeginRow() + "," +
                        fileContent.getLoadIndex() + "," +
                        fileContent.getLoadLen() + "," +
                        dbl.sqlString(fileContent.getTabField()) + "," +
                        dbl.sqlString(fileContent.getFormat()) + "," +
                        //lzp修改07.11.21 导出时字段不对应无法将此字段导入
                        // dbl.sqlString(fileContent.getFileContentDict()) + "," +
                        dbl.sqlString(fileContent.getFileInfoDict()) + "," +
                        1 + "," +
                        dbl.sqlString(pub.getUserCode()) + "," +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        "," +
                        dbl.sqlString(pub.getUserCode()) + ")";
                    dbl.executeSql(strSql);
                }
            }
            //-------------------------------预处理----------------------------
            for (int i = 0; i < pretreatStr.length; i++) {
                if (!pretreatStr[i].equals("null") && pretreatStr[i].length() > 0) {
                    pretreat = new DaoPretreatBean();
                    pretreat.setYssPub(pub);
                    pretreat.parseRowStr(pretreatStr[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Dao_Pretreat") +
                        " where FDPDsCode=" +
                        dbl.sqlString(pretreat.getDPDsCode());
                    dbl.executeSql(strSql);
                    if (dbl.getDBType() == YssCons.DB_ORA) {
                        strSql = "insert into " +
                            pub.yssGetTableName("Tb_Dao_Pretreat") +
                            "(FDPDsCode,FDPDsName,FDsType,FTargetTab,FBeanId,FDataSource," +
                            "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                            " values(" + dbl.sqlString(pretreat.getDPDsCode()) +
                            "," +
                            dbl.sqlString(pretreat.getDPDsName()) + "," +
                            pretreat.getDsType() + "," +
                            dbl.sqlString(pretreat.getTargetTabCode()) + " ," +
                            dbl.sqlString(pretreat.getBeanId()) + " ," +
                            "EMPTY_CLOB()" + "," +
                            1 + "," +
                            dbl.sqlString(pub.getUserCode()) + "," +
                            dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                            "," +
                            dbl.sqlString(pub.getUserCode()) + ")";
                        dbl.executeSql(strSql);
                        strSql = "select * from " +
                            pub.yssGetTableName("Tb_Dao_Pretreat") +
                            " where FDPDsCode =" +
                            dbl.sqlString(pretreat.getDPDsCode());
                        rs = dbl.openResultSet(strSql);
                        while (rs.next()) {
                        	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                        	  // modify by jsc 20120809 连接池对大对象的特殊处理
                            //CLOB clob = ( (oracle.jdbc.OracleResultSet) rs).getCLOB("FDataSource");
                        	CLOB clob = dbl.CastToCLOB(rs.getClob("FDataSource"));
                            clob.putString(1, pretreat.getDataSource());
                            strSql = "update " + pub.yssGetTableName("Tb_Dao_Pretreat") +
                                " set FDataSource = ? where FDPDsCode=" +
                                dbl.sqlString(pretreat.getDPDsCode());
                            PreparedStatement pst = conn.prepareStatement(strSql);
                            pst.setClob(1, clob);
                            pst.executeUpdate();
                            pst.close();
                        }
                    } else if (dbl.getDBType() == YssCons.DB_DB2) {
                        strSql = "insert into " +
                            pub.yssGetTableName("Tb_Dao_Pretreat") +
                            "(FDPDsCode,FDPDsName,FDsType,FTargetTab,FBeanId,FDataSource," +
                            "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                            " values(" + dbl.sqlString(pretreat.getDPDsCode()) +
                            "," +
                            dbl.sqlString(pretreat.getDPDsName()) + "," +
                            pretreat.getDsType() + "," +
                            dbl.sqlString(pretreat.getTargetTabCode()) + " ," +
                            dbl.sqlString(pretreat.getBeanId()) + " ," +
                            dbl.sqlString(pretreat.getDataSource()) + "," +
                            1 + "," +
                            dbl.sqlString(pub.getUserCode()) + "," +
                            dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                            "," +
                            dbl.sqlString(pub.getUserCode()) + ")";
                        dbl.executeSql(strSql);
                    } else {
                        throw new YssException("数据库访问错误。数据库类型不明或选择了非系统兼容数据库！");
                    }
                }
            }

            //------------------------------------预处理字段------------------------------------
            HashMap hmDs = new HashMap();
            for (int i = 0; i < pretreatFieldStr.length; i++) {
                pretreatFieldStr2 = pretreatFieldStr[i].split("\r\t");
                for (int j = 0; j < pretreatFieldStr2.length; j++) {
                    if (!pretreatFieldStr2[j].equals("null") &&
                        pretreatFieldStr2[j].length() > 0) { //此处的i为改为j
                        pretFieldNum = j + 1;
                        pretreatField = new DaoPretreatFieldBean();
                        pretreatField.setYssPub(pub);
                        pretreatField.parseRowStr(pretreatFieldStr2[j]);
                        if (hmDs.get(pretreatField.getDPDsCode()) == null) { //由于传过来的值中记录有重复，所以控制删除，根据预处理代码不同来删除 add liyu 1105
                            pretFieldNum = 1;
                            hmDs.put(pretreatField.getDPDsCode(),
                                     pretreatField.getDPDsCode());
                            if (pretFieldNum == 1) {
                                strSql = "delete from " +
                                    pub.yssGetTableName("Tb_Dao_PretreatField") +
                                    " where FDPDsCode=" +
                                    dbl.sqlString(pretreatField.getDPDsCode());
                                dbl.executeSql(strSql);
                            }
                        }
                        if (hmDell4.get(pretreatField.getDPDsCode() +
                                        pretreatField.getDsField() + pretreatField.getTargetField()) == null) {
                            hmDell4.put(pretreatField.getDPDsCode() +
                                        pretreatField.getDsField() + pretreatField.getTargetField(),
                                        pretreatField.getDPDsCode() +
                                        pretreatField.getDsField() + pretreatField.getTargetField()); //当有重复值时不导入 add liyu 1104
                            strSql = "insert into " +
                                pub.yssGetTableName("Tb_Dao_PretreatField") +
                                "(FDPDsCode, FOrderIndex, FDsField, FTargetField," +
                                " FPretType,FSICode," +
                                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                                " values(" +
                                dbl.sqlString(pretreatField.getDPDsCode()) + "," +
                                pretFieldNum + "," +
                                dbl.sqlString(pretreatField.getDsField()) + "," +
                                dbl.sqlString(pretreatField.getTargetField()) + "," +
                                pretreatField.getPretType() + "," +
                                dbl.sqlString(pretreatField.getSpringCode()) + "," +
                                1 + "," +
                                dbl.sqlString(pub.getUserCode()) + "," +
                                dbl.sqlString(YssFun.formatDatetime(new java.util.
                                Date())) +
                                "," +
                                dbl.sqlString(pub.getUserCode()) + ")";
                            dbl.executeSql(strSql);
                        }
                    }
                    //  pretFieldNum=0; //不需要
                }
            }
            //-----------------------------------接口删除条件------------------------------
            for (int i = 0; i < tarDelCondStr.length; i++) {
                if (!tarDelCondStr[i].equals("null") &&
                    tarDelCondStr[i].length() > 0) {
                    delCondNum++;
                    tgtTabCond = new DaoTgtTabCond();
                    tgtTabCond.setYssPub(pub);
                    tgtTabCond.parseRowStr(tarDelCondStr[i]);
                    if (hmDell5.get(tgtTabCond.getDPDsCode()) == null) {
                        hmDell5.put(tgtTabCond.getDPDsCode(), tgtTabCond.getDPDsCode());
                        delCondNum = 1;
                    }
                    if (delCondNum == 1) {
                        strSql = "delete from " +
                            pub.yssGetTableName("Tb_Dao_TgtTabCond") +
                            " where FDPDsCode=" +
                            dbl.sqlString(tgtTabCond.getDPDsCode());
                        dbl.executeSql(strSql);
                    }
                    strSql = "insert into " +
                        pub.yssGetTableName("Tb_Dao_TgtTabCond") +
                        "(FDPDsCode,FOrderIndex,FTargetField,FDsField,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                        " values(" + dbl.sqlString(tgtTabCond.getDPDsCode()) + "," +
                        delCondNum + "," +
                        dbl.sqlString(tgtTabCond.getTargetField()) + "," +
                        dbl.sqlString(tgtTabCond.getDsField()) + "," +
                        1 + "," +
                        dbl.sqlString(pub.getUserCode()) + "," +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        "," +
                        dbl.sqlString(pub.getUserCode()) + ")";
                    dbl.executeSql(strSql);
                }
            }
            for (int i = 0; i < springStr.length; i++) { //覆盖原有的，保留新的 add方法中删除原有的 by liyu 1226
                if (!springStr[i].equals("null") &&
                    springStr[i].length() > 0) {
                    spring = new SpringInvokeBean();
                    spring.setYssPub(pub);
                    spring.parseRowStr(springStr[i]);
                    spring.addSetting();
                }
            }
            for (int i = 0; i < DataDict.length; i++) {
                if (!DataDict[i].equalsIgnoreCase("null") && DataDict[i].length() > 0) {
                    datadict = new DataDictBean(pub);
                    datadict.protocolParse(DataDict[i]);
                    if (hmDataDict.get(datadict.getTabName().toUpperCase()) == null) {
                        hmDataDict.put(datadict.getTabName().toUpperCase(), datadict.getTabName());
                        strSql = "delete from TB_Fun_DataDict where FTabname=" + dbl.sqlString(datadict.getTabName());
                        dbl.executeSql(strSql);
                    }
                    datadict.addDataDict();
                }
            }
            //--------------------------------------------------add  lzp  11。21 接口字典
            for (int i = 0; i < DaoDict.length; i++) {
                if (!DaoDict[i].equalsIgnoreCase("null") && DaoDict[i].length() > 0) {
                    daodict = new DaoDictBean();
                    daodict.setYssPub(pub);
                    daodict.parseRowStr(DaoDict[i]);
                    if (hmDaoDict.get(daodict.getDictCode() + daodict.getSrcConent()) == null) {
                        hmDaoDict.put(daodict.getDictCode() + daodict.getSrcConent(), daodict.getDictCode() + daodict.getSrcConent());
                        strSql = "delete from " + pub.yssGetTableName("Tb_Dao_dict") + " where FDictCode='" + daodict.getDictCode() + "'"
                            + " and FSrcConent='" + daodict.getSrcConent() + "'";
                        dbl.executeSql(strSql);
                        strSql = "insert into " +
                            pub.yssGetTableName("Tb_Dao_Dict") +
                            "(FDICTCODE,FDICTNAME,FSRCCONENT,FCNVCONENT,FDESC,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                            " values(" +
                            dbl.sqlString(daodict.getDictCode()) + "," +
                            dbl.sqlString(daodict.getDictName()) + "," +
                            dbl.sqlString(daodict.getSrcConent()) + "," +
                            dbl.sqlString(daodict.getCnvConent()) + "," +
                            dbl.sqlString(daodict.getDesc()) + "," +
                            1 + "," +
                            dbl.sqlString(pub.getUserCode()) + "," +
                            dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
                            dbl.sqlString(pub.getUserCode()) + ")";
                        dbl.executeSql(strSql);
                    }
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception re) {throw new YssException("导入数据出错", e);}
            throw new YssException("导入数据出错", e);
        }

    }

    //-----------------------------------导出数据---------------------------------------------------------------
    public String exportData(String sRequestStr) throws YssException {
        String[] cusDaoCodes = sRequestStr.split("\t");

        String[] arrPretreatCode = null;
        Set hPretreatCodes = null;
        Iterator it = null;
        Iterator it2 = null;
        Set hDictCodes = null;
        Set hSpringCodes = null;
        StringBuffer bufDataDict = new StringBuffer();

        //初始化每个类
        DaoCusConfigureBean cusConfigure = null;
        DaoFileNameBean fileName = null; //
        DaoFileInfoBean fileInfo = null;
        DaoFileContentBean fileContent = null;
        DaoPretreatBean pretreat = null;
        DaoPretreatFieldBean pretreatField = null;
        DaoTgtTabCond tgtTabCond = null;
        DaoDictBean dict = null;
        SpringInvokeBean spring = null;
        DataDictBean datadict = null;
        //------------------------------------------------------------------
        String strResult = "";
        String cusConfigStr = "";
        String fileNameStr = "";
        String fileInfoStr = "";
        String fileContentStr = "";
        String pretreatStr = "";
        String pretreatFieldStr = "";
        String tarDelCondStr = "";
        String dictStr = "";
        String springStr = "";
        String DataDict = "";
//------------------------------------------------------------------
        ResultSet rsCus = null;
        ResultSet rsFileName = null;
        ResultSet rsFileInfo = null;
        ResultSet rsFileContent = null;
        ResultSet rsPretreat = null;
        ResultSet rsPretreatField = null;
        ResultSet rsTarDelCond = null;
        ResultSet rsDict = null;
        ResultSet rsSpring = null;
        ResultSet rsDataDict = null;
        String strSql = "";
        HashMap hmSame = null; //处理预处理中的重复值 add liyu 1104
        HashMap ddSame = null; //处理数据字典的重复值 add lzp 1121
        try {
            //--------------------自定义接口------------------------------------------
            for (int i = 0; i < cusDaoCodes.length; i++) {
                hPretreatCodes = new HashSet();
                hDictCodes = new HashSet();
                hSpringCodes = new HashSet();

                strSql = "select * from " +
                    pub.yssGetTableName("Tb_Dao_CusConfig") +
                    " where FCusCfgCode=" + dbl.sqlString(cusDaoCodes[i]);
                rsCus = dbl.openResultSet(strSql);
                while (rsCus.next()) {
                    cusConfigure = new DaoCusConfigureBean();
                    cusConfigure.setCusCfgCode(rsCus.getString("FCusCfgCode"));
                    cusConfigure.setCusCfgName(rsCus.getString("FCusCfgName"));
                    cusConfigure.setCusCfgType(rsCus.getString("FCusCfgType"));
                    cusConfigure.setFileType(rsCus.getString("FFileType"));
                    cusConfigure.setTabName(rsCus.getString("FTabName"));
                    cusConfigure.setFileNameDesc(rsCus.getString("FFileNameDesc"));
                    cusConfigure.setFileInfoDesc(rsCus.getString("FFileInfoDesc"));
                    cusConfigure.setFileCntDesc(rsCus.getString("FFileCntDesc"));
                    cusConfigure.setFileTrailDesc(rsCus.getString("FFileTrailDesc"));
                    cusConfigure.setDPCodes(rsCus.getString("FDPCodes"));
                    cusConfigure.setSplitType(rsCus.getString("FSplitType"));
                    cusConfigure.setSplitMark(rsCus.getString("FSplitMark"));
                    cusConfigure.setFileCusCfg(rsCus.getString("FFileCusCfg")); //lzp 11.23 add

                    if (rsCus.getString("FDPCodes") != null && !rsCus.getString("FDPCodes").equalsIgnoreCase("null")
                        && rsCus.getString("FDPCodes") != null) {
                        arrPretreatCode = rsCus.getString("FDPCodes").split(",");
                        for (int j = 0; j < arrPretreatCode.length; j++) {
                            hPretreatCodes.add(arrPretreatCode[j]);
                        }
                    }
                    if (rsCus.getString("FTabName") != null && !rsCus.getString("FTabName").equalsIgnoreCase("null") &&
                        rsCus.getString("FTabName") != null) {
                        bufDataDict.append(rsCus.getString("FTabName")).append(",");
                    }
                    cusConfigStr += cusConfigure.buildRowStr() + "\r\t";
                }
                dbl.closeResultSetFinal(rsCus);
//----------------------------------文件名----------------------------------------------
                strSql = "select * from " + pub.yssGetTableName("Tb_Dao_FileName") +
                    " where FCusCfgCode=" + dbl.sqlString(cusDaoCodes[i]) +
                    " order by FOrderNum";
                rsFileName = dbl.openResultSet(strSql);
                while (rsFileName.next()) {
                    fileName = new DaoFileNameBean();
                    fileName.setCusCfgCode(rsFileName.getString("FCusCfgCode") + "");
                    fileName.setOrderNum(rsFileName.getInt("FOrderNum") + "");
                    fileName.setValueType(rsFileName.getString("FValueType"));
                    fileName.setFormat(rsFileName.getString("FFormat") + "");
                    fileName.setFileNameConent(rsFileName.getString(
                        "FFIleNameConent") + "");
                    fileName.setFileNameCls(rsFileName.getString("FFileNameCls") +
                                            "");
                    fileName.setFileNameDictCode(rsFileName.getString(
                        "FFileNameDict") + "");
                    fileName.setTabFeildCode(rsFileName.getString("FTabFeild") + "");

                    if (rsFileName.getString("FFileNameDict") != null &&
                        !rsFileName.getString("FFileNameDict").equalsIgnoreCase(
                            "null")) {
                        hDictCodes.add(rsFileName.getString("FFileNameDict"));
                    }

                    fileNameStr += fileName.buildRowStr() + "\r\t";
                }
                dbl.closeResultSetFinal(rsFileName);
//---------------------------------文件头---------------------------------
                strSql = "select * from " + pub.yssGetTableName("Tb_Dao_FileInfo") +
                    " where FCusCfgCode=" + dbl.sqlString(cusDaoCodes[i]) +
                    " order by FOrderNum";
                rsFileInfo = dbl.openResultSet(strSql);
                while (rsFileInfo.next()) {
                    fileInfo = new DaoFileInfoBean();
                    fileInfo.setCusCfgCode(rsFileInfo.getString("rsFileInfo"));
                    fileInfo.setOrderNum(rsFileInfo.getInt("FOrderNum") + "");
                    fileInfo.setLoadRow(rsFileInfo.getInt("FLoadRow") + "");
                    fileInfo.setLoadIndex(rsFileInfo.getInt("FLoadIndex") + "");
                    fileInfo.setLoadLen(rsFileInfo.getInt("FLoadLen") + "");
                    fileInfo.setTabField(rsFileInfo.getString("FTabFeild") + "");
                    fileInfo.setFormat(rsFileInfo.getString("FFormat") + "");
                    fileInfo.setFileInfoDict(rsFileInfo.getString("FFileInfoDict") +
                                             "");

                    if (rsFileInfo.getString("FFileInfoDict") != null &&
                        !rsFileInfo.getString("FFileInfoDict").equalsIgnoreCase(
                            "null")) {
                        hDictCodes.add(rsFileInfo.getString("FFileInfoDict"));
                    }

                    fileInfoStr += fileInfo.buildRowStr() + "\r\t";
                }
                dbl.closeResultSetFinal(rsFileInfo);
                //----------------------------------文件内容---------------------------------

                strSql = "select * from " +
                    pub.yssGetTableName("Tb_Dao_FileContent") +
                    " where FCusCfgCode=" + dbl.sqlString(cusDaoCodes[i]) +
                    " order by FOrderNum";
                rsFileContent = dbl.openResultSet(strSql);
                while (rsFileContent.next()) {
                    fileContent = new DaoFileContentBean();
                    fileContent.setCusCfgCode(rsFileContent.getString("FCusCfgCode") +
                                              "");
                    fileContent.setBeginRow(rsFileContent.getInt("FBeginRow") + "");
                    fileContent.setLoadIndex(rsFileContent.getInt("FLoadIndex") + "");
                    fileContent.setLoadLen(rsFileContent.getInt("FLoadLen") + "");
                    fileContent.setTabField(rsFileContent.getString("FTabFeild") +
                                            "");

                    fileContent.setFormat(rsFileContent.getString("FFormat") + "");
                    //LZP修改07.11.21  导出文件内容表时，他里面的文件内容字典字段是空的，由于此处名字没对应
                    // fileContent.setFileContentDict(rsFileContent.getString(
                    fileContent.setFileInfoDict(rsFileContent.getString(
                        "FFileContentDict") + "");

                    if (rsFileContent.getString("FFileContentDict") != null &&
                        !rsFileContent.getString("FFileContentDict").
                        equalsIgnoreCase("null")) {
                        hDictCodes.add(rsFileContent.getString("FFileContentDict"));
                    }
                    fileContentStr += fileContent.buildRowStr() + "\r\t";
                }
                dbl.closeResultSetFinal(rsFileContent);
//---------------------------------接口预处理设置---------------------------------
                it = hPretreatCodes.iterator();
                while (it.hasNext()) {
                    strSql = "select * from " +
                        pub.yssGetTableName("Tb_Dao_Pretreat") +
                        " where FDPDsCode=" + dbl.sqlString( (String) it.next());
                    rsPretreat = dbl.openResultSet(strSql);
                    while (rsPretreat.next()) {
                        pretreat = new DaoPretreatBean();
                        pretreat.setDPDsCode(rsPretreat.getString("FDPDsCode") + "");
                        pretreat.setDPDsName(rsPretreat.getString("FDPDsName") + "");
                        pretreat.setDsType(rsPretreat.getShort("FDsType"));
                        pretreat.setTargetTabCode(rsPretreat.getString("FTargetTab") +
                                                  "");
                        pretreat.setBeanId(rsPretreat.getString("FBeanId") + "");
                        pretreat.setDataSource
                            (dbl.clobStrValue(rsPretreat.getClob("FDataSource")).
                             replaceAll(
                                 "\t", "   "));
                        pretreatStr += pretreat.buildRowStr() + "\r\t";

                        //--------------------------------------接口预处理字段设置------------------------------
                        hmSame = new HashMap();
                        strSql = "select * from " +
                            pub.yssGetTableName("Tb_Dao_PretreatField") +
                            " where FDPDsCode=" +
                            dbl.sqlString(rsPretreat.getString("FDPDsCode")) +
                            " order by FOrderIndex";
                        rsPretreatField = dbl.openResultSet(strSql);
                        while (rsPretreatField.next()) {
                            if (hmSame.get(rsPretreatField.getString( //如果是重复值就不导出  根据 FDPDsCode,FDsField,FTargetField判断 liyu 1104
                                "FDPDsCode") + rsPretreatField.getString(
                                    "FDsField") + rsPretreatField.getString(
                                        "FTargetField")) == null) {
                                hmSame.put(rsPretreatField.getString(
                                    "FDPDsCode") + rsPretreatField.getString(
                                        "FDsField") + rsPretreatField.getString(
                                            "FTargetField"), rsPretreatField.getString(
                                                "FDPDsCode") + rsPretreatField.getString(
                                    "FDsField") + rsPretreatField.getString(
                                        "FTargetField"));
                                pretreatField = new DaoPretreatFieldBean();
                                pretreatField.setDPDsCode(rsPretreatField.getString(
                                    "FDPDsCode") + "");
                                pretreatField.setOrderIndex(rsPretreatField.getInt(
                                    "FOrderIndex"));
                                pretreatField.setSpringCode(rsPretreatField.getString(
                                    "FSICode") + "");
                                pretreatField.setPretType(rsPretreatField.getInt(
                                    "FPretType") + "");
                                pretreatField.setDsField(rsPretreatField.getString(
                                    "FDsField") + "");
                                pretreatField.setTargetField(rsPretreatField.getString(
                                    "FTargetField") + "");

                                pretreatFieldStr += pretreatField.buildRowStr() + "\r\t";
                            }
                        }

                        strSql = "select * from " +
                            pub.yssGetTableName("Tb_Dao_TgtTabCond") +
                            " where FDPDsCode=" +
                            dbl.sqlString(rsPretreat.getString("FDPDsCode")) +
                            " order by FOrderIndex";

                        rsTarDelCond = dbl.openResultSet(strSql);
                        while (rsTarDelCond.next()) {
                            tgtTabCond = new DaoTgtTabCond();
                            tgtTabCond.setDPDsCode(rsTarDelCond.getString("FDPDsCode") +
                                "");
                            tgtTabCond.setOrderIndex(rsTarDelCond.getInt("FOrderIndex"));
                            tgtTabCond.setTargetField(rsTarDelCond.getString(
                                "FTargetField") + "");
                            tgtTabCond.setDsField(rsTarDelCond.getString("FDsField") +
                                                  "");

                            tarDelCondStr += tgtTabCond.buildRowStr() + "\r\t";
                        }
                        dbl.closeResultSetFinal(rsPretreatField);
                        dbl.closeResultSetFinal(rsTarDelCond);
                    }
                    dbl.closeResultSetFinal(rsPretreat);
                }
                //====================导接口字典=================================lzp11.21修改
                ddSame = new HashMap();
                it2 = hDictCodes.iterator();
                while (it2.hasNext()) {
                    strSql = "select * from " + pub.yssGetTableName("Tb_Dao_Dict") +
                        " where FDictCode=" + dbl.sqlString( (String) it2.next());
                    rsDict = dbl.openResultSet(strSql);
                    while (rsDict.next()) {
                        if (ddSame.get(rsDict.getString("FDictCode") +
                                       rsDict.getString("FSrcConent")) == null) { //处理重复问题
                            ddSame.put(rsDict.getString("FDictCode") + rsDict.getString("FSrcConent"), rsDict.getString("FDictCode") + rsDict.getString("FSrcConent"));

                            dict = new DaoDictBean();
                            dict.setDictCode(rsDict.getString("FDictCode") + "");
                            dict.setDictName(rsDict.getString("FDictName") + "");
                            dict.setSrcConent(rsDict.getString("FSrcConent") + "");
                            dict.setCnvConent(rsDict.getString("FCnvConent") + "");
                            dict.setDesc(rsDict.getString("FDesc") + "");
                            dictStr += dict.buildRowStr() + "\r\t";
                        }
                    }
                    dbl.closeResultSetFinal(rsDict);
                }
            }
            //====================spring处理====================================== add liyu 1104
            strSql = "select * from TB_FUN_SPINGINVOKE";
            rsSpring = dbl.openResultSet(strSql);
            while (rsSpring.next()) {
                spring = new SpringInvokeBean();
                spring.setSICode(rsSpring.getString("FSICode"));
                spring.setSIName(rsSpring.getString("FSIName"));
                spring.setBeanID(rsSpring.getString("FBeanId"));
                spring.setParams(rsSpring.getString("FParams"));
                spring.setReturnType(rsSpring.getString("FReturnType"));
                spring.setModuleCode(rsSpring.getString("FModuleCode"));
                spring.setDesc(rsSpring.getString("FDesc"));
                //spring.setFormCode(rsSpring.getString("FFormCode"));
                //spring.setCtlName(rsSpring.getString("FCTLName"));
                springStr += spring.buildRowStr() + "\r\t";
            }
            dbl.closeResultSetFinal(rsSpring);
            //====================导临时表用的数据字典=================================
            strSql = "select * from TB_Fun_DataDict where FTabName in(" + operSql.sqlCodes(bufDataDict.toString()) + ")";
            rsDataDict = dbl.openResultSet(strSql);
            while (rsDataDict.next()) {
                datadict = new DataDictBean();
                datadict.setYssPub(pub);
                datadict.setTabName(rsDataDict.getString("FTabName"));
                datadict.setFieldName(rsDataDict.getString("FFieldName"));
                datadict.setStrTableType(rsDataDict.getString("FTableType"));
                datadict.setStrIsNull(rsDataDict.getString("FIsNull"));
                datadict.setDesc(rsDataDict.getString("FDesc"));
                datadict.setStrFFieldType(rsDataDict.getString("FFieldType"));
                datadict.setTableDesc(rsDataDict.getString("FTableDesc"));
                datadict.setExNum(rsDataDict.getString("FFieldPre"));
                datadict.setFieldDesc(rsDataDict.getString("FFieldDesc"));
                datadict.setKey(rsDataDict.getString("FKey"));
                datadict.setFDefaultValue(rsDataDict.getString("FDefaultValue"));
                DataDict += datadict.buildRowStr() + "\r\t";
            }
            dbl.closeResultSetFinal(rsDataDict);

            strResult = cusConfigStr + "\r\f" +
                fileNameStr + "\r\f" +
                fileInfoStr + "\r\f" +
                fileContentStr + "\r\f" +
                pretreatStr + "\r\f" +
                pretreatFieldStr + "\r\f" +
                tarDelCondStr + "\r\f" +
                dictStr + "\r\f" +
                springStr + "\r\f" +
                DataDict + "\r\f" +
                dictStr;
            return strResult;
        } catch (Exception e) {
            throw new YssException("导出数据出错");
        }
    }

    public void parseRowStr(String sRowStr) throws YssException {
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        //add by songjie 2009-08-18 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
        if(sType != null && sType.indexOf("judgeDAO") != -1){
            if(sType.indexOf("/t") != -1 &&  sType.split("/t").length >= 2){
                if(sType.split("/t")[1].length() >= 8){
                    this.startDate = sType.split("/t")[1];
                    //modify by zhangfa  20100913 MS01723    接口读数未根据组合判断财务估值表、净值表是否有确认时    QDV4交银施罗德2010年9月8日02_B 
                    	this.portCodes=sType.split("/t")[2];
                    //----------------------------------------------------------------------------------------------------------------------
                    return judgeIfCanDAO();
                }
            }
        }

        //add by songjie 2009-08-18 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
        if(sType != null && sType.indexOf("getCfgInfo") != -1){
            if(sType.indexOf("/t") != -1 &&  sType.split("/t").length >= 2){
                if(sType.split("/t")[1].length() >= 0){
                    this.cfgCode = sType.split("/t")[1];
                    return getCfgInfo();
                }
            }
        }
        //add by songjie 2009-08-18 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
        //add by wuweiqi 20101208 QDV4工银2010年11月1日01_A  
        if(sType != null && sType.indexOf("payeer") != -1) {
       	 if(sType.indexOf("/t") != -1 &&  sType.split("/t").length >= 2){
             if(sType.split("/t")[1].length() >= 0){
                 combineCode = sType.split("/t")[1];//从前台获取指令日期
                 return getPayeer();
             }
         }	
        }
        return "";
    }
 /***
  * add by wuweiqi 20101208 QDV4工银2010年11月1日01_A  
  * 验证收款人付款人设置界面收款人和付款人是否存在
  * @return
  * @throws YssException
  */
    private String getPayeer()throws YssException{
        String strSql = null;//用于储存sql语句
        ResultSet rs = null;//声明结果集
        /**add---huhuichao 2013-7-8 STORY  4051 工银：划款指令收款人可以区分需求*/
        String FctlValue[] = new String[5];//轧差收款人付款人信息
		/**end---huhuichao 2013-7-8 STORY  4051*/
        String FctlValue1="";//获取组合代码
        String FctlValue2[]=null;
        String payeeName = "";//收款人名称
        String payeerName = "";//付款人名称
//        String fctCode="";//投资组合
        String strResult="";
        boolean isExist=false; 
        int i = 0;
        String[] portCodes = null;
        try{
        	strSql= " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
            " where FPUBPARACODE = 'payeeSetter' and Fctlcode='portfolio'";
        	 rs = dbl.openResultSet_antReadonly(strSql);  
        	 rs.last();
             FctlValue2 = new String[rs.getRow()];
             rs.beforeFirst();
             while(rs.next()){
             	FctlValue2[i] = rs.getString("FCTLVALUE");
             	i++;
             }
             dbl.closeResultSetFinal(rs);//modified by yeshenghong 20120320 BUG3958
             //----add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B----//
             if(combineCode.indexOf(",") != -1){
            	 portCodes = combineCode.split(",");
             }
             //----add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B----//
             
             for(int j=0;j < i;j++)
             {
            	//----add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B----//
            	 if(portCodes != null && portCodes.length > 0){
            		 for(int k = 0; k < portCodes.length; k++){
                    	 if(FctlValue2[j].substring(0,FctlValue2[j].indexOf("|")).equals(portCodes[k]))
                    	 {
                    		 FctlValue1=FctlValue2[j];
                    		 isExist=true;
                    		 break;
                    	 } 
            		 }
            	 }else{
            		 //----add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B----//
                	 if(FctlValue2[j].substring(0,FctlValue2[j].indexOf("|")).equals(combineCode))
                	 {
                		 FctlValue1=FctlValue2[j];
                		 isExist=true;
                		 //delete by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B
                		 //break;
                	 }
            	 }
                 if(isExist)
                 {
    	            strSql = " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
    	                     " where FPUBPARACODE = 'payeeSetter' and FPARAID=(select min(FPARAID) from "+
    	                     pub.yssGetTableName("TB_PFOper_PUBPARA") +
    	                     " where FPUBPARACODE ='payeeSetter' and FPARAID!=0 and FCTLVALUE=" +
    	                     dbl.sqlString(FctlValue1) +")" ;
    	            rs = dbl.openResultSet(strSql);         
    	//            rs.last();
    	//            FctlValue = new String[rs.getRow()];
    	//            rs.beforeFirst();
    	            i = 0;
    	            while(rs.next()){
    	            	FctlValue[i] = rs.getString("FCTLVALUE");
    	            	i++;
    	            }
    	            dbl.closeResultSetFinal(rs);//modified by yeshenghong 20120320 BUG3958
    	            /**add---huhuichao 2013-7-8 STORY  4051 工银：划款指令收款人可以区分需求*/
    	            if(FctlValue2.length>0)
    	            /**end---huhuichao 2013-7-8 STORY  4051*/	
    	            {
    		            payeeName=FctlValue[1].substring(0,FctlValue[1].indexOf("|"));
    		            payeerName=FctlValue[2].substring(0,FctlValue[2].indexOf("|"));
    		          //  fctCode=FctlValue[3].substring(0,FctlValue[3].indexOf("|"));
    		            if(!payeeName.equals("")&& !payeerName.equals(""))	
    		            {
    		                return strResult="1";
    		            }
    		            else
    		            {
    		            	return strResult="0";
    		            }
    	            }
    	            else
    	            {
    	            	return strResult="2";
    	            }
                 }else{
                	 return strResult="3";
                 }
             } 
             //add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B
             return strResult="3";
        }
        catch(Exception e){
            throw new YssException("获取轧差收款人付款人信息出错！", e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * add by songjie
     * 2009-09-03
     * 国内：MS00006
     * QDV4.1赢时胜（上海）2009年4月20日06_A
     * 根据自定义接口配置代码获取自定义接口代码对应的接口类型和接口名称
     * @return String
     * @throws YssException
     */
    private String getCfgInfo()throws YssException{
        String strSql = null;//用于储存sql语句
        ResultSet rs = null;//声明结果集
        String cfgType = "";//接口类型
        String cfgName = "";//接口名称
        String tabName = null;//接口的目标表表名
        try{
        	/**add---shashijie 2013-3-1 STORY 3366  增加公共表的获取SQL*/
        	strSql = getSelectCusConfig(pub.yssGetTableName("Tb_Dao_CusConfig"),"0");
            
            strSql += " Union All ";
            //公共表SQL
            strSql += getSelectCusConfig("Tb_Dao_CusConfig","1");
			/**end---shashijie 2013-3-1 STORY 3366 */
            rs = dbl.openResultSet(strSql);
            while(rs.next()){
                cfgType = rs.getString("FCusCfgType");
                cfgName = rs.getString("FCusCfgName");
                tabName = rs.getString("FTabName");
            }
            return cfgType + "\t" + cfgName + "\t" + tabName;
        } catch (Exception e) {
			throw new YssException("获取自定义接口配置的接口类型出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }

    /**shashijie 2013-3-1 STORY */
	/**shashijie 2013-3-1 STORY 3366 获取sql*/
	private String getSelectCusConfig(String tableName, String saveType) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.* from " + tableName + " a "+
        	" where FCusCfgCode = " + dbl.sqlString(this.cfgCode);
		return sql;
	}
	

	/**
     * add by songjie
     * 2009-08-18
     * 国内：MS00006
     * QDV4.1赢时胜（上海）2009年4月20日06_A
     * 判断估值表和净值表中的最大日期是否大于接口处理界面录入的系统读数日期，
     * 若大于，则不允许执行数据导入的操作
     * @param sType String
     * @return String
     * @throws YssException
     */
    private String judgeIfCanDAO() throws YssException {
        String strSql = "";//定义一个保存sql语句的字符串
        ResultSet rs = null;//定义一个结果集用来保存sql查出的结果
        boolean isHave = false; //这里用来判断净值表和估值表里面是否已结算
        java.util.Date dMaxGuessValueDate = null; //保存已经确认的估值表中最大的日期
        java.util.Date dMaxNavDate = null; //保存已经确认的净值表中最大的日期
        try{
            strSql = "select max(fdate) as fdate from " +
                pub.yssGetTableName("Tb_Rep_Guessvalue") + //估值表里的数据
                " where facctlevel = '1' and facctcode='C100' and FDate <= " + dbl.sqlDate(this.startDate)+
                //modify by zhangfa  20100913 MS01723    接口读数未根据组合判断财务估值表、净值表是否有确认时    QDV4交银施罗德2010年9月8日02_B    
                " and FPortCode in(" + operSql.sqlCodes(this.portCodes)+")"
                //----------------------------------------------------------------------------------------------------------------------
                ; //查询条件在业务日期下估值表是否为结算状态
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dMaxGuessValueDate = rs.getDate("FDate");
            }

            dbl.closeResultSetFinal(rs);
            rs = null;

            //-----------查询 净值 表 最大结算日期
            strSql = "select max(fnavdate) as fnavdate from " +
                pub.yssGetTableName("Tb_Data_Navdata") + //净值表里的数据
                " where FKeyCode = 'confirm' "+
              //modify by zhangfa  20100913 MS01723    接口读数未根据组合判断财务估值表、净值表是否有确认时    QDV4交银施罗德2010年9月8日02_B  
                " and FPortCode in(" + operSql.sqlCodes(this.portCodes)+")";
              //----------------------------------------------------------------------------------------------------------------------
				//edit by jiangshichao 20100618 MS01271 QDV4工银2010年06月03日01_B 
				//" and FNavDate <= " + dbl.sqlDate(this.startDate); //查询条件在业务日期下净值表是否为结算状态
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dMaxNavDate = rs.getDate("fnavdate");
            }

            dbl.closeResultSetFinal(rs);
            rs = null;

            if (dMaxGuessValueDate != null && YssFun.dateDiff(YssFun.toDate(this.startDate), dMaxGuessValueDate) >= 0) {
                isHave = true;
                //返回 “1”+ 估值表里已确认状态的最大日期 + 哪个日期 查出 已经结算 了
                return "1" + "," + dMaxGuessValueDate.toString();
            } else if (dMaxNavDate != null && YssFun.dateDiff(YssFun.toDate(this.startDate), dMaxNavDate) >= 0) {
                isHave = true;
                //返回 “1”+ 净值表里已确认状态的最大日期 + 由哪个日期 查出 已经结算 了
                return "1" + "," + dMaxNavDate.toString();
            }

            if (!isHave) { //前台传过来的 多个/单个 日期 在 估值表和净值表里处于已确认状态的记录的日期 之后，返回 0,表明 前台 可以 操作
                return "0"; //在未结算状态，还有可能就是录入状态为空传前台一个标志。防止出错
            }
        }
        catch(Exception e){
            throw new YssException("查询净值表和估值表已确认数据出错！", e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
        return "";
    }
}
