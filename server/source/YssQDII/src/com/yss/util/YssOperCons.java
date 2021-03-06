package com.yss.util;

import java.util.ArrayList;
import java.util.HashMap;

public final class YssOperCons {
    public YssOperCons() {
    }
    
    //集群支持 在这里定义全局变量
   // public static ArrayList vctUser = new ArrayList(); //在线用户统计功能，只要储存session即可
    public static HashMap vctUser = new HashMap();//在线用户统计功能，只要储存session即可
    public static HashMap hashmapLog = new HashMap(); //统计用户登录密码错误次数
    public static HashMap hmOnLineUser= new HashMap(); //当前在线的用户   
    public static HashMap hmEnforceUser= new HashMap();
    
    
    //ETF 结转类型  20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
    public static final String YSS_ETF_OVERTYPE_DEALDAYNUM = "dealdaynum";//补票完成日
    public static final String YSS_ETF_OVERTYPE_SGDEALREPLACE = "sgdealreplace";//申购应付替代结转
    public static final String YSS_ETF_OVERTYPE_SHDEALREPLACE = "shdealreplace";// 赎回应付替代款结转
    public static final String YSS_ETF_OVERTYPE_LASTDEALDAYNUM = "lastestdealdaynum";//强制处理日
    public static final String YSS_ETF_OVERTYPE_SHREPLACEINS = "shreplaceover";//赎回现金替代结转
    
    //ETF 基金补票方式  MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
    public static final String YSS_ETF_MAKEUP_SUB = "3";//轧差补票(按比例)
    public static final String YSS_ETF_MAKEUP_SUB_ORDER = "4";//轧差补票(按先到先补)
    public static final String YSS_ETF_MAKEUP_TIMESUB = "5";//实时加轧差
    public static final String YSS_ETF_MAKEUP_MUST = "6";//轧差补票_强退
    public static final String YSS_ETF_MAKEUP_NO = "7";//无补票（工银瑞信）
    public static final String YSS_ETF_MAKEUP_TIMEAVERAGE= "8";//实时加均摊（华夏）
    public static final String YSS_ETF_MAKEUP_ONE= "9";//钆差补票一次性补完（易方达）
    public static final String YSS_ETF_MAKEUP_GCJQPJ= "10";//轧差+加权平均（国泰）//and by fangjiang 2013.01.08 STORY #3402

    //存款业务
    public static final String YSS_SAVING_BUY = "buy";              //买入
    public static final String YSS_SAVING_FIRST = "first";          //首期
    public static final String YSS_SAVING_SELL = "sell";            //转出
    public static final String YSS_SAVING_CIRCUCATCH = "circucatch";//到期通知

    //交易所代码
    public static final String YSS_JYSDM_YHJ = "CY";    //银行间
    public static final String YSS_JYSDM_SHJYS = "CG";  //上海交所
    public static final String YSS_JYSDM_SZJYS = "CS";  //深交所

    //国内基金业务
    public static final String YSS_MONETARYFUN_INTEREST_SIMPLE = "simple";      //利息计算方式 单利
    public static final String YSS_MONETARYFUN_INTEREST_COMPOUND = "compound";  //利息计算方式 复利
    public static final String YSS_MONETARYFUN_INTEREST_TODAY = "today";        //取今日红利
    public static final String YSS_MONETARYFUN_INTEREST_YESTERDAY = "yesterday";    //取昨日红利
    public static final String YSS_MONETARYFUN_INTEREST_FUNDINVEST = "fundinvest";  //记入基金投资
    public static final String YSS_MONETARYFUN_INTEREST_RECRATE = "recrate";    //记入应收红利
    public static final String YSS_MONETARYFUN_CLOSETYPE_DAY = "day";           //日结型
    public static final String YSS_MONETARYFUN_CLOSETYPE_MONTH = "month";       //月结型
    public static final String YSS_SECRECPAY_RELATYPE_OPENFUND = "openfund";    //证券应收应付关联类型 开放式基金业务
    //add by songjie 2012.03.02 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A
    public static final String YSS_SECRECPAY_RELATYPE_DIVIDINVEST = "dividinvest";//应收应付关联类型：分红转投业务
    public static final String YSS_SECRECPAY_RELATYPE_SUBTRADE = "subtrade";    //场内业务资料
    public static final String YSS_SECRECPAY_RELATYPE_NEWISSUE = "newissue";    //新股新债业务
    public static final String YSS_SECRECPAY_RELATYPE_INTERBANKBOND = "interbankbond";  //银行间债券业务
    public static final String YSS_SECRECPAY_RELATYPE_DEVTRUSTBOND = "devtrustbond";    //债券转托管业务
    public static final String YSS_SECRECPAY_RELATYPE_SECBOWLEND="SecBowLend";	//证券借贷
    
    public static final String YSS_CASHRECPAY_RELATYPE_FIXEDFEE = "fixedTradeFee";    //add by zhouwei 20120321 固定交易费用
    public static final String YSS_INVESTRECPAY_RELATYPE_SECMAFEE="secManagedFee";//add by zhouwei 20120405 证券管理费用
    //股指期货核算类型
    public static final String YSS_FUTURES_ACCOUNTTYPE_FIFO = "FIFO";                       //先入先出
    public static final String YSS_FUTURES_ACCOUNTTYPE_MODAVG = "MODAVG";                   //移动加权平均
    public static final String YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO = "THEDAYFIRSTFIFO"; //当日优先先入先出

    //估值类型
    public static final String YSS_GZLX_SEC_FDYY = "SecsMV";            //证券浮动盈余
    public static final String YSS_GZLX_SEC_HDSY = "SecsFX";            //证券汇兑损益
    public static final String YSS_GZLX_CASH_HDSY = "CashFX";           //存款汇兑损益
    public static final String YSS_GZLX_INCOME_HDSY = "IncomeFX";       //综合损益汇兑损益
    public static final String YSS_GZLX_INDEX_HDSY = "IndexFutruesMV";  //期货
    public static final String YSS_GZLX_FOWARD_HDSY = "ValForwardMV";   //远期
    public static final String YSS_GZLX_OPT_ADDVALUE = "OptionsMV";     //期权估值增值

