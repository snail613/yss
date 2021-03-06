package com.yss.main.operdata.futures;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.*;

/**
 *
 * <p>Title: 表 TB_XXX_Data_FurturesTradeRela 的实体操作类</p>
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
public class FuturesTradeRelaAdmin
    extends BaseDataSettingBean implements IDataSetting {
    //表 TB_XXX_Data_FurturesTradeRela 的实体
    private FuturesTradeRelaBean futTrdRelaBean;
    private FuturesTradeRelaBean filterType;

    public FuturesTradeRelaBean getFutTrdRelaBean() {
        return futTrdRelaBean;
    }

    public void setFutTrdRelaBean(FuturesTradeRelaBean futTrdRelaBean) {
        this.futTrdRelaBean = futTrdRelaBean;
    }

    public FuturesTradeRelaAdmin() {
    }

    public FuturesTradeRelaAdmin(FuturesTradeRelaBean futTrdRelaBean, FuturesTradeRelaBean filterType) {
        this.futTrdRelaBean = futTrdRelaBean;
        this.filterType = filterType;
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {
    }

    public void deleteData(java.util.Date dDate,String sPortCodes) throws YssException {//add by xxm,2010.01.26.MS00930,加上 “组合代码”，//以 SQL IN 形式存在的组合代码
        String strSql = "";
        try {
            strSql = "DELETE FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(dDate)+
                " and FPortCode in ("+ sPortCodes+")"+//add by xxm,2010.01.26.MS00930.多组合估值时，删除了交易关联表中所有数据
                " and ftsftypecode not in (" + operSql.sqlCodes(com.yss.util.YssOperCons.YSS_FU_09) + ")"; //add by fangjiang bug 6324 2012.11.16
                dbl.executeSql(strSql);
            //下面代码中判断是否区分券商来删除数据的逻辑无意义，并与上面DELETE语句逻辑冲突，暂注释。
            //panjunfang modify 20131022--start//
                //---add by songjie 2013.03.12 根据股指期货是否统计券商来删除无效数据 start---//
/*                CtlPubPara ctlpubpara = new CtlPubPara();
                ctlpubpara.setYssPub(pub);
                
                HashMap hmFU = null;
        		hmFU = ctlpubpara.getFUStBroker();
        		if(hmFU != null && hmFU.get(sPortCodes.replaceAll("'", "")) != null &&((String)hmFU.get(sPortCodes.replaceAll("'", ""))).equals("0")){
        			strSql = " delete from " + pub.yssGetTableName("TB_Data_FutTradeRela") +
        			" where FTransDate = " + dbl.sqlDate(dDate) + " and FPortCode in ("+ sPortCodes+")" +
        			" and ftsftypecode in (" + operSql.sqlCodes(com.yss.util.YssOperCons.YSS_FU_09) + ")"+
        			" and FBrokerCode <> ' ' ";
        			
        			dbl.executeSql(strSql);
        		}else{
        			strSql = " delete from " + pub.yssGetTableName("TB_Data_FutTradeRela") +
        			" where FTransDate = " + dbl.sqlDate(dDate) + " and FPortCode in ("+ sPortCodes+")" +
        			" and ftsftypecode in (" + operSql.sqlCodes(com.yss.util.YssOperCons.YSS_FU_09) + ")"+
        			" and FBrokerCode = ' ' ";
        			
        			dbl.executeSql(strSql);
        		}*/
        		//---add by songjie 2013.03.12 根据股指期货是否统计券商来删除无效数据 end---//
              //panjunfang modify 20131022--end//
        } catch (Exception e) {
            throw new YssException("删除期货交易关联数据出错\r\n" + e.getMessage());
        }
    }

    public void checkSetting() throws YssException {
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public String saveMutliSetting(ArrayList alEntityData, Connection conn) throws YssException {
        String strSql = "";
        PreparedStatement pst = null;
        try {
            //添加insert字段处理，避免数据字段混乱 modify by xuqiji MS00481:QDV4中金2009年06月03日01_A
            strSql = "INSERT INTO " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                "(FNum,FCloseNum,FTsfTypeCode,FTransDate,FMoney,FStorageAmount,FBaseCuryRate," +
                "FBaseCuryMoney,FPortCuryRate,FPortCuryMoney,FBailMoney,FSettleState,FCreator," +
                //edit by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 添加 FBrokerCode
                "FCreateTime,FCheckUser,FCheckTime,FPortCode,FBrokerCode)" +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //================================End MS00481========================================
            pst = conn.prepareStatement(strSql);

            for (int i = 0; i < alEntityData.size(); i++) {
                FuturesTradeRelaBean relaData = (FuturesTradeRelaBean) alEntityData.get(i);
                pst.setString(1, relaData.getNum());
                pst.setString(2, relaData.getCloseNum().trim().length() == 0 ? " " : relaData.getCloseNum());
                pst.setString(3, relaData.getTsfTypeCode());
                pst.setDate(4, YssFun.toSqlDate(relaData.getTransDate()));
                pst.setDouble(5, relaData.getMoney());
                pst.setDouble(6, relaData.getStorageAmount());
                pst.setDouble(7, relaData.getBaseCuryRate());
                pst.setDouble(8, relaData.getBaseCuryMoney());
                pst.setDouble(9, relaData.getPortCuryRate());
                pst.setDouble(10, relaData.getPortCuryMoney());
                pst.setDouble(11, relaData.getBailMoney());
                pst.setInt(12, relaData.getSettleState());
                pst.setString(13, pub.getUserCode());
                pst.setString(14, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(15, pub.getUserCode());
                pst.setString(16, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(17, relaData.getPortCode());
                //--- edit by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 添加 FBrokerCode start---//
                pst.setString(18, (relaData.getBrokerCode() == null || 
                		(relaData.getBrokerCode() != null && relaData.getBrokerCode().trim().length() == 0)) ? 
                				" " : relaData.getBrokerCode());
                //--- edit by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 添加 FBrokerCode end---//
                pst.executeUpdate();
            }
        } catch (Exception e) {
            throw new YssException("批量保存期货关联数据出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeStatementFinal(pst);
        }
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
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

    public String getListViewData1() throws YssException {
        return "";
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

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
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
}
