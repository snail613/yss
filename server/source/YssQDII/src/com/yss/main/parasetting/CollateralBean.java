/**
 * 
 */
package com.yss.main.parasetting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * @包名：com.yss.main.parasetting
 * @文件名：Collateral.java
 * @创建人：zhangfa
 * @创建时间：2010-10-23
 * @版本号：0.1
 * @说明：TODO
 * <P> 
 * @修改记录
 * 日期        |   修改人       |   版本         |   说明<br>
 * ----------------------------------------------------------------<br>
 * 2010-10-23 | Administrator | 0.1 |  
 */
public class CollateralBean extends BaseDataSettingBean implements IDataSetting{
	private String collateralCode="";//抵押物代码
	private String collateralName="";//抵押物名称
	private String collateralType="";//抵押物类型
	private String collateralSubType="";//抵押物子类型
	private String brokerCode="";//存管券商代码
	private String brokerName="";////存管券商名称
	private double collateralWarmVaule;//抵押物警戒值
	private String desc="";
	private String oldcollateralCode="";
	private CollateralBean filterType;
	private String sRecycled = "";
	

	/**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";

            if (this.filterType.collateralCode.length() != 0) {
            	//edit by songjie 2011.11.21 BUG 2923 QDV4赢时胜(测试)2011年10月11日05_B 支持模糊查询
                sResult = sResult + " and FCollateralCode like '" + filterType.collateralCode.replaceAll("'", "''") + "%'";
                   
            }
            if (this.filterType.collateralName.length() != 0) {
            	//edit by songjie 2011.11.21 BUG 2923 QDV4赢时胜(测试)2011年10月11日05_B 支持模糊查询
                sResult = sResult + " and FCollateralName like '" + filterType.collateralName.replaceAll("'", "''") + "%'";
                   
            }
            if (this.filterType.collateralType.length() != 0) {
            	//edit by songjie 2011.11.21 BUG 2923 QDV4赢时胜(测试)2011年10月11日05_B 支持模糊查询
                sResult = sResult + " and FCollateralType like '" + filterType.collateralType.replaceAll("'", "''") + "%'";
                   
            }
            if (this.filterType.collateralSubType.length() != 0) {
            	//edit by songjie 2011.11.21 BUG 2923 QDV4赢时胜(测试)2011年10月11日05_B 支持模糊查询
                sResult = sResult + " and FCollateralSubType like '" + filterType.collateralSubType.replaceAll("'", "''") + "%'";
                   
            }
            
            if (this.filterType.brokerCode.length() != 0) {
            	//edit by songjie 2011.11.21 BUG 2923 QDV4赢时胜(测试)2011年10月11日05_B 支持模糊查询
                sResult = sResult + " and FBrokerCode like '" + filterType.brokerCode.replaceAll("'", "''") + "%'";
                  
            }
           
            /**
            sResult = sResult + " and FCollateralWarmVaule =" +filterType.collateralWarmVaule;
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%' ";
            }
            */
        }
        return sResult;
    }
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#addSetting()
	 */
	public String addSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		 try {
	            strSql = "insert into " + pub.yssGetTableName("Tb_para_Collateral") +
	                " (FCollateralCode,FCollateralName ,FCollateralType ,FCollateralSubType ,FBrokerCode,FCollateralWarmVaule , " +
	                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
	                " values(" + dbl.sqlString(this.collateralCode) + "," +
	                dbl.sqlString(this.collateralName) + "," +
	                dbl.sqlString(this.collateralType) + "," +
	                dbl.sqlString(this.collateralSubType) + "," +
	                dbl.sqlString(this.brokerCode) + "," +
	                this.collateralWarmVaule + "," +
	                dbl.sqlString(this.desc) + "," +
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
	            throw new YssException("增加抵押物信息设置信息出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }
		return null;
	}
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#editSetting()
	 */
	public String editSetting() throws YssException {
		 String strSql = "";
	        boolean bTrans = false; //代表是否开始了事务
	        Connection conn = dbl.loadConnection();
	        try {
	            strSql = "update " + pub.yssGetTableName("Tb_para_Collateral") +
	                " set FCollateralCode = " +
	                dbl.sqlString(this.collateralCode) + ", FCollateralName = " +
	                dbl.sqlString(this.collateralName) + " ," +
	                " FCollateralType  = " + dbl.sqlString(this.collateralType) + "," +
	                " FCollateralSubType   = " + dbl.sqlString(this.collateralSubType) + "," +
	                " FBrokerCode   = " + dbl.sqlString(this.brokerCode) + "," +
	                " FCollateralWarmVaule    = " + this.collateralWarmVaule + "," +
	                   " FDesc = " +dbl.sqlString(this.desc) + ",FCheckState = " +
	                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
	                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
	                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
	                (pub.getSysCheckState() ? "' '" :
	                 dbl.sqlString(this.creatorCode)) +
	                " where FCollateralCode = " +
	                dbl.sqlString(this.oldcollateralCode)
	                ;
	            conn.setAutoCommit(false);
	            bTrans = true;
	            dbl.executeSql(strSql);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
	        }

	        catch (Exception e) {
	            throw new YssException("修改抵押物信息设置信息出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }

	        return null;
	}
	public void delSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "update " + pub.yssGetTableName("Tb_para_Collateral")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FCollateralCode = "
					+ dbl.sqlString(this.collateralCode)
					;
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		}

		catch (Exception e) {
			throw new YssException("删除抵押物信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkInput(byte)
	 */
	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("Tb_para_Collateral"),
                "FCollateralCode",
                this.collateralCode,
                this.oldcollateralCode);
		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkSetting()
	 */
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
                    strSql = "update " + pub.yssGetTableName("Tb_para_Collateral") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FCollateralCode = " +
                        dbl.sqlString(this.collateralCode)
                      ;

                        dbl.executeSql(strSql);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }

            }
            //sRecycled如果sRecycled为空，而feeCode不为空，则按照feeCode来执行sql语句
            else if (collateralCode != null&&collateralCode != "" )
            		 {
                strSql = "update " + pub.yssGetTableName("Tb_para_Collateral") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FCollateralCode = " +
                    dbl.sqlString(this.collateralCode);
                //执行sql语句
                dbl.executeSql(strSql);

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
            throw new YssException("审核抵押物信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
//---------------------------------
		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#delSetting()
	 */
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#deleteRecycleData()
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
                    strSql = "delete from " + pub.yssGetTableName("Tb_para_Collateral") +
                    " where FCollateralCode = " +
                    dbl.sqlString(this.collateralCode);

                    dbl.executeSql(strSql);
                }

            }
            //sRecycled如果sRecycled为空，而feeCode不为空，则按照feeCode来执行sql语句
            else if (collateralCode != null&&collateralCode != "") {
                strSql = "delete from " + pub.yssGetTableName("Tb_para_Collateral") +
                " where FCollateralCode = " +
                dbl.sqlString(this.collateralCode);
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
		CollateralBean cb=new CollateralBean();
		String strSql = "";
		ResultSet rs = null;
		try{
		strSql= "select * from "+
		        "(select c.*,d.*,e.FCheckUserName,b.FBrokerName from  "+pub.yssGetTableName("Tb_para_Collateral") +" c "+
		        " left join (select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) d on c.FCreator = d.FUserCode"+
		        " left join (select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList) e on c.FCheckUser = e.FUserCode"+
                " left join (select FBrokerCode ,FBrokerName from " +pub.yssGetTableName("Tb_Para_Broker")+
                
                " where FCheckState=1) b on b.FBrokerCode=c.FBrokerCode ) "+
                " where FCollateralCode="+dbl.sqlString(this.oldcollateralCode);
		rs = dbl.openResultSet(strSql);
		   while (rs.next()) {
			   cb.collateralCode = rs.getString("FCollateralCode");
			   cb.collateralName = rs.getString("FCollateralName");

			   cb.collateralType = rs.getString("FCollateralType");
			   cb.collateralSubType = rs.getString("FCollateralSubType");
			   cb.brokerCode = rs.getString("FBrokerCode");
			   cb.brokerName = rs.getString("FBrokerName");
			   cb.collateralWarmVaule = rs.getDouble("FCOLLATERALWARMVAULE");
				if (rs.getString("FDesc") != null) {
					cb.desc = rs.getString("FDesc");
				} else {
					cb.desc = "";
				}
				
		   }
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs); // 
			
		}
		return cb.buildRowStr();
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssConvert#buildRowStr()
	 */
	public String buildRowStr() throws YssException {
		   StringBuffer buf = new StringBuffer();
	        buf.append(this.collateralCode).append("\t");
	        buf.append(this.collateralName).append("\t");
	        buf.append(this.collateralType).append("\t");
	        buf.append(this.collateralSubType).append("\t");
	        buf.append(this.brokerCode).append("\t");
	        buf.append(this.brokerName).append("\t");
	        buf.append(this.collateralWarmVaule).append("\t");
	        buf.append(this.desc).append("\t");
	        buf.append(super.buildRecLog());
	        return buf.toString();
		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssConvert#getOperValue(java.lang.String)
	 */
	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssConvert#parseRowStr(java.lang.String)
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
           this.collateralCode=reqAry[0];
           this.collateralName=reqAry[1];
           this.collateralType=reqAry[2];
           this.collateralSubType=reqAry[3];
           this.brokerCode=reqAry[4];
           
           this.brokerName=reqAry[5];
           if(YssFun.isNumeric(reqAry[6])){
        	   this.collateralWarmVaule=YssFun.toDouble(reqAry[6]); 
           }
           
           this.oldcollateralCode=reqAry[7];
           //modify by zhangfa 20101013 MS01846    证券代码变更界面清清除还原报错    QDV4赢时胜(测试)2010年10月13日04_B
           if(reqAry[8]!=null){
        	   if(reqAry[8].indexOf("【Enter】")>=0){
        		   this.desc=reqAry[8].replaceAll("【Enter】","\r\n");
        	   }else{
        		   this.desc=reqAry[8];
        	   }  
           }
           //--------------------------------------------------------
           this.checkStateId = Integer.parseInt(reqAry[9]);
           
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CollateralBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
            }
        } catch (Exception e) {
            throw new YssException("解析抵押物信息设置请求出错", e);
        }
		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData1()
	 */
	public String getListViewData1() throws YssException {
		String strSql = "";
		strSql= "select * from "+
		        "(select c.*,d.*,e.FCheckUserName,b.FBrokerName from  "+pub.yssGetTableName("Tb_para_Collateral") +" c "+
		        " left join (select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) d on c.FCreator = d.FUserCode"+
		        " left join (select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList) e on c.FCheckUser = e.FUserCode"+
                " left join (select FBrokerCode ,FBrokerName from " +pub.yssGetTableName("Tb_Para_Broker")+
                
                " where FCheckState=1) b on b.FBrokerCode=c.FBrokerCode ) "
                +buildFilterSql();
		return this.builderListViewData(strSql);
	}
	public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_PARA_CollateralType + "," + YssCons.YSS_PARA_CollateralSubType);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取抵押物信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	
	public void setSecurityAttr(ResultSet rs) throws SQLException {
        this.collateralCode = rs.getString("FCollateralCode");
        this.collateralName = rs.getString("FCollateralName");

        this.collateralType = rs.getString("FCollateralType");
        this.collateralSubType = rs.getString("FCollateralSubType");
        this.brokerCode = rs.getString("FBrokerCode");
        this.brokerName = rs.getString("FBrokerName");
        this.collateralWarmVaule = rs.getDouble("FCOLLATERALWARMVAULE");
        if (rs.getString("FDesc") != null) {
            this.desc = rs.getString("FDesc");
        } else {
            this.desc = "";
        }
        super.setRecLog(rs);
    }
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData2()
	 */
	public String getListViewData2() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		String strSql = "";
		try {
			sHeader="抵押物代码\t抵押物名称";
			strSql="select FCollateralCode,FCollateralName from "+pub.yssGetTableName("tb_para_Collateral")+
			       " where FCHECKSTATE =1";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append((rs.getString("FCollateralCode") + "").trim())
				.append("\t");
				bufShow.append((rs.getString("FCollateralName") + "").trim())
				.append(YssCons.YSS_LINESPLITMARK);
				
				  this.collateralCode = rs.getString("FCollateralCode");
			      this.collateralName = rs.getString("FCollateralName");
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
			
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
		} catch (Exception e) {
            throw new YssException("获取抵押物信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        
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

	public String getCollateralCode() {
		return collateralCode;
	}

	public void setCollateralCode(String collateralCode) {
		this.collateralCode = collateralCode;
	}

	public String getCollateralName() {
		return collateralName;
	}

	public void setCollateralName(String collateralName) {
		this.collateralName = collateralName;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public String getCollateralSubType() {
		return collateralSubType;
	}

	public void setCollateralSubType(String collateralSubType) {
		this.collateralSubType = collateralSubType;
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

	public double getCollateralWarmVaule() {
		return collateralWarmVaule;
	}

	public void setCollateralWarmVaule(double collateralWarmVaule) {
		this.collateralWarmVaule = collateralWarmVaule;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOldcollateralCode() {
		return oldcollateralCode;
	}

	public void setOldcollateralCode(String oldcollateralCode) {
		this.oldcollateralCode = oldcollateralCode;
	}

}
