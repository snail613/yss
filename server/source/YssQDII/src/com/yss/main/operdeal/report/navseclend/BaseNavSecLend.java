package com.yss.main.operdeal.report.navseclend;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.base.BaseAPOperValue;
import com.yss.main.operdeal.report.navseclend.pojo.NavSecLendBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class BaseNavSecLend extends BaseAPOperValue {

	protected Date sLendDate;			//借贷日期
	protected String sPortCode = "";	//组合代码
	protected String sBrokerCode = "";	//券商代码
	protected String sPledgeCode = "";	//抵押物代码
	protected boolean isCreate = true;	//是否生成净值数据 	默认true为生成
	protected String sRepType = "";		//处理报表为借入或借出	-1为借入，1为借出
	protected String valDefine = ""; //分级字段
    protected String[] fields = null; //group条件的字段
    HashMap hm = new HashMap();	//存放各级累加值
    
	public void init(Object bean) throws YssException {
		String reqAry[] = null;
		String reqAry1[] = null;
		String sRowStr = (String) bean;
		if (sRowStr.trim().length() == 0) {
			return;
		}
		reqAry = sRowStr.split("\n");
		for (int i = 0; i < reqAry.length; i++) {
			reqAry1 = reqAry[i].split("\r");
			if(reqAry1[1] != null && reqAry1[1].length() > 0) {
				if(reqAry1[0].equalsIgnoreCase("1")) this.sLendDate = YssFun.toDate(reqAry1[1]);
				else if(reqAry1[0].equalsIgnoreCase("2")) this.sPortCode = reqAry1[1];
				else if(reqAry1[0].equalsIgnoreCase("3")) this.sBrokerCode = reqAry1[1];
				else if(reqAry1[0].equalsIgnoreCase("4")) this.sPledgeCode = reqAry1[1];
			}
		}
		initRep();
    }
	
	protected void initRep() {
		//初始化报表所需数据
	}
	
	public Object invokeOperMothed() throws YssException {
		ArrayList valueMap = null;
		createTempTb();
		valueMap = new ArrayList();
		try {
	         if(isCreate) {
	      	  	deleteDataFromTempTb();//删除原有数据
				buildNavData(valueMap);//根据条件生成净值表数据
				dealNavData(valueMap);//处理已生成的数据（包括资产净值、估值增值和占净值比例的计算）
	         }
		} catch (YssException ex) {
			throw new YssException(ex.getMessage());
		}
		return "";
	}
	
	/**
	 * 通过条件删除原有数据
	 * @方法名：deleteDataFromTempTb
	 * @返回类型：void
	 */
	private void deleteDataFromTempTb() throws YssException {
		String sqlStr = "delete from " + pub.yssGetTableName("tb_Data_NavSecLend") +
						" where FPortCode = " + dbl.sqlString(this.sPortCode) +
						" and FNavDate = " + dbl.sqlDate(this.sLendDate) +
						" and FSecLendType = " + dbl.sqlString(this.sRepType);
						//((null != this.sBrokerCode && this.sBrokerCode.length() > 0) ? (" and FBrokerCode = " + dbl.sqlString(this.sBrokerCode)) : "");
		try {
			dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("删除借入证券净值数据出错！" + e.getMessage());
		}
	}
	
	/**
	 * 创建证券借贷净值表
	 * @方法名：createTempTb
	 * @返回类型：void
	 */
	private void createTempTb() throws YssException {
		String strSql = "";
		try {
			if(dbl.yssTableExist(pub.yssGetTableName("tb_Data_NavSecLend"))) {
				return;
			} else {
				strSql = "create table " + pub.yssGetTableName("tb_Data_NavSecLend") + "(" +
						  "FNAVDATE              DATE not null," +	//1净值表日期
						  "FPORTCODE             VARCHAR2(20) not null," +	//2组合代码
						  "FSecLendType          VARCHAR2(10) not null," +	//3借入借出标志  -1为借入，1为借出
						  "FORDERCODE            VARCHAR2(200) not null," +	//4排序代码
						  "FINOUT                NUMBER(1) default 1 not null," +	//5计算资产净值 +应收-应付标志	1为加，-1为减
						  "FRETYPECODE           VARCHAR2(20)," +	//6类型代码
						  "FBrokerCODE           VARCHAR2(20)," +	//7券商
						  //-------------一下为净值表显示字段---------------
						  "FDETAIL               NUMBER(1) not null," +		//8净值表第一列展开序列
						  "FKEYCODE              VARCHAR2(50) not null," +	//9项目代码
						  "FKEYNAME              VARCHAR2(200) not null," +	//10项目名称
						  "FCURYCODE             VARCHAR2(20)," +	//11币种
						  "FSEDOLCODE            VARCHAR2(50)," +			//12外部代码
						  "FISINCODE             VARCHAR2(50)," +			//13INSI代码
						  "FSPARAMT              NUMBER(20,4)," +			//14票面值/股数
						  "FPRICE                NUMBER(20,12)," +			//15行情价格
						  //----净值表中已隐藏 其它行情1、其它行情2、其它行情3
						  "FOTPRICE1             NUMBER(20,12)," +			//16其它行情1
						  "FOTPRICE2             NUMBER(20,12)," +			//17其它行情2
						  "FOTPRICE3             NUMBER(20,12)," +			//18其它行情3
						  //-----
						  "FBASECURYRATE         NUMBER(20,15) not null," +	//19基础汇率
						  "FPORTCURYRATE         NUMBER(20,15) not null," +	//20组合汇率
						  "FCOST                 NUMBER(18,4) default 0 not null," +	//21原币成本
						  "FMARKETVALUE          NUMBER(18,4) default 0 not null," +	//22原币市值
						  "FMVVALUE              NUMBER(18,4) default 0 not null," +	//23原币浮动盈亏
						  "FPORTCOST             NUMBER(18,4) default 0 not null," +	//24本位币成本
						  "FPORTMARKETVALUE      NUMBER(18,4) default 0 not null," +	//25本位币市值
						  "FPORTMVVALUE          NUMBER(18,4) default 0 not null," +	//26本位币浮动盈亏
						  "FFXVALUE              NUMBER(18,4) default 0 not null," +	//27本位币汇兑损益
						  "FPORTMARKETVALUERATIO NUMBER(18,4)," +			//28占净值比例
						  "FUNITCOST             NUMBER(18,4)," +			//29原币单位成本
						  "FCHANGEWITHCOST       NUMBER(18,4)," +			//30原币涨跌
						  "FPORTUNITCOST         NUMBER(18,4)," +			//31组合货币单位成本
						  "FPORTCHANGEWITHCOST   NUMBER(18,4)," +			//32组合货币涨跌
						  //---------------以上为净值表显示字段---------------
						  "FGRADETYPE1           VARCHAR2(20)," +			//33第一层代码		证券品种
						  "FGRADETYPE2           VARCHAR2(20)," +			//34第二层代码		品种子类型
						  "FGRADETYPE3           VARCHAR2(20)," +			//35第三层代码		币种
						  "FGRADETYPE4           VARCHAR2(20)," +			//36第四层代码		证券代码
						  "FGRADETYPE5           VARCHAR2(20)," +			//37第五层代码		证券借贷子类型代码
						  "FGRADETYPE6           VARCHAR2(20)" +			//38第六层代码		
						  ")";
				dbl.executeSql(strSql);
			}
		} catch (Exception e) {
			throw new YssException("创建证券借贷净值表出错！" + e.getMessage());
		}
	}
	
	/**
	 * 将查出的数据插入借贷净值表
	 * @方法名：insertToTempTb
	 * @参数：valueMap HashMap
	 * @返回类型：void
	 */
	protected void insertToTempTb(ArrayList valueMap) throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		NavSecLendBean secLend = null;
		PreparedStatement ptmt = null;
		String sFields = "FNAVDATE, FPORTCODE, FSecLendType, FORDERCODE, FINOUT, FRETYPECODE, FBrokerCODE, FDETAIL, " +
						" FKEYCODE, FKEYNAME, FCURYCODE, FSEDOLCODE, FISINCODE, FSPARAMT, FPRICE, FOTPRICE1, FOTPRICE2, FOTPRICE3, " +
						" FBASECURYRATE, FPORTCURYRATE, FCOST, FMARKETVALUE, FMVVALUE, FPORTCOST, FPORTMARKETVALUE, FPORTMVVALUE, FFXVALUE, " +
						" FUNITCOST, FCHANGEWITHCOST, FPORTUNITCOST, FPORTCHANGEWITHCOST, " +
						" FGRADETYPE1, FGRADETYPE2, FGRADETYPE3, FGRADETYPE4, FGRADETYPE5, FGRADETYPE6";
		String sqlStr = "insert into " + pub.yssGetTableName("tb_Data_NavSecLend") +
							" (" + sFields + ")" +
							" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			ptmt = dbl.openPreparedStatement(sqlStr);
			for (int i = 0; i < valueMap.size(); i++) {
				secLend = (NavSecLendBean) valueMap.get(i);
				
				ptmt.setDate(1, YssFun.toSqlDate(secLend.getNavDate()));
                ptmt.setString(2, secLend.getPortCode());
                ptmt.setString(3, secLend.getSecLendType());
                ptmt.setString(4, secLend.getOrderKeyCode());
                ptmt.setDouble(5, secLend.getInOut());
                ptmt.setString(6, secLend.getReTypeCode());
                ptmt.setString(7, secLend.getInvMgrCode());
                ptmt.setDouble(8, secLend.getDetail());
                ptmt.setString(9, secLend.getKeyCode());
                ptmt.setString(10, secLend.getKeyName());
                ptmt.setString(11, secLend.getCuryCode());
                ptmt.setString(12, secLend.getSedolCode());
                ptmt.setString(13, secLend.getIsinCode());
                ptmt.setDouble(14, YssD.round(secLend.getSparAmt(), 4));
                ptmt.setDouble(15, YssD.round(secLend.getPrice(), 12));
                ptmt.setDouble(16, YssD.round(secLend.getOtPrice1(), 12));
                ptmt.setDouble(17, YssD.round(secLend.getOtPrice2(), 12));
                ptmt.setDouble(18, YssD.round(secLend.getOtPrice3(), 12));
                ptmt.setDouble(19, secLend.getBaseCuryRate());
                ptmt.setDouble(20, secLend.getPortCuryRate());
                ptmt.setDouble(21, YssD.round(secLend.getBookCost(), 4));
                ptmt.setDouble(22, YssD.round(secLend.getMarketValue(), 4));
                ptmt.setDouble(23, YssD.round(secLend.getPayValue(), 4));
                ptmt.setDouble(24, YssD.round(secLend.getPortBookCost(), 4));
                ptmt.setDouble(25, YssD.round(secLend.getPortMarketValue(), 4));
                ptmt.setDouble(26, YssD.round(secLend.getPortPayValue(), 4));
                ptmt.setDouble(27, YssD.round(secLend.getPortexchangeValue(), 4));
                
                ptmt.setDouble(28,secLend.getUnitCost());//原币单位成本
                ptmt.setDouble(29,secLend.getChangeWithCost());//原币涨跌
                ptmt.setDouble(30,secLend.getPortUnitCost());//组合货币单位成本
                ptmt.setDouble(31,secLend.getPortChangeWithCost());//组合货币涨跌
                ptmt.setString(32, secLend.getGradeType1());
                ptmt.setString(33, secLend.getGradeType2());
                ptmt.setString(34, secLend.getGradeType3());
                ptmt.setString(35, secLend.getGradeType4());
                ptmt.setString(36, secLend.getGradeType5());
                ptmt.setString(37, secLend.getGradeType6());
				
                ptmt.executeUpdate();
			}
			valueMap.clear();//插入成功后清空HashMap
		} catch (Exception e) {
			throw new YssException("插入证券借贷净值数据出错！" + e.getMessage());
		} finally {
			dbl.closeStatementFinal(ptmt);
		}
	}
	
	protected ArrayList getRepData() throws YssException {
        fields = this.valDefine.split(";");
        String groupStr = "";
        ArrayList allData = new ArrayList();
        if (fields.length > 0) {
            for (int Grade = fields.length; Grade > 0; Grade--) {
                groupStr = buildGroupStr(fields, Grade);
                allData.addAll(getGradeData(groupStr, Grade)); //获取各个分级的数据，放入一个总的ArrayList
            }
        }
        return allData;
    }

	/**
	 * 获取各个分级的数据
	 * @方法名：getGradeData
	 * @参数：groupStr String
	 * @参数：Grade groupStr
	 * @返回类型：ArrayList
	 */
    protected ArrayList getGradeData(String groupStr, int Grade) throws YssException {
        return null;
    }
	
    /**
     * 组装排序列字段
     * @方法名：buildGroupStr
     * @参数：fields String[]
     * @参数：grades int
     * @返回类型：String
     */
	protected String buildGroupStr(String[] fields, int grades) throws YssException {
	    String reStr = "";
	    try {
	        for (int i = 0; i < grades; i++) {
	            reStr = reStr + fields[i] + ",";
	        }
	        if (reStr.length() > 0) {
	            reStr = reStr.substring(0, reStr.length() - 1);
	        }
	        return reStr;
	    } catch (Exception e) {
	        throw new YssException("设置group字段数据出错!+\n");
	    }
	}
	
	/**
	 * 生成证券借贷净值表数据插入借贷净值表
	 * @方法名：buildNavData
	 * @参数：HashMap valueMap
	 * @返回类型：void
	 */
	public void buildNavData(ArrayList valueMap) throws YssException {
		//子类重写该方法生成证券借贷净值表数据
		insertToTempTb(valueMap);
	}
	
	public void dealNavData(ArrayList valueMap) throws YssException {
		//子类重写该方法处理已生成的数据（包括资产净值、估值增值和占净值比例的计算）
		insertToTempTb(valueMap);
	}
	
	/**
	 * 组装排序列字段
	 * @方法名：buildOrderStr
	 * @参数：OrderFields String
	 * @返回类型：String
	 */
	protected String buildOrderStr(String OrderFields) throws YssException {
        String reStr = "";
        /**shashijie 2012-7-2 STORY 2475 */
        String[] fieldes = null;
        /**end*/
        try {
            if (OrderFields.length() > 0) {
                fieldes = OrderFields.split(",");
                if (fieldes != null) {
                    for (int i = 0; i < fieldes.length; i++) {
                        reStr += fieldes[i] + dbl.sqlJoinString().trim() + dbl.sqlString("##") + dbl.sqlJoinString().trim();
                    }
                    if (reStr.length() > 0) {
                        reStr = reStr.substring(0, reStr.length() - 8);
                    }
                }
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("Order数据出错!\n");
        }
    }
	
	/**
	 * 生成净值表中项目名称
	 * @方法名：setBlo
	 * @参数：Grade int
	 * @返回类型：String
	 */
	protected String setBlo(int Grade) throws YssException {
        String reStr = "";
        String initStr = "  "; //两个空格
        try {
            for (int i = 0; i < Grade; i++) {
                reStr += initStr;
            }
            if (reStr.length() > 0) {
                reStr = "." + reStr;
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e);
        }
    }
	
	protected String getKey(String sOrder, int kl) {
		String[] tmp = sOrder.split("##");
		String reStr = "";
		for (int i = 0; i < kl; i++) {
			reStr += tmp[i];
		}
		return reStr;
	}
}
