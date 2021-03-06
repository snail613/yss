package com.yss.main.datainterface.cnstock;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 * QDV4.1赢时胜（上海）2009年4月20日03_A
 * MS00003
 * TB_XXX_DAO_BrokerRate 券商佣金利率设置表的实体类以及数据库访问对象
 * create by javachaos
 * 2009-06-17
 */
public class BrokerRateBean
    extends BaseDataSettingBean implements IDataSetting {
    private String assetGroupCode = null;   //组合群代码
    private String portCode = null;         //组合代码
    private String portName = null;         //组合名称
    private String brokerCode = null;       //券商代码
    private String brokerName = null;       //券商名称
    private String seatSite = null;         //席位地点 深圳或者上海
    private String seatCode = null;         //席位号
    private String seatName = null;         //席位名称
    private String speciesType = null;      //品种类型
    private String speciesName = null;      //品种名称
    private double yjRate = 0;              //佣金利率
    //add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
    private double bigYjRate = 0;           //大宗交易佣金利率
    private double startMoney = 0;          //起点金额
    private Date startDate = null;          //启用日期
    private int yjPreci = 0;                //计算深圳佣金小数保留位数
    private int yjCoursePreci = 0;          //计算深圳佣金过程中费用小数保留位数
    private String  strEQType = "";			//股票类型  panjunfang add 20100518
    //修改时原始主键
    private String oldPortCode = null;
    private String oldBrokerCode = null;
    private String oldSeatSite = null;
    private String oldSeatCode = null;
    private String oldSpeciesType = null;
    private Date oldStartDate = null;
    private String  stroldEQType = "";
    private BrokerRateBean filterType = null;
    private String sRecycled = "";

    /**
     * 检查数据的合法性
     * @author sunkey 20090814
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
    	//add by songjie 2010.03.29  国内:MS00949 QDII4.1赢时胜上海2010年03月27日02_B
    	if(btOper == YssCons.OP_EDIT || btOper == YssCons.OP_ADD){
    		checkInfo(btOper);
    	}
    	//add by songjie 2010.03.29  国内:MS00949 QDII4.1赢时胜上海2010年03月27日02_B
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_DAO_BrokerRate"),
                               "FASSETGROUPCODE,FPORTCODE,FBROKERCODE,FSEATSITE,FSEATCODE,FSPECIESTYPE,FStartDate,FEQType",
                               pub.getAssetGroupCode() + "," + this.portCode + "," + this.brokerCode + "," + this.seatSite + "," + this.seatCode + "," + this.speciesType + "," + YssFun.formatDate(this.startDate, "yyyy-MM-dd") + "," + this.strEQType,
                               pub.getAssetGroupCode() + "," + this.oldPortCode + "," + this.oldBrokerCode + "," + this.oldSeatSite + "," + this.oldSeatCode + "," + this.oldSpeciesType + "," + YssFun.formatDate(this.oldStartDate, "yyyy-MM-dd")+ "," + this.stroldEQType) ;
    }

    /**
     * 增加
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = true; //代表是否回滚事物
        try {

        	//品种信息和席位要排序后保存 sunkey@Modify 20091211 MS00853:赢时胜(上海)2009年12月09日02_B  
            strSql = "insert into " + pub.yssGetTableName("TB_DAO_BrokerRate") +
                "(FAssetGroupCode,FPortCode,FBrokerCode," +
                " FSeatSite,FSeatCode,FEQType," +  //增加股票类型  panjunfang add 20100519
                //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加FBigYjRate
                " FSpeciesType,FYJRate,FStartMoney,FStartDate,FYJPreci,FYJCoursePreci,FCheckState,FCreator,FCreateTime,FBigYjRate)" +
                " values(" +
                dbl.sqlString(pub.getAssetGroupCode()) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.brokerCode) + "," +
                dbl.sqlString(this.seatSite) + "," +
                dbl.sqlString(sortArr(this.seatCode,";")) + "," +
                dbl.sqlString(strEQType) + "," +
                dbl.sqlString(sortArr(this.speciesType, ";")) + "," +
                this.yjRate + "," +
                this.startMoney + "," +
                dbl.sqlDate(this.startDate) + "," +
                this.yjPreci + "," +
                this.yjCoursePreci + ",0," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," + 
                //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
                this.bigYjRate + ")";

            //开始事务处理
            conn.setAutoCommit(false);
            dbl.executeSql(strSql); //执行SQL
            conn.commit(); //提交
            bTrans = false;
            conn.setAutoCommit(true); //结束事务

        } catch (Exception e) {
            throw new YssException("新增券商佣金利率出错!", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    /**
     * 修改
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //代表是否回滚事物
        Connection conn = dbl.loadConnection();
        try {
        	//品种信息和席位要排序后保存 sunkey@Modify 20091211 MS00853:赢时胜(上海)2009年12月09日02_B
            strSql = "update " + pub.yssGetTableName("TB_DAO_BrokerRate") +
                " set FPortCode = " +
                dbl.sqlString(this.portCode) + ", FBrokerCode = " +
                dbl.sqlString(this.brokerCode) + " , FSeatSite = " +
                dbl.sqlString(this.seatSite) + ", FSeatCode = " +
                dbl.sqlString(sortArr(this.seatCode,";")) + ", FSpeciesType = " +
                dbl.sqlString(sortArr(this.speciesType, ";")) + ", FYJRate = " +
                this.yjRate + ", FStartMoney = " +
                this.startMoney + ",FStartDate = " +
                dbl.sqlDate(this.startDate) + ",FYJPreci = " +
                this.yjPreci + ", FYJCoursePreci = " +
                this.yjCoursePreci + ",FEQType = " + //增加股票类型  panjunfang add 20100519
                dbl.sqlString(this.strEQType) + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + 
                //add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加 FBigYjRate
                ", FBigYjRate = " + this.bigYjRate + 
                " where FPortCode = " +
                dbl.sqlString(this.oldPortCode) + " and FBrokerCode = " +
                dbl.sqlString(this.oldBrokerCode) + " and FSeatSite = " +
                dbl.sqlString(this.oldSeatSite) + " and FSeatCode = " +
                dbl.sqlString(this.oldSeatCode) + " and FSpeciesType = " +
                dbl.sqlString(this.oldSpeciesType) + " and FStartDate = " +
                dbl.sqlDate(this.oldStartDate) + " and FEQType = " + 
                dbl.sqlString(this.stroldEQType);

            //开始事务处理
            conn.setAutoCommit(false);
            dbl.executeSql(strSql); //执行SQL
            conn.commit(); //提交
            bTrans = false;
            conn.setAutoCommit(true); //结束事务
        } catch (Exception e) {
            throw new YssException("更新券商佣金利率出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    /**
     * 放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //代表是否回滚事物
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update " + pub.yssGetTableName("TB_DAO_BrokerRate") + " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FAssetGroupCode = " +
                dbl.sqlString(pub.getAssetGroupCode()) + " and FPortCode = " +
                dbl.sqlString(this.portCode) + " and FBrokerCode = " +
                dbl.sqlString(this.brokerCode) + " and FSeatSite = " +
                dbl.sqlString(this.seatSite) + " and FSeatCode = " +
                dbl.sqlString(this.seatCode) + " and FSpeciesType = " +
                dbl.sqlString(this.speciesType) + " and FStartDate = " +
                dbl.sqlDate(this.startDate) + " and FEQType = " + //增加股票类型  panjunfang add 20100519
                dbl.sqlString(this.strEQType);

            //开始事物处理
            conn.setAutoCommit(false);
            dbl.executeSql(strSql); //执行SQL
            conn.commit(); //提交
            bTrans = false;
            conn.setAutoCommit(true); //结束事务
        } catch (Exception e) {
            throw new YssException("删除券商佣金利率出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 审核/反审核
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //代表是否回滚事物
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        try {
            //开始事物处理
            conn.setAutoCommit(false);
            if (sRecycled != null && ! ("").equalsIgnoreCase(sRecycled)) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("TB_DAO_BrokerRate") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) +
                        " and FPortCode = " + dbl.sqlString(this.portCode) +
                        " and FBrokerCode = " + dbl.sqlString(this.brokerCode) +
                        " and FSeatSite = " + dbl.sqlString(this.seatSite) +
                        " and FSeatCode = " + dbl.sqlString(this.seatCode) +
                        " and FSpeciesType = " + dbl.sqlString(this.speciesType) +
                        " and FStartDate = " + dbl.sqlDate(this.startDate) + 
                    	" and FEQType = " + dbl.sqlString(this.strEQType);//增加股票类型  panjunfang add 20100519
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("审核券商佣金利率出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    /**
     * 删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = true; //代表是否回滚事物
        Connection conn = dbl.loadConnection(); //获取一个连接
        Statement st = null;
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                st = conn.createStatement();
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("TB_DAO_BrokerRate") +
                        " where FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) +
                        " and FPortCode = " + dbl.sqlString(this.portCode) +
                        " and FBrokerCode = " + dbl.sqlString(this.brokerCode) +
                        " and FSeatSite = " + dbl.sqlString(this.seatSite) +
                        " and FSeatCode = " + dbl.sqlString(this.seatCode) +
                        " and FSpeciesType = " + dbl.sqlString(this.speciesType) +
                        " and FStartDate = " + dbl.sqlDate(this.startDate) + 
                        " and FEQType = " + dbl.sqlString(this.strEQType);//增加股票类型  panjunfang add 20100519
                    st.addBatch(strSql);
                }
                st.executeBatch(); //执行批量处理
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
        }

    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    /**
     * 获得相关数据
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        StringBuffer bufSql = new StringBuffer();
        //逻辑(sunkey@注)：
        //1.从券商佣金利率表中查询数据，连接用户表获取创建人、审核人
        //2.左连接词汇表取出券商席位
        //3.左连接组合表取出组合名称，不区分审核状态...和开发人员沟通说无问题，等待测试
        //4.左连接券商表取出券商代码，不区分审核状态...和开发人员沟通说无问题，等待测试
        //5.左连接交易席位取出席位信息，不区分审核状态...和开发人员沟通说无问题，等待测试
        //6.左连接品种信息表取出品种信息,不区分审核状态...和开发人员沟通说无问题，等待测试
        bufSql.append("select distinct * from (select y.* from (select * from ").append(pub.yssGetTableName("TB_DAO_BrokerRate")).append(") x join ");
        bufSql.append(" (select a.*,b.FUserName as FCreatorName, b.FUserName as FCheckUserName, ");
        bufSql.append(" f.FVocName as FVocName1,d.fportname as fportname1,e.fbrokername as fbrokername1, ");
        bufSql.append(" h.fseatname as fseatname1,g.fcatname as fcatname1 ");
        bufSql.append(" from ").append(pub.yssGetTableName("TB_DAO_BrokerRate")).append(" a ");
        bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator =b.FUserCode ");
        bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser =c.FUserCode ");
        bufSql.append(" left join Tb_Fun_Vocabulary f on a.fseatsite = f.FVocCode and f.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_BROKER_RATEB));
        //bufSql.append(" left join Tb_Fun_Vocabulary fv on a.FEQType = fv.FVocCode and fv.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_BROKER_EQType));//增加股票类型  panjunfang add 20100519
        //------ modify by wangzuochun 2010.07.13  MS01433    当存在组合代码相同，启用日期不同的组合时，系统将会新建多条数据记录    QDV4赢时胜(测试)2010年7月12日02_B   
        //----------------------------------------------------------------------------------------------------
        bufSql.append(" left join (" );//edit by songjie 2011.03.15 不以最大的启用日期查询数据
        //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//        bufSql.append(pub.yssGetTableName("Tb_Para_Portfolio"));
//        bufSql.append(" where FStartDate <= " + dbl.sqlDate(new java.util.Date()));
        //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
        bufSql.append(" select FPortCode, FPortName, FStartDate from ");//edit by songjie 2011.03.15 不以最大的启用日期查询数据
        bufSql.append(pub.yssGetTableName("Tb_Para_Portfolio"));
        bufSql.append(" where FCheckState = 1) d on a.FPortCode = d.FPortCode ");//edit by songjie 2011.03.15 不以最大的启用日期查询数据
        //-------------------------------------------- MS01433 -------------------------------------------//
        
        //bufSql.append(" left join (select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + " ) d on a.fportcode = d.fportcode ");
        //--------------------------------------
        
        //edit by songjie 2010.03.29 MS00949 QDII4.1赢时胜上海2010年03月27日02_B 
        bufSql.append(" left join (");//edit by songjie 2011.03.15 不以最大的启用日期查询数据
        //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//        bufSql.append(" (select FBrokerCode, max(FSTARTDATE) as FStartDate from " + pub.yssGetTableName("Tb_Para_Broker"));
//        bufSql.append(" where fcheckstate = 1 group by FBrokerCode) broker1 ");
        //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
        //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
        bufSql.append("select FBrokerCode, FBrokerName, FStartDate from ");
        bufSql.append(pub.yssGetTableName("Tb_Para_Broker") + " where FCheckState = 1 ");
        bufSql.append(") e on a.fbrokercode = e.fbrokercode ");
        //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
        //edit by songjie 2010.03.29 MS00949 QDII4.1赢时胜上海2010年03月27日02_B
        bufSql.append(" left join (select * from ").append(pub.yssGetTableName("Tb_Para_TradeSeat")).append(" ) h on a.fseatcode = h.fseatnum ");//edited by zhouxiangMS01299    接口处理界面导入上海过户库时出现提示信息    QDV4赢时胜(测试)2010年6月12日2_B   
        bufSql.append(" left join (select * from ").append(pub.yssGetTableName("Tb_Base_Category")).append(" ) g on a.fspeciestype = g.fcatcode " + buildFilterSql() + ") y ");
        bufSql.append(" on y.fportcode = x.fportcode ");
        bufSql.append(" and y.fbrokercode = x.fbrokercode ");
        bufSql.append(" and y.fseatsite = x.fseatsite ");
        bufSql.append(" and y.fseatcode = x.fseatcode ");
        bufSql.append(" and y.fspeciestype = x.fspeciestype ");
        bufSql.append(" and y.fstartdate = x.fstartdate ");
        bufSql.append(" order by x.fportcode, x.fbrokercode, x.fseatsite, x.fseatcode, x.fspeciestype, x.fstartdate) tb");
        strSql = bufSql.toString();
        return builderListViewData(strSql);

    }

    /**
     * 前台ListView的值
     * @param strSql String
     * @return String
     * @throws YssException
     */
    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);

            sVocStr = vocabulary.getVoc(YssCons.YSS_BROKER_RATEB + "," + YssCons.YSS_BROKER_EQType);//增加股票类型  panjunfang add 20100519

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取文件头设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 封装数据，将结果集里的数据直接或间接赋予对应的属性
     * 由于席位和品种类型是可多选的，因此要特殊处理，从而匹配单个数据
     * @param rs ResultSet
     * @throws YssException
     */
    public void setAttr(ResultSet rs) throws YssException {
        try {
            String sql = "";
            this.portCode = rs.getString("FPortCode");      //组合代码
            this.portName = rs.getString("fportname1");     //组合名称
            this.brokerCode = rs.getString("FBrokercode");  //券商代码
            this.brokerName = rs.getString("fbrokername1"); //券商名称
            this.seatSite = rs.getString("FSeatSite");      //席位地点
            
            this.seatCode = rs.getString("FSeatCode");      //席位号
            this.seatName = rs.getString("fseatname1");     //席位名
            this.speciesType = rs.getString("FSpeciesType");//品种类型
            this.speciesName = rs.getString("fcatname1"); 	//品种名
            
            this.yjRate = rs.getDouble("FYJRate");          //佣金利率
            
            //add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
            this.bigYjRate = rs.getDouble("FBigYjRate");    //大宗交易佣金利率
            
            this.startMoney = rs.getDouble("FStartMoney");  //起点金额
            this.startDate = rs.getDate("FStartDate");      //启用日期
            this.yjPreci = rs.getInt("FYJPreci");           //计算深圳佣金小数保留位数
            this.yjCoursePreci = rs.getInt("FYJCoursePreci"); //计算深圳佣金过程中费用小数保留位数
            
            this.strEQType = rs.getString("FEQType");//增加股票类型  panjunfang add 20100519
			//==Start MS00853 处理多个席位号和多个品种类型的情况 sunkey@Modify 20091211 赢时胜(上海)2009年12月09日02_B
            //1.多个席位号的处理
            if (this.seatCode != null && this.seatCode.indexOf(";") != -1) {
				this.seatCode = this.seatCode.replaceAll(";", ",");
				sql = "select fseatcode,fseatname from " + pub.yssGetTableName("Tb_Para_TradeSeat") + " where fseatcode in (" + operSql.sqlCodes(this.seatCode) + ")";
				String[] str = Sql2Value(sql, this.seatCode.split(",")).split("\t");
				this.seatCode = str[0]; // 席位号
				this.seatName = str[1]; // 席位名
			}
            //2.多个品种类型的处理
			if (this.speciesType != null && this.speciesType.indexOf(";") != -1) {
				this.speciesType = this.speciesType.replaceAll(";", ",");
				sql = "select fcatcode,fcatname from " + pub.yssGetTableName("Tb_Base_Category") + " where fcatcode in (" + operSql.sqlCodes(this.speciesType) + ")";
				String[] str = Sql2Value(sql, this.speciesType.split(",")).split("\t");
				this.speciesType = str[0]; // 品种类型
				this.speciesName = str[1]; // 品种名
			}
			//======================================== End MS00853 ==============================================
            
            super.setRecLog(rs);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }
    
	/**
	 * 获取指定sql语句中的两个字段，并根据输入的数组顺序进行排序
	 * 
	 * @param strSql
	 *            查询语句，查询的第一个字段作为key，第二个字段作为value
	 * @param objKeySort
	 *            存放key的数组，获取的key：value将根据这个数组的顺序进行排序
	 * @return 按照数组排序后的key+\t+value
	 * @throws YssException
	 * @author sunkey@Add 20091211 MS00853:赢时胜(上海)2009年12月09日02_B
	 */
	public String Sql2Value(String strSql, Object[] objKeySort) throws YssException {
		ResultSet rs = null;
		Hashtable hashTmp = new Hashtable(); 		// 以键值对的形式存放查询出的数据
		StringBuffer bufKey = new StringBuffer(); 	// 用来存放所有的key，key与key之间用；分隔
		StringBuffer bufValue = new StringBuffer(); // 用来存放所有的value，value与value之间用；分隔
		try {
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				// 第一个是key 第二个是value
				hashTmp.put(rs.getObject(1), rs.getObject(2));
			}

			// 按照排序数组取出数据，并对数据进行组装
			for (int i = 0; i < objKeySort.length; i++) {
				// 1.添加key
				bufKey.append(objKeySort[i]);

				// 2.添加value，因为可能存在key对应的value不存在，此时用空格替代
				if (hashTmp.get(objKeySort[i]) == null) {
					bufValue.append(" ");
				} else {
					bufValue.append(hashTmp.get(objKeySort[i]));
				}
				// 每个key后加分号，value后加逗号，因为key会在前台转换成逗号，而value不会，最后一组值不加任何符号
				if (objKeySort.length - 1 != i) {
					bufKey.append(";");
					bufValue.append(",");
				}
			}

			// 将键、值用\t作为分隔符拼装起来
			return bufKey.append("\t").append(bufValue).toString();

		} catch (Exception e) {
			throw new YssException("匹配系统键值对数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
     * 将字符串根据分割符进行分割排序，并返回排序完的字符串
     * @param strSort	要排序的字符串
     * @param spliteMark分割符
     * @return 采用Arrays.sort()方法排序完的字符串
     * @author sunkey 20091211 MS00853:赢时胜(上海)2009年12月09日02_B(国内) 
     */
    private String sortArr(String strSort,String spliteMark){
    	//1.将要排序的字符串按照分隔符分割成数组
    	String [] strArr = strSort.split(spliteMark);
    	//2.对数据进行排序
    	Arrays.sort(strArr);
    	//3.重新按照分隔符组装成字符串
    	StringBuffer bufSort = new StringBuffer();
    	for(int i=0;i<strArr.length;i++){
    		bufSort.append(strArr[i]).append(spliteMark);
    	}
    	if(bufSort.toString().endsWith(spliteMark)){
    		bufSort.delete(bufSort.length()-1,bufSort.length());
    	}
    	//返回排序过的字符串
    	return bufSort.toString();
    }

    /**
     * 模糊查询
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (filterType.portCode.length() > 0) {     //组合代码
                sResult = sResult + " and a.FPortCode = " +
                    dbl.sqlString(filterType.portCode);
            }
            if (filterType.brokerCode.length() > 0) {   //券商代码
                sResult = sResult + " and a.FBrokerCode = " +
                    dbl.sqlString(filterType.brokerCode);
            }
            //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 排除99 的情况 99代表所有
            if (filterType.seatSite.length() > 0 && !filterType.seatSite.equals("99")) {     //席位地点
                sResult = sResult + " and a.FSeatSite = " +
                    dbl.sqlString(filterType.seatSite);
            }
            if (filterType.seatCode.length() > 0) {     //席位代码
                sResult = sResult + " and a.FSeatCode = " +
                    dbl.sqlString(filterType.seatCode);
            }
            if (filterType.speciesType.length() > 0) {  //品种类型
                sResult = sResult + " and a.FSpeciesType = " +
                    dbl.sqlString(filterType.speciesType);
            }
            
            //---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
            if (filterType.bigYjRate != -1 && filterType.bigYjRate != 0) {      //佣金利率
                sResult = sResult + " and a.FBigYJRate = " +
                    filterType.bigYjRate;
            }
            //---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
            
            //edit by songjie 2010.03.27 MS00939 QDV4赢时胜(测试)2010年3月25日4_B
            if (filterType.yjRate != -1 && filterType.yjRate != 0) {      //佣金利率
                sResult = sResult + " and a.FYJRate = " +
                    filterType.yjRate;
            }
            //edit by songjie 2010.03.27 MS00939 QDV4赢时胜(测试)2010年3月25日4_B
            if (filterType.startMoney != -1 && filterType.startMoney != 0) {  //起点金额
                sResult = sResult + " and a.FStartMoney = " +
                    filterType.startMoney;
            }
            //启用日期
            //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 如果启用日期为 9998-12-31 则不根据该日期筛选数据，9998-12-31 代表 所有日期
            if(filterType.startDate != null && !YssFun.formatDate(filterType.startDate, "yyyy-MM-dd").equals("9998-12-31")){
            	sResult = sResult + " and a.FStartDate <= " + dbl.sqlDate(filterType.startDate);
            }
            //edit by songjie 2010.03.27 MS00939 QDV4赢时胜(测试)2010年3月25日4_B
            if (filterType.yjPreci != -1 && filterType.yjPreci != 0) {     //计算深圳佣金小数保留位数
                sResult = sResult + " and a.FYJPreci = " +
                    filterType.yjPreci;
            }
            //edit by songjie 2010.03.27 MS00939 QDV4赢时胜(测试)2010年3月25日4_B
            if (filterType.yjCoursePreci != -1 && filterType.yjCoursePreci != 0) { //计算深圳佣金过程中费用小数保留位数
                sResult = sResult + " and a.FYJCoursePreci = " +
                    filterType.yjCoursePreci;
            }
        }
        return sResult;

    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     * 分解数据
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0]; //modify by wangzuochun 2010.07.09 MS01413    点击筛选按钮对数据进行筛选后，选择任意一条数据进行审核/反审核，系统会报错   
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.portCode = reqAry[0];
            this.brokerCode = reqAry[1];
            this.seatSite = reqAry[2];
            this.seatCode = reqAry[3];
            this.speciesType = reqAry[4];
            this.yjRate = YssFun.toDouble(reqAry[5]);
            this.startMoney = YssFun.toDouble(reqAry[6]);
            this.startDate = YssFun.toDate(reqAry[7]);
            this.yjPreci = YssFun.toInt(reqAry[8]);
            this.yjCoursePreci = YssFun.toInt(reqAry[9]);
            this.checkStateId = YssFun.toInt(reqAry[10]);
            this.oldPortCode = reqAry[11];
            this.oldBrokerCode = reqAry[12];
            this.oldSeatSite = reqAry[13];
            this.oldSeatCode = reqAry[14];
            this.oldSpeciesType = reqAry[15];
            this.oldStartDate = YssFun.toDate(reqAry[16]);
            this.strEQType = reqAry[17];
            this.stroldEQType = reqAry[18];
            //add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
            this.bigYjRate = YssFun.toDouble(reqAry[19]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new BrokerRateBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析信息出错");
        }

    }

    /**
     * 组装数据
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.brokerCode).append("\t");
        buf.append(this.brokerName).append("\t");
        buf.append(this.seatSite).append("\t");
        buf.append(this.seatCode).append("\t");
        buf.append(this.seatName).append("\t");
        buf.append(this.speciesType).append("\t");
        buf.append(this.speciesName).append("\t");
        buf.append(this.yjRate).append("\t");
        buf.append(this.startMoney).append("\t");
        buf.append(YssFun.formatDate(this.startDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.yjPreci).append("\t");
        buf.append(this.yjCoursePreci).append("\t");
        buf.append(this.strEQType).append("\t");
        //add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
        buf.append(this.bigYjRate).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * 返回Map对象，里面封装BrokerReateBean（券商佣金利率）对象，key为主键，value为对象.主键为空格隔开
     * edit by songjie 2010.03.22 MS00924 QDV4赢时胜（测试）2010年03月19日02_B
     * B股业务 ，edit by panjunfang 20100519 修正sql，并在key中加上股票类型
     * @return Map
     */
    public Map getBrokerReateBean(java.util.Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String errmsg = "";
        Map map = new HashMap();
        BrokerRateBean br = null;
        String key = "";
        String groupCode = "";      //组合群代码
        String portCode = "";       //组合代码
        String brokerCode = "";     //券商代码
        String seatSites = "";      //席位地点
        String[] seatSite;          //席位地点数组
        String seatCodes = "";      //席位号
        String[] seatCode;          //席位号数组
        String speciesTypes = "";   //品种类型
        String[] speciesType;       //品种类型数组
        double yjRate = 0;          //佣金费率
        //add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
        double bigYjRate = 0;       //大宗交易佣金利率 
        double startMoney = 0;      //起点金额
        Date startDate = null;      //启用日期
        int yjPreci = 0;            //计算深圳佣金小数保留位数
        int yjCoursePreci = 0;      //计算深圳佣金过程中费用小数保留位数
        String sEQType = "";		//股票类型：A、B股
        try {
            errmsg = "读取券商佣金利率出错！";
            strSql = "select * from " + pub.yssGetTableName("TB_DAO_BrokerRate") + " a  join (" +
                "select max(fstartdate) as maxdate,FASSETGROUPCODE,FPORTCODE,FBROKERCODE,FSEATSITE,FSEATCODE,FSPECIESTYPE,FEQType from " + pub.yssGetTableName("tb_dao_brokerrate") + 
                //edit by songjie 2010.03.22 MS00924 QDV4赢时胜（测试）2010年03月19日02_B
                " where fcheckstate=1 and fstartdate<=" + dbl.sqlDate(dDate) +
                " group by FASSETGROUPCODE, FPORTCODE, FBROKERCODE, FSEATSITE, " +
                "FSEATCODE, FSPECIESTYPE,FEQType) b on a.fstartdate = b.maxdate " + 
                " and a.FASSETGROUPCODE = b.FASSETGROUPCODE and a.FPORTCODE = b.FPORTCODE and a.FBROKERCODE = b.FBROKERCODE and a.FSEATSITE = b.FSEATSITE" + 
                " and a.FSEATCODE = b.FSEATCODE and a.FSPECIESTYPE = b.FSPECIESTYPE and a.FEQType = b.FEQType " + 
                "where a.FCheckState = 1 and a.FAssetGroupCode = "
                + dbl.sqlString(pub.getAssetGroupCode());
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                groupCode = rs.getString("FAssetGroupCode");
                portCode = rs.getString("FPortCode");
                brokerCode = rs.getString("FBrokerCode");
                seatSites = rs.getString("FSeatSite");
                seatCodes = rs.getString("FSeatCode");
                speciesTypes = rs.getString("FSpeciesType");
                yjRate = rs.getDouble("FYJRate");
                //add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
                bigYjRate = rs.getDouble("FBigYjRate");
                startMoney = rs.getDouble("FStartMoney");
                startDate = rs.getDate("FStartDate");
                yjPreci = rs.getInt("FYJPreci");
                yjCoursePreci = rs.getInt("FYJCoursePreci");
                sEQType = rs.getString("FEQType");
                seatCode = seatCodes.split(";");
                for (int j = 0; j < seatCode.length; j++) {     //席位号
                    seatSite = seatSites.split(",");
                    for (int k = 0; k < seatSite.length; k++) { //席位地点
                        speciesType = speciesTypes.split(";");
                        for (int q = 0; q < speciesType.length; q++) {
                            br = new BrokerRateBean();
                            br.setAssetGroupCode(groupCode);    //组合群代码
                            br.setPortCode(portCode);           //组合代码
                            br.setBrokerCode(brokerCode);       //券商代码
                            br.setSeatSite(seatSite[k]);        //席位地点
                            br.setSeatCode(seatCode[j]);        //席位号
                            br.setSpeciesType(speciesType[q]);  //品种类型
                            br.setYjRate(yjRate);               //佣金费率
                            //add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
                            br.setBigYjRate(bigYjRate);         //大宗交易佣金利率
                            br.setStartMoney(startMoney);       //起点金额
                            br.setStartDate(startDate);         //启用日期
                            br.setYjPreci(yjPreci);             //计算深圳佣金小数保留位数
                            br.setYjCoursePreci(yjCoursePreci); //计算深圳佣金过程中费用小数保留位数
                            key = groupCode + " " + portCode + " " + brokerCode + " " + seatSite[k] + " " + seatCode[j] + " " + speciesType[q] 
                                   + ((speciesType[q].equals("EQ") && sEQType.equals("B")) ? " " + sEQType : "");//如果是B股，则将股票类型也就加为主键
                            map.put(key, br);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException(errmsg, e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return map;

    }

    /**
     * 构造函数
     */
    public BrokerRateBean() {
    }

    public double getYjRate() {
        return yjRate;
    }

    public int getYjPreci() {
        return yjPreci;
    }
    
    //---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
    public void setBigYjRate(double bigYjRate) {
        this.bigYjRate = bigYjRate;
    }
    public double getBigYjRate(){
    	return bigYjRate;
    }
    //---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
    
    public int getYjCoursePreci() {
        return yjCoursePreci;
    }

    public double getStartMoney() {
        return startMoney;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getSpeciesType() {
        return speciesType;
    }

    public String getSeatSite() {
        return seatSite;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public void setYjRate(double yjRate) {
        this.yjRate = yjRate;
    }

    public void setYjPreci(int yjPreci) {
        this.yjPreci = yjPreci;
    }

    public void setYjCoursePreci(int yjCoursePreci) {
        this.yjCoursePreci = yjCoursePreci;
    }

    public void setStartMoney(double startMoney) {
        this.startMoney = startMoney;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setSpeciesType(String speciesType) {
        this.speciesType = speciesType;
    }

    public void setSeatSite(String seatSite) {
        this.seatSite = seatSite;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setOldBrokerCode(String oldBrokerCode) {
        this.oldBrokerCode = oldBrokerCode;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setOldSeatCode(String oldSeatCode) {
        this.oldSeatCode = oldSeatCode;
    }

    public void setOldSeatSite(String oldSeatSite) {
        this.oldSeatSite = oldSeatSite;
    }

    public void setOldSpeciesType(String oldSpeciesType) {
        this.oldSpeciesType = oldSpeciesType;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setFilterType(BrokerRateBean filterType) {
        this.filterType = filterType;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setOldStartDate(Date oldStartDate) {
        this.oldStartDate = oldStartDate;
    }

    public String getOldBrokerCode() {
        return oldBrokerCode;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getOldSeatCode() {
        return oldSeatCode;
    }

    public String getOldSeatSite() {
        return oldSeatSite;
    }

    public String getOldSpeciesType() {
        return oldSpeciesType;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public BrokerRateBean getFilterType() {
        return filterType;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    /**
     * add by songjie
     * 2010.03.29
     * 国内：MS00949
     * QDII4.1赢时胜上海2010年03月27日02_B
     * @param btOper
     * @param sKeyField
     * @throws YssException
     */
	public void checkInfo(byte btOper) throws YssException {
		String sNewKeyValue = "";
		String sOldKeyValue = "";
		String whereSql = "";
		String[] seatCodes = null;
		String[] speciesTypes = null;
		String exchangeName = "";
		try {
			if(this.seatSite.equals("1")){
				exchangeName = "深交所";
			}
			if(this.seatSite.equals("2")){
				exchangeName = "上交所";
			}			
			if(this.seatSite.equals("1,2")){
				exchangeName = "上交所和深交所";
			}					
			
			//新的主键值
			sNewKeyValue = pub.getAssetGroupCode() + "," + this.portCode + "," + this.brokerCode + "," + 
			this.seatSite + "," + this.seatCode + "," + this.speciesType + "," + 
			YssFun.formatDate(this.startDate, "yyyy-MM-dd") + "," + this.strEQType;//增加股票类型  panjunfang add 20100519
			
			//旧的主键值
			sOldKeyValue = pub.getAssetGroupCode() + "," + this.oldPortCode + "," + this.oldBrokerCode + 
			"," + this.oldSeatSite + "," + this.oldSeatCode + "," + 
			this.oldSpeciesType + "," + YssFun.formatDate(this.oldStartDate, "yyyy-MM-dd") + 
			"," + this.stroldEQType;//增加股票类型  panjunfang add 20100519
			
			//若修改前和修改后的主键相同 则退出
			if (sNewKeyValue.equalsIgnoreCase(sOldKeyValue)) {
				return;
			}
			
			//若席位代码包含; 
			if(this.seatCode.indexOf(";") != -1){
				//则拆分席位代码
				seatCodes = seatCode.split(";");
				for(int i = 0; i < seatCodes.length; i++){
					if(this.speciesType.indexOf(";") != -1){
						//拆分品种代码
						speciesTypes = this.speciesType.split(";");
						for(int j = 0; j < speciesTypes.length; j++){
							sNewKeyValue = 
							//"组合群代码  = " + pub.getAssetGroupCode() + "," +    //修改提示信息BY yanghaiming 201007011 MS01384 QDV4赢时胜(测试)2010年07月01日01_AB 
							this.portCode + "," + 
						    this.brokerCode + "," + 
							seatCodes[i] + "," + 
							speciesTypes[j] + "," + 
							YssFun.formatDate(this.startDate, "yyyy-MM-dd");
							
							whereSql = " FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) + 
							" and FPORTCODE = " + dbl.sqlString(this.portCode) + 
							" and FBROKERCODE = " + dbl.sqlString(this.brokerCode) + 
							" and FSeatCode like '%" + seatCodes[i] + "%'" +
							" and FSEATSITE = " + dbl.sqlString(this.seatSite) + 
							" and FSPECIESTYPE like '%" + speciesTypes[j] + "%'" +
							" and FStartDate = " + dbl.sqlDate(this.startDate) + 
							" and FEQType = " + dbl.sqlString(this.strEQType);//增加股票类型  panjunfang add 20100519
							
							if(btOper == YssCons.OP_EDIT){
								whereSql += " and FSeatCode <> " + dbl.sqlString(this.oldSeatCode) +
								" and FSPECIESTYPE <> " + dbl.sqlString(this.oldSpeciesType);
							}
							
							subCheckInfo(whereSql, sNewKeyValue);
						}
					}else{
						sNewKeyValue = 
						this.portCode + "," + 
						this.brokerCode + "," + 
						seatCodes[i] + "," + 
						this.speciesType + "," + 
						YssFun.formatDate(this.startDate, "yyyy-MM-dd");
						
						whereSql = " FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) +
						" and FPORTCODE = " + dbl.sqlString(this.portCode) +
						" and FBROKERCODE = " + dbl.sqlString(this.brokerCode) + 
						" and FSeatCode like '%" + seatCodes[i] + "%'" +
						" and FSEATSITE = " + dbl.sqlString(this.seatSite) + 
						" and FSPECIESTYPE like '%" + this.speciesType + "%'" +  
						" and FStartDate = " + dbl.sqlDate(this.startDate) + 
						" and FEQType = " + dbl.sqlString(this.strEQType);//增加股票类型  panjunfang add 20100519
						
						if(btOper == YssCons.OP_EDIT){
							whereSql += " and FSeatCode <> " + dbl.sqlString(this.oldSeatCode) +
							" and FSPECIESTYPE <> " + dbl.sqlString(this.oldSpeciesType);
						}
						
						subCheckInfo(whereSql, sNewKeyValue);
					}

				}
			}else{
				if(this.speciesType.indexOf(";") != -1){
					speciesTypes = this.speciesType.split(";");
					for(int j = 0; j < speciesTypes.length; j++){
						sNewKeyValue = 
						//"组合群代码  = " + pub.getAssetGroupCode() + "," + 
						this.portCode + "," + 
						this.brokerCode + "," + 
						this.seatCode + "," + 
						speciesTypes[j] + "," + 
						YssFun.formatDate(this.startDate, "yyyy-MM-dd");
						
						whereSql = " FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) + 
						" and FPORTCODE = " + dbl.sqlString(this.portCode) + 
						" and FBROKERCODE = " + dbl.sqlString(this.brokerCode) + 
						" and FSeatCode like '%" + this.seatCode + "%'" +
						" and FSEATSITE = " + dbl.sqlString(this.seatSite) + 
						" and FSPECIESTYPE like '%" + speciesTypes[j] + "%'" +
						" and FStartDate = " + dbl.sqlDate(this.startDate) + 
						" and FEQType = " + dbl.sqlString(this.strEQType);//增加股票类型  panjunfang add 20100519
						
						if(btOper == YssCons.OP_EDIT){
							whereSql += " and FSeatCode <> " + dbl.sqlString(this.oldSeatCode) +
							" and FSPECIESTYPE <> " + dbl.sqlString(this.oldSpeciesType);
						}
						
						subCheckInfo(whereSql, sNewKeyValue);
					}
				}else{
					sNewKeyValue = 
					//"组合群代码  = " + pub.getAssetGroupCode() + "," + 
					this.portCode + "," + 
					this.brokerCode + "," + 
					this.seatCode + "," + 
					this.speciesType + "," + 
					YssFun.formatDate(this.startDate, "yyyy-MM-dd");
					
					whereSql = " FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) + 
					" and FPORTCODE = " + dbl.sqlString(this.portCode) + 
					" and FBROKERCODE = " + dbl.sqlString(this.brokerCode) + 
					" and FSeatCode like '%" + this.seatCode + "%'" +
					" and FSEATSITE = " + dbl.sqlString(this.seatSite) + 
					" and FSPECIESTYPE like '%" + this.speciesType + "%'" +
					" and FStartDate = " + dbl.sqlDate(this.startDate) + 
					" and FEQType = " + dbl.sqlString(this.strEQType);//增加股票类型  panjunfang add 20100519
					
					if(btOper == YssCons.OP_EDIT){
						whereSql += " and FSeatCode <> " + dbl.sqlString(this.oldSeatCode) +
						" and FSPECIESTYPE <> " + dbl.sqlString(this.oldSpeciesType);
					}
					
					subCheckInfo(whereSql, sNewKeyValue);
				}
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	
	/**
	 * add by songjie
	 * 2010.03.30
	 * 国内：MS00949
	 * QDII4.1赢时胜上海2010年03月27日02_B
	 * @param btOper
	 * @throws YssException
	 */
	private void subCheckInfo(String whereSql, String sNewKeyValue)throws YssException{
		ResultSet rs = null;
		String strSql = "";
		String sTmpError = "";
		String sTmpState = "";
		try {
			strSql = "select FASSETGROUPCODE,FPORTCODE,FBROKERCODE,FSEATSITE," + 
			         "FSEATCODE,FSPECIESTYPE,FStartDate,FCheckState from " + 
					 pub.yssGetTableName("TB_DAO_BrokerRate") + " where " + whereSql;
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				if (pub.getSysCheckState() || rs.getInt("FCheckState") == YssCons.RS_DEL) {
					sTmpError = "【" + YssFun.getCheckStateName(rs.getInt("FCheckState")) + "】中已经存在";
					sTmpState = rs.getInt("FCheckState") == 2 ? "请还原该信息" : "请重新输入";
					throw new YssException(sTmpError + "【" + sNewKeyValue + "】的信息，" + sTmpState);
				} else {
					throw new YssException("【" + sNewKeyValue + "】的信息已经存在，请重新输入");
				}
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
}
