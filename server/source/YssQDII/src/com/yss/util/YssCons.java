package com.yss.util;

/**
 * <p>Title: </p>
 * <p>Description: 辅助类，包括所有常数定义</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @author alex
 * @version 1.0
 */

public final class YssCons {
    public YssCons() {
    }

    /**
     * 自动更新数据库的版本号数组，从大到小排列
     * 第一位为版本号，第二位显示是否包含更新文件
     * 0 代表不包含更新文件，1 代表包含更新文件
     * by xuqiji 2009 04010  MS00352    新建组合群时能够自动创建对应的一套表
     */
    public static final String[][] YSS_AUTOUPDATE_VERSIONS = {
        {"1.0.1.0016", "0"},
        {"1.0.1.0017", "1"},
        {"1.0.1.0018", "1"},
        {"1.0.1.0018sp3", "1"},
        {"1.0.1.0019", "1"},
        {"1.0.1.0019sp4", "1"},
        {"1.0.1.0020", "1"},
        {"1.0.1.0020sp1", "1"},	//V4.1.12.20_sp1版本，sunkey@Modify 20091010
        {"1.0.1.0020sp2", "1"},	//V4.1.12.20_sp2版本，sunkey@Modify 20091014
        {"1.0.1.0021", "1"},	//V4.1.13.21版本，sunkey@Modify 20091020
        {"1.0.1.0022", "1"},		//11.13号版本 11月份版本上部
        {"1.0.1.0023", "1"},
        {"1.0.1.0024", "1"},//20100122 25_sp1版本
        {"1.0.1.0025", "1"},//20100127 25_sp3版本
        {"1.0.1.0026", "1"},//20100205  26版本(其中包括了国内的基金、接口、权益)
        {"1.0.1.0026sp1", "1"},//20100304 26_sp1版本
        {"1.0.1.0027", "1"},//20100310 27版本
	    {"1.0.1.0027sp1", "1"},//20100325 27sp1版本
        {"1.0.1.0027sp2", "1"},//20100322 28版本 xuqiji 
	    {"1.0.1.0027sp3", "1"},//20100325 27sp1版本
        {"1.0.1.0028", "1"},//20100407 28版本
        {"1.0.1.0028sp1", "1"},//20100401 28sp1版本panjunfang
        {"1.0.1.0029","1"},//29版本 shenjie
        {"1.0.1.0030","1"},//30版本 yanghaiming
        {"1.0.1.0031","1"},//31版本 yanghaiming 20100711
        {"1.0.1.0031sp1","1"},
        {"1.0.1.0032","1"},
        {"1.0.1.0032sp2","1"},//20100907 32sp1版本
        {"1.0.1.0032sp4","1"},//20100914 32sp4版本
        {"1.0.1.0032sp4em1","1"},//20101214 32sp4em1版本 QDV4华夏2010年12月14日01_A
        {"1.0.1.0033","1"},//增加33版本，by leeyu 20100902 合并太平版本时添加
        {"1.0.1.0033sp1","0"}, //add by fangjiang 2010.10.13  MS01847 权限设置中，综合业务菜单有两个 
        {"1.0.1.0034","1"},//20101019 34版本
        {"1.0.1.0034sp1","1"},//20101108 add by yanghaiming
        {"1.0.1.0035","1"},//20101109 add by yanghaiming
        {"1.0.1.0035sp1","1"},//20101127 add by panjunfang
        {"1.0.1.0036","1"},//20101127 add by panjunfang
        {"1.0.1.0037","1"},//2011年1月正式版本  panjunfang add 20101227
        {"1.0.1.0037sp1","0"},//add by wangzuochun 2011.02.09
        {"1.0.1.0037sp2","1"},//add by panjunfang 2011.02.17
        {"1.0.1.0038","1"},//lidaolong add 2011.01.25
        {"1.0.1.0038sp2","1"},
        {"1.0.1.0039","1"},//lidaolong add 2011.03.14
        {"1.0.1.0039sp1","1"},//panjunfang add 2011.03.31
        {"1.0.1.0040","1"},//panjunfang add 2011.03.31
        {"1.0.1.0041","1"},//panjunfang add 2011.03.31
        {"1.0.1.0041sp1","0"},//shashijie add 2011.05.26
        {"1.0.1.0042","1"},//panjunfang add 2011.06.09
		{"1.0.1.0042sp1","1"},//panjunfang add 2011.07.04
		{"1.0.1.0042sp3","0"},//songjie add 2011.07.20 需求 1282 QDV4博时基金2011年6月29日01_A
		{"1.0.1.0043","1"},//panjunfang add 2011.07.11
		{"1.0.1.0043sp1","1"},//panjunfang add 2011.08.09
		{"1.0.1.0043sp2","1"},//panjunfang add 2011.08.09
		{"1.0.1.0044","1"},//panjunfang add 2011.08.23
		{"1.0.1.0044sp3","0"},//panjunfang add 2011.08.23
		{"1.0.1.0044sp4","1"},//panjunfang add 2011.09.14
		{"1.0.1.0045","1"},//panjunfang add 2011.09.19
		{"1.0.1.0045sp1","1"},
		{"1.0.1.0045sp2","1"},
		{"1.0.1.0045sp3","1"},
		{"1.0.1.0045sp4","1"},
		{"1.0.1.0046","1"},
		{"1.0.1.0046sp1","0"},
		{"1.0.1.0046sp2","1"},
		{"1.0.1.0046sp4","0"},
		{"1.0.1.0046sp5em2","1"},
		{"1.0.1.0047","1"},
		{"1.0.1.0048","1"},
		{"1.0.1.0048sp2","1"},
		{"1.0.1.0049","1"},
		{"1.0.1.0049sp1","1"},
		{"1.0.1.0049sp2","0"},//add by songjie 2012.02.02 BUG 3619 QDV4赢时胜(上海)2012年01月11日01_B
		//50版本表结构更新拆分为两个：49sp3和50，以兼顾RQFII版本。 需求1713 的表结构更新（1713主流上在50版本中发布）
		{"1.0.1.0049sp3","1"},//50版本会同时更新49sp3 和50表结构
		{"1.0.1.0050","1"},//add by songjie 2012.01.19 需求 2007 QDV411建行2011年12月09日01_A
		{"1.0.1.0050sp1","0"},//20120301 added by liubo.Story #2248
		{"1.0.1.0051","1"},//panjunfang add 2012.03.08
		{"1.0.1.0051sp1","1"},//panjunfang add 2012.04.09
		{"1.0.1.0052","1"},
		{"1.0.1.0052sp1","0"},
		{"1.0.1.0052sp2","1"},//panjunfang add 2012.05.02
		{"1.0.1.0052sp3","1"},
		{"1.0.1.0052sp3em1","1"},
		{"1.0.1.0053","1"},
		{"1.0.1.0053sp1","0"},//add by huangqirong 2012-06-01 bug #4679、bug#4667
		{"1.0.1.0053sp2","1"},
		{"1.0.1.0053sp2em1","1"},//针对华夏恒指etf发布的临时版本，加上该版本号确保53sp2em1能正常升级
		{"1.0.1.0053sp2em2","1"},
		{"1.0.1.0053sp2em4","1"},
		{"1.0.1.0053sp2em5","1"},
		{"1.0.1.0053sp3","1"},
		{"1.0.1.0053sp3em1","1"},
		{"1.0.1.0054","1"},
		{"1.0.1.0054sp1","1"},
		{"1.0.1.0055","1"},
		{"1.0.1.0055em2","1"},//整合招行紧急版本的表结构更新 panjunfang add 2013-5-22
		{"1.0.1.0055em4","1"},//整合招行紧急版本的表结构更新 panjunfang add 2013-5-22
		{"1.0.1.0055sp1","1"},
		{"1.0.1.0055sp2","1"},
		{"1.0.1.0056","1"},
		{"1.0.1.0056sp1","1"},
		{"1.0.1.0056sp3","1"},
		{"1.0.1.0056sp3em1","1"},
		{"1.0.1.0056sp4","1"},
		{"1.0.1.0056sp4em1","1"},
		{"1.0.1.0056sp4em2","0"},//add by songjie 2012.12.26 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
		{"1.0.1.0056sp5","1"},
		{"1.0.1.0056sp6","1"},
		{"1.0.1.0056sp7","1"}, //add by huangqirong 2013.01.25 story #3488
		{"1.0.1.0056sp8","1"},
		{"1.0.1.0056sp8em1","0"} ,//整合农行紧急版本的表结构更新 panjunfang add 2013-11-20
		{"1.0.1.0057","1"},
		{"1.0.1.0059","1"},
		{"1.0.1.0059sp1","1"},
		{"1.0.1.0059sp2","1"} //shashijie 2013-03-07 STORY 3366 更新脚本
		,{"1.0.1.0059sp4em1","1"} //整合国泰基金紧急版本的表结构更新 panjunfang add 2013-5-27
		,{"1.0.1.0059sp4em3","1"} //整合国泰基金紧急版本的表结构更新 panjunfang add 2013-5-27
		,{"1.0.1.0060","1"} //shashijie 2013-03-18 STORY 3661 更新脚本
		,{"1.0.1.0060sp1","1"}//panjunfang add 60sp1表结构更新（Story3869 、BUG7760、BUG7689）
		,{"1.0.1.0060sp2","1"}//panjunfang add 2013-06-6 60sp2表结构更新（Story3759）
		,{"1.0.1.0060sp3","1"}//20130704 added by liubo.Story #4135
		,{"1.0.1.0060sp4","1"}//panjunfang add 2013-8-7 60sp4表结构更新（Story4094）
    };
    public static final String[][] YSS_AUTOUPDATE_DB2VERSIONS = {
    	{"1.0.1.0002sp1","1"},
    	{"1.0.1.0052","1"},
		{"1.0.1.0053","1"},//add by yeshenghong DB2直接更新到53版本
		{"1.0.1.0053sp1","0"},
		{"1.0.1.0053sp2","1"},
		{"1.0.1.0053sp2em1","1"},//针对华夏恒指etf发布的临时版本，加上该版本号确保53sp2em1能正常升级
		{"1.0.1.0053sp2em2","1"},
		{"1.0.1.0053sp2em4","1"},
		{"1.0.1.0053sp2em5","1"},
		{"1.0.1.0053sp3","1"},
		{"1.0.1.0053sp3em1","1"},
		{"1.0.1.0054","1"},
		{"1.0.1.0054sp1","1"},
		{"1.0.1.0055","1"},
		{"1.0.1.0055sp1","1"},
		{"1.0.1.0055sp2","1"},
		{"1.0.1.0056","1"},
		{"1.0.1.0056sp1","1"},
		{"1.0.1.0056sp3","1"},
		{"1.0.1.0056sp3em1","1"},
		{"1.0.1.0056sp4","1"},
		{"1.0.1.0056sp4em1","1"},
		{"1.0.1.0056sp4em2","0"},//add by songjie 2012.12.26 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
		{"1.0.1.0056sp5","1"},
		{"1.0.1.0056sp6","1"},
		{"1.0.1.0056sp7","1"}, //add by huangqirong 2013.01.25 story #3488
		{"1.0.1.0056sp8","1"},
		{"1.0.1.0057","1"},
		{"1.0.1.0059","1"},
		{"1.0.1.0059sp1","1"},
		{"1.0.1.0059sp2","1"}, //shashijie 2013-03-07 STORY 3366 更新脚本
		{"1.0.1.0060","1"}, //shashijie 2013-03-18 STORY 3661 更新脚本
		{"1.0.1.0060sp1","1"}//panjunfang add 60sp1表结构更新（Story3869 、BUG7760、BUG7689）
    };
    public static final String YSS_DBUPDATE_SUCCESS = "Success";    //更新成功
    public static final String YSS_DBUPDATE_FAIL = "Fail";          //更新失败
    public static final String Yss_DBUPDATE_FILE ="0";				//add by huangqirong 2011-08-30 story #1267
    public static final String Yss_DBUPDATE_COMMON ="0";			//add by huangqirong 2011-08-30 story #1267
    public static final String Yss_DBUPDATE_Group ="0";				//add by huangqirong 2011-08-30 story #1267
    
