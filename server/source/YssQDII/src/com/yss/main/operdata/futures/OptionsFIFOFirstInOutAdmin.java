package com.yss.main.operdata.futures;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.futures.pojo.OptionsFIFOFirstInOutBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class OptionsFIFOFirstInOutAdmin extends BaseDataSettingBean implements IDataSetting{
	private OptionsFIFOFirstInOutBean fifoFirstInOut = null;
	public OptionsFIFOFirstInOutAdmin() {
		super();
	}
	/**
	 * 删除数据
	 * @param sPortCode
	 * @param dDate
	 * @throws YssException
	 */
	public void deleteData(String sPortCode,Date dDate)throws YssException{
		StringBuffer buff = null;
		try{
			buff = new StringBuffer();
			
			buff.append(" delete from ").append(pub.yssGetTableName("Tb_Data_OptiFIFOStock"));
			buff.append(" where FStorageDate = ").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(sPortCode)).append(")");
			
			dbl.executeSql(buff.toString());
			
		}catch (Exception e) {
			throw new YssException("删除数据出错！",e);
		}
	}
	/**
	 * 保存先入先出库存数据
	 * @param alFifoFirstInOut
	 * @throws YssException
	 */
	public void savingData(ArrayList alFifoFirstInOut) throws YssException{
		StringBuffer buff = null;
		PreparedStatement pst = null;
		try{
			buff = new StringBuffer();
			buff.append(" insert into ").append(pub.yssGetTableName("Tb_Data_OptiFIFOStock"));
			buff.append("(FNum,FStorageDate,FSecurityCode,FPortCode,FStorageAmount,FCuryCost,FPortCuryCost,");
			buff.append(" FBaseCuryRate,FPortCuryRate,FBaseCuryCost,FBailMoney,FCheckState,FCreator,");
			buff.append(" FCreateTime,FCheckUser,FCheckTime").append(")");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			for(int i =0; i < alFifoFirstInOut.size(); i++){
				fifoFirstInOut = (OptionsFIFOFirstInOutBean) alFifoFirstInOut.get(i);
				pst.setString(1,fifoFirstInOut.getSNum());
				pst.setDate(2,YssFun.toSqlDate(fifoFirstInOut.getSStorageDate()));
				pst.setString(3,fifoFirstInOut.getSSecurityCode());
				pst.setString(4,fifoFirstInOut.getSPortCode());
				pst.setDouble(5,fifoFirstInOut.getDStorageAmount());
				pst.setDouble(6,fifoFirstInOut.getDCuryCost());
				pst.setDouble(7,fifoFirstInOut.getDPortCuryCost());
				pst.setDouble(8,fifoFirstInOut.getDBaseCuryRate());
				pst.setDouble(9,fifoFirstInOut.getDPortCuryRate());
				pst.setDouble(10,fifoFirstInOut.getDBaseCuryCost());
				pst.setDouble(11,fifoFirstInOut.getDBailMoney());
				pst.setInt(12,1);
				pst.setString(13,pub.getUserCode());
				pst.setString(14,YssFun.formatDate(new Date()));
				pst.setString(15,pub.getUserCode());
				pst.setString(16,YssFun.formatDate(new Date()));
				
				pst.executeUpdate();
				
			}
			
		}catch (Exception e) {
			throw new YssException("保存先入先出库存数据出错！",e);
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
