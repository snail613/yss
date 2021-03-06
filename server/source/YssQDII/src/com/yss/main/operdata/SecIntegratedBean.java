package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.cashmanage.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.operdeal.BaseAvgCostCalculate;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.stgstat.*; //QDV4中保2010年4月14日02_B MS01092 by leeyu 20100419
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.storagemanage.SecRecPayBalBean;
import java.util.HashMap;
import java.util.ArrayList;
import com.yss.main.storagemanage.SecurityStorageBean;
/**
 * 综合业务
 * <p>Title:证券兑换BEAN </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech </p>
 *
 * <p>
 * 修改记录:<br>
 * 1.添加属性 attrClsName：属性分类名称、investType：投资类型
 *  MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A wangzuochun@Modify 2009-08-14
 * 2.删除一些声明了但是未使用的变量，节省系统资源 sunkey@Delete 20090906 集成删除
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SecIntegratedBean
    extends BaseDataSettingBean implements
    IDataSetting {
    private int iInOutType = 1;             //流入流出标志
    private String sInOutTypeName = "";//兑换方向
    private String sNum = "";//自动编号
    private String sSubNum = "";//交易子编号
    private String sSecurityCode = "";//证券代码
    private String sSecurityName = "";//证券名称
    private String sExchangeDate = "9998-12-31";//兑换日期
    private String sOperDate = "9998-12-31";//业务日期
    private String sTradeTypeCode = "";//交易类型代码
    private String sTradeTypeName = "";//交易类型名称

    //======MS00007 优化项目 add by songjie 2009-03-13=====
    private String sBusinessTypeCode = "";  //业务设置代码
    private String sBusinessTypeName = "";  //业务设置名称
    private String sTsfTypeCode = "";       //调拨类型代码
    private String sTsfTypeName = "";       //调拨类型名称
    private String sSubTsfTypeCode = "";    //调拨子类型代码
    private String sSubTsfTypeName = "";    //调拨子类型名称
    //================End MS00007 ========================

    private String sRelaNum = "";//关联编号
    private String sNumType = "";//编号类型
    private String sNumTypeName = "";//编号类型名称
    private String sPortCode = "";//组合代码
    private String sPortName = "";//组合名称
    private String sAnalysisCode1 = "";//分析代码1
    private String sAnalysisCode2 = "";//分析代码2
    private String sAnalysisCode3 = "";//分析代码3
    private String sAnalysisName1 = "";
    private String sAnalysisName2 = "";
    private String sAnalysisName3 = "";

    private String attrClsCode = "";        //属性分类代码
    private String attrClsName = "" ;       //属性分类名称
    private String investType = "";         //投资类型

    private String sDesc = "";              //针对整笔的描述
    private String sSecExDesc = "";         //针对证券成本兑换的描述
//    private String sisOnlyColumnss = "0";
    private double dAmount = 0;//兑换数量
    private double dCost = 0;//成本
    private double dMCost = 0;//管理成本
    private double dVCost = 0;//估值成本
    private double dBaseCost = 0;//基础成本
    private double dMBaseCost = 0;//基础管理成本
    private double dVBaseCost = 0;//基础估值成本
    private double dPortCost = 0;//组合成本
    private double dMPortCost = 0;//组合管理成本
    private double dVPortCost = 0;//组合估值成本
    private double dBaseCuryRate = 0;//基础汇率
    private double dPortCuryRate = 0;//组合汇率

    private String sOldNum = "";//旧编号
    private String sOldSubNum = "";//旧关联编号
    private SecIntegratedBean filterType = null;

    private String sSecStr = ""; //证券字符串
    private String sSecRecPayStr = ""; //证券应收应付
    private String sCashStr = ""; //现金字符串
    private String sCashRecPayStr = ""; //现金应收应付
    private String sInvestRecPayStr = ""; //运营应收应付 MS00007 add by songjie 2009-03-13
    private String sRecycled = ""; //保存未解析前的字符串

    //===add by xuxuming,20090914.MS00700,债券应收和转货基本数据要相应，不用重新输入,QDV4中保2009年09月15日02_B.业务类型为内部转货（'81'）时，需要查出其对应的应收应付信息将其保存到应收应付表并关联=====
    private StringBuffer tempBuf = new StringBuffer(); //保存从证券应收应付库存查询到的证券信息，add by xuxuming,20090914
    private ArrayList arraySecIn = new ArrayList(); //用于保存业务类型为内部转货（'81'）的流入
    private HashMap hmSecPec = new HashMap(); //用于保存当前取出来的证券应收应付
    private boolean bFlag = false; //用于标记是否有业务类型为内部转货（'81'）的流出
	private SingleLogOper logOper;
    //====end===================================================================================================
    private String errMessage = "";//add by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B
    
    /**shashijie 2011-08-16 STORY 1202*/
    private double FApieceCash = 0;//每股对价金额
    private String FCashAccCode = "";//现金账户代码
    private String FMatureDate = "9998-12-31";//对价资金到帐日
    private double FAltogetherCash = 0;//对价总金额
    private String FCashAccName = "";//现金账户名称
    /**end*/
    
    private String FDataOrigin = "";//数据类型 1：手动输入 0：自动生成  add by zhangjun 2012.07.03 BUG4862业务处理后，综合业务下手工录入一笔交易类型为87的证券更名业务数据被清除 
    
    private boolean bool = false;  //add by zhangjun 2012.06.11 BUG4752在“回收站”中成功清除数据后，已审核”、“未审核”界面中的数据都显示在“回收站”中了 
    
    public SecIntegratedBean() {
    }

    /**
     * getListViewData1 前台子界面的单条显示,
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";

        //==== MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A ====
        String sVocStr = "";    //词汇类型对照字符串
        //============= 2009-08-14 add by wangzuochun ====================

        String sqlStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String sAry[] = null;
        try {
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1",
                "Security"); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2",
                "Security");
            boolean analy3 = operSql.storageAnalysis("FAnalysisCode2",
                "Security");
            sAry = this.operSql.storageAnalysisSql(YssOperCons.
                YSS_KCLX_Security); //获得分析代码
            sHeader = this.getListView1Headers();
			//2.增加属性分类名称的获取 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A 2009-08-14 modify by wangzuochun
            sqlStr = "select distinct k.FCashAccName , a.*, case when a.FInOuttype=1 then '流入' else '流出' " +
            		" end as FInOutTypeName, " +//edit by xuxuming,MS00831 20091203.加上distinct,避免查询重复数据
                //MS00007 delete by songjie 2009-03-13
                //" d.Ftradetypename as FTradeTypeName,e.FPortName as FPortName,f.FUserName as FCreatorName," +
                " s.FTsfTypeName,m.FSubTsfTypeName,d.FBusinessTypeName as FTradeTypeName," + //MS00007 add by songjie 2009-03-13  交易类型已经改为业务类型
                "e.FPortName as FPortName,f.FUserName as FCreatorName," + //MS00007 add by songjie 2009-03-13 原先的一行代码拆为两行代码
                " g.FUserName as FCheckUserName,h.FSecurityName as FSecurityName,j.FAttrClsName as FAttrClsName  ";
            sqlStr += (storageAnalysis().length() == 0 ?
                       ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                       ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +

                " from " + pub.yssGetTableName("tb_data_Integrated") +
                " a " +
                storageAnalysis() +
                //" left join Tb_Base_Tradetype d on a.Ftradetypecode = d.Ftradetypecode " +//MS00007 delete by songjie 2009-03-13 交易类型已经改为业务类型
                " left join Tb_Base_BusinessSet d on a.FTradeTypeCode = d.FBusinessTypeCode " + //MS00007 add by songjie 2009-03-13 交易类型已经改为业务类型
                " left join " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " e on a.Fportcode = e.Fportcode " +
                //--MS00007 add by songjie 2009-03-13
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) s on a.FTsfTypeCode = s.FTsfTypeCode " + //因为界面设置了调拨类型，所以要链接调拨类型表
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) m on m.FSubTsfTypeCode = a.FSubTsfTypeCode" + //因为界面设置了调拨子类型，所以要链接调拨子类型表
                //--MS00007 add by songjie 2009-03-13
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) f on a.FCreator = f.FUserCode " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) g on a.FCheckUser = g.FUserCode " +
                " left join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_para_security") +
                " ) h on a.FSecurityCode=h.FSecurityCode " +
                " left join (SELECT FVocCode, FVocName from Tb_Fun_Vocabulary WHERE FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_InvestType) + ") v1 ON v1.FVocCode = a.FInvestType" +
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +
                /**shashijie 2011-08-30 STORY 1202*/
                " Left Join (select FCashAccCode,FCashAccName From "+pub.yssGetTableName("tb_Para_CashAccount")+
                " ) k on a.FCashAccCode = k.FCashAccCode "+
                /**end*/
                buildSql() +
                " order by FNum ,FSubNum";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setResult(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            dbl.closeResultSetFinal(rs);

            //=添加词汇处理 MS00024:QDV4.1赢时胜（上海）2009年4月20日24_A 交易数据拆分=
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
            //============== End MS00024 2009-08-14 add by wangzuochun ===========


        } catch (Exception e) {
            throw new YssException("解析证券兑换数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //添加关闭结果集方法 by leeyu 2009-01-08
        }

    }

    /**
     * 前台主界面的加载显示
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "", sShowCol = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sqlStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String sAry[] = null;
        try {
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1",
                "Security"); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2",
                "Security");
            boolean analy3 = operSql.storageAnalysis("FAnalysisCode2",
                "Security");
            sAry = this.operSql.storageAnalysisSql(YssOperCons.
                    YSS_KCLX_Security); //获得分析代码
            sHeader = "编号\t操作日期\t业务类型代码\t业务类型名称\t描述信息\t制作人\t制作时间";//xuqiji 20090417  MS00386 综合业务针对兑换日期的几处修改
            sShowCol =
                    "FNum\tFExchangeDate\tFTradeTypeCode\tFTradeTypeName\tFDesc\tFCreatorName\tfcreatetime";//MS01640 QDV4太平2010年08月23日03_A  add by jiangshichao 2010.08.31 增加“制作人”,“制作时间”信息
            //以下为修改综合业务的显示的加载,修改了原不加证券成本就不能加载证券应收应付其他几项的不足 by leeyu 080512
            sqlStr = //" select distinct(a.FNum) as FNum,t.FTradeTypeCode,a.FDesc,d.FExchangeDate,a.FcheckState,e.Ftradetypename from " + //MS00007 delete by songjie 2009-03-13
                " select distinct(a.FNum) as FNum,t.FTradeTypeCode,a.FDesc,d.FExchangeDate," + //MS00007 add by songjie 2009-03-13 交易类型已经改为业务类型
                    "a.FcheckState,e.FBusinessTypeName as FTradeTypeName," +//MS00007 add by songjie 2009-03-13 交易类型已经改为业务类型
                  //MS01640 QDV4太平2010年08月23日03_A  add by jiangshichao 2010.08.31 增加“制作人”,“制作时间”信息--------
                    " nvl(g.FUserName,' ') as FCreatorName, "+ //如果用户不存在则直接赋空值，发现太平资产用户名为”lc“被删除，导致查看lc 建的历史数据会报错

                 //modify by zhangfa 20100930 MS01803    综合业务模块的权限控制有问题    QDV4汇添富2010年09月14日01_B        
                    "a.FCreator,"+
                 //---------------------------------------------------------------------------------------------    
                 
                  //MS01640 QDV4太平2010年08月23日03_A  add by jiangshichao 2010.08.31 end ------------------------------
                " case when f.fportcode is null then ' '  else f.fportcode end as fportcode from " + //MS01551 修改组合代码的获取方式，避免因组合原因导致界面上显示两条数据 panjunfang modify 20100805
                pub.yssGetTableName("Tb_Data_Integrated") + " a " +    ////MS00922.add by xuxuming,2010.01.11.将组合代码也要传给前台，因为综合业务界面是组合权限，需以组合代码来验证权限
                " left join (select FNum, case when FTradeTypeCode<>' ' then FTradeTypeCode end as FTradeTypeCode from " +
                pub.yssGetTableName("Tb_Data_Integrated") +
                " where FTradeTypeCode<>' ') t " +
                " on a.FNum= t.FNum " +
                " left join (select FNum,max(case when FExchangeDate<>" +
                dbl.sqlDate("9998-12-31") +
                " then FExchangeDate end) as FExchangeDate from " +
                pub.yssGetTableName("Tb_Data_Integrated") +
                " where FExchangeDate<>" + dbl.sqlDate("9998-12-31") +
                " group by FNum) d " +
                " on a.FNum = d.FNum " +
                " left join (select FBusinessTypeCode,FBusinessTypeName from Tb_Base_BusinessSet) e on t.Ftradetypecode = e.Fbusinesstypecode"+//MS00007 add by songjie 2009-03-13 交易类型已经改为业务类型
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) g on a.FCreator = g.FUserCode "+//MS01640 QDV4太平2010年08月23日03_A  add by jiangshichao 2010.08.31 增加“制作人”,“制作时间”信息
                " left join (select fportcode,FNum from " + pub.yssGetTableName("Tb_Data_Integrated") + " where fportcode <> ' ' ) f on f.FNum= a.FNum "+////MS01551 修改组合代码的获取方式，避免因组合原因导致界面上显示两条数据 panjunfang modify 20100805
                //MS01838  add by jiangshichao 2010.10.14 增加“制作人”,“制作时间”信息
                " left join (select fnum,fcreatetime from " + pub.yssGetTableName("Tb_Data_Integrated") + " where fportcode<>' ' and FExchangeDate = "+dbl.sqlDate(filterType.sExchangeDate)+" and finouttype=1 ) h on h.FNum= a.FNum ";
               

            if (filterType != null) {
                if (filterType.isOnlyColumns.equalsIgnoreCase("0")) {
                    sqlStr += " where 1=1 ";
                    if (!filterType.sExchangeDate.equals("9998-12-31")) {
                        sqlStr += " and d.FExchangeDate =" +
                            dbl.sqlDate(filterType.sExchangeDate); //获取正确的筛选日期。
                    }
                    if ( (filterType.sTradeTypeCode != null) &&
                        (filterType.sTradeTypeCode.length() > 0 &&
                         !filterType.sTradeTypeCode.equalsIgnoreCase("null"))) { //为了在有筛选条件下，没有证券成本时能正确显示。
                        sqlStr += " and a.FTradeTypeCode like '" +
                            filterType.sTradeTypeCode.replaceAll("'", "''") +
                            "%'";
                    }

                    if (filterType.sSecurityCode != null &&
                        filterType.sSecurityCode.trim().length() > 0) { //增加证券代码的查询条件 MS000123 by leeyu 2008-12-26

                        //----MS00172 QDV4赢时胜上海2009年1月7日06_B.doc  ---------------
                        sqlStr += " and a.FSecurityCode = " +
                            dbl.sqlString(filterType.sSecurityCode); //不再使用模糊查询
                        //-------------------------------------------------------------
                    }
                    if (filterType.sPortCode != null &&
                        filterType.sPortCode.trim().length() > 0) { //增加组合代码的查询条件 MS000123 by leeyu 2008-12-26
                        //----MS00172 QDV4赢时胜上海2009年1月7日06_B.doc  -------------------------------------------
                        sqlStr += " and a.FPortCode = " +
                            dbl.sqlString(filterType.sPortCode); //不再使用模糊查询
                        //-----------------------------------------------------------------------------------------
                    }//else{//组合代码不能为空
                    	//sqlStr += " and a.FPortCode <>' ' ";//2010.01.12.组合代码为空的记录是关联到应收应付表的数据，不用在主ListView中显示
                    //}
                    //--MS00330 add by songjie 2009.03.23
                    if (filterType.sNum != null && filterType.sNum.trim().length() > 0) { //增加编码的查询条件
                        sqlStr += " and a.FNum = " + dbl.sqlString(filterType.sNum);
                    }

                    if (filterType.sDesc != null && filterType.sDesc.trim().length() > 0) { //增加描述的查询条件
                        sqlStr += " and a.FDesc = " + dbl.sqlString(filterType.sDesc);
                    }
                    //--MS00330 add by songjie 2009.03.23
                //---modify by zhangjun 2012.06.06BUG4728综合业务界面新建一笔数据并保存后，系统没有自动加载该记录 
                //} else {
                    //sqlStr += " where 1=2 ";
                //---modify by zhangjun 2012.06.06BUG4728综合业务界面新建一笔数据并保存后，系统没有自动加载该记录 
                }
            }
            sqlStr += " order by a.FNum ";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FNum")).append("\t");
                bufShow.append(rs.getDate("FExchangeDate")).append("\t");
                bufShow.append(rs.getString("FTradeTypeCode")).append("\t");
                bufShow.append(rs.getString("FTradeTypeName")).append("\t");
                bufShow.append(rs.getString("FDesc")).append("\t");
                //--- MS01640 QDV4太平2010年08月23日03_A  add by jiangshichao 2010.08.31 增加“制作人”,“制作时间”信息------//
                bufShow.append(rs.getString("FCreatorName")).append("\t");
                bufShow.append(getCreateTime(rs.getString("FNum"))).append(YssCons.YSS_LINESPLITMARK);
                //--- MS01640 QDV4太平2010年08月23日03_A  add by jiangshichao 2010.08.31 end ---------------------------//
                this.sNum = rs.getString("FNum");
                this.sSubNum = " ";
                this.iInOutType = 1;
                this.sInOutTypeName = " ";
                this.sSecurityCode = " ";
                this.sSecurityName = " ";
                if (rs.getDate("FExchangeDate") != null) {
                    this.sExchangeDate = YssFun.formatDate(rs.getDate(
                        "FExchangeDate"), "yyyy-MM-dd");
                }
                this.sRelaNum = " ";
                this.sNumType = " ";
                this.sTradeTypeCode = rs.getString("FTradeTypeCode");
                this.sTradeTypeName = rs.getString("FTradeTypeName");
//                this.sPortCode = " ";//delete by xuxuming,2010.01.11.
                this.sPortCode=rs.getString("FPortCode");//MS00922.add by xuxuming,2010.01.11.将组合代码也要传给前台，因为综合业务界面是组合权限，需以组合代码来验证权限
                this.sPortName = " ";
                this.sAnalysisCode1 = " ";
                this.sAnalysisName1 = " ";
                this.sAnalysisCode2 = " ";
                this.sAnalysisName2 = " ";
                this.sAnalysisCode3 = " ";
                this.dAmount = 0;
                this.dCost = 0;
                this.dMCost = 0;
                this.dVCost = 0;
                this.dPortCost = 0;
                this.dMPortCost = 0;
                this.dVPortCost = 0;
                this.dBaseCost = 0;
                this.dMBaseCost = 0;
                this.dVBaseCost = 0;
                this.dBaseCuryRate = 0;
                this.dPortCuryRate = 0;
                /**shashijie 2011-08-16 STORY 1202*/
                this.FAltogetherCash = 0;
                this.FApieceCash = 0;
                this.FCashAccCode = " ";
                /*if (rs.getDate("FMatureDate") != null) {
                    this.FMatureDate = YssFun.formatDate(rs.getDate(
                        "FMatureDate"), "yyyy-MM-dd");
                }*/
                this.FCashAccName = " ";
                /**end*/
                this.sDesc = rs.getString("FDesc");
                //--- MS01640 QDV4太平2010年08月23日03_A  add by jiangshichao 2010.08.31 增加“制作人”,“制作时间”信息------//
                //modify by zhangfa 20100930 MS01803    综合业务模块的权限控制有问题    QDV4汇添富2010年09月14日01_B        
                this.creatorCode=rs.getString("FCreator");
                //-------------------------------------------------------------------------------------------------------
                this.creatorName = rs.getString("FCreatorName");//发现用户被删除查询报错
                this.creatorTime = getCreateTime(rs.getString("FNum"));
               //--- MS01640 QDV4太平2010年08月23日03_A  add by jiangshichao 2010.08.31 end ---------------------------//
                checkStateId = rs.getInt("FCheckState");

                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            rs.close();
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                sShowCol;
        } catch (Exception e) {
            throw new YssException("解析证券兑换数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //添加关闭结果集方法 by leeyu 2009-01-08
        }
    }

    private String getCreateTime(String sCreateTime) throws YssException{
    	
    	String sql = "";
    	String createTime= "";
    	ResultSet rs = null;
    	try{
    		sql = " select fcreatetime from " + pub.yssGetTableName("Tb_Data_Integrated") + " where fnum ="+dbl.sqlString(sCreateTime);
    		rs = dbl.openResultSet(sql);
    		if(rs.next()){
    			createTime = rs.getString("fcreatetime");
    		}
    		return createTime;
    	}catch(Exception e){
    		throw new YssException("获取创建时间出错");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        return "";
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /**
     * add 新增方法
     *
     * @return String
     */
    public String addSetting() throws YssException {

        return "";
    }

    /**
     * checkInput插入记录到数据库前的检查 方法
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_Integrated"),
                               "FNum,FSubNum",
                               this.sNum + "," + this.sSubNum,
                               this.sOldNum + "," + this.sOldSubNum);

    }

    /**
     * 修改时间：2008年5月19号
     * 修改人：单亮
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String sSecRPNums = "", sCashNums = "", sCashRPNums = "";
        String sInvestRPNums = ""; //MS00007 add by songjie 2009.03.19 声明运营应收应付编号
        ResultSet rs = null;
        Connection conn = null;
        String[] arrData = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            bTrans = true;
            conn.setAutoCommit(false);

            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( (!sRecycled.equalsIgnoreCase("")) && sRecycled != null) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "select FRelaNum,FNumType from " +
                        pub.yssGetTableName("Tb_Data_Integrated") +
                        " where FRelaNum <>' ' and FNum=" +
                        dbl.sqlString(sNum);
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        if (rs.getString("FNumType").equalsIgnoreCase("Cash")) {
                            sCashNums += rs.getString("FRelaNum");
                            sCashNums += ",";
                        } else if (rs.getString("FNumType").equalsIgnoreCase(
                            "SecRecPay")) {
                            sSecRPNums += rs.getString("FRelaNum");
                            sSecRPNums += ",";
                        } else if (rs.getString("FNumType").equalsIgnoreCase(
                            "CashRecPay")) {
                            sCashRPNums += rs.getString("FRelaNum");
                            sCashRPNums += ",";
                        }
                        else if (rs.getString("FNumType").equalsIgnoreCase("InvestRecPay")) {
                            sInvestRPNums += rs.getString("FReLaNum"); //获取运营应收应付编号
                            sInvestRPNums += ",";
                        }
                    }
                    strSql = " update " +
                        pub.yssGetTableName("Tb_Data_Integrated") +
                        " set FCheckState=" + this.checkStateId +
                        ",FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum =" + dbl.sqlString(sNum);
                    dbl.executeSql(strSql);
                }
            }
            this.changeCheckID(sSecRPNums, sCashNums, sCashRPNums, sInvestRPNums,
                               this.checkStateId); //MS00007 edit by songjie 2009.03.19 多加了一个运营应收应付编号的参数
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            if (rs != null)
            {
            	rs.close();
            }
        } catch (Exception e) {
            throw new YssException("审核数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //添加关闭结果集方法 by leeyu 2009-01-08
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting 删除 方法
     */
    public void delSetting() throws YssException {
        String strSql = "";
        String sSecRPNums = "", sCashNums = "", sCashRPNums = "";
        String sInvestRPNums = ""; //MS00007 add by songjie 2009-03-19 运营应收应付编号
        ResultSet rs = null;
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            bTrans = true;
            conn.setAutoCommit(false);
            strSql = "select FRelaNum,FNumType from " +
                pub.yssGetTableName("Tb_Data_Integrated") +
                " where FRelaNum <>' ' and FNum=" + dbl.sqlString(sNum);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FNumType").equalsIgnoreCase("Cash")) {
                    sCashNums += rs.getString("FRelaNum");
                    sCashNums += ",";
                } else if (rs.getString("FNumType").equalsIgnoreCase(
                    "SecRecPay")) {
                    sSecRPNums += rs.getString("FRelaNum");
                    sSecRPNums += ",";
                } else if (rs.getString("FNumType").equalsIgnoreCase(
                    "CashRecPay")) {
                    sCashRPNums += rs.getString("FRelaNum");
                    sCashRPNums += ",";
                }
                else if (rs.getString("FNumType").equalsIgnoreCase("InvestRecPay")) {
                    sInvestRPNums += rs.getString("FRelaNum"); //获取运营应收应付编号
                    sInvestRPNums += ",";
                }
            }
            strSql = " update " + pub.yssGetTableName("Tb_Data_Integrated") +
                " set FCheckState=" + this.checkStateId + ",FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum =" + dbl.sqlString(sNum);
            dbl.executeSql(strSql);
            this.changeCheckID(sSecRPNums, sCashNums, sCashRPNums, sInvestRPNums,
                               this.checkStateId);
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            rs.close();
        } catch (Exception e) {
            throw new YssException("删除数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //添加关闭结果集方法 by leeyu 2009-01-08
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 2008-5-19 单亮
     * 删除回收站的数据，即彻底从数据库删除数据
     */

    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
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
                        pub.yssGetTableName("Tb_Data_Integrated") +
                        " where FNum =" + dbl.sqlString(sNum);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else if (sNum != "" && sNum != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_Integrated") +
                    " where FNum = " + dbl.sqlString(this.sNum);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            bool = true;  ////modify by zhangjun 2012.06.11 BUG4752在“回收站”中成功清除数据后，已审核”、“未审核”界面中的数据都显示在“回收站”中了 
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * editSetting 修改 方法
     *
     * @return String
     */
    public String editSetting() throws YssException {
        return "";
    }

    /**
     * getAllSetting 获取一条全记录的 方法
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getSetting 通过筛选条件获取符合条件的综合业务的信息（当前主要是编号）
     * 调用此方法为了将编号保存到sOldNum中.
     * by xuxuming,20090831 MS00473,QDV4国泰2009年6月01日01_A
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "SELECT FNum FROM " + pub.yssGetTableName("Tb_Data_Integrated") + " a "
                + buildFilterSql();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.sOldNum = rs.getString("FNum") + "";
            }
        } catch (Exception e) {
            throw new YssException("获取综合业务信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return this;
    }

    /**
     * 批量保存未审核数据
     * MS00473,QDV4国泰2009年6月01日01_A
     * 20090901,by xuxuming
     * @param sMutilRowStr String 要保存的数据
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException{
        try{
            //调用重载后的批量保存方法，默认审核状态由系统确定
            return this.saveMutliSetting(sMutilRowStr, false);
        }catch (Exception e) {
            throw new YssException("保存证券兑换信息出错", e);
        }
    }
	
    /**
     * 批量保存数据，审核状态作为参数输入
     * 添加运营应收应付的处理 MS00007 add by songjie 2009-03-13
     * 应多加代码如下：
     * strSql = " delete from " +
                pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " where FNum in(" + operSql.sqlCodes(sOldCashNums) + ")";
     * @param sMutilRowStr 批量保存 方法
     * @param checkFlag 审核标志
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr,boolean checkFlag) throws YssException {//edit by xuxuming,20090901.加了checkFlag参数。MS00473,QDV4国泰2009年6月01日01_A
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        SecPecPayBean secRPBean = null;
        CashPecPayBean cashRPBean = null;
        InvestPayRecBean investPRBean = null;
        TransferBean cashBean = null;
        String strSql = "";
        PreparedStatement pst = null;
        boolean bTrans = false;
        String[] arrData = null;
        String[] arrSec = null;
        String[] arrSecRP = null;
        String[] arrCash = null;
        String[] arrCashRP = null;
        String[] arrInvestRP = null;    //用于拆分运营应收应付信息
        String sSecRPNums = "", sCashNums = "", sCashRPNums = ""; //关联编号集
        String sInvestRPNums = "";      //声明运营应收应付编号
        String sOldSecRPNums = "", sOldCashNums = "", sOldCashRPNums = "";
        String sOldInvestRPNums = "";   //声明老的运营应收应付编号
        String sTradeType = "";
        String sNewNum = "";
        ArrayList arrayFNum = new ArrayList();//将本次保存到数据库中的记录的编号保存在这里，后面需要用到此编号来更改审校状态
        String sDesc = "";
        boolean isToday = false;
        double accruedInterest =0;
        String createTime = "";//统一综合业务数据流入流出数据的创建时间 add by jiang shichao 2010.09.06 
        isToday = sMutilRowStr.split("\r\t")[sMutilRowStr.split("\r\t").length-1].split("\b\t")[0].equalsIgnoreCase("05FI");  //合并太平版本代码
        if(isToday){
        	accruedInterest = Double.parseDouble(sMutilRowStr.split("\r\t")[sMutilRowStr.split("\r\t").length-1].split("\b\t")[1]);//合并太平版本代码
        	//这里应该去掉最后一个\r\t字符串，原因是最后一个\r\t解析后的数据为 isToday与accruedInterest值 by leeyu 20100812 合并太平版本调整
        	sMutilRowStr =sMutilRowStr.substring(0, sMutilRowStr.lastIndexOf("\r\t"));
        }
        //--- MS00303 QDV4交银施罗德2009年3月10日01_B  ----
        SecIntegratedBean secIn = null; //在各笔数据的解析时调用
        //-----------------------------------------------
        //add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        try {
        	//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
        	integrateAdmin.setYssPub(pub);
        	
            bTrans = true;
            conn.setAutoCommit(false);
            arrData = sMutilRowStr.split("\r\t");
            if (arrData.length >= 1) {
                this.parseRowStr(arrData[0]); //传入的是原数据,如编号,日期,证券交易类型 等
                sTradeType = this.sTradeTypeCode;
                sDesc = this.sDesc;
            }
            if (arrData.length >= 3) {
                sSecStr = arrData[2];
            }
            if (arrData.length >= 4) {
                sSecRecPayStr = arrData[3];
                if(arrData[3].split("\b\t").length == 2)//当为批量转货时arrData[3]为isToday与accruedInterest值，不为应收应付的值，故不能取 by leeyu 20100812 合并太平版本调整
                	sSecRecPayStr ="";
            }
            if (arrData.length >= 5) {
                sCashStr = arrData[4];
            }
            if (arrData.length >= 6) {
                sCashRecPayStr = arrData[5];
            }

            if (arrData.length >= 7) {
                sInvestRecPayStr = arrData[6]; //获取运营应收应付信息
            }

            if (arrData.length >= 8) { //由于多加了一个运营应收应付信息字符串，导致查询信息向后移一位
                this.filterType = new SecIntegratedBean();
                this.filterType.setYssPub(pub);
                this.filterType.parseRowStr(arrData[7]); //filter
            }

            arrSec = sSecStr.split("\f\f");
            arrSecRP = sSecRecPayStr.split("\f\f");
            arrCash = sCashStr.split("\f\f");
            arrCashRP = sCashRecPayStr.split("\f\f");
            arrInvestRP = sInvestRecPayStr.split("\f\f");
            strSql = "select FRelaNum,FNumType from " +
                pub.yssGetTableName("Tb_Data_Integrated") +
                " where FNum=" + dbl.sqlString(this.sOldNum) + //用旧编号 QDV4赢时胜（上海）2009年4月21日04_B MS00408 by leeyu 20090428
                " and FRelaNum<>' ' and FNumType<>' '";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FNumType").equalsIgnoreCase("Cash")) {
                    sOldCashNums += rs.getString("FRelaNum") + ",";
                } else if (rs.getString("FNumType").equalsIgnoreCase(
                    "CashRecPay")) {
                    sOldCashRPNums += rs.getString("FRelaNum") + ",";
                } else if (rs.getString("FNumType").equalsIgnoreCase(
                    "SecRecPay")) {
                    sOldSecRPNums += rs.getString("FRelaNum") + ",";
                }

                else if (rs.getString("FNumType").equalsIgnoreCase(
                    "InvestRecPay")) {
                    sOldInvestRPNums += rs.getString("FRelaNum") + ","; //获取老的运营应收应付编号
                }

            }
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") +
                " where FNum=" + dbl.sqlString(this.sOldNum); //用旧编号 QDV4赢时胜（上海）2009年4月21日04_B MS00408 by leeyu 20090428
            dbl.executeSql(strSql);

            //这里不管是新增与修改，都重新再取一次编号，避免因有错误的编号报主键冲突的问题 by leeyu 20090828 MS00643  by leeyu 20090822
            //修改将FNUM做为条件，原因是之前的产生的编号没有按业务日期生成，造成条件相同时产生编号错误
            sNewNum = "E" + YssFun.formatDate(YssFun.toDate(this.sExchangeDate), "yyyyMMdd") +
                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Integrated"),
                                       dbl.sqlRight("FNUM", 6),
                                       "000001",
                                       " where FExchangeDate=" + dbl.sqlDate(this.sExchangeDate) +
                                       " or FExchangeDate=" + dbl.sqlDate("9998-12-31") +
                                       " or FNum like 'E" + YssFun.formatDate(YssFun.toDate(this.sExchangeDate), "yyyyMMdd") + "%'");

            
           
            
            //1.添加字段FInvestType, modify by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
                " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
                " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
                " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
                " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc," +
                " FCheckState,FCreator,FCreateTime,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode,FInvestType " + //MS00007 edlt by songjie 2009-03-13
               
                //modify by zhangjun 2012.07.03  BUG4862业务处理后，综合业务下手工录入一笔交易类型为87的证券更名业务数据被清除  添加FDataOrigin = 1 表示为手工录入数据，
                " ,FAltogetherCash,FApieceCash,FCashAccCode,FMatureDate,FDataOrigin )"+//shashijie 2011-08-16 STORY 1202 
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"+//add by xuxuming,将　所属分类　合到版本中
                " ,?,?,?,?,?)";//shashijie 2011-08-16 STORY 1202
            pst = dbl.openPreparedStatement(strSql);
            
            //---add by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B---//
            String nums = "";
            ArrayList alNums = new ArrayList();
            for (int i = 0; i < arrSec.length; i++) {
                if (arrSec[i] == null || arrSec[i].trim().length() == 0) {
                    continue;
                }
                secIn = new SecIntegratedBean();//新建业务对象
                secIn.setYssPub(pub);
                secIn.parseRowStr(arrSec[i]);//使用此对象解析
                if(alNums.contains(secIn.getsNum()) && secIn.checkStateId == 1){
                	alNums.add(secIn.getsNum());
                	nums += secIn.getsNum() + ",";
                }
            }
            if(nums.length() > 0){
            	nums = nums.substring(0, nums.length() - 1);
            }
            //---add by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B---//
            
            int count =0;
            for (int i = 0; i < arrSec.length; i++) {
                if (arrSec[i] == null || arrSec[i].trim().length() == 0) {
                    continue;
                }
                //------ MS00303 QDV4交银施罗德2009年3月10日01_B  ---------------
                secIn = new SecIntegratedBean();//新建业务对象
                secIn.setYssPub(pub);
                secIn.parseRowStr(arrSec[i]);   //使用此对象解析
				if (!arrayFNum.contains(sNewNum)) {
					arrayFNum.add(sNewNum);// 将记录的编号保存起来,add by
											// xuxuming,20091106
				}
				if(count==0){
					 createTime = secIn.creatorTime;// 统一综合业务数据流入流出数据的创建时间 add by jiang shichao 2010.09.06 
					 count++;
				}
				//delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
//                this.sSubNum = sNewNum + YssFun.formatNumber(i + 1, "00000");
				//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                this.sSubNum = integrateAdmin.getKeyNum();
                pst.setString(1, sNewNum);
                pst.setString(2, this.sSubNum);     //子编号是this中的
                pst.setInt(3, secIn.iInOutType);    //将获取的信息对象调整为secIn，以下雷同
                pst.setString(4, secIn.sSecurityCode);
                pst.setDate(5, YssFun.toSqlDate(this.sExchangeDate)); //兑换日期是this中的
                pst.setDate(6, YssFun.toSqlDate(secIn.sOperDate));
                pst.setString(7, sTradeType);
                pst.setString(8, " ");              //这里的 sRelaNum,sNumType都为' '
                pst.setString(9, " ");
                pst.setString(10, secIn.sPortCode);
                pst.setString(11, secIn.sAnalysisCode1);
                pst.setString(12, secIn.sAnalysisCode2);
                pst.setString(13, secIn.sAnalysisCode3);
                pst.setDouble(14, secIn.dAmount);
                pst.setDouble(15, secIn.dCost);
                pst.setDouble(16, secIn.dMCost);
                pst.setDouble(17, secIn.dVCost);
                pst.setDouble(18, secIn.dBaseCost);
                pst.setDouble(19, secIn.dMBaseCost);
                pst.setDouble(20, secIn.dVBaseCost);
                pst.setDouble(21, secIn.dPortCost);
                pst.setDouble(22, secIn.dMPortCost);
                pst.setDouble(23, secIn.dVPortCost);
                pst.setDouble(24, secIn.dBaseCuryRate);
                pst.setDouble(25, secIn.dPortCuryRate);
                pst.setString(26, secIn.sSecExDesc);
                pst.setString(27, sDesc);
                pst.setInt(28, pub.getSysCheckState() ? 0 : 1);
                pst.setString(29, secIn.creatorCode);
                //pst.setString(30, secIn.creatorTime);
                pst.setString(30, createTime); // modify by jiangshichao 2010.09.06
                //添加了调拨类型、子类型的处理 MS00007 add by songjie 2009-03-13
                if (secIn.sTsfTypeCode.equals("")) {
                    pst.setString(31, " ");
                } else {
                    pst.setString(31, secIn.sTsfTypeCode);
                }
                if (secIn.sSubTsfTypeCode.equals("")) {
                    pst.setString(32, " ");
                } else {
                    pst.setString(32, secIn.sSubTsfTypeCode);
                }
                //MS00007 add by songjie 2009-03-13 添加了调拨子类型代码字段
                //添加 属性分类 MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A 2009-07-07 蒋锦
                if (secIn.attrClsCode.equals("")) {
                    pst.setString(33, " ");
                } else {
                    pst.setString(33, secIn.attrClsCode);
                }
                //edit by songjie 2010.09.19 若取到的投资类型为空，则插入综合业务表会报错，因为投资类型字段不能为空，所以插入空格
                if(secIn.investType.equals("")){
                	pst.setString(34, "C");
                }else{
                	//edit by songjie 2010.09.19 若取到的投资类型为空，则插入综合业务表会报错，因为投资类型字段不能为空，所以插入空格
                	pst.setString(34,secIn.investType);
                	//edit by songjie 2010.09.19 若取到的投资类型为空，则插入综合业务表会报错，因为投资类型字段不能为空，所以插入空格
                }
                //edit by songjie 2010.09.19 若取到的投资类型为空，则插入综合业务表会报错，因为投资类型字段不能为空，所以插入空格
                
                /**shashijie 2011-08-16 STORY 1202*/
                pst.setDouble(35, secIn.FAltogetherCash);
                pst.setDouble(36, secIn.FApieceCash);
                if (secIn.FCashAccCode.equals("")) {
                	pst.setString(37," ");
                } else {
                	pst.setString(37,secIn.FCashAccCode);
				}
                pst.setDate(38,YssFun.toSqlDate(secIn.FMatureDate));
                /**end*/
                pst.setString(39,"1"); //modify by zhangjun 2012.07.03  BUG4862业务处理后，综合业务下手工录入一笔交易类型为87的证券更名业务数据被清除  
                
                pst.executeUpdate();
               //===add by xuxuming,20090914.MS00700.债券应收和转货基本数据要相应，不用重新输入,QDV4中保2009年09月15日02_B.业务类型为内部转货（'81'）或换股时，需要查出其对应的应收应付信息将其保存到应收应付表并关联=====
               // 将证券相关信息保存起来，作为查询条件：证券代码、投资经理、投资组合、券商代码、兑换数量、业务日期
                if(isToday){//合并太平版本代码 这里添加条件判断
                	this.autoCalcuFees1(secIn, accruedInterest);
                }else{
	               //if ("81".equalsIgnoreCase(sTradeType)||"80".equalsIgnoreCase(sTradeType)) {//换股和转货业务都要进行以下计算
	                  //this.autoCalcuFees(secIn);//调用些方法，根据转货数量，计算各项金额
	               //}
                	if(YssOperCons.YSS_JYLX_SPLITOFF.equalsIgnoreCase(sTradeType)){//换股
                		//edit by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B
                		this.autoCalcuFees(secIn, (nums.equals(""))? sNewNum :(nums + "," + sNewNum));//调用些方法，根据转货数量，计算各项金额
                	}else if(YssOperCons.YSS_JYLX_TRANSSEC.equalsIgnoreCase(sTradeType)){//转货
                		//edit by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B
                		this.autoCalcuFees(secIn,true, (nums.equals(""))? sNewNum :(nums + "," + sNewNum));//采用统计库存的方法来计算利息 by leeyu 20100417 QDV4中保2010年4月14日02_B MS01092
                	}
               }
               //===end,===============================================================================================
            }
            //===add by xuxuming,20090914.MS00700.债券应收和转货基本数据要相应，不用重新输入,QDV4中保2009年09月15日02_B.业务类型为内部转货（'81'）时，需要查出其对应的应收应付信息将其保存到应收应付表并关联=====
            if (bFlag) { //有业务类型为内部转货（'81'）或换股的流出,则需要保存相应的流入数据到证券应收应付表
               SecPecPayBean tempsecPecPayData = new SecPecPayBean(); //对应　证券应收应付款表
               for (int i = 0; i < arraySecIn.size(); i++) { //依次取出这些流入
                  SecIntegratedBean tempSecIn = (SecIntegratedBean) arraySecIn.get(
                        i);
                  String sKey = ""; //标记HashMap中的Key
                  sKey = tempSecIn.sOperDate + "\f\f" + tempSecIn.sPortCode +
                        "\f\f" + tempSecIn.sSecurityCode + "\f\f" +
                        Math.abs(tempSecIn.dAmount); //数量取绝对值。因为流出为负，流入为正
                  if (hmSecPec.containsKey(sKey)) { //当有证券应收应付数据时，才须要保存
                     tempsecPecPayData = (SecPecPayBean) hmSecPec.get(sKey);
                     tempsecPecPayData.setInvMgrCode(tempSecIn.sAnalysisCode1); //投资经理
                     tempsecPecPayData.setBrokerCode(tempSecIn.sAnalysisCode2); //券商代码
                     tempsecPecPayData.setInOutType(tempSecIn.iInOutType); //此处都是流入                     
                     tempBuf.append(tempsecPecPayData.buildRowStrForParse()).append(
                           "\f\f"); //保存流入数据
                  }
               }
            }
            if (sSecRecPayStr.trim().length() < 1||sSecRecPayStr.indexOf("08FI")>=0) { //add by xuxuming,20091110.MS00800 没有要保存的证券应收应付数据时.包含08FI时，也要将下面的数据保存。08FI数据不对，要删除 合并太平版本代码
                sSecRecPayStr = tempBuf.toString(); //将前面查询到的证券应收应付数据保存到字符串中，以备保存到数据库
                arrSecRP = sSecRecPayStr.split("\f\f");
            }
            //===end========================================================================================================

            secRPBean = new SecPecPayBean();
            secRPBean.setYssPub(pub);
            //先删除证券应收应付款表中的信息才能保证关联删除
            strSql = " delete from " +
                pub.yssGetTableName("Tb_data_SecRecPay") +
                " where FNum in(" + operSql.sqlCodes(sOldSecRPNums) + ")";
            dbl.executeSql(strSql);

            for (int i = 0; i < arrSecRP.length; i++) {
                if (arrSecRP[i].length() == 0) {
                    continue;
                }
                secRPBean.parseRowStr(arrSecRP[i]);
                secRPBean.setStrNum("");
                //------- add by wangzuochun 2010.08.02  MS01520    综合业务中维护换股数据时，证券应收应付数据经库存统计后被删除    QDV4赢时胜上海2010年07月30日01_B   
                if ("87".equals(this.sTradeTypeCode)) {
					if (("09".equals(secRPBean.getStrTsfTypeCode()) && "09EQ"
							.equals(secRPBean.getStrSubTsfTypeCode()))
							|| ("99".equals(secRPBean.getStrTsfTypeCode()) && "9909EQ"
									.equals(secRPBean.getStrSubTsfTypeCode()))
							|| ("99".equals(secRPBean.getStrTsfTypeCode()) && "9905EQ"
									.equals(secRPBean.getStrSubTsfTypeCode()))) {
						secRPBean.setRelaNumType("SecNameChange");
					}
				}
                // ---------------------MS01520----------------------///
                secRPBean.addSetting();                
                sSecRPNums += secRPBean.getStrNum();
                sSecRPNums += "\t";
            }
            this.saveRelaDatas("SecRecPay", sSecRecPayStr, sSecRPNums, sNewNum,
                               sDesc);

            cashBean = new TransferBean();
            cashBean.setYssPub(pub);
            //------------先删除资金调拨 sj edit 20080619 ---------------------------
            strSql = " delete from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FNum in(" + operSql.sqlCodes(sOldCashNums) + ")";
            dbl.executeSql(strSql);
            strSql = " delete from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FNum in(" + operSql.sqlCodes(sOldCashNums) + ")";
            dbl.executeSql(strSql);
            //---------------------------------------------------------------------
            for (int i = 0; i < arrCash.length; i++) {
                arrCash[i] = arrCash[i].replaceAll("~~", "\r\t");
                arrCash[i] = arrCash[i].replaceAll("\b\b", "\f\f");
                if (arrCash[i].length() == 0) {
                    continue;
                }
                cashBean.parseRowStr(arrCash[i]);

                cashBean.setStrNum("");
                cashBean.addSetting();
                sCashNums += cashBean.getStrNum();
                sCashNums += "\t";
            }
            this.saveRelaDatas("Cash", sCashStr, sCashNums, sNewNum, sDesc);

            cashRPBean = new CashPecPayBean();
            cashRPBean.setYssPub(pub);
            for (int i = 0; i < arrCashRP.length; i++) {
                if (arrCashRP[i].length() == 0) {
                    continue;
                }
                cashRPBean.parseRowStr(arrCashRP[i]);
                strSql = " delete from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                    " where FNum in(" + operSql.sqlCodes(sOldCashRPNums) + ")";
                dbl.executeSql(strSql);

                cashRPBean.setNum("");
                cashRPBean.addSetting();
                sCashRPNums += cashRPBean.getNum();
                sCashRPNums += "\t";
            }
            this.saveRelaDatas("CashRecPay", sCashRecPayStr, sCashRPNums,
                               sNewNum,
                               sDesc);

            investPRBean = new InvestPayRecBean(); //新建运营应收应付实例
            investPRBean.setYssPub(pub);
            strSql = " delete from " +
                pub.yssGetTableName("Tb_data_InvestPayRec") +
                " where FNum in(" + operSql.sqlCodes(sOldInvestRPNums) +
                ")"; //在运营应收应付表中删除与老的运营应收应付编号相关的所有信息
            dbl.executeSql(strSql);

            for (int i = 0; i < arrInvestRP.length; i++) {
                if (arrInvestRP[i].length() == 0) {
                    continue;
                }
                investPRBean.parseRowStr(arrInvestRP[i]);   //解析运营应收应付信息
                investPRBean.setNum("");                    //设置编号
                investPRBean.addSetting();                  //调用运营应收应付bean的添加方法
                sInvestRPNums += investPRBean.getNum();     //获取编号
                sInvestRPNums += "\t";
            }
            this.saveRelaDatas("InvestRecPay", sInvestRecPayStr, sInvestRPNums,sNewNum,sDesc); //在综合业务表中保存运营应收应付数据的相关信息

            bTrans = false;
            conn.commit();
            conn.setAutoCommit(true);
          //====add by xuxuming,指数信息调整模块　要求　写入到　综合业务及相关联表的数据都为已审核===
          if(checkFlag){ //如果设置了审核标志，则审核状态设置为1，修改综合业务表中的审核状态，并相应修改关联表的审核状态
        	  for(int i=0;i<arrayFNum.size();i++){
        		  SecIntegratedBean  secInTemp = new SecIntegratedBean();//新建业务对象
        		  secInTemp.setYssPub(pub);
        		  secInTemp.setsNum(arrayFNum.get(i).toString());
        		  secInTemp.checkStateId = 1; 
        		  secInTemp.parseRowStr(secInTemp.buildRowStrForParse());//调用这个方法后，下面的方法才能得到要更改状态的记录编号
        		  secInTemp.checkSetting();
        	  }
          }
          //===========end=======================================
        } catch (Exception e) {
            throw new YssException("保存证券兑换信息出错", e);
        } finally {
            dbl.closeStatementFinal(pst);   //添加关闭预处理方法 by leeyu 2009-01-08
            dbl.closeResultSetFinal(rs);    //添加关闭结果集方法 by leeyu 2009-01-08
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

//====add by xuxuming,20090914.MS00700.债券应收和转货基本数据要相应，不用重新输入,QDV4中保2009年09月15日02_B.新增 内部转货类型时，需要将证券应收应付库存数据保存到证券应收应付表，并将综合业务表与之关联=====
    //=========考虑到买入当天就进行 换投的情况，MS00706＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
    /**
 * 将证券应收应付款库存相就数据保存到证券应收应付款表对应字段中
 * @param secPecPayData SecPecPayBean,对应证券应收应付表
 * @param secRecPayBalData SecRecPayBalBean，对应证券应收应付库存
 * @param inOutType int，流出\流入类型
 * @param dAmoutRatio,费用比率。是 流出数量与库存数量的比值，以此来计算各项金额的流出值
 */
 //edit by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B
public void copyBeanToSecPecPay(SecPecPayBean secPecPayData, SecRecPayBalBean secRecPayBalData, int inOutType,
                                double dAmoutRatio, SecPecPayBean checkedSec) {
    secPecPayData.setStrNum("");
    secPecPayData.setInvMgrCode(secRecPayBalData.getSAnalysisCode1());//投资经理
    secPecPayData.setStrSubTsfTypeName(""); //只保存CODE，不保存NAME
    secPecPayData.setBrokerCode(secRecPayBalData.getSAnalysisCode2());//券商代码
    secPecPayData.setExchangeCode("");//交易地点
//    secPecPayData.setStrOldFAnalysisCode1(secRecPayBalData.getSAnalysisCode1());
//    secPecPayData.setStrOldFAnalysisCode2(secRecPayBalData.getSAnalysisCode2());
    secPecPayData.setStrTsfTypeName("");
    secPecPayData.setStrPortCode(secRecPayBalData.getSPortCode());
    secPecPayData.setStrSecurityCode(secRecPayBalData.getSSecurityCode());
    secPecPayData.setOldTransDate(secRecPayBalData.getDtStorageDate());
    secPecPayData.setStartDate(secRecPayBalData.getDtStorageDate());
    secPecPayData.setEndDate(secRecPayBalData.getDtStorageDate());
    secPecPayData.setTransDate(secRecPayBalData.getDtStorageDate());
    secPecPayData.setCatTypeCode(secRecPayBalData.getCatTypeCode());
    secPecPayData.setAttrClsCode(secRecPayBalData.getAttrClsCode());
    secPecPayData.setStrTsfTypeCode(secRecPayBalData.getSTsfTypeCode());
    secPecPayData.setStrSubTsfTypeCode(secRecPayBalData.getSSubTsfTypeCode());
    secPecPayData.setStrCuryCode(secRecPayBalData.getSCuryCode());
    secPecPayData.setInOutType(inOutType);
    secPecPayData.setInvestType(secRecPayBalData.getSInvestType());//添加投资类型 by leeyu 20100812 合并太平版本调整
   //---add by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B---//
    if(checkedSec != null){
    	secPecPayData.setMoney(secRecPayBalData.getDBal() - checkedSec.getMoney());
    	secPecPayData.setMMoney(secRecPayBalData.getDMBal() - checkedSec.getMMoney());
    	secPecPayData.setVMoney(secRecPayBalData.getDVBal() - checkedSec.getVMoney());
    	secPecPayData.setBaseCuryMoney(secRecPayBalData.getDBaseBal() - checkedSec.getBaseCuryMoney());
        secPecPayData.setMBaseCuryMoney(secRecPayBalData.getDMBaseBal() - checkedSec.getMBaseCuryMoney());
        secPecPayData.setVBaseCuryMoney(secRecPayBalData.getDVBaseBal() - checkedSec.getVBaseCuryMoney());
        secPecPayData.setPortCuryMoney(secRecPayBalData.getDPortBal() - checkedSec.getPortCuryMoney());
        secPecPayData.setMPortCuryMoney(secRecPayBalData.getDMPortBal() - checkedSec.getMPortCuryMoney());
        secPecPayData.setVPortCuryMoney(secRecPayBalData.getDVPortBal() - checkedSec.getVPortCuryMoney());
        secPecPayData.setMoneyF(secRecPayBalData.getBalF() - checkedSec.getMoneyF());
        secPecPayData.setBaseCuryMoneyF(secRecPayBalData.getBaseBalF() - checkedSec.getBaseCuryMoneyF());
        secPecPayData.setPortCuryMoneyF(secRecPayBalData.getPortBalF() - checkedSec.getPortCuryMoneyF());
    }else{
        secPecPayData.setMoney(YssD.round(YssD.mul(secRecPayBalData.getDBal(),dAmoutRatio),4));//保留4位小数
        secPecPayData.setMMoney(YssD.round(YssD.mul(secRecPayBalData.getDMBal(),dAmoutRatio),4));
        secPecPayData.setVMoney(YssD.round(YssD.mul(secRecPayBalData.getDVBal(),dAmoutRatio),4));
        secPecPayData.setBaseCuryMoney(YssD.round(YssD.mul(secRecPayBalData.getDBaseBal(),dAmoutRatio),4));
        secPecPayData.setMBaseCuryMoney(YssD.round(YssD.mul(secRecPayBalData.getDMBaseBal(),dAmoutRatio),4));
        secPecPayData.setVBaseCuryMoney(YssD.round(YssD.mul(secRecPayBalData.getDVBaseBal(),dAmoutRatio),4));
        secPecPayData.setPortCuryMoney(YssD.round(YssD.mul(secRecPayBalData.getDPortBal(),dAmoutRatio),4));
        secPecPayData.setMPortCuryMoney(YssD.round(YssD.mul(secRecPayBalData.getDMPortBal(),dAmoutRatio),4));
        secPecPayData.setVPortCuryMoney(YssD.round(YssD.mul(secRecPayBalData.getDVPortBal(),dAmoutRatio),4));
        secPecPayData.setMoneyF(YssD.round(YssD.mul(secRecPayBalData.getBalF(),dAmoutRatio),15));
        secPecPayData.setBaseCuryMoneyF(YssD.round(YssD.mul(secRecPayBalData.getBaseBalF(),dAmoutRatio),15));
        secPecPayData.setPortCuryMoneyF(YssD.round(YssD.mul(secRecPayBalData.getPortBalF(),dAmoutRatio),15));	
    }
    //---add by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B---//
    secPecPayData.setCheckState(secRecPayBalData.checkStateId);
    secPecPayData.creatorCode = secRecPayBalData.creatorCode;
    secPecPayData.creatorTime = secRecPayBalData.creatorTime;
    secPecPayData.checkUserCode = secRecPayBalData.checkUserCode;
    secPecPayData.checkTime = secRecPayBalData.checkTime;
//    secPecPayData.setInvestType(secRecPayBalData.getSInvestType());//投资类型。以前版本中没有这个属性，可去掉这句。
}

 /**
  * 针对转货业务，计算转换的利息
  * @param secIn SecIntegratedBean
  * @throws YssException
  */
  //edit by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B
 public void autoCalcuFees(SecIntegratedBean secIn, String nums) throws YssException{
	 	SecRecPayBalBean secRecPayBalData = new SecRecPayBalBean(); //对应　证券应收应付库存　表
                  SecPecPayBean secPecPayData = new SecPecPayBean(); //对应　证券应收应付款表
                  SecRecPayBalBean secRecPayBalFilterType = new SecRecPayBalBean(); //查询证券应收应付库存的条件
                  secRecPayBalData.setYssPub(pub);
                  double dAmoutRatio = 0;//根据此值来设置转出的利息；由转出的数量计算得到。
                  
      			//---add by songjie 2011.04.15 BUG 1664 QDV4太平2010年10月27日01_B---//
      			//判断当日是否有还未审核的相关业务日期、证券、组合、分析代码、所属分类、流出的转货数据
      			judgeUnCheckInfo(secIn, nums);
      			SecPecPayBean checkedSec = getCheckedInfo(secIn);//获取当日已审核的相关业务日期、证券、组合、分析代码、所属分类、流出的转货数据
      			//---add by songjie 2011.04.15 BUG 1664 QDV4太平2010年10月27日01_B---//
                  
                  if ( -1 == secIn.iInOutType) { //为流出时才能从证券应收应付库存查询到相应记录
                        secRecPayBalFilterType.setSSecurityCode(secIn.
                              sSecurityCode); //证券代码
                        secRecPayBalFilterType.setSPortCode(secIn.sPortCode); //投资组合
                        secRecPayBalFilterType.setBBegin("false"); //是否为期初
                        secRecPayBalFilterType.setDtStorageDate(YssFun.addDay(YssFun.toDate(secIn.sOperDate),-1)); //业务日期前一天
                        secRecPayBalFilterType.setSAnalysisCode1(secIn.
                              sAnalysisCode1); //投资经理
                        secRecPayBalFilterType.setSAnalysisCode2(secIn.
                              sAnalysisCode2); //券商代码
                        //                   secRecPayBalFilterType.setSAnalysisCode3(secIn.SAnalysisCode3); //投资经理、券商代码等信息
                        secRecPayBalFilterType.setSTsfTypeCode("06"); //应收款项
                        secRecPayBalFilterType.setSSubTsfTypeCode("06FI"); //应收债券利息
                        secRecPayBalData.setFilterType(secRecPayBalFilterType);
                        secRecPayBalData.getSetting(); //根据filterType条件,从证券应收应付库存中查询到需要的结果
                        //                         SecPecPayBean secPecPayData = new SecPecPayBean(); //对应　证券应收应付款表
                        if (secRecPayBalData.getSSecurityCode() != null &&
                            secRecPayBalData.getSSecurityCode().trim().length() >
                            0) { //当有查询结果时，才须要保存
                           bFlag = true; //有业务类型为内部转货（'81'）的流出
                           //===以下代码是为了根据流出的数量，自动计算债券应收利息=====
                           //======算法：流出数量/库存数量*应收库存中各项金额=========
                           //计算库存数量,从证券库存中取数据
                           SecurityStorageBean secStorFilterType = new
                                 SecurityStorageBean();
                           SecurityStorageBean secStorData = new
                                 SecurityStorageBean();
                           secStorFilterType.setStrSecurityCode(secIn.
                                 sSecurityCode); //证券代码
                           secStorFilterType.setStrPortCode(secIn.sPortCode); //投资组合
                           secStorFilterType.setBBegin("false"); //是否为期初
                           secStorFilterType.setStrStorageDate(YssFun.formatDate(YssFun.addDay(YssFun.toDate(secIn.sOperDate),-1))); //业务日期前一天
                           secStorFilterType.setStrFAnalysisCode1(secIn.
                                 sAnalysisCode1); //投资经理
                           secStorFilterType.setStrFAnalysisCode2(secIn.
                                 sAnalysisCode2); //券商代码
                           secStorFilterType.setIsOnlyColumns("1");//如果不设置这个，则只是取出表头，没有任何数据
                           secStorData.setFilterType(secStorFilterType);
                           secStorData.setYssPub(pub);
                           secStorData.getSetting(); //主要是为了得到库存数量
                           //===计算库存数量 End==============================
                           double dSecStorAmout = 0;
//                           double dAmoutRatio = 0;
                           if (secStorData.getStrStorageAmount() != null &&
                               secStorData.getStrStorageAmount().trim().length() >
                               0) {
                              dSecStorAmout = YssFun.toDouble(secStorData.
                                    getStrStorageAmount()); //库存数量
                              dAmoutRatio = YssD.div(Math.abs(secIn.dAmount),dSecStorAmout); //流出数量/库存数量
                           }
                            //---add by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B---//
               			   //若当笔流出数量加上当日录入的已审核的流出数量之和等于前一日的库存数量，则用轧差算法计算证券应收应付数据的金额
                           if(checkedSec != null && ((dSecStorAmout + checkedSec.getAmount() + secIn.dAmount) == 0)){
                               this.copyBeanToSecPecPay(secPecPayData,
                            		   secRecPayBalData,
                                       secIn.iInOutType,dAmoutRatio, checkedSec);
                           }else{
                               this.copyBeanToSecPecPay(secPecPayData,
                                       secRecPayBalData,
                                       secIn.iInOutType,dAmoutRatio, null); //将证券应收应付库存保存到证券应收应付表，对应的是流出的数据
                           }
                           //---add by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B---//
                           secPecPayData.setBaseCuryRate(secIn.getDBaseCuryRate());//将基础汇率也保存到应收应付表,add by xuxuming,20091028
                           secPecPayData.setPortCuryRate(secIn.getDPortCuryRate());//将组合汇率也保存到应收应付表,add by xuxuming,20091028
                        }else{//当证券应收应付库存中没有对应数据时，有可能是当天买入就作该（转货）业务，此时库存中没有;要从应收应付表中取数据，再根据转货数量算出金额，然后再保存一条流入和流出
                           TradeSubBean tradeSubFilter = new TradeSubBean();//查询条件
                           TradeSubBean tradeSubData = new TradeSubBean();//交易子表对象,主要是从交易子表中获取交易数量
                           tradeSubData.setYssPub(pub);
                           tradeSubFilter.setSecurityCode(secIn.
                                 sSecurityCode); //证券代码
                           tradeSubFilter.setPortCode(secIn.sPortCode); //投资组合
                           tradeSubFilter.setBargainDate(secIn.sOperDate); //业务日期
                           tradeSubFilter.setInvMgrCode(secIn.
                                 sAnalysisCode1); //投资经理
                           tradeSubFilter.setBrokerCode(secIn.
                                 sAnalysisCode2); //券商代码
                           tradeSubFilter.setIsOnlyColumns("0");//如果不设置这个，则只是取出表头，没有任何数据
                           tradeSubFilter.setAutoSettle("1");
                           tradeSubData.setFilterType(tradeSubFilter);
                           tradeSubData.getSettingByFilter();//得到交易子表对象
//                           double dAmoutRatio = 0;
                           if (tradeSubData.getSecurityCode() != null &&
                               tradeSubData.getSecurityCode().trim().length() > 0) { //交易子表有数据才需要保存
                              bFlag = true; //有业务类型为内部转货（'81'）的流出,后面根据这个标志进行保存相关流入数据
                              dAmoutRatio = YssD.div(Math.abs(secIn.dAmount),
                                                     tradeSubData.getTradeAmount()); //流出数量/总数量
                              //下面要从应收应付表中获取数据。根据dAmoutRatio计算出对应各种金额，然后保存一条流入和流出数据
                              SecPecPayBean secPecPayFilter = new SecPecPayBean();
                              secPecPayFilter.setStrSecurityCode(secIn.sSecurityCode);
                              secPecPayFilter.setStrPortCode(secIn.sPortCode);
                              secPecPayFilter.setTransDate(YssFun.toDate(secIn.sOperDate));
                              secPecPayFilter.setInvMgrCode(secIn.sAnalysisCode1);
                              secPecPayFilter.setBrokerCode(secIn.sAnalysisCode2);
                              secPecPayFilter.setIsOnlyColumns("0"); //取所有数据，而不是表头
                              //----- MS00800 QDV4中保2009年11月6日02_B 合并太平版本代码-------------------------
							  secPecPayFilter.setStrTsfTypeCode("06");
							  //----- MS00800  end --------------------
                              secPecPayFilter.setStrSubTsfTypeCode("06FI_B");
                              secPecPayData.setYssPub(pub);
                              secPecPayData.setFilterType(secPecPayFilter);
                              secPecPayData.getSetting(); //得到应收应付数据
							  //------MS00800 QDV4中保2009年11月6日02_B 合并太平版本代码------------------------------------------------
                              secPecPayData.setStrSubTsfTypeCode("06FI");//将06FI_B改为06FI
                              secPecPayData.setInvestType(secIn.investType);//添加投资类型 by leeyu 20100812 合并太平版本调整
							  //------MS00800 ------------------------------------------------
                              if (secPecPayData.getStrSecurityCode() != null &&
                                  secPecPayData.getStrSecurityCode().trim().length() > 0) {
                                 //有应收应付数据，根据dAmoutRatio设置各项金额
                                 secPecPayData.setMoney(YssD.round(YssD.mul(
                                       secPecPayData.getMoney(), dAmoutRatio), 4)); //保留4位小数
                                 secPecPayData.setMMoney(YssD.round(YssD.mul(
                                       secPecPayData.getMMoney(), dAmoutRatio), 4));
                                 secPecPayData.setVMoney(YssD.round(YssD.mul(
                                       secPecPayData.getVMoney(), dAmoutRatio), 4));
                                 secPecPayData.setBaseCuryMoney(YssD.round(YssD.mul(
                                       secPecPayData.getBaseCuryMoney(), dAmoutRatio), 4));
                                 secPecPayData.setMBaseCuryMoney(YssD.round(YssD.mul(
                                       secPecPayData.getMBaseCuryMoney(), dAmoutRatio), 4));
                                 secPecPayData.setVBaseCuryMoney(YssD.round(YssD.mul(
                                       secPecPayData.getVBaseCuryMoney(), dAmoutRatio), 4));
                                 secPecPayData.setPortCuryMoney(YssD.round(YssD.mul(
                                       secPecPayData.getPortCuryMoney(), dAmoutRatio), 4));
                                 secPecPayData.setMPortCuryMoney(YssD.round(YssD.mul(
                                       secPecPayData.getMPortCuryMoney(), dAmoutRatio), 4));
                                 secPecPayData.setVPortCuryMoney(YssD.round(YssD.mul(
                                       secPecPayData.getVPortCuryMoney(), dAmoutRatio), 4));
                                 secPecPayData.setMoneyF(YssD.round(YssD.mul(
                                       secPecPayData.getMoneyF(), dAmoutRatio), 15));
                                 secPecPayData.setBaseCuryMoneyF(YssD.round(YssD.mul(
                                       secPecPayData.getBaseCuryMoneyF(), dAmoutRatio), 15));
                                 secPecPayData.setPortCuryMoneyF(YssD.round(YssD.mul(
                                       secPecPayData.getPortCuryMoneyF(), dAmoutRatio), 15));
                                 secPecPayData.setInOutType(secIn.iInOutType);
                                 secPecPayData.setStrSubTsfTypeName(""); //只保存CODE，不保存NAME
                                 secPecPayData.setStrTsfTypeName("");
                                 secPecPayData.setOldTransDate(YssFun.toDate(secIn.sOperDate));
                                 secPecPayData.setStartDate(YssFun.toDate(secIn.sOperDate));
                                 secPecPayData.setEndDate(YssFun.toDate(secIn.sOperDate));
                                 secPecPayData.setDesc("");//防止为NULL
                              }
                           }
                        }
                        if(secPecPayData.getStrSecurityCode()!=null&&secPecPayData.getStrSecurityCode().trim().length()>0){//有数据时才需要保存
                           tempBuf.append(secPecPayData.buildRowStrForParse()).
                                 append(
                                 "\f\f"); //保存流出数据
                                    String sKey = "";
                           sKey = secIn.sOperDate + "\f\f" + secIn.sPortCode +
                                 "\f\f" +
                                 secIn.sSecurityCode + "\f\f" +
                                 Math.abs(secIn.dAmount); //数量取绝对值。因为流出为负，流入为正
                           if (!hmSecPec.containsKey(sKey)) {
                              hmSecPec.put(sKey, secPecPayData); //将证券应收应付数据保存，这是流出数据。在后面保存流入时要用到此中数据
                           }

                        }
//                     }
                  }
                  else if (1 == secIn.iInOutType) { //为流入时，将对象保存到ArrayList中，在后面要保存这些流入到证券应收应付表
                     arraySecIn.add(secIn);
                  }

 }
 
	/**
	 * add by songjie 2011.04.15 
	 * BUG 1664 QDV4太平2010年10月27日01_B
	 * @throws YssException
	 */
	private void judgeUnCheckInfo(SecIntegratedBean secIn, String nums) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String num = "";
		try {
			strSql = " select * from " + pub.yssGetTableName("Tb_Data_Integrated") + 
			" where FCheckState = 0 and FTradeTypeCode = " + dbl.sqlString(secIn.getSTradeTypeCode()) + 
			" and FSecurityCode = " + dbl.sqlString(secIn.getSSecurityCode()) + 
			" and FPortCode = " + dbl.sqlString(secIn.getSPortCode()) + 
			" and FOperDate = " + dbl.sqlDate(secIn.getSOperDate()) + 
			" and FInoutType = -1 " + (nums.equals("")? "" : (" and FNum not in (" + operSql.sqlCodes(nums) + ")"));
			if(secIn.getSAnalysisCode1() != null && secIn.getSAnalysisCode1().trim().length() > 0){
				strSql += " and FAnalysiscode1 = " + dbl.sqlString(secIn.getSAnalysisCode1());
			}
			if(secIn.getSAnalysisCode2() != null && secIn.getSAnalysisCode2().trim().length() > 0){
				strSql += " and FAnalysiscode2 = " + dbl.sqlString(secIn.getSAnalysisCode2());
			}
			if(secIn.getSAnalysisCode3() != null && secIn.getSAnalysisCode3().trim().length() > 0){
				strSql += " and FAnalysiscode3 = " + dbl.sqlString(secIn.getSAnalysisCode3());
			}
			if(secIn.getAttrClsCode() != null && secIn.getAttrClsCode().trim().length() > 0){
				strSql += " and FAttrclscode = " + dbl.sqlString(secIn.getAttrClsCode());
			}
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				num += rs.getString("FNum") + ",";
			}
			if(num.length() > 0){
				num = num.substring(0, num.length() - 1);
				errMessage ="在未审核界面有如下交易编号的综合业务数据：" + num + "，请审核其中有效数据并删除无效冗余数据";
				throw new Exception(errMessage);
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());//modified by yeshenghong BUG4503
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
 
	/**
	 * add by songjie 2011.04.15 
	 * BUG 1664 QDV4太平2010年10月27日01_B
	 * @param secIn
	 * @throws YssException
	 */
	private SecPecPayBean getCheckedInfo(SecIntegratedBean secIn) throws YssException{
		String strSql = "";
		ResultSet rs = null;
		SecPecPayBean secRecPay = null;
		try{
			strSql = " select c.FNum, c.FRelaNum, c.FAmount, sec.FMoney, sec.FMmoney, " + 
			" sec.fvmoney, sec.fbasecurymoney, sec.fmbasecurymoney, sec.fvbasecurymoney, " + 
			" sec.fportcurymoney, sec.fmportcurymoney, sec.fvportcurymoney, " +
			" sec.fmoneyf, sec.fbasecurymoneyf, sec.fportcurymoneyf " + 
			" from (select b.FNum, b.FAmount, inte.frelanum as FRelaNum " + 
			" from (select a.Fnum as FNum, a.Famount as FAmount from " + 
			pub.yssGetTableName("Tb_Data_Integrated") + 
			" a where a.FCheckState = 1 and a.Ftradetypecode = " + 
			dbl.sqlString(secIn.getSTradeTypeCode()) + 
			" and a.Fsecuritycode = " + dbl.sqlString(secIn.getSSecurityCode()) + 
			" and a.Fportcode = " + dbl.sqlString(secIn.getSPortCode()) + 
			" and a.Foperdate = " + dbl.sqlDate(secIn.getSOperDate()) + 
			" and a.Finouttype = -1 ";
			if(secIn.getSAnalysisCode1() != null && secIn.getSAnalysisCode1().trim().length() > 0){
				strSql += " and a.Fanalysiscode1 = " + dbl.sqlString(secIn.getSAnalysisCode1());
			}else{
				strSql += " and a.Fanalysiscode1 = ' ' ";
			}
			if(secIn.getSAnalysisCode2() != null && secIn.getSAnalysisCode2().trim().length() > 0){
				strSql += " and a.Fanalysiscode2 = " + dbl.sqlString(secIn.getSAnalysisCode2());
			}else{
				strSql += " and a.Fanalysiscode2 = ' ' ";
			}
			if(secIn.getSAnalysisCode3() != null && secIn.getSAnalysisCode3().trim().length() > 0){
				strSql += " and a.Fanalysiscode3 = " + dbl.sqlString(secIn.getSAnalysisCode3());
			}else{
				strSql += " and a.Fanalysiscode3 = ' ' ";
			}
			if(secIn.getAttrClsCode() != null && secIn.getAttrClsCode().trim().length() > 0){
				strSql += " and a.Fattrclscode = " + dbl.sqlString(secIn.getAttrClsCode());
			}else{
				strSql += " and a.Fattrclscode = ' ' ";
			}
			strSql += " ) b left join (select * from " + pub.yssGetTableName("Tb_Data_Integrated") +
			" where fnumtype = 'SecRecPay') inte on inte.Fnum = b.FNum) c " + 
			" left join (select * from " + pub.yssGetTableName("Tb_Data_Secrecpay") + " secrec where FInOut = -1) " + 
			" sec on sec.fnum = c.FRelaNum where fmoney is not null ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				if(secRecPay == null){
					secRecPay = new SecPecPayBean();
					secRecPay.setAmount(rs.getDouble("FAmount"));
					secRecPay.setMoney(rs.getDouble("fmoney"));
					secRecPay.setMMoney(rs.getDouble("fmmoney"));
					secRecPay.setVMoney(rs.getDouble("fvmoney"));
					secRecPay.setBaseCuryMoney(rs.getDouble("fbasecurymoney"));
					secRecPay.setMBaseCuryMoney(rs.getDouble("fmbasecurymoney"));
					secRecPay.setVBaseCuryMoney(rs.getDouble("fvbasecurymoney"));
					secRecPay.setPortCuryMoney(rs.getDouble("fportcurymoney"));
					secRecPay.setMPortCuryMoney(rs.getDouble("fmportcurymoney"));
					secRecPay.setVPortCuryMoney(rs.getDouble("fvportcurymoney"));
					secRecPay.setMoneyF(rs.getDouble("fmoneyf"));
					secRecPay.setBaseCuryMoneyF(rs.getDouble("fbasecurymoneyf"));
					secRecPay.setPortCuryMoneyF(rs.getDouble("fportcurymoneyf"));
				}else{
					secRecPay.setAmount(secRecPay.getAmount() + rs.getDouble("FAmount"));
					secRecPay.setMoney(secRecPay.getMoney() + rs.getDouble("fmoney"));
					secRecPay.setMMoney(secRecPay.getMMoney() + rs.getDouble("fmmoney"));
					secRecPay.setVMoney(secRecPay.getVMoney() + rs.getDouble("fvmoney"));
					secRecPay.setBaseCuryMoney(secRecPay.getBaseCuryMoney() + rs.getDouble("fbasecurymoney"));
					secRecPay.setMBaseCuryMoney(secRecPay.getMBaseCuryMoney() + rs.getDouble("fmbasecurymoney"));
					secRecPay.setVBaseCuryMoney(secRecPay.getVBaseCuryMoney() + rs.getDouble("fvbasecurymoney"));
					secRecPay.setPortCuryMoney(secRecPay.getPortCuryMoney() + rs.getDouble("fportcurymoney"));
					secRecPay.setMPortCuryMoney(secRecPay.getMPortCuryMoney() + rs.getDouble("fmportcurymoney"));
					secRecPay.setVPortCuryMoney(secRecPay.getVPortCuryMoney() + rs.getDouble("fvportcurymoney"));
					secRecPay.setMoneyF(secRecPay.getMoneyF() + rs.getDouble("fmoneyf"));
					secRecPay.setBaseCuryMoneyF(secRecPay.getBaseCuryMoneyF() + rs.getDouble("fbasecurymoneyf"));
					secRecPay.setPortCuryMoneyF(secRecPay.getPortCuryMoneyF() + rs.getDouble("fportcurymoneyf"));
				}
			}
			
			return secRecPay;
		}catch(Exception e){
			throw new YssException(e.getMessage() , e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
 	/**
 	 * 重载autoCalcuFees()，通过当日的库存余额来计算数据 此方法会统计当日的库存(除当日的综合业务数据)，但不保存到库存表
	 * QDV4中保2010年4月14日02_B MS01092 by leeyu 20100419
 	 * @param secIn
 	 * @param bStagSoock
 	 * @throws YssException
 	 */
	 //edit by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B
 	public void autoCalcuFees(SecIntegratedBean secIn,boolean bStagSoock,String nums) throws YssException{
 		ArrayList alSecurity = null;
 		ArrayList alSecRecPay = null;
 		SecurityStorageBean securitystorage = null; //证券库存
		SecRecPayBalBean secrecpaybal = null;	//证券应收应付
		SecPecPayBean secPecPayData = new SecPecPayBean(); //对应　证券应收应付款表
 		double dAmoutRatio = 0;//根据此值来设置转出的利息；由转出的数量计算得到。
		try {
			//---add by songjie 2011.04.15 BUG 1664 QDV4太平2010年10月27日01_B---//
			//判断当日是否有还未审核的相关业务日期、证券、组合、分析代码、所属分类、流出的转货数据
			judgeUnCheckInfo(secIn, nums);
			SecPecPayBean checkedSec = getCheckedInfo(secIn);//获取当日已审核的相关业务日期、证券、组合、分析代码、所属分类、流出的转货数据
			//---add by songjie 2011.04.15 BUG 1664 QDV4太平2010年10月27日01_B---//
			if ( -1 == secIn.iInOutType) {
     		   StgSecurity stgSecurity=new StgSecurity(); //证券应收应付库存
     		   stgSecurity.setYssPub(pub);
     		   stgSecurity.setStatCodes(secIn.sSecurityCode);
     		   stgSecurity.initStorageStat(YssFun.toDate(secIn.sOperDate), YssFun.toDate(secIn.sOperDate), operSql.sqlCodes(secIn.sPortCode), true, false);
     		   alSecurity= stgSecurity.getPartInsideChangeGoodsStatData(YssFun.toDate(secIn.sOperDate));//先统计当日的库存，这里先不统计当日的综合业务数据
     		   StgSecRecPay stgSecrecPay =new StgSecRecPay(); //证券应收应付库存
     		   stgSecrecPay.setYssPub(pub);
     		   stgSecrecPay.setStatCodes(secIn.sSecurityCode);
     		   stgSecrecPay.initStorageStat(YssFun.toDate(secIn.sOperDate), YssFun.toDate(secIn.sOperDate), operSql.sqlCodes(secIn.sPortCode), true, false);
     		   alSecRecPay = stgSecrecPay.getPartInsideChangeGoodsStatData(YssFun.toDate(secIn.sOperDate));//先统计当日的库存，这里先不统计当日的综合业务数据
     		   for(int i=0;i<alSecurity.size();i++){
     			  securitystorage=(SecurityStorageBean)alSecurity.get(i);
     			  if(securitystorage.getStrSecurityCode().equalsIgnoreCase(secIn.sSecurityCode) &&
     					 securitystorage.getStrPortCode().equalsIgnoreCase(secIn.sPortCode) &&
     					securitystorage.getStrStorageDate().equalsIgnoreCase(secIn.sOperDate) &&
     					securitystorage.getStrFAnalysisCode1().equalsIgnoreCase(secIn.sAnalysisCode1) &&
     					securitystorage.getStrFAnalysisCode2().equalsIgnoreCase(secIn.sAnalysisCode2) &&
     					securitystorage.getStrFAnalysisCode3().equalsIgnoreCase(secIn.sAnalysisCode3) &&
     					securitystorage.getAttrCode().equalsIgnoreCase(secIn.attrClsCode) &&
     					securitystorage.getInvestType().equalsIgnoreCase(secIn.investType))
     				  break;
     			  else
     				 securitystorage = null;
     		   }
     		   for(int i=0;i<alSecRecPay.size();i++){
    			   secrecpaybal =(SecRecPayBalBean)alSecRecPay.get(i);
    			   if(secrecpaybal.getSSecurityCode().equalsIgnoreCase(secIn.sSecurityCode)&&
    					   secrecpaybal.getSPortCode().equalsIgnoreCase(secIn.sPortCode)&&
    					   YssFun.formatDate(secrecpaybal.getDtStorageDate()).equalsIgnoreCase(secIn.sOperDate)&&
    					   secrecpaybal.getSAnalysisCode1().equalsIgnoreCase(secIn.sAnalysisCode1)&&
    					   secrecpaybal.getSAnalysisCode2().equalsIgnoreCase(secIn.sAnalysisCode2)&&
    					   secrecpaybal.getSAnalysisCode3().equalsIgnoreCase(secIn.sAnalysisCode3)&&
    					   secrecpaybal.getAttrClsCode().equalsIgnoreCase(secIn.attrClsCode)&&
    					   secrecpaybal.getSTsfTypeCode().equalsIgnoreCase("06")&&
    					   secrecpaybal.getSInvestType().equalsIgnoreCase(secIn.investType) &&
    					   secrecpaybal.getSSubTsfTypeCode().equalsIgnoreCase("06FI"))
    				   break;
    			   else
    				   secrecpaybal= null;  
    		   }
     		   if(secrecpaybal!=null){
     			  double dSecStorAmout = 0;
     			  bFlag = true; //有业务类型为内部转货（'81'）的流出
     			 if (securitystorage!=null&&securitystorage.getStrStorageAmount() != null &&
     					securitystorage.getStrStorageAmount().trim().length() >
                         0) {
                        dSecStorAmout = YssFun.toDouble(securitystorage.
                              getStrStorageAmount()); //库存数量
                        dAmoutRatio = YssD.div(Math.abs(secIn.dAmount),dSecStorAmout); //流出数量/库存数量
                     }
                 //---add by songjie 2011.04.15 BUG 1664 QDV4太平2010年10月27日01_B---//
     			 //若当笔流出数量加上当日录入的已审核的流出数量之和等于前一日的库存数量，则用轧差算法计算证券应收应付数据的金额
                 if(checkedSec != null && ((dSecStorAmout + checkedSec.getAmount() + secIn.dAmount) == 0)){
                     this.copyBeanToSecPecPay(secPecPayData,
                    		 secrecpaybal,
                             secIn.iInOutType,dAmoutRatio, checkedSec);
                 }else{
                     this.copyBeanToSecPecPay(secPecPayData,
                    		 secrecpaybal,
                             secIn.iInOutType,dAmoutRatio,null);
                 }
                 //---add by songjie 2011.04.15 BUG 1664 QDV4太平2010年10月27日01_B---//
     		   }else{
     			  TradeSubBean tradeSubFilter = new TradeSubBean();//查询条件
                  TradeSubBean tradeSubData = new TradeSubBean();//交易子表对象,主要是从交易子表中获取交易数量
                  tradeSubData.setYssPub(pub);
                  tradeSubFilter.setSecurityCode(secIn.
                        sSecurityCode); //证券代码
                  tradeSubFilter.setPortCode(secIn.sPortCode); //投资组合
                  tradeSubFilter.setBargainDate(secIn.sOperDate); //业务日期
                  tradeSubFilter.setInvMgrCode(secIn.
                        sAnalysisCode1); //投资经理
                  tradeSubFilter.setBrokerCode(secIn.
                        sAnalysisCode2); //券商代码
                  tradeSubFilter.setIsOnlyColumns("0");//如果不设置这个，则只是取出表头，没有任何数据
                  tradeSubFilter.setAutoSettle("1");
                  tradeSubFilter.setTradeCode("01");//这里只查买入的交易 by leeyu 20100417
                  tradeSubData.setFilterType(tradeSubFilter);
                  tradeSubData.getSettingByFilter();//得到交易子表对象
//                  double dAmoutRatio = 0;
                  if (tradeSubData.getSecurityCode() != null &&
                      tradeSubData.getSecurityCode().trim().length() > 0) { //交易子表有数据才需要保存
                     bFlag = true; //有业务类型为内部转货（'81'）的流出,后面根据这个标志进行保存相关流入数据
                     dAmoutRatio = YssD.div(Math.abs(secIn.dAmount),
                                            tradeSubData.getTradeAmount()); //流出数量/总数量
                     //下面要从应收应付表中获取数据。根据dAmoutRatio计算出对应各种金额，然后保存一条流入和流出数据
                     SecPecPayBean secPecPayFilter = new SecPecPayBean();
                     secPecPayFilter.setStrSecurityCode(secIn.sSecurityCode);
                     secPecPayFilter.setStrPortCode(secIn.sPortCode);
                     secPecPayFilter.setTransDate(YssFun.toDate(secIn.sOperDate));
                     secPecPayFilter.setInvMgrCode(secIn.sAnalysisCode1);
                     secPecPayFilter.setBrokerCode(secIn.sAnalysisCode2);
                     secPecPayFilter.setIsOnlyColumns("0"); //取所有数据，而不是表头
                     //----- MS00800 -------------------------
						  secPecPayFilter.setStrTsfTypeCode("06");
						  //----- MS00800  end --------------------
                     secPecPayFilter.setStrSubTsfTypeCode("06FI_B");
                     secPecPayData.setYssPub(pub);
                     secPecPayData.setFilterType(secPecPayFilter);
                     secPecPayData.getSetting(); //得到应收应付数据
						  //------MS00800 ------------------------------------------------
                     secPecPayData.setStrSubTsfTypeCode("06FI");//将06FI_B改为06FI
                     secPecPayData.setInvestType(secIn.investType);//添加投资类型 by leeyu 20100812 合并太平版本调整
						  //------MS00800 ------------------------------------------------
                     if (secPecPayData.getStrSecurityCode() != null &&
                         secPecPayData.getStrSecurityCode().trim().length() > 0) {
                        //有应收应付数据，根据dAmoutRatio设置各项金额
                         //------------20100123邵宏伟  此处的应收应付是交易数据，不用再乘数量了 End
                        secPecPayData.setMoney(YssD.round(
                              secPecPayData.getMoney(), 4)); //保留4位小数
                        secPecPayData.setMMoney(YssD.round(
                              secPecPayData.getMMoney(), 4));
                        secPecPayData.setVMoney(YssD.round(
                              secPecPayData.getVMoney(), 4));
                        secPecPayData.setBaseCuryMoney(YssD.round(
                              secPecPayData.getBaseCuryMoney(), 4));
                        secPecPayData.setMBaseCuryMoney(YssD.round(
                              secPecPayData.getMBaseCuryMoney(), 4));
                        secPecPayData.setVBaseCuryMoney(YssD.round(
                              secPecPayData.getVBaseCuryMoney(), 4));
                        secPecPayData.setPortCuryMoney(YssD.round(
                              secPecPayData.getPortCuryMoney(), 4));
                        secPecPayData.setMPortCuryMoney(YssD.round(
                              secPecPayData.getMPortCuryMoney(), 4));
                        secPecPayData.setVPortCuryMoney(YssD.round(
                              secPecPayData.getVPortCuryMoney(), 4));
                        secPecPayData.setMoneyF(YssD.round(
                              secPecPayData.getMoneyF(), 15));
                        secPecPayData.setBaseCuryMoneyF(YssD.round(
                              secPecPayData.getBaseCuryMoneyF(), 15));
                        secPecPayData.setPortCuryMoneyF(YssD.round(
                              secPecPayData.getPortCuryMoneyF(), 15));
                        //------------20100123邵宏伟End
                        secPecPayData.setInOutType(secIn.iInOutType);
                        secPecPayData.setStrSubTsfTypeName(""); //只保存CODE，不保存NAME
                        secPecPayData.setStrTsfTypeName("");
                        secPecPayData.setOldTransDate(YssFun.toDate(secIn.sOperDate));
                        secPecPayData.setStartDate(YssFun.toDate(secIn.sOperDate));
                        secPecPayData.setEndDate(YssFun.toDate(secIn.sOperDate));
                        secPecPayData.setDesc("");//防止为NULL
                     }
                  }//end
     		   }
     		  if(secPecPayData.getStrSecurityCode()!=null&&secPecPayData.getStrSecurityCode().trim().length()>0){//有数据时才需要保存
     			  secPecPayData.setBaseCuryRate(secIn.getDBaseCuryRate());//QDV4中保2010年6月09日01_B MS01290 by leeyu 20100706 将汇率放进去 合并太平版本代码
     			  secPecPayData.setPortCuryRate(secIn.getDPortCuryRate());//QDV4中保2010年6月09日01_B MS01290 by leeyu 20100706 将汇率放进去 合并太平版本代码
                  tempBuf.append(secPecPayData.buildRowStrForParse()).
                        append(
                        "\f\f"); //保存流出数据
                           String sKey = "";
                  sKey = secIn.sOperDate + "\f\f" + secIn.sPortCode +
                        "\f\f" +
                        secIn.sSecurityCode + "\f\f" +
                        Math.abs(secIn.dAmount); //数量取绝对值。因为流出为负，流入为正
                  if (!hmSecPec.containsKey(sKey)) {
                	  secPecPayData.setBaseCuryRate(secIn.getDBaseCuryRate());//这里将汇率放进去 by leeyu QDV4中保2010年4月14日02_B MS01092 20100419
                	  secPecPayData.setPortCuryRate(secIn.getDPortCuryRate());//这里将汇率放进去 by leeyu QDV4中保2010年4月14日02_B MS01092 20100419
                     hmSecPec.put(sKey, secPecPayData); //将证券应收应付数据保存，这是流出数据。在后面保存流入时要用到此中数据
                  }
               }
     	   }else if (1 == secIn.iInOutType) { //为流入时，将对象保存到ArrayList中，在后面要保存这些流入到证券应收应付表
               arraySecIn.add(secIn);
           }
		} catch (Exception ex) {
		    //edit by songjie 2011.04.16 BUG 1664 QDV4太平2010年10月27日01_B
			throw new YssException (ex.getMessage());
		}
 	}
    /**
     * getTreeViewData1 用于前台Tree型显示1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * buildRowStr 后台数据整编协议
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sNum).append("\t");
        buf.append(this.sSubNum).append("\t");
        buf.append(this.iInOutType).append("\t");
        buf.append(this.sInOutTypeName).append("\t");
        buf.append(this.sExchangeDate).append("\t");
        buf.append(this.sOperDate).append("\t");
        buf.append(this.sSecurityCode).append("\t");
        buf.append(this.sSecurityName).append("\t");
        buf.append(this.sRelaNum).append("\t");
        buf.append(this.sNumType).append("\t");
        buf.append(this.sNumTypeName).append("\t");
        buf.append(this.sTradeTypeCode).append("\t");
        buf.append(this.sTradeTypeName).append("\t");
        buf.append(this.sPortCode).append("\t");
        buf.append(this.sPortName).append("\t");
        buf.append(this.sAnalysisCode1).append("\t");
        buf.append(this.sAnalysisName1).append("\t");
        buf.append(this.sAnalysisCode2).append("\t");
        buf.append(this.sAnalysisName2).append("\t");
        buf.append(this.sAnalysisCode3).append("\t");
        buf.append(this.sAnalysisName3).append("\t");
        buf.append(this.dAmount).append("\t");
        buf.append(this.dCost).append("\t");
        buf.append(this.dMCost).append("\t");
        buf.append(this.dVCost).append("\t");
        buf.append(this.dBaseCost).append("\t");
        buf.append(this.dMBaseCost).append("\t");
        buf.append(this.dVBaseCost).append("\t");
        buf.append(this.dPortCost).append("\t");
        buf.append(this.dMPortCost).append("\t");
        buf.append(this.dVPortCost).append("\t");
        buf.append(this.dBaseCuryRate).append("\t");
        buf.append(this.dPortCuryRate).append("\t");
        buf.append(this.sSecExDesc).append("\t");
        buf.append(this.sDesc).append("\t");

        //--MS00007 add by songjie 2009-03-13
        buf.append(this.sTsfTypeCode).append("\t");     //拼接调拨类型代码
        buf.append(this.sTsfTypeName).append("\t");     //拼接调拨类型名称
        buf.append(this.sSubTsfTypeCode).append("\t");  //拼接调拨子类型代码
        buf.append(this.sSubTsfTypeName).append("\t");  //拼接调拨子类型名称
        //--MS00007 add by songjie 2009-03-13

        // MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
        buf.append(this.investType).append("\t");
        buf.append(this.attrClsCode).append("\t");
        buf.append(this.attrClsName).append("\t");
        //--------------add by wangzuochun 2009.08.14------------

        /**shashijie 2011-08-16 STORY 1202*/
        buf.append(this.FAltogetherCash).append("\t");
        buf.append(this.FApieceCash).append("\t");
        buf.append(this.FCashAccCode).append("\t");
        buf.append(this.FMatureDate).append("\t");
        buf.append(this.FCashAccName).append("\t");
        /**end*/
        buf.append(super.buildRecLog());

        return buf.toString();
    }

    /**
     * getOperValue 根据 sType处理与获取数据
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String sqlStr = "";
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
            if (sType.equalsIgnoreCase("getRelaNum")) {
                sqlStr = " select FRelaNum from " +
                    pub.yssGetTableName("Tb_Data_Integrated") +
                    " where FNumType=" + dbl.sqlString(filterType.sNumType) +
                    " and FNum= " + dbl.sqlString(filterType.sNum);
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    buf.append(rs.getString("FRelaNum")).append(",");
                }
                if (buf.length() == 0) {
                    buf.append("null");
                }
                
                //edit by yanghaiming 20101022 点击查看数据时不应该产生日志
                // ---增加批量删除的日志记录功能----guojianhua add 20100906-------//
//                SecIntegratedBean data=null;
//                data = new SecIntegratedBean();
//				data.setYssPub(pub);
//				data = this;
//                logOper = SingleLogOper.getInstance();
//				if (this.checkStateId == 2) {
//					logOper.setIData(data, YssCons.OP_DEL, pub);
//				} else if (this.checkStateId == 1) {
//					data.checkStateId = 1;
//					logOper.setIData(data, YssCons.OP_AUDIT, pub);
//				} else if (this.checkStateId  == 0) {
//					data.checkStateId = 0;
//					logOper.setIData(data, YssCons.OP_AUDIT, pub);
//				}
                // -----------------------------------------//
                
            } else if (sType.equalsIgnoreCase("getCost")) {
                //BugId:MS00055 20081201 王晓光 证券成本在流出时，算出来的原币的成本不对。（现在取的是T-2日库存，加上T-1日交易数据）
                getCost(sExchangeDate, dBaseCuryRate, dPortCuryRate);
                buf.append(this.buildRowStr());
            } /**shashijie 2011-08-20 STORY 1202 */
              else if (sType.equalsIgnoreCase("getCostFlowInto")) {
                getCostFlowInto(sExchangeDate, dBaseCuryRate, dPortCuryRate);
                buf.append(this.buildRowStr());
              /**end*/
            } 
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //添加关闭结果集方法 by leeyu 2009-01-08
        }
        return buf.toString();
    }

	/**
     * parseRowStr 解析前台传过来的数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                sSecStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.sNum = reqAry[0];
            this.sSubNum = reqAry[1];
            if (YssFun.isNumeric(reqAry[2])) {
                this.iInOutType = YssFun.toInt(reqAry[2]);
            }
            this.sSecurityCode = reqAry[3];
            if (reqAry[3].length() == 0) {
                this.sSecurityCode = " ";
            }
            this.checkStateId = YssFun.toInt(reqAry[4]);
            this.sExchangeDate = reqAry[5];
            this.sOperDate = reqAry[6];
            this.sTradeTypeCode = reqAry[7];
            this.sRelaNum = reqAry[8];
            if (this.sRelaNum.trim().length() == 0) {
                this.sRelaNum = " ";
            }
            this.sNumType = reqAry[9];
            if (this.sNumType.trim().length() == 0) {
                this.sNumType = " ";
            }
            this.sPortCode = reqAry[10];
            this.sAnalysisCode1 = reqAry[11];
            if (this.sAnalysisCode1.trim().length() == 0) {
                this.sAnalysisCode1 = " ";
            }
            this.sAnalysisCode2 = reqAry[12];
            if (this.sAnalysisCode2.trim().length() == 0) {
                this.sAnalysisCode2 = " ";
            }
            this.sAnalysisCode3 = reqAry[13];
            if (this.sAnalysisCode3.trim().length() == 0) {
                this.sAnalysisCode3 = " ";
            }
            if (YssFun.isNumeric(reqAry[14])) {
                this.dAmount = YssFun.toDouble(reqAry[14]);// * iInOutType;//QDV4中保2009年09月02日06_B MS00668 更改综合业务中的流入流出方向的问题，防止成本、数量与方向不一致 数据不需在这里乘以方向,因为前台已经处理过了
            }
            if (YssFun.isNumeric(reqAry[15])) {
                this.dCost = YssFun.toDouble(reqAry[15]) ;//* iInOutType;//QDV4中保2009年09月02日06_B MS00668 更改综合业务中的流入流出方向的问题，防止成本、数量与方向不一致 数据不需在这里乘以方向,因为前台已经处理过了
            }
            if (YssFun.isNumeric(reqAry[16])) {
                this.dMCost = YssFun.toDouble(reqAry[16]);// * iInOutType;//QDV4中保2009年09月02日06_B MS00668 更改综合业务中的流入流出方向的问题，防止成本、数量与方向不一致 数据不需在这里乘以方向,因为前台已经处理过了
            }
            if (YssFun.isNumeric(reqAry[17])) {
                this.dVCost = YssFun.toDouble(reqAry[17]);// * iInOutType;//QDV4中保2009年09月02日06_B MS00668 更改综合业务中的流入流出方向的问题，防止成本、数量与方向不一致 数据不需在这里乘以方向,因为前台已经处理过了
            }
            if (YssFun.isNumeric(reqAry[18])) {
                this.dBaseCost = YssFun.toDouble(reqAry[18]);// * iInOutType;//QDV4中保2009年09月02日06_B MS00668 更改综合业务中的流入流出方向的问题，防止成本、数量与方向不一致 数据不需在这里乘以方向,因为前台已经处理过了
            }
            if (YssFun.isNumeric(reqAry[19])) {
                this.dMBaseCost = YssFun.toDouble(reqAry[19]);// * iInOutType;//QDV4中保2009年09月02日06_B MS00668 更改综合业务中的流入流出方向的问题，防止成本、数量与方向不一致 数据不需在这里乘以方向,因为前台已经处理过了
            }
            if (YssFun.isNumeric(reqAry[20])) {
                this.dVBaseCost = YssFun.toDouble(reqAry[20]);// * iInOutType;//QDV4中保2009年09月02日06_B MS00668 更改综合业务中的流入流出方向的问题，防止成本、数量与方向不一致 数据不需在这里乘以方向,因为前台已经处理过了
            }
            if (YssFun.isNumeric(reqAry[21])) {
                this.dPortCost = YssFun.toDouble(reqAry[21]) ;//* iInOutType;//QDV4中保2009年09月02日06_B MS00668 更改综合业务中的流入流出方向的问题，防止成本、数量与方向不一致 数据不需在这里乘以方向,因为前台已经处理过了
            }
            if (YssFun.isNumeric(reqAry[22])) {
                this.dMPortCost = YssFun.toDouble(reqAry[22]);// * iInOutType;//QDV4中保2009年09月02日06_B MS00668 更改综合业务中的流入流出方向的问题，防止成本、数量与方向不一致 数据不需在这里乘以方向,因为前台已经处理过了
            }
            if (YssFun.isNumeric(reqAry[23])) {
                this.dVPortCost = YssFun.toDouble(reqAry[23]) ;//* iInOutType;//QDV4中保2009年09月02日06_B MS00668 更改综合业务中的流入流出方向的问题，防止成本、数量与方向不一致 数据不需在这里乘以方向,因为前台已经处理过了
            }
            if (YssFun.isNumeric(reqAry[24])) {
                this.dBaseCuryRate = YssFun.toDouble(reqAry[24]);
            }
            if (YssFun.isNumeric(reqAry[25])) {
                this.dPortCuryRate = YssFun.toDouble(reqAry[25]);
            }
            this.sSecExDesc = reqAry[26];
            this.sDesc = reqAry[27];
            isOnlyColumns = reqAry[28];
            this.sOldNum = reqAry[29];
            this.sOldSubNum = reqAry[30];
            this.sTsfTypeCode = reqAry[31];     //获取调拨类型代码 MS00007 add by songjie 2009-03-13
            this.sSubTsfTypeCode = reqAry[32];  //获取调拨子类型代码 MS00007 add by songjie 2009-03-13

            //MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
            this.investType = reqAry[33];   //投资类型
            this.attrClsCode = reqAry[34];  //属性代码
            //-------------add by wangzuochun 2009.08.14------------

            /**shashijie 2011-08-16 STORY 1202 */
            if (YssFun.isNumeric(reqAry[35])) {
                this.FAltogetherCash = YssFun.toDouble(reqAry[35]);
            }
            if (YssFun.isNumeric(reqAry[36])) {
                this.FApieceCash = YssFun.toDouble(reqAry[36]);
            }
            this.FCashAccCode = reqAry[37];
            this.FMatureDate = reqAry[38];
            /**end*/
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0 &&
                !sRowStr.split("\r\t")[1].equals("[null]")) {
                if (this.filterType == null) {
                    this.filterType = new SecIntegratedBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析证券兑换数据出错", e);
        }
    }

    /**
     * 与ParseRowStr()解析顺序一致的方法，整合成字符串
     * add by xuxuming,20090831 MS00473,QDV4国泰2009年6月01日01_A
     * @return String
     */
    public String buildRowStrForParse() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sNum).append("\t");
        buf.append(this.sSubNum).append("\t");
        buf.append(this.iInOutType).append("\t");
        buf.append(this.sSecurityCode).append("\t");
        buf.append(this.checkStateId).append("\t");
        buf.append(this.sExchangeDate).append("\t");
        buf.append(this.sOperDate).append("\t");
        buf.append(this.sTradeTypeCode).append("\t");
        buf.append(this.sRelaNum).append("\t");
        buf.append(this.sNumType).append("\t");
        buf.append(this.sPortCode).append("\t");
        buf.append(this.sAnalysisCode1).append("\t");
        buf.append(this.sAnalysisCode2).append("\t");
        buf.append(this.sAnalysisCode3).append("\t");
        buf.append(this.dAmount).append("\t");
        buf.append(this.dCost).append("\t");
        buf.append(this.dMCost).append("\t");
        buf.append(this.dVCost).append("\t");
        buf.append(this.dBaseCost).append("\t");
        buf.append(this.dMBaseCost).append("\t");
        buf.append(this.dVBaseCost).append("\t");
        buf.append(this.dPortCost).append("\t");
        buf.append(this.dMPortCost).append("\t");
        buf.append(this.dVPortCost).append("\t");
        buf.append(this.dBaseCuryRate).append("\t");
        buf.append(this.dPortCuryRate).append("\t");
        buf.append(this.sSecExDesc).append("\t");
        buf.append(this.sDesc).append("\t");
        buf.append(this.isOnlyColumns).append("\t");
        buf.append(this.sOldNum).append("\t");
        buf.append(this.sOldSubNum).append("\t");
        buf.append(this.sTsfTypeCode).append("\t");     //拼接调拨类型代码
        buf.append(this.sSubTsfTypeCode).append("\t");  //拼接调拨子类型代码
        buf.append(this.investType).append("\t");
        buf.append(this.attrClsCode).append("\t");
        /**shashijie 2011-08-16 STORY 1202*/
        buf.append(this.FAltogetherCash).append("\t");
        buf.append(this.FApieceCash).append("\t");
        buf.append(this.FCashAccCode).append("\t");
        buf.append(this.FMatureDate).append("\t");
        /**end*/
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * getBeforeEditData 获得编辑前的数据
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    /***
     * 获取后台的成本
     * String storageDate 库存日期 这里应取前一天的日期,因为当日还没有统计
     * double dBaseCuryRate 基础货币汇率
     * double dPortCuryRate 组合货币汇率
     */
    private void getCost(String storageDate, double dBaseCuryRate,
                         double dPortCuryRate) throws YssException {
        YssCost cost = null;
        try {
        	//story 1936 20111224 by zhouwei 
        	//综合业务数据证券成本，计算流出成本如果考虑当天的交易（包括综合业务成本）需要在库存统计时重新计算成本
        	 CtlPubPara pubpara = new CtlPubPara();
             pubpara.setYssPub(pub);
             String isNeed = pubpara.getAvgCost(this.sPortCode);
             if(isNeed.equals("Yes(Integrated)")){
            	 this.dCost = 0;
                 this.dMCost = 0;
                 this.dVCost = 0;
                 this.dBaseCost = 0;
                 this.dMBaseCost = 0;
                 this.dVBaseCost = 0;
                 this.dPortCost = 0;
                 this.dMPortCost = 0;
                 this.dVPortCost = 0;
            	 return;
             }
             //------------------------end------------
            ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().
                getBean(
                    "avgcostcalculate");
            //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
            //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
            costCal.initCostCalcutate(YssFun.toDate(storageDate),
                                      sPortCode,
                                      (sAnalysisCode1 == null ||
                                       sAnalysisCode1.length() == 0 ? " " :
                                       sAnalysisCode1),
                                      (sAnalysisCode2 == null ||
                                       sAnalysisCode2.length() == 0 ? " " :
                                       sAnalysisCode2),
                                       attrClsCode);
            costCal.setYssPub(pub);
            costCal.setIfRefreshIntegrated(true);
            cost = costCal.getCarryCost(sSecurityCode,  
                                        this.dAmount,
                                        YssFun.left(sNum +
                "", (sNum + "").length() - 5),
                                        dBaseCuryRate,
                                        dPortCuryRate);
            costCal.roundCost(cost, 4);//保留四位小数  QDV4中保2010年5月14日01_B by leeyu 20100520
            this.dCost = cost.getCost();
            this.dMCost = cost.getMCost();
            this.dVCost = cost.getVCost();
            this.dBaseCost = cost.getBaseCost();
            this.dMBaseCost = cost.getBaseMCost();
            this.dVBaseCost = cost.getBaseVCost();
            this.dPortCost = cost.getPortCost();
            this.dMPortCost = cost.getPortMCost();
            this.dVPortCost = cost.getPortVCost();
        } catch (Exception e) {
            throw new YssException("获取成本信息出错" + "\r\n" + e.getMessage(), e);
        }

    }

    /***
     * 根据 结果集给各变量赋值
     */
    private void setResult(ResultSet rs) throws SQLException, YssException {
        this.sNum = rs.getString("FNum");
        this.sSubNum = rs.getString("FSubNum");
        this.iInOutType = rs.getInt("FInOutType");
        this.sInOutTypeName = rs.getString("FInOutTypeName");
        this.sSecurityCode = rs.getString("FSecurityCode");
        this.sSecurityName = rs.getString("FSecurityName");
        this.sExchangeDate = YssFun.formatDate(rs.getDate("FExchangeDate"),
                                               "yyyy-MM-dd");
        this.sOperDate = YssFun.formatDate(rs.getDate("FOperDate"),
                                           "yyyy-MM-dd");
        this.sRelaNum = rs.getString("FRelaNum");
        this.sNumType = rs.getString("FNumType");

        this.sTradeTypeCode = rs.getString("FTradeTypeCode");
        this.sTradeTypeName = rs.getString("FTradeTypeName");
        this.sPortCode = rs.getString("FPortCode");
        this.sPortName = rs.getString("FPortName");
        this.sAnalysisCode1 = rs.getString("FAnalysisCode1");
        this.sAnalysisName1 = rs.getString("FAnalysisName1");
        this.sAnalysisCode2 = rs.getString("FAnalysisCode2");
        this.sAnalysisName2 = rs.getString("FAnalysisName2");
        this.sAnalysisCode3 = rs.getString("FAnalysisCode3");

        this.dAmount = rs.getDouble("FAmount");
        this.dCost = rs.getDouble("FExchangeCost");
        this.dMCost = rs.getDouble("FMExCost");
        this.dVCost = rs.getDouble("FVExCost");
        this.dPortCost = rs.getDouble("FPortExCost");
        this.dMPortCost = rs.getDouble("FMPortExCost");
        this.dVPortCost = rs.getDouble("FVPortExCost");
        this.dBaseCost = rs.getDouble("FBaseExCost");
        this.dMBaseCost = rs.getDouble("FMBaseExCost");
        this.dVBaseCost = rs.getDouble("FVBaseExCost");
        this.dBaseCuryRate = rs.getDouble("FBaseCuryRate");
        this.dPortCuryRate = rs.getDouble("FPortCuryRate");
        this.sDesc = rs.getString("FDesc");
        this.sSecExDesc = rs.getString("FSecExDesc");

        this.sTsfTypeCode = rs.getString("FTsfTypeCode");       //设置调拨类型代码
        this.sTsfTypeName = rs.getString("FTsfTypeName");       //设置调拨类型名称
        this.sSubTsfTypeCode = rs.getString("FSubTsfTypeCode"); //设置调拨子类型代码
        this.sSubTsfTypeName = rs.getString("FSubTsfTypeName"); //设置调拨子类型名称

        //MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
        this.investType = rs.getString("FInvestType");      //设置投资类型
        this.attrClsCode = rs.getString("FATTRCLSCODE");    //设置属性分类代码
        this.attrClsName = rs.getString("FAttrClsName");    //设置属性分类名称
        //-----------------2009-08-14 add by wangzuochun-----------
        
        /**shashijie 2011-08-16 STORY 1202*/
        this.FAltogetherCash = rs.getDouble("FAltogetherCash");
        this.FApieceCash = rs.getDouble("FApieceCash");
        this.FCashAccCode = rs.getString("FCashAccCode");
        if (rs.getString("FMatureDate")==null || rs.getString("FMatureDate").trim().equals("")) {
        	this.FMatureDate = YssFun.formatDate("9998-12-31","yyyy-MM-dd");
		} else {
			this.FMatureDate = YssFun.formatDate(rs.getString("FMatureDate"),"yyyy-MM-dd");
		}
        this.FCashAccName = rs.getString("FCashAccName");
        /**end*/
        
        super.setRecLog(rs);  //add by zhangjun 2012-06-11 BUG4752在“回收站”中成功清除数据后，已审核”、“未审核”界面中的数据都显示在“回收站”中了 
        
        
    }

    /***
     * SQL的查询条件
     */

    private String buildSql() throws YssException {
        String sqlStr = "";
        try {
            if (this.filterType != null) {
                if (filterType.isOnlyColumns == null ||
                    filterType.isOnlyColumns.equals("0")) {
                	//20091203,edit by xuxuming,因为股指期货估值时存入在成本中的数据有关联编号,且类型为FutruesTrade
                	//MS00831    删除股指期货交易数据时没有删除综合业务中的数据  
                	//MS00929  银行间债券交易成本的关联编号类型为 IBB 20100327 蒋锦 修改
                	//xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
                	//MS001125 银行间回购交易成本关联编号为OPurRE liuwei 
                	// modify by wangzuochun 2010.07.06  增加关联编号类型：securitymanage  MS01359    “综合业务”中无法浏览到一些场外证券业务的证券成本等数据    QDV4国内(测试)2010年06月25日04_B  
                	//edit by sognjie 2011.09.01 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A 添加查询条件 FNumType = 'openfund'                	
                    sqlStr = " where 1=1 and ((a.FRelaNum=' ' and a.FNumType=' ') or (a.FNumType = 'securitymanage') or (a.FNumType = 'FutruesTrade') or (a.FNumType = 'IBB') or(a.FNumType = 'OptionsTrade') or (a.FNumType = 'AutoDropOpRight') or (a.FNumType='OPurRE') or (a.FNumType='openfund')) "; //将关联的除掉
                } else {
                	//modify by zhangjun 2012.06.11 BUG4752在“回收站”中成功清除数据后，已审核”、“未审核”界面中的数据都显示在“回收站”中了 
                	if(!bool){
                		sqlStr = " where 1=2";
                	}
                	//sqlStr = " where 1=2";
                	//modify by zhangjun 2012.06.11 BUG4752在“回收站”中成功清除数据后，已审核”、“未审核”界面中的数据都显示在“回收站”中了 
                }
                if (filterType.sSecurityCode != null &&
                    filterType.sSecurityCode.trim().length() > 0) {
                    sqlStr += " and a.FSecurityCode like '" +
                        filterType.sSecurityCode.replaceAll("'", "''") + "%'";
                }
                if (filterType.sPortCode != null &&
                    filterType.sPortCode.length() > 0) {
                    sqlStr += " and a.FPortCode like '" +
                        filterType.sPortCode.replaceAll("'", "''") + "%'";
                }
                if (filterType.sTradeTypeCode != null &&
                    filterType.sTradeTypeCode.length() > 0) {
                    sqlStr += " and a.FTradeTypeCode like '" +
                        filterType.sTradeTypeCode.replaceAll("'", "''") + "%'";
                }

                if (filterType.sExchangeDate != null &&
                    !filterType.sExchangeDate.equals("9998-12-31")) {
                    sqlStr += " and a.FExchangeDate =" +
                        dbl.sqlDate(filterType.sExchangeDate);
                }
                if (filterType.sNum != null && filterType.sNum.length() > 0) {
                    sqlStr += " and a.FNum =" + dbl.sqlString(filterType.sNum); //详细列表显示专用
                }
            }
            return sqlStr;
        } catch (Exception e) {
            throw new YssException("证券兑换的筛选条件出错!", e);
        }
    }

    /***
     * MS00007 宋洁 2009-03-03
     * 应保存运营应收应付
     * 保存除证券成本外的其他信息,如证券应收应付,现金成本,现金应收应付
     * sNumType :关联类型 sDatas :数据集 sRelaNum :关联编号
     */
    private void saveRelaDatas(String sNumType, String SDatas, String sRelaNum,
                               String sNewNum, String sDesc) throws
        YssException {
        PreparedStatement pst = null;
        String[] arrRelaNum = sRelaNum.split("\t");
        String[] arrData = SDatas.split("\f\f");
        String strSql = "insert into " +
            pub.yssGetTableName("Tb_Data_Integrated") +
            " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
            " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
            " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
            //MS00007 edit by songjie 2009-03-13 表结构添加了调拨类型代码和调拨子类型代码两个字段
            " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,FCheckState,FCreator,FCreateTime,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode) " +
            //MS00007 edit by songjie 2009-03-13 表结构添加了调拨类型代码和调拨子类型代码两个字段 所以添加了，？，？
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; //MS00007 edit by songjie 2009-03-13
        //add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        try {
        	//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
        	integrateAdmin.setYssPub(pub);
        	
            if (SDatas.trim().length() == 0) {
                return;
            }
            pst = dbl.openPreparedStatement(strSql);
            for (int i = 0; i < arrData.length; i++) {
            	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//                this.sSubNum = sNewNum +
//                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Integrated"),
//                                           dbl.sqlRight("FSubNUM", 5),
//                                           "00000",
//                                           " where FNum =" + dbl.sqlString(sNewNum));
            	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
                //add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                this.sSubNum = integrateAdmin.getKeyNum();
                pst.setString(1, sNewNum);      //取前面的
                pst.setString(2, this.sSubNum);
                pst.setInt(3, 0);
                pst.setString(4, " ");
                pst.setDate(5, YssFun.toSqlDate(this.sExchangeDate)); //sj edit  20080626.所有分页的数据的兑换日前统一为业务日期。

                //-----------为了在没有证券成本的时候，向综合业务中插入有正确日期信息的关联信息。sj edit 20080620 ---//
                if (sNumType.equalsIgnoreCase("SecRecPay")) {
                    pst.setDate(6, YssFun.toSqlDate(arrData[i].split("\t")[1]));
                } else if (sNumType.equalsIgnoreCase("Cash")) {
                    pst.setDate(6, YssFun.toSqlDate(arrData[i].split("\t")[5]));
                } else if (sNumType.equalsIgnoreCase("CashRecPay")) {
                    pst.setDate(6, YssFun.toSqlDate(arrData[i].split("\t")[1]));
                }
                else if (sNumType.equalsIgnoreCase("InvestRecPay")) {
                    pst.setDate(6, YssFun.toSqlDate(arrData[i].split("\t")[1]));
                }

                pst.setString(7, sTradeTypeCode); //把业务设置代码存入表中 MS00007 add by songjie 2009-03-13
                pst.setString(8, arrRelaNum[i]);
                pst.setString(9, sNumType);
                pst.setString(10, " ");
                pst.setString(11, " ");
                pst.setString(12, " ");
                pst.setString(13, " ");
                pst.setDouble(14, 0.0);
                pst.setDouble(15, 0.0);
                pst.setDouble(16, 0.0);
                pst.setDouble(17, 0.0);
                pst.setDouble(18, 0.0);
                pst.setDouble(19, 0.0);
                pst.setDouble(20, 0.0);
                pst.setDouble(21, 0.0);
                pst.setDouble(22, 0.0);
                pst.setDouble(23, 0.0);
                pst.setDouble(24, 0.0);
                pst.setDouble(25, 0.0);
                pst.setString(26, sSecExDesc);
                pst.setString(27, sDesc);
                pst.setInt(28, pub.getSysCheckState() ? 0 : 1);
                pst.setString(29, this.creatorCode);
                pst.setString(30, this.creatorTime);
                pst.setString(31, " ");
                pst.setString(32, " ");

                //------------2009-07-07 蒋锦 添加 属性分类--------------//
                //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
                pst.setString(33, " ");
                //-----------------------------------------------------//
                pst.executeUpdate();
            }
        } catch (Exception e) {
            throw new YssException("保存关联表出错", e);
        } finally {
            dbl.closeStatementFinal(pst); //添加关闭预处理方法 by leeyu 2009-01-08
        }
    }

    /***
     * 修改证券应收应付表,资金调拨表,现金应收应付表的状态
     * MS00007 宋洁 2009-03-03
     * 多加了一个参数，用于传入运营应收应付信息
     * MS00007 edit by songjie 2009.03.19
     * 应添加修改运营应收应付表的状态
     */
    private void changeCheckID(String secRecPayNums, String cashNums,String cashRecPayNums,
                               String investRecPayNums, int iCheckState) throws YssException {
        String sqlStr = "";
        try {
            //证券应收应付的处理
            sqlStr = " update " + pub.yssGetTableName("Tb_data_SecRecPay") +
                " set FCheckState=" + iCheckState + ",FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum in(" + operSql.sqlCodes(secRecPayNums) + ")";
            dbl.executeSql(sqlStr);

            //资金调拨主表的处理
            sqlStr = " update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " set FCheckState=" + iCheckState + ",FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum in(" + operSql.sqlCodes(cashNums) + ")";
            dbl.executeSql(sqlStr);

            //资金调拨子表的处理
            sqlStr = " update " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " set FCheckState=" + iCheckState + ",FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum in(" + operSql.sqlCodes(cashNums) + ")";
            dbl.executeSql(sqlStr);

            //现金应收应付的处理
            sqlStr = " update " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " set FCheckState=" + iCheckState + ",FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum in(" + operSql.sqlCodes(cashRecPayNums) + ")";
            dbl.executeSql(sqlStr);

            //运营应收应付的处理
            //当综合业务表中的运营应收应付信息审核状态更新时，同时更新运营应收应付表中相关的信息的审核状态
            sqlStr = " update " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " set FCheckState=" + iCheckState + ",FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum in(" + operSql.sqlCodes(investRecPayNums) + ")";
            dbl.executeSql(sqlStr);
        } catch (Exception e) {
            throw new YssException(e.toString());
        }
    }

    public String storageAnalysis() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_Security);
        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                 
                    
                    " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                    i +
                    " from  " +
                    pub.yssGetTableName("tb_para_broker") +
                    " y where  y.FCheckState = 1) broker on a.FAnalysisCode" +
                    i + " = broker.FBrokerCode";
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("003")) {
                    sResult = sResult +
                        " left join (select m.FExchangeCode ,m.FExchangeName  as FAnalysisName" +
                        i +
                        " from  (select FExchangeCode from tb_base_exchange " +

                        " where  FCheckState = 1 group by FExchangeCode )x " +
                        " join (select * from tb_base_exchange " +
                        ") m on x.FExchangeCode = m.FExchangeCode) exchange on a.FAnalysisCode" +
                        i + " = exchange.FExchangeCode";

                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                     /*   " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                        i +
                        "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        "join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr on a.FAnalysisCode" +
                        i + " = invmgr.FInvMgrCode ";
                    */
                    
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                    i +
                    "  from  " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where  n.FCheckState = 1) invmgr on a.FAnalysisCode" +
                    i + " = invmgr.FInvMgrCode ";
                
                    //end by lidaolong
                } else {
                    sResult = sResult +
                        " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i +
                        " = tn" +
                        i + ".FAnalysisNull ";
                }
            }
        }
        dbl.closeResultSetFinal(rs); //添加关闭结果集方法 by leeyu 2009-01-08
        return sResult;

    }

    /**
     * 筛选条件
     * add by xuxuming,20090831 MS00473,QDV4国泰2009年6月01日01_A
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
        if (this.filterType != null) {
            sResult = " where 1=1 ";

            //组合代码
            if (filterType.sPortCode != null && !"".equals(filterType.sPortCode)) {
                sResult += " and a.FPortCode = " + dbl.sqlString(filterType.sPortCode); //调整为等于，合并太平版本代码
//                	sResult + " and a.FPortCode like '" +
//                    filterType.sPortCode.replaceAll("'", "''") + "%'";
            }
            //交易类型
            if (filterType.sTradeTypeCode != null && !"".equals(filterType.sTradeTypeCode)) {
                sResult += " and a.FTradeTypeCode = " + dbl.sqlString(filterType.sTradeTypeCode);//调整为等于，合并太平版本代码
//                	sResult + " and a.FTradeTypeCode like '" +
//                    filterType.sTradeTypeCode.replaceAll("'", "''") + "%'";
            }
            //投资类型
  /*          if (filterType.investType != null && !"".equals(filterType.investType)) {
                sResult = sResult + " and a.FInvestType like '" +
                    filterType.investType.replaceAll("'", "''") + "%'";
            }*/
            //证券代码
            if (filterType.sSecurityCode != null && !"".equals(filterType.sSecurityCode)) {
				sResult = sResult + " and a.FSecurityCode = '" +//调整为等于，合并太平版本代码
                    	filterType.sSecurityCode.replaceAll("'", "''") + "'";
                //sResult = sResult + " and a.FSecurityCode like '" +
                    //filterType.sSecurityCode.replaceAll("'", "''") + "%'";
            }
            //业务日期
            if (filterType.sOperDate != null && !"".equals(filterType.sOperDate) &&
                !this.filterType.sOperDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FOperDate = " +
                    dbl.sqlDate(filterType.sOperDate);
            }
            //兑换日期
            if (filterType.sExchangeDate != null &&! "".equals(filterType.sExchangeDate) &&
                !this.filterType.sExchangeDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FExchangeDate = " +
                    dbl.sqlDate(filterType.sExchangeDate);
            }
            //分类属性
            if (filterType.attrClsCode != null && !"".equals(filterType.attrClsCode)) {
                sResult += " and a.FAttrClsCode = " + dbl.sqlString(filterType.attrClsCode);//调整为等于，合并太平版本代码
//                	sResult + " and a.FAttrClsCode like '" +
//                    filterType.attrClsCode.replaceAll("'", "''") + "%'";
            }
            //投资经理 合并太平版本代码 这里添加上分析代码1
            if (filterType.sAnalysisCode1 != null && ! "".equalsIgnoreCase(filterType.sAnalysisCode1)) {
            	sResult += " and a.FANALYSISCODE1 = " + dbl.sqlString(filterType.sAnalysisCode1);
            }
            //券商 合并太平版本代码 这里添加上分析代码2
            if (filterType.sAnalysisCode2 != null && ! "".equalsIgnoreCase(filterType.sAnalysisCode2)) {
               sResult += " and a.FANALYSISCODE2 = " + dbl.sqlString(filterType.sAnalysisCode2);
            }

        }
        return sResult;
    }
    /**
     * 针对批量转货业务，计算转换的利息
     * @param secIn SecIntegratedBean
     * @throws YssException
     */
    public void autoCalcuFees1(SecIntegratedBean secIn, double accruedInterest) throws YssException{
		SecRecPayBalBean secRecPayBalData = new SecRecPayBalBean(); // 对应　证券应收应付库存　表
		SecPecPayBean secPecPayData = new SecPecPayBean(); // 对应　证券应收应付款表
		secRecPayBalData.setYssPub(pub);
		StgCash stgCash = new StgCash();
		CashAccountBean cashAccountBean = new CashAccountBean();
		double dAmoutRatio = 0;// 根据此值来设置转出的利息；由转出的数量计算得到。
		double baseMoney = 0;
		double portMoney = 0;
		String newNum = "";
		if (-1 == secIn.iInOutType) { // 为流出时才能从证券应收应付库存查询到相应记录
			// 当证券应收应付库存中没有对应数据时，有可能是当天买入就作该（转货）业务，此时库存中没有;要从应收应付表中取数据，再根据转货数量算出金额，然后再保存一条流入和流出
			TradeSubBean tradeSubFilter = new TradeSubBean();// 查询条件
			TradeSubBean tradeSubData = new TradeSubBean();// 交易子表对象,主要是从交易子表中获取交易数量
			tradeSubData.setYssPub(pub);
			tradeSubFilter.setSecurityCode(secIn.sSecurityCode); // 证券代码
			tradeSubFilter.setPortCode(secIn.sPortCode); // 投资组合
			tradeSubFilter.setBargainDate(secIn.sOperDate); // 业务日期
			tradeSubFilter.setInvMgrCode(secIn.sAnalysisCode1); // 投资经理
			tradeSubFilter.setBrokerCode(secIn.sAnalysisCode2); // 券商代码
			tradeSubFilter.setIsOnlyColumns("0");// 如果不设置这个，则只是取出表头，没有任何数据
			tradeSubFilter.setAutoSettle("1");
			tradeSubData.setFilterType(tradeSubFilter);
			tradeSubData.getSettingByFilter();// 得到交易子表对象
			// double dAmoutRatio = 0;
			if (tradeSubData.getSecurityCode() != null
					&& tradeSubData.getSecurityCode().trim().length() > 0) { // 交易子表有数据才需要保存
				bFlag = true; // 有业务类型为内部转货（'81'）的流出,后面根据这个标志进行保存相关流入数据
				newNum = "SRP" +
                YssFun.formatDate(YssFun.toDate(secIn.getSOperDate()),
                                  "yyyyMMdd") +
                dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_SecRecPay"),
                                       dbl.sqlRight("FNUM", 9),
                                       "000000001",
                                       " where FTRANSDATE=" +
                                       dbl.sqlDate(secIn.getSOperDate()) +
                                       " or FTRANSDATE=" +
                                 //dbl.sqlDate("9998-12-31"));
                                 //这里将FNUM做为条件，原因是之前的产生的编号没有按业务日期生成，造成条件相同时产生编号错误 QDV4中保2009年08月21日01_B  MS00643  by leeyu
                                 dbl.sqlDate("9998-12-31")+" or FNum like 'SRP"+
                                 YssFun.formatDate(YssFun.toDate(secIn.getSOperDate()),
                                 "yyyyMMdd")+"%'");
				secPecPayData.setStrNum(newNum);
				secPecPayData.setTransDate(YssFun.toDate(secIn.getSOperDate()));
				secPecPayData.setStrPortCode(secIn.getSPortCode());
				secPecPayData.setInvMgrCode(tradeSubData.getInvMgrCode());
				secPecPayData.setBrokerCode(tradeSubData.getBrokerCode());
				// secPecPayData.setStrOldFAnalysisCode3(secIn.getSAnalysisCode3());
				secPecPayData
						.setStrSecurityCode(tradeSubData.getSecurityCode());
				// ? secPecPayData.setCatTypeCode()
				secPecPayData.setAttrClsCode("FI");
				secPecPayData.setStrTsfTypeCode("06");
				secPecPayData.setStrSubTsfTypeCode("06FI");
				secPecPayData.setStrCuryCode(tradeSubData
						.getCuryCode(tradeSubData.getSecurityCode()));
				secPecPayData.setInOutType(secIn.iInOutType);
				secPecPayData.setMoney(accruedInterest);
				secPecPayData.setMMoney(accruedInterest);
				secPecPayData.setVMoney(accruedInterest);
				stgCash.setYssPub(pub);
				baseMoney = stgCash.getSettingOper().calBaseMoney(
						accruedInterest, tradeSubData.getBaseCuryRate());
				secPecPayData.setMBaseCuryMoney(baseMoney);
				secPecPayData.setVBaseCuryMoney(baseMoney);
				secPecPayData.setBaseCuryMoney(baseMoney);
				secPecPayData.setInvestType(secIn.investType);//添加投资类型 by leeyu 20100812 合并太平版本调整
				cashAccountBean.setYssPub(pub);
				cashAccountBean.setStrCashAcctCode(tradeSubData
						.getCashAcctCode());
				cashAccountBean.getSetting();
				portMoney = stgCash.getSettingOper().calPortMoney(
						accruedInterest, tradeSubData.getBaseCuryRate(),
						tradeSubData.getPortCuryRate(),
						cashAccountBean.getStrCurrencyCode(),
						YssFun.toDate(tradeSubData.getBargainDate()),
						tradeSubData.getPortCode());
				secPecPayData.setPortCuryMoney(portMoney);
				secPecPayData.setMPortCuryMoney(portMoney);
				secPecPayData.setVPortCuryMoney(portMoney);
				secPecPayData.setCheckState(1);
				secPecPayData.setExchangeCode(" ");
				secPecPayData.setBaseCuryRate(tradeSubData.getBaseCuryRate());
				secPecPayData.setPortCuryRate(tradeSubData.getPortCuryRate());

				secPecPayData.setStrSubTsfTypeName(""); // 只保存CODE，不保存NAME
				secPecPayData.setStrTsfTypeName("");
				secPecPayData.setOldTransDate(YssFun.toDate(secIn.sOperDate));
				secPecPayData.setStartDate(YssFun.toDate(secIn.sOperDate));
				secPecPayData.setEndDate(YssFun.toDate(secIn.sOperDate));
				secPecPayData.setDesc("");// 防止为NULL
			}

			if (secPecPayData.getStrSecurityCode() != null
					&& secPecPayData.getStrSecurityCode().trim().length() > 0) {// 有数据时才需要保存
				secPecPayData.setBaseCuryRate(secIn.getDBaseCuryRate());//QDV4中保2010年6月09日01_B MS01290 by leeyu 20100706 将汇率放进去 合并太平版本代码
    			secPecPayData.setPortCuryRate(secIn.getDPortCuryRate());//QDV4中保2010年6月09日01_B MS01290 by leeyu 20100706 将汇率放进去 合并太平版本代码
				tempBuf.append(secPecPayData.buildRowStrForParse()).append(
						"\f\f"); // 保存流出数据
				String sKey = "";
				sKey = secIn.sOperDate + "\f\f" + secIn.sPortCode + "\f\f"
						+ secIn.sSecurityCode + "\f\f"
						+ Math.abs(secIn.dAmount); // 数量取绝对值。因为流出为负，流入为正
				if (!hmSecPec.containsKey(sKey)) {
					hmSecPec.put(sKey, secPecPayData); // 将证券应收应付数据保存，这是流出数据。在后面保存流入时要用到此中数据
				}
			}
		} else if (1 == secIn.iInOutType) { // 为流入时，将对象保存到ArrayList中，在后面要保存这些流入到证券应收应付表
			arraySecIn.add(secIn);
		}

    }

    public double getDAmount() {
      return dAmount;
    }

    public double getDBaseCost() {
       return dBaseCost;
    }

    public double getDBaseCuryRate() {
        return dBaseCuryRate;
    }

    public double getDCost() {
        return dCost;
    }

    public double getDMBaseCost() {
        return dMBaseCost;
    }

    public double getDMCost() {
        return dMCost;
    }

    public double getDPortCost() {
        return dPortCost;
    }

    public double getDPortCuryRate() {
        return dPortCuryRate;
    }

    public double getDVBaseCost() {
        return dVBaseCost;
    }

    public double getDMPortCost() {
        return dMPortCost;
    }

    public double getDVCost() {
        return dVCost;
    }

    public double getDVPortCost() {
        return dVPortCost;
    }

    public SecIntegratedBean getFilterType() {
        return filterType;
    }

    public int getIInOutType() {
        return iInOutType;
    }

    public String getSAnalysisCode1() {
        return sAnalysisCode1;
    }

    public String getSAnalysisCode2() {
        return sAnalysisCode2;
    }

    public String getSAnalysisCode3() {
        return sAnalysisCode3;
    }

    public String getSExchangeDate() {
        return sExchangeDate;
    }

    public String getSOperDate() {
        return sOperDate;
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public String getSTradeTypeCode() {
        return sTradeTypeCode;
    }

    public String getSSecurityCode() {
        return sSecurityCode;
    }

    public String getSNumType() {
         return sNumType;
    }

    public String getSRelaNum() {
        return sRelaNum;
    }
    public String getSOldNum(){
        return sOldNum;
    }

    /**
     * 获取业务设置名称
     * @return String
     */
    public String getSBusinessTypeName() {
        return sBusinessTypeName;
    }

    /**
     * 获取业务设置代码
     * @return String
     */
    public String getSBusinessTypeCode() {
        return sBusinessTypeCode;
    }

    /**
     * 获取调拨类型名称
     * @return String
     */
    public String getSTsfTypeName() {
        return sTsfTypeName;
    }

    /**
     * 获取调拨类型代码
     * @return String
     */
    public String getSTsfTypeCode() {
        return sTsfTypeCode;
    }

    /**
     * 获取调拨子类型名称
     * @return String
     */
    public String getSSubTsfTypeName() {
        return sSubTsfTypeName;
    }

    /**
     * 获取调拨子类型代码
     * @return String
     */
    public String getSSubTsfTypeCode() {
        return sSubTsfTypeCode;
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    public String getInvestType() {
        return investType;
    }

    public String getAttrClsName() {
        return attrClsName;
    }

    public void setSOldNum(String sOldNum) {
        this.sOldNum = sOldNum;
    }

    public void setFilterType(SecIntegratedBean filterType) {
        this.filterType = filterType;
    }

    public void setDAmount(double dAmount) {
        this.dAmount = dAmount;
    }

    public void setDBaseCost(double dBaseCost) {
        this.dBaseCost = dBaseCost;
    }

    public void setDBaseCuryRate(double dBaseCuryRate) {
        this.dBaseCuryRate = dBaseCuryRate;
    }

    public void setDCost(double dCost) {
        this.dCost = dCost;
    }

    public void setDMBaseCost(double dMBaseCost) {
        this.dMBaseCost = dMBaseCost;
    }

    public void setDMCost(double dMCost) {
        this.dMCost = dMCost;
    }

    public void setDMPortCost(double dMPortCost) {
        this.dMPortCost = dMPortCost;
    }

    public void setDPortCost(double dPortCost) {
        this.dPortCost = dPortCost;
    }

    public void setDPortCuryRate(double dPortCuryRate) {
        this.dPortCuryRate = dPortCuryRate;
    }

    public void setDVBaseCost(double dVBaseCost) {
        this.dVBaseCost = dVBaseCost;
    }

    public void setDVCost(double dVCost) {
        this.dVCost = dVCost;
    }

    public void setDVPortCost(double dVPortCost) {
        this.dVPortCost = dVPortCost;
    }

    public void setIInOutType(int iInOutType) {
        this.iInOutType = iInOutType;
    }

    public void setSAnalysisCode1(String sAnalysisCode1) {
        this.sAnalysisCode1 = sAnalysisCode1;
    }

    public void setSAnalysisCode2(String sAnalysisCode2) {
        this.sAnalysisCode2 = sAnalysisCode2;
    }

    public void setSAnalysisCode3(String sAnalysisCode3) {
        this.sAnalysisCode3 = sAnalysisCode3;
    }

    public void setSExchangeDate(String sExchangeDate) {
        this.sExchangeDate = sExchangeDate;
    }

    public void setSOperDate(String sOperDate) {
        this.sOperDate = sOperDate;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setSSecurityCode(String sSecurityCode) {
        this.sSecurityCode = sSecurityCode;
    }

    public void setSTradeTypeCode(String sTradeTypeCode) {
        this.sTradeTypeCode = sTradeTypeCode;
    }

    public void setSNumType(String sNumType) {
        this.sNumType = sNumType;
    }

    public void setSRelaNum(String sRelaNum) {
        this.sRelaNum = sRelaNum;
    }

    /**
     * 设置业务设置名称
     * @param sBusinessTypeName String
     */
    public void setSBusinessTypeName(String sBusinessTypeName) {
        this.sBusinessTypeName = sBusinessTypeName;
    }

    /**
     * 设置业务设置代码
     * @param sBusinessTypeCode String
     */
    public void setSBusinessTypeCode(String sBusinessTypeCode) {
        this.sBusinessTypeCode = sBusinessTypeCode;
    }

    /**
     * 设置调拨类型名称
     * @param sTsfTypeName String
     */
    public void setSTsfTypeName(String sTsfTypeName) {
        this.sTsfTypeName = sTsfTypeName;
    }

    /**
     * 设置调拨类型代码
     * @param sTsfTypeCode String
     */
    public void setSTsfTypeCode(String sTsfTypeCode) {
        this.sTsfTypeCode = sTsfTypeCode;
    }

    /**
     * 设置调拨子类型名称
     * @param sSubTsfTypeName String
     */
    public void setSSubTsfTypeName(String sSubTsfTypeName) {
        this.sSubTsfTypeName = sSubTsfTypeName;
    }

    /**
     * 设置调拨子类型代码
     * @param sSubTsfTypeCode String
     */
    public void setSSubTsfTypeCode(String sSubTsfTypeCode) {
        this.sSubTsfTypeCode = sSubTsfTypeCode;
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public String getsNum() {
		return sNum;
	}

	public void setsNum(String sNum) {
		this.sNum = sNum;
	}

	public void setInvestType(String investType) {
        this.investType = investType;
    }
	
	public void setAttrClsName(String attrClsName) {
        this.attrClsName = attrClsName;
    }
	//---------邵宏伟20100122修改 增加描述
    public String getDesc() {
      return sDesc;
   }

   public void setDesc(String sDesc) {
      this.sDesc = sDesc;
	}
    //---------邵宏伟20100122修改 增加描述

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
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

	public double getFAltogetherCash() {
		return FAltogetherCash;
	}

	public void setFAltogetherCash(double fAltogetherCash) {
		FAltogetherCash = fAltogetherCash;
	}

	public String getFMatureDate() {
		return FMatureDate;
	}

	public void setFMatureDate(String fMatureDate) {
		FMatureDate = fMatureDate;
	}

	public String getFCashAccCode() {
		return FCashAccCode;
	}

	public void setFCashAccCode(String fCashAccCode) {
		FCashAccCode = fCashAccCode;
	}

	public double getFApieceCash() {
		return FApieceCash;
	}
	
	public void setFApieceCash(double fApieceCash) {
		FApieceCash = fApieceCash;
	}

    /** 获取兑换方向为流入的成本
     * @param sExchangeDate 库存日期
     * @param dBaseCuryRate 基础汇率
     * @param dPortCuryRate 组合汇率
     * @author shashijie ,2011-8-20 , STORY 1202
     * @modified 
     */
    private void getCostFlowInto(String sExchangeDate, double dBaseCuryRate,
			double dPortCuryRate) throws YssException {
    	ResultSet rs = null;
        try {
        	//获取估值方法设置的行情价(收盘价)
        	String param = getPriceByMTVMethod();
        	if (param==null || param.trim().equals("")) { return; }
        	//根据证券代码获取最近行情
            String strSql = getStrSqlPrice(param,sExchangeDate);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
				setObjectCost(rs,dBaseCuryRate,dPortCuryRate,param);
			}
        }
        catch (Exception e) {
            throw new YssException("获取流入时的成本信息出错" + "\r\n" + e.getMessage(), e);
        } finally {
        	dbl.closeResultSetFinal(rs);
        }
	}

	/**根据组合获取估值方法设置的行情价(收盘价)
	 * @author shashijie ,2011-8-20 , STORY 1202
	 * @modified 
	 */
	private String getPriceByMTVMethod() throws YssException {
		ResultSet rs = null;
		String param = "";
		try {
			String sqlString = "select b.FMTvCode, b.FMktPriceCode From "+
				pub.yssGetTableName("Tb_Para_MTVMethod")+" b"+ 
				" Join (select c.FMTvCode, c.FMktPriceCode From "+
				pub.yssGetTableName("Tb_Para_MTVMethod")+//--组合关联表
				" c where c.FMtvCode in (select d.FMtvCode From "+
				pub.yssGetTableName("Tb_Para_MTVMethodLink")+//--估值方法连接表
				" d where d.FLinkCode = "+dbl.sqlString(sSecurityCode)+" )) e on b.FMTvCode = e.FMTvCode "+
				" where b.Fmtvcode in (select a.FSubCode From "+pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")+
				" a where a.FRelaType = 'MTV' and a.FPortCode = "+dbl.sqlString(sPortCode)+" )";
			rs = dbl.openResultSet(sqlString);
			while (rs.next()) {
				//参数名"如收盘价(FClosingPrice)
				param = rs.getString("FMktPriceCode");
			}
		} catch (Exception e) {
			throw new YssException("获取估值方法设置估值行情时出错" + "\r\n" + e.getMessage(), e);
        } finally {
        	dbl.closeResultSetFinal(rs);
        }
		return param;
	}

	/**获取最新行情sql
	 * @param dDate 日期
	 * @param securityCode 证券代码
	 * @author shashijie ,2011-8-20 , STORY 1202
	 * @modified 
	 */
	private String getStrSqlPrice(String FPrice ,String dDate) {
		String strSql = "select a3.FSecuritycode, a3."+FPrice+", a3.FMktValueDate From "+
				pub.yssGetTableName("Tb_Data_MarketValue")+" a3, "+ //--最新行情
			    " (select max(a7.FMktValueDate) as FMktValueDate, a7.FSecuritycode From "+
			    pub.yssGetTableName("Tb_Data_MarketValue")+" a7 where a7.FMktValueDate <= "+dbl.sqlDate(dDate)+
			    " Group by a7.FSecurityCode) a8 where a3.FCheckstate = 1 and a8.FSecuritycode = a3.FSecuritycode "+
			    " and a3.FMktValueDate = a8.FMktValueDate and a3.FSecurityCode = "+dbl.sqlString(sSecurityCode);
		return strSql;
	}

	/**计算成本,管理成本,估值成本
	 * @param rs
	 * @param dBaseCuryRate 基础汇率
	 * @param dPortCuryRate 组合汇率
	 * @param param 参数(收盘价)
	 * @author shashijie ,2011-8-20 , STORY 1202  
	 * @modified 
	 */
	private void setObjectCost(ResultSet rs,double dBaseCuryRate,double dPortCuryRate , 
			String param) throws YssException,SQLException {
		//成本
		setCost(rs.getDouble(param), dAmount);
		//基础成本
        setBaseCost(rs.getDouble(param), dAmount,dBaseCuryRate);
        //组合(本位币)成本
        setPortCost(rs.getDouble(param), dAmount,dBaseCuryRate,dPortCuryRate);
	}

	/**(最近)行情*流入数量*基础汇率/组合汇率  = 组合成本
	 * @param FPrice 行情
	 * @param amount 数量
	 * @param dBaseCuryRate 基础汇率
	 * @param dPortCuryRate 组合汇率
	 * @author shashijie ,2011-8-20 , STORY 1202
	 * @modified 
	 */
	private void setPortCost(double FPrice, double amount,double dBaseCuryRate,
			double dPortCuryRate) {
		double portCost = YssD.div(YssD.mul(FPrice, amount,dBaseCuryRate),dPortCuryRate);
		this.dPortCost = portCost;
		this.dMPortCost = portCost;
		this.dVPortCost = portCost;
	}

	/**(最近)行情*流入数量*基础汇率  = 基础成本
	 * @param FPrice 行情
	 * @param amount 数量
	 * @param dBaseCuryRate 基础汇率
	 * @author shashijie ,2011-8-20 , STORY 1202
	 * @modified 
	 */
	private void setBaseCost(double FPrice, double amount ,
			double dBaseCuryRate) {
		//基础成本
		double baseCost = YssD.mul(FPrice, amount,dBaseCuryRate);
		this.dBaseCost = baseCost;
		this.dMBaseCost = baseCost;//管理
		this.dVBaseCost = baseCost;//估值
	}

	/**(最近)行情*流入数量  = 成本
	 * @param FPrice 行情
	 * @param amount 数量
	 * @author shashijie ,2011-8-20 , STORY 1202
	 * @modified 
	 */
	private void setCost(double FPrice, double amount) {
		double cost = YssD.mul(FPrice, amount);
		this.dCost = cost;
		this.dMCost = cost;//管理
		this.dVCost = cost;//估值
	}

	public String getFCashAccName() {
		return FCashAccName;
	}

	public void setFCashAccName(String fCashAccName) {
		FCashAccName = fCashAccName;
	}

    
} //end Class
