package com.yss.main.operdata;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.main.funsetting.VocabularyBean;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * add by wangzuochun 2009.07.28  MS00015  国内权益处理   QDV4.1赢时胜（上海）2009年4月20日15_A
 *
 * <p>Title: MayApartBondBean</p>
 *
 * <p>Description: 可分离债送配业务</p>
 *
 */
public class MayApartBondBean
    extends BaseDataSettingBean implements IDataSetting {

    private String securityCode = ""; //证券代码
    private String securityName = ""; //证券名称
    private String tSecurityCode = ""; //权证代码
    private String tSecurityName = ""; //权证名称
    private String recordDate = ""; //权益确认日
    private String exRightDate = ""; //除权日
    private String accountType = ""; //计算方式
    private String preTaxRatio = "0"; //税前权益比例
    private String afterTaxRatio = "0"; //税后权益比例
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String assetGroupCode = ""; //组合群代码
    private String oldSecurityCode = "";
    private String oldRecordDate = "";
    private String oldAssetGroupCode = ""; //组合群代码
    private String oldPortCode = ""; //组合代码
    private String desc = ""; //描述
//    private String strisOnlyColumnss = "0"; //在初始登陆时是否只显示列，不查询数据
    private String sRecycled = ""; //保存未解析前的字符串
    private MayApartBondBean filterType;

    public MayApartBondBean() {
    }

    /**
     * checkInput
     * 检查用户输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_MayApartBond"),
                               "FSecurityCode,FRecordDate,FPortCode,FASSETGROUPCODE",
                               this.securityCode + "," + this.recordDate + "," +
                               (this.portCode.length() == 0 ? " " :this.portCode) + ","+ assetGroupCode,
                               this.oldSecurityCode + "," + this.oldRecordDate + ","
                               + (this.oldPortCode.length() == 0 ? " " :this.oldPortCode) + "," + oldAssetGroupCode);
    }

    /**
     * 增加可分离债送配业务操作，对应前台的“新建”
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            //SQL插入语句：向现金对价表插入数据
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Data_MayApartBond") +
                "(FSecurityCode,FTSecurityCode,FRecordDate,FExRightDate,FPreTaxRatio,FAfterTaxRatio,"+
                "FPortCode,FAssetGroupCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FAccountType)" +
                " values(" + dbl.sqlString(this.securityCode) + "," +
                dbl.sqlString(this.tSecurityCode) + "," +
                dbl.sqlDate(this.recordDate) + "," +
                dbl.sqlDate(this.exRightDate) + "," +
                (this.preTaxRatio.trim().equals("0")?"0":this.preTaxRatio) + "," +
                (this.afterTaxRatio.trim().equals("0")?"0":this.afterTaxRatio)+","+
                dbl.sqlString(this.portCode.length() == 0 ? " " : this.portCode) + "," +
                dbl.sqlString(this.assetGroupCode.trim().length() > 0 ? this.assetGroupCode : " ") +","+
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," +
                this.accountType + "" + ")";
            System.out.println("SQL=" + strSql);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增可分离债送配业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
    * 修改可分离债送配业务操作，对应前台的“修改”
    * @return String
    * @throws YssException
    */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            //SQL更新语句：更新可分离债送配业务表中修改操作所对应的记录
            strSql =
                "update " + pub.yssGetTableName("Tb_Data_MayApartBond") +
                " set FSecurityCode = " + dbl.sqlString(this.securityCode) +
                ",FTSecurityCode = " + dbl.sqlString(this.tSecurityCode) +
                ",FRecordDate = " + dbl.sqlDate(this.recordDate) +
                ",FExRightDate = " + dbl.sqlDate(this.exRightDate) +
                ",FPreTaxRatio = " + this.preTaxRatio +
                ",FAfterTaxRatio ="+this.afterTaxRatio+
                ",FPortCode = "+ dbl.sqlString(this.portCode.length() == 0 ? " " : this.portCode) +
                ",FASSETGROUPCODE=" + dbl.sqlString(assetGroupCode.trim().length() > 0 ? assetGroupCode : " ") +
                ",FDesc = " + dbl.sqlString(this.desc) +
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                // ",FDivdendType =" +dbl.sqlString(this.DividentType+"") +   lzp  modify   200712.7   bit不能转换为STRING
                ",FAccountType =" + this.accountType +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
                " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
                " and FPortCode=" + dbl.sqlString(this.oldPortCode.length() == 0 ? " " : this.oldPortCode) +
                " and FASSETGROUPCODE=" + dbl.sqlString(oldAssetGroupCode.trim().length() > 0 ? oldAssetGroupCode : " ");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改可分离债送配业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
    * 删除可分离债送配业务操作，对应前台的“删除”
    * @return String
    * @throws YssException
    */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //SQL更新语句：更新可分离债送配业务表中删除操作对应的记录，重新设置审核状态字段FCheckState
            strSql = "update " + pub.yssGetTableName("Tb_Data_MayApartBond") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = " +
                dbl.sqlString(this.securityCode) +
                " and FRecordDate = " + dbl.sqlDate(this.recordDate) +
                " and FPortCode=" + dbl.sqlString(this.portCode.length() == 0 ? " " : this.portCode) +
                " and FASSETGROUPCODE=" + dbl.sqlString(assetGroupCode.trim().length() > 0 ? assetGroupCode : " ");

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除可分离债送配业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 可以处理可分离债送配业务审核、反审核、回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null&& (!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    //SQL更新语句：更新现金对价表中审核、反审核、还原操作对应的记录，重新设置审核状态字段FCheckState
                    strSql = "update " + pub.yssGetTableName("Tb_Data_MayApartBond") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FSecurityCode = " +
                        dbl.sqlString(this.securityCode) +
                        " and FRecordDate = " + dbl.sqlDate(this.recordDate) +
                        " and FPortCode=" +dbl.sqlString(this.portCode.length() == 0 ? " " :this.portCode)+
                        " and FASSETGROUPCODE=" + dbl.sqlString(assetGroupCode.trim().length() > 0 ? assetGroupCode : " ");
                        //------------------------end----------------//
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核可分离债送配业务数据出错", e);
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
     * 从回收站彻底删除数据,单条和多条信息都可以
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    //SQL删除语句：从现金对价表中删除清除操作所对应的记录
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_MayApartBond") +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.securityCode) +
                        " and FRecordDate = " + dbl.sqlDate(this.recordDate) +
                        " and FPortCode=" +dbl.sqlString(this.portCode.length() == 0 ? " " :this.portCode) +
                        " and FASSETGROUPCODE=" + dbl.sqlString(assetGroupCode.trim().length() > 0 ? assetGroupCode : " ");
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("清除可分离债送配业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
     * 按条件查询出可分离债送配业务表的数据并以一定格式显示，并显示回收站的数据
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {

        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.isOnlyColumns.equals("1")&&!(pub.isBrown())) {
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_BOND_APART);

                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FSecurityName as FSecurityName" +
                ",n.Fvocname as AccountType,h.FPortName as FPortName,f.FSecurityName as FTSecurityName" + " from " +
                pub.yssGetTableName("Tb_Data_MayApartBond") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                
                //------ modify by wangzuochun  2010.07.16  MS01449    组合代码相同而启用日期不同的组合时，新建买入证券据，进行库存统计后，现金库存会增倍 QDV4赢时胜(测试)2010年7月15日01_B 
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FPortCode, FPortName, FStartDate, FPortCury from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Portfolio") + 
                " where FCheckState = 1) h on a.FPortCode = h.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //-------------------------------------------- MS01449 -------------------------------------------//
                
                " left join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + ") d on a.FSecurityCode = d.FSecurityCode " +

                " left join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + ") f on a.FTSecurityCode = f.FSecurityCode " +

                " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FAccountType") + " = n.FVocCode and n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_BOND_APART) +
                buildFilterSql() +
                (this.filterType.assetGroupCode.trim().length()==0?" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE='" + pub.getAssetGroupCode() + "')" :"")+
                " order by a.FCheckState, a.FCreateTime desc";

            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("MayApartBond");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_BOND_APART);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji

        } catch (Exception e) {
            throw new YssException("获取可分离债送配业务信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
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
     * 解析前台发送来的字符串
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
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.securityCode = reqAry[0];
            this.tSecurityCode = reqAry[1];
            this.recordDate = reqAry[2];
            this.exRightDate = reqAry[3];

            this.preTaxRatio = reqAry[4];
            this.afterTaxRatio=reqAry[5];
            this.portCode=reqAry[6];
            this.assetGroupCode=reqAry[7];
            this.accountType = reqAry[8];
            if (reqAry[8].trim().length() == 0) {
                this.accountType = " ";
            }
            this.desc = reqAry[9];
            this.isOnlyColumns = reqAry[10];
            this.checkStateId = Integer.parseInt(reqAry[11]);
            this.oldSecurityCode = reqAry[12];
            this.oldAssetGroupCode=reqAry[13];
            this.oldPortCode=reqAry[14];
            this.oldRecordDate = reqAry[15];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new MayApartBondBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析数据信息出错", e);
        }
    }

    /**
     * 通过拼接字符串来获取数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.tSecurityCode).append("\t");
        buf.append(this.tSecurityName).append("\t");
        buf.append(this.recordDate).append("\t");
        buf.append(this.exRightDate).append("\t");
        buf.append(this.preTaxRatio).append("\t");
        buf.append(this.afterTaxRatio).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.assetGroupCode).append("\t");
        buf.append(this.accountType).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }


    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * 筛选条件
     * @throws YssException
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
        if (this.filterType != null) {
            sResult = " where 1=1";

            if (this.filterType.isOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }

            if (this.filterType.securityCode.trim().length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.securityCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.tSecurityCode.trim().length() != 0) {
               sResult = sResult + " and a.FTSecurityCode like '" +
                   filterType.tSecurityCode.replaceAll("'", "''") + "%'";
           }
            if (this.filterType.recordDate.length() != 0 &&
                !this.filterType.recordDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FRecordDate = " +
                    dbl.sqlDate(filterType.recordDate);
            }
            if (this.filterType.exRightDate.length() != 0 &&
                !this.filterType.exRightDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FExRightDate = " +
                    dbl.sqlDate(filterType.exRightDate);
            }

            if (!this.filterType.preTaxRatio.equals("0")) {
                sResult = sResult + " and a.FPreTaxRatio like '" +
                    filterType.preTaxRatio.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.afterTaxRatio.equals("0")) {
                sResult = sResult + " and a.FAfterTaxRatio like '" +
                    filterType.afterTaxRatio.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
            if (! (this.filterType.accountType.trim().equals("99") || this.filterType.accountType.trim().length() == 0)) {
                sResult += " and a.FAccountType =" +
                    this.filterType.accountType.trim().replaceAll("'", "''");
            }
            if (this.filterType.portCode.length() != 0) {
               sResult = sResult + " and a.FPortCode like '" +
                   filterType.portCode.replaceAll("'", "''") + "%'";
           }
           if (this.filterType.assetGroupCode.trim().length() != 0) {
               sResult = sResult + " and a.FASSETGROUPCODE ='" +
                   filterType.assetGroupCode.replaceAll("'", "''") + "'";
           }
        }
        return sResult;
    }

    /**
     * 为各项变量赋值
     * @param rs ResultSet
     * @throws SQLException
     * @throws YssException
     */
    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.securityCode = rs.getString("FSecurityCode") + "";
        this.securityName = rs.getString("FSecurityName") + "";
        this.tSecurityCode = rs.getString("FTSecurityCode") + "";
        this.tSecurityName = rs.getString("FTSecurityName") + "";
        this.recordDate = rs.getDate("FRecordDate") + "";
        this.exRightDate = rs.getDate("FExRightDate") + "";
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        this.assetGroupCode = rs.getString("FASSETGROUPCODE");
        this.preTaxRatio = rs.getBigDecimal("FPreTaxRatio").toString() + "";
        this.afterTaxRatio = rs.getBigDecimal("FAfterTaxRatio").toString() + "";
        this.desc = rs.getString("FDesc") + "";
        this.accountType = rs.getString("FAccountType");
        super.setRecLog(rs);

    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortName() {
        return portName;
    }

    public String getPreTaxRatio() {
        return preTaxRatio;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getDesc() {
        return desc;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAfterTaxRatio() {
        return afterTaxRatio;
    }

    public String getExRightDate() {
        return exRightDate;
    }

    public String getOldAssetGroupCode() {
        return oldAssetGroupCode;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getTSecurityCode() {
        return tSecurityCode;
    }

    public String getTSecurityName() {
        return tSecurityName;
    }

    public String getOldRecordDate() {
        return oldRecordDate;
    }

    public String getOldSecurityCode() {
        return oldSecurityCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setPreTaxRatio(String preTaxRatio) {
        this.preTaxRatio = preTaxRatio;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setAfterTaxRatio(String afterTaxRatio) {
        this.afterTaxRatio = afterTaxRatio;
    }

    public void setExRightDate(String exRightDate) {
        this.exRightDate = exRightDate;
    }

    public void setOldAssetGroupCode(String oldAssetGroupCode) {
        this.oldAssetGroupCode = oldAssetGroupCode;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setTSecurityCode(String tSecurityCode) {
        this.tSecurityCode = tSecurityCode;
    }

    public void setTSecurityName(String tSecurityName) {
        this.tSecurityName = tSecurityName;
    }

    public void setOldRecordDate(String oldRecordDate) {
        this.oldRecordDate = oldRecordDate;
    }

    public void setOldSecurityCode(String oldSecurityCode) {
        this.oldSecurityCode = oldSecurityCode;
    }
}
