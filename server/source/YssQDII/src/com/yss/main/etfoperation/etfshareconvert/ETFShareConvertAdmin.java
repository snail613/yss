package com.yss.main.etfoperation.etfshareconvert;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.report.navrep.pojo.NavRepBean;
import com.yss.main.operdeal.stgstat.StgTAStorage;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import java.util.ArrayList;

/**
 * <p>Title:xuqiji 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A </p>
 *
 * <p>Description: TA份额折算处理类</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ETFShareConvertAdmin
    extends BaseBean {
    public ETFShareConvertAdmin() {
    }

    /**
     * 此方法为入口方法，处理TA份额折算产生TA交易数据，并统计TA库存
     * @param sPortCode String 组合代码
     * @param sDate Date 日期
     * @param sInvMgrCode String 投资经理
     * @throws YssException
     */
    public void doOperation(String sPortCode, java.util.Date sDate, String sInvMgrCode) throws YssException {
        try {
            //此方法处理，TA份额折算产生TA交易数据
            createTAConvertData(sPortCode,sDate,sInvMgrCode);
            //此方法统计当天TA库存
            countTAStorage(sPortCode,sDate);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * countTAStorage
     * 此方法统计当天TA库存
     * @param sPortCode String 组合代码
     * @param sDate Date 日期
     */
    private void countTAStorage(String sPortCode, Date sDate) throws YssException{
        ArrayList dBeans=null;
        try{
            StgTAStorage taStorage=new StgTAStorage();//实例化库存统计类
            taStorage.setYssPub(pub);//设置PUb
            taStorage.initStorageStat(sDate,sDate,sPortCode,false,false);//初始化属性值
            dBeans = taStorage.getStorageStatData(sDate); //获得数据
            if (dBeans != null) {
               taStorage.saveStorageStatData(dBeans, sDate, true); //然后再进行保存
            }

        }catch(Exception e){
            throw new YssException("统计当天TA库存出错！",e);
        }
    }

    /**
     * 此方法处理，TA份额折算产生TA交易数据
     * @param sPortCode String 组合代码
     * @param sDate Date 日期
     * @param sInvMgrCode String 投资经理
     * @throws YssException
     */
    private void createTAConvertData(String sPortCode, java.util.Date sDate, String sInvMgrCode) throws YssException {
        String strNumDate = ""; //保存交易编号
        StringBuffer buff = null;
        ResultSet rs = null; //声明结果集
        String sPortCury = ""; //组合币种
        double baseCuryRate = 1; //基础汇率
        double portCuryRate = 1; //组合汇率
        BigDecimal dConvertScale = null; //份额折算比例
        double dStorageAmount = 0; //库存数量
        double dConvertMoney = 0; //份额折算金额
        String sCashAccCode = ""; //现金账户
        String sAssetCode="";//资产代码
        Connection conn=null;
        boolean bTrans=true;//事务控制标识
        String strYearMonth="";//期初数年份
        double tradeAmount = 0;//TA交易份额折算的数量
        double tradeConvertNum =0;//TA交易份额折算金额
        double StorageConvertNum = 0;//TA 库存份额折算金额
        try {
            conn=dbl.loadConnection();
            conn.setAutoCommit(false);
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_TA_Trade"));
            buff = new StringBuffer(1000);
            //--------------------拼接交易编号---------------------
            strNumDate = YssFun.formatDatetime(sDate).substring(0, 8);
            strNumDate = strNumDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("tb_ta_trade"),
                                       dbl.sqlRight("FNUM", 6), "000000",
                                       " where FNum like 'T" + strNumDate + "%'", 1);
            strNumDate = "T" + strNumDate;
            //---------------------------------------------------
            strYearMonth = YssFun.left(YssFun.formatDate(sDate), 4) + "00";
            buff.append(" select ta.fportcode, ta.fstorageamount,(case when ta.FConvertNum is null then 0 else ta.FConvertNum end ) as FStorageConvertNum,");
            buff.append("(case when p.fportcury is null then ' ' else p.fportcury end) as fportcury ,");
            buff.append("(case when p.FAssetCode is null then ' ' else p.FAssetCode end) as FAssetCode ,s.fconvertscale,");
            buff.append("(case when pa.fcashacccode is null then ' ' else pa.fcashacccode end) as fcashacccode,");
            buff.append("(case when trade.FSellAmount is null then 0 else trade.FSellAmount end) as FSellAmount,");
            buff.append("(case when trade.FConvertNum is null then 0 else trade.FConvertNum end) as FConvertNum from ");
            buff.append(pub.yssGetTableName("tb_stock_ta"));//TA库存表
            buff.append(" ta left join (select fportcode, FAssetCode, fportcury from  ").append(pub.yssGetTableName("Tb_Para_Portfolio"));//组合设置表
            buff.append(" where FCheckState = 1 and fportcode = ").append(dbl.sqlString(sPortCode)).append(" ) p on ta.fportcode = p.fportcode");
            buff.append(" left join (select fportcode,fconvertscale from ").append(pub.yssGetTableName("Tb_ETF_ShareConvert"));//份额折算表
            buff.append(" where FCheckState = 1 and fconvertdate = ").append(dbl.sqlDate(sDate));
            buff.append(" ) s on ta.fportcode = s.fportcode ").append(" left join (select FPortCode, FCashAccCode from ");
            buff.append(pub.yssGetTableName("tb_etf_param"));//ETF参数设置表
            buff.append(" where FCheckState = 1) pa on ta.fportcode = pa.fportcode ");
            buff.append(" left join( select fportcode,FSellAmount,FConvertNum from ").append(pub.yssGetTableName("tb_ta_trade"));
            buff.append(" where FCheckState = 1 and FTradeDate = ").append(dbl.sqlDate(sDate));
            buff.append(" and FSellType = ").append(dbl.sqlString(YssOperCons.YSS_TATRADETYPE_JYZS));
            buff.append(" ) trade on ta.fportcode = trade.fportcode");
            buff.append(" where ta.fcheckstate = 1 and ta.fportcode = ").append(dbl.sqlString(sPortCode));
            buff.append(" and ta.FStorageDate =").append(dbl.sqlDate(sDate));
            buff.append(" and ta.FYearMonth <>").append(dbl.sqlString(strYearMonth));

            rs = dbl.openResultSet(buff.toString());
            buff.delete(0, buff.length());
            if (rs.next()) {
                sPortCury = rs.getString("FPortCury");//组合币种
                dConvertScale = rs.getBigDecimal("FConvertScale");//份额折算比例
                dStorageAmount = rs.getDouble("fstorageamount");//库存数量
                sCashAccCode = rs.getString("fcashacccode");//现金账户
                sAssetCode=rs.getString("FAssetCode");//资产代码
                tradeAmount = rs.getDouble("FSellAmount");//TA交易份额折算的数量
                tradeConvertNum = rs.getDouble("FConvertNum");//TA交易份额折算金额
                StorageConvertNum = rs.getDouble("FStorageConvertNum");
            }
            if(dConvertScale==null){
            	return;
            }
            //------------此处处理原因是前台重复做TA份额折算生成净值出现数据错误---------------------------//
            buff.append(" update ").append(pub.yssGetTableName("tb_stock_ta"));
            buff.append(" set fstorageamount =").append(YssD.sub(dStorageAmount,tradeAmount));
            buff.append(" ,FConvertNum = ").append(YssD.sub(StorageConvertNum,tradeConvertNum));
            buff.append(" where fcheckstate = 1 and fportcode = ").append(dbl.sqlString(sPortCode));
            buff.append(" and FStorageDate =").append(dbl.sqlDate(sDate));
            buff.append(" and FYearMonth <>").append(dbl.sqlString(strYearMonth));
            dbl.executeSql(buff.toString());
            buff.delete(0,buff.length());
            //------------------------------end--------------------------------------------------//
            dConvertMoney = YssD.sub(YssD.mul(YssD.sub(dStorageAmount,tradeAmount), dConvertScale.doubleValue()), YssD.sub(dStorageAmount,tradeAmount));//份额调整余额
            baseCuryRate = this.getSettingOper().getCuryRate(//基础汇率
                sDate,
                sPortCury,
                sPortCode,
                YssOperCons.YSS_RATE_BASE);

            EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
            rateOper.setYssPub(pub);//设置PUB
            rateOper.getInnerPortRate(sDate, sPortCury, sPortCode);
            portCuryRate = rateOper.getDPortRate();//组合汇率
            //插入之前先删除数据
            buff.append(" delete from ").append(pub.yssGetTableName("Tb_TA_Trade"));
            buff.append(" where FPortCode =").append(dbl.sqlString(sPortCode));
            buff.append(" and FMarkDate =").append(dbl.sqlDate(sDate));
            buff.append(" and FSellType =").append(dbl.sqlString(YssOperCons.YSS_TATRADETYPE_JYZS));

            dbl.executeSql(buff.toString());
            buff.delete(0,buff.length());
            if(sCashAccCode.trim().length()!=0||sPortCury.trim().length()!=0){
	            //throw new YssException("ETF参数设置的现金账户没有链接到，请检查下现金账户是否设置！");
	            
	            //插入到TA交易数据表中
	            buff.append(" insert into ").append(pub.yssGetTableName("Tb_TA_Trade"));
	            buff.append(" (FNum,FTradeDate,FMarkDate,FPortCode,FPortClsCode,FSellNetCode,FSellType,FCuryCode,FAnalysisCode1,FAnalysisCode2,");
	            buff.append(" FAnalysisCode3,FSellMoney,FBeMarkMoney,FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FCashAccCode,");
	            buff.append(" FConfimDate,FSettleDate,FSettleMoney,FPortCuryRate,FBaseCuryRate,FSettleState,FDesc,FCheckState,FCreator,");
	            buff.append(" FCreateTime,FCheckUser,FCheckTime,FConvertNum)values(");
	            buff.append(dbl.sqlString(strNumDate)).append(",").append(dbl.sqlDate(sDate)).append(",").append(dbl.sqlDate(sDate)).append(",");
	            buff.append(dbl.sqlString(sPortCode)).append(",").append(dbl.sqlString(sAssetCode)).append(",").append(dbl.sqlString(" ")).append(",");
	            buff.append(dbl.sqlString(YssOperCons.YSS_TATRADETYPE_JYZS)).append(",").append(dbl.sqlString(sPortCury)).append(",");
	            buff.append(dbl.sqlString(" ")).append(",").append(dbl.sqlString(" ")).append(",").append(dbl.sqlString(" ")).append(",");
	            buff.append(0).append(",").append(0).append(",").append(dConvertMoney).append(",").append(0).append(",").append(0).append(",");
	            buff.append(0).append(",").append(dbl.sqlString(sCashAccCode)).append(",").append(dbl.sqlDate(sDate)).append(",");
	            buff.append(dbl.sqlDate(sDate)).append(",").append(0).append(",").append(portCuryRate).append(",");
	            buff.append(baseCuryRate).append(",").append(0).append(",").append(dbl.sqlString(" ")).append(",").append(1).append(",");
	            buff.append(dbl.sqlString(pub.getUserCode())).append(",").append(dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))).append(",");
	            buff.append(dbl.sqlString(pub.getUserCode())).append(",").append(dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))).append(",");
	            buff.append(dConvertMoney).append(")");
	
	            dbl.executeSql(buff.toString());
            }
            buff.delete(0,buff.length());

            conn.commit();
            bTrans=false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("产生TA份额折算数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn,bTrans);
        }
    }
    /**
     * 此方法查询出份额折算之前的单位净值数据
     * @param string 组合代码
     * @param date 日期
     * @param string2 投资经理
     * @return
     * @throws YssException 
     */
	public NavRepBean getOldNavValueData(String sPortCode, Date dDate, String sInvMgrCode) throws YssException {
		NavRepBean navRep = null;
		ResultSet rs=null;
		StringBuffer buff=null;
		CtlPubPara pubpara = null;
		String resultStr = "";
		double assetValue=0;//资产净值
		String strYearMonth="";//期初数年份
		try{
			buff = new StringBuffer(100); 
			navRep = new NavRepBean();
			navRep.setYssPub(pub);
			
			pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            resultStr = pubpara.getCashUnit(sPortCode);
			
			buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_NavData"));
			buff.append(" where FNavDate =").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode =").append(dbl.sqlString(sPortCode));
			buff.append(sInvMgrCode.trim().length()>0?" and FInvMgrCode ="+ dbl.sqlString(sInvMgrCode):"");
			buff.append(" and FKeyCode =").append(dbl.sqlString("TotalValue"));
			
			rs=dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			if(rs.next()){
				assetValue = rs.getDouble("FportMarketValue");
			}
			dbl.closeResultSetFinal(rs);
			
			strYearMonth = YssFun.left(YssFun.formatDate(dDate), 4) + "00";
            buff.append(" select ta.fstorageamount,(case when trade.FSellAmount is null then 0 else trade.FSellAmount end) as FSellAmount from ")
            	.append(pub.yssGetTableName("tb_stock_ta"));//TA库存表
            buff.append(" ta left join (select fportcode,FSellAmount from  ").append(pub.yssGetTableName("tb_ta_trade"));//组合设置表
            buff.append(" where FCheckState = 1 and FTradeDate = ").append(dbl.sqlDate(dDate));
            buff.append(" and FSellType = ").append(dbl.sqlString(YssOperCons.YSS_TATRADETYPE_JYZS));
            buff.append(" ) trade on ta.fportcode = trade.fportcode");
            buff.append(" where ta.fcheckstate = 1 and ta.fportcode = ").append(dbl.sqlString(sPortCode));
            buff.append(" and ta.FStorageDate =").append(dbl.sqlDate(dDate));
            buff.append(" and ta.FYearMonth <>").append(dbl.sqlString(strYearMonth));
			
            rs=dbl.openResultSet(buff.toString());
            buff.delete(0,buff.length());
			if(rs.next()){
				navRep.setNavDate(dDate); //净值日期
	            navRep.setPortCode(sPortCode);
	            navRep.setOrderKeyCode("Total8");
	            navRep.setKeyCode("OldUnit");//份额折算之前净值标识
	            navRep.setKeyName("原单位净值：");
	            navRep.setDetail(0); //汇总
	            navRep.setReTypeCode("Total");
	            navRep.setCuryCode(" ");
	            navRep.setIsinCode(resultStr); //获取位数，将其存入Insi 代码中。
	            navRep.setPrice(YssD.div(assetValue,YssD.sub(rs.getDouble("fstorageamount"),rs.getDouble("FSellAmount")))); //将单位净值的值放入价格中。
	            if (!sInvMgrCode.equalsIgnoreCase("total")) {
	                navRep.setInvMgrCode(sInvMgrCode);
	            } else {
	                navRep.setInvMgrCode("total");
	            }
			}
		}catch(Exception e){
			throw new YssException("查询份额折算之前的单位净值数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return navRep;
	}
}












