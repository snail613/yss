package com.yss.dsub;

import java.sql.*;
import java.util.*;
import com.yss.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.sql.DataSource;

import com.yss.util.YssException;
import java.io.*;
import java.lang.reflect.Method;

import oracle.jdbc.OracleTypes;

import org.apache.log4j.*;
import com.yss.pojo.sys.YssPageInationBean;//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
import com.yss.vsub.YssPageInation;//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu 
/**
 * <p>Title: </p>
 * <p>Description: 管理数据库操作，包括提供connection，以及跨数据库调用差异管理<br>
 * 还包括部分顶层全局量，如子系统代码</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @author alex
 * @version 1.0
 */

public final class DbBase
    implements HttpSessionBindingListener {
	
	//add by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----start
	private String url="";	
	private String user="";
	private CallableStatement procStmt = null;
	//STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞 add by jsc
	private static Context ctx = null;
	private static DataSource ds = null;
	public CallableStatement getProcStmt() {
		return procStmt;
	}
	public void setProcStmt(CallableStatement procStmt) {
		this.procStmt = procStmt;
	}
	public String getUrl() {
		return url;
	}
	public String getUser() {
		return user;
	}
	//add by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----end
	
	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
	private static Logger log = Logger.getLogger("D");
	//private Logger log = null;
	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
    public DbBase() {
    }

    /**
     * 新建并建立数据库连接
     * @param bConnectNow boolean： true就连接
     * @throws YssException
     */
    public DbBase(boolean bConnectNow) throws YssException {
        if (bConnectNow) {
        	
            loadConnection();
        }
    }

    public String clientAddr = null; //客户端机器的地址，可能是单一IP，也可能是上网网关IP+客户机局域网IP

    public void valueBound(HttpSessionBindingEvent parm1) {
        /**@todo Implement this javax.servlet.http.HttpSessionBindingListener method*/
        //throw new java.lang.UnsupportedOperationException("Method valueBound() not yet implemented.");
        return;
    }

    public void valueUnbound(HttpSessionBindingEvent parm1) {
        /**@todo Implement this javax.servlet.http.HttpSessionBindingListener method*/
        //throw new java.lang.UnsupportedOperationException("Method valueUnbound() not yet implemented.");
        //这里可以放置session结束的清除代码
        try {
            closeConnection();
        } catch (Exception e) {}
        return ;
    }

    private static String dbDriverLoaded = null; //已经加载的driver，避免多次调用class.forname

    public int dbType = 0; //数据库类型

    //全局connection，也可不使用它，而在使用者每次loadConnection请求的时候重新建立
    private Connection conPub = null;

    private int moduleCode; //子系统代码，具体定义看权限设置模块
    
    private boolean bSafeMode = false;//若为true，通过读取加密配置文件来返回一个数据库连接     MS01044 对数据库配置文件进行加密设置  panjunfang add 20100409

    //---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A start---//
    private String dataBaseID = "";//数据库连接ID
    private boolean changeDtBase = false;
    
    public String getDataBaseID(){
    	return dataBaseID;
    }
    
    public void setDataBaseID(String dataBaseID){
    	this.dataBaseID = dataBaseID;
    }
    
    public boolean getChangeDtBase(){
    	return changeDtBase;
    }
    
    public void setChangeDtBase(boolean changeDtBase){
    	this.changeDtBase = changeDtBase;
    }
    //---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A end---//
    
    public boolean isbSafeMode() {
		return bSafeMode;
	}

	public void setbSafeMode(boolean bSafeMode) {
		this.bSafeMode = bSafeMode;
	}

	/**子系统代码的设置和获取*/
    public void setModuleCode(int mCode) {
        moduleCode = mCode;
    }

    public int getModuleCode() {
        return moduleCode;
    }

    public int getDBType() {
        return this.dbType;
    }

    //数据库用户，模式名称
    private String strDBUser = null;

    public Connection loadConnection() throws YssException {
        return loadConnection("");
    }
    
    public Connection loadConnectionSafeMode() throws YssException {
    	return loadConnectionSafeMode("");
    }

    /**获取一个可用的数据库连接
     * 如果不使用conPub（每次都重新打开连接）则
     * 注意部分驱动如mssqlserver的url需要加";selectmethod=cursor"才能打开可写resultset
     */

    public Connection loadConnection(String sDbLbl) throws YssException {
        String dbDriver = null;
        String dbUrl = null;
        String dbUser = null;
        String dbPass = null;
		//STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞 add by jsc
        String dbJndi = null;
        String dtype = null;
        int i = 0, j = 0;

        Connection dbConnection = null;

        try {
            // 如果conPub已经打开，那么返回conPub，否则打开并返回连接
            if (sDbLbl == null || sDbLbl.length() == 0) {
                sDbLbl = "[db_yssimsas]";
            }
            
            //---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A start---//
            //若在登陆界面，数据库ID被重新选择，则关闭 conPub 重新根据 数据库ID 对应的数据库连接配置新建connection
            if(this.changeDtBase && conPub != null){ //modify huangqirong 2012-11-26 bug #6314
            	conPub.close();
            	conPub = null;
            }
            //---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A end---//
            
            if (conPub != null && !conPub.isClosed()) {
                return conPub;
            }

            if(this.bSafeMode){
            	return loadConnectionSafeMode(sDbLbl);
            }
            /*todo考虑使用内部类来加载数据库连接设置*/
            //try块

            try {
                String[] pa = null;
                /*
                            java.io.File ff = new java.io.File("/dbsetting.txt");
                 java.io.FileInputStream fi = new java.io.FileInputStream(ff);

                            int size = (int) ff.length();
                            byte[] fb = new byte[size];
//            int i = 0, j = 0;

                            while (i < size) {
                               j = fi.read(fb, i, size - i);
                               if (j < 0)
                                  break;
                               i += j;
                            }
                 */
//            System.out.println("e:\\dbsetting.txt");
                
                String ps = YssFun.loadTxtFile("/dbsetting.txt");
                
                
              //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞  add by jsc 20120730
                //通过标识来判断数据库连接方式【ds_yssimsas】 连接池； 【db_yssimsas】普通连接方式
                if(ps.indexOf("*[ds_yssimsas]")<0&&ps.indexOf("[ds_yssimsas]")>=0){
                	if(!sDbLbl.equals("[db_ysslog]"))//add by yeshenghong 20130720  story 4082
                	{
	                	YssCons.Yss_DB_ConType = 1;  //判断数据库连接
	                	sDbLbl = "[ds_yssimsas]";
                	}else
                	{
                		return null;
                	}
                }
                
              //#174、#281 QDV4农行2010年10月21日01_B Suse Linux中回车为“\n”，而在Windows环境下为“\r\n”，因此在此处需要区别解析，panjunfang modify 20101108
                pa = ps.split(ps.indexOf("\r\n")>=0?"\r\n":"\n");
//            System.out.println(YssFun.loadTxtFile("/dbsetting.txt"));
                
                //---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A start---//
                ArrayList<String> alDataBaseID = new ArrayList<String>();
                if(YssCons.Yss_DB_ConType == 0){
                	//---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A start---//
                   
                    String dbID = "";
                    for (i = 0; i < pa.length; i++) {
                    	if (pa[i].trim().startsWith("[db_yssimsas"))
                    	{
                    		if(pa[i].trim().equalsIgnoreCase("[db_yssimsas]")){
                    			if(!alDataBaseID.contains("null")){
                    				alDataBaseID.add("null");
                        		}
                    		}
                    		if(pa[i].trim().startsWith("[db_yssimsas_")){
                    			dbID = YssFun.right(pa[i].trim(), pa[i].trim().length() - "[db_yssimsas_".length());
                    			dbID = dbID.substring(0,dbID.length() - 1);
                    			
                    			if(!alDataBaseID.contains(dbID)){
                    				alDataBaseID.add(dbID);
                        		}
                    		}
                    	}
                    }
                    
                    if(alDataBaseID.size() > 0) this.dataBaseID = ""; //add by huangqirong 2013-03-01 bug #7123
                    Iterator<String> itDbID = alDataBaseID.iterator();
                    while(itDbID.hasNext()){
                    	dataBaseID += itDbID.next() + ",";
                    }
                    if(dataBaseID.length() > 1){
                    	dataBaseID = dataBaseID.substring(0,dataBaseID.length() - 1);
                    }
                    //---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A end---//
                	
                }
                
                for (i = 0; i < pa.length; i++) { //星号注释

                    // if (!pa[i].trim().startsWith("*") && pa[i].trim().length()!=0)
                    if (pa[i].trim().equalsIgnoreCase(sDbLbl)) {
                        break;
                    }
                }
                //	//STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞 add by jsc
                if(YssCons.Yss_DB_ConType == 0){
                	 /**shashijie 2012-03-08 BUG 4000,解决多客户端访问一个后台退出报异常BUG */
                    if (i >= pa.length) {
                    	//---edit by songjie 2012.05.23 BUG 4586 QDV4赢时胜(测试)2012年5月17日03_B start---//
                    	if(!alDataBaseID.contains("null")){
                    		//---edit by songjie 2012.12.12 STORY #2343 QDV4建行2012年3月2日04_A start---//
                    		if(!sDbLbl.equals("[db_ysslog]")){
                    			sDbLbl = "[db_yssimsas_" + alDataBaseID.get(0) + "]";
                    		}
                    		//---edit by songjie 2012.12.12 STORY #2343 QDV4建行2012年3月2日04_A end---//
                    	}else{
                    		//---edit by songjie 2012.12.12 STORY #2343 QDV4建行2012年3月2日04_A start---//
                    		if(!sDbLbl.equals("[db_ysslog]")){//排除日志数据库链接的条件
                    			sDbLbl = "[db_yssimsas]";
                    		}
                    		//---edit by songjie 2012.12.12 STORY #2343 QDV4建行2012年3月2日04_A end---//
                    	}
                    	
                    	for(i = 0; i < pa.length; i++){
                            if (pa[i].trim().equalsIgnoreCase(sDbLbl)) {
                                break;
                            }
                    	}
                    	
                    	if(i >= pa.length){
                    		return conPub;
                    	}
                    	//---edit by songjie 2012.05.23 BUG 4586 QDV4赢时胜(测试)2012年5月17日03_B start---//
    				}
                    /**end*/
                    
                    dbDriver = pa[i + 1].trim();
                    dbUrl = pa[i + 2].trim();
                    dbUser = pa[i + 3].trim();
                    dbPass = pa[i + 4].trim();
                    dtype = pa[i + 5].trim();
                    dbType = dtype.equalsIgnoreCase("sql") ? YssCons.DB_SQL :
                        (dtype.equalsIgnoreCase("db2") ? YssCons.DB_DB2 :
                         YssCons.DB_ORA);
                    
                    /**shashijie 2011-10-12 STORY 1698  */
                    String OracleVersion = "";//版本信息
                    try {
                    	OracleVersion = pa[i + 6].trim();//获取第五行数据
    				} catch (Exception e) {
    					OracleVersion = "";
    				}
                    YssCons.Yss_OracleVersion = getOracleVersion(OracleVersion);//获取数据库版本
                    /**end*/
                //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞 add by jsc   
				//连接池方式
                }else if(YssCons.Yss_DB_ConType==1){
               
                	dbJndi = pa[i + 1].trim();
                    dbUrl = pa[i + 2].trim();
                    dbUser = pa[i + 3].trim();
                    dtype = pa[i + 4].trim();
                    dbType = dtype.equalsIgnoreCase("sql") ? YssCons.DB_SQL :
                        (dtype.equalsIgnoreCase("db2") ? YssCons.DB_DB2 :
                         YssCons.DB_ORA);
                    /**shashijie 2011-10-12 STORY 1698  */
                    String OracleVersion = "";//版本信息
                    try {
                    	OracleVersion = pa[i + 5].trim();//获取第五行数据
    				} catch (Exception e) {
    					OracleVersion = "";
    				}
                    YssCons.Yss_OracleVersion = getOracleVersion(OracleVersion);//获取数据库版本
                    YssCons.serviceType = pa[i + 6].trim().toUpperCase();
                    /**end*/
                }
               
//            fi.close();
            } catch (Exception e) {
                System.out.println("Exception:" + e.getMessage());
                throw new YssException("访问数据库出错，请检查连接设置！", e);
            }

            closeConnection(); //处理conPub
            //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞 add by jsc
            if(YssCons.Yss_DB_ConType==1){
    			if(ctx==null){
    				ctx = new InitialContext();
       			    ds = (DataSource)ctx.lookup(dbJndi);
    			}
    			dbConnection = ds.getConnection();
    			conPub = dbConnection;//add by yeshenghong 20130720  story 4082
            }else{
                if (dbDriverLoaded == null ||
                        !dbDriverLoaded.equalsIgnoreCase(dbDriver)) {
                        Class.forName(dbDriver).newInstance(); //注意，这句话未必每次都要执行了，也许前面执行过
                        dbDriverLoaded = dbDriver;
                    }
                    dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            }
            
            //add by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----start
        	this.url=dbUrl;
        	this.user=dbUser;
        	//add by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----end
            strDBUser = dbUser; //后续可能要使用
            conPub = dbConnection; //如果不保存连接，则不要这句
        } catch (SQLException se) {        	
            throw new YssException("访问数据库出错，请检查连接设置！\f\f" + this.dataBaseID + "\f\f", se); // modify huangqirong 2013-03-11 bug #7279
        } catch (Exception ce) {
            throw new YssException("加载数据库驱动程序出错！", ce);
        }

        return dbConnection;
    }

    
    /**
     * 获取一个可用的清算数据库连接// add by yeshenghong story2525 20120519
     * 
     */
    public Connection loadQsConnection( ) throws YssException {
        String dbDriver = null;
        String dbUrl = null;
        String dbUser = null;
        String dbPass = null;
        byte qsDbType;
        int i = 0;
        Connection dbConnection;
        try {
            
//        	if(this.bSafeMode)
//        	{
//        		return this.loadQsConnectionSafeMode();
//        	}
            String sDbLbl = "[db_yssimsasqs3]";
            
            /*todo考虑使用内部类来加载数据库连接设置*/
            String[] pa = null;
            String ps=YssFun.loadTxtFile("/dbsetting.txt");
            if(ps=="")
        	{
        		return this.loadQsConnectionSafeMode();
        	}
          //#174、#281 QDV4农行2010年10月21日01_B Suse Linux中回车为“\n”，而在Windows环境下为“\r\n”，因此在此处需要区别解析，panjunfang modify 20101108
            pa = ps.split(ps.indexOf("\r\n")>=0?"\r\n":"\n");
//            System.out.println(YssFun.loadTxtFile("/dbsetting.txt"));
            
            for (i = 0; i < pa.length; i++) { //星号注释

                // if (!pa[i].trim().startsWith("*") && pa[i].trim().length()!=0)
                if (pa[i].trim().equalsIgnoreCase(sDbLbl)) {
                    break;
                }
            }
            dbDriver = pa[i + 1].trim();
            dbUrl = pa[i + 2].trim();
            dbUser = pa[i + 3].trim();
            dbPass = pa[i + 4].trim();
            String dtype = pa[i + 5].trim();
            qsDbType = dtype.equalsIgnoreCase("sql") ? YssCons.DB_SQL :
                (dtype.equalsIgnoreCase("db2") ? YssCons.DB_DB2 :
                 YssCons.DB_ORA);
            
            String OracleVersion = "";//版本信息
            //start modify huangqirong 2013-05-31 story #3871 避免报错
            try {
            	OracleVersion = pa[i + 6].trim();//获取第五行数据
			} catch (Exception e) {
				OracleVersion = "";
			}
			//end modify huangqirong 2013-05-31 story #3871 避免报错
            YssCons.Yss_OracleVersion = getOracleVersion(OracleVersion);//获取数据库版本
//            if (dbDriverLoaded == null ||
//                    !dbDriverLoaded.equalsIgnoreCase(dbDriver)) {
//                    Class.forName(dbDriver).newInstance(); 
//                    dbDriverLoaded = dbDriver;
//            }
            Class.forName(dbDriver).newInstance(); 
            dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            return dbConnection;
        } catch (SQLException se) {
            throw new YssException("访问清算数据库出错，请检查连接设置！");
        } catch (Exception ce) {
            throw new YssException("请正确配置清算系统数据库连接！");
        }
    }
    
    public Connection loadQsConnectionSafeMode( ) throws YssException
    {
    	 String dbDriver = null;
         String dbUrl = null;
         String dbUser = null;
         String dbPass = null;
         Connection dbConnection;
         String sDbLbl =  "[db_yssimsasqs3]";
         GenDBInfo dbInfo = null;
         //add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A
//         Properties props = new Properties();
         try {
             dbInfo = new GenDBInfo();
             File dbFile = new File(File.separator + "dbsetting.properties");
             if(!dbFile.exists()){
             	throw new YssException("访问清算数据库出错！请先配置数据库连接！");
             }
                 
// 			 InputStream in = new BufferedInputStream(new FileInputStream(dbFile));
// 			 props.load(in);
             dbDriver = dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&driver");
             
             dbUrl = dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&url");
 			//20120315 modified by liubo.Bug #3993
             //使用密文加密dbsetting时，加密密码的同时也要加密用户名
 			//===================================
//                 dbUser = dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&user");
             dbUser = dbInfo.decrypt("yssusername", dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&user"));
 			//=================end==================
             dbPass = dbInfo.decrypt("ysspassword", dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&password"));
             Class.forName(dbDriver).newInstance(); //注意，这句话未必每次都要执行了，也许前面执行过
             dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
         } catch (SQLException se) {
             throw new YssException("访问清算数据库出错，请检查连接设置！");
         } catch (Exception ce) {
             throw new YssException("加载清算数据库驱动程序出错，请正确配置清算系统数据库连接！");
         }

         return dbConnection;
    	
    }
    
    
    /**
     * 获取一个可用的清算数据库连接
     * add by zhangjun  20120601
     * STORY #2420 关于QD系统支持赢时胜直联电子对账系统V2.5的需求
     */
    public Connection loadDZConnection( ) throws YssException {
        String dbDriver = null;
        String dbUrl = null;
        String dbUser = null;
        String dbPass = null;
        int tag = -1 ;//add by huangqirong 2012-09-06 bug #5553
        byte qsDbType;
        int i = 0;
        Connection dbConnection;
        try {
            String sDbSQL = "[db_yssimsasSQL]";
            String sDbORA = "[db_yssimsasORA]";
            
            /*todo考虑使用内部类来加载数据库连接设置*/
            String[] pa = null;
            String ps=YssFun.loadTxtFile("/dbsetting.txt");
            
            //modify huangqirong 2012-09-07 bug #5553 根据用户登陆模式来判断旗舰版目标库是否也需要以加密模式登陆
            if(this.bSafeMode)
            	return this.loadDZConnectionSafeMode();
            /*if(ps.equalsIgnoreCase(""))
        	{
        		return this.loadDZConnectionSafeMode();
        	}*/
            //---end---
            //Linux中回车为“\n”，而在Windows环境下为“\r\n”，因此在此处需要区别解析，
            pa = ps.split(ps.indexOf("\r\n")>=0?"\r\n":"\n");           
            
            for (i = 0; i < pa.length; i++) { //星号注释
                if (pa[i].trim().equalsIgnoreCase(sDbORA) || pa[i].trim().equalsIgnoreCase(sDbSQL)) {
                	tag = i ;
                    break;
                }
            }            
            
            if(tag == -1) return null;  //add by huangqirong 2012-09-06 bug #5553
            
            dbDriver = pa[i + 1].trim();
            dbUrl = pa[i + 2].trim();
            dbUser = pa[i + 3].trim();
            dbPass = pa[i + 4].trim();
            String dtype = pa[i + 5].trim();
            qsDbType = dtype.equalsIgnoreCase("sql") ? YssCons.DB_SQL :
                (dtype.equalsIgnoreCase("db2") ? YssCons.DB_DB2 :
                 YssCons.DB_ORA);
            
            String OracleVersion = "";//版本信息
            if(pa.length > i+6){
            	OracleVersion = pa[i + 6].trim();//获取第五行数据
            	YssCons.Yss_OracleVersion = getOracleVersion(OracleVersion);//获取数据库版本
            }
            Class.forName(dbDriver).newInstance(); 
            dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            return dbConnection;
        } catch (SQLException se) {
            throw new YssException("访问电子对账目标数据库出错，请检查连接设置！");
        } catch (Exception ce) {
            throw new YssException("请正确配置电子对账目标数据库连接！");
        }
    }
    
    /**
     * add by zhangjun  20120601
     * STORY #2420 关于QD系统支持赢时胜直联电子对账系统V2.5的需求
     * @return
     * @throws YssException
     */
    public Connection loadDZConnectionSafeMode( ) throws YssException
    {
    	 String dbDriver = null;
         String dbUrl = null;
         String dbUser = null;
         String dbPass = null;
         Connection dbConnection;
         String sDbSQL = "[db_yssimsasSQL]";
         String sDbORA = "[db_yssimsasORA]";
         GenDBInfo dbInfo = null;
         
        
         try {
             dbInfo = new GenDBInfo();
             File dbFile = new File(File.separator + "dbsetting.properties");
             if(!dbFile.exists()){
             	throw new YssException("访问电子对账目标数据库出错！请先配置数据库连接！");
             }                 

             dbDriver = dbInfo.readValue(dbFile.getPath(), sDbORA + "&&driver");
             
             dbUrl = dbInfo.readValue(dbFile.getPath(), sDbORA + "&&url");
 			
             dbUser = dbInfo.decrypt("yssusername", dbInfo.readValue(dbFile.getPath(), sDbORA + "&&user"));
 			//=================end==================
             dbPass = dbInfo.decrypt("ysspassword", dbInfo.readValue(dbFile.getPath(), sDbORA + "&&password"));
             Class.forName(dbDriver).newInstance(); //注意，这句话未必每次都要执行了，也许前面执行过
             dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
         } catch (SQLException se) {
             throw new YssException("访问电子对账目标数据库出错，请检查连接设置！");
         } catch (Exception ce) {
             throw new YssException("加载电子对账目标数据库驱动程序出错，请正确配置电子对账目标数据库连接！");
         }

         return dbConnection;
    	
    }
    
    
    
    
	/**
     * 通过读取加密的配置文件来获取一个可用的数据库连接
     * 如果不使用conPub（每次都重新打开连接）则
     * 注意部分驱动如mssqlserver的url需要加";selectmethod=cursor"才能打开可写resultset
     */
    public Connection loadConnectionSafeMode(String sDbLbl) throws YssException {
        String dbDriver = null;
        String dbUrl = null;
        String dbUser = null;
        String dbPass = null;

        Connection dbConnection;
        
        GenDBInfo dbInfo = null;
        //add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A
        Properties props = new Properties();
        try {
            // 如果conPub已经打开，那么返回conPub，否则打开并返回连接
            if (sDbLbl == null || sDbLbl.length() == 0) {
                sDbLbl = "[db_yssimsas]";
            }
            
            //---add by songjie 2012.03.09 STORY #2279 QDV4大成基金2012年02月21日02_A start---//
            //若在登陆界面，数据库ID被重新选择，则关闭 conPub 重新根据 数据库ID 对应的数据库连接配置新建connection
            if(this.changeDtBase){
            	conPub.close();
            	conPub = null;
            }
            //---add by songjie 2012.03.09 STORY #2279 QDV4大成基金2012年02月21日02_A end---//
            
            if (conPub != null && !conPub.isClosed()) {
                return conPub;
            }

            try {
                dbInfo = new GenDBInfo();
                File dbFile = new File(File.separator + "dbsetting.properties");
                if(!dbFile.exists()){
                	throw new YssException("访问数据库出错！请先配置数据库连接！");
                }
                
                //---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A start---//
    			InputStream in = new BufferedInputStream(new FileInputStream(dbFile));
    			props.load(in);
    			Set keySet = props.keySet();
    			Iterator ite = keySet.iterator();
    			String strkey = "";
    			int idIndex = 0;
    			while(ite.hasNext()){
    				strkey = (String)ite.next();
    				if(strkey.indexOf("&&url") != -1){
    					idIndex = strkey.indexOf("&&url");
    					dataBaseID += strkey.substring(0, idIndex) + ","; 
    				}
    			}
    			
    			if(dataBaseID.length() > 1){
    				dataBaseID = dataBaseID.substring(0 ,dataBaseID.length() - 1);
    			}
    			//---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A end---//
    			
    			//--- delete by songjie 2013.05.02  兴业银行BUG start---//
    			//获取dbsetting.properties数据库链接出错
    			//20130425 dbsetting.properties中只 配置了一个db_yssimsas，dbInfo.decrypt()调用会报错java.lang.NullPointerException
//                if(sDbLbl.equalsIgnoreCase("[db_ysslog]") && dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&user")==null ){
//               	  sDbLbl = "[db_yssimsas]";
//                }
                //--- delete by songjie 2013.05.02 兴业银行BUG end---//
    			
                dbDriver = dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&driver");
                
                dbUrl = dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&url");
    			//20120315 modified by liubo.Bug #3993
                //使用密文加密dbsetting时，加密密码的同时也要加密用户名
    			//===================================
//                dbUser = dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&user");
                dbUser = dbInfo.decrypt("yssusername", dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&user"));
    			//=================end==================
                dbPass = dbInfo.decrypt("ysspassword", dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&password"));
                String dtype = dbInfo.readValue(dbFile.getPath(), sDbLbl + "&&dbtype"); 
                
                //--- add by songjie 2013.05.02  兴业银行BUG start---//
                //获取dbsetting.properties数据库链接出错
                if(dbDriver == null && dbUrl == null && dbUser == null){
                	return null;
                }
                //--- add by songjie 2013.05.02  兴业银行BUG end---//
                
                dbType = dtype.equalsIgnoreCase("sql") ? YssCons.DB_SQL :
                    (dtype.equalsIgnoreCase("db2") ? YssCons.DB_DB2 :
                     YssCons.DB_ORA);
                
                //add by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----start
            	this.url=dbUrl;
            	this.user=dbUser;
            	//add by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----end
            	
            } catch (Exception e) {
                System.out.println("Exception:" + e.getMessage());
                throw new YssException("访问数据库出错，请检查连接设置！", e);
            }

            closeConnection(); //处理conPub
            if (dbDriverLoaded == null ||
                !dbDriverLoaded.equalsIgnoreCase(dbDriver)) {
                Class.forName(dbDriver).newInstance(); //注意，这句话未必每次都要执行了，也许前面执行过
                dbDriverLoaded = dbDriver;
            }
            dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            strDBUser = dbUser; //后续可能要使用
            conPub = dbConnection; //如果不保存连接，则不要这句
        } catch (SQLException se) {
            throw new YssException("访问数据库出错，请检查连接设置！", se);
        } catch (Exception ce) {
            throw new YssException("加载数据库驱动程序出错！", ce);
        }

        return dbConnection;
    }

    // 改成不从 dbseting取连接的方式
    public Connection stConnection(String sconn) throws YssException {
        String dbDriver = null;
        String dbUrl = null;
        String dbUser = null;
        String dbPass = null;

        Connection dbConnection;

        try {
            // 如果conPub已经打开，那么返回conPub，否则打开并返回连接
            if (conPub != null && !conPub.isClosed()) {
                return conPub;
            }

            /*todo考虑使用内部类来加载数据库连接设置*/
            //try块
            try {
                String[] pa = null;
                String dtype = null;
                pa = new String(sconn).split(YssCons.YSS_LINESPLITMARK);
                for (int i = 0; i < pa.length; i++) { //星号注释
                    if (pa[i].trim().equalsIgnoreCase("")) {
                        break;
                    }
                }

                dbDriver = pa[0].trim();
                dbUrl = pa[1].trim();
                //dbUrl=pa[2].trim();
                dbUser = pa[2].trim();
                dbPass = pa[3].trim();
                dtype = pa[4].trim();

                dbType = dtype.equalsIgnoreCase("Sql Server2000") ? YssCons.DB_SQL :
                    (dtype.equalsIgnoreCase("db2") ? YssCons.DB_DB2 :
                     YssCons.DB_ORA);

            } catch (Exception e) {
                throw new YssException("访问数据库出错，请检查连接设置！", e);
            }

            closeConnection(); //处理conPub
            if (dbDriverLoaded == null ||
                !dbDriverLoaded.equalsIgnoreCase(dbDriver)) {
                Class.forName(dbDriver).newInstance(); //注意，这句话未必每次都要执行了，也许前面执行过
                dbDriverLoaded = dbDriver;
            }
            dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            strDBUser = dbUser; //后续可能要使用
            conPub = dbConnection; //如果不保存连接，则不要这句
        } catch (SQLException se) {
            throw new YssException("访问数据库出错，请检查连接设置！", se);
        } catch (Exception ce) {
            throw new YssException("加载数据库驱动程序出错！", ce);
        }

        return dbConnection;
    }

    /**关闭数据库连接
     * 如果本类用于scope为session的bean
     * 那么从这个bean取到connection的地方不该关闭这个connection
     * 如果关闭，那么下次用到还要重新打开。
     * 只有最后当前session要结束的时候才关闭它
     */
    public void closeConnection() throws YssException {
        try {
            if (conPub != null && !conPub.isClosed()) {
                conPub.close();
            }
        } catch (SQLException se) {
            throw new YssException("关闭数据库连接出错！", se);
        } finally {
            conPub = null;
        }
    }

    /**
     * 创建一个Statement，type为Type_Insensitive
     * @param updatable boolean：是否可更新
     * @throws SQLException
     * @throws YssException
     * @return Statement
     */
    public Statement openStatement(boolean updatable) throws SQLException,
        YssException {
        return loadConnection().createStatement( (dbType == YssCons.DB_DB2 &&
                                                  updatable ?
                                                  ResultSet.TYPE_FORWARD_ONLY :
                                                  ResultSet.
                                                  TYPE_SCROLL_INSENSITIVE),
                                                (updatable ?
                                                 ResultSet.CONCUR_UPDATABLE :
                                                 ResultSet.CONCUR_READ_ONLY));

    }

    /**
     * 用来产生读取数据性能更好的Statement，forward_only
     * @throws SQLException
     * @throws YssException
     * @return Statement
     */

    public Statement openStatement() throws SQLException, YssException {
        return loadConnection().createStatement();
    }

    public PreparedStatement openPreparedStatement(String sql) throws
        SQLException, YssException {
    	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
//    	log.debug(sql); modified by ysh 20111101
    	log.info(sql);
    	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
        return loadConnection().prepareStatement(sql);
    }

    /**
     * 用Sql语句，返回记录集
     * rsType和rsConcur，指定游标类型和是否可写，和创建statement的两个参数对应
     */
    public ResultSet openResultSet(String query, int rsType, int rsConcur) throws
        SQLException, YssException {
        //这里不截获SQLException原因是调用本函数的过程一定也会抛出SQLException，在那里一并捕获即可

        //这种方法打开resultset，关闭的时候要求result.getStatement().close();
        //这样能同时关闭statement，不过似乎直接关闭resultset也能可以但是不好
    	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
//    	log.debug(query); modified by ysh 20111101
    	
    	//20120312 modified by liubo.Story #2145
    	//在此需要加上try\catch，如果查询时出现错误，将进行查询的SQL语句写进errorlog中。
    	//===============================
    	try
    	{
	    	log.info(query);
	    	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
	        //System.out.println(query);
	        return (loadConnection().createStatement(rsType, rsConcur)).executeQuery(
	            query);
    	}
    	catch(Exception ye)
    	{
    		log.error("ERROR SQL:" + query);

			/**Start 20131223 added by liubo.Bug #85825.QDV4赢时胜(上海)2013年12月18日01_B
			 * 当当前用户被管理员中断连接时，因为YssPub对象很多变量都将是Null或者空值，比如说当前组合群代码。
			 * 这种时候查询大部分界面时都会报表或视图不存在的异常。
			 * 提出人认为这个异常可能不是很直观*/
    		if (!YssCons.YSS_Connection_Status)
    		{
    			throw new YssException("您的系统连接已被管理员中断，请重新登录或与管理员联系后再试！");
    		}
    		else
    		{
    			throw new YssException(ye.getMessage());
    		}
			/**End 20131223 added by liubo.Bug #85825.QDV4赢时胜(上海)2013年12月18日01_B*/
    	}
    	//============end===================
        /*以下这种方法不可行，因为每个statement只能同时打开一个resultset，
         每次打开另一个resultset，或者执行executeXXX，都会自动关闭当前打开的resultset
          if (staPub == null || staPub.getResultSetConcurrency()!=rsConcur ||
              staPub.getResultSetType()!=rsType) {
            if (staPub!=null) staPub.close();
            staPub = loadConnection().createStatement(rsType, rsConcur);
          }
          return staPub.executeQuery(query);*/
    }
   
    /**
     * 打开记录集，可以指定是否可写，固定采用type_scroll_insesitive<br>
     * 注意：如果要使用type_forward_only，那么请使用不带参数的openResultSet
     * @param query String：Sql语句
     * @param updatable boolean：是否可写，如果可写，自动指定concur_updatable
     * @throws SQLException
     * @throws YssException
     * @return ResultSet
     */
    public final ResultSet openResultSet(String query, boolean updatable) throws
        SQLException, YssException {
        return openResultSet(query,
                             (dbType == YssCons.DB_DB2 && updatable ?
                              ResultSet.TYPE_FORWARD_ONLY :
                              ResultSet.TYPE_SCROLL_INSENSITIVE),
                             (updatable ? ResultSet.CONCUR_UPDATABLE :
                              ResultSet.CONCUR_READ_ONLY));
//      return openResultSet(query,
//                          (ResultSet.TYPE_SCROLL_INSENSITIVE),ResultSet.CONCUR_UPDATABLE);

    }

    public final ResultSet openResultSet(String query, int rsType) throws
        SQLException, YssException {
        return openResultSet(query, rsType, ResultSet.CONCUR_READ_ONLY);
    }

//增加一种打开纪录集方式: ready_only 方式打开的纪录集不可以使用last()方法
    public final ResultSet openResultSet_antReadonly(String query) throws
        SQLException, YssException {
        return openResultSet(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
                             ResultSet.CONCUR_READ_ONLY);
    }

    public final ResultSet openResultSet(String query) throws SQLException,
        YssException {
        return openResultSet(query, ResultSet.TYPE_FORWARD_ONLY,
                             ResultSet.CONCUR_READ_ONLY);
    }
    /**
     * 分页的数据处理，是对openResultSet(String)方法的扩展
     * QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
     * 分页处理的POJO类@param yssPageInationBean
     * 返回结果集@return
     * @throws SQLException
     * @throws YssException
     */
    public final ResultSet openResultSet(YssPageInationBean pageInationBean) throws SQLException,
    YssException {
    	YssPageInation yssPageInation =new YssPageInation();
    	yssPageInation.setYssPub(pageInationBean.getYssPub());
    	yssPageInation.setPageInationBean(pageInationBean);
    	return yssPageInation.buildQuerySQL();
//    	openResultSet(yssPageInation.buildQuerySQL(), ResultSet.TYPE_FORWARD_ONLY,
//                ResultSet.CONCUR_READ_ONLY);
    }
    //add by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B
    public final ResultSet openResultSet_PageInation(YssPageInationBean yssPageInationBean) throws SQLException,
    YssException {
    	YssPageInation yssPageInation =new YssPageInation();
    	yssPageInation.setYssPub(yssPageInationBean.getYssPub());
    	yssPageInation.setPageInationBean(yssPageInationBean);
    	return openResultSet(yssPageInation.buildQuerySQL1(), ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
    }
    //--------------------
    
    //add by fangjiang 2010.11.16 bug 254
    public final ResultSet openResultSet_PageInation(YssPageInationBean yssPageInationBean,int begin,int end) throws SQLException,
    YssException {
    	YssPageInation yssPageInation =new YssPageInation();
    	yssPageInation.setYssPub(yssPageInationBean.getYssPub());
    	yssPageInation.setPageInationBean(yssPageInationBean);
    	return openResultSet(yssPageInation.buildQuerySQL1(begin,end), ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
    }
    //--------------------
    
    /**
     * 关闭记录集，用在finally里面，可以关闭多个
     * @param rs ResultSet
     */
    public final void closeResultSetFinal(ResultSet rs) {
        closeResultSetFinal(rs, null, null, null);
    }

    public final void closeResultSetFinal(ResultSet rs1, ResultSet rs2) {
        closeResultSetFinal(rs1, rs2, null, null);
    }

    public final void closeResultSetFinal(ResultSet rs1, ResultSet rs2,
                                          ResultSet rs3) {
        closeResultSetFinal(rs1, rs2, rs3, null);
    }

    /**
     * 关闭游标所占用的资源
     * 不但要关闭ResultSet对象以来的Statement,同时要关闭ResultSet对象本身
     * modify by sunkey 20090604s
     * @param rs1 ResultSet
     * @param rs2 ResultSet
     * @param rs3 ResultSet
     * @param rs4 ResultSet
     */
    public final void closeResultSetFinal(ResultSet rs1, ResultSet rs2,
                                          ResultSet rs3, ResultSet rs4) {
        if (rs1 != null) {
            try {
                rs1.getStatement().close();
                rs1.close();
            } catch (Exception e) {}
        }

        if (rs2 != null) {
            try {
                rs2.getStatement().close();
                rs2.close();
            } catch (Exception e) {}
        }

        if (rs3 != null) {
            try {
                rs3.getStatement().close();
                rs3.close();
            } catch (Exception e) {}
        }

        if (rs4 != null) {
            try {
                rs4.getStatement().close();
                rs4.close();
            } catch (Exception e) {}
        }
    }

    /**
     * 关闭游标，用在finally里面，可以关闭多个，也可以关闭preparedstatement等
     * @param st Statement
     */
    public final void closeStatementFinal(Statement st) {
        closeStatementFinal(st, null);
    }

    public final void closeStatementFinal(Statement st1, Statement st2) {
        if (st1 != null) {
            try {
                st1.close();
                st1 = null;//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420
            } catch (Exception e) {}
        }

        if (st2 != null) {
            try {
                st2.close();
                st2 = null;//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420
            } catch (Exception e) {}
        }
    }

    /** Programmer: Dranson
     * 事务结束后的处理
     * @param con Connection
     * @param bTrans boolean
     */
    public final void endTransFinal(Connection con, boolean bTrans) {
        try {
            if (bTrans) {
                con.rollback();
            }
        } catch (Exception e) {}
        try {
            con.setAutoCommit(true);
        } catch (Exception e) {}
    }

    public final void endTransFinal(boolean bTrans) {
        try {
            if (bTrans) {
                loadConnection().rollback();
            }
            loadConnection().setAutoCommit(true);
        } catch (Exception e) {}
    }

    /**
     * 此方法执行之后返回执行的行数。sj 20081222.
     * @param query String
     * @return int
     * @throws SQLException
     * @throws YssException
     */
    public int executeSqlwithReturnRows(String query) throws SQLException, YssException {
        Statement st = null;
        int executeRows = 0;
        try {
        	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
        	//log = Logger.getLogger("D");
//        	log.debug(query); modified by ysh 20111101
        	log.info(query);
        	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
            //System.out.println(query);
            
            st = loadConnection().createStatement();
            executeRows = st.executeUpdate(query);
            return executeRows;
        } catch (SQLException se) {
            throw se;
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (Exception e) {}
        }
    }

    /**执行update类SQL语句*/
    public void executeSql(String query) throws SQLException, YssException {
        Statement st = null;
        try {
        	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
        	//log = Logger.getLogger("D");
//        	log.debug(query);
        	log.info(query); // modified by ysh 20111101
        	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
            //System.out.println(query);
            
            st = loadConnection().createStatement();
            st.executeUpdate(query);
        } catch (SQLException se) {
        	//20120112 added by liubo.Story #1757
        	//执行语句报错时，捕捉发生错误的Sql语句，然后写进errorlog中
        	//===================================
			log.error(se.getMessage());
			log.error("ERROR SQL:" + query);
        	//===================================
            throw se;
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (Exception e) {}
        }
//      }catch (SQLException se) {
//         throw new YssException("执行SQL语句出错！",se);
//      }
    }

    /**
     * 判断记录集里有没有指定的字段
     * @param rs ResultSet
     * @param sField String
     * @throws SQLException
     * @return boolean：字段存在，就返回true
     */
    public boolean isFieldExist(ResultSet rs, String sField) throws SQLException {
        ResultSetMetaData rm = rs.getMetaData();
        int i, iCount = rm.getColumnCount();

        for (i = 1; i <= iCount; i++) {
            if (rm.getColumnName(i).equalsIgnoreCase(sField)) {
                break;
            }
        }

        return (i <= iCount);
    }

//各类数据库特定函数，内部使用****************************************************
//oracle特定函数

//sqlserver特定函数
//db2特定函数
//******************************************************************************

     /* 以下是管理访问SQL函数，各数据库差异的函数***************************************
      * 由于不同数据库其Sql函数不同，这里用函数封装了这种差异
      * 实际要调用这些sql函数，请使用这里的函数，它们返回适应当前数据库的调用语法
      */

     //注意：这里接受的字符串参数，如果是VB常量变量，一定要用''括住，因为对sql语句来说，它是字符串常数
     //     就像传给SQL语句一样

     /**星号指定所有字段，oracle JDBC不能产生可写resultset，请使用这个函数
      * 参数为表名*/
     public String sqlStar(String sTab) {
         if (dbType == YssCons.DB_ORA) {
             return sTab + ".*";
         }
         return "*";
     }

    /**1、instr功能*/
    public String sqlInstr(String sStr1, String sStr2, String sStart) {
        if (dbType == YssCons.DB_ORA) {
            return "instr(" + sStr1 + "," + sStr2 +
                (sStart.length() == 0 ? "" : ("," + sStart)) + ")";
        }

        if (dbType == YssCons.DB_SQL) {
            return "charindex(" + sStr2 + "," + sStr1 +
                (sStart.length() == 0 ? "" : ("," + sStart)) + ")";
        }

        if (dbType == YssCons.DB_DB2) {
            return "LOCATE(" + sStr2 + "," + sStr1 + ")"; //db2不支持sStar
        }
        return ""; //其它数据库在里加
    }

    public String sqlInstr(String sStr1, String sStr2) {
        return sqlInstr(sStr1, sStr2, "");
    }

    /**substr*/
    public String sqlSubStr(String sStr, String sStart, String sLen) {
        if (dbType == YssCons.DB_SQL) {
            return "SubString(" + sStr + "," + sStart + "," + sLen + ")";
        }
        //  lzp 20080122modify   加入判断  因为sStart有可能为0在DB2中就会报错  所以如果为0时候就该为1
        if (dbType == YssCons.DB_DB2) {
            if (sStart.equalsIgnoreCase("0")) {
                return "SUBSTR(" + sStr + "," +
                    String.valueOf( (Integer.parseInt(sStart) + 1)) + "," + sLen +
                    ")";
            } else {
                return "SUBSTR(" + sStr + "," +
                    String.valueOf(Integer.parseInt(sStart)) + "," + sLen + ")";
            }
        }
        return "SUBSTR(" + sStr + "," + sStart + "," + sLen + ")";
    }

    /**substr*/
    public String sqlSubStr(String sStr, String sStart) {
        if (dbType == YssCons.DB_SQL) {
            return "SubString(" + sStr + "," + sStart + ")";
        }

        return "SUBSTR(" + sStr + "," + sStart + ")";
    }

    /**left，第二个参数也可用整数*/
    public String sqlLeft(String sStr, String sLen) {
        if (dbType == YssCons.DB_ORA) {
            return "SubStr(" + sStr + ",1," + sLen + ")";
        }

        return "LEFT(" + sStr + "," + sLen + ")";
    }

    public String sqlLeft(String sStr, int iLen) {
        return sqlLeft(sStr, String.valueOf(iLen));
    }

    /**right，第二个参数也可用整数*/
    public String sqlRight(String sStr, String sLen) {
        //right 和 substr都不能支持slen=0的情况，所以用decode/case。。。
        if (dbType == YssCons.DB_ORA) {
            return "decode(" + sLen + ",0,'',substr(" + sStr + ",-(" + sLen +
                ")))";
        } else if (dbType == YssCons.DB_SQL) {
            return "right(" + sStr + "," + sLen + ")";
        }

        return "case " + sLen + " when 0 then '' else right(" + sStr + "," +
            sLen + ") end";
    }

    public String sqlRight(String sStr, int iLen) {
        return sqlRight(sStr, String.valueOf(iLen));
    }

    /**len*/
    public String sqlLen(String sStr) {
        if (dbType == YssCons.DB_SQL) {
            return "len(" + sStr + ")";
        }

        return "length(" + sStr + ")";
    }

    //2、oracle日期问题：（
    /**返回sql语句中的有效日期...
     * 字符串格式的日期接受时间，有时间参数的先用parseDate析成Date类型
     * */
    public String sqlDate(String sDate) {
        java.util.Date ddate = new java.util.Date();
        if (dbType == YssCons.DB_ORA) {
            if (!YssFun.isDate(sDate, ddate)) {
                return "";
            }

            return ("to_date('" + YssFun.formatDate(ddate, YssCons.YSS_DATEFORMAT) +
                    "','" + YssCons.YSS_DATEFORMAT + "')");
        }
        if (dbType == YssCons.DB_SQL) {
            return "'" + sDate + "'";
        }

        if (dbType == YssCons.DB_DB2) { //db2不能转换/分隔的日期
            return "DATE('" +
                (sDate.indexOf('/') > 0 ? sDate.replace('/', '-') : sDate) +
                "')";
        }
        return "";
    }

    /**
     * 返回适用于sql语句的指定格式的日期字符串，用于字符串类型的日期字段
     * @param sDate String：日期字段，字符串类型
     * @param sFormat String：格式/默认为yyyy/MM/dd
     * @return String
     */
    public String sqlDateS(String sDate, String sFormat) {
        if (dbType == YssCons.DB_ORA) {
            return "to_date(" + sDate + ",'" + sFormat + "')";
        }
        if (dbType == YssCons.DB_SQL) {
            return "cast(" + sDate + " as datetime)";
        }

        if (dbType == YssCons.DB_DB2) {
            return (sFormat.indexOf(':') > 0 ? "TIMESTAMP" : "DATE") + "(" +
                (sFormat.indexOf('/') > 0 ? "replace(" + sDate + ",'/','-')" :
                 sDate) + ")";
        }

        return "";
    }

    public String sqlDateS(String sDate) {
        return sqlDateS(sDate, "yyyy/MM/dd");
    }

    //SQL语句中返回格式化字日期格式,目前仅支持Oracle
    public String sqlFormat(String sDate, String format) {
        if (dbType == YssCons.DB_ORA) {
            return "to_char(" + sDate + ",'" + format + "')";
        }
        if (dbType == YssCons.DB_DB2) { //2007.12.03 添加 蒋锦
            return "Char(Date(" + sDate + "))";
        } else {
            return sDate;
        }
    }

    public String sqlString(String str) {
    	//add by zhangfa 20101014 MS01843    凭证生成方案设置增删改点击确定按钮均报错    QDV4赢时胜(测试)2010年10月12日01_B    
    	if(str==null) return "'" +" "+ "'";
    	//---------------------MS01843----------------------------------------------------------------------------
        return "'" + str.replaceAll("'", "''") + "'";
    }

    public int sqlBoolean(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    /**日期类型参数的sqlDate...btime=true也保存时间*/
    public String sqlDate(java.util.Date dDate, boolean bTime) {
        String dformat = YssCons.YSS_DATEFORMAT + (bTime ? " HH:mm:ss" : "");

        if (dbType == YssCons.DB_ORA) {
            return ("to_date('" + YssFun.formatDate(dDate, dformat) + "','" +
                    YssCons.YSS_DATEFORMAT + (bTime ? "hh24:mi:ss" : "") + "')");
        }

        if (dbType == YssCons.DB_SQL) {
            return "'" + YssFun.formatDate(dDate, dformat) + "'";
        }

        if (dbType == YssCons.DB_DB2) {
            return (bTime ? "TIMESTAMP" : "DATE") + "('" +
                YssFun.formatDate(dDate, dformat) + "')";
        }

        return "";
    }

    public String sqlDate(java.util.Date dDate) {
        return sqlDate(dDate, false);
    }

    /**
     * 日期加减的单位，DB2需要 ＋ 1 days，其他一般都是 +1就可以
     * @return String：如果DB2，返回 " days "
     */
    public String sqlDateAddUnit() {
        if (dbType == YssCons.DB_DB2) {
            return " days ";
        }
        return "";
    }

    /**
     * 日期加减天数
     * @param sDate String：日期，如果是字符串字段，则要调用函数转换成sql日期
     * @param sDays String：天数
     * @return String
     */
    public String sqlDateAdd(String sDate, String sDays) {
        if (dbType == YssCons.DB_DB2) {
            return sDate + " " + sDays + " days ";
        }
        return sDate + " " + sDays;
    }

    /**
     * 将日期字段加上指定天数
     * 添加日期：2007-12-02
     * 蒋锦
     * @param sDateFiled String: 需要计算的日期型字段
     * @param sDays String: 要加的天数
     * @return String
     */
    public String SqlDateFiledAdd(String sDateFiled, String sDays) {
        if (dbType == YssCons.DB_DB2) {
            return "Date(Days(" + sDateFiled + ") + " + sDays + ")";
        }
        return sDateFiled + "+" + sDays;
    }

    /**
     * 将日期字段减去指定天数
     * 添加日期：2007-12-02
     * 蒋锦
     * @param sDateFiled String: 需要计算的日期型字段
     * @param sDays String: 要减的天数
     * @return String
     */
    public String sqlDateFiledSub(String sDateFiled, String sDays) {
        if (dbType == YssCons.DB_DB2) {
            return "Date(Days(" + sDateFiled + ") - " + sDays + ")";
        }
        return sDateFiled + "-" + sDays;
    }

    /**
     * 日期计算
     * @param sDate String:日期字段
     * @param sNumber String:日期差值,为数字
     * @param dateType String:计算日期段数(值分别为：Year,month,day),Year表示计算年份,month表示,day表示计算天数
     * @return String
     */
    public String sqlDays(String sDate, String sNumber, String dateType) {
        if (dbType == YssCons.DB_DB2) {
            if (dateType.equalsIgnoreCase("Year")) {
                return " " + sDate + " + " + sNumber + " years ";
            } else if (dateType.equalsIgnoreCase("Month")) {
                return " " + sDate + " + " + sNumber + " months ";
            } else if (dateType.equalsIgnoreCase("day")) {
                return " " + sDate + " + " + sNumber + " days ";
            }

        } else if (dbType == YssCons.DB_SQL) {
            return " dateadd(" + dateType + "," + sNumber + "," + sDate + " ) ";
        } else if (dbType == YssCons.DB_ORA) {
            if (dateType.equalsIgnoreCase("day")) {
                return " " + sDate + " + " + sNumber + " ";
            } else if (dateType.equalsIgnoreCase("month")) {
                return "add_months(" + sDate + "," + sNumber + ")";
            } else if (dateType.equalsIgnoreCase("year")) {
                return "add_months(" + sDate + "," + sNumber + "*12)";
            }
        }
        return " ";
    }

    /**
     * 日期相减
     * @param sDate1 String：日期1，如果是字符串字段，则要调用函数转换成sql日期
     * @param sDate2 String：日期同上
     * @return String：返回date2-date1的sql表达式
     */
    public String sqlDateDiff(String sDate1, String sDate2) {
        if (dbType == YssCons.DB_DB2) {
            return "days(" + sDate1 + ")-days(" + sDate2 + ")";
        }
        return sDate1 + "-" + sDate2;
    }

    /**
     * oracle没有从日期字段直接获取year,month,day的函数
     * 输入参数：日期字段或表达式
     */
    public String sqlYear(String sDate) {
        if (dbType == YssCons.DB_ORA) {
            return "to_number(to_char(" + sDate + "," + "'yyyy'))";
        }

        return "YEAR(" + sDate + ")";
    }

    /**sqlMonth*/
    public String sqlMonth(String sDate) {
        if (dbType == YssCons.DB_ORA) {
            return "to_number(to_char(" + sDate + "," + "'mm'))";
        }

        return "month(" + sDate + ")";
    }

    /**sqlDay*/
    public String sqlDay(String sDate) {
        if (dbType == YssCons.DB_ORA) {
            return "to_number(to_char(" + sDate + "," + "'dd'))";
        }

        return "day(" + sDate + ")";
    }

    /**sqlTrim*/
    public String sqlTrim(String sStr) {

        if (dbType == YssCons.DB_ORA) {
            return "Trim(" + sStr + ")";
        } else {
            return "RTrim(LTrim(" + sStr + "))";
        }
    }

    public String sqlTrimNull(String sStr) {
        if (dbType == YssCons.DB_SQL) {
            return "len(" + sqlTrim(sStr) + ") = 0";
        } else if (dbType == YssCons.DB_DB2) {
            return "length(" + sqlTrim(sStr) + ") = 0";
        } else {
            return "trim(" + sStr + ") is null";
        }
    }

    /**
     * 参看Oracle的NVL(Sqlserver的isnull)函数
     */
    public String sqlIsNull(String str1, String str2) {
        if (dbType == YssCons.DB_ORA) {
            return "NVL(" + str1 + "," + str2 + ")";
        }

        if (dbType == YssCons.DB_SQL) {
            return "IsNull(" + str1 + "," + str2 + ")";
        }

        if (dbType == YssCons.DB_DB2) {
            return "value(" + str1 + "," + str2 + ")";
        }

        return "(case when " + str1 + " is null then " + str2 + " else " + str1 +
            " end)";
    }

    public String sqlIsNull(String str1) {
        return sqlIsNull(str1, "0");
    }

    /**
     '整除和取模,sstr1和sstr2是整数表达式
     */
    public String sqlIntDiv(String sStr1, String sStr2, boolean bAbs) {
        if (dbType == YssCons.DB_ORA) {
            return "floor(abs(" + sStr1 + ")/abs(" + sStr2 + ")) "
                + (bAbs ? "" : ("* sign(" + sStr1 + ") * sign(" + sStr2 + ")"));
        }

        return (bAbs ? "abs(" : "") + "(" + sStr1 + ") / (" + sStr2 + ")" +
            (bAbs ? ")" : "");
    }

    /**
     * 取日期年、月、日
     * @param cloumnname String
     * @param i int   取年份时i从1开始
     * @param j int
     * @return String
     */
    public String sqlSubstr(String cloumn_date, int i, int j) {
        String ss = "";
        if (dbType == YssCons.DB_ORA) { //ora 中取年份时可以从0-4 也可以从1-4
            ss = "subStr(to_char(" + cloumn_date + ",'yyyy-mm-dd')," + i + "," + j +
                ")";
        } else if (dbType == YssCons.DB_DB2) { //db2 中只能从1-4
            ss = "subStr(char(" + cloumn_date + ")," + i + "," + j + ")";
        } else {
            ss = "SUBSTRING(CONVERT(VARCHAR(10)," + cloumn_date + ",120)," + i +
                "," + j + ")";
        }
        return ss;
    }

    public String sqlIntDiv(String sStr1, String sStr2) {
        return sqlIntDiv(sStr1, sStr2, false);
    }

    /**取模*/
    public String sqlMod(String sStr1, String sStr2, boolean bAbs) {
        if (dbType == YssCons.DB_ORA) {
            return (bAbs ? "abs(" : "") + "mod(" + sStr1 + "," + sStr2 + ")" +
                (bAbs ? ")" : "");
        }

        if (dbType == YssCons.DB_SQL) {

            //return (bAbs ? "abs(" : "") + "(" + sStr1 + ") % (" + sStr2 + ") + (bAbs ? ")" : "");
            return (bAbs ? "abs(" : "") + "(" + sStr1 + ") % (" + sStr2 + ")" +
                (bAbs ? ")" : "");
        }

        return (bAbs ? "abs(" : "") + "mod(" + sStr1 + ", " + sStr2 + ")" +
            (bAbs ? ")" : "");
    }

    public String sqlMod(String sStr1, String sStr2) {
        return sqlMod(sStr1, sStr2, false);
    }

    /**   字符串连接字符*/
    public String sqlJN() {
        if (dbType == YssCons.DB_SQL) {
            return " + ";
        }

        return " || ";
    }

    /**   字符串连接字符*/
    public String sqlJoinString() {
        return sqlJN();
    }

    /**sqlCastType: 用于返回不同数据库类型下的数据类型表示，传入参数是sqlserver格式
     *主要用于cast表达式，目前sql/oracle的cast语法只有as 后面类型不同
     *目前stype支持money和nvarchar，注意不要填nvarchar后面的(20)之类的
     */
    public String sqlCastType(String sType) {
        sType = sType.toLowerCase().trim();

        if (dbType == YssCons.DB_ORA) {
            if (sType.equals("money")) {
                return "number(19,4)";
            }
            if (sType.equals("nvarchar")) {
                return "varchar2";
            }

            return sType;
        }

        if (dbType == YssCons.DB_DB2) {
            if (sType.equals("money")) {
                return "decimal(19,4)";
            }
            if (sType.equals("nvarchar")) {
                return "varchar";
            }

            return sType;
        }

        if (dbType == YssCons.DB_SQL) {
            return sType;
        }

        return "";
    }

    /**
     *  'case when/sql,,,,decode/oracle
     *  '因为oracle的case a when b ...语句对于nvarchar不能用，所以用decode
     * sAllParam的格式为，第一个元素是要比较的表达式，以后两个一组是值和结果，
     * 最后如果有单个的就是else对应的值。每个元素间用逗号间隔，暂时不支持带逗号的表达式
     */
    public String sqlCase(String sAllParam) {
        String[] sarr = null;
        StringBuffer stmp = new StringBuffer(99);
        int lloop;

        if (sAllParam.length() == 0) {
            return "";
        }

        if (dbType == YssCons.DB_ORA) {
            return "decode(" + sAllParam + ")";
        }

        else {
            sarr = splitParam(sAllParam);
            //第一个元素是case的对象，后面对是条件和值，for处理这些 条件/值
            //如果有else，最后就是else内容，在for结束后处理
            for (lloop = 1; lloop <= ( (sarr.length - 1) / 2) * 2; lloop++) {
                stmp.append( ( (lloop % 2) == 0 ? " then " : " when ") + sarr[lloop]);
            }
            //有else
            if (sarr.length - 1 == lloop) {
                stmp.append(" else " + sarr[lloop]);
            }
            return " case " + sarr[0] + stmp.toString() + " end";
        }

    }

    /**
     * 用户group by 后面的cube
     * @param sCube String：应该是"(s1,s2)"这样的形式
     * @return String：对ora："cube(s1,s2)" 对sqlserver："(s1,s2) with cube"
     */
    public String sqlCube(String sCube) {
        if (dbType == YssCons.DB_SQL) {
            return " " + sCube + " with cube";
        }

        return " cube" + sCube + " ";
    }

    public String sqlToNumber(String sStr) {
        String sResult = "";
        if (dbType == YssCons.DB_ORA) {
            sResult = "to_number(" + sStr + ")";
        }
        return sResult;
    }

    /**
     * 如果使用的是DB2数据库，将 Decimal 转换为 Char，否则返回原字符串
     * 添加日期：2007-11-28
     * 蒋锦
     * @param sStr String
     * @return String
     */
    public String sqlToChar(String sStr) {
        String SResult = sStr;
        if (dbType == YssCons.DB_DB2) {
            SResult = "Digits(" + sStr + ")";
        }
        return SResult;
    }

    //分开用逗号间隔的参数列表，各参数本身可包含函数及其逗号间隔的参数
    private String[] splitParam(String sParam) {
        int ltmp = 0, lpair = 0;
        int lloop = 0;
        int llen = 0;
        char ctmp;
        Vector vect = new Vector(5, 2); //其实也可用stringbuffer实现，但试vector：P
        String[] sarr = null; //特意从vector转成array返回:P

        //分析括号，如果左右括号数目差不等于0，那么即使碰到逗号，也不能隔断
        for (; lloop < sParam.length(); lloop++) {
            ctmp = sParam.charAt(lloop);
            if (ctmp == '(') {
                lpair++;
            }
            if (ctmp == ')') {
                lpair--;

            }
            if (ctmp == ',' && lpair == 0) {
                vect.add(sParam.substring(ltmp, lloop));
                ltmp = lloop + 1; //下一个起始点
                llen++;
            }
        }
        vect.add(sParam.substring(ltmp, lloop));
        sarr = new String[vect.size()];
        vect.copyInto(sarr);
        return sarr;
    }

    /**
     * add by songjie 2011.07.18
     * BUG 2274 QDV4建信2011年7月14日01_B
     * 判断序列是否存在,只适用于ORACLE
     * @param sequence
     * @return
     * @throws YssException
     */
    public boolean yssSequenceExist(String sequence) throws YssException {
        boolean ret = false;
        ResultSet rsTab = null;
        String sql = null;
        sql = " select sequence_name from user_sequences where sequence_name='" + 
        sequence.toUpperCase() + "'";
        try {
            rsTab = openResultSet(sql, false);
            if (rsTab.next()) {
                ret = true;
            }
            rsTab.getStatement().close();
            return ret;
        } catch (SQLException se) {
            try {
                if (rsTab != null) {
                    rsTab.getStatement().close();
                }
            } catch (SQLException sse) {}
            throw new YssException("访问数据库出错！\n ERROR SQL:" + sql, se);
        } finally
        {
        	this.closeResultSetFinal(rsTab);
        }
    }
    
	// start dongqingsong  2013-07-08 从主流版本和合并到分支版本的修改字段的方法
	    /**
     * 修改表字段类型，ORA与DB2的语法有区别
     * @param sTab String  表名
     * @param sField String  字段名
     * @param sDataType String  更新后的字段类型
     * @return String  修改表字段的SQL语句 
     * hukun 20130521
     */
    public String sqlAlterFieldModify(String sTab, String sField, String sDataType){
        String SResult;
        if (dbType == YssCons.DB_DB2) {
            SResult = "alter table " + sTab + " alter " + sField + " set data type " + sDataType;
        }else{
        	SResult = "alter table " + sTab + " modify " + sField + " " + sDataType;
        }
        return SResult;
    }
	// end dongqingsong  2013-07-08 从主流版本和合并到分支版本的修改字段的方法
    
    public void createSequence(String tabName) throws YssException{
    	
    	StringBuffer seqBuf = new StringBuffer();
    	int istartNo =1;//Seq起始值
    	try{
    		
    		istartNo = getStartSeqNo(tabName);
    		seqBuf.append("create sequence SEQ_SYS_LOG minvalue 1 maxvalue 9999999999999999999 start with ").append(istartNo);
    		seqBuf.append(" increment by 1 cache 20 order ");

    		this.executeSql(seqBuf.toString());
    	}catch(Exception e){
    		throw new YssException("创建Sequence 出错......");
    	}
    }
    
    /**
     *  获取表中最大的编号
     * @param tabName
     * @return
     * @throws YssException
     */
    private int getStartSeqNo(String tabName) throws YssException {
    	StringBuffer sqlBuf = new StringBuffer();
    	int istartNo =1;//Seq起始值
    	ResultSet rs = null;
    	try{
    		
    		sqlBuf.append(" select to_number(nvl(max(flogcode),1)) as fstartcode from  ").append(tabName);

    		rs = this.openResultSet(sqlBuf.toString());
    		while(rs.next()){
    			istartNo = rs.getInt("fstartcode");
    		}
    		
    		return istartNo;
    	}catch(Exception e){
    		throw new YssException("创建Sequence 出错......");
    	}finally{
    		this.closeResultSetFinal(rs);
    	}
    }
    
    /**
     * 查询指定的数据表是否存在
     * @param sTable String：表名
     * @return boolean：存在返回true
     */
    public boolean yssTableExist(String sTable) throws YssException {
        return tableExist(sTable, false);
    }
    
    public boolean yssProcedureExist(String sProcedure) throws YssException
    {
    	boolean ret = false;
    	ResultSet rsTab = null;
        String sql = " select * from user_objects where object_name = upper('" + sProcedure + "') and OBJECT_TYPE = 'PROCEDURE'";
        try {
            rsTab = openResultSet(sql, false);

            if (rsTab.next()) {
                ret = true;

            }
            rsTab.getStatement().close();
            return ret;
        } catch (SQLException se) {
            try {
                if (rsTab != null) {
                    rsTab.getStatement().close();
                }
            } catch (SQLException sse) {}
            throw new YssException("访问数据库出错！\n ERROR SQL:" + sql, se);
        } finally 
        {	
        	this.closeResultSetFinal(rsTab);
        }
    }

    /**
     * 查询指定的视图是否存在
     * @param sView String：视图名
     * @throws YssException
     * @return boolean：存在返回true
     */
    public boolean yssViewExist(String sView) throws YssException {
        return tableExist(sView, true);
    }

    //具体实现表和视图的存在判断
    private boolean tableExist(String sTable, boolean bView) throws YssException {
        boolean ret = false;
        ResultSet rsTab = null;
        String sql = null;

        if (dbType == YssCons.DB_ORA) {
            sql = (bView ? "select view_name from user_views where view_name='" :
                   "select table_name from user_tables where table_name='") +
                sTable.toUpperCase() + "'";
        } else if (dbType == YssCons.DB_SQL) {
            sql = "select top 1 name from sysobjects where name='" + sTable +
                "' and type=" + (bView ? "'V'" : "'U'");
        } else if (dbType == YssCons.DB_DB2) {
            sql = (bView ? "select name from sysibm.sysviews" :
                   "select name from sysibm.systables") + " where creator='" +
                strDBUser.toUpperCase() + "' and name='" +
                sTable.toUpperCase() + "'";
        } else {
            return false;
        }
        //从数据库查找是否存在指定的表
        try {
            rsTab = openResultSet(sql, false);

            if (rsTab.next()) {
                ret = true;

            }
            rsTab.getStatement().close();
            return ret;
        } catch (SQLException se) {
            try {
                if (rsTab != null) {
                    rsTab.getStatement().close();
                }
            } catch (SQLException sse) {}
            throw new YssException("访问数据库出错！\n ERROR SQL:" + sql, se);
        } finally 
        {	
        	this.closeResultSetFinal(rsTab);
        }

        /*老算法，有问题，部分资源不释放      DatabaseMetaData md = null;
              md = loadConnection().getMetaData();
              return (md.getTables(null, null, sTable.toUpperCase(),
                                   new String[] {"TABLE"})).next();*/
    }

    /**
     * 判断记录集里有没有指定的字段
     * @param sTable
     * @throws SQLException
     * @return String：返回主键名称
     */
    public String getConstaintKey(String sTable) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sKey = "";
        //从数据库查找是否存在指定的表
        try {
            if (dbType == YssCons.DB_ORA) {
                strSql = "SELECT index_name as name FROM user_indexes WHERE table_name = upper('" +
                    sTable + "') and table_type ='TABLE'";
            } else if (dbType == YssCons.DB_SQL) {
                strSql = "SELECT * FROM sysobjects WHERE ( Xtype ='PK') and parent_obj" +
                    " = (select id  from  sysobjects where  name = '" + sTable + "')";
            } else if (dbType == YssCons.DB_DB2) {
                strSql = "select constraint_name as name from sysibm.table_constraints where TABLE_NAME='" +
                    sTable + "'";
            }
            rs = openResultSet(strSql, false);
            if (rs.next()) {
                sKey = rs.getString("name");
            }
            return sKey;
        } catch (SQLException se) {
            throw new YssException("访问数据库出错！\n ERROR SQL:" + strSql, se);
        } finally {
            closeResultSetFinal(rs);
        }
    }

    /**
     * 判断记录集里有没有指定的字段
     * @param sTable
     * @throws SQLException
     * @return String：返回主键名称
     */
    public String getTableByConstaintKey(String sKey) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sTable = "";
        //从数据库查找是否存在指定的表
        try {
            if (dbType == YssCons.DB_ORA) {
                strSql = "SELECT table_name as name FROM user_indexes WHERE index_name = upper('" +
                    sKey + "') and table_type ='TABLE'";
            } else if (dbType == YssCons.DB_SQL) {
                strSql = "SELECT * FROM sysobjects WHERE ( Xtype ='PK') and parent_obj" +
                    " = (select id  from  sysobjects where  name = '" + sKey + "')";
            } else if (dbType == YssCons.DB_DB2) {
                strSql = "select table_name as name from sysibm.table_constraints where constraint_name='" +
                    sKey + "'";
            }
            rs = openResultSet(strSql, false);
            if (rs.next()) {
                sTable = rs.getString("name");
            }
            return sTable;
        } catch (SQLException se) {
            throw new YssException("访问数据库出错！\n + ERROR SQL:" + strSql, se);
        } finally {
            closeResultSetFinal(rs);
        }
    }

    /**
     * 查询指定的表格是否为空
     * @param sView String：视图名
     * @throws YssException
     * @return boolean：存在返回true
     */
    public boolean yssTableNull(String sView) throws YssException {
        return tableNull(sView);
    }

    //具体实现表和视图的存在判断
    private boolean tableNull(String sTable) throws YssException {
        boolean ret = false;
        ResultSet rsTab = null;
        String sql = null;

        if (dbType == YssCons.DB_ORA) {

            sql = "select * from " + sTable;
        } else {
            return false;
        }
        //从数据库查找是否存在指定的表
        try {
            rsTab = openResultSet(sql, false);
            if (rsTab.next()) {
                ret = true;

            }
            rsTab.getStatement().close();
            return ret;
        } catch (SQLException se) {
            try {
                if (rsTab != null) {
                    rsTab.getStatement().close();
                }
            } catch (SQLException sse) {}
            throw new YssException("访问数据库出错！\n ERROR SQL:" + sql, se);
        } finally
        {
        	this.closeResultSetFinal(rsTab);
        }
    }

    public String clobStrValue(java.sql.Clob clob) throws YssException {
        String sResult = "";
        BufferedReader in = null;
        StringBuffer buf = null;
        String str = "";
        int i = 0;
        try {
            if (clob == null) {
                return "";
            }
            if (dbType == YssCons.DB_ORA) {
            	//20120716 deleted by liubo.Bug #4893.Websphere7.0版本操作clob出错
            	//==================================
//                oracle.sql.CLOB clb = (oracle.sql.CLOB) clob;
            	//=================end=================
                in = new BufferedReader(clob.getCharacterStream());
                buf = new StringBuffer();
                while (str != null) {
                    str = in.readLine();
                    if (str != null) {
                        //2008.05.12 蒋锦 添加 "\r\n"
                        buf.append(str).append("\r\n");
                    }
                    i++;
                }
            }
            if (dbType == YssCons.DB_DB2) {
                in = new BufferedReader(clob.getCharacterStream());
                buf = new StringBuffer();
                while (str != null) {
                    str = in.readLine();
                    if (str != null) {
                        //2008.05.12 蒋锦 添加 "\r\n"
                        buf.append(str).append("\r\n");
                    }
                    i++;
                }
            }
            sResult = buf.toString();
            //2008.05.12 蒋锦 添加 去掉末尾的 \r\n
            if (sResult.length() != 0) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * 2009.04.27 蒋锦 添加
     * MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
     * 添加独占锁，当执行 commit 或 rollback 自动解除
     * @param sTableName String：表名
     * @throws YssException
     */
    public void lockTableInEXCLUSIVE(String sTableName) throws YssException {
        String sqlStr = "";
        try {
        	//---edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        	if(sTableName.toUpperCase().indexOf("DATA_CASHPAYREC") == -1 &&
        	   sTableName.toUpperCase().indexOf("DATA_INTEGRATED") == -1 &&
        	   sTableName.toUpperCase().indexOf("DATA_SECRECPAY") == -1 &&
        	   sTableName.toUpperCase().indexOf("CASH_TRANSFER") == -1 &&
        	   sTableName.toUpperCase().indexOf("CASH_SUBTRANSFER") == -1 &&
        	   sTableName.toUpperCase().indexOf("DATA_INVESTPAYREC") == -1 &&
        	   sTableName.toUpperCase().indexOf("VCH_DATA") == -1 &&
        	   sTableName.toUpperCase().indexOf("Vch_DATAENTITY") == -1){
                sqlStr = "LOCK TABLE " + sTableName + " IN EXCLUSIVE MODE";
                executeSql(sqlStr);
        	}
        	//---edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        } catch (Exception ex) {
            throw new YssException("表：" + sTableName + " 添加独占锁失败", ex);
        }
    }

	/**
	 * 为表添加行级共享锁模式,共享表级 by leeyu 20100603 系统优化 合并太平版本代码
	 * 
	 * @param sTableName
	 * @throws YssException
	 */
	public void lockTableInRowShareMode(String sTableName) throws YssException {
		String sqlStr = "";
		try {
			if (dbType == YssCons.DB_ORA) {
				sqlStr = "LOCK TABLE " + sTableName + " IN Row SHARE MODE";
				executeSql(sqlStr);
			}
		} catch (Exception ex) {
			throw new YssException("表：" + sTableName + " 添加行级共享锁失败", ex);
		}
	}

	/**
	 * 为表添加共享锁模式,可阻止其他DML语句 by leeyu 20100603 系统优化 合并太平版本代码
	 * 
	 * @param sTableName
	 * @throws YssException
	 */
	public void lockTableInShareMode(String sTableName) throws YssException {
		String sqlStr = "";
		try {
			if (dbType == YssCons.DB_ORA) {
				sqlStr = "LOCK TABLE " + sTableName + " IN SHARE MODE";
				executeSql(sqlStr);
			}
		} catch (Exception ex) {
			throw new YssException("表：" + sTableName + " 添加共享锁失败", ex);
		}
	}

	/**
	 * 为表添加共享行级独占锁模式,可阻止其他事务操作 by leeyu 20100603 系统优化 合并太平版本代码
	 * 
	 * @param sTableName
	 * @throws YssException
	 */
	public void lockTableInShareRowExclusiveMode(String sTableName)
			throws YssException {
		String sqlStr = "";
		try {
			if (dbType == YssCons.DB_ORA) {
				sqlStr = "LOCK TABLE " + sTableName
						+ " IN Share Row Exclusive MODE";
				executeSql(sqlStr);
			}
		} catch (Exception ex) {
			throw new YssException("表：" + sTableName + " 添加共享行级独占锁失败", ex);
		}
	}

	/**
	 * 为表添加行级独占锁模式,修改行数据 by leeyu 20100603 系统优化 合并太平版本代码
	 * 
	 * @param sTableName
	 * @throws YssException
	 */
	public void lockTableInRowExclusiveMode(String sTableName)
			throws YssException {
		String sqlStr = "";
		try {
			if (dbType == YssCons.DB_ORA) {
				sqlStr = "LOCK TABLE " + sTableName + " IN Row Exclusive MODE";
				executeSql(sqlStr);
			}
		} catch (Exception ex) {
			throw new YssException("表：" + sTableName + " 添加行级独占锁失败", ex);
		}
	}

	/**
	 * 为表添加独占锁模式,此模式为DDL语句时 by leeyu 20100603 系统优化 合并太平版本代码
	 * 
	 * @param sTableName
	 * @throws YssException
	 */
	public void lockTableInExclusiveMode(String sTableName) throws YssException {
		String sqlStr = "";
		try {
			if (dbType == YssCons.DB_ORA) {
				sqlStr = "LOCK TABLE " + sTableName + " IN Exclusive MODE";
				executeSql(sqlStr);
			}
		} catch (Exception ex) {
			throw new YssException("表：" + sTableName + " 添加独占锁失败", ex);
		}
	}
    /**
     * 查询旧表中是否有索引
     * @param sKey String 旧表的主键
     * @return String 旧表的索引名
     * @throws YssException 异常
     * xuqiji 20090512:QDV4赢时胜（上海）2009年4月7日01_A   MS00352    新建组合群时能够自动创建对应的一套表
     */
    public String getTableIndexKey(String sKey) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String index = "";
        //从数据库查找是否存在指定的表
        try {
            if (dbType == YssCons.DB_ORA) {
                strSql = "SELECT index_name  FROM user_indexes WHERE index_name = upper('" +
                    sKey + "') and table_type ='TABLE'";
            } else if (dbType == YssCons.DB_SQL) {
                strSql = "SELECT * FROM sysobjects WHERE ( Xtype ='PK') and parent_obj" +
                    " = (select id  from  sysobjects where  name = '" + sKey + "')";
            } else if (dbType == YssCons.DB_DB2) {
                strSql = "select index_name  from sysibm.table_constraints where constraint_name='" +
                    sKey + "'";
            }
            rs = openResultSet(strSql, false);
            if (rs.next()) {
                index = rs.getString("index_name");
            }
            return index;
        } catch (SQLException se) {
            throw new YssException("访问数据库出错！\n ERROR SQL:" + strSql, se);
        } finally {
            closeResultSetFinal(rs);
        }
    }
    //-------------------------------end------------------------------------------------------------------------------//
    
    /**获取表中字段的类型等信息,返回游标,shashijie 2011.05.30
     * @param 表名 
     * @param 字段名 */
    public ResultSet getUserTabColumns(String table , String columns) throws YssException {
        ResultSet rs = null;
        String strSql = "SELECT * from user_tab_columns WHERE table_name = "+sqlString(table.toUpperCase())+
        				" AND column_name = "+sqlString(columns.toUpperCase());
        try {
            rs = openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (rs.next()) {
            	return rs;
			}
        } catch (SQLException se) {
        	closeResultSetFinal(rs);
            throw new YssException("访问数据库出错！\n ERROR SQL:" + strSql, se);
        } finally {
        	
        }
        return rs;
    }
    
    /**获取表中主键等信息, shashijie ,2011-5-30
     * @param table表名
     * @param constraint主键名(传空则查处所有)
     * @return ResultSet
     */
    public ResultSet getUserConsColumns(String table,String constraint) throws YssException{
    	ResultSet rs = null;
        String strSql = "select cu.* "+
				  "from user_cons_columns cu, user_constraints au "+
				 " where cu.constraint_name = au.constraint_name "+
				 " and au.constraint_type = "+sqlString("P")+
				 " and au.table_name = "+sqlString(table.toUpperCase());
        if (constraint!=null && !constraint.trim().equals("")) {
			strSql += " and au.constraint_name = "+sqlString(constraint.toUpperCase());
		}
        try {
            rs = openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);
        } catch (SQLException se) {
        	closeResultSetFinal(rs);
            throw new YssException("访问数据库出错！\n ERROR SQL:" + strSql, se);
        } finally {
        	
        }
        return rs;
    }
    
    
    /**************************************************************************************
     * STORY #788 大部分语句未使用绑定变量，导致数据库不断对SQL语句进行硬解析，处理能力低下
     * @author jiangshichao
     * @date   2011.08.02
     */
    public ResultSet queryByPreparedStatement(String sql) throws YssException{
    	ResultSet rs = null;
    	PreparedStatement pst = null;
    	try{
    		pst = getPreparedStatement(sql);
        	rs = pst.executeQuery();
        	return rs;
    	}catch(Exception e){
    		//20120312 added by liubo.Story #2145.
	    	//如果查询时出现错误，需要将进行查询的SQL语句写进errorlog中。
    		//=============================
    		log.error("ERROR SQL:" + sql);
    		//==============end===============

			/**Start 20131223 added by liubo.Bug #85825.QDV4赢时胜(上海)2013年12月18日01_B
			 * 当当前用户被管理员中断连接时，因为YssPub对象很多变量都将是Null或者空值，比如说当前组合群代码。
			 * 这种时候查询大部分界面时都会报表或视图不存在的异常。
			 * 提出人认为这个异常可能不是很直观*/
    		if (!YssCons.YSS_Connection_Status)
    		{
    			throw new YssException("您的系统连接已被管理员中断，请重新登录或与管理员联系后再试！");
    		}
    		else
    		{
        		throw new YssException(e.getMessage() + "\n ERROR SQL:" +sql);
    		}
			/**End 20131223 added by liubo.Bug #85825.QDV4赢时胜(上海)2013年12月18日01_B*/
    	}
    } 
    
    public ResultSet queryByPreparedStatement(String sql,int resultSetType,int resultSetConcurrency) throws YssException{
    	ResultSet rs = null;
    	PreparedStatement pst = null;
    	try{
    		pst = getPreparedStatement(sql,resultSetType,resultSetConcurrency);
    		rs = pst.executeQuery();;
        	return rs;
    	}catch(Exception e){
    		//20120312 added by liubo.Story #2145.
	    	//如果查询时出现错误，需要将进行查询的SQL语句写进errorlog中。
    		//=============================
    		log.error("ERROR SQL:" + sql);
    		//===============end==============

			/**Start 20131223 added by liubo.Bug #85825.QDV4赢时胜(上海)2013年12月18日01_B
			 * 当当前用户被管理员中断连接时，因为YssPub对象很多变量都将是Null或者空值，比如说当前组合群代码。
			 * 这种时候查询大部分界面时都会报表或视图不存在的异常。
			 * 提出人认为这个异常可能不是很直观*/
    		if (!YssCons.YSS_Connection_Status)
    		{
    			throw new YssException("您的系统连接已被管理员中断，请重新登录或与管理员联系后再试！");
    		}
    		else
    		{
        		throw new YssException(e.getMessage() + "\n ERROR SQL:" +sql);
    		}
			/**End 20131223 added by liubo.Bug #85825.QDV4赢时胜(上海)2013年12月18日01_B*/
    	}
    }
    
    public PreparedStatement getPreparedStatement(String sql) throws YssException{
    	YssPreparedStatement pst = null;
    	try{
    		//pst = new YssPreparedStatement(this,sql);
    		pst = (YssPreparedStatement)getPreparedStatement(sql,false);
        	return pst;
    	}catch(Exception e){
    		throw new YssException(e.getMessage()+ "\n ERROR SQL:" +sql);
    	}
    }
    
    public PreparedStatement getPreparedStatement(String sql,boolean bParserFlag) throws YssException{
    	YssPreparedStatement pst = null;
    	try{
    		if (bParserFlag){
    			pst = new YssPreparedStatement(this,sql,bParserFlag);
    		}else{
    			pst = new YssPreparedStatement(this,sql);
    		}
    		
        	return pst;
    	}catch(Exception e){
    		throw new YssException(e.getMessage()+ "\n ERROR SQL:" +sql);
    	}
    }
    

    
    public PreparedStatement getPreparedStatement(String sql,int resultSetType,int resultSetConcurrency)throws YssException{
    	YssPreparedStatement pst = null;
    	try{
    		pst = new YssPreparedStatement(this,sql,resultSetType,resultSetConcurrency);
        	return pst;
    	}catch(Exception e){
    		throw new YssException(e.getMessage()+ "\n ERROR SQL:" +sql);
    	}
    }

    //added by liubo.Story #1757
    //=====================================
    public YssPreparedStatement getYssPreparedStatement(String sql) throws YssException{
    	YssPreparedStatement yssPst = null;
    	try{
    		//pst = new YssPreparedStatement(this,sql);
    		yssPst = new YssPreparedStatement(this, sql);
        	return yssPst;
    	}catch(Exception e){
    		throw new YssException(e.getMessage()+ "\n ERROR SQL:" +sql);
    	}
    }
    
    public YssPreparedStatement getYssPreparedStatement(String sql,int resultSetType,int resultSetConcurrency)throws YssException{
    	YssPreparedStatement yssPst = null;
    	try{
    		yssPst = new YssPreparedStatement(this,sql,resultSetType,resultSetConcurrency);
        	return yssPst;
    	}catch(Exception e){
    		throw new YssException(e.getMessage()+ "\n ERROR SQL:" +sql);
    	}
    }
    //=================end====================
    

    /**
     * 处理版本信息数据
     * @param oracleVersion
     * @return
     * @author shashijie ,2011-10-12 , STORY 1698
     * @modified
     */
    private String getOracleVersion(String oracleVersion) {
		String version = "";
		try {
			if(oracleVersion.indexOf("=")>-1){
				version = oracleVersion.substring(oracleVersion.indexOf("=")+2,oracleVersion.length());
			}
		} catch (Exception e) {
			version = "";
		}
		return version;
	}
    
    /**
     * 针对oracle 10G以上版本,删除表时不入回收站直接彻底删除
     * @param sql DROP语句
     * @return 编译好的SQL语句
     * @author shashijie ,2011-10-12 , STORY 1698
     * @modified
     */
    public String doOperSqlDrop(String sql) {
    	String strSql = sql;
    	try {
    		if (dbType==1) {//数据类型为oracle
				if (YssCons.Yss_OracleVersion.trim().length()>0) {//有版本信息
					//获取版本大分支,10g,还是9i
					String temp = YssCons.Yss_OracleVersion.substring(0, YssCons.Yss_OracleVersion.indexOf("."));
					int ve = Integer.valueOf(temp).intValue();
					//10g以上版本则便宜SQL语句
					if (ve>=10) {
						strSql += " purge ";
					}
				}
			}
		} catch (Exception e) {
			strSql = sql;
		}
    	return strSql;
	}
	
    
    /**
     *  add by jsc 20121019
     *  连接池问题处理
     *  将Weblogic 的 OracleCallableStatement 强转成oracle.jdbc.driver.OracleCallableStatement
     * @param in
     * @return
     */
	public static oracle.jdbc.driver.OracleCallableStatement WeblogicCastToOracle(Object in) {

		oracle.jdbc.driver.OracleCallableStatement ocs = null;
		try {
			//在weblogic下 使用自带驱动包，需要强转为ORACLE JDBC驱动包
			if ("oracle.jdbc.driver.OracleCallableStatement".equals(in.getClass().getName())) {

				ocs = (oracle.jdbc.driver.OracleCallableStatement) in;

			} else if (in.getClass().getName().toLowerCase().indexOf("weblogic")>=0) {

				Method method = in.getClass().getMethod("getVendorObj",new Class[] {});
				ocs = (oracle.jdbc.driver.OracleCallableStatement) method.invoke(in);
			}

		} catch (Exception e) {
			e.getMessage();
		}
		return ocs;

	}
    
    
		/**
	 * add by jsc 20120809 
	 * 将Clob类型的数据转成字符串，如果不是clob类型则返回原对象
	 *  连接池对大对象的Native处理。
	 *  目前只支持对Weblogic连接池的特殊处理
	 * @param in
	 * @return
	 */
    //add by zhouwei 20120607 story 2188 获取connection
    public Connection getConnection(){
    	return this.conPub;
    }
	public static oracle.sql.CLOB CastToCLOB(Object in) {
		
		oracle.sql.CLOB clob = null;
		try {
			if ("oracle.sql.CLOB".equals(in.getClass().getName())) {
				
				clob = (oracle.sql.CLOB) in;
	
			} else if ("weblogic.jdbc.wrapper.Clob_oracle_sql_CLOB".equals(in.getClass().getName())) {
				
				Method method = in.getClass().getMethod("getVendorObj",new Class[] {});
				clob = (oracle.sql.CLOB) method.invoke(in);
			}
			
			
		} catch (Exception e) {
			e.getMessage();
		}
		return clob;

	}

	
	
    //add by zhouwei webservice实现获取的数据库连接  story 2188 20120529
    public Connection getWsConnection(String db_flag) throws YssException{
        String dbDriver = null;
        String dbUrl = null;
        String dbUser = null;
        String dbPass = null;
        int i = 0, j = 0;
        Connection dbConnection=null;
        GenDBInfo dbInfo = null;
        String dtype="";
        try {
            // 如果conPub已经打开，那么返回conPub，否则打开并返回连接
            if (db_flag == null || db_flag.length() == 0) {
            	db_flag = "[db_yssimsas]";
            }
                        
            if (conPub != null && !conPub.isClosed()) {
                return conPub;
            }
	    	String[] pa = null;
	        String ps=YssFun.loadTxtFile("/dbsetting.txt");     
	        if(ps!=null && !ps.equals("")){
		         pa = ps.split(ps.indexOf("\r\n")>=0?"\r\n":"\n");                            
		         for (i = 0; i < pa.length; i++) { //星号注释
		             if (pa[i].trim().equalsIgnoreCase(db_flag)) {
		                 break;
		             }
		         }
		         dbDriver = pa[i + 1].trim();
		         dbUrl = pa[i + 2].trim();
		         dbUser = pa[i + 3].trim();
		         dbPass = pa[i + 4].trim();
		         dtype = pa[i + 5].trim();
	         }else{//半加密
	        	 File dbFile = new File(File.separator + "dbsetting.properties");
	                if(!dbFile.exists()){
	                	throw new YssException("访问数据库出错！请先配置数据库连接！");
	                }
	        	  dbDriver = dbInfo.readValue(dbFile.getPath(), db_flag + "&&driver");	                
	              dbUrl = dbInfo.readValue(dbFile.getPath(), db_flag + "&&url");
	              dbUser = dbInfo.decrypt("yssusername", dbInfo.readValue(dbFile.getPath(), db_flag + "&&user"));
	              dbPass = dbInfo.decrypt("ysspassword", dbInfo.readValue(dbFile.getPath(), db_flag + "&&password"));
	              dtype = dbInfo.readValue(dbFile.getPath(), db_flag + "&&dbtype"); 
	         }
	         dbType = dtype.equalsIgnoreCase("sql") ? YssCons.DB_SQL :
            (dtype.equalsIgnoreCase("db2") ? YssCons.DB_DB2 :
             YssCons.DB_ORA);
	        Class.forName(dbDriver).newInstance(); 
            dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            conPub = dbConnection;
        } catch (SQLException se) {
            throw new YssException("访问数据库出错，请检查连接设置！", se);
        } catch (Exception ce) {
            throw new YssException("加载数据库驱动程序出错！", ce);
        }

        return dbConnection;   
    }
    
    /**
     * 20131121 added by liubo.Bug #83351.
     * 使用此方法，判断某张表是否存在主键约束
     * @param strTableName
     * @return
     * @throws YssException
     */
    public boolean getTableConstaintKey(String strTableName) throws YssException
    {
    	boolean bReturn = false;
    	ResultSet rs = null;
    	String strSql = "";
    	
    	try
    	{
    		strSql = "select cu.* from user_cons_columns cu, user_constraints au " +
					 " where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' " +
					 " and au.table_name = " + this.sqlString(strTableName.toUpperCase());
    		rs = this.queryByPreparedStatement(strSql);
    		
    		if (rs.next())
    		{
    			return true;
    		}
    	}
    	catch(Exception ye)
    	{
    		throw new YssException("根据表名获取指定表的主键约束出错：" + ye.getMessage());
    	}
    	finally
    	{
    		this.closeResultSetFinal(rs);
    	}
    	
    	return bReturn;
    }
}

//todo 这里添加其它sql函数封装
//******************************************************************************
