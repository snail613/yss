package com.yss.main.operdeal.report.navrep;

import com.yss.util.YssException;
import java.util.ArrayList;
import com.yss.util.YssCons;
import com.yss.util.YssFun;
import com.yss.util.YssD;
import com.yss.main.operdeal.report.navrep.pojo.NavRepBean;
import java.sql.ResultSet;

public class NavMortgageSec
    extends BaseNavRep {

	public String sBorker;
	public String sMortgage;
    public NavMortgageSec() {
    	
    }

    public void initReport(java.util.Date dDate, String sPortCode,
                              String sBorker,String sMortgage) throws YssException {
        this.valDefine =
            "FCatCode;FSubCatCode;FAttrClsCode;FCuryCode;FSecurityCode";
        this.dDate = dDate;
        this.portCode = sPortCode;
        this.sBorker = sBorker;
        this.sMortgage=sMortgage;
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
                    " as FStorageAmount from " +
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
                rs = dbl.openResultSet(strDetailSql);
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
                " sum (" + dbl.sqlIsNull("FCost", "0") +
                ") as FCost," +
                " sum (" + dbl.sqlIsNull("FMVBal", "0") +
                ") as FMVBal," +
                " sum (" + dbl.sqlIsNull("FFXBal", "0") +
                ") as FFXBal," +
                " sum (" + dbl.sqlIsNull("FPortCost", "0") +
                ") as FPortCost," +
                " sum (" + dbl.sqlIsNull("FPortMVBal", "0") +
                ") as FPortMVBal," +
                " sum (" + dbl.sqlIsNull("FPortFXBal", "0") +
                ") as FPortFXBal," +
                //-----------
                " sum (" + dbl.sqlIsNull("FStorageAmount", "0") +
                ") as FSParAmt " +
                //-----------
                (Grade == fields.length ?
                 " ,FISINCode, FExternalCode,FOtPrice1, FOtPrice2,FOtPrice3, FTradeTypeCode,FMarketPrice,FStorageAmount," +//调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                 ( (String) fieldsArr.get(Grade - 1)).split(",")[0] + "," +
                 ( (String) fieldsArr.get(Grade - 1)).split(",")[2] :
                 ( ( (String) fieldsArr.get(Grade - 1)).split(",")[0].length() ==
                  0 ? "" :
                  "," + ( (String) fieldsArr.get(Grade - 1)).split(",")[0])) +
                " from " + tempViewName +
                " where (FSubTsfTypeCode = '09' or FSubTsfTypeCode = '' or FSubTsfTypeCode is null)" +
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
                (Grade == 5 ? ",FISINCode,FExternalCode,FOtPrice1,FOtPrice2,FOtPrice3,FTradeTypeCode,FMarketPrice,FStorageAmount)" ://调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                 ")") + " dat " +
                leftArr.get(Grade - 1) +
                (Grade == 1 || Grade == 2 || Grade == 3 ? "" :
                 " left join (select FCuryCode as RFCuryCode,FBaseRate,FPortRate from " +
                 pub.yssGetTableName("Tb_Data_ValRate") +
                 " where FValDate = " + dbl.sqlDate(this.dDate) +
                 " and FPortCode = " + dbl.sqlString(this.portCode) +
                 " ) Rate on Rate.RFCuryCode = dat.FCuryCode");
            //----------------------------------------------------------------------------------------
            sumRs = dbl.openResultSet(strSql);
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
                        navRep.setKeyName(setBlo(Grade) + (sumRs.getString("FSecurityName") == null ?
                            " " : sumRs.getString("FSecurityName")));
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
                if (Grade == 5 && sumRs.getString("FTradeTypeCode") !=null &&
                		sumRs.getString("FTradeTypeCode").equalsIgnoreCase("24")){//调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                    navRep.setBookCost(YssD.mul(sumRs.getDouble("FCost"), -1));
                    navRep.setPayValue(YssD.mul(sumRs.getDouble("FMVBal"), -1));
                    navRep.setMarketValue(YssD.mul(YssD.sub(sumRs.getDouble("FCost"),
                        sumRs.getDouble("FMVBal")), -1));
                    navRep.setPortBookCost(YssD.mul(sumRs.getDouble("FPortCost"), -1));
                    navRep.setPortexchangeValue(YssD.mul(sumRs.getDouble(
                        "FPortFXBal"), -1));
                    navRep.setPortPayValue(YssD.add(YssD.mul(sumRs.getDouble("FPortMVBal"),
                        -1), navRep.getPortexchangeValue()));
                    navRep.setPortMarketValue(YssD.add(navRep.getPortBookCost(),
                        navRep.getPortPayValue()));
                    //--- MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A sj -----------------//
                    navRep.setInOut(-1);//当类型为正回购，则在方向中设置为-1.以便在统计净值时，去除。
                    //--------------------------------------------------------------------//
                } else {
                	//edited by zhouxiang 证券借贷--净值统计表 库存数量为零但应收应付有库存则依然要显示
                	if(sumRs.getDouble("FCost")==0 && sumRs.getDouble("Fsparamt")==0){
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
                		navRep.setSparAmt(navRep.getSparAmt()>0?navRep.getSparAmt():YssD.mul(navRep.getSparAmt(), -1));
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
        	String sqlBorker="and a.fbrokercode = "+dbl.sqlString(this.sBorker);
        	String sqlMortgage="and a.fcollateralcode = "+dbl.sqlString(this.sMortgage);
            //------------------------------------------------------------------------抵押物净值表
        	strSql="select Security.*, para.FSecurityName as FSecurityName,para.FCuryName as FCuryName,para.FCatCode as FCatCOde,para.FTradeCury as FCuryCode," 
        		+"para.FSubCatCode as FSubCatCode, c.FOTPRICE1,c.FOTPRICE2,c.FOTPRICE3,d.FTradeTypeCode from (select a1.FSecurityCode as FSecurityCode,"
        		+"'09' as FSubTsfTypeCode,a1.FPortCode as FPortCode,a1.FMarketPrice as FMarketPrice,a1.FISINCode as FISINCode,a1.FExternalCode as FEXternalCode,"
        		+"a4.FBaseRate as FBaseCuryRate,a4.FPortRate as FPortCuryRate,a1.FStorageAmount as FStorageAmount, (a1.FStorageAmount* a1.FMarketPrice) as FCost,a1.FVBaseCuryCost as FBaseCost,"
        		+"(a1.FStorageAmount* a1.FMarketPrice* a4.FBaseRate/a4.FPortRate) as FPortCost,a1.FStorageDate as FNAVDate,a1.FAttrClsCode as FAttrClsCode,a3.FYKVBal as FMVBal,a3.FYKVBaseCuryBal as FBaseMVBal,"
        		+"a3.FYKVPortCuryBal as FPortMVBal,a2.FSYVBal as FFXBal,a2.FSYVBaseCuryBal as FBaseFXBal,a2.FSYVPortCuryBal as FPortFXBal "
        		//a11表取证券信息， 内容，证券数量，a14取行情价格，a12去证券信息设置中的信息
        		+" from (select a11.FSecurityCode, a11.FCuryCode,a12.FMarketCode, a11.FPortCode,a14.FPrice AS FMarketPrice,a12.FISINCode as FISINCode, a11.fbrokercode as FExternalCode,"
        		+" 0 as FBaseCuryRate,0 as FPortCuryRate,a11.FStorageDate, a11.FAttrClsCode,sum(FStorageAmount) as FStorageAmount, 0 as FVStorageCost,"
        		//抵押物证券类型
        		+"0 as FVBaseCuryCost,0 as FVPortCuryCost from (select c.fsecuritycode, t.fbrokercode, 1 as fcheckstate, e.ftradecury as FCuryCode,"
        		+" a.fportcode,e.fcatcode as FAttrClsCode,e.fcatcode as FCatType, c.famount* c.finout as FStorageAmount,"+dbl.sqlDate(this.dDate)+" as FStorageDate,"
        		+"0 as FVStorageCost,0 as FVBaseCuryCost,0 as FVPortCuryCost from "+pub.yssGetTableName("tb_Data_CollateralAdd")
        		+" a left join (select b.famount,b.fsecuritycode,b.fsecurityname,x.finout,b.fcollateralcode,to_date(ftransferdate, 'yyyy-MM-dd') as ftransferdate"
        		+" from "+pub.yssGetTableName("tb_data_collateralsec")
        		+" b join (select sum(a.finout) as finout,a.fsecuritycode,a.fcollateralcode from " + pub.yssGetTableName("tb_data_collateralsec") + " a"
        		+" where a.fcheckstate = 1  and to_date(a.ftransferdate,'yyyy-MM-dd')<="+dbl.sqlDate(this.dDate)+"group by a.fsecuritycode, a.fcollateralcode) x on b.fcollateralcode=x.fcollateralcode and b.fsecuritycode=x.fsecuritycode"
        		+") c on a.fcollateralcode =c.fcollateralcode and a.ftransferdate = c.ftransferdate left join (select distinct d.fsecuritycode,"
        		+" d.fcatcode,d.ftradecury  from "+pub.yssGetTableName("tb_para_security")+" d) e on c.fsecuritycode =e.fsecuritycode"
        		//取券商
        		+" left join (select a.fcollateralcode, a.fbrokercode  from "+pub.yssGetTableName("Tb_Para_Collateral")
        		+" a where a.fcheckstate = 1) t on a.fcollateralcode = t.fcollateralcode"
        		+" where a.fcheckstate = 1 and a.finout = 1 and a.ftransfertype = '证券' and a.fportcode="+dbl.sqlString(this.portCode)+" and c.famount <>0 and a.fcollateralcode in"
        		+" (select a.fcollateralcode from "+pub.yssGetTableName("Tb_Para_Collateral")
        		+" a where a.fcheckstate = 1 "+(this.sBorker.equalsIgnoreCase("total")? " ":sqlBorker )
        		+(this.sMortgage.equalsIgnoreCase("total")? " ":sqlMortgage)
        		
        		//union 抵押物组合类型中的证券
        		+")  union all select m.fsecuritycode,t.fbrokercode,1 as fcheckstate,m.FCuryCode, m.fportcode, m.fattrclscode, m.fattrclscode as FCatType,"
        		+" (m.fstorageamount*fmortinout) as fstorageamount, m.fstoragedate,0 as FVStorageCost,0 as FVBaseCuryCost, 0 as FVPortCuryCost  from "+pub.yssGetTableName("tb_stock_security")
        		+" m  join (select n.fbrokercode, a.fportcode,sum(a.finout) as fmortinout  from "+pub.yssGetTableName("tb_Data_CollateralAdd")
        		+" a join (select a.fcollateralcode, a.fbrokercode from "+pub.yssGetTableName("Tb_Para_Collateral")
        		+" a where a.fcheckstate = 1 "+(this.sBorker.equalsIgnoreCase("Total")? " ":sqlBorker)+") n on a.fcollateralcode =  n.fcollateralcode where a.fcheckstate = 1"
        		+" and a.ftransfertype = '组合'"+(this.sMortgage.equalsIgnoreCase("Total")? "":sqlMortgage)
        		+" and a.ftransferdate<="+dbl.sqlDate(this.dDate)+" group by  n.fbrokercode, a.fportcode) t on t.fportcode = m.fportcode where m.fstoragedate = " +dbl.sqlDate(this.dDate) 
        		
        		+") a11 join (select * from "+pub.yssGetTableName("Tb_Para_Security")
        		+" a121 where FCheckState = 1   and FStartDate =(select max(FStartDate) as FStartDate from "+pub.yssGetTableName("Tb_Para_Security")
        		+" a122 where FStartDate <="+dbl.sqlDate(this.dDate)+" and FCheckState = 1 and a121.FSecurityCode = a122.FSecurityCode)) a12 "
        		+" on a11.FSecurityCode =a12.FSecurityCode LEFT JOIN (SELECT a.FValDate,a.FPortCode, a.FSecurityCode,a.FPrice  FROM "
        		+pub.yssGetTableName("TB_Data_ValMktPrice")+" a JOIN (SELECT MAX(FValDate) AS FValDate,FSecurityCode FROM "
        		+pub.yssGetTableName("TB_Data_ValMktPrice")+" WHERE FValDate <="+dbl.sqlDate(this.dDate)+" and FPortCode = "+dbl.sqlString(this.portCode)
        		+" GROUP BY FSecurityCode) b ON a.FValDate =  b.FValDate AND a.FSecurityCode = b.FSecurityCode"
        		+" WHERE FPortCode = "+dbl.sqlString(this.portCode)+") a14 ON a14.FSecurityCode = a11.FSecurityCode"
        		//删除了行情价格中的所属分类 AND a14.FAttrClsCode =a11.FAttrClsCode
        		+"  where a11.FPortCode ="+dbl.sqlString(this.portCode)+"  and FStorageDate = "+dbl.sqlDate(this.dDate)+" and a11.FCheckState = 1"
        		+"  group by a11.FPortCode,a11.FSecurityCode, a12.FMarketCode,a14.FPrice,a12.FISINCode,a11.fbrokercode,a12.FSubCatCode,a11.FCatType, a11.FAttrClsCode,"
        		//a1结束， a2去汇兑损益
        		+" a11.FCuryCode,a11.FStorageDate) a1 left join (select a21.FSecurityCode as FSecurityCode2,  a21.FAttrClsCode AS FAttrClsCode2, FPortCode as FPortCode2,"
        		+"sum(FVBal) as FSYVBal,sum(FVBaseCuryBal) as FSYVBaseCuryBal,sum(FVPortCuryBal) as FSYVPortCuryBal from "+pub.yssGetTableName("Tb_Stock_SecRecPay")
        		+" a21 join (select * from "+pub.yssGetTableName("Tb_Para_Security")
        		+" where FCheckState = 1) a22 on a21.FSecurityCode =a22.FSecurityCode  where 1 = 1  and ((FSubTsfTypeCode like '9905%' and (FCatCode <> 'FU' and FCatCode <> 'FW')) or"
                +" FSubTsfTypeCode like '9909%') and FPortCode = "+dbl.sqlString(this.portCode)+" and FStorageDate ="+dbl.sqlDate(this.dDate)+"  and a21.FCheckState = 1 "
                +" group by FPortCode, a21.FSecurityCode, a21.FAttrClsCode) a2 on a1.FPortCode = a2.FPortCode2   and a1.FSecurityCode =a2.FSecurityCode2"
                //a2结束 ，a3估增
                +" AND a1.Fattrclscode =a2.FAttrClsCode2 left join (select a31.FSecurityCode as FSecurityCode3, a31.FAttrClsCode AS FAttrClsCode3,"
                +"FPortCode as FPortCode3, sum(FVBal) as FYKVBal,sum(FVBaseCuryBal) as FYKVBaseCuryBal,sum(FVPortCuryBal) as FYKVPortCuryBal"
                +" from "+pub.yssGetTableName("Tb_Stock_SecRecPay")
                +" a31 join (select * from "+pub.yssGetTableName("Tb_Para_Security")
                +" where FCheckState = 1) a32 on a31.FSecurityCode = a32.FSecurityCode where FTsfTypeCode = '09' and FPortCode = "+dbl.sqlString(this.portCode)
                +" and FStorageDate = "+dbl.sqlDate(this.dDate)+" and a31.FCheckState = 1 group by FPortCode, a31.FSecurityCode, a31.Fattrclscode) a3 "
                +" on a1.FPortCode = a3.FPortCode3 and a1.FSecurityCode =a3.FSecurityCode3 AND a1.FAttrClsCode =a3.FAttrClsCode3"
                +" left join (select FCuryCode, FBaseRate, FPortRate from "+pub.yssGetTableName("Tb_Data_ValRate")
                +" where FPortCode ="+dbl.sqlString(this.portCode)+" and FValDate = "+dbl.sqlDate(this.dDate)+") a4 on a1.FCuryCode = a4.FCurycode) Security"
                
                +" left join (select FSecurityCode,FSecurityName,FCatCode,FSubCatCode,b1.FTradeCury, b2.FCuryName from (select x. * from (select * from "
                +pub.yssGetTableName("Tb_Para_Security")+" where FCheckState = 1) x join (select FSecurityCode,max(FStartDate) as FStartDate  from "
                +pub.yssGetTableName("Tb_Para_Security")+" where FCheckState = 1 and FStartDate <= "+dbl.sqlDate(this.dDate)
                +" group by FSecurityCode) x2 on x.FSecurityCode = x2.FSecurityCode  and x.FStartDate =x2.FStartDate) b1 left join (select FCuryCode, FCuryName from " 
                +pub.yssGetTableName("Tb_Para_Currency")+" where FCheckState = 1) b2 on b1.FTradeCury = b2.FCuryCode) para on Security.FSecurityCode =para.FSecurityCode"
                +" left join (select FOTPRICE1,FOTPRICE2,FOTPRICE3,FSecurityCode,FAttrClsCode from "+pub.yssGetTableName("Tb_Data_ValMktPrice")
                +" where FValDate = "+dbl.sqlDate(this.dDate)+" and FPortCode ="+dbl.sqlString(this.portCode)+") c on Security.FSecurityCode = c.FSecurityCode"
                +" AND Security.FAttrClsCode =c.FAttrClsCode left join (select distinct FSecurityCode, FTradeTypeCode, FAttrClsCode  from "
                +pub.yssGetTableName("Tb_Data_SubTrade")+" where "+dbl.sqlDate(this.dDate)+" between FBargainDate and FMATUREDATE"
                +" and FPortCode ="+dbl.sqlString(this.portCode)+" and FTradeTypeCode in ('24', '25') and FCheckState = 1  "
                //取交易类型
                +" union select distinct FSecurityCode,FTradeTypeCode,case  when FTradeTypeCode = '24' then 'SellRepo' when FTradeTypeCode = '25' then"
                +"'AntiRepo'  else  ' ' end as FAttrClsCode from "+pub.yssGetTableName("Tb_Data_Purchase")
                +" where "+dbl.sqlDate(this.dDate)+" between FBARGAINDATE and FMATUREDATE and FPortCode ="+dbl.sqlString(this.portCode)
                +" and FTradeTypeCode in ('24', '25') and FCheckState = 1) d on Security.FSecurityCode = d.FSecurityCode and Security.FAttrClsCode =  d.FAttrClsCode where Security.FStorageAmount>0";
        	//------------------------------------------------------------------------
			///**Start---panjunfang 2014-1-9*/
			// 表名拼接用户名代码，若用户名代码较长的情况下，会导致创建临时表报表名过长的错误
			//因此改为取用户id        	
            tempViewName = "V_Temp_Security_" + pub.getUserID();
			/**End---panjunfang 2014-1-9  */
            //add by jiangshichao 净值统计表的临时表类型是table,而证券借贷的临时表类型是View 所以这里都需要进行判断。
            if (dbl.yssTableExist(tempViewName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " + tempViewName));
                /**end*/
            }
            if (dbl.yssViewExist(tempViewName)) {
                dbl.executeSql("drop view " + tempViewName);
            }
            strFields = "FSecurityCode,FSubTsfTypeCode,FportCode,FMarketPrice,FISINCode,FEXternalCode,FBaseCuryRate,FPortCuryRate," +
                "FStorageAmount,FCost,FBaseCost,FPortCost,FNavDate,FAttrClsCode,FMVBal,FBaseMVBal,FPortMVBal,FFXBal,FBaseFXBal," +
                //"FPortFXBal,FSecurityName,FCUryName,FCatCode,FCuryCode,FSubCatCode,FotPrice1,FotPrice2,FotPrice3,Fpurchasetype";
                "FPortFXBal,FSecurityName,FCUryName,FCatCode,FCuryCode,FSubCatCode,FotPrice1,FotPrice2,FotPrice3,FTradeTypeCode";//调整为从业务资料中取回购类型  QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
            if (dbl.getDBType() == YssCons.DB_ORA) {
                String tempStr = "create view " + tempViewName + "(" + strFields + ") as (" +
                    strSql +
                    ")";
                dbl.executeSql(tempStr);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                String sql = "create view " + tempViewName + "( " + strFields + " ) as " + strSql;
                dbl.executeSql(sql);
                //dbl.executeSql("insert into " + tempViewName + "(" + strSql +
                // ")");
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
            return tempViewName;

        } catch (Exception e) {
            throw new YssException("生成证券视图出错！");
        }
    }

   
    public void buildLeftSql(ArrayList leftArr, ArrayList fieldsArr) {
        String leftSql = "";
        String fields = "";
        //----------------------------------------
        leftSql = " left join (select FCatCode as PFCatCode,FCatName from Tb_Base_Category where FCheckState = 1)" +
            " para on dat.FCatCode = para.PFCatCode";
        fields = "FCatCode,FCatName";
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
