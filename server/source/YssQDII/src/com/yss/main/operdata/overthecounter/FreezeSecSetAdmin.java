package com.yss.main.operdata.overthecounter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.overthecounter.pojo.FreezeSecSetBean;
import com.yss.main.operdata.overthecounter.pojo.PurchaseTradeBean;
import com.yss.main.parasetting.InvestRelaSetBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class FreezeSecSetAdmin 
extends BaseDataSettingBean implements IDataSetting{

	private FreezeSecSetBean freezeSecSetBean = null;
	private String sFnum = "";
	private String sOldFnum = "";
	
	
	public String getsOldFnum() {
		return sOldFnum;
	}

	public void setsOldFnum(String sOldFnum) {
		this.sOldFnum = sOldFnum;
	}

	public String getsFnum() {
		return sFnum;
	}

	public void setsFnum(String sFnum) {
		this.sFnum = sFnum;
	}

	public FreezeSecSetBean getFreezeSecSetBean() {
		return freezeSecSetBean;
	}

	public void setFreezeSecSetBean(FreezeSecSetBean freezeSecSetBean) {
		this.freezeSecSetBean = freezeSecSetBean;
	}

	
	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (freezeSecSetBean == null) {
			freezeSecSetBean = new FreezeSecSetBean();
			freezeSecSetBean.setYssPub(pub);
        }
		freezeSecSetBean.parseRowStr(sRowStr);
        
		
	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return freezeSecSetBean.buildRowStr();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData1() throws YssException {
		
		StringBuffer sqlbuff=new StringBuffer();

		sqlbuff.append(" select * from  ").append(pub.yssGetTableName("tb_data_purchaserela")).append(" a ");
		sqlbuff.append(" left join ");
		sqlbuff.append(" (select fportcode,fportname from  ").append(pub.yssGetTableName("tb_para_portfolio")).append(" where fcheckstate=1)b ");
		sqlbuff.append(" on a.fportcode = b.fportcode ");
		sqlbuff.append(" left join ");
		sqlbuff.append(" (select fsecuritycode,fsecurityname from  ").append(pub.yssGetTableName("tb_para_security")).append(" where fcheckstate=1)c ");
		sqlbuff.append(" on a.fsecuritycode = c.fsecuritycode ");
		sqlbuff.append(buildFilterSql());
		return this.builderListViewData(sqlbuff.toString());
	}

	 public String builderListViewData(String strSql) throws YssException {
	        String sHeader = "";
	        String sShowDataStr = "";
	        String sAllDataStr = "";

	        StringBuffer bufShow = new StringBuffer();
	        StringBuffer bufAll = new StringBuffer();
	        ResultSet rs = null;
	        try {
	            sHeader = this.getListView1Headers();
	            rs = dbl.openResultSet(strSql);
	            while (rs.next()) {
	                bufShow.append(super.buildRowShowStr(rs,
	                    this.getListView1ShowCols())).
	                    append(YssCons.YSS_LINESPLITMARK);
	                this.freezeSecSetBean.setfreezeSecSetAttr(rs);
	                bufAll.append(this.buildRowStr()).append(YssCons.
	                    YSS_LINESPLITMARK);
	            }
	            if (bufShow.toString().length() > 2) {
	                sShowDataStr = bufShow.toString().substring(0,
	                    bufShow.toString().length() - 2);
	            }
	            if (bufAll.toString().length() > 2) {
	                sAllDataStr = bufAll.toString().substring(0,
	                    bufAll.toString().length() -
	                    2);
	            }
	            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
	                this.getListView1ShowCols();
	        } catch (Exception e) {
	            throw new YssException("获取冻结证券设置数据出错", e);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
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
		Connection conn =null;
		boolean bTrans =false;
		String sqlStr="";
		PreparedStatement pst=null;
		FreezeSecSetBean freezeSecSetBean = null;
		String[] arrData=null;
		try{
			arrData = sMutilRowStr.split("\r\f");
			conn =dbl.loadConnection();
			conn.setAutoCommit(bTrans);
			bTrans = true;
			//先删除数据
			sqlStr = "delete from "
					+ pub.yssGetTableName("tb_data_purchaserela")
					+ " where fnum=" + dbl.sqlString(this.sOldFnum);
			dbl.executeSql(sqlStr);
			//再插入数据
			sqlStr = "insert into "
					+ pub.yssGetTableName("tb_data_purchaserela")
					+ "(fnum ,FSECURITYCODE ,FPortCode,FDATE ,FFREEZEAMOUNT) "
					+ " values (?,?,?,?,?) ";
			pst =conn.prepareStatement(sqlStr);
			for(int i=0;i<arrData.length;i++){
				if(arrData[i].length()==0)
					continue;
				freezeSecSetBean = new FreezeSecSetBean();
				freezeSecSetBean.parseRowStr(arrData[i]);

				pst.setString(1,this.sFnum);
				pst.setString(2,freezeSecSetBean.getSecurityCode());
				pst.setString(3,freezeSecSetBean.getPortCode());
				pst.setDate(4,YssFun.toSqlDate(freezeSecSetBean.getBargainDate()));
				pst.setDouble(5,freezeSecSetBean.getFreezeAmount());
				pst.executeUpdate();
			}
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException("新增冻结证券设置出错",ex);
		}finally{
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
		}
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
		String sqlStr="";
		Connection conn =null;
		boolean bTrans = false;
		try{
			conn =dbl.loadConnection();
			conn.setAutoCommit(bTrans);
			bTrans =true;
			sqlStr = "delete from "
				+ pub.yssGetTableName("tb_data_purchaserela")
				+ " where fnum=" + dbl.sqlString(sFnum);
			dbl.executeSql(sqlStr);
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
		
	}

	private String buildFilterSql() throws YssException{
		String sResult = "";
		FreezeSecSetBean filterType = this.freezeSecSetBean.getFilterType();
        if (filterType != null) {
            sResult = " where 1=1";
            
            if (filterType.getNum().trim().length() != 0) {
                sResult = sResult + " and a.fnum =" +dbl.sqlString(filterType.getNum());
            }
        }
        return sResult;
	}
	
	
	
	
}
