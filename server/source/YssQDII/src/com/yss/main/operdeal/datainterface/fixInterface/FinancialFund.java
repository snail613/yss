package com.yss.main.operdeal.datainterface.fixInterface;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.report.GuessValue;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssUtil;
import com.yss.vsub.YssFinance;

/**shashijie 2013-7-3 STORY 4050 需求北京-(工银瑞信)QDIIV4.0(高)20130607003 
 * 导出财务估值表,固定数据源 */
public class FinancialFund extends DataBase {
    
    public FinancialFund() {
    }
    
    /**shashijie 2013-7-3 STORY 4050 需求北京-(工银瑞信)QDIIV4.0(高)20130607003  入口方法 */
    public void inertData() throws YssException {
    	//创建临时表,若有则先删除表
        doCreaterTMPTable();
        //获取插入集合
        ArrayList list = getInsertData(this.sPort, this.sDate);
		//插入临时表
		insertData(list);
    }
    
    /**shashijie 2013-7-5 STORY 4050 插入临时表*/
	private void insertData(ArrayList list) throws YssException {
		//获取插入SQL
		String query = getInsertQuery();
		PreparedStatement ps = null;
		/*boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();*/
		
		try {
			//bTrans = true;
			ps = dbl.openPreparedStatement(query);
			for (int i = 0; i < list.size(); i++) {
				HashMap map = (HashMap) list.get(i);
				//批量属性赋值
				realInsertDataIntoTem(ps,
					map.get("FAcctCode") + "",//科目代码
					map.get("FAcctName") + "",//科目名称
					map.get("FCurCode") + "",//币种
		            map.get("FExchangeRate") + "",//汇率
		            map.get("FAmount") + "",//数量
		            map.get("FUnitCost") + "",//单位成本
		            map.get("FCost") + "",//原币成本
		            map.get("FStandardMoneyCost") + "",//本位币成本
		            map.get("FCostToNetRatio") + "",//成本占净值比
		            map.get("FMarketPrice") + "",//行情价格
		            map.get("FOTPrice1") + "",//其他行情1(即期价格)
		            map.get("FMarketValue") + "",//原币市值
		            map.get("FStandardMoneyMarketValue") + "",//本位币市值
		            map.get("FMarketValueToRatio") + "",//市值占净值比
		    		map.get("FAppreciation") + "",//估值增值
					map.get("FStandardMoneyAppreciation") + "",//本位币估值增值
					map.get("FMarketDescribe") + "",//行情描述
					map.get("FDesc") + "",//描述
					map.get("FOrderByCode") + ""//排序字段
					);
				ps.addBatch();
            }
            
            /*conn.setAutoCommit(false);
            bTrans = true;*/
            ps.executeBatch();
            /*conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);*/
		} catch (Exception e) {
			throw new YssException("\r\n", e);
		} finally {
			dbl.closeStatementFinal(ps);//关闭游标
			//dbl.endTransFinal(conn, bTrans);
		}
		
	}

	/**shashijie 2013-7-5 STORY 4050 获取插入SQL*/
	private String getInsertQuery() {
		StringBuffer buff = new StringBuffer();
        buff.append(" Insert Into tmp_financialfund ( ");
        buff.append(" FAcctCode,");//科目代码
        buff.append(" FAcctName,");//科目名称
        buff.append(" FCurCode,");//币种
        buff.append(" FExchangeRate,");//汇率
        buff.append(" FAmount,");//数量
        buff.append(" FUnitCost,");//单位成本
        buff.append(" FCost,");//原币成本
        buff.append(" FStandardMoneyCost,");//本位币成本
        buff.append(" FCostToNetRatio,");//成本占净值比
        buff.append(" FMarketPrice,");//行情价格
        buff.append(" FOTPrice1,");//其他行情1(即期价格)
        buff.append(" FMarketValue,");//原币市值
        buff.append(" FStandardMoneyMarketValue,");//本位币市值
        buff.append(" FMarketValueToRatio,");//市值占净值比
		buff.append(" FAppreciation,");//估值增值
		buff.append(" FStandardMoneyAppreciation,");//本位币估值增值
		buff.append(" FMarketDescribe,");//行情描述
		buff.append(" FDesc , ");//描述
		buff.append(" FOrderByCode ");//排序字段
		
        buff.append(" ) Values ( ?,?,?,?,?,?,?,?,?,?," +//10
        		"?,?,?,?,?,?,?,?,? ) ");
		return buff.toString();
	}

