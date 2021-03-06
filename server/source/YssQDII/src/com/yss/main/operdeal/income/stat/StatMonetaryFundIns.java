package com.yss.main.operdeal.income.stat;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.main.operdeal.opermanage.OpenFundManage;

/**
 *
 * <p>Title:记提货币基金每万份收益 </p>
 *
 * <p>Description: MS00013 QDV4.1赢时胜（上海）2009年4月20日13_A  国内基金业务 2009.06.16 蒋锦 添加</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class StatMonetaryFundIns
    extends BaseIncomeStatDeal {
    //储存计息相关参数的值，Key = 组合代码 + "\t" + 参数的词汇代码
    private HashMap hmParams = null;
    //储存不同复利计算方式的组合代码 index 1 :使用昨日余额计算的组合代码。index 2：使用今日余额计算的组合代码。
    private ArrayList alCompoundCalType = null;
    //记入基金投资和记入应收红利的组合代码
    private ArrayList alCloseType = null;
    //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
    //计提开始日期为申购确认日的组合代码
    private ArrayList alApplyConfirmDate = null;
    //计提开始日期为申购确认日下一个工作日的组合代码
    private ArrayList alApplyConfNextDate = null;
    //获取基金万份收益的日期为公告日期（T+1日）的组合代码
    private ArrayList alReadDatePorts = null;
    //获取基金万份收益的日期为净值日期（T日）的组合代码
    private ArrayList alBargainDatePorts = null;
    //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
    boolean analy1;
    boolean analy2;
    private HashMap secHolidayMap=null;//story2617 add by zhouwei 20120514 证券与节假日群的对应关系
    private ArrayList secRecPayList=null;//story2617 add by zhouwei 20120514 证券应收应付集合
    public StatMonetaryFundIns() {
    }

    public ArrayList getDayIncomes(java.util.Date dDate) throws YssException {
        HashMap hmRecPay = null;
        ArrayList alResult = null;
        //add by songjie 2011.08.09 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A
        BaseStgStatDeal secstgstat = null;
        try {
        	//--- add by songjie 2014.02.14 BUG 88874 QDV4赢时胜(上海)2014年02月13日01_B start---//
        	//如果没有可计提的证券，则不计提
        	if(selCodes.trim().length() == 0){
        		return new ArrayList();
        	}
        	//--- add by songjie 2014.02.14 BUG 88874 QDV4赢时胜(上海)2014年02月13日01_B end---//
        	
        	//---add by songjie 2011.08.09 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
        	//先删除相关组合、相关证券、计提日对应的应收基金红利证券应收应付数据
        	SecRecPayAdmin secRecPay = new SecRecPayAdmin();
        	secRecPay.setYssPub(pub);
        	secRecPay.delete("", dDate, dDate, YssOperCons.YSS_ZJDBLX_Rec,
        			   YssOperCons.YSS_ZJDBZLX_TR_RecFundIns, selCodes,"", portCodes, "",
        			   //edit by songjie 2012.03.13 BUG 4017 QDV4赢时胜(测试)2012年3月13日01_B 删除条件 FDatasoure 由 = 1 改为 = 0
        			   "", "",0, 0, "", "", "");

        	//统计证券库存
    		secstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("SecurityStorage");
            secstgstat.setYssPub(pub);
            //modify by zhouwei 20120509 统计选择证券的库存
            secstgstat.setStatCodes(selCodes);
            //secstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(portCodes));
            secstgstat.partStroageStat1(dDate, dDate, operSql.sqlCodes(portCodes), true, false);
            //统计证券应收应付库存
    		secstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("SecRecPay");
            secstgstat.setYssPub(pub);
            secstgstat.setStatCodes(selCodes);
            //secstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(portCodes));  
            secstgstat.stroageStat(dDate,dDate, operSql.sqlCodes(portCodes), true, false, false);
            //---add by songjie 2011.08.09 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
            CtlPubPara pubPara=new CtlPubPara();
            pubPara.setYssPub(pub);
            String newPorts=pubPara.getMfIncomeNewWay(dDate);//add by zhouwei 获取采用新计提方式的组合
            String[] arrPorts=this.portCodes.split(",");
            String oldPorts="";//旧方式的组合
            String allPorts=this.portCodes;
            for(int i=0;i<arrPorts.length;i++){
            	if(newPorts.indexOf(arrPorts[i])==-1){
            		oldPorts+=arrPorts[i]+",";
            	}
            }
            if(oldPorts.length()>1){
            	oldPorts=oldPorts.substring(0,oldPorts.length()-1);
            }
        	this.beginDate=dDate;
        	this.endDate=dDate;
            if(!("").equals(oldPorts)){//原有模式
            	this.portCodes=oldPorts;
	        	getCalParams(this.portCodes);
	        	//delete by songjie 2011.08.09 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A
	            //putSubMonFundIntstDate(dDate);
	            hmRecPay = getInterstBy(dDate, this.selCodes);
	            alResult = convertHashMapToArrayList(hmRecPay);
	            this.saveIncomes(alResult);//保存证券应收应付数据
            }
        	//story 2617 add by zhouwei 20120514  货币基金每万份收益计提
            if(!("").equals(newPorts)){//新方式 T-1 T-0
            	hmParams = null;           	    
            	alCompoundCalType = null;
            	alCloseType = null;
            	alApplyConfirmDate = null;
            	alApplyConfNextDate = null;
            	alReadDatePorts = null;
            	alBargainDatePorts = null;
            	this.portCodes=newPorts;
            	getCalParams(this.portCodes);
            	getInterstInNewMethod(dDate, selCodes, portCodes);//获取基金红利的集合
            	this.saveIncomes(this.secRecPayList);//保存证券应收应付数据
            }
        	secstgstat.stroageStat(dDate,dDate, operSql.sqlCodes(allPorts), true, false, false);//统计证券应收应付库存            	
        	alResult=new ArrayList();
        	
        } catch (Exception ex) {
            throw new YssException("提取货币基金每万份收益出错！", ex);
        }
        return alResult;
    }

    /**
     * 记提并保存证券应收应付
     * @param alIncome ArrayList
     * @throws YssException
     */
    public void saveIncomes(ArrayList alIncome) throws YssException {
        SecRecPayAdmin recPay = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        //---delete by songjie 2011.09.14 BUG 2631 QDV4赢时胜（深圳_roy）2011年9月2日04_B start---//
//        int iDays = 0;
//        java.util.Date theDate = null;
        //---delete by songjie 2011.09.14 BUG 2631 QDV4赢时胜（深圳_roy）2011年9月2日04_B end---//
        
        //--- add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        SecPecPayBean secRecPay = null;
        Date logBeginDate = null;
        //--- add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
        	//add by zhouwei 20120515 story 2617
        	if(alIncome.size()==0){
        		return;
        	}
        	//-------------end-----------
        	
    		//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	for(int i = 0; i < alIncome.size(); i++){
        		secRecPay = (SecPecPayBean)alIncome.get(i);
        		logBeginDate = new Date();
        		
        		logInfo = "证券代码:" + secRecPay.getStrSecurityCode() + 
        		           "\r\n利息:" + secRecPay.getMoney();
        		
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, false, 
                		secRecPay.getStrPortCode(), secRecPay.getTransDate(),
                		secRecPay.getTransDate(),secRecPay.getTransDate(),
                		logInfo," ",logBeginDate,logSumCode,new Date());
        		}
        	}
    		//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	
            recPay = new SecRecPayAdmin();
            recPay.setYssPub(pub);
            recPay.addList(alIncome);

            conn.setAutoCommit(false);
            bTrans = true;
            recPay.insert("", beginDate,
               endDate, YssOperCons.YSS_ZJDBLX_Rec,
               YssOperCons.YSS_ZJDBZLX_TR_RecFundIns, portCodes, "",
               "", selCodes, "",
               //edit edit by songjie 2012.03.13 BUG 4017 QDV4赢时胜(测试)2012年3月13日01_B 删除条件 FDatasoure 由 = 1 改为 = 0
               0, true, 0, false, "", "", "");

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //---delete by songjie 2011.09.14 BUG 2631 QDV4赢时胜（深圳_roy）2011年9月2日04_B start---//
//            recPay = new SecRecPayAdmin();
//
//            iDays = YssFun.dateDiff(beginDate, endDate);
//            theDate = this.beginDate;
//            for (int i = 0; i <= iDays; i++) {
//                OpenFundManage fundManage = new OpenFundManage();
//                fundManage.setYssPub(pub);
//                fundManage.initOperManageInfo(theDate, portCodes);
//                fundManage.getCalParams(portCodes);
//
//                recPay.setYssPub(pub);
//                recPay.addList(fundManage.getMonetaryFundIncome(""));
//                theDate = YssFun.addDay(theDate, 1);
//            }
//
//            conn.setAutoCommit(false);
//            bTrans = true;
//            recPay.insert("",
//                          beginDate,
//                          endDate,
//                          "02",
//                          "02TR",
//                          portCodes,
//                          "","","","",
//                          1,
//                          true, 0, false,
//                          "", "", "");
//            conn.commit();
//            bTrans = false;
//            conn.setAutoCommit(true);
            //---delete by songjie 2011.09.14 BUG 2631 QDV4赢时胜（深圳_roy）2011年9月2日04_B end---//
        } catch (Exception e) {
        	//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
        	try{
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, true, 
                		secRecPay.getStrPortCode(), secRecPay.getTransDate(),
                		secRecPay.getTransDate(),secRecPay.getTransDate(),
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                		(logInfo + "\r\n计提货币基金万份收益出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
                		.replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", ""),
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                		" ",logBeginDate,logSumCode,new Date());
        		}
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	finally{//添加 finally 保证可以抛出异常
        		throw new YssException("保存货币基金每万份收益出错！", e);
        	}
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 将每万份收益率主表的数据插入子表中
     * 如果如果业务日期是节假日，或者节假日前一天则将万份收益率平均拆到节假日的每一天
     * @param dReadDate Date
     * @throws YssException
     */
    //edit by songjie 2011.08.09 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A
    private void putSubMonFundIntstDate(java.util.Date dReadDate,String portcode) throws YssException{
        String sqlStr = "";
        Connection conn = dbl.loadConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        ResultSet rsSub = null;
        java.util.Date dLastWorkDate = null;
        boolean bTrans = false;
        String strColumn = "";//add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A
        //add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
		SecRecPayAdmin secRecPay = null;
        try {
		    //---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
        	secRecPay = new SecRecPayAdmin();
        	secRecPay.setYssPub(pub);
        	//---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
            sqlStr = "INSERT INTO " + pub.yssGetTableName("Tb_Data_SubMonFundIntst") +
            "(FSECURITYCODE, FBARGAINDATE, FREADDATE, FFUNDRATE, FDESC, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME)" +
                " VALUES(?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(sqlStr);

            //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
            if(alReadDatePorts.contains(portcode)){
            	strColumn = "FReadDate";
            }
            if(alBargainDatePorts.contains(portcode)){
            	strColumn = "FBargainDate";
            }
            if(!alReadDatePorts.contains(portcode) && !alBargainDatePorts.contains(portcode)){
            	strColumn = "FReadDate";
            }
            //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
            
            conn.setAutoCommit(false);
            bTrans = true;
            //---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
            if(strColumn.equals("FBargainDate")){
            	sqlStr = " delete from " + pub.yssGetTableName("Tb_Data_SubMonFundIntst") +
            	" where FReadDate in ( select FReadDate from " + pub.yssGetTableName("Tb_Data_SubMonFundIntst") + 
            	" where FBargainDate = " + dbl.sqlDate(dReadDate) + ") ";
            	dbl.executeSql(sqlStr);
            }
            //---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
            sqlStr = "DELETE " + pub.yssGetTableName("Tb_Data_SubMonFundIntst") +
            " WHERE " + strColumn + " = " + dbl.sqlDate(dReadDate);
            dbl.executeSql(sqlStr);

            sqlStr = "SELECT a.*, b.FHolidaysCode FROM " +
            	pub.yssGetTableName("Tb_Data_MonFundInterest") +
                " a LEFT JOIN " + pub.yssGetTableName("TB_Para_Security") +
                //edit by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A
                " b ON a.FSecurityCode = b.FSecurityCode WHERE " + strColumn + " = " + dbl.sqlDate(dReadDate) +
                //edit by songjie 2011.09.13 查询货币式基金万分收益率时，需根据收益计提已选证券筛选查询
                /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
                * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//                " AND a.FSecurityCode in (" + operSql.sqlCodes(this.selCodes) + ") " +
                " AND (" + operSql.getNumsDetail(this.selCodes,"b.FSecurityCode",500) + ")" +
                /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
                "and a.FCheckState = 1 ORDER BY FBARGAINDATE";
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while(rs.next()){
                int iDays = 0;
                boolean bargainDateIsWorkDate = false;//净值日期是否是工作日
                bargainDateIsWorkDate =
                    YssFun.dateDiff(
                        rs.getDate("FBARGAINDATE"),
                        this.getSettingOper().getWorkDay(rs.getString("FHolidaysCode"), rs.getDate("FBARGAINDATE"),0)) == 0 ? true : false;
                dLastWorkDate = this.getSettingOper().
                    getWorkDay(rs.getString("FHolidaysCode"), rs.getDate("FBARGAINDATE"), -1);
                //判断业务日期到业务日期前最后一个工作日之间是否有已被拆分过的万分收益
                sqlStr = "SELECT MAX(FBarGainDate) AS FBarGainDate, FSecurityCode FROM " +
                	pub.yssGetTableName("Tb_Data_SubMonFundIntst") +
                	" WHERE FSecurityCode = " + dbl.sqlString(rs.getString("FSecurityCode")) +
                    " AND FBARGAINDATE < " + dbl.sqlDate(rs.getDate("FBARGAINDATE")) +
                    " AND FCheckState = 1 GROUP BY FSecurityCode";
                rsSub = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                if(rsSub.next()){
                    if(dLastWorkDate.compareTo(rsSub.getDate("FBarGainDate")) < 0){
                        dLastWorkDate = rsSub.getDate("FBarGainDate");
                    }
                }
                //游标用完之后释放，避免浪费资源 sunkey@Modify 20090922
                dbl.closeResultSetFinal(rsSub);

                iDays = YssFun.dateDiff(dLastWorkDate, rs.getDate("FBARGAINDATE"));
                //节假日需要拆分，不是节假日就直接插入, 净值日期不为工作日的收益才需要拆分
                if(iDays > 1 && !bargainDateIsWorkDate){
				    //---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
                	if(alBargainDatePorts.contains(portcode)){
                		secRecPay.delete("", YssFun.addDay(dLastWorkDate, 1), rs.getDate("FBARGAINDATE"), 
                				YssOperCons.YSS_ZJDBLX_Rec, YssOperCons.YSS_ZJDBZLX_TR_RecFundIns, selCodes,
                				"", portCodes, "", "", "", 1, 0, "", "", "");
                	}
                	//---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
                    //用于记录最后一天之前已拆分的总数，最后用于轧差计算
                    double dbRate = 0;
                    for(int i = 1; i <= iDays; i++){
                        pst.setString(1, rs.getString("FSECURITYCODE"));
                        pst.setDate(2, YssFun.toSqlDate(YssFun.addDay(dLastWorkDate, i)));
                        pst.setDate(3, YssFun.toSqlDate(rs.getDate("FREADDATE")));
                        if(i != iDays){ //节假日拆分，最后一天轧差
                        	//---edit by songjie 2011.06.27 BUG 2090 QDV4赢时胜（深圳）2011年6月13日03_B 保留位数改为5位---//
                            pst.setDouble(4, YssD.round(YssD.div(rs.getDouble("FFUNDRATE"), iDays), 5));
                            dbRate += YssD.round(YssD.div(rs.getDouble("FFUNDRATE"), iDays), 5);
                            //---edit by songjie 2011.06.27 BUG 2090 QDV4赢时胜（深圳）2011年6月13日03_B 保留位数改为5位---//
                        } else{
                            pst.setDouble(4, YssD.sub(rs.getDouble("FFUNDRATE"), dbRate));
                        }
                        pst.setString(5, rs.getString("FDesc"));
                        pst.setInt(6, rs.getInt("FCHECKSTATE"));
                        pst.setString(7, rs.getString("FCREATOR"));
                        pst.setString(8, rs.getString("FCREATETIME"));
                        pst.setString(9, rs.getString("FCHECKUSER"));
                        pst.setString(10, rs.getString("FCHECKTIME"));
                        pst.executeUpdate();
                    }
                } else {
                    pst.setString(1, rs.getString("FSECURITYCODE"));
                    pst.setDate(2, YssFun.toSqlDate(rs.getDate("FBARGAINDATE")));
                    pst.setDate(3, YssFun.toSqlDate(rs.getDate("FREADDATE")));
                    pst.setDouble(4, rs.getDouble("FFUNDRATE"));
                    pst.setString(5, rs.getString("FDesc"));
                    pst.setInt(6, rs.getInt("FCHECKSTATE"));
                    pst.setString(7, rs.getString("FCREATOR"));
                    pst.setString(8, rs.getString("FCREATETIME"));
                    pst.setString(9, rs.getString("FCHECKUSER"));
                    pst.setString(10, rs.getString("FCHECKTIME"));
                    pst.executeUpdate();
                }
            }
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("删除每万份收益率字表出错！", ex);
        } finally{
            dbl.closeStatementFinal(pst);
            dbl.closeResultSetFinal(rs, rsSub);
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    /** 
     *story 2617 add by zhouwei 20120511 
     *把货币是基金万份收益数据拆分到子表
     *拆分规则是，净值日期是节假日最后一天，需要拆分到节假日每一天，净值日期是工作日，不需要拆分
    */
    private void splitMonFundInterestTable(java.util.Date dBargainDate1,java.util.Date dBargainDate2,
    		   String portcodes,String securityCodes) throws YssException{
    	PreparedStatement pst=null;
    	ResultSet rs=null;
    	String sql="";
    	Connection conn=dbl.loadConnection();
    	boolean bTrans=false;
    	try{
    		BaseOperDeal operDeal=this.getSettingOper();//判断日期是否为工作日的类
			conn.setAutoCommit(false);
	        bTrans = true;
	        //根据净值日期和证券代码来删除
	      	sql = " delete from " + pub.yssGetTableName("Tb_Data_SubMonFundIntst") +
	      	" where FBARGAINDATE>="+dbl.sqlDate(dBargainDate1)+" and FBARGAINDATE<="+dbl.sqlDate(dBargainDate2)+
	      	" and FSECURITYCODE in ("+operSql.sqlCodes(securityCodes)+")";
	      	dbl.executeSql(sql);
	      	
	      	sql = "INSERT INTO " + pub.yssGetTableName("Tb_Data_SubMonFundIntst") +
	            "(FSECURITYCODE, FBARGAINDATE, FREADDATE, FFUNDRATE, FDESC, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME)" +
	                " VALUES(?,?,?,?,?,?,?,?,?,?)";
	        pst = conn.prepareStatement(sql);
	      	//查询基金万份收益率，查询条件；净值日期和证券代码
	      	sql = "SELECT a.*, b.FHolidaysCode FROM " +
        	pub.yssGetTableName("Tb_Data_MonFundInterest") +
            " a LEFT JOIN " + pub.yssGetTableName("TB_Para_Security") +
            " b ON a.FSecurityCode = b.FSecurityCode WHERE a.FBARGAINDATE>= " + dbl.sqlDate(dBargainDate1) +" and a.FBARGAINDATE<= "+dbl.sqlDate(dBargainDate2)+
            " AND a.FSecurityCode in (" + operSql.sqlCodes(securityCodes) + ") and a.FCheckState = 1 ORDER BY a.FBARGAINDATE";
	      	rs=dbl.openResultSet(sql);
	      	while(rs.next()){
	      		//判断当前净值日期是否为节假日，如果为节假日，则需要拆分
	      		java.util.Date barGainDay=operDeal.getWorkDay(rs.getString("FHolidaysCode"), rs.getDate("FBARGAINDATE"), 0);
	      		if(YssFun.dateDiff(barGainDay, rs.getDate("FBARGAINDATE"))==0){//为工作日，不需要拆分
	      			pst.setString(1, rs.getString("FSECURITYCODE"));
                    pst.setDate(2, YssFun.toSqlDate(rs.getDate("FBARGAINDATE")));
                    pst.setDate(3, YssFun.toSqlDate(rs.getDate("FREADDATE")));
                    pst.setDouble(4, rs.getDouble("FFUNDRATE"));
                    pst.setString(5, rs.getString("FDesc"));
                    pst.setInt(6, rs.getInt("FCHECKSTATE"));
                    pst.setString(7, rs.getString("FCREATOR"));
                    pst.setString(8, rs.getString("FCREATETIME"));
                    pst.setString(9, rs.getString("FCHECKUSER"));
                    pst.setString(10, rs.getString("FCHECKTIME"));
                    pst.executeUpdate();
	      		}else{//节假日最后一天，需要拆分
	      			//获取上一个工作日，然后计算节假日的长度
	      			java.util.Date lastWorkDay=operDeal.getWorkDay(rs.getString("FHolidaysCode"), rs.getDate("FBARGAINDATE"), -1);
	      			int iDays=YssFun.dateDiff(lastWorkDay, rs.getDate("FBARGAINDATE"));
	      			double dbRate=0;
	      			for(int i = 1; i <= iDays; i++){
                        pst.setString(1, rs.getString("FSECURITYCODE"));
                        pst.setDate(2, YssFun.toSqlDate(YssFun.addDay(lastWorkDay, i)));
                        pst.setDate(3, YssFun.toSqlDate(rs.getDate("FREADDATE")));
                        if(i != iDays){ //节假日拆分，最后一天轧差	                        	
                            pst.setDouble(4, YssD.round(YssD.div(rs.getDouble("FFUNDRATE"), iDays), 5));
                            dbRate += YssD.round(YssD.div(rs.getDouble("FFUNDRATE"), iDays), 5);
                        } else{
                            pst.setDouble(4, YssD.sub(rs.getDouble("FFUNDRATE"), dbRate));
                        }
                        pst.setString(5, rs.getString("FDesc"));
                        pst.setInt(6, rs.getInt("FCHECKSTATE"));
                        pst.setString(7, rs.getString("FCREATOR"));
                        pst.setString(8, rs.getString("FCREATETIME"));
                        pst.setString(9, rs.getString("FCHECKUSER"));
                        pst.setString(10, rs.getString("FCHECKTIME"));
                        pst.executeUpdate();
	                   }
	      		}
	      	}
	      	conn.commit();
	      	conn.setAutoCommit(true);
	      	bTrans=false;
    	}catch (Exception e) {
			throw new YssException("拆分货币是基金万份收益出错！"+e, e);
		}finally{
			dbl.closeStatementFinal(pst);
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
		}
    }
    /**
     * 获取计息相关参数，并判断选中组合是否需要计息
     * @param sPortCodes String
     * @return boolean
     * @throws YssException
     */
    public boolean getCalParams(String sPortCodes) throws YssException {
        boolean bIsCalInterest = true;
        String[] arrPortCode = null;
        String sNoCalPortCodes = "";
        String sTodayPorts = ""; //使用今日红利计算的组合代码
        String sYestPorts = ""; //使用昨日红利计算的组合代码
        String sFundPorts = ""; //记入基金投资的组合代码
        String sRecPorts = ""; //记入应收红利的组合代码
        try {
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            hmParams = pubPara.getMonetaryFundIncomeCalaParams();
            arrPortCode = sPortCodes.split(",");
            alCompoundCalType = new ArrayList();
            alCloseType = new ArrayList();
			
			//---add by songjie 2011.08.09 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
            alApplyConfirmDate = new ArrayList();
            alApplyConfNextDate = new ArrayList();
            alReadDatePorts = new ArrayList();
            alBargainDatePorts = new ArrayList();
			//---add by songjie 2011.08.09 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
            
            if (hmParams == null) {
                //默认使用前一日余额计算
                alCompoundCalType.add(sPortCodes);
                //默认记入基金投资
                alCloseType.add(sPortCodes);
                return true;
            }
            for (int i = 0; i < arrPortCode.length; i++) {
                //判断是否计息
                if (hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_FUN_CONTINUE) != null &&
                    ( (String) hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_FUN_CONTINUE)).equalsIgnoreCase("0")) {
                    sNoCalPortCodes += (arrPortCode[i] + ",");
                    bIsCalInterest = false;
                }

                //获取复利计算方式
                if (hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_FUNDINSCALA) != null &&
                    ( (String) hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_FUNDINSCALA)).
                    equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_TODAY)) {
                    sTodayPorts += (arrPortCode[i] + ",");
                } else {
                    //如果没有取到参数，默认使用前一日余额计算
                    sYestPorts += (arrPortCode[i] + ",");
                }
                //获取日结型基金红利结转方式
                if (hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_RATERESULT) != null &&
                    ( (String) hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_RATERESULT)).
                    equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_RECRATE)) {
                    sRecPorts += (arrPortCode[i] + ",");
                } else {
                    //默认使用记入基金投资
                    sFundPorts += (arrPortCode[i] + ",");
                }

                //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
                //获取计提开始日期
                if(hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_FUNDSTARTDATE) != null &&
                   ((String) hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_FUNDSTARTDATE)).
                   equalsIgnoreCase("1")){
                	if(!alApplyConfirmDate.contains(arrPortCode[i]))
                		alApplyConfirmDate.add(arrPortCode[i]);
                } else {
                	if(!alApplyConfNextDate.contains(arrPortCode[i]))
                		alApplyConfNextDate.add(arrPortCode[i]);
                }
                //获取基金万份收益的日期
                if(hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_FUNDSTARTDATE) != null &&
                   ((String) hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_FUNDMFRATEDATE)).
                   equalsIgnoreCase("1")){
                	if(!alReadDatePorts.contains(arrPortCode[i]))
                		alReadDatePorts.add(arrPortCode[i]);
                } else {
                	if(!alBargainDatePorts.contains(arrPortCode[i]))
                		alBargainDatePorts.add(arrPortCode[i]);
                }
                //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
            }
            if (!bIsCalInterest) {
                //如果有组合是不计息的弹出信息提示
                throw new YssException("您选中的组合中以下组合：" + sNoCalPortCodes + "已被设置为不计息，请重新选择组合，或修改计息参数！");
            }

            if (sTodayPorts.length() > 0) {
                sTodayPorts = sTodayPorts.substring(0, sTodayPorts.length() - 1);
            }
            if (sYestPorts.length() > 0) {
                sYestPorts = sYestPorts.substring(0, sYestPorts.length() - 1);
            }
            if (sRecPorts.length() > 0) {
                sRecPorts = sRecPorts.substring(0, sRecPorts.length() - 1);
            }
            if (sFundPorts.length() > 0) {
                sFundPorts = sFundPorts.substring(0, sFundPorts.length() - 1);
            }

            alCloseType.add(sFundPorts);//记入基金投资
            alCloseType.add(sRecPorts);//记入应收红利

            alCompoundCalType.add(sYestPorts);//使用昨日红利计息的组合
            alCompoundCalType.add(sTodayPorts);//使用今日红利计息的组合
        } catch (Exception ex) {
            throw new YssException(ex);
        }
        return bIsCalInterest;
    }

    /**
     * 将哈希表中的应收数据储存到链表中
     * @param hmRecPay HashMap
     * @return ArrayList
     * @throws YssException
     */
    private ArrayList convertHashMapToArrayList(HashMap hmRecPay) throws YssException {
        ArrayList alRecPay = new ArrayList();

        Iterator it = hmRecPay.values().iterator();
        while (it.hasNext()) {
            alRecPay.add(it.next());
        }
        return alRecPay;
    }

    /**
     * 计提利息收益，将应收数据保存在哈希表中返回
     * 根据每万份收益率中的业务日期选择库存日期的成本计息，如果
     * 每万份收益率中的统一证券存在多条读数日期为同一天的记录，则只产生一条应收数据
     * @param theDate Date：记息日期
     * @param sSecCodes String：记息基金代码
     * @param sPortCodes String：组合代码
     * @return HashMap
     * @throws YssException
     */
    private HashMap getInterstBy(java.util.Date theDate,
                                 String sSecCodes) throws YssException {
        HashMap hmRecPay = new HashMap();
        StringBuffer bufSql = new StringBuffer();
        ResultSet rs = null;
        SecPecPayBean pay = null;
        String sqlInsDate = "";
        String sHashKey = "";
        String sPortCodes = "";
        double baseCuryRate;
        double portCuryRate;
        double dbCost = 0;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        
        //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
        String strSubSql = "";
		String[] portcodes = null;
        java.util.Date maxApplyConfirmDate = null;
        //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
		//edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
		java.util.Date tranDate = null;
        double fbal = 0;
        String hashKey = "";
        HashMap hmSpRecPay = new HashMap();
		//---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
        //add by songjie 2011.09.13 BUG 2629 QDV4赢时胜（深圳_roy）2011年9月2日02_B
		BaseOperDeal baseOper = null;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");

            //循环红利计算方式
            for (int i = 0; i < alCompoundCalType.size(); i++) {
                //使用今日成本余额
                if (i == 1) {
                    //使用今日成本计算那么库存日期等于万份收益率的净值日期
                    sqlInsDate = " AND ins.FBargainDate = sec.FStorageDate";
                } else { //使用昨日红利计算
                    //使用昨日红利计算，库存日期为万份收益率净值日期减一天
                    sqlInsDate = " AND ins.FBargainDate = " + dbl.sqlDateAdd("sec.FStorageDate", "+1");
                }

                sPortCodes = (String) alCompoundCalType.get(i);
                if (sPortCodes.length() == 0) {
                    continue;
                }
                //---edit by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A---//
                portcodes = sPortCodes.split(",");
                
                for (int j = 0; j < portcodes.length; j++){
                    //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
                    if(alReadDatePorts.contains(portcodes[j])){
					    //edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
                    	strSubSql = " = " + dbl.sqlDate(theDate);
                    }
                    if(alBargainDatePorts.contains(portcodes[j])){
					    ////edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
                    	strSubSql = " in (select FReadDate from " + pub.yssGetTableName("Tb_Data_SubMonFundIntst") + 
                    	//edit by songjie 2011.09.13 BUG 2629 QDV4赢时胜（深圳_roy）2011年9月2日02_B 添加计提日期大于等于净值日期的条件
                    	" where FBargainDate = " + dbl.sqlDate(theDate) + " ) and FBargainDate <= " + dbl.sqlDate(theDate);
                    }
                    if(!alReadDatePorts.contains(portcodes[j]) && !alBargainDatePorts.contains(portcodes[j])){
                    	////edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
						strSubSql = " = " + dbl.sqlDate(theDate);
                    }
                    //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
                    
                    putSubMonFundIntstDate(theDate, portcodes[j]);
                	//---edit by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A---//
					
                	bufSql.append(" SELECT sec.*, rec.FBAL, rec.FMBAL, rec.FVBAL, rec.FBASECURYBAL, ");
                	bufSql.append(" rec.FMBASECURYBAL, rec.FVBASECURYBAL, rec.FPORTCURYBAL, ");
                	bufSql.append(" rec.FMPORTCURYBAL, rec.FVPORTCURYBAL, rec.FBALF, rec.FPORTCURYBALF, ");
                	bufSql.append(" rec.FBASECURYBALF, ins.FFundRate, ins.FBargainDate ");   //此处增加确认金额，用于成本计算时用到songjie
                	//edit by songjie 2011.09.13  BUG 2629  QDV4赢时胜（深圳_roy）2011年9月2日02_B 查询证券对应的节假日群代码  
                	bufSql.append(" FROM (SELECT a.FHolidaysCode, a.FSecurityName, b.* ");
                	bufSql.append(" FROM (SELECT * ");
                	bufSql.append(" FROM ").append(pub.yssGetTableName("TB_Para_Security"));
                	/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
                	* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//                	bufSql.append(" WHERE FSecurityCode IN (" + operSql.sqlCodes(sSecCodes) + ") ");
                	bufSql.append(" WHERE " + operSql.getNumsDetail(sSecCodes,"FSecurityCode",500));
                	/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
                	bufSql.append(" ) a ");
                	bufSql.append(" JOIN (SELECT s.*, m.finteresttype, m.FClosedType ");  //建议证券库存表改为left join ;用证券信息表为主表  songjie
                	bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Stock_Security")).append(" s");
                	bufSql.append(" JOIN " + pub.yssGetTableName("Tb_Para_MonetaryFund") + " m ON s.fsecuritycode = m.fsecuritycode");
                	bufSql.append(" WHERE s.FCheckState = 1 AND m.FCheckState = 1");  //此处建议增加证券库存日期等于业务日期-1天  songjie
                	//BUG3590系统在计提年初节假日的基金万分收益时处理有问题  add by jiangshichao 2012.01.30 start 
                	//计提1月1号到1月3号的基金万分收益，1月1号计提的基金万分收益金额翻倍. 
                	bufSql.append(" and SUBSTR(s.FYearMonth, 5) <> '00'"); 
                	//BUG3590系统在计提年初节假日的基金万分收益时处理有问题  add by jiangshichao 2012.01.30 end
                	bufSql.append(" ) b ON a.FSecurityCode = ");
                	bufSql.append(" b.FSecuritycode) sec ");
                	bufSql.append(" LEFT JOIN (SELECT * ");
                	bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Stock_Secrecpay"));
                	/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
                	* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//                	bufSql.append(" WHERE FSecurityCode IN (" + operSql.sqlCodes(sSecCodes) + ") ");
                	bufSql.append(" WHERE " + operSql.getNumsDetail(sSecCodes,"FSecurityCode",500));
                	/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
                	bufSql.append(" AND FCheckState = 1 ");   //此处建议增加证券应收应付库存日期等于业务日期-1天  songjie
                	bufSql.append(" AND FSubTsfTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_TR_RecFundIns));
                	//edit by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A
					bufSql.append(" AND FPortCode IN (" + operSql.sqlCodes(portcodes[j]) + ") ");
                	bufSql.append(" AND " + dbl.sqlSubStr("FYearMonth", "5") + " <> '00') rec ");
                	bufSql.append(" ON rec.FSecurityCode = ");
                	bufSql.append(" sec.FSecurityCode ");
                	bufSql.append(" AND rec.FPortCode = ");
                	bufSql.append(" sec.FPortCode ");
                	bufSql.append(" AND rec.FCatType = ");
                	bufSql.append(" sec.FCatType ");
                	bufSql.append(" AND rec.FAttrClsCode = ");
                	bufSql.append(" sec.FAttrClsCode ");
                	bufSql.append(" AND rec.FInvestType = sec.FInvestType ");
                	bufSql.append(" AND rec.FStorageDate = sec.FStorageDate");
                	if (analy1) {
                		bufSql.append(" AND rec.FAnalysisCode1 = ");
                		bufSql.append(" sec.FAnalysisCode1 ");
                	}
                	if (analy2) {
                		bufSql.append(" AND rec.FAnalysisCode2 = ");
                		bufSql.append(" sec.FAnalysisCode2 ");
                	}
                	bufSql.append(" JOIN (SELECT FSecurityCode, FBargainDate, FFundRate ");
                	bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_SubMonFundIntst"));
                	//edit by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A
					//edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
                	bufSql.append(" WHERE FCheckState = 1 AND FReadDate " + strSubSql);
                	bufSql.append(" ) ins ON sec.FSecurityCode = ins.FSecurityCode");
					//edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
                	bufSql.append(sqlInsDate).append(" order by ins.FBargainDate ");
                	
                	rs = dbl.openResultSet(bufSql.toString());
                	bufSql.delete(0, bufSql.length());//执行查询后须清空bufSql以便进入下轮循环 ，MS00740 QDV4赢时胜上海2009年9月28日06_AB panjunfang modify 20100228
                	while (rs.next()) {
					    //---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
                        if(alReadDatePorts.contains(portcodes[j])){
                        	tranDate = theDate;
                        }
                        if(alBargainDatePorts.contains(portcodes[j])){
                        	tranDate = rs.getDate("FBargainDate");
                        	
                        	//---add by songjie 2011.09.13 BUG 2629 QDV4赢时胜（深圳_roy）2011年9月2日02_B start---//
                        	baseOper = this.getSettingOper();
                        	
                        	//若计提日期不等于净值日期，且计提日期为净值日期的第一个工作日，则不生成应收基金红利的证券应收应付数据
                        	if(YssFun.dateDiff(tranDate, theDate) != 0 && 
                        	   YssFun.dateDiff(baseOper.getWorkDay(rs.getString("FHolidaysCode"), rs.getDate("FBARGAINDATE"), 0), theDate) == 0){
                        		continue;
                        	}
                        	//---add by songjie 2011.09.13 BUG 2629 QDV4赢时胜（深圳_roy）2011年9月2日02_B end---//
                        	                    	
                            hashKey = YssFun.formatDate(theDate, "yyyy-MM-dd") + "\t" +
                    		rs.getString("FPortCode") + "\t" + rs.getString("FSecurityCode") + "\t"+
                            rs.getString("FCatType") + "\t" + rs.getString("FAttrClsCode") + "\t" + rs.getString("FInvestType") + "\t" +
                            (analy1 ? rs.getString("FAnalysisCode1") : "") + (analy2 ? rs.getString("FAnalysisCode2") : "");
                            
                            fbal = (hmSpRecPay.get(hashKey)== null)? 0 : Double.parseDouble((String)hmSpRecPay.get(hashKey));
                        }
                        if(!alReadDatePorts.contains(portcodes[j]) && !alBargainDatePorts.contains(portcodes[j])){
                        	tranDate = theDate;
                        }
                        //---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
                		//使用哈希表是因为读数日期是同一天业务日期不是同一天的同一只证券的万分收益只能产生一条应收记录
						//edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
                		sHashKey = YssFun.formatDate(tranDate, "yyyy-MM-dd") + "\t" +
                		rs.getString("FPortCode") + "\t" + rs.getString("FSecurityCode") + "\t"+
                        rs.getString("FCatType") + "\t" + rs.getString("FAttrClsCode") + "\t" + rs.getString("FInvestType") + "\t" +
                        (analy1 ? rs.getString("FAnalysisCode1") : "") + (analy2 ? rs.getString("FAnalysisCode2") : "");
//---delete by songjie 2011.08.25 BUG 2544 QDV4建行2011年08月24日04_B start---//                		
//                		---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
//                		若通用参数 货币基金收益计提 中 设置了 计提开始日期，则根据证券代码获取最大的申购确认日期
//                        if(alApplyConfirmDate.contains(portcodes[j])){
//                        	maxApplyConfirmDate = getMaxApplyConfirmDate(rs.getString("FSecurityCode"),false);
//                        }
//                        
//                        if(alApplyConfNextDate.contains(portcodes[j])){
//                        	maxApplyConfirmDate = getMaxApplyConfirmDate(rs.getString("FSecurityCode"),true);
//                        }
//                        
//                        if((alApplyConfirmDate.contains(portcodes[j]) || alApplyConfNextDate.contains(portcodes[j])) && 
//                        	maxApplyConfirmDate == null){
//                        	continue;
//                        }
                        
//                        //若通用参数中设置了 计提开始日期  且 计提日 < 计提开始日期  则不能计提
//                        if((alApplyConfirmDate.contains(portcodes[j]) || alApplyConfNextDate.contains(portcodes[j])) && 
//                        	YssFun.dateDiff(maxApplyConfirmDate,theDate) < 0){
//                        	continue;
//                        }
//                		//---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
//---delete by songjie 2011.08.25 BUG 2544 QDV4建行2011年08月24日04_B end---//                		
                		pay = (SecPecPayBean) hmRecPay.get(sHashKey);
                		if (pay == null) {
                			pay = new SecPecPayBean();
							//edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
                			pay.setTransDate(tranDate);
                			pay.setInvestType(rs.getString("FInvestType"));
                			pay.setStrPortCode(rs.getString("FPortCode") + "");
                			pay.setInvMgrCode(analy1 ? (rs.getString("FAnalysisCode1") + "") :
                			" ");
                			pay.setBrokerCode(analy2 ? (rs.getString("FAnalysisCode2") + "") :
                			" ");
                			pay.setStrSecurityCode(rs.getString("FSecurityCode") + "");
                			pay.setStrCuryCode(rs.getString("FCuryCode") + "");
                			pay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                			pay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_TR_RecFundIns);
                			pay.setRelaNumType("");

                			//基础汇率
							//edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
                			baseCuryRate = this.getSettingOper().getCuryRate(tranDate,
                					pay.getStrCuryCode(), pay.getStrPortCode(),
                					YssOperCons.YSS_RATE_BASE);
                			
                			//组合汇率
							//edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
                			rateOper.getInnerPortRate(tranDate, pay.getStrCuryCode(), pay.getStrPortCode());
                			portCuryRate = rateOper.getDPortRate();
                			
                			pay.setBaseCuryRate(baseCuryRate);
                			pay.setPortCuryRate(portCuryRate);
                		}
                		//复利
                		if (rs.getString("finteresttype").equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_COMPOUND)) {
                			//计息方式为复利的情况下，成本 = 库存成本 + 应收红利余额
							//---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
                			if(alBargainDatePorts.contains(portcodes[j])){
                				dbCost = YssD.add(rs.getDouble("FStorageCost"), rs.getDouble("FBal"), fbal);  
                			}else{
                				dbCost = YssD.add(rs.getDouble("FStorageCost"), rs.getDouble("FBal"));  
                			}
                			//---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
                		} else { //单利
                			//单利的情况是否需要考虑???songjie
                			if(rs.getString("FClosedType").equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_CLOSETYPE_DAY) &&
                            ((String)alCloseType.get(0)).indexOf(rs.getString("FPortCode")) != -1){
                            //如果记息方式为单利，日结型，且红利记入基金投资，则成本需要去掉已记提的红利收入
                				dbCost = YssD.sub(
                						rs.getDouble("FStorageCost"),
                						getIncomedIns(rs.getDate("FStorageDate"),
                								rs.getString("FSecurityCode"),
                								rs.getString("FPortCode"),
                								rs.getString("FAnalysisCode1"),
                								rs.getString("FInvestType")));
                			}else{
                				//计息方式为单利的情况下，成本 = 库存成本
                				dbCost = rs.getDouble("FStorageCost");
                			}
                		}
                		//收益 = 成本 * 每万份收益率 / 10000 保留两位小数
                		pay.setMoney(YssD.add(pay.getMoney(),
                				YssD.round(YssD.div(YssD.mul(dbCost,
                						rs.getDouble("FFundRate")),
                						10000),
                						2)));

                		pay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(pay.
                				getMoney(), pay.getBaseCuryRate()));
                		pay.setPortCuryMoney(this.getSettingOper().calPortMoney(pay.
                				getMoney(), pay.getBaseCuryRate(),
                				pay.getPortCuryRate(),
                				pay.getStrCuryCode(),
								//edit by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A
                				tranDate,
                				pay.getStrPortCode()));
                		
                		pay.setMMoney(pay.getMoney());
                		pay.setVMoney(pay.getMoney());
                		pay.setMBaseCuryMoney(pay.getBaseCuryMoney());
                		pay.setVBaseCuryMoney(pay.getBaseCuryMoney());
                		pay.setMPortCuryMoney(pay.getPortCuryMoney());
                		pay.setVPortCuryMoney(pay.getPortCuryMoney());
                		pay.setCheckState(1);
                		hmRecPay.put(sHashKey, pay);
                		

                		
                		//---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
                        if(alBargainDatePorts.contains(portcodes[j])){
                        	fbal += pay.getMoney();
                    		hmSpRecPay.put(hashKey, fbal + "");
                        }
						//---add by songjie 2011.08.15 需求 1491 QDV4赢时胜（招商证券）2011年08月9日01_A---//
                	}
                	dbl.closeResultSetFinal(rs);
                }
            }
        } catch (Exception ex) {
            throw new YssException("提取货币基金应收利息出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmRecPay;
    }
    private void getInterstInNewMethod(java.util.Date date,
            String SecurityCodes,String sportCodes) throws YssException {
		String sql="";
		ResultSet rs = null;
		SecPecPayBean pay = null;
		try {
			if(this.hmParams==null || this.hmParams.size()==0){
				throw new YssException("请设置货币式基金收益计提参数!");
			}
			this.secRecPayList=new ArrayList();
			//获取证券的节假日信息
			getSecurityHolidayMap(SecurityCodes);
			//计提方式
			String yesPortCodes="";//T-1
			String todayPortCodes="";//T-0
			if(this.alCompoundCalType.size()>1){
				yesPortCodes=(String) this.alCompoundCalType.get(0);
				todayPortCodes=(String) this.alCompoundCalType.get(1);
			}
			String[] arrPortCodes=sportCodes.split(",");
			for(int i=0;i<arrPortCodes.length;i++){
				//计提方式为T-1
				if(yesPortCodes.indexOf(arrPortCodes[i])>-1){
					String[] arrSecCode=SecurityCodes.split(",");
					int len=arrSecCode.length;
					for(int j=0;j<len;j++){
						String securityCode=arrSecCode[j];
						String holidayCode=(String) this.secHolidayMap.get(securityCode);
						java.util.Date workDay=this.getSettingOper().getWorkDay(holidayCode, date, 0);
						if(YssFun.dateDiff(date, workDay)!=0){//如果业务日期为节假日，则不进行计提
							continue;
						}
//						//获取公告日的万份收益率的方式，节假日暂不计提
//						if(alReadDatePorts.contains(arrPortCodes[i])){
//							setSecRecPayOfDay(securityCode, date, arrPortCodes[i], 1);
//							continue;
//						}											
						//业务日期是第二个工作日，需要区分节假日分开计提和节假日不分开计提的计提模式(复利)
						workDay=this.getSettingOper().getWorkDay(holidayCode, date, -1);//上一个工作日
						java.util.Date lastDay=YssFun.addDay(workDay, -1);//上一个自然日
						java.util.Date holidayDay=this.getSettingOper().getWorkDay(holidayCode, lastDay, 0);
						if(YssFun.dateDiff(lastDay, holidayDay)!=0){//业务日期-1工作日-1自然日 为节假日(第二个工作日)
							java.util.Date lastWorkDay=this.getSettingOper().getWorkDay(holidayCode, lastDay, -1);//节假日的前一个工作日
							setSecRecPayOfSecondWorkDay(securityCode, date, arrPortCodes[i], workDay, lastDay,lastWorkDay);
						}else{//工作日
							setSecRecPayOfWorkDay(securityCode, date, arrPortCodes[i],workDay);
						}
					}
					
					
				}
				//计提方式为T-0
				if(todayPortCodes.indexOf(arrPortCodes[i])>-1){
					String[] arrSecCode=SecurityCodes.split(",");
					int len=arrSecCode.length;
					for(int j=0;j<len;j++){
						String securityCode=arrSecCode[j];
						String holidayCode=(String) this.secHolidayMap.get(securityCode);		
//						//获取公告日的万份收益率的方式，节假日暂不计提
//						if(alReadDatePorts.contains(arrPortCodes[i])){
//							java.util.Date workDay=this.getSettingOper().getWorkDay(holidayCode, date, 0);	
//							if(YssFun.dateDiff(date, workDay)!=0){
//								continue;
//							}
//							setSecRecPayOfDay(securityCode, date, arrPortCodes[i], 0);
//							continue;
//						}	
						java.util.Date lastWorkDay=this.getSettingOper().getWorkDay(holidayCode, date, -1);//前一个工作日
						java.util.Date nextWorkDay=this.getSettingOper().getWorkDay(holidayCode, date, 1);//下一个工作日
						splitMonFundInterestTable(lastWorkDay, nextWorkDay, arrPortCodes[i], securityCode);
						String insMode=(String) this.hmParams.get(arrPortCodes[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_INSMODE);
						if("3".equals(insMode)){//节假日最后一天计提  (计提模式)
							setSecRecPayOfT0Mode3(securityCode, date, arrPortCodes[i], holidayCode)	;				
						}else if("4".equals(insMode)){//节假日后第一个工作日
							setSecRecPayOfT0Mode4(securityCode, date, arrPortCodes[i], holidayCode,lastWorkDay,nextWorkDay)	;				
						}else if("5".equals(insMode)){//在节假日期间每一天
							setSecRecPayOfT0Mode5(securityCode, date, arrPortCodes[i], holidayCode)	;				
						}								
					}
				}
			}
		} catch (Exception ex) {
			throw new YssException("提取货币基金应收利息出错！"+ex, ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    private void setSecRecPayList(ResultSet rs,double money
    		                       ,java.util.Date date) throws YssException{
    	try{
    		if(money==0){
    			return;
    		}
    		EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
    	    rateOper.setYssPub(pub);
	    	SecPecPayBean secRecPay=new SecPecPayBean();	    	
	    	secRecPay.setInvestType(rs.getString("FInvestType"));
	    	secRecPay.setTransDate(date);
	    	secRecPay.setCheckState(1);
			secRecPay.setAttrClsCode(" ");
			secRecPay.setRelaNumType("");
			secRecPay.setStrPortCode(rs.getString("FPORTCODE"));
		    secRecPay.setInvMgrCode(" ");
			secRecPay.setBrokerCode(" ");
			secRecPay.setStrSecurityCode(rs.getString("FSECURITYCODE"));
			secRecPay.setStrCuryCode(rs.getString("FCURYCODE"));
			secRecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
			secRecPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_TR_RecFundIns);
			//基础汇率
			double baseCuryRate = this.getSettingOper().getCuryRate(date,
					secRecPay.getStrCuryCode(), secRecPay.getStrPortCode(),
					YssOperCons.YSS_RATE_BASE);
			
			//组合汇率
			rateOper.getInnerPortRate(date, secRecPay.getStrCuryCode(), secRecPay.getStrPortCode());
			double portCuryRate = rateOper.getDPortRate();
			secRecPay.setMoney(money);
			secRecPay.setBaseCuryRate(baseCuryRate);
			secRecPay.setPortCuryRate(portCuryRate);
	
			secRecPay.setMMoney(secRecPay.getMoney());
			secRecPay.setVMoney(secRecPay.getMoney());
	
			secRecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(secRecPay.getMoney(),
					baseCuryRate, 2));
			secRecPay.setMBaseCuryMoney(secRecPay.getBaseCuryMoney());
			secRecPay.setVBaseCuryMoney(secRecPay.getBaseCuryMoney());
	
			secRecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(secRecPay.getMoney(),
					baseCuryRate, portCuryRate,
					rs.getString("FCURYCODE"), date, secRecPay.getStrPortCode(), 2));
			secRecPay.setMPortCuryMoney(secRecPay.getPortCuryMoney());
			secRecPay.setVPortCuryMoney(secRecPay.getPortCuryMoney());
	
			secRecPay.setPortCuryMoneyF(secRecPay.getPortCuryMoney());
			secRecPay.setBaseCuryMoneyF(secRecPay.getBaseCuryMoney());
			secRecPay.setMoneyF(secRecPay.getMoney());
			secRecPay.setInOutType(1);
			this.secRecPayList.add(secRecPay);
		}catch (Exception ex) {
			throw new YssException("证券应收应付对象赋值出错！"+ex, ex);
		}
    	
    }
    /** story 2617 add by zhouwei 20120514 计提方式为T-1，在第二个工作日获取基金红利
    */
    private double setSecRecPayOfSecondWorkDay(String securityCode,java.util.Date date,String portCode,
    									java.util.Date workDay,java.util.Date holidayDay,java.util.Date lastWorkDay) throws YssException{
    	//date 业务日期，workDay 为上一个工作日,holidayDay节假日最后一天,lastWorkDay 为节假日前的工作日
    	String sql="";
    	ResultSet rs=null;
		String insMode="";
	    double money=0;
    	try{
    		splitMonFundInterestTable(lastWorkDay, workDay, portCode, securityCode);
    		insMode=(String) this.hmParams.get(portCode + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_INSMODE);
    		//edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加 证券名称
    		sql="select D.*,sec.FSecurityName, A.FBARGAINDATE AS BARGAINDATE,A.FFUNDRATE AS FUNDRATE,B.FBARGAINDATE AS BARGAINDATE2,nvl(E.FBAL,0) as bal,"
    		   +"B.FFUNDRATE AS FUNDRATE2,C.FCLOSEDTYPE,C.FINTERESTTYPE FROM "+pub.yssGetTableName("TB_DATA_SUBMONFUNDINTST")+" A"//万份收益子表
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_DATA_MONFUNDINTEREST")+" B"//万份收益主表
    		   +" ON A.FSECURITYCODE=B.FSECURITYCODE AND B.FCHECKSTATE=1 "
    		   +" AND A.FBARGAINDATE=B.FBARGAINDATE"
    		   +" INNER JOIN "+pub.yssGetTableName("TB_PARA_MONETARYFUND")+" C ON A.FSECURITYCODE=C.FSECURITYCODE"
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECURITY")+" D"//证券成本
    		   +" ON A.FSECURITYCODE=D.FSECURITYCODE AND A.FBARGAINDATE=D.FSTORAGEDATE"
    		   +" AND D.FPORTCODE="+dbl.sqlString(portCode)
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECRECPAY")+" E"//证券应收应付库存
    		   +" ON A.FSECURITYCODE=E.FSECURITYCODE AND E.FSTORAGEDATE="+dbl.sqlDate(workDay)
    		   +" AND E.FTSFTYPECODE='06' AND E.FSUBTSFTYPECODE='06TR' AND E.FPORTCODE="+dbl.sqlString(portCode)//基金红利
    		   //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
    		   +" left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") 
    		   +" where FCheckState = 1) sec on sec.FSecurityCode = a.FSecurityCode "
    		   //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
    		   +" WHERE A.FSECURITYCODE="+dbl.sqlString(securityCode)
    		   +" AND A.FBARGAINDATE>"+dbl.sqlDate(lastWorkDay)+" and A.FBARGAINDATE<"+dbl.sqlDate(date)
    		   +" AND NVL(D.FSTORAGEAMOUNT,0)>0 ORDER BY A.FBARGAINDATE ASC"; 		
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			//单利下，节假日分开计提和不分开计提两种模式，过程一致
    			if(rs.getString("FINTERESTTYPE").equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_SIMPLE)){
    				//节假日部分
    				if(YssFun.dateDiff(rs.getDate("BARGAINDATE"), holidayDay)==0){
    					money=YssD.round(
    							YssD.div(YssD.mul(rs.getDouble("FSTORAGECOST"),rs.getDouble("FUNDRATE2")), 10000)
    							,2);
    				}
    				//第一个工作日
    				if(YssFun.dateDiff(rs.getDate("BARGAINDATE"), workDay)==0){
    					money+=YssD.round(
    							YssD.div(YssD.mul(rs.getDouble("FSTORAGECOST"),rs.getDouble("FUNDRATE2")), 10000)
    							,2);
    					setSecRecPayList(rs, money, date);
    				}
    			}else{//复利需要区分两种计提模式
    				if("1".equals(insMode)){//节假日分开计提
    					//节假日部分
            			if(YssFun.dateDiff(rs.getDate("BARGAINDATE"), holidayDay)>=0){
            				money+=YssD.round(
        							YssD.div(YssD.mul(
        									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL"),money),
        									rs.getDouble("FUNDRATE")), 10000)
        							,2);
            			}
            			//第一个工作日
            			if(YssFun.dateDiff(rs.getDate("BARGAINDATE"), workDay)==0){
            				money+=YssD.round(
        							YssD.div(YssD.mul(
        									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL"),money),
        									rs.getDouble("FUNDRATE")), 10000)
        							,2);
            				setSecRecPayList(rs, money, date);
        				}
            		}else if("2".equals(insMode)){//节假日不分开计提
    					//节假日部分
            			if(YssFun.dateDiff(rs.getDate("BARGAINDATE"), holidayDay)==0){
            				money=YssD.round(
        							YssD.div(YssD.mul(
        									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL")),
        									rs.getDouble("FUNDRATE2")), 10000)
        							,2);
            			}
            			//第一个工作日
            			if(YssFun.dateDiff(rs.getDate("BARGAINDATE"), workDay)==0){
            				money+=YssD.round(
        							YssD.div(YssD.mul(
        									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL"),money),
        									rs.getDouble("FUNDRATE")), 10000)
        							,2);
            				setSecRecPayList(rs, money, date);
        				}
            		}
    			}  			
    		}
    	}catch (Exception e) {
    		throw new YssException("获取基金红利数据出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return money;
    }
    
    //根据公告日获取万份收益率，生成基金红利
    private double setSecRecPayOfDay(String securityCode,java.util.Date date,String portCode,int i) throws YssException{
    	String sql="";
    	ResultSet rs=null;
		String insMode="";
	    double money=0;
    	try{
    		insMode=(String) this.hmParams.get(portCode + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_INSMODE);
    		sql="select D.*,B.FBARGAINDATE AS BARGAINDATE,B.FREADDATE AS FREADDATE,nvl(E.FBAL,0) as bal,"
    		   +"B.FFUNDRATE AS FUNDRATE,C.FCLOSEDTYPE,C.FINTERESTTYPE"
    		   +" FROM "+pub.yssGetTableName("TB_DATA_MONFUNDINTEREST")+" B"//万份收益主表
    		   +" INNER JOIN "+pub.yssGetTableName("TB_PARA_MONETARYFUND")+" C ON B.FSECURITYCODE=C.FSECURITYCODE"
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECURITY")+" D"//证券成本
    		   +" ON B.FSECURITYCODE=D.FSECURITYCODE AND B.FREADDATE=D.FSTORAGEDATE+"+i
    		   +" AND D.FPORTCODE="+dbl.sqlString(portCode)
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECRECPAY")+" E"//证券应收应付库存
    		   +" ON B.FSECURITYCODE=E.FSECURITYCODE AND B.FREADDATE=E.FSTORAGEDATE+"+i
    		   +" AND E.FTSFTYPECODE='06' AND E.FSUBTSFTYPECODE='06TR' AND E.FPORTCODE="+dbl.sqlString(portCode)//基金红利
    		   +" WHERE B.FSECURITYCODE="+dbl.sqlString(securityCode)
    		   +" AND B.FREADDATE="+dbl.sqlDate(date)
    		   +" AND NVL(D.FSTORAGEAMOUNT,0)>0 ORDER BY B.FREADDATE ASC"; 		
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			//单利
    			if(rs.getString("FINTERESTTYPE").equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_SIMPLE)){
					money=YssD.round(
							YssD.div(YssD.mul(rs.getDouble("FSTORAGECOST"),rs.getDouble("FUNDRATE")), 10000)
							,2);
					setSecRecPayList(rs, money, date);
				
    			}else{//复利
    				money=YssD.round(
							YssD.div(YssD.mul(
									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL")),
									rs.getDouble("FUNDRATE")), 10000)
							,2);
    				setSecRecPayList(rs, money, date);
    			}  			
    		}
    	}catch (Exception e) {
    		throw new YssException("获取基金红利数据出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return money;
    }
    /**story 2617  add by zhouwei 20120514 T-1计提方式下，获取普通工作日的基金红利数据 
    */
    private double setSecRecPayOfWorkDay(String securityCode,java.util.Date date,String portCode,java.util.Date lastWorkDay) throws YssException{
    	String sql="";
    	ResultSet rs=null;
		String insMode="";
	    double money=0;
    	try{
    		splitMonFundInterestTable(lastWorkDay,lastWorkDay,portCode, securityCode);
    		insMode=(String) this.hmParams.get(portCode + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_INSMODE);
    		//edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加 证券名称
    		sql="select D.*, sec.FSecurityName, A.FBARGAINDATE AS BARGAINDATE,A.FFUNDRATE AS FUNDRATE,B.FBARGAINDATE AS BARGAINDATE2,nvl(E.FBAL,0) as bal,"
    		   +"B.FFUNDRATE AS FUNDRATE2,C.FCLOSEDTYPE,C.FINTERESTTYPE FROM "+pub.yssGetTableName("TB_DATA_SUBMONFUNDINTST")+" A"//万份收益子表
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_DATA_MONFUNDINTEREST")+" B"//万份收益主表
    		   +" ON A.FSECURITYCODE=B.FSECURITYCODE AND B.FCHECKSTATE=1 AND B.FBARGAINDATE="+dbl.sqlDate(lastWorkDay)
    		   +" AND A.FBARGAINDATE=B.FBARGAINDATE"
    		   +" INNER JOIN "+pub.yssGetTableName("TB_PARA_MONETARYFUND")+" C ON A.FSECURITYCODE=C.FSECURITYCODE"
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECURITY")+" D"//证券成本
    		   +" ON A.FSECURITYCODE=D.FSECURITYCODE AND A.FBARGAINDATE=D.FSTORAGEDATE"
    		   +" AND D.FPORTCODE="+dbl.sqlString(portCode)
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECRECPAY")+" E"//证券应收应付库存
    		   +" ON A.FSECURITYCODE=E.FSECURITYCODE AND E.FSTORAGEDATE=A.FBARGAINDATE"
    		   +" AND E.FTSFTYPECODE='06' AND E.FSUBTSFTYPECODE='06TR' AND E.FPORTCODE="+dbl.sqlString(portCode)//基金红利
    		   //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
    		   +" left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") 
    		   +" where FCheckState = 1) sec on sec.FSecurityCode = a.FSecurityCode "
    		   //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
    		   +" WHERE A.FSECURITYCODE="+dbl.sqlString(securityCode)
    		   +" AND A.FBARGAINDATE="+dbl.sqlDate(lastWorkDay)
    		   +" AND NVL(D.FSTORAGEAMOUNT,0)>0 ORDER BY A.FBARGAINDATE ASC"; 		
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			//单利下，节假日分开计提和不分开计提两种模式，过程一致
    			if(rs.getString("FINTERESTTYPE").equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_SIMPLE)){
					money=YssD.round(
							YssD.div(YssD.mul(rs.getDouble("FSTORAGECOST"),rs.getDouble("FUNDRATE2")), 10000)
							,2);
					setSecRecPayList(rs, money, date);
				
    			}else{//复利,节假日分开计提和不分开计提两种模式，过程一致
    				if("1".equals(insMode)){//节假日分开计提
        				money=YssD.round(
    							YssD.div(YssD.mul(
    									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL")),
    									rs.getDouble("FUNDRATE2")), 10000)
    							,2);
        			
            		}else if("2".equals(insMode)){//节假日不分开计提
            			money=YssD.round(
    							YssD.div(YssD.mul(
    									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL")),
    									rs.getDouble("FUNDRATE2")), 10000)
    							,2);
            		}
    				setSecRecPayList(rs, money, date);
    			}  			
    		}
    	}catch (Exception e) {
    		throw new YssException("获取基金红利数据出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return money;
    }
    /** STORY 2617 add by zhouwei   20120514计提方式：T-0 计提模式3:节假日最后一天计提
    * @Title: setSecRecPayOfT0Mode3 
    * @Description: TODO
    * @param @param securityCode
    * @param @param date
    * @param @param portCode
    * @param @param holidayCode
    * @param @return
    * @param @throws YssException    设定文件 
    * @return double    返回类型 
    * @throws 
    */
    private double setSecRecPayOfT0Mode3(String securityCode,java.util.Date date,String portCode,String holidayCode) throws YssException{
    	String sql="";
    	ResultSet rs=null;
	    double money=0;
    	try{
			java.util.Date workDay=this.getSettingOper().getWorkDay(holidayCode, date, 0);
			//edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加证券名称
    		sql="select D.*, sec.FSecurityName, A.FBARGAINDATE AS BARGAINDATE,A.FFUNDRATE AS FUNDRATE,B.FBARGAINDATE AS BARGAINDATE2,nvl(E.FBAL,0) as bal,"
    		   +"B.FFUNDRATE AS FUNDRATE2,C.FCLOSEDTYPE,C.FINTERESTTYPE FROM "+pub.yssGetTableName("TB_DATA_SUBMONFUNDINTST")+" A"//万份收益子表
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_DATA_MONFUNDINTEREST")+" B"//万份收益主表
    		   +" ON A.FSECURITYCODE=B.FSECURITYCODE AND B.FCHECKSTATE=1 AND B.FBARGAINDATE="+dbl.sqlDate(date)
    		   +" AND A.FBARGAINDATE=B.FBARGAINDATE"
    		   +" INNER JOIN "+pub.yssGetTableName("TB_PARA_MONETARYFUND")+" C ON A.FSECURITYCODE=C.FSECURITYCODE"
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECURITY")+" D"//证券成本
    		   +" ON A.FSECURITYCODE=D.FSECURITYCODE AND A.FBARGAINDATE=D.FSTORAGEDATE"
    		   +" AND D.FPORTCODE="+dbl.sqlString(portCode)
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECRECPAY")+" E"//证券应收应付库存
    		   +" ON A.FSECURITYCODE=E.FSECURITYCODE AND E.FSTORAGEDATE=A.FBARGAINDATE"
    		   +" AND E.FTSFTYPECODE='06' AND E.FSUBTSFTYPECODE='06TR' AND E.FPORTCODE="+dbl.sqlString(portCode)//基金红利
    		   //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
    		   +" left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") 
    		   +" where FCheckState = 1) sec on sec.FSecurityCode = a.FSecurityCode "
    		   //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
    		   +" WHERE A.FSECURITYCODE="+dbl.sqlString(securityCode)
    		   +" AND A.FBARGAINDATE="+dbl.sqlDate(date)
    		   +" AND NVL(D.FSTORAGEAMOUNT,0)>0 ORDER BY A.FBARGAINDATE ASC"; 		
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
				if(YssFun.dateDiff(date, workDay)==1){//业务日期为节假日最后一天
					if(rs.getString("FINTERESTTYPE").equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_SIMPLE)){//单利
						money=YssD.round(
								YssD.div(YssD.mul(rs.getDouble("FSTORAGECOST"),rs.getDouble("FUNDRATE2")), 10000)
								,2);
					}else{//复利
						money=YssD.round(
    							YssD.div(YssD.mul(
    									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL")),
    									rs.getDouble("FUNDRATE2")), 10000)
    							,2);
					}
					setSecRecPayList(rs, money, date);
				}else if(YssFun.dateDiff(date, workDay)==0){//业务日期为工作日
					if(rs.getString("FINTERESTTYPE").equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_SIMPLE)){//单利
						money=YssD.round(
								YssD.div(YssD.mul(rs.getDouble("FSTORAGECOST"),rs.getDouble("FUNDRATE2")), 10000)
								,2);
					}else{//复利
						money=YssD.round(
    							YssD.div(YssD.mul(
    									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL")),
    									rs.getDouble("FUNDRATE2")), 10000)
    							,2);
					}
					setSecRecPayList(rs, money, date);
				}					    					
    		}
    	}catch (Exception e) {
    		throw new YssException("获取基金红利数据出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return money;
    }
    /**STORY 2617 add by zhouwei   20120514计提方式：T-0 计提模式4:节假日后第一个工作日
    * @Title: setSecRecPayOfT0Mode4 
    * @Description: TODO
    * @param @param securityCode
    * @param @param date
    * @param @param portCode
    * @param @param holidayCode
    * @param @return
    * @param @throws YssException    设定文件 
    * @return double    返回类型 
    * @throws 
    */
    private double setSecRecPayOfT0Mode4(String securityCode,java.util.Date date,String portCode,String holidayCode
    		                     ,java.util.Date lastWorkDay,java.util.Date nextWorkDay) throws YssException{
    	String sql="";
    	ResultSet rs=null;
	    double money=0;
    	try{			
    		//edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加 证券名称
    		sql="select D.*, sec.FSecurityName, A.FBARGAINDATE AS BARGAINDATE,A.FFUNDRATE AS FUNDRATE,B.FBARGAINDATE AS BARGAINDATE2,nvl(E.FBAL,0) as bal,"
    		   +"B.FFUNDRATE AS FUNDRATE2,C.FCLOSEDTYPE,C.FINTERESTTYPE FROM "+pub.yssGetTableName("TB_DATA_SUBMONFUNDINTST")+" A"//万份收益子表
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_DATA_MONFUNDINTEREST")+" B"//万份收益主表
    		   +" ON A.FSECURITYCODE=B.FSECURITYCODE AND B.FCHECKSTATE=1"
    		   +" AND A.FBARGAINDATE=B.FBARGAINDATE"
    		   +" INNER JOIN "+pub.yssGetTableName("TB_PARA_MONETARYFUND")+" C ON A.FSECURITYCODE=C.FSECURITYCODE"
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECURITY")+" D"//证券成本
    		   +" ON A.FSECURITYCODE=D.FSECURITYCODE AND A.FBARGAINDATE=D.FSTORAGEDATE"
    		   +" AND D.FPORTCODE="+dbl.sqlString(portCode)
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECRECPAY")+" E"//证券应收应付库存
    		   +" ON A.FSECURITYCODE=E.FSECURITYCODE AND E.FSTORAGEDATE=A.FBARGAINDATE"
    		   +" AND E.FTSFTYPECODE='06' AND E.FSUBTSFTYPECODE='06TR' AND E.FPORTCODE="+dbl.sqlString(portCode)//基金红利
    		   //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
    		   +" left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") 
    		   +" where FCheckState = 1) sec on sec.FSecurityCode = a.FSecurityCode "
    		   //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
    		   +" WHERE A.FSECURITYCODE="+dbl.sqlString(securityCode)
    		   +" AND A.FBARGAINDATE>"+dbl.sqlDate(lastWorkDay)+" and A.FBARGAINDATE<"+dbl.sqlDate(nextWorkDay)
    		   +" AND NVL(D.FSTORAGEAMOUNT,0)>0 ORDER BY A.FBARGAINDATE ASC"; 		
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){	
    			java.util.Date workDay=this.getSettingOper().getWorkDay(holidayCode, rs.getDate("BARGAINDATE"), 0);
				if(rs.getString("FINTERESTTYPE").equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_SIMPLE)){//单利
					if(YssFun.dateDiff(rs.getDate("BARGAINDATE"), workDay)!=0){//业务日期为节假日
						money+=YssD.round(
								YssD.div(YssD.mul(rs.getDouble("FSTORAGECOST"),rs.getDouble("FUNDRATE")), 10000)
								,2);
						continue;
					}
					//java.util.Date lastWorkDay=this.getSettingOper().getWorkDay(holidayCode, date, -1);//上一个工作日
					if(YssFun.dateDiff(lastWorkDay, date)>1){//业务日期为第一个工作日
						money+=YssD.round(
								YssD.div(YssD.mul(rs.getDouble("FSTORAGECOST"),rs.getDouble("FUNDRATE")), 10000)
								,2);
					}else{
						money=YssD.round(
								YssD.div(YssD.mul(rs.getDouble("FSTORAGECOST"),rs.getDouble("FUNDRATE")), 10000)
								,2);
					}
					setSecRecPayList(rs, money, date);
				}else{//复利
					if(YssFun.dateDiff(rs.getDate("BARGAINDATE"), workDay)!=0){//业务日期为节假日
						money+=YssD.round(
    							YssD.div(YssD.mul(
    									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL"),money),
    									rs.getDouble("FUNDRATE")), 10000)
    							,2);
						continue;
					}
					//java.util.Date lastWorkDay=this.getSettingOper().getWorkDay(holidayCode, date, -1);//上一个工作日
					if(YssFun.dateDiff(lastWorkDay, date)>1){//业务日期为第一个工作日
						money+=YssD.round(
    							YssD.div(YssD.mul(
    									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL"),money),
    									rs.getDouble("FUNDRATE")), 10000)
    							,2);
					}else{
						money=YssD.round(
    							YssD.div(YssD.mul(
    									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL")),
    									rs.getDouble("FUNDRATE")), 10000)
    							,2);
					}
					setSecRecPayList(rs, money, date);
				}
				
					
    		}
    	}catch (Exception e) {
    		throw new YssException("获取基金红利数据出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return money;
    }
    /** STORY 2617 add by zhouwei   20120514计提方式：T-0 计提模式5:在节假日期间每一天
    * @Title: setSecRecPayOfT0Mode5 
    * @Description: TODO
    * @param @param securityCode
    * @param @param date
    * @param @param portCode
    * @param @param holidayCode
    * @param @return
    * @param @throws YssException    设定文件 
    * @return double    返回类型 
    * @throws 
    */
    private double setSecRecPayOfT0Mode5(String securityCode,java.util.Date date,String portCode,String holidayCode) throws YssException{
    	String sql="";
    	ResultSet rs=null;
	    double money=0;
	    double sumMoney=0;
    	try{
    		//edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A
    		sql="select D.*, sec.FSecurityName, A.FBARGAINDATE AS BARGAINDATE,A.FFUNDRATE AS FUNDRATE,B.FBARGAINDATE AS BARGAINDATE2,nvl(E.FBAL,0) as bal,"
    		   +"B.FFUNDRATE AS FUNDRATE2,C.FCLOSEDTYPE,C.FINTERESTTYPE FROM "+pub.yssGetTableName("TB_DATA_SUBMONFUNDINTST")+" A"//万份收益子表
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_DATA_MONFUNDINTEREST")+" B"//万份收益主表
    		   +" ON A.FSECURITYCODE=B.FSECURITYCODE AND B.FCHECKSTATE=1 AND B.FBARGAINDATE="+dbl.sqlDate(date)
    		   +" AND A.FBARGAINDATE=B.FBARGAINDATE"
    		   +" INNER JOIN "+pub.yssGetTableName("TB_PARA_MONETARYFUND")+" C ON A.FSECURITYCODE=C.FSECURITYCODE"
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECURITY")+" D"//证券成本
    		   +" ON A.FSECURITYCODE=D.FSECURITYCODE AND A.FBARGAINDATE=D.FSTORAGEDATE"
    		   +" AND D.FPORTCODE="+dbl.sqlString(portCode)
    		   +" LEFT JOIN "+pub.yssGetTableName("TB_STOCK_SECRECPAY")+" E"//证券应收应付库存
    		   +" ON A.FSECURITYCODE=E.FSECURITYCODE AND E.FSTORAGEDATE=A.FBARGAINDATE"
    		   +" AND E.FTSFTYPECODE='06' AND E.FSUBTSFTYPECODE='06TR' AND E.FPORTCODE="+dbl.sqlString(portCode)//基金红利
    		   //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
    		   +" left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") 
    		   +" where FCheckState = 1) sec on sec.FSecurityCode = a.FSecurityCode "
    		   //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
    		   +" WHERE A.FSECURITYCODE="+dbl.sqlString(securityCode)
    		   +" AND A.FBARGAINDATE="+dbl.sqlDate(date)
    		   +" AND NVL(D.FSTORAGEAMOUNT,0)>0 ORDER BY A.FBARGAINDATE ASC"; 		
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
				if(rs.getString("FINTERESTTYPE").equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_SIMPLE)){//单利
					money=YssD.round(
							YssD.div(YssD.mul(rs.getDouble("FSTORAGECOST"),rs.getDouble("FUNDRATE")), 10000)
							,2);
				}else{//复利
					money=YssD.round(
							YssD.div(YssD.mul(
									YssD.add(rs.getDouble("FSTORAGECOST"),rs.getDouble("BAL"),sumMoney),
									rs.getDouble("FUNDRATE")), 10000)
							,2);
					sumMoney+=money;
				}
				setSecRecPayList(rs, money, date);
    		}
    	}catch (Exception e) {
    		throw new YssException("获取基金红利数据出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return money;
    }
    /** add by zhouwei 20120514 获取证券的节假日代码 （对应关系）
    * @Title: getSecurityHolidayMap 
    * @Description: TODO
    * @param @param SecurityCodes
    * @param @return
    * @param @throws YssException    设定文件 
    * @return HashMap    返回类型 
    * @throws 
    */
    private void getSecurityHolidayMap(String SecurityCodes) throws YssException{
    	String sql="";
    	ResultSet rs=null;
    	try{
    		this.secHolidayMap=new HashMap();
    		sql="select FSECURITYCODE,FHOLIDAYSCODE,FTRADECURY from "+pub.yssGetTableName("TB_PARA_SECURITY")
    		   +" where FSECURITYCODE in ("+operSql.sqlCodes(SecurityCodes)+")";
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			if(!secHolidayMap.containsKey(rs.getString("FSECURITYCODE"))){
    				secHolidayMap.put(rs.getString("FSECURITYCODE"), 
    						rs.getString("FHOLIDAYSCODE")); 
    			}
    		}
    	}catch (Exception e) {
			throw new YssException("获取证券的节假日群信息出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    }
//---delete by songjie 2011.08.25 BUG 2544 QDV4建行2011年08月24日04_B start---//
//    /**
//     * 在开放式基金业务中获取相关证券的申购确认日期
//	 * add by songjie 2011.08.09 
//	 * 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A
//     * @param securityCode
//     * @return
//     * @throws YssException
//     */
//    private java.util.Date getMaxApplyConfirmDate(String securityCode, boolean nextDay) throws YssException{
//        String strSql = "";
//        ResultSet rs = null;
//        java.util.Date maxConfirmDate = null;
//        String holidayCode = "";
//        try{
//        	strSql = " select a.*, b.FHolidaysCode as FHolidaysCode from (select max(FComfDate) as FComfDate, FSecurityCode from " +
//        		pub.yssGetTableName("Tb_Data_OpenFundTrade") + " where FSecurityCode = " + dbl.sqlString(securityCode) + 
//        		" and FCheckState = 1 and FDataType = 'confirm' group by FSecurityCode) a left join (select * from " +
//        		pub.yssGetTableName("Tb_Para_Security") + " where FCheckState = 1) b on a.FSecurityCode = b.FSecuritycode ";
//        	rs = dbl.openResultSet(strSql);
//        	while(rs.next()){
//        		maxConfirmDate = rs.getDate("FComfDate");//最大的申购确认日
//        		holidayCode = rs.getString("FHolidaysCode");//节假日群代码
//        	}
//        	
//        	if(maxConfirmDate != null && nextDay){
//        		maxConfirmDate = this.getSettingOper().getWorkDay(holidayCode, maxConfirmDate, 1);
//        	}
//        	
//        	return maxConfirmDate;
//        }catch(Exception e){
//        	throw new YssException("获取最大的申购确认日！", e);
//        }finally{
//        	dbl.closeResultSetFinal(rs);
//        }
//    }
//---delete by songjie 2011.08.25 BUG 2544 QDV4建行2011年08月24日04_B end---//
    
    /**
     * 获取已记提的利息
     * @param theDate Date：计息日期
     * @param sSecurityCode String：证券代码
     * @param sPortCode String：组合代码
     * @param sAnalysisCode1 String：分析代码
     * @param sInvestType String：投资类型
     * @return double
     * @throws YssException
     */
    private double getIncomedIns(java.util.Date theDate,
                                 String sSecurityCode,
                                 String sPortCode,
                                 String sAnalysisCode1,
                                 String sInvestType) throws YssException{
        String strSql = "";
        ResultSet rs = null;
        double dbIns = 0;
        try {
            for(int i = 0;; i++){
                strSql = "SELECT b.FMoney" +
                    " FROM (SELECT * FROM " + pub.yssGetTableName("Tb_Stock_Security") +
                    " WHERE FCheckState = 1" +
                    " AND FStorageDate = " + dbl.sqlDate(theDate) +
                    " AND FPortCode = " + dbl.sqlString(sPortCode) +
                    " AND FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                    (analy1 ? (" AND FAnalysisCode1 = " + dbl.sqlString(sAnalysisCode1)) : "") +
                    " AND FInvestType = " + dbl.sqlString(sInvestType) + ") a" +
                    " LEFT JOIN (SELECT *" +
                    " FROM " + pub.yssGetTableName("Tb_Data_Secrecpay") +
                    " WHERE FCheckState = 1" +
                    " AND FSubTsfTypeCode = '06TR'" +
                    " AND FInOut = 1" +
                    ") b ON a.FSecurityCode = b.FSecurityCode" +
                    " AND a.FPortCode = b.FPortCode" +
                    " AND a.FStorageDate = b.FTransDate" +
                    " AND a.FAttrClsCode = b.FAttrClsCode" +
                    (analy1 ? " AND a.FAnalysisCode1 = b.FAnalysisCode1" : "");
                rs = dbl.openResultSet(strSql);
                if(rs.next()){
                    dbIns += rs.getDouble("FMoney");
                } else {
                    //如果没有库存了就不再查询
                    break;
                }
                dbl.closeResultSetFinal(rs);
                theDate = YssFun.addDay(theDate, -1);
            }
        } catch (Exception ex) {
            throw new YssException("获取已记提的利息出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
        return dbIns;
    }
}