    //汇率类型
    public static final String YSS_RATE_BASE = "Base";      //基础汇率
    public static final String YSS_RATE_PORT = "Port";      //组合汇率
    public static final String YSS_RATE_MARK = "Mark";      //基准汇率
    public static final String YSS_RATEVAL_BASE = "VBase";  //估值基础汇率
    public static final String YSS_RATEVAL_PORT = "VPort";  //估值组合汇率

    //标识获得基础货币金额还是组合货币金额
    public static final String YSS_BASE_MONEY = "BaseMoney"; //基础货币金额
    public static final String YSS_PORT_MONEY = "PortMoney"; //基础货币金额

    //库存类型
    public static final String YSS_KCLX_Cash = "Cash";                  //现金库存
    public static final String YSS_KCLX_Security = "Security";          //证券库存
    public static final String YSS_KCLX_InvestPayRec = "InvestPayRec";  //运营收支款
    public static final String YSS_KCLX_TA = "TA";                      //TA库存

    //库存配置
    public static final String YSS_KCPZ_CatType = "004";    //品种类型
    public static final String YSS_KCPZ_Broker = "002";     //券商
    public static final String YSS_KCPZ_InvMgr = "001";     //投资经理
    public static final String YSS_KCPZ_Exchange = "003";   //交易地点

    //汇率来源
    public static final String YSS_RateSrc_Bloomberg = "01";    //彭博
    public static final String YSS_RateSrc_Reuter = "02";       //路透
    public static final String YSS_RateSrc_Caihua = "03";       //财华
    public static final String YSS_RateSrc_Wind = "04";         //万德

    //add by zhangfa 20101126 证券借贷交易数据 交易类型
    public static final String YSS_SECLEND_JYLX_Borrow="borrow";			//借入
    public static final String YSS_SECLEND_JYLX_Rcb="Rcb";					//借入归还
    public static final String YSS_SECLEND_JYLX_Drb="Drb";					//借入归还股利
    public static final String YSS_SECLEND_JYLX_BInPayDid="BInPayDid";		//借入应付股利
    public static final String YSS_SECLEND_JYLX_Rbsb ="Rbsb";				//借入归还送股
    public static final String YSS_SECLEND_JYLX_Awrb ="Awrb";				//借入归还配股权证
    
    public static final String YSS_SECLEND_JYLX_Loan="Loan";				//借出
    public static final String YSS_SECLEND_JYLX_Lr="Lr";					//借出召回
    public static final String YSS_SECLEND_JYLX_Rlr="Rlr";					//借出召回股利
    public static final String YSS_SECLEND_JYLX_LOutRecDid="LOutRecDid";	//借出应收股利
    public static final String YSS_SECLEND_JYLX_Mhlr="Mhlr";				//借出召回送股
    public static final String YSS_SECLEND_JYLX_Lpwr="Lpwr";				//借出召回配股权证
    
    
    //add by zhouxiang 2010.11.27 证券借贷交易类型定义----------------------------------
    public static final String Yss_ZJDBZLX_SEC_BOutRecSec = "BOutRecSec";  //借出应收送股
    public static final String Yss_ZJDBZLX_SEC_BInPaySec = "BInPaySec";    //借入应付送股
    public static final String YSS_SECLEND_JYLX__BInPayOP="BInPayOP";		//借入应付配股权证
    public static final String YSS_SECLEND_JYLX__BOutRecOP="BOutRecOP";		//借出应收配股权证
    
    public static final String YSS_SECLEND_JYLX__06LDID="06LDID";			//应收借出股利
    public static final String YSS_SECLEND_JYLX__07BDID="07BDID";			//应付借入股利
    
    
    //end by zhouxiang 2010.11.27 证券借贷交易类型定义---------------------------------- 

    //--------------end---------------------
   
    //交易类型
    public static final String YSS_JYLX_Buy = "01";             //买入
    public static final String YSS_JYLX_Sale = "02";            //卖出
    
