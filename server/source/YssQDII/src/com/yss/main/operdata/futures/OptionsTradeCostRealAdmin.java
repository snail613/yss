package com.yss.main.operdata.futures;

import com.yss.main.dao.IDataSetting;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import com.yss.main.operdata.futures.pojo.OptionsTradeCostRealBean;
import java.sql.ResultSet;

/**
 * <p>Title:xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持</p>
 *
 * <p>Description: 操作期权成本以及估值增值表TB_XXX_DATA_OPTIONSCOST的实体类，此类中关联期权交易数据表的交易编号 </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class OptionsTradeCostRealAdmin
    extends BaseDataSettingBean implements IDataSetting {
    private OptionsTradeCostRealBean optionsTradeCost = new OptionsTradeCostRealBean();
    public OptionsTradeCostRealAdmin() {
    }

    /**
     * 清除数据
     * @param dDate Date
     * @throws YssException
     * modify by fangjiang 2011.09.13 story 1342
     */
    public void deleteData(String funm, String setnum, Date dDate) throws YssException {
        String strSql = "";
        try {
            strSql = "DELETE FROM " + pub.yssGetTableName("Tb_Data_OptionsCost") +
                " WHERE FNum = " + dbl.sqlString(funm) + " and FDate =" + dbl.sqlDate(dDate) +
                " and fsetnum = " + dbl.sqlString(setnum);
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("删除期权交易关联数据出错\r\n", e);
        }
    }
    
    public void deleteData(String sPortCode,Date dDate) throws YssException {
        String strSql = "";
        try {
            strSql = " DELETE FROM " + pub.yssGetTableName("Tb_Data_OptionsCost") +
            		 " where FDate = " + dbl.sqlDate(dDate) + " and FPortCode in (" + this.operSql.sqlCodes(sPortCode) + ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("删除期权交易关联数据出错\r\n", e);
        }
    }

    /**
     * 保存期权交易数据到关联表-期权成本以及估值增值表
     * @param alEntityData ArrayList
     * @param conn Connection
     * @return String
     */
    public String saveMutliSetting(OptionsTradeCostRealBean costRealData) throws YssException {
        Connection conn = null;
        boolean bTrans = true;
        StringBuffer buff = null;
        try {
            buff = new StringBuffer();
            buff.append("insert into ").append(pub.yssGetTableName("Tb_Data_OptionsCost")).append("(");
            buff.append("FNum,FSetNum,FCatType,FCuryCost,FPortCuryCost,FBaseCuryRate,FPortCuryRate,FBaseCuryCost,");
            buff.append("FOriginalAddValue,FBaseAddValue,FPortAddValue,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDate,FPortCode"); //add FPortCode by fangjiang 2011.09.14 story 1342
            buff.append(")values(").append(dbl.sqlString(costRealData.getSFNum())).append(",")
            	.append(dbl.sqlString(costRealData.getSSetNum().trim().length() == 0 ? " " : costRealData.getSSetNum())).append(",")
                .append(dbl.sqlString(costRealData.getSFCatType()));
            buff.append(",").append(costRealData.getFCuryCost()).append(",").append(costRealData.getFPortCuryCost())
                .append(",").append(costRealData.getFBaseCuryRate());
            buff.append(",").append(costRealData.getFPortCuryRate()).append(",")
                .append(costRealData.getFBaseCuryCost()).append(",").append(costRealData.getFOriginalAddValue());
            buff.append(",").append(costRealData.getFBaseAddValue()).append(",").append(costRealData.getFPortAddValue())
                .append(",").append(1);
            buff.append(",").append(dbl.sqlString(costRealData.getScreator())).append(",")
                .append(dbl.sqlString(costRealData.getSCreatorTime()));
            buff.append(",").append(dbl.sqlString(costRealData.getSCheckUser())).append(",")
                .append(dbl.sqlString(costRealData.getSCheckTime())).append(",").append(dbl.sqlDate(costRealData.getSDate()));
            //add by fangjiang 2011.09.14 story 1342
            buff.append(",").append(dbl.sqlString(costRealData.getPortCode())).append(")");
            //---------------
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            dbl.executeSql(buff.toString());
            buff.delete(0, buff.length());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("插入到期权成本以及估值增值表出错！\r\t", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
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

    public void checkSetting() throws YssException {
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
