package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010059sp2 extends BaseDbUpdate  {
	public static final String lockTable = "0";
	
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			/**add---shashijie 2013-2-27 STORY 3366 创建接口公共表和试图*/
			//新增接口公共表
			CreatePublicTable(hmInfo);
			//新增视图
			CreateView(hmInfo);
			/**end---shashijie 2013-2-27 STORY 3366*/
			
			this.addFields();	// add by huangqirong 2013-03-25 story #3488 
			
			//add by yeshenghong story3715 20130320
			if(!dbl.yssTableExist("TB_NH_PAYFEEDATA"))
			{
				CreateNHPayFeeDataTable(hmInfo);
			}
			
			//--- add by songjie 2013.03.07 完善股指期货业务 start---//
			synchronized(this.lockTable){
				createTMPTable(hmInfo);
			}
			//--- add by songjie 2013.03.07 完善股指期货业务 end---//
			
			UpdateLogType();		//20130322 added by liubo.Story #2839
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0059sp2更新出错！", ex);
		}
	}
	
	/**
	 * add by huangqirong 2013-03-25 story #3488
	 * */
	private void addFields(){
		ResultSet rs = null;
		try {
			rs = dbl.openResultSet("select * from " + pub.yssGetTableName("Tb_Para_InvestPay"));
			if(!dbl.isFieldExist(rs, "FLowerCurrencyCode")){
				dbl.executeSql("alter table " + pub.yssGetTableName("Tb_Para_InvestPay") + " add FLowerCurrencyCode VARCHAR2(20) default ' '");
			}
			
			if(!dbl.isFieldExist(rs, "FSupplementDates")){
				dbl.executeSql("alter table " + pub.yssGetTableName("Tb_Para_InvestPay") + " add FSupplementDates varchar2(1000)");
			}
			
		} catch (Exception e) {
			System.out.println("在投资运营收支设置增加字段出错：" + e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**shashijie 2013-2-27 STORY 3366 新增视图*/
	private void CreateView(HashMap hmInfo) throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		
		try{
			conn.setAutoCommit(false);
			bTrans = true;
			
			//交易数据子表视图
			CreateSubTradeView(hmInfo);
			//证券库存视图
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

	/**shashijie 2013-2-27 STORY 3366 证券库存视图*/
	private void CreateSecurity(HashMap hmInfo) throws Exception {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		
		//若视图已存在则无需执行
		if (dbl.yssViewExist("VQ_Tb_Stock_Security")) {
			return;
		}
		
		StringBuffer strSql = new StringBuffer();
		//创建试图
		strSql.append(" Create Or Replace Noforce View VQ_Tb_Stock_Security AS ");
		
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
		updTables.append("VQ_Tb_Stock_Security");
	}

	/**shashijie 2013-2-27 STORY 3366 */
	private void setCreateStockSecurityViewSql(StringBuffer strSql, String fassetgroupcode) {
		String assetgroupcode = pub.getAssetGroupCode();
		pub.setPrefixTB(fassetgroupcode);//设置系统组合群
		try {
			strSql.append("Select b.Fassetgroupcode, a.FSECURITYCODE ," +
					" a.FYEARMONTH ," +
					" a.FSTORAGEDATE," +
					" a.FPORTCODE ," +
					" a.FANALYSISCODE1 ," +
					" a.FANALYSISCODE2 ," +
					" a.FANALYSISCODE3 ," +
					" a.FATTRCLSCODE ," +
					" a.FCATTYPE ," +
					" a.FCURYCODE  ," +
					" a.FSTORAGEAMOUNT ," +
					" a.FSTORAGECOST ," +
					" a.FMSTORAGECOST ," +
					" a.FVSTORAGECOST ," +
					" a.FFREEZEAMOUNT ," +
					" a.FBAILMONEY ," +
					" a.FBASECURYRATE ," +
					" a.FBASECURYCOST ," +
					" a.FMBASECURYCOST ," +
					" a.FVBASECURYCOST ," +
					" a.FPORTCURYRATE ," +
					" a.FPORTCURYCOST ," +
					" a.FMPORTCURYCOST ," +
					" a.FVPORTCURYCOST ," +
					" a.FMARKETPRICE ," +
					" a.FCHECKSTATE ," +
					" a.FSTORAGEIND ," +
					" a.FCREATOR ," +
					" a.FCREATETIME ," +
					" a.FCHECKUSER ," +
					" a.FCHECKTIME ," +
					" a.FINVESTTYPE ," +
					" a.FEFFECTIVERATE  " +
				" From "+pub.yssGetTableName("Tb_Stock_Security")+" a" +
				" Join (Select B1.Fportcode, B1.Fassetgroupcode" +
				" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" B1) b On a.Fportcode = b.Fportcode " +
				" ");
		} catch (Exception e) {

		} finally {
			pub.setPrefixTB(assetgroupcode);
		}
	}

	/**shashijie 2013-2-27 STORY 3366 创建交易数据子表视图*/
	private void CreateSubTradeView(HashMap hmInfo) throws Exception {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		
		//若视图已存在则无需执行
		if (dbl.yssViewExist("VQ_Tb_Data_SubTrade")) {
			return;
		}
		
		StringBuffer strSql = new StringBuffer();
		//创建试图
		strSql.append(" Create Or Replace Noforce View VQ_Tb_Data_SubTrade AS ");
		
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
		updTables.append("VQ_Tb_Data_SubTrade");
	}

	/**shashijie 2013-2-27 STORY 3366 */
	private void setCreateSubTradeViewSql(StringBuffer strSql,
			String fassetgroupcode) {
		String assetgroupcode = pub.getAssetGroupCode();
		pub.setPrefixTB(fassetgroupcode);//设置系统组合群
		try {
			strSql.append("Select b.Fassetgroupcode, a.FNUM ," +
					" a.FSECURITYCODE ," +
					" a.FPORTCODE ," +
					" a.FBROKERCODE ," +
					" a.FINVMGRCODE ," +
					" a.FTRADETYPECODE ," +
					" a.FCASHACCCODE ," +
					" a.FATTRCLSCODE ," +
					" a.FRATEDATE ," +
					" a.FBARGAINDATE ," +
					" a.FBARGAINTIME ," +
					" a.FSETTLEDATE ," +
					" a.FSETTLETIME ," +
					" a.FMATUREDATE ," +
					" a.FMATURESETTLEDATE ," +
					" a.FFACTCASHACCCODE ," +
					" a.FFACTSETTLEMONEY ," +
					" a.FEXRATE ," +
					" a.FFACTBASERATE ," +
					" a.FFACTPORTRATE ," +
					" a.FAUTOSETTLE ," +
					" a.FPORTCURYRATE ," +
					" a.FBASECURYRATE ," +
					" a.FALLOTPROPORTION ," +
					" a.FOLDALLOTAMOUNT ," +
					" a.FALLOTFACTOR ," +
					" a.FTRADEAMOUNT ," +
					" a.FTRADEPRICE ," +
					" a.FTRADEMONEY ," +
					" a.FACCRUEDINTEREST ," +
					" a.FBAILMONEY ," +
					" a.FFEECODE1 ," +
					" a.FTRADEFEE1 ," +
					" a.FFEECODE2 ," +
					" a.FTRADEFEE2 ," +
					" a.FFEECODE3 ," +
					" a.FTRADEFEE3 ," +
					" a.FFEECODE4 ," +
					" a.FTRADEFEE4 ," +
					" a.FFEECODE5 ," +
					" a.FTRADEFEE5 ," +
					" a.FFEECODE6 ," +
					" a.FTRADEFEE6 ," +
					" a.FFEECODE7 ," +
					" a.FTRADEFEE7 ," +
					" a.FFEECODE8 ," +
					" a.FTRADEFEE8 ," +
					" a.FTOTALCOST ," +
					" a.FCOST ," +
					" a.FMCOST ," +
					" a.FVCOST ," +
					" a.FBASECURYCOST ," +
					" a.FMBASECURYCOST ," +
					" a.FVBASECURYCOST ," +
					" a.FPORTCURYCOST ," +
					" a.FMPORTCURYCOST ," +
					" a.FVPORTCURYCOST ," +
					" a.FSETTLESTATE ," +
					" a.FFACTSETTLEDATE ," +
					" a.FSETTLEDESC ," +
					" a.FORDERNUM ," +
					" a.FDATASOURCE ," +
					" a.FDATABIRTH ," +
					" a.FSETTLEORGCODE ," +
					" a.FDESC ," +
					" a.FCHECKSTATE ," +
					" a.FCREATOR ," +
					" a.FCREATETIME ," +
					" a.FCHECKUSER ," +
					" a.FCHECKTIME ," +
					" a.FETFBALAACCTCODE ," +
					" a.FETFBALASETTLEDATE ," +
					" a.FETFBALAMONEY ," +
					" a.FETFCASHALTERNAT ," +
					" a.FSEATCODE ," +
					" a.FSTOCKHOLDERCODE ," +
					" a.FDS ," +
					" a.FSPLITNUM ," +
					" a.FINVESTTYPE ," +
					" a.FDEALNUM ," +
					" a.FAPPDATE ," +
					" a.FJKDR ," +
					" a.FRECORDDATE ," +
					" a.FDIVDENDTYPE ," +
					" a.FSECURITYDELAYSETTLESTATE ," +
					" a.FBROKERIDCODETYPE ," +
					" a.FBROKERIDCODE ," +
					" a.FSETTLEORGIDCODETYPE ," +
					" a.FSETTLEORGIDCODE ," +
					" a.FCLEARINGBROKERCODE ," +
					" a.FCLEARINGACCOUNT ," +
					" a.FHANDCOSTSTATE ," +
					" a.FBSDATE ," +
					" a.FCANRETURNMONEY ," +
					" a.FBEFORESECURITYCODE ," +
					" a.FMTREPLACEDATE" +
				" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" a" +
				" Join (Select B1.Fportcode, B1.Fassetgroupcode" +
				" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" B1) b On a.Fportcode = b.Fportcode " +
				" ");
		} catch (Exception e) {

		} finally {
			pub.setPrefixTB(assetgroupcode);
		}
	}

	/**shashijie 2013-2-27 STORY 3366 新增接口公共表 */
	private void CreatePublicTable(HashMap hmInfo)throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		
		try{
			conn.setAutoCommit(false);
			bTrans = true;
			
			//自定义接口配置
			if (!dbl.yssTableExist("TB_DAO_Cusconfig")) {
				CreateCusconfig(hmInfo);
			}
			//自定义接口字典设置
			if (!dbl.yssTableExist("Tb_Dao_Dict")) {
				CreateDict(hmInfo);
			}
			//文件名设置
			if (!dbl.yssTableExist("Tb_Dao_FileName")) {
				CreateFileName(hmInfo);
			}
			//文件内容设置
			if (!dbl.yssTableExist("Tb_Dao_FileContent")) {
				CreateFileContent(hmInfo);
			}
			//文件筛选条件设置
			if (!dbl.yssTableExist("Tb_Dao_FileFilter")) {
				CreateFileFilter(hmInfo);
			}
			//合并文件名设置
			if (!dbl.yssTableExist("Tb_Dao_FileMergerName")) {
				CreateFileMergerName(hmInfo);
			}
			//接口预处理设置
			if (!dbl.yssTableExist("Tb_Dao_Pretreat")) {
				CreatePretreat(hmInfo);
			}
			//接口预处理字段设置
			if (!dbl.yssTableExist("Tb_Dao_PretreatField")) {
				CreatePretreatField(hmInfo);
			}
			//目标表删除条件
			if (!dbl.yssTableExist("Tb_Dao_TgtTabCond")) {
				CreateTgtTabCond(hmInfo);
			}
			
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
		}catch(Exception e){
			//新增接口公共表
			throw new YssException("新增接口公共表出错！");
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**shashijie 2013-2-27 STORY 3366 目标表删除条件*/
	private void CreateTgtTabCond(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " create table TB_DAO_TGTTABCOND" +
					" (" +
					" FDPDSCODE    VARCHAR2(20) not null," +
					" FORDERINDEX  NUMBER(3) not null," +
					" FTARGETFIELD VARCHAR2(30) not null," +
					" FDSFIELD     VARCHAR2(30) not null," +
					" FCREATOR     VARCHAR2(20) not null," +
					" FCREATETIME  VARCHAR2(20) not null," +
					" FCHECKUSER   VARCHAR2(20)," +
					" FCHECKTIME   VARCHAR2(20)," +
					" FDESC        VARCHAR2(100)," +
					" FCHECKSTATE  NUMBER(1) not null" +
					" )";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_DAO_TGTTABCOND add constraint PK_TB_DAO_TGTTABCOND primary key (FDPDSCODE, FORDERINDEX) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_TGTTABCOND");
		} catch (Exception e) {
			throw new YssException("创建目标表删除条件出错！");
		} finally {
			
		}
	}

	/**shashijie 2013-2-27 STORY 3366 接口预处理字段设置*/
	private void CreatePretreatField(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " create table TB_DAO_PRETREATFIELD" +
					" (" +
					" FDPDSCODE    VARCHAR2(20) not null," +
					" FORDERINDEX  NUMBER(3) not null," +
					" FSICODE      VARCHAR2(20)," +
					" FPRETTYPE    NUMBER(1) not null," +
					" FDSFIELD     VARCHAR2(200) not null," +
					" FTARGETFIELD VARCHAR2(300) not null," +
					" FDESC        VARCHAR2(100)," +
					" FCREATOR     VARCHAR2(20) not null," +
					" FCREATETIME  VARCHAR2(20) not null," +
					" FCHECKUSER   VARCHAR2(20)," +
					" FCHECKTIME   VARCHAR2(20)," +
					" FCHECKSTATE  NUMBER(1) not null" +
					" )";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_DAO_PRETREATFIELD add constraint PK_TB_DAO_PRETREATFIELD primary key (FDPDSCODE, FORDERINDEX) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_PRETREATFIELD");
		} catch (Exception e) {
			throw new YssException("创建接口预处理字段设置出错！");
		} finally {
			
		}
	}

	/**shashijie 2013-2-27 STORY 3366 接口预处理设置*/
	private void CreatePretreat(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " create table TB_DAO_PRETREAT" +
					" (" +
					" FDPDSCODE     VARCHAR2(20) not null," +
					" FDPDSNAME     VARCHAR2(50) not null," +
					" FDSTYPE       NUMBER(1) not null," +
					" FTARGETTAB    VARCHAR2(30)," +
					" FBEANID       VARCHAR2(30)," +
					" FDATASOURCE   CLOB," +
					" FRELACOMPCODE VARCHAR2(20)," +
					" FDESC         VARCHAR2(100)," +
					" FCHECKSTATE   NUMBER(1) not null," +
					" FCREATOR      VARCHAR2(20) not null," +
					" FCREATETIME   VARCHAR2(20) not null," +
					" FCHECKUSER    VARCHAR2(20)," +
					" FCHECKTIME    VARCHAR2(20)," +
					" FMGROUPSHARE  VARCHAR2(200)," +
					" FSHOWIMPNUM   VARCHAR2(20)" +
					" )";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_DAO_PRETREAT add constraint PK_TB_DAO_PRETREAT primary key (FDPDSCODE) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_PRETREAT");
		} catch (Exception e) {
			throw new YssException("创建接口预处理设置出错！");
		} finally {
			
		}
	}

	/**shashijie 2013-2-27 STORY 合并文件名设置 */
	private void CreateFileMergerName(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " create table TB_DAO_FILEMERGERNAME" +
					" (" +
					" FCUSCFGCODE     VARCHAR2(20) not null," +
					" FORDERNUM       NUMBER(3) not null," +
					" FFILENAMECONENT VARCHAR2(300)," +
					" FVALUETYPE      NUMBER(1) not null," +
					" FFILENAMECLS    VARCHAR2(20)," +
					" FFILENAMEDICT   VARCHAR2(20)," +
					" FTABFEILD       VARCHAR2(30)," +
					" FCREATOR        VARCHAR2(20) not null," +
					" FCREATETIME     VARCHAR2(20) not null," +
					" FCHECKUSER      VARCHAR2(20)," +
					" FCHECKTIME      VARCHAR2(20)," +
					" FDESC           VARCHAR2(100)," +
					" FCHECKSTATE     NUMBER(1) not null," +
					" FFORMAT         VARCHAR2(30)," +
					" DELAYDAYS       NUMBER(6)," +
					" HOLIDAYCODE     VARCHAR2(20)" +
					" )";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_DAO_FILEMERGERNAME add constraint PK_TB_DAO_FILEMERGERNAME primary key (FCUSCFGCODE, FORDERNUM) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_FILEMERGERNAME");
		} catch (Exception e) {
			throw new YssException("创建合并文件名设置出错！");
		} finally {
			
		}
	}

	/**shashijie 2013-2-27 STORY 3366 文件筛选条件设置*/
	private void CreateFileFilter(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " create table TB_DAO_FILEFILTER" +
					" (" +
					" FCUSCFGCODE VARCHAR2(20) not null," +
					" FFIELDCODE  VARCHAR2(30) not null," +
					" FFIELDTYPE  VARCHAR2(5) not null," +
					" FCONTENT    VARCHAR2(1000) not null," +
					" FDESC       VARCHAR2(100)," +
					" FCHECKSTATE NUMBER(1) not null," +
					" FCREATOR    VARCHAR2(20) not null," +
					" FCREATETIME VARCHAR2(20) not null," +
					" FCHECKUSER  VARCHAR2(20)," +
					" FCHECKTIME  VARCHAR2(20)" +
					" )";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_DAO_FILEFILTER add constraint PK_TB_DAO_FILEFILTER primary key (FCUSCFGCODE, FFIELDCODE) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_FILEFILTER");
		} catch (Exception e) {
			throw new YssException("创建文件筛选条件设置出错！");
		} finally {
			
		}
	}

	/**shashijie 2013-2-27 STORY 3366 文件内容设置*/
	private void CreateFileContent(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " create table TB_DAO_FILECONTENT" +
					" (" +
					" FCUSCFGCODE      VARCHAR2(20) not null," +
					" FORDERNUM        NUMBER(3) not null," +
					" FBEGINROW        NUMBER(5) not null," +
					" FLOADINDEX       NUMBER(5)," +
					" FLOADLEN         NUMBER(5)," +
					" FFILECONTENTDICT VARCHAR2(20)," +
					" FFORMAT          VARCHAR2(30)," +
					" FTABFEILD        VARCHAR2(30)," +
					" FCREATOR         VARCHAR2(20) not null," +
					" FCREATETIME      VARCHAR2(20) not null," +
					" FCHECKUSER       VARCHAR2(20)," +
					" FCHECKTIME       VARCHAR2(20)," +
					" FDESC            VARCHAR2(100)," +
					" FCHECKSTATE      NUMBER(1) not null," +
					" FORDER           VARCHAR2(4) default ' '," +
					" FUNEXPORT        NUMBER(1) default 0 not null" +
					" )";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_DAO_FILECONTENT add constraint PK_TB_DAO_FILECONTENT primary key (FCUSCFGCODE, FORDERNUM) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_FILECONTENT");
		} catch (Exception e) {
			throw new YssException("创建文件内容设置出错！");
		} finally {
			
		}
	}

	/**shashijie 2013-2-27 STORY 3366 文件名设置*/
	private void CreateFileName(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " create table TB_DAO_FILENAME" +
					" (" +
					" FCUSCFGCODE     VARCHAR2(20) not null," +
					" FORDERNUM       NUMBER(3) not null," +
					" FFILENAMECONENT VARCHAR2(300)," +
					" FVALUETYPE      NUMBER(1) not null," +
					" FFILENAMECLS    VARCHAR2(20)," +
					" FFILENAMEDICT   VARCHAR2(20)," +
					" FTABFEILD       VARCHAR2(30)," +
					" FCREATOR        VARCHAR2(20) not null," +
					" FCREATETIME     VARCHAR2(20) not null," +
					" FCHECKUSER      VARCHAR2(20)," +
					" FCHECKTIME      VARCHAR2(20)," +
					" FDESC           VARCHAR2(100)," +
					" FCHECKSTATE     NUMBER(1) not null," +
					" FFORMAT         VARCHAR2(30)," +
					" DELAYDAYS       NUMBER(6)," +
					" HOLIDAYCODE     VARCHAR2(20)" +
					" )";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_DAO_FILENAME add constraint PK_TB_DAO_FILENAME primary key (FCUSCFGCODE, FORDERNUM) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_FILENAME");
		} catch (Exception e) {
			throw new YssException("创建文件名设置出错！");
		} finally {
			
		}
	}

	/**shashijie 2013-2-27 STORY 3366 自定义接口字典设置*/
	private void CreateDict(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " create table TB_DAO_DICT " +
					" (" +
					" FDICTCODE   VARCHAR2(50) not null," +
					" FSRCCONENT  VARCHAR2(100) not null," +
					" FCNVCONENT  VARCHAR2(50) not null," +
					" FDICTNAME   VARCHAR2(50) not null," +
					" FDESC       VARCHAR2(100)," +
					" FCHECKSTATE NUMBER(1) not null," +
					" FCREATOR    VARCHAR2(20) not null," +
					" FCREATETIME VARCHAR2(20) not null," +
					" FCHECKUSER  VARCHAR2(20)," +
					" FCHECKTIME  VARCHAR2(20)" +
					" )";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_DAO_DICT add constraint PK_TB_DAO_DICT primary key (FDICTCODE, FSRCCONENT) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_DICT");
		} catch (Exception e) {
			throw new YssException("创建自定义接口字典设置出错！");
		} finally {
			
		}
	}

	/**shashijie 2013-2-27 STORY 3366 自定义接口配置*/
	private void CreateCusconfig(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		
		try {
			String strSql = " create table TB_DAO_CUSCONFIG" +
					" (" +
					" FCUSCFGCODE     VARCHAR2(20) not null," +
					" FCUSCFGNAME     VARCHAR2(50) not null," +
					" FSPLITTYPE      NUMBER(1)," +
					" FSPLITMARK      VARCHAR2(20)," +
					" FENDMARK        VARCHAR2(20)," +
					" FFILECUSCFG     VARCHAR2(20) not null," +
					" FCUSCFGTYPE     VARCHAR2(20) not null," +
					" FFILETYPE       VARCHAR2(20) not null," +
					" FTABNAME        VARCHAR2(30) not null," +
					" FFILENAMEDESC   VARCHAR2(300) not null," +
					" FFILEINFODESC   VARCHAR2(300)," +
					" FFILECNTDESC    VARCHAR2(3000) not null," +
					" FFILETRAILDESC  VARCHAR2(300)," +
					" FVALIDATEDESC   VARCHAR2(300)," +
					" FDPCODES        VARCHAR2(400)," +
					" FCREATOR        VARCHAR2(20) not null," +
					" FCREATETIME     VARCHAR2(20) not null," +
					" FCHECKUSER      VARCHAR2(20)," +
					" FCHECKTIME      VARCHAR2(20)," +
					" FDESC           VARCHAR2(100)," +
					" FCHECKSTATE     NUMBER(1) not null," +
					" FAUTOFIX        VARCHAR2(20) default 'Y' not null," +
					" FCFGENCODE      VARCHAR2(20) default 'unicode' not null," +
					" FFILEFILTERDESC VARCHAR2(3000)," +
					" FEXCELPWD       VARCHAR2(1)," +
					" FFILEROWS       VARCHAR2(20)," +
					" FMERGER         VARCHAR2(200)," +
					" FMENUBARCODE    VARCHAR2(200)," +
					" FMENUBARNAME    VARCHAR2(500) " +
					" ) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_DAO_CUSCONFIG add constraint PK_TB_DAO_CUSCONFIG primary key (FCUSCFGCODE) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_CUSCONFIG");
		} catch (Exception e) {
			throw new YssException("创建自定义接口配置出错！");
		} finally {
			
		}
	}

	/**shashijie 2013-2-27 STORY 3366 获取所有组合群代码*/
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
	 * add by songjie 2013.03.07
	 * 创建会话级 股指期货交易数据临时表
	 * STORY #3719 需求上海-[开发部]QDIIV4[中]20130313001
	 * @param hmInfo
	 * @throws YssException
	 */
	private void createTMPTable(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		StringBuffer buff = null;
		ResultSet rs = null;
		String duration = "";//表类型
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try{
			rs = dbl.openResultSet("select * from " + pub.yssGetTableName("tb_Data_FuturesTrade"));			
			if(dbl.isFieldExist(rs, "FBAILTYPE")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_FuturesTrade") +" drop column FBAILTYPE ");
			}
			if(dbl.isFieldExist(rs, "FBAILSCALE")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_FuturesTrade") +" drop column FBAILSCALE ");
			}
			if(dbl.isFieldExist(rs, "FBAILFIX")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_FuturesTrade") +" drop column FBAILFIX ");
			}
			if(dbl.isFieldExist(rs, "FBAILCashCode")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_FuturesTrade") +" drop column FBAILCashCode ");
			}
			
			dbl.closeResultSetFinal(rs);
			
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("Tb_Data_FuturesTrade_TMP"));
            buff.append(" ( ");
            buff.append(" FNUM               VARCHAR2(20) not null, ");
            buff.append(" FSECURITYCODE      VARCHAR2(50) not null, ");
            buff.append(" FPORTCODE          VARCHAR2(50) not null, ");
            buff.append(" FBROKERCODE        VARCHAR2(100) not null, ");
            buff.append(" FINVMGRCODE        VARCHAR2(50) not null, ");
            buff.append(" FTRADETYPECODE     VARCHAR2(50) not null, ");
            buff.append(" FBEGBAILACCTCODE   VARCHAR2(50) not null, ");
            buff.append(" FCHAGEBAILACCTCODE VARCHAR2(50) not null, ");
            buff.append(" FBARGAINDATE       DATE not null, ");
            buff.append(" FBARGAINTIME       VARCHAR2(20) not null, ");
            buff.append(" FSETTLEDATE        DATE not null, ");
            buff.append(" FSETTLETIME        VARCHAR2(20), ");
            buff.append(" FSETTLETYPE        NUMBER(1) default 1 not null, ");
            buff.append(" FTRADEAMOUNT       NUMBER(18,4) not null, ");
            buff.append(" FTRADEPRICE        NUMBER(20,8) not null, ");
            buff.append(" FTRADEMONEY        NUMBER(18,4) not null, ");
            buff.append(" FBEGBAILMONEY      NUMBER(18,4) not null, ");
            buff.append(" FSETTLEMONEY       NUMBER(18,4) not null, ");
            buff.append(" FBASECURYRATE      NUMBER(20,15) not null, ");
            buff.append(" FPORTCURYRATE      NUMBER(20,15) not null, ");
            buff.append(" FSETTLESTATE       NUMBER(1) default 0 not null, ");
            buff.append(" FFEECODE1          VARCHAR2(20), ");
            buff.append(" FTRADEFEE1         NUMBER(18,4), ");
            buff.append(" FFEECODE2          VARCHAR2(20), ");
            buff.append(" FTRADEFEE2         NUMBER(18,4), ");
            buff.append(" FFEECODE3          VARCHAR2(20), ");
            buff.append(" FTRADEFEE3         NUMBER(18,4), ");
            buff.append(" FFEECODE4          VARCHAR2(20), ");
            buff.append(" FTRADEFEE4         NUMBER(18,4), ");
            buff.append(" FFEECODE5          VARCHAR2(20), ");
            buff.append(" FTRADEFEE5         NUMBER(18,4), ");
            buff.append(" FFEECODE6          VARCHAR2(20), ");
            buff.append(" FTRADEFEE6         NUMBER(18,4), ");
            buff.append(" FFEECODE7          VARCHAR2(20), ");
            buff.append(" FTRADEFEE7         NUMBER(18,4), ");
            buff.append(" FFEECODE8          VARCHAR2(20), ");
            buff.append(" FTRADEFEE8         NUMBER(18,4), ");
            buff.append(" FDESC              VARCHAR2(200), ");
            buff.append(" FCHECKSTATE        NUMBER(1) not null, ");
            buff.append(" FCREATOR           VARCHAR2(20) not null, ");
            buff.append(" FCREATETIME        VARCHAR2(20) not null, ");
            buff.append(" FCHECKUSER         VARCHAR2(20), ");
            buff.append(" FCHECKTIME         VARCHAR2(20), ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("TB_DATA_FUTTRADE_TMP"));
            buff.append(" PRIMARY KEY (FNUM) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
			
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("Tb_Data_Futurestrade_TMP".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Data_Futurestrade_TMP"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("Tb_Data_Futurestrade_TMP")));
        			}
        			
                    updTables.append(pub.yssGetTableName("Tb_Data_Futurestrade_TMP"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("Tb_Data_Futurestrade_TMP"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
			
        	buff.delete(0, buff.length());
		}catch(Exception e){
			throw new YssException("获取所有组合群代码出错！");
		}finally{
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		}
	}
	
	/**yeshenghong 2013-3-18 STORY 3715 */
	private void CreateNHPayFeeDataTable(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " create table TB_NH_PAYFEEDATA ( " +
					"  ASSERTCODE VARCHAR2(20) not null, " +
					"  ASSERTNAME VARCHAR2(100) not null, " +
					"  FEEYEAR    NUMBER(4) not null, " +
					"  FEEMONTH   NUMBER(2) not null, " +
					"  FEETYPE    VARCHAR2(100) not null, " +
					"  FEED       NUMBER(24,8), " +
					"  FEEJ       NUMBER(24,8), " +
					"  FEEENDBAL  NUMBER(24,8), " +
					"  FEEPAYTYPE VARCHAR2(20) not null, " +
					"  FEEFLAG    VARCHAR2(1) not null, " +
					"  FEEBY      VARCHAR2(100) not null " +
					" )";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_NH_PAYFEEDATA add constraint PK_TB_NH_PAYFEEDATA primary key (ASSERTCODE, FEEYEAR, FEEMONTH, FEETYPE, FEEBY) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_FILEMERGERNAME");
		} catch (Exception e) {
			throw new YssException("创建农行费用一览表数据表出错！");
		} 
	}
	
	//20130323 added by liubo.Story #2839
	//更新日志类别表中，证券信息设置和权限设置的菜单条
	private void UpdateLogType() throws YssException
	{
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String strSql = "";
		
		try{
			conn.setAutoCommit(false);
			bTrans = true;
			
			if (dbl.yssTableExist("tb_fun_logtype"))
			{
				strSql = "update tb_fun_logtype set FFunCode = 'infor' where FFunCode = 'security'";
				dbl.executeSql(strSql);
				
				strSql = "delete tb_fun_logtype where FFunCode = 'rightSet'";
				dbl.executeSql(strSql);
				
				strSql = "insert into tb_fun_logtype(FFunCode,FFunType,FReserve_1,FReserve_2,FReserve_3) values('rightSet_Public','账号操作类',' ',' ',' ')";
				dbl.executeSql(strSql);

				strSql = "insert into tb_fun_logtype(FFunCode,FFunType,FReserve_1,FReserve_2,FReserve_3) values('rightSet_Port','账号操作类',' ',' ',' ')";
				dbl.executeSql(strSql);

				strSql = "insert into tb_fun_logtype(FFunCode,FFunType,FReserve_1,FReserve_2,FReserve_3) values('rightSet_Report','账号操作类',' ',' ',' ')";
				dbl.executeSql(strSql);

				strSql = "insert into tb_fun_logtype(FFunCode,FFunType,FReserve_1,FReserve_2,FReserve_3) values('rightSet_Dao','账号操作类',' ',' ',' ')";
				dbl.executeSql(strSql);

				strSql = "insert into tb_fun_logtype(FFunCode,FFunType,FReserve_1,FReserve_2,FReserve_3) values('rightSet_Role','账号操作类',' ',' ',' ')";
				dbl.executeSql(strSql);
			}
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
		}catch(Exception e){
			//新增接口公共表
			throw new YssException("更新日志类别表出错：" + e.getMessage());
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
