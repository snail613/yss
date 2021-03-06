package com.yss.main.parasetting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.util.YssUtil;

/**
 * @author zhouwei
 * 20120321
 * 固定交易费用设置
 *
 */
public class FixedFeeCfgBean extends BaseDataSettingBean implements
		IDataSetting {
	private Date startDate=new Date();
	private String bussinessType="0";//业务所属类别 0-网上交易，1-场外回购业务，2-银行间债券业务，3-网下新股新债业务，
	                                  //4-开放式基金业务，5-债券转托管业务，6-存款业务
	private String savingType="0";//存款业务类型 1.同业拆借 4、普通定存;3、通知定存;2、协定定存
	private String tradeType="";//交易方式
	private String tradeTypeName="";//交易方式名称
	private String categoryCode="";//品种类型
	private String categoryName="";//品种名称
	private String portCode="";//组合
	private String portName="";
	private double feeMoney=0;//固定费用
	private String desc="";
	private String bussinessTypeName="";
	private String savingTypeName="";
	private Date oldStartDate=new Date();
	private String oldBussinessType="";
	private String oldSavingType="0";
	private String oldTradeType="";
	private String oldCategoryCode="";
	private String oldPortCode="";
	private String subTsferCode="";
	private String subTsferName="";
	private String cashAccCode="";//现金账户
	private String cashAccName="";
	private String sRecycled = ""; //保存未解析前的字符串
	
	private int choException = 0; //滚存业务除外  zhangjun 2012.05.31 story#2579
	
	private FixedFeeCfgBean filterType;
	
	
	
	public int getChoException() {
		return choException;
	}

	public void setChoException(int choException) {
		this.choException = choException;
	}

	public String getBussinessType() {
		return bussinessType;
	}

	public void setBussinessType(String bussinessType) {
		this.bussinessType = bussinessType;
	}

	public String getSavingType() {
		return savingType;
	}

	public void setSavingType(String savingType) {
		this.savingType = savingType;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	/* 
	 * 解析前台字符串
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
	            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
	            reqAry = sTmpStr.split("\t");
	            this.startDate=YssFun.toDate(reqAry[0]);
	            this.bussinessType=reqAry[1];
	            this.savingType=reqAry[2];
	            this.tradeType=reqAry[3];
	            this.categoryCode=reqAry[4];
	            this.portCode=reqAry[5];
	            this.feeMoney=YssFun.toDouble(reqAry[6]);
	            this.checkStateId=Integer.parseInt(reqAry[7]);
	            this.oldStartDate=YssFun.toDate(reqAry[8]);
	            this.oldBussinessType=reqAry[9];
	            this.oldSavingType=reqAry[10];
	            this.oldTradeType=reqAry[11];
	            this.oldCategoryCode=reqAry[12];
	            this.oldPortCode=reqAry[13];
	            this.subTsferCode=reqAry[14];
	            this.cashAccCode=reqAry[15];
	            this.choException = Integer.parseInt(reqAry[16]); //add by zhangjun 2012.05.31 story#2579
	            this.desc=reqAry[17];	//20120710 modified by liubo.Bug #4895.
	            super.parseRecLog();
	            if (sRowStr.indexOf("\r\t") >= 0) {
	                if (this.filterType == null) {
	                    this.filterType = new FixedFeeCfgBean();
	                    this.filterType.setYssPub(pub);
	                }
	                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
	            }
	        }catch (Exception e) {
	        	 throw new YssException("解析固定费用设置请求出错！", e);
			}
	}

	public String buildRowStr() throws YssException {
		StringBuffer sb=new StringBuffer();
		sb.append(YssFun.formatDate(this.startDate)).append("\t");
		sb.append(this.bussinessType).append("\t");
		sb.append(this.savingType).append("\t");
		sb.append(this.tradeType).append("\t");
		sb.append(this.tradeTypeName).append("\t");
		sb.append(this.categoryCode).append("\t");
		sb.append(this.categoryName).append("\t");
		sb.append(YssFun.formatNumber(this.feeMoney, "0.00##")).append("\t");
		sb.append(this.portCode).append("\t");
		sb.append(this.portName).append("\t");
		sb.append(this.desc).append("\t");
		sb.append(this.bussinessTypeName).append("\t");
		sb.append(this.savingTypeName).append("\t");
		sb.append(this.subTsferCode).append("\t");
		sb.append(this.subTsferName).append("\t");
		sb.append(this.cashAccCode).append("\t");
		sb.append(this.cashAccName).append("\t");
		sb.append(this.choException).append("\t");//add by zhangjun 2012.05.31 story#2579
		sb.append(super.buildRecLog());
		return sb.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData1() throws YssException {
		String strSql="";
		strSql="select a.*,b.fcatname,c.ftradetypename,d.fusername as FCreatorName,e.fusername as FCheckUserName,f.fportname,g.FSubTsfTypeName,acc.FCASHACCNAME,"
			  +"( case when FBUSSINESSTYPE='0' then '网上交易' when FBUSSINESSTYPE='1' then '场外回购业务' when FBUSSINESSTYPE='2' then '银行间债券业务' when FBUSSINESSTYPE='3' then '网下新股新债业务' "
				+" when FBUSSINESSTYPE='4' then '开放式基金业务' when FBUSSINESSTYPE='5' then '债券转托管业务' else '存款业务' end ) as FBUSSINESSTYPENAME,"
			  +"(case when FBUSSINESSTYPE<>'6' then ' ' else (case when FSAVINGTYPE='1' then '同业拆借' when FSAVINGTYPE='2' then '协定定存' when FSAVINGTYPE='3' then '通知定存' else '普通定存' end ) end ) as FSAVINGTYPENAME"
			  +" from "+pub.yssGetTableName("TB_PARA_FIXEDFEECFG")+" a"
			  +" left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) b on a.FCatCode = b.FCatCode"
			  +" left join (select * from Tb_Base_TradeType where fcheckstate=1) c on a.ftradetypecode=c.ftradetypecode"
			  +" left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode"
			  +" left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode"
			  +" left join (select *  from "+pub.yssGetTableName("Tb_Para_Portfolio")+" where fcheckstate=1) f on a.fportcode = f.fportcode"
			  +" left join (select * from Tb_Base_SubTransferType where fcheckstate=1) g on a.FSubTsfTypeCode=g.FSubTsfTypeCode "
			  +" left join (select * from "+pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate=1 ) acc on a.FCASHACCCODE=acc.FCASHACCCODE"
			  +buildFilterSql();
		return this.builderListViewData(strSql);
	}
	 /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                if(this.filterType.portCode!=null && !this.filterType.portCode.equals(" ")){
                	sResult+=" and a.FPORTCODE ="+dbl.sqlString(this.filterType.portCode);
                }
                if(this.filterType.categoryCode!=null && !this.filterType.categoryCode.equals(" ")){
                	sResult+=" and a.FCATCODE ="+dbl.sqlString(this.filterType.categoryCode);
                }
                if(this.filterType.tradeType!=null && !this.filterType.tradeType.equals(" ")){
                	sResult+=" and a.FTRADETYPECODE ="+dbl.sqlString(this.filterType.tradeType);
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选固定费用设置数据出错", e);
        }

        return sResult;
    }
	/**
	 * 向前台返回值
	 * @param strSql
	 * @return
	 * @throws YssException
	 */
	public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String sVocStr="";
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setTableAttr(rs);
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
			sVocStr = vocabulary.getVoc(YssCons.YSS_CSH_SavingType+","
										+YssCons.YSS_BUSSINESS_TYPE+","
										+YssCons.YSS_BondTradeType+","
										+YssCons.YSS_CASHSAVE_TRADETYPE);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+"\r\fvoc"+sVocStr;

        } catch (Exception e) {
            throw new YssException("获取固定费用设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	public void setTableAttr(ResultSet rs) throws SQLException{
		this.bussinessType=rs.getString("FBUSSINESSTYPE");
		this.startDate=rs.getDate("FSTARTDATE");
		this.categoryCode=rs.getString("FCATCODE");
		this.categoryName=rs.getString("fcatname");
		this.desc=rs.getString("fdesc");
		this.feeMoney=rs.getDouble("FMONEEY");
		this.portCode=rs.getString("FPORTCODE");
		this.portName=rs.getString("fportname");
		this.tradeType=rs.getString("FTRADETYPECODE");
		this.tradeTypeName=rs.getString("ftradetypename");
		this.savingType=rs.getString("FSAVINGTYPE");
		this.bussinessTypeName=rs.getString("FBUSSINESSTYPENAME");
		this.savingTypeName=rs.getString("FSAVINGTYPENAME");
		this.subTsferCode=rs.getString("FSubTsfTypeCode");
		this.subTsferName=rs.getString("FSubTsfTypeName");
		this.cashAccCode=rs.getString("FCASHACCCODE");
		this.cashAccName=rs.getString("FCASHACCNAME");
		this.choException = rs.getInt("FchoException"); //add by zhangjun 2012.05.31 story#2579=====
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

	public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("tb_PARA_FIXEDFEECFG"),
                "FSTARTDATE,FPORTCODE,FCATCODE,FTRADETYPECODE,FBUSSINESSTYPE,FSAVINGTYPE",
                YssFun.formatDate(this.startDate) + "," +
                this.portCode+","+this.categoryCode+","+this.tradeType+","+this.bussinessType+","+this.savingType,
                YssFun.formatDate(this.oldStartDate)+","+
                this.oldPortCode+","+this.oldCategoryCode+","+
                this.oldTradeType+","+this.oldBussinessType+","+this.oldSavingType);

}

	public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " +
                pub.yssGetTableName("tb_PARA_FIXEDFEECFG") +
                " (FSTARTDATE,FPORTCODE,FCATCODE,FTRADETYPECODE,FBUSSINESSTYPE,FSAVINGTYPE,FMONEEY,FSubTsfTypeCode,FCASHACCCODE,FDESC" +
                ",FCheckState,FCreator,FCreateTime,FCheckUser,FCHECKTIME,FchoException)" + //modify by zhangjun 2012.05.31 story#2579---------
                " values(" + 
                dbl.sqlDate(this.startDate) + "," +
                dbl.sqlString(this.portCode) +
                "," +
                dbl.sqlString(this.categoryCode) + "," +
                dbl.sqlString(this.tradeType) + "," +
                dbl.sqlString(this.bussinessType) + "," +
                dbl.sqlString(this.savingType) +
                "," +
                this.feeMoney + "," +
                dbl.sqlString(this.subTsferCode)+","+
                dbl.sqlString(this.cashAccCode)+","+
                dbl.sqlString(this.desc) +"," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                //20120706 modified by liubo.Bug #4895
                //新建数据时审核人跟审核时间需要为空
                //======================================
//                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +"," +
//                 dbl.sqlString(this.checkTime) + 
                " ' ', ' '" + 
                //===============end=======================
                "," + this.choException +  //modify by zhangjun 2012.05.31 story#2579---------
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加固定交易费用设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

	public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("tb_PARA_FIXEDFEECFG") +
                " set FPORTCODE = " +
                dbl.sqlString(this.portCode) + ", FCATCODE = " +
                dbl.sqlString(this.categoryCode) + " , FStartDate = " +
                dbl.sqlDate(this.startDate) + " , FTRADETYPECODE = " +
                dbl.sqlString(this.tradeType) +
                ", FBUSSINESSTYPE = " +
                dbl.sqlString(this.bussinessType) + ", FSAVINGTYPE = " +
                dbl.sqlString(this.savingType) + ",FMONEEY = " +
                this.feeMoney+ ",FSubTsfTypeCode="+dbl.sqlString(this.subTsferCode)+",FDESC = " +
                dbl.sqlString(this.desc) + ",FCASHACCCODE="+dbl.sqlString(this.cashAccCode)+",FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime)+
                ",FchoException = " + this.choException +//modify by zhangjun 2012.05.31 story#2579---------
                " where FPORTCODE = " +
                dbl.sqlString(this.oldPortCode) +
                " and FStartDate=" + dbl.sqlDate(this.oldStartDate)+
                " and FCATCODE="+dbl.sqlString(this.oldCategoryCode)+
                " and FTRADETYPECODE="+dbl.sqlString(this.oldTradeType)+
                " and FBUSSINESSTYPE="+dbl.sqlString(this.oldBussinessType)+
                " and FSAVINGTYPE="+dbl.sqlString(this.oldSavingType);
            	
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改固定交易费用设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

	public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("tb_PARA_FIXEDFEECFG") +
                " set FCheckState = " +this.checkStateId 
                + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) + ", FCheckTime = " +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))+
                " where FPORTCODE = " +
                dbl.sqlString(this.portCode) +
                " and FStartDate=" + dbl.sqlDate(this.startDate)+
                " and FCATCODE="+dbl.sqlString(this.categoryCode)+
                " and FTRADETYPECODE="+dbl.sqlString(this.tradeType)+
                " and FBUSSINESSTYPE="+dbl.sqlString(this.bussinessType)+
                " and FSAVINGTYPE="+dbl.sqlString(this.savingType);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("删除固定交易费用设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

	public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String[] arrData = null; //add by zhangjun 2012-05-10 BUG#4443
        try {
        	//------add by zhangjun 2012-05-10 BUG#4443---------------
        	conn = dbl.loadConnection();            
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
           //------add by zhangjun 2012-05-10 BUG#4443---------------
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].trim().length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                
	            strSql = "update " + pub.yssGetTableName("tb_PARA_FIXEDFEECFG") +
	                " set FCheckState = " +this.checkStateId 
	                + ", FCheckUser = " +
	                dbl.sqlString(pub.getUserCode()) + ", FCheckTime = " +
	                dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))+
	                " where FPORTCODE = " +
	                dbl.sqlString(this.portCode) +
	                " and FStartDate=" + dbl.sqlDate(this.startDate)+
	                " and FCATCODE="+dbl.sqlString(this.categoryCode)+
	                " and FTRADETYPECODE="+dbl.sqlString(this.tradeType)+
	                " and FBUSSINESSTYPE="+dbl.sqlString(this.bussinessType)+
	                " and FSAVINGTYPE="+dbl.sqlString(this.savingType);
	            dbl.executeSql(strSql);
            }
            /*
            strSql = "update " + pub.yssGetTableName("tb_PARA_FIXEDFEECFG") +
            " set FCheckState = " +this.checkStateId 
            + ", FCheckUser = " +
            dbl.sqlString(pub.getUserCode()) + ", FCheckTime = " +
            dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))+
            " where FPORTCODE = " +
            dbl.sqlString(this.portCode) +
            " and FStartDate=" + dbl.sqlDate(this.startDate)+
            " and FCATCODE="+dbl.sqlString(this.categoryCode)+
            " and FTRADETYPECODE="+dbl.sqlString(this.tradeType)+
            " and FBUSSINESSTYPE="+dbl.sqlString(this.bussinessType)+
            " and FSAVINGTYPE="+dbl.sqlString(this.savingType);*/
            //dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("审核固定交易费用设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteRecycleData() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String[] arrData = null;
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
		            strSql = "delete from " + pub.yssGetTableName("tb_PARA_FIXEDFEECFG") +
		                " where FPORTCODE = " +
		                  dbl.sqlString(this.portCode) +
		                " and FStartDate=" + dbl.sqlDate(this.startDate)+
		                " and FCATCODE="+dbl.sqlString(this.categoryCode)+
		                " and FTRADETYPECODE="+dbl.sqlString(this.tradeType)+
		                " and FBUSSINESSTYPE="+dbl.sqlString(this.bussinessType)+
		                " and FSAVINGTYPE="+dbl.sqlString(this.savingType);
		            dbl.executeSql(strSql);
            	}
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("清除固定交易费用设置信息出错!", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
	/**by zhouwei 20120321
	 * 根据条件找出符合的固定交易费用配置
	 * @param date
	 * @param bussType
	 * @param pCode
	 * @param tType
	 * @param sType
	 * @param cateCode
	 * @return
	 * @throws YssException
	 * modify by zhangjun 2012.06.15 story#2579
	 */
	public String getFixedFeeMoney(Date date,String bussType,String pCode,
			String tType,String sType,String cateCode,double Flag) throws YssException{
		String reStr="";
		double fixedMoney=0;
		String subTsCode=YssOperCons.YSS_ZJDBZLX_TF;//调拨子类型
    	String cashAccCode="";//现金账户
		ResultSet rs=null;
		String sqlStr="";
		try{
			sqlStr="select a.*,b.fmoneey,b.FSubTsfTypeCode,b.FCASHACCCODE from (select max(FSTARTDATE) as FSTARTDATE, FPORTCODE,FCATCODE,FTRADETYPECODE,FBUSSINESSTYPE,FSAVINGTYPE,FchoException" //modify by zhangjun 2012.05.31 story#2579
				  +" from  "+pub.yssGetTableName("TB_PARA_FIXEDFEECFG")+"  where fcheckstate=1"
				  +" and FSTARTDATE<="+dbl.sqlDate(date)+"  group by  FPORTCODE,FCATCODE,FTRADETYPECODE,FBUSSINESSTYPE,FSAVINGTYPE,FchoException) a" //modify by zhangjun 2012.05.31 story#2579
				  +" join (select * from "+pub.yssGetTableName("TB_PARA_FIXEDFEECFG")+" where fcheckstate=1) b"
				  +" on a.FSTARTDATE=b.FSTARTDATE and a.FPORTCODE=b.FPORTCODE and a.FCATCODE=b.FCATCODE "
				  +" and a.FTRADETYPECODE=b.FTRADETYPECODE and a.FBUSSINESSTYPE=b.FBUSSINESSTYPE and a.FSAVINGTYPE=b.FSAVINGTYPE"
				  +" where (a.FPORTCODE="+dbl.sqlString(pCode)+" or a.FPORTCODE=' ')"
				  +" and (a.FCATCODE="+dbl.sqlString(cateCode)+" or a.FCATCODE=' ')"
				  +" and (a.FTRADETYPECODE="+dbl.sqlString(tType)+" or a.FTRADETYPECODE=' ')"
				  +" and a.FBUSSINESSTYPE="+dbl.sqlString(bussType)
				  +" and a.FSAVINGTYPE="+dbl.sqlString(sType);
			//add by zhangjun 2012.05.31 story#2579
			if( bussType.trim().length()>0 && "6".equals(bussType) && Flag ==1 )
			{
				sqlStr = sqlStr + "and a.FchoException = 0 " ; //没有勾选滚存业务除外
			}
			//add by zhangjun 2012.05.31 story#2579
			rs=dbl.openResultSet(sqlStr);
			if(rs.next()){
				fixedMoney=rs.getDouble("fmoneey");
				cashAccCode=rs.getString("fcashAccCode");
				if(rs.getString("FSubTsfTypeCode")==null || rs.getString("FSubTsfTypeCode").equalsIgnoreCase("") 
						|| rs.getString("FSubTsfTypeCode").equalsIgnoreCase(" ")){
					subTsCode=YssOperCons.YSS_ZJDBZLX_TF;
				}else{
					subTsCode=rs.getString("FSubTsfTypeCode");
				}
			}
			reStr=Double.toString(fixedMoney)+"\t"+subTsCode+"\t"+cashAccCode+"\tnull";
		}catch (Exception e) {
			throw new YssException("获取固定交易费用设置出错!", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return reStr;
		
	}
}
