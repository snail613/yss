package com.yss.main.operdeal.datainterface.cnstock.shstock;

import com.yss.util.*;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;

import java.sql.*;
import java.util.*;

import com.yss.main.operdeal.datainterface.cnstock.pojo.FeeAttributeBean;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadTypeBean;
/**
 * 上海证券变动库，储存的是权益数据
 * 将上海证券变动库文件经过处理读入到交易接口清算库
 * created by songjie
 * 2009-06-04
 */
public class SHZQBDBean extends DataBase{
    HashMap hmSubAssetType = null; //用于储存已选组合代码对应的资产子类型
    
    // add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
    HashMap hmShowZqdm = new HashMap();
    
    public HashMap getHmShowZqdm() {
		return hmShowZqdm;
	}
    //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
    public SHZQBDBean() {
    }

    /**
     * 用于处理上海证券变动库最终到交易接口清算库的数据转换
     * edit by songjie 2010.12.31 BUG:711 南方东英2010年12月16日01_B 
     * 修改了inertData方法，加参数HashMap hmParam
     * @throws YssException
     */
    public void inertData(HashMap hmParam) throws YssException {
        Connection conn=null;
        boolean bTrans =false;
        try {
            hmSubAssetType = new HashMap();//用于储存已选组合代码对应的资产子类型
            pubMethod.setYssPub(pub);
            hmSubAssetType = pubMethod.judgeAssetType(sPort,sDate);//获取已选组合代码对应的资产子类型
            //add by songjie 2010.12.31 BUG:711 南方东英2010年12月16日01_B 
            //获取数据接口参数设置的读数处理方式界面设置的参数对应的HashMap
            hmReadType = (HashMap) hmParam.get("hmReadType");
            conn =dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans =true;
            //1：将临时表TMP_ZQBD的数据添加到SHZQBD表中。先按日期删除旧的数据，再执行插入操作
            //2：将SHZQBD表的数据添加到A001HZJKMX表中。先按日期与组合删除旧的数据，再执行插入操作
            insertHzJkMx(conn);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans =false;
        } catch (Exception e) {
            throw new YssException(e.getMessage(),e);
        } finally {
            dbl.endTransFinal(conn,bTrans);
        }
    } //end inertData()

