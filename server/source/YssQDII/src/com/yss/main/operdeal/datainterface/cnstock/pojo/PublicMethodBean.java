package com.yss.main.operdeal.datainterface.cnstock.pojo;

import com.yss.dsub.BaseBean;
import java.sql.*;

import com.yss.util.*;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;
import com.yss.main.operdata.MarketValueBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import java.util.HashMap;
import com.yss.pojo.param.bond.YssBondIns; //add by wangzuochun 2010.04.29  MS01121    上交所证券变动库接口处理出来的债券派息业务数据不对    QDV4国内（测试）2010年04月26日01_B  
import com.yss.main.operdeal.bond.*;//add by wangzuochun 2010.04.29  MS01121    上交所证券变动库接口处理出来的债券派息业务数据不对    QDV4国内（测试）2010年04月26日01_B  
import com.yss.main.operdeal.BaseOperDeal;

/**
 *
 * <p>Title: 处理国内接口时的调用公共方法的类</p>
 *
 * <p>Description: 类中定义了处理特定数据时的返回方法</p>
 *
 * <p>Copyright: Copyright (c) 2009-06-30</p>
 *
 * <p>Company: ysstech</p>
 *
 * @author by leeyu add 20090630
 * @version 4.0
 */
public class PublicMethodBean extends BaseBean{
    HashMap hmSubAssetType = null;//用于储存已选组合代码对应的资产子类型
	
    public PublicMethodBean() {
    }

    /**
     * 根据库位获取券商代码
     * @return HashMap
     * @throws YssException
     */
    public String getBrokerCode(String tradeSeat)throws YssException{
       String strSql = "";
       ResultSet rs = null;
       String sBrokerCode="";
       try{
          strSql = " select FSeatCode,FBrokerCode from " + pub.yssGetTableName("Tb_Para_TradeSeat") + 
          " where FSeatCode ="+dbl.sqlString(tradeSeat);
          rs = dbl.openResultSet(strSql);
          while(rs.next()){
             sBrokerCode=rs.getString("FBrokerCode");
          }
          return sBrokerCode;
       }
       catch(Exception e){
          throw new YssException("获取席位代码对应的券商代码出错", e);
       }
       finally{
          dbl.closeResultSetFinal(rs);
       }
    }

    /**
     * 用于判断基金资产是否为指数型基金资产或指标型基金资产
     * 返回值应该包括：1.INDEX(表示指数型基金资产) 2.ZB(表示指标型基金资产) 3.OTHER(表示非指标型或指数型基金资产)
     * @return String
     */
    public HashMap judgeAssetType(String portCode, java.util.Date tradeDate) throws YssException {
        String strSql = ""; //储存sql语句
        ResultSet rs = null;
        String subAssetType = null;
        String portCodee = null;
        hmSubAssetType = new HashMap();
        try {
        	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            //在组合设置表中查询当前组合的资产子类型数据
    
            strSql = " select FAssetType, FSubAssetType, FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio") +
            "  where FPortCode in (" + operSql.sqlCodes(portCode) + ") ";
       
        	
            //end by lidaolong
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                subAssetType = rs.getString("FSubAssetType"); //资产子类型
                portCodee = rs.getString("FPortCode"); //组合代码
                hmSubAssetType.put(portCodee, subAssetType);
            }