    /** add by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
    public static final String YSS_OVERSELL_Buy = "01SS";             //买入（卖空）
    public static final String YSS_OVERSELL_Sale = "02SS";         //卖出（卖空） 
    /** -----end----- */
    //----------------------交易类型期权--------------------------------//
    public static final String YSS_JYLX_Excerise = "32FP";      //期权行权
    public static final String YSS_JYLX_Balance = "33FP";       //期权结算
    public static final String YSS_JYLX_DropExcerise = "34FP";  //期权放弃行权
    //--------xuqiji 20100429 MS01134 在现有的程序版本中增加指数期权及股票期权业务-------------------//
    public static final String YSS_JYLX_REGOU_BSTATEExercis = "92";//认购期权买入状态行权
    public static final String YSS_JYLX_REGOU_SSTATEExercis = "93";//认购期权卖出状态行权
    public static final String YSS_JYLX_REGU_BSTATEExercis = "94";//认沽期权买入状态行权
    public static final String YSS_JYLX_REGU_SSTATEExercis = "95";//认沽期权卖出状态行权
    public static final String YSS_TYCS_BAILMONEY_PCTRANSFER = "offcarry";//期权保证金结转通用参数--平仓结转
    public static final String YSS_TYCS_BAILMONEY_DAYTRANSFER = "everydaycarry";//期权保证金结转通用参数 --每日结转
    //期权核算类型
    public static final String YSS_OPTIONS_ACCOUNTTYPE_FIFO = "FIFO";                       //先入先出
    public static final String YSS_OPTIONS_ACCOUNTTYPE_MODAVG = "MODAVG";                   //移动加权平均
    //---------------------------end------------------------------//
    public static final String YSS_JYLX_ZF = "03";              //增发
    public static final String YSS_JYLX_RG = "04";              //认购
    public static final String YSS_JYLX_PS = "05";              //配售
    public static final String YSS_JYLX_PX = "06";              //分发派息
    public static final String YSS_JYLX_PXRC = "06ROC";         //分发派息（资本返还）
    public static final String YSS_JYLX_SG = "07";              //送股
    public static final String YSS_JYLX_DE = "119";             //缩股
    public static final String YSS_JYLX_PG = "08";              //配股
    public static final String YSS_JYLX_ZHGDQ = "09";           //正回购到期
    public static final String YSS_JYLX_NHGDQ = "10";           //逆回购到期
    public static final String YSS_JYLX_ZJCR = "13";            //资金存入
    public static final String YSS_JYLX_SGou = "15";            //申购
    public static final String YSS_JYLX_SH = "16";              //赎回
    /**shashijie 2012-4-27 STORY 2565 增加交易类型*/
    public static final String YSS_JYLX_ETFSGou = "106";        //ETF申购
    public static final String YSS_JYLX_ETFSH = "107";          //ETF赎回
    public static final String YSS_JYLX_ETFLJSGSB = "204";      //ETF联接申购失败
    public static final String YSS_JYLX_ETFLJSHSB = "205";      //ETF联接赎回失败
    public static final String YSS_JYLX_ETFSHTBK = "203";       //ETF联接赎回退补款
	//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 start---//
    public static final String YSS_JYLX_ETFSGTK = "202";       //ETF联接申购退补款
    public static final String YSS_JYLX_ETFSGBK = "206";       //ETF联接申购补款
	//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 end---//
    public static final String YSS_YWLX_ETFMC = "17";			//卖出估增
    public static final String YSS_YWLX_ETFSHSBGZ = "1703";		//赎回失败估值增值
    public static final String YSS_YWLX_ETFSHGZ = "1702";		//赎回估值增值
    public static final String YSS_YWLX_ETFMCGZ = "1704";		//ETF联接卖出估值增值
	/**end*/
    public static final String Yss_JYLX_ZQ = "17";              //债券兑付
    public static final String YSS_JYLX_KC = "20";              //开仓
    public static final String YSS_JYLX_PC = "21";              //平仓
    public static final String YSS_JYLX_QZSP = "22";            //权证送配
    public static final String YSS_JYLX_PGJK = "23";            //配股缴款
    public static final String YSS_JYLX_KFLSP = "84";           //可分离债送配
    public static final String YSS_JYLX_XJDJ = "85";            //现金对价
    public static final String YSS_JYLX_XJDJDZ = "86";          //现金对价到账
    public static final String YSS_JYLX_INVEST="34";			//分红转投　　//by guyichuan 20110515 STORY #741
  //add by zhangfa  MS01336    将债券派息的业务与分红派息业务的交易方式进行区分    
    public static final String YSS_JYLX_ZQPX="88";             //债券派息
    
    
  //------------------------------------------------------------------------  
    public static final String YSS_JYLX_PXDZ="061";             //分红派息到账
    public static final String YSS_JYLX_GFDJ="14";              //股份对价
    public static final String YSS_JYLX_ZRE = "24";             //正回购
    public static final String YSS_JYLX_NRE = "25";             //逆回购
    public static final String YSS_JYLX_RGUXQ = "30";           //认购行权
    public static final String YSS_JYLX_RGOXQ = "31";           //认沽行权
    public static final String Yss_JYLX_CYDQ = "19";            //债券持有到期
    //交易类型-股票
    public static final String YSS_JYLX_XGZQ = "11";    //新股中签
    public static final String YSS_JYLX_WZFK = "12";    //未中返款
    public static final String YSS_JYLX_XGSG = "40";    //新股申购
    public static final String YSS_JYLX_WSZQ = "41";    //网上中签
    public static final String YSS_JYLX_ZQFK = "42";    //中签返款
    public static final String YSS_JYLX_ZFZQ = "43";    //增发中签
    public static final String YSS_JYLX_WXZQ = "44";    //网下中签
    public static final String YSS_JYLX_SD = "45";      //锁定
    public static final String YSS_JYLX_XGLT = "46";    //新股流通
    public static final String YSS_JYLX_SPLITOFF="80";	//换股 by leeyu 20100813 合并太平版本调整
    public static final String YSS_JYLX_ZQDMBG = "87";  //证券代码变更

    //交易类型-股本
    public static final String YSS_JYLX_XGSZPS_SG="105";    //市值配售上海中签
    public static final String YSS_JYLX_EQYYSG ="26";       //要约收购

    //交易类型 - 债券
    public static final String YSS_JYLX_TRANSSEC="81";	//内部转货 by leeyu 20100813 合并太平版本调整
    public static final String YSS_JYLX_XZSG = "50";    //新债申购
    public static final String YSS_JYLX_XZWSZQ = "51";  //新债网上中签
    public static final String YSS_JYLX_XZZQFK = "52";  //新债中签返款
    public static final String YSS_JYLX_YHJZQCX = "57"; //银行间债券承销
    public static final String YSS_JYLX_ZZG = "58";     //债转股业务
    public static final String YSS_JYLX_ZQZTG = "59";   //债权转托管
    public static final String YSS_JYLX_ZQLGDPS = "60"; //债券老股东配售
    public static final String YSS_JYLX_KZZHS = "61";   //可转债回售
    public static final String YSS_JYLX_XZLT = "62"; //新债流通
    public static final String YSS_JYLX_PADCHANAGE = "90";  //溢折价变动
    public static final String YSS_JYLX_PADAMORT = "91";    //溢折价摊销
    
    //交易类型 - ETF基金 xuqiji 20091019
    public static final String YSS_JYLX_ETFSG = "68";    //ETF申购  
    public static final String YSS_JYLX_ETFSHH = "69";    //ETF赎回

    //交易类型 - 基金
    public static final String YSS_JYLX_TRETFTBK="70"; //ETF基金退补款

    //交易类型 - 回购
    public static final String YSS_JYLX_REMR="78";              //买断式买入回购
    public static final String YSS_JYLX_REMC="79";              //买断式卖出回购
    public static final String YSS_JYLX_ZREMATURITY = "241";    //正回购到期
    public static final String YSS_JYLX_NREMATURITY = "251";    //逆回购到期
    //add by zhangfa 20101126 证券借贷调拨类型
    public static final String YSS_SECLEND_DBLX_SecBCost="10";//证券借贷成本
    public static final String YSS_SECLEND_DBLX_BEAV="60";//借入估值增值
    //-----------end-------------------------
    //add by zhangfa 20101126 证券借贷调拨子类型
    public static final String YSS_SECLEND_SUBDBLX_BSC="10BSC";//借入股票成本
    public static final String YSS_SECLEND_SUBDBLX_BLC="10BLC";//借出股票成本
    public static final String YSS_SECLEND_SUBDBLX_PLI="07PLI";//应付借贷利息
    public static final String YSS_SECLEND_SUBDBLX_RLI="06RLI";//应收借贷利息   
    
