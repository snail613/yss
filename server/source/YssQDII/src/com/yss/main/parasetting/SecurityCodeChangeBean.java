package com.yss.main.parasetting;

import java.sql.*;
import java.text.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.linkInfo.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

/**
 *
 * <p>Title: FeeBean </p>
 * <p>Description: MS01446    add by zhangfa 20100806 ‘证券代码变更’    QDV4海富通2010年7月9日01_A   </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SecurityCodeChangeBean
    extends BaseDataSettingBean implements IDataSetting {
	private String businessDate="1900-01-01"; //业务日期
	private String securityCodeBefore="";//变更前的证券代码
	private String securityNameBefore="";//变更前的证券名称
	private String securityCodeAfter="";//变更后的证券代码
	private String securityNameAfter="";//变更后的证券名称
	private String desc="";//描叙
	private String oldbusinessDate="1900-01-01"; //业务日期
	private String oldsecurityCodeBefore="";//变更前的证券代码
	private String oldsecurityCodeAfter="";//变更后的证券代码
	private SecurityCodeChangeBean filterType;
  
    private String sRecycled = "";

	public SecurityCodeChangeBean() {
	}

	public String getOldbusinessDate() {
		return oldbusinessDate;
	}

	public void setOldbusinessDate(String oldbusinessDate) {
		this.oldbusinessDate = oldbusinessDate;
	}

	public String getOldsecurityCodeBefore() {
		return oldsecurityCodeBefore;
	}

	public void setOldsecurityCodeBefore(String oldsecurityCodeBefore) {
		this.oldsecurityCodeBefore = oldsecurityCodeBefore;
	}

	
	public String getOldsecurityCodeAfter() {
		return oldsecurityCodeAfter;
	}

	public void setOldsecurityCodeAfter(String oldsecurityCodeAfter) {
		this.oldsecurityCodeAfter = oldsecurityCodeAfter;
	}

	
	public SecurityCodeChangeBean getFilterType() {
		return filterType;
	}

	public void setFilterType(SecurityCodeChangeBean filterType) {
		this.filterType = filterType;
	}

	public String getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}

	public String getSecurityCodeBefore() {
		return securityCodeBefore;
	}

	public void setSecurityCodeBefore(String securityCodeBefore) {
		this.securityCodeBefore = securityCodeBefore;
	}

	public String getSecurityNameBefore() {
		return securityNameBefore;
	}

	public void setSecurityNameBefore(String securityNameBefore) {
		this.securityNameBefore = securityNameBefore;
	}

	public String getSecurityCodeAfter() {
		return securityCodeAfter;
	}

	public void setSecurityCodeAfter(String securityCodeAfter) {
		this.securityCodeAfter = securityCodeAfter;
	}

	public String getSecurityNameAfter() {
		return securityNameAfter;
	}

	public void setSecurityNameAfter(String securityNameAfter) {
		this.securityNameAfter = securityNameAfter;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}


	/**
     * parseRowStr
     * 解析证券代码变更设置
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
           this.businessDate=reqAry[0];
           this.securityCodeBefore=reqAry[1];
           this.securityNameBefore=reqAry[2];
           this.securityCodeAfter=reqAry[3];
           this.securityNameAfter=reqAry[4];
           
           this.oldbusinessDate=reqAry[5];
           this.oldsecurityCodeBefore=reqAry[6];
           this.oldsecurityCodeAfter=reqAry[7];
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
                    this.filterType = new SecurityCodeChangeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析证券代码变更设置请求出错", e);
        }
    }

    /**
     * auditSetting
     */
    public void auditSetting() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.businessDate).append("\t");
        buf.append(this.securityCodeBefore).append("\t");
        buf.append(this.securityNameBefore).append("\t");
        buf.append(this.securityCodeAfter).append("\t");
        buf.append(this.securityNameAfter).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     */
    public void checkInput() {
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
           if(this.filterType.businessDate.equals("9998-12-31")==false){
            if (this.filterType.businessDate.length() != 0) {
                sResult = sResult + " and FBusinessDate =" +dbl.sqlDate(filterType.businessDate);
            }
            }
            if (this.filterType.securityCodeBefore.length() != 0) {
                sResult = sResult + " and FSecurityCodeBefore like '" +
                    filterType.securityCodeBefore.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.securityCodeAfter.length() != 0) {
                sResult = sResult + " and FSecurityCodeAfter like '" +
                    filterType.securityCodeAfter.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%' ";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取证券代码变更设置信息
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
            strSql ="select a.*,b.*,c.*,d.*,e.* from (select * from "+ pub.yssGetTableName("Tb_Para_SecCodeChange")+" ) a "+
                    " left join (select FUserCode,FUserName as FCreatorName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName as FCheckUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " left join (select FSecurityCode,FSecurityName as FSecurityNameBefore from " +pub.yssGetTableName("Tb_Para_Security") +
                    ") d  on a.FSecurityCodeBefore = d.FSecurityCode " +
                    " left join (select FSecurityCode,FSecurityName as FSecurityNameAfter  from " +pub.yssGetTableName("Tb_Para_Security") +
                    ") e  on a.FSecurityCodeAfter = e.FSecurityCode " +
                buildFilterSql() + " order by a.FCheckState,a.FCreateTime desc"; 

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.businessDate = rs.getDate("FBusinessDate") + "";
                this.securityCodeBefore = rs.getString("FSecurityCodeBefore") + "";
                this.securityNameBefore = rs.getString("FSecurityNameBefore") + "";
                this.securityCodeAfter = rs.getString("FSecurityCodeAfter");
                this.securityNameAfter = rs.getString("FSecurityNameAfter");
                this.desc = rs.getString("FDesc");
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

           

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols(); //+"\f\f"+sVocStr1;
        } catch (Exception e) {
            throw new YssException("获取证券代码变更信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的费用设置信息
     * @return String
     */
    public String getListViewData2() throws YssException {
       return "";
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
      return "";
    }

    /**
     * getPartSetting
     *
     * @return String
     */
    public String getPartSetting() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IBaseSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_SecCodeChange") +
                " where FBusinessDate = " + dbl.sqlDate(this.businessDate)+
                " and FSecurityCodeBefore="+dbl.sqlString(this.securityCodeBefore)+
                " and FSecurityCodeAfter="+dbl.sqlString(this.securityCodeAfter);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.businessDate = rs.getDate("FBusinessDate") + "";
                this.securityCodeBefore = rs.getString("FSecurityCodeBefore") + "";
                this.securityNameBefore = rs.getString("FSecurityNameBefore") + "";
                this.securityCodeAfter = rs.getString("FSecurityCodeAfter") + "";
                this.securityNameAfter = rs.getString("FSecurityNameAfter") + "";
               
                this.desc = rs.getString("FDesc");
                this.checkStateId = rs.getInt("FCheckState");
            }
            return null;
        } catch (Exception e) {
            throw new YssException("获取证券代码变更信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }


    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */
  
    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_SecCodeChange") +
                " (FBusinessDate,FSecurityCodeBefore,FSecurityCodeAfter," +
                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlDate(this.businessDate) + "," +
                dbl.sqlString(this.securityCodeBefore) + "," +
                //dbl.sqlString(this.securityNameBefore) + " ," +
                dbl.sqlString(this.securityCodeAfter) + "," +
                //dbl.sqlString(this.securityNameAfter) + "," +
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
            throw new YssException("增加证券代码变更设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_SecCodeChange") +
                " set FBusinessDate = " +
                dbl.sqlDate(this.businessDate) + ", FSecurityCodeBefore = " +
                dbl.sqlString(this.securityCodeBefore) + " ," +
                " FSecurityCodeAfter = " + dbl.sqlString(this.securityCodeAfter) + "," +
                   " FDesc = " +dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FBusinessDate = " +
                dbl.sqlDate(this.oldbusinessDate)
                +" and FSecurityCodeBefore="+dbl.sqlString(this.oldsecurityCodeBefore)+
                " and FSecurityCodeAfter="+dbl.sqlString(this.oldsecurityCodeAfter);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改证券代码变更设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，即是放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_SecCodeChange") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FBusinessDate = " +
                dbl.sqlDate(this.businessDate)
                +" and FSecurityCodeBefore="+dbl.sqlString(this.securityCodeBefore)+
                " and FSecurityCodeAfter="+dbl.sqlString(this.securityCodeAfter);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除证券代码变更设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 可以处理期间信息审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
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
                    strSql = "update " + pub.yssGetTableName("Tb_Para_SecCodeChange") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FBusinessDate = " +
                        dbl.sqlDate(this.businessDate)
                        +" and FSecurityCodeBefore="+dbl.sqlString(this.securityCodeBefore)+
                        " and FSecurityCodeAfter="+dbl.sqlString(this.securityCodeAfter);

                        dbl.executeSql(strSql);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }

            }
            //sRecycled如果sRecycled为空，而feeCode不为空，则按照feeCode来执行sql语句
            else if ((businessDate != null&&businessDate != "" )&&(securityCodeBefore != null&&securityCodeBefore != "" )
            		&&(securityCodeAfter != null&&securityCodeAfter != "" )) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_SecCodeChange") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FBusinessDate = " +
                    dbl.sqlDate(this.businessDate)
                    +" and FSecurityCodeBefore="+dbl.sqlString(this.securityCodeBefore)+
                    " and FSecurityCodeAfter="+dbl.sqlString(this.securityCodeAfter);
                //执行sql语句
                dbl.executeSql(strSql);

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
            throw new YssException("审核证券代码变更设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
//---------------------------------
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
       return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
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
                    strSql = "delete from " + pub.yssGetTableName("Tb_Para_SecCodeChange") +
                    " where FBusinessDate = " +
                    dbl.sqlDate(this.businessDate)
                    +" and FSecurityCodeBefore="+dbl.sqlString(this.securityCodeBefore)+
                    " and FSecurityCodeAfter="+dbl.sqlString(this.securityCodeAfter);

                    dbl.executeSql(strSql);
                }

            }
            //sRecycled如果sRecycled为空，而feeCode不为空，则按照feeCode来执行sql语句
            else if ((businessDate != null&&businessDate != "" )&&(securityCodeBefore != null&&securityCodeBefore != "" )
            		&&(securityCodeAfter != null&&securityCodeAfter != "" )) {
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_SecCodeChange") +
                " where FBusinessDate = " +dbl.sqlDate(this.businessDate)
                +" and FSecurityCodeBefore="+dbl.sqlString(this.securityCodeBefore)+
                " and FSecurityCodeAfter="+dbl.sqlString(this.securityCodeAfter);
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

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkInput(byte)
	 */
	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("Tb_Para_SecCodeChange"),
                "FBusinessDate,FSecurityCodeBefore",
                this.businessDate+","+this.securityCodeBefore,
                this.oldbusinessDate+","+this.oldsecurityCodeBefore);
		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssLogData#getBeforeEditData()
	 */
	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

}
