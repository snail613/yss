package com.yss.main.operdeal.report.navrep;

import com.yss.util.YssException;
import java.util.ArrayList;
import com.yss.util.YssCons;
import com.yss.util.YssFun;
import com.yss.util.YssD;
import com.yss.util.YssGlobal;
import com.yss.commeach.EachGetPubPara;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.platform.pfoper.pubpara.ParaWithPort;
import com.yss.main.operdeal.report.navrep.pojo.NavRepBean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NavSecurity
    extends BaseNavRep {
    public NavSecurity() {
    }

    protected void initReport(java.util.Date dDate, String sPortCode,
                              String sInvMgrCode) throws YssException {
        this.valDefine =
            "FCatCode;FSubCatCode;FAttrClsCode;FCuryCode;FSecurityCode";
        this.dDate = dDate;
        this.portCode = sPortCode;
        this.invMgrCode = sInvMgrCode;
    }

    protected ArrayList getGradeData(String groupStr, int Grade) throws
        YssException {
        NavRepBean navRep = null;
        ArrayList valCashBeans = null;
        String strSql = "";
        String strDetailSql = "";
        String[] valCashDefineAry = null;
        ResultSet rs = null;
        ResultSet sumRs = null;
        ArrayList leftArr = new ArrayList();
        ArrayList fieldsArr = new ArrayList();
        String sGroupSql = "";
        String OrderStr = "";
        String[] gradeTypes = null;
        try {
        	//---add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
    		boolean contractType = false;
    		ParaWithPort para = new ParaWithPort();
    		para.setYssPub(pub);
    		contractType = para.getFutursPositionType(this.portCode);
    		//---add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
        	
            buildLeftSql(leftArr, fieldsArr);
            valCashBeans = new ArrayList();
            if (Grade + 1 == 6) {
                OrderStr = "FCatCode" + dbl.sqlJoinString() + dbl.sqlString("##") +
                    dbl.sqlJoinString() + "FSubCatCode" + dbl.sqlJoinString() +
                    dbl.sqlString("##") + dbl.sqlJoinString() +
                    "FAttrClsCode" + dbl.sqlJoinString() + dbl.sqlString("##") +
                    dbl.sqlJoinString() + "FCuryCode" + dbl.sqlJoinString() +
                    dbl.sqlString("##") + dbl.sqlJoinString() +
                    "FSecurityCode" + dbl.sqlJoinString() + dbl.sqlString("##") +
                    dbl.sqlJoinString() + "FSubTsfTypeCode";
                OrderStr = OrderStr + " as FOrderCode,";
                strDetailSql = " select " + OrderStr +
                    " V.FSubTsfTypeCode," +
                    " Trans.FSubTsfTypeName," +
                    " V.FCuryCode," +
                    " V.FCatCode," +
                    " V.FSubCatCode," +
                    " V.FAttrClsCode," +
                    " V.FSecurityCode," +
                    //" V.FBaseCuryRate," +
                    //" V.FPortCuryRate," +
                    " Rate.FBaseRate as FBaseCuryRate," +
                    " Rate.FPortRate as FPortCuryRate," +
                    dbl.sqlIsNull("FCost", "0") +
                    " as FCost," +
                    //dbl.sqlIsNull("FMVBal","0") +
                    //" as FMVBal," +
                    dbl.sqlIsNull("FPortCost", "0") +
                    " as FPortCost," +
                    //dbl.sqlIsNull("FPortMVBal","0") +
                    //" as FPortMVBal," +
                    dbl.sqlIsNull("FPortFXBal", "0") +
                    " as FPortFXBal," +
                    dbl.sqlIsNull(" v.FMarketPrice","0")+
                    " as FMarketPrice,"+
                    dbl.sqlIsNull("  v.FStorageAmount ", "0")+//add by zhouxiang 2010.11.22 证券借贷--禁止统计表中新增证券库存中应收应付库存的股票数量
                    " as FStorageAmount," +
                    " V.FInvestType from " + //modify by fangjiang 2011.07.23 story 1176
                    tempViewName + " v " +
                    " left join (select FSubTsfTypeCode as TFSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType where FCheckState = 1) Trans on " +
                    " v.FSubTsfTypeCode = Trans.TFSubTsfTypeCode " +
                    " left join (select FCuryCode as RFCuryCode,FBaseRate,FPortRate from " + pub.yssGetTableName("Tb_Data_ValRate") +
                    " where FValDate = " + dbl.sqlDate(this.dDate) + " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " ) Rate on Rate.RFCuryCode = V.FCuryCode" +
                    " where FSubTsfTypeCode <> '09' and V.FNAVDate = " +
                    dbl.sqlDate(this.dDate) + " and FPortCode = " +
                    dbl.sqlString(this.portCode) +
                    " and (FCost <> 0 or FPortCost <> 0 or FPortFXBal <> 0)";
                rs = dbl.queryByPreparedStatement(strDetailSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    navRep = new NavRepBean();
                    navRep.setNavDate(this.dDate); //净值日期
                    navRep.setPortCode(this.portCode);
                    navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                    /*navRep.setKeyCode(rs.getString("FSubTsfTypeCode") == null ?
                                      " " : rs.getString("FSubTsfTypeCode"));*/
                    navRep.setKeyCode( (rs.getString("FSubTsfTypeCode") == null && rs.getString("FSecurityCode") == null) ?
                                      " " : rs.getString("FSecurityCode") + "-" + rs.getString("FSubTsfTypeCode"));
                    navRep.setKeyName(setBlo(Grade) + (rs.getString("FSubTsfTypeName") == null ?
                        " " : rs.getString("FSubTsfTypeName")));
                    navRep.setDetail(0); //明细
                    //------------------设置净值方向----------------------------------
                    if (rs.getString("FSubTsfTypeCode").indexOf("07") >= 0||rs.getString("FSubTsfTypeCode").indexOf("10BSC") >= 0) {//add by zhouxiang 证券借贷 净值统计 2010.12.6
                        navRep.setInOut( -1);
                    } else if (rs.getString("FSubTsfTypeCode").indexOf("06") >= 0) {
                        navRep.setInOut(1);
                    } 
                    //--------------------------------------------------------------
                    navRep.setReTypeCode("Security");
                    navRep.setCuryCode(rs.getString("FCuryCode"));
                    navRep.setBookCost(rs.getDouble("FCost"));
                    if(rs.getString("FSubTsfTypeCode").indexOf("10BSC") >= 0){//证券借贷--原币：借入成本的市值=数量*行情，不取库存界面的数据了，此处的数量包含了送股
                    	
                    	double dMarketValue=YssD.mul(rs.getDouble("FStorageAmount"), rs.getDouble("FMarketPrice"));
                    	navRep.setMarketValue(dMarketValue);
                    	double dValueAdd=YssD.sub(navRep.getMarketValue(),navRep.getBookCost());//原币估增=市值-成本
                    	navRep.setPayValue(dValueAdd);
                    	
                    	navRep.setPortBookCost(rs.getDouble("FPortCost"));
                    	navRep.setPortMarketValue(YssD.div(YssD.mul(dMarketValue, rs.getDouble("FBaseCuryRate")), rs.getDouble("FPortCuryRate")));
                    	navRep.setPortPayValue(YssD.round(YssD.div(YssD.mul(dValueAdd, rs.getDouble("FBaseCuryRate")), rs.getDouble("FPortCuryRate")),2));
                    	//本位币市值=原币市值*基础汇率/组合汇率（此数据已经包含了估增）、本位币估增=原币估增*基础汇率/组合汇率
                    }else{
                    	navRep.setMarketValue(YssD.sub(rs.getDouble("FCost"),
                                0));
                    	navRep.setMarketValue(YssD.sub(rs.getDouble("FCost"),
                                0));
                    	navRep.setPortBookCost(rs.getDouble("FPortCost"));

                    	navRep.setPortMarketValue(YssD.add(navRep.
                                 getPortBookCost(),
                                 rs.getDouble("FPortFXBal")));
                    }
                    navRep.setPortexchangeValue(rs.getDouble("FPortFXBal"));
                    navRep.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                    navRep.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                   
                    navRep.setGradeType1(rs.getString("FCatCode"));
                    navRep.setGradeType2(rs.getString("FSubCatCode"));
                    navRep.setGradeType3(rs.getString("FAttrClsCode"));
                    navRep.setGradeType4(rs.getString("FCuryCode"));
                    navRep.setGradeType5(rs.getString("FSecurityCode"));
                    navRep.setGradeType6(rs.getString("FSubTsfTypeCode"));
                    navRep.setSparAmt(rs.getDouble("FStorageAmount"));//add by zhouxiang 2010.11.22 证券借贷 --净值统计表
                    navRep.setPrice(rs.getDouble("FMarketPrice"));
                    if (!this.invMgrCode.equalsIgnoreCase("total")) {
                        navRep.setInvMgrCode(this.invMgrCode);
                    } else {
                        navRep.setInvMgrCode("total");
                    }
                    navRep.setInvestType(rs.getString("FInvestType")); //add by fangjiang 2011.07.23 story 1176
                    valCashBeans.add(navRep);
                }
                // return valCashBeans;
            }
            dbl.closeResultSetFinal(rs);
            OrderStr = buildOrderStr(groupStr) + " as FOrderCode,";
            sGroupSql = " group by FPortCode," + groupStr;
            strSql = " select  dat.*" +
                (Grade == 1 || Grade == 2 || Grade == 3 ? "" :
                 ",Rate.FBaseRate as FBaseCuryRate" +
                 ",Rate.FPortRate as FPortCuryRate ");
            strSql += "," + fieldsArr.get(Grade - 1);
            strSql += " from (select " +
                OrderStr +
               // modified by zhaoxianlin 20130125 BUG#6653 ---start --//
//                " sum (" + dbl.sqlIsNull("FCost", "0") +
//                ") as FCost," +
//                " sum (" + dbl.sqlIsNull("FMVBal", "0") +
//                ") as FMVBal," +
//                " sum (" + dbl.sqlIsNull("FFXBal", "0") +
//                ") as FFXBal," +
//                " sum (" + dbl.sqlIsNull("FPortCost", "0") +
//                ") as FPortCost," +
//                " sum (" + dbl.sqlIsNull("FPortMVBal", "0") +
//                ") as FPortMVBal," +
//                " sum (" + dbl.sqlIsNull("FPortFXBal", "0") +
//                ") as FPortFXBal," +
                
                /**Start 20140929 modified by liubo.BUG #101482 QDV4光大保德信2014年09月24日
                 * 做正回购业务流程时，产生的汇兑损益在证券库存中，与正确的方向相反*/
//                    " NVL((sum(case when FTradeTypeCode = '24' then (FCost * -1) else FCost end )),0) as FCost, " +
                	" Nvl(sum(FCost),0) as FCost," +
	                " NVL((sum(case when FTradeTypeCode = '24' then (FMVBal * -1) else FMVBal end )),0) as FMVBal, " +
	                " NVL((sum(case when FTradeTypeCode = '24' then (FFXBal * -1) else FFXBal end )),0) as FFXBal, " +
//	                " NVL((sum(case when FTradeTypeCode = '24' then (FPortCost * -1) else FPortCost end )),0) as FPortCost, " +
	                " Nvl(sum(FPortCost),0) as FPortCost," +
	                " NVL((sum(case when FTradeTypeCode = '24' then (FPortMVBal * -1) else FPortMVBal end )),0) as FPortMVBal, " +
	                " NVL((sum(case when FTradeTypeCode = '24' then (FPortFXBal * -1) else FPortFXBal end )),0) as FPortFXBal, " +
	            /**Start 20140929 modified by liubo.BUG #101482 QDV4光大保德信2014年09月24日*/
	                
              //modified by zhaoxianlin 20130125 BUG#6653 ---end --//
                //-----------
//                " sum (" + dbl.sqlIsNull("FStorageAmount", "0") + ") as FSParAmt " +
					" NVL((sum(case when FTradeTypeCode = '24' then (FStorageAmount * -1) else FStorageAmount end )),0) as FSParAmt " +
                //-----------
                (Grade == fields.length ?
                 " ,FISINCode, FExternalCode,FOtPrice1, FOtPrice2,FOtPrice3, FTradeTypeCode,FMarketPrice,FStorageAmount,FInvestType," +//调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323 //modify by fangjiang 2011.07.23 story 1176
                 ( (String) fieldsArr.get(Grade - 1)).split(",")[0] + "," +
                 ( (String) fieldsArr.get(Grade - 1)).split(",")[2] :
                 ( ( (String) fieldsArr.get(Grade - 1)).split(",")[0].length() ==
                  0 ? "" :
                  "," + ( (String) fieldsArr.get(Grade - 1)).split(",")[0])) +
                  //"," + ( (String) fieldsArr.get(Grade - 1)).split(",")[2])) +
                " from " + tempViewName +  // below modified by yeshenghong 20111111 BUG3104
                " dat where (FSubTsfTypeCode = '09' or FSubTsfTypeCode = '' or FSubTsfTypeCode is null)" +
                " and FNAVDate = " + dbl.sqlDate(this.dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                //-------------------------------------------------------------------------------------//去除成本为零的记录 sj add 20080228
                //" and (FCost <> 0 or FPortCost <> 0 or FMVBal <> 0 or FPortMVBal <> 0) " +
//               " and (FCost <> 0 or FPortCost <> 0 or FMVBal <> 0 or FPortMVBal <> 0 or FStorageAmount <> 0) " +//增加了数量的判断。sj edit 20080616
                //----- MS00318 QDV4建行2009年3月16日01_B  更改为组合金额+汇兑损益的值<>0时，才在汇总时获取值，当=0时说明是权证的买空后的值，此数据不需要------
                " and ((FCost <> 0 or FPortCost <> 0 or FMVBal <> 0 or (FPortMVBal + FPortFXBal) <> 0 or FStorageAmount <> 0) " +
                " or((FCost = 0 or FPortCost = 0) and (FSecurityCode in (select a.FSecurityCode  from "+tempViewName+" a"+
                " where a.FSubTsfTypeCode not in ('09')))) )"+//edited by zhouxiang 证券借贷--净值统计表 如果该证券下没有挂应收应应付才不加入2010.12.6
                //-------------------------------------------------------------------------------------------------------------------------------
                sGroupSql +
                //2008.05.13 蒋锦 修改 去掉使用汇率 Gr.oup By
                //(Grade == 1 || Grade == 2 || Grade == 3?"":
                // ",FBaseCuryRate,FPortCuryRate") +
                (Grade == 5 ? ",FISINCode,FExternalCode,FOtPrice1,FOtPrice2,FOtPrice3,FTradeTypeCode,FMarketPrice,FStorageAmount, FInvestType)" ://调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323 //modify by fangjiang 2011.07.23 story 1176
                 ")") + " dat " +
                leftArr.get(Grade - 1) +
                (Grade == 1 || Grade == 2 || Grade == 3 ? "" :
                 " left join (select FCuryCode as RFCuryCode,FBaseRate,FPortRate from " +
                 pub.yssGetTableName("Tb_Data_ValRate") +
                 " where FValDate = " + dbl.sqlDate(this.dDate) +
                 " and FPortCode = " + dbl.sqlString(this.portCode) +
                 " ) Rate on Rate.RFCuryCode = dat.FCuryCode");
            //----------------------------------------------------------------------------------------
            sumRs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            //add by fangjiang 2011.07.23 story 1176
            CtlPubPara pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            boolean flag = false;
            if(pubpara.getParaValue("CtlNvaNameSet", "selPort", "cboIsShow", this.portCode)){
            	flag = true;
            }
            String keyName = "";
            //---------------
            while (sumRs.next()) {         	
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(sumRs.getString("FOrderCode"));
                //--------------数量 add sj ----------------------//
                navRep.setSparAmt(sumRs.getDouble("FSParAmt"));
                //------------------------------------------------
                if (Grade == 5 || Grade == 4) {
                    navRep.setBaseCuryRate(sumRs.getDouble("FBaseCuryRate"));
                    navRep.setPortCuryRate(sumRs.getDouble("FPortCuryRate"));
                }
                switch (Grade) {
                    case 1:
                        navRep.setKeyCode(sumRs.getString("FCatCode") == null ?
                                          " " : sumRs.getString("FCatCode"));
                        navRep.setKeyName(setBlo(Grade) + (sumRs.getString("FCatName") == null ?
                            " " : sumRs.getString("FCatName")));
                        break;
                    case 2:
                        navRep.setKeyCode(sumRs.getString("FSubCatCode") == null ?
                                          " " : sumRs.getString("FSubCatCode"));
                        navRep.setKeyName(setBlo(Grade) + (sumRs.getString("FSubCatName") == null ?
                            " " :
                            sumRs.getString("FSubCatName")));
                        break;
                    case 3:
                        navRep.setKeyCode(sumRs.getString("FAttrClsCode") == null ?
                                          " " : sumRs.getString("FAttrClsCode"));
                        navRep.setKeyName(setBlo(Grade) + (sumRs.getString("FAttrClsName") == null ?
                            " " : sumRs.getString("FAttrClsName")));
                        break;
                    case 4:
                        navRep.setKeyCode(sumRs.getString("FCuryCode") == null ?
                                          " " : sumRs.getString("FCuryCode"));
                        navRep.setKeyName(setBlo(Grade) + (sumRs.getString("FCuryName") == null ?
                            " " : sumRs.getString("FCuryName")));
                        break;
                    case 5:
                        navRep.setKeyCode(sumRs.getString("FSecurityCode") == null ?
                                          " " : sumRs.getString("FSecurityCode"));
                        //add by fangjiang 2011.07.23 story 1176
                        if(flag){
                        	if("C".equalsIgnoreCase(sumRs.getString("FInvestType"))){
                        		keyName = "(交易性)";
                        	}else if("S".equalsIgnoreCase(sumRs.getString("FInvestType"))){
                        		keyName = "(可供出售)";
                        	}else if("F".equalsIgnoreCase(sumRs.getString("FInvestType"))){
                        		keyName = "(持有到期)";
                        	}else if("X".equalsIgnoreCase(sumRs.getString("FInvestType"))){
                        		keyName = "(委托现券)";
                        	}             	
                        }
                    	//-------------------------
                        navRep.setKeyName(setBlo(Grade) + (sumRs.getString("FSecurityName") == null ?
                            " " : sumRs.getString("FSecurityName")+keyName)); //modify by fangjiang 2011.07.23 story 1176
                        break;
                }
                if (Grade == 5) {
                    navRep.setDetail(0);
                } else {
                    navRep.setDetail(Grade); //汇总
                }
                navRep.setReTypeCode("Security");
                if (Grade == fields.length || Grade == fields.length - 1) {
                    navRep.setCuryCode(sumRs.getString("FCuryCode") == null ?
                                       "" : sumRs.getString("FCuryCode"));
                } else {
                    navRep.setCuryCode("汇总：");
                }
                //调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                //if (Grade == 5 && sumRs.getString("FPurchaseType") != null &&
                //    sumRs.getString("FPurchaseType").equalsIgnoreCase("RePh")) { //为正回购的话，乘以-1
                if ((Grade == 5 && sumRs.getString("FTradeTypeCode") !=null &&
                		sumRs.getString("FTradeTypeCode").equalsIgnoreCase("24"))){//调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
	              // 正回购汇总项调整 这里不再*-1  modified by zhaoxianlin 20130125 BUG #6653 正回购估值后净值统计表显示存在问题  ---start//
                    navRep.setBookCost(sumRs.getDouble("FCost"));
                    navRep.setPayValue(sumRs.getDouble("FMVBal"));
                    navRep.setMarketValue(YssD.sub(sumRs.getDouble("FCost"),
                        sumRs.getDouble("FMVBal")));
                    navRep.setPortBookCost(sumRs.getDouble("FPortCost"));
                    navRep.setPortexchangeValue(sumRs.getDouble(
                        "FPortFXBal"));
                    navRep.setPortPayValue(YssD.add(sumRs.getDouble("FPortMVBal")
                       , navRep.getPortexchangeValue()));
                    navRep.setPortMarketValue(YssD.add(navRep.getPortBookCost(),
                        navRep.getPortPayValue()));
                    //--- MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A sj -----------------//

                    /**Start 20140929 modified by liubo.BUG #101482 QDV4光大保德信2014年09月24日*/
//                    navRep.setInOut(-1);//当类型为正回购，则在方向中设置为-1.以便在统计净值时，去除。
                    /**End 20140929 modified by liubo.BUG #101482 QDV4光大保德信2014年09月24日*/
                    
                    //--------------------------------------------------------------------//
                    // 正回购汇总项调整 这里不再*-1  modified by zhaoxianlin 20130125 BUG #6653 正回购估值后净值统计表显示存在问题  ---end//
                } else {
                	//edited by zhouxiang 证券借贷--净值统计表 库存数量为零但应收应付有库存则依然要显示
                	//add by jiangshichao 2011.07.21 期权不核算成本的时候成本也为零，在这里添加添加条件
                	if(sumRs.getDouble("FCost")==0 && sumRs.getDouble("Fsparamt")==0 && !navRep.getOrderKeyCode().split("##")[0].equalsIgnoreCase("FP")){
                		navRep.setBookCost(0);
                        navRep.setPayValue(0);
                        navRep.setMarketValue(0); 
                        navRep.setPortBookCost(0);
                        navRep.setPortexchangeValue(0);
                        navRep.setPortPayValue(0); 
                        navRep.setPortMarketValue(0); 
                	}else{
                		navRep.setBookCost(sumRs.getDouble("FCost"));
                        navRep.setPayValue(sumRs.getDouble("FMVBal"));
                        navRep.setMarketValue(YssD.add(sumRs.getDouble("FCost"),
                            sumRs.getDouble("FMVBal"))); //成本 + 估值增值
                        navRep.setPortBookCost(sumRs.getDouble("FPortCost"));
                        navRep.setPortexchangeValue(sumRs.getDouble("FPortFXBal"));
                        navRep.setPortPayValue(YssD.add(sumRs.getDouble("FPortMVBal")
                            , navRep.getPortexchangeValue())); //估值增值 = 估值增值 + 汇兑损益
                        navRep.setPortMarketValue(YssD.add(navRep.getPortBookCost(),
                            navRep.getPortPayValue())); //成本 + 估值增值
                	}
                	//edited by zhouxiang 证券借贷--净值统计表 库存数量为零但应收应付有库存则依然要显示
                }
               
                gradeTypes = navRep.getOrderKeyCode().split("##");
                switch (gradeTypes.length) {
                    case 1:
                        navRep.setGradeType1(gradeTypes[0]);
                        break;
                    case 2:
                        navRep.setGradeType1(gradeTypes[0]);
                        navRep.setGradeType2(gradeTypes[1]);
                        break;
                    case 3:
                        navRep.setGradeType1(gradeTypes[0]);
                        navRep.setGradeType2(gradeTypes[1]);
                        navRep.setGradeType3(gradeTypes[2]);

                        break;
                    case 4:
                        navRep.setGradeType1(gradeTypes[0]);
                        navRep.setGradeType2(gradeTypes[1]);
                        navRep.setGradeType3(gradeTypes[2]);
                        navRep.setGradeType4(gradeTypes[3]);

                        break;
                    case 5:
                        navRep.setGradeType1(gradeTypes[0]);
                        navRep.setGradeType2(gradeTypes[1]);
                        navRep.setGradeType3(gradeTypes[2]);
                        navRep.setGradeType4(gradeTypes[3]);
                        navRep.setGradeType5(gradeTypes[4]);
                        navRep.setInvestType(sumRs.getString("FInvestType")); //add by fangjiang 2011.07.23 story 1176
                        break;
                }
                //-------xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务----------//
                if (Grade == 5) {
                	if((navRep.getGradeType2().equalsIgnoreCase("FP02")||navRep.getGradeType2().equalsIgnoreCase("FP01"))&&sumRs.getDouble("FOTPrice2")!=0){//股票期权和股指期权
                		navRep.setSparAmt(navRep.getSparAmt()>0?navRep.getSparAmt():YssD.mul(navRep.getSparAmt(), -1));//modify by jiangshichao
                		navRep.setIsinCode(sumRs.getString("FISINCode"));
                        navRep.setSedolCode(sumRs.getString("FExternalCode"));
                        navRep.setPrice(sumRs.getDouble("FOTPrice2"));
                        //navRep.setSparAmt(sumRs.getDouble("FStorageAmount"));
                        navRep.setOtPrice1(sumRs.getDouble("FOTPrice1"));
                        navRep.setOtPrice2(sumRs.getDouble("FMarketPrice"));
                        navRep.setOtPrice3(sumRs.getDouble("FOTPrice3"));
                	}else{
                		//---add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
                		if(navRep.getGradeType2().equalsIgnoreCase("FU01") && contractType){
                			navRep.setSparAmt(navRep.getSparAmt());
                		}else{
                			//---add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
                			navRep.setSparAmt(navRep.getSparAmt()>0?navRep.getSparAmt():YssD.mul(navRep.getSparAmt(), -1));
                		}
                		
                		navRep.setIsinCode(sumRs.getString("FISINCode"));
                        navRep.setSedolCode(sumRs.getString("FExternalCode"));
                        navRep.setPrice(sumRs.getDouble("FMarketPrice"));
                        //navRep.setSparAmt(sumRs.getDouble("FStorageAmount"));
                        navRep.setOtPrice1(sumRs.getDouble("FOTPrice1"));
                        navRep.setOtPrice2(sumRs.getDouble("FOTPrice2"));
                        navRep.setOtPrice3(sumRs.getDouble("FOTPrice3"));
                	}
                }
                //--------------------------------end-----------------------------------//
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }            
                valCashBeans.add(navRep);
            }
            return valCashBeans;

        } catch (Exception e) {
            throw new YssException("自视图获取证券信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(sumRs);
        }
    }

    protected String buildRepView() throws
        YssException {
        String strSql = "";
        String tempViewName = "";
        String strFields = "";
        try {

            //------------------------------------------------------------------------
            strSql =
                " select Security.*," +
                " para.FSecurityName as FSecurityName," +
                " para.FCuryName as FCuryName," +
                " para.FCatCode as FCatCOde," +
                " para.FTradeCury as FCuryCode," +
                " para.FSubCatCode as FSubCatCode," +
                " c.FOTPRICE1,c.FOTPRICE2,c.FOTPRICE3," +
                //" d.FPurchaseType " +
                " d.FTradeTypeCode " +//调整为用交易方式  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                //------------------------------------------------------------------
                " from  (select a1.FSecurityCode   as FSecurityCode," +
                " '09' as FSubTsfTypeCode," +
                " a1.FPortCode       as FPortCode," +
                " a1.FMarketPrice    as FMarketPrice," +
                " a1.FISINCode       as FISINCode," +
                " a1.FExternalCode   as FEXternalCode," +
                " a4.FBaseRate   as FBaseCuryRate," + //sj modified 20090202 QDV4中保2009年01月22日01_B MS00215 直接从汇率表中获取
                " a4.FPortRate   as FPortCuryRate," + //sj modified 20090202 QDV4中保2009年01月22日01_B MS00215 直接从汇率表中获取
                " a1.FStorageAmount  as FStorageAmount," +
                " a1.FVStorageCost   as FCost," +
                " a1.FVBaseCuryCost  as FBaseCost," +
                " a1.FVPortCuryCost  as FPortCost," +
                " a1.FStorageDate as FNAVDate," +
                " a1.FAttrClsCode as FAttrClsCode," +
                " a3.FYKVBal         as FMVBal," +
                " a3.FYKVBaseCuryBal as FBaseMVBal," +
                " a3.FYKVPortCuryBal as FPortMVBal," +
                " a2.FSYVBal         as FFXBal," +
                " a2.FSYVBaseCuryBal as FBaseFXBal," +
                " a2.FSYVPortCuryBal as FPortFXBal," +
                " a1.finvesttype as finvesttype" + //modify by fangjiang 2011.07.23 story 1176
                //---------------------------------------------------------------  获取证券余额(成本)
                " from (select a11.FSecurityCode," +
                //- sj modified 20090202 QDV4中保2009年01月22日01_B MS00215 为了可以通过库存信息连接汇率表，从而获取汇率----//
                " a11.FCuryCode," +
                //--------------------------------------------------------------------------------------------------//
                " a12.FMarketCode," +
                " a11.FPortCode," +
                " a14.FPrice AS FMarketPrice," +
                " a12.FISINCode as FISINCode," +
                " a12.FExternalCode as FExternalCode," +
//               " FBaseCuryRate," + //sj modified 20090202 QDV4中保2009年01月22日01_B MS00215 获取汇率的位置调整
//               " FPortCuryRate," + //sj modified 20090202 QDV4中保2009年01月22日01_B MS00215 获取汇率的位置调整
                " 0 as FBaseCuryRate," + //sj test
                " 0 as FPortCuryRate," + //sj test
                " a11.FStorageDate," +
                " a11.FAttrClsCode," +
                " sum(FStorageAmount) as FStorageAmount," +
                //---------------------------------------------------------------远期类型的成本不计入净值统计,正回购(RePh)成本乘以-1
                " sum(case" +
                " when FCatCode = 'FW' " + //modify by fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A 
                //modify by zhangfa 20100921 MS01752    买入配股权证，估值后净值表数据有问题    QDV4赢时胜深圳2010年9月16日01_B    
                //将FSubCatCode = 'OP02'情况去掉
                //modify by fangjiang 2011.12.17 STORY #1886 把FU去掉
                "  then" +
                //-------------------------------------------------------------------------------------------------------
                " 0" +
                //" when FPurchaseType = 'RePh' then " +          add by zhouwei 20120523 bug 4284 买断式回购
                " when FTradeTypeCode = '24' or FTradeTypeCode = '79' then " +//调整为用交易方式  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                " FVStorageCost*-1 " +  
                " else" +
                " FVStorageCost" +
                " end) as FVStorageCost," +
                " sum(case" +
                " when FCatCode = 'FW' " + //modify by fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A 
                //modify by zhangfa 20100921 MS01752    买入配股权证，估值后净值表数据有问题    QDV4赢时胜深圳2010年9月16日01_B    
                //将FSubCatCode = 'OP02'情况去掉
                //modify by fangjiang 2011.12.17 STORY #1886 把FU去掉
                "  then" +
                //-------------------------------------------------------------------------------------------------------
                " 0" +
                //" when FPurchaseType = 'RePh' then " +
                " when FTradeTypeCode = '24' or FTradeTypeCode = '79' then " +//调整为用交易方式  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                " FVBaseCuryCost*-1 " +
                " else" +
                " FVBaseCuryCost" +
                " end) as FVBaseCuryCost," +
                " sum(case" +
                " when FCatCode = 'FW' " + //modify by fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A 
                //modify by zhangfa 20100921 MS01752    买入配股权证，估值后净值表数据有问题    QDV4赢时胜深圳2010年9月16日01_B    
                //将FSubCatCode = 'OP02'情况去掉
                //modify by fangjiang 2011.12.17 STORY #1886 把FU去掉
                "  then" +
                //------------------------------------------------------------------------------------------------------
                " 0" +
                //" when FPurchaseType = 'RePh' then " +
                " when FTradeTypeCode = '24' or FTradeTypeCode = '79' then " +//调整为用交易方式  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                " FVPortCuryCost*-1 " + 
                " else" +
                " FVPortCuryCost" +
                " end) as FVPortCuryCost," +
                " a11.finvesttype as finvesttype" + //modify by fangjiang 2011.07.23 story 1176
                //----------------------------------------------------------------
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " a11 join (select *" +
                " from " + pub.yssGetTableName("Tb_Para_Security") +
                //优化SQL处理 by leeyu 20100513 合并太平版本代码
                /*" where FCheckState = 1 ) " +
                " a12 on a11.FSecurityCode = a12.FSecurityCode join (select FSecurityCode," +
                " max(FStartDate) as FStartDate" +
                " from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(this.dDate) +
                " and FCheckState = 1 " +
                " group by FSecurityCode) r on a12.FSecurityCode = r.FSecurityCode " +*/
	 		   //将上面的代码更改如下，提高查询效率
                " a121 where FCheckState=1 and FStartDate=(select max(FStartDate) as FStartDate from "+
                pub.yssGetTableName("Tb_Para_Security") +
                " a122 where FStartDate<="+dbl.sqlDate(this.dDate)+               
                " and FCheckState = 1 and a121.FSecurityCode=a122.FSecurityCode )"+
                " )a12 on a11.FSecurityCode = a12.FSecurityCode "+
                //优化SQL处理 by leeyu 20100513 合并太平版本代码
                //--------------------------------------------------------------------------  获取回购类型
                //调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                //" left join (select FSecurityCode,FPurchaseType from " +
                //pub.yssGetTableName("Tb_Para_Purchase") +
                " left join (select distinct FSecurityCode,FTradeTypeCode,FAttrClsCode, FInvestType  from " + //QDV4赢时胜上海2010年04月27日01_AB MS01125 by liuwei  //modify by fangjiang 2011.07.23 story 1176
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where " + dbl.sqlDate(dDate) + " between FBargainDate and FMATUREDATE  and FPortCode ="
                + dbl.sqlString(portCode) + " and FTradeTypeCode in ('24','25','78','79') and FCheckState = 1" + //add by zhouwei 20120523 bug 4284 买断式回购
                //调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                //QDV4赢时胜（测试）2010年03月24日02_B MS00933 关联上场外回购业务数据 by leeyu 20100327
                " union "+
				//QDV4赢时胜上海2010年04月27日01_AB MS01125 by liuwei 
                " select distinct FSecurityCode,FTradeTypeCode, case  when FTradeTypeCode='24' then  'SellRepo' " + 
                "when  FTradeTypeCode='25' then   'AntiRepo'  else   ' '  end as  FAttrClsCode, 'C' as finvesttype "+//alter by liuwei  当同一证券既有正回购又有逆回购会报错  //modify by fangjiang 2011.07.23 story 1176
                " from "+pub.yssGetTableName("Tb_Data_Purchase")+
				//QDV4赢时胜上海2010年04月27日01_AB MS01125 by liuwei 
                " where "+dbl.sqlDate(dDate) + " between FBARGAINDATE and FMATUREDATE and FPortCode ="+
                dbl.sqlString(portCode) + " and FTradeTypeCode in('24','25') and FCheckState=1" +
                //QDV4赢时胜（测试）2010年03月24日02_B MS00933 关联上场外回购业务数据 by leeyu 20100327
                " ) a13 on a11.FSecurityCode = a13.FSecurityCode  and a11.FAttrClsCode=a13.FAttrClsCode and a11.FInvestType = a13.FInvestType" + //modify by fangjiang 2011.07.23 story 1176 //alter by liuwei  当同一证券既有正回购又有逆回购会报错 20100618 QDV4赢时胜上海2010年04月27日01_AB MS01125
                //---------------------------------------------------------------------------  获取行情 2008.08.18 蒋锦 BUG:0000423
                //-----2009.7.9 蒋锦 修改 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A 估值行情表增加了属性分类作为主键------//
                " LEFT JOIN (SELECT a.FValDate, a.FPortCode, a.FSecurityCode, a.FPrice, a.FAttrClsCode" +
                " FROM " + pub.yssGetTableName("TB_Data_ValMktPrice") + " a" +
                " JOIN (SELECT MAX(FValDate) AS FValDate, FSecurityCode, FAttrClsCode" +
                " FROM " + pub.yssGetTableName("TB_Data_ValMktPrice") +
                " WHERE FValDate <= " + dbl.sqlDate(this.dDate) +
               	" and FPortCode = "+dbl.sqlString(this.portCode)+//SQL优化处理 添加组合，缩小查询范围 by leeyu 20100514  合并太平版本代码
                " GROUP BY FSecurityCode, FAttrClsCode) b ON a.FValDate = b.FValDate" +
                " AND a.FSecurityCode = b.FSecurityCode AND a.Fattrclscode = b.FAttrClsCode" +
                " WHERE FPortCode = " + dbl.sqlString(this.portCode) +
                " ) a14 ON a14.FSecurityCode = a11.FSecurityCode AND a14.FAttrClsCode = a11.FAttrClsCode" +
                //------------------------------------------------------------------------------
                " where 1 = 1 and a11.FPortCode = " + dbl.sqlString(this.portCode) +
                " and FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and a11.FCheckState = 1 " + //添加where条件 BugNo:0000381 edit by jc
                //---------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //---------------------------------------------------------------------------
                " group by a11.FPortCode," +
                " a11.FSecurityCode," +
                " a12.FMarketCode," +
                " a14.FPrice," +
//               " FBaseCuryRate," + //sj modified 20090202 QDV4中保2009年01月22日01_B MS00215 直接通过汇率表来获取汇率，而不是通过库存信息获取汇率。用group获取汇率可能引起重复数据
//               "  FPortCuryRate," +//sj modified 20090202 QDV4中保2009年01月22日01_B MS00215 直接通过汇率表来获取汇率，而不是通过库存信息获取汇率。用group获取汇率可能引起重复数据
                "  a12.FISINCode," +
                "  a12.FExternalCode," +
                "  a12.FSubCatCode," +
                "  a11.FCatType," +
                "  a11.FAttrClsCode," + //2009.07.19 明确字段所在表 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A
                "  a11.FCuryCode," +
                "  a11.FStorageDate," +
                "  a11.FInvestType) a1 " + //modify by fangjiang 2011.07.23 story 1176
                //----------------------------------------------------------------------------获取成本和估值增值的汇兑损益
                //-----2009.7.9 蒋锦 修改 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A 应收应付的连接需要使用属性分类---//
                " left join (select a21.FSecurityCode as FSecurityCode2," +
                " a21.FAttrClsCode AS FAttrClsCode2," +
                " FPortCode         as FPortCode2," +
                " sum(FVBal)               as FSYVBal," +
                " sum(FVBaseCuryBal)               as FSYVBaseCuryBal," +
                " sum(FVPortCuryBal)               as FSYVPortCuryBal," +
                " FInvestType " + //modify by fangjiang 2011.07.23 story 1176
                " from " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a21 join (select *  from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1 )" +
                " a22 on a21.FSecurityCode = a22.FSecurityCode " +
                " where 1 = 1 and ((FSubTsfTypeCode like " +
                " '9905%' and " +
                " (FCatCode <>" +
                " 'FU' and" + //modify by fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A 
                //------ modify by wangzuochun 2010.12.08 BUG #554 配股权证在净值表中的市值不对
                " FCatCode <> 'FW'" +
                " )) or " +
                //------ BUG #554 ------//
                " FSubTsfTypeCode like" +
                " '9909%') and " +
                " FPortCode = " + dbl.sqlString(this.portCode) +
                " and FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and a21.FCheckState = 1 " + //添加where条件 BugNo:0000381 edit by jc
                //-------------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //-------------------------------------------------------------------------------
                " group by FPortCode,a21.FSecurityCode, a21.FAttrClsCode, a21.FInvestType) " + //modify by fangjiang 2011.07.23 story 1176
                " a2 on a1.FPortCode = a2.FPortCode2 and a1.FSecurityCode = a2.FSecurityCode2 AND a1.Fattrclscode = a2.FAttrClsCode2 and a1.FInvestType = a2.FInvestType " + //modify by fangjiang 2011.07.23 story 1176
                //-------------------------------------------------------------------------------//获取估值增值
                //-----2009.7.9 蒋锦 修改 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A 应收应付的连接需要使用属性分类---//
                " left join (select a31.FSecurityCode as FSecurityCode3," +
                " a31.FAttrClsCode AS FAttrClsCode3," +
                " FPortCode         as FPortCode3, " +
                " sum(FVBal)               as FYKVBal," +
                " sum(FVBaseCuryBal)               as FYKVBaseCuryBal," +
                " sum(FVPortCuryBal)               as FYKVPortCuryBal, " +
                " FInvestType " + //modify by fangjiang 2011.07.23 story 1176
                " from " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a31 join (select * from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) a32 on a31.FSecurityCode = a32.FSecurityCode " +
                " where FTsfTypeCode = '09' and FPortCode = " +
                dbl.sqlString(this.portCode) +
                " and FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and a31.FCheckState = 1 " + //添加where条件 BugNo:0000381 edit by jc
                //--------------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //--------------------------------------------------------------------------------
                " group by FPortCode, " +
                " a31.FSecurityCode, a31.Fattrclscode, a31.FInvestType) a3 on a1.FPortCode = a3.FPortCode3 and a1.FSecurityCode = a3.FSecurityCode3 AND a1.FAttrClsCode = a3.FAttrClsCode3 and a1.FInvestType = a3.FInvestType" + //modify by fangjiang 2011.07.23 story 1176
                //------------------------- QDV4中保2009年01月22日01_B MS00215 非小数位引起的错误，在此处增加对汇率的获取 sj modified 20090202----//
                " left join (select FCuryCode, FBaseRate, FPortRate from " + pub.yssGetTableName("Tb_Data_ValRate") +
                "  where FPortCode = " + dbl.sqlString(this.portCode) +
                " and FValDate = " + dbl.sqlDate(this.dDate) +
                " ) a4 on a1.FCuryCode = a4.FCurycode " + //直接通过汇率表来获取汇率，而不是通过库存信息获取汇率。用group获取汇率可能引起重复数据
                //------------------------------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------------
                " union " + //以下union的是所有应收应付的数据
//------------------------------------------------------------------------------------------------------
                " select b1.FSecurityCode1 as FSecurityCode," +
                " b1.FSubTsfTypeCode as FSubTsfTypeCode," +
                " b1.FPortCode1 as FPortCode," +
                " b3.fmarketprice3 as FMarketPrice," +
                " ' ' as FISINCode," +
                " ' ' as FEXternalCode," +
                " 0 as FBaseCuryRate," +
                " 0 as FPortCuryRate," +
                //" 0 as FStorageAmount," +//771 证券借贷业务需求-净值统计  edited by zhouxiang 2010.11.19 证券库存专用取数规则  注释掉使用FAmount 字段
                " FAmount as FStorageAmount," +
                " b1.FLXVBal as FCost," +
                " b1.FLXVBaseCuryBal as FBaseCost," +
                " b1.FLXVPortCuryBal as FPortCost," +
                " b1.FStorageDate as FStorageDate," +
                " b1.FAttrClsCode as FAttrClsCode," +
                " 0 as FMVBal," +
                " 0 as FBaseMVBal," +
                " 0 as FPortMVBal," +
                " b2.FFXBal as FFXBal," +
                " b2.FBaseFXBal as FBaseFXBal," +
                " b2.FPortFXBal as FPortFXBal," +
                " b1.FInvestType as FInvestType " + //modify by fangjiang 2011.07.23 story 1176
                " from ( " +
                //--------------------------------------------------------------------获取应收应付的数据
                " select b11.FSecurityCode as FSecurityCode1," +
                " FPortCode as FPortCode1," +
                " FSubTsfTypeCode as FSubTsfTypeCode," +
                " b11.FStorageDate as FStorageDate," +
                " b11.FAttrClsCode as FAttrClsCode," +
                " sum(FVBal) as FLXVBal," +
                " sum(FVBaseCuryBal) as FLXVBaseCuryBal," +
                " sum(FVPortCuryBal) as FLXVPortCuryBal," +
                " sum(FAmount) as FAmount, "+//edited by zhouxiang 771 2010.11.19 证券借贷估值净值报表
                " b11.FInvestType as FInvestType " + //modify by fangjiang 2011.07.23 story 1176
                " from " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " b11 join (select FSecurityCode," +
                " FSecurityName" +
                " from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) b12 on b11.FSecurityCode = b12.FSecurityCode " +
                //2009.09.05 蒋锦 添加 溢折价的应收应付
                //MS00656 QDV4赢时胜(上海)2009年8月24日01_A
                
                //add by zhouxiang 771  证券借贷估值净值报表 2010.11.19
                /*" where 1 = 1 and (FTsfTypeCode = '06' or FTsfTypeCode = '07' or FTsfTypeCode = '05') and" +
                " ((FSubTsfTypeCode like '06%' or FSubTsfTypeCode like '07%' or FSubTsfTypeCode like '05FI%') and" +
                " FSubTsfTypeCode not like '06%DV%' and FSubTsfTypeCode <> '07FI') and FPortCode = " + dbl.sqlString(this.portCode) +*/
                " where 1 = 1 and FTsfTypeCode in('06','07','05','09','10')  and" +
                " ((FSubTsfTypeCode like '06%' or FSubTsfTypeCode like '07%' or FSubTsfTypeCode like '05FI%'  or FSubTsfTypeCode like '10BSC%' or"
                 +" FSubTsfTypeCode like '09BI%') and" +
                " FSubTsfTypeCode not like '06%DV%' and FSubTsfTypeCode <> '07FI') and FPortCode = " + dbl.sqlString(this.portCode) +
                //end by zhouxiang 771  证券借贷估值净值报表 2010.11.19
                " and" +
                " FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and FCheckState = 1 " + //添加where条件 BugNo:0000381 edit by jc
                //----------------------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //----------------------------------------------------------------------------------------
                " group by FPortCode, b11.FSecurityCode, b11.Fsubtsftypecode, b11.FStorageDate, b11.FAttrClsCode, b11.FInvestType) b1 " +  //modify by fangjiang 2011.07.23 story 1176
                //--------------------------------------------------------------------------------------------------------获取应收应付的汇兑损益
                //-----2009.7.9 蒋锦 修改 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A 应收应付的连接需要使用属性分类---//
                " left join (select b21.FSecurityCode as FSecurityCode2," +
                " b21.FAttrClsCode AS FAttrClsCode2," +
                " FSubTsfTypeCode," +
                //dbl.sqlRight("b21.FSubTsfTypeCode", 4) + " as FTsfTypeCode," + //增加一个字段，以便在后面作为on的关键字 sj edit 20080424
                
                //20130128 modified by liubo.3208需求的一个问题
                //应该只有9907PLI(借贷利息汇兑损益)这个业务子类型的汇兑损益需要截从右截5位字符串，而其他的业务子类型都应该截4位
                //=============================================
//                dbl.sqlRight("b21.FSubTsfTypeCode", 5) + " as FTsfTypeCode," +  //modified by zhaoxianlin 20121107 STORY #3208 没有关联到借贷利息汇兑损益，这里取后5位
                //====================end=========================
                
                /**Start 20131011 modified by liubo.Bug #80794.QDV4赢时胜(深圳)2013年10月09日01_B
                 * 9907PLI之外的业务类型的截位不正确。绝大多数情况下汇兑损益的业务类型是99XXXX，从右截4位可以正确将99过滤，截5位则无法正确过滤*/
                " (case when FSubTsfTypeCode = '9907PLI' then substr(b21.FSubTsfTypeCode, -5) "+
				//增加 ,嘉实资本利得税没有计算汇兑损益  dongqingsong 2013-10-11 BUG #79704
                " when FSubTsfTypeCode ='9907CGT_EQ' then substr(b21.FSubTsfTypeCode, -8)"+
                " when FSubTsfTypeCode ='9907CGT_FI' then substr(b21.FSubTsfTypeCode, -8)"+
                " when FSubTsfTypeCode ='9907CGT_TR' then substr(b21.FSubTsfTypeCode, -8)"+
                " when FSubTsfTypeCode ='9907CGT_DR' then substr(b21.FSubTsfTypeCode, -8)"+
                " when FSubTsfTypeCode ='9907CGT_RT' then substr(b21.FSubTsfTypeCode, -8)"+
                " when FSubTsfTypeCode ='9907CGT_OP' then substr(b21.FSubTsfTypeCode, -8)"+
                //增加 ,嘉实资本利得税没有计算汇兑损益  dongqingsong 2013-10-11 BUG #79704
				"else substr(b21.FSubTsfTypeCode, -4) end) as FTsfTypeCode," +  //bugBUG #79704  add dongqingsong 2013-09-24
                /**End 20131011 modified by liubo.Bug #80794.QDV4赢时胜(深圳)2013年10月09日01_B*/
                
                " FPortCode as FPortCode2," +
                " sum(FVBal) as FFXBal," +
                " sum(FVBaseCuryBal) as FBaseFXBal," +
                " sum(FVPortCuryBal) as FPortFXBal," +
                " b21.FInvestType as FInvestType " + //modify by fangjiang 2011.07.23 story 1176
                //" from Tb_001_Stock_SecRecPay b21 join (select FSecurityCode," +
                " from " + pub.yssGetTableName("Tb_Stock_SecRecPay") + " b21 join (select FSecurityCode," + //
                " FSecurityName" +
                " from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) b22 on b21.FSecurityCode = b22.FSecurityCode " +
                " where FTsfTypeCode = '99' and " +
                " (FSubTsfTypeCode like" +
                " '9907%' or" +
                " FSubTsfTypeCode like" +
                " '9906%') and" +
                " FPortCode = " + dbl.sqlString(this.portCode) +
                " and FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and FCheckState = 1 " + //添加where条件 BugNo:0000381 edit by jc
                //--------------------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //--------------------------------------------------------------------------------------
                " group by b21.FPortCode," +
                " b21.FSecurityCode," +
                " FSubTsfTypeCode, b21.Fattrclscode, b21.FInvestType) b2 on b1.FSecurityCode1 = b2.FSecurityCode2 and b1.FPortCode1 = b2.FPortCode2 and b1.FSubTsfTypeCode = " +
                " b2.FTsfTypeCode " + //改为之前加上的新字段 sj edit 20080424
                //dbl.sqlRight("b2.FSubTsfTypeCode", 4) +
                " AND b1.FAttrClsCode = b2.FAttrClsCode2 and b1.FInvestType = b2.FInvestType " + //modify by fangjiang 2011.07.23 story 1176
                //add by zhouxiang 2010.11.22 证券借贷-净值统计表--库存（应收应付余额部分库存）的行情也从估值含行情表里取数--------------------
                " left join (SELECT a.FValDate as fvaldate3,"+//-------------应收应付估值行情数据
                " a.FPortCode as fportcode3,a.FSecurityCode as FSecurityCode3 ,a.FPrice as fmarketprice3,"+
                 " a.FAttrClsCode as FAttrClsCode3 FROM "+pub.yssGetTableName("TB_Data_ValMktPrice")+
                 " a JOIN (SELECT MAX(FValDate) AS FValDate,FSecurityCode, FAttrClsCode FROM " +
                 pub.yssGetTableName("TB_Data_ValMktPrice")+
                  " WHERE FValDate <= "+dbl.sqlDate(this.dDate)+
                   " and FPortCode = "+dbl.sqlString(this.portCode)+
                   " GROUP BY FSecurityCode, FAttrClsCode) b ON a.FValDate =b.FValDate"+
                   " AND a.FSecurityCode =b.FSecurityCode AND a.Fattrclscode =b.FAttrClsCode"+
                   " WHERE FPortCode = "+dbl.sqlString(this.portCode)+") b3 on b1.FSecurityCode1 =b3.FSecurityCode3"+
                   " and b1.FPortCode1=b3.fportcode3  and b1.FAttrClsCode=b3.FAttrClsCode3"+
                //end by zhouxiang 2010.11.22 证券借贷-净值统计表--库存（应收应付余额部分库存）的行情也从估值含行情表里取数--------------------
                ") Security " + //以上都是净值统计时需要的数据
                //--------------------------------------------------------------------------------------------------------------------
                " left join (select FSecurityCode, FSecurityName, FCatCode,FSubCatCode,b1.FTradeCury, b2.FCuryName " +
                //证券信息设置中启用日期不是主键，因此去掉对启用日期的判断，提高sql查询效率 panjunfang modify 20110722
                " from (select * " +
                " from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) b1 left join (select FCuryCode," + 
                ////END 证券信息设置中启用日期不是主键，因此去掉对启用日期的判断，提高sql查询效率 panjunfang modify 20110722
                " FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) b2 on b1.FTradeCury = b2.FCuryCode) para on Security.FSecurityCode = para.FSecurityCode " +
                //----------------------------------------------------------------------------------------//sj 获取估值行情的其他行情
                //-----2009.7.9 蒋锦 修改 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A 估值行情表增加了属性分类作为主键------//
                " left join (select FOTPRICE1,FOTPRICE2,FOTPRICE3,FSecurityCode,FAttrClsCode from " +
                pub.yssGetTableName("Tb_Data_ValMktPrice") +
                " where FValDate = " + dbl.sqlDate(dDate) + " and FPortCode = " +
                dbl.sqlString(portCode) +
                " ) c on Security.FSecurityCode = c.FSecurityCode AND Security.FAttrClsCode = c.FAttrClsCode" +
                //----------------------------------------------------------------------------------------//sj 20071118 获取回购类型
                //调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                //" left join (select FSecurityCode,FPurchaseType from " +
                //pub.yssGetTableName("Tb_Para_Purchase") +
                " left join (select distinct FSecurityCode,FTradeTypeCode,FAttrClsCode, FInvestType from " +//QDV4赢时胜上海2010年04月27日01_AB MS01125 by liuwei //modify by fangjiang 2011.07.23 story 1176
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where " + dbl.sqlDate(dDate) + " between FBargainDate and FMATUREDATE  and FPortCode ="
                + dbl.sqlString(portCode) + " and FTradeTypeCode in ('24','25','78','79') and FCheckState = 1" +//add by zhouwei 20120523 bug 4284 买断式回购
                //调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                //QDV4赢时胜（测试）2010年03月24日02_B MS00933 关联上场外回购业务数据 by leeyu 20100327
                " union "+
				//QDV4赢时胜上海2010年04月27日01_AB MS01125 by liuwei 
                " select distinct FSecurityCode,FTradeTypeCode, case when FTradeTypeCode='24' then  'SellRepo' " +
                "when  FTradeTypeCode='25' then   'AntiRepo'  else   ' '  end as  FAttrClsCode, 'C' as FInvestType"+ //alter by liuwei 当同一证券既有正回购又有逆回购会报错 20100618 //modify by fangjiang 2011.07.23 story 1176
                " from "+pub.yssGetTableName("Tb_Data_Purchase")+
				//QDV4赢时胜上海2010年04月27日01_AB MS01125 by liuwei 
                " where "+dbl.sqlDate(dDate) + " between FBARGAINDATE and FMATUREDATE and FPortCode ="+
                dbl.sqlString(portCode) + " and FTradeTypeCode in('24','25') and FCheckState=1" +
                //QDV4赢时胜（测试）2010年03月24日02_B MS00933 关联上场外回购业务数据 by leeyu 20100327
                " ) d on Security.FSecurityCode = d.FSecurityCode and Security.FAttrClsCode=d.FAttrClsCode and Security.FInvestType = d.FInvestType ";//alter by liuwei 当同一证券既有正回购又有逆回购会报错 20100618//QDV4赢时胜上海2010年04月27日01_AB MS01125 //modify by fangjiang 2011.07.23 story 1176

//------------------------------------------------------------------------------------------------------
            //} //在此处拼写所需数据的sql语句
            //------------------------------------------------------------------------
       //  synchronized(YssGlobal.objSecRecLock){//add by lidaolong 20110422 BUG #4606 :: 系统里在建临时的表和视图时，系统就会报错
			///**Start---panjunfang 2014-1-9*/
			// 表名拼接用户名代码，若用户名代码较长的情况下，会导致创建临时表报表名过长的错误
			//因此改为取用户id
            tempViewName = "V_Temp_Security_" + pub.getUserID();
			/**End---panjunfang 2014-1-9  */
            //add by jiangshichao 净值统计表的临时表类型是table,而证券借贷的临时表类型是View 所以这里都需要进行判断。
            if(dbl.yssViewExist(tempViewName)){
            	dbl.executeSql("drop view " + tempViewName);
            }
            if (dbl.yssTableExist(tempViewName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
            	dbl.executeSql(dbl.doOperSqlDrop("drop table " + tempViewName));
            	/**end*/
            }
            strFields = "FSecurityCode,FSubTsfTypeCode,FportCode,FMarketPrice,FISINCode,FEXternalCode,FBaseCuryRate,FPortCuryRate," +
                "FStorageAmount,FCost,FBaseCost,FPortCost,FNavDate,FAttrClsCode,FMVBal,FBaseMVBal,FPortMVBal,FFXBal,FBaseFXBal," +
                //"FPortFXBal,FSecurityName,FCUryName,FCatCode,FCuryCode,FSubCatCode,FotPrice1,FotPrice2,FotPrice3,Fpurchasetype";
                "FPortFXBal,FInvestType,FSecurityName,FCUryName,FCatCode,FCuryCode,FSubCatCode,FotPrice1,FotPrice2,FotPrice3,FTradeTypeCode ";//调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323 //modify by fangjiang 2011.07.23 story 1176
            if (dbl.getDBType() == YssCons.DB_ORA) {
            	
                StringBuffer buf= new StringBuffer();
                buf.append("create table ").append(tempViewName);
                buf.append(" ( FSECURITYCODE   VARCHAR2(50), ");
                buf.append(" FSUBTSFTYPECODE VARCHAR2(20), ");
                buf.append(" FPORTCODE       VARCHAR2(20), ");
                buf.append(" FMARKETPRICE    NUMBER(20,12), ");
                buf.append(" FISINCODE       VARCHAR2(50), ");
                buf.append(" FEXTERNALCODE   VARCHAR2(50), ");
                buf.append(" FBASECURYRATE   NUMBER(20,15), ");
                buf.append(" FPORTCURYRATE   NUMBER(20,15), ");
                buf.append(" FSTORAGEAMOUNT  NUMBER(18,4), ");
                buf.append(" FCOST           NUMBER(18,4), ");
                buf.append(" FBASECOST       NUMBER(18,4), ");
                buf.append(" FPORTCOST       NUMBER(18,4), ");
                buf.append(" FNAVDATE        DATE, ");
                buf.append(" FATTRCLSCODE    VARCHAR2(20), ");
                buf.append(" FMVBAL          NUMBER(18,4), ");
                buf.append(" FBASEMVBAL      NUMBER(18,4), ");
                buf.append(" FPORTMVBAL      NUMBER(18,4), ");
                buf.append(" FFXBAL          NUMBER(18,4), ");
                buf.append(" FBASEFXBAL      NUMBER(18,4), ");
                buf.append(" FPORTFXBAL      NUMBER(18,4), ");
                buf.append(" FSECURITYNAME   VARCHAR2(100), ");
                buf.append(" FCURYNAME       VARCHAR2(50), ");
                buf.append(" FCATCODE        VARCHAR2(20), ");
                buf.append(" FCURYCODE       VARCHAR2(20), ");
                buf.append(" FSUBCATCODE     VARCHAR2(20), ");
                buf.append(" FOTPRICE1       NUMBER(20,12), ");
                buf.append(" FOTPRICE2       NUMBER(20,12), ");
                buf.append(" FOTPRICE3       NUMBER(20,12), ");
                buf.append(" FTradeTypeCode   VARCHAR2(20), ");
                buf.append(" FInvestType      VARCHAR2(20), "); //add by fangjiang 2011.07.23 story 1176
                buf.append(" constraint PK_").append(tempViewName);
                buf.append(" primary key (FSECURITYCODE, FSUBTSFTYPECODE, FPORTCODE,FNAVDATE,FATTRCLSCODE,FCATCODE,FCURYCODE,FSUBCATCODE, FInvestType)"); //modify by fangjiang 2011.07.23 story 1176
                buf.append(" ) ");
            	
                dbl.executeSql(buf.toString());
                String tempStr = "insert into " + tempViewName + "(" + strFields + ")  (" +
                    strSql +
                    ")";
                dbl.executeSql(tempStr);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                String sql = "create table " + tempViewName + "( " + strFields + " ) as " + strSql;
                dbl.executeSql(sql);
                //dbl.executeSql("insert into " + tempViewName + "(" + strSql +
                // ")");
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
        // } 
            //STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18
            optionAccountCostDeal(tempViewName);
            
            
            return tempViewName;

        } catch (Exception e) {
            throw new YssException("生成证券视图出错！");
        }
    }

    /********************************************************************************
     * STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  
     * @param tempViewName
     * @throws YssException
     */
    private void optionAccountCostDeal(String tempViewName) throws YssException{
    	
        EachGetPubPara pubPara = new EachGetPubPara();;
        pubPara.setYssPub(pub);
        String sCostAccount_Para="";
        
    	ResultSet rs = null;
    	StringBuffer buff = new StringBuffer();
    	PreparedStatement pst = null;
    	
    	try{
    		buff.append(" select * from ").append(tempViewName);
    		buff.append(" where fcatcode='FP'");
    		rs = dbl.queryByPreparedStatement(buff.toString()); //modify by fangjiang 2011.08.14 STORY #788
    		while(rs.next()){
    			pubPara.setSPortCode(rs.getString("fportcode"));
    			pubPara.setSPubPara(rs.getString("fsecuritycode"));
    			pubPara.setsDate(YssFun.formatDate(rs.getDate("FNAVDATE")));
    			sCostAccount_Para = pubPara.getOptCostAccountSet();
    			if(sCostAccount_Para.equalsIgnoreCase("false")){
    				if(pst == null){
    					pst = dbl.getPreparedStatement(" update "+tempViewName +" set FCost=0,FBaseCost=0,FPortCost=0 where fsecuritycode=? and fsubcatcode=? ");  //modify by fangjiang 2011.08.14 STORY #788
    				}
    				pst.setString(1, rs.getString("fsecuritycode"));
    				pst.setString(2, rs.getString("fsubcatcode"));
    				pst.addBatch();
    				//dbl.executeSql(" update "+tempViewName +" set FCost=0 where fsecuritycode="+dbl.sqlString(rs.getString("fsecuritycode"))+" and fsubcatcode= "+dbl.sqlString(rs.getString("fsubcatcode")));
    			}
    		}
    		if(pst != null){
    			pst.executeBatch();
    		}
    		
    	}catch(Exception e){
    		throw new YssException("");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		dbl.closeStatementFinal(pst);
    	}
    }
    
    
    
    public void buildLeftSql(ArrayList leftArr, ArrayList fieldsArr) {
        String leftSql = "";
        String fields = "";
        //----------------------------------------
        leftSql = " left join (select FCatCode as PFCatCode,FCatName from Tb_Base_Category where FCheckState = 1)" +
            " para on dat.FCatCode = para.PFCatCode";
        fields = "dat.FCatCode,FCatName"; // modified by yeshenghong 20111111 BUG3104
        leftArr.add(0, leftSql);
        fieldsArr.add(0, fields);
        //-----------------------------------------
        leftSql = " left join (select FSubCatCode as PFSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1)" +
            " para on dat.FSubCatCode = para.PFSubCatCode";
        fields = "FSubCatCode,FSubCatName";
        leftArr.add(1, leftSql);
        fieldsArr.add(1, fields);
        //---------------------------------------
        leftSql =
            " left join (select FAttrClsCode as PFAttrClsCode,FAttrClsName from " +
            pub.yssGetTableName("Tb_Para_AttributeClass") +
            " where FCheckState = 1)" +
            " para on dat.FAttrClsCode = para.PFAttrClsCode";
        fields = "FAttrClsCode,FAttrClsName";
        leftArr.add(2, leftSql);
        fieldsArr.add(2, fields);
        //----------------------------------------
        leftSql = " left join (select FCuryCode as PFCuryCode,FCuryName from " +
            pub.yssGetTableName("tb_para_Currency") +
            " where FCheckState =1) para on dat.FCuryCode = para.PFCuryCode";
        fields = "FCuryCode,FCuryName";
        leftArr.add(3, leftSql);
        fieldsArr.add(3, fields);
        //----------------------------------------
        leftSql =
            " left join (select FSecurityCode as PFSecurityCode,FSecurityName from " +
            pub.yssGetTableName("Tb_Para_Security") +
            " where FCheckState = 1 ) para on dat.FSecurityCode = para.PFSecurityCode";
        fields = "FSecurityCode,FSecurityName,FCuryCode";
        leftArr.add(4, leftSql);
        fieldsArr.add(4, fields);
        //---------------------------------------
    }

}
