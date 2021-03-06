package com.yss.main.operdeal.datainterface.etf;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssD;
import com.yss.util.YssException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.regex.Pattern;

import com.yss.util.YssFun;

/**shashijie 2011-11-01 STORY 1434  ETF易方达
 * ETF基金接口导入股票篮数据，旧格式的股票篮文件</p>
 */
public class ETFStockListEFDBean
    extends DataBase {
    public ETFStockListEFDBean() {
    }
    /**
     * 导入数据的入口方法
     * @throws YssException
     */
    public void inertData() throws YssException {
        Connection con = dbl.loadConnection(); // 新建连接
        boolean bTrans = true;//事务控制标识
        ResultSet rs = null;//结果集声明
        PreparedStatement pst = null; // 声明PreparedStatement
        StringBuffer buff=null;//做拼接SQL语句
        String sTmpData="";//保存从临时表中获取的数据
        String [] sOnlyOneData=null;//保存拆分后的每一条数据
        String [] sSingleField=null;//保存拆分后的每一条数据的每一个字段值
        //String sSecurityCode = "";//证券代码
        String[] sParam = new String[3];//保存参数设置中获取的参数值
        String sBSSwitch = "1";//申购赎回切换标识，取股票篮中CreationRedemption字段值，用于接口导出判断是否需要导出数据文件
        try{
            buff=new StringBuffer();
            con.setAutoCommit(false);
            // 1.删除股票篮表Tb_ETF_StockList相关导入日期和组合代码的数据
            buff.append(" delete from ").append(pub.yssGetTableName("Tb_ETF_StockList"));
            buff.append(" where FDate =").append(dbl.sqlDate(this.sDate));
            buff.append(" and FPortCode in(").append(operSql.sqlCodes(this.sPort)).append(")");

            dbl.executeSql(buff.toString());
            buff.delete(0,buff.length());

            // 2.查询出股票篮临时表tmp_etf_stocklist中的数据
            buff.append(" select * from tmp_etf_stocklist");

            rs=dbl.openResultSet(buff.toString());
            buff.delete(0,buff.length());

            while (rs.next()) {
                if (rs.getString("stocklistfile").indexOf("|") != -1) {
                    sTmpData += rs.getString("stocklistfile") + "\t";//把符合条件的数据先拼接在一起
                }
                if (rs.getString("stocklistfile").indexOf("CreationRedemption=") != -1) {
                	//获取申购赎回切换字段值
                	//0 - 不允许申购/赎回；1 - 申购和赎回皆允许；2 - 仅允许申购；3 - 仅允许赎回
                	sBSSwitch = rs.getString("stocklistfile").split("=").length > 1 ? 
                								rs.getString("stocklistfile").split("=")[1].trim() : "1";
                }
            }

            // 3.向目标表Tb_ETF_StockList插入数据
            buff.append(" insert into ").append(pub.yssGetTableName("Tb_ETF_StockList"));
            buff.append(" (FPortCode,FSecurityCode,FAmount,FReplaceMark,FPremiumScale,FTotalMoney,FDesc,FDate,");
            buff.append(" FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FListedMarket"+
			/**shashijie 2011-12-06 增加"替代金额"不算溢价比例字段的 */
				",FReplaceMoney,FBSSwitch").append(")");
			/**end*/
            buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            pst=dbl.openPreparedStatement(buff.toString());
            buff.delete(0,buff.length());
            dbl.closeResultSetFinal(rs);//关闭结果集
            String[] arrPortCodes = this.sPort.split(","); // 拆分已选组合代码
            for (int i = 0; i < arrPortCodes.length; i++) {//循环组合代码
            	   //获取etf参数设置中的：一级市场代码。二级市场代码，资金代码
    			buff.append(" select * from ").append(pub.yssGetTableName("Tb_ETF_Param"));
    			buff.append(" where FPortCode =").append(dbl.sqlString(arrPortCodes[i]));

    			ResultSet ret = dbl.openResultSet(buff.toString());
    			buff.delete(0,buff.length());
    			if (ret.next()) {
    				sParam[0] = ret.getString("FOneGradeMktCode");// 一级市场代码
    				sParam[1] = ret.getString("FTwoGradeMktCode");// 二级市场代码
    				sParam[2] = ret.getString("FCapitalCode");// 资金代码
    			}
    			dbl.closeResultSetFinal(ret);
                sOnlyOneData=sTmpData.split("\t");//根据条件”\t“拆分成每一条数据
                for (int j = 0; j < sOnlyOneData.length; j++) {//循环数据
                    sSingleField = sOnlyOneData[j].split("[|]");//根据条件”|“拆分每一条数据

                    pst.setString(1, arrPortCodes[i]); //组合代码
                    
                    Pattern p = Pattern.compile("[0-9]*");
                    if(p.matcher(sSingleField[0]).matches()){
                    	sSingleField[0] = String.valueOf(YssFun.toInt(sSingleField[0]));
                    }
                    if(sParam[2].equals(sSingleField[0])){
                    	pst.setString(2, sSingleField[0]); //证券代码
                    }else{
                    	 //下面是给股票篮中的证券代码加上交易所代码
                        buff.append(" select s.fsecuritycode from ").append(pub.yssGetTableName("tb_para_security"));
                        buff.append(" s where s.FCheckState = 1 and substr(s.fsecuritycode,1,length(s.fsecuritycode)-3) = ")
                            .append(dbl.sqlString(sSingleField[0].trim().indexOf(" ")!= -1?sSingleField[0].substring(0,sSingleField[0].length()-3):sSingleField[0].trim()));

                        rs=dbl.openResultSet(buff.toString());
                        buff.delete(0,buff.length());
                        if(rs.next()){
                            pst.setString(2, rs.getString("fsecuritycode")); //证券代码
                        }else{
                            throw new YssException("请检查系统证券信息设置中是否有股票篮中的证券信息【"+sSingleField[0].trim()+"】！");
                        }
                    }
                    pst.setDouble(3, Double.parseDouble(sSingleField[2].trim().length() > 0 ? sSingleField[2] : "0")); //证券数量
                    pst.setString(4, sSingleField[3]); //替代标志
					pst.setDouble(5, Double.parseDouble(sSingleField[4].trim().length() > 0 ? sSingleField[4] : "0")); //溢价比例
                    
                    /**shashijie 2011-11-01 STORY 1434  ETF易方达*/
                    double FTotalMoney = Double.parseDouble(sSingleField[5].trim().length() > 0 ? sSingleField[5] : "0");//总金额
                    //当替代标志等于6时说明不需要补票,不需要乘以溢价比例
                    if (sSingleField[3].equals("6")) {
                    	pst.setDouble(6, FTotalMoney);//总金额
					} else {
						//计算替代金额
						double repMoney = getFTotalMoney(FTotalMoney,Double.parseDouble(sSingleField[4])); 
						pst.setDouble(6, repMoney);//总金额
					}
                    /**end*/

					/**shashijie 2011-12-06 存入不算上溢价比例的替代金额字段*/
					pst.setDouble(15, FTotalMoney);//溢价比例
                    /**end*/

                    pst.setString(7, ""); //描述
                    pst.setDate(8, YssFun.toSqlDate(this.sDate)); //导入日期
                    pst.setInt(9, 1); //审核状态
                    pst.setString(10, pub.getUserCode()); //创建人
                    pst.setString(11, YssFun.formatDatetime(new java.util.Date())); //创建时间
                    pst.setString(12, pub.getUserCode()); //复审人
                    pst.setString(13, YssFun.formatDatetime(new java.util.Date())); //复审时间
                    
                    if(sSingleField.length > 6){
                    	pst.setString(14,sSingleField[7]);//华夏挂牌市场
                    }else{
                    	pst.setString(14,"");
                    }
                    pst.setString(16, sBSSwitch);
               
                    pst.addBatch();
                    dbl.closeResultSetFinal(rs);
                }
                pst.executeBatch();
            }
            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交

        }catch(Exception e){
            throw new YssException("接口导入股票篮数据出错！",e);
        }finally{
            dbl.endTransFinal(con,bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
        }
    }
    
    /**
     * 计算替代金额(总金额  * 溢价比例)
     * @param fTotalMoney 总金额
     * @param parseDouble 溢价比例
     * @return 2011-12-06 更爱保留3位小数
     * @author shashijie ,2011-11-1 , STORY 1434
     * @modified
     */
	private double getFTotalMoney(double fTotalMoney, double parseDouble) {
		double money = 0;
		try {
			money = YssD.round(YssD.mul(fTotalMoney, YssD.add(parseDouble,1)),3);
		} catch (Exception e) {
			money = 0;
		}
		return money;
	}

}








