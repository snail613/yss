package com.yss.main.operdata.futures;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.futures.pojo.FuturesHedgRelaBean;
import com.yss.main.operdata.futures.pojo.OptionsFIFOFirstInOutBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 期货套期保值交易关联表操作类
 * @author xuqiji 20100510 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A
 *
 */
public class FuturesHedgRelaAdmin extends BaseDataSettingBean implements IDataSetting{
	private FuturesHedgRelaBean hedgRealBean;
	public FuturesHedgRelaAdmin() {
		super();
	}
	/**
	 * 删除套期保值交易关联表数据
	 * @param sPortCodes
	 * @param date
	 * @throws YssException
	 */
	public void deleteRealData(String sPortCodes,Date date)throws YssException{
		StringBuffer buff = null;
		try{
			buff = new StringBuffer();
			buff.append(" delete from ").append(pub.yssGetTableName("Tb_Data_HedgRela"));
			buff.append(" where FTradeDate = ").append(dbl.sqlDate(date));
			buff.append(" and FPortCode in( ").append(this.operSql.sqlCodes(sPortCodes)).append(")");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
		}catch (Exception e) {
			throw new YssException("删除套期保值交易关联表数据出错！",e);
		}
	}
	/**
	 * 保存套期保值交易关联表数据
	 * @param alRealData
	 * @throws YssException
	 */
	public void savingRealData(ArrayList alRealData) throws YssException{
		PreparedStatement pst = null;
		StringBuffer buff =null;
		try{
			buff = new StringBuffer();
			buff.append(" insert into ").append(pub.yssGetTableName("Tb_Data_HedgRela"));
			buff.append("(FNum,FSetNum,FTradeDate,FSecurityCode,FTsfTypeCode,FPortCode,FMoney,FBaseCuryMoney,FPortCuryMoney,");
			buff.append(" FBaseCuryRate,FPortCuryRate,FCheckState,FCreator,");
			buff.append(" FCreateTime,FCheckUser,FCheckTime,FAmount").append(")");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			for(int i =0; i < alRealData.size(); i++){
				hedgRealBean = (FuturesHedgRelaBean) alRealData.get(i);
				pst.setString(1,hedgRealBean.getSNum());
				pst.setString(2,hedgRealBean.getSSetNum().trim().length() > 0 ? hedgRealBean.getSSetNum() :" ");
				pst.setDate(3,YssFun.toSqlDate(hedgRealBean.getSTradeDate()));
				pst.setString(4,hedgRealBean.getSSecurityCode());
				pst.setString(5,hedgRealBean.getSTsfTypeCode());
				pst.setString(6,hedgRealBean.getSPortCode());
				pst.setDouble(7,hedgRealBean.getDMoney());
				pst.setDouble(8,hedgRealBean.getDBaseCuryMoeny());
				pst.setDouble(9,hedgRealBean.getDPortCuryMoeny());
				pst.setDouble(10,hedgRealBean.getDBaseCuryRate());
				pst.setDouble(11,hedgRealBean.getDPortCuryRate());
				pst.setInt(12,1);
				pst.setString(13,pub.getUserCode());
				pst.setString(14,YssFun.formatDatetime(new Date()));
				pst.setString(15,pub.getUserCode());
				pst.setString(16,YssFun.formatDatetime(new Date()));
				pst.setDouble(17,hedgRealBean.getDTradeAmount());
				
				pst.executeUpdate();
				
			}
			
		}catch (Exception e) {
			throw new YssException("保存套期保值交易关联表数据出错！",e);
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