            return hmSubAssetType;
        } catch (Exception e) {
            throw new YssException("在组合设置表中查询相关组合代码的数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据席位代码获取席位类型
     * @param tradeSeatCode String
     * @return String
     * @throws YssException
     */
    public String getTradeSeatType(String tradeSeatCode) throws YssException{
        String seatType="";
        String strSql ="";
        ResultSet rs =null;
        try{
            strSql = "select distinct FSeatType from " + pub.yssGetTableName("Tb_Para_TradeSeat") + 
            //edit by songjie 2010.06.12 将FSeatCode 改为FSeatNum
            " where FCheckState = 1 and FSeatNum = " + dbl.sqlString(tradeSeatCode);
            rs = dbl.openResultSet(strSql);
         if(rs.next()) {
            seatType = rs.getString("FSeatType");
         }
        }catch(Exception ex){
            throw new YssException("获取席位代码【"+tradeSeatCode+"】的席位类别出错！",ex);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
        return seatType;
    }

    /**
     * 检查权益类型为股份对价的
     * @param rs ResultSet
     * @return boolean
     * @throws YssException
     */
    public boolean checkStockCounterperFormance(String sZQDM,java.util.Date dDate, String szsh) throws YssException{
        try{
        	//add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
        	if(szsh.equals("CS") && sZQDM.startsWith("600")){
        		szsh = "CG";
        	}
        	//add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }finally{

        }
        return false;
    }

    /**
     * 检查证券的现金对价的权益信息，若可以检查出，返回真；否则返回假
     * @param rs ResultSet
     * @return boolean
     * @throws YssException
     */
    public boolean checkCashRight(String sZQDM,java.util.Date dDate, String szsh) throws YssException{
        ResultSet rs =null;
        String sqlStr="";
        boolean bCheck=false;
        try{
        	//add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
        	if(szsh.equals("CS") && sZQDM.startsWith("600")){
        		sZQDM = "003" + YssFun.right(sZQDM, 3);
        	}
        	//add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
            sqlStr="select '1' as FField from " + pub.yssGetTableName("Tb_Data_Cashconsider") + " where FSecurityCode ='" + sZQDM +
                " " + szsh + "' and FCheckState = 1 and " + dbl.sqlDate(dDate)+" between FRecordDate and FExRightDate"; 
            //检查日期在权益确认日与除权日之间的 与陈嘉沟通 20090902 by leeyu
            rs =dbl.openResultSet(sqlStr);
            if(rs.next()){
                bCheck= true;
            }else{
                bCheck=false;
            }
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }finally{
            dbl.closeResultSetFinal(rs);
        }
        return bCheck;
    }
    
    /**
     * 检查证券的现金对价到帐的权益信息，若可以检查出，返回真；否则返回假
     * add by songjie 
     * 2010.03.04
     * MS00900
     * QDII4.1赢时胜上海2010年03月04日01_AB 
     * @param rs ResultSet
     * @return boolean
     * @throws YssException
     */
    public boolean checkCashedRight(String sZQDM,java.util.Date dDate, String szsh) throws YssException{
        ResultSet rs =null;
        String sqlStr="";
        boolean bCheck=false;
        try{
        	//add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
        	if(szsh.equals("CS") && sZQDM.startsWith("600")){
        		sZQDM = "003" + YssFun.right(sZQDM, 3);
        	}
        	//add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
            sqlStr="select '1' as FField from " + pub.yssGetTableName("Tb_Data_Cashconsider") + " where FSecurityCode ='" + sZQDM +
                " " + szsh + "' and FCheckState = 1 and FPayDate = "+dbl.sqlDate(dDate); //检查日期到帐日的 
            rs =dbl.openResultSet(sqlStr);
            if(rs.next()){
                bCheck= true;
            }else{
                bCheck=false;
            }
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }finally{
            dbl.closeResultSetFinal(rs);
        }
        return bCheck;
    }

    /**
     * 获取送股的权益信息
     * @param rs ResultSet
     * @return boolean
     * @throws YssException
     */
    public boolean checkBonusShareRight(String sZQDM,java.util.Date dDate, String szsh) throws YssException {
        ResultSet tmpRs = null;
        String sqlStr = "";
        boolean bRight = true;
        try {
        	//add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
        	if(szsh.equals("CS") && YssFun.left(sZQDM, 3).equals("600")){
        		szsh = "CG";
        	}
        	//add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
            sqlStr = "select '1' as FField from " + pub.yssGetTableName("Tb_data_BonusShare") +
                " where FCheckState=1 and FTSecurityCode='" + sZQDM + " " + szsh + "' and " +
                dbl.sqlDate(dDate) + " between  FAfficheDate and FExRightDate "; //公告日与除息日
            tmpRs = dbl.openResultSet(sqlStr);
            if (tmpRs.next()) {
                bRight = true;
            } else {
                bRight = false;
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(tmpRs);
        }
        return bRight;
    }

    /**
     * 检查证券的分红的权益信息，若可以检查出，返回真；否则返回假
     * 条件：根据证券代码查询分红权益数据在公告日与除息日间的数据
     * @param rs ResultSet
     * @return boolean
     * @throws YssException
     */
    public boolean checkDividSecRight(String sZQDM,java.util.Date dDate, String szsh) throws YssException{
        ResultSet tmpRs =null;
        String sqlStr="";
        boolean bRight=true;
        try{
        	//add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
        	if(szsh.equals("CS") && YssFun.left(sZQDM, 3).equals("600")){
        		szsh = "CG";
        	}
        	//add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
            sqlStr="select '1' as FField from "+pub.yssGetTableName("Tb_data_dividend")+
                " where FCheckState=1 and FSecurityCode='"+sZQDM+" " + szsh + "' "+
                " and FCuryCode='CNY' and "+
                dbl.sqlDate(dDate)+" between  FRecordDate and FDividendDate ";//登记日与除息日
            tmpRs =dbl.openResultSet(sqlStr);
            if(tmpRs.next()){
                bRight =true;
            }else{
                bRight =false;
            }
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }finally{
            dbl.closeResultSetFinal(tmpRs);
        }
        return bRight;
    }

    /**
     * 检查证券的分红到帐的权益信息，若可以检查出，返回真；否则返回假
     * 条件：根据证券代码查询分红到帐权益数据派息日的数据
     * @param rs ResultSet
     * @return boolean
     * @throws YssException
     */
    public boolean checkDividedSecRight(String sZQDM,java.util.Date dDate, String szsh) throws YssException{
        ResultSet tmpRs =null;
        String sqlStr="";
        boolean bRight=true;
        try{
        	if(szsh.equals("CS") && YssFun.left(sZQDM, 3).equals("600")){
        		szsh = "CG";
        	}
            sqlStr="select '1' as FField from "+pub.yssGetTableName("Tb_data_dividend")+
                " where FCheckState=1 and FSecurityCode='"+sZQDM+" " + szsh + "' "+
                " and FCuryCode='CNY' and FDistributeDate = "+  dbl.sqlDate(dDate);//业务日期 = 派息日
            tmpRs =dbl.openResultSet(sqlStr);
            if(tmpRs.next()){
                bRight =true;
            }else{
                bRight =false;
            }
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }finally{
            dbl.closeResultSetFinal(tmpRs);
        }
        return bRight;
    }
    
    /**
    * 获取分红证券的分红比例
    * @param securityCode String
    * @return double
    * @throws YssException
    */
   //添加组合代码，用于参数传递 by leeyu 20090818 添加业务日期 edit by songjie 2010.03.15 MS00908 QDV4赢时胜（上海）2010年03月15日01_B
   public double getDividRatio(String securityCode, java.util.Date dDate, String portCode) throws YssException{
       double dRatio=0;
       ResultSet rs =null;
       String sqlStr="";
       String rightsRatioMethods="";//保存获取的通用参数的值
       CtlPubPara pubPara =null;//通用参数声明
       try{
           pubPara = new CtlPubPara(); //通用参数实例化
           pubPara.setYssPub(pub); //设置Pub
           rightsRatioMethods = (String) pubPara.getRightsRatioMethods(portCode); //获取通用参数
           String ratioMethodsDetail = pubPara.getBRightsRatioMethods(portCode,YssOperCons.YSS_JYLX_PX);//按权益类型获取权益比例方式 panjunfang add 20100510 B股业务
           if(ratioMethodsDetail.length() > 0){
           	rightsRatioMethods = ratioMethodsDetail;
           }
           //通过通用参数去设置，是用税前还是税后权益比例
           sqlStr = "select "+(rightsRatioMethods.equalsIgnoreCase("PreTaxRatio")?"FPreTaxRatio":"FAfterTaxRatio") + 
               " as FRatio from " + pub.yssGetTableName("Tb_data_dividend") +
               " where FCheckState=1 and FSecurityCode="+dbl.sqlString(securityCode) +
               " and FCuryCode='CNY' and " + dbl.sqlDate(dDate) +
               " between  FRecordDate and FDividendDate ";
           rs =dbl.openResultSet(sqlStr);
           if(rs.next()){
               dRatio =rs.getDouble("FRatio");
           }
       }catch(Exception ex){
           throw new YssException(ex.getMessage(),ex);
       }finally{
           dbl.closeResultSetFinal(rs);
       }
       return dRatio;
   }

   /**
    * 获取现金对价证券的权益比例
    * add by songjie
    * 2010.03.15
    * MS00908 
    * QDV4赢时胜（上海）2010年03月15日01_B
    * @param securityCode String
    * @return double
    * @throws YssException
    */
   public double getCashRatio(String securityCode, java.util.Date dDate, String portCode) throws YssException{
       double dRatio=0;
       ResultSet rs =null;
       String sqlStr="";
       String rightsRatioMethods="";//保存获取的通用参数的值
       CtlPubPara pubPara =null;//通用参数声明
       try{
           pubPara = new CtlPubPara(); //通用参数实例化
           pubPara.setYssPub(pub); //设置Pub
           rightsRatioMethods = (String) pubPara.getRightsRatioMethods(portCode); //获取通用参数
           String ratioMethodsDetail = pubPara.getBRightsRatioMethods(portCode,YssOperCons.YSS_JYLX_XJDJ);//按权益类型获取权益比例方式 panjunfang add 20100510 B股业务
           if(ratioMethodsDetail.length() > 0){
           	rightsRatioMethods = ratioMethodsDetail;
           }
           //通过通用参数去设置，是用税前还是税后权益比例
           sqlStr = "select "+(rightsRatioMethods.equalsIgnoreCase("PreTaxRatio")?"FPreTaxRatio":"FAfterTaxRatio") + 
               " as FRatio from " + pub.yssGetTableName("Tb_Data_CashConsider") +
               " where FCheckState=1 and FSecurityCode="+dbl.sqlString(securityCode) +
               " and " + dbl.sqlDate(dDate)+" between FRecordDate and FExRightDate";
           rs =dbl.openResultSet(sqlStr);
           if(rs.next()){
               dRatio =rs.getDouble("FRatio");
           }
       }catch(Exception ex){
           throw new YssException(ex.getMessage(),ex);
       }finally{
           dbl.closeResultSetFinal(rs);
       }
       return dRatio;
   }
   
   /**
    * 获取分红的权益数据中的到帐日
    * @param securityCode String
    * @param dDate Date
    * @return Date
    * @throws YssException
    */
   public java.util.Date getDividSecRightDate(String securityCode,java.util.Date dDate) throws YssException{
        ResultSet tmpRs =null;
        String sqlStr="";
        java.util.Date dRightDate=null;
        try{
            sqlStr="select FDistributeDate from "+pub.yssGetTableName("Tb_data_dividend")+
                " where FCheckState=1 and FSecurityCode='"+securityCode+"' and "+
                " FCuryCode='CNY' and "+
                dbl.sqlDate(dDate)+" between  FRecordDate and FDividendDate ";//登记日与除息日
            tmpRs =dbl.openResultSet(sqlStr);
            if(tmpRs.next()){
                dRightDate = tmpRs.getDate("FDistributeDate");
            }
          //modify by zhangfa 20101227 BUG #674 读数处理方式〗未设置“分红派息提前一天入帐”时，读数报错 
            boolean flag=false;
            if(securityCode!=null&&(securityCode.indexOf("CG")!=-1||securityCode.indexOf("CS")!=-1)){
            	flag=true;
            }
            if(dRightDate==null&&flag){
            	throw new YssException("导入"+securityCode+"分红派息数据,文件日期应与权益登记日期一致,请重新设置!");
            }
          //----------------end 20101227 ------------
        }catch(Exception ex){
        	
            throw new YssException(ex.getMessage());
           
        }finally{
            dbl.closeResultSetFinal(tmpRs);
        }
        return dRightDate;
    }

    /**
     * 返回行情相关的价格数据
     * @param securityCode String
     * @param portCode String
     * @param marketSource String
     * @param dDate Date
     * @return MarketValueBean
     * @throws YssException
     */
    public MarketValueBean getMarketValue(String securityCode,String portCode,String marketSource,java.util.Date dDate) throws YssException{
        MarketValueBean market=new MarketValueBean();
        ResultSet rs =null;
        String sqlStr="";
        try{
            sqlStr="select FBarGainAmount,FbarGainMoney,FYclosePrice,FOpenPrice,FTopPrice,FLowPrice,FClosingPrice,FAveragePrice,FNewPrice from "+
                pub.yssGetTableName("Tb_Data_MarketValue")+" where FSecurityCode="+dbl.sqlString(securityCode)+" and FmktSrcCode="+
                dbl.sqlString(marketSource)+" and (FPortCode="+dbl.sqlString(portCode)+" or FPortCode=' ')"+
                " and FMktValueDate=(select max(FMktValueDate) from "+pub.yssGetTableName("Tb_Data_MarketValue")+
                " where FSecurityCode="+dbl.sqlString(securityCode)+" and FmktSrcCode="+
                dbl.sqlString(marketSource)+" and (FPortCode="+dbl.sqlString(portCode)+" or FPortCode=' ')"+
                " and FMktValueDate<="+dbl.sqlDate(dDate)+")";
            rs =dbl.openResultSet(sqlStr);
            if(rs.next()){
                market.setDblBargainAmount(rs.getDouble("FBarGainAmount"));
                market.setDblBargainMoney(rs.getDouble("FbarGainMoney"));
                market.setDblYClosePrice(rs.getDouble("FYclosePrice"));
                market.setDblOpenPrice(rs.getDouble("FOpenPrice"));
                market.setDblTopPrice(rs.getDouble("FTopPrice"));
                market.setDblLowPrice(rs.getDouble("FLowPrice"));
                market.setDblClosingPrice(rs.getDouble("FClosingPrice"));
                market.setDblAveragePrice(rs.getDouble("FAveragePrice"));
                market.setDblNewPrice(rs.getDouble("FNewPrice"));
            }
        }catch(Exception ex){
            throw new YssException("获取证券的行情信息出错!",ex);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
        return market;
    }
    
    /**
     * add by wangzuochun 2010.04.29  MS01121    上交所证券变动库接口处理出来的债券派息业务数据不对    QDV4国内（测试）2010年04月26日01_B  
     * @param securityCode
     * @param sPort
     * @param dDate  本付息期间的派息截止日
     * @param dSL
     * @return
     * @throws YssException
     */
    public double getLX(String securityCode,String sPort,java.util.Date dDate, double dSL,
    											String sExchangeCode) throws YssException{
        ResultSet tmpRs =null;
        ResultSet rs = null;  
        double dBondIns = 0;   //应收债券利息
        double dPXJE = 0;  //派息金额
        YssBondIns bondIns = null;
        BaseBondOper bondOper = null;
        HashMap hmReadType = null; //用于储存数据接口参数设置界面的读数处理方式分页的各种参数 key--组合群代码,组合代码
        String sqlStr="";
        String assetGroupCode = ""; //组合群代码
        ReadTypeBean rt = null;
        int exchangePreci = 8;
        double dLX = 0;
        
        ResultSet rsSecRec = null;
        
        try{
        	assetGroupCode = pub.getAssetGroupCode();//组合群代码
        	CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin(); //新建CNInterfaceParamAdmin
            interfaceParam.setYssPub(pub);
            
            //获取数据接口参数设置的读书处理方式界面设置的参数对应的HashMap

            /**Start 20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B
             * 提出人要求非国内接口导入的债券数据，不要去取国内接口参数的设置*/
            if (sExchangeCode != null && (sExchangeCode.equalsIgnoreCase("CS") || sExchangeCode.equalsIgnoreCase("CG")))
            {
	            hmReadType = (HashMap) interfaceParam.getReadTypeBean();
	            
	            rt = (ReadTypeBean)hmReadType.get(assetGroupCode + " " + sPort);
            }
            /**End 20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B*/
            if (rt != null){
            	exchangePreci = rt.getExchangePreci();
            }
        	//modify by zhangjun 2012-01-10 STORY #1713 派息计算变更            
            /*sqlStr = "select b.*, c.* from " + pub.yssGetTableName("tb_para_fixinterest") +   //tb_para_fixinterest债券信息设置（固定收益）
                " a join " + pub.yssGetTableName("tb_para_security") +  
                " b on a.FSecuritycode = b.FSecuritycode " + 
                " left join (select case when c2.Finteresttaxcode = '0' then c1.FIntAccPer100 " + 
                " 	else c1.FSHIntAccPer100 end as FBondLX, c1.FSecurityCode from " + 
                pub.yssGetTableName("Tb_data_bondinterest") +   //Tb_data_bondinterest 债券利息
                " c1 left join " + pub.yssGetTableName("tb_para_bondintertax") +  //tb_para_bondintertax 债券税前税后利率设置
                " c2 on c1.Fsecuritycode = c2.Fsecuritycode where c1.Fsecuritycode = '" + securityCode + "'" + 
                " and c1.frecorddate = " + dbl.sqlDate(dDate) + 
                " and c1.fcheckstate = 1 and c2.fcheckstate = 1) c on a.FSecuritycode = c.FSecuritycode " +
                " where a.fsecuritycode = '" + securityCode + "'";
            
            tmpRs =dbl.openResultSet(sqlStr);
            if(tmpRs.next()){ 
            	if (tmpRs.getString("FBondLX") != null){
            		dLX = tmpRs.getDouble("FBondLX");     //债券利息（税前、税后每百元利息）
                }
            	else {
            		BaseOperDeal operDeal = new BaseOperDeal();
                    operDeal.setYssPub(pub);
                    bondOper = operDeal.getSpringRe(securityCode,"Buy");
                	
                	bondIns = new YssBondIns();
                	bondIns.setIsRate100(true);
                	bondIns.setInsType("Buy");
                	bondIns.setSecurityCode(securityCode);
                	bondIns.setInsDate(dDate);
                	bondIns.setPortCode(sPort);
                	bondIns.setHolidaysCode(tmpRs.getString("FHolidaysCode"));
                	bondIns.setAttrClsCode(" ");
                	bondIns.setFactor(tmpRs.getDouble("FFactor"));
                	bondOper.setYssPub(pub);
                    bondOper.init(bondIns);
                    dLX = bondOper.calBondInterest();
            	}
            }*/
            //========end by zhangjun 2012-01-10 ================================
           
          // add by zhangjun 2012-01-10 STORY #1713 债券派息变更
          //检查是否已进行收益计提
            sqlStr = "select fsecuritycode,FBal from " + pub.yssGetTableName("tb_Stock_SecRecPay") + 
            		 " where fsecuritycode = '" + securityCode + "' and FStorageDate = " + dbl.sqlDate(dDate) + " and FPortCode = '" + sPort + "'";
            
            rs = dbl.openResultSet(sqlStr);
            if(rs.next()){
            	dBondIns =rs.getDouble("FBal");  //FBalF:保留15位小数的原币     计息截止日的应收债券利息     FBal:保留4位小数的原币
            //}else{
            	//throw new YssException("请对先对债券【" + securityCode + "】做收益计提！");
            }
          //计算规则：派息金额  = 计息截止日当日库存  * 每百元派息
            sqlStr = " select FSecurityCode ,FMoneyAutomatic,FMoneyControl " +  //FMoneyAutomatic:每百元实际利息(自动),FMoneyControl：每百元实际利息(手动)
            	     " from " + pub.yssGetTableName("tb_Para_InterestTime") + //取每百元派息金额
            	     " where FSecurityCode = '" + securityCode + "' and FISSUEDATE = " + dbl.sqlDate(dDate);
            tmpRs =dbl.openResultSet(sqlStr);
            if(tmpRs.next()){ 
            	//取数规则：优先取“100元实际派发利息（手工维护）如无，则取“100元实际派发利息（自动计算）”
            	if (tmpRs.getDouble("FMoneyControl") != 0.0){
            		dLX = tmpRs.getDouble("FMoneyControl");     
                }else if (tmpRs.getDouble("FMoneyAutomatic") != 0.0){
                	dLX = tmpRs.getDouble("FMoneyAutomatic");  
                }
            	//两个实际派发利息都没有设置，则取“到计息截止日的应收利息余额”的金额作为派息金额
            	//=================================
                else
                {
					/**Start 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B
					 * 计算债券利息的方法，需要传入当前正在操作的组合的组合代码
					 * 以避免多个组合投资同一只债券，可能会取错组合的证券应收应付数据*/
                	sqlStr = "select * from " + pub.yssGetTableName("tb_stock_secrecpay") +
                			 " where fstoragedate = " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                			 " and FSecurityCode = " + dbl.sqlString(securityCode) + " and FSubTSFTypeCode = '06FI'" +
                			 " and FPortCode = " + dbl.sqlString(sPort);
					/**Start 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B*/
                	
                	rsSecRec = dbl.queryByPreparedStatement(sqlStr);
                	
                	if (rsSecRec.next())
                	{
                		dPXJE = rsSecRec.getDouble("FBal");
                		return YssFun.roundIt(dPXJE, exchangePreci);
                	}
                	else
                	{
                		return 999999999;
                	}
                }
            	//===============end==================
            }
            dPXJE = YssFun.roundIt(dLX * dSL, exchangePreci);  //派息金额
            /**
            BaseOperDeal operDeal = null;
        	operDeal = new BaseOperDeal();
            //检查派息金额与计提金额是否一致 (精确到小数点后4位)        	
        	//if(!(Math.abs( dPXJE - dBondIns) < 0.0001)) 精确到小数点后4位
            
        	if(Math.abs( operDeal.cutDigit(dPXJE , 4) - dBondIns) != 0 ){
            	//strSec = strSec + securityCode;
            	throw new YssException("债券【" + securityCode + "】的派息金额与收益计提中应收债券利息金额不一致！");
            }
            */
            //=======end by zhangjun 2012-01-11 ==============================
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }finally{
            dbl.closeResultSetFinal(tmpRs,rsSecRec);
            dbl.closeResultSetFinal(rs);
        }
        return dPXJE;
    }
    // add by zhangjun 2012-02-10 story #1713 检查派息金额与计提金额是否一致
    /**20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B
     * 增加sExchangeCode参数（交易所代码）*/
    public void checkDeal(String securityCode,String sPort,java.util.Date dDate, double dSL 
    		,StringBuffer secCode,String sExchangeCode) throws YssException{
        ResultSet tmpRs =null;
        ResultSet rs = null;  
        double dBondIns = 0;   //应收债券利息
        double dPXJE = 0;  //派息金额
        YssBondIns bondIns = null;
        BaseBondOper bondOper = null;
        HashMap hmReadType = null; //用于储存数据接口参数设置界面的读数处理方式分页的各种参数 key--组合群代码,组合代码
        String sqlStr="";
        String assetGroupCode = ""; //组合群代码
        ReadTypeBean rt = null;
        int exchangePreci = 8;
        double dLX = 0;
        
        ResultSet rSet = null;  
        ResultSet rSet1 = null;
        //String strCode = "";
        
        try{
        	assetGroupCode = pub.getAssetGroupCode();//组合群代码
        	CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin(); //新建CNInterfaceParamAdmin
            interfaceParam.setYssPub(pub);
            
            //获取数据接口参数设置的读书处理方式界面设置的参数对应的HashMap
            
            /**Start 20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B
             * 提出人要求非国内接口导入的债券数据，不要去取国内接口参数的设置*/
            if (sExchangeCode != null && (sExchangeCode.equalsIgnoreCase("CS") || sExchangeCode.equalsIgnoreCase("CG")))
            {
            	hmReadType = (HashMap) interfaceParam.getReadTypeBean();
                rt = (ReadTypeBean)hmReadType.get(assetGroupCode + " " + sPort);
            }
            /**End 20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B*/
            
            if (rt != null){
            	exchangePreci = rt.getExchangePreci();
            }
           
          //收益计提的计提金额
            sqlStr = "select fsecuritycode,FBal from " + pub.yssGetTableName("tb_Stock_SecRecPay") + 
            		 " where fsecuritycode = '" + securityCode + "' and FStorageDate = " + dbl.sqlDate(dDate) + " and FPortCode = '" + sPort + "'";
            
            rs = dbl.openResultSet(sqlStr);
            if(rs.next()){
            	dBondIns =rs.getDouble("FBal");  //FBalF:保留15位小数的原币     计息截止日的应收债券利息     FBal:保留4位小数的原币
            
            }
          //计算规则：派息金额  = 计息截止日当日库存  * 每百元派息
            sqlStr = " select FSecurityCode ,FMoneyAutomatic,FMoneyControl " +  //FMoneyAutomatic:每百元实际利息(自动),FMoneyControl：每百元实际利息(手动)
            	     " from " + pub.yssGetTableName("tb_Para_InterestTime") + //取每百元派息金额
            	     " where FSecurityCode = '" + securityCode + "' and FInsEndDate = " + dbl.sqlDate(dDate);
            tmpRs =dbl.openResultSet(sqlStr);
            if(tmpRs.next()){ 
            	//取数规则：优先取“100元实际派发利息（手工维护）如无，则取“100元实际派发利息（自动计算）”
            	if (tmpRs.getDouble("FMoneyControl") != 0.0){
            		dLX = tmpRs.getDouble("FMoneyControl");     
                }else{
                	dLX = tmpRs.getDouble("FMoneyAutomatic");  
                }
            }
            dPXJE = YssFun.roundIt(dLX * dSL, exchangePreci);  //派息金额
            
            
            //如某债券在｛债券信息设置_基本｝分页中［每百元派息金额］公式未设置
            sqlStr = " select a.FSecurityCode,b.FSecurityName,a.FTaskMoneyCode from " + pub.yssGetTableName("Tb_Para_FixInterest") + " a " +
            		 " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + 
            		 ") b on a.FSecurityCode = b.FSecurityCode " +
            		 " where  a.FSecurityCode = '" + securityCode + "'" ;
            rSet = dbl.openResultSet(sqlStr);
            //｛债券信息设置_付息日表设置｝分页中有无相应付息数据
            sqlStr = " select * from  "+ pub.yssGetTableName("Tb_Para_InterestTime") + 
			 		 " where FSecurityCode = '"  + securityCode + "' and FInsEndDate = " + dbl.sqlDate(dDate);
            rSet1 = dbl.openResultSet(sqlStr);
            
            if (rSet.next() && rSet1.next() ){   
            	
            	if( rSet.getString("FTaskMoneyCode") !=null &&  rSet.getString("FTaskMoneyCode").trim().length() != 0  ){  
            		BaseOperDeal operDeal = null;
                    //检查派息金额与计提金额是否一致 (精确到小数点后4位)        	
                	//if(!(Math.abs( dPXJE - dBondIns) < 0.0001)) 精确到小数点后4位
                    if(Math.abs( operDeal.cutDigit(dPXJE , 4) - dBondIns) != 0 ){
                    	secCode.append("              "+securityCode +"    "+ rSet.getString("FSecurityName")+ 
                    			"              派息金额：" + operDeal.cutDigit(dPXJE , 4) + " ，应收债券利息：" + dBondIns +"\r\n"); 
                    	//runStatus.appendValRunDesc(strCode);
                    	
                    }
            	}
            	
            }
            
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }finally{
            dbl.closeResultSetFinal(tmpRs);
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rSet);
            dbl.closeResultSetFinal(rSet1);
        }
        
    }
    
    //==============end ===========================
    
    /**
     * 根据证券代码获取票面金额
     * @param securityCode String
     * @return double
     * @throws YssException
     * @author ldaolong  需求406 南方东英\2010年12月\14日
     */
    public double getFaceAmount(String securityCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double faceAmount = 0;
        try {
            strSql = " select FFaceAmount from " + pub.yssGetTableName("Tb_Para_Security") + " where FSecurityCode = " + dbl.sqlString(securityCode);
            rs = dbl.openResultSet(strSql); //根据证券代码在证券信息设置中获取相关证券代码的票面金额
            while (rs.next()) {
                faceAmount = rs.getDouble("FFaceAmount"); //票面金额
            }
            return faceAmount;
        } catch (Exception e) {
            throw new YssException("根据证券代码获取票面金额出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