    public static final String YSS_SECLEND_SUBDBLX_MBI="07MBI";//应付送股
    public static final String YSS_SECLEND_SUBDBLX_MHR="06MHR";//应收送股
    public static final String YSS_SECLEND_SUBDBLX_BI="09BI01";//借入股票估增
    public static final String YSS_SECLEND_SUBDBLX_AW="07AW";//应付配股权证
    public static final String YSS_SECLEND_SUBDBLX_BDID="07BDID";//应付借入股利
    public static final String YSS_SECLEND_SUBDBLX_LDID="06LDID";//应收借出股利
    public static final String YSS_SECLEND_SUBDBLX_6AW="06AW";//应收配股权证
    //---------end----------------------------
    //资金调拨类型
    public static final String YSS_ZJDBLX_InnerAccount = "01";  //内部现金帐户
    public static final String YSS_ZJDBLX_Income = "02";        //收入
    public static final String YSS_ZJDBLX_Fee = "03";           //费用
    public static final String YSS_ZJDBLX_Capital = "04";       //资本
    public static final String YSS_ZJDBLX_Cost = "05";          //成本
    public static final String YSS_ZJDBLX_Rec = "06";           //应收款项
    public static final String YSS_ZJDBLX_Pay = "07";           //应付款项
    public static final String YSS_ZJDBLX_FX = "99";            //汇兑损益
    public static final String YSS_ZJDBLX_MV = "09";            //估值增值
    public static final String YSS_ZJDBLX_RecUnAcc = "12";      //应收未清算款
    public static final String YSS_ZJDBLX_PayUnAcc = "13";      //应付未清算款
    public static final String YSS_ZJDBLX_BX = "15";            //本息
    public static final String YSS_ZJDBLX_PAYOUT = "16";        //支出
    public static final String YSS_ZJDBLX_AUTO = "29";          //内部生成的,无类型的类型 //调整调拨数据与子数据时用到
    public static final String YSS_ZJDBLX_Other = "999";        //其他
    public static final String Yss_ZJDBLX_Premium = "20";       //溢价
    public static final String Yss_ZJDBLX_Discounts = "21";     //折价
    public static final String Yss_ZJDBLX_Extension = "22";     //挂账
    public static final String Yss_ZJDBLX_Sell = "23";     		//销账
    public static final String Yss_ZJDBLX_REGULATE = "98";     		//调整金
    public static final String Yss_ZJDBLX_SUPPLEMENT = "97";	//计提补差 add by huangqirong 2013-02-01 story #3488
    
    //资金调拨子类型
    public static final String YSS_ZJDBZLX_TF = "07TF";  //应付交易费用
    public static final String YSS_ZJDBZLX_TF_EQ = "07TF_EQ";  //应付交易费用_股票  
    public static final String YSS_ZJDBZLX_TF_FI = "07TF_FI";  //应付交易费用_债券  
    public static final String YSS_ZJDBZLX_TF_DE = "07TF_DE";  //应付交易费用_存款   ADD BY ZHOUWEI 20120321
    public static final String YSS_ZJDBZLX_LXS_DE = "07LXS_DE";      //存款利息税   add by jiangshichao 20120228  
    
    public static final String YSS_ZJDBZLX_LXS_FI = "07LXS_FI";      //债券利息税   add by jiangshichao 20120228 
	public static final String YSS_ZJDBZLX_ZBLDS_FI = "07CGT_FI"; //资本利得税 add by jiangshichao 20120228
    public static final String YSS_ZJDBZLX_ZBLDS_EQ = "07CGT_EQ"; //资本利得税 add by jiangshichao 20120228
    public static final String YSS_ZJDBZLX_ZBLDS_TR = "07CGT_TR"; //资本利得税 add by jiangshichao 20120228 
    public static final String YSS_ZJDBZLX_PF_REC = "06PF";     //应收直销申购款利息
    public static final String YSS_ZJDBZLX_IV_Rec = "06IV";     //应收运营款项
    public static final String YSS_ZJDBZLX_IV_Pay = "07IV";     //应付运营款项
    public static final String YSS_ZJDBZLX_FD_Pay = "07FD";     //应付TA基金分红款项
    public static final String YSS_ZJDBZLX_CGT_Pay = "07CGT";    //应付资本利得税              // add by fangjiang 2011.05.18 STORY #845
    public static final String YSS_ZJDBZLX_CGT_Fee = "03CGT";    //资本利得税                        // add by fangjiang 2011.05.18 STORY #845
    public static final String YSS_ZJDBZLX_FD_Fee = "03FD";     //TA基金分红费用款项
    public static final String YSS_ZJDBZLX_FD_Capital = "0403"; //TA基金分红资本款项
    public static final String YSS_ZJDBZLX_IV_Income = "02IV";  //收入运营款项
    public static final String YSS_ZJDBZLX_RE_Income = "02RE";  //收入回购款项
    public static final String YSS_ZJDBZLX_TR_Income = "02TR";  //基金收入
    public static final String YSS_ZJDBZLX_IV_Fee = "03IV";     //支出运营款项
    public static final String YSS_ZJDBZLX_RE_Fee = "03RE";     //支出回购款项
    public static final String YSS_ZJDBZLX_LXS_FEE = "03LXS_DE";      //存款利息税   add by jiangshichao 20120323  
    public static final String YSS_ZJDBZLX_09BLVD= "60BLVD";  //借入股票估增发生额

    public static final String YSS_ZJDBZLX_COST_FACT = "0001";  //成本_实际发生
    public static final String YSS_ZJDBZLX_MANUAL_IMPRACTICAL = "0002";     //人工调整_未实际发生
    public static final String YSS_ZJDBZLX_COST_SAVING = "0003";            //存款产生
    public static final String YSS_ZJDBZLX_COST_RateTrade = "0004";         //换汇
    public static final String YSS_ZJDBZLX_COST_LEND = "0105";              //同业拆借
    public static final String YSS_ZJDBZLX_Principal_Ext = "0109";          //本金提取 add by fangjiang 2010.11.29 STORY #97 协议存款业务需支持提前提取本金的功能
    public static final String YSS_ZJDBZLX_DEPOSIT_INTEREST = "02DE";		//调拨子类型存款利息 add by lidaolong, 2011.02.16 #526 QDV4长信基金2011年1月14日01_A
    
