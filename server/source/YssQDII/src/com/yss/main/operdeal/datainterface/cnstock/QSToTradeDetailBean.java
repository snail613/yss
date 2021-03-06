package com.yss.main.operdeal.datainterface.cnstock;

import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdata.TradeRelaBean;
import com.yss.main.operdata.TradeRelaSubBean;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.linkInfo.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.*;
import com.yss.pojo.cache.YssCost;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.commeach.EachRateOper;
import com.yss.manager.TradeRelaDataAdmin;
import java.util.*;
import java.util.Date;
import java.sql.*;

import org.omg.PortableInterceptor.HOLDING;

import com.yss.main.operdata.MarketValueBean;
import com.yss.main.operdata.overthecounter.PurchaseTradeAdmin;
import com.yss.main.operdata.overthecounter.pojo.PurchaseTradeBean;
import com.yss.main.operdeal.BaseAvgCostCalculate;
import com.yss.main.operdeal.datainterface.cnstock.pojo.FeeWayBean;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadTypeBean;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.datainterface.cnstock.*;

/**
 *
 * <p>Title: 处理国内接口：清算表至业务资料表(及关联表)</p>
 *
 * <p>Description: QDV4.1赢时胜（上海）2009年4月20日25_A   MS00025</p>
 *
 * <p>Copyright: 200907</p>
 *
 * <p>Company: ysstech</p>
 *
 * @by leeyu 2009-07-06
 * @version 1.0
 */
public class QSToTradeDetailBean extends DataBase{
    public QSToTradeDetailBean() {
    }
    private HashMap hmNum=null;                         //保存业务资料的交易编号
    private ArrayList alTradeRela =new ArrayList();     //保存交易关联表数据
    private ArrayList alTradeSubRela =new ArrayList();  //保存交易关联子表数据
    private String sSubNums="";                         //保存交易子表的编号
    private HashMap hmHolderSeat =null;
  //Ms01336 modify by zhangfa  20100803    QDV4赢时胜2010年06月22日01_A  
    private  String temp="";
    private String[] flags=null;
    private String errorMsg = "";
  //------------------------------------------------------------------  
    public void inertData(HashMap hmParam) throws YssException{
    	errorMsg = "将数据从清算表插入到业务资料表失败！";
        Connection conn =null;
        boolean bTrans =false;
        try{
            conn =dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans =true;
            pubMethod.setYssPub(pub);

            //获取数据接口参数设置的费用承担方向界面设置的参数对应的HashMap
            hmFeeWay = (HashMap)hmParam.get("hmFeeWay");
            //add bylidaolong 20110406 
            hmReadType=(HashMap)hmParam.get("hmReadType");
            //事前处理
            before(conn);
            //插入数据
            insertTradeDetail(conn);
            
          
            //事后处理
            after(conn);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans =false;
            //MS01336    add by zhangfa 20100803    QDV4赢时胜2010年06月22日01_A   
            if(temp.indexOf("true")!=-1){
            	temp="";
    			errorMsg = "数据已覆盖系统中当日通过权益处理的债券派息数据,且数据全部导入成功!";
    			throw new YssException(errorMsg);
            }
           
            //------------------------------------------------------------------
        }catch(Exception ex){
            throw new YssException(errorMsg,ex);//edit by yanghaiming 20100819
        }finally{
            dbl.endTransFinal(conn,bTrans);
        }
    }

