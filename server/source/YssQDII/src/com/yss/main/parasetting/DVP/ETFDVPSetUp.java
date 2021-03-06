/**@author shashijie
*  @version 创建时间：2012-7-9 上午11:18:17 STORY 2727
*  类说明
*/
package com.yss.main.parasetting.DVP;

import java.sql.Connection;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * @author shashijie STORY 2727
 *
 */
public class ETFDVPSetUp 
	extends BaseDataSettingBean implements IDataSetting{
	
	private String FPortCode = " ";//组合代码
	private String Fportname = " ";
	private String FTradeTypeCode = " ";//交易类型代码
	private String Ftradetypename = " ";
	private String FExchangeCode = " ";//交易所代码
	private String Fexchangename = " ";
	private String FCategoryCode = " ";//品种类型代码
	private String Fcatname = " ";
	private String FSubCatCode = " ";//品种子类型代码
	private String Fsubcatname = " ";
	private String FCashCode = " ";//银行存款账户
	private String Fcashaccname = " ";
	private String FHolidaysCode = " ";//节假日群代码
	private String Fholidaysname = " ";
	private int FDVPSettleOver = 0;//延迟天数
	private String FStartDate = "1900-01-01";//启用日期
	private String FDesc = " ";//描述
	
	
	private String oldFPortCode = " ";//组合代码
	private String oldFTradeTypeCode = " ";//交易类型代码
	private String oldFExchangeCode = " ";//交易所代码
	private String oldFCategoryCode = " ";//品种类型代码
	private String oldFSubCatCode = " ";//品种子类型代码
	private String oldFCashCode = " ";//银行存款账户
	private String oldFStartDate = "1900-01-01";//启用日期
	
	private ETFDVPSetUp filterType = null;
	
	private String sRecycled = ""; //保存未解析前的字符串
	
	

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_para_DVPBusSet") +
                " (FPortCode," +
                " FTradeTypeCode," +
                " FExchangeCode," +
                " FCategoryCode," +
                " FSubCatCode," +
                " FCashCode," +
                " FHolidaysCode," +
                " FDVPSettleOver," +
                " FStartDate," +
                " FDesc," +
                " FCHECKSTATE, FCREATOR, FCREATETIME,FCheckUser "+
                " ) values( " +
                dbl.sqlString(FPortCode)+"," +
        		dbl.sqlString(FTradeTypeCode)+"," +
				dbl.sqlString(FExchangeCode)+"," +
				dbl.sqlString(FCategoryCode)+"," +
				dbl.sqlString(FSubCatCode)+"," +
				dbl.sqlString(FCashCode)+"," +
				dbl.sqlString(FHolidaysCode)+"," +
				FDVPSettleOver+"," +
				dbl.sqlDate(YssFun.toDate(FStartDate))+"," +
				dbl.sqlString(FDesc)+"," +
				
				(pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                 
                " )";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加DVP设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
	}

	/**shashijie 2012-7-9 STORY 2727
	 * @param btOper
	 * @throws YssException
	 */
	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper, 
			pub.yssGetTableName("Tb_Para_DVPBusSet"),
            "FPortCode,FTradeTypeCode,FExchangeCode,FCategoryCode,FSubCatCode,FStartDate",
            this.FPortCode+","+this.FTradeTypeCode+","+this.FExchangeCode+","+
            	this.FCategoryCode+","+this.FSubCatCode+","+this.FStartDate,
            this.oldFPortCode+","+this.oldFTradeTypeCode+","+this.oldFExchangeCode+","+
            	this.oldFCategoryCode+","+this.oldFSubCatCode+","+this.oldFStartDate);
	}

	/**shashijie 2012-7-9 STORY 2727
	 * @throws YssException
	 */
	public void checkSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
        	conn.setAutoCommit(false);
            bTrans = true;
        	//批量还原
        	if (sRecycled != null && !sRecycled.trim().equals("")) {
        		String[] arrData = sRecycled.split("\r\n");
        		
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = getUpdateStart();
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核DVP业务设置出错!", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	/**shashijie 2012-7-12 STORY 2727
	* @return*/
	private String getUpdateStart() throws YssException {
		String strSql = "update " + pub.yssGetTableName("Tb_Para_DVPBusSet") +
	        " set FCheckState = " + this.checkStateId +","+
	        " FCheckUser = " + dbl.sqlString(pub.getUserCode()) +","+
	        " FCheckTime = " +dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
	        " Where " +
	        " FPortCode = " + dbl.sqlString(this.oldFPortCode) +
	        " And FTradeTypeCode = " +dbl.sqlString(this.oldFTradeTypeCode) +
	        " And FExchangeCode = " +dbl.sqlString(this.oldFExchangeCode) +
	        " And FCategoryCode = " +dbl.sqlString(this.oldFCategoryCode) +
	        " And FSubCatCode = " +dbl.sqlString(this.oldFSubCatCode) +
	        //" And FCashCode = " +dbl.sqlString(this.oldFCashCode) +
	        " And FStartDate = " +dbl.sqlDate(YssFun.toDate(this.oldFStartDate));
		return strSql;
	}

	/**shashijie 2012-7-9 STORY 2727删除数据，即放入回收站
	 * @throws YssException
	 */
	public void delSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_DVPBusSet") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' Where " +
                " FPortCode = " + dbl.sqlString(this.oldFPortCode) +
                " And FTradeTypeCode = " +dbl.sqlString(this.oldFTradeTypeCode) +
                " And FExchangeCode = " +dbl.sqlString(this.oldFExchangeCode) +
                " And FCategoryCode = " +dbl.sqlString(this.oldFCategoryCode) +
                " And FSubCatCode = " +dbl.sqlString(this.oldFSubCatCode) +
                //" And FCashCode = " +dbl.sqlString(this.oldFCashCode) +
                " And FStartDate = " +dbl.sqlDate(YssFun.toDate(this.oldFStartDate));
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	/**shashijie 2012-7-9 STORY 2727 从回收站删除数据，即从数据库彻底删除数据
	 * @throws YssException
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
        	//批量删除
        	if (sRecycled != null && !sRecycled.trim().equals("")) {
        		String[] arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = getDeleteSql();
                    //执行sql语句
                    dbl.executeSql(strSql);
                }

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	/**shashijie 2012-7-12 STORY 2727
	* @return*/
	private String getDeleteSql() throws YssException {
		String strSql = "delete from " +
                pub.yssGetTableName("Tb_Para_DVPBusSet") +
                " where " +
                " FPortCode = " + dbl.sqlString(this.oldFPortCode) +
                " And FTradeTypeCode = " +dbl.sqlString(this.oldFTradeTypeCode) +
                " And FExchangeCode = " +dbl.sqlString(this.oldFExchangeCode) +
                " And FCategoryCode = " +dbl.sqlString(this.oldFCategoryCode) +
                " And FSubCatCode = " +dbl.sqlString(this.oldFSubCatCode) +
                //" And FCashCode = " +dbl.sqlString(this.oldFCashCode) +
                " And FStartDate = " +dbl.sqlDate(YssFun.toDate(this.oldFStartDate));
		return strSql;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_DVPBusSet") +
                " set " +
                " FPortCode = " + dbl.sqlString(this.FPortCode) +","+
                " FTradeTypeCode = " +dbl.sqlString(this.FTradeTypeCode) +","+
                " FExchangeCode = " +dbl.sqlString(this.FExchangeCode) +","+
                " FCategoryCode = " +dbl.sqlString(this.FCategoryCode) +","+
                " FSubCatCode = " +dbl.sqlString(this.FSubCatCode) +","+
                //edit by songjie 2012.11.19 现金账户代码不能修改 因为注释了现金账户相关的代码，现已取消注释
                " FCashCode = " +dbl.sqlString(this.FCashCode) +","+
                " FHolidaysCode = " +dbl.sqlString(this.FHolidaysCode) +","+
                " FDVPSettleOver = " + this.FDVPSettleOver +","+
                " FStartDate = " +dbl.sqlDate(YssFun.toDate(this.FStartDate)) +","+
                " FDesc = "+dbl.sqlString(this.FDesc) +","+
                " FCHECKSTATE = " + (pub.getSysCheckState() ? "0" : "1") +","+
                " FCreator = " + dbl.sqlString(this.creatorCode) +","+
                " FCreateTime = " + dbl.sqlString(this.creatorTime) +","+
                " FCheckUser = " + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                
                " Where " +
                " FPortCode = " + dbl.sqlString(this.oldFPortCode) +
                " And FTradeTypeCode = " +dbl.sqlString(this.oldFTradeTypeCode) +
                " And FExchangeCode = " +dbl.sqlString(this.oldFExchangeCode) +
                " And FCategoryCode = " +dbl.sqlString(this.oldFCategoryCode) +
                " And FSubCatCode = " +dbl.sqlString(this.oldFSubCatCode) +
                //edit by songjie 2012.11.19 现金账户代码不能修改 因为注释了现金账户相关的代码，现已取消注释
                " And FCashCode = " +dbl.sqlString(this.oldFCashCode) +
                " And FStartDate = " +dbl.sqlDate(YssFun.toDate(this.oldFStartDate));
                
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改DVP业务设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getAllSetting() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public IDataSetting getSetting() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @param sMutilRowStr
	 * @return
	 * @throws YssException
	 */
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getBeforeEditData() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 2727
	 * @return
	 * @throws YssException
	 */
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		
		buf.append(this.FPortCode);//组合代码
		buf.append("\t");
		buf.append(this.Fportname);
		buf.append("\t");
		buf.append(this.FTradeTypeCode);//交易类型代码
		buf.append("\t");
		buf.append(this.Ftradetypename);
		buf.append("\t");
		buf.append(this.FExchangeCode);//交易所代码
		buf.append("\t");
		buf.append(this.Fexchangename);
		buf.append("\t");
		buf.append(this.FCategoryCode);//品种类型代码
		buf.append("\t");
		buf.append(this.Fcatname);
		buf.append("\t");
		buf.append(this.FSubCatCode);//品种子类型代码
		buf.append("\t");
		buf.append(this.Fsubcatname);
		buf.append("\t");
		buf.append(this.FCashCode);//银行存款账户
		buf.append("\t");
		buf.append(this.Fcashaccname);
		buf.append("\t");
		buf.append(this.FHolidaysCode);//节假日群代码
		buf.append("\t");
		buf.append(this.Fholidaysname);
		buf.append("\t");
		buf.append(this.FDVPSettleOver);//延迟天数
		buf.append("\t");
		buf.append(this.FStartDate);//启用日期
		buf.append("\t");
		buf.append(this.FDesc);//描述
		buf.append("\t");
    	
		buf.append(this.oldFPortCode);//组合代码
		buf.append("\t");
		buf.append(this.oldFTradeTypeCode);//交易类型代码
		buf.append("\t");
		buf.append(this.oldFExchangeCode);//交易所代码
		buf.append("\t");
		buf.append(this.oldFCategoryCode);//品种类型代码
		buf.append("\t");
		buf.append(this.oldFSubCatCode);//品种子类型代码
		buf.append("\t");
		buf.append(this.oldFCashCode);//银行存款账户
		buf.append("\t");
		buf.append(this.oldFStartDate);
		buf.append("\t");
		
		buf.append(super.buildRecLog());
        return buf.toString();
        
        
	}

	
	/**shashijie 2012-7-9 STORY 2727
	* @return
	* @throws YssException*/
	private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                if (this.filterType.FPortCode.trim().length() != 0) {
                    sResult = sResult + " and a.FPortCode like '" +
                        filterType.FPortCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.FTradeTypeCode.trim().length() != 0) {
                    sResult = sResult + " and a.FTradeTypeCode like '" +
                        filterType.FTradeTypeCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.FExchangeCode.trim().length() != 0) {
                    sResult = sResult + " and a.FExchangeCode like '" +
                        filterType.FExchangeCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.FCategoryCode.trim().length() != 0) {
                    sResult = sResult + " and a.FCategoryCode like '" +
                        filterType.FCategoryCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.FSubCatCode.trim().length() != 0) {
                    sResult = sResult + " and a.FSubCatCode like '" +
                        filterType.FSubCatCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.FCashCode.trim().length() != 0) {
                    sResult = sResult + " and a.FCashCode like '" +
                        filterType.FCashCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.FHolidaysCode.trim().length() != 0) {
                    sResult = sResult + " and a.FHolidaysCode like '" +
                        filterType.FHolidaysCode.replaceAll("'", "''") + "%'";
                }
                if(this.filterType.FDVPSettleOver !=0 ){
                	sResult = sResult+"and a.FDVPSettleOver ='"+
                		filterType.FDVPSettleOver+"'";
                }
                if (this.filterType.FStartDate != null &&
                	!this.filterType.FStartDate.equals("1900-01-01") &&
                    !this.filterType.FStartDate.equals("9998-12-31")) {
                    sResult = sResult + " and a.FStartDate <= " +
                        dbl.sqlDate(filterType.FStartDate);
                }
                if (this.filterType.FDesc.trim().length() != 0) {
                    sResult = sResult + " and a.FDesc like " +
                        dbl.sqlString(filterType.FDesc);
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选DVP业务设置数据出错", e);
        }
        return sResult;
    }
	
	/**shashijie 2012-7-9 STORY 
	 * @param sType
	 * @return
	 * @throws YssException
	 */
	public String getOperValue(String sType) throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 2727
	 * @param sRowStr
	 * @throws YssException
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
        String sTmpStr = "";
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
            
            this.FPortCode = reqAry[0];//组合代码
            this.Fportname = reqAry[1];
        	this.FTradeTypeCode = reqAry[2];//交易类型代码
            this.Ftradetypename = reqAry[3];
        	this.FExchangeCode = reqAry[4];//交易所代码
            this.Fexchangename = reqAry[5];
        	this.FCategoryCode = reqAry[6];//品种类型代码
            this.Fcatname = reqAry[7];
        	this.FSubCatCode = reqAry[8];//品种子类型代码
            this.Fsubcatname = reqAry[9];
        	this.FCashCode = reqAry[10];//银行存款账户
            this.Fcashaccname = reqAry[11];
        	this.FHolidaysCode = reqAry[12];//节假日群代码
            this.Fholidaysname = reqAry[13];
        	this.FDVPSettleOver = YssFun.toInt(reqAry[14]);//延迟天数
        	this.FStartDate = reqAry[15];//启用日期
        	//描述
        	if(reqAry[16].indexOf("【Enter】") != -1){
            	this.FDesc = reqAry[16].replaceAll("【Enter】", "\r\n");
            }else{
            	this.FDesc = reqAry[16];
            }
        	
        	this.oldFPortCode = reqAry[17];//组合代码
        	this.oldFTradeTypeCode = reqAry[18];//交易类型代码
        	this.oldFExchangeCode = reqAry[19];//交易所代码
        	this.oldFCategoryCode = reqAry[20];//品种类型代码
        	this.oldFSubCatCode = reqAry[21];//品种子类型代码
        	this.oldFCashCode = reqAry[22];//现金账户代码
        	this.oldFStartDate = reqAry[23];//启用日期
            
            this.checkStateId = YssFun.toInt(reqAry[24]);

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ETFDVPSetUp();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析DVP业务设置出错", e);
        }
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getListViewData1() throws YssException {
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        
		ResultSet rs = null;
		try {
			sHeader = this.getListView1Headers();
			String query = getData1();
			rs = dbl.openResultSet(query);
			
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

            /*VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            String sVocStr = vocabulary.getVoc(YssCons.YSS_FUN_BailType + "," + YssCons.YSS_FUN_FUType);*/
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
		} catch (Exception e) {
			throw new YssException("获取DVP业务设置出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2012-7-9 STORY 2727
	* @param rs*/
	private void setSecurityAttr(ResultSet rs) throws Exception {
		this.FPortCode = rs.getString("FPortCode")==null?"":rs.getString("FPortCode");//组合代码
		this.Fportname = rs.getString("Fportname")==null?"":rs.getString("Fportname");
		this.FTradeTypeCode = rs.getString("FTradeTypeCode")==null?"":rs.getString("FTradeTypeCode");//交易类型代码
		this.Ftradetypename = rs.getString("Ftradetypename")==null?"":rs.getString("Ftradetypename");
		this.FExchangeCode = rs.getString("FExchangeCode")==null?"":rs.getString("FExchangeCode");//交易所代码
		this.Fexchangename = rs.getString("Fexchangename")==null?"":rs.getString("Fexchangename");
		this.FCategoryCode = rs.getString("FCategoryCode")==null?"":rs.getString("FCategoryCode");//品种类型代码
		this.Fcatname = rs.getString("Fcatname")==null?"":rs.getString("Fcatname");
		this.FSubCatCode = rs.getString("FSubCatCode")==null?"":rs.getString("FSubCatCode");//品种子类型代码
		this.Fsubcatname = rs.getString("Fsubcatname")==null?"":rs.getString("Fsubcatname");
	    
		this.FCashCode = rs.getString("FCashCode")==null?"":rs.getString("FCashCode");//银行存款账户
		this.Fcashaccname = rs.getString("Fcashaccname")==null?"":rs.getString("Fcashaccname");
		this.FHolidaysCode = rs.getString("FHolidaysCode")==null?"":rs.getString("FHolidaysCode");//节假日群代码
		this.Fholidaysname = rs.getString("Fholidaysname")==null?"":rs.getString("Fholidaysname");
		this.FDVPSettleOver = rs.getInt("FDVPSettleOver");//延迟天数
		this.FStartDate = rs.getDate("FStartDate")==null?"":YssFun.formatDate(rs.getDate("FStartDate"));//启用日期
		this.FDesc = rs.getString("FDesc")==null?"":rs.getString("FDesc");
		
        super.setRecLog(rs);
	}

	/**shashijie 2012-7-9 STORY 2727
	* @return*/
	private String getData1() throws YssException {
		String query = " Select a.Fportcode,"+//--组合代码
			" b.Fportname,"+
			" a.Ftradetypecode,"+//--交易类型代码
			" c.Ftradetypename,"+
			" a.Fexchangecode,"+//--交易所代码
			" d.Fexchangename,"+
			" a.Fcategorycode,"+//--品种类型代码
			" e.Fcatname,"+
			" a.Fsubcatcode,"+//--品种子类型代码
			" f.Fsubcatname,"+
       
			" a.Fcashcode,"+//--银行存款账户
			" g.Fcashaccname,"+
			" a.Fholidayscode,"+//--节假日群代码
			" h.Fholidaysname,"+
			" a.Fdvpsettleover,"+//--延迟天数
			" a.Fstartdate,"+//--启用日期
			" a.Fdesc,"+//描述
			" a.FCheckState,"+//审核状态
			" a.FCreator,"+//创建人、修改人
			" a.FCreateTime,"+//创建、修改时间
			" a.FCheckUser,"+//复核人
			" a.FCheckTime,"+//复核时间
			" i.Fusername FCreatorName,"+
			" j.Fusername FCheckUserName,"+
			
			" ' ' "+
			" From "+pub.yssGetTableName("Tb_Para_Dvpbusset")+" a"+
			" Left Join "+pub.yssGetTableName("Tb_Para_Portfolio")+" b On a.Fportcode = b.Fportcode"+
			" Left Join Tb_Base_Tradetype c On a.Ftradetypecode = c.Ftradetypecode"+
			" Left Join Tb_Base_Exchange d On a.Fexchangecode = d.Fexchangecode"+
			" Left Join Tb_Base_Category e On a.Fcategorycode = e.Fcatcode"+
			" Left Join Tb_Base_Subcategory f On a.Fsubcatcode = f.Fsubcatcode"+
			" Left Join "+pub.yssGetTableName("Tb_Para_Cashaccount")+" g On a.Fcashcode = g.Fcashacccode"+
			" Left Join Tb_Base_Holidays h On a.Fholidayscode = h.Fholidayscode"+
			" Left Join tb_Sys_UserList i On a.Fcreator = i.Fusercode"+
			" Left Join tb_Sys_UserList j On a.FCHECKUSER = j.Fusercode"+
			buildFilterSql() +
			" Order By a.Fstartdate";
		return query;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getListViewData2() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getListViewData3() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getListViewData4() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getListViewGroupData1() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getListViewGroupData2() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getListViewGroupData3() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getListViewGroupData4() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getListViewGroupData5() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getTreeViewData1() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getTreeViewData2() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getTreeViewData3() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getTreeViewGroupData1() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getTreeViewGroupData2() throws YssException {
		return null;
	}

	/**shashijie 2012-7-9 STORY 
	 * @return
	 * @throws YssException
	 */
	public String getTreeViewGroupData3() throws YssException {
		return null;
	}

	/**返回 fPortCode 的值*/
	public String getFPortCode() {
		return FPortCode;
	}

	/**传入fPortCode 设置  fPortCode 的值*/
	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}

	/**返回 fTradeTypeCode 的值*/
	public String getFTradeTypeCode() {
		return FTradeTypeCode;
	}

	/**传入fTradeTypeCode 设置  fTradeTypeCode 的值*/
	public void setFTradeTypeCode(String fTradeTypeCode) {
		FTradeTypeCode = fTradeTypeCode;
	}

	/**返回 fExchangeCode 的值*/
	public String getFExchangeCode() {
		return FExchangeCode;
	}

	/**传入fExchangeCode 设置  fExchangeCode 的值*/
	public void setFExchangeCode(String fExchangeCode) {
		FExchangeCode = fExchangeCode;
	}

	/**返回 fCategoryCode 的值*/
	public String getFCategoryCode() {
		return FCategoryCode;
	}

	/**传入fCategoryCode 设置  fCategoryCode 的值*/
	public void setFCategoryCode(String fCategoryCode) {
		FCategoryCode = fCategoryCode;
	}

	/**返回 fSubCatCode 的值*/
	public String getFSubCatCode() {
		return FSubCatCode;
	}

	/**传入fSubCatCode 设置  fSubCatCode 的值*/
	public void setFSubCatCode(String fSubCatCode) {
		FSubCatCode = fSubCatCode;
	}

	/**返回 fCashCode 的值*/
	public String getFCashCode() {
		return FCashCode;
	}

	/**传入fCashCode 设置  fCashCode 的值*/
	public void setFCashCode(String fCashCode) {
		FCashCode = fCashCode;
	}

	/**返回 fHolidaysCode 的值*/
	public String getFHolidaysCode() {
		return FHolidaysCode;
	}

	/**传入fHolidaysCode 设置  fHolidaysCode 的值*/
	public void setFHolidaysCode(String fHolidaysCode) {
		FHolidaysCode = fHolidaysCode;
	}

	/**返回 fDVPSettleOver 的值*/
	public int getFDVPSettleOver() {
		return FDVPSettleOver;
	}

	/**传入fDVPSettleOver 设置  fDVPSettleOver 的值*/
	public void setFDVPSettleOver(int fDVPSettleOver) {
		FDVPSettleOver = fDVPSettleOver;
	}

	/**返回 fStartDate 的值*/
	public String getFStartDate() {
		return FStartDate;
	}

	/**传入fStartDate 设置  fStartDate 的值*/
	public void setFStartDate(String fStartDate) {
		FStartDate = fStartDate;
	}

	/**返回 fDesc 的值*/
	public String getFDesc() {
		return FDesc;
	}

	/**传入fDesc 设置  fDesc 的值*/
	public void setFDesc(String fDesc) {
		FDesc = fDesc;
	}

	/**返回 oldFPortCode 的值*/
	public String getOldFPortCode() {
		return oldFPortCode;
	}

	/**传入oldFPortCode 设置  oldFPortCode 的值*/
	public void setOldFPortCode(String oldFPortCode) {
		this.oldFPortCode = oldFPortCode;
	}

	/**返回 oldFTradeTypeCode 的值*/
	public String getOldFTradeTypeCode() {
		return oldFTradeTypeCode;
	}

	/**传入oldFTradeTypeCode 设置  oldFTradeTypeCode 的值*/
	public void setOldFTradeTypeCode(String oldFTradeTypeCode) {
		this.oldFTradeTypeCode = oldFTradeTypeCode;
	}

	/**返回 oldFExchangeCode 的值*/
	public String getOldFExchangeCode() {
		return oldFExchangeCode;
	}

	/**传入oldFExchangeCode 设置  oldFExchangeCode 的值*/
	public void setOldFExchangeCode(String oldFExchangeCode) {
		this.oldFExchangeCode = oldFExchangeCode;
	}

	/**返回 oldFCategoryCode 的值*/
	public String getOldFCategoryCode() {
		return oldFCategoryCode;
	}

	/**传入oldFCategoryCode 设置  oldFCategoryCode 的值*/
	public void setOldFCategoryCode(String oldFCategoryCode) {
		this.oldFCategoryCode = oldFCategoryCode;
	}

	/**返回 oldFSubCatCode 的值*/
	public String getOldFSubCatCode() {
		return oldFSubCatCode;
	}

	/**传入oldFSubCatCode 设置  oldFSubCatCode 的值*/
	public void setOldFSubCatCode(String oldFSubCatCode) {
		this.oldFSubCatCode = oldFSubCatCode;
	}

	/**返回 oldFStartDate 的值*/
	public String getOldFStartDate() {
		return oldFStartDate;
	}

	/**传入oldFStartDate 设置  oldFStartDate 的值*/
	public void setOldFStartDate(String oldFStartDate) {
		this.oldFStartDate = oldFStartDate;
	}

	/**返回 fportname 的值*/
	public String getFportname() {
		return Fportname;
	}

	/**传入fportname 设置  fportname 的值*/
	public void setFportname(String fportname) {
		Fportname = fportname;
	}

	/**返回 ftradetypename 的值*/
	public String getFtradetypename() {
		return Ftradetypename;
	}

	/**传入ftradetypename 设置  ftradetypename 的值*/
	public void setFtradetypename(String ftradetypename) {
		Ftradetypename = ftradetypename;
	}

	/**返回 fexchangename 的值*/
	public String getFexchangename() {
		return Fexchangename;
	}

	/**传入fexchangename 设置  fexchangename 的值*/
	public void setFexchangename(String fexchangename) {
		Fexchangename = fexchangename;
	}

	/**返回 fcatname 的值*/
	public String getFcatname() {
		return Fcatname;
	}

	/**传入fcatname 设置  fcatname 的值*/
	public void setFcatname(String fcatname) {
		Fcatname = fcatname;
	}

	/**返回 fsubcatname 的值*/
	public String getFsubcatname() {
		return Fsubcatname;
	}

	/**传入fsubcatname 设置  fsubcatname 的值*/
	public void setFsubcatname(String fsubcatname) {
		Fsubcatname = fsubcatname;
	}

	/**返回 fcashaccname 的值*/
	public String getFcashaccname() {
		return Fcashaccname;
	}

	/**传入fcashaccname 设置  fcashaccname 的值*/
	public void setFcashaccname(String fcashaccname) {
		Fcashaccname = fcashaccname;
	}

	/**返回 fholidaysname 的值*/
	public String getFholidaysname() {
		return Fholidaysname;
	}

	/**传入fholidaysname 设置  fholidaysname 的值*/
	public void setFholidaysname(String fholidaysname) {
		Fholidaysname = fholidaysname;
	}

	/**返回 filterType 的值*/
	public ETFDVPSetUp getFilterType() {
		return filterType;
	}

	/**传入filterType 设置  filterType 的值*/
	public void setFilterType(ETFDVPSetUp filterType) {
		this.filterType = filterType;
	}

	/**返回 oldFCashCode 的值*/
	public String getOldFCashCode() {
		return oldFCashCode;
	}

	/**传入oldFCashCode 设置  oldFCashCode 的值*/
	public void setOldFCashCode(String oldFCashCode) {
		this.oldFCashCode = oldFCashCode;
	}

	/**返回 sRecycled 的值*/
	public String getsRecycled() {
		return sRecycled;
	}

	/**传入sRecycled 设置  sRecycled 的值*/
	public void setsRecycled(String sRecycled) {
		this.sRecycled = sRecycled;
	}

}
