package com.yss.main.basesetting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.ibm.db2.jcc.a.e;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssException;

public class ReconciliationSetBean 
		extends BaseDataSettingBean implements IDataSetting {

	private String strAccountType = "";//对账设置类型
	private String strAssetGroupCode = "";//组合群代码
	private String strPortCode = "";//组合代码
	private String strSelected = "";//是否选中
	private ArrayList arrayRecSetBean = new ArrayList();//存放设置信息
	
	public String getStrAccountType() {
		return strAccountType;
	}

	public void setStrAccountType(String strAccountType) {
		this.strAccountType = strAccountType;
	}

	public String getStrAssetGroupCode() {
		return strAssetGroupCode;
	}

	public void setStrAssetGroupCode(String strAssetGroupCode) {
		this.strAssetGroupCode = strAssetGroupCode;
	}

	public String getStrPortCode() {
		return strPortCode;
	}

	public void setStrPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}

	public String getStrSelected() {
		return strSelected;
	}

	public void setStrSelected(String strSelected) {
		this.strSelected = strSelected;
	}

	public ArrayList getArrayRecSetBean() {
		return arrayRecSetBean;
	}

	public void setArrayRecSetBean(ArrayList arrayRecSetBean) {
		this.arrayRecSetBean = arrayRecSetBean;
	}

	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub

	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub

	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub

	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub

	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        this.parseRowStr(sMutilRowStr);
        String strSql = "";
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        PreparedStatement ps = null;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        try {
			strSql = "delete from Tb_base_accountpara";//先删除表中原先数据
			conn.setAutoCommit(false);// 打开手动提交
            bTrans = true;
            dbl.executeSql(strSql);
            
            strSql = "insert into Tb_base_accountpara" +
            		" (accountType, FAssetGroupCode, FPORTCODE, selected)" +
            		" values (?, ?, ?, ?)";		//将设置数据批量插入表中
        	//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            ps = dbl.openPreparedStatement(strSql);
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	ArrayList tempArray = this.arrayRecSetBean;
            for (int i = 0; i < tempArray.size(); i++) {
            	ReconciliationSetBean tempBean = (ReconciliationSetBean)tempArray.get(i);//取出一条设置信息
            	ps.setString(1, tempBean.strAccountType);// 设置sql语句参数
            	ps.setString(2, tempBean.strAssetGroupCode);
            	ps.setString(3, tempBean.strPortCode);
            	ps.setString(4, tempBean.strSelected);
            	ps.addBatch();
			}
            ps.executeBatch();// 批量执行插入语句
            conn.commit();// 提交事务
            bTrans = false;
            conn.setAutoCommit(true);// 关闭手动提交，还原成自动提交
		} catch (Exception e) {
			throw new YssException("修改对账参数设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeStatementFinal(ps);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
		
		return "";
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
		ArrayList tmpList = new ArrayList();
		try {
			if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
            	reqAry = sRowStr.split("\r\t");
			/**shashijie 2012-7-2 STORY 2475 */
            } else {
				reqAry = new String[0];
			}
			/**end*/
            for (int i = 0; i < reqAry.length; i++) {
            	if(null != reqAry[i] && reqAry[i] != "") {
            		ReconciliationSetBean tempRecSetBean = new ReconciliationSetBean();
                	String[] tempArray = reqAry[i].split("\t");
                	tempRecSetBean.strAccountType = tempArray[0];
                	tempRecSetBean.strAssetGroupCode = tempArray[1];
                	tempRecSetBean.strPortCode = tempArray[2];
                	tempRecSetBean.strSelected = tempArray[3];
                	tmpList.add(tempRecSetBean);
            	}
			}
            this.arrayRecSetBean = tmpList;
		} catch (Exception e) {
			throw new YssException("解析对账参数设置请求出错", e);
		}

	}

	/***
	 * by yanghaiming 20101020 
	 * 增加查询的方法
	 */
	public String getListViewData1() throws YssException {
		String accountTypeStr = "";
		String aluationStr = "";
		String balanceStr = "";
		String resultStr = "";
		String sql = "";
		ResultSet rs = null;
		try{
			sql = "select * from Tb_base_accountpara where selected = 1";//查询出各对账参数设置了的组合
			rs = dbl.openResultSet(sql);
			while (rs.next()){
				if(rs.getString("ACCOUNTTYPE").equalsIgnoreCase("subjects")){
					accountTypeStr += rs.getString("FASSETGROUPCODE") + "\t" + rs.getString("FPORTCODE") + "\r";
				}else if (rs.getString("ACCOUNTTYPE").equalsIgnoreCase("Valuation")){
					aluationStr += rs.getString("FASSETGROUPCODE") + "\t" + rs.getString("FPORTCODE") + "\r";
				}else if (rs.getString("ACCOUNTTYPE").equalsIgnoreCase("balance")){
					balanceStr += rs.getString("FASSETGROUPCODE") + "\t" + rs.getString("FPORTCODE") + "\r";
				}
			}
			accountTypeStr += "\r\t";
			aluationStr += "\r\t";
			resultStr = accountTypeStr + aluationStr + balanceStr;
			return resultStr;
		}catch(Exception ex){
			throw new YssException(ex.getMessage(), ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

}