    //DB2当期版本号
    public static final String YSS_VERSION_DB2CURRENT = "1.0.1.0002sp1";
    //当前系统版本版本号
    public static final String YSS_VERSION_NUMBER = "1.0.1.0020";   //当前系统8月份更新数据库最大版本号
    public static final String YSS_VERSION_MIN = "1.0.0.0000";      //默认的最小版本号
    public static final String YSS_VERSION_STANDARD = "1.0.1.0016"; //自动创建组合群时插入的版本号 当前程序基准脚本为1.0.1.0017，所以基准版本号目前为 1.0.1.0016 sunkey@Modify
    public static final String YSS_VERSION_1010000 = "1.0.1.0000"; //历史版本
    public static final String YSS_VERSION_1010001 = "1.0.1.0001";
    public static final String YSS_VERSION_1010002 = "1.0.1.0002";
    public static final String YSS_VERSION_1010002sp1 = "1.0.1.0002sp1";
    public static final String YSS_VERSION_1010002sp2 = "1.0.1.0002sp2";
    public static final String YSS_VERSION_1010003 = "1.0.1.0003";
    public static final String YSS_VERSION_1010003sp1 = "1.0.1.0003sp1";
    public static final String YSS_VERSION_1010003sp2 = "1.0.1.0003sp2";
    public static final String YSS_VERSION_1010003sp3 = "1.0.1.0003sp3";
    public static final String YSS_VERSION_1010003sp4 = "1.0.1.0003sp4";
    public static final String YSS_VERSION_1010003sp5 = "1.0.1.0003sp5";
    public static final String YSS_VERSION_1010003sp6 = "1.0.1.0003sp6";
    public static final String YSS_VERSION_1010003sp7 = "1.0.1.0003sp7";
    public static final String YSS_VERSION_1010003sp8 = "1.0.1.0003sp8";
    public static final String YSS_VERSION_1010003sp9 = "1.0.1.0003sp9";
    public static final String YSS_VERSION_1010004 = "1.0.1.0004";
    public static final String YSS_VERSION_1010005 = "1.0.1.0005"; //添加新的版本号   王晓光   2008-10-24
    public static final String YSS_VERSION_1010006 = "1.0.1.0006"; //添加新的版本号   王晓光   2008-11-21
    public static final String YSS_VERSION_1010007 = "1.0.1.0007"; //添加新的版本号   sunkey  2008-11-25 BugID:MS00035
    public static final String YSS_VERSION_1010008 = "1.0.1.0008"; //添加新的版本号   王晓光   2008-12-08
    public static final String YSS_VERSION_1010009 = "1.0.1.0009"; //添加新的版本号   sj      2008-12-26
    public static final String YSS_VERSION_1010009sp1 = "1.0.1.0009sp1"; //添加新的版本号   蒋锦 华夏数据调整      2009-04-21
    public static final String YSS_VERSION_1010010 = "1.0.1.0010"; //添加新的版本号   ly      2008-12-29
    public static final String YSS_VERSION_1010011 = "1.0.1.0011"; //添加新的版本号   sj      2009-02-02
    public static final String YSS_VERSION_1010012 = "1.0.1.0012"; //添加新的版本号   蒋锦      2009-02-06 MS00195
    public static final String YSS_VERSION_1010013 = "1.0.1.0013"; //添加新的版本号   sj
    public static final String YSS_VERSION_1010014sp4 = "1.0.1.0014sp4";    //添加新的版本号   songjie 2009.03.23
    public static final String YSS_VERSION_1010014sp6 = "1.0.1.0014sp6";    //添加新的版本号   MS00339 songjie 2009.03.27
    public static final String YSS_VERSION_1010015sp11 = "1.0.1.0015sp11";  //添加新的版本号 xuqiji 20090611 QDV4建行2009年6月9日01_B MS00490 财务估值核对脚本下拉条不起作用
    public static final String YSS_VERSION_1010015 = "1.0.1.0015"; //添加新的版本号   songjie 2009.03.25
    public static final String YSS_VERSION_1010016 = "1.0.1.0016"; //添加新的版本号 蒋锦 MS00352  2009.04.07
    public static final String YSS_VERSION_1010017 = "1.0.1.0017"; //添加新的版本号 songjie  MS00010  2009.05.11
    public static final String YSS_VERSION_1010018 = "1.0.1.0018"; //添加新的版本号 蒋锦 2006.06.03
    public static final String YSS_VERSION_1010018sp3 = "1.0.1.0018sp3"; 	//V4.1.9.17_sp3版本更新数据库的版本号 sunkey 20090702
    public static final String YSS_VERSION_1010019 = "1.0.1.0019"; 			//添加新的版本号 蒋锦 2006.06.03
    public static final String YSS_VERSION_1010019sp4 = "1.0.1.0019sp4"; 	//V4.1.10.18_sp4版本更新数据库版本号 sunkey 20090803
    public static final String YSS_VERSION_1010020 = "1.0.1.0020";      	//8月份版本，转换角色数据
    public static final String YSS_VERSION_1010022 = "1.0.1.0022";      	//fanghaoln 20091110MS00796 QDV4南方2009年11月4日01_B SWIFT报文设置中新建、修改、复制操作有问题 
    public static final String YSS_VERSION_1010026 = "1.0.1.0026";          //xuqiji 2010-02-05 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理
    public static final String YSS_VERSION_1010027sp1 = "1.0.1.0027sp1";          //fanghaoln 20100416
	public static final String YSS_VERSION_1010027sp2 = "1.0.1.0027sp2";    //xuqiji 20100330 转移PUB表中数据
    public static final String YSS_VERSION_1010028 = "1.0.1.0028";			//add by yanghaiming 20100401 MS00839  QDV4华夏2009年11月26日01_A 
	public static final String YSS_VERSION_1010028sp1 = "1.0.1.0028sp1";    //panjunfang 20100401 转移预提待摊、两费数据
	public static final String YSS_VERSION_1010029 = "1.0.1.0029";          //panjunfang add 20100511  B股业务，更新SHGH表，区分上海过户大宗交易和普通交易
	public static final String YSS_VERSION_1010030 = "1.0.1.0030"; //添加新的版本号   yanghaiming 20100611
	public static final String YSS_VERSION_1010031 = "1.0.1.0031"; //添加新的版本号   yanghaiming 20100711
	public static final String YSS_VERSION_1010031sp1 = "1.0.1.0031sp1"; //添加新的版本号   wangzuochun 2010.08.02  MS01460    进入品种信息——指数期权信息设置，新建一条指数期权信息时，点击保存时报错    QDV4赢时胜(测试)2010年07月20日01_B  
	public static final String YSS_VERSION_1010032 = "1.0.1.0032"; //添加新的版本号   add by wangzuochun  2010.08.11  MS01462    进入库存信息配置，新建时,选择库存类型下拉框,有两个相同的运营收支款  QDV4赢时胜(测试)2010年7月20日1_B 
	public static final String YSS_VERSION_1010033 ="1.0.1.0033";		  //添加新版本号，add by leeyu 20100902 合并太平版本添加
	public static final String YSS_VERSION_1010033sp1 ="1.0.1.0033sp1"; //add by fangjiang 2010.10.13  MS01847 权限设置中，综合业务菜单有两个
	public static final String YSS_VERSION_1010034 ="1.0.1.0034"; //add by yanghaiming 2010.10.19
	public static final String YSS_VERSION_1010035 ="1.0.1.0035"; //add by yanghaiming 2010.11.15
	public static final String YSS_VERSION_1010035sp1 ="1.0.1.0035sp1"; //add by panjunfang 2010.11.27
	public static final String YSS_VERSION_1010036 ="1.0.1.0036"; //add by panjunfang 2010.12.06
	public static final String YSS_VERSION_1010037 ="1.0.1.0037"; //add by panjunfang 2010.12.27
	public static final String YSS_VERSION_1010037sp1 ="1.0.1.0037sp1";//add by wangzuochun 2011.02.09
	public static final String YSS_VERSION_1010037sp2 ="1.0.1.0037sp2";//add by panjunfang 2011.02.14
	public static final String YSS_VERSION_1010038 ="1.0.1.0038"; //add by lidaolong 2011.01.25
	public static final String YSS_VERSION_1010038sp2 ="1.0.1.0038sp2"; //add by lidaolong 2011.01.25
	public static final String YSS_VERSION_1010039 ="1.0.1.0039"; //add by lidaolong 2011.03.14
	public static final String YSS_VERSION_1010039sp1 ="1.0.1.0039sp1"; //add by panjunfang 2011.03.31
	public static final String YSS_VERSION_1010040 ="1.0.1.0040"; //add by panjunfang 2011.03.31
	public static final String YSS_VERSION_1010041 ="1.0.1.0041"; //add by panjunfang 2011.05.19
	public static final String YSS_VERSION_1010041sp1 ="1.0.1.0041sp1"; //add by shashijie 2011.05.26
	public static final String YSS_VERSION_1010042 ="1.0.1.0042"; //add by panjunfang 2011.06.09
	public static final String YSS_VERSION_1010042sp1 ="1.0.1.0042sp1"; //add by panjunfang 2011.07.04
	public static final String YSS_VERSION_1010042sp3 ="1.0.1.0042sp3"; //add by songjie 2011.07.20 需求 1282 QDV4博时基金2011年6月29日01_A
	public static final String YSS_VERSION_1010043 ="1.0.1.0043"; //add by panjunfang 2011.07.11
	public static final String YSS_VERSION_1010043sp1 ="1.0.1.0043sp1"; //add by panjunfang 2011.08.09
	public static final String YSS_VERSION_1010043sp2 ="1.0.1.0043sp2"; //add by panjunfang 2011.08.09
	public static final String YSS_VERSION_1010044 ="1.0.1.0044"; //add by panjunfang 2011.08.09
	public static final String YSS_VERSION_1010044sp4 ="1.0.1.0044sp4"; //add by panjunfang 2011.09.14
	public static final String YSS_VERSION_1010045sp1 ="1.0.1.0045sp1"; //add by guolongchao 2011.09.26
	public static final String YSS_VERSION_1010047 ="1.0.1.0047"; //add by songjie 2012.09.29
	public static final String YSS_VERSION_1010048 ="1.0.1.0048"; //add by zhouwei 2011.11.23
	public static final String YSS_VERSION_1010049sp1 ="1.0.1.0049sp1"; //add by yeshenghong 2012.01.16
	public static final String YSS_VERSION_1010049sp2 ="1.0.1.0049sp2"; //add by songjie 2012.02.02
	public static final String YSS_VERSION_1010049sp3 ="1.0.1.0049sp3"; //add by panjunfang 2012.02.18
	public static final String YSS_VERSION_1010050 ="1.0.1.0050"; //add by yangshaokai 2011.12.31 STORY 2007
	public static final String YSS_VERSION_1010050sp1 ="1.0.1.0050sp1"; //add by yangshaokai 2012.03.01 STORY 2248
	public static final String YSS_VERSION_1010051 ="1.0.1.0051"; //add by yeshenghong 2012.03.16 STORY 2300
	public static final String YSS_VERSION_1010051sp1 ="1.0.1.0051sp1"; //add by yeshenghong 2012.03.16 STORY 2425
	public static final String YSS_VERSION_1010052 ="1.0.1.0052"; //add by yeshenghong 2012.03.16 STORY 2402
	public static final String YSS_VERSION_1010052sp1 ="1.0.1.0052sp1"; //add by yeshenghong 2012.03.16 STORY 2402
	public static final String YSS_VERSION_1010053 ="1.0.1.0053";//add by zhouwei 20120509 RQFII
	public static final String YSS_VERSION_1010053sp1 ="1.0.1.0053sp1";//add by huangqirong 2012-06-01 bug #4679、bug#4667
	public static final String YSS_VERSION_1010053sp2 ="1.0.1.0053sp2";
	public static final String YSS_VERSION_1010053sp3 ="1.0.1.0053sp3";  // add by zhangjun 2012.06.13 Story#2459
	public static final String YSS_VERSION_1010055 ="1.0.1.0055";  // add by songjie 2012.07.17 Story#2727
	public static final String YSS_VERSION_1010056 ="1.0.1.0056";  //add by songjie 2012.08.02 STORY #2188
	// by shashijie 2012.09.13 原需求1713 浮债少字段补充,也不知道为什么少这2个字段,现在补上sql语句
	public static final String YSS_VERSION_1010056sp3 ="1.0.1.0056sp3";
	// end
	public static final String YSS_VERSION_1010056sp3em1 ="1.0.1.0056sp3em1"; //add by zhaoxianlin	 2012.12.05 Story#3208
	//add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
	public static final String YSS_VERSION_1010056sp4em2 ="1.0.1.0056sp4em2";
	/**shashijie 2013-1-29 STORY STORY 运营费用增加费用品种类型*/
	public static final String YSS_VERSION_1010056sp6 ="1.0.1.0056sp6";
	public static final String YSS_VERSION_1010056sp7 ="1.0.1.0056sp7"; //add by huangqirong 2013.01.25 story #3488
	public static final String YSS_VERSION_1010056sp8em1 ="1.0.1.0056sp8em1";
	/**end shashijie 2013-1-29 STORY 3513*/
	public static final String YSS_VERSION_1010057 ="1.0.1.0057";  //add by songjie 2012.09.10 STORY #2344
	/**shashijie 2012-11-22 BUG 3612 分红转头*/
	public static final String YSS_VERSION_1010059 ="1.0.1.0059";
	/**end shashijie 2012-11-22 BUG */
	//add by songjie 2013.02.19 BUG 7102 QDV4招商银行2013年02月17日01_B
	public static final String YSS_VERSION_1010059sp1 ="1.0.1.0059sp1";
	/**add---shashijie 2013-2-27 STORY 3366 估值系统导出所有组合的彭博REQ文件的需求*/
	public static final String YSS_VERSION_1010059sp2 ="1.0.1.0059sp2";
	/**end---shashijie 2013-2-27 STORY shashijie*/
	/**add---shashijie 2013-3-8 STORY 2869  在导出时，系统根据用户选择那些组合群下的组合导出数据*/
	public static final String YSS_VERSION_1010060 ="1.0.1.0060";
	/**end---shashijie 2013-3-8 STORY 2869*/

