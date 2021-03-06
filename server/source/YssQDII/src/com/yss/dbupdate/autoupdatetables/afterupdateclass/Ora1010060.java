package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010060 extends BaseDbUpdate  {
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			/**add---shashijie 2013-3-8 STORY 2869 创建接口视图*/
			//新增视图
			CreateView(hmInfo);
			/**end---shashijie 2013-3-8 STORY 2869*/
			/**yeshenghong 2013-4-2 BUG 7577  删除多余的数字 */
			DeleteRedundantVoc(hmInfo);
			/**end yeshenghong 2013-4-2 BUG 7577  删除多余的数字 */
			addField();//add by huangqirong 2013-05-13 story #2327 , Story #2328
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0060更新出错！", ex);
		}
	}
	
	/**yeshenghong 2013-4-2 BUG 7577  删除多余的数字 */
	private void DeleteRedundantVoc(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " delete from Tb_Fun_Vocabulary where FVocTypeCode = 'key' and FVocCode in ('2','3','4','5','6','7') ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("Tb_Fun_Vocabulary");
		} catch (Exception e) {
			throw new YssException("删除词汇表出错！");
		} 
	}
	
	/**shashijie 2013-3-8 STORY 2869 新增视图*/
	private void CreateView(HashMap hmInfo) throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		
		try{
			conn.setAutoCommit(false);
			bTrans = true;
			
			//组合视图
			CreateSubTradeView(hmInfo);
			//财务估值表视图
			CreateSecurity(hmInfo);
			
			conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
		}catch(Exception e){
			/*版本升级是按照每个组合群依次重低版本升级至高版本,考虑到第一个组合群已有表字段,但是第二个组合群还没有升级的情况
			 *所以这里不抛出异常*/
			//throw new YssException("新增视图出错！");
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**shashijie 2013-3-8 STORY 2869 财务估值表视图*/
	private void CreateSecurity(HashMap hmInfo) throws Exception {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		
		//若视图已存在则无需执行
		if (dbl.yssViewExist("VQ_Tb_Rep_Guessvalue")) {
			return;
		}
		
		StringBuffer strSql = new StringBuffer();
		//创建试图
		strSql.append(" Create Or Replace Noforce View VQ_Tb_Rep_Guessvalue AS ");
		
		//获取所有组合群代码
		ArrayList asset = getAsSet();
		for (int i = 0; i < asset.size(); i++) {
			String Fassetgroupcode = (String)asset.get(i);
			//循环拼接SQL
			setCreateStockSecurityViewSql(strSql,Fassetgroupcode.trim());
			
			if (i != asset.size() -1) {
				strSql.append(" Union All ");
			}
		}
		//With Check Option Constraint PK_Tb_Data_SubTrade  --插入或修改的数据行必须满足视图定义的约束
		strSql.append(" With Read Only ");//该视图上不能进行任何DML操作 
		dbl.executeSql(strSql.toString());
		
		sqlInfo.append(strSql);
		updTables.append("VQ_Tb_Rep_Guessvalue");
	}

	/**shashijie 2013-3-8 STORY 2869 */
	private void setCreateStockSecurityViewSql(StringBuffer strSql, String fassetgroupcode) {
		String assetgroupcode = pub.getAssetGroupCode();
		pub.setPrefixTB(fassetgroupcode);//设置系统组合群
		try {
			strSql.append(" Select c.Fassetgroupcode," +
				" a.Fportcode," +
				" a.Fdate," +
				" a.Facctcode," +
				" a.Fcurcode," +
				" a.Facctname," +
				" a.Facctattr," +
				" a.Facctclass," +
				" a.Fexchangerate," +
				" a.Famount," +
				" a.Fcost," +
				" a.Fstandardmoneycost," +
				" a.Fcosttonetratio," +
				" a.Fstandardmoneycosttonetratio," +
				" a.Fmarketprice," +
				" a.Fotprice1," +
				" a.Fotprice2," +
				" a.Fotprice3," +
				" a.Fmarketvalue," +
				" a.Fstandardmoneymarketvalue," +
				" a.Fmarketvaluetoratio," +
				" a.Fstandardmoneymarketvaluetorat," +
				" a.Fappreciation," +
				" a.Fstandardmoneyappreciation," +
				" a.Fmarketdescribe," +
				" a.Facctlevel," +
				" a.Facctdetail," +
				" a.Fdesc" +
				" From "+pub.yssGetTableName("Tb_Rep_Guessvalue")+" a" +
				" Join (Select Distinct Fsetid, Fsetcode" +
				" From Lsetlist" +
				" Where Fsetid In (Select Fassetcode From "+pub.yssGetTableName("Tb_Para_Portfolio")+
				" Where Fcheckstate = 1)) b On b.Fsetcode = a.Fportcode" +
				" Join (Select a.Fportcode, a.Fassetcode, a.Fassetgroupcode" +
				" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" a) c On b.Fsetid = c.Fassetcode"+
				" ");
		} catch (Exception e) {

		} finally {
			pub.setPrefixTB(assetgroupcode);
		}
	}

	/**shashijie 2013-3-8 STORY 2869 组合视图*/
	private void CreateSubTradeView(HashMap hmInfo) throws Exception {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		
		//若视图已存在则无需执行
		if (dbl.yssViewExist("VQ_Tb_Para_Portfolio")) {
			return;
		}
		
		StringBuffer strSql = new StringBuffer();
		//创建试图
		strSql.append(" Create Or Replace Noforce View VQ_Tb_Para_Portfolio AS ");
		
		//获取所有组合群代码
		ArrayList asset = getAsSet();
		for (int i = 0; i < asset.size(); i++) {
			String Fassetgroupcode = (String)asset.get(i);
			//循环拼接SQL
			setCreateSubTradeViewSql(strSql,Fassetgroupcode.trim());
			
			if (i != asset.size() -1) {
				strSql.append(" Union All ");
			}
		}
		//With Check Option Constraint PK_Tb_Data_SubTrade  --插入或修改的数据行必须满足视图定义的约束
		strSql.append(" With Read Only ");//该视图上不能进行任何DML操作 
		dbl.executeSql(strSql.toString());
		
		sqlInfo.append(strSql);
		updTables.append("VQ_Tb_Para_Portfolio");
	}

	/**shashijie 2013-3-8 STORY 2869 */
	private void setCreateSubTradeViewSql(StringBuffer strSql,
			String fassetgroupcode) {
		String assetgroupcode = pub.getAssetGroupCode();
		pub.setPrefixTB(fassetgroupcode);//设置系统组合群
		try {
			strSql.append("Select a.FASSETGROUPCODE," +
				" a.FSTARTDATE," +
				" a.FPORTNAME," +
				" a.FPORTSHORTNAME," +
				" a.FASSETCODE," +
				" a.FPORTCODE," +
				" a.FPORTCURY," +
				" a.FENABLED," +
				" a.FBASERATESRCCODE ," +
				" a.FPORTRATESRCCODE ," +
				" a.FBASERATECODE ," +
				" a.FPORTRATECODE ," +
				" a.FCOSTING ," +
				" a.FPORTTYPE ," +
				" a.FINCEPTIONDATE ," +
				" a.FINCEPTIONASSET ," +
				" a.FROLLASSET ," +
				" a.FASSETSOURCE ," +
				" a.FEXPIRATIONDATE ," +
				" a.FSTORAGEINITDATE ," +
				" a.FDESC  ," +
				" a.FCHECKSTATE  ," +
				" a.FCREATOR ," +
				" a.FCREATETIME ," +
				" a.FCHECKUSER ," +
				" a.FCHECKTIME ," +
				" a.FASSETTYPE ," +
				" a.FSUBASSETTYPE ," +
				" a.FCURYCODE ," +
				" a.FAIMETFCODE " +
				" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" a" +
				" ");
		} catch (Exception e) {

		} finally {
			pub.setPrefixTB(assetgroupcode);
		}
	}

	/**shashijie 2013-3-8 STORY 2869 获取所有组合群代码*/
	private ArrayList getAsSet() throws YssException {
		ResultSet rs = null;
		ArrayList list = new ArrayList();
		try {
			String strSql = " Select distinct FAssetGroupCode From Tb_sys_assetGroup Order By Fassetgroupcode ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				list.add(rs.getString("FAssetGroupCode"));
			}
		} catch (Exception e) {
			throw new YssException("获取所有组合群代码出错！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return list;
	}
	
	/**
	 * 增加字段
	 * add by huangqirong 2013-05-13 story #2327 , Story #2328
	 * */
	private void addField() {
		String sql = "select * from " + pub.yssGetTableName("tb_Data_SubTrade") + " where 1=2" ;
		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sql);
			if(!dbl.isFieldExist(rs, "FGCSNum")){
				//交易数据
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_SubTrade") + 
						" add FGCSNum varchar2(20) default ''");				
			}
			rs.close();
			//银行间债券表
			rs = dbl.openResultSet("select * from " + pub.yssGetTableName("Tb_Data_IntBakBond") + " where 1=2");
			
			if(!dbl.isFieldExist(rs, "FGCSNum")){
				dbl.executeSql("alter table " + pub.yssGetTableName("Tb_Data_IntBakBond") + 
						" add FGCSNum varchar2(20) default ''");				
			}
			rs.close();
			
			//债券转托管
			rs = dbl.openResultSet("select * from " + pub.yssGetTableName("tb_DATA_DEVTRUSTBOND") + " where 1=2");
			
			if(!dbl.isFieldExist(rs, "FGCSNum")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_DATA_DEVTRUSTBOND") + 
						" add FGCSNum varchar2(20) default ''");				
			}
			rs.close();
			
			//外汇交易
			rs = dbl.openResultSet("select * from " + pub.yssGetTableName("tb_data_ratetrade") + " where 1=2");
			
			if(!dbl.isFieldExist(rs, "FGCSNum")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_data_ratetrade") + 
						" add FGCSNum varchar2(20) default ''");				
			}
			rs.close();
			
			//银行间债券回购
			rs = dbl.openResultSet("select * from " + pub.yssGetTableName("tb_Data_Purchase") + " where 1=2");
			
			if(!dbl.isFieldExist(rs, "FGCSNum")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_Purchase") + 
						" add FGCSNum varchar2(20) default ''");				
			}
			rs.close();
			
			//开放式基金
			rs = dbl.openResultSet("select * from " + pub.yssGetTableName("tb_DATA_OPENFUNDTRADE") + " where 1=2");
			
			if(!dbl.isFieldExist(rs, "FGCSNum")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_DATA_OPENFUNDTRADE") + 
						" add FGCSNum varchar2(20) default ''");				
			}
			rs.close();
			
			//网下新股
			rs = dbl.openResultSet("select * from " + pub.yssGetTableName("tb_Data_Purchase") + " where 1=2");
			
			if(!dbl.isFieldExist(rs, "FGCSNum")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_Purchase") + 
						" add FGCSNum varchar2(20) default ''");				
			}
			
		} catch (Exception e) {
			System.out.println("版本 1.0.1.0060更新GCS相关字段出错：" + e);			
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