	/**shashijie 2013-7-4 STORY 4050 获取插入集合*/
	private ArrayList getInsertData(String sPortCode, Date sDate) throws YssException  {
		ArrayList list = new ArrayList();
		
		//获取查询财务估值表参数
		String parameter = getParameter(sPortCode,sDate);
		
		//获取查询财务估值表数据
		GuessValue guess = new GuessValue();
		guess.setYssPub(pub);
		String data = guess.getGuessValueReport(parameter);
		//解析字符串结果转换成集合
		getGuessValue(data,list);
		return list;
	}

	/**shashijie 2013-7-4 STORY 4050 解析字符串结果转换成集合*/
	private void getGuessValue(String data,ArrayList list) {
		if (list == null || YssUtil.isNullOrEmpty(data)) {
			return;
		}
		
		String[] rows = data.split("\r\n");
		//行
		for (int i = 0; i < rows.length; i++) {
			//列
			String[] cols = rows[i].split("\t");
			HashMap map = new HashMap();
			if (YssUtil.isNullOrEmpty(cols[0]) && cols.length < 19) {
				continue;
			}
			map.put("FAcctCode",getValueString(cols[1]));//科目代码
			map.put("FAcctName",getValueString(cols[2]));//科目名称
			map.put("FCurCode",getValueString(cols[3]));//币种
            map.put("FExchangeRate",getValueString(cols[4]));//汇率
            map.put("FAmount",getValueString(cols[5]));//数量
            map.put("FUnitCost",getValueString(cols[6]));//单位成本
            map.put("FCost",getValueString(cols[7]));//原币成本
            map.put("FStandardMoneyCost",getValueString(cols[8]));//本位币成本
            map.put("FCostToNetRatio",getValueString(cols[9]));//成本占净值比
            map.put("FMarketPrice",getValueString(cols[10]));//行情价格
            map.put("FOTPrice1",getValueString(cols[11]));//其他行情1(即期价格)
            map.put("FMarketValue",getValueString(cols[12]));//原币市值
            map.put("FStandardMoneyMarketValue",getValueString(cols[13]));//本位币市值
            map.put("FMarketValueToRatio",getValueString(cols[14]));//市值占净值比
    		map.put("FAppreciation",getValueString(cols[15]));//估值增值
			map.put("FStandardMoneyAppreciation",getValueString(cols[16]));//本位币估值增值
			map.put("FMarketDescribe",getValueString(cols[17]));//行情描述
			map.put("FDesc",getValueString(cols[18]));//描述
			map.put("FOrderByCode", YssFun.formatNumber(i,"0000") + "##");//排序字段
			
			list.add(map);
		}
		
	}

	/**shashijie 2013-7-9 STORY 4050 获取转换后的String参数,若为空则返回" "空格 */
	private Object getValueString(String param) {
		String value = "";
		if (YssUtil.isNullOrEmpty(param)) {
			value = " ";
		} else {
			value = param;
		}
		return value;
	}

	/**shashijie 2013-7-4 STORY 拼接参数*/
	private String getParameter(String sPortCode, Date sDate) throws YssException {
		String param = "";
		//当前处理日期
		String dDate = YssFun.formatDate(sDate);
		//通过组合代码获取套帐
		YssFinance finance = new YssFinance();
		finance.setYssPub(pub);
		String setId = finance.getBookSetId(sPortCode);
		
		//日期 + 套帐 + 显示级别 + 所有币种
		param = dDate + "\t" + setId + "\t" + "***\tfalse";
		
		return param;
	}