	/**add---liubo 2013-5-22 STORY 3975  变更“电子对账对账报文处理信息表（TDzbbinfo）”的主键约束，改为联合主键*/
	public static final String YSS_VERSION_1010060sp1 ="1.0.1.0060sp1";
	/**end---liubo 2013-5-22 STORY 3975*/
	//--- add by songjie 2013.06.17 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 start---//
	public static final String YSS_VERSION_1010060sp2 ="1.0.1.0060sp2";
	//--- add by songjie 2013.06.17 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 end---//

	public static final String YSS_VERSION_1010060sp3 ="1.0.1.0060sp3";	//20130703 added by liubo.Story #4135
	
	public static final String YSS_VERSION_1010060sp4 ="1.0.1.0060sp4";	//20130813 added by yeshenghong #4151
	
	//当前报表扩展状态标记（扩展）
    public static final String YSS_REPORTSHOWDETAIL = "-";

    //数据协议
    public static final String YSS_LINESPLITMARK = "\f\f";
    public static final String YSS_ITEMSPLITMARK1 = "\t";
    public static final String YSS_ITEMSPLITMARK2 = "\n";
    public static final String YSS_PASSAGESPLITMARK = "\r\f";

    public static final String YSS_GROUPSPLITMARK = "<-AGP->";  // 跨组合群的解析符

    //数据库内码与客户端显示值间关联类型
    //MS00013 QDV4.1赢时胜（上海）2009年4月20日13_A  国内基金业务 2009.06.16 蒋锦 添加 货币基金结转类型 计息方式
    public static final String YSS_INCOMECAL_MONETARYFUND_RATERESULT = "pfsys_rateresule";      //红利结转方式 fundinvest-记入基金投资 recrate-记入应收红利
    public static final String YSS_INCOMECAL_MONETARYFUND_FUNDINSCALA = "pfsys_fundInsCala";    //复利计算方式 today-使用今日余额计算 yesterday-使用昨日余额计算
    //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
    public static final String YSS_INCOMECAL_MONETARYFUND_FUNDSTARTDATE = "pfsys_fundStartDate";//计提开始日期 1 - 申购确认日 2 - 申购确认日的下一个工作日
    public static final String YSS_INCOMECAL_MONETARYFUND_FUNDMFRATEDATE = "pfsys_fundMFRateDate";//获取基金万份收益的日期 1 - 公告日期（T+1日） 2 - 净值日期（T日）
    //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
    public static final String YSS_INCOMECAL_MONETARYFUND_INSMODE = "pfsys_fundMFMode";//story 2617 add  by zhouwei 20120511  增加计提模式的参数
    public static final String YSS_PARA_MONETARYFUND_CLOSEDTYPE = "para_closedtype"; //货币基金结转类型 day-日结型 month-月结型
    public static final String YSS_PARA_MONETARYFUND_INTERESTTYPE = "para_interesttype"; //计息方式 simple-单利；compound-复利
    public static final String YSS_SYS_RIGHTTYPE_PORT = "port"; //权限类型 组合级权限 2009.04.29 蒋锦 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
    public static final String YSS_SYS_RIGHTTYPE_GROUP = "group"; //权限类型 组合群级权限 2009.04.29 蒋锦 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
    public static final String YSS_SYS_RIGHTTYPE_PUBLIC = "public"; //权限类型 公共级权限 2009.04.29 蒋锦 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
    //----add by zhangjun 2011-12-05 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
    public static final String YSS_PARA_InsPeriodType = "para_InsPeriodType";  //投连保险费设置 contraction-收缩期 expansion-扩张期   
   
