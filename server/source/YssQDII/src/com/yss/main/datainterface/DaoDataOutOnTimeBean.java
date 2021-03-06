package com.yss.main.datainterface;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.DividendInvestBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 *  @author guyichuan 20110608 STORY #897 
 *	@功能：处理风控接口投置
 *　@需求编号 QDV4海富通2011年04月07日01_A
 */
public class DaoDataOutOnTimeBean extends BaseDataSettingBean 
	implements IDataSetting{
	private DaoDataOutOnTimeBean filterType = null;
	private DaoDataOutOnTimeBean dataOutOnTime = null;
	private String sRecycled = ""; 			//保存未解析前的字符串
	private String fNum=null;				//编号

    private String m_AssetGroupCode = "";   //组合群代码
    private String m_AssetGroupName = "";	//组合群名称

    private String m_PortCode = "";         //组合代码
    private String m_PortName = "";         //组合名称
    private String m_AutoTime = "";         //采集时间

    private String m_HolidaysCode = "";
    private String m_HolidaysName = "";
    private int m_IsMultiGroup = 0;         //是否是多组合群处理

    private String m_InfaceCode = "";    	//接口代码
    private String m_InfaceName = "";   	//接口名称
    private String m_CusCfgType="";			//接口的类型

	private String oldNum = "";         	//修改前的编号
    private boolean BShow = false;
    private boolean m_isCover=false;       //标识是否需要覆盖
	
    private String  batchData="";//bug 3130 by zhouwei 20111205 QDV4赢时胜(测试)2011年11月11日10_B
    public boolean isM_isCover() {
		return m_isCover;
	}

	public void setM_isCover(boolean mIsCover) {
		m_isCover = mIsCover;
	}

	public String getM_CusCfgType() {
		return m_CusCfgType;
	}

	public void setM_CusCfgType(String mCusCfgType) {
		m_CusCfgType = mCusCfgType;
	}
    
	public boolean isBShow() {
		return BShow;
	}

	public void setBShow(boolean bShow) {
		BShow = bShow;
	}

	public DaoDataOutOnTimeBean getFilterType() {
		return filterType;
	}

	public void setFilterType(DaoDataOutOnTimeBean filterType1) {
		filterType = filterType1;//findbugs风险调整，局部变量名月成员变量名相同 胡坤 20120626
	}

	public String getsRecycled() {
		return sRecycled;
	}

	public void setsRecycled(String sRecycled) {
		this.sRecycled = sRecycled;
	}

	public String getfNum() {
		return fNum;
	}

	public void setfNum(String fNum) {
		this.fNum = fNum;
	}

	public String getM_AssetGroupCode() {
		return m_AssetGroupCode;
	}

	public void setM_AssetGroupCode(String mAssetGroupCode) {
		m_AssetGroupCode = mAssetGroupCode;
	}

	public String getM_AssetGroupName() {
		return m_AssetGroupName;
	}

	public void setM_AssetGroupName(String mAssetGroupName) {
		m_AssetGroupName = mAssetGroupName;
	}

	public String getM_PortCode() {
		return m_PortCode;
	}

	public void setM_PortCode(String mPortCode) {
		m_PortCode = mPortCode;
	}

	public String getM_PortName() {
		return m_PortName;
	}

	public void setM_PortName(String mPortName) {
		m_PortName = mPortName;
	}

	public String getM_AutoTime() {
		return m_AutoTime;
	}

	public void setM_AutoTime(String mAutoTime) {
		m_AutoTime = mAutoTime;
	}

	public String getM_HolidaysCode() {
		return m_HolidaysCode;
	}

	public void setM_HolidaysCode(String mHolidaysCode) {
		m_HolidaysCode = mHolidaysCode;
	}

	public String getM_HolidaysName() {
		return m_HolidaysName;
	}

	public void setM_HolidaysName(String mHolidaysName) {
		m_HolidaysName = mHolidaysName;
	}

	public int getM_IsMultiGroup() {
		return m_IsMultiGroup;
	}

	public void setM_IsMultiGroup(int mIsMultiGroup) {
		m_IsMultiGroup = mIsMultiGroup;
	}

	public String getM_InfaceCode() {
		return m_InfaceCode;
	}

	public void setM_InfaceCode(String mInfaceCode) {
		m_InfaceCode = mInfaceCode;
	}

	public String getM_InfaceName() {
		return m_InfaceName;
	}

	public void setM_InfaceName(String mInfaceName) {
		m_InfaceName = mInfaceName;
	}

	public String getOldNum() {
		return oldNum;
	}

	public void setOldNum(String oldNum) {
		this.oldNum = oldNum;
	}
	public void checkPort()throws YssException {
		String sResult="";
		StringBuffer bufCheckSql=new StringBuffer();
		String[]portCodeArry=null;
		
		ResultSet rs=null;
		
		//保存前如果当前组合已存在就给出提示
        bufCheckSql.append(" select FPortCode from ");
        bufCheckSql.append("TB_Base_DaoDataOutOnTime");
        bufCheckSql.append(" where FCheckState=1 and FAssetGroupCode="+dbl.sqlString(this.m_AssetGroupCode));
        try{
        	rs=dbl.openResultSet(bufCheckSql.toString());
        		portCodeArry=this.m_PortCode.split(",");
        	while(rs.next()){
        		for(int i=0;i<portCodeArry.length;i++){
        			if(rs.getString("FPortCode").startsWith(portCodeArry[i]+",")||
        					rs.getString("FPortCode").indexOf(","+portCodeArry[i]+",")!=-1||
        					rs.getString("FPortCode").endsWith(","+portCodeArry[i])||
        					rs.getString("FPortCode").equals(portCodeArry[i])){
        				sResult+=portCodeArry[i]+",";
        			}	
        		}
        	}
        	if(sResult.length()!=0){
        		sResult=sResult.substring(0,sResult.length()-1);
        		throw new YssException("【"+sResult+"】组合已存在！");
        	}
        }catch(YssException e){
        	throw new YssException(e.getMessage());
        }catch(Exception e){
        	e.printStackTrace();
        	throw new YssException(e.getMessage(),e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	//新增
	public String addSetting() throws YssException {
		StringBuffer bufSql = new StringBuffer();
		String strSql="";
        boolean bTrans = false; // 代表是否开始了事务
        if(m_isCover){  //是否覆盖
        	deleteData();
        }else{
        	checkPort();//保存前如果当前组合已存在就给出提示
        }
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            String nowDate=YssFun.formatDate(new java.util.Date(),
                    "yyyyMMdd");
            //fNum主键
            this.fNum = "T" + nowDate +
            dbFun.getNextInnerCode("TB_Base_DaoDataOutOnTime",
                                   dbl.sqlRight("FNum", 6), "000001",
                                   " where FNum like 'T"
                                   + nowDate + "%'", 1); 
            
            bufSql.append("insert into TB_Base_DaoDataOutOnTime");
            bufSql.append(" (FNum,FAssetGroupCode,FPortCode,FAutoTime,");
            bufSql.append(" FHolidaysCode,FInfaceCode,FIsMultiGroup,FCreator,FCreateTime,FCheckState)");
            bufSql.append(" values( ");
            bufSql.append(dbl.sqlString(this.fNum)+",");
            bufSql.append(dbl.sqlString(this.m_AssetGroupCode)+",");
            bufSql.append(dbl.sqlString(this.m_PortCode)+",");
            bufSql.append(dbl.sqlString(this.m_AutoTime)+",");
            bufSql.append(dbl.sqlString(this.m_HolidaysCode)+",");
            bufSql.append(dbl.sqlString(this.m_InfaceCode)+",");
            bufSql.append(this.m_IsMultiGroup+",");
            bufSql.append(dbl.sqlString(this.creatorCode)+",");
            bufSql.append(dbl.sqlString(this.creatorTime)+","); 
            bufSql.append("1 )");
            
            dbl.executeSql(bufSql.toString());
        
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增风控接口信息出错", e);
        } finally {
        	
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}
	//审核     回收站原还功能使用
	public void checkSetting() throws YssException {
		String strSql = ""; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		
		checkPort();//审核前如果当前组合已存在就给出提示
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		try {
			conn.setAutoCommit(false); 
			bTrans = true; 
			if (sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) { // 判断传来的内容是否为空
				arrData = sRecycled.split("\r\n"); 
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]); 
					strSql = "update Tb_Base_DaoDataOutOnTime "
                	+ " set FCheckState = case fcheckstate when 0 then 0 else 1 end" 
                	+ ", FCheckUser = " 
                	+ dbl.sqlString(this.checkUserCode)
                	+ ", FCheckTime = "
                	+ dbl.sqlString(this.checkTime)
                	+ " where FNum = " + dbl.sqlString(this.fNum);
	
					dbl.executeSql(strSql); // 执行更新操作
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("返回风控数据接口信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}
		
	}
	//删除
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        
        if(this.m_AssetGroupCode!=null&&this.m_AssetGroupCode.length()!=0){
        	pub.setPrefixTB(this.m_AssetGroupCode);//将该组合群代码设为表前缀
        }else{
        	this.m_AssetGroupCode=pub.getAssetGroupCode();
        }
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
            strSql = "update Tb_Base_DaoDataOutOnTime"
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where FNum = " + dbl.sqlString(this.fNum);
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除风控导出信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }	
	}
	//清除
	public void deleteRecycleData() throws YssException {
		String strSql = ""; // 定义一个放SQL语句的字符串
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false; // 代表是否开始了事务
        // 获取一个连接
        Connection conn = dbl.loadConnection();
        
        if(this.m_AssetGroupCode!=null&&this.m_AssetGroupCode.length()!=0){
        	pub.setPrefixTB(this.m_AssetGroupCode);//将该组合群代码设为表前缀
        }else{
        	this.m_AssetGroupCode=pub.getAssetGroupCode();
        }
        try {
            // 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null&&!sRecycled.equalsIgnoreCase("")) {
                // 根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                // 循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from Tb_Base_DaoDataOutOnTime"
	                	+ " where FNum = " + dbl.sqlString(this.fNum);
                    
                    dbl.executeSql(strSql);
                }
            }
            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new YssException("清除风控导出信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); // 释放资源
        }		
	}
	//根据组合群，组合代码删除
	public void deleteData() throws YssException {
		String strSql = ""; // 定义一个放SQL语句的字符串
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false; // 代表是否开始了事务
        // 获取一个连接
        Connection conn = dbl.loadConnection();
        try {
                    strSql = "delete from Tb_Base_DaoDataOutOnTime"
	                	+ " where FAssetGroupCode = " + dbl.sqlString(this.m_AssetGroupCode)
	                	+" and FPortCode="+dbl.sqlString(this.m_PortCode);
                    
                    dbl.executeSql(strSql);          
            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new YssException("清除风控导出信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); // 释放资源
        }		
	}
	//修改
	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        
        if(this.m_AssetGroupCode!=null&&this.m_AssetGroupCode.length()!=0){
        	pub.setPrefixTB(this.m_AssetGroupCode);//将该组合群代码设为表前缀
        }else{
        	this.m_AssetGroupCode=pub.getAssetGroupCode();
        }
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update Tb_Base_DaoDataOutOnTime"
	            	+ " set FAssetGroupCode = " + dbl.sqlString(this.m_AssetGroupCode)
	            	+ " , FPortCode = " + dbl.sqlString(this.m_PortCode)
	            	+ " , FAutoTime = " + dbl.sqlString(this.m_AutoTime)
	            	+ " , FHolidaysCode = " + dbl.sqlString(this.m_HolidaysCode)
	            	+ " , FInfaceCode = " + dbl.sqlString(this.m_InfaceCode)
	            	+ " , FIsMultiGroup="+ this.m_IsMultiGroup
	            	+ " , fcreator = " + dbl.sqlString(this.creatorCode)
	            	+ " , fcreatetime = " + dbl.sqlString(this.creatorTime)  
					+ " where FNum = " + dbl.sqlString(this.oldNum);
            dbl.executeSql(strSql); 
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改风控接口导出信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		buf.append(this.fNum).append("\t");
        buf.append(this.m_AssetGroupCode).append("\t");
        buf.append(this.m_AssetGroupName).append("\t");
        buf.append(this.m_PortCode).append("\t");
        buf.append(this.m_PortName).append("\t");
        buf.append(this.m_AutoTime).append("\t");
        buf.append(this.m_HolidaysCode).append("\t");
        buf.append(this.m_HolidaysName).append("\t");
        buf.append(this.m_IsMultiGroup).append("\t");
        buf.append(this.m_InfaceCode).append("\t");
        buf.append(this.m_InfaceName).append("\t");
        buf.append(this.m_CusCfgType).append("\t");
        
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		//---bug 3130 add by zhouwei 20111205 批量删除  QDV4赢时胜(测试)2011年11月11日10_B start---//
		String retrunStr="";
		try{
			
			if(sType!=null && sType.equals("delBatch")){
				delBatch();
			}
		}catch(Exception e){
			throw new YssException(""+e.getMessage(),e);
		}
	
		return retrunStr;
		//---bug 3130 add by zhouwei 20111205 批量删除  QDV4赢时胜(测试)2011年11月11日10_B end---//
	}
	//批量删除 bug 3130 add by zhouwei 20111205 QDV4赢时胜(测试)2011年11月11日10_B
	public void delBatch() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            String[] arrayStr=this.batchData.split("\f\f");
            for(int i=0;i<arrayStr.length;i++){
	            parseRowStr(arrayStr[i]);
	        	strSql = "update Tb_Base_DaoDataOutOnTime"
	                  + " set FCheckState = 2 " 
	                  + ", FCheckUser = null " 
	                  + ", FCheckTime = null "
	                  + " where FNum = " + dbl.sqlString(this.fNum);
	              
	            dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除风控导出信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }	
	}
	public void parseRowStr(String sRowStr) throws YssException {
			if (dataOutOnTime == null) {
				dataOutOnTime = new DaoDataOutOnTimeBean();
				dataOutOnTime.setYssPub(pub);
	        }
			String reqAry[] = null;
	        String sTmpStr = "";
	        try {
	            if (sRowStr.trim().length() == 0) {
	                return;
	            }
	            //bug 3130 by zhouwei 20111205 QDV4赢时胜(测试)2011年11月11日10_B 批量删除
	            if(sRowStr.indexOf("\f\f")>-1){
	            	sTmpStr=sRowStr.split("\f\f")[0];
	            	this.batchData=sRowStr;
	            }else{
	            	if (sRowStr.indexOf("\r\t") >= 0) {
		                sTmpStr = sRowStr.split("\r\t")[0];
		            } else {
		                sTmpStr = sRowStr;
		            }
	            }	            
	            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
	            reqAry = sTmpStr.split("\t");
	            
	            this.fNum=reqAry[0];
	            this.m_AssetGroupCode = reqAry[1];
	            this.m_PortCode = reqAry[2];
	            this.m_AutoTime = reqAry[3];
	            this.m_HolidaysCode = reqAry[4];
	            this.m_IsMultiGroup = ("true".equalsIgnoreCase(reqAry[5])?1:0);
	            this.m_InfaceCode=reqAry[6];
	            this.oldNum=reqAry[7];
	            if (reqAry[8].equalsIgnoreCase("true")) {
	                this.BShow = true;
	            } else {
	                this.BShow = false;
	            }
	            this.m_isCover=("true".equalsIgnoreCase(reqAry[9])?true:false);
	            
	            super.parseRecLog();
	            
	            if (sRowStr.indexOf("\r\t") >= 0) {
	                if (this.filterType == null) {
	                    this.filterType = new DaoDataOutOnTimeBean();
	                    this.filterType.setYssPub(pub);
	                }
	                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);    
	            }
	        } catch (Exception e) {
	            throw new YssException("解析风控接口数据出错！", e);
	        }
	}

	public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String tmPortName="";						//临时的组合名称
        String tmInterfaceName="";					//临时的接口名称
        
        StringBuffer bufSql=new StringBuffer(); 	// 定义一个存放sql语句的字符串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        StringBuffer tmpPortNameSql=new StringBuffer();
        StringBuffer tmpInfaceNameSql=new StringBuffer();
        
        
        ResultSet rs = null;
        ResultSet tmpRs = null;
        ResultSet tmpRs2 = null;
        
        String groupCode = pub.getAssetGroupCode(); //add by fangjiang 2011.08.25 BUG 2486
        try { 
        	sHeader = this.getListView1Headers();
        	//查询接组合群对应风控接口信息
        	bufSql.append(" select a.*,b.FAssetGroupName as FAssetGroupName,");
        	bufSql.append(" d.FUserName as FCreatorName,e.FUserName as FCheckUserName,");
        	bufSql.append(" c.fholidaysname as FHolidaysName from");
        	/**shashijie 2012-3-7 BUG 3821 风控接口的导出和删除 */
        	//bufSql.append(" (select * from ");
        	bufSql.append(" TB_Base_DaoDataOutOnTime a");
        	//bufSql.append(" where 1=1 ");
        	//bufSql.append((this.m_IsMultiGroup==0?" and FAssetGroupCode="+dbl.sqlString(pub.getAssetGroupCode()):""));
        	//bufSql.append( buildFilterSql()+" )a");
        	bufSql.append(" left join  TB_SYS_ASSETGROUP b on b.FAssetGroupCode=a.FAssetGroupCode");
        	bufSql.append(" left join Tb_Base_Holidays c on c.FHolidaysCode=a.FHolidaysCode ");	
        	bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) d on a.FCreator =d.FUserCode");
        	bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) e on a.FCheckUser =e.FUserCode");
        	//查询时执行
        	bufSql.append( (this.m_IsMultiGroup==0?" where a.FAssetGroupCode="+dbl.sqlString(pub.getAssetGroupCode()):""));
        	//筛选时执行
        	bufSql.append( buildFilterSql() );
			/**end*/
            rs = dbl.openResultSet(bufSql.toString());
            while(rs.next()){
            	tmPortName="";
            	tmInterfaceName="";
            	String portCodeArry[]=rs.getString("FPortCode").split(",");
            	String InfaceCodeArry[]=rs.getString("FInfaceCode").split(",");

            	pub.setPrefixTB(rs.getString("FAssetGroupCode"));//将该组合群代码设为表前缀
            	//循环取出组合名称
            	for(int i=0;i<portCodeArry.length;i++){
            		tmpPortNameSql.append("select FPortCode,FPortName,FAssetGroupCode from ");
            		tmpPortNameSql.append(pub.yssGetTableName("Tb_Para_Portfolio "));
            		tmpPortNameSql.append(" where FAssetGroupCode="+dbl.sqlString(rs.getString("FAssetGroupCode")));
            		tmpPortNameSql.append(" and FPortCode="+dbl.sqlString(portCodeArry[i]));
            		
            		tmpRs = dbl.openResultSet(tmpPortNameSql.toString());
            		tmpPortNameSql.delete(0, tmpPortNameSql.length());
            		if(tmpRs.next()){
            			tmPortName+=tmpRs.getString("FPortName")+",";
            		}
            		//add by songjie 2011.09.26 BUG 2639 QDV4海富通2011年09月05日01_B
            		dbl.closeResultSetFinal(tmpRs);
            	}
            	if(tmPortName.length()!=0){
            		this.m_PortName=tmPortName.substring(0, tmPortName.length()-1);
            	}
            	////循环取出接口名称
            	for(int i=0;i<InfaceCodeArry.length;i++){
            		tmpInfaceNameSql.append("select FCusCfgName from ");
            		tmpInfaceNameSql.append(pub.yssGetTableName("Tb_Dao_CusConfig"));
            		tmpInfaceNameSql.append(" where FCusCfgCode="+dbl.sqlString(InfaceCodeArry[i]));
            		
            		tmpRs2 = dbl.openResultSet(tmpInfaceNameSql.toString());
            		tmpInfaceNameSql.delete(0,tmpInfaceNameSql.length());
            		if(tmpRs2.next()){
            			tmInterfaceName+=tmpRs2.getString("FCusCfgName")+",";
            		}
            		//add by songjie 2011.09.26 BUG 2639 QDV4海富通2011年09月05日01_B
            		dbl.closeResultSetFinal(tmpRs2);
            	}
            	if(tmInterfaceName.length()!=0){
            		this.m_InfaceName=tmInterfaceName.substring(0, tmInterfaceName.length()-1);
            	}
            	bufShow.append(buildRowShowStr(rs)).append(YssCons.YSS_LINESPLITMARK);
            	//bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                //append(YssCons.YSS_LINESPLITMARK);       	
            	this.setResultSetAttr(rs);     
            	bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
            + "\r\f" + this.getListView1ShowCols();
        }
        catch(Exception e){
        	throw new YssException("获取风控接口数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        	dbl.closeResultSetFinal(tmpRs);
        	dbl.closeResultSetFinal(tmpRs2);
        	pub.setPrefixTB(groupCode); //add by fangjiang 2011.08.25 BUG 2486
        }
	}
	private String buildRowShowStr(ResultSet rs)throws YssException{
		StringBuffer buildStr=new StringBuffer();
		try{
			buildStr.append(rs.getString("FAssetGroupCode")).append("\t");
			buildStr.append(rs.getString("FAssetGroupName")).append("\t");
			buildStr.append(rs.getString("FPortCode")).append("\t");
			buildStr.append(this.m_PortName).append("\t");
			buildStr.append(rs.getString("FAutoTime")).append("\t");
			buildStr.append(rs.getString("FHolidaysCode")).append("\t");
			buildStr.append(rs.getString("FHolidaysName")).append("\t");
			buildStr.append(rs.getString("FInfaceCode")).append("\t");
			buildStr.append(this.m_InfaceName).append("\t");
			buildStr.append(rs.getString("FCreator")).append("\t");
			buildStr.append(rs.getString("FCreateTime"));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return buildStr.toString();
	}
	 /**
     * 筛选条件
     * @throws YssException
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
        	/**shashijie 2012-3-7 BUG 3821 风控接口的导出和删除 */
        	if(this.m_IsMultiGroup == 0){
        		sResult = " and 1=1";//sql中已有where
        	} else {
        		sResult = " where 1=1";//sql中已有where
			}
            
            if (this.filterType.m_AssetGroupCode!=null 
            		&&this.filterType.m_AssetGroupCode.length()!=0) {
                sResult +=" and a.FAssetGroupCode= "+dbl.sqlString(this.filterType.m_AssetGroupCode);
            }
            if (this.filterType.m_PortCode!=null
            		&& this.filterType.m_PortCode.length()!=0) {
                sResult += " and a.FPortCode like " +dbl.sqlString("%"+this.filterType.m_PortCode+"%");
            }
            if (this.filterType.m_InfaceCode!=null
            		&& this.filterType.m_InfaceCode.length()!=0) {
                sResult += " and a.FInfaceCode=" +dbl.sqlString(this.filterType.m_InfaceCode);
            }
            if (this.filterType.m_HolidaysCode!=null
            		&& this.filterType.m_HolidaysCode.length()!=0) {
                sResult += " and a.FHolidaysCode=" +dbl.sqlString(this.filterType.m_HolidaysCode);
            }
            /**end*/
        }
        return sResult;
    }
    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
    	this.fNum=rs.getString("FNum");
    	this.m_AssetGroupCode=rs.getString("FAssetGroupCode");
    	this.m_AssetGroupName=rs.getString("FAssetGroupName");
    	this.m_PortCode=rs.getString("FPortCode");
    	this.m_AutoTime=rs.getString("FAutoTime");
    	this.m_HolidaysCode=rs.getString("FHolidaysCode");
    	this.m_HolidaysName=rs.getString("FHolidaysName");
    	this.m_InfaceCode=rs.getString("FInfaceCode");
    	this.m_IsMultiGroup=rs.getInt("FIsMultiGroup");
    	this.m_CusCfgType=String.valueOf(2);//类型都为２　风控-导出
    	
        super.setRecLog(rs);
    }
	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