    /**
     * 处理将清算表数据插入到交易子表(业务资料表)的过程
     * @throws YssException
     */
    private void insertTradeDetail(Connection conn) throws YssException{
        ResultSet rsQs =null;
        ResultSet rs=null;
        PreparedStatement stm=null;
        String sqlStr="";
        String stockholderCode=""; //股东代码
        String seatCode="";  //交易席位号  edited by zhouxiang 
      
        String[] seatNum=null;
        String[] arrPort=null;

        int iRow=0;
        TradeSubBean subTrade=null;
        try{
            arrPort =sPort.split(",");
            sqlStr="insert into "+pub.yssGetTableName("Tb_Data_SubTrade")+
                "(FNum,FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,FAttrClsCode,FRateDate,"+
                "FBargainDate,FBargainTime,FSettleDate,FSettleTime,FMatureDate,FMatureSettleDate,FFactCashAccCode,FFactSettleMoney,"+
                "FExRate,FFactBaseRate,FFactPortRate,FAutoSettle,FPortCuryRate,FBaseCuryRate,FAllotProportion,FOldAllotAmount,"+
                "FAllotFactor,FTradeAmount,FTradePrice,FTradeMoney,FAccruedinterest,FBailMoney,FFeeCode1,FTradeFee1,FFeeCode2,"+
                "FTradeFee2,FFeeCode3,FTradeFee3,FFeeCode4,FTradeFee4,FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6,FFeeCode7,"+
                "FTradeFee7,FFeeCode8,FTradeFee8,FTotalCost,FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,"+
                "FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FSettleState,FFactSettleDate,Fsettledesc,FOrderNum,FDataSource,"+
                "FDataBirth,FSettleOrgCode,FStockholderCode,FSeatCode,FDesc,FCheckState,FCreator,FCreateTime,FDS" +
              //MS01354    add by zhangfa 20100719    QDV4赢时胜(上海)2010年06月25日01_A 
                ",FJKDR)" +
              //------------------------------------------------------------------------  
                " values("+//添加结算类型字段 by leeyu 20090901
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
              //MS01354    add by zhangfa 20100719    QDV4赢时胜(上海)2010年06月25日01_A 
                ",?)";//72
              //
            // add by lidaolong 20110406 #536 有关国内接口数据处理顺序的变更
        	ReadTypeBean readType = null; // 声明ReadTypeBean
        	java.util.Date tradeDate = null; // 声明交易日期
        	BaseOperDeal baseOperDeal = new BaseOperDeal(); // 新建BaseOperDeal
			baseOperDeal.setYssPub(pub);
			//end by lidaolong
           
            
            stm =conn.prepareStatement(sqlStr);
            for (int iPort = 0; iPort < arrPort.length; iPort++) {
                if (hmHolderSeat.get(arrPort[iPort]) != null) {
                    //seatCode = String.valueOf(hmHolderSeat.get(arrPort[iPort])).split("\t")[0]; //查找组合下的所有席位
                    seatCode = getSeatCodeByPort(arrPort[iPort]);//查找组合下的所有代码  edited by zhouxiang MS01299    接口处理界面导入上海过户库时出现提示信息    QDV4赢时胜(测试)2010年6月12日2_B    
                    stockholderCode = String.valueOf(hmHolderSeat.get(arrPort[iPort])).split("\t")[1]; //查找组合下的所有股东代码
                }
              //modify  by zhangfa 20110110 BUG #758 在除权日读入其他交易所业务数据后再查询交易数据，无此股票分红业务记录   
                /**
                //1:删除业务资料表数据
                sqlStr="delete from "+pub.yssGetTableName("Tb_Data_SubTrade")+
                    " sa where exists( select * from (select qs.FDate,FInDate,qs.FZqdm,FJyxwh,FGddm,FPortCode,seat.FBrokerCode from " + pub.yssGetTableName("Tb_HZJKQS") + " qs left join "+
                    //modify by zhangfa MS01683    导过户库时，重复导入，交易数据中会产生重复数据    QDV4赢时胜(上海开发部)2010年09月01日02_B    
                    pub.yssGetTableName("Tb_Para_TradeSeat")+" seat on qs.fjyxwh = seat.fseatnum  where qs.FPortCode=" + dbl.sqlString(arrPort[iPort]) + " and qs.FIndate=" + dbl.sqlDate(sDate)+
                    //-----------------------------------------------------------------------------------------------------------------
                    " and qs.FGddm in(" + operSql.sqlCodes(stockholderCode) + ") and qs.FJyxwh in(" + operSql.sqlCodes(seatCode) + ") ) sb "+
                    " where sa.FSecurityCode=sb.fzqdm and sa.Fbargaindate = sb.Fdate and sa.Fportcode = sb.Fportcode and sa.FStockholderCode=sb.FGddm and sa.FSeatCode=sb.FJyxwh "+
                    " and sa.Fbrokercode = sb.Fbrokercode ) and sa.Fdatasource =1 and sa.FDS in('ZD_JK','ZD_QY') ";//这里去掉结算类型，添加数据来源类型,新增字段 edit by songjie 2009.09.24
                    // modify by fangjiang 2010.09.13 去掉所属分类
                     
                dbl.executeSql(sqlStr);
                *  */

                readType = (ReadTypeBean)hmReadType.get
    			(assetGroupCode + " " + arrPort[iPort]);
                
                //---add by songjie 2011.11.09 BUG 3042 QDV4赢时胜(测试)2011年11月01日02_B start---//
                if(readType == null){
                	throw new YssException("请在交易接口参数设置中设置 " + arrPort[iPort] + " 组合的读数处理方式！");
                }
                //---add by songjie 2011.11.09 BUG 3042 QDV4赢时胜(测试)2011年11月01日02_B end---//
                
    			tradeDate = baseOperDeal.getWorkDay
    			(readType.getHolidaysCode(), sDate, 1);
    			
    			//---add by songjie 2012.01.13 BUG 3642 QDV4赢时胜(测试)2012年01月13日01_B start---//
    			sqlStr = " delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") + 
    			         " where FNum in (select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
    			         " where FTradeNum in (select FNum from " + pub.yssGetTableName("Tb_Data_SubTrade") + 
    			         " sa where sa.FPortCode=" + dbl.sqlString(arrPort[iPort])+
                         " and sa.Fdatasource =1 and ((sa.FDS in ('ZD_JK','ZD_QY') and sa.Fbargaindate =" +dbl.sqlDate(sDate)+
                         ") OR (sa.FDS in ('ZD_JK_T+1','ZD_QY_T+1') and sa.Fbargaindate = " + dbl.sqlDate(tradeDate) + " ))))";
    			dbl.executeSql(sqlStr);
    			
    			sqlStr = " delete from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
    			         " where FTradeNum in (select FNum from " + pub.yssGetTableName("Tb_Data_SubTrade") + 
    			         " sa where sa.FPortCode=" + dbl.sqlString(arrPort[iPort])+
                         " and sa.Fdatasource =1 and ((sa.FDS in ('ZD_JK','ZD_QY') and sa.Fbargaindate =" +dbl.sqlDate(sDate)+
                         ") OR (sa.FDS in ('ZD_JK_T+1','ZD_QY_T+1') and sa.Fbargaindate = " + dbl.sqlDate(tradeDate) + " )))";
    			dbl.executeSql(sqlStr);
    			//---add by songjie 2012.01.13 BUG 3642 QDV4赢时胜(测试)2012年01月13日01_B end---//
    			
                // add by lidaolong 20110406 #536 有关国内接口数据处理顺序的变更
                //删除1 
               sqlStr="delete from "+pub.yssGetTableName("Tb_Data_SubTrade")+" sa where sa.FPortCode = " + dbl.sqlString(arrPort[iPort])+
                " and sa.Fdatasource =1 and ((sa.FDS in('ZD_JK','ZD_QY') AND sa.Fbargaindate = " +dbl.sqlDate(sDate)+
                ")  OR (sa.FDS in('ZD_JK_T+1','ZD_QY_T+1') AND sa.Fbargaindate = " + dbl.sqlDate(tradeDate) + "))";
               dbl.executeSql(sqlStr);
             
                //end by lidaolong
                
                
                //---------------------------end 20110110----------------------------------------------------------
                //2:查询相关数据,执行插入数据操作
                sqlStr = "select qs.*,se.FTradeCury,se.FCatCode,se.FSubCatCode,se.FCuscatCode,se.FExchangeCode,se.FHolidaysCode," +
                    //edit by songjie 2012.01.19 BUG 3708 QDV4赢时胜(上海开发部)2012年1月19日01_B 添加 seat.FSeatCode
                    "se.FSettleDayType,se.FSettleDays,seat.FBrokerCode,sh.finvmgrcode, seat.FSeatCode from " + pub.yssGetTableName("Tb_HZJKQS") +
                    " qs left join " + pub.yssGetTableName("Tb_Para_Security") + " se on qs.FZqdm=se.FSecurityCode "+
                    //edit by songjie 2010.08.14 将seat.FseatCode 改为  seat.FseatNum,应该用席位号来关联席位设置表
                    " left join (select * from "+pub.yssGetTableName("Tb_Para_TradeSeat")+" where FCheckState = 1) seat on qs.FJyxwh = seat.FSeatNum "+
                    " left join (select * from "+pub.yssGetTableName("tb_para_stockholder")+" where FCheckState = 1) sh on qs.FGddm = sh.fstockholdercode "+//通过股东代码获取对应的投资经理 panjunfang add 20100421 B股
                    " where qs.FIndate=" + dbl.sqlDate(sDate) + " and qs.FPortCode=" + dbl.sqlString(arrPort[iPort]) +
                    " and qs.FGddm in(" + operSql.sqlCodes(stockholderCode) + ") and qs.FJyxwh in(" + operSql.sqlCodes(seatCode) + ")";
                rsQs=dbl.openResultSet(sqlStr);

                while(rsQs.next()){
                    subTrade =convertTradeDetail(rsQs);
                    
                    if(subTrade ==null) continue;//如果subTrade 为空,则跳过本次插入操作
                  //add by zhangfa 20110110 BUG #758 在除权日读入其他交易所业务数据后再查询交易数据，无此股票分红业务记录   
                  //此bug解决方案：增加交易类型作为删除条件。由于  交易接口清算表中的Fywbz(业务标志)与交易子表的FtradeTypeCode
                  //之间的对应关系十分复杂，所以不能在原有的删除语句上进行修改，只能根据条件，产生一条业务数据之前，先删除一条
                  //历史业务数据  
                  //1:删除业务资料表数据
                    // del by lidaolong 20110406 #536 有关国内接口数据处理顺序的变更
             /*    String strdSql="delete from "+pub.yssGetTableName("Tb_Data_SubTrade")
                                + "  where  fsecuritycode=" +dbl.sqlString(subTrade.getSecurityCode())  
                                + "  and fbargaindate=" +dbl.sqlDate(subTrade.getBargainDate())
                                + "  and ftradetypecode=" + dbl.sqlString(subTrade.getTradeCode())
                                + "  and FportCode="+dbl.sqlString(subTrade.getPortCode())
                                +"   and FStockholderCode="+dbl.sqlString(subTrade.getStockholderCode()) 
                                +"   and FSeatCode="+dbl.sqlString(subTrade.getTradeSeatCode())
                                +"   and Fbrokercode="+dbl.sqlString(subTrade.getBrokerCode())
                                +"   and Fdatasource =1 and FDS in('ZD_JK','ZD_QY')";
                    dbl.executeSql(strdSql);*/
                    //end by lidaolong
                    //----------------end 20110110 -------------------------------------------------------------------
                    
                  //MS01336    add by zhangfa 20100803    QDV4赢时胜2010年06月22日01_A    
                    if(subTrade.getTradeCode().equalsIgnoreCase("88")){
                    	temp=checkRightTrade(subTrade)+",";
                     String	dSql = "delete from " + pub.yssGetTableName("tb_data_subtrade")
   					           + "  where  fsecuritycode=" +dbl.sqlString(subTrade.getSecurityCode())  
					           + "  and fbargaindate=" +dbl.sqlDate(subTrade.getBargainDate())
					           + "  and ftradetypecode=" + dbl.sqlString(subTrade.getTradeCode());
                       dbl.executeSql(dSql);
                       
                       //若旧数据有资金调拨,则删除资金调拨
                       //下面添加删除结算数据的资金调拨的方法 
                      String cashNums="";
                      String sql = "select FNum from " +
                           pub.yssGetTableName("Tb_Cash_Transfer") +
                           " where FSecurityCode in(" + dbl.sqlString(subTrade.getSecurityCode())+ 
                           ") and FTransDate=" +dbl.sqlDate(subTrade.getBargainDate())+
                           "  and FTransferDate=" +dbl.sqlDate(subTrade.getSettleDate())+
                           "  and FTradeNum is not null "+
                           "  and FTsfTypeCode='02' and FSubTsfTypeCode='02FI'"+
                           "  and FCheckState=1";
                       rs = dbl.openResultSet(sql);
                       while(rs.next()){
                           cashNums +=rs.getString("FNum")+",";
                       }
                       rs.getStatement().close();
                       //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
                       rs.close();
                       if(cashNums.length()>0){
                           //删除子表中的数据
                           dbl.executeSql( "delete from " +
                                           pub.yssGetTableName("Tb_Cash_SubTransfer") +
                                           " where FNum in (" + operSql.sqlCodes(cashNums)+")");

                           //删除主表中的数据
                           dbl.executeSql("delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                               " where FNum in(" + operSql.sqlCodes(cashNums) +")");
                       }
                    }
                   
                  //------------------------------------------------------------------  
                  //MS01354 add by zhangfa 20100819
                  //add by songjie 2010.12.16 BUG 551 南方东英2010年12月02日01_B
                  //若为分红数据，则需先删除权益处理产生的分红数据，再插入接口导出的分红数据，已接口导入的为准
					if (subTrade.getTradeCode().equalsIgnoreCase("08")
							|| subTrade.getTradeCode().equalsIgnoreCase("22")
							|| subTrade.getTradeCode().equalsIgnoreCase("23")
							|| subTrade.getTradeCode().equalsIgnoreCase("06")
							|| subTrade.getTradeCode().equalsIgnoreCase("07")) {
						String dSql = "delete from "
								+ pub.yssGetTableName("tb_data_subtrade")
								+ " where fsecuritycode = "
								+ dbl.sqlString(subTrade.getSecurityCode())
								+ " and fbargaindate = "
								+ dbl.sqlDate(subTrade.getBargainDate()) + " "
								+ " and ftradetypecode = "
								+ dbl.sqlString(subTrade.getTradeCode());
						dbl.executeSql(dSql);
					}
                  //--------------------------------
					
                    stm.setString(1, subTrade.getNum()); //编号
                    stm.setString(2, subTrade.getSecurityCode()); //证券代码
                    stm.setString(3, subTrade.getPortCode()); //组合代码
                    stm.setString(4, subTrade.getBrokerCode()); //券商代码
                    stm.setString(5, subTrade.getInvMgrCode()); //投资经理
                    stm.setString(6, subTrade.getTradeCode()); //交易类型
                    stm.setString(7, subTrade.getCashAcctCode()); //现金帐户
                    stm.setString(8, subTrade.getAttrClsCode()); //所属分类
                    stm.setDate(9, YssFun.toSqlDate(subTrade.getRateDate())); //汇率日期
                    stm.setDate(10, YssFun.toSqlDate(subTrade.getBargainDate())); //业务日期
                    stm.setString(11, subTrade.getBargainTime()); //业务时间
                    stm.setDate(12,YssFun.toSqlDate(subTrade.getSettleDate())); //结算日期
                    stm.setString(13, subTrade.getSettleTime()); //结算时间
                    stm.setDate(14, YssFun.toSqlDate(subTrade.getMatureDate())); //到期日期
                    stm.setDate(15, YssFun.toSqlDate(subTrade.getMatureSettleDate())); //到期结算日期
                    stm.setString(16, subTrade.getFactCashAccCode()); //实际结算现金帐户
                    stm.setDouble(17, subTrade.getFactSettleMoney()); //实际结算现金金额
                    stm.setDouble(18, subTrade.getExRate()); //兑换汇率
                    stm.setDouble(19, subTrade.getFactBaseRate()); //实际基础汇率
                    stm.setDouble(20, subTrade.getFactPortRate()); //实际组合汇率
                    stm.setString(21, subTrade.getAutoSettle()); //自动结算标志
                    stm.setDouble(22, subTrade.getPortCuryRate()); //组合汇率
                    stm.setDouble(23, subTrade.getBaseCuryRate()); //基础汇率
                    stm.setDouble(24, subTrade.getAllotProportion()); //分配比例
                    stm.setDouble(25, subTrade.getOldAllotAmount()); //原始分配数量
                    stm.setDouble(26, subTrade.getFactor()); //分配因子
                    stm.setDouble(27, subTrade.getTradeAmount()); //交易数量
                    stm.setDouble(28, YssD.round(YssD.div(subTrade.getTradeMoney(),subTrade.getTradeAmount()),2)); //交易价格
                    stm.setDouble(29, subTrade.getTradeMoney()); //交易金额
                    stm.setDouble(30, subTrade.getAccruedInterest()); //应计利息
                    stm.setDouble(31, subTrade.getBailMoney()); //保证金金额
                    stm.setString(32, subTrade.getFFeeCode1()); //费用代码1
                    stm.setDouble(33, subTrade.getFTradeFee1()); //交易费用1
                    stm.setString(34, subTrade.getFFeeCode2()); //费用代码2
                    stm.setDouble(35, subTrade.getFTradeFee2()); //交易费用2
                    stm.setString(36, subTrade.getFFeeCode3()); //费用代码3
                    stm.setDouble(37, subTrade.getFTradeFee3()); //交易费用3
                    stm.setString(38, subTrade.getFFeeCode4()); //费用代码4
                    stm.setDouble(39, subTrade.getFTradeFee4()); //交易费用4
                    stm.setString(40, subTrade.getFFeeCode5()); //费用代码5
                    stm.setDouble(41, subTrade.getFTradeFee5()); //交易费用5
                    stm.setString(42, subTrade.getFFeeCode6()); //费用代码6
                    stm.setDouble(43, subTrade.getFTradeFee6()); //交易费用6
                    stm.setString(44, subTrade.getFFeeCode7()); //费用代码7
                    stm.setDouble(45, subTrade.getFTradeFee7()); //交易费用7
                    stm.setString(46, subTrade.getFFeeCode8()); //费用代码8
                    stm.setDouble(47, subTrade.getFTradeFee8()); //交易费用8
                    stm.setDouble(48, subTrade.getTotalCost()); //清算金额
                    stm.setDouble(49, subTrade.getCost().getCost()); //原币成本
                    stm.setDouble(50, subTrade.getCost().getMCost());
                    stm.setDouble(51, subTrade.getCost().getVCost());
                    stm.setDouble(52, subTrade.getCost().getBaseCost()); //基础货币成本
                    stm.setDouble(53, subTrade.getCost().getBaseMCost());
                    stm.setDouble(54, subTrade.getCost().getBaseVCost());
                    stm.setDouble(55, subTrade.getCost().getPortCost()); //组合货币成本
                    stm.setDouble(56, subTrade.getCost().getPortMCost());
                    stm.setDouble(57, subTrade.getCost().getPortVCost());
                    stm.setString(58, subTrade.getSettleState()); //结算状态
                    stm.setDate(59, YssFun.toSqlDate(subTrade.getFactSettleDate())); //实际结算日期
                    stm.setString(60, subTrade.getSettleDesc()); //结算描述
                    stm.setString(61, subTrade.getOrderNum()); //交易序号
                    stm.setDouble(62, subTrade.getDataSource()); //数据来源
                    stm.setString(63, " "); //交易来源
                    stm.setString(64, " "); //结算机构
                    stm.setString(65, subTrade.getStockholderCode()); //股东代码
                    stm.setString(66, subTrade.getTradeSeatCode()); //交易席位
                    stm.setString(67, subTrade.getDesc()); //备注
                    stm.setInt(68, checkState.equalsIgnoreCase("true")?1:0); //审核状态
                    stm.setString(69, rsQs.getString("FCreator")); //创建人
                    stm.setString(70, rsQs.getString("FCreateTime")); //创建时间
                    //add by songjie 2009.09.24
                   
                    // add by lidaolong #536 有关国内接口数据处理顺序的变更
                     if ("ZD_QY_T+1".equals(rsQs.getString("fds"))){
                    	 stm.setString(71,"ZD_QY_T+1");//则将FDs设置为ZD_QY_T+1
                    }
                    else if ("ZD_JK_T+1".equals(rsQs.getString("fds"))){
                    	 stm.setString(71,"ZD_JK_T+1");//则将FDs设置为ZD_JK_T+1
                    }else  if(rsQs.getString("FZqbz").equals("QY")){//若为权益数据
                        stm.setString(71,"ZD_QY");//则将FDs设置为ZD_QY
                    }//END BY LIDAOLONG
                    else{
                        stm.setString(71, "ZD_JK"); //添加操作类型标志，为:自动-接口
                    }
                    //MS01354    add by zhangfa 20100719    QDV4赢时胜(上海)2010年06月25日01_A 
                    stm.setString(72, subTrade.getJkdr());
                    //----------------------------------------------------------------------
                    stm.addBatch();
                    iRow++;
                    if (iRow == 10000) {
                        stm.executeBatch();
                        iRow = 0;
                    }
                }//end while
            }//end for arrPort
            stm.executeBatch();
    
        }catch(Exception ex){
            throw new YssException(ex.getMessage(),ex);
        }finally{
            dbl.closeStatementFinal(stm);
            dbl.closeResultSetFinal(rsQs);
        }
    }
    //MS01336    add by zhangfa 201000803  MS01336
    /**
     * 检测是否存在非接口导入相关的交易数据
     */
    public boolean checkRightTrade(TradeSubBean tradesub) throws YssException{
		boolean flag = false;
		String strSql = "";
		ResultSet rs = null;
		try {
			
			strSql = "select fsecuritycode,fsettledate,fjkdr from  "
					+ pub.yssGetTableName("tb_data_subtrade")
					+ "  where  fsecuritycode=" + dbl.sqlString(tradesub.getSecurityCode()) 
					+ "  and fbargaindate=" +dbl.sqlDate(tradesub.getBargainDate())
				    + "  and ftradetypecode=" + dbl.sqlString(tradesub.getTradeCode())
					+ "  and (FJKDR is null or FJKDR !='1') ";
			;
			rs = dbl.openResultSet(strSql);

			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			throw new YssException("存储配股权益处理出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return flag;
   	 
    }
   //----------------------------------------------------------------------
    /**
     * 
     * @throws SQLException 
     * @方法名：getSeatCode add by zhouxiang 依据组合名称加载出该组合下的席位代码
     * @参数：组合名称 PORT  MS01299    接口处理界面导入上海过户库时出现提示信息    QDV4赢时胜(测试)2010年6月12日2_B    
     * @返回类型：String 
     * @说明：TODO
     */
    private String getSeatCodeByPort(String port) throws YssException, SQLException {
       	String SeatCode="";
       	//edit by songjie 2010.08.14 将FSeatCode 改为FSeatNum，获取的应该是席位号，不是席位代码
    	String StrPortcode="select fseatnum from "+pub.yssGetTableName("Tb_Para_Tradeseat")+" where fseatcode in (select fsubcode from "
    	+pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")+" where fportcode ="+dbl.sqlString(port)+")";
    	ResultSet rs=dbl.openResultSet(StrPortcode);
    	while(rs.next())
    	{
    		//edit by songjie 2010.08.14 将FSeatCode 改为FSeatNum，获取的应该是席位号，不是席位代码
    		SeatCode+=rs.getString("fseatnum")+",";
    	}
    	SeatCode=SeatCode.substring(0,SeatCode.length()-1);
    	//edit by songjie 2011.04.07 关闭结果集
    	dbl.closeResultSetFinal(rs);
		return SeatCode;
	}

	/**
     * 事前方法处理
     * @throws YssException
     */
    private void before(Connection conn) throws YssException{
        ResultSet rs =null;
        String sqlStr="";
        String sNums="";           //交易编号
        String sSecurityCodes="";  //证券代码
        String cashNums="";        //调拨编号
        String stockholderCode="",seatCode="";
        String[] arrPort=null;
        try{
            arrPort= sPort.split(",");
            hmHolderSeat =getStockHolderAndSeat(sPort);
            for(int i=0;i<arrPort.length;i++){
			//---edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B start---//
                seatCode += String.valueOf(hmHolderSeat.get(arrPort[i])).split("\t")[0]+","; //查找组合下的所有席位
                stockholderCode += String.valueOf(hmHolderSeat.get(arrPort[i])).split("\t")[1]+","; //查找组合下的所有股东代码
            }
            
            if(seatCode.length() > 1){
            	seatCode = seatCode.substring(0 , seatCode.length() - 1);
            	stockholderCode = stockholderCode.substring(0, stockholderCode.length() - 1);
            }
            
            sqlStr="select distinct FNum,FSecurityCode from "+pub.yssGetTableName("Tb_Data_SubTrade")+
                " sa where exists( select * from (" + 
                "select qs.*,se.FTradeCury,se.FCatCode,se.FSubCatCode,se.FCuscatCode,se.FExchangeCode,se.FHolidaysCode," +
                "se.FSettleDayType,se.FSettleDays,seat.FBrokerCode,sh.finvmgrcode, seat.FSeatCode from " + pub.yssGetTableName("Tb_HZJKQS") +
                " qs left join " + pub.yssGetTableName("Tb_Para_Security") + " se on qs.FZqdm=se.FSecurityCode "+
                " left join (select * from "+pub.yssGetTableName("Tb_Para_TradeSeat")+" where FCheckState = 1) seat on qs.FJyxwh = seat.FSeatNum "+
                " left join (select * from "+pub.yssGetTableName("tb_para_stockholder")+" where FCheckState = 1) sh on qs.FGddm = sh.fstockholdercode "+
                " where qs.FIndate=" + dbl.sqlDate(sDate) + " and qs.FPortCode in (" + operSql.sqlCodes(sPort) +
                " ) and qs.FGddm in(" + operSql.sqlCodes(stockholderCode) + ") and qs.FJyxwh in(" + operSql.sqlCodes(seatCode) + 
				" )) sb where sa.FSecurityCode=sb.fzqdm and sa.Fbargaindate = sb.Fdate and sa.Fportcode = sb.Fportcode and sa.FStockholderCode=sb.FGddm and sa.FSeatCode=sb.FJyxwh "+
                " and sa.Fbrokercode = sb.Fbrokercode ) and sa.Fattrclscode in ('CEQ','IDXEQ','TAGEQ','YYSG','XGSG','XGFK'," + 
                "'XGZQ','XGZF','PSZFZQ','SHZQ','ZLT','KPSL','XZ','AntiRepo','SellRepo','X','S','F','C','PG','PGJK','PX','SG',' ') " + 
                " and sa.Fdatasource =1 and sa.FDs in('ZD_JK','ZD_QY','ZD_JK_T+1','ZD_QY_T+1') "; //这里去掉结算标志字段，添加数据来源字段，by leeyu 20090901
            //---edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B end---//
			rs =dbl.openResultSet(sqlStr);
            while(rs.next()){
                sNums +=rs.getString("FNum")+",";
                sSecurityCodes +=rs.getString("FSecurityCode")+",";
            }
            //---edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B start---//
            if(sNums.length() > 1){
            	sNums = sNums.substring(0, sNums.length() - 1);
            	sSecurityCodes = sSecurityCodes.substring(0, sSecurityCodes.length() - 1);
            }
            //---edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B end---//
            TradeRelaDataAdmin tradeRela=new TradeRelaDataAdmin();
            tradeRela.setYssPub(pub);
            tradeRela.delete(sNums,"","","","","","");//这里先删除关联表的数据
            rs.getStatement().close();
            //下面添加删除结算数据的资金调拨的方法 by leeyu 20090901
            sqlStr = "select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FSecurityCode in(" + operSql.sqlCodes(sSecurityCodes) +
                ") and FTradeNum in (" + operSql.sqlCodes(sNums) +
                ") and FCheckState=1";
            rs = dbl.openResultSet(sqlStr);
            while(rs.next()){
                cashNums +=rs.getString("FNum")+",";
            }
            //---edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B start---//
            if(cashNums.length() > 1){
            	cashNums = cashNums.substring(0 , cashNums.length() - 1);
            }
            //---edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B end---//
            rs.getStatement().close();
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            rs.close();
            if(cashNums.length()>0){
                //删除子表中的数据
                dbl.executeSql( "delete from " +
                                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                                " where FNum in (" + operSql.sqlCodes(cashNums)+")");

                //删除主表中的数据
                dbl.executeSql("delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FNum in(" + operSql.sqlCodes(cashNums) +")");
            }
        }catch(Exception ex){
            throw new YssException(ex.getMessage(),ex);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 事后方法处理
     * @throws YssException
     */

    private void after(Connection conn) throws YssException{
        try{
            TradeRelaDataAdmin tradeRela=new TradeRelaDataAdmin();
            tradeRela.setYssPub(pub);
            tradeRela.setList(alTradeRela);
            tradeRela.setSubList(alTradeSubRela);
            tradeRela.insert();//这里不删除数据了
        }catch(Exception ex){
            throw new YssException("添加插入到交易关联表数据出错!",ex);
        }
    }
    /**
     * 转换业务明细的方法
     * 返回QD系统能够识别的业务明细数据
     * @param rs ResultSet
     * @return String
     * @throws YssException
     */
    private TradeSubBean convertTradeDetail(ResultSet rs) throws YssException{
        String tradeTypeCode="";
        String securityCode="";//证券代码
        String securitySign="",tradeSign="";//证券标志,业务标志
        java.util.Date dSettleDate=null; //结算日期,根据证券信息的延迟天数算出来
        BaseOperDeal operDeal =null;
        BaseCashAccLinkDeal cashAccLink=null;// by leeyu 20090814
        EachRateOper eachRate=null;
        TradeSubBean subTrade =null;
        try{
            subTrade =new TradeSubBean();
            //modify by zhangfa 20100719  MS01354
            subTrade.setJkdr("1");
            //------------------------------------
            securityCode=rs.getString("FZQDM");//转换后的证券代码
            securitySign=rs.getString("FZQBZ");//证券标志
            subTrade.setSecurityCode(rs.getString("FZQDM"));
            subTrade.setPortCode(rs.getString("FPortCode"));
            subTrade.setBrokerCode(rs.getString("FBrokerCode"));
            if(securitySign.equals("B_GP")){//设置交易数据的投资经理为股东代码对应的投资经理 panjunfang modify 20100421 B股业务
            	if(rs.getString("finvmgrcode") == null){
            		throw new YssException("股东代码【"+rs.getString("FGddm")+"】未设置对应的投资经理，请检查！");
            	}
            	subTrade.setInvMgrCode(rs.getString("finvmgrcode"));
            }
            /**start add by huangqirong 2013-6-26 Bug #8390 投资经理根据获取的股东设置进行加载 */
            else if(rs.getString("finvmgrcode") != null && rs.getString("finvmgrcode").trim().length() > 0){
            	subTrade.setInvMgrCode(rs.getString("finvmgrcode"));
            }
			/**end add by huangqirong 2013-6-26 Bug #8390 投资经理根据获取的股东设置进行加载 */
            else{
                subTrade.setInvMgrCode(" ");	
            }
            tradeSign =rs.getString("FYwbz");//业务标志
            // modify by fangjiang 2010.09.09 MS01138 QDV4赢时胜上海2010年04月28日01_A 
            subTrade.setAttrClsCode(" "); 
            if ("GP".equals(rs.getString("FZQBZ"))) { 
            	//证券标识（ZQBZ）为GP(股票)
            	if ("MR".equals(rs.getString("FYWBZ")) || "MC".equals(rs.getString("FYWBZ"))) {
            		//业务标识（YWBZ）为MR(买入)或MC(卖出)
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_CEQ); 
            	} else if ("MR_ZS".equals(rs.getString("FYWBZ")) || "MC_ZS".equals(rs.getString("FYWBZ"))) {
            		//业务标识（YWBZ）为MR_ZS(买入_指数)或MC_ZS(卖出_指数)
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_IDXEQ); 
            	} else if ("MR_ZB".equals(rs.getString("FYWBZ")) || "MC_ZB".equals(rs.getString("FYWBZ"))) {
            		//业务标识（YWBZ）为MR_ZS(买入_指标)或MC_ZS(卖出_指标)
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_TAGEQ); 
            	} else if ("YYSG_ZS".equals(rs.getString("FYWBZ")) || 
            		       "YYSG_ZB".equals(rs.getString("FYWBZ")) ||
            		       "YYSG".equals(rs.getString("FYWBZ"))) {
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_YYSG); 
            	}
            	
            } else if ("XG".equals(rs.getString("FZQBZ"))) {
            	//---add by songjie 2012.01.06 需求 STORY 2104  QDV4赢时胜(上海开发部)2012年01月03日01_A start---//
            	if("SG".equals(rs.getString("FYWBZ"))){
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_XGSG); 
            	}
            	else if("FK".equals(rs.getString("FYWBZ"))){
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_XGFK); 
            	}
            	else if("ZQ".equals(rs.getString("FYWBZ")) ||
            	   "ZQ_ZS".equals(rs.getString("FYWBZ"))	){
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_XGZQ); 
            	}
            	else if("XGZF".equals(rs.getString("FYWBZ")) ||
            	   "XGZF_ZS".equals(rs.getString("FYWBZ"))	){
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_XGZF); 
            	}
            	else if("ZQ_PSZF".equals(rs.getString("FYWBZ"))){
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_PSZFZQ); 
            	}
            	else if("ZQ_SZ".equals(rs.getString("FYWBZ")) ||
            	   "ZQ_SZ_ZS".equals(rs.getString("FYWBZ"))){
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_SHZQ); 
            	}
            	else if("XGLT".equals(rs.getString("FYWBZ")) || 
            	   "XGLT_ZS".equals(rs.getString("FYWBZ")) ||
            	   "XGLT_ZB".equals(rs.getString("FYWBZ"))){
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_ZLT); 
            	}
            	else if("KPSL".equals(rs.getString("FYWBZ"))){
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_KPSL); 
            	}
            	//---add by songjie 2012.01.06 需求 STORY 2104  QDV4赢时胜(上海开发部)2012年01月03日01_A end---//
            	//证券标识（ZQBZ）为XG(新股)
            	//subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_XG); 
            	} else if ("XZ".equals(rs.getString("FZQBZ"))) {
            	//证券标识（ZQBZ）为XG(新债)
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_XZ); 
            	} else if ("HG".equals(rs.getString("FZQBZ"))) {
            	//证券标识（ZQBZ）为HG(回购)
            		if ("MRHG".equals(rs.getString("FYWBZ")) || "MDMRHG".equals(rs.getString("FYWBZ"))) {
            		//证券标识（ZQBZ）为MR_HG(买入_回购)或MDMRHG(买断买入回购)
            			subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_AntiRepo); 
            	} else if ("MCHG".equals(rs.getString("FYWBZ")) || "MDMCHG".equals(rs.getString("FYWBZ"))) {
            		//证券标识（ZQBZ）为MC_HG(卖出_回购)或MDMCHG(买断卖出回购)
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_SellRepo); 
            	}
            } else if ("ZQ".equals(rs.getString("FZQBZ"))) {
            	//证券标识（ZQBZ）为ZQ(债券)
            	if ("C".equals(rs.getString("FTZBZ"))) {
            		//投资标识（FTZBZ）为C
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_C);
            	} else if ("F".equals(rs.getString("FTZBZ"))) {
            		//投资标识（FTZBZ）为F
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_F);
            	} else if ("S".equals(rs.getString("FTZBZ"))) {
            		//投资标识（FTZBZ）为S
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_S);
            	} else if ("X".equals(rs.getString("FTZBZ"))) {
            		//投资标识（FTZBZ）为X
            		subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_X);
            	}
            }
			// add by songjie 2010.12.13 633 南方东英2010年12月09日01_B
			else if ("QY".equals(rs.getString("FZQBZ"))) {
				//---add by songjie 2012.01.09 STORY 2104 QDV4赢时胜(上海开发部)2012年01月03日01_A start---//
				if ("PG".equals(rs.getString("FYWBZ"))){
					subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_PG);
				}
				if ("PGJK".equals(rs.getString("FYWBZ")) ||
					"PGJK_ZS".equals(rs.getString("FYWBZ")) ||
					"PGJK_ZB".equals(rs.getString("FYWBZ"))	){
					//subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_PGJK);
				}
				if ("PX_GP".equals(rs.getString("FYWBZ"))){
					subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_PX);
				}
				if ("SG".equals(rs.getString("FYWBZ")) || "SG_ZS".equals(rs.getString("FYWBZ"))){
					subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_SG);
				}
				//---add by songjie 2012.01.09 STORY 2104 QDV4赢时胜(上海开发部)2012年01月03日01_A end---//
				//---delete songjie 2012.01.09 STORY 2104 QDV4赢时胜(上海开发部)2012年01月03日01_A start---//
