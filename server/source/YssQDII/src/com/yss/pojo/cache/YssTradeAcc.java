package com.yss.pojo.cache;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class YssTradeAcc
    extends BaseDataSettingBean implements IDataSetting {
    private String fNum = "";
    private String portCode = "";
    private String portName = "";
    private String accType = "";
    private String analysisCode1 = "";
    private String analysisName1 = "";
    private String analysisCode2 = "";
    private String analysisName2 = "";
    private String analysisCode3 = "";
    private String analysisName3 = "";
    private String cashAccCode = "";
    private String cashAccName = "";
    private String oldNum = "";
    private YssTradeAcc filterType;

    public String getAnalysisName1() {
        return analysisName1;
    }

    public String getFNum() {
        return fNum;
    }

    public String getAnalysisCode2() {
        return analysisCode2;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getAnalysisCode1() {
        return analysisCode1;
    }

    public String getAnalysisName3() {
        return analysisName3;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public String getAnalysisCode3() {
        return analysisCode3;
    }

    public String getCashAccName() {
        return cashAccName;
    }

    public String getAnalysisName2() {
        return analysisName2;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setAnalysisName1(String analysisName1) {
        this.analysisName1 = analysisName1;
    }

    public void setFNum(String fNum) {
        this.fNum = fNum;
    }

    public void setAnalysisCode2(String analysisCode2) {
        this.analysisCode2 = analysisCode2;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setAnalysisCode1(String analysisCode1) {
        this.analysisCode1 = analysisCode1;
    }

    public void setAnalysisName3(String analysisName3) {
        this.analysisName3 = analysisName3;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setAnalysisCode3(String analysisCode3) {
        this.analysisCode3 = analysisCode3;
    }

    public void setCashAccName(String cashAccName) {
        this.cashAccName = cashAccName;
    }

    public void setAnalysisName2(String analysisName2) {
        this.analysisName2 = analysisName2;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public void setFilterType(YssTradeAcc filterType) {
        this.filterType = filterType;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

    public String getPortName() {
        return portName;
    }

    public String getAccType() {
        return accType;
    }

    public YssTradeAcc getFilterType() {
        return filterType;
    }

    public String getOldNum() {
        return oldNum;
    }

    public YssTradeAcc() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            //this.fNum = reqAry[0];
            this.accType = reqAry[0];
            this.portCode = reqAry[1];
            this.cashAccCode = reqAry[2];
            this.analysisCode1 = reqAry[3];
            this.analysisCode2 = reqAry[4];
            this.analysisCode3 = reqAry[5];
            this.oldNum = reqAry[6];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new YssTradeAcc();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析远期外汇交易帐户出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        //buf.append(this.fNum);
        //buf.append("\t");
        buf.append(this.accType);
        buf.append("\t");
        buf.append(this.portCode);
        buf.append("\t");
        buf.append(this.portName);
        buf.append("\t");
        buf.append(this.cashAccCode);
        buf.append("\t");
        buf.append(this.cashAccName);
        buf.append("\t");
        buf.append(this.analysisCode1);
        buf.append("\t");
        buf.append(this.analysisName1);
        buf.append("\t");
        buf.append(this.analysisCode2);
        buf.append("\t");
        buf.append(this.analysisName2);
        buf.append("\t");
        buf.append(this.analysisCode3);
        buf.append("\t");
        buf.append(this.analysisName3);
        buf.append("\t");

        //buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_ForwardTradeAcc"),
                               "FNum,FAccType",
                               this.fNum + "," + this.accType,
                               this.oldNum + "," + this.accType);

    }

    public String getAllSetting() {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                if (this.filterType.oldNum.length() != 0 &&
                    this.filterType.fNum != null) {
                    sResult = sResult + " and a.FNum like '" +
                        filterType.oldNum.replaceAll("'", "''") + "%'";
                } else {
                    return " where 1=2 ";
                }
				/**shashijie 2012-7-2 STORY 2475 */
                if (this.filterType.accType != null &&
                		this.filterType.accType.length() != 0) {
				/**end*/
                    sResult = sResult + " and a.FAccType like '" +
                        filterType.accType.replaceAll("'", "''") + "%'";
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选远期外汇交易帐户出错", e);
        }
        return sResult;
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                setSecurityAttr(rs);
                bufShow.append(this.buildRowStr()).
                    append(YssCons.YSS_LINESPLITMARK);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取远期外汇交易帐户出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    // modify by fangjiang 2010.09.26 MS01773 QDV4赢时胜(测试)2010年09月19日02_B 
    public String getListViewData1() throws YssException {
        String strSql = "select a.*, b.FPortName,c.FCashAccName as FCashAccName ";
        try{
	        strSql = strSql +
	        ( FilterSql().length() == 0 ?
	         ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
	         ", FAnalysisName1, FAnalysisName2, FAnalysisName3 " ) +
	        " from " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") + " a " +
	        " left join (select FPortCode,FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") + ") b  on a.FPortCode = b.FPortCode " +
	        " left join (select FCashAccCode,FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") + ") c  on a.FCashAccCode = c.FCashAccCode " +
	        FilterSql() + buildFilterSql();
	        return this.builderListViewData(strSql);
        }catch(Exception e){
        	throw new YssException(e.getMessage(), e);
        }
    }
    //---------------------

    public String getListViewData4() throws YssException {
        String strSql = "";
        return strSql;
    }

    public String getListViewData2() throws YssException {
        String strSql = "";
        return strSql;
    }

    public String getListViewData3() throws YssException {
        String strSql = "";
        return "";
    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
                "(FNum, FAccType, FPortCode, FAnalysisCode1, FAnalysisCode2," +
                " FAnalysisCode3, FCashAccCode) values(" +
                dbl.sqlString(this.fNum) + "," +
                dbl.sqlString(this.accType) + "," +
                dbl.sqlString(this.portCode) + "," +
                ( (this.analysisCode1.length() > 0) ? dbl.sqlString(this.analysisCode1) : "") +
                ( (this.analysisCode2.length() > 0) ? dbl.sqlString(this.analysisCode2) : "") +
                ( (this.analysisCode3.length() > 0) ? dbl.sqlString(this.analysisCode3) : "") +
                dbl.sqlString(this.cashAccCode) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加远期外汇交易帐户出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            /* strSql = "update " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
                   " set " +
                   "  FNum = " + dbl.sqlString(this.fNum) +
                   ", FAccType = " + dbl.sqlString(this.accType) +
                   ", FPortCode = " + dbl.sqlString(this.portCode)+
                   ", FAnalysisCode1 = " + dbl.sqlString(this.analysisCode1) +
                   ", FAnalysisCode2 = " + dbl.sqlString(this.analysisCode2) +
                   ", FAnalysisCode3 = " + dbl.sqlString(this.analysisCode3) +
                   ", FCashAccCode = " + dbl.sqlString(this.cashAccCode) +
                   " where FNum = " +
                   dbl.sqlString(this.oldNum) + " and FAccType = " + dbl.sqlString(this.accType) ;*/
            strSql = "delete from " +
                pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
                " where FNum = " +
                dbl.sqlString(this.oldNum) + " and FAccType = " +
                dbl.sqlString(this.accType);
            addSetting();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改远期外汇交易帐户出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    public void delSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int Count = 0;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "delete from  " + pub.yssGetTableName("Tb_Para_Forward") +
                " where FNum = " +
                dbl.sqlString(this.oldNum) + " and FAccType = " + dbl.sqlString(this.accType);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除远期外汇交易帐户出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void checkSetting() throws YssException {

    }

    public void setSecurityAttr(ResultSet rs) throws SQLException, YssException {
        try {
        	// modify by fangjiang 2010.09.26 MS01773 QDV4赢时胜(测试)2010年09月19日02_B 
            /*boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            boolean analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");*/
        	//-------------------------

            this.fNum = rs.getString("FNum");
            this.accType = rs.getString("FAccType");
            this.portCode = rs.getString("FPortCode");
            this.portName = rs.getString("FPortName");
            this.cashAccCode = rs.getString("FCashAccCode");
            this.cashAccName = rs.getString("FCashAccName");
            // modify by fangjiang 2010.09.26 MS01773 QDV4赢时胜(测试)2010年09月19日02_B
            this.analysisCode1 = rs.getString("FAnalysisCode1");
            this.analysisName1 = rs.getString("FAnalysisName1");
            this.analysisCode2 = rs.getString("FAnalysisCode2");
            this.analysisName2 = rs.getString("FAnalysisName2");
            this.analysisCode3 = rs.getString("FAnalysisCode3");
            this.analysisName3 = rs.getString("FAnalysisName3");
            //----------------------
        
            // this.cashAccCode = rs.getString("FCashAccCode");
            // super.setRecLog(rs);
        } catch (Exception e) {
            throw new YssException("获取交易数据信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getOperValue(String sType) {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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
    
    // add by fangjiang 2010.09.26 MS01773 QDV4赢时胜(测试)2010年09月19日02_B 
    public String FilterSql() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_Cash);
        try {
        	rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                for (int i = 1; i <= 3; i++) {
                	if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                            rs.getString("FAnalysisCode" + String.valueOf(i)).
                            equalsIgnoreCase("001")) {
                		sResult = sResult +
                		// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    	          
                		   " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" + i +
  	                     "  from  " +
  	                     pub.yssGetTableName("tb_para_investmanager") +
  	                     " n where n.FCheckState = 1 ) invmgr" +
  	                     " on a.FAnalysisCode" + i + " = invmgr.FInvMgrCode ";
                		
                		//end by lidaolong
                	} else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                        rs.getString("FAnalysisCode" + String.valueOf(i)).
                        equalsIgnoreCase("002")) {
                        sResult = sResult +
                     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码

                        
                        " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" + i +
                        " from  " +
                        pub.yssGetTableName("tb_para_broker") + " y where  y.FCheckState = 1 ) broker " +
                        " on a.FAnalysisCode" + i + " = broker.FBrokerCode";
                        
                        //end by lidaolong
                    } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                               rs.getString("FAnalysisCode" + String.valueOf(i)).
                               equalsIgnoreCase("003")) {
                        sResult = sResult +
                            " left join (select FExchangeCode,FExchangeName as FAnalysisName" + i +
                            " from tb_base_exchange) e on a.FAnalysisCode" + i + " = e.FExchangeCode " ;
                    } else {
                        sResult = sResult +
                            " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                            i + " from  " + pub.yssGetTableName("Tb_Para_StorageCfg") +
                            " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                            i + ".FAnalysisNull ";
                    }
                }
            }
        } catch(Exception e) {
            throw new YssException("获取现金库存的分析配置出错：" + e.getMessage(), e);
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }
    //----------------------
}
