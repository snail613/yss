package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.parasetting.SecurityBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * A+H期权，此类处理行权日当天还有库存时，自动处理放弃行权，冲减库存,主要是产生证券变动数据
 * @author xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
 *
 */
public class OptionsAutoDropRight extends OptionsControlManage{
    private String securityCodes = "";//保存证券代码
    private String analysisCode1 = "";//分析代码1
    private String analysisCode2 = "";//分析代码2
    private String analysisCode3 = "";//分析代码3
	public OptionsAutoDropRight() {
		super();
	}
	 /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate;         //调拨日期
        this.sPortCode = portCode;  //组合
    }

    /**
     * @throws YssException
     */
    public void doOpertion() throws YssException {
    	try{
    		createIntegratedData(); //生成证券变动的数据
    	}catch (Exception e) {
			throw new YssException(e.getMessage());
		}

    }
	private void createIntegratedData() throws YssException {
		ArrayList IntergrateTradeData = null;//保存综合业务数据
		try{
			IntergrateTradeData = getOptionsTradeData();//此方法查询库存数据关联综合业务数据，此综合业务数据为OptionsIntegratedDataManage类中产生的数据
            saveSecurityStorageData(IntergrateTradeData);//保存数据
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	/**
	 * 保存数据到证券变动库 
	 * @param intergrateTradeData
	 * @throws YssException 
	 */
	private void saveSecurityStorageData(ArrayList intergrateTradeData) throws YssException {
		String filterAnalysisCode1 = " ";//分析代码1筛选
        String filterAnalysisCode2 = " ";//分析代码2筛选
        String filterAnalysisCode3 = " ";//分析代码3筛选
        String filterSecurityCode = "";//证券代码筛选
        if (analysisCode1.length() > 0 &&
            analysisCode1.endsWith(",")) {
            filterAnalysisCode1 = this.analysisCode1.substring(0,
                analysisCode1.length() - 1);//去掉最后的“，”号
        }
        if (analysisCode2.length() > 0 &&
            analysisCode2.endsWith(",")) {
            filterAnalysisCode2 = this.analysisCode2.substring(0,
                analysisCode2.length() - 1);//去掉最后的“，”号
        }
        if (analysisCode3.length() > 0 &&
            analysisCode3.endsWith(",")) {
            filterAnalysisCode3 = this.analysisCode3.substring(0,
                analysisCode3.length() - 1);//去掉最后的“，”号
        }
        if (securityCodes.length() > 0 &&
            securityCodes.endsWith(",")) {
            filterSecurityCode = this.securityCodes.substring(0,
                securityCodes.length() - 1);//去掉最后的“，”号
        }
        //此方法为把数据最后插入到综合业务表的方法
        insertData(intergrateTradeData, filterSecurityCode, this.dDate, this.sPortCode, filterAnalysisCode1,
                   filterAnalysisCode2, filterAnalysisCode3);
	}
	/**
     * insertData 此方法为把数据最后插入到综合业务表的方法
     * @param optionsTradeData ArrayList 存放数据的ArrayList
     * @param filterSecurityCode String 证券代码
     * @param date Date 操作日期
     * @param sPortCode String 组合代码
     * @param filterAnalysisCode1 String 分析代码1
     * @param filterAnalysisCode2 String 分析代码2
     * @param filterAnalysisCode3 String 分析代码3
     */
    private void insertData(ArrayList intergrateTradeData, String filterSecurityCode, Date dWorkDay, String sPortCode, String analysisCode1, String analysisCode2,
                            String analysisCode3) throws YssException {
        Connection conn=null;
        boolean bTrans=true;
        try {
            OptionsIntegratedAdmin optIntegrated = new OptionsIntegratedAdmin();//综合业务表的数据库操作类
            optIntegrated.setYssPub(pub);
            conn=dbl.loadConnection();//获取连接
            conn.setAutoCommit(false);//设置为手动打开连接
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Integrated"));//给操作表加锁
            //根据条件删除数据，日期，组合代码，分析代码
            optIntegrated.deleteData(dWorkDay,sPortCode,analysisCode1,analysisCode2,analysisCode3,"AutoDropOpRight");
            //保存数据
            optIntegrated.saveMutliSetting(intergrateTradeData,dWorkDay);
            conn.commit();
            conn.setAutoCommit(true);
            bTrans=false;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }finally{
            dbl.endTransFinal(conn,bTrans);
        }
    }
	/**
	 * 此方法查询库存数据关联综合业务数据，此综合业务数据为OptionsIntegratedDataManage类中产生的数据
	 * @return
	 * @throws YssException 
	 */
	private ArrayList getOptionsTradeData() throws YssException {
		StringBuffer buff = null;
        ArrayList optionsTradeData = null;
        ResultSet rs = null;
        OptionsIntegratedAdmin optIntegrate = null;
        boolean analy1;
        boolean analy2;
        boolean analy3;
        String strYearMonth = "";
        double dDayRealAmount = 0;//库存与当天交易之差
        try{
        	 //分析代码
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");

            optionsTradeData = new ArrayList();
            buff = new StringBuffer();
            
            strYearMonth = YssFun.left(YssFun.formatDate(this.dDate), 4) + "00";//赋值
            
            /**
             * 在行权日自动处理放弃行权，冲减成本，逻辑：昨日库存关联出当日是行权日的期权关联当天的证券变动（即期权交易数据），
             * 如果当天总交易大于昨日库存，即不能执行放弃行权，注：要考虑昨日库存正负，和当天总交易的正负
             */
            buff.append(" select * from ").append(pub.yssGetTableName("tb_stock_security")).append(" a ");//证券库存表
            buff.append(" join (select * from ").append(pub.yssGetTableName("tb_para_optioncontract"));//期权信息设置表
            buff.append(" where FCheckState = 1 and FExpiryDate = ").append(dbl.sqlDate(this.dDate));
            buff.append(" ) op on a.fsecuritycode = op.FOptionCode ");
            buff.append(" left join (select sum(c.FAmount) as FAmount,c.fsecuritycode,c.fportcode ");
            buff.append(analy1 == true ? ",c.fanalysiscode1 " : "");
            buff.append(analy2 == true ? ",c.fanalysiscode2" : "");
            buff.append(analy3 == true ? ",c.fanalysiscode3" : "");
            buff.append(" from ").append(pub.yssGetTableName("tb_data_integrated"));//综合业务表
            buff.append(" c where FCheckState = 1 and FPortCode in( ").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
            buff.append(" and FExchangedate = ").append(dbl.sqlDate(this.dDate)).append(" and c.fnumtype <> 'AutoDropOpRight' ");
            buff.append(" group by c.fsecuritycode,c.fportcode ");
            buff.append(analy1 == true ? ",c.fanalysiscode1 " : "");
            buff.append(analy2 == true ? ",c.fanalysiscode2" : "");
            buff.append(analy3 == true ? ",c.fanalysiscode3" : "");
            buff.append(" ) b on a.fsecuritycode = b.fsecuritycode and a.fportcode = b.fportcode ");
            buff.append(analy1 == true ? " and a.fanalysiscode1 = b.fanalysiscode1 " : "");
            buff.append(analy2 == true ? " and a.fanalysiscode2 = b.fanalysiscode2 " : "");
            buff.append(analy3 == true ? " and a.fanalysiscode3 = b.fanalysiscode3 " : "");
            buff.append(" where a.fstoragedate = ").append(dbl.sqlDate(YssFun.addDay(this.dDate,-1)));
            buff.append(" and a.fyearmonth <> ").append(dbl.sqlString(strYearMonth));
            buff.append(" and a.fportcode in( ").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
            
            rs = dbl.queryByPreparedStatement(buff.toString());
            while (rs.next()) {
            	dDayRealAmount = YssD.add(rs.getDouble("FStorageAmount"),rs.getDouble("FAmount"));
            	if(dDayRealAmount == 0){
            		continue;
            	}
                optIntegrate = setSecIntegrate(rs, analy1, analy2, analy3);//此方法主要是设置综合业务中数据，把数据保存到综合业务的Bean中
                optionsTradeData.add(optIntegrate); //把数据放到一个ArrayList中
            }
        	
        }catch (Exception e) {
			throw new YssException("获取期权行权日自动放弃行权数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return optionsTradeData;
	}
	
	/**
     * 此方法主要是设置综合业务中数据，把数据保存到综合业务的Bean中
     * @param rs ResultSet
     * @param analy1 boolean 分析代码1
     * @param analy2 boolean 分析代码2
     * @param analy3 boolean 分析代码3
     * @return OptionsIntegratedBean
     * @throws YssException
     */
    private OptionsIntegratedAdmin setSecIntegrate(ResultSet rs, boolean analy1,
        boolean analy2, boolean analy3) throws
        YssException {
        OptionsIntegratedAdmin optIntegrate = null;//综合业务表的操作数据库类
        SecurityBean security = null;//证券库存操作类
        double dStorageAmount =0;//昨日库存数量
        double dTradeAmount = 0;//当天交易数量
        
        try {        	
            optIntegrate = new OptionsIntegratedAdmin();
                       
            dStorageAmount = Math.abs(rs.getDouble("FStorageAmount"));
            dTradeAmount = Math.abs(rs.getDouble("FAmount"));

            if(rs.getDouble("FStorageAmount") > 0){
            	optIntegrate.setIInOutType(-1);//流出
            }else{
            	optIntegrate.setIInOutType(1);//流入
            }
            if(dStorageAmount < dTradeAmount){
            	throw new YssException("库存不足，无法放弃行权，操作失败！");
            }else {
        		dTradeAmount = dStorageAmount;            	
            	optIntegrate.setDAmount(YssD.mul(dTradeAmount, optIntegrate.getIInOutType()));//交易数量
            	
            	optIntegrate.setSSecurityCode(rs.getString("FSecurityCode"));//证券代码
                securityCodes += optIntegrate.getSSecurityCode() + ",";
                optIntegrate.setSExchangeDate(YssFun.formatDate(this.dDate, "yyyy-MM-dd"));//操作日期
                optIntegrate.setSOperDate(YssFun.formatDate(this.dDate, "yyyy-MM-dd"));//业务日期
                optIntegrate.setSRelaNum(" ");
                optIntegrate.setSNumType("AutoDropOpRight");//删除条件

                optIntegrate.setSTradeTypeCode("34FP");//交易方式 --放弃行权

                optIntegrate.setSPortCode(rs.getString("FPortCode"));//组合代码
                if (analy1) {
                    optIntegrate.setSAnalysisCode1(rs.getString("fanalysiscode1"));//投资经理
                } else {
                    optIntegrate.setSAnalysisCode1(" ");
                }
                analysisCode1 += optIntegrate.getSAnalysisCode1() + ",";
                if (analy2) {
                    optIntegrate.setSAnalysisCode2(rs.getString("fanalysiscode2"));//券商
                } else {
                    optIntegrate.setSAnalysisCode2(" ");
                }
                analysisCode2 += optIntegrate.getSAnalysisCode2() + ",";
                if (analy3) {
                    optIntegrate.setSAnalysisCode3(" ");//分析代码3
                } else {
                    optIntegrate.setSAnalysisCode3(" ");
                }
                analysisCode3 += optIntegrate.getSAnalysisCode3() + ",";

                //设置原币成本，移动加权算法 = 昨日库存成本/库存数量*交易数量 * 方向
                optIntegrate.setDCost(YssD.round(YssD.mul(
                		dTradeAmount,
                				YssD.div(rs.getDouble("FStorageCost"),rs.getDouble("FStorageAmount")), 
                				optIntegrate.getIInOutType()),2));
                optIntegrate.setDMCost(optIntegrate.getDCost());
                optIntegrate.setDVCost(optIntegrate.getDCost());
                //----------------

                security = new SecurityBean();
                security.setYssPub(pub);
                security.setSecurityCode(optIntegrate.getSSecurityCode());
                security.getSetting();

                optIntegrate.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
                optIntegrate.setDPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
                //设置基础货币成本，移动加权算法=昨日基础货币成本/库存数量*交易数量*方向
                optIntegrate.setDBaseCost(YssD.round(YssD.mul(
                		dTradeAmount,
        				YssD.div(rs.getDouble("FBaseCuryCost"),rs.getDouble("FStorageAmount")), 
        				optIntegrate.getIInOutType()),2));
                optIntegrate.setDMBaseCost(optIntegrate.getDBaseCost());
                optIntegrate.setDVBaseCost(optIntegrate.getDBaseCost());
                //-----------------------

                //设置组合货币成本，移动加权算法=昨日组合货币成本/库存数量*交易数量 * 方向
                optIntegrate.setDPortCost(YssD.round(YssD.mul(
                		dTradeAmount,
        				YssD.div(rs.getDouble("FPortCuryCost"),rs.getDouble("FStorageAmount")), 
        				optIntegrate.getIInOutType()),2));
                optIntegrate.setDMPortCost(optIntegrate.getDPortCost());
                optIntegrate.setDVPortCost(optIntegrate.getDPortCost());
                //--------------------

                optIntegrate.checkStateId = 1;
            }
        } catch (Exception e) {
            throw new YssException("设置综合业务数据出错！\r\t", e);
        }
        return optIntegrate;
    }
}
















