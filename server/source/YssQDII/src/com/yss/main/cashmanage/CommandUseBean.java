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
		 * CommandUseBean
		 * Description:划款用途
		 * @author zhouwei
		 * DateTime:20120104 
		 * story 1911 QDV4招商基金2011年11月22日01_A
		 * */
public class CommandUseBean extends BaseDataSettingBean implements
		IDataSetting {
	private String oldNum="";
	private String num="";//序号
	private String cashUse="";//用途
	private String userCode="";
	private String createTime="";
	private CommandUseBean filterType;
	public CommandUseBean getFilterType() {
		return filterType;
	}

	public void setFilterType(CommandUseBean filterType) {
		this.filterType = filterType;
	}

	private String sRecycled = ""; //回收站
	/*
	 * 新增划款用途
	 * */
	public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        ResultSet rs=null;
        try {        	
        	//获取序号
        	if(this.num==null || this.num.equals("0")){
	        	int maxNum=0;
	        	strSql="select max(to_number(fnum))  as fnum from "+ pub.yssGetTableName("TB_COMMAND_USE");
	        	rs=dbl.openResultSet(strSql);
	        	if(rs.next()){
	        		maxNum=Integer.parseInt(rs.getString("fnum") == null ? "0": rs.getString("fnum"));	//modify huangqirong 2012-03-20 bug #3789
	        	}else{
	        		maxNum=0;
	        	}
	        	this.num=Integer.toString(maxNum+1);
        	}
            conn.setAutoCommit(false);
            bTrans = true;
        	strSql =
                "insert into " + pub.yssGetTableName("TB_COMMAND_USE") +
                "(FNUM, FCOMMANDUSE, FCREATOR, FCREATETIME,fcheckstate)" +
                " values(" + dbl.sqlString(this.num) + "," +
                dbl.sqlString(this.cashUse) + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime)+","+
                this.checkStateId
                + ")";
            dbl.executeSql(strSql);   
        	conn.commit();
        	 bTrans = false;
             conn.setAutoCommit(true); 
        } catch (Exception e) {
            throw new YssException("保存划款用途信息出错\r\n" + e.getMessage(), e);
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
		  dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_COMMAND_USE"), "FNUM",
                  this.num, this.oldNum);

	}
	public void checkSetting() throws YssException {}
	/*
	 * 删除划款用途
	 * */
	public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	bTrans = true;
            conn.setAutoCommit(false);
            strSql = "delete from  " + pub.yssGetTableName("TB_COMMAND_USE") +              
                " where FNUM = " +
                dbl.sqlString(this.num);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除划款用途信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }
	
	public void deleteRecycleData() throws YssException {}
	/*
	 * 修改划款用途
	 * */
	public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	//验证匹配条件是否重复	
            conn.setAutoCommit(false);
            bTrans=true;
            strSql = "update " + pub.yssGetTableName("TB_COMMAND_USE") +
                " set FNUM = " +
                dbl.sqlString(this.num) + ", FCOMMANDUSE = " +
                dbl.sqlString(this.cashUse) +
                ",FCREATOR="+
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime)+
                " where fnum="+dbl.sqlString(this.oldNum);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改划款用途信息出错\r\n" + e.getMessage(), e);
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

        buf.append(this.num).append("\t");
        buf.append(this.cashUse).append("\t");
        buf.append(this.userCode).append("\t");
        buf.append(this.createTime).append("\t");
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
	            if (sRowStr.indexOf("\r\t") >= 0) {
	                sTmpStr = sRowStr.split("\r\t")[0];

	            } else {
	                sTmpStr = sRowStr;

	            }
	            reqAry = sTmpStr.split("\t", -1);
	            this.num = reqAry[0];
	            this.oldNum = reqAry[1];
	            this.cashUse = reqAry[2];	
	            this.checkStateId=0;
	            super.parseRecLog();
	            if (sRowStr.indexOf("\r\t") >= 0) {
	                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
	                    if (this.filterType == null) {
	                        this.filterType = new CommandUseBean();
	                        this.filterType.setYssPub(pub);
	                    }
	                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
	                }
	            }
	        } catch (Exception e) {
	            throw new YssException("解析划款用途信息出错\r\n" + e.getMessage(), e);
	        }

	}
	/*
	 * 获取划款用途
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
        	strSql="select a.*,u1.fusername from "+pub.yssGetTableName("tb_command_use")+" a"
        	       +" left join (select * from tb_sys_userlist) u1 on u1.fusercode = a.fcreator"       	       
        	       +" order by to_number(a.fnum)";
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
			throw new YssException("获取划款用途信息出错!"+e.getMessage(), e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/*
	 * 赋值
	 * */
	public void setAttr(ResultSet rs) throws SQLException {
        this.num = rs.getString("Fnum") + "";
        this.cashUse = rs.getString("FCOMMANDUSE") + "";
        this.userCode = rs.getString("FCREATOR") + "";
        this.createTime = rs.getString("FCREATETIME")+"";       
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
