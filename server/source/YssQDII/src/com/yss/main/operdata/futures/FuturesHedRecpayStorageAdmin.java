package com.yss.main.operdata.futures;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.futures.pojo.FuturesHedRecpayStorageBean;
import com.yss.main.operdata.futures.pojo.FuturesHedgRecpayBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 期货被套证券应收应付库存表操作类
 * @author xuqiji 20100512 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A
 *
 */
public class FuturesHedRecpayStorageAdmin extends BaseDataSettingBean implements IDataSetting{
	private FuturesHedRecpayStorageBean recpayStorageBean = null;
	public FuturesHedRecpayStorageAdmin() {
		super();
	}
	/**
	 * 删除期货被套证券应收应付库存表数据
	 * @param sPortCodes
	 * @param date
	 * @throws YssException
	 */
	public void deleteRealData(String sPortCodes,Date date)throws YssException{
		StringBuffer buff = null;
		try{
			buff = new StringBuffer();
			buff.append(" delete from ").append(pub.yssGetTableName("Tb_Stock_HedgRecpay"));//证券应收应付库存表
			buff.append(" where FStorageDate = ").append(dbl.sqlDate(date));
			buff.append(" and FPortCode in( ").append(this.operSql.sqlCodes(sPortCodes)).append(")");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
		}catch (Exception e) {
			throw new YssException("删除期货被套证券应收应付库存表数据出错！",e);
		}
	}
	/**
	 * 保存期货被套证券应收应付库存表
	 * @param alRealData
	 * @throws YssException
	 */
	public void savingRealData(HashMap mapRecpayStorageData,Date date) throws YssException{
		PreparedStatement pst = null;
		StringBuffer buff =null;
		Iterator it = null;
		try{
			buff = new StringBuffer();
			buff.append(" insert into ").append(pub.yssGetTableName("Tb_Stock_HedgRecpay"));//证券应收应付库存表
			buff.append("(FNumOrSec,FStorageDate,FSecurityCode,FTsfTypeCode,FPortCode,FHedgingType,FBal,FBaseCuryBal,FPortCuryBal,");
			buff.append(" FBaseCuryRate,FPortCuryRate,FCheckState,FCreator,");
			buff.append(" FCreateTime,FCheckUser,FCheckTime").append(")");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			it = mapRecpayStorageData.values().iterator();
			while(it.hasNext()){
				recpayStorageBean = (FuturesHedRecpayStorageBean) it.next();
				pst.setString(1,recpayStorageBean.getSNumOrSec());
				pst.setDate(2,YssFun.toSqlDate(date));
				pst.setString(3,recpayStorageBean.getSSecurityCode());//证券代码
				pst.setString(4,recpayStorageBean.getSTsfTypeCode());//调拨类型
				pst.setString(5,recpayStorageBean.getSPortCode());//组合代码
				pst.setString(6,recpayStorageBean.getSHedgingType());//套期类型
				pst.setDouble(7,recpayStorageBean.getDBal());//原币金额
				pst.setDouble(8,recpayStorageBean.getDBaseCuryBal());
				pst.setDouble(9,recpayStorageBean.getDPortCuryBal());
				pst.setDouble(10,recpayStorageBean.getDBaseCuryRate());
				pst.setDouble(11,recpayStorageBean.getDPortCuryRate());
				pst.setInt(12,1);
				pst.setString(13,pub.getUserCode());
				pst.setString(14,YssFun.formatDatetime(new Date()));
				pst.setString(15,pub.getUserCode());
				pst.setString(16,YssFun.formatDatetime(new Date()));
				
				pst.executeUpdate();
				
			}
			
		}catch (Exception e) {
			throw new YssException("保存期期货被套证券应收应付库存表数据出错！",e);
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