    //---MS01175  国泰2010年5月13日02_A  add by jiangshichao 2010.05.26 ----------------
    public static final String Yss_ZJDBZLX_SEC_Rec = "06LE";     //应收证券借贷收益
    public static final String Yss_ZJDBZLX_SEC_Income = "02LE";  //证券借贷收入
    public static final String Yss_ZJDBZLX_SEC_Pay = "07LE";     //应付证券借贷费用
    public static final String Yss_ZJDBZLX_SEC_Fee = "03LE";     //证券借贷费用
    //---MS01175  国泰2010年5月13日02_A end -------------------------------------------
    
    
    public static final String YSS_ZJDBZLX_FI_Income = "02FI";      //债券利息收入
    public static final String YSS_ZJDBZLX_FU_Income = "02FU01";    //股指期货收入
    public static final String YSS_ZJDBZLX_FU02_Income = "02FU02";    //债券期货收入
    public static final String YSS_ZJDBZLX_FU03_Income = "02FU03";    //外汇期货收入  add by fangjiang 2011.02.15 STORY #462 外汇期货需求

    public static final String YSS_ZJDBZLX_EQ_COST = "05EQ";        //股票买卖
    public static final String YSS_ZJDBZLX_FI_COST = "05FI";        //债券买卖
    public static final String YSS_ZJDBZLX_DE_COST = "05DE";        //定期存款
    public static final String YSS_ZJDBZLX_OP_COST = "05OP";        //权证买卖
    public static final String YSS_ZJDBZLX_RE_COST = "05RE";        //回购买卖
    public static final String YSS_ZJDBZLX_FU01_COST = "05FU01";    //股指期货买卖
    public static final String YSS_ZJDBZLX_FU02_COST = "05FU02";    //债券期货买卖  add by fangjiang 2010.08.20
    public static final String YSS_ZJDBZLX_FU03_COST = "05FU03";    //外汇期货买卖  add by fangjiang 2011.02.15 STORY #462 外汇期货需求
    public static final String YSS_ZJDBZLX_FU04_COST = "05FU04";	//商品期货		add by huangqirong 2012-08-21  商品期货
    public static final String YSS_ZJDBZLX_FP01_COST = "05FP01";    //期权买卖

    public static final String YSS_ZJDBZLX_MANAGER_FEE = "0301";    //管理费
    public static final String YSS_ZJDBZLX_Trustee_FEE = "0302";    //托管费
    public static final String YSS_ZJDBZLX_BANK_FEE = "0303";       //银行费用

    public static final String YSS_ZJDBZLX_DE_RecInterest = "06DE"; //应收存款利息
    //edited by zhouxiang--MS01301-------------------------------------
    public static final String YSS_ZJDBZLX_DC_RecInterest = "06DE_B"; //定存应收存款利息
    //----------end----------------------------------------------------
    public static final String YSS_ZJDBZLX_DE_RecBuyInterest = "06DE_Buy"; //应收普通定存所含利息
    public static final String YSS_ZJDBZLX_FI_RecInterest = "06FI"; //应收债券利息
    public static final String YSS_ZJDBZLX_RE_RecInterest = "06RE"; //应收回购利息
    public static final String YSS_ZJDBZLX_OH_RecFund = "06OH";     //应收其他款项

    public static final String YSS_ZJDBZLX_DE_PayInterest = "07DE"; //应付存款利息
    public static final String YSS_ZJDBZLX_RE_PayInterest = "07RE"; //应付回购利息
    
    public static final String YSS_ZJDBZLX_DE_PayLoanInterest = "07LI"; //应付贷款利息     by guyichuan 20110520 STORY #561
    public static final String YSS_ZJDBZLX_DE_LoanInterest = "03LI"; //贷款利息  by guyichuan 20110520 STORY #561
    
    //------------------------MS01132    结算服务费、交易手续费及银行费用需分开不同类型进行费用的统计（国内，9月发布 
    public static final String YSS_ZJDBZLX_RE_PayTradeHandleFee ="07RE01"; //应付银行间回购交易手续费 edited zhouxiang 20100828
    public static final String YSS_ZJDBZLX_RE_PayBankHandleFee ="07RE02";	//应付银行间回购银行手续费 add zhouxiang 20100828
    public static final String YSS_ZJDBZLX_RE_PaySetServiceFee ="07RE03";	//应付银行间回购结算服务费 add zhouxiang 20100828
    public static final String YSS_ZJDBZLX_FE_PAYTRADEFEE = "07FE01"; //应付银行间债券交易手续费
    public static final String YSS_ZJDBZLX_FE_PAYBANKFEE = "07FE02"; //应付银行间债券银行手续费
    public static final String YSS_ZJDBZLX_FE_PAYSETTLEFEE= "07FE03"; //应付银行间债券结算手续费
    //---add by songjie 2012.01.17 STORY 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A start---//
    public static final String YSS_ZJDBZLX_FE_PayDomCmsnFEE= "07FE"; //应付国内佣金
    public static final String YSS_ZJDBZLX_FE_RecDomCmsnFEE= "03FE"; //国内佣金费用 
    //---add by songjie 2012.01.17 STORY 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A end---//
    //------------------------end-------------------------------------------------
    
    public static final String YSS_ZJDBZLX_OH_PayFund = "07OH";     //应付回购利息
    public static final String YSS_ZJDBZLX_FE_FEEINTBOND = "03FE02"; //银行间债券交易手续费

    public static final String YSS_ZJDBZLX_TD_RecUnAcc = "06TD"; //应收未清算款项
    public static final String YSS_ZJDBZLX_TD_PayUnAcc = "07TD"; //应付未清算款项

    public static final String YSS_ZJDBZLX_TR_RecFundIns = "06TR"; //应收基金红利 货币
    public static final String YSS_ZJDBZLX_DV_TR_RecFundIns = "06DV_TR";//应收基金红利 基金分红

    public static final String YSS_ZJDBZLX_FU01_CHM = "0005"; //期货调整保证金
    public static final String YSS_ZJDBZLX_FP_CHM = "0006";//期权调整保证金 xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务

