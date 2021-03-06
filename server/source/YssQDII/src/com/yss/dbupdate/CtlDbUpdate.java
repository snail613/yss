package com.yss.dbupdate;

import com.yss.dsub.*;
import com.yss.main.syssetting.AssetGroupBean;
import com.yss.util.*;

import java.io.IOException;
import java.sql.*;

import com.yss.dbupdate.autoupdatetables.updatemain.CtlAutoUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import com.yss.dbupdate.autoupdatetables.updatemain.VersionControl;

public class CtlDbUpdate
    extends BaseBean {
    BaseDbUpdate dbUpdate; //直接执行更新的类
    String strVersionNum = ""; //保存数据库中老的组合群相关最大版本号
    String strCommVerNum = ""; //保存通用数据表的最大版本号
    String strDesc = ""; //保存版本更新的描述
    //------------by xuqiji 2009 0409  MS00352    新建组合群时能够自动创建对应的一套表-------------------------
    CtlAutoUpdate autoUpdate; //自动更新的操作控制类，也是入口类
    HashMap messageMap; //声明
    //--------------by xuqiji 2009 0409  MS00352    新建组合群时能够自动创建对应的一套表--------------------------------
    public CtlDbUpdate() throws YssException {

    }

    public String getVersionNum() {
        return this.strVersionNum;
    }

    public void setVersionNum(String versionNum) {
        this.strVersionNum = versionNum;
    }

    public String getCommVerNum() {
        return this.strCommVerNum;
    }

    public void setCommVerNum(String versionNum) {
        this.strCommVerNum = versionNum;
    }

    public void setDesc(String desc) {
        this.strDesc = desc;
    }

    /**
     * 更新类的入口
     * @param sPre String: 组合群号
     * @throws YssException
     * @throws IOException 
     */
    public void updateEntry(String sPre) throws YssException, IOException {
        this.IsNullGroupCode(sPre); //判断输入的组合群号码是否存在
        this.getOldVersionNum(sPre); //获取老的版本号
      //modify by huangqirong 2011-08-30 story #1267
        synchronized (YssCons.Yss_DBUPDATE_COMMON) {
        	this.updateCommon(true); //公共模块更新
		}
        //---end---
        runStatus.appendRunDesc("updatedb", "updating" + "\r\f" + 2 + "\r\f" + sPre + "\r\f" + "common");//add  by yeshenghong 201203224 story 2164
        //modify by huangqirong 2011-08-30 story #1267
        synchronized (YssCons.Yss_DBUPDATE_Group) {
        	this.updateGroupOnly(sPre, true); //组合群模块更新        	
		}
        //---end---
        runStatus.appendRunDesc("updatedb", "updating" + "\r\f" + 2 + "\r\f" +  sPre + "\r\f" + "group");
        
        //-------------------by xuqiji 2009 0409  MS00352    新建组合群时能够自动创建对应的一套表-----------------
        messageMap = new HashMap(); //实例化
        autoUpdate = new CtlAutoUpdate(); //实例化入口类
        autoUpdate.setYssPub(pub); //把实例化的入口类塞给YssPub
        autoUpdate.setYssRunStatus(runStatus);
        //modify by huangqirong 2011-08-30 story #1267
        synchronized (YssCons.Yss_DBUPDATE_FILE) {
        	autoUpdate.executeUpdate(messageMap, sPre, pub.getUserCode()); //执行update
        }
        //---end---
        //---------------------by xuqiji 2009 0409  MS00352    新建组合群时能够自动创建对应的一套表---------------
    }
    
  //add by huangqirong 2011-09-08 story #1286
    public void copySource(String assetGroupCode) throws Exception{
    	
    	java.util.List listSourceTB=new java.util.ArrayList();
    	java.util.List listTagerTB=new java.util.ArrayList();
    	
    	String copySource="";
    	String copyState="";
    	
    	AssetGroupBean group=new AssetGroupBean();
    	group.setYssPub(pub);
    	String copySources=group.getCopySource(assetGroupCode);    	
    	String columnInfo = "";
    	if(copySources.equals(""))
    		return;
    	
    	copySource=copySources.split("\t")[0].trim();
    	copyState=copySources.split("\t")[1].trim();
    	
    	if(!copyState.equals("0")) //已复制或没有复制源 则返回
    		return;
    	
        ResultSet rs = null;
        try {
            rs=dbl.openResultSet("select table_name from user_tables where table_name like 'TB\\_"+copySource+"%\\_PARA\\_%' escape '\\'");
            while(rs.next()){
            	//add by maxin BUG #87275 QDV4赢时胜（上海开发）2014年1月9日01_B（复制组合群不应该复制运营费用数据） 
            	if(!rs.getString("table_name").trim().equalsIgnoreCase("TB_"+copySource+"_PARA_InvestPay")){
            		listSourceTB.add(rs.getString("table_name").trim());
                	listTagerTB.add(rs.getString("table_name").trim().replaceFirst("TB\\_"+copySource, "TB\\_"+assetGroupCode));
            	}
            	
            }            
            dbl.closeResultSetFinal(rs);
            rs=null;            
            for (int i = 0; i < listSourceTB.size(); i++) {
            	if(dbl.yssTableExist((String)listSourceTB.get(i))&&dbl.yssTableExist((String)listTagerTB.get(i))){
            		/**shashijie 2011-10-21 STORY 1698 */
            		dbl.executeSql("truncate table " + listTagerTB.get(i));
            		/**end*/
            		//modified by yeshenghong  BUG5075 20120829
            		columnInfo = this.getColumnInfo((String)listTagerTB.get(i));
            		//System.out.println("insert into "+listTagerTB.get(i)+"  select * from "+listSourceTB.get(i));
            		dbl.executeSql("insert into "+listTagerTB.get(i) + "  select " + columnInfo + " from "+listSourceTB.get(i));
            		
            	}
			}
            
            /**add by liuxiaojun stroy 4156 20130821    复制相关的组合群数据*/
            String sql = ""; 
        	sql = "insert into  Tb_" +assetGroupCode+ "_TA_SellNet  select * from Tb_" +copySource+"_TA_SellNet";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_vch_assistant  select * from Tb_" +copySource+"_vch_assistant";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_vch_datasource  select * from Tb_" +copySource+"_vch_datasource";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_vch_dstabfield  select * from Tb_" +copySource+"_vch_dstabfield";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_vch_entity  select * from Tb_" +copySource+"_vch_entity";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_vch_entityma  select * from Tb_" +copySource+"_vch_entityma";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_vch_entityresume  select * from Tb_" +copySource+"_vch_entityresume";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_vch_entitysubject  select * from Tb_" +copySource+"_vch_entitysubject";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_vch_attr  select * from Tb_" +copySource+"_vch_attr";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Vch_dict  select * from Tb_" +copySource+"_Vch_dict";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_vch_entitycond  select * from Tb_" +copySource+"_vch_entitycond";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Vch_Project  select * from Tb_" +copySource+"_Vch_Project";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_PFOper_SchProject  select * from Tb_" +copySource+"_PFOper_SchProject";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_PFOper_PUBPARA  select * from Tb_" +copySource+"_PFOper_PUBPARA";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_DAO_RateSpeciesType  select * from Tb_" +copySource+"_DAO_RateSpeciesType";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Dao_CusConfig  select * from Tb_" +copySource+"_Dao_CusConfig";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Dao_FileName  select * from Tb_" +copySource+"_Dao_FileName";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Dao_FileInfo  select * from Tb_" +copySource+"_Dao_FileInfo";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Dao_FileContent  select * from Tb_" +copySource+"_Dao_FileContent";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Dao_Pretreat  select * from Tb_" +copySource+"_Dao_Pretreat";
        	dbl.executeSql(sql);
        	
        	sql = "drop table Tb_" +assetGroupCode+ "_Dao_PretreatField";
        	dbl.executeSql(sql);
        	sql = "create table  Tb_" +assetGroupCode+ "_Dao_PretreatField  as select * from Tb_" +copySource+"_Dao_PretreatField";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Dao_TgtTabCond  select * from Tb_" +copySource+"_Dao_TgtTabCond";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Dao_Dict  select * from Tb_" +copySource+"_Dao_Dict";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Rep_Custom  select * from Tb_" +copySource+"_Rep_Custom";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Rep_Format  select * from Tb_" +copySource+"_Rep_Format";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Rep_DataSource  select * from Tb_" +copySource+"_Rep_DataSource";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Rep_DsField  select * from Tb_" +copySource+"_Rep_DsField";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Rep_Cell  select * from Tb_" +copySource+"_Rep_Cell";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Rep_Pretreat  select * from Tb_" +copySource+"_Rep_Pretreat";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Rep_TgtTabCond  select * from Tb_" +copySource+"_Rep_TgtTabCond";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Rep_FILENAME  select * from Tb_" +copySource+"_Rep_FILENAME";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Rep_PretreatField  select * from Tb_" +copySource+"_Rep_PretreatField";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_Rep_Colorfilter  select * from Tb_" +copySource+"_Rep_Colorfilter";
        	dbl.executeSql(sql);
        	
        	sql = "insert into  Tb_" +assetGroupCode+ "_vch_vchtpl  select * from Tb_" +copySource+"_vch_vchtpl";
        	dbl.executeSql(sql);
            /**end stroy 4156*/
        	
            group.updateSourceState(assetGroupCode);
            
            dbl.executeSql(" update Tb_"+assetGroupCode+"_Para_Portfolio set FassetGroupcode="+dbl.sqlString(assetGroupCode));//复制的组合中的关联组合群都得更新
         // start dongqingsong  2013-09-16 新禅道 BUG #79588  复制组合群是不要求复制组合信息    
            String tableName = "Tb_"+assetGroupCode+"_Para_Portfolio";
            if(dbl.yssTableExist(tableName)){
            	dbl.executeSql("truncate table "+tableName);
            }
         // end dongqingsong  2013-09-16 新禅道 BUG #79588  复制组合群是不要求复制组合信息     
            /**add---huhuichao 2013-9-27  BUG #80050 组合复制之后，凭证方案设置没有复制进来*/
			String tabName1 = "Tb_" + assetGroupCode + "_Vch_Project";
			if (dbl.yssTableExist(tabName1)) {
				dbl.executeSql("truncate table " + tabName1);
				String sQL = "insert into  Tb_" + assetGroupCode
						+ "_Vch_Project  select * from Tb_" + copySource
						+ "_Vch_Project";
				dbl.executeSql(sQL);
			} else {
				dbl.executeSql("create table Tb_" + assetGroupCode
						+ "_Vch_Project as select * from Tb_" + copySource
						+ "_Vch_Project");
			}
			String tabName = "Tb_" + assetGroupCode + "_Vch_BuildLink";
			if (dbl.yssTableExist(tabName)) {
				dbl.executeSql("truncate table " + tabName);
				String sQL = "insert into  Tb_" + assetGroupCode
						+ "_Vch_BuildLink  select * from Tb_" + copySource
						+ "_Vch_BuildLink";
				dbl.executeSql(sQL);
			} else {
				dbl.executeSql("create table Tb_" + assetGroupCode
						+ "_Vch_BuildLink as select * from Tb_" + copySource
						+ "_Vch_BuildLink");
			}
			String tabName2 = "Tb_" + assetGroupCode + "_Vch_Attr";
			if (dbl.yssTableExist(tabName2)) {
				dbl.executeSql("truncate table " + tabName2);
				String sQL = "insert into  Tb_" + assetGroupCode
						+ "_Vch_Attr  select * from Tb_" + copySource
						+ "_Vch_Attr";
				dbl.executeSql(sQL);
			} else {
				dbl.executeSql("create table Tb_" + assetGroupCode
						+ "_Vch_Attr as select * from Tb_" + copySource
						+ "_Vch_Attr");
			}
			/**end---huhuichao 2013-9-27 BUG  80050*/
        } catch (SQLException se) {
            throw new YssException("组合群复制数据源出错！", se);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //get table column information  add by yeshenghong 20120829
    private String getColumnInfo(String tableName) throws Exception
    {
    	ResultSet rs = null;
    	String colInfo = "";
    	ResultSetMetaData rsMeta = null;
    	
    	try {
			rs = dbl.openResultSet(" select * from " + tableName);
			rsMeta = rs.getMetaData();
			for(int i = 1;i<=rsMeta.getColumnCount();i++)
			{
				colInfo += rsMeta.getColumnName(i);
				colInfo += ",";
			}
		} catch (SQLException se) {
			// TODO Auto-generated catch block
			 throw new YssException("复制时获取目标表表结构出错！", se);
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    	return colInfo.substring(0, colInfo.length()-1);
    }

    //判断输入的组合群号码是否存在
    public void IsNullGroupCode(String sPre) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "SELECT * FROM TB_SYS_ASSETGROUP WHERE fassetgroupcode = " +
                dbl.sqlString(sPre);
            rs = dbl.openResultSet(strSql);
            if (!rs.next()) {
                throw new Exception();
            }
        } catch (Exception e) {
        	throw new YssException("组合群代码不存在！", e); //modify by wangzuochun 2010.04.19  MS00977    登陆系统时，当“组合群代码”和“用户代码”输入错误时报错    QDV4赢时胜（测试）2010年04月14日03_B  
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

//获取数据库中保存的最大版本号
    public void getOldVersionNum(String sPre) throws YssException {
        ResultSet rsGroup = null;
        ResultSet rsCommon = null;
        String bufSql = "";
        try {
//            bufSql.append("SELECT FASSETGROUPCODE, FVERNUM");
//            bufSql.append("  FROM TB_FUN_VERSION");
//            bufSql.append(" WHERE FVERNUM = (SELECT MIN(FVERNUM) FROM TB_FUN_VERSION WHERE FASSETGROUPCODE = " +
//                          dbl.sqlString(sPre) + ")");
//            bufSql.append("   AND FASSETGROUPCODE = " + dbl.sqlString(sPre));
        	bufSql = " select min(fvernum)  as fvernum from (select distinct max(t.fvernum) as fvernum,t.fassetgroupcode from tb_fun_version t " + 
        			" join tb_sys_assetgroup a on t.fassetgroupcode = a.fassetgroupcode group by t.fassetgroupcode) " ;
            rsGroup = dbl.openResultSet(bufSql);
            if (rsGroup.next()) {
                this.strVersionNum = rsGroup.getString("FVERNUM"); //组合群最大版本号
            } else {
                this.strVersionNum = YssCons.YSS_VERSION_MIN; //如果结果集内没数据则默认为最小版本号
            }

            bufSql = " SELECT FASSETGROUPCODE, FVERNUM FROM TB_FUN_VERSION WHERE FVERNUM = (SELECT MAX(FVERNUM) FROM TB_FUN_VERSION " +
            		" WHERE FASSETGROUPCODE = 'Common') AND FASSETGROUPCODE = 'Common'";
            rsCommon = dbl.openResultSet(bufSql.toString());
            if (rsCommon.next()) {
                this.strCommVerNum = rsCommon.getString("FVERNUM"); //公共模块最大版本号
            } else {
                this.strCommVerNum = YssCons.YSS_VERSION_MIN; //如果结果集内没数据则默认为最小版本号
            }

        } catch (Exception e) {
            throw new YssException("获取版本号出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsGroup);
            dbl.closeResultSetFinal(rsCommon);
        }
    }
    
    public int getVersionCount() throws SQLException, YssException
    {
    	ResultSet rs = null;
        String strSql = "";
    	int versionCount = 0;
    	int totalVersion = 0;
    	try {
	    	strSql = " select distinct max(t.fvernum) as fvernum,t.fassetgroupcode from tb_fun_version t " +
	    			" join tb_sys_assetgroup a on t.fassetgroupcode = a.fassetgroupcode group by t.fassetgroupcode  order by t.fassetgroupcode ";
	    	rs = dbl.openResultSet(strSql);
	    	while(rs.next())
	    	{
	    		String version = rs.getString("fvernum");
	    		for(int i=0;i<YssCons.YSS_AUTOUPDATE_VERSIONS.length;i++)
            	{
            		if(YssCons.YSS_AUTOUPDATE_VERSIONS[i][0].equals(version))
            		{
            			versionCount = YssCons.YSS_AUTOUPDATE_VERSIONS.length -1 - i;
            			break;
            		}
            	}
	    		if(versionCount!=0)
	    		{
	    			totalVersion += versionCount * 5 + 5 ;
	    			versionCount = 0;
	    		}
	    	}
	    	return totalVersion;
    	 } catch (Exception e) {
             throw new YssException("获取版本号出错！", e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	
    }
    

    //更新数据库中的版本信息
    public void setNewVersionNum(String sPre, String strVerNum) throws
        YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;

        //xuqiji 20090518:QDV4赢时胜（上海）2009年4月7日01_A  MS00352    新建组合群时能够自动创建对应的一套表
        //避免不同组合群版本号不同，而版本表结构已变，造成错误的出现
        //判断是否为第一次更新，如果为最小版本号则提示用户重新创建组合群
        if (strVerNum.equals(YssCons.YSS_VERSION_MIN)) {
            throw new YssException("抱歉，组合群【" + sPre + "】不存在任何表，请删除后重新创建！");
        }
        dbUpdate = new BaseDbUpdate();
        dbUpdate.setYssPub(pub);
        HashMap hmInfo = new HashMap();
        StringBuffer bufTemp = new StringBuffer();
        VersionControl version = new VersionControl();
        version.setYssPub(pub);
        try {
            hmInfo.put("sqlinfo", bufTemp);
            hmInfo.put("errinfo", bufTemp);
            hmInfo.put("updatetables", bufTemp);
            //根据数据库不同采取不同的处理方式，现在只判断oracle和db2
            if (dbl.getDBType() == YssCons.DB_ORA) {
                //如果版本表被更新了，则使用更新的插入版本表数据的方式进行版本更新
                if (!dbUpdate.existsTabColumn_Ora(pub.yssGetTableName(
                    "TB_FUN_VERSION"), "FUSERCODE,FUPDATETABLES,FERRORINFO,FSQLSTR")) {
                	//---#580 建信上线需提供部分方案支持 add by jiangshichao 20110324
                    version.updateVersionInfo((sPre.trim().length() == 0 ? "Common":sPre), pub.getUserCode(), strVerNum, " ",
                                              hmInfo);
                    return;
                }
            } else {
                if (!dbUpdate.existsTabColumn_DB2(pub.yssGetTableName(
                    "TB_FUN_VERSION"), "FUSERCODE,FUPDATETABLES,FERRORINFO,FSQLSTR")) {
                	//---#580 建信上线需提供部分方案支持 add by jiangshichao 20110324
                    version.updateVersionInfo((sPre.trim().length() == 0 ? "Common":sPre), pub.getUserCode(), strVerNum, " ",
                                              hmInfo);
                    return;
                }
            }
            //-------------------------end MS00352---------------------------------------//
            //如果版本表没有更新，仍采用原始的方法插入版本表值
            String strSql = "INSERT INTO TB_FUN_VERSION(FASSETGROUPCODE, FVERNUM, FISSUEDATE, FDESC, FCreateDate, FCreateTime) " +
                "VALUES(" +
                (sPre.trim().length() == 0 ? dbl.sqlString("Common") :
                 dbl.sqlString(sPre)) +
                "," + //如果没有传入组合群编号则认为是更新通用模版本
                dbl.sqlString(strVerNum) + "," +
                dbl.sqlDate(YssFun.toSqlDate(new java.util.Date())) +
                "," +
                dbl.sqlString(this.strDesc) + "," +
                dbl.sqlDate(YssFun.toSqlDate(new java.util.Date())) +
                "," +
                "'" + YssFun.formatDatetime(new java.util.Date()) +
                "')";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------------------------
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新版本信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 将版本信息标志位 FFINISH 设置为 Success
     * @param sPre String: 组合群代码，如果更新公共表则输入“Common”
     * @param strVerNum String: 版本号
     * @throws YssException
     */
    public void updateVersionSuccess(String sPre, String strVerNum) throws
        YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "UPDATE TB_FUN_VERSION SET FFINISH = 'Success' " +
                "WHERE FASSETGROUPCODE = " + dbl.sqlString(sPre) +
                "AND FVERNUM = " + dbl.sqlString(strVerNum);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("结束版本更新出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 更新数据表结构变更
     * @param sPre String: 组合群编号
     * @param bIsAutoUpdate boolean: true 为自动更新， false 为手动更新
     * @throws YssException
     */
    public void updateGroupOnly(String sPre, boolean bIsAutoUpdate) throws
        YssException {
        if (dbl.getDBType() == YssCons.DB_ORA) { //进行数据库类型的判断
            if (bIsAutoUpdate) {
                if (this.strVersionNum.compareTo(YssCons.YSS_VERSION_NUMBER) ==
                    0) {
                    return;
                }
                //---------edit by jc
//            ImportTableData itd = new ImportTableData();
//            itd.setYssPub(this.pub);
//            itd.createTable();
//            System.out.println("添加4张新表");
//            itd.importDatas("");
//            System.out.println("填充数据到4张新表");
                //-----------------jc
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010000) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010000) == 0)) { //版本判断  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010000); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010000();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre); //更新主键名
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustFieldPrecision(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010000); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010000; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010001) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010000) == 0)) { //版本号1.0.1.0001  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010001); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010001();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre); //更新主键名
                dbUpdate.createTable(sPre);
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustFieldName(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010001); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010001; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010001) == 0)) { //版本号1.0.1.0002  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010002); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010002();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.createTable(sPre);
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustTableData(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010002); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010002; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002sp1) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002) == 0)) { //版本号1.0.1.0002  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010002sp1); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010002sp1();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010002sp1); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010002sp1; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            //-------------2008-5-7  单亮--------------------------------begin
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002sp2) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002sp1) ==
                 0)) { //版本号1.0.1.0002  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010002sp2); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010002sp2();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010002sp2); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010002sp2; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            //-------------------------end
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002sp2) ==
                 0)) { //版本号1.0.1.0003  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010003();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.adjustTableKey(sPre); //添加调整主键的功能。 sj add 20080528
                dbUpdate.createTable(sPre);
                //dbUpdate.createTable();
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustFieldPrecision(sPre);
                dbUpdate.adjustTableData(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp1) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003) == 0)) { //版本号1.0.1.0003sp1  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp1); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010003sp1();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.adjustFieldName(sPre);
                dbUpdate.adjustFieldPrecision(sPre); //调整字段的长度 2008-6-16 单亮
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp1); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp1; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp2) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp1) ==
                 0)) { //版本号1.0.1.0003sp2  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp2); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010003sp2();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.addTableField(sPre); //添加字段 2008-07-14 蒋春
                dbUpdate.adjustFieldPrecision(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp2); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp2; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            //BugNo:0000304 edit by jc
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp3) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp2) ==
                 0)) { //版本号1.0.1.0003sp3  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp3); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010003sp3();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustTableData(sPre); //调整数据
                dbUpdate.adjustFieldPrecision(sPre); //调整字段精度
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp3); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp3; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //---------------------jc
            //BugNo:0000363 edit by jc
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp4) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp3) ==
                 0)) { //版本号1.0.1.0003sp4  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp4); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010003sp4();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.addTableField(sPre); //添加字段
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp4); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp4; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //---------------------jc
            //BugNo:0000363 edit by jc
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp5) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp4) ==
                 0)) { //版本号1.0.1.0003sp5  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp5); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010003sp5();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.adjustFieldPrecision(sPre); //调整表字段
                dbUpdate.adjustTableKey(sPre); //调整表主键    //BugNo:0000425 edit by jc
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp5); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp5; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //---------------------jc

            //======add by MaoQiwen  20080820  bug:0000401======//
            //=======更改表TB_VCH_DICT中FDICTNAME字段的长度=======//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp6) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp5) ==
                 0)) { //版本号1.0.1.0003sp6  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp6); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010003sp6();
                dbUpdate.setYssPub(this.pub);
                //==============开始一次更新==============//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.adjustFieldPrecision(sPre); //调整字段的长度
                dbUpdate.createTable(sPre); //创建表      //edit by jc
                dbUpdate.addTableField(sPre); //添加表字段   //BugNo:0000429 edit by jc
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp6); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp6; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果手动更新，一次只更新一个版本
                    return;
                }
            }
            //BugNo:0000363 edit by jc
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp7) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp6) ==
                 0)) { //版本号1.0.1.0003sp7  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp7); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010003sp7();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.addTableField(sPre); //添加表字段  //BugNo:0000447 edit by jc
                dbUpdate.adjustFieldPrecision(sPre); //调整表字段
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp7); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp7; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //---------------------jc
            // add by leeyu 0909
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp8) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp7) ==
                 0)) {
                if (bIsAutoUpdate) {
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp8);
                }
                dbUpdate = new Ora1010003sp8();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.createTable(sPre);
                dbUpdate.addTableField(sPre); //BugNo:0000462 edit by jc
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp8);
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp8;
                if (!bIsAutoUpdate) {
                    return;
                }
            }
            // add by leeyu


            // edit by 张旭 2008-9-26
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp9) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp8) ==
                 0)) { //版本号1.0.1.0003sp9  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp9); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010003sp9();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.addTableField(sPre); //添加表字段
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp9); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp9; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //=========== 添加新版本，20081020 bug 0000486 by leeyu
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010004) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp9) ==
                 0)) { //版本号1.0.1.0004  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010004); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010004();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.updatePKName_Ora(sPre);
                dbUpdate.addTableField(sPre); //添加表字段
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010004); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010004; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //******************* 王晓光 20081024 *******************//
            //******************* 更新数据库中的表，给表 TB_001_PARA_FIXINTEREST 添加字段 FAMORTIZATION；修改 Tb_Base_CalcInsMetic中字段 FFormula 的长度  ****************//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010005) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010004) == 0)) { ////版本号1.0.1.0005  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010005); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010005();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.addTableField(sPre); //给表 TB_001_PARA_FIXINTEREST 添加字段 FAMORTIZATION,FFACTRATE
                dbUpdate.adjustFieldPrecision(sPre); //修改 Tb_Base_CalcInsMetic中字段 FFormula 的长度
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010005); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010005; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //******************* 王晓光 20081121 *******************//
            //******************* 更新数据库中的表，修改 Tb_XXX_Data_BonusShare、TB_XXX_DATA_RIGHTSISSUE、Tb_XXX_Data_Dividend 中的字段 FRATIO 的长度  ****************//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010006) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010005) == 0)) { ////版本号1.0.1.0006  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010006); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010006();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustFieldPrecision(sPre); //修改 Tb_XXX_Data_BonusShare、TB_XXX_DATA_RIGHTSISSUE、Tb_XXX_Data_Dividend 中的字段 FRATIO 的长度
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010006); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010006; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //==========================================================
            /**
             * date   : 2008-11-25
             * author : sunkey
             * bugid  : MS00035
             * desc   : 更新1.0.1.0007版本Oracle数据库，为TB_BASE_COUNTRY添加协议(FAgreement)字段
             */
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010007) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010006) == 0)) { ////版本号1.0.1.0007  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010007); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010007();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustFieldPrecision(sPre); //修改 Tb_XXX_Data_BonusShare、TB_XXX_DATA_RIGHTSISSUE、Tb_XXX_Data_Dividend 中的字段 FRATIO 的长度
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010007); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010007; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }

            //==========================================================
            /**
             * date   : 2008-12-08
             * author : 王晓光
             * bugid  : MS00060
             * desc   : 将证券应收应付中分红的汇兑损益数据逻辑删除。在回购品种信息设置（Tb_XXX_Para_Purchase）中新增一字段FCircuMarket——“流通市场”。并将其设置为主键
             */
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010008) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010007) == 0)) { ////版本号1.0.1.0008  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010008); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010008();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustTableData(sPre); //将证券应收应付中分红的汇兑损益数据逻辑删除。 MS00050
                dbUpdate.adjustFieldPrecision(sPre); //在回购品种信息设置（Tb_XXX_Para_Purchase）中新增一字段FCircuMarket——“流通市场”。并将其设置为主键 MS00060
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010008); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010008; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }

            //===========================================================================================
            //sj add 20081226
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010009) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010008) == 0)) { ////版本号1.0.1.0009  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010009); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010009();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustFieldPrecision(sPre); //在回购品种信息设置（Tb_XXX_Para_Purchase）中新增一字段FINBEGINTYPE——“计息起始日类型”。
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010009); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010009; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //leeyu add 20081229
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010010) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010009) == 0)) { ////版本号1.0.1.0010  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010010); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010010();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustFieldPrecision(); //修改词汇数据表的备注信息长度。
                dbUpdate.adjustFieldPrecision(sPre); //修改交易数据,订单管理交易价格,虚拟价格,确认价格字段长度.//修改了债券信息中的发行价格小数位数
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010010); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010010; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }

            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010011) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010010) == 0)) { ////版本号1.0.1.0011  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010011); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010011();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustFieldPrecision(sPre); //
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010011); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010011; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //2009年2月26日版本
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010012) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010011) == 0)) { ////版本号1.0.1.0012  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010012); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010012();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010012); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010012; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }

            //-----3.16最新的更新版本-------------
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010013) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010012) == 0)) { ////版本号1.0.1.0012  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010013); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010013();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.createTable();
                dbUpdate.createTable(sPre);
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustTableData(sPre); //调整数据
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010013); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010013; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //---------------------------------------
//--MS00273 QDV4中金2009年02月27日01_A add by songjie 2009.03.23-----//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010014sp4) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010013) == 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010014sp4);
                }
                dbUpdate = new Ora1010014sp4();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010014sp4); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010014sp4; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //--MS00273 QDV4中金2009年02月27日01_A add by songjie 2009.03.23-----//'

            //--MS00339 QDV4建行2009年1月16日01_B add by songjie 2009.03.27----//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010014sp6) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010014sp4) ==
                 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010014sp6);
                }
                dbUpdate = new Ora1010014sp6();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010014sp6); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010014sp6; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //--MS00339 QDV4建行2009年1月16日01_B add by songjie 2009.03.27----//

            //--MS00007 QDV4.1赢时胜上海2009年2月1日06_A add by songjie 2009.03.25----//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010015) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010014sp6) ==
                 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010015);
                }
                dbUpdate = new Ora1010015();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.addTableField(sPre);
                dbUpdate.createTable();
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010015); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010015; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }

            //xuqiji 20090611 QDV4建行2009年6月9日01_B MS00490 财务估值核对脚本下拉条不起作用
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010015sp11) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010015) ==
                 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010015sp11);
                }
                dbUpdate = new Ora1010015sp11();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.adjustFieldPrecision(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010015sp11); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010015sp11; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //------------------------------------------end----------------------------------------------//

            //2009-04-07 蒋锦 添加 MS00352
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010016) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010015) ==
                 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010016);
                }
                dbUpdate = new Ora1010016();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.addTableField();
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010016); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010016; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //--MS00007 QDV4.1赢时胜上海2009年2月1日06_A add by songjie 2009.03.25----//
        } else if (dbl.getDBType() == YssCons.DB_DB2) {
            if (bIsAutoUpdate) {
                if (this.strVersionNum.compareTo(YssCons.YSS_VERSION_NUMBER) ==
                    0) {
                    return;
                }
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010000) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010000) == 0)) { //版本判断 版本1.0.1.0000 自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010000); //更新组合群模块版本号
                }
                dbUpdate = new DB21010000();
                dbUpdate.setYssPub(this.pub);
                //----------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre); //更新 DB2 主键名
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustFieldPrecision(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010000); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010000; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //-----------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010001) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010000) == 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010001); //更新组合群模块版本号
                }
                dbUpdate = new DB21010001();
                dbUpdate.setYssPub(this.pub);
                //----------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre); //更新 DB2 主键名
                dbUpdate.createTable(sPre);
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustFieldName(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010001); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010001; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //-----------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010001) == 0)) { //版本号1.0.1.0002  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010002); //更新组合群模块版本号
                }
                dbUpdate = new DB21010002();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.createTable(sPre);
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010002); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010002; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002sp1) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002) == 0)) { //版本号1.0.1.0002  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010002sp1); //更新组合群模块版本号
                }
                dbUpdate = new DB21010002sp1();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010002sp1); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010002sp1; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002sp2) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002sp1) ==
                 0)) { //版本号1.0.1.0002  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010002sp2); //更新组合群模块版本号
                }
                dbUpdate = new DB21010002sp2();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustTableData(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010002sp2); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010002sp2; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010002sp2) ==
                 0)) { //版本号1.0.1.0003  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003); //更新组合群模块版本号
                }
                dbUpdate = new DB21010003();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.addTableField(sPre);
                dbUpdate.createTable(sPre);
                //dbUpdate.createTable();
                dbUpdate.adjustTableData(sPre);
                dbUpdate.adjustFieldPrecision(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp1) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003) == 0)) { //版本号1.0.1.0003sp1  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp1); //更新组合群模块版本号
                }
                dbUpdate = new DB21010003sp1();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.adjustFieldName(sPre);
                dbUpdate.adjustFieldPrecision(sPre); //2008-6-16 单亮 修改字段的长度
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp1); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp1; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp2) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp1) ==
                 0)) { //版本号1.0.1.0003sp2  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp2); //更新组合群模块版本号
                }
                dbUpdate = new DB21010003sp2();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.addTableField(sPre); //添加字段 2008-07-14 蒋春
                dbUpdate.adjustFieldPrecision(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp2); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp2; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            //BugNo:0000304 edit by jc
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp3) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp2) ==
                 0)) { //版本号1.0.1.0003sp3  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp3); //更新组合群模块版本号
                }
                dbUpdate = new DB21010003sp3();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustTableData(sPre); //调整数据
                dbUpdate.adjustFieldPrecision(sPre); //调整字段精度
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp3); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp3; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //----------------------jc
            //BugNo:0000363 edit by jc
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp4) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp3) ==
                 0)) { //版本号1.0.1.0003sp4  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp4); //更新组合群模块版本号
                }
                dbUpdate = new DB21010003sp4();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.addTableField(sPre); //添加字段
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp4); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp4; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //----------------------jc
            //BugNo:0000363 edit by jc
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp5) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp4) ==
                 0)) { //版本号1.0.1.0003sp5  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp5); //更新组合群模块版本号
                }
                dbUpdate = new DB21010003sp5();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.adjustFieldPrecision(sPre); //调整表字段
                dbUpdate.adjustTableKey(sPre); //调整表主键   //BugNo:0000425 edit by jc
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp5); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp5; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //----------------------jc

            //======add by MaoQiwen  20080820  bug:0000401======//
            //=======更改表TB_VCH_DICT中FDICTNAME字段的长度=======//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp6) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp5) ==
                 0)) { //版本号1.0.1.0003sp6  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp6); //更新组合群模块版本号
                }
                dbUpdate = new DB21010003sp6();
                dbUpdate.setYssPub(this.pub);
                //===========开始一次更新=============//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.adjustFieldPrecision(sPre); //更改字段长度
                dbUpdate.createTable(sPre); //创建表      //edit by jc
                dbUpdate.addTableField(sPre); //添加表字段   //BugNo:0000429 edit by jc
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp6); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp6; //更改内存中的最大版本
                if (!bIsAutoUpdate) {
                    return;
                }
            }

            //BugNo:0000363 edit by jc
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp7) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp6) ==
                 0)) { //版本号1.0.1.0003sp7  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp7); //更新组合群模块版本号
                }
                dbUpdate = new DB21010003sp7();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(sPre);
                dbUpdate.addTableField(sPre); //添加表字段  //BugNo:0000447 edit by jc
                dbUpdate.adjustFieldPrecision(sPre); //调整表字段
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp7); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp7; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //----------------------jc
            //add by leeyu 0909
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp8) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp7) ==
                 0)) {
                if (bIsAutoUpdate) {
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp8);
                }
                dbUpdate = new DB21010003sp8();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.createTable(sPre);
                dbUpdate.addTableField(sPre); //BugNo:0000462 edit by jc
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp8);
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp8;
                if (!bIsAutoUpdate) {
                    return;
                }
            }
            // by leeyu

            //edit by 张旭 2008-9-26
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp9) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp8) ==
                 0)) { //版本号1.0.1.0003sp7  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010003sp9); //更新组合群模块版本号
                }
                dbUpdate = new DB21010003sp9();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.addTableField(sPre); //添加表字段
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010003sp9); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010003sp9; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            // by 张旭
            //========添加新版本，20081020 bug 0000486 by leeyu
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010004) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010003sp9) ==
                 0)) { //版本号1.0.1.0004  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010004); //更新组合群模块版本号
                }
                dbUpdate = new DB21010004();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.addTableField(sPre); //添加表字段
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010004); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010004; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //******************* 王晓光 20081024 *******************//
            //******************* 更新数据库中的表，给表 TB_001_PARA_FIXINTEREST 添加字段 FAMORTIZATION；修改 Tb_Base_CalcInsMetic中字段 FFormula 的长度  ****************//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010005) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010004) == 0)) { //版本号1.0.1.0005  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010005); //更新组合群模块版本号
                }
                dbUpdate = new DB21010005();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.addTableField(sPre); //给表 TB_001_PARA_FIXINTEREST 添加字段 FAMORTIZATION,FFACTRATE
                dbUpdate.adjustFieldName(sPre); //修改 Tb_Base_CalcInsMetic中字段 FFormula 的长度
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010005); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010005; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }

            //******************* 王晓光 20081124 *******************//
            //******************* 更新数据库中的表，修改 Tb_XXX_Data_BonusShare、TB_XXX_DATA_RIGHTSISSUE、Tb_XXX_Data_Dividend 中的字段 FRATIO 的长度  ****************//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010006) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010005) == 0)) { ////版本号1.0.1.0006  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010006); //更新组合群模块版本号
                }
                dbUpdate = new DB21010006();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustFieldPrecision(sPre); //修改 Tb_XXX_Data_BonusShare、TB_XXX_DATA_RIGHTSISSUE、Tb_XXX_Data_Dividend 中的字段 FRATIO 的长度
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010006); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010006; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //==========================================================
            /**
             * date   : 2008-11-25
             * author : sunkey
             * bugid  : MS00035
             * desc   : 更新1.0.1.0007版本DB2数据库，为TB_BASE_COUNTRY添加协议(FAgreement)字段
             */
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010007) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010006) == 0)) { ////版本号1.0.1.0007  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010007); //更新组合群模块版本号
                }
                dbUpdate = new DB21010007();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustFieldPrecision(sPre); //修改 Tb_XXX_Data_BonusShare、TB_XXX_DATA_RIGHTSISSUE、Tb_XXX_Data_Dividend 中的字段 FRATIO 的长度
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010007); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010007; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }

            //==========================================================
            /**
             * date   : 2008-12-08
             * author : 王晓光
             * bugid  : MS00060
             * desc   : 将证券应收应付中分红的汇兑损益数据逻辑删除。在回购品种信息设置（Tb_XXX_Para_Purchase）中新增一字段FCircuMarket——“流通市场”。并将其设置为主键
             */
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010008) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010007) == 0)) { ////版本号1.0.1.0008  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010008); //更新组合群模块版本号
                }
                dbUpdate = new DB21010008();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustTableData(sPre); //将证券应收应付中分红的汇兑损益数据逻辑删除。
                dbUpdate.adjustFieldPrecision(sPre); //在回购品种信息设置（Tb_XXX_Para_Purchase）中新增一字段FCircuMarket——“流通市场”。并将其设置为主键
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010008); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010008; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }

            //=========================================================
            //sj add 20081226
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010009) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010008) == 0)) { ////版本号1.0.1.0009  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010009); //更新组合群模块版本号
                }
                dbUpdate = new DB21010009();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustFieldPrecision(sPre); //在回购品种信息设置（Tb_XXX_Para_Purchase）中新增一字段FINBEGINTYPE——“计息起始日类型”。并将其设置为主键
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010009); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010009; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //leeyu add 20081229
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010010) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010009) == 0)) { ////版本号1.0.1.0010  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010010); //更新组合群模块版本号
                }
                dbUpdate = new DB21010010();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustFieldPrecision(); //修改词汇数据表的备注信息长度。
                dbUpdate.adjustFieldPrecision(sPre); //MS00160 QDV4赢时胜上海2009年1月4日02_B 2009.01.13 方浩 债券信息中发行价格小数位数保留12位
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010010); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010010; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }

            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010011) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010010) == 0)) { ////版本号1.0.1.0011  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010011); //更新组合群模块版本号
                }
                dbUpdate = new DB21010011();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.adjustFieldPrecision(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010011); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010011; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //2009年2月26日版本
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010012) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010011) == 0)) { ////版本号1.0.1.0012  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010012); //更新组合群模块版本号
                }
                dbUpdate = new DB21010012();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010012); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010012; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //-----3.16最新的更新版本-------------
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010013) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010012) == 0)) { ////版本号1.0.1.0012  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010013); //更新组合群模块版本号
                }
                dbUpdate = new DB21010013();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.createTable();
                dbUpdate.createTable(sPre);
                dbUpdate.addTableField(sPre);
                dbUpdate.adjustTableData(sPre); //调整数据
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010013); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010013; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //-----

