package com.yss.main.datainterface.cnstock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * QDV4.1赢时胜（上海）2009年4月20日03_A
 * MS00003
 * TB_XXX_DAO_RateSpeciesType 交易费率品种设置表的实体类以及数据库访问对象
 * create by javachaos
 * 2009-06-17
 */
public class RateSpeciesTypeBean
    extends BaseDataSettingBean implements IDataSetting {

    double exchangeRate = 0;
    double bigExchange = 0;
    double startMoney = 0;
    java.util.Date FStartDate = null;
    private String rateType;        //费率类型
    private String oldRateType;     //原始费率类型
    private String rateSpecies;     //费率品种
    private String oldRrateSpecies; //原始费率品种
    private String exchangeRateS;   //交易费率
    private String bigExchangeS;    //大宗交易费率
    private String startMoneyS;     //起点金额
    private String startDateS;      //启用日期
    private String ETFCode;         //ETF代码
    private String sRecycled;       //原始字符串
    private String oldstartDate;    //原始启用日期
    private String upperLimit;      //费用上限  add by yanghaiming 20100417 B股业务
    double upperLimitS = 0;
    

	

	private RateSpeciesTypeBean filterType;
    public RateSpeciesTypeBean() {
    }

    /**
     * 检查数据合法性 sunkey@Modify
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_DAO_RateSpeciesType"),
                               "FRateType,FRateSpecies,FStartDate",
                               this.rateType + "," + this.rateSpecies + "," + this.startDateS,
                               this.oldRateType + "," + this.oldRrateSpecies + "," + this.oldstartDate);
    }

    /**
     * 增加
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        try {
            strSql = "insert into " + pub.yssGetTableName("TB_DAO_RateSpeciesType") + 
                "(FRateType,FRateSpecies," +
                " FExchangeRate,FBigExchange," +
                " FStartMoney,FStartDate,FETFCode,FUPPERLIMIT,FCheckState,FCreator,FCreateTime)" +
                " values(" +
                dbl.sqlString(this.rateType) + "," +
                dbl.sqlString(this.rateSpecies) + "," +
                this.exchangeRateS + "," +
                this.bigExchangeS + "," +
                this.startMoneyS + "," +
                dbl.sqlDate(this.startDateS) + "," +
                dbl.sqlString(this.ETFCode) + "," +
                this.upperLimit + "," +//add by yanghaiming 20100417 B股业务
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + ")";

            conn.setAutoCommit(false); //开始事务
            bTrans = true;
            dbl.executeSql(strSql); //执行SQL
            conn.commit(); //提交
            bTrans = false;
            conn.setAutoCommit(true); //结束事务

        } catch (Exception e) {
            throw new YssException("新增交易费率品种出错!", e);
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
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("TB_DAO_RateSpeciesType") +
                " set FRateType = " +
                dbl.sqlString(this.rateType) + ", FRateSpecies = " +
                dbl.sqlString(this.rateSpecies) + " , FExchangeRate = " +
                this.exchangeRateS + ", FBigExchange = " +
                this.bigExchangeS + ",FStartMoney = " +
                this.startMoneyS + ",FStartDate = " +
                dbl.sqlDate(this.startDateS) + ",FETFCode = " +
                dbl.sqlString(this.ETFCode) + ",FUPPERLIMIT = " +//add by yanghaiming 20100417 B股业务
                this.upperLimit + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + " where FRateType = " +
                dbl.sqlString(this.oldRateType) + " and FRateSpecies = " +
                dbl.sqlString(this.oldRrateSpecies) + " and FStartDate = " +
                dbl.sqlDate(this.oldstartDate);

            conn.setAutoCommit(false); //开始事务
            bTrans = true;
            dbl.executeSql(strSql); //执行SQL
            conn.commit(); //提交
            bTrans = false;
            conn.setAutoCommit(true); //结束事务
        } catch (Exception e) {
            throw new YssException("更新交易费率品种出错", e);
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
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update " + pub.yssGetTableName("TB_DAO_RateSpeciesType") + " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FRateType = " +
                dbl.sqlString(this.rateType) + " and FRateSpecies = " +
                dbl.sqlString(this.rateSpecies) + " and FStartDate = " +
                dbl.sqlDate(this.startDateS);
            conn.setAutoCommit(false); //开始事务
            bTrans = true;
            dbl.executeSql(strSql); //执行SQL
            conn.commit(); //提交
            bTrans = false;
            conn.setAutoCommit(true); //结束事务
        } catch (Exception e) {
            throw new YssException("删除交易费率品种出错", e);
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
        boolean bTrans = false; //代表是否开始了事务
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        Statement st = null;
        try {
            conn.setAutoCommit(false);
            st = conn.createStatement();
            bTrans = true;
            if (sRecycled != null && ! ("").equalsIgnoreCase(sRecycled)) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("TB_DAO_RateSpeciesType") + 
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FRateType = " + dbl.sqlString(this.rateType) +
                        " and FRateSpecies = " + dbl.sqlString(this.rateSpecies) +
                        " and FStartDate = " + dbl.sqlDate(this.startDateS);
                    st.addBatch(strSql);
                }
                st.executeBatch(); //多数据操作，使用批处理，调高效率 sunkey@Modify
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("审核交易费率品种出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
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
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        Statement st = null;
        try {
            st = conn.createStatement();
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
                    strSql = "delete from " + pub.yssGetTableName("TB_DAO_RateSpeciesType") + 
                        " where FRateType = " + dbl.sqlString(this.rateType) +
                        " and FRateSpecies = " + dbl.sqlString(this.rateSpecies) +
                        " and FStartDate = " + dbl.sqlDate(this.startDateS);

                    st.addBatch(strSql);
                }
                st.executeBatch(); //多数据操作，使用批处理，调高效率 sunkey@Modify
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
     * 查询数据
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        StringBuffer bufSql = new StringBuffer();
        bufSql.append(" select y.* ");
        bufSql.append(" from (select * from " + pub.yssGetTableName("TB_DAO_RateSpeciesType") + ") x ");
        bufSql.append(" join (select a.*, ");
        bufSql.append(" b.FUserName as FCreatorName, ");
        bufSql.append(" c.FUserName as FCheckUserName, ");
        bufSql.append(" f.FVocName as FVocName1, ");
        bufSql.append(" v.FVocName as FVocName2 ");
        bufSql.append(" from " + pub.yssGetTableName("TB_DAO_RateSpeciesType") + " a ");
        bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = ");
        bufSql.append(" b.FUserCode ");
        bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = ");
        bufSql.append(" c.FUserCode ");
        bufSql.append(" left join Tb_Fun_Vocabulary f on a.fratetype = f.FVocCode ");
        bufSql.append(" and f.FVocTypeCode = ").append(dbl.sqlString(YssCons.YSS_SPECISE_RATETYPE));
        bufSql.append(" left join Tb_Fun_Vocabulary v on a.fratespecies = v.FVocCode and v.FVocTypeCode = ");
        bufSql.append(dbl.sqlString(YssCons.YSS_SPECISE_SPECIESTYPE)).append(buildFilterSql());
        bufSql.append(") y on y.fratetype =  x.fratetype and y.fratespecies =  x.fratespecies and y.FStartDate  =  x.FStartDate ");
        bufSql.append(" order by x.FRateType, x.FRateSpecies, x.FStartDate");
        strSql = bufSql.toString();
        return builderListViewData(strSql);
    }

    /**
     * ListView数据
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_SPECISE_RATETYPE + "," +
                                        YssCons.YSS_SPECISE_SPECIESTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取文件头设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 得到结果集的值
     * @param rs ResultSet
     * @throws YssException
     */
    public void setAttr(ResultSet rs) throws YssException {
        try {
            this.rateType = rs.getString("fratetype");          //费率类型名称
            this.rateSpecies = rs.getString("fratespecies");    //费率类型名称
            this.exchangeRateS = rs.getString("FExchangeRate"); //交易费率
            this.bigExchangeS = rs.getString("FBigExchange");   //大宗交易费率
            this.startMoneyS = rs.getString("FStartMoney");     //起点金额
            this.startDateS = rs.getString("FStartDate");       //启用日期
            this.ETFCode = rs.getString("FETFCode");            //ETF代码
            this.upperLimit = rs.getString("FUPPERLIMIT");      //费用上限 add by yanghaiming 20100417 B股业务

            super.setRecLog(rs);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

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
            if (!this.filterType.rateType.equalsIgnoreCase("99")) {
                sResult = sResult + " and a.FRateType = '" +
                    filterType.rateType.replaceAll("'", "''") + "'";
            }
            if (!this.filterType.rateSpecies.equalsIgnoreCase("99")) {
                sResult = sResult + " and a.FRateSpecies = '" +
                    filterType.rateSpecies.replaceAll("'", "''") + "'";
            }
//----xuqiji 20100520 MS00939 交易费率品种设置界面的筛选按钮功能不可用 QDV4赢时胜(测试)2010年3月25日4_B ---//
//            if (filterType.exchangeRateS.length() > 0) {
//                sResult = sResult + " and a.FExchangeRate = " +
//                    filterType.exchangeRateS;
//            }
//            if (filterType.bigExchangeS.length() > 0) {
//                sResult = sResult + " and a.FBigExchange = " +
//                    filterType.bigExchangeS;
//            }
//            if (filterType.startMoneyS.length() > 0) {
//                sResult = sResult + " and a.FStartMoney = " +
//                    filterType.startMoneyS;
//            }
//            if(filterType.upperLimit.length() > 0) {
//            	sResult = sResult + " and a.FUPPERLIMIT = " +
//                filterType.upperLimit;
//            }//add by yanghaiming 20100417 B股业务
            sResult = sResult + " and a.FStartDate = " +
                dbl.sqlDate(filterType.startDateS);
            if(filterType.ETFCode.trim().length() > 0){
            	sResult = sResult + " and a.FETFCode like '%" +
                filterType.ETFCode.replaceAll("'", "''") + "%'";
            }
//--------------------------------------end----------------------------------------------//
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
     * 解析数据
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
            	//edit by songjie 2011.12.06 BUG 3132 QDV4赢时胜(测试)2011年11月11日12_B
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.rateType = reqAry[0];
            this.rateSpecies = reqAry[1];
            
            //add by songjie 2010.03.27 MS00935 QDV4赢时胜(测试)2010年3月23日4_B
            if(reqAry[2] != null && reqAry[2].indexOf(",") != -1){
            	reqAry[2] = reqAry[2].replaceAll(",", "");
            }
            if(reqAry[3] != null && reqAry[3].indexOf(",") != -1){
            	reqAry[3] = reqAry[3].replaceAll(",", "");
            }
            if(reqAry[4] != null && reqAry[4].indexOf(",") != -1){
            	reqAry[4] = reqAry[4].replaceAll(",", "");
            } 
            //add by songjie 2010.03.27 MS00935 QDV4赢时胜(测试)2010年3月23日4_B
            
            this.exchangeRateS = reqAry[2];
            this.bigExchangeS = reqAry[3];
            this.startMoneyS = reqAry[4];
            this.startDateS = reqAry[5];
            this.ETFCode = reqAry[6];
            this.checkStateId = YssFun.toInt(reqAry[7]);
            this.oldRateType = reqAry[8];
            this.oldRrateSpecies = reqAry[9];
            this.oldstartDate = reqAry[10];
            this.upperLimit = reqAry[11];//add by yanghaiming 20100417 B股业务
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RateSpeciesTypeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析分录摘要信息出错");
        }

    }

    /**
     * 组装对象
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.rateType).append("\t");
        buf.append(this.rateSpecies).append("\t");
        buf.append(this.exchangeRateS).append("\t");
        buf.append(this.bigExchangeS).append("\t");
        buf.append(this.startMoneyS).append("\t");
        buf.append(this.startDateS).append("\t");
        buf.append(this.ETFCode).append("\t");
        buf.append(this.upperLimit).append("\t");//add by yanghaiming 20100417 B股业务
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * 返回Map对象，里面封装RateSpeciesTypeBean（交易费率品种）对象，key为主键，value为对象.主键为空格隔开
     * @return Map
     * edit by songjie
     * 2010.03.22
     * MS00924
     * QDV4赢时胜（测试）2010年03月19日02_B
     * 添加获取交易费率方法的参数
     */
    public Map getRateSpeciesTypeBean(java.util.Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String errmsg = "";
        Map map = new HashMap();
        RateSpeciesTypeBean st = null;
        String key = "";
        try {
            errmsg = "读取交易费率品种出错！";
            strSql =
                "select * from " + pub.yssGetTableName("TB_DAO_RateSpeciesType") + 
                " a join (select max(fstartdate) maxstartdate,fratespecies from " + 
                pub.yssGetTableName("TB_DAO_RateSpeciesType") + 
                " where FCheckState = 1 and FStartDate <= " +
                dbl.sqlDate(dDate) + " group by FRateType,FRateSpecies) b " + 
                "on a.fstartdate = b.maxstartdate and a.fratespecies = b.fratespecies where a.FCheckState=1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                st = new RateSpeciesTypeBean();
                st.setRateType(rs.getString("FRateType"));          //费率品种
                st.setRateSpecies(rs.getString("FRateSpecies"));    //费率品种
                st.setExchangeRate(rs.getDouble("FExchangeRate"));  //交易费率
                st.setBigExchange(rs.getDouble("FBigExchange"));    //大宗交易费率
                st.setStartMoney(rs.getDouble("FStartMoney"));      //起点金额
                st.setFStartDate(rs.getDate("FStartDate"));         //启用日期
                st.setETFCode(rs.getString("FETFCode"));            //ETF代码
                st.setUpperLimitS(rs.getDouble("FUPPERLIMIT"));		//费用上限  add by yanghaiming 20100417 B股业务
                key = rs.getString("FRateType") + " " + rs.getString("FRateSpecies");
                map.put(key, st);
            }
        } catch (Exception e) {
            throw new YssException(errmsg, e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return map;

    }

    public double getStartMoney() {
        return startMoney;
    }

    public String getRateType() {
        return rateType;
    }

    public String getRateSpecies() {
        return rateSpecies;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setBigExchange(double bigExchange) {
        this.bigExchange = bigExchange;
    }

    public void setStartMoney(double startMoney) {
        this.startMoney = startMoney;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public void setRateSpecies(String rateSpecies) {
        this.rateSpecies = rateSpecies;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public double getBigExchange() {
        return bigExchange;
    }

    public void setFStartDate(Date FStartDate) {
        this.FStartDate = FStartDate;
    }

    public Date getFStartDate() {
        return FStartDate;
    }

    public void setETFCode(String ETFCode) {
        this.ETFCode = ETFCode;
    }

    public String getETFCode() {
        return ETFCode;
    }

    public String getOldRateType() {
        return oldRateType;
    }

    public String getOldRrateSpecies() {
        return oldRrateSpecies;
    }

    public String getOldstartDate() {
        return oldstartDate;
    }

    public void setOldRateType(String oldRateType) {
        this.oldRateType = oldRateType;
    }

    public void setOldRrateSpecies(String oldRrateSpecies) {
        this.oldRrateSpecies = oldRrateSpecies;
    }

    public void setOldstartDate(String oldstartDate) {
        this.oldstartDate = oldstartDate;
    }
    
    public String getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(String upperLimit) {
		this.upperLimit = upperLimit;
	}

	public double getUpperLimitS() {
		return upperLimitS;
	}

	public void setUpperLimitS(double upperLimitS) {
		this.upperLimitS = upperLimitS;
	}
}