    public static final String YSS_REP_REPORTNAME = "report_name";      // 配制导出表的名称;fanghaoln A  : MS00794 QDV4华夏2009年11月02日01_A
    public static final String YSS_FUN_FLOWTYPE = "fun_FlowType";       // 0-普通流程;1-每日流程;fanghaoln BugNO  : MS00003 QDV4.1-参数布局散乱不便操作 090310
    public static final String YSS_FUN_FISMUST = "fun_FIsMust";         // 0-是;1-否;fanghaoln BugNO  : MS00003 QDV4.1-参数布局散乱不便操作 090310

    public static final String YSS_PARA_FACCOUNTINGWAY = "para_FAccountingWay"; //核算方式 0 -计入成本 1- 不计入成本                   //
    public static final String YSS_ACC_SETTLETYPE = "acc_settletype";   // 0-全部;1-已结算;2-未结算
    public static final String YSS_CFG_INOUT = "cfg_inout";             // 0-导入;1-导出

    public static final String YSS_PFM_RL_PERTYPE = "pfm_rl_pertype";   // 比率公式关联设置，0-绝对值;1-相对值
    public static final String YSS_PFM_LINKTYPE = "pfm_linktype";       // 比率公式设置，0-基础
    public static final String YSS_PFM_PERTYPE = "pfm_pertype";         // 比率公式设置，0-绝对值;1-相对值

    public static final String YSS_CRY_TRADEIND = "cry_tradeind";       // 币种配置，1－可兑换；0－不可兑换
    public static final String YSS_CRY_INVERTIND = "cry_invertind";     // 币种配置，0-正常;1-反转
    public static final String YSS_CRY_RATEWAY = "cry_rateway";         // 币种配置， 1－直接获取；0－通过基础货币转换   wdy add 20070822

    public static final String YSS_CRYW_QUOTEWAY = "cryw_quoteway";     // 货币方向配置  报价方向， OnetoX-1基准货币=X原币;XtoOne-1原币=X基准货币  LZP add 20071206
    public static final String YSS_FEE_FEETYPE = "fee_feetype";         // 费用设置，0-金额;1-数量;2-成本;3-收入;4-利息;5-费用

    public static final String YSS_PRD_DAYIND = "prd_dayind";           // 期间设置，0-计头不计尾;1-计尾不计头;2-头尾均计
    public static final String YSS_RND_SYMBOL = "rnd_symbol";           // 舍入设置，0-无论正负;1-舍入负值;2-舍入正值
    public static final String YSS_RND_RANGE = "rnd_range";             // 舍入设置，0-小数点后;1-小数点前

    public static final String YSS_STR_TYPE = "str_type";               // 板块类型，0-基本;
    public static final String YSS_RND_WAY = "rnd_way";                 // 舍入设置，0-普通;1-截断;2-取大;3-取小
    public static final String YSS_TDS_SEATTYPE = "tds_seattype";       // MAIN-主席位;COMMON-交易席位;QUERY-查询席位;INDEX-指数席位

    public static final String YSS_CNT_STATE = "cnt_state";             // 现金帐户设置，0-可用;1-冻结
    public static final String YSS_CNT_INTCYL = "cnt_intcyl";           // 现金帐户设置，0-按日计算;1-按月计算;2-按季计算;3-按年计算
    public static final String YSS_CNT_INTORG = "cnt_intorg";           // 现金帐户设置，0-内部;1-外部;2-不计息
    public static final String YSS_CNT_LINKLEVEL = "cnt_linklevel";     // 现金帐户链接设置，0－普通;1-优先

    public static final String YSS_FIX_QUOTEWAY = "fix_quoteway";       // 债券信息设置，0-全价;1-净价
    public static final String YSS_FIX_CALCINSWAY = "fix_calcinsway";   // 债券信息设置，0-标准法;1-摊余法
    public static final String YSS_FIX_INTORG = "fix_intorg";           // 债券信息设置，0-内部计算;1-外部计算;2-不计息
    public static final String YSS_FIX_Level = "CompGrade";             // 债券信息设置，0-AA 1-BB

    public static final String YSS_SCY_SDAYTYPE = "scy_sdaytype";       // 证券信息维护，0-工作日;1-自然日

    public static final String YSS_PRT_ENABLED = "prt_enabled";         // 1-可用;0-不可用
    public static final String YSS_PRT_COSTING = "prt_costing";         // 0-移动加权平均法;1-先进先出法
    public static final String YSS_PRT_PORTTYPE = "prt_porttype";       // 0-明细组合;1-汇总组合
    public static final String YSS_PRT_ASSETSOURCE = "prt_assetsource"; // 0-集团内部;1-集团外部
    public static final String YSS_PRT_ASSETTYPE = "prt_assettype";         //资产类型：01-证券投资基金 02-资产管理计划 03-信托计划产品 04-企业年金产品
    public static final String YSS_PRT_SUBASSETTYPE = "prt_subassettype";   //资产子类型：0101-普通基金 0102-指数基金 0103-指标基金 0104-社保基金 0105-货币基金 0106-ETF基金 0107-短债基金 0201-普通资产 0202-资产计划 0203-专户理财 0204-普通保险资产 0205-投连保险资产 0301-普通信托 0401-普通年金

    public static final String YSS_CST_STORAGEIND = "cst_storageind";   // 0－自动计算（未锁定）;1-自动计算（锁定）;2-初始化
    public static final String YSS_SST_STORAGEIND = "sst_storageind";   // 0－自动计算（未锁定）;1-自动计算（锁定）;2-初始化
    public static final String YSS_SCG_TYPE = "scg_type";               // Security-证券;Cash-现金
    public static final String YSS_TRD_AUTO = "trd_auto";               // 0-手工结算;1-自动结算
    public static final String YSS_TRD_SETTLETYPE = "settle_Type";      // 交易结算 0-已结算 1-未结算 2-回转 3-延迟结算
    public static final String YSS_CASHBOOK_DEFINE = "cbk_define";      // InvMgr-投资经理;CatType-自动结算;Port-投资组合;Account-帐户;
    public static final String YSS_MTN_QUOTEMODE = "mtn_quotemode";     // 订单制作，0-limit-Strict;1-limit-OB (orbetter)...
    public static final String YSS_ECG_DVPIND = "ecg_dvpind";           // 交易所设置，0-非DVP结算;1-DVP结算
    public static final String YSS_TDT_CASHIND = "tdt_cashind";         // 交易类型->资金方向 1流入；-1流出
    public static final String YSS_TDT_AMOUNTIND = "tdt_amountind";     // 交易类型->数量方向 1流入；-1流出
    public static final String Yss_Acc_ACCATTR = "acc_accattr";         //帐户属性设置 1资产; -1负债
    public static final String Yss_Acc_ACCSORT = "acc_accsort";         //帐户分类设置 1境内账户; 0境外账户  //add by zhaoxianlin 20121217 STORY #3384 外管局报表

    public static final String YSS_FUN_RETURNTYPE = "ReturnType";       //Spring 返回类型 S-字符型  D-日期型  U-双精度型
    public static final String YSS_FUN_MODULECODE = "ModuleCode";       //Spring 模板代码 dao-自定义数据接口  data-业务数据  para-业务参数  vch-凭证管理
    public static final String YSS_DAO_PRETTYPE = "Dao_PretType";       //预处理类型 0-数据源获取  1-函数获取
    public static final String YSS_CSP_DATASOURCE = "csp_datasource";   // 0-自动计算,1-手工
    public static final String YSS_CSP_STOCKIND = "csp_stockind";       // 0-未入帐,1-已入帐

    public static final String YSS_MTV_EXCHANGERATE = "ExchangeRate";   // 汇率来源的汇率字段名
    public static final String YSS_MTV_MTVMETHOD = "MTVMethod";         // 0-市价法;1-成本法;2-成本市价孰底法;3-摊余成本法
    public static final String YSS_MTV_MARKETVALUE = "MarketValue";     // 行情来源的行情字段名
    public static String YSS_CIC_COMPTYPE = "cic_comptype";             // 0－范围监控；1－条件监控；2－比例监控；3－比例范围监控
    public static String YSS_CIC_ATTRTYPE = "cic_attrtype";             // 0-持有类属性、1-交易类属性、2-品种属性
    public static String YSS_CIC_CONRELA = "cic_conrela";               // 0-AND、1-OR、2-终止
    public static String YSS_CIC_SIGN = "cic_sign";                     //0 ＝、1>、2<、3>=、4<=、5<>、6like、7in、8not 9in、10with 11same、12between
    public static String YSS_CA_ATTRTYPE = "ca_attrtype";               //0-持有类属性、1-交易类属性、2-品种属性、3-分母
    public static String YSS_CA_DATATYPE = "ca_datatype";               //0-字符型、1-数字型、2-金额、3-日期、4-时间
    public static String YSS_DB_DivdendType = "DivdendType";            //分红类型；0-Regular、1-Special、2-Liquidating
    public static String YSS_CSH_SavingType = "csh_savingtype";         //存款类型；0-定期、1-活期、2-通知、3-协定
    public static String yss_FEEMONEYJOINTYPE = "FeeMoneyJoinType";     //费用连接类型 0 交易型(原先), 1 赎回型(新加)
    
    /**add---shashijie 2013-3-30 STORY 3528 增加词汇*/
    public static final String YSS_MTV_INVESTMENTTYPE = "investmentType";//投资类型
	/**end---shashijie 2013-3-30 STORY 3528*/
    
    /**shashijie 2012-7-18 STORY 2796 */
    public static String YSS_CSH_FTakeType = "FTakeType";         //提取类型；0-本金、1-利息
	/**end*/
    