//				if ("PG".equals(rs.getString("FYWBZ"))
//						|| "PGJK".equals(rs.getString("FYWBZ"))
//						|| "PX_GP".equals(rs.getString("FYWBZ"))
//						|| "SG".equals(rs.getString("FYWBZ"))) {
//					subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_CEQ);
//				} else if ("PGJK_ZS".equals(rs.getString("FYWBZ"))
//						|| "SG_ZS".equals(rs.getString("FYWBZ"))) {
//					subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_IDXEQ);
//				} else if ("PGJK_ZB".equals(rs.getString("FYWBZ"))) {
//					subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_TAGEQ);
//				} 
				//---delete songjie 2012.01.09 STORY 2104 QDV4赢时胜(上海开发部)2012年01月03日01_A end---//
				else if ("PX_ZQ".equals(rs.getString("FYWBZ"))) {// 证券标识（ZQBZ）为ZQ(债券)
					if ("C".equals(rs.getString("FTZBZ"))) {
						// 投资标识（FTZBZ）为C
						subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_C);
					} else if ("F".equals(rs.getString("FTZBZ"))) {
						// 投资标识（FTZBZ）为F
						subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_F);
					} else if ("S".equals(rs.getString("FTZBZ"))) {
						// 投资标识（FTZBZ）为S
						subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_S);
					} else if ("X".equals(rs.getString("FTZBZ"))) {
						// 投资标识（FTZBZ）为X
						subTrade.setAttrClsCode(YssOperCons.YSS_SXFL_X);
					}
				}
			}
            //add by songjie 2010.12.13 633  南方东英2010年12月09日01_B
            
            //----------------
            operDeal =new BaseOperDeal();
            operDeal.setYssPub(pub);
            //---add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
           //上海过户库接口导入配股缴款数据时 结算日期 = 根据证券信息设置中的节假日群推算出的 权益数据缴款截止日之后的第二个交易日
            if("CG".equals(rs.getString("FSZSH")) && "PGJK".equals(rs.getString("FYWBZ"))){
            	dSettleDate=getExpireDateOfRightIssue(rs.getString("FPORTCODE"),rs.getString("FZQDM"));
            	dSettleDate=operDeal.getWorkDay(getHolidayCode(rs.getString("FZQDM")),dSettleDate,2);   
            //add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 如果是深交所公司债大宗交易数据，则 T+0交收
            } else if("CS".equals(rs.getString("FSZSH")) && rs.getString("FJYFS").equals("DZ") && rs.getString("FSubCatCode").equals("FI08")){
            	dSettleDate = rs.getDate("FDate");
            } else{           	 
                 dSettleDate = operDeal.getWorkDay(rs.getString("FZQDM"),rs.getDate("FDate"));          
            }    
            //---add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
            subTrade.setMatureDate(YssFun.formatDate("9998-12-31"));
            subTrade.setMatureSettleDate(YssFun.formatDate("9998-12-31"));
            eachRate =new EachRateOper();
            eachRate.setYssPub(pub);
            eachRate.setSCuryCode(rs.getString("FTradeCury"));
            eachRate.setDRateDate(rs.getDate("FDate"));
            eachRate.setSPortCode(rs.getString("FPortCode"));
            if(rs.getString("FTradeCury")==null){// by leeyu 20090814
                throw new YssException("证券代码【"+rs.getString("FZQDM")+"】币种为空，请检查!");
            }
            eachRate.getOperValue("rate");
            subTrade.setBaseCuryRate(eachRate.getDBaseRate());
            subTrade.setPortCuryRate(eachRate.getDPortRate());
            subTrade.setFactBaseRate(subTrade.getBaseCuryRate());
            subTrade.setFactPortRate(subTrade.getPortCuryRate());
            YssCost cost =new YssCost();
            //bug 4266 update by zhouwei 20120217 证券标志不为回购，且业务标识为买入的，买入金额作为成本  start---------
            if(!"HG".equals(rs.getString("FZQBZ")) && rs.getString("FYWBZ").startsWith("MR")){
            	 cost.setCost(rs.getDouble("fbje"));
                 cost.setMCost(rs.getDouble("fbje"));
                 cost.setVCost(rs.getDouble("fbje"));
                 cost.setBaseCost(rs.getDouble("fbje"));
                 cost.setBaseMCost(rs.getDouble("fbje"));
                 cost.setBaseVCost(rs.getDouble("fbje"));
                 cost.setPortCost(rs.getDouble("fbje"));
                 cost.setPortMCost(rs.getDouble("fbje"));
                 cost.setPortVCost(rs.getDouble("fbje"));
            }else{
            	cost.setCost(0);
                cost.setMCost(0);
                cost.setVCost(0);
                cost.setBaseCost(0);
                cost.setBaseMCost(0);
                cost.setBaseVCost(0);
                cost.setPortCost(0);
                cost.setPortMCost(0);
                cost.setPortVCost(0);
            }    
          //update by zhouwei 20120217 证券标志不为回购，且业务标识为买入的，买入金额作为成本  end---------
            subTrade.setCost(cost);
            //by leeyu 20090814 采用旧的现金账户链接
            cashAccLink =new BaseCashAccLinkDeal();
            cashAccLink.setYssPub(pub);
            cashAccLink.setLinkParaAttr(subTrade.getInvMgrCode(),rs.getString("FPortCode"),securityCode,rs.getString("FBrokerCode"),tradeTypeCode,rs.getDate("FInDate"));
            CashAccountBean cashAccount =cashAccLink.getCashAccountBean();
            if(cashAccount!=null){
                subTrade.setCashAcctCode(cashAccount.getStrCashAcctCode());
            }else{
                subTrade.setCashAcctCode(" ");
            }
            // by leeyu 20090814 修改
            subTrade.setRateDate(YssFun.formatDate(rs.getDate("FDate")));
            subTrade.setBargainDate(YssFun.formatDate(rs.getDate("FDate")));
            subTrade.setBargainTime("00:00:00");
            subTrade.setSettleDate(YssFun.formatDate(dSettleDate));
            subTrade.setSettleTime("00:00:00");
            subTrade.setFactCashAccCode(subTrade.getCashAcctCode());    //现金帐户一致
            //edit by songjie 2012.01.19 BUG 3708 QDV4赢时胜(上海开发部)2012年1月19日01_B 设置席位代码  非 席位号
            subTrade.setTradeSeatCode(rs.getString("FSeatCode"));
            subTrade.setStockholderCode(rs.getString("FGddm"));
            subTrade.setExRate(1);
            subTrade.setAutoSettle("1");                                //结算标志
            subTrade.setOldAllotAmount(subTrade.getTradeAmount());      //与交易数量相同
            subTrade.setBailMoney(0);
            subTrade.setSettleState("0");                               //未结算
            subTrade.setFactSettleDate(subTrade.getSettleDate());
            subTrade.setDataSource(1);                                   //自动
            //add by songjie 2009.09.24
            if(securitySign.equals("QY")){//若为权益数据
                subTrade.setDsType("ZD_QY"); //则设置数据来源标志为ZD_QY
            }
            else{
            subTrade.setDsType("ZD_JK");     //数据来源标识，此为 接口－自动  by leeyu 20090901
            }
            //add by songjie 2009.09.24 story 1578 update by zhouwei 20111103 12项费用是全以YSS开头的固定费用代码 
            if(rs.getString("FExchangeCode").equalsIgnoreCase("CG")){   //上交所
                subTrade.setFFeeCode1("YSS_SHYJ"); //佣金
                subTrade.setFFeeCode2("YSS_SHJS"); //经手费
                subTrade.setFFeeCode3("YSS_SHYH"); //印花税
                subTrade.setFFeeCode4("YSS_SHZG"); //征管费
                subTrade.setFFeeCode5("YSS_SHGH"); //过户费
                subTrade.setFFeeCode6("YSS_SHJSF"); //其他费

            } else { //深交所
                subTrade.setFFeeCode1("YSS_SZYJ"); //佣金
                subTrade.setFFeeCode2("YSS_SZJS"); //经手费
                subTrade.setFFeeCode3("YSS_SZYH"); //印花税
                subTrade.setFFeeCode4("YSS_SZZG"); //征管费
                subTrade.setFFeeCode5("YSS_SZGH"); //过户费
                subTrade.setFFeeCode6("YSS_SZJSF"); //其他费
                /* 01-	上海经手费  YSS_SHJS
                    02-	上海证管费   YSS_SHZG
                    03-	上海过户费   YSS_SHGH
                    04-	上海印花税   YSS_SHYH
                    05-	上海佣金        YSS_SHYJ
                    06-	深圳经手费   YSS_SZJS
                    07-	深圳证管费   YSS_SZZG
                    08-	深圳过户费   YSS_SZGH
                    09-	深圳印花税   YSS_SZYH
                    10-	深圳佣金        YSS_SZYJ
                    11-	上海结算费   YSS_SHJSF
                    12-	深圳结算费   YSS_SZJSF
                 */
            }
            subTrade.setFFeeCode7("");
            subTrade.setFFeeCode8("");
            subTrade.setFTradeFee7(0);
            subTrade.setFTradeFee8(0);
            if (rs.getString("FZQBZ").equalsIgnoreCase("HG")) { //回购
            	//===add by xuxuming,20091112.在此处添加　回购类型的结算日期，到期日期，到期结算日期。=====================
            	PurchaseTradeAdmin purTrade = new PurchaseTradeAdmin();
            	purTrade.setYssPub(pub);
            	PurchaseTradeBean purBean = new PurchaseTradeBean();
            	purBean.setYssPub(pub);
            	purBean.setSecurityCode(subTrade.getSecurityCode());//证券代码
            	purBean.setBargainDate(subTrade.getBargainDate());//交易日期
            	purTrade.setPurTradeBean(purBean);
            	String allDate = "";
            	allDate = purTrade.getDurability();//所获取的日期是按：到期日期\t到期结算日期\t结算日期
            	if(allDate!=null&&!"".equals(allDate)){
            		String tmpDate[] = allDate.split("\t");//依次取出日期
            		subTrade.setMatureDate(tmpDate[0]);//到期日期
            		subTrade.setMatureSettleDate(tmpDate[1]);//到期结算日期
            		subTrade.setSettleDate(tmpDate[2]);//结算日期
            	}
            	//===============end=========================================================================
                if(rs.getString("FYwbz").equalsIgnoreCase("MRHG") || rs.getString("FYwbz").equalsIgnoreCase("MRHG_QYZQ")){
                    //买入回购(逆回购)
                    tradeTypeCode=YssOperCons.YSS_JYLX_NRE;
//                    subTrade.setTradeAmount(rs.getDouble("FSSL"));  //数量
//                    subTrade.setTradeMoney(rs.getDouble("FSJe"));   //金额
//                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));   //佣金
//                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));  //经手费
//                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));  //印花税
//                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));  //征管费
//                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));  //过户费
//                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));  //其他费
//                    subTrade.setFTradeFee8(0);
//                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                    //=========add by xuxuming,20091112================
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                    //===================end============================
                }else if(rs.getString("FYwbz").equalsIgnoreCase("MCHG")|| rs.getString("FYwbz").equalsIgnoreCase("MCHG_QYZQ")){
                    //卖出回购(正回购)
                    tradeTypeCode=YssOperCons.YSS_JYLX_ZRE;
//                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
//                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
//                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
//                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
//                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
//                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
//                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
//                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
//                    subTrade.setFTradeFee8(0);
//                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                    //==========add by xuxuming,20091112==================
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));  //数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));   //金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));   //佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));  //经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));  //印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));  //征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));  //过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));  //其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                    //============end=================================
                }else if(rs.getString("FYwbz").equalsIgnoreCase("MDMRHG")){//modify by zhouwei 20120411 买入卖出弄反了
                    //买断式买入回购
                    tradeTypeCode=YssOperCons.YSS_JYLX_REMR;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                }else if(rs.getString("FYwbz").equalsIgnoreCase("MDMCHG")){
                    //买断式卖出回购
                    tradeTypeCode=YssOperCons.YSS_JYLX_REMC;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                }else{
                    subTrade =null;
                    return subTrade; //添加，若有回购的其他类型,如卖出回购到期，不用导入到业务资料表 by leeyu 20090808
                }
                subTrade.setAccruedInterest(rs.getDouble("FHGGain"));//回购收益
