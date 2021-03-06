package com.yss.main.operdeal.valuation;

import com.yss.dsub.BaseBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssGlobal;

public class DataPrepareBeforeVal extends BaseBean {
	public DataPrepareBeforeVal() {
	}

	// 创建行情数据的临时表，目的是在接下来的估值中使用，以提高执行效率
	public String createMarketValueTable(java.util.Date dDate)
			throws YssException {
		String tableName = "";
		String sql = "";
		try {
			//synchronized(YssGlobal.objSecRecLock){//add by lidaolong 20110422 BUG #4606 :: 系统里在建临时的表和视图时，系统就会报错
						
			tableName = "Tmp_MV_" + pub.getUserCode();// 临时表由日期与估值方法组成
			if (dbl.yssTableExist(tableName)) {
				sql = "drop table " + tableName;
				/**shashijie ,2011-10-12 , STORY 1698*/
				dbl.executeSql(dbl.doOperSqlDrop(sql));
				/**end*/
			}
			if (dbl.dbType == YssCons.DB_ORA) {
				sql = "create table "
						+ tableName
						+ " as select m1.* from "
						+ pub.yssGetTableName("Tb_Data_MarketValue")
						+ " m1 join "
						+ " (select /*+first_rows(1)*/max(FMktValueDate) as FMktValueDate,FSecurityCode,FMktSrcCode from " //添加oracle提示/*+first_rows(1)*/byleeyu 20100613
						+ pub.yssGetTableName("Tb_Data_MarketValue")
						+ " m2 where FCheckState = 1 and FMktValueDate<="
						+ dbl.sqlDate(dDate)
						+ " group by FSecurityCode,FMktSrcCode)m2 "
						+ " on m1.FMKtvalueDate=m2.FMktValueDate and m1.FSecuritycode=m2.FSecurityCode "
						+ " and m1.FMktSrcCode= m2.FMktSrcCode";
				dbl.executeSql(sql);
				sql = "alter table " + tableName + " add constraints pk_"
						+ tableName
						+ " primary key (FSecurityCode,FPortCode,FMktSrcCode)";// 添加主键
				dbl.executeSql(sql);
			}
	//	}
			return tableName;
		} catch (Exception ex) {
			throw new YssException(ex);
		}
	}
}