//--MS00273 QDV4中金2009年02月27日01_A add by songjie 2009.03.23-----//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010014sp4) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010013) == 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010014sp4);
                }
                dbUpdate = new DB21010014sp4();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010014sp4); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010014sp4; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //--MS00273 QDV4中金2009年02月27日01_A add by songjie 2009.03.23-----//

            //--MS00339 QDV4建行2009年1月16日01_B add by songjie 2009.03.27----//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010014sp6) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010014sp4) ==
                 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010014sp6);
                }
                dbUpdate = new DB21010014sp6();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.addTableField(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010014sp6); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010014sp6; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //--MS00339 QDV4建行2009年1月16日01_B add by songjie 2009.03.27----//

            //--MS00007 QDV4.1赢时胜上海2009年2月1日06_A add by songjie 2009.03.25----//
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010015) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010014sp6) ==
                 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010015);
                }
                dbUpdate = new DB21010015();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.addTableField(sPre);
                dbUpdate.createTable();
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010015); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010015; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }

            //xuqiji 20090611 QDV4建行2009年6月9日01_B MS00490 财务估值核对脚本下拉条不起作用
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010015sp11) <
                 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010015) ==
                 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010015sp11);
                }
                dbUpdate = new DB21010015sp11();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.adjustFieldPrecision(sPre);
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010015sp11); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010015sp11; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
            //----------------------------------------end---------------------------------------//

            //--MS00007 QDV4.1赢时胜上海2009年2月1日06_A add by songjie 2009.03.25----//
            //2009.04.07 蒋锦 MS00352
            if ( (bIsAutoUpdate) ?
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010016) < 0) :
                (this.strVersionNum.compareTo(YssCons.YSS_VERSION_1010015) ==
                 0)) {
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum(sPre, YssCons.YSS_VERSION_1010016);
                }
                dbUpdate = new DB21010016();
                dbUpdate.setYssPub(this.pub);
                dbUpdate.addTableField();
                this.updateVersionSuccess(sPre, YssCons.YSS_VERSION_1010016); //版本更新成功
                this.strVersionNum = YssCons.YSS_VERSION_1010016; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
            }
        } else {
            throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
        }
    }

    public void updateCommon(boolean bIsAutoUpdate) throws YssException {
        if (dbl.getDBType() == YssCons.DB_ORA) { //进行数据库类型的判断
            if (bIsAutoUpdate) {
                if (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_NUMBER) ==
                    0) {
                    return;
                }
            }
            if ( (bIsAutoUpdate) ?
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010000) < 0) :
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010000) == 0)) { //版本判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum("", YssCons.YSS_VERSION_1010000); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010000();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(); //更新主键名
                dbUpdate.adjustFieldPrecision();
                dbUpdate.addTableField();
                this.updateVersionSuccess("Common", YssCons.YSS_VERSION_1010000); //版本更新成功
                this.strCommVerNum = YssCons.YSS_VERSION_1010000; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010001) < 0) :
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010000) == 0)) { //版本号1.0.1.0001
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum("", YssCons.YSS_VERSION_1010001); //更新通用模块版本号
                }
                dbUpdate = new Ora1010001();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_Ora(); //更新主键名
                dbUpdate.createTable();
                this.updateVersionSuccess("Common", YssCons.YSS_VERSION_1010001); //版本更新成功
                this.strCommVerNum = YssCons.YSS_VERSION_1010001; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010002) < 0) :
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010001) == 0)) { //版本号1.0.1.0002  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum("", YssCons.YSS_VERSION_1010002); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010002();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//                    //更新主键名
                dbUpdate.addTableField();
                dbUpdate.executeSysDataSql();
                this.updateVersionSuccess("Common", YssCons.YSS_VERSION_1010002); //版本更新成功
                this.strCommVerNum = YssCons.YSS_VERSION_1010002; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010003) < 0) :
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010002) == 0)) { //版本号1.0.1.0003  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum("", YssCons.YSS_VERSION_1010003); //更新组合群模块版本号
                }
                dbUpdate = new Ora1010003();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.createTable();
                this.updateVersionSuccess("Common", YssCons.YSS_VERSION_1010003); //版本更新成功
                this.strCommVerNum = YssCons.YSS_VERSION_1010003; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
                
              //---#580 建信上线需提供部分方案支持 add by jiangshichao 20110324
				if (dbUpdate.existsTabColumn_Ora("TB_FUN_VERSION", "FSQLSTR")) {
					 dbUpdate = new Ora1010016();
		             dbUpdate.setYssPub(this.pub);
		             dbUpdate.addTableField();
				}
            }

        } else if (dbl.getDBType() == YssCons.DB_DB2) {
            if (bIsAutoUpdate) {
                if (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_NUMBER) ==
                    0) {
                    return;
                }
            }
            if ( (bIsAutoUpdate) ?
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010000) < 0) :
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010000) == 0)) { //版本判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum("", YssCons.YSS_VERSION_1010000); //更新公共模块版本号
                }
                dbUpdate = new DB21010000();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(); //更新主键名
                dbUpdate.addTableField();
                dbUpdate.adjustFieldPrecision();
                this.updateVersionSuccess("Common", YssCons.YSS_VERSION_1010000); //版本更新成功
                this.strCommVerNum = YssCons.YSS_VERSION_1010000; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010001) < 0) :
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010000) == 0)) { //版本判断 版本1.0.1.0001
                if (bIsAutoUpdate) {
                    this.setNewVersionNum("", YssCons.YSS_VERSION_1010001); //更新组合群模块版本号
                }
                dbUpdate = new DB21010001();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.updatePKName_DB2(); //更新主键名
                dbUpdate.createTable();
                this.updateVersionSuccess("Common", YssCons.YSS_VERSION_1010001); //版本更新成功
                this.strCommVerNum = YssCons.YSS_VERSION_1010001; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010002) < 0) :
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010001) == 0)) { //版本号1.0.1.0002  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum("", YssCons.YSS_VERSION_1010002); //更新组合群模块版本号
                }
                dbUpdate = new DB21010002();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.addTableField();
                dbUpdate.executeSysDataSql();
                this.updateVersionSuccess("Common", YssCons.YSS_VERSION_1010002); //版本更新成功
                this.strCommVerNum = YssCons.YSS_VERSION_1010002; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            if ( (bIsAutoUpdate) ?
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010003) < 0) :
                (this.strCommVerNum.compareTo(YssCons.YSS_VERSION_1010002) == 0)) { //版本号1.0.1.0003  自动更新用小于进行判断，手动更新用等于进行判断
                if (bIsAutoUpdate) { //如果是手动更新就不用重新设置版本号了
                    this.setNewVersionNum("", YssCons.YSS_VERSION_1010003); //更新组合群模块版本号
                }
                dbUpdate = new DB21010003();
                dbUpdate.setYssPub(this.pub);
                //--------------开始依次更新---------------//
                dbUpdate.createTable();
                this.updateVersionSuccess("Common", YssCons.YSS_VERSION_1010003); //版本更新成功
                this.strCommVerNum = YssCons.YSS_VERSION_1010003; //更改内存中的最大版本
                if (!bIsAutoUpdate) { //如果是手动更新，一次只更新一个版本
                    return;
                }
                //---------------------------------------//
            }
            
        } else {
            throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
        }
    }
    
    public boolean updateTable(String newVersion) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;
    	String set = "";
    	String curAssetGroupCode = pub.getAssetGroupCode();
    	String curTbPrefix = pub.getPrefixTB();
    	StringBuffer buf = new StringBuffer();//modified by yeshenghong 20120322 story 2164
    	try {
//    		ou.write(("updating" + "\r\f" + 0).getBytes());
//    		runStatus.appendRunDesc("updatedb", "updating" + "\r\f" + 1);
    		strSql = " select distinct max(t.fvernum) as fvernum,t.fassetgroupcode,a.ftabprefix from tb_fun_version t " + 
    				 " join tb_sys_assetgroup a on t.fassetgroupcode = a.fassetgroupcode group by t.fassetgroupcode," +
    				 " a.ftabprefix order by t.fassetgroupcode "; 
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			String fvernum = rs.getString("fvernum");
    			if(fvernum.equals(newVersion))
    			{
    				continue;
    			}
    			set = rs.getString("fassetgroupcode");
    			this.pub.setAssetGroupCode(set);
    			this.pub.setPrefixTB(rs.getString("ftabprefix"));
		    	this.getOldVersionNum(set);
		        //MS01796 add by licai 20101019 TOOLS工具更新程序功能需求  ---end--
		        this.updateEntry(set);
		         //---数据库版本的比对和更新-2007.12.11--蒋锦---end---//
		        //MS01796 add by licai 20101019 TOOLS工具更新程序功能需求  
		        this.getOldVersionNum(set);
		        this.copySource(set);//add by huangqirong 2011-09-08 story #1286
		        runStatus.appendRunDesc("updatedb", "updating" + "\r\f" + 1 + "\r\f" + set + "\r\ffinished");
		        buf.append(set).append(" ");
    		}
