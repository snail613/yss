package com.yss.main.operdata.overthecounter;

import com.yss.main.dao.IDataSetting;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import com.yss.main.operdata.TradeBean;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdata.overthecounter.pojo.OpenFundTradeBean;
import com.yss.util.YssFun;
import java.sql.*;
import java.util.*;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.vsub.YssOperFun;

public class OpenFundTradeAdmin
    extends BaseDataSettingBean implements IDataSetting {
    OpenFundTradeBean openFund = null;
    List openFunds = null;
    private String sRecycled;
    //story 1574 add by zhouwei 20111108
    private List list=new ArrayList();
    public void addList(List list){
    	this.list=list;
    }
    public OpenFundTradeAdmin() {
        openFund = new OpenFundTradeBean();
        openFunds = new ArrayList();
    }

    public void checkInput(byte btOper) throws YssException {
        //判断
        if (btOper == YssCons.OP_ADD && openFund.getNum() != null && openFund.getNum().length() > 0) {
            ResultSet rs = null;
            String strSql = "";
            try {
                strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Data_OpenFundTrade");
                if (openFund.getNum() != null && openFund.getNum().length() > 0) {
                    strSql += " where FNum = " + dbl.sqlString(openFund.getNum()) + " and FDataType = " + dbl.sqlString(openFund.getDataType());
                }
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    throw new YssException("增加数据已存在，请删除后重新增加！");
                }
            } catch (Exception e) {
                throw new YssException("验证设置信息出错！", e);
            } finally {
                dbl.closeResultSetFinal(rs);
            }
        }
    }

    /**
     * 增加数据
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String strSql = "";
        String num = "";
        String strNumDate = "";
        String[] arrData;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strNumDate = YssFun.formatDate(openFund.getBargainDate(), "yyyyMMdd");//xuqiji 20100629 MS01347 QDV4国内(测试)2010年06月24日01_B 
            if (openFund.getNum().equals("")) {
                num = "OTC" + strNumDate + dbFun.getNextInnerCode(
                    pub.yssGetTableName("Tb_Data_OpenFundTrade"), dbl.sqlRight("FNUM", 6),
                    "000000000", " where FNum like 'OTC" + strNumDate + "%'", 1);
            } else {
                num = openFund.getNum();
            }
            if (sRecycled != null && !("").equalsIgnoreCase(sRecycled)) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "insert into " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + "(FNum,FDataType,FTradeTypeCode,FPortCode," +
                        "FSecurityCode,FBARGAINDATE,FInvestType,FInvMgrCode,";
                    if (openFund.getApplyDate() != null) {
                        strSql += "FApplyDate,FApplyCashAccCode,FApplyMoney,FDesc,FCheckState,FCreator,FCreateTime)values(";
                        strSql += dbl.sqlString(num) + "," +
                            dbl.sqlString("apply") + "," +
                            dbl.sqlString(openFund.getTradeTypeCode()) + "," +
                            dbl.sqlString(openFund.getPortCode()) + "," +
                            dbl.sqlString(openFund.getSecurityCode()) + "," +
                            dbl.sqlDate(openFund.getBargainDate()) + "," +
                            dbl.sqlString(openFund.getInvestType()) + "," +
                            dbl.sqlString(openFund.getInvMgrCode()) + ",";
                        strSql += dbl.sqlDate(openFund.getApplyDate()) + "," +
                            dbl.sqlString(openFund.getApplyCashAccCode()) + "," +
                            openFund.getApplyMoney() + ",";
                    }
                    if (openFund.getComfDate() != null) {//edit by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A 
                        strSql += "FComfDate,FApplyCashAccCode,FComfAmount,FSwitchAmount,FComfMoney,FComfFee,FTradePrice,fcomdifmoney,FTsfFunds,FDesc,FCheckState,FCreator,FCreateTime)values(";
                        strSql += dbl.sqlString(num) + "," +
                            dbl.sqlString("confirm") + "," +
                            dbl.sqlString(openFund.getTradeTypeCode()) + "," +
                            dbl.sqlString(openFund.getPortCode()) + "," +
                            dbl.sqlString(openFund.getSecurityCode()) + "," +
                            dbl.sqlDate(openFund.getBargainDate()) + "," +
                            dbl.sqlString(openFund.getInvestType()) + "," +
                            dbl.sqlString(openFund.getInvMgrCode()) + ",";
                        strSql += dbl.sqlDate(openFund.getComfDate()) + "," +
                        dbl.sqlString(openFund.getComCashAccCode()) + "," +//add by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A 
                            openFund.getComfAmount() + "," +
                            openFund.getSwitchAmount() + "," +
                            openFund.getComfMoney() + "," +
                            openFund.getComfFee() + "," +
                            openFund.getTradePrice() + "," +
                            openFund.getComDifMoney()+","+//story 1574 update by zhouwei 20111108
                            dbl.sqlString(openFund.getTsfFunds()) + ",";
                    }
                    if (openFund.getReturnDate() != null) {
                        strSql += "FReturnDate,FReturnFee,FReturnMoney,freturndifmoney,FRtnCashAccCode,FDesc,FCheckState,FCreator,FCreateTime)values(";
                        strSql += dbl.sqlString(num) + "," +
                            dbl.sqlString("return") + "," +
                            dbl.sqlString(openFund.getTradeTypeCode()) + "," +
                            dbl.sqlString(openFund.getPortCode()) + "," +
                            dbl.sqlString(openFund.getSecurityCode()) + "," +
                            dbl.sqlDate(openFund.getBargainDate()) + "," +
                            dbl.sqlString(openFund.getInvestType()) + "," +
                            dbl.sqlString(openFund.getInvMgrCode()) + ",";
                        strSql += dbl.sqlDate(openFund.getReturnDate()) + "," +
                            openFund.getReturnFee() + "," +
                            openFund.getReturnMoney() + "," +
                            openFund.getReturnDifMoney()+","+//story 1574 update by zhouwei 20111108
                            dbl.sqlString(openFund.getRtnCashAccCode()) + ",";
                    }
                                     
                    strSql += dbl.sqlString(openFund.getDesc()) + ",0," + dbl.sqlString(this.creatorCode) + "," +
                        dbl.sqlString(this.creatorTime) + ")";
                    dbl.executeSql(strSql);
                    //修改备注
                    strSql = "update " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " set FDesc = " + dbl.sqlString(openFund.getDesc()) +
                        " where FNum = " + dbl.sqlString(num);
                    dbl.executeSql(strSql);
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增开放式基金业务出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 修改
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        String num = "";
        String strNumDate = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        //---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
        ResultSet rs = null;
        String[] arrData;
        boolean haveConfirm = false;
        boolean haveReturn = false;
        //---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
        try {
		    //---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
        	//实现同时修改多条数据的功能
            if (sRecycled != null &&! ("").equalsIgnoreCase(sRecycled)) {
                arrData = sRecycled.split("\r\n");
                
            	conn.setAutoCommit(false);
            	bTrans = true;
                
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    
                    if(i == 0){
                    	//modify by nimengjing bug#526 修改开放式基金业务的申请日期时，该界面下自动产生的交易编号没有被修改
                    	strNumDate = YssFun.formatDate(openFund.getBargainDate(), "yyyyMMdd");
                    	if (!openFund.getNum().equals("")
                    			//edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A
                    			//如果原有交易编号中的申请日期 不等于 已修改的最新的申请日期，则根据最新的申请日期重新生成交易编号
                    			&& openFund.getNum().indexOf("OTC" + strNumDate) == -1) {
                    		num = "OTC" + strNumDate + dbFun.getNextInnerCode(
                    				pub.yssGetTableName("Tb_Data_OpenFundTrade"), dbl.sqlRight("FNUM", 6),
                    				"000000000", " where FNum like 'OTC" + strNumDate + "%'", 1);   
                    		
                            //---add by songjie 2011.09.01 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
                            //若反审核开放式基金业务数据，则之前由该数据生成的综合业务数据、证券应收应付数据、现金应收应付数据、资金调拨数据全部删除
                    		strSql = " delete from " + pub.yssGetTableName("Tb_Data_Integrated") + 
                    		         " where FRelaNum = " + dbl.sqlString(openFund.getNum()) + 
                    		         " and FNumType = 'openfund' ";
                    		dbl.executeSql(strSql);
                    		
                    		strSql = " delete from " + pub.yssGetTableName("Tb_Data_Secrecpay") + 
                    				 " where FRelaNum = " + dbl.sqlString(openFund.getNum()) + 
                    				 " and FRelaType = 'openfund' ";
                    		dbl.executeSql(strSql);
                    		
                    		strSql = " delete from " + pub.yssGetTableName("Tb_Data_Cashpayrec") + 
                    			 	 " where FRelaNum = " + dbl.sqlString(openFund.getNum()) + 
                    				 " and FRelaType = 'openfund' ";
                    		dbl.executeSql(strSql);

                    		strSql = " delete from " + pub.yssGetTableName("Tb_Cash_Subtransfer") +
                    	 	         " where FNum in( select FNum from " + pub.yssGetTableName("tb_cash_transfer") + 
                    	 	         " where FRelaNum = " + dbl.sqlString(openFund.getNum()) + " and FNumType = 'openfund' ) ";
                    		dbl.executeSql(strSql);
                    		
                    		strSql = " delete from " + pub.yssGetTableName("tb_cash_transfer") +
                    				 " where FRelaNum = " + dbl.sqlString(openFund.getNum()) + 
                    				 " and FNumType = 'openfund' ";
                    		dbl.executeSql(strSql);
                    		//---add by songjie 2011.09.01 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
                    	}
                    	//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
                    	else{
                    		num = openFund.getNum();//若不用重新生成交易编号，则取原有的交易编号
                    	} 
                    	//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
                    	
                    	//修改公共部分（一次性修改多条）
                    	strSql = "update " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " set " +
                    		"FNum= "+dbl.sqlString(num) +","+
                    		//-------------------------end bug#526------------------------------------
                    		"FTradeTypeCode = " + dbl.sqlString(openFund.getTradeTypeCode()) + ", " +
                    		"FPortCode = " + dbl.sqlString(openFund.getPortCode()) + ", " +
                    		"FSecurityCode = " + dbl.sqlString(openFund.getSecurityCode()) + ", " +
                    		"FBARGAINDATE = " + dbl.sqlDate(openFund.getBargainDate()) + ", " +
                    		"FInvestType = " + dbl.sqlString(openFund.getInvestType()) + ", " +
                    		"FInvMgrCode = " + dbl.sqlString(openFund.getInvMgrCode()) + ", " +
                    		"FDesc = " + dbl.sqlString(openFund.getDesc()) + " where FNum = " +
                    		dbl.sqlString(openFund.getNum());

                    	dbl.executeSql(strSql);
                    }
                    
                    //修改单独部分
                    if (openFund.getApplyDate() != null) {
                        strSql = "update " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " set FApplyDate = " +
                            dbl.sqlDate(openFund.getApplyDate()) + ", FApplyCashAccCode = " +
                            dbl.sqlString(openFund.getApplyCashAccCode()) + ", FApplyMoney = " +
                            openFund.getApplyMoney() + " where FNum = " +
                            //edit by songjie 2011.06.09 BUG 2051 QDV4赢时胜(测试)2011年6月8日01_B 将openFund.getNum() 改为 num
                            dbl.sqlString(num) + " and FDataType  = 'apply'";
                        dbl.executeSql(strSql);
                    }
                    if (openFund.getComfDate() != null) {//edit by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A
                        strSql = " select * from " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + 
                        " where FNum = " + dbl.sqlString(num) + " and FDataType = 'confirm' ";
                        
                        rs = dbl.openResultSet(strSql);
                        if(rs.next()){
                        	haveConfirm = true;
                        }
                        
                        dbl.closeResultSetFinal(rs);
                        
                        if(!haveConfirm){
                            strSql = "insert into " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + 
                            "(FNum,FDataType,FTradeTypeCode,FPortCode," +
                            "FSecurityCode,FBARGAINDATE,FInvestType,FInvMgrCode,"+
                            "FComfDate,FApplyCashAccCode,FComfAmount,FSwitchAmount," +
                            "FComfMoney,FComfFee,FTradePrice,FTsfFunds," + 
                            "FDesc,FCheckState,FCreator,FCreateTime,fcomdifmoney)values(";
                            strSql += dbl.sqlString(num) + "," +
                                dbl.sqlString("confirm") + "," +
                                dbl.sqlString(openFund.getTradeTypeCode()) + "," +
                                dbl.sqlString(openFund.getPortCode()) + "," +
                                dbl.sqlString(openFund.getSecurityCode()) + "," +
                                dbl.sqlDate(openFund.getBargainDate()) + "," +
                                dbl.sqlString(openFund.getInvestType()) + "," +
                                dbl.sqlString(openFund.getInvMgrCode()) + "," +
                                dbl.sqlDate(openFund.getComfDate()) + "," +
                                dbl.sqlString(openFund.getComCashAccCode()) + "," +
                                openFund.getComfAmount() + "," +
                                openFund.getSwitchAmount() + "," +
                                openFund.getComfMoney() + "," +
                                openFund.getComfFee() + "," +
                                openFund.getTradePrice() + "," +
                                dbl.sqlString(openFund.getTsfFunds()) + "," +
                                dbl.sqlString(openFund.getDesc()) + ",0," + dbl.sqlString(this.creatorName) + "," +
                            dbl.sqlString(this.creatorTime) + ","+openFund.getComDifMoney()+")";//story 1574 update by zhouwei 20111108
                            
                            dbl.executeSql(strSql);
                            
                            continue;
                        }
                    	
                    	strSql = "update " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " set FComfDate = " +
                            dbl.sqlDate(openFund.getComfDate()) + ", FApplyCashAccCode = " +
                            dbl.sqlString(openFund.getComCashAccCode()) + ", FComfAmount = " +//add by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A 
                            openFund.getComfAmount() + ",FSwitchAmount = " +
                            openFund.getSwitchAmount() + ", FComfMoney = " +
                            openFund.getComfMoney() + ", FComfFee = " +
                            openFund.getComfFee() + ", FTradePrice = " +
                            openFund.getTradePrice() + ", FTsfFunds = " +
                            dbl.sqlString(openFund.getTsfFunds())+",fcomdifmoney="+openFund.getComDifMoney() + " where FNum = " +//story 1574 update by zhouwei 20111108
                            //edit by songjie 2011.06.09 BUG 2051 QDV4赢时胜(测试)2011年6月8日01_B 将openFund.getNum() 改为 num
                            dbl.sqlString(num) + " and FDataType  = 'confirm'";
                    	
                        dbl.executeSql(strSql);
                    }
                    if (openFund.getReturnDate() != null) {
                        strSql = " select * from " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + 
                        " where FNum = " + dbl.sqlString(num) + " and FDataType = 'return' ";
                        
                        rs = dbl.openResultSet(strSql);
                        if(rs.next()){
                        	haveReturn = true;
                        }
                    	
                        dbl.closeResultSetFinal(rs);
                        
                        if(!haveReturn){
                            strSql = "insert into " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + 
                            "(FNum,FDataType,FTradeTypeCode,FPortCode," +
                            "FSecurityCode,FBARGAINDATE,FInvestType,FInvMgrCode,"+
                            "FReturnDate,FReturnFee,FReturnMoney,FRtnCashAccCode,"+
                            "FDesc,FCheckState,FCreator,FCreateTime,freturndifmoney)values(";//story 1574 update by zhouwei 20111108
                            strSql += dbl.sqlString(num) + "," + dbl.sqlString("return") + "," +
                                dbl.sqlString(openFund.getTradeTypeCode()) + "," +
                                dbl.sqlString(openFund.getPortCode()) + "," +
                                dbl.sqlString(openFund.getSecurityCode()) + "," +
                                dbl.sqlDate(openFund.getBargainDate()) + "," +
                                dbl.sqlString(openFund.getInvestType()) + "," +
                                dbl.sqlString(openFund.getInvMgrCode()) + "," +
                                dbl.sqlDate(openFund.getReturnDate()) + "," +
                                openFund.getReturnFee() + "," + openFund.getReturnMoney() + "," +
                                dbl.sqlString(openFund.getRtnCashAccCode()) + "," + 
                                dbl.sqlString(openFund.getDesc()) + ",0," + 
                                dbl.sqlString(this.creatorName) + "," +
                            dbl.sqlString(this.creatorTime) +","+openFund.getReturnDifMoney()+ ")";//story 1574 update by zhouwei 20111108
                            
                            dbl.executeSql(strSql);
                            
                            continue;
                        }
                        strSql = "update " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " set FReturnDate = " +
                            dbl.sqlDate(openFund.getReturnDate()) + ", FReturnFee = " +
                            openFund.getReturnFee() + ", FReturnMoney = " +
                            openFund.getReturnMoney() + ", FRtnCashAccCode = " +
                            dbl.sqlString(openFund.getRtnCashAccCode())+",freturndifmoney="+openFund.getReturnDifMoney() + " where FNum = " +//story 1574 update by zhouwei 20111108
                            //edit by songjie 2011.06.09 BUG 2051 QDV4赢时胜(测试)2011年6月8日01_B 将openFund.getNum() 改为 num
                            dbl.sqlString(num) + " and FDataType  = 'return'";
                        
                        dbl.executeSql(strSql);
                    }
                    
                    haveConfirm = false;
                    haveReturn = false;
                }
				//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
            }
        
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改开放式基金业务出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }

        return null;
    }

    /**
     * 删除
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
				//edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A
                //允许同时删除多条数据
                "' where FNum = " + dbl.sqlString(openFund.getNum()) + " and FCheckState = 0 and FDataType in (" + operSql.sqlCodes(openFund.getDataType()) + ")"; //仅限未审核部分

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除开放式基金业务出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 审核/反审核数据
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String strSub = "";
        String[] arrData;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null&&! ("").equalsIgnoreCase(sRecycled)) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                        "' where FNum = " + dbl.sqlString(openFund.getNum());
                    if (openFund.getDataType().split(",").length == 1) {
                        strSql += " and FCheckState = 2";
                    } else if (1 == this.checkStateId && openFund.getDataType().split(",")[1].equals("FUND")) { //如果是审核，则当前交易编号所有未审核的数据
//                        strSql += " and FDataType = " + dbl.sqlString(openFund.getDataType().split(",")[0]);
                        strSql += " and FCheckState = 0";
                    } else if (0 == this.checkStateId && openFund.getDataType().split(",")[1].equals("FUND")) { //如果是反审核，则当前交易编号所有已审核的数据
//                        strSql += " and FDataType = " + dbl.sqlString(openFund.getDataType().split(",")[0]);
                        strSql += " and FCheckState = 1";
                        
                        //---add by songjie 2011.09.01 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
                        //若反审核开放式基金业务数据，则之前由该数据生成的综合业务数据、证券应收应付数据、现金应收应付数据、资金调拨数据全部删除
                        strSub = " delete from " + pub.yssGetTableName("Tb_Data_Integrated") + 
		                     	 " where FRelaNum = " + dbl.sqlString(openFund.getNum()) + 
		                     	 " and FNumType = 'openfund' ";
                        dbl.executeSql(strSub);
		
                        strSub = " delete from " + pub.yssGetTableName("Tb_Data_Secrecpay") + 
                        		 " where FRelaNum = " + dbl.sqlString(openFund.getNum()) + 
                        		 " and FRelaType = 'openfund' ";
                        dbl.executeSql(strSub);
		
                        strSub = " delete from " + pub.yssGetTableName("Tb_Data_Cashpayrec") + 
                        		 " where FRelaNum = " + dbl.sqlString(openFund.getNum()) + 
                        		 " and FRelaType = 'openfund' ";
                        dbl.executeSql(strSub);
		
                        strSub = " delete from " + pub.yssGetTableName("Tb_Cash_Subtransfer") +
                        		 " where FNum in( select FNum from " + pub.yssGetTableName("tb_cash_transfer") + 
                        		 " where FRelaNum = " + dbl.sqlString(openFund.getNum()) + " and FNumType = 'openfund' ) ";
                        dbl.executeSql(strSub);
                        
                        strSub = " delete from " + pub.yssGetTableName("tb_cash_transfer") +
                        		 " where FRelaNum = " + dbl.sqlString(openFund.getNum()) + 
                        	     " and FNumType = 'openfund' ";
                        dbl.executeSql(strSub);
                        //---add by songjie 2011.09.01 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
                      //story 1574 add by zhouwei 20111108 删除06类型生成的交易数据
                        strSub = "select a.FNum as FZNum,b.FNum as FSNum from "
            				+ pub.yssGetTableName("Tb_Data_SubTrade")
            				+ " a left join (select * from "
            				+ pub.yssGetTableName("Tb_Data_Trade")
            				+ ") b on a.FSecurityCode = b.FSecurityCode  and a.FBrokerCode = b.FBrokerCode"
            				+ " and a.FInvMgrCode = b.FInvMgrCode and a.FBargainDate = b.FBargainDate and a.ftradetypecode =b.ftradetypecode"	
            				+" where a.ftradetypecode='39' and a.fdealnum="+dbl.sqlString(openFund.getNum());
            			ResultSet rs = dbl.openResultSet(strSub);
            			String FZNum="";
            			String FSNum="";
            			while (rs.next()) {
            				FZNum += rs.getString("FZNum") + ",";
            				FSNum += rs.getString("FSNum") + ",";
            			}
            			dbl.closeResultSetFinal(rs);
            			FZNum = operSql.sqlCodes(FZNum);
            			FSNum = operSql.sqlCodes(FSNum);
            			strSub = "delete from "
            				+ pub.yssGetTableName("Tb_Data_SubTrade")+" where fnum in ("+FZNum+")";
            			dbl.executeSql(strSub);
            			strSub = "delete from " + pub.yssGetTableName("Tb_Data_Trade")+" where fnum in ("+FSNum+")";
            			dbl.executeSql(strSub);
                    }
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核/反审核开放式基金业务出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    /**
     * 查询一条数据
     * @return IDataSetting
     * @throws YssException
     */
    public IDataSetting getSetting() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String num = "";
        String dataType = "";
        try {
            //通过开放式基金交易表左连接现金账户表通过申请账户代码和返款账户代码进行匹配，连接证券信息查询出基金信息
            strSql = "SELECT ot.*,ca.fcashaccname as FApplyCashAccName,cat.fcashaccname as FRtnCashAccName,vs.FSECURITYNAME FROM " + pub.yssGetTableName("Tb_Data_OpenFundTrade") +
                " ot left join " + pub.yssGetTableName("Tb_Para_CashAccount") + " ca on ot.fapplycashacccode = ca.fcashacccode " +
                " left join " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " cat on ot.frtncashacccode = cat.fcashacccode left join " + pub.yssGetTableName("tb_Para_Security") +
                " vs on ot.ftsffunds = vs.FSECURITYCODE WHERE ot.FTradeTypeCode = " +
                dbl.sqlString(openFund.getTradeTypeCode()) + " and ot.FPortCode = " +
                dbl.sqlString(openFund.getPortCode()) + " and ot.FSecurityCode = " +
                dbl.sqlString(openFund.getSecurityCode()) + " and ot.FBARGAINDATE = " +
                dbl.sqlDate(openFund.getBargainDate()) + " and ot.FInvestType = " +
                dbl.sqlString(openFund.getInvestType()) + " and ot.FInvMgrCode = " +
                dbl.sqlString(openFund.getInvMgrCode()) + " and ot.FCheckState <> 2 and ot.FNum = " + //不查询回收站内信息
                dbl.sqlString(openFund.getNum()) + "order by ot.FDataType";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                openFund = new OpenFundTradeBean();
                num = rs.getString("FNum"); //交易编号
                dataType = rs.getString("FDataType"); //数据类型
                openFund.setNum(num); //交易编号
                openFund.setDataType(dataType); //数据类型
                openFund.setTradeTypeCode(rs.getString("FTradeTypeCode")); //交易类型代码
                openFund.setPortCode(rs.getString("FPortCode")); //组合代码
                openFund.setSecurityCode(rs.getString("FSecurityCode")); //基金代码
                openFund.setBargainDate(rs.getDate("FBARGAINDATE")); //申请日期
                openFund.setInvestType(rs.getString("FInvestType")); //投资类型
                openFund.setInvMgrCode(rs.getString("FInvMgrCode")); //投资经理
                this.checkStateId = rs.getInt("FCheckState"); //审核状态
                openFund.setDesc(rs.getString("FDesc")); //描述
                //以上为公共数据，以下为单独数据
                if ("apply".equals(dataType)) { //申请部分的数据
                    openFund.setApplyDate(rs.getDate("FApplyDate")); //申请业务日子
                    openFund.setApplyCashAccCode(rs.getString("FApplyCashAccCode")); //申请帐户
                    openFund.setApplyCashAccName(rs.getString("FApplyCashAccName")); //申请帐户
                    openFund.setApplyMoney(rs.getDouble("FApplyMoney")); //申请金额
                } else if ("confirm".equals(dataType)) { //确认部分数据
                    openFund.setComfDate(rs.getDate("FComfDate")); //确认业务日期
                    openFund.setComCashAccCode(rs.getString("FApplyCashAccCode"));//add by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A  赎回账户
                    openFund.setComfAmount(rs.getDouble("FComfAmount")); //确认数量
                    openFund.setSwitchAmount(rs.getDouble("FSwitchAmount")); //转入数量
                    openFund.setComfMoney(rs.getDouble("FComfMoney")); //确认金额
                    openFund.setComfFee(rs.getDouble("FComfFee")); //确认交易费用
                    openFund.setTradePrice(rs.getDouble("FTradePrice")); //交易价格
                    openFund.setTsfFunds(rs.getString("FTsfFunds")); //转入基金
                    openFund.setTsfFundsName(rs.getString("FSECURITYNAME")); //转入基金
                    openFund.setComDifMoney(rs.getDouble("fcomdifmoney"));//转投调整金额 story 1574 add by zhouwei 20111108
                } else if ("return".equals(dataType)) { //返回部分数据
                    openFund.setReturnDate(rs.getDate("FReturnDate")); //退还业务日期
                    openFund.setReturnFee(rs.getDouble("FReturnFee")); //退还费用
                    openFund.setReturnMoney(rs.getDouble("FReturnMoney")); //退还金额
                    openFund.setRtnCashAccCode(rs.getString("FRtnCashAccCode")); //退还账号
                    openFund.setRtnCashAccName(rs.getString("FRtnCashAccName")); //退还账号
                    openFund.setReturnDifMoney(rs.getDouble("freturndifmoney"));//现金调整金额 story 1574 add by zhouwei 20111108
                }
                openFunds.add(openFund); //增加到集合中
            }
        } catch (Exception e) {
            throw new YssException("获取交易类型信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return this;
    }
    //story 1574 add by zhouwei 20111110 修改和删除操作只查询出未审核数据
    public IDataSetting getSetting2() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String num = "";
        String dataType = "";
        try {
            //通过开放式基金交易表左连接现金账户表通过申请账户代码和返款账户代码进行匹配，连接证券信息查询出基金信息
            strSql = "SELECT ot.*,ca.fcashaccname as FApplyCashAccName,cat.fcashaccname as FRtnCashAccName,vs.FSECURITYNAME FROM " + pub.yssGetTableName("Tb_Data_OpenFundTrade") +
                " ot left join " + pub.yssGetTableName("Tb_Para_CashAccount") + " ca on ot.fapplycashacccode = ca.fcashacccode " +
                " left join " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " cat on ot.frtncashacccode = cat.fcashacccode left join " + pub.yssGetTableName("tb_Para_Security") +
                " vs on ot.ftsffunds = vs.FSECURITYCODE WHERE ot.FTradeTypeCode = " +
                dbl.sqlString(openFund.getTradeTypeCode()) + " and ot.FPortCode = " +
                dbl.sqlString(openFund.getPortCode()) + " and ot.FSecurityCode = " +
                dbl.sqlString(openFund.getSecurityCode()) + " and ot.FBARGAINDATE = " +
                dbl.sqlDate(openFund.getBargainDate()) + " and ot.FInvestType = " +
                dbl.sqlString(openFund.getInvestType()) + " and ot.FInvMgrCode = " +
                dbl.sqlString(openFund.getInvMgrCode()) + " and ot.FCheckState=0 and ot.FNum = " + //不查询回收站内信息
                dbl.sqlString(openFund.getNum()) + "order by ot.FDataType";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                openFund = new OpenFundTradeBean();
                num = rs.getString("FNum"); //交易编号
                dataType = rs.getString("FDataType"); //数据类型
                openFund.setNum(num); //交易编号
                openFund.setDataType(dataType); //数据类型
                openFund.setTradeTypeCode(rs.getString("FTradeTypeCode")); //交易类型代码
                openFund.setPortCode(rs.getString("FPortCode")); //组合代码
                openFund.setSecurityCode(rs.getString("FSecurityCode")); //基金代码
                openFund.setBargainDate(rs.getDate("FBARGAINDATE")); //申请日期
                openFund.setInvestType(rs.getString("FInvestType")); //投资类型
                openFund.setInvMgrCode(rs.getString("FInvMgrCode")); //投资经理
                this.checkStateId = rs.getInt("FCheckState"); //审核状态
                openFund.setDesc(rs.getString("FDesc")); //描述
                //以上为公共数据，以下为单独数据
                if ("apply".equals(dataType)) { //申请部分的数据
                    openFund.setApplyDate(rs.getDate("FApplyDate")); //申请业务日子
                    openFund.setApplyCashAccCode(rs.getString("FApplyCashAccCode")); //申请帐户
                    openFund.setApplyCashAccName(rs.getString("FApplyCashAccName")); //申请帐户
                    openFund.setApplyMoney(rs.getDouble("FApplyMoney")); //申请金额
                } else if ("confirm".equals(dataType)) { //确认部分数据
                    openFund.setComfDate(rs.getDate("FComfDate")); //确认业务日期
                    openFund.setComCashAccCode(rs.getString("FApplyCashAccCode"));//add by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A  赎回账户
                    openFund.setComfAmount(rs.getDouble("FComfAmount")); //确认数量
                    openFund.setSwitchAmount(rs.getDouble("FSwitchAmount")); //转入数量
                    openFund.setComfMoney(rs.getDouble("FComfMoney")); //确认金额
                    openFund.setComfFee(rs.getDouble("FComfFee")); //确认交易费用
                    openFund.setTradePrice(rs.getDouble("FTradePrice")); //交易价格
                    openFund.setTsfFunds(rs.getString("FTsfFunds")); //转入基金
                    openFund.setTsfFundsName(rs.getString("FSECURITYNAME")); //转入基金
                    openFund.setComDifMoney(rs.getDouble("fcomdifmoney"));//转投调整金额 story 1574 add by zhouwei 20111108
                } else if ("return".equals(dataType)) { //返回部分数据
                    openFund.setReturnDate(rs.getDate("FReturnDate")); //退还业务日期
                    openFund.setReturnFee(rs.getDouble("FReturnFee")); //退还费用
                    openFund.setReturnMoney(rs.getDouble("FReturnMoney")); //退还金额
                    openFund.setRtnCashAccCode(rs.getString("FRtnCashAccCode")); //退还账号
                    openFund.setRtnCashAccName(rs.getString("FRtnCashAccName")); //退还账号
                    openFund.setReturnDifMoney(rs.getDouble("freturndifmoney"));//现金调整金额 story 1574 add by zhouwei 20111108
                }
                openFunds.add(openFund); //增加到集合中
            }
        } catch (Exception e) {
            throw new YssException("获取交易类型信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return this;
    }

    public String getAllSetting() throws YssException {
        return "";
    }
    //查询基金净值
    public String getPrice() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String price = "";
        try {
            strSql = "select FPrice from " + pub.yssGetTableName("tb_data_navdata");
            strSql += " where FNAVDATE = " + dbl.sqlDate(openFund.getBargainDate()) + " and fportcode = " + dbl.sqlString(openFund.getPortCode()) + " and FKEYCODE='Unit'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                price = rs.getDouble("FPrice") + "";
            }
        } catch (Exception e) {
            throw new YssException("验证设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return price;
    }

    //add by guolongchao STORY 1222 获取货币式基金的品种子类型
    public String getSubTsfType() throws YssException {
    	ResultSet rs = null;
    	String sql="select a.FSubCatCode from "+pub.yssGetTableName("tb_para_security")+" a  " +
    			   " where a.FCheckState = 1 and a.FCatCode = 'TR' and a.fsecuritycode='"+this.openFund.getSecurityCode()+"'";
    	try 
    	{
			rs = dbl.openResultSet(sql);
			if(rs.next())
				return rs.getString("FSubCatCode");
    	 } catch (Exception e) {
             throw new YssException("获取开放式基金的品种子类型出错", e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }    	
        return "";
    }
    
    /**
     * 删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_OpenFundTrade") +
                        " where FNum = " + dbl.sqlString(openFund.getNum()) + " and FCheckState = 2"; //仅限回收站部分
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true); //提交事物
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }

    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    /**
     * 查询
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String num = "";
        String sAllDataStr = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if ("1".equals(openFund.getIsOnlyColumns()) && !(pub.isBrown())) {	//20111027 modified by liubo. STORY #1285 
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType);
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                    "\r\f" + this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql =
                " select distinct dt1.fnum as num,dt1.ftradetypecode,dt5.FTradeTypeName,dt1.fportcode, " +
                " pf.fportname,dt1.fsecuritycode,vs.FSECURITYNAME,dt1.finvmgrcode,pi.finvmgrname,dt1.FInvestType,f.FVocName,dt2.fcheckstate || ','||  dt3.fcheckstate || ',' || dt4.fcheckstate ||  ','||  'null' as createor,dt1.fbargaindate,dt1.fdesc,dt1.fcheckstate, " +
                //edit by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A 
                " dt2.fapplydate,dt2.fapplymoney,dt2.fapplycashacccode,dt2.fapplycashaccname,dt2.dt2type,dt3.fcomfdate,dt3.fcomcashacccode,dt3.fcomcashaccname,dt3.fcomfamount,dt3.fswitchamount,dt3.fcomfmoney, " +
                " dt3.fcomffee,dt3.ftradeprice,dt3.fcomdifmoney,dt3.ftsffunds,dt3.FTsfFundName,dt3.dt3type,dt4.freturndate,dt4.freturnfee,dt4.freturnmoney,dt4.freturndifmoney,dt4.frtncashacccode,dt4.frtncashaccname,dt4.dt4type,"+//story 1574 update by zhouwei 20111108
                " case when dt2.fusercode is not null then dt2.fusercode when dt3.fusercode is not null then dt3.fusercode  when dt4.fusercode is not null then dt4.fusercode  else ' '  end as  creator" +
                " from " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " dt1 " +
                " left join (select ot.fnum,ot.fapplydate,ot.fapplymoney,ot.fapplycashacccode,case when ot.fapplydate is null then ' '  else ' √ ' end as dt2type,ot.fcheckstate,ca.fcashaccname as fapplycashaccname ,us.fusercode" +
                " from " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " ot left join  " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " ca on ot.fapplycashacccode = ca.fcashacccode left join tb_sys_userlist us on ot.fcreator=us.fusercode where ot.FDataType = 'apply') dt2 on dt1.fnum = dt2.fnum  and dt1.fcheckstate = dt2.fcheckstate " +
                //edit by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A 
                " left join (select ot.fcomdifmoney,ot.fnum,ot.FComfDate,ot.fapplycashacccode as fcomcashacccode,ca.fcashaccname as fcomcashaccname,ot.FComfAmount,ot.FSwitchAmount,ot.FComfMoney,ot.FComfFee,ot.FTradePrice,ot.FTsfFunds,vs.FSECURITYNAME as FTsfFundName,case when ot.FComfDate is null then ' '  else ' √ ' end as dt3type,ot.fcheckstate " +
                " , us.fusercode from " + pub.yssGetTableName("Tb_Data_OpenFundTrade") +
                " ot left join " + pub.yssGetTableName("tb_para_Security") +
                " vs on ot.ftsffunds = vs.FSECURITYCODE " +
                //edit by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A 
                " left join " + pub.yssGetTableName("Tb_Para_CashAccount") + " ca on ot.fapplycashacccode = ca.fcashacccode left join tb_sys_userlist us on  ot.fcreator=us.fusercode" +
                " where FDataType = 'confirm') dt3 on dt1.fnum = dt3.fnum  and dt1.fcheckstate = dt3.fcheckstate " +
                " left join (select ot.freturndifmoney,ot.fnum,ot.FReturnDate,ot.FReturnFee,ot.FReturnMoney,ot.FRtnCashAccCode,case when ot.FReturnDate is null then ' '  else ' √ ' end as dt4type,ca.fcashaccname as FRtnCashAccName,ot.fcheckstate " +
                " ,us.fusercode from " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " ot left join  " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " ca on ot.frtncashacccode = ca.fcashacccode left join tb_sys_userlist us on  ot.fcreator=us.fusercode  where ot.FDataType = 'return') dt4" +
                " on dt1.fnum = dt4.fnum  and dt1.fcheckstate = dt4.fcheckstate left join Tb_Fun_Vocabulary f on dt1.FInvestType = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_InvestType) +
                " left join (select * from Tb_Base_TradeType where FCheckState = 1 and FServiceType like '%openfund%') dt5 on dt5.FTradeTypeCode = dt1.FTradeTypeCode " +
                " left join " + pub.yssGetTableName("Tb_Para_Portfolio")
                //edit by zhouxiang MS01254    开放式基金业务界面新建一笔认购数据，保存时在未审核界面产生两笔一模一样的数据    QDV4赢时胜(测试)2010年6月4日2_B    
                +" pf on dt1.fportcode = pf.fportcode and pf.fcheckstate=1" + 
                " left join " + pub.yssGetTableName("tb_para_Security") + " vs on dt1.fsecuritycode = vs.FSECURITYCODE" +
                " left join " + pub.yssGetTableName("Tb_Para_InvestManager") + " pi on dt1.finvmgrcode = pi.finvmgrcode" +
                buildFilterSql() +
                "order by dt1.fnum,dt1.ftradetypecode,dt1.fportcode,dt1.fsecuritycode,dt1.finvmgrcode,dt1.FInvestType,f.FVocName,dt1.fbargaindate";
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("OpenFundTrade");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                openFund = new OpenFundTradeBean();
                openFunds = new ArrayList();
                num = rs.getString("num"); //交易编号
                openFund.setNum(num); //交易编号
                openFund.setTradeTypeCode(rs.getString("ftradetypecode")); //交易类型代码
                openFund.setTradeTypeName(rs.getString("FTradeTypeName")); //交易类型名称
                openFund.setPortCode(rs.getString("FPortCode")); //组合代码
                openFund.setPortName(rs.getString("FPortName")); //组合名称
                openFund.setSecurityCode(rs.getString("FSecurityCode")); //基金代码
                openFund.setSecurityName(rs.getString("FSECURITYNAME")); //基金名称
                openFund.setBargainDate(rs.getDate("FBARGAINDATE")); //申请日期
                openFund.setInvestType(rs.getString("FInvestType")); //投资类型
                openFund.setInvMgrCode(rs.getString("FInvMgrCode")); //投资经理
                openFund.setInvMgrName(rs.getString("finvmgrname")); //投资经理
                openFund.setSuperDataType(rs.getString("createor")); //整体状态
                this.checkStateId = rs.getInt("FCheckState"); //审核状态
                this.checkStateName = YssFun.getCheckStateName(this.checkStateId); //审核状态
                openFund.setDesc(rs.getString("FDesc")); //描述
                openFund.setApplyDate(rs.getDate("FApplyDate")); //申请业务日子
                openFund.setApplyCashAccCode(rs.getString("FApplyCashAccCode")); //申请帐户
                openFund.setApplyCashAccName(rs.getString("fapplycashaccname")); //申请帐户
                openFund.setApplyMoney(rs.getDouble("FApplyMoney")); //申请金额
                openFund.setComfDate(rs.getDate("FComfDate")); //确认业务日期
                openFund.setComCashAccCode(rs.getString("fcomcashacccode"));//add by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A //赎回账户
                openFund.setComCashAccName(rs.getString("fcomcashaccname"));//add by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A //赎回账户
                openFund.setComfAmount(rs.getDouble("FComfAmount")); //确认数量
                openFund.setSwitchAmount(rs.getDouble("FSwitchAmount")); //转入数量
                openFund.setComfMoney(rs.getDouble("FComfMoney")); //确认金额
                openFund.setComfFee(rs.getDouble("FComfFee")); //确认交易费用
                openFund.setTradePrice(rs.getDouble("FTradePrice")); //交易价格
                openFund.setTsfFunds(rs.getString("FTsfFunds")); //转入基金
                openFund.setTsfFundsName(rs.getString("FTsfFundName")); //转入基金
                openFund.setReturnDate(rs.getDate("FReturnDate")); //退还业务日期
                openFund.setReturnFee(rs.getDouble("FReturnFee")); //退还费用
                openFund.setReturnMoney(rs.getDouble("FReturnMoney")); //退还金额
                openFund.setRtnCashAccCode(rs.getString("FRtnCashAccCode")); //退还账号
                openFund.setRtnCashAccName(rs.getString("frtncashaccname")); //退还账号
                openFund.setComDifMoney(rs.getDouble("fcomdifmoney"));//story 1574 add by zhouwei 20111108 调整金额
                openFund.setReturnDifMoney(rs.getDouble("freturndifmoney"));
                openFunds.add(openFund); //增加到集合中
                this.creatorCode=rs.getString("creator");
                bufAll.append(this.buildRowStr()).append("\f\f");
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取开放式基金业务出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    /**
     * 模糊查询
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
        String[] arrData;
        String sResult = " where 1=1 ";
      //20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return sResult;
    	//=============end=================
        if ("1".equals(openFund.getIsOnlyColumns()) && pub.isBrown()==false) {	//20111027 modified by liubo.STORY #1285.  如果要浏览数据，则直接返回
            return " where 1=2 ";
        }
        if (sRecycled != "" && sRecycled != null && sRecycled.split("\r\n").length > 1) { //如果当前查询条件中有\r\n则需要处理
            //根据规定的符号，把多个sql语句分别放入数组
            arrData = sRecycled.split("\r\n");
            //循环执行这些删除语句
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
            }
        } //循环完毕就取到最后一条数据了（利用循环判断更加缜密）
        if ("2".equals(openFund.getIsOnlyColumns())) {
            if (openFund.getNum().length() > 0) { //交易编号
            	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
                sResult += " and dt1.FNum = " + dbl.sqlString(openFund.getNum());
            }
            if (openFund.getTradeTypeCode().length() > 0) { //交易类型代码
            	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
                sResult += " and dt1.FTradeTypeCode = " + dbl.sqlString(openFund.getTradeTypeCode());
            }
            if (openFund.getPortCode().length() > 0) { //组合代码
            	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
                sResult += " and dt1.FPortCode = " + dbl.sqlString(openFund.getPortCode());
            }
            if (openFund.getSecurityCode().length() > 0) { //基金代码
            	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
                sResult += " and dt1.FSecurityCode = " + dbl.sqlString(openFund.getSecurityCode());
            }
            if (openFund.getBargainDate() != null 
            		&& !openFund.getBargainDate().equals(YssFun.toDate("9998-12-31")) ) { //申请日期  modify by zhangjun BUG#4448 2012-05-11
            	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
                sResult += " and dt1.FBARGAINDATE = " + dbl.sqlDate(openFund.getBargainDate());
            }
            //edit by songjie 2010.03.31 国内：MS00945 QDV4赢时胜(测试)2010年3月25日10_B 
            //若根据筛选条件查询数据  投资类型 选择'所有'的话 ，则不将投资类型作为查询条件 
            if (openFund.getInvestType().length() > 0 && !openFund.getInvestType().equals("99")) { //投资类型
            	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
                sResult += " and dt1.FInvestType = " + dbl.sqlString(openFund.getInvestType());
            }            
            if (openFund.getInvMgrCode().trim().length() >0) { //投资经理
            	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
                sResult += " and dt1.FInvMgrCode = " + dbl.sqlString(openFund.getInvMgrCode());
                
            //==edit by licai 20101110 BUG #201 开放式基金业务交易编号加载的控制=====
            }else{
            	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
            	//editb by songjie 2011.06.22 无法获取交易编号 将 is NULL 改为 = ' '因为FInvMgrCode为非空字段 所以不应该用 is NULL 查询
            	sResult += " and dt1.FInvMgrCode = ' ' ";
            }
            //==edit by licai 20101110 BUG #201 开放式基金业务交易编号加载的控制==end===
            return sResult;
        }
        if (!openFund.getSelInvestType().equals("99") && openFund.getSelInvestType().length() > 0) { //查询条件_投资类型
        	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
            sResult += "and dt1.FInvestType = " + dbl.sqlString(openFund.getSelInvestType());
        }
        if (openFund.getSelPortCode().length() > 0) { //查询条件_组合代码
        	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
            sResult += " and dt1.FPortCode = " + dbl.sqlString(openFund.getSelPortCode());
        }
        if (openFund.getSelTradeTypeCode().length() > 0) { //查询条件_交易类型代码
        	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
            sResult += " and dt1.FTradeTypeCode = " + dbl.sqlString(openFund.getSelTradeTypeCode());
        }
        if (openFund.getBargainStateDate() != null && openFund.getBargainEndDate() != null) { //查询条件_申请日期段
        	//edit by songjie 2011.06.17  获取交易编号的时候报未明确到列错误
            sResult += " and dt1.FBARGAINDATE between " + dbl.sqlDate(openFund.getBargainStateDate()) +
                " and " + dbl.sqlDate(openFund.getBargainEndDate());
        }
        return sResult;
    }
    

    public String getListViewData2() throws YssException {      
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            sRecycled = sRowStr;
            if (sRecycled.split("\r\n").length > 1) {           	
            	sRowStr=sRecycled.split("\r\n")[0];
            }
            reqAry = sRowStr.split("\t");

            openFund.setNum(reqAry[0]); //交易编号
            openFund.setDataType(reqAry[1]); //数据类型
            openFund.setTradeTypeCode(reqAry[2]); //交易类型代码
            openFund.setPortCode(reqAry[3]); //组合代码
            openFund.setSecurityCode(reqAry[4]); //基金代码
            openFund.setBargainDate("0001-01-01".equals(reqAry[5]) ? null : YssFun.toDate(reqAry[5])); //申请日期
            openFund.setInvestType(reqAry[6]); //投资类型
            openFund.setInvMgrCode(reqAry[7]); //投资经理
            openFund.setApplyDate("0001-01-01".equals(reqAry[8]) ? null : YssFun.toDate(reqAry[8])); //申请业务日子
            openFund.setApplyCashAccCode(reqAry[9]); //申请帐户
            openFund.setApplyMoney(YssFun.toDouble(reqAry[10])); //申请金额
            openFund.setComfDate("0001-01-01".equals(reqAry[11]) ? null : YssFun.toDate(reqAry[11])); //确认业务日期
            openFund.setComfAmount(YssFun.toDouble(reqAry[12])); //确认数量
            openFund.setSwitchAmount(YssFun.toDouble(reqAry[13])); //确认转入数量
            openFund.setComfMoney(YssFun.toDouble(reqAry[14])); //确认金额
            openFund.setComfFee(YssFun.toDouble(reqAry[15])); //确认交易费用
            openFund.setTradePrice(YssFun.toDouble(reqAry[16])); //交易价格
            openFund.setTsfFunds(reqAry[17].length() == 0 ? " " : reqAry[17]); //转入基金
            openFund.setReturnDate("0001-01-01".equals(reqAry[18]) ? null : YssFun.toDate(reqAry[18])); //退还业务日期
            openFund.setReturnFee(YssFun.toDouble(reqAry[19])); //退还费用
            openFund.setReturnMoney(YssFun.toDouble(reqAry[20])); //退还金额
            openFund.setRtnCashAccCode(reqAry[21]); //退还账号
            openFund.setDesc(reqAry[22]); //描述
            this.checkStateId = YssFun.toInt(reqAry[23]); //审核状态
            openFund.setBargainStateDate("".equals(reqAry[11]) ? null : YssFun.toDate(reqAry[24])); //查询条件_开始申请日期
            openFund.setBargainEndDate("".equals(reqAry[11]) ? null : YssFun.toDate(reqAry[25])); //查询条件_结束申请日期
            openFund.setSelPortCode(reqAry[26]); //查询条件_组合代码
            openFund.setSelTradeTypeCode(reqAry[27]); //查询条件_交易类型代码
            openFund.setSelInvestType(reqAry[28]); //查询条件_投资类型
            openFund.setIsOnlyColumns(reqAry[29]); //查询条件_判断是否查询
            openFund.setComCashAccCode(reqAry[30]);//add by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A //赎回账户
            //story 1574 add by zhouwei 20111108 调整金额
            openFund.setComDifMoney(YssFun.toDouble(reqAry[31]));
            openFund.setReturnDifMoney(YssFun.toDouble(reqAry[32]));
            super.parseRecLog();
        } catch (Exception e) {
            throw new YssException("解析数据出错", e);
        }

    }

    /**
     * 组装数据
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        String reqStr = "";
        for (int i = 0; i < openFunds.size(); i++) { //遍历
            openFund = (OpenFundTradeBean) openFunds.get(i);
            //组装数据
            buf.append(openFund.getNum()).append("\t");
            buf.append(openFund.getDataType()).append("\t");
            buf.append(openFund.getTradeTypeCode()).append("\t");
            buf.append(openFund.getPortCode()).append("\t");
            buf.append(openFund.getPortName()).append("\t");
            buf.append(openFund.getSecurityCode()).append("\t");
            buf.append(openFund.getSecurityName()).append("\t");
            buf.append(openFund.getBargainDate() != null ? YssFun.formatDate(openFund.getBargainDate(), "yyyy-MM-dd") : "").append("\t");
            buf.append(openFund.getInvestType()).append("\t");
            buf.append(openFund.getInvMgrCode()).append("\t");
            buf.append(openFund.getInvMgrName()).append("\t");
            buf.append(openFund.getApplyDate() != null ? YssFun.formatDate(openFund.getApplyDate(), "yyyy-MM-dd") : "").append("\t");
            buf.append(openFund.getApplyCashAccCode()).append("\t");
            buf.append(openFund.getApplyCashAccName()).append("\t");
            buf.append(openFund.getApplyMoney()).append("\t");
            buf.append(openFund.getComfDate() != null ? YssFun.formatDate(openFund.getComfDate(), "yyyy-MM-dd") : "").append("\t");
            buf.append(openFund.getComfAmount()).append("\t");
            buf.append(openFund.getSwitchAmount()).append("\t");
            buf.append(openFund.getComfMoney()).append("\t");
            buf.append(openFund.getComfFee()).append("\t");
            buf.append(openFund.getTradePrice()).append("\t");
            buf.append(openFund.getTsfFunds()).append("\t");
            buf.append(openFund.getTsfFundsName()).append("\t");
            buf.append(openFund.getReturnDate() != null ? YssFun.formatDate(openFund.getReturnDate(), "yyyy-MM-dd") : "").append("\t");
            buf.append(openFund.getReturnFee()).append("\t");
            buf.append(openFund.getReturnMoney()).append("\t");
            buf.append(openFund.getRtnCashAccCode()).append("\t");
            buf.append(openFund.getRtnCashAccName()).append("\t");
            buf.append(openFund.getDesc()).append("\t");
            buf.append(openFund.getTradeTypeName()).append("\t");
            buf.append(openFund.getSuperDataType()).append("\t");
            buf.append(openFund.getBargainStateDate() != null ? YssFun.formatDate(openFund.getBargainStateDate(), "yyyy-MM-dd") : "").append("\t");
            buf.append(openFund.getBargainEndDate() != null ? YssFun.formatDate(openFund.getBargainEndDate(), "yyyy-MM-dd") : "").append("\t");
            buf.append(openFund.getSelPortCode()).append("\t");
            buf.append(openFund.getSelTradeTypeCode()).append("\t");
            buf.append(openFund.getSelInvestType()).append("\t");
            buf.append(openFund.getComCashAccCode()).append("\t");//add by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A //赎回账户
            buf.append(openFund.getComCashAccName()).append("\t");//add by yanghaiming 20100702 MS01234 QDV4赢时胜上海2010年5月31日01_A //赎回账户
            buf.append(openFund.getComDifMoney()).append("\t");//转投调整金额 story 1574 add by zhouwei 20111108
            buf.append(openFund.getReturnDifMoney()).append("\t");//现金调整金额 story 1574 add by zhouwei 20111108
            buf.append(super.buildRecLog()).append("\f\f");
        }
        reqStr = buf.toString();
        if (reqStr.length() > 0) {
            reqStr = reqStr.substring(0, reqStr.length() - 2);
        }
        return reqStr;
    }

    /**
     * 返回一整条数据
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String getOperValue(String sType) throws YssException {
        String strRtn = "";
        try {
            if (sType.equalsIgnoreCase("getsetting")) {
                this.getSetting();
                strRtn = this.buildRowStr();
            }
            if (sType.equalsIgnoreCase("getsetting2")) {//story 1574 add by zhouwei 20111110 针对修改和删除操作 
                this.getSetting2();
                strRtn = this.buildRowStr();
            }
            if (sType.equalsIgnoreCase("getPrice")) {
                return this.getPrice();
            }
            if (sType.equalsIgnoreCase("getYSHL")) {//add by guolongchao 20110831 STORY 1222 
            	 YssOperFun fun = new YssOperFun(pub);
            	 return fun.getSecPayRecBal("",YssFun.addDay(openFund.getBargainDate(), -1),this.openFund.getPortCode(), 
   		              "06", "06TR", this.openFund.getSecurityCode(),
                         "","","") ;	
            }
            if (sType.equalsIgnoreCase("getSubTsfType")) {//add by guolongchao 20110831 STORY 1222 
                return this.getSubTsfType();
            }

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return strRtn;
    }
    //story 1574 add by zhouwei 20111108 获取需要保存的证券编号
    public String getSecurityCodes(List alRightEquitys){
    	OpenFundTradeBean openBean=null;
    	String securityCodes="";
    	Iterator it=alRightEquitys.iterator();
        while(it.hasNext()){
        	openBean=(OpenFundTradeBean)it.next();
        	securityCodes+=openBean.getSecurityCode()+",";
        }
        securityCodes=operSql.sqlCodes(securityCodes);
    	return securityCodes;
    }
    //story 1574 add by zhouwei 20111108 往开放式基金交易表中插入数据
    public void insert(String sNums, java.util.Date beginTradeDate,
			java.util.Date endTradeDate, String sSecurityCode,
			String sPortCode, String sInvMgrCode,
			String sTradeType,
			boolean bAutoDel, boolean needNums, String sDsType)
			throws YssException {
		String strSql = "";
		int i = 0;
		PreparedStatement pst = null;
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String sFNum = "";
		String sTmpNum = "";
		String sFees = "";
		String[] sFee = null;
		OpenFundTradeBean openFund=null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			if (bAutoDel) {
				delete(sNums, beginTradeDate, endTradeDate,sSecurityCode, sPortCode, sInvMgrCode, sTradeType,
						needNums, sDsType);
			}
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Data_OpenFundTrade")
					+ " (FNum,FDataType,FTradeTypeCode,FPortCode," +
                        "FSecurityCode,FBARGAINDATE,FInvestType,FInvMgrCode,FApplyDate,FApplyCashAccCode,FApplyMoney,FCheckState,FCreator,FCreateTime,FDataBirth,fcheckuser,fchecktime) "+ 
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);			
			for (i = 0; i < this.list.size(); i++) {
				openFund=(OpenFundTradeBean)list.get(i);
				pst.setString(1,openFund.getNum());
				pst.setString(2, openFund.getDataType());
				pst.setString(3, openFund.getTradeTypeCode());
				pst.setString(4, openFund.getPortCode());
				pst.setString(5, openFund.getSecurityCode());
				pst.setDate(6,  YssFun.toSqlDate(openFund.getBargainDate()));
				pst.setString(7, openFund.getInvestType());
				pst.setString(8, openFund.getInvMgrCode());
				pst.setDate(9, YssFun.toSqlDate(openFund.getApplyDate()));
				pst.setString(10, openFund.getApplyCashAccCode());
				pst.setDouble(11, openFund.getApplyMoney());
				pst.setInt(12, 1);
				pst.setString(13, pub.getUserName());
				pst.setString(14,  YssFun.formatDatetime(new java.util.
                            Date()));
				pst.setString(15, "QY_CL");
				pst.setString(16, pub.getUserCode());
				pst.setString(17, YssFun.formatDatetime(new java.util.
                        Date()));
				pst.executeUpdate();
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new YssException(e.getMessage(), e);
		} finally {
			dbl.closeStatementFinal(pst);
		}
	}
    //删除数据的方法 add by zhouwei 20111108 story 1574
	public void delete(String sNums, java.util.Date beginTradeDate,
			java.util.Date endTradeDate, String sSecurityCode,
			String sPortCode, String sInvMgrCode,
			String sTradeType,
			boolean needNums, String sDsType) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String sWhereSql = "";
		String sWhereSql1 = "";
		String strSql1 = "";
		String[] sSubNums = null;
		String nums = "";
		try {
			sWhereSql = this.buildWhereSql(sNums, beginTradeDate, endTradeDate,sSecurityCode, sPortCode,
					sInvMgrCode, sTradeType, sDsType);

			if (sWhereSql.trim().length() == 0
					|| sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
				return;
			}
			if (needNums) {
				if (sNums.length() > 0) {
					sSubNums = sNums.split(",");
					for (int i = 0; i < sSubNums.length; i++) {
						strSql = "select distinct FNum from "
								+ pub.yssGetTableName("Tb_Data_OpenFundTrade")
								+ " where FNum like '"
								+ sSubNums[i]
										.substring(0, sSubNums[i].length() - 9)
								+ "%'";
						rs = dbl.openResultSet(strSql);
						while (rs.next()) {
							nums += rs.getString("FNum") + ",";
						}
						dbl.closeResultSetFinal(rs);
					}
				}
				
				if (nums.trim().length() > 0) {
					nums = operSql.sqlCodes(nums);
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Data_OpenFundTrade")
							+ " where FNum in (" + nums + ")" ;
					dbl.executeSql(strSql);

				}
			} else {
				strSql = "select a.FNum as FZNum from "
						+ pub.yssGetTableName("Tb_Data_OpenFundTrade")+" a "+
						sWhereSql;
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					nums += rs.getString("FZNum") + ",";
				}
				dbl.closeResultSetFinal(rs);
				nums = operSql.sqlCodes(nums);
				// 国内权益处理---
				strSql = "delete from "
						+ pub.yssGetTableName("Tb_Data_OpenFundTrade")
						+ " where FNum in (" + nums + ")";				
				dbl.executeSql(strSql);
				
			}
			if(nums.equals("''")){
				return;
			}
			//若反审核开放式基金业务数据，则之前由该数据生成的综合业务数据、证券应收应付数据、现金应收应付数据、资金调拨数据,交易数据全部删除
            String strSub = " delete from " + pub.yssGetTableName("Tb_Data_Integrated") + 
                 	 " where FRelaNum in (" + nums + 
                 	 ") and FNumType = 'openfund' ";
            dbl.executeSql(strSub);

            strSub = " delete from " + pub.yssGetTableName("Tb_Data_Secrecpay") + 
            		 " where FRelaNum in (" + nums + 
            		 ") and FRelaType = 'openfund' ";
            dbl.executeSql(strSub);

            strSub = " delete from " + pub.yssGetTableName("Tb_Data_Cashpayrec") + 
            		 " where FRelaNum in (" + nums + 
            		 ") and FRelaType = 'openfund' ";
            dbl.executeSql(strSub);

            strSub = " delete from " + pub.yssGetTableName("Tb_Cash_Subtransfer") +
            		 " where FNum in( select FNum from " + pub.yssGetTableName("tb_cash_transfer") + 
            		 " where FRelaNum in (" + nums + ") and FNumType = 'openfund' ) ";
            dbl.executeSql(strSub);
            
            strSub = " delete from " + pub.yssGetTableName("tb_cash_transfer") +
            		 " where FRelaNum in (" + nums + 
            	     ") and FNumType = 'openfund' ";
            dbl.executeSql(strSub);
            //删除业务处理产生的交易数据
            strSql = "select a.FNum as FZNum,b.FNum as FSNum from "
				+ pub.yssGetTableName("Tb_Data_SubTrade")
				+ " a left join (select * from "
				+ pub.yssGetTableName("Tb_Data_Trade")
				+ ") b on a.FSecurityCode = b.FSecurityCode  and a.FBrokerCode = b.FBrokerCode"
				+ " and a.FInvMgrCode = b.FInvMgrCode and a.FBargainDate = b.FBargainDate and a.ftradetypecode =b.ftradetypecode"	
				+" where a.ftradetypecode='39' and a.fdealnum in ("+nums+")";
			rs = dbl.openResultSet(strSql);
			String FZNum="";
			String FSNum="";
			while (rs.next()) {
				FZNum += rs.getString("FZNum") + ",";
				FSNum += rs.getString("FSNum") + ",";
			}
			FZNum = operSql.sqlCodes(FZNum);
			FSNum = operSql.sqlCodes(FSNum);
			strSub = "delete from "
				+ pub.yssGetTableName("Tb_Data_SubTrade")+" where fnum in ("+FZNum+")";
			dbl.executeSql(strSub);
			strSub = "delete from " + pub.yssGetTableName("Tb_Data_Trade")+" where fnum in ("+FSNum+")";
			dbl.executeSql(strSub);
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	private String buildWhereSql(String sNums, java.util.Date beginTradeDate,
			java.util.Date endTradeDate, String sSecurityCode,
			String sPortCode, String sInvMgrCode,
			String sTradeType,
			String sDsType) {
		String sResult = " where 1=1 ";
		
		if (sNums.length() > 0) {
			sResult += " and a.FNum in(" + operSql.sqlCodes(sNums) + ")";
		}
		if (beginTradeDate != null && endTradeDate == null) {
			sResult += " and a.FBargainDate = " + dbl.sqlDate(beginTradeDate);
		}
		if (beginTradeDate != null && endTradeDate != null) {
			sResult += " and a.FBargainDate between "
					+ dbl.sqlDate(beginTradeDate) + " and "
					+ dbl.sqlDate(endTradeDate);
		}		
		if (sSecurityCode.length() > 0) {
			sResult += " and a.FSecurityCode in("
					+ this.operSql.sqlCodes(sSecurityCode) + ")";
		}
		if (sPortCode.length() > 0) {
			sResult += " and a.FPortCode in( "
					+ this.operSql.sqlCodes(sPortCode) + ")";
		}
		if (sInvMgrCode.length() > 0) {
			sResult += " and a.FInvMgrCode = " + dbl.sqlString(sInvMgrCode);
		}	
		if (sTradeType.length() > 0) {
			sResult += " and a.FTradeTypeCode in( "
					+ this.operSql.sqlCodes(sTradeType) + ")";
																
		}		
		if (sDsType.length() > 0) {
			if ("QY_CL".equalsIgnoreCase(sDsType)) {
				sResult += " and a.FDataBirth= " + dbl.sqlString(sDsType);//如果为权益处理时 FDataBirth的值为QY_CL
			} else {
				sResult += " and (a.FDataBirth= " + dbl.sqlString(sDsType)
						+ " or a.FDataBirth is null)";
			}
			
		}
		return sResult;
	}
}
