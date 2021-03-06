/**
 * 
 */
package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.storagemanage.CashStorageBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * @包名：com.yss.main.operdata
 * @文件名：CollateralAddBean.java
 * @创建人：zhangfa
 * @创建时间：2010-11-2
 * @版本号：0.1
 * @说明：TODO <P>
 * @修改记录 日期 | 修改人 | 版本 | 说明<br>
 *       ----------------------------------------------------------------<br>
 *       2010-11-2 | zhangfa | 0.1 |
 */
public class CollateralAddBean extends BaseDataSettingBean implements
		IDataSetting {
	private String collateralCode = "";// 抵押物代码
	private String collateralName = "";// 抵押物名称
	private String transferDate = "1900-01-01"; // 调拨日期
	private String transferTime = "00:00:00"; // 调拨时间
	private String transferType = "";// 调拨类型
	private String portCode = ""; // 组合代码
	private String portName = ""; // 组合名称
	private String inOut = "1";//1存入;-1取出
	private CollateralAddBean filterType;
	private String collateralAcc = "";
	private String collateralSec = "";
	private String sRecycled = "";

	private String oldcollateralCode = "";
	private String oldtransferDate = "1900-01-01";
	private String oldInOut="1";
	
	private String oldtransferType="";
	
	
	
	public String checkPort() throws YssException{
		String temp="";
		String strSql = "";
        ResultSet rs = null;
		try{
			strSql=" select FCollateralCode,FPORTCODE from "+pub.yssGetTableName("tb_Data_CollateralAdd")+
			       " where FPORTCODE="+dbl.sqlString(this.portCode) +" and FTransferType='组合'"+
			       " and FInOut=1";//+this.inOut;+" and FCheckState=1"
			rs=dbl.openResultSet(strSql);
			while(rs.next()){
				temp="true";
				
			}
			return temp;
		}catch (Exception e) {
			throw new YssException("检查组合出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}
	
	
	   public String getListViewData1() throws YssException {

	        String sHeader = "";
	        String sShowDataStr = "";
	        String sAllDataStr = "";
	        String sDateStr = "";
	        String sVocStr = "";
	        String inOutVoc="";
	        String strSql = "";
	        ResultSet rs = null;
	        StringBuffer bufShow = new StringBuffer();
	        StringBuffer bufAll = new StringBuffer();
	        try {
	            sHeader = this.getListView1Headers();
	            strSql=" select * from (select a.*,i.FCollateralName,l.FCreatorName,m.FCheckUserName,e.FPortName from "+pub.yssGetTableName("tb_Data_CollateralAdd")+" a "+
	                   " left join (select FCollateralCode , FCollateralName  from "+pub.yssGetTableName("tb_para_Collateral")+"   where FCheckState=1) i on i.FCollateralCode=a.FCollateralCode"+	
	                   " left join (select FPortCode, FPortName  from  "+pub.yssGetTableName("Tb_Para_Portfolio")+" where FCheckState=1)e on e.FPortCode=a.FPortCode"+
	                   " left join(select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) l on a.FCreator=l.FUserCode"+
	            	    " left join(select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList ) m on a.FCheckUser=m.FUserCode ) "+                   
	                   buildFilterSql(); 
	            rs=dbl.openResultSet(strSql);
	            while(rs.next()){
	            	 bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
	                    append(YssCons.YSS_LINESPLITMARK);
	            	 this.collateralCode=rs.getString("FCollateralCode");
	            	 this.collateralName=rs.getString("FCollateralName");
	            	 this.transferDate=rs.getDate("FTransferDATE")+"";
	            	 this.transferTime=rs.getString("FTransferTIME");
	            	 this.transferType=rs.getString("FTransferType");
	            	 this.portCode=rs.getString("FPORTCODE");
	            	 this.portName=rs.getString("FPortName");
	            	 this.inOut=rs.getInt("FInOut")+"";
	            	 
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
			//VocabularyBean inOutVocabulary=new VocabularyBean();
			vocabulary.setYssPub(pub);
			//inOutVocabulary.setYssPub(pub);
			
			//inOutVoc=inOutVocabulary.getVoc(YssCons.YSS_DATA_TransferInOut);
			sVocStr = vocabulary.getVoc(YssCons.YSS_DARA_TransferType+ "," +YssCons.YSS_DATA_TransferInOut);
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc"
					+ sVocStr;
		} catch (Exception e) {
			throw new YssException("获取抵押物补交数据出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";

            if (this.filterType.collateralCode.length() != 0) {
                sResult = sResult + " and FCollateralCode like '" +
                    filterType.collateralCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.collateralName.length() != 0) {
                sResult = sResult + " and FCollateralName like '" +
                    filterType.collateralName.replaceAll("'", "''") + "%'";
            }
            /**
            if (this.filterType.transferDate.length() != 0) {
                sResult = sResult + " and FTransferDATE like '" +
                    filterType.transferDate.replaceAll("'", "''") + "%'";
            }
            */
            if (this.filterType.transferDate.length() != 0 &&
                    !this.filterType.transferDate.equals("9998-12-31")) {
                    sResult = sResult + " and FTransferDATE = " +
                        dbl.sqlDate(filterType.transferDate);
                }
            if (this.filterType.transferType.length() != 0) {
                sResult = sResult + " and FTransferType  ='" +
                    filterType.transferType.replaceAll("'", "''") + "'";
            }
            
            if (this.filterType.portCode.length() != 0) {
                sResult = sResult + " and FPORTCODE like '" +
                    filterType.portCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.inOut.length() != 0) {
                sResult = sResult + " and FInOut = " +filterType.inOut;
                    
            }
           
        }
        return sResult;
    }
	
	
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkInput(byte)
	 */
	public void checkInput(byte btOper) throws YssException {
		try{
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("tb_Data_CollateralAdd"),
                "FCollateralCode,FTransferDATE,FInOut,FTransferType",
                this.collateralCode+","+this.transferDate+","+this.inOut+","+this.transferType,
                this.oldcollateralCode+","+this.oldtransferDate+","+this.oldInOut+","+this.oldtransferType);
		}catch(Exception e){
			String temp="";
			if(e.getMessage().indexOf(",1")!=-1){
				temp=e.getMessage().replaceAll(",1,", ",存入,");
				throw new YssException(temp);
			}else if(e.getMessage().indexOf(",-1,")!=-1){
				temp=e.getMessage().replaceAll(",-1,", ",取出,");
				throw new YssException(temp);
			}else{
				 throw new YssException(e.getMessage());
			}
			
			
		}
		
	}
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#deleteRecycleData()
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = "";
		String accSql="";
		String secSql="";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
        	accSql=" delete from "+ pub.yssGetTableName("tb_Data_CollateralAcc") +
 	               " where FCollateralCode="+dbl.sqlString(this.collateralCode)+
 	               " and FTransferDATE="+dbl.sqlString(this.transferDate)+
 	               " and FInOut="+this.inOut;
        	secSql=" delete from "+ pub.yssGetTableName("tb_Data_CollateralSec") +
 	       		   " where FCollateralCode="+dbl.sqlString(this.collateralCode)+
 	       		   " and FTransferDATE="+dbl.sqlString(this.transferDate)+
 	       		   " and FInOut="+this.inOut;
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
                    strSql = "delete from " + pub.yssGetTableName("tb_Data_CollateralAdd") +
                    " where FCollateralCode="+dbl.sqlString(this.collateralCode)+
          		    " and FTransferDATE="+dbl.sqlDate(this.transferDate)+
          		    " and FInOut="+this.inOut+
          		    " and FTransferType="+dbl.sqlString(this.transferType);
                    
                    if(transferType.equals("现金")){
                    	dbl.executeSql(accSql);
                    }else if(transferType.equals("证券")){
                    	 dbl.executeSql(secSql);
                    }
                    
                    dbl.executeSql(strSql);
                    
                }

            }
            else if ((collateralCode != null&&collateralCode != "")&&(this.transferDate!=null&&this.transferDate.length()>0)) {
                strSql = "delete from " + pub.yssGetTableName("tb_Data_CollateralAdd") +
                	     " where FCollateralCode="+dbl.sqlString(this.collateralCode)+
                	     " and FTransferDATE="+dbl.sqlDate(this.transferDate)+
                	     " and FInOut="+this.inOut+
                	     " and FTransferType="+dbl.sqlString(this.transferType);
                if(transferType.equals("现金")){
                	dbl.executeSql(accSql);
                }else if(transferType.equals("证券")){
                	 dbl.executeSql(secSql);
                }
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("清除抵押物补交数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
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
                    strSql = "update " + pub.yssGetTableName("tb_Data_CollateralAdd") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FCollateralCode="+dbl.sqlString(this.collateralCode)+
             		   " and FTransferDATE="+dbl.sqlDate(this.transferDate)+
             		   " and FInOut="+this.inOut+
             		   " and FTransferType="+dbl.sqlString(this.transferType);
                      

                        dbl.executeSql(strSql);
                    //执行sql语句
                  //  dbl.executeSql(strSql);
                }

            }

            else if ((collateralCode != null&&collateralCode != "")&&(this.transferDate!=null&&this.transferDate.length()>0) )
            		 {
                strSql = "update " + pub.yssGetTableName("tb_Data_CollateralAdd") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    " where FCollateralCode="+dbl.sqlString(this.collateralCode)+
          		    " and FTransferDATE="+dbl.sqlDate(this.transferDate)+
          		    " and FInOut="+this.inOut+
          		    " and FTransferType="+dbl.sqlString(this.transferType);
                //执行sql语句
                dbl.executeSql(strSql);

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
            throw new YssException("审核抵押物补交数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
//---------------------------------
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IDataSetting#editSetting()
	 */
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try{
        	strSql=" update "+ pub.yssGetTableName("tb_Data_CollateralAdd") +
        		   " set FCheckState = " + this.checkStateId+ 
        		   " where FCollateralCode="+dbl.sqlString(this.collateralCode)+
        		   " and FTransferDATE="+dbl.sqlDate(this.transferDate)+
        		   " and FInOut="+this.inOut+
        		   " and FTransferType="+dbl.sqlString(this.transferType);
        		   
        	
        	conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
        	 conn.commit();
             bTrans = false;
             conn.setAutoCommit(true); 
        		   
        	
        }catch (Exception e) {
            throw new YssException("删除抵押物补交数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IDataSetting#editSetting()
	 */
	public String editSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        CollateralAccBean collateralacc=new CollateralAccBean();
        CollateralSecBean collateralsec=new CollateralSecBean();
        try{
        	strSql=" update "+ pub.yssGetTableName("tb_Data_CollateralAdd") +
        		   " set FCollateralCode="+dbl.sqlString(this.collateralCode)+","+
        		   " FTransferDATE="+dbl.sqlDate(this.transferDate)+","+
        		   " FTransferTIME="+dbl.sqlString(this.transferTime)+","+
        		   " FTransferType="+dbl.sqlString(this.transferType)+","+
        		   " FPORTCODE="+dbl.sqlString(this.portCode)+","+
        		   " FInOut="+this.inOut+","+
        		   " FCheckState = " +(pub.getSysCheckState() ? "0" : "1") +","+
        		   " FCreator = " + dbl.sqlString(this.creatorCode) +","+
        		   " FCreateTime = " +dbl.sqlString(this.creatorTime) + ","+
        		   " FCheckUser = " + (pub.getSysCheckState() ? "' '" :dbl.sqlString(this.creatorCode)) +
        		   " where FCollateralCode="+dbl.sqlString(this.oldcollateralCode)+
        		   " and FTransferDATE="+dbl.sqlDate(this.oldtransferDate)+
        		   " and FInOut="+this.oldInOut+
        		   " and FTransferType="+dbl.sqlString(this.oldtransferType);
        		   
        	
        	conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
			if (transferType.equals("现金")) {
				//if (collateralAcc != null && collateralAcc.length() > 0) {
					collateralacc.setYssPub(pub);
					collateralacc.setTransferDate(this.transferDate);
					collateralacc.setCollateralCode(this.collateralCode);
					collateralacc.setInOut(this.inOut);
					collateralacc.saveMutliSetting(collateralAcc, true);
				//}
			}
			if (transferType.equals("证券")) {
				//if (collateralSec != null && collateralSec.length() > 0) {
					collateralsec.setYssPub(pub);
					collateralsec.setTransferDate(this.transferDate);
					collateralsec.setCollateralCode(this.collateralCode);
					collateralsec.setInOut(this.inOut);
					collateralsec.saveMutliSetting(collateralSec, true);
				//}
			}
        	 conn.commit();
             bTrans = false;
             conn.setAutoCommit(true); 
        		   
        	
        }catch (Exception e) {
            throw new YssException("修改抵押物补交数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		return null;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IDataSetting#addSetting()
	 */
	public String addSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        CollateralAccBean collateralacc=new CollateralAccBean();
        CollateralSecBean collateralsec=new CollateralSecBean();
        try{
        	strSql=" insert into "+ pub.yssGetTableName("tb_Data_CollateralAdd") +
        		   " (FCollateralCode,FTransferDATE,FTransferTIME,FTransferType,FPORTCODE,FInOut," +
        		   " FCheckState,FCreator,FCreateTime,FCheckUser)"+
        		   " values ("+dbl.sqlString(this.collateralCode)+","+
        		     dbl.sqlDate(this.transferDate)+","+
        		     dbl.sqlString(this.transferTime)+","+
        		     dbl.sqlString(this.transferType)+","+
        		     dbl.sqlString(this.portCode)+","+
        		     this.inOut+","+
        		     (pub.getSysCheckState() ? "0" : "1") + "," +
	                 dbl.sqlString(this.creatorCode) + "," +
	                 dbl.sqlString(this.creatorTime) + "," +
	                 (pub.getSysCheckState() ? "' '" :
	                 dbl.sqlString(this.creatorCode)) +" )" ;
        	
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
			if (transferType.equals("现金")) {
				//if (collateralAcc != null && collateralAcc.length() > 0) {
					collateralacc.setYssPub(pub);
					collateralacc.setCollateralCode(this.collateralCode);
					collateralacc.setTransferDate(this.transferDate);
					collateralacc.saveMutliSetting(collateralAcc, true);
				//}
			}
			if (transferType.equals("证券")) {
				//if (collateralSec != null && collateralSec.length() > 0) {
					collateralsec.setYssPub(pub);
					collateralsec.setTransferDate(this.transferDate);
					collateralsec.setCollateralCode(this.collateralCode);
					collateralsec.saveMutliSetting(collateralSec, true);
				//}
			}
        	 conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
        	
        } catch (Exception e) {
            throw new YssException("新增抵押物补交数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        
		return null;
	}


	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		buf.append(this.collateralCode).append("\t");
		buf.append(this.collateralName).append("\t");
		buf.append(this.transferDate).append("\t");
		buf.append(this.transferTime).append("\t");
		buf.append(this.transferType).append("\t");
		buf.append(this.portCode).append("\t");
		buf.append(this.portName).append("\t");
		buf.append(this.inOut).append("\t");
		buf.append(super.buildRecLog());
		return buf.toString();

	}

	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		String sTmpStr = "";
		try {
			if (sRowStr.trim().length() == 0) {
				return;
			}
			if (sRowStr.indexOf("\r\t") >= 0) {
				sTmpStr = sRowStr.split("\r\t")[0];
				if (sRowStr.split("\r\t").length >= 3) {
					this.collateralAcc = sRowStr.split("\r\t")[2];
				}
				if (sRowStr.split("\r\t").length >= 5) {
					this.collateralSec = sRowStr.split("\r\t")[4];
				}

			} else {
				sTmpStr = sRowStr;
			}
			this.sRecycled = sRowStr;

			reqAry = sTmpStr.split("\t");
			this.collateralCode = reqAry[0];
			this.collateralName = reqAry[1];
			this.transferDate = reqAry[2];
			this.transferTime = reqAry[3];
			this.transferType=reqAry[4];
			this.portCode = reqAry[5];
			this.portName = reqAry[6];
			this.oldcollateralCode = reqAry[7];
			this.oldtransferDate = reqAry[8];
			this.inOut=reqAry[9];
			this.oldInOut=reqAry[10];
			this.oldtransferType=reqAry[11];
			this.checkStateId = Integer.parseInt(reqAry[12]);

			super.parseRecLog();
			if (sRowStr.indexOf("\r\t") >= 0) {
				if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
					if (this.filterType == null) {
						this.filterType = new CollateralAddBean();
						this.filterType.setYssPub(pub);
					}
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			}

		} catch (Exception e) {
			throw new YssException("解析抵押物补交数据出错", e);
		}
	}






	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IDataSetting#getAllSetting()
	 */
	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IDataSetting#getSetting()
	 */
	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IDataSetting#saveMutliSetting(java.lang.String)
	 */
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IYssLogData#getBeforeEditData()
	 */
	public String getBeforeEditData() throws YssException {
		CollateralAddBean cb=new CollateralAddBean();
		String strSql = "";
		ResultSet rs = null;
		try{
			  strSql=" select * from (select a.*,i.FCollateralName,l.FCreatorName,m.FCheckUserName,e.FPortName from "+pub.yssGetTableName("tb_Data_CollateralAdd")+" a "+
              " left join (select FCollateralCode , FCollateralName  from "+pub.yssGetTableName("tb_para_Collateral")+"   where FCheckState=1) i on i.FCollateralCode=a.FCollateralCode"+	
              " left join (select FPortCode, FPortName  from  "+pub.yssGetTableName("Tb_Para_Portfolio")+" where FCheckState=1)e on e.FPortCode=a.FPortCode"+
              " left join(select FUserCode, FUserName as FCreatorName from Tb_Sys_UserList) l on a.FCreator=l.FUserCode"+
       	      " left join(select FUserCode, FUserName as FCheckUserName from Tb_Sys_UserList ) m on a.FCheckUser=m.FUserCode ) "+   
       	      " where FCollateralCode ="+dbl.sqlString(this.oldcollateralCode)+" and FTransferDATE="+dbl.sqlDate(this.oldtransferDate);
			  rs = dbl.openResultSet(strSql);
				while(rs.next()){
					cb.collateralCode=rs.getString("FCollateralCode");
					cb.collateralName=rs.getString("FCollateralName");
					cb.transferDate=rs.getDate("FTransferDATE")+"";
					cb.transferTime=rs.getString("FTransferTIME");
					cb.transferType=rs.getString("FTransferType");
					cb.portCode=rs.getString("FPORTCODE");
					cb.portName=rs.getString("FPortName");
				}
		}catch(Exception e){
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs); // 
			
		}
		return cb.buildRowStr();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IYssConvert#getOperValue(java.lang.String)
	 */
	public String getOperValue(String sType) throws YssException {
		if(sType.equalsIgnoreCase("checkPort")){
			return checkPort();
		}
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientListView#getListViewData2()
	 */
	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientListView#getListViewData3()
	 */
	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientListView#getListViewData4()
	 */
	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData1()
	 */
	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData2()
	 */
	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData3()
	 */
	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData4()
	 */
	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData5()
	 */
	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData1()
	 */
	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData2()
	 */
	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData3()
	 */
	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData1()
	 */
	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData2()
	 */
	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	public String getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(String transferDate) {
		this.transferDate = transferDate;
	}

	public String getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public CollateralAddBean getFilterType() {
		return filterType;
	}

	public void setFilterType(CollateralAddBean filterType) {
		this.filterType = filterType;
	}

	public String getCollateralAcc() {
		return collateralAcc;
	}

	public void setCollateralAcc(String collateralAcc) {
		this.collateralAcc = collateralAcc;
	}

	public String getCollateralSec() {
		return collateralSec;
	}

	public void setCollateralSec(String collateralSec) {
		this.collateralSec = collateralSec;
	}

	public String getsRecycled() {
		return sRecycled;
	}

	public void setsRecycled(String sRecycled) {
		this.sRecycled = sRecycled;
	}

	public String getOldcollateralCode() {
		return oldcollateralCode;
	}

	public void setOldcollateralCode(String oldcollateralCode) {
		this.oldcollateralCode = oldcollateralCode;
	}

	public String getOldtransferDate() {
		return oldtransferDate;
	}

	public void setOldtransferDate(String oldtransferDate) {
		this.oldtransferDate = oldtransferDate;
	}


	public String getInOut() {
		return inOut;
	}


	public void setInOut(String inOut) {
		this.inOut = inOut;
	}


	public String getOldInOut() {
		return oldInOut;
	}


	public void setOldInOut(String oldInOut) {
		this.oldInOut = oldInOut;
	}


	public String getOldtransferType() {
		return oldtransferType;
	}


	public void setOldtransferType(String oldtransferType) {
		this.oldtransferType = oldtransferType;
	}

}