    /**
     * 将证券变动表的数据添加到汇总接口明细表中
     * @param conn Connection
     * @throws YssException
     */
    private void insertHzJkMx(Connection conn) throws YssException{
        String sqlStr="";
        String stockHolder=""; //股东代码
        String TradeSeat="";   //交易席位号
        //add by songjie 2010.12.31 BUG:711 南方东英2010年12月16日01_B 声明读数处理方式实体类
        ReadTypeBean readType = null; 
        String sInvestSign =""; //投资标志
        HashMap hmHolderSeat=null;
        SecTradejudge judgeBean=null;
        ResultSet rs =null;
        PreparedStatement stm =null;
        FeeAttributeBean feeAttribute = null;
        DataBase dataBase = new DataBase();
        boolean canInsert = false;//判断权益数据是否能处理到交易接口明细库
        try{
            sqlStr="insert into " + pub.yssGetTableName("Tb_HzJkMx") + "(FDate,FZqdm,FSzsh,FGddm,FJyxwh,FBs,FCjsl,FCjjg,FCjje,FYhs,FJsf,FGhf,FZgf,FYj,"+
                " FGzlx,Fhggain,FZqbz,Fywbz,FSqbh,Fqtf,Zqdm,FJYFS,Ffxj,Findate,FTZBZ,FPortCode,FCreator,FCreateTime,FJKDM) "+//edit by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//edit by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
            stm =conn.prepareStatement(sqlStr);
            //1:取股东代码与席位号
            hmHolderSeat =getStockHolderAndSeat(sPort);
            String[] arrPort =sPort.split(",");
            for(int i=0;i<arrPort.length;i++){
            	
                if (hmHolderSeat.get(arrPort[i]) != null) {
                    TradeSeat = String.valueOf(hmHolderSeat.get(arrPort[i])).split("\t")[0] ; //查找组合下的所有席位
                    stockHolder = String.valueOf(hmHolderSeat.get(arrPort[i])).split("\t")[1] ; //查找组合下的所有股东代码
                }
                //2:插入新数据
                //sqlStr="select * from SHZQBD where FDate="+dbl.sqlDate(sDate)+" and ZQzh in("+operSql.sqlCodes(stockHolder)+") and Xwh in("+operSql.sqlCodes(TradeSeat)+")";
                sqlStr = "select * from SHZQBD where fdate=" + dbl.sqlDate(sDate) + " and zqzh in (" + operSql.sqlCodes(stockHolder) + ")"
                    + " and ((bdlx ='00G' and bdsl > 0) or ((qylb in('HL','S','DX') and bdlx <>'00G') "
                    + " or (zqlb='PZ' and ltlx='N' and bdlx ='00J' and " + dbl.sqlTrimNull("qylb")
                    + ") or (zqlb='XL' and ltlx='F' and bdlx ='00J' and " + dbl.sqlTrimNull("qylb") + ")"
                    + " or (zqlb = 'PT' and qylb = 'P' and bdlx = '00J') or (bdlx ='00C' and sl < 0)))";
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    judgeBean = judgeSecurityTypeAndTradeType(rs,arrPort[i],rs.getString("XWH"));
                    if (!judgeBean.bInsert)
                        continue;

                    if(judgeBean.securitySign.equals("QY")){
                        feeAttribute = new FeeAttributeBean();

                        feeAttribute.setBusinessSign(judgeBean.tradeSign);
                        feeAttribute.setSecuritySign(judgeBean.securitySign);
                        feeAttribute.setDate(sDate);
                        feeAttribute.setZqdm(rs.getString("ZQDM") + " CG");

                        dataBase.setYssPub(pub);
                        canInsert = dataBase.judgeQYInfo(feeAttribute); //判断权益数据是否能够处理到交易接口明细库

                        if (!canInsert)
                            continue;
                    }
                    stm.setDate(1, YssFun.toSqlDate(sDate));
                    stm.setString(2, rs.getString("ZQDM") + " CG");
                    stm.setString(3, "CG");
                    stm.setString(4, rs.getString("ZQzh"));
                    stm.setString(5, rs.getString("Xwh"));
                    if(judgeBean.sBS == null || judgeBean.sBS.equals("")){
                    	judgeBean.sBS = " ";
                    }
                    stm.setString(6, judgeBean.sBS);
                    stm.setDouble(7, judgeBean.tradeAmount);
                    stm.setDouble(8, YssD.div(judgeBean.tradeMoney,judgeBean.tradeAmount));
                    stm.setDouble(9, judgeBean.tradeMoney);
                    stm.setDouble(10, 0);
                    stm.setDouble(11, 0);
                    stm.setDouble(12, 0);
                    stm.setDouble(13, 0);
                    stm.setDouble(14, 0);
                    stm.setDouble(15, 0); //国债利息
                    stm.setDouble(16, 0); //回购收益
                    stm.setString(17, judgeBean.securitySign); //证券标志
                    stm.setString(18, judgeBean.tradeSign); //业务标志
                    stm.setString(19, " "); //申请编号
                    stm.setDouble(20, 0); //其他费
                    stm.setString(21, rs.getString("ZQDM")); //证券代码
                    stm.setString(22, "PT"); //交易方式
                    stm.setDouble(23, 0); //风险金
                    stm.setDate(24, YssFun.toSqlDate(sDate)); //插入日期
                    if(hmReadType!=null&&hmReadType.get(assetGroupCode+" "+arrPort[i])!=null){
                    	//add by songjie 2010.12.31 BUG:711 南方东英2010年12月16日01_B
                    	readType =(ReadTypeBean)hmReadType.get(assetGroupCode+" "+arrPort[i]);
                        if (readType == null) {
                            throw new YssException("请在交易接口参数设置中设置已选组合的读数处理方式！");
                        }
                        sInvestSign = readType.getAssetClass();
                        //add by songjie 2010.12.31 BUG:711 南方东英2010年12月16日01_B
                        if(sInvestSign.equalsIgnoreCase("01")){ //交易类
                            sInvestSign ="C";
                        }else if(sInvestSign.equalsIgnoreCase("02")){//可供出售类
                            sInvestSign ="S";
                        }else{//持有到期类
                            sInvestSign ="F";
                        }
                    }else{
                        sInvestSign=" ";
                    }
                    stm.setString(25, sInvestSign); //投资标识
                    stm.setString(26, arrPort[i]); //组合代码
                    stm.setString(27,pub.getUserCode());
                    stm.setString(28,YssFun.formatDate(new java.util.Date(),"yyyyMMdd HH:mm:ss"));
                    stm.setString(29, "SHZQBD");//add by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
                    stm.addBatch();
                }
                stm.executeBatch();
            }//end 循环组合
        }catch(Exception ex){
            throw new YssException("执行上海证券变动表数据到接口明细表时出错！",ex);
        }finally{
            dbl.closeStatementFinal(stm);
            dbl.closeResultSetFinal(rs);
        }
    }


    private SecTradejudge judgeSecurityTypeAndTradeType(ResultSet rs,String portCode,String tradeSeat) throws YssException{
        SecTradejudge judge=new SecTradejudge();
        String sQylb=""; //权益类别
        String sBdlx=""; //变动类型
        String sZqlb=""; //证券类别
        String sLtlx=""; //流通类型

        String sZqbz=""; //证券标志
        String sYwbz=""; //业务标志
        String sBS="";   //买卖标志
        String sOlddm="";//旧代码
        double dCjsl=0;  //成交数量
        double dCjje=0;  //成交金额
        boolean bInsert=true; //是否执行插入操作,默认为执行插入
        try{
            sQylb = rs.getString("QYLB")==null?"":rs.getString("QYLB");
            sBdlx = rs.getString("BDLX")==null?"":rs.getString("BDLX");
            sZqlb = rs.getString("ZQLB")==null?"":rs.getString("ZQLB");
            sLtlx = rs.getString("LTLX")==null?"":rs.getString("LTLX");
            sOlddm = rs.getString("ZQDM");
            if(sQylb.equalsIgnoreCase("HL")){
                sZqbz="QY";
                dCjsl = rs.getDouble("Bdsl");
                pubMethod.setYssPub(pub);
                if(sBdlx.equalsIgnoreCase("00J")){
                    sBS="S";
                    if(pubMethod.checkDividSecRight(rs.getString("ZQDM"),sDate, "CG")){
                        sYwbz="PX_GP";//分红派息
                        dCjje = YssFun.roundIt(YssD.mul(
                        		//edit by songjie 2010.03.15 MS00908 QDV4赢时胜（上海）2010年03月15日01_B
                        		pubMethod.getDividRatio(rs.getString("ZQDM")+" CG", sDate,portCode),
                        		dCjsl),2);//添加组合代码，方法变更 by leeyu 20090818
                        bInsert = true;
                    }else if(pubMethod.checkCashRight(rs.getString("ZQDM"), sDate, "CG")){
                        sYwbz="XJDJ";//现金对价
                        //add by songjie 2010.03.15 MS00908 QDV4赢时胜（上海）2010年03月15日01_B
                        dCjje = YssFun.roundIt(YssD.mul(
                        		pubMethod.getCashRatio(rs.getString("ZQDM")+" CG", sDate,portCode),
                        		dCjsl),2);//添加组合代码，方法变更 by leeyu 20090818
                        bInsert = true;
                    }else{
                        bInsert =false;
                        //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                        if(hmShowZqdm.get(rs.getString("ZQDM")) == null){
                        	hmShowZqdm.put(rs.getString("ZQDM") + " CG", 
                        			rs.getString("ZQDM") + " CG 分红派息或现金对价");
                        }
                        //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                    }
                }else if(sBdlx.equalsIgnoreCase("00K")){
                    sBS="S";
                    if(pubMethod.checkDividedSecRight(rs.getString("ZQDM"),sDate, "CG")){
                        sYwbz="DZ_PX";//分红派息_到帐
                        bInsert = false;
                    }else if(pubMethod.checkCashedRight(rs.getString("ZQDM"),sDate, "CG")){
                        sYwbz="DZ_XJDJ";//现金对价_到帐
                        bInsert = false;
                    }else{
                        bInsert =false;
                        //---add by songjie 2011.03.08 需求:528 QDV4赢时胜(上海开发部)2011年01月17日01_A---//
                        //若既查不到股票分红的权益数据，又查不到现金对价的权益数据，则默认为分发派息数据
                        sYwbz="DZ_PX";//分红派息_到帐  
                        //---add by songjie 2011.03.08 需求:528 QDV4赢时胜(上海开发部)2011年01月17日01_A---//
                        //delete by songjie 2011.03.08 需求:528 QDV4赢时胜(上海开发部)2011年01月17日01_A  
//                        //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
//                        if(hmShowZqdm.get(rs.getString("ZQDM")) == null){
//                        	hmShowZqdm.put(rs.getString("ZQDM") + " CG", 
//                        			rs.getString("ZQDM") + 
//                        			" CG 分红派息到帐或现金对价到帐");
//                        }
//                        //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                        //delete by songjie 2011.03.08 需求:528 QDV4赢时胜(上海开发部)2011年01月17日01_A  
                    }
                }else{
                    bInsert=false;
                }
                //edit by songjie 2012.01.09 STORY 2104 QDV4赢时胜(上海开发部)2012年01月03日01_A 修改送股的判断条件
            }else if((sQylb.equalsIgnoreCase("S") && sBdlx.equalsIgnoreCase("00J") && rs.getDouble("BDSL") > 0)||
            		 (sQylb.equalsIgnoreCase("") && sZqlb.equalsIgnoreCase("XL") && sLtlx.equalsIgnoreCase("F") && sBdlx.equalsIgnoreCase("00J"))){
                sZqbz ="QY";
                sYwbz="SG";
                sBS="B";
                dCjje=0;
                dCjsl = Math.abs(rs.getDouble("bdSL"));
                if(pubMethod.checkStockCounterperFormance(rs.getString("ZQDM"),sDate, "CG")){ //股份对价的权益
                    if(((String)hmSubAssetType.get(portCode)).equals("0102")&& pubMethod.getTradeSeatType(tradeSeat).equalsIgnoreCase("INDEX")){
                        sYwbz ="GFDJ_ZS";//指数股份对价
                        bInsert = true;
                    }else{
                        sYwbz ="GFDJ";//股份对价
                        bInsert = true;
                    }
                }else{
                    if(((String)hmSubAssetType.get(portCode)).equals("0102") && pubMethod.getTradeSeatType(tradeSeat).equalsIgnoreCase("INDEX")){
                        sYwbz ="SG_ZS";//指数送股
                        bInsert = true;
                    }
                  //add by yanghaiming 2010.02.28 MS00892 QDII4.1赢时胜上海2010年02月25日01_AB
                    else{
                        sYwbz ="SG";//送股
                        bInsert = true;
                    }
                }
            }else if(sQylb.equalsIgnoreCase("DX")){
                sZqbz="QY";
                sBS = "S";
                dCjsl = 0; // modify by wangzuochun 2010.04.29  MS01121    上交所证券变动库接口处理出来的债券派息业务数据不对    QDV4国内（测试）2010年04月26日01_B  
                if(sBdlx.equalsIgnoreCase("00J")){
                    sYwbz="PX_ZQ"; //当权益类别为DX，标志类型为00J时，证券标志为QY，业务标识为PX_ZQ(债券派息)
                    bInsert = true;
                    // modify by wangzuochun 2010.08.14 MS01546    系统未区分债券的品种子类型来计算成交金额    QDV4赢时胜(上海)2010年08月03日02_B   
                    // modify by wangzuochun 2010.04.29  MS01121    上交所证券变动库接口处理出来的债券派息业务数据不对    QDV4国内（测试）2010年04月26日01_B  
                    
                    /**Start 20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B
                     * 国内接口处理时给交易所代码，不会影响正常处理*/
                    if ("FI12".equals(getSubcatCode(rs.getString("ZQDM")+" CG"))){
                    	dCjje = pubMethod.getLX(rs.getString("ZQDM")+" CG", portCode, sDate, 
                    			YssD.div(Math.abs(rs.getInt("bdsl")), 100),"CG");
                    }
                    else{
                    	dCjje = pubMethod.getLX(rs.getString("ZQDM")+" CG", portCode, sDate, 
                    			Math.abs(rs.getInt("bdsl")),"CG");
                    }
                    /**End 20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B*/
                    
                    //------------------------------------------MS01121---------------------------------------------//
                    //------------------------------------------MS01546---------------------------------------------//
                }else
                    bInsert=false;
            }else if(sQylb.trim().equalsIgnoreCase("")){//bdlx = ‘00J’ , zqlb = ‘PZ’ ,LTLX = ‘N’
                if(sBdlx.equalsIgnoreCase("00J") && sZqlb.equalsIgnoreCase("PZ") && sLtlx.equalsIgnoreCase("N") ){
                    sZqbz="QY";
                    sYwbz="QZ";//权证送配
                    sBS = "B";
                    dCjje=0;
                    dCjsl = Math.abs(rs.getInt("bdsl"));
                    bInsert = true;
                }else{
                    bInsert =false;
                }
            }else{
                bInsert =false;
            }
            if(sLtlx.equalsIgnoreCase("N") && sBdlx.equalsIgnoreCase("00G")){ //当流通类别为N，变动类型为00G时
                sBS="B";
                dCjje=0;
                if(sZqlb.equalsIgnoreCase("PT")){ //当证券类别为PT时
                    sZqbz="XG";//新股
                    dCjsl =rs.getDouble("Bdsl");
                    if(((String)hmSubAssetType.get(portCode)).equals("0102") && pubMethod.getTradeSeatType(tradeSeat).equalsIgnoreCase("INDEX")){
                        sYwbz="XGLT_ZS";//证券类别：新股 业务标志：新股流通_指数
                        bInsert = true;
                    }else if(((String)hmSubAssetType.get(portCode)).equals("0103") && pubMethod.getTradeSeatType(tradeSeat).equalsIgnoreCase("INDEX")){
                        sYwbz="XGLT_ZB";//证券类别：新股 业务标志：新股流通_指标
                        bInsert = true;
                    }else{
                        sYwbz="XGLT";//证券类别：新股 业务标志：新股流通
                        bInsert = true;
                    }
                }else if(sZqlb.equalsIgnoreCase("GZ")){
                    sZqbz="XZ"; //新债
                    sYwbz="XZLT";//新债转上市流通
                    dCjsl =YssD.div(rs.getDouble("Bdsl"),100);
                    bInsert = true;
                }else
                    bInsert =false;
            }
            //通过读XML文件将做一次业务类型与证券类型的转换
            pubXMLRead.setSHZQBD("",sZqbz+" "+sYwbz);
            if(pubXMLRead.getSecSign()!=null && pubXMLRead.getSecSign().trim().length()>0){
                sZqbz=pubXMLRead.getSecSign();
            }
            if(pubXMLRead.getBusinessSign()!=null && pubXMLRead.getBusinessSign().trim().length()>0){
                sYwbz= pubXMLRead.getBusinessSign();
            }
            judge.oldCode = sOlddm;
            judge.bInsert = bInsert;
            judge.securitySign = sZqbz;
            judge.tradeSign = sYwbz;
            judge.sBS =sBS;
            judge.tradeAmount = dCjsl;
            judge.tradeMoney = dCjje;
        }catch(Exception ex){
            throw new YssException(ex.getMessage(),ex);
        }
        return judge;
    }


    /*
    *辅助功能类
    */
    private class SecTradejudge {
        private SecTradejudge(){
        }
        boolean bInsert=false;   //是否插入到明细表，true 插入，false 跳过
        String securitySign="";  //证券标志
        String tradeSign="";     //业务标志
        String oldCode="";       //旧代码
        String sBS="";           //买卖类型 B/S
        double tradeAmount=0;    //成交数量
        double tradeMoney =0;    //成交金额
    }
    
    /**
     * add by wangzuochun 2010.08.14  MS01546    系统未区分债券的品种子类型来计算成交金额    QDV4赢时胜(上海)2010年08月03日02_B 
     * 做上海证券变动库导入时，判断导入的债券派息数据在证券信息设置表的品种子类型为国债（FI12）还是非国债，   
     * @param secCode
     * @return
     * @throws YssException
     */
    public String getSubcatCode(String secCode) throws YssException{
    	
    	String strReturn = "";
    	ResultSet rs = null;
    	
    	try{
    		
    		String strSql = " select * from " + pub.yssGetTableName("tb_para_security")
    					+ " where FSecurityCode = " + dbl.sqlString(secCode)
    					+ " and FCatCode = 'FI' and FSubCatCode = 'FI12'";
    	
    		rs = dbl.openResultSet(strSql);
    		if (rs.next()){
    			strReturn = "FI12";
    		}
    		
    		return strReturn;
    	}
    	catch(Exception ex){
    		throw new YssException(ex.getMessage(),ex);
    	}
    	finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
}