	/**shashijie 2013-7-5 STORY 4050 批量插入*/
	private void realInsertDataIntoTem(PreparedStatement ps,
			String FAcctCode ,//科目代码
			String FAcctName ,//科目名称
			String FCurCode ,//币种
            String FExchangeRate ,//汇率
            String FAmount ,//数量
            String FUnitCost ,//单位成本
            String FCost ,//原币成本
            String FStandardMoneyCost ,//本位币成本
            String FCostToNetRatio ,//成本占净值比
            String FMarketPrice ,//行情价格
            String FOTPrice1 ,//其他行情1(即期价格)
            String FMarketValue ,//原币市值
            String FStandardMoneyMarketValue ,//本位币市值
            String FMarketValueToRatio ,//市值占净值比
    		String FAppreciation ,//估值增值
			String FStandardMoneyAppreciation ,//本位币估值增值
			String FMarketDescribe ,//行情描述
			String FDesc ,//描述
			String FOrderByCode //字段排序
			) throws Exception {
			
		
        ps.setString(1,FAcctCode);//科目代码
        ps.setString(2,FAcctName);//科目名称
        ps.setString(3,FCurCode);//币种
        ps.setString(4,FExchangeRate);//汇率
        ps.setString(5,FAmount);//数量

        ps.setString(6,FUnitCost);//单位成本
        ps.setString(7,FCost);//原币成本
        ps.setString(8,FStandardMoneyCost);//本位币成本
        ps.setString(9,FCostToNetRatio);//成本占净值比
        ps.setString(10,FMarketPrice);//行情价格
        ps.setString(11,FOTPrice1);//其他行情1(即期价格)
        ps.setString(12,FMarketValue);//原币市值
        ps.setString(13,FStandardMoneyMarketValue);//本位币市值
        ps.setString(14,FMarketValueToRatio);//市值占净值比
        ps.setString(15,FAppreciation);//估值增值
        ps.setString(16,FStandardMoneyAppreciation);//本位币估值增值
        ps.setString(17,FMarketDescribe);//行情描述
        ps.setString(18,FDesc);//描述
        ps.setString(19,FOrderByCode);//排序字段

    }

    /**shashijie 2013-7-3 STORY 4050 需求北京-(工银瑞信)QDIIV4.0(高)20130607003 
     * 创建导出临时表tmp_financialfund
     */
    private void doCreaterTMPTable() throws YssException {
		StringBuffer buff = null;
		try {
			buff = new StringBuffer();
			if (dbl.yssTableExist("tmp_financialfund")) {// 如果数据库中已经有表，先删除再创建
				dbl.executeSql(dbl.doOperSqlDrop(" drop table tmp_financialfund "));
			}
            buff.append(" Create Table tmp_financialfund ( ");
            buff.append(" FAcctCode VARCHAR2(50) DEFAULT ' ',");//科目代码
            buff.append(" FAcctName VARCHAR2(50) DEFAULT ' ',");//科目名称
            buff.append(" FCurCode VARCHAR2(50) DEFAULT ' ',");//币种
            buff.append(" FExchangeRate VARCHAR2(50) DEFAULT '0'  ,");//汇率
            buff.append(" FAmount VARCHAR2(50) DEFAULT '0'  ,");//数量
            buff.append(" FUnitCost VARCHAR2(50) DEFAULT '0'  ,");//单位成本
            buff.append(" FCost VARCHAR2(50) DEFAULT '0'  ,");//原币成本
            buff.append(" FStandardMoneyCost VARCHAR2(50) DEFAULT '0'  ,");//本位币成本
            buff.append(" FCostToNetRatio VARCHAR2(50) DEFAULT '0'  ,");//成本占净值比
            buff.append(" FMarketPrice VARCHAR2(50) DEFAULT '0'  ,");//行情价格
            buff.append(" FOTPrice1 VARCHAR2(50) DEFAULT '0'  ,");//其他行情1(即期价格)
            buff.append(" FMarketValue VARCHAR2(50) DEFAULT '0'  ,");//原币市值
            buff.append(" FStandardMoneyMarketValue VARCHAR2(50) DEFAULT '0'  ,");//本位币市值
            buff.append(" FMarketValueToRatio VARCHAR2(50) DEFAULT '0'  ,");//市值占净值比
    		buff.append(" FAppreciation VARCHAR2(50) DEFAULT '0'  ,");//估值增值
			buff.append(" FStandardMoneyAppreciation VARCHAR2(50) DEFAULT '0'  ,");//本位币估值增值
			buff.append(" FMarketDescribe VARCHAR2(50) DEFAULT ' '  ,");//行情描述
			buff.append(" FDesc VARCHAR2(50) DEFAULT ' ' , ");//描述
			buff.append(" FOrderByCode VARCHAR2(50) DEFAULT ' '  ");//排序
            
            buff.append(")");
            dbl.executeSql(buff.toString());
        }catch(Exception e){
            throw new YssException("创建财务估值表数据导出临时表出错！",e);
        }
    }



    
    
    
    
    
}






