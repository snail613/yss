package com.yss.main.operdeal.datainterface.etf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;

/**
 * add by songjie 2014.08.08
 * STORY #18037 需求上海-[华安]QDIIV4.0[中]20140724001
 * 华安ETF非担保交收划款指令导出接口  
 * 接口导出的取数类似与 博时ETF非担保交收划款指令表取数逻辑
 * @author songjie
 *
 */
public class ExpETFNonGrtStl extends DataBase {
	public ExpETFNonGrtStl(){
		
	}
//  add by songjie 2014.08.08
//  STORY #18037 需求上海-[华安]QDIIV4.0[中]20140724001 
//  总的导出逻辑入口	
	public void inertData() throws YssException {
        Connection con = dbl.loadConnection(); // 新建连接
        boolean bTrans = false;//事务控制标识
        try{
        	con.setAutoCommit(false);
        	bTrans = true;
        	
        	createTMPTable();//如果没有临时表  则创建临时表
        	delTMPData();//删除临时表中的所有数据
			dealDataIntoTMP();//通过sql逻辑取数据插入到临时表中
		
			con.commit(); //提交事务
			bTrans = false;
			con.setAutoCommit(true); //设置可以自动提交
        }catch(Exception e){
        	throw new YssException("导出ETF非担保交收指令数据出错！",e);
        }finally{
        	dbl.endTransFinal(con,bTrans);
        }
	}

//  add by songjie 2014.08.08
//  STORY #18037 需求上海-[华安]QDIIV4.0[中]20140724001 
//  如果没有临时表  则创建临时表
	private void createTMPTable()throws YssException{
		try{
			if (dbl.yssTableExist("TMP_QS_ETF_HA")) {
				return;
			} else {
				StringBuffer createTabSql = new StringBuffer();
				createTabSql
				.append("create table TMP_QS_ETF_HA")
				.append("(")
				.append(" SBBH VARCHAR2(10), ")
				.append(" HFRQ VARCHAR2(8), ")
				.append(" HFLB VARCHAR2(3), ")
				.append(" HFBZ VARCHAR2(1), ")
				.append(" GDDM VARCHAR2(10), ")
				.append(" HBLB VARCHAR2(3), ")
				.append(" HKJE VARCHAR2(10), ")
				.append(" RQ1  VARCHAR2(8), ")
				.append(" RQ2  VARCHAR2(10), ")
				.append(" JJDM VARCHAR2(20), ")
				.append(" YWDM VARCHAR2(20), ")
				.append(" BZ   VARCHAR2(40) ")
				.append(")");
			
				dbl.executeSql(createTabSql.toString());
			}
		}catch(Exception e){
			throw new YssException("创建临时表 TMP_QS_ETF_HA 出错！", e);
		}
	}

//  add by songjie 2014.08.08
//  STORY #18037 需求上海-[华安]QDIIV4.0[中]20140724001 
//  删除临时表中的所有数据	
    private void delTMPData() throws YssException{
    	try {
    		dbl.executeSql(" delete from TMP_QS_ETF_HA ");
        }catch(Exception e){
        	throw new YssException("删除临时表 TMP_QS_ETF_HA 数据出错");
        }
	}

//  add by songjie 2014.08.08
//  STORY #18037 需求上海-[华安]QDIIV4.0[中]20140724001 
//  通过sql逻辑取数据插入到临时表中    
    private void dealDataIntoTMP() throws YssException{
        ResultSet rs = null;//结果集声明
        PreparedStatement pst = null;// 声明PreparedStatement
        StringBuffer insertSql = new StringBuffer();
        int count = 0;
    	try {
            insertSql
            .append(" insert into TMP_QS_ETF_HA(SBBH,HFRQ,HFLB,HFBZ,GDDM,HBLB,HKJE,RQ1,RQ2,JJDM,YWDM,BZ)")
            .append(" values(?,?,?,?,?,?,?,?,?,?,?,?) ");
            pst = dbl.openPreparedStatement(insertSql.toString());
            
            String[] portCodes = this.sPort.split(",");
            for(int i = 0; i < portCodes.length; i++){
            	rs = dbl.openResultSet(buildQuerysql(portCodes[i]));
            	while(rs.next()){
            		count++;
            		pst.setString(1, rs.getString("SBBH"));
            		pst.setString(2, rs.getString("HFRQ"));
            		pst.setString(3, rs.getString("HFLB"));
            		pst.setString(4, rs.getString("HFBZ"));
            		pst.setString(5, rs.getString("GDDM"));
            		pst.setString(6, rs.getString("HBLB"));
            		pst.setString(7, rs.getString("HKJE"));
            		pst.setString(8, rs.getString("RQ1"));
            		pst.setString(9, rs.getString("RQ2"));
            		pst.setString(10, rs.getString("JJDM"));
            		pst.setString(11, rs.getString("YWDM"));
            		pst.setString(12, rs.getString("BZ"));
            		
            		pst.addBatch();
            		
            		if(count == 100){
            			pst.executeBatch();
            			count = 0;
            		}
            	}
            	dbl.closeResultSetFinal(rs);
            }
            
            if(count > 0){
            	pst.executeBatch();
            }
    	}catch(Exception e){
    		throw new YssException("插入临时表  TMP_QS_ETF_HA 出错",e);
    	}finally{
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
        }
	}
   
//    add by songjie 2014.08.08
//    STORY #18037 需求上海-[华安]QDIIV4.0[中]20140724001 
    
//    获取导出到DBF文件中的数据，各字段取数逻辑如下：    
//    HKJE（划款金额）字段取数逻辑如下：      
//    获取类型 1 数据：
//    YWDM（业务代码）= 205
//    BZ（标识） = ETF申购赎回现金差额净额
//    一、获取对应交收日期下面的申赎篮子数：
//    1、申购篮子数的取数规则：
//    通过产品销售业务界面的现金差额结转日期=交收日期，关联出交易日期，
//    再根据“ETF结算明细库”（Tb_014_ETF_JSMXInterface）中的申购日期（FBargainDate）=交易日期、
//    业务类型（FTradeTypeCode）=102（申购）、清算标识（FClearMark）=276、记录类型（FRecordType）=003的条件
//    取出申购份额（FTradeAmount），按照股东代码（fstockholdercode）字段汇总申购份额（FTradeAmount）；
//    再用申购份额（FTradeAmount）字段除以“ETF参数设置”界面的“基准比例”，最后得到每个股东申购的篮子数；
//
//    2、赎回篮子数的取数规则：
//    通过产品销售业务界面的现金差额结转日期=交收日期，关联出交易日期，
//    再根据“ETF结算明细库”（Tb_014_ETF_JSMXInterface）中的申购日期（FBargainDate）=交易日期、
//    业务类型（FTradeTypeCode）=103（赎回）、清算标识（FClearMark）=空、记录类型（FRecordType）=003的条件
//    取出赎回份额（FTradeAmount），按照股东代码（fstockholdercode）汇总赎回份额（FTradeAmount）；
//    再用赎回份额（FTradeAmount）字段除以“ETF参数设置”界面的“基准比例”，最后得到每个股东赎回的篮子数；
//
//    【同时，此处的股东代码（fstockholdercode）字段即“股东代码”】
//
//    3、每个股东的轧差篮子数=申购篮子数-赎回篮子数；
//
//    二、获取对应交收日期下面的单位现金差额
//    1、通过产品销售业务界面的现金差额结转日期=交收日期，关联出交易日期，
//    再根据财务估值表中的日期=交易日期，取出单位现金差额字段；
//
//    三、获取最终的现金差额净额
//    金额=根据每个股东的轧差篮子数*单位现金差额。
//    当该金额大于0时，HFLB（划付类别）显示为F；
//    当该金额小于0时，HFLB（划付类别）显示为S；
//
//    -----------------------------------------------------------------------------------------------------------------
    
//    获取类型 2 数据：
//    YWDM（业务代码）= 203
//    BZ（标识） = ETF申购赎回现金替代退补（申购的退补款）

//    1、获取申购篮子数：
//    通过ETF台账表(Tb_XXX_ETF_StandingBook)中，交易类型为申购，当退款日期（FRefundDate）=交收日期时，
//    直接获取申购日期（FBuyDate）；
//    再根据“ETF结算明细库”（Tb_014_ETF_JSMXInterface）中的申购日期（FBargainDate）=ETF台账表中的申购日期（FBuyDate）、
//    业务类型（FTradeTypeCode）=102（申购）、清算标识（FClearMark）=276、
//    记录类型（FRecordType）=003的条件取出申购份额（FTradeAmount），
//    按照股东代码（fstockholdercode）字段汇总申购份额（FTradeAmount）；
//    再用申购份额（FTradeAmount）字段除以“ETF参数设置”界面的“基准比例”，最后得到每个股东申购的篮子数；
//    【同时，此处的股东代码（fstockholdercode）字段即“股东代码”】
//
//    2、获取单位篮子的申购退补款
//    取自ETF台账表(Tb_XXX_ETF_StandingBook)中，交易类型为申购，当退款日期（FRefundDate）=交收日期，
//    浏览日期（FDate）为最大的日期，再根据股东代码（fstockholdercode）字段汇总“应退合计”字段（FSumReturn）；
//
//    3、获取最终的申购退补款：等于1步骤对应股东的申购篮子数*2步骤对应股东的单位篮子申购退补款；
//    当该金额大于0时，HFLB（划付类别）显示为S；
//    当该金额小于0时，HFLB（划付类别）显示为F；
//
//    -----------------------------------------------------------------------------------------------------------------
    
//    获取类型 3 数据：
//    YWDM（业务代码）= 202
//    BZ（标识） = ETF赎回现金替代（赎回退补款）
//
//    1、获取赎回篮子数：
//    通过ETF台账表(Tb_XXX_ETF_StandingBook)中，交易类型为赎回，当退款日期（FRefundDate）=交收日期时，
//    直接获取赎回日期（FBuyDate）；
//    再根据“ETF结算明细库”（Tb_014_ETF_JSMXInterface）中的申购日期（FBargainDate）=ETF台账表中的赎回日期（FBuyDate）、
//    业务类型（FTradeTypeCode）=103（赎回）、清算标识（FClearMark）=空、
//    记录类型（FRecordType）=003的条件取出赎回份额（FTradeAmount），
//    按照股东代码（fstockholdercode）字段汇总赎回份额（FTradeAmount）；
//    再用赎回份额（FTradeAmount）字段除以“ETF参数设置”界面的“基准比例”，最后得到每个股东赎回的篮子数；
//    【同时，此处的股东代码（fstockholdercode）字段即“股东代码”】
//
//    2、获取赎回的单位篮子必须现金替代
//    通过产品销售业务界面销售类型为赎回，且现金替代结转日期=交收日期，关联出交易日期，
//    再通过ETF股票篮表（Tb_XXX_ETF_StockList）中导入日期=交易日期，替代标志为6的“替代金额”字段
//
//    3、获取赎回的单位篮子可以现金替代
//    取自ETF台账表(Tb_XXX_ETF_StandingBook)，交易类型为赎回，当退款日期（FRefundDate）=交收日期，
//    浏览日期（Fdate）为最大的日期，再根据股东代码（fstockholdercode）字段汇总“应退合计”字段（FSumReturn）；
//
//    4、获取最终的赎回退补款
//    用1步骤每个股东的篮子数*2步骤的单位篮子必须现金替代金额+1步骤每个股东的篮子数*3步骤每个股东的单位篮子可以现金替代金额。
//    这种情况无需判断金额正负，HFLB（划付类别）默认显示为S。
    
//    JJDM（基金代码）字段 = 对应接口处理界面勾选的相应组合的资产代码
//    RQ2（应交收日）字段 = 即HFRQ（交收日期）
//    RQ1（交易日期）字段 = 需要按照“业务代码”的字段获取它的交易日期，详细规则参照“划款金额”字段。
//    HBLB（币种）字段 = RMB
//    GDDM（股东代码）字段 = 详细规则参照“划款金额”字段的取数。
//    HFBZ（收付标志）字段  = 默认为F或S，需要按照“划款金额”和“业务代码”这两个字段指动态加载为F或S，详细规则参照“划款金额”字段的取数。
//    HFLB（划付类别）字段 = 002 
    private String buildQuerysql(String portCode) throws YssException{
    	StringBuffer strBf = new StringBuffer();
    	strBf
    	.append(" select REPLACE(TO_CHAR(FSettleDate, 'MMdd') || to_char(rownum, '000000'), ' ', '') as SBBH, ")
        .append(" to_char(FSettleDate,'yyyyMMdd') as HFRQ, ")
        .append(" '002' as HFLB, ")
        .append(" case ")
        .append(" when FCapitalType = '205' and FCommandMoney > 0 then ")
        .append(" 'F' ")
        .append(" when FCapitalType = '205' and FCommandMoney < 0 then ")
        .append(" 'S' ")
        .append(" when FCapitalType = '203' and FCommandMoney > 0 then ")
        .append(" 'S' ")
        .append(" when FCapitalType = '203' and FCommandMoney < 0 then ")
        .append(" 'F' ")
        .append(" when FCapitalType = '202' then ")
        .append(" 'S' ")
        .append(" end as HFBZ, ")
        .append(" fstockholdercode as GDDM, ")
        .append(" 'RMB' as HBLB, ")
        .append(" case ")
        .append(" when FCapitalType = '205' then ")
        .append(" abs(FCommandMoney) ")
        .append(" when FCapitalType = '203' then ")
        .append(" abs(FCommandMoney) ")
        .append(" else ")
        .append(" FCommandMoney ")
        .append(" end as HKJE, ")
        .append(" to_char(FBargainDate,'yyyyMMdd') as RQ1, ")
        .append(" to_char(FSettleDate,'yyyyMMdd') as RQ2, ")
        .append(" FFundCode as JJDM, ")
        .append(" FCapitalType as YWDM, ")
        .append(" FDesc as BZ ")
        .append(" from (select ").append(dbl.sqlDate(this.sDate)).append(" as FSettleDate, ")
        .append(" nvl((detail.FTradeAmount / para.FNormScale * ")
        .append(" cashbal.FStandardMoneyMarketValue), ")
        .append(" 0) as FCommandMoney, ")
        .append(" detail.FBuyDate as FBargainDate, ")
        .append(" port.FAssetCode as FFundCode, ")
        .append(" '205' as FCapitalType, ")
        .append(" 'ETF申购赎回现金差额净额' as FDesc, ")
        .append(" detail.FStockHolderCode ")
        .append(" from (select FPortCode, ")
        .append(" FClearCode as FBrokerCode, ")
        .append(" FBargainDate as FBuyDate, ")
        .append(" FStockHolderCode, ")
        .append(" sum(case ")
        .append(" when FTradeTypeCode = '103' and jsmx.FClearMark = ' ' then ")
        .append(" -FTradeAmount ")
        .append(" when FTradeTypeCode = '102' and ")
        .append(" jsmx.FClearMark = '276' then ")
        .append(" FTradeAmount ")
        .append(" end) as FTradeAmount ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface")).append(" jsmx ")
        .append(" where FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and exists ")
        .append(" (select FTradeDate, FTradeTypeCode ")
        .append(" from (select FTradeDate, ")
        .append(" case ")
        .append(" when FSellType = '01' then ")
        .append(" '102' ")
        .append(" else ")
        .append(" '103' ")
        .append(" end as FTradeTypeCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_Ta_Trade"))
        .append(" where FCheckState = 1 ")
        .append(" and FSellType in ('01', '02') ")
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FCashBalanceDate = ")
        .append(dbl.sqlDate(this.sDate)).append(") ta ")
        .append(" where ta.FTradeDate = jsmx.FBargainDate ")
        .append(" and ta.FTradeTypeCode = jsmx.FTradeTypeCode) ")
        .append(" and jsmx.FRecordType = '003' ")
        .append(" group by FClearCode, ")
        .append(" FPortCode, ")
        .append(" FBargainDate, ")
        .append(" FStockHolderCode) detail ")
        .append(" left join (select mm.FStandardMoneyMarketValue, ")
        .append(" mm.FSetCode, ")
        .append(" mm.FDate, ")
        .append(" nn.FSetID, ")
        .append(" kk.FPortCode ")
        .append(" from (select gv.FStandardMoneyMarketValue, ")
        .append(" lpad(gv.FPortCode, 3, '0') as FSetCode, ")
        .append(" FDate ")
        .append(" from ").append(pub.yssGetTableName("Tb_Rep_GuessValue")).append(" gv ")
        .append(" where gv.FDate = (select distinct FTradeDate ")
        .append(" from ").append(pub.yssGetTableName("Tb_Ta_Trade")).append(" ta ")
        .append(" where FCheckState = 1 ")
        .append(" and FCashBalanceDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and ta.FSellType in ('01', '02')) ")
        .append(" and gv.FAcctCode = '9802') mm ")
        .append(" left join (select FSetID, FSetCode, FYear ")
        .append(" from lSetList) nn ")
        .append(" on mm.FSetCode = nn.FSetCode ")
        .append(" and nn.FYear = to_number(to_char(mm.FDate, 'yyyy')) ")
        .append(" left join (select FPortCode, FAssetCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
        .append(" where FCheckState = 1) kk ")
        .append(" on kk.FAssetCode = nn.FSetID) cashbal ")
        .append(" on cashbal.FPortCode = detail.FPortCode ")
        .append(" left join (select FAssetCode, FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
        .append(" where FCheckState = 1) port ")
        .append(" on port.FPortCode = detail.FPortCode ")
        .append(" left join (select etfp.FNormScale, etfp.FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_Param")).append(" etfp ")
        .append(" where FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FCheckState = 1) para ")
        .append(" on para.FPortCode = detail.FPortCode ")
        .append(" union all ")
        .append(" select ").append(dbl.sqlDate(this.sDate)).append(" as FSettleDate, ")
        .append(" nvl(a.FTradeAmount / para.FNormScale * m.FSumReturn, 0) as FCommandMoney, ")
        .append(" a.FBuydate as FBargainDate, ")
        .append(" port.FAssetCode as FFundCode, ")
        .append(" '203' as FCapitalType, ")
        .append(" 'ETF申购赎回现金替代退补' as FDesc, ")
        .append(" a.FStockHolderCode ")
        .append(" from (select FClearCode as FBrokerCode, ")
        .append(" FPortCode, ")
        .append(" FBargainDate as FBuyDate, ")
        .append(" FTradeTypeCode, ")
        .append(" FStockHolderCode, ")
        .append(" sum(jsmx.FTradeAmount) as FTradeAmount ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface")).append(" jsmx ")
        .append(" where jsmx.FBargainDate = ")
        .append(" (select distinct FBuyDate ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FRefundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBS = 'B') ")
        .append(" and jsmx.FTradeTypeCode = '102' ")
        .append(" and jsmx.FClearMark = '276' ")
        .append(" and jsmx.FRecordType = '003' ")
        .append(" group by FSettleDate, ")
        .append(" FPortCode, ")
        .append(" FClearCode, ")
        .append(" FBargainDate, ")
        .append(" FTradeTypeCode, ")
        .append(" FStockHolderCode) a ")
        .append(" left join (select FAssetCode, FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
        .append(" where FcheckState = 1) port ")
        .append(" on port.FPortCode = a.FPortCode ")
        .append(" left join (select etfp.FNormScale, etfp.FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_Param")).append(" etfp ")
        .append(" where FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FCheckState = 1) para ")
        .append(" on para.FPortCode = a.FPortCode ")
        .append(" left join (select a1.FSumReturn, ")
        .append(" a1.FDate, ")
        .append(" a1.FPortCode, ")
        .append(" a1.FStockHolderCode ")
        .append(" from (select sum(FSumReturn) as FSumReturn, ")
        .append(" fDate, ")
        .append(" FPortCode, ")
        .append(" FStockHolderCode ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FReFundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBs = 'B' ")
        .append(" and FStockHolderCode <> ' ' ")
        .append(" group by FDate, ")
        .append(" FPortCode, ")
        .append(" FStockHolderCode, ")
        .append(" FReFundDate) a1 ")
        .append(" join (select max(FDate) as FDate, ")
        .append(" FPortCode, ")
        .append(" FStockHolderCode ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FReFundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBs = 'B' ")
        .append(" and FStockHolderCode <> ' ' ")
        .append(" group by FPortCode, ")
        .append(" FStockHolderCode, ")
        .append(" FReFundDate) a2 ")
        .append(" on a1.fDate = a2.fDate ")
        .append(" and a1.FPortCode = a2.FPortCode ")
        .append(" and a1.FStockHolderCode = a2.FStockHolderCode) m ")
        .append(" on m.FPortCode = a.FPortCode ")
        .append(" and m.FStockHolderCode = a.FStockHolderCode ")
        .append(" where a.FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and a.FTradeTypeCode = '102' ")
        .append(" and a.FBrokerCode <> ' ' ")
        .append(" union all ")
        .append(" select ").append(dbl.sqlDate(this.sDate)).append(" as FSettleDate, ")
        .append(" nvl(a.FTradeAmount / para.FNormScale * m.FSumReturn, 0) + ")
        .append(" nvl(a.FTradeAmount / para.FNormScale * n.FReplaceMoney, 0) as FCommandMoney, ")
        .append(" a.FBuyDate as FBargainDate, ")
        .append(" port.FAssetCode as FFundCode, ")
        .append(" '202' as FCapitalType, ")
        .append(" 'ETF赎回现金替代' as FDesc, ")
        .append(" a.FStockHolderCode ")
        .append(" from (select ").append(dbl.sqlDate(this.sDate)).append(" as FRefundDate, ")
        .append(" FClearCode as FBrokerCode, ")
        .append(" FPortCode, ")
        .append(" FBargainDate as FBuydate, ")
        .append(" FTradeTypeCode, ")
        .append(" FStockHolderCode, ")
        .append(" sum(jsmx.FTradeAmount) as FTradeAmount ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface")).append(" jsmx ")
        .append(" where jsmx.FBargainDate = ")
        .append(" (select distinct FBuyDate ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FReFundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBS = 'S') ")
        .append(" and jsmx.ftradetypecode = '103' ")
        .append(" and jsmx.fclearmark = ' ' ")
        .append(" and jsmx.frecordtype = '003' ")
        .append(" group by FSettleDate, ")
        .append(" FPortCode, ")
        .append(" FClearCode, ")
        .append(" FBargainDate, ")
        .append(" FTradeTypeCode, ")
        .append(" FStockHolderCode) a ")
        .append(" left join (select FAssetCode, FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
        .append(" where FCheckState = 1) port ")
        .append(" on port.FPortCode = a.FPortCode ")
        .append(" left join (select etfp.FNormScale, etfp.FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_Param")).append(" etfp ")
        .append(" where FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FCheckState = 1) para ")
        .append(" on para.FPortCode = a.FPortCode ")
        .append(" left join (select a1.FSumReturn, ")
        .append(" a1.fDate, ")
        .append(" a1.FPortCode, ")
        .append(" a1.FStockHolderCode ")
        .append(" from (select sum(FSumReturn * -1) as FSumReturn, ")
        .append(" fDate, ")
        .append(" FPortCode, ")
        .append(" FStockHolderCode ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FReFundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBS = 'S' ")
        .append(" and FStockHolderCode <> ' ' ")
        .append(" group by fDate, FPortCode, FStockHolderCode) a1 ")
        .append(" join (select max(FDate) as FDate, ")
        .append(" FPortCode, ")
        .append(" FStockHolderCode ")
        .append(" from ").append(pub.yssGetTableName("TB_ETF_StandingBook"))
        .append(" where FReFundDate = ").append(dbl.sqlDate(this.sDate))
        .append(" and FPortCode = ").append(dbl.sqlString(portCode))
        .append(" and FBS = 'S' ")
        .append(" and FStockHolderCode <> ' ' ")
        .append(" group by FPortCode, FStockHolderCode) a2 ")
        .append(" on a1.fDate = a2.fDate ")
        .append(" and a1.FPortCode = a2.FPortCode ")
        .append(" and a1.FStockHolderCode = a2.FStockHolderCode) m ")
        .append(" on m.FPortCode = a.FPortCode ")
        .append(" and m.FStockHolderCode = a.FStockHolderCode ")
        .append(" left join (select FReplaceMoney, FPortCode ")
        .append(" from ").append(pub.yssGetTableName("Tb_ETF_StockList"))
        .append(" where FDate = ")
        .append(" (select FTradeDate ")
        .append(" from ").append(pub.yssGetTableName("Tb_Ta_Trade"))
        .append(" where FSellType = '02' ")
        .append(" and FCashReplaceDate = ").append(dbl.sqlDate(this.sDate)).append(") ")
        .append(" and FReplaceMark = '6') n ")
        .append(" on n.FPortCode = a.FPortCode) ");
    	
    	return strBf.toString();
    }
}