    public static final String YSS_ZJDBZLX_FU01_REC = "06FU01"; //应收股指期货收益
    public static final String YSS_ZJDBZLX_FU02_REC = "06FU02"; //应收债券期货收益       add by fangjiang 2010.08.20
    public static final String YSS_ZJDBZLX_FU03_REC = "06FU03"; //应收外汇期货收益       add by fangjiang 2011.02.15 STORY #462 外汇期货需求
    public static final String YSS_ZJDBZLX_FU01_MV = "09FU01";  //股指期货估值增值   
    public static final String YSS_ZJDBZLX_FU02_MV = "09FU02";  //债券期货估值增值       add by fangjiang 2010.08.20
    public static final String YSS_ZJDBZLX_FU03_MV = "09FU03";  //外汇期货估值增值       add by fangjiang 2011.02.15 STORY #462 外汇期货需求
    public static final String YSS_ZJDBZLX_FU04_MV = "09FU04";  //商品期货估值增值       add by huangqirong 2012-08-22 商品期货需求
    public static final String YSS_ZJDBZLX_FU01_SMV = "19FU01"; //股指期货卖出股指增值
    public static final String YSS_ZJDBZLX_FU02_SMV = "19FU02"; //债券期货卖出股指增值   add by fangjiang 2010.08.20
    public static final String YSS_ZJDBZLX_FU03_SMV = "19FU03"; //外汇期货卖出股指增值   add by fangjiang 2011.02.15 STORY #462 外汇期货需求

    public static final String YSS_ZJDBZLX_DEV_MV = "09DEV";//债券转托管估值增值 panjunfang add 20090815

    public static final String YSS_ZJDBZLX_ETF_CashBalRPBuyDone = "02TA_CB";//ETF现金差额实收款（申购）
    public static final String YSS_ZJDBZLX_ETF_CashInsRPBuyDone = "02TA_CR";//现金替代款-ETF申购-可以现金替代款
    public static final String YSS_ZJDBZLX_ETF_CashMustInsRPBuyDone = "02TA_CR_2";//现金替代款-ETF申购-必须现金替代款    
    public static final String YSS_ZJDBZLX_ETF_CashBalRPSellDone = "03TA_CB";//ETF现金差额实付款（赎回）
    public static final String YSS_ZJDBZLX_ETF_CashInsRPSellDone = "03TA_CR";//ETF现金替代实付款（赎回）
    public static final String YSS_ZJDBZLX_ETF_CashInsMustRPSellDone = "03TA_CR_2";//ETF必须现金替代实付款（赎回）
    public static final String YSS_ZJDBZLX_ETF_InsteadDuesSellDone = "03TA_IDS";    //ETF实付替代款（赎回）
    public static final String YSS_ZJDBZLX_ETF_InsteadDuesBuyDone = "03TA_IDB";    //ETF实付替代款（申购）
    public static final String YSS_ZJDBZLX_ETF_InsteadDuesBuyTrans = "04TA_IDB"; //ETF应付申购替代款（退款）
    public static final String YSS_ZJDBZLX_ETF_InsteadDuesBuyTrans_BK = "04TA_IDB_BK"; //ETF应付申购替代款（补款）
    public static final String YSS_ZJDBZLX_ETF_InsteadDuesSellTrans = "04TA_IDS"; //ETF应付替代款
    public static final String YSS_ZJDBZLX_ETF_CashBalTrans = "04CB";//ETF现金差额结转
    public static final String YSS_ZJDBZLX_ETF_CashInsMustTrans = "04CR_2";//ETF赎回必须现金替代结转
    public static final String YSS_ZJDBZLX_ETF_CashInsteadTrans = "04CR";//ETF现金替代结转
    public static final String YSS_ZJDBZLX_ETF_CashBalRPBuy = "06TA_CB";//ETF现金差额应收款（申购）
    public static final String YSS_ZJDBZLX_ETF_CashBalRPSell = "07TA_CB";//ETF现金差额应付款（赎回）
    public static final String YSS_ZJDBZLX_ETF_CashInsRPBuy = "06TA_CR";//现金替代款-ETF申购-可以现金替代款
    public static final String YSS_ZJDBZLX_ETF_CashMustInsRPBuy = "06TA_CR_2";//现金替代款-ETF申购-必须现金替代款
    
    public static final String YSS_ZJDBZLX_ETF_CashInsRPSell = "07TA_CR";//应付赎回款-ETF赎回-可以现金替代款
    public static final String YSS_ZJDBZLX_ETF_CashInsMustRPSell = "07TA_CR_2";//应付赎回款-ETF赎回-必须现金替代款
    public static final String YSS_ZJDBZLX_ETF_InsteadDuesSell = "07TA_IDS";//ETF应付替代款（赎回）
    public static final String YSS_ZJDBZLX_ETF_InsteadDuesBuy = "07TA_IDB";//ETF应付替代款（申购）
    
    public static final String YSS_ETF_QUITVALUEBuy = "07TA_IDB_HD";//ETF应付替代款估值增值（申购）
    public static final String YSS_ETF_QUITVALUESell = "07TA_IDS_HD";//ETF应付替代款估值增值（赎回）
    public static final String YSS_ETF_QUITVALUEETFVALBUY = "07TA_IDB_HD_1";//统计到净值表一的 ETF 应付替代款估值增值（申购）华宝兴业
    public static final String YSS_ETF_QUITVALUEETFVALSELL = "07TA_IDS_HD_1";//统计到净值表一的 ETF 应付替代款估值增值（赎回）华宝兴业
    public static final String YSS_ZJDBZLX_ETF_CashInsMay = "07TA_IDS_MAY";//ETF赎回允许现金替代应付款（股票蓝中替代标志为1的现金替代）
    
    public static final String YSS_ZJDBZLX_ETF_SGCanBackCash = "07TA_CBCB";//ETF可退替代款（申购）
    public static final String YSS_ZJDBZLX_ETF_SHCanBackCash = "07TA_CBCS";//ETF可退替代款（赎回）
    
    public static final String YSS_ZJDBZLX_ETF_ClearTrans = "0007ETF";//备付金结转（华夏）
    
    public static final String YSS_ZJDBZLX_ETFBACKCASH_MV = "09CR";//可退替代款估值增值 
    /**shashijie STORY 1434 易方达ETF调拨子类型*/
    public static final String YSS_ZJDBZLX_ETFBACKCASH_MV_B = "09CR_B";//可退替代款估值增值 (申购)
    public static final String YSS_ZJDBZLX_ETFBACKCASH_MV_S = "09CR_S";//可退替代款估值增值 (赎回)
    /**end*/
    
