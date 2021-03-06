/**
 * 
 */
package com.yss.main.parasetting;

import java.sql.Connection;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * @包名：com.yss.main.parasetting
 * @文件名：SecurityLendBean.java
 * @创建人：zhangfa
 * @创建时间：2010-10-25
 * @版本号：0.1
 * @说明：TODO
 * <P> 
 * @修改记录
 * 日期        |   修改人       |   版本         |   说明<br>
 * ----------------------------------------------------------------<br>
 * 2010-10-25 | Administrator | 0.1 |  
 */
public class SecurityLendBean extends BaseDataSettingBean implements IDataSetting{
	private String securityCode="";//证券代码
	private String securityName="";//证券名称
	private String brokerCode="";//交易券商代码
	private String brokerName="";////交易券商名称
	private String startDate=""; //计息起始日
	private String strPeriodCode = ""; // 期间代码
	private String strPeriodName = ""; // 期间名称
	private String strRoundCode = ""; // 舍入设置代码
	private String strRoundName = ""; // 舍入设置名称
	
	private String oldsecurityCode="";
	private String oldstartDate="";
	private String oldBrokerCode="";
	private SecurityLendBean filterType;
	
	private String sRecycled = "";
	
	/**
     * parseRowStr
     * 解析证券借贷信息
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
           this.securityCode=reqAry[0];
           this.securityName=reqAry[1];
           this.brokerCode=reqAry[2];
           this.brokerName=reqAry[3];
           this.startDate=reqAry[4];
           
           this.strPeriodCode=reqAry[5];
           this.strPeriodName=reqAry[6];
           this.strRoundCode=reqAry[7];
           this.strRoundName=reqAry[8];
           
           this.oldstartDate=reqAry[9];
           this.oldsecurityCode=reqAry[10];
           this.oldBrokerCode=reqAry[11];
          
           this.checkStateId = Integer.parseInt(reqAry[12]);
           
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SecurityLendBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析证券借贷信息设置请求出错", e);
        }
    }
    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.brokerCode).append("\t");
        buf.append(this.brokerName).append("\t");
        buf.append(this.startDate).append("\t");
        buf.append(this.strPeriodCode).append("\t");
        buf.append(this.strPeriodName).append("\t");
        buf.append(this.strRoundCode).append("\t");
        buf.append(this.strRoundName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }
    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";

           if (this.filterType.startDate.length() != 0) {
               sResult = sResult + " and FStartDate =" +dbl.sqlString(filterType.startDate);
           }
            if (this.filterType.securityCode.length() != 0) {
                sResult = sResult + " and FSecurityCode =" +dbl.sqlString(filterType.securityCode);
                  //  filterType.securityCode.replaceAll("'", "''") + "%'";
            }
            if(this.filterType.brokerCode.length()!=0){
            	 sResult = sResult + " and FBROKERCODE =" +dbl.sqlString(filterType.brokerCode);
                // filterType.replaceAll("'", "''") + "%'";
            }
            if(this.filterType.strPeriodCode.length()!=0){
           	 sResult = sResult + " and FPERIODCODE  =" +dbl.sqlString(filterType.strPeriodCode);
               // filterType.replaceAll("'", "''") + "%'";
           }
            if(this.filterType.strRoundCode.length()!=0){
              	 sResult = sResult + " and FRoundCode  =" +dbl.sqlString(filterType.strRoundCode);
                  // filterType.replaceAll("'", "''") + "%'";
              }
        }
        return sResult;
    }
    /**
     * getListViewData1
     * 获取证券借贷信息设置
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = ""; //,sVocStr1="";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql ="select * from (select a.*, b.*,c.FCheckUserName,d.FBrokerName,e.FPeriodName,f.FRoundName,g.FSecurityName ,v.fvocname as FStartDateName from "+pub.yssGetTableName("tb_para_SecurityLend")+" a "+
                   " left join(select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode "+
                   " left join(select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList ) c on a.FCheckUser=c.FUserCode"+
                   " left join(select FBrokerCode ,FBrokerName from "+pub.yssGetTableName("tb_para_Broker")+" where FCheckState=1) d  on d.FBrokerCode =a.FBrokerCode "+
                   " left join(select FPeriodCode,FPeriodName from "+pub.yssGetTableName("Tb_Para_Period")+" where FcheckState=1)e on e.FPeriodCode=a.FPeriodCode "+
                   " left join(select FRoundCode,FRoundName from "+pub.yssGetTableName("Tb_Para_Rounding")+" where FcheckState=1 ) f on f.FRoundCode=a.FRoundCode" +
                   " left join(select FSecurityCode,FSecurityName from "+pub.yssGetTableName("Tb_Para_Security")+" where FCheckState=1 ) g on g.FSecurityCode=a.FSecurityCode"+     
                   " left join (select fvoccode,fvocname from Tb_Fun_Vocabulary f where fvoctypecode='para_startdate' and  FcheckState=1 ) v on v.fvoccode=a.fstartdate    )"+
                   buildFilterSql(); 

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                
                this.securityCode = rs.getString("FSecurityCode") + "";
                this.securityName = rs.getString("FSecurityName") + "";
                this.brokerCode=rs.getString("FBrokerCode")+"";
                this.brokerName=rs.getString("FBrokerName")+"";
                this.startDate = rs.getString("FStartDate") + "";
                this.strPeriodCode=rs.getString("FPeriodCode")+"";
                this.strPeriodName=rs.getString("FPeriodName")+"";
                this.strRoundCode=rs.getString("FRoundCode")+"";
                this.strRoundName=rs.getString("FRoundName")+"";
                super.setRecLog(rs);
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_PARA_STARTSDATE );

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取证券借贷信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#addSetting()
	 */
	public String addSetting() throws YssException {
		
		  String strSql = "";
	        boolean bTrans = false; //代表是否开始了事务
	        Connection conn = dbl.loadConnection();
	        try {
	            strSql = "insert into " + pub.yssGetTableName("tb_para_SecurityLend") +
	                " (FSecurityCode,FBrokerCode,FStartDate ,FPeriodCode,FRoundCode," +
	                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
	                " values(" + dbl.sqlString(this.securityCode) + "," +
	                dbl.sqlString(this.brokerCode) + "," +
	                dbl.sqlString(this.startDate) + "," +
	                dbl.sqlString(this.strPeriodCode) + "," +
	                dbl.sqlString(this.strRoundCode) + "," +

	                (pub.getSysCheckState() ? "0" : "1") + "," +
	                dbl.sqlString(this.creatorCode) + "," +
	                dbl.sqlString(this.creatorTime) + "," +
	                (pub.getSysCheckState() ? "' '" :
	                 dbl.sqlString(this.creatorCode)) +" )" ;
	            conn.setAutoCommit(false);
	            bTrans = true;
	            dbl.executeSql(strSql);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
	        }

	        catch (Exception e) {
	            throw new YssException("增加证券借贷信息出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }

	        return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkInput(byte)
	 */
	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("tb_para_SecurityLend"),
                "FSecurityCode,FBrokerCode",
                this.securityCode+","+this.brokerCode,
                this.oldsecurityCode+","+this.oldBrokerCode);
	}


	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#delSetting()
	 */
	public void delSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql ="update " + pub.yssGetTableName("tb_para_SecurityLend") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where   FSecurityCode="+dbl.sqlString(this.securityCode)+
                " and FBrokerCode="+dbl.sqlString(this.brokerCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除证券借贷信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}



	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#editSetting()
	 */
	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("tb_para_SecurityLend") +
                " set FStartDate = " +
                dbl.sqlString(this.startDate) + ", FSecurityCode = " +
                dbl.sqlString(this.securityCode) + " ," +
                " FBrokerCode = " + dbl.sqlString(this.brokerCode) + "," +
                   " FPeriodCode = " +dbl.sqlString(this.strPeriodCode)+ "," +
                   " FRoundCode = " +dbl.sqlString(this.strRoundCode)+ "," +
                   "FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where   FSecurityCode="+dbl.sqlString(this.oldsecurityCode)+
                " and FBrokerCode="+dbl.sqlString(this.oldBrokerCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改证券借贷信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
		return null;
	}
	 public void checkSetting() throws YssException {
		   
	        String strSql = "";
	        String[] arrData = null;
	        boolean bTrans = false; //代表是否开始了事务
	        Connection conn = dbl.loadConnection();

	        try {
	            conn.setAutoCommit(false);
	            bTrans = true;
	            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
	            if (sRecycled != "" && sRecycled != null) {
	                arrData = sRecycled.split("\r\n");
	                for (int i = 0; i < arrData.length; i++) {
	                    if (arrData[i].length() == 0) {
	                        continue;
	                    }
	                    this.parseRowStr(arrData[i]);
	                    strSql = "update " + pub.yssGetTableName("tb_para_SecurityLend") +
	                        " set FCheckState = " +
	                        this.checkStateId + ", FCheckUser = " +
	                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
	                        YssFun.formatDatetime(new java.util.Date()) + "'" +
	                        " where  FSecurityCode="+dbl.sqlString(this.securityCode)+
	                        " and FBrokerCode="+dbl.sqlString(this.brokerCode);;

	                        dbl.executeSql(strSql);
	                    //执行sql语句
	                    dbl.executeSql(strSql);
	                }

	            }
	            //sRecycled如果sRecycled为空，而feeCode不为空，则按照feeCode来执行sql语句
	            else if ((startDate != null&&startDate != "" )&&(securityCode != null&&securityCode != "" )) {
	                strSql = "update " + pub.yssGetTableName("tb_para_SecurityLend") +
	                    " set FCheckState = " +
	                    this.checkStateId + ", FCheckUser = " +
	                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
	                    YssFun.formatDatetime(new java.util.Date()) + "'" +
	                    " where and FSecurityCode="+dbl.sqlString(this.securityCode)+
	                    " and FBrokerCode="+dbl.sqlString(this.brokerCode);
	                //执行sql语句
	                dbl.executeSql(strSql);

	            }
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);

	        }

	        catch (Exception e) {
	            throw new YssException("审核证券借贷设置信息出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }
	//---------------------------------
	    }
	 /**
	     * 从回收站删除数据，即是彻底删除
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
	                    strSql = "delete from " + pub.yssGetTableName("tb_para_SecurityLend") +
	                    " where  FSecurityCode="+dbl.sqlString(this.securityCode)+
	                    " and FBrokerCode="+dbl.sqlString(this.brokerCode);;

	                    dbl.executeSql(strSql);
	                }

	            }
	            //sRecycled如果sRecycled为空，而feeCode不为空，则按照feeCode来执行sql语句
	            else if ((startDate != null&&startDate != "" )&&(securityCode != null&&securityCode != "" )) {
	                strSql = "delete from " + pub.yssGetTableName("tb_para_SecurityLend") +
	                " where  FSecurityCode="+dbl.sqlString(this.securityCode)
                    +" and FBrokerCode="+dbl.sqlString(this.brokerCode);;
	                dbl.executeSql(strSql);
	            }
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
	        }

	        catch (Exception e) {
	            throw new YssException("清除数据出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }

	    }
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#getAllSetting()
	 */
	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#getSetting()
	 */
	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#saveMutliSetting(java.lang.String)
	 */
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssLogData#getBeforeEditData()
	 */
	public String getBeforeEditData() throws YssException {
		SecurityLendBean sb=new SecurityLendBean();
		String strSql = "";
		ResultSet rs = null;
		try{
			 strSql ="select * from (select a.*, b.*,c.FCheckUserName,d.FBrokerName,e.FPeriodName,f.FRoundName,g.FSecurityName  from "+pub.yssGetTableName("tb_para_SecurityLend")+" a "+
             " left join(select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode "+
             " left join(select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList ) c on a.FCheckUser=c.FUserCode"+
             " left join(select FBrokerCode ,FBrokerName from "+pub.yssGetTableName("tb_para_Broker")+" where FCheckState=1) d  on d.FBrokerCode =a.FBrokerCode "+
             " left join(select FPeriodCode,FPeriodName from "+pub.yssGetTableName("Tb_Para_Period")+")e on e.FPeriodCode=a.FPeriodCode "+
             " left join(select FRoundCode,FRoundName from "+pub.yssGetTableName("Tb_Para_Rounding")+")f on f.FRoundCode=a.FRoundCode" +
             " left join(select FSecurityCode,FSecurityName from "+pub.yssGetTableName("Tb_Para_Security")+" where FCheckState=1 ) g on g.FSecurityCode=a.FSecurityCode) "+                   
             " where FSecurityCode="+dbl.sqlString(this.oldsecurityCode)+" and FBrokerCode="+dbl.sqlString(this.oldBrokerCode);; 
			 rs = dbl.openResultSet(strSql);
			   while (rs.next()) {
				   sb.securityCode = rs.getString("FSecurityCode") + "";
				   sb.securityName = rs.getString("FSecurityName") + "";
				   sb.brokerCode=rs.getString("FBrokerCode")+"";
				   sb.brokerName=rs.getString("FBrokerName")+"";
				   sb.startDate = rs.getString("FStartDate") + "";
				   sb.strPeriodCode=rs.getString("FPeriodCode")+"";
				   sb.strPeriodName=rs.getString("FPeriodName")+"";
				   sb.strRoundCode=rs.getString("FRoundCode")+"";
				   sb.strRoundName=rs.getString("FRoundName")+"";
			   }
		}catch(Exception e){
			throw new YssException(e.getMessage());
		}finally {
			dbl.closeResultSetFinal(rs); // 
			
		}
		return sb.buildRowStr();
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssConvert#getOperValue(java.lang.String)
	 */
	public String getOperValue(String sType) throws YssException {
		if (sType.equalsIgnoreCase("getSettleDate")) {
            return this.getSettleDate();
        }
		return null;
	}
/**
 * 
 * @throws YssException 
 * @方法名：getSettleDate
 * @参数：
 * @返回类型：String
 * @说明：获取证券借贷交易数据的最大结算日期
 */
  public String getSettleDate() throws YssException{
	  String sDate="";
	  String  strSql="";
	  ResultSet rs=null;
	  try{
		  strSql=" select max(FSETTLEDATE) as FSETTLEDATE from  "+pub.yssGetTableName("TB_DATA_SecLendTRADE") +
		         " where FCheckState=1 and  FSECURITYCODE="+dbl.sqlString(this.securityCode)+
		         " and FBrokerCode="+dbl.sqlString(this.brokerCode);
		  rs=dbl.openResultSet(strSql);
		  while(rs.next()){
			  sDate=rs.getDate("FSETTLEDATE")+"";
		  }
		  return sDate;
	  } catch (Exception e) {
          throw new YssException("获取证券借贷交易数据的最大结算日期出错", e);
      } finally {
          dbl.closeResultSetFinal(rs);
      }
	  
  }


	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData2()
	 */
	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData3()
	 */
	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData4()
	 */
	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData1()
	 */
	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData2()
	 */
	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData3()
	 */
	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData4()
	 */
	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData5()
	 */
	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData1()
	 */
	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData2()
	 */
	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData3()
	 */
	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData1()
	 */
	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData2()
	 */
	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData3()
	 */
	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public String getBrokerCode() {
		return brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStrPeriodCode() {
		return strPeriodCode;
	}

	public void setStrPeriodCode(String strPeriodCode) {
		this.strPeriodCode = strPeriodCode;
	}

	public String getStrPeriodName() {
		return strPeriodName;
	}

	public void setStrPeriodName(String strPeriodName) {
		this.strPeriodName = strPeriodName;
	}

	public String getStrRoundCode() {
		return strRoundCode;
	}

	public void setStrRoundCode(String strRoundCode) {
		this.strRoundCode = strRoundCode;
	}

	public String getStrRoundName() {
		return strRoundName;
	}

	public void setStrRoundName(String strRoundName) {
		this.strRoundName = strRoundName;
	}

	public String getOldsecurityCode() {
		return oldsecurityCode;
	}

	public void setOldsecurityCode(String oldsecurityCode) {
		this.oldsecurityCode = oldsecurityCode;
	}

	public String getOldstartDate() {
		return oldstartDate;
	}

	public void setOldstartDate(String oldstartDate) {
		this.oldstartDate = oldstartDate;
	}

	public SecurityLendBean getFilterType() {
		return filterType;
	}

	public void setFilterType(SecurityLendBean filterType) {
		this.filterType = filterType;
	}
	public String getOldBrokerCode() {
		return oldBrokerCode;
	}
	public void setOldBrokerCode(String oldBrokerCode) {
		this.oldBrokerCode = oldBrokerCode;
	}

}
