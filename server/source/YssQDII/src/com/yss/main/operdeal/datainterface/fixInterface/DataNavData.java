package com.yss.main.operdeal.datainterface.fixInterface;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssUtil;

/**shashijie 2013-7-3 STORY 4050 需求北京-(工银瑞信)QDIIV4.0(高)20130607003 
 * 导出净值统计表,固定数据源 */
public class DataNavData extends DataBase {
    
    public DataNavData() {
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
					getMapValue(map,"FKeycode"),//项目代码
					getMapValue(map,"FKeyname"),//项目名称
			        getMapValue(map,"FCurycode"),//币种
			        getMapValue(map,"FSedolcode"),//外部代码
			        getMapValue(map,"FIsincode"),//Insi 代码
			        getMapValue(map,"FSparamt"),//票面值/股数
			        getMapValue(map,"FPrice"),//行情价格
			        getMapValue(map,"FBasecuryRate"),//基础汇率
			        getMapValue(map,"FPortcuryRate"),//组合汇率
			        getMapValue(map,"FCost"),//原币成本
			        getMapValue(map,"FMarketvalue"),//原币市值
			        getMapValue(map,"FMvvalue"),//原币浮动盈亏
			        getMapValue(map,"FPortcost"),//本位币成本
			        getMapValue(map,"FPortmarketvalue"),//本位币市值
			        getMapValue(map,"FPortmvvalue"),//本位币浮动盈亏
			    	getMapValue(map,"FFxvalue"),//本位币汇兑损益
					getMapValue(map,"FPortmarketvalueratio"),//组合货币市值占净值比例
					getMapValue(map,"FUnitCost"),//原币单位成本
					getMapValue(map,"FChangewithCost"),//原币涨跌
					getMapValue(map,"FPortunitCost"),//本位币单位成本
					getMapValue(map,"FPortchangewithCost"),//本位币涨跌
					getMapValue(map,"FOrdercode")//排序
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

	/**shashijie 2013-7-11 STORY 4050 从map中获取值,若没有key则返回空格不报错*/
	private String getMapValue(HashMap map, String key) {
		String value = " ";
		if (map==null || map.isEmpty()) {
			return value;
		}
		String codeKey = key.toLowerCase();
		if (map.containsKey(codeKey)) {
			try{
				value = (String)map.get(codeKey);
			} catch (Exception e) {
				value = " ";
			}
		}
		return value;
	}

	/**shashijie 2013-7-5 STORY 4050 获取插入SQL*/
	private String getInsertQuery() {
		StringBuffer buff = new StringBuffer();
        buff.append(" Insert Into JINGZHITONGJIBIAO ( ");

        buff.append(" FKeyCode ,");//项目代码
        buff.append(" FKeyName ,");//项目名称
        buff.append(" FCuryCode ,");//币种
        buff.append(" FSedolCode ,");//外部代码
        buff.append(" FISINCode ,");//Insi 代码
        buff.append(" FSParAmt ,");//票面值/股数
        buff.append(" FPrice ,");//行情价格
        buff.append(" FBaseCuryRate ,");//基础汇率
        buff.append(" FPortCuryRate ,");//组合汇率
        buff.append(" FCost ,");//原币成本
        buff.append(" FMarketValue ,");//原币市值
        buff.append(" FMVValue ,");//原币浮动盈亏
        buff.append(" FPortCost ,");//本位币成本
        buff.append(" FPortMarketValue ,");//本位币市值
        buff.append(" FPortMVValue ,");//本位币浮动盈亏
		buff.append(" FFXValue ,");//本位币汇兑损益
		buff.append(" FPortMarketValueRatio ,");//组合货币市值占净值比例
		buff.append(" FUnitCost ,");//原币单位成本
		buff.append(" FChangeWithCost ,");//原币涨跌
		buff.append(" FPortUnitCost ,");//本位币单位成本
		buff.append(" FPortChangeWithCost ,");//本位币涨跌
		buff.append(" FORDERCODE ");//排序
		
        buff.append(" ) Values (" +
        		" ?,?,?,?,?,?,?,?,?,?," +//10
        		" ?,?,?,?,?,?,?,?,?,?," +//20
        		" ?,? ) ");
		return buff.toString();
	}

	/**shashijie 2013-7-4 STORY 4050 获取插入集合*/
	private ArrayList getInsertData(String sPortCode, Date sDate) throws YssException  {
		ArrayList list = new ArrayList();
		
		//获取净值统计表参数传递
		String parameter = getParameter(sPortCode,sDate);
		
		//获取查询财务估值表数据,相当于在净值统计表点查询按钮
		CommonRepBean com = new CommonRepBean();
		com.setYssPub(pub);
		com.parseRowStr(parameter);
		String data = com.getReportData("getsearch");
		
		//解析字符串结果转换成集合
		getDataValue(data,list);
		return list;
	}

	/**shashijie 2013-7-4 STORY 4050 解析字符串结果转换成集合*/
	private void getDataValue(String data,ArrayList list) throws YssException {
		if (list == null || YssUtil.isNullOrEmpty(data)) {
			return;
		}
		
		String[] rows = data.split("\r\n");
		//行
		for (int i = 0; i < rows.length; i++) {
			//列
			String[] cols = rows[i].split("\f\f\r\r");//标题行,如"证券","现金","运营"
			
			//去除格式只取数据值
			for (int j = 0; j < cols.length; j++) {
				HashMap map = new HashMap();
				//单元格数据
				String[] worth = cols[j].split("\t");
				
				for (int k = 0; k < worth.length; k++) {
					//如果是汇总明细字段则不处理
					if (isLegitimacy(worth[k])) {
						continue;
					}
					String[] values = worth[k].split("\n");
					//获取KEY
					String key = values[0].split("~")[2].trim().toLowerCase();
					//获取值
					String value = getValueString(values);
					
					map.put(key, value);
				}
				//存入集合
				if (!map.isEmpty()) {
					//存入排序编号
					map.put("Fordercode".toLowerCase(), YssFun.formatNumber(i,"0000")
							+ "##" + YssFun.formatNumber(j, "0000"));
					list.add(map);
				}
			}
		}
		
	}

	/**shashijie 2013-7-11 STORY 4050 判断数据合法性,排除汇总明细字段*/
	private boolean isLegitimacy(String cols) {
		boolean flag = false;
		
		if (YssUtil.isNullOrEmpty(cols) 
				|| cols.indexOf("FDETAIL") > -1 //汇总明细
				|| cols.indexOf("FDETAILORSUM") > -1 || cols.indexOf("FOTPRICE1") > -1 //其他行情1,2,3
				|| cols.indexOf("FOTPRICE2") > -1 || cols.indexOf("FOTPRICE3") > -1 ) {
			flag = true;
		}
		if (cols.split("\t").length == 1 && cols.split("\t")[0].split("~").length == 1) {
			flag = true;
		}
		return flag;
	}

	/**shashijie 2013-7-9 STORY 4050 获取转换后的String参数,若为空则返回" "空格 */
	private String getValueString(String param[]) {
		String value = "";
		if (param != null && param.length > 1) {
			value = param[1];
			//屏蔽科学计数法,考虑汇率字段
			if (this.isNumeric(value)) {
				value = YssFun.formatNumber(Double.valueOf(value), "#,##0.00##################");
			}
		}
		
		if (YssUtil.isNullOrEmpty(value)) {
			value = " ";
		} 
		return value;
	}

	/**shashijie 2013-7-11 STORY 4050 判断是否是合法数字,并屏蔽科学计数法*/
	private boolean isNumeric(String value) {
		boolean flag = false;
		try {
			double temp = Double.valueOf(value);
			System.out.println(temp);
			flag = true;
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**shashijie 2013-7-4 STORY 拼接参数*/
	private String getParameter(String sPortCode, Date sDate) throws YssException {
		String param = "RepSysNav\f\t1\r";
		//当前处理日期
		String dDate = YssFun.formatDate(sDate);
		
		//日期 + 组合
		param += dDate + "\n2\r"+sPortCode+"\n3\rtrue\n4\rtotal\n6\rtotal\n7\rtotal\f\t\f\tFalse\f\t001\f\tnull";
		
		return param;
	}

	/**shashijie 2013-7-5 STORY 4050 批量插入*/
	private void realInsertDataIntoTem(PreparedStatement ps,
			String FKeyCode ,//项目代码
	        String FKeyName ,//项目名称
	        String FCuryCode ,//币种
	        String FSedolCode ,//外部代码
	        String FISINCode ,//Insi 代码
	        String FSParAmt ,//票面值/股数
	        String FPrice ,//行情价格
	        String FBaseCuryRate ,//基础汇率
	        String FPortCuryRate ,//组合汇率
	        String FCost ,//原币成本
	        String FMarketValue ,//原币市值
	        String FMVValue ,//原币浮动盈亏
	        String FPortCost ,//本位币成本
	        String FPortMarketValue ,//本位币市值
	        String FPortMVValue ,//本位币浮动盈亏
			String FFXValue ,//本位币汇兑损益
			String FPortMarketValueRatio ,//组合货币市值占净值比例
			String FUnitCost ,//原币单位成本
			String FChangeWithCost ,//原币涨跌
			String FPortUnitCost ,//本位币单位成本
			String FPortChangeWithCost ,//本位币涨跌
			String FORDERCODE //排序
			) throws Exception {
			
		
        ps.setString(1,FKeyCode);//项目代码
        ps.setString(2,FKeyName);//项目名称
        ps.setString(3,FCuryCode);//币种
        ps.setString(4,FSedolCode);//外部代码
        ps.setString(5,FISINCode);//Insi 代码
        ps.setString(6,FSParAmt);//票面值/股数
        ps.setString(7,FPrice);//行情价格
        ps.setString(8,FBaseCuryRate);//基础汇率
        ps.setString(9,FPortCuryRate);//组合汇率
        ps.setString(10,FCost);//原币成本
        ps.setString(11,FMarketValue);//原币市值
        ps.setString(12,FMVValue);//原币浮动盈亏
        ps.setString(13,FPortCost);//本位币成本
        ps.setString(14,FPortMarketValue);//本位币市值
        ps.setString(15,FPortMVValue);//本位币浮动盈亏
        ps.setString(16,FFXValue);//本位币汇兑损益
        ps.setString(17,FPortMarketValueRatio);//组合货币市值占净值比例
        ps.setString(18,FUnitCost);//原币单位成本
        ps.setString(19,FChangeWithCost);//原币涨跌
        ps.setString(20,FPortUnitCost);//本位币单位成本
        ps.setString(21,FPortChangeWithCost);//本位币涨跌
        ps.setString(22,FORDERCODE);//排序

    }

    /**shashijie 2013-7-3 STORY 4050 需求北京-(工银瑞信)QDIIV4.0(高)20130607003 
     * 创建导出临时表tmp_financialfund
     */
    private void doCreaterTMPTable() throws YssException {
		StringBuffer buff = null;
		try {
			buff = new StringBuffer();
			if (dbl.yssTableExist("JINGZHITONGJIBIAO")) {// 如果数据库中已经有表，先删除再创建
				dbl.executeSql(dbl.doOperSqlDrop(" drop table JINGZHITONGJIBIAO "));
			}
            buff.append(" Create Table JINGZHITONGJIBIAO ( ");
            buff.append(" FKeyCode VARCHAR2(50) DEFAULT ' ',");//项目代码
            buff.append(" FKeyName VARCHAR2(100) DEFAULT ' ',");//项目名称
            buff.append(" FCuryCode VARCHAR2(50) DEFAULT ' ',");//币种
            buff.append(" FSedolCode VARCHAR2(50) DEFAULT ' '  ,");//外部代码
            buff.append(" FISINCode VARCHAR2(50) DEFAULT ' '  ,");//Insi 代码
            buff.append(" FSParAmt VARCHAR2(50) DEFAULT '0'  ,");//票面值/股数
            buff.append(" FPrice VARCHAR2(50) DEFAULT '0'  ,");//行情价格
            buff.append(" FBaseCuryRate VARCHAR2(50) DEFAULT '0'  ,");//基础汇率
            buff.append(" FPortCuryRate VARCHAR2(50) DEFAULT '0'  ,");//组合汇率
            buff.append(" FCost VARCHAR2(50) DEFAULT '0'  ,");//原币成本
            buff.append(" FMarketValue VARCHAR2(50) DEFAULT '0'  ,");//原币市值
            buff.append(" FMVValue VARCHAR2(50) DEFAULT '0'  ,");//原币浮动盈亏
            buff.append(" FPortCost VARCHAR2(50) DEFAULT '0'  ,");//本位币成本
            buff.append(" FPortMarketValue VARCHAR2(50) DEFAULT '0'  ,");//本位币市值
            buff.append(" FPortMVValue VARCHAR2(50) DEFAULT '0'  ,");//本位币浮动盈亏
    		buff.append(" FFXValue VARCHAR2(50) DEFAULT '0'  ,");//本位币汇兑损益
			buff.append(" FPortMarketValueRatio VARCHAR2(50) DEFAULT '0'  ,");//组合货币市值占净值比例
			buff.append(" FUnitCost VARCHAR2(50) DEFAULT '0'  ,");//原币单位成本
			buff.append(" FChangeWithCost VARCHAR2(50) DEFAULT '0'  ,");//原币涨跌
			buff.append(" FPortUnitCost VARCHAR2(50) DEFAULT '0'  ,");//本位币单位成本
			buff.append(" FPortChangeWithCost VARCHAR2(50) DEFAULT '0'  ,");//本位币涨跌
			buff.append(" FORDERCODE VARCHAR2(100) DEFAULT ' '  ");//排序
            
            buff.append(")");
            dbl.executeSql(buff.toString());
        }catch(Exception e){
            throw new YssException("创建净值表数据导出临时表出错！",e);
        }
    }



    
    
    
    
    
}