//    		ou.write(("updating" + "\r\f" + -1).getBytes());
    		runStatus.appendRunDesc("updatedb", "updating" + "\r\f" + 0);
    		this.pub.setAssetGroupCode(curAssetGroupCode);
    		this.pub.setPrefixTB(curTbPrefix);
	        return true;
    	} catch (Exception e) {
			// TODO Auto-generated catch block
    		buf.append("组合群数据库更新成功，\r").append(set).append(" 组合群数据库更新失败");
			throw new YssException(buf.toString(),e);
		} finally
		{
			dbl.closeResultSetFinal(rs);
		}
        //---------end------
						
		
    }
    
//    public int getGroupCount() throws YssException
//    {
//    	
//    	String strSql = " select count(distinct fassetgroupcode) as count from tb_sys_assetgroup ";
//    	ResultSet rs = null;
//    	try {
//			rs = dbl.openResultSet(strSql);
//			if(rs.next())
//    		{
//				return rs.getInt("count");
//    		}else
//    		{
//    			return 0;
//    		}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			throw new YssException("获取组合群数目失败！",e);
//		}
//    	
//    }

//	/**判断系统是否需要更新配置文件
//	 * //MS01796 add by licai 20101019 TOOLS工具更新程序功能需求  
//	 * @param versionBefUpdate
//	 * @param versionAftUpdate
//	 * @return 数据库更新前后版本发生变化的话，就执行更新配置文件，返回true
//	 */
//	private boolean isNeedUpdateCfgFiles(String versionBefUpdate,String versionAftUpdate){
//		return !versionAftUpdate.equals(versionBefUpdate);
//	}
}
