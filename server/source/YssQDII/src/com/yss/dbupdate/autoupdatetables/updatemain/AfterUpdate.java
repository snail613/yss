package com.yss.dbupdate.autoupdatetables.updatemain;

import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.DB21010017;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.DB21010020;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.DB21010022;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.DB21010027sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010017;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010020;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010022;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010026;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010027sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010027sp2;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010028;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010028sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010029;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010030;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010031;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010031sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010032;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010033;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010033sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010034;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010035;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010036;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010037;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010037sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010038;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010039;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010040;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010041sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010042;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010042sp3;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010043;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010043sp2;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010044;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010044sp4;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010045sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010047;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010048;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010049sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010049sp2;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010049sp3;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010050;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010050sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010051;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010051sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010052;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010052sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010053;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010053sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010053sp2;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010053sp3;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010055;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010056;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010056sp3;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010056sp4em2;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010056sp7;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010056sp8em1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010057;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010059;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010059sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010059sp2;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010060;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010060sp1;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010060sp2;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010060sp3;
import com.yss.dbupdate.autoupdatetables.afterupdateclass.Ora1010060sp4;
import com.yss.dsub.BaseBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;

/**
 *
 * <p>Title:自动更新后的手工表结构调整 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AfterUpdate
    extends BaseBean { //QDV4南方东英2010年12月14日01 edit by lidaolong 2011.01.14
    BaseDbUpdate dbUpdate; //直接执行更新的类
    public AfterUpdate() {
    }

    /**
     * 执行更新
     * @param hmInfo HashMap：返回的信息
     * @param sCurrtVerNum String：版本号
     * @return String
     * @throws YssException
     */
    public String afterExecute(HashMap hmInfo, String sCurrtVerNum) throws YssException {
        String sResultTag = YssCons.YSS_DBUPDATE_SUCCESS;
        //记录所有涉及结构调整和数据调整的 SQL 语句
        StringBuffer sqlBuf = new StringBuffer(500000);
        //记录更新过程中出现的异常
        StringBuffer errBuf = new StringBuffer(10000);
        //记录完成更新的表名
        StringBuffer updTables = new StringBuffer(2000);
        try {
            sqlBuf = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
            errBuf = (StringBuffer) hmInfo.get("errinfo"); //用于获取异常信息
            updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
            
         //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-11----//
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010017)) {//若要更新到17版本
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010017(); //新建Ora1010017
                 dbUpdate.setYssPub(this.pub); //设置数据库连接
                 dbUpdate.doUpdate(hmInfo); //执行历史数据转换
             } else if (dbl.getDBType() == YssCons.DB_DB2) { //判断是DB2数据库的更新
                 dbUpdate = new DB21010017(); //新建DB21010017
                 dbUpdate.setYssPub(this.pub); //设置数据库连接
                 dbUpdate.doUpdate(hmInfo); //执行历史数据转换
             }
         }
         //----MS00590:QDV4赢时胜（上海）2009年7月24日09_B 转换角色权限 fanghao 20090813----//
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010020)){
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010020();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new DB21010020();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
       //----MS00796:QDV4南方2009年11月4日01_B  SWIFT报文设置中新建、修改、复制操作有问题 fanghao 20091111----//
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010022)){
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010022();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new DB21010022();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         //-xuqiji 2010-02-05 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理---//
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010026)){
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010026();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new DB21010022();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
		 		          //-fanghao 2010-04-15 删除临时表---fd //    
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010027sp1)){
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010027sp1();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new DB21010027sp1();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
		 //yanghaiming 2010-03-17 MS00839 QDV4华夏2009年11月26日01_A 
         if	(sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010028)){
        	 if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010028();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new DB21010022();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         //---------------------xuqiji 20100330-------------------------//
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010027sp2)){
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010027sp2();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010027sp2();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         //---------------------panjunfang 20100401-------------------------//
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010028sp1)){
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010028sp1();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010028sp1();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010029)){
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010029();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010029();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         //--- 合并中保版本，更新中保历史的用户权限 、角色权限    add by jiangshichao 2010.08.20 合并太平版本调整-----------
		if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010030)) {
			if (dbl.getDBType() == YssCons.DB_ORA) { // 判断是ora数据库的更新
				dbUpdate = new Ora1010030();
				dbUpdate.setYssPub(this.pub);
				dbUpdate.doUpdate(hmInfo);
			}
		}
		//--- 合并中保版本，更新中保历史的用户权限 、角色权限    add by jiangshichao 2010.08.20 end 合并太平版本调整-----------
		if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010031)){
			if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
				dbUpdate = new Ora1010031();
				dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                dbUpdate = new Ora1010031();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
         //------ add by wangzuochun 2010.08.02 MS01460    进入品种信息——指数期权信息设置，新建一条指数期权信息时，点击保存时报错    QDV4赢时胜(测试)2010年07月20日01_B 
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010031sp1)){
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010031sp1();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010031sp1();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         //------MS01460----------//
         
         //------ add by wangzuochun  2010.08.11  MS01462    进入库存信息配置，新建时,选择库存类型下拉框,有两个相同的运营收支款  QDV4赢时胜(测试)2010年7月20日1_B  
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010032)){
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010032();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010032();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         //------MS01462----------//
         
         //新增版本号，合并太平版本增加，by leeyu 20100902 
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010033)){
             if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010033();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         //add by fangjiang 2010.10.13  MS01847 权限设置中，综合业务菜单有两个
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010033sp1)){
        	 
        	 if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010033sp1();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010033sp1();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         
       //add by yanghaiming 2010.10.19
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010034)){
        	 
        	 if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010034();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010034();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         
       //add by yanghaiming 2010.10.19
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010035)){
        	 
        	 if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010035();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010035();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         
         //add by panjunfang 2010.12.06
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010036)){
        	 
        	 if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010036();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010036();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         
         //add by lidaolong 20100114 
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010037)){
        	 
        	 if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010037();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010037();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         
         // add by wangzuochun 2011.02.09 BUG #1059 证券应收应付数据中存在应收股息和应收股息汇兑损益的历史数据
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010037sp1)){
        	 
        	 if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010037sp1();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010037sp1();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         
         //add by lidaolong 20100124
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010038)){
        	 
        	 if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010038();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010038();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         
         //add by lidaolong 20110314
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010039)){
        	 
        	 if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010039();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010039();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         
         //add by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
         if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010040)){
        	 
        	 if (dbl.getDBType() == YssCons.DB_ORA) { //判断是ora数据库的更新
                 dbUpdate = new Ora1010040();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             } else if (dbl.getDBType() == YssCons.DB_DB2) {
                 dbUpdate = new Ora1010040();
                 dbUpdate.setYssPub(this.pub);
                 dbUpdate.doUpdate(hmInfo);
             }
         }
         
        /** shashijie 2011.05.26 BUG1914在划款类型维护界面上缺少划款指令名称字段*/
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010041sp1)){
        	if (dbl.getDBType() == YssCons.DB_ORA) {//判断是ora数据库的更新
        		dbUpdate = new Ora1010041sp1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                dbUpdate = new Ora1010041sp1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
         
        /**
         * add by songjie 2011.06.25 
         * BUG 2116 光大证券2011年6月20日04_B
         * 给用户赋权限时，不显示整行都是灰色的菜单
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010042)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010042();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
     
        /**
         * add by songjie 2011.07.20
         * 需求 1282 QDV4博时基金2011年6月29日01_A
         * 在外汇交易表（Tb_XXX_Data_Ratetrade）中添加FRateTradeType字段
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010042sp3)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010042sp3();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by songjie 2011.07.18
         * BUG 2274 QDV4建信2011年7月14日01_B
         * 在用户设置表中添加FUserID字段
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010043)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010043();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by panjunfang 2011.08.16
         * STORY #1428 QDV411建行2011年07月26日01_A
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010043sp2)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010043sp2();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by guolongchao 2011.08.24
         * 需求 1207 QDV4易方达2011年6月9日01_A_需求     删除通用参数：业务按成交顺序处理
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010044)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010044();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
    	/**
    	 * add by guolongchao 2011.09.26
    	 * BUG 2771 QDV4建行2011年09月15日01_B.xls 
    	 * 当并发操作时，会提示插入日志表出错 *
    	 */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010045sp1)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010045sp1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by fangjiang 2011.09.14
         * story 1342
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010044sp4)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010044sp4();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by songjie 2012.09.29
         * BUG 5867 QDV4赢时胜(上海)2012年09月26日04_B
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010047)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010047();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by zhouwei 2011.12.23
         * story 1936 QDV4赢时胜(上海开发部)2011年11月28日01_A
         * 更新业务处理--分红转投与通用参数移动加权计算当天交易的词汇信息
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010048)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010048();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by yangshaokai
         * 2011.12.31
         * STORY 2007 
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010050)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010050();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        

        /**
         * add by liubo
         * 2012.03.01
         * STORY 2248
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010050sp1)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010050sp1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by yeshenghong 
         * 2012.03.16
         * STORY 2300
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010051)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010051();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by yeshenghong 
         * 2012.04.12
         * STORY 2300
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010051sp1)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010051sp1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by yeshenghong 
         * 2012.04.12
         * STORY 2300
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010052)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010052();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by yeshenghong 
         * 2012.05.04
         * BUG 4395
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010052sp1)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010052sp1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by yeshenghong 2012.01.16
         * story 1927 QDV4赢时胜(上海开发部)2011年11月25日
         *插入核算调拨关系
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010049sp1)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010049sp1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        /**
         * add by songjie 2012.01.16
         * BUG 3619 QDV4赢时胜(上海)2012年01月11日01_B
         * 删除多余的自定义接口文件类型
         */
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010049sp2)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010049sp2();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010049sp3)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010049sp3();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        //add by zhouwei 升级操作 20120509
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010053)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010053();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        
        //add by huangqirong 2012-06-01 bug #4679、bug#4667 
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010053sp1)){
        	if ((dbl.getDBType() == YssCons.DB_ORA) || (dbl.getDBType() == YssCons.DB_DB2)) {
        	    dbUpdate = new Ora1010053sp1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.adjustTableData(this.pub.getAssetGroupCode());
            }
        }
        
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010053sp2)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010053sp2();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        //add by zhangjun 2012.06.13 story#2459 
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010053sp3)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010053sp3();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        } 
        //add by songjie 2012.07.17 story #2727 QDV4赢时胜(北京)2012年6月13日01_A
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010055)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010055();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        } 
        //add by songjie 2012.07.17 story #2727 QDV4赢时胜(北京)2012年6月13日01_A
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010056)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010056();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        } 
        
		     //add by yeshenghong 2012.11.13 bug6240 
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010056sp3)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010056sp3();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        } 
		//add by huangqirong 2013-01-25 story #3488
		if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010056sp7)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010056sp7();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
		}
        //----end---
        //---add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010056sp4em2)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010056sp4em2();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        } 
        //---add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
		
	 	//add by yeshenghong 2013-03-27 story #3736
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010056sp8em1)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010056sp8em1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
		
        //add by songjie 2012.09.10 story #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010057)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010057();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        } 
        /**shashijie 2012-11-22 BUG 6312 红利转投界面的价格做出了整数位不能大于2位的判断*/
        //58跟新至59
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010059)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010059();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
		/**end shashijie 2012-11-22 BUG */
        
        //---add by songjie 2013.02.18 BUG 7102 QDV4招商银行2013年02月17日01_B start---//
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010059sp1)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010059sp1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        //---add by songjie 2013.02.18 BUG 7102 QDV4招商银行2013年02月17日01_B end---//
        
        /**add---shashijie 2013-2-27 STORY 3366 估值系统导出所有组合的彭博REQ文件的需求*/
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010059sp2)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010059sp2();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
		/**end---shashijie 2013-2-27 STORY 3366*/
        
        /**add---shashijie 2013-3-8 STORY 2869 更新60版本*/
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010060)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010060();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
		/**end---shashijie 2013-3-8 STORY 2869*/

        /**add---liubo 2013-5-22 STORY 3975 更新60sp1版本*/
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010060sp1)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010060sp1();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
		/**end---liubo 2013-5-22 STORY 3975*/
        
        //--- add by songjie 2013.06.17 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 start---//
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010060sp2)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010060sp2();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
        //--- add by songjie 2013.06.17 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 end---//

		/**Start 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001
		 * 更新“密码复杂度设置”表*/
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010060sp3)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010060sp3();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
		/**End 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001*/
        
        /**Start 20130702 added by yeshenghong.Story #4151
		 * 更新“分级组合设置”表*/
        if (sCurrtVerNum.equalsIgnoreCase(YssCons.YSS_VERSION_1010060sp4)){
        	if ((dbl.getDBType() == YssCons.DB_ORA)) {
        	    dbUpdate = new Ora1010060sp4();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.doUpdate(hmInfo);
            }
        }
		/**End 20130702 added by yeshenghong.Story #4151*/
        
    } catch (Exception e) {
        errBuf.append(e.getMessage()); //MS00010 QDV4赢时胜（上海）2009年02月01日10_A edit by songjie 2009-05-12 由e改为e.getMessage()
        sResultTag = YssCons.YSS_DBUPDATE_FAIL;
    }
        
        return sResultTag;
    }
    
}
