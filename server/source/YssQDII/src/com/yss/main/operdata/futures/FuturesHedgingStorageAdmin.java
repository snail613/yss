package com.yss.main.operdata.futures;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.futures.pojo.FuturesHedgRelaBean;
import com.yss.main.operdata.futures.pojo.FuturesHedgingStorageBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 期货被套证券库存表操作类
 * @author xuqiji 20100512 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A
 *
 */
public class FuturesHedgingStorageAdmin extends BaseDataSettingBean implements IDataSetting{
	private FuturesHedgingStorageBean storageBean = null;
	public FuturesHedgingStorageAdmin() {
		super();
	}
	/**
	 * 删除期货被套证券库存表数据
	 * @param sPortCodes
	 * @param date
	 * @throws YssException
	 */
	public void deleteStorageData(String sPortCodes,Date date)throws YssException{
		StringBuffer buff = null;
		try{
			buff = new StringBuffer();
			buff.append(" delete from ").append(pub.yssGetTableName("TB_Stock_HedgSecurity"));
			buff.append(" where FStroageDate = ").append(dbl.sqlDate(date));
			buff.append(" and FPortCode in( ").append(this.operSql.sqlCodes(sPortCodes)).append(")");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
		}catch (Exception e) {
			throw new YssException("删除期货被套证券库存表数据出错！",e);
		}
	}
	/**
	 * 保存期货被套证券库存表数据
	 * @param alRealData
	 * @throws YssException
	 */
	public void savingRealData(ArrayList alStorageData,Date date) throws YssException{
		PreparedStatement pst = null;
		StringBuffer buff =null;
		try{
			buff = new StringBuffer();
			buff.append(" insert into ").append(pub.yssGetTableName("TB_Stock_HedgSecurity"));
			buff.append("(FNumOrSec,FStroageDate,FSecurityCode,FPortCode,FHedgingType,FStroageAmount,FCuryCost,FBaseCuryCost,FPortCuryCost,");
			buff.append(" FBaseCuryRate,FPortCuryRate,FCheckState,FCreator,");
			buff.append(" FCreateTime,FCheckUser,FCheckTime").append(")");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			for(int i =0; i < alStorageData.size(); i++){
				storageBean = (FuturesHedgingStorageBean) alStorageData.get(i);
				pst.setString(1,storageBean.getSNumOrSec());
				pst.setDate(2,YssFun.toSqlDate(date));
				pst.setString(3,storageBean.getSSecurityCode());
				pst.setString(4,storageBean.getSPortCode());
				pst.setString(5,storageBean.getSHedgingType());
				pst.setDouble(6,storageBean.getDStroageAmount());
				pst.setDouble(7,storageBean.getDCuryCost());
				pst.setDouble(8,storageBean.getDBaseCuryCost());
				pst.setDouble(9,storageBean.getDPortCuryCost());
				pst.setDouble(10,storageBean.getDBaseCuryRate());
				pst.setDouble(11,storageBean.getDPortCuryRate());
				pst.setInt(12,1);
				pst.setString(13,pub.getUserCode());
				pst.setString(14,YssFun.formatDatetime(new Date()));
				pst.setString(15,pub.getUserCode());
				pst.setString(16,YssFun.formatDatetime(new Date()));
				
				pst.executeUpdate();
				
			}
			
		}catch (Exception e) {
			throw new YssException("保存期货被套证券库存表数据出错！",e);
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
