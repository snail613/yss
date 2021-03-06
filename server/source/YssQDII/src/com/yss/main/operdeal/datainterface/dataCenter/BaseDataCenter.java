package com.yss.main.operdeal.datainterface.dataCenter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/******************************************************
 * MS01541 QDV4赢时胜上海2010年08月4日01_AB  数据中心
 * @author jiangshichao
 *
 */
public  class BaseDataCenter extends BaseBean{
    protected String sStartDate = "";
    protected String sEndDate = "";
	protected String sportCodes = "";
	protected Connection con = null; 
    protected String sDataType = "";
	protected String[] tmpPortCodes = null;//add by jiangshichao STORY #1244 导入汇率时多组合情况下，以组合代码为空的汇率为默认的汇率，导入数据中心相关需求
    
    private static String dbDriverLoaded = null; //已经加载的driver，避免多次调用class.forname
	private String dbDriver = "";
    private String dbUrl = "";
    private String dbUser = "";
    private String dbPass = "";
    private int dbType = 0;
    public void parseRowStr(String sRowStr) throws YssException {
    	 String[] reqAry = null; 
    	 reqAry = sRowStr.split("\t");
    	 this.sStartDate = reqAry[0];
    	 this.sEndDate = reqAry[1];
    	 this.sportCodes=reqAry[2]; 
    	 StringBuffer buff = new StringBuffer();
    	 String[] sport=reqAry[2].split(",");
    	//modified by  yehshenghong story3702 20130416
    	 tmpPortCodes = sport;
    	 for(int i=0;i<sport.length;i++){//modified by  yehshenghong story3702 20130416
    		 buff.append(sport[i]).append(",");	 //modify by chenjianxin QDV4华安基金2011年6月20日01_A_ 20110722
    	 }
    	 if(buff.toString().length()>1){
 			sportCodes= buff.toString().substring(0, buff.toString().length()-1);
 		}
    	//-----end modified by  yehshenghong story3702 20130416
    	 this.sDataType = reqAry[3];
    }
    /***************************************
     * 
     * @return  返回导入记录数目
     * @throws YssException
     */
    public String impData() throws YssException{
    	return "";
    }
    	
    //读取并解析数据库配置信息
	private void parsePrope()throws YssException{
		String[] pa = null;
		String sDbLbl = "[db_datacenter]";
		int i = 0;
		try {
			pa = YssFun.loadTxtFile("/dbsetting.txt").split("\r\n");
			for (i = 0; i < pa.length; i++) {
				if (pa[i].trim().equalsIgnoreCase(sDbLbl)) {
					break;
				}
			}

			dbDriver = pa[i + 1].trim();
			dbUrl = pa[i + 2].trim();
			dbUser = pa[i + 3].trim();
			dbPass = pa[i + 4].trim();
			String dtype = pa[i + 5].trim();

			dbType = dtype.equalsIgnoreCase("sql") ? YssCons.DB_SQL : (dtype
					.equalsIgnoreCase("db2") ? YssCons.DB_DB2 : YssCons.DB_ORA);
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
			throw new YssException("访问数据库出错，请检查连接设置！", e);
		}
	}
	
    protected Connection loadConnection() throws YssException{
    	Connection dbConnection = null;
    	try{
    	 if (con != null && !con.isClosed()) {
             return con;
         }
    	 
    	 if(dbUrl.equalsIgnoreCase("")&& dbUser.equalsIgnoreCase("")&& dbPass.equalsIgnoreCase("")){
    		 parsePrope();
    	 }
    	 
    	 if (dbDriverLoaded == null ||
                 !dbDriverLoaded.equalsIgnoreCase(dbDriver)) {
                 Class.forName(dbDriver).newInstance(); //注意，这句话未必每次都要执行了，也许前面执行过
                 dbDriverLoaded = dbDriver;
             }
             dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             
             con = dbConnection; //如果不保存连接，则不要这句
             
             return con;
    	} catch (SQLException se) {
            throw new YssException("访问数据库出错，请检查连接设置！", se);
        } catch (Exception ce) {
            throw new YssException("加载数据库驱动程序出错！", ce);
        } 
            
    }
    
    protected PreparedStatement openPreparedStatement(String sql) throws
    SQLException, YssException {
    return loadConnection().prepareStatement(sql);
}
    
    protected final void closeStatementFinal(Statement st) {
        closeStatementFinal(st, null);
    }

    private final void closeStatementFinal(Statement st1, Statement st2) {
        if (st1 != null) {
            try {
                st1.close();
            } catch (Exception e) {}
        }

        if (st2 != null) {
            try {
                st2.close();
            } catch (Exception e) {}
        }
    }
     