    public static String YSS_BUSSINESS_TYPE = "BUSSINESS_TYPE";//add by zhouwei 20120320 固定交易费用的所属类别 0-网上交易，1-场外回购业务，2-银行间债券业务，3-网下新股新债业务，4-开放式基金业务，5-债券转托管业务，6-存款业务
    public static String YSS_CI_BCompCond = "ci_BCompCond";     //事前监控条件;Broker-券商、Issue-发行人、Cat-品种、CA-现金帐户
    public static String YSS_CI_BORDER = "ci_border";           //0－否；1－是
    public static String YSS_CI_BCOMPWAY = "ci_bcompway";       //0－警告；1－禁止
    public static String YSS_CI_AORDER = "ci_aorder";           //0－否；1－是
    public static String YSS_CI_ACOMPWAY = "ci_acompway";       //0－警告；1－禁止
    public static String YSS_CI_ENDOFDAY = "ci_endofday";       //0－否；1－是
    public static String YSS_CI_RANGETYPE = "ci_rangetype";     //0－单个品种；1－品种集合
    public static String YSS_CI_RELAWAY = "ci_relaway";         //0-通用
    public static String YSS_CI_RANKFEETYPE = "fee_RankType";   //费用级别：0 -用户级 1 - 系统级 默认为用户级 QDV4.1赢时胜（上海）2009年4月20日25_A MS00025 by leeyu 20090713

    public static String YSS_RDS_DSTYPE = "rds_dstype";         //报表数据源设置；0-静态数据源；1-动态数据源
    public static String YSS_RDS_TEMPTAB = "rds_temptab";       //报表数据源设置；0-证券持有临时表；1-现金持有临时表
    public static String YSS_RDS_STORAGETAB = "rds_storagetab"; //报表数据源设置；0-默认请选择；表名-存储表描述

    public static String YSS_RCT_REPTYPE = "rct_reptype"; //报表自定义设置；0-明细报表；1-汇总报表
    public static String YSS_REP_TEMP = "rep_customtemp"; //报表自定义临时表；

    public static String YSS_REP_FUNCTYPE = "rep_functype";     //报表函数类型：0-系统内部函数； 1-自定义数据库函数
    public static String YSS_REP_FORMATTYPE = "rep_formattype"; //报表格式类型：0-固定报表；1-数据源报表；2-公式报表
    public static String YSS_REP_SAVETYPE = "rep_savetype";     //报表保存类型：0-不保存，1-按日期保存，2-按时间保存，3-按参数保存，4-按日期和参数保存，5-按时间和参数保存
    public static String YSS_REP_ALIGN = "rep_align";           //报表字段对齐方式：L- 左对齐；C-居中；R-右对齐
    public static String YSS_REP_ISTOTAL = "rep_istotal";       //报表字段是否合计：1-是；0-否
    public static String YSS_PCA_INTERESTWAY = "InterestWay";   //计息方式：0-余额计算；1-单笔计算
    public static String YSS_PCA_INTERESTALG = "InterestAlg";   //计息算法：0-默认算法；1-积数法
    public static String YSS_REP_FORMATVARIABLE = "rep_formatvariable"; //报表模板变量   story 1645 by zhouwei 20111212 QDII工银2011年9月13日10_A
    
    public static final String YSS_OPERTYPE = "opertype";       //0新增,1修改,2删除
    public static final String YSS_OPERRESULT = "operresult";   //0失败,1成功

    public static final String YSS_IDX_DATASOURCE = "idx_datasource"; //0自动 1 手动

    public static final String YSS_DEP_DURUNIT = "dep_unit";    //期限单位        ：0日；1周；2月；3年
    public static final String YSS_OPE_TYPE = "ope_type";       //操作类型     :system-系统;fund-业务
    public static final String YSS_RIGHT_TYPE = "right_type";   //权限类型     :system-系统；fund-业务
    public static final String Yss_REPORT_TYPE = "cusreport";   //報表操作類型   :0-導入;1-導出
    public static final String Yss_INVESTPAYCAT_TYPE = "investPayCat"; //收支类型  0-收入；1-支出

    public static String YSS_FIV_FEETYPE = "fiv_feeType";           //运营品种类型 accruedFee-预提  deferredFee-待摊  managetrusteeFee-两费
    public static String YSS_ACCOUNT_SUBJECT = "account_Subject";   //财务会计科目 0-资产  1-负债
    
    /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型*/
    public static String YSS_FOP_FEETYPE = "fop_feeType";
	/**end shashijie 2013-1-28 STORY 3513*/

    public static String YSS_BOND_APART = "bond_apart";         //0-不冲减债券成本，1-冲减且按净价计算，2-冲减且按全价计算

    public static final String Yss_FIV_TYPE = "fiv_type";       //运营品种类型 ASSET-资产   ACPAY-预提待摊
    public static final String Yss_INVESTPAY = "investPay";     //收支來源  0-昨日净值；1-当日净值
    public static final String YSS_YSSACCRUETYPE = "accrueType";//计提方式 EveDayNAV-按每日资产净值计提；ValDayNAV-按估值日资产净值计提，MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A panjunfang add 20090713
    public static final String YSS_FFieldType = "fieldtype";    //0 – 字符型 1 – 数字型 2 － 日期型 3 － 多字符型
    public static final String YSS_FKey = "key";
  //---------add by zhangjun 2012-05-12 Story #2459--------------------------
    public static final String YSS_AsserType_One = "CMB_AssetType_G1";  //资产类型（一级）  +
    public static final String YSS_AsserType_Two = "CMB_AssetType_G2";  //资产类型（二级）
    public static final String YSS_AsserType_Three = "CMB_AssetType_G3"; //资产类型（三级）  


    public static final String YSS_InvestType_One = "CMB_OperationType_G1";//运营类型（一级）
    public static final String YSS_InvestType_Two = "CMB_OperationType_G2";//运营类型（二级）

    public static final String YSS_INVESTBRANCH = "CMB_Branch";//运营分行
    public static final String YSS_SourceBranch = "CMB_Branch_LY";//来源分行

    public static final String YSS_InvestMagMode = "CMB_InvMgModel";//投资管理方式
    public static final String YSS_SellChannel = "CMB_SalesChannels";//销售渠道

    //---------add by zhangjun 2012-05-12 Story #2459--------------------------
    

    public static String YSS_MODE = "vch_mode";             //凭证分录模式   Single 单行取数   Multi 多行取数   //主键
    public static String YSS_DCWay = "vch_DCWay";           //借贷方向      0借   1贷
    public static String YSS_CONReal = "vch_ConReal";       //关系 and 并且 or 或者
    public static String YSS_SIGN = "vch_Sign";             //符号 0 ＝ 1> 2 < 3 >= 4 <= 5<> 6like 7in 8not 9between
    public static String YSS_VALSource = "vch_ValSource";   //值来源  0固定值  1参数值
    public static String YSS_OperSign = "vch_OperSign";     //运算符  0加      1减  2乘     3除    4结束
    public static String YSS_TYPE = "vch_Type";             //类型    0数量  1金额
    public static String YSS_FUNCTION = "vch_Function";     //凭证函数 max 最大值,min 最小值, sum 合计 avg 平均 no 无

    public static String YSS_CodeSubjectDict = "vch_CodeSubjectDict"; //代码科目字典设置
    public static String YSS_VALUETYPE = "vch_ValueType";   //值类型 0.静态，1.动态
   	public static String YSS_FORMAT = "file_Format";                       //日期型字段格式 yyyy-MM-dd yyyMMdd
   	public static String YSS_INFACE_ORDER = "dao_Inface_Order";             //字段排序　Asc-升序　Desc-降序 by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
    public static String YSS_INOUT = "file_Inout";          //导入导出
    public static String YSS_FILETYPE = "dao_Inface_FileType";          //文件?嘈?
    public static String YSS_INFACE_SPLITTYPE = "file_SplitType";       //分割类型  0-固定长度  1-符号分割
    public static String YSS_INFACE_VALUETYPE = "dao_Inface_ValueType";     // 文件名称-值类型  wdy add 20070826
    public static String YSS_INFACE_FILENAMECLS = "dao_Inface_FileNameC";   // 文件名称-文件名称类型  wdy add 20070826
    public static String YSS_INFACE_TYPE = "dao_Inface_Type"; //接口类型   交易类,销售类
    public static String YSS_INFACE_FILTERTYPE = "file_filtertype";         //筛选类型 Fix－固定　Port－组合　TreadSeat－席位　Holder－股东　Assest－资产代码 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
    public static String YSS_INFACE_RELATION = "filter_relation";           //条件关系　And－并且　Or－或者 NULL－无 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
    public static String YSS_FEE_ASSUMEMAN = "fee_assumeman"; //费用承担者

    public static String YSS_DICT_TABTYPE = "tab_type";     //表类型   0-系统表  1-临时表
    public static String YSS_CalcWay = "vch_calcWay";       // 计算方式 Common-普通  Netting-轧差   wdy add 20070919

    public static String YSS_TA_SellNetType = "ta_sellnetType"; //销售网点类型  0-直销  1-代销
    public static String YSS_TA_CalcWay = "ta_CalcWay";         //计算方式  Common-普通  Netting-轧差
    
    public static String YSS_TA_PORTCLS_RANK="TA_PORTCLS_RANK";//STORY 2254 ADD BY ZHOUWEI 20120226 组合分级级别
    public static String YSS_TA_PORTCLS_SCHEMA="ClassAccMethod";//STORY 2254 ADD BY ZHOUWEI 20120226 组合分级级别
    public static String YSS_TA_PORTCLS_CASH="TA_PORTCLS_CASH";//STORY 2254 ADD BY ZHOUWEI 20120226 组合分级级别
    public static String YSS_TA_PORTCLS_SHOWITEM="TA_Cls_ShowItem";//story 2727 etf ADD BY ZHOUWEI 20120620 组合分级显示项
    public static String YSS_TA_FundRightType = "ta_fundRightType"; //基金权益类型：基金分红-Dividend 基金拆分-Reinvest //panjunfang add 20090703 MS00023 QDV4.1赢时胜（上海）2009年4月20日23_A  国内TA业务
    public static String YSS_TA_TradeType = "ta_tradeType";         //交易类型  0-Open和 1-Close
    public static String YSS_TA_CatType = "ta_catType";             //品种类型  0-Spot 1-Forward 2-Swap
    public static String YSS_RATE_BFJType = "rate_bailType";		//add by huangqirong 2012-08-14 story #2822 保证金类型
    public static String YSS_RATE_REASON = "rate_reason";  //add by zhaoxianlin 20121218 STORY #3383 外管局报表  换汇原因
    public static String YSS_FUN_BailType = "fun_bailType";         //保证金类型  Fix-每手固定  Scale-比例\
    public static String YSS_FUN_FUType = "fun_fuType";             //期货类型  BuyAM－多头持仓  SellAM－空头持仓   lzp add 2007 11.23
    public static String YSS_PARA_PurchaseType = "para_purchaseType"; //RePh-正回购  UnPh-逆回购
    //add by zhangfa 20101023 抵押物信息设置
    public static String YSS_PARA_CollateralType="para_collateralType";//抵押物类型 CcT-信用交易抵押物
    public static String YSS_PARA_CollateralSubType="para_collateralSubT";//抵押物子类型 SlC-证券借贷抵押物
    //--------------end--------------------
    