    //xuqiji 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
    public static final String YSS_ETF_CashTradeCost_SG = "07TA_JYSHR_SG";//ETF基金预提交易收入_申购
    public static final String YSS_ETF_CashTradeCost_SH = "07TA_JYSHR_SH";//ETF基金预提交易收入_赎回
    public static final String YSS_ETF_CashTradeFee_SG = "03TA_JYSHR_SG";//ETF基金交易费用_申购
    public static final String YSS_ETF_CashTradeFee_SH = "03TA_JYSHR_SH";//ETF基金交易费用_赎回
    
    public static final String YSS_ZJDBZLX_FP01_REC = "06FP01"; //应收期权收益
    public static final String YSS_ZJDBZLX_FP01_MV = "09FP01";  //期权估值增值
    public static final String YSS_ZJDBZLX_FP01_SR = "02FP01";  //期权收入

    public static final String YSS_ZJDBZLX_FX_Storage = "9905"; //库存成本汇兑损益
    public static final String YSS_ZJDBZLX_FX_Rec = "9906";     //应收款项汇兑损益
    public static final String YSS_ZJDBZLX_FX_Pay = "9907";     //应付款项汇兑损益
    public static final String YSS_ZJDBZLX_FX_OH_Rec = "9906OH"; //其他应收汇兑损益
    public static final String YSS_ZJDBZLX_FX_OH_Pay = "9907OH"; //其他应付汇兑损益

    public static final String YSS_ZJDBZLX_FX_MV = "9909";          //估值增值汇兑损益
    public static final String YSS_ZJDBZLX_FX_RecUnAcc = "9906TD";  //应收未清算款汇兑损益
    public static final String YSS_ZJDBZLX_FX_PayUnAcc = "9907TD";  //应付未清算款汇兑损益
    public static final String YSS_ZJDBZLX_FX_Adjust = "9901";      //汇兑损益调整金
    public static final String YSS_ZJDBZLX_RE_BX = "15RE";          //回购到期

    public static final String Yss_ZJDBLX_SUPPLEMENT_IV = "9707IV";	//计提补差 add by huangqirong 2013-02-01 story #3488
   //---MS01175  国泰2010年5月13日02_A add by jiangshichao 2010.05.26 ----------------
    public static final String YSS_ZJDBZLX_FX_SEC_Rec = "9906LE"; //其他应收汇兑损益
    public static final String YSS_ZJDBZLX_FX_SEC_Pay = "9907LE"; //其他应付汇兑损益
   //---MS01175  国泰2010年5月13日02_A end 2010.05.26 --------------------------------
    
    public static final String YSS_ZJDBLX_PRE_PAYOUT = "16IV"; //待摊费用

    public static final String YSS_ZJDBZLX_EQ_MV = "09EQ";          //证券估值增值
    public static final String YSS_ZJDBZLX_FX_EQ_MV = "9909EQ";     //证券估值增值汇兑损益
    public static final String YSS_ZJDBZLX_FX_EQ_Storage = "9905EQ";//证券库存成本汇兑损益

    public static final String YSS_ZJDBLX_Forward = "00005";        //远期外汇交易
    public static final String YSS_ZJDBZLX_Premium_Fix = "20FI";    //债券溢价
    public static final String YSS_ZJDBZLX_Discounts_Fix = "21FI";  //债券折价

    //成本结算方式
    public static final String YSS_CBJS_YDJQ = "01";        //移动加权
    public static final String YSS_CBJS_XJXC = "02";        //先进先出
    public static final String YSS_CBJS_FeeInCost = "01";   //费用进成本
    public static final String YSS_CBJS_FeeOutCost = "01";  //费用不进成本
    //舍入符号
    public static final int Yss_SRFH_BOTH = 0;      //无论值是正还是负数
    public static final int Yss_SRFH_NEGATIVE = 1;  //当值为负数时
    public static final int Yss_SRFH_POSITIVE = 2;  //为当值为正时
    //输入方法
    public static final int Yss_SRFF_NORMAL = 0;        //普通
    public static final int Yss_SRFF_TRUNCATION = 1;    //截断
    public static final int Yss_SRFF_MAX = 2;           //取大值
    public static final int Yss_SRFF_MIN = 3;           //取小值
    //台帐显示类型
    public static final int Yss_TZXSLX_ALL = 0;         //全部显示
    public static final int Yss_TZXSLX_CLERARED = 1;    //已清算
    public static final int Yss_TZXSLX_NOCLERAR = 2;    //未清算

    //监控指标配置 - 储存方式
    public static final String YSS_JKZBPZ_MEMOYWAY_NO = "NO";       //不储存
    public static final String YSS_JKZBPZ_MEMOYWAY_TABLE = "Table"; //表储存
    public static final String YSS_JKZBPZ_MEMOYWAY_VIEW = "View";   //视图储存
    //交易拆分，拆分方式
    public static final int YSS_JYCFMR_CASH = 01;       //买入按现金比例拆分
    public static final int YSS_JYCFMR_NETVALUE = 02;   //买入按净值比例拆分

    //证券品种子类型
    public static final String YSS_ZQPZZLX_REBANK = "bank";         //回购品种子类型 -- 银行间
    public static final String YSS_ZQPZZLX_REEXCHANGE = "exchange"; //回购品种子类型 -- 交易所
    public static final String YSS_ZQPZZLX_TR04 = "TR04";           //ETF 基金
    
    /**shashijie 2012-4-27 STORY 2565 设置品种类型 */
    public static final String YSS_ZQPZ_TR = "TR";//基金
	/**end*/

    //国内业务之通用参数的常量值
    public static final String YSS_INNER_PURCHASEBIC = "purchaseBankFeeInCost";     //银行间回购交易费用入成本
    public static final String YSS_INNER_PURCHASEEIC = "purchaseExchangeFeeInCost"; //交易所回购交易费用入成本

    public static final String YSS_INNER_PURCHASEBNT = "purchaseBankType";      //银行间回购回购计息方式
    public static final String YSS_INNER_PURCHASEEXT = "purchaseExchangeType";  //交易所回购回购计息方式

    public static final String YSS_INNER_PURCHASEWFEE = "purchaseInterestWithFee"; //回购（包括交易所和银行间）计息包含交易费用
    public static final String YSS_INNER_PURCHASEPED = "purchaseInterestPEachDay"; //回购计息凭证当日计提