    protected final void endTransFinal(Connection con, boolean bTrans) {
        try {
            if (bTrans) {
                con.rollback();
            }
        } catch (Exception e) {}
        try {
            con.setAutoCommit(true);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    /*********************************************************************************
     * 判断数据中心表是否已经进行确认，如果数据已经确认则不能再导入数据
     * @param tabName
     * @param fundNo
     * @param dDate
     * @return
     */
    public String isDataConfirm()throws Exception{
    	String msg = "";
    	String info = "";
    	String Desc = "";
    	String query = "";
    	String strSql = "";
    	String sDate = "";
    	ResultSet rs = null,rs1=null;
    	PreparedStatement pst = null;
    	String curPort = "";
    	String curGroup = "";
    	try{
    		
            String syear = sEndDate.substring(0, 4);
			
            String[] portCodes = sportCodes.split(",");
            query = " select * from DATA_CONFIRM a where fund_id=? and trade_date between "+dbl.sqlString(YssFun.formatDate(sStartDate, "yyyyMMdd"))+" and "+
	        dbl.sqlString(YssFun.formatDate(sEndDate, "yyyyMMdd"))+"   order by trade_date ";

            pst = openPreparedStatement(query);
            //add  yeshenghong to support mutiple groups 20130412
            for(int i=0;i<portCodes.length;i++)
            {
            	if(portCodes[i].indexOf("-")>0)
            	{
            		curGroup = portCodes[i].split("-")[0];
            		curPort = portCodes[i].split("-")[1];
            		
            	}
            	else
            	{
            		curGroup = pub.getAssetGroupCode();
            		curPort = portCodes[i];
            	}
				strSql = " select a.fassetcode,b.fsetcode,b.fsetname from "+
		           " (select fassetcode from " + "tb_" + curGroup + "_para_portfolio" +" where fportcode in("+ dbl.sqlString(curPort) + ") and fcheckstate=1)a"+
		        " left join" +
		           " (select fsetcode,fsetid,fsetname from lsetlist where fyear="+syear+" order by FSetCode desc) b"+
		        " on a.fassetcode = b.fsetid";
	    		
				rs = dbl.openResultSet(strSql);
				while(rs.next()){
					pst.setString(1, rs.getString("fassetcode"));
					
					rs1 = pst.executeQuery();
					int count=0;
					String sBeginDate = "";
					String sDate1 = "";
					while(rs1.next()){
						if(count ==0){
							sBeginDate = rs1.getString("trade_date");
						}
						sDate1 = rs1.getString("trade_date");
						count++;
					}
					dbl.closeResultSetFinal(rs1);
					if(count==1){
						Desc = "【"+rs.getString("fsetname")+"】"+sBeginDate+"日数据已确认，无法进行导入操作！！！";
						break;
					}else if(count>1){
						Desc = "【"+rs.getString("fsetname")+"】从"+sBeginDate+"日 至"+sDate1+"日数据已确认，无法进行导入操作！！！";
						break;
					}
				}
				dbl.closeResultSetFinal(rs);
            }
            //-----end add  yeshenghong to support mutiple groups 20130412
            if(Desc.length()>0){
    			msg = Desc;
    		}
    		return msg ;
    	}catch(Exception e){
			throw new YssException("【数据中心接口：核对数据确认表出错......】\t"+"");
    	}finally{
    		dbl.closeResultSetFinal(rs, rs1);
    		closeStatementFinal(pst);
    	}
    }
    
    /**
	 * add by jsc 20120608
	 * 缓存资产代码
	 * @return
	 * @throws YssException
	 */
    public HashMap initAssetMap()throws YssException{
		
		StringBuffer sqlBuf = new StringBuffer();
		ResultSet rs =null;
		ResultSet rs1 = null;
		HashMap assetMap = new HashMap();
		try{
			
			rs = dbl.openResultSet(" select fassetgroupcode from TB_SYS_ASSETGROUP ");
			
			while(rs.next()){
				sqlBuf.setLength(0);
				sqlBuf.append(" select fportcode,fassetcode from tb_").append(rs.getString("fassetgroupcode").trim()).append("_para_portfolio where fcheckstate=1");
			    rs1 = dbl.openResultSet(sqlBuf.toString());
				while(rs1.next()){
					assetMap.put(rs1.getString("fportcode").trim(), rs1.getString("fassetcode"));
				}
				dbl.closeResultSetFinal(rs1);
				
			}
			
			return assetMap;
		}catch(Exception e){
			throw new YssException("初始化资产代码出错... ...");
		}finally{
			if(rs1 !=null){
				dbl.closeResultSetFinal(rs, rs1);
			}else{
				dbl.closeResultSetFinal(rs);
			}
			
			sqlBuf.setLength(0);
		}
	}
    
}