    //add by zhangfa 20101206 证券借贷信息设置需求变更
    public static String YSS_PARA_STARTSDATE="para_startdate";//计息起始生日 :fbargaindate 交易日 ;fsettledate 结算日
    //---------------20101206---------------
    
    //add by zhangfa 20101028 证券借贷交易数据
    public static String YSS_DATA_AgreementType = "data_AgreementType";//协议类型 XsS-协商式 ZdS-制度式
    //--------------end-----------------------
    //add by zhangfa 20101102 抵押物补交数据
    public static String YSS_DARA_TransferType="data_TransferType";//调拨类型:现金-Cash 证券-Sec 组合-Port
    public static String YSS_DATA_TransferInOut="data_TransferInout";//1存入,-1取出;
    //---------------end-----------------------
    
    public static String YSS_FUN_AttrSrc = "fun_AttrSrc";           //通用参数属性设置  Voc-词汇 Other-其他   lzp add 2007 12.17
    public static String YSS_FUN_CondType = "fun_CondType";         //通用参数属性设置  Assetgroup-组合群 Port-组合 SecCat-证券品种类型 SecCatSub-证券品种子类型  CashCat-现金品种类型  CashCatSub-现金品种子类型   lzp add 2007 12.18

    public static String YSS_CAL_TYPE = "cal_Type";         //利息算法类型 Bond-债券利息  Purchase-回购利息
    public static String YSS_VER_FINISH = "ver_Finish";     //版本信息 Success-成功  Fail-失败
    public static String YSS_PFOPER_FUNMODULES = "pfoper_funmodules";   //调度方案功能模块 bond-债券计息，cash-现金计息，Fee-两费计提，purchase-回购计息，invest-权益处理，valcheck-估值检查，valuation-资产估值，vchproject-凭证方案
    public static String YSS_INVEST_OPERTYPE = "invest_opertype";       //权益处理_业务类型

    public static String YSS_PURCHASE_InBeginType = "purchase_inbegtype"; //tradedate - 交易日;settledate - 结算日

    //---------------------------财务模块部分---------------------------------------------------------------------
    public static String YSS_ACC_AuxType = "Acc_AuxType";               //核算项目类型:Basic（基础项目），Extra（扩展项目）
    public static String YSS_ACC_CompOperator = "Acc_CompOperator";     //比较运算符:>（大于），>=（大于等于），<（小于），<=（小于等于），!=（不等于），in（包含）
    public static String YSS_ACC_LogicOperator = "Acc_LogicOperator";   //逻辑运算符:none（无），and（并且），or（或者）
    public static String YSS_FUN_FieldType = "Sys_FieldType";           //字段类型:string（字符），number（数字），date（日期）
    public static String YSS_ACC_SetType = "Acc_SetType";               //字段类型:Unit（业务子账），Collection（业务总账），Group（操作总账）
    public static String YSS_ACC_SubjectClass="Acc_SubjectClass";       //科目类别：Asset（资产类），Debt（负债类），Common（共同类），OwnersEquity（权益类），ProfitAndLoss（损益类）
    public static String YSS_ACC_BalDC="Acc_BalDC";                     //余额方向：J（借），D（贷），ZJFD（正借负贷），ZDFJ（正贷负借）
    public static String YSS_ACC_AssetType="Acc_AssetType";             //资产类别：None（无分类），Trading（交易性），HeldToMaturity（持有至到期），FairValueMeasure（指定公允价值计量），AvailableForSale（可供出售）
    public static String YSS_ACC_VchExecFreq="Acc_VchExecFreq";         //执行频度：Daily（每日），EndOfMonth（月末），EndOfYear（年末）
    public static String YSS_ACC_CopyMode = "Acc_CopyMode";             //复制模式：Copy（同步模式），Overlay（覆盖模式），Append（追加模式）

    public static final int BookType_General=0;             //汇总账簿
    public static final int BookType_DailyDetail=1;         //日记明细账
    public static final int BookType_CommonDetail=2;        //普通明细账

    public static String YSS_ACC_VchCompOper = "Acc_VchCompOper";   //比较 > < >= <= = <> like in not between 空
    public static String YSS_ACC_VchLogicOper = "Acc_VchLogicOper"; //逻辑 or and 空
    public static String YSS_ACC_VchFieldName = "Acc_VchFieldName"; //字段 TB_XXX_ACC_VOUCHER
    public static String YSS_ACC_VchFilter="Acc_VchFilter";         //凭证分录滤除条件：
    //---------------------------财务模块部分---------------------------------------------------------------------
    //---------------------------xuqiji 20091027 ETF台账报表部分-----------------------------------//
    public static String YSS_ETFBOOK_SubscribeData = "ETF_Subscribe";//ETF台账报表申购数据列参数设置
    public static String YSS_ETFBOOK_RedeemData = "ETF_Redeem";      //ETF台账报表赎回数据列参数设置
    public static String YSS_ETFBOOK_RightData = "ETF_Right";//ETF台账报表权益数据列参数设置
    public static String YSS_ETFBOOK_SupplyAndForceData = "ETF_Supply";//ETF台账报表补票和强制处理数据列参数设置
    public static String YSS_ETFBOOK_OtherData = "ETF_Other";//ETF台账报表其它数据列参数设置
    public static String YSS_ETFBOOK_QUITMONEYVALUE = "ETF_quitMoneyValue";//ETF台账报表应退款估值增值列参数设置
    //---------------------------end 20091027----------------------------------------------------//

    //监控指标配置
    public static String YSS_CIC_FINALCOMP = "cic_finalcomp";   //日终监控 Ture-是，False-否
    public static String YSS_CIC_BEFORECOMP = "cic_beforecomp"; //事前监控 Ture-是，False-否
    public static String YSS_CIC_MEMOYWAY = "cic_memoyway";     //储存方式 Table-表储存，View-视图储存，NO-不储存
    public static String YSS_CIC_INDEXTYPE = "cic_indextype";   //指标类型 Dynamic-动态指标数据源，Fix-固定指标数据源

    public static String YSS_OPER_COST = "prt_costing";         //成本计算 0-	移动加权平均法 1-	先进先出法 2-汇兑损益已计入实现收益 3-汇兑损益未计入实现收益
    public static String YSS_OPER_INCOMEPAID = "incomepaid";    //收益支付
    public static String YSS_OPER_INCOMESTAT = "incomestat";    //收益计提
    public static String YSS_OPER_STORAGESTAT = "storagestat";  //库存统计
    public static String YSS_OPER_VALUATION = "valuation";      //资产估值

    public static String YSS_VCH_SUBATTR = "vch_subattr";   //凭证科目性质  Asset—资产,Debt—负债,Right—权益,ProfitAndLoss—损益,Together—共同
    public static String YSS_VCH_EXBUILD = "vch_exbuild";   //凭证生成方案执行生成 0—是,1—否
    public static String YSS_VCH_EXCHECK = "vch_excheck";   //凭证生成方案执行审核 0—是,1—否
    public static String YSS_VCH_EXINSERT = "vch_exinsert"; //凭证生成方案执行导入 0—是,1—否
    public static String YSS_VCH_INOUTMDB = "vch_inoutmdb"; //导入导出凭证设置信息 0—导入,1—导出
    public static String YSS_VCH_ALLOWS = "vch_allows";     //凭证分录允许做的部分 0:无、AisZero:数量允许为零、MisZero:金额允许为零、AMisZero:数量金额允许为零     QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209

    public static String YSS_PF_FUNMODULES = "pf_funmodules";               //界面配置 系统功能 RepParam-报表参数  CompParam-监控参数
    public static String YSS_PFOPER_DAYREPORT = "pfoper_dayreport";         //业务平台 调度方案设置 日终报表
    public static String YSS_PFOPER_SETTLETYPE = "pfoper_settletype";       //业务平台 调度方案设置 业务类型
    public static String YSS_PFOPER_INCOMETYPE = "pfoper_incometype";       //业务平台 调度方案设置 收益计提类型 BugNo:MS01297 add by lvhx 20100624
    public static String YSS_PFSYS_OPEREXTENDLINKMOD = "operextend_link";   //系统平台 通用业务扩展设置 关联模块
    public static String YSS_PFSYS_OPEREXTENDENABLE = "operextend_enable";  //系统平台 通用业务扩展设置 是否可用

    public static String YSS_TA_TAMODE = "TA_TAMode";           //中登TA模式 TA_SHZD：上海中登模式 TA_SZZD：深圳中登模式.
    public static String YSS_TA_COLLECTSTYLE = "TA_CollectTyle";   //add by liuxiaoju 20130724 story #4094
    public static String YSS_TA_NETTYPE = "TA_NETType";          //add by liuxiaoju 20130724 story #4094
    public static String YSS_TA_DATASOURCE = "TA_DataSource";          //add by liuxiaoju 20130724 story #4094
    
    public static String YSS_TA_TradeUsage_GCS = "TradeUsage_GCS" ; //add by huangqirong 2012-04-12 story #2326
    public static String YSS_TA_SELLTYPE = "TA_SellType";       //TA划款方式 01申购款金额 02	净赎回款金额 03赎回款金额  04净转出款金额 05转出款金额  06赎回转出款金额  07申购转入款金额   
    public static String YSS_FIX_OrgCodeType = "orgCodeType";   //机构代码类型 目前只有一个BIC 2008-6-3 单亮
    //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao start ---//
    public static String YSS_FIX_CREDITRATING = "creditrating";
    public static String YSS_FIX_MANAGERORG = "managerorg";
    //--- STORY #1509 监控管理－监控结果 建立诺安QDII基金指标库，具体指标见《监控指标》  add by jiangshichao end ---//
    public static String YSS_GEN_DIVIDENDTYPE = "DivdendType";  //股票分红类型 BugNo:0000328 edit by jc
    public static String YSS_ENCODING_TYPE = "encoding_type";   //编码类型 default—默认编码、utf-7—UTF-7、utf-8—UTF-8、utf-16—UTF-16、utf-32—UTF-32、unicode—UniCode、ascii—ASCII

