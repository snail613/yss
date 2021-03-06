package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.*;
//story 2300 add by yeshenghong 20120227
public class AccountTransferRelationBean
    extends BaseDataSettingBean implements IDataSetting {
    private String relaNum = ""; //关系编号
	private String accountingType = ""; //核算类型
    private String accTypeCode = ""; //核算类型代码
    private String accountSubType = ""; //核算子类型
    private String accSubTypeCode = ""; //核算子类型代码
    private String securityShow;//证券代码显示
    private String ivPayCatShow;//费用明细显示
    private String cashAccShow;//现金账户显示
    private String subTsfTypeCode;//调拨子类型
    private String subjectType;//科目类型  资产 负债
    private String dataStyle; // 产生的数据类型
    private String dataStyleName;//产生的数据类型名称
    private String status = ""; //是否记入系统信息状态  lzp 11.30 add
    
    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private AccountTransferRelationBean filterType;
    private String subjectTypeName;//科目类型名称
    private String subTsfTypeName;
    private String reqItem; 
    private AccountTransferRelationBean atrb;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 2009.01.13 方浩
	private String oldRelaNum;

    
    public String getOldRelaNum() {
		return oldRelaNum;
	}

	public void setOldRelaNum(String oldRelaNum) {
		this.oldRelaNum = oldRelaNum;
	}

	public String getRelaNum() {
		return relaNum;
	}

	public void setRelaNum(String relaNum) {
		this.relaNum = relaNum;
	}

	public String getAccountingType() {
		return accountingType;
	}

	public void setAccountingType(String accountingType) {
		this.accountingType = accountingType;
	}

	public String getAccTypeCode() {
		return accTypeCode;
	}

	public void setAccTypeCode(String accTypeCode) {
		this.accTypeCode = accTypeCode;
	}

	public String getAccountSubType() {
		return accountSubType;
	}

	public void setAccountSubType(String accountSubType) {
		this.accountSubType = accountSubType;
	}

	public String getAccSubTypeCode() {
		return accSubTypeCode;
	}

	public void setAccSubTypeCode(String accSubTypeCode) {
		this.accSubTypeCode = accSubTypeCode;
	}

	public String getSecurityShow() {
		return securityShow;
	}

	public void setSecurityShow(String securityShow) {
		this.securityShow = securityShow;
	}

	public String getIvPayCatShow() {
		return ivPayCatShow;
	}

	public void setIvPayCatShow(String ivPayCatShow) {
		this.ivPayCatShow = ivPayCatShow;
	}

	public String getCashAccShow() {
		return cashAccShow;
	}

	public void setCashAccShow(String cashAccShow) {
		this.cashAccShow = cashAccShow;
	}

	public String getSubTsfTypeCode() {
		return subTsfTypeCode;
	}

	public void setSubTsfTypeCode(String subTsfTypeCode) {
		this.subTsfTypeCode = subTsfTypeCode;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public String getDataStyle() {
		return dataStyle;
	}

	public void setDataStyle(String dataStyle) {
		this.dataStyle = dataStyle;
	}

	public AccountTransferRelationBean getAtrb() {
		return atrb;
	}

	public void setAtrb(AccountTransferRelationBean atrb) {
		this.atrb = atrb;
	}

	public String getsRecycled() {
		return sRecycled;
	}

	public void setsRecycled(String sRecycled) {
		this.sRecycled = sRecycled;
	}

    public String getDataStyleName() {
		return dataStyleName;
	}

	public void setDataStyleName(String dataStyleName) {
		this.dataStyleName = dataStyleName;
	}

	public String getSubjectTypeName() {
		return subjectTypeName;
	}

	public void setSubjectTypeName(String subjectTypeName) {
		this.subjectTypeName = subjectTypeName;
	}

	public String getSubTsfTypeName() {
		return subTsfTypeName;
	}

	public void setSubTsfTypeName(String subTsfTypeName) {
		this.subTsfTypeName = subTsfTypeName;
	}

	public String getReqItem() {
		return reqItem;
	}

	public void setReqItem(String reqItem) {
		this.reqItem = reqItem;
	}

    public AccountTransferRelationBean() {
    }

    /**
     * parseRowStr
     * 解析分析代码设置数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //bug MS00149 2009.01.13 方浩
            reqAry = sRowStr.split("\t");
            this.relaNum = reqAry[0];
//            if(this.relaNum.equals("new"))
//            {
//            	this.relaNum = this.getNextRelaNum();
//            }
            this.accTypeCode = reqAry[1];
            this.accountingType = reqAry[2];
            this.accSubTypeCode = reqAry[3];
//            证券明细' when t.fivpaycatshow= 1 then '费用明细'  else '现金账户
            this.accountSubType = reqAry[4];
            this.reqItem = reqAry[5];
            if(this.reqItem.equals("证券明细"))
            {
            	this.securityShow = "1";
                this.ivPayCatShow = "0";
                this.cashAccShow = "0";
            }else if(this.reqItem.equals("费用明细"))
            {
            	this.securityShow = "0";
                this.ivPayCatShow = "1";
                this.cashAccShow = "0";
            }else if(this.reqItem.equals("现金账户"))
            {
            	this.securityShow = "0";
                this.ivPayCatShow = "0";
                this.cashAccShow = "1";
            }
            this.subTsfTypeCode = reqAry[6];
            this.dataStyle = reqAry[7];
            this.dataStyleName = reqAry[8];
            this.subjectType = reqAry[9];
            this.subjectTypeName = reqAry[10];
//            this.subTsfTypeName = reqAry[11];
//            this.securityShow = reqAry[5];
//            this.ivPayCatShow = reqAry[6];
//            this.cashAccShow = reqAry[7];
//            this.subTsfTypeCode = reqAry[8];
//            this.subjectType = reqAry[9];
//            this.dataStyle = reqAry[10];
//            
            this.checkStateId = Integer.parseInt(reqAry[11]);
//            this.oldanalysisCode = reqAry[5];
//            this.status = reqAry[6]; //lzp add 11.30
            this.oldRelaNum = reqAry[12];
            this.status = reqAry[13]; //lzp add 11.30
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new AccountTransferRelationBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析分析代码设置请求出错", e);
        }
    } 
    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer(); 
        //FRelaNum	FAccountSubType	FReqItem	FSubTsfTypeName	FSubjectTypeName	FDataStyleName
        buf.append(this.relaNum.trim()).append("\t");
        buf.append(this.accountingType).append("\t");
        buf.append(this.accountSubType).append("\t");
        buf.append(this.reqItem).append("\t");
        buf.append(this.subTsfTypeName).append("\t");
        buf.append(this.subTsfTypeCode).append("\t");
        buf.append(this.subjectTypeName).append("\t");
        buf.append(this.dataStyleName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }
    
    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildShowRowStr() {
        StringBuffer buf = new StringBuffer(); 
        //FRelaNum	FAccountSubType	FReqItem	FSubTsfTypeName	FSubjectTypeName	FDataStyleName
        buf.append(this.relaNum.trim()).append("\t");
        buf.append(this.accountingType).append("\t");
        buf.append(this.accountSubType).append("\t");
        buf.append(this.reqItem).append("\t");
        buf.append(this.subTsfTypeName).append("\t");
        buf.append(this.subjectTypeName).append("\t");
        buf.append(this.dataStyleName).append("\t");
//        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
//        dbFun.checkInputCommon(btOper, "Tb_Base_ActStfRela", "FRelaNum",
//                               this.relaNum, this.oldRelaNum);
        ResultSet rs = null;
        String strSql = " select FRelaNum from tb_base_actstfrela where FSubjectType = " + this.subjectType
        				+ " and FAccSubTypeCode = " + dbl.sqlString(this.accSubTypeCode)
        				+ " and FSubTsfTypeCode = " + dbl.sqlString(this.subTsfTypeCode);
        try {
	        if(btOper == YssCons.OP_ADD)
	        {
				rs = dbl.openResultSet(strSql);
				if(rs.next())
				{
					throw new YssException("科目类型为【" + this.subjectTypeName + "】、核算子类型为【" + this.accountSubType + "】、" +
							" 调拨类型为【" + this.subTsfTypeCode + "】信息已经存在，请重新输入");
				}
	        }else if(btOper == YssCons.OP_EDIT)
	        {
	        	rs = dbl.openResultSet(strSql);
				if(rs.next())
				{
					String relaNum = rs.getString("FRelaNum");
					if(!this.relaNum.equals(relaNum))
					{
					throw new YssException("科目类型为【" + this.subjectTypeName + "】、核算子类型为【" + this.accountSubType + "】、" +
							" 调拨类型为【" + this.subTsfTypeCode + "】信息已经存在，请重新输入");
					}
				}
	        	
	        }
        } catch (Exception e) {
			// TODO Auto-generated catch block
        	throw new YssException(e.getMessage());
		} finally
		{
			dbl.closeResultSetFinal(rs);
		}
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
     * getListViewData1
     * 获取分析代码设置数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql = " select t.frelanum,t.faccountingtype, t.faccountsubtype, (case when t.fsecurityshow=1 then " +
                 	" '证券明细' when t.fivpaycatshow= 1 then '费用明细'  else '现金账户' end)  as freqitem," +
                 	" (case when b.fsubtsftypename is null then t.fsubtsftypecode else " +
                 	" b.fsubtsftypename end) as fsubtsftypename,t.fsubtsftypecode, t.fsubjecttypename,t.fdatastylename,t.fcheckstate " +
                 	" from tb_base_actstfrela t left join (select fsubtsftypecode, fsubtsftypename from tb_base_subtransfertype " +
                 	" where fcheckstate = 1) b on " + 
                 	" t.fsubtsftypecode = b.fsubtsftypecode " +
                 	buildFilterSql() +
                 	" order by t.frelanum asc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                
                this.accountingType = rs.getString("faccountingtype") + "";
                this.accountSubType = rs.getString("faccountsubtype") + "";
                this.dataStyleName = rs.getString("fdatastylename") + "";
                this.reqItem = rs.getString("freqitem").trim();
                this.relaNum = rs.getString("frelanum");
                this.subjectTypeName = rs.getString("fsubjecttypename");
                this.subTsfTypeCode = rs.getString("fsubtsftypecode");
                this.subTsfTypeName = rs.getString("fsubtsftypename");
                this.checkStateId = rs.getInt("FCheckState");
                bufShow.append(this.buildShowRowStr()).
                        append(YssCons.YSS_LINESPLITMARK);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufAll.toString().length() > 2) {
            	sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_BASE_AccounttingType + "," +
                                        YssCons.YSS_BASE_ZQSubAccType + "," +
                                        YssCons.YSS_BASE_XJSubAccType + "," +
                                        YssCons.YSS_BASE_ReqField + "," +
                                        YssCons.YSS_BASE_DataType + "," +
                                        YssCons.YSS_BASE_SubjectType
            							);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取核算类型调拨类型关系出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData2
     * 获取已审核的分析代码设置
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
    public String getListViewData3() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IBaseSetting
     */
    public IDataSetting getSetting() {
        return null;
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
     * buildFilterSql
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
    	String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.accTypeCode.length() != 0 && !this.filterType.accTypeCode.equals("99")) {
                sResult = sResult + " and t.FAccTypeCode = '" +
                    filterType.accTypeCode + "'";
            }
            if (this.filterType.accSubTypeCode.length() != 0 && !this.filterType.accSubTypeCode.equals("99")) {
                sResult = sResult + " and t.FAccSubTypeCode = '" +
                    filterType.accSubTypeCode + "'";
            }
            if (this.filterType.securityShow != null && this.filterType.securityShow.equals("1")) {
                sResult = sResult + " and t.FSecurityShow = 1 ";  
            }
            if (this.filterType.ivPayCatShow != null && this.filterType.ivPayCatShow.equals("1")) {
                sResult = sResult + " and t.FIvPayCatShow = 1 ";  
            }
            if (this.filterType.cashAccShow != null && this.filterType.cashAccShow.equals("1")) {
                sResult = sResult + " and t.FCashAccShow = 1 ";  
            }
            if (this.filterType.subTsfTypeCode.length()!=0)
            {
            	sResult = sResult + " and t.FSubTsfTypeCode = '" + 
            			filterType.subTsfTypeCode + "'"; 
            }
            if (this.filterType.dataStyle.length() != 0 && !this.filterType.dataStyle.equals("99")) {
                sResult = sResult + " and t.FDataStyle = '" +
                    filterType.dataStyle + "'";
            }
            
            if (this.filterType.subjectType.length() != 0 && !this.filterType.subjectType.equals("99")) {
                sResult = sResult + " and t.FSubjectType = '" +
                    filterType.subjectType + "'";
            }
        }
        return sResult;
    }
    
    /**
     * 获取新的编号
     * @return String
     */
	/**shashijie 2012-7-2 STORY 2475 */
    private String getNextRelaNum() throws YssException {
	/**end*/
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        try {
        	strSql = "select max(frelanum) frelanum from tb_base_actstfrela";
			rs = dbl.openResultSet(strSql);
			if(rs.next())
			{
				int curNum = (rs.getInt("frelanum") + 1);
				sResult = String.valueOf(curNum);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			new YssException("获取编号出错！",e);
		} finally
		{
			dbl.closeResultSetFinal(rs);
		}
        return sResult;
    }

    public String addSetting() throws YssException {
    	 String strSql = "";
         boolean bTrans = false; //代表是否开始了事务
         Connection conn = dbl.loadConnection();
         try {
        	 this.relaNum = this.getNextRelaNum();
             strSql = "insert into Tb_Base_ActStfRela" +
                 "(FRelaNum,FAccountingType,FAccTypeCode,FAccountSubType,FAccSubTypeCode,FSecurityShow,FIvPayCatShow, FCashAccShow," +
                 "FSubTsfTypeCode,FDATASTYLE,FDATASTYLENAME,FSubjectType,FSubjectTypeName," +
                 "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                 " values(" + this.relaNum + "," +
                 dbl.sqlString(this.accountingType) + "," +
                 dbl.sqlString(this.accTypeCode) + "," + //hjj 10090701 add
                 dbl.sqlString(this.accountSubType) + "," +
                 dbl.sqlString(this.accSubTypeCode) + "," +
                 dbl.sqlString(this.securityShow) + "," +
                 dbl.sqlString(this.ivPayCatShow) + "," +
                 dbl.sqlString(this.cashAccShow) + "," +
                 dbl.sqlString(this.subTsfTypeCode) + "," +
                 dbl.sqlString(this.dataStyle) + "," +
                 dbl.sqlString(this.dataStyleName) + "," +
                 dbl.sqlString(this.subjectType) + "," +
                 dbl.sqlString(this.subjectTypeName) + "," +
                 (pub.getSysCheckState() ? "0" : "1") + "," +
                 dbl.sqlString(this.creatorCode) + "," +
                 dbl.sqlString(this.creatorTime) + "," +
                 (pub.getSysCheckState() ? "' '" :
                  dbl.sqlString(this.creatorCode)) + ")";

             conn.setAutoCommit(false);
             bTrans = true;
             dbl.executeSql(strSql);
             //---------lzp add 11.30
             if (this.status.equalsIgnoreCase("1")) {
                 com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                     funsetting.SysDataBean();
                 sysdata.setYssPub(pub);
                 sysdata.setStrAssetGroupCode("Common");
                 sysdata.setStrFunName("新增-核算调拨关系设置");
                 sysdata.setStrCode(this.relaNum);
                 sysdata.setStrName(this.accountSubType);
                 sysdata.setStrUpdateSql(strSql);
                 sysdata.setStrCreator(pub.getUserName());
                 sysdata.addSetting();
             }
             //-----------------------

             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
         } catch (Exception e) {
             throw new YssException("新增核算调拨关系设置信息出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }
         return null;

    }

    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理回购品种信息设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */

    public void checkSetting() throws YssException {
    	String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环还原
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false); //开启一个事物
            bTrans = true; //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (  sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql = "update Tb_Base_ActStfRela set FCheckState = " +
                        this.checkStateId + ",FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FRelaNum = " +
                        dbl.sqlString(this.relaNum); //更新数据的SQL语句
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而tradeTypeCode不为空，则按照tradeTypeCode来执行sql语句
            else if (relaNum != null && (!this.relaNum.equalsIgnoreCase("")) ) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_ActStfRela set FCheckState = " +
                    this.checkStateId + ",FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FRelaNum = " +
                    dbl.sqlString(this.relaNum); //更新数据的SQL语句
                dbl.executeSql(strSql); //执行更新操作
            }
            if (this.status.equalsIgnoreCase("1")) { //判断status是否等于1,当传入1的时候就记录系统的信息状态
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub); //设置pub的值
                sysdata.setStrAssetGroupCode("Common"); //设置StrAssetGroupCode的值
                if (this.checkStateId == 1) { //如果checkStateId==1就是它要的状态是审核状
                    sysdata.setStrFunName("审核-核算调拨关系设置"); //设置StrFunName的值
                } else {
                    sysdata.setStrFunName("反审核-核算调拨关系设置"); //设置StrFunName的值
                }

                sysdata.setStrCode(this.relaNum); //设置StrCode的值
                sysdata.setStrName(this.accountSubType); //设置StrName的值
                sysdata.setStrUpdateSql(strSql); //设置StrUpdateSql的值
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting(); //把这些以上数据添加到系统数据表Tb_Fun_SysData
            }
//-----------------------

            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核核算调拨关系设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
    	String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update Tb_Base_ActStfRela set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FRelaNum = " +
                dbl.sqlString(this.relaNum);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-核算调拨关系设置");
                sysdata.setStrCode(this.relaNum);
                sysdata.setStrName(this.accountSubType);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除核算调拨关系设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
        	 conn.setAutoCommit(false);
             bTrans = true;
             strSql = " delete from Tb_Base_ActStfRela t where t.FRelaNum = " + this.relaNum;
             dbl.executeSql(strSql);
        	 strSql = "insert into Tb_Base_ActStfRela" +
                 "(FRelaNum,FAccountingType,FAccTypeCode,FAccountSubType,FAccSubTypeCode,FSecurityShow,FIvPayCatShow, FCashAccShow," +
                 "FSubTsfTypeCode,FDATASTYLE,FDATASTYLENAME,FSubjectType,FSubjectTypeName," +
                 "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                 " values(" + this.relaNum + "," +
                 dbl.sqlString(this.accountingType) + "," +
                 dbl.sqlString(this.accTypeCode) + "," + //hjj 10090701 add
                 dbl.sqlString(this.accountSubType) + "," +
                 dbl.sqlString(this.accSubTypeCode) + "," +
                 dbl.sqlString(this.securityShow) + "," +
                 dbl.sqlString(this.ivPayCatShow) + "," +
                 dbl.sqlString(this.cashAccShow) + "," +
                 dbl.sqlString(this.subTsfTypeCode) + "," +
                 dbl.sqlString(this.dataStyle) + "," +
                 dbl.sqlString(this.dataStyleName) + "," +
                 dbl.sqlString(this.subjectType) + "," +
                 dbl.sqlString(this.subjectTypeName) + "," +
                 (pub.getSysCheckState() ? "0" : "1") + "," +
                 dbl.sqlString(this.creatorCode) + "," +
                 dbl.sqlString(this.creatorTime) + "," +
                 (pub.getSysCheckState() ? "' '" :
                  dbl.sqlString(this.creatorCode)) + ")";
        	 dbl.executeSql(strSql);
             
             //---------lzp add 11.30
             if (this.status.equalsIgnoreCase("1")) {
                 com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                     funsetting.SysDataBean();
                 sysdata.setYssPub(pub);
                 sysdata.setStrAssetGroupCode("Common");
                 sysdata.setStrFunName("修改-核算调拨关系设置");
                 sysdata.setStrCode(this.relaNum);
                 sysdata.setStrName(this.accountSubType);
                 sysdata.setStrUpdateSql(strSql);
                 sysdata.setStrCreator(pub.getUserName());
                 sysdata.addSetting();
             }
             //-----------------------

             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
         } catch (Exception e) {
             throw new YssException("修改核算调拨关系设置信息出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }
             return null;

    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }


    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 回收站的删除功能调用此方法deleteRecycleData()
     * 从数据库删除数据，即彻底删除数据
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
    	String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
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
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Base_ActStfRela") +
                        " where FRelaNum = " +
                        dbl.sqlString(this.relaNum);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而tradeTypeCode不为空，则按照tradeTypeCode来执行sql语句
            else if (relaNum != "" && relaNum != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Base_ActStfRela") +
                    " where FRelaNum = " +
                    dbl.sqlString(this.relaNum);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true); //提交事物
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
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

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
}
