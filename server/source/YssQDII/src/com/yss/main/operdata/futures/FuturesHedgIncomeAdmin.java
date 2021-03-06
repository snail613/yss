package com.yss.main.operdata.futures;

import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.futures.pojo.FuturesHedRecpayStorageBean;
import com.yss.main.operdata.futures.pojo.FuturesHedgIncomeBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 期货套期保值收益表操作类
 * @author xuqiji 20100512 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A
 *
 */
public class FuturesHedgIncomeAdmin extends BaseDataSettingBean implements IDataSetting{
	private FuturesHedgIncomeBean incomeBean =null;
	public FuturesHedgIncomeAdmin() {
		super();
	}
	/**
	 * 删除期货套期保值收益表数据
	 * @param sPortCodes
	 * @param date
	 * @throws YssException
	 */
	public void deleteRealData(String sPortCodes,Date date)throws YssException{
		StringBuffer buff = null;
		try{
			buff = new StringBuffer();
			buff.append(" delete from ").append(pub.yssGetTableName("Tb_Data_HedgIncome"));
			buff.append(" where FDate = ").append(dbl.sqlDate(date));
			buff.append(" and FPortCode in( ").append(this.operSql.sqlCodes(sPortCodes)).append(")");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
		}catch (Exception e) {
			throw new YssException("删除期货套期保值收益表数据出错！",e);
		}
	}
	/**
	 * 保存期货套期保值收益表
	 * @param alRealData
	 * @throws YssException
	 */
	public void savingRealData(HashMap mapIncomeStorageData,Date date) throws YssException{
		PreparedStatement pst = null;
		StringBuffer buff =null;
		Iterator it = null;
		try{
			buff = new StringBuffer();
			buff.append(" insert into ").append(pub.yssGetTableName("Tb_Data_HedgIncome"));
			buff.append("(FNumOrSec,FDate,FPortCode,FHedgingType,FStorageAmount,FBal,FBaseCuryBal,FPortCuryBal,");
			buff.append(" FBaseCuryRate,FPortCuryRate,FCheckState,FCreator,");
			buff.append(" FCreateTime,FCheckUser,FCheckTime,FSecurityCode").append(")");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			it = mapIncomeStorageData.values().iterator();
			while(it.hasNext()){
				incomeBean = (FuturesHedgIncomeBean) it.next();
				pst.setString(1,incomeBean.getSNumOrSec());
				pst.setDate(2,YssFun.toSqlDate(date));
				pst.setString(3,incomeBean.getSPortCode());
				pst.setString(4,incomeBean.getSHedgingType());
				pst.setDouble(5,incomeBean.getDStroageAmount());
				pst.setDouble(6,incomeBean.getDBal());
				pst.setDouble(7,incomeBean.getDBaseCuryBal());
				pst.setDouble(8,incomeBean.getDPortCuryBal());
				pst.setDouble(9,incomeBean.getDBaseCuryRate());
				pst.setDouble(10,incomeBean.getDPortCuryRate());
				pst.setInt(11,1);
				pst.setString(12,pub.getUserCode());
				pst.setString(13,YssFun.formatDatetime(new Date()));
				pst.setString(14,pub.getUserCode());
				pst.setString(15,YssFun.formatDatetime(new Date()));
				pst.setString(16,incomeBean.getSSecurityCode());
				
				pst.executeUpdate();
				
			}
			
		}catch (Exception e) {
			throw new YssException("保存期货套期保值收益表表数据出错！",e);
		}finally{
			dbl.closeStatementFinal(pst);
		}
	}
	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

}