    public static String YSS_FURTURE_CASHINOUT = "Furture_CashInOut"; //期货帐户流动方向 1:存入 -1:取出
    public static String YSS_COUNTRY_AGREEMENT = "country_agreement"; //已签署的协议 BugNo:MS00035 edit by sunkey at 20081128

    public static String YSS_FUNCTION_TYPE = "function_type"; //公式类型 datainterface：公式类型 by leeyu add MS00032
    //通用业务类型设定
    public static String YSS_TYYWLX_TASELLINTEREST = "TASellInterest"; //TA 申购款计息

    public static String YSS_PARA_PERIODTYPE = "para_periodtype"; //期间类型 0:固定天数 1:实际天数
    
   
    public static Integer YSS_USER_OLDPASS_COUNT = 0;   //登录时判断用户是否是修改密码后登录   //add by zhaoxianlin 20120817 Story #2766 
    public static String YSS_BAIL_ACC_SETTINGSTATE = ""; //期货交易数据保证金账户设置状态   现金账户已设置     //add by zhaoxianlin 20121105 #story 3159
    
    public static String YSS_DAO_ACCOUNTTYPE = "dao_accounttype";   //接口　比对核算方式 0:不核算；1:相对值； 2:绝对值 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090304
    public static String YSS_DAO_DSTYPE = "dao_dstype";             //接口数据源类型；0-静态数据源；1-动态数据源;2-固定数据源;3-参数数据源;4-提示数据源;5-核对数据源  QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090304

    public static String YSS_FUN_SHOWALL = "show_YesOrNo";  //公式RelaFieldIsNullX 对应是否全部显示：0-否，1-是
    public static String YSS_FUN_CONTINUE = "YesOrNo";      //公式RelaFieldIsNullX 对应是否继续：0-否，1-是
    public static String YSS_FUN_MESSAGE = "MessageType";   //公式RelaFieldIsNullX 对应是否信息类型：0-alarm，1-error,3-message

    public static String YSS_SYS_RIGHT_RIGHTTYPE = "sys_righttype"; //权限类型代码

    public static String YSS_SPECISE_RATETYPE = "rate_ratetype";        //交易费率品种设置_费率类型 1:公共费率、2:套帐选择
    public static String YSS_SPECISE_SPECIESTYPE = "rate_ratespecies";  //交易费率品种设置_费率品种
    public static String YSS_BROKER_RATEB = "broker_rateb";             //券商佣金利率设置_席位地点
    public static String YSS_BROKER_EQType = "broker_EQType";			//券商佣金利率设置_股票类型
    

    public static String YSS_CASHSAVE_TRADETYPE = "cash_tradetype"; //计息方式   首期 买入 转出 通知取款
    public static String YSS_CASHSAVE_CALCTYPE = "cash_calctype";   //交易类型 固定利率 固定收益

    public static String YSS_TRADETYPE_SERVICE = "TradeType_ServiceTyp";    //交易类型设置_业务类型
    public static String YSS_InvestType = "investType";                     //投资类型：C-交易性；S-可供出售；F-持有到期；X-委托现券
    public static String YSS_BondTradeType = "bondTradeType";//债券业务交易类型：bond_trusteeship-债券转托管，bond_circulation -债券转流通 xuqiji 20100413
    public static String YSS_DATA_SECURITYTYPE = "data_securitytype";       // 证券类型
    public static String YSS_OPENFUND_INVESTTYPE = "openfund_ivesttype";    // 交易类型设置_交易类型代码
    
    //---------------------xuqiji 20091029--------------------//
    public static String YSS_SUPPLY_MODE = "Supply_Mode";//补票方式: 先到先得-0  均摊方式-1  实时补票-2
    public static String YSS_ETF_BOOKGATHERMODE = "ETF_BookGatherMode";//台账汇总方式 0---按股票汇总、1---按投资者汇总
    public static String YSS_BAILMONEY_SET = "bailmoneyset";//期权和期货保证金账户设置 MS00562    期权和期货结算估值的保证金账户需要独立的界面让用户指定    
    //---------------------end-------------------------------//
  //------------yeshenghong add 20120415 story2425     
    public static String YSS_BASE_AccounttingType = "AccountingType";//核算类型
    public static String YSS_BASE_ZQSubAccType = "AT_ZQ";//证券类核算子类型
    public static String YSS_BASE_XJSubAccType = "AT_XJ";//现金类核算子类型
    public static String YSS_BASE_ReqField = "AT_ReqField";//核算关系必输项
    public static String YSS_BASE_DataType = "AT_DataType";//数据类型
    public static String YSS_BASE_SubjectType = "AT_SubType";//科目类型
    //------------------------------end yeshenghong---//
    
    /*added by yeshenghong 2013-5-23 Story 3759 */
    public static String YSS_TA_DiscountType = "TA_DiscountType";//折算类型
	/*end by yeshenghong 2013-5-23 Story 3759 */
    //数据库类型定义
    public static final byte DB_SQL = 0;
    public static final byte DB_ORA = 1;
    public static final byte DB_DB2 = 2;
    
    //数据库版本信息
    public static String Yss_OracleVersion = "";

    //数据库连接方式
    public static byte Yss_DB_ConType = 0;//0	普通连接方式【默认】；1	连接池连接方式  add by jsc STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
    public static String serviceType = "";//应用服务器类型
    
    //显示格式
    public static final String YSS_DATEFORMAT = "yyyy-MM-dd";
    public static final String YSS_DATETIMEFORMAT = "yyyyMMdd HH:mm:ss";

    //存放数据库脚本的文件名
    public static final String Yss_ORA_CREATETABLE_SCRIPT = "imsas_ora_createtable.sql";
    public static final String Yss_ORA_CREATETABLE_SCRIPT_PREFIX = "imsas_ora_createtable_prefix.sql";
    public static final String Yss_ORA_ALTERTABLE_SCRIPT = "imsas_ora_altertable.sql";
    public static final String Yss_ORA_ALTERTABLE_SCRIPT_PREFIX = "imsas_ora_altertable_prefix.sql";

    //存放表名的脚本文件名
    public static final String Yss_TABLENAME_SCRIPT = "imsas_tablename.txt";
    public static final String Yss_TABLENAME_SCRIPT_PREFIX = "imsas_tablename_prefix.txt";

    //session变量名称定义
    public static final String SS_DBLINK = "yss_database_connection";   //数据库连接对象
    
    //---add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A start---//
    public static final String SS_DBLINK_BLOG = "yss_database_connection_BLog";   //业务日志数据库连接对象
    public static final String DB_CONNECTION_BLOG ="db_connection_BLog";//业务日志数据库连接
    public static String DB_CONNECTTION_RECENTLY = "[db_yssimsas]";//最近登录的数据库标识
    //---add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A end---//
    
    public static final String SS_PUBLIC = "yss_public_environment";    //全局变量对象
    public static final String SS_RUNSTATUS = "yss_run_status";         //运行状态对象

    //其它常量
    public static final String ERROR_TO_CLIENT = "A401"; //通过response发给客户端异常消息时用来setHeader
    public static final String CONTENT_TEXT = "text/html; charset=GB2312";
    public static final String Yss_LOG_PORPERTIES = "log4j.properties";

    //定义全局变量
    public static String Status_ReadData = "";
    public static String Status_sqlAnalysis = "";
    public static String Status_OperRights = "";

    //操作类型
    public static final byte OP_MutliAdd = 100;    //跨组合操作   1770
    public static final byte OP_MutliEdit =101;
    public static final byte OP_ADD = 0;        //增加
    public static final byte OP_EDIT = 1;       //修改
    public static final byte OP_DEL = 2;        //删除
    public static final byte OP_AUDIT = 3;      //审核
    public static final byte OP_js = 4;			//结算
    public static final byte OP_fjs = 5;		//反结算
    public static final byte OP_kctj = 6;		//库存统计
    public static final byte OP_syjt = 7;		//收益计提
    public static final byte OP_gztj = 8;		//估值统计
    public static final byte OP_dr = 9;			//导入
    public static final byte OP_dc = 10;		//导出
    public static final byte OP_UNAUDIT = 11;	//反审核
    public static final byte OP_CLEAR = 12;     //清除
    //add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    public static final byte OP_REVERT = 35;     //还原 
    public static final byte OP_Login = 15;		//用户登录
    public static final byte OP_BUILD = 16;     //生成财务估值表
    public static final byte OP_DAYCON = 17;    //当日确认
    public static final byte OP_DAYUNCON = 18;  //当日反确认
    public static final byte OP_ycjs = 21;		//延迟结算
    public static final byte OP_hz = 22;		//回转
    public static final byte OP_fhz = 23;		//反回转
    public static final byte OP_SEND=29;		//edited by zhouxiang MS01628 2010.10.25 报文发送
    public static final byte OP_INTERRUPT=28;	//edited by zhouxiang MS01635 2010.10.25 在线用户管理中断用户 
    public static final byte OP_CANCEL=30;		//edited by zhouxiang MS01628 2010.10.25 指令撤销
    public static final byte OP_BUILDSUB=31;	//add by qiuxufeng MS01620 2010.10.26 生成对账科目表
    public static final byte OP_BUILDJJGZB=32;	//add by qiuxufeng MS01620 2010.10.26 生成对账估值表
    public static final byte OP_BUILDBAL=33;	//add by qiuxufeng MS01620 2010.10.26 生成对账余额表


