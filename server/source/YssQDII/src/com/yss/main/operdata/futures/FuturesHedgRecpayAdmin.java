package com.yss.main.operdata.futures;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.futures.pojo.FuturesHedgRecpayBean;
import com.yss.main.operdata.futures.pojo.FuturesHedgRelaBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 期货被套证券应收应付表操作类
 * @author xuqiji 20100512 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A
 *
 */
public class FuturesHedgRecpayAdmin extends BaseDataSettingBean implements IDataSetting{
	private FuturesHedgRecpayBean recpayBean = null;
	public FuturesHedgRecpayAdmin() {
		super();
	}
	/**
	 * 删除期货被套证券应收应付数据
	 * @param sPortCodes
	 * @param date
	 * @throws YssException
	 */
	public void deleteRealData(String sPortCodes,Date date)throws YssException{
		StringBuffer buff = null;
		try{
			buff = new StringBuffer();
			buff.append(" delete from ").append(pub.yssGetTableName("Tb_Data_HedgRecpay"));
			buff.append(" where FTradeDate = ").append(dbl.sqlDate(date));
			buff.append(" and FPortCode in( ").append(this.operSql.sqlCodes(sPortCodes)).append(")");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
		}catch (Exception e) {
			throw new YssException("删除期货被套证券应收应付数据出错！",e);
		}
	}
	/**
	 * 保存期货被套证券应收应付数据
	 * @param alRealData
	 * @throws YssException
	 */
	public void savingRealData(ArrayList alRecpayData) throws YssException{
		PreparedStatement pst = null;
		StringBuffer buff =null;
		try{
			buff = new StringBuffer();
			buff.append(" insert into ").append(pub.yssGetTableName("Tb_Data_HedgRecpay"));
			buff.append("(FNumOrSec,FTradeDate,FSecurityCode,FTsfTypeCode,FPortCode,FHedgingType,FMoney,FBaseCuryMoney,FPortCuryMoney,");
			buff.append(" FBaseCuryRate,FPortCuryRate,FInOut,FCheckState,FCreator,");
			buff.append(" FCreateTime,FCheckUser,FCheckTime").append(")");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			for(int i =0; i < alRecpayData.size(); i++){
				recpayBean = (FuturesHedgRecpayBean) alRecpayData.get(i);
				pst.setString(1,recpayBean.getSNumOrSec());
				pst.setDate(2,YssFun.toSqlDate(recpayBean.getSTradeDate()));
				pst.setString(3,recpayBean.getSSecurityCode());
				pst.setString(4,recpayBean.getSTsfTypeCode());
				pst.setString(5,recpayBean.getSPortCode());
				pst.setString(6,recpayBean.getSHedgingType());
				pst.setDouble(7,recpayBean.getDMoney());
				pst.setDouble(8,recpayBean.getDBaseCuryMoeny());
				pst.setDouble(9,recpayBean.getDPortCuryMoeny());
				pst.setDouble(10,recpayBean.getDBaseCuryRate());
				pst.setDouble(11,recpayBean.getDPortCuryRate());
				pst.setInt(12,recpayBean.getIInOut());
				pst.setInt(13,1);
				pst.setString(14,pub.getUserCode());
				pst.setString(15,YssFun.formatDatetime(new Date()));
				pst.setString(16,pub.getUserCode());
				pst.setString(17,YssFun.formatDatetime(new Date()));
				
				pst.executeUpdate();
				
			}
			
		}catch (Exception e) {
			throw new YssException("保存期货被套证券应收应付数据出错！",e);
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