//                subTrade.setMatureDate(YssFun.formatDate(operDeal.getWorkDay(rs.getString("FZQDM"),YssFun.addDay(rs.getDate("FDate"),getPurChaseDay(rs)))));
//                subTrade.setMatureSettleDate(YssFun.formatDate(operDeal.getWorkDay(rs.getString("FZQDM"),YssFun.toDate(subTrade.getMatureDate()))));//实际到期结算日在实际到期日基础上加1天
            }else if (rs.getString("FZQBZ").equalsIgnoreCase("XG")) {                                               //新股
                if(rs.getString("FYwbz").equalsIgnoreCase("SG")){
                    //新股申购
                    tradeTypeCode=YssOperCons.YSS_JYLX_XGSG;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款

                }else if(rs.getString("FYwbz").equalsIgnoreCase("ZQ_PSZF")){
                    //配售增发中签
                    tradeTypeCode=YssOperCons.YSS_JYLX_ZFZQ;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                }else if(rs.getString("FYwbz").startsWith("ZQ_SZ")){
                    //市值配售上海中签
                    tradeTypeCode=YssOperCons.YSS_JYLX_XGSZPS_SG;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                }else if(rs.getString("FYwbz").startsWith("ZQ")&&!(rs.getString("FYwbz").endsWith("PSZF") || rs.getString("FYwbz").startsWith("ZQ_SZ"))){
                    //新股中签
                    //tradeTypeCode=YssOperCons.YSS_JYLX_XGZQ;
                    tradeTypeCode=YssOperCons.YSS_JYLX_WSZQ;//将新股中签的交易类型代码更改为41 by leeyu QDII4.1赢时胜上海2010年03月02日03_B MS00899 20100303
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款

                }else if(rs.getString("FYwbz").startsWith("XGZF")){
                    //新股增发
                    tradeTypeCode=YssOperCons.YSS_JYLX_ZF;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款

                }else if(rs.getString("FYwbz").startsWith("FK")){
                    //新股中签返款
                    tradeTypeCode=YssOperCons.YSS_JYLX_ZQFK;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                }else if(rs.getString("FYwbz").startsWith("XGLT")){
                    //新股流通
                    tradeTypeCode=YssOperCons.YSS_JYLX_XGLT;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款

                }
            }else if (rs.getString("FZQBZ").equalsIgnoreCase("GP")) {                                                 //股票
                // 删除对所属分类的设置，前面已统一设置 fangjiang 2010.09.13 MS01138 
                if(rs.getString("FYwbz").equalsIgnoreCase("KZZGP")){
                    //债转股 存入债券数据的关联表中
                    tradeTypeCode=" ";
                    subTrade =null;
                    return subTrade; //如果是债转股,股票的数据,则不执行插入业务资料操作,其数据插入到交易关联子表中.
                }else if(rs.getString("FYwbz").startsWith("YYSG")){
                    //要约收购
                    tradeTypeCode=YssOperCons.YSS_JYLX_EQYYSG;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                }else if(rs.getString("FYwbz").equalsIgnoreCase("SH_ETF") ||rs.getString("FYwbz").equalsIgnoreCase("SG_ETF")){
                    subTrade =null;
                    return subTrade;//ETF基金关联的数据需插入到业务资料关联的数据子表中
                }
            }else if (rs.getString("FZQBZ").equalsIgnoreCase("ZQ")) {                                            //债券
                if(rs.getString("FYwbz").equalsIgnoreCase("KZZGP")){
                    //债转股
                    tradeTypeCode=YssOperCons.YSS_JYLX_ZZG;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    //delete by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
//                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                    subTrade.setNum(getNum(tradeTypeCode));
                    tmpClass tmpcla= this.insertRelaTrade(rs,tradeTypeCode,subTrade.getNum());//插入数据到交易关联表中
                    //edit by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
                    //将股票的补差金额与债券利息的和存入到债券的实收实付金额中
                    subTrade.setTotalCost(YssD.add(tmpcla.dJE, rs.getDouble("FSGZLX")));//将股票的补差金额存入到债券的实收实付金额中
                    YssCost yssCost =calcBondCost(subTrade);
                    if(yssCost!=null){
                        subTrade.setTradeMoney(YssFun.roundIt(yssCost.getCost(),2));//将算出来的成本插入到成交金额字段中
                        subTrade.setTradePrice(YssD.div(subTrade.getTradeMoney(),subTrade.getTradeAmount()));//用金额/数量算出价格
                    }
                }else if(rs.getString("FYwbz").equalsIgnoreCase("KZZHS")){
                    //可转债回售
                    tradeTypeCode=YssOperCons.YSS_JYLX_KZZHS;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                }
                subTrade.setAccruedInterest(rs.getDouble("FSGZLx"));//债券利息
            }else if (rs.getString("FZQBZ").equalsIgnoreCase("XZ")) {                                     //新债
                if(rs.getString("FYwbz").startsWith("SG")){
                    //新债申购
                    tradeTypeCode=YssOperCons.YSS_JYLX_XZSG;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                    subTrade.setAccruedInterest(rs.getDouble("FBGZLx"));//债券利息
                }else if(rs.getString("FYwbz").startsWith("ZQ")){
                    //新债中签
                    tradeTypeCode=YssOperCons.YSS_JYLX_XZWSZQ;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                    subTrade.setAccruedInterest(rs.getDouble("FBGZLx"));//债券利息
                }else if(rs.getString("FYwbz").startsWith("FK")){
                    //新债中签返款
                    tradeTypeCode=YssOperCons.YSS_JYLX_XZZQFK;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                    subTrade.setAccruedInterest(rs.getDouble("FSGZLx"));//债券利息
                }else if(rs.getString("FYwbz").startsWith("XZLT")){
                    //新债流通
                    tradeTypeCode=YssOperCons.YSS_JYLX_XZLT;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                    subTrade.setAccruedInterest(rs.getDouble("FBGZLx"));//债券利息
                }else if(rs.getString("FYwbz").endsWith("XZ")){
                    //老股东配售
                    tradeTypeCode=YssOperCons.YSS_JYLX_ZQLGDPS;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                    subTrade.setAccruedInterest(rs.getDouble("FBGZLx"));//债券利息
                }
            }else if (rs.getString("FZQBZ").equalsIgnoreCase("JJ")) {                                               //基金
            	// 删除对所属分类的设置，前面已统一设置 fangjiang 2010.09.13 MS01138 
                if(rs.getString("FYwbz").equalsIgnoreCase("ETFTK")){
                    //ETF基金退补款
                    tradeTypeCode=YssOperCons.YSS_JYLX_TRETFTBK;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                }else if(rs.getString("FYwbz").equalsIgnoreCase("XJCE")){
                    //ETF基金申赎现金差额存入申赎数据的子页面中
                    tradeTypeCode=" ";
                    subTrade =null;
                    return subTrade;//ETF现金差额数据需将数据插入到交易关联表中
                }else if(rs.getString("FYwbz").startsWith("SG")){
                    //基金申购
                    tradeTypeCode=YssOperCons.YSS_JYLX_SGou;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                    if(rs.getString("FYwbz").equalsIgnoreCase("SG_ETF")){
                        //ETF基金申购
                        subTrade.setNum(getNum(tradeTypeCode));//在这里获取业务编号
                        this.insertRelaTrade(rs,tradeTypeCode,subTrade.getNum());
                    }
                }else if(rs.getString("FYwbz").startsWith("RG")){
                    //基金认购
                    tradeTypeCode=YssOperCons.YSS_JYLX_RG;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                }else if(rs.getString("FYwbz").startsWith("SH")){
                    //基金赎回
                    tradeTypeCode=YssOperCons.YSS_JYLX_SH;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                    if(rs.getString("FYwbz").equalsIgnoreCase("SG_ETF")){
                        //ETF基金赎回
                        subTrade.setNum(getNum(tradeTypeCode));//在这里获取业务编号
                        this.insertRelaTrade(rs,tradeTypeCode,subTrade.getNum());
                    }
                }
            }else if (rs.getString("FZQBZ").equalsIgnoreCase("QZ")) {                                                    //权证
                if(rs.getString("FYwbz").equalsIgnoreCase("MR_RGQZ")){
                    //买入认购权证
                    tradeTypeCode=YssOperCons.YSS_JYLX_Buy;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee7(rs.getDouble("FBFXJ"));//风险金
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                }else if(rs.getString("FYwbz").equalsIgnoreCase("MC_RGQZ")){
                    //卖出认购权证
                    tradeTypeCode=YssOperCons.YSS_JYLX_Sale;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款

                }else if(rs.getString("FYwbz").equalsIgnoreCase("MR_RZQZ")){
                    //买入认沽权证
                    tradeTypeCode=YssOperCons.YSS_JYLX_Buy;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee7(rs.getDouble("FBFXJ"));//风险金
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                }else if(rs.getString("FYwbz").equalsIgnoreCase("MC_RZQZ")){
                    //卖出认沽权证
                    tradeTypeCode=YssOperCons.YSS_JYLX_Sale;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款

                }else if(rs.getString("FYwbz").equalsIgnoreCase("XQ_RGQZ")){
                    //认购行权（产生出标的证券的数据后，存入其关联信息表中）
                    tradeTypeCode=YssOperCons.YSS_JYLX_RGUXQ;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setNum(this.getNum(tradeTypeCode));
                    convertWarrant(subTrade,tradeTypeCode);
                    this.insertRelaTrade(rs,tradeTypeCode,subTrade.getNum());
                }else if(rs.getString("FYwbz").equalsIgnoreCase("XQ_RZQZ")){
                    //认沽行权（产生出标的证券的数据后，存入其关联信息表中）
                    tradeTypeCode=YssOperCons.YSS_JYLX_RGOXQ;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setNum(this.getNum(tradeTypeCode));
                    convertWarrant(subTrade,tradeTypeCode);
                    this.insertRelaTrade(rs,tradeTypeCode,subTrade.getNum());
                }
            }else if (rs.getString("FZQBZ").equalsIgnoreCase("QY")) { //权益
                if(rs.getString("FYwbz").equalsIgnoreCase("PX_GP")){
                    //分发派息
                    tradeTypeCode=YssOperCons.YSS_JYLX_PX;
                    subTrade.setTradeAmount(0);//数量
                    subTrade.setTradeMoney(0);//金额
                    subTrade.setTradePrice(0);
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    //add by songjie 2010.03.11 MS00905 QDII4.1赢时胜上海2010年03月10日02_B
                    subTrade.setAccruedInterest(rs.getDouble("FSSSJE"));//应计利息
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                    //delete by songjie 2010.04.01 国内:MS00964 QDII4.1赢时胜上海2010年03月31日01_B
//                    if(rs.getString("FSZSH").equalsIgnoreCase("CG")){//当交易所为上海时
                    dSettleDate = pubMethod.getDividSecRightDate(rs.getString("FZQDM"),rs.getDate("FDate"));
                    //delete by songjie 2010.04.01 国内:MS00964 QDII4.1赢时胜上海2010年03月31日01_B
                    //                    }else if(rs.getString("FSZSH").equalsIgnoreCase("CS")){//当交易所为深圳时
//                        dSettleDate = rs.getDate("FDate");
//                    }
                    //delete by songjie 2010.04.01 国内:MS00964 QDII4.1赢时胜上海2010年03月31日01_B
                    subTrade.setSettleDate(YssFun.formatDate(dSettleDate));
                    subTrade.setFactSettleDate(YssFun.formatDate(dSettleDate));
                }else if(rs.getString("FYwbz").equalsIgnoreCase("DZ_PX")){
                    //分红派息到账
                    tradeTypeCode= YssOperCons.YSS_JYLX_PXDZ;
                    subTrade.setTradeAmount(0);
                    subTrade.setTradeMoney(0);
                    subTrade.setTradePrice(0);
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                }else if(rs.getString("FYwbz").equalsIgnoreCase("PX_ZQ")){
                    //债券派息
                	
                	//add by zhangfa  MS01336    将债券派息的业务与分红派息业务的交易方式进行区分    
                	 tradeTypeCode = YssOperCons.YSS_JYLX_ZQPX;
                	 subTrade.setTradeAmount(0);//数量
                     subTrade.setTradeMoney(0);//金额
                     subTrade.setTradePrice(0);
                     subTrade.setAccruedInterest(rs.getDouble("FSSSJE"));//应计利息
                    //--------------------------------------------------------------------- 
                     
                	//tradeTypeCode =YssOperCons.YSS_JYLX_PX;
                    //subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    //subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                }else if(rs.getString("FYwbz").equalsIgnoreCase("PX_JJ")){
                    //基金派息
                    tradeTypeCode= YssOperCons.YSS_JYLX_PX;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                }else if(rs.getString("FYwbz").equalsIgnoreCase("PG")){
                    //配股
                	//MS01354    add by zhangfa 20100719    QDV4赢时胜(上海)2010年06月25日01_A 
                    tradeTypeCode=YssOperCons.YSS_JYLX_PG;
                    subTrade.setJkdr("1");
                    //-----------------------------------------------------------------------
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                    
                }else if(rs.getString("FYwbz").equalsIgnoreCase("QZ")){
                    //权证送配
                    tradeTypeCode=YssOperCons.YSS_JYLX_QZSP;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                  //MS01354    add by zhangfa 20100719    QDV4赢时胜(上海)2010年06月25日01_A 
                    subTrade.setJkdr("1");
                  //----------------------------------------------------------------------- 

                }else if(rs.getString("FYwbz").equalsIgnoreCase("XJDJ")){
                    //现金对价
                    tradeTypeCode=YssOperCons.YSS_JYLX_XJDJ;
                    subTrade.setTradeAmount(0);//数量 //----- modify by wangzuochun 2010.05.28 MS01140 日终处理权益处理出来的现金对价数据与国内接口处理出来的不一致  QDV4国内（测试）2010年04月28日01_B 
                    subTrade.setTradeMoney(0);//金额 //----- modify by wangzuochun 2010.05.28 MS01140 日终处理权益处理出来的现金对价数据与国内接口处理出来的不一致  QDV4国内（测试）2010年04月28日01_B 
                    subTrade.setNum(this.getNum(tradeTypeCode)); //----- modify by wangzuochun 2010.05.28 MS01140 日终处理权益处理出来的现金对价数据与国内接口处理出来的不一致  QDV4国内（测试）2010年04月28日01_B 
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    //add by songjie 2010.03.11 MS00905 QDII4.1赢时胜上海2010年03月10日02_B
                    subTrade.setAccruedInterest(rs.getDouble("FSSSJE"));//应计利息
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                    
                    this.insertRelaTrade(rs,tradeTypeCode,subTrade.getNum()); //----- modify by wangzuochun 2010.05.28 MS01140 日终处理权益处理出来的现金对价数据与国内接口处理出来的不一致  QDV4国内（测试）2010年04月28日01_B 
                    
                }else if(rs.getString("FYwbz").equalsIgnoreCase("DZ_XJDJ")){
                    //现金对价到账
                    tradeTypeCode=YssOperCons.YSS_JYLX_XJDJDZ;
                    subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FSJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FSYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FSJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FSYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FSZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FSGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FSQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FSSSJE"));//清算款
                }else if(rs.getString("FYwbz").startsWith("SG")){
                    //送股
                    tradeTypeCode=YssOperCons.YSS_JYLX_SG;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款

                }else if(rs.getString("FYwbz").startsWith("PGJK")){
                    //配股缴款
                    tradeTypeCode=YssOperCons.YSS_JYLX_PGJK;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                  //MS01354    add by zhangfa 20100719    QDV4赢时胜(上海)2010年06月25日01_A 
                    subTrade.setJkdr("1");
                  //----------------------------------------------------------------------- 

                }else if(rs.getString("FYwbz").startsWith("GFDJ")){
                    //股份对价
                    tradeTypeCode= YssOperCons.YSS_JYLX_GFDJ;
                    subTrade.setTradeAmount(rs.getDouble("FBSL"));//数量
                    subTrade.setTradeMoney(rs.getDouble("FBJe"));//金额
                    subTrade.setFTradeFee1(rs.getDouble("FBYJ"));//佣金
                    subTrade.setFTradeFee2(rs.getDouble("FBJSF"));//经手费
                    subTrade.setFTradeFee3(rs.getDouble("FBYHS"));//印花税
                    subTrade.setFTradeFee4(rs.getDouble("FBZGF"));//征管费
                    subTrade.setFTradeFee5(rs.getDouble("FBGHF"));//过户费
                    subTrade.setFTradeFee6(rs.getDouble("FBQTF"));//其他费
                    subTrade.setFTradeFee8(0);
                    subTrade.setTotalCost(rs.getDouble("FBSFJE"));//清算款
                }
            }
            //另外判断的
            if(rs.getString("FYwbz").startsWith("MR") &&!(rs.getString("FZQBZ").equalsIgnoreCase("HG") || rs.getString("FZQBZ").equalsIgnoreCase("QZ"))){
                //买入
                tradeTypeCode=YssOperCons.YSS_JYLX_Buy;
                subTrade.setTradeAmount(rs.getDouble("FBSL")); //数量
                subTrade.setTradeMoney(rs.getDouble("FBJe")); //金额
                subTrade.setFTradeFee1(rs.getDouble("FBYJ")); //佣金
                subTrade.setFTradeFee2(rs.getDouble("FBJSF")); //经手费
                subTrade.setFTradeFee3(rs.getDouble("FBYHS")); //印花税
                subTrade.setFTradeFee4(rs.getDouble("FBZGF")); //征管费
                subTrade.setFTradeFee5(rs.getDouble("FBGHF")); //过户费
                subTrade.setFTradeFee6(rs.getDouble("FBQTF")); //其他费
                subTrade.setFTradeFee8(0);
                subTrade.setTotalCost(rs.getDouble("FBSFJE")); //清算款
                subTrade.setAccruedInterest(rs.getDouble("FBGZLx"));//债券利息或其他利息
            }
            if(rs.getString("FYwbz").startsWith("MC") &&!(rs.getString("FZQBZ").equalsIgnoreCase("HG") || rs.getString("FZQBZ").equalsIgnoreCase("QZ"))){
                //卖出
                tradeTypeCode=YssOperCons.YSS_JYLX_Sale;
                subTrade.setTradeAmount(rs.getDouble("FSSL"));//数量
                subTrade.setTradeMoney(rs.getDouble("FSJe")); //金额
                subTrade.setFTradeFee1(rs.getDouble("FSYJ")); //佣金
                subTrade.setFTradeFee2(rs.getDouble("FSJSF")); //经手费
                subTrade.setFTradeFee3(rs.getDouble("FSYHS")); //印花税
                subTrade.setFTradeFee4(rs.getDouble("FSZGF")); //征管费
                subTrade.setFTradeFee5(rs.getDouble("FSGHF")); //过户费
                subTrade.setFTradeFee6(rs.getDouble("FSQTF")); //其他费
                subTrade.setFTradeFee8(0);
                subTrade.setTotalCost(rs.getDouble("FSSSJE")); //清算款
                subTrade.setAccruedInterest(rs.getDouble("FSGZLx"));//债券利息或其他利息
            }
            if(rs.getString("FYwbz").startsWith("XJTD_SG") && (rs.getString("FZQBZ").equalsIgnoreCase("JJ") || rs.getString("FZQBZ").equalsIgnoreCase("GP"))){
                //ETF基金申购现金替代存入申赎数据的子页面中
                tradeTypeCode=" ";
                subTrade =null;
                return subTrade;// ETF基金申购现金替代数据插入到业务关联子表中.
            }
            if(rs.getString("FYwbz").startsWith("XJTD_SH") && (rs.getString("FZQBZ").equalsIgnoreCase("JJ") || rs.getString("FZQBZ").equalsIgnoreCase("GP"))){
                //ETF基金赎回现金替代存入申赎数据的子页面中
                tradeTypeCode=" ";
                subTrade =null;
                return subTrade; //ETF基金赎回现金替代数据插入到业务关联子表中.
            }
            subTrade.setTradeCode(tradeTypeCode);       //在这里将转换的业务类型添进去
            subTrade.setFactSettleMoney(subTrade.getTotalCost());       //实际结算金额,与FtotalCost一致
            if(subTrade.getNum().trim().length()==0){   //如果业务编号在上面已经取过了,这里就不再取了.
                subTrade.setNum(getNum(tradeTypeCode)); //设置业务资料的交易编号
            }
            if(subTrade.getTradeCode()==null||subTrade.getTradeCode().trim().length()==0){
                return null; //这里添加如果处理的数据没有交易类型，说明证券标志或业务标志不在处理范围内，这里不插入到交易子表
            }
        }catch(Exception ex){
        	//modify by zhangfa 20101227 BUG #674 读数处理方式〗未设置“分红派息提前一天入帐”时，读数报错 
        	String strMes="";
        	if(ex.getMessage().equals("&&导入"+securityCode+"分红派息数据,文件日期应与权益登记日期一致,请重新设置!")){
        		strMes="\n\n\n"+ex.getMessage().substring(2, ex.getMessage().length());
        	}
            throw new YssException("证券代码【"+securityCode+"】证券标志【"+securitySign+"】业务标志【"+tradeSign+"】业务转换出错!"+strMes,ex);
            //-------------------end 20101227-------------------------------------------------------
        }
        return subTrade;
    }
	
    //story 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A 
	//add by zhouwei 20120516 
	//获取配股权益信息中的缴款截止日
    private  Date getExpireDateOfRightIssue(String sportCode,String securityCode) throws YssException{
		Date expireDate=new Date();;
		ResultSet rs=null;
		String sql="";
		try{	
            sql = " select * from " + pub.yssGetTableName("Tb_Data_RightsIssue") +
            	" where FTSecurityCode = " + dbl.sqlString(securityCode) + " and FCheckState = 1 ";
            rs = dbl.openResultSet(sql); //在配股权益表中查询相关证券代码的数据
            if(rs.next()){
            	expireDate=rs.getDate("FExpirationDate");              
            }
		}catch (Exception e) {
			throw new YssException("获取配股权益信息中的缴款截止日出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return expireDate;
	
    }
    //story 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A 
	//add by zhouwei 20120516 获取证券的节假日群信息
    private  String getHolidayCode(String securityCode) throws YssException{
		String holidayCode="";
		ResultSet rs=null;
		String sql="";
		try{	
            sql = " select * from " + pub.yssGetTableName("TB_PARA_SECURITY") +
            	" where FSecurityCode = " + dbl.sqlString(securityCode) + " and FCheckState = 1 ";
            rs = dbl.openResultSet(sql); //在配股权益表中查询相关证券代码的数据
            if(rs.next()){
            	holidayCode=rs.getString("FholidaysCode");              
            }
		}catch (Exception e) {
			throw new YssException("获取证券信息的节假日群出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return holidayCode;
	
    }
    /**
     * 计算业务资料的编号
     * @param tradeType String
     * @return String
     * @throws YssException
     */
    private String getNum(String tradeType) throws YssException{
        String sMaxNum="";
        if(hmNum ==null){
            hmNum = new HashMap();
            sMaxNum=dbFun.getNextInnerCode(pub.yssGetTableName("tb_data_subtrade"),
                                           dbl.sqlSubStr("FNum", "16", "5"), "00000",
                                           " where FNum like '"
                                           + ("T" + YssFun.formatDate(sDate,"yyyyMMdd") + "200001" +"%'"));
            hmNum.put(YssOperCons.YSS_JYLX_Buy,sMaxNum);//类型为买入时的编号
            sMaxNum=dbFun.getNextInnerCode(pub.yssGetTableName("tb_data_subtrade"),
                                           dbl.sqlSubStr("FNum", "16", "5"), "00000",
                                           " where FNum like '"
                                           + ("T" + YssFun.formatDate(sDate,"yyyyMMdd") + "900001" +"%'"));
            hmNum.put(YssOperCons.YSS_JYLX_Sale,sMaxNum);//类型为卖出时的编号
            sMaxNum=dbFun.getNextInnerCode(pub.yssGetTableName("tb_data_subtrade"),
                                           dbl.sqlSubStr("FNum", "16", "5"), "00000",
                                           " where FNum like '"
                                           + ("T" + YssFun.formatDate(sDate,"yyyyMMdd") + "100001" +"%'"));
            hmNum.put(YssOperCons.YSS_JYLX_PX,sMaxNum);//类型为分红时的编号
            sMaxNum=dbFun.getNextInnerCode(pub.yssGetTableName("tb_data_subtrade"),
                                           dbl.sqlSubStr("FNum", "16", "5"), "00000",
                                           " where FNum like '"
                                           + ("T" + YssFun.formatDate(sDate,"yyyyMMdd") + "000000" +"%'"));
             hmNum.put("00",sMaxNum);//类型为其他类型时的编号
        }
        if(tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)){
            sMaxNum =String.valueOf(hmNum.get(YssOperCons.YSS_JYLX_Buy));
            sMaxNum =YssFun.formatNumber(YssFun.toInt(sMaxNum)+1,"00000");
            hmNum.put(YssOperCons.YSS_JYLX_Buy,sMaxNum);
            sMaxNum="200001"+sMaxNum;
        }else if(tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)){
            sMaxNum =String.valueOf(hmNum.get(YssOperCons.YSS_JYLX_Sale));
            sMaxNum =YssFun.formatNumber(YssFun.toInt(sMaxNum)+1,"00000");
            hmNum.put(YssOperCons.YSS_JYLX_Sale,sMaxNum);
            sMaxNum="900001"+sMaxNum;
        }else if(tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)){
            sMaxNum =String.valueOf(hmNum.get(YssOperCons.YSS_JYLX_PX));
            sMaxNum =YssFun.formatNumber(YssFun.toInt(sMaxNum)+1,"00000");
            hmNum.put(YssOperCons.YSS_JYLX_PX,sMaxNum);
            sMaxNum="100001"+sMaxNum;
        }else{
            sMaxNum =String.valueOf(hmNum.get("00"));
            sMaxNum =YssFun.formatNumber(YssFun.toInt(sMaxNum)+1,"00000");
            hmNum.put("00",sMaxNum);
            sMaxNum="000000"+sMaxNum;
        }
        sMaxNum="T"+YssFun.formatDate(sDate,"yyyyMMdd")+sMaxNum;
        System.out.println(sMaxNum);
        return sMaxNum;
    }

    /**
     * 将数据提取并插入到交易关联表中
     * @param rs ResultSet  当前行结果集
     * @param tradeType String 交易类型
     * @throws YssException
     */
	private tmpClass insertRelaTrade(ResultSet rs,String tradeType,String sNum) throws YssException{
        TradeRelaBean rela =null;
        TradeRelaSubBean relaSub=null;
        ResultSet rsTmp =null;
        String sqlStr="";
        String[] analys =new String[]{"","",""};
        EachRateOper eachOper=null;
        tmpClass tmpcla=new tmpClass();
        try{
            rela =new TradeRelaBean();
            relaSub =new TradeRelaSubBean();
            rela.setSNum(sNum);
            relaSub.setSNum(sNum);
            analys[0] =operSql.storageAnalysisType("FAnalysisCode1", "Security");
            analys[1] =operSql.storageAnalysisType("FAnalysisCode2", "Security");
            analys[2] =operSql.storageAnalysisType("FAnalysisCode3", "Security");
            if(tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_SGou)||tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_SH)){
                sqlStr=" select qs.*,se.FTradeCury,se.FCatCode,se.FSubCatCode,se.FCuscatCode,se.FExchangeCode,seat.FBrokerCode "+
                    "  from "+pub.yssGetTableName("Tb_HZJKQS")+" qs left join "+pub.yssGetTableName("Tb_Para_Security")+" se on qs.FZqdm = se.FSecurityCode "+
                    " left join "+pub.yssGetTableName("Tb_Para_TradeSeat")+" seat on qs.FJyxwh = seat.FSeatCode where "+
                    " FInDate="+dbl.sqlDate(rs.getDate("FInDate"))+" and FDate="+dbl.sqlDate(rs.getDate("FDate"))+" and Zqdm="+dbl.sqlString(rs.getString("Zqdm"))+
                    " and ((FZqbz ='GP' and FYwbz in ('SH_ETF', 'SG_ETF')) or (FZqbz ='JJ' and FYwbz in ('XJTD_SG', 'XJTD_SH'))) "; //取所有的 现金替代 与 股票类的申购赎回
                rsTmp =dbl.openResultSet(sqlStr);
                while(rsTmp.next()){
                    rela =new TradeRelaBean();
                    relaSub =new TradeRelaSubBean();
                    rela.setSNum(sNum);
                    relaSub.setSNum(sNum);
                    for(int ia =0;ia<3;ia++){
                        if (analys[ia].equals("001")) {
                            analys[ia]=" ";//投资经理
                        } else if (analys[ia].equals("002")) {
                            analys[ia]=rsTmp.getString("FBrokerCode");//券商
                        } else if (analys[ia].equals("003")) {
                            analys[ia]=rsTmp.getString("FExchangeCode");//交易所
                        } else if (analys[ia].equals("004")) {
                            analys[ia]=rsTmp.getString("FCatCode");//品种类型
                        } else {
                            analys[ia]="";//空
                        }
                    }
                    eachOper =new EachRateOper();
                    eachOper.setYssPub(pub);
                    eachOper.setDRateDate(rsTmp.getDate("FDate"));
                    eachOper.setSCuryCode(rsTmp.getString("FTradeCury"));
                    eachOper.setSPortCode(rsTmp.getString("FPortCode"));
                    if(rsTmp.getString("FTradeCury")==null){// by leeyu 20090814
                        throw new YssException("证券代码【"+rsTmp.getString("FZQDM")+"】币种为空，请检查!");
                    }
                    eachOper.getOperValue("rate");
                    if(rsTmp.getString("FZQBZ").equalsIgnoreCase("GP") &&(rsTmp.getString("FYwbz").equalsIgnoreCase("SH_ETF")||rsTmp.getString("FYwbz").equalsIgnoreCase("SG_ETF"))){
                        //ETF 股票的申购与赎回
                        if(rsTmp.getString("FYwbz").equalsIgnoreCase("SH_ETF")){
                            rela.setDAmount(rsTmp.getDouble("FSSL"));//
                            rela.setDCost(0);
                            relaSub.setDBal(rsTmp.getDouble("FSJE")); //余额
                            rela.setIInOut(1);
                            relaSub.setIInOut(1);
                            rela.setSRelaType("SH_ETF");
                            relaSub.setSRelaType("SH_ETF");
                            //add by songjie 2012.01.06 需求 STORY 2104  QDV4赢时胜(上海开发部)2012年01月03日01_A
                            relaSub.setAttrClsCode(YssOperCons.YSS_SXFL_ETFSH);
                        }else{
                            rela.setDAmount(rsTmp.getDouble("FBSL"));//
                            rela.setDCost(0);
                            relaSub.setDBal(rsTmp.getDouble("FBJE")); //余额
                            rela.setIInOut(-1);
                            relaSub.setIInOut(-1);
                            rela.setSRelaType("SG_ETF");
                            relaSub.setSRelaType("SG_ETF");
                          //add by songjie 2012.01.06 需求 STORY 2104  QDV4赢时胜(上海开发部)2012年01月03日01_A
                            relaSub.setAttrClsCode(YssOperCons.YSS_SXFL_ETFSG);
                        }
                    }else if(rsTmp.getString("FZQBZ").equalsIgnoreCase("JJ") &&(rsTmp.getString("FYwbz").equalsIgnoreCase("SH_ETF")||rsTmp.getString("FYwbz").equalsIgnoreCase("SG_ETF"))){
                        continue;
                    }
                    if(rsTmp.getString("FYwbz").equalsIgnoreCase("XJTD_SH") ) {
                        //现金替代数据
                        rela.setDAmount(rsTmp.getDouble("FBSL"));//
                        rela.setDCost(0);
                        relaSub.setDBal(rsTmp.getDouble("FBJE")); //余额
                        rela.setSRelaType("XJTD_SH");
                        relaSub.setSRelaType("XJTD_SH");

                    }else if(rsTmp.getString("FYwbz").equalsIgnoreCase("XJTD_SG")){
                        //现金替代数据
                        rela.setDAmount(rsTmp.getDouble("FSSL"));//
                        rela.setDCost(0);
                        relaSub.setDBal(rsTmp.getDouble("FSJE")); //余额
                        rela.setSRelaType("XJTD_SG");
                        relaSub.setSRelaType("XJTD_SG");
                    }
                    if(rsTmp.getString("FYwbz").equalsIgnoreCase("XJCE")){
                        //现金差额数据,取清算表数据的前一工作日的数据
                        ResultSet rsCE =null;
                        BaseOperDeal operDeal =new BaseOperDeal();
                        operDeal.setYssPub(pub);
                        java.util.Date dLastWorkDate= operDeal.getWorkDay(rsTmp.getString("FHolidaysCode"),rsTmp.getDate("FDate"),-1);
                        sqlStr=" select qs.*,se.FTradeCury,se.FCatCode,se.FSubCatCode,se.FCuscatCode,se.FExchangeCode,seat.FBrokerCode "+
                            "  from "+pub.yssGetTableName("Tb_HZJKQS")+" qs left join "+pub.yssGetTableName("Tb_Para_Security")+" se on qs.FZqdm = se.FSecurityCode "+
                            " left join "+pub.yssGetTableName("Tb_Para_TradeSeat")+" seat on qs.FJyxwh = seat.FSeatCode where "+
                            "  FDate="+dbl.sqlDate(dLastWorkDate)+" and Zqdm="+dbl.sqlString(rsTmp.getString("ZQDM"))+" and FYwbz ="+dbl.sqlString(rs.getString("FYwbz"))+" and FZqbz ='JJ'";
                        rsCE=dbl.openResultSet(sqlStr);
                        if(rsCE.next()){
                            if(rs.getString("FYwbz").indexOf("SG")>-1){//申购
                                rela.setDAmount(rsCE.getDouble("FBSL"));//
                                rela.setDCost(0);
                                relaSub.setDBal(rsCE.getDouble("FBJE")); //余额
                                rela.setIInOut(1);
                                relaSub.setIInOut(1);
                                rela.setSRelaType("SG");
                                relaSub.setSRelaType("SG");
                            }else{
                                rela.setDAmount(rsCE.getDouble("FSSL"));//
                                rela.setDCost(0);
                                relaSub.setDBal(rsCE.getDouble("FSJE")); //余额
                                rela.setIInOut(-1);
                                relaSub.setIInOut(-1);
                                rela.setSRelaType("SH");
                                relaSub.setSRelaType("SH");
                            }
                        }
                        rsCE.getStatement().close();//关闭查询现金差额的数据处理
                    }
                    rela.setDMCost(rela.getDCost());
                    rela.setDVCost(rela.getDCost());
                    rela.setDBaseCuryCost(YssD.mul(eachOper.getDBaseRate(),rela.getDCost()));
                    rela.setDMBaseCuryCost(YssD.mul(eachOper.getDBaseRate(),rela.getDCost()));
                    rela.setDVBaseCuryCost(YssD.mul(eachOper.getDBaseRate(),rela.getDCost()));
                    rela.setDPortCuryCost(YssD.mul(eachOper.getDPortRate(),rela.getDCost()));
                    rela.setDMPortCuryCost(YssD.mul(eachOper.getDPortRate(),rela.getDCost()));
                    rela.setDVPortCuryCost(YssD.mul(eachOper.getDPortRate(),rela.getDCost()));
                    relaSub.setDMBal(relaSub.getDBal());
                    relaSub.setDVBal(relaSub.getDBal());
                    relaSub.setDBaseCuryBal(YssD.mul(eachOper.getDBaseRate(),relaSub.getDBal()));
                    relaSub.setDMBaseCuryBal(YssD.mul(eachOper.getDBaseRate(),relaSub.getDBal()));
                    relaSub.setDVBaseCuryBal(YssD.mul(eachOper.getDBaseRate(),relaSub.getDBal()));
                    relaSub.setDPortCuryBal(YssD.mul(eachOper.getDPortRate(),relaSub.getDBal()));
                    relaSub.setDMPortCuryBal(YssD.mul(eachOper.getDPortRate(),relaSub.getDBal()));
                    relaSub.setDVPortCuryBal(YssD.mul(eachOper.getDPortRate(),relaSub.getDBal()));
                    //rela.setSRelaType(tradeType);
                    rela.setSPortCode(rsTmp.getString("FPortCode"));
                    rela.setSAnalysisCode1(analys[0].length()==0?" ":analys[0]);
                    rela.setSAnalysisCode2(analys[1].length()==0?" ":analys[1]);
                    rela.setSAnalysisCode3(analys[2].length()==0?" ":analys[2]);
                    rela.setSSecurityCode(rsTmp.getString("FZqdm"));
                    rela.checkStateId =checkState.equalsIgnoreCase("true")?1:0;
                    //relaSub.setSRelaType(tradeType);
                    relaSub.setSTsfTypeCode(" ");//调拨类型// by leeyu 20090814
                    relaSub.setSSubTsfTypeCode(" ");
                    relaSub.setSPortCode(rsTmp.getString("FPortCode"));
                    relaSub.setSAnalysisCode1(analys[0].length()==0?" ":analys[0]);
                    relaSub.setSAnalysisCode2(analys[1].length()==0?" ":analys[1]);
                    relaSub.setSAnalysisCode3(analys[2].length()==0?" ":analys[2]);
                    relaSub.setSSecurityCode(rsTmp.getString("FZQDM"));
                    relaSub.setSCuryCode(rsTmp.getString("FTradeCury"));//交易币种
                    relaSub.checkStateId =checkState.equalsIgnoreCase("true")?1:0;
                    this.alTradeRela.add(rela);
                    this.alTradeSubRela.add(relaSub);
                }//end while
            }else if(tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_ZZG)){ //债转股的数据
                sqlStr = " select qs.*,se.FTradeCury,se.FCatCode,se.FSubCatCode,se.FCuscatCode,se.FExchangeCode,seat.FBrokerCode " +
                    "  from " + pub.yssGetTableName("Tb_HZJKQS") + " qs left join " + pub.yssGetTableName("Tb_Para_Security") + " se on qs.FZqdm = se.FSecurityCode " +
                    " left join " + pub.yssGetTableName("Tb_Para_TradeSeat") + " seat on qs.FJyxwh = seat.FSeatCode where " +
                    " FInDate=" + dbl.sqlDate(rs.getDate("FInDate")) + " and FDate=" + dbl.sqlDate(rs.getDate("FDate")) +
                    " and Zqdm=" + dbl.sqlString(rs.getString("ZQDM")) + " and FYwbz ='KZZGP' and FZqbz ='GP'";
                rsTmp = dbl.openResultSet(sqlStr);
                MarketValueBean market =null;
                if(rsTmp.next()){
                    for(int ia =0;ia<3;ia++){
                        if (analys[ia].equals("001")) {
                            analys[ia]=" ";//投资经理
                        } else if (analys[ia].equals("002")) {
                            analys[ia]=rsTmp.getString("FBrokerCode");//券商
                        } else if (analys[ia].equals("003")) {
                            analys[ia]=rsTmp.getString("FExchangeCode");//交易所
                        } else if (analys[ia].equals("004")) {
                            analys[ia]=rsTmp.getString("FCatCode");//品种类型
                        } else {
                            analys[ia]="";//空
                        }
                    }
                    eachOper =new EachRateOper();
                    eachOper.setYssPub(pub);
                    eachOper.setDRateDate(rsTmp.getDate("FDate"));
                    eachOper.setSCuryCode(rsTmp.getString("FTradeCury"));
                    eachOper.setSPortCode(rsTmp.getString("FPortCode"));
                    if(rsTmp.getString("FTradeCury")==null){// by leeyu 20090814
                        throw new YssException("证券代码【"+rsTmp.getString("FZQDM")+"】币种为空，请检查!");
                    }
                    eachOper.getOperValue("rate");
					//edit by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A
                    market = pubMethod.getMarketValue(rsTmp.getString("FZqdm"),rsTmp.getString("FPortCode"),rsTmp.getString("FExchangeCode"),rsTmp.getDate("FDate"));
                    rela.setSRelaType(tradeType);
                    rela.setSPortCode(rsTmp.getString("FPortCode"));
                    rela.setSAnalysisCode1(analys[0].length()==0?" ":analys[0]);
                    rela.setSAnalysisCode2(analys[1].length()==0?" ":analys[1]);
                    rela.setSAnalysisCode3(analys[2].length()==0?" ":analys[2]);
                    rela.setSSecurityCode(rsTmp.getString("FZqdm"));
                    rela.setDAmount(rsTmp.getDouble("FBSL"));//股票的话只有数量
                    rela.setDCost(YssD.mul(market.getDblClosingPrice(),rsTmp.getDouble("FBSL")));//股票的成本=收盘价*股票转入数量
                    rela.setDMCost(rela.getDCost());
                    rela.setDVCost(rela.getDCost());
                    rela.setDBaseCuryCost(YssD.mul(rela.getDCost(),eachOper.getDBaseRate()));
                    rela.setDMBaseCuryCost(YssD.mul(rela.getDCost(),eachOper.getDBaseRate()));
                    rela.setDVBaseCuryCost(YssD.mul(rela.getDCost(),eachOper.getDBaseRate()));
                    //add by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
                    if(eachOper.getDPortRate() == 0){
                        rela.setDPortCuryCost(YssD.mul(rela.getDCost(),eachOper.getDBaseRate()));
                        rela.setDMPortCuryCost(YssD.mul(rela.getDCost(),eachOper.getDBaseRate()));
                        rela.setDVPortCuryCost(YssD.mul(rela.getDCost(),eachOper.getDBaseRate()));
                    }else{
                        rela.setDPortCuryCost(YssD.mul(rela.getDCost(),YssD.div(eachOper.getDBaseRate(), eachOper.getDPortRate())));
                        rela.setDMPortCuryCost(YssD.mul(rela.getDCost(),YssD.div(eachOper.getDBaseRate(), eachOper.getDPortRate())));
                        rela.setDVPortCuryCost(YssD.mul(rela.getDCost(),YssD.div(eachOper.getDBaseRate(), eachOper.getDPortRate())));
                    }
                    //add by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
                    //delete by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
//                    rela.setDPortCuryCost(YssD.mul(rela.getDCost(),eachOper.getDPortRate()));
//                    rela.setDMPortCuryCost(YssD.mul(rela.getDCost(),eachOper.getDPortRate()));
//                    rela.setDVPortCuryCost(YssD.mul(rela.getDCost(),eachOper.getDPortRate()));
                    //delete by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
                    rela.checkStateId =checkState.equalsIgnoreCase("true")?1:0;
                    //delete by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
//                    relaSub.setSRelaType(tradeType);
//                    relaSub.setSTsfTypeCode("06");//调拨类型// by leeyu 20090814
//                    relaSub.setSSubTsfTypeCode("06FI");
//                    relaSub.setSPortCode(rsTmp.getString("FPortCode"));
//                    relaSub.setSAnalysisCode1(analys[0].length()==0?" ":analys[0]);
//                    relaSub.setSAnalysisCode2(analys[1].length()==0?" ":analys[1]);
//                    relaSub.setSAnalysisCode3(analys[2].length()==0?" ":analys[2]);
//                    relaSub.setSSecurityCode(rsTmp.getString("FZQDM"));
//                    relaSub.setSCuryCode(rsTmp.getString("FTradeCury"));//交易币种
//                    relaSub.setDBal(rs.getDouble("FSJE"));//取卖的金额
//                    relaSub.setDMBal(relaSub.getDBal());
//                    relaSub.setDVBal(relaSub.getDBal());
//                    relaSub.setDBaseCuryBal(YssD.mul(eachOper.getDBaseRate(),relaSub.getDBal()));
//                    relaSub.setDMBaseCuryBal(YssD.mul(eachOper.getDBaseRate(),relaSub.getDBal()));
//                    relaSub.setDVBaseCuryBal(YssD.mul(eachOper.getDBaseRate(),relaSub.getDBal()));
//                    relaSub.setDPortCuryBal(YssD.mul(eachOper.getDPortRate(),relaSub.getDBal()));
//                    relaSub.setDMPortCuryBal(YssD.mul(eachOper.getDPortRate(),relaSub.getDBal()));
//                    relaSub.setDVPortCuryBal(YssD.mul(eachOper.getDPortRate(),relaSub.getDBal()));
                  //delete by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
                    relaSub.checkStateId =checkState.equalsIgnoreCase("true")?1:0;
                    //add by songjie 2012.01.04 需求 STORY 2104 QDV4赢时胜(上海开发部)2012年01月03日01_A
                    relaSub.setAttrClsCode(YssOperCons.YSS_SXFL_KZZGP);
                    rela.setIInOut(1);
//                    relaSub.setIInOut(1);//delete by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
                    this.alTradeRela.add(rela);
//                    this.alTradeSubRela.add(relaSub);//delete by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
                    //edit by songjie 2010.03.12 MS00907 QDV4赢时胜（上海）2010年03月11日01_B
                    tmpcla.dJE = rsTmp.getDouble("FSJe");//取买入的金额出来 //edit by songjie 取卖出的金额
                }//end while
            }else if(tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_RGOXQ)||tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_RGUXQ)){
                for (int ia = 0; ia < 3; ia++) {
                    if (analys[ia].equals("001")) {
                        analys[ia] = " "; //投资经理
                    } else if (analys[ia].equals("002")) {
                        analys[ia] = rs.getString("FBrokerCode"); //券商
                    } else if (analys[ia].equals("003")) {
                        analys[ia] = rs.getString("FExchangeCode"); //交易所
                    } else if (analys[ia].equals("004")) {
                        analys[ia] = rs.getString("FCatCode"); //品种类型
                    } else {
                        analys[ia] = ""; //空
                    }
                }
                TradeSubBean subtrade = new TradeSubBean();
                YssCost cost=null;
                YssCost costMV=null;
                eachOper = new EachRateOper();
                eachOper.setYssPub(pub);
                eachOper.setDRateDate(rs.getDate("FDate"));
                eachOper.setSCuryCode(rs.getString("FTradeCury"));
                eachOper.setSPortCode(rs.getString("FPortCode"));
                if(rs.getString("FTradeCury")==null){// by leeyu 20090814
                    throw new YssException("证券代码【"+rsTmp.getString("FZQDM")+"】币种为空，请检查!");
                }
                eachOper.getOperValue("rate");
                //rela.setSRelaType(tradeType);
                rela.setSPortCode(rs.getString("FPortCode"));
                rela.setSAnalysisCode1(analys[0].length() == 0 ? " " : analys[0]);
                rela.setSAnalysisCode2(analys[1].length() == 0 ? " " : analys[1]);
                rela.setSAnalysisCode3(analys[2].length() == 0 ? " " : analys[2]);
                rela.setSSecurityCode(rs.getString("FZqdm"));
                subtrade.setSecurityCode(rela.getSSecurityCode());
                subtrade.setPortCode(rela.getSPortCode());
                subtrade.setBargainDate(YssFun.formatDate(rs.getDate("FDate")));
                subtrade.setBrokerCode(rs.getString("FBrokerCode"));
                subtrade.setNum(sNum);
                subtrade.setBaseCuryRate(eachOper.getDBaseRate());
                subtrade.setPortCuryRate(eachOper.getDPortRate());
                if (tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_RGUXQ)) { //认购行权
                    rela.setDAmount(rs.getDouble("FBSL"));
                    subtrade.setTradeAmount(rs.getDouble("FBSL"));
                    cost=calcBondCost(subtrade);
                    rela.setDCost(cost.getCost());
                } else {
                    rela.setDAmount(rs.getDouble("FSSL"));
                    subtrade.setTradeAmount(rs.getDouble("FSSL"));
                    cost=calcBondCost(subtrade);
                    rela.setDCost(cost.getCost());
                    relaSub.setDBal(rs.getDouble("FSJE"));
                }
                rela.setSRelaType(tradeType);
                relaSub.setSRelaType(tradeType);
                rela.setDMCost(rela.getDCost());
                rela.setDVCost(rela.getDCost());
                rela.setDBaseCuryCost(YssD.mul(rela.getDCost(), eachOper.getDBaseRate()));
                rela.setDMBaseCuryCost(YssD.mul(rela.getDCost(), eachOper.getDBaseRate()));
                rela.setDVBaseCuryCost(YssD.mul(rela.getDCost(), eachOper.getDBaseRate()));
                rela.setDPortCuryCost(YssD.mul(rela.getDCost(), eachOper.getDPortRate()));
                rela.setDMPortCuryCost(YssD.mul(rela.getDCost(), eachOper.getDPortRate()));
                rela.setDVPortCuryCost(YssD.mul(rela.getDCost(), eachOper.getDPortRate()));
                rela.checkStateId = checkState.equalsIgnoreCase("true") ? 1 : 0;
                //relaSub.setSRelaType(tradeType);
                relaSub.setSTsfTypeCode("09"); //调拨类型
                relaSub.setSSubTsfTypeCode("09EQ");
                relaSub.setSPortCode(rs.getString("FPortCode"));
                relaSub.setSAnalysisCode1(analys[0].length() == 0 ? " " : analys[0]);
                relaSub.setSAnalysisCode2(analys[1].length() == 0 ? " " : analys[1]);
                relaSub.setSAnalysisCode3(analys[2].length() == 0 ? " " : analys[2]);
                relaSub.setSSecurityCode(rs.getString("FZQDM"));
                relaSub.setSCuryCode(rs.getString("FTradeCury")); //交易币种
                relaSub.checkStateId = checkState.equalsIgnoreCase("true") ? 1 : 0;
                costMV = clasSecurityMV(subtrade, analys[0], analys[1], analys[2]);
                relaSub.setDMBal(costMV.getMCost());
                relaSub.setDVBal(costMV.getVCost());
                relaSub.setDBaseCuryBal(costMV.getBaseCost());
                relaSub.setDMBaseCuryBal(costMV.getBaseMCost());
                relaSub.setDVBaseCuryBal(costMV.getBaseVCost());
                relaSub.setDPortCuryBal(costMV.getPortCost());
                relaSub.setDMPortCuryBal(costMV.getPortMCost());
                relaSub.setDVPortCuryBal(costMV.getPortVCost());
                rela.setIInOut(1);
                relaSub.setIInOut(1);
                this.alTradeRela.add(rela);
                this.alTradeSubRela.add(relaSub);
            }
            //------- add by wangzuochun 2010.05.28 MS01140 日终处理权益处理出来的现金对价数据与国内接口处理出来的不一致  QDV4国内（测试）2010年04月28日01_B 
            else if(tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_XJDJ)){
            	
            	for (int ia = 0; ia < 3; ia++) {
                    if (analys[ia].equals("001")) {
                        analys[ia] = " "; //投资经理
                    } else if (analys[ia].equals("002")) {
                        analys[ia] = rs.getString("FBrokerCode"); //券商
                    } else if (analys[ia].equals("003")) {
                        analys[ia] = rs.getString("FExchangeCode"); //交易所
                    } else if (analys[ia].equals("004")) {
                        analys[ia] = rs.getString("FCatCode"); //品种类型
                    } else {
                        analys[ia] = ""; //空
                    }
                }
            	
            	eachOper =new EachRateOper();
            	eachOper.setYssPub(pub);
            	eachOper.setSCuryCode(rs.getString("FTradeCury"));
            	eachOper.setDRateDate(rs.getDate("FDate"));
            	eachOper.setSPortCode(rs.getString("FPortCode"));
                if(rs.getString("FTradeCury")==null){
                    throw new YssException("证券代码【"+rs.getString("FZQDM")+"】币种为空，请检查!");
                }
                eachOper.getOperValue("rate");
                
            	rela.setSNum(sNum); //交易编号

            	rela.setSRelaType(YssOperCons.YSS_JYLX_XJDJ); //关联类型

            	rela.setSPortCode(rs.getString("FPortCode")); //组合代码

                
            	rela.setSSecurityCode(rs.getString("FZQDM")); //证券代码

            	rela.setDAmount(0);

            	rela.setIInOut(-1); //设置成本流入、流出方向为-流出

                //--------------设置原币成本------------------
            	rela.setDCost(rs.getDouble("FSSSJE")); //核算成本

            	rela.setDMCost(rs.getDouble("FSSSJE")); //管理成本

            	rela.setDVCost(rs.getDouble("FSSSJE")); //估值成本
                //---------------end------------------------

                //---------------设置基础货币成本---------------
            	rela.setDBaseCuryCost(YssD.mul(rs.getDouble("FSSSJE"), eachOper.getDBaseRate()));     //基础货币核算成本

            	rela.setDMBaseCuryCost(YssD.mul(rs.getDouble("FSSSJE"), eachOper.getDBaseRate()));    //基础货币管理成本

            	rela.setDVBaseCuryCost(YssD.mul(rs.getDouble("FSSSJE"), eachOper.getDBaseRate()));    //基础货币估值成本
                //---------------end--------------------------

                //---------------设置组合货币成本----------------
            	rela.setDPortCuryCost(YssD.div(YssD.mul(rs.getDouble("FSSSJE"), eachOper.getDBaseRate()), eachOper.getDPortRate()));    //组合货币核算成本

            	rela.setDMPortCuryCost(YssD.div(YssD.mul(rs.getDouble("FSSSJE"), eachOper.getDBaseRate()), eachOper.getDPortRate()));   //组合货币管理成本

            	rela.setDVPortCuryCost(YssD.div(YssD.mul(rs.getDouble("FSSSJE"), eachOper.getDBaseRate()), eachOper.getDPortRate()));   //组合货币估值成本
            	
            	rela.setSDesc(""); //描述

            	rela.checkStateId = 1; //审核状态

            	rela.creatorCode = pub.getUserCode();      //创建人

            	rela.creatorTime = YssFun.formatDatetime(new java.util.Date()); //创建时间

            	rela.checkUserCode = pub.getUserCode();    //审核人

            	rela.checkTime = YssFun.formatDatetime(new java.util.Date()); //审核时间
            	this.alTradeRela.add(rela);
            }
            //----------------------------------MS01140---------------------------------//
            
        }catch(Exception ex){
            throw new YssException("处理转换交易关联数据时出错!",ex);
        }finally{
            dbl.closeResultSetFinal(rsTmp);
        }
        return tmpcla;
    }

    /**
     * 权证行权的业务资料数据处理
     * @param tradeSub TradeSubBean
     * @param tradeType String
     * @throws YssException
     */
    private void convertWarrant(TradeSubBean tradeSub,String tradeType) throws YssException{
        ResultSet rs =null;
        String sqlStr="";
        FeeWayBean feeWayBean =null;
        try{
            sqlStr="select a.*,b.FExchangeCode from "+pub.yssGetTableName("Tb_Para_Warrant")+
                " a left join "+pub.yssGetTableName("Tb_Para_Security")+" b on a.FTSecurityCode=b.FSecurityCode where FWarrantCode="+dbl.sqlString(tradeSub.getSecurityCode());
            rs =dbl.openResultSet(sqlStr);
            if(rs.next()){
                feeWayBean = (FeeWayBean) hmFeeWay.get(assetGroupCode + " " + tradeSub.getPortCode() + " " + pubMethod.getBrokerCode(tradeSub.getTradeSeatCode()) + " " + tradeSub.getTradeSeatCode()); //获取费用承担方向表信息
                tradeSub.setSecurityCode(rs.getString("FTSecurityCode"));
                if (feeWayBean.getBrokerBear().indexOf("01") > -1) { //如果是券商承担股票经手费
                    tradeSub.setFTradeFee1(YssD.sub(tradeSub.getFTradeFee1(),tradeSub.getFTradeFee2())) ;
                }
                if (feeWayBean.getBrokerBear().indexOf("05") > -1) { //如果是券商承担股票征管费
                    tradeSub.setFTradeFee1(YssD.sub(tradeSub.getFTradeFee1(),tradeSub.getFTradeFee4()));
                }
                if(rs.getString("FSettleTypeCode").equalsIgnoreCase("Cash")){//现金结算
                    tradeSub.setTradeAmount(0);
                    tradeSub.setTradePrice(0);
                    tradeSub.setTradeMoney(0);
                    tradeSub.setTotalCost(YssD.mul(YssD.sub(rs.getDouble("FSquarePrice"),
                        rs.getDouble("FExePrice")),
                        rs.getDouble("FExeRatio"),
                        tradeSub.getTradeAmount()));//(结算价格－行权价格)×比例×数量
                    if(tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_RGUXQ)){//认购
                        //加上费用
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee1()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee2()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee3()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee4()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee5()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee6()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee7()));
                    }else{
                        //减去费用
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee1()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee2()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee3()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee4()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee5()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee6()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee7()));

                    }
                }else{
                    tradeSub.setTradeAmount(YssD.mul(rs.getDouble("FExeRatio"),tradeSub.getTradeAmount()));//行权比例×原始数量
                    if(tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_RGUXQ)){//认购
                        tradeSub.setTradePrice(pubMethod.getMarketValue(tradeSub.getSecurityCode(),tradeSub.getPortCode(),rs.getString("FExchangeCode"), YssFun.toDate(tradeSub.getBargainDate())).getDblYClosePrice());
                        tradeSub.setTradeMoney(YssD.mul(tradeSub.getTradePrice(),tradeSub.getTradeAmount()));
                        tradeSub.setTotalCost(YssD.mul(rs.getDouble("FExePrice"),tradeSub.getTradeAmount()));//行权价格×数量
                        //加上费用
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee1()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee2()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee3()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee4()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee5()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee6()));
                        tradeSub.setTotalCost(YssD.add(tradeSub.getTotalCost(),tradeSub.getFTradeFee7()));
                    }else{
                        YssCost cost= calcBondCost(tradeSub);
                        tradeSub.setTradeMoney(cost.getCost());
                        tradeSub.setTradePrice(YssD.div(tradeSub.getTradeMoney(),tradeSub.getTradeAmount()));
                        tradeSub.setTotalCost(YssD.mul(rs.getDouble("FExePrice"),tradeSub.getTradeAmount()));//行权价格×数量
                        //减去费用
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee1()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee2()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee3()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee4()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee5()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee6()));
                        tradeSub.setTotalCost(YssD.sub(tradeSub.getTotalCost(),tradeSub.getFTradeFee7()));
                    }
                }
            }
        }catch(Exception ex){
            throw new YssException(ex.getMessage(),ex);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 获取回购证券信息的期间日期
     * @param rs ResultSet
     * @return int
     * @throws YssException
     */
    private int getPurChaseDay(ResultSet rs) throws YssException{
        int iDay=0;
        ResultSet rsTmp =null;
        String sqlStr="";
        try{
            sqlStr="select FDuration from "+pub.yssGetTableName("Tb_Para_Purchase")+
                " a left join "+pub.yssGetTableName("Tb_Para_Depositduration")+
                " b on a.FDepDurCode= b.FDepdurCode where a.FSecurityCode ="+dbl.sqlString(rs.getString("FZQDM"));
            rsTmp =dbl.openResultSet(sqlStr);
            if(rsTmp.next()){
                iDay = rsTmp.getInt("FDuration");
            }
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }finally{
            dbl.closeResultSetFinal(rsTmp);
        }
        return iDay;
    }

    /**
     * 计算债券的业务资料成本
     * @param rs ResultSet
     * @param tradeSub TradeSubBean
     * @return YssCost
     * @throws YssException
     */
    private YssCost calcBondCost(TradeSubBean tradeSub )throws YssException{
        YssCost cost =null;
        try{
            BaseAvgCostCalculate costCal = new BaseAvgCostCalculate();
            costCal.setYssPub(pub);
         costCal.initCostCalcutate(YssFun.toDate(tradeSub.getBargainDate()),
                                   tradeSub.getPortCode(),
                                   "",tradeSub.getBrokerCode(),
                                   tradeSub.getAttrClsCode());
         cost = costCal.getCarryCost(tradeSub.getSecurityCode(),
                                     tradeSub.getTradeAmount(),
                                     tradeSub.getNum(),
                                     tradeSub.getBaseCuryRate(),
                                     tradeSub.getPortCuryRate());

        }catch(Exception ex){
            throw new YssException(ex.getMessage(),ex);
        }
        return cost;
    }

    /**
     * 计算当日行权估值增值余额
     * 昨日估值增值余额/库存数量*行权数量
     * @param tradeSub TradeSubBean
     * @return YssCost
     * @throws YssException
     */
    private YssCost clasSecurityMV(TradeSubBean tradeSub,String analys1,String analys2,String analys3) throws YssException{
        YssCost costMV=new YssCost();
        ResultSet rs =null;
        ResultSet rsSec=null;
        String sqlStr="";
        try{
            sqlStr="select FBal,FMBal,FVBal,FPortCuryBal,FMPortCuryBal,FVPortCuryBal,FBaseCuryBal,FMBaseCuryBal,FVBaseCuryBal from "+
                pub.yssGetTableName("Tb_stock_secrecpay")+
                " where FTsfTypeCode='09' and FSubTsfTypeCode='09EQ' "+
                " and FPortCode="+dbl.sqlString(tradeSub.getPortCode())+" and FSecurityCode="+dbl.sqlString(tradeSub.getSecurityCode())+"  and "+
                operSql.sqlStoragEve(YssFun.toDate(tradeSub.getBargainDate()))+
                ((tradeSub.getAttrClsCode()==null || tradeSub.getAttrClsCode().trim().length()==0)?"":(" and FAttrClsCode="+dbl.sqlString(tradeSub.getAttrClsCode())))+
                (analys1!=null?" and FAnalySisCode1="+dbl.sqlString(analys1):" ")+
                (analys2!=null?" and FAnalySisCode2="+dbl.sqlString(analys2):" ")+
                (analys3!=null?" and FAnalySisCode3="+dbl.sqlString(analys3):" ");
            rs =dbl.openResultSet(sqlStr);
            if(rs.next()){
                sqlStr="select FStorageAmount from "+pub.yssGetTableName("Tb_stock_security")+
                    " where FPortCode="+dbl.sqlString(tradeSub.getPortCode())+" and FSecurityCode="+dbl.sqlString(tradeSub.getSecurityCode())+"  and "+
                    operSql.sqlStoragEve(YssFun.toDate(tradeSub.getBargainDate()))+
                    ((tradeSub.getAttrClsCode()==null || tradeSub.getAttrClsCode().trim().length()==0)?"":(" and FAttrClsCode="+dbl.sqlString(tradeSub.getAttrClsCode())))+
                    (analys1!=null?" and FAnalySisCode1="+dbl.sqlString(analys1):" ")+
                    (analys2!=null?" and FAnalySisCode2="+dbl.sqlString(analys2):" ")+
                    (analys3!=null?" and FAnalySisCode3="+dbl.sqlString(analys3):" ");
                rsSec =dbl.openResultSet(sqlStr);
                if(rsSec.next()){
                    costMV.setCost(YssD.mul(YssD.div(rs.getDouble("FBal"),rsSec.getDouble("FStorageAmount")),tradeSub.getTradeAmount()));
                    costMV.setMCost(YssD.mul(YssD.div(rs.getDouble("FMBal"),rsSec.getDouble("FStorageAmount")),tradeSub.getTradeAmount()));
                    costMV.setVCost(YssD.mul(YssD.div(rs.getDouble("FVBal"),rsSec.getDouble("FStorageAmount")),tradeSub.getTradeAmount()));
                    costMV.setBaseCost(YssD.mul(YssD.div(rs.getDouble("FBaseCuryBal"),rsSec.getDouble("FStorageAmount")),tradeSub.getTradeAmount()));
                    costMV.setBaseMCost(YssD.mul(YssD.div(rs.getDouble("FMBaseCuryBal"),rsSec.getDouble("FStorageAmount")),tradeSub.getTradeAmount()));
                    costMV.setBaseVCost(YssD.mul(YssD.div(rs.getDouble("FVBaseCuryBal"),rsSec.getDouble("FStorageAmount")),tradeSub.getTradeAmount()));
                    costMV.setPortCost(YssD.mul(YssD.div(rs.getDouble("FPortCuryBal"),rsSec.getDouble("FStorageAmount")),tradeSub.getTradeAmount()));
                    costMV.setPortMCost(YssD.mul(YssD.div(rs.getDouble("FMPortCuryBal"),rsSec.getDouble("FStorageAmount")),tradeSub.getTradeAmount()));
                    costMV.setPortVCost(YssD.mul(YssD.div(rs.getDouble("FVPortCuryBal"),rsSec.getDouble("FStorageAmount")),tradeSub.getTradeAmount()));
                }
            }
        }catch(Exception ex){
            throw new YssException("计算证券【"+tradeSub.getSecurityCode()+"】估值增值出错",ex);
        }finally{
            dbl.closeResultSetFinal(rsSec);
            dbl.closeResultSetFinal(rs);
        }
        return costMV;
    }
    /**
     * 计算辅助的类
     */
    private class tmpClass{
        private tmpClass(){
        }
        private double dJE=0; //金额项
        private double dSL=0; //数量项
    }
}