    //数据记录状态
    public static final byte RS_AUDIT = 1;      //已审核状态
    public static final byte RS_UNAUDIT = 0;    //未审核状态
    public static final byte RS_DEL = 2;        //删除状态

    //定义控件的标识代码
    public static String CTL_PORTTYPE = "<portset>";    //标识为组合的控件标识
    public static String CTL_DATETYPE = "<dateyear>";   //标识为日期的控件标识

    //流程点执行状态的常量
    public static int YSS_FLOW_POINTSTATE_UNFINISHED = 0;   //未执行
    public static int YSS_FLOW_POINTSTATE_SUCCESS = 1;      //执行成功
    public static int YSS_FLOW_POINTSTATE_FALSE = 2;        //执行失败
    public static int YSS_FLOW_POINTSTATE_EXECUTION = 3;    //执行中
    public static int YSS_FLOW_POINTSTATE_PARTOF = 4;       //部分完成

    public static final String YSS_PARASET_TRADETYPECODETYPE = "para_TradeTypeCodeTy";  //认购：CALL、认沽：PUT
    public static final String YSS_PARASET_EXECUTETYPECODE = "para_ExecuteTypeCode";    // 美式：USA、欧式：EUR
    public static final String YSS_PARASET_FFUType = "para_FFUType";                    //BuyAM－多头持仓  SellAM－空头持仓
    public static final String YSS_CONTRACT_INVESTTYPE = "contractInvestType";          //值域：arb套利、mtl保值；

    //权证相关
    public static String YSS_PARA_WARRANTTYPECODE = "para_WarrantTypeCode"; //权证类型：认购-Call、认沽-Put
    public static String YSS_PARA_EXERCISETYPECODE = "para_ExeTypeCode";    //行权方式：美式-USA、欧式-EUR、百慕大-BMD
    public static String YSS_PARA_SETTLETYPECODE = "para_SettleTypeCode";   //结算方式：现金-Cash、实物-Res


    //swift相关
    public static String YSS_SWIFT_App_TagD="App_TagD";             //应用标识: 金融应用-F  应用控制-A  逻辑终端控制-L
    public static String YSS_SWIFT_Ser_TagD="Ser_TagD";             //服务标识: Message-01
    public static String YSS_SWIFT_GetS_TagD="GetS_TagD";           //收发标识:  输入报文-I 输出报文-O
    public static String YSS_SWIFT_Mess_TypeD="Mess_TypeD";         //报文类型:MT1XX-MT1XX MT3XX-MT3XX MT5XX-MT5XX MT9XX-MT9XX
    public static String YSS_SWIFT_Prior_ClassD="Prior_ClassD";     //优先等级: 普通-N 加急-U 系统-S
    public static String YSS_SWIFT_Send_ConD="Send_ConD";           //传送监控:未送达警告和送达通知-3   送达通知-2  未送达警告-1
    public static String YSS_SWIFT_User_ReD="User_ReD";             //用户参考:报文参考号-refer 自定义-userdefine
    public static String YSS_SWIFT_CRITERION = "swift_criterion";   //报文标准: 0-ISO15022;1-ISO29922;
    public static String YSS_SWIFT_OPERTYPE = "swift_operType";     //业务类型: 0全部业务   1支付业务   2外汇业务   3证券业务   4现金对账
    public static String YSS_SWIFT_REFLOW = "swift_reflow";         //报文流向:0导入1导出
    public static String YSS_SWIFT_ENTITY_STATE = "swift_estate";   //报文状态:   “M”、“O”、“---->”、“-----|”。
    public static String YSS_SWIFT_STATUS = "swift_status";			//报文状态 by leeyu add 20091111

    //凭证模块相关
    //---edit by songjie 2013.01.18 默认模式改为  批处理 start---//
    public static String YSS_VCH_BUILDER_MODE = "batch";      //凭证生成模式  default 默认模式(老模式) batch 批量模式
    public static String YSS_VCH_CHECK_MODE = "batch";        //凭证审核模式  default 默认模式(老模式) batch 批量模式
    public static String YSS_VCH_DOOUTACC_MODE = "batch";        //凭证导出模式  default 默认模式(老模式) batch 批量模式
    //---edit by songjie 2013.01.18 默认模式改为  批处理 end---//
    
    /*added by yeshenghong 2013-6-7 Story 3958 */
    public static String YSS_VCH_DICT_CashInBank = "YSS_DCT907";   
    public static String YSS_VCH_DICT_OtherReceivalbes = "YSS_DCT908";    
    public static String YSS_VCH_DICT_OtherPayables = "YSS_DCT909"; 
    public static String YSS_VCH_DICT_Interest = "YSS_DCT910";   
    public static String YSS_VCH_DICT_Revenue = "YSS_DCT911";    
    public static String YSS_VCH_DICT_ProfitLossCost = "YSS_DCT904"; 
    public static String YSS_VCH_DICT_AccrualDeferral = "YSS_DCT905";   
    public static String YSS_VCH_DICT_VoucherAbstract = "YSS_DCT906";    
	/*end by yeshenghong 2013-6-7 Story 3958 */
    
    //add by songjie 2013.01.23 用于保存fundacc.lic文件中的公司名称
    public static String companyName = "";//公司名称
    
    ///**Start---panjunfang 2013-11-18 BUG 83523 */
    public static String YSS_WebRealPath = "";//应用程序的绝对路径 
	/**End---panjunfang 2013-11-18 BUG 83523*/
	
    //add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
    //股指期货合约价值 财务估值表市值算法 default 默认算法(老算法) new 新算法 
    public static String YSS_STOCK_INDEX_FUTURE_MV_MODE = "default"; 
    //add by songjie 2012.09.18 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A
    public static String YSS_EWCLUE_MODE = "hide"; //预警执行模式 default
    //add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
    public static boolean YSS_EXP_MONITOR_FILE = false;
    
    public static String sInterestInfoCollected = "";	//20130407 added by liubo.Story #3714.此变量用于在处理债券派息时，汇总派息债券的数据，这些数据需要返回给前台
    
    //20121126 added by liubo.Story #3057.若前台不操作系统的时间，超过这个设置的值，则前台不需要再自动尝试连接后台（即断线重连功能）
    //若设置的值为0，则不启用这个功能
    public static String YSS_DEFER_TIME = "0";	
    public static String YSS_WGJRep_BuldingMode = "JH";			//20130220 added by liubo.Story #3517.外管局月报的生成模式。JH表示建行模式，NH表示农行模式
    public static String[] arrTables = {
        "Broker", "ClientInfo", "ClientLink", "CurrencyInfo", "CashAccount_temp", "Exchange", "Manager"
        , "Trustee", "Portfolio", "Security_temp", "ExchageRate", "Market"
        , "Interest", "NAV", "TransCash", "TransMain", "TransConversion"
        , "TransRepo", "sectorGroup", "attrGroup", "Category", "subCategory"
        , "transType", "Hold", "LrptGroup", "LRptList", "LTabList"
        , "LTabCell", "Attribute", "AttrGroup", "AttrLink", "cusCategory"
        , "sector", "sectorChild", "Region", "FI", "creditLevel"
        , "FIcalc", "Fee", "LrptLink", "Income", "lCusRptList"
        , "lCusRptSql", "lCusRptSqlSelect", "lCusRptField", "lCusRptCondition", "lCusRptReturn"
        , "TransCkYw", "RelateCorp", "unSecurity_temp", "otherHold", "schedule"
        , "taskGroup", "task", "role"};

    public static String[] arrFileTypes = {
        "Broker", "ClientInfo", "ClientLink", "CurrencyInfo", "CashAccount_temp", "Exchange", "Manager"
        , "Trustee", "Portfolio", "Security_temp", "ExchageRate", "Market"
        , "Interest", "NAV", "TransCash", "TransMain", "TransConversion"
        , "TransRepo", "sectorGroup", "attrGroup", "Category", "subCategory"
        , "transType", "Hold", "LrptGroup", "LRptList", "LTabList"
        , "LTabCell", "Attribute", "AttrGroup", "AttrLink", "cusCategory"
        , "sector", "sectorChild", "Region", "FI", "creditLevel"
        , "FIcalc", "Fee", "LrptLink", "Income", "lCusRptList"
        , "lCusRptSql", "lCusRptSqlSelect", "lCusRptField", "lCusRptCondition", "lCusRptReturn"
        , "TransCkYw", "RelateCorp", "unSecurity_temp", "otherHold", "schedule"
        , "taskGroup", "task", "role"};

    public static String[] arrCnFileTypes = {
        "券商信息表", "客户信息表", "客户套帐表", "货币信息表", "现金账户信息表", "交易所信息表", "管理人信息表"
        , "托管人信息表", "组合信息表", "证券信息表", "汇率行情表", "交易行情表"
        , "每日利息统计表", "净值统计表", "现金交易统计表", "主交易统计表", "转换交易统计附表"
        , "回购交易统计附表", "板块分类群", "属性群", "投资品种类型表", "投资品种子类型表"
        , "交易类型表", "持仓统计表", "报表群设置", "报表设置Ⅰ", "报表设置Ⅱ"
        , "报表设置Ⅲ", "属性特征设置", "属性群设置", "属性链接设置", "自定义投资品种表"
        , "板块分类", "板块分类链接", "地域设置", "债券信息设置", "信用评级信息"
        , "债券评价数据", "费用业务统计表", "报表链接设置", "收入统计表", "自定义报表"
        , "自定义sql语句", "自定义表名和字段名", "自定义筛选字段", "自定义查询条件", "自定义返回字段"
        , "存款业务副表", "关联单位表", "非交易类品种类型设置", "非交易类持仓", "调度设置"
        , "任务群设置", "任务设置", "角色设置"};

    public static String[] innerArrFileTypes = {
        "CashAccount", "Security", "unSecurity"};


    /**20131223 added by liubo.Bug #85825.QDV4赢时胜(上海)2013年12月18日01_B
     * 记录连接状态，主要用于记录当前用户是否被管理员从在线用户管理界面中中断。true为连接状态，false为中断状态*/
    public static boolean YSS_Connection_Status = true;
    
    public static String YSS_BusinessLog_GenerateType = "3";
    
}
