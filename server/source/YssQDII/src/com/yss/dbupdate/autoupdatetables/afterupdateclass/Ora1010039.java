package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class Ora1010039 extends BaseDbUpdate{
	
	public Ora1010039(){
		
	}
	
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			alterTable();//add by lidaolong 20110315  #386 增加一个功能，能够自动支付管理费和托管费
			alterTableTDzAccount(); // add by qiuxufeng 20110315 615 电子对账科目表中的FAcctClass字段改成保留数字的形式
			alterTableFcwVch(); // add by qiuxufeng 20110316 599 录入交易数据的数量增加可保留至小数点后6位
			alterTableTdzBbinfo(); // add by qiuxufeng 20110316 581 生成余额对账表tdzbalance数据时，组合代码需改为资产代码
			addTableExchange();  //add by fangjiang 2011.03.22 529
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0039 更新出错！", ex);
		}
	}

	/**
	 * add by lidaolong 20110315  #386 增加一个功能，能够自动支付管理费和托管费
	 * @throws YssException
	 */
	public void alterTable() throws YssException{
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
		
		try {
			if (existsTabColumn_Ora(pub.yssGetTableName("Tb_Para_InvestPay"),"FAutoPay")){//判断表中这个字段是否存在
				strSql ="alter table "+pub.yssGetTableName("Tb_Para_InvestPay")+"  add FAutoPay VARCHAR2(20) default '0'";
				dbl.executeSql(strSql);	
			}
			
			if (existsTabColumn_Ora(pub.yssGetTableName("Tb_Para_InvestPay"),"FAutoPayType")){//判断表中这个字段是否存在
				strSql ="alter table "+pub.yssGetTableName("Tb_Para_InvestPay")+"  add FAutoPayType VARCHAR2(20)";
				dbl.executeSql(strSql);	
			}
			
			if (existsTabColumn_Ora(pub.yssGetTableName("Tb_Para_InvestPay"),"FAutoPayDay")){//判断表中这个字段是否存在
				strSql ="alter table "+pub.yssGetTableName("Tb_Para_InvestPay")+"  add FAutoPayDay number(4) default 1";
				dbl.executeSql(strSql);	
			}
			
			//#1279 使用当前版本设置【期权期货保证金账户设置】时错误  add by jiangshichao 2011.03.22 
			if(dbl.yssTableExist(pub.yssGetTableName("TB_DATA_OPTIONSVALCAL"))){
				if (dbl.getTableConstaintKey(pub.yssGetTableName("TB_DATA_OPTIONSVALCAL")))
				{
					dbl.executeSql("alter table "+pub.yssGetTableName("TB_DATA_OPTIONSVALCAL")+
								" drop constraint PK_"+pub.yssGetTableName("TB_DATA_OPTIONSVALCAL"));
				}
				if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("TB_DATA_OPTIONSVALCAL")).trim().length()!=0){
					dbl.executeSql("drop  index PK_"+pub.yssGetTableName("TB_DATA_OPTIONSVALCAL"));	
				}
				strSql =" alter table "+pub.yssGetTableName("TB_DATA_OPTIONSVALCAL")+" add constraint PK_"+pub.yssGetTableName("TB_DATA_OPTIONSVALCAL")+" primary key (FCASHACCCODE, FPORTCODE, FMARKTYPE,FEXCHAGECODE)";
				dbl.executeSql(strSql);
			}
			//#1279 使用当前版本设置【期权期货保证金账户设置】时错误 end -------------------------
		}catch (Exception e) {
			throw new YssException("版本1.0.1.0039 变更表结构出错！", e);
		}
		finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	/**
	 * add by qiuxufeng 20110315 615 电子对账科目表中的FAcctClass字段改成保留数字的形式
	 * @方法名：alterTableTDzAccount
	 * @返回类型：void
	 */
	public void alterTableTDzAccount() throws YssException {
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
		
		try {
			if (existsTabColumn_Ora("TDzAccount","FAcctClass_temp")){//判断表中这个字段是否存在
				strSql ="alter table TDzAccount add FAcctClass_temp number(2)"; // 增加新列
				dbl.executeSql(strSql);
			}
			
			if (!existsTabColumn_Ora("TDzAccount","FAcctClass")){//判断表中这个字段是否存在
				strSql ="update TDzAccount" +
						" set FAcctClass_temp =" +
						" decode(FAcctClass, '资产类', 1, '负债类', 2, '共同类', 3, '权益类', 4, '损益类', 5)"; // 转换原列数据到新列
				dbl.executeSql(strSql);
			}
			
			if (!existsTabColumn_Ora("TDzAccount","FAcctClass")){//判断表中这个字段是否存在
				strSql ="alter table TDzAccount drop column FAcctClass"; // 删除原列
				dbl.executeSql(strSql);
			}
			
			if (!existsTabColumn_Ora("TDzAccount","FAcctClass_temp") && existsTabColumn_Ora("TDzAccount","FAcctClass")){//判断表中这个字段是否存在
				strSql ="alter table TDzAccount rename column FAcctClass_temp to FAcctClass"; // 更改新列名替换原列
				dbl.executeSql(strSql);
			}
		}catch (Exception e) {
			throw new YssException("版本1.0.1.0039 变更表结构出错！", e);
		}
		finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	/**
	 * add by qiuxufeng 20110316 599 录入交易数据的数量增加可保留至小数点后6位
	 * @方法名：alterTableFcwVch
	 * @返回类型：void
	 */
	public void alterTableFcwVch() throws YssException {
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
		
		try {
			String sTableName = "";
			if(dbl.yssTableExist("LSetList")){
				//判断是否有财务套帐列表，如果没有就不进行更新
				strSql = "select * from LSetList where FYear = 2011";
				rs = dbl.openResultSet(strSql);
				while(rs.next()) {
					sTableName = "A2011" + YssFun.formatNumber(rs.getInt("FSetCode"), "000") + "FcwVch";
					if(!existsTabColumn_Ora(sTableName,"Fsl") && !existsTabColumn_Ora(sTableName,"FBsl")
							&& existsTabColumn_Ora(sTableName,"Fsl_temp") && existsTabColumn_Ora(sTableName,"FBsl_temp")) {
						strSql = "alter table " + sTableName + " add Fsl_temp number(19,4)"; // 增加临时列
						dbl.executeSql(strSql);
						strSql = "alter table " + sTableName + " add FBsl_temp number(19,4)"; // 增加临时列
						dbl.executeSql(strSql);
						strSql = "update " + sTableName + " set Fsl_temp = Fsl, FBsl_temp = FBsl"; // 复制原有数据
						dbl.executeSql(strSql);
						strSql = "alter table " + sTableName + " modify Fsl null"; // 清空原列数据
						dbl.executeSql(strSql);
						strSql = "alter table " + sTableName + " modify FBsl null"; // 清空原列数据
						dbl.executeSql(strSql);
						strSql = "alter table " + sTableName + " modify Fsl number(21,6)"; // 修改原字段类型长度
						dbl.executeSql(strSql);
						strSql = "alter table " + sTableName + " modify FBsl number(21,6)"; // 修改原字段类型长度
						dbl.executeSql(strSql);
						strSql = "update " + sTableName + " set Fsl = Fsl_temp, FBsl = FBsl_temp"; // 还原原有数据
						dbl.executeSql(strSql);
						strSql = "alter table " + sTableName + " modify Fsl not null"; // 还原字段类型不可为空
						dbl.executeSql(strSql);
						strSql = "alter table " + sTableName + " modify FBsl not null"; // 还原字段类型不可为空
						dbl.executeSql(strSql);
						strSql = "alter table " + sTableName + " drop column Fsl_temp"; // 删除临时列
						dbl.executeSql(strSql);
						strSql = "alter table " + sTableName + " drop column FBsl_temp"; // 删除临时列
						dbl.executeSql(strSql);
					}
				}
			}
		}catch (Exception e) {
			throw new YssException("版本1.0.1.0039 变更表结构出错！", e);
		}
		finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	/**
	 * add by qiuxufeng 20110316 581 生成余额对账表tdzbalance数据时，组合代码需改为资产代码
	 * @方法名：alterTableTdzBbinfo
	 * @返回类型：void
	 */
	public void alterTableTdzBbinfo() throws YssException {
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
		
		try {
			if(existsTabColumn_Ora("TdzBbinfo","FFundCode")) {
				strSql = "alter table tdzbbinfo add FFundCode varchar2(20)"; // 报文信息表增加资产代码
				dbl.executeSql(strSql);
			}
			
		}catch (Exception e) {
			throw new YssException("版本1.0.1.0039 变更表结构出错！", e);
		}
		finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	/**
	 * add by fangjiang 20110322 549  汇入汇出临时表
	 * @方法名：addTableExchange
	 * @返回类型：void
	 */
	public void addTableExchange() throws YssException {
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
		
		try {
			if (dbl.yssTableExist("TMP_EXCHANGE_INOUT")) {
				strSql = " drop table TMP_EXCHANGE_INOUT ";//先删除原有表
				dbl.executeSql(strSql);	
			}
			//创建表
			strSql = " create table TMP_EXCHANGE_INOUT " 
				  + "(FPORTCODE    VARCHAR2(20) not null,"
				  + "FTRADEDATE    DATE not null,"
				  + "FSETTLEDATE   DATE not null,"
				  + "FTYPE         VARCHAR2(20) not null,"
				  + "FBCURYCODE    VARCHAR2(20) not null,"
				  + "FBMONEY       NUMBER(18,4),"
				  + "constraint PK_TMP_EXCHANGE_INOUT" + " primary key (FPORTCODE,FTRADEDATE,FSETTLEDATE,FTYPE,FBCURYCODE))";
			dbl.executeSql(strSql);	
			
		}catch (Exception e) {
			throw new YssException("版本1.0.1.0039 变更表结构出错！", e);
		}
		finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
}
