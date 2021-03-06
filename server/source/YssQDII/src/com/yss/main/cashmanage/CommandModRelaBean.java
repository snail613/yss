package com.yss.main.cashmanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
		/**
		 * CommandModRelaBean
		 * Description:划款指令模板关联
		 * @author zhouwei
		 * DateTime:20111214 
		 * story 2004 QDV4赢时胜(上海开发部)2011年12月8日01_A 
		 * */
public class CommandModRelaBean extends BaseDataSettingBean implements
		IDataSetting {
	private String oldRelaCode;
	private String oldPortCode;
	private String oldPayerCode;
	private String oldReceiverCode;
	private String oldTransferType;
	private String oldFexState;
	private String relaCode="";//关联编号
	private String relaName="";//关联名称
	private String repCode="";//模板编号
	private String repName="";
	private String fportCode="";//组合编号
	private String fportName="";
	private String payerCode="";//付款人
	private String payerName="";
	private String receiverCode="";//收款人
	private String receiverName="";
	private String transferType="";//划款类型
	private String transferName="";
	private String fexState="0";//外汇交收
	private String fexStateName="";//外汇交收描述
	private String desc="";//描述
	private CommandModRelaBean filterType;
	public CommandModRelaBean getFilterType() {
		return filterType;
	}

	public void setFilterType(CommandModRelaBean filterType) {
		this.filterType = filterType;
	}

	private String sRecycled = ""; //回收站
	/*
	 * 新增划款指令链接
	 * */
	public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	//modify huangqirong 2012-11-23 bug #6344 增加 FMODULERELACODE为主键 
        	//验证匹配条件是否重复
        	dbFun.checkInputCommon(YssCons.OP_ADD, pub.yssGetTableName("TB_CASH_MODULE_RELA"), "FPORTCODE,FPAYERCODE,FRECEIVERCODE," +
        			"FTRANSFERTYPECODE,FFOREXCHANGESTATE,FMODULERELACODE",
                    this.fportCode+","+this.payerCode+","+this.receiverCode+","+this.transferType+","+this.fexState + "," + this.relaCode
                    , this.oldPortCode+","+this.oldPayerCode+","+this.oldReceiverCode+","+this.oldTransferType+","+this.oldFexState + "," + this.oldRelaCode);
        	//--end---
            conn.setAutoCommit(false);
            bTrans = true;
        	strSql =
                "insert into " + pub.yssGetTableName("TB_CASH_MODULE_RELA") +
                "(FMODULERELACODE, FMODULERELANAME, FREPFORMATCODE, FPAYERCODE, FPORTCODE, FRECEIVERCODE, FTRANSFERTYPECODE, FFOREXCHANGESTATE, " +
                " FDESC," +
                " FCheckState, FCreator, FCreateTime, FCheckUser,FCHECKTIME)" +
                " values(" + dbl.sqlString(this.relaCode) + "," +
                dbl.sqlString(this.relaName) + "," +
                dbl.sqlString(this.repCode) + "," +
                dbl.sqlString(this.payerCode) + "," +
                dbl.sqlString(this.fportCode) + "," +
                dbl.sqlString(this.receiverCode) + "," +
                dbl.sqlString(this.transferType) + "," +
                dbl.sqlString(this.fexState) + "," +
                dbl.sqlString(this.desc) + "," +
                0+","+
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                dbl.sqlString(this.checkUserCode) + "," +
                dbl.sqlString(this.checkTime)
                + ")";
            dbl.executeSql(strSql);   
        	conn.commit();
        	 bTrans = false;
             conn.setAutoCommit(true); 
        } catch (Exception e) {
            throw new YssException("保存划款指令模板关联信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		return "";

    }
	/**
     * checkInput
     * 检查输入数据是否合法
     * @param btOper byte
     */
	public void checkInput(byte btOper) throws YssException {
		  dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_CASH_MODULE_RELA"), "FMODULERELACODE",
                  this.relaCode, this.oldRelaCode);

	}
	/*
	 * 审核划款指令链接
	 * */
	public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        Statement st = null;
        String array[] = null;
        try {
            if (null != sRecycled && !sRecycled.trim().equalsIgnoreCase("")) { ////如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                array = sRecycled.split("\r\n"); //根据规定的符号，把多个sql语句分别放入数组
                st = conn.createStatement();
                conn.setAutoCommit(false);
                bTrans = true;
                for (int i = 0; i < array.length; i++) { //循环执行这些update语句
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);               
                    strSql = "update " + pub.yssGetTableName("TB_CASH_MODULE_RELA") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FMODULERELACODE = " +
                        dbl.sqlString(this.relaCode);
                    st.addBatch(strSql);
                }
                st.executeBatch();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核划款指令模板关联信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
        }
    }
	/*
	 * 删除划款指令链接
	 * */
	public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	bTrans = true;
            conn.setAutoCommit(false);
            strSql = "update " + pub.yssGetTableName("TB_CASH_MODULE_RELA") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FMODULERELACODE = " +
                dbl.sqlString(this.relaCode);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除划款指令模板关联信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }
	/*
	 * 清楚划款指令链接
	 * */
	public void deleteRecycleData() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        String[] arrData = null;
        Connection conn = dbl.loadConnection(); //获取一个连接
        try {
            if (sRecycled != "" && sRecycled != null) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                arrData = sRecycled.split("\r\n"); //根据规定的符号，把多个sql语句分别放入数组
                conn.setAutoCommit(false);
                bTrans = true;
                for (int i = 0; i < arrData.length; i++) { //循环执行这些删除语句
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("TB_CASH_MODULE_RELA") +
                        " where FMODULERELACODE = " + dbl.sqlString(this.relaCode);
                    dbl.executeSql(strSql); //执行sql语句
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
	/*
	 * 修改划款指令链接
	 * */
	public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	//验证匹配条件是否重复
        	dbFun.checkInputCommon(YssCons.OP_ADD, pub.yssGetTableName("TB_CASH_MODULE_RELA"), "FPORTCODE,FPAYERCODE,FRECEIVERCODE," +
        			"FTRANSFERTYPECODE,FFOREXCHANGESTATE",
                    this.fportCode+","+this.payerCode+","+this.receiverCode+","+this.transferType+","+this.fexState
                    , this.oldPortCode+","+this.oldPayerCode+","+this.oldReceiverCode+","+this.oldTransferType+","+this.oldFexState);
            conn.setAutoCommit(false);
            bTrans=true;
            strSql = "update " + pub.yssGetTableName("TB_CASH_MODULE_RELA") +
                " set FMODULERELACODE = " +
                dbl.sqlString(this.relaCode) + ", FMODULERELANAME = " +
                dbl.sqlString(this.relaName) + ", FREPFORMATCODE = " +
                dbl.sqlString(this.repCode) + ", FPAYERCODE = " +
                dbl.sqlString(this.payerCode) + ", FPORTCODE = " +
                dbl.sqlString(this.fportCode) + ", FRECEIVERCODE = " +
                dbl.sqlString(this.receiverCode) + ", FTRANSFERTYPECODE = " +
                dbl.sqlString(this.transferType) + ", FDESC = " +
                dbl.sqlString(this.desc) + ", FFOREXCHANGESTATE = " +
                dbl.sqlString(this.fexState)  + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                dbl.sqlString(this.creatorCode)+		
                " where FMODULERELACODE = " +
                dbl.sqlString(this.oldRelaCode);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改划款指令模板信息出错\r\n" + e.getMessage(), e);
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
	/*
	 * 构建字符串
	 * */
	public String buildRowStr() throws YssException {

        StringBuffer buf = new StringBuffer();

        buf.append(this.relaCode).append("\t");
        buf.append(this.relaName).append("\t");
        buf.append(this.repCode).append("\t");
        buf.append(this.repName).append("\t");
        buf.append(this.fportCode).append("\t");
        buf.append(this.fportName).append("\t");
        buf.append(this.payerCode).append("\t");
        buf.append(this.payerName).append("\t");
        buf.append(this.receiverCode).append("\t");
        buf.append(this.receiverName).append("\t");
        buf.append(this.transferType).append("\t");
        buf.append(this.transferName).append("\t");
        buf.append(this.fexState).append("\t");
        buf.append(this.fexStateName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();    
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * 解析字符串
	 * */
	public void parseRowStr(String sRowStr) throws YssException {
			String reqAry[] = null;
	        String sTmpStr = "";
	        sRecycled = sRowStr;
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
	            if (sRowStr.indexOf("\r\t") >= 0) {
	                sTmpStr = sRowStr.split("\r\t")[0];

	            } else {
	                sTmpStr = sRowStr;

	            }
	            reqAry = sTmpStr.split("\t", -1);
	            this.relaCode = reqAry[0];
	            this.relaName = reqAry[1];
	            this.repCode = reqAry[2];
	            this.fportCode =reqAry[3];
	            this.payerCode =reqAry[4];
	            this.receiverCode = reqAry[5];
	            this.transferType = reqAry[6];
	            this.fexState = reqAry[7];
	            this.oldRelaCode = reqAry[8];	            
	            this.oldPortCode = reqAry[9];
	            this.oldPayerCode = reqAry[10];
	            this.oldReceiverCode = reqAry[11];
	            this.oldTransferType = reqAry[12];
	            this.oldFexState = reqAry[13];            
	            this.checkStateId=Integer.parseInt(reqAry[14]);
	            this.desc = reqAry[15];	            
	            super.parseRecLog();
	            if (sRowStr.indexOf("\r\t") >= 0) {
	                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
	                    if (this.filterType == null) {
	                        this.filterType = new CommandModRelaBean();
	                        this.filterType.setYssPub(pub);
	                    }
	                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
	                }
	            }
	        } catch (Exception e) {
	            throw new YssException("解析划款指令模板关联信息出错\r\n" + e.getMessage(), e);
	        }

	}
	/*
	 * 获取划款指令链接
	 * */
	public String getListViewData1() throws YssException {
	    String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "", sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try{
        	sHeader = this.getListView1Headers();
        	strSql="select a.*,b.FCommmondNAME,c.freceivername as fpayername,d.freceivername as freceivername,"+  //modify by zhangjun 2012-05-22 BUG#4451
        	       "f.fportname, e.fhkcode,u1.fusername as FCreatorName,u2.fusername as FCheckUserName,"
        	       +"case when a.FFOREXCHANGESTATE='0' then '否' when  a.FFOREXCHANGESTATE='1' then '是' else ' ' end as fexstatename from "+pub.yssGetTableName("TB_CASH_MODULE_RELA")
        	       +" a left join (select * from "+pub.yssGetTableName("TB_data_commondModel")+" where fcheckstate = 1) b "
        	       +"on b.FCommmondCODE  =a.frepformatcode"  //modify by zhangjun 2012-05-22 BUG#4451
        	       +" left join (select * from "+pub.yssGetTableName("tb_para_receiver")+" where fcheckstate = 1) c "
        	       +"on c.freceivercode =a.fpayercode"
        	       +" left join (select * from "+pub.yssGetTableName("tb_para_receiver")+" where fcheckstate = 1) d "
        	       +"on d.freceivercode = a.freceivercode"
        	       +" left join (select * from TDzTypeCodePP where fcheckstate = 1) e on e.fhktype = a.ftransfertypecode"
        	       +"  left join (select * from "+pub.yssGetTableName("tb_para_portfolio")+" where fcheckstate = 1) f "
        	       +"on a.fportcode = f.fportcode"
        	       +" left join (select * from tb_sys_userlist) u1 on u1.fusercode = a.fcreator"
        	       +" left join (select * from tb_sys_userlist) u2 on u2.fusercode =a.fcheckuser"
        	       +buildFilterSql()
        	       +" order by a.FMODULERELACODE, a.FCheckState, a.FCreateTime desc";
        	rs=dbl.openResultSet(strSql);
        	while(rs.next()){
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setAttr(rs);
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
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        }catch (Exception e) {
			throw new YssException("获取划款指令模板配置信息出错!"+e.getMessage(), e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/*
	 * 赋值
	 * */
	public void setAttr(ResultSet rs) throws SQLException {
        this.relaCode = rs.getString("FMODULERELACODE") + "";
        this.relaName = rs.getString("FMODULERELANAME") + "";
        this.repCode = rs.getString("FREPFORMATCODE") + "";
        this.repName = rs.getString("FCommmondNAME")+"";//modify by zhangjun 2012-05-22 BUG#4451
        this.payerCode = rs.getString("FPAYERCODE")+"";
        this.payerName = rs.getString("fpayername")+"";
        this.receiverCode = rs.getString("FRECEIVERCODE")+"";
        this.receiverName = rs.getString("freceivername") + "";
        this.fportCode = rs.getString("FPORTCODE") + "";
        this.fportName = rs.getString("fportname") + "";
        this.transferType = rs.getString("FTRANSFERTYPECODE") + "";
        this.transferName = rs.getString("fhkcode") + "";
        this.fexState = rs.getString("FFOREXCHANGESTATE") + "";
        this.desc = rs.getString("FDesc") + "";
        this.fexStateName=rs.getString("fexstatename")+"";
        super.setRecLog(rs);
    }
	
	/**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.relaCode.length() != 0) {
                sResult = sResult + " and a.FMODULERELACODE like '%" +
                    filterType.relaCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.relaName.length() != 0) {
                sResult = sResult + " and a.FMODULERELANAME like '%" +
                    filterType.relaName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.repCode.length() != 0) {
                sResult = sResult + " and a.FREPFORMATCODE like '%" +
                    filterType.repCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.fportCode.length() != 0) {
                sResult = sResult + " and a.FPORTCODE like '%" +
                    filterType.fportCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.payerCode.length() != 0) {
                sResult = sResult + " and a.FPAYERCODE like '%" +
                    filterType.payerCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.receiverCode.length() != 0) {
                sResult = sResult + " and a.FRECEIVERCODE like '%" +
                    filterType.receiverCode.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.fexState.equals("-1")) {
                sResult = sResult + " and a.FFOREXCHANGESTATE like '%" +
                    filterType.transferType.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
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