    //核算项目与存储字段
    public static HashMap YSS_ACC_AuxiliaryToFieldMap; //核算项目-存储字段的对应关系表 key-核算项目代码；value-字段名

    //属性分类
    public static final String YSS_SXFL_PONS = "PONS";      //公开发行新股 - 国内股票业务
    public static final String YSS_SXFL_UNPONS = "UNPONS";  //非公开法行新股 - 国内股票业务
    public static final String YSS_SXFL_CEQ = "CEQ";        //上市普通股票
    public static final String YSS_SXFL_IDXEQ = "IDXEQ";    //上市指数股票
    public static final String YSS_SXFL_XG = "XG";          //新股  add by fangjiang 2010.09.13 MS01138 QDV4赢时胜上海2010年04月28日01_A 
    public static final String YSS_SXFL_XZ = "XZ";          //新债  add by fangjiang 2010.09.13 MS01138 QDV4赢时胜上海2010年04月28日01_A 
    public static final String YSS_SXFL_TAGEQ = "TAGEQ";    //上市指标股票  add by fangjiang 2010.09.13 MS01138 QDV4赢时胜上海2010年04月28日01_A 
    public static final String YSS_SXFL_AntiRepo = "AntiRepo"; //逆回购  add by fangjiang 2010.09.13 MS01138 QDV4赢时胜上海2010年04月28日01_A 
    public static final String YSS_SXFL_SellRepo = "SellRepo"; //正回购  add by fangjiang 2010.09.13 MS01138 QDV4赢时胜上海2010年04月28日01_A 
    public static final String YSS_SXFL_X = "X";            //现券委托型  add by fangjiang 2010.09.13 MS01138 QDV4赢时胜上海2010年04月28日01_A 
    public static final String YSS_SXFL_S = "S";            //可供出售型  add by fangjiang 2010.09.13 MS01138 QDV4赢时胜上海2010年04月28日01_A 
    public static final String YSS_SXFL_F = "F";            //持有到期型  add by fangjiang 2010.09.13 MS01138 QDV4赢时胜上海2010年04月28日01_A 
    public static final String YSS_SXFL_C = "C";            //交易型  add by fangjiang 2010.09.13 MS01138 QDV4赢时胜上海2010年04月28日01_A 
    //---add by songjie 2012.01.06 需求 STORY 2104  QDV4赢时胜(上海开发部)2012年01月03日01_A start---//
    public static final String YSS_SXFL_ETFSG = "ETFSG";    //ETF申购  
    public static final String YSS_SXFL_ETFSH = "ETFSH";    //ETF赎回
    public static final String YSS_SXFL_KZZGP = "KZZGP";    //债转股
    public static final String YSS_SXFL_XGSG = "XGSG";      //新股申购
    public static final String YSS_SXFL_XGFK = "XGFK";      //新股申购
    public static final String YSS_SXFL_XGZQ = "XGZQ";      //新股中签
    public static final String YSS_SXFL_XGZF = "XGZF";      //新股增发
    public static final String YSS_SXFL_PSZFZQ = "PSZFZQ";  //配售增发中签
    public static final String YSS_SXFL_SHZQ = "SHZQ";      //上海新股中签
    public static final String YSS_SXFL_PG = "PG";          //配股
    public static final String YSS_SXFL_PGJK = "PGJK";      //配股缴款
    public static final String YSS_SXFL_PX = "PX";          //分红派息
    public static final String YSS_SXFL_SG = "SG";          //送股
    public static final String YSS_SXFL_ZLT = "ZLT";        //新股转流通
    public static final String YSS_SXFL_KPSL = "KPSL";      //可配售许可数据
    public static final String YSS_SXFL_YYSG = "YYSG";      //邀约收购
    //---add by songjie 2012.01.06 需求 STORY 2104  QDV4赢时胜(上海开发部)2012年01月03日01_A end---//
    
    //投资类型
    public static final String YSS_INVESTTYPE_JYX = "C";    //交易性
    public static final String YSS_INVESTTYPE_CYDQ = "F";   //持有到期
    public static final String YSS_INVESTTYPE_KGCS = "S";   //可供出售
    public static final String YSS_INVESTTYPE_WTXQ = "X";   //委托现券

    //TA销售类型 xuqiji 20091013
    public static final String YSS_TATRADETYPE_JYZS = "10";    //TA份额折算
    
    //2009-10-30 蒋锦 添加 MS00005 QDV4.1赢时胜（上海）2009年9月28日04_A 是或否的缩写
    public static final String YSS_DAO_YES = "Y";
    public static final String YSS_DAO_NO = "N";
    public static final String YSS_ETF_BUYORSELL = "ETFBS";//ETF 申赎标志
    
    public static final String YSS_SAVEINTEREST="depositInterest";	//存款利息　by guyichuan 20110520 STORY #561
    public static final String YSS_LOANINTEREST="loanInterest";		//贷款利息　by guyichuan 20110520 STORY #561
    
    //add by fangjiang 2010.08.25 AccountType为股指期货的核算类型，BondAccountType为债券期货的核算类型
    //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
    public static final String[] YSS_FU_ACCOUT_TYPE = {"AccountType","BondAccountType","RateAccountType","CommodityAccountType"};//modify huangqirong 2012-08-21 商品期货
    // add by fangjiang 2010.08.25 01为股指期货，02为债券期货, 03外汇期货
    //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
    public static final String[] YSS_FU = {"FU01","FU02","FU03","FU04"}; //modify huangqirong 2012-08-21  商品期货
    // add by fangjiang 2010.08.25 用于期货的成本、估值增值等，01为股指期货，02为债券期货，03为外汇期货， 可扩展
    //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
    public static final String[][] YSS_ZJDBZLX_FU = {
    	{"02FU01","05FU01","06FU01","09FU01","19FU01","9906FU01","9909FU01"},
    	{"02FU02","05FU02","06FU02","09FU02","19FU02","9906FU02","9909FU02"},
    	{"02FU03","05FU03","06FU03","09FU03","19FU03","9906FU03","9909FU03"}
    	,{"02FU04","05FU04","06FU04","09FU04","19FU04","9906FU04","9909FU04"}	//add huangqirong 2012-08-21  商品期货
    };
    public static final String YSS_FU_09 = "09FU01,09FU02,09FU03,09FU04"; //add by fangjiang bug 6324 2012.11.16
}
