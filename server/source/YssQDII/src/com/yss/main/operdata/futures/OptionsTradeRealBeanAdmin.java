package com.yss.main.operdata.futures;

import com.yss.main.dao.IDataSetting;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import java.sql.PreparedStatement;
import com.yss.util.YssFun;
import java.sql.Connection;
import java.util.ArrayList;
import com.yss.main.operdata.futures.pojo.OptionsTradeRealBean;
import java.sql.*;  //------ add by wangzuochun 2010.09.26 MS01776    认购指数期权卖出开仓，取消交易，估值后仍有库存    QDV4赢时胜（深圳）2010年9月21日01_B    
/**
 * <p>Title: xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持</p>
 *
 * <p>Description: 期权关联表TB_XXX_DATA_OPTIONSTRADERELA的实体操作类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OptionsTradeRealBeanAdmin  extends BaseDataSettingBean
      implements IDataSetting {
    private OptionsTradeRealBean realBean;
    public OptionsTradeRealBeanAdmin() {
    }

    public OptionsTradeRealBeanAdmin(OptionsTradeRealBean realBean) {
        this.realBean = realBean;
    }

    public void checkInput(byte btOper) throws YssException {
    }

    /**
     * 插入数据
     * @param alEntityData ArrayList
     * @param conn Connection
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(ArrayList alEntityData) throws YssException {
        PreparedStatement pst = null;
        Connection conn = null;
        StringBuffer buff = null;
        try {
            buff = new StringBuffer();
            buff.append(" insert into ").append(pub.yssGetTableName("Tb_Data_Optionstraderela"));
            buff.append("(FNum,FCloseNum,FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FBegBailAcctCode,");
            buff.append("FChageBailAcctCode,FBargainDate,FSettleDate,FTradeAmount,FTradePrice,FBaseCuryRate,FPortCuryRate,FTradeMoney,");
            buff.append("FBegBailMoney,FSettleMoney,FInvestTactics,FFeeCode1,FTradeFee1,FFeeCode2,FTradeFee2,FFeeCode3,");
            buff.append("FTradeFee3,FFeeCode4,FTradeFee4,FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6,FFeeCode7,FTradeFee7,FFeeCode8,");
            buff.append("FTradeFee8,FDesc,FSettleType,FSettleState,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime, FDROPRIGHTDATASOURCE)"); //add FDROPRIGHTDATASOURCE by fangjiang 2011.09.15 story 1342
            buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
            buff.append("?,?,?,?,?,?,?,?,?,?,?,?)");

            pst = dbl.openPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            for (int i = 0; i < alEntityData.size(); i++) {
                OptionsTradeRealBean relaData = (OptionsTradeRealBean) alEntityData.get(i);
                pst.setString(1, relaData.getNum());
                pst.setString(2, relaData.getCloseNum());
                pst.setString(3, relaData.getSecurityCode());
                pst.setString(4, relaData.getPortCode());
                pst.setString(5, relaData.getBrokerCode());
                pst.setString(6, relaData.getInvMgrCode());
                pst.setString(7, relaData.getTradeTypeCode());
                pst.setString(8, relaData.getBegBailAcctCode());
                pst.setString(9, relaData.getChageBailAcctCode());
                pst.setDate(10, YssFun.toSqlDate(relaData.getBargainDate()));
                pst.setDate(11, YssFun.toSqlDate(relaData.getSettleDate()));
                pst.setDouble(12, relaData.getTradeAmount());
                pst.setDouble(13, relaData.getTradePrice());
                pst.setDouble(14, relaData.getBaseCuryRate());
                pst.setDouble(15, relaData.getPortCuryRate());
                pst.setDouble(16, relaData.getTradeMoney());
                pst.setDouble(17, relaData.getBegBailMoney());
                pst.setDouble(18, relaData.getSettleMoney());
                pst.setString(19, relaData.getInvestTastic());

               pst.setString(20, relaData.getFeeCode1());
               pst.setDouble(21, relaData.getTradeFee1());
               pst.setString(22, relaData.getFeeCode2());
               pst.setDouble(23, relaData.getTradeFee2());
               pst.setString(24, relaData.getFeeCode3());
               pst.setDouble(25, relaData.getTradeFee3());
               pst.setString(26, relaData.getFeeCode4());
               pst.setDouble(27, relaData.getTradeFee4());
               pst.setString(28, relaData.getFeeCode5());
               pst.setDouble(29, relaData.getTradeFee5());
               pst.setString(30, relaData.getFeeCode6());
               pst.setDouble(31, relaData.getTradeFee6());
               pst.setString(32, relaData.getFeeCode7());
               pst.setDouble(33, relaData.getTradeFee7());
               pst.setString(34, relaData.getFeeCode8());
               pst.setDouble(35, relaData.getTradeFee8());
               pst.setString(36, relaData.getDesc());
               pst.setInt(37, relaData.getSettleType());
               pst.setInt(38, relaData.getSettleState());
               pst.setInt(39, 1);

               pst.setString(40, pub.getUserCode());
               pst.setString(41, YssFun.formatDatetime(new java.util.Date()));
               pst.setString(42, pub.getUserCode());
               pst.setString(43, YssFun.formatDatetime(new java.util.Date()));
               
               pst.setString(44, relaData.getDropRightDataSource()); //add by fangjiang 2011.09.15 story 1342
               pst.addBatch();
           }
           pst.executeBatch();
       }
       catch(Exception e){
          throw new YssException("批量保存期权关联数据出错！\r\n" ,e);
       }
       finally{
          dbl.closeStatementFinal(pst);
       }
       return "";
    }

    public String addSetting() throws YssException {
        return "";
    }

    /**
     * 删除数据
     * @param dDate Date
     * @throws YssException
     */
    public void deleteData(java.util.Date dDate,String sPortCode) throws YssException {
        String strSql = "";
        Connection conn = null;
        //------ add by wangzuochun 2010.09.26 MS01776    认购指数期权卖出开仓，取消交易，估值后仍有库存    QDV4赢时胜（深圳）2010年9月21日01_B    
        ResultSet rs = null;
        ResultSet rsTrans = null;
        String strNums = "";
        String strTransNum = "";
        //--------MS01776-------//
        boolean bTrans = true;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            //------ modify by wangzuochun 2010.09.26 MS01776    认购指数期权卖出开仓，取消交易，估值后仍有库存    QDV4赢时胜（深圳）2010年9月21日01_B    
            strSql = " select FNum from " + pub.yssGetTableName("TB_Data_Optionstraderela") +
            		 " WHERE FBargainDate = " + dbl.sqlDate(dDate)+" and FPortCode = "+dbl.sqlString(sPortCode); //add by jiangshichao 2011.07.14 添加组合代码，不然多组合时会造成数据丢失
            
            rs = dbl.openResultSet(strSql);
            
            while (rs.next()){
            	strNums += rs.getString("FNum") + ",";
            }
            
            dbl.closeResultSetFinal(rs);
            
            strSql = " DELETE FROM " + pub.yssGetTableName("TB_Data_Optionstraderela") +
            		 " WHERE FBargainDate = " + dbl.sqlDate(dDate)+" and FPortCode = "+dbl.sqlString(sPortCode);
            dbl.executeSql(strSql);
            
            if (strNums.length() > 1) {
            	strNums = strNums.substring(0, strNums.length() - 1);
            	strNums = operSql.sqlCodes(strNums);
				
				strSql = " Select * from "
						+ pub.yssGetTableName("Tb_cash_transfer")
						+ " Where FRelaNum in (" + strNums + ") "
						+ " and FTransferDate = "
						+ dbl.sqlDate(dDate);
						
				rsTrans = dbl.openResultSet(strSql);
				
				// 把要删除的资金调拨编号拼接起来
				while (rsTrans.next()) {
					strTransNum += rsTrans.getString("FNum") + ",";
				}
				dbl.closeResultSetFinal(rsTrans);
			}
            
            if (strTransNum.length() > 1) {
				strTransNum = strTransNum.substring(0, strTransNum.length() - 1);
				strTransNum = operSql.sqlCodes(strTransNum);
				if (strTransNum.trim().length() > 0) {
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_Transfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);

					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_SubTransfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);
				}
			}
            //-----------------------------MS01776---------------------------//
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除期权交易关联数据出错\r\n", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs); //------ add by wangzuochun 2010.09.26 MS01776    认购指数期权卖出开仓，取消交易，估值后仍有库存    QDV4赢时胜（深圳）2010年9月21日01_B    
            dbl.closeResultSetFinal(rsTrans); //------ add by wangzuochun 2010.09.26 MS01776    认购指数期权卖出开仓，取消交易，估值后仍有库存    QDV4赢时胜（深圳）2010年9月21日01_B    
        }
    }
    
    /**
     * 删除数据
     * @param dDate Date
     * @throws YssException
     */
    public void deleteData(java.util.Date dDate,String sPortCode,String sTradeType,String sCloseNum, String dropRightDataSource) throws YssException {
        String strSql = "";
        Connection conn = null;
        boolean bTrans = true;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            strSql = "DELETE FROM " + pub.yssGetTableName("TB_Data_Optionstraderela") +
                " WHERE FBargainDate = " + dbl.sqlDate(dDate) + " and FCloseNum = " + dbl.sqlString(sCloseNum)
                +" and FTradeTypeCode =" + dbl.sqlString(sTradeType)+" and FPortCode = "+dbl.sqlString(sPortCode)
                + " and FDROPRIGHTDATASOURCE = "+dbl.sqlString(dropRightDataSource); //modify by fangjiang 2011.09.15 story 1342
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除期权交易关联数据出错\r\n", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
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

    public void parseRowStr(String sRowStr) throws YssException {
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }
}
